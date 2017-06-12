diff --git a/hibernate-core/src/main/java/org/hibernate/Hibernate.java b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
index e2cae866c8..4f737fda93 100644
--- a/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+++ b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
@@ -1,489 +1,181 @@
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
+
 import java.util.Iterator;
-import java.util.Properties;
 
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.HibernateIterator;
-import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
-import org.hibernate.type.AnyType;
-import org.hibernate.type.BigDecimalType;
-import org.hibernate.type.BigIntegerType;
-import org.hibernate.type.BinaryType;
-import org.hibernate.type.BlobType;
-import org.hibernate.type.BooleanType;
-import org.hibernate.type.ByteType;
-import org.hibernate.type.CalendarDateType;
-import org.hibernate.type.CalendarType;
-import org.hibernate.type.CharArrayType;
-import org.hibernate.type.CharacterArrayType;
-import org.hibernate.type.CharacterType;
-import org.hibernate.type.ClassType;
-import org.hibernate.type.ClobType;
-import org.hibernate.type.CurrencyType;
-import org.hibernate.type.DateType;
-import org.hibernate.type.DoubleType;
-import org.hibernate.type.FloatType;
-import org.hibernate.type.ImageType;
-import org.hibernate.type.IntegerType;
-import org.hibernate.type.LocaleType;
-import org.hibernate.type.LongType;
-import org.hibernate.type.ManyToOneType;
-import org.hibernate.type.MaterializedBlobType;
-import org.hibernate.type.MaterializedClobType;
-import org.hibernate.type.ObjectType;
-import org.hibernate.type.SerializableType;
-import org.hibernate.type.ShortType;
-import org.hibernate.type.StringType;
-import org.hibernate.type.TextType;
-import org.hibernate.type.TimeType;
-import org.hibernate.type.TimeZoneType;
-import org.hibernate.type.TimestampType;
-import org.hibernate.type.TrueFalseType;
-import org.hibernate.type.Type;
-import org.hibernate.type.TypeFactory;
-import org.hibernate.type.WrapperBinaryType;
-import org.hibernate.type.YesNoType;
-import org.hibernate.usertype.CompositeUserType;
 
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
-	/**
-	 * Hibernate <tt>boolean</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BOOLEAN} instead.
-	 */
-	public static final BooleanType BOOLEAN = BooleanType.INSTANCE;
-	/**
-	 * Hibernate <tt>true_false</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TRUE_FALSE} instead.
-	 */
-	public static final TrueFalseType TRUE_FALSE = TrueFalseType.INSTANCE;
-	/**
-	 * Hibernate <tt>yes_no</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#YES_NO} instead.
-	 */
-	public static final YesNoType YES_NO = YesNoType.INSTANCE;
-	/**
-	 * Hibernate <tt>byte</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BYTE} instead.
-	 */
-	public static final ByteType BYTE = ByteType.INSTANCE;
-	/**
-	 * Hibernate <tt>short</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#SHORT} instead.
-	 */
-	public static final ShortType SHORT = ShortType.INSTANCE;
-	/**
-	 * Hibernate <tt>integer</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#INTEGER} instead.
-	 */
-	public static final IntegerType INTEGER = IntegerType.INSTANCE;
-	/**
-	 * Hibernate <tt>long</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#LONG} instead.
-	 */
-	public static final LongType LONG = LongType.INSTANCE;
-	/**
-	 * Hibernate <tt>float</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#FLOAT} instead.
-	 */
-	public static final FloatType FLOAT = FloatType.INSTANCE;
-	/**
-	 * Hibernate <tt>double</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#DOUBLE} instead.
-	 */
-	public static final DoubleType DOUBLE = DoubleType.INSTANCE;
-	/**
-	 * Hibernate <tt>big_integer</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BIG_INTEGER} instead.
-	 */
-	public static final BigIntegerType BIG_INTEGER = BigIntegerType.INSTANCE;
-	/**
-	 * Hibernate <tt>big_decimal</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BIG_DECIMAL} instead.
-	 */
-	public static final BigDecimalType BIG_DECIMAL = BigDecimalType.INSTANCE;
-	/**
-	 * Hibernate <tt>character</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHARACTER} instead.
-	 */
-	public static final CharacterType CHARACTER = CharacterType.INSTANCE;
-	/**
-	 * Hibernate <tt>string</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#STRING} instead.
-	 */
-	public static final StringType STRING = StringType.INSTANCE;
-	/**
-	 * Hibernate <tt>time</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIME} instead.
-	 */
-	public static final TimeType TIME = TimeType.INSTANCE;
-	/**
-	 * Hibernate <tt>date</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#DATE} instead.
-	 */
-	public static final DateType DATE = DateType.INSTANCE;
-	/**
-	 * Hibernate <tt>timestamp</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIMESTAMP} instead.
-	 */
-	public static final TimestampType TIMESTAMP = TimestampType.INSTANCE;
-	/**
-	 * Hibernate <tt>binary</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BINARY} instead.
-	 */
-	public static final BinaryType BINARY = BinaryType.INSTANCE;
-	/**
-	 * Hibernate <tt>wrapper-binary</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#WRAPPER_BINARY} instead.
-	 */
-	public static final WrapperBinaryType WRAPPER_BINARY = WrapperBinaryType.INSTANCE;
-	/**
-	 * Hibernate char[] type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHAR_ARRAY} instead.
-	 */
-	public static final CharArrayType CHAR_ARRAY = CharArrayType.INSTANCE;
-	/**
-	 * Hibernate Character[] type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CHARACTER_ARRAY} instead.
-	 */
-	public static final CharacterArrayType CHARACTER_ARRAY = CharacterArrayType.INSTANCE;
-	/**
-	 * Hibernate <tt>image</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#IMAGE} instead.
-	 */
-	public static final ImageType IMAGE = ImageType.INSTANCE;
-	/**
-	 * Hibernate <tt>text</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TEXT} instead.
-	 */
-	public static final TextType TEXT = TextType.INSTANCE;
-	/**
-	 * Hibernate <tt>materialized_blob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#MATERIALIZED_BLOB} instead.
-	 */
-	public static final MaterializedBlobType MATERIALIZED_BLOB = MaterializedBlobType.INSTANCE;
-	/**
-	 * Hibernate <tt>materialized_clob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#MATERIALIZED_CLOB} instead.
-	 */
-	public static final MaterializedClobType MATERIALIZED_CLOB = MaterializedClobType.INSTANCE;
-	/**
-	 * Hibernate <tt>blob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#BLOB} instead.
-	 */
-	public static final BlobType BLOB = BlobType.INSTANCE;
-	/**
-	 * Hibernate <tt>clob</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CLOB} instead.
-	 */
-	public static final ClobType CLOB = ClobType.INSTANCE;
-	/**
-	 * Hibernate <tt>calendar</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CALENDAR} instead.
-	 */
-	public static final CalendarType CALENDAR = CalendarType.INSTANCE;
-	/**
-	 * Hibernate <tt>calendar_date</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CALENDAR_DATE} instead.
-	 */
-	public static final CalendarDateType CALENDAR_DATE = CalendarDateType.INSTANCE;
-	/**
-	 * Hibernate <tt>locale</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#LOCALE} instead.
-	 */
-	public static final LocaleType LOCALE = LocaleType.INSTANCE;
-	/**
-	 * Hibernate <tt>currency</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CURRENCY} instead.
-	 */
-	public static final CurrencyType CURRENCY = CurrencyType.INSTANCE;
-	/**
-	 * Hibernate <tt>timezone</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#TIMEZONE} instead.
-	 */
-	public static final TimeZoneType TIMEZONE = TimeZoneType.INSTANCE;
-	/**
-	 * Hibernate <tt>class</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#CLASS} instead.
-	 */
-	public static final ClassType CLASS = ClassType.INSTANCE;
-	/**
-	 * Hibernate <tt>serializable</tt> type.
-	 * @deprecated Use {@link org.hibernate.type.StandardBasicTypes#SERIALIZABLE} instead.
-	 */
-	public static final SerializableType SERIALIZABLE = SerializableType.INSTANCE;
-
-
-	/**
-	 * Hibernate <tt>object</tt> type.
-	 * @deprecated Use {@link ObjectType#INSTANCE} instead.
-	 */
-	public static final ObjectType OBJECT = ObjectType.INSTANCE;
-
-	/**
-	 * A Hibernate <tt>serializable</tt> type.
-	 *
-	 * @param serializableClass The {@link java.io.Serializable} implementor class.
-	 *
-	 * @return
-	 *
-	 * @deprecated Use {@link SerializableType#SerializableType} instead.
-	 */
-	@SuppressWarnings({ "unchecked" })
-	public static Type serializable(Class serializableClass) {
-		return new SerializableType( serializableClass );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#any} instead.
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type any(Type metaType, Type identifierType) {
-		return new AnyType( metaType, identifierType );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#entity} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration", "deprecation" })
-	public static Type entity(Class persistentClass) {
-		return entity( persistentClass.getName() );
-	}
 
-	private static class NoScope implements TypeFactory.TypeScope {
-		public static final NoScope INSTANCE = new NoScope();
-
-		public SessionFactoryImplementor resolveFactory() {
-			throw new HibernateException( "Cannot access SessionFactory from here" );
-		}
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#entity} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type entity(String entityName) {
-		return new ManyToOneType( NoScope.INSTANCE, entityName );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type custom(Class userTypeClass) {
-		return custom( userTypeClass, null );
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration" })
-	public static Type custom(Class userTypeClass, String[] parameterNames, String[] parameterValues) {
-		return custom( userTypeClass, toProperties( parameterNames, parameterValues ) );	}
-
-	private static Properties toProperties(String[] parameterNames, String[] parameterValues) {
-		if ( parameterNames == null || parameterNames.length == 0 ) {
-			return null;
-		}
-		Properties parameters = new Properties();
-		for ( int i = 0; i < parameterNames.length; i ++ ) {
-			parameters.put( parameterNames[i], parameterValues[i] );
-		}
-		return parameters;
-	}
-
-	/**
-	 * DO NOT USE!
-	 *
-	 * @deprecated Use {@link TypeHelper#custom} instead; see http://opensource.atlassian.com/projects/hibernate/browse/HHH-5182
-	 */
-	@SuppressWarnings({ "JavaDoc", "UnusedDeclaration", "unchecked" })
-	public static Type custom(Class userTypeClass, Properties parameters) {
-		if ( CompositeUserType.class.isAssignableFrom( userTypeClass ) ) {
-			return TypeFactory.customComponent( userTypeClass, parameters, NoScope.INSTANCE );
-		}
-		else {
-			return TypeFactory.custom( userTypeClass, parameters, NoScope.INSTANCE );
-		}
-	}
 
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
 				.getJdbcServices()
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index 0a63b62b59..c13791cfca 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,370 +1,372 @@
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
 package org.hibernate.cfg.annotations;
 
-import java.io.Serializable;
-import java.sql.Types;
-import java.util.Calendar;
-import java.util.Date;
-import java.util.Properties;
 import javax.persistence.Enumerated;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
+import java.io.Serializable;
+import java.sql.Types;
+import java.util.Calendar;
+import java.util.Date;
+import java.util.Properties;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
-import org.hibernate.Hibernate;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.CharacterArrayClobType;
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
 import org.hibernate.type.SerializableToBlobType;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.WrappedMaterializedBlobType;
-import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SimpleValueBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SimpleValueBinder.class.getName());
 
 	private String propertyName;
 	private String returnedClassName;
 	private Ejb3Column[] columns;
 	private String persistentClassName;
 	private String explicitType = "";
 	private Properties typeParameters = new Properties();
 	private Mappings mappings;
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
 	}
 
 	public void setVersion(boolean isVersion) {
 		this.isVersion = isVersion;
 	}
 
 	public void setTimestampVersionType(String versionType) {
 		this.timeStampVersionType = versionType;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		this.columns = columns;
 	}
 
 
 	public void setPersistentClassName(String persistentClassName) {
 		this.persistentClassName = persistentClassName;
 	}
 
 	//TODO execute it lazily to be order safe
 
 	public void setType(XProperty property, XClass returnedClass) {
 		if ( returnedClass == null ) {
 			return;
 		} //we cannot guess anything
 		XClass returnedClassOrElement = returnedClass;
 		boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) ) {
 
 			boolean isDate;
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, Date.class ) ) {
 				isDate = true;
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Calendar.class ) ) {
 				isDate = false;
 			}
 			else {
 				throw new AnnotationException(
 						"@Temporal should only be set on a java.util.Date or java.util.Calendar property: "
 								+ StringHelper.qualify( persistentClassName, propertyName )
 				);
 			}
 			final TemporalType temporalType = getTemporalType( property );
 			switch ( temporalType ) {
 				case DATE:
 					type = isDate ? "date" : "calendar_date";
 					break;
 				case TIME:
 					type = "time";
 					if ( !isDate ) {
 						throw new NotYetImplementedException(
 								"Calendar cannot persist TIME only"
 										+ StringHelper.qualify( persistentClassName, propertyName )
 						);
 					}
 					break;
 				case TIMESTAMP:
 					type = isDate ? "timestamp" : "calendar";
 					break;
 				default:
 					throw new AssertionFailure( "Unknown temporal type: " + temporalType );
 			}
 		}
 		else if ( property.isAnnotationPresent( Lob.class ) ) {
 
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Clob.class ) ) {
 				type = "clob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
-				type = Hibernate.MATERIALIZED_CLOB.getName();
+				type = StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
 				type = CharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
 				type = PrimitiveCharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Byte.class ) && isArray ) {
 				type = WrappedMaterializedBlobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, byte.class ) && isArray ) {
-				type = Hibernate.MATERIALIZED_BLOB.getName();
+				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
 			}
 			else if ( mappings.getReflectionManager()
 					.toXClass( Serializable.class )
 					.isAssignableFrom( returnedClassOrElement ) ) {
 				type = SerializableToBlobType.class.getName();
 				//typeParameters = new Properties();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
 		}
 		//implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
 		}
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
 				typeParameters = new Properties();
 				typeParameters.setProperty( EnumType.ENUM, returnedClassOrElement.getName() );
 				String schema = columns[0].getTable().getSchema();
 				schema = schema == null ? "" : schema;
 				String catalog = columns[0].getTable().getCatalog();
 				catalog = catalog == null ? "" : catalog;
 				typeParameters.setProperty( EnumType.SCHEMA, schema );
 				typeParameters.setProperty( EnumType.CATALOG, catalog );
 				typeParameters.setProperty( EnumType.TABLE, columns[0].getTable().getName() );
 				typeParameters.setProperty( EnumType.COLUMN, columns[0].getName() );
 				javax.persistence.EnumType enumType = getEnumType( property );
 				if ( enumType != null ) {
 					if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
 						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.INTEGER ) );
 					}
 					else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
 						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.VARCHAR ) );
 					}
 					else {
 						throw new AssertionFailure( "Unknown EnumType: " + enumType );
 					}
 				}
 			}
 		}
 		explicitType = type;
 		this.typeParameters = typeParameters;
 		Type annType = property.getAnnotation( Type.class );
 		setExplicitType( annType );
 	}
 
 	private javax.persistence.EnumType getEnumType(XProperty property) {
 		javax.persistence.EnumType enumType = null;
 		if ( key ) {
 			MapKeyEnumerated enumAnn = property.getAnnotation( MapKeyEnumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		else {
 			Enumerated enumAnn = property.getAnnotation( Enumerated.class );
 			if ( enumAnn != null ) {
 				enumType = enumAnn.value();
 			}
 		}
 		return enumType;
 	}
 
 	private TemporalType getTemporalType(XProperty property) {
 		if ( key ) {
 			MapKeyTemporal ann = property.getAnnotation( MapKeyTemporal.class );
 			return ann.value();
 		}
 		else {
 			Temporal ann = property.getAnnotation( Temporal.class );
 			return ann.value();
 		}
 	}
 
 	public void setExplicitType(String explicitType) {
 		this.explicitType = explicitType;
 	}
 
 	//FIXME raise an assertion failure  if setExplicitType(String) and setExplicitType(Type) are use at the same time
 
 	public void setExplicitType(Type typeAnn) {
 		if ( typeAnn != null ) {
 			explicitType = typeAnn.type();
 			typeParameters.clear();
 			for ( Parameter param : typeAnn.parameters() ) {
 				typeParameters.setProperty( param.name(), param.value() );
 			}
 		}
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	private void validate() {
 		//TODO check necessary params
 		Ejb3Column.checkPropertyConsistency( columns, propertyName );
 	}
 
 	public SimpleValue make() {
 
 		validate();
         LOG.debugf("building SimpleValue for %s", propertyName);
 		if ( table == null ) {
 			table = columns[0].getTable();
 		}
 		simpleValue = new SimpleValue( mappings, table );
 
 		linkWithValue();
 
 		boolean isInSecondPass = mappings.isInSecondPass();
 		SetSimpleValueTypeSecondPass secondPass = new SetSimpleValueTypeSecondPass( this );
 		if ( !isInSecondPass ) {
 			//Defer this to the second pass
 			mappings.addSecondPass( secondPass );
 		}
 		else {
 			//We are already in second pass
 			fillSimpleValue();
 		}
 		return simpleValue;
 	}
 
 	public void linkWithValue() {
 		if ( columns[0].isNameDeferred() && !mappings.isInSecondPass() && referencedEntityName != null ) {
 			mappings.addSecondPass(
 					new PkDrivenByDefaultMapsIdSecondPass(
 							referencedEntityName, ( Ejb3JoinColumn[] ) columns, simpleValue
 					)
 			);
 		}
 		else {
 			for ( Ejb3Column column : columns ) {
 				column.linkWithValue( simpleValue );
 			}
 		}
 	}
 
 	public void fillSimpleValue() {
 
         LOG.debugf("Setting SimpleValue typeName for %s", propertyName);
 
 		String type = BinderHelper.isEmptyAnnotationValue( explicitType ) ? returnedClassName : explicitType;
 		org.hibernate.mapping.TypeDef typeDef = mappings.getTypeDef( type );
 		if ( typeDef != null ) {
 			type = typeDef.getTypeClass();
 			simpleValue.setTypeParameters( typeDef.getParameters() );
 		}
 		if ( typeParameters != null && typeParameters.size() != 0 ) {
 			//explicit type params takes precedence over type def params
 			simpleValue.setTypeParameters( typeParameters );
 		}
 		simpleValue.setTypeName( type );
 		if ( persistentClassName != null ) {
 			simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
index e6cf07d9b8..a7c9c6d170 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
@@ -1,84 +1,84 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.criterion;
+
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
-import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.sql.ConditionFragment;
+import org.hibernate.type.StandardBasicTypes;
 
 /**
  * @author Gavin King
  */
 public class SizeExpression implements Criterion {
 	
 	private final String propertyName;
 	private final int size;
 	private final String op;
 	
 	protected SizeExpression(String propertyName, int size, String op) {
 		this.propertyName = propertyName;
 		this.size = size;
 		this.op = op;
 	}
 
 	public String toString() {
 		return propertyName + ".size" + op + size;
 	}
 
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
 	throws HibernateException {
 		String role = criteriaQuery.getEntityName(criteria, propertyName) + 
 				'.' +  
 				criteriaQuery.getPropertyName(propertyName);
 		QueryableCollection cp = (QueryableCollection) criteriaQuery.getFactory()
 				.getCollectionPersister(role);
 		//String[] fk = StringHelper.qualify( "collection_", cp.getKeyColumnNames() );
 		String[] fk = cp.getKeyColumnNames();
 		String[] pk = ( (Loadable) cp.getOwnerEntityPersister() ).getIdentifierColumnNames(); //TODO: handle property-ref
 		return "? " + 
 				op + 
 				" (select count(*) from " +
 				cp.getTableName() +
 				//" collection_ where " +
 				" where " +
 				new ConditionFragment()
 						.setTableAlias( criteriaQuery.getSQLAlias(criteria, propertyName) )
 						.setCondition(pk, fk)
 						.toFragmentString() +
 				")";
 	}
 
 	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
 	throws HibernateException {
 		return new TypedValue[] { 
-			new TypedValue( Hibernate.INTEGER, new Integer(size), EntityMode.POJO ) 
+			new TypedValue( StandardBasicTypes.INTEGER, size, EntityMode.POJO )
 		};
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
index 3fbed531d6..d96aecd78f 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
@@ -1,426 +1,428 @@
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
 package org.hibernate.dialect;
+
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
-import org.hibernate.Hibernate;
+
 import org.hibernate.LockOptions;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.PositionSubstringFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.ViolatedConstraintNameExtracter;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.internal.util.JdbcExceptionHelper;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An SQL dialect for Postgres
  * <p/>
  * For discussion of BLOB support in Postgres, as of 8.4, have a peek at
  * <a href="http://jdbc.postgresql.org/documentation/84/binary-data.html">http://jdbc.postgresql.org/documentation/84/binary-data.html</a>.
  * For the effects in regards to Hibernate see <a href="http://in.relation.to/15492.lace">http://in.relation.to/15492.lace</a>
  *
  * @author Gavin King
  */
 public class PostgreSQLDialect extends Dialect {
 
 	public PostgreSQLDialect() {
 		super();
 		registerColumnType( Types.BIT, "bool" );
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.SMALLINT, "int2" );
 		registerColumnType( Types.TINYINT, "int2" );
 		registerColumnType( Types.INTEGER, "int4" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float4" );
 		registerColumnType( Types.DOUBLE, "float8" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "bytea" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.LONGVARBINARY, "bytea" );
 		registerColumnType( Types.CLOB, "text" );
 		registerColumnType( Types.BLOB, "oid" );
 		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
 		registerColumnType( Types.OTHER, "uuid" );
 
 		registerFunction( "abs", new StandardSQLFunction("abs") );
-		registerFunction( "sign", new StandardSQLFunction("sign", Hibernate.INTEGER) );
-
-		registerFunction( "acos", new StandardSQLFunction("acos", Hibernate.DOUBLE) );
-		registerFunction( "asin", new StandardSQLFunction("asin", Hibernate.DOUBLE) );
-		registerFunction( "atan", new StandardSQLFunction("atan", Hibernate.DOUBLE) );
-		registerFunction( "cos", new StandardSQLFunction("cos", Hibernate.DOUBLE) );
-		registerFunction( "cot", new StandardSQLFunction("cot", Hibernate.DOUBLE) );
-		registerFunction( "exp", new StandardSQLFunction("exp", Hibernate.DOUBLE) );
-		registerFunction( "ln", new StandardSQLFunction("ln", Hibernate.DOUBLE) );
-		registerFunction( "log", new StandardSQLFunction("log", Hibernate.DOUBLE) );
-		registerFunction( "sin", new StandardSQLFunction("sin", Hibernate.DOUBLE) );
-		registerFunction( "sqrt", new StandardSQLFunction("sqrt", Hibernate.DOUBLE) );
-		registerFunction( "cbrt", new StandardSQLFunction("cbrt", Hibernate.DOUBLE) );
-		registerFunction( "tan", new StandardSQLFunction("tan", Hibernate.DOUBLE) );
-		registerFunction( "radians", new StandardSQLFunction("radians", Hibernate.DOUBLE) );
-		registerFunction( "degrees", new StandardSQLFunction("degrees", Hibernate.DOUBLE) );
-
-		registerFunction( "stddev", new StandardSQLFunction("stddev", Hibernate.DOUBLE) );
-		registerFunction( "variance", new StandardSQLFunction("variance", Hibernate.DOUBLE) );
-
-		registerFunction( "random", new NoArgSQLFunction("random", Hibernate.DOUBLE) );
+		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
+
+		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
+		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
+		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
+		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
+		registerFunction( "cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
+		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
+		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
+		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
+		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
+		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
+		registerFunction( "cbrt", new StandardSQLFunction("cbrt", StandardBasicTypes.DOUBLE) );
+		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
+		registerFunction( "radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
+		registerFunction( "degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
+
+		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
+		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
+
+		registerFunction( "random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
-		registerFunction( "chr", new StandardSQLFunction("chr", Hibernate.CHARACTER) );
+		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
-		registerFunction( "substr", new StandardSQLFunction("substr", Hibernate.STRING) );
+		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "to_ascii", new StandardSQLFunction("to_ascii") );
-		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", Hibernate.STRING) );
-		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", Hibernate.STRING) );
+		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", StandardBasicTypes.STRING) );
+		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", StandardBasicTypes.STRING) );
 		registerFunction( "md5", new StandardSQLFunction("md5") );
-		registerFunction( "ascii", new StandardSQLFunction("ascii", Hibernate.INTEGER) );
-		registerFunction( "char_length", new StandardSQLFunction("char_length", Hibernate.LONG) );
-		registerFunction( "bit_length", new StandardSQLFunction("bit_length", Hibernate.LONG) );
-		registerFunction( "octet_length", new StandardSQLFunction("octet_length", Hibernate.LONG) );
+		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
+		registerFunction( "char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG) );
+		registerFunction( "bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG) );
+		registerFunction( "octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG) );
 
 		registerFunction( "age", new StandardSQLFunction("age") );
-		registerFunction( "current_date", new NoArgSQLFunction("current_date", Hibernate.DATE, false) );
-		registerFunction( "current_time", new NoArgSQLFunction("current_time", Hibernate.TIME, false) );
-		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", Hibernate.TIMESTAMP, false) );
-		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", Hibernate.TIMESTAMP ) );
-		registerFunction( "localtime", new NoArgSQLFunction("localtime", Hibernate.TIME, false) );
-		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", Hibernate.TIMESTAMP, false) );
-		registerFunction( "now", new NoArgSQLFunction("now", Hibernate.TIMESTAMP) );
-		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", Hibernate.STRING) );
-
-		registerFunction( "current_user", new NoArgSQLFunction("current_user", Hibernate.STRING, false) );
-		registerFunction( "session_user", new NoArgSQLFunction("session_user", Hibernate.STRING, false) );
-		registerFunction( "user", new NoArgSQLFunction("user", Hibernate.STRING, false) );
-		registerFunction( "current_database", new NoArgSQLFunction("current_database", Hibernate.STRING, true) );
-		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", Hibernate.STRING, true) );
+		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
+		registerFunction( "current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false) );
+		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
+		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIME, false) );
+		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false) );
+		registerFunction( "now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP) );
+		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", StandardBasicTypes.STRING) );
+
+		registerFunction( "current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false) );
+		registerFunction( "session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false) );
+		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
+		registerFunction( "current_database", new NoArgSQLFunction("current_database", StandardBasicTypes.STRING, true) );
+		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, true) );
 		
-		registerFunction( "to_char", new StandardSQLFunction("to_char", Hibernate.STRING) );
-		registerFunction( "to_date", new StandardSQLFunction("to_date", Hibernate.DATE) );
-		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", Hibernate.TIMESTAMP) );
-		registerFunction( "to_number", new StandardSQLFunction("to_number", Hibernate.BIG_DECIMAL) );
+		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
+		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.DATE) );
+		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", StandardBasicTypes.TIMESTAMP) );
+		registerFunction( "to_number", new StandardSQLFunction("to_number", StandardBasicTypes.BIG_DECIMAL) );
 
-		registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "(","||",")" ) );
+		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","||",")" ) );
 
 		registerFunction( "locate", new PositionSubstringFunction() );
 
-		registerFunction( "str", new SQLFunctionTemplate(Hibernate.STRING, "cast(?1 as varchar)") );
+		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
 
 		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
 		getDefaultProperties().setProperty( Environment.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
 				descriptor = BlobTypeDescriptor.BLOB_BINDING;
 				break;
 			}
 			case Types.CLOB: {
 				descriptor = ClobTypeDescriptor.CLOB_BINDING;
 				break;
 			}
 			default: {
 				descriptor = super.getSqlTypeDescriptorOverride( sqlCode );
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName );
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "nextval ('" + sequenceName + "')";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName; //starts with 1, implicitly
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public String getQuerySequencesString() {
 		return "select relname from pg_class where relkind='S'";
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		return new StringBuffer( sql.length()+20 )
 				.append( sql )
 				.append( hasOffset ? " limit ? offset ?" : " limit ?" )
 				.toString();
 	}
 
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	public String getIdentitySelectString(String table, String column, int type) {
 		return new StringBuffer().append("select currval('")
 			.append(table)
 			.append('_')
 			.append(column)
 			.append("_seq')")
 			.toString();
 	}
 
 	public String getIdentityColumnString(int type) {
 		return type==Types.BIGINT ?
 			"bigserial not null" :
 			"serial not null";
 	}
 
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	public Class getNativeIdentifierGeneratorClass() {
 		return SequenceGenerator.class;
 	}
 
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 	
 	public boolean useInputStreamToInsertBlob() {
 		return false;
 	}
 
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	/**
 	 * Workaround for postgres bug #1453
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		String typeName = getTypeName(sqlType, 1, 1, 0);
 		//trim off the length/precision/scale
 		int loc = typeName.indexOf('(');
 		if (loc>-1) {
 			typeName = typeName.substring(0, loc);
 		}
 		return "null::" + typeName;
 	}
 
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "create temporary table";
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit drop";
 	}
 
 	/*public boolean dropTemporaryTableAfterUse() {
 		//we have to, because postgres sets current tx
 		//to rollback only after a failed create table
 		return true;
 	}*/
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "true" : "false";
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
-	 * Constraint-name extractor for Postgres contraint violation exceptions.
+	 * Constraint-name extractor for Postgres constraint violation exceptions.
 	 * Orginally contributed by Denny Bartelt.
 	 */
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			try {
 				int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( sqle )).intValue();
 				switch (sqlState) {
 					// CHECK VIOLATION
 					case 23514: return extractUsingTemplate("violates check constraint \"","\"", sqle.getMessage());
 					// UNIQUE VIOLATION
 					case 23505: return extractUsingTemplate("violates unique constraint \"","\"", sqle.getMessage());
 					// FOREIGN KEY VIOLATION
 					case 23503: return extractUsingTemplate("violates foreign key constraint \"","\"", sqle.getMessage());
 					// NOT NULL VIOLATION
 					case 23502: return extractUsingTemplate("null value in column \"","\" violates not-null constraint", sqle.getMessage());
 					// TODO: RESTRICT VIOLATION
 					case 23001: return null;
 					// ALL OTHER
 					default: return null;
 				}
 			} catch (NumberFormatException nfe) {
 				return null;
 			}
 		}
 	};
 	
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		// Register the type of the out param - PostgreSQL uses Types.OTHER
 		statement.registerOutParameter(col, Types.OTHER);
 		col++;
 		return col;
 	}
 
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		ResultSet rs = (ResultSet) ps.getObject(1);
 		return rs;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	//only necessary for postgre < 7.4
 	//http://anoncvs.postgresql.org/cvsweb.cgi/pgsql/doc/src/sgml/ref/create_sequence.sgml
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) {
 		return getCreateSequenceString( sequenceName ) + " start " + initialValue + " increment " + incrementSize;
 	}
 	
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 // seems to not really...
 //	public boolean supportsRowValueConstructorSyntax() {
 //		return true;
 //	}
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 
 	// locking support
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	public String getWriteLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT )
 			return " for update nowait";
 		else
 			return " for update";
 	}
 
 	public String getReadLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT )
 			return " for share nowait";
 		else
 			return " for share";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
index 6a3d25de75..108c1be4da 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
@@ -1,250 +1,252 @@
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
 package org.hibernate.dialect;
+
 import java.sql.Types;
-import org.hibernate.Hibernate;
+
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
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
+import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for TimesTen 5.1.
  * 
  * Known limitations:
  * joined-subclass support because of no CASE support in TimesTen
  * No support for subqueries that includes aggregation
  *  - size() in HQL not supported
  *  - user queries that does subqueries with aggregation
  * No CLOB/BLOB support 
  * No cascade delete support.
  * No Calendar support
  * No support for updating primary keys.
  * 
  * @author Sherry Listgarten and Max Andersen
  */
 public class TimesTenDialect extends Dialect {
 	
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
 	
 		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
 		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
-		registerFunction( "concat", new StandardSQLFunction("concat", Hibernate.STRING) );
+		registerFunction( "concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING) );
 		registerFunction( "mod", new StandardSQLFunction("mod") );
-		registerFunction( "to_char", new StandardSQLFunction("to_char",Hibernate.STRING) );
-		registerFunction( "to_date", new StandardSQLFunction("to_date",Hibernate.TIMESTAMP) );
-		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", Hibernate.TIMESTAMP, false) );
-		registerFunction( "getdate", new NoArgSQLFunction("getdate", Hibernate.TIMESTAMP, false) );
+		registerFunction( "to_char", new StandardSQLFunction("to_char",StandardBasicTypes.STRING) );
+		registerFunction( "to_date", new StandardSQLFunction("to_date",StandardBasicTypes.TIMESTAMP) );
+		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP, false) );
+		registerFunction( "getdate", new NoArgSQLFunction("getdate", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 
 	}
 	
 	public boolean dropConstraints() {
             return true;
 	}
 	
 	public boolean qualifyIndexName() {
             return false;
 	}
 
 	public boolean supportsUnique() {
 		return false;
 	}
     
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 		return false;
 	}
 	
     public String getAddColumnString() {
             return "add";
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "select first 1 " + sequenceName + ".nextval from sys.tables";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getQuerySequencesString() {
 		return "select NAME from sys.sequences";
 	}
 
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	// new methods in dialect3
 	/*public boolean supportsForUpdateNowait() {
 		return false;
 	}*/
 	
 	public String getForUpdateString() {
 		return "";
 	}
 	
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	public boolean supportsTableCheck() {
 		return false;
 	}
 	
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuffer( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "select first 1 sysdate from sys.tables";
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String generateTemporaryTableName(String baseTableName) {
 		String name = super.generateTemporaryTableName(baseTableName);
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// TimesTen has no known variation of a "SELECT ... FOR UPDATE" syntax...
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
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
index 9d2e041cfb..050a420885 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
@@ -1,225 +1,219 @@
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
 package org.hibernate.dialect.function;
+
 import java.util.ArrayList;
 import java.util.List;
-import org.hibernate.Hibernate;
+
 import org.hibernate.QueryException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * A {@link org.hibernate.dialect.function.SQLFunction} providing support for implementing TRIM functionality
  * (as defined by both the ANSI SQL and JPA specs) in cases where the dialect may not support the full <tt>trim</tt>
  * function itself.
  * <p/>
  * Follows the <a href="http://en.wikipedia.org/wiki/Template_method_pattern">template</a> pattern in order to implement
  * the {@link #render} method.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAnsiTrimEmulationFunction implements SQLFunction {
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final boolean hasArguments() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final boolean hasParenthesesIfNoArguments() {
 		return false;
 	}
 
-	/**
-	 * {@inheritDoc} 
-	 */
+	@Override
 	public final Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
-		return Hibernate.STRING;
+		return StandardBasicTypes.STRING;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
 		// According to both the ANSI-SQL and JPA specs, trim takes a variable number of parameters between 1 and 4.
 		// at least one paramer (trimSource) is required.  From the SQL spec:
 		//
 		// <trim function> ::=
 		//      TRIM <left paren> <trim operands> <right paren>
 		//
 		// <trim operands> ::=
 		//      [ [ <trim specification> ] [ <trim character> ] FROM ] <trim source>
 		//
 		// <trim specification> ::=
 		//      LEADING
 		//      | TRAILING
 		//      | BOTH
 		//
 		// If <trim specification> is omitted, BOTH is assumed.
 		// If <trim character> is omitted, space is assumed
 		if ( args.size() == 1 ) {
 			// we have the form: trim(trimSource)
 			//      so we trim leading and trailing spaces
 			return resolveBothSpaceTrimFunction().render( argumentType, args, factory );			// EARLY EXIT!!!!
 		}
 		else if ( "from".equalsIgnoreCase( ( String ) args.get( 0 ) ) ) {
 			// we have the form: trim(from trimSource).
 			//      This is functionally equivalent to trim(trimSource)
 			return resolveBothSpaceTrimFromFunction().render( argumentType, args, factory );  		// EARLY EXIT!!!!
 		}
 		else {
 			// otherwise, a trim-specification and/or a trim-character
 			// have been specified;  we need to decide which options
 			// are present and "do the right thing"
 			boolean leading = true;         // should leading trim-characters be trimmed?
 			boolean trailing = true;        // should trailing trim-characters be trimmed?
 			String trimCharacter;    		// the trim-character (what is to be trimmed off?)
 			String trimSource;       		// the trim-source (from where should it be trimmed?)
 
 			// potentialTrimCharacterArgIndex = 1 assumes that a
 			// trim-specification has been specified.  we handle the
 			// exception to that explicitly
 			int potentialTrimCharacterArgIndex = 1;
 			String firstArg = ( String ) args.get( 0 );
 			if ( "leading".equalsIgnoreCase( firstArg ) ) {
 				trailing = false;
 			}
 			else if ( "trailing".equalsIgnoreCase( firstArg ) ) {
 				leading = false;
 			}
 			else if ( "both".equalsIgnoreCase( firstArg ) ) {
 			}
 			else {
 				potentialTrimCharacterArgIndex = 0;
 			}
 
 			String potentialTrimCharacter = ( String ) args.get( potentialTrimCharacterArgIndex );
 			if ( "from".equalsIgnoreCase( potentialTrimCharacter ) ) { 
 				trimCharacter = "' '";
 				trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
 			}
 			else if ( potentialTrimCharacterArgIndex + 1 >= args.size() ) {
 				trimCharacter = "' '";
 				trimSource = potentialTrimCharacter;
 			}
 			else {
 				trimCharacter = potentialTrimCharacter;
 				if ( "from".equalsIgnoreCase( ( String ) args.get( potentialTrimCharacterArgIndex + 1 ) ) ) {
 					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 2 );
 				}
 				else {
 					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
 				}
 			}
 
-			List argsToUse = new ArrayList();
+			List<String> argsToUse = new ArrayList<String>();
 			argsToUse.add( trimSource );
 			argsToUse.add( trimCharacter );
 
 			if ( trimCharacter.equals( "' '" ) ) {
 				if ( leading && trailing ) {
 					return resolveBothSpaceTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else if ( leading ) {
 					return resolveLeadingSpaceTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else {
 					return resolveTrailingSpaceTrimFunction().render( argumentType, argsToUse, factory );
 				}
 			}
 			else {
 				if ( leading && trailing ) {
 					return resolveBothTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else if ( leading ) {
 					return resolveLeadingTrimFunction().render( argumentType, argsToUse, factory );
 				}
 				else {
 					return resolveTrailingTrimFunction().render( argumentType, argsToUse, factory );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Resolve the function definition which should be used to trim both leading and trailing spaces.
 	 * <p/>
 	 * In this form, the imput arguments is missing the <tt>FROM</tt> keyword.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveBothSpaceTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim both leading and trailing spaces.
 	 * <p/>
 	 * The same as {#link resolveBothSpaceTrimFunction} except that here the<tt>FROM</tt> is included and
 	 * will need to be accounted for during {@link SQLFunction#render} processing.
 	 * 
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveBothSpaceTrimFromFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim leading spaces.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveLeadingSpaceTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim trailing spaces.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveTrailingSpaceTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim the specified character from both the
 	 * beginning (leading) and end (trailing) of the trim source.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveBothTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim the specified character from the
 	 * beginning (leading) of the trim source.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveLeadingTrimFunction();
 
 	/**
 	 * Resolve the function definition which should be used to trim the specified character from the
 	 * end (trailing) of the trim source.
 	 *
 	 * @return The sql function
 	 */
 	protected abstract SQLFunction resolveTrailingTrimFunction();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
index ac094417b1..75b8fea6ee 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/DerbyConcatFunction.java
@@ -1,178 +1,178 @@
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
 package org.hibernate.dialect.function;
 import java.util.Iterator;
 import java.util.List;
 import org.hibernate.Hibernate;
 import org.hibernate.QueryException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * A specialized concat() function definition in which:<ol>
  * <li>we translate to use the concat operator ('||')</li>
  * <li>wrap dynamic parameters in CASTs to VARCHAR</li>
  * </ol>
  * <p/>
  * This last spec is to deal with a limitation on DB2 and variants (e.g. Derby)
  * where dynamic parameters cannot be used in concatenation unless they are being
  * concatenated with at least one non-dynamic operand.  And even then, the rules
  * are so convoluted as to what is allowed and when the CAST is needed and when
  * it is not that we just go ahead and do the CASTing.
  *
  * @author Steve Ebersole
  */
 public class DerbyConcatFunction implements SQLFunction {
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here we always return <tt>true</tt>
 	 */
 	public boolean hasArguments() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here we always return <tt>true</tt>
 	 */
 	public boolean hasParenthesesIfNoArguments() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
-	 * Here we always return {@link Hibernate#STRING}.
+	 * Here we always return {@link StandardBasicTypes#STRING}.
 	 */
 	public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here's the meat..  The whole reason we have a separate impl for this for Derby is to re-define
 	 * this method.  The logic here says that if not all the incoming args are dynamic parameters
 	 * (i.e. <tt>?</tt>) then we simply use the Derby concat operator (<tt>||</tt>) on the unchanged
 	 * arg elements.  However, if all the args are dynamic parameters, then we need to wrap the individual
 	 * arg elements in <tt>cast</tt> function calls, use the concatenation operator on the <tt>cast</tt>
 	 * returns, and then wrap that whole thing in a call to the Derby <tt>varchar</tt> function.
 	 */
 	public String render(Type argumentType, List args, SessionFactoryImplementor factory) throws QueryException {
 		boolean areAllArgsParams = true;
 		Iterator itr = args.iterator();
 		while ( itr.hasNext() ) {
 			final String arg = ( String ) itr.next();
 			if ( ! "?".equals( arg ) ) {
 				areAllArgsParams = false;
 				break;
 			}
 		}
 
 		if ( areAllArgsParams ) {
 			return join(
 					args.iterator(),
 					new StringTransformer() {
 						public String transform(String string) {
 							return "cast( ? as varchar(32672) )";
 						}
 					},
 					new StringJoinTemplate() {
 						public String getBeginning() {
 							return "varchar( ";
 						}
 						public String getSeparator() {
 							return " || ";
 						}
 						public String getEnding() {
 							return " )";
 						}
 					}
 			);
 		}
 		else {
 			return join(
 					args.iterator(),
 					new StringTransformer() {
 						public String transform(String string) {
 							return string;
 						}
 					},
 					new StringJoinTemplate() {
 						public String getBeginning() {
 							return "(";
 						}
 						public String getSeparator() {
 							return "||";
 						}
 						public String getEnding() {
 							return ")";
 						}
 					}
 			);
 		}
 	}
 
 	private static interface StringTransformer {
 		public String transform(String string);
 	}
 
 	private static interface StringJoinTemplate {
 		/**
 		 * Getter for property 'beginning'.
 		 *
 		 * @return Value for property 'beginning'.
 		 */
 		public String getBeginning();
 		/**
 		 * Getter for property 'separator'.
 		 *
 		 * @return Value for property 'separator'.
 		 */
 		public String getSeparator();
 		/**
 		 * Getter for property 'ending'.
 		 *
 		 * @return Value for property 'ending'.
 		 */
 		public String getEnding();
 	}
 
 	private static String join(Iterator/*<String>*/ elements, StringTransformer elementTransformer, StringJoinTemplate template) {
 		// todo : make this available via StringHelper?
 		StringBuffer buffer = new StringBuffer( template.getBeginning() );
 		while ( elements.hasNext() ) {
 			final String element = ( String ) elements.next();
 			buffer.append( elementTransformer.transform( element ) );
 			if ( elements.hasNext() ) {
 				buffer.append( template.getSeparator() );
 			}
 		}
 		return buffer.append( template.getEnding() ).toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
index fe8275d07e..1f7fa8d128 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/function/StandardAnsiSqlAggregationFunctions.java
@@ -1,203 +1,205 @@
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
 package org.hibernate.dialect.function;
+
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
-import org.hibernate.Hibernate;
+
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
- * TODO : javadoc
+ * Centralized definition of standard ANSI SQL aggregation functions
  *
  * @author Steve Ebersole
  */
 public class StandardAnsiSqlAggregationFunctions {
 	/**
 	 * Definition of a standard ANSI SQL compliant <tt>COUNT</tt> function
 	 */
 	public static class CountFunction extends StandardSQLFunction {
 		public static final CountFunction INSTANCE = new CountFunction();
 
 		public CountFunction() {
-			super( "count", Hibernate.LONG );
+			super( "count", StandardBasicTypes.LONG );
 		}
 
 		@Override
 		public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) {
 			if ( arguments.size() > 1 ) {
 				if ( "distinct".equalsIgnoreCase( arguments.get( 0 ).toString() ) ) {
 					return renderCountDistinct( arguments );
 				}
 			}
 			return super.render( firstArgumentType, arguments, factory );
 		}
 
 		private String renderCountDistinct(List arguments) {
 			StringBuffer buffer = new StringBuffer();
 			buffer.append( "count(distinct " );
 			String sep = "";
 			Iterator itr = arguments.iterator();
 			itr.next(); // intentionally skip first
 			while ( itr.hasNext() ) {
 				buffer.append( sep )
 						.append( itr.next() );
 				sep = ", ";
 			}
 			return buffer.append( ")" ).toString();
 		}
 	}
 
 
 	/**
 	 * Definition of a standard ANSI SQL compliant <tt>AVG</tt> function
 	 */
 	public static class AvgFunction extends StandardSQLFunction {
 		public static final AvgFunction INSTANCE = new AvgFunction();
 
 		public AvgFunction() {
-			super( "avg", Hibernate.DOUBLE );
+			super( "avg", StandardBasicTypes.DOUBLE );
 		}
 
 		@Override
 		public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
 			int jdbcTypeCode = determineJdbcTypeCode( firstArgumentType, factory );
 			return render( jdbcTypeCode, arguments.get(0).toString(), factory );
 		}
 
 		protected final int determineJdbcTypeCode(Type firstArgumentType, SessionFactoryImplementor factory) throws QueryException {
 			try {
 				final int[] jdbcTypeCodes = firstArgumentType.sqlTypes( factory );
 				if ( jdbcTypeCodes.length != 1 ) {
 					throw new QueryException( "multiple-column type in avg()" );
 				}
 				return jdbcTypeCodes[0];
 			}
 			catch ( MappingException me ) {
 				throw new QueryException( me );
 			}
 		}
 
 		protected String render(int firstArgumentJdbcType, String argument, SessionFactoryImplementor factory) {
 			return "avg(" + renderArgument( argument, firstArgumentJdbcType ) + ")";
 		}
 
 		protected String renderArgument(String argument, int firstArgumentJdbcType) {
 			return argument;
 		}
 	}
 
 
 	public static class MaxFunction extends StandardSQLFunction {
 		public static final MaxFunction INSTANCE = new MaxFunction();
 
 		public MaxFunction() {
 			super( "max" );
 		}
 	}
 
 	public static class MinFunction extends StandardSQLFunction {
 		public static final MinFunction INSTANCE = new MinFunction();
 
 		public MinFunction() {
 			super( "min" );
 		}
 	}
 
 
 	public static class SumFunction extends StandardSQLFunction {
 		public static final SumFunction INSTANCE = new SumFunction();
 
 		public SumFunction() {
 			super( "sum" );
 		}
 
 		protected final int determineJdbcTypeCode(Type type, Mapping mapping) throws QueryException {
 			try {
 				final int[] jdbcTypeCodes = type.sqlTypes( mapping );
 				if ( jdbcTypeCodes.length != 1 ) {
 					throw new QueryException( "multiple-column type in sum()" );
 				}
 				return jdbcTypeCodes[0];
 			}
 			catch ( MappingException me ) {
 				throw new QueryException( me );
 			}
 		}
 
 		public Type getReturnType(Type firstArgumentType, Mapping mapping) {
 			final int jdbcType = determineJdbcTypeCode( firstArgumentType, mapping );
 
 			// First allow the actual type to control the return value; the underlying sqltype could
 			// actually be different
-			if ( firstArgumentType == Hibernate.BIG_INTEGER ) {
-				return Hibernate.BIG_INTEGER;
+			if ( firstArgumentType == StandardBasicTypes.BIG_INTEGER ) {
+				return StandardBasicTypes.BIG_INTEGER;
 			}
-			else if ( firstArgumentType == Hibernate.BIG_DECIMAL ) {
-				return Hibernate.BIG_DECIMAL;
+			else if ( firstArgumentType == StandardBasicTypes.BIG_DECIMAL ) {
+				return StandardBasicTypes.BIG_DECIMAL;
 			}
-			else if ( firstArgumentType == Hibernate.LONG
-					|| firstArgumentType == Hibernate.SHORT
-					|| firstArgumentType == Hibernate.INTEGER ) {
-				return Hibernate.LONG;
+			else if ( firstArgumentType == StandardBasicTypes.LONG
+					|| firstArgumentType == StandardBasicTypes.SHORT
+					|| firstArgumentType == StandardBasicTypes.INTEGER ) {
+				return StandardBasicTypes.LONG;
 			}
-			else if ( firstArgumentType == Hibernate.FLOAT || firstArgumentType == Hibernate.DOUBLE)  {
-				return Hibernate.DOUBLE;
+			else if ( firstArgumentType == StandardBasicTypes.FLOAT || firstArgumentType == StandardBasicTypes.DOUBLE)  {
+				return StandardBasicTypes.DOUBLE;
 			}
 
 			// finally use the jdbcType if == on Hibernate types did not find a match.
 			//
 			//	IMPL NOTE : we do not match on Types.NUMERIC because it could be either, so we fall-through to the
 			// 		first argument type
 			if ( jdbcType == Types.FLOAT
 					|| jdbcType == Types.DOUBLE
 					|| jdbcType == Types.DECIMAL
 					|| jdbcType == Types.REAL) {
-				return Hibernate.DOUBLE;
+				return StandardBasicTypes.DOUBLE;
 			}
 			else if ( jdbcType == Types.BIGINT
 					|| jdbcType == Types.INTEGER
 					|| jdbcType == Types.SMALLINT
 					|| jdbcType == Types.TINYINT ) {
-				return Hibernate.LONG;
+				return StandardBasicTypes.LONG;
 			}
 
 			// as a last resort, return the type of the first argument
 			return firstArgumentType;
 		}
 	}
 
 	public static void primeFunctionMap(Map<String, SQLFunction> functionMap) {
 		functionMap.put( AvgFunction.INSTANCE.getName(), AvgFunction.INSTANCE );
 		functionMap.put( CountFunction.INSTANCE.getName(), CountFunction.INSTANCE );
 		functionMap.put( MaxFunction.INSTANCE.getName(), MaxFunction.INSTANCE );
 		functionMap.put( MinFunction.INSTANCE.getName(), MinFunction.INSTANCE );
 		functionMap.put( SumFunction.INSTANCE.getName(), SumFunction.INSTANCE );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
index 3239a67e8c..b6c9f65553 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BetweenOperatorNode.java
@@ -1,84 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.hql.ast.tree;
-import org.hibernate.Hibernate;
-import org.hibernate.type.Type;
+
 import antlr.SemanticException;
 
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
 /**
- * Contract for nodes representing logcial BETWEEN (ternary) operators.
+ * Contract for nodes representing logical BETWEEN (ternary) operators.
  *
  * @author Steve Ebersole
  */
 public class BetweenOperatorNode extends SqlNode implements OperatorNode {
 
 	public void initialize() throws SemanticException {
 		Node fixture = getFixtureOperand();
 		if ( fixture == null ) {
 			throw new SemanticException( "fixture operand of a between operator was null" );
 		}
 		Node low = getLowOperand();
 		if ( low == null ) {
 			throw new SemanticException( "low operand of a between operator was null" );
 		}
 		Node high = getHighOperand();
 		if ( high == null ) {
 			throw new SemanticException( "high operand of a between operator was null" );
 		}
 		check( fixture, low, high );
 		check( low, high, fixture );
 		check( high, fixture, low );
 	}
 
 	public Type getDataType() {
 		// logic operators by definition resolve to boolean.
-		return Hibernate.BOOLEAN;
+		return StandardBasicTypes.BOOLEAN;
 	}
 
 	public Node getFixtureOperand() {
 		return ( Node ) getFirstChild();
 	}
 
 	public Node getLowOperand() {
 		return ( Node ) getFirstChild().getNextSibling();
 	}
 
 	public Node getHighOperand() {
 		return ( Node ) getFirstChild().getNextSibling().getNextSibling();
 	}
 
 	private void check(Node check, Node first, Node second) {
 		if ( ExpectedTypeAwareNode.class.isAssignableFrom( check.getClass() ) ) {
 			Type expectedType = null;
 			if ( SqlNode.class.isAssignableFrom( first.getClass() ) ) {
 				expectedType = ( ( SqlNode ) first ).getDataType();
 			}
 			if ( expectedType == null && SqlNode.class.isAssignableFrom( second.getClass() ) ) {
 				expectedType = ( ( SqlNode ) second ).getDataType();
 			}
 			( ( ExpectedTypeAwareNode ) check ).setExpectedType( expectedType );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
index c0cb42b548..5cfcc15446 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryArithmeticOperatorNode.java
@@ -1,212 +1,225 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.hql.ast.tree;
-import org.hibernate.Hibernate;
+
+import antlr.SemanticException;
+
 import org.hibernate.hql.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.ast.util.ColumnHelper;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
-import antlr.SemanticException;
 
 /**
  * Nodes which represent binary arithmetic operators.
  *
  * @author Gavin King
  */
 public class BinaryArithmeticOperatorNode extends AbstractSelectExpression implements BinaryOperatorNode, DisplayableNode {
 
 	public void initialize() throws SemanticException {
 		Node lhs = getLeftHandOperand();
 		Node rhs = getRightHandOperand();
 		if ( lhs == null ) {
 			throw new SemanticException( "left-hand operand of a binary operator was null" );
 		}
 		if ( rhs == null ) {
 			throw new SemanticException( "right-hand operand of a binary operator was null" );
 		}
 
 		Type lhType = ( lhs instanceof SqlNode ) ? ( ( SqlNode ) lhs ).getDataType() : null;
 		Type rhType = ( rhs instanceof SqlNode ) ? ( ( SqlNode ) rhs ).getDataType() : null;
 
 		if ( ExpectedTypeAwareNode.class.isAssignableFrom( lhs.getClass() ) && rhType != null ) {
 			Type expectedType = null;
 			// we have something like : "? [op] rhs"
 			if ( isDateTimeType( rhType ) ) {
 				// more specifically : "? [op] datetime"
 				//      1) if the operator is MINUS, the param needs to be of
 				//          some datetime type
 				//      2) if the operator is PLUS, the param needs to be of
 				//          some numeric type
-				expectedType = getType() == HqlSqlTokenTypes.PLUS ? Hibernate.DOUBLE : rhType;
+				expectedType = getType() == HqlSqlTokenTypes.PLUS ? StandardBasicTypes.DOUBLE : rhType;
 			}
 			else {
 				expectedType = rhType;
 			}
 			( ( ExpectedTypeAwareNode ) lhs ).setExpectedType( expectedType );
 		}
 		else if ( ParameterNode.class.isAssignableFrom( rhs.getClass() ) && lhType != null ) {
 			Type expectedType = null;
 			// we have something like : "lhs [op] ?"
 			if ( isDateTimeType( lhType ) ) {
 				// more specifically : "datetime [op] ?"
 				//      1) if the operator is MINUS, we really cannot determine
 				//          the expected type as either another datetime or
 				//          numeric would be valid
 				//      2) if the operator is PLUS, the param needs to be of
 				//          some numeric type
 				if ( getType() == HqlSqlTokenTypes.PLUS ) {
-					expectedType = Hibernate.DOUBLE;
+					expectedType = StandardBasicTypes.DOUBLE;
 				}
 			}
 			else {
 				expectedType = lhType;
 			}
 			( ( ExpectedTypeAwareNode ) rhs ).setExpectedType( expectedType );
 		}
 	}
 
 	/**
 	 * Figure out the type of the binary expression by looking at
 	 * the types of the operands. Sometimes we don't know both types,
 	 * if, for example, one is a parameter.
 	 */
 	public Type getDataType() {
 		if ( super.getDataType() == null ) {
 			super.setDataType( resolveDataType() );
 		}
 		return super.getDataType();
 	}
 
 	private Type resolveDataType() {
 		// TODO : we may also want to check that the types here map to exactly one column/JDBC-type
 		//      can't think of a situation where arithmetic expression between multi-column mappings
 		//      makes any sense.
 		Node lhs = getLeftHandOperand();
 		Node rhs = getRightHandOperand();
 		Type lhType = ( lhs instanceof SqlNode ) ? ( ( SqlNode ) lhs ).getDataType() : null;
 		Type rhType = ( rhs instanceof SqlNode ) ? ( ( SqlNode ) rhs ).getDataType() : null;
 		if ( isDateTimeType( lhType ) || isDateTimeType( rhType ) ) {
 			return resolveDateTimeArithmeticResultType( lhType, rhType );
 		}
 		else {
 			if ( lhType == null ) {
 				if ( rhType == null ) {
 					// we do not know either type
-					return Hibernate.DOUBLE; //BLIND GUESS!
+					return StandardBasicTypes.DOUBLE; //BLIND GUESS!
 				}
 				else {
 					// we know only the rhs-hand type, so use that
 					return rhType;
 				}
 			}
 			else {
 				if ( rhType == null ) {
 					// we know only the lhs-hand type, so use that
 					return lhType;
 				}
 				else {
-					if ( lhType==Hibernate.DOUBLE || rhType==Hibernate.DOUBLE ) return Hibernate.DOUBLE;
-					if ( lhType==Hibernate.FLOAT || rhType==Hibernate.FLOAT ) return Hibernate.FLOAT;
-					if ( lhType==Hibernate.BIG_DECIMAL || rhType==Hibernate.BIG_DECIMAL ) return Hibernate.BIG_DECIMAL;
-					if ( lhType==Hibernate.BIG_INTEGER || rhType==Hibernate.BIG_INTEGER ) return Hibernate.BIG_INTEGER;
-					if ( lhType==Hibernate.LONG || rhType==Hibernate.LONG ) return Hibernate.LONG;
-					if ( lhType==Hibernate.INTEGER || rhType==Hibernate.INTEGER ) return Hibernate.INTEGER;
+					if ( lhType== StandardBasicTypes.DOUBLE || rhType==StandardBasicTypes.DOUBLE ) {
+						return StandardBasicTypes.DOUBLE;
+					}
+					if ( lhType==StandardBasicTypes.FLOAT || rhType==StandardBasicTypes.FLOAT ) {
+						return StandardBasicTypes.FLOAT;
+					}
+					if ( lhType==StandardBasicTypes.BIG_DECIMAL || rhType==StandardBasicTypes.BIG_DECIMAL ) {
+						return StandardBasicTypes.BIG_DECIMAL;
+					}
+					if ( lhType==StandardBasicTypes.BIG_INTEGER || rhType==StandardBasicTypes.BIG_INTEGER ) {
+						return StandardBasicTypes.BIG_INTEGER;
+					}
+					if ( lhType==StandardBasicTypes.LONG || rhType==StandardBasicTypes.LONG ) {
+						return StandardBasicTypes.LONG;
+					}
+					if ( lhType==StandardBasicTypes.INTEGER || rhType==StandardBasicTypes.INTEGER ) {
+						return StandardBasicTypes.INTEGER;
+					}
 					return lhType;
 				}
 			}
 		}
 	}
 
 	private boolean isDateTimeType(Type type) {
 		if ( type == null ) {
 			return false;
 		}
 		return java.util.Date.class.isAssignableFrom( type.getReturnedClass() ) ||
 	           java.util.Calendar.class.isAssignableFrom( type.getReturnedClass() );
 	}
 
 	private Type resolveDateTimeArithmeticResultType(Type lhType, Type rhType) {
 		// here, we work under the following assumptions:
 		//      ------------ valid cases --------------------------------------
 		//      1) datetime + {something other than datetime} : always results
 		//              in a datetime ( db will catch invalid conversions )
 		//      2) datetime - datetime : always results in a DOUBLE
 		//      3) datetime - {something other than datetime} : always results
 		//              in a datetime ( db will catch invalid conversions )
 		//      ------------ invalid cases ------------------------------------
 		//      4) datetime + datetime
 		//      5) {something other than datetime} - datetime
 		//      6) datetime * {any type}
 		//      7) datetime / {any type}
 		//      8) {any type} / datetime
 		// doing so allows us to properly handle parameters as either the left
 		// or right side here in the majority of cases
 		boolean lhsIsDateTime = isDateTimeType( lhType );
 		boolean rhsIsDateTime = isDateTimeType( rhType );
 
 		// handle the (assumed) valid cases:
 		// #1 - the only valid datetime addition synatx is one or the other is a datetime (but not both)
 		if ( getType() == HqlSqlTokenTypes.PLUS ) {
 			// one or the other needs to be a datetime for us to get into this method in the first place...
 			return lhsIsDateTime ? lhType : rhType;
 		}
 		else if ( getType() == HqlSqlTokenTypes.MINUS ) {
 			// #3 - note that this is also true of "datetime - :param"...
 			if ( lhsIsDateTime && !rhsIsDateTime ) {
 				return lhType;
 			}
 			// #2
 			if ( lhsIsDateTime && rhsIsDateTime ) {
-				return Hibernate.DOUBLE;
+				return StandardBasicTypes.DOUBLE;
 			}
 		}
 		return null;
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		ColumnHelper.generateSingleScalarColumn( this, i );
 	}
 
 	/**
 	 * Retrieves the left-hand operand of the operator.
 	 *
 	 * @return The left-hand operand
 	 */
 	public Node getLeftHandOperand() {
 		return ( Node ) getFirstChild();
 	}
 
 	/**
 	 * Retrieves the right-hand operand of the operator.
 	 *
 	 * @return The right-hand operand
 	 */
 	public Node getRightHandOperand() {
 		return ( Node ) getFirstChild().getNextSibling();
 	}
 
 	public String getDisplayText() {
 		return "{dataType=" + getDataType() + "}";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
index 9232f36350..ad58ec0e58 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
@@ -1,263 +1,262 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.hql.ast.tree;
 
-import org.hibernate.Hibernate;
+import antlr.SemanticException;
+import antlr.collections.AST;
+
 import org.hibernate.HibernateException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.hql.antlr.HqlSqlTokenTypes;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.type.OneToOneType;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
-import antlr.SemanticException;
-import antlr.collections.AST;
 
 /**
  * Contract for nodes representing binary operators.
  *
  * @author Steve Ebersole
  */
 public class BinaryLogicOperatorNode extends HqlSqlWalkerNode implements BinaryOperatorNode {
 	/**
 	 * Performs the operator node initialization by seeking out any parameter
 	 * nodes and setting their expected type, if possible.
 	 */
 	public void initialize() throws SemanticException {
 		Node lhs = getLeftHandOperand();
 		if ( lhs == null ) {
 			throw new SemanticException( "left-hand operand of a binary operator was null" );
 		}
 		Node rhs = getRightHandOperand();
 		if ( rhs == null ) {
 			throw new SemanticException( "right-hand operand of a binary operator was null" );
 		}
 
 		Type lhsType = extractDataType( lhs );
 		Type rhsType = extractDataType( rhs );
 
 		if ( lhsType == null ) {
 			lhsType = rhsType;
 		}
 		if ( rhsType == null ) {
 			rhsType = lhsType;
 		}
 
 		if ( ExpectedTypeAwareNode.class.isAssignableFrom( lhs.getClass() ) ) {
 			( ( ExpectedTypeAwareNode ) lhs ).setExpectedType( rhsType );
 		}
 		if ( ExpectedTypeAwareNode.class.isAssignableFrom( rhs.getClass() ) ) {
 			( ( ExpectedTypeAwareNode ) rhs ).setExpectedType( lhsType );
 		}
 
 		mutateRowValueConstructorSyntaxesIfNecessary( lhsType, rhsType );
 	}
 
 	protected final void mutateRowValueConstructorSyntaxesIfNecessary(Type lhsType, Type rhsType) {
 		// TODO : this really needs to be delayed until after we definitively know all node types
 		// where this is currently a problem is parameters for which where we cannot unequivocally
 		// resolve an expected type
 		SessionFactoryImplementor sessionFactory = getSessionFactoryHelper().getFactory();
 		if ( lhsType != null && rhsType != null ) {
 			int lhsColumnSpan = getColumnSpan( lhsType, sessionFactory );
 			if ( lhsColumnSpan != getColumnSpan( rhsType, sessionFactory ) ) {
 				throw new TypeMismatchException(
 						"left and right hand sides of a binary logic operator were incompatibile [" +
 						lhsType.getName() + " : "+ rhsType.getName() + "]"
 				);
 			}
 			if ( lhsColumnSpan > 1 ) {
 				// for dialects which are known to not support ANSI-SQL row-value-constructor syntax,
 				// we should mutate the tree.
 				if ( !sessionFactory.getDialect().supportsRowValueConstructorSyntax() ) {
 					mutateRowValueConstructorSyntax( lhsColumnSpan );
 				}
 			}
 		}
 	}
 
 	private int getColumnSpan(Type type, SessionFactoryImplementor sfi) {
 		int columnSpan = type.getColumnSpan( sfi );
 		if ( columnSpan == 0 && type instanceof OneToOneType ) {
 			columnSpan = ( ( OneToOneType ) type ).getIdentifierOrUniqueKeyType( sfi ).getColumnSpan( sfi );
 		}
 		return columnSpan;
 	}
 
 	/**
 	 * Mutate the subtree relating to a row-value-constructor to instead use
 	 * a series of ANDed predicates.  This allows multi-column type comparisons
 	 * and explicit row-value-constructor syntax even on databases which do
 	 * not support row-value-constructor.
 	 * <p/>
 	 * For example, here we'd mutate "... where (col1, col2) = ('val1', 'val2) ..." to
 	 * "... where col1 = 'val1' and col2 = 'val2' ..."
 	 *
 	 * @param valueElements The number of elements in the row value constructor list.
 	 */
 	private void mutateRowValueConstructorSyntax(int valueElements) {
 		// mutation depends on the types of nodes involved...
 		int comparisonType = getType();
 		String comparisonText = getText();
 		setType( HqlSqlTokenTypes.AND );
 		setText( "AND" );
 		String[] lhsElementTexts = extractMutationTexts( getLeftHandOperand(), valueElements );
 		String[] rhsElementTexts = extractMutationTexts( getRightHandOperand(), valueElements );
 
 		ParameterSpecification lhsEmbeddedCompositeParameterSpecification =
 				getLeftHandOperand() == null || ( !ParameterNode.class.isInstance( getLeftHandOperand() ) )
 						? null
 						: ( ( ParameterNode ) getLeftHandOperand() ).getHqlParameterSpecification();
 
 		ParameterSpecification rhsEmbeddedCompositeParameterSpecification =
 				getRightHandOperand() == null || ( !ParameterNode.class.isInstance( getRightHandOperand() ) )
 						? null
 						: ( ( ParameterNode ) getRightHandOperand() ).getHqlParameterSpecification();
 
 		translate( valueElements, comparisonType, comparisonText,
                 lhsElementTexts, rhsElementTexts,
                 lhsEmbeddedCompositeParameterSpecification,
                 rhsEmbeddedCompositeParameterSpecification, this );
 	}
-	/**
-	 * 
-	 */
+
     protected void translate( int valueElements, int comparisonType,
             String comparisonText, String[] lhsElementTexts,
             String[] rhsElementTexts,
             ParameterSpecification lhsEmbeddedCompositeParameterSpecification,
             ParameterSpecification rhsEmbeddedCompositeParameterSpecification,
             AST container ) {
         for ( int i = valueElements - 1; i > 0; i-- ) {
 			if ( i == 1 ) {
 				AST op1 = getASTFactory().create( comparisonType, comparisonText );
 				AST lhs1 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[0] );
 				AST rhs1 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[0] );
 				op1.setFirstChild( lhs1 );
 				lhs1.setNextSibling( rhs1 );
 				container.setFirstChild( op1 );
 				AST op2 = getASTFactory().create( comparisonType, comparisonText );
 				AST lhs2 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[1] );
 				AST rhs2 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[1] );
 				op2.setFirstChild( lhs2 );
 				lhs2.setNextSibling( rhs2 );
 				op1.setNextSibling( op2 );
 
 				// "pass along" our initial embedded parameter node(s) to the first generated
 				// sql fragment so that it can be handled later for parameter binding...
 				SqlFragment fragment = ( SqlFragment ) lhs1;
 				if ( lhsEmbeddedCompositeParameterSpecification != null ) {
 					fragment.addEmbeddedParameter( lhsEmbeddedCompositeParameterSpecification );
 				}
 				if ( rhsEmbeddedCompositeParameterSpecification != null ) {
 					fragment.addEmbeddedParameter( rhsEmbeddedCompositeParameterSpecification );
 				}
 			}
 			else {
 				AST op = getASTFactory().create( comparisonType, comparisonText );
 				AST lhs = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[i] );
 				AST rhs = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[i] );
 				op.setFirstChild( lhs );
 				lhs.setNextSibling( rhs );
 				AST newContainer = getASTFactory().create( HqlSqlTokenTypes.AND, "AND" );
 				container.setFirstChild( newContainer );
 				newContainer.setNextSibling( op );
 				container = newContainer;
 			}
 		}
     }
 
 	protected static String[] extractMutationTexts(Node operand, int count) {
 		if ( operand instanceof ParameterNode ) {
 			String[] rtn = new String[count];
 			for ( int i = 0; i < count; i++ ) {
 				rtn[i] = "?";
 			}
 			return rtn;
 		}
 		else if ( operand.getType() == HqlSqlTokenTypes.VECTOR_EXPR ) {
 			String[] rtn = new String[ operand.getNumberOfChildren() ];
 			int x = 0;
 			AST node = operand.getFirstChild();
 			while ( node != null ) {
 				rtn[ x++ ] = node.getText();
 				node = node.getNextSibling();
 			}
 			return rtn;
 		}
 		else if ( operand instanceof SqlNode ) {
 			String nodeText = operand.getText();
 			if ( nodeText.startsWith( "(" ) ) {
 				nodeText = nodeText.substring( 1 );
 			}
 			if ( nodeText.endsWith( ")" ) ) {
 				nodeText = nodeText.substring( 0, nodeText.length() - 1 );
 			}
 			String[] splits = StringHelper.split( ", ", nodeText );
 			if ( count != splits.length ) {
 				throw new HibernateException( "SqlNode's text did not reference expected number of columns" );
 			}
 			return splits;
 		}
 		else {
 			throw new HibernateException( "dont know how to extract row value elements from node : " + operand );
 		}
 	}
 
 	protected Type extractDataType(Node operand) {
 		Type type = null;
 		if ( operand instanceof SqlNode ) {
 			type = ( ( SqlNode ) operand ).getDataType();
 		}
 		if ( type == null && operand instanceof ExpectedTypeAwareNode ) {
 			type = ( ( ExpectedTypeAwareNode ) operand ).getExpectedType();
 		}
 		return type;
 	}
 
 	@Override
     public Type getDataType() {
 		// logic operators by definition resolve to booleans
-		return Hibernate.BOOLEAN;
+		return StandardBasicTypes.BOOLEAN;
 	}
 
 	/**
 	 * Retrieves the left-hand operand of the operator.
 	 *
 	 * @return The left-hand operand
 	 */
 	public Node getLeftHandOperand() {
 		return ( Node ) getFirstChild();
 	}
 
 	/**
 	 * Retrieves the right-hand operand of the operator.
 	 *
 	 * @return The right-hand operand
 	 */
 	public Node getRightHandOperand() {
 		return ( Node ) getFirstChild().getNextSibling();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
index 4f234f0cbc..a44159c1c7 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/BooleanLiteralNode.java
@@ -1,78 +1,73 @@
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
 package org.hibernate.hql.ast.tree;
-import org.hibernate.Hibernate;
-import org.hibernate.QueryException;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.type.LiteralType;
-import org.hibernate.type.Type;
-
-/**
- * Represents a boolean literal within a query.
- *
- * @author Steve Ebersole
- */
-public class BooleanLiteralNode extends LiteralNode implements ExpectedTypeAwareNode {
-	private Type expectedType;
-
-	public Type getDataType() {
-		return expectedType == null ? Hibernate.BOOLEAN : expectedType;
-	}
-
-	public Boolean getValue() {
-		return getType() == TRUE ? Boolean.TRUE : Boolean.FALSE;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void setExpectedType(Type expectedType) {
-		this.expectedType = expectedType;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Type getExpectedType() {
-		return expectedType;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String getRenderText(SessionFactoryImplementor sessionFactory) {
-		try {
-			return typeAsLiteralType().objectToSQLString( getValue(), sessionFactory.getDialect() );
-		}
-		catch( Throwable t ) {
-			throw new QueryException( "Unable to render boolean literal value", t );
-		}
-	}
-
-	private LiteralType typeAsLiteralType() {
-		return (LiteralType) getDataType();
-
-	}
-}
+
+import org.hibernate.QueryException;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.type.LiteralType;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
+/**
+ * Represents a boolean literal within a query.
+ *
+ * @author Steve Ebersole
+ */
+public class BooleanLiteralNode extends LiteralNode implements ExpectedTypeAwareNode {
+	private Type expectedType;
+
+	public Type getDataType() {
+		return expectedType == null ? StandardBasicTypes.BOOLEAN : expectedType;
+	}
+
+	public Boolean getValue() {
+		return getType() == TRUE ? Boolean.TRUE : Boolean.FALSE;
+	}
+
+	@Override
+	public void setExpectedType(Type expectedType) {
+		this.expectedType = expectedType;
+	}
+
+	@Override
+	public Type getExpectedType() {
+		return expectedType;
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public String getRenderText(SessionFactoryImplementor sessionFactory) {
+		try {
+			return typeAsLiteralType().objectToSQLString( getValue(), sessionFactory.getDialect() );
+		}
+		catch( Throwable t ) {
+			throw new QueryException( "Unable to render boolean literal value", t );
+		}
+	}
+
+	private LiteralType typeAsLiteralType() {
+		return (LiteralType) getDataType();
+
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
index 373d027017..3f04744cfd 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/LiteralNode.java
@@ -1,66 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.hql.ast.tree;
-import org.hibernate.Hibernate;
+
+import antlr.SemanticException;
+
 import org.hibernate.hql.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.ast.util.ColumnHelper;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
-import antlr.SemanticException;
 
 /**
  * Represents a literal.
  *
  * @author josh
  */
 public class LiteralNode extends AbstractSelectExpression implements HqlSqlTokenTypes {
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		ColumnHelper.generateSingleScalarColumn( this, i );
 	}
 
 	public Type getDataType() {
 		switch ( getType() ) {
 			case NUM_INT:
-				return Hibernate.INTEGER;
+				return StandardBasicTypes.INTEGER;
 			case NUM_FLOAT:
-				return Hibernate.FLOAT;
+				return StandardBasicTypes.FLOAT;
 			case NUM_LONG:
-				return Hibernate.LONG;
+				return StandardBasicTypes.LONG;
 			case NUM_DOUBLE:
-				return Hibernate.DOUBLE;
+				return StandardBasicTypes.DOUBLE;
 			case NUM_BIG_INTEGER:
-				return Hibernate.BIG_INTEGER;
+				return StandardBasicTypes.BIG_INTEGER;
 			case NUM_BIG_DECIMAL:
-				return Hibernate.BIG_DECIMAL;
+				return StandardBasicTypes.BIG_DECIMAL;
 			case QUOTED_STRING:
-				return Hibernate.STRING;
+				return StandardBasicTypes.STRING;
 			case TRUE:
 			case FALSE:
-				return Hibernate.BOOLEAN;
+				return StandardBasicTypes.BOOLEAN;
 			default:
 				return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
index 2aea842231..0240795cd4 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/UnaryLogicOperatorNode.java
@@ -1,48 +1,48 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.hql.ast.tree;
-import org.hibernate.Hibernate;
+
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Represents a unary operator node.
  *
  * @author Steve Ebersole
  */
 public class UnaryLogicOperatorNode extends HqlSqlWalkerNode implements UnaryOperatorNode {
 	public Node getOperand() {
 		return ( Node ) getFirstChild();
 	}
 
 	public void initialize() {
 		// nothing to do; even if the operand is a parameter, no way we could
 		// infer an appropriate expected type here
 	}
 
 	public Type getDataType() {
 		// logic operators by definition resolve to booleans
-		return Hibernate.BOOLEAN;
+		return StandardBasicTypes.BOOLEAN;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
index fbcfaf1942..fa1cf3f9ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/SelectParser.java
@@ -1,254 +1,255 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.hql.classic;
+
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
-import org.hibernate.Hibernate;
+
 import org.hibernate.QueryException;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.hql.QuerySplitter;
-import org.hibernate.type.Type;
 import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
 
 /**
  * Parsers the select clause of a Hibernate query.
  *
  * @author Gavin King, David Channon
  */
 public class SelectParser implements Parser {
 
 	//TODO: arithmetic expressions, multiple new Foo(...)
 
 	private static final Set COUNT_MODIFIERS = new HashSet();
 
 	static {
 		COUNT_MODIFIERS.add( "distinct" );
 		COUNT_MODIFIERS.add( "all" );
 		COUNT_MODIFIERS.add( "*" );
 	}
 
 	private LinkedList aggregateFuncTokenList = new LinkedList();
 
 	private boolean ready;
 	private boolean aggregate;
 	private boolean first;
 	private boolean afterNew;
 	private boolean insideNew;
 	private boolean aggregateAddSelectScalar;
 	private Class holderClass;
 
 	private final SelectPathExpressionParser pathExpressionParser;
 	private final PathExpressionParser aggregatePathExpressionParser;
 
 	{
 		pathExpressionParser = new SelectPathExpressionParser();
 		aggregatePathExpressionParser = new PathExpressionParser();
 		//TODO: would be nice to use false, but issues with MS SQL
 		pathExpressionParser.setUseThetaStyleJoin( true );
 		aggregatePathExpressionParser.setUseThetaStyleJoin( true );
 	}
 
 	public void token(String token, QueryTranslatorImpl q) throws QueryException {
 
 		String lctoken = token.toLowerCase();
 
 		if ( first ) {
 			first = false;
 			if ( "distinct".equals( lctoken ) ) {
 				q.setDistinct( true );
 				return;
 			}
 			else if ( "all".equals( lctoken ) ) {
 				q.setDistinct( false );
 				return;
 			}
 		}
 
 		if ( afterNew ) {
 			afterNew = false;
 			try {
 				holderClass = ReflectHelper.classForName( QuerySplitter.getImportedClass( token, q.getFactory() ) );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				throw new QueryException( cnfe );
 			}
 			if ( holderClass == null ) throw new QueryException( "class not found: " + token );
 			q.setHolderClass( holderClass );
 			insideNew = true;
 		}
 		else if ( token.equals( "," ) ) {
 			if ( !aggregate && ready ) throw new QueryException( "alias or expression expected in SELECT" );
 			q.appendScalarSelectToken( ", " );
 			ready = true;
 		}
 		else if ( "new".equals( lctoken ) ) {
 			afterNew = true;
 			ready = false;
 		}
 		else if ( "(".equals( token ) ) {
 			if ( insideNew && !aggregate && !ready ) {
 				//opening paren in new Foo ( ... )
 				ready = true;
 			}
 			else if ( aggregate ) {
 				q.appendScalarSelectToken( token );
 			}
 			else {
 				throw new QueryException( "aggregate function expected before ( in SELECT" );
 			}
 			ready = true;
 		}
 		else if ( ")".equals( token ) ) {
 			if ( insideNew && !aggregate && !ready ) {
 				//if we are inside a new Result(), but not inside a nested function
 				insideNew = false;
 			}
 			else if ( aggregate && ready ) {
 				q.appendScalarSelectToken( token );
 				aggregateFuncTokenList.removeLast();
 				if ( aggregateFuncTokenList.size() < 1 ) {
 					aggregate = false;
 					ready = false;
 				}
 			}
 			else {
 				throw new QueryException( "( expected before ) in select" );
 			}
 		}
 		else if ( COUNT_MODIFIERS.contains( lctoken ) ) {
 			if ( !ready || !aggregate ) {
 				throw new QueryException( token + " only allowed inside aggregate function in SELECT" );
 			}
 			q.appendScalarSelectToken( token );
 			if ( "*".equals( token ) ) {
 				// special case
-				q.addSelectScalar( getFunction( "count", q ).getReturnType( Hibernate.LONG, q.getFactory() ) );
+				q.addSelectScalar( getFunction( "count", q ).getReturnType( StandardBasicTypes.LONG, q.getFactory() ) );
 			}
 		}
 		else if ( getFunction( lctoken, q ) != null && token.equals( q.unalias( token ) ) ) {
 			// the name of an SQL function
 			if ( !ready ) throw new QueryException( ", expected before aggregate function in SELECT: " + token );
 			aggregate = true;
 			aggregateAddSelectScalar = true;
 			aggregateFuncTokenList.add( lctoken );
 			ready = false;
 			q.appendScalarSelectToken( token );
 			if ( !aggregateHasArgs( lctoken, q ) ) {
 				q.addSelectScalar( aggregateType( aggregateFuncTokenList, null, q ) );
 				if ( !aggregateFuncNoArgsHasParenthesis( lctoken, q ) ) {
 					aggregateFuncTokenList.removeLast();
 					if ( aggregateFuncTokenList.size() < 1 ) {
 						aggregate = false;
 						ready = false;
 					}
 					else {
 						ready = true;
 					}
 				}
 			}
 		}
 		else if ( aggregate ) {
 			boolean constantToken = false;
 			if ( !ready ) throw new QueryException( "( expected after aggregate function in SELECT" );
 			try {
 				ParserHelper.parse( aggregatePathExpressionParser, q.unalias( token ), ParserHelper.PATH_SEPARATORS, q );
 			}
 			catch ( QueryException qex ) {
 				constantToken = true;
 			}
 
 			if ( constantToken ) {
 				q.appendScalarSelectToken( token );
 			}
 			else {
 				if ( aggregatePathExpressionParser.isCollectionValued() ) {
 					q.addCollection( aggregatePathExpressionParser.getCollectionName(),
 							aggregatePathExpressionParser.getCollectionRole() );
 				}
 				q.appendScalarSelectToken( aggregatePathExpressionParser.getWhereColumn() );
 				if ( aggregateAddSelectScalar ) {
 					q.addSelectScalar( aggregateType( aggregateFuncTokenList, aggregatePathExpressionParser.getWhereColumnType(), q ) );
 					aggregateAddSelectScalar = false;
 				}
 				aggregatePathExpressionParser.addAssociation( q );
 			}
 		}
 		else {
 			if ( !ready ) throw new QueryException( ", expected in SELECT" );
 			ParserHelper.parse( pathExpressionParser, q.unalias( token ), ParserHelper.PATH_SEPARATORS, q );
 			if ( pathExpressionParser.isCollectionValued() ) {
 				q.addCollection( pathExpressionParser.getCollectionName(),
 						pathExpressionParser.getCollectionRole() );
 			}
 			else if ( pathExpressionParser.getWhereColumnType().isEntityType() ) {
 				q.addSelectClass( pathExpressionParser.getSelectName() );
 			}
 			q.appendScalarSelectTokens( pathExpressionParser.getWhereColumns() );
 			q.addSelectScalar( pathExpressionParser.getWhereColumnType() );
 			pathExpressionParser.addAssociation( q );
 
 			ready = false;
 		}
 	}
 
 	public boolean aggregateHasArgs(String funcToken, QueryTranslatorImpl q) {
 		return getFunction( funcToken, q ).hasArguments();
 	}
 
 	public boolean aggregateFuncNoArgsHasParenthesis(String funcToken, QueryTranslatorImpl q) {
 		return getFunction( funcToken, q ).hasParenthesesIfNoArguments();
 	}
 
 	public Type aggregateType(List funcTokenList, Type type, QueryTranslatorImpl q) throws QueryException {
 		Type retType = type;
 		Type argType;
 		for ( int i = funcTokenList.size() - 1; i >= 0; i-- ) {
 			argType = retType;
 			String funcToken = ( String ) funcTokenList.get( i );
 			retType = getFunction( funcToken, q ).getReturnType( argType, q.getFactory() );
 		}
 		return retType;
 	}
 
 	private SQLFunction getFunction(String name, QueryTranslatorImpl q) {
 		return q.getFactory().getSqlFunctionRegistry().findSQLFunction( name );
 	}
 
 	public void start(QueryTranslatorImpl q) {
 		ready = true;
 		first = true;
 		aggregate = false;
 		afterNew = false;
 		insideNew = false;
 		holderClass = null;
 		aggregateFuncTokenList.clear();
 	}
 
 	public void end(QueryTranslatorImpl q) {
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
index ae737a2696..9ad39516a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
@@ -1,956 +1,958 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.impl;
+
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
+
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
-import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueResultException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
+import org.hibernate.Session;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.engine.query.ParameterMetadata;
 import org.hibernate.hql.classic.ParserHelper;
+import org.hibernate.internal.util.MarkerObject;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.property.Getter;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.SerializableType;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
-import org.hibernate.internal.util.MarkerObject;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.internal.util.StringHelper;
 
 /**
  * Abstract implementation of the Query interface.
  *
  * @author Gavin King
  * @author Max Andersen
  */
 public abstract class AbstractQueryImpl implements Query {
 
 	private static final Object UNSET_PARAMETER = new MarkerObject("<unset parameter>");
 	private static final Object UNSET_TYPE = new MarkerObject("<unset type>");
 
 	private final String queryString;
 	protected final SessionImplementor session;
 	protected final ParameterMetadata parameterMetadata;
 
 	// parameter bind values...
 	private List values = new ArrayList(4);
 	private List types = new ArrayList(4);
 	private Map namedParameters = new HashMap(4);
 	private Map namedParameterLists = new HashMap(4);
 
 	private Object optionalObject;
 	private Serializable optionalId;
 	private String optionalEntityName;
 
 	private RowSelection selection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
 	private FlushMode flushMode;
 	private CacheMode cacheMode;
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 	private Serializable collectionKey;
 	private Boolean readOnly;
 	private ResultTransformer resultTransformer;
 
 	public AbstractQueryImpl(
 			String queryString,
 	        FlushMode flushMode,
 	        SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		this.session = session;
 		this.queryString = queryString;
 		this.selection = new RowSelection();
 		this.flushMode = flushMode;
 		this.cacheMode = null;
 		this.parameterMetadata = parameterMetadata;
 	}
 
 	public ParameterMetadata getParameterMetadata() {
 		return parameterMetadata;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + queryString + ')';
 	}
 
 	public final String getQueryString() {
 		return queryString;
 	}
 
 	//TODO: maybe call it getRowSelection() ?
 	public RowSelection getSelection() {
 		return selection;
 	}
 	
 	public Query setFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 	
 	public Query setCacheMode(CacheMode cacheMode) {
 		this.cacheMode = cacheMode;
 		return this;
 	}
 
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
 	public Query setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	public Query setCacheRegion(String cacheRegion) {
 		if (cacheRegion != null)
 			this.cacheRegion = cacheRegion.trim();
 		return this;
 	}
 
 	public Query setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 	public Query setFirstResult(int firstResult) {
 		selection.setFirstRow( new Integer(firstResult) );
 		return this;
 	}
 
 	public Query setMaxResults(int maxResults) {
 		if ( maxResults < 0 ) {
 			// treat negatives specically as meaning no limit...
 			selection.setMaxRows( null );
 		}
 		else {
 			selection.setMaxRows( new Integer(maxResults) );
 		}
 		return this;
 	}
 
 	public Query setTimeout(int timeout) {
 		selection.setTimeout( new Integer(timeout) );
 		return this;
 	}
 	public Query setFetchSize(int fetchSize) {
 		selection.setFetchSize( new Integer(fetchSize) );
 		return this;
 	}
 
 	public Type[] getReturnTypes() throws HibernateException {
 		return session.getFactory().getReturnTypes( queryString );
 	}
 
 	public String[] getReturnAliases() throws HibernateException {
 		return session.getFactory().getReturnAliases( queryString );
 	}
 
 	public Query setCollectionKey(Serializable collectionKey) {
 		this.collectionKey = collectionKey;
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isReadOnly() {
 		return ( readOnly == null ?
 				getSession().getPersistenceContext().isDefaultReadOnly() :
 				readOnly.booleanValue() 
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Query setReadOnly(boolean readOnly) {
 		this.readOnly = Boolean.valueOf( readOnly );
 		return this;
 	}
 
 	public Query setResultTransformer(ResultTransformer transformer) {
 		this.resultTransformer = transformer;
 		return this;
 	}
 	
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	SessionImplementor getSession() {
 		return session;
 	}
 
 	public abstract LockOptions getLockOptions();
 
 
 	// Parameter handling code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns a shallow copy of the named parameter value map.
 	 *
 	 * @return Shallow copy of the named parameter value map
 	 */
 	protected Map getNamedParams() {
 		return new HashMap( namedParameters );
 	}
 
 	/**
 	 * Returns an array representing all named parameter names encountered
 	 * during (intial) parsing of the query.
 	 * <p/>
 	 * Note <i>initial</i> here means different things depending on whether
 	 * this is a native-sql query or an HQL/filter query.  For native-sql, a
 	 * precursory inspection of the query string is performed specifically to
 	 * locate defined parameters.  For HQL/filter queries, this is the
 	 * information returned from the query-translator.  This distinction
 	 * holds true for all parameter metadata exposed here.
 	 *
 	 * @return Array of named parameter names.
 	 * @throws HibernateException
 	 */
 	public String[] getNamedParameters() throws HibernateException {
 		return ArrayHelper.toStringArray( parameterMetadata.getNamedParameterNames() );
 	}
 
 	/**
 	 * Does this query contain named parameters?
 	 *
 	 * @return True if the query was found to contain named parameters; false
 	 * otherwise;
 	 */
 	public boolean hasNamedParameters() {
 		return parameterMetadata.getNamedParameterNames().size() > 0;
 	}
 
 	/**
 	 * Retreive the value map for any named parameter lists (i.e., for
 	 * auto-expansion) bound to this query.
 	 *
 	 * @return The parameter list value map.
 	 */
 	protected Map getNamedParameterLists() {
 		return namedParameterLists;
 	}
 
 	/**
 	 * Retreives the list of parameter values bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter values.
 	 */
 	protected List getValues() {
 		return values;
 	}
 
 	/**
 	 * Retreives the list of parameter {@link Type type}s bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter types.
 	 */
 	protected List getTypes() {
 		return types;
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @throws QueryException
 	 */
 	protected void verifyParameters() throws QueryException {
 		verifyParameters(false);
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @param reserveFirstParameter if true, the first ? will not be verified since
 	 * its needed for e.g. callable statements returning a out parameter
 	 * @throws HibernateException
 	 */
 	protected void verifyParameters(boolean reserveFirstParameter) throws HibernateException {
 		if ( parameterMetadata.getNamedParameterNames().size() != namedParameters.size() + namedParameterLists.size() ) {
 			Set missingParams = new HashSet( parameterMetadata.getNamedParameterNames() );
 			missingParams.removeAll( namedParameterLists.keySet() );
 			missingParams.removeAll( namedParameters.keySet() );
 			throw new QueryException( "Not all named parameters have been set: " + missingParams, getQueryString() );
 		}
 
 		int positionalValueSpan = 0;
 		for ( int i = 0; i < values.size(); i++ ) {
 			Object object = types.get( i );
 			if( values.get( i ) == UNSET_PARAMETER || object == UNSET_TYPE ) {
 				if ( reserveFirstParameter && i==0 ) {
 					continue;
 				}
 				else {
 					throw new QueryException( "Unset positional parameter at position: " + i, getQueryString() );
 				}
 			}
 			positionalValueSpan += ( (Type) object ).getColumnSpan( session.getFactory() );
 		}
 
 		if ( parameterMetadata.getOrdinalParameterCount() != positionalValueSpan ) {
 			if ( reserveFirstParameter && parameterMetadata.getOrdinalParameterCount() - 1 != positionalValueSpan ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		(parameterMetadata.getOrdinalParameterCount()-1) +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 			else if ( !reserveFirstParameter ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		parameterMetadata.getOrdinalParameterCount() +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 		}
 	}
 
 	public Query setParameter(int position, Object val, Type type) {
 		if ( parameterMetadata.getOrdinalParameterCount() == 0 ) {
 			throw new IllegalArgumentException("No positional parameters in query: " + getQueryString() );
 		}
 		if ( position < 0 || position > parameterMetadata.getOrdinalParameterCount() - 1 ) {
 			throw new IllegalArgumentException("Positional parameter does not exist: " + position + " in query: " + getQueryString() );
 		}
 		int size = values.size();
 		if ( position < size ) {
 			values.set( position, val );
 			types.set( position, type );
 		}
 		else {
 			// prepend value and type list with null for any positions before the wanted position.
 			for ( int i = 0; i < position - size; i++ ) {
 				values.add( UNSET_PARAMETER );
 				types.add( UNSET_TYPE );
 			}
 			values.add( val );
 			types.add( type );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val, Type type) {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		else {
 			 namedParameters.put( name, new TypedValue( type, val, session.getEntityMode() ) );
 			 return this;
 		}
 	}
 
 	public Query setParameter(int position, Object val) throws HibernateException {
 		if (val == null) {
-			setParameter( position, val, Hibernate.SERIALIZABLE );
+			setParameter( position, val, StandardBasicTypes.SERIALIZABLE );
 		}
 		else {
 			setParameter( position, val, determineType( position, val ) );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val) throws HibernateException {
 		if (val == null) {
 			Type type = parameterMetadata.getNamedParameterExpectedType( name );
 			if ( type == null ) {
-				type = Hibernate.SERIALIZABLE;
+				type = StandardBasicTypes.SERIALIZABLE;
 			}
 			setParameter( name, val, type );
 		}
 		else {
 			setParameter( name, val, determineType( name, val ) );
 		}
 		return this;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Class clazz) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( clazz );
 		}
 		return type;
 	}
 
 	private Type guessType(Object param) throws HibernateException {
 		Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy( param );
 		return guessType( clazz );
 	}
 
 	private Type guessType(Class clazz) throws HibernateException {
 		String typename = clazz.getName();
 		Type type = session.getFactory().getTypeResolver().heuristicType(typename);
 		boolean serializable = type!=null && type instanceof SerializableType;
 		if (type==null || serializable) {
 			try {
 				session.getFactory().getEntityPersister( clazz.getName() );
 			}
 			catch (MappingException me) {
 				if (serializable) {
 					return type;
 				}
 				else {
 					throw new HibernateException("Could not determine a type for class: " + typename);
 				}
 			}
-			return Hibernate.entity(clazz);
+			return ( (Session) session ).getTypeHelper().entity( clazz );
 		}
 		else {
 			return type;
 		}
 	}
 
 	public Query setString(int position, String val) {
-		setParameter(position, val, Hibernate.STRING);
+		setParameter(position, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setCharacter(int position, char val) {
-		setParameter(position, new Character(val), Hibernate.CHARACTER);
+		setParameter(position, new Character(val), StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setBoolean(int position, boolean val) {
 		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
-		Type typeToUse = determineType( position, valueToUse, Hibernate.BOOLEAN );
+		Type typeToUse = determineType( position, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( position, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(int position, byte val) {
-		setParameter(position, new Byte(val), Hibernate.BYTE);
+		setParameter(position, new Byte(val), StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setShort(int position, short val) {
-		setParameter(position, new Short(val), Hibernate.SHORT);
+		setParameter(position, new Short(val), StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setInteger(int position, int val) {
-		setParameter(position, new Integer(val), Hibernate.INTEGER);
+		setParameter(position, new Integer(val), StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLong(int position, long val) {
-		setParameter(position, new Long(val), Hibernate.LONG);
+		setParameter(position, new Long(val), StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setFloat(int position, float val) {
-		setParameter(position, new Float(val), Hibernate.FLOAT);
+		setParameter(position, new Float(val), StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setDouble(int position, double val) {
-		setParameter(position, new Double(val), Hibernate.DOUBLE);
+		setParameter(position, new Double(val), StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setBinary(int position, byte[] val) {
-		setParameter(position, val, Hibernate.BINARY);
+		setParameter(position, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(int position, String val) {
-		setParameter(position, val, Hibernate.TEXT);
+		setParameter(position, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setSerializable(int position, Serializable val) {
-		setParameter(position, val, Hibernate.SERIALIZABLE);
+		setParameter(position, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setDate(int position, Date date) {
-		setParameter(position, date, Hibernate.DATE);
+		setParameter(position, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setTime(int position, Date date) {
-		setParameter(position, date, Hibernate.TIME);
+		setParameter(position, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(int position, Date date) {
-		setParameter(position, date, Hibernate.TIMESTAMP);
+		setParameter(position, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setEntity(int position, Object val) {
-		setParameter( position, val, Hibernate.entity( resolveEntityName( val ) ) );
+		setParameter( position, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	private String resolveEntityName(Object val) {
 		if ( val == null ) {
 			throw new IllegalArgumentException( "entity for parameter binding cannot be null" );
 		}
 		return session.bestGuessEntityName( val );
 	}
 
 	public Query setLocale(int position, Locale locale) {
-		setParameter(position, locale, Hibernate.LOCALE);
+		setParameter(position, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(int position, Calendar calendar) {
-		setParameter(position, calendar, Hibernate.CALENDAR);
+		setParameter(position, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(int position, Calendar calendar) {
-		setParameter(position, calendar, Hibernate.CALENDAR_DATE);
+		setParameter(position, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setBinary(String name, byte[] val) {
-		setParameter(name, val, Hibernate.BINARY);
+		setParameter(name, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(String name, String val) {
-		setParameter(name, val, Hibernate.TEXT);
+		setParameter(name, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setBoolean(String name, boolean val) {
 		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
-		Type typeToUse = determineType( name, valueToUse, Hibernate.BOOLEAN );
+		Type typeToUse = determineType( name, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( name, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(String name, byte val) {
-		setParameter(name, new Byte(val), Hibernate.BYTE);
+		setParameter(name, new Byte(val), StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setCharacter(String name, char val) {
-		setParameter(name, new Character(val), Hibernate.CHARACTER);
+		setParameter(name, new Character(val), StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setDate(String name, Date date) {
-		setParameter(name, date, Hibernate.DATE);
+		setParameter(name, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setDouble(String name, double val) {
-		setParameter(name, new Double(val), Hibernate.DOUBLE);
+		setParameter(name, new Double(val), StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setEntity(String name, Object val) {
-		setParameter( name, val, Hibernate.entity( resolveEntityName( val ) ) );
+		setParameter( name, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	public Query setFloat(String name, float val) {
-		setParameter(name, new Float(val), Hibernate.FLOAT);
+		setParameter(name, new Float(val), StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setInteger(String name, int val) {
-		setParameter(name, new Integer(val), Hibernate.INTEGER);
+		setParameter(name, new Integer(val), StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLocale(String name, Locale locale) {
-		setParameter(name, locale, Hibernate.LOCALE);
+		setParameter(name, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(String name, Calendar calendar) {
-		setParameter(name, calendar, Hibernate.CALENDAR);
+		setParameter(name, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(String name, Calendar calendar) {
-		setParameter(name, calendar, Hibernate.CALENDAR_DATE);
+		setParameter(name, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setLong(String name, long val) {
-		setParameter(name, new Long(val), Hibernate.LONG);
+		setParameter(name, new Long(val), StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setSerializable(String name, Serializable val) {
-		setParameter(name, val, Hibernate.SERIALIZABLE);
+		setParameter(name, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setShort(String name, short val) {
-		setParameter(name, new Short(val), Hibernate.SHORT);
+		setParameter(name, new Short(val), StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setString(String name, String val) {
-		setParameter(name, val, Hibernate.STRING);
+		setParameter(name, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setTime(String name, Date date) {
-		setParameter(name, date, Hibernate.TIME);
+		setParameter(name, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(String name, Date date) {
-		setParameter(name, date, Hibernate.TIMESTAMP);
+		setParameter(name, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setBigDecimal(int position, BigDecimal number) {
-		setParameter(position, number, Hibernate.BIG_DECIMAL);
+		setParameter(position, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigDecimal(String name, BigDecimal number) {
-		setParameter(name, number, Hibernate.BIG_DECIMAL);
+		setParameter(name, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigInteger(int position, BigInteger number) {
-		setParameter(position, number, Hibernate.BIG_INTEGER);
+		setParameter(position, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setBigInteger(String name, BigInteger number) {
-		setParameter(name, number, Hibernate.BIG_INTEGER);
+		setParameter(name, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		namedParameterLists.put( name, new TypedValue( type, vals, session.getEntityMode() ) );
 		return this;
 	}
 	
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	protected String expandParameterLists(Map namedParamsCopy) {
 		String query = this.queryString;
 		Iterator iter = namedParameterLists.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			query = expandParameterList( query, (String) me.getKey(), (TypedValue) me.getValue(), namedParamsCopy );
 		}
 		return query;
 	}
 
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	private String expandParameterList(String query, String name, TypedValue typedList, Map namedParamsCopy) {
 		Collection vals = (Collection) typedList.getValue();
 		Type type = typedList.getType();
 
 		boolean isJpaPositionalParam = parameterMetadata.getNamedParameterDescriptor( name ).isJpaStyle();
 		String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
 		String placeholder =
 				new StringBuffer( paramPrefix.length() + name.length() )
 						.append( paramPrefix ).append(  name )
 						.toString();
 
 		if ( query == null ) {
 			return query;
 		}
 		int loc = query.indexOf( placeholder );
 
 		if ( loc < 0 ) {
 			return query;
 		}
 
 		String beforePlaceholder = query.substring( 0, loc );
 		String afterPlaceholder =  query.substring( loc + placeholder.length() );
 
 		// check if placeholder is already immediately enclosed in parentheses
 		// (ignoring whitespace)
 		boolean isEnclosedInParens =
 				StringHelper.getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' &&
 				StringHelper.getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')';
 
 		if ( vals.size() == 1  && isEnclosedInParens ) {
 			// short-circuit for performance when only 1 value and the
 			// placeholder is already enclosed in parentheses...
 			namedParamsCopy.put( name, new TypedValue( type, vals.iterator().next(), session.getEntityMode() ) );
 			return query;
 		}
 
 		StringBuffer list = new StringBuffer( 16 );
 		Iterator iter = vals.iterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			String alias = ( isJpaPositionalParam ? 'x' + name : name ) + i++ + '_';
 			namedParamsCopy.put( alias, new TypedValue( type, iter.next(), session.getEntityMode() ) );
 			list.append( ParserHelper.HQL_VARIABLE_PREFIX ).append( alias );
 			if ( iter.hasNext() ) {
 				list.append( ", " );
 			}
 		}
 		return StringHelper.replace(
 				beforePlaceholder,
 				afterPlaceholder,
 				placeholder.toString(),
 				list.toString(),
 				true,
 				true
 		);
 	}
 
 	public Query setParameterList(String name, Collection vals) throws HibernateException {
 		if ( vals == null ) {
 			throw new QueryException( "Collection must be not null!" );
 		}
 
 		if( vals.size() == 0 ) {
 			setParameterList( name, vals, null );
 		}
 		else {
 			setParameterList(name, vals, determineType( name, vals.iterator().next() ) );
 		}
 
 		return this;
 	}
 
 	public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals), type );
 	}
 
 	public Query setParameterList(String name, Object[] vals) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals) );
 	}
 
 	public Query setProperties(Map map) throws HibernateException {
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 				final Object object = map.get(namedParam);
 				if(object==null) {
 					continue;
 				}
 				Class retType = object.getClass();
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 
 			
 		}
 		return this;				
 	}
 	
 	public Query setProperties(Object bean) throws HibernateException {
 		Class clazz = bean.getClass();
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 			try {
 				Getter getter = ReflectHelper.getGetter( clazz, namedParam );
 				Class retType = getter.getReturnType();
 				final Object object = getter.get( bean );
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 				 	setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 			}
 			catch (PropertyNotFoundException pnfe) {
 				// ignore
 			}
 		}
 		return this;
 	}
 
 	public Query setParameters(Object[] values, Type[] types) {
 		this.values = Arrays.asList(values);
 		this.types = Arrays.asList(types);
 		return this;
 	}
 
 
 	// Execution methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object uniqueResult() throws HibernateException {
 		return uniqueElement( list() );
 	}
 
 	static Object uniqueElement(List list) throws NonUniqueResultException {
 		int size = list.size();
 		if (size==0) return null;
 		Object first = list.get(0);
 		for ( int i=1; i<size; i++ ) {
 			if ( list.get(i)!=first ) {
 				throw new NonUniqueResultException( list.size() );
 			}
 		}
 		return first;
 	}
 
 	protected RowSelection getRowSelection() {
 		return selection;
 	}
 
 	public Type[] typeArray() {
 		return ArrayHelper.toTypeArray( getTypes() );
 	}
 	
 	public Object[] valueArray() {
 		return getValues().toArray();
 	}
 
 	public QueryParameters getQueryParameters(Map namedParams) {
 		return new QueryParameters(
 				typeArray(),
 				valueArray(),
 				namedParams,
 				getLockOptions(),
 				getSelection(),
 				true,
 				isReadOnly(),
 				cacheable,
 				cacheRegion,
 				comment,
 				collectionKey == null ? null : new Serializable[] { collectionKey },
 				optionalObject,
 				optionalEntityName,
 				optionalId,
 				resultTransformer
 		);
 	}
 	
 	protected void before() {
 		if ( flushMode!=null ) {
 			sessionFlushMode = getSession().getFlushMode();
 			getSession().setFlushMode(flushMode);
 		}
 		if ( cacheMode!=null ) {
 			sessionCacheMode = getSession().getCacheMode();
 			getSession().setCacheMode(cacheMode);
 		}
 	}
 	
 	protected void after() {
 		if (sessionFlushMode!=null) {
 			getSession().setFlushMode(sessionFlushMode);
 			sessionFlushMode = null;
 		}
 		if (sessionCacheMode!=null) {
 			getSession().setCacheMode(sessionCacheMode);
 			sessionCacheMode = null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
index 0ac3c78a22..80cf6b4668 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
@@ -1,288 +1,290 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.impl;
+
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;
-import org.hibernate.Hibernate;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.hql.HolderInstantiator;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.Loader;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
 
 /**
  * Implementation of the <tt>ScrollableResults</tt> interface
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractScrollableResults implements ScrollableResults {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractScrollableResults.class.getName());
 
 	private final ResultSet resultSet;
 	private final PreparedStatement ps;
 	private final SessionImplementor session;
 	private final Loader loader;
 	private final QueryParameters queryParameters;
 	private final Type[] types;
 	private HolderInstantiator holderInstantiator;
 
 	public AbstractScrollableResults(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        SessionImplementor sess,
 			Loader loader,
 			QueryParameters queryParameters,
 	        Type[] types,
 	        HolderInstantiator holderInstantiator) throws MappingException {
 		this.resultSet=rs;
 		this.ps=ps;
 		this.session = sess;
 		this.loader = loader;
 		this.queryParameters = queryParameters;
 		this.types = types;
 		this.holderInstantiator = holderInstantiator!=null && holderInstantiator.isRequired()
 		        ? holderInstantiator
 		        : null;
 	}
 
 	protected abstract Object[] getCurrentRow();
 
 	protected ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	protected PreparedStatement getPs() {
 		return ps;
 	}
 
 	protected SessionImplementor getSession() {
 		return session;
 	}
 
 	protected Loader getLoader() {
 		return loader;
 	}
 
 	protected QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	protected Type[] getTypes() {
 		return types;
 	}
 
 	protected HolderInstantiator getHolderInstantiator() {
 		return holderInstantiator;
 	}
 
 	public final void close() throws HibernateException {
 		try {
 			// not absolutely necessary, but does help with aggressive release
 			//session.getJDBCContext().getConnectionManager().closeQueryStatement( ps, resultSet );
 			ps.close();
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not close results"
 				);
 		}
 		finally {
 			try {
 				session.getPersistenceContext().getLoadContexts().cleanup( resultSet );
 			}
 			catch( Throwable ignore ) {
 				// ignore this error for now
                 LOG.trace("Exception trying to cleanup load context : " + ignore.getMessage());
 			}
 		}
 	}
 
 	public final Object[] get() throws HibernateException {
 		return getCurrentRow();
 	}
 
 	public final Object get(int col) throws HibernateException {
 		return getCurrentRow()[col];
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are identical.
 	 *
 	 * @param col the column
 	 * @param returnType a "final" type
 	 */
 	protected final Object getFinal(int col, Type returnType) throws HibernateException {
 		if ( holderInstantiator!=null ) {
 			throw new HibernateException("query specifies a holder class");
 		}
 
 		if ( returnType.getReturnedClass()==types[col].getReturnedClass() ) {
 			return get(col);
 		}
 		else {
 			return throwInvalidColumnTypeException(col, types[col], returnType);
 		}
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are "assignable".
 	 *
 	 * @param col the column
 	 * @param returnType any type
 	 */
 	protected final Object getNonFinal(int col, Type returnType) throws HibernateException {
 		if ( holderInstantiator!=null ) {
 			throw new HibernateException("query specifies a holder class");
 		}
 
 		if ( returnType.getReturnedClass().isAssignableFrom( types[col].getReturnedClass() ) ) {
 			return get(col);
 		}
 		else {
 			return throwInvalidColumnTypeException(col, types[col], returnType);
 		}
 	}
 
 	public final BigDecimal getBigDecimal(int col) throws HibernateException {
-		return (BigDecimal) getFinal(col, Hibernate.BIG_DECIMAL);
+		return (BigDecimal) getFinal(col, StandardBasicTypes.BIG_DECIMAL);
 	}
 
 	public final BigInteger getBigInteger(int col) throws HibernateException {
-		return (BigInteger) getFinal(col, Hibernate.BIG_INTEGER);
+		return (BigInteger) getFinal(col, StandardBasicTypes.BIG_INTEGER);
 	}
 
 	public final byte[] getBinary(int col) throws HibernateException {
-		return (byte[]) getFinal(col, Hibernate.BINARY);
+		return (byte[]) getFinal(col, StandardBasicTypes.BINARY);
 	}
 
 	public final String getText(int col) throws HibernateException {
-		return (String) getFinal(col, Hibernate.TEXT);
+		return (String) getFinal(col, StandardBasicTypes.TEXT);
 	}
 
 	public final Blob getBlob(int col) throws HibernateException {
-		return (Blob) getNonFinal(col, Hibernate.BLOB);
+		return (Blob) getNonFinal(col, StandardBasicTypes.BLOB);
 	}
 
 	public final Clob getClob(int col) throws HibernateException {
-		return (Clob) getNonFinal(col, Hibernate.CLOB);
+		return (Clob) getNonFinal(col, StandardBasicTypes.CLOB);
 	}
 
 	public final Boolean getBoolean(int col) throws HibernateException {
-		return (Boolean) getFinal(col, Hibernate.BOOLEAN);
+		return (Boolean) getFinal(col, StandardBasicTypes.BOOLEAN);
 	}
 
 	public final Byte getByte(int col) throws HibernateException {
-		return (Byte) getFinal(col, Hibernate.BYTE);
+		return (Byte) getFinal(col, StandardBasicTypes.BYTE);
 	}
 
 	public final Character getCharacter(int col) throws HibernateException {
-		return (Character) getFinal(col, Hibernate.CHARACTER);
+		return (Character) getFinal(col, StandardBasicTypes.CHARACTER);
 	}
 
 	public final Date getDate(int col) throws HibernateException {
-		return (Date) getNonFinal(col, Hibernate.TIMESTAMP);
+		return (Date) getNonFinal(col, StandardBasicTypes.TIMESTAMP);
 	}
 
 	public final Calendar getCalendar(int col) throws HibernateException {
-		return (Calendar) getNonFinal(col, Hibernate.CALENDAR);
+		return (Calendar) getNonFinal(col, StandardBasicTypes.CALENDAR);
 	}
 
 	public final Double getDouble(int col) throws HibernateException {
-		return (Double) getFinal(col, Hibernate.DOUBLE);
+		return (Double) getFinal(col, StandardBasicTypes.DOUBLE);
 	}
 
 	public final Float getFloat(int col) throws HibernateException {
-		return (Float) getFinal(col, Hibernate.FLOAT);
+		return (Float) getFinal(col, StandardBasicTypes.FLOAT);
 	}
 
 	public final Integer getInteger(int col) throws HibernateException {
-		return (Integer) getFinal(col, Hibernate.INTEGER);
+		return (Integer) getFinal(col, StandardBasicTypes.INTEGER);
 	}
 
 	public final Long getLong(int col) throws HibernateException {
-		return (Long) getFinal(col, Hibernate.LONG);
+		return (Long) getFinal(col, StandardBasicTypes.LONG);
 	}
 
 	public final Short getShort(int col) throws HibernateException {
-		return (Short) getFinal(col, Hibernate.SHORT);
+		return (Short) getFinal(col, StandardBasicTypes.SHORT);
 	}
 
 	public final String getString(int col) throws HibernateException {
-		return (String) getFinal(col, Hibernate.STRING);
+		return (String) getFinal(col, StandardBasicTypes.STRING);
 	}
 
 	public final Locale getLocale(int col) throws HibernateException {
-		return (Locale) getFinal(col, Hibernate.LOCALE);
+		return (Locale) getFinal(col, StandardBasicTypes.LOCALE);
 	}
 
 	/*public final Currency getCurrency(int col) throws HibernateException {
 		return (Currency) get(col);
 	}*/
 
 	public final TimeZone getTimeZone(int col) throws HibernateException {
-		return (TimeZone) getNonFinal(col, Hibernate.TIMEZONE);
+		return (TimeZone) getNonFinal(col, StandardBasicTypes.TIMEZONE);
 	}
 
 	public final Type getType(int i) {
 		return types[i];
 	}
 
 	private Object throwInvalidColumnTypeException(
 	        int i,
 	        Type type,
 	        Type returnType) throws HibernateException {
 		throw new HibernateException(
 				"incompatible column types: " +
 				type.getName() +
 				", " +
 				returnType.getName()
 		);
 	}
 
 	protected void afterScrollOperation() {
 		session.afterScrollOperation();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
index b0f6caf86e..9a6e0f0b69 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPropertyMapping.java
@@ -1,123 +1,123 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.persister.collection;
-import org.hibernate.Hibernate;
+
 import org.hibernate.QueryException;
 import org.hibernate.persister.entity.PropertyMapping;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public class CollectionPropertyMapping implements PropertyMapping {
 
 	private final QueryableCollection memberPersister;
 
 	public CollectionPropertyMapping(QueryableCollection memberPersister) {
 		this.memberPersister = memberPersister;
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		if ( propertyName.equals(CollectionPropertyNames.COLLECTION_ELEMENTS) ) {
 			return memberPersister.getElementType();
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_INDICES) ) {
 			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection before indices()");
 			return memberPersister.getIndexType();
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_SIZE) ) {
-			return Hibernate.INTEGER;
+			return StandardBasicTypes.INTEGER;
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MAX_INDEX) ) {
 			return memberPersister.getIndexType();
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MIN_INDEX) ) {
 			return memberPersister.getIndexType();
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MAX_ELEMENT) ) {
 			return memberPersister.getElementType();
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MIN_ELEMENT) ) {
 			return memberPersister.getElementType();
 		}
 		else {
 			//return memberPersister.getPropertyType(propertyName);
 			throw new QueryException("illegal syntax near collection: " + propertyName);
 		}
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( propertyName.equals(CollectionPropertyNames.COLLECTION_ELEMENTS) ) {
 			return memberPersister.getElementColumnNames(alias);
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_INDICES) ) {
 			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection in indices()");
 			return memberPersister.getIndexColumnNames(alias);
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_SIZE) ) {
 			String[] cols = memberPersister.getKeyColumnNames();
 			return new String[] { "count(" + alias + '.' + cols[0] + ')' };
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MAX_INDEX) ) {
 			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection in maxIndex()");
 			String[] cols = memberPersister.getIndexColumnNames(alias);
 			if ( cols.length!=1 ) throw new QueryException("composite collection index in maxIndex()");
 			return new String[] { "max(" + cols[0] + ')' };
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MIN_INDEX) ) {
 			if ( !memberPersister.hasIndex() ) throw new QueryException("unindexed collection in minIndex()");
 			String[] cols = memberPersister.getIndexColumnNames(alias);
 			if ( cols.length!=1 ) throw new QueryException("composite collection index in minIndex()");
 			return new String[] { "min(" + cols[0] + ')' };
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MAX_ELEMENT) ) {
 			String[] cols = memberPersister.getElementColumnNames(alias);
 			if ( cols.length!=1 ) throw new QueryException("composite collection element in maxElement()");
 			return new String[] { "max(" + cols[0] + ')' };
 		}
 		else if ( propertyName.equals(CollectionPropertyNames.COLLECTION_MIN_ELEMENT) ) {
 			String[] cols = memberPersister.getElementColumnNames(alias);
 			if ( cols.length!=1 ) throw new QueryException("composite collection element in minElement()");
 			return new String[] { "min(" + cols[0] + ')' };
 		}
 		else {
 			//return memberPersister.toColumns(alias, propertyName);
 			throw new QueryException("illegal syntax near collection: " + propertyName);
 		}
 	}
 
 	/**
 	 * Given a property path, return the corresponding column name(s).
 	 */
 	public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
 		throw new UnsupportedOperationException( "References to collections must be define a SQL alias" );
 	}
 
 	public Type getType() {
 		//return memberPersister.getType();
 		return memberPersister.getCollectionType();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
index 1fef02502d..afeb6008b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
@@ -1,778 +1,779 @@
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
 package org.hibernate.persister.entity;
-import java.io.Serializable;
-import java.util.ArrayList;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.Map;
-import org.hibernate.AssertionFailure;
-import org.hibernate.Hibernate;
-import org.hibernate.HibernateException;
-import org.hibernate.MappingException;
-import org.hibernate.QueryException;
-import org.hibernate.cache.access.EntityRegionAccessStrategy;
-import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.Versioning;
-import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.mapping.Column;
-import org.hibernate.mapping.Join;
-import org.hibernate.mapping.KeyValue;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.mapping.Selectable;
-import org.hibernate.mapping.Subclass;
-import org.hibernate.mapping.Table;
-import org.hibernate.sql.CaseFragment;
-import org.hibernate.sql.SelectFragment;
-import org.hibernate.type.Type;
-
-/**
- * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
- * mapping strategy
- *
- * @author Gavin King
- */
-public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
-
-	// the class hierarchy structure
-	private final int tableSpan;
-	private final String[] tableNames;
-	private final String[] naturalOrderTableNames;
-	private final String[][] tableKeyColumns;
-	private final String[][] tableKeyColumnReaders;
-	private final String[][] tableKeyColumnReaderTemplates;
-	private final String[][] naturalOrderTableKeyColumns;
-	private final String[][] naturalOrderTableKeyColumnReaders;
-	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
-	private final boolean[] naturalOrderCascadeDeleteEnabled;
-
-	private final String[] spaces;
-
-	private final String[] subclassClosure;
-
-	private final String[] subclassTableNameClosure;
-	private final String[][] subclassTableKeyColumnClosure;
-	private final boolean[] isClassOrSuperclassTable;
-
-	// properties of this class, including inherited properties
-	private final int[] naturalOrderPropertyTableNumbers;
-	private final int[] propertyTableNumbers;
-
-	// the closure of all properties in the entire hierarchy including
-	// subclasses and superclasses of this class
-	private final int[] subclassPropertyTableNumberClosure;
-
-	// the closure of all columns used by the entire hierarchy including
-	// subclasses and superclasses of this class
-	private final int[] subclassColumnTableNumberClosure;
-	private final int[] subclassFormulaTableNumberClosure;
-
-	private final boolean[] subclassTableSequentialSelect;
-	private final boolean[] subclassTableIsLazyClosure;
-	
-	// subclass discrimination works by assigning particular
-	// values to certain combinations of null primary key
-	// values in the outer join using an SQL CASE
-	private final Map subclassesByDiscriminatorValue = new HashMap();
-	private final String[] discriminatorValues;
-	private final String[] notNullColumnNames;
-	private final int[] notNullColumnTableNumbers;
-
-	private final String[] constraintOrderedTableNames;
-	private final String[][] constraintOrderedKeyColumnNames;
-
-	private final String discriminatorSQLString;
-
-	//INITIALIZATION:
-
-	public JoinedSubclassEntityPersister(
-			final PersistentClass persistentClass,
-			final EntityRegionAccessStrategy cacheAccessStrategy,
-			final SessionFactoryImplementor factory,
-			final Mapping mapping) throws HibernateException {
-
-		super( persistentClass, cacheAccessStrategy, factory );
-
-		// DISCRIMINATOR
-
-		final Object discriminatorValue;
-		if ( persistentClass.isPolymorphic() ) {
-			try {
-				discriminatorValue = new Integer( persistentClass.getSubclassId() );
-				discriminatorSQLString = discriminatorValue.toString();
-			}
-			catch (Exception e) {
-				throw new MappingException("Could not format discriminator value to SQL string", e );
-			}
-		}
-		else {
-			discriminatorValue = null;
-			discriminatorSQLString = null;
-		}
-
-		if ( optimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION ) {
-			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
-		}
-
-		//MULTITABLES
-
-		final int idColumnSpan = getIdentifierColumnSpan();
-
-		ArrayList tables = new ArrayList();
-		ArrayList keyColumns = new ArrayList();
-		ArrayList keyColumnReaders = new ArrayList();
-		ArrayList keyColumnReaderTemplates = new ArrayList();
-		ArrayList cascadeDeletes = new ArrayList();
-		Iterator titer = persistentClass.getTableClosureIterator();
-		Iterator kiter = persistentClass.getKeyClosureIterator();
-		while ( titer.hasNext() ) {
-			Table tab = (Table) titer.next();
-			KeyValue key = (KeyValue) kiter.next();
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			tables.add(tabname);
-			String[] keyCols = new String[idColumnSpan];
-			String[] keyColReaders = new String[idColumnSpan];
-			String[] keyColReaderTemplates = new String[idColumnSpan];
-			Iterator citer = key.getColumnIterator();
-			for ( int k=0; k<idColumnSpan; k++ ) {
-				Column column = (Column) citer.next();
-				keyCols[k] = column.getQuotedName( factory.getDialect() );
-				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
-				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
-			}
-			keyColumns.add(keyCols);
-			keyColumnReaders.add(keyColReaders);
-			keyColumnReaderTemplates.add(keyColReaderTemplates);
-			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
-		}
-		
-		//Span of the tables directly mapped by this entity and super-classes, if any
-		int coreTableSpan = tables.size();
-		
-		Iterator joinIter = persistentClass.getJoinClosureIterator();
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
-			
-			Table tab = join.getTable();
-			 
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			tables.add(tabname);
-			
-			KeyValue key = join.getKey();
-			int joinIdColumnSpan = 	key.getColumnSpan();		
-			
-			String[] keyCols = new String[joinIdColumnSpan];
-			String[] keyColReaders = new String[joinIdColumnSpan];
-			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
-						
-			Iterator citer = key.getColumnIterator();
-			
-			for ( int k=0; k<joinIdColumnSpan; k++ ) {
-				Column column = (Column) citer.next();
-				keyCols[k] = column.getQuotedName( factory.getDialect() );
-				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
-				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
-			}
-			keyColumns.add(keyCols);
-			keyColumnReaders.add(keyColReaders);
-			keyColumnReaderTemplates.add(keyColReaderTemplates);
-			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
-		}
-		
-		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
-		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray(keyColumns);
-		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray(keyColumnReaders);
-		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray(keyColumnReaderTemplates);
-		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray(cascadeDeletes);
-
-		ArrayList subtables = new ArrayList();
-		ArrayList isConcretes = new ArrayList();
-		ArrayList isDeferreds = new ArrayList();
-		ArrayList isLazies = new ArrayList();
-		
-		keyColumns = new ArrayList();
-		titer = persistentClass.getSubclassTableClosureIterator();
-		while ( titer.hasNext() ) {
-			Table tab = (Table) titer.next();
-			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
-			isDeferreds.add(Boolean.FALSE);
-			isLazies.add(Boolean.FALSE);
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			subtables.add(tabname);
-			String[] key = new String[idColumnSpan];
-			Iterator citer = tab.getPrimaryKey().getColumnIterator();
-			for ( int k=0; k<idColumnSpan; k++ ) {
-				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
-			}
-			keyColumns.add(key);
-		}
-		
-		//Add joins
-		joinIter = persistentClass.getSubclassJoinClosureIterator();
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
-			
-			Table tab = join.getTable();
-			 
-			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
-			isDeferreds.add( new Boolean( join.isSequentialSelect() ) );
-			isLazies.add(new Boolean(join.isLazy()));
-			
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			subtables.add(tabname);
-			String[] key = new String[idColumnSpan];
-			Iterator citer = tab.getPrimaryKey().getColumnIterator();
-			for ( int k=0; k<idColumnSpan; k++ ) {
-				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
-			}
-			keyColumns.add(key);
-						
-		}
-				
-		String [] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray(subtables);
-		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(keyColumns);
-		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
-		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
-		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
-		
-		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
-		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
-		int currentPosition = 0;
-		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0 ; i--, currentPosition++ ) {
-			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
-			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
-		} 
-
-		/**
-		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
-		 * For the Client entity:
-		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
-		 * added to the meta-data when the annotated entities are processed.
-		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
-		 * the first table as it will the driving table.
-		 * tableNames -> CLIENT, PERSON
-		 */
-				
-		tableSpan = naturalOrderTableNames.length;
- 		tableNames = reverse(naturalOrderTableNames, coreTableSpan);
-		tableKeyColumns = reverse(naturalOrderTableKeyColumns, coreTableSpan);
-		tableKeyColumnReaders = reverse(naturalOrderTableKeyColumnReaders, coreTableSpan);
-		tableKeyColumnReaderTemplates = reverse(naturalOrderTableKeyColumnReaderTemplates, coreTableSpan);
-		subclassTableNameClosure = reverse(naturalOrderSubclassTableNameClosure, coreTableSpan);
-		subclassTableKeyColumnClosure = reverse(naturalOrderSubclassTableKeyColumnClosure, coreTableSpan);
- 
-		spaces = ArrayHelper.join(
-				tableNames,
-				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
-		);
-
-		// Custom sql
-		customSQLInsert = new String[tableSpan];
-		customSQLUpdate = new String[tableSpan];
-		customSQLDelete = new String[tableSpan];
-		insertCallable = new boolean[tableSpan];
-		updateCallable = new boolean[tableSpan];
-		deleteCallable = new boolean[tableSpan];
-		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
-		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
-		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
-
-		PersistentClass pc = persistentClass;
-		int jk = coreTableSpan-1;
-		while (pc!=null) {
-			customSQLInsert[jk] = pc.getCustomSQLInsert();
-			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
-			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[jk], insertCallable[jk] )
-		                                  : pc.getCustomSQLInsertCheckStyle();
-			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
-			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
-			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
-		                                  : pc.getCustomSQLUpdateCheckStyle();
-			customSQLDelete[jk] = pc.getCustomSQLDelete();
-			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
-			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
-		                                  : pc.getCustomSQLDeleteCheckStyle();
-			jk--;
-			pc = pc.getSuperclass();
-		}
-		
-		if ( jk != -1 ) {
-			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
-		}
- 
-		joinIter = persistentClass.getJoinClosureIterator();
-		int j = coreTableSpan;
-		while ( joinIter.hasNext() ) {
-			Join join = (Join) joinIter.next();
-			
-			customSQLInsert[j] = join.getCustomSQLInsert();
-			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
-			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
-		                                  : join.getCustomSQLInsertCheckStyle();
-			customSQLUpdate[j] = join.getCustomSQLUpdate();
-			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
-			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
-		                                  : join.getCustomSQLUpdateCheckStyle();
-			customSQLDelete[j] = join.getCustomSQLDelete();
-			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
-			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
-			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
-		                                  : join.getCustomSQLDeleteCheckStyle();
-			j++;
-		}
-		
-		// PROPERTIES
-		int hydrateSpan = getPropertySpan();
-		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
-		propertyTableNumbers = new int[hydrateSpan];
-		Iterator iter = persistentClass.getPropertyClosureIterator();
-		int i=0;
-		while( iter.hasNext() ) {
-			Property prop = (Property) iter.next();
-			String tabname = prop.getValue().getTable().getQualifiedName(
-				factory.getDialect(),
-				factory.getSettings().getDefaultCatalogName(),
-				factory.getSettings().getDefaultSchemaName()
-			);
-			propertyTableNumbers[i] = getTableId(tabname, tableNames);
-			naturalOrderPropertyTableNumbers[i] = getTableId(tabname, naturalOrderTableNames);
-			i++;
-		}
-
-		// subclass closure properties
-
-		//TODO: code duplication with SingleTableEntityPersister
-
-		ArrayList columnTableNumbers = new ArrayList();
-		ArrayList formulaTableNumbers = new ArrayList();
-		ArrayList propTableNumbers = new ArrayList();
-
-		iter = persistentClass.getSubclassPropertyClosureIterator();
-		while ( iter.hasNext() ) {
-			Property prop = (Property) iter.next();
-			Table tab = prop.getValue().getTable();
-			String tabname = tab.getQualifiedName(
-					factory.getDialect(),
-					factory.getSettings().getDefaultCatalogName(),
-					factory.getSettings().getDefaultSchemaName()
-			);
-			Integer tabnum = new Integer( getTableId(tabname, subclassTableNameClosure) );
-  			propTableNumbers.add(tabnum);
-
-			Iterator citer = prop.getColumnIterator();
-			while ( citer.hasNext() ) {
-				Selectable thing = (Selectable) citer.next();
-				if ( thing.isFormula() ) {
-					formulaTableNumbers.add(tabnum);
-				}
-				else {
-					columnTableNumbers.add(tabnum);
-				}
-			}
-
-		}
-
-		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnTableNumbers);
-		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propTableNumbers);
-		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaTableNumbers);
-
-		// SUBCLASSES
- 
-		int subclassSpan = persistentClass.getSubclassSpan() + 1;
-		subclassClosure = new String[subclassSpan];
-		subclassClosure[subclassSpan-1] = getEntityName();
-		if ( persistentClass.isPolymorphic() ) {
-			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
-			discriminatorValues = new String[subclassSpan];
-			discriminatorValues[subclassSpan-1] = discriminatorSQLString;
-			notNullColumnTableNumbers = new int[subclassSpan];
-			final int id = getTableId(
-				persistentClass.getTable().getQualifiedName(
-						factory.getDialect(),
-						factory.getSettings().getDefaultCatalogName(),
-						factory.getSettings().getDefaultSchemaName()
-				),
-				subclassTableNameClosure
-			);
-			notNullColumnTableNumbers[subclassSpan-1] = id;
-			notNullColumnNames = new String[subclassSpan];
-			notNullColumnNames[subclassSpan-1] =  subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
-		}
-		else {
-			discriminatorValues = null;
-			notNullColumnTableNumbers = null;
-			notNullColumnNames = null;
-		}
-
-		iter = persistentClass.getSubclassIterator();
-		int k=0;
-		while ( iter.hasNext() ) {
-			Subclass sc = (Subclass) iter.next();
-			subclassClosure[k] = sc.getEntityName();
-			try {
-				if ( persistentClass.isPolymorphic() ) {
-					// we now use subclass ids that are consistent across all
-					// persisters for a class hierarchy, so that the use of
-					// "foo.class = Bar" works in HQL
-					Integer subclassId = new Integer( sc.getSubclassId() );//new Integer(k+1);
-					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
-					discriminatorValues[k] = subclassId.toString();
-					int id = getTableId(
-						sc.getTable().getQualifiedName(
-								factory.getDialect(),
-								factory.getSettings().getDefaultCatalogName(),
-								factory.getSettings().getDefaultSchemaName()
-						),
-						subclassTableNameClosure
-					);
-					notNullColumnTableNumbers[k] = id;
-					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
-				}
-			}
-			catch (Exception e) {
-				throw new MappingException("Error parsing discriminator value", e );
-			}
-			k++;
-		}
-
-		initLockers();
-
-		initSubclassPropertyAliasesMap(persistentClass);
-
-		postConstruct(mapping);
-
-	}
-
-	protected boolean isSubclassTableSequentialSelect(int j) {
-		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
-	}
-	
-	
-	/*public void postInstantiate() throws MappingException {
-		super.postInstantiate();
-		//TODO: other lock modes?
-		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
-	}*/
-
-	public String getSubclassPropertyTableName(int i) {
-		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
-	}
-
-	public Type getDiscriminatorType() {
-		return Hibernate.INTEGER;
-	}
-
-	public String getDiscriminatorSQLValue() {
-		return discriminatorSQLString;
-	}
-
-
-	public String getSubclassForDiscriminatorValue(Object value) {
-		return (String) subclassesByDiscriminatorValue.get(value);
-	}
-
-	public Serializable[] getPropertySpaces() {
-		return spaces; // don't need subclass tables, because they can't appear in conditions
-	}
-
-
-	protected String getTableName(int j) {
-		return naturalOrderTableNames[j];
-	}
-
-	protected String[] getKeyColumns(int j) {
-		return naturalOrderTableKeyColumns[j];
-	}
-
-	protected boolean isTableCascadeDeleteEnabled(int j) {
-		return naturalOrderCascadeDeleteEnabled[j];
-	}
-
-	protected boolean isPropertyOfTable(int property, int j) {
-		return naturalOrderPropertyTableNumbers[property]==j;
-	}
-
-	/**
-	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
-	 * depending upon the value of the <tt>lock</tt> parameter
-	 */
-	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
-	throws HibernateException {
-
-		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
-
-		final UniqueEntityLoader loader = hasQueryLoader() ?
-				getQueryLoader() :
-				this.loader;
-		try {
-
-			final Object result = loader.load(id, optionalObject, session);
-
-			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
-
-			return result;
-
-		}
-		catch (SQLException sqle) {
-			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
-		}
-	}*/
-
-	private static final void reverse(Object[] objects, int len) {
-		Object[] temp = new Object[len];
-		for (int i=0; i<len; i++) {
-			temp[i] = objects[len-i-1];
-		}
-		for (int i=0; i<len; i++) {
-			objects[i] = temp[i];
-		}
-	}
-
-	
-	/**
-	 * Reverse the first n elements of the incoming array
-	 * @param objects
-	 * @param n
-	 * @return New array with the first n elements in reversed order 
-	 */
-	private static final String[] reverse(String [] objects, int n) {
-		
-		int size = objects.length;
-		String[] temp = new String[size];
-		
-		for (int i=0; i<n; i++) {
-			temp[i] = objects[n-i-1];
-		}
-		
-		for (int i=n; i < size; i++) {
-			temp[i] =  objects[i];
-		}
-		
-		return temp;
-	}
-		
-	/**
-	 * Reverse the first n elements of the incoming array
-	 * @param objects
-	 * @param n
-	 * @return New array with the first n elements in reversed order 
-	 */
-	private static final String[][] reverse(String[][] objects, int n) {
-		int size = objects.length;
-		String[][] temp = new String[size][];
-		for (int i=0; i<n; i++) {
-			temp[i] = objects[n-i-1];
-		}
-		
-		for (int i=n; i<size; i++) {
-			temp[i] = objects[i];
-		}
-		
-		return temp;
-	}
-	
-	
-	
-	public String fromTableFragment(String alias) {
-		return getTableName() + ' ' + alias;
-	}
-
-	public String getTableName() {
-		return tableNames[0];
-	}
-
-	private static int getTableId(String tableName, String[] tables) {
-		for ( int j=0; j<tables.length; j++ ) {
-			if ( tableName.equals( tables[j] ) ) {
-				return j;
-			}
-		}
-		throw new AssertionFailure("Table " + tableName + " not found");
-	}
-
-	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
-		if ( hasSubclasses() ) {
-			select.setExtraSelectList( discriminatorFragment(name), getDiscriminatorAlias() );
-		}
-	}
-
-	private CaseFragment discriminatorFragment(String alias) {
-		CaseFragment cases = getFactory().getDialect().createCaseFragment();
-
-		for ( int i=0; i<discriminatorValues.length; i++ ) {
-			cases.addWhenColumnNotNull(
-				generateTableAlias( alias, notNullColumnTableNumbers[i] ),
-				notNullColumnNames[i],
-				discriminatorValues[i]
-			);
-		}
-
-		return cases;
-	}
-
-	public String filterFragment(String alias) {
-		return hasWhere() ?
-			" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
-			"";
-	}
-
-	public String generateFilterConditionAlias(String rootAlias) {
-		return generateTableAlias( rootAlias, tableSpan-1 );
-	}
-
-	public String[] getIdentifierColumnNames() {
-		return tableKeyColumns[0];
-	}
-
-	public String[] getIdentifierColumnReaderTemplates() {
-		return tableKeyColumnReaderTemplates[0];
-	}
-
-	public String[] getIdentifierColumnReaders() {
-		return tableKeyColumnReaders[0];
-	}		
-	
-	public String[] toColumns(String alias, String propertyName) throws QueryException {
-
-		if ( ENTITY_CLASS.equals(propertyName) ) {
-			// This doesn't actually seem to work but it *might*
-			// work on some dbs. Also it doesn't work if there
-			// are multiple columns of results because it
-			// is not accounting for the suffix:
-			// return new String[] { getDiscriminatorColumnName() };
-
-			return new String[] { discriminatorFragment(alias).toFragmentString() };
-		}
-		else {
-			return super.toColumns(alias, propertyName);
-		}
-
-	}
-
-	protected int[] getPropertyTableNumbersInSelect() {
-		return propertyTableNumbers;
-	}
-
-	protected int getSubclassPropertyTableNumber(int i) {
-		return subclassPropertyTableNumberClosure[i];
-	}
-
-	public int getTableSpan() {
-		return tableSpan;
-	}
-
-	public boolean isMultiTable() {
-		return true;
-	}
-
-	protected int[] getSubclassColumnTableNumberClosure() {
-		return subclassColumnTableNumberClosure;
-	}
-
-	protected int[] getSubclassFormulaTableNumberClosure() {
-		return subclassFormulaTableNumberClosure;
-	}
-
-	protected int[] getPropertyTableNumbers() {
-		return naturalOrderPropertyTableNumbers;
-	}
-
-	protected String[] getSubclassTableKeyColumns(int j) {
-		return subclassTableKeyColumnClosure[j];
-	}
-
-	public String getSubclassTableName(int j) {
-		return subclassTableNameClosure[j];
-	}
-
-	public int getSubclassTableSpan() {
-		return subclassTableNameClosure.length;
-	}
-
-	protected boolean isSubclassTableLazy(int j) {
-		return subclassTableIsLazyClosure[j];
-	}
-	
-	
-	protected boolean isClassOrSuperclassTable(int j) {
-		return isClassOrSuperclassTable[j];
-	}
-
-	public String getPropertyTableName(String propertyName) {
-		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
-		if ( index == null ) {
-			return null;
-		}
-		return tableNames[ propertyTableNumbers[ index.intValue() ] ];
-	}
-
-	public String[] getConstraintOrderedTableNameClosure() {
-		return constraintOrderedTableNames;
-	}
-
-	public String[][] getContraintOrderedTableKeyColumnClosure() {
-		return constraintOrderedKeyColumnNames;
-	}
-
-	public String getRootTableName() {
-		return naturalOrderTableNames[0];
-	}
-
-	public String getRootTableAlias(String drivingAlias) {
-		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
-	}
-
-	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
-		if ( "class".equals( propertyPath ) ) {
-			// special case where we need to force incloude all subclass joins
-			return Declarer.SUBCLASS;
-		}
-		return super.getSubclassPropertyDeclarer( propertyPath );
-	}
-}
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.Map;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.HibernateException;
+import org.hibernate.MappingException;
+import org.hibernate.QueryException;
+import org.hibernate.cache.access.EntityRegionAccessStrategy;
+import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
+import org.hibernate.engine.Mapping;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.Versioning;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.Join;
+import org.hibernate.mapping.KeyValue;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.mapping.Selectable;
+import org.hibernate.mapping.Subclass;
+import org.hibernate.mapping.Table;
+import org.hibernate.sql.CaseFragment;
+import org.hibernate.sql.SelectFragment;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.Type;
+
+/**
+ * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
+ * mapping strategy
+ *
+ * @author Gavin King
+ */
+public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
+
+	// the class hierarchy structure
+	private final int tableSpan;
+	private final String[] tableNames;
+	private final String[] naturalOrderTableNames;
+	private final String[][] tableKeyColumns;
+	private final String[][] tableKeyColumnReaders;
+	private final String[][] tableKeyColumnReaderTemplates;
+	private final String[][] naturalOrderTableKeyColumns;
+	private final String[][] naturalOrderTableKeyColumnReaders;
+	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
+	private final boolean[] naturalOrderCascadeDeleteEnabled;
+
+	private final String[] spaces;
+
+	private final String[] subclassClosure;
+
+	private final String[] subclassTableNameClosure;
+	private final String[][] subclassTableKeyColumnClosure;
+	private final boolean[] isClassOrSuperclassTable;
+
+	// properties of this class, including inherited properties
+	private final int[] naturalOrderPropertyTableNumbers;
+	private final int[] propertyTableNumbers;
+
+	// the closure of all properties in the entire hierarchy including
+	// subclasses and superclasses of this class
+	private final int[] subclassPropertyTableNumberClosure;
+
+	// the closure of all columns used by the entire hierarchy including
+	// subclasses and superclasses of this class
+	private final int[] subclassColumnTableNumberClosure;
+	private final int[] subclassFormulaTableNumberClosure;
+
+	private final boolean[] subclassTableSequentialSelect;
+	private final boolean[] subclassTableIsLazyClosure;
+	
+	// subclass discrimination works by assigning particular
+	// values to certain combinations of null primary key
+	// values in the outer join using an SQL CASE
+	private final Map subclassesByDiscriminatorValue = new HashMap();
+	private final String[] discriminatorValues;
+	private final String[] notNullColumnNames;
+	private final int[] notNullColumnTableNumbers;
+
+	private final String[] constraintOrderedTableNames;
+	private final String[][] constraintOrderedKeyColumnNames;
+
+	private final String discriminatorSQLString;
+
+	//INITIALIZATION:
+
+	public JoinedSubclassEntityPersister(
+			final PersistentClass persistentClass,
+			final EntityRegionAccessStrategy cacheAccessStrategy,
+			final SessionFactoryImplementor factory,
+			final Mapping mapping) throws HibernateException {
+
+		super( persistentClass, cacheAccessStrategy, factory );
+
+		// DISCRIMINATOR
+
+		final Object discriminatorValue;
+		if ( persistentClass.isPolymorphic() ) {
+			try {
+				discriminatorValue = new Integer( persistentClass.getSubclassId() );
+				discriminatorSQLString = discriminatorValue.toString();
+			}
+			catch (Exception e) {
+				throw new MappingException("Could not format discriminator value to SQL string", e );
+			}
+		}
+		else {
+			discriminatorValue = null;
+			discriminatorSQLString = null;
+		}
+
+		if ( optimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION ) {
+			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
+		}
+
+		//MULTITABLES
+
+		final int idColumnSpan = getIdentifierColumnSpan();
+
+		ArrayList tables = new ArrayList();
+		ArrayList keyColumns = new ArrayList();
+		ArrayList keyColumnReaders = new ArrayList();
+		ArrayList keyColumnReaderTemplates = new ArrayList();
+		ArrayList cascadeDeletes = new ArrayList();
+		Iterator titer = persistentClass.getTableClosureIterator();
+		Iterator kiter = persistentClass.getKeyClosureIterator();
+		while ( titer.hasNext() ) {
+			Table tab = (Table) titer.next();
+			KeyValue key = (KeyValue) kiter.next();
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			tables.add(tabname);
+			String[] keyCols = new String[idColumnSpan];
+			String[] keyColReaders = new String[idColumnSpan];
+			String[] keyColReaderTemplates = new String[idColumnSpan];
+			Iterator citer = key.getColumnIterator();
+			for ( int k=0; k<idColumnSpan; k++ ) {
+				Column column = (Column) citer.next();
+				keyCols[k] = column.getQuotedName( factory.getDialect() );
+				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
+				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
+			}
+			keyColumns.add(keyCols);
+			keyColumnReaders.add(keyColReaders);
+			keyColumnReaderTemplates.add(keyColReaderTemplates);
+			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
+		}
+		
+		//Span of the tables directly mapped by this entity and super-classes, if any
+		int coreTableSpan = tables.size();
+		
+		Iterator joinIter = persistentClass.getJoinClosureIterator();
+		while ( joinIter.hasNext() ) {
+			Join join = (Join) joinIter.next();
+			
+			Table tab = join.getTable();
+			 
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			tables.add(tabname);
+			
+			KeyValue key = join.getKey();
+			int joinIdColumnSpan = 	key.getColumnSpan();		
+			
+			String[] keyCols = new String[joinIdColumnSpan];
+			String[] keyColReaders = new String[joinIdColumnSpan];
+			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
+						
+			Iterator citer = key.getColumnIterator();
+			
+			for ( int k=0; k<joinIdColumnSpan; k++ ) {
+				Column column = (Column) citer.next();
+				keyCols[k] = column.getQuotedName( factory.getDialect() );
+				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
+				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
+			}
+			keyColumns.add(keyCols);
+			keyColumnReaders.add(keyColReaders);
+			keyColumnReaderTemplates.add(keyColReaderTemplates);
+			cascadeDeletes.add( new Boolean( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() ) );
+		}
+		
+		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
+		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray(keyColumns);
+		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray(keyColumnReaders);
+		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray(keyColumnReaderTemplates);
+		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray(cascadeDeletes);
+
+		ArrayList subtables = new ArrayList();
+		ArrayList isConcretes = new ArrayList();
+		ArrayList isDeferreds = new ArrayList();
+		ArrayList isLazies = new ArrayList();
+		
+		keyColumns = new ArrayList();
+		titer = persistentClass.getSubclassTableClosureIterator();
+		while ( titer.hasNext() ) {
+			Table tab = (Table) titer.next();
+			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
+			isDeferreds.add(Boolean.FALSE);
+			isLazies.add(Boolean.FALSE);
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			subtables.add(tabname);
+			String[] key = new String[idColumnSpan];
+			Iterator citer = tab.getPrimaryKey().getColumnIterator();
+			for ( int k=0; k<idColumnSpan; k++ ) {
+				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
+			}
+			keyColumns.add(key);
+		}
+		
+		//Add joins
+		joinIter = persistentClass.getSubclassJoinClosureIterator();
+		while ( joinIter.hasNext() ) {
+			Join join = (Join) joinIter.next();
+			
+			Table tab = join.getTable();
+			 
+			isConcretes.add( new Boolean( persistentClass.isClassOrSuperclassTable(tab) ) );
+			isDeferreds.add( new Boolean( join.isSequentialSelect() ) );
+			isLazies.add(new Boolean(join.isLazy()));
+			
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			subtables.add(tabname);
+			String[] key = new String[idColumnSpan];
+			Iterator citer = tab.getPrimaryKey().getColumnIterator();
+			for ( int k=0; k<idColumnSpan; k++ ) {
+				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
+			}
+			keyColumns.add(key);
+						
+		}
+				
+		String [] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray(subtables);
+		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(keyColumns);
+		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
+		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
+		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
+		
+		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
+		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
+		int currentPosition = 0;
+		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0 ; i--, currentPosition++ ) {
+			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
+			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
+		} 
+
+		/**
+		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
+		 * For the Client entity:
+		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
+		 * added to the meta-data when the annotated entities are processed.
+		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
+		 * the first table as it will the driving table.
+		 * tableNames -> CLIENT, PERSON
+		 */
+				
+		tableSpan = naturalOrderTableNames.length;
+ 		tableNames = reverse(naturalOrderTableNames, coreTableSpan);
+		tableKeyColumns = reverse(naturalOrderTableKeyColumns, coreTableSpan);
+		tableKeyColumnReaders = reverse(naturalOrderTableKeyColumnReaders, coreTableSpan);
+		tableKeyColumnReaderTemplates = reverse(naturalOrderTableKeyColumnReaderTemplates, coreTableSpan);
+		subclassTableNameClosure = reverse(naturalOrderSubclassTableNameClosure, coreTableSpan);
+		subclassTableKeyColumnClosure = reverse(naturalOrderSubclassTableKeyColumnClosure, coreTableSpan);
+ 
+		spaces = ArrayHelper.join(
+				tableNames,
+				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
+		);
+
+		// Custom sql
+		customSQLInsert = new String[tableSpan];
+		customSQLUpdate = new String[tableSpan];
+		customSQLDelete = new String[tableSpan];
+		insertCallable = new boolean[tableSpan];
+		updateCallable = new boolean[tableSpan];
+		deleteCallable = new boolean[tableSpan];
+		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
+		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
+		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
+
+		PersistentClass pc = persistentClass;
+		int jk = coreTableSpan-1;
+		while (pc!=null) {
+			customSQLInsert[jk] = pc.getCustomSQLInsert();
+			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
+			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[jk], insertCallable[jk] )
+		                                  : pc.getCustomSQLInsertCheckStyle();
+			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
+			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
+			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
+		                                  : pc.getCustomSQLUpdateCheckStyle();
+			customSQLDelete[jk] = pc.getCustomSQLDelete();
+			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
+			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
+		                                  : pc.getCustomSQLDeleteCheckStyle();
+			jk--;
+			pc = pc.getSuperclass();
+		}
+		
+		if ( jk != -1 ) {
+			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
+		}
+ 
+		joinIter = persistentClass.getJoinClosureIterator();
+		int j = coreTableSpan;
+		while ( joinIter.hasNext() ) {
+			Join join = (Join) joinIter.next();
+			
+			customSQLInsert[j] = join.getCustomSQLInsert();
+			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
+			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
+		                                  : join.getCustomSQLInsertCheckStyle();
+			customSQLUpdate[j] = join.getCustomSQLUpdate();
+			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
+			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
+		                                  : join.getCustomSQLUpdateCheckStyle();
+			customSQLDelete[j] = join.getCustomSQLDelete();
+			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
+			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
+			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
+		                                  : join.getCustomSQLDeleteCheckStyle();
+			j++;
+		}
+		
+		// PROPERTIES
+		int hydrateSpan = getPropertySpan();
+		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
+		propertyTableNumbers = new int[hydrateSpan];
+		Iterator iter = persistentClass.getPropertyClosureIterator();
+		int i=0;
+		while( iter.hasNext() ) {
+			Property prop = (Property) iter.next();
+			String tabname = prop.getValue().getTable().getQualifiedName(
+				factory.getDialect(),
+				factory.getSettings().getDefaultCatalogName(),
+				factory.getSettings().getDefaultSchemaName()
+			);
+			propertyTableNumbers[i] = getTableId(tabname, tableNames);
+			naturalOrderPropertyTableNumbers[i] = getTableId(tabname, naturalOrderTableNames);
+			i++;
+		}
+
+		// subclass closure properties
+
+		//TODO: code duplication with SingleTableEntityPersister
+
+		ArrayList columnTableNumbers = new ArrayList();
+		ArrayList formulaTableNumbers = new ArrayList();
+		ArrayList propTableNumbers = new ArrayList();
+
+		iter = persistentClass.getSubclassPropertyClosureIterator();
+		while ( iter.hasNext() ) {
+			Property prop = (Property) iter.next();
+			Table tab = prop.getValue().getTable();
+			String tabname = tab.getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			Integer tabnum = new Integer( getTableId(tabname, subclassTableNameClosure) );
+  			propTableNumbers.add(tabnum);
+
+			Iterator citer = prop.getColumnIterator();
+			while ( citer.hasNext() ) {
+				Selectable thing = (Selectable) citer.next();
+				if ( thing.isFormula() ) {
+					formulaTableNumbers.add(tabnum);
+				}
+				else {
+					columnTableNumbers.add(tabnum);
+				}
+			}
+
+		}
+
+		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnTableNumbers);
+		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propTableNumbers);
+		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaTableNumbers);
+
+		// SUBCLASSES
+ 
+		int subclassSpan = persistentClass.getSubclassSpan() + 1;
+		subclassClosure = new String[subclassSpan];
+		subclassClosure[subclassSpan-1] = getEntityName();
+		if ( persistentClass.isPolymorphic() ) {
+			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
+			discriminatorValues = new String[subclassSpan];
+			discriminatorValues[subclassSpan-1] = discriminatorSQLString;
+			notNullColumnTableNumbers = new int[subclassSpan];
+			final int id = getTableId(
+				persistentClass.getTable().getQualifiedName(
+						factory.getDialect(),
+						factory.getSettings().getDefaultCatalogName(),
+						factory.getSettings().getDefaultSchemaName()
+				),
+				subclassTableNameClosure
+			);
+			notNullColumnTableNumbers[subclassSpan-1] = id;
+			notNullColumnNames = new String[subclassSpan];
+			notNullColumnNames[subclassSpan-1] =  subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
+		}
+		else {
+			discriminatorValues = null;
+			notNullColumnTableNumbers = null;
+			notNullColumnNames = null;
+		}
+
+		iter = persistentClass.getSubclassIterator();
+		int k=0;
+		while ( iter.hasNext() ) {
+			Subclass sc = (Subclass) iter.next();
+			subclassClosure[k] = sc.getEntityName();
+			try {
+				if ( persistentClass.isPolymorphic() ) {
+					// we now use subclass ids that are consistent across all
+					// persisters for a class hierarchy, so that the use of
+					// "foo.class = Bar" works in HQL
+					Integer subclassId = new Integer( sc.getSubclassId() );//new Integer(k+1);
+					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
+					discriminatorValues[k] = subclassId.toString();
+					int id = getTableId(
+						sc.getTable().getQualifiedName(
+								factory.getDialect(),
+								factory.getSettings().getDefaultCatalogName(),
+								factory.getSettings().getDefaultSchemaName()
+						),
+						subclassTableNameClosure
+					);
+					notNullColumnTableNumbers[k] = id;
+					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
+				}
+			}
+			catch (Exception e) {
+				throw new MappingException("Error parsing discriminator value", e );
+			}
+			k++;
+		}
+
+		initLockers();
+
+		initSubclassPropertyAliasesMap(persistentClass);
+
+		postConstruct(mapping);
+
+	}
+
+	protected boolean isSubclassTableSequentialSelect(int j) {
+		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
+	}
+	
+	
+	/*public void postInstantiate() throws MappingException {
+		super.postInstantiate();
+		//TODO: other lock modes?
+		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
+	}*/
+
+	public String getSubclassPropertyTableName(int i) {
+		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
+	}
+
+	public Type getDiscriminatorType() {
+		return StandardBasicTypes.INTEGER;
+	}
+
+	public String getDiscriminatorSQLValue() {
+		return discriminatorSQLString;
+	}
+
+
+	public String getSubclassForDiscriminatorValue(Object value) {
+		return (String) subclassesByDiscriminatorValue.get(value);
+	}
+
+	public Serializable[] getPropertySpaces() {
+		return spaces; // don't need subclass tables, because they can't appear in conditions
+	}
+
+
+	protected String getTableName(int j) {
+		return naturalOrderTableNames[j];
+	}
+
+	protected String[] getKeyColumns(int j) {
+		return naturalOrderTableKeyColumns[j];
+	}
+
+	protected boolean isTableCascadeDeleteEnabled(int j) {
+		return naturalOrderCascadeDeleteEnabled[j];
+	}
+
+	protected boolean isPropertyOfTable(int property, int j) {
+		return naturalOrderPropertyTableNumbers[property]==j;
+	}
+
+	/**
+	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
+	 * depending upon the value of the <tt>lock</tt> parameter
+	 */
+	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
+	throws HibernateException {
+
+		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
+
+		final UniqueEntityLoader loader = hasQueryLoader() ?
+				getQueryLoader() :
+				this.loader;
+		try {
+
+			final Object result = loader.load(id, optionalObject, session);
+
+			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
+
+			return result;
+
+		}
+		catch (SQLException sqle) {
+			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
+		}
+	}*/
+
+	private static final void reverse(Object[] objects, int len) {
+		Object[] temp = new Object[len];
+		for (int i=0; i<len; i++) {
+			temp[i] = objects[len-i-1];
+		}
+		for (int i=0; i<len; i++) {
+			objects[i] = temp[i];
+		}
+	}
+
+	
+	/**
+	 * Reverse the first n elements of the incoming array
+	 * @param objects
+	 * @param n
+	 * @return New array with the first n elements in reversed order 
+	 */
+	private static final String[] reverse(String [] objects, int n) {
+		
+		int size = objects.length;
+		String[] temp = new String[size];
+		
+		for (int i=0; i<n; i++) {
+			temp[i] = objects[n-i-1];
+		}
+		
+		for (int i=n; i < size; i++) {
+			temp[i] =  objects[i];
+		}
+		
+		return temp;
+	}
+		
+	/**
+	 * Reverse the first n elements of the incoming array
+	 * @param objects
+	 * @param n
+	 * @return New array with the first n elements in reversed order 
+	 */
+	private static final String[][] reverse(String[][] objects, int n) {
+		int size = objects.length;
+		String[][] temp = new String[size][];
+		for (int i=0; i<n; i++) {
+			temp[i] = objects[n-i-1];
+		}
+		
+		for (int i=n; i<size; i++) {
+			temp[i] = objects[i];
+		}
+		
+		return temp;
+	}
+	
+	
+	
+	public String fromTableFragment(String alias) {
+		return getTableName() + ' ' + alias;
+	}
+
+	public String getTableName() {
+		return tableNames[0];
+	}
+
+	private static int getTableId(String tableName, String[] tables) {
+		for ( int j=0; j<tables.length; j++ ) {
+			if ( tableName.equals( tables[j] ) ) {
+				return j;
+			}
+		}
+		throw new AssertionFailure("Table " + tableName + " not found");
+	}
+
+	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
+		if ( hasSubclasses() ) {
+			select.setExtraSelectList( discriminatorFragment(name), getDiscriminatorAlias() );
+		}
+	}
+
+	private CaseFragment discriminatorFragment(String alias) {
+		CaseFragment cases = getFactory().getDialect().createCaseFragment();
+
+		for ( int i=0; i<discriminatorValues.length; i++ ) {
+			cases.addWhenColumnNotNull(
+				generateTableAlias( alias, notNullColumnTableNumbers[i] ),
+				notNullColumnNames[i],
+				discriminatorValues[i]
+			);
+		}
+
+		return cases;
+	}
+
+	public String filterFragment(String alias) {
+		return hasWhere() ?
+			" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
+			"";
+	}
+
+	public String generateFilterConditionAlias(String rootAlias) {
+		return generateTableAlias( rootAlias, tableSpan-1 );
+	}
+
+	public String[] getIdentifierColumnNames() {
+		return tableKeyColumns[0];
+	}
+
+	public String[] getIdentifierColumnReaderTemplates() {
+		return tableKeyColumnReaderTemplates[0];
+	}
+
+	public String[] getIdentifierColumnReaders() {
+		return tableKeyColumnReaders[0];
+	}		
+	
+	public String[] toColumns(String alias, String propertyName) throws QueryException {
+
+		if ( ENTITY_CLASS.equals(propertyName) ) {
+			// This doesn't actually seem to work but it *might*
+			// work on some dbs. Also it doesn't work if there
+			// are multiple columns of results because it
+			// is not accounting for the suffix:
+			// return new String[] { getDiscriminatorColumnName() };
+
+			return new String[] { discriminatorFragment(alias).toFragmentString() };
+		}
+		else {
+			return super.toColumns(alias, propertyName);
+		}
+
+	}
+
+	protected int[] getPropertyTableNumbersInSelect() {
+		return propertyTableNumbers;
+	}
+
+	protected int getSubclassPropertyTableNumber(int i) {
+		return subclassPropertyTableNumberClosure[i];
+	}
+
+	public int getTableSpan() {
+		return tableSpan;
+	}
+
+	public boolean isMultiTable() {
+		return true;
+	}
+
+	protected int[] getSubclassColumnTableNumberClosure() {
+		return subclassColumnTableNumberClosure;
+	}
+
+	protected int[] getSubclassFormulaTableNumberClosure() {
+		return subclassFormulaTableNumberClosure;
+	}
+
+	protected int[] getPropertyTableNumbers() {
+		return naturalOrderPropertyTableNumbers;
+	}
+
+	protected String[] getSubclassTableKeyColumns(int j) {
+		return subclassTableKeyColumnClosure[j];
+	}
+
+	public String getSubclassTableName(int j) {
+		return subclassTableNameClosure[j];
+	}
+
+	public int getSubclassTableSpan() {
+		return subclassTableNameClosure.length;
+	}
+
+	protected boolean isSubclassTableLazy(int j) {
+		return subclassTableIsLazyClosure[j];
+	}
+	
+	
+	protected boolean isClassOrSuperclassTable(int j) {
+		return isClassOrSuperclassTable[j];
+	}
+
+	public String getPropertyTableName(String propertyName) {
+		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
+		if ( index == null ) {
+			return null;
+		}
+		return tableNames[ propertyTableNumbers[ index.intValue() ] ];
+	}
+
+	public String[] getConstraintOrderedTableNameClosure() {
+		return constraintOrderedTableNames;
+	}
+
+	public String[][] getContraintOrderedTableKeyColumnClosure() {
+		return constraintOrderedKeyColumnNames;
+	}
+
+	public String getRootTableName() {
+		return naturalOrderTableNames[0];
+	}
+
+	public String getRootTableAlias(String drivingAlias) {
+		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
+	}
+
+	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
+		if ( "class".equals( propertyPath ) ) {
+			// special case where we need to force incloude all subclass joins
+			return Declarer.SUBCLASS;
+		}
+		return super.getSubclassPropertyDeclarer( propertyPath );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
index 0a624bd12b..b792573e3c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
@@ -1,481 +1,482 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.persister.entity;
+
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
+
 import org.hibernate.AssertionFailure;
-import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the "table-per-concrete-class" or "roll-down" mapping 
  * strategy for an entity and its inheritence hierarchy.
  *
  * @author Gavin King
  */
 public class UnionSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final String subquery;
 	private final String tableName;
 	//private final String rootTableName;
 	private final String[] subclassClosure;
 	private final String[] spaces;
 	private final String[] subclassSpaces;
 	private final String discriminatorSQLValue;
 	private final Map subclassByDiscriminatorValue = new HashMap();
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//INITIALIZATION:
 
 	public UnionSubclassEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 		
 		if ( getIdentifierGenerator() instanceof IdentityGenerator ) {
 			throw new MappingException(
 					"Cannot use identity column key generation with <union-subclass> mapping for: " + 
 					getEntityName() 
 			);
 		}
 
 		// TABLE
 
 		tableName = persistentClass.getTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		/*rootTableName = persistentClass.getRootTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getDefaultCatalog(), 
 				factory.getDefaultSchema() 
 		);*/
 
 		//Custom SQL
 
 		String sql;
 		boolean callable = false;
 		ExecuteUpdateResultCheckStyle checkStyle = null;
 		sql = persistentClass.getCustomSQLInsert();
 		callable = sql != null && persistentClass.isCustomInsertCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLInsertCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLInsert = new String[] { sql };
 		insertCallable = new boolean[] { callable };
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLUpdate();
 		callable = sql != null && persistentClass.isCustomUpdateCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLUpdateCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLUpdate = new String[] { sql };
 		updateCallable = new boolean[] { callable };
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLDelete();
 		callable = sql != null && persistentClass.isCustomDeleteCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLDeleteCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLDeleteCheckStyle();
 		customSQLDelete = new String[] { sql };
 		deleteCallable = new boolean[] { callable };
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		discriminatorSQLValue = String.valueOf( persistentClass.getSubclassId() );
 
 		// PROPERTIES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 
 		// SUBCLASSES
 		subclassByDiscriminatorValue.put( 
 				new Integer( persistentClass.getSubclassId() ), 
 				persistentClass.getEntityName() 
 		);
 		if ( persistentClass.isPolymorphic() ) {
 			Iterator iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				subclassByDiscriminatorValue.put( new Integer( sc.getSubclassId() ), sc.getEntityName() );
 			}
 		}
 		
 		//SPACES
 		//TODO: i'm not sure, but perhaps we should exclude
 		//      abstract denormalized tables?
 		
 		int spacesSize = 1 + persistentClass.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = tableName;
 		Iterator iter = persistentClass.getSynchronizedTables().iterator();
 		for ( int i=1; i<spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 		
 		HashSet subclassTables = new HashSet();
 		iter = persistentClass.getSubclassTableClosureIterator();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			subclassTables.add( table.getQualifiedName(
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 		}
 		subclassSpaces = ArrayHelper.toStringArray(subclassTables);
 
 		subquery = generateSubquery(persistentClass, mapping);
 
 		if ( isMultiTable() ) {
 			int idColumnSpan = getIdentifierColumnSpan();
 			ArrayList tableNames = new ArrayList();
 			ArrayList keyColumns = new ArrayList();
 			if ( !isAbstract() ) {
 				tableNames.add( tableName );
 				keyColumns.add( getIdentifierColumnNames() );
 			}
 			iter = persistentClass.getSubclassTableClosureIterator();
 			while ( iter.hasNext() ) {
 				Table tab = ( Table ) iter.next();
 				if ( !tab.isAbstractUnionTable() ) {
 					String tableName = tab.getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					);
 					tableNames.add( tableName );
 					String[] key = new String[idColumnSpan];
 					Iterator citer = tab.getPrimaryKey().getColumnIterator();
 					for ( int k=0; k<idColumnSpan; k++ ) {
 						key[k] = ( ( Column ) citer.next() ).getQuotedName( factory.getDialect() );
 					}
 					keyColumns.add( key );
 				}
 			}
 
 			constraintOrderedTableNames = ArrayHelper.toStringArray( tableNames );
 			constraintOrderedKeyColumnNames = ArrayHelper.to2DStringArray( keyColumns );
 		}
 		else {
 			constraintOrderedTableNames = new String[] { tableName };
 			constraintOrderedKeyColumnNames = new String[][] { getIdentifierColumnNames() };
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return subclassSpaces;
 	}
 	
 	public String getTableName() {
 		return subquery;
 	}
 
 	public Type getDiscriminatorType() {
-		return Hibernate.INTEGER;
+		return StandardBasicTypes.INTEGER;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassByDiscriminatorValue.get(value);
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	protected boolean isDiscriminatorFormula() {
 		return false;
 	}
 
 	/**
 	 * Generate the SQL that selects a row by id
 	 */
 	protected String generateSelectString(LockMode lockMode) {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 			.setLockMode(lockMode)
 			.setTableName( getTableName() )
 			.addColumns( getIdentifierColumnNames() )
 			.addColumns( 
 					getSubclassColumnClosure(), 
 					getSubclassColumnAliasClosure(),
 					getSubclassColumnLazyiness()
 			)
 			.addColumns( 
 					getSubclassFormulaClosure(), 
 					getSubclassFormulaAliasClosure(),
 					getSubclassFormulaLazyiness()
 			);
 		//TODO: include the rowids!!!!
 		if ( hasSubclasses() ) {
 			if ( isDiscriminatorFormula() ) {
 				select.addColumn( getDiscriminatorFormula(), getDiscriminatorAlias() );
 			}
 			else {
 				select.addColumn( getDiscriminatorColumnName(), getDiscriminatorAlias() );
 			}
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "load " + getEntityName() );
 		}
 		return select.addCondition( getIdentifierColumnNames(), "=?" ).toStatementString();
 	}
 
 	protected String getDiscriminatorFormula() {
 		return null;
 	}
 
 	protected String getTableName(int j) {
 		return tableName;
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return getIdentifierColumnNames();
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return false;
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return true;
 	}
 
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' '  + name;
 	}
 
 	public String filterFragment(String name) {
 		return hasWhere() ?
 			" and " + getSQLWhereString(name) :
 			"";
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return getTableName();//ie. the subquery! yuck!
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return 0;
 	}
 
 	public int getSubclassPropertyTableNumber(String propertyName) {
 		return 0;
 	}
 
 	public boolean isMultiTable() {
 		// This could also just be true all the time...
 		return isAbstract() || hasSubclasses();
 	}
 
 	public int getTableSpan() {
 		return 1;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return new int[ getSubclassColumnClosure().length ];
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return new int[ getSubclassFormulaClosure().length ];
 	}
 
 	protected boolean[] getTableHasColumns() {
 		return new boolean[] { true };
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected String generateSubquery(PersistentClass model, Mapping mapping) {
 
 		Dialect dialect = getFactory().getDialect();
 		Settings settings = getFactory().getSettings();
 		
 		if ( !model.hasSubclasses() ) {
 			return model.getTable().getQualifiedName(
 					dialect,
 					settings.getDefaultCatalogName(),
 					settings.getDefaultSchemaName()
 				);
 		}
 
 		HashSet columns = new LinkedHashSet();
 		Iterator titer = model.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table table = (Table) titer.next();
 			if ( !table.isAbstractUnionTable() ) {
 				Iterator citer = table.getColumnIterator();
 				while ( citer.hasNext() ) columns.add( citer.next() );
 			}
 		}
 
 		StringBuffer buf = new StringBuffer()
 			.append("( ");
 
 		Iterator siter = new JoinedIterator(
 			new SingletonIterator(model),
 			model.getSubclassIterator()
 		);
 
 		while ( siter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) siter.next();
 			Table table = clazz.getTable();
 			if ( !table.isAbstractUnionTable() ) {
 				//TODO: move to .sql package!!
 				buf.append("select ");
 				Iterator citer = columns.iterator();
 				while ( citer.hasNext() ) {
 					Column col = (Column) citer.next();
 					if ( !table.containsColumn(col) ) {
 						int sqlType = col.getSqlTypeCode(mapping);
 						buf.append( dialect.getSelectClauseNullString(sqlType) )
 							.append(" as ");
 					}
 					buf.append( col.getName() );
 					buf.append(", ");
 				}
 				buf.append( clazz.getSubclassId() )
 					.append(" as clazz_");
 				buf.append(" from ")
 					.append( table.getQualifiedName(
 							dialect,
 							settings.getDefaultCatalogName(),
 							settings.getDefaultSchemaName()
 					) );
 				buf.append(" union ");
 				if ( dialect.supportsUnionAll() ) {
 					buf.append("all ");
 				}
 			}
 		}
 		
 		if ( buf.length() > 2 ) {
 			//chop the last union (all)
 			buf.setLength( buf.length() - ( dialect.supportsUnionAll() ? 11 : 7 ) );
 		}
 
 		return buf.append(" )").toString();
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return getIdentifierColumnNames();
 	}
 
 	public String getSubclassTableName(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return tableName;
 	}
 
 	public int getSubclassTableSpan() {
 		return 1;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return true;
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		//TODO: check this....
 		return getTableName();
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 			return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index c29e7942e0..21ec971aa3 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,402 +1,405 @@
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
 package org.hibernate.type;
+
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Map;
+
 import org.dom4j.Node;
+
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
-import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxyHelper;
 
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
 	private final Type identifierType;
 	private final Type metaType;
 
 	public AnyType(Type metaType, Type identifierType) {
 		this.identifierType = identifierType;
 		this.metaType = metaType;
 	}
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
 	throws HibernateException {
 		return value;
 	}
 	
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
 		return x==y;
 	}
 
 	public int compare(Object x, Object y, EntityMode entityMode) {
 		return 0; //TODO: entities CAN be compared, by PK and entity name, fix this!
 	}
 
 	public int getColumnSpan(Mapping session)
 	throws MappingException {
 		return 2;
 	}
 
 	public String getName() {
 		return "object";
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 
 		throw new UnsupportedOperationException("object is a multicolumn type");
 	}
 
 	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 	throws HibernateException, SQLException {
 		return resolveAny(
 				(String) metaType.nullSafeGet(rs, names[0], session, owner),
 				(Serializable) identifierType.nullSafeGet(rs, names[1], session, owner),
 				session
 			);
 	}
 
 	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 	throws HibernateException, SQLException {
 		String entityName = (String) metaType.nullSafeGet(rs, names[0], session, owner);
 		Serializable id = (Serializable) identifierType.nullSafeGet(rs, names[1], session, owner);
 		return new ObjectTypeCacheEntry(entityName, id);
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
 		return resolveAny(holder.entityName, holder.id, session);
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		throw new UnsupportedOperationException("any mappings may not form part of a property-ref");
 	}
 	
 	private Object resolveAny(String entityName, Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return entityName==null || id==null ?
 				null : session.internalLoad( entityName, id, false, false );
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
 	throws HibernateException, SQLException {
 		nullSafeSet(st, value, index, null, session);
 	}
 	
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Serializable id;
 		String entityName;
 		if (value==null) {
 			id=null;
 			entityName=null;
 		}
 		else {
 			entityName = session.bestGuessEntityName(value);
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, value, session);
 		}
 		
 		// metaType is assumed to be single-column type
 		if ( settable==null || settable[0] ) {
 			metaType.nullSafeSet(st, entityName, index, session);
 		}
 		if (settable==null) {
 			identifierType.nullSafeSet(st, id, index+1, session);
 		}
 		else {
 			boolean[] idsettable = new boolean[ settable.length-1 ];
 			System.arraycopy(settable, 1, idsettable, 0, idsettable.length);
 			identifierType.nullSafeSet(st, id, index+1, idsettable, session);
 		}
 	}
 
 	public Class getReturnedClass() {
 		return Object.class;
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join(
 				metaType.sqlTypes( mapping ),
 				identifierType.sqlTypes( mapping )
 		);
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join(
 				metaType.dictatedSizes( mapping ),
 				identifierType.dictatedSizes( mapping )
 		);
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join(
 				metaType.defaultSizes( mapping ),
 				identifierType.defaultSizes( mapping )
 		);
 	}
 
 	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types cannot be stringified");
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		//TODO: terrible implementation!
-		return value==null ?
-				"null" :
-				Hibernate.entity( HibernateProxyHelper.getClassWithoutInitializingProxy(value) )
-						.toLoggableString(value, factory);
+		return value == null
+				? "null"
+				: factory.getTypeHelper()
+						.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
+						.toLoggableString( value, factory );
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		throw new UnsupportedOperationException(); //TODO: is this right??
 	}
 
 	public static final class ObjectTypeCacheEntry implements Serializable {
 		String entityName;
 		Serializable id;
 		ObjectTypeCacheEntry(String entityName, Serializable id) {
 			this.entityName = entityName;
 			this.id = id;
 		}
 	}
 
 	public Object assemble(
 		Serializable cached,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException {
 
 		ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
 		return e==null ? null : session.internalLoad(e.entityName, e.id, false, false);
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return value==null ?
 			null :
 			new ObjectTypeCacheEntry(
 						session.bestGuessEntityName(value),
 						ForeignKeys.getEntityIdentifierIfNotUnsaved( 
 								session.bestGuessEntityName(value), value, session 
 							)
 					);
 	}
 
 	public boolean isAnyType() {
 		return true;
 	}
 
 	public Object replace(
 			Object original, 
 			Object target,
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache)
 	throws HibernateException {
 		if (original==null) {
 			return null;
 		}
 		else {
 			String entityName = session.bestGuessEntityName(original);
 			Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( 
 					entityName, 
 					original, 
 					session 
 				);
 			return session.internalLoad( 
 					entityName, 
 					id, 
 					false, 
 					false
 				);
 		}
 	}
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyle.NONE;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.SELECT;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 		throws HibernateException {
 
 		return i==0 ?
 				session.bestGuessEntityName(component) :
 				getIdentifier(component, session);
 	}
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 		throws HibernateException {
 
 		return new Object[] { session.bestGuessEntityName(component), getIdentifier(component, session) };
 	}
 
 	private Serializable getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		try {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved( session.bestGuessEntityName(value), value, session );
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
 	public Type[] getSubtypes() {
 		return new Type[] { metaType, identifierType };
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 		throws HibernateException {
 
 		throw new UnsupportedOperationException();
 
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	public boolean isComponentType() {
 		return true;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		//return AssociationType.FOREIGN_KEY_TO_PARENT; //this is better but causes a transient object exception...
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 	throws HibernateException {
 		if (current==null) return old!=null;
 		if (old==null) return current!=null;
 		ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
 		boolean[] idcheckable = new boolean[checkable.length-1];
 		System.arraycopy(checkable, 1, idcheckable, 0, idcheckable.length);
 		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName(current) ) ) ||
 				identifierType.isModified(holder.id, getIdentifier(current, session), idcheckable, session);
 	}
 
 	public String getAssociatedEntityName(SessionFactoryImplementor factory)
 		throws MappingException {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 	
 	public boolean[] getPropertyNullability() {
 		return null;
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 	throws MappingException {
 		throw new UnsupportedOperationException();
 	}
 	
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
 	
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
 	public boolean isEmbeddedInXML() {
 		return false;
 	}
 	
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan(mapping) ];
 		if (value!=null) Arrays.fill(result, true);
 		return result;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) 
 	throws HibernateException {
 		//TODO!!!
 		return isDirty(old, current, session);
 	}
 
 	public boolean isEmbedded() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
index 1b2577ad35..e2bf612368 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
@@ -1,324 +1,328 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.ParameterizedType;
 import org.hibernate.usertype.UserType;
 
 /**
  * Used internally to build instances of {@link Type}, specifically it builds instances of
  *
  *
  * Used internally to obtain instances of <tt>Type</tt>. Applications should use static methods
  * and constants on <tt>org.hibernate.Hibernate</tt>.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "unchecked" })
 public final class TypeFactory implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TypeFactory.class.getName());
 
 	private final TypeScopeImpl typeScope = new TypeScopeImpl();
 
 	public static interface TypeScope extends Serializable {
 		public SessionFactoryImplementor resolveFactory();
 	}
 
 	private static class TypeScopeImpl implements TypeFactory.TypeScope {
 		private SessionFactoryImplementor factory;
 
 		public void injectSessionFactory(SessionFactoryImplementor factory) {
-            if (this.factory != null) LOG.scopingTypesToSessionFactoryAfterAlreadyScoped(this.factory, factory);
-            else LOG.trace("Scoping types to session factory " + factory);
+            if (this.factory != null) {
+				LOG.scopingTypesToSessionFactoryAfterAlreadyScoped( this.factory, factory );
+			}
+            else {
+				LOG.trace( "Scoping types to session factory " + factory );
+			}
 			this.factory = factory;
 		}
 
 		public SessionFactoryImplementor resolveFactory() {
 			if ( factory == null ) {
 				throw new HibernateException( "SessionFactory for type scoping not yet known" );
 			}
 			return factory;
 		}
 	}
 
 	public void injectSessionFactory(SessionFactoryImplementor factory) {
 		typeScope.injectSessionFactory( factory );
 	}
 
 	public SessionFactoryImplementor resolveSessionFactory() {
 		return typeScope.resolveFactory();
 	}
 
 	public Type byClass(Class clazz, Properties parameters) {
 		if ( Type.class.isAssignableFrom( clazz ) ) {
 			return type( clazz, parameters );
 		}
 
 		if ( CompositeUserType.class.isAssignableFrom( clazz ) ) {
 			return customComponent( clazz, parameters );
 		}
 
 		if ( UserType.class.isAssignableFrom( clazz ) ) {
 			return custom( clazz, parameters );
 		}
 
 		if ( Lifecycle.class.isAssignableFrom( clazz ) ) {
 			// not really a many-to-one association *necessarily*
 			return manyToOne( clazz.getName() );
 		}
 
 		if ( Serializable.class.isAssignableFrom( clazz ) ) {
 			return serializable( clazz );
 		}
 
 		return null;
 	}
 
 	public Type type(Class<Type> typeClass, Properties parameters) {
 		try {
 			Type type = typeClass.newInstance();
 			injectParameters( type, parameters );
 			return type;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not instantiate Type: " + typeClass.getName(), e );
 		}
 	}
 
 	public static void injectParameters(Object type, Properties parameters) {
 		if ( ParameterizedType.class.isInstance( type ) ) {
 			( (ParameterizedType) type ).setParameterValues(parameters);
 		}
 		else if ( parameters!=null && !parameters.isEmpty() ) {
 			throw new MappingException( "type is not parameterized: " + type.getClass().getName() );
 		}
 	}
 
 	public CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters) {
 		return customComponent( typeClass, parameters, typeScope );
 	}
 
 	/**
 	 * @deprecated Only for use temporary use by {@link org.hibernate.Hibernate}
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public static CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters, TypeScope scope) {
 		try {
 			CompositeUserType userType = typeClass.newInstance();
 			injectParameters( userType, parameters );
 			return new CompositeCustomType( userType );
 		}
 		catch ( Exception e ) {
 			throw new MappingException( "Unable to instantiate custom type: " + typeClass.getName(), e );
 		}
 	}
 
 	public CollectionType customCollection(
 			String typeName,
 			Properties typeParameters,
 			String role,
 			String propertyRef,
 			boolean embedded) {
 		Class typeClass;
 		try {
 			typeClass = ReflectHelper.classForName( typeName );
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "user collection type class not found: " + typeName, cnfe );
 		}
 		CustomCollectionType result = new CustomCollectionType( typeScope, typeClass, role, propertyRef, embedded );
 		if ( typeParameters != null ) {
 			injectParameters( result.getUserType(), typeParameters );
 		}
 		return result;
 	}
 
 	public CustomType custom(Class<UserType> typeClass, Properties parameters) {
 		return custom( typeClass, parameters, typeScope );
 	}
 
 	/**
 	 * @deprecated Only for use temporary use by {@link org.hibernate.Hibernate}
 	 */
 	@Deprecated
     public static CustomType custom(Class<UserType> typeClass, Properties parameters, TypeScope scope) {
 		try {
 			UserType userType = typeClass.newInstance();
 			injectParameters( userType, parameters );
 			return new CustomType( userType );
 		}
 		catch ( Exception e ) {
 			throw new MappingException( "Unable to instantiate custom type: " + typeClass.getName(), e );
 		}
 	}
 
 	/**
 	 * Build a {@link SerializableType} from the given {@link Serializable} class.
 	 *
 	 * @param serializableClass The {@link Serializable} class.
 	 * @param <T> The actual class type (extends Serializable)
 	 *
 	 * @return The built {@link SerializableType}
 	 */
 	public static <T extends Serializable> SerializableType<T> serializable(Class<T> serializableClass) {
 		return new SerializableType<T>( serializableClass );
 	}
 
 
 	// one-to-one type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public EntityType oneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			String entityName,
 			String propertyName) {
 		return new OneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
 				lazy, unwrapProxy, isEmbeddedInXML, entityName, propertyName );
 	}
 
 	public EntityType specialOneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		return new SpecialOneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
 				lazy, unwrapProxy, entityName, propertyName );
 	}
 
 
 	// many-to-one type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public EntityType manyToOne(String persistentClass) {
 		return new ManyToOneType( typeScope, persistentClass );
 	}
 
 	public EntityType manyToOne(String persistentClass, boolean lazy) {
 		return new ManyToOneType( typeScope, persistentClass, lazy );
 	}
 
 	public EntityType manyToOne(
 			String persistentClass,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		return new ManyToOneType(
 				typeScope,
 				persistentClass,
 				uniqueKeyPropertyName,
 				lazy,
 				unwrapProxy,
 				isEmbeddedInXML,
 				ignoreNotFound,
 				isLogicalOneToOne
 		);
 	}
 
 
 	// collection type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public CollectionType array(String role, String propertyRef, boolean embedded, Class elementClass) {
 		return new ArrayType( typeScope, role, propertyRef, elementClass, embedded );
 	}
 
 	public CollectionType list(String role, String propertyRef, boolean embedded) {
 		return new ListType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType bag(String role, String propertyRef, boolean embedded) {
 		return new BagType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType idbag(String role, String propertyRef, boolean embedded) {
 		return new IdentifierBagType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType map(String role, String propertyRef, boolean embedded) {
 		return new MapType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType orderedMap(String role, String propertyRef, boolean embedded) {
 		return new OrderedMapType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType sortedMap(String role, String propertyRef, boolean embedded, Comparator comparator) {
 		return new SortedMapType( typeScope, role, propertyRef, comparator, embedded );
 	}
 
 	public CollectionType set(String role, String propertyRef, boolean embedded) {
 		return new SetType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType orderedSet(String role, String propertyRef, boolean embedded) {
 		return new OrderedSetType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType sortedSet(String role, String propertyRef, boolean embedded, Comparator comparator) {
 		return new SortedSetType( typeScope, role, propertyRef, comparator, embedded );
 	}
 
 
 	// component type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public ComponentType component(ComponentMetamodel metamodel) {
 		return new ComponentType( typeScope, metamodel );
 	}
 
 	public EmbeddedComponentType embeddedComponent(ComponentMetamodel metamodel) {
 		return new EmbeddedComponentType( typeScope, metamodel );
 	}
 
 
 	// any type builder ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Type any(Type metaType, Type identifierType) {
 		return new AnyType( metaType, identifierType );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
index 5cac2329da..5153f17e03 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
@@ -1,174 +1,174 @@
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
 package org.hibernate.id;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
-import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.impl.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
  * unit test so that we can make sure the value keep being generated in the expected manner
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "deprecation" })
 public class SequenceHiLoGeneratorNoIncrementTest extends BaseUnitTestCase {
 	private static final String TEST_SEQUENCE = "test_sequence";
 
 	private Configuration cfg;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 	private SequenceHiLoGenerator generator;
 
 	@Before
 	public void setUp() throws Exception {
 		Properties properties = new Properties();
 		properties.setProperty( SequenceGenerator.SEQUENCE, TEST_SEQUENCE );
 		properties.setProperty( SequenceHiLoGenerator.MAX_LO, "0" ); // JPA allocationSize of 1
 		properties.put(
 				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 				new ObjectNameNormalizer() {
 					@Override
 					protected boolean isUseQuotedIdentifiersGlobally() {
 						return false;
 					}
 
 					@Override
 					protected NamingStrategy getNamingStrategy() {
 						return cfg.getNamingStrategy();
 					}
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
 		generator = new SequenceHiLoGenerator();
-		generator.configure( Hibernate.LONG, properties, dialect );
+		generator.configure( StandardBasicTypes.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration()
 				.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[0],
 						generator.sqlDropStrings( dialect )[0]
 				)
 		);
 
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	@Test
 	public void testHiLoAlgorithm() {
 		SessionImpl session = (SessionImpl) sessionFactory.openSession();
 		session.beginTransaction();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// initially sequence should be uninitialized
 		assertEquals( 0L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// historically the hilo generators skipped the initial block of values;
 		// 		so the first generated id value is maxlo + 1, here be 4
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 1L, generatedValue.longValue() );
 		// which should also perform the first read on the sequence which should set it to its "start with" value (1)
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 2L, generatedValue.longValue() );
 		assertEquals( 2L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 3L, generatedValue.longValue() );
 		assertEquals( 3L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		assertEquals( 4L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 5L, extractSequenceValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractSequenceValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
index a0e9885080..b6d7077dbf 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
@@ -1,176 +1,176 @@
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
 package org.hibernate.id;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
-import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.impl.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
  * unit test so that we can make sure the value keep being generated in the expected manner
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "deprecation" })
 public class SequenceHiLoGeneratorTest extends BaseUnitTestCase {
 	private static final String TEST_SEQUENCE = "test_sequence";
 
 	private Configuration cfg;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 	private SequenceHiLoGenerator generator;
 
 	@Before
 	public void setUp() throws Exception {
 		Properties properties = new Properties();
 		properties.setProperty( SequenceGenerator.SEQUENCE, TEST_SEQUENCE );
 		properties.setProperty( SequenceHiLoGenerator.MAX_LO, "3" );
 		properties.put(
 				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 				new ObjectNameNormalizer() {
 					@Override
 					protected boolean isUseQuotedIdentifiersGlobally() {
 						return false;
 					}
 
 					@Override
 					protected NamingStrategy getNamingStrategy() {
 						return cfg.getNamingStrategy();
 					}
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
 		generator = new SequenceHiLoGenerator();
-		generator.configure( Hibernate.LONG, properties, dialect );
+		generator.configure( StandardBasicTypes.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration()
 				.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[0],
 						generator.sqlDropStrings( dialect )[0]
 				)
 		);
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	@Test
 	public void testHiLoAlgorithm() {
 		SessionImpl session = (SessionImpl) sessionFactory.openSession();
 		session.beginTransaction();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// initially sequence should be uninitialized
 		assertEquals( 0L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// historically the hilo generators skipped the initial block of values;
 		// 		so the first generated id value is maxlo + 1, here be 4
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		// which should also perform the first read on the sequence which should set it to its "start with" value (1)
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 6L, generatedValue.longValue() );
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 7L, generatedValue.longValue() );
 		// unlike the newer strategies, the db value will not get update here.  It gets updated on the next invocation
 		// 	after a clock over
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 8L, generatedValue.longValue() );
 		// this should force an increment in the sequence value
 		assertEquals( 2L, extractSequenceValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractSequenceValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
index e45b00821c..be46e0d4b5 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
@@ -1,180 +1,180 @@
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
 package org.hibernate.id;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
-import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.impl.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
  * unit test so that we can make sure the value keep being generated in the expected manner
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "deprecation" })
 public class TableHiLoGeneratorTest extends BaseUnitTestCase {
 	private static final String GEN_TABLE = "generator_table";
 	private static final String GEN_COLUMN = TableHiLoGenerator.DEFAULT_COLUMN_NAME;
 
 	private Configuration cfg;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 	private TableHiLoGenerator generator;
 
 	@Before
 	public void setUp() throws Exception {
 		Properties properties = new Properties();
 		properties.setProperty( TableGenerator.TABLE, GEN_TABLE );
 		properties.setProperty( TableGenerator.COLUMN, GEN_COLUMN );
 		properties.setProperty( TableHiLoGenerator.MAX_LO, "3" );
 		properties.put(
 				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 				new ObjectNameNormalizer() {
 					@Override
 					protected boolean isUseQuotedIdentifiersGlobally() {
 						return false;
 					}
 
 					@Override
 					protected NamingStrategy getNamingStrategy() {
 						return cfg.getNamingStrategy();
 					}
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
 		generator = new TableHiLoGenerator();
-		generator.configure( Hibernate.LONG, properties, dialect );
+		generator.configure( StandardBasicTypes.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration()
 				.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[0],
 						generator.sqlDropStrings( dialect )[0]
 				)
 		);
 
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[1],
 						null
 				)
 		);
 
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	@Test
 	public void testHiLoAlgorithm() {
 		SessionImpl session = (SessionImpl) sessionFactory.openSession();
 		session.beginTransaction();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// initially sequence should be uninitialized
 		assertEquals( 0L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 1L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 2L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 3L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		assertEquals( 2L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 2L, extractInDatabaseValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractInDatabaseValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select " + GEN_COLUMN + " from " + GEN_TABLE );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java b/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
index e77a6e9859..a68ff165f8 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
@@ -1,248 +1,249 @@
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
-package org.hibernate.id.enhanced;
-
-import java.util.Properties;
-
-import org.hibernate.Hibernate;
-import org.hibernate.MappingException;
-import org.hibernate.cfg.Environment;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.cfg.ObjectNameNormalizer;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.id.PersistentIdentifierGenerator;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.fail;
-
-/**
- * Tests that SequenceStyleGenerator configures itself as expected in various scenarios
- *
- * @author Steve Ebersole
- */
-@SuppressWarnings({ "deprecation" })
-public class SequenceStyleConfigUnitTest extends BaseUnitTestCase {
-	private void assertClassAssignability(Class expected, Class actual) {
-		if ( ! expected.isAssignableFrom( actual ) ) {
-			fail( "Actual type [" + actual.getName() + "] is not assignable to expected type [" + expected.getName() + "]" );
-		}
-	}
-
-
-	/**
-	 * Test all params defaulted with a dialect supporting sequences
-	 */
-	@Test
-	public void testDefaultedSequenceBackedConfiguration() {
-		Dialect dialect = new SequenceDialect();
-		Properties props = buildGeneratorPropertiesBase();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	private Properties buildGeneratorPropertiesBase() {
-		Properties props = new Properties();
-		props.put(
-				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
-				new ObjectNameNormalizer() {
-					protected boolean isUseQuotedIdentifiersGlobally() {
-						return false;
-					}
-
-					protected NamingStrategy getNamingStrategy() {
-						return null;
-					}
-				}
-		);
-		return props;
-	}
-
-	/**
-	 * Test all params defaulted with a dialect which does not support sequences
-	 */
-	@Test
-	public void testDefaultedTableBackedConfiguration() {
-		Dialect dialect = new TableDialect();
-		Properties props = buildGeneratorPropertiesBase();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test default optimizer selection for sequence backed generators
-	 * based on the configured increment size; both in the case of the
-	 * dialect supporting pooled sequences (pooled) and not (hilo)
-	 */
-	@Test
-	public void testDefaultOptimizerBasedOnIncrementBackedBySequence() {
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
-
-		// for dialects which do not support pooled sequences, we default to pooled+table
-		Dialect dialect = new SequenceDialect();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-
-		// for dialects which do support pooled sequences, we default to pooled+sequence
-		dialect = new PooledSequenceDialect();
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test default optimizer selection for table backed generators
-	 * based on the configured increment size.  Here we always prefer
-	 * pooled.
-	 */
-	@Test
-	public void testDefaultOptimizerBasedOnIncrementBackedByTable() {
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
-		Dialect dialect = new TableDialect();
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test forcing of table as backing strucuture with dialect supporting sequences
-	 */
-	@Test
-	public void testForceTableUse() {
-		Dialect dialect = new SequenceDialect();
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.FORCE_TBL_PARAM, "true" );
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
-	}
-
-	/**
-	 * Test explicitly specifying both optimizer and increment
-	 */
-	@Test
-	public void testExplicitOptimizerWithExplicitIncrementSize() {
-		// with sequence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		final Dialect dialect = new SequenceDialect();
-
-		// optimizer=none w/ increment > 1 => should honor optimizer
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.NONE );
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( 1, generator.getOptimizer().getIncrementSize() );
-		assertEquals( 1, generator.getDatabaseStructure().getIncrementSize() );
-
-		// optimizer=hilo w/ increment > 1 => hilo
-		props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.HILO );
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.HiLoOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
-		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
-
-		// optimizer=pooled w/ increment > 1 => hilo
-		props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.POOL );
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		// because the dialect reports to not support pooled seqyences, the expectation is that we will
-		// use a table for the backing structure...
-		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
-		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
-	}
-
-	@Test
-	public void testPreferPooledLoSettingHonored() {
-		final Dialect dialect = new PooledSequenceDialect();
-
-		Properties props = buildGeneratorPropertiesBase();
-		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
-		SequenceStyleGenerator generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
-
-		props.setProperty( Environment.PREFER_POOLED_VALUES_LO, "true" );
-		generator = new SequenceStyleGenerator();
-		generator.configure( Hibernate.LONG, props, dialect );
-		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
-		assertClassAssignability( OptimizerFactory.PooledLoOptimizer.class, generator.getOptimizer().getClass() );
-	}
-
-	private static class TableDialect extends Dialect {
-		public boolean supportsSequences() {
-			return false;
-		}
-	}
-
-	private static class SequenceDialect extends Dialect {
-		public boolean supportsSequences() {
-			return true;
-		}
-		public boolean supportsPooledSequences() {
-			return false;
-		}
-		public String getSequenceNextValString(String sequenceName) throws MappingException {
-			return "";
-		}
-	}
-
-	private static class PooledSequenceDialect extends SequenceDialect {
-		public boolean supportsPooledSequences() {
-			return true;
-		}
-	}
-}
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
+package org.hibernate.id.enhanced;
+
+import java.util.Properties;
+
+import org.hibernate.Hibernate;
+import org.hibernate.MappingException;
+import org.hibernate.cfg.Environment;
+import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.id.PersistentIdentifierGenerator;
+import org.hibernate.type.StandardBasicTypes;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.fail;
+
+/**
+ * Tests that SequenceStyleGenerator configures itself as expected in various scenarios
+ *
+ * @author Steve Ebersole
+ */
+@SuppressWarnings({ "deprecation" })
+public class SequenceStyleConfigUnitTest extends BaseUnitTestCase {
+	private void assertClassAssignability(Class expected, Class actual) {
+		if ( ! expected.isAssignableFrom( actual ) ) {
+			fail( "Actual type [" + actual.getName() + "] is not assignable to expected type [" + expected.getName() + "]" );
+		}
+	}
+
+
+	/**
+	 * Test all params defaulted with a dialect supporting sequences
+	 */
+	@Test
+	public void testDefaultedSequenceBackedConfiguration() {
+		Dialect dialect = new SequenceDialect();
+		Properties props = buildGeneratorPropertiesBase();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	private Properties buildGeneratorPropertiesBase() {
+		Properties props = new Properties();
+		props.put(
+				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
+				new ObjectNameNormalizer() {
+					protected boolean isUseQuotedIdentifiersGlobally() {
+						return false;
+					}
+
+					protected NamingStrategy getNamingStrategy() {
+						return null;
+					}
+				}
+		);
+		return props;
+	}
+
+	/**
+	 * Test all params defaulted with a dialect which does not support sequences
+	 */
+	@Test
+	public void testDefaultedTableBackedConfiguration() {
+		Dialect dialect = new TableDialect();
+		Properties props = buildGeneratorPropertiesBase();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test default optimizer selection for sequence backed generators
+	 * based on the configured increment size; both in the case of the
+	 * dialect supporting pooled sequences (pooled) and not (hilo)
+	 */
+	@Test
+	public void testDefaultOptimizerBasedOnIncrementBackedBySequence() {
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
+
+		// for dialects which do not support pooled sequences, we default to pooled+table
+		Dialect dialect = new SequenceDialect();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+
+		// for dialects which do support pooled sequences, we default to pooled+sequence
+		dialect = new PooledSequenceDialect();
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test default optimizer selection for table backed generators
+	 * based on the configured increment size.  Here we always prefer
+	 * pooled.
+	 */
+	@Test
+	public void testDefaultOptimizerBasedOnIncrementBackedByTable() {
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
+		Dialect dialect = new TableDialect();
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test forcing of table as backing strucuture with dialect supporting sequences
+	 */
+	@Test
+	public void testForceTableUse() {
+		Dialect dialect = new SequenceDialect();
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.FORCE_TBL_PARAM, "true" );
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
+	}
+
+	/**
+	 * Test explicitly specifying both optimizer and increment
+	 */
+	@Test
+	public void testExplicitOptimizerWithExplicitIncrementSize() {
+		// with sequence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		final Dialect dialect = new SequenceDialect();
+
+		// optimizer=none w/ increment > 1 => should honor optimizer
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.NONE );
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( 1, generator.getOptimizer().getIncrementSize() );
+		assertEquals( 1, generator.getDatabaseStructure().getIncrementSize() );
+
+		// optimizer=hilo w/ increment > 1 => hilo
+		props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.HILO );
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.HiLoOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
+		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
+
+		// optimizer=pooled w/ increment > 1 => hilo
+		props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.POOL );
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		// because the dialect reports to not support pooled seqyences, the expectation is that we will
+		// use a table for the backing structure...
+		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
+		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
+	}
+
+	@Test
+	public void testPreferPooledLoSettingHonored() {
+		final Dialect dialect = new PooledSequenceDialect();
+
+		Properties props = buildGeneratorPropertiesBase();
+		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
+		SequenceStyleGenerator generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
+
+		props.setProperty( Environment.PREFER_POOLED_VALUES_LO, "true" );
+		generator = new SequenceStyleGenerator();
+		generator.configure( StandardBasicTypes.LONG, props, dialect );
+		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
+		assertClassAssignability( OptimizerFactory.PooledLoOptimizer.class, generator.getOptimizer().getClass() );
+	}
+
+	private static class TableDialect extends Dialect {
+		public boolean supportsSequences() {
+			return false;
+		}
+	}
+
+	private static class SequenceDialect extends Dialect {
+		public boolean supportsSequences() {
+			return true;
+		}
+		public boolean supportsPooledSequences() {
+			return false;
+		}
+		public String getSequenceNextValString(String sequenceName) throws MappingException {
+			return "";
+		}
+	}
+
+	private static class PooledSequenceDialect extends SequenceDialect {
+		public boolean supportsPooledSequences() {
+			return true;
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
index 50afb9c80a..c286c28972 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/EntityTest.java
@@ -1,447 +1,448 @@
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
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.StaleStateException;
 import org.hibernate.Transaction;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard
  */
 public class EntityTest extends BaseCoreFunctionalTestCase {
 	private DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
 
 	@Test
 	public void testLoad() throws Exception {
 		//put an object in DB
 		assertEquals( "Flight", configuration().getClassMapping( Flight.class.getName() ).getTable().getName() );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight firstOne = new Flight();
 		firstOne.setId( new Long( 1 ) );
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
 		firstOne = (Flight) s.get( Flight.class, new Long( 1 ) );
 		assertNotNull( firstOne );
 		assertEquals( new Long( 1 ), firstOne.getId() );
 		assertEquals( "AF3202", firstOne.getName() );
 		assertEquals( new Long( 1000000 ), firstOne.getDuration() );
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
 		firstOne.setId( new Long( 1 ) );
 		firstOne.setName( "AF3202" );
 		firstOne.setDuration( new Long( 1000000 ) );
 		firstOne.setDurationInSec( 2000 );
 		s.save( firstOne );
 		s.flush();
 		tx.commit();
 		s.close();
 		
 
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = new Flight();
 		firstOne.setId( new Long( 1 ) );
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
 		firstOne.setId( new Long( 1 ) );
 		firstOne.setName( "AF3202" );
 		firstOne.setTriggeredData( "should not be insertable" );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = (Flight) s.get( Flight.class, new Long( 1 ) );
 		assertNotNull( firstOne );
 		assertEquals( new Long( 1 ), firstOne.getId() );
 		assertEquals( "AF3202", firstOne.getName() );
 		assertFalse( "should not be insertable".equals( firstOne.getTriggeredData() ) );
 		firstOne.setName( "BA1234" );
 		firstOne.setTriggeredData( "should not be updatable" );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = (Flight) s.get( Flight.class, new Long( 1 ) );
 		assertNotNull( firstOne );
 		assertEquals( new Long( 1 ), firstOne.getId() );
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
 		sky.id = new Long( 2 );
 		sky.color = "blue";
 		sky.day = "monday";
 		sky.month = "January";
 
 		Sky sameSky = new Sky();
 		sameSky.id = new Long( 3 );
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
 		sky.id = new Long( id++ );
 		sky.color = "green";
 		sky.day = "monday";
 		sky.month = "March";
 
 		Sky otherSky = new Sky();
 		otherSky.id = new Long( id++ );
 		otherSky.color = "red";
 		otherSky.day = "friday";
 		otherSky.month = "March";
 
 		Sky sameSky = new Sky();
 		sameSky.id = new Long( id++ );
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
 		}
 		finally {
 			if ( tx != null ) tx.rollback();
 			s.close();
 		}
 	}
 
 	@Test
 	public void testVersion() throws Exception {
 //		put an object in DB
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight firstOne = new Flight();
 		firstOne.setId( new Long( 2 ) );
 		firstOne.setName( "AF3202" );
 		firstOne.setDuration( new Long( 500 ) );
 		s.save( firstOne );
 		s.flush();
 		tx.commit();
 		s.close();
 
 		//read it
 		s = openSession();
 		tx = s.beginTransaction();
 		firstOne = (Flight) s.get( Flight.class, new Long( 2 ) );
 		tx.commit();
 		s.close();
 
 		//read it again
 		s = openSession();
 		tx = s.beginTransaction();
 		Flight concurrentOne = (Flight) s.get( Flight.class, new Long( 2 ) );
 		concurrentOne.setDuration( new Long( 1000 ) );
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
 		sky.id = new Long( 1 );
 		sky.color = "black";
 		Sky.area = "Paris";
 		sky.day = "23";
 		sky.month = "1";
 		s.save( sky );
 		tx.commit();
 		s.close();
 		Sky.area = "London";
 
 		s = openSession();
 		tx = s.beginTransaction();
 		sky = (Sky) s.get( Sky.class, sky.id );
 		assertNotNull( sky );
 		assertEquals( "black", sky.color );
 		assertFalse( "Paris".equals( Sky.area ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEntityName() throws Exception {
 		assertEquals( "Corporation", configuration().getClassMapping( Company.class.getName() ).getTable().getName() );
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
 		airFrance.setId( new Long( 747 ) );
 		airFrance.setName( "Paris-Amsterdam" );
 		airFrance.setDuration( new Long( 10 ) );
 		airFrance.setFactor( 25 );
 		s.persist( airFrance );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		airFrance = (Flight) s.get( Flight.class, airFrance.getId() );
 		assertNotNull( airFrance );
 		assertEquals( new Long( 10 ), airFrance.getDuration() );
 		assertFalse( 25 == airFrance.getFactor( false ) );
 		s.delete( airFrance );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testTemporalType() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Flight airFrance = new Flight();
 		airFrance.setId( new Long( 747 ) );
 		airFrance.setName( "Paris-Amsterdam" );
 		airFrance.setDuration( new Long( 10 ) );
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
-		q.setParameter( "departureDate", airFrance.getDepartureDate(), Hibernate.DATE );
+		q.setParameter( "departureDate", airFrance.getDepartureDate(), StandardBasicTypes.DATE );
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
 		airFrance.setId( new Long( 747 ) );
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
 		return new SchemaExport( serviceRegistry(), configuration() );
 	}
 
 	@After
 	public void runDropSchema() {
 		schemaExport().drop( false, true );
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java
index 4d08e21a67..5d3369ec51 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/entity/MonetaryAmountUserType.java
@@ -1,104 +1,106 @@
 //$Id$
 package org.hibernate.test.annotations.entity;
+
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Currency;
-import org.hibernate.Hibernate;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.usertype.CompositeUserType;
 
 /**
  * @author Emmanuel Bernard
  */
 public class MonetaryAmountUserType implements CompositeUserType {
 
 	public String[] getPropertyNames() {
 		return new String[]{"amount", "currency"};
 	}
 
 	public Type[] getPropertyTypes() {
-		return new Type[]{Hibernate.BIG_DECIMAL, Hibernate.CURRENCY};
+		return new Type[]{ StandardBasicTypes.BIG_DECIMAL, StandardBasicTypes.CURRENCY };
 	}
 
 	public Object getPropertyValue(Object component, int property) throws HibernateException {
 		MonetaryAmount ma = (MonetaryAmount) component;
-		return property == 0 ? (Object) ma.getAmount() : (Object) ma.getCurrency();
+		return property == 0 ? ma.getAmount() : ma.getCurrency();
 	}
 
 	public void setPropertyValue(Object component, int property, Object value)
 			throws HibernateException {
 		MonetaryAmount ma = (MonetaryAmount) component;
 		if ( property == 0 ) {
 			ma.setAmount( (BigDecimal) value );
 		}
 		else {
 			ma.setCurrency( (Currency) value );
 		}
 	}
 
 	public Class returnedClass() {
 		return MonetaryAmount.class;
 	}
 
 	public boolean equals(Object x, Object y) throws HibernateException {
 		if ( x == y ) return true;
 		if ( x == null || y == null ) return false;
 		MonetaryAmount mx = (MonetaryAmount) x;
 		MonetaryAmount my = (MonetaryAmount) y;
 		return mx.getAmount().equals( my.getAmount() ) &&
 				mx.getCurrency().equals( my.getCurrency() );
 	}
 
 	public int hashCode(Object x) throws HibernateException {
 		return ( (MonetaryAmount) x ).getAmount().hashCode();
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
-		BigDecimal amt = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet( rs, names[0], session);
-		Currency cur = (Currency) Hibernate.CURRENCY.nullSafeGet( rs, names[1], session );
+		BigDecimal amt = StandardBasicTypes.BIG_DECIMAL.nullSafeGet( rs, names[0], session);
+		Currency cur = StandardBasicTypes.CURRENCY.nullSafeGet( rs, names[1], session );
 		if ( amt == null ) return null;
 		return new MonetaryAmount( amt, cur );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st, Object value, int index,
 			SessionImplementor session
 	) throws HibernateException, SQLException {
 		MonetaryAmount ma = (MonetaryAmount) value;
 		BigDecimal amt = ma == null ? null : ma.getAmount();
 		Currency cur = ma == null ? null : ma.getCurrency();
-		Hibernate.BIG_DECIMAL.nullSafeSet( st, amt, index, session );
-		Hibernate.CURRENCY.nullSafeSet( st, cur, index + 1, session );
+		StandardBasicTypes.BIG_DECIMAL.nullSafeSet( st, amt, index, session );
+		StandardBasicTypes.CURRENCY.nullSafeSet( st, cur, index + 1, session );
 	}
 
 	public Object deepCopy(Object value) throws HibernateException {
 		MonetaryAmount ma = (MonetaryAmount) value;
 		return new MonetaryAmount( ma.getAmount(), ma.getCurrency() );
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session)
 			throws HibernateException {
 		return (Serializable) deepCopy( value );
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return deepCopy( cached );
 	}
 
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return deepCopy( original ); //TODO: improve
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java
index 7825457b26..33c20b7373 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/type/MyOidType.java
@@ -1,145 +1,149 @@
-//$Id$
 package org.hibernate.test.annotations.type;
+
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
-import org.hibernate.Hibernate;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.usertype.CompositeUserType;
 
 /**
  * @author Emmanuel Bernard
  */
 public class MyOidType implements CompositeUserType {
 
-	public static final String[] PROPERTY_NAMES = new String[]{"high", "middle", "low", "other"};
-	public static final Type[] TYPES = new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER};
-
+	public static final String[] PROPERTY_NAMES = new String[]{
+			"high", "middle", "low", "other"
+	};
+	public static final Type[] TYPES = new Type[]{
+			StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER
+	};
 
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	public Object getPropertyValue(Object aObject, int i) throws HibernateException {
 		MyOid dbOid = (MyOid) aObject;
 		switch ( i ) {
 			case 0:
 				return dbOid.getHigh();
 			case 1:
 				return dbOid.getMiddle();
 			case 2:
 				return dbOid.getLow();
 			case 3:
 				return dbOid.getOther();
 			default:
 				throw new HibernateException( "Unsupported property index " + i );
 		}
 
 	}
 
 	public void setPropertyValue(Object aObject, int i, Object aObject1) throws HibernateException {
 		MyOid dbOid = (MyOid) aObject;
 		switch ( i ) {
 			case 0:
 				dbOid.setHigh( (Integer) aObject1 );
 			case 1:
 				dbOid.setMiddle( (Integer) aObject1 );
 			case 2:
 				dbOid.setLow( (Integer) aObject1 );
 			case 3:
 				dbOid.setOther( (Integer) aObject1 );
 			default:
 				throw new HibernateException( "Unsupported property index " + i );
 		}
 	}
 
 	public Class returnedClass() {
 		return MyOid.class;
 	}
 
 	public boolean equals(Object x, Object y) throws HibernateException {
 		if ( x == y ) return true;
 		if ( x == null || y == null ) return false;
 
 		MyOid oid1 = (MyOid) x;
 		MyOid oid2 = (MyOid) y;
 
 		if ( oid1.getHigh() != oid2.getHigh() ) {
 			return false;
 		}
 		if ( oid1.getMiddle() != oid2.getMiddle() ) {
 			return false;
 		}
 		if ( oid1.getLow() != oid2.getLow() ) {
 			return false;
 		}
 		return oid1.getOther() == oid2.getOther();
 
 	}
 
 	public int hashCode(Object aObject) throws HibernateException {
 		return aObject.hashCode();
 	}
 
 	public Object nullSafeGet(
 			ResultSet aResultSet, String[] names, SessionImplementor aSessionImplementor, Object aObject
 	) throws HibernateException, SQLException {
-		Integer highval = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[0], aSessionImplementor );
-		Integer midval = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[1], aSessionImplementor );
-		Integer lowval = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[2], aSessionImplementor );
-		Integer other = (Integer) Hibernate.INTEGER.nullSafeGet( aResultSet, names[3], aSessionImplementor );
+		Integer highval = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[0], aSessionImplementor );
+		Integer midval = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[1], aSessionImplementor );
+		Integer lowval = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[2], aSessionImplementor );
+		Integer other = StandardBasicTypes.INTEGER.nullSafeGet( aResultSet, names[3], aSessionImplementor );
 
 		return new MyOid( highval, midval, lowval, other );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement aPreparedStatement, Object value, int index, SessionImplementor aSessionImplementor
 	) throws HibernateException, SQLException {
 		MyOid c;
 		if ( value == null ) {
 			// todo is this correct?
 			throw new HibernateException( "Oid object may not be null" );
 		}
 		else {
 			c = (MyOid) value;
 		}
 
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getHigh(), index, aSessionImplementor );
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getMiddle(), index + 1, aSessionImplementor );
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getLow(), index + 2, aSessionImplementor );
-		Hibernate.INTEGER.nullSafeSet( aPreparedStatement, c.getOther(), index + 3, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getHigh(), index, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getMiddle(), index + 1, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getLow(), index + 2, aSessionImplementor );
+		StandardBasicTypes.INTEGER.nullSafeSet( aPreparedStatement, c.getOther(), index + 3, aSessionImplementor );
 	}
 
 	public Object deepCopy(Object aObject) throws HibernateException {
 		MyOid oldOid = (MyOid) aObject;
 
 		return new MyOid( oldOid.getHigh(), oldOid.getMiddle(), oldOid.getLow(), oldOid.getOther() );
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor aSessionImplementor) throws HibernateException {
 		return (Serializable) deepCopy( value );
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor aSessionImplementor, Object aObject)
 			throws HibernateException {
 		return deepCopy( cached );
 	}
 
 	public Object replace(Object original, Object target, SessionImplementor aSessionImplementor, Object aObject2)
 			throws HibernateException {
 		// we are immutable. return original
 		return original;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/component/basic/ComponentTest.java b/hibernate-core/src/test/java/org/hibernate/test/component/basic/ComponentTest.java
index ec672f7009..3a961e4e74 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/component/basic/ComponentTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/component/basic/ComponentTest.java
@@ -1,420 +1,421 @@
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
 package org.hibernate.test.component.basic;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.criterion.Property;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class ComponentTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "component/basic/User.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Override
 	public void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 		super.afterConfigurationBuilt( mappings, dialect );
 		// Oracle and Postgres do not have year() functions, so we need to
 		// redefine the 'User.person.yob' formula
 		//
 		// consider temporary until we add the capability to define
 		// mapping formulas which can use dialect-registered functions...
 		PersistentClass user = mappings.getClass( User.class.getName() );
 		org.hibernate.mapping.Property personProperty = user.getProperty( "person" );
 		Component component = ( Component ) personProperty.getValue();
 		Formula f = ( Formula ) component.getProperty( "yob" ).getValue().getColumnIterator().next();
 
 		SQLFunction yearFunction = ( SQLFunction ) dialect.getFunctions().get( "year" );
 		if ( yearFunction == null ) {
 			// the dialect not know to support a year() function, so rely on the
 			// ANSI SQL extract function
 			f.setFormula( "extract( year from dob )");
 		}
 		else {
 			List args = new ArrayList();
 			args.add( "dob" );
-			f.setFormula( yearFunction.render( Hibernate.INTEGER, args, null ) );
+			f.setFormula( yearFunction.render( StandardBasicTypes.INTEGER, args, null ) );
 		}
 	}
 
 	@Test
 	public void testUpdateFalse() {
 		sessionFactory().getStatistics().clear();
 		
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User u = new User( "gavin", "secret", new Person("Gavin King", new Date(), "Karbarook Ave") );
 		s.persist(u);
 		s.flush();
 		u.getPerson().setName("XXXXYYYYY");
 		t.commit();
 		s.close();
 		
 		assertEquals( 1, sessionFactory().getStatistics().getEntityInsertCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getEntityUpdateCount() );
 
 		s = openSession();
 		t = s.beginTransaction();
 		u = (User) s.get(User.class, "gavin");
 		assertEquals( u.getPerson().getName(), "Gavin King" );
 		s.delete(u);
 		t.commit();
 		s.close();
 		
 		assertEquals( 1, sessionFactory().getStatistics().getEntityDeleteCount() );
 	}
 	
 	@Test
 	public void testComponent() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User u = new User( "gavin", "secret", new Person("Gavin King", new Date(), "Karbarook Ave") );
 		s.persist(u);
 		s.flush();
 		u.getPerson().changeAddress("Phipps Place");
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		u = (User) s.get(User.class, "gavin");
 		assertEquals( u.getPerson().getAddress(), "Phipps Place" );
 		assertEquals( u.getPerson().getPreviousAddress(), "Karbarook Ave" );
 		assertEquals( u.getPerson().getYob(), u.getPerson().getDob().getYear()+1900 );
 		u.setPassword("$ecret");
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		u = (User) s.get(User.class, "gavin");
 		assertEquals( u.getPerson().getAddress(), "Phipps Place" );
 		assertEquals( u.getPerson().getPreviousAddress(), "Karbarook Ave" );
 		assertEquals( u.getPassword(), "$ecret" );
 		s.delete(u);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2366" )
 	public void testComponentStateChangeAndDirtiness() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "hibernater", new Person( "Steve Ebersole", new Date(), "Main St") );
 		s.persist( u );
 		s.flush();
 		long intialUpdateCount = sessionFactory().getStatistics().getEntityUpdateCount();
 		u.getPerson().setAddress( "Austin" );
 		s.flush();
 		assertEquals( intialUpdateCount + 1, sessionFactory().getStatistics().getEntityUpdateCount() );
 		intialUpdateCount = sessionFactory().getStatistics().getEntityUpdateCount();
 		u.getPerson().setAddress( "Cedar Park" );
 		s.flush();
 		assertEquals( intialUpdateCount + 1, sessionFactory().getStatistics().getEntityUpdateCount() );
 		s.delete( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentQueries() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Employee emp = new Employee();
 		emp.setHireDate( new Date() );
 		emp.setPerson( new Person() );
 		emp.getPerson().setName( "steve" );
 		emp.getPerson().setDob( new Date() );
 		s.save( emp );
 
 		s.createQuery( "from Employee e where e.person = :p and 1 = 1 and 2=2" ).setParameter( "p", emp.getPerson() ).list();
 		s.createQuery( "from Employee e where :p = e.person" ).setParameter( "p", emp.getPerson() ).list();
 		// The following fails on Sybase due to HHH-3510. When HHH-3510 
 		// is fixed, the check for SybaseASE15Dialect should be removed.
 		if ( ! ( getDialect() instanceof SybaseASE15Dialect ) ) {
 			s.createQuery( "from Employee e where e.person = ('steve', current_timestamp)" ).list();
 		}
 
 		s.delete( emp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialect( value = SybaseASE15Dialect.class )
 	@FailureExpected( jiraKey = "HHH-3150" )
 	public void testComponentQueryMethodNoParensFailureExpected() {
 		// Sybase should translate "current_timestamp" in HQL to "getdate()";
 		// This fails currently due to HHH-3510. The following test should be
 		// deleted and testComponentQueries() should be updated (as noted
 		// in that test case) when HHH-3510 is fixed.
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Employee emp = new Employee();
 		emp.setHireDate( new Date() );
 		emp.setPerson( new Person() );
 		emp.getPerson().setName( "steve" );
 		emp.getPerson().setDob( new Date() );
 		s.save( emp );
 		s.createQuery( "from Employee e where e.person = ('steve', current_timestamp)" ).list();
 		s.delete( emp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentFormulaQuery() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery("from User u where u.person.yob = 1999").list();
 		s.createCriteria(User.class)
 			.add( Property.forName("person.yob").between( new Integer(1999), new Integer(2002) ) )
 			.list();
 		if ( getDialect().supportsRowValueConstructorSyntax() ) {
 			s.createQuery("from User u where u.person = ('gavin', :dob, 'Peachtree Rd', 'Karbarook Ave', 1974, 34, 'Peachtree Rd')")
 				.setDate("dob", new Date("March 25, 1974")).list();
 			s.createQuery("from User where person = ('gavin', :dob, 'Peachtree Rd', 'Karbarook Ave', 1974, 34, 'Peachtree Rd')")
 				.setDate("dob", new Date("March 25, 1974")).list();
 		}
 		t.commit();
 		s.close();
 	}
 	
 	@Test
 	public void testCustomColumnReadAndWrite() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User u = new User( "steve", "hibernater", new Person( "Steve Ebersole", new Date(), "Main St") );
 		final double HEIGHT_INCHES = 73;
 		final double HEIGHT_CENTIMETERS = HEIGHT_INCHES * 2.54d;
 		u.getPerson().setHeightInches(HEIGHT_INCHES);
 		s.persist( u );
 		s.flush();
 		
 		// Test value conversion during insert
 		Double heightViaSql = (Double)s.createSQLQuery("select height_centimeters from t_user where t_user.username='steve'").uniqueResult();
 		assertEquals(HEIGHT_CENTIMETERS, heightViaSql, 0.01d);
 
 		// Test projection
 		Double heightViaHql = (Double)s.createQuery("select u.person.heightInches from User u where u.id = 'steve'").uniqueResult();
 		assertEquals(HEIGHT_INCHES, heightViaHql, 0.01d);
 		
 		// Test restriction and entity load via criteria
 		u = (User)s.createCriteria(User.class)
 			.add(Restrictions.between("person.heightInches", HEIGHT_INCHES - 0.01d, HEIGHT_INCHES + 0.01d))
 			.uniqueResult();
 		assertEquals(HEIGHT_INCHES, u.getPerson().getHeightInches(), 0.01d);
 		
 		// Test predicate and entity load via HQL
 		u = (User)s.createQuery("from User u where u.person.heightInches between ? and ?")
 			.setDouble(0, HEIGHT_INCHES - 0.01d)
 			.setDouble(1, HEIGHT_INCHES + 0.01d)
 			.uniqueResult();
 		assertEquals(HEIGHT_INCHES, u.getPerson().getHeightInches(), 0.01d);
 		
 		// Test update
 		u.getPerson().setHeightInches(1);
 		s.flush();
 		heightViaSql = (Double)s.createSQLQuery("select height_centimeters from t_user where t_user.username='steve'").uniqueResult();
 		assertEquals(2.54d, heightViaSql, 0.01d);
 		s.delete(u);
 		t.commit();
 		s.close();
 	}
 	
 	@Test
 	public void testNamedQuery() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.getNamedQuery("userNameIn")
 			.setParameterList( "nameList", new Object[] {"1ovthafew", "turin", "xam"} )
 			.list();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testMergeComponent() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Employee emp = new Employee();
 		emp.setHireDate( new Date() );
 		emp.setPerson( new Person() );
 		emp.getPerson().setName( "steve" );
 		emp.getPerson().setDob( new Date() );
 		s.persist( emp );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.get( Employee.class, emp.getId() );
 		t.commit();
 		s.close();
 
 		assertNull(emp.getOptionalComponent());
 		emp.setOptionalComponent( new OptionalComponent() );
 		emp.getOptionalComponent().setValue1( "emp-value1" );
 		emp.getOptionalComponent().setValue2( "emp-value2" );
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.merge( emp );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.get( Employee.class, emp.getId() );
 		t.commit();
 		s.close();
 
 		assertEquals("emp-value1", emp.getOptionalComponent().getValue1());
 		assertEquals("emp-value2", emp.getOptionalComponent().getValue2());
 		emp.getOptionalComponent().setValue1( null );
 		emp.getOptionalComponent().setValue2( null );
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.merge( emp );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.get( Employee.class, emp.getId() );
 		Hibernate.initialize(emp.getDirectReports());
 		t.commit();
 		s.close();
 
 		assertNull(emp.getOptionalComponent());
 
 		Employee emp1 = new Employee();
 		emp1.setHireDate( new Date() );
 		emp1.setPerson( new Person() );
 		emp1.getPerson().setName( "bozo" );
 		emp1.getPerson().setDob( new Date() );
 		emp.getDirectReports().add( emp1 );
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.merge( emp );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.get( Employee.class, emp.getId() );
 		Hibernate.initialize(emp.getDirectReports());
 		t.commit();
 		s.close();
 
 		assertEquals(1, emp.getDirectReports().size());
 		emp1 = (Employee)emp.getDirectReports().iterator().next();
 		assertNull( emp1.getOptionalComponent() );
 		emp1.setOptionalComponent( new OptionalComponent() );
 		emp1.getOptionalComponent().setValue1( "emp1-value1" );
 		emp1.getOptionalComponent().setValue2( "emp1-value2" );
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.merge( emp );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.get( Employee.class, emp.getId() );
 		Hibernate.initialize(emp.getDirectReports());
 		t.commit();
 		s.close();
 
 		assertEquals(1, emp.getDirectReports().size());
 		emp1 = (Employee)emp.getDirectReports().iterator().next();
 		assertEquals( "emp1-value1", emp1.getOptionalComponent().getValue1());
 		assertEquals( "emp1-value2", emp1.getOptionalComponent().getValue2());
 		emp1.getOptionalComponent().setValue1( null );
 		emp1.getOptionalComponent().setValue2( null );
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.merge( emp );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		emp = (Employee)s.get( Employee.class, emp.getId() );
 		Hibernate.initialize(emp.getDirectReports());
 		t.commit();
 		s.close();
 
 		assertEquals(1, emp.getDirectReports().size());
 		emp1 = (Employee)emp.getDirectReports().iterator().next();
 		assertNull(emp1.getOptionalComponent());
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( emp );
 		t.commit();
 		s.close();
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/compositeelement/CompositeElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/compositeelement/CompositeElementTest.java
index 4b8d9826e1..812a665376 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/compositeelement/CompositeElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/compositeelement/CompositeElementTest.java
@@ -1,147 +1,148 @@
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
 package org.hibernate.test.compositeelement;
 import java.util.ArrayList;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Formula;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Gavin King
  */
 public class CompositeElementTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "compositeelement/Parent.hbm.xml" };
 	}
 
 	@Override
 	public void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 		super.afterConfigurationBuilt( mappings, dialect );
 		Collection children = mappings.getCollection( Parent.class.getName() + ".children" );
 		Component childComponents = ( Component ) children.getElement();
 		Formula f = ( Formula ) childComponents.getProperty( "bioLength" ).getValue().getColumnIterator().next();
 
 		SQLFunction lengthFunction = ( SQLFunction ) dialect.getFunctions().get( "length" );
 		if ( lengthFunction != null ) {
 			ArrayList args = new ArrayList();
 			args.add( "bio" );
-			f.setFormula( lengthFunction.render( Hibernate.INTEGER, args, null ) );
+			f.setFormula( lengthFunction.render( StandardBasicTypes.INTEGER, args, null ) );
 		}
 	}
 
 	@Test
 	public void testHandSQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Child c = new Child( "Child One" );
 		Parent p = new Parent( "Parent" );
 		p.getChildren().add( c );
 		c.setParent( p );
 		s.save( p );
 		s.flush();
 
 		p.getChildren().remove( c );
 		c.setParent( null );
 		s.flush();
 
 		p.getChildren().add( c );
 		c.setParent( p );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.createQuery( "select distinct p from Parent p join p.children c where c.name like 'Child%'" ).uniqueResult();
 		s.clear();
 		s.createQuery( "select new Child(c.name) from Parent p left outer join p.children c where c.name like 'Child%'" )
 				.uniqueResult();
 		s.clear();
 		//s.createQuery("select c from Parent p left outer join p.children c where c.name like 'Child%'").uniqueResult(); //we really need to be able to do this!
 		s.clear();
 		p = ( Parent ) s.createQuery( "from Parent p left join fetch p.children" ).uniqueResult();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( p );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCustomColumnReadAndWrite() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Child c = new Child( "Child One" );
 		c.setPosition( 1 );
 		Parent p = new Parent( "Parent" );
 		p.getChildren().add( c );
 		c.setParent( p );
 		s.save( p );
 		s.flush();
 
 		Integer sqlValue = (Integer) s.createSQLQuery("select child_position from parentchild c where c.name='Child One'")
 				.uniqueResult();
 		assertEquals( 0, sqlValue.intValue() );
 
 		Integer hqlValue = (Integer)s.createQuery("select c.position from Parent p join p.children c where p.name='Parent'")
 				.uniqueResult();
 		assertEquals( 1, hqlValue.intValue() );
 
 		p = (Parent)s.createCriteria(Parent.class).add(Restrictions.eq("name", "Parent")).uniqueResult();
 		c = (Child)p.getChildren().iterator().next();
 		assertEquals( 1, c.getPosition() );
 
 		p = (Parent)s.createQuery("from Parent p join p.children c where c.position = 1").uniqueResult();
 		c = (Child)p.getChildren().iterator().next();
 		assertEquals( 1, c.getPosition() );
 
 		c.setPosition( 2 );
 		s.flush();
 		sqlValue = (Integer) s.createSQLQuery("select child_position from parentchild c where c.name='Child One'")
 				.uniqueResult();
 		assertEquals( 1, sqlValue.intValue() );
 		s.delete( p );
 		t.commit();
 		s.close();
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java b/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java
index ffc048d782..d1401ee58f 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/criteria/CriteriaQueryTest.java
@@ -1,1778 +1,1779 @@
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
 package org.hibernate.test.criteria;
 
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.HashSet;
 import java.util.HashMap;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.JDBCException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.DetachedCriteria;
 import org.hibernate.criterion.Example;
 import org.hibernate.criterion.MatchMode;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projection;
 import org.hibernate.criterion.Projections;
 import org.hibernate.criterion.Property;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.criterion.Subqueries;
 import org.hibernate.exception.SQLGrammarException;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.transform.Transformers;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.hql.Animal;
 import org.hibernate.test.hql.Reptile;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class CriteriaQueryTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "criteria/Enrolment.hbm.xml","criteria/Foo.hbm.xml", "hql/Animal.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "criteriaquerytest" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Test
 	public void testEscapeCharacter() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 		Course c1 = new Course();
 		c1.setCourseCode( "course-1" );
 		c1.setDescription( "%1" );
 		Course c2 = new Course();
 		c2.setCourseCode( "course-2" );
 		c2.setDescription( "%2" );
 		Course c3 = new Course();
 		c3.setCourseCode( "course-3" );
 		c3.setDescription( "control" );
 		session.persist( c1 );
 		session.persist( c2 );
 		session.persist( c3 );
 		session.flush();
 		session.clear();
 
 		// finds all courses which have a description equal to '%1'
 		Course example = new Course();
 		example.setDescription( "&%1" );
 		List result = session.createCriteria( Course.class )
 				.add( Example.create( example ).ignoreCase().enableLike().setEscapeCharacter( new Character( '&' ) ) )
 				.list();
 		assertEquals( 1, result.size() );
 		// finds all courses which contain '%' as the first char in the description
 		example.setDescription( "&%%" );
 		result = session.createCriteria( Course.class )
 				.add( Example.create( example ).ignoreCase().enableLike().setEscapeCharacter( new Character( '&' ) ) )
 				.list();
 		assertEquals( 2, result.size() );
 
 		session.createQuery( "delete Course" ).executeUpdate();
 		t.commit();
 		session.close();
 	}
 
 	@Test
 	public void testScrollCriteria() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		session.persist(course);
 		session.flush();
 		session.clear();
 		ScrollableResults sr = session.createCriteria(Course.class).scroll();
 		assertTrue( sr.next() );
 		course = (Course) sr.get(0);
 		assertNotNull(course);
 		sr.close();
 		session.delete(course);
 
 		t.commit();
 		session.close();
 	}
 	
 	@Test
 	public void testSubselect() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		session.persist(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(232);
 		session.persist(gavin);
 
 		Enrolment enrolment2 = new Enrolment();
 		enrolment2.setCourse(course);
 		enrolment2.setCourseCode(course.getCourseCode());
 		enrolment2.setSemester((short) 3);
 		enrolment2.setYear((short) 1998);
 		enrolment2.setStudent(gavin);
 		enrolment2.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment2);
 		session.persist(enrolment2);
 
 		DetachedCriteria dc = DetachedCriteria.forClass(Student.class)
 			.add( Property.forName("studentNumber").eq( new Long(232) ) )
 			.setProjection( Property.forName("name") );
 
 		session.createCriteria(Student.class)
 			.add( Subqueries.propertyEqAll("name", dc) )
 			.list();
 
 		session.createCriteria(Student.class)
 			.add( Subqueries.exists(dc) )
 			.list();
 
 		session.createCriteria(Student.class)
 			.add( Property.forName("name").eqAll(dc) )
 			.list();
 
 		session.createCriteria(Student.class)
 			.add( Subqueries.in("Gavin King", dc) )
 			.list();
 
 		DetachedCriteria dc2 = DetachedCriteria.forClass(Student.class, "st")
 			.add( Property.forName("st.studentNumber").eqProperty("e.studentNumber") )
 			.setProjection( Property.forName("name") );
 
 		session.createCriteria(Enrolment.class, "e")
 			.add( Subqueries.eq("Gavin King", dc2) )
 			.list();
 
 		DetachedCriteria dc3 = DetachedCriteria.forClass(Student.class, "st")
 			.createCriteria("enrolments")
 				.createCriteria("course")
 					.add( Property.forName("description").eq("Hibernate Training") )
 					.setProjection( Property.forName("st.name") );
 
 		session.createCriteria(Enrolment.class, "e")
 			.add( Subqueries.eq("Gavin King", dc3) )
 			.list();
 
 		DetachedCriteria dc4 = DetachedCriteria.forClass(Student.class, "st")
 			.setProjection( Property.forName("name").as( "stname" ) );
 
 		dc4.getExecutableCriteria( session ).list();
 
 		dc4.getExecutableCriteria( session ).addOrder( Order.asc( "stname" ) ).list();
 
 		session.createCriteria(Enrolment.class, "e")
 			.add( Subqueries.eq("Gavin King", dc4) )
 			.list();
 
 		session.delete(enrolment2);
 		session.delete(gavin);
 		session.delete(course);
 		t.commit();
 		session.close();
 	}
 
 	@Test
 	public void testSubselectWithComponent() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		session.persist(course);
 
 		CityState odessaWa = new CityState( "Odessa", "WA" );
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(232);
 		gavin.setCityState( odessaWa );
 		session.persist(gavin);
 
 		Enrolment enrolment2 = new Enrolment();
 		enrolment2.setCourse(course);
 		enrolment2.setCourseCode(course.getCourseCode());
 		enrolment2.setSemester((short) 3);
 		enrolment2.setYear((short) 1998);
 		enrolment2.setStudent(gavin);
 		enrolment2.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment2);
 		session.persist(enrolment2);
 
 		DetachedCriteria dc = DetachedCriteria.forClass(Student.class)
 			.add( Property.forName("cityState").eq( odessaWa ) )
 			.setProjection( Property.forName("cityState") );
 
 		session.createCriteria(Student.class)
 			.add( Subqueries.exists(dc) )
 			.list();
 		t.commit();
 		session.close();
 
 		session = openSession();
 		t = session.beginTransaction();
 		try {
 			session.createCriteria(Student.class)
 				.add( Subqueries.propertyEqAll("cityState", dc) )
 				.list();
 			fail( "should have failed because cannot compare subquery results with multiple columns" );
 		}
 		catch ( QueryException ex ) {
 			// expected
 		}
 		finally {
 			t.rollback();
 			session.close();
 		}
 
 		session = openSession();
 		t = session.beginTransaction();
 		try {
 			session.createCriteria(Student.class)
 				.add( Property.forName("cityState").eqAll(dc) )
 				.list();
 			fail( "should have failed because cannot compare subquery results with multiple columns" );
 		}
 		catch ( QueryException ex ) {
 			// expected
 		}
 		finally {
 			t.rollback();
 			session.close();
 		}
 
 		session = openSession();
 		t = session.beginTransaction();
 		try {
 			session.createCriteria(Student.class)
 				.add( Subqueries.in( odessaWa, dc) )
 				.list();
 			fail( "should have failed because cannot compare subquery results with multiple columns" );
 		}
 		catch ( JDBCException ex ) {
 			// expected
 		}
 		finally {
 			t.rollback();
 			session.close();
 		}
 
 		session = openSession();
 		t = session.beginTransaction();
 		DetachedCriteria dc2 = DetachedCriteria.forClass(Student.class, "st1")
 			.add( Property.forName("st1.cityState").eqProperty("st2.cityState") )
 			.setProjection( Property.forName("cityState") );
 		try {
 			session.createCriteria(Student.class, "st2")
 				.add( Subqueries.eq( odessaWa, dc2) )
 				.list();
 			fail( "should have failed because cannot compare subquery results with multiple columns" );
 		}
 		catch ( JDBCException ex ) {
 			// expected
 		}
 		finally {
 			t.rollback();
 			session.close();
 		}
 
 		session = openSession();
 		t = session.beginTransaction();
 		DetachedCriteria dc3 = DetachedCriteria.forClass(Student.class, "st")
 			.createCriteria("enrolments")
 				.createCriteria("course")
 					.add( Property.forName("description").eq("Hibernate Training") )
 					.setProjection( Property.forName("st.cityState") );
 		try {
 			session.createCriteria(Enrolment.class, "e")
 				.add( Subqueries.eq( odessaWa, dc3) )
 				.list();
 			fail( "should have failed because cannot compare subquery results with multiple columns" );
 		}
 		catch ( JDBCException ex ) {
 			// expected
 		}
 		finally {
 			t.rollback();
 			session.close();
 		}
 
 		session = openSession();
 		t = session.beginTransaction();
 		session.delete(enrolment2);
 		session.delete(gavin);
 		session.delete(course);
 		t.commit();
 		session.close();
 
 	}
 
 	@Test
 	public void testDetachedCriteria() {
 		DetachedCriteria dc = DetachedCriteria.forClass(Student.class)
 			.add( Property.forName("name").eq("Gavin King") )
 			.addOrder( Order.asc("studentNumber") )
 			.setProjection( Property.forName("studentNumber") );
 
 		byte[] bytes = SerializationHelper.serialize(dc);
 
 		dc = (DetachedCriteria) SerializationHelper.deserialize( bytes );
 
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(232);
 		Student bizarroGavin = new Student();
 		bizarroGavin.setName("Gavin King");
 		bizarroGavin.setStudentNumber(666);
 		session.persist(bizarroGavin);
 		session.persist(gavin);
 
 		List result = dc.getExecutableCriteria(session)
 			.setMaxResults(3)
 			.list();
 
 		assertEquals( result.size(), 2 );
 		assertEquals( result.get(0), new Long(232) );
 		assertEquals( result.get(1), new Long(666) );
 
 		session.delete(gavin);
 		session.delete(bizarroGavin);
 		t.commit();
 		session.close();
 	}
 
 	@Test
 	public void testProjectionCache() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		s.save(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(666);
 		s.save(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 		s.save(xam);
 
 		Enrolment enrolment1 = new Enrolment();
 		enrolment1.setCourse(course);
 		enrolment1.setCourseCode(course.getCourseCode());
 		enrolment1.setSemester((short) 1);
 		enrolment1.setYear((short) 1999);
 		enrolment1.setStudent(xam);
 		enrolment1.setStudentNumber(xam.getStudentNumber());
 		xam.getEnrolments().add(enrolment1);
 		s.save(enrolment1);
 
 		Enrolment enrolment2 = new Enrolment();
 		enrolment2.setCourse(course);
 		enrolment2.setCourseCode(course.getCourseCode());
 		enrolment2.setSemester((short) 3);
 		enrolment2.setYear((short) 1998);
 		enrolment2.setStudent(gavin);
 		enrolment2.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment2);
 		s.save(enrolment2);
 
 		List list = s.createCriteria(Enrolment.class)
 			.createAlias( "student", "s" )
 			.createAlias( "course", "c" )
 			.add( Restrictions.isNotEmpty( "s.enrolments" ) )
 			.setProjection(
 					Projections.projectionList()
 							.add( Projections.property( "s.name" ) )
 							.add( Projections.property( "c.description" ) )
 			)
 			.setCacheable( true )
 			.list();
 
 		assertEquals( list.size(), 2 );
 		assertEquals( ( (Object[]) list.get(0) ).length, 2 );
 		assertEquals( ( (Object[]) list.get(1) ).length, 2 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		s.createCriteria(Enrolment.class)
 			.createAlias( "student", "s" )
 			.createAlias( "course", "c" )
 			.add( Restrictions.isNotEmpty( "s.enrolments" ) )
 			.setProjection(
 					Projections.projectionList()
 							.add( Projections.property( "s.name" ) )
 							.add( Projections.property( "c.description" ) )
 			)
 			.setCacheable( true )
 			.list();
 
 		assertEquals( list.size(), 2 );
 		assertEquals( ( (Object[]) list.get(0) ).length, 2 );
 		assertEquals( ( (Object[]) list.get(1) ).length, 2 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		s.createCriteria(Enrolment.class)
 			.createAlias( "student", "s" )
 			.createAlias( "course", "c" )
 			.add( Restrictions.isNotEmpty( "s.enrolments" ) )
 			.setProjection(
 					Projections.projectionList()
 							.add( Projections.property( "s.name" ) )
 							.add( Projections.property( "c.description" ) )
 			)
 			.setCacheable( true )
 			.list();
 
 		assertEquals( list.size(), 2 );
 		assertEquals( ( (Object[]) list.get(0) ).length, 2 );
 		assertEquals( ( (Object[]) list.get(1) ).length, 2 );
 
 		s.delete(enrolment1);
 		s.delete(enrolment2);
 		s.delete(course);
 		s.delete(gavin);
 		s.delete(xam);
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProjections() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		s.save(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(667);
 		s.save(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 		s.save(xam);
 
 		Enrolment enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 1);
 		enrolment.setYear((short) 1999);
 		enrolment.setStudent(xam);
 		enrolment.setStudentNumber(xam.getStudentNumber());
 		xam.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 3);
 		enrolment.setYear((short) 1998);
 		enrolment.setStudent(gavin);
 		enrolment.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		//s.flush();
 
 		Long count = (Long) s.createCriteria(Enrolment.class)
 			.setProjection( Projections.count("studentNumber").setDistinct() )
 			.uniqueResult();
 		assertEquals(count, new Long(2));
 
 		count = (Long) s.createCriteria(Enrolment.class)
 			.setProjection( Projections.countDistinct("studentNumber") )
 			.uniqueResult();
 		assertEquals(count, new Long(2));
 
 		count = (Long) s.createCriteria(Enrolment.class)
 			.setProjection( Projections.countDistinct("courseCode").as( "cnt" ) )
 			.uniqueResult();
 		assertEquals(count, new Long(1));
 
 		Object object = s.createCriteria(Enrolment.class)
 			.setProjection( Projections.projectionList()
 					.add( Projections.count("studentNumber") )
 					.add( Projections.max("studentNumber") )
 					.add( Projections.min("studentNumber") )
 					.add( Projections.avg("studentNumber") )
 			)
 			.uniqueResult();
 		Object[] result = (Object[])object;
 
 		assertEquals(new Long(2),result[0]);
 		assertEquals(new Long(667),result[1]);
 		assertEquals(new Long(101),result[2]);
 		assertEquals( 384.0, ( (Double) result[3] ).doubleValue(), 0.01 );
 
 
 		List resultWithMaps = s.createCriteria(Enrolment.class)
 			.setProjection( Projections.distinct( Projections.projectionList()
 					.add( Projections.property("studentNumber"), "stNumber" )
 					.add( Projections.property("courseCode"), "cCode" ) )
 			)
 		    .add( Restrictions.gt( "studentNumber", new Long(665) ) )
 		    .add( Restrictions.lt( "studentNumber", new Long(668) ) )
 		    .addOrder( Order.asc("stNumber") )
 			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
 			.list();
 
 		assertEquals(1, resultWithMaps.size());
 		Map m1 = (Map) resultWithMaps.get(0);
 
 		assertEquals(new Long(667), m1.get("stNumber"));
 		assertEquals(course.getCourseCode(), m1.get("cCode"));
 
 		resultWithMaps = s.createCriteria(Enrolment.class)
 			.setProjection( Projections.property("studentNumber").as("stNumber") )
 		    .addOrder( Order.desc("stNumber") )
 			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
 			.list();
 
 		assertEquals(2, resultWithMaps.size());
 		Map m0 = (Map) resultWithMaps.get(0);
 		m1 = (Map) resultWithMaps.get(1);
 
 		assertEquals(new Long(101), m1.get("stNumber"));
 		assertEquals(new Long(667), m0.get("stNumber"));
 
 
 		List resultWithAliasedBean = s.createCriteria(Enrolment.class)
 			.createAlias("student", "st")
 			.createAlias("course", "co")
 			.setProjection( Projections.projectionList()
 					.add( Projections.property("st.name"), "studentName" )
 					.add( Projections.property("co.description"), "courseDescription" )
 			)
 			.addOrder( Order.desc("studentName") )
 			.setResultTransformer( Transformers.aliasToBean(StudentDTO.class) )
 			.list();
 
 		assertEquals(2, resultWithAliasedBean.size());
 
 		StudentDTO dto = (StudentDTO) resultWithAliasedBean.get(0);
 		assertNotNull(dto.getDescription());
 		assertNotNull(dto.getName());
 
 		s.createCriteria(Student.class)
 			.add( Restrictions.like("name", "Gavin", MatchMode.START) )
 			.addOrder( Order.asc("name") )
 			.createCriteria("enrolments", "e")
 				.addOrder( Order.desc("year") )
 				.addOrder( Order.desc("semester") )
 			.createCriteria("course","c")
 				.addOrder( Order.asc("description") )
 				.setProjection( Projections.projectionList()
 					.add( Projections.property("this.name") )
 					.add( Projections.property("e.year") )
 					.add( Projections.property("e.semester") )
 					.add( Projections.property("c.courseCode") )
 					.add( Projections.property("c.description") )
 				)
 			.uniqueResult();
 
 		Projection p1 = Projections.projectionList()
 			.add( Projections.count("studentNumber") )
 			.add( Projections.max("studentNumber") )
 			.add( Projections.rowCount() );
 
 		Projection p2 = Projections.projectionList()
 			.add( Projections.min("studentNumber") )
 			.add( Projections.avg("studentNumber") )
 			.add( Projections.sqlProjection(
 					"1 as constOne, count(*) as countStar",
 					new String[] { "constOne", "countStar" },
-					new Type[] { Hibernate.INTEGER, Hibernate.INTEGER }
+					new Type[] { StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER }
 			) );
 
 		Object[] array = (Object[]) s.createCriteria(Enrolment.class)
 			.setProjection( Projections.projectionList().add(p1).add(p2) )
 			.uniqueResult();
 
 		assertEquals( array.length, 7 );
 
 		List list = s.createCriteria(Enrolment.class)
 			.createAlias("student", "st")
 			.createAlias("course", "co")
 			.setProjection( Projections.projectionList()
 					.add( Projections.groupProperty("co.courseCode") )
 					.add( Projections.count("st.studentNumber").setDistinct() )
 					.add( Projections.groupProperty("year") )
 			)
 			.list();
 
 		assertEquals( list.size(), 2 );
 
 		Object g = s.createCriteria(Student.class)
 			.add( Restrictions.idEq( new Long(667) ) )
 			.setFetchMode("enrolments", FetchMode.JOIN)
 			//.setFetchMode("enrolments.course", FetchMode.JOIN) //TODO: would love to make that work...
 			.uniqueResult();
 		assertSame(g, gavin);
 
 		s.delete(gavin);
 		s.delete(xam);
 		s.delete(course);
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProjectionsUsingProperty() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		course.getCourseMeetings().add( new CourseMeeting( course, "Monday", 1, "1313 Mockingbird Lane" ) );
 		s.save(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(667);
 		CityState odessaWa = new CityState( "Odessa", "WA" );
 		gavin.setCityState( odessaWa );
 		gavin.setPreferredCourse( course );
 		s.save(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 		s.save(xam);
 
 		Enrolment enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 1);
 		enrolment.setYear((short) 1999);
 		enrolment.setStudent(xam);
 		enrolment.setStudentNumber(xam.getStudentNumber());
 		xam.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 3);
 		enrolment.setYear((short) 1998);
 		enrolment.setStudent(gavin);
 		enrolment.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		s.flush();
 
 		List  resultList = s.createCriteria(Enrolment.class)
 			.setProjection( Projections.projectionList()
 					.add( Property.forName( "student" ), "student" )
 					.add( Property.forName( "course" ), "course" )
 					.add( Property.forName( "semester" ), "semester" )
 					.add( Property.forName("year"), "year" )
 			)
 			.list();
 		assertEquals( 2, resultList.size() );
 		for ( Iterator it = resultList.iterator(); it.hasNext(); ) {
 			Object[] objects = ( Object[] ) it.next();
 			assertEquals( 4, objects.length );
 			assertTrue( objects[ 0 ] instanceof Student );
 			assertTrue( objects[ 1 ] instanceof Course );
 			assertTrue( objects[ 2 ] instanceof Short );
 			assertTrue( objects[ 3 ] instanceof Short );
 		}
 
 		resultList = s.createCriteria(Student.class)
 			.setProjection( Projections.projectionList()
 					.add( Projections.id().as( "studentNumber" ))
 					.add( Property.forName( "name" ), "name" )
 					.add( Property.forName( "cityState" ), "cityState" )
 					.add( Property.forName("preferredCourse"), "preferredCourse" )
 			)
 			.list();
 		assertEquals( 2, resultList.size() );
 		for ( Iterator it = resultList.iterator(); it.hasNext(); ) {
 			Object[] objects = ( Object[] ) it.next();
 			assertEquals( 4, objects.length );
 			assertTrue( objects[ 0 ] instanceof Long );
 			assertTrue( objects[ 1 ] instanceof String );
 			if ( "Gavin King".equals( objects[ 1 ] ) ) {
 				assertTrue( objects[ 2 ] instanceof CityState );
 				assertTrue( objects[ 3 ] instanceof Course );
 			}
 			else {
 				assertNull( objects[ 2 ] );
 				assertNull( objects[ 3 ] );
 			}
 		}
 
 		resultList = s.createCriteria(Student.class)
 			.add(Restrictions.eq("name", "Gavin King"))
 			.setProjection( Projections.projectionList()
 					.add( Projections.id().as( "studentNumber" ))
 					.add( Property.forName( "name" ), "name" )
 					.add( Property.forName( "cityState" ), "cityState" )
 					.add( Property.forName("preferredCourse"), "preferredCourse" )
 			)
 			.list();
 		assertEquals( 1, resultList.size() );
 
 		Object[] aResult = ( Object[] ) s.createCriteria(Student.class)
 			.add( Restrictions.idEq( new Long( 667 ) ) )
 			.setProjection( Projections.projectionList()
 					.add( Projections.id().as( "studentNumber" ))
 					.add( Property.forName( "name" ), "name" )
 					.add( Property.forName( "cityState" ), "cityState" )
 					.add( Property.forName("preferredCourse"), "preferredCourse" )
 			)
 			.uniqueResult();
 		assertNotNull( aResult );
 		assertEquals( 4, aResult.length );
 		assertTrue( aResult[ 0 ] instanceof Long );
 		assertTrue( aResult[ 1 ] instanceof String );
 		assertTrue( aResult[ 2 ] instanceof CityState );
 		assertTrue( aResult[ 3 ] instanceof Course );
 
 		Long count = (Long) s.createCriteria(Enrolment.class)
 			.setProjection( Property.forName("studentNumber").count().setDistinct() )
 			.uniqueResult();
 		assertEquals(count, new Long(2));
 
 		Object object = s.createCriteria(Enrolment.class)
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("studentNumber").count() )
 					.add( Property.forName("studentNumber").max() )
 					.add( Property.forName("studentNumber").min() )
 					.add( Property.forName("studentNumber").avg() )
 			)
 			.uniqueResult();
 		Object[] result = (Object[])object;
 
 		assertEquals(new Long(2),result[0]);
 		assertEquals(new Long(667),result[1]);
 		assertEquals(new Long(101),result[2]);
 		assertEquals(384.0, ( (Double) result[3] ).doubleValue(), 0.01);
 
 
 		s.createCriteria(Enrolment.class)
 		    .add( Property.forName("studentNumber").gt( new Long(665) ) )
 		    .add( Property.forName("studentNumber").lt( new Long(668) ) )
 		    .add( Property.forName("courseCode").like("HIB", MatchMode.START) )
 		    .add( Property.forName("year").eq( new Short( (short) 1999 ) ) )
 		    .addOrder( Property.forName("studentNumber").asc() )
 			.uniqueResult();
 
 		List resultWithMaps = s.createCriteria(Enrolment.class)
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("studentNumber").as("stNumber") )
 					.add( Property.forName("courseCode").as("cCode") )
 			)
 		    .add( Property.forName("studentNumber").gt( new Long(665) ) )
 		    .add( Property.forName("studentNumber").lt( new Long(668) ) )
 		    .addOrder( Property.forName("studentNumber").asc() )
 			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
 			.list();
 
 		assertEquals(1, resultWithMaps.size());
 		Map m1 = (Map) resultWithMaps.get(0);
 
 		assertEquals(new Long(667), m1.get("stNumber"));
 		assertEquals(course.getCourseCode(), m1.get("cCode"));
 
 		resultWithMaps = s.createCriteria(Enrolment.class)
 			.setProjection( Property.forName("studentNumber").as("stNumber") )
 		    .addOrder( Order.desc("stNumber") )
 			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
 			.list();
 
 		assertEquals(2, resultWithMaps.size());
 		Map m0 = (Map) resultWithMaps.get(0);
 		m1 = (Map) resultWithMaps.get(1);
 
 		assertEquals(new Long(101), m1.get("stNumber"));
 		assertEquals(new Long(667), m0.get("stNumber"));
 
 
 		List resultWithAliasedBean = s.createCriteria(Enrolment.class)
 			.createAlias("student", "st")
 			.createAlias("course", "co")
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("st.name").as("studentName") )
 					.add( Property.forName("co.description").as("courseDescription") )
 			)
 			.addOrder( Order.desc("studentName") )
 			.setResultTransformer( Transformers.aliasToBean(StudentDTO.class) )
 			.list();
 
 		assertEquals(2, resultWithAliasedBean.size());
 
 		StudentDTO dto = (StudentDTO) resultWithAliasedBean.get(0);
 		assertNotNull(dto.getDescription());
 		assertNotNull(dto.getName());
 
 		CourseMeeting courseMeetingDto = ( CourseMeeting ) s.createCriteria(CourseMeeting.class)
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("id").as("id") )
 					.add( Property.forName("course").as("course") )
 			)
 			.addOrder( Order.desc("id") )
 			.setResultTransformer( Transformers.aliasToBean(CourseMeeting.class) )
 			.uniqueResult();
 
 		assertNotNull( courseMeetingDto.getId() );
 		assertEquals( course.getCourseCode(), courseMeetingDto.getId().getCourseCode() );
 		assertEquals( "Monday", courseMeetingDto.getId().getDay() );
 		assertEquals( "1313 Mockingbird Lane", courseMeetingDto.getId().getLocation() );
 		assertEquals( 1, courseMeetingDto.getId().getPeriod() );
 		assertEquals( course.getDescription(), courseMeetingDto.getCourse().getDescription() );
 
 		s.createCriteria(Student.class)
 			.add( Restrictions.like("name", "Gavin", MatchMode.START) )
 			.addOrder( Order.asc("name") )
 			.createCriteria("enrolments", "e")
 				.addOrder( Order.desc("year") )
 				.addOrder( Order.desc("semester") )
 			.createCriteria("course","c")
 				.addOrder( Order.asc("description") )
 				.setProjection( Projections.projectionList()
 					.add( Property.forName("this.name") )
 					.add( Property.forName("e.year") )
 					.add( Property.forName("e.semester") )
 					.add( Property.forName("c.courseCode") )
 					.add( Property.forName("c.description") )
 				)
 			.uniqueResult();
 
 		Projection p1 = Projections.projectionList()
 			.add( Property.forName("studentNumber").count() )
 			.add( Property.forName("studentNumber").max() )
 			.add( Projections.rowCount() );
 
 		Projection p2 = Projections.projectionList()
 			.add( Property.forName("studentNumber").min() )
 			.add( Property.forName("studentNumber").avg() )
 			.add( Projections.sqlProjection(
 					"1 as constOne, count(*) as countStar",
 					new String[] { "constOne", "countStar" },
-					new Type[] { Hibernate.INTEGER, Hibernate.INTEGER }
+					new Type[] { StandardBasicTypes.INTEGER, StandardBasicTypes.INTEGER }
 			) );
 
 		Object[] array = (Object[]) s.createCriteria(Enrolment.class)
 			.setProjection( Projections.projectionList().add(p1).add(p2) )
 			.uniqueResult();
 
 		assertEquals( array.length, 7 );
 
 		List list = s.createCriteria(Enrolment.class)
 			.createAlias("student", "st")
 			.createAlias("course", "co")
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("co.courseCode").group() )
 					.add( Property.forName("st.studentNumber").count().setDistinct() )
 					.add( Property.forName("year").group() )
 			)
 			.list();
 
 		assertEquals( list.size(), 2 );
 
 		list = s.createCriteria(Enrolment.class)
 			.createAlias("student", "st")
 			.createAlias("course", "co")
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("co.courseCode").group().as( "courseCode" ))
 					.add( Property.forName("st.studentNumber").count().setDistinct().as( "studentNumber" ))
 					.add( Property.forName("year").group())
 			)
 			.addOrder( Order.asc( "courseCode" ) )
 			.addOrder( Order.asc( "studentNumber" ) )
 			.list();
 
 		assertEquals( list.size(), 2 );
 
 		list = s.createCriteria(Enrolment.class)
 			.createAlias("student", "st")
 			.createAlias("course", "co")
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("co.courseCode").group().as( "cCode" ))
 					.add( Property.forName("st.studentNumber").count().setDistinct().as( "stNumber" ))
 					.add( Property.forName("year").group())
 			)
 			.addOrder( Order.asc( "cCode" ) )
 			.addOrder( Order.asc( "stNumber" ) )
 			.list();
 
 		assertEquals( list.size(), 2 );
 
 		s.delete(gavin);
 		s.delete(xam);
 		s.delete(course);
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDistinctProjectionsOfComponents() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		s.save(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(667);
 		gavin.setCityState( new CityState( "Odessa", "WA" ) );
 		s.save(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 		xam.setPreferredCourse( course );
 		xam.setCityState( new CityState( "Odessa", "WA" ) );
 		s.save(xam);
 
 		Enrolment enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 1);
 		enrolment.setYear((short) 1999);
 		enrolment.setStudent(xam);
 		enrolment.setStudentNumber(xam.getStudentNumber());
 		xam.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 3);
 		enrolment.setYear((short) 1998);
 		enrolment.setStudent(gavin);
 		enrolment.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		s.flush();
 
 		Object result = s.createCriteria( Student.class )
 			.setProjection( Projections.distinct( Property.forName( "cityState" ) ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		result = s.createCriteria( Student.class )
 			.setProjection( Projections.distinct( Property.forName( "cityState" ).as( "cityState" ) ) )
 				.addOrder( Order.asc( "cityState" ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		result = s.createCriteria( Student.class )
 			.setProjection( Projections.count( "cityState.city" ) )
 			.uniqueResult();
 		assertEquals( 2, ( ( Long ) result ).longValue() );
 
 		result = s.createCriteria( Student.class )
 			.setProjection( Projections.countDistinct( "cityState.city" ) )
 			.uniqueResult();
 		assertEquals( 1, ( ( Long ) result ).longValue() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		try {
 			result = s.createCriteria( Student.class )
 				.setProjection( Projections.count( "cityState" ) )
 				.uniqueResult();
 			if ( ! getDialect().supportsTupleCounts() ) {
 				fail( "expected SQLGrammarException" );
 			}
 			assertEquals( 1, ( ( Long ) result ).longValue() );
 		}
 		catch ( SQLGrammarException ex ) {
 			if ( ! getDialect().supportsTupleCounts() ) {
 				// expected
 			}
 			else {
 				throw ex;
 			}
 		}
 		finally {
 			t.rollback();
 			s.close();
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		try {
 			result = s.createCriteria( Student.class )
 					.setProjection( Projections.countDistinct( "cityState" ) )
 					.uniqueResult();
 			if ( ! getDialect().supportsTupleDistinctCounts() ) {
 				fail( "expected SQLGrammarException" );
 			}
 			assertEquals( 1, ( ( Long ) result ).longValue() );
 		}
 		catch ( SQLGrammarException ex ) {
 			if ( ! getDialect().supportsTupleDistinctCounts() ) {
 				// expected
 			}
 			else {
 				throw ex;
 			}
 		}
 		finally {
 			t.rollback();
 			s.close();
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete(gavin);
 		s.delete(xam);
 		s.delete(course);
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testGroupByComponent() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		s.save(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(667);
 		gavin.setCityState( new CityState( "Odessa", "WA" ) );
 		s.save(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 		xam.setPreferredCourse( course );
 		xam.setCityState( new CityState( "Odessa", "WA" ) );
 		s.save(xam);
 
 		Enrolment enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 1);
 		enrolment.setYear((short) 1999);
 		enrolment.setStudent(xam);
 		enrolment.setStudentNumber(xam.getStudentNumber());
 		xam.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 3);
 		enrolment.setYear((short) 1998);
 		enrolment.setStudent(gavin);
 		enrolment.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		s.flush();
 
 		Object result = s.createCriteria( Student.class )
 			.setProjection( Projections.groupProperty( "cityState" ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		result = s.createCriteria( Student.class, "st")
 			.setProjection( Projections.groupProperty( "st.cityState" ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		result = s.createCriteria( Student.class, "st")
 			.setProjection( Projections.groupProperty( "st.cityState" ) )
 				.addOrder( Order.asc( "cityState" ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		result = s.createCriteria( Student.class, "st")
 			.setProjection( Projections.groupProperty( "st.cityState" ).as( "cityState" ) )
 				.addOrder( Order.asc( "cityState" ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		result = s.createCriteria( Student.class, "st")
 			.setProjection( Projections.groupProperty( "st.cityState" ).as( "cityState" ) )
 				.addOrder( Order.asc( "cityState" ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		result = s.createCriteria( Student.class, "st")
 			.setProjection( Projections.groupProperty( "st.cityState" ).as( "cityState" ) )
 			.add( Restrictions.eq( "st.cityState", new CityState( "Odessa", "WA" ) ) )
 			.addOrder( Order.asc( "cityState" ) )
 			.uniqueResult();
 		assertTrue( result instanceof CityState );
 		assertEquals( ( ( CityState ) result ).getCity(), "Odessa" );
 		assertEquals( ( ( CityState ) result ).getState(), "WA" );
 
 		List list = s.createCriteria(Enrolment.class)
 			.createAlias("student", "st")
 			.createAlias("course", "co")
 			.setProjection( Projections.projectionList()
 					.add( Property.forName("co.courseCode").group() )
 					.add( Property.forName("st.cityState").group() )
 					.add( Property.forName("year").group() )
 			)
 			.list();
 
 
 		s.delete(gavin);
 		s.delete(xam);
 		s.delete(course);
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testRestrictionOnSubclassCollection() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		s.createCriteria( Reptile.class )
 				.add( Restrictions.isEmpty( "offspring" ) )
 				.list();
 
 		s.createCriteria( Reptile.class )
 				.add( Restrictions.isNotEmpty( "offspring" ) )
 				.list();
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testClassProperty() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// HQL: from Animal a where a.mother.class = Reptile
 		Criteria c = s.createCriteria(Animal.class,"a")
 			.createAlias("mother","m")
 			.add( Property.forName("m.class").eq(Reptile.class) );
 		c.list();
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testClassProperty2() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 		GreatFoo foo = new GreatFoo();
 		Bar b = new Bar();
 		b.setMyFoo(foo);
 		foo.setId(1);
 		b.setId(1);
 		session.persist(b);
 		session.flush();
 		t.commit();
 		session=openSession();
 		t=session.beginTransaction();
 		// OK, one BAR in DB
 		assertEquals(1, session.createCriteria(Bar.class).list().size());
 		Criteria crit = session.createCriteria(Bar.class, "b").createAlias(
 				"myFoo", "m").add(
 				Property.forName("m.class").eq(GreatFoo.class));
 		assertEquals(1, crit.list().size());
 		crit = session.createCriteria(Bar.class, "b").createAlias("myFoo", "m")
 				.add(Restrictions.eq("m.class", GreatFoo.class));
 		assertEquals(1, crit.list().size());
 		t.commit();
 		session.close();
 	}
 
 	@Test
 	public void testProjectedId() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createCriteria(Course.class).setProjection( Projections.property("courseCode") ).list();
 		s.createCriteria(Course.class).setProjection( Projections.id() ).list();
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testProjectedEmbeddedCompositeId() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		s.save(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(667);
 		s.save(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 		s.save(xam);
 
 		Enrolment enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 1);
 		enrolment.setYear((short) 1999);
 		enrolment.setStudent(xam);
 		enrolment.setStudentNumber(xam.getStudentNumber());
 		xam.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 3);
 		enrolment.setYear((short) 1998);
 		enrolment.setStudent(gavin);
 		enrolment.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		s.flush();
 
 		List enrolments = ( List ) s.createCriteria( Enrolment.class).setProjection( Projections.id() ).list();
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testProjectedCompositeId() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		course.getCourseMeetings().add( new CourseMeeting( course, "Monday", 1, "1313 Mockingbird Lane" ) );
 		s.save(course);
 		s.flush();
 		s.clear();
 		List data = ( List ) s.createCriteria( CourseMeeting.class).setProjection( Projections.id() ).list();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		try {
 			s.createCriteria( CourseMeeting.class).setProjection( Projections.count( "id" ) ).list();
 			fail( "should have thrown SQLGrammarException" );
 		}
 		catch ( SQLGrammarException ex ) {
 			// expected
 		}
 		finally {
 			t.rollback();
 			s.close();
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		try {
 			s.createCriteria( CourseMeeting.class).setProjection( Projections.countDistinct( "id" ) ).list();
 			if ( ! getDialect().supportsTupleDistinctCounts() ) {
 				fail( "expected SQLGrammarException" );
 			}
 		}
 		catch ( SQLGrammarException ex ) {
 			if ( ! getDialect().supportsTupleDistinctCounts() ) {
 				// expected
 			}
 			else {
 				throw ex;
 			}
 		}
 		finally {
 			t.rollback();
 			s.close();
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( course );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProjectedCompositeIdWithAlias() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		course.getCourseMeetings().add( new CourseMeeting( course, "Monday", 1, "1313 Mockingbird Lane" ) );
 		s.save(course);
 		s.flush();
 
 		List data = ( List ) s.createCriteria( CourseMeeting.class).setProjection( Projections.id().as( "id" ) ).list();
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testProjectedComponent() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Student gaith = new Student();
 		gaith.setName("Gaith Bell");
 		gaith.setStudentNumber(123);
 		gaith.setCityState( new CityState( "Chicago", "Illinois" ) );
 		s.save( gaith );
 		s.flush();
 
 		List cityStates = ( List ) s.createCriteria( Student.class).setProjection( Projections.property( "cityState" )).list();
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testProjectedListIncludesComponent() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Student gaith = new Student();
 		gaith.setName("Gaith Bell");
 		gaith.setStudentNumber(123);
 		gaith.setCityState( new CityState( "Chicago", "Illinois" ) );
 		s.save(gaith);
 		s.flush();
 		List data = ( List ) s.createCriteria( Student.class)
 				.setProjection( Projections.projectionList()
 					.add( Projections.property( "cityState" ) )
 					.add( Projections.property("name") ) )
 				.list();
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testProjectedListIncludesEmbeddedCompositeId() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		s.save(course);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(667);
 		s.save(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 		s.save(xam);
 
 		Enrolment enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 1);
 		enrolment.setYear((short) 1999);
 		enrolment.setStudent(xam);
 		enrolment.setStudentNumber(xam.getStudentNumber());
 		xam.getEnrolments().add(enrolment);
 		s.save(enrolment);
 
 		enrolment = new Enrolment();
 		enrolment.setCourse(course);
 		enrolment.setCourseCode(course.getCourseCode());
 		enrolment.setSemester((short) 3);
 		enrolment.setYear((short) 1998);
 		enrolment.setStudent(gavin);
 		enrolment.setStudentNumber(gavin.getStudentNumber());
 		gavin.getEnrolments().add(enrolment);
 		s.save(enrolment);
 		s.flush();
 		List data = ( List ) s.createCriteria( Enrolment.class)
 				.setProjection( Projections.projectionList()
 					.add( Projections.property( "semester" ) )
 					.add( Projections.property("year") )
 					.add( Projections.id() ) )
 				.list();
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testSubcriteriaJoinTypes() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Course courseA = new Course();
 		courseA.setCourseCode("HIB-A");
 		courseA.setDescription("Hibernate Training A");
 		session.persist(courseA);
 
 		Course courseB = new Course();
 		courseB.setCourseCode("HIB-B");
 		courseB.setDescription("Hibernate Training B");
 		session.persist(courseB);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(232);
 		gavin.setPreferredCourse(courseA);
 		session.persist(gavin);
 
 		Student leonardo = new Student();
 		leonardo.setName("Leonardo Quijano");
 		leonardo.setStudentNumber(233);
 		leonardo.setPreferredCourse(courseB);
 		session.persist(leonardo);
 
 		Student johnDoe = new Student();
 		johnDoe.setName("John Doe");
 		johnDoe.setStudentNumber(235);
 		johnDoe.setPreferredCourse(null);
 		session.persist(johnDoe);
 
 		List result = session.createCriteria( Student.class )
 				.setProjection( Property.forName("preferredCourse.courseCode") )
 				.createCriteria( "preferredCourse", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "courseCode" ) )
 						.list();
 		assertEquals( 3, result.size() );
 		// can't be sure of NULL comparison ordering aside from they should
 		// either come first or last
 		if ( result.get( 0 ) == null ) {
 			assertEquals( "HIB-A", result.get(1) );
 			assertEquals( "HIB-B", result.get(2) );
 		}
 		else {
 			assertNull( result.get(2) );
 			assertEquals( "HIB-A", result.get(0) );
 			assertEquals( "HIB-B", result.get(1) );
 		}
 
 		result = session.createCriteria( Student.class )
 				.setFetchMode( "preferredCourse", FetchMode.JOIN )
 				.createCriteria( "preferredCourse", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "courseCode" ) )
 						.list();
 		assertEquals( 3, result.size() );
 		assertNotNull( result.get(0) );
 		assertNotNull( result.get(1) );
 		assertNotNull( result.get(2) );
 
 		result = session.createCriteria( Student.class )
 				.setFetchMode( "preferredCourse", FetchMode.JOIN )
 				.createAlias( "preferredCourse", "pc", Criteria.LEFT_JOIN )
 				.addOrder( Order.asc( "pc.courseCode" ) )
 				.list();
 		assertEquals( 3, result.size() );
 		assertNotNull( result.get(0) );
 		assertNotNull( result.get(1) );
 		assertNotNull( result.get(2) );
 
 		session.delete(gavin);
 		session.delete(leonardo);
 		session.delete(johnDoe);
 		session.delete(courseA);
 		session.delete(courseB);
 		t.commit();
 		session.close();
 	}
 	
 	@Test
 	public void testAliasJoinCriterion() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Course courseA = new Course();
 		courseA.setCourseCode("HIB-A");
 		courseA.setDescription("Hibernate Training A");
 		session.persist(courseA);
 
 		Course courseB = new Course();
 		courseB.setCourseCode("HIB-B");
 		courseB.setDescription("Hibernate Training B");
 		session.persist(courseB);
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(232);
 		gavin.setPreferredCourse(courseA);
 		session.persist(gavin);
 
 		Student leonardo = new Student();
 		leonardo.setName("Leonardo Quijano");
 		leonardo.setStudentNumber(233);
 		leonardo.setPreferredCourse(courseB);
 		session.persist(leonardo);
 
 		Student johnDoe = new Student();
 		johnDoe.setName("John Doe");
 		johnDoe.setStudentNumber(235);
 		johnDoe.setPreferredCourse(null);
 		session.persist(johnDoe);
 
 		// test == on one value exists
 		List result = session.createCriteria( Student.class )
 			.createAlias( "preferredCourse", "pc", Criteria.LEFT_JOIN, Restrictions.eq("pc.courseCode", "HIB-A") )
 			.setProjection( Property.forName("pc.courseCode") )
 			.addOrder(Order.asc("pc.courseCode"))
 			.list();
 
 		assertEquals( 3, result.size() );
 
 		// can't be sure of NULL comparison ordering aside from they should
 		// either come first or last
 		if ( result.get( 0 ) == null ) {
 			assertNull(result.get(1));
 			assertEquals( "HIB-A", result.get(2) );
 		}
 		else {
 			assertNull( result.get(2) );
 			assertNull( result.get(1) );
 			assertEquals( "HIB-A", result.get(0) );
 		}
 
 		// test == on non existent value
 		result = session.createCriteria( Student.class )
 		.createAlias( "preferredCourse", "pc", Criteria.LEFT_JOIN, Restrictions.eq("pc.courseCode", "HIB-R") )
 		.setProjection( Property.forName("pc.courseCode") )
 		.addOrder(Order.asc("pc.courseCode"))
 		.list();
 
 		assertEquals( 3, result.size() );
 		assertNull( result.get(2) );
 		assertNull( result.get(1) );
 		assertNull(result.get(0) );
 
 		// test != on one existing value
 		result = session.createCriteria( Student.class )
 		.createAlias( "preferredCourse", "pc", Criteria.LEFT_JOIN, Restrictions.ne("pc.courseCode", "HIB-A") )
 		.setProjection( Property.forName("pc.courseCode") )
 		.addOrder(Order.asc("pc.courseCode"))
 		.list();
 
 		assertEquals( 3, result.size() );
 		// can't be sure of NULL comparison ordering aside from they should
 		// either come first or last
 		if ( result.get( 0 ) == null ) {
 			assertNull( result.get(1) );
 			assertEquals( "HIB-B", result.get(2) );
 		}
 		else {
 			assertEquals( "HIB-B", result.get(0) );
 			assertNull( result.get(1) );
 			assertNull( result.get(2) );
 		}
 
 		session.delete(gavin);
 		session.delete(leonardo);
 		session.delete(johnDoe);
 		session.delete(courseA);
 		session.delete(courseB);
 		t.commit();
 		session.close();
 	}
 
         @Test
 	public void testCriteriaCollectionOfValue() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Course course = new Course();
 		course.setCourseCode("HIB");
 		course.setDescription("Hibernate Training");
 		Set crossListedAs = new HashSet();
 		crossListedAs.add("Java Persistence 101");
 		crossListedAs.add("CS101");
 		course.setCrossListedAs(crossListedAs);
 		session.persist(course);
 		session.flush();
 		session.clear();
 		List results = session.createCriteria(Course.class)
 		    .createCriteria("crossListedAs")
 		    .add(Restrictions.eq("elements", "CS101"))
 		    .list();
 
 		assertEquals( 1, results.size() );
 		course = (Course)results.get(0);
 		assertEquals( 2, course.getCrossListedAs().size() );
 
 		session.delete(course);
 		
 		t.commit();
 		session.close();
 		
 	}
 
         @Test
 	public void testCriteriaCollectionOfComponent() {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 
 		Student gavin = new Student();
 		gavin.setName("Gavin King");
 		gavin.setStudentNumber(232);
 
 		Map addresses = new HashMap();
 		StudentAddress addr = new StudentAddress();
 		addr.setLine1("101 Main St.");
 		addr.setCity("Anytown");
 		addr.setState("NY");
 		addr.setZip("10016");
 		addresses.put("HOME", addr);
 		
 		addr = new StudentAddress();
 		addr.setLine1("202 Spring St.");
 		addr.setCity("Springfield");
 		addr.setState("MA");
 		addr.setZip("99999");
 		addresses.put("SCHOOL", addr);
 	       
 		gavin.setAddresses(addresses);
 		session.persist(gavin);
 
 		Student xam = new Student();
 		xam.setName("Max Rydahl Andersen");
 		xam.setStudentNumber(101);
 
 		addresses = new HashMap();
 		addr = new StudentAddress();
 		addr.setLine1("123 3rd Ave");
 		addr.setCity("New York");
 		addr.setState("NY");
 		addr.setZip("10004");
 		addresses.put("HOME", addr);
 
 		xam.setAddresses(addresses);
 		session.persist(xam);
 
 		session.flush();
 		session.clear();
 
 		// search on a component property
 		List results = session.createCriteria(Student.class)
 		    .createCriteria("addresses")
 		    .add(Restrictions.eq("state", "MA"))
 		    .list();
 
 		assertEquals(1, results.size());
 
 		gavin = (Student)results.get(0);
 		assertEquals(2, gavin.getAddresses().keySet().size());
 
 		session.delete(gavin);
 		session.delete(xam);
 		
 		t.commit();
 		session.close();
 		
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java b/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java
index e2fdc9b148..3e5d6226ef 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cut/MonetoryAmountUserType.java
@@ -1,102 +1,103 @@
-//$Id: MonetoryAmountUserType.java 6235 2005-03-29 03:17:49Z oneovthafew $
 package org.hibernate.test.cut;
+
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Currency;
-import org.hibernate.Hibernate;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.usertype.CompositeUserType;
 
 /**
  * @author Gavin King
  */
 public class MonetoryAmountUserType implements CompositeUserType {
 
 	public String[] getPropertyNames() {
 		return new String[] { "amount", "currency" };
 	}
 
 	public Type[] getPropertyTypes() {
-		return new Type[] { Hibernate.BIG_DECIMAL, Hibernate.CURRENCY };
+		return new Type[] { StandardBasicTypes.BIG_DECIMAL, StandardBasicTypes.CURRENCY };
 	}
 
 	public Object getPropertyValue(Object component, int property) throws HibernateException {
 		MonetoryAmount ma = (MonetoryAmount) component;
-		return property==0 ? (Object) ma.getAmount() : (Object) ma.getCurrency();
+		return property==0 ? ma.getAmount() : ma.getCurrency();
 	}
 
 	public void setPropertyValue(Object component, int property, Object value)
 			throws HibernateException {
 		MonetoryAmount ma = (MonetoryAmount) component;
 		if ( property==0 ) {
 			ma.setAmount( (BigDecimal) value );
 		}
 		else {
 			ma.setCurrency( (Currency) value );
 		}
 	}
 
 	public Class returnedClass() {
 		return MonetoryAmount.class;
 	}
 
 	public boolean equals(Object x, Object y) throws HibernateException {
 		if (x==y) return true;
 		if (x==null || y==null) return false;
 		MonetoryAmount mx = (MonetoryAmount) x;
 		MonetoryAmount my = (MonetoryAmount) y;
 		return mx.getAmount().equals( my.getAmount() ) &&
 			mx.getCurrency().equals( my.getCurrency() );
 	}
 
 	public int hashCode(Object x) throws HibernateException {
 		return ( (MonetoryAmount) x ).getAmount().hashCode();
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
-		BigDecimal amt = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet( rs, names[0], session );
-		Currency cur = (Currency) Hibernate.CURRENCY.nullSafeGet( rs, names[1], session );
+		BigDecimal amt = StandardBasicTypes.BIG_DECIMAL.nullSafeGet( rs, names[0], session );
+		Currency cur = StandardBasicTypes.CURRENCY.nullSafeGet( rs, names[1], session );
 		if (amt==null) return null;
 		return new MonetoryAmount(amt, cur);
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		MonetoryAmount ma = (MonetoryAmount) value;
 		BigDecimal amt = ma == null ? null : ma.getAmount();
 		Currency cur = ma == null ? null : ma.getCurrency();
-		Hibernate.BIG_DECIMAL.nullSafeSet(st, amt, index, session);
-		Hibernate.CURRENCY.nullSafeSet(st, cur, index+1, session);
+		StandardBasicTypes.BIG_DECIMAL.nullSafeSet(st, amt, index, session);
+		StandardBasicTypes.CURRENCY.nullSafeSet(st, cur, index+1, session);
 	}
 
 	public Object deepCopy(Object value) throws HibernateException {
 		MonetoryAmount ma = (MonetoryAmount) value;
 		return new MonetoryAmount( ma.getAmount(), ma.getCurrency() );
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session)
 			throws HibernateException {
 		return (Serializable) deepCopy(value);
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return deepCopy(cached);
 	}
 
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return deepCopy(original); //TODO: improve
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java b/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java
index 6182a14873..62cd897973 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ClassificationType.java
@@ -1,102 +1,102 @@
-package org.hibernate.test.hql;
-
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import org.hibernate.Hibernate;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.type.IntegerType;
-import org.hibernate.usertype.EnhancedUserType;
-
-/**
- * A custom type for mapping {@link org.hibernate.test.hql.Classification} instances
- * to the respective db column.
- * </p>
- * THis is largely intended to mimic JDK5 enum support in JPA.  Here we are
- * using the approach of storing the ordinal values, rather than the names.
- *
- * @author Steve Ebersole
- */
-public class ClassificationType implements EnhancedUserType {
-
-	public int[] sqlTypes() {
-		return new int[] { Types.TINYINT };
-	}
-
-	public Class returnedClass() {
-		return Classification.class;
-	}
-
-	public boolean equals(Object x, Object y) throws HibernateException {
-		if ( x == null && y == null ) {
-			return false;
-		}
-		else if ( x != null ) {
-			return x.equals( y );
-		}
-		else {
-			return y.equals( x );
-		}
-	}
-
-	public int hashCode(Object x) throws HibernateException {
-		return x.hashCode();
-	}
-
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
-		Integer ordinal = IntegerType.INSTANCE.nullSafeGet( rs, names[0], session );
-		return Classification.valueOf( ordinal );
-	}
-
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
-		Integer ordinal = value == null ? null : new Integer( ( ( Classification ) value ).ordinal() );
-		Hibernate.INTEGER.nullSafeSet( st, ordinal, index, session );
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
-		return ( Classification ) value;
-	}
-
-	public Object assemble(Serializable cached, Object owner) throws HibernateException {
-		return cached;
-	}
-
-	public Object replace(Object original, Object target, Object owner) throws HibernateException {
-		return original;
-	}
-
-	public String objectToSQLString(Object value) {
-		return extractOrdinalString( value );
-	}
-
-	public String toXMLString(Object value) {
-		return extractName( value );
-	}
-
-	public Object fromXMLString(String xmlValue) {
-		return Classification.valueOf( xmlValue );
-	}
-
-	private String extractName(Object obj) {
-		return ( ( Classification ) obj ).name();
-	}
-
-	private int extractOrdinal(Object value) {
-		return ( ( Classification ) value ).ordinal();
-	}
-
-	private String extractOrdinalString(Object value) {
-		return Integer.toString( extractOrdinal( value ) );
-	}
-}
+package org.hibernate.test.hql;
+
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.usertype.EnhancedUserType;
+
+/**
+ * A custom type for mapping {@link org.hibernate.test.hql.Classification} instances
+ * to the respective db column.
+ * </p>
+ * THis is largely intended to mimic JDK5 enum support in JPA.  Here we are
+ * using the approach of storing the ordinal values, rather than the names.
+ *
+ * @author Steve Ebersole
+ */
+public class ClassificationType implements EnhancedUserType {
+
+	public int[] sqlTypes() {
+		return new int[] { Types.TINYINT };
+	}
+
+	public Class returnedClass() {
+		return Classification.class;
+	}
+
+	public boolean equals(Object x, Object y) throws HibernateException {
+		if ( x == null && y == null ) {
+			return false;
+		}
+		else if ( x != null ) {
+			return x.equals( y );
+		}
+		else {
+			return y.equals( x );
+		}
+	}
+
+	public int hashCode(Object x) throws HibernateException {
+		return x.hashCode();
+	}
+
+	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+		Integer ordinal = StandardBasicTypes.INTEGER.nullSafeGet( rs, names[0], session );
+		return Classification.valueOf( ordinal );
+	}
+
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+		Integer ordinal = value == null ? null : new Integer( ( ( Classification ) value ).ordinal() );
+		StandardBasicTypes.INTEGER.nullSafeSet( st, ordinal, index, session );
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
+		return ( Classification ) value;
+	}
+
+	public Object assemble(Serializable cached, Object owner) throws HibernateException {
+		return cached;
+	}
+
+	public Object replace(Object original, Object target, Object owner) throws HibernateException {
+		return original;
+	}
+
+	public String objectToSQLString(Object value) {
+		return extractOrdinalString( value );
+	}
+
+	public String toXMLString(Object value) {
+		return extractName( value );
+	}
+
+	public Object fromXMLString(String xmlValue) {
+		return Classification.valueOf( xmlValue );
+	}
+
+	private String extractName(Object obj) {
+		return ( ( Classification ) obj ).name();
+	}
+
+	private int extractOrdinal(Object value) {
+		return ( ( Classification ) value ).ordinal();
+	}
+
+	private String extractOrdinalString(Object value) {
+		return Integer.toString( extractOrdinal( value ) );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
index aee346a83b..47cbb35d91 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/domain/CustomBlobType.java
@@ -1,109 +1,110 @@
-package org.hibernate.test.instrument.domain;
-
-import java.io.Serializable;
-import java.sql.PreparedStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.Arrays;
-import org.hibernate.Hibernate;
-import org.hibernate.HibernateException;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.usertype.UserType;
-
-/**
- * A simple byte[]-based custom type.
- */
-public class CustomBlobType implements UserType {
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object nullSafeGet(ResultSet rs, String names[], SessionImplementor session, Object owner) throws SQLException {
-		// cast just to make sure...
-		return Hibernate.BINARY.nullSafeGet( rs, names[0], session );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void nullSafeSet(PreparedStatement ps, Object value, int index, SessionImplementor session) throws SQLException, HibernateException {
-		// cast just to make sure...
-		Hibernate.BINARY.nullSafeSet( ps, value, index, session );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object deepCopy(Object value) {
-		byte result[] = null;
-
-		if ( value != null ) {
-			byte bytes[] = ( byte[] ) value;
-
-			result = new byte[bytes.length];
-			System.arraycopy( bytes, 0, result, 0, bytes.length );
-		}
-
-		return result;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isMutable() {
-		return true;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int[] sqlTypes() {
-		return new int[] { Types.VARBINARY };
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class returnedClass() {
-		return byte[].class;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean equals(Object x, Object y) {
-		return Arrays.equals( ( byte[] ) x, ( byte[] ) y );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object assemble(Serializable arg0, Object arg1)
-			throws HibernateException {
-		return null;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Serializable disassemble(Object arg0)
-			throws HibernateException {
-		return null;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public int hashCode(Object arg0)
-			throws HibernateException {
-		return 0;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Object replace(Object arg0, Object arg1, Object arg2)
-			throws HibernateException {
-		return null;
-	}
+package org.hibernate.test.instrument.domain;
+
+import java.io.Serializable;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.Arrays;
+import org.hibernate.Hibernate;
+import org.hibernate.HibernateException;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.usertype.UserType;
+
+/**
+ * A simple byte[]-based custom type.
+ */
+public class CustomBlobType implements UserType {
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object nullSafeGet(ResultSet rs, String names[], SessionImplementor session, Object owner) throws SQLException {
+		// cast just to make sure...
+		return StandardBasicTypes.BINARY.nullSafeGet( rs, names[0], session );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void nullSafeSet(PreparedStatement ps, Object value, int index, SessionImplementor session) throws SQLException, HibernateException {
+		// cast just to make sure...
+		StandardBasicTypes.BINARY.nullSafeSet( ps, value, index, session );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object deepCopy(Object value) {
+		byte result[] = null;
+
+		if ( value != null ) {
+			byte bytes[] = ( byte[] ) value;
+
+			result = new byte[bytes.length];
+			System.arraycopy( bytes, 0, result, 0, bytes.length );
+		}
+
+		return result;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isMutable() {
+		return true;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public int[] sqlTypes() {
+		return new int[] { Types.VARBINARY };
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class returnedClass() {
+		return byte[].class;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean equals(Object x, Object y) {
+		return Arrays.equals( ( byte[] ) x, ( byte[] ) y );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object assemble(Serializable arg0, Object arg1)
+			throws HibernateException {
+		return null;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Serializable disassemble(Object arg0)
+			throws HibernateException {
+		return null;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public int hashCode(Object arg0)
+			throws HibernateException {
+		return 0;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object replace(Object arg0, Object arg1, Object arg2)
+			throws HibernateException {
+		return null;
+	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index 861d28230f..dcafa88897 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,674 +1,674 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
+
 import org.hibernate.EntityMode;
-import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TwoPhaseLoad;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.sql.QuerySelect;
 import org.hibernate.sql.Select;
 import org.hibernate.tuple.entity.EntityMetamodel;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping mapping) {
 		this.factory = factory;
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	private void checkEntityMode(EntityMode entityMode) {
 		if ( EntityMode.POJO != entityMode ) {
 			throw new IllegalArgumentException( "Unhandled EntityMode : " + entityMode );
 		}
 	}
 
 	private void checkEntityMode(SessionImplementor session) {
 		checkEntityMode( session.getEntityMode() );
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return new Boolean( ( (Custom) object ).id==null );
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 	throws HibernateException {
 		return getPropertyValues( object, session.getEntityMode() );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Class getMappedClass(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return Custom.class;
 	}
 
 	public boolean implementsLifecycle(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return false;
 	}
 
 	public boolean implementsValidatable(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return false;
 	}
 
 	public Class getConcreteProxyClass(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return Custom.class;
 	}
 
 	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		setPropertyValue( object, 0, values[0], entityMode );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		( (Custom) object ).setName( (String) value );
 	}
 
 	public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return ( (Custom) object ).getName();
 	}
 
 	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return ( (Custom) object ).getName();
 	}
 
 	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return ( (Custom) object ).id;
 	}
 
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		checkEntityMode( session );
 		return ( (Custom) entity ).id;
 	}
 
 	public void setIdentifier(Object object, Serializable id, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		( (Custom) object ).id = (String) id;
 	}
 
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		checkEntityMode( session );
 		( (Custom) entity ).id = (String) id;
 	}
 
 	public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return null;
 	}
 
 	public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return instantiate( id );
 	}
 
 	private Object instantiate(Serializable id) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		checkEntityMode( session );
 		return instantiate( id );
 	}
 
 	public boolean isInstance(Object object, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return object instanceof Custom;
 	}
 
 	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return false;
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		checkEntityMode( session );
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session
 	) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session
 	) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session ),
 					new PostLoadEvent( (EventSource) session )
 				);
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
-	private static final Type[] TYPES = new Type[] { Hibernate.STRING };
+	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
-		return Hibernate.STRING;
+		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object getPropertyValue(Object object, String propertyName)
 		throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	public EntityMode guessEntityMode(Object object) {
 		if ( !isInstance(object, EntityMode.POJO) ) {
 			return null;
 		}
 		else {
 			return EntityMode.POJO;
 		}
 	}
 
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	public boolean isDynamic() {
 		return false;
 	}
 
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	public void applyFilters(QuerySelect select, String alias, Map filters) {
 	}
 
 	public void applyFilters(Select select, String alias, Map filters) {
 	}
 
 
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return null;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return new UnstructuredCacheEntry();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	public Type[] getNaturalIdentifierTypes() {
 		return null;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	public boolean isInstrumented(EntityMode entityMode) {
 		return false;
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	public boolean hasGeneratedProperties() {
 		return false;
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	public String[] getOrphanRemovalOneToOnePaths() {
 		return null;
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
index 63f8ce0d3f..313261c080 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
@@ -1,4901 +1,4902 @@
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
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Time;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Set;
 import java.util.SortedSet;
 import java.util.TimeZone;
 import java.util.TreeMap;
 import java.util.TreeSet;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.LockMode;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.Example;
 import org.hibernate.criterion.MatchMode;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.DerbyDialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InterbaseDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PointbaseDialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SAPDBDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.Test;
 
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 public class FooBarTest extends LegacyTestCase {
 	private static final Logger log = Logger.getLogger( FooBarTest.class );
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
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
 			"legacy/Location.hbm.xml",
 			"legacy/Stuff.hbm.xml",
 			"legacy/Container.hbm.xml",
 			"legacy/Simple.hbm.xml",
 			"legacy/XY.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testSaveOrUpdateCopyAny() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		One one = new One();
 		bar.setObject(one);
 		s.save(bar);
 		GlarchProxy g = bar.getComponent().getGlarch();
 		bar.getComponent().setGlarch(null);
 		s.delete(g);
 		s.flush();
 		assertTrue( s.contains(one) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Bar bar2 = (Bar) s.merge( bar );
 		s.flush();
 		s.delete(bar2);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRefreshProxy() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g = new Glarch();
 		Serializable gid = s.save(g);
 		s.flush();
 		s.clear();
 		GlarchProxy gp = (GlarchProxy) s.load(Glarch.class, gid);
 		gp.getName(); //force init
 		s.refresh(gp);
 		s.delete(gp);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsCircularCascadeDeleteCheck.class,
 			comment = "db/dialect does not support circular cascade delete constraints"
 	)
 	public void testOnCascadeDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.subs = new ArrayList();
 		Baz sub = new Baz();
 		sub.superBaz = baz;
 		baz.subs.add(sub);
 		s.save(baz);
 		s.flush();
 		assertTrue( s.createQuery("from Baz").list().size()==2 );
 		s.getTransaction().commit();
 		s.beginTransaction();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.beginTransaction();
 		assertTrue( s.createQuery("from Baz").list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRemoveFromIdbag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setByteBag( new ArrayList() );
 		byte[] bytes = { 12, 13 };
 		baz.getByteBag().add( new byte[] { 10, 45 } );
 		baz.getByteBag().add(bytes);
 		baz.getByteBag().add( new byte[] { 1, 11 } );
 		baz.getByteBag().add( new byte[] { 12 } );
 		s.save(baz);
 		s.flush();
 		baz.getByteBag().remove(bytes);
 		s.flush();
 		baz.getByteBag().add(bytes);
 		s.flush();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save(q);
 		BarProxy b = new Bar();
 		s.save(b);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load(Qux.class, q.getKey() );
 		b = (BarProxy) s.load( Foo.class, b.getKey() );
 		b.getKey();
 		assertFalse( Hibernate.isInitialized(b) );
 		b.getBarString();
 		assertTrue( Hibernate.isInitialized(b) );
 		BarProxy b2 = (BarProxy) s.load( Bar.class, b.getKey() );
 		Qux q2 = (Qux) s.load( Qux.class, q.getKey() );
 		assertTrue( "loaded same object", q==q2 );
 		assertTrue( "loaded same object", b==b2 );
 		assertTrue( Math.round( b.getFormula() ) == b.getInt() / 2 );
 		s.delete(q2);
 		s.delete( b2 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testJoin() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		foo.setJoinedProp("foo");
 		s.save( foo );
 		s.flush();
 		foo.setJoinedProp("bar");
 		s.flush();
 		String fid = foo.getKey();
 		s.delete( foo );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo2 = new Foo();
 		foo2.setJoinedProp("foo");
 		s.save(foo2);
 		s.createQuery( "select foo.id from Foo foo where foo.joinedProp = 'foo'" ).list();
 		assertNull( s.get(Foo.class, fid) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testDereferenceLazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setFooSet( new HashSet() );
 		Foo foo = new Foo();
 		baz.getFooSet().add(foo);
 		s.save(foo);
 		s.save(baz);
 		foo.setBytes( "foobar".getBytes() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( Hibernate.isInitialized( foo.getBytes() ) );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooSet().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().evictCollection("org.hibernate.test.legacy.Baz.fooSet");
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		baz.setFooSet(null);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		assertTrue( baz.getFooSet().size()==0 );
 		s.delete(baz);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testMoveLazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Baz baz2 = new Baz();
 		baz.setFooSet( new HashSet() );
 		Foo foo = new Foo();
 		baz.getFooSet().add(foo);
 		s.save(foo);
 		s.save(baz);
 		s.save(baz2);
 		foo.setBytes( "foobar".getBytes() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( Hibernate.isInitialized( foo.getBytes() ) );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooSet().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().evictCollection("org.hibernate.test.legacy.Baz.fooSet");
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		baz2 = (Baz) s.get( Baz.class, baz2.getCode() );
 		baz2.setFooSet( baz.getFooSet() );
 		baz.setFooSet(null);
 		assertFalse( Hibernate.isInitialized( baz2.getFooSet() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (Foo) s.get( Foo.class, foo.getKey() );
 		assertTrue( foo.getBytes().length==6 );
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		baz2 = (Baz) s.get( Baz.class, baz2.getCode() );
 		assertFalse( Hibernate.isInitialized( baz.getFooSet() ) );
 		assertTrue( baz.getFooSet().size()==0 );
 		assertTrue( Hibernate.isInitialized( baz2.getFooSet() ) ); //fooSet has batching enabled
 		assertTrue( baz2.getFooSet().size()==1 );
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCriteriaCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz bb = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( bb == null );
 		Baz baz = new Baz();
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Baz b = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( Hibernate.isInitialized( b.getTopGlarchez() ) );
 		assertTrue( b.getTopGlarchez().size() == 0 );
 		s.delete( b );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQuery() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		foo.setFoo(foo2);
 
 		List list = s.createQuery( "from Foo foo inner join fetch foo.foo" ).list();
 		Foo foof = (Foo) list.get(0);
 		assertTrue( Hibernate.isInitialized( foof.getFoo() ) );
 
 		s.createQuery( "from Baz baz left outer join fetch baz.fooToGlarch" ).list();
 
 		list = s.createQuery( "select foo, bar from Foo foo left outer join foo.foo bar where foo = ?" )
-				.setParameter( 0, foo, Hibernate.entity(Foo.class) )
+				.setParameter( 0, foo, s.getTypeHelper().entity(Foo.class) )
 				.list();
 		Object[] row1 = (Object[]) list.get(0);
 		assertTrue( row1[0]==foo && row1[1]==foo2 );
 
 		s.createQuery( "select foo.foo.foo.string from Foo foo where foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.foo.string from Foo foo where foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo from Foo foo where foo.foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.foo.string from Foo foo where foo.foo.foo = 'bar'" ).list();
 		s.createQuery( "select foo.foo.foo.string from Foo foo where foo.foo.foo.foo.string = 'bar'" ).list();
 		if ( ! (getDialect() instanceof HSQLDialect) )
 			s.createQuery( "select foo.string from Foo foo where foo.foo.foo.foo = foo.foo.foo" ).list();
 		s.createQuery( "select foo.string from Foo foo where foo.foo.foo = 'bar' and foo.foo.foo.foo = 'baz'" ).list();
 		s.createQuery( "select foo.string from Foo foo where foo.foo.foo.foo.string = 'a' and foo.foo.string = 'b'" )
 				.list();
 
 		s.createQuery( "from Bar bar, foo in elements(bar.baz.fooArray)" ).list();
 
 		//s.find("from Baz as baz where baz.topComponents[baz].name = 'bazzz'");
 
 		if ( (getDialect() instanceof DB2Dialect) && !(getDialect() instanceof DerbyDialect) ) {
 			s.createQuery( "from Foo foo where lower( foo.foo.string ) = 'foo'" ).list();
 			s.createQuery( "from Foo foo where lower( (foo.foo.string || 'foo') || 'bar' ) = 'foo'" ).list();
 			s.createQuery( "from Foo foo where repeat( (foo.foo.string || 'foo') || 'bar', 2 ) = 'foo'" ).list();
 			s.createQuery(
 					"from Bar foo where foo.foo.integer is not null and repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'"
 			).list();
 			s.createQuery(
 					"from Bar foo where foo.foo.integer is not null or repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'"
 			).list();
 		}
 		if (getDialect() instanceof SybaseDialect) {
 			s.createQuery( "select baz from Baz as baz join baz.fooArray foo group by baz order by sum(foo.float)" )
 					.iterate();
 		}
 
 		s.createQuery( "from Foo as foo where foo.component.glarch.name is not null" ).list();
 		s.createQuery( "from Foo as foo left outer join foo.component.glarch as glarch where glarch.name = 'foo'" )
 				.list();
 
 		list = s.createQuery( "from Foo" ).list();
 		assertTrue( list.size()==2 && list.get(0) instanceof FooProxy );
 		list = s.createQuery( "from Foo foo left outer join foo.foo" ).list();
 		assertTrue( list.size()==2 && ( (Object[]) list.get(0) )[0] instanceof FooProxy );
 
 		s.createQuery("from Bar, Bar").list();
 		s.createQuery("from Foo, Bar").list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch, Bar bar join bar.foo" ).list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch join baz.fooSet" ).list();
 		s.createQuery( "from Baz baz left join baz.fooToGlarch join fetch baz.fooSet foo left join fetch foo.foo" )
 				.list();
 
 		list = s.createQuery(
 				"from Foo foo where foo.string='osama bin laden' and foo.boolean = true order by foo.string asc, foo.component.count desc"
 		).list();
 		assertTrue( "empty query", list.size()==0 );
 		Iterator iter = s.createQuery(
 				"from Foo foo where foo.string='osama bin laden' order by foo.string asc, foo.component.count desc"
 		).iterate();
 		assertTrue( "empty iterator", !iter.hasNext() );
 
 		list = s.createQuery( "select foo.foo from Foo foo" ).list();
 		assertTrue( "query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo.getFoo() );
 		foo.getFoo().setFoo(foo);
 		foo.setString("fizard");
 		//The following test is disabled for databases with no subselects...also for Interbase (not sure why).
 		if (
 				!(getDialect() instanceof MySQLDialect) &&
 				!(getDialect() instanceof HSQLDialect) &&
 				!(getDialect() instanceof MckoiDialect) &&
 				!(getDialect() instanceof SAPDBDialect) &&
 				!(getDialect() instanceof PointbaseDialect) &&
 				!(getDialect() instanceof DerbyDialect)
 		)  {
 			// && !db.equals("weblogic") {
 			if ( !( getDialect() instanceof InterbaseDialect ) ) {
 				list = s.createQuery( "from Foo foo where ? = some elements(foo.component.importantDates)" )
-						.setParameter( 0, new Date(), Hibernate.DATE )
+						.setParameter( 0, new Date(), StandardBasicTypes.DATE )
 						.list();
 				assertTrue( "component query", list.size()==2 );
 			}
 			if( !( getDialect() instanceof TimesTenDialect)) {
 				list = s.createQuery( "from Foo foo where size(foo.component.importantDates) = 3" ).list(); //WAS: 4
 				assertTrue( "component query", list.size()==2 );
 				list = s.createQuery( "from Foo foo where 0 = size(foo.component.importantDates)" ).list();
 				assertTrue( "component query", list.size()==0 );
 			}
 			list = s.createQuery( "from Foo foo where exists elements(foo.component.importantDates)" ).list();
 			assertTrue( "component query", list.size()==2 );
 			s.createQuery( "from Foo foo where not exists (from Bar bar where bar.id = foo.id)" ).list();
 
 			s.createQuery(
 					"select foo.foo from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)"
 			).list();
 			s.createQuery( "select foo.foo from Foo foo where foo = some(from Foo x where (x.long > foo.foo.long))" )
 					.list();
 			if ( !( getDialect() instanceof TimesTenDialect)) {
 				s.createQuery(
 						"select foo.foo from Foo foo where foo.long = some( select max(x.long) from Foo x where (x.long > foo.foo.long) group by x.foo )"
 				).list();
 			}
 			s.createQuery(
 					"from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long) and foo.foo.string='baz'"
 			).list();
 			s.createQuery(
 					"from Foo foo where foo.foo.string='baz' and foo = some(select x from Foo x where x.long > foo.foo.long)"
 			).list();
 			s.createQuery( "from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)" ).list();
 
 			s.createQuery(
 					"select foo.string, foo.date, foo.foo.string, foo.id from Foo foo, Baz baz where foo in elements(baz.fooArray) and foo.string like 'foo'"
 			).iterate();
 		}
 		list = s.createQuery( "from Foo foo where foo.component.count is null order by foo.component.count" ).list();
 		assertTrue( "component query", list.size()==0 );
 		list = s.createQuery( "from Foo foo where foo.component.name='foo'" ).list();
 		assertTrue( "component query", list.size()==2 );
 		list = s.createQuery(
 				"select distinct foo.component.name, foo.component.name from Foo foo where foo.component.name='foo'"
 		).list();
 		assertTrue( "component query", list.size()==1 );
 		list = s.createQuery( "select distinct foo.component.name, foo.id from Foo foo where foo.component.name='foo'" )
 				.list();
 		assertTrue( "component query", list.size()==2 );
 		list = s.createQuery( "select foo.foo from Foo foo" ).list();
 		assertTrue( "query", list.size()==2 );
 		list = s.createQuery( "from Foo foo where foo.id=?" )
-				.setParameter( 0, foo.getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "id query", list.size()==1 );
 		list = s.createQuery( "from Foo foo where foo.key=?" )
-				.setParameter( 0, foo.getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "named id query", list.size()==1 );
 		assertTrue( "id query", list.get(0)==foo );
 		list = s.createQuery( "select foo.foo from Foo foo where foo.string='fizard'" ).list();
 		assertTrue( "query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo.getFoo() );
 		list = s.createQuery( "from Foo foo where foo.component.subcomponent.name='bar'" ).list();
 		assertTrue( "components of components", list.size()==2 );
 		list = s.createQuery( "select foo.foo from Foo foo where foo.foo.id=?" )
-				.setParameter( 0, foo.getFoo().getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
 				.list();
 		assertTrue( "by id query", list.size()==1 );
 		assertTrue( "by id returned object", list.get(0)==foo.getFoo() );
 
-		s.createQuery( "from Foo foo where foo.foo = ?" ).setParameter( 0, foo.getFoo(), Hibernate.entity(Foo.class) ).list();
+		s.createQuery( "from Foo foo where foo.foo = ?" ).setParameter( 0, foo.getFoo(), s.getTypeHelper().entity(Foo.class) ).list();
 
 		assertTrue( !s.createQuery( "from Bar bar where bar.string='a string' or bar.string='a string'" )
 				.iterate()
 				.hasNext() );
 
 		iter = s.createQuery( "select foo.component.name, elements(foo.component.importantDates) from Foo foo where foo.foo.id=?" )
-				.setParameter( 0, foo.getFoo().getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getFoo().getKey(), StandardBasicTypes.STRING )
 				.iterate();
 		int i=0;
 		while ( iter.hasNext() ) {
 			i++;
 			Object[] row = (Object[]) iter.next();
 			assertTrue( row[0] instanceof String && ( row[1]==null || row[1] instanceof Date ) );
 		}
 		assertTrue(i==3); //WAS: 4
 		iter = s.createQuery( "select max( elements(foo.component.importantDates) ) from Foo foo group by foo.id" )
 				.iterate();
 		assertTrue( iter.next() instanceof Date );
 
 		list = s.createQuery(
 				"select foo.foo.foo.foo from Foo foo, Foo foo2 where"
 						+ " foo = foo2.foo and not not ( not foo.string='fizard' )"
 						+ " and foo2.string between 'a' and (foo.foo.string)"
 						+ ( ( getDialect() instanceof HSQLDialect || getDialect() instanceof InterbaseDialect || getDialect() instanceof TimesTenDialect ) ?
 						" and ( foo2.string in ( 'fiz', 'blah') or 1=1 )"
 						:
 						" and ( foo2.string in ( 'fiz', 'blah', foo.foo.string, foo.string, foo2.string ) )"
 				)
 		).list();
 		assertTrue( "complex query", list.size()==1 );
 		assertTrue( "returned object", list.get(0)==foo );
 		foo.setString("from BoogieDown  -tinsel town  =!@#$^&*())");
 		list = s.createQuery( "from Foo foo where foo.string='from BoogieDown  -tinsel town  =!@#$^&*())'" ).list();
 		assertTrue( "single quotes", list.size()==1 );
 		list = s.createQuery( "from Foo foo where not foo.string='foo''bar'" ).list();
 		assertTrue( "single quotes", list.size()==2 );
 		list = s.createQuery( "from Foo foo where foo.component.glarch.next is null" ).list();
 		assertTrue( "query association in component", list.size()==2 );
 		Bar bar = new Bar();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		bar.setBaz(baz);
 		baz.setManyToAny( new ArrayList() );
 		baz.getManyToAny().add(bar);
 		baz.getManyToAny().add(foo);
 		s.save(bar);
 		s.save(baz);
 		list = s.createQuery(
 				" from Bar bar where bar.baz.count=667 and bar.baz.count!=123 and not bar.baz.name='1-E-1'"
 		).list();
 		assertTrue( "query many-to-one", list.size()==1 );
 		list = s.createQuery( " from Bar i where i.baz.name='Bazza'" ).list();
 		assertTrue( "query many-to-one", list.size()==1 );
 
 		Iterator rs = s.createQuery( "select count(distinct foo.foo) from Foo foo" ).iterate();
 		assertTrue( "count", ( (Long) rs.next() ).longValue()==2 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(foo.foo.boolean) from Foo foo" ).iterate();
 		assertTrue( "count", ( (Long) rs.next() ).longValue()==2 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(*), foo.int from Foo foo group by foo.int" ).iterate();
 		assertTrue( "count(*) group by", ( (Object[]) rs.next() )[0].equals( new Long(3) ) );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select sum(foo.foo.int) from Foo foo" ).iterate();
 		assertTrue( "sum", ( (Long) rs.next() ).longValue()==4 );
 		assertTrue( !rs.hasNext() );
 		rs = s.createQuery( "select count(foo) from Foo foo where foo.id=?" )
-				.setParameter( 0, foo.getKey(), Hibernate.STRING )
+				.setParameter( 0, foo.getKey(), StandardBasicTypes.STRING )
 				.iterate();
 		assertTrue( "id query count", ( (Long) rs.next() ).longValue()==1 );
 		assertTrue( !rs.hasNext() );
 
 		s.createQuery( "from Foo foo where foo.boolean = ?" )
-				.setParameter( 0, new Boolean(true), Hibernate.BOOLEAN )
+				.setParameter( 0, new Boolean(true), StandardBasicTypes.BOOLEAN )
 				.list();
 
 		s.createQuery( "select new Foo(fo.x) from Fo fo" ).list();
 		s.createQuery( "select new Foo(fo.integer) from Foo fo" ).list();
 
 		list = s.createQuery("select new Foo(fo.x) from Foo fo")
 			//.setComment("projection test")
 			.setCacheable(true)
 			.list();
 		assertTrue(list.size()==3);
 		list = s.createQuery("select new Foo(fo.x) from Foo fo")
 			//.setComment("projection test 2")
 			.setCacheable(true)
 			.list();
 		assertTrue(list.size()==3);
 
 		rs = s.createQuery( "select new Foo(fo.x) from Foo fo" ).iterate();
 		assertTrue( "projection iterate (results)", rs.hasNext() );
 		assertTrue( "projection iterate (return check)", Foo.class.isAssignableFrom( rs.next().getClass() ) );
 
 		ScrollableResults sr = s.createQuery("select new Foo(fo.x) from Foo fo").scroll();
 		assertTrue( "projection scroll (results)", sr.next() );
 		assertTrue( "projection scroll (return check)", Foo.class.isAssignableFrom( sr.get(0).getClass() ) );
 
 		list = s.createQuery( "select foo.long, foo.component.name, foo, foo.foo from Foo foo" ).list();
 		rs = list.iterator();
 		int count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Long );
 			assertTrue( row[1] instanceof String );
 			assertTrue( row[2] instanceof Foo );
 			assertTrue( row[3] instanceof Foo );
 		}
 		assertTrue(count!=0);
 		list = s.createQuery( "select avg(foo.float), max(foo.component.name), count(distinct foo.id) from Foo foo" )
 				.list();
 		rs = list.iterator();
 		count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Double );
 			assertTrue( row[1] instanceof String );
 			assertTrue( row[2] instanceof Long );
 		}
 		assertTrue(count!=0);
 		list = s.createQuery( "select foo.long, foo.component, foo, foo.foo from Foo foo" ).list();
 		rs = list.iterator();
 		count=0;
 		while ( rs.hasNext() ) {
 			count++;
 			Object[] row = (Object[]) rs.next();
 			assertTrue( row[0] instanceof Long );
 			assertTrue( row[1] instanceof FooComponent );
 			assertTrue( row[2] instanceof Foo );
 			assertTrue( row[3] instanceof Foo );
 		}
 		assertTrue(count!=0);
 
 		s.save( new Holder("ice T") );
 		s.save( new Holder("ice cube") );
 
 		assertTrue( s.createQuery( "from java.lang.Object as o" ).list().size()==15 );
 		assertTrue( s.createQuery( "from Named" ).list().size()==7 );
 		assertTrue( s.createQuery( "from Named n where n.name is not null" ).list().size()==4 );
 		iter = s.createQuery( "from Named n" ).iterate();
 		while ( iter.hasNext() ) {
 			assertTrue( iter.next() instanceof Named );
 		}
 
 		s.save( new Holder("bar") );
 		iter = s.createQuery( "from Named n0, Named n1 where n0.name = n1.name" ).iterate();
 		int cnt = 0;
 		while ( iter.hasNext() ) {
 			Object[] row = (Object[]) iter.next();
 			if ( row[0]!=row[1] ) cnt++;
 		}
 		if ( !(getDialect() instanceof HSQLDialect) ) {
 			assertTrue(cnt==2);
 			assertTrue( s.createQuery( "from Named n0, Named n1 where n0.name = n1.name" ).list().size()==7 );
 		}
 
 		Query qu = s.createQuery("from Named n where n.name = :name");
 		qu.getReturnTypes();
 		qu.getNamedParameters();
 
 		iter = s.createQuery( "from java.lang.Object" ).iterate();
 		int c = 0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			c++;
 		}
 		assertTrue(c==16);
 
 		s.createQuery( "select baz.code, min(baz.count) from Baz baz group by baz.code" ).iterate();
 
 		iter = s.createQuery( "selecT baz from Baz baz where baz.stringDateMap['foo'] is not null or baz.stringDateMap['bar'] = ?" )
-				.setParameter( 0, new Date(), Hibernate.DATE )
+				.setParameter( 0, new Date(), StandardBasicTypes.DATE )
 				.iterate();
 		assertFalse( iter.hasNext() );
 		list = s.createQuery( "select baz from Baz baz where baz.stringDateMap['now'] is not null" ).list();
 		assertTrue( list.size()==1 );
 		list = s.createQuery(
 				"select baz from Baz baz where baz.stringDateMap['now'] is not null and baz.stringDateMap['big bang'] < baz.stringDateMap['now']"
 		).list();
 		assertTrue( list.size()==1 );
 		list = s.createQuery( "select index(date) from Baz baz join baz.stringDateMap date" ).list();
 		System.out.println(list);
 		assertTrue( list.size()==2 );
 
 		s.createQuery(
 				"from Foo foo where foo.integer not between 1 and 5 and foo.string not in ('cde', 'abc') and foo.string is not null and foo.integer<=3"
 		).list();
 
 		s.createQuery( "from Baz baz inner join baz.collectionComponent.nested.foos foo where foo.string is null" )
 				.list();
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof MckoiDialect) && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			s.createQuery(
 					"from Baz baz inner join baz.fooSet where '1' in (from baz.fooSet foo where foo.string is not null)"
 			).list();
 			s.createQuery(
 					"from Baz baz where 'a' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)"
 			).list();
 			s.createQuery(
 					"from Baz baz where 'b' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)"
 			).list();
 		}
 
 		s.createQuery( "from Foo foo join foo.foo where foo.foo in ('1','2','3')" ).list();
 		if ( !(getDialect() instanceof HSQLDialect) )
 			s.createQuery( "from Foo foo left join foo.foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo from Foo foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.string from Foo foo where foo.foo in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.string from Foo foo where foo.foo.string in ('1','2','3')" ).list();
 		s.createQuery( "select foo.foo.long from Foo foo where foo.foo.string in ('1','2','3')" ).list();
 		s.createQuery( "select count(*) from Foo foo where foo.foo.string in ('1','2','3') or foo.foo.long in (1,2,3)" )
 				.list();
 		s.createQuery( "select count(*) from Foo foo where foo.foo.string in ('1','2','3') group by foo.foo.long" )
 				.list();
 
 		s.createQuery( "from Foo foo1 left join foo1.foo foo2 left join foo2.foo where foo1.string is not null" )
 				.list();
 		s.createQuery( "from Foo foo1 left join foo1.foo.foo where foo1.string is not null" ).list();
 		s.createQuery( "from Foo foo1 left join foo1.foo foo2 left join foo1.foo.foo foo3 where foo1.string is not null" )
 				.list();
 
 		s.createQuery( "select foo.formula from Foo foo where foo.formula > 0" ).list();
 
 		int len = s.createQuery( "from Foo as foo join foo.foo as foo2 where foo2.id >'a' or foo2.id <'a'" ).list().size();
 		assertTrue(len==2);
 
 		for ( Object entity : s.createQuery( "from Holder" ).list() ) {
 			s.delete( entity );
 		}
 
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.createQuery("from Baz baz left outer join fetch baz.manyToAny").uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getManyToAny() ) );
 		assertTrue( baz.getManyToAny().size()==2 );
 		BarProxy barp = (BarProxy) baz.getManyToAny().get(0);
 		s.createQuery( "from Baz baz join baz.manyToAny" ).list();
 		assertTrue( s.createQuery( "select baz from Baz baz join baz.manyToAny a where index(a) = 0" ).list().size()==1 );
 
 		FooProxy foop = (FooProxy) s.get( Foo.class, foo.getKey() );
 		assertTrue( foop == baz.getManyToAny().get(1) );
 
 		barp.setBaz(baz);
 		assertTrue(
 				s.createQuery( "select bar from Bar bar where bar.baz.stringDateMap['now'] is not null" ).list().size()==1 );
 		assertTrue(
 				s.createQuery(
 						"select bar from Bar bar join bar.baz b where b.stringDateMap['big bang'] < b.stringDateMap['now'] and b.stringDateMap['now'] is not null"
 				).list()
 						.size()==1 );
 		assertTrue(
 				s.createQuery(
 						"select bar from Bar bar where bar.baz.stringDateMap['big bang'] < bar.baz.stringDateMap['now'] and bar.baz.stringDateMap['now'] is not null"
 				).list()
 						.size()==1 );
 
 		list = s.createQuery( "select foo.string, foo.component, foo.id from Bar foo" ).list();
 		assertTrue ( ( (FooComponent) ( (Object[]) list.get(0) )[1] ).getName().equals("foo") );
 		list = s.createQuery( "select elements(baz.components) from Baz baz" ).list();
 		assertTrue( list.size()==2 );
 		list = s.createQuery( "select bc.name from Baz baz join baz.components bc" ).list();
 		assertTrue( list.size()==2 );
 		//list = s.find("select bc from Baz baz join baz.components bc");
 
 		s.createQuery("from Foo foo where foo.integer < 10 order by foo.string").setMaxResults(12).list();
 
 		s.delete(barp);
 		s.delete(baz);
 		s.delete( foop.getFoo() );
 		s.delete(foop);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeDeleteDetached() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		list.add( new Fee() );
 		baz.setFees( list );
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertFalse( Hibernate.isInitialized( baz.getFees() ) );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( baz );
 		s.flush();
 		assertFalse( s.createQuery( "from Fee" ).iterate().hasNext() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = new Baz();
 		list = new ArrayList();
 		list.add( new Fee() );
 		list.add( new Fee() );
 		baz.setFees(list);
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		Hibernate.initialize( baz.getFees() );
 		s.getTransaction().commit();
 		s.close();
 
 		assertTrue( baz.getFees().size() == 2 );
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete(baz);
 		s.flush();
 		assertFalse( s.createQuery( "from Fee" ).iterate().hasNext() );
 		s.getTransaction().commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testForeignKeys() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Foo foo = new Foo();
 		List bag = new ArrayList();
 		bag.add(foo);
 		baz.setIdFooBag(bag);
 		baz.setFoo(foo);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNonlazyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.createCriteria(Baz.class)
 			//.setComment("criteria test")
 			.setFetchMode( "stringDateMap", FetchMode.JOIN )
 			.uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getFooToGlarch() ) );
 		assertTrue( Hibernate.isInitialized( baz.getFooComponentToFoo() ) );
 		assertTrue( !Hibernate.isInitialized( baz.getStringSet() ) );
 		assertTrue( Hibernate.isInitialized( baz.getStringDateMap() ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testReuseDeletedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.flush();
 		s.delete(baz);
 		Baz baz2 = new Baz();
 		baz2.setStringArray( new String[] {"x-y-z"} );
 		s.save(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		baz2.setStringSet( baz.getStringSet() );
 		baz2.setStringArray( baz.getStringArray() );
 		baz2.setFooArray( baz.getFooArray() );
 
 		s = openSession();
 		s.beginTransaction();
 		s.update(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		assertTrue( baz2.getStringArray().length==3 );
 		assertTrue( baz2.getStringSet().size()==3 );
 		s.delete(baz2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPropertyRef() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Holder h = new Holder();
 		h.setName("foo");
 		Holder h2 = new Holder();
 		h2.setName("bar");
 		h.setOtherHolder(h2);
 		Serializable hid = s.save(h);
 		Qux q = new Qux();
 		q.setHolder(h2);
 		Serializable qid = s.save(q);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		h = (Holder) s.load(Holder.class, hid);
 		assertEquals( h.getName(), "foo");
 		assertEquals( h.getOtherHolder().getName(), "bar");
 		Object[] res = (Object[]) s.createQuery( "from Holder h join h.otherHolder oh where h.otherHolder.name = 'bar'" )
 				.list()
 				.get(0);
 		assertTrue( res[0]==h );
 		q = (Qux) s.get(Qux.class, qid);
 		assertTrue( q.getHolder() == h.getOtherHolder() );
 		s.delete(h);
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryCollectionOfValues() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		Glarch g = new Glarch();
 		Serializable gid = s.save(g);
 
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) /*&& !(dialect instanceof MckoiDialect)*/ && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) && !(getDialect() instanceof TimesTenDialect) ) {
 			s.createFilter( baz.getFooArray(), "where size(this.bytes) > 0" ).list();
 			s.createFilter( baz.getFooArray(), "where 0 in elements(this.bytes)" ).list();
 		}
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Baz baz join baz.fooSet foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.fooArray foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.stringDateMap date where index(date) = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.topGlarchez g where index(g) = 'A'" ).list();
 		s.createQuery( "select index(g) from Baz baz join baz.topGlarchez g" ).list();
 
 		assertTrue( s.createQuery( "from Baz baz left join baz.stringSet" ).list().size()==3 );
 		baz = (Baz) s.createQuery( "from Baz baz join baz.stringSet str where str='foo'" ).list().get(0);
 		assertTrue( !Hibernate.isInitialized( baz.getStringSet() ) );
 		baz = (Baz) s.createQuery( "from Baz baz left join fetch baz.stringSet" ).list().get(0);
 		assertTrue( Hibernate.isInitialized( baz.getStringSet() ) );
 		assertTrue( s.createQuery( "from Baz baz join baz.stringSet string where string='foo'" ).list().size()==1 );
 		assertTrue( s.createQuery( "from Baz baz inner join baz.components comp where comp.name='foo'" ).list().size()==1 );
 		//List bss = s.find("select baz, ss from Baz baz inner join baz.stringSet ss");
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee is not null" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp join comp.fee fee where fee.count > 0" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee.count is not null" ).list();
 
 		s.delete(baz);
 		s.delete( s.get(Glarch.class, gid) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBatchLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		SortedSet stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("bar");
 		Set fooSet = new HashSet();
 		for (int i=0; i<3; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz.setFooSet(fooSet);
 		baz.setStringSet(stringSet);
 		s.save(baz);
 		Baz baz2 = new Baz();
 		fooSet = new HashSet();
 		for (int i=0; i<2; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz2.setFooSet(fooSet);
 		s.save(baz2);
 		Baz baz3 = new Baz();
 		stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("baz");
 		baz3.setStringSet(stringSet);
 		s.save(baz3);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz3 = (Baz) s.load( Baz.class, baz3.getCode() );
 		assertFalse( Hibernate.isInitialized(baz.getFooSet()) || Hibernate.isInitialized(baz2.getFooSet()) || Hibernate.isInitialized(baz3.getFooSet()) );
 		assertFalse( Hibernate.isInitialized(baz.getStringSet()) || Hibernate.isInitialized(baz2.getStringSet()) || Hibernate.isInitialized(baz3.getStringSet()) );
 		assertTrue( baz.getFooSet().size()==3 );
 		assertTrue( Hibernate.isInitialized(baz.getFooSet()) && Hibernate.isInitialized(baz2.getFooSet()) && Hibernate.isInitialized(baz3.getFooSet()));
 		assertTrue( baz2.getFooSet().size()==2 );
 		assertTrue( baz3.getStringSet().contains("baz") );
 		assertTrue( Hibernate.isInitialized(baz.getStringSet()) && Hibernate.isInitialized(baz2.getStringSet()) && Hibernate.isInitialized(baz3.getStringSet()));
 		assertTrue( baz.getStringSet().size()==2 && baz2.getStringSet().size()==0 );
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete(baz3);
 		Iterator iter = new JoinedIterator( new Iterator[] { baz.getFooSet().iterator(), baz2.getFooSet().iterator() } );
 		while ( iter.hasNext() ) s.delete( iter.next() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInitializedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Collection fooBag = new ArrayList();
 		fooBag.add( new Foo() );
 		fooBag.add( new Foo() );
 		baz.setFooBag( fooBag );
 		s.save(baz);
 		s.flush();
 		fooBag = baz.getFooBag();
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( fooBag == baz.getFooBag() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		Object bag = baz.getFooBag();
 		assertFalse( Hibernate.isInitialized( bag ) );
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( bag==baz.getFooBag() );
 		assertTrue( baz.getFooBag().size() == 2 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLateCollectionAdd() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List l = new ArrayList();
 		baz.setStringList(l);
 		l.add( "foo" );
 		Serializable id = s.save(baz);
 		l.add("bar");
 		s.flush();
 		l.add( "baz" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		assertTrue( baz.getStringList().size() == 3 && baz.getStringList().contains( "bar" ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testUpdate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save( foo );
 		s.getTransaction().commit();
 		s.close();
 
 		foo = (Foo) SerializationHelper.deserialize( SerializationHelper.serialize(foo) );
 
 		s = openSession();
 		s.beginTransaction();
 		FooProxy foo2 = (FooProxy) s.load( Foo.class, foo.getKey() );
 		foo2.setString("dirty");
 		foo2.setBoolean( new Boolean( false ) );
 		foo2.setBytes( new byte[] {1, 2, 3} );
 		foo2.setDate( null );
 		foo2.setShort( new Short( "69" ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2.setString( "dirty again" );
 		s.update(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2.setString( "dirty again 2" );
 		s.update( foo2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo3 = new Foo();
 		s.load( foo3, foo.getKey() );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "update", foo2.equalsFoo(foo3) );
 		s.delete( foo3 );
 		doDelete( s, "from Glarch" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testListRemove() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz b = new Baz();
 		List stringList = new ArrayList();
 		List feeList = new ArrayList();
 		b.setFees(feeList);
 		b.setStringList(stringList);
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		stringList.add("foo");
 		stringList.add("bar");
 		stringList.add("baz");
 		stringList.add("glarch");
 		s.save(b);
 		s.flush();
 		stringList.remove(1);
 		feeList.remove(1);
 		s.flush();
 		s.evict(b);
 		s.refresh(b);
 		assertTrue( b.getFees().size()==3 );
 		stringList = b.getStringList();
 		assertTrue(
 			stringList.size()==3 &&
 			"baz".equals( stringList.get(1) ) &&
 			"foo".equals( stringList.get(0) )
 		);
 		s.delete(b);
 		doDelete( s, "from Fee" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInitializedCollectionDupe() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Collection fooBag = new ArrayList();
 		fooBag.add( new Foo() );
 		fooBag.add( new Foo() );
 		baz.setFooBag(fooBag);
 		s.save( baz );
 		s.flush();
 		fooBag = baz.getFooBag();
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( Hibernate.isInitialized( fooBag ) );
 		assertTrue( fooBag == baz.getFooBag() );
 		assertTrue( baz.getFooBag().size() == 2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		Object bag = baz.getFooBag();
 		assertFalse( Hibernate.isInitialized(bag) );
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( Hibernate.isInitialized( bag ) );
 		assertTrue( bag==baz.getFooBag() );
 		assertTrue( baz.getFooBag().size()==2 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSortables() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz b = new Baz();
 		b.setName("name");
 		SortedSet ss = new TreeSet();
 		ss.add( new Sortable("foo") );
 		ss.add( new Sortable("bar") );
 		ss.add( new Sortable("baz") );
 		b.setSortablez(ss);
 		s.save(b);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Criteria cr = s.createCriteria(Baz.class);
 		cr.setFetchMode( "topGlarchez", FetchMode.SELECT );
 		List result = cr
 			.addOrder( Order.asc("name") )
 			.list();
 		assertTrue( result.size()==1 );
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		result = s.createQuery("from Baz baz left join fetch baz.sortablez order by baz.name asc")
 			.list();
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		result = s.createQuery("from Baz baz order by baz.name asc")
 			.list();
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.delete(b);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchList() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		s.flush();
 		List list = new ArrayList();
 		for ( int i=0; i<5; i++ ) {
 			Fee fee = new Fee();
 			list.add(fee);
 		}
 		baz.setFees(list);
 		list = s.createQuery( "from Foo foo, Baz baz left join fetch baz.fees" ).list();
 		assertTrue( Hibernate.isInitialized( ( (Baz) ( (Object[]) list.get(0) )[1] ).getFees() ) );
 		s.delete(foo);
 		s.delete(foo2);
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBagOneToMany() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		baz.setBazez(list);
 		list.add( new Baz() );
 		s.save(baz);
 		s.flush();
 		list.add( new Baz() );
 		s.flush();
 		list.add( 0, new Baz() );
 		s.flush();
 		s.delete( list.remove(1) );
 		s.flush();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryLockMode() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Bar bar = new Bar();
 		s.save(bar);
 		s.flush();
 		bar.setString("changed");
 		Baz baz = new Baz();
 		baz.setFoo(bar);
 		s.save(baz);
 		Query q = s.createQuery("from Foo foo, Bar bar");
 		if ( !(getDialect() instanceof DB2Dialect) ) {
 			q.setLockMode("bar", LockMode.UPGRADE);
 		}
 		Object[] result = (Object[]) q.uniqueResult();
 		Object b = result[0];
 		assertTrue( s.getCurrentLockMode(b)==LockMode.WRITE && s.getCurrentLockMode( result[1] )==LockMode.WRITE );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		assertTrue( s.getCurrentLockMode( b ) == LockMode.NONE );
 		s.createQuery( "from Foo foo" ).list();
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		q = s.createQuery("from Foo foo");
 		q.setLockMode( "foo", LockMode.READ );
 		q.list();
 		assertTrue( s.getCurrentLockMode( b ) == LockMode.READ );
 		s.evict( baz );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		s.delete( s.load( Baz.class, baz.getCode() ) );
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		q = s.createQuery("from Foo foo, Bar bar, Bar bar2");
 		if ( !(getDialect() instanceof DB2Dialect) ) {
 			q.setLockMode("bar", LockMode.UPGRADE);
 		}
 		q.setLockMode("bar2", LockMode.READ);
 		result = (Object[]) q.list().get(0);
 		if ( !(getDialect() instanceof DB2Dialect) ) {
 			assertTrue( s.getCurrentLockMode( result[0] )==LockMode.UPGRADE && s.getCurrentLockMode( result[1] )==LockMode.UPGRADE );
 		}
 		s.delete( result[0] );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToManyBag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Serializable id = s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		baz.getFooBag().add( new Foo() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		assertTrue( !Hibernate.isInitialized( baz.getFooBag() ) );
 		assertTrue( baz.getFooBag().size()==1 );
 		if ( !(getDialect() instanceof HSQLDialect) ) assertTrue( Hibernate.isInitialized( baz.getFooBag().iterator().next() ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testIdBag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		List l = new ArrayList();
 		List l2 = new ArrayList();
 		baz.setIdFooBag(l);
 		baz.setByteBag(l2);
 		l.add( new Foo() );
 		l.add( new Bar() );
 		byte[] bytes = "ffo".getBytes();
 		l2.add(bytes);
 		l2.add( "foo".getBytes() );
 		s.flush();
 		l.add( new Foo() );
 		l.add( new Bar() );
 		l2.add( "bar".getBytes() );
 		s.flush();
 		s.delete( l.remove(3) );
 		bytes[1]='o';
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==3 );
 		assertTrue( baz.getByteBag().size()==3 );
 		bytes = "foobar".getBytes();
 		Iterator iter = baz.getIdFooBag().iterator();
 		while ( iter.hasNext() ) s.delete( iter.next() );
 		baz.setIdFooBag(null);
 		baz.getByteBag().add(bytes);
 		baz.getByteBag().add(bytes);
 		assertTrue( baz.getByteBag().size()==5 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==0 );
 		assertTrue( baz.getByteBag().size()==5 );
 		baz.getIdFooBag().add( new Foo() );
 		iter = baz.getByteBag().iterator();
 		iter.next();
 		iter.remove();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==1 );
 		assertTrue( baz.getByteBag().size()==4 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private boolean isOuterJoinFetchingDisabled() {
 		return new Integer(0).equals( ( (SessionFactoryImplementor) sessionFactory() ).getSettings().getMaximumFetchDepth() );
 	}
 
 	@Test
 	public void testForceOuterJoin() throws Exception {
 		if ( isOuterJoinFetchingDisabled() ) {
 			return;
 		}
 
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g = new Glarch();
 		FooComponent fc = new FooComponent();
 		fc.setGlarch(g);
 		FooProxy f = new Foo();
 		FooProxy f2 = new Foo();
 		f.setComponent(fc);
 		f.setFoo(f2);
 		s.save(f2);
 		Serializable id = s.save(f);
 		Serializable gid = s.getIdentifier( f.getComponent().getGlarch() );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().evict(Foo.class);
 
 		s = openSession();
 		s.beginTransaction();
 		f = (FooProxy) s.load(Foo.class, id);
 		assertFalse( Hibernate.isInitialized(f) );
 		assertTrue( Hibernate.isInitialized( f.getComponent().getGlarch() ) ); //outer-join="true"
 		assertFalse( Hibernate.isInitialized( f.getFoo() ) ); //outer-join="auto"
 		assertEquals( s.getIdentifier( f.getComponent().getGlarch() ), gid );
 		s.delete(f);
 		s.delete( f.getFoo() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmptyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Serializable id = s.save( new Baz() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Baz baz = (Baz) s.load(Baz.class, id);
 		Set foos = baz.getFooSet();
 		assertTrue( foos.size() == 0 );
 		Foo foo = new Foo();
 		foos.add( foo );
 		s.save(foo);
 		s.flush();
 		s.delete(foo);
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testOneToOneGenerator() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		X x = new X();
 		Y y = new Y();
 		x.setY(y);
 		y.setTheX(x);
 		x.getXxs().add( new X.XX(x) );
 		x.getXxs().add( new X.XX(x) );
 		Serializable id = s.save(y);
 		assertEquals( id, s.save(x) );
 		s.flush();
 		assertTrue( s.contains(y) && s.contains(x) );
 		s.getTransaction().commit();
 		s.close();
 		assertEquals( new Long(x.getId()), y.getId() );
 
 		s = openSession();
 		s.beginTransaction();
 		x = new X();
 		y = new Y();
 		x.setY(y);
 		y.setTheX(x);
 		x.getXxs().add( new X.XX(x) );
 		s.save(y);
 		s.flush();
 		assertTrue( s.contains(y) && s.contains(x) );
 		s.getTransaction().commit();
 		s.close();
 		assertEquals( new Long(x.getId()), y.getId() );
 
 		s = openSession();
 		s.beginTransaction();
 		x = new X();
 		y = new Y();
 		x.setY(y);
 		y.setTheX(x);
 		x.getXxs().add( new X.XX(x) );
 		x.getXxs().add( new X.XX(x) );
 		id = s.save(x);
 		assertEquals( id, y.getId() );
 		assertEquals( id, new Long( x.getId() ) );
 		s.flush();
 		assertTrue( s.contains(y) && s.contains(x) );
 		doDelete( s, "from X x" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLimit() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		for ( int i=0; i<10; i++ ) s.save( new Foo() );
 		Iterator iter = s.createQuery("from Foo foo")
 			.setMaxResults(4)
 			.setFirstResult(2)
 			.iterate();
 		int count=0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			count++;
 		}
 		assertTrue(count==4);
 		iter = s.createQuery("select distinct foo from Foo foo")
 			.setMaxResults(2)
 			.setFirstResult(2)
 			.list()
 			.iterator();
 		count=0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			count++;
 		}
 		assertTrue(count==2);
 		iter = s.createQuery("select distinct foo from Foo foo")
 		.setMaxResults(3)
 		.list()
 		.iterator();
 		count=0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			count++;
 		}
 		assertTrue(count==3);
 		assertEquals( 10, doDelete( s, "from Foo foo" ) );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCustom() throws Exception {
 		GlarchProxy g = new Glarch();
 		Multiplicity m = new Multiplicity();
 		m.count = 12;
 		m.glarch = (Glarch) g;
 		g.setMultiple(m);
 
 		Session s = openSession();
 		s.beginTransaction();
 		Serializable gid = s.save(g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		//g = (Glarch) s.createQuery( "from Glarch g where g.multiple.count=12" ).list().get(0);
 		s.createQuery( "from Glarch g where g.multiple.count=12" ).list().get( 0 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (Glarch) s.createQuery( "from Glarch g where g.multiple.glarch=g and g.multiple.count=12" ).list().get(0);
 		assertTrue( g.getMultiple()!=null );
 		assertEquals( g.getMultiple().count, 12 );
 		assertSame(g.getMultiple().glarch, g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( g.getMultiple() != null );
 		assertEquals( g.getMultiple().count, 12 );
 		assertSame( g.getMultiple().glarch, g );
 		s.delete(g);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveAddDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars( bars );
 		s.save( baz );
 		s.flush();
 		baz.getCascadingBars().add( new Bar() );
 		s.delete(baz);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNamedParams() throws Exception {
 		Bar bar = new Bar();
 		Bar bar2 = new Bar();
 		bar.setName("Bar");
 		bar2.setName("Bar Two");
 		bar.setX( 10 );
 		bar2.setX( 1000 );Baz baz = new Baz();
 		baz.setCascadingBars( new HashSet() );
 		baz.getCascadingBars().add(bar);
 		bar.setBaz(baz);
 
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		s.save( baz );
 		s.save( bar2 );
 
 		List list = s.createQuery(
 				"from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like 'Bar %'"
 		).list();
 		Object row = list.iterator().next();
 		assertTrue( row instanceof Object[] && ( (Object[]) row ).length==3 );
 
 		Query q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like 'Bar%'");
 		list = q.list();
 		if ( !(getDialect() instanceof SAPDBDialect) ) assertTrue( list.size()==2 );
 
 		q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where ( bar.name in (:nameList) or bar.name in (:nameList) ) and bar.string = :stringVal");
 		HashSet nameList = new HashSet();
 		nameList.add( "bar" );
 		nameList.add( "Bar" );
 		nameList.add( "Bar Two" );
 		q.setParameterList( "nameList", nameList );
 		q.setParameter( "stringVal", "a string" );
 		list = q.list();
 		if ( !(getDialect() instanceof SAPDBDialect) ) assertTrue( list.size()==2 );
 
 		try {
 			q.setParameterList("nameList", (Collection)null);
 			fail("Should throw an queryexception when passing a null!");
 		} catch (QueryException qe) {
 			//should happen
 		}
 
 		q = s.createQuery("select bar, b from Bar bar inner join bar.baz baz inner join baz.cascadingBars b where bar.name like 'Bar%'");
 		Object result = q.uniqueResult();
 		assertTrue( result != null );
 		q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like :name and b.name like :name");
 		q.setString( "name", "Bar%" );
 		list = q.list();
 		assertTrue( list.size()==1 );
 
 
 		// This test added for issue HB-297 - there is an named parameter in the Order By clause
 		q = s.createQuery("select bar from Bar bar order by ((bar.x - :valueX)*(bar.x - :valueX))");
 		q.setInteger( "valueX", bar.getX() + 1 );
 		list = q.list();
 		assertTrue( ((Bar) list.get( 0 )).getX() == bar.getX() );
 		q.setInteger( "valueX", bar2.getX() + 1 );
 		list = q.list();
 		assertTrue( ((Bar)list.get(0)).getX() == bar2.getX());
 
 		s.delete(baz);
 		s.delete(bar2);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsEmptyInListCheck.class,
 			comment = "Dialect does not support SQL empty in list [x in ()]"
 	)
 	public void testEmptyInListQuery() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		Query q = s.createQuery( "select bar from Bar as bar where bar.name in (:nameList)" );
 		q.setParameterList( "nameList", Collections.EMPTY_LIST );
 		assertEquals( 0, q.list().size() );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testParameterCheck() throws HibernateException {
 		Session s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.x > :myX");
 			q.list();
 			fail("Should throw QueryException for missing myX");
 		}
 		catch (QueryException iae) {
 			// should happen
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.x > ?");
 			q.list();
 			fail("Should throw QueryException for missing ?");
 		}
 		catch (QueryException iae) {
 			// should happen
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.x > ? or bar.short = 1 or bar.string = 'ff ? bb'");
 			q.setInteger(0, 1);
 			q.list();
 		}
 		catch (QueryException iae) {
 			fail("Should not throw QueryException for missing ?");
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.string = ' ? ' or bar.string = '?'");
 			q.list();
 		}
 		catch (QueryException iae) {
 			fail("Should not throw QueryException for ? in quotes");
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.string = ? or bar.string = ? or bar.string = ?");
 			q.setParameter(0, "bull");
 			q.setParameter(2, "shit");
 			q.list();
 			fail("should throw exception telling me i have not set parameter 1");
 		}
 		catch (QueryException iae) {
 			// should happen!
 		}
 		finally {
 			s.close();
 		}
 	}
 
 	@Test
 	public void testDyna() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		g.setName("G");
 		Serializable id = s.save(g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( g.getName().equals("G") );
 		assertTrue( g.getDynaBean().get("foo").equals("foo") && g.getDynaBean().get("bar").equals( new Integer(66) ) );
 		assertTrue( ! (g instanceof Glarch) );
 		g.getDynaBean().put("foo", "bar");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( g.getDynaBean().get("foo").equals("bar") && g.getDynaBean().get("bar").equals( new Integer(66) ) );
 		g.setDynaBean(null);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( g.getDynaBean()==null );
 		s.delete(g);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFindByCriteria() throws Exception {
 		if ( getDialect() instanceof DB2Dialect ) {
 			return;
 		}
 
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo f = new Foo();
 		s.save( f );
 		s.flush();
 
 		List list = s.createCriteria(Foo.class)
 			.add( Restrictions.eq( "integer", f.getInteger() ) )
 			.add( Restrictions.eqProperty("integer", "integer") )
 			.add( Restrictions.like( "string", f.getString().toUpperCase() ).ignoreCase() )
 			.add( Restrictions.in( "boolean", new Boolean[] { f.getBoolean(), f.getBoolean() } ) )
 			.setFetchMode("foo", FetchMode.JOIN)
 			.setFetchMode("baz", FetchMode.SELECT)
 			.setFetchMode("abstracts", FetchMode.JOIN)
 			.list();
 		assertTrue( list.size() == 1 && list.get( 0 ) == f );
 
 		list = s.createCriteria(Foo.class).add(
 				Restrictions.disjunction()
 					.add( Restrictions.eq( "integer", f.getInteger() ) )
 					.add( Restrictions.like( "string", f.getString() ) )
 					.add( Restrictions.eq( "boolean", f.getBoolean() ) )
 			)
 			.add( Restrictions.isNotNull("boolean") )
 			.list();
 		assertTrue( list.size() == 1 && list.get( 0 ) == f );
 
 		Foo example = new Foo();
 		example.setString("a STRing");
 		list = s.createCriteria(Foo.class).add(
 			Example.create(example)
 				.excludeZeroes()
 				.ignoreCase()
 				.excludeProperty("bool")
 				.excludeProperty("char")
 				.excludeProperty("yesno")
 			)
 			.list();
 		assertTrue(
 				"Example API without like did not work correctly, size was " + list.size(),
 				list.size() == 1 && list.get( 0 ) == f
 		);
 		example.setString("rin");
 
 		list = s.createCriteria(Foo.class).add(
 			Example.create(example)
 				.excludeZeroes()
 				.enableLike(MatchMode.ANYWHERE)
 				.excludeProperty("bool")
 				.excludeProperty("char")
 				.excludeProperty("yesno")
 			)
 			.list();
 		assertTrue( "Example API without like did not work correctly, size was " + list.size(), list.size()==1 && list.get(0)==f );
 
 		list = s.createCriteria(Foo.class)
 			.add( Restrictions.or(
 					Restrictions.and(
 					Restrictions.eq( "integer", f.getInteger() ),
 					Restrictions.like( "string", f.getString() )
 				),
 				Restrictions.eq( "boolean", f.getBoolean() )
 			) )
 			.list();
 		assertTrue( list.size()==1 && list.get(0)==f );
 		list = s.createCriteria(Foo.class)
 			.setMaxResults(5)
 			.addOrder( Order.asc("date") )
 			.list();
 		assertTrue( list.size()==1 && list.get(0)==f );
 		if(!(getDialect() instanceof TimesTenDialect || getDialect() instanceof HSQLDialect)) {
 			list = s.createCriteria(Foo.class).setMaxResults(0).list();
 			assertTrue( list.size()==0 );
 		}
 		list = s.createCriteria(Foo.class)
 			.setFirstResult(1)
 			.addOrder( Order.asc("date") )
 			.addOrder( Order.desc("string") )
 			.list();
 		assertTrue( list.size() == 0 );
 		list = s.createCriteria(Foo.class)
 			.setFetchMode( "component.importantDates", FetchMode.JOIN )
 			.list();
 		assertTrue( list.size() == 3 );
 
 		list = s.createCriteria(Foo.class)
 			.setFetchMode( "component.importantDates", FetchMode.JOIN )
 			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
 			.list();
 		assertTrue( list.size()==1 );
 
 		f.setFoo( new Foo() );
 		s.save( f.getFoo() );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		list = s.createCriteria(Foo.class)
 			.add( Restrictions.eq( "integer", f.getInteger() ) )
 			.add( Restrictions.like( "string", f.getString() ) )
 			.add( Restrictions.in( "boolean", new Boolean[] { f.getBoolean(), f.getBoolean() } ) )
 			.add( Restrictions.isNotNull("foo") )
 			.setFetchMode( "foo", FetchMode.JOIN )
 			.setFetchMode( "baz", FetchMode.SELECT )
 			.setFetchMode( "component.glarch", FetchMode.SELECT )
 			.setFetchMode( "foo.baz", FetchMode.SELECT )
 			.setFetchMode( "foo.component.glarch", FetchMode.SELECT )
 			.list();
 		f = (Foo) list.get(0);
 		assertTrue( Hibernate.isInitialized( f.getFoo() ) );
 		assertTrue( !Hibernate.isInitialized( f.getComponent().getGlarch() ) );
 
 		s.save( new Bar() );
 		list = s.createCriteria(Bar.class)
 			.list();
 		assertTrue( list.size() == 1 );
 		assertTrue( s.createCriteria(Foo.class).list().size()==3 );
 		s.delete( list.get( 0 ) );
 
 		s.delete( f.getFoo() );
 		s.delete(f);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testAfterDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		s.flush();
 		s.delete(foo);
 		s.save(foo);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionWhere() throws Exception {
 		Foo foo1 = new Foo();
 		Foo foo2 = new Foo();
 		Baz baz = new Baz();
 		Foo[] arr = new Foo[10];
 		arr[0] = foo1;
 		arr[9] = foo2;
 
 		Session s = openSession();
 		s.beginTransaction();
 		s.save( foo1 );
 		s.save(foo2);
 		baz.setFooArray( arr );
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooArray().length == 1 );
 		assertTrue( s.createQuery( "from Baz baz join baz.fooArray foo" ).list().size()==1 );
 		assertTrue( s.createQuery( "from Foo foo" ).list().size()==2 );
 		assertTrue( s.createFilter( baz.getFooArray(), "" ).list().size() == 1 );
 		//assertTrue( s.delete("from java.lang.Object o")==9 );
 		doDelete( s, "from Foo foo" );
 		final String bazid = baz.getCode();
 		s.delete( baz );
 		int rows = s.doReturningWork(
 				new AbstractReturningWork<Integer>() {
 					@Override
 					public Integer execute(Connection connection) throws SQLException {
 						return connection.createStatement()
 								.executeUpdate( "delete from FOO_ARRAY where id_='" + bazid + "' and i>=8" );
 					}
 				}
 		);
 		assertTrue( rows == 1 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentParent() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		BarProxy bar = new Bar();
 		bar.setBarComponent( new FooComponent() );
 		Baz baz = new Baz();
 		baz.setComponents( new FooComponent[] { new FooComponent(), new FooComponent() } );
 		s.save(bar);
 		s.save(baz);
 		t.commit();
 		s.close();
 		s = openSession();
 		t = s.beginTransaction();
 		bar = (BarProxy) s.load(Bar.class, bar.getKey());
 		s.load(baz, baz.getCode());
 		assertTrue( bar.getBarComponent().getParent()==bar );
 		assertTrue( baz.getComponents()[0].getBaz()==baz && baz.getComponents()[1].getBaz()==baz );
 		s.delete(baz);
 		s.delete(bar);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionCache() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( Baz.class, baz.getCode() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void ntestAssociationId() throws Exception {
 		// IMPL NOTE : previously not being run due to the name
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Bar bar = new Bar();
 		String id = (String) s.save(bar);
 		MoreStuff more = new MoreStuff();
 		more.setName("More Stuff");
 		more.setIntId(12);
 		more.setStringId("id");
 		Stuff stuf = new Stuff();
 		stuf.setMoreStuff(more);
 		more.setStuffs( new ArrayList() );
 		more.getStuffs().add(stuf);
 		stuf.setFoo(bar);
 		stuf.setId(1234);
 		stuf.setProperty( TimeZone.getDefault() );
 		s.save(more);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List results = s.createQuery(
 				"from Stuff as s where s.foo.id = ? and s.id.id = ? and s.moreStuff.id.intId = ? and s.moreStuff.id.stringId = ?"
 		)
-				.setParameter( 0, bar, Hibernate.entity(Foo.class) )
-				.setParameter( 1, new Long(1234), Hibernate.LONG )
-				.setParameter( 2, new Integer(12), Hibernate.INTEGER )
-				.setParameter( 3, "id", Hibernate.STRING )
+				.setParameter( 0, bar, s.getTypeHelper().entity(Foo.class) )
+				.setParameter( 1, new Long(1234), StandardBasicTypes.LONG )
+				.setParameter( 2, new Integer(12), StandardBasicTypes.INTEGER )
+				.setParameter( 3, "id", StandardBasicTypes.STRING )
 				.list();
 		assertEquals( 1, results.size() );
 		results = s.createQuery( "from Stuff as s where s.foo.id = ? and s.id.id = ? and s.moreStuff.name = ?" )
-				.setParameter( 0, bar, Hibernate.entity(Foo.class) )
-				.setParameter( 1, new Long(1234), Hibernate.LONG )
-				.setParameter( 2, "More Stuff", Hibernate.STRING )
+				.setParameter( 0, bar, s.getTypeHelper().entity(Foo.class) )
+				.setParameter( 1, new Long(1234), StandardBasicTypes.LONG )
+				.setParameter( 2, "More Stuff", StandardBasicTypes.STRING )
 				.list();
 		assertEquals( 1, results.size() );
 		s.createQuery( "from Stuff as s where s.foo.string is not null" ).list();
 		assertTrue(
 				s.createQuery( "from Stuff as s where s.foo > '0' order by s.foo" ).list().size()==1
 		);
 		//s.createCriteria(Stuff.class).createCriteria("id.foo").add( Expression.isNull("foo") ).list();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		FooProxy foo = (FooProxy) s.load(Foo.class, id);
 		s.load(more, more);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Stuff stuff = new Stuff();
 		stuff.setFoo(foo);
 		stuff.setId(1234);
 		stuff.setMoreStuff(more);
 		s.load(stuff, stuff);
 		assertTrue( stuff.getProperty().equals( TimeZone.getDefault() ) );
 		assertTrue( stuff.getMoreStuff().getName().equals("More Stuff") );
 		doDelete( s, "from MoreStuff" );
 		doDelete( s, "from Foo foo" );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeSave() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		list.add( new Fee() );
 		list.add( new Fee() );
 		baz.setFees( list );
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( baz.getFees().size() == 2 );
 		s.delete(baz);
 		assertTrue( !s.createQuery( "from Fee fee" ).iterate().hasNext() );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionsInSelect() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Foo[] foos = new Foo[] { null, new Foo() };
 		s.save( foos[1] );
 		Baz baz = new Baz();
 		baz.setDefaults();
 		baz.setFooArray(foos);
 		s.save(baz);
 		Baz baz2 = new Baz();
 		baz2.setDefaults();
 		s.save(baz2);
 
 		Bar bar = new Bar();
 		bar.setBaz(baz);
 		s.save(bar);
 
 		List list = s.createQuery( "select new Result(foo.string, foo.long, foo.integer) from Foo foo" ).list();
 		assertTrue( list.size()==2 && ( list.get(0) instanceof Result ) && ( list.get(1) instanceof Result ) );
 		/*list = s.find("select new Result( baz.name, foo.long, count(elements(baz.fooArray)) ) from Baz baz join baz.fooArray foo group by baz.name, foo.long");
 		assertTrue( list.size()==1 && ( list.get(0) instanceof Result ) );
 		Result r = ((Result) list.get(0) );
 		assertEquals( r.getName(), baz.getName() );
 		assertEquals( r.getCount(), 1 );
 		assertEquals( r.getAmount(), foos[1].getLong().longValue() );*/
 		list = s.createQuery(
 				"select new Result( baz.name, max(foo.long), count(foo) ) from Baz baz join baz.fooArray foo group by baz.name"
 		).list();
 		assertTrue( list.size()==1 && ( list.get(0) instanceof Result ) );
 		Result r = ((Result) list.get(0) );
 		assertEquals( r.getName(), baz.getName() );
 		assertEquals( r.getCount(), 1 );
 		assertTrue( r.getAmount() > 696969696969696000l );
 
 
 		//s.find("select max( elements(bar.baz.fooArray) ) from Bar as bar");
 		//The following test is disabled for databases with no subselects...also for Interbase (not sure why).
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) /*&& !(dialect instanceof MckoiDialect)*/ && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			s.createQuery( "select count(*) from Baz as baz where 1 in indices(baz.fooArray)" ).list();
 			s.createQuery( "select count(*) from Bar as bar where 'abc' in elements(bar.baz.fooArray)" ).list();
 			s.createQuery( "select count(*) from Bar as bar where 1 in indices(bar.baz.fooArray)" ).list();
 			if ( !(getDialect() instanceof DB2Dialect) &&  !(getDialect() instanceof Oracle8iDialect ) && !( getDialect() instanceof SybaseDialect ) && !( getDialect() instanceof Sybase11Dialect ) && !( getDialect() instanceof SybaseASE15Dialect ) && !( getDialect() instanceof PostgreSQLDialect )) {
 				// SybaseAnywhereDialect supports implicit conversions from strings to ints
 				s.createQuery(
 						"select count(*) from Bar as bar, bar.component.glarch.proxyArray as g where g.id in indices(bar.baz.fooArray)"
 				).list();
 				s.createQuery(
 						"select max( elements(bar.baz.fooArray) ) from Bar as bar, bar.component.glarch.proxyArray as g where g.id in indices(bar.baz.fooArray)"
 				).list();
 			}
 			s.createQuery(
 					"select count(*) from Bar as bar where '1' in (from bar.component.glarch.proxyArray g where g.name='foo')"
 			).list();
 			s.createQuery(
 					"select count(*) from Bar as bar where '1' in (from bar.component.glarch.proxyArray g where g.name='foo')"
 			).list();
 			s.createQuery(
 					"select count(*) from Bar as bar left outer join bar.component.glarch.proxyArray as pg where '1' in (from bar.component.glarch.proxyArray)"
 			).list();
 		}
 
 		list = s.createQuery(
 				"from Baz baz left join baz.fooToGlarch join fetch baz.fooArray foo left join fetch foo.foo"
 		).list();
 		assertTrue( list.size()==1 && ( (Object[]) list.get(0) ).length==2 );
 
 		s.createQuery(
 				"select baz.name from Bar bar inner join bar.baz baz inner join baz.fooSet foo where baz.name = bar.string"
 		).list();
 		s.createQuery(
 				"SELECT baz.name FROM Bar AS bar INNER JOIN bar.baz AS baz INNER JOIN baz.fooSet AS foo WHERE baz.name = bar.string"
 		).list();
 
 		if ( !( getDialect() instanceof HSQLDialect ) ) s.createQuery(
 				"select baz.name from Bar bar join bar.baz baz left outer join baz.fooSet foo where baz.name = bar.string"
 		).list();
 
 		s.createQuery( "select baz.name from Bar bar join bar.baz baz join baz.fooSet foo where baz.name = bar.string" )
 				.list();
 		s.createQuery(
 				"SELECT baz.name FROM Bar AS bar JOIN bar.baz AS baz JOIN baz.fooSet AS foo WHERE baz.name = bar.string"
 		).list();
 
 		if ( !( getDialect() instanceof HSQLDialect ) ) {
 			s.createQuery(
 					"select baz.name from Bar bar left join bar.baz baz left join baz.fooSet foo where baz.name = bar.string"
 			).list();
 			s.createQuery( "select foo.string from Bar bar left join bar.baz.fooSet foo where bar.string = foo.string" )
 					.list();
 		}
 
 		s.createQuery(
 				"select baz.name from Bar bar left join bar.baz baz left join baz.fooArray foo where baz.name = bar.string"
 		).list();
 		s.createQuery( "select foo.string from Bar bar left join bar.baz.fooArray foo where bar.string = foo.string" )
 				.list();
 
 		s.createQuery(
 				"select bar.string, foo.string from Bar bar inner join bar.baz as baz inner join baz.fooSet as foo where baz.name = 'name'"
 		).list();
 		s.createQuery( "select foo from Bar bar inner join bar.baz as baz inner join baz.fooSet as foo" ).list();
 		s.createQuery( "select foo from Bar bar inner join bar.baz.fooSet as foo" ).list();
 
 		s.createQuery(
 				"select bar.string, foo.string from Bar bar join bar.baz as baz join baz.fooSet as foo where baz.name = 'name'"
 		).list();
 		s.createQuery( "select foo from Bar bar join bar.baz as baz join baz.fooSet as foo" ).list();
 		s.createQuery( "select foo from Bar bar join bar.baz.fooSet as foo" ).list();
 
 		assertTrue( s.createQuery( "from Bar bar join bar.baz.fooArray foo" ).list().size()==1 );
 
 		assertTrue( s.createQuery( "from Bar bar join bar.baz.fooSet foo" ).list().size()==0 );
 		assertTrue( s.createQuery( "from Bar bar join bar.baz.fooArray foo" ).list().size()==1 );
 
 		s.delete(bar);
 
 		if ( getDialect() instanceof DB2Dialect || getDialect() instanceof PostgreSQLDialect ) {
 			s.createQuery( "select one from One one join one.manies many group by one order by count(many)" ).iterate();
 			s.createQuery( "select one from One one join one.manies many group by one having count(many) < 5" )
 					.iterate();
 		}
 
 		s.createQuery( "from One one join one.manies many where one.id = 1 and many.id = 1" ).list();
 		s.createQuery( "select one.id, elements(one.manies) from One one" ).iterate();
 		s.createQuery( "select max( elements(one.manies) ) from One one" ).iterate();
 		s.createQuery( "select one, elements(one.manies) from One one" ).list();
 		Iterator iter = s.createQuery( "select elements(baz.fooArray) from Baz baz where baz.id=?" )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.iterate();
 		assertTrue( iter.next()==foos[1] && !iter.hasNext() );
 		list = s.createQuery( "select elements(baz.fooArray) from Baz baz where baz.id=?" )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.list();
 		assertEquals( 1, list.size() );
 		iter = s.createQuery( "select indices(baz.fooArray) from Baz baz where baz.id=?" )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.iterate();
 		assertTrue( iter.next().equals( new Integer(1) ) && !iter.hasNext() );
 
 		iter = s.createQuery( "select size(baz.stringSet) from Baz baz where baz.id=?" )
-				.setParameter( 0, baz.getCode(), Hibernate.STRING )
+				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.iterate();
 		assertEquals( new Integer(3), iter.next() );
 
 		s.createQuery( "from Foo foo where foo.component.glarch.id is not null" ).list();
 
 		iter = s.createQuery(
 				"select baz, size(baz.stringSet), count( distinct elements(baz.stringSet) ), max( elements(baz.stringSet) ) from Baz baz group by baz"
 		).iterate();
 		while ( iter.hasNext() ) {
 			Object[] arr = (Object[]) iter.next();
             log.info(arr[0] + " " + arr[1] + " " + arr[2] + " " + arr[3]);
 		}
 
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete( foos[1] );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNewFlushing() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.flush();
 		baz.getStringArray()[0] = "a new value";
 		Iterator iter = s.createQuery( "from Baz baz" ).iterate();//no flush
 		assertTrue( iter.next()==baz );
 		iter = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		boolean found = false;
 		while ( iter.hasNext() ) {
 			if ( iter.next().equals("a new value") ) found = true;
 		}
 		assertTrue( found );
 		baz.setStringArray( null );
 		s.createQuery( "from Baz baz" ).iterate(); //no flush
 		iter = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		assertTrue( !iter.hasNext() );
 		baz.getStringList().add( "1E1" );
 		iter = s.createQuery( "from Foo foo" ).iterate();//no flush
 		assertTrue( !iter.hasNext() );
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		found = false;
 		while ( iter.hasNext() ) {
 			if ( iter.next().equals("1E1") ) found = true;
 		}
 		assertTrue( found );
 		baz.getStringList().remove( "1E1" );
 		iter = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate(); //no flush
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		found = false;
 		while ( iter.hasNext() ) {
 			if ( iter.next().equals("1E1") ) found = true;
 		}
 		assertTrue(!found);
 
 		List newList = new ArrayList();
 		newList.add("value");
 		baz.setStringList( newList );
 		iter = s.createQuery( "from Foo foo" ).iterate();//no flush
 		baz.setStringList( null );
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		assertTrue( !iter.hasNext() );
 
 		baz.setStringList(newList);
 		iter = s.createQuery( "from Foo foo" ).iterate();//no flush
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		assertTrue( iter.hasNext() );
 
 		s.delete( baz );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing", "unchecked"})
 	public void testPersistCollections() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		assertEquals( 0, ( (Long) s.createQuery( "select count(*) from Bar" ).iterate().next() ).longValue() );
 		assertTrue( s.createQuery( "select count(*) from Bar b" ).iterate().next().equals( new Long(0) ) );
 		assertFalse( s.createQuery( "from Glarch g" ).iterate().hasNext() );
 
 		Baz baz = new Baz();
 		s.save(baz);
 		baz.setDefaults();
 		baz.setStringArray( new String[] { "stuff" } );
 		Set bars = new HashSet();
 		bars.add( new Bar() );
 		baz.setCascadingBars(bars);
 		HashMap sgm = new HashMap();
 		sgm.put( "a", new Glarch() );
 		sgm.put( "b", new Glarch() );
 		baz.setStringGlarchMap(sgm);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 1L, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) ( (Object[]) s.createQuery( "select baz, baz from Baz baz" ).list().get(0) )[1];
 		assertTrue( baz.getCascadingBars().size()==1 );
 		//System.out.println( s.print(baz) );
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo() ;
 		s.save(foo2);
 		baz.setFooArray( new Foo[] { foo, foo, null, foo2 } );
 		baz.getFooSet().add(foo);
 		baz.getCustoms().add( new String[] { "new", "custom" } );
 		baz.setStringArray(null);
 		baz.getStringList().set(0, "new value");
 		baz.setStringSet( new TreeSet() );
 		Time time = new java.sql.Time(12345);
 		baz.getTimeArray()[2] = time;
 		//System.out.println(time);
 
 		assertTrue( baz.getStringGlarchMap().size()==1 );
 
 		//The following test is disabled databases with no subselects
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			List list = s.createQuery(
 					"select foo from Foo foo, Baz baz where foo in elements(baz.fooArray) and 3 = some elements(baz.intArray) and 4 > all indices(baz.intArray)"
 			).list();
 			assertTrue( "collection.elements find", list.size()==2 );
 		}
 		if (!(getDialect() instanceof SAPDBDialect) ) { // SAPDB doesn't like distinct with binary type
 			List list = s.createQuery( "select distinct foo from Baz baz join baz.fooArray foo" ).list();
 			assertTrue( "collection.elements find", list.size()==2 );
 		}
 
 		List list = s.createQuery( "select foo from Baz baz join baz.fooSet foo" ).list();
 		assertTrue( "association.elements find", list.size()==1 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 1, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( "collection of custom types - added element", baz.getCustoms().size()==4 && baz.getCustoms().get(0)!=null );
 		assertTrue ( "component of component in collection", baz.getComponents()[1].getSubcomponent()!=null );
 		assertTrue( baz.getComponents()[1].getBaz()==baz );
 		assertTrue( "set of objects", ( (FooProxy) baz.getFooSet().iterator().next() ).getKey().equals( foo.getKey() ));
 		assertTrue( "collection removed", baz.getStringArray().length==0 );
 		assertTrue( "changed element", baz.getStringList().get(0).equals("new value"));
 		assertTrue( "replaced set", baz.getStringSet().size()==0 );
 		assertTrue( "array element change", baz.getTimeArray()[2]!=null );
 		assertTrue( baz.getCascadingBars().size()==1 );
 		//System.out.println( s.print(baz) );
 		baz.getStringSet().add("two");
 		baz.getStringSet().add("one");
 		baz.getBag().add("three");
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( baz.getStringSet().size()==2 );
 		assertTrue( baz.getStringSet().first().equals("one") );
 		assertTrue( baz.getStringSet().last().equals("two") );
 		assertTrue( baz.getBag().size()==5 );
 		baz.getStringSet().remove("two");
 		baz.getBag().remove("duplicate");
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 1, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getCascadingBars().size()==1 );
 		Bar bar = new Bar();
 		Bar bar2 = new Bar();
 		s.save(bar); s.save(bar2);
 		baz.setTopFoos( new HashSet() );
 		baz.getTopFoos().add(bar);
 		baz.getTopFoos().add(bar2);
 		assertTrue( baz.getCascadingBars().size()==1 );
 		baz.setTopGlarchez( new TreeMap() );
 		GlarchProxy g = new Glarch();
 		s.save(g);
 		baz.getTopGlarchez().put( new Character('G'), g );
 		HashMap map = new HashMap();
 		map.put(bar, g);
 		map.put(bar2, g);
 		baz.setFooToGlarch(map);
 		map = new HashMap();
 		map.put( new FooComponent("name", 123, null, null), bar );
 		map.put( new FooComponent("nameName", 12, null, null), bar );
 		baz.setFooComponentToFoo(map);
 		map = new HashMap();
 		map.put(bar, g);
 		baz.setGlarchToFoo(map);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( baz.getCascadingBars().size()==1 );
 
 		Session s2 = openSession();
 		Transaction txn2 = s2.beginTransaction();
 		assertEquals( 3, ((Long) s2.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		Baz baz2 = (Baz) s2.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		Object o = baz2.getFooComponentToFoo().get( new FooComponent("name", 123, null, null) );
 		assertTrue(
 			o==baz2.getFooComponentToFoo().get( new FooComponent("nameName", 12, null, null) ) && o!=null
 		);
 		txn2.commit();
 		s2.close();
 
 		assertTrue( Hibernate.isInitialized( baz.getFooToGlarch() ) );
 		assertTrue( baz.getTopFoos().size()==2 );
 		assertTrue( baz.getTopGlarchez().size()==1 );
 		assertTrue( baz.getTopFoos().iterator().next()!=null );
 		assertTrue( baz.getStringSet().size()==1 );
 		assertTrue( baz.getBag().size()==4 );
 		assertTrue( baz.getFooToGlarch().size()==2 );
 		assertTrue( baz.getFooComponentToFoo().size()==2 );
 		assertTrue( baz.getGlarchToFoo().size()==1 );
 		Iterator iter = baz.getFooToGlarch().keySet().iterator();
 		for (int i=0; i<2; i++ ) assertTrue( iter.next() instanceof BarProxy );
 		FooComponent fooComp = (FooComponent) baz.getFooComponentToFoo().keySet().iterator().next();
 		assertTrue(
 			( (fooComp.getCount()==123 && fooComp.getName().equals("name"))
 			|| (fooComp.getCount()==12 && fooComp.getName().equals("nameName")) )
 			&& ( baz.getFooComponentToFoo().get(fooComp) instanceof BarProxy )
 		);
 		Glarch g2 = new Glarch();
 		s.save(g2);
 		g = (GlarchProxy) baz.getTopGlarchez().get( new Character('G') );
 		baz.getTopGlarchez().put( new Character('H'), g );
 		baz.getTopGlarchez().put( new Character('G'), g2 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getTopGlarchez().size()==2 );
 		assertTrue( baz.getCascadingBars().size()==1 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 3, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( baz.getTopGlarchez().size()==2 );
 		assertTrue( baz.getCascadingBars().size()==1 );
 		txn.commit();
 
 		s2 = (Session) SerializationHelper.deserialize( SerializationHelper.serialize(s) );
 		s.close();
 
 		txn2 = s2.beginTransaction();
 		baz = (Baz) s2.load(Baz.class, baz.getCode());
 		assertEquals( 3, ((Long) s2.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		s2.delete(baz);
 		s2.delete( baz.getTopGlarchez().get( Character.valueOf('G') ) );
 		s2.delete( baz.getTopGlarchez().get( Character.valueOf('H') ) );
 		int rows = s2.doReturningWork(
 				new AbstractReturningWork<Integer>() {
 					@Override
 					public Integer execute(Connection connection) throws SQLException {
 						final String sql = "update " + getDialect().openQuote() + "glarchez" + getDialect().closeQuote() + " set baz_map_id=null where baz_map_index='a'";
 						return connection.createStatement().executeUpdate( sql );
 					}
 				}
 		);
 		assertTrue(rows==1);
 		assertEquals( 2, doDelete( s2, "from Bar bar" ) );
 		FooProxy[] arr = baz.getFooArray();
 		assertTrue( "new array of objects", arr.length==4 && arr[1].getKey().equals( foo.getKey() ) );
 		for ( int i=1; i<arr.length; i++ ) {
 			if ( arr[i]!=null) s2.delete(arr[i]);
 		}
 
 		s2.load( Qux.class, new Long(666) ); //nonexistent
 
 		assertEquals( 1, doDelete( s2, "from Glarch g" ) );
 		txn2.commit();
 
 		s2.disconnect();
 
 		Session s3 = (Session) SerializationHelper.deserialize( SerializationHelper.serialize( s2 ) );
 		s2.close();
 		//s3.reconnect();
 		assertTrue( s3.load( Qux.class, new Long(666) )!=null ); //nonexistent
 		//s3.disconnect();
 		s3.close();
 	}
 
 	@Test
 	public void testSaveFlush() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fee fee = new Fee();
 		s.save( fee );
 		fee.setFi( "blah" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		fee = (Fee) s.load( Fee.class, fee.getKey() );
 		assertTrue( "blah".equals( fee.getFi() ) );
 		s.delete(fee);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCreateUpdate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		foo.setString("dirty");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo2 = new Foo();
 		s.load( foo2, foo.getKey() );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "create-update", foo.equalsFoo(foo2) );
 		//System.out.println( s.print(foo2) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = new Foo();
 		s.save(foo);
 		foo.setString("dirty");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load(foo2, foo.getKey());
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "create-update", foo.equalsFoo(foo2) );
 		//System.out.println( s.print(foo2) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testUpdateCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Holder baz = new Holder();
 		baz.setName("123");
 		Foo f1 = new Foo();
 		Foo f2 = new Foo();
 		Foo f3 = new Foo();
 		One o = new One();
 		baz.setOnes( new ArrayList() );
 		baz.getOnes().add(o);
 		Foo[] foos = new Foo[] { f1, null, f2 };
 		baz.setFooArray(foos);
 		baz.setFoos( new HashSet() );
 		baz.getFoos().add(f1);
 		s.save(f1);
 		s.save(f2);
 		s.save(f3);
 		s.save(o);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		baz.getOnes().set(0, null);
 		baz.getOnes().add(o);
 		baz.getFoos().add(f2);
 		foos[0] = f3;
 		foos[1] = f1;
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Holder h = (Holder) s.load(Holder.class, baz.getId());
 		assertTrue( h.getOnes().get(0)==null );
 		assertTrue( h.getOnes().get(1)!=null );
 		assertTrue( h.getFooArray()[0]!=null);
 		assertTrue( h.getFooArray()[1]!=null);
 		assertTrue( h.getFooArray()[2]!=null);
 		assertTrue( h.getFoos().size()==2 );
 		s.getTransaction().commit();
 		s.close();
 
 		baz.getFoos().remove(f1);
 		baz.getFoos().remove(f2);
 		baz.getFooArray()[0]=null;
 		baz.getFooArray()[0]=null;
 		baz.getFooArray()[0]=null;
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(baz);
 		doDelete( s, "from Foo" );
 		baz.getOnes().remove(o);
 		doDelete( s, "from One" );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCreate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo2 = new Foo();
 		s.load( foo2, foo.getKey() );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "create", foo.equalsFoo( foo2 ) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCallback() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux("0");
 		s.save(q);
 		q.setChild( new Qux( "1" ) );
 		s.save( q.getChild() );
 		Qux q2 = new Qux("2");
 		q2.setChild( q.getChild() );
 		Qux q3 = new Qux("3");
 		q.getChild().setChild(q3);
 		s.save( q3 );
 		Qux q4 = new Qux("4");
 		q4.setChild( q3 );
 		s.save(q4);
 		s.save( q2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List l = s.createQuery( "from Qux" ).list();
 		assertTrue( "", l.size() == 5 );
 		s.delete( l.get( 0 ) );
 		s.delete( l.get( 1 ) );
 		s.delete( l.get( 2 ) );
 		s.delete( l.get(3) );
 		s.delete( l.get(4) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPolymorphism() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		s.save(bar);
 		bar.setBarString("bar bar");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		FooProxy foo = (FooProxy) s.load( Foo.class, bar.getKey() );
 		assertTrue( "polymorphic", foo instanceof BarProxy );
 		assertTrue( "subclass property", ( (BarProxy) foo ).getBarString().equals( bar.getBarString() ) );
 		//System.out.println( s.print(foo) );
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRemoveContains() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save( baz );
 		s.flush();
 		assertTrue( s.contains(baz) );
 		s.evict( baz );
 		assertFalse( s.contains(baz) );
 		Baz baz2 = (Baz) s.load( Baz.class, baz.getCode() );
 		assertFalse( baz == baz2 );
 		s.delete(baz2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testCollectionOfSelf() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		s.save(bar);
 		bar.setAbstracts( new HashSet() );
 		bar.getAbstracts().add( bar );
 		Bar bar2 = new Bar();
 		bar.getAbstracts().add( bar2 );
 		bar.setFoo(bar);
 		s.save( bar2 );
 		s.getTransaction().commit();
 		s.close();
 
 		bar.setAbstracts( null );
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( bar, bar.getKey() );
 		assertTrue( "collection contains self", bar.getAbstracts().size() == 2 && bar.getAbstracts().contains( bar ) );
 		assertTrue( "association to self", bar.getFoo()==bar );
 		for ( Object o : bar.getAbstracts() ) {
 			s.delete( o );
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFind() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 
 		Bar bar = new Bar();
 		s.save(bar);
 		bar.setBarString("bar bar");
 		bar.setString("xxx");
 		Foo foo = new Foo();
 		s.save(foo);
 		foo.setString("foo bar");
 		s.save( new Foo() );
 		s.save( new Bar() );
 		List list1 = s.createQuery( "select foo from Foo foo where foo.string='foo bar'" ).list();
 		assertTrue( "find size", list1.size()==1 );
 		assertTrue( "find ==", list1.get(0)==foo );
 		List list2 = s.createQuery( "from Foo foo order by foo.string, foo.date" ).list();
 		assertTrue( "find size", list2.size()==4 );
 
 		list1 = s.createQuery( "from Foo foo where foo.class='B'" ).list();
 		assertTrue( "class special property", list1.size()==2);
 		list1 = s.createQuery( "from Foo foo where foo.class=Bar" ).list();
 		assertTrue( "class special property", list1.size()==2);
 		list1 = s.createQuery( "from Foo foo where foo.class=Bar" ).list();
 		list2 = s.createQuery( "select bar from Bar bar, Foo foo where bar.string = foo.string and not bar=foo" ).list();
 		assertTrue( "class special property", list1.size()==2);
 		assertTrue( "select from a subclass", list2.size()==1);
 		Trivial t = new Trivial();
 		s.save(t);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		list1 = s.createQuery( "from Foo foo where foo.string='foo bar'" ).list();
 		assertTrue( "find size", list1.size()==1 );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "find equals", ( (Foo) list1.get(0) ).equalsFoo(foo) );
 		list2 = s.createQuery( "select foo from Foo foo" ).list();
 		assertTrue( "find size", list2.size()==5 );
 		List list3 = s.createQuery( "from Bar bar where bar.barString='bar bar'" ).list();
 		assertTrue( "find size", list3.size()==1 );
 		assertTrue( "find same instance", list2.contains( list1.get(0) ) && list2.contains( list2.get(0) ) );
 		assertTrue( s.createQuery( "from Trivial" ).list().size()==1 );
 		doDelete( s, "from Trivial" );
 
 		list2 = s.createQuery( "from Foo foo where foo.date = ?" )
-				.setParameter( 0, new java.sql.Date(123), Hibernate.DATE )
+				.setParameter( 0, new java.sql.Date(123), StandardBasicTypes.DATE )
 				.list();
 		assertTrue ( "find by date", list2.size()==4 );
 		Iterator iter = list2.iterator();
 		while ( iter.hasNext() ) {
 			s.delete( iter.next() );
 		}
 		list2 = s.createQuery( "from Foo foo" ).list();
 		assertTrue( "find deleted", list2.size()==0);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteRecursive() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo x = new Foo();
 		Foo y = new Foo();
 		x.setFoo( y );
 		y.setFoo( x );
 		s.save( x );
 		s.save( y );
 		s.flush();
 		s.delete( y );
 		s.delete( x );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testReachability() throws Exception {
 		//first for unkeyed collections
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz1 = new Baz();
 		s.save(baz1);
 		Baz baz2 = new Baz();
 		s.save(baz2);
 		baz1.setIntArray( new int[] {1 ,2, 3, 4} );
 		baz1.setFooSet( new HashSet() );
 		Foo foo = new Foo();
 		s.save(foo);
 		baz1.getFooSet().add(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz1 = (Baz) s.load( Baz.class, baz1.getCode() );
 		baz2.setFooSet( baz1.getFooSet() ); baz1.setFooSet(null);
 		baz2.setIntArray( baz1.getIntArray() ); baz1.setIntArray(null);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz1 = (Baz) s.load( Baz.class, baz1.getCode() );
 		assertTrue( "unkeyed reachability", baz2.getIntArray().length==4 );
 		assertTrue( "unkeyed reachability", baz2.getFooSet().size()==1 );
 		assertTrue( "unkeyed reachability", baz1.getIntArray().length==0 );
 		assertTrue( "unkeyed reachability", baz1.getFooSet().size()==0 );
 		//System.out.println( s.print(baz1) + s.print(baz2) );
 		FooProxy fp = (FooProxy) baz2.getFooSet().iterator().next();
 		s.delete(fp);
 		s.delete(baz1);
 		s.delete(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		//now for collections of collections
 		s = openSession();
 		s.beginTransaction();
 		baz1 = new Baz();
 		s.save(baz1);
 		baz2 = new Baz();
 		s.save(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz1 = (Baz) s.load( Baz.class, baz1.getCode() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz1 = (Baz) s.load( Baz.class, baz1.getCode() );
 		s.delete(baz1);
 		s.delete(baz2);
 		s.getTransaction().commit();
 		s.close();
 
 		//now for keyed collections
 		s = openSession();
 		s.beginTransaction();
 		baz1 = new Baz();
 		s.save(baz1);
 		baz2 = new Baz();
 		s.save(baz2);
 		Foo foo1 = new Foo();
 		Foo foo2 = new Foo();
 		s.save(foo1); s.save(foo2);
 		baz1.setFooArray( new Foo[] { foo1, null, foo2 } );
 		baz1.setStringDateMap( new TreeMap() );
 		baz1.getStringDateMap().put("today", new Date( System.currentTimeMillis() ) );
 		baz1.getStringDateMap().put("tomorrow", new Date( System.currentTimeMillis() + 86400000 ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz1 = (Baz) s.load( Baz.class, baz1.getCode() );
 		baz2.setFooArray( baz1.getFooArray() ); baz1.setFooArray(null);
 		baz2.setStringDateMap( baz1.getStringDateMap() ); baz1.setStringDateMap(null);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz1 = (Baz) s.load( Baz.class, baz1.getCode() );
 		assertTrue( "reachability", baz2.getStringDateMap().size()==2 );
 		assertTrue( "reachability", baz2.getFooArray().length==3 );
 		assertTrue( "reachability", baz1.getStringDateMap().size()==0 );
 		assertTrue( "reachability", baz1.getFooArray().length==0 );
 		assertTrue( "null element", baz2.getFooArray()[1]==null );
 		assertTrue( "non-null element", baz2.getStringDateMap().get("today")!=null );
 		assertTrue( "non-null element", baz2.getStringDateMap().get("tomorrow")!=null );
 		assertTrue( "null element", baz2.getStringDateMap().get("foo")==null );
 		s.delete( baz2.getFooArray()[0] );
 		s.delete( baz2.getFooArray()[2] );
 		s.delete(baz1);
 		s.delete(baz2);
 		s.flush();
 		assertTrue( s.createQuery( "from java.lang.Object" ).list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPersistentLifecycle() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save(q);
 		q.setStuff("foo bar baz qux");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load( Qux.class, q.getKey() );
 		assertTrue( "lifecycle create", q.getCreated() );
 		assertTrue( "lifecycle load", q.getLoaded() );
 		assertTrue( "lifecycle subobject", q.getFoo()!=null );
 		s.delete(q);
 		assertTrue( "lifecycle delete", q.getDeleted() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		assertTrue( "subdeletion", s.createQuery( "from Foo foo" ).list().size()==0);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testIterators() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		for ( int i=0; i<10; i++ ) {
 			Qux q = new Qux();
 			Object qid = s.save(q);
 			assertTrue("not null", qid!=null);
 		}
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Iterator iter = s.createQuery( "from Qux q where q.stuff is null" ).iterate();
 		int count=0;
 		while ( iter.hasNext() ) {
 			Qux q = (Qux) iter.next();
 			q.setStuff("foo");
 			if (count==0 || count==5) iter.remove();
 			count++;
 		}
 		assertTrue("iterate", count==10);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
-		assertEquals( 8, doDelete( s, "from Qux q where q.stuff=?", "foo", Hibernate.STRING ) );
+		assertEquals( 8, doDelete( s, "from Qux q where q.stuff=?", "foo", StandardBasicTypes.STRING ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		iter = s.createQuery( "from Qux q" ).iterate();
 		assertTrue( "empty iterator", !iter.hasNext() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testVersioning() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		s.save(g);
 		GlarchProxy g2 = new Glarch();
 		s.save(g2);
 		Serializable gid = s.getIdentifier(g);
 		Serializable g2id = s.getIdentifier(g2);
 		g.setName("glarch");
 		txn.commit();
 		s.close();
 
 		sessionFactory().evict(Glarch.class);
 
 		s = openSession();
 		txn = s.beginTransaction();
 		g = (GlarchProxy) s.load( Glarch.class, gid );
 		s.lock(g, LockMode.UPGRADE);
 		g2 = (GlarchProxy) s.load( Glarch.class, g2id );
 		assertTrue( "version", g.getVersion()==1 );
 		assertTrue( "version", g.getDerivedVersion()==1 );
 		assertTrue( "version", g2.getVersion()==0 );
 		g.setName("foo");
 		assertTrue(
 			"find by version",
 				s.createQuery( "from Glarch g where g.version=2" ).list().size()==1
 		);
 		g.setName("bar");
 		txn.commit();
 		s.close();
 
 		sessionFactory().evict(Glarch.class);
 
 		s = openSession();
 		txn = s.beginTransaction();
 		g = (GlarchProxy) s.load( Glarch.class, gid );
 		g2 = (GlarchProxy) s.load( Glarch.class, g2id );
 		assertTrue( "version", g.getVersion()==3 );
 		assertTrue( "version", g.getDerivedVersion()==3 );
 		assertTrue( "version", g2.getVersion()==0 );
 		g.setNext(null);
 		g2.setNext(g);
 		s.delete(g2);
 		s.delete(g);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testVersionedCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		s.save(g);
 		g.setProxyArray( new GlarchProxy[] { g } );
 		String gid = (String) s.getIdentifier(g);
 		ArrayList list = new ArrayList();
 		list.add("foo");
 		g.setStrings(list);
 		HashSet set = new HashSet();
 		set.add( g );
 		g.setProxySet( set );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( g.getStrings().size()==1 );
 		assertTrue( g.getProxyArray().length==1 );
 		assertTrue( g.getProxySet().size()==1 );
 		assertTrue( "versioned collection before", g.getVersion() == 1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( g.getStrings().get(0).equals("foo") );
 		assertTrue( g.getProxyArray()[0]==g );
 		assertTrue( g.getProxySet().iterator().next()==g );
 		assertTrue( "versioned collection before", g.getVersion() == 1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( "versioned collection before", g.getVersion() == 1 );
 		g.getStrings().add( "bar" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( "versioned collection after", g.getVersion()==2 );
 		assertTrue( "versioned collection after", g.getStrings().size() == 2 );
 		g.setProxyArray( null );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( "versioned collection after", g.getVersion()==3 );
 		assertTrue( "versioned collection after", g.getProxyArray().length == 0 );
 		g.setFooComponents( new ArrayList() );
 		g.setProxyArray( null );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( "versioned collection after", g.getVersion()==4 );
 		s.delete(g);
 		s.flush();
 		assertTrue( s.createQuery( "from java.lang.Object" ).list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRecursiveLoad() throws Exception {
 		//Non polymorphic class (there is an implementation optimization
 		//being tested here)
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		GlarchProxy last = new Glarch();
 		s.save(last);
 		last.setOrder( (short) 0 );
 		for (int i=0; i<5; i++) {
 			GlarchProxy next = new Glarch();
 			s.save(next);
 			last.setNext(next);
 			last = next;
 			last.setOrder( (short) (i+1) );
 		}
 		Iterator iter = s.createQuery( "from Glarch g" ).iterate();
 		while ( iter.hasNext() ) {
 			iter.next();
 		}
 		List list = s.createQuery( "from Glarch g" ).list();
 		assertTrue( "recursive find", list.size()==6 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		list = s.createQuery( "from Glarch g" ).list();
 		assertTrue( "recursive iter", list.size()==6 );
 		list = s.createQuery( "from Glarch g where g.next is not null" ).list();
 		assertTrue( "recursive iter", list.size()==5 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		iter = s.createQuery( "from Glarch g order by g.order asc" ).iterate();
 		while ( iter.hasNext() ) {
 			GlarchProxy g = (GlarchProxy) iter.next();
 			assertTrue( "not null", g!=null );
 			iter.remove();
 		}
 		txn.commit();
 		s.close();
 
 		//Same thing but using polymorphic class (no optimisation possible):
 		s = openSession();
 		txn = s.beginTransaction();
 		FooProxy flast = new Bar();
 		s.save(flast);
 		flast.setString( "foo0" );
 		for (int i=0; i<5; i++) {
 			FooProxy foo = new Bar();
 			s.save(foo);
 			flast.setFoo(foo);
 			flast = flast.getFoo();
 			flast.setString( "foo" + (i+1) );
 		}
 		iter = s.createQuery( "from Foo foo" ).iterate();
 		while ( iter.hasNext() ) {
 			iter.next();
 		}
 		list = s.createQuery( "from Foo foo" ).list();
 		assertTrue( "recursive find", list.size()==6 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		list = s.createQuery( "from Foo foo" ).list();
 		assertTrue( "recursive iter", list.size()==6 );
 		iter = list.iterator();
 		while ( iter.hasNext() ) {
 			assertTrue( "polymorphic recursive load", iter.next() instanceof BarProxy );
 		}
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		iter = s.createQuery( "from Foo foo order by foo.string asc" ).iterate();
 		while ( iter.hasNext() ) {
 			BarProxy bar = (BarProxy) iter.next();
 			assertTrue( "not null", bar!=null );
 			iter.remove();
 		}
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testScrollableIterator() throws Exception {
 		// skip if not one of these named dialects
 		boolean match = getDialect() instanceof DB2Dialect
 				|| getDialect() instanceof SybaseDialect
 				|| getDialect() instanceof HSQLDialect
 				|| getDialect() instanceof Oracle8iDialect // 9i/10g too because of inheritence...
 				;
 		if ( ! match ) {
 			return;
 		}
 
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		s.save( new Foo() );
 		s.save( new Foo() );
 		s.save( new Foo() );
 		s.save( new Bar() );
 		Query query = s.createQuery("select f, f.integer from Foo f");
 		assertTrue( query.getReturnTypes().length==2 );
 		ScrollableResults iter = query.scroll();
 		assertTrue( iter.next() );
 		assertTrue( iter.scroll(1) );
 		FooProxy f2 = (FooProxy) iter.get()[0];
 		assertTrue( f2!=null );
 		assertTrue( iter.scroll(-1) );
 		Object f1 = iter.get(0);
 		iter.next();
 		assertTrue( f1!=null && iter.get(0)==f2 );
 		iter.getInteger(1);
 
 		assertTrue( !iter.scroll(100) );
 		assertTrue( iter.first() );
 		assertTrue( iter.scroll(3) );
 		Object f4 = iter.get(0);
 		assertTrue( f4!=null );
 		assertTrue( !iter.next() );
 		assertTrue( iter.first() );
 		assertTrue( iter.get(0)==f1 );
 		assertTrue( iter.last() );
 		assertTrue( iter.get(0)==f4 );
 		assertTrue( iter.previous() );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		query = s.createQuery("select f, f.integer from Foo f");
 		assertTrue( query.getReturnTypes().length==2 );
 		iter = query.scroll();
 		assertTrue( iter.next() );
 		assertTrue( iter.scroll(1) );
 		f2 = (FooProxy) iter.get()[0];
 		assertTrue( f2!=null );
 		assertTrue( f2.getString()!=null  && f2.getComponent().getImportantDates().length > 0 );
 		assertTrue( iter.scroll(-1) );
 		f1 = iter.get(0);
 		iter.next();
 		assertTrue( f1!=null && iter.get(0)==f2 );
 		iter.getInteger(1);
 
 		assertTrue( !iter.scroll(100) );
 		assertTrue( iter.first() );
 		assertTrue( iter.scroll(3) );
 		f4 = iter.get(0);
 		assertTrue( f4!=null );
 		assertTrue( !iter.next() );
 		assertTrue( iter.first() );
 		assertTrue( iter.get(0)==f1 );
 		assertTrue( iter.last() );
 		assertTrue( iter.get(0)==f4 );
 		assertTrue( iter.previous() );
 		int i = 0;
 		for ( Object entity : s.createQuery( "from Foo" ).list() ) {
 			i++;
 			s.delete( entity );
 		}
 		assertEquals( 4, i );
 		s.flush();
 		assertTrue( s.createQuery( "from java.lang.Object" ).list().size()==0 );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testMultiColumnQueries() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		foo.setFoo(foo1);
 		List l = s.createQuery( "select parent, child from Foo parent, Foo child where parent.foo = child" ).list();
 		assertTrue( "multi-column find", l.size()==1 );
 
 		Iterator rs = s.createQuery(
 				"select count(distinct child.id), count(distinct parent.id) from Foo parent, Foo child where parent.foo = child"
 		).iterate();
 		Object[] row = (Object[]) rs.next();
 		assertTrue( "multi-column count", ( (Long) row[0] ).intValue()==1 );
 		assertTrue( "multi-column count", ( (Long) row[1] ).intValue()==1 );
 		assertTrue( !rs.hasNext() );
 
 		rs = s.createQuery( "select child.id, parent.id, child.long from Foo parent, Foo child where parent.foo = child" )
 				.iterate();
 		row = (Object[]) rs.next();
 		assertTrue( "multi-column id", row[0].equals( foo.getFoo().getKey() ) );
 		assertTrue( "multi-column id", row[1].equals( foo.getKey() ) );
 		assertTrue( "multi-column property", row[2].equals( foo.getFoo().getLong() ) );
 		assertTrue( !rs.hasNext() );
 
 		rs = s.createQuery(
 				"select child.id, parent.id, child.long, child, parent.foo from Foo parent, Foo child where parent.foo = child"
 		).iterate();
 		row = (Object[]) rs.next();
 		assertTrue(
 			foo.getFoo().getKey().equals( row[0] ) &&
 			foo.getKey().equals( row[1] ) &&
 			foo.getFoo().getLong().equals( row[2] ) &&
 			row[3] == foo.getFoo() &&
 			row[3]==row[4]
 		);
 		assertTrue( !rs.hasNext() );
 
 		row = (Object[]) l.get(0);
 		assertTrue( "multi-column find", row[0]==foo && row[1]==foo.getFoo() );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		Iterator iter = s.createQuery(
 				"select parent, child from Foo parent, Foo child where parent.foo = child and parent.string='a string'"
 		).iterate();
 		int deletions=0;
 		while ( iter.hasNext() ) {
 			Object[] pnc = (Object[]) iter.next();
 			s.delete( pnc[0] );
 			s.delete( pnc[1] );
 			deletions++;
 		}
 		assertTrue("multi-column iterate", deletions==1);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteTransient() throws Exception {
 		Fee fee = new Fee();
 		Fee fee2 = new Fee();
 		fee2.setAnotherFee(fee);
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.save(fee);
 		s.save(fee2);
 		s.flush();
 		fee.setCount(123);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete(fee);
 		s.delete(fee2);
 		//foo.setAnotherFee(null);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		assertTrue( s.createQuery( "from Fee fee" ).list().size()==0 );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteUpdatedTransient() throws Exception {
 		Fee fee = new Fee();
 		Fee fee2 = new Fee();
 		fee2.setAnotherFee(fee);
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.save(fee);
 		s.save(fee2);
 		s.flush();
 		fee.setCount(123);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		s.update(fee);
 		//fee2.setAnotherFee(null);
 		s.update(fee2);
 		s.delete(fee);
 		s.delete(fee2);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		assertTrue( s.createQuery( "from Fee fee" ).list().size()==0 );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testUpdateOrder() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fee fee1 = new Fee();
 		s.save(fee1);
 		Fee fee2 = new Fee();
 		fee1.setFee(fee2);
 		fee2.setFee(fee1);
 		fee2.setFees( new HashSet() );
 		Fee fee3 = new Fee();
 		fee3.setFee(fee1);
 		fee3.setAnotherFee(fee2);
 		fee2.setAnotherFee(fee3);
 		s.save(fee3);
 		s.save(fee2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		fee1.setCount(10);
 		fee2.setCount(20);
 		fee3.setCount(30);
 		s.update(fee1);
 		s.update(fee2);
 		s.update(fee3);
 		s.flush();
 		s.delete(fee1);
 		s.delete(fee2);
 		s.delete(fee3);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		assertTrue( s.createQuery( "from Fee fee" ).list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testUpdateFromTransient() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fee fee1 = new Fee();
 		s.save(fee1);
 		Fee fee2 = new Fee();
 		fee1.setFee(fee2);
 		fee2.setFee(fee1);
 		fee2.setFees( new HashSet() );
 		Fee fee3 = new Fee();
 		fee3.setFee(fee1);
 		fee3.setAnotherFee(fee2);
 		fee2.setAnotherFee(fee3);
 		s.save(fee3);
 		s.save(fee2);
 		s.getTransaction().commit();
 		s.close();
 
 		fee1.setFi("changed");
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(fee1);
 		s.getTransaction().commit();
 		s.close();
 
 		Qux q = new Qux("quxxy");
 		q.setTheKey(0);
 		fee1.setQux(q);
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(fee1);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		fee1 = (Fee) s.load( Fee.class, fee1.getKey() );
 		assertTrue( "updated from transient", fee1.getFi().equals("changed") );
 		assertTrue( "unsaved value", fee1.getQux()!=null );
 		s.delete( fee1.getQux() );
 		fee1.setQux(null);
 		s.getTransaction().commit();
 		s.close();
 
 		fee2.setFi("CHANGED");
 		fee2.getFees().add("an element");
 		fee1.setFi("changed again");
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(fee2);
 		s.update( fee1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Fee fee = new Fee();
 		s.load( fee, fee2.getKey() );
 		fee1 = (Fee) s.load( Fee.class, fee1.getKey() );
 		assertTrue( "updated from transient", fee1.getFi().equals("changed again") );
 		assertTrue( "updated from transient", fee.getFi().equals("CHANGED") );
 		assertTrue( "updated collection", fee.getFees().contains("an element") );
 		s.getTransaction().commit();
 		s.close();
 
 		fee.getFees().clear();
 		fee.getFees().add("new element");
 		fee1.setFee(null);
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(fee);
 		s.saveOrUpdate(fee1);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( fee, fee.getKey() );
 		assertTrue( "update", fee.getAnotherFee()!=null );
 		assertTrue( "update", fee.getFee()!=null );
 		assertTrue( "update", fee.getAnotherFee().getFee()==fee.getFee() );
 		assertTrue( "updated collection", fee.getFees().contains("new element") );
 		assertTrue( "updated collection", !fee.getFees().contains("an element") );
 		s.getTransaction().commit();
 		s.close();
 
 		fee.setQux( new Qux("quxy") );
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(fee);
 		s.getTransaction().commit();
 		s.close();
 
 		fee.getQux().setStuff("xxx");
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(fee);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( fee, fee.getKey() );
 		assertTrue( "cascade update", fee.getQux()!=null );
 		assertTrue( "cascade update", fee.getQux().getStuff().equals("xxx") );
 		assertTrue( "update", fee.getAnotherFee()!=null );
 		assertTrue( "update", fee.getFee()!=null );
 		assertTrue( "update", fee.getAnotherFee().getFee()==fee.getFee() );
 		fee.getAnotherFee().setAnotherFee(null);
 		s.delete(fee);
 		doDelete( s, "from Fee fee" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		assertTrue( s.createQuery( "from Fee fee" ).list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testArraysOfTimes() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz() ;
 		s.save(baz);
 		baz.setDefaults();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz.getTimeArray()[2] = new Date(123);
 		baz.getTimeArray()[3] = new java.sql.Time(1234);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		s.delete( baz );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponents() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 //		foo.setComponent( new FooComponent("foo", 69, null, new FooComponent("bar", 96, null, null) ) );
 		s.save(foo);
 		foo.getComponent().setName( "IFA" );
 		txn.commit();
 		s.close();
 
 		foo.setComponent( null );
 
 		s = openSession();
 		txn = s.beginTransaction();
 		s.load( foo, foo.getKey() );
 		assertTrue(
 			"save components",
 			foo.getComponent().getName().equals("IFA") &&
 			foo.getComponent().getSubcomponent().getName().equals("bar")
 		);
 		assertTrue( "cascade save via component", foo.getComponent().getGlarch() != null );
 		foo.getComponent().getSubcomponent().setName("baz");
 		txn.commit();
 		s.close();
 
 		foo.setComponent(null);
 
 		s = openSession();
 		txn = s.beginTransaction();
 		s.load( foo, foo.getKey() );
 		assertTrue(
 			"update components",
 			foo.getComponent().getName().equals("IFA") &&
 			foo.getComponent().getSubcomponent().getName().equals("baz")
 		);
 		s.delete(foo);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		foo = new Foo();
 		s.save( foo );
 		foo.setCustom( new String[] { "one", "two" } );
 		assertTrue( s.createQuery( "from Foo foo where foo.custom.s1 = 'one'" ).list().get(0)==foo );
 		s.delete( foo );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNoForeignKeyViolations() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g1 = new Glarch();
 		Glarch g2 = new Glarch();
 		g1.setNext(g2);
 		g2.setNext(g1);
 		s.save(g1);
 		s.save(g2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List l = s.createQuery( "from Glarch g where g.next is not null" ).list();
 		s.delete( l.get(0) );
 		s.delete( l.get(1) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLazyCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save(q);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load( Qux.class, q.getKey() );
 		s.getTransaction().commit();
 		s.close();
 
 		System.out.println("Two exceptions are supposed to occur:");
 		boolean ok = false;
 		try {
 			q.getMoreFums().isEmpty();
 		}
 		catch (LazyInitializationException e) {
 			ok = true;
 		}
 		assertTrue( "lazy collection with one-to-many", ok );
 
 		ok = false;
 		try {
 			q.getFums().isEmpty();
 		}
 		catch (LazyInitializationException e) {
 			ok = true;
 		}
 		assertTrue( "lazy collection with many-to-many", ok );
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load( Qux.class, q.getKey() );
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNewSessionLifecycle() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Serializable fid = null;
 		try {
 			Foo f = new Foo();
 			s.save(f);
 			fid = s.getIdentifier(f);
 			s.getTransaction().commit();
 		}
 		catch (Exception e) {
 			s.getTransaction().rollback();
 			throw e;
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		try {
 			Foo f = new Foo();
 			s.delete(f);
 			s.getTransaction().commit();
 		}
 		catch (Exception e) {
 			s.getTransaction().rollback();
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		try {
 			Foo f = (Foo) s.load(Foo.class, fid, LockMode.UPGRADE);
 			s.delete(f);
 			s.flush();
 			s.getTransaction().commit();
 		}
 		catch (Exception e) {
 			s.getTransaction().rollback();
 			throw e;
 		}
 		finally {
 			assertTrue( s.close()==null );
 		}
 	}
 
 	@Test
 	public void testOrderBy() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		List list = s.createQuery(
 				"select foo from Foo foo, Fee fee where foo.dependent = fee order by foo.string desc, foo.component.count asc, fee.id"
 		).list();
 		assertTrue( "order by", list.size()==1 );
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		foo.setFoo(foo2);
 		list = s.createQuery(
 				"select foo.foo, foo.dependent from Foo foo order by foo.foo.string desc, foo.component.count asc, foo.dependent.id"
 		).list();
 		assertTrue( "order by", list.size()==1 );
 		list = s.createQuery( "select foo from Foo foo order by foo.dependent.id, foo.dependent.fi" ).list();
 		assertTrue( "order by", list.size()==2 );
 		s.delete(foo);
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Many manyB = new Many();
 		s.save(manyB);
 		One oneB = new One();
 		s.save(oneB);
 		oneB.setValue("b");
 		manyB.setOne(oneB);
 		Many manyA = new Many();
 		s.save(manyA);
 		One oneA = new One();
 		s.save(oneA);
 		oneA.setValue("a");
 		manyA.setOne(oneA);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "SELECT one FROM " + One.class.getName() + " one ORDER BY one.value ASC" ).list();
 		assertEquals( 2, results.size() );
 		assertEquals( "'a' isn't first element", "a", ( (One) results.get(0) ).getValue() );
 		assertEquals( "'b' isn't second element", "b", ( (One) results.get(1) ).getValue() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		results = s.createQuery( "SELECT many.one FROM " + Many.class.getName() + " many ORDER BY many.one.value ASC, many.one.id" )
 				.list();
 		assertEquals( 2, results.size() );
 		assertEquals( 2, results.size() );
 		assertEquals( "'a' isn't first element", "a", ( (One) results.get(0) ).getValue() );
 		assertEquals( "'b' isn't second element", "b", ( (One) results.get(1) ).getValue() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		oneA = (One)s.load(One.class, oneA.getKey());
 		manyA = (Many)s.load(Many.class, manyA.getKey());
 		oneB = (One)s.load(One.class, oneB.getKey());
 		manyB = (Many)s.load(Many.class, manyB.getKey());
 		s.delete(manyA);
 		s.delete(oneA);
 		s.delete(manyB);
 		s.delete(oneB);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOne() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		One one = new One();
 		s.save(one);
 		one.setValue( "yada" );
 		Many many = new Many();
 		many.setOne( one );
 		s.save( many );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		one = (One) s.load( One.class, one.getKey() );
 		one.getManies().size();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		many = (Many) s.load( Many.class, many.getKey() );
 		assertTrue( "many-to-one assoc", many.getOne()!=null );
 		s.delete( many.getOne() );
 		s.delete(many);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo f = new Foo();
 		s.save(f);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( s.load( Foo.class, f.getKey() ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testProxyArray() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		Glarch g1 = new Glarch();
 		Glarch g2 = new Glarch();
 		g.setProxyArray( new GlarchProxy[] { g1, g2 } );
 		Glarch g3 = new Glarch();
 		s.save(g3);
 		g2.setProxyArray( new GlarchProxy[] {null, g3, g} );
 		Set set = new HashSet();
 		set.add(g1);
 		set.add(g2);
 		g.setProxySet(set);
 		s.save(g);
 		s.save(g1);
 		s.save(g2);
 		Serializable id = s.getIdentifier(g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( "array of proxies", g.getProxyArray().length==2 );
 		assertTrue( "array of proxies", g.getProxyArray()[0]!=null );
 		assertTrue("deferred load test",g.getProxyArray()[1].getProxyArray()[0]==null );
 		assertTrue("deferred load test",g.getProxyArray()[1].getProxyArray()[2]==g );
 		assertTrue( "set of proxies", g.getProxySet().size()==2 );
 		Iterator iter = s.createQuery( "from Glarch g" ).iterate();
 		while ( iter.hasNext() ) {
 			iter.next();
 			iter.remove();
 		}
 		s.getTransaction().commit();
 		s.disconnect();
 		SerializationHelper.deserialize( SerializationHelper.serialize(s) );
 		s.close();
 	}
 
 	@Test
 	public void testCache() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Immutable im = new Immutable();
 		s.save(im);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( im, im.getId() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( im, im.getId() );
 		assertEquals(
 				"cached object identity",
 				im,
 				s.createQuery( "from Immutable im where im = ?" ).setParameter(
-						0, im, Hibernate.entity( Immutable.class )
+						0, im, s.getTypeHelper().entity( Immutable.class )
 				).uniqueResult()
 		);
 		s.doWork(
 				new AbstractWork() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						connection.createStatement().executeUpdate("delete from immut");
 					}
 				}
 		);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFindLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		FooProxy foo = new Foo();
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (FooProxy) s.createQuery( "from Foo foo" ).list().get(0);
 		FooProxy foo2 = (FooProxy) s.load( Foo.class, foo.getKey() );
 		assertTrue( "find returns same object as load", foo == foo2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2 = (FooProxy) s.load( Foo.class, foo.getKey() );
 		foo = (FooProxy) s.createQuery( "from Foo foo" ).list().get(0);
 		assertTrue( "find returns same object as load", foo == foo2 );
 		doDelete( s, "from Foo foo" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRefresh() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save( foo );
 		s.flush();
 		s.doWork(
 				new AbstractWork() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						final String sql = "update " + getDialect().openQuote() + "foos" + getDialect().closeQuote() + " set long_ = -3";
 						connection.createStatement().executeUpdate( sql );
 					}
 				}
 		);
 		s.refresh(foo);
 		assertTrue( foo.getLong().longValue() == -3l );
 		assertTrue( s.getCurrentLockMode(foo)==LockMode.READ );
 		s.refresh(foo, LockMode.UPGRADE);
 		if ( getDialect().supportsOuterJoinForUpdate() ) {
 			assertTrue( s.getCurrentLockMode(foo)==LockMode.UPGRADE );
 		}
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAutoFlush() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		FooProxy foo = new Foo();
 		s.save(foo);
 		assertTrue( "autoflush create", s.createQuery( "from Foo foo" ).list().size()==1 );
 		foo.setChar( new Character('X') );
 		assertTrue( "autoflush update", s.createQuery( "from Foo foo where foo.char='X'" ).list().size()==1 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		foo = (FooProxy) s.load( Foo.class, foo.getKey() );
 		//s.update( new Foo(), foo.getKey() );
 		//assertTrue( s.find("from Foo foo where not foo.char='X'").size()==1, "autoflush update" );
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			foo.setBytes( "osama".getBytes() );
 			assertTrue( "autoflush collection update",
 					s.createQuery( "from Foo foo where 111 in elements(foo.bytes)" ).list().size()==1 );
 			foo.getBytes()[0] = 69;
 			assertTrue( "autoflush collection update",
 					s.createQuery( "from Foo foo where 69 in elements(foo.bytes)" ).list()
 							.size()==1 );
 		}
 		s.delete(foo);
 		assertTrue( "autoflush delete", s.createQuery( "from Foo foo" ).list().size()==0 );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testVeto() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Vetoer v = new Vetoer();
 		s.save(v);
 		s.save(v);
 		s.getTransaction().commit();
 		s.close();
 		s = openSession();
 		s.beginTransaction();
 		s.update( v );
 		s.update( v );
 		s.delete( v );
 		s.delete( v );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSerializableType() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Vetoer v = new Vetoer();
 		v.setStrings( new String[] {"foo", "bar", "baz"} );
 		s.save( v ); Serializable id = s.save(v);
 		v.getStrings()[1] = "osama";
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		v = (Vetoer) s.load(Vetoer.class, id);
 		assertTrue( "serializable type", v.getStrings()[1].equals( "osama" ) );
 		s.delete(v); s.delete( v );
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAutoFlushCollections() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		baz.getStringArray()[0] = "bark";
 		Iterator i = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		boolean found = false;
 		while ( i.hasNext() ) {
 			if ( "bark".equals( i.next() ) ) found = true;
 		}
 		assertTrue(found);
 		baz.setStringArray(null);
 		i = s.createQuery( "select distinct elements(baz.stringArray) from Baz baz" ).iterate();
 		assertTrue( !i.hasNext() );
 		baz.setStringArray( new String[] { "foo", "bar" } );
 		i = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		assertTrue( i.hasNext() );
 
 		Foo foo = new Foo();
 		s.save(foo);
 		s.flush();
 		baz.setFooArray( new Foo[] {foo} );
 
 		i = s.createQuery( "select foo from Baz baz join baz.fooArray foo" ).iterate();
 		found = false;
 		while ( i.hasNext() ) {
 			if ( foo==i.next() ) found = true;
 		}
 		assertTrue(found);
 
 		baz.getFooArray()[0] = null;
 		i = s.createQuery( "select foo from Baz baz join baz.fooArray foo" ).iterate();
 		assertTrue( !i.hasNext() );
 		baz.getFooArray()[0] = foo;
 		i = s.createQuery( "select elements(baz.fooArray) from Baz baz" ).iterate();
 		assertTrue( i.hasNext() );
 
 		if ( !(getDialect() instanceof MySQLDialect)
 				&& !(getDialect() instanceof HSQLDialect)
 				&& !(getDialect() instanceof InterbaseDialect)
 				&& !(getDialect() instanceof PointbaseDialect)
 				&& !(getDialect() instanceof SAPDBDialect) )  {
 			baz.getFooArray()[0] = null;
 			i = s.createQuery( "from Baz baz where ? in elements(baz.fooArray)" )
-					.setParameter( 0, foo, Hibernate.entity( Foo.class ) )
+					.setParameter( 0, foo, s.getTypeHelper().entity( Foo.class ) )
 					.iterate();
 			assertTrue( !i.hasNext() );
 			baz.getFooArray()[0] = foo;
 			i = s.createQuery( "select foo from Foo foo where foo in (select elt from Baz baz join baz.fooArray elt)" )
 					.iterate();
 			assertTrue( i.hasNext() );
 		}
 		s.delete(foo);
 		s.delete(baz);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testUserProvidedConnection() throws Exception {
 		ConnectionProvider dcp = ConnectionProviderBuilder.buildConnectionProvider();
 		Session s = sessionFactory().withOptions().connection( dcp.getConnection() ).openSession();
 		Transaction tx = s.beginTransaction();
 		s.createQuery( "from Fo" ).list();
 		tx.commit();
 		Connection c = s.disconnect();
 		assertTrue( c != null );
 		s.reconnect( c );
 		tx = s.beginTransaction();
 		s.createQuery( "from Fo" ).list();
 		tx.commit();
 		assertTrue( s.close() == c );
 		c.close();
 	}
 
 	@Test
 	public void testCachedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		( (FooComponent) baz.getTopComponents().get(0) ).setCount(99);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( ((FooComponent) baz.getTopComponents().get( 0 )).getCount() == 99 );
 		s.delete( baz );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComplicatedQuery() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 		Serializable id = s.save(foo);
 		assertTrue( id != null );
 		Qux q = new Qux("q");
 		foo.getDependent().setQux(q);
 		s.save( q );
 		q.getFoo().setString( "foo2" );
 		//s.flush();
 		//s.connection().commit();
 		assertTrue(
 				s.createQuery( "from Foo foo where foo.dependent.qux.foo.string = 'foo2'" ).iterate().hasNext()
 		);
 		s.delete( foo );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoadAfterDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		Serializable id = s.save(foo);
 		s.flush();
 		s.delete(foo);
 		boolean err=false;
 		try {
 			s.load(Foo.class, id);
 		}
 		catch (ObjectNotFoundException ode) {
 			err=true;
 		}
 		assertTrue(err);
 		s.flush();
 		err=false;
 		try {
 			( (FooProxy) s.load(Foo.class, id) ).getBool();
 		}
 		catch (ObjectNotFoundException onfe) {
 			err=true;
 		}
 		assertTrue(err);
 		id = FumTest.fumKey( "abc" ); //yuck!!
 		Fo fo = Fo.newFo( (FumCompositeID) id );
 		s.save(fo);
 		s.flush();
 		s.delete(fo);
 		err=false;
 		try {
 			s.load(Fo.class, id);
 		}
 		catch (ObjectNotFoundException ode) {
 			err=true;
 		}
 		assertTrue(err);
 		s.flush();
 		err=false;
 		try {
 			s.load(Fo.class, id);
 		}
 		catch (ObjectNotFoundException onfe) {
 			err=true;
 		}
 		assertTrue(err);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testObjectType() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		Foo foo = new Foo();
 		g.setAny( foo );
 		Serializable gid = s.save( g );
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( g.getAny()!=null && g.getAny() instanceof FooProxy );
 		s.delete( g.getAny() );
 		s.delete( g );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAny() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		One one = new One();
 		BarProxy foo = new Bar();
 		foo.setObject(one);
 		Serializable fid = s.save(foo);
 		Serializable oid = one.getKey();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from Bar bar where bar.object.id = ? and bar.object.class = ?" )
-				.setParameter( 0, oid, Hibernate.LONG )
-				.setParameter( 1, new Character('O'), Hibernate.CHARACTER )
+				.setParameter( 0, oid, StandardBasicTypes.LONG )
+				.setParameter( 1, new Character('O'), StandardBasicTypes.CHARACTER )
 				.list();
 		assertEquals( 1, results.size() );
 		results = s.createQuery( "select one from One one, Bar bar where bar.object.id = one.id and bar.object.class = 'O'" )
 				.list();
 		assertEquals( 1, results.size() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (BarProxy) s.load(Foo.class, fid);
 		assertTrue( foo.getObject()!=null && foo.getObject() instanceof One && s.getIdentifier( foo.getObject() ).equals(oid) );
 		//s.delete( foo.getObject() );
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedCompositeID() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Location l = new Location();
 		l.setCountryCode("AU");
 		l.setDescription("foo bar");
 		l.setLocale( Locale.getDefault() );
 		l.setStreetName("Brunswick Rd");
 		l.setStreetNumber(300);
 		l.setCity("Melbourne");
 		s.save(l);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.setFlushMode(FlushMode.MANUAL);
 		l = (Location) s.createQuery( "from Location l where l.countryCode = 'AU' and l.description='foo bar'" )
 				.list()
 				.get(0);
 		assertTrue( l.getCountryCode().equals("AU") );
 		assertTrue( l.getCity().equals("Melbourne") );
 		assertTrue( l.getLocale().equals( Locale.getDefault() ) );
 		assertTrue( s.createCriteria(Location.class).add( Restrictions.eq( "streetNumber", new Integer(300) ) ).list().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		l.setDescription("sick're");
 		s.update(l);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		l = new Location();
 		l.setCountryCode("AU");
 		l.setDescription("foo bar");
 		l.setLocale(Locale.ENGLISH);
 		l.setStreetName("Brunswick Rd");
 		l.setStreetNumber(300);
 		l.setCity("Melbourne");
 		assertTrue( l==s.load(Location.class, l) );
 		assertTrue( l.getLocale().equals( Locale.getDefault() ) );
 		s.delete(l);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAutosaveChildren() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars(bars);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		baz.getCascadingBars().add( new Bar() );
 		baz.getCascadingBars().add( new Bar() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( baz.getCascadingBars().size()==2 );
 		assertTrue( baz.getCascadingBars().iterator().next()!=null );
 		baz.getCascadingBars().clear(); //test all-delete-orphan;
 		s.flush();
 		assertTrue( s.createQuery( "from Bar bar" ).list().size()==0 );
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testOrphanDelete() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars(bars);
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		bars = baz.getCascadingBars();
 		assertEquals( 4, bars.size() );
 		bars.remove( bars.iterator().next() );
 		assertEquals( 3, s.createQuery( "From Bar bar" ).list().size() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		bars = baz.getCascadingBars();
 		assertEquals( 3, bars.size() );
 		bars.remove( bars.iterator().next() );
 		s.delete(baz);
 		bars.remove( bars.iterator().next() );
 		assertEquals( 0, s.createQuery( "From Bar bar" ).list().size() );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testTransientOrphanDelete() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars(bars);
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		List foos = new ArrayList();
 		foos.add( new Foo() );
 		foos.add( new Foo() );
 		baz.setFooBag(foos);
 		s.save(baz);
 		Iterator i = new JoinedIterator( new Iterator[] {foos.iterator(), bars.iterator()} );
 		while ( i.hasNext() ) {
 			FooComponent cmp = ( (Foo) i.next() ).getComponent();
 			s.delete( cmp.getGlarch() );
 			cmp.setGlarch(null);
 		}
 		t.commit();
 		s.close();
 
 		bars.remove( bars.iterator().next() );
 		foos.remove(1);
 		s = openSession();
 		t = s.beginTransaction();
 		s.update(baz);
 		assertEquals( 2, s.createQuery( "From Bar bar" ).list().size() );
 		assertEquals( 3, s.createQuery( "From Foo foo" ).list().size() );
 		t.commit();
 		s.close();
 
 		foos.remove(0);
 		s = openSession();
 		t = s.beginTransaction();
 		s.update(baz);
 		bars.remove( bars.iterator().next() );
 		assertEquals( 1, s.createQuery( "From Foo foo" ).list().size() );
 		s.delete(baz);
 		//s.flush();
 		assertEquals( 0, s.createQuery( "From Foo foo" ).list().size() );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProxiesInCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Bar bar = new Bar();
 		Bar bar2 = new Bar();
 		s.save(bar);
 		Serializable bar2id = s.save(bar2);
 		baz.setFooArray( new Foo[] { bar, bar2 } );
 		HashSet set = new HashSet();
 		bar = new Bar();
 		s.save(bar);
 		set.add(bar);
 		baz.setFooSet(set);
 		set = new HashSet();
 		set.add( new Bar() );
 		set.add( new Bar() );
 		baz.setCascadingBars(set);
 		ArrayList list = new ArrayList();
 		list.add( new Foo() );
 		baz.setFooBag(list);
 		Serializable id = s.save(baz);
 		Serializable bid = ( (Bar) baz.getCascadingBars().iterator().next() ).getKey();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		BarProxy barprox = (BarProxy) s.load(Bar.class, bid);
 		BarProxy bar2prox = (BarProxy) s.load(Bar.class, bar2id);
 		assertTrue(bar2prox instanceof HibernateProxy);
 		assertTrue(barprox instanceof HibernateProxy);
 		baz = (Baz) s.load(Baz.class, id);
 		Iterator i = baz.getCascadingBars().iterator();
 		BarProxy b1 = (BarProxy) i.next();
 		BarProxy b2 = (BarProxy) i.next();
 		assertTrue( ( b1==barprox && !(b2 instanceof HibernateProxy) ) || ( b2==barprox && !(b1 instanceof HibernateProxy) ) ); //one-to-many
 		assertTrue( baz.getFooArray()[0] instanceof HibernateProxy ); //many-to-many
 		assertTrue( baz.getFooArray()[1]==bar2prox );
 		if ( !isOuterJoinFetchingDisabled() ) assertTrue( !(baz.getFooBag().iterator().next() instanceof HibernateProxy) ); //many-to-many outer-join="true"
 		assertTrue( !(baz.getFooSet().iterator().next() instanceof HibernateProxy) ); //one-to-many
 		doDelete( s, "from Baz" );
 		doDelete( s, "from Foo" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPSCache() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		for ( int i=0; i<10; i++ ) s.save( new Foo() );
 		Query q = s.createQuery("from Foo");
 		q.setMaxResults(2);
 		q.setFirstResult(5);
 		assertTrue( q.list().size()==2 );
 		q = s.createQuery("from Foo");
 		assertTrue( q.list().size()==10 );
 		assertTrue( q.list().size()==10 );
 		q.setMaxResults(3);
 		q.setFirstResult(3);
 		assertTrue( q.list().size()==3 );
 		q = s.createQuery("from Foo");
 		assertTrue( q.list().size()==10 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		q = s.createQuery("from Foo");
 		assertTrue( q.list().size()==10 );
 		q.setMaxResults(5);
 		assertTrue( q.list().size()==5 );
 		doDelete( s, "from Foo" );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testForCertain() throws Exception {
 		Glarch g = new Glarch();
 		Glarch g2 = new Glarch();
 		List set = new ArrayList();
 		set.add("foo");
 		g2.setStrings(set);
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Serializable gid = (Serializable) s.save(g);
 		Serializable g2id = (Serializable) s.save(g2);
 		t.commit();
 		assertTrue( g.getVersion()==0 );
 		assertTrue( g2.getVersion()==0 );
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Glarch) s.get(Glarch.class, gid);
 		g2 = (Glarch) s.get(Glarch.class, g2id);
 		assertTrue( g2.getStrings().size()==1 );
 		s.delete(g);
 		s.delete(g2);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testBagMultipleElements() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setBag( new ArrayList() );
 		baz.setByteBag( new ArrayList() );
 		s.save(baz);
 		baz.getBag().add("foo");
 		baz.getBag().add("bar");
 		baz.getByteBag().add( "foo".getBytes() );
 		baz.getByteBag().add( "bar".getBytes() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		//put in cache
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getBag().size()==2 );
 		assertTrue( baz.getByteBag().size()==2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getBag().size()==2 );
 		assertTrue( baz.getByteBag().size()==2 );
 		baz.getBag().remove("bar");
  		baz.getBag().add("foo");
  		baz.getByteBag().add( "bar".getBytes() );
 		t.commit();
 		s.close();
 
  		s = openSession();
  		t = s.beginTransaction();
  		baz = (Baz) s.get( Baz.class, baz.getCode() );
  		assertTrue( baz.getBag().size()==2 );
  		assertTrue( baz.getByteBag().size()==3 );
  		s.delete(baz);
  		t.commit();
  		s.close();
  	}
 
 	@Test
 	public void testWierdSession() throws Exception {
  		Session s = openSession();
  		Transaction t = s.beginTransaction();
  		Serializable id =  s.save( new Foo() );
  		t.commit();
  		s.close();
 
  		s = openSession();
  		s.setFlushMode(FlushMode.MANUAL);
 		t = s.beginTransaction();
 		Foo foo = (Foo) s.get(Foo.class, id);
 		t.commit();
 
 		t = s.beginTransaction();
 		s.flush();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (Foo) s.get(Foo.class, id);
 		s.delete(foo);
 		t.commit();
 		s.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/FumTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/FumTest.java
index 41ac61a693..a9afbcf8cd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FumTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FumTest.java
@@ -1,902 +1,903 @@
 //$Id: FumTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 package org.hibernate.test.legacy;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.MatchMode;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.PointbaseDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.transform.Transformers;
 import org.hibernate.type.DateType;
 import org.hibernate.type.EntityType;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.StringType;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 public class FumTest extends LegacyTestCase {
 	private static short fumKeyShort = 1;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
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
 			"legacy/Location.hbm.xml",
 			"legacy/Stuff.hbm.xml",
 			"legacy/Container.hbm.xml",
 			"legacy/Simple.hbm.xml",
 			"legacy/Middle.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testQuery() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery("from Fum fum where fum.fo.id.string = 'x'").list();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCriteriaCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fum fum = new Fum( fumKey("fum") );
 		fum.setFum("a value");
 		fum.getMapComponent().getFummap().put("self", fum);
 		fum.getMapComponent().getStringmap().put("string", "a staring");
 		fum.getMapComponent().getStringmap().put("string2", "a notha staring");
 		fum.getMapComponent().setCount(1);
 		s.save(fum);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Fum b = (Fum) s.createCriteria(Fum.class).add(
 			Restrictions.in("fum", new String[] { "a value", "no value" } )
 		)
 		.uniqueResult();
 		assertTrue( Hibernate.isInitialized( b.getMapComponent().getStringmap() ) );
 		assertTrue( b.getMapComponent().getFummap().size()==1 );
 		assertTrue( b.getMapComponent().getStringmap().size()==2 );
 		s.delete(b);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCriteria() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Fum fum = new Fum( fumKey("fum") );
 		fum.setFo( new Fum( fumKey("fo") ) );
 		fum.setFum("fo fee fi");
 		fum.getFo().setFum("stuff");
 		Fum fr = new Fum( fumKey("fr") );
 		fr.setFum("goo");
 		Fum fr2 = new Fum( fumKey("fr2") );
 		fr2.setFum("soo");
 		fum.setFriends( new HashSet() );
 		fum.getFriends().add(fr);
 		fum.getFriends().add(fr2);
 		s.save(fr);
 		s.save(fr2);
 		s.save( fum.getFo() );
 		s.save(fum);
 
 		Criteria base = s.createCriteria(Fum.class)
 			.add( Restrictions.like("fum", "f", MatchMode.START) );
 		base.createCriteria("fo")
 			.add( Restrictions.isNotNull("fum") );
 		base.createCriteria("friends")
 			.add( Restrictions.like("fum", "g%") );
 		List list = base.list();
 		assertTrue( list.size()==1 && list.get(0)==fum );
 
 		base = s.createCriteria(Fum.class)
 			.add( Restrictions.like("fum", "f%") )
 			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
 		base.createCriteria("fo", "fo")
 			.add( Restrictions.isNotNull("fum") );
 		base.createCriteria("friends", "fum")
 			.add( Restrictions.like("fum", "g", MatchMode.START) );
 		Map map = (Map) base.uniqueResult();
 
 		assertTrue(
 			map.get("this")==fum &&
 			map.get("fo")==fum.getFo() &&
 			fum.getFriends().contains( map.get("fum") ) &&
 			map.size()==3
 		);
 
 		base = s.createCriteria(Fum.class)
 			.add( Restrictions.like("fum", "f%") )
 			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
 			.setFetchMode( "friends", FetchMode.JOIN );
 		base.createCriteria("fo", "fo")
 			.add( Restrictions.eq( "fum", fum.getFo().getFum() ) );
 		map = (Map) base.list().get(0);
 
 		assertTrue(
 			map.get("this")==fum &&
 			map.get("fo")==fum.getFo() &&
 			map.size()==2
 		);
 
 		list = s.createCriteria(Fum.class)
 			.createAlias("friends", "fr")
 			.createAlias("fo", "fo")
 			.add( Restrictions.like("fum", "f%") )
 			.add( Restrictions.isNotNull("fo") )
 			.add( Restrictions.isNotNull("fo.fum") )
 			.add( Restrictions.like("fr.fum", "g%") )
 			.add( Restrictions.eqProperty("fr.id.short", "id.short") )
 			.list();
 		assertTrue( list.size()==1 && list.get(0)==fum );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		base = s.createCriteria(Fum.class)
 			.add( Restrictions.like("fum", "f%") );
 		base.createCriteria("fo")
 			.add( Restrictions.isNotNull("fum") );
 		base.createCriteria("friends")
 			.add( Restrictions.like("fum", "g%") );
 		fum = (Fum) base.list().get(0);
 		assertTrue(  fum.getFriends().size()==2 );
 		s.delete(fum);
 		s.delete( fum.getFo() );
 		Iterator iter = fum.getFriends().iterator();
 		while ( iter.hasNext() ) {
 			s.delete( iter.next() );
 		}
 		txn.commit();
 		s.close();
 	}
 
 	static public class ABean {
 		public Fum fum;
 		public Fum fo;
 		public Fum getFo() {
 			return fo;
 		}
 		public void setFo(Fum fo) {
 			this.fo = fo;
 		}
 		public Fum getFum() {
 			return fum;
 		}
 		public void setFum(Fum fum) {
 			this.fum = fum;
 		}
 	}
 
 	@Test
 	public void testBeanResultTransformer() throws HibernateException, SQLException {
 		Session s = openSession();
 		Transaction transaction = s.beginTransaction();
 		Fum fum = new Fum( fumKey("fum") );
 		fum.setFo( new Fum( fumKey("fo") ) );
 		fum.setFum("fo fee fi");
 		fum.getFo().setFum("stuff");
 		Fum fr = new Fum( fumKey("fr") );
 		fr.setFum("goo");
 		Fum fr2 = new Fum( fumKey("fr2") );
 		fr2.setFum("soo");
 		fum.setFriends( new HashSet() );
 		fum.getFriends().add(fr);
 		fum.getFriends().add(fr2);
 		s.save(fr);
 		s.save(fr2);
 		s.save( fum.getFo() );
 		s.save(fum);
 		
 		Criteria test = s.createCriteria(Fum.class, "xam")
 			.createCriteria("fo", "fo")
 			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
 		
 		Map fc = (Map) test.list().get(0);
 		assertNotNull(fc.get("xam"));
 		
 		Criteria base = s.createCriteria(Fum.class, "fum")
 		.add( Restrictions.like("fum", "f%") )
 		.setResultTransformer(Transformers.aliasToBean(ABean.class))
 		.setFetchMode("friends", FetchMode.JOIN);
 		base.createCriteria("fo", "fo")
 		.add( Restrictions.eq( "fum", fum.getFo().getFum() ) );
 		ABean map = (ABean) base.list().get(0);
 
 		assertTrue(
 				map.getFum()==fum &&
 				map.getFo()==fum.getFo() );
 		
 		s.delete(fr);
 		s.delete(fr2);
 		s.delete(fum);
 		s.delete(fum.getFo());
 		s.flush();
 		transaction.commit();
 		s.close();
 	}
 
 	@Test
 	public void testListIdentifiers() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Fum fum = new Fum( fumKey("fum") );
 		fum.setFum("fo fee fi");
 		s.save(fum);
 		fum = new Fum( fumKey("fi") );
 		fum.setFum("fee fi fo");
 		s.save(fum);
 		List list = s.createQuery( "select fum.id from Fum as fum where not fum.fum='FRIEND'" ).list();
 		assertTrue( "list identifiers", list.size()==2);
 		Iterator iter = s.createQuery( "select fum.id from Fum fum where not fum.fum='FRIEND'" ).iterate();
 		int i=0;
 		while ( iter.hasNext() ) {
 			assertTrue( "iterate identifiers",  iter.next() instanceof FumCompositeID);
 			i++;
 		}
 		assertTrue(i==2);
 
 		s.delete( s.load(Fum.class, (Serializable) list.get(0) ) );
 		s.delete( s.load(Fum.class, (Serializable) list.get(1) ) );
 		txn.commit();
 		s.close();
 	}
 
 
 	public static FumCompositeID fumKey(String str) {
 		return fumKey(str,false);
 	}
 
 	private static FumCompositeID fumKey(String str, boolean aCompositeQueryTest) {
 		FumCompositeID id = new FumCompositeID();
 		if ( getDialect() instanceof MckoiDialect ) {
 			GregorianCalendar now = new GregorianCalendar();
 			GregorianCalendar cal = new GregorianCalendar(
 				now.get(java.util.Calendar.YEAR),
 				now.get(java.util.Calendar.MONTH),
 				now.get(java.util.Calendar.DATE)
 			);
 			id.setDate( cal.getTime() );
 		}
 		else {
 			id.setDate( new Date() );
 		}
 		id.setString( str );
 
 		if (aCompositeQueryTest) {
 			id.setShort( fumKeyShort++ );
 		}
 		else {
 			id.setShort( (short) 12 );
 		}
 
 		return id;
 	}
 
 	@Test
 	public void testCompositeID() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Fum fum = new Fum( fumKey("fum") );
 		fum.setFum("fee fi fo");
 		s.save(fum);
 		assertTrue( "load by composite key", fum==s.load( Fum.class, fumKey("fum") ) );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		fum = (Fum) s.load( Fum.class, fumKey("fum"), LockMode.UPGRADE );
 		assertTrue( "load by composite key", fum!=null );
 
 		Fum fum2 = new Fum( fumKey("fi") );
 		fum2.setFum("fee fo fi");
 		fum.setFo(fum2);
 		s.save(fum2);
 		assertTrue(
 			"find composite keyed objects",
 				s.createQuery( "from Fum fum where not fum.fum='FRIEND'" ).list().size()==2
 		);
 		assertTrue(
 			"find composite keyed object",
 				s.createQuery( "select fum from Fum fum where fum.fum='fee fi fo'" ).list().get(0)==fum
 		);
 		fum.setFo(null);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		Iterator iter = s.createQuery( "from Fum fum where not fum.fum='FRIEND'" ).iterate();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			fum = (Fum) iter.next();
 			//iter.remove();
 			s.delete(fum);
 			i++;
 		}
 		assertTrue( "iterate on composite key", i==2 );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeIDOneToOne() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Fum fum = new Fum( fumKey("fum") );
 		fum.setFum("fee fi fo");
 		//s.save(fum);
 		Fumm fumm = new Fumm();
 		fumm.setFum(fum);
 		s.save(fumm);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		fumm = (Fumm) s.load( Fumm.class, fumKey("fum") );
 		//s.delete( fumm.getFum() );
 		s.delete(fumm);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeIDQuery() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fum fee = new Fum( fumKey("fee",true) );
 		fee.setFum("fee");
 		s.save(fee);
 		Fum fi = new Fum( fumKey("fi",true) );
 		fi.setFum("fi");
 		short fiShort = fi.getId().getShort();
 		s.save(fi);
 		Fum fo = new Fum( fumKey("fo",true) );
 		fo.setFum("fo");
 		s.save(fo);
 		Fum fum = new Fum( fumKey("fum",true) );
 		fum.setFum("fum");
 		s.save(fum);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		// Try to find the Fum object "fo" that we inserted searching by the string in the id
 		List vList = s.createQuery( "from Fum fum where fum.id.string='fo'" ).list();
 		assertTrue( "find by composite key query (find fo object)", vList.size() == 1 );
 		fum = (Fum)vList.get(0);
 		assertTrue( "find by composite key query (check fo object)", fum.getId().getString().equals("fo") );
 
 		// Try to find the Fum object "fi" that we inserted searching by the date in the id
 		vList = s.createQuery( "from Fum fum where fum.id.short = ?" )
-				.setParameter( 0, new Short(fiShort), Hibernate.SHORT )
+				.setParameter( 0, new Short(fiShort), StandardBasicTypes.SHORT )
 				.list();
 		assertEquals( "find by composite key query (find fi object)", 1, vList.size() );
 		fi = (Fum)vList.get(0);
 		assertEquals( "find by composite key query (check fi object)", "fi", fi.getId().getString() );
 
 		// Make sure we can return all of the objects by searching by the date id
 		vList = s.createQuery( "from Fum fum where fum.id.date <= ? and not fum.fum='FRIEND'" )
-				.setParameter( 0, new Date(), Hibernate.DATE )
+				.setParameter( 0, new Date(), StandardBasicTypes.DATE )
 				.list();
 		assertEquals( "find by composite key query with arguments", 4, vList.size() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		assertTrue(
 				s.createQuery( "select fum.id.short, fum.id.date, fum.id.string from Fum fum" ).iterate().hasNext()
 		);
 		assertTrue(
 				s.createQuery( "select fum.id from Fum fum" ).iterate().hasNext()
 		);
 		Query qu = s.createQuery("select fum.fum, fum , fum.fum, fum.id.date from Fum fum");
 		Type[] types = qu.getReturnTypes();
 		assertTrue(types.length==4);
 		for ( int k=0; k<types.length; k++) {
 			assertTrue( types[k]!=null );
 		}
 		assertTrue(types[0] instanceof StringType);
 		assertTrue(types[1] instanceof EntityType);
 		assertTrue(types[2] instanceof StringType);
 		assertTrue(types[3] instanceof DateType);
 		Iterator iter = qu.iterate();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			j++;
 			assertTrue( ( (Object[]) iter.next() )[1] instanceof Fum );
 		}
 		assertTrue( "iterate on composite key", j==8 );
 
 		fum = (Fum) s.load( Fum.class, fum.getId() );
 		s.createFilter( fum.getQuxArray(), "where this.foo is null" ).list();
 		s.createFilter( fum.getQuxArray(), "where this.foo.id = ?" )
-				.setParameter( 0, "fooid", Hibernate.STRING )
+				.setParameter( 0, "fooid", StandardBasicTypes.STRING )
 				.list();
 		Query f = s.createFilter( fum.getQuxArray(), "where this.foo.id = :fooId" );
 		f.setString("fooId", "abc");
 		assertFalse( f.iterate().hasNext() );
 
 		iter = s.createQuery( "from Fum fum where not fum.fum='FRIEND'" ).iterate();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			fum = (Fum) iter.next();
 			s.delete(fum);
 			i++;
 		}
 		assertTrue( "iterate on composite key", i==4 );
 		s.flush();
 
 		s.createQuery( "from Fum fu, Fum fo where fu.fo.id.string = fo.id.string and fo.fum is not null" ).iterate();
 
 		s.createQuery( "from Fumm f1 inner join f1.fum f2" ).list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeIDCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fum fum1 = new Fum( fumKey("fum1") );
 		Fum fum2 = new Fum( fumKey("fum2") );
 		fum1.setFum("fee fo fi");
 		fum2.setFum("fee fo fi");
 		s.save(fum1);
 		s.save(fum2);
 		Qux q = new Qux();
 		s.save(q);
 		Set set = new HashSet();
 		List list = new ArrayList();
 		set.add(fum1); set.add(fum2);
 		list.add(fum1);
 		q.setFums(set);
 		q.setMoreFums(list);
 		fum1.setQuxArray( new Qux[] {q} );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load( Qux.class, q.getKey() );
 		assertTrue( "collection of fums", q.getFums().size()==2 );
 		assertTrue( "collection of fums", q.getMoreFums().size()==1 );
 		assertTrue( "unkeyed composite id collection", ( (Fum) q.getMoreFums().get(0) ).getQuxArray()[0]==q );
 		Iterator iter = q.getFums().iterator();
 		iter.hasNext();
 		Fum f = (Fum) iter.next();
 		s.delete(f);
 		iter.hasNext();
 		f = (Fum) iter.next();
 		s.delete(f);
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteOwner() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save(q);
 		Fum f1 = new Fum( fumKey("f1") );
 		Fum f2 = new Fum( fumKey("f2") );
 		Set set = new HashSet();
 		set.add(f1);
 		set.add(f2);
 		List list = new LinkedList();
 		list.add(f1);
 		list.add(f2);
 		f1.setFum("f1");
 		f2.setFum("f2");
 		q.setFums(set);
 		q.setMoreFums(list);
 		s.save(f1);
 		s.save(f2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load( Qux.class, q.getKey(), LockMode.UPGRADE );
 		s.lock( q, LockMode.UPGRADE );
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		list = s.createQuery( "from Fum fum where not fum.fum='FRIEND'" ).list();
 		assertTrue( "deleted owner", list.size()==2 );
 		s.lock( list.get(0), LockMode.UPGRADE );
 		s.lock( list.get(1), LockMode.UPGRADE );
 		Iterator iter = list.iterator();
 		while ( iter.hasNext() ) {
 			s.delete( iter.next() );
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeIDs() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fo fo = Fo.newFo( fumKey("an instance of fo") );
 		Properties props = new Properties();
 		props.setProperty("foo", "bar");
 		props.setProperty("bar", "foo");
 		fo.setSerial(props);
 		fo.setBuf( "abcdefghij1`23%$*^*$*\n\t".getBytes() );
 		s.save( fo );
 		s.flush();
 		props.setProperty("x", "y");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		fo = (Fo) s.load( Fo.class, fumKey("an instance of fo") );
 		props = (Properties) fo.getSerial();
 		assertTrue( props.getProperty("foo").equals("bar") );
 		//assertTrue( props.contains("x") );
 		assertTrue( props.getProperty("x").equals("y") );
 		assertTrue( fo.getBuf()[0]=='a' );
 		fo.getBuf()[1]=(byte)126;
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		fo = (Fo) s.load( Fo.class, fumKey("an instance of fo") );
 		assertTrue( fo.getBuf()[1]==126 );
 		assertTrue(
 				s.createQuery( "from Fo fo where fo.id.string like 'an instance of fo'" ).iterate().next()==fo
 		);
 		s.delete(fo);
 		s.flush();
 		try {
 			s.save( Fo.newFo() );
 			assertTrue(false);
 		}
 		catch (Exception e) {
 			//System.out.println( e.getMessage() );
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testKeyManyToOne() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Inner sup = new Inner();
 		InnerKey sid = new InnerKey();
 		sup.setDudu("dudu");
 		sid.setAkey("a");
 		sid.setBkey("b");
 		sup.setId(sid);
 		Middle m = new Middle();
 		MiddleKey mid = new MiddleKey();
 		mid.setOne("one");
 		mid.setTwo("two");
 		mid.setSup(sup);
 		m.setId(mid);
 		m.setBla("bla");
 		Outer d = new Outer();
 		OuterKey did = new OuterKey();
 		did.setMaster(m);
 		did.setDetailId("detail");
 		d.setId(did);
 		d.setBubu("bubu");
 		s.save(sup);
 		s.save(m);
 		s.save(d);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Inner in = (Inner) s.createQuery( "from Inner" ).list().get(0);
 		assertTrue( in.getMiddles().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		assertTrue( s.createQuery( "from Inner _inner join _inner.middles middle" ).list().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		d = (Outer) s.load(Outer.class, did);
 		assertTrue( d.getId().getMaster().getId().getSup().getDudu().equals("dudu") );
 		s.delete(d);
 		s.delete( d.getId().getMaster() );
 		s.save( d.getId().getMaster() );
 		s.save(d);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		d = (Outer) s.createQuery( "from Outer o where o.id.detailId = ?" )
-				.setParameter( 0, d.getId().getDetailId(), Hibernate.STRING )
+				.setParameter( 0, d.getId().getDetailId(), StandardBasicTypes.STRING )
 				.list()
 				.get(0);
 		s.createQuery( "from Outer o where o.id.master.id.sup.dudu is not null" ).list();
 		s.createQuery( "from Outer o where o.id.master.id.sup.id.akey is not null" ).list();
 		s.createQuery( "from Inner i where i.backOut.id.master.id.sup.id.akey = i.id.bkey" ).list();
 		List l = s.createQuery( "select o.id.master.id.sup.dudu from Outer o where o.id.master.id.sup.dudu is not null" )
 				.list();
 		assertTrue(l.size()==1);
 		l = s.createQuery( "select o.id.master.id.sup.id.akey from Outer o where o.id.master.id.sup.id.akey is not null" )
 				.list();
 		assertTrue(l.size()==1);
 		s.createQuery(
 				"select i.backOut.id.master.id.sup.id.akey from Inner i where i.backOut.id.master.id.sup.id.akey = i.id.bkey"
 		).list();
 		s.createQuery( "from Outer o where o.id.master.bla = ''" ).list();
 		s.createQuery( "from Outer o where o.id.master.id.one = ''" ).list();
 		s.createQuery( "from Inner inn where inn.id.bkey is not null and inn.backOut.id.master.id.sup.id.akey > 'a'" )
 				.list();
 		s.createQuery( "from Outer as o left join o.id.master m left join m.id.sup where o.bubu is not null" ).list();
 		s.createQuery( "from Outer as o left join o.id.master.id.sup s where o.bubu is not null" ).list();
 		s.createQuery( "from Outer as o left join o.id.master m left join o.id.master.id.sup s where o.bubu is not null" )
 				.list();
 		s.delete(d);
 		s.delete( d.getId().getMaster() );
 		s.delete( d.getId().getMaster().getId().getSup() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeKeyPathExpressions() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "select fum1.fo from Fum fum1 where fum1.fo.fum is not null" ).list();
 		s.createQuery( "from Fum fum1 where fum1.fo.fum is not null order by fum1.fo.fum" ).list();
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) && !(getDialect() instanceof MckoiDialect) && !(getDialect() instanceof PointbaseDialect) ) {
 			s.createQuery( "from Fum fum1 where exists elements(fum1.friends)" ).list();
 			if(!(getDialect() instanceof TimesTenDialect)) { // can't execute because TimesTen can't do subqueries combined with aggreations
 				s.createQuery( "from Fum fum1 where size(fum1.friends) = 0" ).list();
 			}
 		}
 		s.createQuery( "select elements(fum1.friends) from Fum fum1" ).list();
 		s.createQuery( "from Fum fum1, fr in elements( fum1.friends )" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testUnflushedSessionSerialization() throws Exception {
 		///////////////////////////////////////////////////////////////////////////
 		// Test insertions across serializations
 		Session s = sessionFactory().openSession();
 		s.setFlushMode(FlushMode.MANUAL);
 		s.beginTransaction();
 
 		Simple simple = new Simple( Long.valueOf(10) );
 		simple.setAddress("123 Main St. Anytown USA");
 		simple.setCount(1);
 		simple.setDate( new Date() );
 		simple.setName("My UnflushedSessionSerialization Simple");
 		simple.setPay( Float.valueOf(5000) );
 		s.save( simple );
 
 		// Now, try to serialize session without flushing...
 		s.getTransaction().commit();
 		Session s2 = spoofSerialization(s);
 		s.close();
 		s = s2;
 		s.beginTransaction();
 
 		simple = (Simple) s.load( Simple.class, new Long(10) );
 		Simple other = new Simple( Long.valueOf(11) );
 		other.init();
 		s.save( other );
 
 		simple.setOther(other);
 		s.flush();
 
 		s.getTransaction().commit();
 		s.close();
 		Simple check = simple;
 
 		///////////////////////////////////////////////////////////////////////////
 		// Test updates across serializations
 		s = sessionFactory().openSession();
 		s.setFlushMode(FlushMode.MANUAL);
 		s.beginTransaction();
 
 		simple = (Simple) s.get( Simple.class, Long.valueOf(10) );
 		assertTrue("Not same parent instances", check.getName().equals( simple.getName() ) );
 		assertTrue("Not same child instances", check.getOther().getName().equals( other.getName() ) );
 
 		simple.setName("My updated name");
 
 		s.getTransaction().commit();
 		s2 = spoofSerialization(s);
 		s.close();
 		s = s2;
 		s.beginTransaction();
 		s.flush();
 
 		s.getTransaction().commit();
 		s.close();
 		check = simple;
 
 		///////////////////////////////////////////////////////////////////////////
 		// Test deletions across serializations
 		s = sessionFactory().openSession();
 		s.setFlushMode(FlushMode.MANUAL);
 		s.beginTransaction();
 
 		simple = (Simple) s.get( Simple.class, Long.valueOf( 10 ) );
 		assertTrue("Not same parent instances", check.getName().equals( simple.getName() ) );
 		assertTrue("Not same child instances", check.getOther().getName().equals( other.getName() ) );
 
 		// Now, lets delete across serialization...
 		s.delete(simple);
 
 		s.getTransaction().commit();
 		s2 = spoofSerialization(s);
 		s.close();
 		s = s2;
 		s.beginTransaction();
 		s.flush();
 
 		s.getTransaction().commit();
 		s.close();
 
 		///////////////////////////////////////////////////////////////////////////
 		// Test collection actions across serializations
 		s = sessionFactory().openSession();
 		s.setFlushMode(FlushMode.MANUAL);
 		s.beginTransaction();
 
 		Fum fum = new Fum( fumKey("uss-fum") );
 		fum.setFo( new Fum( fumKey("uss-fo") ) );
 		fum.setFum("fo fee fi");
 		fum.getFo().setFum("stuff");
 		Fum fr = new Fum( fumKey("uss-fr") );
 		fr.setFum("goo");
 		Fum fr2 = new Fum( fumKey("uss-fr2") );
 		fr2.setFum("soo");
 		fum.setFriends( new HashSet() );
 		fum.getFriends().add(fr);
 		fum.getFriends().add(fr2);
 		s.save(fr);
 		s.save(fr2);
 		s.save( fum.getFo() );
 		s.save(fum);
 
 		s.getTransaction().commit();
 		s2 = spoofSerialization(s);
 		s.close();
 		s = s2;
 		s.beginTransaction();
 		s.flush();
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		s.setFlushMode(FlushMode.MANUAL);
 		s.beginTransaction();
 		fum = (Fum) s.load( Fum.class, fum.getId() );
 
 		assertTrue("the Fum.friends did not get saved", fum.getFriends().size() == 2);
 
 		fum.setFriends(null);
 		s.getTransaction().commit();
 		s2 = spoofSerialization(s);
 		s.close();
 		
 		s = s2;
 		s.beginTransaction();
 		s.flush();
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		s.beginTransaction();
 		s.setFlushMode(FlushMode.MANUAL);
 		fum = (Fum) s.load( Fum.class, fum.getId() );
 		assertTrue("the Fum.friends is not empty", fum.getFriends() == null || fum.getFriends().size() == 0);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private Session spoofSerialization(Session session) throws IOException {
 		try {
 			// Serialize the incoming out to memory
 			ByteArrayOutputStream serBaOut = new ByteArrayOutputStream();
 			ObjectOutputStream serOut = new ObjectOutputStream(serBaOut);
 
 			serOut.writeObject(session);
 
 			// Now, re-constitute the model from memory
 			ByteArrayInputStream serBaIn =
 			        new ByteArrayInputStream(serBaOut.toByteArray());
 			ObjectInputStream serIn = new ObjectInputStream(serBaIn);
 
 			Session outgoing = (Session) serIn.readObject();
 
 			return outgoing;
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new IOException("Unable to locate class on reconstruction");
 		}
 	}
 
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java
index a4209f4229..0a893fe470 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/MultiplicityType.java
@@ -1,150 +1,166 @@
 //$Id: MultiplicityType.java 6592 2005-04-28 15:44:16Z oneovthafew $
 package org.hibernate.test.legacy;
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
+import org.hibernate.Session;
 import org.hibernate.engine.ForeignKeys;
+import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.type.IntegerType;
+import org.hibernate.type.ManyToOneType;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.StringType;
 import org.hibernate.type.Type;
+import org.hibernate.type.TypeFactory;
 import org.hibernate.usertype.CompositeUserType;
 
 public class MultiplicityType implements CompositeUserType {
 
 	private static final String[] PROP_NAMES = new String[] {
 		"count", "glarch"
 	};
 	private static final int[] SQL_TYPES = new int[] {
-		IntegerType.INSTANCE.getSqlTypeDescriptor().getSqlType(), StringType.INSTANCE.getSqlTypeDescriptor().getSqlType()
+			IntegerType.INSTANCE.getSqlTypeDescriptor().getSqlType(),
+			StringType.INSTANCE.getSqlTypeDescriptor().getSqlType()
 	};
 	private static final Type[] TYPES = new Type[] {
-		IntegerType.INSTANCE, Hibernate.entity(Glarch.class)
+			IntegerType.INSTANCE,
+			new ManyToOneType(
+					new TypeFactory.TypeScope() {
+						@Override
+						public SessionFactoryImplementor resolveFactory() {
+							// todo : can we tie this into org.hibernate.type.TypeFactory.TypeScopeImpl() somehow?
+							throw new HibernateException( "Cannot access SessionFactory from here" );
+						}
+					},
+					Glarch.class.getName()
+			)
 	};
 
 	public String[] getPropertyNames() {
 		return PROP_NAMES;
 	}
 
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	public int hashCode(Object x) throws HibernateException {
 		Multiplicity o = (Multiplicity) x;
 		return o.count + o.glarch.hashCode();
 	}
 
 	public Object getPropertyValue(Object component, int property) {
 		Multiplicity o = (Multiplicity) component;
 		return property==0 ?
 			(Object) new Integer(o.count) :
 			(Object) o.glarch;
 	}
 
 	public void setPropertyValue(
 		Object component,
 		int property,
 		Object value) {
 
 		Multiplicity o = (Multiplicity) component;
 		if (property==0) {
 			o.count = ( (Integer) value ).intValue();
 		}
 		else {
 			o.glarch = (Glarch) value;
 		}
 	}
 
 	public int[] sqlTypes() {
 		return SQL_TYPES;
 	}
 
 	public Class returnedClass() {
 		return Multiplicity.class;
 	}
 
 	public boolean equals(Object x, Object y) {
 		Multiplicity mx = (Multiplicity) x;
 		Multiplicity my = (Multiplicity) y;
 		if (mx==my) return true;
 		if (mx==null || my==null) return false;
 		return mx.count==my.count && mx.glarch==my.glarch;
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 		throws HibernateException, SQLException {
 
 		Integer c = (Integer) IntegerType.INSTANCE.nullSafeGet( rs, names[0], session );
-		GlarchProxy g = (GlarchProxy) Hibernate.entity(Glarch.class).nullSafeGet(rs, names[1], session, owner);
+		GlarchProxy g = (GlarchProxy) ( (Session) session ).getTypeHelper().entity( Glarch.class ).nullSafeGet(rs, names[1], session, owner);
 		Multiplicity m = new Multiplicity();
 		m.count = c==null ? 0 : c.intValue();
 		m.glarch = g;
 		return m;
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 		throws HibernateException, SQLException {
 
 		Multiplicity o = (Multiplicity) value;
 		GlarchProxy g;
 		Integer c;
 		if (o==null) {
 			g=null;
 			c=new Integer(0);
 		}
 		else {
 			g = o.glarch;
 			c = new Integer(o.count);
 		}
-		Hibernate.INTEGER.nullSafeSet(st, c, index, session);
-		Hibernate.entity(Glarch.class).nullSafeSet(st, g, index+1, session);
+		StandardBasicTypes.INTEGER.nullSafeSet(st, c, index, session);
+		( (Session) session ).getTypeHelper().entity( Glarch.class ).nullSafeSet(st, g, index+1, session);
 
 	}
 
 	public Object deepCopy(Object value) {
 		if (value==null) return null;
 		Multiplicity v = (Multiplicity) value;
 		Multiplicity m = new Multiplicity();
 		m.count = v.count;
 		m.glarch = v.glarch;
 		return m;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public Object assemble(
 		Serializable cached,
 		SessionImplementor session,
 		Object owner) throws HibernateException {
 		if (cached==null) return null;
 		Serializable[] o = (Serializable[]) cached;
 		Multiplicity m = new Multiplicity();
 		m.count = ( (Integer) o[0] ).intValue();
 		m.glarch = o[1]==null ? 
 			null : 
 			(GlarchProxy) session.internalLoad( Glarch.class.getName(), o[1], false, false );
 		return m;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session)
 	throws HibernateException {
 		if (value==null) return null;
 		Multiplicity m = (Multiplicity) value;
 		return new Serializable[] { 
 				new Integer(m.count), 
 				ForeignKeys.getEntityIdentifierIfNotUnsaved( Glarch.class.getName(), m.glarch, session ) 
 		};
 	}
 
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner) 
 	throws HibernateException {
 		return assemble( disassemble(original, session), session, owner);
 	}
 	
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
index f625020ec8..78108fff61 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
@@ -1,1245 +1,1246 @@
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
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.impl.SessionImpl;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 
 @SuppressWarnings( {"UnnecessaryBoxing"})
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
 		foo2.setInt(1234567);
 		foo.setInt(1234);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		foo2 = (FooProxy) s.load(Foo.class, id2);
 		assertFalse( Hibernate.isInitialized(foo) );
 		Hibernate.initialize(foo2);
 		Hibernate.initialize(foo);
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		assertTrue( foo2.getComponent().getImportantDates().length==4 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo.setKey( "xyzid" );
 		foo.setFloat( new Float( 1.2f ) );
 		foo2.setKey( (String) id ); //intentionally id, not id2!
 		foo2.setFloat( new Float(1.3f) );
 		foo2.getDependent().setKey( null );
 		foo2.getComponent().getSubcomponent().getFee().setKey(null);
 		assertFalse( foo2.getKey().equals( id ) );
 		s.save( foo );
 		s.update( foo2 );
 		assertEquals( foo2.getKey(), id );
 		assertTrue( foo2.getInt()==1234567 );
 		assertEquals( foo.getKey(), "xyzid" );
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		assertTrue( foo.getInt()==1234567 );
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		String feekey = foo.getDependent().getKey();
 		String fookey = foo.getKey();
 		s.delete(foo);
 		s.delete( s.get(Foo.class, id2) );
 		s.delete( s.get(Foo.class, "xyzid") );
 // here is the issue (HHH-4092).  After the deletes above there are 2 Fees and a Glarch unexpectedly hanging around
 		assertEquals( 2, doDelete( s, "from java.lang.Object" ) );
 		t.commit();
 		s.close();
 		
 		//to account for new id rollback shit
 		foo.setKey(fookey);
 		foo.getDependent().setKey(feekey);
 		foo.getComponent().setGlarch(null);
 		foo.getComponent().setSubcomponent(null);
 		
 		s = openSession();
 		t = s.beginTransaction();
 		//foo.getComponent().setGlarch(null); //no id property!
 		s.replicate(foo, ReplicationMode.OVERWRITE);
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
 
 		LockMode lockMode = (getDialect() instanceof DB2Dialect) ? LockMode.READ : LockMode.UPGRADE;
 
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
-				.setParameter( 0, new Integer(66), Hibernate.INTEGER )
+				.setParameter( 0, new Integer(66), StandardBasicTypes.INTEGER )
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
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		s.save( s4 );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.WRITE );
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
 		s4 = (Simple) s.get(Simple.class, new Long(4), LockMode.UPGRADE_NOWAIT);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 
 		s1 = (Simple) s.load(Simple.class, new Long(1), LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.UPGRADE );
 		s2 = (Simple) s.load(Simple.class, new Long(2), LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.READ );
 		s3 = (Simple) s.load(Simple.class, new Long(3), LockMode.READ);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s4 = (Simple) s.load(Simple.class, new Long(4), LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 
 		s.lock(s2, LockMode.UPGRADE); //upgrade
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.UPGRADE );
 		s.lock(s3, LockMode.UPGRADE);
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.UPGRADE );
 		s.lock(s1, LockMode.UPGRADE_NOWAIT);
 		s.lock(s4, LockMode.NONE);
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.UPGRADE_NOWAIT );
 
 		tx.commit();
 		tx = s.beginTransaction();
 
 		assertTrue( s.getCurrentLockMode(s3)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s1)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s2)==LockMode.NONE );
 		assertTrue( s.getCurrentLockMode(s4)==LockMode.NONE );
 
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
 
 		s.delete(s1); s.delete(s2); s.delete(s3); s.delete(s4);
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java b/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
index 4b23a4c411..9c792c2df3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
@@ -1,2741 +1,2742 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.querycache;
 
 import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.CriteriaSpecification;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projections;
 import org.hibernate.criterion.Property;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.impl.SessionFactoryImpl;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.transform.Transformers;
+import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractQueryCacheResultTransformerTest extends BaseCoreFunctionalTestCase {
 	private Student yogiExpected;
 	private Student shermanExpected;
 	private CourseMeeting courseMeetingExpected1;
 	private CourseMeeting courseMeetingExpected2;
 	private Course courseExpected;
 	private Enrolment yogiEnrolmentExpected;
 	private Enrolment shermanEnrolmentExpected;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] { "querycache/Enrolment.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "foo" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	protected abstract class CriteriaExecutor extends QueryExecutor {
 		protected abstract Criteria getCriteria(Session s) throws Exception;
 		@Override
         protected Object getResults(Session s, boolean isSingleResult) throws Exception {
 			Criteria criteria = getCriteria( s ).setCacheable( getQueryCacheMode() != CacheMode.IGNORE ).setCacheMode( getQueryCacheMode() );
 			return ( isSingleResult ? criteria.uniqueResult() : criteria.list() );
 		}
 	}
 
 	protected abstract class HqlExecutor extends QueryExecutor {
 		protected abstract Query getQuery(Session s);
 		@Override
         protected Object getResults(Session s, boolean isSingleResult) {
 			Query query = getQuery( s ).setCacheable( getQueryCacheMode() != CacheMode.IGNORE ).setCacheMode( getQueryCacheMode() );
 			return ( isSingleResult ? query.uniqueResult() : query.list() );
 		}
 	}
 
 	protected abstract class QueryExecutor {
 		public Object execute(boolean isSingleResult) throws Exception{
 			Session s = openSession();
 			Transaction t = s.beginTransaction();
 			Object result = null;
 			try {
 				result = getResults( s, isSingleResult );
 				t.commit();
 			}
 			catch ( Exception ex ) {
 				t.rollback();
 				throw ex;
 			}
 			finally {
 				s.close();
 			}
 			return result;
 		}
 		protected abstract Object getResults(Session s, boolean isSingleResult) throws Exception;
 	}
 
 	protected interface ResultChecker {
 		void check(Object results);
 	}
 
 	protected abstract CacheMode getQueryCacheMode();
 
 	protected boolean areDynamicNonLazyAssociationsChecked() {
 		return true;
 	}
 
 	protected void createData() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		courseExpected = new Course();
 		courseExpected.setCourseCode( "HIB" );
 		courseExpected.setDescription( "Hibernate Training" );
 		courseMeetingExpected1 = new CourseMeeting( courseExpected, "Monday", 1, "1313 Mockingbird Lane" );
 		courseMeetingExpected2 = new CourseMeeting( courseExpected, "Tuesday", 2, "1313 Mockingbird Lane" );
 		courseExpected.getCourseMeetings().add( courseMeetingExpected1 );
 		courseExpected.getCourseMeetings().add( courseMeetingExpected2 );
 		s.save( courseExpected );
 
 		yogiExpected = new Student();
 		yogiExpected.setName( new PersonName( "Yogi", "The", "Bear" ) );
 		yogiExpected.setStudentNumber( 111 );
 		yogiExpected.setPreferredCourse( courseExpected );
 		List yogiSecretCodes = new ArrayList();
 		yogiSecretCodes.add( Integer.valueOf( 0 ) );
 		yogiExpected.setSecretCodes( yogiSecretCodes );
 		s.save( yogiExpected );
 
 		Address address1 = new Address( yogiExpected, "home", "1 Main Street", "Podunk", "WA", "98000", "USA" );
 		Address address2 = new Address( yogiExpected, "work", "2 Main Street", "NotPodunk", "WA", "98001", "USA" );
 		yogiExpected.getAddresses().put( address1.getAddressType(), address1 );
 		yogiExpected.getAddresses().put( address2.getAddressType(), address2  );
 		s.save( address1 );
 		s.save( address2 );
 
 		shermanExpected = new Student();
 		shermanExpected.setName( new PersonName( "Sherman", null, "Grote" ) );
 		shermanExpected.setStudentNumber( 999 );
 		List shermanSecretCodes = new ArrayList();
 		shermanSecretCodes.add( Integer.valueOf( 1 ) );
 		shermanSecretCodes.add( Integer.valueOf( 2 ) );
 		shermanExpected.setSecretCodes( shermanSecretCodes );
 		s.save( shermanExpected );
 
 		shermanEnrolmentExpected = new Enrolment();
 		shermanEnrolmentExpected.setCourse( courseExpected );
 		shermanEnrolmentExpected.setCourseCode( courseExpected.getCourseCode() );
 		shermanEnrolmentExpected.setSemester( ( short ) 1 );
 		shermanEnrolmentExpected.setYear( ( short ) 1999 );
 		shermanEnrolmentExpected.setStudent( shermanExpected );
 		shermanEnrolmentExpected.setStudentNumber( shermanExpected.getStudentNumber() );
 		shermanExpected.getEnrolments().add( shermanEnrolmentExpected );
 		s.save( shermanEnrolmentExpected );
 
 		yogiEnrolmentExpected = new Enrolment();
 		yogiEnrolmentExpected.setCourse( courseExpected );
 		yogiEnrolmentExpected.setCourseCode( courseExpected.getCourseCode() );
 		yogiEnrolmentExpected.setSemester( ( short ) 3 );
 		yogiEnrolmentExpected.setYear( ( short ) 1998 );
 		yogiEnrolmentExpected.setStudent( yogiExpected );
 		yogiEnrolmentExpected.setStudentNumber( yogiExpected.getStudentNumber() );
 		yogiExpected.getEnrolments().add( yogiEnrolmentExpected );
 		s.save( yogiEnrolmentExpected );
 
 		t.commit();
 		s.close();
 	}
 
 	protected void deleteData() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.delete( yogiExpected );
 		s.delete( shermanExpected );
 		s.delete( yogiEnrolmentExpected );
 		s.delete( shermanEnrolmentExpected );
 		s.delete( courseMeetingExpected1 );
 		s.delete( courseMeetingExpected2 );
 		s.delete( courseExpected );
 		t.commit();
 		s.close();
 	}
 
 
 	public void testAliasToEntityMapNoProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			@Override
             protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.enrolments", "e", CriteriaSpecification.LEFT_JOIN )
 						.createAlias( "e.course", "c", CriteriaSpecification.LEFT_JOIN )
 								.setResultTransformer( CriteriaSpecification.ALIAS_TO_ENTITY_MAP )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			@Override
             public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.enrolments e left join e.course c order by s.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				assertEquals( 3, yogiMap.size() );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( 3, shermanMap.size() );
 				assertEquals( yogiExpected, yogiMap.get( "s" ) );
 				assertEquals( yogiEnrolmentExpected, yogiMap.get( "e" ) );
 				assertEquals( courseExpected, yogiMap.get( "c" ) );
 				assertEquals( shermanExpected, shermanMap.get( "s" ) );
 				assertEquals( shermanEnrolmentExpected, shermanMap.get( "e" ) );
 				assertEquals( courseExpected, shermanMap.get( "c" ) );
 				assertSame( ( ( Map ) resultList.get( 0 ) ).get( "c" ), shermanMap.get( "c" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToEntityMapNoProjectionMultiAndNullList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			@Override
             protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "p", CriteriaSpecification.LEFT_JOIN )
 						.createAlias( "s.addresses", "a", CriteriaSpecification.LEFT_JOIN )
 								.setResultTransformer( CriteriaSpecification.ALIAS_TO_ENTITY_MAP )
 						.addOrder( Order.asc( "s.studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			@Override
             public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.preferredCourse p left join s.addresses a order by s.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				Map yogiMap1 = ( Map ) resultList.get( 0 );
 				assertEquals( 3, yogiMap1.size() );
 				Map yogiMap2 = ( Map ) resultList.get( 1 );
 				assertEquals( 3, yogiMap2.size() );
 				Map shermanMap = ( Map ) resultList.get( 2 );
 				assertEquals( 3, shermanMap.size() );
 				assertEquals( yogiExpected, yogiMap1.get( "s" ) );
 				assertEquals( courseExpected, yogiMap1.get( "p" ) );
 				Address yogiAddress1 = ( Address ) yogiMap1.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress1.getAddressType() ),
 						yogiMap1.get( "a" ));
 				assertEquals( yogiExpected, yogiMap2.get( "s" ) );
 				assertEquals( courseExpected, yogiMap2.get( "p" ) );
 				Address yogiAddress2 = ( Address ) yogiMap2.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress2.getAddressType() ),
 						yogiMap2.get( "a" ));
 				assertSame( yogiMap1.get( "s" ), yogiMap2.get( "s" ) );
 				assertSame( yogiMap1.get( "p" ), yogiMap2.get( "p" ) );
 				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
 				assertEquals( shermanExpected, shermanMap.get( "s" ) );
 				assertEquals( shermanExpected.getPreferredCourse(), shermanMap.get( "p" ) );
 				assertNull( shermanMap.get( "a" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToEntityMapNoProjectionNullAndNonNullAliasList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			@Override
             protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", CriteriaSpecification.LEFT_JOIN )
 								.setResultTransformer( CriteriaSpecification.ALIAS_TO_ENTITY_MAP )
 						.createCriteria( "s.preferredCourse", CriteriaSpecification.INNER_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			@Override
             public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.addresses a left join s.preferredCourse order by s.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap1 = ( Map ) resultList.get( 0 );
 				assertEquals( 2, yogiMap1.size() );
 				Map yogiMap2 = ( Map ) resultList.get( 1 );
 				assertEquals( 2, yogiMap2.size() );
 				assertEquals( yogiExpected, yogiMap1.get( "s" ) );
 				Address yogiAddress1 = ( Address ) yogiMap1.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress1.getAddressType() ),
 						yogiMap1.get( "a" ));
 				assertEquals( yogiExpected, yogiMap2.get( "s" ) );
 				Address yogiAddress2 = ( Address ) yogiMap2.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress2.getAddressType() ),
 						yogiMap2.get( "a" ));
 				assertSame( yogiMap1.get( "s" ), yogiMap2.get( "s" ) );
 				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithNonLazyOneToManyUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Course.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Course" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Course );
 				assertEquals( courseExpected, results );
 				assertTrue( Hibernate.isInitialized( ((Course) courseExpected).getCourseMeetings() ) );
 				assertEquals( courseExpected.getCourseMeetings(), ((Course) courseExpected).getCourseMeetings() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testEntityWithNonLazyManyToOneList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( CourseMeeting.class )
 						.addOrder( Order.asc( "id.day" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			protected Query getQuery(Session s) {
 				return s.createQuery( "from CourseMeeting order by id.day" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( courseMeetingExpected1, resultList.get( 0 ) );
 				assertEquals( courseMeetingExpected2, resultList.get( 1 ) );
 				assertTrue( Hibernate.isInitialized( ((CourseMeeting) resultList.get( 0 )).getCourse() ) );
 				assertTrue( Hibernate.isInitialized( ((CourseMeeting) resultList.get( 1 )).getCourse() ) );
 				assertEquals( courseExpected, ((CourseMeeting) resultList.get( 0 )).getCourse() );
 				assertEquals( courseExpected, ((CourseMeeting) resultList.get( 1 )).getCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithLazyAssnUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.add( Restrictions.eq( "studentNumber", shermanExpected.getStudentNumber() ) );
 				}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s where s.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", shermanExpected.getStudentNumber() );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Student );
 				assertEquals( shermanExpected, results );
 				assertNotNull( ((Student) results).getEnrolments() );
 				assertFalse( Hibernate.isInitialized( ((Student) results).getEnrolments() ) );
 				assertNull( ((Student) results).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testEntityWithLazyAssnList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class )
 						.addOrder( Order.asc( "studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student order by studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ((Student) resultList.get( 0 )).getEnrolments() );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() ) );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithUnaliasedJoinFetchedLazyOneToManySingleElementList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.enrolments order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				}
 			}
 		};
 
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false);
 	}
 
 	@Test
 	public void testJoinWithFetchJoinListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pc", Criteria.LEFT_JOIN  )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				// The following fails for criteria due to HHH-3524
 				//assertEquals( yogiExpected.getPreferredCourse(), ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() );
 				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( null, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinListHql() throws Exception {
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber"
 				);
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertNull( shermanObjects[1] );
 				assertNull( ((Student) shermanObjects[0]).getPreferredCourse() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithOwnerAndPropProjectedList() throws Exception {
 		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s, s.name from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals( yogiExpected.getName(), yogiObjects[ 1 ] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[0] );
 				assertEquals( shermanExpected.getName(), shermanObjects[1] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlSelectNewMapExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithPropAndOwnerProjectedList() throws Exception {
 		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s.name, s from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected.getName(), yogiObjects[ 0 ] );
 				assertEquals( yogiExpected, yogiObjects[ 1 ] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected.getName(), shermanObjects[ 0 ] );
 				assertEquals( shermanExpected, shermanObjects[1] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlSelectNewMapExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithOwnerAndAliasedJoinedProjectedListHql() throws Exception {
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s, pc from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Course ) yogiObjects[ 1 ] ).getCourseCode()
 				);
 				Object[] shermanObjects = ( Object[]  ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertNull( shermanObjects[1] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithAliasedJoinedAndOwnerProjectedListHql() throws Exception {
 		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select pc, s from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber"
 				);
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 1 ] );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						((Course) yogiObjects[0]).getCourseCode()
 				);
 				Object[] shermanObjects = ( Object[]  ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[1] );
 				assertNull( shermanObjects[0] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 0 ] );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlSelectNewMapExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testEntityWithAliasedJoinFetchedLazyOneToManySingleElementListHql() throws Exception {
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.enrolments e order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode()
 				);
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				}
 			}
 		};
 
 		runTest( hqlExecutor, null, checker, false);
 	}
 
 	@Test
 	public void testEntityWithSelectFetchedLazyOneToManySingleElementListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "enrolments", FetchMode.SELECT )
 						.addOrder( Order.asc( "s.studentNumber" ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ((Student) resultList.get( 0 )).getEnrolments() );
 				assertFalse( Hibernate.isInitialized( ((Student) resultList.get( 0 )).getEnrolments() ) );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 			}
 		};
 
 		runTest( null, criteriaExecutorUnaliased, checker, false);
 	}
 
 	@Test
 	public void testEntityWithJoinFetchedLazyOneToManyMultiAndNullElementList() throws Exception {
 		//unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.addresses order by s.studentNumber" );
 			}
 		};
 
 		//aliased
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "a", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased3 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased4 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "a", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.addresses a order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertSame( resultList.get( 0 ), resultList.get( 1 ) );
 				assertEquals( shermanExpected, resultList.get( 2 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 2 ) ).getAddresses() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getAddresses() ) );
 					assertEquals( yogiExpected.getAddresses(), ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 					assertTrue( ( ( Student ) resultList.get( 2 ) ).getAddresses().isEmpty() );
 				}
 			}
 		};
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false );
 		runTest( hqlExecutorAliased, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 		runTest( null, criteriaExecutorAliased3, checker, false );
 		runTest( null, criteriaExecutorAliased4, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinFetchedLazyManyToOneList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.preferredCourse order by s.studentNumber" );
 			}
 		};
 
 		// aliased
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "pCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased3 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased4 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "pCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.preferredCourse pCourse order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );
 				assertNull( ((Student) resultList.get( 1 )).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false );
 		runTest( hqlExecutorAliased, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 		runTest( null, criteriaExecutorAliased3, checker, false );
 		runTest( null, criteriaExecutorAliased4, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinFetchedLazyManyToOneUsingProjectionList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.createAlias( "e.student", "s", Criteria.LEFT_JOIN )
 						.setFetchMode( "student", FetchMode.JOIN )
 						.setFetchMode( "student.preferredCourse", FetchMode.JOIN )
 						.setProjection(
 								Projections.projectionList()
 										.add( Projections.property( "s.name" ) )
 										.add( Projections.property( "e.student" ) )
 						)
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select s.name, s from Enrolment e left join e.student s left join fetch s.preferredCourse order by s.studentNumber"
 				);
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( yogiExpected.getName(), yogiObjects[ 0 ] );
 				assertEquals( shermanExpected.getName(), shermanObjects[ 0 ] );
 				// The following fails for criteria due to HHH-1425
 				// assertEquals( yogiExpected, yogiObjects[ 1 ] );
 				// assertEquals( shermanExpected, shermanObjects[ 1 ] );
 				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiObjects[ 1 ] ).getStudentNumber() );
 				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanObjects[ 1 ] ).getStudentNumber() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					// The following fails for criteria due to HHH-1425
 					//assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() ) );
 					//assertEquals( yogiExpected.getPreferredCourse(),  ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() );
 					//assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() ) );
 					//assertEquals( shermanExpected.getPreferredCourse(),  ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() );
 				}
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyOneToManySingleElementListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.enrolments", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.enrolments", "e", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.enrolments", "e", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				}
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyOneToManyMultiAndNullListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertSame( resultList.get( 0 ), resultList.get( 1 ) );
 				assertEquals( shermanExpected, resultList.get( 2 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 2 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getAddresses() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getAddresses() ) );
 					assertEquals( yogiExpected.getAddresses(), ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 					assertTrue( ( ( Student ) resultList.get( 2 ) ).getAddresses().isEmpty() );
 				}
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyManyToOneListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "p", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "p", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyOneToManySingleElementListHql() throws Exception {
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.enrolments order by s.studentNumber" );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.enrolments e order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.get( 0 ) instanceof Object[] );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals( yogiEnrolmentExpected, yogiObjects[ 1 ] );
 				assertTrue( resultList.get( 0 ) instanceof Object[] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertEquals( shermanEnrolmentExpected, shermanObjects[ 1 ] );
 			}
 		};
 		runTest( hqlExecutorUnaliased, null, checker, false );
 		runTest( hqlExecutorAliased, null, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyOneToManyMultiAndNullListHql() throws Exception {
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.addresses order by s.studentNumber" );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.addresses a order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertTrue( resultList.get( 0 ) instanceof Object[] );
 				Object[] yogiObjects1 = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects1[ 0 ] );
 				Address address1 = ( Address ) yogiObjects1[ 1 ];
 				assertEquals( yogiExpected.getAddresses().get( address1.getAddressType() ), address1 );
 				Object[] yogiObjects2 = ( Object[] ) resultList.get( 1 );
 				assertSame( yogiObjects1[ 0 ], yogiObjects2[ 0 ] );
 				Address address2 = ( Address ) yogiObjects2[ 1 ];
 				assertEquals( yogiExpected.getAddresses().get( address2.getAddressType() ), address2 );
 				assertFalse( address1.getAddressType().equals( address2.getAddressType() ) );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 2 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertNull( shermanObjects[ 1 ] );
 			}
 		};
 		runTest( hqlExecutorUnaliased, null, checker, false );
 		runTest( hqlExecutorAliased, null, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyManyToOneListHql() throws Exception {
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			protected Query getQuery(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createQuery( "from Student s left join s.preferredCourse order by s.studentNumber" );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			protected Query getQuery(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createQuery( "from Student s left join s.preferredCourse p order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertNull( shermanObjects[ 1 ] );
 			}
 		};
 		runTest( hqlExecutorUnaliased, null, checker, false );
 		runTest( hqlExecutorAliased, null, checker, false );
 	}
 
 	@Test
 	public void testAliasToEntityMapOneProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.student" ).as( "student" ) )
 						.addOrder( Order.asc( "e.studentNumber") )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student as student from Enrolment e order by e.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( 1, yogiMap.size() );
 				assertEquals( 1, shermanMap.size() );
 				// TODO: following are initialized for hql and uninitialied for criteria; why?
 				// assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
 				// assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
 				assertTrue( yogiMap.get( "student" ) instanceof Student );
 				assertTrue( shermanMap.get( "student" ) instanceof Student );
 				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
 				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false);
 	}
 
 	@Test
 	public void testAliasToEntityMapMultiProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "e.student" ), "student" )
 										.add( Property.forName( "e.semester" ), "semester" )
 										.add( Property.forName( "e.year" ), "year" )
 										.add( Property.forName( "e.course" ), "course" )
 						)
 						.addOrder( Order.asc( "studentNumber") )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student as student, e.semester as semester, e.year as year, e.course as course from Enrolment e order by e.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( 4, yogiMap.size() );
 				assertEquals( 4, shermanMap.size() );
 				assertTrue( yogiMap.get( "student" ) instanceof Student );
 				assertTrue( shermanMap.get( "student" ) instanceof Student );
 				// TODO: following are initialized for hql and uninitialied for criteria; why?
 				// assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
 				// assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
 				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
 				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
 				assertEquals( yogiEnrolmentExpected.getSemester(), yogiMap.get( "semester" ) );
 				assertEquals( yogiEnrolmentExpected.getYear(), yogiMap.get( "year" )  );
 				assertEquals( courseExpected, yogiMap.get( "course" ) );
 				assertEquals( shermanEnrolmentExpected.getSemester(), shermanMap.get( "semester" ) );
 				assertEquals( shermanEnrolmentExpected.getYear(), shermanMap.get( "year" )  );
 				assertEquals( courseExpected, shermanMap.get( "course" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToEntityMapMultiProjectionWithNullAliasList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "e.student" ), "student" )
 										.add( Property.forName( "e.semester" ) )
 										.add( Property.forName( "e.year" ) )
 										.add( Property.forName( "e.course" ), "course" )
 						)
 						.addOrder( Order.asc( "e.studentNumber") )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student as student, e.semester, e.year, e.course as course from Enrolment e order by e.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				// TODO: following are initialized for hql and uninitialied for criteria; why?
 				// assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
 				// assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
 				assertTrue( yogiMap.get( "student" ) instanceof Student );
 				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
 				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
 				assertNull( yogiMap.get( "semester" ) );
 				assertNull( yogiMap.get( "year" )  );
 				assertEquals( courseExpected, yogiMap.get( "course" ) );
 				assertNull( shermanMap.get( "semester" ) );
 				assertNull( shermanMap.get( "year" )  );
 				assertEquals( courseExpected, shermanMap.get( "course" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToEntityMapMultiAggregatedPropProjectionSingleResult() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class )
 						.setProjection(
 								Projections.projectionList()
 									.add( Projections.min( "studentNumber" ).as( "minStudentNumber" ) )
 									.add( Projections.max( "studentNumber" ).as( "maxStudentNumber" ) )
 						)
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select min( e.studentNumber ) as minStudentNumber, max( e.studentNumber ) as maxStudentNumber from Enrolment e" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Map );
 				Map resultMap = ( Map ) results;
 				assertEquals( 2, resultMap.size() );
 				assertEquals( yogiExpected.getStudentNumber(), resultMap.get( "minStudentNumber" ) );
 				assertEquals( shermanExpected.getStudentNumber(), resultMap.get( "maxStudentNumber" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testOneNonEntityProjectionUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.semester" ) )
 						.add( Restrictions.eq( "e.studentNumber", shermanEnrolmentExpected.getStudentNumber() ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.semester from Enrolment e where e.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", shermanEnrolmentExpected.getStudentNumber() );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Short );
 				assertEquals( Short.valueOf( shermanEnrolmentExpected.getSemester() ), results );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testOneNonEntityProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.semester" ) )
 						.addOrder( Order.asc( "e.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.semester from Enrolment e order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiEnrolmentExpected.getSemester(), resultList.get( 0 ) );
 				assertEquals( shermanEnrolmentExpected.getSemester(), resultList.get( 1 ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testListElementsProjectionList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.secretCodes" )
 						.setProjection( Projections.property( "s.secretCodes" ) )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select elements(s.secretCodes) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertTrue( resultList.contains( yogiExpected.getSecretCodes().get( 0 ) ) );
 				assertTrue( resultList.contains( shermanExpected.getSecretCodes().get( 0 ) ) );
 				assertTrue( resultList.contains( shermanExpected.getSecretCodes().get( 1 ) ) );
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testOneEntityProjectionUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class )
 						.setProjection( Projections.property( "student" ) )
 						.add( Restrictions.eq( "studentNumber", Long.valueOf( yogiExpected.getStudentNumber() ) ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student from Enrolment e where e.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", Long.valueOf( yogiExpected.getStudentNumber() ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Student );
 				Student student = ( Student ) results;
 				// TODO: following is initialized for hql and uninitialied for criteria; why?
 				//assertFalse( Hibernate.isInitialized( student ) );
 				assertEquals( yogiExpected.getStudentNumber(), student.getStudentNumber() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testOneEntityProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			// should use PassThroughTransformer by default
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.student" ) )
 						.addOrder( Order.asc( "e.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student from Enrolment e order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				// TODO: following is initialized for hql and uninitialied for criteria; why?
 				//assertFalse( Hibernate.isInitialized( resultList.get( 0 ) ) );
 				//assertFalse( Hibernate.isInitialized( resultList.get( 1 ) ) );
 				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) resultList.get( 0 ) ).getStudentNumber() );
 				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) resultList.get( 1 ) ).getStudentNumber() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiEntityProjectionUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "student" ) )
 										.add( Property.forName( "semester" ) )
 										.add( Property.forName( "year" ) )
 										.add( Property.forName( "course" ) )
 						)
 						.add( Restrictions.eq( "studentNumber", Long.valueOf( shermanEnrolmentExpected.getStudentNumber() ) ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select e.student, e.semester, e.year, e.course from Enrolment e  where e.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", shermanEnrolmentExpected.getStudentNumber() );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Object[] );
 				Object shermanObjects[] = ( Object [] ) results;
 				assertEquals( 4, shermanObjects.length );
 				assertNotNull( shermanObjects[ 0 ] );
 				assertTrue( shermanObjects[ 0 ] instanceof Student );
 				// TODO: following is initialized for hql and uninitialied for criteria; why?
 				//assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
 				assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 				assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 				assertTrue( ! ( shermanObjects[ 3 ] instanceof HibernateProxy ) );
 				assertTrue( shermanObjects[ 3 ] instanceof Course );
 				assertEquals( courseExpected, shermanObjects[ 3 ] );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testMultiEntityProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "e.student" ) )
 										.add( Property.forName( "e.semester" ) )
 										.add( Property.forName( "e.year" ) )
 										.add( Property.forName( "e.course" ) )
 						)
 						.addOrder( Order.asc( "e.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student, e.semester, e.year, e.course from Enrolment e order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( 4, yogiObjects.length );
 				// TODO: following is initialized for hql and uninitialied for criteria; why?
 				//assertFalse( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
 				//assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
 				assertTrue( yogiObjects[ 0 ] instanceof Student );
 				assertTrue( shermanObjects[ 0 ] instanceof Student );
 				assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
 				assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
 				assertEquals( courseExpected, yogiObjects[ 3 ] );
 				assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 				assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 				assertTrue( shermanObjects[ 3 ] instanceof Course );
 				assertEquals( courseExpected, shermanObjects[ 3 ] );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiEntityProjectionAliasedList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "e.student" ).as( "st" ) )
 										.add( Property.forName( "e.semester" ).as("sem" ) )
 										.add( Property.forName( "e.year" ).as( "yr" ) )
 										.add( Property.forName( "e.course" ).as( "c" ) )
 						)
 						.addOrder( Order.asc( "e.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student as st, e.semester as sem, e.year as yr, e.course as c from Enrolment e order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( 4, yogiObjects.length );
 				// TODO: following is initialized for hql and uninitialied for criteria; why?
 				//assertFalse( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
 				//assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
 				assertTrue( yogiObjects[ 0 ] instanceof Student );
 				assertTrue( shermanObjects[ 0 ] instanceof Student );
 				assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
 				assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
 				assertEquals( courseExpected, yogiObjects[ 3 ] );
 				assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 				assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 				assertTrue( shermanObjects[ 3 ] instanceof Course );
 				assertEquals( courseExpected, shermanObjects[ 3 ] );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testSingleAggregatedPropProjectionSingleResult() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class )
 						.setProjection( Projections.min( "studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select min( e.studentNumber ) from Enrolment e" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Long );
 				assertEquals( Long.valueOf( yogiExpected.getStudentNumber() ), results );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testMultiAggregatedPropProjectionSingleResult() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class )
 						.setProjection(
 								Projections.projectionList()
 									.add( Projections.min( "studentNumber" ).as( "minStudentNumber" ) )
 									.add( Projections.max( "studentNumber" ).as( "maxStudentNumber" ) )
 						);
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select min( e.studentNumber ) as minStudentNumber, max( e.studentNumber ) as maxStudentNumber from Enrolment e" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Object[] );
 				Object[] resultObjects = ( Object[] ) results;
 				assertEquals( Long.valueOf( yogiExpected.getStudentNumber() ), resultObjects[ 0 ] );
 				assertEquals( Long.valueOf( shermanExpected.getStudentNumber() ), resultObjects[ 1 ] );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testAliasToBeanDtoOneArgList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection( Projections.property( "st.name" ).as( "studentName" ) )
 				.addOrder( Order.asc( "st.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName from Student st order by st.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertNull( dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertNull( dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToBeanDtoMultiArgList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Property.forName( "co.description" ).as( "courseDescription" ) )
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiProjectionListThenApplyAliasToBean() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ) )
 								.add( Property.forName( "co.description" ) )
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				ResultTransformer transformer = Transformers.aliasToBean( StudentDTO.class );
 				String[] aliases = new String[] { "studentName", "courseDescription" };
 				for ( int i = 0 ; i < resultList.size(); i++ ) {
 					resultList.set(
 							i,
 							transformer.transformTuple( ( Object[] ) resultList.get( i ), aliases )
 					);
 				}
 
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToBeanDtoLiteralArgList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Projections.sqlProjection(
 										"'lame description' as courseDescription",
 										new String[] { "courseDescription" },
-										new Type[] { Hibernate.STRING }
+										new Type[] { StandardBasicTypes.STRING }
 								)
 						)
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, 'lame description' as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( "lame description", dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( "lame description", dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToBeanDtoWithNullAliasList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Property.forName( "st.studentNumber" ) )
 								.add( Property.forName( "co.description" ).as( "courseDescription" ) )
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testOneSelectNewNoAliasesList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) throws Exception {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection( Projections.property( "s.name" ) )
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() throws NoSuchMethodException {
 				return StudentDTO.class.getConstructor( PersonName.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new org.hibernate.test.querycache.StudentDTO(s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO yogi = ( StudentDTO ) resultList.get( 0 );
 				assertNull( yogi.getDescription() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				StudentDTO sherman = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 				assertNull( sherman.getDescription() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testOneSelectNewAliasesList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) throws Exception {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection( Projections.property( "s.name" ).as( "name" ))
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() throws NoSuchMethodException {
 				return StudentDTO.class.getConstructor( PersonName.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new org.hibernate.test.querycache.StudentDTO(s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO yogi = ( StudentDTO ) resultList.get( 0 );
 				assertNull( yogi.getDescription() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				StudentDTO sherman = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 				assertNull( sherman.getDescription() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiSelectNewList() throws Exception{
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) throws Exception {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "s.studentNumber" ).as( "studentNumber" ))
 								.add( Property.forName( "s.name" ).as( "name" ))
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() throws NoSuchMethodException {
 				return  Student.class.getConstructor( long.class, PersonName.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new Student(s.studentNumber, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Student yogi = ( Student ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogi.getStudentNumber() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				Student sherman = ( Student ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), sherman.getStudentNumber() );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiSelectNewWithLiteralList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) throws Exception {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
-								.add( Projections.sqlProjection( "555 as studentNumber", new String[]{ "studentNumber" }, new Type[] { Hibernate.LONG } ) )
+								.add( Projections.sqlProjection( "555 as studentNumber", new String[]{ "studentNumber" }, new Type[] { StandardBasicTypes.LONG } ) )
 								.add( Property.forName( "s.name" ).as( "name" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() throws NoSuchMethodException {
 				return Student.class.getConstructor( long.class, PersonName.class );
 			}
 		};
 
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new Student(555L, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Student yogi = ( Student ) resultList.get( 0 );
 				assertEquals( 555L, yogi.getStudentNumber() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				Student sherman = ( Student ) resultList.get( 1 );
 				assertEquals( 555L, sherman.getStudentNumber() );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiSelectNewListList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "s.studentNumber" ).as( "studentNumber" ))
 								.add( Property.forName( "s.name" ).as( "name" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( Transformers.TO_LIST );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new list(s.studentNumber, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				List yogiList = ( List ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogiList.get( 0 ) );
 				assertEquals( yogiExpected.getName(), yogiList.get( 1 ) );
 				List shermanList = ( List ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), shermanList.get( 0 ) );
 				assertEquals( shermanExpected.getName(), shermanList.get( 1 ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiSelectNewMapUsingAliasesList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "s.studentNumber" ).as( "sNumber" ) )
 								.add( Property.forName( "s.name" ).as( "sName" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new map(s.studentNumber as sNumber, s.name as sName) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogiMap.get( "sNumber" ) );
 				assertEquals( yogiExpected.getName(), yogiMap.get( "sName" ) );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), shermanMap.get( "sNumber" ) );
 				assertEquals( shermanExpected.getName(), shermanMap.get( "sName" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiSelectNewMapUsingAliasesWithFetchJoinList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pc", Criteria.LEFT_JOIN  )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber" ))
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new map(s as s, pc as pc) from Student s left join s.preferredCourse pc left join fetch s.enrolments order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiMap.get( "s" ) );
 				assertEquals( yogiExpected.getPreferredCourse(), yogiMap.get( "pc" ) );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanMap.get( "s" ) );
 				assertNull( shermanMap.get( "pc" ) );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlSelectNewMapExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMultiSelectAliasToEntityMapUsingAliasesWithFetchJoinList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pc", Criteria.LEFT_JOIN  )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlAliasToEntityMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s as s, pc as pc from Student s left join s.preferredCourse pc left join fetch s.enrolments order by s.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiMap.get( "s" ) );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Course ) yogiMap.get( "pc" ) ).getCourseCode()
 				);
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanMap.get( "s" ) );
 				assertNull( shermanMap.get( "pc" ) );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertEquals( yogiExpected.getPreferredCourse(), yogiMap.get( "pc" ) );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlAliasToEntityMapExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testMultiSelectUsingImplicitJoinWithFetchJoinListHql() throws Exception {
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s as s, s.preferredCourse as pc from Student s left join fetch s.enrolments" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Object[] );
 				Object[] yogiObjects = ( Object[] ) results;
 				assertEquals( 2, yogiObjects.length );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Course ) yogiObjects[ 1 ] ).getCourseCode()
 				);
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
 				}
 			}
 		};
 		runTest( hqlExecutor, null, checker, true );
 	}
 
 	@Test
 	public void testSelectNewMapUsingAliasesList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "s.studentNumber" ).as( "sNumber" ) )
 								.add( Property.forName( "s.name" ).as( "sName" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new map(s.studentNumber as sNumber, s.name as sName) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogiMap.get( "sNumber" ) );
 				assertEquals( yogiExpected.getName(), yogiMap.get( "sName" ) );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), shermanMap.get( "sNumber" ) );
 				assertEquals( shermanExpected.getName(), shermanMap.get( "sName" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testSelectNewEntityConstructorList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "s.studentNumber" ).as( "studentNumber" ) )
 								.add( Property.forName( "s.name" ).as( "name" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() {
 				Type studentNametype =
 						( ( SessionFactoryImpl ) sessionFactory() )
 								.getEntityPersister( Student.class.getName() )
 								.getPropertyType( "name" );
-				return ReflectHelper.getConstructor( Student.class, new Type[] {Hibernate.LONG, studentNametype} );
+				return ReflectHelper.getConstructor( Student.class, new Type[] {StandardBasicTypes.LONG, studentNametype} );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new Student(s.studentNumber, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Student yogi = ( Student ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogi.getStudentNumber() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				Student sherman = ( Student ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), sherman.getStudentNumber() );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMapKeyList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a" )
 				.setProjection( Projections.property( "a.addressType" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select key(s.addresses) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.contains( "home" ) );
 				assertTrue( resultList.contains( "work" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testMapValueList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a" )
 				.setProjection( Projections.property( "s.addresses" ));
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select value(s.addresses) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "home" ) ) );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "work" ) ) );
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testMapEntryList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Projections.property( "a.addressType" ) )
 								.add( Projections.property( "s.addresses" ).as( "a" ) );
 				)
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select entry(s.addresses) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Iterator it=resultList.iterator();
 				assertTrue( resultList.get( 0 ) instanceof Map.Entry );
 				Map.Entry entry = ( Map.Entry ) it.next();
 				if ( "home".equals( entry.getKey() ) ) {
 					assertTrue( yogiExpected.getAddresses().get( "home" ).equals( entry.getValue() ) );
 					entry = ( Map.Entry ) it.next();
 					assertTrue( yogiExpected.getAddresses().get( "work" ).equals( entry.getValue() ) );
 				}
 				else {
 					assertTrue( "work".equals( entry.getKey() ) );
 					assertTrue( yogiExpected.getAddresses().get( "work" ).equals( entry.getValue() ) );
 					entry = ( Map.Entry ) it.next();
 					assertTrue( yogiExpected.getAddresses().get( "home" ).equals( entry.getValue() ) );
 				}
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testMapElementsList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.INNER_JOIN )
 				.setProjection( Projections.property( "s.addresses" ) );
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select elements(a) from Student s inner join s.addresses a" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "home" ) ) );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "work" ) ) );
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	protected void runTest(HqlExecutor hqlExecutor, CriteriaExecutor criteriaExecutor, ResultChecker checker, boolean isSingleResult)
 		throws Exception {
 		createData();
 		try {
 			if ( criteriaExecutor != null ) {
 				runTest( criteriaExecutor, checker, isSingleResult );
 			}
 			if ( hqlExecutor != null ) {
 				runTest( hqlExecutor, checker, isSingleResult );
 			}
 		}
 		finally {
 			deleteData();
 		}
 	}
 
 	private boolean isQueryCacheGetEnabled() {
 		return getQueryCacheMode() == CacheMode.NORMAL ||
 			getQueryCacheMode() == CacheMode.GET;
 	}
 
 	private boolean isQueryCachePutEnabled() {
 		return getQueryCacheMode() == CacheMode.NORMAL ||
 			getQueryCacheMode() == CacheMode.PUT;
 	}
 
 	protected void runTest(QueryExecutor queryExecutor, ResultChecker resultChecker, boolean isSingleResult) throws Exception{
 		clearCache();
 		clearStatistics();
 
 		Object results = queryExecutor.execute( isSingleResult );
 
 		assertHitCount( 0 );
 		assertMissCount( isQueryCacheGetEnabled() ? 1 : 0 );
 		assertPutCount( isQueryCachePutEnabled() ? 1 : 0 );
 		clearStatistics();
 
 		resultChecker.check( results );
 
 		// check again to make sure nothing got initialized while checking results;
 		assertHitCount( 0 );
 		assertMissCount( 0 );
 		assertPutCount( 0 );
 		clearStatistics();
 
 		results = queryExecutor.execute( isSingleResult );
 
 		assertHitCount( isQueryCacheGetEnabled() ? 1 : 0 );
 		assertMissCount( 0 );
 		assertPutCount( ! isQueryCacheGetEnabled() && isQueryCachePutEnabled() ? 1 : 0 );
 		clearStatistics();
 
 		resultChecker.check( results );
 
 		// check again to make sure nothing got initialized while checking results;
 		assertHitCount( 0 );
 		assertMissCount( 0 );
 		assertPutCount( 0 );
 		clearStatistics();
 	}
 
 	private void multiPropProjectionNoTransformerDynNonLazy(CacheMode sessionCacheMode,
 																 boolean isCacheableQuery) {
 		Session s = openSession();
 		s.setCacheMode( sessionCacheMode );
 		Transaction t = s.beginTransaction();
 		List resultList = s.createCriteria( Enrolment.class )
 				.setCacheable( isCacheableQuery )
 				.setFetchMode( "student", FetchMode.JOIN )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "student" ), "student" )
 								.add( Property.forName( "semester" ), "semester" )
 								.add( Property.forName( "year" ), "year" )
 								.add( Property.forName( "course" ), "course" )
 				)
 				.addOrder( Order.asc( "studentNumber") )
 				.list();
 		t.commit();
 		s.close();
 
 		assertEquals( 2, resultList.size() );
 		Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 		Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 		assertEquals( 4, yogiObjects.length );
 		assertTrue( yogiObjects[ 0 ] instanceof Student );
 		assertTrue( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
 		assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
 		assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
 		assertEquals( courseExpected, yogiObjects[ 3 ] );
 		assertTrue( shermanObjects[ 0 ] instanceof Student );
 		assertTrue( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
 		assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 		assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 		assertTrue( ! ( shermanObjects[ 3 ] instanceof HibernateProxy ) );
 		assertTrue( shermanObjects[ 3 ] instanceof Course );
 		assertEquals( courseExpected, shermanObjects[ 3 ] );
 	}
 
 /*
 	{
 
 		assertEquals( 2, resultList.size() );
 		Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 		Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 		assertEquals( 4, yogiObjects.length );
 		assertEquals( yogiExpected, ( Student ) yogiObjects[ 0 ] );
 		assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
 		assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
 		assertEquals( courseExpected, yogiObjects[ 3 ] );
 		assertEquals( shermanExpected, ( Student ) shermanObjects[ 0 ] );
 		assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 		assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 		assertEquals( courseExpected, shermanObjects[ 3 ] );
 
 	}
 
 */
 /*
 	private void executeProperty() {
 		resultList = s.createCriteria( Student.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Projections.id().as( "studentNumber" ) )
 								.add( Property.forName( "name" ), "name" )
 								.add( Property.forName( "cityState" ), "cityState" )
 								.add( Property.forName( "preferredCourse" ), "preferredCourse" )
 				)
 				.list();
 		assertEquals( 2, resultList.size() );
 		for ( Iterator it = resultList.iterator(); it.hasNext(); ) {
 			Object[] objects = ( Object[] ) it.next();
 			assertEquals( 4, objects.length );
 			assertTrue( objects[0] instanceof Long );
 			assertTrue( objects[1] instanceof String );
 			if ( "yogiExpected King".equals( objects[1] ) ) {
 				assertTrue( objects[2] instanceof Name );
 				assertTrue( objects[3] instanceof Course );
 			}
 			else {
 				assertNull( objects[2] );
 				assertNull( objects[3] );
 			}
 		}
 
 		Object[] aResult = ( Object[] ) s.createCriteria( Student.class )
 				.setCacheable( true )
 				.add( Restrictions.idEq( new Long( 667 ) ) )
 				.setProjection(
 						Projections.projectionList()
 								.add( Projections.id().as( "studentNumber" ) )
 								.add( Property.forName( "name" ), "name" )
 								.add( Property.forName( "cityState" ), "cityState" )
 								.add( Property.forName( "preferredCourse" ), "preferredCourse" )
 				)
 				.uniqueResult();
 		assertNotNull( aResult );
 		assertEquals( 4, aResult.length );
 		assertTrue( aResult[0] instanceof Long );
 		assertTrue( aResult[1] instanceof String );
 		assertTrue( aResult[2] instanceof Name );
 		assertTrue( aResult[3] instanceof Course );
 
 		Long count = ( Long ) s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection( Property.forName( "studentNumber" ).count().setDistinct() )
 				.uniqueResult();
 		assertEquals( count, new Long( 2 ) );
 
 		Object object = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "studentNumber" ).count() )
 								.add( Property.forName( "studentNumber" ).max() )
 								.add( Property.forName( "studentNumber" ).min() )
 								.add( Property.forName( "studentNumber" ).avg() )
 				)
 				.uniqueResult();
 		Object[] result = ( Object[] ) object;
 
 		assertEquals( new Long( 2 ), result[0] );
 		assertEquals( new Long( 667 ), result[1] );
 		assertEquals( new Long( 101 ), result[2] );
 		assertEquals( 384.0, ( ( Double ) result[3] ).doubleValue(), 0.01 );
 
 
 		s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.add( Property.forName( "studentNumber" ).gt( new Long( 665 ) ) )
 				.add( Property.forName( "studentNumber" ).lt( new Long( 668 ) ) )
 				.add( Property.forName( "courseCode" ).like( "HIB", MatchMode.START ) )
 				.add( Property.forName( "year" ).eq( new Short( ( short ) 1999 ) ) )
 				.addOrder( Property.forName( "studentNumber" ).asc() )
 				.uniqueResult();
 
 		List resultWithMaps = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "studentNumber" ).as( "stNumber" ) )
 								.add( Property.forName( "courseCode" ).as( "cCode" ) )
 				)
 				.add( Property.forName( "studentNumber" ).gt( new Long( 665 ) ) )
 				.add( Property.forName( "studentNumber" ).lt( new Long( 668 ) ) )
 				.addOrder( Property.forName( "studentNumber" ).asc() )
 				.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
 				.list();
 
 		assertEquals( 1, resultWithMaps.size() );
 		Map m1 = ( Map ) resultWithMaps.get( 0 );
 
 		assertEquals( new Long( 667 ), m1.get( "stNumber" ) );
 		assertEquals( courseExpected.getCourseCode(), m1.get( "cCode" ) );
 
 		resultWithMaps = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection( Property.forName( "studentNumber" ).as( "stNumber" ) )
 				.addOrder( Order.desc( "stNumber" ) )
 				.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
 				.list();
 
 		assertEquals( 2, resultWithMaps.size() );
 		Map m0 = ( Map ) resultWithMaps.get( 0 );
 		m1 = ( Map ) resultWithMaps.get( 1 );
 
 		assertEquals( new Long( 101 ), m1.get( "stNumber" ) );
 		assertEquals( new Long( 667 ), m0.get( "stNumber" ) );
 
 		List resultWithAliasedBean = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.createAlias( "student", "st" )
 				.createAlias( "courseExpected", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Property.forName( "co.description" ).as( "courseDescription" ) )
 				)
 				.addOrder( Order.desc( "studentName" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) )
 				.list();
 
 		assertEquals( 2, resultWithAliasedBean.size() );
 
 		StudentDTO dto = ( StudentDTO ) resultWithAliasedBean.get( 0 );
 		assertNotNull( dto.getDescription() );
 		assertNotNull( dto.getName() );
 
 		CourseMeeting courseMeetingDto = ( CourseMeeting ) s.createCriteria( CourseMeeting.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "id" ).as( "id" ) )
 								.add( Property.forName( "courseExpected" ).as( "courseExpected" ) )
 				)
 				.addOrder( Order.desc( "id" ) )
 				.setResultTransformer( Transformers.aliasToBean( CourseMeeting.class ) )
 				.uniqueResult();
 
 		assertNotNull( courseMeetingDto.getId() );
 		assertEquals( courseExpected.getCourseCode(), courseMeetingDto.getId().getCourseCode() );
 		assertEquals( "Monday", courseMeetingDto.getId().getDay() );
 		assertEquals( "1313 Mockingbird Lane", courseMeetingDto.getId().getLocation() );
 		assertEquals( 1, courseMeetingDto.getId().getPeriod() );
 		assertEquals( courseExpected.getDescription(), courseMeetingDto.getCourse().getDescription() );
 
 		s.createCriteria( Student.class )
 				.setCacheable( true )
 				.add( Restrictions.like( "name", "yogiExpected", MatchMode.START ) )
 				.addOrder( Order.asc( "name" ) )
 				.createCriteria( "enrolments", "e" )
 				.addOrder( Order.desc( "year" ) )
 				.addOrder( Order.desc( "semester" ) )
 				.createCriteria( "courseExpected", "c" )
 				.addOrder( Order.asc( "description" ) )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "this.name" ) )
 								.add( Property.forName( "e.year" ) )
 								.add( Property.forName( "e.semester" ) )
 								.add( Property.forName( "c.courseCode" ) )
 								.add( Property.forName( "c.description" ) )
 				)
 				.uniqueResult();
 
 		Projection p1 = Projections.projectionList()
 				.add( Property.forName( "studentNumber" ).count() )
 				.add( Property.forName( "studentNumber" ).max() )
 				.add( Projections.rowCount() );
 
 		Projection p2 = Projections.projectionList()
 				.add( Property.forName( "studentNumber" ).min() )
 				.add( Property.forName( "studentNumber" ).avg() )
 				.add(
 						Projections.sqlProjection(
 								"1 as constOne, count(*) as countStar",
 								new String[] { "constOne", "countStar" },
 								new Type[] { Hibernate.INTEGER, Hibernate.INTEGER }
 						)
 				);
 
 		Object[] array = ( Object[] ) s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection( Projections.projectionList().add( p1 ).add( p2 ) )
 				.uniqueResult();
 
 		assertEquals( array.length, 7 );
 
 		List list = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.createAlias( "student", "st" )
 				.createAlias( "courseExpected", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "co.courseCode" ).group() )
 								.add( Property.forName( "st.studentNumber" ).count().setDistinct() )
 								.add( Property.forName( "year" ).group() )
 				)
 				.list();
 
 		assertEquals( list.size(), 2 );
 	}
 */
 	protected void clearCache() {
 		sessionFactory().getCache().evictQueryRegions();
 	}
 
 	protected void clearStatistics() {
 		sessionFactory().getStatistics().clear();
 	}
 
 	protected void assertEntityFetchCount(int expected) {
 		int actual = ( int ) sessionFactory().getStatistics().getEntityFetchCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertCount(int expected) {
 		int actual = ( int ) sessionFactory().getStatistics().getQueries().length;
 		assertEquals( expected, actual );
 	}
 
 	protected void assertHitCount(int expected) {
 		int actual = ( int ) sessionFactory().getStatistics().getQueryCacheHitCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertMissCount(int expected) {
 		int actual = ( int ) sessionFactory().getStatistics().getQueryCacheMissCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertPutCount(int expected) {
 		int actual = ( int ) sessionFactory().getStatistics().getQueryCachePutCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertInsertCount(int expected) {
 		int inserts = ( int ) sessionFactory().getStatistics().getEntityInsertCount();
 		assertEquals( "unexpected insert count", expected, inserts );
 	}
 
 	protected void assertUpdateCount(int expected) {
 		int updates = ( int ) sessionFactory().getStatistics().getEntityUpdateCount();
 		assertEquals( "unexpected update counts", expected, updates );
 	}
 
 	protected void assertDeleteCount(int expected) {
 		int deletes = ( int ) sessionFactory().getStatistics().getEntityDeleteCount();
 		assertEquals( "unexpected delete counts", expected, deletes );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/version/db/DbVersionTest.java b/hibernate-core/src/test/java/org/hibernate/test/version/db/DbVersionTest.java
index 56353159b7..9d1e783431 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/version/db/DbVersionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/version/db/DbVersionTest.java
@@ -1,134 +1,135 @@
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
 package org.hibernate.test.version.db;
+
 import java.sql.Timestamp;
 
-import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
+import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class DbVersionTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "version/db/User.hbm.xml" };
 	}
 
 	@Test
 	public void testCollectionVersion() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User steve = new User( "steve" );
 		s.persist( steve );
 		Group admin = new Group( "admin" );
 		s.persist( admin );
 		t.commit();
 		s.close();
 
 		Timestamp steveTimestamp = steve.getTimestamp();
 
 		// For dialects (Oracle8 for example) which do not return "true
 		// timestamps" sleep for a bit to allow the db date-time increment...
 		Thread.sleep( 1500 );
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		admin = ( Group ) s.get( Group.class, admin.getId() );
 		steve.getGroups().add( admin );
 		admin.getUsers().add( steve );
 		t.commit();
 		s.close();
 
-		assertFalse( "owner version not incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertFalse( "owner version not incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
 
 		steveTimestamp = steve.getTimestamp();
 		Thread.sleep( 1500 );
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		steve.getGroups().clear();
 		t.commit();
 		s.close();
 
-		assertFalse( "owner version not incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertFalse( "owner version not incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.load( User.class, steve.getId() ) );
 		s.delete( s.load( Group.class, admin.getId() ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionNoVersion() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User steve = new User( "steve" );
 		s.persist( steve );
 		Permission perm = new Permission( "silly", "user", "rw" );
 		s.persist( perm );
 		t.commit();
 		s.close();
 
-		Timestamp steveTimestamp = ( Timestamp ) steve.getTimestamp();
+		Timestamp steveTimestamp = steve.getTimestamp();
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		perm = ( Permission ) s.get( Permission.class, perm.getId() );
 		steve.getPermissions().add( perm );
 		t.commit();
 		s.close();
 
-		assertTrue( "owner version was incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertTrue( "owner version was incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
 
 		s = openSession();
 		t = s.beginTransaction();
 		steve = ( User ) s.get( User.class, steve.getId() );
 		steve.getPermissions().clear();
 		t.commit();
 		s.close();
 
-		assertTrue( "owner version was incremented", Hibernate.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
+		assertTrue( "owner version was incremented", StandardBasicTypes.TIMESTAMP.isEqual( steveTimestamp, steve.getTimestamp() ) );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.load( User.class, steve.getId() ) );
 		s.delete( s.load( Permission.class, perm.getId() ) );
 		t.commit();
 		s.close();
 	}
 }
\ No newline at end of file
