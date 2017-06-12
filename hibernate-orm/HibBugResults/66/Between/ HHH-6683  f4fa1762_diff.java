diff --git a/documentation/src/main/docbook/devguide/en-US/Services.xml b/documentation/src/main/docbook/devguide/en-US/Services.xml
index 6354f08288..f489f93dfd 100644
--- a/documentation/src/main/docbook/devguide/en-US/Services.xml
+++ b/documentation/src/main/docbook/devguide/en-US/Services.xml
@@ -1,948 +1,953 @@
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
             of being started and stopped.  Another optional service contract is
             <interfacename>org.hibernate.service.spi.Manageable</interfacename> which marks the service as manageable
             in JMX provided the JMX integration is enabled.
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
 
     <section id="services-registry">
         <title>ServiceRegistry</title>
         <para>
             The central service API, aside from the services themselves, is the
             <interfacename>org.hibernate.service.ServiceRegistry</interfacename> interface. The main purpose of
             a service registry is to hold, manage and provide access to services.
         </para>
         <para>
             Service registries are hierarchical.  Services in one registry can depend on and utilize services in that
             same registry as well as any parent registries.
         </para>
         <para>
             Use <classname>org.hibernate.service.ServiceRegistryBuilder</classname> to build a
             <interfacename>org.hibernate.service.ServiceRegistry</interfacename> instance.
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
 
         <section id="services-ConfigurationService">
             <title><interfacename>org.hibernate.service.config.spi.ConfigurationService</interfacename></title>
             <variablelist>
                 <varlistentry>
                     <term>Notes</term>
                     <listitem>
                         <para>
                             Provides access to the configuration settings, combining those explicitly provided as well
                             as those contributed by any registered
                             <interfacename>org.hibernate.integrator.spi.Integrator</interfacename> implementations
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Initiator</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.config.internal.ConfigurationServiceInitiator</classname>
                         </para>
                     </listitem>
                 </varlistentry>
                 <varlistentry>
                     <term>Implementations</term>
                     <listitem>
                         <para>
                             <classname>org.hibernate.service.config.internal.ConfigurationServiceImpl</classname>
                         </para>
                     </listitem>
                 </varlistentry>
             </variablelist>
         </section>
 
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
                             <xref linkend="services-registry-sessionfactory"/> for more details.
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
         <title>Special service registries</title>
 
         <section id="services-registry-bootstrap">
             <title>Boot-strap registry</title>
             <para>
                 The boot-strap registry holds services that absolutely have to be available for most things to work.
                 The main service here is the <xref linkend="services-ClassLoaderService"/> which is a perfect example.
                 Even resolving configuration files needs access to class loading services (resource look ups).  This
                 is the root registry (no parent) in normal use.
             </para>
 
