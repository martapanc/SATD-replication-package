diff --git a/hibernate-core/src/main/java/org/hibernate/Hibernate.java b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
index 87591f8076..53b3cb89ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+++ b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
@@ -1,489 +1,488 @@
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
 package org.hibernate;
 import java.util.Iterator;
 import java.util.Properties;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.HibernateIterator;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.intercept.FieldInterceptionHelper;
 import org.hibernate.intercept.FieldInterceptor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.AnyType;
 import org.hibernate.type.BigDecimalType;
 import org.hibernate.type.BigIntegerType;
 import org.hibernate.type.BinaryType;
 import org.hibernate.type.BlobType;
 import org.hibernate.type.BooleanType;
 import org.hibernate.type.ByteType;
 import org.hibernate.type.CalendarDateType;
 import org.hibernate.type.CalendarType;
 import org.hibernate.type.CharArrayType;
 import org.hibernate.type.CharacterArrayType;
 import org.hibernate.type.CharacterType;
 import org.hibernate.type.ClassType;
 import org.hibernate.type.ClobType;
 import org.hibernate.type.CurrencyType;
 import org.hibernate.type.DateType;
 import org.hibernate.type.DoubleType;
 import org.hibernate.type.FloatType;
 import org.hibernate.type.ImageType;
 import org.hibernate.type.IntegerType;
 import org.hibernate.type.LocaleType;
 import org.hibernate.type.LongType;
 import org.hibernate.type.ManyToOneType;
 import org.hibernate.type.MaterializedBlobType;
 import org.hibernate.type.MaterializedClobType;
 import org.hibernate.type.ObjectType;
 import org.hibernate.type.SerializableType;
 import org.hibernate.type.ShortType;
 import org.hibernate.type.StringType;
 import org.hibernate.type.TextType;
 import org.hibernate.type.TimeType;
 import org.hibernate.type.TimeZoneType;
 import org.hibernate.type.TimestampType;
 import org.hibernate.type.TrueFalseType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeFactory;
 import org.hibernate.type.WrapperBinaryType;
 import org.hibernate.type.YesNoType;
 import org.hibernate.usertype.CompositeUserType;
 
 /**
  * <ul>
  * <li>Provides access to the full range of Hibernate built-in types. <tt>Type</tt>
  * instances may be used to bind values to query parameters.
  * <li>A factory for new <tt>Blob</tt>s and <tt>Clob</tt>s.
  * <li>Defines static methods for manipulation of proxies.
  * </ul>
  *
  * @author Gavin King
  * @see java.sql.Clob
  * @see java.sql.Blob
  * @see org.hibernate.type.Type
  */
 
 public final class Hibernate {
 	/**
 	 * Cannot be instantiated.
 	 */
 	private Hibernate() {
 		throw new UnsupportedOperationException();
 	}
 	/**
 	 * Hibernate <tt>boolean</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BOOLEAN} instead.
 	 */
 	public static final BooleanType BOOLEAN = BooleanType.INSTANCE;
 	/**
 	 * Hibernate <tt>true_false</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TRUE_FALSE} instead.
 	 */
 	public static final TrueFalseType TRUE_FALSE = TrueFalseType.INSTANCE;
 	/**
 	 * Hibernate <tt>yes_no</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#YES_NO} instead.
 	 */
 	public static final YesNoType YES_NO = YesNoType.INSTANCE;
 	/**
 	 * Hibernate <tt>byte</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BYTE} instead.
 	 */
 	public static final ByteType BYTE = ByteType.INSTANCE;
 	/**
 	 * Hibernate <tt>short</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#SHORT} instead.
 	 */
 	public static final ShortType SHORT = ShortType.INSTANCE;
 	/**
 	 * Hibernate <tt>integer</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#INTEGER} instead.
 	 */
 	public static final IntegerType INTEGER = IntegerType.INSTANCE;
 	/**
 	 * Hibernate <tt>long</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#LONG} instead.
 	 */
 	public static final LongType LONG = LongType.INSTANCE;
 	/**
 	 * Hibernate <tt>float</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#FLOAT} instead.
 	 */
 	public static final FloatType FLOAT = FloatType.INSTANCE;
 	/**
 	 * Hibernate <tt>double</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#DOUBLE} instead.
 	 */
 	public static final DoubleType DOUBLE = DoubleType.INSTANCE;
 	/**
 	 * Hibernate <tt>big_integer</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BIG_INTEGER} instead.
 	 */
 	public static final BigIntegerType BIG_INTEGER = BigIntegerType.INSTANCE;
 	/**
 	 * Hibernate <tt>big_decimal</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BIG_DECIMAL} instead.
 	 */
 	public static final BigDecimalType BIG_DECIMAL = BigDecimalType.INSTANCE;
 	/**
 	 * Hibernate <tt>character</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHARACTER} instead.
 	 */
 	public static final CharacterType CHARACTER = CharacterType.INSTANCE;
 	/**
 	 * Hibernate <tt>string</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#STRING} instead.
 	 */
 	public static final StringType STRING = StringType.INSTANCE;
 	/**
 	 * Hibernate <tt>time</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIME} instead.
 	 */
 	public static final TimeType TIME = TimeType.INSTANCE;
 	/**
 	 * Hibernate <tt>date</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#DATE} instead.
 	 */
 	public static final DateType DATE = DateType.INSTANCE;
 	/**
 	 * Hibernate <tt>timestamp</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIMESTAMP} instead.
 	 */
 	public static final TimestampType TIMESTAMP = TimestampType.INSTANCE;
 	/**
 	 * Hibernate <tt>binary</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BINARY} instead.
 	 */
 	public static final BinaryType BINARY = BinaryType.INSTANCE;
 	/**
 	 * Hibernate <tt>wrapper-binary</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#WRAPPER_BINARY} instead.
 	 */
 	public static final WrapperBinaryType WRAPPER_BINARY = WrapperBinaryType.INSTANCE;
 	/**
 	 * Hibernate char[] type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHAR_ARRAY} instead.
 	 */
 	public static final CharArrayType CHAR_ARRAY = CharArrayType.INSTANCE;
 	/**
 	 * Hibernate Character[] type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHARACTER_ARRAY} instead.
 	 */
 	public static final CharacterArrayType CHARACTER_ARRAY = CharacterArrayType.INSTANCE;
 	/**
 	 * Hibernate <tt>image</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#IMAGE} instead.
 	 */
 	public static final ImageType IMAGE = ImageType.INSTANCE;
 	/**
 	 * Hibernate <tt>text</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TEXT} instead.
 	 */
 	public static final TextType TEXT = TextType.INSTANCE;
 	/**
 	 * Hibernate <tt>materialized_blob</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#MATERIALIZED_BLOB} instead.
 	 */
 	public static final MaterializedBlobType MATERIALIZED_BLOB = MaterializedBlobType.INSTANCE;
 	/**
 	 * Hibernate <tt>materialized_clob</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#MATERIALIZED_CLOB} instead.
 	 */
 	public static final MaterializedClobType MATERIALIZED_CLOB = MaterializedClobType.INSTANCE;
 	/**
 	 * Hibernate <tt>blob</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BLOB} instead.
 	 */
 	public static final BlobType BLOB = BlobType.INSTANCE;
 	/**
 	 * Hibernate <tt>clob</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CLOB} instead.
 	 */
 	public static final ClobType CLOB = ClobType.INSTANCE;
 	/**
 	 * Hibernate <tt>calendar</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CALENDAR} instead.
 	 */
 	public static final CalendarType CALENDAR = CalendarType.INSTANCE;
 	/**
 	 * Hibernate <tt>calendar_date</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CALENDAR_DATE} instead.
 	 */
 	public static final CalendarDateType CALENDAR_DATE = CalendarDateType.INSTANCE;
 	/**
 	 * Hibernate <tt>locale</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#LOCALE} instead.
 	 */
 	public static final LocaleType LOCALE = LocaleType.INSTANCE;
 	/**
 	 * Hibernate <tt>currency</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CURRENCY} instead.
 	 */
 	public static final CurrencyType CURRENCY = CurrencyType.INSTANCE;
 	/**
 	 * Hibernate <tt>timezone</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIMEZONE} instead.
 	 */
 	public static final TimeZoneType TIMEZONE = TimeZoneType.INSTANCE;
 	/**
 	 * Hibernate <tt>class</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CLASS} instead.
 	 */
 	public static final ClassType CLASS = ClassType.INSTANCE;
 	/**
 	 * Hibernate <tt>serializable</tt> type.
 	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#SERIALIZABLE} instead.
 	 */
 	public static final SerializableType SERIALIZABLE = SerializableType.INSTANCE;
 
 
 	/**
 	 * Hibernate <tt>object</tt> type.
 	 * @deprecated Use {@link ObjectType#INSTANCE} instead.
 	 */
 	public static final ObjectType OBJECT = ObjectType.INSTANCE;
 
 	/**
 	 * A Hibernate <tt>serializable</tt> type.
 	 *
 	 * @param serializableClass The {@link java.io.Serializable} implementor class.
 	 *
 	 * @return
 	 *
 	 * @deprecated Use {@link SerializableType#SerializableType} instead.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public static Type serializable(Class serializableClass) {
 		return new SerializableType( serializableClass );
 	}
 
 	/**
 	 * DO NOT USE!
 	 *
 	 * @deprecated Use {@link TypeHelper#any} instead.
 	 */
 	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
 	public static Type any(Type metaType, Type identifierType) {
 		return new AnyType( metaType, identifierType );
 	}
 
 	/**
 	 * DO NOT USE!
 	 *
 	 * @deprecated Use {@link TypeHelper#entity} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
 	 */
 	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration", "deprecation" })
 	public static Type entity(Class persistentClass) {
 		return entity( persistentClass.getName() );
 	}
 
 	private static class NoScope implements TypeFactory.TypeScope {
 		public static final NoScope INSTANCE = new NoScope();
 
 		public SessionFactoryImplementor resolveFactory() {
 			throw new HibernateException( "Cannot access SessionFactory from here" );
 		}
 	}
 
 	/**
 	 * DO NOT USE!
 	 *
 	 * @deprecated Use {@link TypeHelper#entity} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
 	 */
 	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
 	public static Type entity(String entityName) {
 		return new ManyToOneType( NoScope.INSTANCE, entityName );
 	}
 
 	/**
 	 * DO NOT USE!
 	 *
 	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
 	 */
 	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
 	public static Type custom(Class userTypeClass) {
 		return custom( userTypeClass, null );
 	}
 
 	/**
 	 * DO NOT USE!
 	 *
 	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
 	 */
 	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
 	public static Type custom(Class userTypeClass, String[] parameterNames, String[] parameterValues) {
 		return custom( userTypeClass, toProperties( parameterNames, parameterValues ) );	}
 
 	private static Properties toProperties(String[] parameterNames, String[] parameterValues) {
 		if ( parameterNames == null || parameterNames.length == 0 ) {
 			return null;
 		}
 		Properties parameters = new Properties();
 		for ( int i = 0; i < parameterNames.length; i ++ ) {
 			parameters.put( parameterNames[i], parameterValues[i] );
 		}
 		return parameters;
 	}
 
 	/**
 	 * DO NOT USE!
 	 *
 	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
 	 */
 	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration", "unchecked" })
 	public static Type custom(Class userTypeClass, Properties parameters) {
 		if ( CompositeUserType.class.isAssignableFrom( userTypeClass ) ) {
 			return TypeFactory.customComponent( userTypeClass, parameters, NoScope.INSTANCE );
 		}
 		else {
 			return TypeFactory.custom( userTypeClass, parameters, NoScope.INSTANCE );
 		}
 	}
 
 	/**
 	 * Force initialization of a proxy or persistent collection.
 	 * <p/>
 	 * Note: This only ensures intialization of a proxy object or collection;
 	 * it is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
 	 *
 	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
 	 * @throws HibernateException if we can't initialize the proxy at this time, eg. the <tt>Session</tt> was closed
 	 */
 	public static void initialize(Object proxy) throws HibernateException {
 		if ( proxy == null ) {
 			return;
 		}
 		else if ( proxy instanceof HibernateProxy ) {
 			( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().initialize();
 		}
 		else if ( proxy instanceof PersistentCollection ) {
 			( ( PersistentCollection ) proxy ).forceInitialization();
 		}
 	}
 
 	/**
 	 * Check if the proxy or persistent collection is initialized.
 	 *
 	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
 	 * @return true if the argument is already initialized, or is not a proxy or collection
 	 */
 	public static boolean isInitialized(Object proxy) {
 		if ( proxy instanceof HibernateProxy ) {
 			return !( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isUninitialized();
 		}
 		else if ( proxy instanceof PersistentCollection ) {
 			return ( ( PersistentCollection ) proxy ).wasInitialized();
 		}
 		else {
 			return true;
 		}
 	}
 
 	/**
 	 * Get the true, underlying class of a proxied persistent class. This operation
 	 * will initialize a proxy by side-effect.
 	 *
 	 * @param proxy a persistable object or proxy
 	 * @return the true class of the instance
 	 * @throws HibernateException
 	 */
 	public static Class getClass(Object proxy) {
 		if ( proxy instanceof HibernateProxy ) {
 			return ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer()
 					.getImplementation()
 					.getClass();
 		}
 		else {
 			return proxy.getClass();
 		}
 	}
 
 	public static LobCreator getLobCreator(Session session) {
 		return getLobCreator( ( SessionImplementor ) session );
 	}
 
 	public static LobCreator getLobCreator(SessionImplementor session) {
 		return session.getFactory()
-				.getSettings()
-				.getJdbcSupport()
+				.getJdbcServices()
 				.getLobCreator( ( LobCreationContext ) session );
 	}
 
 	/**
 	 * Close an <tt>Iterator</tt> created by <tt>iterate()</tt> immediately,
 	 * instead of waiting until the session is closed or disconnected.
 	 *
 	 * @param iterator an <tt>Iterator</tt> created by <tt>iterate()</tt>
 	 * @throws HibernateException
 	 * @see org.hibernate.Query#iterate
 	 * @see Query#iterate()
 	 */
 	public static void close(Iterator iterator) throws HibernateException {
 		if ( iterator instanceof HibernateIterator ) {
 			( ( HibernateIterator ) iterator ).close();
 		}
 		else {
 			throw new IllegalArgumentException( "not a Hibernate iterator" );
 		}
 	}
 
 	/**
 	 * Check if the property is initialized. If the named property does not exist
 	 * or is not persistent, this method always returns <tt>true</tt>.
 	 *
 	 * @param proxy The potential proxy
 	 * @param propertyName the name of a persistent attribute of the object
 	 * @return true if the named property of the object is not listed as uninitialized; false otherwise
 	 */
 	public static boolean isPropertyInitialized(Object proxy, String propertyName) {
 		
 		Object entity;
 		if ( proxy instanceof HibernateProxy ) {
 			LazyInitializer li = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				return false;
 			}
 			else {
 				entity = li.getImplementation();
 			}
 		}
 		else {
 			entity = proxy;
 		}
 
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			return interceptor == null || interceptor.isInitialized( propertyName );
 		}
 		else {
 			return true;
 		}
 		
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 46c3be4f08..5b9c416c25 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,452 +1,441 @@
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
 
 import java.util.Map;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
-import org.hibernate.engine.jdbc.JdbcSupport;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Settings that affect the behaviour of Hibernate at runtime.
  *
  * @author Gavin King
  */
 public final class Settings {
 
 	private Integer maximumFetchDepth;
 	private Map querySubstitutions;
 	private int jdbcBatchSize;
 	private int defaultBatchFetchSize;
 	private boolean scrollableResultSetsEnabled;
 	private boolean getGeneratedKeysEnabled;
 	private String defaultSchemaName;
 	private String defaultCatalogName;
 	private Integer jdbcFetchSize;
 	private String sessionFactoryName;
 	private boolean autoCreateSchema;
 	private boolean autoDropSchema;
 	private boolean autoUpdateSchema;
 	private boolean autoValidateSchema;
 	private boolean queryCacheEnabled;
 	private boolean structuredCacheEntriesEnabled;
 	private boolean secondLevelCacheEnabled;
 	private String cacheRegionPrefix;
 	private boolean minimalPutsEnabled;
 	private boolean commentsEnabled;
 	private boolean statisticsEnabled;
 	private boolean jdbcBatchVersionedData;
 	private boolean identifierRollbackEnabled;
 	private boolean flushBeforeCompletionEnabled;
 	private boolean autoCloseSessionEnabled;
 	private ConnectionReleaseMode connectionReleaseMode;
 	private RegionFactory regionFactory;
 	private QueryCacheFactory queryCacheFactory;
 	private QueryTranslatorFactory queryTranslatorFactory;
 	private boolean wrapResultSetsEnabled;
 	private boolean orderUpdatesEnabled;
 	private boolean orderInsertsEnabled;
 	private EntityMode defaultEntityMode;
 	private boolean dataDefinitionImplicitCommit;
 	private boolean dataDefinitionInTransactionSupported;
 	private boolean strictJPAQLCompliance;
 	private boolean namedQueryStartupCheckingEnabled;
 	private EntityTuplizerFactory entityTuplizerFactory;
 	private boolean checkNullability;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 //	private BytecodeProvider bytecodeProvider;
-	private JdbcSupport jdbcSupport;
 	private String importFiles;
 
 	private JtaPlatform jtaPlatform;
 
 	/**
 	 * Package protected constructor
 	 */
 	Settings() {
 	}
 
 	// public getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getImportFiles() {
 		return importFiles;
 	}
 
 	public void setImportFiles(String importFiles) {
 		this.importFiles = importFiles;
 	}
 
 	public String getDefaultSchemaName() {
 		return defaultSchemaName;
 	}
 
 	public String getDefaultCatalogName() {
 		return defaultCatalogName;
 	}
 
 	public int getJdbcBatchSize() {
 		return jdbcBatchSize;
 	}
 
 	public int getDefaultBatchFetchSize() {
 		return defaultBatchFetchSize;
 	}
 
 	public Map getQuerySubstitutions() {
 		return querySubstitutions;
 	}
 
 	public boolean isIdentifierRollbackEnabled() {
 		return identifierRollbackEnabled;
 	}
 
 	public boolean isScrollableResultSetsEnabled() {
 		return scrollableResultSetsEnabled;
 	}
 
 	public boolean isGetGeneratedKeysEnabled() {
 		return getGeneratedKeysEnabled;
 	}
 
 	public boolean isMinimalPutsEnabled() {
 		return minimalPutsEnabled;
 	}
 
 	public Integer getJdbcFetchSize() {
 		return jdbcFetchSize;
 	}
 
 	public String getSessionFactoryName() {
 		return sessionFactoryName;
 	}
 
 	public boolean isAutoCreateSchema() {
 		return autoCreateSchema;
 	}
 
 	public boolean isAutoDropSchema() {
 		return autoDropSchema;
 	}
 
 	public boolean isAutoUpdateSchema() {
 		return autoUpdateSchema;
 	}
 
 	public Integer getMaximumFetchDepth() {
 		return maximumFetchDepth;
 	}
 
 	public RegionFactory getRegionFactory() {
 		return regionFactory;
 	}
 
 	public boolean isQueryCacheEnabled() {
 		return queryCacheEnabled;
 	}
 
 	public boolean isCommentsEnabled() {
 		return commentsEnabled;
 	}
 
 	public boolean isSecondLevelCacheEnabled() {
 		return secondLevelCacheEnabled;
 	}
 
 	public String getCacheRegionPrefix() {
 		return cacheRegionPrefix;
 	}
 
 	public QueryCacheFactory getQueryCacheFactory() {
 		return queryCacheFactory;
 	}
 
 	public boolean isStatisticsEnabled() {
 		return statisticsEnabled;
 	}
 
 	public boolean isJdbcBatchVersionedData() {
 		return jdbcBatchVersionedData;
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	public QueryTranslatorFactory getQueryTranslatorFactory() {
 		return queryTranslatorFactory;
 	}
 
 	public boolean isWrapResultSetsEnabled() {
 		return wrapResultSetsEnabled;
 	}
 
 	public boolean isOrderUpdatesEnabled() {
 		return orderUpdatesEnabled;
 	}
 
 	public boolean isOrderInsertsEnabled() {
 		return orderInsertsEnabled;
 	}
 
 	public boolean isStructuredCacheEntriesEnabled() {
 		return structuredCacheEntriesEnabled;
 	}
 
 	public EntityMode getDefaultEntityMode() {
 		return defaultEntityMode;
 	}
 
 	public boolean isAutoValidateSchema() {
 		return autoValidateSchema;
 	}
 
 	public boolean isDataDefinitionImplicitCommit() {
 		return dataDefinitionImplicitCommit;
 	}
 
 	public boolean isDataDefinitionInTransactionSupported() {
 		return dataDefinitionInTransactionSupported;
 	}
 
 	public boolean isStrictJPAQLCompliance() {
 		return strictJPAQLCompliance;
 	}
 
 	public boolean isNamedQueryStartupCheckingEnabled() {
 		return namedQueryStartupCheckingEnabled;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 //	public ComponentTuplizerFactory getComponentTuplizerFactory() {
 //		return componentTuplizerFactory;
 //	}
 
-	public JdbcSupport getJdbcSupport() {
-		return jdbcSupport;
-	}
-
-
 	// package protected setters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	void setDefaultSchemaName(String string) {
 		defaultSchemaName = string;
 	}
 
 	void setDefaultCatalogName(String string) {
 		defaultCatalogName = string;
 	}
 
 	void setJdbcBatchSize(int i) {
 		jdbcBatchSize = i;
 	}
 
 	void setDefaultBatchFetchSize(int i) {
 		defaultBatchFetchSize = i;
 	}
 
 	void setQuerySubstitutions(Map map) {
 		querySubstitutions = map;
 	}
 
 	void setIdentifierRollbackEnabled(boolean b) {
 		identifierRollbackEnabled = b;
 	}
 
 	void setMinimalPutsEnabled(boolean b) {
 		minimalPutsEnabled = b;
 	}
 
 	void setScrollableResultSetsEnabled(boolean b) {
 		scrollableResultSetsEnabled = b;
 	}
 
 	void setGetGeneratedKeysEnabled(boolean b) {
 		getGeneratedKeysEnabled = b;
 	}
 
 	void setJdbcFetchSize(Integer integer) {
 		jdbcFetchSize = integer;
 	}
 
 	void setSessionFactoryName(String string) {
 		sessionFactoryName = string;
 	}
 
 	void setAutoCreateSchema(boolean b) {
 		autoCreateSchema = b;
 	}
 
 	void setAutoDropSchema(boolean b) {
 		autoDropSchema = b;
 	}
 
 	void setAutoUpdateSchema(boolean b) {
 		autoUpdateSchema = b;
 	}
 
 	void setMaximumFetchDepth(Integer i) {
 		maximumFetchDepth = i;
 	}
 
 	void setRegionFactory(RegionFactory regionFactory) {
 		this.regionFactory = regionFactory;
 	}
 
 	void setQueryCacheEnabled(boolean b) {
 		queryCacheEnabled = b;
 	}
 
 	void setCommentsEnabled(boolean commentsEnabled) {
 		this.commentsEnabled = commentsEnabled;
 	}
 
 	void setSecondLevelCacheEnabled(boolean secondLevelCacheEnabled) {
 		this.secondLevelCacheEnabled = secondLevelCacheEnabled;
 	}
 
 	void setCacheRegionPrefix(String cacheRegionPrefix) {
 		this.cacheRegionPrefix = cacheRegionPrefix;
 	}
 
 	void setQueryCacheFactory(QueryCacheFactory queryCacheFactory) {
 		this.queryCacheFactory = queryCacheFactory;
 	}
 
 	void setStatisticsEnabled(boolean statisticsEnabled) {
 		this.statisticsEnabled = statisticsEnabled;
 	}
 
 	void setJdbcBatchVersionedData(boolean jdbcBatchVersionedData) {
 		this.jdbcBatchVersionedData = jdbcBatchVersionedData;
 	}
 
 	void setFlushBeforeCompletionEnabled(boolean flushBeforeCompletionEnabled) {
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 	}
 
 	void setAutoCloseSessionEnabled(boolean autoCloseSessionEnabled) {
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 	}
 
 	void setConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 		this.connectionReleaseMode = connectionReleaseMode;
 	}
 
 	void setQueryTranslatorFactory(QueryTranslatorFactory queryTranslatorFactory) {
 		this.queryTranslatorFactory = queryTranslatorFactory;
 	}
 
 	void setWrapResultSetsEnabled(boolean wrapResultSetsEnabled) {
 		this.wrapResultSetsEnabled = wrapResultSetsEnabled;
 	}
 
 	void setOrderUpdatesEnabled(boolean orderUpdatesEnabled) {
 		this.orderUpdatesEnabled = orderUpdatesEnabled;
 	}
 
 	void setOrderInsertsEnabled(boolean orderInsertsEnabled) {
 		this.orderInsertsEnabled = orderInsertsEnabled;
 	}
 
 	void setStructuredCacheEntriesEnabled(boolean structuredCacheEntriesEnabled) {
 		this.structuredCacheEntriesEnabled = structuredCacheEntriesEnabled;
 	}
 
 	void setDefaultEntityMode(EntityMode defaultEntityMode) {
 		this.defaultEntityMode = defaultEntityMode;
 	}
 
 	void setAutoValidateSchema(boolean autoValidateSchema) {
 		this.autoValidateSchema = autoValidateSchema;
 	}
 
 	void setDataDefinitionImplicitCommit(boolean dataDefinitionImplicitCommit) {
 		this.dataDefinitionImplicitCommit = dataDefinitionImplicitCommit;
 	}
 
 	void setDataDefinitionInTransactionSupported(boolean dataDefinitionInTransactionSupported) {
 		this.dataDefinitionInTransactionSupported = dataDefinitionInTransactionSupported;
 	}
 
 	void setStrictJPAQLCompliance(boolean strictJPAQLCompliance) {
 		this.strictJPAQLCompliance = strictJPAQLCompliance;
 	}
 
 	void setNamedQueryStartupCheckingEnabled(boolean namedQueryStartupCheckingEnabled) {
 		this.namedQueryStartupCheckingEnabled = namedQueryStartupCheckingEnabled;
 	}
 
 	void setEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
 		this.entityTuplizerFactory = entityTuplizerFactory;
 	}
 
 	public boolean isCheckNullability() {
 		return checkNullability;
 	}
 
 	public void setCheckNullability(boolean checkNullability) {
 		this.checkNullability = checkNullability;
 	}
 
 	//	void setComponentTuplizerFactory(ComponentTuplizerFactory componentTuplizerFactory) {
 //		this.componentTuplizerFactory = componentTuplizerFactory;
 //	}
 
-	void setJdbcSupport(JdbcSupport jdbcSupport) {
-		this.jdbcSupport = jdbcSupport;
-	}
-
 	//	public BytecodeProvider getBytecodeProvider() {
 //		return bytecodeProvider;
 //	}
 //
 //	void setBytecodeProvider(BytecodeProvider bytecodeProvider) {
 //		this.bytecodeProvider = bytecodeProvider;
 //	}
 
 
 	public JtaPlatform getJtaPlatform() {
 		return jtaPlatform;
 	}
 
 	void setJtaPlatform(JtaPlatform jtaPlatform) {
 		this.jtaPlatform = jtaPlatform;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index f9e8dc2d1c..b1fc9cbd37 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,332 +1,329 @@
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
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.bytecode.BytecodeProvider;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
-import org.hibernate.engine.jdbc.JdbcSupport;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistry;
 import org.jboss.logging.Logger;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	protected SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
-		settings.setJdbcSupport( new JdbcSupport( ! ConfigurationHelper.getBoolean( Environment.NON_CONTEXTUAL_LOB_CREATION, properties ) ) );
-
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
         LOG.autoFlush(enabledDisabled(flushBeforeCompletion));
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
         LOG.autoSessionClose(enabledDisabled(autoCloseSession));
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) batchSize = 0;
 		if (batchSize>0) LOG.jdbcBatchSize(batchSize);
 		settings.setJdbcBatchSize(batchSize);
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
         if (batchSize > 0) LOG.jdbcBatchUpdates(enabledDisabled(jdbcBatchVersionedData));
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, meta.supportsScrollableResults());
         LOG.scrollabelResultSets(enabledDisabled(useScrollableResultSets));
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
         LOG.wrapResultSets(enabledDisabled(wrapResultSets));
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
         LOG.jdbc3GeneratedKeys(enabledDisabled(useGetGeneratedKeys));
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
         if (statementFetchSize != null) LOG.jdbcResultSetFetchSize(statementFetchSize);
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
         LOG.connectionReleaseMode(releaseModeName);
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
                 LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty(Environment.DEFAULT_SCHEMA);
 		String defaultCatalog = properties.getProperty(Environment.DEFAULT_CATALOG);
         if (defaultSchema != null) LOG.defaultSchema(defaultSchema);
         if (defaultCatalog != null) LOG.defaultCatalog(defaultCatalog);
 		settings.setDefaultSchemaName(defaultSchema);
 		settings.setDefaultCatalogName(defaultCatalog);
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger(Environment.MAX_FETCH_DEPTH, properties);
         if (maxFetchDepth != null) LOG.maxOuterJoinFetchDepth(maxFetchDepth);
 		settings.setMaximumFetchDepth(maxFetchDepth);
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
         LOG.defaultBatchFetchSize(batchFetchSize);
 		settings.setDefaultBatchFetchSize(batchFetchSize);
 
 		boolean comments = ConfigurationHelper.getBoolean(Environment.USE_SQL_COMMENTS, properties);
         LOG.generateSqlWithComments(enabledDisabled(comments));
 		settings.setCommentsEnabled(comments);
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean(Environment.ORDER_UPDATES, properties);
         LOG.orderSqlUpdatesByPrimaryKey(enabledDisabled(orderUpdates));
 		settings.setOrderUpdatesEnabled(orderUpdates);
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
         LOG.orderSqlInsertsForBatching(enabledDisabled(orderInserts));
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );
 
         Map querySubstitutions = ConfigurationHelper.toMap(Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties);
         LOG.queryLanguageSubstitutions(querySubstitutions);
 		settings.setQuerySubstitutions(querySubstitutions);
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
         LOG.jpaQlStrictCompliance(enabledDisabled(jpaqlCompliance));
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean(Environment.USE_SECOND_LEVEL_CACHE, properties, true);
         LOG.secondLevelCache(enabledDisabled(useSecondLevelCache));
 		settings.setSecondLevelCacheEnabled(useSecondLevelCache);
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
         LOG.queryCache(enabledDisabled(useQueryCache));
 		settings.setQueryCacheEnabled(useQueryCache);
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
         LOG.optimizeCacheForMinimalInputs(enabledDisabled(useMinimalPuts));
 		settings.setMinimalPutsEnabled(useMinimalPuts);
 
 		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
 		if ( StringHelper.isEmpty(prefix) ) prefix=null;
         if (prefix != null) LOG.cacheRegionPrefix(prefix);
 		settings.setCacheRegionPrefix(prefix);
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean(Environment.USE_STRUCTURED_CACHE, properties, false);
         LOG.structuredSecondLevelCacheEntries(enabledDisabled(useStructuredCacheEntries));
 		settings.setStructuredCacheEntriesEnabled(useStructuredCacheEntries);
 
 		if (useQueryCache) settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean(Environment.GENERATE_STATISTICS, properties);
 		LOG.statistics( enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled(useStatistics);
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean(Environment.USE_IDENTIFIER_ROLLBACK, properties);
         LOG.deletedEntitySyntheticIdentifierRollback(enabledDisabled(useIdentifierRollback));
 		settings.setIdentifierRollbackEnabled(useIdentifierRollback);
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty(Environment.HBM2DDL_AUTO);
 		if ( "validate".equals(autoSchemaExport) ) settings.setAutoValidateSchema(true);
 		if ( "update".equals(autoSchemaExport) ) settings.setAutoUpdateSchema(true);
 		if ( "create".equals(autoSchemaExport) ) settings.setAutoCreateSchema(true);
 		if ( "create-drop".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema(true);
 			settings.setAutoDropSchema(true);
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
         LOG.defaultEntityMode(defaultEntityMode);
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
         LOG.namedQueryChecking(enabledDisabled(namedQueryChecking));
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
         LOG.checkNullability(enabledDisabled(checkNullability));
 		settings.setCheckNullability(checkNullability);
 
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
 		else if ( "cglib".equals( providerName ) ) {
 			return new org.hibernate.bytecode.cglib.BytecodeProviderImpl();
 		}
 		else {
             LOG.debugf("Using javassist as bytecode provider by default");
 			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
 		}
 	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.StandardQueryCacheFactory"
 		);
         LOG.queryCacheFactory(queryCacheFactoryClassName);
 		try {
 			return (QueryCacheFactory) ReflectHelper.classForName(queryCacheFactoryClassName).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, cnfe);
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null && cachingEnabled ) {
 			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
 			if ( providerClassName != null ) {
 				// legacy behavior, apply the bridge...
 				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
 			}
 		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.cacheRegionFactory(regionFactoryClassName);
 		try {
 			try {
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException nsme ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.constructorWithPropertiesNotFound(regionFactoryClassName);
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName ).newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.ast.ASTQueryTranslatorFactory"
 		);
         LOG.queryTranslator(className);
 		try {
 			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryTranslatorFactory: " + className, cnfe);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/JdbcSupport.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/JdbcSupport.java
deleted file mode 100644
index 596b66c397..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/JdbcSupport.java
+++ /dev/null
@@ -1,78 +0,0 @@
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
-package org.hibernate.engine.jdbc;
-import java.sql.ResultSet;
-
-/**
- * Central place for locating JDBC support elements.
- *
- * @author Steve Ebersole
- */
-public class JdbcSupport {
-	public final boolean userContextualLobCreator;
-
-	/**
-	 * Create a support object
-	 *
-	 * @param userContextualLobCreator Should we use contextual (using the JDBC {@link java.sql.Connection}) to
-	 * create LOB instances.  In almost all instances this should be the case.  However if the underlying driver
-	 * does not support the {@link java.sql.Connection#createBlob()}, {@link java.sql.Connection#createClob()} or
-	 * {@link java.sql.Connection#createNClob()} methods this will need to be set to false.
-	 */
-	public JdbcSupport(boolean userContextualLobCreator) {
-		this.userContextualLobCreator = userContextualLobCreator;
-	}
-
-	/**
-	 * Get an explcitly non-contextual LOB creator.
-	 *
-	 * @return The LOB creator
-	 */
-	public LobCreator getLobCreator() {
-		return NonContextualLobCreator.INSTANCE;
-	}
-
-	/**
-	 * Get a LOB creator, based on the given context
-	 *
-	 * @return The LOB creator
-	 */
-	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
-		return userContextualLobCreator
-				? new ContextualLobCreator( lobCreationContext )
-				: NonContextualLobCreator.INSTANCE;
-	}
-
-	/**
-	 * Wrap a result set in a "colun name cache" wrapper.
-	 *
-	 * @param resultSet The result set to wrap
-	 * @param columnNameCache The column name cache.
-	 *
-	 * @return The wrapped result set.
-	 */
-	public ResultSet wrap(ResultSet resultSet, ColumnNameCache columnNameCache) {
-		return ResultSetWrapperProxy.generateProxy( resultSet, columnNameCache );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
index 1f7a29818a..6d6de07f56 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
@@ -1,331 +1,357 @@
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
 package org.hibernate.engine.jdbc.internal;
 import java.lang.reflect.InvocationTargetException;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.LobCreationContext;
+import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.InjectService;
 import org.jboss.logging.Logger;
 
 /**
  * Standard implementation of the {@link JdbcServices} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesImpl implements JdbcServices, Configurable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JdbcServicesImpl.class.getName());
 
 	private ConnectionProvider connectionProvider;
 
 	@InjectService
 	public void setConnectionProvider(ConnectionProvider connectionProvider) {
 		this.connectionProvider = connectionProvider;
 	}
 
 	private DialectFactory dialectFactory;
 
 	@InjectService
 	public void setDialectFactory(DialectFactory dialectFactory) {
 		this.dialectFactory = dialectFactory;
 	}
 
 	private Dialect dialect;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper sqlExceptionHelper;
 	private ExtractedDatabaseMetaData extractedMetaDataSupport;
+	private LobCreatorBuilder lobCreatorBuilder;
 
 	public void configure(Map configValues) {
 		Dialect dialect = null;
+		LobCreatorBuilder lobCreatorBuilder = null;
 
 		boolean metaSupportsScrollable = false;
 		boolean metaSupportsGetGeneratedKeys = false;
 		boolean metaSupportsBatchUpdates = false;
 		boolean metaReportsDDLCausesTxnCommit = false;
 		boolean metaReportsDDLInTxnSupported = true;
 		String extraKeywordsString = "";
 		int sqlStateType = -1;
 		boolean lobLocatorUpdateCopy = false;
 		String catalogName = null;
 		String schemaName = null;
 		LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
 
 		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
 		// The need for it is intended to be alleviated with future development, thus it is
 		// not defined as an Environment constant...
 		//
 		// it is used to control whether we should consult the JDBC metadata to determine
 		// certain Settings default values; it is useful to *not* do this when the database
 		// may not be available (mainly in tools usage).
 		boolean useJdbcMetadata = ConfigurationHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", configValues, true );
 		if ( useJdbcMetadata ) {
 			try {
 				Connection conn = connectionProvider.getConnection();
 				try {
 					DatabaseMetaData meta = conn.getMetaData();
                     LOG.database(meta.getDatabaseProductName(),
                                  meta.getDatabaseProductVersion(),
                                  meta.getDatabaseMajorVersion(),
                                  meta.getDatabaseMinorVersion());
                     LOG.driver(meta.getDriverName(),
                                meta.getDriverVersion(),
                                meta.getDriverMajorVersion(),
                                meta.getDriverMinorVersion());
                     LOG.jdbcVersion(meta.getJDBCMajorVersion(), meta.getJDBCMinorVersion());
 
 					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
 					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
 					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
 					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
 					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
 					extraKeywordsString = meta.getSQLKeywords();
 					sqlStateType = meta.getSQLStateType();
 					lobLocatorUpdateCopy = meta.locatorsUpdateCopy();
 					typeInfoSet.addAll( TypeInfoExtracter.extractTypeInfo( meta ) );
 
 					dialect = dialectFactory.buildDialect( configValues, conn );
 
 					catalogName = conn.getCatalog();
 					SchemaNameResolver schemaNameResolver = determineExplicitSchemaNameResolver( configValues );
 					if ( schemaNameResolver == null ) {
 // todo : add dialect method
 //						schemaNameResolver = dialect.getSchemaNameResolver();
 					}
 					if ( schemaNameResolver != null ) {
 						schemaName = schemaNameResolver.resolveSchemaName( conn );
 					}
+					lobCreatorBuilder = new LobCreatorBuilder( configValues, conn );
 				}
 				catch ( SQLException sqle ) {
                     LOG.unableToObtainConnectionMetadata(sqle.getMessage());
 				}
 				finally {
 					connectionProvider.closeConnection( conn );
 				}
 			}
 			catch ( SQLException sqle ) {
                 LOG.unableToObtainConnectionToQueryMetadata(sqle.getMessage());
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 			catch ( UnsupportedOperationException uoe ) {
 				// user supplied JDBC connections
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 		}
 		else {
 			dialect = dialectFactory.buildDialect( configValues, null );
 		}
 
 		final boolean showSQL = ConfigurationHelper.getBoolean( Environment.SHOW_SQL, configValues, false );
 		final boolean formatSQL = ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, configValues, false );
 
 		this.dialect = dialect;
+		this.lobCreatorBuilder = (
+				lobCreatorBuilder == null ?
+						new LobCreatorBuilder( configValues, null ) :
+						lobCreatorBuilder
+		);
+
 		this.sqlStatementLogger =  new SqlStatementLogger( showSQL, formatSQL );
 		this.sqlExceptionHelper = new SqlExceptionHelper( dialect.buildSQLExceptionConverter() );
 		this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl(
 				metaSupportsScrollable,
 				metaSupportsGetGeneratedKeys,
 				metaSupportsBatchUpdates,
 				metaReportsDDLInTxnSupported,
 				metaReportsDDLCausesTxnCommit,
 				parseKeywords( extraKeywordsString ),
 				parseSQLStateType( sqlStateType ),
 				lobLocatorUpdateCopy,
 				schemaName,
 				catalogName,
 				typeInfoSet
 		);
 	}
 
 	// todo : add to Environment
 	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
 
 	private SchemaNameResolver determineExplicitSchemaNameResolver(Map configValues) {
 		Object setting = configValues.get( SCHEMA_NAME_RESOLVER );
 		if ( SchemaNameResolver.class.isInstance( setting ) ) {
 			return (SchemaNameResolver) setting;
 		}
 
 		String resolverClassName = (String) setting;
 		if ( resolverClassName != null ) {
 			try {
 				Class resolverClass = ReflectHelper.classForName( resolverClassName, getClass() );
 				return (SchemaNameResolver) ReflectHelper.getDefaultConstructor( resolverClass ).newInstance();
 			}
 			catch ( ClassNotFoundException e ) {
                 LOG.unableToLocateConfiguredSchemaNameResolver(resolverClassName, e.toString());
 			}
 			catch ( InvocationTargetException e ) {
                 LOG.unableToInstantiateConfiguredSchemaNameResolver(resolverClassName, e.getTargetException().toString());
 			}
 			catch ( Exception e ) {
                 LOG.unableToInstantiateConfiguredSchemaNameResolver(resolverClassName, e.toString());
 			}
 		}
 		return null;
 	}
 
 	private Set<String> parseKeywords(String extraKeywordsString) {
 		Set<String> keywordSet = new HashSet<String>();
 		for ( String keyword : extraKeywordsString.split( "," ) ) {
 			keywordSet.add( keyword );
 		}
 		return keywordSet;
 	}
 
 	private ExtractedDatabaseMetaData.SQLStateType parseSQLStateType(int sqlStateType) {
 		switch ( sqlStateType ) {
 			case DatabaseMetaData.sqlStateSQL99 : {
 				return ExtractedDatabaseMetaData.SQLStateType.SQL99;
 			}
 			case DatabaseMetaData.sqlStateXOpen : {
 				return ExtractedDatabaseMetaData.SQLStateType.XOpen;
 			}
 			default : {
 				return ExtractedDatabaseMetaData.SQLStateType.UNKOWN;
 			}
 		}
 	}
 
 	private static class ExtractedDatabaseMetaDataImpl implements ExtractedDatabaseMetaData {
 		private final boolean supportsScrollableResults;
 		private final boolean supportsGetGeneratedKeys;
 		private final boolean supportsBatchUpdates;
 		private final boolean supportsDataDefinitionInTransaction;
 		private final boolean doesDataDefinitionCauseTransactionCommit;
 		private final Set<String> extraKeywords;
 		private final SQLStateType sqlStateType;
 		private final boolean lobLocatorUpdateCopy;
 		private final String connectionSchemaName;
 		private final String connectionCatalogName;
 		private final LinkedHashSet<TypeInfo> typeInfoSet;
 
 		private ExtractedDatabaseMetaDataImpl(
 				boolean supportsScrollableResults,
 				boolean supportsGetGeneratedKeys,
 				boolean supportsBatchUpdates,
 				boolean supportsDataDefinitionInTransaction,
 				boolean doesDataDefinitionCauseTransactionCommit,
 				Set<String> extraKeywords,
 				SQLStateType sqlStateType,
 				boolean lobLocatorUpdateCopy,
 				String connectionSchemaName,
 				String connectionCatalogName,
 				LinkedHashSet<TypeInfo> typeInfoSet) {
 			this.supportsScrollableResults = supportsScrollableResults;
 			this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
 			this.supportsBatchUpdates = supportsBatchUpdates;
 			this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
 			this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
 			this.extraKeywords = extraKeywords;
 			this.sqlStateType = sqlStateType;
 			this.lobLocatorUpdateCopy = lobLocatorUpdateCopy;
 			this.connectionSchemaName = connectionSchemaName;
 			this.connectionCatalogName = connectionCatalogName;
 			this.typeInfoSet = typeInfoSet;
 		}
 
 		public boolean supportsScrollableResults() {
 			return supportsScrollableResults;
 		}
 
 		public boolean supportsGetGeneratedKeys() {
 			return supportsGetGeneratedKeys;
 		}
 
 		public boolean supportsBatchUpdates() {
 			return supportsBatchUpdates;
 		}
 
 		public boolean supportsDataDefinitionInTransaction() {
 			return supportsDataDefinitionInTransaction;
 		}
 
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return doesDataDefinitionCauseTransactionCommit;
 		}
 
 		public Set<String> getExtraKeywords() {
 			return extraKeywords;
 		}
 
 		public SQLStateType getSqlStateType() {
 			return sqlStateType;
 		}
 
 		public boolean doesLobLocatorUpdateCopy() {
 			return lobLocatorUpdateCopy;
 		}
 
 		public String getConnectionSchemaName() {
 			return connectionSchemaName;
 		}
 
 		public String getConnectionCatalogName() {
 			return connectionCatalogName;
 		}
 
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return typeInfoSet;
 		}
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return extractedMetaDataSupport;
 	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
+		return lobCreatorBuilder.buildLobCreator( lobCreationContext );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public ResultSetWrapper getResultSetWrapper() {
+		return ResultSetWrapperImpl.INSTANCE;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ResultSetWrapperImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ResultSetWrapperImpl.java
new file mode 100644
index 0000000000..b76819aced
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/ResultSetWrapperImpl.java
@@ -0,0 +1,49 @@
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
+package org.hibernate.engine.jdbc.internal;
+import java.sql.ResultSet;
+
+import org.hibernate.engine.jdbc.ColumnNameCache;
+import org.hibernate.engine.jdbc.ResultSetWrapperProxy;
+import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
+
+/**
+ * Standard Hibernate implementation for wrapping a {@link ResultSet} in a
+ " column name cache" wrapper.
+ *
+ * @author Gail Badner
+ */
+public class ResultSetWrapperImpl implements ResultSetWrapper {
+	public static ResultSetWrapper INSTANCE = new ResultSetWrapperImpl();
+
+	private ResultSetWrapperImpl() {
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public ResultSet wrap(ResultSet resultSet, ColumnNameCache columnNameCache) {
+		return ResultSetWrapperProxy.generateProxy( resultSet, columnNameCache );
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
index a85ee384c8..8a51b0be09 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
@@ -1,74 +1,95 @@
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
 package org.hibernate.engine.jdbc.spi;
 import java.sql.Connection;
+import java.sql.ResultSet;
+
 import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.ColumnNameCache;
+import org.hibernate.engine.jdbc.LobCreationContext;
+import org.hibernate.engine.jdbc.LobCreator;
+import org.hibernate.engine.jdbc.ResultSetWrapperProxy;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Service;
 
 /**
  * Contract for services around JDBC operations.  These represent shared resources, aka not varied by session/use.
  *
  * @author Steve Ebersole
  */
 public interface JdbcServices extends Service {
 	/**
 	 * Obtain service for providing JDBC connections.
 	 *
 	 * @return The connection provider.
 	 */
 	public ConnectionProvider getConnectionProvider();
 
 	/**
 	 * Obtain the dialect of the database to which {@link Connection connections} from
 	 * {@link #getConnectionProvider()} point.
 	 *
 	 * @return The database dialect.
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Obtain service for logging SQL statements.
 	 *
 	 * @return The SQL statement logger.
 	 */
 	public SqlStatementLogger getSqlStatementLogger();
 
 	/**
 	 * Obtain service for dealing with exceptions.
 	 *
 	 * @return The exception helper service.
 	 */
 	public SqlExceptionHelper getSqlExceptionHelper();
 
 	/**
 	 * Obtain infomration about supported behavior reported by the JDBC driver.
 	 * <p/>
 	 * Yuck, yuck, yuck!  Much prefer this to be part of a "basic settings" type object.  See discussion
 	 * on {@link org.hibernate.cfg.internal.JdbcServicesBuilder}
 	 * 
 	 * @return
 	 */
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport();
+
+	/**
+	 * Create an instance of a {@link LobCreator} appropriate for the current environment, mainly meant to account for
+	 * variance between JDBC 4 (<= JDK 1.6) and JDBC3 (>= JDK 1.5).
+	 *
+	 * @param lobCreationContext The context in which the LOB is being created
+	 * @return The LOB creator.
+	 */
+	public LobCreator getLobCreator(LobCreationContext lobCreationContext);
+
+	/**
+	 * Obtain service for wrapping a {@link ResultSet} in a "column name cache" wrapper.
+	 * @return The ResultSet wrapper.
+	 */
+	public ResultSetWrapper getResultSetWrapper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ResultSetWrapper.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ResultSetWrapper.java
new file mode 100644
index 0000000000..62cefea807
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ResultSetWrapper.java
@@ -0,0 +1,44 @@
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
+package org.hibernate.engine.jdbc.spi;
+
+import java.sql.ResultSet;
+import org.hibernate.engine.jdbc.ColumnNameCache;
+
+/**
+ * Contract for wrapping a {@link ResultSet} in a "column name cache" wrapper.
+ *
+ * @author Gail Badner
+ */
+public interface ResultSetWrapper {
+	/**
+	 * Wrap a result set in a "column name cache" wrapper.
+	 *
+	 * @param resultSet The result set to wrap
+	 * @param columnNameCache The column name cache.
+	 *
+	 * @return The wrapped result set.
+	 */
+	public ResultSet wrap(ResultSet resultSet, ColumnNameCache columnNameCache);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
index efe87d8cf6..a6a59d6841 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
@@ -1232,1105 +1232,1105 @@ public final class SessionImpl
 		return list( query, new QueryParameters() );
 	}
 
 	public List find(String query, Object value, Type type) throws HibernateException {
 		return list( query, new QueryParameters(type, value) );
 	}
 
 	public List find(String query, Object[] values, Type[] types) throws HibernateException {
 		return list( query, new QueryParameters(types, values) );
 	}
 
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		List results = CollectionHelper.EMPTY_LIST;
 		boolean success = false;
 
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
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
 			afterOperation(success);
 		}
 		return result;
 	}
 
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
             afterOperation(success);
         }
         return result;
     }
 
 	public Iterator iterate(String query) throws HibernateException {
 		return iterate( query, new QueryParameters() );
 	}
 
 	public Iterator iterate(String query, Object value, Type type) throws HibernateException {
 		return iterate( query, new QueryParameters(type, value) );
 	}
 
 	public Iterator iterate(String query, Object[] values, Type[] types) throws HibernateException {
 		return iterate( query, new QueryParameters(types, values) );
 	}
 
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
 			dontFlushFromFind--;
 		}
 	}
 
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
 			dontFlushFromFind--;
 		}
 	}
 
 	public int delete(String query) throws HibernateException {
 		return delete( query, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY );
 	}
 
 	public int delete(String query, Object value, Type type) throws HibernateException {
 		return delete( query, new Object[]{value}, new Type[]{type} );
 	}
 
 	public int delete(String query, Object[] values, Type[] types) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( query == null ) {
 			throw new IllegalArgumentException("attempt to doAfterTransactionCompletion delete-by-query with null query");
 		}
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("delete: " + query);
             if (values.length != 0) LOG.trace("Parameters: " + StringHelper.toString(values));
 		}
 
 		List list = find( query, values, types );
 		int deletionCount = list.size();
 		for ( int i = 0; i < deletionCount; i++ ) {
 			delete( list.get( i ) );
 		}
 
 		return deletionCount;
 	}
 
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
 		return filter;
 	}
 
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.getNamedQuery( queryName );
 	}
 
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( factory.getEntityPersister( entityName ), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Object result = interceptor.instantiate( persister.getEntityName(), entityMode, id );
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		return result;
 	}
 
 	public EntityMode getEntityMode() {
 		checkTransactionSynchStatus();
 		return entityMode;
 	}
 
 	public void setFlushMode(FlushMode flushMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
         LOG.trace("Setting flush mode to: " + flushMode);
 		this.flushMode = flushMode;
 	}
 
 	public FlushMode getFlushMode() {
 		checkTransactionSynchStatus();
 		return flushMode;
 	}
 
 	public CacheMode getCacheMode() {
 		checkTransactionSynchStatus();
 		return cacheMode;
 	}
 
 	public void setCacheMode(CacheMode cacheMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
         LOG.trace("Setting cache mode to: " + cacheMode);
 		this.cacheMode= cacheMode;
 	}
 
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getTransaction();
 	}
 
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
         // todo : should seriously consider not allowing a txn to begin from a child session
         // can always route the request to the root session...
         if (rootSession != null) LOG.transactionStartedOnNonRootSession();
 
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
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
 				return factory.getEntityPersister( entityName )
 						.getSubclassEntityPersister( object, getFactory(), entityMode );
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
 
 	public Collection filter(Object collection, String filter) throws HibernateException {
 		return listFilter( collection, filter, new QueryParameters( new Type[1], new Object[1] ) );
 	}
 
 	public Collection filter(Object collection, String filter, Object value, Type type) throws HibernateException {
 		return listFilter( collection, filter, new QueryParameters( new Type[]{null, type}, new Object[]{null, value} ) );
 	}
 
 	public Collection filter(Object collection, String filter, Object[] values, Type[] types)
 	throws HibernateException {
 		Object[] vals = new Object[values.length + 1];
 		Type[] typs = new Type[types.length + 1];
 		System.arraycopy( values, 0, vals, 1, values.length );
 		System.arraycopy( types, 0, typs, 1, types.length );
 		return listFilter( collection, filter, new QueryParameters( typs, vals ) );
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
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 		}
 		else {
 			// otherwise, we only need to flush if there are in-memory changes
 			// to the queried tables
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleBeforeFlush.getRole(), shallow, getEnabledFilters() );
 			if ( autoFlushIfRequired( plan.getQuerySpaces() ) ) {
 				// might need to run a different filter entirely after the flush
 				// because the collection role may have changed
 				entry = persistenceContext.getCollectionEntryOrNull( collection );
 				CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 				if ( roleBeforeFlush != roleAfterFlush ) {
 					if ( roleAfterFlush == null ) {
 						throw new QueryException( "The collection was dereferenced" );
 					}
 					plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 				}
 			}
 		}
 
 		if ( parameters != null ) {
 			parameters.getPositionalParameterValues()[0] = entry.getLoadedKey();
 			parameters.getPositionalParameterTypes()[0] = entry.getLoadedPersister().getKeyType();
 		}
 
 		return plan;
 	}
 
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, false );
 		List results = CollectionHelper.EMPTY_LIST;
 
 		boolean success = false;
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		return plan.performIterate( queryParameters, this );
 	}
 
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteria.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteria,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public List list(CriteriaImpl criteria) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteria.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteria,
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
 		}
 
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
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
 		return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 	}
 
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createQuery( queryString );
 	}
 
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createSQLQuery( sql );
 	}
 
 	public Query createSQLQuery(String sql, String returnAlias, Class returnClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new SQLQueryImpl(
 				sql,
 		        new String[] { returnAlias },
 		        new Class[] { returnClass },
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 	}
 
 	public Query createSQLQuery(String sql, String returnAliases[], Class returnClasses[]) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new SQLQueryImpl(
 				sql,
 		        returnAliases,
 		        returnClasses,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 	}
 
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
         LOG.trace("Scroll SQL query: " + customQuery.getSQL());
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return loader.scroll(queryParameters, this);
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
         LOG.trace("SQL query: " + customQuery.getSQL());
 
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
 			afterOperation(success);
 		}
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		checkTransactionSynchStatus();
 		return factory;
 	}
 
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		InitializeCollectionEventListener[] listener = listeners.getInitializeCollectionEventListeners();
 		for ( int i = 0; i < listener.length; i++ ) {
 			listener[i].onInitializeCollection( new InitializeCollectionEvent(collection, this) );
 		}
 	}
 
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
 
 	public String guessEntityName(Object object) throws HibernateException {
 		errorIfClosed();
 		return entityNameResolver.resolveEntityName( object );
 	}
 
 	public void cancelQuery() throws HibernateException {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().cancelLastQuery();
 	}
 
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
 	public String toString() {
 		StringBuffer buf = new StringBuffer(500)
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
 
 	public EventListeners getListeners() {
 		return listeners;
 	}
 
 	public ActionQueue getActionQueue() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 
 	public PersistenceContext getPersistenceContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl(this);
 	}
 
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
 	public boolean isReadOnly(Object entityOrProxy) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );
 	}
 
 	public void setReadOnly(Object entity, boolean readOnly) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly(entity, readOnly);
 	}
 
 	public void doWork(Work work) throws HibernateException {
 		transactionCoordinator.getJdbcCoordinator().coordinateWork( work );
 	}
 
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		errorIfClosed();
 		return transactionCoordinator;
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter enableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void disableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object getFilterParameterValue(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterValue( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type getFilterParameterType(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterType( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Map getEnabledFilters() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilters();
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String getFetchProfile() {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getInternalFetchProfile();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setFetchProfile(String fetchProfile) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.setInternalFetchProfile( fetchProfile );
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 
 	private void checkTransactionSynchStatus() {
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
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
         LOG.trace("Deserializing session");
 
 		ois.defaultReadObject();
 
 		entityNameResolver = new CoordinatingEntityNameResolver();
 
 		boolean isRootSession = ois.readBoolean();
 		connectionReleaseMode = ConnectionReleaseMode.parse( ( String ) ois.readObject() );
 		entityMode = EntityMode.parse( ( String ) ois.readObject() );
 		autoClear = ois.readBoolean();
 		flushMode = FlushMode.parse( ( String ) ois.readObject() );
 		cacheMode = CacheMode.parse( ( String ) ois.readObject() );
 		flushBeforeCompletionEnabled = ois.readBoolean();
 		autoCloseSessionEnabled = ois.readBoolean();
 		interceptor = ( Interceptor ) ois.readObject();
 
 		factory = SessionFactoryImpl.deserialize( ois );
 		listeners = factory.getEventListeners();
 
 		if ( isRootSession ) {
 			transactionCoordinator = TransactionCoordinatorImpl.deserialize( ois, this );
 		}
 
 		persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		actionQueue = ActionQueue.deserialize( ois, this );
 
 		loadQueryInfluencers = ( LoadQueryInfluencers ) ois.readObject();
 
 		childSessionsByEntityMode = ( Map ) ois.readObject();
 
 		// LoadQueryInfluencers.getEnabledFilters() tries to validate each enabled
 		// filter, which will fail when called before FilterImpl.afterDeserialize( factory );
 		// Instead lookup the filter by name and then call FilterImpl.afterDeserialize( factory ).
 		Iterator iter = loadQueryInfluencers.getEnabledFilterNames().iterator();
 		while ( iter.hasNext() ) {
 			String filterName = ( String ) iter.next();
 			 ( ( FilterImpl ) loadQueryInfluencers.getEnabledFilter( filterName )  )
 					.afterDeserialize( factory );
 		}
 
 		if ( isRootSession && childSessionsByEntityMode != null ) {
 			iter = childSessionsByEntityMode.values().iterator();
 			while ( iter.hasNext() ) {
 				final SessionImpl child = ( ( SessionImpl ) iter.next() );
 				child.rootSession = this;
 				child.transactionCoordinator = this.transactionCoordinator;
 			}
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param oos The output stream to which we are being written...
 	 * @throws IOException Indicates a general IO stream exception
 	 */
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if ( ! transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isReadyForSerialization() ) {
 			throw new IllegalStateException( "Cannot serialize a session while connected" );
 		}
 
         LOG.trace("Serializing session");
 
 		oos.defaultWriteObject();
 
 		oos.writeBoolean( rootSession == null );
 		oos.writeObject( connectionReleaseMode.toString() );
 		oos.writeObject( entityMode.toString() );
 		oos.writeBoolean( autoClear );
 		oos.writeObject( flushMode.toString() );
 		oos.writeObject( cacheMode.toString() );
 		oos.writeBoolean( flushBeforeCompletionEnabled );
 		oos.writeBoolean( autoCloseSessionEnabled );
 		// we need to writeObject() on this since interceptor is user defined
 		oos.writeObject( interceptor );
 
 		factory.serialize( oos );
 
 		if ( rootSession == null ) {
 			transactionCoordinator.serialize( oos );
 		}
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 		oos.writeObject( childSessionsByEntityMode );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object execute(LobCreationContext.Callback callback) {
 		Connection connection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnection();
 		try {
 			return callback.executeOnConnection( connection );
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"Error creating contextual LOB : " + e.getMessage()
 			);
 		}
 		finally {
 			transactionCoordinator.getJdbcCoordinator().getLogicalConnection().afterStatementExecution();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypeHelper getTypeHelper() {
 		return getSessionFactory().getTypeHelper();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public LobHelper getLobHelper() {
 		if ( lobHelper == null ) {
 			lobHelper = new LobHelperImpl( this );
 		}
 		return lobHelper;
 	}
 
 	private transient LobHelperImpl lobHelper;
 
 	private static class LobHelperImpl implements LobHelper {
 		private final SessionImpl session;
 
 		private LobHelperImpl(SessionImpl session) {
 			this.session = session;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Blob createBlob(byte[] bytes) {
 			return lobCreator().createBlob( bytes );
 		}
 
 		private LobCreator lobCreator() {
-			return session.getFactory().getSettings().getJdbcSupport().getLobCreator( session );
+			return session.getFactory().getJdbcServices().getLobCreator( session );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Blob createBlob(InputStream stream, long length) {
 			return lobCreator().createBlob( stream, length );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createClob(String string) {
 			return lobCreator().createClob( string );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createClob(Reader reader, long length) {
 			return lobCreator().createClob( reader, length );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createNClob(String string) {
 			return lobCreator().createNClob( string );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createNClob(Reader reader, long length) {
 			return lobCreator().createNClob( reader, length );
 		}
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
 		public String resolveEntityName(Object entity) {
 			String entityName = interceptor.getEntityName( entity );
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			Iterator itr = factory.iterateEntityNameResolvers( entityMode );
 			while ( itr.hasNext() ) {
 				final EntityNameResolver resolver = ( EntityNameResolver ) itr.next();
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
 
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode(lockMode);
 			return this;
 		}
 
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut(timeout);
 			return this;
 		}
 
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope(scope);
 			return this;
 		}
 
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
 		public void lock(Object object) throws HibernateException {
 			fireLock( object, lockOptions );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 936f29bed2..acc0df1f46 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1003,1589 +1003,1589 @@ public abstract class Loader {
 		if ( session.isEventSource() ) {
 			pre = new PreLoadEvent( (EventSource) session );
 			post = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			pre = null;
 			post = null;
 		}
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
             LOG.trace("Total objects hydrated: " + hydratedObjectsSize);
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SessionImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		 return null;
 	}
 
 	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(Object[] row,
 														 ResultSet rs,
 														 SessionImplementor session)
 			throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
 	private void registerNonExists(
 	        final EntityKey[] keys,
 	        final Loadable[] persisters,
 	        final SessionImplementor session) {
 
 		final int[] owners = getOwners();
 		if ( owners != null ) {
 
 			EntityType[] ownerAssociationTypes = getOwnerAssociationTypes();
 			for ( int i = 0; i < keys.length; i++ ) {
 
 				int owner = owners[i];
 				if ( owner > -1 ) {
 					EntityKey ownerKey = keys[owner];
 					if ( keys[i] == null && ownerKey != null ) {
 
 						final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 						/*final boolean isPrimaryKey;
 						final boolean isSpecialOneToOne;
 						if ( ownerAssociationTypes == null || ownerAssociationTypes[i] == null ) {
 							isPrimaryKey = true;
 							isSpecialOneToOne = false;
 						}
 						else {
 							isPrimaryKey = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()==null;
 							isSpecialOneToOne = ownerAssociationTypes[i].getLHSPropertyName()!=null;
 						}*/
 
 						//TODO: can we *always* use the "null property" approach for everything?
 						/*if ( isPrimaryKey && !isSpecialOneToOne ) {
 							persistenceContext.addNonExistantEntityKey(
 									new EntityKey( ownerKey.getIdentifier(), persisters[i], session.getEntityMode() )
 							);
 						}
 						else if ( isSpecialOneToOne ) {*/
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
 						}
 						/*}
 						else {
 							persistenceContext.addNonExistantEntityUniqueKey( new EntityUniqueKey(
 									persisters[i].getEntityName(),
 									ownerAssociationTypes[i].getRHSUniqueKeyPropertyName(),
 									ownerKey.getIdentifier(),
 									persisters[owner].getIdentifierType(),
 									session.getEntityMode()
 							) );
 						}*/
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read one collection element from the current row of the JDBC result set
 	 */
 	private void readCollectionElement(
 	        final Object optionalOwner,
 	        final Serializable optionalKey,
 	        final CollectionPersister persister,
 	        final CollectionAliases descriptor,
 	        final ResultSet rs,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		final Serializable collectionRowKey = (Serializable) persister.readKey(
 				rs,
 				descriptor.getSuffixedKeyAliases(),
 				session
 			);
 
 		if ( collectionRowKey != null ) {
 			// we found a collection element in the result set
 
             if (LOG.isDebugEnabled()) LOG.debugf("Found row of collection: %s",
                                                  MessageHelper.collectionInfoString(persister, collectionRowKey, getFactory()));
 
 			Object owner = optionalOwner;
 			if ( owner == null ) {
 				owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
 				if ( owner == null ) {
 					//TODO: This is assertion is disabled because there is a bug that means the
 					//	  original owner of a transient, uninitialized collection is not known
 					//	  if the collection is re-referenced by a different object associated
 					//	  with the current Session
 					//throw new AssertionFailure("bug loading unowned collection");
 				}
 			}
 
 			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, collectionRowKey );
 
 			if ( rowCollection != null ) {
 				rowCollection.readFrom( rs, persister, descriptor, owner );
 			}
 
 		}
 		else if ( optionalKey != null ) {
 			// we did not find a collection element in the result set, so we
 			// ensure that a collection is created with the owner's identifier,
 			// since what we have is an empty collection
 
             if (LOG.isDebugEnabled()) LOG.debugf("Result set contains (possibly empty) collection: %s",
                                                  MessageHelper.collectionInfoString(persister, optionalKey, getFactory()));
 
 			persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 		}
 
 		// else no collection element, but also no owner
 
 	}
 
 	/**
 	 * If this is a collection initializer, we need to tell the session that a collection
 	 * is being initialized, to account for the possibility of the collection having
 	 * no elements (hence no rows in the result set).
 	 */
 	private void handleEmptyCollections(
 	        final Serializable[] keys,
 	        final Object resultSetId,
 	        final SessionImplementor session) {
 
 		if ( keys != null ) {
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
                     if (LOG.isDebugEnabled()) LOG.debugf("Result set contains (possibly empty) collection: %s",
                                                          MessageHelper.collectionInfoString(collectionPersisters[j],
                                                                                             keys[i],
                                                                                             getFactory()));
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( ( ResultSet ) resultSetId )
 							.getLoadingCollection( collectionPersisters[j], keys[i] );
 				}
 			}
 		}
 
 		// else this is not a collection initializer (and empty collections will
 		// be detected by looking for the owner's identifier in the result set)
 	}
 
 	/**
 	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array.
 	 * Warning: this method is side-effecty.
 	 * <p/>
 	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
 	 */
 	private EntityKey getKeyFromResultSet(
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final ResultSet rs,
 	        final SessionImplementor session) throws HibernateException, SQLException {
 
 		Serializable resultId;
 
 		// if we know there is exactly 1 row, we can skip.
 		// it would be great if we could _always_ skip this;
 		// it is a problem for <key-many-to-one>
 
 		if ( isSingleRowLoader() && id != null ) {
 			resultId = id;
 		}
 		else {
 
 			Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 				);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, session.getEntityMode(), factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ?
 				null :
 				new EntityKey( resultId, persister, session.getEntityMode() );
 	}
 
 	/**
 	 * Check the version of the object in the <tt>ResultSet</tt> against
 	 * the object version in the session cache, throwing an exception
 	 * if the version numbers are different
 	 */
 	private void checkVersion(
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final Object entity,
 	        final ResultSet rs,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 				);
 			if ( !versionType.isEqual(version, currentVersion) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 		}
 
 	}
 
 	/**
 	 * Resolve any IDs for currently loaded objects, duplications within the
 	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the
 	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an
 	 * array of booleans (by side-effect) that determine whether the corresponding
 	 * object should be initialized.
 	 */
 	private Object[] getRow(
 	        final ResultSet rs,
 	        final Loadable[] persisters,
 	        final EntityKey[] keys,
 	        final Object optionalObject,
 	        final EntityKey optionalObjectKey,
 	        final LockMode[] lockModes,
 	        final List hydratedObjects,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
         if (LOG.isDebugEnabled()) LOG.debugf("Result row: %s", StringHelper.toString(keys));
 
 		final Object[] rowResults = new Object[cols];
 
 		for ( int i = 0; i < cols; i++ ) {
 
 			Object object = null;
 			EntityKey key = keys[i];
 
 			if ( keys[i] == null ) {
 				//do nothing
 			}
 			else {
 
 				//If the object is already loaded, return the loaded one
 				object = session.getEntityUsingInterceptor( key );
 				if ( object != null ) {
 					//its already loaded so don't need to hydrate it
 					instanceAlreadyLoaded(
 							rs,
 							i,
 							persisters[i],
 							key,
 							object,
 							lockModes[i],
 							session
 						);
 				}
 				else {
 					object = instanceNotYetLoaded(
 							rs,
 							i,
 							persisters[i],
 							descriptors[i].getRowIdAlias(),
 							key,
 							lockModes[i],
 							optionalObjectKey,
 							optionalObject,
 							hydratedObjects,
 							session
 						);
 				}
 
 			}
 
 			rowResults[i] = object;
 
 		}
 
 		return rowResults;
 	}
 
 	/**
 	 * The entity instance is already in the session cache
 	 */
 	private void instanceAlreadyLoaded(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final EntityKey key,
 	        final Object object,
 	        final LockMode lockMode,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 		if ( !persister.isInstance( object, session.getEntityMode() ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 				);
 		}
 
 		if ( LockMode.NONE != lockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 
 			final boolean isVersionCheckNeeded = persister.isVersioned() &&
 					session.getPersistenceContext().getEntry(object)
 							.getLockMode().lessThan( lockMode );
 			// we don't need to worry about existing version being uninitialized
 			// because this block isn't called by a re-entrant load (re-entrant
 			// loads _always_ have lock mode NONE)
 			if (isVersionCheckNeeded) {
 				//we only check the version when _upgrading_ lock modes
 				checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				//we need to upgrade the lock mode to the mode requested
 				session.getPersistenceContext().getEntry(object)
 						.setLockMode(lockMode);
 			}
 		}
 	}
 
 	/**
 	 * The entity instance is not in the session cache
 	 */
 	private Object instanceNotYetLoaded(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final String rowIdAlias,
 	        final EntityKey key,
 	        final LockMode lockMode,
 	        final EntityKey optionalObjectKey,
 	        final Object optionalObject,
 	        final List hydratedObjects,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 		final String instanceClass = getInstanceClass(
 				rs,
 				i,
 				persister,
 				key.getIdentifier(),
 				session
 			);
 
 		final Object object;
 		if ( optionalObjectKey != null && key.equals( optionalObjectKey ) ) {
 			//its the given optional object
 			object = optionalObject;
 		}
 		else {
 			// instantiate a new instance
 			object = session.instantiate( instanceClass, key.getIdentifier() );
 		}
 
 		//need to hydrate it.
 
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		LockMode acquiredLockMode = lockMode == LockMode.NONE ? LockMode.READ : lockMode;
 		loadFromResultSet(
 				rs,
 				i,
 				object,
 				instanceClass,
 				key,
 				rowIdAlias,
 				acquiredLockMode,
 				persister,
 				session
 			);
 
 		//materialize associations (and initialize the object) later
 		hydratedObjects.add( object );
 
 		return object;
 	}
 
 	private boolean isEagerPropertyFetchEnabled(int i) {
 		boolean[] array = getEntityEagerPropertyFetches();
 		return array!=null && array[i];
 	}
 
 
 	/**
 	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
 	 * an array or "hydrated" values (do not resolve associations yet),
 	 * and pass the hydrates state to the session.
 	 */
 	private void loadFromResultSet(
 	        final ResultSet rs,
 	        final int i,
 	        final Object object,
 	        final String instanceEntityName,
 	        final EntityKey key,
 	        final String rowIdAlias,
 	        final LockMode lockMode,
 	        final Loadable rootPersister,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
         if (LOG.isTraceEnabled()) LOG.trace("Initializing object from ResultSet: "
                                             + MessageHelper.infoString(persister, id, getFactory()));
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled(i);
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 			);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases(persister);
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 			);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if (ukName!=null) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex(ukName);
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, object ),
 						type,
 						session.getEntityMode(), session.getFactory()
 					);
 				session.getPersistenceContext().addEntity( euk, object );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				object,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 			);
 
 	}
 
 	/**
 	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
 	 */
 	private String getInstanceClass(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedDiscriminatorAlias(),
 					session,
 					null
 				);
 
 			final String result = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 
 			if ( result == null ) {
 				//woops we got an instance of another class hierarchy branch
 				throw new WrongClassException(
 						"Discriminator: " + discriminatorValue,
 						id,
 						persister.getEntityName()
 					);
 			}
 
 			return result;
 
 		}
 		else {
 			return persister.getEntityName();
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	private void advance(final ResultSet rs, final RowSelection selection)
 			throws SQLException {
 
 		final int firstRow = getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) rs.next();
 			}
 		}
 	}
 
 	private static boolean hasMaxRows(RowSelection selection) {
 		return selection != null && selection.getMaxRows() != null;
 	}
 
 	private static int getFirstRow(RowSelection selection) {
 		if ( selection == null || selection.getFirstRow() == null ) {
 			return 0;
 		}
 		else {
 			return selection.getFirstRow().intValue();
 		}
 	}
 
 	private int interpretFirstRow(int zeroBasedFirstResult) {
 		return getFactory().getDialect().convertToFirstRowValue( zeroBasedFirstResult );
 	}
 
 	/**
 	 * Should we pre-process the SQL string, adding a dialect-specific
 	 * LIMIT clause.
 	 */
 	private static boolean useLimit(final RowSelection selection, final Dialect dialect) {
 		return dialect.supportsLimit() && hasMaxRows( selection );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        final QueryParameters queryParameters,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		queryParameters.processFilters( getSQLString(), session );
 		String sql = queryParameters.getFilteredSQL();
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = useLimit( selection, dialect );
 		boolean hasFirstRow = getFirstRow( selection ) > 0;
 		boolean useOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		final boolean useScrollableResultSetToSkip = hasFirstRow &&
 				!useOffset &&
 				getFactory().getSettings().isScrollableResultSetsEnabled();
 		final ScrollMode scrollMode =
 				canScroll
 						? scroll || useScrollableResultSetToSkip
 								? queryParameters.getScrollMode()
 								: ScrollMode.SCROLL_INSENSITIVE
 						: null;
 
 		if ( useLimit ) {
 			sql = dialect.getLimitString(
 					sql.trim(), //use of trim() here is ugly?
 					useOffset ? getFirstRow(selection) : 0,
 					getMaxOrLimit(selection, dialect)
 				);
 		}
 
 		sql = preprocessSQL( sql, queryParameters, dialect );
 
 		PreparedStatement st = null;
 
 
 		st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			if ( useLimit && dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			if ( useLimit && !dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 
 			if ( !useLimit ) {
 				setMaxRows( st, selection );
 			}
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout().intValue() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize().intValue() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
                     if (!dialect.supportsLockTimeouts()) LOG.debugf("Lock timeout [%s] requested but dialect reported to not support lock timeouts",
                                                                     lockOptions.getTimeOut());
                     else if (dialect.isLockTimeoutParameterized()) st.setInt(col++, lockOptions.getTimeOut());
 				}
 			}
 
             LOG.trace("Bound [" + col + "] parameters total");
 		}
 		catch ( SQLException sqle ) {
 			st.close();
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			st.close();
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Some dialect-specific LIMIT clauses require the maximum last row number
 	 * (aka, first_row_number + total_row_count), while others require the maximum
 	 * returned row count (the total maximum number of rows to return).
 	 *
 	 * @param selection The selection criteria
 	 * @param dialect The dialect
 	 * @return The appropriate value to bind into the limit clause.
 	 */
 	private static int getMaxOrLimit(final RowSelection selection, final Dialect dialect) {
 		final int firstRow = dialect.convertToFirstRowValue( getFirstRow( selection ) );
 		final int lastRow = selection.getMaxRows().intValue();
 		if ( dialect.useMaxForLimit() ) {
 			return lastRow + firstRow;
 		}
 		else {
 			return lastRow;
 		}
 	}
 
 	/**
 	 * Bind parameter values needed by the dialect-specific LIMIT clause.
 	 *
 	 * @param statement The statement to which to bind limit param values.
 	 * @param index The bind position from which to start binding
 	 * @param selection The selection object containing the limit information.
 	 * @return The number of parameter values bound.
 	 * @throws java.sql.SQLException Indicates problems binding parameter values.
 	 */
 	private int bindLimitParameters(
 			final PreparedStatement statement,
 			final int index,
 			final RowSelection selection) throws SQLException {
 		Dialect dialect = getFactory().getDialect();
 		if ( !dialect.supportsVariableLimit() ) {
 			return 0;
 		}
 		if ( !hasMaxRows( selection ) ) {
 			throw new AssertionFailure( "no max results set" );
 		}
 		int firstRow = interpretFirstRow( getFirstRow( selection ) );
 		int lastRow = getMaxOrLimit( selection, dialect );
 		boolean hasFirstRow = dialect.supportsLimitOffset() && ( firstRow > 0 || dialect.forceLimitUsage() );
 		boolean reverse = dialect.bindLimitParametersInReverseOrder();
 		if ( hasFirstRow ) {
 			statement.setInt( index + ( reverse ? 1 : 0 ), firstRow );
 		}
 		statement.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
 		return hasFirstRow ? 2 : 1;
 	}
 
 	/**
 	 * Use JDBC API to limit the number of rows returned by the SQL query if necessary
 	 */
 	private void setMaxRows(
 			final PreparedStatement st,
 			final RowSelection selection) throws SQLException {
 		if ( hasMaxRows( selection ) ) {
 			st.setMaxRows( selection.getMaxRows().intValue() + interpretFirstRow( getFirstRow( selection ) ) );
 		}
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SessionImplementor session) throws SQLException {
 		int span = 0;
 		span += bindPositionalParameters( statement, queryParameters, startIndex, session );
 		span += bindNamedParameters( statement, queryParameters.getNamedParameters(), startIndex + span, session );
 		return span;
 	}
 
 	/**
 	 * Bind positional parameter values to the JDBC prepared statement.
 	 * <p/>
 	 * Positional parameters are those specified by JDBC-style ? parameters
 	 * in the source query.  It is (currently) expected that these come
 	 * before any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 	        final PreparedStatement statement,
 	        final QueryParameters queryParameters,
 	        final int startIndex,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( statement, values[i], startIndex + span, session );
 			span += types[i].getColumnSpan( getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Bind named parameters to the JDBC prepared statement.
 	 * <p/>
 	 * This is a generic implementation, the problem being that in the
 	 * general case we do not know enough information about the named
 	 * parameters to perform this in a complete manner here.  Thus this
 	 * is generally overridden on subclasses allowing named parameters to
 	 * apply the specific behavior.  The most usual limitation here is that
 	 * we need to assume the type span is always one...
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param namedParams A map of parameter names to values
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
                     LOG.debugf("bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex);
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Fetch a <tt>PreparedStatement</tt>, call <tt>setMaxRows</tt> and then execute it,
 	 * advance to the first result and return an SQL <tt>ResultSet</tt>
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final boolean autodiscovertypes,
 	        final boolean callable,
 	        final RowSelection selection,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		ResultSet rs = null;
 		try {
 			Dialect dialect = getFactory().getDialect();
 			rs = st.executeQuery();
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !dialect.supportsLimitOffset() || !useLimit( selection, dialect ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			st.close();
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
                 LOG.debugf("Wrapping result set [%s]", rs);
 				return session.getFactory()
-						.getSettings()
-						.getJdbcSupport().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
+						.getJdbcServices()
+						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
                 LOG.unableToWrapResultSet(e);
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
             LOG.trace("Building columnName->columnIndex cache");
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	protected final List loadEntity(
 			final SessionImplementor session,
 			final Object id,
 			final Type identifierType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalIdentifier,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
         if (LOG.isDebugEnabled()) LOG.debugf("Loading entity: %s",
                                              MessageHelper.infoString(persister, id, identifierType, getFactory()));
 
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] { identifierType } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity: " +
 			        MessageHelper.infoString( persisters[persisters.length-1], id, identifierType, getFactory() ),
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done entity load");
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 	        final SessionImplementor session,
 	        final Object key,
 	        final Object index,
 	        final Type keyType,
 	        final Type indexType,
 	        final EntityPersister persister) throws HibernateException {
 
         LOG.debugf("Loading collection element by index");
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] { keyType, indexType },
 							new Object[] { key, index }
 					),
 					false
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not collection element by index",
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done entity load");
 
 		return result;
 
 	}
 
 	/**
 	 * Called by wrappers that batch load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	public final List loadEntityBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type idType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
         if (LOG.isDebugEnabled()) LOG.debugf("Batch loading entity: %s", MessageHelper.infoString(persister, ids, getFactory()));
 
 		Type[] types = new Type[ids.length];
 		Arrays.fill( types, idType );
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( types );
 			qp.setPositionalParameterValues( ids );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalId );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity batch: " +
 			        MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done entity batch load");
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
         if (LOG.isDebugEnabled()) LOG.debugf("Loading collection: %s",
                                              MessageHelper.collectionInfoString(getCollectionPersisters()[0], id, getFactory()));
 
 		Serializable[] ids = new Serializable[]{id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[]{type}, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 				);
 		}
 
         LOG.debugf("Done loading collection");
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Type type) throws HibernateException {
 
         if (LOG.isDebugEnabled()) LOG.debugf("Batch loading collection: %s",
                                              MessageHelper.collectionInfoString(getCollectionPersisters()[0], ids, getFactory()));
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not initialize a collection batch: " +
 			        MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done batch load");
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Object[] parameterValues,
 	        final Type[] parameterTypes,
 	        final Map namedParameters,
 	        final Type type) throws HibernateException {
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections( session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load collection by subselect: " +
 			        MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Return the query results, using the query cache, called
 	 * by subclasses that implement cacheable queries
 	 */
 	protected List list(
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final Set querySpaces,
 	        final Type[] resultTypes) throws HibernateException {
 
 		final boolean cacheable = factory.getSettings().isQueryCacheEnabled() &&
 			queryParameters.isCacheable();
 
 		if ( cacheable ) {
 			return listUsingQueryCache( session, queryParameters, querySpaces, resultTypes );
 		}
 		else {
 			return listIgnoreQueryCache( session, queryParameters );
 		}
 	}
 
 	private List listIgnoreQueryCache(SessionImplementor session, QueryParameters queryParameters) {
 		return getResultList( doList( session, queryParameters ), queryParameters.getResultTransformer() );
 	}
 
 	private List listUsingQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
         if (querySpaces == null || querySpaces.size() == 0) LOG.trace("Unexpected querySpaces is "
                                                                       + (querySpaces == null ? querySpaces : "empty"));
         else LOG.trace("querySpaces is " + querySpaces.toString());
 
 		List result = getResultFromQueryCache(
 				session,
 				queryParameters,
 				querySpaces,
 				resultTypes,
 				queryCache,
 				key
 			);
 
 		if ( result == null ) {
 			result = doList( session, queryParameters, key.getResultTransformer() );
 
 			putResultInQueryCache(
 					session,
 					queryParameters,
 					resultTypes,
 					queryCache,
 					key,
 					result
 			);
 		}
 
 		ResultTransformer resolvedTransformer = resolveResultTransformer( queryParameters.getResultTransformer() );
 		if ( resolvedTransformer != null ) {
 			result = (
 					areResultSetRowsTransformedImmediately() ?
 							key.getResultTransformer().retransformResults(
 									result,
 									getResultRowAliases(),
 									queryParameters.getResultTransformer(),
 									includeInResultRow()
 							) :
 							key.getResultTransformer().untransformToTuples(
 									result
 							)
 			);
 		}
 
 		return getResultList( result, queryParameters.getResultTransformer() );
 	}
 
 	private QueryKey generateQueryKey(
 			SessionImplementor session,
 			QueryParameters queryParameters) {
 		return QueryKey.generateQueryKey(
 				getSQLString(),
 				queryParameters,
 				FilterKey.createFilterKeys(
 						session.getLoadQueryInfluencers().getEnabledFilters(),
 						session.getEntityMode()
 				),
 				session,
 				createCacheableResultTransformer( queryParameters )
 		);
 	}
 
 	private CacheableResultTransformer createCacheableResultTransformer(QueryParameters queryParameters) {
 		return CacheableResultTransformer.create(
 				queryParameters.getResultTransformer(),
 				getResultRowAliases(),
 				includeInResultRow()
 		);
 	}
 
 	private List getResultFromQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set querySpaces,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key) {
 		List result = null;
 
 		if ( session.getCacheMode().isGetEnabled() ) {
 			boolean isImmutableNaturalKeyLookup = queryParameters.isNaturalKeyLookup()
 					&& getEntityPersisters()[0].getEntityMetamodel().hasImmutableNaturalId();
 
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
 			if ( queryParameters.isReadOnlyInitialized() ) {
 				// The read-only/modifiable mode for the query was explicitly set.
 				// Temporarily set the default read-only/modifiable setting to the query's setting.
 				persistenceContext.setDefaultReadOnly( queryParameters.isReadOnly() );
 			}
 			else {
 				// The read-only/modifiable setting for the query was not initialized.
 				// Use the default read-only/modifiable from the persistence context instead.
 				queryParameters.setReadOnly( persistenceContext.isDefaultReadOnly() );
 			}
 			try {
 				result = queryCache.get(
 						key,
 						key.getResultTransformer().getCachedResultTypes( resultTypes ),
 						isImmutableNaturalKeyLookup,
 						querySpaces,
 						session
 				);
 			}
 			finally {
 				persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 			}
 
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				if ( result == null ) {
 					factory.getStatisticsImplementor()
 							.queryCacheMiss( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 				else {
 					factory.getStatisticsImplementor()
 							.queryCacheHit( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private void putResultInQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		if ( session.getCacheMode().isPutEnabled() ) {
 			boolean put = queryCache.put(
 					key,
 					key.getResultTransformer().getCachedResultTypes( resultTypes ),
 					result,
 					queryParameters.isNaturalKeyLookup(),
 					session
 			);
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor()
 						.queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
 
 	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
 		return doList( session, queryParameters, null);
 	}
 
 	private List doList(final SessionImplementor session,
 						final QueryParameters queryParameters,
 						final ResultTransformer forcedResultTransformer)
 			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query",
 			        getSQLString()
 				);
 		}
 
 		if ( stats ) {
 			getFactory().getStatisticsImplementor().queryExecuted(
 					getQueryIdentifier(),
 					result.size(),
 					System.currentTimeMillis() - startTime
 				);
 		}
 
 		return result;
 	}
 
 	/**
 	 * Check whether the current loader can support returning ScrollableResults.
 	 *
 	 * @throws HibernateException
 	 */
 	protected void checkScrollability() throws HibernateException {
 		// Allows various loaders (ok mainly the QueryLoader :) to check
 		// whether scrolling of their result set should be allowed.
 		//
 		// By default it is allowed.
 		return;
 	}
 
 	/**
 	 * Does the result set to be scrolled contain collection fetches?
 	 *
 	 * @return True if it does, and thus needs the special fetching scroll
 	 * functionality; false otherwise.
 	 */
 	protected boolean needsFetchingScroll() {
 		return false;
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 *
 	 * @param queryParameters The parameters with which the query should be executed.
 	 * @param returnTypes The expected return types of the query
 	 * @param holderInstantiator If the return values are expected to be wrapped
 	 * in a holder, this is the thing that knows how to wrap them.
 	 * @param session The session from which the scroll request originated.
 	 * @return The ScrollableResults instance.
 	 * @throws HibernateException Indicates an error executing the query, or constructing
 	 * the ScrollableResults.
 	 */
 	protected ScrollableResults scroll(
 	        final QueryParameters queryParameters,
 	        final Type[] returnTypes,
 	        final HolderInstantiator holderInstantiator,
 	        final SessionImplementor session) throws HibernateException {
 
 		checkScrollability();
 
 		final boolean stats = getQueryIdentifier() != null &&
 				getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 
 			PreparedStatement st = prepareQueryStatement( queryParameters, true, session );
 			ResultSet rs = getResultSet(st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), queryParameters.getRowSelection(), session);
 
 			if ( stats ) {
 				getFactory().getStatisticsImplementor().queryExecuted(
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
 					);
 			}
 
 			if ( needsFetchingScroll() ) {
 				return new FetchingScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
 						returnTypes,
 						holderInstantiator
 					);
 			}
 			else {
 				return new ScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
 						returnTypes,
 						holderInstantiator
 					);
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query using scroll",
 			        getSQLString()
 				);
 		}
 
 	}
 
 	/**
 	 * Calculate and cache select-clause suffixes. Must be
 	 * called by subclasses after instantiation.
 	 */
 	protected void postInstantiate() {}
 
 	/**
 	 * Get the result set descriptor
 	 */
 	protected abstract EntityAliases[] getEntityAliases();
 
 	protected abstract CollectionAliases[] getCollectionAliases();
 
 	/**
 	 * Identifies the query for statistics reporting, if null,
 	 * no statistics will be reported
 	 */
 	protected String getQueryIdentifier() {
 		return null;
 	}
 
 	public final SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getSQLString() + ')';
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/jdbc/JdbcSupportTest.java b/hibernate-core/src/test/java/org/hibernate/jdbc/JdbcSupportTest.java
deleted file mode 100644
index 1cab676171..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/jdbc/JdbcSupportTest.java
+++ /dev/null
@@ -1,297 +0,0 @@
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
-package org.hibernate.jdbc;
-import java.io.InputStream;
-import java.io.OutputStream;
-import java.io.Reader;
-import java.io.Writer;
-import java.lang.reflect.InvocationHandler;
-import java.lang.reflect.Method;
-import java.lang.reflect.Proxy;
-import java.sql.Blob;
-import java.sql.Clob;
-import java.sql.Connection;
-import java.sql.DatabaseMetaData;
-import java.sql.NClob;
-import java.sql.SQLException;
-import junit.framework.TestCase;
-import org.hibernate.engine.jdbc.BlobImplementer;
-import org.hibernate.engine.jdbc.ClobImplementer;
-import org.hibernate.engine.jdbc.ContextualLobCreator;
-import org.hibernate.engine.jdbc.JdbcSupport;
-import org.hibernate.engine.jdbc.LobCreationContext;
-import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.engine.jdbc.NClobImplementer;
-import org.hibernate.engine.jdbc.WrappedBlob;
-import org.hibernate.engine.jdbc.WrappedClob;
-
-/**
- * TODO : javadoc
- *
- * @author Steve Ebersole
- */
-public class JdbcSupportTest extends TestCase {
-	private static JdbcSupport jdbcSupport = new JdbcSupport( true );
-
-	public void testConnectedLobCreator() throws SQLException {
-		final Connection connection = createConnectionProxy(
-				4,
-				new JdbcLobBuilder() {
-					public Blob createBlob() {
-						return new JdbcBlob();
-					}
-
-					public Clob createClob() {
-						return new JdbcClob();
-					}
-
-					public NClob createNClob() {
-						return new JdbcNClob();
-					}
-				}
-		);
-		final LobCreationContext lobCreationContext = new LobCreationContext() {
-			public Object execute(Callback callback) {
-				try {
-					return callback.executeOnConnection( connection );
-				}
-				catch ( SQLException e ) {
-					throw new RuntimeException( "Unexpected SQLException", e );
-				}
-			}
-		};
-
-		LobCreator lobCreator = jdbcSupport.getLobCreator( lobCreationContext );
-		assertTrue( lobCreator instanceof ContextualLobCreator );
-
-		Blob blob = lobCreator.createBlob( new byte[] {} );
-		assertTrue( blob instanceof JdbcBlob );
-		blob = lobCreator.wrap( blob );
-		assertTrue( blob instanceof WrappedBlob );
-
-		Clob clob = lobCreator.createClob( "Hi" );
-		assertTrue( clob instanceof JdbcClob );
-		clob = lobCreator.wrap( clob );
-		assertTrue( clob instanceof WrappedClob );
-
-		Clob nclob = lobCreator.createNClob( "Hi" );
-		assertTrue( nclob instanceof JdbcNClob );
-		nclob = lobCreator.wrap( nclob );
-		assertTrue( nclob instanceof WrappedClob );
-
-		blob.free();
-		clob.free();
-		nclob.free();
-		connection.close();
-	}
-
-	public void testLegacyLobCreator() throws SQLException {
-		LobCreator lobCreator = jdbcSupport.getLobCreator();
-
-		Blob blob = lobCreator.createBlob( new byte[] {} );
-		assertTrue( blob instanceof BlobImplementer );
-		blob = lobCreator.wrap( blob );
-		assertTrue( blob instanceof WrappedBlob );
-
-		Clob clob = lobCreator.createClob( "Hi" );
-		assertTrue( clob instanceof ClobImplementer );
-		clob = lobCreator.wrap( clob );
-		assertTrue( clob instanceof WrappedClob );
-
-		Clob nclob = lobCreator.createNClob( "Hi" );
-		assertTrue( nclob instanceof NClobImplementer );
-		assertTrue( NClob.class.isInstance( nclob ) );
-		nclob = lobCreator.wrap( nclob );
-		assertTrue( nclob instanceof WrappedClob );
-
-		blob.free();
-		clob.free();
-		nclob.free();
-	}
-
-	private interface JdbcLobBuilder {
-		public Blob createBlob();
-		public Clob createClob();
-		public NClob createNClob();
-	}
-
-	private class ConnectionProxyHandler implements InvocationHandler {
-		private final JdbcLobBuilder lobBuilder;
-		private final DatabaseMetaData metadata;
-
-		private ConnectionProxyHandler(int version, JdbcLobBuilder lobBuilder) {
-			this.lobBuilder = lobBuilder;
-			this.metadata = createMetadataProxy( version );
-		}
-
-		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
-			// the only methods we are interested in are the LOB creation methods...
-			if ( args == null || args.length == 0 ) {
-				final String methodName = method.getName();
-				if ( "createBlob".equals( methodName ) ) {
-					return lobBuilder.createBlob();
-				}
-				else if ( "createClob".equals( methodName ) ) {
-					return lobBuilder.createClob();
-				}
-				else if ( "createNClob".equals( methodName ) ) {
-					return lobBuilder.createNClob();
-				}
-				else if ( "getMetaData".equals( methodName ) ) {
-					return metadata;
-				}
-			}
-			return null;
-		}
-	}
-
-	private static Class[] CONN_PROXY_TYPES = new Class[] { Connection.class };
-
-	private Connection createConnectionProxy(int version, JdbcLobBuilder jdbcLobBuilder) {
-		ConnectionProxyHandler handler = new ConnectionProxyHandler( version, jdbcLobBuilder );
-		return ( Connection ) Proxy.newProxyInstance( getClass().getClassLoader(), CONN_PROXY_TYPES, handler );
-	}
-
-	private class MetadataProxyHandler implements InvocationHandler {
-		private final int jdbcVersion;
-
-		private MetadataProxyHandler(int jdbcVersion) {
-			this.jdbcVersion = jdbcVersion;
-		}
-
-		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
-			final String methodName = method.getName();
-			if ( "getJDBCMajorVersion".equals( methodName ) ) {
-				return jdbcVersion;
-			}
-			return null;
-		}
-	}
-
-	private static Class[] META_PROXY_TYPES = new Class[] { DatabaseMetaData.class };
-
-	private DatabaseMetaData createMetadataProxy(int  version) {
-		MetadataProxyHandler handler = new MetadataProxyHandler( version );
-		return ( DatabaseMetaData ) Proxy.newProxyInstance( getClass().getClassLoader(), META_PROXY_TYPES, handler );
-	}
-
-	private class JdbcBlob implements Blob {
-		public long length() throws SQLException {
-			return 0;
-		}
-
-		public byte[] getBytes(long pos, int length) throws SQLException {
-			return new byte[0];
-		}
-
-		public InputStream getBinaryStream() throws SQLException {
-			return null;
-		}
-
-		public long position(byte[] pattern, long start) throws SQLException {
-			return 0;
-		}
-
-		public long position(Blob pattern, long start) throws SQLException {
-			return 0;
-		}
-
-		public int setBytes(long pos, byte[] bytes) throws SQLException {
-			return 0;
-		}
-
-		public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
-			return 0;
-		}
-
-		public OutputStream setBinaryStream(long pos) throws SQLException {
-			return null;
-		}
-
-		public void truncate(long len) throws SQLException {
-		}
-
-		public void free() throws SQLException {
-		}
-
-		public InputStream getBinaryStream(long pos, long length) throws SQLException {
-			return null;
-		}
-	}
-
-	private class JdbcClob implements Clob {
-		public long length() throws SQLException {
-			return 0;
-		}
-
-		public String getSubString(long pos, int length) throws SQLException {
-			return null;
-		}
-
-		public Reader getCharacterStream() throws SQLException {
-			return null;
-		}
-
-		public InputStream getAsciiStream() throws SQLException {
-			return null;
-		}
-
-		public long position(String searchstr, long start) throws SQLException {
-			return 0;
-		}
-
-		public long position(Clob searchstr, long start) throws SQLException {
-			return 0;
-		}
-
-		public int setString(long pos, String str) throws SQLException {
-			return 0;
-		}
-
-		public int setString(long pos, String str, int offset, int len) throws SQLException {
-			return 0;
-		}
-
-		public OutputStream setAsciiStream(long pos) throws SQLException {
-			return null;
-		}
-
-		public Writer setCharacterStream(long pos) throws SQLException {
-			return null;
-		}
-
-		public void truncate(long len) throws SQLException {
-		}
-
-		public void free() throws SQLException {
-		}
-
-		public Reader getCharacterStream(long pos, long length) throws SQLException {
-			return null;
-		}
-	}
-
-	private class JdbcNClob extends JdbcClob implements NClob {
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
index 1c5695a692..12bca3ff8b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/BasicTestingJdbcServiceImpl.java
@@ -1,144 +1,153 @@
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
 package org.hibernate.test.common;
 
+import java.sql.ResultSet;
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.Set;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.jdbc.JdbcSupport;
+import org.hibernate.engine.jdbc.ColumnNameCache;
+import org.hibernate.engine.jdbc.LobCreationContext;
+import org.hibernate.engine.jdbc.LobCreator;
+import org.hibernate.engine.jdbc.internal.ResultSetWrapperImpl;
 import org.hibernate.engine.jdbc.internal.TypeInfo;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Stoppable;
 
 
 /**
  * Implementation of the {@link JdbcServices} contract for use by these
  * tests.
  *
  * @author Steve Ebersole
  */
 public class BasicTestingJdbcServiceImpl implements JdbcServices {
 	private ConnectionProvider connectionProvider;
 	private Dialect dialect;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper exceptionHelper;
 	private final ExtractedDatabaseMetaData metaDataSupport = new MetaDataSupportImpl();
-
+	private final ResultSetWrapper resultSetWrapper = ResultSetWrapperImpl.INSTANCE;
 
 	public void start() {
 	}
 
 	public void stop() {
 		release();
 	}
 
 	public void prepare(boolean allowAggressiveRelease) {
 		connectionProvider = ConnectionProviderBuilder.buildConnectionProvider( allowAggressiveRelease );
 		dialect = ConnectionProviderBuilder.getCorrespondingDialect();
 		sqlStatementLogger = new SqlStatementLogger( true, false );
 		exceptionHelper = new SqlExceptionHelper();
 
 	}
 
 	public void release() {
 		if ( connectionProvider instanceof Stoppable ) {
 			( (Stoppable) connectionProvider ).stop();
 		}
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	public Dialect getDialect() {
 		return dialect;
 	}
 
-	public JdbcSupport getJdbcSupport() {
+	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
+		return null;
+	}
+
+	public ResultSetWrapper getResultSetWrapper() {
 		return null;
 	}
 
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return exceptionHelper;
 	}
 
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return metaDataSupport;
 	}
 
 	private static class MetaDataSupportImpl implements ExtractedDatabaseMetaData {
 		public boolean supportsScrollableResults() {
 			return false;
 		}
 
 		public boolean supportsGetGeneratedKeys() {
 			return false;
 		}
 
 		public boolean supportsBatchUpdates() {
 			return false;
 		}
 
 		public boolean supportsDataDefinitionInTransaction() {
 			return false;
 		}
 
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return false;
 		}
 
 		public Set<String> getExtraKeywords() {
 			return Collections.emptySet();
 		}
 
 		public SQLStateType getSqlStateType() {
 			return SQLStateType.UNKOWN;
 		}
 
 		public boolean doesLobLocatorUpdateCopy() {
 			return false;
 		}
 
 		public String getConnectionSchemaName() {
 			return null;
 		}
 
 		public String getConnectionCatalogName() {
 			return null;
 		}
 
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return null;
 		}
 	}
 }
