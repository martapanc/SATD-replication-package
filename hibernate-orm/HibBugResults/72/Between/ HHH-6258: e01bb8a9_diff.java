diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractResultSetProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractResultSetProxyHandler.java
index 26f3623f69..79aaf2c1ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractResultSetProxyHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractResultSetProxyHandler.java
@@ -1,124 +1,126 @@
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
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 
 import org.jboss.logging.Logger;
 
 /**
  * Basic support for building {@link ResultSet}-based proxy handlers
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractResultSetProxyHandler extends AbstractProxyHandler {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractResultSetProxyHandler.class.getName());
 
 	private ResultSet resultSet;
 
 	public AbstractResultSetProxyHandler(ResultSet resultSet) {
 		super( resultSet.hashCode() );
 		this.resultSet = resultSet;
 	}
 
 	protected abstract JdbcServices getJdbcServices();
 
 	protected abstract JdbcResourceRegistry getResourceRegistry();
 
 	protected abstract Statement getExposableStatement();
 
 	protected final ResultSet getResultSet() {
 		errorIfInvalid();
 		return resultSet;
 	}
 
 	protected final ResultSet getResultSetWithoutChecks() {
 		return resultSet;
 	}
 
 	@Override
     protected Object continueInvocation(Object proxy, Method method, Object[] args) throws Throwable {
 		String methodName = method.getName();
-        LOG.trace("Handling invocation of ResultSet method [" + methodName + "]");
+        if (LOG.isTraceEnabled()) {
+           LOG.trace("Handling invocation of ResultSet method [" + methodName + "]");
+        }
 
 		// other methods allowed while invalid ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( "close".equals( methodName ) ) {
 			explicitClose( ( ResultSet ) proxy );
 			return null;
 		}
 		if ( "invalidate".equals( methodName ) ) {
 			invalidateHandle();
 			return null;
 		}
 
 		errorIfInvalid();
 
 		// handle the JDBC 4 Wrapper#isWrapperFor and Wrapper#unwrap calls
 		//		these cause problems to the whole proxy scheme though as we need to return the raw objects
 		if ( "isWrapperFor".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( getResultSetWithoutChecks(), args );
 		}
 		if ( "unwrap".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( getResultSetWithoutChecks(), args );
 		}
 
 		if ( "getWrappedObject".equals( methodName ) ) {
 			return getResultSetWithoutChecks();
 		}
 
 		if ( "getStatement".equals( methodName ) ) {
 			return getExposableStatement();
 		}
 
 		try {
 			return method.invoke( resultSet, args );
 		}
 		catch ( InvocationTargetException e ) {
 			Throwable realException = e.getTargetException();
             if (SQLException.class.isInstance(realException)) throw getJdbcServices().getSqlExceptionHelper().convert((SQLException)realException,
                                                                                                                       realException.getMessage());
             throw realException;
 		}
 	}
 
 	private void explicitClose(ResultSet proxy) {
 		if ( isValid() ) {
 			getResourceRegistry().release( proxy );
 		}
 	}
 
 	protected void invalidateHandle() {
 		resultSet = null;
 		invalidate();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java
index 4a8bb8e446..7c699c8980 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/proxy/AbstractStatementProxyHandler.java
@@ -1,170 +1,172 @@
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
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.sql.Connection;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 
 import org.jboss.logging.Logger;
 
 /**
  * Basic support for building {@link Statement}-based proxy handlers
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStatementProxyHandler extends AbstractProxyHandler {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractStatementProxyHandler.class.getName());
 
 	private ConnectionProxyHandler connectionProxyHandler;
 	private Connection connectionProxy;
 	private Statement statement;
 
 	protected AbstractStatementProxyHandler(
 			Statement statement,
 			ConnectionProxyHandler connectionProxyHandler,
 			Connection connectionProxy) {
 		super( statement.hashCode() );
 		this.statement = statement;
 		this.connectionProxyHandler = connectionProxyHandler;
 		this.connectionProxy = connectionProxy;
 	}
 
 	protected ConnectionProxyHandler getConnectionProxy() {
 		errorIfInvalid();
 		return connectionProxyHandler;
 	}
 
 	protected JdbcServices getJdbcServices() {
 		return getConnectionProxy().getJdbcServices();
 	}
 
 	protected JdbcResourceRegistry getResourceRegistry() {
 		return getConnectionProxy().getResourceRegistry();
 	}
 
 	protected Statement getStatement() {
 		errorIfInvalid();
 		return statement;
 	}
 
 	protected Statement getStatementWithoutChecks() {
 		return statement;
 	}
 
 	@Override
     protected Object continueInvocation(Object proxy, Method method, Object[] args) throws Throwable {
 		String methodName = method.getName();
-        LOG.trace("Handling invocation of statement method [" + methodName + "]");
+        if (LOG.isTraceEnabled()) {
+           LOG.trace("Handling invocation of statement method [" + methodName + "]");
+        }
 
 		// other methods allowed while invalid ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( "close".equals( methodName ) ) {
 			explicitClose( ( Statement ) proxy );
 			return null;
 		}
 		if ( "invalidate".equals( methodName ) ) {
 			invalidateHandle();
 			return null;
 		}
 
 		errorIfInvalid();
 
 		// handle the JDBC 4 Wrapper#isWrapperFor and Wrapper#unwrap calls
 		//		these cause problems to the whole proxy scheme though as we need to return the raw objects
 		if ( "isWrapperFor".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( getStatementWithoutChecks(), args );
 		}
 		if ( "unwrap".equals( methodName ) && args.length == 1 ) {
 			return method.invoke( getStatementWithoutChecks(), args );
 		}
 
 		if ( "getWrappedObject".equals( methodName ) ) {
 			return getStatementWithoutChecks();
 		}
 
 		if ( "getConnection".equals( methodName ) ) {
 			return connectionProxy;
 		}
 
 		beginningInvocationHandling( method, args );
 
 		try {
 			Object result = method.invoke( statement, args );
 			result = wrapIfNecessary( result, proxy, method );
 			return result;
 		}
 		catch ( InvocationTargetException e ) {
 			Throwable realException = e.getTargetException();
 			if ( SQLException.class.isInstance( realException ) ) {
 				throw connectionProxyHandler.getJdbcServices().getSqlExceptionHelper()
 						.convert( ( SQLException ) realException, realException.getMessage() );
 			}
 			else {
 				throw realException;
 			}
 		}
 	}
 
 	private Object wrapIfNecessary(Object result, Object proxy, Method method) {
 		if ( !( ResultSet.class.isAssignableFrom( method.getReturnType() ) ) ) {
 			return result;
 		}
 
 		final ResultSet wrapper;
 		if ( "getGeneratedKeys".equals( method.getName() ) ) {
 			wrapper = ProxyBuilder.buildImplicitResultSet( ( ResultSet ) result, connectionProxyHandler, connectionProxy, ( Statement ) proxy );
 		}
 		else {
 			wrapper = ProxyBuilder.buildResultSet( ( ResultSet ) result, this, ( Statement ) proxy );
 		}
 		getResourceRegistry().register( wrapper );
 		return wrapper;
 	}
 
 	protected void beginningInvocationHandling(Method method, Object[] args) {
 	}
 
 	private void explicitClose(Statement proxy) {
 		if ( isValid() ) {
 			LogicalConnectionImplementor lc = getConnectionProxy().getLogicalConnection();
 			getResourceRegistry().release( proxy );
 			lc.afterStatementExecution();
 		}
 	}
 
 	private void invalidateHandle() {
 		connectionProxyHandler = null;
 		statement = null;
 		invalidate();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
index 5f82978433..b7c69bc15f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
@@ -1,310 +1,312 @@
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
 package org.hibernate.engine.loading.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Maps {@link ResultSet result-sets} to specific contextual data
  * related to processing that {@link ResultSet result-sets}.
  * <p/>
  * Implementation note: internally an {@link IdentityMap} is used to maintain
  * the mappings; {@link IdentityMap} was chosen because I'd rather not be
  * dependent upon potentially bad {@link ResultSet#equals} and {ResultSet#hashCode}
  * implementations.
  * <p/>
  * Considering the JDBC-redesign work, would further like this contextual info
  * not mapped seperately, but available based on the result set being processed.
  * This would also allow maintaining a single mapping as we could reliably get
  * notification of the result-set closing...
  *
  * @author Steve Ebersole
  */
 public class LoadContexts {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, LoadContexts.class.getName());
 
 	private final PersistenceContext persistenceContext;
 	private Map collectionLoadContexts;
 	private Map entityLoadContexts;
 
 	private Map xrefLoadingCollectionEntries;
 
 	/**
 	 * Creates and binds this to the given persistence context.
 	 *
 	 * @param persistenceContext The persistence context to which this
 	 * will be bound.
 	 */
 	public LoadContexts(PersistenceContext persistenceContext) {
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * Retrieves the persistence context to which this is bound.
 	 *
 	 * @return The persistence context to which this is bound.
 	 */
 	public PersistenceContext getPersistenceContext() {
 		return persistenceContext;
 	}
 
 	private SessionImplementor getSession() {
 		return getPersistenceContext().getSession();
 	}
 
 	private EntityMode getEntityMode() {
 		return getSession().getEntityMode();
 	}
 
 
 	// cleanup code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
  	/**
 	 * Release internal state associated with the given result set.
 	 * <p/>
 	 * This should be called when we are done with processing said result set,
 	 * ideally as the result set is being closed.
 	 *
 	 * @param resultSet The result set for which it is ok to release
 	 * associated resources.
 	 */
 	public void cleanup(ResultSet resultSet) {
 		if ( collectionLoadContexts != null ) {
 			CollectionLoadContext collectionLoadContext = ( CollectionLoadContext ) collectionLoadContexts.remove( resultSet );
 			collectionLoadContext.cleanup();
 		}
 		if ( entityLoadContexts != null ) {
 			EntityLoadContext entityLoadContext = ( EntityLoadContext ) entityLoadContexts.remove( resultSet );
 			entityLoadContext.cleanup();
 		}
 	}
 
 	/**
 	 * Release internal state associated with *all* result sets.
 	 * <p/>
 	 * This is intended as a "failsafe" process to make sure we get everything
 	 * cleaned up and released.
 	 */
 	public void cleanup() {
 		if ( collectionLoadContexts != null ) {
 			Iterator itr = collectionLoadContexts.values().iterator();
 			while ( itr.hasNext() ) {
 				CollectionLoadContext collectionLoadContext = ( CollectionLoadContext ) itr.next();
                 LOG.failSafeCollectionsCleanup(collectionLoadContext);
 				collectionLoadContext.cleanup();
 			}
 			collectionLoadContexts.clear();
 		}
 		if ( entityLoadContexts != null ) {
 			Iterator itr = entityLoadContexts.values().iterator();
 			while ( itr.hasNext() ) {
 				EntityLoadContext entityLoadContext = ( EntityLoadContext ) itr.next();
                 LOG.failSafeEntitiesCleanup(entityLoadContext);
 				entityLoadContext.cleanup();
 			}
 			entityLoadContexts.clear();
 		}
 	}
 
 
 	// Collection load contexts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Do we currently have any internal entries corresponding to loading
 	 * collections?
 	 *
 	 * @return True if we currently hold state pertaining to loading collections;
 	 * false otherwise.
 	 */
 	public boolean hasLoadingCollectionEntries() {
 		return ( collectionLoadContexts != null && !collectionLoadContexts.isEmpty() );
 	}
 
 	/**
 	 * Do we currently have any registered internal entries corresponding to loading
 	 * collections?
 	 *
 	 * @return True if we currently hold state pertaining to a registered loading collections;
 	 * false otherwise.
 	 */
 	public boolean hasRegisteredLoadingCollectionEntries() {
 		return ( xrefLoadingCollectionEntries != null && !xrefLoadingCollectionEntries.isEmpty() );
 	}
 
 
 	/**
 	 * Get the {@link CollectionLoadContext} associated with the given
 	 * {@link ResultSet}, creating one if needed.
 	 *
 	 * @param resultSet The result set for which to retrieve the context.
 	 * @return The processing context.
 	 */
 	public CollectionLoadContext getCollectionLoadContext(ResultSet resultSet) {
 		CollectionLoadContext context = null;
         if (collectionLoadContexts == null) collectionLoadContexts = IdentityMap.instantiate(8);
         else context = (CollectionLoadContext)collectionLoadContexts.get(resultSet);
 		if ( context == null ) {
-            LOG.trace("Constructing collection load context for result set [" + resultSet + "]");
+                   if (LOG.isTraceEnabled()) {
+                      LOG.trace("Constructing collection load context for result set [" + resultSet + "]");
+                   }
 			context = new CollectionLoadContext( this, resultSet );
 			collectionLoadContexts.put( resultSet, context );
 		}
 		return context;
 	}
 
 	/**
 	 * Attempt to locate the loading collection given the owner's key.  The lookup here
 	 * occurs against all result-set contexts...
 	 *
 	 * @param persister The collection persister
 	 * @param ownerKey The owner key
 	 * @return The loading collection, or null if not found.
 	 */
 	public PersistentCollection locateLoadingCollection(CollectionPersister persister, Serializable ownerKey) {
 		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey, getEntityMode() ) );
 		if ( lce != null ) {
             if (LOG.isTraceEnabled()) LOG.trace("Returning loading collection: "
                                                 + MessageHelper.collectionInfoString(persister, ownerKey, getSession().getFactory()));
 			return lce.getCollection();
 		}
         // TODO : should really move this log statement to CollectionType, where this is used from...
         if (LOG.isTraceEnabled()) LOG.trace("Creating collection wrapper: "
                                             + MessageHelper.collectionInfoString(persister, ownerKey, getSession().getFactory()));
         return null;
 	}
 
 	// loading collection xrefs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Register a loading collection xref.
 	 * <p/>
 	 * This xref map is used because sometimes a collection is in process of
 	 * being loaded from one result set, but needs to be accessed from the
 	 * context of another "nested" result set processing.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param entryKey The xref collection key
 	 * @param entry The corresponding loading collection entry
 	 */
 	void registerLoadingCollectionXRef(CollectionKey entryKey, LoadingCollectionEntry entry) {
 		if ( xrefLoadingCollectionEntries == null ) {
 			xrefLoadingCollectionEntries = new HashMap();
 		}
 		xrefLoadingCollectionEntries.put( entryKey, entry );
 	}
 
 	/**
 	 * The inverse of {@link #registerLoadingCollectionXRef}.  Here, we are done
 	 * processing the said collection entry, so we remove it from the
 	 * load context.
 	 * <p/>
 	 * The idea here is that other loading collections can now reference said
 	 * collection directly from the {@link PersistenceContext} because it
 	 * has completed its load cycle.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param key The key of the collection we are done processing.
 	 */
 	void unregisterLoadingCollectionXRef(CollectionKey key) {
 		if ( !hasRegisteredLoadingCollectionEntries() ) {
 			return;
 		}
 		xrefLoadingCollectionEntries.remove(key);
 	 }
 
 	/*package*/Map getLoadingCollectionXRefs() {
  		return xrefLoadingCollectionEntries;
  	}
 
 
 	/**
 	 * Locate the LoadingCollectionEntry within *any* of the tracked
 	 * {@link CollectionLoadContext}s.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param key The collection key.
 	 * @return The located entry; or null.
 	 */
 	LoadingCollectionEntry locateLoadingCollectionEntry(CollectionKey key) {
         if (xrefLoadingCollectionEntries == null) return null;
         LOG.trace("Attempting to locate loading collection entry [" + key + "] in any result-set context");
 		LoadingCollectionEntry rtn = ( LoadingCollectionEntry ) xrefLoadingCollectionEntries.get( key );
         if (rtn == null) LOG.trace("Collection [" + key + "] not located in load context");
         else LOG.trace("Collection [" + key + "] located in load context");
 		return rtn;
 	}
 
 	/*package*/void cleanupCollectionXRefs(Set entryKeys) {
 		Iterator itr = entryKeys.iterator();
 		while ( itr.hasNext() ) {
 			final CollectionKey entryKey = (CollectionKey) itr.next();
 			xrefLoadingCollectionEntries.remove( entryKey );
 		}
 	}
 
 
 	// Entity load contexts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// 	* currently, not yet used...
 
 	public EntityLoadContext getEntityLoadContext(ResultSet resultSet) {
 		EntityLoadContext context = null;
 		if ( entityLoadContexts == null ) {
 			entityLoadContexts = IdentityMap.instantiate( 8 );
 		}
 		else {
 			context = ( EntityLoadContext ) entityLoadContexts.get( resultSet );
 		}
 		if ( context == null ) {
 			context = new EntityLoadContext( this, resultSet );
 			entityLoadContexts.put( resultSet, context );
 		}
 		return context;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java
index 4b5ae2cb85..f5e273f88b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionCoordinatorImpl.java
@@ -1,361 +1,373 @@
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
 package org.hibernate.engine.transaction.internal;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.ResourceClosedException;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.spi.JoinStatus;
 import org.hibernate.engine.transaction.spi.SynchronizationRegistry;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionObserver;
 import org.hibernate.engine.transaction.synchronization.internal.RegisteredSynchronization;
 import org.hibernate.engine.transaction.synchronization.internal.SynchronizationCallbackCoordinatorImpl;
 import org.hibernate.engine.transaction.synchronization.spi.SynchronizationCallbackCoordinator;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.jboss.logging.Logger;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.List;
 
 /**
  * Standard implementation of the Hibernate {@link TransactionCoordinator}
  * <p/>
  * IMPL NOTE : Custom serialization handling!
  *
  * @author Steve Ebersole
  */
 public class TransactionCoordinatorImpl implements TransactionCoordinator {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TransactionCoordinatorImpl.class.getName());
 
 	private final transient TransactionContext transactionContext;
 	private final transient JdbcCoordinatorImpl jdbcCoordinator;
         private final transient TransactionFactory transactionFactory;
         private final transient TransactionEnvironment transactionEnvironment;
 
 	private final transient List<TransactionObserver> observers;
 	private final transient SynchronizationRegistryImpl synchronizationRegistry;
 
 	private transient TransactionImplementor currentHibernateTransaction;
 
 	private transient SynchronizationCallbackCoordinatorImpl callbackCoordinator;
 
 	private transient boolean open = true;
 	private transient boolean synchronizationRegistered;
 	private transient boolean ownershipTaken;
 
 	public TransactionCoordinatorImpl(
 			Connection userSuppliedConnection,
 			TransactionContext transactionContext) {
 		this.transactionContext = transactionContext;
 		this.jdbcCoordinator = new JdbcCoordinatorImpl( userSuppliedConnection, this );
                 this.transactionEnvironment = transactionContext.getTransactionEnvironment();
                 this.transactionFactory = this.transactionEnvironment.getTransactionFactory();
 		this.observers = new ArrayList<TransactionObserver>();
 		this.synchronizationRegistry = new SynchronizationRegistryImpl();
 		reset();
 
 		final boolean registerSynchronization = transactionContext.isAutoCloseSessionEnabled()
 		        || transactionContext.isFlushBeforeCompletionEnabled()
 		        || transactionContext.getConnectionReleaseMode() == ConnectionReleaseMode.AFTER_TRANSACTION;
 		if ( registerSynchronization ) {
 			pulse();
 		}
 	}
 
 	public TransactionCoordinatorImpl(
 			TransactionContext transactionContext,
 			JdbcCoordinatorImpl jdbcCoordinator,
 			List<TransactionObserver> observers) {
 		this.transactionContext = transactionContext;
 		this.jdbcCoordinator = jdbcCoordinator;
                 this.transactionEnvironment = transactionContext.getTransactionEnvironment();
                 this.transactionFactory = this.transactionEnvironment.getTransactionFactory();
 		this.observers = observers;
 		this.synchronizationRegistry = new SynchronizationRegistryImpl();
 		reset();
 	}
 
 	/**
 	 * Reset the internal state.
 	 */
 	public void reset() {
 		synchronizationRegistered = false;
 		ownershipTaken = false;
 
 		if ( currentHibernateTransaction != null ) {
 			currentHibernateTransaction.invalidate();
 		}
 		currentHibernateTransaction = transactionFactory().createTransaction( this );
 		if ( transactionContext.shouldAutoJoinTransaction() ) {
 			currentHibernateTransaction.markForJoin();
 			currentHibernateTransaction.join();
 		}
 
 		// IMPL NOTE : reset clears synchronizations (following jta spec), but not observers!
 		synchronizationRegistry.clearSynchronizations();
 	}
 
 	public void afterTransaction(TransactionImplementor hibernateTransaction, int status) {
-		LOG.trace( "after transaction completion" );
+                if (LOG.isTraceEnabled()) {
+		   LOG.trace( "after transaction completion" );
+                }
 
 		final boolean success = JtaStatusHelper.isCommitted( status );
 
                 transactionEnvironment.getStatisticsImplementor().endTransaction( success );
 
 		getJdbcCoordinator().afterTransaction();
 
 		getTransactionContext().afterTransactionCompletion( hibernateTransaction, success );
 		sendAfterTransactionCompletionNotifications( hibernateTransaction, status );
 		reset();
 	}
 
 	private SessionFactoryImplementor sessionFactory() {
 		return transactionEnvironment.getSessionFactory();
 	}
 
 	public boolean isSynchronizationRegistered() {
 		return synchronizationRegistered;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public boolean isTransactionInProgress() {
 		return getTransaction().isActive() && getTransaction().getJoinStatus() == JoinStatus.JOINED;
 	}
 
 	@Override
 	public TransactionContext getTransactionContext() {
 		return transactionContext;
 	}
 
 	@Override
 	public JdbcCoordinator getJdbcCoordinator() {
 		return jdbcCoordinator;
 	}
 
 	private TransactionFactory transactionFactory() {
                 return transactionFactory;
 	}
 
 	private TransactionEnvironment getTransactionEnvironment() {
                 return transactionEnvironment;
 	}
 
 	@Override
 	public TransactionImplementor getTransaction() {
 		if ( ! open ) {
 			throw new ResourceClosedException( "This TransactionCoordinator has been closed" );
 		}
 		pulse();
 		return currentHibernateTransaction;
 	}
 
 	public void afterNonTransactionalQuery(boolean success) {
 		// check to see if the connection is in auto-commit mode (no connection means aggressive connection
 		// release outside a JTA transaction context, so MUST be autocommit mode)
 		boolean isAutocommit = getJdbcCoordinator().getLogicalConnection().isAutoCommit();
 		getJdbcCoordinator().getLogicalConnection().afterTransaction();
 
 		if ( isAutocommit ) {
 			for ( TransactionObserver observer : observers ) {
 				observer.afterCompletion( success, this.getTransaction() );
 			}
 		}
 	}
 
 	@Override
 	public void resetJoinStatus() {
 		getTransaction().resetJoinStatus();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void attemptToRegisterJtaSync() {
 		if ( synchronizationRegistered ) {
 			return;
 		}
 
 		// Has the local transaction (Hibernate facade) taken on the responsibility of driving the transaction inflow?
 		if ( currentHibernateTransaction.isInitiator() ) {
 			return;
 		}
 
 		if ( ! transactionContext.shouldAutoJoinTransaction() ) {
 			if ( currentHibernateTransaction.getJoinStatus() != JoinStatus.MARKED_FOR_JOINED ) {
-				LOG.debug( "Skipping JTA sync registration due to auto join checking" );
+                                if (LOG.isDebugEnabled()) {
+				   LOG.debug( "Skipping JTA sync registration due to auto join checking" );
+                                }
 				return;
 			}
 		}
 
 		// IMPL NOTE : At this point the local callback is the "maybe" one.  The only time that needs to change is if
 		// we are able to successfully register the transaction synchronization in which case the local callback would  become
 		// non driving.  To that end, the following checks are simply opt outs where we are unable to register the
 		// synchronization
 
 		JtaPlatform jtaPlatform = getTransactionEnvironment().getJtaPlatform();
 		if ( jtaPlatform == null ) {
 			// if no jta platform was registered we wont be able to register a jta synchronization
 			return;
 		}
 
 		// Can we resister a synchronization
 		if ( ! jtaPlatform.canRegisterSynchronization() ) {
-			LOG.trace(  "registered JTA platform says we cannot currently resister synchronization; skipping" );
+                        if (LOG.isTraceEnabled()) {
+			   LOG.trace(  "registered JTA platform says we cannot currently resister synchronization; skipping" );
+                        }
 			return;
 		}
 
 		// Should we resister a synchronization
 		if ( ! transactionFactory().isJoinableJtaTransaction( this, currentHibernateTransaction ) ) {
-			LOG.trace( "TransactionFactory reported no JTA transaction to join; skipping Synchronization registration" );
+                        if (LOG.isTraceEnabled()) {
+			   LOG.trace( "TransactionFactory reported no JTA transaction to join; skipping Synchronization registration" );
+                        }
 			return;
 		}
 
 		jtaPlatform.registerSynchronization( new RegisteredSynchronization( getSynchronizationCallbackCoordinator() ) );
 		synchronizationRegistered = true;
-		LOG.debug( "successfully registered Synchronization" );
+                if (LOG.isDebugEnabled()) {
+		   LOG.debug( "successfully registered Synchronization" );
+                }
 	}
 
 	@Override
 	public SynchronizationCallbackCoordinator getSynchronizationCallbackCoordinator() {
 		if ( callbackCoordinator == null ) {
 			callbackCoordinator = new SynchronizationCallbackCoordinatorImpl( this );
 		}
 		return callbackCoordinator;
 	}
 
 	public void pulse() {
-		LOG.trace( "Starting transaction coordinator pulse" );
+                if (LOG.isTraceEnabled()) {
+		   LOG.trace( "Starting transaction coordinator pulse" );
+                }
 		if ( transactionFactory().compatibleWithJtaSynchronization() ) {
 			// the configured transaction strategy says it supports callbacks via JTA synchronization, so attempt to
 			// register JTA synchronization if possible
 			attemptToRegisterJtaSync();
 		}
 	}
 
 	public Connection close() {
 		open = false;
 		reset();
 		observers.clear();
 		return jdbcCoordinator.close();
 	}
 
 	public SynchronizationRegistry getSynchronizationRegistry() {
 		return synchronizationRegistry;
 	}
 
 	public void addObserver(TransactionObserver observer) {
 		observers.add( observer );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public boolean isTransactionJoinable() {
 		return transactionFactory().isJoinableJtaTransaction( this, currentHibernateTransaction );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public boolean isTransactionJoined() {
 		return currentHibernateTransaction != null && currentHibernateTransaction.getJoinStatus() == JoinStatus.JOINED;
 	}
 
 	public void setRollbackOnly() {
 		getTransaction().markRollbackOnly();
 	}
 
 	@Override
 	public boolean takeOwnership() {
 		if ( ownershipTaken ) {
 			return false;
 		}
 		else {
 			ownershipTaken = true;
 			return true;
 		}
 	}
 
 	@Override
 	public void sendAfterTransactionBeginNotifications(TransactionImplementor hibernateTransaction) {
 		for ( TransactionObserver observer : observers ) {
 			observer.afterBegin( currentHibernateTransaction );
 		}
 	}
 
 	@Override
 	public void sendBeforeTransactionCompletionNotifications(TransactionImplementor hibernateTransaction) {
 		synchronizationRegistry.notifySynchronizationsBeforeTransactionCompletion();
 		for ( TransactionObserver observer : observers ) {
 			observer.beforeCompletion( hibernateTransaction );
 		}
 	}
 
 	@Override
 	public void sendAfterTransactionCompletionNotifications(TransactionImplementor hibernateTransaction, int status) {
 		final boolean successful = JtaStatusHelper.isCommitted( status );
 		for ( TransactionObserver observer : observers ) {
 			observer.afterCompletion( successful, hibernateTransaction );
 		}
 		synchronizationRegistry.notifySynchronizationsAfterTransactionCompletion( status );
 	}
 
 
 	// serialization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		jdbcCoordinator.serialize( oos );
 		oos.writeInt( observers.size() );
 		for ( TransactionObserver observer : observers ) {
 			oos.writeObject( observer );
 		}
 	}
 
 	public static TransactionCoordinatorImpl deserialize(
 			ObjectInputStream ois,
 			TransactionContext transactionContext) throws ClassNotFoundException, IOException {
 		final JdbcCoordinatorImpl jdbcCoordinator = JdbcCoordinatorImpl.deserialize( ois, transactionContext );
 		final int observerCount = ois.readInt();
 		final List<TransactionObserver> observers = CollectionHelper.arrayList( observerCount );
 		for ( int i = 0; i < observerCount; i++ ) {
 			observers.add( (TransactionObserver) ois.readObject() );
 		}
 		final TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( transactionContext, jdbcCoordinator, observers );
 		jdbcCoordinator.afterDeserialize( transactionCoordinator );
 		return transactionCoordinator;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
index 28e084013e..7cfbc3c797 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
@@ -1,386 +1,390 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.Collections;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.internal.util.collections.LazyIterator;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.Printer;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 
 /**
  * A convenience base class for listeners whose functionality results in flushing.
  *
  * @author Steve Eberole
  */
 public abstract class AbstractFlushingEventListener implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractFlushingEventListener.class.getName()
 	);
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Pre-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Coordinates the processing necessary to get things ready for executions
 	 * as db calls by preping the session caches and moving the appropriate
 	 * entities and collections to their respective execution queues.
 	 *
 	 * @param event The flush event.
 	 * @throws HibernateException Error flushing caches to execution queues.
 	 */
 	protected void flushEverythingToExecutions(FlushEvent event) throws HibernateException {
 
         LOG.trace("Flushing session");
 
 		EventSource session = event.getSession();
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		session.getInterceptor().preFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 		prepareEntityFlushes(session);
 		// we could move this inside if we wanted to
 		// tolerate collection initializations during
 		// collection dirty checking:
 		prepareCollectionFlushes(session);
 		// now, any collections that are initialized
 		// inside this block do not get updated - they
 		// are ignored until the next flush
 
 		persistenceContext.setFlushing(true);
 		try {
 			flushEntities(event);
 			flushCollections(session);
 		}
 		finally {
 			persistenceContext.setFlushing(false);
 		}
 
 		//some statistics
         if (LOG.isDebugEnabled()) {
             LOG.debugf(
 					"Flushed: %s insertions, %s updates, %s deletions to %s objects",
 					session.getActionQueue().numberOfInsertions(),
 					session.getActionQueue().numberOfUpdates(),
 					session.getActionQueue().numberOfDeletions(),
 					persistenceContext.getEntityEntries().size()
 			);
             LOG.debugf(
 					"Flushed: %s (re)creations, %s updates, %s removals to %s collections",
 					session.getActionQueue().numberOfCollectionCreations(),
 					session.getActionQueue().numberOfCollectionUpdates(),
 					session.getActionQueue().numberOfCollectionRemovals(),
 					persistenceContext.getCollectionEntries().size()
 			);
 			new Printer( session.getFactory() ).toString(
 					persistenceContext.getEntitiesByKey().values().iterator(),
 					session.getEntityMode()
 				);
 		}
 	}
 
 	/**
 	 * process cascade save/update at the start of a flush to discover
 	 * any newly referenced entity that must be passed to saveOrUpdate(),
 	 * and also apply orphan delete
 	 */
 	private void prepareEntityFlushes(EventSource session) throws HibernateException {
 
         LOG.debugf( "Processing flush-time cascades" );
 
 		final Map.Entry[] list = IdentityMap.concurrentEntries( session.getPersistenceContext().getEntityEntries() );
 		//safe from concurrent modification because of how entryList() is implemented on IdentityMap
 		final int size = list.length;
 		final Object anything = getAnything();
 		for ( int i=0; i<size; i++ ) {
 			Map.Entry me = list[i];
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 			if ( status == Status.MANAGED || status == Status.SAVING || status == Status.READ_ONLY ) {
 				cascadeOnFlush( session, entry.getPersister(), me.getKey(), anything );
 			}
 		}
 	}
 
 	private void cascadeOnFlush(EventSource session, EntityPersister persister, Object object, Object anything)
 	throws HibernateException {
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadingAction(), Cascade.BEFORE_FLUSH, session )
 			.cascade( persister, object, anything );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected Object getAnything() { return null; }
 
 	protected CascadingAction getCascadingAction() {
 		return CascadingAction.SAVE_UPDATE;
 	}
 
 	/**
 	 * Initialize the flags of the CollectionEntry, including the
 	 * dirty check.
 	 */
 	private void prepareCollectionFlushes(SessionImplementor session) throws HibernateException {
 
 		// Initialize dirty flags for arrays + collections with composite elements
 		// and reset reached, doupdate, etc.
 
         LOG.debugf( "Dirty checking collections" );
 
 		final List list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		final int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry e = ( Map.Entry ) list.get( i );
 			( (CollectionEntry) e.getValue() ).preFlush( (PersistentCollection) e.getKey() );
 		}
 	}
 
 	/**
 	 * 1. detect any dirty entities
 	 * 2. schedule any entity updates
 	 * 3. search out any reachable collections
 	 */
 	private void flushEntities(FlushEvent event) throws HibernateException {
 
         LOG.trace("Flushing entities and processing referenced collections");
 
 		// Among other things, updateReachables() will recursively load all
 		// collections that are moving roles. This might cause entities to
 		// be loaded.
 
 		// So this needs to be safe from concurrent modification problems.
 		// It is safe because of how IdentityMap implements entrySet()
 
 		final EventSource source = event.getSession();
 
 		final Map.Entry[] list = IdentityMap.concurrentEntries( source.getPersistenceContext().getEntityEntries() );
 		final int size = list.length;
 		for ( int i = 0; i < size; i++ ) {
 
 			// Update the status of the object and if necessary, schedule an update
 
 			Map.Entry me = list[i];
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 
 			if ( status != Status.LOADING && status != Status.GONE ) {
 				final FlushEntityEvent entityEvent = new FlushEntityEvent( source, me.getKey(), entry );
 				final EventListenerGroup<FlushEntityEventListener> listenerGroup = source
 						.getFactory()
 						.getServiceRegistry()
 						.getService( EventListenerRegistry.class )
 						.getEventListenerGroup( EventType.FLUSH_ENTITY );
 				for ( FlushEntityEventListener listener : listenerGroup.listeners() ) {
 					listener.onFlushEntity( entityEvent );
 				}
 			}
 		}
 
 		source.getActionQueue().sortActions();
 	}
 
 	/**
 	 * process any unreferenced collections and then inspect all known collections,
 	 * scheduling creates/removes/updates
 	 */
 	private void flushCollections(EventSource session) throws HibernateException {
 
-        LOG.trace("Processing unreferenced collections");
+                if (LOG.isTraceEnabled()) {
+                   LOG.trace("Processing unreferenced collections");
+                }
 
 		List list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry me = ( Map.Entry ) list.get( i );
 			CollectionEntry ce = (CollectionEntry) me.getValue();
 			if ( !ce.isReached() && !ce.isIgnore() ) {
 				Collections.processUnreachableCollection( (PersistentCollection) me.getKey(), session );
 			}
 		}
 
 		// Schedule updates to collections:
 
-        LOG.trace("Scheduling collection removes/(re)creates/updates");
+                if (LOG.isTraceEnabled()) {
+                   LOG.trace("Scheduling collection removes/(re)creates/updates");
+                }
 
 		list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		size = list.size();
 		ActionQueue actionQueue = session.getActionQueue();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry me = (Map.Entry) list.get(i);
 			PersistentCollection coll = (PersistentCollection) me.getKey();
 			CollectionEntry ce = (CollectionEntry) me.getValue();
 
 			if ( ce.isDorecreate() ) {
 				session.getInterceptor().onCollectionRecreate( coll, ce.getCurrentKey() );
 				actionQueue.addAction(
 						new CollectionRecreateAction(
 								coll,
 								ce.getCurrentPersister(),
 								ce.getCurrentKey(),
 								session
 							)
 					);
 			}
 			if ( ce.isDoremove() ) {
 				session.getInterceptor().onCollectionRemove( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionRemoveAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 			if ( ce.isDoupdate() ) {
 				session.getInterceptor().onCollectionUpdate( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionUpdateAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 
 		}
 
 		actionQueue.sortCollectionActions();
 
 	}
 
 	/**
 	 * Execute all SQL and second-level cache updates, in a
 	 * special order so that foreign-key constraints cannot
 	 * be violated:
 	 * <ol>
 	 * <li> Inserts, in the order they were performed
 	 * <li> Updates
 	 * <li> Deletion of collection elements
 	 * <li> Insertion of collection elements
 	 * <li> Deletes, in the order they were performed
 	 * </ol>
 	 */
 	protected void performExecutions(EventSource session) throws HibernateException {
 
         LOG.trace("Executing flush");
 
 		try {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushBeginning();
 			// we need to lock the collection caches before
 			// executing entity inserts/updates in order to
 			// account for bidi associations
 			session.getActionQueue().prepareActions();
 			session.getActionQueue().executeActions();
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushEnding();
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Post-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * 1. Recreate the collection key -> collection map
 	 * 2. rebuild the collection entries
 	 * 3. call Interceptor.postFlush()
 	 */
 	protected void postFlush(SessionImplementor session) throws HibernateException {
 
         LOG.trace("Post flush");
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		persistenceContext.getCollectionsByKey().clear();
 		persistenceContext.getBatchFetchQueue()
 				.clearSubselects(); //the database has changed now, so the subselect results need to be invalidated
 
 		Iterator iter = persistenceContext.getCollectionEntries().entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			CollectionEntry collectionEntry = (CollectionEntry) me.getValue();
 			PersistentCollection persistentCollection = (PersistentCollection) me.getKey();
 			collectionEntry.postFlush(persistentCollection);
 			if ( collectionEntry.getLoadedPersister() == null ) {
 				//if the collection is dereferenced, remove from the session cache
 				//iter.remove(); //does not work, since the entrySet is not backed by the set
 				persistenceContext.getCollectionEntries()
 						.remove(persistentCollection);
 			}
 			else {
 				//otherwise recreate the mapping between the collection and its key
 				CollectionKey collectionKey = new CollectionKey(
 						collectionEntry.getLoadedPersister(),
 						collectionEntry.getLoadedKey(),
 						session.getEntityMode()
 					);
 				persistenceContext.getCollectionsByKey()
 						.put(collectionKey, persistentCollection);
 			}
 		}
 
 		session.getInterceptor().postFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 7f3cd4c1ef..ae4006561e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1884 +1,1890 @@
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
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Abstract superclass of object loading (and querying) strategies. This class implements
  * useful common functionality that concrete loaders delegate to. It is not intended that this
  * functionality would be directly accessed by client code. (Hence, all methods of this class
  * are declared <tt>protected</tt> or <tt>private</tt>.) This class relies heavily upon the
  * <tt>Loadable</tt> interface, which is the contract between this class and
  * <tt>EntityPersister</tt>s that may be loaded by it.<br>
  * <br>
  * The present implementation is able to load any number of columns of entities and at most
  * one collection role per query.
  *
  * @author Gavin King
  * @see org.hibernate.persister.entity.Loadable
  */
 public abstract class Loader {
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
 
 	private final SessionFactoryImplementor factory;
 	private ColumnNameCache columnNameCache;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	protected abstract String getSQLString();
 
 	/**
 	 * An array of persisters of entity classes contained in each row of results;
 	 * implemented by all subclasses
 	 *
 	 * @return The entity persisters.
 	 */
 	protected abstract Loadable[] getEntityPersisters();
 
 	/**
 	 * An array indicating whether the entities have eager property fetching
 	 * enabled.
 	 *
 	 * @return Eager property fetching indicators.
 	 */
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return null;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner").  The
 	 * indexes contained here are relative to the result of
 	 * {@link #getEntityPersisters}.
 	 *
 	 * @return The owner indicators (see discussion above).
 	 */
 	protected int[] getOwners() {
 		return null;
 	}
 
 	/**
 	 * An array of the owner types corresponding to the {@link #getOwners()}
 	 * returns.  Indices indicating no owner would be null here.
 	 *
 	 * @return The types for the owners.
 	 */
 	protected EntityType[] getOwnerAssociationTypes() {
 		return null;
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only
 	 * collection loaders return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return null;
 	}
 
 	/**
 	 * Get the index of the entity that owns the collection, or -1
 	 * if there is no owner in the query results (ie. in the case of a
 	 * collection initializer) or no collection.
 	 */
 	protected int[] getCollectionOwners() {
 		return null;
 	}
 
 	protected int[][] getCompositeKeyManyToOneTargetIndices() {
 		return null;
 	}
 
 	/**
 	 * What lock options does this load entities with?
 	 *
 	 * @param lockOptions a collection of lock options specified dynamically via the Query interface
 	 */
 	//protected abstract LockOptions[] getLockOptions(Map lockOptions);
 	protected abstract LockMode[] getLockModes(LockOptions lockOptions);
 
 	/**
 	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
 	 * empty superclass implementation merely returns its first
 	 * argument.
 	 */
 	protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws HibernateException {
 		return sql;
 	}
 
 	/**
 	 * Does this query return objects that might be already cached
 	 * by the session, whose lock mode may need upgrading
 	 */
 	protected boolean upgradeLocks() {
 		return false;
 	}
 
 	/**
 	 * Return false is this loader is a batch entity loader
 	 */
 	protected boolean isSingleRowLoader() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL table aliases of entities whose
 	 * associations are subselect-loadable, returning
 	 * null if this loader does not support subselect
 	 * loading
 	 */
 	protected String[] getAliases() {
 		return null;
 	}
 
 	/**
 	 * Modify the SQL, adding lock hints and comments, if necessary
 	 */
 	protected String preprocessSQL(String sql, QueryParameters parameters, Dialect dialect)
 			throws HibernateException {
 
 		sql = applyLocks( sql, parameters.getLockOptions(), dialect );
 
 		return getFactory().getSettings().isCommentsEnabled() ?
 				prependComment( sql, parameters ) : sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuffer( comment.length() + sql.length() + 5 )
 					.append( "/* " )
 					.append( comment )
 					.append( " */ " )
 					.append( sql )
 					.toString();
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	private List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
 		return doQueryAndInitializeNonLazyCollections(
 				session,
 				queryParameters,
 				returnProxies,
 				null
 		);
 	}
 
 	private List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException, SQLException {
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
 		persistenceContext.beforeLoad();
 		List result;
 		try {
 			try {
 				result = doQuery( session, queryParameters, returnProxies, forcedResultTransformer );
 			}
 			finally {
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 		return result;
 	}
 
 	/**
 	 * Loads a single row from the result set.  This is the processing used from the
 	 * ScrollableResults where no collection fetches were encountered.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		final Object result;
 		try {
 			result = getRowFromResultSet(
 			        resultSet,
 					session,
 					queryParameters,
 					getLockModes( queryParameters.getLockOptions() ),
 					null,
 					hydratedObjects,
 					new EntityKey[entitySpan],
 					returnProxies
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not read next row of results",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 			);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private Object sequentialLoad(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final EntityKey keyToRead) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		Object result = null;
 		final EntityKey[] loadedKeys = new EntityKey[entitySpan];
 
 		try {
 			do {
 				Object loaded = getRowFromResultSet(
 						resultSet,
 						session,
 						queryParameters,
 						getLockModes( queryParameters.getLockOptions() ),
 						null,
 						hydratedObjects,
 						loadedKeys,
 						returnProxies
 					);
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( keyToRead.equals( loadedKeys[0] ) && resultSet.next() );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 			);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isAfterLast() ) {
 				// don't even bother trying to read further
 				return null;
 			}
 
 			if ( resultSet.isBeforeFirst() ) {
 				resultSet.next();
 			}
 
 			// We call getKeyFromResultSet() here so that we can know the
 			// key value upon which to doAfterTransactionCompletion the breaking logic.  However,
 			// it is also then called from getRowFromResultSet() which is certainly
 			// not the most efficient.  But the call here is needed, and there
 			// currently is no other way without refactoring of the doQuery()/getRowFromResultSet()
 			// methods
 			final EntityKey currentKey = getKeyFromResultSet(
 					0,
 					getEntityPersisters()[0],
 					null,
 					resultSet,
 					session
 				);
 
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, currentKey );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final boolean isLogicallyAfterLast) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isFirst() ) {
 				// don't even bother trying to read any further
 				return null;
 			}
 
 			EntityKey keyToRead = null;
 			// This check is needed since processing leaves the cursor
 			// after the last physical row for the current logical row;
 			// thus if we are after the last physical row, this might be
 			// caused by either:
 			//      1) scrolling to the last logical row
 			//      2) scrolling past the last logical row
 			// In the latter scenario, the previous logical row
 			// really is the last logical row.
 			//
 			// In all other cases, we should process back two
 			// logical records (the current logic row, plus the
 			// previous logical row).
 			if ( resultSet.isAfterLast() && isLogicallyAfterLast ) {
 				// position cursor to the last row
 				resultSet.last();
 				keyToRead = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 			}
 			else {
 				// Since the result set cursor is always left at the first
 				// physical row after the "last processed", we need to jump
 				// back one position to get the key value we are interested
 				// in skipping
 				resultSet.previous();
 
 				// sequentially read the result set in reverse until we recognize
 				// a change in the key value.  At that point, we are pointed at
 				// the last physical sequential row for the logical row in which
 				// we are interested in processing
 				boolean firstPass = true;
 				final EntityKey lastKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 				while ( resultSet.previous() ) {
 					EntityKey checkKey = getKeyFromResultSet(
 							0,
 							getEntityPersisters()[0],
 							null,
 							resultSet,
 							session
 						);
 
 					if ( firstPass ) {
 						firstPass = false;
 						keyToRead = checkKey;
 					}
 
 					if ( !lastKey.equals( checkKey ) ) {
 						break;
 					}
 				}
 
 			}
 
 			// Read backwards until we read past the first physical sequential
 			// row with the key we are interested in loading
 			while ( resultSet.previous() ) {
 				EntityKey checkKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 
 				if ( !keyToRead.equals( checkKey ) ) {
 					break;
 				}
 			}
 
 			// Finally, read ahead one row to position result set cursor
 			// at the first physical row we are interested in loading
 			resultSet.next();
 
 			// and doAfterTransactionCompletion the load
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, keyToRead );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies) throws SQLException, HibernateException {
 		return getRowFromResultSet(
 				resultSet,
 				session,
 				queryParameters,
 				lockModesArray,
 				optionalObjectKey,
 				hydratedObjects,
 				keys,
 				returnProxies,
 				null
 		);
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies,
 	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
 		registerNonExists( keys, persisters, session );
 
 		// this call is side-effecty
 		Object[] row = getRow(
 		        resultSet,
 				persisters,
 				keys,
 				queryParameters.getOptionalObject(),
 				optionalObjectKey,
 				lockModesArray,
 				hydratedObjects,
 				session
 		);
 
 		readCollectionElements( row, resultSet, session );
 
 		if ( returnProxies ) {
 			// now get an existing proxy for each row element (if there is one)
 			for ( int i = 0; i < entitySpan; i++ ) {
 				Object entity = row[i];
 				Object proxy = session.getPersistenceContext().proxyFor( persisters[i], keys[i], entity );
 				if ( entity != proxy ) {
 					// force the proxy to resolve itself
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null ?
 				getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session ) :
 				forcedResultTransformer.transformTuple(
 						getResultRow( row, resultSet, session ),
 						getResultRowAliases()
 				)
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
 		}
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			if ( idType.isComponentType() && getCompositeKeyManyToOneTargetIndices() != null ) {
 				// we may need to force resolve any key-many-to-one(s)
 				int[] keyManyToOneTargetIndices = getCompositeKeyManyToOneTargetIndices()[i];
 				// todo : better solution is to order the index processing based on target indices
 				//		that would account for multiple levels whereas this scheme does not
 				if ( keyManyToOneTargetIndices != null ) {
 					for ( int targetIndex : keyManyToOneTargetIndices ) {
 						if ( targetIndex < numberOfPersistersToProcess ) {
 							final Type targetIdType = persisters[targetIndex].getIdentifierType();
 							final Serializable targetId = (Serializable) targetIdType.resolve(
 									hydratedKeyState[targetIndex],
 									session,
 									null
 							);
 							// todo : need a way to signal that this key is resolved and its data resolved
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
 						}
 
 						// this part copied from #getRow, this section could be refactored out
 						Object object = session.getEntityUsingInterceptor( keys[targetIndex] );
 						if ( object != null ) {
 							//its already loaded so don't need to hydrate it
 							instanceAlreadyLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									keys[targetIndex],
 									object,
 									lockModes[targetIndex],
 									session
 							);
 						}
 						else {
 							instanceNotYetLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									getEntityAliases()[targetIndex].getRowIdAlias(),
 									keys[targetIndex],
 									lockModes[targetIndex],
 									getOptionalObjectKey( queryParameters, session ),
 									queryParameters.getOptionalObject(),
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	private Serializable determineResultId(SessionImplementor session, Serializable optionalId, Type idType, Serializable resolvedId) {
 		final boolean idIsResultId = optionalId != null
 				&& resolvedId != null
 				&& idType.isEqual( optionalId, resolvedId, session.getEntityMode(), factory );
 		final Serializable resultId = idIsResultId ? optionalId : resolvedId;
 		return resultId;
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
 						null; //if null, owner will be retrieved from session
 
 				final CollectionPersister collectionPersister = collectionPersisters[i];
 				final Serializable key;
 				if ( owner == null ) {
 					key = null;
 				}
 				else {
 					key = collectionPersister.getCollectionType().getKeyOfOwner( owner, session );
 					//TODO: old version did not require hashmap lookup:
 					//keys[collectionOwner].getIdentifier()
 				}
 
 				readCollectionElement(
 						owner,
 						key,
 						collectionPersister,
 						descriptors[i],
 						resultSet,
 						session
 					);
 
 			}
 
 		}
 	}
 
 	private List doQuery(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = hasMaxRows( selection ) ?
 				selection.getMaxRows().intValue() :
 				Integer.MAX_VALUE;
 
 		final int entitySpan = getEntityPersisters().length;
 
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 		final ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), selection, session );
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final List results = new ArrayList();
 
 		try {
 
 			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 
 			EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 
-            LOG.trace("Processing result set");
+                        if (LOG.isTraceEnabled()) {
+                           LOG.trace("Processing result set");
+                        }
 
 			int count;
 			for ( count = 0; count < maxRows && rs.next(); count++ ) {
 
-                LOG.debugf("Result set row: %s", count);
+                                if (LOG.isDebugEnabled()) {
+                                   LOG.debugf("Result set row: %s", count);
+                                }
 
 				Object result = getRowFromResultSet(
 						rs,
 						session,
 						queryParameters,
 						lockModesArray,
 						optionalObjectKey,
 						hydratedObjects,
 						keys,
 						returnProxies,
 						forcedResultTransformer
 				);
 				results.add( result );
 
 				if ( createSubselects ) {
 					subselectResultKeys.add(keys);
 					keys = new EntityKey[entitySpan]; //can't reuse in this case
 				}
 
 			}
 
-            LOG.trace("Done processing result set (" + count + " rows)");
+                        if (LOG.isTraceEnabled()) {
+                           LOG.trace("Done processing result set (" + count + " rows)");
+                        }
 
 		}
 		finally {
 			st.close();
 		}
 
 		initializeEntitiesAndCollections( hydratedObjects, rs, session, queryParameters.isReadOnly( session ) );
 
 		if ( createSubselects ) createSubselects( subselectResultKeys, queryParameters, session );
 
 		return results; //getResultList(results);
 
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
 								aliases[i],
 								loadables[i],
 								queryParameters,
 								keySets[i],
 								namedParameterLocMap
 							);
 
 						session.getPersistenceContext()
 								.getBatchFetchQueue()
 								.addSubselect( rowKeys[i], subselectFetch );
 					}
 
 				}
 
 			}
 		}
 	}
 
 	private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
 					);
 			}
 			return namedParameterLocMap;
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly)
 	throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 		//important: reuse the same event instances for performance!
 		final PreLoadEvent pre;
 		final PostLoadEvent post;
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
 
 		return resultId == null ? null : session.generateEntityKey( resultId, persister );
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
 
 	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
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
 		boolean useLimitOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		final boolean useScrollableResultSetToSkip = hasFirstRow &&
 				!useLimitOffset && canScroll;
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimit, queryParameters );
 //
 //		if(canScroll && ( scroll || useScrollableResultSetToSkip )){
 //			 scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;
 //		}else{
 //			scrollMode = null;
 //		}
 		if ( useLimit ) {
 			sql = dialect.getLimitString(
 					sql.trim(), //use of trim() here is ugly?
 					useLimitOffset ? getFirstRow(selection) : 0,
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java
index 8d754a2684..84bd6d98a9 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceBinding.java
@@ -1,81 +1,83 @@
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
-			log.debug( "Overriding existing service binding [" + serviceRole.getName() + "]" );
+                        if (log.isDebugEnabled()) {
+			   log.debug( "Overriding existing service binding [" + serviceRole.getName() + "]" );
+                        }
 		}
 		this.service = service;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
index 7fe3ac61e0..44260b4c9e 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicBinder.java
@@ -1,91 +1,95 @@
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
 package org.hibernate.type.descriptor.sql;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.descriptor.JdbcTypeNameMapper;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.jboss.logging.Logger;
 
 /**
  * Convenience base implementation of {@link ValueBinder}
  *
  * @author Steve Ebersole
  */
 public abstract class BasicBinder<J> implements ValueBinder<J> {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicBinder.class.getName());
 
     private static final String BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - %s";
     private static final String NULL_BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - <null>";
 
 	private final JavaTypeDescriptor<J> javaDescriptor;
 	private final SqlTypeDescriptor sqlDescriptor;
 
 	public JavaTypeDescriptor<J> getJavaDescriptor() {
 		return javaDescriptor;
 	}
 
 	public SqlTypeDescriptor getSqlDescriptor() {
 		return sqlDescriptor;
 	}
 
 	public BasicBinder(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
 		this.javaDescriptor = javaDescriptor;
 		this.sqlDescriptor = sqlDescriptor;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final void bind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException {
 		if ( value == null ) {
-            LOG.trace(String.format(NULL_BIND_MSG_TEMPLATE, index, JdbcTypeNameMapper.getTypeName(sqlDescriptor.getSqlType())));
+                        if (LOG.isTraceEnabled()) {
+                           LOG.trace(String.format(NULL_BIND_MSG_TEMPLATE, index, JdbcTypeNameMapper.getTypeName(sqlDescriptor.getSqlType())));
+                        }
 			st.setNull( index, sqlDescriptor.getSqlType() );
 		}
 		else {
-            LOG.trace(String.format(BIND_MSG_TEMPLATE,
-                                    index,
-                                    JdbcTypeNameMapper.getTypeName(sqlDescriptor.getSqlType()),
-                                    getJavaDescriptor().extractLoggableRepresentation(value)));
+                        if (LOG.isTraceEnabled()) {
+                           LOG.trace(String.format(BIND_MSG_TEMPLATE,
+                                                   index,
+                                                   JdbcTypeNameMapper.getTypeName(sqlDescriptor.getSqlType()),
+                                                   getJavaDescriptor().extractLoggableRepresentation(value)));
+                        }
 			doBind( st, value, index, options );
 		}
 	}
 
 	/**
 	 * Perform the binding.  Safe to assume that value is not null.
 	 *
 	 * @param st The prepared statement
 	 * @param value The value to bind (not null).
 	 * @param index The index at which to bind
 	 * @param options The binding options
 	 *
 	 * @throws SQLException Indicates a problem binding to the prepared statement.
 	 */
 	protected abstract void doBind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
index 909d326c93..73625b05d2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
@@ -1,89 +1,93 @@
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
 package org.hibernate.type.descriptor.sql;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.jboss.logging.Logger;
 
 /**
  * Convenience base implementation of {@link org.hibernate.type.descriptor.ValueExtractor}
  *
  * @author Steve Ebersole
  */
 public abstract class BasicExtractor<J> implements ValueExtractor<J> {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicExtractor.class.getName());
 
 	private final JavaTypeDescriptor<J> javaDescriptor;
 	private final SqlTypeDescriptor sqlDescriptor;
 
 	public BasicExtractor(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
 		this.javaDescriptor = javaDescriptor;
 		this.sqlDescriptor = sqlDescriptor;
 	}
 
 	public JavaTypeDescriptor<J> getJavaDescriptor() {
 		return javaDescriptor;
 	}
 
 	public SqlTypeDescriptor getSqlDescriptor() {
 		return sqlDescriptor;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public J extract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		final J value = doExtract( rs, name, options );
 		if ( value == null || rs.wasNull() ) {
-            LOG.trace("Found [null] as column [" + name + "]");
+                        if (LOG.isTraceEnabled()) {
+                           LOG.trace("Found [null] as column [" + name + "]");
+                        }
 			return null;
 		}
 		else {
-            LOG.trace("Found [" + getJavaDescriptor().extractLoggableRepresentation(value) + "] as column [" + name + "]");
+                        if (LOG.isTraceEnabled()) {
+                           LOG.trace("Found [" + getJavaDescriptor().extractLoggableRepresentation(value) + "] as column [" + name + "]");
+                        }
 			return value;
 		}
 	}
 
 	/**
 	 * Perform the extraction.
 	 * <p/>
 	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
 	 * done there.
 	 *
 	 * @param rs The result set
 	 * @param name The value name in the result set
 	 * @param options The binding options
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates a problem access the result set
 	 */
 	protected abstract J doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException;
 }
