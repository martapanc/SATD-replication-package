diff --git a/changelog.txt b/changelog.txt
index a2fc570847..e679bf4880 100644
--- a/changelog.txt
+++ b/changelog.txt
@@ -3893,1042 +3893,1042 @@ Changes in version 2.1 rc1 (29.11.2003)
 * fixed some minor bugs in dialect-specific query pagination
 * immutable entity passed to update() is now lock()ed instead
 * reworked the semantics of nonstrict-read-write
 * JCS cache support now deprecated
 * fixed some obscure bugs in collection handling
 * migrated to CGLIB2 (thanks to Chris Nockleberg)
 * fixed bugs in replicate()
 * fixed a bug affecting joined-subclass mappings with dynamic-update=true
 * performance improvements to boolean type mappings (Bertrand Renuart)
 * integrated JBoss TreeCache clustered cache (thanks to Bela Ban and Ben Wang)
 * fixed a bug in new query parameter validation (Steve Ebersole)
 * fixed a bug where <any> mappings caused unnecessary ObjectDeletedException at flush time
 * fixed a bug where associations with property-ref mappings were not properly cached
 * throw PropertyValueException when not-null properties are null at flush time
 * added unsaved-value attribute to version property mapping (Emmanuel Bernard)
 * tolerate classnames containing $ (Steve Ebersole)
 
 Changes in version 2.1 beta 6 (5.11.2003)
 -----------------------------------------
 * added Session.cancelQuery()
 * improvements to transaction handling for failed commit (thanks to Juergen Hoeller)
 * added cascade="delete-orphan"
 * fixed an exception that occurred when a property was declared not-null="true" update="false" (thanks to John Kristian)
 * support multiple named query cache regions (Mikheil Kapanadze)
 * some improvements to collection reattachment
 * fixed a bad bug with adds to an uninitialized bag or list
 * removed support for <dynabean/> components
 * added <dynamic-component/> mapping for properties of type Map
 * fixed a bug where schema export generated index names that were too long for DB2
 * allow per-region expiry policies in OSCache (Matthias Bogaert)
 * fixed a stack overflow that could occur while initializing nonlazy collections
 * fixed a bug in case-insensitive like for Example queries
 * fixed a bug in ScrollableResults.setRowNumber() (Martin Priekopa)
 * improvements to the cache concurrency strategies
 
 Changes in version 2.1 beta 5 (30.10.2003)
 ------------------------------------------
 * Support for custom CollectionPersister (Nick Johnson, Max Andersen)
 * Support for named SQL queries (Max Andersen)
 * duplicate named queries now throws MappingException instead of just logging warning (Max Andersen)
 * fixed problems with WebSphereTransactionManagerLookup (Ralf Taugerbeck, Daniel Bradby)
 * added support for custom collection persisters (thanks to Max Anderson, Nick Johnson)
 * fixed a performance problem during query compilation (Bulent Erdemir)
 * composite keys now supported in createSQLQuery() (Max Andersen)
 * fixed JCA adaptor to run in WebLogic (Daniel Bradby)
 * integrated SwarmCache (Jason Carreira)
 * integrated OSCache (Matthias Bogaert)
 * fixed an NPE that could occur with lists and orphan delete
 * allow nullable one-to-one with property-ref
 * improved usage of Dialect-specific limit SQL
 * fixed problem where non-lazy collections held by cached objects were not immediately initialized
 * fixed getReturnTypes() for native SQL queries (Max Andersen)
 * fixed problems with Criterions that applied to multi-column properties
 * check of rowcounts when JDBC batch updates enabled
 * added named SQL queries using <sql-query> element (Max Andersen)
 * added some extra validations so Hibernate fails earlier when user makes mistakes
 * allow lazy="true" as an alternative to proxy="ClassName"
 * removed dependency to commons-lang
 * SchemaExport now creates indexes for collection foreign key columns if specified by Dialect
 * fixed a bug parsing named parameters in setParameterList()
 * select new Foo(...) will now tolerate null values if the constructor accepts a wrapper type
 * fixed a problem detecting Proxool
 * added logging of persistent object states during flush()
 * allow "not null" as a discriminator value
 * added "parameters" config param to "sequence" generator (Matthias Bogaert)
 
 Changes in version 2.1 beta 4 (3.10.2003)
 -----------------------------------------
 * fixed a bug where <any> mappings did not handle proxies correctly
 * implemented new optimistic-lock strategies
 * fixed several bugs in Criteria query API
 * fixed a bug caching property-ref associations
 * improvements to XML Databinder (Ara Abrahamian)
 * added Session.replicate() and ReplicationMode
 * added ScrollableResults.setRowNumber() / ScrollableResults.getRowNumber()
 * added query cache and Query.setCacheable()
 * added Criteria.returnMaps()
 * fixed some problems with CGLIB proxies
 * fixed an NPE that occurred when a joined-subclass of a versioned entity defined only collections
 * added the access attribute, direct field access and the PropertyAccessor extension point
 * added MatchMode for use with Criteria queries (thanks to Michael Gloegl)
 * fixed a bug where some lazy="false" collections were not immediately initialized
 * fixed problem with WebSphere 5 TransactionManager
 * support schema attribute in MySQL, by using an underscore in the table name (Chris Hane)
 * now seperate Dialects for Interbase and Firebird (Reha Cenani, Max Andersen)
 * removed built-in PreparedStatement cache
 * added Session.getSessionFactory()
 * fixed problem with native SQL queries and Query.setProperties() (Max Andersen)
 * Query now fully validates parameters against the query string before passing them to JDBC (Max Andersen)
 * fixed an NPE in SessionFactory.close()
 * fixed an NPE that occurred when using <idbag>s
 * added SQL-level query results paging to DB2Dialect
 * "foreign" id generator now handles detached instances
 
 Changes in version 2.1 beta 3 (7.9.2003)
 ----------------------------------------
 * added Example queries
 * fixed an exception that occurred at startup with <key-many-to-one> and <joined-subclass>
 * fixed a bug where composite-elements were not being updated if a property not in the equals() was changed
 * <parent> property of a composite-element may now be used in equals()
 * named parameters may now be used in HQL order by clause
 * null value of version property now indicates unsaved instance
 * added select-before-update attribute
 * two-phase loading now use for components
 * better implementation of equals()/hashCode() for proxies
 * added property-ref attribute to <many-to-one>
 * renamed result() to uniqueResult()
 * added Session.get()
 * added HashtableCacheProvider
 * JTA TransactionManager now used even when not using Hibernate Transaction API
 * always bypass process-level cache for LockMode.READ
 
 Changes in version 2.1 beta 2 (27.8.2003)
 -----------------------------------------
 * <subclass> and <joined-subclass> may now appear outside of a <class> element, by providing the extends attribute (Max Andersen)
 * fixed an NPE at startup that was introduced in beta 1
 * fixed a bug in Map.putAll()
 * new pluggable cache API
 - deprecated <jcs-cache> in favor of <cache>
 - net.sf.hibernate.cache.CacheProvider settable via hibernate.cache.provider_class
 * more aggressive caching
 * added Hibernate.close(Iterator)
 * Criteria queries may now include joins
 - Criteria.addJoin()
 - Criteria.createCriteria()
 * hibernate.transaction.manager_lookup_class should now ALWAYS be specified in JTA environment when using jcs caching
 * fixed a bug caching <key-many-to-one>
 * fixed bug where cached component did not get <parent> property populated
 * added hibernate.max_fetch_depth property
 * smarter outer-join fetching
 * transient object may now be associated with session using Session.lock()
 * added Query.result(), Criteria.result()
 
 Changes in version 2.1 beta 1 (10.8.2003)
 -----------------------------------------
 * batch-size attribute for collection and class mappings, to allow batch loading
 * collections of "value types" (including composite-elements) may now appear in HQL from clause
 * more efficient loading of collections, and better handling of non-lazy collections
 * added HQL index() function to allow access to collection element index
 * added Session.createSQLQuery() (Max Andersen)
 * added outer-join attribute to collection mappings
 * Criteria.setFetchMode() now applies to collection-valued path expressions
 * added property-ref attribute to <one-to-one>, enabling unique foreign key associations
 * added hibernate.max_fetch_depth config property
 * added hibernate.hbm2ddl.auto config property
 * fixed a bug with combination of <jcs-cache> and <key-many-to-one>
 * support for Dialect-specific SQL functions in HQL select clause (David Channon)
 * added Session.clear()
 
 Changes in version 2.0.2 (2.8.2003)
 -----------------------------------
 * subqueries may now use HAVING and GROUP BY clauses
 * fixed a bug with setMaxResults(), setFirstResult() in HSQL (introduced in 2.0.1)
 * fixed a bug in Set.removeAll()
 * fixed a bug in SchemaUpdate (Mathias Bogaert)
 * added weak typing functionality to ScrollableResults
 * fixed a bug with "calendar" versioning in IBM JDK1.3.1 (workaround for JDK bug)
 * fixed a bug in mapping DTD that caused a problem for hbm2java (Max Andersen)
 * fixed a bug querying nested components
 * SQL generation now prefers ANSI-style inner joins to theta inner joins
 * fixed a bug caching collection references loaded using FETCH
 * fixed a bug with composite foreign keys in normalized table mappings (Tom Sedge)
 * limit support for Interbase (Ludovic Orban)
 * added where attribute to <class> mappings
 * added cascade="all-delete-orphan" for collection mappings
 * fixed a bug binding named parameters with setMaxResults()/setFirstResults()
 * fixed some minor bugs in HQL translator
 * fixed a long-standing bug where a <key-many-to-one> could not be dereferenced in HQL
 * SQL UPDATEs now occur in a predictable order (same order as objects were loaded)
 * support for SELECT ... FOR UPDATE in SAPDB
 * fixed bug where Criteria queries against a subclass also returned superclass instances
 * fixed a very rare bug where an update could get lost with normalized mappings
 * fixed a problem with proxied class heirarchies rooted at an interface or abstract class
 * where and order-by attributes now allow SQL function calls and subselects
 * added formula attribute to <property> tag, to allow "computed" properties
 * fixed a bug where PreparedStatements were sometimes not closed when an exception occured
 * workaround for a problem with <joined-subclass> and Interceptor.onFlushDirty()
 
 Changes in version 2.0.1 (17.6.2003)
 ------------------------------------
 * fixed some problems with new dialect-specific LIMIT clauses
 * improved parsing of collection where attribute
 * made one-to-many bags more efficient (they are really sets!)
 * allowed type="calendar" for <version> properties
 * fixed a bug with locking a versioned composite-id class
 * refresh() may now take a transient instance
 * added ProxoolConnectionProvider (Martin Crawford)
 * fixed some minor JCA issues (Mike Mosiewicz)
 * fixed a bug with FETCH and sorted associations
 * improved performance of SchemaUpdate tool (Teodor Danciu)
 * fixed a bug in Configuration.addFile(String) (Ken Geis)
 * tidied up and documented hbm2ddl package (esp. Ant tasks)
 * deprecated CounterGenerator in favor of IncrementGenerator
 * improved logging during initialization
 * deprecated "vm" in favor of "increment" id generator
 
 Changes in version 2.0 final (8.6.2003)
 ---------------------------------------
 * added "fetch" keyword to HQL
 * added evict() methods to SessionFactory for JVM-level cache
 * destroy caches from SessionFactory.close()
 * fixed an NPE in Session.evict() (Simon Spero)
 * added Query.setLockMode()
 * tidied up implementation of Loader
 * release ResultSets more aggressively
 * miscellaneous improvements to some Dialects
 * hbm2java now honors the sort attribute (Max Andersen)
 * added exceptions to Interceptor interface
 * fixed problem with setMaxResults(), setFirstResult() in Oracle (introduced in beta 6)
 * fixed some SQL generation that was a problem for Sybase (Dietmar Posselt)
 * fixed some problems with ODMG API (Oliver Gries)
 * added JOTMTransactionManagerLookup (Low Heng Sin)
 * added JOnASTransactionManagerLookup (?)
 * fixed a bug in WeblogicTransactionManagerLookup (Mathias Bogaert)
 * added Criteria.setFetchMode()
 * added new Expressions
 * much more elegant/robust handling of quoted identifiers
 * renamed Hibernate.association() to Hibernate.entity()
 * added dynamic-update and dynamic-insert mapping attributes
 * fixed a bug with refresh() of objects with collections
 * HQL aliases now optional - "from Cat" now legal
 * platform-independant quoting of identifiers using backticks
 
 Changes in version 2.0 beta 6 (10.5.2003)
 -----------------------------------------
 * fixed a bug querying one-to-many associations to a <joined-subclass>
 * added support for dialect-specific LIMIT-style clauses (David White)
 * added <idbag>
 * fixed bug in hashCode() of persistent collections
 * <joined-subclass> now supported in HSQL (Wolfgang Jung)
 * fixed problem for XML parsers which ignore default values declared in DTD
 * <meta> tags can now be set to not be inheritable
 * fixed bug in Expression.in()
 * fixed an NPE that could occur from update() in very strange cases (Chris Nockleberg)
 * disabled outer-join back to owner when initializing one-to-many (performance improvement)
 * fixed a bug in Query.setParameterList() (Nick Heudecker)
 * improved JCA support (Igor Fedorenko)
 
 Changes in version 2.0 beta 5 (21.4.2003)
 -----------------------------------------
 * Informix support (Steve Molitor)
 * fixed a bug parsing "select new ... " queries
 * deprecated "object" type in favor of <any> mappings
 * added Session.contains()
 * added extra DBCP config options (Alex Burgel)
 * SessionFactory.close() now unbinds from JNDI
 * added Session.evict()
 * got rid of an unnecessary SQL DELETE issued when an empty collection was dereferenced
 * where attribute of collection mappings no longer ignored for deletion
 * improved logging
 * support polymorphic associations to "embedded" composite id classes
 * various bugfixes to collection filter parameter binding
 * fixed some problems with proxies introduced in earlier beta versions
 * fixed bug with self-reference in the case of identity column id generation
 * added hibernate.cglib.use_reflection_optimizer property
 * added nonstrict-read-write cache
 * fixed an SQL-generation bug in new Criteria API
 * added CompositeUserType
 * sequence and table id generators now aware of default-schema
 * added update and insert attributes to <component> element
 * fixed a bug with expressions like elements(foo.bar.baz) in where clause
 * more efficient Set initialization (two-phase load)
 * removed support for hibernate.query.imports and added <import> mapping element
 * fixed problem in DBCP connection validation and added new config properties
 * hbm2java can now generate finder methods for persistent objects (experimental) (Matt Hall)
 * hbm2java small fixes/refactorings to support generating more than one file per persistent object (Max Andersen)
 
 Changes in version 2.0 beta 4 (22.3.2003)
 -----------------------------------------
 * Major HQL improvements
 - from "Foo as foo join foo.bars as bar" instead of "from foo in class Foo, bar in elements(foo.bars)"
 - "select new Foo(bar.name, bar.amount) from ...."
 - outer and full join support
 * Query methods now return this, to allow chaining
 * FrontBase support (Run Lussier)
 * experimental JCA support (Daniel Bradby)
 * hbm2java now can generate Beans style property events (Klaus Zimmermann)
 * support SQL identifiers quoted with []
 * fixed bug with PostgreSQL
 * name attribute now optional in .cfg.xml
 * support for postgres ilike operator (M Lang)
 * squash warnings with GNU JAXP (Chris Nockleberg)
 * fixed a bug in Query.setParameterList()
 * Ingres support (Ian Booth)
 * collections now detect changes not made via wrapper for newly saved objects
 * new (experimental) Criteria + Expression APIs
 * Query.setEntity(), etc, now aware of proxies (also improved hueristics for guessing Type)
 * added Hibernate.isInitialized()
 * detect changes made directly to newly-wrapped collection (ie. not via the wrapper)
 * added Hibernate.refresh(Object, LockMode)
 * update(), saveOrUpdate() no longer initialize a proxy
 * fixed problems with Sybase
 * added force attribute to <discriminator>
 * improved handling of null discriminator-value
 * support SQL-style '' escape for HQL strings
 
 Changes in version 2.0 beta 3 (24.2.2003)
 ----------------------------------------
 * collections now represent null elements as a missing row
 * collections now deserialize correctly (fix for bug in beta 2)
 * standardised on dom4j for XML parsing
 * fixed bugs in collection caching (an exception occurred for some sorted collections and some kinds of maps)
 * allowed null discriminators
 * set autocommit to true in SchemaUpdate
 * fixed a stack overflow that could occur in toString() of classes created with hbm2java (Max Andersen)
 * fixed a bug where composite-element <parent> property was not being set after retrieval from cache
 * added where attribute to collection mappings to allow filtering
 * fixed a exception that occurred when wrapping collections with sort="MyComparator" (Jason Horne)
 * objects with mutable="false" are now never updated
 * fixed an exception that occurs with <key-many-to-one> association to a class with a composite id (Stefano Travelli)
 * added SchemaExport Ant task (Rong C Ou)
 * integrated latest CGLIB release (Juozas Baliuka)
 - added support for new CGLIB reflection optimizer (Juozas Baliuka)
 * improved query cache algorithm (Who?)
 * fixed a bug in "object" type
 * Lists and arrays now represent null elements as a missing row
 * fixed a bug in Hibernate PreparedStatement cache where maxRows and fetchSize were not cleared before re-caching
 * fixed a bug in HibernateService that caused a restart to fail
 * added SybaseAnywhereDialect (?)
 * added SessionFactory.close()
 
 Changes in version 2.0 beta 2 (2.2.2003)
 ----------------------------------------
 * property column names may now be of any length (Mark Woon)
 * fixed problem where CodeGenerator created private get/set pairs (Max Andersen)
 * fixed all silly bugs in Configuration.configure()
 * efficient collection updates from Session.update()
 * added <jcs-class-cache> and <jcs-collection-cache> elements to hibernate-configuration.dtd
 * support for normalized mappings for databases with DECODE instead of CASE (Simon Harris)
 * added Oracle9Dialect
 * added JRun4TransactionManagerLookup (Joseph Bissen)
 * fixed JDBCException to keep reference to underlying SQLException
 * fixed a bug loading many-to-many associations with a repeated column name
 * fixed a bug in ShortType
 * added IngresDialect (Ian Booth)
 * added --config option to SchemaExport
 
 Changed in version 2.0 beta 1 (28.1.2003)
 -----------------------------------------
 * renamed packages to net.sf.hibernate.*
 * all API methods now wrap SQLExceptions
 * removed support for toplevel collections / subcollections
 * created hibernate-mapping-2.0.dtd
 - renamed 'readonly' attribute to 'inverse'
 - renamed 'role' attribute to 'name'
 - changed default value for 'unsaved-value' to "null"
 - added mandatory 'name' attribute to <param>
 - added <meta> tag
 * created hibernate-configuration-2.0.dtd
 * brand new Configuration API, including exposed mapping package
 * completely reworked IdentifierGenerator framework
 - built-in generators now auto-detect the type (so integer identity columns are supported, for example)
 - parameters are now named
 - built-in strategies are renamed
 * expanded Interceptor interface
 * removed exceptions that occur if an object is saved or deleted multiple times in a session
 * added <parent> subelement to <composite-element> and <nested-composite-element>
 * collections except for <bag>s now implement by-value equals() and hashCode()
 * removed all deprecated methods
 * added Session.refresh()
 * added dynamic-update functionality
 * added update and insert attributes to <property> and <many-to-one> mappings
 * added elements(), indices(), size(), maxelement(), minelement(), maxindex(), minindex() collection functions to query language
 * huge improvements to CodeGenerator (Max Andersen)
 * enhanced outerjoin fetching support in queries
 * experimental support for DynaBeans as components
 
 Changes in version 1.2.3 (28.1.2003)
 ------------------------------------
 * fixed a recently-introduced problem with Timestamp dirty checking
 * added createClob(), createBlob() for streams (Benoit Menendez)
 * SchemaUpdate now configures Dialect correctly (Michael Locher)
 * update() now working for classes with embedded composite ids
 * unsaved-value attribute now recognized for <composite-id>
 * fixed a minor problem where a very specific kind of SQL select did not qualify a column name
 * added Query.getQueryString()
 * fixed an NPE that sometimes occurred when hibernate.connection.username was not specified
 * fixed a bug in SchemaExport where foreign key constraints did not use qualified tablenames
 * added isFirst(), isLast() to ScrollableResults
 * fixed bug finding properties inherited by mapped interfaces
 
 Changes in version 1.2.1b (4.1.2003)
 ------------------------------------
 * fixed an NPE that occurred while loading Hibernate classes in IBM JVM
 * arbitrary JNDI InitialContext properties may now be passed as hibernate.jndi.*
 * fixed a problem where index column was not being nullified when an entity was removed from a one-to-many
 
 Changes in version 1.2.1 (31.12.2002)
 -------------------------------------
 * Changed the MySQL mapping of Hibernate "timestamp" to MySQL "DATETIME" (Matthias Schwinn)
 * TransactionManagerLookup classes now define defaut UserTransaction JNDI names
 * support for WebSphere 5 (Venkat Srinivasan)
 * fixed a bug with query expressions of the form "foo.bar.id" for normalized mappings
 * experimental Blob/Clob support (thanks to Benoit Menendez and Mark Woon)
 * improvements to SchemaUpdater (Benoit Menendez)
 * deprecated suspendFlushes() / resumeFlushes() in favor of FlushMode
 * deprecated IDGenerationException in favor of IdentifierGenerationException
 * fixed a bug introduced in 1.2 final where cascade save-update was sometimes ignored for readonly="true" bags
 * fixed a bug caching null-valued one-to-one associations
 * CodeGenerator now supports <bag> and <joined-subclass>
 * fixed problem with TimestampType on DB2 (Jonas)
 * fixed a bug in generated SQL for collections with <joined-subclass> mappings (Robson Miranda)
 * fixed a bug caching Maps (Benoit Menendez)
 * SchemaExport now accepts a .jar file as a source of mappings
 * hibernate.dbcp.validationQuery setting (Juozas Baliuka)
 * hibernate.c3p0.validate setting
 * added Query.setTimeout()
 * setMaxResults() now behaves sensibly on SAPDB (Russel Smyth)
 * added Query.setProperties() and Query.getNamedParameters(), fixed a bug in Query.getReturnTypes()
 * CodeGenerator now generates equals() and hashCode() for composite-id classes (and toString() for all classes)
 * CodeGenerator now includes superclass properties in subclass constructors (Max Andersen)
 * added Hibernate.custom()
 
 Changes in version 1.2 final (7.12.2002)
 ----------------------------------------
 * fixed a bug where uppercase IS NOT NULL, NOT IN, etc were not parsed correctly
 * addition to readonly="true" bags now no longer requires collection initialization
 * added ResinTransactionManagerLookup (Aapo Laakkonen)
 * improved exception message when setting null to primitive type (Max Andersen)
 * improved exception message for an unserializable identifier
 * support for overloaded setter methods (patch by Alex Staubo)
 * CodeGenerator support for <composite-element> (patch by Wolfgang Jung)
 
 Changes in version 1.2 beta 4 (29.11.2002)
 ------------------------------------------
 * fixed bugs in one-to-many associations to a <joined-subclass>
 * LockMode class now properly serializable
 * exceptions thrown by proxied objects are now propagated correctly (rather than being wrapped)
 * made Calendar types compilable in JDK1.2
 * added --format and --delimiter options to SchemaExport (thanks to Richard Mixon)
 * fix for problem with class with no properties + native id generation + MS SQL Server contributed by Max Andersen
 * fixed a BAD bug in Hibernate.configure() (thanks to Rob Stokes)
 * CodeGenerator now recognizes <key-many-to-one> (patch by Wolfgang Jung)
 * CodeGenerator now recognizes <version> and <timestamp> (patch by Max Andersen)
 
 Changes in version 1.2 beta 3 (26.11.2002)
 ------------------------------------------
 * fixed bug in UPDATE SQL generation for <joined-subclass> mapping strategy (fix by Robson Miranda)
 * support <composite-id> correctly in CodeGenerator (patch by Tom Cellucci)
 * fixed an exception that occurred with short qualified tablenames
 * added the polymorphism attribute to the <class> element
 * allow "not between", "not in" in query language
 * allow subqueries beginning with a from clause in query language
 * query expressions like "not (foo.bar.baz=1)" now translated to "(bar.baz!=1 and foo.bar=bar.id)"
 * support for PostgreSQL ~ operator (regular expression match)
 * load(id, lockMode) now working for normalized table mappings
 * now compiling properly under JDK1.2, 1.3 (fix by Tom Cellucci)
 * support for subcollections in query language: foo.bars[2]['index'], foo.bars[4].elements, foo.bars[0].size, etc.
 * added calendar and calendar_date types
 * find() queries may now return scalar values
 * component-type properties may appear in a select clause
 * ConnectionProviders now set isolation level before toggle autocommit
 * Iterator.next() now throws NoSuchElementException as per Iterator contract (fix by Alex Staubo)
 * database reverse engineering GUI tool contributed by Tom Cellucci
 * SchemaExport now generates column in mapping file order (rather than alphabetical order)
 * <joined-subclass> mappings working on Oracle (?)
 
 Changes in version 1.2 beta 2 (15.11.2002)
 ------------------------------------------
 * support multi-argument SQL functions in queries
 * reintroduced deprecated form of update() with slightly altered semantics
 * fixed BAD problem in the generated SQL for certain queries
 * added OrionTransactionManagerLookup
 
 Changes in version 1.2 beta 1 (11.11.2002)
 ------------------------------------------
 * Fixed a bad bug binding to JNDI with servers that use serialization in preference to getReference()
 * support for quoted SQL identifiers (patch by Jean-Francois Nadeau)
 * Hibernate.initialize() allows the user to force initialization of a proxy or persistent collection
 * fix to minor bug in CodeGenerator by Max Andersen
 * fixed a problem with outerjoin fetching of one-to-one associations defined on subclasses
 * fixed a minor problem with proxies of classes that override finalize()
 * finished work on normalized table mappings using <joined-subclass> declaration (only for databases with ANSI OUTER JOIN and CASE)
 * deprecated hibernate-mapping.dtd in favor of hibernate-mapping-1.1.dtd
 * reworked unmapped class / interface / table-per-concrete-class query functionality, fixing several problems
 * removed deprecated methods
 * deprecated findIdentifiers()
 * fixed some problems with embedded composite identifiers
 * fixed a bug cascading deletes to one-to-one associations
 * CodeGenerator now generates isFoo() style getters for boolean properties (patch by Aapo Laakkonen)
 * components may now have a nonpublic constructor (patch by Jon Lipsky)
 * changes / bugfixes to MapGenerator tool
 * experimental SchemaUpdate tool contributed by Christoph Sturm
 
 Changes in version 1.1.8 (30.10.2002)
 -------------------------------------
 * full support for composite keys in query language
 * fixed bug where character type could not be null
 * fixed bug parsing collection filters like: "group by this.date"
 * fixed a bad bug where C3P0 properties were not always being used
 * replaced hibernate.use_jdbc_batch with hibernate.jdbc.batch_size
 * renamed some other properties to hibernate.jdbc.*
 * made hibernate.show_sql settable from JMX (patch by Matas Veitas)
 * added SessionFactory.getAllClassMetadata(), getAllCollectionMetadata (patch by Max Andersen)
 * allowed use of concrete-class proxies with inherited classes ie. <subclass name="ConcreteClass" proxy="ConcreteClass">
 * HibernateException extends Apache commons lang NestableException (patch by Max Andersen)
 * <parent> subelement of <component> allows a component to get a reference back to owning entity
 * Query.setParameterList() to bind lists of values to "in (:list)"
 * Java constants may now be used in Queries
 * serialization of an object graph now removes all initialized proxies
 * several other improvements to proxy handling
 * proxies may now be used in JDK 1.2
 
 Changes in version 1.1.7 (25.10.2002)
 -------------------------------------
 * added Session.createFilter() 
 * fixed a bug parsing queries with properties of form idXXX (introduced in 1.1.6)
 * fixed a bug parsing queries with the id property named in the select clause (introduced in 1.1.6)
 * fixed a bug dirty checking big_decimal (fix by Andrea Aime)
 
 Changes in version 1.1.6 (24.10.2002)
 -------------------------------------
 * classes without subclasses may now declare themselves as their own proxy
 * outer-join attribute now working for component properties and <many-to-many>
 * outer-join="true" will now force outerjoin loading for an association even if associated class has a proxy
 * enabled oracle-style outerjoins for SAP DB
 * version properties may now be long or short (previously always integer)
 * discriminators may now be boolean type
 * fixed the foo.bar.id.baz syntax for queries doing composite-key joins
 * fixed an NPE that occurred when no Dialect was specified
 * CodeGenerator now fully proxy-aware (patch by Max Andersen)
 * removed dependency upon trove4j
 
 Changes in version 1.1.5b (20.10.2002)
 --------------------------------------
 * fixed an NPE that occurred on JMX startup
 * smarter fetching for one-to-one associations
 
 Changes in version 1.1.5 (19.10.2002)
 -------------------------------------
 * added built-in currency and timezone types
 * hibernate-mapping-1.1.dtd
 - added <index-many-to-many> and <composite-index> subelements of <map>
 - added <key-property> and <key-many-to-one>
 - renamed "save/update" to "save-update"
 - tightened up the dtd (now using enumerated attribute types)
 * enabled multi-column map indexes (ie. key of a Map)
 * composited-id may now include a many-to-one association
 * improvements to Databinder contributed by Brad Clow
 * fixed bugs in minIndex, maxIndex, minElement, maxElement
 * fixed a problem with JTATransaction in a JBoss BMT bean
 * added addMapResource() to the MBean
 * minor improvements to Configuration
 * more accurate cache entry timestamping to increase the likelihood of cache hits
 * JCS cache may now be used with JTATransaction in WebSphere, Weblogic, JBoss (thanks to Matt Baird)
 * improvements to CodeGenerator contributed by Andrea Aime
 * stopped a TransientObjectException that was being thrown when it shouldn't be
 * re-enabled primary key export for tables of sets with not-null elements
 * hibernate.statement.fetch_size configuration contributed by Matas Veitas
 * added Interceptor application callback interface
 * added metadata package to expose persistence metadata to the application
 * changed filter() argument type from Collection to Object to allow filtering of arrays and Maps
 * added <column> index attribute to allow table indexes to be specified in mapping document
 * implemented support for queries against interfaces and abstract superclasses
 
 Changes in version 1.1.4b (4.10.2002)
 -------------------------------------
 * fixed problems for JDK1.2 (thanks to Chris Webb)
 
 Changes in version 1.1.4 (4.10.2002)
 ------------------------------------
 * New locking API
 * disabled 2-phase load for objects contained in Sets (because they should be initialized before equals() or hashCode() is called)
 * fixed a bug where non-serializable cache entries could not be cached by JCS auxiliary cache
 * fixed a bug in dirty checking PersistentEnums
 * deprecated getID() in favor of getIdentifier() (for mainly cosmetic reasons)
 * HibernateService may now be subclassed to obtain mapping files by some other mechanism (patch by Chris Winters)
 
 Changes in version 1.1.3 (1.10.2002)
 ------------------------------------
 * new 2-phase loading process (replaces complicated "deferred" loading process)
 * new ScrollableResults interface for efficiently navigating Query results
 * removed deprecated interfaces
 * created engine package to hold "internal" APIs (ie. the external contract of the impl package)
 * fixed bug where a component defined before all collections in the mapping file caused collections to not be persisted (thanks to Michael Link)
 * fixed a bug where cascaded saveOrUpdate was ignoring the unsaved-value setting
 * faster Timestamp dirty checking
 
 Changes in version 1.1.2 (29.9.2002)
 ------------------------------------
 * added persister attibute of class mapping to support custom persistence strategies
 * Support for Oracle outerjoins contributed by Jon Lipsky
 * Reworked versioning, fixing bugs (and tightening tests)
 * Fixed a bug where an ObjectNotFoundException was thrown for null one-to-one associations
 * fixed problems with timestamps introduced in 1.1.1
 * added batch file for running demo
 
 Changes in version 1.1.1 (27.9.2002)
 ------------------------------------
 * Major refactoring / abstraction of persistence logic
 * added maxIndex, minIndex, maxElement, minElement properties for collections
 * added support for class names in where clause of queries
 * fixed a bug where an association could become null after caching
 * fixed a bug where an NPE could occur for a null component
 * fixed minor bugs in SortedMap, SortedSet
 * object type is now cacheable
 * added big_integer type
 * improved dirty checking for timestamp type
 
 Changes in version 1.1.0 (22.9.2002)
 ------------------------------------
 * implemented collection indexing with [] in query language
 * fixed some minor query-language bugs
 
 Changes in version 1.1 beta 14 (19.9.2002)
 ------------------------------------------
 * bags now implement java.util.List
 * delete() may now take a transient object
 * bug where sorted collections were not being sorted fixed by Brad Clow
 * fixed a bug in many-to-many association filtering
 * no longer try to query connection metadata when using user-supplied connections
 * added hibernate.use_scrollable_resultsets for user-supplied connections
 * fixed a problem where sublists were not being proxied
 * fixed a problem where Hibernate could not call methods of package-visibility classes
 * removed obsolete select attribute from MapGenerator
 * multiple occurrences of same path in a query no longer require multiple joins
 * added WrongClassException
 
 Changes in version 1.1 beta 13 (15.9.2002)
 ------------------------------------------
 * added constants to Lifecycle interface
 * fix for circular cascade="save/update"
 * fixed a bug in cascaded update introduced in version 1.1 beta 11
 * added object type
 
 Changes in version 1.1 beta 12 (14.9.2002)
 ------------------------------------------
 * Session.filter() for applying a filter query to collections
 * experimental ODMG API (OQL features are not yet compliant)
 * new DBCPConnectionProvider for Apache commons-dbcp connection pool
 * Session.lock() now does version number check even on databases with no FOR UPDATE clause
 * introduced new cascade styles: cascade="save/update", cascade="delete"
 * added default-cascade attribute
 * foreign key columns lengths now automatically set to primary key column lengths for SchemaExport
 * added error checking of row update counts when batching disabled
 * major improvements to ProxyGenerator tool
 * CodeGenerator now aware of proxy attribute
 * integrated PointbaseDialect contributed by Ed Mackenzie
 * fix for problem where Proxies were being initialized on identifier access by Christoph Sturm
 
 Changes in version 1.1 beta 11 (7.9.2002)
 -----------------------------------------
 * deprecated update() in favor of saveOrUpdate() and introduced unsaved-value attribute of <id>
 * children mapped with cascade="all" are now always saved/updated even without a call to update(parent)
 * support for composite-id classes where the composite id consists of properties of the persistent class
 * added constrained attribute to <one-to-one> element
 * added Validatable interface
 * renamed several config properties (Hibernate issues log warnings for obsolete property usage)
 * arbitrary JDBC connection properties may now be passed using hibernate.connection.*
 * fixed a classloading bug in C3P0ConnectionProvider (introduced in 1.1 beta 10)
 * CodeGenerator may now take multiple mapping files on the commandline
 
 Changes in version 1.1 beta 10 (28.8.2002)
 ------------------------------------------
 * fixed a bug that occurred when calling Session.update() for an object with no properties
 * changed class loading to use the context classloader first
 * introduced <timestamp> as an alternative to <version>
 * added Query.getReturnTypes()
 * fixed a bug with composite-elements introduced in 1.1 beta 7
 * save() may now be used to persist classes with composite-ids
 * improved handling of nanoseconds values in Timestamps
 * support for composite id properties + objects in select clause of iterate()
 * beefed-up collection tests
 
 Changes in version 1.1 beta 9 (26.8.2002)
 -----------------------------------------
 * fixed a bug introduced in 1.1 beta 8 that could cause an NPE after deserializing a session with proxies
 * deprecated insert() in favor of more flexible save()
 * deprecated IDGenerator in favor of new IdentifierGenerator interface
 * "assigned" id generator now returns the existing value of the id property instead of throwing an Exception
 * fixed a problem where PreparedStatements were not being recached after retrieving a natively generated id
 
 Changes in version 1.1 beta 8 (25.8.2002)
 -----------------------------------------
 * fixed a bug introduced in 1.1 beta 6 where an updated element of an indexed one-to-many collection caused an SQLException
 * uninitialized collections passed to update() may now be initialized in the new Session
 * uninitialized proxies passed to update() may now be initialized in the new Session
 
 Changes in version 1.1 beta 7 (23.8.2002)
 -----------------------------------------
 * fixed a bug where Hibernate was not returning statements to the cache when batch updates were disabled
 * fixed a bad bug parsing mappings with toplevel one-to-many associations
 * fixed a bug with versioning and subcollections
 * reworked Set updates again for much improved efficiency
 * schema export now creates primary keys for indexed collection tables
 * minor refactor to Type hierarchy
 * improved some user-error detection
 * fixed foreign key constraint creation for MySQL with Innodb tables
 
 Changes in version 1.1 beta 6b (20.8.2002)
 ------------------------------------------
 * Fixed a problem updating Sets
 * added <bag> mapping for java.util.Collection
 
 Changes in version 1.1 beta 6 (20.8.2002)
 -----------------------------------------
 * completely reworked fetching code
 - one-to-many associations now always fetched in a single select
 - many-to-many associations fetched in a single select when outerjoin fetching is enabled 
 - this includes nested outerjoin fetching of the associated class!
 - outerjoin fetching for <many-to-one> nested inside <component> or <composite-element>
 - code refactored to be cleaner and faster
 * removed unnecessary order by clause in List and array fetching SQL
 * collections now individually update, insert and delete only rows that changed (thanks to Doug Currie)
 * fixed a problem where exceptions were being wrapped in a LazyInitializationException for non-lazy collections
 * added order-by attribute to <set> and <map> to specify a table column as defining the iteration order (JDK1.4 only)
 * improved error detection in Session.update()
 * further usage of JDBC2 batch updates
 * some fine-tuning of JDBC2 feature usage configuration
 * CodeGenerator will now generate components and arrays
 * fixed problem where CodeGenerator could not generate classes in the default package
 * improved logging of flush() activity
 * renamed property hibernate.use_jdbc2 to hibernate.use_jdbc_batch
 
 Changes in version 1.1 beta 5 (13.8.2002)
 -----------------------------------------
 * hibernate.query.imports property to allow use of unqualified classnames in queries
 * fixed a bug in collection flushing that was introduced in 1.1 beta 4
 
 Changes in version 1.1 beta 4 (11.8.2002)
 -----------------------------------------
 * JMX integration (thanks to John Urberg)
 * "having" clause in query language
 
 Changes in version 1.1 beta 3 (10.8.2002)
 -----------------------------------------
 * removed the select="all" attribute for <class> mappings - "select distinct" now specified in the hibernate query
 * system properties now override hibernate.properties
 * Session now flushes changes even less often (only when actual updates to the queried table(s) are waiting)
 * fixed a *very* rare problem where an unnecessary update could be accidently issued before a delete
 
 Changes in version 1.1 beta 2 (6.8.2002)
 ----------------------------------------
 * fixed a bug exporting schemas with identity columns
 * implemented factory-level caching of collections
 * Datastore.storeJar() contributed by Christian Meunier
 * added <mapping jar="jarfile"> to hibernate.cfg.xml
 
 Changes in version 1.1 beta 1 (4.8.2002)
 ----------------------------------------
 * new Query API including named parameters, pageable results
 * subqueries in Hibernate queries (only for databases that support subselects)
 * new DocBook documentation (contributed by Christian Bauer)
 * support collections .elements, .indices inside select clause (even in aggregate functions)
 * don't load collections before removal unless absolutely necessary
 * mutable="false" attribute in <class> element to map immutable classes
 * use JDBC batch to insert collection elements if hibernate.use_jdbc2 is set
 * brand new PreparedStatementCache
 * improvements to MYSQL dialect for long datatypes
 * always check isAccessible() before setAccessible(true)
 * removed extra unnecessary table join in queries with one-to-many association
 * removed ugly "WHERE 1=1" from generated SQL
 * fixed exception mapping a class with no properties (fix by Rob Stokes)
 * logging enhancements including SQLException logging
 * reworked factory-level cache and integrated JCS support (thanks to Christian Meunier)
 * fixed a bug with circular references in cached data
 * removed blocking cache support
 * now rendering outerjoins as "LEFT OUTER JOIN" because "LEFT JOIN" caused problems for some Sybase versions
 * added default Hibernate properties to Dialects
 * native id generation now falls back to sequence id generation when identity columns not supported by the dialect
 * fixed some problems with native id generation under HSQL
 * may now use Session.insert() even when using native id generation
 
 Changes in version 1.0.1b (18.7.2002)
 -------------------------------------
 * fixed a bad bug in query parser when hibernate.query.substitutions was unset
 * much improved build.xml Ant script
 * latest c3p0.jar
 
 Changes in version 1.0.1 (17.7.2002)
 ------------------------------------
 * enabled use of scalar values and aggregate SQL functions in select clause of iterate() queries
 * fixed bug in JNDI lookup for SessionFactory
 * changed ordering of SQL inserts/deletes for child collections of a cascade-enabled association 
 - better behaviour for some not-null constraints
 - performance optimization for bidirectional many-to-one associations
 * added hibernate.query.substitutions property to allow named query constants (eg. translate 'true' to '1')
 * added locale type for java.util.Locale
 * added sequence hi/lo generator (seqhilo.long)
 * fixed bug where load(), onLoad() callbacks were sometimes called at wrong time
 * fixed an exception (fix by Eric Everman) and improved identifier searching in MapGenerator tool 
 * refactored SerializableType
 * extra logging + error handling
 * documentation enhancements
 
 Changes in version 0.9.17 (3.7.2002)
 ------------------------------------
 * Added UserType interface
 * documented Lifecycle
 * added some new trace messages to log
 * bugfix to allow SQL functions like upper(), lower(), etc to work on all platforms
 * documented SAP DB support (dialect contributed by Brad Clow)
 * foreign key constraint export for SAP DB
 * map index may be the composite-id of the element class (contributed by Jon Lipsky)
 * fixes to CodeGenerator tool (contributed by Uros Jurglic)
 
 Changes in version 0.9.16 (19.6.2002)
 ------------------------------------
 * fixed bug cacheing objects with references to themselves
 * support for composite ids of classes with no id property
 * may now disable outer join (deep) fetching for an association by setting outer-join="false"
 * enabled outer join fetching for one-to-one
 * fixed a bug for mappings that specify class attribute of <one-to-many>
 * fixed a bug where Hashbelt did not expire cached data in a timely fashion
 * fixed a mistake in the mapping DTD
 * new user-error check in update()
 
 Changes in version 0.9.15 (15.6.2002)
 ------------------------------------
 * one-to-one associations
 * support for "tricky" mappings in SchemaExport tool (multiple properties to one column, etc)
 * Transaction API contributed by Anton van Straaten
 * SessionFactory may be bound to JNDI name by setting hibernate.session_factory_name
 * Sessions are now Serializable!
 * added Session.findIdentifiers() query methods
 * new Lifecycle interface to replace deprecated PersistentLifecycle
 * fixed problem where session did not autoflush on iterate() queries
 * performance enhancements to collection dirty checking
 * added Hibernate.configure() and configuration file format
 * removed some deprecated methods
 * refactored Type hierarchy
 * query language identifiers now case sensitive (bugfix for case sensitive SQL dialects)
 * username/password now optional for datasource (contributed by Emmanuel Bourg)
 * much improved API documentation
 * binary types now saved using streams if hibernate.use_streams_for_binary=true (contributed by Jon Lipsky)
 * MySQL Strings now mapped to TEXT columns if length > 255 (contributed by Christoph Beck)
 
 Changes in version 0.9.14 (4.6.2002)
 -------------------------------------
 * lifecycle objects - properties now have a cascade attribute to cascade save, update, delete
 * composite id properties may now be used in queries eg. foo.id.bar (contributed by Jon Lipsky)
 * slightly changed semantics of update() so it now also save()s new transient instances
 * Session now flushes() itself less often before query execution (performance enhancement)
 * fixed problem where Session.save() returned null instead of the natively generated id
 * fixed bug with object identity for cached classes
 * fixed bug where delete(x) could not be called after update(x)
 * MUCH improved Exception hierarchy
 * deprecated create()
 * added sql-type attribute to <column> tag to allow user to override default type mapping
 * deeper fetching with use_outer_join
 * new ConnectionProvider framework
 * fixed a bug where blocking cache could return stale data
 * now working again in JDK1.2.2
 * fixed problem with not-null associations + native key generation
 * minor changes to PersistentLifecycle interface
 * initial, minimal version of new Transaction mechanism
 * MUCH improved documentation
 
 Changes in version 0.9.13 (25.5.2002)
 -------------------------------------
 * Datastore.storeResource() to load mapping files from classpath
 * fixed a problem executing under JDK1.3 when compiled from JDK1.4
 * documentation improvements
 
 Changes in version 0.9.12 (24.5.2002)
 ------------------------------------
 * Session.update() methods to update a persistent instance from transient copy (as requested by many users)
 * discriminator column name, type, length, etc now configurable by <discriminator> tag in mapping file
 * discriminator column values configurable by discriminator-value attribute of <class> and <subclass> tags
 * added Session.insert(object, id) for classes with no identifier property
 * fixed another bad bug with connection handling (fix by Anton van Straaten)
 * fixed a problem with deferred loading
 * fixed a problem with sorted collections (fix by Anton van Straaten)
 * nested collections of objects now require 2 SQL SELECTs to load, rather than size+1
 * session is NO LONGER atomic - application should discard session when exception occurs
 * fixed problem where character type was mapped to VARCHAR column
 * arrays of proxies now possible by using new element-class attribute of <array> tag
 * fixed various problems with proxies
 * added proxy generation tool
 * proxy unit tests
 * replaced two RuntimeExceptions with checked exceptions
 * made hibernate.username/hibernate.password optional for DriverManager
 * CodeGenerator now supports all hibernate basic types
 * much improved caching algorithm for compiled queries
 * may now specify properties simply by placing hibernate.properties in classpath
 * documentation improvements + fixes
 * --abstract switch to MapGenerator (contributed by Eric Everman)
 
 Changes in version 0.9.11 (12.5.2002)
 ------------------------------------
 * fixed major bug with connection handling (fix by Anton van Straaten)
 
 Changes in version 0.9.10 (11.5.2002)
 ------------------------------------
 * set a default schema name using SessionFactory property hibernate.default_schema
 * code generator tool contributed by Brad Clow (www.workingmouse.com)
 * lazy object initialization under JDK 1.3 and above
 * fixed some error messages to go to logging framework, not stdout
 * new system property hibernate.show_sql=true logs all executed SQL to stdout
 * integration of bugfixes in c3p0
 * wrap IllegalArgumentExceptions in HibernateExceptions
 * added ObjectNotFoundException and StaleObjectStateException
 * fixed a bug when using schemas
 * new caching strategy (and documented cache feature)
 
 Changes in version 0.9.9 (25.4.2002)
 -----------------------------------
 * sorted sets and maps (thanks to Doug Currie)
 * mapping files may now be loaded using getResourceAsStream() (thanks to Markus Meissner)
 * hibernate messages now logged by Apache commons-logging
 * default hi/lo generator table now has column named "next_id", instead of "next"
 * query language may now refer to identifier property name (eg. foo.fooKey as alternative to foo.id)
 * hibernate.jndi_class, hibernate.jndi_url now optional when using datasource
 * hibernate now throws an exception if you try to persist an object with a reference to a transient object
 * improved connection pooling algorithm (hibernate.pool_size limits pooled conections)
 * very experimental integration of c3p0 JDBC connection pool (http://sourceforge.net/projects/c3p0)
 * now compiles under JDK 1.2.2
 * fixed bug persisting null components
 * fixed bug where cached prepared statements were not cleaned up after disconnect() session
 * fixed a null pointer exception in MappingByReflection
 
 Changes in version 0.9.8 (13.3.2002)
 -----------------------------------
 * supports database native key generation in Sybase, MS SQL, MySQL, DB2, Hypersonic (contributed by Christoph Sturm)
 * supports Mckoi (dialect contributed by Doug Currie)
 * supports Progress (dialect contributed by Phillip Baird)
 * added exceptions to catch Session reentrancy from PersistentLifecycle.load() + store()
 * experimental cross-transaction cache
 * Session.lock() and Session.loadWithLock() for pessimistic locking
 * HiLoGenerators may now use their own DriverManager connection properties + may now use same table across diff mapping files
 * Session.flush(), Session.close() and Session.connection() replace Session.commit(), Session.cancel()
 * Session.disconnect() and Session.reconnect() for long transactions
 * added single JVM id generators vm.long, vm.hex
 * added unique column constraints to mappings
 * extensions to IDGenerator framework
 * support sequence-style ID generation in Oracle, PostgreSQL, DB2, Interbase
 
 * fixed problem where subcollections of a collection that changed roles would be deleted
 * changed class loading strategy to be compatible with JBoss
 * stopped queries retrieving unnecessary columns
 * mutable types (binary + serializable) now always detected as dirty
 
 Changes in version 0.9.7 (26.2.2002)
 -----------------------------------
 * save() now safe from foreign key violations (so create() is no longer preferred method of adding data)
 * delete() now completely safe from foreign key violations - it no longer matters what order objects are deleted in
 * removed Session.copy()
 * hilo generators now NOT for use with application server datasources
 
 * fixed two intermittent bugs in queries
 * fixed a problem where components not detected as dirty
 * fixed broken hilo generator which was not updating database
 * fixed a minor bug when hibernate.use_outer_join was set
 
 Changes in version 0.9.6 (24.2.2002)
 -----------------------------------
 * experimental XML generation
 * added support for bi-directional associations (one-to-set, set-to-set) with <set readonly="true"> config
 * reflective generation of mappings tool was contributed by Doug Currie
 * Session operations now atomic, so exceptions are recoverable
 * made ID properties optional in persistent classes
 * support for multiple schemas through schema attribute of <hibernate-mapping>, <class>, <set>, <map>, etc.
 * auto-creation of tables for hilo id generators (restriction: cannot use same table from more than one mapping file)
 * added some more assertions to catch user "mistakes" like deleting transient or saving persistent objects
 
 * major rework of collections and fixed some bad bugs
 * lazy initialization re-enabled for one-to-many associations (thanks to Georg Schneemayer)
 * fixed a problem in the mapping DTD to allow nested components in collections
 * fixed a BAD bug in RelationalDatabaseSession when loading objects with PersistentLifecycle callbacks (thanks to Paul Szego)
 * fixed problems with quoted strings in query language
 * fixed a bug where a stack overflow occurred instead of HibernateException thrown (thanks to Georg Schneemayer)
 * fixed a bug deleting updated versioned data
 * fixed some problems with name generation for indexes + foreign keys
 * fixed problem in Sun JDK 1.4 (only?) where IllegalArgumentException was not handled
 * minor improvements to handling of dates and times
 * HiLoGenerator now safe for all transaction isolation levels + safe when rollback occurs
 * noticed and fixed obscure piece of nonthreadsafe code outside of core persistence engine
 * removed unnecessary drop constraints for some dialects
 
 * MUCH more comprehensive test suite
 
 * changed some terminology used in documentation
 * added javadoc for API classes
 * commented the mapping DTD
 
 Changes in version 0.9.5 (8.2.2002)
 -----------------------------------
 * supports HypersonicSQL (dialect contributed by Phillip Baird)
 * supports Microsoft SQL server (with third party JDBC driver)
 * proper command-line tool for schema generation and export
 * deprecated the interface cirrus.hibernate.Persistent (due to popular demand)
 * changes to hibernate-mapping DTD (required to support optional Persistent interface):
 - deprecated <property type="package.PersistentClassName"/> in favor of <many-to-one class="package.PersistentClassName"/>
 - deprecated <element type="package.PersistentClassName"/> in favor of <many-to-many class="package.PersistentClassName"/>
 - deprecated <property role="..."/> in favor of <collection role="..."/>
 - deprecated <element role=".."/> in favor of <subcollection role="..."/>
 - deprecated <association> in favor of <one-to-many>
 * class attribute optional in <component> and <composite-id> tags (determined by reflection)
 * querying components of components now supported
 * one-shot table creation (no use of unportable "alter table")
 * time dataype support
 * reflective mappings of java.sql.Time, java.sql.Timestamp, java.sql.Date
 * fixed error msg thrown when class is missing a method but has a superclass
 * property names now conform to JavaBean spec ("foo" instead of "Foo"). Note that "Foo" still works
 * constructors of persistent classes may now be non-public
 * collection indexes now mapped to not-null columns
 * fixed obscure bug with querying collections inside components of components
 * fixed potential bug related to cacheing of compiled queries
 * major rewrite of code relating to O-R mappings
 * Session.copy() and Session.equals() as convenience for users
-* fixed repeated invocations of hasNext() on iterator + iterators now always work with distinct SQL resultsets
+* fixed repeated invocations of hasNext() on iterator + wrappedIterators now always work with distinct SQL resultsets
 * McKoi dialect was contributed by Gabe Hicks
 
 Changes in version 0.9.4 (29.1.2002)
 ------------------------------------
 * fixed BAD bug where XML parsing would not work for parsers other than Xerces - thanks to Christian Winkler
 * added some more assertions to catch user "mistakes" like changing ids or reusing existing ids
 
 Changes in version 0.9.3 (27.1.2002)
 ------------------------------------
 * repackaged (corrupted DatasourceConnectionProvider.class)
 * better exception reporting using datasource
 * added Datastore.storeClass() which looks for mapping file in classpath (class foo.Bar -> foo/Bar.hbm.xml)
 * improved documentation
 
 Changes in version 0.9.2 (25.1.2002)
 ------------------------------------
 * iterate over query results (lazy instantiation of query results)
 * added "select foo, bar" style queries returning multiple objects per row
 * delete by query
 * composite key support
 * outer joins for faster (?) loading of associated objects ( set "hibernate.use_outer_join" to "true" )
 * connection pooling when using DriverManager
 * foreign key constraint from unkeyed collection table to owner entity table
 * improved drop tables script execution (still not infallible)
 * added <composite-element> tag
 * added not-null properties and elements
 * added an optimisation for dates and components
 * made some XML attributes optional
 * fixed errors in documentation + documented some extra features
 * bugfix: store() not getting called on lifecycle interface
 * bugfix: schema generation for indexed associations
 * added many tests
 
 Changes in version 0.9.1 (20.1.2002)
 ------------------------------------
 Too many to list
 
 version 0.8.1
 -------------
 Initial alpha version
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
index 0e63016139..9dad56567f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/CopyIdentifierComponentSecondPass.java
@@ -1,157 +1,167 @@
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
 import java.util.Iterator;
 import java.util.Map;
 
+import org.jboss.logging.Logger;
+
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
+import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.SimpleValue;
 
 /**
  * @author Emmanuel Bernard
  */
 public class CopyIdentifierComponentSecondPass implements SecondPass {
+	private static final Logger log = Logger.getLogger( CopyIdentifierComponentSecondPass.class );
+
 	private final String referencedEntityName;
 	private final Component component;
 	private final Mappings mappings;
 	private final Ejb3JoinColumn[] joinColumns;
 
 	public CopyIdentifierComponentSecondPass(
 			Component comp,
 			String referencedEntityName,
 			Ejb3JoinColumn[] joinColumns,
 			Mappings mappings) {
 		this.component = comp;
 		this.referencedEntityName = referencedEntityName;
 		this.mappings = mappings;
 		this.joinColumns = joinColumns;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void doSecondPass(Map persistentClasses) throws MappingException {
 		PersistentClass referencedPersistentClass = (PersistentClass) persistentClasses.get( referencedEntityName );
 		// TODO better error names
 		if ( referencedPersistentClass == null ) {
 			throw new AnnotationException( "Unknown entity name: " + referencedEntityName );
 		}
 		if ( ! ( referencedPersistentClass.getIdentifier() instanceof Component ) ) {
 			throw new AssertionFailure(
 					"Unexpected identifier type on the referenced entity when mapping a @MapsId: "
 							+ referencedEntityName
 			);
 		}
 		Component referencedComponent = (Component) referencedPersistentClass.getIdentifier();
 		Iterator<Property> properties = referencedComponent.getPropertyIterator();
 
 
 		//prepare column name structure
 		boolean isExplicitReference = true;
 		Map<String, Ejb3JoinColumn> columnByReferencedName = new HashMap<String, Ejb3JoinColumn>(joinColumns.length);
 		for (Ejb3JoinColumn joinColumn : joinColumns) {
 			final String referencedColumnName = joinColumn.getReferencedColumn();
 			if ( referencedColumnName == null || BinderHelper.isEmptyAnnotationValue( referencedColumnName ) ) {
 				break;
 			}
 			//JPA 2 requires referencedColumnNames to be case insensitive
 			columnByReferencedName.put( referencedColumnName.toLowerCase(), joinColumn );
 		}
 		//try default column orientation
 		int index = 0;
 		if ( columnByReferencedName.isEmpty() ) {
 			isExplicitReference = false;
 			for (Ejb3JoinColumn joinColumn : joinColumns) {
 				columnByReferencedName.put( "" + index, joinColumn );
 				index++;
 			}
 			index = 0;
 		}
 
 		while ( properties.hasNext() ) {
 			Property referencedProperty = properties.next();
 			if ( referencedProperty.isComposite() ) {
 				throw new AssertionFailure( "Unexpected nested component on the referenced entity when mapping a @MapsId: "
 						+ referencedEntityName);
 			}
 			else {
 				Property property = new Property();
 				property.setName( referencedProperty.getName() );
 				property.setNodeName( referencedProperty.getNodeName() );
 				//FIXME set optional?
 				//property.setOptional( property.isOptional() );
 				property.setPersistentClass( component.getOwner() );
 				property.setPropertyAccessorName( referencedProperty.getPropertyAccessorName() );
 				SimpleValue value = new SimpleValue( mappings, component.getTable() );
 				property.setValue( value );
 				final SimpleValue referencedValue = (SimpleValue) referencedProperty.getValue();
 				value.setTypeName( referencedValue.getTypeName() );
 				value.setTypeParameters( referencedValue.getTypeParameters() );
-				final Iterator<Column> columns = referencedValue.getColumnIterator();
+				final Iterator<Selectable> columns = referencedValue.getColumnIterator();
 
 				if ( joinColumns[0].isNameDeferred() ) {
 					joinColumns[0].copyReferencedStructureAndCreateDefaultJoinColumns(
 						referencedPersistentClass,
 						columns,
 						value);
 				}
 				else {
 					//FIXME take care of Formula
 					while ( columns.hasNext() ) {
-						Column column = columns.next();
+						final Selectable selectable = columns.next();
+						if ( ! Column.class.isInstance( selectable ) ) {
+							log.debug( "Encountered formula definition; skipping" );
+							continue;
+						}
+						final Column column = (Column) selectable;
 						final Ejb3JoinColumn joinColumn;
 						String logicalColumnName = null;
 						if ( isExplicitReference ) {
 							final String columnName = column.getName();
 							logicalColumnName = mappings.getLogicalColumnName( columnName, referencedPersistentClass.getTable() );
 							//JPA 2 requires referencedColumnNames to be case insensitive
 							joinColumn = columnByReferencedName.get( logicalColumnName.toLowerCase() );
 						}
 						else {
 							joinColumn = columnByReferencedName.get( "" + index );
 							index++;
 						}
 						if ( joinColumn == null && ! joinColumns[0].isNameDeferred() ) {
 							throw new AnnotationException(
 									isExplicitReference ?
 											"Unable to find column reference in the @MapsId mapping: " + logicalColumnName :
 											"Implicit column reference in the @MapsId mapping fails, try to use explicit referenceColumnNames: " + referencedEntityName
 							);
 						}
 						final String columnName = joinColumn == null || joinColumn.isNameDeferred() ? "tata_" + column.getName() : joinColumn
 								.getName();
 						value.addColumn( new Column( columnName ) );
 						column.setValue( value );
 					}
 				}
 				component.addProperty( property );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
index fb5733adc8..07c808e746 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
@@ -1,453 +1,467 @@
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
 package org.hibernate.cfg.beanvalidation;
 
 import javax.validation.Validation;
 import javax.validation.ValidatorFactory;
 import javax.validation.constraints.Digits;
 import javax.validation.constraints.Max;
 import javax.validation.constraints.Min;
 import javax.validation.constraints.NotNull;
 import javax.validation.constraints.Size;
 import javax.validation.metadata.BeanDescriptor;
 import javax.validation.metadata.ConstraintDescriptor;
 import javax.validation.metadata.PropertyDescriptor;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
+import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.SingleTableSubclass;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 class TypeSafeActivator {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TypeSafeActivator.class.getName());
 
 	private static final String FACTORY_PROPERTY = "javax.persistence.validation.factory";
 
 	/**
 	 * Used to validate a supplied ValidatorFactory instance as being castable to ValidatorFactory.
 	 *
 	 * @param object The supplied ValidatorFactory instance.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public static void validateSuppliedFactory(Object object) {
 		if ( ! ValidatorFactory.class.isInstance( object ) ) {
 			throw new IntegrationException(
 					"Given object was not an instance of " + ValidatorFactory.class.getName()
 							+ "[" + object.getClass().getName() + "]"
 			);
 		}
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public static void activate(ActivationContext activationContext) {
 		final Properties properties = activationContext.getConfiguration().getProperties();
 		final ValidatorFactory factory;
 		try {
 			factory = getValidatorFactory( properties );
 		}
 		catch (IntegrationException e) {
 			if ( activationContext.getValidationModes().contains( ValidationMode.CALLBACK ) ) {
 				throw new IntegrationException( "Bean Validation provider was not available, but 'callback' validation was requested", e );
 			}
 			if ( activationContext.getValidationModes().contains( ValidationMode.DDL ) ) {
 				throw new IntegrationException( "Bean Validation provider was not available, but 'ddl' validation was requested", e );
 			}
 
 			LOG.debug( "Unable to acquire Bean Validation ValidatorFactory, skipping activation" );
 			return;
 		}
 
 		applyRelationalConstraints( factory, activationContext );
 
 		applyCallbackListeners( factory, activationContext );
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public static void applyCallbackListeners(ValidatorFactory validatorFactory, ActivationContext activationContext) {
 		final Set<ValidationMode> modes = activationContext.getValidationModes();
 		if ( ! ( modes.contains( ValidationMode.CALLBACK ) || modes.contains( ValidationMode.AUTO ) ) ) {
 			return;
 		}
 
 		// de-activate not-null tracking at the core level when Bean Validation is present unless the user explicitly
 		// asks for it
 		if ( activationContext.getConfiguration().getProperty( Environment.CHECK_NULLABILITY ) == null ) {
 			activationContext.getSessionFactory().getSettings().setCheckNullability( false );
 		}
 
 		final BeanValidationEventListener listener = new BeanValidationEventListener(
 				validatorFactory,
 				activationContext.getConfiguration().getProperties()
 		);
 
 		final EventListenerRegistry listenerRegistry = activationContext.getServiceRegistry()
 				.getService( EventListenerRegistry.class );
 
 		listenerRegistry.addDuplicationStrategy( DuplicationStrategyImpl.INSTANCE );
 
 		listenerRegistry.appendListeners( EventType.PRE_INSERT, listener );
 		listenerRegistry.appendListeners( EventType.PRE_UPDATE, listener );
 		listenerRegistry.appendListeners( EventType.PRE_DELETE, listener );
 
 		listener.initialize( activationContext.getConfiguration() );
 	}
 
 	@SuppressWarnings({"unchecked", "UnusedParameters"})
 	private static void applyRelationalConstraints(ValidatorFactory factory, ActivationContext activationContext) {
 		final Properties properties = activationContext.getConfiguration().getProperties();
 		if ( ! ConfigurationHelper.getBoolean( BeanValidationIntegrator.APPLY_CONSTRAINTS, properties, true ) ){
 			LOG.debug( "Skipping application of relational constraints from legacy Hibernate Validator" );
 			return;
 		}
 
 		final Set<ValidationMode> modes = activationContext.getValidationModes();
 		if ( ! ( modes.contains( ValidationMode.DDL ) || modes.contains( ValidationMode.AUTO ) ) ) {
 			return;
 		}
 
 		applyRelationalConstraints(
+				factory,
 				activationContext.getConfiguration().createMappings().getClasses().values(),
 				properties,
 				activationContext.getServiceRegistry().getService( JdbcServices.class ).getDialect()
 		);
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
-	public static void applyRelationalConstraints(Collection<PersistentClass> persistentClasses, Properties properties, Dialect dialect) {
-		ValidatorFactory factory = getValidatorFactory( properties );
+	public static void applyRelationalConstraints(
+			ValidatorFactory factory,
+			Collection<PersistentClass> persistentClasses,
+			Properties properties,
+			Dialect dialect) {
 		Class<?>[] groupsArray = new GroupsPerOperation( properties ).get( GroupsPerOperation.Operation.DDL );
 		Set<Class<?>> groups = new HashSet<Class<?>>( Arrays.asList( groupsArray ) );
 
 		for ( PersistentClass persistentClass : persistentClasses ) {
 			final String className = persistentClass.getClassName();
 
 			if ( className == null || className.length() == 0 ) {
 				continue;
 			}
 			Class<?> clazz;
 			try {
 				clazz = ReflectHelper.classForName( className, TypeSafeActivator.class );
 			}
 			catch ( ClassNotFoundException e ) {
 				throw new AssertionFailure( "Entity class not found", e );
 			}
 
 			try {
 				applyDDL( "", persistentClass, clazz, factory, groups, true, dialect );
 			}
 			catch (Exception e) {
 				LOG.unableToApplyConstraints( className, e );
 			}
 		}
 	}
 
 	private static void applyDDL(
 			String prefix,
 			PersistentClass persistentClass,
 			Class<?> clazz,
 			ValidatorFactory factory,
 			Set<Class<?>> groups,
 			boolean activateNotNull,
 			Dialect dialect) {
 		final BeanDescriptor descriptor = factory.getValidator().getConstraintsForClass( clazz );
 		//no bean level constraints can be applied, go to the properties
 
 		for ( PropertyDescriptor propertyDesc : descriptor.getConstrainedProperties() ) {
 			Property property = findPropertyByName( persistentClass, prefix + propertyDesc.getPropertyName() );
 			boolean hasNotNull;
 			if ( property != null ) {
 				hasNotNull = applyConstraints(
 						propertyDesc.getConstraintDescriptors(), property, propertyDesc, groups, activateNotNull, dialect
 				);
 				if ( property.isComposite() && propertyDesc.isCascaded() ) {
 					Class<?> componentClass = ( (Component) property.getValue() ).getComponentClass();
 
 					/*
 					 * we can apply not null if the upper component let's us activate not null
 					 * and if the property is not null.
 					 * Otherwise, all sub columns should be left nullable
 					 */
 					final boolean canSetNotNullOnColumns = activateNotNull && hasNotNull;
 					applyDDL(
 							prefix + propertyDesc.getPropertyName() + ".",
 							persistentClass, componentClass, factory, groups,
 							canSetNotNullOnColumns,
                             dialect
 					);
 				}
 				//FIXME add collection of components
 			}
 		}
 	}
 
 	private static boolean applyConstraints(
 			Set<ConstraintDescriptor<?>> constraintDescriptors,
 			Property property,
 			PropertyDescriptor propertyDesc,
 			Set<Class<?>> groups,
 			boolean canApplyNotNull,
 			Dialect dialect) {
 		boolean hasNotNull = false;
 		for ( ConstraintDescriptor<?> descriptor : constraintDescriptors ) {
 			if ( groups != null && Collections.disjoint( descriptor.getGroups(), groups ) ) {
 				continue;
 			}
 
 			if ( canApplyNotNull ) {
 				hasNotNull = hasNotNull || applyNotNull( property, descriptor );
 			}
 
 			// apply bean validation specific constraints
 			applyDigits( property, descriptor );
 			applySize( property, descriptor, propertyDesc );
 			applyMin( property, descriptor, dialect );
 			applyMax( property, descriptor, dialect );
 
 			// apply hibernate validator specific constraints - we cannot import any HV specific classes though!
 			// no need to check explicitly for @Range. @Range is a composed constraint using @Min and @Max which
 			// will be taken care later
 			applyLength( property, descriptor, propertyDesc );
 
 			// pass an empty set as composing constraints inherit the main constraint and thus are matching already
 			hasNotNull = hasNotNull || applyConstraints(
 					descriptor.getComposingConstraints(),
 					property, propertyDesc, null,
 					canApplyNotNull,
                     dialect
 			);
 		}
 		return hasNotNull;
 	}
 
 	private static void applyMin(Property property, ConstraintDescriptor<?> descriptor, Dialect dialect) {
 		if ( Min.class.equals( descriptor.getAnnotation().annotationType() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Min> minConstraint = (ConstraintDescriptor<Min>) descriptor;
 			long min = minConstraint.getAnnotation().value();
 
 			Column col = (Column) property.getColumnIterator().next();
 			String checkConstraint = col.getQuotedName(dialect) + ">=" + min;
 			applySQLCheck( col, checkConstraint );
 		}
 	}
 
 	private static void applyMax(Property property, ConstraintDescriptor<?> descriptor, Dialect dialect) {
 		if ( Max.class.equals( descriptor.getAnnotation().annotationType() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Max> maxConstraint = (ConstraintDescriptor<Max>) descriptor;
 			long max = maxConstraint.getAnnotation().value();
 			Column col = (Column) property.getColumnIterator().next();
 			String checkConstraint = col.getQuotedName(dialect) + "<=" + max;
 			applySQLCheck( col, checkConstraint );
 		}
 	}
 
 	private static void applySQLCheck(Column col, String checkConstraint) {
 		String existingCheck = col.getCheckConstraint();
 		// need to check whether the new check is already part of the existing check, because applyDDL can be called
 		// multiple times
 		if ( StringHelper.isNotEmpty( existingCheck ) && !existingCheck.contains( checkConstraint ) ) {
 			checkConstraint = col.getCheckConstraint() + " AND " + checkConstraint;
 		}
 		col.setCheckConstraint( checkConstraint );
 	}
 
 	private static boolean applyNotNull(Property property, ConstraintDescriptor<?> descriptor) {
 		boolean hasNotNull = false;
 		if ( NotNull.class.equals( descriptor.getAnnotation().annotationType() ) ) {
+			// single table inheritance should not be forced to null due to shared state
 			if ( !( property.getPersistentClass() instanceof SingleTableSubclass ) ) {
-				//single table should not be forced to null
-				if ( !property.isComposite() ) { //composite should not add not-null on all columns
-					@SuppressWarnings( "unchecked" )
-					Iterator<Column> iter = property.getColumnIterator();
+				//composite should not add not-null on all columns
+				if ( !property.isComposite() ) {
+					final Iterator<Selectable> iter = property.getColumnIterator();
 					while ( iter.hasNext() ) {
-						iter.next().setNullable( false );
-						hasNotNull = true;
+						final Selectable selectable = iter.next();
+						if ( Column.class.isInstance( selectable ) ) {
+							Column.class.cast( selectable ).setNullable( false );
+						}
+						else {
+							LOG.debugf(
+									"@NotNull was applied to attribute [%s] which is defined (at least partially) " +
+											"by formula(s); formula portions will be skipped",
+									property.getName()
+							);
+						}
 					}
 				}
 			}
 			hasNotNull = true;
 		}
 		return hasNotNull;
 	}
 
 	private static void applyDigits(Property property, ConstraintDescriptor<?> descriptor) {
 		if ( Digits.class.equals( descriptor.getAnnotation().annotationType() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Digits> digitsConstraint = (ConstraintDescriptor<Digits>) descriptor;
 			int integerDigits = digitsConstraint.getAnnotation().integer();
 			int fractionalDigits = digitsConstraint.getAnnotation().fraction();
 			Column col = (Column) property.getColumnIterator().next();
 			col.setPrecision( integerDigits + fractionalDigits );
 			col.setScale( fractionalDigits );
 		}
 	}
 
 	private static void applySize(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
 		if ( Size.class.equals( descriptor.getAnnotation().annotationType() )
 				&& String.class.equals( propertyDescriptor.getElementClass() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Size> sizeConstraint = (ConstraintDescriptor<Size>) descriptor;
 			int max = sizeConstraint.getAnnotation().max();
 			Column col = (Column) property.getColumnIterator().next();
 			if ( max < Integer.MAX_VALUE ) {
 				col.setLength( max );
 			}
 		}
 	}
 
 	private static void applyLength(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
 		if ( "org.hibernate.validator.constraints.Length".equals(
 				descriptor.getAnnotation().annotationType().getName()
 		)
 				&& String.class.equals( propertyDescriptor.getElementClass() ) ) {
 			@SuppressWarnings("unchecked")
 			int max = (Integer) descriptor.getAttributes().get( "max" );
 			Column col = (Column) property.getColumnIterator().next();
 			if ( max < Integer.MAX_VALUE ) {
 				col.setLength( max );
 			}
 		}
 	}
 
 	/**
 	 * @param associatedClass
 	 * @param propertyName
      * @return the property by path in a recursive way, including IdentifierProperty in the loop if propertyName is
      * <code>null</code>.  If propertyName is <code>null</code> or empty, the IdentifierProperty is returned
 	 */
 	private static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
 		Property property = null;
 		Property idProperty = associatedClass.getIdentifierProperty();
 		String idName = idProperty != null ? idProperty.getName() : null;
 		try {
 			if ( propertyName == null
 					|| propertyName.length() == 0
 					|| propertyName.equals( idName ) ) {
 				//default to id
 				property = idProperty;
 			}
 			else {
 				if ( propertyName.indexOf( idName + "." ) == 0 ) {
 					property = idProperty;
 					propertyName = propertyName.substring( idName.length() + 1 );
 				}
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) {
 							return null;
 						}
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 		}
 		catch ( MappingException e ) {
 			try {
 				//if we do not find it try to check the identifier mapper
 				if ( associatedClass.getIdentifierMapper() == null ) {
 					return null;
 				}
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getIdentifierMapper().getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) {
 							return null;
 						}
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 			catch ( MappingException ee ) {
 				return null;
 			}
 		}
 		return property;
 	}
 
 	private static ValidatorFactory getValidatorFactory(Map<Object, Object> properties) {
 		ValidatorFactory factory = null;
 		if ( properties != null ) {
 			Object unsafeProperty = properties.get( FACTORY_PROPERTY );
 			if ( unsafeProperty != null ) {
 				try {
 					factory = ValidatorFactory.class.cast( unsafeProperty );
 				}
 				catch ( ClassCastException e ) {
 					throw new IntegrationException(
 							"Property " + FACTORY_PROPERTY
 									+ " should contain an object of type " + ValidatorFactory.class.getName()
 					);
 				}
 			}
 		}
 		if ( factory == null ) {
 			try {
 				factory = Validation.buildDefaultValidatorFactory();
 			}
 			catch ( Exception e ) {
 				throw new IntegrationException( "Unable to build the default ValidatorFactory", e );
 			}
 		}
 		return factory;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterator.java b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterator.java
index b7a3e02085..28a910d9b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/JoinedIterator.java
@@ -1,107 +1,95 @@
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
 package org.hibernate.internal.util.collections;
 
+import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 
 /**
- * An JoinedIterator is an Iterator that wraps a number of Iterators.
- *
- * This class makes multiple iterators look like one to the caller.
- * When any method from the Iterator interface is called, the JoinedIterator
- * will delegate to a single underlying Iterator. The JoinedIterator will
- * invoke the Iterators in sequence until all Iterators are exhausted.
+ * An Iterator implementation that wraps other Iterators, and presents them all as one
+ * continuous Iterator.  When any method from Iterator is called, we delegate to each
+ * wrapped Iterator in turn until all wrapped Iterators are exhausted.
  *
+ * @author Gavine King
+ * @author Steve Ebersole
  */
-public class JoinedIterator implements Iterator {
-
-	private static final Iterator[] ITERATORS = {};
-
-	// wrapped iterators
-	private Iterator[] iterators;
+public class JoinedIterator<T> implements Iterator<T> {
+	private Iterator<T>[] wrappedIterators;
 
-	// index of current iterator in the wrapped iterators array
 	private int currentIteratorIndex;
+	private Iterator<T> currentIterator;
+	private Iterator<T> lastUsedIterator;
 
-	// the current iterator
-	private Iterator currentIterator;
-
-	// the last used iterator
-	private Iterator lastUsedIterator;
-
-	public JoinedIterator(List iterators) {
-		this( (Iterator[]) iterators.toArray(ITERATORS) );
+	@SuppressWarnings("unchecked")
+	public JoinedIterator(List<Iterator<T>> wrappedIterators) {
+		this( wrappedIterators.toArray( new Iterator[ wrappedIterators.size() ]) );
 	}
 
-	public JoinedIterator(Iterator[] iterators) {
-		if( iterators==null )
-			throw new NullPointerException("Unexpected NULL iterators argument");
-		this.iterators = iterators;
-	}
-
-	public JoinedIterator(Iterator first, Iterator second) {
-		this( new Iterator[] { first, second } );
+	public JoinedIterator(Iterator<T>... iteratorsToWrap) {
+		if( iteratorsToWrap == null ) {
+			throw new NullPointerException( "Iterators to join were null" );
+		}
+		this.wrappedIterators = iteratorsToWrap;
 	}
 
 	public boolean hasNext() {
 		updateCurrentIterator();
 		return currentIterator.hasNext();
 	}
 
-	public Object next() {
+	public T next() {
 		updateCurrentIterator();
 		return currentIterator.next();
 	}
 
 	public void remove() {
 		updateCurrentIterator();
 		lastUsedIterator.remove();
 	}
 
 
 	// call this before any Iterator method to make sure that the current Iterator
 	// is not exhausted
 	protected void updateCurrentIterator() {
-
-		if (currentIterator == null) {
-			if( iterators.length==0  ) {
-				currentIterator = EmptyIterator.INSTANCE;
+		if ( currentIterator == null ) {
+			if( wrappedIterators.length == 0  ) {
+				currentIterator = Collections.emptyIterator();
 			}
 			else {
-				currentIterator = iterators[0];
+				currentIterator = wrappedIterators[0];
 			}
 			// set last used iterator here, in case the user calls remove
 			// before calling hasNext() or next() (although they shouldn't)
 			lastUsedIterator = currentIterator;
 		}
 
-		while (! currentIterator.hasNext() && currentIteratorIndex < iterators.length - 1) {
+		while (! currentIterator.hasNext() && currentIteratorIndex < wrappedIterators.length - 1) {
 			currentIteratorIndex++;
-			currentIterator = iterators[currentIteratorIndex];
+			currentIterator = wrappedIterators[currentIteratorIndex];
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
index 49dd3a0bc4..ba1ec4e63d 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
@@ -1,669 +1,670 @@
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
 package org.hibernate.mapping;
 import java.util.ArrayList;
+import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Mapping for a collection. Subclasses specialize to particular collection styles.
  * 
  * @author Gavin King
  */
 public abstract class Collection implements Fetchable, Value, Filterable {
 
 	public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
 	public static final String DEFAULT_KEY_COLUMN_NAME = "id";
 
 	private final Mappings mappings;
 	private PersistentClass owner;
 
 	private KeyValue key;
 	private Value element;
 	private Table collectionTable;
 	private String role;
 	private boolean lazy;
 	private boolean extraLazy;
 	private boolean inverse;
 	private boolean mutable = true;
 	private boolean subselectLoadable;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String orderBy;
 	private String where;
 	private String manyToManyWhere;
 	private String manyToManyOrderBy;
 	private String referencedPropertyName;
 	private String nodeName;
 	private String elementNodeName;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 	private boolean orphanDelete;
 	private int batchSize = -1;
 	private FetchMode fetchMode;
 	private boolean embedded = true;
 	private boolean optimisticLocked = true;
 	private Class collectionPersisterClass;
 	private String typeName;
 	private Properties typeParameters;
 	private final java.util.List filters = new ArrayList();
 	private final java.util.List manyToManyFilters = new ArrayList();
 	private final java.util.Set synchronizedTables = new HashSet();
 
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private String customSQLDeleteAll;
 	private boolean customDeleteAllCallable;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private String loaderName;
 
 	protected Collection(Mappings mappings, PersistentClass owner) {
 		this.mappings = mappings;
 		this.owner = owner;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isSet() {
 		return false;
 	}
 
 	public KeyValue getKey() {
 		return key;
 	}
 
 	public Value getElement() {
 		return element;
 	}
 
 	public boolean isIndexed() {
 		return false;
 	}
 
 	public Table getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table table) {
 		this.collectionTable = table;
 	}
 
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	public Comparator getComparator() {
 		if ( comparator == null && comparatorClassName != null ) {
 			try {
 				setComparator( (Comparator) ReflectHelper.classForName( comparatorClassName ).newInstance() );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Could not instantiate comparator class [" + comparatorClassName
 						+ "] for collection " + getRole()  
 				);
 			}
 		}
 		return comparator;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public abstract CollectionType getDefaultCollectionType() throws MappingException;
 
 	public boolean isPrimitiveArray() {
 		return false;
 	}
 
 	public boolean isArray() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 
 	public boolean isOneToMany() {
 		return element instanceof OneToMany;
 	}
 
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public String getOwnerEntityName() {
 		return owner.getEntityName();
 	}
 
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public void setElement(Value element) {
 		this.element = element;
 	}
 
 	public void setKey(KeyValue key) {
 		this.key = key;
 	}
 
 	public void setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	public void setSorted(boolean sorted) {
 		this.sorted = sorted;
 	}
 
 	public void setInverse(boolean inverse) {
 		this.inverse = inverse;
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	/**
 	 * @deprecated Inject the owner into constructor.
 	 *
 	 * @param owner The owner
 	 */
 	@Deprecated
     public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public String getWhere() {
 		return where;
 	}
 
 	public void setWhere(String where) {
 		this.where = where;
 	}
 
 	public String getManyToManyWhere() {
 		return manyToManyWhere;
 	}
 
 	public void setManyToManyWhere(String manyToManyWhere) {
 		this.manyToManyWhere = manyToManyWhere;
 	}
 
 	public String getManyToManyOrdering() {
 		return manyToManyOrderBy;
 	}
 
 	public void setManyToManyOrdering(String orderFragment) {
 		this.manyToManyOrderBy = orderFragment;
 	}
 
 	public boolean isIdentified() {
 		return false;
 	}
 
 	public boolean hasOrphanDelete() {
 		return orphanDelete;
 	}
 
 	public void setOrphanDelete(boolean orphanDelete) {
 		this.orphanDelete = orphanDelete;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int i) {
 		batchSize = i;
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	public void setCollectionPersisterClass(Class persister) {
 		this.collectionPersisterClass = persister;
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		if ( getKey().isCascadeDeleteEnabled() && ( !isInverse() || !isOneToMany() ) ) {
 			throw new MappingException(
 				"only inverse one-to-many associations may use on-delete=\"cascade\": " 
 				+ getRole() );
 		}
 		if ( !getKey().isValid( mapping ) ) {
 			throw new MappingException(
 				"collection foreign key mapping has wrong number of columns: "
 				+ getRole()
 				+ " type: "
 				+ getKey().getType().getName() );
 		}
 		if ( !getElement().isValid( mapping ) ) {
 			throw new MappingException( 
 				"collection element mapping has wrong number of columns: "
 				+ getRole()
 				+ " type: "
 				+ getElement().getType().getName() );
 		}
 
 		checkColumnDuplication();
 		
 		if ( elementNodeName!=null && elementNodeName.startsWith("@") ) {
 			throw new MappingException("element node must not be an attribute: " + elementNodeName );
 		}
 		if ( elementNodeName!=null && elementNodeName.equals(".") ) {
 			throw new MappingException("element node must not be the parent: " + elementNodeName );
 		}
 		if ( nodeName!=null && nodeName.indexOf('@')>-1 ) {
 			throw new MappingException("collection node must not be an attribute: " + elementNodeName );
 		}
 	}
 
 	private void checkColumnDuplication(java.util.Set distinctColumns, Iterator columns)
 			throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable s = (Selectable) columns.next();
 			if ( !s.isFormula() ) {
 				Column col = (Column) s;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException( "Repeated column in mapping for collection: "
 						+ getRole()
 						+ " column: "
 						+ col.getName() );
 				}
 			}
 		}
 	}
 
 	private void checkColumnDuplication() throws MappingException {
 		HashSet cols = new HashSet();
 		checkColumnDuplication( cols, getKey().getColumnIterator() );
 		if ( isIndexed() ) {
 			checkColumnDuplication( cols, ( (IndexedCollection) this )
 				.getIndex()
 				.getColumnIterator() );
 		}
 		if ( isIdentified() ) {
 			checkColumnDuplication( cols, ( (IdentifierCollection) this )
 				.getIdentifier()
 				.getColumnIterator() );
 		}
 		if ( !isOneToMany() ) {
 			checkColumnDuplication( cols, getElement().getColumnIterator() );
 		}
 	}
 
-	public Iterator getColumnIterator() {
-		return EmptyIterator.INSTANCE;
+	public Iterator<Selectable> getColumnIterator() {
+		return Collections.emptyIterator();
 	}
 
 	public int getColumnSpan() {
 		return 0;
 	}
 
 	public Type getType() throws MappingException {
 		return getCollectionType();
 	}
 
 	public CollectionType getCollectionType() {
 		if ( typeName == null ) {
 			return getDefaultCollectionType();
 		}
 		else {
 			return mappings.getTypeResolver()
 					.getTypeFactory()
 					.customCollection( typeName, typeParameters, role, referencedPropertyName );
 		}
 	}
 
 	public boolean isNullable() {
 		return true;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public Table getTable() {
 		return owner.getTable();
 	}
 
 	public void createForeignKey() {
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return true;
 	}
 
 	private void createForeignKeys() throws MappingException {
 		// if ( !isInverse() ) { // for inverse collections, let the "other end" handle it
 		if ( referencedPropertyName == null ) {
 			getElement().createForeignKey();
 			key.createForeignKeyOfEntity( getOwner().getEntityName() );
 		}
 		// }
 	}
 
 	abstract void createPrimaryKey();
 
 	public void createAllKeys() throws MappingException {
 		createForeignKeys();
 		if ( !isInverse() ) createPrimaryKey();
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
 		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName == null ? role : cacheRegionName;
 	}
 
 	public void setCacheRegionName(String cacheRegionName) {
 		this.cacheRegionName = cacheRegionName;
 	}
 
 
 
 	public void setCustomSQLInsert(String customSQLInsert, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLInsert = customSQLInsert;
 		this.customInsertCallable = callable;
 		this.insertCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLInsert() {
 		return customSQLInsert;
 	}
 
 	public boolean isCustomInsertCallable() {
 		return customInsertCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLUpdate = customSQLUpdate;
 		this.customUpdateCallable = callable;
 		this.updateCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLUpdate() {
 		return customSQLUpdate;
 	}
 
 	public boolean isCustomUpdateCallable() {
 		return customUpdateCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	public void setCustomSQLDelete(String customSQLDelete, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDelete = customSQLDelete;
 		this.customDeleteCallable = callable;
 		this.deleteCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDelete() {
 		return customSQLDelete;
 	}
 
 	public boolean isCustomDeleteCallable() {
 		return customDeleteCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	public void setCustomSQLDeleteAll(String customSQLDeleteAll, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDeleteAll = customSQLDeleteAll;
 		this.customDeleteAllCallable = callable;
 		this.deleteAllCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDeleteAll() {
 		return customSQLDeleteAll;
 	}
 
 	public boolean isCustomDeleteAllCallable() {
 		return customDeleteAllCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public void addFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
 		filters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap, null));
 	}
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public void addManyToManyFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
 		manyToManyFilters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap, null));
 	}
 
 	public java.util.List getManyToManyFilters() {
 		return manyToManyFilters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public java.util.Set getSynchronizedTables() {
 		return synchronizedTables;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String name) {
 		this.loaderName = name;
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String propertyRef) {
 		this.referencedPropertyName = propertyRef;
 	}
 
 	public boolean isOptimisticLocked() {
 		return optimisticLocked;
 	}
 
 	public void setOptimisticLocked(boolean optimisticLocked) {
 		this.optimisticLocked = optimisticLocked;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		this.typeName = typeName;
 	}
 
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 
 	public boolean[] getColumnInsertability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public boolean[] getColumnUpdateability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public void setElementNodeName(String elementNodeName) {
 		this.elementNodeName = elementNodeName;
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 	
 
 	public void setSubselectLoadable(boolean subqueryLoadable) {
 		this.subselectLoadable = subqueryLoadable;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isExtraLazy() {
 		return extraLazy;
 	}
 
 	public void setExtraLazy(boolean extraLazy) {
 		this.extraLazy = extraLazy;
 	}
 	
 	public boolean hasOrder() {
 		return orderBy!=null || manyToManyOrderBy!=null;
 	}
 
 	public void setComparatorClassName(String comparatorClassName) {
 		this.comparatorClassName = comparatorClassName;		
 	}
 	
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
index fd48b6156c..7a2bfe4ee6 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
@@ -1,433 +1,433 @@
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
 package org.hibernate.mapping;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.CompositeNestedGeneratedValueGenerator;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.property.Setter;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeFactory;
 
 /**
  * The mapping for a component, composite element,
  * composite identifier, etc.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class Component extends SimpleValue implements MetaAttributable {
 	private ArrayList properties = new ArrayList();
 	private String componentClassName;
 	private boolean embedded;
 	private String parentProperty;
 	private PersistentClass owner;
 	private boolean dynamic;
 	private Map metaAttributes;
 	private String nodeName;
 	private boolean isKey;
 	private String roleName;
 
 	private java.util.Map tuplizerImpls;
 
 	public Component(Mappings mappings, PersistentClass owner) throws MappingException {
 		super( mappings, owner.getTable() );
 		this.owner = owner;
 	}
 
 	public Component(Mappings mappings, Component component) throws MappingException {
 		super( mappings, component.getTable() );
 		this.owner = component.getOwner();
 	}
 
 	public Component(Mappings mappings, Join join) throws MappingException {
 		super( mappings, join.getTable() );
 		this.owner = join.getPersistentClass();
 	}
 
 	public Component(Mappings mappings, Collection collection) throws MappingException {
 		super( mappings, collection.getCollectionTable() );
 		this.owner = collection.getOwner();
 	}
 
 	public int getPropertySpan() {
 		return properties.size();
 	}
 	public Iterator getPropertyIterator() {
 		return properties.iterator();
 	}
 	public void addProperty(Property p) {
 		properties.add(p);
 	}
 	public void addColumn(Column column) {
 		throw new UnsupportedOperationException("Cant add a column to a component");
 	}
 	public int getColumnSpan() {
 		int n=0;
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property p = (Property) iter.next();
 			n+= p.getColumnSpan();
 		}
 		return n;
 	}
-	public Iterator getColumnIterator() {
+	public Iterator<Selectable> getColumnIterator() {
 		Iterator[] iters = new Iterator[ getPropertySpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			iters[i++] = ( (Property) iter.next() ).getColumnIterator();
 		}
 		return new JoinedIterator(iters);
 	}
 
 	public void setTypeByReflection(String propertyClass, String propertyName) {}
 
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	public String getComponentClassName() {
 		return componentClassName;
 	}
 
 	public Class getComponentClass() throws MappingException {
 		try {
 			return ReflectHelper.classForName(componentClassName);
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("component class not found: " + componentClassName, cnfe);
 		}
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	public String getParentProperty() {
 		return parentProperty;
 	}
 
 	public void setComponentClassName(String componentClass) {
 		this.componentClassName = componentClass;
 	}
 
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public void setParentProperty(String parentProperty) {
 		this.parentProperty = parentProperty;
 	}
 
 	public boolean isDynamic() {
 		return dynamic;
 	}
 
 	public void setDynamic(boolean dynamic) {
 		this.dynamic = dynamic;
 	}
 
 	public Type getType() throws MappingException {
 		// TODO : temporary initial step towards HHH-1907
 		final ComponentMetamodel metamodel = new ComponentMetamodel( this );
 		final TypeFactory factory = getMappings().getTypeResolver().getTypeFactory();
 		return isEmbedded() ? factory.embeddedComponent( metamodel ) : factory.component( metamodel );
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName)
 		throws MappingException {
 	}
 	
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
 	public MetaAttribute getMetaAttribute(String attributeName) {
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(attributeName);
 	}
 
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			boolean[] chunk = prop.getValue().getColumnInsertability();
 			if ( prop.isInsertable() ) {
 				System.arraycopy(chunk, 0, result, i, chunk.length);
 			}
 			i+=chunk.length;
 		}
 		return result;
 	}
 
 	public boolean[] getColumnUpdateability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			boolean[] chunk = prop.getValue().getColumnUpdateability();
 			if ( prop.isUpdateable() ) {
 				System.arraycopy(chunk, 0, result, i, chunk.length);
 			}
 			i+=chunk.length;
 		}
 		return result;
 	}
 	
 	public String getNodeName() {
 		return nodeName;
 	}
 	
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 	
 	public boolean isKey() {
 		return isKey;
 	}
 	
 	public void setKey(boolean isKey) {
 		this.isKey = isKey;
 	}
 	
 	public boolean hasPojoRepresentation() {
 		return componentClassName!=null;
 	}
 
 	public void addTuplizer(EntityMode entityMode, String implClassName) {
 		if ( tuplizerImpls == null ) {
 			tuplizerImpls = new HashMap();
 		}
 		tuplizerImpls.put( entityMode, implClassName );
 	}
 
 	public String getTuplizerImplClassName(EntityMode mode) {
 		// todo : remove this once ComponentMetamodel is complete and merged
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return ( String ) tuplizerImpls.get( mode );
 	}
 
 	public Map getTuplizerMap() {
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return java.util.Collections.unmodifiableMap( tuplizerImpls );
 	}
 
 	public Property getProperty(String propertyName) throws MappingException {
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( prop.getName().equals(propertyName) ) {
 				return prop;
 			}
 		}
 		throw new MappingException("component property not found: " + propertyName);
 	}
 
 	public String getRoleName() {
 		return roleName;
 	}
 
 	public void setRoleName(String roleName) {
 		this.roleName = roleName;
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + properties.toString() + ')';
 	}
 
 	private IdentifierGenerator builtIdentifierGenerator;
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect,
 			String defaultCatalog,
 			String defaultSchema,
 			RootClass rootClass) throws MappingException {
 		if ( builtIdentifierGenerator == null ) {
 			builtIdentifierGenerator = buildIdentifierGenerator(
 					identifierGeneratorFactory,
 					dialect,
 					defaultCatalog,
 					defaultSchema,
 					rootClass
 			);
 		}
 		return builtIdentifierGenerator;
 	}
 
 	private IdentifierGenerator buildIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect,
 			String defaultCatalog,
 			String defaultSchema,
 			RootClass rootClass) throws MappingException {
 		final boolean hasCustomGenerator = ! DEFAULT_ID_GEN_STRATEGY.equals( getIdentifierGeneratorStrategy() );
 		if ( hasCustomGenerator ) {
 			return super.createIdentifierGenerator(
 					identifierGeneratorFactory, dialect, defaultCatalog, defaultSchema, rootClass
 			);
 		}
 
 		final Class entityClass = rootClass.getMappedClass();
 		final Class attributeDeclarer; // what class is the declarer of the composite pk attributes
 		CompositeNestedGeneratedValueGenerator.GenerationContextLocator locator;
 
 		// IMPL NOTE : See the javadoc discussion on CompositeNestedGeneratedValueGenerator wrt the
 		//		various scenarios for which we need to account here
 		if ( rootClass.getIdentifierMapper() != null ) {
 			// we have the @IdClass / <composite-id mapped="true"/> case
 			attributeDeclarer = resolveComponentClass();
 		}
 		else if ( rootClass.getIdentifierProperty() != null ) {
 			// we have the "@EmbeddedId" / <composite-id name="idName"/> case
 			attributeDeclarer = resolveComponentClass();
 		}
 		else {
 			// we have the "straight up" embedded (again the hibernate term) component identifier
 			attributeDeclarer = entityClass;
 		}
 
 		locator = new StandardGenerationContextLocator( rootClass.getEntityName() );
 		final CompositeNestedGeneratedValueGenerator generator = new CompositeNestedGeneratedValueGenerator( locator );
 
 		Iterator itr = getPropertyIterator();
 		while ( itr.hasNext() ) {
 			final Property property = (Property) itr.next();
 			if ( property.getValue().isSimpleValue() ) {
 				final SimpleValue value = (SimpleValue) property.getValue();
 
 				if ( DEFAULT_ID_GEN_STRATEGY.equals( value.getIdentifierGeneratorStrategy() ) ) {
 					// skip any 'assigned' generators, they would have been handled by
 					// the StandardGenerationContextLocator
 					continue;
 				}
 
 				final IdentifierGenerator valueGenerator = value.createIdentifierGenerator(
 						identifierGeneratorFactory,
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						rootClass
 				);
 				generator.addGeneratedValuePlan(
 						new ValueGenerationPlan(
 								property.getName(),
 								valueGenerator,
 								injector( property, attributeDeclarer )
 						)
 				);
 			}
 		}
 		return generator;
 	}
 
 	private Setter injector(Property property, Class attributeDeclarer) {
 		return property.getPropertyAccessor( attributeDeclarer )
 				.getSetter( attributeDeclarer, property.getName() );
 	}
 
 	private Class resolveComponentClass() {
 		try {
 			return getComponentClass();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
 	public static class StandardGenerationContextLocator
 			implements CompositeNestedGeneratedValueGenerator.GenerationContextLocator {
 		private final String entityName;
 
 		public StandardGenerationContextLocator(String entityName) {
 			this.entityName = entityName;
 		}
 
 		public Serializable locateGenerationContext(SessionImplementor session, Object incomingObject) {
 			return session.getEntityPersister( entityName, incomingObject ).getIdentifier( incomingObject, session );
 		}
 	}
 
 	public static class ValueGenerationPlan implements CompositeNestedGeneratedValueGenerator.GenerationPlan {
 		private final String propertyName;
 		private final IdentifierGenerator subGenerator;
 		private final Setter injector;
 
 		public ValueGenerationPlan(
 				String propertyName,
 				IdentifierGenerator subGenerator,
 				Setter injector) {
 			this.propertyName = propertyName;
 			this.subGenerator = subGenerator;
 			this.injector = injector;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public void execute(SessionImplementor session, Object incomingObject, Object injectionContext) {
 			final Object generatedValue = subGenerator.generate( session, incomingObject );
 			injector.set( injectionContext, generatedValue, session.getFactory() );
 		}
 
 		public void registerPersistentGenerators(Map generatorMap) {
 			if ( PersistentIdentifierGenerator.class.isInstance( subGenerator ) ) {
 				generatorMap.put( ( (PersistentIdentifierGenerator) subGenerator ).generatorKey(), subGenerator );
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
index 5abfefd081..18b45cded1 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
@@ -1,179 +1,179 @@
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
 package org.hibernate.mapping;
 import java.util.Iterator;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A mapping for a one-to-many association
  * @author Gavin King
  */
 public class OneToMany implements Value {
 
 	private final Mappings mappings;
 	private final Table referencingTable;
 
 	private String referencedEntityName;
 	private PersistentClass associatedClass;
 	private boolean embedded;
 	private boolean ignoreNotFound;
 
 	private EntityType getEntityType() {
 		return mappings.getTypeResolver().getTypeFactory().manyToOne(
 				getReferencedEntityName(), 
 				null, 
 				false,
 				false,
 				isIgnoreNotFound(),
 				false
 			);
 	}
 
 	public OneToMany(Mappings mappings, PersistentClass owner) throws MappingException {
 		this.mappings = mappings;
 		this.referencingTable = (owner==null) ? null : owner.getTable();
 	}
 
 	public PersistentClass getAssociatedClass() {
 		return associatedClass;
 	}
 
     /**
      * Associated entity on the many side
      */
 	public void setAssociatedClass(PersistentClass associatedClass) {
 		this.associatedClass = associatedClass;
 	}
 
 	public void createForeignKey() {
 		// no foreign key element of for a one-to-many
 	}
 
-	public Iterator getColumnIterator() {
+	public Iterator<Selectable> getColumnIterator() {
 		return associatedClass.getKey().getColumnIterator();
 	}
 
 	public int getColumnSpan() {
 		return associatedClass.getKey().getColumnSpan();
 	}
 
 	public FetchMode getFetchMode() {
 		return FetchMode.JOIN;
 	}
 
     /** 
      * Table of the owner entity (the "one" side)
      */
 	public Table getTable() {
 		return referencingTable;
 	}
 
 	public Type getType() {
 		return getEntityType();
 	}
 
 	public boolean isNullable() {
 		return false;
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 	
 	public boolean isValid(Mapping mapping) throws MappingException {
 		if (referencedEntityName==null) {
 			throw new MappingException("one to many association must specify the referenced entity");
 		}
 		return true;
 	}
 
     public String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
     /** 
      * Associated entity on the "many" side
      */    
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName==null ? null : referencedEntityName.intern();
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	
 	public boolean[] getColumnInsertability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public boolean isIgnoreNotFound() {
 		return ignoreNotFound;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Selectable.java b/hibernate-core/src/main/java/org/hibernate/mapping/Selectable.java
index 92a370e8f3..b79b1b89ea 100755
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Selectable.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Selectable.java
@@ -1,35 +1,39 @@
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
 package org.hibernate.mapping;
+
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 
+/**
+ * Models the commonality between a column and a formula (computed value).
+ */
 public interface Selectable {
 	public String getAlias(Dialect dialect);
 	public String getAlias(Dialect dialect, Table table);
 	public boolean isFormula();
 	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry);
 	public String getText(Dialect dialect);
 	public String getText();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index f5fb84a81b..a9bbdb3619 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,610 +1,611 @@
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
 package org.hibernate.mapping;
 
 import javax.persistence.AttributeConverter;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.TypeVariable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final Logger log = Logger.getLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final Mappings mappings;
 
-	private final List columns = new ArrayList();
+	private final List<Selectable> columns = new ArrayList<Selectable>();
+
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition jpaAttributeConverterDefinition;
 	private Type type;
 
 	public SimpleValue(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public SimpleValue(Mappings mappings, Table table) {
 		this( mappings );
 		this.table = table;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
 	
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
-	public Iterator getColumnIterator() {
+	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
 	public List getConstraintColumns() {
 		return columns;
 	}
 	public String getTypeName() {
 		return typeName;
 	}
 	public void setTypeName(String type) {
 		this.typeName = type;
 	}
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void createForeignKey() throws MappingException {}
 
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) tables.append(", ");
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		params.put(
 				Environment.PREFER_POOLED_VALUES_LO,
 				mappings.getConfigurationProperties().getProperty( Environment.PREFER_POOLED_VALUES_LO, "false" )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 		
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		if ( hasFormula() ) return true;
 		boolean nullable = true;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			if ( !( (Column) iter.next() ).isNullable() ) {
 				nullable = false;
 				return nullable; //shortcut
 			}
 		}
 		return nullable;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 		if ( typeParameters != null
 				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
 				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
 			createParameterImpl();
 		}
 
 		Type result = mappings.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	@SuppressWarnings("unchecked")
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
 		if ( jpaAttributeConverterDefinition == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "you must specify types for a dynamic entity: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 			return;
 		}
 
 		// we had an AttributeConverter...
 
 		// todo : we should validate the number of columns present
 		// todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 		//		then we can "play them against each other" in terms of determining proper typing
 		// todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 
 		// AttributeConverter works totally in memory, meaning it converts between one Java representation (the entity
 		// attribute representation) and another (the value bound into JDBC statements or extracted from results).
 		// However, the Hibernate Type system operates at the lower level of actually dealing with those JDBC objects.
 		// So even though we have an AttributeConverter, we still need to "fill out" the rest of the BasicType
 		// data.  For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
 		// "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
 		// and use that type-code to resolve the SqlTypeDescriptor to use.
 		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
 		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 
 		final JavaTypeDescriptor javaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// the adapter here injects the AttributeConverter calls into the binding/extraction process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				jpaAttributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor
 		);
 
 		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
 		type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, javaTypeDescriptor ) {
 			@Override
 			public String getName() {
 				return name;
 			}
 		};
 		log.debug( "Created : " + name );
 
 		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition jpaAttributeConverterDefinition) {
 		this.jpaAttributeConverterDefinition = jpaAttributeConverterDefinition;
 	}
 
 	public static class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
 		private final AttributeConverter converter;
 		private final SqlTypeDescriptor delegate;
 
 		public AttributeConverterSqlTypeDescriptorAdapter(AttributeConverter converter, SqlTypeDescriptor delegate) {
 			this.converter = converter;
 			this.delegate = delegate;
 		}
 
 		@Override
 		public int getSqlType() {
 			return delegate.getSqlType();
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return delegate.canBeRemapped();
 		}
 
 		@Override
 		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueBinder realBinder = delegate.getBinder( javaTypeDescriptor );
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 						throws SQLException {
 					realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
 				}
 			};
 		}
 
 		@Override
 		public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueExtractor realExtractor = delegate.getExtractor( javaTypeDescriptor );
 			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
 						throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, new String[] {name}, options ) );
 				}
 			};
 		}
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				columnsNames[i] = ( (Column) columns.get( i ) ).getName();
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							ReflectHelper.classForName(
 									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
 							),
 							annotations,
 							table.getCatalog(),
 							table.getSchema(),
 							table.getName(),
 							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
 							columnsNames
 					)
 			);
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, cnfe );
 		}
 	}
 
 	private final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
 		private final Class returnedClass;
 		private final Annotation[] annotationsMethod;
 		private final String catalog;
 		private final String schema;
 		private final String table;
 		private final boolean primaryKey;
 		private final String[] columns;
 
 		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
 				String table, boolean primaryKey, String[] columns) {
 			this.returnedClass = returnedClass;
 			this.annotationsMethod = annotationsMethod;
 			this.catalog = catalog;
 			this.schema = schema;
 			this.table = table;
 			this.primaryKey = primaryKey;
 			this.columns = columns;
 		}
 
 		@Override
 		public Class getReturnedClass() {
 			return returnedClass;
 		}
 
 		@Override
 		public Annotation[] getAnnotationsMethod() {
 			return annotationsMethod;
 		}
 
 		@Override
 		public String getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String getSchema() {
 			return schema;
 		}
 
 		@Override
 		public String getTable() {
 			return table;
 		}
 
 		@Override
 		public boolean isPrimaryKey() {
 			return primaryKey;
 		}
 
 		@Override
 		public String[] getColumns() {
 			return columns;
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Value.java b/hibernate-core/src/main/java/org/hibernate/mapping/Value.java
index eaa460e64a..731216baf1 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Value.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Value.java
@@ -1,58 +1,58 @@
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
 package org.hibernate.mapping;
 import java.io.Serializable;
 import java.util.Iterator;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.type.Type;
 
 /**
  * A value is anything that is persisted by value, instead of
  * by reference. It is essentially a Hibernate Type, together
  * with zero or more columns. Values are wrapped by things with
  * higher level semantics, for example properties, collections,
  * classes.
  *
  * @author Gavin King
  */
 public interface Value extends Serializable {
 	public int getColumnSpan();
-	public Iterator getColumnIterator();
+	public Iterator<Selectable> getColumnIterator();
 	public Type getType() throws MappingException;
 	public FetchMode getFetchMode();
 	public Table getTable();
 	public boolean hasFormula();
 	public boolean isAlternateUniqueKey();
 	public boolean isNullable();
 	public boolean[] getColumnUpdateability();
 	public boolean[] getColumnInsertability();
 	public void createForeignKey() throws MappingException;
 	public boolean isSimpleValue();
 	public boolean isValid(Mapping mapping) throws MappingException;
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException;
 	public Object accept(ValueVisitor visitor);
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedDetail.java b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedDetail.java
new file mode 100644
index 0000000000..b2d090cc3d
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedDetail.java
@@ -0,0 +1,42 @@
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
+package org.hibernate.test.formulajoin;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class AnnotatedDetail {
+	@Id
+	private Integer id;
+	private String name;
+
+	// because otherwise schema export would not know about it...
+	@Column( name = "detail_domain" )
+	private String domain;
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedFormWithBeanValidationNotNullTest.java b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedFormWithBeanValidationNotNullTest.java
new file mode 100644
index 0000000000..2bb218be18
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedFormWithBeanValidationNotNullTest.java
@@ -0,0 +1,44 @@
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
+package org.hibernate.test.formulajoin;
+
+import org.hibernate.cfg.Configuration;
+
+import org.junit.Test;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+/**
+ * @author Steve Ebersole
+ */
+public class AnnotatedFormWithBeanValidationNotNullTest extends BaseUnitTestCase {
+	@Test
+	@TestForIssue( jiraKey = "HHH-8167" )
+	public void testAnnotatedFormWithBeanValidationNotNull() {
+		Configuration cfg = new Configuration();
+		cfg.addAnnotatedClass( AnnotatedMaster.class ).addAnnotatedClass( AnnotatedDetail.class );
+		cfg.buildSessionFactory();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedMaster.java b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedMaster.java
new file mode 100644
index 0000000000..17779287f9
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/AnnotatedMaster.java
@@ -0,0 +1,55 @@
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
+package org.hibernate.test.formulajoin;
+
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.validation.constraints.NotNull;
+
+import org.hibernate.annotations.Fetch;
+import org.hibernate.annotations.FetchMode;
+import org.hibernate.annotations.JoinColumnOrFormula;
+import org.hibernate.annotations.JoinColumnsOrFormulas;
+import org.hibernate.annotations.JoinFormula;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class AnnotatedMaster {
+	@Id
+	private Integer id;
+	private String name;
+	@ManyToOne(fetch= FetchType.EAGER, optional=false)
+	@JoinColumnsOrFormulas({
+			@JoinColumnOrFormula(formula=@JoinFormula(value="my_domain_key'", referencedColumnName="detail_domain")),
+			@JoinColumnOrFormula(column=@JoinColumn(name="detail", referencedColumnName="id"))
+	})
+	@Fetch(FetchMode.JOIN)
+	@NotNull
+	private AnnotatedDetail detail;
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/formulajoin/FormulaJoinTest.java b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/FormulaJoinTest.java
index 6f95c2eb91..de3f0728ea 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/formulajoin/FormulaJoinTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/formulajoin/FormulaJoinTest.java
@@ -1,116 +1,117 @@
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
 package org.hibernate.test.formulajoin;
 import java.util.List;
 
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
+
+import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gavin King
  */
 public class FormulaJoinTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "formulajoin/Master.hbm.xml" };
 	}
 
 	@Test
 	public void testFormulaJoin() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Master master = new Master();
 		master.setName("master 1");
 		Detail current = new Detail();
 		current.setCurrentVersion(true);
 		current.setVersion(2);
 		current.setDetails("details of master 1 blah blah");
 		current.setMaster(master);
 		master.setDetail(current);
 		Detail past = new Detail();
 		past.setCurrentVersion(false);
 		past.setVersion(1);
 		past.setDetails("old details of master 1 yada yada");
 		past.setMaster(master);
 		s.persist(master);
 		s.persist(past);
 		s.persist(current);
 		tx.commit();
 		s.close();
 		
 		if ( getDialect() instanceof PostgreSQLDialect  || getDialect() instanceof PostgreSQL81Dialect ) return;
 
 		s = openSession();
 		tx = s.beginTransaction();
 		List l = s.createQuery("from Master m left join m.detail d").list();
 		assertEquals( l.size(), 1 );
 		tx.commit();
 		s.close();
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		l = s.createQuery("from Master m left join fetch m.detail").list();
 		assertEquals( l.size(), 1 );
 		Master m = (Master) l.get(0);
 		assertEquals( "master 1", m.getDetail().getMaster().getName() );
 		assertTrue( m==m.getDetail().getMaster() );
 		tx.commit();
 		s.close();
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		l = s.createQuery("from Master m join fetch m.detail").list();
 		assertEquals( l.size(), 1 );
 		tx.commit();
 		s.close();
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		l = s.createQuery("from Detail d join fetch d.currentMaster.master").list();
 		assertEquals( l.size(), 2 );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		l = s.createQuery("from Detail d join fetch d.currentMaster.master m join fetch m.detail").list();
 		assertEquals( l.size(), 2 );
 		
 		s.createQuery("delete from Detail").executeUpdate();
 		s.createQuery("delete from Master").executeUpdate();
 		
 		tx.commit();
 		s.close();
 
 	}
-
 }
 
