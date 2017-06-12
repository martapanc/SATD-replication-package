diff --git a/hibernate-core/src/main/java/org/hibernate/Hibernate.java b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
index 53b3cb89ab..e2cae866c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+++ b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
@@ -1,488 +1,489 @@
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
+
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.HibernateIterator;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.intercept.FieldInterceptionHelper;
-import org.hibernate.intercept.FieldInterceptor;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/JavassistInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
similarity index 77%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/JavassistInstrumenter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
index 0acef33731..1182ed9d18 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/JavassistInstrumenter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/internal/JavassistInstrumenter.java
@@ -1,98 +1,102 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.bytecode.buildtime;
+package org.hibernate.bytecode.buildtime.internal;
 
 import java.io.ByteArrayInputStream;
 import java.io.DataInputStream;
 import java.io.IOException;
 import java.util.Set;
+
 import javassist.bytecode.ClassFile;
-import org.hibernate.bytecode.ClassTransformer;
-import org.hibernate.bytecode.javassist.BytecodeProviderImpl;
-import org.hibernate.bytecode.javassist.FieldHandled;
-import org.hibernate.bytecode.util.BasicClassFilter;
-import org.hibernate.bytecode.util.ClassDescriptor;
+
+import org.hibernate.bytecode.buildtime.spi.AbstractInstrumenter;
+import org.hibernate.bytecode.buildtime.spi.BasicClassFilter;
+import org.hibernate.bytecode.buildtime.spi.ClassDescriptor;
+import org.hibernate.bytecode.buildtime.spi.Logger;
+import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
+import org.hibernate.bytecode.internal.javassist.FieldHandled;
+import org.hibernate.bytecode.spi.ClassTransformer;
 
 /**
  * Strategy for performing build-time instrumentation of persistent classes in order to enable
  * field-level interception using Javassist.
  *
  * @author Steve Ebersole
  * @author Muga Nishizawa
  */
 public class JavassistInstrumenter extends AbstractInstrumenter {
 
 	private static final BasicClassFilter CLASS_FILTER = new BasicClassFilter();
 
 	private final BytecodeProviderImpl provider = new BytecodeProviderImpl();
 
 	public JavassistInstrumenter(Logger logger, Options options) {
 		super( logger, options );
 	}
 
 	@Override
     protected ClassDescriptor getClassDescriptor(byte[] bytecode) throws IOException {
 		return new CustomClassDescriptor( bytecode );
 	}
 
 	@Override
     protected ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames) {
 		if ( descriptor.isInstrumented() ) {
 			logger.debug( "class [" + descriptor.getName() + "] already instrumented" );
 			return null;
 		}
 		else {
 			return provider.getTransformer( CLASS_FILTER, new CustomFieldFilter( descriptor, classNames ) );
 		}
 	}
 
 	private static class CustomClassDescriptor implements ClassDescriptor {
 		private final byte[] bytes;
 		private final ClassFile classFile;
 
 		public CustomClassDescriptor(byte[] bytes) throws IOException {
 			this.bytes = bytes;
 			this.classFile = new ClassFile( new DataInputStream( new ByteArrayInputStream( bytes ) ) );
 		}
 
 		public String getName() {
 			return classFile.getName();
 		}
 
 		public boolean isInstrumented() {
-			String[] intfs = classFile.getInterfaces();
-			for ( int i = 0; i < intfs.length; i++ ) {
-				if ( FieldHandled.class.getName().equals( intfs[i] ) ) {
+			String[] interfaceNames = classFile.getInterfaces();
+			for ( String interfaceName : interfaceNames ) {
+				if ( FieldHandled.class.getName().equals( interfaceName ) ) {
 					return true;
 				}
 			}
 			return false;
 		}
 
 		public byte[] getBytes() {
 			return bytes;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/AbstractInstrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/AbstractInstrumenter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
index b982e60173..2b22755321 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/AbstractInstrumenter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/AbstractInstrumenter.java
@@ -1,442 +1,437 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.bytecode.buildtime;
+package org.hibernate.bytecode.buildtime.spi;
 
 import java.io.ByteArrayInputStream;
 import java.io.DataInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.util.HashSet;
-import java.util.Iterator;
 import java.util.Set;
 import java.util.zip.CRC32;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipInputStream;
 import java.util.zip.ZipOutputStream;
-import org.hibernate.bytecode.ClassTransformer;
-import org.hibernate.bytecode.util.ByteCodeHelper;
-import org.hibernate.bytecode.util.ClassDescriptor;
-import org.hibernate.bytecode.util.FieldFilter;
+
+import org.hibernate.bytecode.spi.ByteCodeHelper;
+import org.hibernate.bytecode.spi.ClassTransformer;
 
 /**
  * Provides the basic templating of how instrumentation should occur.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractInstrumenter implements Instrumenter {
 	private static final int ZIP_MAGIC = 0x504B0304;
 	private static final int CLASS_MAGIC = 0xCAFEBABE;
 	
 	protected final Logger logger;
 	protected final Options options;
 
 	/**
 	 * Creates the basic instrumentation strategy.
 	 *
 	 * @param logger The bridge to the environment's logging system.
 	 * @param options User-supplied options.
 	 */
 	public AbstractInstrumenter(Logger logger, Options options) {
 		this.logger = logger;
 		this.options = options;
 	}
 
 	/**
 	 * Given the bytecode of a java class, retrieve the descriptor for that class.
 	 *
 	 * @param byecode The class bytecode.
 	 *
 	 * @return The class's descriptor
 	 *
 	 * @throws Exception Indicates problems access the bytecode.
 	 */
 	protected abstract ClassDescriptor getClassDescriptor(byte[] byecode) throws Exception;
 
 	/**
 	 * Create class transformer for the class.
 	 *
 	 * @param descriptor The descriptor of the class to be instrumented.
 	 * @param classNames The names of all classes to be instrumented; the "pipeline" if you will.
 	 *
 	 * @return The transformer for the given class; may return null to indicate that transformation should
 	 * be skipped (ala already instrumented).
 	 */
 	protected abstract ClassTransformer getClassTransformer(ClassDescriptor descriptor, Set classNames);
 
 	/**
 	 * The main instrumentation entry point.  Given a set of files, perform instrumentation on each discovered class
 	 * file.
 	 *
 	 * @param files The files.
 	 */
-	public void execute(Set files) {
-		Set classNames = new HashSet();
+	public void execute(Set<File> files) {
+		Set<String> classNames = new HashSet<String>();
 
 		if ( options.performExtendedInstrumentation() ) {
 			logger.debug( "collecting class names for extended instrumentation determination" );
 			try {
-				Iterator itr = files.iterator();
-				while ( itr.hasNext() ) {
-					final File file = ( File ) itr.next();
+				for ( Object file1 : files ) {
+					final File file = (File) file1;
 					collectClassNames( file, classNames );
 				}
 			}
 			catch ( ExecutionException ee ) {
 				throw ee;
 			}
 			catch ( Exception e ) {
 				throw new ExecutionException( e );
 			}
 		}
 
 		logger.info( "starting instrumentation" );
 		try {
-			Iterator itr = files.iterator();
-			while ( itr.hasNext() ) {
-				final File file = ( File ) itr.next();
+			for ( File file : files ) {
 				processFile( file, classNames );
 			}
 		}
 		catch ( ExecutionException ee ) {
 			throw ee;
 		}
 		catch ( Exception e ) {
 			throw new ExecutionException( e );
 		}
 	}
 
 	/**
 	 * Extract the names of classes from file, addding them to the classNames collection.
 	 * <p/>
 	 * IMPL NOTE : file here may be either a class file or a jar.  If a jar, all entries in the jar file are
 	 * processed.
 	 *
 	 * @param file The file from which to extract class metadata (descriptor).
 	 * @param classNames The collected class name collection.
 	 *
 	 * @throws Exception indicates problems accessing the file or its contents.
 	 */
-	private void collectClassNames(File file, final Set classNames) throws Exception {
+	private void collectClassNames(File file, final Set<String> classNames) throws Exception {
 	    if ( isClassFile( file ) ) {
 			byte[] bytes = ByteCodeHelper.readByteCode( file );
 			ClassDescriptor descriptor = getClassDescriptor( bytes );
 		    classNames.add( descriptor.getName() );
 	    }
 	    else if ( isJarFile( file ) ) {
 		    ZipEntryHandler collector = new ZipEntryHandler() {
 			    public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
 					if ( !entry.isDirectory() ) {
 						// see if the entry represents a class file
 						DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
 						if ( din.readInt() == CLASS_MAGIC ) {
 				            classNames.add( getClassDescriptor( byteCode ).getName() );
 						}
 					}
 			    }
 		    };
 			ZipFileProcessor processor = new ZipFileProcessor( collector );
 		    processor.process( file );
 	    }
 	}
 
 	/**
 	 * Does this file represent a compiled class?
 	 *
 	 * @param file The file to check.
 	 *
 	 * @return True if the file is a class; false otherwise.
 	 *
 	 * @throws IOException Indicates problem access the file.
 	 */
 	protected final boolean isClassFile(File file) throws IOException {
         return checkMagic( file, CLASS_MAGIC );
     }
 
 	/**
 	 * Does this file represent a zip file of some format?
 	 *
 	 * @param file The file to check.
 	 *
 	 * @return True if the file is n archive; false otherwise.
 	 *
 	 * @throws IOException Indicates problem access the file.
 	 */
     protected final boolean isJarFile(File file) throws IOException {
         return checkMagic(file, ZIP_MAGIC);
     }
 
 	protected final boolean checkMagic(File file, long magic) throws IOException {
         DataInputStream in = new DataInputStream( new FileInputStream( file ) );
         try {
             int m = in.readInt();
             return magic == m;
         }
         finally {
             in.close();
         }
     }
 
 	/**
 	 * Actually process the file by applying instrumentation transformations to any classes it contains.
 	 * <p/>
 	 * Again, just like with {@link #collectClassNames} this method can handle both class and archive files.
 	 *
 	 * @param file The file to process.
 	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
 	 * specifies to perform {@link Options#performExtendedInstrumentation() extended} instrumentation.
 	 *
 	 * @throws Exception Indicates an issue either access files or applying the transformations.
 	 */
-	protected void processFile(File file, Set classNames) throws Exception {
+	protected void processFile(File file, Set<String> classNames) throws Exception {
 	    if ( isClassFile( file ) ) {
 			logger.debug( "processing class file : " + file.getAbsolutePath() );
 	        processClassFile( file, classNames );
 	    }
 	    else if ( isJarFile( file ) ) {
 			logger.debug( "processing jar file : " + file.getAbsolutePath() );
 	        processJarFile( file, classNames );
 	    }
 	    else {
 		    logger.debug( "ignoring file : " + file.getAbsolutePath() );
 	    }
 	}
 
 	/**
 	 * Process a class file.  Delegated to from {@link #processFile} in the case of a class file.
 	 *
 	 * @param file The class file to process.
 	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
 	 * specifies to perform {@link Options#performExtendedInstrumentation() extended} instrumentation.
 	 *
 	 * @throws Exception Indicates an issue either access files or applying the transformations.
 	 */
-	protected void processClassFile(File file, Set classNames) throws Exception {
+	protected void processClassFile(File file, Set<String> classNames) throws Exception {
 		byte[] bytes = ByteCodeHelper.readByteCode( file );
 		ClassDescriptor descriptor = getClassDescriptor( bytes );
 		ClassTransformer transformer = getClassTransformer( descriptor, classNames );
 		if ( transformer == null ) {
 			logger.debug( "no trasformer for class file : " + file.getAbsolutePath() );
 			return;
 		}
 
 		logger.info( "processing class : " + descriptor.getName() + ";  file = " + file.getAbsolutePath() );
 		byte[] transformedBytes = transformer.transform(
 				getClass().getClassLoader(),
 				descriptor.getName(),
 				null,
 				null,
 				descriptor.getBytes()
 		);
 
 		OutputStream out = new FileOutputStream( file );
 		try {
 			out.write( transformedBytes );
 			out.flush();
 		}
 		finally {
 			try {
 				out.close();
 			}
 			catch ( IOException ignore) {
 				// intentionally empty
 			}
 		}
 	}
 
 	/**
 	 * Process an archive file.  Delegated to from {@link #processFile} in the case of an archive file.
 	 *
 	 * @param file The archive file to process.
 	 * @param classNames The 'pipeline' of classes to be processed.  Only actually populated when the user
 	 * specifies to perform {@link Options#performExtendedInstrumentation() extended} instrumentation.
 	 *
 	 * @throws Exception Indicates an issue either access files or applying the transformations.
 	 */
-	protected void processJarFile(final File file, final Set classNames) throws Exception {
+	protected void processJarFile(final File file, final Set<String> classNames) throws Exception {
         File tempFile = File.createTempFile(
 		        file.getName(),
 		        null,
 		        new File( file.getAbsoluteFile().getParent() )
         );
 
         try {
 			FileOutputStream fout = new FileOutputStream( tempFile, false );
 			try {
 				final ZipOutputStream out = new ZipOutputStream( fout );
 				ZipEntryHandler transformer = new ZipEntryHandler() {
 					public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
 								logger.debug( "starting zip entry : " + entry.toString() );
 								if ( !entry.isDirectory() ) {
 									// see if the entry represents a class file
 									DataInputStream din = new DataInputStream( new ByteArrayInputStream( byteCode ) );
 									if ( din.readInt() == CLASS_MAGIC ) {
 										ClassDescriptor descriptor = getClassDescriptor( byteCode );
 										ClassTransformer transformer = getClassTransformer( descriptor, classNames );
 										if ( transformer == null ) {
 											logger.debug( "no transformer for zip entry :  " + entry.toString() );
 										}
 										else {
 											logger.info( "processing class : " + descriptor.getName() + ";  entry = " + file.getAbsolutePath() );
 											byteCode = transformer.transform(
 													getClass().getClassLoader(),
 													descriptor.getName(),
 													null,
 													null,
 													descriptor.getBytes()
 											);
 										}
 									}
 									else {
 										logger.debug( "ignoring zip entry : " + entry.toString() );
 									}
 								}
 
 								ZipEntry outEntry = new ZipEntry( entry.getName() );
 								outEntry.setMethod( entry.getMethod() );
 								outEntry.setComment( entry.getComment() );
 								outEntry.setSize( byteCode.length );
 
 								if ( outEntry.getMethod() == ZipEntry.STORED ){
 									CRC32 crc = new CRC32();
 									crc.update( byteCode );
 									outEntry.setCrc( crc.getValue() );
 									outEntry.setCompressedSize( byteCode.length );
 								}
 								out.putNextEntry( outEntry );
 								out.write( byteCode );
 								out.closeEntry();
 					}
 				};
 				ZipFileProcessor processor = new ZipFileProcessor( transformer );
 				processor.process( file );
 				out.close();
 			}
 			finally{
 				fout.close();
 			}
 
             if ( file.delete() ) {
 	            File newFile = new File( tempFile.getAbsolutePath() );
                 if( !newFile.renameTo( file ) ) {
 	                throw new IOException( "can not rename " + tempFile + " to " + file );
                 }
             }
             else {
 	            throw new IOException( "can not delete " + file );
             }
         }
         finally {
 	        if ( ! tempFile.delete() ) {
 				logger.info( "Unable to cleanup temporary jar file : " + tempFile.getAbsolutePath() );
 			}
         }
 	}
 
 	/**
 	 * Allows control over what exacctly to transform.
 	 */
 	protected class CustomFieldFilter implements FieldFilter {
 		private final ClassDescriptor descriptor;
 		private final Set classNames;
 
 		public CustomFieldFilter(ClassDescriptor descriptor, Set classNames) {
 			this.descriptor = descriptor;
 			this.classNames = classNames;
 		}
 
 		public boolean shouldInstrumentField(String className, String fieldName) {
 			if ( descriptor.getName().equals( className ) ) {
 				logger.trace( "accepting transformation of field [" + className + "." + fieldName + "]" );
 				return true;
 			}
 			else {
 				logger.trace( "rejecting transformation of field [" + className + "." + fieldName + "]" );
 				return false;
 			}
 		}
 
 		public boolean shouldTransformFieldAccess(
 				String transformingClassName,
 				String fieldOwnerClassName,
 				String fieldName) {
 			if ( descriptor.getName().equals( fieldOwnerClassName ) ) {
 				logger.trace( "accepting transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]" );
 				return true;
 			}
 			else if ( options.performExtendedInstrumentation() && classNames.contains( fieldOwnerClassName ) ) {
 				logger.trace( "accepting extended transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]" );
 				return true;
 			}
 			else {
 				logger.trace( "rejecting transformation of field access [" + fieldOwnerClassName + "." + fieldName + "]; caller = " + transformingClassName  );
 				return false;
 			}
 		}
 	}
 
 	/**
 	 * General strategy contract for handling entries in an archive file.
 	 */
 	private static interface ZipEntryHandler {
 		/**
 		 * Apply strategy to the given archive entry.
 		 *
 		 * @param entry The archive file entry.
-		 * @param byteCode
+		 * @param byteCode The bytes making up the entry
 		 *
-		 * @throws Exception
+		 * @throws Exception Problem handling entry
 		 */
 		public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception;
 	}
 
 	/**
 	 * Applies {@link ZipEntryHandler} strategies to the entries of an archive file.
 	 */
 	private static class ZipFileProcessor {
 		private final ZipEntryHandler entryHandler;
 
 		public ZipFileProcessor(ZipEntryHandler entryHandler) {
 			this.entryHandler = entryHandler;
 		}
 
 		public void process(File file) throws Exception {
 			ZipInputStream zip = new ZipInputStream( new FileInputStream( file ) );
 
 			try {
 				ZipEntry entry;
 				while ( (entry = zip.getNextEntry()) != null ) {
 					byte bytes[] = ByteCodeHelper.readByteCode( zip );
 					entryHandler.handleEntry( entry, bytes );
 					zip.closeEntry();
 				}
             }
             finally {
 	            zip.close();
             }
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/util/BasicClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
similarity index 71%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/util/BasicClassFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
index e7ec152366..642fb3811f 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/util/BasicClassFilter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/BasicClassFilter.java
@@ -1,82 +1,72 @@
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
-package org.hibernate.bytecode.util;
-import java.util.HashSet;
-import java.util.Set;
-
-/**
- * BasicClassFilter provides class filtering based on a series of packages to
- * be included and/or a series of explicit class names to be included.  If
- * neither is specified, then no restrictions are applied.
- *
- * @author Steve Ebersole
- */
-public class BasicClassFilter implements ClassFilter {
-	private final String[] includedPackages;
-	private final Set includedClassNames = new HashSet();
-	private final boolean isAllEmpty;
-
-	public BasicClassFilter() {
-		this( null, null );
-	}
-
-	public BasicClassFilter(String[] includedPackages, String[] includedClassNames) {
-		this.includedPackages = includedPackages;
-		if ( includedClassNames != null ) {
-			for ( int i = 0; i < includedClassNames.length; i++ ) {
-				this.includedClassNames.add( includedClassNames[i] );
-			}
-		}
-
-		isAllEmpty = ( this.includedPackages == null || this.includedPackages.length == 0 )
-		             && ( this.includedClassNames.isEmpty() );
-	}
-
-	public boolean shouldInstrumentClass(String className) {
-		if ( isAllEmpty ) {
-			return true;
-		}
-		else if ( includedClassNames.contains( className ) ) {
-			return true;
-		}
-		else if ( isInIncludedPackage( className ) ) {
-			return true;
-		}
-		else {
-			return false;
-		}
-	}
-
-	private boolean isInIncludedPackage(String className) {
-		if ( includedPackages != null ) {
-			for ( int i = 0; i < includedPackages.length; i++ ) {
-				if ( className.startsWith( includedPackages[i] ) ) {
-					return true;
-				}
-			}
-		}
-		return false;
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
+package org.hibernate.bytecode.buildtime.spi;
+
+import java.util.Arrays;
+import java.util.HashSet;
+import java.util.Set;
+
+/**
+ * BasicClassFilter provides class filtering based on a series of packages to
+ * be included and/or a series of explicit class names to be included.  If
+ * neither is specified, then no restrictions are applied.
+ *
+ * @author Steve Ebersole
+ */
+public class BasicClassFilter implements ClassFilter {
+	private final String[] includedPackages;
+	private final Set<String> includedClassNames = new HashSet<String>();
+	private final boolean isAllEmpty;
+
+	public BasicClassFilter() {
+		this( null, null );
+	}
+
+	public BasicClassFilter(String[] includedPackages, String[] includedClassNames) {
+		this.includedPackages = includedPackages;
+		if ( includedClassNames != null ) {
+			this.includedClassNames.addAll( Arrays.asList( includedClassNames ) );
+		}
+
+		isAllEmpty = ( this.includedPackages == null || this.includedPackages.length == 0 )
+		             && ( this.includedClassNames.isEmpty() );
+	}
+
+	public boolean shouldInstrumentClass(String className) {
+		return isAllEmpty ||
+				includedClassNames.contains( className ) ||
+				isInIncludedPackage( className );
+	}
+
+	private boolean isInIncludedPackage(String className) {
+		if ( includedPackages != null ) {
+			for ( String includedPackage : includedPackages ) {
+				if ( className.startsWith( includedPackage ) ) {
+					return true;
+				}
+			}
+		}
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassDescriptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassDescriptor.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassDescriptor.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassDescriptor.java
index 88f0af867d..11ea3bf24e 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassDescriptor.java
@@ -1,55 +1,53 @@
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
-package org.hibernate.bytecode.util;
-
-
-/**
- * Contract describing the information Hibernate needs in terms of instrumenting
- * a class, either via ant task or dynamic classloader.
- *
- * @author Steve Ebersole
- */
-public interface ClassDescriptor {
-	/**
-	 * The name of the class.
-	 *
-	 * @return The class name.
-	 */
-	public String getName();
-
-	/**
-	 * Determine if the class is already instrumented.
-	 *
-	 * @return True if already instrumented; false otherwise.
-	 */
-	public boolean isInstrumented();
-
-	/**
-	 * The bytes making up the class' bytecode.
-	 *
-	 * @return The bytecode bytes.
-	 */
-	public byte[] getBytes();
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
+package org.hibernate.bytecode.buildtime.spi;
+
+/**
+ * Contract describing the information Hibernate needs in terms of instrumenting
+ * a class, either via ant task or dynamic classloader.
+ *
+ * @author Steve Ebersole
+ */
+public interface ClassDescriptor {
+	/**
+	 * The name of the class.
+	 *
+	 * @return The class name.
+	 */
+	public String getName();
+
+	/**
+	 * Determine if the class is already instrumented.
+	 *
+	 * @return True if already instrumented; false otherwise.
+	 */
+	public boolean isInstrumented();
+
+	/**
+	 * The bytes making up the class' bytecode.
+	 *
+	 * @return The bytecode bytes.
+	 */
+	public byte[] getBytes();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
similarity index 70%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
index be10a28d46..935e4a5912 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/util/ClassFilter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ClassFilter.java
@@ -1,35 +1,40 @@
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
-package org.hibernate.bytecode.util;
-
-
-/**
- * Used to determine whether a class should be instrumented.
- *
- * @author Steve Ebersole
- */
-public interface ClassFilter {
-		public boolean shouldInstrumentClass(String className);
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
+package org.hibernate.bytecode.buildtime.spi;
+
+/**
+ * Used to determine whether a class should be instrumented.
+ *
+ * @author Steve Ebersole
+ */
+public interface ClassFilter {
+	/**
+	 * Should this class be included in instrumentation
+	 *
+	 * @param className The name of the class to check
+	 *
+	 * @return {@literal true} to include class in instrumentation; {@literal false} otherwise.
+	 */
+	public boolean shouldInstrumentClass(String className);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/ExecutionException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/ExecutionException.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
index 45dea0b68e..0b09fe61e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/ExecutionException.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/ExecutionException.java
@@ -1,44 +1,44 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.bytecode.buildtime;
-
+package org.hibernate.bytecode.buildtime.spi;
 
 /**
  * Indicates problem performing the instrumentation execution.
  *
  * @author Steve Ebersole
  */
+@SuppressWarnings( {"UnusedDeclaration"})
 public class ExecutionException extends RuntimeException {
 	public ExecutionException(String message) {
 		super( message );
 	}
 
 	public ExecutionException(Throwable cause) {
 		super( cause );
 	}
 
 	public ExecutionException(String message, Throwable cause) {
 		super( message, cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/util/FieldFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/FieldFilter.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/util/FieldFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/FieldFilter.java
index b62f531d0d..aca390c658 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/util/FieldFilter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/FieldFilter.java
@@ -1,54 +1,52 @@
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
-package org.hibernate.bytecode.util;
-
-
-/**
- * Used to determine whether a field reference should be instrumented.
- *
- * @author Steve Ebersole
- */
-public interface FieldFilter {
-	/**
-	 * Should this field definition be instrumented?
-	 *
-	 * @param className The name of the class currently being processed
-	 * @param fieldName The name of the field being checked.
-	 * @return True if we should instrument this field.
-	 */
-	public boolean shouldInstrumentField(String className, String fieldName);
-
-	/**
-	 * Should we instrument *access to* the given field.  This differs from
-	 * {@link #shouldInstrumentField} in that here we are talking about a particular usage of
-	 * a field.
-	 *
-	 * @param transformingClassName The class currently being transformed.
-	 * @param fieldOwnerClassName The name of the class owning this field being checked.
-	 * @param fieldName The name of the field being checked.
-	 * @return True if this access should be transformed.
-	 */
-	public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName);
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
+package org.hibernate.bytecode.buildtime.spi;
+
+/**
+ * Used to determine whether a field reference should be instrumented.
+ *
+ * @author Steve Ebersole
+ */
+public interface FieldFilter {
+	/**
+	 * Should this field definition be instrumented?
+	 *
+	 * @param className The name of the class currently being processed
+	 * @param fieldName The name of the field being checked.
+	 * @return True if we should instrument this field.
+	 */
+	public boolean shouldInstrumentField(String className, String fieldName);
+
+	/**
+	 * Should we instrument *access to* the given field.  This differs from
+	 * {@link #shouldInstrumentField} in that here we are talking about a particular usage of
+	 * a field.
+	 *
+	 * @param transformingClassName The class currently being transformed.
+	 * @param fieldOwnerClassName The name of the class owning this field being checked.
+	 * @param fieldName The name of the field being checked.
+	 * @return True if this access should be transformed.
+	 */
+	public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Instrumenter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
similarity index 66%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Instrumenter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
index b89107f6ce..3a281b91f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Instrumenter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Instrumenter.java
@@ -1,38 +1,53 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.bytecode.buildtime;
+package org.hibernate.bytecode.buildtime.spi;
+
+import java.io.File;
 import java.util.Set;
 
 /**
- * TODO : javadoc
+ * Basic contract for performing instrumentation
  *
  * @author Steve Ebersole
  */
 public interface Instrumenter {
-	public void execute(Set files);
+	/**
+	 * Perform the instrumentation
+	 *
+	 * @param files The file on which to perform instrumentation
+	 */
+	public void execute(Set<File> files);
 
+	/**
+	 * Instrumentation options
+	 */
 	public static interface Options {
+		/**
+		 * Should we enhance references to class fields outside the class itself?
+		 *
+		 * @return {@literal true}/{@literal false}
+		 */
 		public boolean performExtendedInstrumentation();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Logger.java b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Logger.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
index f2581394af..7a489687cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/Logger.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/buildtime/spi/Logger.java
@@ -1,42 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.bytecode.buildtime;
+package org.hibernate.bytecode.buildtime.spi;
 
 /**
  * Provides an abstraction for how instrumentation does logging because it is usually run in environments (Ant/Maven)
  * with their own logging infrastructure.  This abstraction allows proper bridging.
  *
  * @author Steve Ebersole
  */
 public interface Logger {
 	public void trace(String message);
 
 	public void debug(String message);
 
 	public void info(String message);
 
 	public void warn(String message);
 
 	public void error(String message);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
new file mode 100644
index 0000000000..c1dfa8fee5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/FieldInterceptionHelper.java
@@ -0,0 +1,154 @@
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
+package org.hibernate.bytecode.instrumentation.internal;
+
+import java.util.HashSet;
+import java.util.Set;
+
+import org.hibernate.bytecode.instrumentation.internal.javassist.JavassistHelper;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.engine.SessionImplementor;
+
+/**
+ * Helper class for dealing with enhanced entity classes.
+ *
+ * @author Steve Ebersole
+ */
+public class FieldInterceptionHelper {
+	private static final Set<Delegate> INSTRUMENTATION_DELEGATES = buildInstrumentationDelegates();
+
+	private static Set<Delegate> buildInstrumentationDelegates() {
+		HashSet<Delegate> delegates = new HashSet<Delegate>();
+		delegates.add( JavassistDelegate.INSTANCE );
+		return delegates;
+	}
+
+	private FieldInterceptionHelper() {
+	}
+
+	public static boolean isInstrumented(Class entityClass) {
+		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
+			if ( delegate.isInstrumented( entityClass ) ) {
+				return true;
+			}
+		}
+		return false;
+	}
+
+	public static boolean isInstrumented(Object entity) {
+		return entity != null && isInstrumented( entity.getClass() );
+	}
+
+	public static FieldInterceptor extractFieldInterceptor(Object entity) {
+		if ( entity == null ) {
+			return null;
+		}
+		FieldInterceptor interceptor = null;
+		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
+			interceptor = delegate.extractInterceptor( entity );
+			if ( interceptor != null ) {
+				break;
+			}
+		}
+		return interceptor;
+	}
+
+
+	public static FieldInterceptor injectFieldInterceptor(
+			Object entity,
+	        String entityName,
+	        Set uninitializedFieldNames,
+	        SessionImplementor session) {
+		if ( entity == null ) {
+			return null;
+		}
+		FieldInterceptor interceptor = null;
+		for ( Delegate delegate : INSTRUMENTATION_DELEGATES ) {
+			interceptor = delegate.injectInterceptor( entity, entityName, uninitializedFieldNames, session );
+			if ( interceptor != null ) {
+				break;
+			}
+		}
+		return interceptor;
+	}
+
+	public static void clearDirty(Object entity) {
+		FieldInterceptor interceptor = extractFieldInterceptor( entity );
+		if ( interceptor != null ) {
+			interceptor.clearDirty();
+		}
+	}
+
+	public static void markDirty(Object entity) {
+		FieldInterceptor interceptor = extractFieldInterceptor( entity );
+		if ( interceptor != null ) {
+			interceptor.dirty();
+		}
+	}
+
+	private static interface Delegate {
+		public boolean isInstrumented(Class classToCheck);
+		public FieldInterceptor extractInterceptor(Object entity);
+		public FieldInterceptor injectInterceptor(Object entity, String entityName, Set uninitializedFieldNames, SessionImplementor session);
+	}
+
+	private static class JavassistDelegate implements Delegate {
+		public static final JavassistDelegate INSTANCE = new JavassistDelegate();
+		public static final String MARKER = "org.hibernate.bytecode.internal.javassist.FieldHandled";
+
+		@Override
+		public boolean isInstrumented(Class classToCheck) {
+			for ( Class definedInterface : classToCheck.getInterfaces() ) {
+				if ( MARKER.equals( definedInterface.getName() ) ) {
+					return true;
+				}
+			}
+			return false;
+		}
+
+		@Override
+		public FieldInterceptor extractInterceptor(Object entity) {
+			for ( Class definedInterface : entity.getClass().getInterfaces() ) {
+				if ( MARKER.equals( definedInterface.getName() ) ) {
+					return JavassistHelper.extractFieldInterceptor( entity );
+				}
+			}
+			return null;
+		}
+
+		@Override
+		public FieldInterceptor injectInterceptor(
+				Object entity,
+				String entityName,
+				Set uninitializedFieldNames,
+				SessionImplementor session) {
+			for ( Class definedInterface : entity.getClass().getInterfaces() ) {
+				if ( MARKER.equals( definedInterface.getName() ) ) {
+					return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
+				}
+			}
+			return null;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/javassist/FieldInterceptorImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
similarity index 77%
rename from hibernate-core/src/main/java/org/hibernate/intercept/javassist/FieldInterceptorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
index ba775ef4bf..fc396886c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/intercept/javassist/FieldInterceptorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
@@ -1,175 +1,170 @@
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
-package org.hibernate.intercept.javassist;
+package org.hibernate.bytecode.instrumentation.internal.javassist;
+
 import java.io.Serializable;
 import java.util.Set;
-import org.hibernate.bytecode.javassist.FieldHandler;
+
+import org.hibernate.bytecode.instrumentation.spi.AbstractFieldInterceptor;
+import org.hibernate.bytecode.internal.javassist.FieldHandler;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.AbstractFieldInterceptor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * A field-level interceptor that initializes lazily fetched properties.
  * This interceptor can be attached to classes instrumented by Javassist.
  * Note that this implementation assumes that the instance variable
  * name is the same as the name of the persistent property that must
  * be loaded.
  * </p>
  * Note: most of the interesting functionality here is farmed off
  * to the super-class.  The stuff here mainly acts as an adapter to the
  * Javassist-specific functionality, routing interception through
  * the super-class's intercept() method
  *
  * @author Steve Ebersole
  */
+@SuppressWarnings( {"UnnecessaryUnboxing", "UnnecessaryBoxing"})
 public final class FieldInterceptorImpl extends AbstractFieldInterceptor implements FieldHandler, Serializable {
 
-	/**
-	 * Package-protected constructor.
-	 *
-	 * @param session
-	 * @param uninitializedFields
-	 * @param entityName
-	 */
 	FieldInterceptorImpl(SessionImplementor session, Set uninitializedFields, String entityName) {
 		super( session, uninitializedFields, entityName );
 	}
 
 
 	// FieldHandler impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean readBoolean(Object target, String name, boolean oldValue) {
 		return ( ( Boolean ) intercept( target, name, oldValue  ? Boolean.TRUE : Boolean.FALSE ) )
 				.booleanValue();
 	}
 
 	public byte readByte(Object target, String name, byte oldValue) {
-		return ( ( Byte ) intercept( target, name, new Byte( oldValue ) ) ).byteValue();
+		return ( ( Byte ) intercept( target, name, Byte.valueOf( oldValue ) ) ).byteValue();
 	}
 
 	public char readChar(Object target, String name, char oldValue) {
-		return ( ( Character ) intercept( target, name, new Character( oldValue ) ) )
+		return ( ( Character ) intercept( target, name, Character.valueOf( oldValue ) ) )
 				.charValue();
 	}
 
 	public double readDouble(Object target, String name, double oldValue) {
-		return ( ( Double ) intercept( target, name, new Double( oldValue ) ) )
+		return ( ( Double ) intercept( target, name, Double.valueOf( oldValue ) ) )
 				.doubleValue();
 	}
 
 	public float readFloat(Object target, String name, float oldValue) {
-		return ( ( Float ) intercept( target, name, new Float( oldValue ) ) )
+		return ( ( Float ) intercept( target, name, Float.valueOf( oldValue ) ) )
 				.floatValue();
 	}
 
 	public int readInt(Object target, String name, int oldValue) {
-		return ( ( Integer ) intercept( target, name, new Integer( oldValue ) ) )
+		return ( ( Integer ) intercept( target, name, Integer.valueOf( oldValue ) ) )
 				.intValue();
 	}
 
 	public long readLong(Object target, String name, long oldValue) {
-		return ( ( Long ) intercept( target, name, new Long( oldValue ) ) ).longValue();
+		return ( ( Long ) intercept( target, name, Long.valueOf( oldValue ) ) ).longValue();
 	}
 
 	public short readShort(Object target, String name, short oldValue) {
-		return ( ( Short ) intercept( target, name, new Short( oldValue ) ) )
+		return ( ( Short ) intercept( target, name, Short.valueOf( oldValue ) ) )
 				.shortValue();
 	}
 
 	public Object readObject(Object target, String name, Object oldValue) {
 		Object value = intercept( target, name, oldValue );
 		if (value instanceof HibernateProxy) {
 			LazyInitializer li = ( (HibernateProxy) value ).getHibernateLazyInitializer();
 			if ( li.isUnwrap() ) {
 				value = li.getImplementation();
 			}
 		}
 		return value;
 	}
 
 	public boolean writeBoolean(Object target, String name, boolean oldValue, boolean newValue) {
 		dirty();
 		intercept( target, name, oldValue ? Boolean.TRUE : Boolean.FALSE );
 		return newValue;
 	}
 
 	public byte writeByte(Object target, String name, byte oldValue, byte newValue) {
 		dirty();
-		intercept( target, name, new Byte( oldValue ) );
+		intercept( target, name, Byte.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public char writeChar(Object target, String name, char oldValue, char newValue) {
 		dirty();
-		intercept( target, name, new Character( oldValue ) );
+		intercept( target, name, Character.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public double writeDouble(Object target, String name, double oldValue, double newValue) {
 		dirty();
-		intercept( target, name, new Double( oldValue ) );
+		intercept( target, name, Double.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public float writeFloat(Object target, String name, float oldValue, float newValue) {
 		dirty();
-		intercept( target, name, new Float( oldValue ) );
+		intercept( target, name, Float.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public int writeInt(Object target, String name, int oldValue, int newValue) {
 		dirty();
-		intercept( target, name, new Integer( oldValue ) );
+		intercept( target, name, Integer.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public long writeLong(Object target, String name, long oldValue, long newValue) {
 		dirty();
-		intercept( target, name, new Long( oldValue ) );
+		intercept( target, name, Long.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public short writeShort(Object target, String name, short oldValue, short newValue) {
 		dirty();
-		intercept( target, name, new Short( oldValue ) );
+		intercept( target, name, Short.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public Object writeObject(Object target, String name, Object oldValue, Object newValue) {
 		dirty();
 		intercept( target, name, oldValue );
 		return newValue;
 	}
 
 	public String toString() {
 		return "FieldInterceptorImpl(" +
 		       "entityName=" + getEntityName() +
 		       ",dirty=" + isDirty() +
 		       ",uninitializedFields=" + getUninitializedFields() +
 		       ')';
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/javassist/JavassistHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
similarity index 83%
rename from hibernate-core/src/main/java/org/hibernate/intercept/javassist/JavassistHelper.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
index 205dba1660..ad76788178 100644
--- a/hibernate-core/src/main/java/org/hibernate/intercept/javassist/JavassistHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/JavassistHelper.java
@@ -1,51 +1,52 @@
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
-package org.hibernate.intercept.javassist;
+package org.hibernate.bytecode.instrumentation.internal.javassist;
+
 import java.util.Set;
-import org.hibernate.bytecode.javassist.FieldHandled;
+
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.bytecode.internal.javassist.FieldHandled;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.FieldInterceptor;
 
 /**
  * @author Steve Ebersole
  */
 public class JavassistHelper {
 	private JavassistHelper() {
 	}
 
 	public static FieldInterceptor extractFieldInterceptor(Object entity) {
 		return ( FieldInterceptor ) ( ( FieldHandled ) entity ).getFieldHandler();
 	}
 
 	public static FieldInterceptor injectFieldInterceptor(
 			Object entity,
 	        String entityName,
 	        Set uninitializedFieldNames,
 	        SessionImplementor session) {
 		FieldInterceptorImpl fieldInterceptor = new FieldInterceptorImpl( session, uninitializedFieldNames, entityName );
 		( ( FieldHandled ) entity ).setFieldHandler( fieldInterceptor );
 		return fieldInterceptor;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/package.html b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/package.html
similarity index 79%
rename from hibernate-core/src/main/java/org/hibernate/intercept/package.html
rename to hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/package.html
index cacbcfa895..1c9a616459 100755
--- a/hibernate-core/src/main/java/org/hibernate/intercept/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/package.html
@@ -1,35 +1,32 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
-  ~
   -->
 
 <html>
 <head></head>
 <body>
 <p>
-	This package implements an interception
-	mechanism for lazy property fetching,
-	based on CGLIB bytecode instrumentation.
+	This package implements an interception mechanism for lazy property fetching, based on bytecode instrumentation.
 </p>
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/AbstractFieldInterceptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/intercept/AbstractFieldInterceptor.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
index d44286aa19..800b1b98ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/intercept/AbstractFieldInterceptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/AbstractFieldInterceptor.java
@@ -1,125 +1,124 @@
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
-package org.hibernate.intercept;
+package org.hibernate.bytecode.instrumentation.spi;
 import java.io.Serializable;
 import java.util.Set;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.engine.SessionImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractFieldInterceptor implements FieldInterceptor, Serializable {
 
 	private transient SessionImplementor session;
 	private Set uninitializedFields;
 	private final String entityName;
 
 	private transient boolean initializing;
 	private boolean dirty;
 
 	protected AbstractFieldInterceptor(SessionImplementor session, Set uninitializedFields, String entityName) {
 		this.session = session;
 		this.uninitializedFields = uninitializedFields;
 		this.entityName = entityName;
 	}
 
 
 	// FieldInterceptor impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final void setSession(SessionImplementor session) {
 		this.session = session;
 	}
 
 	public final boolean isInitialized() {
 		return uninitializedFields == null || uninitializedFields.size() == 0;
 	}
 
 	public final boolean isInitialized(String field) {
 		return uninitializedFields == null || !uninitializedFields.contains( field );
 	}
 
 	public final void dirty() {
 		dirty = true;
 	}
 
 	public final boolean isDirty() {
 		return dirty;
 	}
 
 	public final void clearDirty() {
 		dirty = false;
 	}
 
 
 	// subclass accesses ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected final Object intercept(Object target, String fieldName, Object value) {
 		if ( initializing ) {
 			return value;
 		}
 
 		if ( uninitializedFields != null && uninitializedFields.contains( fieldName ) ) {
 			if ( session == null ) {
 				throw new LazyInitializationException( "entity with lazy properties is not associated with a session" );
 			}
 			else if ( !session.isOpen() || !session.isConnected() ) {
 				throw new LazyInitializationException( "session is not connected" );
 			}
 
 			final Object result;
 			initializing = true;
 			try {
 				result = ( ( LazyPropertyInitializer ) session.getFactory()
 						.getEntityPersister( entityName ) )
 						.initializeLazyProperty( fieldName, target, session );
 			}
 			finally {
 				initializing = false;
 			}
 			uninitializedFields = null; //let's assume that there is only one lazy fetch group, for now!
 			return result;
 		}
 		else {
 			return value;
 		}
 	}
 
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
 	public final Set getUninitializedFields() {
 		return uninitializedFields;
 	}
 
 	public final String getEntityName() {
 		return entityName;
 	}
 
 	public final boolean isInitializing() {
 		return initializing;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptor.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java
index 4913d37ba2..c36508bd46 100755
--- a/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/FieldInterceptor.java
@@ -1,73 +1,73 @@
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
-package org.hibernate.intercept;
+package org.hibernate.bytecode.instrumentation.spi;
+
 import org.hibernate.engine.SessionImplementor;
 
 /**
  * Contract for field interception handlers.
  *
  * @author Steve Ebersole
  */
 public interface FieldInterceptor {
 
 	/**
 	 * Use to associate the entity to which we are bound to the given session.
 	 *
 	 * @param session The session to which we are now associated.
 	 */
 	public void setSession(SessionImplementor session);
 
 	/**
 	 * Is the entity to which we are bound completely initialized?
 	 *
 	 * @return True if the entity is initialized; otherwise false.
 	 */
 	public boolean isInitialized();
 
 	/**
 	 * The the given field initialized for the entity to which we are bound?
 	 *
 	 * @param field The name of the field to check
 	 * @return True if the given field is initialized; otherwise false.
 	 */
 	public boolean isInitialized(String field);
 
 	/**
 	 * Forcefully mark the entity as being dirty.
 	 */
 	public void dirty();
 
 	/**
 	 * Is the entity considered dirty?
 	 *
 	 * @return True if the entity is dirty; otherwise false.
 	 */
 	public boolean isDirty();
 
 	/**
 	 * Clear the internal dirty flag.
 	 */
 	public void clearDirty();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/LazyPropertyInitializer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
similarity index 78%
rename from hibernate-core/src/main/java/org/hibernate/intercept/LazyPropertyInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
index 9a0d8c53ac..bae1f6ffdf 100755
--- a/hibernate-core/src/main/java/org/hibernate/intercept/LazyPropertyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/spi/LazyPropertyInitializer.java
@@ -1,55 +1,60 @@
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
-package org.hibernate.intercept;
+package org.hibernate.bytecode.instrumentation.spi;
+
 import java.io.Serializable;
-import org.hibernate.HibernateException;
+
 import org.hibernate.engine.SessionImplementor;
 
 /**
  * Contract for controlling how lazy properties get initialized.
  * 
  * @author Gavin King
  */
 public interface LazyPropertyInitializer {
 
 	/**
 	 * Marker value for uninitialized properties
 	 */
 	public static final Serializable UNFETCHED_PROPERTY = new Serializable() {
 		public String toString() {
 			return "<lazy>";
 		}
 		public Object readResolve() {
 			return UNFETCHED_PROPERTY;
 		}
 	};
 
 	/**
 	 * Initialize the property, and return its new value
+	 *
+	 * @param fieldName The name of the field being initialized
+	 * @param entity The entity on which the initialization is occurring
+	 * @param session The session from which the initialization originated.
+	 *
+	 * @return ?
 	 */
-	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
-	throws HibernateException;
+	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/AccessOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/AccessOptimizerAdapter.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/AccessOptimizerAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/AccessOptimizerAdapter.java
index 4e1fceeed8..318994b84f 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/AccessOptimizerAdapter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/AccessOptimizerAdapter.java
@@ -1,103 +1,104 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
+
 import java.io.Serializable;
+
 import org.hibernate.PropertyAccessException;
-import org.hibernate.bytecode.ReflectionOptimizer;
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
 
 /**
  * The {@link ReflectionOptimizer.AccessOptimizer} implementation for Javassist
- * which simply acts as an adpater to the {@link BulkAccessor} class.
+ * which simply acts as an adapter to the {@link BulkAccessor} class.
  *
  * @author Steve Ebersole
  */
 public class AccessOptimizerAdapter implements ReflectionOptimizer.AccessOptimizer, Serializable {
 
 	public static final String PROPERTY_GET_EXCEPTION =
 		"exception getting property value with Javassist (set hibernate.bytecode.use_reflection_optimizer=false for more info)";
 
 	public static final String PROPERTY_SET_EXCEPTION =
 		"exception setting property value with Javassist (set hibernate.bytecode.use_reflection_optimizer=false for more info)";
 
 	private final BulkAccessor bulkAccessor;
 	private final Class mappedClass;
 
 	public AccessOptimizerAdapter(BulkAccessor bulkAccessor, Class mappedClass) {
 		this.bulkAccessor = bulkAccessor;
 		this.mappedClass = mappedClass;
 	}
 
 	public String[] getPropertyNames() {
 		return bulkAccessor.getGetters();
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		try {
 			return bulkAccessor.getPropertyValues( object );
 		}
 		catch ( Throwable t ) {
 			throw new PropertyAccessException(
 					t,
 			        PROPERTY_GET_EXCEPTION,
 			        false,
 			        mappedClass,
 			        getterName( t, bulkAccessor )
 				);
 		}
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		try {
 			bulkAccessor.setPropertyValues( object, values );
 		}
 		catch ( Throwable t ) {
 			throw new PropertyAccessException(
 					t,
 			        PROPERTY_SET_EXCEPTION,
 			        true,
 			        mappedClass,
 			        setterName( t, bulkAccessor )
 			);
 		}
 	}
 
 	private static String setterName(Throwable t, BulkAccessor accessor) {
 		if (t instanceof BulkAccessorException ) {
 			return accessor.getSetters()[ ( (BulkAccessorException) t ).getIndex() ];
 		}
 		else {
 			return "?";
 		}
 	}
 
 	private static String getterName(Throwable t, BulkAccessor accessor) {
 		if (t instanceof BulkAccessorException ) {
 			return accessor.getGetters()[ ( (BulkAccessorException) t ).getIndex() ];
 		}
 		else {
 			return "?";
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessor.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessor.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessor.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessor.java
index 49538e8c87..d849c04404 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessor.java
@@ -1,115 +1,114 @@
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
-package org.hibernate.bytecode.javassist;
-import java.io.Serializable;
+package org.hibernate.bytecode.internal.javassist;
 
+import java.io.Serializable;
 
 /**
  * A JavaBean accessor.
  * <p/>
  * <p>This object provides methods that set/get multiple properties
  * of a JavaBean at once.  This class and its support classes have been
  * developed for the comaptibility with cglib
  * (<tt>http://cglib.sourceforge.net/</tt>).
  *
  * @author Muga Nishizawa
  * @author modified by Shigeru Chiba
  */
 public abstract class BulkAccessor implements Serializable {
 	protected Class target;
 	protected String[] getters, setters;
 	protected Class[] types;
 
 	protected BulkAccessor() {
 	}
 
 	/**
 	 * Obtains the values of properties of a given bean.
 	 *
 	 * @param bean   JavaBean.
 	 * @param values the obtained values are stored in this array.
 	 */
 	public abstract void getPropertyValues(Object bean, Object[] values);
 
 	/**
 	 * Sets properties of a given bean to specified values.
 	 *
 	 * @param bean   JavaBean.
 	 * @param values the values assinged to properties.
 	 */
 	public abstract void setPropertyValues(Object bean, Object[] values);
 
 	/**
 	 * Returns the values of properties of a given bean.
 	 *
 	 * @param bean JavaBean.
 	 */
 	public Object[] getPropertyValues(Object bean) {
 		Object[] values = new Object[getters.length];
 		getPropertyValues( bean, values );
 		return values;
 	}
 
 	/**
 	 * Returns the types of properties.
 	 */
 	public Class[] getPropertyTypes() {
 		return ( Class[] ) types.clone();
 	}
 
 	/**
 	 * Returns the setter names of properties.
 	 */
 	public String[] getGetters() {
 		return ( String[] ) getters.clone();
 	}
 
 	/**
 	 * Returns the getter names of the properties.
 	 */
 	public String[] getSetters() {
 		return ( String[] ) setters.clone();
 	}
 
 	/**
 	 * Creates a new instance of <code>BulkAccessor</code>.
 	 * The created instance provides methods for setting/getting
 	 * specified properties at once.
 	 *
 	 * @param beanClass the class of the JavaBeans accessed
 	 *                  through the created object.
 	 * @param getters   the names of setter methods for specified properties.
 	 * @param setters   the names of getter methods for specified properties.
 	 * @param types     the types of specified properties.
 	 */
 	public static BulkAccessor create(
 			Class beanClass,
 	        String[] getters,
 	        String[] setters,
 	        Class[] types) {
 		BulkAccessorFactory factory = new BulkAccessorFactory( beanClass, getters, setters, types );
 		return factory.create();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorException.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorException.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorException.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorException.java
index 2d52d27b72..0b2a23f29e 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorException.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorException.java
@@ -1,103 +1,101 @@
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
-package org.hibernate.bytecode.javassist;
-
+package org.hibernate.bytecode.internal.javassist;
 
 /**
  * An exception thrown while generating a bulk accessor.
  * 
  * @author Muga Nishizawa
  * @author modified by Shigeru Chiba
  */
 public class BulkAccessorException extends RuntimeException {
     private Throwable myCause;
 
     /**
      * Gets the cause of this throwable.
      * It is for JDK 1.3 compatibility.
      */
     public Throwable getCause() {
         return (myCause == this ? null : myCause);
     }
 
     /**
      * Initializes the cause of this throwable.
      * It is for JDK 1.3 compatibility.
      */
     public synchronized Throwable initCause(Throwable cause) {
         myCause = cause;
         return this;
     }
 
     private int index;
 
     /**
      * Constructs an exception.
      */
     public BulkAccessorException(String message) {
         super(message);
         index = -1;
         initCause(null);
     }
 
     /**
      * Constructs an exception.
      *
      * @param index     the index of the property that causes an exception.
      */
     public BulkAccessorException(String message, int index) {
         this(message + ": " + index);
         this.index = index;
     }
 
     /**
      * Constructs an exception.
      */
     public BulkAccessorException(String message, Throwable cause) {
         super(message);
         index = -1;
         initCause(cause);
     }
 
     /**
      * Constructs an exception.
      *
      * @param index     the index of the property that causes an exception.
      */
     public BulkAccessorException(Throwable cause, int index) {
         this("Property " + index);
         this.index = index;
         initCause(cause);
     }
 
     /**
      * Returns the index of the property that causes this exception.
      *
      * @return -1 if the index is not specified.
      */
     public int getIndex() {
         return this.index;
     }
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorFactory.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorFactory.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorFactory.java
index add1fee09b..6e4fa62465 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BulkAccessorFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BulkAccessorFactory.java
@@ -1,410 +1,411 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
+
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.security.ProtectionDomain;
+
 import javassist.CannotCompileException;
 import javassist.bytecode.AccessFlag;
 import javassist.bytecode.Bytecode;
 import javassist.bytecode.ClassFile;
 import javassist.bytecode.ConstPool;
 import javassist.bytecode.MethodInfo;
 import javassist.bytecode.Opcode;
 import javassist.util.proxy.FactoryHelper;
 import javassist.util.proxy.RuntimeSupport;
 
 /**
  * A factory of bulk accessors.
  *
  * @author Muga Nishizawa
  * @author modified by Shigeru Chiba
  */
 class BulkAccessorFactory {
 	private static final String PACKAGE_NAME_PREFIX = "org.javassist.tmp.";
 	private static final String BULKACESSOR_CLASS_NAME = BulkAccessor.class.getName();
 	private static final String OBJECT_CLASS_NAME = Object.class.getName();
 	private static final String GENERATED_GETTER_NAME = "getPropertyValues";
 	private static final String GENERATED_SETTER_NAME = "setPropertyValues";
 	private static final String GET_SETTER_DESC = "(Ljava/lang/Object;[Ljava/lang/Object;)V";
 	private static final String THROWABLE_CLASS_NAME = Throwable.class.getName();
 	private static final String BULKEXCEPTION_CLASS_NAME = BulkAccessorException.class.getName();
 	private static int counter = 0;
 
 	private Class targetBean;
 	private String[] getterNames;
 	private String[] setterNames;
 	private Class[] types;
 	public String writeDirectory;
 
 	BulkAccessorFactory(
 			Class target,
 	        String[] getterNames,
 	        String[] setterNames,
 	        Class[] types) {
 		this.targetBean = target;
 		this.getterNames = getterNames;
 		this.setterNames = setterNames;
 		this.types = types;
 		this.writeDirectory = null;
 	}
 
 	BulkAccessor create() {
 		Method[] getters = new Method[getterNames.length];
 		Method[] setters = new Method[setterNames.length];
 		findAccessors( targetBean, getterNames, setterNames, types, getters, setters );
 
 		Class beanClass;
 		try {
 			ClassFile classfile = make( getters, setters );
 			ClassLoader loader = this.getClassLoader();
 			if ( writeDirectory != null ) {
 				FactoryHelper.writeFile( classfile, writeDirectory );
 			}
 
 			beanClass = FactoryHelper.toClass( classfile, loader, getDomain() );
 			return ( BulkAccessor ) this.newInstance( beanClass );
 		}
 		catch ( Exception e ) {
 			throw new BulkAccessorException( e.getMessage(), e );
 		}
 	}
 
 	private ProtectionDomain getDomain() {
 		Class cl;
 		if ( this.targetBean != null ) {
 			cl = this.targetBean;
 		}
 		else {
 			cl = this.getClass();
 		}
 		return cl.getProtectionDomain();
 	}
 
 	private ClassFile make(Method[] getters, Method[] setters) throws CannotCompileException {
 		String className = targetBean.getName();
 		// set the name of bulk accessor.
 		className = className + "_$$_bulkaccess_" + counter++;
 		if ( className.startsWith( "java." ) ) {
 			className = "org.javassist.tmp." + className;
 		}
 
 		ClassFile classfile = new ClassFile( false, className, BULKACESSOR_CLASS_NAME );
 		classfile.setAccessFlags( AccessFlag.PUBLIC );
 		addDefaultConstructor( classfile );
 		addGetter( classfile, getters );
 		addSetter( classfile, setters );
 		return classfile;
 	}
 
 	private ClassLoader getClassLoader() {
 		if ( targetBean != null && targetBean.getName().equals( OBJECT_CLASS_NAME ) ) {
 			return targetBean.getClassLoader();
 		}
 		else {
 			return getClass().getClassLoader();
 		}
 	}
 
 	private Object newInstance(Class type) throws Exception {
 		BulkAccessor instance = ( BulkAccessor ) type.newInstance();
 		instance.target = targetBean;
 		int len = getterNames.length;
 		instance.getters = new String[len];
 		instance.setters = new String[len];
 		instance.types = new Class[len];
 		for ( int i = 0; i < len; i++ ) {
 			instance.getters[i] = getterNames[i];
 			instance.setters[i] = setterNames[i];
 			instance.types[i] = types[i];
 		}
 
 		return instance;
 	}
 
 	/**
 	 * Declares a constructor that takes no parameter.
 	 *
 	 * @param classfile
 	 * @throws CannotCompileException
 	 */
 	private void addDefaultConstructor(ClassFile classfile) throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		String cons_desc = "()V";
 		MethodInfo mi = new MethodInfo( cp, MethodInfo.nameInit, cons_desc );
 
 		Bytecode code = new Bytecode( cp, 0, 1 );
 		// aload_0
 		code.addAload( 0 );
 		// invokespecial
 		code.addInvokespecial( BulkAccessor.class.getName(), MethodInfo.nameInit, cons_desc );
 		// return
 		code.addOpcode( Opcode.RETURN );
 
 		mi.setCodeAttribute( code.toCodeAttribute() );
 		mi.setAccessFlags( AccessFlag.PUBLIC );
 		classfile.addMethod( mi );
 	}
 
 	private void addGetter(ClassFile classfile, final Method[] getters) throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		int target_type_index = cp.addClassInfo( this.targetBean.getName() );
 		String desc = GET_SETTER_DESC;
 		MethodInfo mi = new MethodInfo( cp, GENERATED_GETTER_NAME, desc );
 
 		Bytecode code = new Bytecode( cp, 6, 4 );
 		/* | this | bean | args | raw bean | */
 		if ( getters.length >= 0 ) {
 			// aload_1 // load bean
 			code.addAload( 1 );
 			// checkcast // cast bean
 			code.addCheckcast( this.targetBean.getName() );
 			// astore_3 // store bean
 			code.addAstore( 3 );
 			for ( int i = 0; i < getters.length; ++i ) {
 				if ( getters[i] != null ) {
 					Method getter = getters[i];
 					// aload_2 // args
 					code.addAload( 2 );
 					// iconst_i // continue to aastore
 					code.addIconst( i ); // growing stack is 1
 					Class returnType = getter.getReturnType();
 					int typeIndex = -1;
 					if ( returnType.isPrimitive() ) {
 						typeIndex = FactoryHelper.typeIndex( returnType );
 						// new
 						code.addNew( FactoryHelper.wrapperTypes[typeIndex] );
 						// dup
 						code.addOpcode( Opcode.DUP );
 					}
 
 					// aload_3 // load the raw bean
 					code.addAload( 3 );
 					String getter_desc = RuntimeSupport.makeDescriptor( getter );
 					String getterName = getter.getName();
 					if ( this.targetBean.isInterface() ) {
 						// invokeinterface
 						code.addInvokeinterface( target_type_index, getterName, getter_desc, 1 );
 					}
 					else {
 						// invokevirtual
 						code.addInvokevirtual( target_type_index, getterName, getter_desc );
 					}
 
 					if ( typeIndex >= 0 ) {       // is a primitive type
 						// invokespecial
 						code.addInvokespecial(
 								FactoryHelper.wrapperTypes[typeIndex],
 						        MethodInfo.nameInit,
 						        FactoryHelper.wrapperDesc[typeIndex]
 						);
 					}
 
 					// aastore // args
 					code.add( Opcode.AASTORE );
 					code.growStack( -3 );
 				}
 			}
 		}
 		// return
 		code.addOpcode( Opcode.RETURN );
 
 		mi.setCodeAttribute( code.toCodeAttribute() );
 		mi.setAccessFlags( AccessFlag.PUBLIC );
 		classfile.addMethod( mi );
 	}
 
 	private void addSetter(ClassFile classfile, final Method[] setters) throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		int target_type_index = cp.addClassInfo( this.targetBean.getName() );
 		String desc = GET_SETTER_DESC;
 		MethodInfo mi = new MethodInfo( cp, GENERATED_SETTER_NAME, desc );
 
 		Bytecode code = new Bytecode( cp, 4, 6 );
 		/* | this | bean | args | i | raw bean | exception | */
 		if ( setters.length > 0 ) {
 			int start, end; // required to exception table
 			// iconst_0 // i
 			code.addIconst( 0 );
 			// istore_3 // store i
 			code.addIstore( 3 );
 			// aload_1 // load the bean
 			code.addAload( 1 );
 			// checkcast // cast the bean into a raw bean
 			code.addCheckcast( this.targetBean.getName() );
 			// astore 4 // store the raw bean
 			code.addAstore( 4 );
 			/* current stack len = 0 */
 			// start region to handling exception (BulkAccessorException)
 			start = code.currentPc();
 			int lastIndex = 0;
 			for ( int i = 0; i < setters.length; ++i ) {
 				if ( setters[i] != null ) {
 					int diff = i - lastIndex;
 					if ( diff > 0 ) {
 						// iinc 3, 1
 						code.addOpcode( Opcode.IINC );
 						code.add( 3 );
 						code.add( diff );
 						lastIndex = i;
 					}
 				}
 				/* current stack len = 0 */
 				// aload 4 // load the raw bean
 				code.addAload( 4 );
 				// aload_2 // load the args
 				code.addAload( 2 );
 				// iconst_i
 				code.addIconst( i );
 				// aaload
 				code.addOpcode( Opcode.AALOAD );
 				// checkcast
 				Class[] setterParamTypes = setters[i].getParameterTypes();
 				Class setterParamType = setterParamTypes[0];
 				if ( setterParamType.isPrimitive() ) {
 					// checkcast (case of primitive type)
 					// invokevirtual (case of primitive type)
 					this.addUnwrapper( classfile, code, setterParamType );
 				}
 				else {
 					// checkcast (case of reference type)
 					code.addCheckcast( setterParamType.getName() );
 				}
 				/* current stack len = 2 */
 				String rawSetterMethod_desc = RuntimeSupport.makeDescriptor( setters[i] );
 				if ( !this.targetBean.isInterface() ) {
 					// invokevirtual
 					code.addInvokevirtual( target_type_index, setters[i].getName(), rawSetterMethod_desc );
 				}
 				else {
 					// invokeinterface
 					Class[] params = setters[i].getParameterTypes();
 					int size;
 					if ( params[0].equals( Double.TYPE ) || params[0].equals( Long.TYPE ) ) {
 						size = 3;
 					}
 					else {
 						size = 2;
 					}
 
 					code.addInvokeinterface( target_type_index, setters[i].getName(), rawSetterMethod_desc, size );
 				}
 			}
 
 			// end region to handling exception (BulkAccessorException)
 			end = code.currentPc();
 			// return
 			code.addOpcode( Opcode.RETURN );
 			/* current stack len = 0 */
 			// register in exception table
 			int throwableType_index = cp.addClassInfo( THROWABLE_CLASS_NAME );
 			code.addExceptionHandler( start, end, code.currentPc(), throwableType_index );
 			// astore 5 // store exception
 			code.addAstore( 5 );
 			// new // BulkAccessorException
 			code.addNew( BULKEXCEPTION_CLASS_NAME );
 			// dup
 			code.addOpcode( Opcode.DUP );
 			// aload 5 // load exception
 			code.addAload( 5 );
 			// iload_3 // i
 			code.addIload( 3 );
 			// invokespecial // BulkAccessorException.<init>
 			String cons_desc = "(Ljava/lang/Throwable;I)V";
 			code.addInvokespecial( BULKEXCEPTION_CLASS_NAME, MethodInfo.nameInit, cons_desc );
 			// athrow
 			code.addOpcode( Opcode.ATHROW );
 		}
 		else {
 			// return
 			code.addOpcode( Opcode.RETURN );
 		}
 
 		mi.setCodeAttribute( code.toCodeAttribute() );
 		mi.setAccessFlags( AccessFlag.PUBLIC );
 		classfile.addMethod( mi );
 	}
 
 	private void addUnwrapper(
 			ClassFile classfile,
 	        Bytecode code,
 	        Class type) {
 		int index = FactoryHelper.typeIndex( type );
 		String wrapperType = FactoryHelper.wrapperTypes[index];
 		// checkcast
 		code.addCheckcast( wrapperType );
 		// invokevirtual
 		code.addInvokevirtual( wrapperType, FactoryHelper.unwarpMethods[index], FactoryHelper.unwrapDesc[index] );
 	}
 
 	private static void findAccessors(
 			Class clazz,
 	        String[] getterNames,
 	        String[] setterNames,
 	        Class[] types,
 	        Method[] getters,
 	        Method[] setters) {
 		int length = types.length;
 		if ( setterNames.length != length || getterNames.length != length ) {
 			throw new BulkAccessorException( "bad number of accessors" );
 		}
 
 		Class[] getParam = new Class[0];
 		Class[] setParam = new Class[1];
 		for ( int i = 0; i < length; i++ ) {
 			if ( getterNames[i] != null ) {
 				Method getter = findAccessor( clazz, getterNames[i], getParam, i );
 				if ( getter.getReturnType() != types[i] ) {
 					throw new BulkAccessorException( "wrong return type: " + getterNames[i], i );
 				}
 
 				getters[i] = getter;
 			}
 
 			if ( setterNames[i] != null ) {
 				setParam[0] = types[i];
 				setters[i] = findAccessor( clazz, setterNames[i], setParam, i );
 			}
 		}
 	}
 
 	private static Method findAccessor(
 			Class clazz,
 	        String name,
 	        Class[] params,
 	        int index) throws BulkAccessorException {
 		try {
 			Method method = clazz.getDeclaredMethod( name, params );
 			if ( Modifier.isPrivate( method.getModifiers() ) ) {
 				throw new BulkAccessorException( "private property", index );
 			}
 
 			return method;
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new BulkAccessorException( "cannot find an accessor", index );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
similarity index 87%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
index eb5443f9e4..9238dc9749 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/BytecodeProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/BytecodeProviderImpl.java
@@ -1,102 +1,103 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Modifier;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateLogger;
-import org.hibernate.bytecode.BytecodeProvider;
-import org.hibernate.bytecode.ClassTransformer;
-import org.hibernate.bytecode.ProxyFactoryFactory;
-import org.hibernate.bytecode.ReflectionOptimizer;
-import org.hibernate.bytecode.util.ClassFilter;
-import org.hibernate.bytecode.util.FieldFilter;
+import org.hibernate.bytecode.buildtime.spi.ClassFilter;
+import org.hibernate.bytecode.buildtime.spi.FieldFilter;
+import org.hibernate.bytecode.spi.BytecodeProvider;
+import org.hibernate.bytecode.spi.ClassTransformer;
+import org.hibernate.bytecode.spi.ProxyFactoryFactory;
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.internal.util.StringHelper;
-import org.jboss.logging.Logger;
 
 /**
  * Bytecode provider implementation for Javassist.
  *
  * @author Steve Ebersole
  */
 public class BytecodeProviderImpl implements BytecodeProvider {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BytecodeProviderImpl.class.getName());
 
 	public ProxyFactoryFactory getProxyFactoryFactory() {
 		return new ProxyFactoryFactoryImpl();
 	}
 
 	public ReflectionOptimizer getReflectionOptimizer(
 			Class clazz,
 	        String[] getterNames,
 	        String[] setterNames,
 	        Class[] types) {
 		FastClass fastClass;
 		BulkAccessor bulkAccessor;
 		try {
 			fastClass = FastClass.create( clazz );
 			bulkAccessor = BulkAccessor.create( clazz, getterNames, setterNames, types );
 			if ( !clazz.isInterface() && !Modifier.isAbstract( clazz.getModifiers() ) ) {
 				if ( fastClass == null ) {
 					bulkAccessor = null;
 				}
 				else {
 					//test out the optimizer:
 					Object instance = fastClass.newInstance();
 					bulkAccessor.setPropertyValues( instance, bulkAccessor.getPropertyValues( instance ) );
 				}
 			}
 		}
 		catch ( Throwable t ) {
 			fastClass = null;
 			bulkAccessor = null;
             if (LOG.isDebugEnabled()) {
                 int index = 0;
                 if (t instanceof BulkAccessorException) index = ((BulkAccessorException)t).getIndex();
                 if (index >= 0) LOG.debugf("Reflection optimizer disabled for: %s [%s: %s (property %s)",
                                            clazz.getName(),
                                            StringHelper.unqualify(t.getClass().getName()),
                                            t.getMessage(),
                                            setterNames[index]);
                 else LOG.debugf("Reflection optimizer disabled for: %s [%s: %s",
                                 clazz.getName(),
                                 StringHelper.unqualify(t.getClass().getName()),
                                 t.getMessage());
             }
 		}
 
 		if ( fastClass != null && bulkAccessor != null ) {
 			return new ReflectionOptimizerImpl(
 					new InstantiationOptimizerAdapter( fastClass ),
 			        new AccessOptimizerAdapter( bulkAccessor, clazz )
 			);
 		}
         return null;
 	}
 
 	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter) {
 		return new JavassistClassTransformer( classFilter, fieldFilter );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FastClass.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FastClass.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FastClass.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FastClass.java
index a9b3693d20..82069f103a 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FastClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FastClass.java
@@ -1,193 +1,193 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
+
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 
 /**
  * @author Muga Nishizawa
  */
 public class FastClass implements Serializable {
 
 	private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
 
 	private Class type;
 
 	private FastClass() {
 	}
 
 	private FastClass(Class type) {
 		this.type = type;
 	}
 
 	public Object invoke(
 			String name,
 	        Class[] parameterTypes,
 	        Object obj,
 	        Object[] args) throws InvocationTargetException {
 		return this.invoke( this.getIndex( name, parameterTypes ), obj, args );
 	}
 
 	public Object invoke(
 			int index,
 	        Object obj,
 	        Object[] args) throws InvocationTargetException {
 		Method[] methods = this.type.getMethods();
 		try {
 			return methods[index].invoke( obj, args );
 		}
 		catch ( ArrayIndexOutOfBoundsException e ) {
 			throw new IllegalArgumentException(
 					"Cannot find matching method/constructor"
 			);
 		}
 		catch ( IllegalAccessException e ) {
 			throw new InvocationTargetException( e );
 		}
 	}
 
 	public Object newInstance() throws InvocationTargetException {
 		return this.newInstance( this.getIndex( EMPTY_CLASS_ARRAY ), null );
 	}
 
 	public Object newInstance(
 			Class[] parameterTypes,
 	        Object[] args) throws InvocationTargetException {
 		return this.newInstance( this.getIndex( parameterTypes ), args );
 	}
 
 	public Object newInstance(
 			int index,
 	        Object[] args) throws InvocationTargetException {
 		Constructor[] conss = this.type.getConstructors();
 		try {
 			return conss[index].newInstance( args );
 		}
 		catch ( ArrayIndexOutOfBoundsException e ) {
 			throw new IllegalArgumentException( "Cannot find matching method/constructor" );
 		}
 		catch ( InstantiationException e ) {
 			throw new InvocationTargetException( e );
 		}
 		catch ( IllegalAccessException e ) {
 			throw new InvocationTargetException( e );
 		}
 	}
 
 	public int getIndex(String name, Class[] parameterTypes) {
 		Method[] methods = this.type.getMethods();
 		boolean eq = true;
 		for ( int i = 0; i < methods.length; ++i ) {
 			if ( !Modifier.isPublic( methods[i].getModifiers() ) ) {
 				continue;
 			}
 			if ( !methods[i].getName().equals( name ) ) {
 				continue;
 			}
 			Class[] params = methods[i].getParameterTypes();
 			if ( params.length != parameterTypes.length ) {
 				continue;
 			}
 			eq = true;
 			for ( int j = 0; j < params.length; ++j ) {
 				if ( !params[j].equals( parameterTypes[j] ) ) {
 					eq = false;
 					break;
 				}
 			}
 			if ( eq ) {
 				return i;
 			}
 		}
 		return -1;
 	}
 
 	public int getIndex(Class[] parameterTypes) {
 		Constructor[] conss = this.type.getConstructors();
 		boolean eq = true;
 		for ( int i = 0; i < conss.length; ++i ) {
 			if ( !Modifier.isPublic( conss[i].getModifiers() ) ) {
 				continue;
 			}
 			Class[] params = conss[i].getParameterTypes();
 			if ( params.length != parameterTypes.length ) {
 				continue;
 			}
 			eq = true;
 			for ( int j = 0; j < params.length; ++j ) {
 				if ( !params[j].equals( parameterTypes[j] ) ) {
 					eq = false;
 					break;
 				}
 			}
 			if ( eq ) {
 				return i;
 			}
 		}
 		return -1;
 	}
 
 	public int getMaxIndex() {
 		Method[] methods = this.type.getMethods();
 		int count = 0;
 		for ( int i = 0; i < methods.length; ++i ) {
 			if ( Modifier.isPublic( methods[i].getModifiers() ) ) {
 				count++;
 			}
 		}
 		return count;
 	}
 
 	public String getName() {
 		return this.type.getName();
 	}
 
 	public Class getJavaClass() {
 		return this.type;
 	}
 
 	public String toString() {
 		return this.type.toString();
 	}
 
 	public int hashCode() {
 		return this.type.hashCode();
 	}
 
 	public boolean equals(Object o) {
 		if (! ( o instanceof FastClass ) ) {
 			return false;
 		}
 		return this.type.equals( ( ( FastClass ) o ).type );
 	}
 
 	public static FastClass create(Class type) {
 		FastClass fc = new FastClass( type );
 		return fc;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldFilter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldFilter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
index 788daafa42..6d7d975f8a 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldFilter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldFilter.java
@@ -1,58 +1,57 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
 
 
 /**
  * Contract for deciding whether fields should be read and/or write intercepted.
  *
  * @author Muga Nishizawa
  * @author Steve Ebersole
  */
 public interface FieldFilter {
 	/**
 	 * Should the given field be read intercepted?
 	 *
 	 * @param desc
 	 * @param name
 	 * @return true if the given field should be read intercepted; otherwise
 	 * false.
 	 */
 	boolean handleRead(String desc, String name);
 
 	/**
 	 * Should the given field be write intercepted?
 	 *
 	 * @param desc
 	 * @param name
 	 * @return true if the given field should be write intercepted; otherwise
 	 * false.
 	 */
 	boolean handleWrite(String desc, String name);
 
 	boolean handleReadAccess(String fieldOwnerClassName, String fieldName);
 
 	boolean handleWriteAccess(String fieldOwnerClassName, String fieldName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandled.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandled.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandled.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandled.java
index 29b9630679..7d230bdc8b 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandled.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandled.java
@@ -1,48 +1,46 @@
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
-package org.hibernate.bytecode.javassist;
-
+package org.hibernate.bytecode.internal.javassist;
 
 /**
  * Interface introduced to the enhanced class in order to be able to
  * inject a {@link FieldHandler} to define the interception behavior.
  *
  * @author Muga Nishizawa
  */
 public interface FieldHandled {
 	/**
 	 * Inject the field interception handler to be used.
 	 *
 	 * @param handler The field interception handler.
 	 */
 	public void setFieldHandler(FieldHandler handler);
 
 	/**
 	 * Access to the current field interception handler.
 	 *
 	 * @return The current field interception handler.
 	 */
 	public FieldHandler getFieldHandler();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandler.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandler.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
index 382bd9793d..135f6433ec 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldHandler.java
@@ -1,81 +1,79 @@
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
-package org.hibernate.bytecode.javassist;
-
+package org.hibernate.bytecode.internal.javassist;
 
 /**
  * The interface defining how interception of a field should be handled.
  *
  * @author Muga Nishizawa
  */
 public interface FieldHandler {
 
 	/**
 	 * Called to handle writing an int value to a given field.
 	 *
 	 * @param obj ?
 	 * @param name The name of the field being written
 	 * @param oldValue The old field value
 	 * @param newValue The new field value.
 	 * @return ?
 	 */
 	int writeInt(Object obj, String name, int oldValue, int newValue);
 
 	char writeChar(Object obj, String name, char oldValue, char newValue);
 
 	byte writeByte(Object obj, String name, byte oldValue, byte newValue);
 
 	boolean writeBoolean(Object obj, String name, boolean oldValue,
 			boolean newValue);
 
 	short writeShort(Object obj, String name, short oldValue, short newValue);
 
 	float writeFloat(Object obj, String name, float oldValue, float newValue);
 
 	double writeDouble(Object obj, String name, double oldValue, double newValue);
 
 	long writeLong(Object obj, String name, long oldValue, long newValue);
 
 	Object writeObject(Object obj, String name, Object oldValue, Object newValue);
 
 	int readInt(Object obj, String name, int oldValue);
 
 	char readChar(Object obj, String name, char oldValue);
 
 	byte readByte(Object obj, String name, byte oldValue);
 
 	boolean readBoolean(Object obj, String name, boolean oldValue);
 
 	short readShort(Object obj, String name, short oldValue);
 
 	float readFloat(Object obj, String name, float oldValue);
 
 	double readDouble(Object obj, String name, double oldValue);
 
 	long readLong(Object obj, String name, long oldValue);
 
 	Object readObject(Object obj, String name, Object oldValue);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
index 9b70c29970..4757612b80 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/FieldTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/FieldTransformer.java
@@ -1,603 +1,604 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
+
 import java.io.DataInputStream;
 import java.io.DataOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.util.Iterator;
 import java.util.List;
+
 import javassist.CannotCompileException;
 import javassist.bytecode.AccessFlag;
 import javassist.bytecode.BadBytecode;
 import javassist.bytecode.Bytecode;
 import javassist.bytecode.ClassFile;
 import javassist.bytecode.CodeAttribute;
 import javassist.bytecode.CodeIterator;
 import javassist.bytecode.ConstPool;
 import javassist.bytecode.Descriptor;
 import javassist.bytecode.FieldInfo;
 import javassist.bytecode.MethodInfo;
 import javassist.bytecode.Opcode;
 
 /**
  * The thing that handles actual class enhancement in regards to
  * intercepting field accesses.
  *
  * @author Muga Nishizawa
  * @author Steve Ebersole
  */
 public class FieldTransformer {
 
 	private static final String EACH_READ_METHOD_PREFIX = "$javassist_read_";
 
 	private static final String EACH_WRITE_METHOD_PREFIX = "$javassist_write_";
 
 	private static final String FIELD_HANDLED_TYPE_NAME = FieldHandled.class
 			.getName();
 
 	private static final String HANDLER_FIELD_NAME = "$JAVASSIST_READ_WRITE_HANDLER";
 
 	private static final String FIELD_HANDLER_TYPE_NAME = FieldHandler.class
 			.getName();
 
 	private static final String HANDLER_FIELD_DESCRIPTOR = 'L' + FIELD_HANDLER_TYPE_NAME
 			.replace('.', '/') + ';';
 
 	private static final String GETFIELDHANDLER_METHOD_NAME = "getFieldHandler";
 
 	private static final String SETFIELDHANDLER_METHOD_NAME = "setFieldHandler";
 
 	private static final String GETFIELDHANDLER_METHOD_DESCRIPTOR = "()"
 	                                                                + HANDLER_FIELD_DESCRIPTOR;
 
 	private static final String SETFIELDHANDLER_METHOD_DESCRIPTOR = "("
 	                                                                + HANDLER_FIELD_DESCRIPTOR + ")V";
 
 	private FieldFilter filter;
 
 	public FieldTransformer() {
 		this(null);
 	}
 
 	public FieldTransformer(FieldFilter f) {
 		filter = f;
 	}
 
 	public void setFieldFilter(FieldFilter f) {
 		filter = f;
 	}
 
 	public void transform(File file) throws Exception {
 		DataInputStream in = new DataInputStream(new FileInputStream(file));
 		ClassFile classfile = new ClassFile(in);
 		transform(classfile);
 		DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
 		try {
 			classfile.write(out);
 		} finally {
 			out.close();
 		}
 	}
 
 	public void transform(ClassFile classfile) throws Exception {
 		if (classfile.isInterface()) {
 			return;
 		}
 		try {
 			addFieldHandlerField(classfile);
 			addGetFieldHandlerMethod(classfile);
 			addSetFieldHandlerMethod(classfile);
 			addFieldHandledInterface(classfile);
 			addReadWriteMethods(classfile);
 			transformInvokevirtualsIntoPutAndGetfields(classfile);
 		} catch (CannotCompileException e) {
 			throw new RuntimeException(e.getMessage(), e);
 		}
 	}
 
 	private void addFieldHandlerField(ClassFile classfile)
 			throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		FieldInfo finfo = new FieldInfo(cp, HANDLER_FIELD_NAME,
 		                                HANDLER_FIELD_DESCRIPTOR);
 		finfo.setAccessFlags(AccessFlag.PRIVATE | AccessFlag.TRANSIENT);
 		classfile.addField(finfo);
 	}
 
 	private void addGetFieldHandlerMethod(ClassFile classfile)
 			throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		int this_class_index = cp.getThisClassInfo();
 		MethodInfo minfo = new MethodInfo(cp, GETFIELDHANDLER_METHOD_NAME,
 		                                  GETFIELDHANDLER_METHOD_DESCRIPTOR);
 		/* local variable | this | */
 		Bytecode code = new Bytecode(cp, 2, 1);
 		// aload_0 // load this
 		code.addAload(0);
 		// getfield // get field "$JAVASSIST_CALLBACK" defined already
 		code.addOpcode(Opcode.GETFIELD);
 		int field_index = cp.addFieldrefInfo(this_class_index,
 		                                     HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR);
 		code.addIndex(field_index);
 		// areturn // return the value of the field
 		code.addOpcode(Opcode.ARETURN);
 		minfo.setCodeAttribute(code.toCodeAttribute());
 		minfo.setAccessFlags(AccessFlag.PUBLIC);
 		classfile.addMethod(minfo);
 	}
 
 	private void addSetFieldHandlerMethod(ClassFile classfile)
 			throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		int this_class_index = cp.getThisClassInfo();
 		MethodInfo minfo = new MethodInfo(cp, SETFIELDHANDLER_METHOD_NAME,
 		                                  SETFIELDHANDLER_METHOD_DESCRIPTOR);
 		/* local variables | this | callback | */
 		Bytecode code = new Bytecode(cp, 3, 3);
 		// aload_0 // load this
 		code.addAload(0);
 		// aload_1 // load callback
 		code.addAload(1);
 		// putfield // put field "$JAVASSIST_CALLBACK" defined already
 		code.addOpcode(Opcode.PUTFIELD);
 		int field_index = cp.addFieldrefInfo(this_class_index,
 		                                     HANDLER_FIELD_NAME, HANDLER_FIELD_DESCRIPTOR);
 		code.addIndex(field_index);
 		// return
 		code.addOpcode(Opcode.RETURN);
 		minfo.setCodeAttribute(code.toCodeAttribute());
 		minfo.setAccessFlags(AccessFlag.PUBLIC);
 		classfile.addMethod(minfo);
 	}
 
 	private void addFieldHandledInterface(ClassFile classfile) {
 		String[] interfaceNames = classfile.getInterfaces();
 		String[] newInterfaceNames = new String[interfaceNames.length + 1];
 		System.arraycopy(interfaceNames, 0, newInterfaceNames, 0,
 		                 interfaceNames.length);
 		newInterfaceNames[newInterfaceNames.length - 1] = FIELD_HANDLED_TYPE_NAME;
 		classfile.setInterfaces(newInterfaceNames);
 	}
 
 	private void addReadWriteMethods(ClassFile classfile)
 			throws CannotCompileException {
 		List fields = classfile.getFields();
 		for (Iterator field_iter = fields.iterator(); field_iter.hasNext();) {
 			FieldInfo finfo = (FieldInfo) field_iter.next();
 			if ((finfo.getAccessFlags() & AccessFlag.STATIC) == 0
 			    && (!finfo.getName().equals(HANDLER_FIELD_NAME))) {
 				// case of non-static field
 				if (filter.handleRead(finfo.getDescriptor(), finfo
 						.getName())) {
 					addReadMethod(classfile, finfo);
 				}
 				if (filter.handleWrite(finfo.getDescriptor(), finfo
 						.getName())) {
 					addWriteMethod(classfile, finfo);
 				}
 			}
 		}
 	}
 
 	private void addReadMethod(ClassFile classfile, FieldInfo finfo)
 			throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		int this_class_index = cp.getThisClassInfo();
 		String desc = "()" + finfo.getDescriptor();
 		MethodInfo minfo = new MethodInfo(cp, EACH_READ_METHOD_PREFIX
 		                                      + finfo.getName(), desc);
 		/* local variables | target obj | each oldvalue | */
 		Bytecode code = new Bytecode(cp, 5, 3);
 		// aload_0
 		code.addAload(0);
 		// getfield // get each field
 		code.addOpcode(Opcode.GETFIELD);
 		int base_field_index = cp.addFieldrefInfo(this_class_index, finfo
 				.getName(), finfo.getDescriptor());
 		code.addIndex(base_field_index);
 		// aload_0
 		code.addAload(0);
 		// invokeinterface // invoke Enabled.getInterceptFieldCallback()
 		int enabled_class_index = cp.addClassInfo(FIELD_HANDLED_TYPE_NAME);
 		code.addInvokeinterface(enabled_class_index,
 		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
 		                        1);
 		// ifnonnull
 		code.addOpcode(Opcode.IFNONNULL);
 		code.addIndex(4);
 		// *return // each type
 		addTypeDependDataReturn(code, finfo.getDescriptor());
 		// *store_1 // each type
 		addTypeDependDataStore(code, finfo.getDescriptor(), 1);
 		// aload_0
 		code.addAload(0);
 		// invokeinterface // invoke Enabled.getInterceptFieldCallback()
 		code.addInvokeinterface(enabled_class_index,
 		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
 		                        1);
 		// aload_0
 		code.addAload(0);
 		// ldc // name of the field
 		code.addLdc(finfo.getName());
 		// *load_1 // each type
 		addTypeDependDataLoad(code, finfo.getDescriptor(), 1);
 		// invokeinterface // invoke Callback.read*() // each type
 		addInvokeFieldHandlerMethod(classfile, code, finfo.getDescriptor(),
 		                            true);
 		// *return // each type
 		addTypeDependDataReturn(code, finfo.getDescriptor());
 
 		minfo.setCodeAttribute(code.toCodeAttribute());
 		minfo.setAccessFlags(AccessFlag.PUBLIC);
 		classfile.addMethod(minfo);
 	}
 
 	private void addWriteMethod(ClassFile classfile, FieldInfo finfo)
 			throws CannotCompileException {
 		ConstPool cp = classfile.getConstPool();
 		int this_class_index = cp.getThisClassInfo();
 		String desc = "(" + finfo.getDescriptor() + ")V";
 		MethodInfo minfo = new MethodInfo(cp, EACH_WRITE_METHOD_PREFIX
 		                                      + finfo.getName(), desc);
 		/* local variables | target obj | each oldvalue | */
 		Bytecode code = new Bytecode(cp, 6, 3);
 		// aload_0
 		code.addAload(0);
 		// invokeinterface // enabled.getInterceptFieldCallback()
 		int enabled_class_index = cp.addClassInfo(FIELD_HANDLED_TYPE_NAME);
 		code.addInvokeinterface(enabled_class_index,
 		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
 		                        1);
 		// ifnonnull (label1)
 		code.addOpcode(Opcode.IFNONNULL);
 		code.addIndex(9);
 		// aload_0
 		code.addAload(0);
 		// *load_1
 		addTypeDependDataLoad(code, finfo.getDescriptor(), 1);
 		// putfield
 		code.addOpcode(Opcode.PUTFIELD);
 		int base_field_index = cp.addFieldrefInfo(this_class_index, finfo
 				.getName(), finfo.getDescriptor());
 		code.addIndex(base_field_index);
 		code.growStack(-Descriptor.dataSize(finfo.getDescriptor()));
 		// return ;
 		code.addOpcode(Opcode.RETURN);
 		// aload_0
 		code.addAload(0);
 		// dup
 		code.addOpcode(Opcode.DUP);
 		// invokeinterface // enabled.getInterceptFieldCallback()
 		code.addInvokeinterface(enabled_class_index,
 		                        GETFIELDHANDLER_METHOD_NAME, GETFIELDHANDLER_METHOD_DESCRIPTOR,
 		                        1);
 		// aload_0
 		code.addAload(0);
 		// ldc // field name
 		code.addLdc(finfo.getName());
 		// aload_0
 		code.addAload(0);
 		// getfield // old value of the field
 		code.addOpcode(Opcode.GETFIELD);
 		code.addIndex(base_field_index);
 		code.growStack(Descriptor.dataSize(finfo.getDescriptor()) - 1);
 		// *load_1
 		addTypeDependDataLoad(code, finfo.getDescriptor(), 1);
 		// invokeinterface // callback.write*(..)
 		addInvokeFieldHandlerMethod(classfile, code, finfo.getDescriptor(),
 		                            false);
 		// putfield // new value of the field
 		code.addOpcode(Opcode.PUTFIELD);
 		code.addIndex(base_field_index);
 		code.growStack(-Descriptor.dataSize(finfo.getDescriptor()));
 		// return
 		code.addOpcode(Opcode.RETURN);
 
 		minfo.setCodeAttribute(code.toCodeAttribute());
 		minfo.setAccessFlags(AccessFlag.PUBLIC);
 		classfile.addMethod(minfo);
 	}
 
 	private void transformInvokevirtualsIntoPutAndGetfields(ClassFile classfile)
 			throws CannotCompileException {
 		List methods = classfile.getMethods();
 		for (Iterator method_iter = methods.iterator(); method_iter.hasNext();) {
 			MethodInfo minfo = (MethodInfo) method_iter.next();
 			String methodName = minfo.getName();
 			if (methodName.startsWith(EACH_READ_METHOD_PREFIX)
 			    || methodName.startsWith(EACH_WRITE_METHOD_PREFIX)
 			    || methodName.equals(GETFIELDHANDLER_METHOD_NAME)
 			    || methodName.equals(SETFIELDHANDLER_METHOD_NAME)) {
 				continue;
 			}
 			CodeAttribute codeAttr = minfo.getCodeAttribute();
 			if (codeAttr == null) {
 				continue;
 			}
 			CodeIterator iter = codeAttr.iterator();
 			while (iter.hasNext()) {
 				try {
 					int pos = iter.next();
 					pos = transformInvokevirtualsIntoGetfields(classfile, iter, pos);
 					pos = transformInvokevirtualsIntoPutfields(classfile, iter, pos);
 
 				} catch (BadBytecode e) {
 					throw new CannotCompileException(e);
 				}
 			}
 		}
 	}
 
 	private int transformInvokevirtualsIntoGetfields(ClassFile classfile, CodeIterator iter, int pos) {
 		ConstPool cp = classfile.getConstPool();
 		int c = iter.byteAt(pos);
 		if (c != Opcode.GETFIELD) {
 			return pos;
 		}
 		int index = iter.u16bitAt(pos + 1);
 		String fieldName = cp.getFieldrefName(index);
 		String className = cp.getFieldrefClassName(index);
 		if ( !filter.handleReadAccess( className, fieldName ) ) {
 			return pos;
 		}
 		String desc = "()" + cp.getFieldrefType( index );
 		int read_method_index = cp.addMethodrefInfo(
 				cp.getThisClassInfo(),
 				EACH_READ_METHOD_PREFIX + fieldName,
 				desc
 		);
 		iter.writeByte(Opcode.INVOKEVIRTUAL, pos);
 		iter.write16bit(read_method_index, pos + 1);
 		return pos;
 	}
 
 	private int transformInvokevirtualsIntoPutfields(
 			ClassFile classfile,
 			CodeIterator iter, int pos) {
 		ConstPool cp = classfile.getConstPool();
 		int c = iter.byteAt(pos);
 		if (c != Opcode.PUTFIELD) {
 			return pos;
 		}
 		int index = iter.u16bitAt(pos + 1);
 		String fieldName = cp.getFieldrefName(index);
 		String className = cp.getFieldrefClassName(index);
 		if ( !filter.handleWriteAccess( className, fieldName ) ) {
 			return pos;
 		}
 		String desc = "(" + cp.getFieldrefType( index ) + ")V";
 		int write_method_index = cp.addMethodrefInfo(
 				cp.getThisClassInfo(),
 				EACH_WRITE_METHOD_PREFIX + fieldName,
 				desc
 		);
 		iter.writeByte(Opcode.INVOKEVIRTUAL, pos);
 		iter.write16bit(write_method_index, pos + 1);
 		return pos;
 	}
 
 	private static void addInvokeFieldHandlerMethod(ClassFile classfile,
 	                                                Bytecode code, String typeName, boolean isReadMethod) {
 		ConstPool cp = classfile.getConstPool();
 		// invokeinterface
 		int callback_type_index = cp.addClassInfo(FIELD_HANDLER_TYPE_NAME);
 		if ((typeName.charAt(0) == 'L')
 		    && (typeName.charAt(typeName.length() - 1) == ';')
 		    || (typeName.charAt(0) == '[')) {
 			// reference type
 			int indexOfL = typeName.indexOf('L');
 			String type;
 			if (indexOfL == 0) {
 				// not array
 				type = typeName.substring(1, typeName.length() - 1);
 				type = type.replace('/', '.');
 			} else if (indexOfL == -1) {
 				// array of primitive type
 				// do nothing
 				type = typeName;
 			} else {
 				// array of reference type
 				type = typeName.replace('/', '.');
 			}
 			if (isReadMethod) {
 				code
 						.addInvokeinterface(
 								callback_type_index,
 								"readObject",
 								"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;",
 								4);
 				// checkcast
 				code.addCheckcast(type);
 			} else {
 				code
 						.addInvokeinterface(
 								callback_type_index,
 								"writeObject",
 								"(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
 								5);
 				// checkcast
 				code.addCheckcast(type);
 			}
 		} else if (typeName.equals("Z")) {
 			// boolean
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readBoolean",
 				                        "(Ljava/lang/Object;Ljava/lang/String;Z)Z", 4);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeBoolean",
 				                        "(Ljava/lang/Object;Ljava/lang/String;ZZ)Z", 5);
 			}
 		} else if (typeName.equals("B")) {
 			// byte
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readByte",
 				                        "(Ljava/lang/Object;Ljava/lang/String;B)B", 4);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeByte",
 				                        "(Ljava/lang/Object;Ljava/lang/String;BB)B", 5);
 			}
 		} else if (typeName.equals("C")) {
 			// char
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readChar",
 				                        "(Ljava/lang/Object;Ljava/lang/String;C)C", 4);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeChar",
 				                        "(Ljava/lang/Object;Ljava/lang/String;CC)C", 5);
 			}
 		} else if (typeName.equals("I")) {
 			// int
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readInt",
 				                        "(Ljava/lang/Object;Ljava/lang/String;I)I", 4);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeInt",
 				                        "(Ljava/lang/Object;Ljava/lang/String;II)I", 5);
 			}
 		} else if (typeName.equals("S")) {
 			// short
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readShort",
 				                        "(Ljava/lang/Object;Ljava/lang/String;S)S", 4);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeShort",
 				                        "(Ljava/lang/Object;Ljava/lang/String;SS)S", 5);
 			}
 		} else if (typeName.equals("D")) {
 			// double
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readDouble",
 				                        "(Ljava/lang/Object;Ljava/lang/String;D)D", 5);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeDouble",
 				                        "(Ljava/lang/Object;Ljava/lang/String;DD)D", 7);
 			}
 		} else if (typeName.equals("F")) {
 			// float
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readFloat",
 				                        "(Ljava/lang/Object;Ljava/lang/String;F)F", 4);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeFloat",
 				                        "(Ljava/lang/Object;Ljava/lang/String;FF)F", 5);
 			}
 		} else if (typeName.equals("J")) {
 			// long
 			if (isReadMethod) {
 				code.addInvokeinterface(callback_type_index, "readLong",
 				                        "(Ljava/lang/Object;Ljava/lang/String;J)J", 5);
 			} else {
 				code.addInvokeinterface(callback_type_index, "writeLong",
 				                        "(Ljava/lang/Object;Ljava/lang/String;JJ)J", 7);
 			}
 		} else {
 			// bad type
 			throw new RuntimeException("bad type: " + typeName);
 		}
 	}
 
 	private static void addTypeDependDataLoad(Bytecode code, String typeName,
 	                                          int i) {
 		if ((typeName.charAt(0) == 'L')
 		    && (typeName.charAt(typeName.length() - 1) == ';')
 		    || (typeName.charAt(0) == '[')) {
 			// reference type
 			code.addAload(i);
 		} else if (typeName.equals("Z") || typeName.equals("B")
 		           || typeName.equals("C") || typeName.equals("I")
 		           || typeName.equals("S")) {
 			// boolean, byte, char, int, short
 			code.addIload(i);
 		} else if (typeName.equals("D")) {
 			// double
 			code.addDload(i);
 		} else if (typeName.equals("F")) {
 			// float
 			code.addFload(i);
 		} else if (typeName.equals("J")) {
 			// long
 			code.addLload(i);
 		} else {
 			// bad type
 			throw new RuntimeException("bad type: " + typeName);
 		}
 	}
 
 	private static void addTypeDependDataStore(Bytecode code, String typeName,
 	                                           int i) {
 		if ((typeName.charAt(0) == 'L')
 		    && (typeName.charAt(typeName.length() - 1) == ';')
 		    || (typeName.charAt(0) == '[')) {
 			// reference type
 			code.addAstore(i);
 		} else if (typeName.equals("Z") || typeName.equals("B")
 		           || typeName.equals("C") || typeName.equals("I")
 		           || typeName.equals("S")) {
 			// boolean, byte, char, int, short
 			code.addIstore(i);
 		} else if (typeName.equals("D")) {
 			// double
 			code.addDstore(i);
 		} else if (typeName.equals("F")) {
 			// float
 			code.addFstore(i);
 		} else if (typeName.equals("J")) {
 			// long
 			code.addLstore(i);
 		} else {
 			// bad type
 			throw new RuntimeException("bad type: " + typeName);
 		}
 	}
 
 	private static void addTypeDependDataReturn(Bytecode code, String typeName) {
 		if ((typeName.charAt(0) == 'L')
 		    && (typeName.charAt(typeName.length() - 1) == ';')
 		    || (typeName.charAt(0) == '[')) {
 			// reference type
 			code.addOpcode(Opcode.ARETURN);
 		} else if (typeName.equals("Z") || typeName.equals("B")
 		           || typeName.equals("C") || typeName.equals("I")
 		           || typeName.equals("S")) {
 			// boolean, byte, char, int, short
 			code.addOpcode(Opcode.IRETURN);
 		} else if (typeName.equals("D")) {
 			// double
 			code.addOpcode(Opcode.DRETURN);
 		} else if (typeName.equals("F")) {
 			// float
 			code.addOpcode(Opcode.FRETURN);
 		} else if (typeName.equals("J")) {
 			// long
 			code.addOpcode(Opcode.LRETURN);
 		} else {
 			// bad type
 			throw new RuntimeException("bad type: " + typeName);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/InstantiationOptimizerAdapter.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/InstantiationOptimizerAdapter.java
similarity index 79%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/InstantiationOptimizerAdapter.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/InstantiationOptimizerAdapter.java
index ee6248f66d..a66000b0af 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/InstantiationOptimizerAdapter.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/InstantiationOptimizerAdapter.java
@@ -1,54 +1,55 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
+
 import java.io.Serializable;
+
 import org.hibernate.InstantiationException;
-import org.hibernate.bytecode.ReflectionOptimizer;
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
 
 /**
- * The {@link ReflectionOptimizer.InstantiationOptimizer} implementation for Javassist
- * which simply acts as an adpater to the {@link FastClass} class.
+ * The {@link org.hibernate.bytecode.spi.ReflectionOptimizer.InstantiationOptimizer} implementation for Javassist
+ * which simply acts as an adapter to the {@link FastClass} class.
  *
  * @author Steve Ebersole
  */
 public class InstantiationOptimizerAdapter implements ReflectionOptimizer.InstantiationOptimizer, Serializable {
 	private final FastClass fastClass;
 
 	public InstantiationOptimizerAdapter(FastClass fastClass) {
 		this.fastClass = fastClass;
 	}
 
 	public Object newInstance() {
 		try {
 			return fastClass.newInstance();
 		}
 		catch ( Throwable t ) {
 			throw new InstantiationException(
 					"Could not instantiate entity with Javassist optimizer: ",
 			        fastClass.getJavaClass(), t
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/JavassistClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/JavassistClassTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
index 224240eb23..5334b24786 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/JavassistClassTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/JavassistClassTransformer.java
@@ -1,132 +1,134 @@
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
-package org.hibernate.bytecode.javassist;
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.DataInputStream;
-import java.io.DataOutputStream;
-import java.io.IOException;
-import java.security.ProtectionDomain;
-import javassist.bytecode.ClassFile;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.bytecode.AbstractClassTransformerImpl;
-import org.hibernate.bytecode.util.ClassFilter;
-import org.jboss.logging.Logger;
-
-/**
- * Enhance the classes allowing them to implements InterceptFieldEnabled
- * This interface is then used by Hibernate for some optimizations.
- *
- * @author Emmanuel Bernard
- * @author Steve Ebersole
- */
-public class JavassistClassTransformer extends AbstractClassTransformerImpl {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       JavassistClassTransformer.class.getName());
-
-	public JavassistClassTransformer(ClassFilter classFilter, org.hibernate.bytecode.util.FieldFilter fieldFilter) {
-		super( classFilter, fieldFilter );
-	}
-
-	@Override
-    protected byte[] doTransform(
-			ClassLoader loader,
-			String className,
-			Class classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer) {
-		ClassFile classfile;
-		try {
-			// WARNING: classfile only
-			classfile = new ClassFile( new DataInputStream( new ByteArrayInputStream( classfileBuffer ) ) );
-		}
-		catch (IOException e) {
-            LOG.unableToBuildEnhancementMetamodel(className);
-			return classfileBuffer;
-		}
-		FieldTransformer transformer = getFieldTransformer( classfile );
-		if ( transformer != null ) {
-            LOG.debugf("Enhancing %s", className);
-			DataOutputStream out = null;
-			try {
-				transformer.transform( classfile );
-				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
-				out = new DataOutputStream( byteStream );
-				classfile.write( out );
-				return byteStream.toByteArray();
-			}
-			catch (Exception e) {
-                LOG.unableToTransformClass(e.getMessage());
-				throw new HibernateException( "Unable to transform class: " + e.getMessage() );
-			}
-			finally {
-				try {
-					if ( out != null ) out.close();
-				}
-				catch (IOException e) {
-					//swallow
-				}
-			}
-		}
-		return classfileBuffer;
-	}
-
-	protected FieldTransformer getFieldTransformer(final ClassFile classfile) {
-		if ( alreadyInstrumented( classfile ) ) {
-			return null;
-		}
-		return new FieldTransformer(
-				new FieldFilter() {
-					public boolean handleRead(String desc, String name) {
-						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
-					}
-
-					public boolean handleWrite(String desc, String name) {
-						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
-					}
-
-					public boolean handleReadAccess(String fieldOwnerClassName, String fieldName) {
-						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
-					}
-
-					public boolean handleWriteAccess(String fieldOwnerClassName, String fieldName) {
-						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
-					}
-				}
-		);
-	}
-
-	private boolean alreadyInstrumented(ClassFile classfile) {
-		String[] intfs = classfile.getInterfaces();
-		for ( int i = 0; i < intfs.length; i++ ) {
-			if ( FieldHandled.class.getName().equals( intfs[i] ) ) {
-				return true;
-			}
-		}
-		return false;
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
+package org.hibernate.bytecode.internal.javassist;
+
+import java.io.ByteArrayInputStream;
+import java.io.ByteArrayOutputStream;
+import java.io.DataInputStream;
+import java.io.DataOutputStream;
+import java.io.IOException;
+import java.security.ProtectionDomain;
+
+import javassist.bytecode.ClassFile;
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.HibernateLogger;
+import org.hibernate.bytecode.buildtime.spi.ClassFilter;
+import org.hibernate.bytecode.spi.AbstractClassTransformerImpl;
+
+/**
+ * Enhance the classes allowing them to implements InterceptFieldEnabled
+ * This interface is then used by Hibernate for some optimizations.
+ *
+ * @author Emmanuel Bernard
+ * @author Steve Ebersole
+ */
+public class JavassistClassTransformer extends AbstractClassTransformerImpl {
+
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+                                                                       JavassistClassTransformer.class.getName());
+
+	public JavassistClassTransformer(ClassFilter classFilter, org.hibernate.bytecode.buildtime.spi.FieldFilter fieldFilter) {
+		super( classFilter, fieldFilter );
+	}
+
+	@Override
+    protected byte[] doTransform(
+			ClassLoader loader,
+			String className,
+			Class classBeingRedefined,
+			ProtectionDomain protectionDomain,
+			byte[] classfileBuffer) {
+		ClassFile classfile;
+		try {
+			// WARNING: classfile only
+			classfile = new ClassFile( new DataInputStream( new ByteArrayInputStream( classfileBuffer ) ) );
+		}
+		catch (IOException e) {
+            LOG.unableToBuildEnhancementMetamodel(className);
+			return classfileBuffer;
+		}
+		FieldTransformer transformer = getFieldTransformer( classfile );
+		if ( transformer != null ) {
+            LOG.debugf("Enhancing %s", className);
+			DataOutputStream out = null;
+			try {
+				transformer.transform( classfile );
+				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
+				out = new DataOutputStream( byteStream );
+				classfile.write( out );
+				return byteStream.toByteArray();
+			}
+			catch (Exception e) {
+                LOG.unableToTransformClass(e.getMessage());
+				throw new HibernateException( "Unable to transform class: " + e.getMessage() );
+			}
+			finally {
+				try {
+					if ( out != null ) out.close();
+				}
+				catch (IOException e) {
+					//swallow
+				}
+			}
+		}
+		return classfileBuffer;
+	}
+
+	protected FieldTransformer getFieldTransformer(final ClassFile classfile) {
+		if ( alreadyInstrumented( classfile ) ) {
+			return null;
+		}
+		return new FieldTransformer(
+				new FieldFilter() {
+					public boolean handleRead(String desc, String name) {
+						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
+					}
+
+					public boolean handleWrite(String desc, String name) {
+						return fieldFilter.shouldInstrumentField( classfile.getName(), name );
+					}
+
+					public boolean handleReadAccess(String fieldOwnerClassName, String fieldName) {
+						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
+					}
+
+					public boolean handleWriteAccess(String fieldOwnerClassName, String fieldName) {
+						return fieldFilter.shouldTransformFieldAccess( classfile.getName(), fieldOwnerClassName, fieldName );
+					}
+				}
+		);
+	}
+
+	private boolean alreadyInstrumented(ClassFile classfile) {
+		String[] intfs = classfile.getInterfaces();
+		for ( int i = 0; i < intfs.length; i++ ) {
+			if ( FieldHandled.class.getName().equals( intfs[i] ) ) {
+				return true;
+			}
+		}
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
index a43dcc4d85..5fce7a5887 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
@@ -1,145 +1,147 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
+
 import java.lang.reflect.Method;
 import java.util.HashMap;
+
 import javassist.util.proxy.MethodFilter;
 import javassist.util.proxy.MethodHandler;
 import javassist.util.proxy.ProxyObject;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
-import org.hibernate.bytecode.BasicProxyFactory;
-import org.hibernate.bytecode.ProxyFactoryFactory;
+import org.hibernate.bytecode.spi.BasicProxyFactory;
+import org.hibernate.bytecode.spi.ProxyFactoryFactory;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.proxy.pojo.javassist.JavassistProxyFactory;
 
 /**
  * A factory for Javassist-based {@link ProxyFactory} instances.
  *
  * @author Steve Ebersole
  */
 public class ProxyFactoryFactoryImpl implements ProxyFactoryFactory {
 
 	/**
 	 * Builds a Javassist-based proxy factory.
 	 *
 	 * @return a new Javassist-based proxy factory.
 	 */
 	public ProxyFactory buildProxyFactory() {
 		return new JavassistProxyFactory();
 	}
 
 	public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces) {
 		return new BasicProxyFactoryImpl( superClass, interfaces );
 	}
 
 	private static class BasicProxyFactoryImpl implements BasicProxyFactory {
 		private final Class proxyClass;
 
 		public BasicProxyFactoryImpl(Class superClass, Class[] interfaces) {
 			if ( superClass == null && ( interfaces == null || interfaces.length < 1 ) ) {
 				throw new AssertionFailure( "attempting to build proxy without any superclass or interfaces" );
 			}
 			javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
 			factory.setFilter( FINALIZE_FILTER );
 			if ( superClass != null ) {
 				factory.setSuperclass( superClass );
 			}
 			if ( interfaces != null && interfaces.length > 0 ) {
 				factory.setInterfaces( interfaces );
 			}
 			proxyClass = factory.createClass();
 		}
 
 		public Object getProxy() {
 			try {
 				ProxyObject proxy = ( ProxyObject ) proxyClass.newInstance();
 				proxy.setHandler( new PassThroughHandler( proxy, proxyClass.getName() ) );
 				return proxy;
 			}
 			catch ( Throwable t ) {
 				throw new HibernateException( "Unable to instantiated proxy instance" );
 			}
 		}
 
 		public boolean isInstance(Object object) {
 			return proxyClass.isInstance( object );
 		}
 	}
 
 	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
 		public boolean isHandled(Method m) {
 			// skip finalize methods
 			return !( m.getParameterTypes().length == 0 && m.getName().equals( "finalize" ) );
 		}
 	};
 
 	private static class PassThroughHandler implements MethodHandler {
 		private HashMap data = new HashMap();
 		private final Object proxiedObject;
 		private final String proxiedClassName;
 
 		public PassThroughHandler(Object proxiedObject, String proxiedClassName) {
 			this.proxiedObject = proxiedObject;
 			this.proxiedClassName = proxiedClassName;
 		}
 
 		public Object invoke(
 				Object object,
 		        Method method,
 		        Method method1,
 		        Object[] args) throws Exception {
 			String name = method.getName();
 			if ( "toString".equals( name ) ) {
 				return proxiedClassName + "@" + System.identityHashCode( object );
 			}
 			else if ( "equals".equals( name ) ) {
 				return proxiedObject == object ? Boolean.TRUE : Boolean.FALSE;
 			}
 			else if ( "hashCode".equals( name ) ) {
 				return new Integer( System.identityHashCode( object ) );
 			}
 			boolean hasGetterSignature = method.getParameterTypes().length == 0 && method.getReturnType() != null;
 			boolean hasSetterSignature = method.getParameterTypes().length == 1 && ( method.getReturnType() == null || method.getReturnType() == void.class );
 			if ( name.startsWith( "get" ) && hasGetterSignature ) {
 				String propName = name.substring( 3 );
 				return data.get( propName );
 			}
 			else if ( name.startsWith( "is" ) && hasGetterSignature ) {
 				String propName = name.substring( 2 );
 				return data.get( propName );
 			}
 			else if ( name.startsWith( "set" ) && hasSetterSignature) {
 				String propName = name.substring( 3 );
 				data.put( propName, args[0] );
 				return null;
 			}
 			else {
 				// todo : what else to do here?
 				return null;
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ReflectionOptimizerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ReflectionOptimizerImpl.java
similarity index 87%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ReflectionOptimizerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ReflectionOptimizerImpl.java
index 034c8a1f16..e95bc67254 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/ReflectionOptimizerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ReflectionOptimizerImpl.java
@@ -1,54 +1,55 @@
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
-package org.hibernate.bytecode.javassist;
+package org.hibernate.bytecode.internal.javassist;
+
 import java.io.Serializable;
-import org.hibernate.bytecode.ReflectionOptimizer;
+
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
 
 /**
  * ReflectionOptimizer implementation for Javassist.
  *
  * @author Steve Ebersole
  */
 public class ReflectionOptimizerImpl implements ReflectionOptimizer, Serializable {
 
 	private final InstantiationOptimizer instantiationOptimizer;
 	private final AccessOptimizer accessOptimizer;
 
 	public ReflectionOptimizerImpl(
 			InstantiationOptimizer instantiationOptimizer,
 	        AccessOptimizer accessOptimizer) {
 		this.instantiationOptimizer = instantiationOptimizer;
 		this.accessOptimizer = accessOptimizer;
 	}
 
 	public InstantiationOptimizer getInstantiationOptimizer() {
 		return instantiationOptimizer;
 	}
 
 	public AccessOptimizer getAccessOptimizer() {
 		return accessOptimizer;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
index 25d19d303d..0c03fa93be 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
@@ -1,78 +1,80 @@
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
-package org.hibernate.bytecode.javassist;
-import java.io.IOException;
-import javassist.CannotCompileException;
-import javassist.ClassPool;
-import javassist.CtClass;
-import javassist.NotFoundException;
-import org.hibernate.HibernateException;
-
-/**
- * @author Steve Ebersole
- */
-public class TransformingClassLoader extends ClassLoader {
-	private ClassLoader parent;
-	private ClassPool classPool;
-
-	/*package*/ TransformingClassLoader(ClassLoader parent, String[] classpath) {
-		this.parent = parent;
-		classPool = new ClassPool( true );
-		for ( int i = 0; i < classpath.length; i++ ) {
-			try {
-				classPool.appendClassPath( classpath[i] );
-			}
-			catch ( NotFoundException e ) {
-				throw new HibernateException(
-						"Unable to resolve requested classpath for transformation [" +
-						classpath[i] + "] : " + e.getMessage()
-				);
-			}
-		}
-	}
-
-	protected Class findClass(String name) throws ClassNotFoundException {
-        try {
-            CtClass cc = classPool.get( name );
-	        // todo : modify the class definition if not already transformed...
-            byte[] b = cc.toBytecode();
-            return defineClass( name, b, 0, b.length );
-        }
-        catch ( NotFoundException e ) {
-            throw new ClassNotFoundException();
-        }
-        catch ( IOException e ) {
-            throw new ClassNotFoundException();
-        }
-        catch ( CannotCompileException e ) {
-            throw new ClassNotFoundException();
-        }
-    }
-
-	public void release() {
-		classPool = null;
-		parent = null;
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
+package org.hibernate.bytecode.internal.javassist;
+
+import java.io.IOException;
+
+import javassist.CannotCompileException;
+import javassist.ClassPool;
+import javassist.CtClass;
+import javassist.NotFoundException;
+
+import org.hibernate.HibernateException;
+
+/**
+ * @author Steve Ebersole
+ */
+public class TransformingClassLoader extends ClassLoader {
+	private ClassLoader parent;
+	private ClassPool classPool;
+
+	/*package*/ TransformingClassLoader(ClassLoader parent, String[] classpath) {
+		this.parent = parent;
+		classPool = new ClassPool( true );
+		for ( int i = 0; i < classpath.length; i++ ) {
+			try {
+				classPool.appendClassPath( classpath[i] );
+			}
+			catch ( NotFoundException e ) {
+				throw new HibernateException(
+						"Unable to resolve requested classpath for transformation [" +
+						classpath[i] + "] : " + e.getMessage()
+				);
+			}
+		}
+	}
+
+	protected Class findClass(String name) throws ClassNotFoundException {
+        try {
+            CtClass cc = classPool.get( name );
+	        // todo : modify the class definition if not already transformed...
+            byte[] b = cc.toBytecode();
+            return defineClass( name, b, 0, b.length );
+        }
+        catch ( NotFoundException e ) {
+            throw new ClassNotFoundException();
+        }
+        catch ( IOException e ) {
+            throw new ClassNotFoundException();
+        }
+        catch ( CannotCompileException e ) {
+            throw new ClassNotFoundException();
+        }
+    }
+
+	public void release() {
+		classPool = null;
+		parent = null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/package.html b/hibernate-core/src/main/java/org/hibernate/bytecode/package.html
index 20cd5da1a6..fecd6e3797 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/package.html
@@ -1,63 +1,59 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
-  ~
   -->
 
 <html>
 	<head></head>
 	<body>
 		<p>
 			This package defines the API for plugging in bytecode libraries
 			for usage by Hibernate.  Hibernate uses these bytecode libraries
 			in three scenarios:<ol>
 				<li>
 					<b>Reflection optimization</b> - to speed up the performance of
 					POJO entity and component conctruction and field/property access
 				</li>
 				<li>
 					<b>Proxy generation</b> - runtime building of proxies used for
 					deferred loading of lazy entities
 				</li>
 				<li>
 					<b>Field-level interception</b> - build-time instrumentation of entity
 					classes for the purpose of intercepting field-level access (read/write)
 					for both lazy loading and dirty tracking.
 				</li>
 			</ol>
 		</p>
 		<p>
-			Currently, both CGLIB and Javassist are supported out-of-the-box.
-		</p>
-		<p>
 			Note that for field-level interception, simply plugging in a new {@link BytecodeProvider}
 			is not enough for Hibernate to be able to recognize new providers.  You would additionally
 			need to make appropriate code changes to the {@link org.hibernate.intercept.Helper}
 			class.  This is because the detection of these enhanced classes is needed in a static
 			environment (i.e. outside the scope of any {@link org.hibernate.SessionFactory}.
 		</p>
 		<p>
 			Note that in the current form the ability to specify a different bytecode provider
 			is actually considered a global settings (global to the JVM).
 		</p>
 	</body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/AbstractClassTransformerImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/AbstractClassTransformerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
index 3068cb1794..b310ac2207 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/AbstractClassTransformerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/AbstractClassTransformerImpl.java
@@ -1,68 +1,69 @@
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
-package org.hibernate.bytecode;
-import java.security.ProtectionDomain;
-import org.hibernate.bytecode.util.ClassFilter;
-import org.hibernate.bytecode.util.FieldFilter;
-
-/**
- * Basic implementation of the {@link ClassTransformer} contract.
- *
- * @author Emmanuel Bernard
- * @author Steve Ebersole
- */
-public abstract class AbstractClassTransformerImpl implements ClassTransformer {
-
-	protected final ClassFilter classFilter;
-	protected final FieldFilter fieldFilter;
-
-	protected AbstractClassTransformerImpl(ClassFilter classFilter, FieldFilter fieldFilter) {
-		this.classFilter = classFilter;
-		this.fieldFilter = fieldFilter;
-	}
-
-	public byte[] transform(
-			ClassLoader loader,
-			String className,
-			Class classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer) {
-		// to be safe...
-		className = className.replace( '/', '.' );
-		if ( classFilter.shouldInstrumentClass( className ) ) {
-			return doTransform( loader, className, classBeingRedefined, protectionDomain, classfileBuffer );
-		}
-		else {
-			return classfileBuffer;
-		}
-	}
-
-	protected abstract byte[] doTransform(
-			ClassLoader loader,
-			String className,
-			Class classBeingRedefined,
-			ProtectionDomain protectionDomain,
-			byte[] classfileBuffer);
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
+package org.hibernate.bytecode.spi;
+
+import java.security.ProtectionDomain;
+
+import org.hibernate.bytecode.buildtime.spi.ClassFilter;
+import org.hibernate.bytecode.buildtime.spi.FieldFilter;
+
+/**
+ * Basic implementation of the {@link ClassTransformer} contract.
+ *
+ * @author Emmanuel Bernard
+ * @author Steve Ebersole
+ */
+public abstract class AbstractClassTransformerImpl implements ClassTransformer {
+
+	protected final ClassFilter classFilter;
+	protected final FieldFilter fieldFilter;
+
+	protected AbstractClassTransformerImpl(ClassFilter classFilter, FieldFilter fieldFilter) {
+		this.classFilter = classFilter;
+		this.fieldFilter = fieldFilter;
+	}
+
+	public byte[] transform(
+			ClassLoader loader,
+			String className,
+			Class classBeingRedefined,
+			ProtectionDomain protectionDomain,
+			byte[] classfileBuffer) {
+		// to be safe...
+		className = className.replace( '/', '.' );
+		if ( classFilter.shouldInstrumentClass( className ) ) {
+			return doTransform( loader, className, classBeingRedefined, protectionDomain, classfileBuffer );
+		}
+		else {
+			return classfileBuffer;
+		}
+	}
+
+	protected abstract byte[] doTransform(
+			ClassLoader loader,
+			String className,
+			Class classBeingRedefined,
+			ProtectionDomain protectionDomain,
+			byte[] classfileBuffer);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/BasicProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BasicProxyFactory.java
similarity index 84%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/BasicProxyFactory.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/BasicProxyFactory.java
index f2668cd909..41c6d136ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/BasicProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BasicProxyFactory.java
@@ -1,40 +1,38 @@
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
-package org.hibernate.bytecode;
-
-
-/**
- * A proxy factory for "basic proxy" generation
- *
- * @author Steve Ebersole
- */
-public interface BasicProxyFactory {
-	/**
-	 * Get a proxy reference.
-	 *
-	 * @return A proxy reference.
-	 */
-	public Object getProxy();
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
+package org.hibernate.bytecode.spi;
+
+/**
+ * A proxy factory for "basic proxy" generation
+ *
+ * @author Steve Ebersole
+ */
+public interface BasicProxyFactory {
+	/**
+	 * Get a proxy reference.
+	 *
+	 * @return A proxy reference.
+	 */
+	public Object getProxy();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/util/ByteCodeHelper.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ByteCodeHelper.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/util/ByteCodeHelper.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/ByteCodeHelper.java
index 84b548bf55..623f1079fd 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/util/ByteCodeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ByteCodeHelper.java
@@ -1,125 +1,126 @@
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
-package org.hibernate.bytecode.util;
-import java.io.BufferedInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.File;
-import java.io.FileInputStream;
-import java.io.IOException;
-import java.io.InputStream;
-import java.util.zip.ZipInputStream;
-
-/**
- * A helper for reading byte code from various input sources.
- *
- * @author Steve Ebersole
- */
-public class ByteCodeHelper {
-	private ByteCodeHelper() {
-	}
-
-	/**
-	 * Reads class byte array info from the given input stream.
-	 * <p/>
-	 * The stream is closed within this method!
-	 *
-	 * @param inputStream The stream containing the class binary; null will lead to an {@link IOException}
-	 *
-	 * @return The read bytes
-	 *
-	 * @throws IOException Indicates a problem accessing the given stream.
-	 */
-	public static byte[] readByteCode(InputStream inputStream) throws IOException {
-		if ( inputStream == null ) {
-			throw new IOException( "null input stream" );
-		}
-
-		byte[] buffer = new byte[409600];
-		byte[] classBytes = new byte[0];
-
-		try {
-			int r = inputStream.read( buffer );
-			while ( r >= buffer.length ) {
-				byte[] temp = new byte[ classBytes.length + buffer.length ];
-				// copy any previously read bytes into the temp array
-				System.arraycopy( classBytes, 0, temp, 0, classBytes.length );
-				// copy the just read bytes into the temp array (after the previously read)
-				System.arraycopy( buffer, 0, temp, classBytes.length, buffer.length );
-				classBytes = temp;
-				// read the next set of bytes into buffer
-				r = inputStream.read( buffer );
-			}
-			if ( r != -1 ) {
-				byte[] temp = new byte[ classBytes.length + r ];
-				// copy any previously read bytes into the temp array
-				System.arraycopy( classBytes, 0, temp, 0, classBytes.length );
-				// copy the just read bytes into the temp array (after the previously read)
-				System.arraycopy( buffer, 0, temp, classBytes.length, r );
-				classBytes = temp;
-			}
-		}
-		finally {
-			try {
-				inputStream.close();
-			}
-			catch (IOException ignore) {
-				// intentionally empty
-			}
-		}
-
-		return classBytes;
-	}
-
-	/**
-	 * Read class definition from a file.
-	 *
-	 * @param file The file to read.
-	 *
-	 * @return The class bytes
-	 *
-	 * @throws IOException Indicates a problem accessing the given stream.
-	 */
-	public static byte[] readByteCode(File file) throws IOException {
-		return ByteCodeHelper.readByteCode( new FileInputStream( file ) );
-	}
-
-	/**
-	 * Read class definition a zip (jar) file entry.
-	 *
-	 * @param zip The zip entry stream.
-	 * 
-	 * @return The class bytes
-	 *
-	 * @throws IOException Indicates a problem accessing the given stream.
-	 */
-	public static byte[] readByteCode(ZipInputStream zip) throws IOException {
-        ByteArrayOutputStream bout = new ByteArrayOutputStream();
-        InputStream in = new BufferedInputStream( zip );
-        int b;
-        while ( ( b = in.read() ) != -1 ) {
-            bout.write( b );
-        }
-        return bout.toByteArray();
-    }
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
+package org.hibernate.bytecode.spi;
+
+import java.io.BufferedInputStream;
+import java.io.ByteArrayOutputStream;
+import java.io.File;
+import java.io.FileInputStream;
+import java.io.IOException;
+import java.io.InputStream;
+import java.util.zip.ZipInputStream;
+
+/**
+ * A helper for reading byte code from various input sources.
+ *
+ * @author Steve Ebersole
+ */
+public class ByteCodeHelper {
+	private ByteCodeHelper() {
+	}
+
+	/**
+	 * Reads class byte array info from the given input stream.
+	 * <p/>
+	 * The stream is closed within this method!
+	 *
+	 * @param inputStream The stream containing the class binary; null will lead to an {@link IOException}
+	 *
+	 * @return The read bytes
+	 *
+	 * @throws IOException Indicates a problem accessing the given stream.
+	 */
+	public static byte[] readByteCode(InputStream inputStream) throws IOException {
+		if ( inputStream == null ) {
+			throw new IOException( "null input stream" );
+		}
+
+		byte[] buffer = new byte[409600];
+		byte[] classBytes = new byte[0];
+
+		try {
+			int r = inputStream.read( buffer );
+			while ( r >= buffer.length ) {
+				byte[] temp = new byte[ classBytes.length + buffer.length ];
+				// copy any previously read bytes into the temp array
+				System.arraycopy( classBytes, 0, temp, 0, classBytes.length );
+				// copy the just read bytes into the temp array (after the previously read)
+				System.arraycopy( buffer, 0, temp, classBytes.length, buffer.length );
+				classBytes = temp;
+				// read the next set of bytes into buffer
+				r = inputStream.read( buffer );
+			}
+			if ( r != -1 ) {
+				byte[] temp = new byte[ classBytes.length + r ];
+				// copy any previously read bytes into the temp array
+				System.arraycopy( classBytes, 0, temp, 0, classBytes.length );
+				// copy the just read bytes into the temp array (after the previously read)
+				System.arraycopy( buffer, 0, temp, classBytes.length, r );
+				classBytes = temp;
+			}
+		}
+		finally {
+			try {
+				inputStream.close();
+			}
+			catch (IOException ignore) {
+				// intentionally empty
+			}
+		}
+
+		return classBytes;
+	}
+
+	/**
+	 * Read class definition from a file.
+	 *
+	 * @param file The file to read.
+	 *
+	 * @return The class bytes
+	 *
+	 * @throws IOException Indicates a problem accessing the given stream.
+	 */
+	public static byte[] readByteCode(File file) throws IOException {
+		return ByteCodeHelper.readByteCode( new FileInputStream( file ) );
+	}
+
+	/**
+	 * Read class definition a zip (jar) file entry.
+	 *
+	 * @param zip The zip entry stream.
+	 * 
+	 * @return The class bytes
+	 *
+	 * @throws IOException Indicates a problem accessing the given stream.
+	 */
+	public static byte[] readByteCode(ZipInputStream zip) throws IOException {
+        ByteArrayOutputStream bout = new ByteArrayOutputStream();
+        InputStream in = new BufferedInputStream( zip );
+        int b;
+        while ( ( b = in.read() ) != -1 ) {
+            bout.write( b );
+        }
+        return bout.toByteArray();
+    }
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/BytecodeProvider.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeProvider.java
similarity index 81%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/BytecodeProvider.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeProvider.java
index df3ebcb881..f03bfc72ac 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/BytecodeProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/BytecodeProvider.java
@@ -1,72 +1,71 @@
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
-package org.hibernate.bytecode;
-import org.hibernate.bytecode.util.ClassFilter;
-import org.hibernate.bytecode.util.FieldFilter;
+package org.hibernate.bytecode.spi;
+
+import org.hibernate.bytecode.buildtime.spi.ClassFilter;
+import org.hibernate.bytecode.buildtime.spi.FieldFilter;
 
 /**
  * Contract for providers of bytecode services to Hibernate.
  * <p/>
  * Bytecode requirements break down into basically 3 areas<ol>
- * <li>proxy generation (both for runtime-lazy-loading and basic proxy generation)
- * {@link #getProxyFactoryFactory()}
- * <li>bean reflection optimization {@link #getReflectionOptimizer}
- * <li>field-access instrumentation {@link #getTransformer}
+ *     <li>proxy generation (both for runtime-lazy-loading and basic proxy generation) {@link #getProxyFactoryFactory()}</li>
+ *     <li>bean reflection optimization {@link #getReflectionOptimizer}</li>
+ *     <li>field-access instrumentation {@link #getTransformer}</li>
  * </ol>
  *
  * @author Steve Ebersole
  */
 public interface BytecodeProvider {
 	/**
 	 * Retrieve the specific factory for this provider capable of
 	 * generating run-time proxies for lazy-loading purposes.
 	 *
 	 * @return The provider specific factory.
 	 */
 	public ProxyFactoryFactory getProxyFactoryFactory();
 
 	/**
 	 * Retrieve the ReflectionOptimizer delegate for this provider
 	 * capable of generating reflection optimization components.
 	 *
 	 * @param clazz The class to be reflected upon.
 	 * @param getterNames Names of all property getters to be accessed via reflection.
 	 * @param setterNames Names of all property setters to be accessed via reflection.
 	 * @param types The types of all properties to be accessed.
 	 * @return The reflection optimization delegate.
 	 */
 	public ReflectionOptimizer getReflectionOptimizer(Class clazz, String[] getterNames, String[] setterNames, Class[] types);
 
 	/**
 	 * Generate a ClassTransformer capable of performing bytecode manipulation.
 	 *
 	 * @param classFilter filter used to limit which classes are to be instrumented
 	 * via this ClassTransformer.
 	 * @param fieldFilter filter used to limit which fields are to be instrumented
 	 * via this ClassTransformer.
 	 * @return The appropriate ClassTransformer.
 	 */
 	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/ClassTransformer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/ClassTransformer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
index e0831b61ae..0480c9b8f8 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/ClassTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ClassTransformer.java
@@ -1,56 +1,56 @@
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
-package org.hibernate.bytecode;
-import java.security.ProtectionDomain;
-
-/**
- * A persistence provider provides an instance of this interface
- * to the PersistenceUnitInfo.addTransformer method.
- * The supplied transformer instance will get called to transform
- * entity class files when they are loaded and redefined.  The transformation
- * occurs before the class is defined by the JVM
- *
- *
- * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
- * @author Emmanuel Bernard
- */
-public interface ClassTransformer
-{
-   /**
-	* Invoked when a class is being loaded or redefined to add hooks for persistence bytecode manipulation
-	*
-	* @param loader the defining class loaderof the class being transformed.  It may be null if using bootstrap loader
-	* @param classname The name of the class being transformed
-	* @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
-	* @param protectionDomain ProtectionDomain of the class being (re)-defined
-	* @param classfileBuffer The input byte buffer in class file format
-	* @return A well-formed class file that can be loaded
-	*/
-   public byte[] transform(ClassLoader loader,
-					String classname,
-					Class classBeingRedefined,
-					ProtectionDomain protectionDomain,
-					byte[] classfileBuffer);
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
+package org.hibernate.bytecode.spi;
+
+import java.security.ProtectionDomain;
+
+/**
+ * A persistence provider provides an instance of this interface
+ * to the PersistenceUnitInfo.addTransformer method.
+ * The supplied transformer instance will get called to transform
+ * entity class files when they are loaded and redefined.  The transformation
+ * occurs before the class is defined by the JVM
+ *
+ *
+ * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
+ * @author Emmanuel Bernard
+ */
+public interface ClassTransformer
+{
+   /**
+	* Invoked when a class is being loaded or redefined to add hooks for persistence bytecode manipulation
+	*
+	* @param loader the defining class loaderof the class being transformed.  It may be null if using bootstrap loader
+	* @param classname The name of the class being transformed
+	* @param classBeingRedefined If an already loaded class is being redefined, then pass this as a parameter
+	* @param protectionDomain ProtectionDomain of the class being (re)-defined
+	* @param classfileBuffer The input byte buffer in class file format
+	* @return A well-formed class file that can be loaded
+	*/
+   public byte[] transform(ClassLoader loader,
+					String classname,
+					Class classBeingRedefined,
+					ProtectionDomain protectionDomain,
+					byte[] classfileBuffer);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/InstrumentedClassLoader.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/InstrumentedClassLoader.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/InstrumentedClassLoader.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/InstrumentedClassLoader.java
index b4a4948067..033530527b 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/InstrumentedClassLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/InstrumentedClassLoader.java
@@ -1,76 +1,75 @@
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
-package org.hibernate.bytecode;
-import java.io.InputStream;
-import org.hibernate.bytecode.util.ByteCodeHelper;
-
-/**
- * A specialized classloader which performs bytecode enhancement on class
- * definitions as they are loaded into the classloader scope.
- *
- * @author Emmanuel Bernard
- * @author Steve Ebersole
- */
-public class InstrumentedClassLoader extends ClassLoader {
-
-	private ClassTransformer classTransformer;
-
-	public InstrumentedClassLoader(ClassLoader parent, ClassTransformer classTransformer) {
-		super( parent );
-		this.classTransformer = classTransformer;
-	}
-
-	public Class loadClass(String name) throws ClassNotFoundException {
-		if ( name.startsWith( "java." ) || classTransformer == null ) {
-			return getParent().loadClass( name );
-		}
-
-		Class c = findLoadedClass( name );
-		if ( c != null ) {
-			return c;
-		}
-
-		InputStream is = this.getResourceAsStream( name.replace( '.', '/' ) + ".class" );
-		if ( is == null ) {
-			throw new ClassNotFoundException( name + " not found" );
-		}
-
-		try {
-			byte[] originalBytecode = ByteCodeHelper.readByteCode( is );
-			byte[] transformedBytecode = classTransformer.transform( getParent(), name, null, null, originalBytecode );
-			if ( originalBytecode == transformedBytecode ) {
-				// no transformations took place, so handle it as we would a
-				// non-instrumented class
-				return getParent().loadClass( name );
-			}
-			else {
-				return defineClass( name, transformedBytecode, 0, transformedBytecode.length );
-			}
-		}
-		catch( Throwable t ) {
-			throw new ClassNotFoundException( name + " not found", t );
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
+package org.hibernate.bytecode.spi;
+
+import java.io.InputStream;
+
+/**
+ * A specialized classloader which performs bytecode enhancement on class
+ * definitions as they are loaded into the classloader scope.
+ *
+ * @author Emmanuel Bernard
+ * @author Steve Ebersole
+ */
+public class InstrumentedClassLoader extends ClassLoader {
+
+	private ClassTransformer classTransformer;
+
+	public InstrumentedClassLoader(ClassLoader parent, ClassTransformer classTransformer) {
+		super( parent );
+		this.classTransformer = classTransformer;
+	}
+
+	public Class loadClass(String name) throws ClassNotFoundException {
+		if ( name.startsWith( "java." ) || classTransformer == null ) {
+			return getParent().loadClass( name );
+		}
+
+		Class c = findLoadedClass( name );
+		if ( c != null ) {
+			return c;
+		}
+
+		InputStream is = this.getResourceAsStream( name.replace( '.', '/' ) + ".class" );
+		if ( is == null ) {
+			throw new ClassNotFoundException( name + " not found" );
+		}
+
+		try {
+			byte[] originalBytecode = ByteCodeHelper.readByteCode( is );
+			byte[] transformedBytecode = classTransformer.transform( getParent(), name, null, null, originalBytecode );
+			if ( originalBytecode == transformedBytecode ) {
+				// no transformations took place, so handle it as we would a
+				// non-instrumented class
+				return getParent().loadClass( name );
+			}
+			else {
+				return defineClass( name, transformedBytecode, 0, transformedBytecode.length );
+			}
+		}
+		catch( Throwable t ) {
+			throw new ClassNotFoundException( name + " not found", t );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/ProxyFactoryFactory.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ProxyFactoryFactory.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/ProxyFactoryFactory.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/ProxyFactoryFactory.java
index 600e1219c9..e9a831593c 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/ProxyFactoryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ProxyFactoryFactory.java
@@ -1,60 +1,60 @@
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
-package org.hibernate.bytecode;
+package org.hibernate.bytecode.spi;
+
 import org.hibernate.proxy.ProxyFactory;
 
 /**
  * An interface for factories of {@link ProxyFactory proxy factory} instances.
  * <p/>
  * Currently used to abstract from the tupizer whether we are using CGLIB or
  * Javassist for lazy proxy generation.
  *
  * @author Steve Ebersole
  */
 public interface ProxyFactoryFactory {
 	/**
 	 * Build a proxy factory specifically for handling runtime
 	 * lazy loading.
 	 *
 	 * @return The lazy-load proxy factory.
 	 */
 	public ProxyFactory buildProxyFactory();
 
 	/**
 	 * Build a proxy factory for basic proxy concerns.  The return
 	 * should be capable of properly handling newInstance() calls.
 	 * <p/>
 	 * Should build basic proxies essentially equivalent to JDK proxies in
 	 * terms of capabilities, but should be able to deal with abstract super
 	 * classes in addition to proxy interfaces.
 	 * <p/>
 	 * Must pass in either superClass or interfaces (or both).
 	 *
 	 * @param superClass The abstract super class (or null if none).
 	 * @param interfaces Interfaces to be proxied (or null if none).
 	 * @return The proxy class
 	 */
 	public BasicProxyFactory buildBasicProxyFactory(Class superClass, Class[] interfaces);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/ReflectionOptimizer.java b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ReflectionOptimizer.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/bytecode/ReflectionOptimizer.java
rename to hibernate-core/src/main/java/org/hibernate/bytecode/spi/ReflectionOptimizer.java
index 1cf3cc1370..8205ede743 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/ReflectionOptimizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/spi/ReflectionOptimizer.java
@@ -1,60 +1,58 @@
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
-package org.hibernate.bytecode;
-
+package org.hibernate.bytecode.spi;
 
 /**
  * Represents reflection optimization for a particular class.
  *
  * @author Steve Ebersole
  */
 public interface ReflectionOptimizer {
 
 	public InstantiationOptimizer getInstantiationOptimizer();
 	public AccessOptimizer getAccessOptimizer();
 
 	/**
 	 * Represents optimized entity instantiation.
 	 */
 	public static interface InstantiationOptimizer {
 		/**
 		 * Perform instantiation of an instance of the underlying class.
 		 *
 		 * @return The new instance.
 		 */
 		public Object newInstance();
 	}
 
 	/**
 	 * Represents optimized entity property access.
 	 *
 	 * @author Steve Ebersole
 	 */
 	public interface AccessOptimizer {
 		public String[] getPropertyNames();
 		public Object[] getPropertyValues(Object object);
 		public void setPropertyValues(Object object, Object[] values);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index 945ce45fc8..cc3c8d36bc 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,805 +1,805 @@
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
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.Timestamp;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.Version;
-import org.hibernate.bytecode.BytecodeProvider;
+import org.hibernate.bytecode.spi.BytecodeProvider;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.jboss.logging.Logger;
 
 
 /**
  * Provides access to configuration info passed in <tt>Properties</tt> objects.
  * <br><br>
  * Hibernate has two property scopes:
  * <ul>
  * <li><b>Factory-level</b> properties may be passed to the <tt>SessionFactory</tt> when it
  * instantiated. Each instance might have different property values. If no
  * properties are specified, the factory calls <tt>Environment.getProperties()</tt>.
  * <li><b>System-level</b> properties are shared by all factory instances and are always
  * determined by the <tt>Environment</tt> properties.
  * </ul>
  * The only system-level properties are
  * <ul>
  * <li><tt>hibernate.jdbc.use_streams_for_binary</tt>
  * <li><tt>hibernate.cglib.use_reflection_optimizer</tt>
  * </ul>
  * <tt>Environment</tt> properties are populated by calling <tt>System.getProperties()</tt>
  * and then from a resource named <tt>/hibernate.properties</tt> if it exists. System
  * properties override properties specified in <tt>hibernate.properties</tt>.<br>
  * <br>
  * The <tt>SessionFactory</tt> is controlled by the following properties.
  * Properties may be either be <tt>System</tt> properties, properties
  * defined in a resource named <tt>/hibernate.properties</tt> or an instance of
  * <tt>java.util.Properties</tt> passed to
  * <tt>Configuration.buildSessionFactory()</tt><br>
  * <br>
  * <table>
  * <tr><td><b>property</b></td><td><b>meaning</b></td></tr>
  * <tr>
  *   <td><tt>hibernate.dialect</tt></td>
  *   <td>classname of <tt>org.hibernate.dialect.Dialect</tt> subclass</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.cache.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.cache.CacheProvider</tt>
  *   subclass (if not specified EHCache is used)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.service.jdbc.connections.spi.ConnectionProvider</tt>
  *   subclass (if not specified hueristics are used)</td>
  * </tr>
  * <tr><td><tt>hibernate.connection.username</tt></td><td>database username</td></tr>
  * <tr><td><tt>hibernate.connection.password</tt></td><td>database password</td></tr>
  * <tr>
  *   <td><tt>hibernate.connection.url</tt></td>
  *   <td>JDBC URL (when using <tt>java.sql.DriverManager</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.driver_class</tt></td>
  *   <td>classname of JDBC driver</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.isolation</tt></td>
  *   <td>JDBC transaction isolation level (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  *   <td><tt>hibernate.connection.pool_size</tt></td>
  *   <td>the maximum size of the connection pool (only when using
  *     <tt>java.sql.DriverManager</tt>)
  *   </td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.datasource</tt></td>
  *   <td>databasource JNDI name (when using <tt>javax.sql.Datasource</tt>)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.url</tt></td><td>JNDI <tt>InitialContext</tt> URL</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jndi.class</tt></td><td>JNDI <tt>InitialContext</tt> classname</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.max_fetch_depth</tt></td>
  *   <td>maximum depth of outer join fetching</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.batch_size</tt></td>
  *   <td>enable use of JDBC2 batch API for drivers which support it</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.fetch_size</tt></td>
  *   <td>set the JDBC fetch size</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_scrollable_resultset</tt></td>
  *   <td>enable use of JDBC2 scrollable resultsets (you only need this specify
  *   this property when using user supplied connections)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.jdbc.use_getGeneratedKeys</tt></td>
  *   <td>enable use of JDBC3 PreparedStatement.getGeneratedKeys() to retrieve
  *   natively generated keys after insert. Requires JDBC3+ driver and JRE1.4+</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.hbm2ddl.auto</tt></td>
  *   <td>enable auto DDL export</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_schema</tt></td>
  *   <td>use given schema name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.default_catalog</tt></td>
  *   <td>use given catalog name for unqualified tables (always optional)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.session_factory_name</tt></td>
  *   <td>If set, the factory attempts to bind this name to itself in the
  *   JNDI context. This name is also used to support cross JVM <tt>
  *   Session</tt> (de)serialization.</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.manager_lookup_class</tt></td>
  *   <td>classname of <tt>org.hibernate.transaction.TransactionManagerLookup</tt>
  *   implementor</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.transaction.factory_class</tt></td>
  *   <td>the factory to use for instantiating <tt>Transaction</tt>s.
  *   (Defaults to <tt>JdbcTransactionFactory</tt>.)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.query.substitutions</tt></td><td>query language token substitutions</td>
  * </tr>
  * </table>
  *
  * @see org.hibernate.SessionFactory
  * @author Gavin King
  */
 public final class Environment {
 	/**
 	 * <tt>ConnectionProvider</tt> implementor to use when obtaining connections
 	 */
 	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 	/**
 	 * JDBC driver class
 	 */
 	public static final String DRIVER ="hibernate.connection.driver_class";
 	/**
 	 * JDBC transaction isolation level
 	 */
 	public static final String ISOLATION ="hibernate.connection.isolation";
 	/**
 	 * JDBC URL
 	 */
 	public static final String URL ="hibernate.connection.url";
 	/**
 	 * JDBC user
 	 */
 	public static final String USER ="hibernate.connection.username";
 	/**
 	 * JDBC password
 	 */
 	public static final String PASS ="hibernate.connection.password";
 	/**
 	 * JDBC autocommit mode
 	 */
 	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
 	/**
 	 * Maximum number of inactive connections for Hibernate's connection pool
 	 */
 	public static final String POOL_SIZE ="hibernate.connection.pool_size";
 	/**
 	 * <tt>java.sql.Datasource</tt> JNDI name
 	 */
 	public static final String DATASOURCE ="hibernate.connection.datasource";
 	/**
 	 * prefix for arbitrary JDBC connection properties
 	 */
 	public static final String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>
 	 */
 	public static final String JNDI_CLASS ="hibernate.jndi.class";
 	/**
 	 * JNDI provider URL, <tt>Context.PROVIDER_URL</tt>
 	 */
 	public static final String JNDI_URL ="hibernate.jndi.url";
 	/**
 	 * prefix for arbitrary JNDI <tt>InitialContext</tt> properties
 	 */
 	public static final String JNDI_PREFIX = "hibernate.jndi";
 	/**
 	 * JNDI name to bind to <tt>SessionFactory</tt>
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Hibernate SQL {@link org.hibernate.dialect.Dialect} class
 	 */
 	public static final String DIALECT ="hibernate.dialect";
 
 	/**
 	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} classes to register with the
 	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}
 	 */
 	public static final String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
 
 	/**
 	 * A default database schema (owner) name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
 	/**
 	 * A default database catalog name to use for unqualified tablenames
 	 */
 	public static final String DEFAULT_CATALOG = "hibernate.default_catalog";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	public static final String SHOW_SQL ="hibernate.show_sql";
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	public static final String FORMAT_SQL ="hibernate.format_sql";
 	/**
 	 * Add comments to the generated SQL
 	 */
 	public static final String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 	/**
 	 * The default batch size for batch fetching
 	 */
 	public static final String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	public static final String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 	/**
 	 * An XSLT resource used to generate "custom" XML
 	 */
 	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 	/**
 	 * Idle time before a C3P0 pooled connection is validated
 	 */
 	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 */
 	public static final String PROXOOL_PREFIX = "hibernate.proxool";
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	public static final String PROXOOL_XML = "hibernate.proxool.xml";
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	public static final String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	public static final String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 	/**
 	 * Specifies how Hibernate should release JDBC connections.
 	 */
 	public static final String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	public static final String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 	/**
 	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionContext} to use for
 	 * creating {@link org.hibernate.Transaction} instances
 	 */
 	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 	/**
 	 * <tt>TransactionManagerLookup</tt> implementor to use for obtaining the <tt>TransactionManager</tt>
 	 */
 	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
 	/**
 	 * JNDI name of JTA <tt>UserTransaction</tt> object
 	 */
 	public static final String USER_TRANSACTION = "jta.UserTransaction";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
 
 	/**
 	 * The {@link org.hibernate.cache.RegionFactory} implementation class
 	 */
 	public static final String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 	/**
 	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
 	 */
 	public static final String CACHE_NAMESPACE = "hibernate.cache.jndi";
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 	/**
 	 * The <tt>QueryCacheFactory</tt> implementation class.
 	 */
 	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	public static final String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	public static final String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 
 	/**
 	 * Enable statistics collection
 	 */
 	public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	public static final String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	public static final String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	public static final String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
 	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
 	 */
 	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed before the statements of the
 	 * following files.
 	 *
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 *
 	 * The default value is <tt>/import.sql</tt>
 	 */
 	public static final String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * The {@link org.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	public static final String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	public static final String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	public static final String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
     public static final String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
     /**
      * The jacc context id of the deployment
      */
     public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
 
 	/**
 	 * Should all database identifiers be quoted.
 	 */
 	public static final String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	public static final String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	public static final String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	public static final String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 */
 	public static final String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
 	private static final boolean JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
 	private static final boolean JVM_HAS_JDK14_TIMESTAMP;
 	private static final boolean JVM_SUPPORTS_GET_GENERATED_KEYS;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final HashMap ISOLATION_LEVELS = new HashMap();
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Environment.class.getName());
 
 	/**
 	 * Issues warnings to the user when any obsolete or renamed property names are used.
 	 *
 	 * @param props The specified properties.
 	 */
 	public static void verifyProperties(Properties props) {
 		Iterator iter = props.keySet().iterator();
 		Map propertiesToAdd = new HashMap();
 		while ( iter.hasNext() ) {
 			final Object propertyName = iter.next();
 			Object newPropertyName = OBSOLETE_PROPERTIES.get( propertyName );
             if (newPropertyName != null) LOG.unsupportedProperty(propertyName, newPropertyName);
 			newPropertyName = RENAMED_PROPERTIES.get( propertyName );
 			if ( newPropertyName != null ) {
                 LOG.renamedProperty(propertyName, newPropertyName);
 				if ( ! props.containsKey( newPropertyName ) ) {
 					propertiesToAdd.put( newPropertyName, props.get( propertyName ) );
 				}
 			}
 		}
 		props.putAll(propertiesToAdd);
 	}
 
 	static {
 
         LOG.version(Version.getVersionString());
 
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_NONE), "NONE" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_UNCOMMITTED), "READ_UNCOMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_COMMITTED), "READ_COMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_REPEATABLE_READ), "REPEATABLE_READ" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_SERIALIZABLE), "SERIALIZABLE" );
 
 		GLOBAL_PROPERTIES = new Properties();
 		//Set USE_REFLECTION_OPTIMIZER to false to fix HHH-227
 		GLOBAL_PROPERTIES.setProperty( USE_REFLECTION_OPTIMIZER, Boolean.FALSE.toString() );
 
 		try {
 			InputStream stream = ConfigHelper.getResourceAsStream( "/hibernate.properties" );
 			try {
 				GLOBAL_PROPERTIES.load(stream);
                 LOG.propertiesLoaded(ConfigurationHelper.maskOut(GLOBAL_PROPERTIES, PASS));
 			}
 			catch (Exception e) {
                 LOG.unableToloadProperties();
 			}
 			finally {
 				try{
 					stream.close();
 				}
 				catch (IOException ioe){
                     LOG.unableToCloseStreamError(ioe);
 				}
 			}
 		}
 		catch (HibernateException he) {
             LOG.propertiesNotFound();
 		}
 
 		try {
 			GLOBAL_PROPERTIES.putAll( System.getProperties() );
 		}
 		catch (SecurityException se) {
             LOG.unableToCopySystemProperties();
 		}
 
 		verifyProperties(GLOBAL_PROPERTIES);
 
 		ENABLE_BINARY_STREAMS = ConfigurationHelper.getBoolean(USE_STREAMS_FOR_BINARY, GLOBAL_PROPERTIES);
 		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
 
         if (ENABLE_BINARY_STREAMS) LOG.usingStreams();
         if (ENABLE_REFLECTION_OPTIMIZER) LOG.usingReflectionOptimizer();
 		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		boolean getGeneratedKeysSupport;
 		try {
 			Statement.class.getMethod("getGeneratedKeys", (Class[])null);
 			getGeneratedKeysSupport = true;
 		}
 		catch (NoSuchMethodException nsme) {
 			getGeneratedKeysSupport = false;
 		}
 		JVM_SUPPORTS_GET_GENERATED_KEYS = getGeneratedKeysSupport;
         if (!JVM_SUPPORTS_GET_GENERATED_KEYS) LOG.generatedKeysNotSupported();
 
 		boolean linkedHashSupport;
 		try {
 			Class.forName("java.util.LinkedHashSet");
 			linkedHashSupport = true;
 		}
 		catch (ClassNotFoundException cnfe) {
 			linkedHashSupport = false;
 		}
 		JVM_SUPPORTS_LINKED_HASH_COLLECTIONS = linkedHashSupport;
         if (!JVM_SUPPORTS_LINKED_HASH_COLLECTIONS) LOG.linkedMapsAndSetsNotSupported();
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
         if (JVM_HAS_TIMESTAMP_BUG) LOG.usingTimestampWorkaround();
 
 		Timestamp t = new Timestamp(0);
 		t.setNanos(5 * 1000000);
 		JVM_HAS_JDK14_TIMESTAMP = t.getTime() == 5;
         if (JVM_HAS_JDK14_TIMESTAMP) LOG.usingJdk14TimestampHandling();
         else LOG.usingPreJdk14TimestampHandling();
 	}
 
 	public static BytecodeProvider getBytecodeProvider() {
 		return BYTECODE_PROVIDER_INSTANCE;
 	}
 
 	/**
 	 * Does this JVM's implementation of {@link java.sql.Timestamp} have a bug in which the following is true:<code>
 	 * new java.sql.Timestamp( x ).getTime() != x
 	 * </code>
 	 * <p/>
 	 * NOTE : IBM JDK 1.3.1 the only known JVM to exhibit this behavior.
 	 *
 	 * @return True if the JVM's {@link Timestamp} implementa
 	 */
 	public static boolean jvmHasTimestampBug() {
 		return JVM_HAS_TIMESTAMP_BUG;
 	}
 
 	/**
 	 * Does this JVM handle {@link java.sql.Timestamp} in the JDK 1.4 compliant way wrt to nano rolling>
 	 *
 	 * @return True if the JDK 1.4 (JDBC3) specification for {@link java.sql.Timestamp} nano rolling is adhered to.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	@Deprecated
     public static boolean jvmHasJDK14Timestamp() {
 		return JVM_HAS_JDK14_TIMESTAMP;
 	}
 
 	/**
 	 * Does this JVM support {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap}?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap} are available.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 * @see java.util.LinkedHashSet
 	 * @see java.util.LinkedHashMap
 	 */
 	@Deprecated
     public static boolean jvmSupportsLinkedHashCollections() {
 		return JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	}
 
 	/**
 	 * Does this JDK/JVM define the JDBC {@link Statement} interface with a 'getGeneratedKeys' method?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if generated keys can be retrieved via Statement; false otherwise.
 	 *
 	 * @see Statement
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	@Deprecated
     public static boolean jvmSupportsGetGeneratedKeys() {
 		return JVM_SUPPORTS_GET_GENERATED_KEYS;
 	}
 
 	/**
 	 * Should we use streams to bind binary types to JDBC IN parameters?
 	 *
 	 * @return True if streams should be used for binary data handling; false otherwise.
 	 *
 	 * @see #USE_STREAMS_FOR_BINARY
 	 */
 	public static boolean useStreamsForBinary() {
 		return ENABLE_BINARY_STREAMS;
 	}
 
 	/**
 	 * Should we use reflection optimization?
 	 *
 	 * @return True if reflection optimization should be used; false otherwise.
 	 *
 	 * @see #USE_REFLECTION_OPTIMIZER
 	 * @see #getBytecodeProvider()
 	 * @see BytecodeProvider#getReflectionOptimizer
 	 */
 	public static boolean useReflectionOptimizer() {
 		return ENABLE_REFLECTION_OPTIMIZER;
 	}
 
 	/**
 	 * Disallow instantiation
 	 */
 	private Environment() {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Return <tt>System</tt> properties, extended by any properties specified
 	 * in <tt>hibernate.properties</tt>.
 	 * @return Properties
 	 */
 	public static Properties getProperties() {
 		Properties copy = new Properties();
 		copy.putAll(GLOBAL_PROPERTIES);
 		return copy;
 	}
 
 	/**
 	 * Get the name of a JDBC transaction isolation level
 	 *
 	 * @see java.sql.Connection
 	 * @param isolation as defined by <tt>java.sql.Connection</tt>
 	 * @return a human-readable name
 	 */
 	public static String isolationLevelToString(int isolation) {
 		return (String) ISOLATION_LEVELS.get( new Integer(isolation) );
 	}
 
 	public static BytecodeProvider buildBytecodeProvider(Properties properties) {
 		String provider = ConfigurationHelper.getString( BYTECODE_PROVIDER, properties, "javassist" );
         LOG.bytecodeProvider(provider);
 		return buildBytecodeProvider( provider );
 	}
 
 	private static BytecodeProvider buildBytecodeProvider(String providerName) {
 		if ( "javassist".equals( providerName ) ) {
-			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
+			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 		}
 
         LOG.unknownBytecodeProvider( providerName );
-		return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
+		return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
index 88306c0e60..99c6b2f3cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyContainer.java
@@ -1,279 +1,279 @@
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
 
 // $Id$
 
 package org.hibernate.cfg;
 
 import java.util.Collection;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 import java.util.TreeMap;
 import javax.persistence.Access;
 import javax.persistence.ManyToMany;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.Transient;
 import org.hibernate.AnnotationException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.Target;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.internal.util.StringHelper;
 import org.jboss.logging.Logger;
 
 /**
  * A helper class to keep the {@code XProperty}s of a class ordered by access type.
  *
  * @author Hardy Ferentschik
  */
 class PropertyContainer {
 
     static {
         System.setProperty("jboss.i18n.generate-proxies", "true");
     }
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, PropertyContainer.class.getName());
 
 	private final AccessType explicitClassDefinedAccessType;
 
 	/**
 	 * Constains the properties which must be returned in case the class is accessed via {@code AccessType.FIELD}. Note,
 	 * this does not mean that all {@code XProperty}s in this map are fields. Due to JPA access rules single properties
 	 * can have different access type than the overall class access type.
 	 */
 	private final TreeMap<String, XProperty> fieldAccessMap;
 
 	/**
 	 * Constains the properties which must be returned in case the class is accessed via {@code AccessType.Property}. Note,
 	 * this does not mean that all {@code XProperty}s in this map are properties/methods. Due to JPA access rules single properties
 	 * can have different access type than the overall class access type.
 	 */
 	private final TreeMap<String, XProperty> propertyAccessMap;
 
 	/**
 	 * The class for which this container is created.
 	 */
 	private final XClass xClass;
 	private final XClass entityAtStake;
 
 	PropertyContainer(XClass clazz, XClass entityAtStake) {
 		this.xClass = clazz;
 		this.entityAtStake = entityAtStake;
 
 		explicitClassDefinedAccessType = determineClassDefinedAccessStrategy();
 
 		// first add all properties to field and property map
 		fieldAccessMap = initProperties( AccessType.FIELD );
 		propertyAccessMap = initProperties( AccessType.PROPERTY );
 
 		considerExplicitFieldAndPropertyAccess();
 	}
 
 	public XClass getEntityAtStake() {
 		return entityAtStake;
 	}
 
 	public XClass getDeclaringClass() {
 		return xClass;
 	}
 
 	public AccessType getExplicitAccessStrategy() {
 		return explicitClassDefinedAccessType;
 	}
 
 	public boolean hasExplicitAccessStrategy() {
 		return !explicitClassDefinedAccessType.equals( AccessType.DEFAULT );
 	}
 
 	public Collection<XProperty> getProperties(AccessType accessType) {
 		assertTypesAreResolvable( accessType );
 		if ( AccessType.DEFAULT == accessType || AccessType.PROPERTY == accessType ) {
 			return Collections.unmodifiableCollection( propertyAccessMap.values() );
 		}
 		else {
 			return Collections.unmodifiableCollection( fieldAccessMap.values() );
 		}
 	}
 
 	private void assertTypesAreResolvable(AccessType access) {
 		Map<String, XProperty> xprops;
 		if ( AccessType.PROPERTY.equals( access ) || AccessType.DEFAULT.equals( access ) ) {
 			xprops = propertyAccessMap;
 		}
 		else {
 			xprops = fieldAccessMap;
 		}
 		for ( XProperty property : xprops.values() ) {
 			if ( !property.isTypeResolved() && !discoverTypeWithoutReflection( property ) ) {
 				String msg = "Property " + StringHelper.qualify( xClass.getName(), property.getName() ) +
 						" has an unbound type and no explicit target entity. Resolve this Generic usage issue" +
 						" or set an explicit target attribute (eg @OneToMany(target=) or use an explicit @Type";
 				throw new AnnotationException( msg );
 			}
 		}
 	}
 
 	private void considerExplicitFieldAndPropertyAccess() {
 		for ( XProperty property : fieldAccessMap.values() ) {
 			Access access = property.getAnnotation( Access.class );
 			if ( access == null ) {
 				continue;
 			}
 
 			// see "2.3.2 Explicit Access Type" of JPA 2 spec
 			// the access type for this property is explicitly set to AccessType.FIELD, hence we have to
 			// use field access for this property even if the default access type for the class is AccessType.PROPERTY
 			AccessType accessType = AccessType.getAccessStrategy( access.value() );
             if (accessType == AccessType.FIELD) propertyAccessMap.put(property.getName(), property);
             else LOG.annotationHasNoEffect(AccessType.FIELD);
 		}
 
 		for ( XProperty property : propertyAccessMap.values() ) {
 			Access access = property.getAnnotation( Access.class );
 			if ( access == null ) {
 				continue;
 			}
 
 			AccessType accessType = AccessType.getAccessStrategy( access.value() );
 
 			// see "2.3.2 Explicit Access Type" of JPA 2 spec
 			// the access type for this property is explicitly set to AccessType.PROPERTY, hence we have to
 			// return use method access even if the default class access type is AccessType.FIELD
             if (accessType == AccessType.PROPERTY) fieldAccessMap.put(property.getName(), property);
             else LOG.annotationHasNoEffect(AccessType.PROPERTY);
 		}
 	}
 
 	/**
 	 * Retrieves all properties from the {@code xClass} with the specified access type. This method does not take
 	 * any jpa access rules/annotations into account yet.
 	 *
 	 * @param access The access type - {@code AccessType.FIELD}  or {@code AccessType.Property}
 	 *
 	 * @return A maps of the properties with the given access type keyed against their property name
 	 */
 	private TreeMap<String, XProperty> initProperties(AccessType access) {
 		if ( !( AccessType.PROPERTY.equals( access ) || AccessType.FIELD.equals( access ) ) ) {
 			throw new IllegalArgumentException( "Acces type has to be AccessType.FIELD or AccessType.Property" );
 		}
 
 		//order so that property are used in the same order when binding native query
 		TreeMap<String, XProperty> propertiesMap = new TreeMap<String, XProperty>();
 		List<XProperty> properties = xClass.getDeclaredProperties( access.getType() );
 		for ( XProperty property : properties ) {
 			if ( mustBeSkipped( property ) ) {
 				continue;
 			}
 			propertiesMap.put( property.getName(), property );
 		}
 		return propertiesMap;
 	}
 
 	private AccessType determineClassDefinedAccessStrategy() {
 		AccessType classDefinedAccessType;
 
 		AccessType hibernateDefinedAccessType = AccessType.DEFAULT;
 		AccessType jpaDefinedAccessType = AccessType.DEFAULT;
 
 		org.hibernate.annotations.AccessType accessType = xClass.getAnnotation( org.hibernate.annotations.AccessType.class );
 		if ( accessType != null ) {
 			hibernateDefinedAccessType = AccessType.getAccessStrategy( accessType.value() );
 		}
 
 		Access access = xClass.getAnnotation( Access.class );
 		if ( access != null ) {
 			jpaDefinedAccessType = AccessType.getAccessStrategy( access.value() );
 		}
 
 		if ( hibernateDefinedAccessType != AccessType.DEFAULT
 				&& jpaDefinedAccessType != AccessType.DEFAULT
 				&& hibernateDefinedAccessType != jpaDefinedAccessType ) {
 			throw new MappingException(
 					"@AccessType and @Access specified with contradicting values. Use of @Access only is recommended. "
 			);
 		}
 
 		if ( hibernateDefinedAccessType != AccessType.DEFAULT ) {
 			classDefinedAccessType = hibernateDefinedAccessType;
 		}
 		else {
 			classDefinedAccessType = jpaDefinedAccessType;
 		}
 		return classDefinedAccessType;
 	}
 
 	private static boolean discoverTypeWithoutReflection(XProperty p) {
 		if ( p.isAnnotationPresent( OneToOne.class ) && !p.getAnnotation( OneToOne.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( OneToMany.class ) && !p.getAnnotation( OneToMany.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( ManyToOne.class ) && !p.getAnnotation( ManyToOne.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( ManyToMany.class ) && !p.getAnnotation( ManyToMany.class )
 				.targetEntity()
 				.equals( void.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( org.hibernate.annotations.Any.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( ManyToAny.class ) ) {
 			if ( !p.isCollection() && !p.isArray() ) {
 				throw new AnnotationException( "@ManyToAny used on a non collection non array property: " + p.getName() );
 			}
 			return true;
 		}
 		else if ( p.isAnnotationPresent( Type.class ) ) {
 			return true;
 		}
 		else if ( p.isAnnotationPresent( Target.class ) ) {
 			return true;
 		}
 		return false;
 	}
 
 	private static boolean mustBeSkipped(XProperty property) {
 		//TODO make those hardcoded tests more portable (through the bytecode provider?)
 		return property.isAnnotationPresent( Transient.class )
 				|| "net.sf.cglib.transform.impl.InterceptFieldCallback".equals( property.getType().getName() )
-				|| "org.hibernate.bytecode.javassist.FieldHandler".equals( property.getType().getName() );
+				|| "org.hibernate.bytecode.internal.javassist.FieldHandler".equals( property.getType().getName() );
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index ab409ec5e4..0a222063e4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,326 +1,325 @@
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
-import org.hibernate.bytecode.BytecodeProvider;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
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
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
-//			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
+//			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debugf("Using javassist as bytecode provider by default");
-//			return new org.hibernate.bytecode.javassist.BytecodeProviderImpl();
+//			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
index 9c1a082de7..8d50a52c1e 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
@@ -1,389 +1,389 @@
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
 package org.hibernate.engine;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
-import org.hibernate.intercept.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * We need an entry to tell us all about the current state
  * of an object with respect to its persistent state
  * 
  * @author Gavin King
  */
 public final class EntityEntry implements Serializable {
 
 	private LockMode lockMode;
 	private Status status;
 	private Status previousStatus;
 	private final Serializable id;
 	private Object[] loadedState;
 	private Object[] deletedState;
 	private boolean existsInDatabase;
 	private Object version;
 	private transient EntityPersister persister; // for convenience to save some lookups
 	private final EntityMode entityMode;
 	private final String entityName;
 	private transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
 	private boolean isBeingReplicated;
 	private boolean loadedWithLazyPropertiesUnfetched; //NOTE: this is not updated when properties are fetched lazily!
 	private final transient Object rowId;
 
 	EntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final EntityMode entityMode,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched) {
 		this.status=status;
 		this.previousStatus = null;
 		// only retain loaded state if the status is not Status.READ_ONLY
 		if ( status != Status.READ_ONLY ) { this.loadedState = loadedState; }
 		this.id=id;
 		this.rowId=rowId;
 		this.existsInDatabase=existsInDatabase;
 		this.version=version;
 		this.lockMode=lockMode;
 		this.isBeingReplicated=disableVersionIncrement;
 		this.loadedWithLazyPropertiesUnfetched = lazyPropertiesAreUnfetched;
 		this.persister=persister;
 		this.entityMode = entityMode;
 		this.entityName = persister == null ? null : persister.getEntityName();
 	}
 
 	private EntityEntry(
 			final SessionFactoryImplementor factory,
 			final String entityName,
 			final Serializable id,
 			final EntityMode entityMode,
 			final Status status,
 			final Status previousStatus,
 			final Object[] loadedState,
 	        final Object[] deletedState,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final boolean isBeingReplicated,
 			final boolean loadedWithLazyPropertiesUnfetched) {
 		// Used during custom deserialization
 		this.entityName = entityName;
 		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
 		this.id = id;
 		this.entityMode = entityMode;
 		this.status = status;
 		this.previousStatus = previousStatus;
 		this.loadedState = loadedState;
 		this.deletedState = deletedState;
 		this.version = version;
 		this.lockMode = lockMode;
 		this.existsInDatabase = existsInDatabase;
 		this.isBeingReplicated = isBeingReplicated;
 		this.loadedWithLazyPropertiesUnfetched = loadedWithLazyPropertiesUnfetched;
 		this.rowId = null; // this is equivalent to the old behavior...
 	}
 
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	public void setLockMode(LockMode lockMode) {
 		this.lockMode = lockMode;
 	}
 
 	public Status getStatus() {
 		return status;
 	}
 
 	public void setStatus(Status status) {
 		if (status==Status.READ_ONLY) {
 			loadedState = null; //memory optimization
 		}
 		if ( this.status != status ) {
 			this.previousStatus = this.status;
 			this.status = status;
 		}
 	}
 
 	public Serializable getId() {
 		return id;
 	}
 
 	public Object[] getLoadedState() {
 		return loadedState;
 	}
 
 	public Object[] getDeletedState() {
 		return deletedState;
 	}
 
 	public void setDeletedState(Object[] deletedState) {
 		this.deletedState = deletedState;
 	}
 
 	public boolean isExistsInDatabase() {
 		return existsInDatabase;
 	}
 
 	public Object getVersion() {
 		return version;
 	}
 
 	public EntityPersister getPersister() {
 		return persister;
 	}
 
 	/**
 	 * Get the EntityKey based on this EntityEntry.
 	 * @return the EntityKey
 	 * @throws  IllegalStateException if getId() is null
 	 */
 	public EntityKey getEntityKey() {
 		if ( cachedEntityKey == null ) {
 			if ( getId() == null ) {
 				throw new IllegalStateException( "cannot generate an EntityKey when id is null.");
 			}
 			cachedEntityKey = new EntityKey( getId(), getPersister(), entityMode );
 		}
 		return cachedEntityKey;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public boolean isBeingReplicated() {
 		return isBeingReplicated;
 	}
 	
 	public Object getRowId() {
 		return rowId;
 	}
 	
 	/**
 	 * Handle updating the internal state of the entry after actually performing
 	 * the database update.  Specifically we update the snapshot information and
 	 * escalate the lock mode
 	 *
 	 * @param entity The entity instance
 	 * @param updatedState The state calculated after the update (becomes the
 	 * new {@link #getLoadedState() loaded state}.
 	 * @param nextVersion The new version.
 	 */
 	public void postUpdate(Object entity, Object[] updatedState, Object nextVersion) {
 		this.loadedState = updatedState;
 		setLockMode(LockMode.WRITE);
 
 		if ( getPersister().isVersioned() ) {
 			this.version = nextVersion;
 			getPersister().setPropertyValue(
 					entity,
 					getPersister().getVersionProperty(), 
 					nextVersion, 
 					entityMode 
 			);
 		}
 
 		FieldInterceptionHelper.clearDirty( entity );
 	}
 
 	/**
 	 * After actually deleting a row, record the fact that the instance no longer
 	 * exists in the database
 	 */
 	public void postDelete() {
 		previousStatus = status;
 		status = Status.GONE;
 		existsInDatabase = false;
 	}
 	
 	/**
 	 * After actually inserting a row, record the fact that the instance exists on the 
 	 * database (needed for identity-column key generation)
 	 */
 	public void postInsert() {
 		existsInDatabase = true;
 	}
 	
 	public boolean isNullifiable(boolean earlyInsert, SessionImplementor session) {
 		return getStatus() == Status.SAVING || (
 				earlyInsert ?
 						!isExistsInDatabase() :
 						session.getPersistenceContext().getNullifiableEntityKeys()
 							.contains( getEntityKey() )
 				);
 	}
 	
 	public Object getLoadedValue(String propertyName) {
 		int propertyIndex = ( (UniqueKeyLoadable) persister ).getPropertyIndex(propertyName);
 		return loadedState[propertyIndex];
 	}
 
 	public boolean requiresDirtyCheck(Object entity) {		
 		return isModifiableEntity() && (
 				getPersister().hasMutableProperties() ||
 				!FieldInterceptionHelper.isInstrumented( entity ) ||
 				FieldInterceptionHelper.extractFieldInterceptor( entity).isDirty()
 			);
 	}
 
 	/**
 	 * Can the entity be modified?
 	 *
 	 * The entity is modifiable if all of the following are true:
 	 * <ul>
 	 * <li>the entity class is mutable</li>
 	 * <li>the entity is not read-only</li>
 	 * <li>if the current status is Status.DELETED, then the entity was not read-only when it was deleted</li>
 	 * </ul>
 	 * @return true, if the entity is modifiable; false, otherwise,
 	 */
 	public boolean isModifiableEntity() {
 		return ( status != Status.READ_ONLY ) &&
 				! ( status == Status.DELETED && previousStatus == Status.READ_ONLY ) &&
 				getPersister().isMutable();
 	}
 
 	public void forceLocked(Object entity, Object nextVersion) {
 		version = nextVersion;
 		loadedState[ persister.getVersionProperty() ] = version;
 		//noinspection deprecation
 		setLockMode( LockMode.FORCE );  // TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
 		persister.setPropertyValue(
 				entity,
 		        getPersister().getVersionProperty(),
 		        nextVersion,
 		        entityMode
 		);
 	}
 
 	public boolean isReadOnly() {
 		if (status != Status.MANAGED && status != Status.READ_ONLY) {
 			throw new HibernateException("instance was not in a valid state");
 		}
 		return status == Status.READ_ONLY;
 	}
 
 	public void setReadOnly(boolean readOnly, Object entity) {
 		if ( readOnly == isReadOnly() ) {
 			// simply return since the status is not being changed
 			return;
 		}
 		if ( readOnly ) {
 			setStatus( Status.READ_ONLY );
 			loadedState = null;
 		}
 		else {
 			if ( ! persister.isMutable() ) {
 				throw new IllegalStateException( "Cannot make an immutable entity modifiable." );
 			}
 			setStatus( Status.MANAGED );
 			loadedState = getPersister().getPropertyValues( entity, entityMode );
 		}
 	}
 	
 	public String toString() {
 		return "EntityEntry" + 
 				MessageHelper.infoString(entityName, id) + 
 				'(' + status + ')';
 	}
 
 	public boolean isLoadedWithLazyPropertiesUnfetched() {
 		return loadedWithLazyPropertiesUnfetched;
 	}
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 *
 	 * @throws IOException If a stream error occurs
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( entityName );
 		oos.writeObject( id );
 		oos.writeObject( entityMode.toString() );
 		oos.writeObject( status.toString() );
 		oos.writeObject( ( previousStatus == null ? "" : previousStatus.toString() ) );
 		// todo : potentially look at optimizing these two arrays
 		oos.writeObject( loadedState );
 		oos.writeObject( deletedState );
 		oos.writeObject( version );
 		oos.writeObject( lockMode.toString() );
 		oos.writeBoolean( existsInDatabase );
 		oos.writeBoolean( isBeingReplicated );
 		oos.writeBoolean( loadedWithLazyPropertiesUnfetched );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param session The session being deserialized.
 	 *
 	 * @return The deserialized EntityEntry
 	 *
 	 * @throws IOException If a stream error occurs
 	 * @throws ClassNotFoundException If any of the classes declared in the stream
 	 * cannot be found
 	 */
 	static EntityEntry deserialize(
 			ObjectInputStream ois,
 	        SessionImplementor session) throws IOException, ClassNotFoundException {
 		String previousStatusString = null;
 		return new EntityEntry(
 				( session == null ? null : session.getFactory() ),
 		        ( String ) ois.readObject(),
 				( Serializable ) ois.readObject(),
 	            EntityMode.parse( ( String ) ois.readObject() ),
 				Status.parse( ( String ) ois.readObject() ),
 				( ( previousStatusString = ( String ) ois.readObject() ).length() == 0 ?
 							null :
 							Status.parse( previousStatusString ) 
 				),
 	            ( Object[] ) ois.readObject(),
 	            ( Object[] ) ois.readObject(),
 	            ois.readObject(),
 	            LockMode.parse( ( String ) ois.readObject() ),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 	            ois.readBoolean()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/ForeignKeys.java b/hibernate-core/src/main/java/org/hibernate/engine/ForeignKeys.java
index 7d44216c09..ee2ba43995 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/ForeignKeys.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/ForeignKeys.java
@@ -1,252 +1,252 @@
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
 package org.hibernate.engine;
 import java.io.Serializable;
 import org.hibernate.HibernateException;
 import org.hibernate.TransientObjectException;
-import org.hibernate.intercept.LazyPropertyInitializer;
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Algorithms related to foreign key constraint transparency
  * 
  * @author Gavin King
  */
 public final class ForeignKeys {
 	
 	private ForeignKeys() {}
 	
 	public static class Nullifier {
 	
 		private final boolean isDelete;
 		private final boolean isEarlyInsert;
 		private final SessionImplementor session;
 		private final Object self;
 		
 		public Nullifier(Object self, boolean isDelete, boolean isEarlyInsert, SessionImplementor session) {
 			this.isDelete = isDelete;
 			this.isEarlyInsert = isEarlyInsert;
 			this.session = session;
 			this.self = self;
 		}
 		
 		/**
 		 * Nullify all references to entities that have not yet 
 		 * been inserted in the database, where the foreign key
 		 * points toward that entity
 		 */
 		public void nullifyTransientReferences(final Object[] values, final Type[] types) 
 		throws HibernateException {
 			for ( int i = 0; i < types.length; i++ ) {
 				values[i] = nullifyTransientReferences( values[i], types[i] );
 			}
 		}
 	
 		/**
 		 * Return null if the argument is an "unsaved" entity (ie. 
 		 * one with no existing database row), or the input argument 
 		 * otherwise. This is how Hibernate avoids foreign key constraint
 		 * violations.
 		 */
 		private Object nullifyTransientReferences(final Object value, final Type type) 
 		throws HibernateException {
 			if ( value == null ) {
 				return null;
 			}
 			else if ( type.isEntityType() ) {
 				EntityType entityType = (EntityType) type;
 				if ( entityType.isOneToOne() ) {
 					return value;
 				}
 				else {
 					String entityName = entityType.getAssociatedEntityName();
 					return isNullifiable(entityName, value) ? null : value;
 				}
 			}
 			else if ( type.isAnyType() ) {
 				return isNullifiable(null, value) ? null : value;
 			}
 			else if ( type.isComponentType() ) {
 				CompositeType actype = (CompositeType) type;
 				Object[] subvalues = actype.getPropertyValues(value, session);
 				Type[] subtypes = actype.getSubtypes();
 				boolean substitute = false;
 				for ( int i = 0; i < subvalues.length; i++ ) {
 					Object replacement = nullifyTransientReferences( subvalues[i], subtypes[i] );
 					if ( replacement != subvalues[i] ) {
 						substitute = true;
 						subvalues[i] = replacement;
 					}
 				}
 				if (substitute) actype.setPropertyValues( value, subvalues, session.getEntityMode() );
 				return value;
 			}
 			else {
 				return value;
 			}
 		}
 	
 		/**
 		 * Determine if the object already exists in the database, 
 		 * using a "best guess"
 		 */
 		private boolean isNullifiable(final String entityName, Object object) 
 		throws HibernateException {
 			
 			if (object==LazyPropertyInitializer.UNFETCHED_PROPERTY) return false; //this is kinda the best we can do...
 			
 			if ( object instanceof HibernateProxy ) {
 				// if its an uninitialized proxy it can't be transient
 				LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 				if ( li.getImplementation(session) == null ) {
 					return false;
 					// ie. we never have to null out a reference to
 					// an uninitialized proxy
 				}
 				else {
 					//unwrap it
 					object = li.getImplementation();
 				}
 			}
 	
 			// if it was a reference to self, don't need to nullify
 			// unless we are using native id generation, in which
 			// case we definitely need to nullify
 			if ( object == self ) {
 				return isEarlyInsert || (
 					isDelete &&
 					session.getFactory()
 						.getDialect()
 						.hasSelfReferentialForeignKeyBug()
 				);
 			}
 	
 			// See if the entity is already bound to this session, if not look at the
 			// entity identifier and assume that the entity is persistent if the
 			// id is not "unsaved" (that is, we rely on foreign keys to keep
 			// database integrity)
 	
 			EntityEntry entityEntry = session.getPersistenceContext().getEntry(object);
 			if ( entityEntry==null ) {
 				return isTransient(entityName, object, null, session);
 			}
 			else {
 				return entityEntry.isNullifiable(isEarlyInsert, session);
 			}
 	
 		}
 		
 	}
 	
 	/**
 	 * Is this instance persistent or detached?
 	 * If <tt>assumed</tt> is non-null, don't hit the database to make the 
 	 * determination, instead assume that value; the client code must be 
 	 * prepared to "recover" in the case that this assumed result is incorrect.
 	 */
 	public static boolean isNotTransient(String entityName, Object entity, Boolean assumed, SessionImplementor session) 
 	throws HibernateException {
 		if (entity instanceof HibernateProxy) return true;
 		if ( session.getPersistenceContext().isEntryFor(entity) ) return true;
 		return !isTransient(entityName, entity, assumed, session);
 	}
 	
 	/**
 	 * Is this instance, which we know is not persistent, actually transient?
 	 * If <tt>assumed</tt> is non-null, don't hit the database to make the 
 	 * determination, instead assume that value; the client code must be 
 	 * prepared to "recover" in the case that this assumed result is incorrect.
 	 */
 	public static boolean isTransient(String entityName, Object entity, Boolean assumed, SessionImplementor session) 
 	throws HibernateException {
 		
-		if (entity==LazyPropertyInitializer.UNFETCHED_PROPERTY) {
+		if (entity== LazyPropertyInitializer.UNFETCHED_PROPERTY) {
 			// an unfetched association can only point to
 			// an entity that already exists in the db
 			return false;
 		}
 		
 		// let the interceptor inspect the instance to decide
 		Boolean isUnsaved = session.getInterceptor().isTransient(entity);
 		if (isUnsaved!=null) return isUnsaved.booleanValue();
 		
 		// let the persister inspect the instance to decide
 		EntityPersister persister = session.getEntityPersister(entityName, entity);
 		isUnsaved = persister.isTransient(entity, session);
 		if (isUnsaved!=null) return isUnsaved.booleanValue();
 
 		// we use the assumed value, if there is one, to avoid hitting
 		// the database
 		if (assumed!=null) return assumed.booleanValue();
 		
 		// hit the database, after checking the session cache for a snapshot
 		Object[] snapshot = session.getPersistenceContext().getDatabaseSnapshot(
 				persister.getIdentifier( entity, session ),
 				persister
 		);
 		return snapshot==null;
 
 	}
 
 	/**
 	 * Return the identifier of the persistent or transient object, or throw
 	 * an exception if the instance is "unsaved"
 	 *
 	 * Used by OneToOneType and ManyToOneType to determine what id value should 
 	 * be used for an object that may or may not be associated with the session. 
 	 * This does a "best guess" using any/all info available to use (not just the 
 	 * EntityEntry).
 	 */
 	public static Serializable getEntityIdentifierIfNotUnsaved(
 			final String entityName, 
 			final Object object, 
 			final SessionImplementor session) 
 	throws HibernateException {
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Serializable id = session.getContextEntityIdentifier( object );
 			if ( id == null ) {
 				// context-entity-identifier returns null explicitly if the entity
 				// is not associated with the persistence context; so make some
 				// deeper checks...
 				if ( isTransient(entityName, object, Boolean.FALSE, session) ) {
 					throw new TransientObjectException(
 							"object references an unsaved transient instance - save the transient instance before flushing: " +
 							(entityName == null ? session.guessEntityName( object ) : entityName)
 					);
 				}
 				id = session.getEntityPersister( entityName, object ).getIdentifier( object, session );
 			}
 			return id;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/Nullability.java b/hibernate-core/src/main/java/org/hibernate/engine/Nullability.java
index b6540d1f78..2e196211b6 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/Nullability.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/Nullability.java
@@ -1,210 +1,210 @@
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
 package org.hibernate.engine;
 import java.util.Iterator;
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyValueException;
-import org.hibernate.intercept.LazyPropertyInitializer;
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Implements the algorithm for validating property values
  * for illegal null values
  * @author Gavin King
  */
 public final class Nullability {
 	
 	private final SessionImplementor session;
 	private final boolean checkNullability;
 
 	public Nullability(SessionImplementor session) {
 		this.session = session;
 		this.checkNullability = session.getFactory().getSettings().isCheckNullability();
 	}
 	/**
 	 * Check nullability of the class persister properties
 	 *
 	 * @param values entity properties
 	 * @param persister class persister
 	 * @param isUpdate wether it is intended to be updated or saved
 	 * @throws org.hibernate.PropertyValueException Break the nullability of one property
 	 * @throws HibernateException error while getting Component values
 	 */
 	public void checkNullability(
 			final Object[] values,
 			final EntityPersister persister,
 			final boolean isUpdate) 
 	throws PropertyValueException, HibernateException {
 		/*
 		 * Typically when Bean Validation is on, we don't want to validate null values
 		 * at the Hibernate Core level. Hence the checkNullability setting.
 		 */
 		if ( checkNullability ) {
 			/*
 			  * Algorithm
 			  * Check for any level one nullability breaks
 			  * Look at non null components to
 			  *   recursively check next level of nullability breaks
 			  * Look at Collections contraining component to
 			  *   recursively check next level of nullability breaks
 			  *
 			  *
 			  * In the previous implementation, not-null stuffs where checked
 			  * filtering by level one only updateable
 			  * or insertable columns. So setting a sub component as update="false"
 			  * has no effect on not-null check if the main component had good checkeability
 			  * In this implementation, we keep this feature.
 			  * However, I never see any documentation mentioning that, but it's for
 			  * sure a limitation.
 			  */
 
 			final boolean[] nullability = persister.getPropertyNullability();
 			final boolean[] checkability = isUpdate ?
 				persister.getPropertyUpdateability() :
 				persister.getPropertyInsertability();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 
 			for ( int i = 0; i < values.length; i++ ) {
 
-				if ( checkability[i] && values[i]!=LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
+				if ( checkability[i] && values[i]!= LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 					final Object value = values[i];
 					if ( !nullability[i] && value == null ) {
 
 						//check basic level one nullablilty
 						throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								persister.getPropertyNames()[i]
 							);
 
 					}
 					else if ( value != null ) {
 
 						//values is not null and is checkable, we'll look deeper
 						String breakProperties = checkSubElementsNullability( propertyTypes[i], value );
 						if ( breakProperties != null ) {
 							throw new PropertyValueException(
 								"not-null property references a null or transient value",
 								persister.getEntityName(),
 								buildPropertyPath( persister.getPropertyNames()[i], breakProperties )
 							);
 						}
 
 					}
 				}
 
 			}
 		}
 	}
 
 	/**
 	 * check sub elements-nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param propertyType type to check
 	 * @param value value to check
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
 	private String checkSubElementsNullability(final Type propertyType, final Object value) 
 	throws HibernateException {
 		//for non null args, check for components and elements containing components
 		if ( propertyType.isComponentType() ) {
 			return checkComponentNullability( value, (CompositeType) propertyType );
 		}
 		else if ( propertyType.isCollectionType() ) {
 
 			//persistent collections may have components
 			CollectionType collectionType = (CollectionType) propertyType;
 			Type collectionElementType = collectionType.getElementType( session.getFactory() );
 			if ( collectionElementType.isComponentType() ) {
 				//check for all components values in the collection
 
 				CompositeType componentType = (CompositeType) collectionElementType;
 				Iterator iter = CascadingAction.getLoadedElementsIterator(session, collectionType, value);
 				while ( iter.hasNext() ) {
 					Object compValue = iter.next();
 					if (compValue != null) {
 						return checkComponentNullability(compValue, componentType);
 					}
 				}
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * check component nullability. Returns property path that break
 	 * nullability or null if none
 	 *
 	 * @param value component properties
 	 * @param compType component not-nullable type
 	 *
 	 * @return property path
 	 * @throws HibernateException error while getting subcomponent values
 	 */
 	private String checkComponentNullability(final Object value, final CompositeType compType)
 	throws HibernateException {
 		/* will check current level if some of them are not null
 		 * or sublevels if they exist
 		 */
 		boolean[] nullability = compType.getPropertyNullability();
 		if ( nullability!=null ) {
 			//do the test
 			final Object[] values = compType.getPropertyValues( value, session.getEntityMode() );
 			final Type[] propertyTypes = compType.getSubtypes();
 			for ( int i=0; i<values.length; i++ ) {
 				final Object subvalue = values[i];
 				if ( !nullability[i] && subvalue==null ) {
 					return compType.getPropertyNames()[i];
 				}
 				else if ( subvalue != null ) {
 					String breakProperties = checkSubElementsNullability( propertyTypes[i], subvalue );
 					if ( breakProperties != null ) {
 						return buildPropertyPath( compType.getPropertyNames()[i], breakProperties );
 					}
 	 			}
 	 		}
 		}
 		return null;
 	}
 
 	/**
 	 * Return a well formed property path.
 	 * Basicaly, it will return parent.child
 	 *
 	 * @param parent parent in path
 	 * @param child child in path
 	 * @return parent-child path
 	 */
 	private static String buildPropertyPath(String parent, String child) {
 		return new StringBuffer( parent.length() + child.length() + 1 )
 			.append(parent).append('.').append(child).toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
index 8578fe5a95..60fdf905a6 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
@@ -1,314 +1,314 @@
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
 package org.hibernate.engine;
 import java.io.Serializable;
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.event.PreLoadEventListener;
-import org.hibernate.intercept.LazyPropertyInitializer;
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Functionality relating to Hibernate's two-phase loading process,
  * that may be reused by persisters that do not use the Loader
  * framework
  *
  * @author Gavin King
  */
 public final class TwoPhaseLoad {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, TwoPhaseLoad.class.getName());
 
 	private TwoPhaseLoad() {}
 
 	/**
 	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading.
 	 *
 	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
 	 * to resolve any associations yet, because there might be other entities waiting to be
 	 * read from the JDBC result set we are currently processing
 	 */
 	public static void postHydrate(
 		final EntityPersister persister,
 		final Serializable id,
 		final Object[] values,
 		final Object rowId,
 		final Object object,
 		final LockMode lockMode,
 		final boolean lazyPropertiesAreUnfetched,
 		final SessionImplementor session)
 	throws HibernateException {
 
 		Object version = Versioning.getVersion(values, persister);
 		session.getPersistenceContext().addEntry(
 				object,
 				Status.LOADING,
 				values,
 				rowId,
 				id,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnfetched
 			);
 
         if (LOG.isTraceEnabled() && version != null) {
 			String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 			        : "null";
             LOG.trace("Version: " + versionStr);
 		}
 
 	}
 
 	/**
 	 * Perform the second step of 2-phase load. Fully initialize the entity
 	 * instance.
 	 *
 	 * After processing a JDBC result set, we "resolve" all the associations
 	 * between the entities which were instantiated and had their state
 	 * "hydrated" into an array
 	 */
 	public static void initializeEntity(
 			final Object entity,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent,
 			final PostLoadEvent postLoadEvent) throws HibernateException {
 
 		//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		EntityEntry entityEntry = persistenceContext.getEntry(entity);
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
 		EntityPersister persister = entityEntry.getPersister();
 		Serializable id = entityEntry.getId();
 		Object[] hydratedState = entityEntry.getLoadedState();
 
         if (LOG.isDebugEnabled()) LOG.debugf("Resolving associations for %s",
                                              MessageHelper.infoString(persister, id, session.getFactory()));
 
 		Type[] types = persister.getPropertyTypes();
 		for ( int i = 0; i < hydratedState.length; i++ ) {
 			final Object value = hydratedState[i];
 			if ( value!=LazyPropertyInitializer.UNFETCHED_PROPERTY && value!=BackrefPropertyAccessor.UNKNOWN ) {
 				hydratedState[i] = types[i].resolve( value, session, entity );
 			}
 		}
 
 		//Must occur after resolving identifiers!
 		if ( session.isEventSource() ) {
 			preLoadEvent.setEntity(entity).setState(hydratedState).setId(id).setPersister(persister);
 			PreLoadEventListener[] listeners = session.getListeners().getPreLoadEventListeners();
 			for ( int i = 0; i < listeners.length; i++ ) {
 				listeners[i].onPreLoad(preLoadEvent);
 			}
 		}
 
 		persister.setPropertyValues( entity, hydratedState, session.getEntityMode() );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
             if (LOG.isDebugEnabled()) LOG.debugf("Adding entity to second-level cache: %s",
                                                  MessageHelper.infoString(persister, id, session.getFactory()));
 
 			Object version = Versioning.getVersion(hydratedState, persister);
 			CacheEntry entry = new CacheEntry(
 					hydratedState,
 					persister,
 					entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 					version,
 					session,
 					entity
 			);
 			CacheKey cacheKey = new CacheKey(
 					id,
 					persister.getIdentifierType(),
 					persister.getRootEntityName(),
 					session.getEntityMode(),
 					session.getFactory()
 			);
 
 			// explicit handling of caching for rows just inserted and then somehow forced to be read
 			// from the database *within the same transaction*.  usually this is done by
 			// 		1) Session#refresh, or
 			// 		2) Session#clear + some form of load
 			//
 			// we need to be careful not to clobber the lock here in the cache so that it can be rolled back if need be
 			if ( session.getPersistenceContext().wasInsertedDuringTransaction( persister, id ) ) {
 				persister.getCacheAccessStrategy().update(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						version,
 						version
 				);
 			}
 			else {
 				boolean put = persister.getCacheAccessStrategy().putFromLoad(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						session.getTimestamp(),
 						version,
 						useMinimalPuts( session, entityEntry )
 				);
 
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 		}
 
 		boolean isReallyReadOnly = readOnly;
 		if ( !persister.isMutable() ) {
 			isReallyReadOnly = true;
 		}
 		else {
 			Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReallyReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		if ( isReallyReadOnly ) {
 			//no need to take a snapshot - this is a
 			//performance optimization, but not really
 			//important, except for entities with huge
 			//mutable property values
 			persistenceContext.setEntryStatus(entityEntry, Status.READ_ONLY);
 		}
 		else {
 			//take a snapshot
 			TypeHelper.deepCopy(
 					hydratedState,
 					persister.getPropertyTypes(),
 					persister.getPropertyUpdateability(),
 					hydratedState,  //after setting values to object, entityMode
 					session
 			);
 			persistenceContext.setEntryStatus(entityEntry, Status.MANAGED);
 		}
 
 		persister.afterInitialize(
 				entity,
 				entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 				session
 			);
 
 		if ( session.isEventSource() ) {
 			postLoadEvent.setEntity(entity).setId(id).setPersister(persister);
 			PostLoadEventListener[] listeners = session.getListeners().getPostLoadEventListeners();
 			for ( int i = 0; i < listeners.length; i++ ) {
 				listeners[i].onPostLoad(postLoadEvent);
 			}
 		}
 
         if (LOG.isDebugEnabled()) LOG.debugf("Done materializing entity %s",
                                              MessageHelper.infoString(persister, id, session.getFactory()));
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().loadEntity( persister.getEntityName() );
 		}
 
 	}
 
 	private static boolean useMinimalPuts(SessionImplementor session, EntityEntry entityEntry) {
 		return ( session.getFactory().getSettings().isMinimalPutsEnabled() &&
 						session.getCacheMode()!=CacheMode.REFRESH ) ||
 				( entityEntry.getPersister().hasLazyProperties() &&
 						entityEntry.isLoadedWithLazyPropertiesUnfetched() &&
 						entityEntry.getPersister().isLazyPropertiesCacheable() );
 	}
 
 	/**
 	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object
 	 * identity. Must be called before <tt>postHydrate()</tt>.
 	 *
 	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
 	 * but we need the mapping from id to instance in order to guarantee uniqueness.
 	 */
 	public static void addUninitializedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnfetched,
 			final SessionImplementor session
 	) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				null,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnfetched
 			);
 	}
 
 	public static void addUninitializedCachedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnfetched,
 			final Object version,
 			final SessionImplementor session
 	) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnfetched
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
index 8c8766f4a2..5767f7227f 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
@@ -1,521 +1,521 @@
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
 package org.hibernate.event.def;
 import java.io.Serializable;
 import java.util.Map;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.action.EntityIdentityInsertAction;
 import org.hibernate.action.EntityInsertAction;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.classic.Validatable;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Nullability;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.Versioning;
 import org.hibernate.event.EventSource;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
-import org.hibernate.intercept.FieldInterceptionHelper;
-import org.hibernate.intercept.FieldInterceptor;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.jboss.logging.Logger;
 
 /**
  * A convenience bas class for listeners responding to save events.
  *
  * @author Steve Ebersole.
  */
 public abstract class AbstractSaveEventListener extends AbstractReassociateEventListener {
 
 	protected static final int PERSISTENT = 0;
 	protected static final int TRANSIENT = 1;
 	protected static final int DETACHED = 2;
 	protected static final int DELETED = 3;
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        AbstractSaveEventListener.class.getName());
 
 	/**
 	 * Prepares the save call using the given requested id.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param requestedId The id to which to associate the entity.
 	 * @param entityName The name of the entity being saved.
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 *
 	 * @return The id used to save the entity.
 	 */
 	protected Serializable saveWithRequestedId(
 			Object entity,
 			Serializable requestedId,
 			String entityName,
 			Object anything,
 			EventSource source) {
 		return performSave(
 				entity,
 				requestedId,
 				source.getEntityPersister( entityName, entity ),
 				false,
 				anything,
 				source,
 				true
 		);
 	}
 
 	/**
 	 * Prepares the save call using a newly generated id.
 	 *
 	 * @param entity The entity to be saved
 	 * @param entityName The entity-name for the entity to be saved
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable saveWithGeneratedId(
 			Object entity,
 			String entityName,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 		EntityPersister persister = source.getEntityPersister( entityName, entity );
 		Serializable generatedId = persister.getIdentifierGenerator().generate( source, entity );
 		if ( generatedId == null ) {
 			throw new IdentifierGenerationException( "null id generated for:" + entity.getClass() );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR ) {
 			return source.getIdentifier( entity );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			return performSave( entity, null, persister, true, anything, source, requiresImmediateIdAccess );
 		}
 		else {
             // TODO: define toString()s for generators
             if (LOG.isDebugEnabled()) LOG.debugf("Generated identifier: %s, using strategy: %s",
                                                  persister.getIdentifierType().toLoggableString(generatedId, source.getFactory()),
                                                  persister.getIdentifierGenerator().getClass().getName());
 
 			return performSave( entity, generatedId, persister, false, anything, source, true );
 		}
 	}
 
 	/**
 	 * Ppepares the save call by checking the session caches for a pre-existing
 	 * entity and performing any lifecycle callbacks.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param id The id by which to save the entity.
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Is an identity column being used?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session from which the event originated.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSave(
 			Object entity,
 			Serializable id,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
         if (LOG.isTraceEnabled()) LOG.trace("Saving " + MessageHelper.infoString(persister, id, source.getFactory()));
 
 		EntityKey key;
 		if ( !useIdentityColumn ) {
 			key = new EntityKey( id, persister, source.getEntityMode() );
 			Object old = source.getPersistenceContext().getEntity( key );
 			if ( old != null ) {
 				if ( source.getPersistenceContext().getEntry( old ).getStatus() == Status.DELETED ) {
 					source.forceFlush( source.getPersistenceContext().getEntry( old ) );
 				}
 				else {
 					throw new NonUniqueObjectException( id, persister.getEntityName() );
 				}
 			}
 			persister.setIdentifier( entity, id, source );
 		}
 		else {
 			key = null;
 		}
 
 		if ( invokeSaveLifecycle( entity, persister, source ) ) {
 			return id; //EARLY EXIT
 		}
 
 		return performSaveOrReplicate(
 				entity,
 				key,
 				persister,
 				useIdentityColumn,
 				anything,
 				source,
 				requiresImmediateIdAccess
 		);
 	}
 
 	protected boolean invokeSaveLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		// Sub-insertions should occur before containing insertion so
 		// Try to do the callback now
 		if ( persister.implementsLifecycle( source.getEntityMode() ) ) {
             LOG.debugf("Calling onSave()");
 			if ( ( ( Lifecycle ) entity ).onSave( source ) ) {
                 LOG.debugf("Insertion vetoed by onSave()");
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected void validate(Object entity, EntityPersister persister, EventSource source) {
 		if ( persister.implementsValidatable( source.getEntityMode() ) ) {
 			( ( Validatable ) entity ).validate();
 		}
 	}
 
 	/**
 	 * Performs all the actual work needed to save an entity (well to get the save moved to
 	 * the execution queue).
 	 *
 	 * @param entity The entity to be saved
 	 * @param key The id to be used for saving the entity (or null, in the case of identity columns)
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Should an identity column be used for id generation?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of the current event.
 	 * @param requiresImmediateIdAccess Is access to the identifier required immediately
 	 * after the completion of the save?  persist(), for example, does not require this...
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSaveOrReplicate(
 			Object entity,
 			EntityKey key,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		validate( entity, persister, source );
 
 		Serializable id = key == null ? null : key.getIdentifier();
 
 		boolean inTxn = source.getTransactionCoordinator().isTransactionInProgress();
 		boolean shouldDelayIdentityInserts = !inTxn && !requiresImmediateIdAccess;
 
 		// Put a placeholder in entries, so we don't recurse back and try to save() the
 		// same object again. QUESTION: should this be done before onSave() is called?
 		// likewise, should it be done before onUpdate()?
 		source.getPersistenceContext().addEntry(
 				entity,
 				Status.SAVING,
 				null,
 				null,
 				id,
 				null,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				false,
 				false
 		);
 
 		cascadeBeforeSave( source, persister, entity, anything );
 
 		if ( useIdentityColumn && !shouldDelayIdentityInserts ) {
             LOG.trace("Executing insertions");
 			source.getActionQueue().executeInserts();
 		}
 
 		Object[] values = persister.getPropertyValuesToInsert( entity, getMergeMap( anything ), source );
 		Type[] types = persister.getPropertyTypes();
 
 		boolean substitute = substituteValuesIfNecessary( entity, id, values, persister, source );
 
 		if ( persister.hasCollections() ) {
 			substitute = substitute || visitCollectionsBeforeSave( entity, id, values, types, source );
 		}
 
 		if ( substitute ) {
 			persister.setPropertyValues( entity, values, source.getEntityMode() );
 		}
 
 		TypeHelper.deepCopy(
 				values,
 				types,
 				persister.getPropertyUpdateability(),
 				values,
 				source
 		);
 
 		new ForeignKeys.Nullifier( entity, false, useIdentityColumn, source )
 				.nullifyTransientReferences( values, types );
 		new Nullability( source ).checkNullability( values, persister, false );
 
 		if ( useIdentityColumn ) {
 			EntityIdentityInsertAction insert = new EntityIdentityInsertAction(
 					values, entity, persister, source, shouldDelayIdentityInserts
 			);
 			if ( !shouldDelayIdentityInserts ) {
                 LOG.debugf("Executing identity-insert immediately");
 				source.getActionQueue().execute( insert );
 				id = insert.getGeneratedId();
 				key = new EntityKey( id, persister, source.getEntityMode() );
 				source.getPersistenceContext().checkUniqueness( key, entity );
 			}
 			else {
                 LOG.debugf("Delaying identity-insert due to no transaction in progress");
 				source.getActionQueue().addAction( insert );
 				key = insert.getDelayedEntityKey();
 			}
 		}
 
 		Object version = Versioning.getVersion( values, persister );
 		source.getPersistenceContext().addEntity(
 				entity,
 				( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				values,
 				key,
 				version,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				isVersionIncrementDisabled(),
 				false
 		);
 		//source.getPersistenceContext().removeNonExist( new EntityKey( id, persister, source.getEntityMode() ) );
 
 		if ( !useIdentityColumn ) {
 			source.getActionQueue().addAction(
 					new EntityInsertAction( id, values, entity, version, persister, source )
 			);
 		}
 
 		cascadeAfterSave( source, persister, entity, anything );
 
 		markInterceptorDirty( entity, persister, source );
 
 		return id;
 	}
 
 	private void markInterceptorDirty(Object entity, EntityPersister persister, EventSource source) {
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.injectFieldInterceptor(
 					entity,
 					persister.getEntityName(),
 					null,
 					source
 			);
 			interceptor.dirty();
 		}
 	}
 
 	protected Map getMergeMap(Object anything) {
 		return null;
 	}
 
 	/**
 	 * After the save, will te version number be incremented
 	 * if the instance is modified?
 	 *
 	 * @return True if the version will be incremented on an entity change after save;
 	 *         false otherwise.
 	 */
 	protected boolean isVersionIncrementDisabled() {
 		return false;
 	}
 
 	protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
 		WrapVisitor visitor = new WrapVisitor( source );
 		// substitutes into values by side-effect
 		visitor.processEntityPropertyValues( values, types );
 		return visitor.isSubstitutionRequired();
 	}
 
 	/**
 	 * Perform any property value substitution that is necessary
 	 * (interceptor callback, version initialization...)
 	 *
 	 * @param entity The entity
 	 * @param id The entity identifier
 	 * @param values The snapshot entity state
 	 * @param persister The entity persister
 	 * @param source The originating session
 	 *
 	 * @return True if the snapshot state changed such that
 	 * reinjection of the values into the entity is required.
 	 */
 	protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		boolean substitute = source.getInterceptor().onSave(
 				entity,
 				id,
 				values,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		//keep the existing version number in the case of replicate!
 		if ( persister.isVersioned() ) {
 			substitute = Versioning.seedVersion(
 					values,
 					persister.getVersionProperty(),
 					persister.getVersionType(),
 					source
 			) || substitute;
 		}
 		return substitute;
 	}
 
 	/**
 	 * Handles the calls needed to perform pre-save cascades for the given entity.
 	 *
 	 * @param source The session from whcih the save event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity to be saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeBeforeSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to many-to-one BEFORE the parent is saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.BEFORE_INSERT_AFTER_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	/**
 	 * Handles to calls needed to perform post-save cascades.
 	 *
 	 * @param source The session from which the event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity beng saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeAfterSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to collections AFTER the collection owner was saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.AFTER_INSERT_BEFORE_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected abstract CascadingAction getCascadeAction();
 
 	/**
 	 * Determine whether the entity is persistent, detached, or transient
 	 *
 	 * @param entity The entity to check
 	 * @param entityName The name of the entity
 	 * @param entry The entity's entry in the persistence context
 	 * @param source The originating session.
 	 *
 	 * @return The state.
 	 */
 	protected int getEntityState(
 			Object entity,
 			String entityName,
 			EntityEntry entry, //pass this as an argument only to avoid double looking
 			SessionImplementor source) {
 
 		if ( entry != null ) { // the object is persistent
 
 			//the entity is associated with the session, so check its status
 			if ( entry.getStatus() != Status.DELETED ) {
 				// do nothing for persistent instances
                 if (LOG.isTraceEnabled()) LOG.trace("Persistent instance of: " + getLoggableName(entityName, entity));
 				return PERSISTENT;
 			}
             // ie. e.status==DELETED
             if (LOG.isTraceEnabled()) LOG.trace("Deleted instance of: " + getLoggableName(entityName, entity));
             return DELETED;
 
 		}
         // the object is transient or detached
 
 		// the entity is not associated with the session, so
         // try interceptor and unsaved-value
 
 		if (ForeignKeys.isTransient(entityName, entity, getAssumedUnsaved(), source)) {
             if (LOG.isTraceEnabled()) LOG.trace("Transient instance of: " + getLoggableName(entityName, entity));
             return TRANSIENT;
 		}
         if (LOG.isTraceEnabled()) LOG.trace("Detached instance of: " + getLoggableName(entityName, entity));
         return DETACHED;
 	}
 
 	protected String getLoggableName(String entityName, Object entity) {
 		return entityName == null ? entity.getClass().getName() : entityName;
 	}
 
 	protected Boolean getAssumedUnsaved() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractVisitor.java
index 1d8f8e2141..815471fbe6 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractVisitor.java
@@ -1,177 +1,177 @@
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
 package org.hibernate.event.def;
 import org.hibernate.HibernateException;
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.event.EventSource;
-import org.hibernate.intercept.LazyPropertyInitializer;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Abstract superclass of algorithms that walk
  * a tree of property values of an entity, and
  * perform specific functionality for collections,
  * components and associated entities.
  *
  * @author Gavin King
  */
 public abstract class AbstractVisitor {
 
 	private final EventSource session;
 
 	AbstractVisitor(EventSource session) {
 		this.session = session;
 	}
 
 	/**
 	 * Dispatch each property value to processValue().
 	 *
 	 * @param values
 	 * @param types
 	 * @throws HibernateException
 	 */
 	void processValues(Object[] values, Type[] types) throws HibernateException {
 		for ( int i=0; i<types.length; i++ ) {
 			if ( includeProperty(values, i) ) {
 				processValue( i, values, types );
 			}
 		}
 	}
 	
 	/**
 	 * Dispatch each property value to processValue().
 	 *
 	 * @param values
 	 * @param types
 	 * @throws HibernateException
 	 */
 	public void processEntityPropertyValues(Object[] values, Type[] types) throws HibernateException {
 		for ( int i=0; i<types.length; i++ ) {
 			if ( includeEntityProperty(values, i) ) {
 				processValue( i, values, types );
 			}
 		}
 	}
 	
 	void processValue(int i, Object[] values, Type[] types) {
 		processValue( values[i], types[i] );
 	}
 	
 	boolean includeEntityProperty(Object[] values, int i) {
 		return includeProperty(values, i);
 	}
 	
 	boolean includeProperty(Object[] values, int i) {
-		return values[i]!=LazyPropertyInitializer.UNFETCHED_PROPERTY;
+		return values[i]!= LazyPropertyInitializer.UNFETCHED_PROPERTY;
 	}
 
 	/**
 	 * Visit a component. Dispatch each property
 	 * to processValue().
 	 * @param component
 	 * @param componentType
 	 * @throws HibernateException
 	 */
 	Object processComponent(Object component, CompositeType componentType) throws HibernateException {
 		if (component!=null) {
 			processValues(
 				componentType.getPropertyValues(component, session),
 				componentType.getSubtypes()
 			);
 		}
 		return null;
 	}
 
 	/**
 	 * Visit a property value. Dispatch to the
 	 * correct handler for the property type.
 	 * @param value
 	 * @param type
 	 * @throws HibernateException
 	 */
 	final Object processValue(Object value, Type type) throws HibernateException {
 
 		if ( type.isCollectionType() ) {
 			//even process null collections
 			return processCollection( value, (CollectionType) type );
 		}
 		else if ( type.isEntityType() ) {
 			return processEntity( value, (EntityType) type );
 		}
 		else if ( type.isComponentType() ) {
 			return processComponent( value, (CompositeType) type );
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Walk the tree starting from the given entity.
 	 *
 	 * @param object
 	 * @param persister
 	 * @throws HibernateException
 	 */
 	void process(Object object, EntityPersister persister)
 	throws HibernateException {
 		processEntityPropertyValues(
 			persister.getPropertyValues( object, getSession().getEntityMode() ),
 			persister.getPropertyTypes()
 		);
 	}
 
 	/**
 	 * Visit a collection. Default superclass
 	 * implementation is a no-op.
 	 * @param collection
 	 * @param type
 	 * @throws HibernateException
 	 */
 	Object processCollection(Object collection, CollectionType type)
 	throws HibernateException {
 		return null;
 	}
 
 	/**
 	 * Visit a many-to-one or one-to-one associated
 	 * entity. Default superclass implementation is
 	 * a no-op.
 	 * @param value
 	 * @param entityType
 	 * @throws HibernateException
 	 */
 	Object processEntity(Object value, EntityType entityType)
 	throws HibernateException {
 		return null;
 	}
 
 	final EventSource getSession() {
 		return session;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
index e6416d8272..cb48e46ac5 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
@@ -1,581 +1,581 @@
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
 package org.hibernate.event.def;
 
 import java.io.Serializable;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.action.DelayedPostInsertIdentifier;
 import org.hibernate.action.EntityUpdateAction;
 import org.hibernate.classic.Validatable;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.Nullability;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.Versioning;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.FlushEntityEvent;
 import org.hibernate.event.FlushEntityEventListener;
-import org.hibernate.intercept.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * An event that occurs for each entity instance at flush time
  *
  * @author Gavin King
  */
 public class DefaultFlushEntityEventListener implements FlushEntityEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultFlushEntityEventListener.class.getName());
 
 	/**
 	 * make sure user didn't mangle the id
 	 */
 	public void checkId(
 			Object object,
 			EntityPersister persister,
 			Serializable id,
 			EntityMode entityMode,
 			SessionImplementor session) throws HibernateException {
 
 		if ( id != null && id instanceof DelayedPostInsertIdentifier ) {
 			// this is a situation where the entity id is assigned by a post-insert generator
 			// and was saved outside the transaction forcing it to be delayed
 			return;
 		}
 
 		if ( persister.canExtractIdOutOfEntity() ) {
 
 			Serializable oid = persister.getIdentifier( object, session );
 			if (id==null) {
 				throw new AssertionFailure("null id in " + persister.getEntityName() + " entry (don't flush the Session after an exception occurs)");
 			}
 			if ( !persister.getIdentifierType().isEqual( id, oid, entityMode, session.getFactory() ) ) {
 				throw new HibernateException(
 						"identifier of an instance of " +
 						persister.getEntityName() +
 						" was altered from " + id +
 						" to " + oid
 					);
 			}
 		}
 
 	}
 
 	private void checkNaturalId(
 			EntityPersister persister,
 	        EntityEntry entry,
 	        Object[] current,
 	        Object[] loaded,
 	        EntityMode entityMode,
 	        SessionImplementor session) {
 		if ( persister.hasNaturalIdentifier() && entry.getStatus() != Status.READ_ONLY ) {
  			Object[] snapshot = null;
 			Type[] types = persister.getPropertyTypes();
 			int[] props = persister.getNaturalIdentifierProperties();
 			boolean[] updateable = persister.getPropertyUpdateability();
 			for ( int i=0; i<props.length; i++ ) {
 				int prop = props[i];
 				if ( !updateable[prop] ) {
  					Object loadedVal;
  					if ( loaded == null ) {
  						if ( snapshot == null) {
  							snapshot = session.getPersistenceContext().getNaturalIdSnapshot( entry.getId(), persister );
  						}
  						loadedVal = snapshot[i];
  					} else {
  						loadedVal = loaded[prop];
  					}
  					if ( !types[prop].isEqual( current[prop], loadedVal, entityMode ) ) {
 						throw new HibernateException(
 								"immutable natural identifier of an instance of " +
 								persister.getEntityName() +
 								" was altered"
 							);
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Flushes a single entity's state to the database, by scheduling
 	 * an update action, if necessary
 	 */
 	public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
 		final Object entity = event.getEntity();
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final EntityPersister persister = entry.getPersister();
 		final Status status = entry.getStatus();
 		final EntityMode entityMode = session.getEntityMode();
 		final Type[] types = persister.getPropertyTypes();
 
 		final boolean mightBeDirty = entry.requiresDirtyCheck(entity);
 
 		final Object[] values = getValues( entity, entry, entityMode, mightBeDirty, session );
 
 		event.setPropertyValues(values);
 
 		//TODO: avoid this for non-new instances where mightBeDirty==false
 		boolean substitute = wrapCollections( session, persister, types, values);
 
 		if ( isUpdateNecessary( event, mightBeDirty ) ) {
 			substitute = scheduleUpdate( event ) || substitute;
 		}
 
 		if ( status != Status.DELETED ) {
 			// now update the object .. has to be outside the main if block above (because of collections)
 			if (substitute) persister.setPropertyValues( entity, values, entityMode );
 
 			// Search for collections by reachability, updating their role.
 			// We don't want to touch collections reachable from a deleted object
 			if ( persister.hasCollections() ) {
 				new FlushVisitor(session, entity).processEntityPropertyValues(values, types);
 			}
 		}
 
 	}
 
 	private Object[] getValues(
 			Object entity,
 			EntityEntry entry,
 			EntityMode entityMode,
 			boolean mightBeDirty,
 	        SessionImplementor session) {
 		final Object[] loadedState = entry.getLoadedState();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 
 		final Object[] values;
 		if ( status == Status.DELETED ) {
 			//grab its state saved at deletion
 			values = entry.getDeletedState();
 		}
 		else if ( !mightBeDirty && loadedState!=null ) {
 			values = loadedState;
 		}
 		else {
 			checkId( entity, persister, entry.getId(), entityMode, session );
 
 			// grab its current state
 			values = persister.getPropertyValues( entity, entityMode );
 
 			checkNaturalId( persister, entry, values, loadedState, entityMode, session );
 		}
 		return values;
 	}
 
 	private boolean wrapCollections(
 			EventSource session,
 			EntityPersister persister,
 			Type[] types,
 			Object[] values
 	) {
 		if ( persister.hasCollections() ) {
 
 			// wrap up any new collections directly referenced by the object
 			// or its components
 
 			// NOTE: we need to do the wrap here even if its not "dirty",
 			// because collections need wrapping but changes to _them_
 			// don't dirty the container. Also, for versioned data, we
 			// need to wrap before calling searchForDirtyCollections
 
 			WrapVisitor visitor = new WrapVisitor(session);
 			// substitutes into values by side-effect
 			visitor.processEntityPropertyValues(values, types);
 			return visitor.isSubstitutionRequired();
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isUpdateNecessary(final FlushEntityEvent event, final boolean mightBeDirty) {
 		final Status status = event.getEntityEntry().getStatus();
 		if ( mightBeDirty || status==Status.DELETED ) {
 			// compare to cached state (ignoring collections unless versioned)
 			dirtyCheck(event);
 			if ( isUpdateNecessary(event) ) {
 				return true;
 			}
 			else {
 				FieldInterceptionHelper.clearDirty( event.getEntity() );
 				return false;
 			}
 		}
 		else {
 			return hasDirtyCollections( event, event.getEntityEntry().getPersister(), status );
 		}
 	}
 
 	private boolean scheduleUpdate(final FlushEntityEvent event) {
 
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final Object entity = event.getEntity();
 		final Status status = entry.getStatus();
 		final EntityMode entityMode = session.getEntityMode();
 		final EntityPersister persister = entry.getPersister();
 		final Object[] values = event.getPropertyValues();
 
         if (LOG.isTraceEnabled()) {
 			if ( status == Status.DELETED ) {
                 if (!persister.isMutable()) LOG.trace("Updating immutable, deleted entity: "
                                                       + MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
                 else if (!entry.isModifiableEntity()) LOG.trace("Updating non-modifiable, deleted entity: "
                                                                 + MessageHelper.infoString(persister,
                                                                                            entry.getId(),
                                                                                            session.getFactory()));
                 else LOG.trace("Updating deleted entity: "
                                + MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
             } else LOG.trace("Updating entity: " + MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
 		}
 
 		final boolean intercepted;
 		if ( !entry.isBeingReplicated() ) {
 			// give the Interceptor a chance to process property values, if the properties
 			// were modified by the Interceptor, we need to set them back to the object
 			intercepted = handleInterception( event );
 		}
 		else {
 			intercepted = false;
 		}
 
 		validate( entity, persister, status, entityMode );
 
 		// increment the version number (if necessary)
 		final Object nextVersion = getNextVersion(event);
 
 		// if it was dirtied by a collection only
 		int[] dirtyProperties = event.getDirtyProperties();
 		if ( event.isDirtyCheckPossible() && dirtyProperties == null ) {
 			if ( ! intercepted && !event.hasDirtyCollection() ) {
 				throw new AssertionFailure( "dirty, but no dirty properties" );
 			}
 			dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;
 		}
 
 		// check nullability but do not doAfterTransactionCompletion command execute
 		// we'll use scheduled updates for that.
 		new Nullability(session).checkNullability( values, persister, true );
 
 		// schedule the update
 		// note that we intentionally do _not_ pass in currentPersistentState!
 		session.getActionQueue().addAction(
 				new EntityUpdateAction(
 						entry.getId(),
 						values,
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						( status == Status.DELETED && ! entry.isModifiableEntity() ?
 								persister.getPropertyValues( entity, entityMode ) :
 								entry.getLoadedState() ),
 						entry.getVersion(),
 						nextVersion,
 						entity,
 						entry.getRowId(),
 						persister,
 						session
 					)
 			);
 
 		return intercepted;
 	}
 
 	protected void validate(Object entity, EntityPersister persister, Status status, EntityMode entityMode) {
 		// validate() instances of Validatable
 		if ( status == Status.MANAGED && persister.implementsValidatable( entityMode ) ) {
 			( (Validatable) entity ).validate();
 		}
 	}
 
 	protected boolean handleInterception(FlushEntityEvent event) {
 		SessionImplementor session = event.getSession();
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		Object entity = event.getEntity();
 
 		//give the Interceptor a chance to modify property values
 		final Object[] values = event.getPropertyValues();
 		final boolean intercepted = invokeInterceptor( session, entity, entry, values, persister );
 
 		//now we might need to recalculate the dirtyProperties array
 		if ( intercepted && event.isDirtyCheckPossible() && !event.isDirtyCheckHandledByInterceptor() ) {
 			int[] dirtyProperties;
 			if ( event.hasDatabaseSnapshot() ) {
 				dirtyProperties = persister.findModified( event.getDatabaseSnapshot(), values, entity, session );
 			}
 			else {
 				dirtyProperties = persister.findDirty( values, entry.getLoadedState(), entity, session );
 			}
 			event.setDirtyProperties(dirtyProperties);
 		}
 
 		return intercepted;
 	}
 
 	protected boolean invokeInterceptor(
 			SessionImplementor session,
 			Object entity,
 			EntityEntry entry,
 			final Object[] values,
 			EntityPersister persister) {
 		return session.getInterceptor().onFlushDirty(
 				entity,
 				entry.getId(),
 				values,
 				entry.getLoadedState(),
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 	}
 
 	/**
 	 * Convience method to retreive an entities next version value
 	 */
 	private Object getNextVersion(FlushEntityEvent event) throws HibernateException {
 
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		if ( persister.isVersioned() ) {
 
 			Object[] values = event.getPropertyValues();
 
 			if ( entry.isBeingReplicated() ) {
 				return Versioning.getVersion(values, persister);
 			}
 			else {
 				int[] dirtyProperties = event.getDirtyProperties();
 
 				final boolean isVersionIncrementRequired = isVersionIncrementRequired(
 						event,
 						entry,
 						persister,
 						dirtyProperties
 					);
 
 				final Object nextVersion = isVersionIncrementRequired ?
 						Versioning.increment( entry.getVersion(), persister.getVersionType(), event.getSession() ) :
 						entry.getVersion(); //use the current version
 
 				Versioning.setVersion(values, nextVersion, persister);
 
 				return nextVersion;
 			}
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private boolean isVersionIncrementRequired(
 			FlushEntityEvent event,
 			EntityEntry entry,
 			EntityPersister persister,
 			int[] dirtyProperties
 	) {
 		final boolean isVersionIncrementRequired = entry.getStatus()!=Status.DELETED && (
 				dirtyProperties==null ||
 				Versioning.isVersionIncrementRequired(
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						persister.getPropertyVersionability()
 					)
 			);
 		return isVersionIncrementRequired;
 	}
 
 	/**
 	 * Performs all necessary checking to determine if an entity needs an SQL update
 	 * to synchronize its state to the database. Modifies the event by side-effect!
 	 * Note: this method is quite slow, avoid calling if possible!
 	 */
 	protected final boolean isUpdateNecessary(FlushEntityEvent event) throws HibernateException {
 
 		EntityPersister persister = event.getEntityEntry().getPersister();
 		Status status = event.getEntityEntry().getStatus();
 
 		if ( !event.isDirtyCheckPossible() ) {
 			return true;
 		}
 		else {
 
 			int[] dirtyProperties = event.getDirtyProperties();
 			if ( dirtyProperties!=null && dirtyProperties.length!=0 ) {
 				return true; //TODO: suck into event class
 			}
 			else {
 				return hasDirtyCollections( event, persister, status );
 			}
 
 		}
 	}
 
 	private boolean hasDirtyCollections(FlushEntityEvent event, EntityPersister persister, Status status) {
 		if ( isCollectionDirtyCheckNecessary(persister, status ) ) {
 			DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(
 					event.getSession(),
 					persister.getPropertyVersionability()
 				);
 			visitor.processEntityPropertyValues( event.getPropertyValues(), persister.getPropertyTypes() );
 			boolean hasDirtyCollections = visitor.wasDirtyCollectionFound();
 			event.setHasDirtyCollection(hasDirtyCollections);
 			return hasDirtyCollections;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isCollectionDirtyCheckNecessary(EntityPersister persister, Status status) {
 		return ( status == Status.MANAGED || status == Status.READ_ONLY ) &&
 				persister.isVersioned() &&
 				persister.hasCollections();
 	}
 
 	/**
 	 * Perform a dirty check, and attach the results to the event
 	 */
 	protected void dirtyCheck(FlushEntityEvent event) throws HibernateException {
 
 		final Object entity = event.getEntity();
 		final Object[] values = event.getPropertyValues();
 		final SessionImplementor session = event.getSession();
 		final EntityEntry entry = event.getEntityEntry();
 		final EntityPersister persister = entry.getPersister();
 		final Serializable id = entry.getId();
 		final Object[] loadedState = entry.getLoadedState();
 
 		int[] dirtyProperties = session.getInterceptor().findDirty(
 				entity,
 				id,
 				values,
 				loadedState,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 			);
 
 		event.setDatabaseSnapshot(null);
 
 		final boolean interceptorHandledDirtyCheck;
 		boolean cannotDirtyCheck;
 
 		if ( dirtyProperties==null ) {
 			// Interceptor returned null, so do the dirtycheck ourself, if possible
 			interceptorHandledDirtyCheck = false;
 
 			cannotDirtyCheck = loadedState==null; // object loaded by update()
 			if ( !cannotDirtyCheck ) {
 				// dirty check against the usual snapshot of the entity
 				dirtyProperties = persister.findDirty( values, loadedState, entity, session );
 			}
 			else if ( entry.getStatus() == Status.DELETED && ! event.getEntityEntry().isModifiableEntity() ) {
 				// A non-modifiable (e.g., read-only or immutable) entity needs to be have
 				// references to transient entities set to null before being deleted. No other
 				// fields should be updated.
 				if ( values != entry.getDeletedState() ) {
 					throw new IllegalStateException(
 							"Entity has status Status.DELETED but values != entry.getDeletedState"
 					);
 				}
 				// Even if loadedState == null, we can dirty-check by comparing currentState and
 				// entry.getDeletedState() because the only fields to be updated are those that
 				// refer to transient entities that are being set to null.
 				// - currentState contains the entity's current property values.
 				// - entry.getDeletedState() contains the entity's current property values with
 				//   references to transient entities set to null.
 				// - dirtyProperties will only contain properties that refer to transient entities
 				final Object[] currentState =
 						persister.getPropertyValues( event.getEntity(), event.getSession().getEntityMode() );
 				dirtyProperties = persister.findDirty( entry.getDeletedState(), currentState, entity, session );
 				cannotDirtyCheck = false;
 			}
 			else {
 				// dirty check against the database snapshot, if possible/necessary
 				final Object[] databaseSnapshot = getDatabaseSnapshot(session, persister, id);
 				if ( databaseSnapshot != null ) {
 					dirtyProperties = persister.findModified(databaseSnapshot, values, entity, session);
 					cannotDirtyCheck = false;
 					event.setDatabaseSnapshot(databaseSnapshot);
 				}
 			}
 		}
 		else {
 			// the Interceptor handled the dirty checking
 			cannotDirtyCheck = false;
 			interceptorHandledDirtyCheck = true;
 		}
 
 		logDirtyProperties( id, dirtyProperties, persister );
 
 		event.setDirtyProperties(dirtyProperties);
 		event.setDirtyCheckHandledByInterceptor(interceptorHandledDirtyCheck);
 		event.setDirtyCheckPossible(!cannotDirtyCheck);
 
 	}
 
 	private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
         if (LOG.isTraceEnabled() && dirtyProperties != null && dirtyProperties.length > 0) {
 			final String[] allPropertyNames = persister.getPropertyNames();
 			final String[] dirtyPropertyNames = new String[ dirtyProperties.length ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				dirtyPropertyNames[i] = allPropertyNames[ dirtyProperties[i]];
 			}
             LOG.trace("Found dirty properties [" + MessageHelper.infoString(persister.getEntityName(), id) + "] : "
                       + dirtyPropertyNames);
 		}
 	}
 
 	private Object[] getDatabaseSnapshot(SessionImplementor session, EntityPersister persister, Serializable id) {
 		if ( persister.isSelectBeforeUpdateRequired() ) {
 			Object[] snapshot = session.getPersistenceContext()
 					.getDatabaseSnapshot(id, persister);
 			if (snapshot==null) {
 				//do we even really need this? the update will fail anyway....
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
             return snapshot;
 		}
         // TODO: optimize away this lookup for entities w/o unsaved-value="undefined"
         EntityKey entityKey = new EntityKey(id, persister, session.getEntityMode());
         return session.getPersistenceContext().getCachedDatabaseSnapshot(entityKey);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
index a2131b114a..f4d49f6170 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
@@ -1,663 +1,663 @@
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
 package org.hibernate.event.def;
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.PropertyValueException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.WrongClassException;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.MergeEvent;
 import org.hibernate.event.MergeEventListener;
-import org.hibernate.intercept.FieldInterceptionHelper;
-import org.hibernate.intercept.FieldInterceptor;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Defines the default copy event listener used by hibernate for copying entities
  * in response to generated copy events.
  *
  * @author Gavin King
  */
 public class DefaultMergeEventListener extends AbstractSaveEventListener
 	implements MergeEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultMergeEventListener.class.getName());
 
 	@Override
     protected Map getMergeMap(Object anything) {
 		return ( ( EventCache ) anything ).invertMap();
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event) throws HibernateException {
 		EventCache copyCache = new EventCache();
 		onMerge( event, copyCache );
 		// TODO: iteratively get transient entities and retry merge until one of the following conditions:
 		//       1) transientCopyCache.size() == 0
 		//       2) transientCopyCache.size() is not decreasing and copyCache.size() is not increasing
 		// TODO: find out if retrying can add entities to copyCache (don't think it can...)
 		// For now, just retry once; throw TransientObjectException if there are still any transient entities
 		Map transientCopyCache = getTransientCopyCache(event, copyCache );
 		if ( transientCopyCache.size() > 0 ) {
 			retryMergeTransientEntities( event, transientCopyCache, copyCache, true );
 			// find any entities that are still transient after retry
 			transientCopyCache = getTransientCopyCache(event, copyCache );
 			if ( transientCopyCache.size() > 0 ) {
 				Set transientEntityNames = new HashSet();
 				for( Iterator it=transientCopyCache.entrySet().iterator(); it.hasNext(); ) {
 					Object transientEntity = ( ( Map.Entry ) it.next() ).getKey();
 					String transientEntityName = event.getSession().guessEntityName( transientEntity );
 					transientEntityNames.add( transientEntityName );
                     LOG.trace("Transient instance could not be processed by merge when checking nullability: "
                               + transientEntityName + "[" + transientEntity + "]");
 				}
                 if (isNullabilityCheckedGlobal(event.getSession())) throw new TransientObjectException(
 						"one or more objects is an unsaved transient instance - save transient instance(s) before merging: " +
 						transientEntityNames );
                 LOG.trace("Retry saving transient instances without checking nullability");
                 // failures will be detected later...
                 retryMergeTransientEntities(event, transientCopyCache, copyCache, false);
 			}
 		}
 		copyCache.clear();
 		copyCache = null;
 	}
 
 	protected EventCache getTransientCopyCache(MergeEvent event, EventCache copyCache) {
 		EventCache transientCopyCache = new EventCache();
 		for ( Iterator it=copyCache.entrySet().iterator(); it.hasNext(); ) {
 			Map.Entry mapEntry = ( Map.Entry ) it.next();
 			Object entity = mapEntry.getKey();
 			Object copy = mapEntry.getValue();
 			if ( copy instanceof HibernateProxy ) {
 				copy = ( (HibernateProxy) copy ).getHibernateLazyInitializer().getImplementation();
 			}
 			EntityEntry copyEntry = event.getSession().getPersistenceContext().getEntry( copy );
 			if ( copyEntry == null ) {
 				// entity name will not be available for non-POJO entities
 				// TODO: cache the entity name somewhere so that it is available to this exception
                 LOG.trace("Transient instance could not be processed by merge: " + event.getSession().guessEntityName(copy) + "["
                           + entity + "]");
 				// merge did not cascade to this entity; it's in copyCache because a
 				// different entity has a non-nullable reference to this entity;
 				// this entity should not be put in transientCopyCache, because it was
 				// not included in the merge;
 				// if the global setting for checking nullability is false, the non-nullable
 				// reference to this entity will be detected later
 				if ( isNullabilityCheckedGlobal( event.getSession() ) ) {
 					throw new TransientObjectException(
 						"object is an unsaved transient instance - save the transient instance before merging: " +
 							event.getSession().guessEntityName( copy )
 					);
 				}
 			}
 			else if ( copyEntry.getStatus() == Status.SAVING ) {
 				transientCopyCache.put( entity, copy, copyCache.isOperatedOn( entity ) );
 			}
 			else if ( copyEntry.getStatus() != Status.MANAGED && copyEntry.getStatus() != Status.READ_ONLY ) {
 				throw new AssertionFailure( "Merged entity does not have status set to MANAGED or READ_ONLY; "+copy+" status="+copyEntry.getStatus() );
 			}
 		}
 		return transientCopyCache;
 	}
 
 	protected void retryMergeTransientEntities(
 			MergeEvent event,
 			Map transientCopyCache,
 			EventCache copyCache,
 			boolean isNullabilityChecked) {
 		// TODO: The order in which entities are saved may matter (e.g., a particular transient entity
 		//       may need to be saved before other transient entities can be saved;
 		//       Keep retrying the batch of transient entities until either:
 		//       1) there are no transient entities left in transientCopyCache
 		//       or 2) no transient entities were saved in the last batch
 		// For now, just run through the transient entities and retry the merge
 		for ( Iterator it=transientCopyCache.entrySet().iterator(); it.hasNext(); ) {
 			Map.Entry mapEntry = ( Map.Entry ) it.next();
 			Object entity = mapEntry.getKey();
 			Object copy = transientCopyCache.get( entity );
 			EntityEntry copyEntry = event.getSession().getPersistenceContext().getEntry( copy );
 			mergeTransientEntity(
 					entity,
 					copyEntry.getEntityName(),
 					( entity == event.getEntity() ? event.getRequestedId() : copyEntry.getId() ),
 					event.getSession(),
 					copyCache,
 					isNullabilityChecked
 			);
 		}
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event, Map copiedAlready) throws HibernateException {
 
 		final EventCache copyCache = ( EventCache ) copiedAlready;
 		final EventSource source = event.getSession();
 		final Object original = event.getOriginal();
 
 		if ( original != null ) {
 
 			final Object entity;
 			if ( original instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) original ).getHibernateLazyInitializer();
 				if ( li.isUninitialized() ) {
                     LOG.trace("Ignoring uninitialized proxy");
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
                 LOG.trace("Already in merge process");
 				event.setResult( entity );
 			}
 			else {
 				if ( copyCache.containsKey( entity ) ) {
                     LOG.trace("Already in copyCache; setting in merge process");
 					copyCache.setOperatedOn( entity, true );
 				}
 				event.setEntity( entity );
 				int entityState = -1;
 
 				// Check the persistence context for an entry relating to this
 				// entity to be merged...
 				EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 				if ( entry == null ) {
 					EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 					Serializable id = persister.getIdentifier( entity, source );
 					if ( id != null ) {
 						EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
 						Object managedEntity = source.getPersistenceContext().getEntity( key );
 						entry = source.getPersistenceContext().getEntry( managedEntity );
 						if ( entry != null ) {
 							// we have specialized case of a detached entity from the
 							// perspective of the merge operation.  Specifically, we
 							// have an incoming entity instance which has a corresponding
 							// entry in the current persistence context, but registered
 							// under a different entity instance
 							entityState = DETACHED;
 						}
 					}
 				}
 
 				if ( entityState == -1 ) {
 					entityState = getEntityState( entity, event.getEntityName(), entry, source );
 				}
 
 				switch (entityState) {
 					case DETACHED:
 						entityIsDetached(event, copyCache);
 						break;
 					case TRANSIENT:
 						entityIsTransient(event, copyCache);
 						break;
 					case PERSISTENT:
 						entityIsPersistent(event, copyCache);
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
         LOG.trace("Ignoring persistent instance");
 
 		//TODO: check that entry.getIdentifier().equals(requestedId)
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		( ( EventCache ) copyCache ).put( entity, entity, true  );  //before cascade!
 
 		cascadeOnMerge(source, persister, entity, copyCache);
 		copyValues(persister, entity, entity, source, copyCache);
 
 		event.setResult(entity);
 	}
 
 	protected void entityIsTransient(MergeEvent event, Map copyCache) {
 
         LOG.trace("Merging transient instance");
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 		final String entityName = persister.getEntityName();
 
 		event.setResult( mergeTransientEntity( entity, entityName, event.getRequestedId(), source, copyCache, true ) );
 	}
 
 	protected Object mergeTransientEntity(Object entity, String entityName, Serializable requestedId, EventSource source, Map copyCache) {
 		return mergeTransientEntity( entity, entityName, requestedId, source, copyCache, true );
 	}
 
 	private Object mergeTransientEntity(
 			Object entity,
 			String entityName,
 			Serializable requestedId,
 			EventSource source,
 			Map copyCache,
 			boolean isNullabilityChecked) {
 
         LOG.trace("Merging transient instance");
 
 		final EntityPersister persister = source.getEntityPersister( entityName, entity );
 
 		final Serializable id = persister.hasIdentifierProperty() ?
 				persister.getIdentifier( entity, source ) :
 		        null;
 		if ( copyCache.containsKey( entity ) ) {
 			persister.setIdentifier( copyCache.get( entity ), id, source );
 		}
 		else {
 			( ( EventCache ) copyCache ).put( entity, source.instantiate( persister, id ), true ); //before cascade!
 		}
 		final Object copy = copyCache.get( entity );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		//cascadeOnMerge(event, persister, entity, copyCache, Cascades.CASCADE_BEFORE_MERGE);
 		super.cascadeBeforeSave(source, persister, entity, copyCache);
 		copyValues(persister, entity, copy, source, copyCache, ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT);
 
 		try {
 			// try saving; check for non-nullable properties that are null or transient entities before saving
 			saveTransientEntity( copy, entityName, requestedId, source, copyCache, isNullabilityChecked );
 		}
 		catch (PropertyValueException ex) {
 			String propertyName = ex.getPropertyName();
 			Object propertyFromCopy = persister.getPropertyValue( copy, propertyName, source.getEntityMode() );
 			Object propertyFromEntity = persister.getPropertyValue( entity, propertyName, source.getEntityMode() );
 			Type propertyType = persister.getPropertyType( propertyName );
 			EntityEntry copyEntry = source.getPersistenceContext().getEntry( copy );
 			if ( propertyFromCopy == null ||
 					propertyFromEntity == null ||
 					! propertyType.isEntityType() ||
 					! copyCache.containsKey( propertyFromEntity ) ) {
 				if ( LOG.isTraceEnabled() ) {
                     LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName + "' in copy is "
                               + (propertyFromCopy == null ? "null" : propertyFromCopy));
                     LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName + "' in original is "
                               + (propertyFromCopy == null ? "null" : propertyFromCopy));
                     LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName + "' is"
                               + (propertyType.isEntityType() ? "" : " not") + " an entity type");
                     if (propertyFromEntity != null && !copyCache.containsKey(propertyFromEntity)) LOG.trace("Property '"
                                                                                                             + copyEntry.getEntityName()
                                                                                                             + "."
                                                                                                             + propertyName
                                                                                                             + "' is not in copy cache");
 	            }
                 if ( isNullabilityCheckedGlobal( source ) ) {
                     throw ex;
                 }
                 else {
                     // retry save w/o checking for non-nullable properties
                     // (the failure will be detected later)
                     saveTransientEntity( copy, entityName, requestedId, source, copyCache, false );
 				}
 			}
 			if ( LOG.isTraceEnabled() && propertyFromEntity != null ) {
                 if (((EventCache)copyCache).isOperatedOn(propertyFromEntity)) LOG.trace("Property '"
                                                                                         + copyEntry.getEntityName()
                                                                                         + "."
                                                                                         + propertyName
                                                                                         + "' from original entity is in copyCache and is in the process of being merged; "
                                                                                         + propertyName + " =[" + propertyFromEntity
                                                                                         + "]");
                 else LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName
                                + "' from original entity is in copyCache and is not in the process of being merged; "
                                + propertyName + " =[" + propertyFromEntity + "]");
 			}
 			// continue...; we'll find out if it ends up not getting saved later
 		}
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		super.cascadeAfterSave(source, persister, entity, copyCache);
 		copyValues(persister, entity, copy, source, copyCache, ForeignKeyDirection.FOREIGN_KEY_TO_PARENT);
 
 		return copy;
 
 	}
 
 	private boolean isNullabilityCheckedGlobal(EventSource source) {
 		return source.getFactory().getSettings().isCheckNullability();
 	}
 
 	private void saveTransientEntity(
 			Object entity,
 			String entityName,
 			Serializable requestedId,
 			EventSource source,
 			Map copyCache,
 			boolean isNullabilityChecked) {
 
 		boolean isNullabilityCheckedOrig =
 			source.getFactory().getSettings().isCheckNullability();
 		try {
 			source.getFactory().getSettings().setCheckNullability( isNullabilityChecked );
 			//this bit is only *really* absolutely necessary for handling
 			//requestedId, but is also good if we merge multiple object
 			//graphs, since it helps ensure uniqueness
 			if (requestedId==null) {
 				saveWithGeneratedId( entity, entityName, copyCache, source, false );
 			}
 			else {
 				saveWithRequestedId( entity, requestedId, entityName, copyCache, source );
 			}
 		}
 		finally {
 			source.getFactory().getSettings().setCheckNullability( isNullabilityCheckedOrig );
 		}
 	}
 	protected void entityIsDetached(MergeEvent event, Map copyCache) {
 
         LOG.trace("Merging detached instance");
 
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
 			if ( !persister.getIdentifierType().isEqual( id, entityId, source.getEntityMode(), source.getFactory() ) ) {
 				throw new HibernateException( "merge requested with id not matching id of passed entity" );
 			}
 		}
 
 		String previousFetchProfile = source.getFetchProfile();
 		source.setFetchProfile("merge");
 		//we must clone embedded composite identifiers, or
 		//we will get back the same instance that we pass in
 		final Serializable clonedIdentifier = (Serializable) persister.getIdentifierType()
 				.deepCopy( id, source.getEntityMode(), source.getFactory() );
 		final Object result = source.get(entityName, clonedIdentifier);
 		source.setFetchProfile(previousFetchProfile);
 
 		if ( result == null ) {
 			//TODO: we should throw an exception if we really *know* for sure
 			//      that this is a detached instance, rather than just assuming
 			//throw new StaleObjectStateException(entityName, id);
 
 			// we got here because we assumed that an instance
 			// with an assigned id was detached, when it was
 			// really persistent
 			entityIsTransient(event, copyCache);
 		}
 		else {
 			( ( EventCache ) copyCache ).put( entity, result, true ); //before cascade!
 
 			final Object target = source.getPersistenceContext().unproxy(result);
 			if ( target == entity ) {
 				throw new AssertionFailure("entity was not detached");
 			}
 			else if ( !source.getEntityName(target).equals(entityName) ) {
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
 			cascadeOnMerge(source, persister, entity, copyCache);
 			copyValues(persister, entity, target, source, copyCache);
 
 			//copyValues works by reflection, so explicitly mark the entity instance dirty
 			markInterceptorDirty( entity, target );
 
 			event.setResult(result);
 		}
 
 	}
 
 	private void markInterceptorDirty(final Object entity, final Object target) {
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( target );
 			if ( interceptor != null ) {
 				interceptor.dirty();
 			}
 		}
 	}
 
 	private boolean isVersionChanged(Object entity, EventSource source, EntityPersister persister, Object target) {
 		if ( ! persister.isVersioned() ) {
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
 		boolean changed = ! persister.getVersionType().isSame(
 				persister.getVersion( target, source.getEntityMode() ),
 				persister.getVersion( entity, source.getEntityMode() ),
 				source.getEntityMode()
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
 				EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
 				Object managedEntity = source.getPersistenceContext().getEntity( key );
 				entry = source.getPersistenceContext().getEntry( managedEntity );
 			}
 		}
 
 		if ( entry == null ) {
 			// perhaps this should be an exception since it is only ever used
 			// in the above method?
 			return false;
 		}
 		else {
 			return entry.isExistsInDatabase();
 		}
 	}
 
 	protected void copyValues(
 		final EntityPersister persister,
 		final Object entity,
 		final Object target,
 		final SessionImplementor source,
 		final Map copyCache
 	) {
 		final Object[] copiedValues = TypeHelper.replace(
 				persister.getPropertyValues( entity, source.getEntityMode() ),
 				persister.getPropertyValues( target, source.getEntityMode() ),
 				persister.getPropertyTypes(),
 				source,
 				target,
 				copyCache
 			);
 
 		persister.setPropertyValues( target, copiedValues, source.getEntityMode() );
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 
 		final Object[] copiedValues;
 
 		if ( foreignKeyDirection == ForeignKeyDirection.FOREIGN_KEY_TO_PARENT ) {
 			// this is the second pass through on a merge op, so here we limit the
 			// replacement to associations types (value types were already replaced
 			// during the first pass)
 			copiedValues = TypeHelper.replaceAssociations(
 					persister.getPropertyValues( entity, source.getEntityMode() ),
 					persister.getPropertyValues( target, source.getEntityMode() ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 		else {
 			copiedValues = TypeHelper.replace(
 					persister.getPropertyValues( entity, source.getEntityMode() ),
 					persister.getPropertyValues( target, source.getEntityMode() ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 
 		persister.setPropertyValues( target, copiedValues, source.getEntityMode() );
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
 			new Cascade( getCascadeAction(), Cascade.BEFORE_MERGE, source )
 					.cascade(persister, entity, copyCache);
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingAction.MERGE;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
deleted file mode 100644
index a23192c450..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
+++ /dev/null
@@ -1,106 +0,0 @@
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
-package org.hibernate.intercept;
-import java.util.Set;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.javassist.JavassistHelper;
-
-/**
- * Helper class for dealing with enhanced entity classes.
- *
- * @author Steve Ebersole
- */
-public class FieldInterceptionHelper {
-
-	// VERY IMPORTANT!!!! - This class needs to be free of any static references
-	// to any CGLIB or Javassist classes.  Otherwise, users will always need both
-	// on their classpaths no matter which (if either) they use.
-	//
-	// Another option here would be to remove the Hibernate.isPropertyInitialized()
-	// method and have the users go through the SessionFactory to get this information.
-
-	private FieldInterceptionHelper() {
-	}
-
-	public static boolean isInstrumented(Class entityClass) {
-		Class[] definedInterfaces = entityClass.getInterfaces();
-		for ( int i = 0; i < definedInterfaces.length; i++ ) {
-			if ( "net.sf.cglib.transform.impl.InterceptFieldEnabled".equals( definedInterfaces[i].getName() )
-			     || "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
-				return true;
-			}
-		}
-		return false;
-	}
-
-	public static boolean isInstrumented(Object entity) {
-		return entity != null && isInstrumented( entity.getClass() );
-	}
-
-	public static FieldInterceptor extractFieldInterceptor(Object entity) {
-		if ( entity == null ) {
-			return null;
-		}
-		Class[] definedInterfaces = entity.getClass().getInterfaces();
-		for ( int i = 0; i < definedInterfaces.length; i++ ) {
-			if ( "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
-				// we have a Javassist enhanced entity
-				return JavassistHelper.extractFieldInterceptor( entity );
-			}
-		}
-		return null;
-	}
-
-	public static FieldInterceptor injectFieldInterceptor(
-			Object entity,
-	        String entityName,
-	        Set uninitializedFieldNames,
-	        SessionImplementor session) {
-		if ( entity != null ) {
-			Class[] definedInterfaces = entity.getClass().getInterfaces();
-			for ( int i = 0; i < definedInterfaces.length; i++ ) {
-				if ( "org.hibernate.bytecode.javassist.FieldHandled".equals( definedInterfaces[i].getName() ) ) {
-					// we have a Javassist enhanced entity
-					return JavassistHelper.injectFieldInterceptor( entity, entityName, uninitializedFieldNames, session );
-				}
-			}
-		}
-		return null;
-	}
-
-	public static void clearDirty(Object entity) {
-		FieldInterceptor interceptor = extractFieldInterceptor( entity );
-		if ( interceptor != null ) {
-			interceptor.clearDirty();
-		}
-	}
-
-	public static void markDirty(Object entity) {
-		FieldInterceptor interceptor = extractFieldInterceptor( entity );
-		if ( interceptor != null ) {
-			interceptor.dirty();
-		}
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 58ee26e11a..ae47877f7f 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1076 +1,1076 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.StructuredCacheEntry;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.engine.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.impl.FilterHelper;
-import org.hibernate.intercept.FieldInterceptionHelper;
-import org.hibernate.intercept.FieldInterceptor;
-import org.hibernate.intercept.LazyPropertyInitializer;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoader;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.Tuplizer;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 import org.jboss.logging.Logger;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 		SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        AbstractEntityPersister.class.getName());
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryStructure cacheEntryStructure;
 	private final EntityMetamodel entityMetamodel;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	private final int batchSize;
 	private final boolean hasSubselectLoadableCollections;
 	protected final String rowIdName;
 
 	private final Set lazyProperties;
 
 	// The optional SQL string defined in the where attribute
 	private final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	//information about properties of this class,
 	//including inherited properties
 	//(only really needed for updatable/insertable properties)
 	private final int[] propertyColumnSpans;
 	private final String[] propertySubclassNames;
 	private final String[][] propertyColumnAliases;
 	private final String[][] propertyColumnNames;
 	private final String[][] propertyColumnFormulaTemplates;
 	private final String[][] propertyColumnReaderTemplates;
 	private final String[][] propertyColumnWriters;
 	private final boolean[][] propertyColumnUpdateable;
 	private final boolean[][] propertyColumnInsertable;
 	private final boolean[] propertyUniqueness;
 	private final boolean[] propertySelectable;
 
 	//information about lazy properties of this class
 	private final String[] lazyPropertyNames;
 	private final int[] lazyPropertyNumbers;
 	private final Type[] lazyPropertyTypes;
 	private final String[][] lazyPropertyColumnAliases;
 
 	//information about all properties in class hierarchy
 	private final String[] subclassPropertyNameClosure;
 	private final String[] subclassPropertySubclassNameClosure;
 	private final Type[] subclassPropertyTypeClosure;
 	private final String[][] subclassPropertyFormulaTemplateClosure;
 	private final String[][] subclassPropertyColumnNameClosure;
 	private final String[][] subclassPropertyColumnReaderClosure;
 	private final String[][] subclassPropertyColumnReaderTemplateClosure;
 	private final FetchMode[] subclassPropertyFetchModeClosure;
 	private final boolean[] subclassPropertyNullabilityClosure;
 	private final boolean[] propertyDefinedOnSubclass;
 	private final int[][] subclassPropertyColumnNumberClosure;
 	private final int[][] subclassPropertyFormulaNumberClosure;
 	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
 
 	//information about all columns/formulas in class hierarchy
 	private final String[] subclassColumnClosure;
 	private final boolean[] subclassColumnLazyClosure;
 	private final String[] subclassColumnAliasClosure;
 	private final boolean[] subclassColumnSelectableClosure;
 	private final String[] subclassColumnReaderTemplateClosure;
 	private final String[] subclassFormulaClosure;
 	private final String[] subclassFormulaTemplateClosure;
 	private final String[] subclassFormulaAliasClosure;
 	private final boolean[] subclassFormulaLazyClosure;
 
 	// dynamic filters attached to the class-level
 	private final FilterHelper filterHelper;
 
 	private final Set affectingFetchProfileNames = new HashSet();
 
 	private final Map uniqueKeyLoaders = new HashMap();
 	private final Map lockers = new HashMap();
 	private final Map loaders = new HashMap();
 
 	// SQL strings
 	private String sqlVersionSelectString;
 	private String sqlSnapshotSelectString;
 	private String sqlLazySelectString;
 
 	private String sqlIdentityInsertString;
 	private String sqlUpdateByRowIdString;
 	private String sqlLazyUpdateByRowIdString;
 
 	private String[] sqlDeleteStrings;
 	private String[] sqlInsertStrings;
 	private String[] sqlUpdateStrings;
 	private String[] sqlLazyUpdateStrings;
 
 	private String sqlInsertGeneratedValuesSelectString;
 	private String sqlUpdateGeneratedValuesSelectString;
 
 	//Custom SQL (would be better if these were private)
 	protected boolean[] insertCallable;
 	protected boolean[] updateCallable;
 	protected boolean[] deleteCallable;
 	protected String[] customSQLInsert;
 	protected String[] customSQLUpdate;
 	protected String[] customSQLDelete;
 	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
 
 	private InsertGeneratedIdentifierDelegate identityDelegate;
 
 	private boolean[] tableHasColumns;
 
 	private final String loaderName;
 
 	private UniqueEntityLoader queryLoader;
 
 	private final String temporaryIdTableName;
 	private final String temporaryIdTableDDL;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	protected void addDiscriminatorToInsert(Insert insert) {}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}
 
 	protected abstract int[] getSubclassColumnTableNumberClosure();
 
 	protected abstract int[] getSubclassFormulaTableNumberClosure();
 
 	public abstract String getSubclassTableName(int j);
 
 	protected abstract String[] getSubclassTableKeyColumns(int j);
 
 	protected abstract boolean isClassOrSuperclassTable(int j);
 
 	protected abstract int getSubclassTableSpan();
 
 	protected abstract int getTableSpan();
 
 	protected abstract boolean isTableCascadeDeleteEnabled(int j);
 
 	protected abstract String getTableName(int j);
 
 	protected abstract String[] getKeyColumns(int j);
 
 	protected abstract boolean isPropertyOfTable(int property, int j);
 
 	protected abstract int[] getPropertyTableNumbersInSelect();
 
 	protected abstract int[] getPropertyTableNumbers();
 
 	protected abstract int getSubclassPropertyTableNumber(int i);
 
 	protected abstract String filterFragment(String alias) throws MappingException;
 
 	private static final String DISCRIMINATOR_ALIAS = "clazz_";
 
 	public String getDiscriminatorColumnName() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaderTemplate() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorAlias() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return null;
 	}
 
 	protected boolean isInverseTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return false;
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return false;
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return entityMetamodel.getSubclassEntityNames().contains(entityName);
 	}
 
 	private boolean[] getTableHasColumns() {
 		return tableHasColumns;
 	}
 
 	public String[] getRootTableKeyColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	protected String[] getSQLUpdateByRowIdStrings() {
 		if ( sqlUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan() + 1];
 		result[0] = sqlUpdateByRowIdString;
 		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
 		return result;
 	}
 
 	protected String[] getSQLLazyUpdateByRowIdStrings() {
 		if ( sqlLazyUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan()];
 		result[0] = sqlLazyUpdateByRowIdString;
 		for ( int i = 1; i < getTableSpan(); i++ ) {
 			result[i] = sqlLazyUpdateStrings[i];
 		}
 		return result;
 	}
 
 	protected String getSQLSnapshotSelectString() {
 		return sqlSnapshotSelectString;
 	}
 
 	protected String getSQLLazySelectString() {
 		return sqlLazySelectString;
 	}
 
 	protected String[] getSQLDeleteStrings() {
 		return sqlDeleteStrings;
 	}
 
 	protected String[] getSQLInsertStrings() {
 		return sqlInsertStrings;
 	}
 
 	protected String[] getSQLUpdateStrings() {
 		return sqlUpdateStrings;
 	}
 
 	protected String[] getSQLLazyUpdateStrings() {
 		return sqlLazyUpdateStrings;
 	}
 
 	/**
 	 * The query that inserts a row, letting the database generate an id
 	 *
 	 * @return The IDENTITY-based insertion query.
 	 */
 	protected String getSQLIdentityInsertString() {
 		return sqlIdentityInsertString;
 	}
 
 	protected String getVersionSelectString() {
 		return sqlVersionSelectString;
 	}
 
 	protected boolean isInsertCallable(int j) {
 		return insertCallable[j];
 	}
 
 	protected boolean isUpdateCallable(int j) {
 		return updateCallable[j];
 	}
 
 	protected boolean isDeleteCallable(int j) {
 		return deleteCallable[j];
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return false;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return false;
 	}
 
 	public boolean hasSequentialSelect() {
 		return false;
 	}
 
 	/**
 	 * Decide which tables need to be updated.
 	 * <p/>
 	 * The return here is an array of boolean values with each index corresponding
 	 * to a given table in the scope of this persister.
 	 *
 	 * @param dirtyProperties The indices of all the entity properties considered dirty.
 	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
 	 *
 	 * @return Array of booleans indicating which table require updating.
 	 */
 	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {
 
 		if ( dirtyProperties == null ) {
 			return getTableHasColumns(); // for objects that came in via update()
 		}
 		else {
 			boolean[] updateability = getPropertyUpdateability();
 			int[] propertyTableNumbers = getPropertyTableNumbers();
 			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				int property = dirtyProperties[i];
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan(property) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 			}
 			return tableUpdateNeeded;
 		}
 	}
 
 	public boolean hasRowId() {
 		return rowIdName != null;
 	}
 
 	protected boolean[][] getPropertyColumnUpdateable() {
 		return propertyColumnUpdateable;
 	}
 
 	protected boolean[][] getPropertyColumnInsertable() {
 		return propertyColumnInsertable;
 	}
 
 	protected boolean[] getPropertySelectable() {
 		return propertySelectable;
 	}
 
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 		this.cacheEntryStructure = factory.getSettings().isStructuredCacheEntriesEnabled() ?
 				(CacheEntryStructure) new StructuredCacheEntry(this) :
 				(CacheEntryStructure) new UnstructuredCacheEntry();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = persistentClass.getRootTable().getRowId();
 
 		loaderName = persistentClass.getLoaderName();
 
 		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Column col = ( Column ) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		iter = persistentClass.getPropertyClosureIterator();
 		i = 0;
 		boolean foundFormula = false;
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			thisClassProperties.add( prop );
 
 			int span = prop.getColumnSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			Iterator colIter = prop.getColumnIterator();
 			int k = 0;
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column)thing;
 					colNames[k] = col.getQuotedName( factory.getDialect() );
 					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					colWriters[k] = col.getWriteExpr();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getName() );
 				lazyNames.add( prop.getName() );
 				lazyNumbers.add( new Integer( i ) );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList joinedFetchesList = new ArrayList();
 		ArrayList cascades = new ArrayList();
 		ArrayList definedBySubclass = new ArrayList();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			names.add( prop.getName() );
 			classes.add( prop.getPersistentClass().getEntityName() );
 			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
 			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
 			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
 			types.add( prop.getType() );
 
 			Iterator colIter = prop.getColumnIterator();
 			String[] cols = new String[prop.getColumnSpan()];
 			String[] readers = new String[prop.getColumnSpan()];
 			String[] readerTemplates = new String[prop.getColumnSpan()];
 			String[] forms = new String[prop.getColumnSpan()];
 			int[] colnos = new int[prop.getColumnSpan()];
 			int[] formnos = new int[prop.getColumnSpan()];
 			int l = 0;
 			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				if ( thing.isFormula() ) {
 					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( thing.getText( factory.getDialect() ) );
 					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					Column col = (Column)thing;
 					String colName = col.getQuotedName( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
 					columnsLazy.add( lazy );
 					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
 
 					readers[l] = col.getReadExpr( factory.getDialect() );
 					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					readerTemplates[l] = readerTemplate;
 					columnReaderTemplates.add( readerTemplate );
 				}
 				l++;
 			}
 			propColumns.add( cols );
 			propColumnReaders.add( readers );
 			propColumnReaderTemplates.add( readerTemplates );
 			templates.add( forms );
 			propColumnNumbers.add( colnos );
 			propFormulaNumbers.add( formnos );
 
 			joinedFetchesList.add( prop.getValue().getFetchMode() );
 			cascades.add( prop.getCascadeStyle() );
 		}
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
 		iter = cascades.iterator();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilterMap(), factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add( new Integer( tableNumber ) );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( new Integer( colNumbers[j] ) );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( new Integer( formNumbers[j] ) );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
         if (LOG.isTraceEnabled()) LOG.trace("Initializing lazy properties of: " + MessageHelper.infoString(this, id, getFactory())
                                             + ", field access: " + fieldName);
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = new CacheKey(id, getIdentifierType(), getEntityName(), session.getEntityMode(), getFactory() );
 			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
 			if (ce!=null) {
 				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
         if (!hasLazyProperties()) throw new AssertionFailure("no lazy properties");
 
         LOG.trace("Initializing lazy properties from datastore");
 
 		try {
 
 			Object result = null;
 			PreparedStatement ps = null;
 			try {
 				final String lazySelect = getSQLLazySelectString();
 				ResultSet rs = null;
 				try {
 					if ( lazySelect != null ) {
 						// null sql means that the only lazy properties
 						// are shared PK one-to-one associations which are
 						// handled differently in the Type#nullSafeGet code...
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = ps.executeQuery();
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					ps.close();
 				}
 			}
 
             LOG.trace("Done initializing lazy properties");
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize lazy properties: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getSQLLazySelectString()
 				);
 		}
 	}
 
 	private Object initializeLazyPropertiesFromCache(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final EntityEntry entry,
 			final CacheEntry cacheEntry
 	) {
 
         LOG.trace("Initializing lazy properties from second-level cache");
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[ lazyPropertyNumbers[j] ],
 					session,
 					entity
 				);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
         LOG.trace("Done initializing lazy properties");
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue, session.getEntityMode() );
 		if (snapshot != null) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, session.getEntityMode(), factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_NONE ||
 			( !isVersioned() && optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_VERSION ) ||
 			getFactory().getSettings().isJdbcBatchVersionedData();
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return getPropertySpaces();
 	}
 
 	protected Set getLazyProperties() {
 		return lazyProperties;
 	}
 
 	public boolean isBatchLoadable() {
 		return batchSize > 1;
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return rootTableKeyColumnReaders;
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return rootTableKeyColumnReaderTemplates;
 	}
 
 	protected int getIdentifierColumnSpan() {
 		return identifierColumnSpan;
 	}
 
 	protected String[] getIdentifierAliases() {
 		return identifierAliases;
 	}
 
 	public String getVersionColumnName() {
 		return versionColumnName;
 	}
 
 	protected String getVersionedTableName() {
 		return getTableName( 0 );
 	}
 
 	protected boolean[] getSubclassColumnLazyiness() {
 		return subclassColumnLazyClosure;
 	}
 
 	protected boolean[] getSubclassFormulaLazyiness() {
 		return subclassFormulaLazyClosure;
 	}
 
 	/**
 	 * We can't immediately add to the cache if we have formulas
 	 * which must be evaluated, or if we have the possibility of
 	 * two concurrent updates to the same item being merged on
 	 * the database. This can happen if (a) the item is not
 	 * versioned and either (b) we have dynamic update enabled
 	 * or (c) we have multiple tables holding the state of the
 	 * item.
 	 */
 	public boolean isCacheInvalidationRequired() {
 		return hasFormulaProperties() ||
 				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return isLazyPropertiesCacheable;
 	}
 
 	public String selectFragment(String alias, String suffix) {
 		return identifierSelectFragment( alias, suffix ) +
 				propertySelectFragment( alias, suffix, false );
 	}
 
 	public String[] getIdentifierAliases(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
 	}
 
 	public String[] getPropertyAliases(String suffix, int i) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
 	}
 
 	public String getDiscriminatorAlias(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return entityMetamodel.hasSubclasses() ?
 				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
 				null;
 	}
 
 	public String identifierSelectFragment(String name, String suffix) {
 		return new SelectFragment()
 				.setSuffix( suffix )
 				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
 				.toFragmentString()
 				.substring( 2 ); //strip leading ", "
 	}
 
 
 	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
 		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
 	}
 
 	public SelectFragment propertySelectFragmentFragment(
 			String tableAlias,
 			String suffix,
 			boolean allProperties) {
 		SelectFragment select = new SelectFragment()
 				.setSuffix( suffix )
 				.setUsedAliases( getIdentifierAliases() );
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
 				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
 				subclassColumnSelectableClosure[i];
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
 				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
 				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
diff --git a/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java b/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
index 0cf2327a77..acfd6424ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
+++ b/hibernate-core/src/main/java/org/hibernate/pretty/Printer.java
@@ -1,119 +1,119 @@
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
 package org.hibernate.pretty;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.TypedValue;
-import org.hibernate.intercept.LazyPropertyInitializer;
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * Renders entities to a nicely readable string.
  * @author Gavin King
  */
 public final class Printer {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Printer.class.getName());
 
     private SessionFactoryImplementor factory;
 
 	/**
 	 * @param entity an actual entity object, not a proxy!
 	 */
 	public String toString(Object entity, EntityMode entityMode) throws HibernateException {
 
 		// todo : this call will not work for anything other than pojos!
 		ClassMetadata cm = factory.getClassMetadata( entity.getClass() );
 
 		if ( cm==null ) return entity.getClass().getName();
 
 		Map result = new HashMap();
 
 		if ( cm.hasIdentifierProperty() ) {
 			result.put(
 				cm.getIdentifierPropertyName(),
 				cm.getIdentifierType().toLoggableString( cm.getIdentifier( entity, entityMode ), factory )
 			);
 		}
 
 		Type[] types = cm.getPropertyTypes();
 		String[] names = cm.getPropertyNames();
 		Object[] values = cm.getPropertyValues( entity, entityMode );
 		for ( int i=0; i<types.length; i++ ) {
 			if ( !names[i].startsWith("_") ) {
 				String strValue = values[i]==LazyPropertyInitializer.UNFETCHED_PROPERTY ?
 					values[i].toString() :
 					types[i].toLoggableString( values[i], factory );
 				result.put( names[i], strValue );
 			}
 		}
 		return cm.getEntityName() + result.toString();
 	}
 
 	public String toString(Type[] types, Object[] values) throws HibernateException {
 		List list = new ArrayList( types.length * 5 );
 		for ( int i=0; i<types.length; i++ ) {
 			if ( types[i]!=null ) list.add( types[i].toLoggableString( values[i], factory ) );
 		}
 		return list.toString();
 	}
 
 	public String toString(Map namedTypedValues) throws HibernateException {
 		Map result = new HashMap();
 		Iterator iter = namedTypedValues.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			TypedValue tv = (TypedValue) me.getValue();
 			result.put( me.getKey(), tv.getType().toLoggableString( tv.getValue(), factory ) );
 		}
 		return result.toString();
 	}
 
 	public void toString(Iterator iter, EntityMode entityMode) throws HibernateException {
         if (!LOG.isDebugEnabled() || !iter.hasNext()) return;
         LOG.debugf("Listing entities:");
 		int i=0;
 		while ( iter.hasNext() ) {
 			if (i++>20) {
                 LOG.debugf("More......");
 				break;
 			}
             LOG.debugf(toString(iter.next(), entityMode));
 		}
 	}
 
 	public Printer(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
index ceee5d5a7b..c3c06ca71c 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/instrument/BasicInstrumentationTask.java
@@ -1,135 +1,137 @@
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
 package org.hibernate.tool.instrument;
-
-import java.io.File;
-import java.util.ArrayList;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Set;
-import org.apache.tools.ant.BuildException;
-import org.apache.tools.ant.DirectoryScanner;
-import org.apache.tools.ant.Project;
-import org.apache.tools.ant.Task;
-import org.apache.tools.ant.types.FileSet;
-import org.hibernate.bytecode.buildtime.Instrumenter;
-import org.hibernate.bytecode.buildtime.Logger;
-
-/**
- * Super class for all Hibernate instrumentation tasks.  Provides the basic templating of how instrumentation
- * should occur; subclasses simply plug in to that process appropriately for the given bytecode provider.
- *
- * @author Steve Ebersole
- */
-public abstract class BasicInstrumentationTask extends Task implements Instrumenter.Options {
-
-	private final LoggerBridge logger = new LoggerBridge();
-
-	private List filesets = new ArrayList();
-	private boolean extended;
-
-	// deprecated option...
-	private boolean verbose;
-
-	public void addFileset(FileSet set) {
-		this.filesets.add( set );
-	}
-
-	protected final Iterator filesets() {
-		return filesets.iterator();
-	}
-
-	public boolean isExtended() {
-		return extended;
-	}
-
-	public void setExtended(boolean extended) {
-		this.extended = extended;
-	}
-
-	public boolean isVerbose() {
-		return verbose;
-	}
-
-	public void setVerbose(boolean verbose) {
-		this.verbose = verbose;
-	}
-
-	public final boolean performExtendedInstrumentation() {
-		return isExtended();
-	}
-
-	protected abstract Instrumenter buildInstrumenter(Logger logger, Instrumenter.Options options);
-
-	@Override
-    public void execute() throws BuildException {
-		try {
-			buildInstrumenter( logger, this )
-					.execute( collectSpecifiedFiles() );
-		}
-		catch ( Throwable t ) {
-			throw new BuildException( t );
-		}
-	}
-
-	private Set collectSpecifiedFiles() {
-		HashSet files = new HashSet();
-		Project project = getProject();
-		Iterator filesets = filesets();
-		while ( filesets.hasNext() ) {
-			FileSet fs = ( FileSet ) filesets.next();
-			DirectoryScanner ds = fs.getDirectoryScanner( project );
-			String[] includedFiles = ds.getIncludedFiles();
-			File d = fs.getDir( project );
-			for ( int i = 0; i < includedFiles.length; ++i ) {
-				files.add( new File( d, includedFiles[i] ) );
-			}
-		}
-		return files;
-	}
-
-	protected class LoggerBridge implements Logger {
-		public void trace(String message) {
-			log( message, Project.MSG_VERBOSE );
-		}
-
-		public void debug(String message) {
-			log( message, Project.MSG_DEBUG );
-		}
-
-		public void info(String message) {
-			log( message, Project.MSG_INFO );
-		}
-
-		public void warn(String message) {
-			log( message, Project.MSG_WARN );
-		}
-
-		public void error(String message) {
-			log( message, Project.MSG_ERR );
-		}
-	}
-
-}
+
+import java.io.File;
+import java.util.ArrayList;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Set;
+
+import org.apache.tools.ant.BuildException;
+import org.apache.tools.ant.DirectoryScanner;
+import org.apache.tools.ant.Project;
+import org.apache.tools.ant.Task;
+import org.apache.tools.ant.types.FileSet;
+
+import org.hibernate.bytecode.buildtime.spi.Instrumenter;
+import org.hibernate.bytecode.buildtime.spi.Logger;
+
+/**
+ * Super class for all Hibernate instrumentation tasks.  Provides the basic templating of how instrumentation
+ * should occur; subclasses simply plug in to that process appropriately for the given bytecode provider.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class BasicInstrumentationTask extends Task implements Instrumenter.Options {
+
+	private final LoggerBridge logger = new LoggerBridge();
+
+	private List filesets = new ArrayList();
+	private boolean extended;
+
+	// deprecated option...
+	private boolean verbose;
+
+	public void addFileset(FileSet set) {
+		this.filesets.add( set );
+	}
+
+	protected final Iterator filesets() {
+		return filesets.iterator();
+	}
+
+	public boolean isExtended() {
+		return extended;
+	}
+
+	public void setExtended(boolean extended) {
+		this.extended = extended;
+	}
+
+	public boolean isVerbose() {
+		return verbose;
+	}
+
+	public void setVerbose(boolean verbose) {
+		this.verbose = verbose;
+	}
+
+	public final boolean performExtendedInstrumentation() {
+		return isExtended();
+	}
+
+	protected abstract Instrumenter buildInstrumenter(Logger logger, Instrumenter.Options options);
+
+	@Override
+    public void execute() throws BuildException {
+		try {
+			buildInstrumenter( logger, this )
+					.execute( collectSpecifiedFiles() );
+		}
+		catch ( Throwable t ) {
+			throw new BuildException( t );
+		}
+	}
+
+	private Set collectSpecifiedFiles() {
+		HashSet files = new HashSet();
+		Project project = getProject();
+		Iterator filesets = filesets();
+		while ( filesets.hasNext() ) {
+			FileSet fs = ( FileSet ) filesets.next();
+			DirectoryScanner ds = fs.getDirectoryScanner( project );
+			String[] includedFiles = ds.getIncludedFiles();
+			File d = fs.getDir( project );
+			for ( int i = 0; i < includedFiles.length; ++i ) {
+				files.add( new File( d, includedFiles[i] ) );
+			}
+		}
+		return files;
+	}
+
+	protected class LoggerBridge implements Logger {
+		public void trace(String message) {
+			log( message, Project.MSG_VERBOSE );
+		}
+
+		public void debug(String message) {
+			log( message, Project.MSG_DEBUG );
+		}
+
+		public void info(String message) {
+			log( message, Project.MSG_INFO );
+		}
+
+		public void warn(String message) {
+			log( message, Project.MSG_WARN );
+		}
+
+		public void error(String message) {
+			log( message, Project.MSG_ERR );
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java b/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
index 8a8756d7ef..80ca85c9fb 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/instrument/javassist/InstrumentTask.java
@@ -1,71 +1,71 @@
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
 package org.hibernate.tool.instrument.javassist;
 
-import org.hibernate.bytecode.buildtime.Instrumenter;
-import org.hibernate.bytecode.buildtime.JavassistInstrumenter;
-import org.hibernate.bytecode.buildtime.Logger;
+import org.hibernate.bytecode.buildtime.internal.JavassistInstrumenter;
+import org.hibernate.bytecode.buildtime.spi.Instrumenter;
+import org.hibernate.bytecode.buildtime.spi.Logger;
 import org.hibernate.tool.instrument.BasicInstrumentationTask;
 
 /**
  * An Ant task for instrumenting persistent classes in order to enable
  * field-level interception using Javassist.
  * <p/>
  * In order to use this task, typically you would define a a taskdef
  * similiar to:<pre>
  * <taskdef name="instrument" classname="org.hibernate.tool.instrument.javassist.InstrumentTask">
  *     <classpath refid="lib.class.path"/>
  * </taskdef>
  * </pre>
  * where <tt>lib.class.path</tt> is an ANT path reference containing all the
  * required Hibernate and Javassist libraries.
  * <p/>
  * And then use it like:<pre>
  * <instrument verbose="true">
  *     <fileset dir="${testclasses.dir}/org/hibernate/test">
  *         <include name="yadda/yadda/**"/>
  *         ...
  *     </fileset>
  * </instrument>
  * </pre>
  * where the nested ANT fileset includes the class you would like to have
  * instrumented.
  * <p/>
  * Optionally you can chose to enable "Extended Instrumentation" if desired
  * by specifying the extended attriubute on the task:<pre>
  * <instrument verbose="true" extended="true">
  *     ...
  * </instrument>
  * </pre>
  * See the Hibernate manual regarding this option.
  *
  * @author Muga Nishizawa
  * @author Steve Ebersole
  */
 public class InstrumentTask extends BasicInstrumentationTask {
 	@Override
     protected Instrumenter buildInstrumenter(Logger logger, Instrumenter.Options options) {
 		return new JavassistInstrumenter( logger, options );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java b/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
index b6ae5bb7d1..f3c3d7b342 100755
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PojoInstantiator.java
@@ -1,121 +1,121 @@
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
 package org.hibernate.tuple;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import org.hibernate.HibernateLogger;
 import org.hibernate.InstantiationException;
 import org.hibernate.PropertyNotFoundException;
-import org.hibernate.bytecode.ReflectionOptimizer;
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.jboss.logging.Logger;
 
 /**
  * Defines a POJO-based instantiator for use from the tuplizers.
  */
 public class PojoInstantiator implements Instantiator, Serializable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, PojoInstantiator.class.getName());
 
 	private transient Constructor constructor;
 
 	private final Class mappedClass;
 	private final transient ReflectionOptimizer.InstantiationOptimizer optimizer;
 	private final boolean embeddedIdentifier;
 	private final Class proxyInterface;
 
 	public PojoInstantiator(Component component, ReflectionOptimizer.InstantiationOptimizer optimizer) {
 		this.mappedClass = component.getComponentClass();
 		this.optimizer = optimizer;
 
 		this.proxyInterface = null;
 		this.embeddedIdentifier = false;
 
 		try {
 			constructor = ReflectHelper.getDefaultConstructor(mappedClass);
 		}
 		catch ( PropertyNotFoundException pnfe ) {
             LOG.noDefaultConstructor(mappedClass.getName());
 			constructor = null;
 		}
 	}
 
 	public PojoInstantiator(PersistentClass persistentClass, ReflectionOptimizer.InstantiationOptimizer optimizer) {
 		this.mappedClass = persistentClass.getMappedClass();
 		this.proxyInterface = persistentClass.getProxyInterface();
 		this.embeddedIdentifier = persistentClass.hasEmbeddedIdentifier();
 		this.optimizer = optimizer;
 
 		try {
 			constructor = ReflectHelper.getDefaultConstructor( mappedClass );
 		}
 		catch ( PropertyNotFoundException pnfe ) {
             LOG.noDefaultConstructor(mappedClass.getName());
 			constructor = null;
 		}
 	}
 
 	private void readObject(java.io.ObjectInputStream stream)
 	throws ClassNotFoundException, IOException {
 		stream.defaultReadObject();
 		constructor = ReflectHelper.getDefaultConstructor( mappedClass );
 	}
 
 	public Object instantiate() {
 		if ( ReflectHelper.isAbstractClass(mappedClass) ) {
 			throw new InstantiationException( "Cannot instantiate abstract class or interface: ", mappedClass );
 		}
 		else if ( optimizer != null ) {
 			return optimizer.newInstance();
 		}
 		else if ( constructor == null ) {
 			throw new InstantiationException( "No default constructor for entity: ", mappedClass );
 		}
 		else {
 			try {
 				return constructor.newInstance( (Object[]) null );
 			}
 			catch ( Exception e ) {
 				throw new InstantiationException( "Could not instantiate entity: ", mappedClass, e );
 			}
 		}
 	}
 
 	public Object instantiate(Serializable id) {
 		final boolean useEmbeddedIdentifierInstanceAsEntity = embeddedIdentifier &&
 				id != null &&
 				id.getClass().equals(mappedClass);
 		return useEmbeddedIdentifierInstanceAsEntity ? id : instantiate();
 	}
 
 	public boolean isInstance(Object object) {
 		return mappedClass.isInstance(object) ||
 				( proxyInterface!=null && proxyInterface.isInstance(object) ); //this one needed only for guessEntityMode()
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
index 322b22f0b8..b417cfe110 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/PojoComponentTuplizer.java
@@ -1,191 +1,191 @@
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
 package org.hibernate.tuple.component;
-import java.io.Serializable;
-import java.lang.reflect.Method;
-import org.hibernate.AssertionFailure;
-import org.hibernate.HibernateException;
-import org.hibernate.bytecode.BasicProxyFactory;
-import org.hibernate.bytecode.ReflectionOptimizer;
-import org.hibernate.cfg.Environment;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.mapping.Component;
-import org.hibernate.mapping.Property;
-import org.hibernate.property.BackrefPropertyAccessor;
-import org.hibernate.property.Getter;
-import org.hibernate.property.PropertyAccessor;
-import org.hibernate.property.PropertyAccessorFactory;
-import org.hibernate.property.Setter;
-import org.hibernate.tuple.Instantiator;
-import org.hibernate.tuple.PojoInstantiator;
-import org.hibernate.internal.util.ReflectHelper;
-
-/**
- * A {@link ComponentTuplizer} specific to the pojo entity mode.
- *
- * @author Gavin King
- * @author Steve Ebersole
- */
-public class PojoComponentTuplizer extends AbstractComponentTuplizer {
-	private final Class componentClass;
-	private ReflectionOptimizer optimizer;
-	private final Getter parentGetter;
-	private final Setter parentSetter;
-
-	public PojoComponentTuplizer(Component component) {
-		super( component );
-
-		this.componentClass = component.getComponentClass();
-
-		String[] getterNames = new String[propertySpan];
-		String[] setterNames = new String[propertySpan];
-		Class[] propTypes = new Class[propertySpan];
-		for ( int i = 0; i < propertySpan; i++ ) {
-			getterNames[i] = getters[i].getMethodName();
-			setterNames[i] = setters[i].getMethodName();
-			propTypes[i] = getters[i].getReturnType();
-		}
-
-		final String parentPropertyName = component.getParentProperty();
-		if ( parentPropertyName == null ) {
-			parentSetter = null;
-			parentGetter = null;
-		}
-		else {
-			PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( null );
-			parentSetter = pa.getSetter( componentClass, parentPropertyName );
-			parentGetter = pa.getGetter( componentClass, parentPropertyName );
-		}
-
-		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
-			optimizer = null;
-		}
-		else {
-			// TODO: here is why we need to make bytecode provider global :(
-			// TODO : again, fix this after HHH-1907 is complete
-			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
-					componentClass, getterNames, setterNames, propTypes
-			);
-		}
-	}
-
-	public Class getMappedClass() {
-		return componentClass;
-	}
-
-	public Object[] getPropertyValues(Object component) throws HibernateException {
-		if ( component == BackrefPropertyAccessor.UNKNOWN ) {
-			return new Object[propertySpan];
-		}
-		if ( optimizer != null && optimizer.getAccessOptimizer() != null ) {
-			return optimizer.getAccessOptimizer().getPropertyValues( component );
-		}
-		else {
-			return super.getPropertyValues( component );
-		}
-	}
-
-	public void setPropertyValues(Object component, Object[] values) throws HibernateException {
-		if ( optimizer != null && optimizer.getAccessOptimizer() != null ) {
-			optimizer.getAccessOptimizer().setPropertyValues( component, values );
-		}
-		else {
-			super.setPropertyValues( component, values );
-		}
-	}
-
-	public Object getParent(Object component) {
-		return parentGetter.get( component );
-	}
-
-	public boolean hasParentProperty() {
-		return parentGetter != null;
-	}
-
-	public boolean isMethodOf(Method method) {
-		for ( int i = 0; i < propertySpan; i++ ) {
-			final Method getterMethod = getters[i].getMethod();
-			if ( getterMethod != null && getterMethod.equals( method ) ) {
-				return true;
-			}
-		}
-		return false;
-	}
-
-	public void setParent(Object component, Object parent, SessionFactoryImplementor factory) {
-		parentSetter.set( component, parent, factory );
-	}
-
-	protected Instantiator buildInstantiator(Component component) {
-		if ( component.isEmbedded() && ReflectHelper.isAbstractClass( component.getComponentClass() ) ) {
-			return new ProxiedInstantiator( component );
-		}
-		if ( optimizer == null ) {
-			return new PojoInstantiator( component, null );
-		}
-		else {
-			return new PojoInstantiator( component, optimizer.getInstantiationOptimizer() );
-		}
-	}
-
-	protected Getter buildGetter(Component component, Property prop) {
-		return prop.getGetter( component.getComponentClass() );
-	}
-
-	protected Setter buildSetter(Component component, Property prop) {
-		return prop.getSetter( component.getComponentClass() );
-	}
-
-	private static class ProxiedInstantiator implements Instantiator {
-		private final Class proxiedClass;
-		private final BasicProxyFactory factory;
-
-		public ProxiedInstantiator(Component component) {
-			proxiedClass = component.getComponentClass();
-			if ( proxiedClass.isInterface() ) {
-				factory = Environment.getBytecodeProvider()
-						.getProxyFactoryFactory()
-						.buildBasicProxyFactory( null, new Class[] { proxiedClass } );
-			}
-			else {
-				factory = Environment.getBytecodeProvider()
-						.getProxyFactoryFactory()
-						.buildBasicProxyFactory( proxiedClass, null );
-			}
-		}
-
-		public Object instantiate(Serializable id) {
-			throw new AssertionFailure( "ProxiedInstantiator can only be used to instantiate component" );
-		}
-
-		public Object instantiate() {
-			return factory.getProxy();
-		}
-
-		public boolean isInstance(Object object) {
-			return proxiedClass.isInstance( object );
-		}
-	}
-}
+import java.io.Serializable;
+import java.lang.reflect.Method;
+import org.hibernate.AssertionFailure;
+import org.hibernate.HibernateException;
+import org.hibernate.bytecode.spi.BasicProxyFactory;
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
+import org.hibernate.cfg.Environment;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.mapping.Component;
+import org.hibernate.mapping.Property;
+import org.hibernate.property.BackrefPropertyAccessor;
+import org.hibernate.property.Getter;
+import org.hibernate.property.PropertyAccessor;
+import org.hibernate.property.PropertyAccessorFactory;
+import org.hibernate.property.Setter;
+import org.hibernate.tuple.Instantiator;
+import org.hibernate.tuple.PojoInstantiator;
+import org.hibernate.internal.util.ReflectHelper;
+
+/**
+ * A {@link ComponentTuplizer} specific to the pojo entity mode.
+ *
+ * @author Gavin King
+ * @author Steve Ebersole
+ */
+public class PojoComponentTuplizer extends AbstractComponentTuplizer {
+	private final Class componentClass;
+	private ReflectionOptimizer optimizer;
+	private final Getter parentGetter;
+	private final Setter parentSetter;
+
+	public PojoComponentTuplizer(Component component) {
+		super( component );
+
+		this.componentClass = component.getComponentClass();
+
+		String[] getterNames = new String[propertySpan];
+		String[] setterNames = new String[propertySpan];
+		Class[] propTypes = new Class[propertySpan];
+		for ( int i = 0; i < propertySpan; i++ ) {
+			getterNames[i] = getters[i].getMethodName();
+			setterNames[i] = setters[i].getMethodName();
+			propTypes[i] = getters[i].getReturnType();
+		}
+
+		final String parentPropertyName = component.getParentProperty();
+		if ( parentPropertyName == null ) {
+			parentSetter = null;
+			parentGetter = null;
+		}
+		else {
+			PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( null );
+			parentSetter = pa.getSetter( componentClass, parentPropertyName );
+			parentGetter = pa.getGetter( componentClass, parentPropertyName );
+		}
+
+		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
+			optimizer = null;
+		}
+		else {
+			// TODO: here is why we need to make bytecode provider global :(
+			// TODO : again, fix this after HHH-1907 is complete
+			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
+					componentClass, getterNames, setterNames, propTypes
+			);
+		}
+	}
+
+	public Class getMappedClass() {
+		return componentClass;
+	}
+
+	public Object[] getPropertyValues(Object component) throws HibernateException {
+		if ( component == BackrefPropertyAccessor.UNKNOWN ) {
+			return new Object[propertySpan];
+		}
+		if ( optimizer != null && optimizer.getAccessOptimizer() != null ) {
+			return optimizer.getAccessOptimizer().getPropertyValues( component );
+		}
+		else {
+			return super.getPropertyValues( component );
+		}
+	}
+
+	public void setPropertyValues(Object component, Object[] values) throws HibernateException {
+		if ( optimizer != null && optimizer.getAccessOptimizer() != null ) {
+			optimizer.getAccessOptimizer().setPropertyValues( component, values );
+		}
+		else {
+			super.setPropertyValues( component, values );
+		}
+	}
+
+	public Object getParent(Object component) {
+		return parentGetter.get( component );
+	}
+
+	public boolean hasParentProperty() {
+		return parentGetter != null;
+	}
+
+	public boolean isMethodOf(Method method) {
+		for ( int i = 0; i < propertySpan; i++ ) {
+			final Method getterMethod = getters[i].getMethod();
+			if ( getterMethod != null && getterMethod.equals( method ) ) {
+				return true;
+			}
+		}
+		return false;
+	}
+
+	public void setParent(Object component, Object parent, SessionFactoryImplementor factory) {
+		parentSetter.set( component, parent, factory );
+	}
+
+	protected Instantiator buildInstantiator(Component component) {
+		if ( component.isEmbedded() && ReflectHelper.isAbstractClass( component.getComponentClass() ) ) {
+			return new ProxiedInstantiator( component );
+		}
+		if ( optimizer == null ) {
+			return new PojoInstantiator( component, null );
+		}
+		else {
+			return new PojoInstantiator( component, optimizer.getInstantiationOptimizer() );
+		}
+	}
+
+	protected Getter buildGetter(Component component, Property prop) {
+		return prop.getGetter( component.getComponentClass() );
+	}
+
+	protected Setter buildSetter(Component component, Property prop) {
+		return prop.getSetter( component.getComponentClass() );
+	}
+
+	private static class ProxiedInstantiator implements Instantiator {
+		private final Class proxiedClass;
+		private final BasicProxyFactory factory;
+
+		public ProxiedInstantiator(Component component) {
+			proxiedClass = component.getComponentClass();
+			if ( proxiedClass.isInterface() ) {
+				factory = Environment.getBytecodeProvider()
+						.getProxyFactoryFactory()
+						.buildBasicProxyFactory( null, new Class[] { proxiedClass } );
+			}
+			else {
+				factory = Environment.getBytecodeProvider()
+						.getProxyFactoryFactory()
+						.buildBasicProxyFactory( proxiedClass, null );
+			}
+		}
+
+		public Object instantiate(Serializable id) {
+			throw new AssertionFailure( "ProxiedInstantiator can only be used to instantiate component" );
+		}
+
+		public Object instantiate() {
+			return factory.getProxy();
+		}
+
+		public boolean isInstance(Object object) {
+			return proxiedClass.isInstance( object );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index 3b2ac43439..882483f0a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,675 +1,675 @@
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
 package org.hibernate.tuple.entity;
-import java.io.Serializable;
-import java.util.Iterator;
-import java.util.Map;
-import java.util.Set;
-import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.MappingException;
-import org.hibernate.engine.EntityEntry;
-import org.hibernate.engine.EntityKey;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.event.EventSource;
-import org.hibernate.event.PersistEvent;
-import org.hibernate.id.Assigned;
-import org.hibernate.intercept.LazyPropertyInitializer;
-import org.hibernate.mapping.Component;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.property.Getter;
-import org.hibernate.property.Setter;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.tuple.Instantiator;
-import org.hibernate.tuple.StandardProperty;
-import org.hibernate.tuple.VersionProperty;
-import org.hibernate.type.ComponentType;
-import org.hibernate.type.CompositeType;
-import org.hibernate.type.EntityType;
-import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
-
-
-/**
- * Support for tuplizers relating to entities.
- *
- * @author Steve Ebersole
- * @author Gavin King
- */
-public abstract class AbstractEntityTuplizer implements EntityTuplizer {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       AbstractEntityTuplizer.class.getName());
-
-	//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter() and getSetter() are implemented currently; yuck!
-
-	private final EntityMetamodel entityMetamodel;
-
-	private final Getter idGetter;
-	private final Setter idSetter;
-
-	protected final Getter[] getters;
-	protected final Setter[] setters;
-	protected final int propertySpan;
-	protected final boolean hasCustomAccessors;
-	private final Instantiator instantiator;
-	private final ProxyFactory proxyFactory;
-	private final CompositeType identifierMapperType;
-
-	public Type getIdentifierMapperType() {
-		return identifierMapperType;
-	}
-
-	/**
-	 * Build an appropriate Getter for the given property.
-	 *
-	 * @param mappedProperty The property to be accessed via the built Getter.
-	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
-	 * @return An appropriate Getter instance.
-	 */
-	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
-
-	/**
-	 * Build an appropriate Setter for the given property.
-	 *
-	 * @param mappedProperty The property to be accessed via the built Setter.
-	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
-	 * @return An appropriate Setter instance.
-	 */
-	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
-
-	/**
-	 * Build an appropriate Instantiator for the given mapped entity.
-	 *
-	 * @param mappingInfo The mapping information regarding the mapped entity.
-	 * @return An appropriate Instantiator instance.
-	 */
-	protected abstract Instantiator buildInstantiator(PersistentClass mappingInfo);
-
-	/**
-	 * Build an appropriate ProxyFactory for the given mapped entity.
-	 *
-	 * @param mappingInfo The mapping information regarding the mapped entity.
-	 * @param idGetter The constructed Getter relating to the entity's id property.
-	 * @param idSetter The constructed Setter relating to the entity's id property.
-	 * @return An appropriate ProxyFactory instance.
-	 */
-	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
-
-	/**
-	 * Constructs a new AbstractEntityTuplizer instance.
-	 *
-	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
-	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
-	 */
-	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
-		this.entityMetamodel = entityMetamodel;
-
-		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
-			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
-			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
-		}
-		else {
-			idGetter = null;
-			idSetter = null;
-		}
-
-		propertySpan = entityMetamodel.getPropertySpan();
-
-        getters = new Getter[propertySpan];
-		setters = new Setter[propertySpan];
-
-		Iterator itr = mappingInfo.getPropertyClosureIterator();
-		boolean foundCustomAccessor=false;
-		int i=0;
-		while ( itr.hasNext() ) {
-			//TODO: redesign how PropertyAccessors are acquired...
-			Property property = (Property) itr.next();
-			getters[i] = buildPropertyGetter(property, mappingInfo);
-			setters[i] = buildPropertySetter(property, mappingInfo);
-			if ( !property.isBasicPropertyAccessor() ) {
-				foundCustomAccessor = true;
-			}
-			i++;
-		}
-		hasCustomAccessors = foundCustomAccessor;
-
-        instantiator = buildInstantiator( mappingInfo );
-
-		if ( entityMetamodel.isLazy() ) {
-			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
-			if (proxyFactory == null) {
-				entityMetamodel.setLazy( false );
-			}
-		}
-		else {
-			proxyFactory = null;
-		}
-
-		Component mapper = mappingInfo.getIdentifierMapper();
-		if ( mapper == null ) {
-			identifierMapperType = null;
-			mappedIdentifierValueMarshaller = null;
-		}
-		else {
-			identifierMapperType = (CompositeType) mapper.getType();
-			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
-					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
-					(ComponentType) identifierMapperType
-			);
-		}
-	}
-
-	/** Retreives the defined entity-name for the tuplized entity.
-	 *
-	 * @return The entity-name.
-	 */
-	protected String getEntityName() {
-		return entityMetamodel.getName();
-	}
-
-	/**
-	 * Retrieves the defined entity-names for any subclasses defined for this
-	 * entity.
-	 *
-	 * @return Any subclass entity-names.
-	 */
-	protected Set getSubclassEntityNames() {
-		return entityMetamodel.getSubclassEntityNames();
-	}
-
-	public Serializable getIdentifier(Object entity) throws HibernateException {
-		return getIdentifier( entity, null );
-	}
-
-	public Serializable getIdentifier(Object entity, SessionImplementor session) {
-		final Object id;
-		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
-			id = entity;
-		}
-		else {
-			if ( idGetter == null ) {
-				if (identifierMapperType==null) {
-					throw new HibernateException( "The class has no identifier property: " + getEntityName() );
-				}
-				else {
-					id = mappedIdentifierValueMarshaller.getIdentifier( entity, getEntityMode(), session );
-				}
-			}
-			else {
-                id = idGetter.get( entity );
-            }
-        }
-
-		try {
-			return (Serializable) id;
-		}
-		catch ( ClassCastException cce ) {
-			StringBuffer msg = new StringBuffer( "Identifier classes must be serializable. " );
-			if ( id != null ) {
-				msg.append( id.getClass().getName() ).append( " is not serializable. " );
-			}
-			if ( cce.getMessage() != null ) {
-				msg.append( cce.getMessage() );
-			}
-			throw new ClassCastException( msg.toString() );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
-		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
-		// interpretations of JPA 2 "derived identity" support
-		setIdentifier( entity, id, null );
-	}
-
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
-		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
-			if ( entity != id ) {
-				CompositeType copier = (CompositeType) entityMetamodel.getIdentifierProperty().getType();
-				copier.setPropertyValues( entity, copier.getPropertyValues( id, getEntityMode() ), getEntityMode() );
-			}
-		}
-		else if ( idSetter != null ) {
-			idSetter.set( entity, id, getFactory() );
-		}
-		else if ( identifierMapperType != null ) {
-			mappedIdentifierValueMarshaller.setIdentifier( entity, id, getEntityMode(), session );
-		}
-	}
-
-	private static interface MappedIdentifierValueMarshaller {
-		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
-	}
-
-	private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;
-
-	private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(
-			ComponentType mappedIdClassComponentType,
-			ComponentType virtualIdComponent) {
-		// so basically at this point we know we have a "mapped" composite identifier
-		// which is an awful way to say that the identifier is represented differently
-		// in the entity and in the identifier value.  The incoming value should
-		// be an instance of the mapped identifier class (@IdClass) while the incoming entity
-		// should be an instance of the entity class as defined by metamodel.
-		//
-		// However, even within that we have 2 potential scenarios:
-		//		1) @IdClass types and entity @Id property types match
-		//			- return a NormalMappedIdentifierValueMarshaller
-		//		2) They do not match
-		//			- return a IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
-		boolean wereAllEquivalent = true;
-		// the sizes being off is a much bigger problem that should have been caught already...
-		for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
-			if ( virtualIdComponent.getSubtypes()[i].isEntityType()
-					&& ! mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
-				wereAllEquivalent = false;
-				break;
-			}
-		}
-
-		return wereAllEquivalent
-				? (MappedIdentifierValueMarshaller) new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
-				: (MappedIdentifierValueMarshaller) new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType );
-	}
-
-	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
-		private final ComponentType virtualIdComponent;
-		private final ComponentType mappedIdentifierType;
-
-		private NormalMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
-			this.virtualIdComponent = virtualIdComponent;
-			this.mappedIdentifierType = mappedIdentifierType;
-		}
-
-		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
-			Object id = mappedIdentifierType.instantiate( entityMode );
-			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
-			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
-			return id;
-		}
-
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
-			virtualIdComponent.setPropertyValues(
-					entity,
-					mappedIdentifierType.getPropertyValues( id, session ),
-					entityMode
-			);
-		}
-	}
-
-	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
-		private final ComponentType virtualIdComponent;
-		private final ComponentType mappedIdentifierType;
-
-		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
-			this.virtualIdComponent = virtualIdComponent;
-			this.mappedIdentifierType = mappedIdentifierType;
-		}
-
-		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
-			final Object id = mappedIdentifierType.instantiate( entityMode );
-			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
-			final Type[] subTypes = virtualIdComponent.getSubtypes();
-			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
-			final int length = subTypes.length;
-			for ( int i = 0 ; i < length; i++ ) {
-				if ( propertyValues[i] == null ) {
-					throw new HibernateException( "No part of a composite identifier may be null" );
-				}
-				//JPA 2 @MapsId + @IdClass points to the pk of the entity
-				if ( subTypes[i].isAssociationType() && ! copierSubTypes[i].isAssociationType() ) {
-					// we need a session to handle this use case
-					if ( session == null ) {
-						throw new AssertionError(
-								"Deprecated version of getIdentifier (no session) was used but session was required"
-						);
-					}
-					final Object subId;
-					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
-						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
-					}
-					else {
-						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
-						if ( pcEntry != null ) {
-							subId = pcEntry.getId();
-						}
-						else {
-                            LOG.debugf("Performing implicit derived identity cascade");
-							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
-							for ( int x = 0; x < session.getListeners().getPersistEventListeners().length; x++ ) {
-								session.getListeners().getPersistEventListeners()[x].onPersist( event );
-
-							}
-							pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
-							if ( pcEntry == null || pcEntry.getId() == null ) {
-								throw new HibernateException( "Unable to process implicit derived identity cascade" );
-							}
-							else {
-								subId = pcEntry.getId();
-							}
-						}
-					}
-					propertyValues[i] = subId;
-				}
-			}
-			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
-			return id;
-		}
-
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
-			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
-			final Object[] injectionValues = new Object[ extractedValues.length ];
-			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
-				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
-				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
-				if ( virtualPropertyType.isEntityType() && ! idClassPropertyType.isEntityType() ) {
-					if ( session == null ) {
-						throw new AssertionError(
-								"Deprecated version of getIdentifier (no session) was used but session was required"
-						);
-					}
-					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
-					final EntityKey entityKey = new EntityKey(
-							(Serializable) extractedValues[i],
-							session.getFactory().getEntityPersister( associatedEntityName ),
-							entityMode
-					);
-					// it is conceivable there is a proxy, so check that first
-					Object association = session.getPersistenceContext().getProxy( entityKey );
-					if ( association == null ) {
-						// otherwise look for an initialized version
-						association = session.getPersistenceContext().getEntity( entityKey );
-					}
-					injectionValues[i] = association;
-				}
-				else {
-					injectionValues[i] = extractedValues[i];
-				}
-			}
-			virtualIdComponent.setPropertyValues( entity, injectionValues, session.getEntityMode() );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
-		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
-		// interpretations of JPA 2 "derived identity" support
-		resetIdentifier( entity, currentId, currentVersion, null );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public void resetIdentifier(
-			Object entity,
-			Serializable currentId,
-			Object currentVersion,
-			SessionImplementor session) {
-		if ( entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned ) {
-		}
-		else {
-			//reset the id
-			Serializable result = entityMetamodel.getIdentifierProperty()
-					.getUnsavedValue()
-					.getDefaultValue( currentId );
-			setIdentifier( entity, result, session );
-			//reset the version
-			VersionProperty versionProperty = entityMetamodel.getVersionProperty();
-			if ( entityMetamodel.isVersioned() ) {
-				setPropertyValue(
-				        entity,
-				        entityMetamodel.getVersionPropertyIndex(),
-						versionProperty.getUnsavedValue().getDefaultValue( currentVersion )
-				);
-			}
-		}
-	}
-
-	public Object getVersion(Object entity) throws HibernateException {
-		if ( !entityMetamodel.isVersioned() ) return null;
-		return getters[ entityMetamodel.getVersionPropertyIndex() ].get( entity );
-	}
-
-	protected boolean shouldGetAllProperties(Object entity) {
-		return !hasUninitializedLazyProperties( entity );
-	}
-
-	public Object[] getPropertyValues(Object entity) throws HibernateException {
-		boolean getAll = shouldGetAllProperties( entity );
-		final int span = entityMetamodel.getPropertySpan();
-		final Object[] result = new Object[span];
-
-		for ( int j = 0; j < span; j++ ) {
-			StandardProperty property = entityMetamodel.getProperties()[j];
-			if ( getAll || !property.isLazy() ) {
-				result[j] = getters[j].get( entity );
-			}
-			else {
-				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
-			}
-		}
-		return result;
-	}
-
-	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
-	throws HibernateException {
-		final int span = entityMetamodel.getPropertySpan();
-		final Object[] result = new Object[span];
-
-		for ( int j = 0; j < span; j++ ) {
-			result[j] = getters[j].getForInsert( entity, mergeMap, session );
-		}
-		return result;
-	}
-
-	public Object getPropertyValue(Object entity, int i) throws HibernateException {
-		return getters[i].get( entity );
-	}
-
-	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
-		int loc = propertyPath.indexOf('.');
-		String basePropertyName = loc > 0
-				? propertyPath.substring( 0, loc )
-				: propertyPath;
-		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
-		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
-		if (index == null) {
-			propertyPath = "_identifierMapper." + propertyPath;
-			loc = propertyPath.indexOf('.');
-			basePropertyName = loc > 0
-				? propertyPath.substring( 0, loc )
-				: propertyPath;
-		}
-		index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
-		final Object baseValue = getPropertyValue( entity, index.intValue() );
-		if ( loc > 0 ) {
-			if ( baseValue == null ) {
-				return null;
-			}
-			return getComponentValue(
-					(ComponentType) entityMetamodel.getPropertyTypes()[index.intValue()],
-					baseValue,
-					propertyPath.substring(loc+1)
-			);
-		}
-		else {
-			return baseValue;
-		}
-	}
-
-	/**
-	 * Extract a component property value.
-	 *
-	 * @param type The component property types.
-	 * @param component The component instance itself.
-	 * @param propertyPath The property path for the property to be extracted.
-	 * @return The property value extracted.
-	 */
-	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
-		final int loc = propertyPath.indexOf( '.' );
-		final String basePropertyName = loc > 0
-				? propertyPath.substring( 0, loc )
-				: propertyPath;
-		final int index = findSubPropertyIndex( type, basePropertyName );
-		final Object baseValue = type.getPropertyValue( component, index, getEntityMode() );
-		if ( loc > 0 ) {
-			if ( baseValue == null ) {
-				return null;
-			}
-			return getComponentValue(
-					(ComponentType) type.getSubtypes()[index],
-					baseValue,
-					propertyPath.substring(loc+1)
-			);
-		}
-		else {
-			return baseValue;
-		}
-
-	}
-
-	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
-		final String[] propertyNames = type.getPropertyNames();
-		for ( int index = 0; index<propertyNames.length; index++ ) {
-			if ( subPropertyName.equals( propertyNames[index] ) ) {
-				return index;
-			}
-		}
-		throw new MappingException( "component property not found: " + subPropertyName );
-	}
-
-	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
-		boolean setAll = !entityMetamodel.hasLazyProperties();
-
-		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
-			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
-				setters[j].set( entity, values[j], getFactory() );
-			}
-		}
-	}
-
-	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
-		setters[i].set( entity, value, getFactory() );
-	}
-
-	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
-		setters[ entityMetamodel.getPropertyIndex( propertyName ) ].set( entity, value, getFactory() );
-	}
-
-	public final Object instantiate(Serializable id) throws HibernateException {
-		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
-		// interpretations of JPA 2 "derived identity" support
-		return instantiate( id, null );
-	}
-
-	public final Object instantiate(Serializable id, SessionImplementor session) {
-		Object result = getInstantiator().instantiate( id );
-		if ( id != null ) {
-			setIdentifier( result, id, session );
-		}
-		return result;
-	}
-
-	public final Object instantiate() throws HibernateException {
-		return instantiate( null, null );
-	}
-
-	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
-
-	public boolean hasUninitializedLazyProperties(Object entity) {
-		// the default is to simply not lazy fetch properties for now...
-		return false;
-	}
-
-	public final boolean isInstance(Object object) {
-        return getInstantiator().isInstance( object );
-	}
-
-	public boolean hasProxy() {
-		return entityMetamodel.isLazy();
-	}
-
-	public final Object createProxy(Serializable id, SessionImplementor session)
-	throws HibernateException {
-		return getProxyFactory().getProxy( id, session );
-	}
-
-	public boolean isLifecycleImplementor() {
-		return false;
-	}
-
-	public boolean isValidatableImplementor() {
-		return false;
-	}
-
-	protected final EntityMetamodel getEntityMetamodel() {
-		return entityMetamodel;
-	}
-
-	protected final SessionFactoryImplementor getFactory() {
-		return entityMetamodel.getSessionFactory();
-	}
-
-	protected final Instantiator getInstantiator() {
-		return instantiator;
-	}
-
-	protected final ProxyFactory getProxyFactory() {
-		return proxyFactory;
-	}
-
-	@Override
-    public String toString() {
-		return getClass().getName() + '(' + getEntityMetamodel().getName() + ')';
-	}
-
-	public Getter getIdentifierGetter() {
-		return idGetter;
-	}
-
-	public Getter getVersionGetter() {
-		if ( getEntityMetamodel().isVersioned() ) {
-			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
-		}
-		return null;
-	}
-
-	public Getter getGetter(int i) {
-		return getters[i];
-	}
-}
+import java.io.Serializable;
+import java.util.Iterator;
+import java.util.Map;
+import java.util.Set;
+import org.hibernate.EntityMode;
+import org.hibernate.HibernateException;
+import org.hibernate.HibernateLogger;
+import org.hibernate.MappingException;
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
+import org.hibernate.engine.EntityEntry;
+import org.hibernate.engine.EntityKey;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.event.EventSource;
+import org.hibernate.event.PersistEvent;
+import org.hibernate.id.Assigned;
+import org.hibernate.mapping.Component;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.property.Getter;
+import org.hibernate.property.Setter;
+import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.proxy.ProxyFactory;
+import org.hibernate.tuple.Instantiator;
+import org.hibernate.tuple.StandardProperty;
+import org.hibernate.tuple.VersionProperty;
+import org.hibernate.type.ComponentType;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.EntityType;
+import org.hibernate.type.Type;
+import org.jboss.logging.Logger;
+
+
+/**
+ * Support for tuplizers relating to entities.
+ *
+ * @author Steve Ebersole
+ * @author Gavin King
+ */
+public abstract class AbstractEntityTuplizer implements EntityTuplizer {
+
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
+                                                                       AbstractEntityTuplizer.class.getName());
+
+	//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter() and getSetter() are implemented currently; yuck!
+
+	private final EntityMetamodel entityMetamodel;
+
+	private final Getter idGetter;
+	private final Setter idSetter;
+
+	protected final Getter[] getters;
+	protected final Setter[] setters;
+	protected final int propertySpan;
+	protected final boolean hasCustomAccessors;
+	private final Instantiator instantiator;
+	private final ProxyFactory proxyFactory;
+	private final CompositeType identifierMapperType;
+
+	public Type getIdentifierMapperType() {
+		return identifierMapperType;
+	}
+
+	/**
+	 * Build an appropriate Getter for the given property.
+	 *
+	 * @param mappedProperty The property to be accessed via the built Getter.
+	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
+	 * @return An appropriate Getter instance.
+	 */
+	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
+
+	/**
+	 * Build an appropriate Setter for the given property.
+	 *
+	 * @param mappedProperty The property to be accessed via the built Setter.
+	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
+	 * @return An appropriate Setter instance.
+	 */
+	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
+
+	/**
+	 * Build an appropriate Instantiator for the given mapped entity.
+	 *
+	 * @param mappingInfo The mapping information regarding the mapped entity.
+	 * @return An appropriate Instantiator instance.
+	 */
+	protected abstract Instantiator buildInstantiator(PersistentClass mappingInfo);
+
+	/**
+	 * Build an appropriate ProxyFactory for the given mapped entity.
+	 *
+	 * @param mappingInfo The mapping information regarding the mapped entity.
+	 * @param idGetter The constructed Getter relating to the entity's id property.
+	 * @param idSetter The constructed Setter relating to the entity's id property.
+	 * @return An appropriate ProxyFactory instance.
+	 */
+	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
+
+	/**
+	 * Constructs a new AbstractEntityTuplizer instance.
+	 *
+	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
+	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
+	 */
+	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
+		this.entityMetamodel = entityMetamodel;
+
+		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
+			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
+			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
+		}
+		else {
+			idGetter = null;
+			idSetter = null;
+		}
+
+		propertySpan = entityMetamodel.getPropertySpan();
+
+        getters = new Getter[propertySpan];
+		setters = new Setter[propertySpan];
+
+		Iterator itr = mappingInfo.getPropertyClosureIterator();
+		boolean foundCustomAccessor=false;
+		int i=0;
+		while ( itr.hasNext() ) {
+			//TODO: redesign how PropertyAccessors are acquired...
+			Property property = (Property) itr.next();
+			getters[i] = buildPropertyGetter(property, mappingInfo);
+			setters[i] = buildPropertySetter(property, mappingInfo);
+			if ( !property.isBasicPropertyAccessor() ) {
+				foundCustomAccessor = true;
+			}
+			i++;
+		}
+		hasCustomAccessors = foundCustomAccessor;
+
+        instantiator = buildInstantiator( mappingInfo );
+
+		if ( entityMetamodel.isLazy() ) {
+			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
+			if (proxyFactory == null) {
+				entityMetamodel.setLazy( false );
+			}
+		}
+		else {
+			proxyFactory = null;
+		}
+
+		Component mapper = mappingInfo.getIdentifierMapper();
+		if ( mapper == null ) {
+			identifierMapperType = null;
+			mappedIdentifierValueMarshaller = null;
+		}
+		else {
+			identifierMapperType = (CompositeType) mapper.getType();
+			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
+					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
+					(ComponentType) identifierMapperType
+			);
+		}
+	}
+
+	/** Retreives the defined entity-name for the tuplized entity.
+	 *
+	 * @return The entity-name.
+	 */
+	protected String getEntityName() {
+		return entityMetamodel.getName();
+	}
+
+	/**
+	 * Retrieves the defined entity-names for any subclasses defined for this
+	 * entity.
+	 *
+	 * @return Any subclass entity-names.
+	 */
+	protected Set getSubclassEntityNames() {
+		return entityMetamodel.getSubclassEntityNames();
+	}
+
+	public Serializable getIdentifier(Object entity) throws HibernateException {
+		return getIdentifier( entity, null );
+	}
+
+	public Serializable getIdentifier(Object entity, SessionImplementor session) {
+		final Object id;
+		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
+			id = entity;
+		}
+		else {
+			if ( idGetter == null ) {
+				if (identifierMapperType==null) {
+					throw new HibernateException( "The class has no identifier property: " + getEntityName() );
+				}
+				else {
+					id = mappedIdentifierValueMarshaller.getIdentifier( entity, getEntityMode(), session );
+				}
+			}
+			else {
+                id = idGetter.get( entity );
+            }
+        }
+
+		try {
+			return (Serializable) id;
+		}
+		catch ( ClassCastException cce ) {
+			StringBuffer msg = new StringBuffer( "Identifier classes must be serializable. " );
+			if ( id != null ) {
+				msg.append( id.getClass().getName() ).append( " is not serializable. " );
+			}
+			if ( cce.getMessage() != null ) {
+				msg.append( cce.getMessage() );
+			}
+			throw new ClassCastException( msg.toString() );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
+		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
+		// interpretations of JPA 2 "derived identity" support
+		setIdentifier( entity, id, null );
+	}
+
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
+		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
+			if ( entity != id ) {
+				CompositeType copier = (CompositeType) entityMetamodel.getIdentifierProperty().getType();
+				copier.setPropertyValues( entity, copier.getPropertyValues( id, getEntityMode() ), getEntityMode() );
+			}
+		}
+		else if ( idSetter != null ) {
+			idSetter.set( entity, id, getFactory() );
+		}
+		else if ( identifierMapperType != null ) {
+			mappedIdentifierValueMarshaller.setIdentifier( entity, id, getEntityMode(), session );
+		}
+	}
+
+	private static interface MappedIdentifierValueMarshaller {
+		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
+		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
+	}
+
+	private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;
+
+	private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(
+			ComponentType mappedIdClassComponentType,
+			ComponentType virtualIdComponent) {
+		// so basically at this point we know we have a "mapped" composite identifier
+		// which is an awful way to say that the identifier is represented differently
+		// in the entity and in the identifier value.  The incoming value should
+		// be an instance of the mapped identifier class (@IdClass) while the incoming entity
+		// should be an instance of the entity class as defined by metamodel.
+		//
+		// However, even within that we have 2 potential scenarios:
+		//		1) @IdClass types and entity @Id property types match
+		//			- return a NormalMappedIdentifierValueMarshaller
+		//		2) They do not match
+		//			- return a IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
+		boolean wereAllEquivalent = true;
+		// the sizes being off is a much bigger problem that should have been caught already...
+		for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
+			if ( virtualIdComponent.getSubtypes()[i].isEntityType()
+					&& ! mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
+				wereAllEquivalent = false;
+				break;
+			}
+		}
+
+		return wereAllEquivalent
+				? (MappedIdentifierValueMarshaller) new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
+				: (MappedIdentifierValueMarshaller) new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType );
+	}
+
+	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
+		private final ComponentType virtualIdComponent;
+		private final ComponentType mappedIdentifierType;
+
+		private NormalMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
+			this.virtualIdComponent = virtualIdComponent;
+			this.mappedIdentifierType = mappedIdentifierType;
+		}
+
+		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
+			Object id = mappedIdentifierType.instantiate( entityMode );
+			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
+			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
+			return id;
+		}
+
+		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
+			virtualIdComponent.setPropertyValues(
+					entity,
+					mappedIdentifierType.getPropertyValues( id, session ),
+					entityMode
+			);
+		}
+	}
+
+	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
+		private final ComponentType virtualIdComponent;
+		private final ComponentType mappedIdentifierType;
+
+		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
+			this.virtualIdComponent = virtualIdComponent;
+			this.mappedIdentifierType = mappedIdentifierType;
+		}
+
+		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
+			final Object id = mappedIdentifierType.instantiate( entityMode );
+			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
+			final Type[] subTypes = virtualIdComponent.getSubtypes();
+			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
+			final int length = subTypes.length;
+			for ( int i = 0 ; i < length; i++ ) {
+				if ( propertyValues[i] == null ) {
+					throw new HibernateException( "No part of a composite identifier may be null" );
+				}
+				//JPA 2 @MapsId + @IdClass points to the pk of the entity
+				if ( subTypes[i].isAssociationType() && ! copierSubTypes[i].isAssociationType() ) {
+					// we need a session to handle this use case
+					if ( session == null ) {
+						throw new AssertionError(
+								"Deprecated version of getIdentifier (no session) was used but session was required"
+						);
+					}
+					final Object subId;
+					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
+						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
+					}
+					else {
+						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
+						if ( pcEntry != null ) {
+							subId = pcEntry.getId();
+						}
+						else {
+                            LOG.debugf("Performing implicit derived identity cascade");
+							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
+							for ( int x = 0; x < session.getListeners().getPersistEventListeners().length; x++ ) {
+								session.getListeners().getPersistEventListeners()[x].onPersist( event );
+
+							}
+							pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
+							if ( pcEntry == null || pcEntry.getId() == null ) {
+								throw new HibernateException( "Unable to process implicit derived identity cascade" );
+							}
+							else {
+								subId = pcEntry.getId();
+							}
+						}
+					}
+					propertyValues[i] = subId;
+				}
+			}
+			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
+			return id;
+		}
+
+		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
+			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
+			final Object[] injectionValues = new Object[ extractedValues.length ];
+			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
+				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
+				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
+				if ( virtualPropertyType.isEntityType() && ! idClassPropertyType.isEntityType() ) {
+					if ( session == null ) {
+						throw new AssertionError(
+								"Deprecated version of getIdentifier (no session) was used but session was required"
+						);
+					}
+					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
+					final EntityKey entityKey = new EntityKey(
+							(Serializable) extractedValues[i],
+							session.getFactory().getEntityPersister( associatedEntityName ),
+							entityMode
+					);
+					// it is conceivable there is a proxy, so check that first
+					Object association = session.getPersistenceContext().getProxy( entityKey );
+					if ( association == null ) {
+						// otherwise look for an initialized version
+						association = session.getPersistenceContext().getEntity( entityKey );
+					}
+					injectionValues[i] = association;
+				}
+				else {
+					injectionValues[i] = extractedValues[i];
+				}
+			}
+			virtualIdComponent.setPropertyValues( entity, injectionValues, session.getEntityMode() );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
+		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
+		// interpretations of JPA 2 "derived identity" support
+		resetIdentifier( entity, currentId, currentVersion, null );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public void resetIdentifier(
+			Object entity,
+			Serializable currentId,
+			Object currentVersion,
+			SessionImplementor session) {
+		if ( entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned ) {
+		}
+		else {
+			//reset the id
+			Serializable result = entityMetamodel.getIdentifierProperty()
+					.getUnsavedValue()
+					.getDefaultValue( currentId );
+			setIdentifier( entity, result, session );
+			//reset the version
+			VersionProperty versionProperty = entityMetamodel.getVersionProperty();
+			if ( entityMetamodel.isVersioned() ) {
+				setPropertyValue(
+				        entity,
+				        entityMetamodel.getVersionPropertyIndex(),
+						versionProperty.getUnsavedValue().getDefaultValue( currentVersion )
+				);
+			}
+		}
+	}
+
+	public Object getVersion(Object entity) throws HibernateException {
+		if ( !entityMetamodel.isVersioned() ) return null;
+		return getters[ entityMetamodel.getVersionPropertyIndex() ].get( entity );
+	}
+
+	protected boolean shouldGetAllProperties(Object entity) {
+		return !hasUninitializedLazyProperties( entity );
+	}
+
+	public Object[] getPropertyValues(Object entity) throws HibernateException {
+		boolean getAll = shouldGetAllProperties( entity );
+		final int span = entityMetamodel.getPropertySpan();
+		final Object[] result = new Object[span];
+
+		for ( int j = 0; j < span; j++ ) {
+			StandardProperty property = entityMetamodel.getProperties()[j];
+			if ( getAll || !property.isLazy() ) {
+				result[j] = getters[j].get( entity );
+			}
+			else {
+				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
+			}
+		}
+		return result;
+	}
+
+	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
+	throws HibernateException {
+		final int span = entityMetamodel.getPropertySpan();
+		final Object[] result = new Object[span];
+
+		for ( int j = 0; j < span; j++ ) {
+			result[j] = getters[j].getForInsert( entity, mergeMap, session );
+		}
+		return result;
+	}
+
+	public Object getPropertyValue(Object entity, int i) throws HibernateException {
+		return getters[i].get( entity );
+	}
+
+	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
+		int loc = propertyPath.indexOf('.');
+		String basePropertyName = loc > 0
+				? propertyPath.substring( 0, loc )
+				: propertyPath;
+		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
+		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
+		if (index == null) {
+			propertyPath = "_identifierMapper." + propertyPath;
+			loc = propertyPath.indexOf('.');
+			basePropertyName = loc > 0
+				? propertyPath.substring( 0, loc )
+				: propertyPath;
+		}
+		index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
+		final Object baseValue = getPropertyValue( entity, index.intValue() );
+		if ( loc > 0 ) {
+			if ( baseValue == null ) {
+				return null;
+			}
+			return getComponentValue(
+					(ComponentType) entityMetamodel.getPropertyTypes()[index.intValue()],
+					baseValue,
+					propertyPath.substring(loc+1)
+			);
+		}
+		else {
+			return baseValue;
+		}
+	}
+
+	/**
+	 * Extract a component property value.
+	 *
+	 * @param type The component property types.
+	 * @param component The component instance itself.
+	 * @param propertyPath The property path for the property to be extracted.
+	 * @return The property value extracted.
+	 */
+	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
+		final int loc = propertyPath.indexOf( '.' );
+		final String basePropertyName = loc > 0
+				? propertyPath.substring( 0, loc )
+				: propertyPath;
+		final int index = findSubPropertyIndex( type, basePropertyName );
+		final Object baseValue = type.getPropertyValue( component, index, getEntityMode() );
+		if ( loc > 0 ) {
+			if ( baseValue == null ) {
+				return null;
+			}
+			return getComponentValue(
+					(ComponentType) type.getSubtypes()[index],
+					baseValue,
+					propertyPath.substring(loc+1)
+			);
+		}
+		else {
+			return baseValue;
+		}
+
+	}
+
+	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
+		final String[] propertyNames = type.getPropertyNames();
+		for ( int index = 0; index<propertyNames.length; index++ ) {
+			if ( subPropertyName.equals( propertyNames[index] ) ) {
+				return index;
+			}
+		}
+		throw new MappingException( "component property not found: " + subPropertyName );
+	}
+
+	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
+		boolean setAll = !entityMetamodel.hasLazyProperties();
+
+		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
+			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
+				setters[j].set( entity, values[j], getFactory() );
+			}
+		}
+	}
+
+	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
+		setters[i].set( entity, value, getFactory() );
+	}
+
+	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
+		setters[ entityMetamodel.getPropertyIndex( propertyName ) ].set( entity, value, getFactory() );
+	}
+
+	public final Object instantiate(Serializable id) throws HibernateException {
+		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
+		// interpretations of JPA 2 "derived identity" support
+		return instantiate( id, null );
+	}
+
+	public final Object instantiate(Serializable id, SessionImplementor session) {
+		Object result = getInstantiator().instantiate( id );
+		if ( id != null ) {
+			setIdentifier( result, id, session );
+		}
+		return result;
+	}
+
+	public final Object instantiate() throws HibernateException {
+		return instantiate( null, null );
+	}
+
+	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
+
+	public boolean hasUninitializedLazyProperties(Object entity) {
+		// the default is to simply not lazy fetch properties for now...
+		return false;
+	}
+
+	public final boolean isInstance(Object object) {
+        return getInstantiator().isInstance( object );
+	}
+
+	public boolean hasProxy() {
+		return entityMetamodel.isLazy();
+	}
+
+	public final Object createProxy(Serializable id, SessionImplementor session)
+	throws HibernateException {
+		return getProxyFactory().getProxy( id, session );
+	}
+
+	public boolean isLifecycleImplementor() {
+		return false;
+	}
+
+	public boolean isValidatableImplementor() {
+		return false;
+	}
+
+	protected final EntityMetamodel getEntityMetamodel() {
+		return entityMetamodel;
+	}
+
+	protected final SessionFactoryImplementor getFactory() {
+		return entityMetamodel.getSessionFactory();
+	}
+
+	protected final Instantiator getInstantiator() {
+		return instantiator;
+	}
+
+	protected final ProxyFactory getProxyFactory() {
+		return proxyFactory;
+	}
+
+	@Override
+    public String toString() {
+		return getClass().getName() + '(' + getEntityMetamodel().getName() + ')';
+	}
+
+	public Getter getIdentifierGetter() {
+		return idGetter;
+	}
+
+	public Getter getVersionGetter() {
+		if ( getEntityMetamodel().isVersioned() ) {
+			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
+		}
+		return null;
+	}
+
+	public Getter getGetter(int i) {
+		return getters[i];
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 3a2491c657..e95da92945 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,638 +1,638 @@
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
 package org.hibernate.tuple.entity;
-
-import java.io.Serializable;
-import java.util.ArrayList;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Map;
-import java.util.Set;
-import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.MappingException;
-import org.hibernate.engine.CascadeStyle;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.ValueInclusion;
-import org.hibernate.engine.Versioning;
-import org.hibernate.intercept.FieldInterceptionHelper;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.mapping.Component;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.mapping.PropertyGeneration;
-import org.hibernate.tuple.IdentifierProperty;
-import org.hibernate.tuple.PropertyFactory;
-import org.hibernate.tuple.StandardProperty;
-import org.hibernate.tuple.VersionProperty;
-import org.hibernate.type.AssociationType;
-import org.hibernate.type.CompositeType;
-import org.hibernate.type.EntityType;
-import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
-
-/**
- * Centralizes metamodel information about an entity.
- *
- * @author Steve Ebersole
- */
-public class EntityMetamodel implements Serializable {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, EntityMetamodel.class.getName());
-
-	private static final int NO_VERSION_INDX = -66;
-
-	private final SessionFactoryImplementor sessionFactory;
-
-	private final String name;
-	private final String rootName;
-	private final EntityType entityType;
-
-	private final IdentifierProperty identifierProperty;
-	private final boolean versioned;
-
-	private final int propertySpan;
-	private final int versionPropertyIndex;
-	private final StandardProperty[] properties;
-	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-	private final String[] propertyNames;
-	private final Type[] propertyTypes;
-	private final boolean[] propertyLaziness;
-	private final boolean[] propertyUpdateability;
-	private final boolean[] nonlazyPropertyUpdateability;
-	private final boolean[] propertyCheckability;
-	private final boolean[] propertyInsertability;
-	private final ValueInclusion[] insertInclusions;
-	private final ValueInclusion[] updateInclusions;
-	private final boolean[] propertyNullability;
-	private final boolean[] propertyVersionability;
-	private final CascadeStyle[] cascadeStyles;
-	private final boolean hasInsertGeneratedValues;
-	private final boolean hasUpdateGeneratedValues;
-	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-	private final Map propertyIndexes = new HashMap();
-	private final boolean hasCollections;
-	private final boolean hasMutableProperties;
-	private final boolean hasLazyProperties;
-	private final boolean hasNonIdentifierPropertyNamedId;
-
-	private final int[] naturalIdPropertyNumbers;
-	private final boolean hasImmutableNaturalId;
-
-	private boolean lazy; //not final because proxy factory creation can fail
-	private final boolean hasCascades;
-	private final boolean mutable;
-	private final boolean isAbstract;
-	private final boolean selectBeforeUpdate;
-	private final boolean dynamicUpdate;
-	private final boolean dynamicInsert;
-	private final int optimisticLockMode;
-
-	private final boolean polymorphic;
-	private final String superclass;  // superclass entity-name
-	private final boolean explicitPolymorphism;
-	private final boolean inherited;
-	private final boolean hasSubclasses;
-	private final Set subclassEntityNames = new HashSet();
-	private final Map entityNameByInheritenceClassMap = new HashMap();
-
-	private final EntityEntityModeToTuplizerMapping tuplizerMapping;
-
-	public EntityMetamodel(PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
-		this.sessionFactory = sessionFactory;
-
-		name = persistentClass.getEntityName();
-		rootName = persistentClass.getRootClass().getEntityName();
-		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
-
-		identifierProperty = PropertyFactory.buildIdentifierProperty(
-		        persistentClass,
-		        sessionFactory.getIdentifierGenerator( rootName )
-			);
-
-		versioned = persistentClass.isVersioned();
-
-		boolean lazyAvailable = persistentClass.hasPojoRepresentation() &&
-		                        FieldInterceptionHelper.isInstrumented( persistentClass.getMappedClass() );
-		boolean hasLazy = false;
-
-		propertySpan = persistentClass.getPropertyClosureSpan();
-		properties = new StandardProperty[propertySpan];
-		List naturalIdNumbers = new ArrayList();
-		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		propertyNames = new String[propertySpan];
-		propertyTypes = new Type[propertySpan];
-		propertyUpdateability = new boolean[propertySpan];
-		propertyInsertability = new boolean[propertySpan];
-		insertInclusions = new ValueInclusion[propertySpan];
-		updateInclusions = new ValueInclusion[propertySpan];
-		nonlazyPropertyUpdateability = new boolean[propertySpan];
-		propertyCheckability = new boolean[propertySpan];
-		propertyNullability = new boolean[propertySpan];
-		propertyVersionability = new boolean[propertySpan];
-		propertyLaziness = new boolean[propertySpan];
-		cascadeStyles = new CascadeStyle[propertySpan];
-		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-
-		Iterator iter = persistentClass.getPropertyClosureIterator();
-		int i = 0;
-		int tempVersionProperty = NO_VERSION_INDX;
-		boolean foundCascade = false;
-		boolean foundCollection = false;
-		boolean foundMutable = false;
-		boolean foundNonIdentifierPropertyNamedId = false;
-		boolean foundInsertGeneratedValue = false;
-		boolean foundUpdateGeneratedValue = false;
-		boolean foundUpdateableNaturalIdProperty = false;
-
-		while ( iter.hasNext() ) {
-			Property prop = ( Property ) iter.next();
-
-			if ( prop == persistentClass.getVersion() ) {
-				tempVersionProperty = i;
-				properties[i] = PropertyFactory.buildVersionProperty( prop, lazyAvailable );
-			}
-			else {
-				properties[i] = PropertyFactory.buildStandardProperty( prop, lazyAvailable );
-			}
-
-			if ( prop.isNaturalIdentifier() ) {
-				naturalIdNumbers.add( new Integer(i) );
-				if ( prop.isUpdateable() ) {
-					foundUpdateableNaturalIdProperty = true;
-				}
-			}
-
-			if ( "id".equals( prop.getName() ) ) {
-				foundNonIdentifierPropertyNamedId = true;
-			}
-
-			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-			boolean lazy = prop.isLazy() && lazyAvailable;
-			if ( lazy ) hasLazy = true;
-			propertyLaziness[i] = lazy;
-
-			propertyNames[i] = properties[i].getName();
-			propertyTypes[i] = properties[i].getType();
-			propertyNullability[i] = properties[i].isNullable();
-			propertyUpdateability[i] = properties[i].isUpdateable();
-			propertyInsertability[i] = properties[i].isInsertable();
-			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
-			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
-			propertyVersionability[i] = properties[i].isVersionable();
-			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
-			propertyCheckability[i] = propertyUpdateability[i] ||
-					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
-
-			cascadeStyles[i] = properties[i].getCascadeStyle();
-			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-			if ( properties[i].isLazy() ) {
-				hasLazy = true;
-			}
-
-			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
-				foundCascade = true;
-			}
-
-			if ( indicatesCollection( properties[i].getType() ) ) {
-				foundCollection = true;
-			}
-
-			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
-				foundMutable = true;
-			}
-
-			if ( insertInclusions[i] != ValueInclusion.NONE ) {
-				foundInsertGeneratedValue = true;
-			}
-
-			if ( updateInclusions[i] != ValueInclusion.NONE ) {
-				foundUpdateGeneratedValue = true;
-			}
-
-			mapPropertyToIndex(prop, i);
-			i++;
-		}
-
-		if (naturalIdNumbers.size()==0) {
-			naturalIdPropertyNumbers = null;
-			hasImmutableNaturalId = false;
-		}
-		else {
-			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
-			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
-		}
-
-		hasInsertGeneratedValues = foundInsertGeneratedValue;
-		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
-
-		hasCascades = foundCascade;
-		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
-		versionPropertyIndex = tempVersionProperty;
-		hasLazyProperties = hasLazy;
-        if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
-
-		lazy = persistentClass.isLazy() && (
-				// TODO: this disables laziness even in non-pojo entity modes:
-				!persistentClass.hasPojoRepresentation() ||
-				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
-		);
-		mutable = persistentClass.isMutable();
-		if ( persistentClass.isAbstract() == null ) {
-			// legacy behavior (with no abstract attribute specified)
-			isAbstract = persistentClass.hasPojoRepresentation() &&
-			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
-		}
-		else {
-			isAbstract = persistentClass.isAbstract().booleanValue();
-			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
-			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
-                LOG.entityMappedAsNonAbstract(name);
-			}
-		}
-		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
-		dynamicUpdate = persistentClass.useDynamicUpdate();
-		dynamicInsert = persistentClass.useDynamicInsert();
-
-		polymorphic = persistentClass.isPolymorphic();
-		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
-		inherited = persistentClass.isInherited();
-		superclass = inherited ?
-				persistentClass.getSuperclass().getEntityName() :
-				null;
-		hasSubclasses = persistentClass.hasSubclasses();
-
-		optimisticLockMode = persistentClass.getOptimisticLockMode();
-		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
-			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
-		}
-		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
-			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
-		}
-
-		hasCollections = foundCollection;
-		hasMutableProperties = foundMutable;
-
-		iter = persistentClass.getSubclassIterator();
-		while ( iter.hasNext() ) {
-			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
-		}
-		subclassEntityNames.add( name );
-
-		if ( persistentClass.hasPojoRepresentation() ) {
-			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
-			iter = persistentClass.getSubclassIterator();
-			while ( iter.hasNext() ) {
-				final PersistentClass pc = ( PersistentClass ) iter.next();
-				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
-			}
-		}
-
-		tuplizerMapping = new EntityEntityModeToTuplizerMapping( persistentClass, this );
-	}
-
-	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
-		if ( runtimeProperty.isInsertGenerated() ) {
-			return ValueInclusion.FULL;
-		}
-		else if ( mappingProperty.getValue() instanceof Component ) {
-			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
-				return ValueInclusion.PARTIAL;
-			}
-		}
-		return ValueInclusion.NONE;
-	}
-
-	private boolean hasPartialInsertComponentGeneration(Component component) {
-		Iterator subProperties = component.getPropertyIterator();
-		while ( subProperties.hasNext() ) {
-			Property prop = ( Property ) subProperties.next();
-			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
-				return true;
-			}
-			else if ( prop.getValue() instanceof Component ) {
-				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
-					return true;
-				}
-			}
-		}
-		return false;
-	}
-
-	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
-		if ( runtimeProperty.isUpdateGenerated() ) {
-			return ValueInclusion.FULL;
-		}
-		else if ( mappingProperty.getValue() instanceof Component ) {
-			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
-				return ValueInclusion.PARTIAL;
-			}
-		}
-		return ValueInclusion.NONE;
-	}
-
-	private boolean hasPartialUpdateComponentGeneration(Component component) {
-		Iterator subProperties = component.getPropertyIterator();
-		while ( subProperties.hasNext() ) {
-			Property prop = ( Property ) subProperties.next();
-			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
-				return true;
-			}
-			else if ( prop.getValue() instanceof Component ) {
-				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
-					return true;
-				}
-			}
-		}
-		return false;
-	}
-
-	private void mapPropertyToIndex(Property prop, int i) {
-		propertyIndexes.put( prop.getName(), new Integer(i) );
-		if ( prop.getValue() instanceof Component ) {
-			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
-			while ( iter.hasNext() ) {
-				Property subprop = (Property) iter.next();
-				propertyIndexes.put(
-						prop.getName() + '.' + subprop.getName(),
-						new Integer(i)
-					);
-			}
-		}
-	}
-
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
-	}
-
-	public int[] getNaturalIdentifierProperties() {
-		return naturalIdPropertyNumbers;
-	}
-
-	public boolean hasNaturalIdentifier() {
-		return naturalIdPropertyNumbers!=null;
-	}
-
-	public boolean hasImmutableNaturalId() {
-		return hasImmutableNaturalId;
-	}
-
-	public Set getSubclassEntityNames() {
-		return subclassEntityNames;
-	}
-
-	private boolean indicatesCollection(Type type) {
-		if ( type.isCollectionType() ) {
-			return true;
-		}
-		else if ( type.isComponentType() ) {
-			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
-			for ( int i = 0; i < subtypes.length; i++ ) {
-				if ( indicatesCollection( subtypes[i] ) ) {
-					return true;
-				}
-			}
-		}
-		return false;
-	}
-
-	public SessionFactoryImplementor getSessionFactory() {
-		return sessionFactory;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public String getRootName() {
-		return rootName;
-	}
-
-	public EntityType getEntityType() {
-		return entityType;
-	}
-
-	public IdentifierProperty getIdentifierProperty() {
-		return identifierProperty;
-	}
-
-	public int getPropertySpan() {
-		return propertySpan;
-	}
-
-	public int getVersionPropertyIndex() {
-		return versionPropertyIndex;
-	}
-
-	public VersionProperty getVersionProperty() {
-		if ( NO_VERSION_INDX == versionPropertyIndex ) {
-			return null;
-		}
-		else {
-			return ( VersionProperty ) properties[ versionPropertyIndex ];
-		}
-	}
-
-	public StandardProperty[] getProperties() {
-		return properties;
-	}
-
-	public int getPropertyIndex(String propertyName) {
-		Integer index = getPropertyIndexOrNull(propertyName);
-		if ( index == null ) {
-			throw new HibernateException("Unable to resolve property: " + propertyName);
-		}
-		return index.intValue();
-	}
-
-	public Integer getPropertyIndexOrNull(String propertyName) {
-		return (Integer) propertyIndexes.get( propertyName );
-	}
-
-	public boolean hasCollections() {
-		return hasCollections;
-	}
-
-	public boolean hasMutableProperties() {
-		return hasMutableProperties;
-	}
-
-	public boolean hasNonIdentifierPropertyNamedId() {
-		return hasNonIdentifierPropertyNamedId;
-	}
-
-	public boolean hasLazyProperties() {
-		return hasLazyProperties;
-	}
-
-	public boolean hasCascades() {
-		return hasCascades;
-	}
-
-	public boolean isMutable() {
-		return mutable;
-	}
-
-	public boolean isSelectBeforeUpdate() {
-		return selectBeforeUpdate;
-	}
-
-	public boolean isDynamicUpdate() {
-		return dynamicUpdate;
-	}
-
-	public boolean isDynamicInsert() {
-		return dynamicInsert;
-	}
-
-	public int getOptimisticLockMode() {
-		return optimisticLockMode;
-	}
-
-	public boolean isPolymorphic() {
-		return polymorphic;
-	}
-
-	public String getSuperclass() {
-		return superclass;
-	}
-
-	public boolean isExplicitPolymorphism() {
-		return explicitPolymorphism;
-	}
-
-	public boolean isInherited() {
-		return inherited;
-	}
-
-	public boolean hasSubclasses() {
-		return hasSubclasses;
-	}
-
-	public boolean isLazy() {
-		return lazy;
-	}
-
-	public void setLazy(boolean lazy) {
-		this.lazy = lazy;
-	}
-
-	public boolean isVersioned() {
-		return versioned;
-	}
-
-	public boolean isAbstract() {
-		return isAbstract;
-	}
-
-	/**
-	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
-	 *
-	 * @param inheritenceClass The class for which to resolve the entity-name.
-	 * @return The mapped entity-name, or null if no such mapping was found.
-	 */
-	public String findEntityNameByEntityClass(Class inheritenceClass) {
-		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
-	}
-
-	@Override
-    public String toString() {
-		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
-	}
-
-	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-	public String[] getPropertyNames() {
-		return propertyNames;
-	}
-
-	public Type[] getPropertyTypes() {
-		return propertyTypes;
-	}
-
-	public boolean[] getPropertyLaziness() {
-		return propertyLaziness;
-	}
-
-	public boolean[] getPropertyUpdateability() {
-		return propertyUpdateability;
-	}
-
-	public boolean[] getPropertyCheckability() {
-		return propertyCheckability;
-	}
-
-	public boolean[] getNonlazyPropertyUpdateability() {
-		return nonlazyPropertyUpdateability;
-	}
-
-	public boolean[] getPropertyInsertability() {
-		return propertyInsertability;
-	}
-
-	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
-		return insertInclusions;
-	}
-
-	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
-		return updateInclusions;
-	}
-
-	public boolean[] getPropertyNullability() {
-		return propertyNullability;
-	}
-
-	public boolean[] getPropertyVersionability() {
-		return propertyVersionability;
-	}
-
-	public CascadeStyle[] getCascadeStyles() {
-		return cascadeStyles;
-	}
-
-	public boolean hasInsertGeneratedValues() {
-		return hasInsertGeneratedValues;
-	}
-
-	public boolean hasUpdateGeneratedValues() {
-		return hasUpdateGeneratedValues;
-	}
-}
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+import org.hibernate.EntityMode;
+import org.hibernate.HibernateException;
+import org.hibernate.HibernateLogger;
+import org.hibernate.MappingException;
+import org.hibernate.engine.CascadeStyle;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.ValueInclusion;
+import org.hibernate.engine.Versioning;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.mapping.Component;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.tuple.IdentifierProperty;
+import org.hibernate.tuple.PropertyFactory;
+import org.hibernate.tuple.StandardProperty;
+import org.hibernate.tuple.VersionProperty;
+import org.hibernate.type.AssociationType;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.EntityType;
+import org.hibernate.type.Type;
+import org.jboss.logging.Logger;
+
+/**
+ * Centralizes metamodel information about an entity.
+ *
+ * @author Steve Ebersole
+ */
+public class EntityMetamodel implements Serializable {
+
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, EntityMetamodel.class.getName());
+
+	private static final int NO_VERSION_INDX = -66;
+
+	private final SessionFactoryImplementor sessionFactory;
+
+	private final String name;
+	private final String rootName;
+	private final EntityType entityType;
+
+	private final IdentifierProperty identifierProperty;
+	private final boolean versioned;
+
+	private final int propertySpan;
+	private final int versionPropertyIndex;
+	private final StandardProperty[] properties;
+	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+	private final String[] propertyNames;
+	private final Type[] propertyTypes;
+	private final boolean[] propertyLaziness;
+	private final boolean[] propertyUpdateability;
+	private final boolean[] nonlazyPropertyUpdateability;
+	private final boolean[] propertyCheckability;
+	private final boolean[] propertyInsertability;
+	private final ValueInclusion[] insertInclusions;
+	private final ValueInclusion[] updateInclusions;
+	private final boolean[] propertyNullability;
+	private final boolean[] propertyVersionability;
+	private final CascadeStyle[] cascadeStyles;
+	private final boolean hasInsertGeneratedValues;
+	private final boolean hasUpdateGeneratedValues;
+	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+	private final Map propertyIndexes = new HashMap();
+	private final boolean hasCollections;
+	private final boolean hasMutableProperties;
+	private final boolean hasLazyProperties;
+	private final boolean hasNonIdentifierPropertyNamedId;
+
+	private final int[] naturalIdPropertyNumbers;
+	private final boolean hasImmutableNaturalId;
+
+	private boolean lazy; //not final because proxy factory creation can fail
+	private final boolean hasCascades;
+	private final boolean mutable;
+	private final boolean isAbstract;
+	private final boolean selectBeforeUpdate;
+	private final boolean dynamicUpdate;
+	private final boolean dynamicInsert;
+	private final int optimisticLockMode;
+
+	private final boolean polymorphic;
+	private final String superclass;  // superclass entity-name
+	private final boolean explicitPolymorphism;
+	private final boolean inherited;
+	private final boolean hasSubclasses;
+	private final Set subclassEntityNames = new HashSet();
+	private final Map entityNameByInheritenceClassMap = new HashMap();
+
+	private final EntityEntityModeToTuplizerMapping tuplizerMapping;
+
+	public EntityMetamodel(PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
+		this.sessionFactory = sessionFactory;
+
+		name = persistentClass.getEntityName();
+		rootName = persistentClass.getRootClass().getEntityName();
+		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
+
+		identifierProperty = PropertyFactory.buildIdentifierProperty(
+		        persistentClass,
+		        sessionFactory.getIdentifierGenerator( rootName )
+			);
+
+		versioned = persistentClass.isVersioned();
+
+		boolean lazyAvailable = persistentClass.hasPojoRepresentation() &&
+		                        FieldInterceptionHelper.isInstrumented( persistentClass.getMappedClass() );
+		boolean hasLazy = false;
+
+		propertySpan = persistentClass.getPropertyClosureSpan();
+		properties = new StandardProperty[propertySpan];
+		List naturalIdNumbers = new ArrayList();
+		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		propertyNames = new String[propertySpan];
+		propertyTypes = new Type[propertySpan];
+		propertyUpdateability = new boolean[propertySpan];
+		propertyInsertability = new boolean[propertySpan];
+		insertInclusions = new ValueInclusion[propertySpan];
+		updateInclusions = new ValueInclusion[propertySpan];
+		nonlazyPropertyUpdateability = new boolean[propertySpan];
+		propertyCheckability = new boolean[propertySpan];
+		propertyNullability = new boolean[propertySpan];
+		propertyVersionability = new boolean[propertySpan];
+		propertyLaziness = new boolean[propertySpan];
+		cascadeStyles = new CascadeStyle[propertySpan];
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+
+		Iterator iter = persistentClass.getPropertyClosureIterator();
+		int i = 0;
+		int tempVersionProperty = NO_VERSION_INDX;
+		boolean foundCascade = false;
+		boolean foundCollection = false;
+		boolean foundMutable = false;
+		boolean foundNonIdentifierPropertyNamedId = false;
+		boolean foundInsertGeneratedValue = false;
+		boolean foundUpdateGeneratedValue = false;
+		boolean foundUpdateableNaturalIdProperty = false;
+
+		while ( iter.hasNext() ) {
+			Property prop = ( Property ) iter.next();
+
+			if ( prop == persistentClass.getVersion() ) {
+				tempVersionProperty = i;
+				properties[i] = PropertyFactory.buildVersionProperty( prop, lazyAvailable );
+			}
+			else {
+				properties[i] = PropertyFactory.buildStandardProperty( prop, lazyAvailable );
+			}
+
+			if ( prop.isNaturalIdentifier() ) {
+				naturalIdNumbers.add( new Integer(i) );
+				if ( prop.isUpdateable() ) {
+					foundUpdateableNaturalIdProperty = true;
+				}
+			}
+
+			if ( "id".equals( prop.getName() ) ) {
+				foundNonIdentifierPropertyNamedId = true;
+			}
+
+			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+			boolean lazy = prop.isLazy() && lazyAvailable;
+			if ( lazy ) hasLazy = true;
+			propertyLaziness[i] = lazy;
+
+			propertyNames[i] = properties[i].getName();
+			propertyTypes[i] = properties[i].getType();
+			propertyNullability[i] = properties[i].isNullable();
+			propertyUpdateability[i] = properties[i].isUpdateable();
+			propertyInsertability[i] = properties[i].isInsertable();
+			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
+			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
+			propertyVersionability[i] = properties[i].isVersionable();
+			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
+			propertyCheckability[i] = propertyUpdateability[i] ||
+					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
+
+			cascadeStyles[i] = properties[i].getCascadeStyle();
+			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+			if ( properties[i].isLazy() ) {
+				hasLazy = true;
+			}
+
+			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
+				foundCascade = true;
+			}
+
+			if ( indicatesCollection( properties[i].getType() ) ) {
+				foundCollection = true;
+			}
+
+			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
+				foundMutable = true;
+			}
+
+			if ( insertInclusions[i] != ValueInclusion.NONE ) {
+				foundInsertGeneratedValue = true;
+			}
+
+			if ( updateInclusions[i] != ValueInclusion.NONE ) {
+				foundUpdateGeneratedValue = true;
+			}
+
+			mapPropertyToIndex(prop, i);
+			i++;
+		}
+
+		if (naturalIdNumbers.size()==0) {
+			naturalIdPropertyNumbers = null;
+			hasImmutableNaturalId = false;
+		}
+		else {
+			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
+			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
+		}
+
+		hasInsertGeneratedValues = foundInsertGeneratedValue;
+		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
+
+		hasCascades = foundCascade;
+		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
+		versionPropertyIndex = tempVersionProperty;
+		hasLazyProperties = hasLazy;
+        if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
+
+		lazy = persistentClass.isLazy() && (
+				// TODO: this disables laziness even in non-pojo entity modes:
+				!persistentClass.hasPojoRepresentation() ||
+				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
+		);
+		mutable = persistentClass.isMutable();
+		if ( persistentClass.isAbstract() == null ) {
+			// legacy behavior (with no abstract attribute specified)
+			isAbstract = persistentClass.hasPojoRepresentation() &&
+			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
+		}
+		else {
+			isAbstract = persistentClass.isAbstract().booleanValue();
+			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
+			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
+                LOG.entityMappedAsNonAbstract(name);
+			}
+		}
+		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
+		dynamicUpdate = persistentClass.useDynamicUpdate();
+		dynamicInsert = persistentClass.useDynamicInsert();
+
+		polymorphic = persistentClass.isPolymorphic();
+		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
+		inherited = persistentClass.isInherited();
+		superclass = inherited ?
+				persistentClass.getSuperclass().getEntityName() :
+				null;
+		hasSubclasses = persistentClass.hasSubclasses();
+
+		optimisticLockMode = persistentClass.getOptimisticLockMode();
+		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
+			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
+		}
+		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
+			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
+		}
+
+		hasCollections = foundCollection;
+		hasMutableProperties = foundMutable;
+
+		iter = persistentClass.getSubclassIterator();
+		while ( iter.hasNext() ) {
+			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
+		}
+		subclassEntityNames.add( name );
+
+		if ( persistentClass.hasPojoRepresentation() ) {
+			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
+			iter = persistentClass.getSubclassIterator();
+			while ( iter.hasNext() ) {
+				final PersistentClass pc = ( PersistentClass ) iter.next();
+				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
+			}
+		}
+
+		tuplizerMapping = new EntityEntityModeToTuplizerMapping( persistentClass, this );
+	}
+
+	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
+		if ( runtimeProperty.isInsertGenerated() ) {
+			return ValueInclusion.FULL;
+		}
+		else if ( mappingProperty.getValue() instanceof Component ) {
+			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
+				return ValueInclusion.PARTIAL;
+			}
+		}
+		return ValueInclusion.NONE;
+	}
+
+	private boolean hasPartialInsertComponentGeneration(Component component) {
+		Iterator subProperties = component.getPropertyIterator();
+		while ( subProperties.hasNext() ) {
+			Property prop = ( Property ) subProperties.next();
+			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
+				return true;
+			}
+			else if ( prop.getValue() instanceof Component ) {
+				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
+					return true;
+				}
+			}
+		}
+		return false;
+	}
+
+	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
+		if ( runtimeProperty.isUpdateGenerated() ) {
+			return ValueInclusion.FULL;
+		}
+		else if ( mappingProperty.getValue() instanceof Component ) {
+			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
+				return ValueInclusion.PARTIAL;
+			}
+		}
+		return ValueInclusion.NONE;
+	}
+
+	private boolean hasPartialUpdateComponentGeneration(Component component) {
+		Iterator subProperties = component.getPropertyIterator();
+		while ( subProperties.hasNext() ) {
+			Property prop = ( Property ) subProperties.next();
+			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
+				return true;
+			}
+			else if ( prop.getValue() instanceof Component ) {
+				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
+					return true;
+				}
+			}
+		}
+		return false;
+	}
+
+	private void mapPropertyToIndex(Property prop, int i) {
+		propertyIndexes.put( prop.getName(), new Integer(i) );
+		if ( prop.getValue() instanceof Component ) {
+			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
+			while ( iter.hasNext() ) {
+				Property subprop = (Property) iter.next();
+				propertyIndexes.put(
+						prop.getName() + '.' + subprop.getName(),
+						new Integer(i)
+					);
+			}
+		}
+	}
+
+	public EntityEntityModeToTuplizerMapping getTuplizerMapping() {
+		return tuplizerMapping;
+	}
+
+	public EntityTuplizer getTuplizer(EntityMode entityMode) {
+		return (EntityTuplizer) tuplizerMapping.getTuplizer( entityMode );
+	}
+
+	public EntityTuplizer getTuplizerOrNull(EntityMode entityMode) {
+		return ( EntityTuplizer ) tuplizerMapping.getTuplizerOrNull( entityMode );
+	}
+
+	public EntityMode guessEntityMode(Object object) {
+		return tuplizerMapping.guessEntityMode( object );
+	}
+
+	public int[] getNaturalIdentifierProperties() {
+		return naturalIdPropertyNumbers;
+	}
+
+	public boolean hasNaturalIdentifier() {
+		return naturalIdPropertyNumbers!=null;
+	}
+
+	public boolean hasImmutableNaturalId() {
+		return hasImmutableNaturalId;
+	}
+
+	public Set getSubclassEntityNames() {
+		return subclassEntityNames;
+	}
+
+	private boolean indicatesCollection(Type type) {
+		if ( type.isCollectionType() ) {
+			return true;
+		}
+		else if ( type.isComponentType() ) {
+			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
+			for ( int i = 0; i < subtypes.length; i++ ) {
+				if ( indicatesCollection( subtypes[i] ) ) {
+					return true;
+				}
+			}
+		}
+		return false;
+	}
+
+	public SessionFactoryImplementor getSessionFactory() {
+		return sessionFactory;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public String getRootName() {
+		return rootName;
+	}
+
+	public EntityType getEntityType() {
+		return entityType;
+	}
+
+	public IdentifierProperty getIdentifierProperty() {
+		return identifierProperty;
+	}
+
+	public int getPropertySpan() {
+		return propertySpan;
+	}
+
+	public int getVersionPropertyIndex() {
+		return versionPropertyIndex;
+	}
+
+	public VersionProperty getVersionProperty() {
+		if ( NO_VERSION_INDX == versionPropertyIndex ) {
+			return null;
+		}
+		else {
+			return ( VersionProperty ) properties[ versionPropertyIndex ];
+		}
+	}
+
+	public StandardProperty[] getProperties() {
+		return properties;
+	}
+
+	public int getPropertyIndex(String propertyName) {
+		Integer index = getPropertyIndexOrNull(propertyName);
+		if ( index == null ) {
+			throw new HibernateException("Unable to resolve property: " + propertyName);
+		}
+		return index.intValue();
+	}
+
+	public Integer getPropertyIndexOrNull(String propertyName) {
+		return (Integer) propertyIndexes.get( propertyName );
+	}
+
+	public boolean hasCollections() {
+		return hasCollections;
+	}
+
+	public boolean hasMutableProperties() {
+		return hasMutableProperties;
+	}
+
+	public boolean hasNonIdentifierPropertyNamedId() {
+		return hasNonIdentifierPropertyNamedId;
+	}
+
+	public boolean hasLazyProperties() {
+		return hasLazyProperties;
+	}
+
+	public boolean hasCascades() {
+		return hasCascades;
+	}
+
+	public boolean isMutable() {
+		return mutable;
+	}
+
+	public boolean isSelectBeforeUpdate() {
+		return selectBeforeUpdate;
+	}
+
+	public boolean isDynamicUpdate() {
+		return dynamicUpdate;
+	}
+
+	public boolean isDynamicInsert() {
+		return dynamicInsert;
+	}
+
+	public int getOptimisticLockMode() {
+		return optimisticLockMode;
+	}
+
+	public boolean isPolymorphic() {
+		return polymorphic;
+	}
+
+	public String getSuperclass() {
+		return superclass;
+	}
+
+	public boolean isExplicitPolymorphism() {
+		return explicitPolymorphism;
+	}
+
+	public boolean isInherited() {
+		return inherited;
+	}
+
+	public boolean hasSubclasses() {
+		return hasSubclasses;
+	}
+
+	public boolean isLazy() {
+		return lazy;
+	}
+
+	public void setLazy(boolean lazy) {
+		this.lazy = lazy;
+	}
+
+	public boolean isVersioned() {
+		return versioned;
+	}
+
+	public boolean isAbstract() {
+		return isAbstract;
+	}
+
+	/**
+	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
+	 *
+	 * @param inheritenceClass The class for which to resolve the entity-name.
+	 * @return The mapped entity-name, or null if no such mapping was found.
+	 */
+	public String findEntityNameByEntityClass(Class inheritenceClass) {
+		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
+	}
+
+	@Override
+    public String toString() {
+		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
+	}
+
+	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+	public String[] getPropertyNames() {
+		return propertyNames;
+	}
+
+	public Type[] getPropertyTypes() {
+		return propertyTypes;
+	}
+
+	public boolean[] getPropertyLaziness() {
+		return propertyLaziness;
+	}
+
+	public boolean[] getPropertyUpdateability() {
+		return propertyUpdateability;
+	}
+
+	public boolean[] getPropertyCheckability() {
+		return propertyCheckability;
+	}
+
+	public boolean[] getNonlazyPropertyUpdateability() {
+		return nonlazyPropertyUpdateability;
+	}
+
+	public boolean[] getPropertyInsertability() {
+		return propertyInsertability;
+	}
+
+	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
+		return insertInclusions;
+	}
+
+	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
+		return updateInclusions;
+	}
+
+	public boolean[] getPropertyNullability() {
+		return propertyNullability;
+	}
+
+	public boolean[] getPropertyVersionability() {
+		return propertyVersionability;
+	}
+
+	public CascadeStyle[] getCascadeStyles() {
+		return cascadeStyles;
+	}
+
+	public boolean hasInsertGeneratedValues() {
+		return hasInsertGeneratedValues;
+	}
+
+	public boolean hasUpdateGeneratedValues() {
+		return hasUpdateGeneratedValues;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index 44a6a7ea2a..1114a585b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,379 +1,379 @@
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
 package org.hibernate.tuple.entity;
-
-import java.lang.reflect.Method;
-import java.lang.reflect.Modifier;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.Map;
-import java.util.Set;
-import org.hibernate.EntityMode;
-import org.hibernate.EntityNameResolver;
-import org.hibernate.HibernateException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.MappingException;
-import org.hibernate.bytecode.ReflectionOptimizer;
-import org.hibernate.cfg.Environment;
-import org.hibernate.classic.Lifecycle;
-import org.hibernate.classic.Validatable;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.FieldInterceptionHelper;
-import org.hibernate.intercept.FieldInterceptor;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Property;
-import org.hibernate.mapping.Subclass;
-import org.hibernate.property.Getter;
-import org.hibernate.property.Setter;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.tuple.Instantiator;
-import org.hibernate.tuple.PojoInstantiator;
-import org.hibernate.type.CompositeType;
-import org.jboss.logging.Logger;
-
-/**
- * An {@link EntityTuplizer} specific to the pojo entity mode.
- *
- * @author Steve Ebersole
- * @author Gavin King
- */
-public class PojoEntityTuplizer extends AbstractEntityTuplizer {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, PojoEntityTuplizer.class.getName());
-
-	private final Class mappedClass;
-	private final Class proxyInterface;
-	private final boolean lifecycleImplementor;
-	private final boolean validatableImplementor;
-	private final Set lazyPropertyNames = new HashSet();
-	private final ReflectionOptimizer optimizer;
-
-	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
-		super( entityMetamodel, mappedEntity );
-		this.mappedClass = mappedEntity.getMappedClass();
-		this.proxyInterface = mappedEntity.getProxyInterface();
-		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
-		this.validatableImplementor = Validatable.class.isAssignableFrom( mappedClass );
-
-		Iterator iter = mappedEntity.getPropertyClosureIterator();
-		while ( iter.hasNext() ) {
-			Property property = (Property) iter.next();
-			if ( property.isLazy() ) {
-				lazyPropertyNames.add( property.getName() );
-			}
-		}
-
-		String[] getterNames = new String[propertySpan];
-		String[] setterNames = new String[propertySpan];
-		Class[] propTypes = new Class[propertySpan];
-		for ( int i = 0; i < propertySpan; i++ ) {
-			getterNames[i] = getters[i].getMethodName();
-			setterNames[i] = setters[i].getMethodName();
-			propTypes[i] = getters[i].getReturnType();
-		}
-
-		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
-			optimizer = null;
-		}
-		else {
-			// todo : YUCK!!!
-			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
-//			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
-//					mappedClass, getterNames, setterNames, propTypes
-//			);
-		}
-
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
-		// determine the id getter and setter methods from the proxy interface (if any)
-        // determine all interfaces needed by the resulting proxy
-		HashSet<Class> proxyInterfaces = new HashSet<Class>();
-		proxyInterfaces.add( HibernateProxy.class );
-
-		Class mappedClass = persistentClass.getMappedClass();
-		Class proxyInterface = persistentClass.getProxyInterface();
-
-		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
-			if ( !proxyInterface.isInterface() ) {
-				throw new MappingException(
-						"proxy must be either an interface, or the class itself: " + getEntityName()
-				);
-			}
-			proxyInterfaces.add( proxyInterface );
-		}
-
-		if ( mappedClass.isInterface() ) {
-			proxyInterfaces.add( mappedClass );
-		}
-
-		Iterator subclasses = persistentClass.getSubclassIterator();
-		while ( subclasses.hasNext() ) {
-			final Subclass subclass = ( Subclass ) subclasses.next();
-			final Class subclassProxy = subclass.getProxyInterface();
-			final Class subclassClass = subclass.getMappedClass();
-			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
-				if ( !subclassProxy.isInterface() ) {
-					throw new MappingException(
-							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
-					);
-				}
-				proxyInterfaces.add( subclassProxy );
-			}
-		}
-
-		Iterator properties = persistentClass.getPropertyIterator();
-		Class clazz = persistentClass.getMappedClass();
-		while ( properties.hasNext() ) {
-			Property property = (Property) properties.next();
-			Method method = property.getGetter(clazz).getMethod();
-			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
-                LOG.gettersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
-			}
-			method = property.getSetter(clazz).getMethod();
-            if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
-                LOG.settersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
-			}
-		}
-
-		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
-		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
-
-		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
-				null :
-		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
-		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
-				null :
-		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
-
-		ProxyFactory pf = buildProxyFactoryInternal( persistentClass, idGetter, idSetter );
-		try {
-			pf.postInstantiate(
-					getEntityName(),
-					mappedClass,
-					proxyInterfaces,
-					proxyGetIdentifierMethod,
-					proxySetIdentifierMethod,
-					persistentClass.hasEmbeddedIdentifier() ?
-			                (CompositeType) persistentClass.getIdentifier().getType() :
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
-	protected ProxyFactory buildProxyFactoryInternal(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
-		// TODO : YUCK!!!  fix after HHH-1907 is complete
-		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
-//		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Instantiator buildInstantiator(PersistentClass persistentClass) {
-		if ( optimizer == null ) {
-			return new PojoInstantiator( persistentClass, null );
-		}
-		else {
-			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
-		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
-			setPropertyValuesWithOptimizer( entity, values );
-		}
-		else {
-			super.setPropertyValues( entity, values );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public Object[] getPropertyValues(Object entity) throws HibernateException {
-		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
-			return getPropertyValuesWithOptimizer( entity );
-		}
-		else {
-			return super.getPropertyValues( entity );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
-		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
-			return getPropertyValuesWithOptimizer( entity );
-		}
-		else {
-			return super.getPropertyValuesToInsert( entity, mergeMap, session );
-		}
-	}
-
-	protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
-		optimizer.getAccessOptimizer().setPropertyValues( object, values );
-	}
-
-	protected Object[] getPropertyValuesWithOptimizer(Object object) {
-		return optimizer.getAccessOptimizer().getPropertyValues( object );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityMode getEntityMode() {
-		return EntityMode.POJO;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getMappedClass() {
-		return mappedClass;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public boolean isLifecycleImplementor() {
-		return lifecycleImplementor;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public boolean isValidatableImplementor() {
-		return validatableImplementor;
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
-		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public Class getConcreteProxyClass() {
-		return proxyInterface;
-	}
-
-    //TODO: need to make the majority of this functionality into a top-level support class for custom impl support
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
-		if ( isInstrumented() ) {
-			Set lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
-					lazyPropertyNames : null;
-			//TODO: if we support multiple fetch groups, we would need
-			//      to clone the set of lazy properties!
-			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-    public boolean hasUninitializedLazyProperties(Object entity) {
-		if ( getEntityMetamodel().hasLazyProperties() ) {
-			FieldInterceptor callback = FieldInterceptionHelper.extractFieldInterceptor( entity );
-			return callback != null && !callback.isInitialized();
-		}
-		else {
-			return false;
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public boolean isInstrumented() {
-		return FieldInterceptionHelper.isInstrumented( getMappedClass() );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
-		final Class concreteEntityClass = entityInstance.getClass();
-		if ( concreteEntityClass == getMappedClass() ) {
-			return getEntityName();
-		}
-		else {
-			String entityName = getEntityMetamodel().findEntityNameByEntityClass( concreteEntityClass );
-			if ( entityName == null ) {
-				throw new HibernateException(
-						"Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
-								+ " expected instance/subclass of [" + getEntityName() + "]"
-				);
-			}
-			return entityName;
-		}
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
-	public EntityNameResolver[] getEntityNameResolvers() {
-		return null;
-	}
-}
+
+import java.lang.reflect.Method;
+import java.lang.reflect.Modifier;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.Map;
+import java.util.Set;
+import org.hibernate.EntityMode;
+import org.hibernate.EntityNameResolver;
+import org.hibernate.HibernateException;
+import org.hibernate.HibernateLogger;
+import org.hibernate.MappingException;
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
+import org.hibernate.cfg.Environment;
+import org.hibernate.classic.Lifecycle;
+import org.hibernate.classic.Validatable;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.mapping.Subclass;
+import org.hibernate.property.Getter;
+import org.hibernate.property.Setter;
+import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.proxy.ProxyFactory;
+import org.hibernate.tuple.Instantiator;
+import org.hibernate.tuple.PojoInstantiator;
+import org.hibernate.type.CompositeType;
+import org.jboss.logging.Logger;
+
+/**
+ * An {@link EntityTuplizer} specific to the pojo entity mode.
+ *
+ * @author Steve Ebersole
+ * @author Gavin King
+ */
+public class PojoEntityTuplizer extends AbstractEntityTuplizer {
+
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, PojoEntityTuplizer.class.getName());
+
+	private final Class mappedClass;
+	private final Class proxyInterface;
+	private final boolean lifecycleImplementor;
+	private final boolean validatableImplementor;
+	private final Set lazyPropertyNames = new HashSet();
+	private final ReflectionOptimizer optimizer;
+
+	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
+		super( entityMetamodel, mappedEntity );
+		this.mappedClass = mappedEntity.getMappedClass();
+		this.proxyInterface = mappedEntity.getProxyInterface();
+		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
+		this.validatableImplementor = Validatable.class.isAssignableFrom( mappedClass );
+
+		Iterator iter = mappedEntity.getPropertyClosureIterator();
+		while ( iter.hasNext() ) {
+			Property property = (Property) iter.next();
+			if ( property.isLazy() ) {
+				lazyPropertyNames.add( property.getName() );
+			}
+		}
+
+		String[] getterNames = new String[propertySpan];
+		String[] setterNames = new String[propertySpan];
+		Class[] propTypes = new Class[propertySpan];
+		for ( int i = 0; i < propertySpan; i++ ) {
+			getterNames[i] = getters[i].getMethodName();
+			setterNames[i] = setters[i].getMethodName();
+			propTypes[i] = getters[i].getReturnType();
+		}
+
+		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
+			optimizer = null;
+		}
+		else {
+			// todo : YUCK!!!
+			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
+//			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
+//					mappedClass, getterNames, setterNames, propTypes
+//			);
+		}
+
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
+		// determine the id getter and setter methods from the proxy interface (if any)
+        // determine all interfaces needed by the resulting proxy
+		HashSet<Class> proxyInterfaces = new HashSet<Class>();
+		proxyInterfaces.add( HibernateProxy.class );
+
+		Class mappedClass = persistentClass.getMappedClass();
+		Class proxyInterface = persistentClass.getProxyInterface();
+
+		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
+			if ( !proxyInterface.isInterface() ) {
+				throw new MappingException(
+						"proxy must be either an interface, or the class itself: " + getEntityName()
+				);
+			}
+			proxyInterfaces.add( proxyInterface );
+		}
+
+		if ( mappedClass.isInterface() ) {
+			proxyInterfaces.add( mappedClass );
+		}
+
+		Iterator subclasses = persistentClass.getSubclassIterator();
+		while ( subclasses.hasNext() ) {
+			final Subclass subclass = ( Subclass ) subclasses.next();
+			final Class subclassProxy = subclass.getProxyInterface();
+			final Class subclassClass = subclass.getMappedClass();
+			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
+				if ( !subclassProxy.isInterface() ) {
+					throw new MappingException(
+							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
+					);
+				}
+				proxyInterfaces.add( subclassProxy );
+			}
+		}
+
+		Iterator properties = persistentClass.getPropertyIterator();
+		Class clazz = persistentClass.getMappedClass();
+		while ( properties.hasNext() ) {
+			Property property = (Property) properties.next();
+			Method method = property.getGetter(clazz).getMethod();
+			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
+                LOG.gettersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
+			}
+			method = property.getSetter(clazz).getMethod();
+            if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
+                LOG.settersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
+			}
+		}
+
+		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
+		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
+
+		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
+				null :
+		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
+		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
+				null :
+		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
+
+		ProxyFactory pf = buildProxyFactoryInternal( persistentClass, idGetter, idSetter );
+		try {
+			pf.postInstantiate(
+					getEntityName(),
+					mappedClass,
+					proxyInterfaces,
+					proxyGetIdentifierMethod,
+					proxySetIdentifierMethod,
+					persistentClass.hasEmbeddedIdentifier() ?
+			                (CompositeType) persistentClass.getIdentifier().getType() :
+			                null
+			);
+		}
+		catch ( HibernateException he ) {
+            LOG.unableToCreateProxyFactory(getEntityName(), he);
+			pf = null;
+		}
+		return pf;
+	}
+
+	protected ProxyFactory buildProxyFactoryInternal(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
+		// TODO : YUCK!!!  fix after HHH-1907 is complete
+		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
+//		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Instantiator buildInstantiator(PersistentClass persistentClass) {
+		if ( optimizer == null ) {
+			return new PojoInstantiator( persistentClass, null );
+		}
+		else {
+			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
+		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
+			setPropertyValuesWithOptimizer( entity, values );
+		}
+		else {
+			super.setPropertyValues( entity, values );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public Object[] getPropertyValues(Object entity) throws HibernateException {
+		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
+			return getPropertyValuesWithOptimizer( entity );
+		}
+		else {
+			return super.getPropertyValues( entity );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
+		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
+			return getPropertyValuesWithOptimizer( entity );
+		}
+		else {
+			return super.getPropertyValuesToInsert( entity, mergeMap, session );
+		}
+	}
+
+	protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
+		optimizer.getAccessOptimizer().setPropertyValues( object, values );
+	}
+
+	protected Object[] getPropertyValuesWithOptimizer(Object object) {
+		return optimizer.getAccessOptimizer().getPropertyValues( object );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public EntityMode getEntityMode() {
+		return EntityMode.POJO;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class getMappedClass() {
+		return mappedClass;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public boolean isLifecycleImplementor() {
+		return lifecycleImplementor;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public boolean isValidatableImplementor() {
+		return validatableImplementor;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
+		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
+		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Class getConcreteProxyClass() {
+		return proxyInterface;
+	}
+
+    //TODO: need to make the majority of this functionality into a top-level support class for custom impl support
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
+		if ( isInstrumented() ) {
+			Set lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
+					lazyPropertyNames : null;
+			//TODO: if we support multiple fetch groups, we would need
+			//      to clone the set of lazy properties!
+			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	@Override
+    public boolean hasUninitializedLazyProperties(Object entity) {
+		if ( getEntityMetamodel().hasLazyProperties() ) {
+			FieldInterceptor callback = FieldInterceptionHelper.extractFieldInterceptor( entity );
+			return callback != null && !callback.isInitialized();
+		}
+		else {
+			return false;
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isInstrumented() {
+		return FieldInterceptionHelper.isInstrumented( getMappedClass() );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
+		final Class concreteEntityClass = entityInstance.getClass();
+		if ( concreteEntityClass == getMappedClass() ) {
+			return getEntityName();
+		}
+		else {
+			String entityName = getEntityMetamodel().findEntityNameByEntityClass( concreteEntityClass );
+			if ( entityName == null ) {
+				throw new HibernateException(
+						"Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
+								+ " expected instance/subclass of [" + getEntityName() + "]"
+				);
+			}
+			return entityName;
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public EntityNameResolver[] getEntityNameResolvers() {
+		return null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
index b888aab1ad..4f0348c09c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
@@ -1,362 +1,363 @@
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
 import java.util.Map;
+
+import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.intercept.LazyPropertyInitializer;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.tuple.StandardProperty;
 
 /**
  * Collection of convenience methods relating to operations across arrays of types...
  *
  * @author Steve Ebersole
  */
 public class TypeHelper {
 	/**
 	 * Disallow instantiation
 	 */
 	private TypeHelper() {
 	}
 
 	/**
 	 * Deep copy a series of values from one array to another...
 	 *
 	 * @param values The values to copy (the source)
 	 * @param types The value types
 	 * @param copy an array indicating which values to include in the copy
 	 * @param target The array into which to copy the values
 	 * @param session The originating session
 	 */
 	public static void deepCopy(
 			final Object[] values,
 			final Type[] types,
 			final boolean[] copy,
 			final Object[] target,
 			final SessionImplementor session) {
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( copy[i] ) {
 				if ( values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| values[i] == BackrefPropertyAccessor.UNKNOWN ) {
 					target[i] = values[i];
 				}
 				else {
 					target[i] = types[i].deepCopy( values[i], session.getEntityMode(), session
 						.getFactory() );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Apply the {@link Type#beforeAssemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param session The originating session
 	 */
 	public static void beforeAssemble(
 			final Serializable[] row,
 			final Type[] types,
 			final SessionImplementor session) {
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( row[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY
 				&& row[i] != BackrefPropertyAccessor.UNKNOWN ) {
 				types[i].beforeAssemble( row[i], session );
 			}
 		}
 	}
 
 	/**
 	 * Apply the {@link Type#assemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @return The assembled state
 	 */
 	public static Object[] assemble(
 			final Serializable[] row,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner) {
 		Object[] assembled = new Object[row.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == BackrefPropertyAccessor.UNKNOWN ) {
 				assembled[i] = row[i];
 			}
 			else {
 				assembled[i] = types[i].assemble( row[i], session, owner );
 			}
 		}
 		return assembled;
 	}
 
 	/**
 	 * Apply the {@link Type#disassemble} operation across a series of values.
 	 *
 	 * @param row The values
 	 * @param types The value types
 	 * @param nonCacheable An array indicating which values to include in the disassembled state
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 *
 	 * @return The disassembled state
 	 */
 	public static Serializable[] disassemble(
 			final Object[] row,
 			final Type[] types,
 			final boolean[] nonCacheable,
 			final SessionImplementor session,
 			final Object owner) {
 		Serializable[] disassembled = new Serializable[row.length];
 		for ( int i = 0; i < row.length; i++ ) {
 			if ( nonCacheable!=null && nonCacheable[i] ) {
 				disassembled[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 			else if ( row[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || row[i] == BackrefPropertyAccessor.UNKNOWN ) {
 				disassembled[i] = (Serializable) row[i];
 			}
 			else {
 				disassembled[i] = types[i].disassemble( row[i], session, owner );
 			}
 		}
 		return disassembled;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replace(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 				|| original[i] == BackrefPropertyAccessor.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 * @param foreignKeyDirection FK directionality to be applied to the replacement
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replace(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 				|| original[i] == BackrefPropertyAccessor.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache, foreignKeyDirection );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Apply the {@link Type#replace} operation across a series of values, as long as the corresponding
 	 * {@link Type} is an association.
 	 * <p/>
 	 * If the corresponding type is a component type, then apply {@link Type#replace} across the component
 	 * subtypes but do not replace the component value itself.
 	 *
 	 * @param original The source of the state
 	 * @param target The target into which to replace the source values.
 	 * @param types The value types
 	 * @param session The originating session
 	 * @param owner The entity "owning" the values
 	 * @param copyCache A map representing a cache of already replaced state
 	 * @param foreignKeyDirection FK directionality to be applied to the replacement
 	 *
 	 * @return The replaced state
 	 */
 	public static Object[] replaceAssociations(
 			final Object[] original,
 			final Object[] target,
 			final Type[] types,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 		Object[] copied = new Object[original.length];
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( original[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY
 					|| original[i] == BackrefPropertyAccessor.UNKNOWN ) {
 				copied[i] = target[i];
 			}
 			else if ( types[i].isComponentType() ) {
 				// need to extract the component values and check for subtype replacements...
 				CompositeType componentType = ( CompositeType ) types[i];
 				Type[] subtypes = componentType.getSubtypes();
 				Object[] origComponentValues = original[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues( original[i], session );
 				Object[] targetComponentValues = target[i] == null ? new Object[subtypes.length] : componentType.getPropertyValues( target[i], session );
 				replaceAssociations( origComponentValues, targetComponentValues, subtypes, session, null, copyCache, foreignKeyDirection );
 				copied[i] = target[i];
 			}
 			else if ( !types[i].isAssociationType() ) {
 				copied[i] = target[i];
 			}
 			else {
 				copied[i] = types[i].replace( original[i], target[i], session, owner, copyCache, foreignKeyDirection );
 			}
 		}
 		return copied;
 	}
 
 	/**
 	 * Determine if any of the given field values are dirty, returning an array containing
 	 * indices of the dirty fields.
 	 * <p/>
 	 * If it is determined that no fields are dirty, null is returned.
 	 *
 	 * @param properties The property definitions
 	 * @param currentState The current state of the entity
 	 * @param previousState The baseline state of the entity
 	 * @param includeColumns Columns to be included in the dirty checking, per property
 	 * @param anyUninitializedProperties Does the entity currently hold any uninitialized property values?
 	 * @param session The session from which the dirty check request originated.
 	 * 
 	 * @return Array containing indices of the dirty properties, or null if no properties considered dirty.
 	 */
 	public static int[] findDirty(
 			final StandardProperty[] properties,
 			final Object[] currentState,
 			final Object[] previousState,
 			final boolean[][] includeColumns,
 			final boolean anyUninitializedProperties,
 			final SessionImplementor session) {
 		int[] results = null;
 		int count = 0;
 		int span = properties.length;
 
 		for ( int i = 0; i < span; i++ ) {
 			final boolean dirty = currentState[i] != LazyPropertyInitializer.UNFETCHED_PROPERTY
 					&& properties[i].isDirtyCheckable( anyUninitializedProperties )
 					&& properties[i].getType().isDirty( previousState[i], currentState[i], includeColumns[i], session );
 			if ( dirty ) {
 				if ( results == null ) {
 					results = new int[span];
 				}
 				results[count++] = i;
 			}
 		}
 
 		if ( count == 0 ) {
 			return null;
 		}
 		else {
 			int[] trimmed = new int[count];
 			System.arraycopy( results, 0, trimmed, 0, count );
 			return trimmed;
 		}
 	}
 
 	/**
 	 * Determine if any of the given field values are modified, returning an array containing
 	 * indices of the modified fields.
 	 * <p/>
 	 * If it is determined that no fields are dirty, null is returned.
 	 *
 	 * @param properties The property definitions
 	 * @param currentState The current state of the entity
 	 * @param previousState The baseline state of the entity
 	 * @param includeColumns Columns to be included in the mod checking, per property
 	 * @param anyUninitializedProperties Does the entity currently hold any uninitialized property values?
 	 * @param session The session from which the dirty check request originated.
 	 *
 	 * @return Array containing indices of the modified properties, or null if no properties considered modified.
 	 */
 	public static int[] findModified(
 			final StandardProperty[] properties,
 			final Object[] currentState,
 			final Object[] previousState,
 			final boolean[][] includeColumns,
 			final boolean anyUninitializedProperties,
 			final SessionImplementor session) {
 		int[] results = null;
 		int count = 0;
 		int span = properties.length;
 
 		for ( int i = 0; i < span; i++ ) {
 			final boolean modified = currentState[i]!=LazyPropertyInitializer.UNFETCHED_PROPERTY
 					&& properties[i].isDirtyCheckable(anyUninitializedProperties)
 					&& properties[i].getType().isModified( previousState[i], currentState[i], includeColumns[i], session );
 
 			if ( modified ) {
 				if ( results == null ) {
 					results = new int[span];
 				}
 				results[count++] = i;
 			}
 		}
 
 		if ( count == 0 ) {
 			return null;
 		}
 		else {
 			int[] trimmed = new int[count];
 			System.arraycopy( results, 0, trimmed, 0, count );
 			return trimmed;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/InvocationTargetExceptionTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/InvocationTargetExceptionTest.java
index 57d0cc3882..53cf91ba6a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/InvocationTargetExceptionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/InvocationTargetExceptionTest.java
@@ -1,92 +1,92 @@
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
 package org.hibernate.test.bytecode.javassist;
 import java.text.ParseException;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
-import org.hibernate.bytecode.javassist.BytecodeProviderImpl;
+import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
 import org.hibernate.cfg.Environment;
 
 import org.junit.Test;
 
 import org.hibernate.testing.Skip;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.bytecode.Bean;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.fail;
 
 /**
  * Test that the Javassist-based lazy initializer properly handles InvocationTargetExceptions
  *
  * @author Steve Ebersole
  */
 @Skip(
 		condition = InvocationTargetExceptionTest.LocalSkipMatcher.class,
 		message = "environment not configured for javassist bytecode provider"
 )
 public class InvocationTargetExceptionTest extends BaseCoreFunctionalTestCase {
 	public static class LocalSkipMatcher implements Skip.Matcher {
 		@Override
 		public boolean isMatch() {
 			return ! BytecodeProviderImpl.class.isInstance( Environment.getBytecodeProvider() );
 		}
 	}
 
 	@Override
 	public String[] getMappings() {
 		return new String[] { "bytecode/Bean.hbm.xml" };
 	}
 
 	@Test
 	public void testProxiedInvocationException() {
 		Session s = openSession();
 		s.beginTransaction();
 		Bean bean = new Bean();
 		bean.setSomeString( "my-bean" );
 		s.save( bean );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		bean = ( Bean ) s.load( Bean.class, bean.getSomeString() );
 		assertFalse( Hibernate.isInitialized( bean ) );
 		try {
 			bean.throwException();
 			fail( "exception not thrown" );
 		}
 		catch ( ParseException e ) {
 			// expected behavior
 		}
 		catch ( Throwable t ) {
 			fail( "unexpected exception type : " + t );
 		}
 
 		s.delete( bean );
 		s.getTransaction().commit();
 		s.close();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/ReflectionOptimizerTest.java b/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/ReflectionOptimizerTest.java
index 852fadf4d3..16f884cd1f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/ReflectionOptimizerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/bytecode/javassist/ReflectionOptimizerTest.java
@@ -1,71 +1,71 @@
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
 package org.hibernate.test.bytecode.javassist;
 
-import org.hibernate.bytecode.ReflectionOptimizer;
-import org.hibernate.bytecode.javassist.BytecodeProviderImpl;
+import org.hibernate.bytecode.spi.ReflectionOptimizer;
+import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.bytecode.Bean;
 import org.hibernate.test.bytecode.BeanReflectionHelper;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class ReflectionOptimizerTest extends BaseUnitTestCase {
 	@Test
 	public void testReflectionOptimization() {
 		BytecodeProviderImpl provider = new BytecodeProviderImpl();
 		ReflectionOptimizer optimizer = provider.getReflectionOptimizer(
 				Bean.class,
 		        BeanReflectionHelper.getGetterNames(),
 		        BeanReflectionHelper.getSetterNames(),
 		        BeanReflectionHelper.getTypes()
 		);
 		assertNotNull( optimizer );
 		assertNotNull( optimizer.getInstantiationOptimizer() );
 		assertNotNull( optimizer.getAccessOptimizer() );
 
 		Object instance = optimizer.getInstantiationOptimizer().newInstance();
 		assertEquals( instance.getClass(), Bean.class );
 		Bean bean = ( Bean ) instance;
 
 		optimizer.getAccessOptimizer().setPropertyValues( bean, BeanReflectionHelper.TEST_VALUES );
 		assertEquals( bean.getSomeString(), BeanReflectionHelper.TEST_VALUES[0] );
 		Object[] values = optimizer.getAccessOptimizer().getPropertyValues( bean );
 		assertEquivalent( values, BeanReflectionHelper.TEST_VALUES );
 	}
 
 	private void assertEquivalent(Object[] checkValues, Object[] values) {
 		assertEquals( "Different lengths", checkValues.length, values.length );
 		for ( int i = 0; i < checkValues.length; i++ ) {
 			assertEquals( "different values at index [" + i + "]", checkValues[i], values[i] );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/buildtime/InstrumentTest.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/buildtime/InstrumentTest.java
index 6a04dd2e2a..1201147cb2 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/buildtime/InstrumentTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/buildtime/InstrumentTest.java
@@ -1,120 +1,120 @@
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
 package org.hibernate.test.instrument.buildtime;
 
-import org.hibernate.intercept.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 
 import org.junit.Test;
 
 import org.hibernate.testing.Skip;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.instrument.cases.Executable;
 import org.hibernate.test.instrument.cases.TestCustomColumnReadAndWrite;
 import org.hibernate.test.instrument.cases.TestDirtyCheckExecutable;
 import org.hibernate.test.instrument.cases.TestFetchAllExecutable;
 import org.hibernate.test.instrument.cases.TestInjectFieldInterceptorExecutable;
 import org.hibernate.test.instrument.cases.TestIsPropertyInitializedExecutable;
 import org.hibernate.test.instrument.cases.TestLazyExecutable;
 import org.hibernate.test.instrument.cases.TestLazyManyToOneExecutable;
 import org.hibernate.test.instrument.cases.TestLazyPropertyCustomTypeExecutable;
 import org.hibernate.test.instrument.cases.TestManyToOneProxyExecutable;
 import org.hibernate.test.instrument.cases.TestSharedPKOneToOneExecutable;
 import org.hibernate.test.instrument.domain.Document;
 
 /**
  * @author Gavin King
  */
 @Skip(
 		message = "domain classes not instrumented for build-time instrumentation testing",
 		condition = InstrumentTest.SkipCheck.class
 )
 public class InstrumentTest extends BaseUnitTestCase {
 	@Test
 	public void testDirtyCheck() throws Exception {
 		execute( new TestDirtyCheckExecutable() );
 	}
 
 	@Test
 	public void testFetchAll() throws Exception {
 		execute( new TestFetchAllExecutable() );
 	}
 
 	@Test
 	public void testLazy() throws Exception {
 		execute( new TestLazyExecutable() );
 	}
 
 	@Test
 	public void testLazyManyToOne() throws Exception {
 		execute( new TestLazyManyToOneExecutable() );
 	}
 
 	@Test
 	public void testSetFieldInterceptor() throws Exception {
 		execute( new TestInjectFieldInterceptorExecutable() );
 	}
 
 	@Test
 	public void testPropertyInitialized() throws Exception {
 		execute( new TestIsPropertyInitializedExecutable() );
 	}
 
 	@Test
 	public void testManyToOneProxy() throws Exception {
 		execute( new TestManyToOneProxyExecutable() );
 	}
 
 	@Test
 	public void testLazyPropertyCustomTypeExecutable() throws Exception {
 		execute( new TestLazyPropertyCustomTypeExecutable() );
 	}
 
 	@Test
 	public void testSharedPKOneToOne() throws Exception {
 		execute( new TestSharedPKOneToOneExecutable() );
 	}
 
 	@Test
 	public void testCustomColumnReadAndWrite() throws Exception {
 		execute( new TestCustomColumnReadAndWrite() );
 	}	
 	
 	private void execute(Executable executable) throws Exception {
 		executable.prepare();
 		try {
 			executable.execute();
 		}
 		finally {
 			executable.complete();
 		}
 	}
 
 	public static class SkipCheck implements Skip.Matcher {
 		@Override
 		public boolean isMatch() {
 			return ! FieldInterceptionHelper.isInstrumented( new Document() );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestInjectFieldInterceptorExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestInjectFieldInterceptorExecutable.java
index 272f687d23..826eb4034d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestInjectFieldInterceptorExecutable.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestInjectFieldInterceptorExecutable.java
@@ -1,15 +1,17 @@
 package org.hibernate.test.instrument.cases;
 import java.util.HashSet;
-import org.hibernate.intercept.FieldInterceptionHelper;
+
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+
 import org.hibernate.test.instrument.domain.Document;
-
-/**
- * @author Steve Ebersole
- */
-public class TestInjectFieldInterceptorExecutable extends AbstractExecutable {
-	public void execute() {
-		Document doc = new Document();
-		FieldInterceptionHelper.injectFieldInterceptor( doc, "Document", new HashSet(), null );
-		doc.getId();
-	}
-}
+
+/**
+ * @author Steve Ebersole
+ */
+public class TestInjectFieldInterceptorExecutable extends AbstractExecutable {
+	public void execute() {
+		Document doc = new Document();
+		FieldInterceptionHelper.injectFieldInterceptor( doc, "Document", new HashSet(), null );
+		doc.getId();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyPropertyCustomTypeExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyPropertyCustomTypeExecutable.java
index 260cf10c28..8388d76152 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyPropertyCustomTypeExecutable.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/TestLazyPropertyCustomTypeExecutable.java
@@ -1,89 +1,90 @@
 package org.hibernate.test.instrument.cases;
 import java.util.Iterator;
 import junit.framework.Assert;
 import org.hibernate.Session;
-import org.hibernate.intercept.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+
 import org.hibernate.test.instrument.domain.Problematic;
-
-/**
- * {@inheritDoc}
- *
- * @author Steve Ebersole
- */
-public class TestLazyPropertyCustomTypeExecutable extends AbstractExecutable {
-
-	protected String[] getResources() {
-		return new String[] { "org/hibernate/test/instrument/domain/Problematic.hbm.xml" };
-	}
-
-	public void execute() throws Exception {
-		Session s = getFactory().openSession();
-		Problematic p = new Problematic();
-		try {
-			s.beginTransaction();
-			p.setName( "whatever" );
-			p.setBytes( new byte[] { 1, 0, 1, 1, 0 } );
-			s.save( p );
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-
-		// this access should be ok because p1 is not a lazy proxy 
-		s = getFactory().openSession();
-		try {
-			s.beginTransaction();
-			Problematic p1 = (Problematic) s.get( Problematic.class, p.getId() );
-			Assert.assertTrue( FieldInterceptionHelper.isInstrumented( p1 ) );
-			p1.getRepresentation();
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-		
-		s = getFactory().openSession();
-		try {
-			s.beginTransaction();
-			Problematic p1 = (Problematic) s.createQuery( "from Problematic" ).setReadOnly(true ).list().get( 0 );
-			p1.getRepresentation();
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-		
-		s = getFactory().openSession();
-		try {
-			s.beginTransaction();
-			Problematic p1 = (Problematic) s.load( Problematic.class, p.getId() );
-			Assert.assertFalse( FieldInterceptionHelper.isInstrumented( p1 ) );
-			p1.setRepresentation( p.getRepresentation() );
-			s.getTransaction().commit();
-		} catch (Exception e) {
-			s.getTransaction().rollback();
-			throw e;
-		} finally {
-			s.close();
-		}
-	}
-
-	protected void cleanup() {
-		Session s = getFactory().openSession();
-		s.beginTransaction();
-		Iterator itr = s.createQuery( "from Problematic" ).list().iterator();
-		while ( itr.hasNext() ) {
-			Problematic p = (Problematic) itr.next();
-			s.delete( p );
-		}
-		s.getTransaction().commit();
-		s.close();
-	}
+
+/**
+ * {@inheritDoc}
+ *
+ * @author Steve Ebersole
+ */
+public class TestLazyPropertyCustomTypeExecutable extends AbstractExecutable {
+
+	protected String[] getResources() {
+		return new String[] { "org/hibernate/test/instrument/domain/Problematic.hbm.xml" };
+	}
+
+	public void execute() throws Exception {
+		Session s = getFactory().openSession();
+		Problematic p = new Problematic();
+		try {
+			s.beginTransaction();
+			p.setName( "whatever" );
+			p.setBytes( new byte[] { 1, 0, 1, 1, 0 } );
+			s.save( p );
+			s.getTransaction().commit();
+		} catch (Exception e) {
+			s.getTransaction().rollback();
+			throw e;
+		} finally {
+			s.close();
+		}
+
+		// this access should be ok because p1 is not a lazy proxy 
+		s = getFactory().openSession();
+		try {
+			s.beginTransaction();
+			Problematic p1 = (Problematic) s.get( Problematic.class, p.getId() );
+			Assert.assertTrue( FieldInterceptionHelper.isInstrumented( p1 ) );
+			p1.getRepresentation();
+			s.getTransaction().commit();
+		} catch (Exception e) {
+			s.getTransaction().rollback();
+			throw e;
+		} finally {
+			s.close();
+		}
+		
+		s = getFactory().openSession();
+		try {
+			s.beginTransaction();
+			Problematic p1 = (Problematic) s.createQuery( "from Problematic" ).setReadOnly(true ).list().get( 0 );
+			p1.getRepresentation();
+			s.getTransaction().commit();
+		} catch (Exception e) {
+			s.getTransaction().rollback();
+			throw e;
+		} finally {
+			s.close();
+		}
+		
+		s = getFactory().openSession();
+		try {
+			s.beginTransaction();
+			Problematic p1 = (Problematic) s.load( Problematic.class, p.getId() );
+			Assert.assertFalse( FieldInterceptionHelper.isInstrumented( p1 ) );
+			p1.setRepresentation( p.getRepresentation() );
+			s.getTransaction().commit();
+		} catch (Exception e) {
+			s.getTransaction().rollback();
+			throw e;
+		} finally {
+			s.close();
+		}
+	}
+
+	protected void cleanup() {
+		Session s = getFactory().openSession();
+		s.beginTransaction();
+		Iterator itr = s.createQuery( "from Problematic" ).list().iterator();
+		while ( itr.hasNext() ) {
+			Problematic p = (Problematic) itr.next();
+			s.delete( p );
+		}
+		s.getTransaction().commit();
+		s.close();
+	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java
index d51afe1110..4d93f878ae 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/AbstractTransformingClassLoaderInstrumentTestCase.java
@@ -1,167 +1,167 @@
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
 package org.hibernate.test.instrument.runtime;
 
 import java.lang.reflect.InvocationTargetException;
 
 import org.hibernate.HibernateException;
-import org.hibernate.bytecode.BytecodeProvider;
-import org.hibernate.bytecode.InstrumentedClassLoader;
-import org.hibernate.bytecode.util.BasicClassFilter;
-import org.hibernate.bytecode.util.FieldFilter;
+import org.hibernate.bytecode.buildtime.spi.BasicClassFilter;
+import org.hibernate.bytecode.buildtime.spi.FieldFilter;
+import org.hibernate.bytecode.spi.BytecodeProvider;
+import org.hibernate.bytecode.spi.InstrumentedClassLoader;
 
 import org.junit.Rule;
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.testing.junit4.ClassLoadingIsolater;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractTransformingClassLoaderInstrumentTestCase extends BaseUnitTestCase {
 
 	@Rule
 	public ClassLoadingIsolater isolater = new ClassLoadingIsolater(
 			new ClassLoadingIsolater.IsolatedClassLoaderProvider() {
 				final BytecodeProvider provider = buildBytecodeProvider();
 
 				@Override
 				public ClassLoader buildIsolatedClassLoader() {
 					return new InstrumentedClassLoader(
 							Thread.currentThread().getContextClassLoader(),
 							provider.getTransformer(
 									new BasicClassFilter( new String[] { "org.hibernate.test.instrument" }, null ),
 									new FieldFilter() {
 										public boolean shouldInstrumentField(String className, String fieldName) {
 											return className.startsWith( "org.hibernate.test.instrument.domain" );
 										}
 										public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName) {
 											return fieldOwnerClassName.startsWith( "org.hibernate.test.instrument.domain" )
 													&& transformingClassName.equals( fieldOwnerClassName );
 										}
 									}
 							)
 					);
 				}
 
 				@Override
 				public void releaseIsolatedClassLoader(ClassLoader isolatedClassLoader) {
 					// nothing to do
 				}
 			}
 	);
 
 	protected abstract BytecodeProvider buildBytecodeProvider();
 
 
 	// the tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Test
 	public void testSetFieldInterceptor() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestInjectFieldInterceptorExecutable" );
 	}
 
 	@Test
 	public void testDirtyCheck() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestDirtyCheckExecutable" );
 	}
 
 	@Test
 	public void testFetchAll() throws Exception {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestFetchAllExecutable" );
 	}
 
 	@Test
 	public void testLazy() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyExecutable" );
 	}
 
 	@Test
 	public void testLazyManyToOne() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyManyToOneExecutable" );
 	}
 
 	@Test
 	public void testPropertyInitialized() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestIsPropertyInitializedExecutable" );
 	}
 
 	@Test
 	public void testManyToOneProxy() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestManyToOneProxyExecutable" );
 	}
 
 	@Test
 	public void testLazyPropertyCustomType() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestLazyPropertyCustomTypeExecutable" );
 	}
 
 	@Test
 	public void testSharedPKOneToOne() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestSharedPKOneToOneExecutable" );
 	}
 
 	@Test
 	public void testCustomColumnReadAndWrite() {
 		executeExecutable( "org.hibernate.test.instrument.cases.TestCustomColumnReadAndWrite" );
 	}	
 
 	// reflection code to ensure isolation into the created classloader ~~~~~~~
 
 	private static final Class[] SIG = new Class[] {};
 	private static final Object[] ARGS = new Object[] {};
 
 	public void executeExecutable(String name) {
 		Class execClass = null;
 		Object executable = null;
 		try {
 			execClass = Thread.currentThread().getContextClassLoader().loadClass( name );
 			executable = execClass.newInstance();
 		}
 		catch( Throwable t ) {
 			throw new HibernateException( "could not load executable", t );
 		}
 		try {
 			execClass.getMethod( "prepare", SIG ).invoke( executable, ARGS );
 			execClass.getMethod( "execute", SIG ).invoke( executable, ARGS );
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new HibernateException( "could not exeucte executable", e );
 		}
 		catch ( IllegalAccessException e ) {
 			throw new HibernateException( "could not exeucte executable", e );
 		}
 		catch ( InvocationTargetException e ) {
 			throw new HibernateException( "could not exeucte executable", e.getTargetException() );
 		}
 		finally {
 			try {
 				execClass.getMethod( "complete", SIG ).invoke( executable, ARGS );
 			}
 			catch ( Throwable ignore ) {
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/JavassistInstrumentationTest.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/JavassistInstrumentationTest.java
index c0c0f4394e..0915db1bfd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/JavassistInstrumentationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/runtime/JavassistInstrumentationTest.java
@@ -1,36 +1,36 @@
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
-package org.hibernate.test.instrument.runtime;
-
-import org.hibernate.bytecode.BytecodeProvider;
-import org.hibernate.bytecode.javassist.BytecodeProviderImpl;
-
-/**
- * @author Steve Ebersole
- */
-public class JavassistInstrumentationTest extends AbstractTransformingClassLoaderInstrumentTestCase {
-	protected BytecodeProvider buildBytecodeProvider() {
-		return new BytecodeProviderImpl();
-	}
-}
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
+package org.hibernate.test.instrument.runtime;
+
+import org.hibernate.bytecode.spi.BytecodeProvider;
+import org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JavassistInstrumentationTest extends AbstractTransformingClassLoaderInstrumentTestCase {
+	protected BytecodeProvider buildBytecodeProvider() {
+		return new BytecodeProviderImpl();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java b/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java
index 3161dd37ea..b6063fb251 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lazycache/InstrumentCacheTest.java
@@ -1,149 +1,149 @@
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
 package org.hibernate.test.lazycache;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.intercept.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 
 import org.junit.Test;
 
 import org.hibernate.testing.Skip;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gavin King
  */
 @Skip( condition = InstrumentCacheTest.SkipMatcher.class, message = "Test domain classes not instrumented" )
 public class InstrumentCacheTest extends BaseCoreFunctionalTestCase {
 	public static class SkipMatcher implements Skip.Matcher {
 		@Override
 		public boolean isMatch() {
 			return ! FieldInterceptionHelper.isInstrumented( Document.class );
 		}
 	}
 
 	public String[] getMappings() {
 		return new String[] { "lazycache/Documents.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
 	}
 
 	public boolean overrideCacheStrategy() {
 		return false;
 	}
 
 	@Test
 	public void testInitFromCache() {
 		Session s;
 		Transaction tx;
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.persist( new Document("HiA", "Hibernate book", "Hibernate is....") );
 		tx.commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.createQuery("from Document fetch all properties").uniqueResult();
 		tx.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		Document d = (Document) s.createCriteria(Document.class).uniqueResult();
 		assertFalse( Hibernate.isPropertyInitialized(d, "text") );
 		assertFalse( Hibernate.isPropertyInitialized(d, "summary") );
 		assertEquals( "Hibernate is....", d.getText() );
 		assertTrue( Hibernate.isPropertyInitialized(d, "text") );
 		assertTrue( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 
 		assertEquals( 2, sessionFactory().getStatistics().getPrepareStatementCount() );
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		d = (Document) s.get(Document.class, d.getId());
 		assertFalse( Hibernate.isPropertyInitialized(d, "text") );
 		assertFalse( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testInitFromCache2() {
 		Session s;
 		Transaction tx;
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.persist( new Document("HiA", "Hibernate book", "Hibernate is....") );
 		tx.commit();
 		s.close();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		s.createQuery("from Document fetch all properties").uniqueResult();
 		tx.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		Document d = (Document) s.createCriteria(Document.class).uniqueResult();
 		assertFalse( Hibernate.isPropertyInitialized(d, "text") );
 		assertFalse( Hibernate.isPropertyInitialized(d, "summary") );
 		assertEquals( "Hibernate is....", d.getText() );
 		assertTrue( Hibernate.isPropertyInitialized(d, "text") );
 		assertTrue( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getPrepareStatementCount() );
 
 		s = sessionFactory().openSession();
 		tx = s.beginTransaction();
 		d = (Document) s.get(Document.class, d.getId());
 		assertTrue( Hibernate.isPropertyInitialized(d, "text") );
 		assertTrue( Hibernate.isPropertyInitialized(d, "summary") );
 		tx.commit();
 		s.close();
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java b/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java
index a5496d5f37..f0220c4d20 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/lazyonetoone/LazyOneToOneTest.java
@@ -1,111 +1,111 @@
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
 package org.hibernate.test.lazyonetoone;
 import java.util.Date;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.intercept.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 
 import org.junit.Test;
 
 import org.hibernate.testing.Skip;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gavin King
  */
 @Skip(
 		condition = LazyOneToOneTest.DomainClassesInstrumentedMatcher.class,
 		message = "Test domain classes were not instrumented"
 )
 public class LazyOneToOneTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "lazyonetoone/Person.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setProperty(Environment.MAX_FETCH_DEPTH, "2");
 		cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
 	}
 
 	@Test
 	public void testLazy() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Person p = new Person("Gavin");
 		Person p2 = new Person("Emmanuel");
 		Employee e = new Employee(p);
 		new Employment(e, "JBoss");
 		Employment old = new Employment(e, "IFA");
 		old.setEndDate( new Date() );
 		s.persist(p);
 		s.persist(p2);
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Person) s.createQuery("from Person where name='Gavin'").uniqueResult();
 		//assertFalse( Hibernate.isPropertyInitialized(p, "employee") );
 		assertSame( p.getEmployee().getPerson(), p );
 		assertTrue( Hibernate.isInitialized( p.getEmployee().getEmployments() ) );
 		assertEquals( p.getEmployee().getEmployments().size(), 1 );
 		p2 = (Person) s.createQuery("from Person where name='Emmanuel'").uniqueResult();
 		assertNull( p2.getEmployee() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Person) s.get(Person.class, "Gavin");
 		//assertFalse( Hibernate.isPropertyInitialized(p, "employee") );
 		assertSame( p.getEmployee().getPerson(), p );
 		assertTrue( Hibernate.isInitialized( p.getEmployee().getEmployments() ) );
 		assertEquals( p.getEmployee().getEmployments().size(), 1 );
 		p2 = (Person) s.get(Person.class, "Emmanuel");
 		assertNull( p2.getEmployee() );
 		s.delete(p2);
 		s.delete(old);
 		s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	public static class DomainClassesInstrumentedMatcher implements Skip.Matcher {
 		@Override
 		public boolean isMatch() {
 			return FieldInterceptionHelper.isInstrumented( Person.class );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/SaveOrUpdateTest.java b/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/SaveOrUpdateTest.java
index dc02c9ce0d..807a7d9b27 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/SaveOrUpdateTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/nonflushedchanges/SaveOrUpdateTest.java
@@ -1,549 +1,549 @@
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
 package org.hibernate.test.nonflushedchanges;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Projections;
-import org.hibernate.intercept.FieldInterceptionHelper;
 import org.hibernate.proxy.HibernateProxy;
 
 import org.junit.Test;
 
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * adapted this from "ops" tests version
  *
  * @author Gail Badner
  * @author Gavin King
  */
 public class SaveOrUpdateTest extends AbstractOperationTestCase {
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" );
 	}
 
 	public String[] getMappings() {
 		return new String[] { "nonflushedchanges/Node.hbm.xml" };
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment"})
 	public void testSaveOrUpdateDeepTree() throws Exception {
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		Node root = new Node( "root" );
 		Node child = new Node( "child" );
 		Node grandchild = new Node( "grandchild" );
 		root.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		child = ( Node ) getOldToNewEntityRefMap().get( child );
 		grandchild = ( Node ) getOldToNewEntityRefMap().get( grandchild );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 3 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		grandchild.setDescription( "the grand child" );
 		Node grandchild2 = new Node( "grandchild2" );
 		child.addChild( grandchild2 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 1 );
 		clearCounts();
 
 		Node child2 = new Node( "child2" );
 		Node grandchild3 = new Node( "grandchild3" );
 		child2.addChild( grandchild3 );
 		root.addChild( child2 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.delete( grandchild );
 		s.delete( grandchild2 );
 		s.delete( grandchild3 );
 		s.delete( child );
 		s.delete( child2 );
 		s.delete( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment"})
 	public void testSaveOrUpdateDeepTreeWithGeneratedId() throws Exception {
 		boolean instrumented = FieldInterceptionHelper.isInstrumented( new NumberedNode() );
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		NumberedNode root = new NumberedNode( "root" );
 		NumberedNode child = new NumberedNode( "child" );
 		NumberedNode grandchild = new NumberedNode( "grandchild" );
 		root.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		child = ( NumberedNode ) getOldToNewEntityRefMap().get( child );
 		grandchild = ( NumberedNode ) getOldToNewEntityRefMap().get( grandchild );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 3 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		child = ( NumberedNode ) root.getChildren().iterator().next();
 		grandchild = ( NumberedNode ) child.getChildren().iterator().next();
 		grandchild.setDescription( "the grand child" );
 		NumberedNode grandchild2 = new NumberedNode( "grandchild2" );
 		child.addChild( grandchild2 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( instrumented ? 1 : 3 );
 		clearCounts();
 
 		NumberedNode child2 = new NumberedNode( "child2" );
 		NumberedNode grandchild3 = new NumberedNode( "grandchild3" );
 		child2.addChild( grandchild3 );
 		root.addChild( child2 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( instrumented ? 0 : 4 );
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.createQuery( "delete from NumberedNode where name like 'grand%'" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode where name like 'child%'" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode" ).executeUpdate();
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment"})
 	public void testSaveOrUpdateTree() throws Exception {
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		Node root = new Node( "root" );
 		Node child = new Node( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		child = ( Node ) getOldToNewEntityRefMap().get( child );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 2 );
 		clearCounts();
 
 		root.setDescription( "The root node" );
 		child.setDescription( "The child node" );
 
 		Node secondChild = new Node( "second child" );
 
 		root.addChild( secondChild );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 2 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.createQuery( "delete from Node where parent is not null" ).executeUpdate();
 		s.createQuery( "delete from Node" ).executeUpdate();
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment"})
 	public void testSaveOrUpdateTreeWithGeneratedId() throws Exception {
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		NumberedNode root = new NumberedNode( "root" );
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		child = ( NumberedNode ) getOldToNewEntityRefMap().get( child );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 2 );
 		clearCounts();
 
 		root.setDescription( "The root node" );
 		child.setDescription( "The child node" );
 
 		NumberedNode secondChild = new NumberedNode( "second child" );
 
 		root.addChild( secondChild );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 2 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.createQuery( "delete from NumberedNode where parent is not null" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode" ).executeUpdate();
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnnecessaryBoxing"})
 	public void testSaveOrUpdateManaged() throws Exception {
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		NumberedNode root = new NumberedNode( "root" );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		root = ( NumberedNode ) s.get( NumberedNode.class, root.getId() );
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		assertNull( getOldToNewEntityRefMap().get( child ) );
 		s.flush();
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		child = ( NumberedNode ) getOldToNewEntityRefMap().get( child );
 		child = ( NumberedNode ) root.getChildren().iterator().next();
 		assertTrue( s.contains( child ) );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		child = ( NumberedNode ) getOldToNewEntityRefMap().get( child );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertTrue( root.getChildren().contains( child ) );
 		assertEquals( root.getChildren().size(), 1 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		assertEquals(
 				Long.valueOf( 2 ),
 				s.createCriteria( NumberedNode.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult()
 		);
 		s.delete( root );
 		s.delete( child );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnnecessaryBoxing"})
 	public void testSaveOrUpdateGot() throws Exception {
 		boolean instrumented = FieldInterceptionHelper.isInstrumented( new NumberedNode() );
 
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		NumberedNode root = new NumberedNode( "root" );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( instrumented ? 0 : 1 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		root = ( NumberedNode ) s.get( NumberedNode.class, Long.valueOf( root.getId() ) );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		Hibernate.initialize( root.getChildren() );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( NumberedNode ) getOldToNewEntityRefMap().get( root );
 		assertTrue( Hibernate.isInitialized( root.getChildren() ) );
 		child = ( NumberedNode ) root.getChildren().iterator().next();
 		assertTrue( s.contains( child ) );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( instrumented ? 0 : 1 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		assertEquals(
 				s.createCriteria( NumberedNode.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult(),
 				new Long( 2 )
 		);
 		s.delete( root );
 		s.delete( child );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnnecessaryBoxing"})
 	public void testSaveOrUpdateGotWithMutableProp() throws Exception {
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		Node root = new Node( "root" );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( 0 );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		root = ( Node ) s.get( Node.class, "root" );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		Hibernate.initialize( root.getChildren() );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		clearCounts();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		Node child = new Node( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		child = ( Node ) root.getChildren().iterator().next();
 		assertTrue( s.contains( child ) );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		root = ( Node ) getOldToNewEntityRefMap().get( root );
 		child = ( Node ) getOldToNewEntityRefMap().get( child );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		assertInsertCount( 1 );
 		//assertUpdateCount( 1 ); //note: will fail here if no second-level cache
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		assertEquals(
 				Long.valueOf( 2 ),
 				s.createCriteria( Node.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult()
 		);
 		s.delete( root );
 		s.delete( child );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment"})
 	public void testEvictThenSaveOrUpdate() throws Exception {
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s = openSession();
 		Node parent = new Node( "1:parent" );
 		Node child = new Node( "2:child" );
 		Node grandchild = new Node( "3:grandchild" );
 		parent.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( parent );
 		s = applyNonFlushedChangesToNewSessionCloseOldSession( s );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s1 = openSession();
 		child = ( Node ) s1.load( Node.class, "2:child" );
 		s1 = applyNonFlushedChangesToNewSessionCloseOldSession( s1 );
 		child = ( Node ) getOldToNewEntityRefMap().get( child );
 		assertTrue( s1.contains( child ) );
 		assertFalse( Hibernate.isInitialized( child ) );
 		assertTrue( s1.contains( child.getParent() ) );
 		assertTrue( Hibernate.isInitialized( child ) );
 		assertFalse( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertTrue( s1.contains( child ) );
 		s1 = applyNonFlushedChangesToNewSessionCloseOldSession( s1 );
 		// child is an initialized proxy; after serialization, it is
 		// the proxy is replaced by its implementation
 		// TODO: find out if this is how this should work...
 		child = ( Node ) getOldToNewEntityRefMap().get(
 				( ( HibernateProxy ) child ).getHibernateLazyInitializer().getImplementation()
 		);
 		s1.evict( child );
 		assertFalse( s1.contains( child ) );
 		assertTrue( s1.contains( child.getParent() ) );
 
 		javax.transaction.Transaction tx1 = TestingJtaBootstrap.INSTANCE.getTransactionManager().suspend();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		Session s2 = openSession();
 		try {
 			s2.getTransaction().begin();
 			s2.saveOrUpdate( child );
 			fail();
 		}
 		catch ( HibernateException ex ) {
 			// expected because parent is connected to s1
 		}
 		finally {
 			TestingJtaBootstrap.INSTANCE.getTransactionManager().rollback();
 		}
 
 		s1.evict( child.getParent() );
 		assertFalse( s1.contains( child.getParent() ) );
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s2 = openSession();
  		s2.saveOrUpdate( child );
 		s2 = applyNonFlushedChangesToNewSessionCloseOldSession( s2 );
 		child = ( Node ) getOldToNewEntityRefMap().get( child );
 		assertTrue( s2.contains( child ) );
 		assertFalse( s1.contains( child ) );
 		assertTrue( s2.contains( child.getParent() ) );
 		assertFalse( s1.contains( child.getParent() ) );
 		assertFalse( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertEquals( 1, child.getChildren().size() );
 		assertEquals( "1:parent", child.getParent().getName() );
 		assertTrue( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertNull( child.getParent().getDescription() );
 		assertTrue( Hibernate.isInitialized( child.getParent() ) );
 		s1 = applyNonFlushedChangesToNewSessionCloseOldSession( s1 );
 		s2 = applyNonFlushedChangesToNewSessionCloseOldSession( s2 );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().resume( tx1 );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 //		tx1.commit();
 
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 		s = openSession();
 		s.delete( s.get( Node.class, "3:grandchild" ) );
 		s.delete( s.get( Node.class, "2:child" ) );
 		s.delete( s.get( Node.class, "1:parent" ) );
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java b/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java
index 62282e64da..5308973ba3 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/ops/SaveOrUpdateTest.java
@@ -1,503 +1,503 @@
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
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Projections;
-import org.hibernate.intercept.FieldInterceptionHelper;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class SaveOrUpdateTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public void configure(Configuration cfg) {
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" );
 	}
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {"ops/Node.hbm.xml"};
 	}
 
 	@Override
 	protected String getCacheConcurrencyStrategy() {
 		return "nonstrict-read-write";
 	}
 
 	@Test
 	public void testSaveOrUpdateDeepTree() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Node root = new Node( "root" );
 		Node child = new Node( "child" );
 		Node grandchild = new Node( "grandchild" );
 		root.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 3 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		grandchild.setDescription( "the grand child" );
 		Node grandchild2 = new Node( "grandchild2" );
 		child.addChild( grandchild2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 1 );
 		clearCounts();
 
 		Node child2 = new Node( "child2" );
 		Node grandchild3 = new Node( "grandchild3" );
 		child2.addChild( grandchild3 );
 		root.addChild( child2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.delete( grandchild );
 		s.delete( grandchild2 );
 		s.delete( grandchild3 );
 		s.delete( child );
 		s.delete( child2 );
 		s.delete( root );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateDeepTreeWithGeneratedId() {
 		boolean instrumented = FieldInterceptionHelper.isInstrumented( new NumberedNode() );
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		NumberedNode child = new NumberedNode( "child" );
 		NumberedNode grandchild = new NumberedNode( "grandchild" );
 		root.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 3 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		child = ( NumberedNode ) root.getChildren().iterator().next();
 		grandchild = ( NumberedNode ) child.getChildren().iterator().next();
 		grandchild.setDescription( "the grand child" );
 		NumberedNode grandchild2 = new NumberedNode( "grandchild2" );
 		child.addChild( grandchild2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( instrumented ? 1 : 3 );
 		clearCounts();
 
 		NumberedNode child2 = new NumberedNode( "child2" );
 		NumberedNode grandchild3 = new NumberedNode( "grandchild3" );
 		child2.addChild( grandchild3 );
 		root.addChild( child2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( instrumented ? 0 : 4 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete from NumberedNode where name like 'grand%'" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode where name like 'child%'" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode" ).executeUpdate();
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateTree() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Node root = new Node( "root" );
 		Node child = new Node( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		clearCounts();
 
 		root.setDescription( "The root node" );
 		child.setDescription( "The child node" );
 
 		Node secondChild = new Node( "second child" );
 
 		root.addChild( secondChild );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete from Node where parent is not null" ).executeUpdate();
 		s.createQuery( "delete from Node" ).executeUpdate();
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateTreeWithGeneratedId() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		clearCounts();
 
 		root.setDescription( "The root node" );
 		child.setDescription( "The child node" );
 
 		NumberedNode secondChild = new NumberedNode( "second child" );
 
 		root.addChild( secondChild );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 2 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.createQuery( "delete from NumberedNode where parent is not null" ).executeUpdate();
 		s.createQuery( "delete from NumberedNode" ).executeUpdate();
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateManaged() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		s.saveOrUpdate( root );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		assertFalse( s.contains( child ) );
 		s.flush();
 		assertTrue( s.contains( child ) );
 		tx.commit();
 
 		assertTrue( root.getChildren().contains( child ) );
 		assertEquals( root.getChildren().size(), 1 );
 
 		tx = s.beginTransaction();
 		assertEquals(
 				Long.valueOf( 2 ),
 				s.createCriteria( NumberedNode.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult()
 		);
 		s.delete( root );
 		s.delete( child );
 		tx.commit();
 		s.close();
 	}
 
 
 	@Test
 	public void testSaveOrUpdateGot() {
 		clearCounts();
 
 		boolean instrumented = FieldInterceptionHelper.isInstrumented( new NumberedNode() );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		NumberedNode root = new NumberedNode( "root" );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( instrumented ? 0 : 1 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		root = ( NumberedNode ) s.get( NumberedNode.class, new Long( root.getId() ) );
 		Hibernate.initialize( root.getChildren() );
 		tx.commit();
 		s.close();
 
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		NumberedNode child = new NumberedNode( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		assertTrue( s.contains( child ) );
 		tx.commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( instrumented ? 0 : 1 );
 
 		tx = s.beginTransaction();
 		assertEquals(
 				s.createCriteria( NumberedNode.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult(),
 		        new Long( 2 )
 		);
 		s.delete( root );
 		s.delete( child );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveOrUpdateGotWithMutableProp() {
 		clearCounts();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Node root = new Node( "root" );
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		s.saveOrUpdate( root );
 		tx.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( 0 );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		root = ( Node ) s.get( Node.class, "root" );
 		Hibernate.initialize( root.getChildren() );
 		tx.commit();
 		s.close();
 
 		clearCounts();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Node child = new Node( "child" );
 		root.addChild( child );
 		s.saveOrUpdate( root );
 		assertTrue( s.contains( child ) );
 		tx.commit();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 1 ); //note: will fail here if no second-level cache
 
 		tx = s.beginTransaction();
 		assertEquals(
 				s.createCriteria( Node.class )
 						.setProjection( Projections.rowCount() )
 						.uniqueResult(),
 		        new Long( 2 )
 		);
 		s.delete( root );
 		s.delete( child );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEvictThenSaveOrUpdate() {
 		Session s = openSession();
 		s.getTransaction().begin();
 		Node parent = new Node( "1:parent" );
 		Node child = new Node( "2:child" );
 		Node grandchild = new Node( "3:grandchild" );
 		parent.addChild( child );
 		child.addChild( grandchild );
 		s.saveOrUpdate( parent );
 		s.getTransaction().commit();
 		s.close();
 
 		Session s1 = openSession();
 		s1.getTransaction().begin();
 		child = ( Node ) s1.load( Node.class, "2:child" );
 		assertTrue( s1.contains( child ) );
 		assertFalse( Hibernate.isInitialized( child ) );
 		assertTrue( s1.contains( child.getParent() ) );
 		assertTrue( Hibernate.isInitialized( child ) );
 		assertFalse( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertTrue( s1.contains( child ) );
 		s1.evict( child );
 		assertFalse( s1.contains( child ) );
 		assertTrue( s1.contains( child.getParent() ) );
 
 		Session s2 = openSession();
 		try {
 			s2.getTransaction().begin();
 			s2.saveOrUpdate( child );
 			fail();
 		}
 		catch ( HibernateException ex ) {
 			// expected because parent is connected to s1
 		}
 		finally {
 			s2.getTransaction().rollback();
 		}
 		s2.close();
 
 		s1.evict( child.getParent() );
 		assertFalse( s1.contains( child.getParent() ) );
 
 		s2 = openSession();
 		s2.getTransaction().begin();
 		s2.saveOrUpdate( child );
 		assertTrue( s2.contains( child ) );
 		assertFalse( s1.contains( child ) );
 		assertTrue( s2.contains( child.getParent() ) );
 		assertFalse( s1.contains( child.getParent() ) );
 		assertFalse( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertEquals( 1, child.getChildren().size() );
 		assertEquals( "1:parent", child.getParent().getName() );
 		assertTrue( Hibernate.isInitialized( child.getChildren() ) );
 		assertFalse( Hibernate.isInitialized( child.getParent() ) );
 		assertNull( child.getParent().getDescription() );
 		assertTrue( Hibernate.isInitialized( child.getParent() ) );
 
 		s1.getTransaction().commit();
 		s2.getTransaction().commit();
 		s1.close();
 		s2.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( s.get( Node.class, "3:grandchild" ) );
 		s.delete( s.get( Node.class, "2:child" ) );
 		s.delete( s.get( Node.class, "1:parent" ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private void clearCounts() {
 		sessionFactory().getStatistics().clear();
 	}
 
 	private void assertInsertCount(int count) {
 		int inserts = ( int ) sessionFactory().getStatistics().getEntityInsertCount();
 		assertEquals( count, inserts );
 	}
 
 	private void assertUpdateCount(int count) {
 		int updates = ( int ) sessionFactory().getStatistics().getEntityUpdateCount();
 		assertEquals( count, updates );
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java b/hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java
index a69f22ad50..4012fecf5e 100644
--- a/hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/util/SerializationHelperTest.java
@@ -1,131 +1,131 @@
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
 package org.hibernate.util;
 import java.io.InputStream;
 import java.io.Serializable;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertSame;
 
 import org.hibernate.LockMode;
-import org.hibernate.bytecode.util.ByteCodeHelper;
+import org.hibernate.bytecode.spi.ByteCodeHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * This is basically a test to assert the expectations of {@link org.hibernate.type.SerializableType}
  * in regards to deserializing bytes from second level caches.
  *
  * @author Steve Ebersole
  */
 public class SerializationHelperTest extends BaseUnitTestCase {
 	private ClassLoader original;
 	private CustomClassLoader custom;
 
 	@Before
 	public void setUp() throws Exception {
 		original = Thread.currentThread().getContextClassLoader();
 		custom = new CustomClassLoader( original );
 		Thread.currentThread().setContextClassLoader( custom );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		Thread.currentThread().setContextClassLoader( original );
 	}
 
 	@Test
 	public void testSerializeDeserialize() throws Exception {
 		Class clazz = Thread.currentThread().getContextClassLoader().loadClass( "org.hibernate.util.SerializableThing" );
 		Object instance = clazz.newInstance();
 
 		// SerializableType.toBytes() logic, as called from SerializableType.disassemble()
 		byte[] bytes = SerializationHelper.serialize( (Serializable) instance );
 
 		// SerializableType.fromBytes() logic, as called from SerializableType.assemble
 		//		NOTE : specifically we use Serializable.class.getClassLoader for the CL in many cases
 		//			which are the problematic cases
 		Object instance2 = SerializationHelper.deserialize( bytes, Serializable.class.getClassLoader() );
 
 		assertEquals( instance.getClass(), instance2.getClass() );
 		assertEquals( instance.getClass().getClassLoader(), instance2.getClass().getClassLoader() );
 		assertEquals( custom, instance2.getClass().getClassLoader() );
 	}
 
 	public void testSerDeserClassUnknownToCustomLoader() throws Exception {
 		Object instance = LockMode.OPTIMISTIC;
 		assertSame( 
 			SerializationHelper.hibernateClassLoader(),
 			instance.getClass().getClassLoader() 
 		);
 
 		// SerializableType.toBytes() logic, as called from SerializableType.disassemble()
 		byte[] bytes = SerializationHelper.serialize( (Serializable) instance );
 
 		// SerializableType.fromBytes() logic, as called from SerializableType.assemble
 		// NOTE : specifically we use custom so that LockType.class is not found
 		//        until the 3rd loader (because loader1 == loader2, the custom classloader)
 		Object instance2 = SerializationHelper.deserialize( bytes, custom );
 
 		assertSame( instance.getClass(), instance2.getClass() );
 		assertSame( instance.getClass().getClassLoader(), instance2.getClass().getClassLoader() );
 	}
 
 
 	public static class CustomClassLoader extends ClassLoader {
 		public CustomClassLoader(ClassLoader parent) {
 			super( parent );
 		}
 
 		public Class loadClass(String name) throws ClassNotFoundException {
 			if ( name.equals( "org.hibernate.LockMode" ) ) {
 				throw new ClassNotFoundException( "Could not find "+ name );
 			}
 			if ( ! name.equals( "org.hibernate.util.SerializableThing" ) ) {
 				return getParent().loadClass( name );
 			}
 
 			Class c = findLoadedClass( name );
 			if ( c != null ) {
 				return c;
 			}
 
 			InputStream is = this.getResourceAsStream( name.replace( '.', '/' ) + ".class" );
 			if ( is == null ) {
 				throw new ClassNotFoundException( name + " not found" );
 			}
 
 			try {
 				byte[] bytecode = ByteCodeHelper.readByteCode( is );
 				return defineClass( name, bytecode, 0, bytecode.length );
 			}
 			catch( Throwable t ) {
 				throw new ClassNotFoundException( name + " not found", t );
 			}
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/instrument/InterceptFieldClassFileTransformer.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/instrument/InterceptFieldClassFileTransformer.java
index 0a847cfa58..28f5f01720 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/instrument/InterceptFieldClassFileTransformer.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/instrument/InterceptFieldClassFileTransformer.java
@@ -1,80 +1,82 @@
 /*
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
  */
 package org.hibernate.ejb.instrument;
 import java.lang.instrument.IllegalClassFormatException;
 import java.security.ProtectionDomain;
 import java.util.ArrayList;
 import java.util.List;
-import org.hibernate.bytecode.util.ClassFilter;
-import org.hibernate.bytecode.util.FieldFilter;
+
+import org.hibernate.bytecode.buildtime.spi.ClassFilter;
+import org.hibernate.bytecode.buildtime.spi.FieldFilter;
+import org.hibernate.bytecode.spi.ClassTransformer;
 import org.hibernate.cfg.Environment;
 
 /**
  * Enhance the classes allowing them to implements InterceptFieldEnabled
  * This interface is then used by Hibernate for some optimizations.
  *
  * @author Emmanuel Bernard
  */
 public class InterceptFieldClassFileTransformer implements javax.persistence.spi.ClassTransformer {
-	private org.hibernate.bytecode.ClassTransformer classTransformer;
+	private ClassTransformer classTransformer;
 
 	public InterceptFieldClassFileTransformer(List<String> entities) {
 		final List<String> copyEntities = new ArrayList<String>( entities.size() );
 		copyEntities.addAll( entities );
 		classTransformer = Environment.getBytecodeProvider().getTransformer(
 				//TODO change it to a static class to make it faster?
 				new ClassFilter() {
 					public boolean shouldInstrumentClass(String className) {
 						return copyEntities.contains( className );
 					}
 				},
 				//TODO change it to a static class to make it faster?
 				new FieldFilter() {
 
 					public boolean shouldInstrumentField(String className, String fieldName) {
 						return true;
 					}
 
 					public boolean shouldTransformFieldAccess(
 							String transformingClassName, String fieldOwnerClassName, String fieldName
 					) {
 						return true;
 					}
 				}
 		);
 	}
 
 	public byte[] transform(
 			ClassLoader loader,
 			String className,
 			Class<?> classBeingRedefined,
 			ProtectionDomain protectionDomain,
 			byte[] classfileBuffer ) throws IllegalClassFormatException {
 		try {
 			return classTransformer.transform( loader, className, classBeingRedefined,
 					protectionDomain, classfileBuffer );
 		}
 		catch (Exception e) {
 			throw new IllegalClassFormatException( e.getMessage() );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java
index f132589c49..2c7fa86d0d 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java
@@ -1,236 +1,236 @@
 package org.hibernate.ejb.util;
 import java.io.Serializable;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.WeakHashMap;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.LoadState;
 import org.hibernate.AssertionFailure;
 import org.hibernate.collection.PersistentCollection;
-import org.hibernate.intercept.FieldInterceptionHelper;
-import org.hibernate.intercept.FieldInterceptor;
+import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 public class PersistenceUtilHelper {
 	public static LoadState isLoadedWithoutReference(Object proxy, String property, MetadataCache cache) {
 		Object entity;
 		boolean sureFromUs = false;
 		if ( proxy instanceof HibernateProxy ) {
 			LazyInitializer li = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				return LoadState.NOT_LOADED;
 			}
 			else {
 				entity = li.getImplementation();
 			}
 			sureFromUs = true;
 		}
 		else {
 			entity = proxy;
 		}
 
 		//we are instrumenting but we can't assume we are the only ones
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			final boolean isInitialized = interceptor == null || interceptor.isInitialized( property );
 			LoadState state;
 			if (isInitialized && interceptor != null) {
 				//property is loaded according to bytecode enhancement, but is it loaded as far as association?
 				//it's ours, we can read
 				state = isLoaded( get( entity, property, cache ) );
 				//it's ours so we know it's loaded
 				if (state == LoadState.UNKNOWN) state = LoadState.LOADED;
 			}
 			else if ( interceptor != null && (! isInitialized)) {
 				state = LoadState.NOT_LOADED;
 			}
 			else if ( sureFromUs ) { //interceptor == null
 				//property is loaded according to bytecode enhancement, but is it loaded as far as association?
 				//it's ours, we can read
 				state = isLoaded( get( entity, property, cache ) );
 				//it's ours so we know it's loaded
 				if (state == LoadState.UNKNOWN) state = LoadState.LOADED;
 			}
 			else {
 				state = LoadState.UNKNOWN;
 			}
 
 			return state;
 		}
 		else {
 			//can't do sureFromUs ? LoadState.LOADED : LoadState.UNKNOWN;
 			//is that an association?
 			return LoadState.UNKNOWN;
 		}
 	}
 
 	public static LoadState isLoadedWithReference(Object proxy, String property, MetadataCache cache) {
 		//for sure we don't instrument and for sure it's not a lazy proxy
 		Object object = get(proxy, property, cache);
 		return isLoaded( object );
 	}
 
 	private static Object get(Object proxy, String property, MetadataCache cache) {
 		final Class<?> clazz = proxy.getClass();
 
 		try {
 			Member member = cache.getMember( clazz, property );
 			if (member instanceof Field) {
 				return ( (Field) member ).get( proxy );
 			}
 			else if (member instanceof Method) {
 				return ( (Method) member ).invoke( proxy );
 			}
 			else {
 				throw new AssertionFailure( "Member object neither Field nor Method: " + member);
 			}
 		}
 		catch ( IllegalAccessException e ) {
 			throw new PersistenceException( "Unable to access field or method: "
 							+ clazz + "#"
 							+ property, e);
 		}
 		catch ( InvocationTargetException e ) {
 			throw new PersistenceException( "Unable to access field or method: "
 							+ clazz + "#"
 							+ property, e);
 		}
 	}
 
 	private static void setAccessibility(Member member) {
 		//Sun's ease of use, sigh...
 		( ( AccessibleObject ) member ).setAccessible( true );
 	}
 
 	public static LoadState isLoaded(Object o) {
 		if ( o instanceof HibernateProxy ) {
 			final boolean isInitialized = !( ( HibernateProxy ) o ).getHibernateLazyInitializer().isUninitialized();
 			return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
 		}
 		else if ( o instanceof PersistentCollection ) {
 			final boolean isInitialized = ( ( PersistentCollection ) o ).wasInitialized();
 			return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
 		}
 		else {
 			return LoadState.UNKNOWN;
 		}
 	}
 
 	/**
 	 * Returns the method with the specified name or <code>null</code> if it does not exist.
 	 *
 	 * @param clazz The class to check.
 	 * @param methodName The method name.
 	 *
 	 * @return Returns the method with the specified name or <code>null</code> if it does not exist.
 	 */
 	private static Method getMethod(Class<?> clazz, String methodName) {
 		try {
 			char string[] = methodName.toCharArray();
 			string[0] = Character.toUpperCase( string[0] );
 			methodName = new String( string );
 			try {
 				return clazz.getDeclaredMethod( "get" + methodName );
 			}
 			catch ( NoSuchMethodException e ) {
 				return clazz.getDeclaredMethod( "is" + methodName );
 			}
 		}
 		catch ( NoSuchMethodException e ) {
 			return null;
 		}
 	}
 
 	/**
 	 * Cache hierarchy and member resolution in a weak hash map
 	 */
 	//TODO not really thread-safe
 	public static class MetadataCache implements Serializable {
 		private transient Map<Class<?>, ClassCache> classCache = new WeakHashMap<Class<?>, ClassCache>();
 
 
 		private void readObject(java.io.ObjectInputStream stream) {
 			classCache = new WeakHashMap<Class<?>, ClassCache>();
 		}
 
 		Member getMember(Class<?> clazz, String property) {
 			ClassCache cache = classCache.get( clazz );
 			if (cache == null) {
 				cache = new ClassCache(clazz);
 				classCache.put( clazz, cache );
 			}
 			Member member = cache.members.get( property );
 			if ( member == null ) {
 				member = findMember( clazz, property );
 				cache.members.put( property, member );
 			}
 			return member;
 		}
 
 		private Member findMember(Class<?> clazz, String property) {
 			final List<Class<?>> classes = getClassHierarchy( clazz );
 
 			for (Class current : classes) {
 				final Field field;
 				try {
 					field = current.getDeclaredField( property );
 					setAccessibility( field );
 					return field;
 				}
 				catch ( NoSuchFieldException e ) {
 					final Method method = getMethod( current, property );
 					if (method != null) {
 						setAccessibility( method );
 						return method;
 					}
 				}
 			}
 			//we could not find any match
 			throw new PersistenceException( "Unable to find field or method: "
 							+ clazz + "#"
 							+ property);
 		}
 
 		private List<Class<?>> getClassHierarchy(Class<?> clazz) {
 			ClassCache cache = classCache.get( clazz );
 			if (cache == null) {
 				cache = new ClassCache(clazz);
 				classCache.put( clazz, cache );
 			}
 			return cache.classHierarchy;
 		}
 
 		private static List<Class<?>> findClassHierarchy(Class<?> clazz) {
 			List<Class<?>> classes = new ArrayList<Class<?>>();
 			Class<?> current = clazz;
 			do {
 				classes.add( current );
 				current = current.getSuperclass();
 			}
 			while ( current != null );
 			return classes;
 		}
 
 		private static class ClassCache {
 			List<Class<?>> classHierarchy;
 			Map<String, Member> members = new HashMap<String, Member>();
 
 			public ClassCache(Class<?> clazz) {
 				classHierarchy = findClassHierarchy( clazz );
 			}
 		}
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/instrument/InterceptFieldClassFileTransformerTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/instrument/InterceptFieldClassFileTransformerTest.java
index 780fa47336..161912405f 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/instrument/InterceptFieldClassFileTransformerTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/instrument/InterceptFieldClassFileTransformerTest.java
@@ -1,40 +1,40 @@
 //$Id$
 package org.hibernate.ejb.test.instrument;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.List;
 import junit.framework.TestCase;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 public class InterceptFieldClassFileTransformerTest extends TestCase {
 	/**
 	 * Tests that class file enhancement works.
 	 * 
 	 * @throws Exception in case the test fails.
 	 */
 	public void testEnhancement() throws Exception {
 		List<String> entities = new ArrayList<String>();
 		entities.add( "org.hibernate.ejb.test.instrument.Simple" );
 
 		// sanity check that the class is unmodified and does not contain getFieldHandler()
 		try {
 			org.hibernate.ejb.test.instrument.Simple.class.getDeclaredMethod( "getFieldHandler" );
 			fail();
 		} catch ( NoSuchMethodException nsme ) {
 			// success
 		}
 
 		// use custom class loader which enhances the class
 		InstrumentedClassLoader cl = new InstrumentedClassLoader( Thread.currentThread().getContextClassLoader() );
 		cl.setEntities( entities );
 		Class clazz = cl.loadClass( entities.get( 0 ) );
 		
 		// javassist is our default byte code enhancer. Enhancing will eg add the method getFieldHandler()
-		// see org.hibernate.bytecode.javassist.FieldTransformer
+		// see org.hibernate.bytecode.internal.javassist.FieldTransformer
 		Method method = clazz.getDeclaredMethod( "getFieldHandler" );
 		assertNotNull( method );
 	}
 }