+            <para>
+                Instances of boot-strap registries are built using the
+                <classname>org.hibernate.service.BootstrapServiceRegistryBuilder</classname> builder.
+            </para>
+
             <section id="services-registry-bootstrap-services">
                 <title>Boot-strap registry services</title>
                 <section id="services-ClassLoaderService">
                     <title><interfacename>org.hibernate.service.classloading.spi.ClassLoaderService</interfacename></title>
                     <para>
                         Coming soon...  See <ulink url="https://hibernate.onjira.com/browse/HHH-6656" />
                     </para>
                 </section>
 
                 <section id="services-IntegratorService">
                     <title><interfacename>org.hibernate.integrator.spi.IntegratorService</interfacename></title>
                     <para>
                         Coming soon...  See <ulink url="https://hibernate.onjira.com/browse/HHH-6657" />
                     </para>
                 </section>
             </section>
         </section>
 
         <section id="services-registry-sessionfactory">
             <title>SessionFactory registry</title>
             <para>
                 While it is best practice to treat instances of all the registry types as targeting a given
                 <interfacename>org.hibernate.SessionFactory</interfacename>, the instances of services in this group
                 explicitly belong to a single <interfacename>org.hibernate.SessionFactory</interfacename>.  The
                 difference is a matter of timing in when they need to be initiated.  Generally they need access to the
                 <interfacename>org.hibernate.SessionFactory</interfacename> to be initiated.  This special registry is
                 <interfacename>org.hibernate.service.spi.SessionFactoryServiceRegistry</interfacename>
             </para>
 
             <section id="services-EventListenerRegistry">
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
 
     </section>
 
 
     <section id="services-use">
         <title>Using services and registries</title>
         <para>
             Coming soon...
         </para>
     </section>
 
 </chapter>
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 08240d6218..a825f64536 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,2757 +1,2757 @@
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
 
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.io.StringReader;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.MetadataProvider;
 import org.hibernate.annotations.common.reflection.MetadataProviderInjector;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.internal.util.xml.MappingReader;
 import org.hibernate.internal.util.xml.Origin;
 import org.hibernate.internal.util.xml.OriginImpl;
 import org.hibernate.internal.util.xml.XMLHelper;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.internal.util.xml.XmlDocumentImpl;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DenormalizedTable;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.internal.JACCConfiguration;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
 import org.hibernate.tool.hbm2ddl.IndexMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 /**
  * An instance of <tt>Configuration</tt> allows the application
  * to specify properties and mapping documents to be used when
  * creating a <tt>SessionFactory</tt>. Usually an application will create
  * a single <tt>Configuration</tt>, build a single instance of
  * <tt>SessionFactory</tt> and then instantiate <tt>Session</tt>s in
  * threads servicing client requests. The <tt>Configuration</tt> is meant
  * only as an initialization-time object. <tt>SessionFactory</tt>s are
  * immutable and do not retain any association back to the
  * <tt>Configuration</tt>.<br>
  * <br>
  * A new <tt>Configuration</tt> will use the properties specified in
  * <tt>hibernate.properties</tt> by default.
  * <p/>
  * NOTE : This will be replaced by use of {@link ServiceRegistryBuilder} and
  * {@link org.hibernate.metamodel.MetadataSources} instead after the 4.0 release at which point this class will become
  * deprecated and scheduled for removal in 5.0.  See
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6183">HHH-6183</a>,
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-2578">HHH-2578</a> and
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6586">HHH-6586</a> for details
  *
  * @author Gavin King
  * @see org.hibernate.SessionFactory
  */
 public class Configuration implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Configuration.class.getName());
 
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
 
 	public static final String ARTEFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";
 
 	/**
 	 * Class name of the class needed to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_CLASS = "org.hibernate.search.event.EventListenerRegister";
 
 	/**
 	 * Method to call to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_METHOD = "enableHibernateSearch";
 
 	protected MetadataSourceQueue metadataSourceQueue;
 	private transient ReflectionManager reflectionManager;
 
 	protected Map<String, PersistentClass> classes;
 	protected Map<String, String> imports;
 	protected Map<String, Collection> collections;
 	protected Map<String, Table> tables;
 	protected List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects;
 
 	protected Map<String, NamedQueryDefinition> namedQueries;
 	protected Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	protected Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 
 	protected Map<String, TypeDef> typeDefs;
 	protected Map<String, FilterDefinition> filterDefinitions;
 	protected Map<String, FetchProfile> fetchProfiles;
 
 	protected Map tableNameBinding;
 	protected Map columnNameBindingPerTable;
 
 	protected List<SecondPass> secondPasses;
 	protected List<Mappings.PropertyReference> propertyReferences;
 	protected Map<ExtendsQueueEntry, ?> extendsQueue;
 
 	protected Map<String, SQLFunction> sqlFunctions;
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private EntityTuplizerFactory entityTuplizerFactory;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 
 	private Interceptor interceptor;
 	private Properties properties;
 	private EntityResolver entityResolver;
 	private EntityNotFoundDelegate entityNotFoundDelegate;
 
 	protected transient XMLHelper xmlHelper;
 	protected NamingStrategy namingStrategy;
 	private SessionFactoryObserver sessionFactoryObserver;
 
 	protected final SettingsFactory settingsFactory;
 
 	private transient Mapping mapping = buildMapping();
 
 	private MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private Map<Class<?>, org.hibernate.mapping.MappedSuperclass> mappedSuperClasses;
 
 	private Map<String, IdGenerator> namedGenerators;
 	private Map<String, Map<String, Join>> joins;
 	private Map<String, AnnotatedClassType> classTypes;
 	private Set<String> defaultNamedQueryNames;
 	private Set<String> defaultNamedNativeQueryNames;
 	private Set<String> defaultSqlResultSetMappingNames;
 	private Set<String> defaultNamedGenerators;
 	private Map<String, Properties> generatorTables;
 	private Map<Table, List<UniqueConstraintHolder>> uniqueConstraintHoldersByTable;
 	private Map<String, String> mappedByResolver;
 	private Map<String, String> propertyRefResolver;
 	private Map<String, AnyMetaDef> anyMetaDefs;
 	private List<CacheHolder> caches;
 	private boolean inSecondPass = false;
 	private boolean isDefaultProcessed = false;
 	private boolean isValidatorNotPresentLogged;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
 	private boolean specjProprietarySyntaxEnabled;
 
 
 	protected Configuration(SettingsFactory settingsFactory) {
 		this.settingsFactory = settingsFactory;
 		reset();
 	}
 
 	public Configuration() {
 		this( new SettingsFactory() );
 	}
 
 	protected void reset() {
 		metadataSourceQueue = new MetadataSourceQueue();
 		createReflectionManager();
 
 		classes = new HashMap<String,PersistentClass>();
 		imports = new HashMap<String,String>();
 		collections = new HashMap<String,Collection>();
 		tables = new TreeMap<String,Table>();
 
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		namedSqlQueries = new HashMap<String,NamedSQLQueryDefinition>();
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 
 		typeDefs = new HashMap<String,TypeDef>();
 		filterDefinitions = new HashMap<String, FilterDefinition>();
 		fetchProfiles = new HashMap<String, FetchProfile>();
 		auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 		tableNameBinding = new HashMap();
 		columnNameBindingPerTable = new HashMap();
 
 		secondPasses = new ArrayList<SecondPass>();
 		propertyReferences = new ArrayList<Mappings.PropertyReference>();
 		extendsQueue = new HashMap<ExtendsQueueEntry, String>();
 
 		xmlHelper = new XMLHelper();
 		interceptor = EmptyInterceptor.INSTANCE;
 		properties = Environment.getProperties();
 		entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
 
 		sqlFunctions = new HashMap<String, SQLFunction>();
 
 		entityTuplizerFactory = new EntityTuplizerFactory();
 //		componentTuplizerFactory = new ComponentTuplizerFactory();
 
 		identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 		mappedSuperClasses = new HashMap<Class<?>, MappedSuperclass>();
 
 		metadataSourcePrecedence = Collections.emptyList();
 
 		namedGenerators = new HashMap<String, IdGenerator>();
 		joins = new HashMap<String, Map<String, Join>>();
 		classTypes = new HashMap<String, AnnotatedClassType>();
 		generatorTables = new HashMap<String, Properties>();
 		defaultNamedQueryNames = new HashSet<String>();
 		defaultNamedNativeQueryNames = new HashSet<String>();
 		defaultSqlResultSetMappingNames = new HashSet<String>();
 		defaultNamedGenerators = new HashSet<String>();
 		uniqueConstraintHoldersByTable = new HashMap<Table, List<UniqueConstraintHolder>>();
 		mappedByResolver = new HashMap<String, String>();
 		propertyRefResolver = new HashMap<String, String>();
 		caches = new ArrayList<CacheHolder>();
 		namingStrategy = EJB3NamingStrategy.INSTANCE;
 		setEntityResolver( new EJB3DTDEntityResolver() );
 		anyMetaDefs = new HashMap<String, AnyMetaDef>();
 		propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		propertiesAnnotatedWithIdAndToOne = new HashMap<XClass, Map<String, PropertyData>>();
 		specjProprietarySyntaxEnabled = System.getProperty( "hibernate.enable_specj_proprietary_syntax" ) != null;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 	public ReflectionManager getReflectionManager() {
 		return reflectionManager;
 	}
 
 //	public ComponentTuplizerFactory getComponentTuplizerFactory() {
 //		return componentTuplizerFactory;
 //	}
 
 	/**
 	 * Iterate the entity mappings
 	 *
 	 * @return Iterator of the entity mappings currently contained in the configuration.
 	 */
 	public Iterator<PersistentClass> getClassMappings() {
 		return classes.values().iterator();
 	}
 
 	/**
 	 * Iterate the collection mappings
 	 *
 	 * @return Iterator of the collection mappings currently contained in the configuration.
 	 */
 	public Iterator getCollectionMappings() {
 		return collections.values().iterator();
 	}
 
 	/**
 	 * Iterate the table mappings
 	 *
 	 * @return Iterator of the table mappings currently contained in the configuration.
 	 */
 	public Iterator<Table> getTableMappings() {
 		return tables.values().iterator();
 	}
 
 	/**
 	 * Iterate the mapped super class mappings
 	 * EXPERIMENTAL Consider this API as PRIVATE
 	 *
 	 * @return iterator over the MappedSuperclass mapping currently contained in the configuration.
 	 */
 	public Iterator<MappedSuperclass> getMappedSuperclassMappings() {
 		return mappedSuperClasses.values().iterator();
 	}
 
 	/**
 	 * Get the mapping for a particular entity
 	 *
 	 * @param entityName An entity name.
 	 * @return the entity mapping information
 	 */
 	public PersistentClass getClassMapping(String entityName) {
 		return classes.get( entityName );
 	}
 
 	/**
 	 * Get the mapping for a particular collection role
 	 *
 	 * @param role a collection role
 	 * @return The collection mapping information
 	 */
 	public Collection getCollectionMapping(String role) {
 		return collections.get( role );
 	}
 
 	/**
 	 * Set a custom entity resolver. This entity resolver must be
 	 * set before addXXX(misc) call.
 	 * Default value is {@link org.hibernate.internal.util.xml.DTDEntityResolver}
 	 *
 	 * @param entityResolver entity resolver to use
 	 */
 	public void setEntityResolver(EntityResolver entityResolver) {
 		this.entityResolver = entityResolver;
 	}
 
 	public EntityResolver getEntityResolver() {
 		return entityResolver;
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
 		return addFile( new File( xmlFile ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates inability to locate the specified mapping file.  Historically this could
 	 * have indicated a problem parsing the XML document, but that is now delayed until after {@link #buildMappings}
 	 */
 	public Configuration addFile(final File xmlFile) throws MappingException {
         LOG.readingMappingsFromFile(xmlFile.getPath());
 		final String name =  xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 		add( inputSource, "file", name );
 		return this;
 	}
 
 	private XmlDocument add(InputSource inputSource, String originType, String originName) {
 		return add( inputSource, new OriginImpl( originType, originName ) );
 	}
 
 	private XmlDocument add(InputSource inputSource, Origin origin) {
 		XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument( entityResolver, inputSource, origin );
 		add( metadataXml );
 		return metadataXml;
 	}
 
 	public void add(XmlDocument metadataXml) {
 		if ( inSecondPass || !isOrmXml( metadataXml ) ) {
 			metadataSourceQueue.add( metadataXml );
 		}
 		else {
 			final MetadataProvider metadataProvider = ( (MetadataProviderInjector) reflectionManager ).getMetadataProvider();
 			JPAMetadataProvider jpaMetadataProvider = ( JPAMetadataProvider ) metadataProvider;
 			List<String> classNames = jpaMetadataProvider.getXMLContext().addDocument( metadataXml.getDocumentTree() );
 			for ( String className : classNames ) {
 				try {
 					metadataSourceQueue.add( reflectionManager.classForName( className, this.getClass() ) );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to load class defined in XML: " + className, e );
 				}
 			}
 		}
 	}
 
 	private static boolean isOrmXml(XmlDocument xmlDocument) {
 		return "entity-mappings".equals( xmlDocument.getDocumentTree().getRootElement().getName() );
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
 		File cachedFile = determineCachedDomFile( xmlFile );
 
 		try {
 			return addCacheableFileStrictly( xmlFile );
 		}
 		catch ( SerializationException e ) {
             LOG.unableToDeserializeCache(cachedFile.getPath(), e);
 		}
 		catch ( FileNotFoundException e ) {
             LOG.cachedFileNotFound( cachedFile.getPath(), e );
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
         LOG.readingMappingsFromFile(xmlFile.getPath());
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
             LOG.debugf("Writing cache file for: %s to: %s", xmlFile, cachedFile);
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
         } catch (Exception e) {
             LOG.unableToWriteCachedFile(cachedFile.getPath(), e.getMessage());
 		}
 
 		return this;
 	}
 
 	private File determineCachedDomFile(File xmlFile) {
 		return new File( xmlFile.getAbsolutePath() + ".bin" );
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
 		final File cachedFile = determineCachedDomFile( xmlFile );
 
 		final boolean useCachedFile = xmlFile.exists()
 				&& cachedFile.exists()
 				&& xmlFile.lastModified() < cachedFile.lastModified();
 
 		if ( ! useCachedFile ) {
 			throw new FileNotFoundException( "Cached file could not be found or could not be used" );
 		}
 
         LOG.readingCachedMappings(cachedFile);
 		Document document = ( Document ) SerializationHelper.deserialize( new FileInputStream( cachedFile ) );
 		add( new XmlDocumentImpl( document, "file", xmlFile.getAbsolutePath() ) );
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
 		return addCacheableFile( new File( xmlFile ) );
 	}
 
 
 	/**
 	 * Read mappings from a <tt>String</tt>
 	 *
 	 * @param xml an XML string
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates problems parsing the
 	 * given XML string
 	 */
 	public Configuration addXML(String xml) throws MappingException {
         LOG.debugf("Mapping XML:\n%s", xml);
 		final InputSource inputSource = new InputSource( new StringReader( xml ) );
 		add( inputSource, "string", "XML String" );
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
 		final String urlExternalForm = url.toExternalForm();
 
         LOG.debugf("Reading mapping document from URL : %s", urlExternalForm);
 
 		try {
 			add( url.openStream(), "URL", urlExternalForm );
 		}
 		catch ( IOException e ) {
 			throw new InvalidMappingException( "Unable to open url stream [" + urlExternalForm + "]", "URL", urlExternalForm, e );
 		}
 		return this;
 	}
 
 	private XmlDocument add(InputStream inputStream, final String type, final String name) {
 		final InputSource inputSource = new InputSource( inputStream );
 		try {
 			return add( inputSource, type, name );
 		}
 		finally {
 			try {
 				inputStream.close();
 			}
 			catch ( IOException ignore ) {
                 LOG.trace("Was unable to close input stream");
 			}
 		}
 	}
 
 	/**
 	 * Read mappings from a DOM <tt>Document</tt>
 	 *
 	 * @param doc The DOM document
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the DOM or processing
 	 * the mapping document.
 	 */
 	public Configuration addDocument(org.w3c.dom.Document doc) throws MappingException {
         LOG.debugf("Mapping Document:\n%s", doc);
 
 		final Document document = xmlHelper.createDOMReader().read( doc );
 		add( new XmlDocumentImpl( document, "unknown", null ) );
 
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
 		add( xmlInputStream, "input stream", null );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resource (i.e. classpath lookup).
 	 *
 	 * @param resourceName The resource name
 	 * @param classLoader The class loader to use.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
         LOG.readingMappingsFromResource(resourceName);
 		InputStream resourceInputStream = classLoader.getResourceAsStream( resourceName );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
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
         LOG.readingMappingsFromResource(resourceName);
 		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
 		InputStream resourceInputStream = null;
 		if ( contextClassLoader != null ) {
 			resourceInputStream = contextClassLoader.getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			resourceInputStream = Environment.class.getClassLoader().getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
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
 		String mappingResourceName = persistentClass.getName().replace( '.', '/' ) + ".hbm.xml";
         LOG.readingMappingsFromResource(mappingResourceName);
 		return addResource( mappingResourceName, persistentClass.getClassLoader() );
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
 		final ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder()
 				.applySettings( properties )
 				.buildServiceRegistry();
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
-						( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
+						( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
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
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects() {
 			return auxiliaryDatabaseObjects.iterator();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse() {
 			return iterateAuxiliaryDatabaseObjectsInReverse();
 		}
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
index 19974c0a34..24a46ffbf1 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
@@ -1,382 +1,382 @@
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
 package org.hibernate.metamodel;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import org.jboss.logging.Logger;
 import org.w3c.dom.Document;
 import org.xml.sax.EntityResolver;
 
 import org.hibernate.cfg.EJB3DTDEntityResolver;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.jaxb.JaxbRoot;
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.jaxb.SourceType;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.MappingNotFoundException;
 import org.hibernate.metamodel.source.internal.JaxbHelper;
 import org.hibernate.metamodel.source.internal.MetadataBuilderImpl;
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataSources {
 	private static final Logger LOG = Logger.getLogger( MetadataSources.class );
 
 	private List<JaxbRoot> jaxbRootList = new ArrayList<JaxbRoot>();
 	private LinkedHashSet<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();
 	private LinkedHashSet<String> annotatedPackages = new LinkedHashSet<String>();
 
 	private final JaxbHelper jaxbHelper;
 
-	private final BasicServiceRegistry serviceRegistry;
+	private final ServiceRegistry serviceRegistry;
 	private final EntityResolver entityResolver;
 	private final NamingStrategy namingStrategy;
 
 	private final MetadataBuilderImpl metadataBuilder;
 
-	public MetadataSources(BasicServiceRegistry serviceRegistry) {
+	public MetadataSources(ServiceRegistry serviceRegistry) {
 		this( serviceRegistry, EJB3DTDEntityResolver.INSTANCE, EJB3NamingStrategy.INSTANCE );
 	}
 
-	public MetadataSources(BasicServiceRegistry serviceRegistry, EntityResolver entityResolver, NamingStrategy namingStrategy) {
+	public MetadataSources(ServiceRegistry serviceRegistry, EntityResolver entityResolver, NamingStrategy namingStrategy) {
 		this.serviceRegistry = serviceRegistry;
 		this.entityResolver = entityResolver;
 		this.namingStrategy = namingStrategy;
 
 		this.jaxbHelper = new JaxbHelper( this );
 		this.metadataBuilder = new MetadataBuilderImpl( this );
 	}
 
 	public List<JaxbRoot> getJaxbRootList() {
 		return jaxbRootList;
 	}
 
 	public Iterable<String> getAnnotatedPackages() {
 		return annotatedPackages;
 	}
 
 	public Iterable<Class<?>> getAnnotatedClasses() {
 		return annotatedClasses;
 	}
 
-	public BasicServiceRegistry getServiceRegistry() {
+	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	public MetadataBuilder getMetadataBuilder() {
 		return metadataBuilder;
 	}
 
 	public Metadata buildMetadata() {
 		return getMetadataBuilder().buildMetadata();
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
 		InputStream resourceInputStream = classLoaderService().locateResourceStream( name );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( origin );
 		}
 		add( resourceInputStream, origin, true );
 
 		return this;
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	private JaxbRoot add(InputStream inputStream, Origin origin, boolean close) {
 		try {
 			JaxbRoot jaxbRoot = jaxbHelper.unmarshal( inputStream, origin );
 			jaxbRootList.add( jaxbRoot );
 			return jaxbRoot;
 		}
 		finally {
 			if ( close ) {
 				try {
 					inputStream.close();
 				}
 				catch ( IOException ignore ) {
 					LOG.trace( "Was unable to close input stream" );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class named {@code foo.bar.Foo} is
 	 * mapped by a file named {@code foo/bar/Foo.hbm.xml} which can be resolved as a classpath resource.
 	 *
 	 * @param entityClass The mapped class. Cannot be {@code null} null.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
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
 	 * @param path The path to a file.  Expected to be resolvable by {@link File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addFile(java.io.File)
 	 */
 	public MetadataSources addFile(String path) {
 		return addFile( new File( path ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param file The reference to the XML file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addFile(File file) {
 		final String name = file.getAbsolutePath();
 		LOG.tracef( "reading mappings from file : %s", name );
 		final Origin origin = new Origin( SourceType.FILE, name );
 		try {
 			add( new FileInputStream( file ), origin, true );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * See {@link #addCacheableFile(java.io.File)} for description
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public MetadataSources addCacheableFile(String path) {
 		return this; // todo : implement method body
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
 		return this; // todo : implement method body
 	}
 
 	/**
 	 * Read metadata from an {@link InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addInputStream(InputStream xmlInputStream) {
 		add( xmlInputStream, new Origin( SourceType.INPUT_STREAM, "<unknown>" ), false );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a {@link URL}
 	 *
 	 * @param url The url for the mapping document to be read.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addURL(URL url) {
 		final String urlExternalForm = url.toExternalForm();
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		final Origin origin = new Origin( SourceType.URL, urlExternalForm );
 		try {
 			add( url.openStream(), origin, true );
 		}
 		catch ( IOException e ) {
 			throw new MappingNotFoundException( "Unable to open url stream [" + urlExternalForm + "]", e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * Read mappings from a DOM {@link Document}
 	 *
 	 * @param document The DOM document
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addDocument(Document document) {
 		final Origin origin = new Origin( SourceType.DOM, "<unknown>" );
 		JaxbRoot jaxbRoot = jaxbHelper.unmarshal( document, origin );
 		jaxbRootList.add( jaxbRoot );
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
 						try {
 							add( jarFile.getInputStream( zipEntry ), origin, true );
 						}
 						catch ( Exception e ) {
 							throw new MappingException( "could not read mapping documents", e, origin );
 						}
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
index 2e7f489e2c..985272bc47 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataImplementor.java
@@ -1,77 +1,77 @@
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
 package org.hibernate.metamodel.source;
 
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.Database;
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.TypeResolver;
 
 /**
  * @author Steve Ebersole
  */
 public interface MetadataImplementor extends Metadata, BindingContext, Mapping {
-	public BasicServiceRegistry getServiceRegistry();
+	public ServiceRegistry getServiceRegistry();
 
 	public Database getDatabase();
 
 	public TypeResolver getTypeResolver();
 
 	public void addImport(String entityName, String entityName1);
 
 	public void addEntity(EntityBinding entityBinding);
 
 	public void addCollection(PluralAttributeBinding collectionBinding);
 
 	public void addFetchProfile(FetchProfile profile);
 
 	public void addTypeDefinition(TypeDef typeDef);
 
 	public void addFilterDefinition(FilterDefinition filterDefinition);
 
 	public void addIdGenerator(IdGenerator generator);
 
 	public void registerIdentifierGenerator(String name, String clazz);
 
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def);
 
 	public void addNamedQuery(NamedQueryDefinition def);
 
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition);
 
 	// todo : this needs to move to AnnotationBindingContext
 	public void setGloballyQuotedIdentifiers(boolean b);
 
 	public MetaAttributeContext getGlobalMetaAttributeContext();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
index 92d65fbba4..5e07de1e95 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
@@ -1,196 +1,196 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataBuilder;
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.config.spi.ConfigurationService;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataBuilderImpl implements MetadataBuilder {
 	private final MetadataSources sources;
 	private final OptionsImpl options;
 
 	public MetadataBuilderImpl(MetadataSources sources) {
 		this.sources = sources;
 		this.options = new OptionsImpl( sources.getServiceRegistry() );
 	}
 
 	@Override
 	public MetadataBuilder with(NamingStrategy namingStrategy) {
 		this.options.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(MetadataSourceProcessingOrder metadataSourceProcessingOrder) {
 		this.options.metadataSourceProcessingOrder = metadataSourceProcessingOrder;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(SharedCacheMode sharedCacheMode) {
 		this.options.sharedCacheMode = sharedCacheMode;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(AccessType accessType) {
 		this.options.defaultCacheAccessType = accessType;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled) {
 		this.options.useNewIdentifierGenerators = enabled;
 		return this;
 	}
 
 	@Override
 	public Metadata buildMetadata() {
 		return new MetadataImpl( sources, options );
 	}
 
 	private static class OptionsImpl implements Metadata.Options {
 		private MetadataSourceProcessingOrder metadataSourceProcessingOrder = MetadataSourceProcessingOrder.HBM_FIRST;
 		private NamingStrategy namingStrategy = EJB3NamingStrategy.INSTANCE;
 		private SharedCacheMode sharedCacheMode = SharedCacheMode.ENABLE_SELECTIVE;
 		private AccessType defaultCacheAccessType;
         private boolean useNewIdentifierGenerators;
         private boolean globallyQuotedIdentifiers;
 		private String defaultSchemaName;
 		private String defaultCatalogName;
 
-		public OptionsImpl(BasicServiceRegistry serviceRegistry) {
+		public OptionsImpl(ServiceRegistry serviceRegistry) {
 			ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
 
 			// cache access type
 			defaultCacheAccessType = configService.getSetting(
 					AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY,
 					new ConfigurationService.Converter<AccessType>() {
 						@Override
 						public AccessType convert(Object value) {
 							return AccessType.fromExternalName( value.toString() );
 						}
 					}
 			);
 
 			useNewIdentifierGenerators = configService.getSetting(
 					AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS,
 					new ConfigurationService.Converter<Boolean>() {
 						@Override
 						public Boolean convert(Object value) {
 							return Boolean.parseBoolean( value.toString() );
 						}
 					},
 					false
 			);
 
 			defaultSchemaName = configService.getSetting(
 					AvailableSettings.DEFAULT_SCHEMA,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
 
 			defaultCatalogName = configService.getSetting(
 					AvailableSettings.DEFAULT_CATALOG,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
 
             globallyQuotedIdentifiers = configService.getSetting(
                     AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS,
                     new ConfigurationService.Converter<Boolean>() {
                         @Override
                         public Boolean convert(Object value) {
                             return Boolean.parseBoolean( value.toString() );
                         }
                     },
                     false
             );
 		}
 
 
 		@Override
 		public MetadataSourceProcessingOrder getMetadataSourceProcessingOrder() {
 			return metadataSourceProcessingOrder;
 		}
 
 		@Override
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		@Override
 		public AccessType getDefaultAccessType() {
 			return defaultCacheAccessType;
 		}
 
 		@Override
 		public SharedCacheMode getSharedCacheMode() {
 			return sharedCacheMode;
 		}
 
 		@Override
         public boolean useNewIdentifierGenerators() {
             return useNewIdentifierGenerators;
         }
 
         @Override
         public boolean isGloballyQuotedIdentifiers() {
             return globallyQuotedIdentifiers;
         }
 
         @Override
 		public String getDefaultSchemaName() {
 			return defaultSchemaName;
 		}
 
 		@Override
 		public String getDefaultCatalogName() {
 			return defaultCatalogName;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index ab14627e0d..0a1ab528d6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,602 +1,602 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
-import org.hibernate.metamodel.binding.PluralAttributeBinding;
-import org.hibernate.metamodel.source.MappingDefaults;
-import org.hibernate.metamodel.source.MetaAttributeContext;
-import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.MetadataSourceProcessor;
-import org.hibernate.metamodel.source.annotations.AnnotationMetadataSourceProcessorImpl;
-import org.hibernate.metamodel.source.hbm.HbmMetadataSourceProcessorImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
+import org.hibernate.metamodel.source.MappingDefaults;
+import org.hibernate.metamodel.source.MetaAttributeContext;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.MetadataSourceProcessor;
+import org.hibernate.metamodel.source.annotations.AnnotationMetadataSourceProcessorImpl;
+import org.hibernate.metamodel.source.hbm.HbmMetadataSourceProcessorImpl;
 import org.hibernate.persister.spi.PersisterClassResolver;
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MetadataImpl.class.getName()
 	);
 
-	private final BasicServiceRegistry serviceRegistry;
+	private final ServiceRegistry serviceRegistry;
 	private final Options options;
 
 	private final Value<ClassLoaderService> classLoaderService;
 	private final Value<PersisterClassResolver> persisterClassResolverService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private final MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private final Database database;
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports = new HashMap<String, String>();
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
 	private Map<String, ResultSetMappingDefinition> resultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
     private boolean globallyQuotedIdentifiers = false;
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry =  metadataSources.getServiceRegistry();
 		this.options = options;
 		this.identifierGeneratorFactory = serviceRegistry.getService( MutableIdentifierGeneratorFactory.class );
 				//new DefaultIdentifierGeneratorFactory( dialect );
 		this.database = new Database( options );
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final MetadataSourceProcessor[] metadataSourceProcessors;
 		if ( options.getMetadataSourceProcessingOrder() == MetadataSourceProcessingOrder.HBM_FIRST ) {
 			metadataSourceProcessors = new MetadataSourceProcessor[] {
 					new HbmMetadataSourceProcessorImpl( this ),
 					new AnnotationMetadataSourceProcessorImpl( this )
 			};
 		}
 		else {
 			metadataSourceProcessors = new MetadataSourceProcessor[] {
 					new AnnotationMetadataSourceProcessorImpl( this ),
 					new HbmMetadataSourceProcessorImpl( this )
 			};
 		}
 
 		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return serviceRegistry.getService( ClassLoaderService.class );
 					}
 				}
 		);
 		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
 					@Override
 					public PersisterClassResolver initialize() {
 						return serviceRegistry.getService( PersisterClassResolver.class );
 					}
 				}
 		);
 
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( metadataSourceProcessors, metadataSources );
 		bindIndependentMetadata( metadataSourceProcessors, metadataSources );
 		bindTypeDependentMetadata( metadataSourceProcessors, metadataSources );
 		bindMappingMetadata( metadataSourceProcessors, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( metadataSourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new AssociationResolver( this ).resolve();
 		new HibernateTypeResolver( this ).resolve();
 		// IdentifierGeneratorResolver.resolve() must execute after AttributeTypeResolver.resolve()
 		new IdentifierGeneratorResolver( this ).resolve();
 	}
 
 	private void prepare(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
 		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
 			metadataSourceProcessor.processMappingDependentMetadata( metadataSources );
 		}
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		if ( profile == null || profile.getName() == null ) {
 			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
 		}
 		fetchProfiles.put( profile.getName(), profile );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition def) {
 		if ( def == null || def.getFilterName() == null ) {
 			throw new IllegalArgumentException( "Filter definition object or name is null: "  + def );
 		}
 		filterDefs.put( def.getFilterName(), def );
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefs.values();
 	}
 
 	@Override
 	public void addIdGenerator(IdGenerator generator) {
 		if ( generator == null || generator.getName() == null ) {
 			throw new IllegalArgumentException( "ID generator object or name is null." );
 		}
 		idGenerators.put( generator.getName(), generator );
 	}
 
 	@Override
 	public IdGenerator getIdGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGenerators.get( name );
 	}
 	@Override
 	public void registerIdentifierGenerator(String name, String generatorClassName) {
 		 identifierGeneratorFactory.register( name, classLoaderService().classForName( generatorClassName ) );
 	}
 
 	@Override
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named native query definition object or name is null: " + def.getQueryString() );
 		}
 		namedNativeQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid native query name" );
 		}
 		return namedNativeQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
 		return namedNativeQueryDefs.values();
 	}
 
 	@Override
 	public void addNamedQuery(NamedQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
 		}
 		namedQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions() {
 		return namedQueryDefs.values();
 	}
 
 	@Override
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		if ( resultSetMappingDefinition == null || resultSetMappingDefinition.getName() == null ) {
 			throw new IllegalArgumentException( "Result-set mapping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 		else if ( typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition name is null: " + typeDef.getTypeClass() );
 		}
 		final TypeDef previous = typeDefs.put( typeDef.getName(), typeDef );
 		if ( previous != null ) {
 			LOG.debugf( "Duplicate typedef name [%s] now -> %s", typeDef.getName(), typeDef.getTypeClass() );
 		}
 	}
 
 	@Override
 	public Iterable<TypeDef> getTypeDefinitions() {
 		return typeDefs.values();
 	}
 
 	@Override
 	public TypeDef getTypeDefinition(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return classLoaderService.getValue();
 	}
 
 	private PersisterClassResolver persisterClassResolverService() {
 		return persisterClassResolverService.getValue();
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		return sessionFactoryBuilder.buildSessionFactory();
 	}
 
 	@Override
-	public BasicServiceRegistry getServiceRegistry() {
+	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> locateClassByName(String name) {
 		return classLoaderService().classForName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		// todo : have this perform some analysis of the incoming type name to determine appropriate return
 		return new BasicType( className, makeClassReference( className ) );
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(final String className) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.getValue().classForName( className );
 					}
 				}
 		);
 	}
 
 	@Override
 	public String qualifyClassName(String name) {
 		return name;
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
 		EntityBinding binding = entityBindingMap.get( entityName );
 		if ( binding == null ) {
 			throw new IllegalStateException( "Unknown entity binding: " + entityName );
 		}
 
 		do {
 			if ( binding.isRoot() ) {
 				return binding;
 			}
 			binding = binding.getSuperEntityBinding();
 		} while ( binding != null );
 
 		throw new AssertionFailure( "Entity binding has no root: " + entityName );
 	}
 
 	public Iterable<EntityBinding> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	public void addEntity(EntityBinding entityBinding) {
 		final String entityName = entityBinding.getEntity().getName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, entityBinding );
 	}
 
 	public PluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	@Override
 	public Iterable<PluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
 		final String owningEntityName = pluralAttributeBinding.getContainer().getPathBase();
 		final String attributeName = pluralAttributeBinding.getAttribute().getName();
 		final String collectionRole = owningEntityName + '.' + attributeName;
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, pluralAttributeBinding );
 	}
 
 	public void addImport(String importName, String entityName) {
 		if ( importName == null || entityName == null ) {
 			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 	@Override
 	public Iterable<Map.Entry<String, String>> getImports() {
 		return imports.entrySet();
 	}
 
 	@Override
 	public Iterable<FetchProfile> getFetchProfiles() {
 		return fetchProfiles.values();
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		return sessionFactoryBuilder;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return options.getNamingStrategy();
 	}
 
     @Override
     public boolean isGloballyQuotedIdentifiers() {
         return globallyQuotedIdentifiers || getOptions().isGloballyQuotedIdentifiers();
     }
 
     public void setGloballyQuotedIdentifiers(boolean globallyQuotedIdentifiers){
        this.globallyQuotedIdentifiers = globallyQuotedIdentifiers;
     }
 
     @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getGlobalMetaAttributeContext() {
 		return globalMetaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return this;
 	}
 
 	private static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	private static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 	private static final String DEFAULT_CASCADE = "none";
 	private static final String DEFAULT_PROPERTY_ACCESS = "property";
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getHierarchyDetails()
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getResolvedTypeMapping();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		AttributeBinding idBinding = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.locateAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping();
 	}
 
 	private class MappingDefaultsImpl implements MappingDefaults {
 
 		@Override
 		public String getPackageName() {
 			return null;
 		}
 
 		@Override
 		public String getSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
 		public String getCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
 		public String getIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getCascadeStyle() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
 		public String getPropertyAccessorName() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
 		public boolean areAssociationsLazy() {
 			return true;
 		}
 
 		private final Value<AccessType> regionFactorySpecifiedDefaultAccessType = new Value<AccessType>(
 				new Value.DeferredInitializer<AccessType>() {
 					@Override
 					public AccessType initialize() {
 						final RegionFactory regionFactory = getServiceRegistry().getService( RegionFactory.class );
 						return regionFactory.getDefaultAccessType();
 					}
 				}
 		);
 
 		@Override
 		public AccessType getCacheAccessType() {
 			return options.getDefaultAccessType() != null
 					? options.getDefaultAccessType()
 					: regionFactorySpecifiedDefaultAccessType.getValue();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/BasicServiceRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/BasicServiceRegistry.java
deleted file mode 100644
index dc6bf9aae0..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/service/BasicServiceRegistry.java
+++ /dev/null
@@ -1,30 +0,0 @@
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
-package org.hibernate.service;
-
-/**
- * @author Steve Ebersole
- */
-public interface BasicServiceRegistry extends ServiceRegistry {
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/BootstrapServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/service/BootstrapServiceRegistryBuilder.java
new file mode 100644
index 0000000000..3a73fd721a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/BootstrapServiceRegistryBuilder.java
@@ -0,0 +1,127 @@
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
+package org.hibernate.service;
+
+import java.util.LinkedHashSet;
+
+import org.hibernate.integrator.internal.IntegratorServiceImpl;
+import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.service.classloading.internal.ClassLoaderServiceImpl;
+import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+
+/**
+ * Builder for bootstrap {@link ServiceRegistry} instances.
+ *
+ * @author Steve Ebersole
+ *
+ * @see BootstrapServiceRegistryImpl
+ * @see ServiceRegistryBuilder#ServiceRegistryBuilder(BootstrapServiceRegistryImpl)
+ */
+public class BootstrapServiceRegistryBuilder {
+	private final LinkedHashSet<Integrator> providedIntegrators = new LinkedHashSet<Integrator>();
+	private ClassLoader applicationClassLoader;
+	private ClassLoader resourcesClassLoader;
+	private ClassLoader hibernateClassLoader;
+	private ClassLoader environmentClassLoader;
+
+	/**
+	 * Add an {@link Integrator} to be applied to the bootstrap registry.
+	 *
+	 * @param integrator The integrator to add.
+	 * @return {@code this}, for method chaining
+	 */
+	public BootstrapServiceRegistryBuilder with(Integrator integrator) {
+		providedIntegrators.add( integrator );
+		return this;
+	}
+
+	/**
+	 * Applies the specified {@link ClassLoader} as the application class loader for the bootstrap registry
+	 *
+	 * @param classLoader The class loader to use
+	 * @return {@code this}, for method chaining
+	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public BootstrapServiceRegistryBuilder withApplicationClassLoader(ClassLoader classLoader) {
+		this.applicationClassLoader = classLoader;
+		return this;
+	}
+
+	/**
+	 * Applies the specified {@link ClassLoader} as the resource class loader for the bootstrap registry
+	 *
+	 * @param classLoader The class loader to use
+	 * @return {@code this}, for method chaining
+	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public BootstrapServiceRegistryBuilder withResourceClassLoader(ClassLoader classLoader) {
+		this.resourcesClassLoader = classLoader;
+		return this;
+	}
+
+	/**
+	 * Applies the specified {@link ClassLoader} as the Hibernate class loader for the bootstrap registry
+	 *
+	 * @param classLoader The class loader to use
+	 * @return {@code this}, for method chaining
+	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public BootstrapServiceRegistryBuilder withHibernateClassLoader(ClassLoader classLoader) {
+		this.hibernateClassLoader = classLoader;
+		return this;
+	}
+
+	/**
+	 * Applies the specified {@link ClassLoader} as the environment (or system) class loader for the bootstrap registry
+	 *
+	 * @param classLoader The class loader to use
+	 * @return {@code this}, for method chaining
+	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public BootstrapServiceRegistryBuilder withEnvironmentClassLoader(ClassLoader classLoader) {
+		this.environmentClassLoader = classLoader;
+		return this;
+	}
+
+	/**
+	 * Build the bootstrap registry.
+	 *
+	 * @return The built bootstrap registry
+	 */
+	public ServiceRegistry build() {
+		final ClassLoaderServiceImpl classLoaderService = new ClassLoaderServiceImpl(
+				applicationClassLoader,
+				resourcesClassLoader,
+				hibernateClassLoader,
+				environmentClassLoader
+		);
+
+		final IntegratorServiceImpl integratorService = new IntegratorServiceImpl(
+				providedIntegrators,
+				classLoaderService
+		);
+
+		return new BootstrapServiceRegistryImpl( classLoaderService, integratorService );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistry.java
index bae2006e7f..910f301a8f 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistry.java
@@ -1,53 +1,53 @@
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
 package org.hibernate.service;
 
 /**
- * The registry of {@link Service services}.
+ * A registry of {@link Service services}.
  *
  * @author Steve Ebersole
  */
 public interface ServiceRegistry {
 	/**
 	 * Retrieve this registry's parent registry.
 	 * 
 	 * @return The parent registry.  May be null.
 	 */
 	public ServiceRegistry getParentServiceRegistry();
 
 	/**
 	 * Retrieve a service by role.  If service is not found, but a {@link org.hibernate.service.spi.BasicServiceInitiator} is registered for
 	 * this service role, the service will be initialized and returned.
 	 * <p/>
 	 * NOTE: We cannot return {@code <R extends Service<T>>} here because the service might come from the parent...
 	 * 
 	 * @param serviceRole The service role
 	 * @param <R> The service role type
 	 *
 	 * @return The requested service.
 	 *
 	 * @throws UnknownServiceException Indicates the service was not known.
 	 */
 	public <R extends Service> R getService(Class<R> serviceRole);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
index fa07d817fb..136fdce588 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
@@ -1,261 +1,264 @@
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
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.integrator.spi.ServiceContributingIntegrator;
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.jaxb.SourceType;
 import org.hibernate.internal.util.Value;
 import org.hibernate.internal.util.config.ConfigurationException;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.internal.jaxb.cfg.JaxbHibernateConfiguration;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.service.internal.JaxbProcessor;
 import org.hibernate.service.internal.ProvidedService;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
- * Builder for basic service registry instances.
+ * Builder for standard {@link ServiceRegistry} instances.
  *
  * @author Steve Ebersole
+ * 
+ * @see StandardServiceRegistryImpl
+ * @see BootstrapServiceRegistryBuilder
  */
 public class ServiceRegistryBuilder {
 	private static final Logger log = Logger.getLogger( ServiceRegistryBuilder.class );
 
 	public static final String DEFAULT_CFG_RESOURCE_NAME = "hibernate.cfg.xml";
 
 	private final Map settings;
 	private final List<BasicServiceInitiator> initiators = standardInitiatorList();
 	private final List<ProvidedService> providedServices = new ArrayList<ProvidedService>();
 
 	private final BootstrapServiceRegistryImpl bootstrapServiceRegistry;
 
 	/**
 	 * Create a default builder
 	 */
 	public ServiceRegistryBuilder() {
 		this( new BootstrapServiceRegistryImpl() );
 	}
 
 	/**
 	 * Create a builder with the specified bootstrap services.
 	 *
 	 * @param bootstrapServiceRegistry Provided bootstrap registry to use.
 	 */
 	public ServiceRegistryBuilder(BootstrapServiceRegistryImpl bootstrapServiceRegistry) {
 		this.settings = Environment.getProperties();
 		this.bootstrapServiceRegistry = bootstrapServiceRegistry;
 	}
 
 	/**
 	 * Used from the {@link #initiators} variable initializer
 	 *
 	 * @return List of standard initiators
 	 */
 	private static List<BasicServiceInitiator> standardInitiatorList() {
 		final List<BasicServiceInitiator> initiators = new ArrayList<BasicServiceInitiator>();
 		initiators.addAll( StandardServiceInitiators.LIST );
 		return initiators;
 	}
 
 	/**
 	 * Read settings from a {@link Properties} file.  Differs from {@link #configure()} and {@link #configure(String)}
 	 * in that here we read a {@link Properties} file while for {@link #configure} we read the XML variant.
 	 *
 	 * @param resourceName The name by which to perform a resource look up for the properties file.
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @see #configure()
 	 * @see #configure(String)
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public ServiceRegistryBuilder loadProperties(String resourceName) {
 		InputStream stream = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResourceStream( resourceName );
 		try {
 			Properties properties = new Properties();
 			properties.load( stream );
 			settings.putAll( properties );
 		}
 		catch (IOException e) {
 			throw new ConfigurationException( "Unable to apply settings from properties file [" + resourceName + "]", e );
 		}
 		finally {
 			try {
 				stream.close();
 			}
 			catch (IOException e) {
 				log.debug(
 						String.format( "Unable to close properties file [%s] stream", resourceName ),
 						e
 				);
 			}
 		}
 
 		return this;
 	}
 
 	/**
 	 * Read setting information from an XML file using the standard resource location
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @see #DEFAULT_CFG_RESOURCE_NAME
 	 * @see #configure(String)
 	 * @see #loadProperties(String)
 	 */
 	public ServiceRegistryBuilder configure() {
 		return configure( DEFAULT_CFG_RESOURCE_NAME );
 	}
 
 	/**
 	 * Read setting information from an XML file using the named resource location
 	 *
 	 * @param resourceName The named resource
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @see #loadProperties(String)
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public ServiceRegistryBuilder configure(String resourceName) {
 		InputStream stream = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResourceStream( resourceName );
 		JaxbHibernateConfiguration configurationElement = jaxbProcessorHolder.getValue().unmarshal(
 				stream,
 				new Origin( SourceType.RESOURCE, resourceName )
 		);
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbProperty xmlProperty : configurationElement.getSessionFactory().getProperty() ) {
 			settings.put( xmlProperty.getName(), xmlProperty.getValue() );
 		}
 
 		return this;
 	}
 
 	private Value<JaxbProcessor> jaxbProcessorHolder = new Value<JaxbProcessor>(
 			new Value.DeferredInitializer<JaxbProcessor>() {
 				@Override
 				public JaxbProcessor initialize() {
 					return new JaxbProcessor( bootstrapServiceRegistry.getService( ClassLoaderService.class ) );
 				}
 			}
 	);
 
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
-	public BasicServiceRegistry buildServiceRegistry() {
+	public ServiceRegistry buildServiceRegistry() {
 		Map<?,?> settingsCopy = new HashMap();
 		settingsCopy.putAll( settings );
 		Environment.verifyProperties( settingsCopy );
 		ConfigurationHelper.resolvePlaceHolders( settingsCopy );
 
 		for ( Integrator integrator : bootstrapServiceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			if ( ServiceContributingIntegrator.class.isInstance( integrator ) ) {
 				ServiceContributingIntegrator.class.cast( integrator ).prepareServices( this );
 			}
 		}
 
-		return new BasicServiceRegistryImpl( bootstrapServiceRegistry, initiators, providedServices, settingsCopy );
+		return new StandardServiceRegistryImpl( bootstrapServiceRegistry, initiators, providedServices, settingsCopy );
 	}
 
 	/**
 	 * Destroy a service registry.  Applications should only destroy registries they have explicitly created.
 	 *
 	 * @param serviceRegistry The registry to be closed.
 	 */
-	public static void destroy(BasicServiceRegistry serviceRegistry) {
-		( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
+	public static void destroy(ServiceRegistry serviceRegistry) {
+		( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index b7c46f0c09..caa32b0cee 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,82 +1,85 @@
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
+import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.id.factory.internal.MutableIdentifierGeneratorFactoryInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
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
+ * Central definition of the standard set of service initiators defined by Hibernate.
+ * 
  * @author Steve Ebersole
  */
 public class StandardServiceInitiators {
 	public static List<BasicServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<BasicServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<BasicServiceInitiator> serviceInitiators = new ArrayList<BasicServiceInitiator>();
 
 		serviceInitiators.add( ConfigurationServiceInitiator.INSTANCE );
 
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
 
 		serviceInitiators.add( RegionFactoryInitiator.INSTANCE );
 
-		return serviceInitiators;
+		return Collections.unmodifiableList( serviceInitiators );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
index b7a9366c2d..5dea5ad49a 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
@@ -1,260 +1,261 @@
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
 
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.service.Service;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.UnknownServiceException;
 import org.hibernate.service.jmx.spi.JmxService;
 import org.hibernate.service.spi.InjectService;
 import org.hibernate.service.spi.Manageable;
 import org.hibernate.service.spi.ServiceBinding;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.Startable;
 import org.hibernate.service.spi.Stoppable;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractServiceRegistryImpl implements ServiceRegistryImplementor, ServiceBinding.OwningRegistry {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AbstractServiceRegistryImpl.class.getName() );
 
 	private final ServiceRegistryImplementor parent;
 
 	private ConcurrentHashMap<Class,ServiceBinding> serviceBindingMap;
 	// IMPL NOTE : the list used for ordered destruction.  Cannot used map above because we need to
 	// iterate it in reverse order which is only available through ListIterator
 	private List<Service> serviceList = new ArrayList<Service>();
 
+	@SuppressWarnings( {"UnusedDeclaration"})
 	protected AbstractServiceRegistryImpl() {
 		this( null );
 	}
 
 	protected AbstractServiceRegistryImpl(ServiceRegistryImplementor parent) {
 		this.parent = parent;
 		// assume 20 services for initial sizing
 		this.serviceBindingMap = CollectionHelper.concurrentMap( 20 );
 		this.serviceList = CollectionHelper.arrayList( 20 );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected <R extends Service> void createServiceBinding(ServiceInitiator<R> initiator) {
 		serviceBindingMap.put( initiator.getServiceInitiated(), new ServiceBinding( this, initiator ) );
 	}
 
 	protected <R extends Service> void createServiceBinding(ProvidedService<R> providedService) {
 		ServiceBinding<R> binding = locateServiceBinding( providedService.getServiceRole(), false );
 		if ( binding == null ) {
 			binding = new ServiceBinding<R>( this, providedService.getServiceRole(), providedService.getService() );
 			serviceBindingMap.put( providedService.getServiceRole(), binding );
 		}
 		registerService( binding, providedService.getService() );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public ServiceRegistry getParentServiceRegistry() {
 		return parent;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
 		return locateServiceBinding( serviceRole, true );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole, boolean checkParent) {
 		ServiceBinding<R> serviceBinding = serviceBindingMap.get( serviceRole );
 		if ( serviceBinding == null && checkParent && parent != null ) {
 			// look in parent
 			serviceBinding = parent.locateServiceBinding( serviceRole );
 		}
 		return serviceBinding;
 	}
 
 	@Override
 	public <R extends Service> R getService(Class<R> serviceRole) {
 		final ServiceBinding<R> serviceBinding = locateServiceBinding( serviceRole );
 		if ( serviceBinding == null ) {
 			throw new UnknownServiceException( serviceRole );
 		}
 
 		R service = serviceBinding.getService();
 		if ( service == null ) {
 			service = initializeService( serviceBinding );
 		}
 
 		return service;
 	}
 
 	protected <R extends Service> void registerService(ServiceBinding<R> serviceBinding, R service) {
 		R priorServiceInstance = serviceBinding.getService();
 		serviceBinding.setService( service );
 		if ( priorServiceInstance != null ) {
 			serviceList.remove( priorServiceInstance );
 		}
 		serviceList.add( service );
 	}
 
 	private <R extends Service> R initializeService(ServiceBinding<R> serviceBinding) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.trace( "Initializing service [role=" + serviceBinding.getServiceRole().getName() + "]" );
 		}
 
 		// PHASE 1 : create service
 		R service = createService( serviceBinding );
 		if ( service == null ) {
 			return null;
 		}
 
 		// PHASE 2 : configure service (***potentially recursive***)
 		configureService( service );
 
 		// PHASE 3 : Start service
 		startService( serviceBinding );
 
 		return service;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	protected <R extends Service> R createService(ServiceBinding<R> serviceBinding) {
 		final ServiceInitiator<R> serviceInitiator = serviceBinding.getServiceInitiator();
 		if ( serviceInitiator == null ) {
 			// this condition should never ever occur
 			throw new UnknownServiceException( serviceBinding.getServiceRole() );
 		}
 
 		try {
 			R service = serviceBinding.getServiceRegistry().initiateService( serviceInitiator );
 			// IMPL NOTE : the register call here is important to avoid potential stack overflow issues
 			//		from recursive calls through #configureService
 			registerService( serviceBinding, service );
 			return service;
 		}
 		catch ( ServiceException e ) {
 			throw e;
 		}
 		catch ( Exception e ) {
 			throw new ServiceException( "Unable to create requested service [" + serviceBinding.getServiceRole().getName() + "]", e );
 		}
 	}
 
 	protected abstract <T extends Service> void configureService(T service);
 
 	protected <T extends Service> void applyInjections(T service) {
 		try {
 			for ( Method method : service.getClass().getMethods() ) {
 				InjectService injectService = method.getAnnotation( InjectService.class );
 				if ( injectService == null ) {
 					continue;
 				}
 
 				applyInjection( service, method, injectService );
 			}
 		}
 		catch (NullPointerException e) {
             LOG.error("NPE injecting service deps : " + service.getClass().getName());
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private <T extends Service> void applyInjection(T service, Method injectionMethod, InjectService injectService) {
 		if ( injectionMethod.getParameterTypes() == null || injectionMethod.getParameterTypes().length != 1 ) {
 			throw new ServiceDependencyException(
 					"Encountered @InjectService on method with unexpected number of parameters"
 			);
 		}
 
 		Class dependentServiceRole = injectService.serviceRole();
 		if ( dependentServiceRole == null || dependentServiceRole.equals( Void.class ) ) {
 			dependentServiceRole = injectionMethod.getParameterTypes()[0];
 		}
 
 		// todo : because of the use of proxies, this is no longer returning null here...
 
 		final Service dependantService = getService( dependentServiceRole );
 		if ( dependantService == null ) {
 			if ( injectService.required() ) {
 				throw new ServiceDependencyException(
 						"Dependency [" + dependentServiceRole + "] declared by service [" + service + "] not found"
 				);
 			}
 		}
 		else {
 			try {
 				injectionMethod.invoke( service, dependantService );
 			}
 			catch ( Exception e ) {
 				throw new ServiceDependencyException( "Cannot inject dependency service", e );
 			}
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected <R extends Service> void startService(ServiceBinding<R> serviceBinding) {
 		if ( Startable.class.isInstance( serviceBinding.getService() ) ) {
 			( (Startable) serviceBinding.getService() ).start();
 		}
 
 		if ( Manageable.class.isInstance( serviceBinding.getService() ) ) {
 			getService( JmxService.class ).registerService(
 					(Manageable) serviceBinding.getService(),
 					serviceBinding.getServiceRole()
 			);
 		}
 	}
 
 	public void destroy() {
 		ListIterator<Service> serviceIterator = serviceList.listIterator( serviceList.size() );
 		while ( serviceIterator.hasPrevious() ) {
 			final Service service = serviceIterator.previous();
 			if ( Stoppable.class.isInstance( service ) ) {
 				try {
 					( (Stoppable) service ).stop();
 				}
 				catch ( Exception e ) {
                     LOG.unableToStopService(service.getClass(), e.toString());
 				}
 			}
 		}
 		serviceList.clear();
 		serviceList = null;
 		serviceBindingMap.clear();
 		serviceBindingMap = null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/BootstrapServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/BootstrapServiceRegistryImpl.java
index 9a3872aaf4..d5188d03bf 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/BootstrapServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BootstrapServiceRegistryImpl.java
@@ -1,169 +1,122 @@
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
 
 import java.util.LinkedHashSet;
 
 import org.hibernate.integrator.internal.IntegratorServiceImpl;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
 import org.hibernate.service.Service;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.spi.ServiceBinding;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * {@link ServiceRegistry} implementation containing specialized "bootstrap" services, specifically:<ul>
  * <li>{@link ClassLoaderService}</li>
  * <li>{@link IntegratorService}</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public class BootstrapServiceRegistryImpl implements ServiceRegistryImplementor, ServiceBinding.OwningRegistry {
 	private static final LinkedHashSet<Integrator> NO_INTEGRATORS = new LinkedHashSet<Integrator>();
 
 	private final ServiceBinding<ClassLoaderService> classLoaderServiceBinding;
 	private final ServiceBinding<IntegratorService> integratorServiceBinding;
 
-	public static Builder builder() {
-		return new Builder();
+	public static BootstrapServiceRegistryBuilder builder() {
+		return new BootstrapServiceRegistryBuilder();
 	}
 
 	public BootstrapServiceRegistryImpl() {
 		this( new ClassLoaderServiceImpl(), NO_INTEGRATORS );
 	}
 
 	public BootstrapServiceRegistryImpl(
 			ClassLoaderService classLoaderService,
 			IntegratorService integratorService) {
 		this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(
 				this,
 				ClassLoaderService.class,
 				classLoaderService
 		);
 
 		this.integratorServiceBinding = new ServiceBinding<IntegratorService>(
 				this,
 				IntegratorService.class,
 				integratorService
 		);
 	}
 
 
 	public BootstrapServiceRegistryImpl(
 			ClassLoaderService classLoaderService,
 			LinkedHashSet<Integrator> providedIntegrators) {
 		this( classLoaderService, new IntegratorServiceImpl( providedIntegrators, classLoaderService ) );
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
 		// the bootstrap registry should currently be made up of only directly built services.
 		throw new ServiceException( "Boot-strap registry should only contain directly built services" );
 	}
 
-	public static class Builder {
-		private final LinkedHashSet<Integrator> providedIntegrators = new LinkedHashSet<Integrator>();
-		private ClassLoader applicationClassLoader;
-		private ClassLoader resourcesClassLoader;
-		private ClassLoader hibernateClassLoader;
-		private ClassLoader environmentClassLoader;
-
-		public Builder with(Integrator integrator) {
-			providedIntegrators.add( integrator );
-			return this;
-		}
-
-		public Builder withApplicationClassLoader(ClassLoader classLoader) {
-			this.applicationClassLoader = classLoader;
-			return this;
-		}
-
-		public Builder withResourceClassLoader(ClassLoader classLoader) {
-			this.resourcesClassLoader = classLoader;
-			return this;
-		}
-
-		public Builder withHibernateClassLoader(ClassLoader classLoader) {
-			this.hibernateClassLoader = classLoader;
-			return this;
-		}
-
-		public Builder withEnvironmentClassLoader(ClassLoader classLoader) {
-			this.environmentClassLoader = classLoader;
-			return this;
-		}
-
-		public BootstrapServiceRegistryImpl build() {
-			final ClassLoaderServiceImpl classLoaderService = new ClassLoaderServiceImpl(
-					applicationClassLoader,
-					resourcesClassLoader,
-					hibernateClassLoader,
-					environmentClassLoader
-			);
-
-			final IntegratorServiceImpl integratorService = new IntegratorServiceImpl(
-					providedIntegrators,
-					classLoaderService
-			);
-
-			return new BootstrapServiceRegistryImpl( classLoaderService, integratorService );
-		}
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceProxy.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceProxy.java
deleted file mode 100644
index 23e5be6a6e..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceProxy.java
+++ /dev/null
@@ -1,38 +0,0 @@
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
-package org.hibernate.service.internal;
-
-import org.hibernate.service.Service;
-
-/**
- * Marker interface for a service proxy which allows mixed-in ability to unproxy.
- *
- * @author Steve Ebersole
- */
-public interface ServiceProxy extends Service {
-	/**
-	 * Get the target service instance represented by this proxy.
-	 */
-	public <T extends Service> T getTargetInstance();
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
index a58edfb79d..dc04d9f48c 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
@@ -1,60 +1,60 @@
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
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 
 /**
- * Acts as a {@link Service} in the {@link BasicServiceRegistryImpl} whose function is as a factory for
+ * Acts as a {@link Service} in the {@link StandardServiceRegistryImpl} whose function is as a factory for
  * {@link SessionFactoryServiceRegistryImpl} implementations.
  *
  * @author Steve Ebersole
  */
 public class SessionFactoryServiceRegistryFactoryImpl implements SessionFactoryServiceRegistryFactory {
 	private final ServiceRegistryImplementor theBasicServiceRegistry;
 
 	public SessionFactoryServiceRegistryFactoryImpl(ServiceRegistryImplementor theBasicServiceRegistry) {
 		this.theBasicServiceRegistry = theBasicServiceRegistry;
 	}
 
 	@Override
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration) {
 		return new SessionFactoryServiceRegistryImpl( theBasicServiceRegistry, sessionFactory, configuration );
 	}
 
 	@Override
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata) {
 		return new SessionFactoryServiceRegistryImpl( theBasicServiceRegistry, sessionFactory, metadata );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
index 759b3f2bb8..48243fd918 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
@@ -1,107 +1,106 @@
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
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.Service;
-import org.hibernate.service.StandardSessionFactoryServiceInitiators;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public class SessionFactoryServiceRegistryImpl extends AbstractServiceRegistryImpl implements SessionFactoryServiceRegistry  {
 
 	// for now we need to hold on to the Configuration... :(
 	private final Configuration configuration;
 	private final MetadataImplementor metadata;
 	private final SessionFactoryImplementor sessionFactory;
 
 	@SuppressWarnings( {"unchecked"})
 	public SessionFactoryServiceRegistryImpl(
 			ServiceRegistryImplementor parent,
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration) {
 		super( parent );
 
 		this.sessionFactory = sessionFactory;
 		this.configuration = configuration;
 		this.metadata = null;
 
 		// for now, just use the standard initiator list
 		for ( SessionFactoryServiceInitiator initiator : StandardSessionFactoryServiceInitiators.LIST ) {
 			// create the bindings up front to help identify to which registry services belong
 			createServiceBinding( initiator );
 		}
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public SessionFactoryServiceRegistryImpl(
 			ServiceRegistryImplementor parent,
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata) {
 		super( parent );
 
 		this.sessionFactory = sessionFactory;
 		this.configuration = null;
 		this.metadata = metadata;
 
 		// for now, just use the standard initiator list
 		for ( SessionFactoryServiceInitiator initiator : StandardSessionFactoryServiceInitiators.LIST ) {
 			// create the bindings up front to help identify to which registry services belong
 			createServiceBinding( initiator );
 		}
 	}
 
 	@Override
 	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
 		// todo : add check/error for unexpected initiator types?
 		SessionFactoryServiceInitiator<R> sessionFactoryServiceInitiator =
 				(SessionFactoryServiceInitiator<R>) serviceInitiator;
 		if ( metadata != null ) {
 			return sessionFactoryServiceInitiator.initiateService( sessionFactory, metadata, this );
 		}
 		else if ( configuration != null ) {
 			return sessionFactoryServiceInitiator.initiateService( sessionFactory, configuration, this );
 		}
 		else {
 			throw new IllegalStateException( "Both metadata and configuration are null." );
 		}
 	}
 
 	@Override
 	protected <T extends Service> void configureService(T service) {
 		applyInjections( service );
 
 		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
 			( (ServiceRegistryAwareService) service ).injectServices( this );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/StandardServiceRegistryImpl.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/StandardServiceRegistryImpl.java
index 4229772048..4bbdc32811 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/StandardServiceRegistryImpl.java
@@ -1,85 +1,85 @@
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
 
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.Service;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
- * Standard Hibernate implementation of the service registry.
+ * Hibernate implementation of the standard service registry.
  *
  * @author Steve Ebersole
  */
-public class BasicServiceRegistryImpl extends AbstractServiceRegistryImpl implements BasicServiceRegistry {
+public class StandardServiceRegistryImpl extends AbstractServiceRegistryImpl implements ServiceRegistry {
 	private final Map configurationValues;
 
 	@SuppressWarnings( {"unchecked"})
-	public BasicServiceRegistryImpl(
+	public StandardServiceRegistryImpl(
 			ServiceRegistryImplementor bootstrapServiceRegistry,
 			List<BasicServiceInitiator> serviceInitiators,
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardSessionFactoryServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/internal/StandardSessionFactoryServiceInitiators.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/service/StandardSessionFactoryServiceInitiators.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/StandardSessionFactoryServiceInitiators.java
index 00b06c2108..8a71318101 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardSessionFactoryServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/StandardSessionFactoryServiceInitiators.java
@@ -1,47 +1,51 @@
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
-package org.hibernate.service;
+package org.hibernate.service.internal;
 
 import java.util.ArrayList;
+import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.event.service.internal.EventListenerServiceInitiator;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.stat.internal.StatisticsInitiator;
 
 /**
+ * Central definition of the standard set of initiators defined by Hibernate for the
+ * {@link org.hibernate.service.spi.SessionFactoryServiceRegistry}
+ *
  * @author Steve Ebersole
  */
 public class StandardSessionFactoryServiceInitiators {
 	public static List<SessionFactoryServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<SessionFactoryServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<SessionFactoryServiceInitiator> serviceInitiators = new ArrayList<SessionFactoryServiceInitiator>();
 
 		serviceInitiators.add( EventListenerServiceInitiator.INSTANCE );
 		serviceInitiators.add( StatisticsInitiator.INSTANCE );
 
-		return serviceInitiators;
+		return Collections.unmodifiableList( serviceInitiators );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
index 2115ddee70..1235549ff6 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
@@ -1,45 +1,45 @@
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
 package org.hibernate.service.spi;
 
 import java.util.Map;
 
 import org.hibernate.service.Service;
 
 /**
- * Responsible for initiating services.
+ * Contract for an initiator of services that target the standard {@link org.hibernate.service.ServiceRegistry}
  *
  * @author Steve Ebersole
  */
 public interface BasicServiceInitiator<R extends Service> extends ServiceInitiator<R> {
 	/**
 	 * Initiates the managed service.
 	 *
 	 * @param configurationValues The configuration values in effect
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
 	public R initiateService(Map configurationValues, ServiceRegistryImplementor registry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
index 7fe42b8820..f08420c881 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
@@ -1,55 +1,54 @@
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
 package org.hibernate.service.spi;
 
-
 /**
  * Optional {@link org.hibernate.service.Service} contract for services which can be managed in JMX
  *
  * @author Steve Ebersole
  */
 public interface Manageable {
 	/**
 	 * Get the domain name to be used in registering the management bean.  May be {@code null} to indicate Hibernate's
 	 * default domain ({@code org.hibernate.core}) should be used.
 	 *
 	 * @return The management domain.
 	 */
 	public String getManagementDomain();
 
 	/**
 	 * Allows the service to specify a special 'serviceType' portion of the object name.  {@code null} indicates
 	 * we should use the default scheme, which is to use the name of the service impl class for this purpose.
 	 *
 	 * @return The custom 'serviceType' name.
 	 */
 	public String getManagementServiceType();
 
 	/**
 	 * The the management bean (MBean) for this service.
 	 *
 	 * @return The management bean.
 	 */
 	public Object getManagementBean();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java
index 13749b6e97..d34629d8bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java
@@ -1,83 +1,84 @@
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
 package org.hibernate.service.spi;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.service.Service;
 
 /**
  * Models a binding for a particular service
+ *
  * @author Steve Ebersole
  */
 public final class ServiceBinding<R extends Service> {
 	private static final Logger log = Logger.getLogger( ServiceBinding.class );
 
 	public static interface OwningRegistry {
 		public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator);
 	}
 
 	private final OwningRegistry serviceRegistry;
 	private final Class<R> serviceRole;
 	private final ServiceInitiator<R> serviceInitiator;
 	private R service;
 
 	public ServiceBinding(OwningRegistry serviceRegistry, Class<R> serviceRole, R service) {
 		this.serviceRegistry = serviceRegistry;
 		this.serviceRole = serviceRole;
 		this.serviceInitiator = null;
 		this.service = service;
 	}
 
 	public ServiceBinding(OwningRegistry serviceRegistry, ServiceInitiator<R> serviceInitiator) {
 		this.serviceRegistry = serviceRegistry;
 		this.serviceRole = serviceInitiator.getServiceInitiated();
 		this.serviceInitiator = serviceInitiator;
 	}
 
 	public OwningRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public Class<R> getServiceRole() {
 		return serviceRole;
 	}
 
 	public ServiceInitiator<R> getServiceInitiator() {
 		return serviceInitiator;
 	}
 
 	public R getService() {
 		return service;
 	}
 
 	public void setService(R service) {
 		if ( this.service != null ) {
 			if ( log.isDebugEnabled() ) {
 				log.debug( "Overriding existing service binding [" + serviceRole.getName() + "]" );
 			}
 		}
 		this.service = service;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
index 40107caf06..7149bc0f50 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
@@ -1,38 +1,40 @@
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
 package org.hibernate.service.spi;
 
 import org.hibernate.service.Service;
 
 /**
+ * Base contract for an initiator of a service.
+ *
  * @author Steve Ebersole
  */
 public interface ServiceInitiator<R extends Service> {
 	/**
 	 * Obtains the service role initiated by this initiator.  Should be unique within a registry
 	 *
 	 * @return The service role.
 	 */
 	public Class<R> getServiceInitiated();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryImplementor.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryImplementor.java
index e15d6153bb..d2a5e59046 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryImplementor.java
@@ -1,35 +1,49 @@
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
 package org.hibernate.service.spi;
 
 import org.hibernate.service.Service;
 import org.hibernate.service.ServiceRegistry;
 
 /**
+ * Additional integration contracts for a service registry.
+ *
  * @author Steve Ebersole
  */
 public interface ServiceRegistryImplementor extends ServiceRegistry {
+	/**
+	 * Locate the binding for the given role.  Should, generally speaking, look into parent registry if one.
+	 *
+	 * @param serviceRole The service role for which to locate a binding.
+	 * @param <R> generic return type.
+	 *
+	 * @return The located binding; may be {@code null}
+	 */
 	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole);
+
+	/**
+	 * Release resources
+	 */
 	public void destroy();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
index 59448c1cdf..9e169360aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
@@ -1,63 +1,66 @@
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
 package org.hibernate.service.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.Service;
 
 /**
+ * Contract for an initiator of services that target the specialized service registry
+ * {@link SessionFactoryServiceRegistry}
+ *
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceInitiator<R extends Service> extends ServiceInitiator<R>{
 	/**
 	 * Initiates the managed service.
 	 * <p/>
 	 * Note for implementors: signature is guaranteed to change once redesign of SessionFactory building is complete
 	 *
 	 * @param sessionFactory The session factory.  Note the the session factory is still in flux; care needs to be taken
 	 * in regards to what you call.
 	 * @param configuration The configuration.
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
 	public R initiateService(SessionFactoryImplementor sessionFactory, Configuration configuration, ServiceRegistryImplementor registry);
 
 	/**
 	 * Initiates the managed service.
 	 * <p/>
 	 * Note for implementors: signature is guaranteed to change once redesign of SessionFactory building is complete
 	 *
 	 * @param sessionFactory The session factory.  Note the the session factory is still in flux; care needs to be taken
 	 * in regards to what you call.
 	 * @param metadata The configuration.
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
 	public R initiateService(SessionFactoryImplementor sessionFactory, MetadataImplementor metadata, ServiceRegistryImplementor registry);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistry.java
index e2775874b6..1d893c1605 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistry.java
@@ -1,33 +1,33 @@
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
 package org.hibernate.service.spi;
 
 /**
- * Acts as a {@link org.hibernate.service.Service} in the {@link org.hibernate.service.internal.BasicServiceRegistryImpl} whose function is as a factory for
- * {@link org.hibernate.service.internal.SessionFactoryServiceRegistryImpl} implementations.
+ * Specialized {@link org.hibernate.service.ServiceRegistry} implementation that holds services which need access
+ * to the {@link org.hibernate.SessionFactory} during initialization.
  *
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceRegistry extends ServiceRegistryImplementor {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
index 33ae849622..dbf86a1ab4 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
@@ -1,70 +1,71 @@
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
 package org.hibernate.service.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
 
 /**
- * Contract for builder of {@link SessionFactoryServiceRegistry} instances.  Defined as a service to
- * "sit inside" the {@link org.hibernate.service.BasicServiceRegistry}.
+ * Contract for builder of {@link SessionFactoryServiceRegistry} instances.
+ * <p/>
+ * Is itself a service within the standard service registry.
  *
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceRegistryFactory extends Service {
 	/**
 	 * Create the registry.
 	 *
 	 * @todo : fully expect this signature to change!
 	 *
 	 * @param sessionFactory The (in flux) session factory.  Generally this is useful for grabbing a reference for later
 	 * 		use.  However, care should be taken when invoking on the session factory until after it has been fully
 	 * 		initialized.
 	 * @param configuration The configuration object.
 	 *
 	 * @return The registry
 	 */
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration);
 
 	/**
 	 * Create the registry.
 	 *
 	 * @todo : fully expect this signature to change!
 	 *
 	 * @param sessionFactory The (in flux) session factory.  Generally this is useful for grabbing a reference for later
 	 * 		use.  However, care should be taken when invoking on the session factory until after it has been fully
 	 * 		initialized.
 	 * @param metadata The configuration object.
 	 *
 	 * @return The registry
 	 */
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/Startable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Startable.java
index fdf3486248..379359bfd7 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/Startable.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/Startable.java
@@ -1,37 +1,37 @@
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
 package org.hibernate.service.spi;
 
 
 /**
- * Lifecyle contract for services which wish to be notified when it is time to start.
+ * Lifecycle contract for services which wish to be notified when it is time to start.
  *
  * @author Steve Ebersole
  */
 public interface Startable {
 	/**
 	 * Start phase notification
 	 */
 	public void start();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/Stoppable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Stoppable.java
index 81b37033dd..7f3b9a1d40 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/Stoppable.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/Stoppable.java
@@ -1,37 +1,36 @@
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
 package org.hibernate.service.spi;
 
-
 /**
- * Lifecyle contract for services which wish to be notified when it is time to stop.
+ * Lifecycle contract for services which wish to be notified when it is time to stop.
  *
  * @author Steve Ebersole
  */
 public interface Stoppable {
 	/**
 	 * Stop phase notification
 	 */
 	public void stop();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/Wrapped.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Wrapped.java
index 3458ccea10..fde1a84866 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/Wrapped.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/Wrapped.java
@@ -1,47 +1,51 @@
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
 package org.hibernate.service.spi;
 
 /**
+ * Optional contract for services that wrap stuff that to which it is useful to have access.  For example, a service
+ * that maintains a {@link javax.sql.DataSource} might want to expose access to the {@link javax.sql.DataSource} or
+ * its {@link java.sql.Connection} instances.
+ *
  * @author Steve Ebersole
  */
 public interface Wrapped {
 	/**
 	 * Can this wrapped service be unwrapped as the indicated type?
 	 *
 	 * @param unwrapType The type to check.
 	 *
 	 * @return True/false.
 	 */
 	public boolean isUnwrappableAs(Class unwrapType);
 
 	/**
 	 * Unproxy the service proxy
 	 *
 	 * @param unwrapType The java type as which to unwrap this instance.
 	 *
 	 * @return The unwrapped reference
 	 */
 	public <T> T unwrap(Class<T> unwrapType);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
index b40b1a7f8b..00391b6b16 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ManagedProviderConnectionHelper.java
@@ -1,107 +1,107 @@
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
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistryBuilder;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * A {@link ConnectionHelper} implementation based on an internally
  * built and managed {@link ConnectionProvider}.
  *
  * @author Steve Ebersole
  */
 class ManagedProviderConnectionHelper implements ConnectionHelper {
 	private Properties cfgProperties;
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 	private Connection connection;
 
 	public ManagedProviderConnectionHelper(Properties cfgProperties) {
 		this.cfgProperties = cfgProperties;
 	}
 
 	public void prepare(boolean needsAutoCommit) throws SQLException {
 		serviceRegistry = createServiceRegistry( cfgProperties );
 		connection = serviceRegistry.getService( ConnectionProvider.class ).getConnection();
 		if ( needsAutoCommit && ! connection.getAutoCommit() ) {
 			connection.commit();
 			connection.setAutoCommit( true );
 		}
 	}
 
-	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
+	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
 	}
 
 	public Connection getConnection() throws SQLException {
 		return connection;
 	}
 
 	public void release() throws SQLException {
 		try {
 			releaseConnection();
 		}
 		finally {
 			releaseServiceRegistry();
 		}
 	}
 
 	private void releaseConnection() throws SQLException {
 		if ( connection != null ) {
 			try {
 				new SqlExceptionHelper().logAndClearWarnings( connection );
 			}
 			finally {
 				try  {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				finally {
 					connection = null;
 				}
 			}
 		}
 	}
 
 	private void releaseServiceRegistry() {
 		if ( serviceRegistry != null ) {
 			try {
 				serviceRegistry.destroy();
 			}
 			finally {
 				serviceRegistry = null;
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index cd12c24417..a8e3a237ad 100644
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
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
 
-	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
+	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
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
 
-			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
+			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
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
index d2d0624087..c5f939698f 100644
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
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
 
-	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
+	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
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
 
-			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
+			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
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
index 666ed76ed2..3bd307173b 100755
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
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
 
-	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
+	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
+		return (StandardServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
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
 
-			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
+			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
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
index 47b5f60269..4bc453c10c 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
@@ -1,95 +1,95 @@
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
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
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
-		BasicServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
+		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
 				.buildServiceRegistry();
 		//no exception as the GoofyPersisterClassProvider is not set
 		SessionFactory sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 		sessionFactory.close();
 		ServiceRegistryBuilder.destroy( serviceRegistry );
 
 		serviceRegistry = new ServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
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
 		serviceRegistry = new ServiceRegistryBuilder()
 				.applySettings( cfg.getProperties() )
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
index 06b0468490..2f6170b24a 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/events/CallbackTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/events/CallbackTest.java
@@ -1,145 +1,145 @@
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
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
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
-	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 		super.prepareBootstrapRegistryBuilder( builder );
 		builder.with(
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
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/extendshbm/ExtendsTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/extendshbm/ExtendsTest.java
index 536df42047..e8f0bd688a 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/extendshbm/ExtendsTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/extendshbm/ExtendsTest.java
@@ -1,211 +1,211 @@
 //$Id: ExtendsTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 package org.hibernate.test.extendshbm;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class ExtendsTest extends BaseUnitTestCase {
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() {
 		ServiceRegistryBuilder.destroy( serviceRegistry );
 	}
 
 	private String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	@Test
 	public void testAllInOne() {
 		Configuration cfg = new Configuration();
 
 		cfg.addResource( getBaseForMappings() + "extendshbm/allinone.hbm.xml" );
 		cfg.buildMappings();
 		assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 		assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 		assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 	}
 
 	@Test
 	public void testOutOfOrder() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/Customer.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Person.hbm.xml" );
 			cfg.addResource( getBaseForMappings() + "extendshbm/Employee.hbm.xml" );
 
 			cfg.buildSessionFactory( serviceRegistry );
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 
 		}
 		catch ( HibernateException e ) {
 			fail( "should not fail with exception! " + e );
 		}
 
 	}
 
 	@Test
 	public void testNwaitingForSuper() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/Customer.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Employee.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Person.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 
 	}
 
 	@Test
 	public void testMissingSuper() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/Customer.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Employee.hbm.xml" );
 
 			cfg.buildSessionFactory( serviceRegistry );
 
 			fail( "Should not be able to build sessionFactory without a Person" );
 		}
 		catch ( HibernateException e ) {
 
 		}
 
 	}
 
 	@Test
 	public void testAllSeparateInOne() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/allseparateinone.hbm.xml" );
 
 			cfg.buildSessionFactory( serviceRegistry );
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 
 		}
 		catch ( HibernateException e ) {
 			fail( "should not fail with exception! " + e );
 		}
 
 	}
 
 	@Test
 	public void testJoinedSubclassAndEntityNamesOnly() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/entitynames.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "EntityHasName" ) );
 			assertNotNull( cfg.getClassMapping( "EntityCompany" ) );
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 	}
 
 	@Test
 	public void testEntityNamesWithPackage() {
 		Configuration cfg = new Configuration();
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/packageentitynames.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "EntityHasName" ) );
 			assertNotNull( cfg.getClassMapping( "EntityCompany" ) );
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 	}
 
 	@Test
 	public void testUnionSubclass() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/unionsubclass.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 	}
 
 }
 
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java b/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
index deafccc854..6923fee1c5 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
@@ -1,116 +1,116 @@
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
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
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
-	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 		super.prepareBootstrapRegistryBuilder( builder );
 		builder.with(
 				new Integrator() {
 
 					@Override
 					public void integrate(
 							Configuration configuration,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
 						integrate( serviceRegistry );
 					}
 
 					@Override
 					public void integrate(
 							MetadataImplementor metadata,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
 						integrate( serviceRegistry );
 					}
 
 					private void integrate(SessionFactoryServiceRegistry serviceRegistry) {
 						serviceRegistry.getService( EventListenerRegistry.class )
 								.getEventListenerGroup( EventType.PRE_UPDATE )
 								.appendListener( new InitializingPreUpdateEventListener() );
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
index 0b6b1794fb..2081788784 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/jpa/AbstractJPATest.java
@@ -1,192 +1,192 @@
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
 import org.hibernate.event.internal.DefaultAutoFlushEventListener;
 import org.hibernate.event.internal.DefaultFlushEntityEventListener;
 import org.hibernate.event.internal.DefaultFlushEventListener;
 import org.hibernate.event.internal.DefaultPersistEventListener;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.event.spi.FlushEventListener;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
-import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
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
-	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 		builder.with(
 				new Integrator() {
 
 					@Override
 					public void integrate(
 							Configuration configuration,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
 						integrate( serviceRegistry );
 					}
 
 					@Override
 					public void integrate(
 							MetadataImplementor metadata,
 							SessionFactoryImplementor sessionFactory,
 							SessionFactoryServiceRegistry serviceRegistry) {
 						integrate( serviceRegistry );
 					}
 
 					private void integrate(SessionFactoryServiceRegistry serviceRegistry) {
 						EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 						eventListenerRegistry.setListeners( EventType.PERSIST, buildPersistEventListeners() );
 						eventListenerRegistry.setListeners(
 								EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners()
 						);
 						eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, buildAutoFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH, buildFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, buildFlushEntityEventListeners() );
 					}
 
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
index 2b865d0915..f4cf68d397 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
@@ -1,211 +1,211 @@
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
 import org.hibernate.event.internal.DefaultLoadEventListener;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
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
-	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 		super.prepareBootstrapRegistryBuilder( builder );
 		builder.with(
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index 5ebcfd09c6..2ac46f5800 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,207 +1,207 @@
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
 package org.hibernate.metamodel.binding;
 
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Set;
 
-import org.junit.After;
-import org.junit.Assert;
-import org.junit.Before;
-import org.junit.Test;
-
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.type.LongType;
 import org.hibernate.type.StringType;
 
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Basic tests of {@code hbm.xml} and annotation binding code
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractBasicBindingTests extends BaseUnitTestCase {
 
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
-	protected BasicServiceRegistry basicServiceRegistry() {
+	protected ServiceRegistry basicServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Test
 	public void testSimpleEntityMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForSimpleEntityBinding( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertRoot( metadata, entityBinding );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding() );
 	}
 
 	@Test
 	public void testSimpleVersionedEntityMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForSimpleVersionedEntityBinding( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleVersionedEntity.class.getName() );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNotNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding() );
 		assertNotNull( entityBinding.getHierarchyDetails().getVersioningAttributeBinding().getAttribute() );
 	}
 
 	@Test
 	public void testEntityWithManyToOneMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForSimpleEntityBinding( sources );
 		addSourcesForManyToOne( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding simpleEntityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertIdAndSimpleProperty( simpleEntityBinding );
 
 		Set<SingularAssociationAttributeBinding> referenceBindings = simpleEntityBinding.locateAttributeBinding( "id" )
 				.getEntityReferencingAttributeBindings();
 		assertEquals( "There should be only one reference binding", 1, referenceBindings.size() );
 
 		SingularAssociationAttributeBinding referenceBinding = referenceBindings.iterator().next();
 		EntityBinding referencedEntityBinding = referenceBinding.getReferencedEntityBinding();
 		// TODO - Is this assertion correct (HF)?
 		assertEquals( "Should be the same entity binding", referencedEntityBinding, simpleEntityBinding );
 
 		EntityBinding entityWithManyToOneBinding = metadata.getEntityBinding( ManyToOneEntity.class.getName() );
 		Iterator<SingularAssociationAttributeBinding> it = entityWithManyToOneBinding.getEntityReferencingAttributeBindings()
 				.iterator();
 		assertTrue( it.hasNext() );
 		assertSame( entityWithManyToOneBinding.locateAttributeBinding( "simpleEntity" ), it.next() );
 		assertFalse( it.hasNext() );
 	}
 
 	@Test
 	public void testSimpleEntityWithSimpleComponentMapping() {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 		addSourcesForComponentBinding( sources );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntityWithSimpleComponent.class.getName() );
 		assertRoot( metadata, entityBinding );
 		assertIdAndSimpleProperty( entityBinding );
 
 		ComponentAttributeBinding componentAttributeBinding = (ComponentAttributeBinding) entityBinding.locateAttributeBinding( "simpleComponent" );
 		assertNotNull( componentAttributeBinding );
 		assertSame( componentAttributeBinding.getAttribute().getSingularAttributeType(), componentAttributeBinding.getAttributeContainer() );
 		assertEquals( SimpleEntityWithSimpleComponent.class.getName() + ".simpleComponent", componentAttributeBinding.getPathBase() );
 		assertSame( entityBinding, componentAttributeBinding.seekEntityBinding() );
 		assertNotNull( componentAttributeBinding.getComponent() );
 	}
 
 	public abstract void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
 
 	public abstract void addSourcesForSimpleEntityBinding(MetadataSources sources);
 
 	public abstract void addSourcesForManyToOne(MetadataSources sources);
 
 	public abstract void addSourcesForComponentBinding(MetadataSources sources);
 
 	protected void assertIdAndSimpleProperty(EntityBinding entityBinding) {
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getHierarchyDetails().getEntityIdentifier() );
 		assertNotNull( entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 
 		AttributeBinding idAttributeBinding = entityBinding.locateAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 		assertTrue( idAttributeBinding.getAttribute().isSingular() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		SingularAttributeBinding singularIdAttributeBinding = (SingularAttributeBinding) idAttributeBinding;
 		assertFalse( singularIdAttributeBinding.isNullable() );
 		SingularAttribute singularIdAttribute =  ( SingularAttribute ) idAttributeBinding.getAttribute();
 		BasicType basicIdAttributeType = ( BasicType ) singularIdAttribute.getSingularAttributeType();
 		assertSame( Long.class, basicIdAttributeType.getClassReference() );
 
 		assertNotNull( singularIdAttributeBinding.getValue() );
 		assertTrue( singularIdAttributeBinding.getValue() instanceof Column );
 		Datatype idDataType = ( (Column) singularIdAttributeBinding.getValue() ).getDatatype();
 		assertSame( Long.class, idDataType.getJavaType() );
 		assertSame( Types.BIGINT, idDataType.getTypeCode() );
 		assertSame( LongType.INSTANCE.getName(), idDataType.getTypeName() );
 
 		assertNotNull( entityBinding.locateAttributeBinding( "name" ) );
 		assertNotNull( entityBinding.locateAttributeBinding( "name" ).getAttribute() );
 		assertTrue( entityBinding.locateAttributeBinding( "name" ).getAttribute().isSingular() );
 
 		SingularAttributeBinding nameBinding = (SingularAttributeBinding) entityBinding.locateAttributeBinding( "name" );
 		assertTrue( nameBinding.isNullable() );
 		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 		SingularAttribute singularNameAttribute =  ( SingularAttribute ) nameBinding.getAttribute();
 		BasicType basicNameAttributeType = ( BasicType ) singularNameAttribute.getSingularAttributeType();
 		assertSame( String.class, basicNameAttributeType.getClassReference() );
 
 		assertNotNull( nameBinding.getValue() );
 		SimpleValue nameValue = (SimpleValue) nameBinding.getValue();
 		assertTrue( nameValue instanceof Column );
 		Datatype nameDataType = nameValue.getDatatype();
 		assertSame( String.class, nameDataType.getJavaType() );
 		assertSame( Types.VARCHAR, nameDataType.getTypeCode() );
 		assertSame( StringType.INSTANCE.getName(), nameDataType.getTypeName() );
 	}
 
 	protected void assertRoot(MetadataImplementor metadata, EntityBinding entityBinding) {
 		assertTrue( entityBinding.isRoot() );
 		assertSame( entityBinding, metadata.getRootEntityBinding( entityBinding.getEntity().getName() ) );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
index 6f7bf39643..09d5af3857 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicCollectionBindingTests.java
@@ -1,91 +1,91 @@
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
 package org.hibernate.metamodel.binding;
 
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicCollectionBindingTests extends BaseUnitTestCase {
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 //	@Test
 //	public void testAnnotations() {
 //		doTest( MetadataSourceProcessingOrder.ANNOTATIONS_FIRST );
 //	}
 
 	@Test
 	public void testHbm() {
 		doTest( MetadataSourceProcessingOrder.HBM_FIRST );
 	}
 
 	private void doTest(MetadataSourceProcessingOrder processingOrder) {
 		MetadataSources sources = new MetadataSources( serviceRegistry );
 //		sources.addAnnotatedClass( EntityWithBasicCollections.class );
 		sources.addResource( "org/hibernate/metamodel/binding/EntityWithBasicCollections.hbm.xml" );
 		MetadataImpl metadata = (MetadataImpl) sources.getMetadataBuilder().with( processingOrder ).buildMetadata();
 
 		final EntityBinding entityBinding = metadata.getEntityBinding( EntityWithBasicCollections.class.getName() );
 		assertNotNull( entityBinding );
 
 		PluralAttributeBinding bagBinding = metadata.getCollection( EntityWithBasicCollections.class.getName() + ".theBag" );
 		assertNotNull( bagBinding );
 		assertSame( bagBinding, entityBinding.locateAttributeBinding( "theBag" ) );
 		assertNotNull( bagBinding.getCollectionTable() );
 		assertEquals( CollectionElementNature.BASIC, bagBinding.getCollectionElement().getCollectionElementNature() );
 		assertEquals( String.class.getName(), ( (BasicCollectionElement) bagBinding.getCollectionElement() ).getHibernateTypeDescriptor().getJavaTypeName() );
 
 		PluralAttributeBinding setBinding = metadata.getCollection( EntityWithBasicCollections.class.getName() + ".theSet" );
 		assertNotNull( setBinding );
 		assertSame( setBinding, entityBinding.locateAttributeBinding( "theSet" ) );
 		assertNotNull( setBinding.getCollectionTable() );
 		assertEquals( CollectionElementNature.BASIC, setBinding.getCollectionElement().getCollectionElementNature() );
 		assertEquals( String.class.getName(), ( (BasicCollectionElement) setBinding.getCollectionElement() ).getHibernateTypeDescriptor().getJavaTypeName() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
index cb099d15d9..344f7da7a2 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
@@ -1,146 +1,146 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.Iterator;
 
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContextImpl;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.fail;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class FetchProfileBinderTest extends BaseUnitTestCase {
 
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testSingleFetchProfile() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.JOIN)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertEquals( "Wrong fetch profile name", "foo", profile.getName() );
 		org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 		assertEquals( "Wrong association name", "bar", fetch.getAssociation() );
 		assertEquals( "Wrong association type", Foo.class.getName(), fetch.getEntity() );
 	}
 
 	@Test
 	public void testFetchProfiles() {
 		Index index = JandexHelper.indexForClass( service, FooBar.class );
 		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 
 		assertTrue( mappedFetchProfiles.hasNext() );
 		profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 	}
 
 	private void assertProfiles(org.hibernate.metamodel.binding.FetchProfile profile) {
 		if ( profile.getName().equals( "foobar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "foobar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else if ( profile.getName().equals( "fubar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "fubar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else {
 			fail( "Wrong fetch name:" + profile.getName() );
 		}
 	}
 
 	@Test(expected = MappingException.class)
 	public void testNonJoinFetchThrowsException() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.SELECT)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 	}
 
 	@FetchProfiles( {
 			@FetchProfile(name = "foobar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "foobar", mode = FetchMode.JOIN)
 			}),
 			@FetchProfile(name = "fubar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "fubar", mode = FetchMode.JOIN)
 			})
 	})
 	class FooBar {
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
index f30d389e11..50e8a80b04 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/QueryBinderTest.java
@@ -1,98 +1,98 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import javax.persistence.NamedNativeQuery;
 
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContextImpl;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class QueryBinderTest extends BaseUnitTestCase {
 
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test(expected = NotYetImplementedException.class)
 	public void testNoResultClass() {
 		@NamedNativeQuery(name = "fubar", query = "SELECT * FROM FOO")
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 		QueryBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 	}
 
 	@Test
 	public void testResultClass() {
 		@NamedNativeQuery(name = "fubar", query = "SELECT * FROM FOO", resultClass = Foo.class)
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 		QueryBinder.bind( new AnnotationBindingContextImpl( meta, index ) );
 
 		NamedSQLQueryDefinition namedQuery = meta.getNamedNativeQuery( "fubar" );
 		assertNotNull( namedQuery );
 		NativeSQLQueryReturn queryReturns[] = namedQuery.getQueryReturns();
 		assertTrue( "Wrong number of returns", queryReturns.length == 1 );
 		assertTrue( "Wrong query return type", queryReturns[0] instanceof NativeSQLQueryRootReturn );
 		NativeSQLQueryRootReturn rootReturn = (NativeSQLQueryRootReturn) queryReturns[0];
 		assertEquals( "Wrong result class", Foo.class.getName(), rootReturn.getReturnEntityName() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java
index d6f32001a6..ec326a806a 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/JandexHelperTest.java
@@ -1,249 +1,249 @@
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
 package org.hibernate.metamodel.source.annotations.util;
 
 import java.util.List;
 import java.util.Map;
 import javax.persistence.AttributeOverride;
 import javax.persistence.Basic;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.LockModeType;
 import javax.persistence.NamedQuery;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.NamedNativeQuery;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertTrue;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;
 
 /**
  * Tests for the helper class {@link JandexHelper}.
  *
  * @author Hardy Ferentschik
  */
 public class JandexHelperTest extends BaseUnitTestCase {
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService classLoaderService;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testGetMemberAnnotations() {
 		class Foo {
 			@Column
 			@Basic
 			private String bar;
 			private String fubar;
 		}
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 
 		ClassInfo classInfo = index.getClassByName( DotName.createSimple( Foo.class.getName() ) );
 		Map<DotName, List<AnnotationInstance>> memberAnnotations = JandexHelper.getMemberAnnotations(
 				classInfo, "bar"
 		);
 		assertTrue(
 				"property bar should defines @Column annotation",
 				memberAnnotations.containsKey( DotName.createSimple( Column.class.getName() ) )
 		);
 		assertTrue(
 				"property bar should defines @Basic annotation",
 				memberAnnotations.containsKey( DotName.createSimple( Basic.class.getName() ) )
 		);
 
 		memberAnnotations = JandexHelper.getMemberAnnotations( classInfo, "fubar" );
 		assertTrue( "there should be no annotations in fubar", memberAnnotations.isEmpty() );
 	}
 
 	@Test
 	public void testGettingNestedAnnotation() {
 		@AttributeOverride(name = "foo", column = @Column(name = "FOO"))
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.ATTRIBUTE_OVERRIDE );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		// try to retrieve the name
 		String name = JandexHelper.getValue( annotationInstance, "name", String.class );
 		assertEquals( "Wrong nested annotation", "foo", name );
 
 		// try to retrieve the nested column annotation instance
 		AnnotationInstance columnAnnotationInstance = JandexHelper.getValue(
 				annotationInstance,
 				"column",
 				AnnotationInstance.class
 		);
 		assertNotNull( columnAnnotationInstance );
 		assertEquals(
 				"Wrong nested annotation",
 				"javax.persistence.Column",
 				columnAnnotationInstance.name().toString()
 		);
 	}
 
 	@Test(expected = AssertionFailure.class)
 	public void testTryingToRetrieveWrongType() {
 		@AttributeOverride(name = "foo", column = @Column(name = "FOO"))
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.ATTRIBUTE_OVERRIDE );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		JandexHelper.getValue( annotationInstance, "name", Float.class );
 	}
 
 	@Test
 	public void testRetrieveDefaultEnumElement() {
 		@NamedQuery(name = "foo", query = "fubar")
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.NAMED_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		LockModeType lockMode = JandexHelper.getEnumValue( annotationInstance, "lockMode", LockModeType.class );
 		assertEquals( "Wrong lock mode", LockModeType.NONE, lockMode );
 	}
 
 	@Test
 	public void testRetrieveExplicitEnumElement() {
 		@NamedQuery(name = "foo", query = "bar", lockMode = LockModeType.OPTIMISTIC)
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.NAMED_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		LockModeType lockMode = JandexHelper.getEnumValue( annotationInstance, "lockMode", LockModeType.class );
 		assertEquals( "Wrong lock mode", LockModeType.OPTIMISTIC, lockMode );
 	}
 
 	@Test
 	public void testRetrieveStringArray() {
 		class Foo {
 			@org.hibernate.annotations.Index(name = "index", columnNames = { "a", "b", "c" })
 			private String foo;
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( HibernateDotNames.INDEX );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		String[] columnNames = JandexHelper.getValue( annotationInstance, "columnNames", String[].class );
 		Assert.assertTrue( columnNames.length == 3 );
 	}
 
 	@Test(expected = AssertionFailure.class)
 	public void testRetrieveClassParameterAsClass() {
 		@NamedNativeQuery(name = "foo", query = "bar", resultClass = Foo.class)
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		JandexHelper.getValue( annotationInstance, "resultClass", Class.class );
 	}
 
 	@Test
 	public void testRetrieveClassParameterAsString() {
 		@NamedNativeQuery(name = "foo", query = "bar", resultClass = Foo.class)
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		String fqcn = JandexHelper.getValue( annotationInstance, "resultClass", String.class );
 		assertEquals( "Wrong class names", Foo.class.getName(), fqcn );
 	}
 
 	@Test
 	public void testRetrieveUnknownParameter() {
 		@Entity
 		class Foo {
 		}
 
 		Index index = JandexHelper.indexForClass( classLoaderService, Foo.class );
 		List<AnnotationInstance> annotationInstances = index.getAnnotations( JPADotNames.ENTITY );
 		assertTrue( annotationInstances.size() == 1 );
 		AnnotationInstance annotationInstance = annotationInstances.get( 0 );
 
 		try {
 			JandexHelper.getValue( annotationInstance, "foo", String.class );
 			fail();
 		}
 		catch ( AssertionFailure e ) {
 			assertTrue(
 					e.getMessage()
 							.startsWith( "The annotation javax.persistence.Entity does not define a parameter 'foo'" )
 			);
 		}
 	}
 
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
index 4146da5e5e..aeb9201f43 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
@@ -1,221 +1,221 @@
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
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
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
index d2f55507a8..5a3b1cb868 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
@@ -1,104 +1,104 @@
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
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
-		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		StandardServiceRegistryImpl serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
 				.buildServiceRegistry();
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
 
-		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		StandardServiceRegistryImpl serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( props )
 				.buildServiceRegistry();
 
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertTrue( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithServiceOverride() {
-		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		StandardServiceRegistryImpl serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
 				.buildServiceRegistry();
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.setProperty( Environment.DIALECT, H2Dialect.class.getName() );
 
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( props )
 				.addService( ConnectionProvider.class, new UserSuppliedConnectionProviderImpl() )
 				.buildServiceRegistry();
 		jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( UserSuppliedConnectionProviderImpl.class ) );
 
 		serviceRegistry.destroy();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
index 7490ed38e3..af39ade87c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
@@ -1,142 +1,142 @@
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
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
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( ConnectionProviderBuilder.getConnectionProviderProperties() )
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
index 23100122b7..ad21b32e4c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
@@ -1,166 +1,164 @@
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
 
-import org.junit.After;
-import org.junit.Before;
-import org.junit.Test;
-
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.service.ServiceRegistryBuilder;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.internal.ServiceProxy;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
-import org.hibernate.test.common.JournalingTransactionObserver;
-import org.hibernate.test.common.TransactionContextImpl;
-import org.hibernate.test.common.TransactionEnvironmentImpl;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.hibernate.test.common.JournalingTransactionObserver;
+import org.hibernate.test.common.TransactionContextImpl;
+import org.hibernate.test.common.TransactionEnvironmentImpl;
 
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
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName() );
 		TestingJtaBootstrap.prepare( configValues );
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( configValues )
 				.buildServiceRegistry();
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
-				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
-				instance.retrieveTransactionManager().rollback();
+				serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
-				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
-				instance.retrieveTransactionManager().rollback();
+				serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager().rollback();
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
index e0a38ec07c..c983b85fd2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
@@ -1,178 +1,178 @@
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
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
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		TestingJtaBootstrap.prepare( configValues );
 		configValues.put( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName() );
 
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( configValues )
 				.buildServiceRegistry();
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
index 86ae02486f..37fc966c82 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1633 +1,1634 @@
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
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
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
 @SuppressWarnings( {"JavaDoc"})
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
 	public EntityManagerFactory createEntityManagerFactory() {
 		configure( cfg.getProperties(), new HashMap() );
 		return buildEntityManagerFactory();
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory() {
 		return buildEntityManagerFactory( BootstrapServiceRegistryImpl.builder() );
 	}
 
-	public EntityManagerFactory buildEntityManagerFactory(BootstrapServiceRegistryImpl.Builder builder) {
+	public EntityManagerFactory buildEntityManagerFactory(BootstrapServiceRegistryBuilder builder) {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 
 		if ( overridenClassLoader != null ) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 
 		try {
 			final ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder(
 					builder.with( new JpaIntegrator() ).build()
 			);
 			serviceRegistryBuilder.applySettings( cfg.getProperties() );
 			configure( (Properties) null, null );
 			NamingHelper.bind( this );
 			return new EntityManagerFactoryImpl(
 					transactionType,
 					discardOnClose,
 					getSessionInterceptorClass( cfg.getProperties() ),
 					cfg,
 					serviceRegistryBuilder.buildServiceRegistry()
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
index 5bb70b01c2..d6e482ae1a 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
@@ -1,286 +1,287 @@
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
 import org.hibernate.ejb.EntityManagerFactoryImpl;
 import org.hibernate.internal.SessionFactoryImpl;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
 import org.hibernate.service.ServiceRegistryBuilder;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 
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
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 	private EntityManagerFactoryImpl entityManagerFactory;
 
 	private EntityManager em;
 	private ArrayList<EntityManager> isolatedEms = new ArrayList<EntityManager>();
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	protected EntityManagerFactory entityManagerFactory() {
 		return entityManagerFactory;
 	}
 
-	protected BasicServiceRegistryImpl serviceRegistry() {
+	protected StandardServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Before
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void buildEntityManagerFactory() throws Exception {
 		log.trace( "Building session factory" );
 		ejb3Configuration = buildConfiguration();
 		ejb3Configuration.configure( getConfig() );
 		afterConfigurationBuilt( ejb3Configuration );
 
 		entityManagerFactory = (EntityManagerFactoryImpl) ejb3Configuration.buildEntityManagerFactory( bootstrapRegistryBuilder() );
-		serviceRegistry = (BasicServiceRegistryImpl) ( (SessionFactoryImpl) entityManagerFactory.getSessionFactory() ).getServiceRegistry().getParentServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) ( (SessionFactoryImpl) entityManagerFactory.getSessionFactory() ).getServiceRegistry().getParentServiceRegistry();
 
 		afterEntityManagerFactoryBuilt();
 	}
 
-	private BootstrapServiceRegistryImpl.Builder bootstrapRegistryBuilder() {
+	private BootstrapServiceRegistryBuilder bootstrapRegistryBuilder() {
 		return BootstrapServiceRegistryImpl.builder();
 	}
 
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
 
 	@SuppressWarnings( {"UnusedParameters"})
 	protected void applyServices(ServiceRegistryBuilder registryBuilder) {
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
index 318b0e3c1d..fd58d4cef1 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
@@ -1,139 +1,140 @@
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
 
 import javax.persistence.EntityManager;
 import java.io.IOException;
 import java.util.Properties;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.ejb.EntityManagerFactoryImpl;
 import org.hibernate.envers.AuditReader;
 import org.hibernate.envers.AuditReaderFactory;
 import org.hibernate.envers.event.EnversIntegrator;
 import org.hibernate.internal.SessionFactoryImpl;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 
 import org.junit.Before;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public abstract class AbstractEntityTest extends AbstractEnversTest {
     private EntityManagerFactoryImpl emf;
     private EntityManager entityManager;
     private AuditReader auditReader;
     private Ejb3Configuration cfg;
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
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
 
         emf = (EntityManagerFactoryImpl) cfg.buildEntityManagerFactory( createBootstrapRegistryBuilder() );
 
-		serviceRegistry = (BasicServiceRegistryImpl) ( (SessionFactoryImpl) emf.getSessionFactory() ).getServiceRegistry().getParentServiceRegistry();
+		serviceRegistry = (StandardServiceRegistryImpl) ( (SessionFactoryImpl) emf.getSessionFactory() ).getServiceRegistry().getParentServiceRegistry();
 
         newEntityManager();
     }
 
-	private BootstrapServiceRegistryImpl.Builder createBootstrapRegistryBuilder() {
+	private BootstrapServiceRegistryBuilder createBootstrapRegistryBuilder() {
 		return BootstrapServiceRegistryImpl.builder();
 	}
 
 
 	@AfterClassOnce
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
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
index 98544a28ff..7621e548bb 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
@@ -1,141 +1,141 @@
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Defines the environment for a node.
  *
  * @author Steve Ebersole
  */
 public class NodeEnvironment {
 	private final Configuration configuration;
 
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 	private InfinispanRegionFactory regionFactory;
 
 	private Map<String,EntityRegionImpl> entityRegionMap;
 	private Map<String,CollectionRegionImpl> collectionRegionMap;
 
 	public NodeEnvironment(Configuration configuration) {
 		this.configuration = configuration;
 	}
 
 	public Configuration getConfiguration() {
 		return configuration;
 	}
 
-	public BasicServiceRegistryImpl getServiceRegistry() {
+	public StandardServiceRegistryImpl getServiceRegistry() {
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
-		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder()
+		serviceRegistry = (StandardServiceRegistryImpl) new ServiceRegistryBuilder()
 				.applySettings( configuration.getProperties() )
 				.buildServiceRegistry();
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
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
index ab4c5e4208..e7e302681a 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
@@ -1,191 +1,191 @@
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class DualNodeTestCase extends BaseCoreFunctionalTestCase {
 	private static final Log log = LogFactory.getLog( DualNodeTestCase.class );
 
 	public static final String NODE_ID_PROP = "hibernate.test.cluster.node.id";
 	public static final String NODE_ID_FIELD = "nodeId";
 	public static final String LOCAL = "local";
 	public static final String REMOTE = "remote";
 
 	private SecondNodeEnvironment secondNodeEnvironment;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"cache/infinispan/functional/Contact.hbm.xml", "cache/infinispan/functional/Customer.hbm.xml"
 		};
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		return "transactional";
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		standardConfigure( cfg );
 		cfg.setProperty( NODE_ID_PROP, LOCAL );
 		cfg.setProperty( NODE_ID_FIELD, LOCAL );
 	}
 
 	@Override
 	protected void cleanupTest() throws Exception {
 		cleanupTransactionManagement();
 	}
 
 	protected void cleanupTransactionManagement() {
 		DualNodeJtaTransactionManagerImpl.cleanupTransactions();
 		DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
 	}
 
 	@Before
 	public void prepare() throws Exception {
 		secondNodeEnvironment = new SecondNodeEnvironment();
 	}
 
 	@After
 	public void unPrepare() {
 		if ( secondNodeEnvironment != null ) {
 			secondNodeEnvironment.shutDown();
 		}
 	}
 
 	protected SecondNodeEnvironment secondNodeEnvironment() {
 		return secondNodeEnvironment;
 	}
 
 	protected Class getCacheRegionFactory() {
 		return ClusterAwareRegionFactory.class;
 	}
 
 	protected Class getConnectionProviderClass() {
 		return DualNodeConnectionProviderImpl.class;
 	}
 
 	protected Class getJtaPlatformClass() {
 		return DualNodeJtaPlatformImpl.class;
 	}
 
 	protected Class getTransactionFactoryClass() {
 		return CMTTransactionFactory.class;
 	}
 
 	protected void sleep(long ms) {
 		try {
 			Thread.sleep( ms );
 		}
 		catch (InterruptedException e) {
 			log.warn( "Interrupted during sleep", e );
 		}
 	}
 
 	protected boolean getUseQueryCache() {
 		return true;
 	}
 
 	protected void configureSecondNode(Configuration cfg) {
 
 	}
 
 	protected void standardConfigure(Configuration cfg) {
 		super.configure( cfg );
 
 		cfg.setProperty( Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName() );
 		cfg.setProperty( AvailableSettings.JTA_PLATFORM, getJtaPlatformClass().getName() );
 		cfg.setProperty( Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName() );
 		cfg.setProperty( Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName() );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, String.valueOf( getUseQueryCache() ) );
 	}
 
 	public class SecondNodeEnvironment {
 		private Configuration configuration;
-		private BasicServiceRegistryImpl serviceRegistry;
+		private StandardServiceRegistryImpl serviceRegistry;
 		private SessionFactoryImplementor sessionFactory;
 
 		public SecondNodeEnvironment() {
 			configuration = constructConfiguration();
 			standardConfigure( configuration );
 			configuration.setProperty( NODE_ID_PROP, REMOTE );
 			configuration.setProperty( NODE_ID_FIELD, REMOTE );
 			configureSecondNode( configuration );
 			addMappings(configuration);
 			configuration.buildMappings();
 			applyCacheSettings( configuration );
 			afterConfigurationBuilt( configuration );
 			serviceRegistry = buildServiceRegistry( configuration );
 			sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		}
 
 		public Configuration getConfiguration() {
 			return configuration;
 		}
 
-		public BasicServiceRegistryImpl getServiceRegistry() {
+		public StandardServiceRegistryImpl getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		public SessionFactoryImplementor getSessionFactory() {
 			return sessionFactory;
 		}
 
 		public void shutDown() {
 			if ( sessionFactory != null ) {
 				try {
 					sessionFactory.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 			if ( serviceRegistry != null ) {
 				try {
 					serviceRegistry.destroy();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
index 96d9626575..902d14ce91 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
@@ -1,49 +1,49 @@
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
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceRegistryBuilder {
-	public static BasicServiceRegistryImpl buildServiceRegistry() {
+	public static StandardServiceRegistryImpl buildServiceRegistry() {
 		return buildServiceRegistry( Environment.getProperties() );
 	}
 
-	public static BasicServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
-		return (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder()
+	public static StandardServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
+		return (StandardServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder()
 				.applySettings( serviceRegistryConfig )
 				.buildServiceRegistry();
 	}
 
 	public static void destroy(ServiceRegistry serviceRegistry) {
-		( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
+		( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index 60a356ad4e..3b1a6933e3 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,504 +1,505 @@
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
-import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.BootstrapServiceRegistryBuilder;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.config.spi.ConfigurationService;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
+import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
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
-	private BasicServiceRegistryImpl serviceRegistry;
+	private StandardServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	private Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
-	protected BasicServiceRegistryImpl serviceRegistry() {
+	protected StandardServiceRegistryImpl serviceRegistry() {
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
 
-	private MetadataImplementor buildMetadata(BasicServiceRegistry serviceRegistry) {
+	private MetadataImplementor buildMetadata(ServiceRegistry serviceRegistry) {
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
 
-	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
+	protected StandardServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 
 		final BootstrapServiceRegistryImpl bootstrapServiceRegistry = generateBootstrapRegistry( properties );
 		ServiceRegistryBuilder registryBuilder = new ServiceRegistryBuilder( bootstrapServiceRegistry )
 				.applySettings( properties );
 		prepareBasicRegistryBuilder( registryBuilder );
-		return (BasicServiceRegistryImpl) registryBuilder.buildServiceRegistry();
+		return (StandardServiceRegistryImpl) registryBuilder.buildServiceRegistry();
 	}
 
 	protected BootstrapServiceRegistryImpl generateBootstrapRegistry(Properties properties) {
-		final BootstrapServiceRegistryImpl.Builder builder = BootstrapServiceRegistryImpl.builder();
+		final BootstrapServiceRegistryBuilder builder = BootstrapServiceRegistryImpl.builder();
 		prepareBootstrapRegistryBuilder( builder );
 		return builder.build();
 	}
 
-	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryImpl.Builder builder) {
+	protected void prepareBootstrapRegistryBuilder(BootstrapServiceRegistryBuilder builder) {
 	}
 
 	protected void prepareBasicRegistryBuilder(ServiceRegistryBuilder serviceRegistryBuilder) {
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
