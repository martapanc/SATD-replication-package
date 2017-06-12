diff --git a/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java b/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
index 49a8c3f30c..12dc31e2ae 100644
--- a/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
+++ b/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
@@ -1,262 +1,262 @@
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
 package org.hibernate.service.jdbc.connections.internal;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import javax.sql.DataSource;
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.UnknownUnwrapTypeException;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.Stoppable;
 
 import org.jboss.logging.Logger;
 import com.mchange.v2.c3p0.DataSources;
 
 /**
  * A connection provider that uses a C3P0 connection pool. Hibernate will use this by
  * default if the <tt>hibernate.c3p0.*</tt> properties are set.
  *
  * @author various people
  * @see ConnectionProvider
  */
 public class C3P0ConnectionProvider implements ConnectionProvider, Configurable, Stoppable {
 
     private static final C3P0MessageLogger LOG = Logger.getMessageLogger(C3P0MessageLogger.class, C3P0ConnectionProvider.class.getName());
 
 	//swaldman 2006-08-28: define c3p0-style configuration parameters for properties with
 	//                     hibernate-specific overrides to detect and warn about conflicting
 	//                     declarations
 	private final static String C3P0_STYLE_MIN_POOL_SIZE = "c3p0.minPoolSize";
 	private final static String C3P0_STYLE_MAX_POOL_SIZE = "c3p0.maxPoolSize";
 	private final static String C3P0_STYLE_MAX_IDLE_TIME = "c3p0.maxIdleTime";
 	private final static String C3P0_STYLE_MAX_STATEMENTS = "c3p0.maxStatements";
 	private final static String C3P0_STYLE_ACQUIRE_INCREMENT = "c3p0.acquireIncrement";
 	private final static String C3P0_STYLE_IDLE_CONNECTION_TEST_PERIOD = "c3p0.idleConnectionTestPeriod";
 
 	//swaldman 2006-08-28: define c3p0-style configuration parameters for initialPoolSize, which
 	//                     hibernate sensibly lets default to minPoolSize, but we'll let users
 	//                     override it with the c3p0-style property if they want.
 	private final static String C3P0_STYLE_INITIAL_POOL_SIZE = "c3p0.initialPoolSize";
 
 	private DataSource ds;
 	private Integer isolation;
 	private boolean autocommit;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection getConnection() throws SQLException {
 		final Connection c = ds.getConnection();
 		if ( isolation != null ) {
 			c.setTransactionIsolation( isolation.intValue() );
 		}
 		if ( c.getAutoCommit() != autocommit ) {
 			c.setAutoCommit( autocommit );
 		}
 		return c;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void closeConnection(Connection conn) throws SQLException {
 		conn.close();
 	}
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return ConnectionProvider.class.equals( unwrapType ) ||
 				C3P0ConnectionProvider.class.isAssignableFrom( unwrapType ) ||
 				DataSource.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( ConnectionProvider.class.equals( unwrapType ) ||
 				C3P0ConnectionProvider.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else if ( DataSource.class.isAssignableFrom( unwrapType ) ) {
 			return (T) ds;
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
     @SuppressWarnings( {"unchecked"})
 	public void configure(Map props) {
 		String jdbcDriverClass = (String) props.get( Environment.DRIVER );
 		String jdbcUrl = (String) props.get( Environment.URL );
 		Properties connectionProps = ConnectionProviderInitiator.getConnectionProperties( props );
 
         LOG.c3p0UsingDriver(jdbcDriverClass, jdbcUrl);
         LOG.connectionProperties(ConfigurationHelper.maskOut(connectionProps, "password"));
 
 		autocommit = ConfigurationHelper.getBoolean( Environment.AUTOCOMMIT, props );
         LOG.autoCommitMode( autocommit );
 
         if (jdbcDriverClass == null) LOG.jdbcDriverNotSpecified(Environment.DRIVER);
 		else {
 			try {
 				Class.forName( jdbcDriverClass );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				try {
 					ReflectHelper.classForName( jdbcDriverClass );
 				}
 				catch ( ClassNotFoundException e ) {
                     String msg = LOG.jdbcDriverNotFound(jdbcDriverClass);
                     LOG.error(msg, e);
 					throw new HibernateException( msg, e );
 				}
 			}
 		}
 
 		try {
 
 			//swaldman 2004-02-07: modify to allow null values to signify fall through to c3p0 PoolConfig defaults
 			Integer minPoolSize = ConfigurationHelper.getInteger( Environment.C3P0_MIN_SIZE, props );
 			Integer maxPoolSize = ConfigurationHelper.getInteger( Environment.C3P0_MAX_SIZE, props );
 			Integer maxIdleTime = ConfigurationHelper.getInteger( Environment.C3P0_TIMEOUT, props );
 			Integer maxStatements = ConfigurationHelper.getInteger( Environment.C3P0_MAX_STATEMENTS, props );
 			Integer acquireIncrement = ConfigurationHelper.getInteger( Environment.C3P0_ACQUIRE_INCREMENT, props );
 			Integer idleTestPeriod = ConfigurationHelper.getInteger( Environment.C3P0_IDLE_TEST_PERIOD, props );
 
 			Properties c3props = new Properties();
 
 			// turn hibernate.c3p0.* into c3p0.*, so c3p0
 			// gets a chance to see all hibernate.c3p0.*
 			for ( Iterator ii = props.keySet().iterator(); ii.hasNext(); ) {
 				String key = ( String ) ii.next();
 				if ( key.startsWith( "hibernate.c3p0." ) ) {
 					String newKey = key.substring( 15 );
 					if ( props.containsKey( newKey ) ) {
 						warnPropertyConflict( key, newKey );
 					}
 					c3props.put( newKey, props.get( key ) );
 				}
 			}
 
 			setOverwriteProperty( Environment.C3P0_MIN_SIZE, C3P0_STYLE_MIN_POOL_SIZE, props, c3props, minPoolSize );
 			setOverwriteProperty( Environment.C3P0_MAX_SIZE, C3P0_STYLE_MAX_POOL_SIZE, props, c3props, maxPoolSize );
 			setOverwriteProperty( Environment.C3P0_TIMEOUT, C3P0_STYLE_MAX_IDLE_TIME, props, c3props, maxIdleTime );
 			setOverwriteProperty(
 					Environment.C3P0_MAX_STATEMENTS, C3P0_STYLE_MAX_STATEMENTS, props, c3props, maxStatements
 			);
 			setOverwriteProperty(
 					Environment.C3P0_ACQUIRE_INCREMENT, C3P0_STYLE_ACQUIRE_INCREMENT, props, c3props, acquireIncrement
 			);
 			setOverwriteProperty(
 					Environment.C3P0_IDLE_TEST_PERIOD, C3P0_STYLE_IDLE_CONNECTION_TEST_PERIOD, props, c3props, idleTestPeriod
 			);
 
 			// revert to traditional hibernate behavior of setting initialPoolSize to minPoolSize
 			// unless otherwise specified with a c3p0.*-style parameter.
 			Integer initialPoolSize = ConfigurationHelper.getInteger( C3P0_STYLE_INITIAL_POOL_SIZE, props );
 			if ( initialPoolSize == null && minPoolSize != null ) {
 				c3props.put( C3P0_STYLE_INITIAL_POOL_SIZE, String.valueOf( minPoolSize ).trim() );
 			}
 
 			/*DataSource unpooled = DataSources.unpooledDataSource(
 				jdbcUrl, props.getProperty(Environment.USER), props.getProperty(Environment.PASS)
 			);*/
 			DataSource unpooled = DataSources.unpooledDataSource( jdbcUrl, connectionProps );
 
 			Map allProps = new HashMap();
 			allProps.putAll( props );
 			allProps.putAll( c3props );
 
 			ds = DataSources.pooledDataSource( unpooled, allProps );
 		}
 		catch ( Exception e ) {
             LOG.error(LOG.unableToInstantiateC3p0ConnectionPool(), e);
             throw new HibernateException(LOG.unableToInstantiateC3p0ConnectionPool(), e);
 		}
 
 		String i = (String) props.get( Environment.ISOLATION );
         if (i == null) isolation = null;
 		else {
-			isolation = new Integer( i );
-            LOG.jdbcIsolationLevel(Environment.isolationLevelToString(isolation.intValue()));
+			isolation = Integer.valueOf( i );
+            LOG.jdbcIsolationLevel(Environment.isolationLevelToString(isolation));
 		}
 
 	}
 
     /**
 	 *
 	 */
 	public void close() {
 		try {
 			DataSources.destroy( ds );
 		}
 		catch ( SQLException sqle ) {
             LOG.unableToDestroyC3p0ConnectionPool(sqle);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean supportsAggressiveRelease() {
 		return false;
 	}
 
 	private void setOverwriteProperty(String hibernateStyleKey, String c3p0StyleKey, Map hibp, Properties c3p, Integer value) {
 		if ( value != null ) {
             String peeledC3p0Key = c3p0StyleKey.substring(5);
 			c3p.put( peeledC3p0Key, String.valueOf( value ).trim() );
 			if ( hibp.containsKey( c3p0StyleKey )  ) {
 				warnPropertyConflict( hibernateStyleKey, c3p0StyleKey );
 			}
 			String longC3p0StyleKey = "hibernate." + c3p0StyleKey;
 			if ( hibp.containsKey( longC3p0StyleKey ) ) {
 				warnPropertyConflict( hibernateStyleKey, longC3p0StyleKey );
 			}
 		}
 	}
 
 	private void warnPropertyConflict(String hibernateStyle, String c3p0Style) {
         LOG.bothHibernateAndC3p0StylesSet(hibernateStyle, c3p0Style, hibernateStyle, c3p0Style);
 	}
 
 	@Override
 	public void stop() {
 		close();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
index 8407b62715..c593a53e75 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/instrumentation/internal/javassist/FieldInterceptorImpl.java
@@ -1,170 +1,170 @@
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
 package org.hibernate.bytecode.instrumentation.internal.javassist;
 
 import java.io.Serializable;
 import java.util.Set;
 
 import org.hibernate.bytecode.instrumentation.spi.AbstractFieldInterceptor;
 import org.hibernate.bytecode.internal.javassist.FieldHandler;
 import org.hibernate.engine.spi.SessionImplementor;
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
 @SuppressWarnings( {"UnnecessaryUnboxing", "UnnecessaryBoxing"})
 public final class FieldInterceptorImpl extends AbstractFieldInterceptor implements FieldHandler, Serializable {
 
 	FieldInterceptorImpl(SessionImplementor session, Set uninitializedFields, String entityName) {
 		super( session, uninitializedFields, entityName );
 	}
 
 
 	// FieldHandler impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean readBoolean(Object target, String name, boolean oldValue) {
-		return ( ( Boolean ) intercept( target, name, oldValue  ? Boolean.TRUE : Boolean.FALSE ) )
+		return ( ( Boolean ) intercept( target, name, oldValue ) )
 				.booleanValue();
 	}
 
 	public byte readByte(Object target, String name, byte oldValue) {
 		return ( ( Byte ) intercept( target, name, Byte.valueOf( oldValue ) ) ).byteValue();
 	}
 
 	public char readChar(Object target, String name, char oldValue) {
 		return ( ( Character ) intercept( target, name, Character.valueOf( oldValue ) ) )
 				.charValue();
 	}
 
 	public double readDouble(Object target, String name, double oldValue) {
 		return ( ( Double ) intercept( target, name, Double.valueOf( oldValue ) ) )
 				.doubleValue();
 	}
 
 	public float readFloat(Object target, String name, float oldValue) {
 		return ( ( Float ) intercept( target, name, Float.valueOf( oldValue ) ) )
 				.floatValue();
 	}
 
 	public int readInt(Object target, String name, int oldValue) {
 		return ( ( Integer ) intercept( target, name, Integer.valueOf( oldValue ) ) )
 				.intValue();
 	}
 
 	public long readLong(Object target, String name, long oldValue) {
 		return ( ( Long ) intercept( target, name, Long.valueOf( oldValue ) ) ).longValue();
 	}
 
 	public short readShort(Object target, String name, short oldValue) {
 		return ( ( Short ) intercept( target, name, Short.valueOf( oldValue ) ) )
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
-		intercept( target, name, oldValue ? Boolean.TRUE : Boolean.FALSE );
+		intercept( target, name, oldValue );
 		return newValue;
 	}
 
 	public byte writeByte(Object target, String name, byte oldValue, byte newValue) {
 		dirty();
 		intercept( target, name, Byte.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public char writeChar(Object target, String name, char oldValue, char newValue) {
 		dirty();
 		intercept( target, name, Character.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public double writeDouble(Object target, String name, double oldValue, double newValue) {
 		dirty();
 		intercept( target, name, Double.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public float writeFloat(Object target, String name, float oldValue, float newValue) {
 		dirty();
 		intercept( target, name, Float.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public int writeInt(Object target, String name, int oldValue, int newValue) {
 		dirty();
 		intercept( target, name, Integer.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public long writeLong(Object target, String name, long oldValue, long newValue) {
 		dirty();
 		intercept( target, name, Long.valueOf( oldValue ) );
 		return newValue;
 	}
 
 	public short writeShort(Object target, String name, short oldValue, short newValue) {
 		dirty();
 		intercept( target, name, Short.valueOf( oldValue ) );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
index 5fce7a5887..73db828ef9 100644
--- a/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/ProxyFactoryFactoryImpl.java
@@ -1,147 +1,147 @@
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
 package org.hibernate.bytecode.internal.javassist;
 
 import java.lang.reflect.Method;
 import java.util.HashMap;
 
 import javassist.util.proxy.MethodFilter;
 import javassist.util.proxy.MethodHandler;
 import javassist.util.proxy.ProxyObject;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.bytecode.spi.BasicProxyFactory;
 import org.hibernate.bytecode.spi.ProxyFactoryFactory;
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
-				return proxiedObject == object ? Boolean.TRUE : Boolean.FALSE;
+				return proxiedObject == object;
 			}
 			else if ( "hashCode".equals( name ) ) {
-				return new Integer( System.identityHashCode( object ) );
+				return System.identityHashCode( object );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
index a36089f41a..c80e8853ba 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
@@ -1,245 +1,245 @@
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
 package org.hibernate.cache.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 import java.util.Set;
 import javax.persistence.EntityNotFoundException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * The standard implementation of the Hibernate QueryCache interface.  This
  * implementation is very good at recognizing stale query results and
  * and re-running queries when it detects this condition, recaching the new
  * results.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class StandardQueryCache implements QueryCache {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, StandardQueryCache.class.getName());
 
 	private QueryResultsRegion cacheRegion;
 	private UpdateTimestampsCache updateTimestampsCache;
 
 	public void clear() throws CacheException {
 		cacheRegion.evictAll();
 	}
 
 	public StandardQueryCache(
 			final Settings settings,
 			final Properties props,
 			final UpdateTimestampsCache updateTimestampsCache,
 			String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			regionName = StandardQueryCache.class.getName();
 		}
 		String prefix = settings.getCacheRegionPrefix();
 		if ( prefix != null ) {
 			regionName = prefix + '.' + regionName;
 		}
 		LOG.startingQueryCache( regionName );
 
 		this.cacheRegion = settings.getRegionFactory().buildQueryResultsRegion( regionName, props );
 		this.updateTimestampsCache = updateTimestampsCache;
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing", "unchecked" })
 	public boolean put(
 			QueryKey key,
 			Type[] returnTypes,
 			List result,
 			boolean isNaturalKeyLookup,
 			SessionImplementor session) throws HibernateException {
         if (isNaturalKeyLookup && result.size() == 0) return false;
-        Long ts = new Long(session.getFactory().getSettings().getRegionFactory().nextTimestamp());
+        Long ts = session.getFactory().getSettings().getRegionFactory().nextTimestamp();
 
 		LOG.debugf( "Caching query results in region: %s; timestamp=%s", cacheRegion.getName(), ts );
 
 		List cacheable = new ArrayList(result.size() + 1);
         logCachedResultDetails(key, null, returnTypes, cacheable);
         cacheable.add(ts);
         for (Object aResult : result) {
             if (returnTypes.length == 1) cacheable.add(returnTypes[0].disassemble(aResult, session, null));
             else cacheable.add(TypeHelper.disassemble((Object[])aResult, returnTypes, null, session, null));
             logCachedResultRowDetails(returnTypes, aResult);
         }
 
 		cacheRegion.put(key, cacheable);
         return true;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public List get(
 			QueryKey key,
 			Type[] returnTypes,
 			boolean isNaturalKeyLookup,
 			Set spaces,
 			SessionImplementor session) throws HibernateException {
 		LOG.debugf( "Checking cached query results in region: %s", cacheRegion.getName() );
 
 		List cacheable = ( List ) cacheRegion.get( key );
 		logCachedResultDetails( key, spaces, returnTypes, cacheable );
 
 		if ( cacheable == null ) {
 			LOG.debugf( "Query results were not found in cache" );
 			return null;
 		}
 
 		Long timestamp = ( Long ) cacheable.get( 0 );
 		if ( !isNaturalKeyLookup && !isUpToDate( spaces, timestamp ) ) {
 			LOG.debugf( "Cached query results were not up-to-date" );
 			return null;
 		}
 
 		LOG.debugf( "Returning cached query results" );
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			if ( returnTypes.length == 1 ) {
 				returnTypes[0].beforeAssemble( ( Serializable ) cacheable.get( i ), session );
 			}
 			else {
 				TypeHelper.beforeAssemble( ( Serializable[] ) cacheable.get( i ), returnTypes, session );
 			}
 		}
 		List result = new ArrayList( cacheable.size() - 1 );
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			try {
 				if ( returnTypes.length == 1 ) {
 					result.add( returnTypes[0].assemble( ( Serializable ) cacheable.get( i ), session, null ) );
 				}
 				else {
 					result.add(
 							TypeHelper.assemble( ( Serializable[] ) cacheable.get( i ), returnTypes, session, null )
 					);
 				}
 				logCachedResultRowDetails( returnTypes, result.get( i - 1 ) );
 			}
 			catch ( RuntimeException ex ) {
 				if ( isNaturalKeyLookup &&
 						( UnresolvableObjectException.class.isInstance( ex ) ||
 						EntityNotFoundException.class.isInstance( ex ) ) ) {
 					//TODO: not really completely correct, since
 					//      the uoe could occur while resolving
 					//      associations, leaving the PC in an
 					//      inconsistent state
 					LOG.debugf( "Unable to reassemble cached result set" );
 					cacheRegion.evict( key );
 					return null;
 				}
 				throw ex;
 			}
 		}
 		return result;
 	}
 
 	protected boolean isUpToDate(Set spaces, Long timestamp) {
 		LOG.debugf( "Checking query spaces are up-to-date: %s", spaces );
 		return updateTimestampsCache.isUpToDate( spaces, timestamp );
 	}
 
 	public void destroy() {
 		try {
 			cacheRegion.destroy();
 		}
 		catch ( Exception e ) {
 			LOG.unableToDestroyQueryCache( cacheRegion.getName(), e.getMessage() );
 		}
 	}
 
 	public QueryResultsRegion getRegion() {
 		return cacheRegion;
 	}
 
 	@Override
     public String toString() {
 		return "StandardQueryCache(" + cacheRegion.getName() + ')';
 	}
 
 	private static void logCachedResultDetails(QueryKey key, Set querySpaces, Type[] returnTypes, List result) {
         if (!LOG.isTraceEnabled()) return;
         LOG.trace("key.hashCode=" + key.hashCode());
         LOG.trace("querySpaces=" + querySpaces);
         if (returnTypes == null || returnTypes.length == 0) LOG.trace("Unexpected returnTypes is "
                                                                       + (returnTypes == null ? "null" : "empty") + "! result"
                                                                       + (result == null ? " is null" : ".size()=" + result.size()));
 		else {
 			StringBuffer returnTypeInfo = new StringBuffer();
 			for ( int i=0; i<returnTypes.length; i++ ) {
 				returnTypeInfo.append( "typename=" )
 						.append( returnTypes[ i ].getName() )
 						.append(" class=" )
 						.append( returnTypes[ i ].getReturnedClass().getName() ).append(' ');
 			}
             LOG.trace("unexpected returnTypes is " + returnTypeInfo.toString() + "! result");
 		}
 	}
 
 	private static void logCachedResultRowDetails(Type[] returnTypes, Object result) {
 		if ( !LOG.isTraceEnabled() ) return;
 		logCachedResultRowDetails(
 				returnTypes,
 				( result instanceof Object[] ? ( Object[] ) result : new Object[] { result } )
 		);
 	}
 
 	private static void logCachedResultRowDetails(Type[] returnTypes, Object[] tuple) {
         if (!LOG.isTraceEnabled()) return;
 		if ( tuple == null ) {
             LOG.trace(" tuple is null; returnTypes is " + returnTypes == null ? "null" : "Type[" + returnTypes.length + "]");
             if (returnTypes != null && returnTypes.length > 1) LOG.trace("Unexpected result tuple! tuple is null; should be Object["
                                                                          + returnTypes.length + "]!");
 		}
 		else {
             if (returnTypes == null || returnTypes.length == 0) LOG.trace("Unexpected result tuple! tuple is null; returnTypes is "
                                                                           + (returnTypes == null ? "null" : "empty"));
             LOG.trace(" tuple is Object[" + tuple.length + "]; returnTypes is Type[" + returnTypes.length + "]");
             if (tuple.length != returnTypes.length) LOG.trace("Unexpected tuple length! transformer= expected="
                                                               + returnTypes.length + " got=" + tuple.length);
             else for (int j = 0; j < tuple.length; j++) {
                 if (tuple[j] != null && !returnTypes[j].getReturnedClass().isInstance(tuple[j])) LOG.trace("Unexpected tuple value type! transformer= expected="
                                                                                                            + returnTypes[j].getReturnedClass().getName()
                                                                                                            + " got="
                                                                                                            + tuple[j].getClass().getName());
             }
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
index c8d21999e4..6b47692f6b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
@@ -1,172 +1,172 @@
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
 package org.hibernate.cache.spi;
 
 import java.io.Serializable;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Tracks the timestamps of the most recent updates to particular tables. It is
  * important that the cache timeout of the underlying cache implementation be set
  * to a higher value than the timeouts of any of the query caches. In fact, we
  * recommend that the the underlying cache not be configured for expiry at all.
  * Note, in particular, that an LRU cache expiry policy is never appropriate.
  *
  * @author Gavin King
  * @author Mikheil Kapanadze
  */
 public class UpdateTimestampsCache {
 
 	public static final String REGION_NAME = UpdateTimestampsCache.class.getName();
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, UpdateTimestampsCache.class.getName() );
 
 	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
 	private final TimestampsRegion region;
 	private final SessionFactoryImplementor factory;
 
 	public UpdateTimestampsCache(Settings settings, Properties props, final SessionFactoryImplementor factory) throws HibernateException {
 		this.factory = factory;
 		String prefix = settings.getCacheRegionPrefix();
 		String regionName = prefix == null ? REGION_NAME : prefix + '.' + REGION_NAME;
 		LOG.startingUpdateTimestampsCache( regionName );
 		this.region = settings.getRegionFactory().buildTimestampsRegion( regionName, props );
 	}
     @SuppressWarnings({"UnusedDeclaration"})
     public UpdateTimestampsCache(Settings settings, Properties props)
             throws HibernateException {
         this(settings, props, null);
     }
 
 	@SuppressWarnings({"UnnecessaryBoxing"})
 	public void preinvalidate(Serializable[] spaces) throws CacheException {
 		readWriteLock.writeLock().lock();
 
 		try {
-			Long ts = new Long( region.nextTimestamp() + region.getTimeout() );
+			Long ts = region.nextTimestamp() + region.getTimeout();
 			for ( Serializable space : spaces ) {
 				LOG.debugf( "Pre-invalidating space [%s]", space );
 				//put() has nowait semantics, is this really appropriate?
 				//note that it needs to be async replication, never local or sync
 				region.put( space, ts );
 				if ( factory != null && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().updateTimestampsCachePut();
 				}
 			}
 		}
 		finally {
 			readWriteLock.writeLock().unlock();
 		}
 	}
 
 	 @SuppressWarnings({"UnnecessaryBoxing"})
 	public void invalidate(Serializable[] spaces) throws CacheException {
 		readWriteLock.writeLock().lock();
 
 		try {
-			Long ts = new Long( region.nextTimestamp() );
+			Long ts = region.nextTimestamp();
 			for (Serializable space : spaces) {
 				LOG.debugf( "Invalidating space [%s], timestamp: %s", space, ts );
 				//put() has nowait semantics, is this really appropriate?
 				//note that it needs to be async replication, never local or sync
 				region.put( space, ts );
 				if ( factory != null && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().updateTimestampsCachePut();
 				}
 			}
 		}
 		finally {
 			readWriteLock.writeLock().unlock();
 		}
 	}
 
 	@SuppressWarnings({"unchecked", "UnnecessaryUnboxing"})
 	public boolean isUpToDate(Set spaces, Long timestamp) throws HibernateException {
 		readWriteLock.readLock().lock();
 
 		try {
 			for ( Serializable space : (Set<Serializable>) spaces ) {
 				Long lastUpdate = (Long) region.get( space );
 				if ( lastUpdate == null ) {
 					if ( factory != null && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor().updateTimestampsCacheMiss();
 					}
 					//the last update timestamp was lost from the cache
 					//(or there were no updates since startup!)
 					//updateTimestamps.put( space, new Long( updateTimestamps.nextTimestamp() ) );
 					//result = false; // safer
 				}
 				else {
                     if ( LOG.isDebugEnabled() ) {
                         LOG.debugf(
                                 "[%s] last update timestamp: %s",
                                 space,
                                 lastUpdate + ", result set timestamp: " + timestamp
                         );
                     }
 					if ( factory != null && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor().updateTimestampsCacheHit();
 					}
 					if ( lastUpdate >= timestamp ) return false;
 				}
 			}
 			return true;
 		}
 		finally {
 			readWriteLock.readLock().unlock();
 		}
 	}
 
 	public void clear() throws CacheException {
 		region.evictAll();
 	}
 
 	public void destroy() {
 		try {
 			region.destroy();
 		}
 		catch (Exception e) {
 			LOG.unableToDestroyUpdateTimestampsCache( region.getName(), e.getMessage() );
 		}
 	}
 
 	public TimestampsRegion getRegion() {
 		return region;
 	}
 
 	@Override
     public String toString() {
 		return "UpdateTimestampsCache";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
index 040bc3ac06..095f91606c 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StructuredCacheEntry.java
@@ -1,70 +1,70 @@
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Gavin King
  */
 public class StructuredCacheEntry implements CacheEntryStructure {
 
 	private EntityPersister persister;
 
 	public StructuredCacheEntry(EntityPersister persister) {
 		this.persister = persister;
 	}
 	
 	public Object destructure(Object item, SessionFactoryImplementor factory) {
 		Map map = (Map) item;
 		boolean lazyPropertiesUnfetched = ( (Boolean) map.get("_lazyPropertiesUnfetched") ).booleanValue();
 		String subclass = (String) map.get("_subclass");
 		Object version = map.get("_version");
 		EntityPersister subclassPersister = factory.getEntityPersister(subclass);
 		String[] names = subclassPersister.getPropertyNames();
 		Serializable[] state = new Serializable[names.length];
 		for ( int i=0; i<names.length; i++ ) {
 			state[i] = (Serializable) map.get( names[i] );
 		}
 		return new CacheEntry(state, subclass, lazyPropertiesUnfetched, version);
 	}
 
 	public Object structure(Object item) {
 		CacheEntry entry = (CacheEntry) item;
 		String[] names = persister.getPropertyNames();
 		Map map = new HashMap(names.length+2);
 		map.put( "_subclass", entry.getSubclass() );
 		map.put( "_version", entry.getVersion() );
-		map.put( "_lazyPropertiesUnfetched", entry.areLazyPropertiesUnfetched() ? Boolean.TRUE : Boolean.FALSE );
+		map.put( "_lazyPropertiesUnfetched", entry.areLazyPropertiesUnfetched() );
 		for ( int i=0; i<names.length; i++ ) {
 			map.put( names[i], entry.getDisassembledState()[i] );
 		}
 		return map;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 9cd78eca92..6cade49d1e 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1646,1505 +1646,1505 @@ public final class HbmBinder {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
 				}
 				any.setMetaValues( values );
 			}
 
 		}
 
 		bindColumns( node, any, isNullable, false, null, mappings );
 	}
 
 	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
 			Mappings mappings) throws MappingException {
 
 		bindColumns( node, oneToOne, isNullable, false, null, mappings );
 
 		Attribute constrNode = node.attribute( "constrained" );
 		boolean constrained = constrNode != null && constrNode.getValue().equals( "true" );
 		oneToOne.setConstrained( constrained );
 
 		oneToOne.setForeignKeyType( constrained ?
 				ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 				ForeignKeyDirection.FOREIGN_KEY_TO_PARENT );
 
 		initOuterJoinFetchSetting( node, oneToOne );
 		initLaziness( node, oneToOne, mappings, true );
 
 		oneToOne.setEmbedded( "true".equals( node.attributeValue( "embed-xml" ) ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 
 		oneToOne.setPropertyName( node.attributeValue( "name" ) );
 
 		oneToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( oneToOne.isConstrained() ) {
 				throw new MappingException(
 						"one-to-one attribute [" + path + "] does not support orphan delete as it is constrained"
 				);
 			}
 		}
 	}
 
 	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
 			throws MappingException {
 
 		oneToMany.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		oneToMany.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 	}
 
 	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
 		Attribute lengthNode = node.attribute( "length" );
 		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
 		Attribute scalNode = node.attribute( "scale" );
 		if ( scalNode != null ) column.setScale( Integer.parseInt( scalNode.getValue() ) );
 		Attribute precNode = node.attribute( "precision" );
 		if ( precNode != null ) column.setPrecision( Integer.parseInt( precNode.getValue() ) );
 
 		Attribute nullNode = node.attribute( "not-null" );
 		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
 
 		Attribute unqNode = node.attribute( "unique" );
 		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );
 
 		column.setCheckConstraint( node.attributeValue( "check" ) );
 		column.setDefaultValue( node.attributeValue( "default" ) );
 
 		Attribute typeNode = node.attribute( "sql-type" );
 		if ( typeNode != null ) column.setSqlType( typeNode.getValue() );
 
 		String customWrite = node.attributeValue( "write" );
 		if(customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
 			throw new MappingException("write expression must contain exactly one value placeholder ('?') character");
 		}
 		column.setCustomWrite( customWrite );
 		column.setCustomRead( node.attributeValue( "read" ) );
 
 		Element comment = node.element("comment");
 		if (comment!=null) column.setComment( comment.getTextTrim() );
 
 	}
 
 	/**
 	 * Called for arrays and primitive arrays
 	 */
 	public static void bindArray(Element node, Array array, String prefix, String path,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollection( node, array, prefix, path, mappings, inheritedMetas );
 
 		Attribute att = node.attribute( "element-class" );
 		if ( att != null ) array.setElementClassName( getClassName( att, mappings ) );
 
 	}
 
 	private static Class reflectedPropertyClass(String className, String propertyName)
 			throws MappingException {
 		if ( className == null ) return null;
 		return ReflectHelper.reflectedPropertyClass( className, propertyName );
 	}
 
 	public static void bindComposite(Element node, Component component, String path,
 			boolean isNullable, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 		bindComponent(
 				node,
 				component,
 				null,
 				null,
 				path,
 				isNullable,
 				false,
 				mappings,
 				inheritedMetas,
 				false
 			);
 	}
 
 	public static void bindCompositeId(Element node, Component component,
 			PersistentClass persistentClass, String propertyName, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		component.setKey( true );
 
 		String path = StringHelper.qualify(
 				persistentClass.getEntityName(),
 				propertyName == null ? "id" : propertyName );
 
 		bindComponent(
 				node,
 				component,
 				persistentClass.getClassName(),
 				propertyName,
 				path,
 				false,
 				node.attribute( "class" ) == null
 						&& propertyName == null,
 				mappings,
 				inheritedMetas,
 				false
 			);
 
 		if ( "true".equals( node.attributeValue("mapped") ) ) {
 			if ( propertyName!=null ) {
 				throw new MappingException("cannot combine mapped=\"true\" with specified name");
 			}
 			Component mapper = new Component( mappings, persistentClass );
 			bindComponent(
 					node,
 					mapper,
 					persistentClass.getClassName(),
 					null,
 					path,
 					false,
 					true,
 					mappings,
 					inheritedMetas,
 					true
 				);
 			persistentClass.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName("_identifierMapper");
 			property.setNodeName("id");
 			property.setUpdateable(false);
 			property.setInsertable(false);
 			property.setValue(mapper);
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty(property);
 		}
 
 	}
 
 	public static void bindComponent(
 			Element node,
 			Component component,
 			String ownerClassName,
 			String parentProperty,
 			String path,
 			boolean isNullable,
 			boolean isEmbedded,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			boolean isIdentifierMapper) throws MappingException {
 
 		component.setEmbedded( isEmbedded );
 		component.setRoleName( path );
 
 		inheritedMetas = getMetas( node, inheritedMetas );
 		component.setMetaAttributes( inheritedMetas );
 
 		Attribute classNode = isIdentifierMapper ? null : node.attribute( "class" );
 		if ( classNode != null ) {
 			component.setComponentClassName( getClassName( classNode, mappings ) );
 		}
 		else if ( "dynamic-component".equals( node.getName() ) ) {
 			component.setDynamic( true );
 		}
 		else if ( isEmbedded ) {
 			// an "embedded" component (composite ids and unique)
 			// note that this does not handle nested components
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				component.setComponentClassName( component.getOwner().getClassName() );
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 		else {
 			// todo : again, how *should* this work for non-pojo entities?
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				Class reflectedClass = reflectedPropertyClass( ownerClassName, parentProperty );
 				if ( reflectedClass != null ) {
 					component.setComponentClassName( reflectedClass.getName() );
 				}
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setName("_UniqueKey");
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, persistentClass
 					.getClassName(), subnode, mappings, inheritedMetas );
 				if ( !mutable ) property.setUpdateable(false);
 				if ( naturalId ) property.setNaturalIdentifier(true);
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) uniqueKey.addColumns( property.getColumnIterator() );
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
 			);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + list.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( list.getRole() );
 			ib.setEntityName( list.getOwner().getEntityName() );
 			ib.setValue( list.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	public static void bindIdentifierCollectionSecondPass(Element node,
 			IdentifierCollection collection, java.util.Map persistentClasses, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, collection, persistentClasses, mappings, inheritedMetas );
 
 		Element subnode = node.element( "collection-id" );
 		SimpleValue id = new SimpleValue( mappings, collection.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				id,
 				false,
 				IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME,
 				mappings
 			);
 		collection.setIdentifier( id );
 		makeIdentifier( subnode, id, mappings );
 
 	}
 
 	/**
 	 * Called for Maps
 	 */
 	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
 			collection.addManyToManyFilter( name, condition );
 		}
 	}
 
 	public static final FlushMode getFlushMode(String flushMode) {
 		if ( flushMode == null ) {
 			return null;
 		}
 		else if ( "auto".equals( flushMode ) ) {
 			return FlushMode.AUTO;
 		}
 		else if ( "commit".equals( flushMode ) ) {
 			return FlushMode.COMMIT;
 		}
 		else if ( "never".equals( flushMode ) ) {
 			return FlushMode.NEVER;
 		}
 		else if ( "manual".equals( flushMode ) ) {
 			return FlushMode.MANUAL;
 		}
 		else if ( "always".equals( flushMode ) ) {
 			return FlushMode.ALWAYS;
 		}
 		else {
 			throw new MappingException( "unknown flushmode" );
 		}
 	}
 
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
 		LOG.debugf( "Named query: %s -> %s", queryName, query );
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
-		Integer timeout = tAtt == null ? null : new Integer( tAtt.getValue() );
+		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
-		Integer fetchSize = fsAtt == null ? null : new Integer( fsAtt.getValue() );
+		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinition(
 				queryName,
 				query,
 				cacheable,
 				region,
 				timeout,
 				fetchSize,
 				getFlushMode( queryElem.attributeValue( "flush-mode" ) ) ,
 				getCacheMode( cacheMode ),
 				readOnly,
 				comment,
 				getParameterTypes(queryElem)
 			);
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static CacheMode getCacheMode(String cacheMode) {
 		if (cacheMode == null) return null;
 		if ( "get".equals( cacheMode ) ) return CacheMode.GET;
 		if ( "ignore".equals( cacheMode ) ) return CacheMode.IGNORE;
 		if ( "normal".equals( cacheMode ) ) return CacheMode.NORMAL;
 		if ( "put".equals( cacheMode ) ) return CacheMode.PUT;
 		if ( "refresh".equals( cacheMode ) ) return CacheMode.REFRESH;
 		throw new MappingException("Unknown Cache Mode: " + cacheMode);
 	}
 
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
 	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
 
 		if ( olAtt == null ) return Versioning.OPTIMISTIC_LOCK_VERSION;
 		String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
 			if ( onlyInheritable & !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
 		LOG.debugf( "Parsing filter-def [%s]", name );
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
 			LOG.debugf( "Adding filter parameter : %s -> %s", paramName, paramType );
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
 			LOG.debugf( "Parameter heuristic type : %s", heuristicType );
 			paramMappings.put( paramName, heuristicType );
 		}
 		LOG.debugf( "Parsed filter-def [%s]", name );
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.buildSessionFactory
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
 		filterable.addFilter( name, condition );
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
index c637ced0ba..1df87eaeaf 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
@@ -1,129 +1,129 @@
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
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.dom4j.Attribute;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Emmanuel Bernard
  */
 public class NamedSQLQuerySecondPass extends ResultSetMappingBinder implements QuerySecondPass {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        NamedSQLQuerySecondPass.class.getName());
 
 	private Element queryElem;
 	private String path;
 	private Mappings mappings;
 
 	public NamedSQLQuerySecondPass(Element queryElem, String path, Mappings mappings) {
 		this.queryElem = queryElem;
 		this.path = path;
 		this.mappings = mappings;
 	}
 
 	public void doSecondPass(Map persistentClasses) throws MappingException {
 		String queryName = queryElem.attribute( "name" ).getValue();
 		if (path!=null) queryName = path + '.' + queryName;
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
-		Integer timeout = tAtt == null ? null : new Integer( tAtt.getValue() );
+		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
-		Integer fetchSize = fsAtt == null ? null : new Integer( fsAtt.getValue() );
+		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		java.util.List<String> synchronizedTables = new ArrayList<String>();
 		Iterator tables = queryElem.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			synchronizedTables.add( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 		boolean callable = "true".equals( queryElem.attributeValue( "callable" ) );
 
 		NamedSQLQueryDefinition namedQuery;
 		Attribute ref = queryElem.attribute( "resultset-ref" );
 		String resultSetRef = ref == null ? null : ref.getValue();
 		if ( StringHelper.isNotEmpty( resultSetRef ) ) {
 			namedQuery = new NamedSQLQueryDefinition(
 					queryName,
 					queryElem.getText(),
 					resultSetRef,
 					synchronizedTables,
 					cacheable,
 					region,
 					timeout,
 					fetchSize,
 					HbmBinder.getFlushMode( queryElem.attributeValue( "flush-mode" ) ),
 					HbmBinder.getCacheMode( cacheMode ),
 					readOnly,
 					comment,
 					HbmBinder.getParameterTypes( queryElem ),
 					callable
 			);
 			//TODO check there is no actual definition elemnents when a ref is defined
 		}
 		else {
 			ResultSetMappingDefinition definition = buildResultSetMappingDefinition( queryElem, path, mappings );
 			namedQuery = new NamedSQLQueryDefinition(
 					queryName,
 					queryElem.getText(),
 					definition.getQueryReturns(),
 					synchronizedTables,
 					cacheable,
 					region,
 					timeout,
 					fetchSize,
 					HbmBinder.getFlushMode( queryElem.attributeValue( "flush-mode" ) ),
 					HbmBinder.getCacheMode( cacheMode ),
 					readOnly,
 					comment,
 					HbmBinder.getParameterTypes( queryElem ),
 					callable
 			);
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Named SQL query: %s -> %s", namedQuery.getName(), namedQuery.getQueryString() );
 		}
 		mappings.addSQLQuery( queryName, namedQuery );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
index 52bb575c77..36ceae439b 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
@@ -1,255 +1,255 @@
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
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.lang.reflect.Array;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * A persistent wrapper for an array. Lazy initialization
  * is NOT supported. Use of Hibernate arrays is not really
  * recommended.
  *
  * @author Gavin King
  */
 public class PersistentArrayHolder extends AbstractPersistentCollection {
 	protected Object array;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PersistentArrayHolder.class.getName());
 
 	//just to help out during the load (ugly, i know)
 	private transient Class elementClass;
 	private transient java.util.List tempList;
 
 	public PersistentArrayHolder(SessionImplementor session, Object array) {
 		super(session);
 		this.array = array;
 		setInitialized();
 	}
 
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		int length = /*(array==null) ? tempList.size() :*/ Array.getLength(array);
 		Serializable result = (Serializable) Array.newInstance( persister.getElementClass(), length );
 		for ( int i=0; i<length; i++ ) {
 			Object elt = /*(array==null) ? tempList.get(i) :*/ Array.get(array, i);
 			try {
 				Array.set( result, i, persister.getElementType().deepCopy(elt, persister.getFactory()) );
 			}
 			catch (IllegalArgumentException iae) {
 				LOG.invalidArrayElementType( iae.getMessage() );
 				throw new HibernateException( "Array element type error", iae );
 			}
 		}
 		return result;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return Array.getLength( snapshot ) == 0;
 	}
 
 	@Override
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		Object[] sn = (Object[]) snapshot;
 		Object[] arr = (Object[]) array;
 		ArrayList result = new ArrayList();
 		for (int i=0; i<sn.length; i++) result.add( sn[i] );
 		for (int i=0; i<sn.length; i++) identityRemove( result, arr[i], entityName, getSession() );
 		return result;
 	}
 
 	public PersistentArrayHolder(SessionImplementor session, CollectionPersister persister) throws HibernateException {
 		super(session);
 		elementClass = persister.getElementClass();
 	}
 
 	public Object getArray() {
 		return array;
 	}
 
 	public boolean isWrapper(Object collection) {
 		return array==collection;
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		Serializable snapshot = getSnapshot();
 		int xlen = Array.getLength(snapshot);
 		if ( xlen!= Array.getLength(array) ) return false;
 		for ( int i=0; i<xlen; i++) {
 			if ( elementType.isDirty( Array.get(snapshot, i), Array.get(array, i), getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public Iterator elements() {
 		//if (array==null) return tempList.iterator();
 		int length = Array.getLength(array);
 		java.util.List list = new ArrayList(length);
 		for (int i=0; i<length; i++) {
 			list.add( Array.get(array, i) );
 		}
 		return list.iterator();
 	}
 	@Override
 	public boolean empty() {
 		return false;
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		int index = ( (Integer) persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() ) ).intValue();
 		for ( int i = tempList.size(); i<=index; i++) {
 			tempList.add(i, null);
 		}
 		tempList.set(index, element);
 		return element;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return elements();
 	}
 
 	@Override
 	public void beginRead() {
 		super.beginRead();
 		tempList = new ArrayList();
 	}
 	@Override
     public boolean endRead() {
 		setInitialized();
 		array = Array.newInstance( elementClass, tempList.size() );
 		for ( int i=0; i<tempList.size(); i++) {
 			Array.set(array, i, tempList.get(i) );
 		}
 		tempList=null;
 		return true;
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		//if (tempList==null) throw new UnsupportedOperationException("Can't lazily initialize arrays");
 	}
 
 	@Override
     public boolean isDirectlyAccessible() {
 		return true;
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] cached = (Serializable[]) disassembled;
 
 		array = Array.newInstance( persister.getElementClass(), cached.length );
 
 		for ( int i=0; i<cached.length; i++ ) {
 			Array.set( array, i, persister.getElementType().assemble( cached[i], getSession(), owner ) );
 		}
 	}
 
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 		int length = Array.getLength(array);
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( Array.get(array,i), getSession(), null );
 		}
 
 		/*int length = tempList.size();
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( tempList.get(i), session );
 		}*/
 
 		return result;
 
 	}
 
 	@Override
     public Object getValue() {
 		return array;
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		java.util.List deletes = new ArrayList();
 		Serializable sn = getSnapshot();
 		int snSize = Array.getLength(sn);
 		int arraySize = Array.getLength(array);
 		int end;
 		if ( snSize > arraySize ) {
-			for ( int i=arraySize; i<snSize; i++ ) deletes.add( new Integer(i) );
+			for ( int i=arraySize; i<snSize; i++ ) deletes.add( i );
 			end = arraySize;
 		}
 		else {
 			end = snSize;
 		}
 		for ( int i=0; i<end; i++ ) {
-			if ( Array.get(array, i)==null && Array.get(sn, i)!=null ) deletes.add( new Integer(i) );
+			if ( Array.get(array, i)==null && Array.get(sn, i)!=null ) deletes.add( i );
 		}
 		return deletes.iterator();
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		Serializable sn = getSnapshot();
 		return Array.get(array, i)!=null && ( i >= Array.getLength(sn) || Array.get(sn, i)==null );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 		Serializable sn = getSnapshot();
 		return i<Array.getLength(sn) &&
 				Array.get(sn, i)!=null &&
 				Array.get(array, i)!=null &&
 				elemType.isDirty( Array.get(array, i), Array.get(sn, i), getSession() );
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
-		return new Integer(i);
+		return i;
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		Serializable sn = getSnapshot();
 		return Array.get(sn, i);
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
index 65f8e7c050..beac3d457b 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
@@ -1,429 +1,429 @@
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
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * An <tt>IdentifierBag</tt> implements "bag" semantics more efficiently than
  * a regular <tt>Bag</tt> by adding a synthetic identifier column to the
  * table. This identifier is unique for all rows in the table, allowing very
  * efficient updates and deletes. The value of the identifier is never exposed
  * to the application.<br>
  * <br>
  * <tt>IdentifierBag</tt>s may not be used for a many-to-one association.
  * Furthermore, there is no reason to use <tt>inverse="true"</tt>.
  *
  * @author Gavin King
  */
 public class PersistentIdentifierBag extends AbstractPersistentCollection implements List {
 
 	protected List values; //element
 	protected Map identifiers; //index -> id
 
 	public PersistentIdentifierBag(SessionImplementor session) {
 		super(session);
 	}
 
 	public PersistentIdentifierBag() {} //needed for SOAP libraries, etc
 
 	public PersistentIdentifierBag(SessionImplementor session, Collection coll) {
 		super(session);
 		if (coll instanceof List) {
 			values = (List) coll;
 		}
 		else {
 			values = new ArrayList();
 			Iterator iter = coll.iterator();
 			while ( iter.hasNext() ) {
 				values.add( iter.next() );
 			}
 		}
 		setInitialized();
 		setDirectlyAccessible(true);
 		identifiers = new HashMap();
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] array = (Serializable[]) disassembled;
 		int size = array.length;
 		beforeInitialize( persister, size );
 		for ( int i = 0; i < size; i+=2 ) {
 			identifiers.put(
-				new Integer(i/2),
+				(i/2),
 				persister.getIdentifierType().assemble( array[i], getSession(), owner )
 			);
 			values.add( persister.getElementType().assemble( array[i+1], getSession(), owner ) );
 		}
 	}
 
 	public Object getIdentifier(Object entry, int i) {
-		return identifiers.get( new Integer(i) );
+		return identifiers.get( i );
 	}
 
 	public boolean isWrapper(Object collection) {
 		return values==collection;
 	}
 
 	public boolean add(Object o) {
 		write();
 		values.add(o);
 		return true;
 	}
 
 	public void clear() {
 		initialize( true );
 		if ( ! values.isEmpty() || ! identifiers.isEmpty() ) {
 			values.clear();
 			identifiers.clear();
 			dirty();
 		}
 	}
 
 	public boolean contains(Object o) {
 		read();
 		return values.contains(o);
 	}
 
 	public boolean containsAll(Collection c) {
 		read();
 		return values.containsAll(c);
 	}
 
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : values.isEmpty();
 	}
 
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( values.iterator() );
 	}
 
 	public boolean remove(Object o) {
 		initialize( true );
 		int index = values.indexOf(o);
 		if (index>=0) {
 			beforeRemove(index);
 			values.remove(index);
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public boolean removeAll(Collection c) {
 		if ( c.size() > 0 ) {
 			boolean result = false;
 			Iterator iter = c.iterator();
 			while ( iter.hasNext() ) {
 				if ( remove( iter.next() ) ) result=true;
 			}
 			return result;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public boolean retainAll(Collection c) {
 		initialize( true );
 		if ( values.retainAll( c ) ) {
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public int size() {
 		return readSize() ? getCachedSize() : values.size();
 	}
 
 	public Object[] toArray() {
 		read();
 		return values.toArray();
 	}
 
 	public Object[] toArray(Object[] a) {
 		read();
 		return values.toArray(a);
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		identifiers = anticipatedSize <= 0 ? new HashMap() : new HashMap( anticipatedSize + 1 + (int)( anticipatedSize * .75f ), .75f );
 		values = anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize );
 	}
 
 	public Serializable disassemble(CollectionPersister persister)
 			throws HibernateException {
 		Serializable[] result = new Serializable[ values.size() * 2 ];
 		int i=0;
 		for (int j=0; j< values.size(); j++) {
 			Object value = values.get(j);
-			result[i++] = persister.getIdentifierType().disassemble( identifiers.get( new Integer(j) ), getSession(), null );
+			result[i++] = persister.getIdentifierType().disassemble( identifiers.get( j ), getSession(), null );
 			result[i++] = persister.getElementType().disassemble( value, getSession(), null );
 		}
 		return result;
 	}
 
 	public boolean empty() {
 		return values.isEmpty();
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return values.iterator();
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		Map snap = (Map) getSnapshot();
 		if ( snap.size()!= values.size() ) return false;
 		for ( int i=0; i<values.size(); i++ ) {
 			Object value = values.get(i);
-			Object id = identifiers.get( new Integer(i) );
+			Object id = identifiers.get( i );
 			if (id==null) return false;
 			Object old = snap.get(id);
 			if ( elementType.isDirty( old, value, getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Map) snapshot ).isEmpty();
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		Map snap = (Map) getSnapshot();
 		List deletes = new ArrayList( snap.keySet() );
 		for ( int i=0; i<values.size(); i++ ) {
-			if ( values.get(i)!=null ) deletes.remove( identifiers.get( new Integer(i) ) );
+			if ( values.get(i)!=null ) deletes.remove( identifiers.get( i ) );
 		}
 		return deletes.iterator();
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		throw new UnsupportedOperationException("Bags don't have indexes");
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		Map snap = (Map) getSnapshot();
-		Object id = identifiers.get( new Integer(i) );
+		Object id = identifiers.get( i );
 		return snap.get(id);
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType)
 		throws HibernateException {
 
 		Map snap = (Map) getSnapshot();
-		Object id = identifiers.get( new Integer(i) );
+		Object id = identifiers.get( i );
 		return entry!=null && ( id==null || snap.get(id)==null );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 
 		if (entry==null) return false;
 		Map snap = (Map) getSnapshot();
-		Object id = identifiers.get( new Integer(i) );
+		Object id = identifiers.get( i );
 		if (id==null) return false;
 		Object old = snap.get(id);
 		return old!=null && elemType.isDirty( old, entry, getSession() );
 	}
 
 
 	public Object readFrom(
 		ResultSet rs,
 		CollectionPersister persister,
 		CollectionAliases descriptor,
 		Object owner)
 		throws HibernateException, SQLException {
 
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		Object old = identifiers.put(
-			new Integer( values.size() ),
+			values.size(),
 			persister.readIdentifier( rs, descriptor.getSuffixedIdentifierAlias(), getSession() )
 		);
 		if ( old==null ) values.add(element); //maintain correct duplication if loaded in a cartesian product
 		return element;
 	}
 
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		HashMap map = new HashMap( values.size() );
 		Iterator iter = values.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Object value = iter.next();
 			map.put(
-				identifiers.get( new Integer(i++) ),
+				identifiers.get( i++ ),
 				persister.getElementType().deepCopy(value, persister.getFactory())
 			);
 		}
 		return map;
 	}
 
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		Map sn = (Map) snapshot;
 		return getOrphans( sn.values(), values, entityName, getSession() );
 	}
 
 	public void preInsert(CollectionPersister persister) throws HibernateException {
 		Iterator iter = values.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Object entry = iter.next();
-			Integer loc = new Integer(i++);
+			Integer loc = i++;
 			if ( !identifiers.containsKey(loc) ) { //TODO: native ids
 				Serializable id = persister.getIdentifierGenerator().generate( getSession(), entry );
 				identifiers.put(loc, id);
 			}
 		}
 	}
 
 	public void add(int index, Object element) {
 		write();
 		beforeAdd(index);
 		values.add(index, element);
 	}
 
 	public boolean addAll(int index, Collection c) {
 		if ( c.size() > 0 ) {
 			Iterator iter = c.iterator();
 			while ( iter.hasNext() ) {
 				add( index++, iter.next() );
 			}
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public Object get(int index) {
 		read();
 		return values.get(index);
 	}
 
 	public int indexOf(Object o) {
 		read();
 		return values.indexOf(o);
 	}
 
 	public int lastIndexOf(Object o) {
 		read();
 		return values.lastIndexOf(o);
 	}
 
 	public ListIterator listIterator() {
 		read();
 		return new ListIteratorProxy( values.listIterator() );
 	}
 
 	public ListIterator listIterator(int index) {
 		read();
 		return new ListIteratorProxy( values.listIterator(index) );
 	}
 
 	private void beforeRemove(int index) {
-		Object removedId = identifiers.get( new Integer(index) );
+		Object removedId = identifiers.get( index );
 		int last = values.size()-1;
 		for ( int i=index; i<last; i++ ) {
-			Object id = identifiers.get( new Integer(i+1) );
+			Object id = identifiers.get( i+1 );
 	        if ( id==null ) {
-				identifiers.remove( new Integer(i) );
+				identifiers.remove( i );
 	        }
 	        else {
-				identifiers.put( new Integer(i), id );
+				identifiers.put( i, id );
 	        }
 		}
-		identifiers.put( new Integer(last), removedId );
+		identifiers.put( last, removedId );
 	}
 
 	private void beforeAdd(int index) {
 		for ( int i=index; i<values.size(); i++ ) {
-			identifiers.put( new Integer(i+1), identifiers.get( new Integer(i) ) );
+			identifiers.put( i+1, identifiers.get( i ) );
 		}
-		identifiers.remove( new Integer(index) );
+		identifiers.remove( index );
 	}
 
 	public Object remove(int index) {
 		write();
 		beforeRemove(index);
 		return values.remove(index);
 	}
 
 	public Object set(int index, Object element) {
 		write();
 		return values.set(index, element);
 	}
 
 	public List subList(int fromIndex, int toIndex) {
 		read();
 		return new ListProxy( values.subList(fromIndex, toIndex) );
 	}
 
 	public boolean addAll(Collection c) {
 		if ( c.size()> 0 ) {
 			write();
 			return values.addAll(c);
 		}
 		else {
 			return false;
 		}
 	}
 
 	public void afterRowInsert(
 		CollectionPersister persister,
 		Object entry,
 		int i)
 		throws HibernateException {
 		//TODO: if we are using identity columns, fetch the identifier
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
index ae3a251708..526c1c373a 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
@@ -1,602 +1,602 @@
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
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * A persistent wrapper for a <tt>java.util.List</tt>. Underlying
  * collection is an <tt>ArrayList</tt>.
  *
  * @see java.util.ArrayList
  * @author Gavin King
  */
 public class PersistentList extends AbstractPersistentCollection implements List {
 	protected List list;
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
 
 		ArrayList clonedList = new ArrayList( list.size() );
 		for ( Object element : list ) {
 			Object deepCopy = persister.getElementType().deepCopy( element, persister.getFactory() );
 			clonedList.add( deepCopy );
 		}
 		return clonedList;
 	}
 
 	@Override
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		List sn = (List) snapshot;
 	    return getOrphans( sn, list, entityName, getSession() );
 	}
 
 	@Override
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		List sn = (List) getSnapshot();
 		if ( sn.size()!=this.list.size() ) return false;
 		Iterator iter = list.iterator();
 		Iterator sniter = sn.iterator();
 		while ( iter.hasNext() ) {
 			if ( elementType.isDirty( iter.next(), sniter.next(), getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Collection) snapshot ).isEmpty();
 	}
 
 	public PersistentList(SessionImplementor session) {
 		super(session);
 	}
 
 	public PersistentList(SessionImplementor session, List list) {
 		super(session);
 		this.list = list;
 		setInitialized();
 		setDirectlyAccessible(true);
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.list = ( List ) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	public boolean isWrapper(Object collection) {
 		return list==collection;
 	}
 
 	public PersistentList() {} //needed for SOAP libraries, etc
 
 	/**
 	 * @see java.util.List#size()
 	 */
 	public int size() {
 		return readSize() ? getCachedSize() : list.size();
 	}
 
 	/**
 	 * @see java.util.List#isEmpty()
 	 */
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : list.isEmpty();
 	}
 
 	/**
 	 * @see java.util.List#contains(Object)
 	 */
 	public boolean contains(Object object) {
 		Boolean exists = readElementExistence(object);
 		return exists==null ?
 				list.contains(object) :
 				exists.booleanValue();
 	}
 
 	/**
 	 * @see java.util.List#iterator()
 	 */
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( list.iterator() );
 	}
 
 	/**
 	 * @see java.util.List#toArray()
 	 */
 	public Object[] toArray() {
 		read();
 		return list.toArray();
 	}
 
 	/**
 	 * @see java.util.List#toArray(Object[])
 	 */
 	public Object[] toArray(Object[] array) {
 		read();
 		return list.toArray(array);
 	}
 
 	/**
 	 * @see java.util.List#add(Object)
 	 */
 	public boolean add(Object object) {
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return list.add(object);
 		}
 		else {
 			queueOperation( new SimpleAdd(object) );
 			return true;
 		}
 	}
 
 	/**
 	 * @see java.util.List#remove(Object)
 	 */
 	public boolean remove(Object value) {
 		Boolean exists = isPutQueueEnabled() ? readElementExistence(value) : null;
 		if ( exists == null ) {
 			initialize( true );
 			if ( list.remove( value ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else if ( exists.booleanValue() ) {
 			queueOperation( new SimpleRemove(value) );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#containsAll(Collection)
 	 */
 	public boolean containsAll(Collection coll) {
 		read();
 		return list.containsAll(coll);
 	}
 
 	/**
 	 * @see java.util.List#addAll(Collection)
 	 */
 	public boolean addAll(Collection values) {
 		if ( values.size()==0 ) {
 			return false;
 		}
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return list.addAll(values);
 		}
 		else {
 			Iterator iter = values.iterator();
 			while ( iter.hasNext() ) {
 				queueOperation( new SimpleAdd( iter.next() ) );
 			}
 			return values.size()>0;
 		}
 	}
 
 	/**
 	 * @see java.util.List#addAll(int, Collection)
 	 */
 	public boolean addAll(int index, Collection coll) {
 		if ( coll.size()>0 ) {
 			write();
 			return list.addAll(index,  coll);
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#removeAll(Collection)
 	 */
 	public boolean removeAll(Collection coll) {
 		if ( coll.size()>0 ) {
 			initialize( true );
 			if ( list.removeAll( coll ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#retainAll(Collection)
 	 */
 	public boolean retainAll(Collection coll) {
 		initialize( true );
 		if ( list.retainAll( coll ) ) {
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#clear()
 	 */
 	public void clear() {
 		if ( isClearQueueEnabled() ) {
 			queueOperation( new Clear() );
 		}
 		else {
 			initialize( true );
 			if ( ! list.isEmpty() ) {
 				list.clear();
 				dirty();
 			}
 		}
 	}
 
 	/**
 	 * @see java.util.List#get(int)
 	 */
 	public Object get(int index) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
-		Object result = readElementByIndex( new Integer(index) );
+		Object result = readElementByIndex( index );
 		return result==UNKNOWN ? list.get(index) : result;
 	}
 
 	/**
 	 * @see java.util.List#set(int, Object)
 	 */
 	public Object set(int index, Object value) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
-		Object old = isPutQueueEnabled() ? readElementByIndex( new Integer(index) ) : UNKNOWN;
+		Object old = isPutQueueEnabled() ? readElementByIndex( index ) : UNKNOWN;
 		if ( old==UNKNOWN ) {
 			write();
 			return list.set(index, value);
 		}
 		else {
 			queueOperation( new Set(index, value, old) );
 			return old;
 		}
 	}
 
 	/**
 	 * @see java.util.List#add(int, Object)
 	 */
 	public void add(int index, Object value) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			list.add(index, value);
 		}
 		else {
 			queueOperation( new Add(index, value) );
 		}
 	}
 
 	/**
 	 * @see java.util.List#remove(int)
 	 */
 	public Object remove(int index) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
 		Object old = isPutQueueEnabled() ?
-				readElementByIndex( new Integer(index) ) : UNKNOWN;
+				readElementByIndex( index ) : UNKNOWN;
 		if ( old==UNKNOWN ) {
 			write();
 			return list.remove(index);
 		}
 		else {
 			queueOperation( new Remove(index, old) );
 			return old;
 		}
 	}
 
 	/**
 	 * @see java.util.List#indexOf(Object)
 	 */
 	public int indexOf(Object value) {
 		read();
 		return list.indexOf(value);
 	}
 
 	/**
 	 * @see java.util.List#lastIndexOf(Object)
 	 */
 	public int lastIndexOf(Object value) {
 		read();
 		return list.lastIndexOf(value);
 	}
 
 	/**
 	 * @see java.util.List#listIterator()
 	 */
 	public ListIterator listIterator() {
 		read();
 		return new ListIteratorProxy( list.listIterator() );
 	}
 
 	/**
 	 * @see java.util.List#listIterator(int)
 	 */
 	public ListIterator listIterator(int index) {
 		read();
 		return new ListIteratorProxy( list.listIterator(index) );
 	}
 
 	/**
 	 * @see java.util.List#subList(int, int)
 	 */
 	public java.util.List subList(int from, int to) {
 		read();
 		return new ListProxy( list.subList(from, to) );
 	}
 
 	public boolean empty() {
 		return list.isEmpty();
 	}
 
 	public String toString() {
 		read();
 		return list.toString();
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() ) ;
 		int index = ( (Integer) persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() ) ).intValue();
 
 		//pad with nulls from the current last element up to the new index
 		for ( int i = list.size(); i<=index; i++) {
 			list.add(i, null);
 		}
 
 		list.set(index, element);
 		return element;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return list.iterator();
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] array = ( Serializable[] ) disassembled;
 		int size = array.length;
 		beforeInitialize( persister, size );
 		for ( int i = 0; i < size; i++ ) {
 			list.add( persister.getElementType().assemble( array[i], getSession(), owner ) );
 		}
 	}
 
 	public Serializable disassemble(CollectionPersister persister)
 	throws HibernateException {
 
 		int length = list.size();
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( list.get(i), getSession(), null );
 		}
 		return result;
 	}
 
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		List deletes = new ArrayList();
 		List sn = (List) getSnapshot();
 		int end;
 		if ( sn.size() > list.size() ) {
 			for ( int i=list.size(); i<sn.size(); i++ ) {
-				deletes.add( indexIsFormula ? sn.get(i) : new Integer(i) );
+				deletes.add( indexIsFormula ? sn.get(i) : i );
 			}
 			end = list.size();
 		}
 		else {
 			end = sn.size();
 		}
 		for ( int i=0; i<end; i++ ) {
 			if ( list.get(i)==null && sn.get(i)!=null ) {
-				deletes.add( indexIsFormula ? sn.get(i) : new Integer(i) );
+				deletes.add( indexIsFormula ? sn.get(i) : i );
 			}
 		}
 		return deletes.iterator();
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		final List sn = (List) getSnapshot();
 		return list.get(i)!=null && ( i >= sn.size() || sn.get(i)==null );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 		final List sn = (List) getSnapshot();
 		return i<sn.size() && sn.get(i)!=null && list.get(i)!=null &&
 			elemType.isDirty( list.get(i), sn.get(i), getSession() );
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
-		return new Integer(i);
+		return i;
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		final List sn = (List) getSnapshot();
 		return sn.get(i);
 	}
 
 	public boolean equals(Object other) {
 		read();
 		return list.equals(other);
 	}
 
 	public int hashCode() {
 		read();
 		return list.hashCode();
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 	final class Clear implements DelayedOperation {
 		public void operate() {
 			list.clear();
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
 		}
 	}
 
 	final class SimpleAdd implements DelayedOperation {
 		private Object value;
 
 		public SimpleAdd(Object value) {
 			this.value = value;
 		}
 		public void operate() {
 			list.add(value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return null;
 		}
 	}
 
 	final class Add implements DelayedOperation {
 		private int index;
 		private Object value;
 
 		public Add(int index, Object value) {
 			this.index = index;
 			this.value = value;
 		}
 		public void operate() {
 			list.add(index, value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return null;
 		}
 	}
 
 	final class Set implements DelayedOperation {
 		private int index;
 		private Object value;
 		private Object old;
 
 		public Set(int index, Object value, Object old) {
 			this.index = index;
 			this.value = value;
 			this.old = old;
 		}
 		public void operate() {
 			list.set(index, value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return old;
 		}
 	}
 
 	final class Remove implements DelayedOperation {
 		private int index;
 		private Object old;
 
 		public Remove(int index, Object old) {
 			this.index = index;
 			this.old = old;
 		}
 		public void operate() {
 			list.remove(index);
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			return old;
 		}
 	}
 
 	final class SimpleRemove implements DelayedOperation {
 		private Object value;
 
 		public SimpleRemove(Object value) {
 			this.value = value;
 		}
 		public void operate() {
 			list.remove(value);
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			return value;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
index 8181154d2d..4be9234a83 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
@@ -1,200 +1,200 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.engine.jdbc;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.sql.Blob;
 import java.sql.SQLException;
 
 import org.hibernate.type.descriptor.java.BinaryStreamImpl;
 import org.hibernate.type.descriptor.java.DataHelper;
 
 /**
  * Manages aspects of proxying {@link Blob Blobs} for non-contextual creation, including proxy creation and
  * handling proxy invocations.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class BlobProxy implements InvocationHandler {
 	private static final Class[] PROXY_INTERFACES = new Class[] { Blob.class, BlobImplementer.class };
 
 	private InputStream stream;
 	private long length;
 	private boolean needsReset = false;
 
 	/**
 	 * Constructor used to build {@link Blob} from byte array.
 	 *
 	 * @param bytes The byte array
 	 * @see #generateProxy(byte[])
 	 */
 	private BlobProxy(byte[] bytes) {
 		this.stream = new BinaryStreamImpl( bytes );
 		this.length = bytes.length;
 	}
 
 	/**
 	 * Constructor used to build {@link Blob} from a stream.
 	 *
 	 * @param stream The binary stream
 	 * @param length The length of the stream
 	 * @see #generateProxy(java.io.InputStream, long)
 	 */
 	private BlobProxy(InputStream stream, long length) {
 		this.stream = stream;
 		this.length = length;
 	}
 
 	private long getLength() {
 		return length;
 	}
 
 	private InputStream getStream() throws SQLException {
 		try {
 			if (needsReset) {
 				stream.reset();
 			}
 		}
 		catch ( IOException ioe) {
 			throw new SQLException("could not reset reader");
 		}
 		needsReset = true;
 		return stream;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @throws UnsupportedOperationException if any methods other than {@link Blob#length()}
 	 * or {@link Blob#getBinaryStream} are invoked.
 	 */
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		final String methodName = method.getName();
 		final int argCount = method.getParameterTypes().length;
 
 		if ( "length".equals( methodName ) && argCount == 0 ) {
 			return Long.valueOf( getLength() );
 		}
 		if ( "getBinaryStream".equals( methodName ) ) {
 			if ( argCount == 0 ) {
 				return getStream();
 			}
 			else if ( argCount == 2 ) {
 				long start = (Long) args[0];
 				if ( start < 1 ) {
 					throw new SQLException( "Start position 1-based; must be 1 or more." );
 				}
 				if ( start > getLength() ) {
 					throw new SQLException( "Start position [" + start + "] cannot exceed overall CLOB length [" + getLength() + "]" );
 				}
 				int length = (Integer) args[1];
 				if ( length < 0 ) {
 					// java docs specifically say for getBinaryStream(long,int) that the start+length must not exceed the
 					// total length, however that is at odds with the getBytes(long,int) behavior.
 					throw new SQLException( "Length must be great-than-or-equal to zero." );
 				}
 				return DataHelper.subStream( getStream(), start-1, length );
 			}
 		}
 		if ( "getBytes".equals( methodName ) ) {
 			if ( argCount == 2 ) {
 				long start = (Long) args[0];
 				if ( start < 1 ) {
 					throw new SQLException( "Start position 1-based; must be 1 or more." );
 				}
 				int length = (Integer) args[1];
 				if ( length < 0 ) {
 					throw new SQLException( "Length must be great-than-or-equal to zero." );
 				}
 				return DataHelper.extractBytes( getStream(), start-1, length );
 			}
 		}
 		if ( "free".equals( methodName ) && argCount == 0 ) {
 			stream.close();
 			return null;
 		}
 		if ( "toString".equals( methodName ) && argCount == 0 ) {
 			return this.toString();
 		}
 		if ( "equals".equals( methodName ) && argCount == 1 ) {
 			return Boolean.valueOf( proxy == args[0] );
 		}
 		if ( "hashCode".equals( methodName ) && argCount == 0 ) {
-			return new Integer( this.hashCode() );
+			return this.hashCode();
 		}
 
 		throw new UnsupportedOperationException( "Blob may not be manipulated from creating session" );
 	}
 
 	/**
 	 * Generates a BlobImpl proxy using byte data.
 	 *
 	 * @param bytes The data to be created as a Blob.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Blob generateProxy(byte[] bytes) {
 		return ( Blob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new BlobProxy( bytes )
 		);
 	}
 
 	/**
 	 * Generates a BlobImpl proxy using a given number of bytes from an InputStream.
 	 *
 	 * @param stream The input stream of bytes to be created as a Blob.
 	 * @param length The number of bytes from stream to be written to the Blob.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Blob generateProxy(InputStream stream, long length) {
 		return ( Blob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new BlobProxy( stream, length )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	private static ClassLoader getProxyClassLoader() {
 		ClassLoader cl = Thread.currentThread().getContextClassLoader();
 		if ( cl == null ) {
 			cl = BlobImplementer.class.getClassLoader();
 		}
 		return cl;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
index 52913d4c51..8a5c88b26d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
@@ -1,227 +1,227 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.engine.jdbc;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.io.StringReader;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.sql.Clob;
 import java.sql.SQLException;
 
 import org.hibernate.type.descriptor.java.DataHelper;
 
 /**
  * Manages aspects of proxying {@link Clob Clobs} for non-contextual creation, including proxy creation and
  * handling proxy invocations.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class ClobProxy implements InvocationHandler {
 	private static final Class[] PROXY_INTERFACES = new Class[] { Clob.class, ClobImplementer.class };
 
 	private String string;
 	private Reader reader;
 	private long length;
 	private boolean needsReset = false;
 
 
 	/**
 	 * Constructor used to build {@link Clob} from string data.
 	 *
 	 * @param string The byte array
 	 * @see #generateProxy(String)
 	 */
 	protected ClobProxy(String string) {
 		this.string = string;
 		reader = new StringReader(string);
 		length = string.length();
 	}
 
 	/**
 	 * Constructor used to build {@link Clob} from a reader.
 	 *
 	 * @param reader The character reader.
 	 * @param length The length of the reader stream.
 	 * @see #generateProxy(java.io.Reader, long)
 	 */
 	protected ClobProxy(Reader reader, long length) {
 		this.reader = reader;
 		this.length = length;
 	}
 
 	protected long getLength() {
 		return length;
 	}
 
 	protected InputStream getAsciiStream() throws SQLException {
 		resetIfNeeded();
 		return new ReaderInputStream( reader );
 	}
 
 	protected Reader getCharacterStream() throws SQLException {
 		resetIfNeeded();
 		return reader;
 	}
 
 	protected String getSubString(long start, int length) {
 		if ( string == null ) {
 			throw new UnsupportedOperationException( "Clob was not created from string; cannot substring" );
 		}
 		// semi-naive implementation
 		int endIndex = Math.min( ((int)start)+length, string.length() );
 		return string.substring( (int)start, endIndex );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @throws UnsupportedOperationException if any methods other than {@link Clob#length()},
 	 * {@link Clob#getAsciiStream()}, or {@link Clob#getCharacterStream()} are invoked.
 	 */
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		final String methodName = method.getName();
 		final int argCount = method.getParameterTypes().length;
 
 		if ( "length".equals( methodName ) && argCount == 0 ) {
 			return Long.valueOf( getLength() );
 		}
 		if ( "getAsciiStream".equals( methodName ) && argCount == 0 ) {
 			return getAsciiStream();
 		}
 		if ( "getCharacterStream".equals( methodName ) ) {
 			if ( argCount == 0 ) {
 				return getCharacterStream();
 			}
 			else if ( argCount == 2 ) {
 				long start = (Long) args[0];
 				if ( start < 1 ) {
 					throw new SQLException( "Start position 1-based; must be 1 or more." );
 				}
 				if ( start > getLength() ) {
 					throw new SQLException( "Start position [" + start + "] cannot exceed overall CLOB length [" + getLength() + "]" );
 				}
 				int length = (Integer) args[1];
 				if ( length < 0 ) {
 					// java docs specifically say for getCharacterStream(long,int) that the start+length must not exceed the
 					// total length, however that is at odds with the getSubString(long,int) behavior.
 					throw new SQLException( "Length must be great-than-or-equal to zero." );
 				}
 				return DataHelper.subStream( getCharacterStream(), start-1, length );
 			}
 		}
 		if ( "getSubString".equals( methodName ) && argCount == 2 ) {
 			long start = (Long) args[0];
 			if ( start < 1 ) {
 				throw new SQLException( "Start position 1-based; must be 1 or more." );
 			}
 			if ( start > getLength() ) {
 				throw new SQLException( "Start position [" + start + "] cannot exceed overall CLOB length [" + getLength() + "]" );
 			}
 			int length = (Integer) args[1];
 			if ( length < 0 ) {
 				throw new SQLException( "Length must be great-than-or-equal to zero." );
 			}
 			return getSubString( start-1, length );
 		}
 		if ( "free".equals( methodName ) && argCount == 0 ) {
 			reader.close();
 			return null;
 		}
 		if ( "toString".equals( methodName ) && argCount == 0 ) {
 			return this.toString();
 		}
 		if ( "equals".equals( methodName ) && argCount == 1 ) {
 			return Boolean.valueOf( proxy == args[0] );
 		}
 		if ( "hashCode".equals( methodName ) && argCount == 0 ) {
-			return new Integer( this.hashCode() );
+			return this.hashCode();
 		}
 
 		throw new UnsupportedOperationException( "Clob may not be manipulated from creating session" );
 	}
 
 	protected void resetIfNeeded() throws SQLException {
 		try {
 			if ( needsReset ) {
 				reader.reset();
 			}
 		}
 		catch ( IOException ioe ) {
 			throw new SQLException( "could not reset reader", ioe );
 		}
 		needsReset = true;
 	}
 
 	/**
 	 * Generates a {@link Clob} proxy using the string data.
 	 *
 	 * @param string The data to be wrapped as a {@link Clob}.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Clob generateProxy(String string) {
 		return ( Clob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new ClobProxy( string )
 		);
 	}
 
 	/**
 	 * Generates a {@link Clob} proxy using a character reader of given length.
 	 *
 	 * @param reader The character reader
 	 * @param length The length of the character reader
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Clob generateProxy(Reader reader, long length) {
 		return ( Clob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new ClobProxy( reader, length )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	protected static ClassLoader getProxyClassLoader() {
 		ClassLoader cl = Thread.currentThread().getContextClassLoader();
 		if ( cl == null ) {
 			cl = ClobImplementer.class.getClassLoader();
 		}
 		return cl;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ParameterParser.java b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ParameterParser.java
index b970591afe..d3616414db 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ParameterParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/ParameterParser.java
@@ -1,162 +1,162 @@
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
 package org.hibernate.engine.query.spi;
 import org.hibernate.QueryException;
 import org.hibernate.hql.internal.classic.ParserHelper;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * The single available method {@link #parse} is responsible for parsing a
  * query string and recognizing tokens in relation to parameters (either
  * named, JPA-style, or ordinal) and providing callbacks about such
  * recognitions.
  *
  * @author Steve Ebersole
  */
 public class ParameterParser {
 
 	public static interface Recognizer {
 		public void outParameter(int position);
 		public void ordinalParameter(int position);
 		public void namedParameter(String name, int position);
 		public void jpaPositionalParameter(String name, int position);
 		public void other(char character);
 	}
 
 	/**
 	 * Direct instantiation of ParameterParser disallowed.
 	 */
 	private ParameterParser() {
 	}
 
 	/**
 	 * Performs the actual parsing and tokenizing of the query string making appropriate
 	 * callbacks to the given recognizer upon recognition of the various tokens.
 	 * <p/>
 	 * Note that currently, this only knows how to deal with a single output
 	 * parameter (for callable statements).  If we later add support for
 	 * multiple output params, this, obviously, needs to change.
 	 *
 	 * @param sqlString The string to be parsed/tokenized.
 	 * @param recognizer The thing which handles recognition events.
 	 * @throws QueryException Indicates unexpected parameter conditions.
 	 */
 	public static void parse(String sqlString, Recognizer recognizer) throws QueryException {
 		boolean hasMainOutputParameter = startsWithEscapeCallTemplate( sqlString );
 		boolean foundMainOutputParam = false;
 
 		int stringLength = sqlString.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = sqlString.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 				recognizer.other( c );
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 				recognizer.other( c );
 			}
 			else {
 				if ( c == ':' ) {
 					// named parameter
 					int right = StringHelper.firstIndexOfChar( sqlString, ParserHelper.HQL_SEPARATORS, indx + 1 );
 					int chopLocation = right < 0 ? sqlString.length() : right;
 					String param = sqlString.substring( indx + 1, chopLocation );
 					if ( StringHelper.isEmpty( param ) ) {
 						throw new QueryException(
 								"Space is not allowed after parameter prefix ':' [" + sqlString + "]"
 						);
 					}
 					recognizer.namedParameter( param, indx );
 					indx = chopLocation - 1;
 				}
 				else if ( c == '?' ) {
 					// could be either an ordinal or JPA-positional parameter
 					if ( indx < stringLength - 1 && Character.isDigit( sqlString.charAt( indx + 1 ) ) ) {
 						// a peek ahead showed this as an JPA-positional parameter
 						int right = StringHelper.firstIndexOfChar( sqlString, ParserHelper.HQL_SEPARATORS, indx + 1 );
 						int chopLocation = right < 0 ? sqlString.length() : right;
 						String param = sqlString.substring( indx + 1, chopLocation );
 						// make sure this "name" is an integral
 						try {
-							new Integer( param );
+                            Integer.valueOf( param );
 						}
 						catch( NumberFormatException e ) {
 							throw new QueryException( "JPA-style positional param was not an integral ordinal" );
 						}
 						recognizer.jpaPositionalParameter( param, indx );
 						indx = chopLocation - 1;
 					}
 					else {
 						if ( hasMainOutputParameter && !foundMainOutputParam ) {
 							foundMainOutputParam = true;
 							recognizer.outParameter( indx );
 						}
 						else {
 							recognizer.ordinalParameter( indx );
 						}
 					}
 				}
 				else {
 					recognizer.other( c );
 				}
 			}
 		}
 	}
 
 	public static boolean startsWithEscapeCallTemplate(String sqlString) {
 		if ( ! ( sqlString.startsWith( "{" ) && sqlString.endsWith( "}" ) ) ) {
 			return false;
 		}
 
 		int chopLocation = sqlString.indexOf( "call" );
 		if ( chopLocation <= 0 ) {
 			return false;
 		}
 
 		final String checkString = sqlString.substring( 1, chopLocation + 4 );
 		final String fixture = "?=call";
 		int fixturePosition = 0;
 		boolean matches = true;
 		for ( int i = 0, max = checkString.length(); i < max; i++ ) {
 			final char c = Character.toLowerCase( checkString.charAt( i ) );
 			if ( Character.isWhitespace( c ) ) {
 				continue;
 			}
 			if ( c == fixture.charAt( fixturePosition ) ) {
 				fixturePosition++;
 				continue;
 			}
 			matches = false;
 			break;
 		}
 
 		return matches;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/QueryPlanCache.java b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/QueryPlanCache.java
index cfde574049..6588970fdb 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/QueryPlanCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/QueryPlanCache.java
@@ -1,356 +1,356 @@
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
 package org.hibernate.engine.query.spi;
 
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterImpl;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.SimpleMRUCache;
 import org.hibernate.internal.util.collections.SoftLimitMRUCache;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 /**
  * Acts as a cache for compiled query plans, as well as query-parameter metadata.
  *
  * @see Environment#QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES
  * @see Environment#QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES
  *
  * @author Steve Ebersole
  */
 public class QueryPlanCache implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryPlanCache.class.getName());
 	/**
 	 * simple cache of param metadata based on query string.  Ideally, the original "user-supplied query"
 	 * string should be used to obtain this metadata (i.e., not the para-list-expanded query string) to avoid
 	 * unnecessary cache entries.
 	 * <p>
 	 * Used solely for caching param metadata for native-sql queries, see {@link #getSQLParameterMetadata} for a
 	 * discussion as to why...
 	 */
 	private final SimpleMRUCache sqlParamMetadataCache;
 
 	/**
 	 * the cache of the actual plans...
 	 */
 	private final SoftLimitMRUCache planCache;
 	private SessionFactoryImplementor factory;
 
 	public QueryPlanCache(SessionFactoryImplementor factory) {
 		int maxStrongReferenceCount = ConfigurationHelper.getInt(
 				Environment.QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES,
 				factory.getProperties(),
 				SoftLimitMRUCache.DEFAULT_STRONG_REF_COUNT
 		);
 		int maxSoftReferenceCount = ConfigurationHelper.getInt(
 				Environment.QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES,
 				factory.getProperties(),
 				SoftLimitMRUCache.DEFAULT_SOFT_REF_COUNT
 		);
 
 		this.factory = factory;
 		this.sqlParamMetadataCache = new SimpleMRUCache( maxStrongReferenceCount );
 		this.planCache = new SoftLimitMRUCache( maxStrongReferenceCount, maxSoftReferenceCount );
 	}
 
 	/**
 	 * Obtain the parameter metadata for given native-sql query.
 	 * <p/>
 	 * for native-sql queries, the param metadata is determined outside any relation to a query plan, because
 	 * query plan creation and/or retrieval for a native-sql query depends on all of the return types having been
 	 * set, which might not be the case up-front when param metadata would be most useful
 	 *
 	 * @param query The query
 	 * @return The parameter metadata
 	 */
 	public ParameterMetadata getSQLParameterMetadata(String query) {
 		ParameterMetadata metadata = ( ParameterMetadata ) sqlParamMetadataCache.get( query );
 		if ( metadata == null ) {
 			metadata = buildNativeSQLParameterMetadata( query );
 			sqlParamMetadataCache.put( query, metadata );
 		}
 		return metadata;
 	}
 
 	public HQLQueryPlan getHQLQueryPlan(String queryString, boolean shallow, Map enabledFilters)
 			throws QueryException, MappingException {
 		HQLQueryPlanKey key = new HQLQueryPlanKey( queryString, shallow, enabledFilters );
 		HQLQueryPlan plan = ( HQLQueryPlan ) planCache.get ( key );
 
 		if ( plan == null ) {
 			LOG.tracev( "Unable to locate HQL query plan in cache; generating ({0})", queryString );
 			plan = new HQLQueryPlan(queryString, shallow, enabledFilters, factory );
 		}
 		else {
 			LOG.tracev( "Located HQL query plan in cache ({0})", queryString );
 		}
 		planCache.put( key, plan );
 
 		return plan;
 	}
 
 	public FilterQueryPlan getFilterQueryPlan(String filterString, String collectionRole, boolean shallow, Map enabledFilters)
 			throws QueryException, MappingException {
 		FilterQueryPlanKey key = new FilterQueryPlanKey( filterString, collectionRole, shallow, enabledFilters );
 		FilterQueryPlan plan = ( FilterQueryPlan ) planCache.get ( key );
 
 		if ( plan == null ) {
 			LOG.tracev( "Unable to locate collection-filter query plan in cache; generating ({0} : {1} )",
 					collectionRole, filterString );
 			plan = new FilterQueryPlan( filterString, collectionRole, shallow, enabledFilters, factory );
 		}
 		else {
 			LOG.tracev( "Located collection-filter query plan in cache ({0} : {1})", collectionRole, filterString );
 		}
 
 		planCache.put( key, plan );
 
 		return plan;
 	}
 
 	public NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) {
 		NativeSQLQueryPlan plan = ( NativeSQLQueryPlan ) planCache.get( spec );
 
 		if ( plan == null ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Unable to locate native-sql query plan in cache; generating ({0})", spec.getQueryString() );
 			}
 			plan = new NativeSQLQueryPlan( spec, factory );
 		}
 		else {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Located native-sql query plan in cache ({0})", spec.getQueryString() );
 			}
 		}
 
 		planCache.put( spec, plan );
 		return plan;
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	private ParameterMetadata buildNativeSQLParameterMetadata(String sqlString) {
 		ParamLocationRecognizer recognizer = ParamLocationRecognizer.parseLocations( sqlString );
 
 		OrdinalParameterDescriptor[] ordinalDescriptors =
 				new OrdinalParameterDescriptor[ recognizer.getOrdinalParameterLocationList().size() ];
 		for ( int i = 0; i < recognizer.getOrdinalParameterLocationList().size(); i++ ) {
 			final Integer position = ( Integer ) recognizer.getOrdinalParameterLocationList().get( i );
 			ordinalDescriptors[i] = new OrdinalParameterDescriptor( i, null, position.intValue() );
 		}
 
 		Iterator itr = recognizer.getNamedParameterDescriptionMap().entrySet().iterator();
 		Map<String,NamedParameterDescriptor> namedParamDescriptorMap = new HashMap<String,NamedParameterDescriptor>();
 		while( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String name = ( String ) entry.getKey();
 			final ParamLocationRecognizer.NamedParameterDescription description =
 					( ParamLocationRecognizer.NamedParameterDescription ) entry.getValue();
 			namedParamDescriptorMap.put(
 					name ,
 			        new NamedParameterDescriptor( name, null, description.buildPositionsArray(), description.isJpaStyle() )
 			);
 		}
 
 		return new ParameterMetadata( ordinalDescriptors, namedParamDescriptorMap );
 	}
 
 	private static class HQLQueryPlanKey implements Serializable {
 		private final String query;
 		private final boolean shallow;
 		private final Set<DynamicFilterKey> filterKeys;
 		private final int hashCode;
 
 		public HQLQueryPlanKey(String query, boolean shallow, Map enabledFilters) {
 			this.query = query;
 			this.shallow = shallow;
 
 			if ( enabledFilters == null || enabledFilters.isEmpty() ) {
 				filterKeys = Collections.emptySet();
 			}
 			else {
 				Set<DynamicFilterKey> tmp = new HashSet<DynamicFilterKey>(
 						CollectionHelper.determineProperSizing( enabledFilters ),
 						CollectionHelper.LOAD_FACTOR
 				);
 				for ( Object o : enabledFilters.values() ) {
 					tmp.add( new DynamicFilterKey( (FilterImpl) o ) );
 				}
 				this.filterKeys = Collections.unmodifiableSet( tmp );
 			}
 
 			int hash = query.hashCode();
 			hash = 29 * hash + ( shallow ? 1 : 0 );
 			hash = 29 * hash + filterKeys.hashCode();
 			this.hashCode = hash;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			final HQLQueryPlanKey that = ( HQLQueryPlanKey ) o;
 
 			return shallow == that.shallow
 					&& filterKeys.equals( that.filterKeys )
 					&& query.equals( that.query );
 
 		}
 
 		@Override
 		public int hashCode() {
 			return hashCode;
 		}
 	}
 
 	private static class DynamicFilterKey implements Serializable {
 		private final String filterName;
 		private final Map<String,Integer> parameterMetadata;
 		private final int hashCode;
 
 		@SuppressWarnings({ "UnnecessaryBoxing" })
 		private DynamicFilterKey(FilterImpl filter) {
 			this.filterName = filter.getName();
 			if ( filter.getParameters().isEmpty() ) {
 				parameterMetadata = Collections.emptyMap();
 			}
 			else {
 				parameterMetadata = new HashMap<String,Integer>(
 						CollectionHelper.determineProperSizing( filter.getParameters() ),
 						CollectionHelper.LOAD_FACTOR
 				);
 				for ( Object o : filter.getParameters().entrySet() ) {
 					final Map.Entry entry = (Map.Entry) o;
 					final String key = (String) entry.getKey();
 					final Integer valueCount;
 					if ( Collection.class.isInstance( entry.getValue() ) ) {
-						valueCount = new Integer( ( (Collection) entry.getValue() ).size() );
+						valueCount = ( (Collection) entry.getValue() ).size();
 					}
 					else {
 						valueCount = 1;
 					}
 					parameterMetadata.put( key, valueCount );
 				}
 			}
 
 			int hash = filterName.hashCode();
 			hash = 31 * hash + parameterMetadata.hashCode();
 			this.hashCode = hash;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			DynamicFilterKey that = ( DynamicFilterKey ) o;
 
 			return filterName.equals( that.filterName )
 					&& parameterMetadata.equals( that.parameterMetadata );
 
 		}
 
 		@Override
 		public int hashCode() {
 			return hashCode;
 		}
 	}
 
 	private static class FilterQueryPlanKey implements Serializable {
 		private final String query;
 		private final String collectionRole;
 		private final boolean shallow;
 		private final Set<String> filterNames;
 		private final int hashCode;
 
 		@SuppressWarnings({ "unchecked" })
 		public FilterQueryPlanKey(String query, String collectionRole, boolean shallow, Map enabledFilters) {
 			this.query = query;
 			this.collectionRole = collectionRole;
 			this.shallow = shallow;
 
 			if ( enabledFilters == null || enabledFilters.isEmpty() ) {
 				filterNames = Collections.emptySet();
 			}
 			else {
 				Set<String> tmp = new HashSet<String>();
 				tmp.addAll( enabledFilters.keySet() );
 				this.filterNames = Collections.unmodifiableSet( tmp );
 			}
 
 			int hash = query.hashCode();
 			hash = 29 * hash + collectionRole.hashCode();
 			hash = 29 * hash + ( shallow ? 1 : 0 );
 			hash = 29 * hash + filterNames.hashCode();
 			this.hashCode = hash;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			final FilterQueryPlanKey that = ( FilterQueryPlanKey ) o;
 
 			return shallow == that.shallow
 					&& filterNames.equals( that.filterNames )
 					&& query.equals( that.query )
 					&& collectionRole.equals( that.collectionRole );
 
 		}
 
 		@Override
 		public int hashCode() {
 			return hashCode;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BooleanLiteralNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BooleanLiteralNode.java
index abf8caf7e5..5da071d3c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BooleanLiteralNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BooleanLiteralNode.java
@@ -1,73 +1,73 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.LiteralType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Represents a boolean literal within a query.
  *
  * @author Steve Ebersole
  */
 public class BooleanLiteralNode extends LiteralNode implements ExpectedTypeAwareNode {
 	private Type expectedType;
 
 	public Type getDataType() {
 		return expectedType == null ? StandardBasicTypes.BOOLEAN : expectedType;
 	}
 
 	public Boolean getValue() {
-		return getType() == TRUE ? Boolean.TRUE : Boolean.FALSE;
+		return getType() == TRUE ;
 	}
 
 	@Override
 	public void setExpectedType(Type expectedType) {
 		this.expectedType = expectedType;
 	}
 
 	@Override
 	public Type getExpectedType() {
 		return expectedType;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public String getRenderText(SessionFactoryImplementor sessionFactory) {
 		try {
 			return typeAsLiteralType().objectToSQLString( getValue(), sessionFactory.getDialect() );
 		}
 		catch( Throwable t ) {
 			throw new QueryException( "Unable to render boolean literal value", t );
 		}
 	}
 
 	private LiteralType typeAsLiteralType() {
 		return (LiteralType) getDataType();
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTPrinter.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTPrinter.java
index 9deebbe08a..a6bee93ef7 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTPrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTPrinter.java
@@ -1,229 +1,229 @@
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
 package org.hibernate.hql.internal.ast.util;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.Map;
 
 import antlr.collections.AST;
 
 import org.hibernate.hql.internal.ast.tree.DisplayableNode;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Utility for generating pretty "ASCII art" representations of syntax trees.
  *
  * @author Joshua Davis
  * @author Steve Ebersole
  */
 public class ASTPrinter {
 	private final Map tokenTypeNameCache;
 	private final boolean showClassNames;
 
 	/**
 	 * Constructs a printer.
 	 * <p/>
 	 * Delegates to {@link #ASTPrinter(Class, boolean)} with {@link #isShowClassNames showClassNames} as <tt>true</tt>
 	 *
 	 * @param tokenTypeConstants The token types to use during printing; typically the {vocabulary}TokenTypes.java
 	 * interface generated by ANTLR.
 	 */
 	public ASTPrinter(Class tokenTypeConstants) {
 		this( ASTUtil.generateTokenNameCache( tokenTypeConstants ), true );
 	}
 
 	public ASTPrinter(boolean showClassNames) {
 		this( ( Map ) null, showClassNames );
 	}
 
 	/**
 	 * Constructs a printer.
 	 *
 	 * @param tokenTypeConstants The token types to use during printing; typically the {vocabulary}TokenTypes.java
 	 * interface generated by ANTLR.
 	 * @param showClassNames Should the AST class names be shown.
 	 */
 	public ASTPrinter(Class tokenTypeConstants, boolean showClassNames) {
 		this( ASTUtil.generateTokenNameCache( tokenTypeConstants ), showClassNames );
 	}
 
 	private ASTPrinter(Map tokenTypeNameCache, boolean showClassNames) {
 		this.tokenTypeNameCache = tokenTypeNameCache;
 		this.showClassNames = showClassNames;
 	}
 
 	/**
 	 * Getter for property 'showClassNames'.
 	 *
 	 * @return Value for property 'showClassNames'.
 	 */
 	public boolean isShowClassNames() {
 		return showClassNames;
 	}
 
 	/**
 	 * Renders the AST into 'ASCII art' form and returns that string representation.
 	 *
 	 * @param ast The AST to display.
 	 * @param header The header for the display.
 	 *
 	 * @return The AST in 'ASCII art' form, as a string.
 	 */
 	public String showAsString(AST ast, String header) {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream();
 		PrintStream ps = new PrintStream( baos );
 		ps.println( header );
 		showAst( ast, ps );
 		ps.flush();
 		return new String( baos.toByteArray() );
 	}
 
 	/**
 	 * Prints the AST in 'ASCII art' form to the specified print stream.
 	 *
 	 * @param ast The AST to print.
 	 * @param out The print stream to which the AST should be printed.
 	 */
 	public void showAst(AST ast, PrintStream out) {
 		showAst( ast, new PrintWriter( out ) );
 	}
 
 	/**
 	 * Prints the AST in 'ASCII art' tree form to the specified print writer.
 	 *
 	 * @param ast The AST to print.
 	 * @param pw The print writer to which the AST should be written.
 	 */
 	public void showAst(AST ast, PrintWriter pw) {
 		ArrayList parents = new ArrayList();
 		showAst( parents, pw, ast );
 		pw.flush();
 	}
 
 	/**
 	 * Returns the token type name for the given token type.
 	 *
 	 * @param type The token type.
 	 * @return String - The token type name from the token type constant class,
 	 *         or just the integer as a string if none exists.
 	 */
 	public String getTokenTypeName(int type) {
-		final Integer typeInteger = new Integer( type );
+		final Integer typeInteger = type;
 		String value = null;
 		if ( tokenTypeNameCache != null ) {
 			value = ( String ) tokenTypeNameCache.get( typeInteger );
 		}
 		if ( value == null ) {
 			value = typeInteger.toString();
 		}
 		return value;
 	}
 
 	private void showAst(ArrayList parents, PrintWriter pw, AST ast) {
 		if ( ast == null ) {
 			pw.println( "AST is null!" );
 			return;
 		}
 
 		for ( int i = 0; i < parents.size(); i++ ) {
 			AST parent = ( AST ) parents.get( i );
 			if ( parent.getNextSibling() == null ) {
 
 				pw.print( "   " );
 			}
 			else {
 				pw.print( " | " );
 			}
 		}
 
 		if ( ast.getNextSibling() == null ) {
 			pw.print( " \\-" );
 		}
 		else {
 			pw.print( " +-" );
 		}
 
 		showNode( pw, ast );
 
 		ArrayList newParents = new ArrayList( parents );
 		newParents.add( ast );
 		for ( AST child = ast.getFirstChild(); child != null; child = child.getNextSibling() ) {
 			showAst( newParents, pw, child );
 		}
 		newParents.clear();
 	}
 
 	private void showNode(PrintWriter pw, AST ast) {
 		String s = nodeToString( ast, isShowClassNames() );
 		pw.println( s );
 	}
 
 	public String nodeToString(AST ast, boolean showClassName) {
 		if ( ast == null ) {
 			return "{node:null}";
 		}
 		StringBuffer buf = new StringBuffer();
 		buf.append( "[" ).append( getTokenTypeName( ast.getType() ) ).append( "] " );
 		if ( showClassName ) {
 			buf.append( StringHelper.unqualify( ast.getClass().getName() ) ).append( ": " );
 		}
 
         buf.append( "'" );
         String text = ast.getText();
 		if ( text == null ) {
 			text = "{text:null}";
 		}
 		appendEscapedMultibyteChars(text, buf);
         buf.append( "'" );
 		if ( ast instanceof DisplayableNode ) {
 			DisplayableNode displayableNode = ( DisplayableNode ) ast;
 			// Add a space before the display text.
 			buf.append( " " ).append( displayableNode.getDisplayText() );
 		}
 		return buf.toString();
 	}
 
     public static void appendEscapedMultibyteChars(String text, StringBuffer buf) {
         char[] chars = text.toCharArray();
         for (int i = 0; i < chars.length; i++) {
             char aChar = chars[i];
             if (aChar > 256) {
                 buf.append("\\u");
                 buf.append(Integer.toHexString(aChar));
             }
             else
                 buf.append(aChar);
         }
     }
 
     public static String escapeMultibyteChars(String text) {
         StringBuffer buf = new StringBuffer();
         appendEscapedMultibyteChars(text,buf);
         return buf.toString();
     }
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTUtil.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTUtil.java
index e39442c420..00e5d1cdb1 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTUtil.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/ASTUtil.java
@@ -1,463 +1,463 @@
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
 package org.hibernate.hql.internal.ast.util;
 import java.lang.reflect.Field;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import antlr.ASTFactory;
 import antlr.collections.AST;
 import antlr.collections.impl.ASTArray;
 
 /**
  * Provides utility methods for AST traversal and manipulation.
  *
  * @author Joshua Davis
  * @author Steve Ebersole
  */
 public final class ASTUtil {
 	/**
 	 * Disallow instantiation.
 	 *
 	 * @deprecated (tellclovertoignorethis)
 	 */
 	private ASTUtil() {
 	}
 
 	/**
 	 * Creates a single node AST.
 	 * <p/>
 	 * TODO : this is silly, remove it...
 	 *
 	 * @param astFactory The factory.
 	 * @param type The node type.
 	 * @param text The node text.
 	 *
 	 * @return AST - A single node tree.
 	 *
 	 * @deprecated silly
 	 */
 	public static AST create(ASTFactory astFactory, int type, String text) {
 		return astFactory.create( type, text );
 	}
 
 	/**
 	 * Creates a single node AST as a sibling of the passed prevSibling,
 	 * taking care to reorganize the tree correctly to account for this
 	 * newly created node.
 	 *
 	 * @param astFactory The factory.
 	 * @param type The node type.
 	 * @param text The node text.
 	 * @param prevSibling The previous sibling.
 	 *
 	 * @return The created AST node.
 	 */
 	public static AST createSibling(ASTFactory astFactory, int type, String text, AST prevSibling) {
 		AST node = astFactory.create( type, text );
 		return insertSibling( node, prevSibling );
 	}
 
 	/**
 	 * Inserts a node into a child subtree as a particularly positioned
 	 * sibling taking care to properly reorganize the tree to account for this
 	 * new addition.
 	 *
 	 * @param node The node to insert
 	 * @param prevSibling The previous node at the sibling position
 	 * where we want this node inserted.
 	 *
 	 * @return The return is the same as the node parameter passed in.
 	 */
 	public static AST insertSibling(AST node, AST prevSibling) {
 		node.setNextSibling( prevSibling.getNextSibling() );
 		prevSibling.setNextSibling( node );
 		return node;
 	}
 
 	/**
 	 * Creates a 'binary operator' subtree, given the information about the
 	 * parent and the two child nodex.
 	 *
 	 * @param factory The AST factory.
 	 * @param parentType The type of the parent node.
 	 * @param parentText The text of the parent node.
 	 * @param child1 The first child.
 	 * @param child2 The second child.
 	 *
 	 * @return AST - A new sub-tree of the form "(parent child1 child2)"
 	 */
 	public static AST createBinarySubtree(ASTFactory factory, int parentType, String parentText, AST child1, AST child2) {
 		ASTArray array = createAstArray( factory, 3, parentType, parentText, child1 );
 		array.add( child2 );
 		return factory.make( array );
 	}
 
 	/**
 	 * Creates a single parent of the specified child (i.e. a 'unary operator'
 	 * subtree).
 	 *
 	 * @param factory The AST factory.
 	 * @param parentType The type of the parent node.
 	 * @param parentText The text of the parent node.
 	 * @param child The child.
 	 *
 	 * @return AST - A new sub-tree of the form "(parent child)"
 	 */
 	public static AST createParent(ASTFactory factory, int parentType, String parentText, AST child) {
 		ASTArray array = createAstArray( factory, 2, parentType, parentText, child );
 		return factory.make( array );
 	}
 
 	public static AST createTree(ASTFactory factory, AST[] nestedChildren) {
 		AST[] array = new AST[2];
 		int limit = nestedChildren.length - 1;
 		for ( int i = limit; i >= 0; i-- ) {
 			if ( i != limit ) {
 				array[1] = nestedChildren[i + 1];
 				array[0] = nestedChildren[i];
 				factory.make( array );
 			}
 		}
 		return array[0];
 	}
 
 	/**
 	 * Determine if a given node (test) is contained anywhere in the subtree
 	 * of another given node (fixture).
 	 *
 	 * @param fixture The node against which to testto be checked for children.
 	 * @param test The node to be tested as being a subtree child of the parent.
 	 * @return True if child is contained in the parent's collection of children.
 	 */
 	public static boolean isSubtreeChild(AST fixture, AST test) {
 		AST n = fixture.getFirstChild();
 		while ( n != null ) {
 			if ( n == test ) {
 				return true;
 			}
 			if ( n.getFirstChild() != null && isSubtreeChild( n, test ) ) {
 				return true;
 			}
 			n = n.getNextSibling();
 		}
 		return false;
 	}
 
 	/**
 	 * Finds the first node of the specified type in the chain of children.
 	 *
 	 * @param parent The parent
 	 * @param type The type to find.
 	 *
 	 * @return The first node of the specified type, or null if not found.
 	 */
 	public static AST findTypeInChildren(AST parent, int type) {
 		AST n = parent.getFirstChild();
 		while ( n != null && n.getType() != type ) {
 			n = n.getNextSibling();
 		}
 		return n;
 	}
 
 	/**
 	 * Returns the last direct child of 'n'.
 	 *
 	 * @param n The parent
 	 *
 	 * @return The last direct child of 'n'.
 	 */
 	public static AST getLastChild(AST n) {
 		return getLastSibling( n.getFirstChild() );
 	}
 
 	/**
 	 * Returns the last sibling of 'a'.
 	 *
 	 * @param a The sibling.
 	 *
 	 * @return The last sibling of 'a'.
 	 */
 	private static AST getLastSibling(AST a) {
 		AST last = null;
 		while ( a != null ) {
 			last = a;
 			a = a.getNextSibling();
 		}
 		return last;
 	}
 
 	/**
 	 * Returns the 'list' representation with some brackets around it for debugging.
 	 *
 	 * @param n The tree.
 	 *
 	 * @return The list representation of the tree.
 	 */
 	public static String getDebugString(AST n) {
 		StringBuffer buf = new StringBuffer();
 		buf.append( "[ " );
 		buf.append( ( n == null ) ? "{null}" : n.toStringTree() );
 		buf.append( " ]" );
 		return buf.toString();
 	}
 
 	/**
 	 * Find the previous sibling in the parent for the given child.
 	 *
 	 * @param parent the parent node
 	 * @param child the child to find the previous sibling of
 	 *
 	 * @return the previous sibling of the child
 	 */
 	public static AST findPreviousSibling(AST parent, AST child) {
 		AST prev = null;
 		AST n = parent.getFirstChild();
 		while ( n != null ) {
 			if ( n == child ) {
 				return prev;
 			}
 			prev = n;
 			n = n.getNextSibling();
 		}
 		throw new IllegalArgumentException( "Child not found in parent!" );
 	}
 
 	/**
 	 * Makes the child node a sibling of the parent, reconnecting all siblings.
 	 *
 	 * @param parent the parent
 	 * @param child the child
 	 */
 	public static void makeSiblingOfParent(AST parent, AST child) {
 		AST prev = findPreviousSibling( parent, child );
 		if ( prev != null ) {
 			prev.setNextSibling( child.getNextSibling() );
 		}
 		else { // child == parent.getFirstChild()
 			parent.setFirstChild( child.getNextSibling() );
 		}
 		child.setNextSibling( parent.getNextSibling() );
 		parent.setNextSibling( child );
 	}
 
 	public static String getPathText(AST n) {
 		StringBuffer buf = new StringBuffer();
 		getPathText( buf, n );
 		return buf.toString();
 	}
 
 	private static void getPathText(StringBuffer buf, AST n) {
 		AST firstChild = n.getFirstChild();
 		// If the node has a first child, recurse into the first child.
 		if ( firstChild != null ) {
 			getPathText( buf, firstChild );
 		}
 		// Append the text of the current node.
 		buf.append( n.getText() );
 		// If there is a second child (RHS), recurse into that child.
 		if ( firstChild != null && firstChild.getNextSibling() != null ) {
 			getPathText( buf, firstChild.getNextSibling() );
 		}
 	}
 
 	public static boolean hasExactlyOneChild(AST n) {
 		return n != null && n.getFirstChild() != null && n.getFirstChild().getNextSibling() == null;
 	}
 
 	public static void appendSibling(AST n, AST s) {
 		while ( n.getNextSibling() != null ) {
 			n = n.getNextSibling();
 		}
 		n.setNextSibling( s );
 	}
 
 	/**
 	 * Inserts the child as the first child of the parent, all other children are shifted over to the 'right'.
 	 *
 	 * @param parent the parent
 	 * @param child the new first child
 	 */
 	public static void insertChild(AST parent, AST child) {
 		if ( parent.getFirstChild() == null ) {
 			parent.setFirstChild( child );
 		}
 		else {
 			AST n = parent.getFirstChild();
 			parent.setFirstChild( child );
 			child.setNextSibling( n );
 		}
 	}
 
 	private static ASTArray createAstArray(ASTFactory factory, int size, int parentType, String parentText, AST child1) {
 		ASTArray array = new ASTArray( size );
 		array.add( factory.create( parentType, parentText ) );
 		array.add( child1 );
 		return array;
 	}
 
 	/**
 	 * Filters nodes out of a tree.
 	 */
 	public static interface FilterPredicate {
 		/**
 		 * Returns true if the node should be filtered out.
 		 *
 		 * @param n The node.
 		 *
 		 * @return true if the node should be filtered out, false to keep the node.
 		 */
 		boolean exclude(AST n);
 	}
 
 	/**
 	 * A predicate that uses inclusion, rather than exclusion semantics.
 	 */
 	public abstract static class IncludePredicate implements FilterPredicate {
 		public final boolean exclude(AST node) {
 			return !include( node );
 		}
 
 		public abstract boolean include(AST node);
 	}
 
 	public static List collectChildren(AST root, FilterPredicate predicate) {
 		return new CollectingNodeVisitor( predicate ).collect( root );
 	}
 
 	private static class CollectingNodeVisitor implements NodeTraverser.VisitationStrategy {
 		private final FilterPredicate predicate;
 		private final List collectedNodes = new ArrayList();
 
 		public CollectingNodeVisitor(FilterPredicate predicate) {
 			this.predicate = predicate;
 		}
 
 		public void visit(AST node) {
 			if ( predicate == null || !predicate.exclude( node ) ) {
 				collectedNodes.add( node );
 			}
 		}
 
 		public List getCollectedNodes() {
 			return collectedNodes;
 		}
 
 		public List collect(AST root) {
 			NodeTraverser traverser = new NodeTraverser( this );
 			traverser.traverseDepthFirst( root );
 			return collectedNodes;
 		}
 	}
 
 	/**
 	 * Method to generate a map of token type names, keyed by their token type values.
 	 *
 	 * @param tokenTypeInterface The *TokenTypes interface (or implementor of said interface).
 	 * @return The generated map.
 	 */
 	public static Map generateTokenNameCache(Class tokenTypeInterface) {
 		final Field[] fields = tokenTypeInterface.getFields();
 		Map cache = new HashMap( (int)( fields.length * .75 ) + 1 );
 		for ( int i = 0; i < fields.length; i++ ) {
 			final Field field = fields[i];
 			if ( Modifier.isStatic( field.getModifiers() ) ) {
 				try {
 					cache.put( field.get( null ), field.getName() );
 				}
 				catch ( Throwable ignore ) {
 				}
 			}
 		}
 		return cache;
 	}
 
 	/**
 	 * Get the name of a constant defined on the given class which has the given value.
 	 * <p/>
 	 * Note, if multiple constants have this value, the first will be returned which is known to be different
 	 * on different JVM implementations.
 	 *
 	 * @param owner The class which defines the constant
 	 * @param value The value of the constant.
 	 *
 	 * @return The token type name, *or* the integer value if the name could not be found.
 	 *
 	 * @deprecated Use #getTokenTypeName instead
 	 */
 	public static String getConstantName(Class owner, int value) {
 		return getTokenTypeName( owner, value );
 	}
 
 	/**
 	 * Intended to retrieve the name of an AST token type based on the token type interface.  However, this
 	 * method can be used to look up the name of any constant defined on a class/interface based on the constant value.
 	 * Note that if multiple constants have this value, the first will be returned which is known to be different
 	 * on different JVM implementations.
 	 *
 	 * @param tokenTypeInterface The *TokenTypes interface (or one of its implementors).
 	 * @param tokenType The token type value.
 	 *
 	 * @return The corresponding name.
 	 */
 	public static String getTokenTypeName(Class tokenTypeInterface, int tokenType) {
 		String tokenTypeName = Integer.toString( tokenType );
 		if ( tokenTypeInterface != null ) {
 			Field[] fields = tokenTypeInterface.getFields();
 			for ( int i = 0; i < fields.length; i++ ) {
 				final Integer fieldValue = extractIntegerValue( fields[i] );
 				if ( fieldValue != null && fieldValue.intValue() == tokenType ) {
 					tokenTypeName = fields[i].getName();
 					break;
 				}
 			}
 		}
 		return tokenTypeName;
 	}
 
 	private static Integer extractIntegerValue(Field field) {
 		Integer rtn = null;
 		try {
 			Object value = field.get( null );
 			if ( value instanceof Integer ) {
 				rtn = ( Integer ) value;
 			}
 			else if ( value instanceof Short ) {
-				rtn = new Integer( ( ( Short ) value ).intValue() );
+				rtn =  ( ( Short ) value ).intValue();
 			}
 			else if ( value instanceof Long ) {
 				if ( ( ( Long ) value ).longValue() <= Integer.MAX_VALUE ) {
-					rtn = new Integer( ( ( Long ) value ).intValue() );
+					rtn = ( ( Long ) value ).intValue();
 				}
 			}
 		}
 		catch ( IllegalAccessException ignore ) {
 		}
 		return rtn;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java
index 4a12420536..389c6f35b8 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/IdentifierGeneratorHelper.java
@@ -1,686 +1,686 @@
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
 package org.hibernate.id;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.CustomType;
 import org.hibernate.type.Type;
 
 /**
  * Factory and helper methods for {@link IdentifierGenerator} framework.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class IdentifierGeneratorHelper {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        IdentifierGeneratorHelper.class.getName());
 
 	/**
 	 * Marker object returned from {@link IdentifierGenerator#generate} to indicate that we should short-circuit any
 	 * continued generated id checking.  Currently this is only used in the case of the
 	 * {@link org.hibernate.id.ForeignGenerator foreign} generator as a way to signal that we should use the associated
 	 * entity's id value.
 	 */
 	public static final Serializable SHORT_CIRCUIT_INDICATOR = new Serializable() {
 		@Override
         public String toString() {
 			return "SHORT_CIRCUIT_INDICATOR";
 		}
 	};
 
 	/**
 	 * Marker object returned from {@link IdentifierGenerator#generate} to indicate that the entity's identifier will
 	 * be generated as part of the datbase insertion.
 	 */
 	public static final Serializable POST_INSERT_INDICATOR = new Serializable() {
 		@Override
         public String toString() {
 			return "POST_INSERT_INDICATOR";
 		}
 	};
 
 
 	/**
 	 * Get the generated identifier when using identity columns
 	 *
 	 * @param rs The result set from which to extract the the generated identity.
 	 * @param type The expected type mapping for the identity value.
 	 * @return The generated identity value
 	 * @throws SQLException Can be thrown while accessing the result set
 	 * @throws HibernateException Indicates a problem reading back a generated identity value.
 	 */
 	public static Serializable getGeneratedIdentity(ResultSet rs, Type type) throws SQLException, HibernateException {
 		if ( !rs.next() ) {
 			throw new HibernateException( "The database returned no natively generated identity value" );
 		}
 		final Serializable id = get( rs, type );
 		LOG.debugf( "Natively generated identity: %s", id );
 		return id;
 	}
 
 	/**
 	 * Extract the value from the result set (which is assumed to already have been positioned to the apopriate row)
 	 * and wrp it in the appropriate Java numeric type.
 	 *
 	 * @param rs The result set from which to extract the value.
 	 * @param type The expected type of the value.
 	 * @return The extracted value.
 	 * @throws SQLException Indicates problems access the result set
 	 * @throws IdentifierGenerationException Indicates an unknown type.
 	 */
 	public static Serializable get(ResultSet rs, Type type) throws SQLException, IdentifierGenerationException {
 		if ( ResultSetIdentifierConsumer.class.isInstance( type ) ) {
 			return ( ( ResultSetIdentifierConsumer ) type ).consumeIdentifier( rs );
 		}
 		if ( CustomType.class.isInstance( type ) ) {
 			final CustomType customType = (CustomType) type;
 			if ( ResultSetIdentifierConsumer.class.isInstance( customType.getUserType() ) ) {
 				return ( (ResultSetIdentifierConsumer) customType.getUserType() ).consumeIdentifier( rs );
 			}
 		}
 
 		Class clazz = type.getReturnedClass();
 		if ( clazz == Long.class ) {
-			return new Long( rs.getLong( 1 ) );
+			return rs.getLong( 1 );
 		}
 		else if ( clazz == Integer.class ) {
-			return new Integer( rs.getInt( 1 ) );
+			return rs.getInt( 1 );
 		}
 		else if ( clazz == Short.class ) {
-			return new Short( rs.getShort( 1 ) );
+			return rs.getShort( 1 );
 		}
 		else if ( clazz == String.class ) {
 			return rs.getString( 1 );
 		}
 		else if ( clazz == BigInteger.class ) {
 			return rs.getBigDecimal( 1 ).setScale( 0, BigDecimal.ROUND_UNNECESSARY ).toBigInteger();
 		}
 		else if ( clazz == BigDecimal.class ) {
 			return rs.getBigDecimal( 1 ).setScale( 0, BigDecimal.ROUND_UNNECESSARY );
 		}
 		else {
 			throw new IdentifierGenerationException(
 					"unrecognized id type : " + type.getName() + " -> " + clazz.getName()
 			);
 		}
 	}
 
 	/**
 	 * Wrap the given value in the given Java numeric class.
 	 *
 	 * @param value The primitive value to wrap.
 	 * @param clazz The Java numeric type in which to wrap the value.
 	 *
 	 * @return The wrapped type.
 	 *
 	 * @throws IdentifierGenerationException Indicates an unhandled 'clazz'.
 	 *
 	 * @deprecated Use the {@link #getIntegralDataTypeHolder holders} instead.
 	 */
 	@Deprecated
     public static Number createNumber(long value, Class clazz) throws IdentifierGenerationException {
 		if ( clazz == Long.class ) {
-			return new Long( value );
+			return value;
 		}
 		else if ( clazz == Integer.class ) {
-			return new Integer( ( int ) value );
+			return ( int ) value;
 		}
 		else if ( clazz == Short.class ) {
-			return new Short( ( short ) value );
+			return ( short ) value;
 		}
 		else {
 			throw new IdentifierGenerationException( "unrecognized id type : " + clazz.getName() );
 		}
 	}
 
 	public static IntegralDataTypeHolder getIntegralDataTypeHolder(Class integralType) {
 		if ( integralType == Long.class
 				|| integralType == Integer.class
 				|| integralType == Short.class ) {
 			return new BasicHolder( integralType );
 		}
 		else if ( integralType == BigInteger.class ) {
 			return new BigIntegerHolder();
 		}
 		else if ( integralType == BigDecimal.class ) {
 			return new BigDecimalHolder();
 		}
 		else {
 			throw new IdentifierGenerationException(
 					"Unknown integral data type for ids : " + integralType.getName()
 			);
 		}
 	}
 
 	public static long extractLong(IntegralDataTypeHolder holder) {
 		if ( holder.getClass() == BasicHolder.class ) {
 			( (BasicHolder) holder ).checkInitialized();
 			return ( (BasicHolder) holder ).value;
 		}
 		else if ( holder.getClass() == BigIntegerHolder.class ) {
 			( (BigIntegerHolder) holder ).checkInitialized();
 			return ( (BigIntegerHolder) holder ).value.longValue();
 		}
 		else if ( holder.getClass() == BigDecimalHolder.class ) {
 			( (BigDecimalHolder) holder ).checkInitialized();
 			return ( (BigDecimalHolder) holder ).value.longValue();
 		}
 		throw new IdentifierGenerationException( "Unknown IntegralDataTypeHolder impl [" + holder + "]" );
 	}
 
 	public static BigInteger extractBigInteger(IntegralDataTypeHolder holder) {
 		if ( holder.getClass() == BasicHolder.class ) {
 			( (BasicHolder) holder ).checkInitialized();
 			return BigInteger.valueOf( ( (BasicHolder) holder ).value );
 		}
 		else if ( holder.getClass() == BigIntegerHolder.class ) {
 			( (BigIntegerHolder) holder ).checkInitialized();
 			return ( (BigIntegerHolder) holder ).value;
 		}
 		else if ( holder.getClass() == BigDecimalHolder.class ) {
 			( (BigDecimalHolder) holder ).checkInitialized();
 			// scale should already be set...
 			return ( (BigDecimalHolder) holder ).value.toBigInteger();
 		}
 		throw new IdentifierGenerationException( "Unknown IntegralDataTypeHolder impl [" + holder + "]" );
 	}
 
 	public static BigDecimal extractBigDecimal(IntegralDataTypeHolder holder) {
 		if ( holder.getClass() == BasicHolder.class ) {
 			( (BasicHolder) holder ).checkInitialized();
 			return BigDecimal.valueOf( ( (BasicHolder) holder ).value );
 		}
 		else if ( holder.getClass() == BigIntegerHolder.class ) {
 			( (BigIntegerHolder) holder ).checkInitialized();
 			return new BigDecimal( ( (BigIntegerHolder) holder ).value );
 		}
 		else if ( holder.getClass() == BigDecimalHolder.class ) {
 			( (BigDecimalHolder) holder ).checkInitialized();
 			// scale should already be set...
 			return ( (BigDecimalHolder) holder ).value;
 		}
 		throw new IdentifierGenerationException( "Unknown IntegralDataTypeHolder impl [" + holder + "]" );
 	}
 
 	public static class BasicHolder implements IntegralDataTypeHolder {
 		private final Class exactType;
 		private long value = Long.MIN_VALUE;
 
 		public BasicHolder(Class exactType) {
 			this.exactType = exactType;
 			if ( exactType != Long.class && exactType != Integer.class && exactType != Short.class ) {
 				throw new IdentifierGenerationException( "Invalid type for basic integral holder : " + exactType );
 			}
 		}
 
 		public long getActualLongValue() {
 			return value;
 		}
 
 		public IntegralDataTypeHolder initialize(long value) {
 			this.value = value;
 			return this;
 		}
 
 		public IntegralDataTypeHolder initialize(ResultSet resultSet, long defaultValue) throws SQLException {
 			long value = resultSet.getLong( 1 );
 			if ( resultSet.wasNull() ) {
 				value = defaultValue;
 			}
 			return initialize( value );
 		}
 
 		public void bind(PreparedStatement preparedStatement, int position) throws SQLException {
 			// TODO : bind it as 'exact type'?  Not sure if that gains us anything...
 			preparedStatement.setLong( position, value );
 		}
 
 		public IntegralDataTypeHolder increment() {
 			checkInitialized();
 			value++;
 			return this;
 		}
 
 		private void checkInitialized() {
 			if ( value == Long.MIN_VALUE ) {
 				throw new IdentifierGenerationException( "integral holder was not initialized" );
 			}
 		}
 
 		public IntegralDataTypeHolder add(long addend) {
 			checkInitialized();
 			value += addend;
 			return this;
 		}
 
 		public IntegralDataTypeHolder decrement() {
 			checkInitialized();
 			value--;
 			return this;
 		}
 
 		public IntegralDataTypeHolder subtract(long subtrahend) {
 			checkInitialized();
 			value -= subtrahend;
 			return this;
 		}
 
 		public IntegralDataTypeHolder multiplyBy(IntegralDataTypeHolder factor) {
 			return multiplyBy( extractLong( factor ) );
 		}
 
 		public IntegralDataTypeHolder multiplyBy(long factor) {
 			checkInitialized();
 			value *= factor;
 			return this;
 		}
 
 		public boolean eq(IntegralDataTypeHolder other) {
 			return eq( extractLong( other ) );
 		}
 
 		public boolean eq(long value) {
 			checkInitialized();
 			return this.value == value;
 		}
 
 		public boolean lt(IntegralDataTypeHolder other) {
 			return lt( extractLong( other ) );
 		}
 
 		public boolean lt(long value) {
 			checkInitialized();
 			return this.value < value;
 		}
 
 		public boolean gt(IntegralDataTypeHolder other) {
 			return gt( extractLong( other ) );
 		}
 
 		public boolean gt(long value) {
 			checkInitialized();
 			return this.value > value;
 		}
 
 		public IntegralDataTypeHolder copy() {
 			BasicHolder copy = new BasicHolder( exactType );
 			copy.value = value;
 			return copy;
 		}
 
 		public Number makeValue() {
 			// TODO : should we check for truncation?
 			checkInitialized();
 			if ( exactType == Long.class ) {
-				return new Long( value );
+				return value;
 			}
 			else if ( exactType == Integer.class ) {
-				return new Integer( ( int ) value );
+				return ( int ) value;
 			}
 			else {
-				return new Short( ( short ) value );
+				return ( short ) value;
 			}
 		}
 
 		public Number makeValueThenIncrement() {
 			final Number result = makeValue();
 			value++;
 			return result;
 		}
 
 		public Number makeValueThenAdd(long addend) {
 			final Number result = makeValue();
 			value += addend;
 			return result;
 		}
 
 		@Override
         public String toString() {
 			return "BasicHolder[" + exactType.getName() + "[" + value + "]]";
 		}
 
 		@Override
         public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			BasicHolder that = (BasicHolder) o;
 
 			return value == that.value;
 		}
 
 		@Override
         public int hashCode() {
 			return (int) ( value ^ ( value >>> 32 ) );
 		}
 	}
 
 	public static class BigIntegerHolder implements IntegralDataTypeHolder {
 		private BigInteger value;
 
 		public IntegralDataTypeHolder initialize(long value) {
 			this.value = BigInteger.valueOf( value );
 			return this;
 		}
 
 		public IntegralDataTypeHolder initialize(ResultSet resultSet, long defaultValue) throws SQLException {
 			final BigDecimal rsValue = resultSet.getBigDecimal( 1 );
 			if ( resultSet.wasNull() ) {
 				return initialize( defaultValue );
 			}
 			this.value = rsValue.setScale( 0, BigDecimal.ROUND_UNNECESSARY ).toBigInteger();
 			return this;
 		}
 
 		public void bind(PreparedStatement preparedStatement, int position) throws SQLException {
 			preparedStatement.setBigDecimal( position, new BigDecimal( value ) );
 		}
 
 		public IntegralDataTypeHolder increment() {
 			checkInitialized();
 			value = value.add( BigInteger.ONE );
 			return this;
 		}
 
 		private void checkInitialized() {
 			if ( value == null ) {
 				throw new IdentifierGenerationException( "integral holder was not initialized" );
 			}
 		}
 
 		public IntegralDataTypeHolder add(long increment) {
 			checkInitialized();
 			value = value.add( BigInteger.valueOf( increment ) );
 			return this;
 		}
 
 		public IntegralDataTypeHolder decrement() {
 			checkInitialized();
 			value = value.subtract( BigInteger.ONE );
 			return this;
 		}
 
 		public IntegralDataTypeHolder subtract(long subtrahend) {
 			checkInitialized();
 			value = value.subtract( BigInteger.valueOf( subtrahend ) );
 			return this;
 		}
 
 		public IntegralDataTypeHolder multiplyBy(IntegralDataTypeHolder factor) {
 			checkInitialized();
 			value = value.multiply( extractBigInteger( factor ) );
 			return this;
 		}
 
 		public IntegralDataTypeHolder multiplyBy(long factor) {
 			checkInitialized();
 			value = value.multiply( BigInteger.valueOf( factor ) );
 			return this;
 		}
 
 		public boolean eq(IntegralDataTypeHolder other) {
 			checkInitialized();
 			return value.compareTo( extractBigInteger( other ) ) == 0;
 		}
 
 		public boolean eq(long value) {
 			checkInitialized();
 			return this.value.compareTo( BigInteger.valueOf( value ) ) == 0;
 		}
 
 		public boolean lt(IntegralDataTypeHolder other) {
 			checkInitialized();
 			return value.compareTo( extractBigInteger( other ) ) < 0;
 		}
 
 		public boolean lt(long value) {
 			checkInitialized();
 			return this.value.compareTo( BigInteger.valueOf( value ) ) < 0;
 		}
 
 		public boolean gt(IntegralDataTypeHolder other) {
 			checkInitialized();
 			return value.compareTo( extractBigInteger( other ) ) > 0;
 		}
 
 		public boolean gt(long value) {
 			checkInitialized();
 			return this.value.compareTo( BigInteger.valueOf( value ) ) > 0;
 		}
 
 		public IntegralDataTypeHolder copy() {
 			BigIntegerHolder copy = new BigIntegerHolder();
 			copy.value = value;
 			return copy;
 		}
 
 		public Number makeValue() {
 			checkInitialized();
 			return value;
 		}
 
 		public Number makeValueThenIncrement() {
 			final Number result = makeValue();
 			value = value.add( BigInteger.ONE );
 			return result;
 		}
 
 		public Number makeValueThenAdd(long addend) {
 			final Number result = makeValue();
 			value = value.add( BigInteger.valueOf( addend ) );
 			return result;
 		}
 
 		@Override
         public String toString() {
 			return "BigIntegerHolder[" + value + "]";
 		}
 
 		@Override
         public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			BigIntegerHolder that = (BigIntegerHolder) o;
 
 			return this.value == null
 					? that.value == null
 					: value.equals( that.value );
 		}
 
 		@Override
         public int hashCode() {
 			return value != null ? value.hashCode() : 0;
 		}
 	}
 
 	public static class BigDecimalHolder implements IntegralDataTypeHolder {
 		private BigDecimal value;
 
 		public IntegralDataTypeHolder initialize(long value) {
 			this.value = BigDecimal.valueOf( value );
 			return this;
 		}
 
 		public IntegralDataTypeHolder initialize(ResultSet resultSet, long defaultValue) throws SQLException {
 			final BigDecimal rsValue = resultSet.getBigDecimal( 1 );
 			if ( resultSet.wasNull() ) {
 				return initialize( defaultValue );
 			}
 			this.value = rsValue.setScale( 0, BigDecimal.ROUND_UNNECESSARY );
 			return this;
 		}
 
 		public void bind(PreparedStatement preparedStatement, int position) throws SQLException {
 			preparedStatement.setBigDecimal( position, value );
 		}
 
 		public IntegralDataTypeHolder increment() {
 			checkInitialized();
 			value = value.add( BigDecimal.ONE );
 			return this;
 		}
 
 		private void checkInitialized() {
 			if ( value == null ) {
 				throw new IdentifierGenerationException( "integral holder was not initialized" );
 			}
 		}
 
 		public IntegralDataTypeHolder add(long increment) {
 			checkInitialized();
 			value = value.add( BigDecimal.valueOf( increment ) );
 			return this;
 		}
 
 		public IntegralDataTypeHolder decrement() {
 			checkInitialized();
 			value = value.subtract( BigDecimal.ONE );
 			return this;
 		}
 
 		public IntegralDataTypeHolder subtract(long subtrahend) {
 			checkInitialized();
 			value = value.subtract( BigDecimal.valueOf( subtrahend ) );
 			return this;
 		}
 
 		public IntegralDataTypeHolder multiplyBy(IntegralDataTypeHolder factor) {
 			checkInitialized();
 			value = value.multiply( extractBigDecimal( factor ) );
 			return this;
 		}
 
 		public IntegralDataTypeHolder multiplyBy(long factor) {
 			checkInitialized();
 			value = value.multiply( BigDecimal.valueOf( factor ) );
 			return this;
 		}
 
 		public boolean eq(IntegralDataTypeHolder other) {
 			checkInitialized();
 			return value.compareTo( extractBigDecimal( other ) ) == 0;
 		}
 
 		public boolean eq(long value) {
 			checkInitialized();
 			return this.value.compareTo( BigDecimal.valueOf( value ) ) == 0;
 		}
 
 		public boolean lt(IntegralDataTypeHolder other) {
 			checkInitialized();
 			return value.compareTo( extractBigDecimal( other ) ) < 0;
 		}
 
 		public boolean lt(long value) {
 			checkInitialized();
 			return this.value.compareTo( BigDecimal.valueOf( value ) ) < 0;
 		}
 
 		public boolean gt(IntegralDataTypeHolder other) {
 			checkInitialized();
 			return value.compareTo( extractBigDecimal( other ) ) > 0;
 		}
 
 		public boolean gt(long value) {
 			checkInitialized();
 			return this.value.compareTo( BigDecimal.valueOf( value ) ) > 0;
 		}
 
 		public IntegralDataTypeHolder copy() {
 			BigDecimalHolder copy = new BigDecimalHolder();
 			copy.value = value;
 			return copy;
 		}
 
 		public Number makeValue() {
 			checkInitialized();
 			return value;
 		}
 
 		public Number makeValueThenIncrement() {
 			final Number result = makeValue();
 			value = value.add( BigDecimal.ONE );
 			return result;
 		}
 
 		public Number makeValueThenAdd(long addend) {
 			final Number result = makeValue();
 			value = value.add( BigDecimal.valueOf( addend ) );
 			return result;
 		}
 
 		@Override
         public String toString() {
 			return "BigDecimalHolder[" + value + "]";
 		}
 
 		@Override
         public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			BigDecimalHolder that = (BigDecimalHolder) o;
 
 			return this.value == null
 					? that.value == null
 					: this.value.equals( that.value );
 		}
 
 		@Override
         public int hashCode() {
 			return value != null ? value.hashCode() : 0;
 		}
 	}
 
 	/**
 	 * Disallow instantiation of IdentifierGeneratorHelper.
 	 */
 	private IdentifierGeneratorHelper() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
index 96d268b28e..4d40eefc6d 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
@@ -1,958 +1,958 @@
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
 package org.hibernate.internal;
 
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
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueResultException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.classic.ParserHelper;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.property.Getter;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.SerializableType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
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
 	private Map<String,TypedValue> namedParameters = new HashMap<String, TypedValue>(4);
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
 		selection.setFirstRow( firstResult);
 		return this;
 	}
 
 	public Query setMaxResults(int maxResults) {
 		if ( maxResults < 0 ) {
 			// treat negatives specically as meaning no limit...
 			selection.setMaxRows( null );
 		}
 		else {
 			selection.setMaxRows( maxResults);
 		}
 		return this;
 	}
 
 	public Query setTimeout(int timeout) {
 		selection.setTimeout( timeout);
 		return this;
 	}
 	public Query setFetchSize(int fetchSize) {
 		selection.setFetchSize( fetchSize);
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
 			 namedParameters.put( name, new TypedValue( type, val  ) );
 			 return this;
 		}
 	}
 
 	public Query setParameter(int position, Object val) throws HibernateException {
 		if (val == null) {
 			setParameter( position, val, StandardBasicTypes.SERIALIZABLE );
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
 				type = StandardBasicTypes.SERIALIZABLE;
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
 			return ( (Session) session ).getTypeHelper().entity( clazz );
 		}
 		else {
 			return type;
 		}
 	}
 
 	public Query setString(int position, String val) {
 		setParameter(position, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setCharacter(int position, char val) {
 		setParameter(position, new Character(val), StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setBoolean(int position, boolean val) {
-		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
+		Boolean valueToUse = val;
 		Type typeToUse = determineType( position, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( position, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(int position, byte val) {
-		setParameter(position, new Byte(val), StandardBasicTypes.BYTE);
+		setParameter(position, val, StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setShort(int position, short val) {
-		setParameter(position, new Short(val), StandardBasicTypes.SHORT);
+		setParameter(position, val, StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setInteger(int position, int val) {
-		setParameter(position, new Integer(val), StandardBasicTypes.INTEGER);
+		setParameter(position, val, StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLong(int position, long val) {
-		setParameter(position, new Long(val), StandardBasicTypes.LONG);
+		setParameter(position, val, StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setFloat(int position, float val) {
-		setParameter(position, new Float(val), StandardBasicTypes.FLOAT);
+		setParameter(position, val, StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setDouble(int position, double val) {
-		setParameter(position, new Double(val), StandardBasicTypes.DOUBLE);
+		setParameter(position, val, StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setBinary(int position, byte[] val) {
 		setParameter(position, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(int position, String val) {
 		setParameter(position, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setSerializable(int position, Serializable val) {
 		setParameter(position, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setDate(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setTime(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setEntity(int position, Object val) {
 		setParameter( position, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	private String resolveEntityName(Object val) {
 		if ( val == null ) {
 			throw new IllegalArgumentException( "entity for parameter binding cannot be null" );
 		}
 		return session.bestGuessEntityName( val );
 	}
 
 	public Query setLocale(int position, Locale locale) {
 		setParameter(position, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(int position, Calendar calendar) {
 		setParameter(position, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(int position, Calendar calendar) {
 		setParameter(position, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setBinary(String name, byte[] val) {
 		setParameter(name, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(String name, String val) {
 		setParameter(name, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setBoolean(String name, boolean val) {
-		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
+		Boolean valueToUse = val;
 		Type typeToUse = determineType( name, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( name, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(String name, byte val) {
-		setParameter(name, new Byte(val), StandardBasicTypes.BYTE);
+		setParameter(name, val, StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setCharacter(String name, char val) {
-		setParameter(name, new Character(val), StandardBasicTypes.CHARACTER);
+		setParameter(name, val, StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setDate(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setDouble(String name, double val) {
-		setParameter(name, new Double(val), StandardBasicTypes.DOUBLE);
+		setParameter(name, val, StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setEntity(String name, Object val) {
 		setParameter( name, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	public Query setFloat(String name, float val) {
-		setParameter(name, new Float(val), StandardBasicTypes.FLOAT);
+		setParameter(name, val, StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setInteger(String name, int val) {
-		setParameter(name, new Integer(val), StandardBasicTypes.INTEGER);
+		setParameter(name, val, StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLocale(String name, Locale locale) {
 		setParameter(name, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(String name, Calendar calendar) {
 		setParameter(name, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(String name, Calendar calendar) {
 		setParameter(name, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setLong(String name, long val) {
-		setParameter(name, new Long(val), StandardBasicTypes.LONG);
+		setParameter(name, val, StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setSerializable(String name, Serializable val) {
 		setParameter(name, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setShort(String name, short val) {
-		setParameter(name, new Short(val), StandardBasicTypes.SHORT);
+		setParameter(name, val, StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setString(String name, String val) {
 		setParameter(name, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setTime(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setBigDecimal(int position, BigDecimal number) {
 		setParameter(position, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigDecimal(String name, BigDecimal number) {
 		setParameter(name, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigInteger(int position, BigInteger number) {
 		setParameter(position, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setBigInteger(String name, BigInteger number) {
 		setParameter(name, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		namedParameterLists.put( name, new TypedValue( type, vals ) );
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
 			namedParamsCopy.put( name, new TypedValue( type, vals.iterator().next() ) );
 			return query;
 		}
 
 		StringBuffer list = new StringBuffer( 16 );
 		Iterator iter = vals.iterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			String alias = ( isJpaPositionalParam ? 'x' + name : name ) + i++ + '_';
 			namedParamsCopy.put( alias, new TypedValue( type, iter.next() ) );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
index 306bab2362..cb34b9129a 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
@@ -1,333 +1,333 @@
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
 package org.hibernate.internal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.loader.Loader;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of ScrollableResults which can handle collection fetches.
  *
  * @author Steve Ebersole
  */
 public class FetchingScrollableResultsImpl extends AbstractScrollableResults {
 
 	public FetchingScrollableResultsImpl(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        SessionImplementor sess,
 	        Loader loader,
 	        QueryParameters queryParameters,
 	        Type[] types,
 	        HolderInstantiator holderInstantiator) throws MappingException {
 		super( rs, ps, sess, loader, queryParameters, types, holderInstantiator );
 	}
 
 	private Object[] currentRow = null;
 	private int currentPosition = 0;
 	private Integer maxPosition = null;
 
 	@Override
     protected Object[] getCurrentRow() {
 		return currentRow;
 	}
 
 	/**
 	 * Advance to the next result
 	 *
 	 * @return <tt>true</tt> if there is another result
 	 */
 	public boolean next() throws HibernateException {
 		if ( maxPosition != null && maxPosition.intValue() <= currentPosition ) {
 			currentRow = null;
 			currentPosition = maxPosition.intValue() + 1;
 			return false;
 		}
 
 		if ( isResultSetEmpty() ) {
 			currentRow = null;
 			currentPosition = 0;
 			return false;
 		}
 
 		Object row = getLoader().loadSequentialRowsForward(
 				getResultSet(),
 				getSession(),
 				getQueryParameters(),
 				false
 		);
 
 
 		boolean afterLast;
 		try {
 			afterLast = getResultSet().isAfterLast();
 		}
 		catch( SQLException e ) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "exception calling isAfterLast()"
 				);
 		}
 
 		currentPosition++;
 		currentRow = new Object[] { row };
 
 		if ( afterLast ) {
 			if ( maxPosition == null ) {
 				// we just hit the last position
-				maxPosition = new Integer( currentPosition );
+				maxPosition = currentPosition;
 			}
 		}
 
 		afterScrollOperation();
 
 		return true;
 	}
 
 	/**
 	 * Retreat to the previous result
 	 *
 	 * @return <tt>true</tt> if there is a previous result
 	 */
 	public boolean previous() throws HibernateException {
 		if ( currentPosition <= 1 ) {
 			currentPosition = 0;
 			currentRow = null;
 			return false;
 		}
 
 		Object loadResult = getLoader().loadSequentialRowsReverse(
 				getResultSet(),
 				getSession(),
 				getQueryParameters(),
 				false,
 		        ( maxPosition != null && currentPosition > maxPosition.intValue() )
 		);
 
 		currentRow = new Object[] { loadResult };
 		currentPosition--;
 
 		afterScrollOperation();
 
 		return true;
 
 	}
 
 	/**
 	 * Scroll an arbitrary number of locations
 	 *
 	 * @param positions a positive (forward) or negative (backward) number of rows
 	 *
 	 * @return <tt>true</tt> if there is a result at the new location
 	 */
 	public boolean scroll(int positions) throws HibernateException {
 		boolean more = false;
 		if ( positions > 0 ) {
 			// scroll ahead
 			for ( int i = 0; i < positions; i++ ) {
 				more = next();
 				if ( !more ) {
 					break;
 				}
 			}
 		}
 		else if ( positions < 0 ) {
 			// scroll backward
 			for ( int i = 0; i < ( 0 - positions ); i++ ) {
 				more = previous();
 				if ( !more ) {
 					break;
 				}
 			}
 		}
 		else {
 			throw new HibernateException( "scroll(0) not valid" );
 		}
 
 		afterScrollOperation();
 
 		return more;
 	}
 
 	/**
 	 * Go to the last result
 	 *
 	 * @return <tt>true</tt> if there are any results
 	 */
 	public boolean last() throws HibernateException {
 		boolean more = false;
 		if ( maxPosition != null ) {
 			if ( currentPosition > maxPosition.intValue() ) {
 				more = previous();
 			}
 			for ( int i = currentPosition; i < maxPosition.intValue(); i++ ) {
 				more = next();
 			}
 		}
 		else {
 			try {
 				if ( isResultSetEmpty() || getResultSet().isAfterLast() ) {
 					// should not be able to reach last without maxPosition being set
 					// unless there are no results
 					return false;
 				}
 
 				while ( !getResultSet().isAfterLast() ) {
 					more = next();
 				}
 			}
 			catch( SQLException e ) {
 				throw getSession().getFactory().getSQLExceptionHelper().convert(
 						e,
 						"exception calling isAfterLast()"
 					);
 			}
 		}
 
 		afterScrollOperation();
 
 		return more;
 	}
 
 	/**
 	 * Go to the first result
 	 *
 	 * @return <tt>true</tt> if there are any results
 	 */
 	public boolean first() throws HibernateException {
 		beforeFirst();
 		boolean more = next();
 
 		afterScrollOperation();
 
 		return more;
 	}
 
 	/**
 	 * Go to a location just before first result (this is the initial location)
 	 */
 	public void beforeFirst() throws HibernateException {
 		try {
 			getResultSet().beforeFirst();
 		}
 		catch( SQLException e ) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "exception calling beforeFirst()"
 				);
 		}
 		currentRow = null;
 		currentPosition = 0;
 	}
 
 	/**
 	 * Go to a location just after the last result
 	 */
 	public void afterLast() throws HibernateException {
 		// TODO : not sure the best way to handle this.
 		// The non-performant way :
 		last();
 		next();
 		afterScrollOperation();
 	}
 
 	/**
 	 * Is this the first result?
 	 *
 	 * @return <tt>true</tt> if this is the first row of results
 	 *
 	 * @throws org.hibernate.HibernateException
 	 */
 	public boolean isFirst() throws HibernateException {
 		return currentPosition == 1;
 	}
 
 	/**
 	 * Is this the last result?
 	 *
 	 * @return <tt>true</tt> if this is the last row of results
 	 *
 	 * @throws org.hibernate.HibernateException
 	 */
 	public boolean isLast() throws HibernateException {
 		if ( maxPosition == null ) {
 			// we have not yet hit the last result...
 			return false;
 		}
 		else {
 			return currentPosition == maxPosition.intValue();
 		}
 	}
 
 	/**
 	 * Get the current location in the result set. The first row is number <tt>0</tt>, contrary to JDBC.
 	 *
 	 * @return the row number, numbered from <tt>0</tt>, or <tt>-1</tt> if there is no current row
 	 */
 	public int getRowNumber() throws HibernateException {
 		return currentPosition;
 	}
 
 	/**
 	 * Set the current location in the result set, numbered from either the first row (row number <tt>0</tt>), or the last
 	 * row (row number <tt>-1</tt>).
 	 *
 	 * @param rowNumber the row number, numbered from the last row, in the case of a negative row number
 	 *
 	 * @return true if there is a row at that row number
 	 */
 	public boolean setRowNumber(int rowNumber) throws HibernateException {
 		if ( rowNumber == 1 ) {
 			return first();
 		}
 		else if ( rowNumber == -1 ) {
 			return last();
 		}
 		else if ( maxPosition != null && rowNumber == maxPosition.intValue() ) {
 			return last();
 		}
 		return scroll( rowNumber - currentPosition );
 	}
 
 	private boolean isResultSetEmpty() {
 		try {
 			return currentPosition == 0 && ! getResultSet().isBeforeFirst() && ! getResultSet().isAfterLast();
 		}
 		catch( SQLException e ) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "Could not determine if resultset is empty due to exception calling isBeforeFirst or isAfterLast()"
 			);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 824965b15e..5ff636db00 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1827 +1,1827 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  * 
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class,
 			AbstractCollectionPersister.class.getName() );
 
 	// TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	// SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final String sqlWhereString;
 	private final String sqlOrderByStringTemplate;
 	private final String sqlWhereStringTemplate;
 	private final boolean hasOrder;
 	protected final boolean hasWhere;
 	private final int baseIndex;
 
 	private final String nodeName;
 	private final String elementNodeName;
 	private final String indexNodeName;
 
 	protected final boolean indexContainsFormula;
 	protected final boolean elementIsPureFormula;
 
 	// types
 	private final Type keyType;
 	private final Type indexType;
 	protected final Type elementType;
 	private final Type identifierType;
 
 	// columns
 	protected final String[] keyColumnNames;
 	protected final String[] indexColumnNames;
 	protected final String[] indexFormulaTemplates;
 	protected final String[] indexFormulas;
 	protected final boolean[] indexColumnIsSettable;
 	protected final String[] elementColumnNames;
 	protected final String[] elementColumnWriters;
 	protected final String[] elementColumnReaders;
 	protected final String[] elementColumnReaderTemplates;
 	protected final String[] elementFormulaTemplates;
 	protected final String[] elementFormulas;
 	protected final boolean[] elementColumnIsSettable;
 	protected final boolean[] elementColumnIsInPrimaryKey;
 	protected final String[] indexColumnAliases;
 	protected final String[] elementColumnAliases;
 	protected final String[] keyColumnAliases;
 
 	protected final String identifierColumnName;
 	private final String identifierColumnAlias;
 	// private final String unquotedIdentifierColumnName;
 
 	protected final String qualifiedTableName;
 
 	private final String queryLoaderName;
 
 	private final boolean isPrimitiveArray;
 	private final boolean isArray;
 	protected final boolean hasIndex;
 	protected final boolean hasIdentifier;
 	private final boolean isLazy;
 	private final boolean isExtraLazy;
 	private final boolean isInverse;
 	private final boolean isMutable;
 	private final boolean isVersioned;
 	protected final int batchSize;
 	private final FetchMode fetchMode;
 	private final boolean hasOrphanDelete;
 	private final boolean subselectLoadable;
 
 	// extra information about the element type
 	private final Class elementClass;
 	private final String entityName;
 
 	private final Dialect dialect;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final SessionFactoryImplementor factory;
 	private final EntityPersister ownerPersister;
 	private final IdentifierGenerator identifierGenerator;
 	private final PropertyMapping elementPropertyMapping;
 	private final EntityPersister elementPersister;
 	private final CollectionRegionAccessStrategy cacheAccessStrategy;
 	private final CollectionType collectionType;
 	private CollectionInitializer initializer;
 
 	private final CacheEntryStructure cacheEntryStructure;
 
 	// dynamic filters for the collection
 	private final FilterHelper filterHelper;
 
 	// dynamic filters specifically for many-to-many inside the collection
 	private final FilterHelper manyToManyFilterHelper;
 
 	private final String manyToManyWhereString;
 	private final String manyToManyWhereTemplate;
 
 	private final boolean hasManyToManyOrder;
 	private final String manyToManyOrderByTemplate;
 
 	// custom sql
 	private final boolean insertCallable;
 	private final boolean updateCallable;
 	private final boolean deleteCallable;
 	private final boolean deleteAllCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private final Serializable[] spaces;
 
 	private Map collectionPropertyColumnAliases = new HashMap();
 	private Map collectionPropertyColumnNames = new HashMap();
 
 	public AbstractCollectionPersister(
 			final Collection collection,
 			final CollectionRegionAccessStrategy cacheAccessStrategy,
 			final Configuration cfg,
 			final SessionFactoryImplementor factory) throws MappingException, CacheException {
 
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collection.isMap() ?
 					(CacheEntryStructure) new StructuredMapCacheEntry() :
 					(CacheEntryStructure) new StructuredCollectionCacheEntry();
 		}
 		else {
 			cacheEntryStructure = new UnstructuredCacheEntry();
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collection.getCollectionType();
 		role = collection.getRole();
 		entityName = collection.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister( entityName );
 		queryLoaderName = collection.getLoaderName();
 		nodeName = collection.getNodeName();
 		isMutable = collection.isMutable();
 
 		Table table = collection.getCollectionTable();
 		fetchMode = collection.getElement().getFetchMode();
 		elementType = collection.getElement().getType();
 		// isSet = collection.isSet();
 		// isSorted = collection.isSorted();
 		isPrimitiveArray = collection.isPrimitiveArray();
 		isArray = collection.isArray();
 		subselectLoadable = collection.isSubselectLoadable();
 
 		qualifiedTableName = table.getQualifiedName(
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 				);
 
 		int spacesSize = 1 + collection.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collection.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collection.getWhere() ) ? "( " + collection.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate( sqlWhereString, dialect, factory.getSqlFunctionRegistry() ) :
 				null;
 
 		hasOrphanDelete = collection.hasOrphanDelete();
 
 		int batch = collection.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collection.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collection.getKey().getType();
 		iter = collection.getKey().getColumnIterator();
 		int keySpan = collection.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName( dialect );
 			keyColumnAliases[k] = col.getAlias( dialect, collection.getOwner().getRootTable() );
 			k++;
 		}
 
 		// unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		// ELEMENT
 
 		String elemNode = collection.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister( entityName );
 			if ( elemNode == null ) {
 				elemNode = cfg.getClassMapping( entityName ).getNodeName();
 			}
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
 		elementNodeName = elemNode;
 
 		int elementSpan = collection.getElement().getColumnSpan();
 		elementColumnAliases = new String[elementSpan];
 		elementColumnNames = new String[elementSpan];
 		elementColumnWriters = new String[elementSpan];
 		elementColumnReaders = new String[elementSpan];
 		elementColumnReaderTemplates = new String[elementSpan];
 		elementFormulaTemplates = new String[elementSpan];
 		elementFormulas = new String[elementSpan];
 		elementColumnIsSettable = new boolean[elementSpan];
 		elementColumnIsInPrimaryKey = new boolean[elementSpan];
 		boolean isPureFormula = true;
 		boolean hasNotNullableColumns = false;
 		int j = 0;
 		iter = collection.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias( dialect );
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName( dialect );
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr( dialect );
 				elementColumnReaderTemplates[j] = col.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 				elementColumnIsSettable[j] = true;
 				elementColumnIsInPrimaryKey[j] = !col.isNullable();
 				if ( !col.isNullable() ) {
 					hasNotNullableColumns = true;
 				}
 				isPureFormula = false;
 			}
 			j++;
 		}
 		elementIsPureFormula = isPureFormula;
 
 		// workaround, for backward compatibility of sets with no
 		// not-null columns, assume all columns are used in the
 		// row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collection.isIndexed();
 		if ( hasIndex ) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collection;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
 			indexColumnIsSettable = new boolean[indexSpan];
 			indexColumnAliases = new String[indexSpan];
 			int i = 0;
 			boolean hasFormula = false;
 			while ( iter.hasNext() ) {
 				Selectable s = (Selectable) iter.next();
 				indexColumnAliases[i] = s.getAlias( dialect );
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName( dialect );
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
 
 			indexNodeName = indexedCollection.getIndexNodeName();
 
 		}
 		else {
 			indexContainsFormula = false;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
 			indexNodeName = null;
 		}
 
 		hasIdentifier = collection.isIdentified();
 		if ( hasIdentifier ) {
 			if ( collection.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collection;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = (Column) iter.next();
 			identifierColumnName = col.getQuotedName( dialect );
 			identifierColumnAlias = col.getAlias( dialect );
 			// unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					cfg.getIdentifierGeneratorFactory(),
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName(),
 					null
 					);
 		}
 		else {
 			identifierType = null;
 			identifierColumnName = null;
 			identifierColumnAlias = null;
 			// unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 
 		// GENERATE THE SQL:
 
 		// sqlSelectString = sqlSelectString();
 		// sqlSelectRowString = sqlSelectRowString();
 
 		if ( collection.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collection.getCustomSQLInsert();
 			insertCallable = collection.isCustomInsertCallable();
 			insertCheckStyle = collection.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLInsert(), insertCallable )
 					: collection.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collection.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collection.getCustomSQLUpdate();
 			updateCallable = collection.isCustomUpdateCallable();
 			updateCheckStyle = collection.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLUpdate(), insertCallable )
 					: collection.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collection.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collection.getCustomSQLDelete();
 			deleteCallable = collection.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collection.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collection.getCustomSQLDeleteAll();
 			deleteAllCallable = collection.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString( collection.isIndexed() && !collection.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collection.isLazy();
 		isExtraLazy = collection.isExtraLazy();
 
 		isInverse = collection.isInverse();
 
 		if ( collection.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collection ).getElementClass();
 		}
 		else {
 			// for non-arrays, we don't need to know the element class
 			elementClass = null; // elementType.returnedClass();
 		}
 
 		if ( elementType.isComponentType() ) {
 			elementPropertyMapping = new CompositeElementPropertyMapping(
 					elementColumnNames,
 					elementColumnReaders,
 					elementColumnReaderTemplates,
 					elementFormulaTemplates,
 					(CompositeType) elementType,
 					factory
 					);
 		}
 		else if ( !elementType.isEntityType() ) {
 			elementPropertyMapping = new ElementPropertyMapping(
 					elementColumnNames,
 					elementType
 					);
 		}
 		else {
 			if ( elementPersister instanceof PropertyMapping ) { // not all classpersisters implement PropertyMapping!
 				elementPropertyMapping = (PropertyMapping) elementPersister;
 			}
 			else {
 				elementPropertyMapping = new ElementPropertyMapping(
 						elementColumnNames,
 						elementType
 						);
 			}
 		}
 
 		hasOrder = collection.getOrderBy() != null;
 		if ( hasOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			sqlOrderByStringTemplate = Template.renderOrderByStringTemplate(
 					collection.getOrderBy(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 					);
 		}
 		else {
 			sqlOrderByStringTemplate = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			manyToManyOrderByTemplate = Template.renderOrderByStringTemplate(
 					collection.getManyToManyOrdering(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 					);
 		}
 		else {
 			manyToManyOrderByTemplate = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Static SQL for collection: %s", getRole() );
 			if ( getSQLInsertRowString() != null ) LOG.debugf( " Row insert: %s", getSQLInsertRowString() );
 			if ( getSQLUpdateRowString() != null ) LOG.debugf( " Row update: %s", getSQLUpdateRowString() );
 			if ( getSQLDeleteRowString() != null ) LOG.debugf( " Row delete: %s", getSQLDeleteRowString() );
 			if ( getSQLDeleteString() != null ) LOG.debugf( " One-shot delete: %s", getSQLDeleteString() );
 		}
 	}
 
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			// if there is a user-specified loader, return that
 			// TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
 		if ( !isSubselectLoadable() ) {
 			return null;
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		SubselectFetch subselect = persistenceContext.getBatchFetchQueue()
 				.getSubselect( session.generateEntityKey( key, getOwnerEntityPersister() ) );
 
 		if ( subselect == null ) {
 			return null;
 		}
 		else {
 
 			// Take care of any entities that might have
 			// been evicted!
 			Iterator iter = subselect.getResult().iterator();
 			while ( iter.hasNext() ) {
 				if ( !persistenceContext.containsEntity( (EntityKey) iter.next() ) ) {
 					iter.remove();
 				}
 			}
 
 			// Run a subquery loader
 			return createSubselectInitializer( subselect, session );
 		}
 	}
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? StringHelper.replace( sqlOrderByStringTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? StringHelper.replace( manyToManyOrderByTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	public boolean hasWhere() {
 		return hasWhere;
 	}
 
 	protected String getSQLDeleteString() {
 		return sqlDeleteString;
 	}
 
 	protected String getSQLInsertRowString() {
 		return sqlInsertRowString;
 	}
 
 	protected String getSQLUpdateRowString() {
 		return sqlUpdateRowString;
 	}
 
 	protected String getSQLDeleteRowString() {
 		return sqlDeleteRowString;
 	}
 
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass() { // needed by arrays
 		return elementClass;
 	}
 
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object index = getIndexType().nullSafeGet( rs, aliases, session, null );
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
-			index = new Integer( ( (Integer) index ).intValue() - baseIndex );
+            index = (Integer)index - baseIndex;
 		}
 		return index;
 	}
 
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role ); // an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( indexColumnIsSettable );
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
-			index = new Integer( ( (Integer) index ).intValue() + baseIndex );
+            index = (Integer)index + baseIndex;
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( elementIsPureFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based element in the where condition" );
 		}
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsInPrimaryKey, session );
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( indexContainsFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based index in the where condition" );
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	public boolean isArray() {
 		return isArray;
 	}
 
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); // strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 				"max(" + getIndexColumnNames()[0] + ") + 1" : // lists, arrays
 				"count(" + getElementColumnNames()[0] + ")"; // sets, maps, bags
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn( selectValue )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i = 0; i < elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify( alias, elementColumnNames, elementFormulaTemplates );
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for ( int i = 0; i < span; i++ ) {
 			if ( columnNames[i] == null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			// Remove all the old entries
 
 			try {
 				int offset = 1;
 				PreparedStatement st = null;
 				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
 				boolean callable = isDeleteAllCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLDeleteString();
 				if ( useBatch ) {
 					if ( removeBatchKey == null ) {
 						removeBatchKey = new BasicBatchKey(
 								getRole() + "#REMOVE",
 								expectation
 								);
 					}
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						st.close();
 					}
 				}
 
 				LOG.debugf( "Done deleting collection" );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLDeleteString()
 						);
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			try {
 				// create all the new entries
 				Iterator entries = collection.entries( this );
 				if ( entries.hasNext() ) {
 					Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 					collection.preInsert( this );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isInsertCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLInsertRowString();
 
 							if ( useBatch ) {
 								if ( recreateBatchKey == null ) {
 									recreateBatchKey = new BasicBatchKey(
 											getRole() + "#RECREATE",
 											expectation
 											);
 								}
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
 							}
 
 							try {
 								offset += expectation.prepare( st );
 
 								// TODO: copy/paste from insertRows()
 								int loc = writeKey( st, id, offset, session );
 								if ( hasIdentifier ) {
 									loc = writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 								}
 								if ( hasIndex /* && !indexIsFormula */) {
 									loc = writeIndex( st, collection.getIndex( entry, i, this ), loc, session );
 								}
 								loc = writeElement( st, collection.getElement( entry ), loc, session );
 
 								if ( useBatch ) {
 									session.getTransactionCoordinator()
 											.getJdbcCoordinator()
 											.getBatch( recreateBatchKey )
 											.addToBatch();
 								}
 								else {
 									expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 								}
 
 								collection.afterRowInsert( this, entry, i );
 								count++;
 							}
 							catch ( SQLException sqle ) {
 								if ( useBatch ) {
 									session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 								}
 								throw sqle;
 							}
 							finally {
 								if ( !useBatch ) {
 									st.close();
 								}
 							}
 
 						}
 						i++;
 					}
 
 					LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 				}
 				else {
 					LOG.debugf( "Collection was empty" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLInsertRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting rows of collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 			final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 			try {
 				// delete all the deleted entries
 				Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 				if ( deletes.hasNext() ) {
 					int offset = 1;
 					int count = 0;
 					while ( deletes.hasNext() ) {
 						PreparedStatement st = null;
 						boolean callable = isDeleteCallable();
 						boolean useBatch = expectation.canBeBatched();
 						String sql = getSQLDeleteRowString();
 
 						if ( useBatch ) {
 							if ( deleteBatchKey == null ) {
 								deleteBatchKey = new BasicBatchKey(
 										getRole() + "#DELETE",
 										expectation
 										);
 							}
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							expectation.prepare( st );
 
 							Object entry = deletes.next();
 							int loc = offset;
 							if ( hasIdentifier ) {
 								writeIdentifier( st, entry, loc, session );
 							}
 							else {
 								loc = writeKey( st, id, loc, session );
 								if ( deleteByIndex ) {
 									writeIndexToWhere( st, entry, loc, session );
 								}
 								else {
 									writeElementToWhere( st, entry, loc, session );
 								}
 							}
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								st.close();
 							}
 						}
 
 						LOG.debugf( "Done deleting collection rows: %s deleted", count );
 					}
 				}
 				else {
 					LOG.debugf( "No rows to delete" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection rows: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLDeleteRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey insertBatchKey;
 
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) LOG.debugf( "Inserting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, id, getFactory() ) );
 
 			try {
 				// insert all the new entries
 				collection.preInsert( this );
 				Iterator entries = collection.entries( this );
 				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean callable = isInsertCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLInsertRowString();
 				int i = 0;
 				int count = 0;
 				while ( entries.hasNext() ) {
 					int offset = 1;
 					Object entry = entries.next();
 					PreparedStatement st = null;
 					if ( collection.needsInserting( entry, i, elementType ) ) {
 
 						if ( useBatch ) {
 							if ( insertBatchKey == null ) {
 								insertBatchKey = new BasicBatchKey(
 										getRole() + "#INSERT",
 										expectation
 										);
 							}
 							if ( st == null ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( insertBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							offset += expectation.prepare( st );
 							// TODO: copy/paste from recreate()
 							offset = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 							}
 							if ( hasIndex /* && !indexIsFormula */) {
 								offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 							}
 							writeElement( st, collection.getElement( entry ), offset, session );
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								st.close();
 							}
 						}
 					}
 					i++;
 				}
 				LOG.debugf( "Done inserting rows: %s inserted", count );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection rows: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLInsertRowString()
 						);
 			}
 
 		}
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	public abstract boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, alias, enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			if ( indexFragments == null ) {
 				String[] tmp = new String[indexColumnNames.length];
 				for ( int i = 0; i < indexColumnNames.length; i++ ) {
 					tmp[i] = indexColumnNames[i] == null
 							? indexFormulas[i]
 							: indexColumnNames[i];
 					indexFragments = tmp;
 				}
 			}
 			return indexFragments;
 		}
 
 		return elementPropertyMapping.toColumns( propertyName );
 	}
 
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
 	public String getName() {
 		return getRole();
 	}
 
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
 	public boolean isCollection() {
 		return true;
 	}
 
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			LOG.debugf( "Updating rows of collection: %s#%s", role, id );
 
 			// update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			LOG.debugf( "Done updating rows: %s updated", count );
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException;
 
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, alias, enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	protected boolean isInsertCallable() {
 		return insertCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	protected boolean isUpdateCallable() {
 		return updateCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	protected boolean isDeleteCallable() {
 		return deleteCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	protected boolean isDeleteAllCallable() {
 		return deleteAllCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public String getIndexNodeName() {
 		return indexNodeName;
 	}
 
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SqlExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return filterHelper.isAffectedBy( session.getEnabledFilters() ) ||
 				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	// TODO: formulas ?
 	public void initCollectionPropertyMap() {
 
 		initCollectionPropertyMap( "key", keyType, keyColumnAliases, keyColumnNames );
 		initCollectionPropertyMap( "element", elementType, elementColumnAliases, elementColumnNames );
 		if ( hasIndex ) {
 			initCollectionPropertyMap( "index", indexType, indexColumnAliases, indexColumnNames );
 		}
 		if ( hasIdentifier ) {
 			initCollectionPropertyMap(
 					"id",
 					identifierType,
 					new String[] { identifierColumnAlias },
 					new String[] { identifierColumnName } );
 		}
 	}
 
 	private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
 
 		collectionPropertyColumnAliases.put( aliasName, columnAliases );
 		collectionPropertyColumnNames.put( aliasName, columnNames );
 
 		if ( type.isComponentType() ) {
 			CompositeType ct = (CompositeType) type;
 			String[] propertyNames = ct.getPropertyNames();
 			for ( int i = 0; i < propertyNames.length; i++ ) {
 				String name = propertyNames[i];
 				collectionPropertyColumnAliases.put( aliasName + "." + name, columnAliases[i] );
 				collectionPropertyColumnNames.put( aliasName + "." + name, columnNames[i] );
 			}
 		}
 
 	}
 
 	public int getSize(Serializable key, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 		return exists( key, element, getElementType(), sqlDetectRowByElementString, session );
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next();
 				}
 				finally {
 					rs.close();
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				getIndexType().nullSafeSet( st, incrementIndexByBase( index ), keyColumnNames.length + 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					rs.close();
 				}
 			}
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
index be3424288d..e2b9d4eb2d 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
@@ -1,137 +1,137 @@
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
 package org.hibernate.proxy.pojo;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.proxy.AbstractLazyInitializer;
 import org.hibernate.type.CompositeType;
 
 /**
  * Lazy initializer for POJOs
  *
  * @author Gavin King
  */
 public abstract class BasicLazyInitializer extends AbstractLazyInitializer {
 
 	protected static final Object INVOKE_IMPLEMENTATION = new MarkerObject("INVOKE_IMPLEMENTATION");
 
 	protected Class persistentClass;
 	protected Method getIdentifierMethod;
 	protected Method setIdentifierMethod;
 	protected boolean overridesEquals;
 	private Object replacement;
 	protected CompositeType componentIdType;
 
 	protected BasicLazyInitializer(
 			String entityName,
 	        Class persistentClass,
 	        Serializable id,
 	        Method getIdentifierMethod,
 	        Method setIdentifierMethod,
 	        CompositeType componentIdType,
 	        SessionImplementor session) {
 		super(entityName, id, session);
 		this.persistentClass = persistentClass;
 		this.getIdentifierMethod = getIdentifierMethod;
 		this.setIdentifierMethod = setIdentifierMethod;
 		this.componentIdType = componentIdType;
 		overridesEquals = ReflectHelper.overridesEquals(persistentClass);
 	}
 
 	protected abstract Object serializableProxy();
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	protected final Object invoke(Method method, Object[] args, Object proxy) throws Throwable {
 		String methodName = method.getName();
 		int params = args.length;
 
 		if ( params==0 ) {
 			if ( "writeReplace".equals(methodName) ) {
 				return getReplacement();
 			}
 			else if ( !overridesEquals && "hashCode".equals(methodName) ) {
 				return Integer.valueOf( System.identityHashCode(proxy) );
 			}
 			else if ( isUninitialized() && method.equals(getIdentifierMethod) ) {
 				return getIdentifier();
 			}
 			else if ( "getHibernateLazyInitializer".equals(methodName) ) {
 				return this;
 			}
 		}
 		else if ( params==1 ) {
 			if ( !overridesEquals && "equals".equals(methodName) ) {
-				return args[0]==proxy ? Boolean.TRUE : Boolean.FALSE;
+				return args[0]==proxy;
 			}
 			else if ( method.equals(setIdentifierMethod) ) {
 				initialize();
 				setIdentifier( (Serializable) args[0] );
 				return INVOKE_IMPLEMENTATION;
 			}
 		}
 
 		//if it is a property of an embedded component, invoke on the "identifier"
 		if ( componentIdType!=null && componentIdType.isMethodOf(method) ) {
 			return method.invoke( getIdentifier(), args );
 		}
 
 		// otherwise:
 		return INVOKE_IMPLEMENTATION;
 
 	}
 
 	private Object getReplacement() {
 		final SessionImplementor session = getSession();
 		if ( isUninitialized() && session != null && session.isOpen()) {
 			final EntityKey key = session.generateEntityKey(
 					getIdentifier(),
 					session.getFactory().getEntityPersister( getEntityName() )
 			);
 			final Object entity = session.getPersistenceContext().getEntity(key);
 			if (entity!=null) setImplementation( entity );
 		}
 
 		if ( isUninitialized() ) {
 			if (replacement==null) {
 				replacement = serializableProxy();
 			}
 			return replacement;
 		}
 		else {
 			return getTarget();
 		}
 
 	}
 
 	public final Class getPersistentClass() {
 		return persistentClass;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ByteType.java b/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
index 5845f4412c..93847f0600 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ByteType.java
@@ -1,94 +1,94 @@
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
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.descriptor.java.ByteTypeDescriptor;
 import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor;
 
 /**
  * A type that maps between {@link java.sql.Types#TINYINT TINYINT} and {@link Byte}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "UnnecessaryBoxing" })
 public class ByteType
 		extends AbstractSingleColumnStandardBasicType<Byte>
 		implements PrimitiveType<Byte>, DiscriminatorType<Byte>, VersionType<Byte> {
 
 	public static final ByteType INSTANCE = new ByteType();
 
-	private static final Byte ZERO = new Byte( (byte) 0 );
+	private static final Byte ZERO = Byte.valueOf( (byte)0 );
 
 	public ByteType() {
 		super( TinyIntTypeDescriptor.INSTANCE, ByteTypeDescriptor.INSTANCE );
 	}
 
 	public String getName() {
 		return "byte";
 	}
 
 	@Override
 	public String[] getRegistrationKeys() {
 		return new String[] { getName(), byte.class.getName(), Byte.class.getName() };
 	}
 
 	public Serializable getDefaultValue() {
 		return ZERO;
 	}
 
 	public Class getPrimitiveClass() {
 		return byte.class;
 	}
 
 	public String objectToSQLString(Byte value, Dialect dialect) {
 		return toString( value );
 	}
 
 	public Byte stringToObject(String xml) {
 		return fromString( xml );
 	}
 
 	public Byte fromStringValue(String xml) {
 		return fromString( xml );
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public Byte next(Byte current, SessionImplementor session) {
 		return Byte.valueOf( (byte) ( current.byteValue() + 1 ) );
 	}
 
 	public Byte seed(SessionImplementor session) {
 		return ZERO;
 	}
 
 	public Comparator<Byte> getComparator() {
 		return getJavaTypeDescriptor().getComparator();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/LongType.java b/hibernate-core/src/main/java/org/hibernate/type/LongType.java
index 604c9cc048..f0e526f48b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/LongType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/LongType.java
@@ -1,90 +1,90 @@
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
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.descriptor.java.LongTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BigIntTypeDescriptor;
 
 /**
  * A type that maps between {@link java.sql.Types#BIGINT BIGINT} and {@link Long}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class LongType
 		extends AbstractSingleColumnStandardBasicType<Long>
 		implements PrimitiveType<Long>, DiscriminatorType<Long>, VersionType<Long> {
 
 	public static final LongType INSTANCE = new LongType();
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	private static final Long ZERO = Long.valueOf( 0 );
 
 	public LongType() {
 		super( BigIntTypeDescriptor.INSTANCE, LongTypeDescriptor.INSTANCE );
 	}
 
 	public String getName() {
 		return "long";
 	}
 
 	@Override
 	public String[] getRegistrationKeys() {
 		return new String[] { getName(), long.class.getName(), Long.class.getName() };
 	}
 
 	public Serializable getDefaultValue() {
 		return ZERO;
 	}
 
 	public Class getPrimitiveClass() {
 		return long.class;
 	}
 
 	public Long stringToObject(String xml) throws Exception {
-		return new Long(xml);
+		return Long.valueOf( xml );
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing", "UnnecessaryUnboxing" })
 	public Long next(Long current, SessionImplementor session) {
-		return Long.valueOf( current.longValue() + 1 );
+		return current + 1l;
 	}
 
 	public Long seed(SessionImplementor session) {
 		return ZERO;
 	}
 
 	public Comparator<Long> getComparator() {
 		return getJavaTypeDescriptor().getComparator();
 	}
 	
 	public String objectToSQLString(Long value, Dialect dialect) throws Exception {
 		return value.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ShortType.java b/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
index 61ce855352..296448d411 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ShortType.java
@@ -1,91 +1,91 @@
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
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.descriptor.java.ShortTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 
 /**
  * A type that maps between {@link java.sql.Types#SMALLINT SMALLINT} and {@link Short}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class ShortType
 		extends AbstractSingleColumnStandardBasicType<Short>
 		implements PrimitiveType<Short>, DiscriminatorType<Short>, VersionType<Short> {
 
 	public static final ShortType INSTANCE = new ShortType();
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	private static final Short ZERO = Short.valueOf( (short) 0 );
 
 	public ShortType() {
 		super( SmallIntTypeDescriptor.INSTANCE, ShortTypeDescriptor.INSTANCE );
 	}
 
 	public String getName() {
 		return "short";
 	}
 
 	@Override
 	public String[] getRegistrationKeys() {
 		return new String[] { getName(), short.class.getName(), Short.class.getName() };
 	}
 
 	public Serializable getDefaultValue() {
 		return ZERO;
 	}
 	
 	public Class getPrimitiveClass() {
 		return short.class;
 	}
 
 	public String objectToSQLString(Short value, Dialect dialect) throws Exception {
 		return value.toString();
 	}
 
 	public Short stringToObject(String xml) throws Exception {
-		return new Short(xml);
+		return Short.valueOf( xml );
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing", "UnnecessaryUnboxing" })
 	public Short next(Short current, SessionImplementor session) {
 		return Short.valueOf( (short) ( current.shortValue() + 1 ) );
 	}
 
 	public Short seed(SessionImplementor session) {
 		return ZERO;
 	}
 
 	public Comparator<Short> getComparator() {
 		return getJavaTypeDescriptor().getComparator();
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractQueryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractQueryImpl.java
index 88df1b9b3e..7b806dadaa 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractQueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractQueryImpl.java
@@ -1,412 +1,412 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 import static org.hibernate.ejb.QueryHints.HINT_CACHEABLE;
 import static org.hibernate.ejb.QueryHints.HINT_CACHE_MODE;
 import static org.hibernate.ejb.QueryHints.HINT_CACHE_REGION;
 import static org.hibernate.ejb.QueryHints.HINT_COMMENT;
 import static org.hibernate.ejb.QueryHints.HINT_FETCH_SIZE;
 import static org.hibernate.ejb.QueryHints.HINT_FLUSH_MODE;
 import static org.hibernate.ejb.QueryHints.HINT_READONLY;
 import static org.hibernate.ejb.QueryHints.HINT_TIMEOUT;
 import static org.hibernate.ejb.QueryHints.SPEC_HINT_TIMEOUT;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.FlushModeType;
 import javax.persistence.Parameter;
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.TypedQuery;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.ejb.internal.EntityManagerMessageLogger;
 import org.hibernate.ejb.util.CacheModeHelper;
 import org.hibernate.ejb.util.ConfigurationHelper;
 import org.hibernate.ejb.util.LockModeTypeHelper;
 import org.hibernate.hql.internal.QueryExecutionRequestException;
 import org.jboss.logging.Logger;
 
 /**
  * Intended as a base class providing convenience in implementing both {@link javax.persistence.Query} and
  * {@link javax.persistence.TypedQuery}.
  * <p/>
  * IMPL NOTE : This issue, and the reason for this distinction, is that criteria and hl.sql queries share no
  * commonality currently in Hibernate internals.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractQueryImpl<X> implements TypedQuery<X> {
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                            AbstractQueryImpl.class.getName());
 
 	private final HibernateEntityManagerImplementor entityManager;
 
 	public AbstractQueryImpl(HibernateEntityManagerImplementor entityManager) {
 		this.entityManager = entityManager;
 	}
 
 	protected HibernateEntityManagerImplementor getEntityManager() {
 		return entityManager;
 	}
 
 	/**
 	 * Actually execute the update; all pre-requisites have been checked.
 	 *
 	 * @return The number of "affected rows".
 	 */
 	protected abstract int internalExecuteUpdate();
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "ThrowableInstanceNeverThrown" })
 	public int executeUpdate() {
 		try {
 			if ( ! entityManager.isTransactionInProgress() ) {
 				entityManager.throwPersistenceException( new TransactionRequiredException( "Executing an update/delete query" ) );
 				return 0;
 			}
 			return internalExecuteUpdate();
 		}
 		catch ( QueryExecutionRequestException he) {
 			throw new IllegalStateException(he);
 		}
 		catch( TypeMismatchException e ) {
 			throw new IllegalArgumentException(e);
 		}
 		catch ( HibernateException he) {
 			entityManager.throwPersistenceException( he );
 			return 0;
 		}
 	}
 
 	private int maxResults = -1;
 
 	/**
 	 * Apply the given max results value.
 	 *
 	 * @param maxResults The specified max results
 	 */
 	protected abstract void applyMaxResults(int maxResults);
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setMaxResults(int maxResult) {
 		if ( maxResult < 0 ) {
 			throw new IllegalArgumentException(
 					"Negative value (" + maxResult + ") passed to setMaxResults"
 			);
 		}
 		this.maxResults = maxResult;
 		applyMaxResults( maxResult );
 		return this;
 	}
 
 	public int getSpecifiedMaxResults() {
 		return maxResults;
 	}
 
 	public int getMaxResults() {
 		return maxResults == -1
 				? Integer.MAX_VALUE // stupid spec... MAX_VALUE??
 				: maxResults;
 	}
 
 	private int firstResult;
 
 	/**
 	 * Apply the given first-result value.
 	 *
 	 * @param firstResult The specified first-result value.
 	 */
 	protected abstract void applyFirstResult(int firstResult);
 
 	public TypedQuery<X> setFirstResult(int firstResult) {
 		if ( firstResult < 0 ) {
 			throw new IllegalArgumentException(
 					"Negative value (" + firstResult + ") passed to setFirstResult"
 			);
 		}
 		this.firstResult = firstResult;
 		applyFirstResult( firstResult );
 		return this;
 	}
 
 	public int getFirstResult() {
 		return firstResult;
 	}
 
 	private Map<String, Object> hints;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Map<String, Object> getHints() {
 		return hints;
 	}
 
 	protected abstract void applyTimeout(int timeout);
 
 	protected abstract void applyComment(String comment);
 
 	protected abstract void applyFetchSize(int fetchSize);
 
 	protected abstract void applyCacheable(boolean isCacheable);
 
 	protected abstract void applyCacheRegion(String regionName);
 
 	protected abstract void applyReadOnly(boolean isReadOnly);
 
 	protected abstract void applyCacheMode(CacheMode cacheMode);
 
 	protected abstract void applyFlushMode(FlushMode flushMode);
 
 	protected abstract boolean canApplyLockModes();
 
 	protected abstract void applyAliasSpecificLockMode(String alias, LockMode lockMode);
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setHint(String hintName, Object value) {
 		boolean skipped = false;
 		try {
 			if ( HINT_TIMEOUT.equals( hintName ) ) {
 				applyTimeout( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( SPEC_HINT_TIMEOUT.equals( hintName ) ) {
 				// convert milliseconds to seconds
 				int timeout = (int)Math.round(ConfigurationHelper.getInteger( value ).doubleValue() / 1000.0 );
-				applyTimeout( new Integer(timeout) );
+				applyTimeout( timeout );
 			}
 			else if ( HINT_COMMENT.equals( hintName ) ) {
 				applyComment( (String) value );
 			}
 			else if ( HINT_FETCH_SIZE.equals( hintName ) ) {
 				applyFetchSize( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( HINT_CACHEABLE.equals( hintName ) ) {
 				applyCacheable( ConfigurationHelper.getBoolean( value ) );
 			}
 			else if ( HINT_CACHE_REGION.equals( hintName ) ) {
 				applyCacheRegion( (String) value );
 			}
 			else if ( HINT_READONLY.equals( hintName ) ) {
 				applyReadOnly( ConfigurationHelper.getBoolean( value ) );
 			}
 			else if ( HINT_CACHE_MODE.equals( hintName ) ) {
 				applyCacheMode( ConfigurationHelper.getCacheMode( value ) );
 			}
 			else if ( HINT_FLUSH_MODE.equals( hintName ) ) {
 				applyFlushMode( ConfigurationHelper.getFlushMode( value ) );
 			}
 			else if ( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE.equals( hintName ) ) {
 				final CacheRetrieveMode retrieveMode = (CacheRetrieveMode) value;
 
 				CacheStoreMode storeMode = hints != null
 						? (CacheStoreMode) hints.get( AvailableSettings.SHARED_CACHE_STORE_MODE )
 						: null;
 				if ( storeMode == null ) {
 					storeMode = (CacheStoreMode) entityManager.getProperties()
 							.get( AvailableSettings.SHARED_CACHE_STORE_MODE );
 				}
 				applyCacheMode(
 						CacheModeHelper.interpretCacheMode( storeMode, retrieveMode )
 				);
 			}
 			else if ( AvailableSettings.SHARED_CACHE_STORE_MODE.equals( hintName ) ) {
 				final CacheStoreMode storeMode = (CacheStoreMode) value;
 
 				CacheRetrieveMode retrieveMode = hints != null
 						? (CacheRetrieveMode) hints.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE )
 						: null;
 				if ( retrieveMode == null ) {
 					retrieveMode = (CacheRetrieveMode) entityManager.getProperties()
 							.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 				}
 				applyCacheMode(
 						CacheModeHelper.interpretCacheMode( storeMode, retrieveMode )
 				);
 			}
 			else if ( hintName.startsWith( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE ) ) {
 				if ( ! canApplyLockModes() ) {
 					skipped = true;
 				}
 				else {
 					// extract the alias
 					final String alias = hintName.substring( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE.length() + 1 );
 					// determine the LockMode
 					try {
 						final LockMode lockMode = LockModeTypeHelper.interpretLockMode( value );
 						applyAliasSpecificLockMode( alias, lockMode );
 					}
 					catch ( Exception e ) {
                         LOG.unableToDetermineLockModeValue(hintName, value);
 						skipped = true;
 					}
 				}
 			}
 			else {
 				skipped = true;
                 LOG.ignoringUnrecognizedQueryHint(hintName);
 			}
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( "Value for hint" );
 		}
 
 		if ( !skipped ) {
 			if ( hints == null ) {
 				hints = new HashMap<String,Object>();
 			}
 			hints.put( hintName, value );
 		}
 
 		return this;
 	}
 
 	public Set<String> getSupportedHints() {
 		return QueryHints.getDefinedHints();
 	}
 
 	public abstract TypedQuery<X> setLockMode(javax.persistence.LockModeType lockModeType);
 
 	public abstract javax.persistence.LockModeType getLockMode();
 
 	private FlushModeType jpaFlushMode;
 
 	public TypedQuery<X> setFlushMode(FlushModeType jpaFlushMode) {
 		this.jpaFlushMode = jpaFlushMode;
 		// TODO : treat as hint?
 		if ( jpaFlushMode == FlushModeType.AUTO ) {
 			applyFlushMode( FlushMode.AUTO );
 		}
 		else if ( jpaFlushMode == FlushModeType.COMMIT ) {
 			applyFlushMode( FlushMode.COMMIT );
 		}
 		return this;
 	}
 
 	protected FlushModeType getSpecifiedFlushMode() {
 		return jpaFlushMode;
 	}
 
 	public FlushModeType getFlushMode() {
 		return jpaFlushMode != null
 				? jpaFlushMode
 				: entityManager.getFlushMode();
 	}
 
 	private Map parameterBindings;
 
 	protected void registerParameterBinding(Parameter parameter, Object value) {
 		if ( value != null && parameter.getParameterType() != null ) {
 			if ( Collection.class.isInstance( value ) ) {
 				final Collection collection = (Collection) value;
 				// validate the elements...
 				for ( Object element : collection ) {
 					if ( ! parameter.getParameterType().isInstance( element ) ) {
 						throw new IllegalArgumentException(
 								"Parameter value [" + element + "] was not matching type [" +
 										parameter.getParameterType().getName() + "]"
 						);
 					}
 				}
 			}
 			else if ( value.getClass().isArray() && value.getClass().equals( Object[].class ) ) {
 				final Object[] array = (Object[]) value;
 				for ( Object element : array ) {
 					if ( ! parameter.getParameterType().isInstance( element ) ) {
 						throw new IllegalArgumentException(
 								"Parameter value [" + element + "] was not matching type [" +
 										parameter.getParameterType().getName() + "]"
 						);
 					}
 				}
 			}
 			else {
 				if ( ! parameter.getParameterType().isInstance( value ) ) {
 					throw new IllegalArgumentException(
 							"Parameter value [" + value + "] was not matching type [" +
 									parameter.getParameterType().getName() + "]"
 					);
 				}
 			}
 		}
 
 		if ( parameterBindings == null ) {
 			parameterBindings = new HashMap();
 		}
 		parameterBindings.put( parameter, value );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
     public boolean isBound(Parameter<?> param) {
 		return parameterBindings != null && parameterBindings.containsKey( param );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <T> T getParameterValue(Parameter<T> param) {
 		if ( parameterBindings == null ) {
 			throw new IllegalStateException( "No parameters have been bound" );
 		}
 		try {
 			T value = (T) parameterBindings.get( param );
 			if ( value == null ) {
 				throw new IllegalStateException( "Parameter has not been bound" );
 			}
 			return value;
 		}
 		catch ( ClassCastException cce ) {
 			throw new IllegalStateException( "Encountered a parameter value type exception" );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object getParameterValue(String name) {
 		return getParameterValue( getParameter( name ) );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object getParameterValue(int position) {
 		return getParameterValue( getParameter( position ) );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
index 1144591026..054bac22f4 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1634 +1,1634 @@
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
 import org.hibernate.service.BootstrapServiceRegistryBuilder;
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
-				isJTA = info.getJtaDataSource() != null ? Boolean.TRUE : Boolean.FALSE;
+				isJTA = info.getJtaDataSource() != null;
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
 		return buildEntityManagerFactory( new BootstrapServiceRegistryBuilder() );
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory(BootstrapServiceRegistryBuilder builder) {
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
