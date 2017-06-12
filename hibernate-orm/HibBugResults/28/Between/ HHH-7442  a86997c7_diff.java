diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
index 5390a3c482..c95b7a6401 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
@@ -1,147 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 import java.util.Arrays;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Defines a key for caching natural identifier resolutions into the second level cache.
  *
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
 public class NaturalIdCacheKey implements Serializable {
 	private final Serializable[] naturalIdValues;
 	private final String entityName;
 	private final String tenantId;
 	private final int hashCode;
-	private final Value<String> toString;
+	private final ValueHolder<String> toString;
 
 	/**
 	 * Construct a new key for a caching natural identifier resolutions into the second level cache.
 	 * Note that an entity name should always be the root entity name, not a subclass entity name.
 	 *
 	 * @param naturalIdValues The naturalIdValues associated with the cached data
 	 * @param persister The persister for the entity
 	 * @param session The originating session
 	 */
 	public NaturalIdCacheKey(
 			final Object[] naturalIdValues,
 			final EntityPersister persister,
 			final SessionImplementor session) {
 
 		this.entityName = persister.getRootEntityName();
 		this.tenantId = session.getTenantIdentifier();
 
 		this.naturalIdValues = new Serializable[naturalIdValues.length];
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Type[] propertyTypes = persister.getPropertyTypes();
 
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ( ( this.entityName == null ) ? 0 : this.entityName.hashCode() );
 		result = prime * result + ( ( this.tenantId == null ) ? 0 : this.tenantId.hashCode() );
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			final Type type = propertyTypes[naturalIdPropertyIndexes[i]];
 			final Object value = naturalIdValues[i];
 			
 			result = prime * result + (value != null ? type.getHashCode( value, factory ) : 0);
 			
 			this.naturalIdValues[i] = type.disassemble( value, session, null );
 		}
 		
 		this.hashCode = result;
-		this.toString = new Value<String>(
-				new Value.DeferredInitializer<String>() {
+		this.toString = new ValueHolder<String>(
+				new ValueHolder.DeferredInitializer<String>() {
 					@Override
 					public String initialize() {
 						//Complex toString is needed as naturalIds for entities are not simply based on a single value like primary keys
 						//the only same way to differentiate the keys is to included the disassembled values in the string.
 						final StringBuilder toStringBuilder = new StringBuilder( entityName ).append( "##NaturalId[" );
 						for ( int i = 0; i < naturalIdValues.length; i++ ) {
 							toStringBuilder.append( naturalIdValues[i] );
 							if ( i + 1 < naturalIdValues.length ) {
 								toStringBuilder.append( ", " );
 							}
 						}
 						toStringBuilder.append( "]" );
 
 						return toStringBuilder.toString();
 					}
 				}
 		);
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getTenantId() {
 		return tenantId;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public Serializable[] getNaturalIdValues() {
 		return naturalIdValues;
 	}
 
 	@Override
 	public String toString() {
 		return toString.getValue();
 	}
 	
 	@Override
 	public int hashCode() {
 		return this.hashCode;
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		
 		if ( hashCode != o.hashCode() || !(o instanceof NaturalIdCacheKey) ) {
 			//hashCode is part of this check since it is pre-calculated and hash must match for equals to be true
 			return false;
 		}
 
 		final NaturalIdCacheKey other = (NaturalIdCacheKey) o;
 		return entityName.equals( other.entityName ) 
 				&& EqualsHelper.equals( tenantId, other.tenantId )
 				&& Arrays.deepEquals( this.naturalIdValues, other.naturalIdValues );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java
index 4db834e0d0..e297a9e10c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/ProxyBuilder.java
@@ -1,377 +1,377 @@
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
 package org.hibernate.engine.jdbc.internal.proxy;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Proxy;
 import java.sql.CallableStatement;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.Statement;
 
 import org.hibernate.engine.jdbc.spi.InvalidatableWrapper;
 import org.hibernate.engine.jdbc.spi.JdbcWrapper;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Centralized builder for proxy instances
  *
  * @author Steve Ebersole
  */
 public class ProxyBuilder {
 
 	// Connection ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] CONNECTION_PROXY_INTERFACES = new Class[] {
 			Connection.class,
 			JdbcWrapper.class
 	};
 
-	private static final Value<Constructor<Connection>> connectionProxyConstructorValue = new Value<Constructor<Connection>>(
-			new Value.DeferredInitializer<Constructor<Connection>>() {
+	private static final ValueHolder<Constructor<Connection>> connectionProxyConstructorValue = new ValueHolder<Constructor<Connection>>(
+			new ValueHolder.DeferredInitializer<Constructor<Connection>>() {
 				@Override
 				public Constructor<Connection> initialize() {
 					try {
 						return locateConnectionProxyClass().getConstructor( InvocationHandler.class );
 					}
 					catch (NoSuchMethodException e) {
 						throw new JdbcProxyException( "Could not find proxy constructor in JDK generated Connection proxy class", e );
 					}
 				}
 
 				@SuppressWarnings("unchecked")
 				private Class<Connection> locateConnectionProxyClass() {
 					return (Class<Connection>) Proxy.getProxyClass(
 							JdbcWrapper.class.getClassLoader(),
 							CONNECTION_PROXY_INTERFACES
 					);
 				}
 			}
 	);
 
 	public static Connection buildConnection(LogicalConnectionImplementor logicalConnection) {
 		final ConnectionProxyHandler proxyHandler = new ConnectionProxyHandler( logicalConnection );
 		try {
 			return connectionProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC Connection proxy", e );
 		}
 	}
 
 
 	// Statement ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] STMNT_PROXY_INTERFACES = new Class[] {
 			Statement.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
-	private static final Value<Constructor<Statement>> statementProxyConstructorValue = new Value<Constructor<Statement>>(
-			new Value.DeferredInitializer<Constructor<Statement>>() {
+	private static final ValueHolder<Constructor<Statement>> statementProxyConstructorValue = new ValueHolder<Constructor<Statement>>(
+			new ValueHolder.DeferredInitializer<Constructor<Statement>>() {
 				@Override
 				public Constructor<Statement> initialize() {
 					try {
 						return locateStatementProxyClass().getConstructor( InvocationHandler.class );
 					}
 					catch (NoSuchMethodException e) {
 						throw new JdbcProxyException( "Could not find proxy constructor in JDK generated Statement proxy class", e );
 					}
 				}
 
 				@SuppressWarnings("unchecked")
 				private Class<Statement> locateStatementProxyClass() {
 					return (Class<Statement>) Proxy.getProxyClass(
 							JdbcWrapper.class.getClassLoader(),
 							STMNT_PROXY_INTERFACES
 					);
 				}
 			}
 	);
 
 	public static Statement buildStatement(
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		final BasicStatementProxyHandler proxyHandler = new BasicStatementProxyHandler(
 				statement,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		try {
 			return statementProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC Statement proxy", e );
 		}
 	}
 
 	public static Statement buildImplicitStatement(
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		if ( statement == null ) {
 			return null;
 		}
 		final ImplicitStatementProxyHandler proxyHandler = new ImplicitStatementProxyHandler( statement, connectionProxyHandler, connectionProxy );
 		try {
 			return statementProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC Statement proxy", e );
 		}
 	}
 
 
 	// PreparedStatement ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] PREPARED_STMNT_PROXY_INTERFACES = new Class[] {
 			PreparedStatement.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
-	private static final Value<Constructor<PreparedStatement>> preparedStatementProxyConstructorValue = new Value<Constructor<PreparedStatement>>(
-			new Value.DeferredInitializer<Constructor<PreparedStatement>>() {
+	private static final ValueHolder<Constructor<PreparedStatement>> preparedStatementProxyConstructorValue = new ValueHolder<Constructor<PreparedStatement>>(
+			new ValueHolder.DeferredInitializer<Constructor<PreparedStatement>>() {
 				@Override
 				public Constructor<PreparedStatement> initialize() {
 					try {
 						return locatePreparedStatementProxyClass().getConstructor( InvocationHandler.class );
 					}
 					catch (NoSuchMethodException e) {
 						throw new JdbcProxyException( "Could not find proxy constructor in JDK generated Statement proxy class", e );
 					}
 				}
 
 				@SuppressWarnings("unchecked")
 				private Class<PreparedStatement> locatePreparedStatementProxyClass() {
 					return (Class<PreparedStatement>) Proxy.getProxyClass(
 							JdbcWrapper.class.getClassLoader(),
 							PREPARED_STMNT_PROXY_INTERFACES
 					);
 				}
 			}
 	);
 
 	public static PreparedStatement buildPreparedStatement(
 			String sql,
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		final PreparedStatementProxyHandler proxyHandler = new PreparedStatementProxyHandler(
 				sql,
 				statement,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		try {
 			return preparedStatementProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC PreparedStatement proxy", e );
 		}
 	}
 
 
 	// CallableStatement ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] CALLABLE_STMNT_PROXY_INTERFACES = new Class[] {
 			CallableStatement.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
-	private static final Value<Constructor<CallableStatement>> callableStatementProxyConstructorValue = new Value<Constructor<CallableStatement>>(
-			new Value.DeferredInitializer<Constructor<CallableStatement>>() {
+	private static final ValueHolder<Constructor<CallableStatement>> callableStatementProxyConstructorValue = new ValueHolder<Constructor<CallableStatement>>(
+			new ValueHolder.DeferredInitializer<Constructor<CallableStatement>>() {
 				@Override
 				public Constructor<CallableStatement> initialize() {
 					try {
 						return locateCallableStatementProxyClass().getConstructor( InvocationHandler.class );
 					}
 					catch (NoSuchMethodException e) {
 						throw new JdbcProxyException( "Could not find proxy constructor in JDK generated Statement proxy class", e );
 					}
 				}
 
 				@SuppressWarnings("unchecked")
 				private Class<CallableStatement> locateCallableStatementProxyClass() {
 					return (Class<CallableStatement>) Proxy.getProxyClass(
 							JdbcWrapper.class.getClassLoader(),
 							CALLABLE_STMNT_PROXY_INTERFACES
 					);
 				}
 			}
 	);
 
 	public static CallableStatement buildCallableStatement(
 			String sql,
 			CallableStatement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		final CallableStatementProxyHandler proxyHandler = new CallableStatementProxyHandler(
 				sql,
 				statement,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		try {
 			return callableStatementProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC CallableStatement proxy", e );
 		}
 	}
 
 
 	// ResultSet ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] RESULTSET_PROXY_INTERFACES = new Class[] {
 			ResultSet.class,
 			JdbcWrapper.class,
 			InvalidatableWrapper.class
 	};
 
-	private static final Value<Constructor<ResultSet>> resultSetProxyConstructorValue = new Value<Constructor<ResultSet>>(
-			new Value.DeferredInitializer<Constructor<ResultSet>>() {
+	private static final ValueHolder<Constructor<ResultSet>> resultSetProxyConstructorValue = new ValueHolder<Constructor<ResultSet>>(
+			new ValueHolder.DeferredInitializer<Constructor<ResultSet>>() {
 				@Override
 				public Constructor<ResultSet> initialize() {
 					try {
 						return locateCallableStatementProxyClass().getConstructor( InvocationHandler.class );
 					}
 					catch (NoSuchMethodException e) {
 						throw new JdbcProxyException( "Could not find proxy constructor in JDK generated ResultSet proxy class", e );
 					}
 				}
 
 				@SuppressWarnings("unchecked")
 				private Class<ResultSet> locateCallableStatementProxyClass() {
 					return (Class<ResultSet>) Proxy.getProxyClass(
 							JdbcWrapper.class.getClassLoader(),
 							RESULTSET_PROXY_INTERFACES
 					);
 				}
 			}
 	);
 
 	public static ResultSet buildResultSet(
 			ResultSet resultSet,
 			AbstractStatementProxyHandler statementProxyHandler,
 			Statement statementProxy) {
 		final ResultSetProxyHandler proxyHandler = new ResultSetProxyHandler( resultSet, statementProxyHandler, statementProxy );
 		try {
 			return resultSetProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC ResultSet proxy", e );
 		}
 	}
 
 	public static ResultSet buildImplicitResultSet(
 			ResultSet resultSet,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		final ImplicitResultSetProxyHandler proxyHandler = new ImplicitResultSetProxyHandler(
 				resultSet,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		try {
 			return resultSetProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC ResultSet proxy", e );
 		}
 	}
 
 	public static ResultSet buildImplicitResultSet(
 			ResultSet resultSet,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy,
 			Statement sourceStatement) {
 		final ImplicitResultSetProxyHandler proxyHandler = new ImplicitResultSetProxyHandler(
 				resultSet,
 				connectionProxyHandler,
 				connectionProxy,
 				sourceStatement
 		);
 		try {
 			return resultSetProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC ResultSet proxy", e );
 		}
 	}
 
 
 	// DatabaseMetaData ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static final Class[] METADATA_PROXY_INTERFACES = new Class[] {
 			DatabaseMetaData.class,
 			JdbcWrapper.class
 	};
 
-	private static final Value<Constructor<DatabaseMetaData>> metadataProxyConstructorValue = new Value<Constructor<DatabaseMetaData>>(
-			new Value.DeferredInitializer<Constructor<DatabaseMetaData>>() {
+	private static final ValueHolder<Constructor<DatabaseMetaData>> metadataProxyConstructorValue = new ValueHolder<Constructor<DatabaseMetaData>>(
+			new ValueHolder.DeferredInitializer<Constructor<DatabaseMetaData>>() {
 				@Override
 				public Constructor<DatabaseMetaData> initialize() {
 					try {
 						return locateDatabaseMetaDataProxyClass().getConstructor( InvocationHandler.class );
 					}
 					catch (NoSuchMethodException e) {
 						throw new JdbcProxyException( "Could not find proxy constructor in JDK generated DatabaseMetaData proxy class", e );
 					}
 				}
 
 				@SuppressWarnings("unchecked")
 				private Class<DatabaseMetaData> locateDatabaseMetaDataProxyClass() {
 					return (Class<DatabaseMetaData>) Proxy.getProxyClass(
 							JdbcWrapper.class.getClassLoader(),
 							METADATA_PROXY_INTERFACES
 					);
 				}
 			}
 	);
 
 	public static DatabaseMetaData buildDatabaseMetaData(
 			DatabaseMetaData metaData,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		final DatabaseMetaDataProxyHandler proxyHandler = new DatabaseMetaDataProxyHandler(
 				metaData,
 				connectionProxyHandler,
 				connectionProxy
 		);
 		try {
 			return metadataProxyConstructorValue.getValue().newInstance( proxyHandler );
 		}
 		catch (Exception e) {
 			throw new JdbcProxyException( "Could not instantiate JDBC DatabaseMetaData proxy", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/Value.java b/hibernate-core/src/main/java/org/hibernate/internal/util/ValueHolder.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/internal/util/Value.java
rename to hibernate-core/src/main/java/org/hibernate/internal/util/ValueHolder.java
index b575539b92..3570bb2020 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/Value.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/ValueHolder.java
@@ -1,81 +1,81 @@
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
 package org.hibernate.internal.util;
 
 /**
- * Represents a "final" value that is initialized either {@link #Value(Object) up front} or once at some point
- * {@link #Value(org.hibernate.internal.util.Value.DeferredInitializer) after} declaration.
+ * Represents a "final" value that is initialized either {@link #ValueHolder(Object) up front} or once at some point
+ * {@link #ValueHolder(ValueHolder.DeferredInitializer) after} declaration.
  *
  * @author Steve Ebersole
  */
-public class Value<T> {
+public class ValueHolder<T> {
 
 	/**
 	 * The snippet that generates the initialization value.
 	 *
 	 * @param <T>
 	 */
 	public static interface DeferredInitializer<T> {
 		/**
 		 * Build the initialization value.
 		 * <p/>
 		 * Implementation note: returning {@code null} is "ok" but will cause this method to keep being called.
 		 *
 		 * @return The initialization value.
 		 */
 		public T initialize();
 	}
 
 	private final DeferredInitializer<T> valueInitializer;
 	private T value;
 
 	/**
-	 * Instantiates a {@link Value} with the specified initializer.
+	 * Instantiates a {@link ValueHolder} with the specified initializer.
 	 *
 	 * @param valueInitializer The initializer to use in {@link #getValue} when value not yet known.
 	 */
-	public Value(DeferredInitializer<T> valueInitializer) {
+	public ValueHolder(DeferredInitializer<T> valueInitializer) {
 		this.valueInitializer = valueInitializer;
 	}
 
 	@SuppressWarnings( {"unchecked"})
-	public Value(T value) {
+	public ValueHolder(T value) {
 		this( NO_DEFERRED_INITIALIZER );
 		this.value = value;
 	}
 
 	public T getValue() {
 		if ( value == null ) {
 			value = valueInitializer.initialize();
 		}
 		return value;
 	}
 
 	private static final DeferredInitializer NO_DEFERRED_INITIALIZER = new DeferredInitializer() {
 		@Override
 		public Void initialize() {
 			return null;
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index 50eb59ac13..967e2a627a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,605 +1,605 @@
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.engine.spi.FilterDefinition;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.internal.util.collections.JoinedIterable;
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.PluralAttribute;
 import org.hibernate.metamodel.domain.PluralAttributeNature;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.binder.JpaCallbackClass;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Provides the link between the domain and the relational model for an entity.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class EntityBinding implements AttributeBindingContainer {
 	private static final String NULL_DISCRIMINATOR_MATCH_VALUE = "null";
 	private static final String NOT_NULL_DISCRIMINATOR_MATCH_VALUE = "not null";
 
 	private final EntityBinding superEntityBinding;
 	private final List<EntityBinding> subEntityBindings = new ArrayList<EntityBinding>();
 	private final HierarchyDetails hierarchyDetails;
 
 	private Entity entity;
 	private TableSpecification primaryTable;
     private String primaryTableName;
 	private Map<String, TableSpecification> secondaryTables = new HashMap<String, TableSpecification>();
 
-	private Value<Class<?>> proxyInterfaceType;
+	private ValueHolder<Class<?>> proxyInterfaceType;
 
 	private String jpaEntityName;
 
 	private Class<? extends EntityPersister> customEntityPersisterClass;
 	private Class<? extends EntityTuplizer> customEntityTuplizerClass;
 
 	private String discriminatorMatchValue;
 
 	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>();
 	private Set<SingularAssociationAttributeBinding> entityReferencingAttributeBindings = new HashSet<SingularAssociationAttributeBinding>();
 
 	private MetaAttributeContext metaAttributeContext;
 
 	private boolean lazy;
 	private boolean mutable;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private boolean hasSubselectLoadableCollections;
 
 	private Boolean isAbstract;
 
 	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 
     private List<JpaCallbackClass> jpaCallbackClasses = new ArrayList<JpaCallbackClass>();
 
 	/**
 	 * Used to instantiate the EntityBinding for an entity that is the root of an inheritance hierarchy
 	 *
 	 * @param inheritanceType The inheritance type for the hierarchy
 	 * @param entityMode The entity mode used in this hierarchy.
 	 */
 	public EntityBinding(InheritanceType inheritanceType, EntityMode entityMode) {
 		this.superEntityBinding = null;
 		this.hierarchyDetails = new HierarchyDetails( this, inheritanceType, entityMode );
 	}
 
 	/**
 	 * Used to instantiate the EntityBinding for an entity that is a subclass (sub-entity) in an inheritance hierarchy
 	 *
 	 * @param superEntityBinding The entity binding of this binding's super
 	 */
 	public EntityBinding(EntityBinding superEntityBinding) {
 		this.superEntityBinding = superEntityBinding;
 		this.superEntityBinding.subEntityBindings.add( this );
 		this.hierarchyDetails = superEntityBinding.getHierarchyDetails();
 	}
 
 	public HierarchyDetails getHierarchyDetails() {
 		return hierarchyDetails;
 	}
 
 	public EntityBinding getSuperEntityBinding() {
 		return superEntityBinding;
 	}
 
 	public boolean isRoot() {
 		return superEntityBinding == null;
 	}
 
 	public boolean isPolymorphic() {
 		return  superEntityBinding != null ||
 				hierarchyDetails.getEntityDiscriminator() != null ||
 				! subEntityBindings.isEmpty();
 	}
 
 	public boolean hasSubEntityBindings() {
 		return subEntityBindings.size() > 0;
 	}
 
 	public int getSubEntityBindingClosureSpan() {
 		int n = subEntityBindings.size();
 		for ( EntityBinding subEntityBinding : subEntityBindings ) {
 			n += subEntityBinding.getSubEntityBindingClosureSpan();
 		}
 		return n;
 	}
 
 	/* used for testing */
 	public Iterable<EntityBinding> getDirectSubEntityBindings() {
 		return subEntityBindings;
 	}
 
 	/**
 	 * Returns sub-EntityBinding objects in a special 'order', most derived subclasses
 	 * first. Specifically, the sub-entity bindings follow a depth-first,
 	 * post-order traversal
 	 *
 	 * Note that the returned value excludes this entity binding.
 	 *
 	 * @return sub-entity bindings ordered by those entity bindings that are most derived.
 	 */
 	public Iterable<EntityBinding> getPostOrderSubEntityBindingClosure() {
 		// TODO: why this order?
 		List<Iterable<EntityBinding>> subclassIterables = new ArrayList<Iterable<EntityBinding>>( subEntityBindings.size() + 1 );
 		for ( EntityBinding subEntityBinding : subEntityBindings ) {
 			Iterable<EntityBinding> subSubEntityBindings = subEntityBinding.getPostOrderSubEntityBindingClosure();
 			if ( subSubEntityBindings.iterator().hasNext() ) {
 				subclassIterables.add( subSubEntityBindings );
 			}
 		}
 		if ( ! subEntityBindings.isEmpty() ) {
 			subclassIterables.add( subEntityBindings );
 		}
 		return new JoinedIterable<EntityBinding>( subclassIterables );
 	}
 
 	/**
 	 * Returns sub-EntityBinding ordered as a depth-first,
 	 * pre-order traversal (a subclass precedes its own subclasses).
 	 *
 	 * Note that the returned value specifically excludes this entity binding.
 	 *
 	 * @return sub-entity bindings ordered as a depth-first,
 	 * pre-order traversal
 	 */
 	public Iterable<EntityBinding> getPreOrderSubEntityBindingClosure() {
 		return getPreOrderSubEntityBindingClosure( false );
 	}
 
 	private Iterable<EntityBinding> getPreOrderSubEntityBindingClosure(boolean includeThis) {
 		List<Iterable<EntityBinding>> iterables = new ArrayList<Iterable<EntityBinding>>();
 		if ( includeThis ) {
 			iterables.add( java.util.Collections.singletonList( this ) );
 		}
 		for ( EntityBinding subEntityBinding : subEntityBindings ) {
 			Iterable<EntityBinding> subSubEntityBindingClosure =  subEntityBinding.getPreOrderSubEntityBindingClosure( true );
 			if ( subSubEntityBindingClosure.iterator().hasNext() ) {
 				iterables.add( subSubEntityBindingClosure );
 			}
 		}
 		return new JoinedIterable<EntityBinding>( iterables );
 	}
 
 	public Entity getEntity() {
 		return entity;
 	}
 
 	public void setEntity(Entity entity) {
 		this.entity = entity;
 	}
 
 	public TableSpecification getPrimaryTable() {
 		return primaryTable;
 	}
 
 	public void setPrimaryTable(TableSpecification primaryTable) {
 		this.primaryTable = primaryTable;
 	}
 
     public TableSpecification locateTable(String tableName) {
         if ( tableName == null || tableName.equals( getPrimaryTableName() ) ) {
             return primaryTable;
         }
         TableSpecification tableSpec = secondaryTables.get( tableName );
         if ( tableSpec == null ) {
             throw new AssertionFailure(
                     String.format(
                             "Unable to find table %s amongst tables %s",
                             tableName,
                             secondaryTables.keySet()
                     )
             );
         }
         return tableSpec;
     }
     public String getPrimaryTableName() {
         return primaryTableName;
     }
 
     public void setPrimaryTableName(String primaryTableName) {
         this.primaryTableName = primaryTableName;
     }
 
 	public void addSecondaryTable(String tableName, TableSpecification table) {
 		secondaryTables.put( tableName, table );
 	}
 
 	public boolean isVersioned() {
 		return getHierarchyDetails().getVersioningAttributeBinding() != null;
 	}
 
 	public boolean isDiscriminatorMatchValueNull() {
 		return NULL_DISCRIMINATOR_MATCH_VALUE.equals( discriminatorMatchValue );
 	}
 
 	public boolean isDiscriminatorMatchValueNotNull() {
 		return NOT_NULL_DISCRIMINATOR_MATCH_VALUE.equals( discriminatorMatchValue );
 	}
 
 	public String getDiscriminatorMatchValue() {
 		return discriminatorMatchValue;
 	}
 
 	public void setDiscriminatorMatchValue(String discriminatorMatchValue) {
 		this.discriminatorMatchValue = discriminatorMatchValue;
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition filterDefinition) {
 		filterDefinitions.add( filterDefinition );
 	}
 
 	public Iterable<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
 		return entityReferencingAttributeBindings;
 	}
 
 	@Override
 	public EntityBinding seekEntityBinding() {
 		return this;
 	}
 
 	@Override
 	public String getPathBase() {
 		return getEntity().getName();
 	}
 
 	@Override
 	public Class<?> getClassReference() {
 		return getEntity().getClassReference();
 	}
 
 	@Override
 	public AttributeContainer getAttributeContainer() {
 		return getEntity();
 	}
 
 	protected void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
 		if ( SingularAssociationAttributeBinding.class.isInstance( attributeBinding ) ) {
 			entityReferencingAttributeBindings.add( (SingularAssociationAttributeBinding) attributeBinding );
 		}
 		attributeBindingMap.put( name, attributeBinding );
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
-	public Value<Class<?>> getProxyInterfaceType() {
+	public ValueHolder<Class<?>> getProxyInterfaceType() {
 		return proxyInterfaceType;
 	}
 
-	public void setProxyInterfaceType(Value<Class<?>> proxyInterfaceType) {
+	public void setProxyInterfaceType(ValueHolder<Class<?>> proxyInterfaceType) {
 		this.proxyInterfaceType = proxyInterfaceType;
 	}
 
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	public void setWhereFilter(String whereFilter) {
 		this.whereFilter = whereFilter;
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	/* package-protected */
 	void setSubselectLoadableCollections(boolean hasSubselectLoadableCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectLoadableCollections;
 	}
 
 	public Class<? extends EntityPersister> getCustomEntityPersisterClass() {
 		return customEntityPersisterClass;
 	}
 
 	public void setCustomEntityPersisterClass(Class<? extends EntityPersister> customEntityPersisterClass) {
 		this.customEntityPersisterClass = customEntityPersisterClass;
 	}
 
 	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass() {
 		return customEntityTuplizerClass;
 	}
 
 	public void setCustomEntityTuplizerClass(Class<? extends EntityTuplizer> customEntityTuplizerClass) {
 		this.customEntityTuplizerClass = customEntityTuplizerClass;
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public void setAbstract(Boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 
 	public void addSynchronizedTableNames(java.util.Collection<String> synchronizedTableNames) {
 		this.synchronizedTableNames.addAll( synchronizedTableNames );
 	}
 
 	public String getJpaEntityName() {
 		return jpaEntityName;
 	}
 
 	public void setJpaEntityName(String jpaEntityName) {
 		this.jpaEntityName = jpaEntityName;
 	}
 
 	public String getCustomLoaderName() {
 		return customLoaderName;
 	}
 
 	public void setCustomLoaderName(String customLoaderName) {
 		this.customLoaderName = customLoaderName;
 	}
 
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	public void setCustomInsert(CustomSQL customInsert) {
 		this.customInsert = customInsert;
 	}
 
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	public void setCustomUpdate(CustomSQL customUpdate) {
 		this.customUpdate = customUpdate;
 	}
 
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	public void setCustomDelete(CustomSQL customDelete) {
 		this.customDelete = customDelete;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "EntityBinding" );
 		sb.append( "{entity=" ).append( entity != null ? entity.getName() : "not set" );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	@Override
 	public BasicAttributeBinding makeBasicAttributeBinding(SingularAttribute attribute) {
 		return makeSimpleAttributeBinding( attribute, false, false );
 	}
 
 	private BasicAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute, boolean forceNonNullable, boolean forceUnique) {
 		final BasicAttributeBinding binding = new BasicAttributeBinding(
 				this,
 				attribute,
 				forceNonNullable,
 				forceUnique
 		);
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
 		final ComponentAttributeBinding binding = new ComponentAttributeBinding( this, attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
 		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this, attribute );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
 		Helper.checkPluralAttributeNature( attribute, PluralAttributeNature.BAG );
 		final BagBinding binding = new BagBinding( this, attribute, nature );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public SetBinding makeSetAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
 		Helper.checkPluralAttributeNature( attribute, PluralAttributeNature.SET );
 		final SetBinding binding = new SetBinding( this, attribute, nature );
 		registerAttributeBinding( attribute.getName(), binding );
 		return binding;
 	}
 
 	@Override
 	public AttributeBinding locateAttributeBinding(String name) {
 		return attributeBindingMap.get( name );
 	}
 
 	@Override
 	public Iterable<AttributeBinding> attributeBindings() {
 		return attributeBindingMap.values();
 	}
 
 	/**
 	 * Gets the number of attribute bindings defined on this class, including the
 	 * identifier attribute binding and attribute bindings defined
 	 * as part of a join.
 	 *
 	 * @return The number of attribute bindings
 	 */
 	public int getAttributeBindingClosureSpan() {
 		// TODO: update account for join attribute bindings
 		return superEntityBinding != null ?
 				superEntityBinding.getAttributeBindingClosureSpan() + attributeBindingMap.size() :
 				attributeBindingMap.size();
 	}
 
 	/**
 	 * Gets the attribute bindings defined on this class, including the
 	 * identifier attribute binding and attribute bindings defined
 	 * as part of a join.
 	 *
 	 * @return The attribute bindings.
 	 */
 	public Iterable<AttributeBinding> getAttributeBindingClosure() {
 		// TODO: update size to account for joins
 		Iterable<AttributeBinding> iterable;
 		if ( superEntityBinding != null ) {
 			List<Iterable<AttributeBinding>> iterables = new ArrayList<Iterable<AttributeBinding>>( 2 );
 			iterables.add( superEntityBinding.getAttributeBindingClosure() );
 			iterables.add( attributeBindings() );
 			iterable = new JoinedIterable<AttributeBinding>( iterables );
 		}
 		else {
 			iterable = attributeBindings();
 		}
 		return iterable;
 	}
 
 	/**
 	 * Gets the attribute bindings for this EntityBinding and all of its
 	 * sub-EntityBinding, starting from the root of the hierarchy; includes
 	 * the identifier and attribute bindings defined as part of a join.
 	 * @return
 	 */
 	public Iterable<AttributeBinding> getSubEntityAttributeBindingClosure() {
 		List<Iterable<AttributeBinding>> iterables = new ArrayList<Iterable<AttributeBinding>>();
 		iterables.add( getAttributeBindingClosure() );
 		for ( EntityBinding subEntityBinding : getPreOrderSubEntityBindingClosure() ) {
 			// only add attribute bindings declared for the subEntityBinding
 			iterables.add( subEntityBinding.attributeBindings() );
 			// TODO: if EntityBinding.attributeBindings() excludes joined attributes, then they need to be added here
 		}
 		return new JoinedIterable<AttributeBinding>( iterables );
 	}
 
 	public void setJpaCallbackClasses( List<JpaCallbackClass> jpaCallbackClasses ) {
 	    this.jpaCallbackClasses = jpaCallbackClasses;
 	}
 
     public Iterable<JpaCallbackClass> getJpaCallbackClasses() {
         return jpaCallbackClasses;
     }
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
index 941cbb580c..d8bb0f35c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/AbstractAttributeContainer.java
@@ -1,308 +1,308 @@
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
 package org.hibernate.metamodel.domain;
 
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Convenient base class for {@link AttributeContainer}.  Because in our model all
  * {@link AttributeContainer AttributeContainers} are also {@link Hierarchical} we also implement that here
  * as well.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttributeContainer implements AttributeContainer, Hierarchical {
 	private final String name;
 	private final String className;
-	private final Value<Class<?>> classReference;
+	private final ValueHolder<Class<?>> classReference;
 	private final Hierarchical superType;
 	private LinkedHashSet<Attribute> attributeSet = new LinkedHashSet<Attribute>();
 	private HashMap<String, Attribute> attributeMap = new HashMap<String, Attribute>();
 
-	public AbstractAttributeContainer(String name, String className, Value<Class<?>> classReference, Hierarchical superType) {
+	public AbstractAttributeContainer(String name, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
 		this.name = name;
 		this.className = className;
 		this.classReference = classReference;
 		this.superType = superType;
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
 
 	@Override
 	public String getClassName() {
 		return className;
 	}
 
 	@Override
 	public Class<?> getClassReference() {
 		return classReference.getValue();
 	}
 
 	@Override
-	public Value<Class<?>> getClassReferenceUnresolved() {
+	public ValueHolder<Class<?>> getClassReferenceUnresolved() {
 		return classReference;
 	}
 
 	@Override
 	public Hierarchical getSuperType() {
 		return superType;
 	}
 
 	@Override
 	public Set<Attribute> attributes() {
 		return Collections.unmodifiableSet( attributeSet );
 	}
 
 	@Override
 	public String getRoleBaseName() {
 		return getClassName();
 	}
 
 	@Override
 	public Attribute locateAttribute(String name) {
 		return attributeMap.get( name );
 	}
 
 	@Override
 	public SingularAttribute locateSingularAttribute(String name) {
 		return (SingularAttribute) locateAttribute( name );
 	}
 
 	@Override
 	public SingularAttribute createSingularAttribute(String name) {
 		SingularAttribute attribute = new SingularAttributeImpl( name, this );
 		addAttribute( attribute );
 		return attribute;
 	}
 
 	@Override
 	public SingularAttribute createVirtualSingularAttribute(String name) {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public SingularAttribute locateComponentAttribute(String name) {
 		return (SingularAttributeImpl) locateAttribute( name );
 	}
 
 	@Override
 	public SingularAttribute createComponentAttribute(String name, Component component) {
 		SingularAttributeImpl attribute = new SingularAttributeImpl( name, this );
 		attribute.resolveType( component );
 		addAttribute( attribute );
 		return attribute;
 	}
 
 	@Override
 	public PluralAttribute locatePluralAttribute(String name) {
 		return (PluralAttribute) locateAttribute( name );
 	}
 
 	protected PluralAttribute createPluralAttribute(String name, PluralAttributeNature nature) {
 		PluralAttribute attribute = nature.isIndexed()
 				? new IndexedPluralAttributeImpl( name, nature, this )
 				: new PluralAttributeImpl( name, nature, this );
 		addAttribute( attribute );
 		return attribute;
 	}
 
 	@Override
 	public PluralAttribute locateBag(String name) {
 		return locatePluralAttribute( name );
 	}
 
 	@Override
 	public PluralAttribute createBag(String name) {
 		return createPluralAttribute( name, PluralAttributeNature.BAG );
 	}
 
 	@Override
 	public PluralAttribute locateSet(String name) {
 		return locatePluralAttribute( name );
 	}
 
 	@Override
 	public PluralAttribute createSet(String name) {
 		return createPluralAttribute( name, PluralAttributeNature.SET );
 	}
 
 	@Override
 	public IndexedPluralAttribute locateList(String name) {
 		return (IndexedPluralAttribute) locatePluralAttribute( name );
 	}
 
 	@Override
 	public IndexedPluralAttribute createList(String name) {
 		return (IndexedPluralAttribute) createPluralAttribute( name, PluralAttributeNature.LIST );
 	}
 
 	@Override
 	public IndexedPluralAttribute locateMap(String name) {
 		return (IndexedPluralAttribute) locatePluralAttribute( name );
 	}
 
 	@Override
 	public IndexedPluralAttribute createMap(String name) {
 		return (IndexedPluralAttribute) createPluralAttribute( name, PluralAttributeNature.MAP );
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "AbstractAttributeContainer" );
 		sb.append( "{name='" ).append( name ).append( '\'' );
 		sb.append( ", superType=" ).append( superType );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	protected void addAttribute(Attribute attribute) {
 		// todo : how to best "secure" this?
 		if ( attributeMap.put( attribute.getName(), attribute ) != null ) {
 			throw new IllegalArgumentException( "Attribute with name [" + attribute.getName() + "] already registered" );
 		}
 		attributeSet.add( attribute );
 	}
 
 	// todo : inner classes for now..
 
 	public static class SingularAttributeImpl implements SingularAttribute {
 		private final AttributeContainer attributeContainer;
 		private final String name;
 		private Type type;
 
 		public SingularAttributeImpl(String name, AttributeContainer attributeContainer) {
 			this.name = name;
 			this.attributeContainer = attributeContainer;
 		}
 
 		public boolean isTypeResolved() {
 			return type != null;
 		}
 
 		public void resolveType(Type type) {
 			if ( type == null ) {
 				throw new IllegalArgumentException( "Attempt to resolve with null type" );
 			}
 			this.type = type;
 		}
 
 		@Override
 		public Type getSingularAttributeType() {
 			return type;
 		}
 
 		@Override
 		public String getName() {
 			return name;
 		}
 
 		@Override
 		public AttributeContainer getAttributeContainer() {
 			return attributeContainer;
 		}
 
 		@Override
 		public boolean isSingular() {
 			return true;
 		}
 	}
 
 	public static class PluralAttributeImpl implements PluralAttribute {
 		private final AttributeContainer attributeContainer;
 		private final PluralAttributeNature nature;
 		private final String name;
 
 		private Type elementType;
 
 		public PluralAttributeImpl(String name, PluralAttributeNature nature, AttributeContainer attributeContainer) {
 			this.name = name;
 			this.nature = nature;
 			this.attributeContainer = attributeContainer;
 		}
 
 		@Override
 		public AttributeContainer getAttributeContainer() {
 			return attributeContainer;
 		}
 
 		@Override
 		public boolean isSingular() {
 			return false;
 		}
 
 		@Override
 		public PluralAttributeNature getNature() {
 			return nature;
 		}
 
 		@Override
 		public String getName() {
 			return name;
 		}
 
 		@Override
 		public String getRole() {
 			return StringHelper.qualify( attributeContainer.getRoleBaseName(), name );
 		}
 
 		@Override
 		public Type getElementType() {
 			return elementType;
 		}
 
 		@Override
 		public void setElementType(Type elementType) {
 			this.elementType = elementType;
 		}
 	}
 
 	public static class IndexedPluralAttributeImpl extends PluralAttributeImpl implements IndexedPluralAttribute {
 		private Type indexType;
 
 		public IndexedPluralAttributeImpl(String name, PluralAttributeNature nature, AttributeContainer attributeContainer) {
 			super( name, nature, attributeContainer );
 		}
 
 		@Override
 		public Type getIndexType() {
 			return indexType;
 		}
 
 		@Override
 		public void setIndexType(Type indexType) {
 			this.indexType = indexType;
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java
index 06c4fc9d29..682ce46afd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/BasicType.java
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
 package org.hibernate.metamodel.domain;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Models a basic type.
  *
  * @author Steve Ebersole
  */
 public class BasicType implements Type {
 	private final String name;
-	private final Value<Class<?>> classReference;
+	private final ValueHolder<Class<?>> classReference;
 
-	public BasicType(String name, Value<Class<?>> classReference) {
+	public BasicType(String name, ValueHolder<Class<?>> classReference) {
 		this.name = name;
 		this.classReference = classReference;
 	}
 
 	@Override
 	public String getName() {
 		return name;
 	}
 
 	@Override
 	public String getClassName() {
 		return name;
 	}
 
 	@Override
 	public Class<?> getClassReference() {
 		return classReference.getValue();
 	}
 
 	@Override
-	public Value<Class<?>> getClassReferenceUnresolved() {
+	public ValueHolder<Class<?>> getClassReferenceUnresolved() {
 		return classReference;
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return false;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java
index c12296825e..e300355a43 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Component.java
@@ -1,56 +1,56 @@
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
 package org.hibernate.metamodel.domain;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Models the notion of a component (what JPA calls an Embeddable).
  * <p/>
  * NOTE : Components are not currently really hierarchical.  But that is a feature I want to add.
  *
  * @author Steve Ebersole
  */
 public class Component extends AbstractAttributeContainer {
-	public Component(String name, String className, Value<Class<?>> classReference, Hierarchical superType) {
+	public Component(String name, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
 		super( name, className, classReference, superType );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return false;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return true;
 	}
 
 	@Override
 	public String getRoleBaseName() {
 		// todo : this is not really completely accurate atm
 		//		the role base here should really be the role of the component attribute.
 		return getClassName();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
index 4b2f733689..fc6b41f6a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Entity.java
@@ -1,56 +1,56 @@
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
 package org.hibernate.metamodel.domain;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Models the notion of an entity
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class Entity extends AbstractAttributeContainer {
 	/**
 	 * Constructor for the entity
 	 *
 	 * @param entityName The name of the entity
 	 * @param className The name of this entity's java class
 	 * @param classReference The reference to this entity's {@link Class}
 	 * @param superType The super type for this entity. If there is not super type {@code null} needs to be passed.
 	 */
-	public Entity(String entityName, String className, Value<Class<?>> classReference, Hierarchical superType) {
+	public Entity(String entityName, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
 		super( entityName, className, classReference, superType );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java
index c34de36628..d6c6a0eeff 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/JavaType.java
@@ -1,70 +1,70 @@
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
 package org.hibernate.metamodel.domain;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Models the naming of a Java type where we may not have access to that type's {@link Class} reference.  Generally
  * speaking this is the case in various hibernate-tools and reverse-engineering use cases.
  *
  * @author Steve Ebersole
  */
 public class JavaType {
 	private final String name;
-	private final Value<Class<?>> classReference;
+	private final ValueHolder<Class<?>> classReference;
 
 	public JavaType(final String name, final ClassLoaderService classLoaderService) {
 		this.name = name;
-		this.classReference = new Value<Class<?>>(
-				new Value.DeferredInitializer<Class<?>>() {
+		this.classReference = new ValueHolder<Class<?>>(
+				new ValueHolder.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.classForName( name );
 					}
 				}
 		);
 	}
 
 	public JavaType(Class<?> theClass) {
 		this.name = theClass.getName();
-		this.classReference = new Value<Class<?>>( theClass );
+		this.classReference = new ValueHolder<Class<?>>( theClass );
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Class<?> getClassReference() {
 		return classReference.getValue();
 	}
 
 	@Override
 	public String toString() {
 		return new StringBuilder( super.toString() )
 				.append( "[name=" ).append( name ).append( "]" )
 				.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java
index 5a320e830a..973dc8af6e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/NonEntity.java
@@ -1,55 +1,55 @@
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
 package org.hibernate.metamodel.domain;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Models the concept class in the hierarchy with no persistent attributes.
  *
  * @author Hardy Ferentschik
  */
 public class NonEntity extends AbstractAttributeContainer {
 	/**
 	 * Constructor for the non-entity
 	 *
 	 * @param entityName The name of the non-entity
 	 * @param className The name of this non-entity's java class
 	 * @param classReference The reference to this non-entity's {@link Class}
 	 * @param superType The super type for this non-entity. If there is not super type {@code null} needs to be passed.
 	 */
-	public NonEntity(String entityName, String className, Value<Class<?>> classReference, Hierarchical superType) {
+	public NonEntity(String entityName, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
 		super( entityName, className, classReference, superType );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java
index 54b504f440..4a5a095339 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Superclass.java
@@ -1,55 +1,55 @@
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
 package org.hibernate.metamodel.domain;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Models the concept of a (intermediate) superclass
  *
  * @author Steve Ebersole
  */
 public class Superclass extends AbstractAttributeContainer {
 	/**
 	 * Constructor for the entity
 	 *
 	 * @param entityName The name of the entity
 	 * @param className The name of this entity's java class
 	 * @param classReference The reference to this entity's {@link Class}
 	 * @param superType The super type for this entity. If there is not super type {@code null} needs to be passed.
 	 */
-	public Superclass(String entityName, String className, Value<Class<?>> classReference, Hierarchical superType) {
+	public Superclass(String entityName, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
 		super( entityName, className, classReference, superType );
 	}
 
 	@Override
 	public boolean isAssociation() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponent() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java
index f70cc38362..dac9779b6a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/domain/Type.java
@@ -1,64 +1,64 @@
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
 package org.hibernate.metamodel.domain;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * Basic information about a Java type, in regards to its role in particular set of mappings.
  *
  * @author Steve Ebersole
  */
 public interface Type {
 	/**
 	 * Obtain the name of the type.
 	 *
 	 * @return The name
 	 */
 	public String getName();
 
 	/**
 	 * Obtain the java class name for this type.
 	 *
 	 * @return The class name
 	 */
 	public String getClassName();
 
 	/**
 	 * Obtain the java {@link Class} reference for this type
 	 *
 	 * @return The {@link Class} reference
 	 *
 	 * @throws org.hibernate.service.classloading.spi.ClassLoadingException Indicates the class reference
 	 * could not be determined.  Generally this is the case in reverse-engineering scenarios where the specified
 	 * domain model classes do not yet exist.
 	 */
 	public Class<?> getClassReference();
 
-	public Value<Class<?>> getClassReferenceUnresolved();
+	public ValueHolder<Class<?>> getClassReferenceUnresolved();
 
 	public boolean isAssociation();
 
 	public boolean isComponent();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/BindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/BindingContext.java
index a510b070d6..6fbcfc0c0c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/BindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/BindingContext.java
@@ -1,52 +1,52 @@
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
 
 import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public interface BindingContext {
 	public ServiceRegistry getServiceRegistry();
 
 	public NamingStrategy getNamingStrategy();
 
 	public MappingDefaults getMappingDefaults();
 
 	public MetadataImplementor getMetadataImplementor();
 
 	public <T> Class<T> locateClassByName(String name);
 
 	public Type makeJavaType(String className);
 
 	public boolean isGloballyQuotedIdentifiers();
 
-	public Value<Class<?>> makeClassReference(String className);
+	public ValueHolder<Class<?>> makeClassReference(String className);
 
 	public String qualifyClassName(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContextImpl.java
index c09771b39c..b3aa93d03c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBindingContextImpl.java
@@ -1,161 +1,161 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import com.fasterxml.classmate.MemberResolver;
 import com.fasterxml.classmate.ResolvedType;
 import com.fasterxml.classmate.ResolvedTypeWithMembers;
 import com.fasterxml.classmate.TypeResolver;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 
 import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * @author Steve Ebersole
  */
 public class AnnotationBindingContextImpl implements AnnotationBindingContext {
 	private final MetadataImplementor metadata;
-	private final Value<ClassLoaderService> classLoaderService;
+	private final ValueHolder<ClassLoaderService> classLoaderService;
 	private final Index index;
 	private final TypeResolver typeResolver = new TypeResolver();
 	private final Map<Class<?>, ResolvedType> resolvedTypeCache = new HashMap<Class<?>, ResolvedType>();
 
 	public AnnotationBindingContextImpl(MetadataImplementor metadata, Index index) {
 		this.metadata = metadata;
-		this.classLoaderService = new Value<ClassLoaderService>(
-				new Value.DeferredInitializer<ClassLoaderService>() {
+		this.classLoaderService = new ValueHolder<ClassLoaderService>(
+				new ValueHolder.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return AnnotationBindingContextImpl.this.metadata
 								.getServiceRegistry()
 								.getService( ClassLoaderService.class );
 					}
 				}
 		);
 		this.index = index;
 	}
 
 	@Override
 	public Index getIndex() {
 		return index;
 	}
 
 	@Override
 	public ClassInfo getClassInfo(String name) {
 		DotName dotName = DotName.createSimple( name );
 		return index.getClassByName( dotName );
 	}
 
 	@Override
 	public void resolveAllTypes(String className) {
 		// the resolved type for the top level class in the hierarchy
 		Class<?> clazz = classLoaderService.getValue().classForName( className );
 		ResolvedType resolvedType = typeResolver.resolve( clazz );
 		while ( resolvedType != null ) {
 			// todo - check whether there is already something in the map
 			resolvedTypeCache.put( clazz, resolvedType );
 			resolvedType = resolvedType.getParentClass();
 			if ( resolvedType != null ) {
 				clazz = resolvedType.getErasedType();
 			}
 		}
 	}
 
 	@Override
 	public ResolvedType getResolvedType(Class<?> clazz) {
 		// todo - error handling
 		return resolvedTypeCache.get( clazz );
 	}
 
 	@Override
 	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
 		// todo : is there a reason we create this resolver every time?
 		MemberResolver memberResolver = new MemberResolver( typeResolver );
 		return memberResolver.resolve( type, null, null );
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadataImplementor().getServiceRegistry();
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return metadata.getNamingStrategy();
 	}
 
 	@Override
 	public MappingDefaults getMappingDefaults() {
 		return metadata.getMappingDefaults();
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return metadata;
 	}
 
 	@Override
 	public <T> Class<T> locateClassByName(String name) {
 		return classLoaderService.getValue().classForName( name );
 	}
 
 	private Map<String, Type> nameToJavaTypeMap = new HashMap<String, Type>();
 
 	@Override
 	public Type makeJavaType(String className) {
 		Type javaType = nameToJavaTypeMap.get( className );
 		if ( javaType == null ) {
 			javaType = metadata.makeJavaType( className );
 			nameToJavaTypeMap.put( className, javaType );
 		}
 		return javaType;
 	}
 
 	@Override
-	public Value<Class<?>> makeClassReference(String className) {
-		return new Value<Class<?>>( locateClassByName( className ) );
+	public ValueHolder<Class<?>> makeClassReference(String className) {
+		return new ValueHolder<Class<?>>( locateClassByName( className ) );
 	}
 
 	@Override
 	public String qualifyClassName(String name) {
 		return name;
 	}
 
 	@Override
 	public boolean isGloballyQuotedIdentifiers() {
 		return metadata.isGloballyQuotedIdentifiers();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java
index acc685e23a..3ab8e3d2c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/ComponentAttributeSourceImpl.java
@@ -1,236 +1,236 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.AttributeOverride;
 import org.hibernate.metamodel.source.annotations.attribute.BasicAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SingularAttributeSourceImpl;
 import org.hibernate.metamodel.source.annotations.attribute.ToOneAttributeSourceImpl;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.ComponentAttributeSource;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 
 /**
  * Annotation backed implementation of {@code ComponentAttributeSource}.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
 	private static final String PATH_SEPERATOR = ".";
 	private final EmbeddableClass embeddableClass;
-	private final Value<Class<?>> classReference;
+	private final ValueHolder<Class<?>> classReference;
 	private final Map<String, AttributeOverride> attributeOverrides;
 	private final String path;
 
 	public ComponentAttributeSourceImpl(EmbeddableClass embeddableClass, String parentPath, Map<String, AttributeOverride> attributeOverrides) {
 		this.embeddableClass = embeddableClass;
-		this.classReference = new Value<Class<?>>( embeddableClass.getConfiguredClass() );
+		this.classReference = new ValueHolder<Class<?>>( embeddableClass.getConfiguredClass() );
 		this.attributeOverrides = attributeOverrides;
 		if ( StringHelper.isEmpty( parentPath ) ) {
 			path = embeddableClass.getEmbeddedAttributeName();
 		}
 		else {
 			path = parentPath + "." + embeddableClass.getEmbeddedAttributeName();
 		}
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.COMPONENT;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public String getClassName() {
 		return embeddableClass.getConfiguredClass().getName();
 	}
 
 	@Override
-	public Value<Class<?>> getClassReference() {
+	public ValueHolder<Class<?>> getClassReference() {
 		return classReference;
 	}
 
 	@Override
 	public String getName() {
 		return embeddableClass.getEmbeddedAttributeName();
 	}
 
 	@Override
 	public String getExplicitTuplizerClassName() {
 		return embeddableClass.getCustomTuplizer();
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return embeddableClass.getClassAccessType().toString().toLowerCase();
 	}
 
 	@Override
 	public LocalBindingContext getLocalBindingContext() {
 		return embeddableClass.getLocalBindingContext();
 	}
 
 	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeList = new ArrayList<AttributeSource>();
 		for ( BasicAttribute attribute : embeddableClass.getSimpleAttributes() ) {
 			AttributeOverride attributeOverride = null;
 			String tmp = getPath() + PATH_SEPERATOR + attribute.getName();
 			if ( attributeOverrides.containsKey( tmp ) ) {
 				attributeOverride = attributeOverrides.get( tmp );
 			}
 			attributeList.add( new SingularAttributeSourceImpl( attribute, attributeOverride ) );
 		}
 		for ( EmbeddableClass embeddable : embeddableClass.getEmbeddedClasses().values() ) {
 			attributeList.add(
 					new ComponentAttributeSourceImpl(
 							embeddable,
 							getPath(),
 							createAggregatedOverrideMap()
 					)
 			);
 		}
 		for ( AssociationAttribute associationAttribute : embeddableClass.getAssociationAttributes() ) {
 			attributeList.add( new ToOneAttributeSourceImpl( associationAttribute ) );
 		}
 		return attributeList;
 	}
 
 	@Override
 	public String getPath() {
 		return path;
 	}
 
 	@Override
 	public String getParentReferenceAttributeName() {
 		return embeddableClass.getParentReferencingAttributeName();
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		// not relevant for annotations
 		return Collections.emptySet();
 	}
 
 	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		// none, they are defined on the simple sub-attributes
 		return null;
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		// probably need to check for @Target in EmbeddableClass (HF)
 		return null;
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return true;
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return true;
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesIncludedInInsertByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesIncludedInUpdateByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesNullableByDefault() {
 		return true;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "ComponentAttributeSourceImpl" );
 		sb.append( "{embeddableClass=" ).append( embeddableClass.getConfiguredClass().getSimpleName() );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	private Map<String, AttributeOverride> createAggregatedOverrideMap() {
 		// add all overrides passed down to this instance - they override overrides ;-) which are defined further down
 		// the embeddable chain
 		Map<String, AttributeOverride> aggregatedOverrideMap = new HashMap<String, AttributeOverride>(
 				attributeOverrides
 		);
 
 		for ( Map.Entry<String, AttributeOverride> entry : embeddableClass.getAttributeOverrideMap().entrySet() ) {
 			String fullPath = getPath() + PATH_SEPERATOR + entry.getKey();
 			if ( !aggregatedOverrideMap.containsKey( fullPath ) ) {
 				aggregatedOverrideMap.put( fullPath, entry.getValue() );
 			}
 		}
 		return aggregatedOverrideMap;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBindingContext.java
index 9c4f533cd0..9dccc0d526 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBindingContext.java
@@ -1,130 +1,130 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import com.fasterxml.classmate.ResolvedType;
 import com.fasterxml.classmate.ResolvedTypeWithMembers;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.Index;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.jaxb.SourceType;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * Annotation version of a local binding context.
  * 
  * @author Steve Ebersole
  */
 public class EntityBindingContext implements LocalBindingContext, AnnotationBindingContext {
 	private final AnnotationBindingContext contextDelegate;
 	private final Origin origin;
 
 	public EntityBindingContext(AnnotationBindingContext contextDelegate, ConfiguredClass source) {
 		this.contextDelegate = contextDelegate;
 		this.origin = new Origin( SourceType.ANNOTATION, source.getName() );
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return origin;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return contextDelegate.getServiceRegistry();
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return contextDelegate.getNamingStrategy();
 	}
 
 	@Override
 	public MappingDefaults getMappingDefaults() {
 		return contextDelegate.getMappingDefaults();
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return contextDelegate.getMetadataImplementor();
 	}
 
 	@Override
 	public <T> Class<T> locateClassByName(String name) {
 		return contextDelegate.locateClassByName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		return contextDelegate.makeJavaType( className );
 	}
 
 	@Override
 	public boolean isGloballyQuotedIdentifiers() {
 		return contextDelegate.isGloballyQuotedIdentifiers();
 	}
 
 	@Override
-	public Value<Class<?>> makeClassReference(String className) {
+	public ValueHolder<Class<?>> makeClassReference(String className) {
 		return contextDelegate.makeClassReference( className );
 	}
 
 	@Override
 	public String qualifyClassName(String name) {
 		return contextDelegate.qualifyClassName( name );
 	}
 
 	@Override
 	public Index getIndex() {
 		return contextDelegate.getIndex();
 	}
 
 	@Override
 	public ClassInfo getClassInfo(String name) {
 		return contextDelegate.getClassInfo( name );
 	}
 
 	@Override
 	public void resolveAllTypes(String className) {
 		contextDelegate.resolveAllTypes( className );
 	}
 
 	@Override
 	public ResolvedType getResolvedType(Class<?> clazz) {
 		return contextDelegate.getResolvedType( clazz );
 	}
 
 	@Override
 	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
 		return contextDelegate.resolveMemberTypes( type );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
index f23c26f8aa..a480489397 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ComponentAttributeSource.java
@@ -1,39 +1,39 @@
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
 package org.hibernate.metamodel.source.binder;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * @author Steve Ebersole
  */
 public interface ComponentAttributeSource extends SingularAttributeSource, AttributeSourceContainer {
 	public String getClassName();
 
-	public Value<Class<?>> getClassReference();
+	public ValueHolder<Class<?>> getClassReference();
 
 	public String getParentReferenceAttributeName();
 
 	public String getExplicitTuplizerClassName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/CompositePluralAttributeElementSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/CompositePluralAttributeElementSource.java
index 8b190e32af..0965d93fc8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/CompositePluralAttributeElementSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/CompositePluralAttributeElementSource.java
@@ -1,39 +1,39 @@
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
 package org.hibernate.metamodel.source.binder;
 
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 
 /**
  * @author Steve Ebersole
  */
 public interface CompositePluralAttributeElementSource extends PluralAttributeElementSource, AttributeSourceContainer {
 	public String getClassName();
 
-	public Value<Class<?>> getClassReference();
+	public ValueHolder<Class<?>> getClassReference();
 
 	public String getParentReferenceAttributeName();
 
 	public String getExplicitTuplizerClassName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
index 8002fff345..b091df0f2f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/ComponentAttributeSourceImpl.java
@@ -1,239 +1,239 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.EntityMode;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbAnyElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbComponentElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbManyToManyElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbManyToOneElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbOneToManyElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbOneToOneElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbPropertyElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbTuplizerElement;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.AttributeSourceContainer;
 import org.hibernate.metamodel.source.binder.ComponentAttributeSource;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 
 /**
  * @author Steve Ebersole
  */
 public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
 	private final JaxbComponentElement componentElement;
 	private final AttributeSourceContainer parentContainer;
 
-	private final Value<Class<?>> componentClassReference;
+	private final ValueHolder<Class<?>> componentClassReference;
 	private final String path;
 
 	public ComponentAttributeSourceImpl(
 			JaxbComponentElement componentElement,
 			AttributeSourceContainer parentContainer,
 			LocalBindingContext bindingContext) {
 		this.componentElement = componentElement;
 		this.parentContainer = parentContainer;
 
 		this.componentClassReference = bindingContext.makeClassReference(
 				bindingContext.qualifyClassName( componentElement.getClazz() )
 		);
 		this.path = parentContainer.getPath() + '.' + componentElement.getName();
 	}
 
 	@Override
 	public String getClassName() {
 		return componentElement.getClazz();
 	}
 
 	@Override
-	public Value<Class<?>> getClassReference() {
+	public ValueHolder<Class<?>> getClassReference() {
 		return componentClassReference;
 	}
 
 	@Override
 	public String getPath() {
 		return path;
 	}
 
 	@Override
 	public LocalBindingContext getLocalBindingContext() {
 		return parentContainer.getLocalBindingContext();
 	}
 
 	@Override
 	public String getParentReferenceAttributeName() {
 		return componentElement.getParent() == null ? null : componentElement.getParent().getName();
 	}
 
 	@Override
 	public String getExplicitTuplizerClassName() {
 		if ( componentElement.getTuplizer() == null ) {
 			return null;
 		}
 		final EntityMode entityMode = StringHelper.isEmpty( componentElement.getClazz() ) ? EntityMode.MAP : EntityMode.POJO;
 		for ( JaxbTuplizerElement tuplizerElement : componentElement.getTuplizer() ) {
 			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
 				return tuplizerElement.getClazz();
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
 		for ( Object attributeElement : componentElement.getPropertyOrManyToOneOrOneToOne() ) {
 			if ( JaxbPropertyElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new PropertyAttributeSourceImpl(
 								JaxbPropertyElement.class.cast( attributeElement ),
 								getLocalBindingContext()
 						)
 				);
 			}
 			else if ( JaxbComponentElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ComponentAttributeSourceImpl(
 								(JaxbComponentElement) attributeElement,
 								this,
 								getLocalBindingContext()
 						)
 				);
 			}
 			else if ( JaxbManyToOneElement.class.isInstance( attributeElement ) ) {
 				attributeSources.add(
 						new ManyToOneAttributeSourceImpl(
 								JaxbManyToOneElement.class.cast( attributeElement ),
 								getLocalBindingContext()
 						)
 				);
 			}
 			else if ( JaxbOneToOneElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( JaxbAnyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( JaxbOneToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 			else if ( JaxbManyToManyElement.class.isInstance( attributeElement ) ) {
 				// todo : implement
 			}
 		}
 		return attributeSources;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.COMPONENT;
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		// <component/> does not support type information.
 		return null;
 	}
 
 	@Override
 	public String getName() {
 		return componentElement.getName();
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return componentElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return componentElement.isInsert();
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return componentElement.isUpdate();
 	}
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		// todo : is this correct here?
 		return null;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return componentElement.isLazy();
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return componentElement.isOptimisticLock();
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( componentElement.getMeta() );
 	}
 
 	@Override
 	public boolean areValuesIncludedInInsertByDefault() {
 		return isInsertable();
 	}
 
 	@Override
 	public boolean areValuesIncludedInUpdateByDefault() {
 		return isUpdatable();
 	}
 
 	@Override
 	public boolean areValuesNullableByDefault() {
 		return true;
 	}
 
 	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		// none, they are defined on the simple sub-attributes
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/CompositePluralAttributeElementSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/CompositePluralAttributeElementSourceImpl.java
index 4efafb4621..50181f6380 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/CompositePluralAttributeElementSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/CompositePluralAttributeElementSourceImpl.java
@@ -1,108 +1,108 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.EntityMode;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbCompositeElementElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbTuplizerElement;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.binder.AttributeSource;
 import org.hibernate.metamodel.source.binder.CompositePluralAttributeElementSource;
 import org.hibernate.metamodel.source.binder.PluralAttributeElementNature;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositePluralAttributeElementSourceImpl implements CompositePluralAttributeElementSource {
 	private final JaxbCompositeElementElement compositeElement;
 	private final LocalBindingContext bindingContext;
 
 	public CompositePluralAttributeElementSourceImpl(
 			JaxbCompositeElementElement compositeElement,
 			LocalBindingContext bindingContext) {
 		this.compositeElement = compositeElement;
 		this.bindingContext = bindingContext;
 	}
 
 	@Override
 	public PluralAttributeElementNature getNature() {
 		return PluralAttributeElementNature.COMPONENT;
 	}
 
 	@Override
 	public String getClassName() {
 		return bindingContext.qualifyClassName( compositeElement.getClazz() );
 	}
 
 	@Override
-	public Value<Class<?>> getClassReference() {
+	public ValueHolder<Class<?>> getClassReference() {
 		return bindingContext.makeClassReference( getClassName() );
 	}
 
 	@Override
 	public String getParentReferenceAttributeName() {
 		return compositeElement.getParent() != null
 				? compositeElement.getParent().getName()
 				: null;
 	}
 
 	@Override
 	public String getExplicitTuplizerClassName() {
 		if ( compositeElement.getTuplizer() == null ) {
 			return null;
 		}
 		final EntityMode entityMode = StringHelper.isEmpty( compositeElement.getClazz() ) ? EntityMode.MAP : EntityMode.POJO;
 		for ( JaxbTuplizerElement tuplizerElement : compositeElement.getTuplizer() ) {
 			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
 				return tuplizerElement.getClazz();
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public String getPath() {
 		// todo : implementing this requires passing in the collection source and being able to resolve the collection's role
 		return null;
 	}
 
 	@Override
 	public Iterable<AttributeSource> attributeSources() {
 		List<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
 		for ( Object attribute : compositeElement.getPropertyOrManyToOneOrAny() ) {
 
 		}
 		return attributeSources;
 	}
 
 	@Override
 	public LocalBindingContext getLocalBindingContext() {
 		return bindingContext;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
index 54270fddf6..5769cb950a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
@@ -1,287 +1,287 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbFetchProfileElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbHibernateMapping;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbParamElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbQueryElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbSqlQueryElement;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
  * {@link org.hibernate.metamodel.source.MetadataSourceProcessor}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor {
 	private final MetadataImplementor metadata;
 	private final MappingDocument mappingDocument;
 
-	private Value<ClassLoaderService> classLoaderService = new Value<ClassLoaderService>(
-			new Value.DeferredInitializer<ClassLoaderService>() {
+	private ValueHolder<ClassLoaderService> classLoaderService = new ValueHolder<ClassLoaderService>(
+			new ValueHolder.DeferredInitializer<ClassLoaderService>() {
 				@Override
 				public ClassLoaderService initialize() {
 					return metadata.getServiceRegistry().getService( ClassLoaderService.class );
 				}
 			}
 	);
 
 	public HibernateMappingProcessor(MetadataImplementor metadata, MappingDocument mappingDocument) {
 		this.metadata = metadata;
 		this.mappingDocument = mappingDocument;
 	}
 
 	private JaxbHibernateMapping mappingRoot() {
 		return mappingDocument.getMappingRoot();
 	}
 
 	private Origin origin() {
 		return mappingDocument.getOrigin();
 	}
 
 	private HbmBindingContext bindingContext() {
 		return mappingDocument.getMappingLocalBindingContext();
 	}
 
 	private <T> Class<T> classForName(String name) {
 		return classLoaderService.getValue().classForName( bindingContext().qualifyClassName( name ) );
 	}
 
 	public void processIndependentMetadata() {
 		processDatabaseObjectDefinitions();
 		processTypeDefinitions();
 	}
 
 	private void processDatabaseObjectDefinitions() {
 		if ( mappingRoot().getDatabaseObject() == null ) {
 			return;
 		}
 
 		for ( JaxbHibernateMapping.JaxbDatabaseObject databaseObjectElement : mappingRoot().getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classForName( className ).newInstance();
 				}
 				catch (ClassLoadingException e) {
 					throw e;
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"could not instantiate custom database object class [" + className + "]",
 							origin()
 					);
 				}
 			}
 			else {
 				Set<String> dialectScopes = new HashSet<String>();
 				if ( databaseObjectElement.getDialectScope() != null ) {
 					for ( JaxbHibernateMapping.JaxbDatabaseObject.JaxbDialectScope dialectScope : databaseObjectElement.getDialectScope() ) {
 						dialectScopes.add( dialectScope.getName() );
 					}
 				}
 				auxiliaryDatabaseObject = new BasicAuxiliaryDatabaseObjectImpl(
 						metadata.getDatabase().getDefaultSchema(),
 						databaseObjectElement.getCreate(),
 						databaseObjectElement.getDrop(),
 						dialectScopes
 				);
 			}
 			metadata.getDatabase().addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 		}
 	}
 
 	private void processTypeDefinitions() {
 		if ( mappingRoot().getTypedef() == null ) {
 			return;
 		}
 
 		for ( JaxbHibernateMapping.JaxbTypedef typedef : mappingRoot().getTypedef() ) {
 			final Map<String, String> parameters = new HashMap<String, String>();
 			for ( JaxbParamElement paramElement : typedef.getParam() ) {
 				parameters.put( paramElement.getName(), paramElement.getValue() );
 			}
 			metadata.addTypeDefinition(
 					new TypeDef(
 							typedef.getName(),
 							typedef.getClazz(),
 							parameters
 					)
 			);
 		}
 	}
 
 	public void processTypeDependentMetadata() {
 		processFilterDefinitions();
 		processIdentifierGenerators();
 	}
 
 	private void processFilterDefinitions() {
 		if ( mappingRoot().getFilterDef() == null ) {
 			return;
 		}
 
 		for ( JaxbHibernateMapping.JaxbFilterDef filterDefinition : mappingRoot().getFilterDef() ) {
 			final String name = filterDefinition.getName();
 			final Map<String,Type> parameters = new HashMap<String, Type>();
 			String condition = null;
 			for ( Object o : filterDefinition.getContent() ) {
 				if ( o instanceof String ) {
 					// represents the condition
 					if ( condition != null ) {
 						// log?
 					}
 					condition = (String) o;
 				}
 				else if ( o instanceof JaxbHibernateMapping.JaxbFilterDef.JaxbFilterParam ) {
 					final JaxbHibernateMapping.JaxbFilterDef.JaxbFilterParam paramElement =
 							JaxbHibernateMapping.JaxbFilterDef.JaxbFilterParam.class.cast( o );
 					// todo : should really delay this resolution until later to allow typedef names
 					parameters.put(
 							paramElement.getName(),
 							metadata.getTypeResolver().heuristicType( paramElement.getType() )
 					);
 				}
 				else {
 					throw new MappingException( "Unrecognized nested filter content", origin() );
 				}
 			}
 			if ( condition == null ) {
 				condition = filterDefinition.getCondition();
 			}
 			metadata.addFilterDefinition( new FilterDefinition( name, condition, parameters ) );
 		}
 	}
 
 	private void processIdentifierGenerators() {
 		if ( mappingRoot().getIdentifierGenerator() == null ) {
 			return;
 		}
 
 		for ( JaxbHibernateMapping.JaxbIdentifierGenerator identifierGeneratorElement : mappingRoot().getIdentifierGenerator() ) {
 			metadata.registerIdentifierGenerator(
 					identifierGeneratorElement.getName(),
 					identifierGeneratorElement.getClazz()
 			);
 		}
 	}
 
 	public void processMappingDependentMetadata() {
 		processFetchProfiles();
 		processImports();
 		processResultSetMappings();
 		processNamedQueries();
 	}
 
 	private void processFetchProfiles(){
 		if ( mappingRoot().getFetchProfile() == null ) {
 			return;
 		}
 
 		processFetchProfiles( mappingRoot().getFetchProfile(), null );
 	}
 
 	public void processFetchProfiles(List<JaxbFetchProfileElement> fetchProfiles, String containingEntityName) {
 		for ( JaxbFetchProfileElement fetchProfile : fetchProfiles ) {
 			String profileName = fetchProfile.getName();
 			Set<FetchProfile.Fetch> fetches = new HashSet<FetchProfile.Fetch>();
 			for ( JaxbFetchProfileElement.JaxbFetch fetch : fetchProfile.getFetch() ) {
 				String entityName = fetch.getEntity() == null ? containingEntityName : fetch.getEntity();
 				if ( entityName == null ) {
 					throw new MappingException(
 							"could not determine entity for fetch-profile fetch [" + profileName + "]:[" +
 									fetch.getAssociation() + "]",
 							origin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void processImports() {
 		if ( mappingRoot().getImport() == null ) {
 			return;
 		}
 
 		for ( JaxbHibernateMapping.JaxbImport importValue : mappingRoot().getImport() ) {
 			String className = mappingDocument.getMappingLocalBindingContext().qualifyClassName( importValue.getClazz() );
 			String rename = importValue.getRename();
 			rename = ( rename == null ) ? StringHelper.unqualify( className ) : rename;
 			metadata.addImport( className, rename );
 		}
 	}
 
 	private void processResultSetMappings() {
 		if ( mappingRoot().getResultset() == null ) {
 			return;
 		}
 
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void processNamedQueries() {
 		if ( mappingRoot().getQueryOrSqlQuery() == null ) {
 			return;
 		}
 
 		for ( Object queryOrSqlQuery : mappingRoot().getQueryOrSqlQuery() ) {
 			if ( JaxbQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //					bindNamedQuery( element, null, mappings );
 			}
 			else if ( JaxbSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //				bindNamedSQLQuery( element, null, mappings );
 			}
 			else {
 				throw new MappingException(
 						"unknown type of query: " +
 								queryOrSqlQuery.getClass().getName(), origin()
 				);
 			}
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/MappingDocument.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/MappingDocument.java
index 1f16dd341e..b9d8cb609b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/MappingDocument.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/MappingDocument.java
@@ -1,173 +1,173 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.jaxb.JaxbRoot;
 import org.hibernate.internal.jaxb.Origin;
 import org.hibernate.internal.jaxb.mapping.hbm.EntityElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbFetchProfileElement;
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbHibernateMapping;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.internal.OverriddenMappingDefaults;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * Aggregates together information about a mapping document.
  * 
  * @author Steve Ebersole
  */
 public class MappingDocument {
 	private final JaxbRoot<JaxbHibernateMapping> hbmJaxbRoot;
 	private final LocalBindingContextImpl mappingLocalBindingContext;
 
 	public MappingDocument(JaxbRoot<JaxbHibernateMapping> hbmJaxbRoot, MetadataImplementor metadata) {
 		this.hbmJaxbRoot = hbmJaxbRoot;
 		this.mappingLocalBindingContext = new LocalBindingContextImpl( metadata );
 
 	}
 
 	public JaxbHibernateMapping getMappingRoot() {
 		return hbmJaxbRoot.getRoot();
 	}
 
 	public Origin getOrigin() {
 		return hbmJaxbRoot.getOrigin();
 	}
 
 	public JaxbRoot<JaxbHibernateMapping> getJaxbRoot() {
 		return hbmJaxbRoot;
 	}
 
 	public HbmBindingContext getMappingLocalBindingContext() {
 		return mappingLocalBindingContext;
 	}
 
 	private class LocalBindingContextImpl implements HbmBindingContext {
 		private final MetadataImplementor metadata;
 		private final MappingDefaults localMappingDefaults;
 		private final MetaAttributeContext metaAttributeContext;
 
 		private LocalBindingContextImpl(MetadataImplementor metadata) {
 			this.metadata = metadata;
 			this.localMappingDefaults = new OverriddenMappingDefaults(
 					metadata.getMappingDefaults(),
 					hbmJaxbRoot.getRoot().getPackage(),
 					hbmJaxbRoot.getRoot().getSchema(),
 					hbmJaxbRoot.getRoot().getCatalog(),
 					null,
 					null,
 					hbmJaxbRoot.getRoot().getDefaultCascade(),
 					hbmJaxbRoot.getRoot().getDefaultAccess(),
 					hbmJaxbRoot.getRoot().isDefaultLazy()
 			);
 			if ( hbmJaxbRoot.getRoot().getMeta() == null || hbmJaxbRoot.getRoot().getMeta().isEmpty() ) {
 				this.metaAttributeContext = new MetaAttributeContext( metadata.getGlobalMetaAttributeContext() );
 			}
 			else {
 				this.metaAttributeContext = Helper.extractMetaAttributeContext(
 						hbmJaxbRoot.getRoot().getMeta(),
 						true,
 						metadata.getGlobalMetaAttributeContext()
 				);
 			}
 		}
 
 		@Override
 		public ServiceRegistry getServiceRegistry() {
 			return metadata.getServiceRegistry();
 		}
 
 		@Override
 		public NamingStrategy getNamingStrategy() {
 			return metadata.getNamingStrategy();
 		}
 
 		@Override
 		public MappingDefaults getMappingDefaults() {
 			return localMappingDefaults;
 		}
 
 		@Override
 		public MetadataImplementor getMetadataImplementor() {
 			return metadata;
 		}
 
 		@Override
 		public <T> Class<T> locateClassByName(String name) {
 			return metadata.locateClassByName( name );
 		}
 
 		@Override
 		public Type makeJavaType(String className) {
 			return metadata.makeJavaType( className );
 		}
 
 		@Override
-		public Value<Class<?>> makeClassReference(String className) {
+		public ValueHolder<Class<?>> makeClassReference(String className) {
 			return metadata.makeClassReference( className );
 		}
 
 		@Override
 		public boolean isAutoImport() {
 			return hbmJaxbRoot.getRoot().isAutoImport();
 		}
 
 		@Override
 		public MetaAttributeContext getMetaAttributeContext() {
 			return metaAttributeContext;
 		}
 
 		@Override
 		public Origin getOrigin() {
 			return hbmJaxbRoot.getOrigin();
 		}
 
 		@Override
 		public String qualifyClassName(String unqualifiedName) {
 			return Helper.qualifyIfNeeded( unqualifiedName, getMappingDefaults().getPackageName() );
 		}
 
 		@Override
 		public String determineEntityName(EntityElement entityElement) {
 			return Helper.determineEntityName( entityElement, getMappingDefaults().getPackageName() );
 		}
 
 		@Override
 		public boolean isGloballyQuotedIdentifiers() {
 			return metadata.isGloballyQuotedIdentifiers();
 		}
 
 		@Override
 		public void processFetchProfiles(List<JaxbFetchProfileElement> fetchProfiles, String containingEntityName) {
 			// todo : this really needs to not be part of the context
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
index 2e67eaf7bb..52ef5eeab6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/TimestampAttributeSourceImpl.java
@@ -1,201 +1,201 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbHibernateMapping;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 
 /**
  * Implementation for {@code <timestamp/>} mappings
  *
  * @author Steve Ebersole
  */
 class TimestampAttributeSourceImpl implements SingularAttributeSource {
 	private final JaxbHibernateMapping.JaxbClass.JaxbTimestamp timestampElement;
 	private final LocalBindingContext bindingContext;
 	private final List<RelationalValueSource> valueSources;
 
 	TimestampAttributeSourceImpl(
 			final JaxbHibernateMapping.JaxbClass.JaxbTimestamp timestampElement,
 			LocalBindingContext bindingContext) {
 		this.timestampElement = timestampElement;
 		this.bindingContext = bindingContext;
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return timestampElement.getColumn();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return null;
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return null;
 					}
 
 					@Override
 					public String getContainingTableName() {
 						// by definition the version should come from the primary table of the root entity.
 						return null;
 					}
 
 					@Override
 					public boolean isIncludedInInsertByDefault() {
 						return true;
 					}
 
 					@Override
 					public boolean isIncludedInUpdateByDefault() {
 						return true;
 					}
 				},
 				bindingContext
 		);
 	}
 
 	private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
 		@Override
 		public String getName() {
 			return "db".equals( timestampElement.getSource() ) ? "dbtimestamp" : "timestamp";
 		}
 
 		@Override
 		public Map<String, String> getParameters() {
 			return null;
 		}
 	};
 
 	@Override
 	public String getName() {
 		return timestampElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return typeSource;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return timestampElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return true;
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return true;
 	}
 
-	private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
-			new Value.DeferredInitializer<PropertyGeneration>() {
+	private ValueHolder<PropertyGeneration> propertyGenerationValue = new ValueHolder<PropertyGeneration>(
+			new ValueHolder.DeferredInitializer<PropertyGeneration>() {
 				@Override
 				public PropertyGeneration initialize() {
 					final PropertyGeneration propertyGeneration = timestampElement.getGenerated() == null
 							? PropertyGeneration.NEVER
 							: PropertyGeneration.parse( timestampElement.getGenerated().value() );
 					if ( propertyGeneration == PropertyGeneration.INSERT ) {
 						throw new MappingException(
 								"'generated' attribute cannot be 'insert' for versioning property",
 								bindingContext.getOrigin()
 						);
 					}
 					return propertyGeneration;
 				}
 			}
 	);
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return propertyGenerationValue.getValue();
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.BASIC;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
 	public boolean areValuesIncludedInInsertByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesIncludedInUpdateByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesNullableByDefault() {
 		return true;
 	}
 
 	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( timestampElement.getMeta() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
index 3100bf42f1..79dc3d42e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/VersionAttributeSourceImpl.java
@@ -1,201 +1,201 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.internal.jaxb.mapping.hbm.JaxbHibernateMapping;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.binder.ExplicitHibernateTypeSource;
 import org.hibernate.metamodel.source.binder.MetaAttributeSource;
 import org.hibernate.metamodel.source.binder.RelationalValueSource;
 import org.hibernate.metamodel.source.binder.SingularAttributeNature;
 import org.hibernate.metamodel.source.binder.SingularAttributeSource;
 
 /**
  * Implementation for {@code <version/>} mappings
  *
  * @author Steve Ebersole
  */
 class VersionAttributeSourceImpl implements SingularAttributeSource {
 	private final JaxbHibernateMapping.JaxbClass.JaxbVersion versionElement;
 	private final LocalBindingContext bindingContext;
 	private final List<RelationalValueSource> valueSources;
 
 	VersionAttributeSourceImpl(
 			final JaxbHibernateMapping.JaxbClass.JaxbVersion versionElement,
 			LocalBindingContext bindingContext) {
 		this.versionElement = versionElement;
 		this.bindingContext = bindingContext;
 		this.valueSources = Helper.buildValueSources(
 				new Helper.ValueSourcesAdapter() {
 					@Override
 					public String getColumnAttribute() {
 						return versionElement.getColumnAttribute();
 					}
 
 					@Override
 					public String getFormulaAttribute() {
 						return null;
 					}
 
 					@Override
 					public List getColumnOrFormulaElements() {
 						return versionElement.getColumn();
 					}
 
 					@Override
 					public String getContainingTableName() {
 						// by definition the version should come from the primary table of the root entity.
 						return null;
 					}
 
 					@Override
 					public boolean isIncludedInInsertByDefault() {
 						return Helper.getBooleanValue( versionElement.isInsert(), true );
 					}
 
 					@Override
 					public boolean isIncludedInUpdateByDefault() {
 						return true;
 					}
 				},
 				bindingContext
 		);
 	}
 
 	private final ExplicitHibernateTypeSource typeSource = new ExplicitHibernateTypeSource() {
 		@Override
 		public String getName() {
 			return versionElement.getType() == null ? "integer" : versionElement.getType();
 		}
 
 		@Override
 		public Map<String, String> getParameters() {
 			return null;
 		}
 	};
 
 	@Override
 	public String getName() {
 		return versionElement.getName();
 	}
 
 	@Override
 	public ExplicitHibernateTypeSource getTypeInformation() {
 		return typeSource;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return versionElement.getAccess();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return Helper.getBooleanValue( versionElement.isInsert(), true );
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return true;
 	}
 
-	private Value<PropertyGeneration> propertyGenerationValue = new Value<PropertyGeneration>(
-			new Value.DeferredInitializer<PropertyGeneration>() {
+	private ValueHolder<PropertyGeneration> propertyGenerationValue = new ValueHolder<PropertyGeneration>(
+			new ValueHolder.DeferredInitializer<PropertyGeneration>() {
 				@Override
 				public PropertyGeneration initialize() {
 					final PropertyGeneration propertyGeneration = versionElement.getGenerated() == null
 							? PropertyGeneration.NEVER
 							: PropertyGeneration.parse( versionElement.getGenerated().value() );
 					if ( propertyGeneration == PropertyGeneration.INSERT ) {
 						throw new MappingException(
 								"'generated' attribute cannot be 'insert' for versioning property",
 								bindingContext.getOrigin()
 						);
 					}
 					return propertyGeneration;
 				}
 			}
 	);
 
 	@Override
 	public PropertyGeneration getGeneration() {
 		return propertyGenerationValue.getValue();
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isIncludedInOptimisticLocking() {
 		return false;
 	}
 
 	@Override
 	public SingularAttributeNature getNature() {
 		return SingularAttributeNature.BASIC;
 	}
 
 	@Override
 	public boolean isVirtualAttribute() {
 		return false;
 	}
 
 	@Override
 	public boolean areValuesIncludedInInsertByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesIncludedInUpdateByDefault() {
 		return true;
 	}
 
 	@Override
 	public boolean areValuesNullableByDefault() {
 		return true;
 	}
 
 	@Override
 	public List<RelationalValueSource> relationalValueSources() {
 		return valueSources;
 	}
 
 	@Override
 	public boolean isSingular() {
 		return true;
 	}
 
 	@Override
 	public Iterable<MetaAttributeSource> metaAttributes() {
 		return Helper.buildMetaAttributeSources( versionElement.getMeta() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 3d1f4da4ef..bf6baa9786 100644
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
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.MetadataSourceProcessor;
 import org.hibernate.metamodel.source.annotations.AnnotationMetadataSourceProcessorImpl;
 import org.hibernate.metamodel.source.hbm.HbmMetadataSourceProcessorImpl;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.ServiceRegistry;
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
 
 	private final ServiceRegistry serviceRegistry;
 	private final Options options;
 
-	private final Value<ClassLoaderService> classLoaderService;
-	private final Value<PersisterClassResolver> persisterClassResolverService;
+	private final ValueHolder<ClassLoaderService> classLoaderService;
+	private final ValueHolder<PersisterClassResolver> persisterClassResolverService;
 
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
 
-		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
-				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
+		this.classLoaderService = new ValueHolder<ClassLoaderService>(
+				new ValueHolder.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return serviceRegistry.getService( ClassLoaderService.class );
 					}
 				}
 		);
-		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
-				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
+		this.persisterClassResolverService = new ValueHolder<PersisterClassResolver>(
+				new ValueHolder.DeferredInitializer<PersisterClassResolver>() {
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
 	public ServiceRegistry getServiceRegistry() {
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
-	public Value<Class<?>> makeClassReference(final String className) {
-		return new Value<Class<?>>(
-				new Value.DeferredInitializer<Class<?>>() {
+	public ValueHolder<Class<?>> makeClassReference(final String className) {
+		return new ValueHolder<Class<?>>(
+				new ValueHolder.DeferredInitializer<Class<?>>() {
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
 		LOG.tracev( "Import: {0} -> {1}", importName, entityName );
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
 
-		private final Value<AccessType> regionFactorySpecifiedDefaultAccessType = new Value<AccessType>(
-				new Value.DeferredInitializer<AccessType>() {
+		private final ValueHolder<AccessType> regionFactorySpecifiedDefaultAccessType = new ValueHolder<AccessType>(
+				new ValueHolder.DeferredInitializer<AccessType>() {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
index da905c2be5..df8cf184c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
@@ -1,264 +1,264 @@
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
 import org.hibernate.internal.jaxb.cfg.JaxbHibernateConfiguration;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.internal.util.config.ConfigurationException;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BootstrapServiceRegistryImpl;
 import org.hibernate.service.internal.JaxbProcessor;
 import org.hibernate.service.internal.ProvidedService;
 import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Builder for standard {@link ServiceRegistry} instances.
  *
  * @author Steve Ebersole
  * 
  * @see StandardServiceRegistryImpl
  * @see BootstrapServiceRegistryBuilder
  */
 public class ServiceRegistryBuilder {
 	private static final Logger log = Logger.getLogger( ServiceRegistryBuilder.class );
 
 	public static final String DEFAULT_CFG_RESOURCE_NAME = "hibernate.cfg.xml";
 
 	private final Map settings;
 	private final List<BasicServiceInitiator> initiators = standardInitiatorList();
 	private final List<ProvidedService> providedServices = new ArrayList<ProvidedService>();
 
 	private final BootstrapServiceRegistry bootstrapServiceRegistry;
 
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
 	public ServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry) {
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
 
-	private Value<JaxbProcessor> jaxbProcessorHolder = new Value<JaxbProcessor>(
-			new Value.DeferredInitializer<JaxbProcessor>() {
+	private ValueHolder<JaxbProcessor> jaxbProcessorHolder = new ValueHolder<JaxbProcessor>(
+			new ValueHolder.DeferredInitializer<JaxbProcessor>() {
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
 	public ServiceRegistry buildServiceRegistry() {
 		Map<?,?> settingsCopy = new HashMap();
 		settingsCopy.putAll( settings );
 		Environment.verifyProperties( settingsCopy );
 		ConfigurationHelper.resolvePlaceHolders( settingsCopy );
 
 		for ( Integrator integrator : bootstrapServiceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			if ( ServiceContributingIntegrator.class.isInstance( integrator ) ) {
 				ServiceContributingIntegrator.class.cast( integrator ).prepareServices( this );
 			}
 		}
 
 		return new StandardServiceRegistryImpl( bootstrapServiceRegistry, initiators, providedServices, settingsCopy );
 	}
 
 	/**
 	 * Destroy a service registry.  Applications should only destroy registries they have explicitly created.
 	 *
 	 * @param serviceRegistry The registry to be closed.
 	 */
 	public static void destroy(ServiceRegistry serviceRegistry) {
 		( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
index b2fdebc11b..2b62e8b585 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/SimpleValueBindingTests.java
@@ -1,92 +1,92 @@
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
 
 import org.junit.Test;
 
 import org.hibernate.EntityMode;
-import org.hibernate.internal.util.Value;
+import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertSame;
 
 /**
  * Basic binding "smoke" tests
  *
  * @author Steve Ebersole
  */
 public class SimpleValueBindingTests extends BaseUnitTestCase {
 	public static final Datatype BIGINT = new Datatype( Types.BIGINT, "BIGINT", Long.class );
 	public static final Datatype VARCHAR = new Datatype( Types.VARCHAR, "VARCHAR", String.class );
 
 
 	@Test
 	public void testBasicMiddleOutBuilding() {
 		Table table = new Table( new Schema( null, null ), "the_table" );
 		Entity entity = new Entity( "TheEntity", "NoSuchClass", makeJavaType( "NoSuchClass" ), null );
 		EntityBinding entityBinding = new EntityBinding( InheritanceType.NO_INHERITANCE, EntityMode.POJO );
 		entityBinding.setEntity( entity );
 		entityBinding.setPrimaryTable( table );
 
 		SingularAttribute idAttribute = entity.createSingularAttribute( "id" );
 		BasicAttributeBinding attributeBinding = entityBinding.makeBasicAttributeBinding( idAttribute );
 		attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( "long" );
 		assertSame( idAttribute, attributeBinding.getAttribute() );
 
 		entityBinding.getHierarchyDetails().getEntityIdentifier().setValueBinding( attributeBinding );
 
 		Column idColumn = table.locateOrCreateColumn( "id" );
 		idColumn.setDatatype( BIGINT );
 		idColumn.setSize( Size.precision( 18, 0 ) );
 		table.getPrimaryKey().addColumn( idColumn );
 		table.getPrimaryKey().setName( "my_table_pk" );
 		//attributeBinding.setValue( idColumn );
 	}
 
-	Value<Class<?>> makeJavaType(final String name) {
-		return new Value<Class<?>>(
-				new Value.DeferredInitializer<Class<?>>() {
+	ValueHolder<Class<?>> makeJavaType(final String name) {
+		return new ValueHolder<Class<?>>(
+				new ValueHolder.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						try {
 							return Class.forName( name );
 						}
 						catch ( Exception e ) {
 							throw new ClassLoadingException( "Could not load class : " + name, e );
 						}
 					}
 				}
 		);
 	}
 }
