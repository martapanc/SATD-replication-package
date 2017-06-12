diff --git a/documentation/src/main/asciidoc/userguide/appendices/Configurations.adoc b/documentation/src/main/asciidoc/userguide/appendices/Configurations.adoc
index ae8a0e9df8..806e849583 100644
--- a/documentation/src/main/asciidoc/userguide/appendices/Configurations.adoc
+++ b/documentation/src/main/asciidoc/userguide/appendices/Configurations.adoc
@@ -1,900 +1,906 @@
 [[configurations]]
 == Configurations
 
 [[configurations-strategy]]
 === Strategy configurations
 
 Many configuration settings define pluggable strategies that Hibernate uses for various purposes.
 The configuration of many of these strategy type settings accept definition in various forms.
 The documentation of such configuration settings refer here.
 The types of forms available in such cases include:
 
 short name (if defined)::
   Certain built-in strategy implementations have a corresponding short name.
 strategy instance::
   An instance of the strategy implementation to use can be specified
 strategy Class reference::
   A `java.lang.Class` reference of the strategy implementation to use
 strategy Class name::
   The class name (`java.lang.String`) of the strategy implementation to use
 
 [[configurations-general]]
 === General Configuration
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.dialect` | `org.hibernate.dialect.
 PostgreSQL94Dialect` |
 The classname of a Hibernate https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/dialect/Dialect.html[`Dialect`] from which Hibernate can generate SQL optimized for a particular relational database.
 
 In most cases Hibernate can choose the correct https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/dialect/Dialect.html[`Dialect`] implementation based on the JDBC metadata returned by the JDBC driver.
 
 |`hibernate.current_session_context_class` |`jta`, `thread`, `managed`, or a custom class implementing `org.hibernate.context.spi.
 CurrentSessionContext` |
 
 Supply a custom strategy for the scoping of the _current_ `Session`.
 
 The definition of what exactly _current_ means is controlled by the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/context/spi/CurrentSessionContext.html[`CurrentSessionContext`] implementation in use.
 
 Note that for backwards compatibility, if a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/context/spi/CurrentSessionContext.html[`CurrentSessionContext`] is not configured but JTA is configured this will default to the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/context/internal/JTASessionContext.html[`JTASessionContext`].
 
 |===================================================================================================================================================================================================================================================================
 
 [[configurations-database-connection]]
 === Database connection properties
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.connection.driver_class` or `javax.persistence.jdbc.driver` | `org.postgresql.Driver` | Names the JDBC `Driver` class name.
 |`hibernate.connection.url` or `javax.persistence.jdbc.url` | `jdbc:postgresql:hibernate_orm_test` | Names the JDBC connection URL.
 |`hibernate.connection.username` or `javax.persistence.jdbc.user` | | Names the JDBC connection user name.
 |`hibernate.connection.password` or `javax.persistence.jdbc.password` | | Names the JDBC connection password.
 |`hibernate.connection.isolation` | `REPEATABLE_READ` or
 `Connection.TRANSACTION_REPEATABLE_READ` | Names the JDBC connection transaction isolation level.
 |`hibernate.connection.autocommit` | `true` or `false` (default value) | Names the JDBC connection autocommit mode.
 |`hibernate.connection.datasource` | |
 
 Either a `javax.sql.DataSource` instance or a JNDI name under which to locate the `DataSource`.
 
 For JNDI names, ses also `hibernate.jndi.class`, `hibernate.jndi.url`, `hibernate.jndi`.
 
 |`hibernate.connection` | | Names a prefix used to define arbitrary JDBC connection properties. These properties are passed along to the JDBC provider when creating a connection.
 |`hibernate.connection.provider_class` | `org.hibernate.hikaricp.internal.
 HikariCPConnectionProvider` a|
 
 Names the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/connections/spi/ConnectionProvider.html[`ConnectionProvider`] to use for obtaining JDBC connections.
 
 Can reference:
 
 * an instance of `ConnectionProvider`
 * a `Class<? extends ConnectionProvider` object reference
 * a fully qualified name of a class implementing `ConnectionProvider`
 
 The term `class` appears in the setting name due to legacy reasons; however it can accept instances.
 
 |`hibernate.jndi.class` | | Names the JNDI `javax.naming.InitialContext` class.
 |`hibernate.jndi.url` | java:global/jdbc/default | Names the JNDI provider/connection url.
 |`hibernate.jndi` | |
 
 Names a prefix used to define arbitrary JNDI `javax.naming.InitialContext` properties.
 
 These properties are passed along to `javax.naming.InitialContext#InitialContext(java.util.Hashtable)`
 
 |`hibernate.connection.acquisition_mode` | `immediate` |
 
 Specifies how Hibernate should acquire JDBC connections. The possible values are given by `org.hibernate.ConnectionAcquisitionMode`.
 
 Should generally only configure this or `hibernate.connection.release_mode`, not both.
 
 |`hibernate.connection.release_mode` | `auto` (default value) |
 
 Specifies how Hibernate should release JDBC connections. The possible values are given by the current transaction mode (`after_transaction` for JDBC transactions and `after_statement` for JTA transactions).
 
 Should generally only configure this or `hibernate.connection.acquisition_mode`, not both.
 
 3+|Hibernate internal connection pool options
 |`hibernate.connection.initial_pool_size` | 1 (default value) | Minimum number of connections for the built-in Hibernate connection pool.
 |`hibernate.connection.pool_size` | 20 (default value) | Maximum number of connections for the built-in Hibernate connection pool.
 |`hibernate.connection.pool_validation_interval` | 30 (default value) | The number of seconds between two consecutive pool validations. During validation, the pool size can increase or decreases based on the connection acquisition request count.
 
 |===================================================================================================================================================================================================================================
 
 [[configurations-c3p0]]
 === c3p0 properties
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.c3p0.min_size` | 1 | Minimum size of C3P0 connection pool. Refers to http://www.mchange.com/projects/c3p0/#minPoolSize[c3p0 `minPoolSize` setting].
 |`hibernate.c3p0.max_size` | 5 | Maximum size of C3P0 connection pool. Refers to http://www.mchange.com/projects/c3p0/#maxPoolSize[c3p0 `maxPoolSize` setting].
 |`hibernate.c3p0.timeout` | 30 | Maximum idle time for C3P0 connection pool. Refers to http://www.mchange.com/projects/c3p0/#maxIdleTime[c3p0 `maxIdleTime` setting].
 |`hibernate.c3p0.max_statements` | 5 | Maximum size of C3P0 statement cache. Refers to http://www.mchange.com/projects/c3p0/#maxStatements[c3p0 `maxStatements` setting].
 |`hibernate.c3p0.acquire_increment` | 2 | Number of connections acquired at a time when there's no connection available in the pool. Refers to http://www.mchange.com/projects/c3p0/#acquireIncrement[c3p0 `acquireIncrement` setting].
 |`hibernate.c3p0.idle_test_period` | 5 | Idle time before a C3P0 pooled connection is validated. Refers to http://www.mchange.com/projects/c3p0/#idleConnectionTestPeriod[c3p0 `idleConnectionTestPeriod` setting].
 |`hibernate.c3p0` | | A setting prefix used to indicate additional c3p0 properties that need to be passed to the underlying c3p0 connection pool.
 |===================================================================================================================================================================================================================================
 
 [[configurations-mapping]]
 === Mapping Properties
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 3+|Table qualifying options
 |`hibernate.default_catalog` |A catalog name |Qualifies unqualified table names with the given catalog in generated SQL.
 |`hibernate.default_schema` |A schema name |Qualify unqualified table names with the given schema or tablespace in generated SQL.
 |`hibernate.schema_name_resolver` |The fully qualified name of an https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/env/spi/SchemaNameResolver.html[`org.hibernate.engine.jdbc.env.spi.SchemaNameResolver`] implementation class |
 By default, Hibernate uses the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/dialect/Dialect.html#getSchemaNameResolver--[`org.hibernate.dialect.Dialect#getSchemaNameResolver`] You can customize how the schema name is resolved by providing a custom implementation of the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/env/spi/SchemaNameResolver.html[`SchemaNameResolver`] interface.
 
 3+|Identifier options
 |`hibernate.id.new_generator_mappings` |`true` (default value) or `false` |
 
 Setting which indicates whether or not the new https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/id/IdentifierGenerator.html[`IdentifierGenerator`] are used for `AUTO`, `TABLE` and `SEQUENCE`.
 
 Existing applications may want to disable this (set it `false`) for upgrade compatibility from 3.x and 4.x to 5.x.
 
 |`hibernate.use_identifier_rollback` |`true` or `false` (default value) |If true, generated identifier properties are reset to default values when objects are deleted.
 |`hibernate.id.optimizer.pooled.preferred` |`none`, `hilo`, `legacy-hilo`, `pooled` (default value), `pooled-lo`, `pooled-lotl` or a fully-qualified name of the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/id/enhanced/Optimizer.html[`Optimizer`] implementation |
 
 When a generator specified an increment-size and an optimizer was not explicitly specified, which of the _pooled_ optimizers should be preferred?
 
 3+|Quoting options
 |`hibernate.globally_quoted_identifiers` |`true` or `false` (default value) |Should all database identifiers be quoted.
 |`hibernate.globally_quoted_identifiers_skip_column_definitions` |`true` or `false` (default value) |
 
 Assuming `hibernate.globally_quoted_identifiers` is `true`, this allows the global quoting to skip column-definitions as defined by `javax.persistence.Column`,
 `javax.persistence.JoinColumn`, etc, and while it avoids column-definitions being quoted due to global quoting, they can still be explicitly quoted in the annotation/xml mappings.
 
 |`hibernate.auto_quote_keyword` |`true` or `false` (default value) |Specifies whether to automatically quote any names that are deemed keywords.
 
 3+|Discriminator options
 |`hibernate.discriminator.implicit_for_joined` |`true` or `false` (default value) |
 
 The legacy behavior of Hibernate is to not use discriminators for joined inheritance (Hibernate does not need the discriminator).
 However, some JPA providers do need the discriminator for handling joined inheritance so, in the interest of portability, this capability has been added to Hibernate too.
 
 However, we want to make sure that legacy applications continue to work as well, which puts us in a bind in terms of how to handle _implicit_ discriminator mappings.
 The solution is to assume that the absence of discriminator metadata means to follow the legacy behavior _unless_ this setting is enabled.
 
 With this setting enabled, Hibernate will interpret the absence of discriminator metadata as an indication to use the JPA-defined defaults for these absent annotations.
 
 See Hibernate Jira issue https://hibernate.atlassian.net/browse/HHH-6911[HHH-6911] for additional background info.
 
 |`hibernate.discriminator.ignore_explicit_for_joined` |`true` or `false` (default value) |
 
 The legacy behavior of Hibernate is to not use discriminators for joined inheritance (Hibernate does not need the discriminator).
 However, some JPA providers do need the discriminator for handling joined inheritance so, in the interest of portability, this capability has been added to Hibernate too.
 
 Existing applications rely (implicitly or explicitly) on Hibernate ignoring any `DiscriminatorColumn` declarations on joined inheritance hierarchies.
 This setting allows these applications to maintain the legacy behavior of `DiscriminatorColumn` annotations being ignored when paired with joined inheritance.
 
 See Hibernate Jira issue https://hibernate.atlassian.net/browse/HHH-6911[HHH-6911] for additional background info.
 
 3+|Naming strategies
 |`hibernate.implicit_naming_strategy` |`default` (default value), `jpa`, `legacy-jpa`, `legacy-hbm`, `component-path` a|
 
 Used to specify the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/naming/ImplicitNamingStrategy.html[`ImplicitNamingStrategy`] class to use.
 The following short names are defined for this setting:
 
 `default`:: Uses the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/naming/ImplicitNamingStrategyJpaCompliantImpl.html[`ImplicitNamingStrategyJpaCompliantImpl`]
 `jpa`:: Uses the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/naming/ImplicitNamingStrategyJpaCompliantImpl.html[`ImplicitNamingStrategyJpaCompliantImpl`]
 `legacy-jpa`:: Uses the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/naming/ImplicitNamingStrategyLegacyJpaImpl.html[`ImplicitNamingStrategyLegacyJpaImpl`]
 `legacy-hbm`:: Uses the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/naming/ImplicitNamingStrategyLegacyHbmImpl.html[`ImplicitNamingStrategyLegacyHbmImpl`]
 `component-path`:: Uses the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/naming/ImplicitNamingStrategyComponentPathImpl.html[`ImplicitNamingStrategyComponentPathImpl`]
 
 If this property happens to be empty, the fallback is to use `default` strategy.
 
 |`hibernate.physical_naming_strategy` | `org.hibernate.boot.model.naming.
 PhysicalNamingStrategyStandardImpl` (default value) | Used to specify the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/naming/PhysicalNamingStrategy.html[`PhysicalNamingStrategy`] class to use.
 3+|Metadata scanning options
 |`hibernate.archive.scanner` | a|
 
 Pass an implementation of https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/archive/scan/spi/Scanner.html[`Scanner`].
 By default, https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/archive/scan/internal/StandardScanner.html[`StandardScanner`] is used.
 
 Accepts either:
 
 * an actual `Scanner` instance
 * a reference to a Class that implements `Scanner`
 * a fully qualified name of a Class that implements `Scanner`
 
 |`hibernate.archive.interpreter` | a|
 
 Pass https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/archive/spi/ArchiveDescriptorFactory.html[`ArchiveDescriptorFactory`] to use in the scanning process.
 
 Accepts either:
 
 * an actual `ArchiveDescriptorFactory` instance
 * a reference to a Class that implements `ArchiveDescriptorFactory`
 * a fully qualified name of a Class that implements `ArchiveDescriptorFactory`
 
 See information on https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/archive/scan/spi/Scanner.html[`Scanner`] about expected constructor forms.
 
 |`hibernate.archive.autodetection` | `hbm,class` (default value) a|
 
 Identifies a comma-separate list of values indicating the mapping types we should auto-detect during scanning.
 
 Allowable values include:
 
 `class`:: scan classes (e.g. `.class`) to extract entity mapping metadata
 `hbm`:: scan `hbm` mapping files (e.g. `hbm.xml`) to extract entity mapping metadata
 
 By default both HBM, annotations, and JPA XML mappings are scanned.
 
 When using JPA, to disable the automatic scanning of all entity classes, the `exclude-unlisted-classes` `persistence.xml` element must be set to true.
 Therefore, when setting `exclude-unlisted-classes` to true, only the classes that are explicitly declared in the `persistence.xml` configuration files are going to be taken into consideration.
 
 |`hibernate.mapping.precedence` | `hbm,class` (default value)  |
 
 Used to specify the order in which metadata sources should be processed.
 Value is a delimited-list whose elements are defined by https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/cfg/MetadataSourceType.html[`MetadataSourceType`].
 
 Default is `hbm,class"`, therefore `hbm.xml` files are processed first, followed by annotations (combined with `orm.xml` mappings).
 
 When using JPA, the XML mapping overrides a conflicting annotation mapping that targets the same entity attribute.
 
 3+|JDBC-related options
 |`hibernate.use_nationalized_character_data` |`true` or `false` (default value) |Enable nationalized character support on all string / clob based attribute ( string, char, clob, text etc ).
 |`hibernate.jdbc.lob.non_contextual_creation` |`true` or `false` (default value) |Should we not use contextual LOB creation (aka based on `java.sql.Connection#createBlob()` et al)? The default value for HANA, H2, and PostgreSQL is `true`.
 |`hibernate.jdbc.time_zone` | A `java.util.TimeZone`, a `java.time.ZoneId` or a `String` representation of a `ZoneId` |Unless specified, the JDBC Driver uses the default JVM time zone. If a different time zone is configured via this setting, the JDBC https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html#setTimestamp-int-java.sql.Timestamp-java.util.Calendar-[PreparedStatement#setTimestamp] is going to use a `Calendar` instance according to the specified time zone.
 |`hibernate.dialect.oracle.prefer_long_raw` | `true` or `false` (default value) |This setting applies to Oracle Dialect only, and it specifies whether `byte[]` or `Byte[]` arrays should be mapped to the deprecated `LONG RAW` (when this configuration property value is `true`) or to a `BLOB` column type (when this configuration property value is `false`).
 
 3+|Bean Validation options
 |`javax.persistence.validation.factory` |`javax.validation.ValidationFactory` implementation | Specify the  `javax.validation.ValidationFactory` implementation to use for Bean Validation.
 |`hibernate.check_nullability` |`true` or `false` |
 
 Enable nullability checking. Raises an exception if a property marked as not-null is null.
 
 Default to `false` if Bean Validation is present in the classpath and Hibernate Annotations is used, `true` otherwise.
 |`hibernate.validator.apply_to_ddl` |`true` (default value) or `false` |
 
 Bean Validation constraints will be applied in DDL if the automatic schema generation is enabled.
 In other words, the database schema will reflect the Bean Validation constraints.
 
 To disable constraint propagation to DDL, set up `hibernate.validator.apply_to_ddl` to `false` in the configuration file.
 Such a need is very uncommon and not recommended.
 
 3+|Misc options
 |`hibernate.create_empty_composites.enabled` |`true` or `false` (default value) | Enable instantiation of composite/embeddable objects when all of its attribute values are `null`. The default (and historical) behavior is that a `null` reference will be used to represent the composite when all of its attributes are `null`.
 |`hibernate.entity_dirtiness_strategy` | fully-qualified class name or an actual `CustomEntityDirtinessStrategy` instance | Setting to identify a `org.hibernate.CustomEntityDirtinessStrategy` to use.
 |`hibernate.default_entity_mode` |`pojo` (default value) or `dynamic-map` |Default `EntityMode` for entity representation for all sessions opened from this `SessionFactory`, defaults to `pojo`.
 |===================================================================================================================================================================================================================================
 
 [[configurations-bytecode-enhancement]]
 === Bytecode Enhancement Properties
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.enhancer.enableDirtyTracking`| `true` or `false` (default value) | Enable dirty tracking feature in runtime bytecode enhancement.
 |`hibernate.enhancer.enableLazyInitialization`| `true` or `false` (default value) | Enable lazy loading feature in runtime bytecode enhancement. This way, even basic types (e.g. `@Basic(fetch = FetchType.LAZY`)) can be fetched lazily.
 |`hibernate.enhancer.enableAssociationManagement`| `true` or `false` (default value) | Enable association management feature in runtime bytecode enhancement which automatically synchronizes a bidirectional association when only one side is changed.
 |`hibernate.bytecode.provider` |`javassist` (default value) | The https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/bytecode/spi/BytecodeProvider.html[`BytecodeProvider`] built-in implementation flavor. Currently, only `javassist` is supported.
 |`hibernate.bytecode.use_reflection_optimizer`| `true` or `false` (default value) | Should we use reflection optimization? The reflection optimizer implements the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/bytecode/spi/ReflectionOptimizer.html[`ReflectionOptimizer`] interface and improves entity instantiation and property getter/setter calls.
 |===================================================================================================================================================================================================================================
 
 [[configurations-query]]
 === Query settings
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.query.plan_cache_max_size` | `2048` (default value)  a|
 
 The maximum number of entries including:
 
 * https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/query/spi/HQLQueryPlan.html[`HQLQueryPlan`]
 * https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/query/spi/FilterQueryPlan.html[`FilterQueryPlan`]
 * https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/query/spi/NativeSQLQueryPlan.html[`NativeSQLQueryPlan`]
 
 maintained by https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/query/spi/QueryPlanCache.html[`QueryPlanCache`].
 
 |`hibernate.query.plan_parameter_metadata_max_size` | `128` (default value) | The maximum number of strong references associated with `ParameterMetadata` maintained by https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/query/spi/QueryPlanCache.html[`QueryPlanCache`].
 |`hibernate.order_by.default_null_ordering` |`none`, `first` or `last` |Defines precedence of null values in `ORDER BY` clause. Defaults to `none` which varies between RDBMS implementation.
 |`hibernate.discriminator.force_in_select` |`true` or `false` (default value) | For entities which do not explicitly say, should we force discriminators into SQL selects?
 |`hibernate.query.substitutions` | `true=1,false=0` |A comma-separated list of token substitutions to use when translating a Hibernate query to SQL.
 |`hibernate.query.factory_class` |`org.hibernate.hql.internal.ast.
 ASTQueryTranslatorFactory` (default value) or `org.hibernate.hql.internal.classic.
 ClassicQueryTranslatorFactory` |Chooses the HQL parser implementation.
 
 |`hibernate.query.jpaql_strict_compliance` |`true` or `false` (default value) |Map from tokens in Hibernate queries to SQL tokens, such as function or literal names.
 
 Should we strictly adhere to JPA Query Language (JPQL) syntax, or more broadly support all of Hibernate's superset (HQL)?
 
 Setting this to `true` may cause valid HQL to throw an exception because it violates the JPQL subset.
 |`hibernate.query.startup_check` | `true` (default value) or `false` |Should named queries be checked during startup?
 |`hibernate.proc.param_null_passing` | `true` or `false` (default value) |
 
 Global setting for whether `null` parameter bindings should be passed to database procedure/function calls as part of https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/procedure/ProcedureCall.html[`ProcedureCall`] handling.
 Implicitly Hibernate will not pass the `null`, the intention being to allow any default argument values to be applied.
 
 This defines a global setting, which can then be controlled per parameter via `org.hibernate.procedure.ParameterRegistration#enablePassingNulls(boolean)`
 
 Values are `true` (pass the NULLs) or `false` (do not pass the NULLs).
 
 |`hibernate.jdbc.log.warnings` | `true` or `false` |Enable fetching JDBC statement warning for logging. Default value is given by `org.hibernate.dialect.Dialect#isJdbcLogWarningsEnabledByDefault()`.
 |`hibernate.session_factory.statement_inspector` | A fully-qualified class name, an instance, or a `Class` object reference a|
 
 Names a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/resource/jdbc/spi/StatementInspector.html[`StatementInspector`] implementation to be applied to every `Session` created by the current `SessionFactory`.
 
 Can reference
 
 * `StatementInspector` instance
 * `StatementInspector` implementation {@link Class} reference
 * `StatementInspector` implementation class name (fully-qualified class name)
 
 3+|Multi-table bulk HQL operations
 |`hibernate.hql.bulk_id_strategy` | A fully-qualified class name, an instance, or a `Class` object reference |Provide a custom https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/hql/spi/id/MultiTableBulkIdStrategy.html[`org.hibernate.hql.spi.id.MultiTableBulkIdStrategy`] implementation for handling multi-table bulk HQL operations.
 |`hibernate.hql.bulk_id_strategy.global_temporary.drop_tables` | `true` or `false` (default value) | For databases that don't support local tables, but just global ones, this configuration property allows you to DROP the global tables used for multi-table bulk HQL operations when the `SessionFactory` or the `EntityManagerFactory` is closed.
 |`hibernate.hql.bulk_id_strategy.persistent.drop_tables` | `true` or `false` (default value) |
 This configuration property is used by the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/hql/spi/id/persistent/PersistentTableBulkIdStrategy.html[`PersistentTableBulkIdStrategy`], that mimics temporary tables for databases which do not support temporary tables.
 It follows a pattern similar to the ANSI SQL definition of global temporary table using a "session id" column to segment rows from the various sessions.
 
 This configuration property allows you to DROP the tables used for multi-table bulk HQL operations when the `SessionFactory` or the `EntityManagerFactory` is closed.
 |`hibernate.hql.bulk_id_strategy.persistent.schema` | Database schema name. By default, the `hibernate.default_schema` is used. |
 This configuration property is used by the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/hql/spi/id/persistent/PersistentTableBulkIdStrategy.html[`PersistentTableBulkIdStrategy`], that mimics temporary tables for databases which do not support temporary tables.
 It follows a pattern similar to the ANSI SQL definition of global temporary table using a "session id" column to segment rows from the various sessions.
 
 This configuration property defines the database schema used for storing the temporary tables used for bulk HQL operations.
 |`hibernate.hql.bulk_id_strategy.persistent.catalog` | Database catalog name. By default, the `hibernate.default_catalog` is used. |
 This configuration property is used by the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/hql/spi/id/persistent/PersistentTableBulkIdStrategy.html[`PersistentTableBulkIdStrategy`], that mimics temporary tables for databases which do not support temporary tables.
 It follows a pattern similar to the ANSI SQL definition of global temporary table using a "session id" column to segment rows from the various sessions.
 
 This configuration property defines the database catalog used for storing the temporary tables used for bulk HQL operations.
+|`hibernate.legacy_limit_handler` | `true` or `false` (default value) |
+Setting which indicates whether or not to use `org.hibernate.dialect.pagination.LimitHandler`
+implementations that sacrifices performance optimizations to allow legacy 4.x limit behavior.
+
+Legacy 4.x behavior favored performing pagination in-memory by avoiding the use of the offset value, which is overall poor performance.
+In 5.x, the limit handler behavior favors performance, thus, if the dialect doesn't support offsets, an exception is thrown instead.
 |===================================================================================================================================================================================================================================
 
 [[configurations-batch]]
 === Batching properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.jdbc.batch_size` |5 |Maximum JDBC batch size. A nonzero value enables batch updates.
 |`hibernate.order_inserts` |`true` or `false` (default value) |Forces Hibernate to order SQL inserts by the primary key value of the items being inserted. This preserves batching when using cascading.
 |`hibernate.order_updates` |`true` or `false` (default value) |Forces Hibernate to order SQL updates by the primary key value of the items being updated. This preserves batching when using cascading and reduces the likelihood of transaction deadlocks in highly-concurrent systems.
 |`hibernate.jdbc.batch_versioned_data` |`true`(default value) or `false` |
 Should versioned entities be included in batching?
 
 Set this property to `true` if your JDBC driver returns correct row counts from executeBatch(). This option is usually safe, but is disabled by default. If enabled, Hibernate uses batched DML for automatically versioned data.
 
 |`hibernate.batch_fetch_style` |`LEGACY`(default value) |
 
 Names the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/loader/BatchFetchStyle.html[`BatchFetchStyle`] to use.
 
 Can specify either the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/loader/BatchFetchStyle.html[`BatchFetchStyle`] name (insensitively), or a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/loader/BatchFetchStyle.html[`BatchFetchStyle`] instance. `LEGACY}` is the default value.
 
 |`hibernate.jdbc.batch.builder` | The fully qualified name of an https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/batch/spi/BatchBuilder.html[`BatchBuilder`] implementation class type or an actual object instance  | Names the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/batch/spi/BatchBuilder.html[`BatchBuilder`] implementation to use.
 |=====================================================================================================================================================================================================================================================================================================================================================================================================
 
 [[configurations-database-fetch]]
 ==== Fetching properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.max_fetch_depth`|A value between `0` and `3` |Sets a maximum depth for the outer join fetch tree for single-ended associations. A single-ended association is a one-to-one or many-to-one assocation. A value of `0` disables default outer join fetching.
 |`hibernate.default_batch_fetch_size` |`4`,`8`, or `16` |Default size for Hibernate Batch fetching of associations (lazily fetched associations can be fetched in batches to prevent N+1 query problems).
 |`hibernate.jdbc.fetch_size` |`0` or an integer |A non-zero value determines the JDBC fetch size, by calling `Statement.setFetchSize()`.
 |`hibernate.jdbc.use_scrollable_resultset` |`true` or `false` |Enables Hibernate to use JDBC2 scrollable resultsets. This property is only relevant for user-supplied JDBC connections. Otherwise, Hibernate uses connection metadata.
 |`hibernate.jdbc.use_streams_for_binary` |`true` or `false` (default value) |Use streams when writing or reading `binary` or `serializable` types to or from JDBC. This is a system-level property.
 |`hibernate.jdbc.use_get_generated_keys` |`true` or `false` |Allows Hibernate to use JDBC3 `PreparedStatement.getGeneratedKeys()` to retrieve natively-generated keys after insert. You need the JDBC3+ driver and JRE1.4+. Disable this property if your driver has problems with the Hibernate identifier generators. By default, it tries to detect the driver capabilities from connection metadata.
 |`hibernate.jdbc.wrap_result_sets` |`true` or `false` (default value) |Enable wrapping of JDBC result sets in order to speed up column name lookups for broken JDBC drivers.
 |`hibernate.enable_lazy_load_no_trans` |`true` or `false` (default value) |
 
 Initialize Lazy Proxies or Collections outside a given Transactional Persistence Context.
 
 Although enabling this configuration can make `LazyInitializationException` go away, it's better to use a fetch plan that guarantees that all properties are properly initialised before the Session is closed.
 
 In reality, you shouldn't probably enable this setting anyway.
 |=====================================================================================================================================================================================================================================================================================================================================================================================================
 
 [[configurations-logging]]
 === Statement logging and statistics
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 3+|SQL statement logging
 |`hibernate.show_sql` |`true` or `false` (default value) |Write all SQL statements to the console. This is an alternative to setting the log category `org.hibernate.SQL` to debug.
 |`hibernate.format_sql` |`true` or `false` (default value) |Pretty-print the SQL in the log and console.
 |`hibernate.use_sql_comments` |`true` or `false` (default value) |If true, Hibernate generates comments inside the SQL, for easier debugging.
 3+|Statistics settings
 |`hibernate.generate_statistics` |`true` or `false` |Causes Hibernate to collect statistics for performance tuning.
 |`hibernate.stats.factory` |The fully qualified name of an https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/stat/spi/StatisticsFactory.html[`StatisticsFactory`] implementation or an actual instance
 The `StatisticsFactory` allow you to customize how the Hibernate Statistics are being collected.
 |`hibernate.session.events.log` |`true` or `false` |
 
 A setting to control whether the `org.hibernate.engine.internal
 .StatisticalLoggingSessionEventListener` is enabled on all `Sessions` (unless explicitly disabled for a given `Session`).
 The default value of this setting is determined by the value for `hibernate.generate_statistics`, meaning that if statistics are enabled, then logging of Session metrics is enabled by default too.
 
 |===================================================================================================================================================================================================================================
 
 [[configurations-cache]]
 === Cache Properties
 
 [width="100%",cols="20%,20%,60%",]
 |==================================================================================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.cache.region.factory_class` | `org.hibernate.cache.infinispan.
 InfinispanRegionFactory` |The fully-qualified name of the `RegionFactory` implementation class.
 |`hibernate.cache.default_cache_concurrency_strategy` | |
 
 Setting used to give the name of the default https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/annotations/CacheConcurrencyStrategy.html[`CacheConcurrencyStrategy`] to use when either `@javax.persistence.Cacheable` or
 `@org.hibernate.annotations.Cache`.  `@org.hibernate.annotations.Cache` is used to override the global setting.
 
 |`hibernate.cache.use_minimal_puts` |`true` (default value) or `false` |Optimizes second-level cache operation to minimize writes, at the cost of more frequent reads. This is most useful for clustered caches and is enabled by default for clustered cache implementations.
 |`hibernate.cache.use_query_cache` |`true` or `false` (default value) |Enables the query cache. You still need to set individual queries to be cachable.
 |`hibernate.cache.use_second_level_cache` |`true` (default value) or `false` |Enable/disable the second level cache, which is enabled by default, although the default `RegionFactor` is `NoCachingRegionFactory` (meaning there is no actual caching implementation).
 |`hibernate.cache.query_cache_factory` |Fully-qualified classname |A custom https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/cache/spi/QueryCacheFactory.html[`QueryCacheFactory`] interface. The default is the built-in `StandardQueryCacheFactory`.
 |`hibernate.cache.region_prefix` |A string |A prefix for second-level cache region names.
 |`hibernate.cache.use_structured_entries` |`true` or `false` (default value) |Forces Hibernate to store data in the second-level cache in a more human-readable format.
 |`hibernate.cache.auto_evict_collection_cache` |`true` or `false` (default: false) |Enables the automatic eviction of a bi-directional association's collection cache when an element in the `ManyToOne` collection is added/updated/removed without properly managing the change on the `OneToMany` side.
 |`hibernate.cache.use_reference_entries` |`true` or `false` |Optimizes second-level cache operation to store immutable entities (aka "reference") which do not have associations into cache directly, this case, lots of disasseble and deep copy operations can be avoid. Default value of this property is `false`.
 |`hibernate.ejb.classcache`| `hibernate.ejb.classcache
 .org.hibernate.ejb.test.Item` = `read-write` |	Sets the associated entity class cache concurrency strategy for the designated region. Caching configuration should follow the following pattern `hibernate.ejb.classcache.<fully.qualified.Classname>` usage[, region] where usage is the cache strategy used and region the cache region name.
 |`hibernate.ejb.collectioncache`| `hibernate.ejb.collectioncache
 .org.hibernate.ejb.test.Item.distributors` = `read-write, RegionName`/> | Sets the associated collection cache concurrency strategy for the designated region. Caching configuration should follow the following pattern `hibernate.ejb.collectioncache.<fully.qualified.Classname>.<role>` usage[, region] where usage is the cache strategy used and region the cache region name
 |==================================================================================================================================================================================================================================================================================================================
 
 [[configurations-infinispan]]
 === Infinispan properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.cache.infinispan.cfg` | `org/hibernate/cache/infinispan/
 builder/infinispan-configs.xml` | Classpath or filesystem resource containing the Infinispan configuration settings.
 |`hibernate.cache.infinispan.statistics` | | Property name that controls whether Infinispan statistics are enabled. The property value is expected to be a boolean true or false, and it overrides statistic configuration in base Infinispan configuration, if provided.
 |`hibernate.cache.infinispan.use_synchronization` | | Deprecated setting because Infinispan is designed to always register a `Synchronization` for `TRANSACTIONAL` caches.
 |`hibernate.cache.infinispan.cachemanager` | There is no default value, the user must specify the property. | Specifies the JNDI name under which the `EmbeddedCacheManager` is bound.
 |=====================================================================================================================================================================================================================================================
 
 [[configurations-transactions]]
 === Transactions properties
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.transaction.jta.platform` |`JBossAS`, `BitronixJtaPlatform` |
 
 Names the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/transaction/jta/platform/spi/JtaPlatform.html[`JtaPlatform`] implementation to use for integrating with JTA systems.
 Can reference either a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/transaction/jta/platform/spi/JtaPlatform.html[`JtaPlatform`] instance or the name of the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/transaction/jta/platform/spi/JtaPlatform.html[`JtaPlatform`] implementation class
 
 |`hibernate.jta.prefer_user_transaction` |`true` or `false` (default value) |
 
 Should we prefer using the `org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform#retrieveUserTransaction` over using `org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform#retrieveTransactionManager`
 
 |`hibernate.transaction.jta.platform_resolver` | | Names the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/transaction/jta/platform/spi/JtaPlatformResolver.html[`JtaPlatformResolver`] implementation to use.
 |`hibernate.jta.cacheTransactionManager` | `true` (default value) or `false` | A configuration value key used to indicate that it is safe to cache.
 |`hibernate.jta.cacheUserTransaction` | `true` or `false` (default value) | A configuration value key used to indicate that it is safe to cache.
 |`hibernate.transaction.flush_before_completion` |`true` or `false` (default value) | Causes the session be flushed during the before completion phase of the transaction. If possible, use built-in and automatic session context management instead.
 |`hibernate.transaction.auto_close_session` |`true` or `false` (default value) |Causes the session to be closed during the after completion phase of the transaction. If possible, use built-in and automatic session context management instead.
 |`hibernate.transaction.coordinator_class` | a|
 
 Names the implementation of https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/resource/transaction/spi/TransactionCoordinatorBuilder.html[`TransactionCoordinatorBuilder`] to use for creating https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/resource/transaction/spi/TransactionCoordinator.html[`TransactionCoordinator`] instances.
 
 Can be
 
 * `TransactionCoordinatorBuilder` instance
 * `TransactionCoordinatorBuilder` implementation `Class` reference
 * `TransactionCoordinatorBuilder` implementation class name (fully-qualified name) or short name
 
 The following short names are defined for this setting:
 
 `jdbc`:: Manages transactions via calls to `java.sql.Connection` (default for non-JPA applications)
 `jta`:: Manages transactions via JTA. See <<chapters/bootstrap/Bootstrap.adoc#bootstrap-jpa-compliant,Java EE bootstrapping>>
 
 If a JPA application does not provide a setting for `hibernate.transaction.coordinator_class`, Hibernate will
 automatically build the proper transaction coordinator based on the transaction type for the persistence unit.
 
 If a non-JPA application does not provide a setting for `hibernate.transaction.coordinator_class`, Hibernate
 will use `jdbc` as the default. This default will cause problems if the application actually uses JTA-based transactions.
 A non-JPA application that uses JTA-based transactions should explicitly set `hibernate.transaction.coordinator_class=jta`
 or provide a custom https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/resource/transaction/TransactionCoordinatorBuilder.html[`TransactionCoordinatorBuilder`] that builds a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/resource/transaction/TransactionCoordinator.html[`TransactionCoordinator`] that properly coordinates with JTA-based transactions.
 
 |`hibernate.jta.track_by_thread` | `true` (default value) or `false` |
 
 A transaction can be rolled back by another thread ("tracking by thread") and not the original application.
 Examples of this include a JTA transaction timeout handled by a background reaper thread.
 
 The ability to handle this situation requires checking the Thread ID every time Session is called, so enabling this can certainly have a performance impact.
 
 |`hibernate.transaction.factory_class` | | This is a legacy setting that's been deprecated and you should use the `hibernate.transaction.jta.platform` instead.
 
 |===================================================================================================================================================================================================================================
 
 [[configurations-multi-tenancy]]
 === Multi-tenancy settings
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.multiTenancy` | `NONE` (default value), `SCHEMA`, `DATABASE`, and `DISCRIMINATOR` (not implemented yet)  | The multi-tenancy strategy in use.
 |`hibernate.multi_tenant_connection_provider` | `true` or `false` (default value) | Names a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/connections/spi/MultiTenantConnectionProvider.html[`MultiTenantConnectionProvider`] implementation to use. As `MultiTenantConnectionProvider` is also a service, can be configured directly through the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/registry/StandardServiceRegistryBuilder.html[`StandardServiceRegistryBuilder`].
 |`hibernate.tenant_identifier_resolver` | a|
 
 Names a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/context/spi/CurrentTenantIdentifierResolver.html[`CurrentTenantIdentifierResolver`] implementation to resolve the resolve the current tenant identifier so that calling `SessionFactory#openSession()` would get a `Session` that's connected to the right tenant.
 
 Can be:
 
 * `CurrentTenantIdentifierResolver` instance
 * `CurrentTenantIdentifierResolver` implementation `Class` object reference
 * `CurrentTenantIdentifierResolver` implementation class name
 
 |`hibernate.multi_tenant.datasource.identifier_for_any` | `true` or `false` (default value) | When the `hibernate.connection.datasource` property value is resolved to a `javax.naming.Context` object, this configuration property defines the JNDI name used to locate the `DataSource` used for fetching the initial `Connection` which is used to access to the database metadata of the underlying database(s) (in situations where we do not have a tenant id, like startup processing).
 
 |===================================================================================================================================================================================================================================
 
 [[configurations-hbmddl]]
 === Automatic schema generation
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.hbm2ddl.auto` |`none` (default value), `create-only`, `drop`, `create`, `create-drop`, `validate`, and `update` a|
 
 Setting to perform `SchemaManagementTool` actions automatically as part of the `SessionFactory` lifecycle.
 Valid options are defined by the `externalHbm2ddlName` value of the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/Action.html[`Action`] enum:
 
 `none`:: No action will be performed.
 `create-only`:: Database creation will be generated.
 `drop`:: Database dropping will be generated.
 `create`:: Database dropping will be generated followed by database creation.
 `create-drop`:: Drop the schema and recreate it on SessionFactory startup.  Additionally, drop the schema on SessionFactory shutdown.
 `validate`:: Validate the database schema
 `update`:: Update the database schema
 
 |`javax.persistence.schema-generation.database.action` |`none` (default value), `create-only`, `drop`, `create`, `create-drop`, `validate`, and `update` a|
 
 Setting to perform `SchemaManagementTool` actions automatically as part of the `SessionFactory` lifecycle.
 Valid options are defined by the `externalJpaName` value of the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/Action.html[`Action`] enum:
 
 `none`:: No action will be performed.
 `create`:: Database creation will be generated.
 `drop`:: Database dropping will be generated.
 `drop-and-create`:: Database dropping will be generated followed by database creation.
 
 |`javax.persistence.schema-generation.scripts.action` |`none` (default value), `create-only`, `drop`, `create`, `create-drop`, `validate`, and `update` a|
 
 Setting to perform `SchemaManagementTool` actions writing the commands into a DDL script file.
 Valid options are defined by the `externalJpaName` value of the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/Action.html[`Action`] enum:
 
 `none`:: No action will be performed.
 `create`:: Database creation will be generated.
 `drop`:: Database dropping will be generated.
 `drop-and-create`:: Database dropping will be generated followed by database creation.
 
 |`javax.persistence.schema-generation-connection` | |Allows passing a specific `java.sql.Connection` instance to be used by `SchemaManagementTool`
 |`javax.persistence.database-product-name` | |
 
 Specifies the name of the database provider in cases where a Connection to the underlying database is not available (aka, mainly in generating scripts).
 In such cases, a value for this setting _must_ be specified.
 
 The value of this setting is expected to match the value returned by `java.sql.DatabaseMetaData#getDatabaseProductName()` for the target database.
 
 Additionally, specifying `javax.persistence.database-major-version` and/or `javax.persistence.database-minor-version` may be required to understand exactly how to generate the required schema commands.
 
 |`javax.persistence.database-major-version` | |
 
 Specifies the major version of the underlying database, as would be returned by `java.sql.DatabaseMetaData#getDatabaseMajorVersion` for the target database.
 
 This value is used to help more precisely determine how to perform schema generation tasks for the underlying database in cases where `javax.persistence.database-product-name` does not provide enough distinction.
 
 |`javax.persistence.database-minor-version` | |
 
 Specifies the minor version of the underlying database, as would be returned by `java.sql.DatabaseMetaData#getDatabaseMinorVersion` for the target database.
 
 This value is used to help more precisely determine how to perform schema generation tasks for the underlying database in cases where `javax.persistence.database-product-name` and `javax.persistence.database-major-version` does not provide enough distinction.
 
 |`javax.persistence.schema-generation.create-source` | a|
 
 Specifies whether schema generation commands for schema creation are to be determine based on object/relational mapping metadata, DDL scripts, or a combination of the two.
 See https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/SourceType.html[`SourceType`] for valid set of values.
 
 If no value is specified, a default is assumed as follows:
 
 * if source scripts are specified (per `javax.persistence.schema-generation.create-script-source`), then `scripts` is assumed
 * otherwise, `metadata` is assumed
 
 |`javax.persistence.schema-generation.drop-source` | a|
 
 Specifies whether schema generation commands for schema dropping are to be determine based on object/relational mapping metadata, DDL scripts, or a combination of the two.
 See https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/SourceType.html[`SourceType`] for valid set of values.
 
 If no value is specified, a default is assumed as follows:
 
 * if source scripts are specified (per `javax.persistence.schema-generation.create-script-source`), then `scripts` is assumed
 * otherwise, `metadata` is assumed
 
 |`javax.persistence.schema-generation.create-script-source` | |
 
 Specifies the `create` script file as either a `java.io.Reader` configured for reading of the DDL script file or a string designating a file `java.net.URL` for the DDL script.
 
 Hibernate historically also accepted `hibernate.hbm2ddl.import_files` for a similar purpose, but `javax.persistence.schema-generation.create-script-source` should be preferred over `hibernate.hbm2ddl.import_files`.
 
 |`javax.persistence.schema-generation.drop-script-source` | | Specifies the `drop` script file as either a `java.io.Reader` configured for reading of the DDL script file or a string designating a file `java.net.URL` for the DDL script.
 |`javax.persistence.schema-generation.scripts.create-target` | |For cases where the `javax.persistence.schema-generation.scripts.action` value indicates that schema creation commands should be written to DDL script file, `javax.persistence.schema-generation.scripts.create-target` specifies either a `java.io.Writer` configured for output of the DDL script or a string specifying the file URL for the DDL script.
 |`javax.persistence.schema-generation.scripts.drop-target` | |For cases where the `javax.persistence.schema-generation.scripts.action` value indicates that schema dropping commands should be written to DDL script file, `javax.persistence.schema-generation.scripts.drop-target` specifies either a `java.io.Writer` configured for output of the DDL script or a string specifying the file URL for the DDL script.
 |`javax.persistence.hibernate.hbm2ddl.import_files` | `import.sql` (default value) a|
 
 Comma-separated names of the optional files containing SQL DML statements executed during the `SessionFactory` creation.
 File order matters, the statements of a give file are executed before the statements of the following one.
 
 These statements are only executed if the schema is created, meaning that `hibernate.hbm2ddl.auto` is set to `create`, `create-drop`, or `update`.
 `javax.persistence.schema-generation.create-script-source` / `javax.persistence.schema-generation.drop-script-source` should be preferred.
 
 |`javax.persistence.sql-load-script-source` | |
 
 JPA variant of `hibernate.hbm2ddl.import_files`. Specifies a `java.io.Reader` configured for reading of the SQL load script or a string designating the file `java.net.URL` for the SQL load script.
 A "SQL load script" is a script that performs some database initialization (INSERT, etc).
 
 |`hibernate.hbm2ddl.import_files_sql_extractor` | |
 
 Reference to the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/hbm2ddl/ImportSqlCommandExtractor.html[`ImportSqlCommandExtractor`] implementation class to use for parsing source/import files as defined by `javax.persistence.schema-generation.create-script-source`,
 `javax.persistence.schema-generation.drop-script-source` or `hibernate.hbm2ddl.import_files`.
 
 Reference may refer to an instance, a Class implementing `ImportSqlCommandExtractor` of the fully-qualified name of the `ImportSqlCommandExtractor` implementation.
 If the fully-qualified name is given, the implementation must provide a no-arg constructor.
 
 The default value is https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/hbm2ddl/SingleLineSqlCommandExtractor.html[`SingleLineSqlCommandExtractor`].
 
 |`hibernate.hbm2dll.create_namespaces` | `true` or `false` (default value) |Specifies whether to automatically create also the database schema/catalog.
 |`javax.persistence.create-database-schemas` | `true` or `false` (default value) |
 
 The JPA variant of `hibernate.hbm2dll.create_namespaces`. Specifies whether the persistence provider is to create the database schema(s) in addition to creating database objects (tables, sequences, constraints, etc).
 The value of this boolean property should be set to `true` if the persistence provider is to create schemas in the database or to generate DDL that contains "CREATE SCHEMA" commands.
 If this property is not supplied (or is explicitly `false`), the provider should not attempt to create database schemas.
 
 |`hibernate.hbm2ddl.schema_filter_provider` | |
 
 Used to specify the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/spi/SchemaFilterProvider.html[`SchemaFilterProvider`] to be used by `create`, `drop`, `migrate`, and `validate` operations on the database schema.
 `SchemaFilterProvider` provides filters that can be used to limit the scope of these operations to specific namespaces, tables and sequences. All objects are included by default.
 
 |`hibernate.hbm2ddl.jdbc_metadata_extraction_strategy` |`grouped` (default value) or `individually` a|
 
 Setting to choose the strategy used to access the JDBC Metadata.
 Valid options are defined by the `strategy` value of the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/JdbcMetadaAccessStrategy.html[`JdbcMetadaAccessStrategy`] enum:
 
 `grouped`:: https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/spi/SchemaMigrator.html[`SchemaMigrator`] and https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/spi/SchemaValidator.html[`SchemaValidator`] execute a single `java.sql.DatabaseMetaData#getTables(String, String, String, String[])` call to retrieve all the database table in order to determine if all the `javax.persistence.Entity` have a corresponding mapped database tables.
 `individually`:: https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/spi/SchemaMigrator.html[`SchemaMigrator`] and https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/spi/SchemaValidator.html[`SchemaValidator`] execute one `java.sql.DatabaseMetaData#getTables(String, String, String, String[])` call for each `javax.persistence.Entity` in order to determine if a corresponding database table exists.
 
 |`hibernate.hbm2ddl.delimiter` | `;` |Identifies the delimiter to use to separate schema management statements in script outputs.
 
 |`hibernate.schema_management_tool` |A schema name |Used to specify the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/spi/SchemaManagementTool.html[`SchemaManagementTool`] to use for performing schema management. The default is to use https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/schema/internal/HibernateSchemaManagementTool.html[`HibernateSchemaManagementTool`]
 |`hibernate.synonyms` |`true` or `false` (default value) |If enabled, allows schema update and validation to support synonyms. Due to the possibility that this would return duplicate tables (especially in Oracle), this is disabled by default.
 |`hibernate.hbm2dll.extra_physical_table_types` |`BASE TABLE` |Identifies a comma-separated list of values to specify extra table types, other than the default `TABLE` value, to recognize as defining a physical table by schema update, creation and validation.
 |`hibernate.schema_update.unique_constraint_strategy` |`DROP_RECREATE_QUIETLY`, `RECREATE_QUIETLY`, `SKIP` a|
 
 Unique columns and unique keys both use unique constraints in most dialects.
 `SchemaUpdate` needs to create these constraints, but DBs support for finding existing constraints is extremely inconsistent.
 Further, non-explicitly-named unique constraints use randomly generated characters.
 
 Therefore, the https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/tool/hbm2ddl/UniqueConstraintSchemaUpdateStrategy.html[`UniqueConstraintSchemaUpdateStrategy`] offers the following options:
 
 `DROP_RECREATE_QUIETLY`:: Default option.
 	Attempt to drop, then (re-)create each unique constraint. Ignore any exceptions being thrown.
 `RECREATE_QUIETLY`::
 	Attempts to (re-)create unique constraints, ignoring exceptions thrown if the constraint already existed
 `SKIP`::
 	Does not attempt to create unique constraints on a schema update.
 |`hibernate.hbm2ddl.charset_name` |`Charset.defaultCharset()` |Defines the charset (encoding) used for all input/output schema generation resources. By default, Hibernate uses the default charset given by `Charset.defaultCharset()`. This configuration property allows you to override the default JVM setting so that you can specify which encoding is used when reading and writing schema generation resources (e.g. File, URL).
 |`hibernate.hbm2ddl.halt_on_error` |`true` or `false` (default value) |Whether the schema migration tool should halt on error, therefore terminating the bootstrap process. By default, the `EntityManagerFactory` or `SessionFactory` are created even if the schema migration throws exceptions. To prevent this default behavior, set this property value to `true`.
 
 |===================================================================================================================================================================================================================================
 
 [[configurations-exception-handling]]
 === Exception handling
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.jdbc.sql_exception_converter` | Fully-qualified name of class implementing `SQLExceptionConverter` |The https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/exception/spi/SQLExceptionConverter.html[`SQLExceptionConverter`] to use for converting `SQLExceptions` to Hibernate's `JDBCException` hierarchy. The default is to use the configured https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/dialect/Dialect.html[`Dialect`]'s preferred `SQLExceptionConverter`.
 |===================================================================================================================================================================================================================================
 
 [[configurations-session-events]]
 === Session events
 
 [width="100%",cols="20%,20%,60%",]
 |===========================================================================================================================
 |Property |Example |Purpose
 |`hibernate.session.events.auto` | | Fully qualified class name implementing the `SessionEventListener` interface.
 |`hibernate.session_factory.interceptor` or `hibernate.ejb.interceptor` | `org.hibernate.EmptyInterceptor` (default value)  a|
 
 Names a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/Interceptor` implementation to be applied to every `Session` created by the current `org.hibernate.SessionFactory`
 
 Can reference:
 
 * `Interceptor` instance
 * `Interceptor` implementation `Class` object reference
 * `Interceptor` implementation class name
 
 |`hibernate.ejb.interceptor.session_scoped` | fully-qualified class name or class reference | An optional Hibernate interceptor.
 
 The interceptor instance is specific to a given Session instance (and hence is not thread-safe) has to implement `org.hibernate.Interceptor` and have a no-arg constructor.
 
 This property can not be combined with `hibernate.ejb.interceptor`.
 |`hibernate.ejb.session_factory_observer` | fully-qualified class name or class reference | Specifies a `SessionFactoryObserver` to be applied to the SessionFactory. The class must have a no-arg constructor.
 |`hibernate.ejb.event` | `hibernate.ejb.event.pre-load` = `com.acme.SecurityListener,com.acme.AuditListener` | Event listener list for a given event type. The list of event listeners is a comma separated fully qualified class name list.
 |===========================================================================================================================
 
 [[configurations-jmx]]
 === JMX settings
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.jmx.enabled` | `true` or `false` (default value) | Enable JMX.
 |`hibernate.jmx.usePlatformServer` | `true` or `false` (default value) | Uses the platform MBeanServer as returned by `ManagementFactory#getPlatformMBeanServer()`.
 |`hibernate.jmx.agentId` | | The agent identifier of the associated `MBeanServer`.
 |`hibernate.jmx.defaultDomain` | | The domain name of the associated `MBeanServer`.
 |`hibernate.jmx.sessionFactoryName` | | The `SessionFactory` name appended to the object name the Manageable Bean is registered with. If null, the `hibernate.session_factory_name` configuration value is used.
 |`org.hibernate.core | | The default object domain appended to the object name the Manageable Bean is registered with.
 |===================================================================================================================================================================================================================================
 
 [[configurations-jacc]]
 === JACC settings
 
 [width="100%",cols="20%,20%,60%",]
 |===================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.jacc.enabled` | `true` or `false` (default value)  | Is JACC enabled?
 |`hibernate.jacc` | `hibernate.jacc.allowed.org.jboss.ejb3.test.jacc.AllEntity` | The property name defines the role (e.g. `allowed`) and the entity class name (e.g. `org.jboss.ejb3.test.jacc.AllEntity`), while the property value defines the authorized actions (e.g. `insert,update,read`).
 |`hibernate.jacc_context_id` | | A String identifying the policy context whose PolicyConfiguration interface is to be returned. The value passed to this parameter must not be null.
 |===================================================================================================================================================================================================================================
 
 [[configurations-misc]]
 === ClassLoaders properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.classLoaders` | |Used to define a `java.util.Collection<ClassLoader>` or the `ClassLoader` instance Hibernate should use for class-loading and resource-lookups.
 |`hibernate.classLoader.application` | |Names the `ClassLoader` used to load user application classes.
 |`hibernate.classLoader.resources` | |Names the `ClassLoader` Hibernate should use to perform resource loading.
 |`hibernate.classLoader.hibernate` | |Names the `ClassLoader` responsible for loading Hibernate classes.  By default this is the `ClassLoader` that loaded this class.
 |`hibernate.classLoader.environment` | |Names the `ClassLoader` used when Hibernate is unable to locates classes on the `hibernate.classLoader.application` or `hibernate.classLoader.hibernate`.
 |=====================================================================================================================================================================================================================================================
 
 [[configurations-bootstrap]]
 === Bootstrap properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.integrator_provider` | The fully qualified name of an https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/jpa/boot/spi/IntegratorProvider.html[`IntegratorProvider`] |
 Used to define a list of  https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/integrator/spi/Integrator.html[`Integrator`] which are used during bootstrap process to integrate various services.
 |`hibernate.strategy_registration_provider` | The fully qualified name of an https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/jpa/boot/spi/StrategyRegistrationProviderList.html[`StrategyRegistrationProviderList`] |
 Used to define a list of  https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/registry/selector/StrategyRegistrationProvider.html[`StrategyRegistrationProvider`] which are used during bootstrap process to provide registrations of strategy selector(s).
 |`hibernate.type_contributors` | The fully qualified name of an https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/jpa/boot/spi/TypeContributorList.html[`TypeContributorList`] |
 Used to define a list of  https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/boot/model/TypeContributor.html[`TypeContributor`] which are used during bootstrap process to contribute types.
 |`hibernate.persister.resolver` | The fully qualified name of a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/persister/spi/PersisterClassResolver.html[`PersisterClassResolver`] or a `PersisterClassResolver` instance |
 Used to define an implementation of the `PersisterClassResolver` interface which can be used to customize how an entity or a collection is being persisted.
 |`hibernate.persister.factory` | The fully qualified name of a https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/persister/spi/PersisterFactory.html[`PersisterFactory`] or a `PersisterFactory` instance |
 Like a `PersisterClassResolver`, the `PersisterFactory` can be used to customize how an entity or a collection are being persisted.
 |`hibernate.service.allow_crawling` | `true` (default value) or `false` |
 Crawl all available service bindings for an alternate registration of a given Hibernate `Service`.
 
 
 
 |=====================================================================================================================================================================================================================================================
 
 [[configurations-misc]]
 === Miscellaneous properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.dialect_resolvers` | | Names any additional https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/dialect/spi/DialectResolver.html[`DialectResolver`] implementations to  register with the standard https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/engine/jdbc/dialect/spi/DialectFactory.html[`DialectFactory`]
 |`hibernate.session_factory_name` |A JNDI name |
 
 Setting used to name the Hibernate `SessionFactory`.
 Naming the `SessionFactory` allows for it to be properly serialized across JVMs as long as the same name is used on each JVM.
 
 If `hibernate.session_factory_name_is_jndi` is set to `true`, this is also the name under which the `SessionFactory` is bound into JNDI on startup and from which it can be obtained from JNDI.
 
 |`hibernate.session_factory_name_is_jndi` |`true` (default value) or `false` |
 
 Does the value defined by `hibernate.session_factory_name` represent a JNDI namespace into which the `org.hibernate.SessionFactory` should be bound and made accessible?
 
 Defaults to `true` for backwards compatibility. Set this to `false` if naming a SessionFactory is needed for serialization purposes, but no writable JNDI context exists in the runtime environment or if the user simply does not want JNDI to be used.
 
 |`hibernate.ejb.entitymanager_factory_name`| By default, the persistence unit name is used, otherwise a randomly generated UUID | Internally, Hibernate keeps track of all `EntityManagerFactory` instances using the `EntityManagerFactoryRegistry`. The name is used as a key to identify a given `EntityManagerFactory` reference.
 |`hibernate.ejb.cfgfile`| `hibernate.cfg.xml` (default value) | XML configuration file to use to configure Hibernate.
 |`hibernate.ejb.discard_pc_on_close`| `true` or `false` (default value) |
 
 If true, the persistence context will be discarded (think `clear()` when the method is called.
 Otherwise, the persistence context will stay alive till the transaction completion: all objects will remain managed, and any change will be synchronized with the database (default to false, ie wait for transaction completion).
 
 |`hibernate.ejb.metamodel.population`| `enabled` or `disabled`, or `ignoreUnsupported` (default value) a|
 
 Setting that indicates whether to build the JPA types.
 
 Accepts three values:
 
 enabled:: Do the build
 disabled:: Do not do the build
 ignoreUnsupported:: Do the build, but ignore any non-JPA features that would otherwise result in a failure (e.g. `@Any` annotation).
 
 |`hibernate.jpa.static_metamodel.population` | `enabled` or `disabled`, or `skipUnsupported` (default value) a|
 
 Setting that controls whether we seek out JPA _static metamodel_ classes and populate them.
 
 Accepts three values:
 
 enabled:: Do the population
 disabled:: Do not do the population
 skipUnsupported:: Do the population, but ignore any non-JPA features that would otherwise result in the population failing (e.g. `@Any` annotation).
 
 
 |`hibernate.delay_cdi_access`| `true` or `false` (default value) | Defines delayed access to CDI `BeanManager`. Starting in 5.1 the preferred means for CDI bootstrapping is through https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/jpa/event/spi/jpa/ExtendedBeanManager.html[`ExtendedBeanManager`].
 
 |`hibernate.allow_update_outside_transaction` | `true` or `false` (default value) a|
 
 Setting that allows to perform update operations outside of a transaction boundary.
 
 Accepts two values:
 
 true:: allows to flush an update out of a transaction
 false:: does not allow
 
 |`hibernate.collection_join_subquery`| `true` (default value) or `false` | Setting which indicates whether or not the new JOINS over collection tables should be rewritten to subqueries.
 
 |`hibernate.allow_refresh_detached_entity`| `true` (default value when using Hibernate native bootstrapping) or `false` (default value when using JPA bootstrapping) | Setting that allows to call `javax.persistence.EntityManager#refresh(entity)` or `Session#refresh(entity)` on a detached instance even when the `org.hibernate.Session` is obtained from a JPA `javax.persistence.EntityManager`.
 |`hibernate.event.merge.entity_copy_observer`| `disallow` (default value), `allow`, `log` (testing purpose only) or fully-qualified class name a|
 
 Setting that specifies how Hibernate will respond when multiple representations of the same persistent entity ("entity copy") is detected while merging.
 
 The possible values are:
 
 disallow (the default):: throws `IllegalStateException` if an entity copy is detected
 allow:: performs the merge operation on each entity copy that is detected
 log:: (provided for testing only) performs the merge operation on each entity copy that is detected and logs information about the entity copies.
 This setting requires DEBUG logging be enabled for https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/event/internal/EntityCopyAllowedLoggedObserver.html[`EntityCopyAllowedLoggedObserver`].
 
 In addition, the application may customize the behavior by providing an implementation of https://docs.jboss.org/hibernate/orm/{majorMinorVersion}/javadocs/org/hibernate/event/spi/EntityCopyObserver.html[`EntityCopyObserver`] and setting `hibernate.event.merge.entity_copy_observer` to the class name.
 When this property is set to `allow` or `log`, Hibernate will merge each entity copy detected while cascading the merge operation.
 In the process of merging each entity copy, Hibernate will cascade the merge operation from each entity copy to its associations with `cascade=CascadeType.MERGE` or `CascadeType.ALL`.
 The entity state resulting from merging an entity copy will be overwritten when another entity copy is merged.
 
 For more details, check out the <<chapters/pc/PersistenceContext.adoc#pc-merge-gotchas,Merge gotchas>> section.
 
 |=====================================================================================================================================================================================================================================================
 
 [[configurations-envers]]
 === Envers properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.envers.autoRegisterListeners` | `true` (default value) or `false` |When set to `false`, the Envers entity listeners are no longer auto-registered, so you need to register them manually during the bootstrap process.
 |`hibernate.integration.envers.enabled` | `true` (default value) or `false` |Enable or disable the Hibernate Envers `Service` integration.
 |`hibernate.listeners.envers.autoRegister` | |Legacy setting. Use `hibernate.envers.autoRegisterListeners` or `hibernate.integration.envers.enabled` instead.
 |=====================================================================================================================================================================================================================================================
 
 [[configurations-spatial]]
 === Spatial properties
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.integration.spatial.enabled` | `true` (default value) or `false` | Enable or disable the Hibernate Spatial `Service` integration.
 |`hibernate.spatial.connection_finder` | `org.geolatte.geom.codec.db.oracle.DefaultConnectionFinder` | Define the fully-qualified name of class implementing the `org.geolatte.geom.codec.db.oracle.ConnectionFinder` interface.
 |=====================================================================================================================================================================================================================================================
 
 [[configurations-internal]]
 === Internal properties
 
 The following configuration properties are used internally, and you shouldn't probably have to configured them in your application.
 
 [width="100%",cols="20%,20%,60%",]
 |=====================================================================================================================================================================================================================================================
 |Property |Example |Purpose
 |`hibernate.enable_specj_proprietary_syntax` | `true` or `false` (default value) | Enable or disable the SpecJ proprietary mapping syntax which differs from JPA specification. Used during performance testing only.
 |`hibernate.temp.use_jdbc_metadata_defaults` | `true` (default value) or `false` |
 This setting is used to control whether we should consult the JDBC metadata to determine certain Settings default values when the database may not be available (mainly in tools usage).
 |`hibernate.connection_provider.injection_data` | `java.util.Map` | Connection provider settings to be injected in the currently configured connection provider.
 |`hibernate.jandex_index` | `org.jboss.jandex.Index` | Names a Jandex `org.jboss.jandex.Index` instance to use.
 |=====================================================================================================================================================================================================================================================
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index 3dae9fb638..b497499e06 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -607,1005 +607,1014 @@ public interface AvailableSettings {
 
 	/**
 	 * Specifies whether to automatically quote any names that are deemed keywords.  Auto-quoting
 	 * is disabled by default. Set to true to enable it.
 	 *
 	 * @since 5.0
 	 */
 	String KEYWORD_AUTO_QUOTING_ENABLED = "hibernate.auto_quote_keyword";
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// SessionFactoryBuilder level settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Setting used to name the Hibernate {@link org.hibernate.SessionFactory}.
 	 *
 	 * Naming the SessionFactory allows for it to be properly serialized across JVMs as
 	 * long as the same name is used on each JVM.
 	 *
 	 * If {@link #SESSION_FACTORY_NAME_IS_JNDI} is set to {@code true}, this is also the
 	 * name under which the SessionFactory is bound into JNDI on startup and from which
 	 * it can be obtained from JNDI.
 	 *
 	 * @see #SESSION_FACTORY_NAME_IS_JNDI
 	 * @see org.hibernate.internal.SessionFactoryRegistry
 	 */
 	String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Does the value defined by {@link #SESSION_FACTORY_NAME} represent a JNDI namespace into which
 	 * the {@link org.hibernate.SessionFactory} should be bound and made accessible?
 	 *
 	 * Defaults to {@code true} for backwards compatibility.
 	 *
 	 * Set this to {@code false} if naming a SessionFactory is needed for serialization purposes, but
 	 * no writable JNDI context exists in the runtime environment or if the user simply does not want
 	 * JNDI to be used.
 	 *
 	 * @see #SESSION_FACTORY_NAME
 	 */
 	String SESSION_FACTORY_NAME_IS_JNDI = "hibernate.session_factory_name_is_jndi";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	String SHOW_SQL ="hibernate.show_sql";
 
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	String FORMAT_SQL ="hibernate.format_sql";
 
 	/**
 	 * Add comments to the generated SQL
 	 */
 	String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 
 	/**
 	 * The default batch size for batch fetching
 	 */
 	String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 
 	/**
 	 * Default JDBC TimeZone. Unless specified, the JVM default TimeZone is going to be used by the underlying JDBC Driver.
 	 *
 	 * @since 5.2.3
 	 */
 	String JDBC_TIME_ZONE = "hibernate.jdbc.time_zone";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 
 	/**
 	 * Specifies how Hibernate should acquire JDBC connections.  Should generally only configure
 	 * this or {@link #RELEASE_CONNECTIONS}, not both
 	 *
 	 * @see org.hibernate.ConnectionAcquisitionMode
 	 *
 	 * @since 5.1
 	 *
 	 * @deprecated (since 5.2) use {@link #CONNECTION_HANDLING} instead
 	 */
 	@Deprecated
 	String ACQUIRE_CONNECTIONS = "hibernate.connection.acquisition_mode";
 
 	/**
 	 * Specifies how Hibernate should release JDBC connections.  Should generally only configure
 	 * this or {@link #ACQUIRE_CONNECTIONS}, not both
 	 *
 	 * @see org.hibernate.ConnectionReleaseMode
 	 *
 	 * @deprecated (since 5.2) use {@link #CONNECTION_HANDLING} instead
 	 */
 	@Deprecated
 	String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 
 	/**
 	 * Specifies how Hibernate should manage JDBC connections in terms of acquiring and releasing.
 	 * Supersedes {@link #ACQUIRE_CONNECTIONS} and {@link #RELEASE_CONNECTIONS}
 	 *
 	 * @see org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode
 	 *
 	 * @since 5.2
 	 */
 	String CONNECTION_HANDLING = "hibernate.connection.handling_mode";
 
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 
 	String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * The {@link org.hibernate.exception.spi.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * Default precedence of null values in {@code ORDER BY} clause.  Supported options: {@code none} (default),
 	 * {@code first}, {@code last}.
 	 */
 	String DEFAULT_NULL_ORDERING = "hibernate.order_by.default_null_ordering";
 
 	/**
 	 * Enable fetching JDBC statement warning for logging.
 	 *
 	 * Values are {@code true}  or {@code false} .
 	 * Default value is {@link org.hibernate.dialect.Dialect#isJdbcLogWarningsEnabledByDefault()}
 	 *
 	 * @since 5.1
 	 */
 	String LOG_JDBC_WARNINGS =  "hibernate.jdbc.log.warnings";
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// c3p0 connection pooling specific settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * A setting prefix used to indicate settings that target the hibernate-c3p0 integration
 	 */
 	String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 
 	/**
 	 * Idle time beforeQuery a C3P0 pooled connection is validated
 	 */
 	String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// proxool connection pooling specific settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * A setting prefix used to indicate settings that target the hibernate-proxool integration
 	 */
 	String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 * @deprecated Use {@link #PROXOOL_CONFIG_PREFIX} instead
 	 */
 	@Deprecated
 	String PROXOOL_PREFIX = PROXOOL_CONFIG_PREFIX;
 
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	String PROXOOL_XML = "hibernate.proxool.xml";
 
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Second-level cache settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The {@link org.hibernate.cache.spi.RegionFactory} implementation.  Can refer to:<ul>
 	 *     <li>an Object implementing {@link org.hibernate.cache.spi.RegionFactory}</li>
 	 *     <li>a Class implementing {@link org.hibernate.cache.spi.RegionFactory}</li>
 	 *     <li>FQN of a Class implementing {@link org.hibernate.cache.spi.RegionFactory}</li>
 	 * </ul>
 	 */
 	String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * Allow control to specify the {@link org.hibernate.cache.spi.CacheKeysFactory} impl to use.
 	 * Can refer to:<ul>
 	 *     <li>an Object implementing {@link org.hibernate.cache.spi.CacheKeysFactory}</li>
 	 *     <li>a Class implementing {@link org.hibernate.cache.spi.CacheKeysFactory}</li>
 	 *     <li>FQN of a Class implementing {@link org.hibernate.cache.spi.CacheKeysFactory}</li>
 	 *     <li>'default' as a short name for {@link org.hibernate.cache.internal.DefaultCacheKeysFactory}</li>
 	 *     <li>'simple' as a short name for {@link org.hibernate.cache.internal.SimpleCacheKeysFactory}</li>
 	 * </ul>
 	 *
 	 * @since 5.2 - note that currently this is only honored for hibernate-infinispan
 	 */
 	String CACHE_KEYS_FACTORY = "hibernate.cache.keys_factory";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 
 	/**
 	 * The {@link org.hibernate.cache.spi.QueryCacheFactory} implementation class.
 	 */
 	String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 
 	/**
 	 * Enables the automatic eviction of a bi-directional association's collection cache when an element in the
 	 * ManyToOne collection is added/updated/removed without properly managing the change on the OneToMany side.
 	 */
 	String AUTO_EVICT_COLLECTION_CACHE = "hibernate.cache.auto_evict_collection_cache";
 
 	/**
 	 * Enable direct storage of entity references into the second level cache when applicable (immutable data, etc).
 	 * Default is to not store direct references.
 	 */
 	String USE_DIRECT_REFERENCE_CACHE_ENTRIES = "hibernate.cache.use_reference_entries";
 
 
 
 
 
 
 	// Still to categorize
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
 	String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
 	/**
 	 * Should all database identifiers be quoted.  A {@code true}/{@code false} option.
 	 */
 	String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Assuming {@link #GLOBALLY_QUOTED_IDENTIFIERS}, this allows such global quoting
 	 * to skip column-definitions as defined by {@link javax.persistence.Column},
 	 * {@link javax.persistence.JoinColumn}, etc.
 	 * <p/>
 	 * JPA states that column-definitions are subject to global quoting, so by default this setting
 	 * is {@code false} for JPA compliance.  Set to {@code true} to avoid column-definitions
 	 * being quoted due to global quoting (they will still be quoted if explicitly quoted in the
 	 * annotation/xml).
 	 */
 	String GLOBALLY_QUOTED_IDENTIFIERS_SKIP_COLUMN_DEFINITIONS = "hibernate.globally_quoted_identifiers_skip_column_definitions";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 *
 	 * @deprecated Use {@link #PREFERRED_POOLED_OPTIMIZER} instead
 	 */
 	@Deprecated
 	String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * When a generator specified an increment-size and an optimizer was not explicitly specified, which of
 	 * the "pooled" optimizers should be preferred?  Can specify an optimizer short name or an Optimizer
 	 * impl FQN.
 	 */
 	String PREFERRED_POOLED_OPTIMIZER = "hibernate.id.optimizer.pooled.preferred";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE}
 	 */
 	@Deprecated
 	String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_MAX_SIZE}
 	 */
 	@Deprecated
 	String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * The maximum number of entries including:
 	 * <ul>
 	 *     <li>{@link org.hibernate.engine.query.spi.HQLQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.FilterQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.NativeSQLQueryPlan}</li>
 	 * </ul>
 	 * 
 	 * maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 */
 	String QUERY_PLAN_CACHE_MAX_SIZE = "hibernate.query.plan_cache_max_size";
 
 	/**
 	 * The maximum number of {@link ParameterMetadataImpl} maintained
 	 * by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 */
 	String QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE = "hibernate.query.plan_parameter_metadata_max_size";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// SchemaManagementTool settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Setting to perform SchemaManagementTool actions automatically as part of
 	 * the SessionFactory lifecycle.  Valid options are defined by the
 	 * {@link org.hibernate.tool.schema.Action} enum.
 	 * <p/>
 	 * Interpreted in combination with {@link #HBM2DDL_DATABASE_ACTION} and
 	 * {@link #HBM2DDL_SCRIPTS_ACTION}.  If no value is specified, the default
 	 * is "none" ({@link org.hibernate.tool.schema.Action#NONE}).
 	 *
 	 * @see org.hibernate.tool.schema.Action
 	 */
 	String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Setting to perform SchemaManagementTool actions against the database directly via JDBC
 	 * automatically as part of the SessionFactory lifecycle.  Valid options are defined by the
 	 * {@link org.hibernate.tool.schema.Action} enum.
 	 * <p/>
 	 * Interpreted in combination with {@link #HBM2DDL_AUTO}.  If no value is specified, the default
 	 * is "none" ({@link org.hibernate.tool.schema.Action#NONE}).
 	 *
 	 * @see org.hibernate.tool.schema.Action
 	 */
 	String HBM2DDL_DATABASE_ACTION = "javax.persistence.schema-generation.database.action";
 
 	/**
 	 * Setting to perform SchemaManagementTool actions writing the commands into a DDL script file.
 	 * Valid options are defined by the {@link org.hibernate.tool.schema.Action} enum.
 	 * <p/>
 	 * Interpreted in combination with {@link #HBM2DDL_AUTO}.  If no value is specified, the default
 	 * is "none" ({@link org.hibernate.tool.schema.Action#NONE}).
 	 *
 	 * @see org.hibernate.tool.schema.Action
 	 */
 	String HBM2DDL_SCRIPTS_ACTION = "javax.persistence.schema-generation.scripts.action";
 
 	/**
 	 * Allows passing a specific {@link java.sql.Connection} instance to be used by SchemaManagementTool.
 	 * <p/>
 	 * May also be used to determine the values for {@value #HBM2DDL_DB_NAME},
 	 * {@value #HBM2DDL_DB_MAJOR_VERSION} and {@value #HBM2DDL_DB_MINOR_VERSION}.
 	 */
 	String HBM2DDL_CONNECTION = "javax.persistence.schema-generation-connection";
 
 	/**
 	 * Specifies the name of the database provider in cases where a Connection to the underlying database is
 	 * not available (aka, mainly in generating scripts).  In such cases, a value for this setting
 	 * *must* be specified.
 	 * <p/>
 	 * The value of this setting is expected to match the value returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseProductName()} for the target database.
 	 * <p/>
 	 * Additionally specifying {@value #HBM2DDL_DB_MAJOR_VERSION} and/or {@value #HBM2DDL_DB_MINOR_VERSION}
 	 * may be required to understand exactly how to generate the required schema commands.
 	 *
 	 * @see #HBM2DDL_DB_MAJOR_VERSION
 	 * @see #HBM2DDL_DB_MINOR_VERSION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String HBM2DDL_DB_NAME = "javax.persistence.database-product-name";
 
 	/**
 	 * Specifies the major version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMajorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where {@value #HBM2DDL_DB_NAME} does not provide enough distinction.
 
 	 * @see #HBM2DDL_DB_NAME
 	 * @see #HBM2DDL_DB_MINOR_VERSION
 	 */
 	String HBM2DDL_DB_MAJOR_VERSION = "javax.persistence.database-major-version";
 
 	/**
 	 * Specifies the minor version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMinorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where the combination of {@value #HBM2DDL_DB_NAME} and {@value #HBM2DDL_DB_MAJOR_VERSION} does not provide
 	 * enough distinction.
 	 *
 	 * @see #HBM2DDL_DB_NAME
 	 * @see #HBM2DDL_DB_MAJOR_VERSION
 	 */
 	String HBM2DDL_DB_MINOR_VERSION = "javax.persistence.database-minor-version";
 
 	/**
 	 * Specifies whether schema generation commands for schema creation are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SourceType} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #HBM2DDL_CREATE_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SourceType
 	 */
 	String HBM2DDL_CREATE_SOURCE = "javax.persistence.schema-generation.create-source";
 
 	/**
 	 * Specifies whether schema generation commands for schema dropping are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SourceType} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #HBM2DDL_DROP_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SourceType
 	 */
 	String HBM2DDL_DROP_SOURCE = "javax.persistence.schema-generation.drop-source";
 
 	/**
 	 * Specifies the CREATE script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 * <p/>
 	 * Hibernate historically also accepted {@link #HBM2DDL_IMPORT_FILES} for a similar purpose.  This setting
 	 * should be preferred over {@link #HBM2DDL_IMPORT_FILES} moving forward
 	 *
 	 * @see #HBM2DDL_CREATE_SOURCE
 	 * @see #HBM2DDL_IMPORT_FILES
 	 */
 	String HBM2DDL_CREATE_SCRIPT_SOURCE = "javax.persistence.schema-generation.create-script-source";
 
 	/**
 	 * Specifies the DROP script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 *
 	 * @see #HBM2DDL_DROP_SOURCE
 	 */
 	String HBM2DDL_DROP_SCRIPT_SOURCE = "javax.persistence.schema-generation.drop-script-source";
 
 	/**
 	 * For cases where the {@value #HBM2DDL_SCRIPTS_ACTION} value indicates that schema creation commands should
 	 * be written to DDL script file, {@value #HBM2DDL_SCRIPTS_CREATE_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #HBM2DDL_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String HBM2DDL_SCRIPTS_CREATE_TARGET = "javax.persistence.schema-generation.scripts.create-target";
 
 	/**
 	 * For cases where the {@value #HBM2DDL_SCRIPTS_ACTION} value indicates that schema drop commands should
 	 * be written to DDL script file, {@value #HBM2DDL_SCRIPTS_DROP_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #HBM2DDL_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String HBM2DDL_SCRIPTS_DROP_TARGET = "javax.persistence.schema-generation.scripts.drop-target";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed beforeQuery the statements of the
 	 * following files.
 	 * <p/>
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 * <p/>
 	 * The default value is <tt>/import.sql</tt>
 	 * <p/>
 	 * {@link #HBM2DDL_CREATE_SCRIPT_SOURCE} / {@link #HBM2DDL_DROP_SCRIPT_SOURCE} should be preferred
 	 * moving forward
 	 */
 	String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * JPA variant of {@link #HBM2DDL_IMPORT_FILES}
 	 * <p/>
 	 * Specifies a {@link java.io.Reader} configured for reading of the SQL load script or a string designating the
 	 * file {@link java.net.URL} for the SQL load script.
 	 * <p/>
 	 * A "SQL load script" is a script that performs some database initialization (INSERT, etc).
 	 */
 	String HBM2DDL_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";
 
 	/**
 	 * Reference to the {@link org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor} implementation class
 	 * to use for parsing source/import files as defined by {@link #HBM2DDL_CREATE_SCRIPT_SOURCE},
 	 * {@link #HBM2DDL_DROP_SCRIPT_SOURCE} or {@link #HBM2DDL_IMPORT_FILES}.
 	 * <p/>
 	 * Reference may refer to an instance, a Class implementing ImportSqlCommandExtractor of the FQN
 	 * of the ImportSqlCommandExtractor implementation.  If the FQN is given, the implementation
 	 * must provide a no-arg constructor.
 	 * <p/>
 	 * The default value is {@link org.hibernate.tool.hbm2ddl.SingleLineSqlCommandExtractor}.
 	 */
 	String HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR = "hibernate.hbm2ddl.import_files_sql_extractor";
 
 	/**
 	 * Specifies whether to automatically create also the database schema/catalog.
 	 * The default is false.
 	 *
 	 * @since 5.0
 	 */
 	String HBM2DLL_CREATE_NAMESPACES = "hibernate.hbm2dll.create_namespaces";
 
 	/**
 	 * The JPA variant of {@link #HBM2DLL_CREATE_NAMESPACES}
 	 * <p/>
 	 * Specifies whether the persistence provider is to create the database schema(s) in addition to creating
 	 * database objects (tables, sequences, constraints, etc).  The value of this boolean property should be set
 	 * to {@code true} if the persistence provider is to create schemas in the database or to generate DDL that
 	 * contains "CREATE SCHEMA" commands.  If this property is not supplied (or is explicitly {@code false}), the
 	 * provider should not attempt to create database schemas.
 	 */
 	String HBM2DLL_CREATE_SCHEMAS = "javax.persistence.create-database-schemas";
 
 	/**
 	 * Used to specify the {@link org.hibernate.tool.schema.spi.SchemaFilterProvider} to be used by
 	 * create, drop, migrate and validate operations on the database schema.  SchemaFilterProvider
 	 * provides filters that can be used to limit the scope of these operations to specific namespaces,
 	 * tables and sequences. All objects are included by default.
 	 *
 	 * @since 5.1
 	 */
 	String HBM2DDL_FILTER_PROVIDER = "hibernate.hbm2ddl.schema_filter_provider";
 
 	/**
 	 * Setting to choose the strategy used to access the JDBC Metadata.
 	 *
 	 * Valid options are defined by the {@link JdbcMetadaAccessStrategy} enum.
 	 *
 	 * @see JdbcMetadaAccessStrategy
 	 */
 	String HBM2DDL_JDBC_METADATA_EXTRACTOR_STRATEGY = "hibernate.hbm2ddl.jdbc_metadata_extraction_strategy";
 
 	/**
 	 * Identifies the delimiter to use to separate schema management statements in script outputs
 	 */
 	String HBM2DDL_DELIMITER = "hibernate.hbm2ddl.delimiter";
 
 	/**
 	 * The name of the charset used by the schema generation resource. Without specifying this configuration property, the JVM default charset is used.
 	 *
 	 * @since 5.2.3
 	 */
 	String HBM2DDL_CHARSET_NAME = "hibernate.hbm2ddl.charset_name";
 
 	/**
 	 * Whether the schema migration tool should halt on error, therefore terminating the bootstrap process.
 	 *
 	 * @since 5.2.4
 	 */
 	String HBM2DDL_HALT_ON_ERROR = "hibernate.hbm2ddl.halt_on_error";
 
 	String JMX_ENABLED = "hibernate.jmx.enabled";
 	String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
 	String JMX_AGENT_ID = "hibernate.jmx.agentId";
 	String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
 	String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
 	String JMX_DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
 
 	/**
 	 * Setting to identify a {@link org.hibernate.CustomEntityDirtinessStrategy} to use.  May point to
 	 * either a class name or instance.
 	 */
 	String CUSTOM_ENTITY_DIRTINESS_STRATEGY = "hibernate.entity_dirtiness_strategy";
 
 	/**
 	 * Strategy for multi-tenancy.
 
 	 * @see org.hibernate.MultiTenancyStrategy
 	 * @since 4.0
 	 */
 	String MULTI_TENANT = "hibernate.multiTenancy";
 
 	/**
 	 * Names a {@link org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider} implementation to
 	 * use.  As MultiTenantConnectionProvider is also a service, can be configured directly through the
 	 * {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}
 	 *
 	 * @since 4.1
 	 */
 	String MULTI_TENANT_CONNECTION_PROVIDER = "hibernate.multi_tenant_connection_provider";
 
 	/**
 	 * Names a {@link org.hibernate.context.spi.CurrentTenantIdentifierResolver} implementation to use.
 	 * <p/>
 	 * Can be<ul>
 	 *     <li>CurrentTenantIdentifierResolver instance</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation {@link Class} reference</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation class name</li>
 	 * </ul>
 	 *
 	 * @since 4.1
 	 */
 	String MULTI_TENANT_IDENTIFIER_RESOLVER = "hibernate.tenant_identifier_resolver";
 
 	/**
 	 * Names a {@link org.hibernate.Interceptor} implementation to be applied to the
 	 * {@link org.hibernate.SessionFactory} and propagated to each Session created from the SessionFactory.
 	 * This setting identifies an Interceptor which is effectively a singleton across all the Sessions
 	 * opened from the SessionFactory to which it is applied; the same instance will be passed to each Session.
 	 * <p/>
 	 * See {@link #SESSION_SCOPED_INTERCEPTOR} for an approach to create unique Interceptor instances for each Session
 	 * <p/>
 	 * Can reference<ul>
 	 *     <li>Interceptor instance</li>
 	 *     <li>Interceptor implementation {@link Class} reference</li>
 	 *     <li>Interceptor implementation class name</li>
 	 * </ul>
 	 *
 	 * @since 5.0
 	 */
 	String INTERCEPTOR = "hibernate.session_factory.interceptor";
 
 	/**
 	 * Names a {@link org.hibernate.Interceptor} implementation to be applied to the
 	 * {@link org.hibernate.SessionFactory} and propagated to each Session created from the SessionFactory.
 	 * This setting identifies an Interceptor implementation that is to be applied to every Session opened
 	 * from the SessionFactory, but unlike {@link #INTERCEPTOR} a unique instance of the Interceptor is
 	 * used for each Session.
 	 * <p/>
 	 * Can reference<ul>
 	 *     <li>Interceptor implementation {@link Class} reference</li>
 	 *     <li>Interceptor implementation class name</li>
 	 * </ul>
 	 * Note specifically that this setting cannot name an Interceptor instance.
 	 *
 	 * @since 5.2
 	 */
 	String SESSION_SCOPED_INTERCEPTOR = "hibernate.session_factory.session_scoped_interceptor";
 
 	/**
 	 * Names a {@link org.hibernate.resource.jdbc.spi.StatementInspector} implementation to be applied to
 	 * the {@link org.hibernate.SessionFactory}.  Can reference<ul>
 	 *     <li>StatementInspector instance</li>
 	 *     <li>StatementInspector implementation {@link Class} reference</li>
 	 *     <li>StatementInspector implementation class name (FQN)</li>
 	 * </ul>
 	 *
 	 * @since 5.0
 	 */
 	String STATEMENT_INSPECTOR = "hibernate.session_factory.statement_inspector";
 
 	String ENABLE_LAZY_LOAD_NO_TRANS = "hibernate.enable_lazy_load_no_trans";
 
 	String HQL_BULK_ID_STRATEGY = "hibernate.hql.bulk_id_strategy";
 
 	/**
 	 * Names the {@link org.hibernate.loader.BatchFetchStyle} to use.  Can specify either the
 	 * {@link org.hibernate.loader.BatchFetchStyle} name (insensitively), or a
 	 * {@link org.hibernate.loader.BatchFetchStyle} instance.
 	 * 
 	 * {@code LEGACY} is the default value.
 	 */
 	String BATCH_FETCH_STYLE = "hibernate.batch_fetch_style";
 	
 	/**
 	 * A transaction can be rolled back by another thread ("tracking by thread")
 	 * -- not the original application. Examples of this include a JTA
 	 * transaction timeout handled by a background reaper thread.  The ability
 	 * to handle this situation requires checking the Thread ID every time
 	 * Session is called.  This can certainly have performance considerations.
 	 * 
 	 * Default is <code>true</code> (enabled).
 	 */
 	String JTA_TRACK_BY_THREAD = "hibernate.jta.track_by_thread";
 
 	String JACC_CONTEXT_ID = "hibernate.jacc_context_id";
 	String JACC_PREFIX = "hibernate.jacc";
 	String JACC_ENABLED = "hibernate.jacc.enabled";
 
 	/**
 	 * If enabled, allows schema update and validation to support synonyms.  Due
 	 * to the possibility that this would return duplicate tables (especially in
 	 * Oracle), this is disabled by default.
 	 */
 	String ENABLE_SYNONYMS = "hibernate.synonyms";
 
 	/**
 	 * Identifies a comma-separate list of values to specify extra table types,
 	 * other than the default "TABLE" value, to recognize as defining a physical table
 	 * by schema update, creation and validation.
 	 *
 	 * @since 5.0
 	 */
 	String EXTRA_PHYSICAL_TABLE_TYPES = "hibernate.hbm2dll.extra_physical_table_types";
 
 	/**
 	 * Unique columns and unique keys both use unique constraints in most dialects.
 	 * SchemaUpdate needs to create these constraints, but DB's
 	 * support for finding existing constraints is extremely inconsistent. Further,
 	 * non-explicitly-named unique constraints use randomly generated characters.
 	 * 
 	 * Therefore, select from these strategies.
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#DROP_RECREATE_QUIETLY} (DEFAULT):
 	 * 			Attempt to drop, then (re-)create each unique constraint.
 	 * 			Ignore any exceptions thrown.
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#RECREATE_QUIETLY}:
 	 * 			attempt to (re-)create unique constraints,
 	 * 			ignoring exceptions thrown if the constraint already existed
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#SKIP}:
 	 * 			do not attempt to create unique constraints on a schema update
 	 */
 	String UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY = "hibernate.schema_update.unique_constraint_strategy";
 
 	/**
 	 * Enable statistics collection
 	 */
 	String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	/**
 	 * A setting to control whether to {@link org.hibernate.engine.internal.StatisticalLoggingSessionEventListener} is
 	 * enabled on all Sessions (unless explicitly disabled for a given Session).  The default value of this
 	 * setting is determined by the value for {@link #GENERATE_STATISTICS}, meaning that if collection of statistics
 	 * is enabled logging of Session metrics is enabled by default too.
 	 */
 	String LOG_SESSION_METRICS = "hibernate.session.events.log";
 
 	/**
 	 * Defines a default {@link org.hibernate.SessionEventListener} to be applied to opened Sessions.
 	 */
 	String AUTO_SESSION_EVENTS_LISTENER = "hibernate.session.events.auto";
 
 	/**
 	 * Global setting for whether NULL parameter bindings should be passed to database
 	 * procedure/function calls as part of {@link org.hibernate.procedure.ProcedureCall}
 	 * handling.  Implicitly Hibernate will not pass the NULL, the intention being to allow
 	 * any default argumnet values to be applied.
 	 * <p/>
 	 * This defines a global setting, which can them be controlled per parameter via
 	 * {@link org.hibernate.procedure.ParameterRegistration#enablePassingNulls(boolean)}
 	 * <p/>
 	 * Values are {@code true} (pass the NULLs) or {@code false} (do not pass the NULLs).
 	 */
 	String PROCEDURE_NULL_PARAM_PASSING = "hibernate.proc.param_null_passing";
 
 	/**
 	 * Enable instantiation of composite/embedded objects when all of its attribute values are {@code null}.
 	 * The default (and historical) behavior is that a {@code null} reference will be used to represent the
 	 * composite when all of its attributes are {@code null}
 	 *
 	 * @since 5.1
 	 */
 	String CREATE_EMPTY_COMPOSITES_ENABLED = "hibernate.create_empty_composites.enabled";
 
 	/**
 	 * Setting that allows access to the underlying {@link org.hibernate.Transaction}, even
 	 * when using a JTA since normal JPA operations prohibit this behavior.
 	 * <p/>
 	 * Values are {@code true} grants access, {@code false} does not.
 	 * <p/>
 	 * The default behavior is to allow access unless the session is bootstrapped via JPA.
 	 */
 	String ALLOW_JTA_TRANSACTION_ACCESS = "hibernate.jta.allowTransactionAccess";
 
 	/**
 	 * Setting that allows to perform update operations outside of a transaction boundary.
 	 *
 	 * Since version 5.2 Hibernate conforms with the JPA specification and does not allow anymore
 	 * to flush any update out of a transaction boundary.
 	 * <p/>
 	 * Values are: {@code true} to allow flush operations out of a transaction, {@code false} to disallow.
 	 * <p/>
 	 * The default behavior is {@code false}
 	 *
 	 * @since 5.2
 	 */
 	String ALLOW_UPDATE_OUTSIDE_TRANSACTION = "hibernate.allow_update_outside_transaction";
 
 	/**
 	 * Setting which indicates whether or not the new JOINS over collection tables should be rewritten to subqueries.
 	 * <p/>
 	 * Default is {@code true}.  Existing applications may want to disable this (set it {@code false}) for
 	 * upgrade compatibility.
 	 *
 	 * @since 5.2
 	 */
 	String COLLECTION_JOIN_SUBQUERY = "hibernate.collection_join_subquery";
 
 	/**
 	 * Setting that allows to call {@link javax.persistence.EntityManager#refresh(Object)}
 	 * or {@link org.hibernate.Session#refresh(Object)} on a detached entity instance when the {@link org.hibernate.Session} is obtained from
 	 * a JPA {@link javax.persistence.EntityManager}).
 	 * <p>
 	 * <p/>
 	 * Values are {@code true} permits the refresh, {@code false} does not permit the detached instance refresh and an {@link IllegalArgumentException} is thrown.
 	 * <p/>
 	 * The default value is {@code false} when the Session is bootstrapped via JPA {@link javax.persistence.EntityManagerFactory}, otherwise is {@code true}
 	 *
 	 * @since 5.2
 	 */
 	String ALLOW_REFRESH_DETACHED_ENTITY = "hibernate.allow_refresh_detached_entity";
 
 	/**
 	 * Setting that specifies how Hibernate will respond when multiple representations of the same persistent entity ("entity copy") is detected while merging.
 	 * <p/>
 	 * The possible values are:
 	 *
 	 * <ul>
 	 *     <li>disallow (the default): throws {@link java.lang.IllegalStateException} if an entity copy is detected</li>
 	 *     <li>allow: performs the merge operation on each entity copy that is detected</li>
 	 *     <li>log: (provided for testing only) performs the merge operation on each entity copy that is detected and logs information about the entity copies.
 	 *     This setting requires DEBUG logging be enabled for {@link org.hibernate.event.internal.EntityCopyAllowedLoggedObserver}.
 	 *     </li>
 	 * </ul>
 	 *
 	 * <p/>
 	 * In addition, the application may customize the behavior by providing an implementation of {@link org.hibernate.event.spi.EntityCopyObserver} and setting {@code hibernate.event.merge.entity_copy_observer} to the class name.
 	 * When this property is set to {@code allow} or {@code log}, Hibernate will merge each entity copy detected while cascading the merge operation.
 	 * In the process of merging each entity copy, Hibernate will cascade the merge operation from each entity copy to its associations with {@code CascadeType.MERGE} or {@code CascadeType.ALL}.
 	 * The entity state resulting from merging an entity copy will be overwritten when another entity copy is merged.
 	 *
 	 * @since 4.3
 	 */
 	String MERGE_ENTITY_COPY_OBSERVER = "hibernate.event.merge.entity_copy_observer";
 
-
-
-
-
+	/**
+	 * Setting which indicates whether or not to use {@link org.hibernate.dialect.pagination.LimitHandler}
+	 * implementations that sacrifices performance optimizations to allow legacy 4.x limit behavior.
+	 * </p>
+	 * Legacy 4.x behavior favored performing pagination in-memory by avoiding the use of the offset
+	 * value, which is overall poor performance.  In 5.x, the limit handler behavior favors performance
+	 * thus if the dialect doesn't support offsets, an exception is thrown instead.
+	 * </p>
+	 * Default is {@code false}.
+	 *
+	 * @since 5.2.5
+	 */
+	String USE_LEGACY_LIMIT_HANDLERS = "hibernate.legacy_limit_handler";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index 04bf153cc6..966cb8caba 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,676 +1,679 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.ConditionalParenthesisFunction;
 import org.hibernate.dialect.function.ConvertFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.identity.Chache71IdentityColumnSupport;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.TopLimitHandler;
 import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CacheJoinFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * Cach&eacute; 2007.1 dialect.
  *
  * This class is required in order to use Hibernate with Intersystems Cach&eacute; SQL.  Compatible with
  * Cach&eacute; 2007.1.
  *
  * <h2>PREREQUISITES</h2>
  * These setup instructions assume that both Cach&eacute; and Hibernate are installed and operational.
  * <br>
  * <h2>HIBERNATE DIRECTORIES AND FILES</h2>
  * JBoss distributes the InterSystems Cache' dialect for Hibernate 3.2.1
  * For earlier versions of Hibernate please contact
  * <a href="http://www.intersystems.com/support/cache-support.html">InterSystems Worldwide Response Center</A> (WRC)
  * for the appropriate source files.
  * <br>
  * <h2>CACH&Eacute; DOCUMENTATION</h2>
  * Documentation for Cach&eacute; is available online when Cach&eacute; is running.
  * It can also be obtained from the
  * <a href="http://www.intersystems.com/cache/downloads/documentation.html">InterSystems</A> website.
  * The book, "Object-oriented Application Development Using the Cach&eacute; Post-relational Database:
  * is also available from Springer-Verlag.
  * <br>
  * <h2>HIBERNATE DOCUMENTATION</h2>
  * Hibernate comes with extensive electronic documentation.
  * In addition, several books on Hibernate are available from
  * <a href="http://www.manning.com">Manning Publications Co</a>.
  * Three available titles are "Hibernate Quickly", "Hibernate in Action", and "Java Persistence with Hibernate".
  * <br>
  * <h2>TO SET UP HIBERNATE FOR USE WITH CACH&Eacute;</h2>
  * The following steps assume that the directory where Cach&eacute; was installed is C:\CacheSys.
  * This is the default installation directory for  Cach&eacute;.
  * The default installation directory for Hibernate is assumed to be C:\Hibernate.
  * <p/>
  * If either product is installed in a different location, the pathnames that follow should be modified appropriately.
  * <p/>
  * Cach&eacute; version 2007.1 and above is recommended for use with
  * Hibernate.  The next step depends on the location of your
  * CacheDB.jar depending on your version of Cach&eacute;.
  * <ol>
  * <li>Copy C:\CacheSys\dev\java\lib\JDK15\CacheDB.jar to C:\Hibernate\lib\CacheDB.jar.</li>
  * <p/>
  * <li>Insert the following files into your Java classpath:
  * <p/>
  * <ul>
  * <li>All jar files in the directory C:\Hibernate\lib</li>
  * <li>The directory (or directories) where hibernate.properties and/or hibernate.cfg.xml are kept.</li>
  * </ul>
  * </li>
  * <p/>
  * <li>In the file, hibernate.properties (or hibernate.cfg.xml),
  * specify the Cach&eacute; dialect and the Cach&eacute; version URL settings.</li>
  * </ol>
  * <p/>
  * For example, in Hibernate 3.2, typical entries in hibernate.properties would have the following
  * "name=value" pairs:
  * <p/>
  * <table cols=3 border cellpadding=5 cellspacing=0>
  * <tr>
  * <th>Property Name</th>
  * <th>Property Value</th>
  * </tr>
  * <tr>
  * <td>hibernate.dialect</td>
  * <td>org.hibernate.dialect.Cache71Dialect</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.driver_class</td>
  * <td>com.intersys.jdbc.CacheDriver</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.username</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.password</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.url</td>
  * <td>jdbc:Cache://127.0.0.1:1972/USER</td>
  * </tr>
  * </table>
  * <p/>
  * <b>NOTE:</b> Please contact your administrator for the userid and password you should use when
  *         attempting access via JDBC.  By default, these are chosen to be "_SYSTEM" and "SYS" respectively
  *         as noted in the SQL standard.
  * <br>
  * <h2>CACH&Eacute; VERSION URL</h2>
  * This is the standard URL for the JDBC driver.
  * For a JDBC driver on the machine hosting Cach&eacute;, use the IP "loopback" address, 127.0.0.1.
  * For 1972, the default port, specify the super server port of your Cach&eacute; instance.
  * For USER, substitute the NAMESPACE which contains your Cach&eacute; database data.
  * <br>
  * <h2>CACH&Eacute; DIALECTS</h2>
  * Choices for Dialect are:
  * <br>
  * <p/>
  * <ol>
  * <li>org.hibernate.dialect.Cache71Dialect (requires Cach&eacute;
  * 2007.1 or above)</li>
  * <p/>
  * </ol>
  * <br>
  * <h2>SUPPORT FOR IDENTITY COLUMNS</h2>
  * Cach&eacute; 2007.1 or later supports identity columns.  For
  * Hibernate to use identity columns, specify "native" as the
  * generator.
  * <br>
  * <h2>SEQUENCE DIALECTS SUPPORT SEQUENCES</h2>
  * <p/>
  * To use Hibernate sequence support with Cach&eacute; in a namespace, you must FIRST load the following file into that namespace:
  * <pre>
  *     etc\CacheSequences.xml
  * </pre>
  * For example, at the COS terminal prompt in the namespace, run the
  * following command:
  * <p>
  * d LoadFile^%apiOBJ("c:\hibernate\etc\CacheSequences.xml","ck")
  * <p>
  * In your Hibernate mapping you can specify sequence use.
  * <p>
  * For example, the following shows the use of a sequence generator in a Hibernate mapping:
  * <pre>
  *     &lt;id name="id" column="uid" type="long" unsaved-value="null"&gt;
  *         &lt;generator class="sequence"/&gt;
  *     &lt;/id&gt;
  * </pre>
  * <br>
  * <p/>
  * Some versions of Hibernate under some circumstances call
  * getSelectSequenceNextValString() in the dialect.  If this happens
  * you will receive the error message: new MappingException( "Dialect
  * does not support sequences" ).
  * <br>
  * <h2>HIBERNATE FILES ASSOCIATED WITH CACH&Eacute; DIALECT</h2>
  * The following files are associated with Cach&eacute; dialect:
  * <p/>
  * <ol>
  * <li>src\org\hibernate\dialect\Cache71Dialect.java</li>
  * <li>src\org\hibernate\dialect\function\ConditionalParenthesisFunction.java</li>
  * <li>src\org\hibernate\dialect\function\ConvertFunction.java</li>
  * <li>src\org\hibernate\exception\CacheSQLStateConverter.java</li>
  * <li>src\org\hibernate\sql\CacheJoinFragment.java</li>
  * </ol>
  * Cache71Dialect ships with Hibernate 3.2.  All other dialects are distributed by InterSystems and subclass Cache71Dialect.
  *
  * @author Jonathan Levinson
  */
 
 public class Cache71Dialect extends Dialect {
 
-	private final TopLimitHandler limitHandler;
+	private LimitHandler limitHandler;
 
 	/**
 	 * Creates new <code>Cache71Dialect</code> instance. Sets up the JDBC /
 	 * Cach&eacute; type mappings.
 	 */
 	public Cache71Dialect() {
 		super();
 		commonRegistration();
 		register71Functions();
-		this.limitHandler = new TopLimitHandler(true, true);
+		this.limitHandler = new TopLimitHandler( true, true );
 	}
 
 	protected final void commonRegistration() {
 		// Note: For object <-> SQL datatype mappings see:
 		//	 Configuration Manager | Advanced | SQL | System DDL Datatype Mappings
 		//
 		//	TBD	registerColumnType(Types.BINARY,        "binary($1)");
 		// changed 08-11-2005, jsl
 		registerColumnType( Types.BINARY, "varbinary($1)" );
 		registerColumnType( Types.BIGINT, "BigInt" );
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.VARBINARY, "longvarbinary" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.BLOB, "longvarbinary" );
 		registerColumnType( Types.CLOB, "longvarchar" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", StandardBasicTypes.STRING ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
 		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "($length(?1)*8)" ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardJDBCEscapeFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "cos", new StandardJDBCEscapeFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardJDBCEscapeFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 		registerFunction( "convert", new ConvertFunction() );
 		registerFunction( "curdate", new StandardJDBCEscapeFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction(
 				"current_timestamp", new ConditionalParenthesisFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP )
 		);
 		registerFunction( "curtime", new StandardJDBCEscapeFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "database", new StandardJDBCEscapeFunction( "database", StandardBasicTypes.STRING ) );
 		registerFunction( "dateadd", new VarArgsSQLFunction( StandardBasicTypes.TIMESTAMP, "dateadd(", ",", ")" ) );
 		registerFunction( "datediff", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datediff(", ",", ")" ) );
 		registerFunction( "datename", new VarArgsSQLFunction( StandardBasicTypes.STRING, "datename(", ",", ")" ) );
 		registerFunction( "datepart", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datepart(", ",", ")" ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardJDBCEscapeFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardJDBCEscapeFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardJDBCEscapeFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardJDBCEscapeFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		// is it necessary to register %exact since it can only appear in a where clause?
 		registerFunction( "%exact", new StandardSQLFunction( "%exact", StandardBasicTypes.STRING ) );
 		registerFunction( "exp", new StandardJDBCEscapeFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%external", new StandardSQLFunction( "%external", StandardBasicTypes.STRING ) );
 		registerFunction( "$extract", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$extract(", ",", ")" ) );
 		registerFunction( "$find", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$find(", ",", ")" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "getdate", new StandardSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "hour", new StandardJDBCEscapeFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(", ",", ")" ) );
 		registerFunction( "%internal", new StandardSQLFunction( "%internal" ) );
 		registerFunction( "isnull", new VarArgsSQLFunction( "isnull(", ",", ")" ) );
 		registerFunction( "isnumeric", new StandardSQLFunction( "isnumeric", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lcase", new StandardJDBCEscapeFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardJDBCEscapeFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
 		registerFunction( "$length", new VarArgsSQLFunction( "$length(", ",", ")" ) );
 		registerFunction( "$list", new VarArgsSQLFunction( "$list(", ",", ")" ) );
 		registerFunction( "$listdata", new VarArgsSQLFunction( "$listdata(", ",", ")" ) );
 		registerFunction( "$listfind", new VarArgsSQLFunction( "$listfind(", ",", ")" ) );
 		registerFunction( "$listget", new VarArgsSQLFunction( "$listget(", ",", ")" ) );
 		registerFunction( "$listlength", new StandardSQLFunction( "$listlength", StandardBasicTypes.INTEGER ) );
 		registerFunction( "locate", new StandardSQLFunction( "$FIND", StandardBasicTypes.INTEGER ) );
 		registerFunction( "log", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "minute", new StandardJDBCEscapeFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "mod", new StandardJDBCEscapeFunction( "mod", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "month", new StandardJDBCEscapeFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "nullif", new VarArgsSQLFunction( "nullif(", ",", ")" ) );
 		registerFunction( "nvl", new NvlFunction() );
 		registerFunction( "%odbcin", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%odbcout", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%pattern", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%pattern", "" ) );
 		registerFunction( "pi", new StandardJDBCEscapeFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "$piece", new VarArgsSQLFunction( StandardBasicTypes.STRING, "$piece(", ",", ")" ) );
 		registerFunction( "position", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "position(", " in ", ")" ) );
 		registerFunction( "power", new VarArgsSQLFunction( StandardBasicTypes.STRING, "power(", ",", ")" ) );
 		registerFunction( "quarter", new StandardJDBCEscapeFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "repeat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "repeat(", ",", ")" ) );
 		registerFunction( "replicate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "replicate(", ",", ")" ) );
 		registerFunction( "right", new StandardJDBCEscapeFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "round", new VarArgsSQLFunction( StandardBasicTypes.FLOAT, "round(", ",", ")" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "second", new StandardJDBCEscapeFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardJDBCEscapeFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "%sqlstring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlstring(", ",", ")" ) );
 		registerFunction( "%sqlupper", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlupper(", ",", ")" ) );
 		registerFunction( "sqrt", new StandardJDBCEscapeFunction( "SQRT", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%startswith", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%startswith", "" ) );
 		// below is for Cache' that don't have str in 2007.1 there is str and we register str directly
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as char varying)" ) );
 		registerFunction( "string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "string(", ",", ")" ) );
 		// note that %string is deprecated
 		registerFunction( "%string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%string(", ",", ")" ) );
 		registerFunction( "substr", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substr(", ",", ")" ) );
 		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "tan", new StandardJDBCEscapeFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampadd", new StandardJDBCEscapeFunction( "timestampadd", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampdiff", new StandardJDBCEscapeFunction( "timestampdiff", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tochar", new VarArgsSQLFunction( StandardBasicTypes.STRING, "tochar(", ",", ")" ) );
 		registerFunction( "to_char", new VarArgsSQLFunction( StandardBasicTypes.STRING, "to_char(", ",", ")" ) );
 		registerFunction( "todate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "to_date", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "tonumber", new StandardSQLFunction( "tonumber" ) );
 		registerFunction( "to_number", new StandardSQLFunction( "tonumber" ) );
 		// TRIM(end_keyword string-expression-1 FROM string-expression-2)
 		// use Hibernate implementation "From" is one of the parameters they pass in position ?3
 		//registerFunction( "trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1 ?2 from ?3)") );
 		registerFunction( "truncate", new StandardJDBCEscapeFunction( "truncate", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardJDBCEscapeFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		// %upper is deprecated
 		registerFunction( "%upper", new StandardSQLFunction( "%upper" ) );
 		registerFunction( "user", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "week", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.INTEGER ) );
 		registerFunction( "xmlconcat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlconcat(", ",", ")" ) );
 		registerFunction( "xmlelement", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlelement(", ",", ")" ) );
 		// xmlforest requires a new kind of function constructor
 		registerFunction( "year", new StandardJDBCEscapeFunction( "year", StandardBasicTypes.INTEGER ) );
 	}
 
 	protected final void register71Functions() {
 		this.registerFunction( "str", new VarArgsSQLFunction( StandardBasicTypes.STRING, "str(", ",", ")" ) );
 	}
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean hasAlterTable() {
 		// Does this dialect support the ALTER TABLE syntax?
 		return true;
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		// Do we need to qualify index names with the schema name?
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("StringBufferReplaceableByString")
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		// The syntax used to add a foreign key constraint to a table.
 		return new StringBuilder( 300 )
 				.append( " ADD CONSTRAINT " )
 				.append( constraintName )
 				.append( " FOREIGN KEY " )
 				.append( constraintName )
 				.append( " (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") REFERENCES " )
 				.append( referencedTable )
 				.append( " (" )
 				.append( StringHelper.join( ", ", primaryKey ) )
 				.append( ") " )
 				.toString();
 	}
 
 	/**
 	 * Does this dialect support check constraints?
 	 *
 	 * @return {@code false} (Cache does not support check constraints)
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public boolean supportsCheck() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		// The syntax used to add a column to a table
 		return " add column";
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		// Completely optional cascading drop clause.
 		return "";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		// Do we need to drop constraints beforeQuery dropping tables in this dialect?
 		return true;
 	}
 
 	@Override
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	@Override
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new GlobalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						final String name = super.generateIdTableName( baseName );
 						return name.length() > 25 ? name.substring( 1, 25 ) : name;
 					}
 
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create global temporary table";
 					}
 				},
 				AfterUseAction.DROP
 		);
 	}
 
 	@Override
 	public String getNativeIdentifierGeneratorStrategy() {
 		return "identity";
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public IdentityColumnSupport getIdentityColumnSupport() {
 		return new Chache71IdentityColumnSupport();
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 // It really does support sequences, but InterSystems elects to suggest usage of IDENTITY instead :/
 // Anyway, below are the actual support overrides for users wanting to use this combo...
 //
 //	public String getSequenceNextValString(String sequenceName) {
 //		return "select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getSelectSequenceNextValString(String sequenceName) {
 //		return "(select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getCreateSequenceString(String sequenceName) {
 //		return "insert into InterSystems.Sequences(Name) values (ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getDropSequenceString(String sequenceName) {
 //		return "delete from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getQuerySequencesString() {
 //		return "select name from InterSystems.Sequences";
 //	}
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// InterSystems Cache' does not current support "SELECT ... FOR UPDATE" syntax...
 		// Set your transaction mode to READ_COMMITTED beforeQuery using
 		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC) {
 			return new OptimisticLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// LIMIT support (ala TOP) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public LimitHandler getLimitHandler() {
+		if ( isLegacyLimitHandlerBehaviorEnabled() ) {
+			return super.getLimitHandler();
+		}
 		return limitHandler;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean bindLimitParametersFirst() {
 		// Does the LIMIT clause come at the start of the SELECT statement, rather than at the end?
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean useMaxForLimit() {
 		// Does the LIMIT clause take a "maximum" row number instead of a total number of returned rows?
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hasOffset ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 
 		// This does not support the Cache SQL 'DISTINCT BY (comma-list)' extensions,
 		// but this extension is not supported through Hibernate anyway.
 		final int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
 
 		return new StringBuilder( sql.length() + 8 )
 				.append( sql )
 				.insert( insertionPoint, " TOP ? " )
 				.toString();
 	}
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getLowercaseFunction() {
 		// The name of the SQL function that transforms a string to lowercase
 		return "lower";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		// Create an OuterJoinGenerator for this dialect.
 		return new CacheJoinFragment();
 	}
 
 	@Override
 	public String getNoColumnsInsertString() {
 		// The keyword used to insert a row without specifying
 		// any column values
 		return " default values";
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new CacheSQLExceptionConversionDelegate( this );
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
 	 * The Cache ViolatedConstraintNameExtracter.
 	 */
 	public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
 		}
 	};
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
index 136f0b1437..dbedf85d25 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2390Dialect.java
@@ -1,97 +1,123 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import org.hibernate.dialect.identity.DB2390IdentityColumnSupport;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 
 
 /**
  * An SQL dialect for DB2/390. This class provides support for
  * DB2 Universal Database for OS/390, also known as DB2/390.
  *
  * @author Kristoffer Dyrkorn
  */
 public class DB2390Dialect extends DB2Dialect {
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			if (LimitHelper.hasFirstRow( selection )) {
 				throw new UnsupportedOperationException( "query result offset is not supported" );
 			}
 			return sql + " fetch first " + getMaxOrLimit( selection ) + " rows only";
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean supportsVariableLimit() {
 			return false;
 		}
 	};
 
+	private static final AbstractLimitHandler LEGACY_LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			return sql + " fetch first " + getMaxOrLimit( selection ) + " rows only";
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean useMaxForLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	};
+
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		if ( limit == 0 ) {
 			return sql;
 		}
 		return sql + " fetch first " + limit + " rows only ";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
-		return LIMIT_HANDLER;
+		if ( isLegacyLimitHandlerBehaviorEnabled() ) {
+			return LEGACY_LIMIT_HANDLER;
+		}
+		else {
+			return LIMIT_HANDLER;
+		}
 	}
 
 	@Override
 	public IdentityColumnSupport getIdentityColumnSupport() {
 		return new DB2390IdentityColumnSupport();
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index ea55b0a6da..e681d19919 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,1310 +1,1314 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.DatabaseMetaData;
 import java.sql.NClob;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.ScrollMode;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Sequence;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CastFunction;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
+import org.hibernate.engine.config.spi.ConfigurationService;
+import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.env.internal.DefaultSchemaNameResolver;
 import org.hibernate.engine.jdbc.env.spi.AnsiSqlKeywords;
 import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
 import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
 import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
 import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.persistent.PersistentTableBulkIdStrategy;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.loader.BatchLoadSizingStrategy;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Table;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
 import org.hibernate.tool.schema.internal.StandardAuxiliaryDatabaseObjectExporter;
 import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
 import org.hibernate.tool.schema.internal.StandardIndexExporter;
 import org.hibernate.tool.schema.internal.StandardSequenceExporter;
 import org.hibernate.tool.schema.internal.StandardTableExporter;
 import org.hibernate.tool.schema.internal.StandardUniqueKeyExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.  Subclasses implement Hibernate compatibility
  * with different systems.  Subclasses should provide a public default constructor that register a set of type
  * mappings and default Hibernate properties.  Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 @SuppressWarnings("deprecation")
 public abstract class Dialect implements ConversionContext {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( Dialect.class );
 
 	/**
 	 * Defines a default batch size constant
 	 */
 	public static final String DEFAULT_BATCH_SIZE = "15";
 
 	/**
 	 * Defines a "no batching" batch size constant
 	 */
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used as opening for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 
 	/**
 	 * Characters used as closing for quoting SQL identifiers
 	 */
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<>();
 	private final Set<String> sqlKeywords = new HashSet<>();
 
 	private final UniqueDelegate uniqueDelegate;
 
+	private boolean legacyLimitHandlerBehavior;
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
 		LOG.usingDialect( this );
 		StandardAnsiSqlAggregationFunctions.primeFunctionMap( sqlFunctions );
 
 		// standard sql92 functions (can be overridden by subclasses)
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1, ?2, ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "locate(?1, ?2, ?3)" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "cast", new CastFunction() );
 		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(?1 ?2 ?3)") );
 
 		//map second/minute/hour/day/month/year to ANSI extract(), override on subclasses
 		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(second from ?1)") );
 		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(minute from ?1)") );
 		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(hour from ?1)") );
 		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(day from ?1)") );
 		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(month from ?1)") );
 		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)") );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char)") );
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.FLOAT, "float($p)" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.LONGVARBINARY, "bit varying($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar($l)" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.NCHAR, "nchar($l)" );
 		registerColumnType( Types.NVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.LONGNVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.NCLOB, "nclob" );
 
 		// register hibernate types for default use in scalar sqlquery type auto detection
 		registerHibernateType( Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName() );
 		registerHibernateType( Types.BINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.BIT, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.CHAR, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.DATE, StandardBasicTypes.DATE.getName() );
 		registerHibernateType( Types.DOUBLE, StandardBasicTypes.DOUBLE.getName() );
 		registerHibernateType( Types.FLOAT, StandardBasicTypes.FLOAT.getName() );
 		registerHibernateType( Types.INTEGER, StandardBasicTypes.INTEGER.getName() );
 		registerHibernateType( Types.SMALLINT, StandardBasicTypes.SHORT.getName() );
 		registerHibernateType( Types.TINYINT, StandardBasicTypes.BYTE.getName() );
 		registerHibernateType( Types.TIME, StandardBasicTypes.TIME.getName() );
 		registerHibernateType( Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName() );
 		registerHibernateType( Types.VARCHAR, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.VARBINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName() );
 		registerHibernateType( Types.LONGVARBINARY, StandardBasicTypes.IMAGE.getName() );
 		registerHibernateType( Types.NUMERIC, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.BLOB, StandardBasicTypes.BLOB.getName() );
 		registerHibernateType( Types.CLOB, StandardBasicTypes.CLOB.getName() );
 		registerHibernateType( Types.REAL, StandardBasicTypes.FLOAT.getName() );
 
 		if(supportsPartitionBy()) {
 			registerKeyword( "PARTITION" );
 		}
 
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		return instantiateDialect( Environment.getProperties().getProperty( Environment.DIALECT ) );
 	}
 
 
 	/**
 	 * Get an instance of the dialect specified by the given properties or by
 	 * the current <tt>System</tt> properties.
 	 *
 	 * @param props The properties to use for finding the dialect class to use.
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect(Properties props) throws HibernateException {
 		final String dialectName = props.getProperty( Environment.DIALECT );
 		if ( dialectName == null ) {
 			return getDialect();
 		}
 		return instantiateDialect( dialectName );
 	}
 
 	private static Dialect instantiateDialect(String dialectName) throws HibernateException {
 		if ( dialectName == null ) {
 			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
 		}
 		try {
 			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
 		}
 	}
 
 	/**
 	 * Retrieve a set of default Hibernate properties for this database.
 	 *
 	 * @return a set of Hibernate properties
 	 */
 	public final Properties getDefaultProperties() {
 		return properties;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName();
 	}
 
 
 	// database type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Allows the Dialect to contribute additional types
 	 *
 	 * @param typeContributions Callback to contribute the types
 	 * @param serviceRegistry The service registry
 	 */
 	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
-		// by default, nothing to do
+		resolveLegacyLimitHandlerBehavior( serviceRegistry );
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		final String result = typeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode with the given storage specification
 	 * parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
 		final String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length )
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type appropriate for casting operations
 	 * (via the CAST() SQL function) for the given {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The database type name
 	 */
 	public String getCastTypeName(int code) {
 		return getTypeName( code, Column.DEFAULT_LENGTH, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_PRECISION} and
 	 * {@link Column#DEFAULT_SCALE} as the precision/scale.
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_LENGTH} as the length
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
 		return cast( value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, long capacity, String name) {
 		typeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
 	 * the type name with be replaced by the column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, String name) {
 		typeNames.put( code, name );
 	}
 
 	/**
 	 * Allows the dialect to override a {@link SqlTypeDescriptor}.
 	 * <p/>
 	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
 	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
 	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} does not allow itself to be
 	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
 	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
 	 * @see #getSqlTypeDescriptorOverride
 	 */
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( sqlTypeDescriptor == null ) {
 			throw new IllegalArgumentException( "sqlTypeDescriptor is null" );
 		}
 		if ( ! sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final SqlTypeDescriptor overridden = getSqlTypeDescriptorOverride( sqlTypeDescriptor.getSqlType() );
 		return overridden == null ? sqlTypeDescriptor : overridden;
 	}
 
 	/**
 	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
 	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.CLOB: {
 				descriptor = useInputStreamToInsertBlob() ? ClobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			default: {
 				descriptor = null;
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	/**
 	 * The legacy behavior of Hibernate.  LOBs are not processed by merge
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy LEGACY_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
 			if ( original != target ) {
 				try {
 					// the BLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setBinaryStream( 1L );
 					// the BLOB from the detached state
 					final InputStream detachedStream = original.getBinaryStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeBlob( original, target, session );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
 			if ( original != target ) {
 				try {
 					// the CLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the CLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob( original, target, session );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
 			if ( original != target ) {
 				try {
 					// the NCLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the NCLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator(
 						session
 				);
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getServiceRegistry().getService( JdbcServices.class ).getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		final String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Whether or not the given type name has been registered for this dialect (including both hibernate type names and
 	 * custom-registered type names).
 	 *
 	 * @param typeName the type name.
 	 *
 	 * @return true if the given string has been registered either as a hibernate type or as a custom-registered one
 	 */
 	public boolean isTypeNameRegistered(final String typeName) {
 		return this.typeNames.containsTypeName( typeName );
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		final String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format(
 							"No Hibernate type mapping for type [code=%s, length=%s]",
 							code,
 							length
 					)
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name );
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( name.toLowerCase( Locale.ROOT ), function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 * @deprecated use {@link #getNativeIdentifierGeneratorStrategy()} instead
 	 */
 	@Deprecated
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( getIdentityColumnSupport().supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else {
 			return SequenceStyleGenerator.class;
 		}
 	}
 
 	/**
 	 * Resolves the native generation strategy associated to this dialect.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator strategy.
 	 */
 	public String getNativeIdentifierGeneratorStrategy() {
 		if ( getIdentityColumnSupport().supportsIdentityColumns() ) {
 			return "identity";
 		}
 		else {
 			return "sequence";
 		}
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the appropriate {@link IdentityColumnSupport}
 	 *
 	 * @return the IdentityColumnSupport
 	 * @since 5.1
 	 */
 	public IdentityColumnSupport getIdentityColumnSupport(){
 		return new IdentityColumnSupportImpl();
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
 	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 	public SequenceInformationExtractor getSequenceInformationExtractor() {
 		if ( getQuerySequencesString() == null ) {
 			return SequenceInformationExtractorNoOpImpl.INSTANCE;
 		}
 		else {
 			return SequenceInformationExtractorLegacyImpl.INSTANCE;
 		}
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns the delegate managing LIMIT clause.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler getLimitHandler() {
 		return new LegacyLimitHandler( this );
 	}
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come beforeQuery other parameters
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Given a limit and an offset, apply the limit clause to the query.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param offset The offset of the limit
 	 * @param limit The limit of the limit ;)
 	 * @return The modified query statement with the limit applied.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getLimitString(String query, int offset, int limit) {
 		return getLimitString( query, ( offset > 0 || forceLimitUsage() )  );
 	}
 
 	/**
 	 * Apply s limit clause to the query.
 	 * <p/>
 	 * Typically dialects utilize {@link #supportsVariableLimit() variable}
 	 * limit clauses when they support limits.  Thus, when building the
 	 * select command we do not actually need to know the limit or the offest
 	 * since we will just be using placeholders.
 	 * <p/>
 	 * Here we do still pass along whether or not an offset was specified
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
 	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 * @return The corresponding db/dialect specific offset.
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		switch ( lockMode ) {
 			case PESSIMISTIC_FORCE_INCREMENT:
 				return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 			case PESSIMISTIC_WRITE:
 				return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
 			case PESSIMISTIC_READ:
 				return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
 			case OPTIMISTIC:
 				return new OptimisticLockingStrategy( lockable, lockMode );
 			case OPTIMISTIC_FORCE_INCREMENT:
 				return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 			default:
 				return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
 		final LockMode lockMode = lockOptions.getLockMode();
 		return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
 	@SuppressWarnings( {"deprecation"})
 	private String getForUpdateString(LockMode lockMode, int timeout){
 		switch ( lockMode ) {
 			case UPGRADE:
 				return getForUpdateString();
 			case PESSIMISTIC_READ:
 				return getReadLockString( timeout );
 			case PESSIMISTIC_WRITE:
 				return getWriteLockString( timeout );
 			case UPGRADE_NOWAIT:
 			case FORCE:
 			case PESSIMISTIC_FORCE_INCREMENT:
 				return getForUpdateNowaitString();
 			case UPGRADE_SKIPLOCKED:
 				return getForUpdateSkipLockedString();
 			default:
 				return "";
 		}
 	}
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect given the aliases of the columns to be write locked.
 	 * Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param aliases The columns to be read locked.
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(String aliases, int timeout) {
 		// by default we simply return the getWriteLockString(timeout) result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getWriteLockString( timeout );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire READ locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire READ locks
 	 * for this dialect given the aliases of the columns to be read locked.
 	 * Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param aliases The columns to be read locked.
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(String aliases, int timeout) {
 		// by default we simply return the getReadLockString(timeout) result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getReadLockString( timeout );
 	}
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
@@ -1862,1001 +1866,1016 @@ public abstract class Dialect implements ConversionContext {
 
 		builder.applyReservedWords( dbMetaData );
 		builder.applyReservedWords( AnsiSqlKeywords.INSTANCE.sql2003() );
 		builder.applyReservedWords( sqlKeywords );
 
 		builder.setNameQualifierSupport( getNameQualifierSupport() );
 
 		return builder.build();
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private StandardTableExporter tableExporter = new StandardTableExporter( this );
 	private StandardSequenceExporter sequenceExporter = new StandardSequenceExporter( this );
 	private StandardIndexExporter indexExporter = new StandardIndexExporter( this );
 	private StandardForeignKeyExporter foreignKeyExporter = new StandardForeignKeyExporter( this );
 	private StandardUniqueKeyExporter uniqueKeyExporter = new StandardUniqueKeyExporter( this );
 	private StandardAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter = new StandardAuxiliaryDatabaseObjectExporter( this );
 
 	public Exporter<Table> getTableExporter() {
 		return tableExporter;
 	}
 
 	public Exporter<Sequence> getSequenceExporter() {
 		return sequenceExporter;
 	}
 
 	public Exporter<Index> getIndexExporter() {
 		return indexExporter;
 	}
 
 	public Exporter<ForeignKey> getForeignKeyExporter() {
 		return foreignKeyExporter;
 	}
 
 	public Exporter<Constraint> getUniqueKeyExporter() {
 		return uniqueKeyExporter;
 	}
 
 	public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
 		return auxiliaryObjectExporter;
 	}
 
 	/**
 	 * Does this dialect support catalog creation?
 	 *
 	 * @return True if the dialect supports catalog creation; false otherwise.
 	 */
 	public boolean canCreateCatalog() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL command used to create the named catalog
 	 *
 	 * @param catalogName The name of the catalog to be created.
 	 *
 	 * @return The creation commands
 	 */
 	public String[] getCreateCatalogCommand(String catalogName) {
 		throw new UnsupportedOperationException( "No create catalog syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * Get the SQL command used to drop the named catalog
 	 *
 	 * @param catalogName The name of the catalog to be dropped.
 	 *
 	 * @return The drop commands
 	 */
 	public String[] getDropCatalogCommand(String catalogName) {
 		throw new UnsupportedOperationException( "No drop catalog syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * Does this dialect support schema creation?
 	 *
 	 * @return True if the dialect supports schema creation; false otherwise.
 	 */
 	public boolean canCreateSchema() {
 		return true;
 	}
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation commands
 	 */
 	public String[] getCreateSchemaCommand(String schemaName) {
 		return new String[] {"create schema " + schemaName};
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop commands
 	 */
 	public String[] getDropSchemaCommand(String schemaName) {
 		return new String[] {"drop schema " + schemaName};
 	}
 
 	/**
 	 * Get the SQL command used to retrieve the current schema name.  Works in conjunction
 	 * with {@link #getSchemaNameResolver()}, unless the return from there does not need this
 	 * information.  E.g., a custom impl might make use of the Java 1.7 addition of
 	 * the {@link java.sql.Connection#getSchema()} method
 	 *
 	 * @return The current schema retrieval SQL
 	 */
 	public String getCurrentSchemaCommand() {
 		return null;
 	}
 
 	/**
 	 * Get the strategy for determining the schema name of a Connection
 	 *
 	 * @return The schema name resolver strategy
 	 */
 	public SchemaNameResolver getSchemaNameResolver() {
 		return DefaultSchemaNameResolver.INSTANCE;
 	}
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints beforeQuery dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * The syntax for the suffix used to add a column to a table (optional).
 	 *
 	 * @return The suffix "add column" fragment.
 	 */
 	public String getAddColumnSuffixString() {
 		return "";
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( quote( constraintName ) )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String foreignKeyDefinition) {
 		return new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( quote( constraintName ) )
 				.append( " " )
 				.append( foreignKeyDefinition )
 				.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
 	/**
 	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
 	 *
 	 * @return {@code true} if the database/driver has this bug
 	 */
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	/**
 	 * Does this dialect/database support commenting on tables, columns, etc?
 	 *
 	 * @return {@code true} if commenting is supported
 	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	/**
 	 * Get the comment into a form supported for table definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * Get the comment into a form supported for column definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied beforeQuery the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied beforeQuery the table name
 	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied afterQuery the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied afterQuery the table name
 	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied beforeQuery the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied beforeQuery the constraint name
 	 */
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied afterQuery the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied afterQuery the constraint name
 	 */
 	public boolean supportsIfExistsAfterConstraintName() {
 		return false;
 	}
 
 	/**
 	 * Generate a DROP TABLE statement
 	 *
 	 * @param tableName The name of the table to drop
 	 *
 	 * @return The DROP TABLE command
 	 */
 	public String getDropTableString(String tableName) {
 		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support cascaded delete on foreign key definitions?
 	 *
 	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
 	 */
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 * @return The cross join separator
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Renders an ordering fragment
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase( Locale.ROOT ) );
 		}
 		return orderByElement.toString();
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support `count(distinct a,b)`?
 	 *
 	 * @return True if the database supports counting distinct tuples; false otherwise.
 	 */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 
 	/**
 	 * If {@link #supportsTupleDistinctCounts()} is true, does the Dialect require the tuple to be wrapped with parens?
 	 *
 	 * @return boolean
 	 */
 	public boolean requiresParensForTupleDistinctCounts() {
 		return false;
 	}
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
 	 *
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
 
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
 	 *
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 * @deprecated Use {@link #useFollowOnLocking(QueryParameters)} instead.
 	 */
 	@Deprecated
 	public boolean useFollowOnLocking() {
 		return useFollowOnLocking( null );
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @param parameters query parameters
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 * @since 5.2
 	 */
 	public boolean useFollowOnLocking(QueryParameters parameters) {
 		return false;
 	}
 
 	/**
 	 * Negate an expression
 	 *
 	 * @param expression The expression to negate
 	 *
 	 * @return The negated expression
 	 */
 	public String getNotExpression(String expression) {
 		return "not " + expression;
 	}
 
 	/**
 	 * Get the UniqueDelegate supported by this dialect
 	 *
 	 * @return The UniqueDelegate
 	 */
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUnique() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support adding Unique constraints via create and alter table ?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a unique constraint to a table.
 	 *
 	 * @param constraintName The name of the unique constraint.
 	 * @return The "add unique" fragment
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getAddUniqueConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " unique ";
 	}
 
 	/**
 	 * Is the combination of not-null and unique supported?
 	 *
 	 * @return deprecated
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 
 	/**
 	 * Apply a hint to the query.  The entire query is provided, allowing the Dialect full control over the placement
 	 * and syntax of the hint.  By default, ignore the hint and simply return the query.
 	 *
 	 * @param query The query to which to apply the hint.
 	 * @param hints The  hints to apply
 	 * @return The modified SQL
 	 */
 	public String getQueryHintString(String query, List<String> hints) {
 		return query;
 	}
 
 	/**
 	 * Certain dialects support a subset of ScrollModes.  Provide a default to be used by Criteria and Query.
 	 *
 	 * @return ScrollMode
 	 */
 	public ScrollMode defaultScrollMode() {
 		return ScrollMode.SCROLL_INSENSITIVE;
 	}
 
 	/**
 	 * Does this dialect support tuples in subqueries?  Ex:
 	 * delete from Table1 where (col1, col2) in (select col1, col2 from Table2)
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsTuplesInSubqueries() {
 		return true;
 	}
 
 	public CallableStatementSupport getCallableStatementSupport() {
 		// most databases do not support returning cursors (ref_cursor)...
 		return StandardCallableStatementSupport.NO_REF_CURSOR_INSTANCE;
 	}
 
 	/**
 	 * By default interpret this based on DatabaseMetaData.
 	 *
 	 * @return
 	 */
 	public NameQualifierSupport getNameQualifierSupport() {
 		return null;
 	}
 
 	protected final BatchLoadSizingStrategy STANDARD_DEFAULT_BATCH_LOAD_SIZING_STRATEGY = new BatchLoadSizingStrategy() {
 		@Override
 		public int determineOptimalBatchLoadSize(int numberOfKeyColumns, int numberOfKeys) {
 			return 50;
 		}
 	};
 
 	public BatchLoadSizingStrategy getDefaultBatchLoadSizingStrategy() {
 		return STANDARD_DEFAULT_BATCH_LOAD_SIZING_STRATEGY;
 	}
 
 	/**
 	 * Does the fetching JDBC statement warning for logging is enabled by default
 	 *
 	 * @return boolean
 	 *
 	 * @since 5.1
 	 */
 	public boolean isJdbcLogWarningsEnabledByDefault() {
 		return true;
 	}
 
 	public void augmentRecognizedTableTypes(List<String> tableTypesList) {
 		// noihing to do
 	}
 
 	/**
 	 * Does the underlying database support partition by
 	 *
 	 * @return boolean
 	 *
 	 * @since 5.2
 	 */
 	public boolean supportsPartitionBy() {
 		return false;
 	}
 
 	/**
 	 * Override the DatabaseMetaData#supportsNamedParameters()
 	 *
 	 * @return boolean
 	 *
 	 * @throws SQLException Accessing the DatabaseMetaData can throw it.  Just re-throw and Hibernate will handle.
 	 */
 	public boolean supportsNamedParameters(DatabaseMetaData databaseMetaData) throws SQLException {
 		return databaseMetaData != null && databaseMetaData.supportsNamedParameters();
 	}
 
 	/**
 	 * Does this dialect supports Nationalized Types
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsNationalizedTypes() {
 		return true;
 	}
+
+	public boolean isLegacyLimitHandlerBehaviorEnabled() {
+		return legacyLimitHandlerBehavior;
+	}
+
+	private void resolveLegacyLimitHandlerBehavior(ServiceRegistry serviceRegistry) {
+		// HHH-11194
+		// Temporary solution to set whether legacy limit handler behavior should be used.
+		final ConfigurationService configurationService = serviceRegistry.getService( ConfigurationService.class );
+		legacyLimitHandlerBehavior = configurationService.getSetting(
+				AvailableSettings.USE_LEGACY_LIMIT_HANDLERS,
+				StandardConverters.BOOLEAN,
+				false
+		);
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
index 11319238a6..7ef27c98d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
@@ -1,305 +1,309 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.identity.InformixIdentityColumnSupport;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
+import org.hibernate.dialect.pagination.LegacyFirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.InformixUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * Informix dialect.<br>
  * <br>
  * Seems to work with Informix Dynamic Server Version 7.31.UD3,  Informix JDBC driver version 2.21JC3.
  *
  * @author Steve Molitor
  */
 public class InformixDialect extends Dialect {
 	
 	private final UniqueDelegate uniqueDelegate;
 
 	/**
 	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
 	 * Informix type mappings.
 	 */
 	public InformixDialect() {
 		super();
 
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.BINARY, "byte" );
 		// Informix doesn't have a bit type
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.FLOAT, "smallfloat" );
 		registerColumnType( Types.INTEGER, "integer" );
 		// or BYTE
 		registerColumnType( Types.LONGVARBINARY, "blob" );
 		// or TEXT?
 		registerColumnType( Types.LONGVARCHAR, "clob" );
 		// or MONEY
 		registerColumnType( Types.NUMERIC, "decimal" );
 		registerColumnType( Types.REAL, "smallfloat" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "datetime year to fraction(5)" );
 		registerColumnType( Types.TIME, "datetime hour to second" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.VARBINARY, "byte" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 32739, "lvarchar($l)" );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "substring", new SQLFunctionTemplate(StandardBasicTypes.STRING, "substring(?1 FROM ?2 FOR ?3)"));
 		registerFunction( "substr", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substr(?1, ?2, ?3)"));
 		registerFunction( "coalesce", new NvlFunction());
 		registerFunction( "nvl", new NvlFunction());
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "today", StandardBasicTypes.DATE, false ) );
 
 		uniqueDelegate = new InformixUniqueDelegate( this );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder result = new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			result.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		result.append( " constraint " ).append( constraintName );
 
 		return result.toString();
 	}
 
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String foreignKeyDefinition) {
 		return new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( foreignKeyDefinition )
 				.append( " constraint " )
 				.append( constraintName )
 				.toString();
 	}
 
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint primary key constraint " + constraintName + " ";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from informix.systables where tabid=1";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select tabname from informix.systables where tabtype='Q'";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
+		if ( isLegacyLimitHandlerBehaviorEnabled() ) {
+			return LegacyFirstLimitHandler.INSTANCE;
+		}
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( querySelect.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -268 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == -691 ) {
 				constraintName = extractUsingTemplate(
 						"Missing key in referenced table for referential constraint (",
 						").",
 						sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -692 ) {
 				constraintName = extractUsingTemplate(
 						"Key value for constraint (",
 						") is still being referenced.",
 						sqle.getMessage()
 				);
 			}
 
 			if ( constraintName != null ) {
 				// strip table-owner because Informix always returns constraint names as "<table-owner>.<constraint-name>"
 				final int i = constraintName.indexOf( '.' );
 				if ( i != -1 ) {
 					constraintName = constraintName.substring( i + 1 );
 				}
 			}
 
 			return constraintName;
 		}
 
 	};
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select distinct current timestamp from informix.systables";
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create temp table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "with no log";
 					}
 				},
 				AfterUseAction.CLEAN,
 				null
 		);
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	@Override
 	public IdentityColumnSupport getIdentityColumnSupport() {
 		return new InformixIdentityColumnSupport();
 	}
 
 	@Override
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "'t'" : "'f'";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
index 18aae55f91..b474cd6270 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
@@ -1,307 +1,312 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
+import org.hibernate.dialect.pagination.LegacyFirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Ingres 9.2.
  * <p/>
  * Known limitations: <ul>
  *     <li>
  *         Only supports simple constants or columns on the left side of an IN,
  *         making {@code (1,2,3) in (...)} or {@code (subselect) in (...)} non-supported.
  *     </li>
  *     <li>
  *         Supports only 39 digits in decimal.
  *     </li>
  *     <li>
  *         Explicitly set USE_GET_GENERATED_KEYS property to false.
  *     </li>
  *     <li>
  *         Perform string casts to varchar; removes space padding.
  *     </li>
  * </ul>
  * 
  * @author Ian Booth
  * @author Bruce Lunsford
  * @author Max Rydahl Andersen
  * @author Raymond Fan
  */
 @SuppressWarnings("deprecation")
 public class IngresDialect extends Dialect {
+
 	/**
 	 * Constructs a IngresDialect
 	 */
 	public IngresDialect() {
 		super();
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.NUMERIC, "decimal($p, $s)" );
 		registerColumnType( Types.DECIMAL, "decimal($p, $s)" );
 		registerColumnType( Types.BINARY, 32000, "byte($l)" );
 		registerColumnType( Types.BINARY, "long byte" );
 		registerColumnType( Types.VARBINARY, 32000, "varbyte($l)" );
 		registerColumnType( Types.VARBINARY, "long byte" );
 		registerColumnType( Types.LONGVARBINARY, "long byte" );
 		registerColumnType( Types.CHAR, 32000, "char($l)" );
 		registerColumnType( Types.VARCHAR, 32000, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, "long varchar" );
 		registerColumnType( Types.LONGVARCHAR, "long varchar" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time with time zone" );
 		registerColumnType( Types.TIMESTAMP, "timestamp with time zone" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_add", new StandardSQLFunction( "bit_add" ) );
 		registerFunction( "bit_and", new StandardSQLFunction( "bit_and" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "octet_length(hex(?1))*4" ) );
 		registerFunction( "bit_not", new StandardSQLFunction( "bit_not" ) );
 		registerFunction( "bit_or", new StandardSQLFunction( "bit_or" ) );
 		registerFunction( "bit_xor", new StandardSQLFunction( "bit_xor" ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
 		registerFunction( "charextract", new StandardSQLFunction( "charextract", StandardBasicTypes.STRING ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "+", ")" ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "current_user", new NoArgSQLFunction( "current_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dba", new NoArgSQLFunction( "dba", StandardBasicTypes.STRING, true ) );
 		registerFunction( "dow", new StandardSQLFunction( "dow", StandardBasicTypes.STRING ) );
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "date_part('?1', ?3)" ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "gmt_timestamp", new StandardSQLFunction( "gmt_timestamp", StandardBasicTypes.STRING ) );
 		registerFunction( "hash", new StandardSQLFunction( "hash", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "initial_user", new NoArgSQLFunction( "initial_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "intextract", new StandardSQLFunction( "intextract", StandardBasicTypes.INTEGER ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.LONG, "locate(?1, ?2)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.LONG ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "lowercase", new StandardSQLFunction( "lowercase" ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
 		registerFunction( "pad", new StandardSQLFunction( "pad", StandardBasicTypes.STRING ) );
 		registerFunction( "position", new StandardSQLFunction( "position", StandardBasicTypes.LONG ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "random", new NoArgSQLFunction( "random", StandardBasicTypes.LONG, true ) );
 		registerFunction( "randomf", new NoArgSQLFunction( "randomf", StandardBasicTypes.DOUBLE, true ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "session_user", new NoArgSQLFunction( "session_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "size", new NoArgSQLFunction( "size", StandardBasicTypes.LONG, true ) );
 		registerFunction( "squeeze", new StandardSQLFunction( "squeeze" ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1 FROM ?2 FOR ?3)" ) );
 		registerFunction( "system_user", new NoArgSQLFunction( "system_user", StandardBasicTypes.STRING, false ) );
 		//registerFunction( "trim", new StandardSQLFunction( "trim", StandardBasicTypes.STRING ) );
 		registerFunction( "unhex", new StandardSQLFunction( "unhex", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "uppercase", new StandardSQLFunction( "uppercase" ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "usercode", new NoArgSQLFunction( "usercode", StandardBasicTypes.STRING, true ) );
 		registerFunction( "username", new NoArgSQLFunction( "username", StandardBasicTypes.STRING, true ) );
 		registerFunction( "uuid_create", new StandardSQLFunction( "uuid_create", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_compare", new StandardSQLFunction( "uuid_compare", StandardBasicTypes.INTEGER ) );
 		registerFunction( "uuid_from_char", new StandardSQLFunction( "uuid_from_char", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_to_char", new StandardSQLFunction( "uuid_to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		// Casting to char of numeric values introduces space padding up to the
 		// maximum width of a value for that return type.  Casting to varchar
 		// does not introduce space padding.
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
 		// Ingres driver supports getGeneratedKeys but only in the following
 		// form:
 		// The Ingres DBMS returns only a single table key or a single object
 		// key per insert statement. Ingres does not return table and object
 		// keys for INSERT AS SELECT statements. Depending on the keys that are
 		// produced by the statement executed, auto-generated key parameters in
 		// execute(), executeUpdate(), and prepareStatement() methods are
 		// ignored and getGeneratedKeys() returns a result-set containing no
 		// rows, a single row with one column, or a single row with two columns.
 		// Ingres JDBC Driver returns table and object keys as BINARY values.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 		// There is no support for a native boolean type that accepts values
 		// of true, false or unknown. Using the tinyint type requires
 		// substitions of true and false.
 		getDefaultProperties().setProperty( Environment.QUERY_SUBSTITUTIONS, "true=1,false=0" );
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select uuid_to_char(uuid_create())";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		return " with null";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select nextval for " + sequenceName;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select seq_name from iisequence";
 	}
 
 	@Override
 	public String getLowercaseFunction() {
 		return "lowercase";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
+		if ( isLegacyLimitHandlerBehaviorEnabled() ) {
+			return LegacyFirstLimitHandler.INSTANCE;
+		}
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 16 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new GlobalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						return "session." + super.generateIdTableName( baseName );
 					}
 
 					@Override
 					public String getCreateIdTableCommand() {
 						return "declare global temporary table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "on commit preserve rows with norecovery";
 					}
 				},
 				AfterUseAction.CLEAN
 		);
 	}
 
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "date(now)";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsSubselectAsInPredicateLHS() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
index a7f236fc90..cba477cc57 100755
--- a/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/RDMSOS2200Dialect.java
@@ -1,403 +1,428 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.jboss.logging.Logger;
 
 /**
  * This is the Hibernate dialect for the Unisys 2200 Relational Database (RDMS).
  * This dialect was developed for use with Hibernate 3.0.5. Other versions may
  * require modifications to the dialect.
  * <p/>
  * Version History:
  * Also change the version displayed below in the constructor
  * 1.1
  * 1.0  2005-10-24  CDH - First dated version for use with CP 11
  *
  * @author Ploski and Hanson
  */
 @SuppressWarnings("deprecation")
 public class RDMSOS2200Dialect extends Dialect {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			RDMSOS2200Dialect.class.getName()
 	);
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			if (hasOffset) {
 				throw new UnsupportedOperationException( "query result offset is not supported" );
 			}
 			return sql + " fetch first " + getMaxOrLimit( selection ) + " rows only ";
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean supportsLimitOffset() {
 			return false;
 		}
 
 		@Override
 		public boolean supportsVariableLimit() {
 			return false;
 		}
 	};
 
+	private static final AbstractLimitHandler LEGACY_LIMIT_HANDLER = new AbstractLimitHandler() {
+		@Override
+		public String processSql(String sql, RowSelection selection) {
+			return sql + " fetch first " + getMaxOrLimit( selection ) + " rows only ";
+		}
+
+		@Override
+		public boolean supportsLimit() {
+			return true;
+		}
+
+		@Override
+		public boolean supportsLimitOffset() {
+			return false;
+		}
+
+		@Override
+		public boolean supportsVariableLimit() {
+			return false;
+		}
+	};
+
 	/**
 	 * Constructs a RDMSOS2200Dialect
 	 */
 	public RDMSOS2200Dialect() {
 		super();
 		// Display the dialect version.
 		LOG.rdmsOs2200Dialect();
 
 		/**
 		 * This section registers RDMS Built-in Functions (BIFs) with Hibernate.
 		 * The first parameter is the 'register' function name with Hibernate.
 		 * The second parameter is the defined RDMS SQL Function and it's
 		 * characteristics. If StandardSQLFunction(...) is used, the RDMS BIF
 		 * name and the return type (if any) is specified.  If
 		 * SQLFunctionTemplate(...) is used, the return type and a template
 		 * string is provided, plus an optional hasParenthesesIfNoArgs flag.
 		 */
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
 
 		// The RDMS concat() function only supports 2 parameters
 		registerFunction( "concat", new SQLFunctionTemplate( StandardBasicTypes.STRING, "concat(?1, ?2)" ) );
 		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.STRING ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 
 		// RDMS does not directly support the trim() function, we use rtrim() and ltrim()
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "ltrim(rtrim(?1))" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "initcap", new StandardSQLFunction( "initcap" ) );
 
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "days", new StandardSQLFunction( "days", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 
 		/**
 		 * For a list of column types to register, see section A-1
 		 * in 7862 7395, the Unisys JDBC manual.
 		 *
 		 * Here are column sizes as documented in Table A-1 of
 		 * 7831 0760, "Enterprise Relational Database Server
 		 * for ClearPath OS2200 Administration Guide"
 		 * Numeric - 21
 		 * Decimal - 22 (21 digits plus one for sign)
 		 * Float   - 60 bits
 		 * Char    - 28000
 		 * NChar   - 14000
 		 * BLOB+   - 4294967296 (4 Gb)
 		 * + RDMS JDBC driver does not support BLOBs
 		 *
 		 * DATE, TIME and TIMESTAMP literal formats are
 		 * are all described in section 2.3.4 DATE Literal Format
 		 * in 7830 8160.
 		 * The DATE literal format is: YYYY-MM-DD
 		 * The TIME literal format is: HH:MM:SS[.[FFFFFF]]
 		 * The TIMESTAMP literal format is: YYYY-MM-DD HH:MM:SS[.[FFFFFF]]
 		 *
 		 * Note that $l (dollar-L) will use the length value if provided.
 		 * Also new for Hibernate3 is the $p percision and $s (scale) parameters
 		 */
 		registerColumnType( Types.BIT, "SMALLINT" );
 		registerColumnType( Types.TINYINT, "SMALLINT" );
 		registerColumnType( Types.BIGINT, "NUMERIC(21,0)" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.CHAR, "CHARACTER(1)" );
 		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.REAL, "REAL" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.NUMERIC, "NUMERIC(21,$l)" );
 		registerColumnType( Types.DECIMAL, "NUMERIC(21,$l)" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.VARCHAR, "CHARACTER($l)" );
 		registerColumnType( Types.BLOB, "BLOB($l)" );
 		/*
          * The following types are not supported in RDMS/JDBC and therefore commented out.
          * However, in some cases, mapping them to CHARACTER columns works
          * for many applications, but does not work for all cases.
          */
 		// registerColumnType(Types.VARBINARY, "CHARACTER($l)");
 		// registerColumnType(Types.BLOB, "CHARACTER($l)" );  // For use prior to CP 11.0
 		// registerColumnType(Types.CLOB, "CHARACTER($l)" );
 	}
 
 
 	// Dialect method overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * RDMS does not support qualifing index names with the schema name.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	/**
 	 * The RDMS DB supports the 'FOR UPDATE OF' clause. However, the RDMS-JDBC
 	 * driver does not support this feature, so a false is return.
 	 * The base dialect also returns a false, but we will leave this over-ride
 	 * in to make sure it stays false.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean forUpdateOfColumns() {
 		return false;
 	}
 
 	/**
 	 * Since the RDMS-JDBC driver does not support for updates, this string is
 	 * set to an empty string. Whenever, the driver does support this feature,
 	 * the returned string should be " FOR UPDATE OF". Note that RDMS does not
 	 * support the string 'FOR UPDATE' string.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getForUpdateString() {
 		// Original Dialect.java returns " for update";
 		return "";
 	}
 
 	// Verify the state of this new method in Hibernate 3.0 Dialect.java
 
 	/**
 	 * RDMS does not support Cascade Deletes.
 	 * Need to review this in the future when support is provided.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
 	/**
 	 * Currently, RDMS-JDBC does not support ForUpdate.
 	 * Need to review this in the future when support is provided.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		// The where clause was added to eliminate this statement from Brute Force Searches.
 		return "select permuted_id('NEXT',31) from rdms.rdms_dummy where key_col = 1 ";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		// We must return a valid RDMS/RSA command from this method to
 		// prevent RDMS/RSA from issuing *ERROR 400
 		return "";
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		// We must return a valid RDMS/RSA command from this method to
 		// prevent RDMS/RSA from issuing *ERROR 400
 		return "";
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		// Used with DROP TABLE to delete all records in the table.
 		return " including contents";
 	}
 
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
+		if ( isLegacyLimitHandlerBehaviorEnabled() ) {
+			return LEGACY_LIMIT_HANDLER;
+		}
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return sql + " fetch first " + limit + " rows only ";
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		// RDMS supports the UNION ALL clause.
 		return true;
 	}
 
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// RDMS has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
index 22eae36a52..47e20bc50f 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
@@ -1,204 +1,208 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.identity.SQLServerIdentityColumnSupport;
+import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.TopLimitHandler;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Microsoft SQL Server 2000
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class SQLServerDialect extends AbstractTransactSQLDialect {
 	private static final int PARAM_LIST_SIZE_LIMIT = 2100;
 
 	private final LimitHandler limitHandler;
 
 	/**
 	 * Constructs a SQLServerDialect
 	 */
 	public SQLServerDialect() {
 		registerColumnType( Types.VARBINARY, "image" );
 		registerColumnType( Types.VARBINARY, 8000, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "image" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.BOOLEAN, "bit" );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(second, ?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(minute, ?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(hour, ?1)" ) );
 		registerFunction( "locate", new StandardSQLFunction( "charindex", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datepart(?1, ?3)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1 % ?2" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "datalength(?1) * 8" ) );
 
 		registerFunction( "trim", new AnsiTrimEmulationFunction() );
 
 		registerKeyword( "top" );
 		registerKeyword( "key" );
 
 		this.limitHandler = new TopLimitHandler( false, false );
 	}
 
 	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	static int getAfterSelectInsertPoint(String sql) {
 		final int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select" );
 		final int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select distinct" );
 		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( getAfterSelectInsertPoint( querySelect ), " top " + limit )
 				.toString();
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
+		if ( isLegacyLimitHandlerBehaviorEnabled() ) {
+			return new LegacyLimitHandler( this );
+		}
 		return limitHandler;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public char closeQuote() {
 		return ']';
 	}
 
 	@Override
 	public char openQuote() {
 		return '[';
 	}
 
 	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
 		final LockMode mode = lockOptions.getLockMode();
 		switch ( mode ) {
 			case UPGRADE:
 			case UPGRADE_NOWAIT:
 			case PESSIMISTIC_WRITE:
 			case WRITE:
 				return tableName + " with (updlock, rowlock)";
 			case PESSIMISTIC_READ:
 				return tableName + " with (holdlock, rowlock)";
 			case UPGRADE_SKIPLOCKED:
 				return tableName + " with (updlock, rowlock, readpast)";
 			default:
 				return tableName;
 		}
 	}
 
 
 	/**
 	 * The current_timestamp is more accurate, but only known to be supported in SQL Server 7.0 and later and
 	 * Sybase not known to support it at all
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select current_timestamp";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		// SQL Server (at least up through 2005) does not support defining
 		// cascade delete constraints which can circle back to the mutating
 		// table
 		return false;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		// note: at least my local SQL Server 2005 Express shows this not working...
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		// here assume SQLServer2005 using snapshot isolation, which does not have this problem
 		return false;
 	}
 
 	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		// here assume SQLServer2005 using snapshot isolation, which does not have this problem
 		return false;
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.TINYINT ?
 				SmallIntTypeDescriptor.INSTANCE :
 				super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 
 	@Override
 	public IdentityColumnSupport getIdentityColumnSupport() {
 		return new SQLServerIdentityColumnSupport();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
index 563c8fa8dd..41e016f942 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
@@ -1,267 +1,270 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
+import org.hibernate.dialect.pagination.LegacyFirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for TimesTen 5.1.
  * <p/>
  * Known limitations:
  * joined-subclass support because of no CASE support in TimesTen
  * No support for subqueries that includes aggregation
  * - size() in HQL not supported
  * - user queries that does subqueries with aggregation
  * No CLOB/BLOB support
  * No cascade delete support.
  * No Calendar support
  * No support for updating primary keys.
  *
  * @author Sherry Listgarten and Max Andersen
  */
 @SuppressWarnings("deprecation")
 public class TimesTenDialect extends Dialect {
 	/**
 	 * Constructs a TimesTenDialect
 	 */
 	public TimesTenDialect() {
 		super();
 		registerColumnType( Types.BIT, "TINYINT" );
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.TINYINT, "TINYINT" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.CHAR, "CHAR(1)" );
 		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.DOUBLE, "DOUBLE" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.VARBINARY, "VARBINARY($l)" );
 		registerColumnType( Types.NUMERIC, "DECIMAL($p, $s)" );
 		// TimesTen has no BLOB/CLOB support, but these types may be suitable 
 		// for some applications. The length is limited to 4 million bytes.
 		registerColumnType( Types.BLOB, "VARBINARY(4000000)" );
 		registerColumnType( Types.CLOB, "VARCHAR(4000000)" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod" ) );
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "getdate", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
-
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select first 1 " + sequenceName + ".nextval from sys.tables";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select NAME from sys.sequences";
 	}
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
 	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
+		if ( isLegacyLimitHandlerBehaviorEnabled() ) {
+			return LegacyFirstLimitHandler.INSTANCE;
+		}
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select first 1 sysdate from sys.tables";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new GlobalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						final String name = super.generateIdTableName( baseName );
 						return name.length() > 30 ? name.substring( 1, 30 ) : name;
 					}
 
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create global temporary table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "on commit delete rows";
 					}
 				},
 				AfterUseAction.CLEAN
 		);
 	}
 
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// TimesTen has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/FirstLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/FirstLimitHandler.java
index e18379e830..72af896aaa 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/FirstLimitHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/FirstLimitHandler.java
@@ -1,55 +1,31 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect.pagination;
 
-import java.util.Locale;
 import org.hibernate.engine.spi.RowSelection;
 
 
 /**
  * @author Brett Meyer
  */
-public class FirstLimitHandler extends AbstractLimitHandler {
+public class FirstLimitHandler extends LegacyFirstLimitHandler {
 
 	public static final FirstLimitHandler INSTANCE = new FirstLimitHandler();
 
 	private FirstLimitHandler() {
 		// NOP
 	}
 	
 	@Override
 	public String processSql(String sql, RowSelection selection) {
 		final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 		if ( hasOffset ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
-		return new StringBuilder( sql.length() + 16 )
-				.append( sql )
-				.insert( sql.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6, " first " + getMaxOrLimit( selection ) )
-				.toString();
-	}
-
-	@Override
-	public boolean supportsLimit() {
-		return true;
-	}
-
-	@Override
-	public boolean useMaxForLimit() {
-		return true;
-	}
-
-	@Override
-	public boolean supportsLimitOffset() {
-		return false;
-	}
-
-	@Override
-	public boolean supportsVariableLimit() {
-		return false;
+		return super.processSql( sql, selection );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyFirstLimitHandler.java b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyFirstLimitHandler.java
new file mode 100644
index 0000000000..dbf6dca837
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/pagination/LegacyFirstLimitHandler.java
@@ -0,0 +1,51 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect.pagination;
+
+import java.util.Locale;
+
+import org.hibernate.engine.spi.RowSelection;
+
+/**
+ * @author Chris Cranford
+ */
+public class LegacyFirstLimitHandler extends AbstractLimitHandler {
+
+	public static final LegacyFirstLimitHandler INSTANCE = new LegacyFirstLimitHandler();
+
+	LegacyFirstLimitHandler() {
+		// NOP
+	}
+
+	@Override
+	public String processSql(String sql, RowSelection selection) {
+		return new StringBuilder( sql.length() + 16 )
+				.append( sql )
+				.insert( sql.toLowerCase( Locale.ROOT).indexOf( "select" ) + 6, " first " + getMaxOrLimit( selection ) )
+				.toString();
+	}
+
+	@Override
+	public boolean supportsLimit() {
+		return true;
+	}
+
+	@Override
+	public boolean useMaxForLimit() {
+		return true;
+	}
+
+	@Override
+	public boolean supportsLimitOffset() {
+		return false;
+	}
+
+	@Override
+	public boolean supportsVariableLimit() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/dialect/LegacyLimitHandlerTestCase.java b/hibernate-core/src/test/java/org/hibernate/dialect/LegacyLimitHandlerTestCase.java
new file mode 100644
index 0000000000..25ea4e0a25
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/dialect/LegacyLimitHandlerTestCase.java
@@ -0,0 +1,87 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.dialect;
+
+import java.util.Map;
+
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.engine.spi.RowSelection;
+
+import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.RequiresDialects;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.junit.Assert.assertEquals;
+
+/**
+ * @author Chris Cranford
+ */
+public class LegacyLimitHandlerTestCase extends
+		BaseNonConfigCoreFunctionalTestCase {
+
+	private final String TEST_SQL = "SELECT field FROM table";
+
+	@Override
+	protected void addSettings(Map settings) {
+		settings.put( AvailableSettings.USE_LEGACY_LIMIT_HANDLERS, "true" );
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-11194")
+	@RequiresDialect(Cache71Dialect.class)
+	public void testCache71DialectLegacyLimitHandler() {
+		assertLimitHandlerEquals( "SELECT TOP ?  field FROM table" );
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-11194")
+	@RequiresDialect(DB2390Dialect.class)
+	public void testDB2390DialectLegacyLimitHandler() {
+		assertLimitHandlerEquals( "SELECT field FROM table fetch first 6 rows only" );
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-11194")
+	@RequiresDialects({ @RequiresDialect(InformixDialect.class), @RequiresDialect(IngresDialect.class)})
+	public void testInformixDialectOrIngresDialectLegacyLimitHandler() {
+		assertLimitHandlerEquals( "SELECT first 6 field FROM table" );
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-11194")
+	@RequiresDialect(RDMSOS2200Dialect.class)
+	public void testRDMSOS2200DialectLegacyLimitHandler() {
+		assertLimitHandlerEquals( "SELECT field FROM table fetch first 5 rows only" );
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-11194")
+	@RequiresDialect(value = SQLServerDialect.class, strictMatching = true)
+	public void testSQLServerDialectLegacyLimitHandler() {
+		assertLimitHandlerEquals( "SELECT top 6 field FROM table" );
+	}
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-11194")
+	@RequiresDialect(TimesTenDialect.class)
+	public void testTimesTenDialectLegacyLimitHandler() {
+		assertLimitHandlerEquals( "SELECT first 6 field FROM table" );
+	}
+
+	private void assertLimitHandlerEquals(String sql) {
+		assertEquals( sql, getDialect().getLimitHandler().processSql( TEST_SQL, toRowSelection( 1, 5 ) ) );
+	}
+
+	private RowSelection toRowSelection(int firstRow, int maxRows) {
+		RowSelection selection = new RowSelection();
+		selection.setFirstRow( firstRow );
+		selection.setMaxRows( maxRows );
+		return selection;
+	}
+}
diff --git a/migration-guide.adoc b/migration-guide.adoc
index 3317b3b75a..8b6d9c74af 100644
--- a/migration-guide.adoc
+++ b/migration-guide.adoc
@@ -1,79 +1,102 @@
 = 5.2 Migration Guide
 :toc:
 
 This guide discusses migration from Hibernate ORM version 5.1 to version 5.2.  For migration from
 earlier versions, see any other pertinent migration guides as well.
 
 == Background
 
 Lots of work has been done for 6.0.  One of the things 6.0 will need is a unified view of "type systems"
 including its own type system (Type, EntityPersister, CollectionPersister, etc) and JPA's type system - which
 would mean unifying all of this in hibernate-core.  Because of this and the other large changes slated for 6.0
 we decided to release a 5.2 that showed a clear migration path to the changes in 6.0 but that still supported the
 older calls and expectations as much as possible.
 
 
 == Move to Java 8 for baseline
 
 Hibernate 5.2 is built using Java 8 JDK and will require Java 8 JRE at runtime (we are investigating whether
 Java 9 will also work).  This has a number of implications:
 
 * The hibernate-java8 module has been merged into hibernate-core and the Java 8 date/time types are now natively
 	supported.
 * (todo) support for Java 8 Optional
 * (todo) support for other Java 8 features?
 
 
 == hibernate-entitymanager merged into hibernate-core
 
 The hibernate-entitymanager module has also been merged into hibernate-core.
 
 * `org.hibernate.SessionFactory` now extends `javax.persistence.EntityManagerFactory` - temporarily it
 	technically extends `org.hibernate.jpa.HibernateEntityManagerFactory` (which in turn extends
 	`javax.persistence.EntityManagerFactory`) for backwards compatibility.  `HibernateEntityManagerFactory`
 	is deprecated.
 * `org.hibernate.Session` now extends `javax.persistence.EntityManager` - temporarily it
 	technically extends `org.hibernate.jpa.HibernateEntityManager` (which in turn extends
 	`javax.persistence.EntityManager`) for backwards compatibility.  `HibernateEntityManager` is deprecated.
 * `org.hibernate.Query` (deprecated in favor of new `org.hibernate.query.Query`) now extends the JPA contracts
 	`javax.persistence.Query` and `javax.persistence.TypedQuery`.  `ProcedureCall` and `StoredProcedureQuery` as well.
 * `org.hibernate.HibernateException` now extends `javax.persistence.PersistenceExceptions`.  Hibernate methods
 	that "override" methods from their JPA counterparts now will also throw various JDK defined RuntimeExceptions
 	(such as `IllegalArgumentException`, `IllegalStateException`, etc) as required by the JPA contract.
 * Persister/type access is now exposed through `org.hibernate.Metamodel`, which extends
 	`javax.persistence.metamodel.Metamodel`.  MetamodelImpl now manages all aspects of type system (see below).
 * Cache management has also been consolidated.  `org.hibernate.Cache` now extends `javax.persistence.Cache`.  CacheImpl
 	now manages all aspects of cache regions (see below).
 
 
 == SessionFactory hierarchy cleanup
 
 As part of merging hibernate-entitymanager into hibernate-core, I also wanted to take a moment to clean up
 some of these very old contracts,  In conjunction with the move to Java 8 (default methods) and needing to
 implement JPA methods now in core I decided to implement more of a composition approach here, thus:
 
 * SessionFactoryImplementor used to have a number of methods pertaining to managing and accessing entity and collection persisters.
 	Since we need to deal with JPA Metamodel contract anyway, I went ahead and moved all of that code into our new
 	`org.hibernate.metamodel.spi.MetamodelImplementor`
 * SessionFactory and SessionFactoryImplementor each had a number of methods dealing with cache regions.
 	Many of these methods have been deprecated since 5.0 and those will be removed.  However, the functionality
 	has been moved into the `org.hibernate.Cache` and `org.hibernate.engine.spi.CacheImplementor` contracts
 	helping implement JPA's `javax.persistence.Cache` role.
 
+== LimitHandler changes
+
+In Hibernate 4.3, dialect implementations that did not support a limit offset would fetch all rows for a query and
+perform pagination in-memory.  This solution, while functional, could have severe performance penalties.  In 5.x,
+we prefered to favor performance optimizations which meant dialect implementations would throw an exception if a
+limit offset was specified but the dialect didn't support such syntax.
+
+As of 5.2.5.Final, we have introduced a new setting, `hibernate.legacy_limit_handler`, that is designed to allow
+users to enable the legacy 4.3 limit handler behavior.  By default, this setting is _false_.
+
+The specific dialects impacted by this change are restricted to the following.
+
+* Cache71Dialect
+* DB2390Dialect
+* InformixDialect
+* IngresDialect
+* RDMSOS2200Dialect
+* SQLServerDialect
+* TimesTenDialect
+
+NOTE: If a dialect that extends any in the above list but overrides the limit handler implementation, then those
+dialects remain unchanged, e.g. SQLServer2005Dialect.
+
 
 == Misc
 
 * QueryCacheFactory contract changed
 * RegionFactory contract changes
 * todo : merge AvailableSettings together
 * org.hibernate.Transaction now extends JPA's EntityTransaction and follows its pre- and post- assertions.
 	e.g. begin() now throws an exception if transaction is already active.
 * (todo) following the above one, JPA also says that only PersistenceUnitTransactionType#JTA EntiytManagers
 	are allowed to access EntityTransactions.  Need a strategy to handle this
 * Session#getFlushMode and Query#getFlushMode clash in terms of Hibernate (FlushMode) and JPA (FlushModeType)
 	returns.  #getFlushMode has been altered to return JPA's FlushModeType.  The Hibernate FlushMode
 	is still available via #getHibernateFlushMode and #setHibernateFlushMode.  Same for Session#getFlushMode
 	and EntityManager#getFlushMode.
 * Setting `hibernate.listeners.envers.autoRegister` has been deprecated in favor of
   `hibernate.envers.autoRegisterListeners`.
 * AuditReader#getCurrentRevision has been deprecated in favor of `org.hibernate.envers.RevisionListener`.
\ No newline at end of file
