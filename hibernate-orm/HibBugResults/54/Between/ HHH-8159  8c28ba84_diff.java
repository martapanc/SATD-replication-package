diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/package-info.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/package-info.java
index 88b77afdc4..962c27b4e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/package-info.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/package-info.java
@@ -1,7 +1,19 @@
 /**
  * Defines service registry contracts application are likely to want to utilize for
  * configuring Hibernate behavior.
  *
- * {@link BootstrapServiceRegistry} is the
+ * Service registries are designed to be hierarchical.  This works in 2 fashions.  First registries can "hide" or
+ * "override" services from parent registries.  It also allows granular building of registries as services
+ * become available.
+ *
+ * {@link BootstrapServiceRegistry} is the base service registry, intended to be built via
+ * {@link BootstrapServiceRegistryBuilder} if you need customization.  For non-customized
+ * {@link BootstrapServiceRegistry} usage, the {@link BootstrapServiceRegistryBuilder} and
+ * {@link BootstrapServiceRegistry} can be bypassed altogether.
+ *
+ * Usually the next level in a standard registry set up is the {@link StandardServiceRegistry}, intended to be built
+ * by the {@link StandardServiceRegistryBuilder} if you need customization.  The builder optionally takes the
+ * {@link BootstrapServiceRegistry} to use as a base; if none is provided a default one is generated assuming sensible
+ * defaults in Java SE and EE environments, particularly in respect to Class loading.
  */
 package org.hibernate.boot.registry;
diff --git a/hibernate-core/src/main/java/org/hibernate/context/TenantIdentifierMismatchException.java b/hibernate-core/src/main/java/org/hibernate/context/TenantIdentifierMismatchException.java
index afea9d35ec..41bb0427ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/context/TenantIdentifierMismatchException.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/TenantIdentifierMismatchException.java
@@ -1,43 +1,54 @@
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
 package org.hibernate.context;
 
 import org.hibernate.HibernateException;
 
 /**
  * Indicates that tenant identifiers did not match in cases where
  * {@link org.hibernate.context.spi.CurrentTenantIdentifierResolver#validateExistingCurrentSessions()} returns
  * {@code true} and there is a mismatch found.
  *
  * @author Steve Ebersole
  */
 public class TenantIdentifierMismatchException extends HibernateException{
+	/**
+	 * Constructs a TenantIdentifierMismatchException.
+	 *
+	 * @param message Message explaining the exception condition
+	 */
 	public TenantIdentifierMismatchException(String message) {
 		super( message );
 	}
 
-	public TenantIdentifierMismatchException(String message, Throwable root) {
-		super( message, root );
+	/**
+	 * Constructs a TenantIdentifierMismatchException.
+	 *
+	 * @param message Message explaining the exception condition
+	 * @param cause The underlying cause
+	 */
+	public TenantIdentifierMismatchException(String message, Throwable cause) {
+		super( message, cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/context/internal/JTASessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/internal/JTASessionContext.java
index 997732f133..2aea50e033 100644
--- a/hibernate-core/src/main/java/org/hibernate/context/internal/JTASessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/internal/JTASessionContext.java
@@ -1,213 +1,211 @@
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
 package org.hibernate.context.internal;
 
-import java.util.Map;
-import java.util.concurrent.ConcurrentHashMap;
-
 import javax.transaction.Synchronization;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
+import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.context.spi.AbstractCurrentSessionContext;
-import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
+import org.hibernate.internal.CoreMessageLogger;
 
 /**
- * An implementation of {@link CurrentSessionContext} which scopes the notion
- * of a current session to a JTA transaction.  Because JTA gives us a nice
- * tie-in to clean up after ourselves, this implementation will generate
- * Sessions as needed provided a JTA transaction is in effect.  If a session
- * is not already associated with the current JTA transaction at the time
- * {@link #currentSession()} is called, a new session will be opened and it
- * will be associated with that JTA transaction.
- * <p/>
- * Note that the sessions returned from this method are automatically configured with
- * both the {@link org.hibernate.cfg.Environment#FLUSH_BEFORE_COMPLETION auto-flush} and
- * {@link org.hibernate.cfg.Environment#AUTO_CLOSE_SESSION auto-close} attributes set to
- * true, meaning that the Session will be automatically flushed and closed
- * as part of the lifecycle for the JTA transaction to which it is associated.
- * Additionally, it will also be configured to aggressively release JDBC
- * connections after each statement is executed.  These settings are governed
- * by the {@link #isAutoFlushEnabled()}, {@link #isAutoCloseEnabled()}, and
- * {@link #getConnectionReleaseMode()} methods; these are provided (along with
- * the {@link #buildOrObtainSession()} method) for easier subclassing for custom
- * JTA-based session tracking logic (like maybe long-session semantics).
+ * An implementation of {@link org.hibernate.context.spi.CurrentSessionContext} which scopes the notion
+ * of a current session to a JTA transaction.  Because JTA gives us a nice tie-in to clean up after
+ * ourselves, this implementation will generate Sessions as needed provided a JTA transaction is in
+ * effect.  If a session is not already associated with the current JTA transaction at the time
+ * {@link #currentSession()} is called, a new session will be opened and it will be associated with that
+ * JTA transaction.
+ *
+ * Note that the sessions returned from this method are automatically configured with both the
+ * {@link org.hibernate.cfg.Environment#FLUSH_BEFORE_COMPLETION auto-flush} and
+ * {@link org.hibernate.cfg.Environment#AUTO_CLOSE_SESSION auto-close} attributes set to true, meaning
+ * that the Session will be automatically flushed and closed as part of the lifecycle for the JTA
+ * transaction to which it is associated.  Additionally, it will also be configured to aggressively
+ * release JDBC connections after each statement is executed.  These settings are governed by the
+ * {@link #isAutoFlushEnabled()}, {@link #isAutoCloseEnabled()}, and {@link #getConnectionReleaseMode()}
+ * methods; these are provided (along with the {@link #buildOrObtainSession()} method) for easier
+ * subclassing for custom JTA-based session tracking logic (like maybe long-session semantics).
  *
  * @author Steve Ebersole
  */
 public class JTASessionContext extends AbstractCurrentSessionContext {
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JTASessionContext.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			JTASessionContext.class.getName()
+	);
 
 	private transient Map<Object, Session> currentSessionMap = new ConcurrentHashMap<Object, Session>();
 
+	/**
+	 * Constructs a JTASessionContext
+	 *
+	 * @param factory The factory this context will service
+	 */
 	public JTASessionContext(SessionFactoryImplementor factory) {
 		super( factory );
 	}
 
 	@Override
 	public Session currentSession() throws HibernateException {
 		final JtaPlatform jtaPlatform = factory().getServiceRegistry().getService( JtaPlatform.class );
 		final TransactionManager transactionManager = jtaPlatform.retrieveTransactionManager();
 		if ( transactionManager == null ) {
 			throw new HibernateException( "No TransactionManagerLookup specified" );
 		}
 
 		Transaction txn;
 		try {
 			txn = transactionManager.getTransaction();
 			if ( txn == null ) {
 				throw new HibernateException( "Unable to locate current JTA transaction" );
 			}
 			if ( !JtaStatusHelper.isActive( txn.getStatus() ) ) {
 				// We could register the session against the transaction even though it is
 				// not started, but we'd have no guarantee of ever getting the map
 				// entries cleaned up (aside from spawning threads).
 				throw new HibernateException( "Current transaction is not in progress" );
 			}
 		}
 		catch ( HibernateException e ) {
 			throw e;
 		}
 		catch ( Throwable t ) {
 			throw new HibernateException( "Problem locating/validating JTA transaction", t );
 		}
 
 		final Object txnIdentifier = jtaPlatform.getTransactionIdentifier( txn );
 
 		Session currentSession = currentSessionMap.get( txnIdentifier );
 
 		if ( currentSession == null ) {
 			currentSession = buildOrObtainSession();
 
 			try {
 				txn.registerSynchronization( buildCleanupSynch( txnIdentifier ) );
 			}
 			catch ( Throwable t ) {
 				try {
 					currentSession.close();
 				}
 				catch ( Throwable ignore ) {
 					LOG.debug( "Unable to release generated current-session on failed synch registration", ignore );
 				}
 				throw new HibernateException( "Unable to register cleanup Synchronization with TransactionManager" );
 			}
 
 			currentSessionMap.put( txnIdentifier, currentSession );
 		}
 		else {
 			validateExistingSession( currentSession );
 		}
 
 		return currentSession;
 	}
 
 	/**
-	 * Builds a {@link CleanupSynch} capable of cleaning up the the current session map as an after transaction
+	 * Builds a {@link org.hibernate.context.internal.JTASessionContext.CleanupSync} capable of cleaning up the the current session map as an after transaction
 	 * callback.
 	 *
 	 * @param transactionIdentifier The transaction identifier under which the current session is registered.
 	 * @return The cleanup synch.
 	 */
-	private CleanupSynch buildCleanupSynch(Object transactionIdentifier) {
-		return new CleanupSynch( transactionIdentifier, this );
+	private CleanupSync buildCleanupSynch(Object transactionIdentifier) {
+		return new CleanupSync( transactionIdentifier, this );
 	}
 
 	/**
 	 * Strictly provided for subclassing purposes; specifically to allow long-session
-	 * support.
-	 * <p/>
-	 * This implementation always just opens a new session.
+	 * support.  This implementation always just opens a new session.
 	 *
 	 * @return the built or (re)obtained session.
 	 */
+	@SuppressWarnings("deprecation")
 	protected Session buildOrObtainSession() {
 		return baseSessionBuilder()
 				.autoClose( isAutoCloseEnabled() )
 				.connectionReleaseMode( getConnectionReleaseMode() )
 				.flushBeforeCompletion( isAutoFlushEnabled() )
 				.openSession();
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns true.
 	 *
 	 * @return Whether or not the the session should be closed by transaction completion.
 	 */
 	protected boolean isAutoCloseEnabled() {
 		return true;
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns true.
 	 *
 	 * @return Whether or not the the session should be flushed prior transaction completion.
 	 */
 	protected boolean isAutoFlushEnabled() {
 		return true;
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns after_statement.
 	 *
 	 * @return The connection release mode for any built sessions.
 	 */
 	protected ConnectionReleaseMode getConnectionReleaseMode() {
 		return ConnectionReleaseMode.AFTER_STATEMENT;
 	}
 
 	/**
-	 * JTA transaction synch used for cleanup of the internal session map.
+	 * JTA transaction sync used for cleanup of the internal session map.
 	 */
-	protected static class CleanupSynch implements Synchronization {
+	protected static class CleanupSync implements Synchronization {
 		private Object transactionIdentifier;
 		private JTASessionContext context;
 
-		public CleanupSynch(Object transactionIdentifier, JTASessionContext context) {
+		public CleanupSync(Object transactionIdentifier, JTASessionContext context) {
 			this.transactionIdentifier = transactionIdentifier;
 			this.context = context;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void beforeCompletion() {
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void afterCompletion(int i) {
 			context.currentSessionMap.remove( transactionIdentifier );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/context/internal/ManagedSessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/internal/ManagedSessionContext.java
index 13fd5a8d16..b6ad360631 100644
--- a/hibernate-core/src/main/java/org/hibernate/context/internal/ManagedSessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/internal/ManagedSessionContext.java
@@ -1,149 +1,153 @@
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
 package org.hibernate.context.internal;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.context.spi.AbstractCurrentSessionContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * Represents a {@link org.hibernate.context.spi.CurrentSessionContext} the notion of a contextual session
  * is managed by some external entity (generally some form of interceptor, etc).
  * This external manager is responsible for scoping these contextual sessions
  * appropriately binding/unbinding them here for exposure to the application
  * through {@link SessionFactory#getCurrentSession} calls.
  * <p/>
  *  Basically exposes two interfaces.  <ul>
  * <li>First is the implementation of CurrentSessionContext which is then used
  * by the {@link SessionFactory#getCurrentSession()} calls.  This
  * portion is instance-based specific to the session factory owning the given
  * instance of this impl (there will be one instance of this per each session
  * factory using this strategy).
  * <li>Second is the externally facing methods {@link #hasBind}, {@link #bind},
  * and {@link #unbind} used by the external thing to manage exposure of the
  * current session it is scoping.  This portion is static to allow easy
  * reference from that external thing.
  * </ul>
  * The underlying storage of the current sessions here is a static
  * {@link ThreadLocal}-based map where the sessions are keyed by the
  * the owning session factory.
  *
  * @author Steve Ebersole
  */
 public class ManagedSessionContext extends AbstractCurrentSessionContext {
+	private static final ThreadLocal<Map<SessionFactory,Session>> CONTEXT_TL = new ThreadLocal<Map<SessionFactory,Session>>();
 
-	private static final ThreadLocal<Map<SessionFactory,Session>> context = new ThreadLocal<Map<SessionFactory,Session>>();
-
+	/**
+	 * Constructs a new ManagedSessionContext
+	 *
+	 * @param factory The factory this context will service
+	 */
 	public ManagedSessionContext(SessionFactoryImplementor factory) {
 		super( factory );
 	}
 
 	@Override
 	public Session currentSession() {
-		Session current = existingSession( factory() );
+		final Session current = existingSession( factory() );
 		if ( current == null ) {
 			throw new HibernateException( "No session currently bound to execution context" );
 		}
 		else {
 			validateExistingSession( current );
 		}
 		return current;
 	}
 
 	/**
 	 * Check to see if there is already a session associated with the current
 	 * thread for the given session factory.
 	 *
 	 * @param factory The factory against which to check for a given session
 	 * within the current thread.
 	 * @return True if there is currently a session bound.
 	 */
 	public static boolean hasBind(SessionFactory factory) {
 		return existingSession( factory ) != null;
 	}
 
 	/**
 	 * Binds the given session to the current context for its session factory.
 	 *
 	 * @param session The session to be bound.
 	 * @return Any previously bound session (should be null in most cases).
 	 */
 	public static Session bind(Session session) {
 		return sessionMap( true ).put( session.getSessionFactory(), session );
 	}
 
 	/**
 	 * Unbinds the session (if one) current associated with the context for the
 	 * given session.
 	 *
 	 * @param factory The factory for which to unbind the current session.
 	 * @return The bound session if one, else null.
 	 */
 	public static Session unbind(SessionFactory factory) {
+		final Map<SessionFactory,Session> sessionMap = sessionMap();
 		Session existing = null;
-		Map<SessionFactory,Session> sessionMap = sessionMap();
 		if ( sessionMap != null ) {
 			existing = sessionMap.remove( factory );
 			doCleanup();
 		}
 		return existing;
 	}
 
 	private static Session existingSession(SessionFactory factory) {
-		Map sessionMap = sessionMap();
+		final Map sessionMap = sessionMap();
 		if ( sessionMap == null ) {
 			return null;
 		}
 		else {
-			return ( Session ) sessionMap.get( factory );
+			return (Session) sessionMap.get( factory );
 		}
 	}
 
 	protected static Map<SessionFactory,Session> sessionMap() {
 		return sessionMap( false );
 	}
 
 	private static synchronized Map<SessionFactory,Session> sessionMap(boolean createMap) {
-		Map<SessionFactory,Session> sessionMap = context.get();
+		Map<SessionFactory,Session> sessionMap = CONTEXT_TL.get();
 		if ( sessionMap == null && createMap ) {
 			sessionMap = new HashMap<SessionFactory,Session>();
-			context.set( sessionMap );
+			CONTEXT_TL.set( sessionMap );
 		}
 		return sessionMap;
 	}
 
 	private static synchronized void doCleanup() {
-		Map<SessionFactory,Session> sessionMap = sessionMap( false );
+		final Map<SessionFactory,Session> sessionMap = sessionMap( false );
 		if ( sessionMap != null ) {
 			if ( sessionMap.isEmpty() ) {
-				context.set( null );
+				CONTEXT_TL.set( null );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/context/internal/ThreadLocalSessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/internal/ThreadLocalSessionContext.java
index 4ee7ebac03..c4765cfd24 100644
--- a/hibernate-core/src/main/java/org/hibernate/context/internal/ThreadLocalSessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/internal/ThreadLocalSessionContext.java
@@ -1,391 +1,397 @@
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
 package org.hibernate.context.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.util.HashMap;
 import java.util.Map;
 import javax.transaction.Synchronization;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.context.spi.AbstractCurrentSessionContext;
-import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
- * A {@link CurrentSessionContext} impl which scopes the notion of current
- * session by the current thread of execution.  Unlike the JTA counterpart,
- * threads do not give us a nice hook to perform any type of cleanup making
- * it questionable for this impl to actually generate Session instances.  In
- * the interest of usability, it was decided to have this default impl
- * actually generate a session upon first request and then clean it up
- * after the {@link org.hibernate.Transaction} associated with that session
- * is committed/rolled-back.  In order for ensuring that happens, the sessions
- * generated here are unusable until after {@link Session#beginTransaction()}
- * has been called. If <tt>close()</tt> is called on a session managed by
- * this class, it will be automatically unbound.
- * <p/>
- * Additionally, the static {@link #bind} and {@link #unbind} methods are
- * provided to allow application code to explicitly control opening and
- * closing of these sessions.  This, with some from of interception,
- * is the preferred approach.  It also allows easy framework integration
- * and one possible approach for implementing long-sessions.
- * <p/>
- * The {@link #buildOrObtainSession}, {@link #isAutoCloseEnabled},
- * {@link #isAutoFlushEnabled}, {@link #getConnectionReleaseMode}, and
- * {@link #buildCleanupSynch} methods are all provided to allow easy
+ * A {@link org.hibernate.context.spi.CurrentSessionContext} impl which scopes the notion of current
+ * session by the current thread of execution.  Unlike the JTA counterpart, threads do not give us a nice
+ * hook to perform any type of cleanup making it questionable for this impl to actually generate Session
+ * instances.  In the interest of usability, it was decided to have this default impl actually generate
+ * a session upon first request and then clean it up after the {@link org.hibernate.Transaction}
+ * associated with that session is committed/rolled-back.  In order for ensuring that happens, the
+ * sessions generated here are unusable until after {@link Session#beginTransaction()} has been
+ * called. If <tt>close()</tt> is called on a session managed by this class, it will be automatically
+ * unbound.
+ *
+ * Additionally, the static {@link #bind} and {@link #unbind} methods are provided to allow application
+ * code to explicitly control opening and closing of these sessions.  This, with some from of interception,
+ * is the preferred approach.  It also allows easy framework integration and one possible approach for
+ * implementing long-sessions.
+ *
+ * The {@link #buildOrObtainSession}, {@link #isAutoCloseEnabled}, {@link #isAutoFlushEnabled},
+ * {@link #getConnectionReleaseMode}, and {@link #buildCleanupSynch} methods are all provided to allow easy
  * subclassing (for long-running session scenarios, for example).
  *
  * @author Steve Ebersole
  */
 public class ThreadLocalSessionContext extends AbstractCurrentSessionContext {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			ThreadLocalSessionContext.class.getName()
+	);
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       ThreadLocalSessionContext.class.getName());
 	private static final Class[] SESSION_PROXY_INTERFACES = new Class[] {
 			Session.class,
-	        SessionImplementor.class,
-	        EventSource.class,
+			SessionImplementor.class,
+			EventSource.class,
 			TransactionContext.class,
 			LobCreationContext.class
 	};
 
 	/**
 	 * A ThreadLocal maintaining current sessions for the given execution thread.
 	 * The actual ThreadLocal variable is a java.util.Map to account for
-	 * the possibility for multiple SessionFactorys being used during execution
+	 * the possibility for multiple SessionFactory instances being used during execution
 	 * of the given thread.
 	 */
-	private static final ThreadLocal<Map> context = new ThreadLocal<Map>();
+	private static final ThreadLocal<Map> CONTEXT_TL = new ThreadLocal<Map>();
 
+	/**
+	 * Constructs a ThreadLocal
+	 *
+	 * @param factory The factory this context will service
+	 */
 	public ThreadLocalSessionContext(SessionFactoryImplementor factory) {
 		super( factory );
 	}
 
 	@Override
 	public final Session currentSession() throws HibernateException {
 		Session current = existingSession( factory() );
 		if ( current == null ) {
 			current = buildOrObtainSession();
 			// register a cleanup sync
 			current.getTransaction().registerSynchronization( buildCleanupSynch() );
 			// wrap the session in the transaction-protection proxy
 			if ( needsWrapping( current ) ) {
 				current = wrap( current );
 			}
 			// then bind it
 			doBind( current, factory() );
 		}
 		else {
 			validateExistingSession( current );
 		}
 		return current;
 	}
 
 	private boolean needsWrapping(Session session) {
 		// try to make sure we don't wrap and already wrapped session
-		return session != null
-		       && ! Proxy.isProxyClass( session.getClass() )
-		       || ( Proxy.getInvocationHandler( session ) != null
-		       && ! ( Proxy.getInvocationHandler( session ) instanceof TransactionProtectionWrapper ) );
+		if ( session != null ) {
+			if ( Proxy.isProxyClass( session.getClass() ) ) {
+				final InvocationHandler invocationHandler = Proxy.getInvocationHandler( session );
+				if ( invocationHandler != null && TransactionProtectionWrapper.class.isInstance( invocationHandler ) ) {
+					return false;
+				}
+			}
+		}
+		return true;
 	}
 
 	/**
 	 * Getter for property 'factory'.
 	 *
 	 * @return Value for property 'factory'.
 	 */
 	protected SessionFactoryImplementor getFactory() {
 		return factory();
 	}
 
 	/**
-	 * Strictly provided for subclassing purposes; specifically to allow long-session
+	 * Strictly provided for sub-classing purposes; specifically to allow long-session
 	 * support.
 	 * <p/>
 	 * This implementation always just opens a new session.
 	 *
 	 * @return the built or (re)obtained session.
 	 */
+	@SuppressWarnings("deprecation")
 	protected Session buildOrObtainSession() {
 		return baseSessionBuilder()
 				.autoClose( isAutoCloseEnabled() )
 				.connectionReleaseMode( getConnectionReleaseMode() )
 				.flushBeforeCompletion( isAutoFlushEnabled() )
 				.openSession();
 	}
 
-	protected CleanupSynch buildCleanupSynch() {
-		return new CleanupSynch( factory() );
+	protected CleanupSync buildCleanupSynch() {
+		return new CleanupSync( factory() );
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns true.
 	 *
 	 * @return Whether or not the the session should be closed by transaction completion.
 	 */
 	protected boolean isAutoCloseEnabled() {
 		return true;
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns true.
 	 *
 	 * @return Whether or not the the session should be flushed prior transaction completion.
 	 */
 	protected boolean isAutoFlushEnabled() {
 		return true;
 	}
 
 	/**
 	 * Mainly for subclass usage.  This impl always returns after_transaction.
 	 *
 	 * @return The connection release mode for any built sessions.
 	 */
 	protected ConnectionReleaseMode getConnectionReleaseMode() {
 		return factory().getSettings().getConnectionReleaseMode();
 	}
 
 	protected Session wrap(Session session) {
-		TransactionProtectionWrapper wrapper = new TransactionProtectionWrapper( session );
-		Session wrapped = ( Session ) Proxy.newProxyInstance(
+		final TransactionProtectionWrapper wrapper = new TransactionProtectionWrapper( session );
+		final Session wrapped = (Session) Proxy.newProxyInstance(
 				Session.class.getClassLoader(),
 				SESSION_PROXY_INTERFACES,
-		        wrapper
-			);
+				wrapper
+		);
 		// yick!  need this for proper serialization/deserialization handling...
 		wrapper.setWrapped( wrapped );
 		return wrapped;
 	}
 
 	/**
 	 * Associates the given session with the current thread of execution.
 	 *
 	 * @param session The session to bind.
 	 */
 	public static void bind(org.hibernate.Session session) {
-		SessionFactory factory = session.getSessionFactory();
+		final SessionFactory factory = session.getSessionFactory();
 		cleanupAnyOrphanedSession( factory );
 		doBind( session, factory );
 	}
 
 	private static void cleanupAnyOrphanedSession(SessionFactory factory) {
-		Session orphan = doUnbind( factory, false );
+		final Session orphan = doUnbind( factory, false );
 		if ( orphan != null ) {
 			LOG.alreadySessionBound();
 			try {
 				if ( orphan.getTransaction() != null && orphan.getTransaction().isActive() ) {
 					try {
 						orphan.getTransaction().rollback();
 					}
 					catch( Throwable t ) {
 						LOG.debug( "Unable to rollback transaction for orphaned session", t );
 					}
 				}
 				orphan.close();
 			}
 			catch( Throwable t ) {
 				LOG.debug( "Unable to close orphaned session", t );
 			}
 		}
 	}
 
 	/**
 	 * Disassociates a previously bound session from the current thread of execution.
 	 *
 	 * @param factory The factory for which the session should be unbound.
 	 * @return The session which was unbound.
 	 */
 	public static Session unbind(SessionFactory factory) {
 		return doUnbind( factory, true );
 	}
 
 	private static Session existingSession(SessionFactory factory) {
-		Map sessionMap = sessionMap();
+		final Map sessionMap = sessionMap();
 		if ( sessionMap == null ) {
 			return null;
 		}
 		return (Session) sessionMap.get( factory );
 	}
 
 	protected static Map sessionMap() {
-		return context.get();
+		return CONTEXT_TL.get();
 	}
 
 	@SuppressWarnings({"unchecked"})
 	private static void doBind(org.hibernate.Session session, SessionFactory factory) {
 		Map sessionMap = sessionMap();
 		if ( sessionMap == null ) {
 			sessionMap = new HashMap();
-			context.set( sessionMap );
+			CONTEXT_TL.set( sessionMap );
 		}
 		sessionMap.put( factory, session );
 	}
 
 	private static Session doUnbind(SessionFactory factory, boolean releaseMapIfEmpty) {
-		Map sessionMap = sessionMap();
 		Session session = null;
+		final Map sessionMap = sessionMap();
 		if ( sessionMap != null ) {
-			session = ( Session ) sessionMap.remove( factory );
+			session = (Session) sessionMap.remove( factory );
 			if ( releaseMapIfEmpty && sessionMap.isEmpty() ) {
-				context.set( null );
+				CONTEXT_TL.set( null );
 			}
 		}
 		return session;
 	}
 
 	/**
-	 * JTA transaction synch used for cleanup of the internal session map.
+	 * Transaction sync used for cleanup of the internal session map.
 	 */
-	protected static class CleanupSynch implements Synchronization, Serializable {
+	protected static class CleanupSync implements Synchronization, Serializable {
 		protected final SessionFactory factory;
 
-		public CleanupSynch(SessionFactory factory) {
+		public CleanupSync(SessionFactory factory) {
 			this.factory = factory;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void beforeCompletion() {
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void afterCompletion(int i) {
 			unbind( factory );
 		}
 	}
 
 	private class TransactionProtectionWrapper implements InvocationHandler, Serializable {
 		private final Session realSession;
 		private Session wrappedSession;
 
 		public TransactionProtectionWrapper(Session realSession) {
 			this.realSession = realSession;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 			final String methodName = method.getName(); 
 			try {
 				// If close() is called, guarantee unbind()
 				if ( "close".equals( methodName ) ) {
 					unbind( realSession.getSessionFactory() );
 				}
 				else if ( "toString".equals( methodName )
-					     || "equals".equals( methodName )
-					     || "hashCode".equals( methodName )
-				         || "getStatistics".equals( methodName )
-					     || "isOpen".equals( methodName )
-						 || "getListeners".equals( methodName )
-						) {
+						|| "equals".equals( methodName )
+						|| "hashCode".equals( methodName )
+						|| "getStatistics".equals( methodName )
+						|| "isOpen".equals( methodName )
+						|| "getListeners".equals( methodName ) ) {
 					// allow these to go through the the real session no matter what
+					LOG.tracef( "Allowing invocation [%s] to proceed to real session", methodName );
 				}
 				else if ( !realSession.isOpen() ) {
 					// essentially, if the real session is closed allow any
 					// method call to pass through since the real session
 					// will complain by throwing an appropriate exception;
 					// NOTE that allowing close() above has the same basic effect,
 					//   but we capture that there simply to doAfterTransactionCompletion the unbind...
+					LOG.tracef( "Allowing invocation [%s] to proceed to real (closed) session", methodName );
 				}
 				else if ( !realSession.getTransaction().isActive() ) {
 					// limit the methods available if no transaction is active
 					if ( "beginTransaction".equals( methodName )
 							|| "getTransaction".equals( methodName )
 							|| "isTransactionInProgress".equals( methodName )
 							|| "setFlushMode".equals( methodName )
 							|| "getFactory".equals( methodName )
 							|| "getSessionFactory".equals( methodName )
 							|| "getTenantIdentifier".equals( methodName ) ) {
-						LOG.tracev( "Allowing method [{0}] in non-transacted context", methodName );
+						LOG.tracef( "Allowing invocation [%s] to proceed to real (non-transacted) session", methodName );
 					}
 					else if ( "reconnect".equals( methodName ) || "disconnect".equals( methodName ) ) {
 						// allow these (deprecated) methods to pass through
+						LOG.tracef( "Allowing invocation [%s] to proceed to real (non-transacted) session - deprecated methods", methodName );
 					}
 					else {
 						throw new HibernateException( methodName + " is not valid without active transaction" );
 					}
 				}
-				LOG.tracev( "Allowing proxied method [{0}] to proceed to real session", methodName );
+				LOG.tracef( "Allowing proxy invocation [%s] to proceed to real session", methodName );
 				return method.invoke( realSession, args );
 			}
 			catch ( InvocationTargetException e ) {
-                if (e.getTargetException() instanceof RuntimeException) throw (RuntimeException)e.getTargetException();
-                throw e;
+				if (e.getTargetException() instanceof RuntimeException) {
+					throw (RuntimeException)e.getTargetException();
+				}
+				throw e;
 			}
 		}
 
 		/**
 		 * Setter for property 'wrapped'.
 		 *
 		 * @param wrapped Value to set for property 'wrapped'.
 		 */
 		public void setWrapped(Session wrapped) {
 			this.wrappedSession = wrapped;
 		}
 
 
 		// serialization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		private void writeObject(ObjectOutputStream oos) throws IOException {
 			// if a ThreadLocalSessionContext-bound session happens to get
 			// serialized, to be completely correct, we need to make sure
 			// that unbinding of that session occurs.
 			oos.defaultWriteObject();
 			if ( existingSession( factory() ) == wrappedSession ) {
 				unbind( factory() );
 			}
 		}
 
 		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 			// on the inverse, it makes sense that if a ThreadLocalSessionContext-
 			// bound session then gets deserialized to go ahead and re-bind it to
 			// the ThreadLocalSessionContext session map.
 			ois.defaultReadObject();
 			realSession.getTransaction().registerSynchronization( buildCleanupSynch() );
 			doBind( wrappedSession, factory() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/context/internal/package-info.java b/hibernate-core/src/main/java/org/hibernate/context/internal/package-info.java
new file mode 100644
index 0000000000..03452df764
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/context/internal/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Internal implementations and support around "current session" handling.
+ */
+package org.hibernate.context.internal;
diff --git a/hibernate-core/src/main/java/org/hibernate/context/package-info.java b/hibernate-core/src/main/java/org/hibernate/context/package-info.java
new file mode 100644
index 0000000000..ed1d98f2ac
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/context/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Defines support for "current session" feature.
+ */
+package org.hibernate.context;
diff --git a/hibernate-core/src/main/java/org/hibernate/context/spi/AbstractCurrentSessionContext.java b/hibernate-core/src/main/java/org/hibernate/context/spi/AbstractCurrentSessionContext.java
index 4ec693e79a..e848dc0aa1 100644
--- a/hibernate-core/src/main/java/org/hibernate/context/spi/AbstractCurrentSessionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/context/spi/AbstractCurrentSessionContext.java
@@ -1,73 +1,78 @@
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
 package org.hibernate.context.spi;
 
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.context.TenantIdentifierMismatchException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.compare.EqualsHelper;
 
 /**
  * Base support for {@link CurrentSessionContext} implementors.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractCurrentSessionContext implements CurrentSessionContext {
 	private final SessionFactoryImplementor factory;
 
 	protected AbstractCurrentSessionContext(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
+	/**
+	 * Access to the SessionFactory
+	 *
+	 * @return The SessionFactory being serviced by this context
+	 */
 	public SessionFactoryImplementor factory() {
 		return factory;
 	}
 
 	protected SessionBuilder baseSessionBuilder() {
 		final SessionBuilder builder = factory.withOptions();
 		final CurrentTenantIdentifierResolver resolver = factory.getCurrentTenantIdentifierResolver();
 		if ( resolver != null ) {
 			builder.tenantIdentifier( resolver.resolveCurrentTenantIdentifier() );
 		}
 		return builder;
 	}
 
 	protected void validateExistingSession(Session existingSession) {
 		final CurrentTenantIdentifierResolver resolver = factory.getCurrentTenantIdentifierResolver();
 		if ( resolver != null && resolver.validateExistingCurrentSessions() ) {
 			final String current = resolver.resolveCurrentTenantIdentifier();
 			if ( ! EqualsHelper.equals( existingSession.getTenantIdentifier(), current ) ) {
 				throw new TenantIdentifierMismatchException(
 						String.format(
 								"Reported current tenant identifier [%s] did not match tenant identifier from " +
 										"existing session [%s]",
 								current,
 								existingSession.getTenantIdentifier()
 						)
 				);
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/context/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/context/spi/package-info.java
new file mode 100644
index 0000000000..102dcbacc1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/context/spi/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * SPI level contracts around "current session" support.
+ */
+package org.hibernate.context.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
index a4f2047ccf..87723ecb62 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/AbstractEmptinessExpression.java
@@ -1,108 +1,116 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.sql.ConditionFragment;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
- * Implementation of AbstractEmptinessExpression.
+ * Base expression implementation for (not) emptiness checking of collection properties
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractEmptinessExpression implements Criterion {
 
 	private static final TypedValue[] NO_VALUES = new TypedValue[0];
 
 	protected final String propertyName;
 
 	protected AbstractEmptinessExpression(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
+	/**
+	 * Should empty rows be excluded?
+	 *
+	 * @return {@code true} Indicates the expression should be 'exists'; {@code false} indicates 'not exists'
+	 */
 	protected abstract boolean excludeEmpty();
 
+	@Override
 	public final String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
-		String entityName = criteriaQuery.getEntityName( criteria, propertyName );
-		String actualPropertyName = criteriaQuery.getPropertyName( propertyName );
-		String sqlAlias = criteriaQuery.getSQLAlias( criteria, propertyName );
+		final String entityName = criteriaQuery.getEntityName( criteria, propertyName );
+		final String actualPropertyName = criteriaQuery.getPropertyName( propertyName );
+		final String sqlAlias = criteriaQuery.getSQLAlias( criteria, propertyName );
 
-		SessionFactoryImplementor factory = criteriaQuery.getFactory();
-		QueryableCollection collectionPersister = getQueryableCollection( entityName, actualPropertyName, factory );
+		final SessionFactoryImplementor factory = criteriaQuery.getFactory();
+		final QueryableCollection collectionPersister = getQueryableCollection( entityName, actualPropertyName, factory );
 
-		String[] collectionKeys = collectionPersister.getKeyColumnNames();
-		String[] ownerKeys = ( ( Loadable ) factory.getEntityPersister( entityName ) ).getIdentifierColumnNames();
+		final String[] collectionKeys = collectionPersister.getKeyColumnNames();
+		final String[] ownerKeys = ( (Loadable) factory.getEntityPersister( entityName ) ).getIdentifierColumnNames();
 
-		String innerSelect = "(select 1 from " + collectionPersister.getTableName()
-		        + " where "
-		        + new ConditionFragment().setTableAlias( sqlAlias ).setCondition( ownerKeys, collectionKeys ).toFragmentString()
-		        + ")";
+		final String innerSelect = "(select 1 from " + collectionPersister.getTableName() + " where "
+				+ new ConditionFragment().setTableAlias( sqlAlias ).setCondition( ownerKeys, collectionKeys ).toFragmentString()
+				+ ")";
 
 		return excludeEmpty()
-		        ? "exists " + innerSelect
-		        : "not exists " + innerSelect;
+				? "exists " + innerSelect
+				: "not exists " + innerSelect;
 	}
 
 
-	protected QueryableCollection getQueryableCollection(String entityName, String propertyName, SessionFactoryImplementor factory)
-	        throws HibernateException {
-		PropertyMapping ownerMapping = ( PropertyMapping ) factory.getEntityPersister( entityName );
-		Type type = ownerMapping.toType( propertyName );
+	protected QueryableCollection getQueryableCollection(
+			String entityName,
+			String propertyName,
+			SessionFactoryImplementor factory) throws HibernateException {
+		final PropertyMapping ownerMapping = (PropertyMapping) factory.getEntityPersister( entityName );
+		final Type type = ownerMapping.toType( propertyName );
 		if ( !type.isCollectionType() ) {
 			throw new MappingException(
-			        "Property path [" + entityName + "." + propertyName + "] does not reference a collection"
+					"Property path [" + entityName + "." + propertyName + "] does not reference a collection"
 			);
 		}
 
-		String role = ( ( CollectionType ) type ).getRole();
+		final String role = ( (CollectionType) type ).getRole();
 		try {
-			return ( QueryableCollection ) factory.getCollectionPersister( role );
+			return (QueryableCollection) factory.getCollectionPersister( role );
 		}
 		catch ( ClassCastException cce ) {
 			throw new QueryException( "collection role is not queryable: " + role );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "collection role not found: " + role );
 		}
 	}
 
+	@Override
 	public final TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	        throws HibernateException {
+			throws HibernateException {
 		return NO_VALUES;
 	}
 
+	@Override
 	public final String toString() {
 		return propertyName + ( excludeEmpty() ? " is not empty" : " is empty" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/AggregateProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/AggregateProjection.java
index 42f7744c57..c4528b55d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/AggregateProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/AggregateProjection.java
@@ -1,104 +1,102 @@
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
 package org.hibernate.criterion;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.type.Type;
 
 /**
  * Base class for standard aggregation functions.
  *
  * @author max
  */
 public class AggregateProjection extends SimpleProjection {
 	protected final String propertyName;
 	private final String functionName;
 	
 	protected AggregateProjection(String functionName, String propertyName) {
 		this.functionName = functionName;
 		this.propertyName = propertyName;
 	}
 
 	public String getFunctionName() {
 		return functionName;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
-	public String toString() {
-		return functionName + "(" + propertyName + ')';
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return new Type[] {
 				getFunction( criteriaQuery ).getReturnType(
 						criteriaQuery.getType( criteria, getPropertyName() ),
 						criteriaQuery.getFactory()
 				)
 		};
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) throws HibernateException {
 		final String functionFragment = getFunction( criteriaQuery ).render(
 				criteriaQuery.getType( criteria, getPropertyName() ),
 				buildFunctionParameterList( criteria, criteriaQuery ),
 				criteriaQuery.getFactory()
 		);
 		return functionFragment + " as y" + loc + '_';
 	}
 
 	protected SQLFunction getFunction(CriteriaQuery criteriaQuery) {
 		return getFunction( getFunctionName(), criteriaQuery );
 	}
 
 	protected SQLFunction getFunction(String functionName, CriteriaQuery criteriaQuery) {
-		SQLFunction function = criteriaQuery.getFactory()
+		final SQLFunction function = criteriaQuery.getFactory()
 				.getSqlFunctionRegistry()
 				.findSQLFunction( functionName );
 		if ( function == null ) {
 			throw new HibernateException( "Unable to locate mapping for function named [" + functionName + "]" );
 		}
 		return function;
 	}
 
 	protected List buildFunctionParameterList(Criteria criteria, CriteriaQuery criteriaQuery) {
 		return buildFunctionParameterList( criteriaQuery.getColumn( criteria, getPropertyName() ) );
 	}
 
 	protected List buildFunctionParameterList(String column) {
 		return Collections.singletonList( column );
 	}
+
+	@Override
+	public String toString() {
+		return functionName + "(" + propertyName + ')';
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/AliasedProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/AliasedProjection.java
index 20d6af7634..164f63db4b 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/AliasedProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/AliasedProjection.java
@@ -1,98 +1,106 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.type.Type;
 
 /**
+ * Represents a projection that specifies an alias
+ *
  * @author Gavin King
  */
 public class AliasedProjection implements EnhancedProjection {
-	
 	private final Projection projection;
 	private final String alias;
-	
-	public String toString() {
-		return projection.toString() + " as " + alias;
-	}
-	
+
 	protected AliasedProjection(Projection projection, String alias) {
 		this.projection = projection;
 		this.alias = alias;
 	}
 
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		return projection.toSqlString(criteria, position, criteriaQuery);
+	@Override
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
+		return projection.toSqlString( criteria, position, criteriaQuery );
 	}
 
+	@Override
 	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
-		return projection.toGroupSqlString(criteria, criteriaQuery);
+		return projection.toGroupSqlString( criteria, criteriaQuery );
 	}
 
-	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		return projection.getTypes(criteria, criteriaQuery);
+	@Override
+	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		return projection.getTypes( criteria, criteriaQuery );
 	}
 
+	@Override
 	public String[] getColumnAliases(int loc) {
-		return projection.getColumnAliases(loc);
+		return projection.getColumnAliases( loc );
 	}
 
+	@Override
 	public String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
-		return projection instanceof EnhancedProjection ?
-				( ( EnhancedProjection ) projection ).getColumnAliases( loc, criteria, criteriaQuery ) :
-				getColumnAliases( loc );
+		return projection instanceof EnhancedProjection
+				? ( (EnhancedProjection) projection ).getColumnAliases( loc, criteria, criteriaQuery )
+				: getColumnAliases( loc );
 	}
 
-	public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		return this.alias.equals(alias) ?
-				getTypes(criteria, criteriaQuery) :
-				null;
+	@Override
+	public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		return this.alias.equals( alias )
+				? getTypes( criteria, criteriaQuery )
+				: null;
 	}
 
+	@Override
 	public String[] getColumnAliases(String alias, int loc) {
-		return this.alias.equals(alias) ? 
-				getColumnAliases(loc) :
-				null;
+		return this.alias.equals( alias )
+				? getColumnAliases( loc )
+				: null;
 	}
 
+	@Override
 	public String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
-		return this.alias.equals(alias) ?
-				getColumnAliases( loc, criteria, criteriaQuery ) :
-				null;
+		return this.alias.equals( alias )
+				? getColumnAliases( loc, criteria, criteriaQuery )
+				: null;
 	}
 
+	@Override
 	public String[] getAliases() {
-		return new String[]{ alias };
+		return new String[] { alias };
 	}
 
+	@Override
 	public boolean isGrouped() {
 		return projection.isGrouped();
 	}
 
+	@Override
+	public String toString() {
+		return projection.toString() + " as " + alias;
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/AvgProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/AvgProjection.java
index d10dfbc7f6..fee6a732a5 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/AvgProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/AvgProjection.java
@@ -1,36 +1,40 @@
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
 package org.hibernate.criterion;
 
-
 /**
  * An avg() projection
  *
  * @author Gavin King
  */
 public class AvgProjection extends AggregateProjection {
+	/**
+	 * Constructs the AvgProjection
+	 *
+	 * @param propertyName The name of the property to average
+	 */
 	public AvgProjection(String propertyName) {
-		super("avg", propertyName);
+		super( "avg", propertyName );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
index 5c62161421..ba755db02c 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
@@ -1,69 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Constrains a property to between two values
+ *
  * @author Gavin King
  */
 public class BetweenExpression implements Criterion {
-
 	private final String propertyName;
 	private final Object lo;
 	private final Object hi;
 
 	protected BetweenExpression(String propertyName, Object lo, Object hi) {
 		this.propertyName = propertyName;
 		this.lo = lo;
 		this.hi = hi;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		return StringHelper.join(
-			" and ",
-			StringHelper.suffix( criteriaQuery.findColumns(propertyName, criteria), " between ? and ?" )
-		);
-
-		//TODO: get SQL rendering out of this package!
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
+		final String[] expressions = StringHelper.suffix( columns, " between ? and ?" );
+		return StringHelper.join( " and ", expressions );
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return new TypedValue[] {
-				criteriaQuery.getTypedValue(criteria, propertyName, lo),
-				criteriaQuery.getTypedValue(criteria, propertyName, hi)
+				criteriaQuery.getTypedValue( criteria, propertyName, lo ),
+				criteriaQuery.getTypedValue( criteria, propertyName, hi )
 		};
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + " between " + lo + " and " + hi;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Conjunction.java b/hibernate-core/src/main/java/org/hibernate/criterion/Conjunction.java
index d804a0d556..77bb514647 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Conjunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Conjunction.java
@@ -1,33 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
 
 /**
+ * Defines a conjunction (AND series).
+ *
  * @author Gavin King
+ * @author Steve Ebersole
+ *
+ * @see Disjunction
  */
 public class Conjunction extends Junction {
+	/**
+	 * Constructs a Conjunction
+	 */
 	public Conjunction() {
 		super( Nature.AND );
 	}
+
+	protected Conjunction(Criterion... criterion) {
+		super( Nature.AND, criterion );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/CountProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/CountProjection.java
index 9ad6da6996..3367df3457 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/CountProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/CountProjection.java
@@ -1,67 +1,85 @@
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
 package org.hibernate.criterion;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 
 import org.hibernate.Criteria;
 
 /**
- * A count
+ * A count projection
+ *
  * @author Gavin King
  */
 public class CountProjection extends AggregateProjection {
 	private boolean distinct;
 
+	/**
+	 * Constructs the count projection.
+	 *
+	 * @param prop The property name
+	 *
+	 * @see Projections#count(String)
+	 * @see Projections#countDistinct(String)
+	 */
 	protected CountProjection(String prop) {
-		super("count", prop);
-	}
-
-	public String toString() {
-		if ( distinct ) {
-			return "distinct " + super.toString();
-		}
-		else {
-			return super.toString();
-		}
+		super( "count", prop );
 	}
 
+	@Override
 	protected List buildFunctionParameterList(Criteria criteria, CriteriaQuery criteriaQuery) {
-		String cols[] = criteriaQuery.getColumns( propertyName, criteria );
+		final String[] cols = criteriaQuery.getColumns( propertyName, criteria );
 		return ( distinct ? buildCountDistinctParameterList( cols ) : Arrays.asList( cols ) );
 	}
 
+	@SuppressWarnings("unchecked")
 	private List buildCountDistinctParameterList(String[] cols) {
-		List params = new ArrayList( cols.length + 1 );
+		final List params = new ArrayList( cols.length + 1 );
 		params.add( "distinct" );
 		params.addAll( Arrays.asList( cols ) );
 		return params;
 	}
 
+	/**
+	 * Sets the count as being distinct
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public CountProjection setDistinct() {
 		distinct = true;
 		return this;
 	}
+
+	@Override
+	public String toString() {
+		if ( distinct ) {
+			return "distinct " + super.toString();
+		}
+		else {
+			return super.toString();
+		}
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java b/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
index 10c967d9c2..8e82720202 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaQuery.java
@@ -1,130 +1,220 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.type.Type;
 
 /**
  * An instance of <tt>CriteriaQuery</tt> is passed to criterion, 
  * order and projection instances when actually compiling and
  * executing the query. This interface is not used by application
  * code.
  * 
  * @author Gavin King
  */
 public interface CriteriaQuery {
+	/**
+	 * Provides access to the SessionFactory
+	 *
+	 * @return The SessionFactory
+	 */
 	public SessionFactoryImplementor getFactory();
 	
 	/**
-	 * Get the names of the columns mapped by a property path,
-	 * ignoring projection aliases
-	 * @throws org.hibernate.QueryException if the property maps to more than 1 column
+	 * Resolve a property path to the name of the column it maps to.  Ignores projection aliases.
+	 *
+	 * @param criteria The overall criteria
+	 * @param propertyPath The property path to resolve
+	 *
+	 * @return The column name
+	 *
+	 * @throws HibernateException if the property maps to more than 1 column, or if the property could not be resolved
+	 *
+	 * @see #getColumns
 	 */
-	public String getColumn(Criteria criteria, String propertyPath) 
-	throws HibernateException;
-	
+	public String getColumn(Criteria criteria, String propertyPath) throws HibernateException;
+
 	/**
-	 * Get the names of the columns mapped by a property path,
-	 * ignoring projection aliases
+	 * Resolve a property path to the names of the columns it maps to.  Ignores projection aliases
+	 *
+	 * @param criteria The criteria
+	 * @param propertyPath The property path to resolve
+	 *
+	 * @return The column names
+	 *
+	 * @throws HibernateException if the property maps to more than 1 column, or if the property could not be resolved
 	 */
-	public String[] getColumns(String propertyPath, Criteria criteria)
-	throws HibernateException;
+	public String[] getColumns(String propertyPath, Criteria criteria) throws HibernateException;
 
 	/**
-	 * Get the names of the columns mapped by a property path; if the
-	 * property path is not found in criteria, try the "outer" query.
-	 * Projection aliases are ignored.
+	 * Get the names of the columns mapped by a property path; if the property path is not found in criteria, try
+	 * the "outer" query.  Projection aliases are ignored.
+	 *
+	 * @param criteria The criteria
+	 * @param propertyPath The property path to resolve
+	 *
+	 * @return The column names
+	 *
+	 * @throws HibernateException if the property could not be resolved
 	 */
-	public String[] findColumns(String propertyPath, Criteria criteria)
-	throws HibernateException;
+	public String[] findColumns(String propertyPath, Criteria criteria) throws HibernateException;
 
 	/**
-	 * Get the type of a property path, ignoring projection aliases
+	 * Get the type of a property path.
+	 *
+	 * @param criteria The criteria
+	 * @param propertyPath The property path to resolve
+	 *
+	 * @return The type
+	 *
+	 * @throws HibernateException if the property could not be resolved
 	 */
-	public Type getType(Criteria criteria, String propertyPath)
-	throws HibernateException;
+	public Type getType(Criteria criteria, String propertyPath) throws HibernateException;
 
 	/**
-	 * Get the names of the columns mapped by a property path
+	 * Get the names of the columns mapped by a property path.  Here, the property path can refer to
+	 * a projection alias.
+	 *
+	 * @param criteria The criteria
+	 * @param propertyPath The property path to resolve or projection alias
+	 *
+	 * @return The column names
+	 *
+	 * @throws HibernateException if the property/alias could not be resolved
 	 */
-	public String[] getColumnsUsingProjection(Criteria criteria, String propertyPath) 
-	throws HibernateException;
+	public String[] getColumnsUsingProjection(Criteria criteria, String propertyPath) throws HibernateException;
 
 	/**
-	 * Get the type of a property path
+	 * Get the type of a property path.  Here, the property path can refer to a projection alias.
+	 *
+	 * @param criteria The criteria
+	 * @param propertyPath The property path to resolve or projection alias
+	 *
+	 * @return The type
+	 *
+	 * @throws HibernateException if the property/alias could not be resolved
 	 */
-	public Type getTypeUsingProjection(Criteria criteria, String propertyPath)
-	throws HibernateException;
+	public Type getTypeUsingProjection(Criteria criteria, String propertyPath) throws HibernateException;
 
 	/**
-	 * Get the a typed value for the given property value.
+	 * Build a typed-value for the property/value combo.  Essentially the same as manually building a TypedValue
+	 * using the given value and the resolved type using {@link #getTypeUsingProjection}.
+	 *
+	 * @param criteria The criteria query
+	 * @param propertyPath The property path/alias to resolve to type.
+	 * @param value The value
+	 *
+	 * @return The TypedValue
+	 *
+	 * @throws HibernateException if the property/alias could not be resolved
 	 */
-	public TypedValue getTypedValue(Criteria criteria, String propertyPath, Object value)
-	throws HibernateException;
-	
+	public TypedValue getTypedValue(Criteria criteria, String propertyPath, Object value) throws HibernateException;
+
 	/**
 	 * Get the entity name of an entity
+	 *
+	 * @param criteria The criteria
+	 *
+	 * @return The entity name
 	 */
 	public String getEntityName(Criteria criteria);
 	
 	/**
-	 * Get the entity name of an entity, taking into account
-	 * the qualifier of the property path
+	 * Get the entity name of an entity, taking into account the qualifier of the property path
+	 *
+	 * @param criteria The criteria
+	 * @param propertyPath The property path that (supposedly) references an entity
+	 *
+	 * @return The entity name
 	 */
 	public String getEntityName(Criteria criteria, String propertyPath);
 
 	/**
 	 * Get the root table alias of an entity
+	 *
+	 * @param criteria The criteria
+	 *
+	 * @return The SQL table alias for the given criteria
 	 */
-	public String getSQLAlias(Criteria subcriteria);
+	public String getSQLAlias(Criteria criteria);
 
 	/**
 	 * Get the root table alias of an entity, taking into account
 	 * the qualifier of the property path
+	 *
+	 * @param criteria The criteria
+	 * @param propertyPath The property path whose SQL alias should be returned.
+	 *
+	 * @return The SQL table alias for the given criteria
 	 */
 	public String getSQLAlias(Criteria criteria, String propertyPath);
 	
 	/**
 	 * Get the property name, given a possibly qualified property name
+	 *
+	 * @param propertyName The (possibly qualified) property name
+	 *
+	 * @return The simple property name
 	 */
 	public String getPropertyName(String propertyName);
 	
 	/**
 	 * Get the identifier column names of this entity
+	 *
+	 * @param criteria The criteria
+	 *
+	 * @return The identifier column names
 	 */
-	public String[] getIdentifierColumns(Criteria subcriteria);
+	public String[] getIdentifierColumns(Criteria criteria);
 	
 	/**
 	 * Get the identifier type of this entity
+	 *
+	 * @param criteria The criteria
+	 *
+	 * @return The identifier type.
+	 */
+	public Type getIdentifierType(Criteria criteria);
+
+	/**
+	 * Build a TypedValue for the given identifier value.
+	 *
+	 * @param criteria The criteria whose identifier is referenced.
+	 * @param value The identifier value
+	 *
+	 * @return The TypedValue
+	 */
+	public TypedValue getTypedIdentifierValue(Criteria criteria, Object value);
+
+	/**
+	 * Generate a unique SQL alias
+	 *
+	 * @return The generated alias
 	 */
-	public Type getIdentifierType(Criteria subcriteria);
-	
-	public TypedValue getTypedIdentifierValue(Criteria subcriteria, Object value);
-	
 	public String generateSQLAlias();
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java b/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
index de097e63c7..980754d2b1 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/CriteriaSpecification.java
@@ -1,85 +1,90 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.sql.JoinType;
 import org.hibernate.transform.AliasToEntityMapResultTransformer;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 import org.hibernate.transform.PassThroughResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.transform.RootEntityResultTransformer;
 
 /**
+ * Commonality between different types of Criteria.
+ *
  * @author Gavin King
  */
 public interface CriteriaSpecification {
 
 	/**
 	 * The alias that refers to the "root" entity of the criteria query.
 	 */
 	public static final String ROOT_ALIAS = "this";
 
 	/**
 	 * Each row of results is a <tt>Map</tt> from alias to entity instance
 	 */
 	public static final ResultTransformer ALIAS_TO_ENTITY_MAP = AliasToEntityMapResultTransformer.INSTANCE;
 
 	/**
 	 * Each row of results is an instance of the root entity
 	 */
 	public static final ResultTransformer ROOT_ENTITY = RootEntityResultTransformer.INSTANCE;
 
 	/**
 	 * Each row of results is a distinct instance of the root entity
 	 */
 	public static final ResultTransformer DISTINCT_ROOT_ENTITY = DistinctRootEntityResultTransformer.INSTANCE;
 
 	/**
 	 * This result transformer is selected implicitly by calling <tt>setProjection()</tt>
 	 */
 	public static final ResultTransformer PROJECTION = PassThroughResultTransformer.INSTANCE;
 
 	/**
 	 * Specifies joining to an entity based on an inner join.
-	 * @deprecated use {@link JoinType#INNER_JOIN}
+	 *
+	 * @deprecated use {@link org.hibernate.sql.JoinType#INNER_JOIN}
 	 */
 	@Deprecated
-	public static final int INNER_JOIN = org.hibernate.sql.JoinFragment.INNER_JOIN;
+	public static final int INNER_JOIN = JoinType.INNER_JOIN.getJoinTypeValue();
 
 	/**
 	 * Specifies joining to an entity based on a full join.
-	 * @deprecated use {@link JoinType#FULL_JOIN}
+	 *
+	 * @deprecated use {@link org.hibernate.sql.JoinType#FULL_JOIN}
 	 */
 	@Deprecated
-	public static final int FULL_JOIN = org.hibernate.sql.JoinFragment.FULL_JOIN;
+	public static final int FULL_JOIN = JoinType.FULL_JOIN.getJoinTypeValue();
 
 	/**
 	 * Specifies joining to an entity based on a left outer join.
-	 * @deprecated use {@link JoinType#LEFT_OUTER_JOIN}
+	 *
+	 * @deprecated use {@link org.hibernate.sql.JoinType#LEFT_OUTER_JOIN}
 	 */
 	@Deprecated
-	public static final int LEFT_JOIN = org.hibernate.sql.JoinFragment.LEFT_OUTER_JOIN;
+	public static final int LEFT_JOIN = JoinType.LEFT_OUTER_JOIN.getJoinTypeValue();
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java b/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
index 57c93799ed..c4074107c5 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
@@ -1,221 +1,446 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import java.io.Serializable;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
-import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.sql.JoinType;
 import org.hibernate.transform.ResultTransformer;
 
 /**
- * Some applications need to create criteria queries in "detached
- * mode", where the Hibernate session is not available. This class
- * may be instantiated anywhere, and then a <literal>Criteria</literal>
- * may be obtained by passing a session to 
- * <literal>getExecutableCriteria()</literal>. All methods have the
- * same semantics and behavior as the corresponding methods of the
- * <literal>Criteria</literal> interface.
- * 
- * @see org.hibernate.Criteria
+ * Models a detached form of a Criteria (not associated with a Session).
+ *
+ * Some applications need to create criteria queries in "detached mode", where the Hibernate Session is
+ * not available.  Applications would create a DetachableCriteria to describe the query, and then later
+ * associated it with a Session to obtain the "executable" Criteria:
+ * <code>
+ *     DetachedCriteria detached = new DetachedCriteria();
+ *     ...
+ *     Criteria criteria = detached.getExecutableCriteria( session );
+ *     ...
+ *     criteria.list();
+ * </code>
+ *
+ * All methods have the same semantics and behavior as the corresponding methods of the Criteria interface.
+ *
  * @author Gavin King
+ *
+ * @see org.hibernate.Criteria
  */
 public class DetachedCriteria implements CriteriaSpecification, Serializable {
-	
 	private final CriteriaImpl impl;
 	private final Criteria criteria;
-	
+
 	protected DetachedCriteria(String entityName) {
-		impl = new CriteriaImpl(entityName, null);
+		impl = new CriteriaImpl( entityName, null );
 		criteria = impl;
 	}
-	
+
 	protected DetachedCriteria(String entityName, String alias) {
-		impl = new CriteriaImpl(entityName, alias, null);
+		impl = new CriteriaImpl( entityName, alias, null );
 		criteria = impl;
 	}
-	
+
 	protected DetachedCriteria(CriteriaImpl impl, Criteria criteria) {
 		this.impl = impl;
 		this.criteria = criteria;
 	}
-	
+
 	/**
-	 * Get an executable instance of <literal>Criteria</literal>,
-	 * to actually run the query.
+	 * Get an executable instance of Criteria to actually run the query.
+	 *
+	 * @param session The session to associate the built Criteria with
+	 *
+	 * @return The "executable" Criteria
 	 */
 	public Criteria getExecutableCriteria(Session session) {
-		impl.setSession( ( SessionImplementor ) session );
+		impl.setSession( (SessionImplementor) session );
+		return impl;
+	}
+
+	/**
+	 * Obtain the alias associated with this DetachedCriteria
+	 *
+	 * @return The alias
+	 */
+	public String getAlias() {
+		return criteria.getAlias();
+	}
+
+	/**
+	 * Retrieve the CriteriaImpl used internally to hold the DetachedCriteria state
+	 *
+	 * @return The internally maintained CriteriaImpl
+	 */
+	CriteriaImpl getCriteriaImpl() {
 		return impl;
 	}
-	
+
+	/**
+	 * Static builder to create a DetachedCriteria for the given entity.
+	 *
+	 * @param entityName The name of the entity to create a DetachedCriteria for
+	 *
+	 * @return The DetachedCriteria
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static DetachedCriteria forEntityName(String entityName) {
-		return new DetachedCriteria(entityName);
+		return new DetachedCriteria( entityName );
 	}
-	
+
+	/**
+	 * Static builder to create a DetachedCriteria for the given entity.
+	 *
+	 * @param entityName The name of the entity to create a DetachedCriteria for
+	 * @param alias The alias to apply to the entity
+	 *
+	 * @return The DetachedCriteria
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static DetachedCriteria forEntityName(String entityName, String alias) {
-		return new DetachedCriteria(entityName, alias);
+		return new DetachedCriteria( entityName, alias );
 	}
-	
+
+	/**
+	 * Static builder to create a DetachedCriteria for the given entity, by its Class.
+	 *
+	 * @param clazz The entity class
+	 *
+	 * @return The DetachedCriteria
+	 */
 	public static DetachedCriteria forClass(Class clazz) {
 		return new DetachedCriteria( clazz.getName() );
 	}
-	
+
+	/**
+	 * Static builder to create a DetachedCriteria for the given entity, by its Class.
+	 *
+	 * @param clazz The entity class
+	 * @param alias The alias to apply to the entity
+	 *
+	 * @return The DetachedCriteria
+	 */
 	public static DetachedCriteria forClass(Class clazz, String alias) {
 		return new DetachedCriteria( clazz.getName() , alias );
 	}
-	
+
+	/**
+	 * Add a restriction
+	 *
+	 * @param criterion The restriction
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public DetachedCriteria add(Criterion criterion) {
-		criteria.add(criterion);
+		criteria.add( criterion );
 		return this;
 	}
 
+	/**
+	 * Adds an ordering
+	 *
+	 * @param order The ordering
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public DetachedCriteria addOrder(Order order) {
-		criteria.addOrder(order);
+		criteria.addOrder( order );
 		return this;
 	}
 
-	public DetachedCriteria createAlias(String associationPath, String alias)
-	throws HibernateException {
-		criteria.createAlias(associationPath, alias);
+	/**
+	 * Set the fetch mode for a given association
+	 *
+	 * @param associationPath The association path
+	 * @param mode The fetch mode to apply
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria setFetchMode(String associationPath, FetchMode mode) {
+		criteria.setFetchMode( associationPath, mode );
 		return this;
 	}
 
-	public DetachedCriteria createCriteria(String associationPath, String alias)
-	throws HibernateException {
-		return new DetachedCriteria( impl, criteria.createCriteria(associationPath, alias) );
+	/**
+	 * Set the projection to use.
+	 *
+	 * @param projection The projection to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria setProjection(Projection projection) {
+		criteria.setProjection( projection );
+		return this;
 	}
 
-	public DetachedCriteria createCriteria(String associationPath)
-	throws HibernateException {
-		return new DetachedCriteria( impl, criteria.createCriteria(associationPath) );
+	/**
+	 * Set the result transformer to use.
+	 *
+	 * @param resultTransformer The result transformer to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria setResultTransformer(ResultTransformer resultTransformer) {
+		criteria.setResultTransformer( resultTransformer );
+		return this;
 	}
 
-	public String getAlias() {
-		return criteria.getAlias();
+	/**
+	 * Creates an association path alias within this DetachedCriteria.  The alias can then be used in further
+	 * alias creations or restrictions, etc.
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to apply to that association path
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria createAlias(String associationPath, String alias) {
+		criteria.createAlias( associationPath, alias );
+		return this;
 	}
 
-	public DetachedCriteria setFetchMode(String associationPath, FetchMode mode)
-	throws HibernateException {
-		criteria.setFetchMode(associationPath, mode);
+	/**
+	 * Creates an association path alias within this DetachedCriteria specifying the type of join.  The alias
+	 * can then be used in further alias creations or restrictions, etc.
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to apply to that association path
+	 * @param joinType The type of join to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria createAlias(String associationPath, String alias, JoinType joinType) {
+		criteria.createAlias( associationPath, alias, joinType );
 		return this;
 	}
 
-	public DetachedCriteria setProjection(Projection projection) {
-		criteria.setProjection(projection);
+	/**
+	 * Creates an association path alias within this DetachedCriteria specifying the type of join.  The alias
+	 * can then be used in further alias creations or restrictions, etc.
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to apply to that association path
+	 * @param joinType The type of join to use
+	 * @param withClause An additional restriction on the join
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) {
+		criteria.createAlias( associationPath, alias, joinType, withClause );
 		return this;
 	}
 
-	public DetachedCriteria setResultTransformer(ResultTransformer resultTransformer) {
-		criteria.setResultTransformer(resultTransformer);
-		return this;
+	/**
+	 * Deprecated!
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to apply to that association path
+	 * @param joinType The type of join to use
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @deprecated use {@link #createAlias(String, String, JoinType)}
+	 */
+	@Deprecated
+	public DetachedCriteria createAlias(String associationPath, String alias, int joinType) {
+		return createAlias( associationPath, alias, JoinType.parse( joinType ) );
 	}
-	
-	public String toString() {
-		return "DetachableCriteria(" + criteria.toString() + ')';
+
+	/**
+	 * Deprecated!
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to apply to that association path
+	 * @param joinType The type of join to use
+	 * @param withClause An additional restriction on the join
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @deprecated use {@link #createAlias(String, String, JoinType, Criterion)}
+	 */
+	@Deprecated
+	public DetachedCriteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) {
+		return createAlias( associationPath, alias, JoinType.parse( joinType ), withClause );
 	}
-	
-	CriteriaImpl getCriteriaImpl() {
-		return impl;
+
+	/**
+	 * Creates an nested DetachedCriteria representing the association path.
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to apply to that association path
+	 *
+	 * @return the newly created, nested DetachedCriteria
+	 */
+	public DetachedCriteria createCriteria(String associationPath, String alias) {
+		return new DetachedCriteria( impl, criteria.createCriteria( associationPath, alias ) );
 	}
 
-    public DetachedCriteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException {
-        criteria.createAlias(associationPath, alias, joinType);
-        return this;
-    }
-	
-	public DetachedCriteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
-		criteria.createAlias(associationPath, alias, joinType, withClause);
-		return this;
+	/**
+	 * Creates an nested DetachedCriteria representing the association path.
+	 *
+	 * @param associationPath The association path
+	 *
+	 * @return the newly created, nested DetachedCriteria
+	 */
+	public DetachedCriteria createCriteria(String associationPath) {
+		return new DetachedCriteria( impl, criteria.createCriteria( associationPath ) );
 	}
-	
-	public DetachedCriteria createCriteria(String associationPath, JoinType joinType) throws HibernateException {
-        return new DetachedCriteria(impl, criteria.createCriteria(associationPath, joinType));
-    }
 
-    public DetachedCriteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException {
-        return new DetachedCriteria(impl, criteria.createCriteria(associationPath, alias, joinType));
-    }
-	
-	public DetachedCriteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
-		return new DetachedCriteria(impl, criteria.createCriteria(associationPath, alias, joinType, withClause));
+	/**
+	 * Creates an nested DetachedCriteria representing the association path, specifying the type of join to use.
+	 *
+	 * @param associationPath The association path
+	 * @param joinType The type of join to use
+	 *
+	 * @return the newly created, nested DetachedCriteria
+	 */
+	public DetachedCriteria createCriteria(String associationPath, JoinType joinType) {
+		return new DetachedCriteria( impl, criteria.createCriteria( associationPath, joinType ) );
 	}
 
 	/**
-	 * @deprecated use {@link #createAlias(String, String, JoinType)}
+	 * Creates an nested DetachedCriteria representing the association path, specifying the type of join to use.
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to associate with this "join".
+	 * @param joinType The type of join to use
+	 *
+	 * @return the newly created, nested DetachedCriteria
 	 */
-	@Deprecated
-	public DetachedCriteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
-       return createAlias( associationPath, alias, JoinType.parse( joinType ) );
-    }
+	public DetachedCriteria createCriteria(String associationPath, String alias, JoinType joinType) {
+		return new DetachedCriteria( impl, criteria.createCriteria( associationPath, alias, joinType ) );
+	}
+
 	/**
-	 * @deprecated use {@link #createAlias(String, String, JoinType, Criterion)}
+	 * Creates an nested DetachedCriteria representing the association path, specifying the type of join to use and
+	 * an additional join restriction.
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to associate with this "join".
+	 * @param joinType The type of join to use
+	 * @param withClause The additional join restriction
+	 *
+	 * @return the newly created, nested DetachedCriteria
 	 */
-	@Deprecated
-	public DetachedCriteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
-		return createAlias( associationPath, alias, JoinType.parse( joinType ), withClause );
+	public DetachedCriteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause)  {
+		return new DetachedCriteria(impl, criteria.createCriteria( associationPath, alias, joinType, withClause ) );
 	}
+
 	/**
+	 * Deprecated!
+	 *
+	 * @param associationPath The association path
+	 * @param joinType The type of join to use
+	 *
+	 * @return the newly created, nested DetachedCriteria
+	 *
 	 * @deprecated use {@link #createCriteria(String, JoinType)}
 	 */
 	@Deprecated
-	public DetachedCriteria createCriteria(String associationPath, int joinType) throws HibernateException {
-        return createCriteria( associationPath, JoinType.parse( joinType ) );
-    }
+	public DetachedCriteria createCriteria(String associationPath, int joinType) {
+		return createCriteria( associationPath, JoinType.parse( joinType ) );
+	}
+
 	/**
+	 * Deprecated!
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias
+	 * @param joinType The type of join to use
+	 *
+	 * @return the newly created, nested DetachedCriteria
+	 *
 	 * @deprecated use {@link #createCriteria(String, String, JoinType)}
 	 */
 	@Deprecated
-    public DetachedCriteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
-        return createCriteria( associationPath, alias, JoinType.parse( joinType ) );
-    }
+	public DetachedCriteria createCriteria(String associationPath, String alias, int joinType) {
+		return createCriteria( associationPath, alias, JoinType.parse( joinType ) );
+	}
+
 	/**
+	 * Deprecated!
+	 *
+	 * @param associationPath The association path
+	 * @param alias The alias to associate with this "join".
+	 * @param joinType The type of join to use
+	 * @param withClause The additional join restriction
+	 *
+	 * @return the newly created, nested DetachedCriteria
+	 *
 	 * @deprecated use {@link #createCriteria(String, String, JoinType, Criterion)}
 	 */
 	@Deprecated
-	public DetachedCriteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
+	public DetachedCriteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) {
 		return createCriteria( associationPath, alias, JoinType.parse( joinType ), withClause );
 	}
-	
+
+	/**
+	 * Set the SQL comment to use.
+	 *
+	 * @param comment The SQL comment to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public DetachedCriteria setComment(String comment) {
-        criteria.setComment(comment);
-        return this;
-    }
-
-    public DetachedCriteria setLockMode(LockMode lockMode) {
-        criteria.setLockMode(lockMode);
-        return this;
-    }
-
-    public DetachedCriteria setLockMode(String alias, LockMode lockMode) {
-        criteria.setLockMode(alias, lockMode);
-        return this;
-    }
+		criteria.setComment( comment );
+		return this;
+	}
+
+	/**
+	 * Set the lock mode to use.
+	 *
+	 * @param lockMode The lock mode to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria setLockMode(LockMode lockMode) {
+		criteria.setLockMode( lockMode );
+		return this;
+	}
+
+	/**
+	 * Set an alias-specific lock mode.  The specified lock mode applies only to that alias.
+	 *
+	 * @param alias The alias to apply the lock to
+	 * @param lockMode The lock mode to use.
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public DetachedCriteria setLockMode(String alias, LockMode lockMode) {
+		criteria.setLockMode( alias, lockMode );
+		return this;
+	}
+
+	@Override
+	public String toString() {
+		return "DetachableCriteria(" + criteria.toString() + ')';
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Disjunction.java b/hibernate-core/src/main/java/org/hibernate/criterion/Disjunction.java
index 4c2032c1bf..3ffa5ab2ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Disjunction.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Disjunction.java
@@ -1,33 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
 
 /**
+ * Defines a disjunction (OR series).
+ *
  * @author Gavin King
+ * @author Steve Ebersole
+ *
+ * @see Conjunction
  */
 public class Disjunction extends Junction {
+	/**
+	 * Constructs a Disjunction
+	 */
 	protected Disjunction() {
 		super( Nature.OR );
 	}
+
+	protected Disjunction(Criterion[] conditions) {
+		super( Nature.OR, conditions );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Distinct.java b/hibernate-core/src/main/java/org/hibernate/criterion/Distinct.java
index 1237a8a462..457fb68ead 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Distinct.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Distinct.java
@@ -1,92 +1,104 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.HibernateException;
 import org.hibernate.type.Type;
 
 /**
+ * A wrappedProjection that is a wrapper around other projections to apply distinction.
+ *
  * @author Gavin King
  */
 public class Distinct implements EnhancedProjection {
+	private final Projection wrappedProjection;
 
-	private final Projection projection;
-	
-	public Distinct(Projection proj) {
-		this.projection = proj;
+	/**
+	 * Constructs a Distinct
+	 *
+	 * @param wrappedProjection The wrapped projection
+	 */
+	public Distinct(Projection wrappedProjection) {
+		this.wrappedProjection = wrappedProjection;
 	}
 
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery)
-			throws HibernateException {
-		return "distinct " + projection.toSqlString(criteria, position, criteriaQuery);
+	@Override
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
+		return "distinct " + wrappedProjection.toSqlString( criteria, position, criteriaQuery );
 	}
 
-	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-			throws HibernateException {
-		return projection.toGroupSqlString(criteria, criteriaQuery);
+	@Override
+	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		return wrappedProjection.toGroupSqlString( criteria, criteriaQuery );
 	}
 
-	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery)
-			throws HibernateException {
-		return projection.getTypes(criteria, criteriaQuery);
+	@Override
+	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) {
+		return wrappedProjection.getTypes( criteria, criteriaQuery );
 	}
 
-	public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery)
-			throws HibernateException {
-		return projection.getTypes(alias, criteria, criteriaQuery);
+	@Override
+	public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery) {
+		return wrappedProjection.getTypes( alias, criteria, criteriaQuery );
 	}
 
+	@Override
 	public String[] getColumnAliases(int loc) {
-		return projection.getColumnAliases(loc);
+		return wrappedProjection.getColumnAliases( loc );
 	}
 
+	@Override
 	public String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
-		return projection instanceof EnhancedProjection ?
-				( ( EnhancedProjection ) projection ).getColumnAliases( loc, criteria, criteriaQuery ) :
-				getColumnAliases( loc );
+		return wrappedProjection instanceof EnhancedProjection
+				? ( (EnhancedProjection) wrappedProjection).getColumnAliases( loc, criteria, criteriaQuery )
+				: getColumnAliases( loc );
 	}
 
+	@Override
 	public String[] getColumnAliases(String alias, int loc) {
-		return projection.getColumnAliases(alias, loc);
+		return wrappedProjection.getColumnAliases( alias, loc );
 	}
 
+	@Override
 	public String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
-		return projection instanceof EnhancedProjection ?
-				( ( EnhancedProjection ) projection ).getColumnAliases( alias, loc, criteria, criteriaQuery ) :
-				getColumnAliases( alias, loc );
+		return wrappedProjection instanceof EnhancedProjection
+				? ( (EnhancedProjection) wrappedProjection).getColumnAliases( alias, loc, criteria, criteriaQuery )
+				: getColumnAliases( alias, loc );
 	}
 
+	@Override
 	public String[] getAliases() {
-		return projection.getAliases();
+		return wrappedProjection.getAliases();
 	}
 
+	@Override
 	public boolean isGrouped() {
-		return projection.isGrouped();
+		return wrappedProjection.isGrouped();
 	}
 
+	@Override
 	public String toString() {
-		return "distinct " + projection.toString();
+		return "distinct " + wrappedProjection.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/EmptyExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/EmptyExpression.java
index b1bf13eb64..c9df93d033 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/EmptyExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/EmptyExpression.java
@@ -1,41 +1,47 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
-
 /**
+ * An expression asserting that a collection property is empty
+ *
  * @author Gavin King
  */
 public class EmptyExpression extends AbstractEmptinessExpression implements Criterion {
-
+	/**
+	 * Constructs an EmptyExpression
+	 *
+	 * @param propertyName The collection property name
+	 *
+	 * @see Restrictions#isEmpty
+	 */
 	protected EmptyExpression(String propertyName) {
 		super( propertyName );
 	}
 
+	@Override
 	protected boolean excludeEmpty() {
 		return false;
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/EnhancedProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/EnhancedProjection.java
index 5c375e01eb..57a5e67a70 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/EnhancedProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/EnhancedProjection.java
@@ -1,64 +1,64 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
 /**
  * An "enhanced" Projection for a {@link Criteria} query.
  *
  * @author Gail Badner
  * @see Projection
  * @see Criteria
  */
 public interface EnhancedProjection extends Projection {
 
 	/**
 	 * Get the SQL column aliases used by this projection for the columns it writes for inclusion into the
 	 * <tt>SELECT</tt> clause ({@link #toSqlString}.  Hibernate always uses column aliases to extract data from the
 	 * JDBC {@link java.sql.ResultSet}, so it is important that these be implemented correctly in order for
 	 * Hibernate to be able to extract these val;ues correctly.
 	 *
 	 * @param position Just as in {@link #toSqlString}, represents the number of <b>columns</b> rendered
 	 * prior to this projection.
 	 * @param criteria The local criteria to which this project is attached (for resolution).
 	 * @param criteriaQuery The overall criteria query instance.
 	 * @return The columns aliases.
 	 */
 	public String[] getColumnAliases(int position, Criteria criteria, CriteriaQuery criteriaQuery);
 
 	/**
 	 * Get the SQL column aliases used by this projection for the columns it writes for inclusion into the
 	 * <tt>SELECT</tt> clause ({@link #toSqlString} <i>for a particular criteria-level alias</i>.
 	 *
 	 * @param alias The criteria-level alias
 	 * @param position Just as in {@link #toSqlString}, represents the number of <b>columns</b> rendered
 	 * prior to this projection.
 	 * @param criteria The local criteria to which this project is attached (for resolution).
 	 * @param criteriaQuery The overall criteria query instance.
 	 * @return The columns aliases pertaining to a particular criteria-level alias; expected to return null if
 	 * this projection does not understand this alias.
 	 */
 	public String[] getColumnAliases(String alias, int position, Criteria criteria, CriteriaQuery criteriaQuery);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Example.java b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
index d35d86deb7..44a9abc6fe 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Example.java
@@ -1,395 +1,500 @@
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
 package org.hibernate.criterion;
+
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Support for query by example.
+ *
  * <pre>
  * List results = session.createCriteria(Parent.class)
  *     .add( Example.create(parent).ignoreCase() )
  *     .createCriteria("child")
  *         .add( Example.create( parent.getChild() ) )
  *     .list();
  * </pre>
- * "Examples" may be mixed and matched with "Expressions" in the same <tt>Criteria</tt>.
+ *
+ * "Examples" may be mixed and matched with "Expressions" in the same Criteria.
+ *
  * @see org.hibernate.Criteria
  * @author Gavin King
  */
 
 public class Example implements Criterion {
-
-	private final Object entity;
-	private final Set excludedProperties = new HashSet();
+	private final Object exampleEntity;
 	private PropertySelector selector;
+
 	private boolean isLikeEnabled;
 	private Character escapeCharacter;
 	private boolean isIgnoreCaseEnabled;
 	private MatchMode matchMode;
 
+	private final Set<String> excludedProperties = new HashSet<String>();
+
 	/**
-	 * A strategy for choosing property values for inclusion in the query
-	 * criteria
+	 * Create a new Example criterion instance, which includes all non-null properties by default
+	 *
+	 * @param exampleEntity The example bean to use.
+	 *
+	 * @return a new instance of Example
 	 */
-
-	public static interface PropertySelector extends Serializable {
-		public boolean include(Object propertyValue, String propertyName, Type type);
-	}
-
-	private static final PropertySelector NOT_NULL = new NotNullPropertySelector();
-	private static final PropertySelector ALL = new AllPropertySelector();
-	private static final PropertySelector NOT_NULL_OR_ZERO = new NotNullOrZeroPropertySelector();
-
-	static final class AllPropertySelector implements PropertySelector {
-		public boolean include(Object object, String propertyName, Type type) {
-			return true;
-		}
-		
-		private Object readResolve() {
-			return ALL;
+	public static Example create(Object exampleEntity) {
+		if ( exampleEntity == null ) {
+			throw new NullPointerException( "null example entity" );
 		}
+		return new Example( exampleEntity, NotNullPropertySelector.INSTANCE );
 	}
 
-	static final class NotNullPropertySelector implements PropertySelector {
-		public boolean include(Object object, String propertyName, Type type) {
-			return object!=null;
-		}
-		
-		private Object readResolve() {
-			return NOT_NULL;
-		}
-	}
-
-	static final class NotNullOrZeroPropertySelector implements PropertySelector {
-		public boolean include(Object object, String propertyName, Type type) {
-			return object!=null && (
-				!(object instanceof Number) || ( (Number) object ).longValue()!=0
-			);
-		}
-		
-		private Object readResolve() {
-			return NOT_NULL_OR_ZERO;
-		}
+	/**
+	 * Allow subclasses to instantiate as needed.
+	 *
+	 * @param exampleEntity The example bean
+	 * @param selector The property selector to use
+	 */
+	protected Example(Object exampleEntity, PropertySelector selector) {
+		this.exampleEntity = exampleEntity;
+		this.selector = selector;
 	}
 
 	/**
-	 * Set escape character for "like" clause
+	 * Set escape character for "like" clause if like matching was enabled
+	 *
+	 * @param escapeCharacter The escape character
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #enableLike
 	 */
 	public Example setEscapeCharacter(Character escapeCharacter) {
 		this.escapeCharacter = escapeCharacter;
 		return this;
 	}
 
 	/**
-	 * Set the property selector
+	 * Use the "like" operator for all string-valued properties.  This form implicitly uses {@link MatchMode#EXACT}
+	 *
+	 * @return {@code this}, for method chaining
 	 */
-	public Example setPropertySelector(PropertySelector selector) {
-		this.selector = selector;
-		return this;
+	public Example enableLike() {
+		return enableLike( MatchMode.EXACT );
 	}
 
 	/**
-	 * Exclude zero-valued properties
+	 * Use the "like" operator for all string-valued properties
+	 *
+	 * @param matchMode The match mode to use.
+	 *
+	 * @return {@code this}, for method chaining
 	 */
-	public Example excludeZeroes() {
-		setPropertySelector(NOT_NULL_OR_ZERO);
+	public Example enableLike(MatchMode matchMode) {
+		this.isLikeEnabled = true;
+		this.matchMode = matchMode;
 		return this;
 	}
 
 	/**
-	 * Don't exclude null or zero-valued properties
+	 * Ignore case for all string-valued properties
+	 *
+	 * @return {@code this}, for method chaining
 	 */
-	public Example excludeNone() {
-		setPropertySelector(ALL);
+	public Example ignoreCase() {
+		this.isIgnoreCaseEnabled = true;
 		return this;
 	}
 
 	/**
-	 * Use the "like" operator for all string-valued properties
+	 * Set the property selector to use.
+	 *
+	 * The property selector operates separate from excluding a property.
+	 *
+	 * @param selector The selector to use
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #excludeProperty
 	 */
-	public Example enableLike(MatchMode matchMode) {
-		isLikeEnabled = true;
-		this.matchMode = matchMode;
+	public Example setPropertySelector(PropertySelector selector) {
+		this.selector = selector;
 		return this;
 	}
 
 	/**
-	 * Use the "like" operator for all string-valued properties
+	 * Exclude zero-valued properties.
+	 *
+	 * Equivalent to calling {@link #setPropertySelector} passing in {@link NotNullOrZeroPropertySelector#INSTANCE}
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #setPropertySelector
 	 */
-	public Example enableLike() {
-		return enableLike(MatchMode.EXACT);
+	public Example excludeZeroes() {
+		setPropertySelector( NotNullOrZeroPropertySelector.INSTANCE );
+		return this;
 	}
 
 	/**
-	 * Ignore case for all string-valued properties
+	 * Include all properties.
+	 *
+	 * Equivalent to calling {@link #setPropertySelector} passing in {@link AllPropertySelector#INSTANCE}
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #setPropertySelector
 	 */
-	public Example ignoreCase() {
-		isIgnoreCaseEnabled = true;
+	public Example excludeNone() {
+		setPropertySelector( AllPropertySelector.INSTANCE );
 		return this;
 	}
 
 	/**
-	 * Exclude a particular named property
+	 * Exclude a particular property by name.
+	 *
+	 * @param name The name of the property to exclude
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see #setPropertySelector
 	 */
 	public Example excludeProperty(String name) {
-		excludedProperties.add(name);
+		excludedProperties.add( name );
 		return this;
 	}
 
-	/**
-	 * Create a new instance, which includes all non-null properties
-	 * by default
-	 * @param entity
-	 * @return a new instance of <tt>Example</tt>
-	 */
-	public static Example create(Object entity) {
-		if (entity==null) throw new NullPointerException("null example");
-		return new Example(entity, NOT_NULL);
-	}
-
-	protected Example(Object entity, PropertySelector selector) {
-		this.entity = entity;
-		this.selector = selector;
-	}
-
-	public String toString() {
-		return "example (" + entity + ')';
-	}
-
-	private boolean isPropertyIncluded(Object value, String name, Type type) {
-		return !excludedProperties.contains(name) &&
-			!type.isAssociationType() &&
-			selector.include(value, name, type);
-	}
-
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-		throws HibernateException {
-
-		StringBuilder buf = new StringBuilder().append('(');
-		EntityPersister meta = criteriaQuery.getFactory().getEntityPersister( criteriaQuery.getEntityName(criteria) );
-		String[] propertyNames = meta.getPropertyNames();
-		Type[] propertyTypes = meta.getPropertyTypes();
-		//TODO: get all properties, not just the fetched ones!
-		Object[] propertyValues = meta.getPropertyValues( entity );
-		for (int i=0; i<propertyNames.length; i++) {
-			Object propertyValue = propertyValues[i];
-			String propertyName = propertyNames[i];
-
-			boolean isPropertyIncluded = i!=meta.getVersionProperty() &&
-				isPropertyIncluded( propertyValue, propertyName, propertyTypes[i] );
-			if (isPropertyIncluded) {
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final StringBuilder buf = new StringBuilder().append( '(' );
+		final EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(
+				criteriaQuery.getEntityName( criteria )
+		);
+		final String[] propertyNames = meta.getPropertyNames();
+		final Type[] propertyTypes = meta.getPropertyTypes();
+
+		final Object[] propertyValues = meta.getPropertyValues( exampleEntity );
+		for ( int i=0; i<propertyNames.length; i++ ) {
+			final Object propertyValue = propertyValues[i];
+			final String propertyName = propertyNames[i];
+
+			final boolean isVersionProperty = i == meta.getVersionProperty();
+			if ( ! isVersionProperty && isPropertyIncluded( propertyValue, propertyName, propertyTypes[i] ) ) {
 				if ( propertyTypes[i].isComponentType() ) {
 					appendComponentCondition(
 						propertyName,
 						propertyValue,
 						(CompositeType) propertyTypes[i],
 						criteria,
 						criteriaQuery,
 						buf
 					);
 				}
 				else {
 					appendPropertyCondition(
 						propertyName,
 						propertyValue,
 						criteria,
 						criteriaQuery,
 						buf
 					);
 				}
 			}
 		}
-		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
-		return buf.append(')').toString();
+
+		if ( buf.length()==1 ) {
+			buf.append( "1=1" );
+		}
+
+		return buf.append( ')' ).toString();
 	}
 
-	private static final Object[] TYPED_VALUES = new TypedValue[0];
+	@SuppressWarnings("SimplifiableIfStatement")
+	private boolean isPropertyIncluded(Object value, String name, Type type) {
+		if ( excludedProperties.contains( name ) ) {
+			// was explicitly excluded
+			return false;
+		}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+		if ( type.isAssociationType() ) {
+			// associations are implicitly excluded
+			return false;
+		}
 
-		EntityPersister meta = criteriaQuery.getFactory()
-				.getEntityPersister( criteriaQuery.getEntityName(criteria) );
-		String[] propertyNames = meta.getPropertyNames();
-		Type[] propertyTypes = meta.getPropertyTypes();
-		 //TODO: get all properties, not just the fetched ones!
-		Object[] values = meta.getPropertyValues( entity );
-		List list = new ArrayList();
-		for (int i=0; i<propertyNames.length; i++) {
-			Object value = values[i];
-			Type type = propertyTypes[i];
-			String name = propertyNames[i];
+		return selector.include( value, name, type );
+	}
+
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(
+				criteriaQuery.getEntityName( criteria )
+		);
+		final String[] propertyNames = meta.getPropertyNames();
+		final Type[] propertyTypes = meta.getPropertyTypes();
 
-			boolean isPropertyIncluded = i!=meta.getVersionProperty() &&
-				isPropertyIncluded(value, name, type);
+		final Object[] values = meta.getPropertyValues( exampleEntity );
+		final List<TypedValue> list = new ArrayList<TypedValue>();
+		for ( int i=0; i<propertyNames.length; i++ ) {
+			final Object value = values[i];
+			final Type type = propertyTypes[i];
+			final String name = propertyNames[i];
 
-			if (isPropertyIncluded) {
+			final boolean isVersionProperty = i == meta.getVersionProperty();
+
+			if ( ! isVersionProperty && isPropertyIncluded( value, name, type ) ) {
 				if ( propertyTypes[i].isComponentType() ) {
-					addComponentTypedValues(name, value, (CompositeType) type, list, criteria, criteriaQuery);
+					addComponentTypedValues( name, value, (CompositeType) type, list, criteria, criteriaQuery );
 				}
 				else {
-					addPropertyTypedValue(value, type, list);
+					addPropertyTypedValue( value, type, list );
 				}
 			}
 		}
-		return (TypedValue[]) list.toArray(TYPED_VALUES);
-	}
-	
-	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {
-		EntityPersister meta = criteriaQuery.getFactory()
-				.getEntityPersister( criteriaQuery.getEntityName(criteria) );
-		EntityMode result = meta.getEntityMode();
-		if ( ! meta.getEntityMetamodel().getTuplizer().isInstance( entity ) ) {
-			throw new ClassCastException( entity.getClass().getName() );
-		}
-		return result;
+
+		return list.toArray( new TypedValue[ list.size() ] );
 	}
 
-	protected void addPropertyTypedValue(Object value, Type type, List list) {
-		if ( value!=null ) {
+	protected void addPropertyTypedValue(Object value, Type type, List<TypedValue> list) {
+		if ( value != null ) {
 			if ( value instanceof String ) {
 				String string = (String) value;
-				if (isIgnoreCaseEnabled) string = string.toLowerCase();
-				if (isLikeEnabled) string = matchMode.toMatchString(string);
+				if ( isIgnoreCaseEnabled ) {
+					string = string.toLowerCase();
+				}
+				if ( isLikeEnabled ) {
+					string = matchMode.toMatchString( string );
+				}
 				value = string;
 			}
-			list.add( new TypedValue(type, value, null) );
+			list.add( new TypedValue( type, value ) );
 		}
 	}
 
 	protected void addComponentTypedValues(
 			String path, 
 			Object component, 
 			CompositeType type,
-			List list, 
+			List<TypedValue> list,
 			Criteria criteria, 
-			CriteriaQuery criteriaQuery)
-	throws HibernateException {
-
-		if (component!=null) {
-			String[] propertyNames = type.getPropertyNames();
-			Type[] subtypes = type.getSubtypes();
-			Object[] values = type.getPropertyValues( component, getEntityMode(criteria, criteriaQuery) );
-			for (int i=0; i<propertyNames.length; i++) {
-				Object value = values[i];
-				Type subtype = subtypes[i];
-				String subpath = StringHelper.qualify( path, propertyNames[i] );
-				if ( isPropertyIncluded(value, subpath, subtype) ) {
+			CriteriaQuery criteriaQuery) {
+		if ( component != null ) {
+			final String[] propertyNames = type.getPropertyNames();
+			final Type[] subtypes = type.getSubtypes();
+			final Object[] values = type.getPropertyValues( component, getEntityMode( criteria, criteriaQuery ) );
+			for ( int i=0; i<propertyNames.length; i++ ) {
+				final Object value = values[i];
+				final Type subtype = subtypes[i];
+				final String subpath = StringHelper.qualify( path, propertyNames[i] );
+				if ( isPropertyIncluded( value, subpath, subtype ) ) {
 					if ( subtype.isComponentType() ) {
-						addComponentTypedValues(subpath, value, (CompositeType) subtype, list, criteria, criteriaQuery);
+						addComponentTypedValues( subpath, value, (CompositeType) subtype, list, criteria, criteriaQuery );
 					}
 					else {
-						addPropertyTypedValue(value, subtype, list);
+						addPropertyTypedValue( value, subtype, list );
 					}
 				}
 			}
 		}
 	}
 
+	private EntityMode getEntityMode(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final EntityPersister meta = criteriaQuery.getFactory().getEntityPersister(
+				criteriaQuery.getEntityName( criteria )
+		);
+		final EntityMode result = meta.getEntityMode();
+		if ( ! meta.getEntityMetamodel().getTuplizer().isInstance( exampleEntity ) ) {
+			throw new ClassCastException( exampleEntity.getClass().getName() );
+		}
+		return result;
+	}
+
 	protected void appendPropertyCondition(
-		String propertyName,
-		Object propertyValue,
-		Criteria criteria,
-		CriteriaQuery cq,
-		StringBuilder buf)
-	throws HibernateException {
-		Criterion crit;
-		if ( propertyValue!=null ) {
-			boolean isString = propertyValue instanceof String;
+			String propertyName,
+			Object propertyValue,
+			Criteria criteria,
+			CriteriaQuery cq,
+			StringBuilder buf) {
+		final Criterion condition;
+		if ( propertyValue != null ) {
+			final boolean isString = propertyValue instanceof String;
 			if ( isLikeEnabled && isString ) {
-				crit = new LikeExpression(
+				condition = new LikeExpression(
 						propertyName,
-						( String ) propertyValue,
+						(String) propertyValue,
 						matchMode,
 						escapeCharacter,
 						isIgnoreCaseEnabled
 				);
 			}
 			else {
-				crit = new SimpleExpression( propertyName, propertyValue, "=", isIgnoreCaseEnabled && isString );
+				condition = new SimpleExpression( propertyName, propertyValue, "=", isIgnoreCaseEnabled && isString );
 			}
 		}
 		else {
-			crit = new NullExpression(propertyName);
+			condition = new NullExpression(propertyName);
+		}
+
+		final String conditionFragment = condition.toSqlString( criteria, cq );
+		if ( conditionFragment.trim().length() > 0 ) {
+			if ( buf.length() > 1 ) {
+				buf.append( " and " );
+			}
+			buf.append( conditionFragment );
 		}
-		String critCondition = crit.toSqlString(criteria, cq);
-		if ( buf.length()>1 && critCondition.trim().length()>0 ) buf.append(" and ");
-		buf.append(critCondition);
 	}
 
 	protected void appendComponentCondition(
-		String path,
-		Object component,
-		CompositeType type,
-		Criteria criteria,
-		CriteriaQuery criteriaQuery,
-		StringBuilder buf)
-	throws HibernateException {
-
-		if (component!=null) {
-			String[] propertyNames = type.getPropertyNames();
-			Object[] values = type.getPropertyValues( component, getEntityMode(criteria, criteriaQuery) );
-			Type[] subtypes = type.getSubtypes();
-			for (int i=0; i<propertyNames.length; i++) {
-				String subpath = StringHelper.qualify( path, propertyNames[i] );
-				Object value = values[i];
-				if ( isPropertyIncluded( value, subpath, subtypes[i] ) ) {
-					Type subtype = subtypes[i];
+			String path,
+			Object component,
+			CompositeType type,
+			Criteria criteria,
+			CriteriaQuery criteriaQuery,
+			StringBuilder buf) {
+		if ( component != null ) {
+			final String[] propertyNames = type.getPropertyNames();
+			final Object[] values = type.getPropertyValues( component, getEntityMode( criteria, criteriaQuery ) );
+			final Type[] subtypes = type.getSubtypes();
+			for ( int i=0; i<propertyNames.length; i++ ) {
+				final String subPath = StringHelper.qualify( path, propertyNames[i] );
+				final Object value = values[i];
+				if ( isPropertyIncluded( value, subPath, subtypes[i] ) ) {
+					final Type subtype = subtypes[i];
 					if ( subtype.isComponentType() ) {
 						appendComponentCondition(
-							subpath,
-							value,
-							(CompositeType) subtype,
-							criteria,
-							criteriaQuery,
-							buf
+								subPath,
+								value,
+								(CompositeType) subtype,
+								criteria,
+								criteriaQuery,
+								buf
 						);
 					}
 					else {
 						appendPropertyCondition(
-							subpath,
-							value,
-							criteria,
-							criteriaQuery,
-							buf
+								subPath,
+								value,
+								criteria,
+								criteriaQuery,
+								buf
 						);
 					}
 				}
 			}
 		}
 	}
-}
\ No newline at end of file
+
+	@Override
+	public String toString() {
+		return "example (" + exampleEntity + ')';
+	}
+
+
+	// PropertySelector definitions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * A strategy for choosing property values for inclusion in the query criteria.  Note that
+	 * property selection (for inclusion) operates separately from excluding a property.  Excluded
+	 * properties are not even passed in to the PropertySelector for consideration.
+	 */
+	public static interface PropertySelector extends Serializable {
+		/**
+		 * Determine whether the given property should be used in the criteria.
+		 *
+		 * @param propertyValue The property value (from the example bean)
+		 * @param propertyName The name of the property
+		 * @param type The type of the property
+		 *
+		 * @return {@code true} indicates the property should be included; {@code false} indiates it should not.
+		 */
+		public boolean include(Object propertyValue, String propertyName, Type type);
+	}
+
+	/**
+	 * Property selector that includes all properties
+	 */
+	public static final class AllPropertySelector implements PropertySelector {
+		/**
+		 * Singleton access
+		 */
+		public static final AllPropertySelector INSTANCE = new AllPropertySelector();
+
+		@Override
+		public boolean include(Object object, String propertyName, Type type) {
+			return true;
+		}
+
+		private Object readResolve() {
+			return INSTANCE;
+		}
+	}
+
+	/**
+	 * Property selector that includes only properties that are not {@code null}
+	 */
+	public static final class NotNullPropertySelector implements PropertySelector {
+		/**
+		 * Singleton access
+		 */
+		public static final NotNullPropertySelector INSTANCE = new NotNullPropertySelector();
+
+		@Override
+		public boolean include(Object object, String propertyName, Type type) {
+			return object!=null;
+		}
+
+		private Object readResolve() {
+			return INSTANCE;
+		}
+	}
+
+	/**
+	 * Property selector that includes only properties that are not {@code null} and non-zero (if numeric)
+	 */
+	public static final class NotNullOrZeroPropertySelector implements PropertySelector {
+		/**
+		 * Singleton access
+		 */
+		public static final NotNullOrZeroPropertySelector INSTANCE = new NotNullOrZeroPropertySelector();
+
+		@Override
+		public boolean include(Object object, String propertyName, Type type) {
+			return object != null
+					&& ( !(object instanceof Number) || ( (Number) object ).longValue()!=0
+			);
+		}
+
+		private Object readResolve() {
+			return INSTANCE;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java
index 61ab9209c4..cd50509fca 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/ExistsSubqueryExpression.java
@@ -1,40 +1,51 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
 /**
+ * Expression that checks the existence of rows in a sub-query
+ *
  * @author Gavin King
  */
 public class ExistsSubqueryExpression extends SubqueryExpression {
+	/**
+	 * Constructs the ExistsSubqueryExpression
+	 *
+	 * @param quantifier The "exists"/"not exists" sub-query quantifier
+	 * @param dc The DetachedCriteria representing the sub-query
+	 *
+	 * @see Subqueries#exists
+	 * @see Subqueries#notExists
+	 */
+	protected ExistsSubqueryExpression(String quantifier, DetachedCriteria dc) {
+		super( null, quantifier, dc );
+	}
 
+	@Override
 	protected String toLeftSqlString(Criteria criteria, CriteriaQuery outerQuery) {
 		return "";
 	}
-	
-	protected ExistsSubqueryExpression(String quantifier, DetachedCriteria dc) {
-		super(null, quantifier, dc);
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Expression.java b/hibernate-core/src/main/java/org/hibernate/criterion/Expression.java
index 3ad682547c..37603b375e 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Expression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Expression.java
@@ -1,85 +1,91 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
-import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.type.Type;
 
 /**
- * @deprecated Use <tt>Restrictions</tt>.
- * @see Restrictions
+ * Factory for Criterion objects.  Deprecated!
+ *
  * @author Gavin King
+ *
+ * @see Restrictions
+ *
+ * @deprecated Use {@link Restrictions} instead
  */
 @Deprecated
 public final class Expression extends Restrictions {
-
-	private Expression() {
-		//cannot be instantiated
-	}
-
 	/**
-	 * Apply a constraint expressed in SQL, with the given JDBC
-	 * parameters. Any occurrences of <tt>{alias}</tt> will be
+	 * Apply a constraint expressed in SQL, with JDBC parameters.  Any occurrences of <tt>{alias}</tt> will be
 	 * replaced by the table alias.
 	 *
-	 * @deprecated use {@link org.hibernate.criterion.Restrictions#sqlRestriction(String, Object[], Type[])}
-	 * @param sql
-	 * @param values
-	 * @param types
+	 * @param sql The sql
+	 * @param values The parameter values
+	 * @param types The parameter types
+	 *
 	 * @return Criterion
+	 *
+	 * @deprecated use {@link org.hibernate.criterion.Restrictions#sqlRestriction(String, Object[], Type[])}
 	 */
 	@Deprecated
-    public static Criterion sql(String sql, Object[] values, Type[] types) {
-		return new SQLCriterion(sql, values, types);
+	public static Criterion sql(String sql, Object[] values, Type[] types) {
+		return new SQLCriterion( sql, values, types );
 	}
+
 	/**
-	 * Apply a constraint expressed in SQL, with the given JDBC
-	 * parameter. Any occurrences of <tt>{alias}</tt> will be replaced
-	 * by the table alias.
+	 * Apply a constraint expressed in SQL, with a JDBC parameter.  Any occurrences of <tt>{alias}</tt> will be
+	 * replaced by the table alias.
+	 *
+	 * @param sql The sql
+	 * @param value The parameter value
+	 * @param type The parameter type
 	 *
-	 * @deprecated use {@link org.hibernate.criterion.Restrictions#sqlRestriction(String, Object, Type)}
-	 * @param sql
-	 * @param value
-	 * @param type
 	 * @return Criterion
+	 *
+	 * @deprecated use {@link org.hibernate.criterion.Restrictions#sqlRestriction(String, Object, Type)}
 	 */
 	@Deprecated
-    public static Criterion sql(String sql, Object value, Type type) {
-		return new SQLCriterion(sql, new Object[] { value }, new Type[] { type } );
+	public static Criterion sql(String sql, Object value, Type type) {
+		return new SQLCriterion( sql, value, type );
 	}
+
 	/**
-	 * Apply a constraint expressed in SQL. Any occurrences of <tt>{alias}</tt>
-	 * will be replaced by the table alias.
+	 * Apply a constraint expressed in SQL with no parameters.  Any occurrences of <tt>{alias}</tt> will be
+	 * replaced by the table alias.
+	 *
+	 * @param sql The sql
 	 *
-	 * @deprecated use {@link org.hibernate.criterion.Restrictions#sqlRestriction(String)}
-	 * @param sql
 	 * @return Criterion
+	 *
+	 * @deprecated use {@link org.hibernate.criterion.Restrictions#sqlRestriction(String)}
 	 */
 	@Deprecated
-    public static Criterion sql(String sql) {
-		return new SQLCriterion(sql, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY);
+	public static Criterion sql(String sql) {
+		return new SQLCriterion( sql );
 	}
 
+	private Expression() {
+		//cannot be instantiated
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
index 5c89a2445a..1fb625a1dc 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
@@ -1,67 +1,70 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * An identifier constraint
+ *
  * @author Gavin King
  */
 public class IdentifierEqExpression implements Criterion {
-
 	private final Object value;
 
+	/**
+	 * Constructs an IdentifierEqExpression
+	 *
+	 * @param value The identifier value
+	 *
+	 * @see Restrictions#idEq
+	 */
 	protected IdentifierEqExpression(Object value) {
 		this.value = value;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-
-		String[] columns = criteriaQuery.getIdentifierColumns(criteria);
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final String[] columns = criteriaQuery.getIdentifierColumns( criteria );
 
-		String result = StringHelper.join(
-				" and ",
-				StringHelper.suffix( columns, " = ?" )
-		);
-		if (columns.length>1) result = '(' + result + ')';
+		String result = StringHelper.join( " and ", StringHelper.suffix( columns, " = ?" ) );
+		if ( columns.length > 1) {
+			result = '(' + result + ')';
+		}
 		return result;
-
-		//TODO: get SQL rendering out of this package!
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		return new TypedValue[] { criteriaQuery.getTypedIdentifierValue(criteria, value) };
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
+		return new TypedValue[] { criteriaQuery.getTypedIdentifierValue( criteria, value ) };
 	}
 
+	@Override
 	public String toString() {
 		return "id = " + value;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
index e44c638c8e..d494f24cb5 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/IdentifierProjection.java
@@ -1,85 +1,96 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.HibernateException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * A property value, or grouped property value
+ *
  * @author Gavin King
  */
 public class IdentifierProjection extends SimpleProjection {
-
 	private boolean grouped;
-	
-	protected IdentifierProjection(boolean grouped) {
-		this.grouped = grouped;
-	}
-	
+
+	/**
+	 * Constructs a non-grouped identifier projection
+	 *
+	 * @see Projections#id
+	 */
 	protected IdentifierProjection() {
-		this(false);
+		this( false );
 	}
-	
-	public String toString() {
-		return "id";
+
+	/**
+	 *
+	 * Not used externally
+	 */
+	private IdentifierProjection(boolean grouped) {
+		this.grouped = grouped;
 	}
 
-	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		return new Type[] { criteriaQuery.getIdentifierType(criteria) };
+	@Override
+	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) {
+		return new Type[] { criteriaQuery.getIdentifierType( criteria ) };
 	}
 
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		StringBuilder buf = new StringBuilder();
-		String[] cols = criteriaQuery.getIdentifierColumns(criteria);
+	@Override
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
+		final StringBuilder buf = new StringBuilder();
+		final String[] cols = criteriaQuery.getIdentifierColumns( criteria );
 		for ( int i=0; i<cols.length; i++ ) {
 			buf.append( cols[i] )
-				.append(" as y")
-				.append(position + i)
-				.append('_');
-			if (i < cols.length -1)
-			   buf.append(", ");			
+					.append( " as y" )
+					.append( position + i )
+					.append( '_' );
+			if ( i < cols.length -1 ) {
+				buf.append( ", " );
+			}
 		}
 		return buf.toString();
 	}
 
+	@Override
 	public boolean isGrouped() {
 		return grouped;
 	}
-	
-	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		if (!grouped) {
-			return super.toGroupSqlString(criteria, criteriaQuery);
+
+	@Override
+	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		if ( !grouped ) {
+			return super.toGroupSqlString( criteria, criteriaQuery );
 		}
 		else {
-			return StringHelper.join( ", ", criteriaQuery.getIdentifierColumns(criteria) );
+			return StringHelper.join( ", ", criteriaQuery.getIdentifierColumns( criteria ) );
 		}
 	}
 
+	@Override
+	public String toString() {
+		return "id";
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
index 9219b98674..923890bcaf 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
@@ -1,86 +1,87 @@
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
 package org.hibernate.criterion;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
- * A case-insensitive "like"
+ * A case-insensitive "like".
  *
  * @author Gavin King
+ *
+ * @deprecated Prefer {@link LikeExpression} which now has case-insensitivity capability.
  */
 @Deprecated
+@SuppressWarnings({"deprecation", "UnusedDeclaration"})
 public class IlikeExpression implements Criterion {
-
 	private final String propertyName;
 	private final Object value;
 
 	protected IlikeExpression(String propertyName, Object value) {
 		this.propertyName = propertyName;
 		this.value = value;
 	}
 
 	protected IlikeExpression(String propertyName, String value, MatchMode matchMode) {
 		this( propertyName, matchMode.toMatchString( value ) );
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-			throws HibernateException {
-		Dialect dialect = criteriaQuery.getFactory().getDialect();
-		String[] columns = criteriaQuery.findColumns( propertyName, criteria );
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final Dialect dialect = criteriaQuery.getFactory().getDialect();
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
 		if ( columns.length != 1 ) {
 			throw new HibernateException( "ilike may only be used with single-column properties" );
 		}
 		if ( dialect instanceof PostgreSQLDialect || dialect instanceof PostgreSQL81Dialect) {
 			return columns[0] + " ilike ?";
 		}
 		else {
 			return dialect.getLowercaseFunction() + '(' + columns[0] + ") like ?";
 		}
-
-		//TODO: get SQL rendering out of this package!
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-			throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
 		return new TypedValue[] {
 				criteriaQuery.getTypedValue(
 						criteria,
 						propertyName,
 						value.toString().toLowerCase()
 				)
 		};
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + " ilike " + value;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
index ec8e913d18..c7bd8a45db 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/InExpression.java
@@ -1,105 +1,115 @@
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
 package org.hibernate.criterion;
+
 import java.util.ArrayList;
 
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Constrains the property to a specified list of values
+ *
  * @author Gavin King
  */
 public class InExpression implements Criterion {
-
 	private final String propertyName;
 	private final Object[] values;
 
+	/**
+	 * Constructs an InExpression
+	 *
+	 * @param propertyName The property name to check
+	 * @param values The values to check against
+	 *
+	 * @see Restrictions#in(String, java.util.Collection)
+	 * @see Restrictions#in(String, Object[])
+	 */
 	protected InExpression(String propertyName, Object[] values) {
 		this.propertyName = propertyName;
 		this.values = values;
 	}
 
-    public String toSqlString( Criteria criteria, CriteriaQuery criteriaQuery )
-            throws HibernateException {
-        String[] columns = criteriaQuery.findColumns(propertyName, criteria);
-        if ( criteriaQuery.getFactory().getDialect()
-                .supportsRowValueConstructorSyntaxInInList() || columns.length<=1) {
-
-            String singleValueParam = StringHelper.repeat( "?, ",
-                    columns.length - 1 )
-                    + "?";
-            if ( columns.length > 1 )
-                singleValueParam = '(' + singleValueParam + ')';
-            String params = values.length > 0 ? StringHelper.repeat(
-                    singleValueParam + ", ", values.length - 1 )
-                    + singleValueParam : "";
-            String cols = StringHelper.join( ", ", columns );
-            if ( columns.length > 1 )
-                cols = '(' + cols + ')';
-            return cols + " in (" + params + ')';
-        } else {
-           String cols = " ( " + StringHelper.join( " = ? and ", columns ) + "= ? ) ";
-             cols = values.length > 0 ? StringHelper.repeat( cols
-                    + "or ", values.length - 1 )
-                    + cols : "";
-            cols = " ( " + cols + " ) ";
-            return cols;
-        }
-    }
+	@Override
+	public String toSqlString( Criteria criteria, CriteriaQuery criteriaQuery ) {
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
+		if ( criteriaQuery.getFactory().getDialect().supportsRowValueConstructorSyntaxInInList() || columns.length <= 1 ) {
+			String singleValueParam = StringHelper.repeat( "?, ", columns.length - 1 ) + "?";
+			if ( columns.length > 1 ) {
+				singleValueParam = '(' + singleValueParam + ')';
+			}
+			final String params = values.length > 0
+					? StringHelper.repeat( singleValueParam + ", ", values.length - 1 ) + singleValueParam
+					: "";
+			String cols = StringHelper.join( ", ", columns );
+			if ( columns.length > 1 ) {
+				cols = '(' + cols + ')';
+			}
+			return cols + " in (" + params + ')';
+		}
+		else {
+			String cols = " ( " + StringHelper.join( " = ? and ", columns ) + "= ? ) ";
+			cols = values.length > 0
+					? StringHelper.repeat( cols + "or ", values.length - 1 ) + cols
+					: "";
+			cols = " ( " + cols + " ) ";
+			return cols;
+		}
+	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		ArrayList list = new ArrayList();
-		Type type = criteriaQuery.getTypeUsingProjection(criteria, propertyName);
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final ArrayList<TypedValue> list = new ArrayList<TypedValue>();
+		final Type type = criteriaQuery.getTypeUsingProjection( criteria, propertyName );
 		if ( type.isComponentType() ) {
-			CompositeType actype = (CompositeType) type;
-			Type[] types = actype.getSubtypes();
-			for ( int j=0; j<values.length; j++ ) {
-				for ( int i=0; i<types.length; i++ ) {
-					Object subval = values[j]==null ? 
-						null : 
-						actype.getPropertyValues( values[j], EntityMode.POJO )[i];
-					list.add( new TypedValue( types[i], subval, EntityMode.POJO ) );
+			final CompositeType compositeType = (CompositeType) type;
+			final Type[] subTypes = compositeType.getSubtypes();
+			for ( Object value : values ) {
+				for ( int i = 0; i < subTypes.length; i++ ) {
+					final Object subValue = value == null
+							? null
+							: compositeType.getPropertyValues( value, EntityMode.POJO )[i];
+					list.add( new TypedValue( subTypes[i], subValue ) );
 				}
 			}
 		}
 		else {
-			for ( int j=0; j<values.length; j++ ) {
-				list.add( new TypedValue( type, values[j], EntityMode.POJO ) );
+			for ( Object value : values ) {
+				list.add( new TypedValue( type, value ) );
 			}
 		}
-		return (TypedValue[]) list.toArray( new TypedValue[ list.size() ] );
+
+		return list.toArray( new TypedValue[ list.size() ] );
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + " in (" + StringHelper.toString(values) + ')';
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java b/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
index d3b2ebc946..d9cde4b3b3 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Junction.java
@@ -1,104 +1,138 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * A sequence of a logical expressions combined by some
  * associative logical operator
  *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public class Junction implements Criterion {
 	private final Nature nature;
 	private final List<Criterion> conditions = new ArrayList<Criterion>();
 
 	protected Junction(Nature nature) {
 		this.nature = nature;
 	}
-	
+
+	protected Junction(Nature nature, Criterion... criterion) {
+		this( nature );
+		Collections.addAll( conditions, criterion );
+	}
+
+	/**
+	 * Adds a criterion to the junction (and/or)
+	 *
+	 * @param criterion The criterion to add
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public Junction add(Criterion criterion) {
 		conditions.add( criterion );
 		return this;
 	}
 
 	public Nature getNature() {
 		return nature;
 	}
 
+	/**
+	 * Access the conditions making up the junction
+	 *
+	 * @return the criterion
+	 */
 	public Iterable<Criterion> conditions() {
 		return conditions;
 	}
 
 	@Override
 	public TypedValue[] getTypedValues(Criteria crit, CriteriaQuery criteriaQuery) throws HibernateException {
-		ArrayList<TypedValue> typedValues = new ArrayList<TypedValue>();
+		final ArrayList<TypedValue> typedValues = new ArrayList<TypedValue>();
 		for ( Criterion condition : conditions ) {
-			TypedValue[] subValues = condition.getTypedValues( crit, criteriaQuery );
+			final TypedValue[] subValues = condition.getTypedValues( crit, criteriaQuery );
 			Collections.addAll( typedValues, subValues );
 		}
 		return typedValues.toArray( new TypedValue[ typedValues.size() ] );
 	}
 
 	@Override
 	public String toSqlString(Criteria crit, CriteriaQuery criteriaQuery) throws HibernateException {
 		if ( conditions.size()==0 ) {
 			return "1=1";
 		}
 
-		StringBuilder buffer = new StringBuilder().append( '(' );
-		Iterator itr = conditions.iterator();
+		final StringBuilder buffer = new StringBuilder().append( '(' );
+		final Iterator itr = conditions.iterator();
 		while ( itr.hasNext() ) {
 			buffer.append( ( (Criterion) itr.next() ).toSqlString( crit, criteriaQuery ) );
 			if ( itr.hasNext() ) {
-				buffer.append(' ').append( nature.getOperator() ).append(' ');
+				buffer.append( ' ' )
+						.append( nature.getOperator() )
+						.append( ' ' );
 			}
 		}
-		return buffer.append(')').toString();
+
+		return buffer.append( ')' ).toString();
 	}
 
 	@Override
 	public String toString() {
 		return '(' + StringHelper.join( ' ' + nature.getOperator() + ' ', conditions.iterator() ) + ')';
 	}
 
+	/**
+	 * The type of junction
+	 */
 	public static enum Nature {
+		/**
+		 * An AND
+		 */
 		AND,
-		OR
-		;
+		/**
+		 * An OR
+		 */
+		OR;
 
+		/**
+		 * The corresponding SQL operator
+		 *
+		 * @return SQL operator
+		 */
 		public String getOperator() {
 			return name().toLowerCase();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java
index 22a89cf993..eb71480ab2 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/LikeExpression.java
@@ -1,106 +1,101 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
  * A criterion representing a "like" expression
  *
  * @author Scott Marlow
  * @author Steve Ebersole
  */
 public class LikeExpression implements Criterion {
 	private final String propertyName;
 	private final Object value;
 	private final Character escapeChar;
 	private final boolean ignoreCase;
 
 	protected LikeExpression(
 			String propertyName,
 			String value,
 			Character escapeChar,
 			boolean ignoreCase) {
 		this.propertyName = propertyName;
 		this.value = value;
 		this.escapeChar = escapeChar;
 		this.ignoreCase = ignoreCase;
 	}
 
-	protected LikeExpression(
-			String propertyName,
-			String value) {
+	protected LikeExpression(String propertyName, String value) {
 		this( propertyName, value, null, false );
 	}
 
-	protected LikeExpression(
-			String propertyName,
-			String value,
-			MatchMode matchMode) {
+	@SuppressWarnings("UnusedDeclaration")
+	protected LikeExpression(String propertyName, String value, MatchMode matchMode) {
 		this( propertyName, matchMode.toMatchString( value ) );
 	}
 
 	protected LikeExpression(
 			String propertyName,
 			String value,
 			MatchMode matchMode,
 			Character escapeChar,
 			boolean ignoreCase) {
 		this( propertyName, matchMode.toMatchString( value ), escapeChar, ignoreCase );
 	}
 
-	public String toSqlString(
-			Criteria criteria,
-			CriteriaQuery criteriaQuery) throws HibernateException {
-		Dialect dialect = criteriaQuery.getFactory().getDialect();
-		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
+	@Override
+	public String toSqlString(Criteria criteria,CriteriaQuery criteriaQuery) {
+		final Dialect dialect = criteriaQuery.getFactory().getDialect();
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
 		if ( columns.length != 1 ) {
 			throw new HibernateException( "Like may only be used with single-column properties" );
 		}
-		String escape = escapeChar == null ? "" : " escape \'" + escapeChar + "\'";
-		String column = columns[0];
+
+		final String escape = escapeChar == null ? "" : " escape \'" + escapeChar + "\'";
+		final String column = columns[0];
 		if ( ignoreCase ) {
 			if ( dialect.supportsCaseInsensitiveLike() ) {
 				return column +" " + dialect.getCaseInsensitiveLike() + " ?" + escape;
 			}
 			else {
 				return dialect.getLowercaseFunction() + '(' + column + ')' + " like ?" + escape;
 			}
 		}
 		else {
 			return column + " like ?" + escape;
 		}
 	}
 
-	public TypedValue[] getTypedValues(
-			Criteria criteria,
-			CriteriaQuery criteriaQuery) throws HibernateException {
-		return new TypedValue[] {
-				criteriaQuery.getTypedValue( criteria, propertyName, ignoreCase ? value.toString().toLowerCase() : value.toString() )
-		};
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final String matchValue = ignoreCase ? value.toString().toLowerCase() : value.toString();
+
+		return new TypedValue[] { criteriaQuery.getTypedValue( criteria, propertyName, matchValue ) };
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java
index 442eff8e69..7216b12272 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/LogicalExpression.java
@@ -1,76 +1,75 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
  * Superclass of binary logical expressions
+ *
  * @author Gavin King
  */
 public class LogicalExpression implements Criterion {
-
 	private final Criterion lhs;
 	private final Criterion rhs;
 	private final String op;
 
 	protected LogicalExpression(Criterion lhs, Criterion rhs, String op) {
 		this.lhs = lhs;
 		this.rhs = rhs;
 		this.op = op;
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final TypedValue[] lhsTypedValues = lhs.getTypedValues( criteria, criteriaQuery );
+		final TypedValue[] rhsTypedValues = rhs.getTypedValues( criteria, criteriaQuery );
 
-		TypedValue[] lhstv = lhs.getTypedValues(criteria, criteriaQuery);
-		TypedValue[] rhstv = rhs.getTypedValues(criteria, criteriaQuery);
-		TypedValue[] result = new TypedValue[ lhstv.length + rhstv.length ];
-		System.arraycopy(lhstv, 0, result, 0, lhstv.length);
-		System.arraycopy(rhstv, 0, result, lhstv.length, rhstv.length);
+		final TypedValue[] result = new TypedValue[ lhsTypedValues.length + rhsTypedValues.length ];
+		System.arraycopy( lhsTypedValues, 0, result, 0, lhsTypedValues.length );
+		System.arraycopy( rhsTypedValues, 0, result, lhsTypedValues.length, rhsTypedValues.length );
 		return result;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-
-		return '(' +
-			lhs.toSqlString(criteria, criteriaQuery) +
-			' ' +
-			getOp() +
-			' ' +
-			rhs.toSqlString(criteria, criteriaQuery) +
-			')';
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		return '('
+				+ lhs.toSqlString( criteria, criteriaQuery )
+				+ ' '
+				+ getOp()
+				+ ' '
+				+ rhs.toSqlString( criteria, criteriaQuery )
+				+ ')';
 	}
 
 	public String getOp() {
 		return op;
 	}
 
+	@Override
 	public String toString() {
 		return lhs.toString() + ' ' + getOp() + ' ' + rhs.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/MatchMode.java b/hibernate-core/src/main/java/org/hibernate/criterion/MatchMode.java
index 6b834c3f60..00c29052af 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/MatchMode.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/MatchMode.java
@@ -1,81 +1,88 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
 /**
  * Represents an strategy for matching strings using "like".
  *
  * @author Gavin King
  * @see Example#enableLike(MatchMode)
  */
 public enum MatchMode {
 
 	/**
 	 * Match the entire string to the pattern
 	 */
 	EXACT {
+		@Override
 		public String toMatchString(String pattern) {
 			return pattern;
 		}
 	},
 
 	/**
 	 * Match the start of the string to the pattern
 	 */
 	START {
+		@Override
 		public String toMatchString(String pattern) {
 			return pattern + '%';
 		}
 	},
 
 	/**
 	 * Match the end of the string to the pattern
 	 */
 	END {
+		@Override
 		public String toMatchString(String pattern) {
 			return '%' + pattern;
 		}
 	},
 
 	/**
 	 * Match the pattern anywhere in the string
 	 */
 	ANYWHERE {
+		@Override
 		public String toMatchString(String pattern) {
 			return '%' + pattern + '%';
 		}
 	};
 
 	/**
-	 * convert the pattern, by appending/prepending "%"
+	 * Convert the pattern, by appending/prepending "%"
+	 *
+	 * @param pattern The pattern for convert according to the mode
+	 *
+	 * @return The converted pattern
 	 */
 	public abstract String toMatchString(String pattern);
 
 }
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
index c98ba83f5d..51ec35cf7b 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
@@ -1,73 +1,91 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
 
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
-import org.hibernate.Session;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
+ * An expression pertaining to an entity's defined natural identifier
+ *
  * @author Gavin King
- * @see Session#byNaturalId(Class)
- * @see Session#byNaturalId(String)
- * @see Session#bySimpleNaturalId(Class)
- * @see Session#bySimpleNaturalId(String)
+ *
+ * @see org.hibernate.Session#byNaturalId(Class)
+ * @see org.hibernate.Session#byNaturalId(String)
+ * @see org.hibernate.Session#bySimpleNaturalId(Class)
+ * @see org.hibernate.Session#bySimpleNaturalId(String)
  */
 public class NaturalIdentifier implements Criterion {
 	private final Conjunction conjunction = new Conjunction();
 
+	@Override
 	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return conjunction.getTypedValues( criteria, criteriaQuery );
 	}
 
+	@Override
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return conjunction.toSqlString( criteria, criteriaQuery );
 	}
 
+	/**
+	 * Get a map of set of the natural identifier values set on this criterion (for composite natural identifiers
+	 * this need not be the full set of properties).
+	 *
+	 * @return The value map.
+	 */
 	public Map<String, Object> getNaturalIdValues() {
 		final Map<String, Object> naturalIdValueMap = new ConcurrentHashMap<String, Object>();
 		for ( Criterion condition : conjunction.conditions() ) {
 			if ( !SimpleExpression.class.isInstance( condition ) ) {
 				continue;
 			}
 			final SimpleExpression equalsCondition = SimpleExpression.class.cast( condition );
 			if ( !"=".equals( equalsCondition.getOp() ) ) {
 				continue;
 			}
 
 			naturalIdValueMap.put( equalsCondition.getPropertyName(), equalsCondition.getValue() );
 		}
 		return naturalIdValueMap;
 	}
 
+	/**
+	 * Set a natural identifier value for this expression
+	 *
+	 * @param property The specific property name
+	 * @param value The value to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public NaturalIdentifier set(String property, Object value) {
 		conjunction.add( Restrictions.eq( property, value ) );
 		return this;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java
index f3bf90ce93..aec3b7c1af 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NotEmptyExpression.java
@@ -1,41 +1,48 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
-
 /**
+ * An expression asserting that a collection property is empty
+ *
  * @author Gavin King
  */
 public class NotEmptyExpression extends AbstractEmptinessExpression implements Criterion {
-
+	/**
+	 * Constructs an EmptyExpression
+	 *
+	 * @param propertyName The collection property name
+	 *
+	 * @see Restrictions#isNotEmpty
+	 */
 	protected NotEmptyExpression(String propertyName) {
 		super( propertyName );
 	}
 
+	@Override
 	protected boolean excludeEmpty() {
 		return true;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java
index 35bf4e5346..83d49063db 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NotExpression.java
@@ -1,58 +1,66 @@
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
 package org.hibernate.criterion;
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
- * Negates another criterion
+ * A criterion that is a wrapper for another, negating the wrapped one.
+ *
  * @author Gavin King
- * @author Brett Meyer
  */
 public class NotExpression implements Criterion {
-
 	private Criterion criterion;
 
+	/**
+	 * Constructs a NotExpression
+	 *
+	 * @param criterion The expression to wrap and negate
+	 *
+	 * @see Restrictions#not
+	 */
 	protected NotExpression(Criterion criterion) {
 		this.criterion = criterion;
 	}
 
+	@Override
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return criteriaQuery.getFactory().getDialect().getNotExpression(
-				criterion.toSqlString( criteria, criteriaQuery ) );
+				criterion.toSqlString( criteria, criteriaQuery )
+		);
 	}
 
-	public TypedValue[] getTypedValues(
-		Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		return criterion.getTypedValues(criteria, criteriaQuery);
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		return criterion.getTypedValues( criteria, criteriaQuery );
 	}
 
+	@Override
 	public String toString() {
 		return "not " + criterion.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
index 0f1c3abf56..5788af8431 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
@@ -1,67 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Constrains a property to be non-null
+ *
  * @author Gavin King
  */
 public class NotNullExpression implements Criterion {
+	private static final TypedValue[] NO_VALUES = new TypedValue[0];
 
 	private final String propertyName;
 
-	private static final TypedValue[] NO_VALUES = new TypedValue[0];
-
 	protected NotNullExpression(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
 		String result = StringHelper.join(
 				" or ",
 				StringHelper.suffix( columns, " is not null" )
 		);
-		if (columns.length>1) result = '(' + result + ')';
+		if ( columns.length > 1 ) {
+			result = '(' + result + ')';
+		}
 		return result;
-
-		//TODO: get SQL rendering out of this package!
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return NO_VALUES;
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + " is not null";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
index 1d22ef121a..2a451f91dd 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
@@ -1,67 +1,75 @@
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
 package org.hibernate.criterion;
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Constrains a property to be null
+ *
  * @author Gavin King
  */
 public class NullExpression implements Criterion {
+	private static final TypedValue[] NO_VALUES = new TypedValue[0];
 
 	private final String propertyName;
 
-	private static final TypedValue[] NO_VALUES = new TypedValue[0];
-
+	/**
+	 * Constructs a NullExpression
+	 *
+	 * @param propertyName The name of the property to check for null
+	 *
+	 * @see Restrictions#isNull
+	 */
 	protected NullExpression(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
 		String result = StringHelper.join(
 				" and ",
 				StringHelper.suffix( columns, " is null" )
 		);
-		if (columns.length>1) result = '(' + result + ')';
+		if ( columns.length > 1 ) {
+			result = '(' + result + ')';
+		}
 		return result;
-
-		//TODO: get SQL rendering out of this package!
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return NO_VALUES;
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + " is null";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Order.java b/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
index 9888d76400..c7feea418c 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Order.java
@@ -1,141 +1,172 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import java.io.Serializable;
 import java.sql.Types;
 
 import org.hibernate.Criteria;
-import org.hibernate.HibernateException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
- * Represents an order imposed upon a <tt>Criteria</tt> result set
+ * Represents an ordering imposed upon the results of a Criteria
  * 
  * @author Gavin King
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
- * @author Brett Meyer
  */
 public class Order implements Serializable {
 	private boolean ascending;
 	private boolean ignoreCase;
 	private String propertyName;
 	private NullPrecedence nullPrecedence;
-	
-	public String toString() {
-		return propertyName + ' ' + ( ascending ? "asc" : "desc" ) + ( nullPrecedence != null ? ' ' + nullPrecedence.name().toLowerCase() : "" );
+
+	/**
+	 * Ascending order
+	 *
+	 * @param propertyName The property to order on
+	 *
+	 * @return The build Order instance
+	 */
+	public static Order asc(String propertyName) {
+		return new Order( propertyName, true );
 	}
-	
+
+	/**
+	 * Descending order.
+	 *
+	 * @param propertyName The property to order on
+	 *
+	 * @return The build Order instance
+	 */
+	public static Order desc(String propertyName) {
+		return new Order( propertyName, false );
+	}
+
+	/**
+	 * Constructor for Order.  Order instances are generally created by factory methods.
+	 *
+	 * @see #asc
+	 * @see #desc
+	 */
+	protected Order(String propertyName, boolean ascending) {
+		this.propertyName = propertyName;
+		this.ascending = ascending;
+	}
+
+	/**
+	 * Should this ordering ignore case?  Has no effect on non-character properties.
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public Order ignoreCase() {
 		ignoreCase = true;
 		return this;
 	}
 
+	/**
+	 * Defines precedence for nulls.
+	 *
+	 * @param nullPrecedence The null precedence to use
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public Order nulls(NullPrecedence nullPrecedence) {
 		this.nullPrecedence = nullPrecedence;
 		return this;
 	}
 
-	/**
-	 * Constructor for Order.
-	 */
-	protected Order(String propertyName, boolean ascending) {
-		this.propertyName = propertyName;
-		this.ascending = ascending;
+	public String getPropertyName() {
+		return propertyName;
+	}
+
+	@SuppressWarnings("UnusedDeclaration")
+	public boolean isAscending() {
+		return ascending;
 	}
 
+	@SuppressWarnings("UnusedDeclaration")
+	public boolean isIgnoreCase() {
+		return ignoreCase;
+	}
+
+
 	/**
 	 * Render the SQL fragment
 	 *
+	 * @param criteria The criteria
+	 * @param criteriaQuery The overall query
+	 *
+	 * @return The ORDER BY fragment for this ordering
 	 */
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);
-		Type type = criteriaQuery.getTypeUsingProjection(criteria, propertyName);
-		StringBuilder fragment = new StringBuilder();
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
+		final String[] columns = criteriaQuery.getColumnsUsingProjection( criteria, propertyName );
+		final Type type = criteriaQuery.getTypeUsingProjection( criteria, propertyName );
+		final SessionFactoryImplementor factory = criteriaQuery.getFactory();
+		final int[] sqlTypes = type.sqlTypes( factory );
+
+		final StringBuilder fragment = new StringBuilder();
 		for ( int i=0; i<columns.length; i++ ) {
 			final StringBuilder expression = new StringBuilder();
-			SessionFactoryImplementor factory = criteriaQuery.getFactory();
 			boolean lower = false;
 			if ( ignoreCase ) {
-				int sqlType = type.sqlTypes( factory )[i];
+				int sqlType = sqlTypes[i];
 				lower = sqlType == Types.VARCHAR
 						|| sqlType == Types.CHAR
 						|| sqlType == Types.LONGVARCHAR;
 			}
 			
-			if (lower) {
-				expression.append( factory.getDialect().getLowercaseFunction() ).append('(');
+			if ( lower ) {
+				expression.append( factory.getDialect().getLowercaseFunction() )
+						.append( '(' );
 			}
 			expression.append( columns[i] );
-			if (lower) expression.append(')');
+			if ( lower ) {
+				expression.append( ')' );
+			}
+
 			fragment.append(
-					factory.getDialect()
-							.renderOrderByElement(
-									expression.toString(),
-									null,
-									ascending ? "asc" : "desc",
-									nullPrecedence != null ? nullPrecedence : factory.getSettings().getDefaultNullPrecedence()
-							)
+					factory.getDialect().renderOrderByElement(
+							expression.toString(),
+							null,
+							ascending ? "asc" : "desc",
+							nullPrecedence != null ? nullPrecedence : factory.getSettings().getDefaultNullPrecedence()
+					)
 			);
-			if ( i<columns.length-1 ) fragment.append(", ");
+			if ( i < columns.length-1 ) {
+				fragment.append( ", " );
+			}
 		}
+
 		return fragment.toString();
 	}
 	
-	public String getPropertyName() {
-		return propertyName;
-	}
-
-	public boolean isAscending() {
-		return ascending;
-	}
-
-	public boolean isIgnoreCase() {
-		return ignoreCase;
-	}
-
-	/**
-	 * Ascending order
-	 *
-	 * @param propertyName
-	 * @return Order
-	 */
-	public static Order asc(String propertyName) {
-		return new Order(propertyName, true);
-	}
-
-	/**
-	 * Descending order
-	 *
-	 * @param propertyName
-	 * @return Order
-	 */
-	public static Order desc(String propertyName) {
-		return new Order(propertyName, false);
+	@Override
+	public String toString() {
+		return propertyName + ' '
+				+ ( ascending ? "asc" : "desc" )
+				+ ( nullPrecedence != null ? ' ' + nullPrecedence.name().toLowerCase() : "" );
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Projection.java b/hibernate-core/src/main/java/org/hibernate/criterion/Projection.java
index 1648bae1bb..f94720288e 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Projection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Projection.java
@@ -1,135 +1,136 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import java.io.Serializable;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.type.Type;
 
 /**
  * An object-oriented representation of a query result set projection  in a {@link Criteria} query.
  * Built-in projection types are provided  by the {@link Projections} factory class.  This interface might be
  * implemented by application classes that define custom projections.
  *
  * @author Gavin King
  * @author Steve Ebersole
+ *
  * @see Projections
  * @see Criteria
  */
 public interface Projection extends Serializable {
 
 	/**
 	 * Render the SQL fragment to be used in the <tt>SELECT</tt> clause.
 	 *
 	 * @param criteria The local criteria to which this project is attached (for resolution).
 	 * @param position The number of columns rendered in the <tt>SELECT</tt> clause before this projection.  Generally
 	 * speaking this is useful to ensure uniqueness of the individual columns aliases.
 	 * @param criteriaQuery The overall criteria query instance.
 	 * @return The SQL fragment to plug into the <tt>SELECT</tt>
 	 * @throws HibernateException Indicates a problem performing the rendering
 	 */
 	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery)
 			throws HibernateException;
 
 	/**
 	 * Render the SQL fragment to be used in the <tt>GROUP BY</tt> clause
 	 *
 	 * @param criteria The local criteria to which this project is attached (for resolution).
 	 * @param criteriaQuery The overall criteria query instance.
 	 * @return The SQL fragment to plug into the <tt>GROUP BY</tt>
 	 * @throws HibernateException Indicates a problem performing the rendering
 	 */
 	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
 			throws HibernateException;
 
 	/**
 	 * Types returned by the rendered SQL {@link #toSqlString fragment}.  In other words what are the types
 	 * that would represent the values this projection asked to be pulled into the result set?
 	 *
 	 * @param criteria The local criteria to which this project is attached (for resolution).
 	 * @param criteriaQuery The overall criteria query instance.
 	 * @return The return types.
 	 * @throws HibernateException Indicates a problem resolving the types
 	 */
 	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery)
 			throws HibernateException;
 
 	/**
 	 * Get the return types for a particular user-visible alias.
 	 * <p/>
 	 * Differs from {@link #getTypes(org.hibernate.Criteria, CriteriaQuery)} in that here we are only interested in
 	 * the types related to the given criteria-level alias.
 	 *
 	 * @param alias The criteria-level alias for which to find types.
 	 * @param criteria The local criteria to which this project is attached (for resolution).
 	 * @param criteriaQuery The overall criteria query instance.
 	 * @return The return types; expected to return null if this projection does not understand this alias.
 	 * @throws HibernateException Indicates a problem resolving the types
 	 */
 	public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery)
 			throws HibernateException;
 
 
 	/**
 	 * Get the SQL column aliases used by this projection for the columns it writes for inclusion into the
 	 * <tt>SELECT</tt> clause ({@link #toSqlString}.  Hibernate always uses column aliases to extract data from the
 	 * JDBC {@link java.sql.ResultSet}, so it is important that these be implemented correctly in order for
 	 * Hibernate to be able to extract these val;ues correctly.
 	 *
 	 * @param position Just as in {@link #toSqlString}, represents the number of <b>columns</b> rendered
 	 * prior to this projection.
 	 * @return The columns aliases.
 	 */
 	public String[] getColumnAliases(int position);
 
 	/**
 	 * Get the SQL column aliases used by this projection for the columns it writes for inclusion into the
 	 * <tt>SELECT</tt> clause ({@link #toSqlString} <i>for a particular criteria-level alias</i>.
 	 *
 	 * @param alias The criteria-level alias
 	 * @param position Just as in {@link #toSqlString}, represents the number of <b>columns</b> rendered
 	 * prior to this projection.
 	 * @return The columns aliases pertaining to a particular criteria-level alias; expected to return null if
 	 * this projection does not understand this alias.
 	 */
 	public String[] getColumnAliases(String alias, int position);
 
 	/**
 	 * Get the criteria-level aliases for this projection (ie. the ones that will be passed to the
 	 * {@link org.hibernate.transform.ResultTransformer})
 	 *
 	 * @return The aliases
 	 */
 	public String[] getAliases();
 
 	/**
 	 * Is this projection fragment (<tt>SELECT</tt> clause) also part of the <tt>GROUP BY</tt>
 	 *
 	 * @return True if the projection is also part of the <tt>GROUP BY</tt>; false otherwise.
 	 */
 	public boolean isGrouped();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java b/hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java
index 2b060984e3..f6758187f1 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/ProjectionList.java
@@ -1,178 +1,242 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import java.util.ArrayList;
+import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
-import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.type.Type;
 
 /**
+ * A projection that wraps other projections to allow selecting multiple values.
+ *
  * @author Gavin King
  */
 public class ProjectionList implements EnhancedProjection {
-	
-	private List elements = new ArrayList();
-	
-	protected ProjectionList() {}
-	
+	private List<Projection> elements = new ArrayList<Projection>();
+
+	/**
+	 * Constructs a ProjectionList
+	 *
+	 * @see Projections#projectionList()
+	 */
+	protected ProjectionList() {
+	}
+
+	/**
+	 * Lol
+	 *
+	 * @return duh
+	 *
+	 * @deprecated an instance factory method does not make sense
+	 *
+	 * @see Projections#projectionList()
+	 */
+	@Deprecated
 	public ProjectionList create() {
 		return new ProjectionList();
 	}
-	
-	public ProjectionList add(Projection proj) {
-		elements.add(proj);
+
+	/**
+	 * Add a projection to this list of projections
+	 *
+	 * @param projection The projection to add
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public ProjectionList add(Projection projection) {
+		elements.add( projection );
 		return this;
 	}
 
+	/**
+	 * Adds a projection to this list of projections after wrapping it with an alias
+	 *
+	 * @param projection The projection to add
+	 * @param alias The alias to apply to the projection
+	 *
+	 * @return {@code this}, for method chaining
+	 *
+	 * @see Projections#alias
+	 */
 	public ProjectionList add(Projection projection, String alias) {
 		return add( Projections.alias( projection, alias ) );
 	}
 
-	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		List types = new ArrayList( getLength() );
-		for ( int i=0; i<getLength(); i++ ) {
-			Type[] elemTypes = getProjection(i).getTypes(criteria, criteriaQuery);
-			ArrayHelper.addAll(types, elemTypes);
+	@Override
+	public boolean isGrouped() {
+		for ( Projection projection : elements ) {
+			if ( projection.isGrouped() ) {
+				return true;
+			}
 		}
-		return ArrayHelper.toTypeArray(types);
-	}
-	
-	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		StringBuilder buf = new StringBuilder();
-		for ( int i=0; i<getLength(); i++ ) {
-			Projection proj = getProjection(i);
-			buf.append( proj.toSqlString(criteria, loc, criteriaQuery) );
-			loc += getColumnAliases(loc, criteria, criteriaQuery, proj ).length;
-			if ( i<elements.size()-1 ) buf.append(", ");
+		return false;
+	}
+
+	@Override
+	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final List<Type> types = new ArrayList<Type>( getLength() );
+		for ( Projection projection : elements ) {
+			final Type[] elemTypes = projection.getTypes( criteria, criteriaQuery );
+			Collections.addAll( types, elemTypes );
+		}
+		return types.toArray( new Type[types.size()] );
+	}
+
+	@Override
+	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) throws HibernateException {
+		final StringBuilder buf = new StringBuilder();
+		String separator = "";
+
+		for ( Projection projection : elements ) {
+			buf.append( separator ).append( projection.toSqlString( criteria, loc, criteriaQuery ) );
+			loc += getColumnAliases( loc, criteria, criteriaQuery, projection ).length;
+			separator = ", ";
 		}
 		return buf.toString();
 	}
-	
-	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		StringBuilder buf = new StringBuilder();
-		for ( int i=0; i<getLength(); i++ ) {
-			Projection proj = getProjection(i);
-			if ( proj.isGrouped() ) {
-				buf.append( proj.toGroupSqlString(criteria, criteriaQuery) )
-					.append(", ");
+
+	@Override
+	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final StringBuilder buf = new StringBuilder();
+		String separator = "";
+		for ( Projection projection : elements ) {
+			if ( ! projection.isGrouped() ) {
+				continue;
 			}
+
+			buf.append( separator ).append( projection.toGroupSqlString( criteria, criteriaQuery ) );
+			separator = ", ";
 		}
-		if ( buf.length()>2 ) buf.setLength( buf.length()-2 ); //pull off the last ", "
 		return buf.toString();
 	}
-	
-	public String[] getColumnAliases(int loc) {
-		List result = new ArrayList( getLength() );
-		for ( int i=0; i<getLength(); i++ ) {
-			String[] colAliases = getProjection(i).getColumnAliases(loc);
-			ArrayHelper.addAll(result, colAliases);
-			loc+=colAliases.length;
+
+	@Override
+	public String[] getColumnAliases(final int loc) {
+		int position = loc;
+		final List<String> result = new ArrayList<String>( getLength() );
+		for ( Projection projection : elements ) {
+			final String[] aliases = projection.getColumnAliases( position );
+			Collections.addAll( result, aliases );
+			position += aliases.length;
 		}
-		return ArrayHelper.toStringArray(result);
+		return result.toArray( new String[ result.size() ] );
 	}
 
-	public String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
-		List result = new ArrayList( getLength() );
-		for ( int i=0; i<getLength(); i++ ) {
-			String[] colAliases = getColumnAliases( loc, criteria, criteriaQuery, getProjection( i ) );
-			ArrayHelper.addAll(result, colAliases);
-			loc+=colAliases.length;
+	@Override
+	public String[] getColumnAliases(final int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
+		int position = loc;
+		final List<String> result = new ArrayList<String>( getLength() );
+		for ( Projection projection : elements ) {
+			final String[] aliases = getColumnAliases( position, criteria, criteriaQuery, projection );
+			Collections.addAll( result, aliases );
+			position += aliases.length;
 		}
-		return ArrayHelper.toStringArray(result);
+		return result.toArray( new String[result.size()] );
 	}
 
-	public String[] getColumnAliases(String alias, int loc) {
-		for ( int i=0; i<getLength(); i++ ) {
-			String[] result = getProjection(i).getColumnAliases(alias, loc);
-			if (result!=null) return result;
-			loc += getProjection(i).getColumnAliases(loc).length;
+	@Override
+	public String[] getColumnAliases(String alias, final int loc) {
+		int position = loc;
+		for ( Projection projection : elements ) {
+			final String[] aliases = projection.getColumnAliases( alias, position );
+			if ( aliases != null ) {
+				return aliases;
+			}
+			position += projection.getColumnAliases( position ).length;
 		}
 		return null;
 	}
 
+	@Override
 	public String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
-		for ( int i=0; i<getLength(); i++ ) {
-			String[] result = getColumnAliases( alias, loc, criteria, criteriaQuery, getProjection(i) );
-			if (result!=null) return result;
-			loc += getColumnAliases( loc, criteria, criteriaQuery, getProjection( i ) ).length;
+		int position = loc;
+		for ( Projection projection : elements ) {
+			final String[] aliases = getColumnAliases( alias, position, criteria, criteriaQuery, projection );
+			if ( aliases != null ) {
+				return aliases;
+			}
+			position += getColumnAliases( position, criteria, criteriaQuery, projection ).length;
 		}
 		return null;
 	}
 
 	private static String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery, Projection projection) {
-		return projection instanceof EnhancedProjection ?
-				( ( EnhancedProjection ) projection ).getColumnAliases( loc, criteria, criteriaQuery ) :
-				projection.getColumnAliases( loc );
+		return projection instanceof EnhancedProjection
+				? ( (EnhancedProjection) projection ).getColumnAliases( loc, criteria, criteriaQuery )
+				: projection.getColumnAliases( loc );
 	}
 
 	private static String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery, Projection projection) {
-		return projection instanceof EnhancedProjection ?
-				( ( EnhancedProjection ) projection ).getColumnAliases( alias, loc, criteria, criteriaQuery ) :
-				projection.getColumnAliases( alias, loc );
+		return projection instanceof EnhancedProjection
+				? ( (EnhancedProjection) projection ).getColumnAliases( alias, loc, criteria, criteriaQuery )
+				: projection.getColumnAliases( alias, loc );
 	}
 
+	@Override
 	public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery) {
-		for ( int i=0; i<getLength(); i++ ) {
-			Type[] result = getProjection(i).getTypes(alias, criteria, criteriaQuery);
-			if (result!=null) return result;
+		for ( Projection projection : elements ) {
+			final Type[] types = projection.getTypes( alias, criteria, criteriaQuery );
+			if ( types != null ) {
+				return types;
+			}
 		}
 		return null;
 	}
 
+	@Override
 	public String[] getAliases() {
-		List result = new ArrayList( getLength() );
-		for ( int i=0; i<getLength(); i++ ) {
-			String[] aliases = getProjection(i).getAliases();
-			ArrayHelper.addAll( result, aliases );
+		final List<String> result = new ArrayList<String>( getLength() );
+		for ( Projection projection : elements ) {
+			final String[] aliases = projection.getAliases();
+			Collections.addAll( result, aliases );
 		}
-		return ArrayHelper.toStringArray(result);
-
+		return result.toArray( new String[result.size()] );
 	}
-	
+
+	/**
+	 * Access a wrapped projection by index
+	 *
+	 * @param i The index of the projection to return
+	 *
+	 * @return The projection
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Projection getProjection(int i) {
-		return (Projection) elements.get(i);
+		return elements.get( i );
 	}
-	
+
 	public int getLength() {
 		return elements.size();
 	}
 
+	@Override
 	public String toString() {
 		return elements.toString();
 	}
 
-	public boolean isGrouped() {
-		for ( int i=0; i<getLength(); i++ ) {
-			if ( getProjection(i).isGrouped() ) return true;
-		}
-		return false;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Projections.java b/hibernate-core/src/main/java/org/hibernate/criterion/Projections.java
index 79ce711148..53de5cffaf 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Projections.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Projections.java
@@ -1,150 +1,242 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.type.Type;
 
 /**
  * The <tt>criterion</tt> package may be used by applications as a framework for building
  * new kinds of <tt>Projection</tt>. However, it is intended that most applications will
- * simply use the built-in projection types via the static factory methods of this class.<br/>
- * <br/>
+ * simply use the built-in projection types via the static factory methods of this class.
+ *
  * The factory methods that take an alias allow the projected value to be referred to by 
  * criterion and order instances.
  *
- * @see org.hibernate.Criteria
- * @see Restrictions factory methods for <tt>Criterion</tt> instances
+ * See also the {@link Restrictions} factory methods for generating {@link Criterion} instances
+ *
  * @author Gavin King
+ * @author Steve Ebersole
+ *
+ * @see org.hibernate.Criteria
  */
 public final class Projections {
+	/**
+	 * A property value projection
+	 *
+	 * @param propertyName The name of the property whose values should be projected
+	 *
+	 * @return The property projection
+	 *
+	 * @see PropertyProjection
+	 */
+	public static PropertyProjection property(String propertyName) {
+		return new PropertyProjection( propertyName );
+	}
 
-	private Projections() {
-		//cannot be instantiated
+	/**
+	 * A grouping property value projection
+	 *
+	 * @param propertyName The name of the property to group
+	 *
+	 * @return The grouped projection
+	 *
+	 * @see PropertyProjection
+	 */
+	public static PropertyProjection groupProperty(String propertyName) {
+		return new PropertyProjection( propertyName, true );
 	}
-	
+
+	/**
+	 * An identifier value projection.
+	 *
+	 * @return The identifier projection
+	 *
+	 * @see IdentifierProjection
+	 */
+	public static IdentifierProjection id() {
+		return new IdentifierProjection();
+	}
+
 	/**
-	 * Create a distinct projection from a projection
+	 * Create a distinct projection from a projection.
+	 *
+	 * @param projection The project to treat distinctly
+	 *
+	 * @return The distinct projection
+	 *
+	 * @see Distinct
 	 */
-	public static Projection distinct(Projection proj) {
-		return new Distinct(proj);
+	public static Projection distinct(Projection projection) {
+		return new Distinct( projection );
 	}
 	
 	/**
-	 * Create a new projection list
+	 * Create a new projection list.
+	 *
+	 * @return The projection list
 	 */
 	public static ProjectionList projectionList() {
 		return new ProjectionList();
 	}
 		
 	/**
 	 * The query row count, ie. <tt>count(*)</tt>
+	 *
+	 * @return The projection representing the row count
+	 *
+	 * @see RowCountProjection
 	 */
 	public static Projection rowCount() {
 		return new RowCountProjection();
 	}
 	
 	/**
-	 * A property value count
+	 * A property value count projection
+	 *
+	 * @param propertyName The name of the property to count over
+	 *
+	 * @return The count projection
+	 *
+	 * @see CountProjection
 	 */
 	public static CountProjection count(String propertyName) {
-		return new CountProjection(propertyName);
+		return new CountProjection( propertyName );
 	}
 	
 	/**
-	 * A distinct property value count
+	 * A distinct property value count projection
+	 *
+	 * @param propertyName The name of the property to count over
+	 *
+	 * @return The count projection
+	 *
+	 * @see CountProjection
 	 */
 	public static CountProjection countDistinct(String propertyName) {
-		return new CountProjection(propertyName).setDistinct();
+		return new CountProjection( propertyName ).setDistinct();
 	}
 	
 	/**
-	 * A property maximum value
+	 * A property maximum value projection
+	 *
+	 * @param propertyName The property for which to find the max
+	 *
+	 * @return the max projection
+	 *
+	 * @see AggregateProjection
 	 */
 	public static AggregateProjection max(String propertyName) {
-		return new AggregateProjection("max", propertyName);
+		return new AggregateProjection( "max", propertyName );
 	}
 	
 	/**
-	 * A property minimum value
+	 * A property minimum value projection
+	 *
+	 * @param propertyName The property for which to find the min
+	 *
+	 * @return the min projection
+	 *
+	 * @see AggregateProjection
 	 */
 	public static AggregateProjection min(String propertyName) {
-		return new AggregateProjection("min", propertyName);
+		return new AggregateProjection( "min", propertyName );
 	}
 	
 	/**
-	 * A property average value
+	 * A property average value projection
+	 *
+	 * @param propertyName The property over which to find the average
+	 *
+	 * @return the avg projection
+	 *
+	 * @see AvgProjection
 	 */
 	public static AggregateProjection avg(String propertyName) {
-		return new AvgProjection(propertyName);
+		return new AvgProjection( propertyName );
 	}
 	
 	/**
-	 * A property value sum
+	 * A property value sum projection
+	 *
+	 * @param propertyName The property over which to sum
+	 *
+	 * @return the sum projection
+	 *
+	 * @see AggregateProjection
 	 */
 	public static AggregateProjection sum(String propertyName) {
-		return new AggregateProjection("sum", propertyName);
+		return new AggregateProjection( "sum", propertyName );
 	}
 	
 	/**
+	 * Assign an alias to a projection, by wrapping it
+	 *
+	 * @param projection The projection to be aliased
+	 * @param alias The alias to apply
+	 *
+	 * @return The aliased projection
+	 *
+	 * @see AliasedProjection
+	 */
+	public static Projection alias(Projection projection, String alias) {
+		return new AliasedProjection( projection, alias );
+	}
+
+	/**
 	 * A SQL projection, a typed select clause fragment
+	 *
+	 * @param sql The SQL fragment
+	 * @param columnAliases The column aliases
+	 * @param types The resulting types
+	 *
+	 * @return The SQL projection
+	 *
+	 * @see SQLProjection
 	 */
 	public static Projection sqlProjection(String sql, String[] columnAliases, Type[] types) {
-		return new SQLProjection(sql, columnAliases, types);
+		return new SQLProjection( sql, columnAliases, types );
 	}
-	
+
 	/**
 	 * A grouping SQL projection, specifying both select clause and group by clause fragments
+	 *
+	 * @param sql The SQL SELECT fragment
+	 * @param groupBy The SQL GROUP BY fragment
+	 * @param columnAliases The column aliases
+	 * @param types The resulting types
+	 *
+	 * @return The SQL projection
+	 *
+	 * @see SQLProjection
 	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static Projection sqlGroupProjection(String sql, String groupBy, String[] columnAliases, Type[] types) {
 		return new SQLProjection(sql, groupBy, columnAliases, types);
 	}
 
-	/**
-	 * A grouping property value
-	 */
-	public static PropertyProjection groupProperty(String propertyName) {
-		return new PropertyProjection(propertyName, true);
-	}
-	
-	/**
-	 * A projected property value
-	 */
-	public static PropertyProjection property(String propertyName) {
-		return new PropertyProjection(propertyName);
-	}
-	
-	/**
-	 * A projected identifier value
-	 */
-	public static IdentifierProjection id() {
-		return new IdentifierProjection();
-	}
-	
-	/**
-	 * Assign an alias to a projection, by wrapping it
-	 */
-	public static Projection alias(Projection projection, String alias) {
-		return new AliasedProjection(projection, alias);
+	private Projections() {
+		//cannot be instantiated
 	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/PropertiesSubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertiesSubqueryExpression.java
index 69df2e56e9..cb1b1b4089 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertiesSubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertiesSubqueryExpression.java
@@ -1,28 +1,52 @@
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
 package org.hibernate.criterion;
 
 import org.hibernate.Criteria;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * A comparison between several properties value in the outer query and the result of a multicolumn subquery.
+ *
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public class PropertiesSubqueryExpression extends SubqueryExpression {
 	private final String[] propertyNames;
 
 	protected PropertiesSubqueryExpression(String[] propertyNames, String op, DetachedCriteria dc) {
 		super( op, null, dc );
 		this.propertyNames = propertyNames;
 	}
 
 	@Override
 	protected String toLeftSqlString(Criteria criteria, CriteriaQuery outerQuery) {
-		StringBuilder left = new StringBuilder( "(" );
+		final StringBuilder left = new StringBuilder( "(" );
 		final String[] sqlColumnNames = new String[propertyNames.length];
 		for ( int i = 0; i < sqlColumnNames.length; ++i ) {
 			sqlColumnNames[i] = outerQuery.getColumn( criteria, propertyNames[i] );
 		}
 		left.append( StringHelper.join( ", ", sqlColumnNames ) );
 		return left.append( ")" ).toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Property.java b/hibernate-core/src/main/java/org/hibernate/criterion/Property.java
index ef50ae62d8..129ebd21d9 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Property.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Property.java
@@ -1,265 +1,758 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import java.util.Collection;
 
 /**
  * A factory for property-specific criterion and projection instances
+ *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public class Property extends PropertyProjection {
-	//private String propertyName;
+	/**
+	 * Factory for Property instances.
+	 *
+	 * @param propertyName The name of the property.
+	 *
+	 * @return The Property instance
+	 */
+	public static Property forName(String propertyName) {
+		return new Property( propertyName );
+	}
+
+	/**
+	 * Constructs a Property.  non-private to allow subclassing.
+	 *
+	 * @param propertyName The property name.
+	 */
 	protected Property(String propertyName) {
-		super(propertyName);
+		super( propertyName );
 	}
 
+	/**
+	 * Creates a BETWEEN restriction for this property between the given min and max
+	 *
+	 * @param min The minimum
+	 * @param max The maximum
+	 *
+	 * @return The BETWEEN restriction
+	 *
+	 * @see Restrictions#between(String, Object, Object)
+	 */
 	public Criterion between(Object min, Object max) {
-		return Restrictions.between(getPropertyName(), min, max);
+		return Restrictions.between( getPropertyName(), min, max );
 	}
 
+	/**
+	 * Creates an IN restriction for this property based on the given list of literals
+	 *
+	 * @param values The literal values
+	 *
+	 * @return The IN restriction
+	 *
+	 * @see Restrictions#in(String, Collection)
+	 */
 	public Criterion in(Collection values) {
-		return Restrictions.in(getPropertyName(), values);
+		return Restrictions.in( getPropertyName(), values );
 	}
 
+	/**
+	 * Creates an IN restriction for this property based on the given list of literals
+	 *
+	 * @param values The literal values
+	 *
+	 * @return The IN restriction
+	 *
+	 * @see Restrictions#in(String, Object[])
+	 */
 	public Criterion in(Object[] values) {
-		return Restrictions.in(getPropertyName(), values);
+		return Restrictions.in( getPropertyName(), values );
 	}
 
+	/**
+	 * Creates a LIKE restriction for this property
+	 *
+	 * @param value The value to like compare with
+	 *
+	 * @return The LIKE restriction
+	 *
+	 * @see Restrictions#like(String, Object)
+	 */
 	public SimpleExpression like(Object value) {
-		return Restrictions.like(getPropertyName(), value);
+		return Restrictions.like( getPropertyName(), value );
 	}
 
+	/**
+	 * Creates a LIKE restriction for this property
+	 *
+	 * @param value The value to like compare with
+	 * @param matchMode The match mode to apply to the LIKE
+	 *
+	 * @return The LIKE restriction
+	 *
+	 * @see Restrictions#like(String, String, MatchMode)
+	 */
 	public SimpleExpression like(String value, MatchMode matchMode) {
-		return Restrictions.like(getPropertyName(), value, matchMode);
+		return Restrictions.like( getPropertyName(), value, matchMode );
 	}
 
+	/**
+	 * Creates an equality restriction.
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The equality restriction.
+	 *
+	 * @see Restrictions#eq(String, Object)
+	 */
 	public SimpleExpression eq(Object value) {
-		return Restrictions.eq(getPropertyName(), value);
+		return Restrictions.eq( getPropertyName(), value );
 	}
 
+	/**
+	 * Creates an equality restriction capable of also rendering as IS NULL if the given value is {@code null}
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The equality restriction.
+	 *
+	 * @see Restrictions#eqOrIsNull(String, Object)
+	 * @see #eq
+	 * @see #isNull
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion eqOrIsNull(Object value) {
-		return Restrictions.eqOrIsNull(getPropertyName(), value);
+		return Restrictions.eqOrIsNull( getPropertyName(), value );
 	}
 
+	/**
+	 * Creates an non-equality restriction.
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The non-equality restriction.
+	 *
+	 * @see Restrictions#ne(String, Object)
+	 */
 	public SimpleExpression ne(Object value) {
-		return Restrictions.ne(getPropertyName(), value);
+		return Restrictions.ne( getPropertyName(), value );
 	}
 
+	/**
+	 * Creates an non-equality restriction capable of also rendering as IS NOT NULL if the given value is {@code null}
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The non-equality restriction.
+	 *
+	 * @see Restrictions#neOrIsNotNull(String, Object)
+	 * @see #ne
+	 * @see #isNotNull
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion neOrIsNotNull(Object value) {
-		return Restrictions.neOrIsNotNull(getPropertyName(), value);
+		return Restrictions.neOrIsNotNull( getPropertyName(), value );
 	}
 
+	/**
+	 * Create a greater-than restriction based on this property
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The greater-than restriction
+	 *
+	 * @see Restrictions#gt(String, Object)
+	 */
 	public SimpleExpression gt(Object value) {
-		return Restrictions.gt(getPropertyName(), value);
+		return Restrictions.gt( getPropertyName(), value );
 	}
 
+	/**
+	 * Create a less-than restriction based on this property
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The less-than restriction
+	 *
+	 * @see Restrictions#lt(String, Object)
+	 */
 	public SimpleExpression lt(Object value) {
-		return Restrictions.lt(getPropertyName(), value);
+		return Restrictions.lt( getPropertyName(), value );
 	}
 
+	/**
+	 * Create a less-than-or-equal-to restriction based on this property
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The less-than-or-equal-to restriction
+	 *
+	 * @see Restrictions#le(String, Object)
+	 */
 	public SimpleExpression le(Object value) {
-		return Restrictions.le(getPropertyName(), value);
+		return Restrictions.le( getPropertyName(), value );
 	}
 
+	/**
+	 * Create a greater-than-or-equal-to restriction based on this property
+	 *
+	 * @param value The value to check against
+	 *
+	 * @return The greater-than-or-equal-to restriction
+	 *
+	 * @see Restrictions#ge(String, Object)
+	 */
 	public SimpleExpression ge(Object value) {
-		return Restrictions.ge(getPropertyName(), value);
+		return Restrictions.ge( getPropertyName(), value );
 	}
 
+	/**
+	 * Creates an equality restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#eqProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public PropertyExpression eqProperty(Property other) {
 		return Restrictions.eqProperty( getPropertyName(), other.getPropertyName() );
 	}
 
+	/**
+	 * Creates an equality restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#eqProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
+	public PropertyExpression eqProperty(String other) {
+		return Restrictions.eqProperty( getPropertyName(), other );
+	}
+
+	/**
+	 * Creates an non-equality restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#neProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public PropertyExpression neProperty(Property other) {
 		return Restrictions.neProperty( getPropertyName(), other.getPropertyName() );
 	}
-	
-	public PropertyExpression leProperty(Property other) {
-		return Restrictions.leProperty( getPropertyName(), other.getPropertyName() );
-	}
 
-	public PropertyExpression geProperty(Property other) {
-		return Restrictions.geProperty( getPropertyName(), other.getPropertyName() );
-	}
-	
-	public PropertyExpression ltProperty(Property other) {
-		return Restrictions.ltProperty( getPropertyName(), other.getPropertyName() );
+	/**
+	 * Creates an non-equality restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#neProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
+	public PropertyExpression neProperty(String other) {
+		return Restrictions.neProperty( getPropertyName(), other );
 	}
 
-	public PropertyExpression gtProperty(Property other) {
-		return Restrictions.gtProperty( getPropertyName(), other.getPropertyName() );
-	}
-	
-	public PropertyExpression eqProperty(String other) {
-		return Restrictions.eqProperty( getPropertyName(), other );
+	/**
+	 * Creates an less-than-or-equal-to restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#leProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
+	public PropertyExpression leProperty(Property other) {
+		return Restrictions.leProperty( getPropertyName(), other.getPropertyName() );
 	}
 
-	public PropertyExpression neProperty(String other) {
-		return Restrictions.neProperty( getPropertyName(), other );
-	}
-	
+	/**
+	 * Creates an less-than-or-equal-to restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#leProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public PropertyExpression leProperty(String other) {
 		return Restrictions.leProperty( getPropertyName(), other );
 	}
 
+	/**
+	 * Creates an greater-than-or-equal-to restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#geProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
+	public PropertyExpression geProperty(Property other) {
+		return Restrictions.geProperty( getPropertyName(), other.getPropertyName() );
+	}
+
+	/**
+	 * Creates an greater-than-or-equal-to restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#geProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public PropertyExpression geProperty(String other) {
 		return Restrictions.geProperty( getPropertyName(), other );
 	}
-	
+
+	/**
+	 * Creates an less-than restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#ltProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
+	public PropertyExpression ltProperty(Property other) {
+		return Restrictions.ltProperty( getPropertyName(), other.getPropertyName() );
+	}
+
+	/**
+	 * Creates an less-than restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#ltProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public PropertyExpression ltProperty(String other) {
 		return Restrictions.ltProperty( getPropertyName(), other );
 	}
 
+	/**
+	 * Creates an greater-than restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#geProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
+	public PropertyExpression gtProperty(Property other) {
+		return Restrictions.gtProperty( getPropertyName(), other.getPropertyName() );
+	}
+
+	/**
+	 * Creates an greater-than restriction between 2 properties
+	 *
+	 * @param other The other property to compare against
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#geProperty(String, String)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public PropertyExpression gtProperty(String other) {
 		return Restrictions.gtProperty( getPropertyName(), other );
 	}
-	
+
+	/**
+	 * Creates a NULL restriction
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#isNull(String)
+	 */
 	public Criterion isNull() {
-		return Restrictions.isNull(getPropertyName());
+		return Restrictions.isNull( getPropertyName() );
 	}
 
+	/**
+	 * Creates a NOT NULL restriction
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#isNotNull(String)
+	 */
 	public Criterion isNotNull() {
-		return Restrictions.isNotNull(getPropertyName());
+		return Restrictions.isNotNull( getPropertyName() );
 	}
 
+	/**
+	 * Creates a restriction to check that a collection is empty
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#isEmpty(String)
+	 */
 	public Criterion isEmpty() {
-		return Restrictions.isEmpty(getPropertyName());
+		return Restrictions.isEmpty( getPropertyName() );
 	}
 
+	/**
+	 * Creates a restriction to check that a collection is not empty
+	 *
+	 * @return The restriction
+	 *
+	 * @see Restrictions#isNotEmpty(String)
+	 */
 	public Criterion isNotEmpty() {
-		return Restrictions.isNotEmpty(getPropertyName());
+		return Restrictions.isNotEmpty( getPropertyName() );
 	}
-	
+
+	/**
+	 * Creates a property count projection
+	 *
+	 * @return The projection
+	 *
+	 * @see Projections#count
+	 */
 	public CountProjection count() {
-		return Projections.count(getPropertyName());
+		return Projections.count( getPropertyName() );
 	}
-	
+
+	/**
+	 * Creates a property max projection
+	 *
+	 * @return The projection
+	 *
+	 * @see Projections#max
+	 */
 	public AggregateProjection max() {
-		return Projections.max(getPropertyName());
+		return Projections.max( getPropertyName() );
 	}
 
+	/**
+	 * Creates a property min projection
+	 *
+	 * @return The projection
+	 *
+	 * @see Projections#min
+	 */
 	public AggregateProjection min() {
-		return Projections.min(getPropertyName());
+		return Projections.min( getPropertyName() );
 	}
 
+	/**
+	 * Creates a property avg projection
+	 *
+	 * @return The projection
+	 *
+	 * @see Projections#avg
+	 */
 	public AggregateProjection avg() {
-		return Projections.avg(getPropertyName());
+		return Projections.avg( getPropertyName() );
 	}
-	
-	/*public PropertyProjection project() {
-		return Projections.property(getPropertyName());
-	}*/
 
+	/**
+	 * Creates a projection for this property as a group expression
+	 *
+	 * @return The group projection
+	 *
+	 * @see Projections#groupProperty
+	 */
 	public PropertyProjection group() {
-		return Projections.groupProperty(getPropertyName());
+		return Projections.groupProperty( getPropertyName() );
 	}
-	
+
+	/**
+	 * Creates an ascending ordering for this property
+	 *
+	 * @return The order
+	 */
 	public Order asc() {
-		return Order.asc(getPropertyName());
+		return Order.asc( getPropertyName() );
 	}
 
+	/**
+	 * Creates a descending ordering for this property
+	 *
+	 * @return The order
+	 */
 	public Order desc() {
-		return Order.desc(getPropertyName());
-	}
-
-	public static Property forName(String propertyName) {
-		return new Property(propertyName);
+		return Order.desc( getPropertyName() );
 	}
 	
 	/**
-	 * Get a component attribute of this property
+	 * Get a component attribute of this property.
+	 *
+	 * @param propertyName The sub property name
+	 *
+	 * @return The property
 	 */
 	public Property getProperty(String propertyName) {
 		return forName( getPropertyName() + '.' + propertyName );
 	}
-	
+
+	/**
+	 * Creates a sub-query equality expression for this property
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyEq(String, DetachedCriteria)
+	 */
 	public Criterion eq(DetachedCriteria subselect) {
 		return Subqueries.propertyEq( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a sub-query non-equality expression for this property
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyNe(String, DetachedCriteria)
+	 */
 	public Criterion ne(DetachedCriteria subselect) {
 		return Subqueries.propertyNe( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a sub-query less-than expression for this property
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyLt(String, DetachedCriteria)
+	 */
 	public Criterion lt(DetachedCriteria subselect) {
 		return Subqueries.propertyLt( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a sub-query less-than-or-equal-to expression for this property
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyLe(String, DetachedCriteria)
+	 */
 	public Criterion le(DetachedCriteria subselect) {
 		return Subqueries.propertyLe( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a sub-query greater-than expression for this property
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyGt(String, DetachedCriteria)
+	 */
 	public Criterion gt(DetachedCriteria subselect) {
 		return Subqueries.propertyGt( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a sub-query greater-than-or-equal-to expression for this property
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyGe(String, DetachedCriteria)
+	 */
 	public Criterion ge(DetachedCriteria subselect) {
 		return Subqueries.propertyGe( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a sub-query NOT IN expression for this property.  I.e., {@code [prop] NOT IN [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyNotIn(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion notIn(DetachedCriteria subselect) {
 		return Subqueries.propertyNotIn( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a sub-query IN expression for this property.  I.e., {@code [prop] IN [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyIn(String, DetachedCriteria)
+	 */
 	public Criterion in(DetachedCriteria subselect) {
 		return Subqueries.propertyIn( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a equals-all sub-query expression for this property.  I.e., {@code [prop] = ALL [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyEqAll(String, DetachedCriteria)
+	 */
 	public Criterion eqAll(DetachedCriteria subselect) {
 		return Subqueries.propertyEqAll( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a greater-than-all sub-query expression for this property.  I.e., {@code [prop] > ALL [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyGtAll(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion gtAll(DetachedCriteria subselect) {
 		return Subqueries.propertyGtAll( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a less-than-all sub-query expression for this property.  I.e., {@code [prop] < ALL [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyLtAll(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion ltAll(DetachedCriteria subselect) {
 		return Subqueries.propertyLtAll( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a less-than-or-equal-to-all sub-query expression for this property.  I.e., {@code [prop] <= ALL [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyLeAll(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion leAll(DetachedCriteria subselect) {
 		return Subqueries.propertyLeAll( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a greater-than-or-equal-to-all sub-query expression for this property.  I.e., {@code [prop] >= ALL [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyGeAll(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion geAll(DetachedCriteria subselect) {
 		return Subqueries.propertyGeAll( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a greater-than-some sub-query expression for this property.  I.e., {@code [prop] > SOME [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyGtSome(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion gtSome(DetachedCriteria subselect) {
 		return Subqueries.propertyGtSome( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a less-than-some sub-query expression for this property.  I.e., {@code [prop] < SOME [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyLtSome(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion ltSome(DetachedCriteria subselect) {
 		return Subqueries.propertyLtSome( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a less-than-or-equal-to-some sub-query expression for this property.  I.e., {@code [prop] <= SOME [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyLeSome(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion leSome(DetachedCriteria subselect) {
 		return Subqueries.propertyLeSome( getPropertyName(), subselect );
 	}
 
+	/**
+	 * Creates a greater-than-or-equal-to-some sub-query expression for this property.  I.e., {@code [prop] >= SOME [subquery]}
+	 *
+	 * @param subselect The sub-query
+	 *
+	 * @return The expression
+	 *
+	 * @see Subqueries#propertyGeSome(String, DetachedCriteria)
+	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public Criterion geSome(DetachedCriteria subselect) {
 		return Subqueries.propertyGeSome( getPropertyName(), subselect );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
index 359696b5d3..c9b5701168 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
@@ -1,75 +1,77 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * superclass for comparisons between two properties (with SQL binary operators)
+ *
  * @author Gavin King
  */
 public class PropertyExpression implements Criterion {
+	private static final TypedValue[] NO_TYPED_VALUES = new TypedValue[0];
 
 	private final String propertyName;
 	private final String otherPropertyName;
 	private final String op;
 
-	private static final TypedValue[] NO_TYPED_VALUES = new TypedValue[0];
-
 	protected PropertyExpression(String propertyName, String otherPropertyName, String op) {
 		this.propertyName = propertyName;
 		this.otherPropertyName = otherPropertyName;
 		this.op = op;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		String[] xcols = criteriaQuery.findColumns(propertyName, criteria);
-		String[] ycols = criteriaQuery.findColumns(otherPropertyName, criteria);
-		String result = StringHelper.join(
-			" and ",
-			StringHelper.add( xcols, getOp(), ycols )
-		);
-		if (xcols.length>1) result = '(' + result + ')';
-		return result;
-		//TODO: get SQL rendering out of this package!
+	public String getOp() {
+		return op;
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final String[] lhsColumns = criteriaQuery.findColumns( propertyName, criteria );
+		final String[] rhsColumns = criteriaQuery.findColumns( otherPropertyName, criteria );
+
+		final String[] comparisons = StringHelper.add( lhsColumns, getOp(), rhsColumns );
+		if ( comparisons.length > 1 ) {
+			return '(' + StringHelper.join( " and ", comparisons ) + ')';
+		}
+		else {
+			return comparisons[0];
+		}
+	}
+
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
 		return NO_TYPED_VALUES;
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + getOp() + otherPropertyName;
 	}
 
-	public String getOp() {
-		return op;
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
index 00fe0a9324..44832d7928 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertyProjection.java
@@ -1,91 +1,95 @@
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
 package org.hibernate.criterion;
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * A property value, or grouped property value
+ *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public class PropertyProjection extends SimpleProjection {
-
 	private String propertyName;
 	private boolean grouped;
-	
+
 	protected PropertyProjection(String prop, boolean grouped) {
 		this.propertyName = prop;
 		this.grouped = grouped;
 	}
-	
+
 	protected PropertyProjection(String prop) {
 		this(prop, false);
 	}
 
-	public String getPropertyName() {
-		return propertyName;
+	@Override
+	public boolean isGrouped() {
+		return grouped;
 	}
-	
-	public String toString() {
+
+	public String getPropertyName() {
 		return propertyName;
 	}
 
-	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		return new Type[] { criteriaQuery.getType(criteria, propertyName) };
+	@Override
+	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		return new Type[] { criteriaQuery.getType( criteria, propertyName ) };
 	}
 
-	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		StringBuilder buf = new StringBuilder();
-		String[] cols = criteriaQuery.getColumns( propertyName, criteria );
+	@Override
+	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
+		final StringBuilder buf = new StringBuilder();
+		final String[] cols = criteriaQuery.getColumns( propertyName, criteria );
 		for ( int i=0; i<cols.length; i++ ) {
 			buf.append( cols[i] )
-				.append(" as y")
-				.append(position + i)
-				.append('_');
-			if (i < cols.length -1)
-			   buf.append(", ");
+					.append( " as y" )
+					.append( position + i )
+					.append( '_' );
+			if (i < cols.length -1) {
+				buf.append( ", " );
+			}
 		}
 		return buf.toString();
 	}
 
-	public boolean isGrouped() {
-		return grouped;
-	}
-	
-	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		if (!grouped) {
-			return super.toGroupSqlString(criteria, criteriaQuery);
+	@Override
+	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		if ( !grouped ) {
+			return super.toGroupSqlString( criteria, criteriaQuery );
 		}
 		else {
 			return StringHelper.join( ", ", criteriaQuery.getColumns( propertyName, criteria ) );
 		}
 	}
 
+	@Override
+	public String toString() {
+		return propertyName;
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java
index 71ecdf3289..c0d5005e7c 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/PropertySubqueryExpression.java
@@ -1,45 +1,47 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
 /**
- * A comparison between a property value in the outer query and the
- * result of a subquery
+ * A comparison between a property value in the outer query and the result of a subquery
+ *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public class PropertySubqueryExpression extends SubqueryExpression {
 	private String propertyName;
 
 	protected PropertySubqueryExpression(String propertyName, String op, String quantifier, DetachedCriteria dc) {
-		super(op, quantifier, dc);
+		super( op, quantifier, dc );
 		this.propertyName = propertyName;
 	}
 
+	@Override
 	protected String toLeftSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
-		return criteriaQuery.getColumn(criteria, propertyName);
+		return criteriaQuery.getColumn( criteria, propertyName );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java b/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
index b9fdbfa386..3e1d47eb10 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Restrictions.java
@@ -1,463 +1,697 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 
 import java.util.Collection;
-import java.util.Iterator;
 import java.util.Map;
 
-import org.hibernate.Session;
-import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.type.Type;
 
 /**
  * The <tt>criterion</tt> package may be used by applications as a framework for building
  * new kinds of <tt>Criterion</tt>. However, it is intended that most applications will
  * simply use the built-in criterion types via the static factory methods of this class.
  *
- * @see org.hibernate.Criteria
- * @see Projections factory methods for <tt>Projection</tt> instances
+ * See also the {@link Projections} factory methods for generating {@link Projection} instances
+ *
  * @author Gavin King
+ * @author Steve Ebersole
+ *
+ * @see org.hibernate.Criteria
  */
 public class Restrictions {
-
-	Restrictions() {
-		//cannot be instantiated
-	}
-
 	/**
 	 * Apply an "equal" constraint to the identifier property
-	 * @param value
+	 *
+	 * @param value The value to use in comparison
+	 *
 	 * @return Criterion
+	 *
+	 * @see IdentifierEqExpression
 	 */
 	public static Criterion idEq(Object value) {
-		return new IdentifierEqExpression(value);
+		return new IdentifierEqExpression( value );
 	}
 	/**
 	 * Apply an "equal" constraint to the named property
-	 * @param propertyName
-	 * @param value
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
 	 * @return SimpleExpression
+	 *
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression eq(String propertyName, Object value) {
-		return new SimpleExpression(propertyName, value, "=");
+		return new SimpleExpression( propertyName, value, "=" );
 	}
+
 	/**
 	 * Apply an "equal" constraint to the named property.  If the value
 	 * is null, instead apply "is null".
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see #eq
+	 * @see #isNull
 	 */
 	public static Criterion eqOrIsNull(String propertyName, Object value) {
-		if (value == null) {
-			return isNull(propertyName);
-		}
-		return new SimpleExpression(propertyName, value, "=");
+		return value == null
+				? isNull( propertyName )
+				: eq( propertyName, value );
 	}
+
 	/**
 	 * Apply a "not equal" constraint to the named property
-	 * @param propertyName
-	 * @param value
-	 * @return SimpleExpression
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression ne(String propertyName, Object value) {
-		return new SimpleExpression(propertyName, value, "<>");
+		return new SimpleExpression( propertyName, value, "<>" );
 	}
+
 	/**
 	 * Apply a "not equal" constraint to the named property.  If the value
 	 * is null, instead apply "is not null".
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see #ne
+	 * @see #isNotNull
 	 */
 	public static Criterion neOrIsNotNull(String propertyName, Object value) {
-		if (value == null) {
-			return isNotNull(propertyName);
-		}
-		return new SimpleExpression(propertyName, value, "<>");
+		return value == null
+				? isNotNull( propertyName )
+				: ne( propertyName, value );
 	}
+
 	/**
 	 * Apply a "like" constraint to the named property
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression like(String propertyName, Object value) {
-		return new SimpleExpression(propertyName, value, " like ");
+		// todo : update this to use LikeExpression
+		return new SimpleExpression( propertyName, value, " like " );
 	}
+
 	/**
-	 * Apply a "like" constraint to the named property
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 * Apply a "like" constraint to the named property using the provided match mode
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 * @param matchMode The match mode to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression like(String propertyName, String value, MatchMode matchMode) {
-		return new SimpleExpression(propertyName, matchMode.toMatchString(value), " like " );
+		// todo : update this to use LikeExpression
+		return new SimpleExpression( propertyName, matchMode.toMatchString( value ), " like " );
 	}
 
 	/**
-	 * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
-	 * operator
+	 * A case-insensitive "like" (similar to Postgres <tt>ilike</tt> operator)
 	 *
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see LikeExpression
 	 */
-	public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
-		return new LikeExpression(propertyName, value, matchMode, null, true);
+	public static Criterion ilike(String propertyName, Object value) {
+		if ( value == null ) {
+			throw new IllegalArgumentException( "Comparison value passed to ilike cannot be null" );
+		}
+		return ilike( propertyName, value.toString(), MatchMode.EXACT );
 	}
+
 	/**
-	 * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
-	 * operator
+	 * A case-insensitive "like" (similar to Postgres <tt>ilike</tt> operator) using the provided match mode
 	 *
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 * @param matchMode The match mode to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see LikeExpression
 	 */
-	public static Criterion ilike(String propertyName, Object value) {
+	public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
 		if ( value == null ) {
 			throw new IllegalArgumentException( "Comparison value passed to ilike cannot be null" );
 		}
-		return ilike( propertyName, value.toString(), MatchMode.EXACT );
+		return new LikeExpression( propertyName, value, matchMode, null, true );
 	}
 
 	/**
 	 * Apply a "greater than" constraint to the named property
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression gt(String propertyName, Object value) {
-		return new SimpleExpression(propertyName, value, ">");
+		return new SimpleExpression( propertyName, value, ">" );
 	}
+
 	/**
 	 * Apply a "less than" constraint to the named property
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression lt(String propertyName, Object value) {
-		return new SimpleExpression(propertyName, value, "<");
+		return new SimpleExpression( propertyName, value, "<" );
 	}
+
 	/**
 	 * Apply a "less than or equal" constraint to the named property
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression le(String propertyName, Object value) {
-		return new SimpleExpression(propertyName, value, "<=");
+		return new SimpleExpression( propertyName, value, "<=" );
 	}
 	/**
 	 * Apply a "greater than or equal" constraint to the named property
-	 * @param propertyName
-	 * @param value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param value The value to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleExpression
 	 */
 	public static SimpleExpression ge(String propertyName, Object value) {
-		return new SimpleExpression(propertyName, value, ">=");
+		return new SimpleExpression( propertyName, value, ">=" );
 	}
+
 	/**
 	 * Apply a "between" constraint to the named property
-	 * @param propertyName
-	 * @param lo value
-	 * @param hi value
-	 * @return Criterion
+	 *
+	 * @param propertyName The name of the property
+	 * @param lo The low value
+	 * @param hi The high value
+	 *
+	 * @return The Criterion
+	 *
+	 * @see BetweenExpression
 	 */
 	public static Criterion between(String propertyName, Object lo, Object hi) {
-		return new BetweenExpression(propertyName, lo, hi);
+		return new BetweenExpression( propertyName, lo, hi );
 	}
+
 	/**
-	 * Apply an "in" constraint to the named property
-	 * @param propertyName
-	 * @param values
-	 * @return Criterion
+	 * Apply an "in" constraint to the named property.
+	 *
+	 * @param propertyName The name of the property
+	 * @param values The literal values to use in the IN restriction
+	 *
+	 * @return The Criterion
+	 *
+	 * @see InExpression
 	 */
 	public static Criterion in(String propertyName, Object[] values) {
-		return new InExpression(propertyName, values);
+		return new InExpression( propertyName, values );
 	}
+
 	/**
-	 * Apply an "in" constraint to the named property
-	 * @param propertyName
-	 * @param values
-	 * @return Criterion
+	 * Apply an "in" constraint to the named property.
+	 *
+	 * @param propertyName The name of the property
+	 * @param values The literal values to use in the IN restriction
+	 *
+	 * @return The Criterion
+	 *
+	 * @see InExpression
 	 */
 	public static Criterion in(String propertyName, Collection values) {
 		return new InExpression( propertyName, values.toArray() );
 	}
+
 	/**
 	 * Apply an "is null" constraint to the named property
+	 *
+	 * @param propertyName The name of the property
+	 *
 	 * @return Criterion
+	 *
+	 * @see NullExpression
 	 */
 	public static Criterion isNull(String propertyName) {
-		return new NullExpression(propertyName);
+		return new NullExpression( propertyName );
 	}
+
+	/**
+	 * Apply an "is not null" constraint to the named property
+	 *
+	 * @param propertyName The property name
+	 *
+	 * @return The Criterion
+	 *
+	 * @see NotNullExpression
+	 */
+	public static Criterion isNotNull(String propertyName) {
+		return new NotNullExpression( propertyName );
+	}
+
 	/**
 	 * Apply an "equal" constraint to two properties
+	 *
+	 * @param propertyName One property name
+	 * @param otherPropertyName The other property name
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertyExpression
 	 */
 	public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
-		return new PropertyExpression(propertyName, otherPropertyName, "=");
+		return new PropertyExpression( propertyName, otherPropertyName, "=" );
 	}
+
 	/**
 	 * Apply a "not equal" constraint to two properties
+	 *
+	 * @param propertyName One property name
+	 * @param otherPropertyName The other property name
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertyExpression
 	 */
 	public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
-		return new PropertyExpression(propertyName, otherPropertyName, "<>");
+		return new PropertyExpression( propertyName, otherPropertyName, "<>" );
 	}
+
 	/**
 	 * Apply a "less than" constraint to two properties
+	 *
+	 * @param propertyName One property name
+	 * @param otherPropertyName The other property name
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertyExpression
 	 */
 	public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
-		return new PropertyExpression(propertyName, otherPropertyName, "<");
+		return new PropertyExpression( propertyName, otherPropertyName, "<" );
 	}
+
 	/**
 	 * Apply a "less than or equal" constraint to two properties
+	 *
+	 * @param propertyName One property name
+	 * @param otherPropertyName The other property name
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertyExpression
 	 */
 	public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
-		return new PropertyExpression(propertyName, otherPropertyName, "<=");
+		return new PropertyExpression( propertyName, otherPropertyName, "<=" );
 	}
+
 	/**
 	 * Apply a "greater than" constraint to two properties
+	 *
+	 * @param propertyName One property name
+	 * @param otherPropertyName The other property name
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertyExpression
 	 */
 	public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
-		return new PropertyExpression(propertyName, otherPropertyName, ">");
+		return new PropertyExpression( propertyName, otherPropertyName, ">" );
 	}
+
 	/**
 	 * Apply a "greater than or equal" constraint to two properties
+	 *
+	 * @param propertyName One property name
+	 * @param otherPropertyName The other property name
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertyExpression
 	 */
 	public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
-		return new PropertyExpression(propertyName, otherPropertyName, ">=");
-	}
-	/**
-	 * Apply an "is not null" constraint to the named property
-	 * @return Criterion
-	 */
-	public static Criterion isNotNull(String propertyName) {
-		return new NotNullExpression(propertyName);
+		return new PropertyExpression( propertyName, otherPropertyName, ">=" );
 	}
+
 	/**
 	 * Return the conjuction of two expressions
 	 *
-	 * @param lhs
-	 * @param rhs
-	 * @return Criterion
+	 * @param lhs One expression
+	 * @param rhs The other expression
+	 *
+	 * @return The Criterion
 	 */
 	public static LogicalExpression and(Criterion lhs, Criterion rhs) {
 		return new LogicalExpression(lhs, rhs, "and");
 	}
 	/**
 	 * Return the conjuction of multiple expressions
 	 *
 	 * @param predicates The predicates making up the initial junction
 	 *
 	 * @return The conjunction
 	 */
 	public static Conjunction and(Criterion... predicates) {
-		Conjunction conjunction = conjunction();
-		if ( predicates != null ) {
-			for ( Criterion predicate : predicates ) {
-				conjunction.add( predicate );
-			}
-		}
-		return conjunction;
+		return conjunction( predicates );
 	}
+
 	/**
 	 * Return the disjuction of two expressions
 	 *
-	 * @param lhs
-	 * @param rhs
-	 * @return Criterion
+	 * @param lhs One expression
+	 * @param rhs The other expression
+	 *
+	 * @return The Criterion
 	 */
 	public static LogicalExpression or(Criterion lhs, Criterion rhs) {
-		return new LogicalExpression(lhs, rhs, "or");
+		return new LogicalExpression( lhs, rhs, "or" );
 	}
+
 	/**
 	 * Return the disjuction of multiple expressions
 	 *
 	 * @param predicates The predicates making up the initial junction
 	 *
 	 * @return The conjunction
 	 */
 	public static Disjunction or(Criterion... predicates) {
-		Disjunction disjunction = disjunction();
-		if ( predicates != null ) {
-			for ( Criterion predicate : predicates ) {
-				disjunction.add( predicate );
-			}
-		}
-		return disjunction;
+		return disjunction( predicates );
 	}
+
 	/**
 	 * Return the negation of an expression
 	 *
-	 * @param expression
+	 * @param expression The expression to be negated
+	 *
 	 * @return Criterion
+	 *
+	 * @see NotExpression
 	 */
 	public static Criterion not(Criterion expression) {
-		return new NotExpression(expression);
+		return new NotExpression( expression );
 	}
+
 	/**
-	 * Apply a constraint expressed in SQL, with the given JDBC
-	 * parameters. Any occurrences of <tt>{alias}</tt> will be
+	 * Create a restriction expressed in SQL with JDBC parameters.  Any occurrences of <tt>{alias}</tt> will be
 	 * replaced by the table alias.
 	 *
-	 * @param sql
-	 * @param values
-	 * @param types
-	 * @return Criterion
+	 * @param sql The SQL restriction
+	 * @param values The parameter values
+	 * @param types The parameter types
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SQLCriterion
 	 */
 	public static Criterion sqlRestriction(String sql, Object[] values, Type[] types) {
-		return new SQLCriterion(sql, values, types);
+		return new SQLCriterion( sql, values, types );
 	}
+
 	/**
-	 * Apply a constraint expressed in SQL, with the given JDBC
-	 * parameter. Any occurrences of <tt>{alias}</tt> will be replaced
-	 * by the table alias.
+	 * Create a restriction expressed in SQL with one JDBC parameter.  Any occurrences of <tt>{alias}</tt> will be
+	 * replaced by the table alias.
 	 *
-	 * @param sql
-	 * @param value
-	 * @param type
-	 * @return Criterion
+	 * @param sql The SQL restriction
+	 * @param value The parameter value
+	 * @param type The parameter type
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SQLCriterion
 	 */
 	public static Criterion sqlRestriction(String sql, Object value, Type type) {
-		return new SQLCriterion(sql, new Object[] { value }, new Type[] { type } );
+		return new SQLCriterion( sql, value, type );
 	}
+
 	/**
-	 * Apply a constraint expressed in SQL. Any occurrences of <tt>{alias}</tt>
-	 * will be replaced by the table alias.
+	 * Apply a constraint expressed in SQL with no JDBC parameters.  Any occurrences of <tt>{alias}</tt> will be
+	 * replaced by the table alias.
 	 *
-	 * @param sql
-	 * @return Criterion
+	 * @param sql The SQL restriction
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SQLCriterion
 	 */
 	public static Criterion sqlRestriction(String sql) {
-		return new SQLCriterion(sql, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY);
+		return new SQLCriterion( sql );
 	}
 
 	/**
-	 * Group expressions together in a single conjunction (A and B and C...)
+	 * Group expressions together in a single conjunction (A and B and C...).
+	 *
+	 * This form creates an empty conjunction.  See {@link Conjunction#add(Criterion)}
 	 *
 	 * @return Conjunction
 	 */
 	public static Conjunction conjunction() {
 		return new Conjunction();
 	}
 
 	/**
-	 * Group expressions together in a single disjunction (A or B or C...)
+	 * Group expressions together in a single conjunction (A and B and C...).
+	 *
+	 * @param conditions The initial set of conditions to put into the Conjunction
+	 *
+	 * @return Conjunction
+	 */
+	public static Conjunction conjunction(Criterion... conditions) {
+		return new Conjunction( conditions );
+	}
+
+	/**
+	 * Group expressions together in a single disjunction (A or B or C...).
+	 *
+	 * This form creates an empty disjunction.  See {@link Disjunction#add(Criterion)}
 	 *
 	 * @return Conjunction
 	 */
 	public static Disjunction disjunction() {
 		return new Disjunction();
 	}
 
 	/**
-	 * Apply an "equals" constraint to each property in the
-	 * key set of a <tt>Map</tt>
+	 * Group expressions together in a single disjunction (A or B or C...).
+	 *
+	 * @param conditions The initial set of conditions to put into the Disjunction
+	 *
+	 * @return Conjunction
+	 */
+	public static Disjunction disjunction(Criterion... conditions) {
+		return new Disjunction( conditions );
+	}
+
+	/**
+	 * Apply an "equals" constraint to each property in the key set of a <tt>Map</tt>
 	 *
 	 * @param propertyNameValues a map from property names to values
+	 *
 	 * @return Criterion
+	 *
+	 * @see Conjunction
 	 */
-	public static Criterion allEq(Map propertyNameValues) {
-		Conjunction conj = conjunction();
-		Iterator iter = propertyNameValues.entrySet().iterator();
-		while ( iter.hasNext() ) {
-			Map.Entry me = (Map.Entry) iter.next();
-			conj.add( eq( (String) me.getKey(), me.getValue() ) );
+	@SuppressWarnings("UnusedDeclaration")
+	public static Criterion allEq(Map<String,?> propertyNameValues) {
+		final Conjunction conj = conjunction();
+
+		for ( Map.Entry<String,?> entry : propertyNameValues.entrySet() ) {
+			conj.add( eq( entry.getKey(), entry.getValue() ) );
 		}
 		return conj;
 	}
 
 	/**
 	 * Constrain a collection valued property to be empty
+	 *
+	 * @param propertyName The name of the collection property
+	 *
+	 * @return The Criterion
+	 *
+	 * @see EmptyExpression
 	 */
 	public static Criterion isEmpty(String propertyName) {
-		return new EmptyExpression(propertyName);
+		return new EmptyExpression( propertyName );
 	}
 
 	/**
 	 * Constrain a collection valued property to be non-empty
+	 *
+	 * @param propertyName The name of the collection property
+	 *
+	 * @return The Criterion
+	 *
+	 * @see NotEmptyExpression
 	 */
 	public static Criterion isNotEmpty(String propertyName) {
-		return new NotEmptyExpression(propertyName);
+		return new NotEmptyExpression( propertyName );
 	}
 
 	/**
 	 * Constrain a collection valued property by size
+	 *
+	 * @param propertyName The name of the collection property
+	 * @param size The size to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SizeExpression
 	 */
 	public static Criterion sizeEq(String propertyName, int size) {
-		return new SizeExpression(propertyName, size, "=");
+		return new SizeExpression( propertyName, size, "=" );
 	}
 
 	/**
 	 * Constrain a collection valued property by size
+	 *
+	 * @param propertyName The name of the collection property
+	 * @param size The size to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SizeExpression
 	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static Criterion sizeNe(String propertyName, int size) {
-		return new SizeExpression(propertyName, size, "<>");
+		return new SizeExpression( propertyName, size, "<>" );
 	}
 
 	/**
 	 * Constrain a collection valued property by size
+	 *
+	 * @param propertyName The name of the collection property
+	 * @param size The size to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SizeExpression
 	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static Criterion sizeGt(String propertyName, int size) {
-		return new SizeExpression(propertyName, size, "<");
+		return new SizeExpression( propertyName, size, "<" );
 	}
 
 	/**
 	 * Constrain a collection valued property by size
+	 *
+	 * @param propertyName The name of the collection property
+	 * @param size The size to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SizeExpression
 	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static Criterion sizeLt(String propertyName, int size) {
-		return new SizeExpression(propertyName, size, ">");
+		return new SizeExpression( propertyName, size, ">" );
 	}
 
 	/**
 	 * Constrain a collection valued property by size
+	 *
+	 * @param propertyName The name of the collection property
+	 * @param size The size to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SizeExpression
 	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static Criterion sizeGe(String propertyName, int size) {
-		return new SizeExpression(propertyName, size, "<=");
+		return new SizeExpression( propertyName, size, "<=" );
 	}
 
 	/**
 	 * Constrain a collection valued property by size
+	 *
+	 * @param propertyName The name of the collection property
+	 * @param size The size to use in comparison
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SizeExpression
 	 */
+	@SuppressWarnings("UnusedDeclaration")
 	public static Criterion sizeLe(String propertyName, int size) {
-		return new SizeExpression(propertyName, size, ">=");
+		return new SizeExpression( propertyName, size, ">=" );
 	}
 
 	/**
 	 * Consider using any of the natural id based loading stuff from session instead, especially in cases
 	 * where the restriction is the full set of natural id values.
 	 *
-	 * @see Session#byNaturalId(Class)
-	 * @see Session#byNaturalId(String)
-	 * @see Session#bySimpleNaturalId(Class)
-	 * @see Session#bySimpleNaturalId(String)
+	 * @return The Criterion
+	 *
+	 * @see NaturalIdentifier
+	 *
+	 * @see org.hibernate.Session#byNaturalId(Class)
+	 * @see org.hibernate.Session#byNaturalId(String)
+	 * @see org.hibernate.Session#bySimpleNaturalId(Class)
+	 * @see org.hibernate.Session#bySimpleNaturalId(String)
 	 */
 	public static NaturalIdentifier naturalId() {
 		return new NaturalIdentifier();
 	}
 
+	protected Restrictions() {
+		// cannot be instantiated, but needs to be protected so Expression can extend it
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/RowCountProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/RowCountProjection.java
index 14027d6588..3ce0dba2b7 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/RowCountProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/RowCountProjection.java
@@ -1,64 +1,66 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2010, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
+
 import java.util.List;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.function.SQLFunction;
+import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.type.Type;
 
 /**
  * A row count
  *
  * @author Gavin King
  */
 public class RowCountProjection extends SimpleProjection {
-	private static List ARGS = java.util.Collections.singletonList( "*" );
-
-	public String toString() {
-		return "count(*)";
-	}
+	private static final List ARGS = java.util.Collections.singletonList( "*" );
 
+	@Override
 	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
-		return new Type[] {
-				getFunction( criteriaQuery ).getReturnType( null, criteriaQuery.getFactory() )
-		};
+		final Type countFunctionReturnType = getFunction( criteriaQuery ).getReturnType( null, criteriaQuery.getFactory() );
+		return new Type[] { countFunctionReturnType };
 	}
 
+	@Override
 	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
-		return getFunction( criteriaQuery ).render( null, ARGS, criteriaQuery.getFactory() )
-				+ " as y" + position + '_';
+		return getFunction( criteriaQuery ).render( null, ARGS, criteriaQuery.getFactory() ) + " as y" + position + '_';
 	}
 
 	protected SQLFunction getFunction(CriteriaQuery criteriaQuery) {
-		SQLFunction function = criteriaQuery.getFactory()
-				.getSqlFunctionRegistry()
-				.findSQLFunction( "count" );
+		final SQLFunctionRegistry sqlFunctionRegistry = criteriaQuery.getFactory().getSqlFunctionRegistry();
+		final SQLFunction function = sqlFunctionRegistry.findSQLFunction( "count" );
 		if ( function == null ) {
 			throw new HibernateException( "Unable to locate count function mapping" );
 		}
 		return function;
 	}
+
+	@Override
+	public String toString() {
+		return "count(*)";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java b/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
index c44bac5ecb..60171d1cd3 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SQLCriterion.java
@@ -1,66 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.EntityMode;
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * A SQL fragment. The string {alias} will be replaced by the
  * alias of the root entity.
  */
 public class SQLCriterion implements Criterion {
-
 	private final String sql;
 	private final TypedValue[] typedValues;
 
-	public String toSqlString(
-		Criteria criteria,
-		CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	protected SQLCriterion(String sql, Object[] values, Type[] types) {
+		this.sql = sql;
+		this.typedValues = new TypedValue[values.length];
+		for ( int i=0; i<typedValues.length; i++ ) {
+			typedValues[i] = new TypedValue( types[i], values[i] );
+		}
+	}
+
+	protected SQLCriterion(String sql, Object value, Type type) {
+		this.sql = sql;
+		this.typedValues = new TypedValue[] { new TypedValue( type, value ) };
+	}
+
+	protected SQLCriterion(String sql) {
+		this.sql = sql;
+		this.typedValues = new TypedValue[0];
+	}
+
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
 		return StringHelper.replace( sql, "{alias}", criteriaQuery.getSQLAlias( criteria ) );
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
 		return typedValues;
 	}
 
+	@Override
 	public String toString() {
 		return sql;
 	}
-
-	protected SQLCriterion(String sql, Object[] values, Type[] types) {
-		this.sql = sql;
-		typedValues = new TypedValue[values.length];
-		for ( int i=0; i<typedValues.length; i++ ) {
-			typedValues[i] = new TypedValue( types[i], values[i], EntityMode.POJO );
-		}
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java
index 22cd680122..8b57ed4c93 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SQLProjection.java
@@ -1,98 +1,99 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.HibernateException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * A SQL fragment. The string {alias} will be replaced by the
  * alias of the root entity.
  */
 public class SQLProjection implements Projection {
-
 	private final String sql;
 	private final String groupBy;
 	private final Type[] types;
 	private String[] aliases;
 	private String[] columnAliases;
 	private boolean grouped;
 
-	public String toSqlString(
-			Criteria criteria, 
-			int loc, 
-			CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		return StringHelper.replace( sql, "{alias}", criteriaQuery.getSQLAlias(criteria) );
+	protected SQLProjection(String sql, String[] columnAliases, Type[] types) {
+		this( sql, null, columnAliases, types );
+	}
+
+	protected SQLProjection(String sql, String groupBy, String[] columnAliases, Type[] types) {
+		this.sql = sql;
+		this.types = types;
+		this.aliases = columnAliases;
+		this.columnAliases = columnAliases;
+		this.grouped = groupBy!=null;
+		this.groupBy = groupBy;
+	}
+
+	@Override
+	public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) {
+		return StringHelper.replace( sql, "{alias}", criteriaQuery.getSQLAlias( criteria ) );
 	}
 
-	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	@Override
+	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
 		return StringHelper.replace( groupBy, "{alias}", criteriaQuery.getSQLAlias( criteria ) );
 	}
 
-	public Type[] getTypes(Criteria crit, CriteriaQuery criteriaQuery)
-	throws HibernateException {
+	@Override
+	public Type[] getTypes(Criteria crit, CriteriaQuery criteriaQuery) {
 		return types;
 	}
 
+	@Override
 	public String toString() {
 		return sql;
 	}
 
-	protected SQLProjection(String sql, String[] columnAliases, Type[] types) {
-		this(sql, null, columnAliases, types);
-	}
-	
-	protected SQLProjection(String sql, String groupBy, String[] columnAliases, Type[] types) {
-		this.sql = sql;
-		this.types = types;
-		this.aliases = columnAliases;
-		this.columnAliases = columnAliases;
-		this.grouped = groupBy!=null;
-		this.groupBy = groupBy;
-	}
-
+	@Override
 	public String[] getAliases() {
 		return aliases;
 	}
-	
+
+	@Override
 	public String[] getColumnAliases(int loc) {
 		return columnAliases;
 	}
-	
+
+	@Override
 	public boolean isGrouped() {
 		return grouped;
 	}
 
+	@Override
 	public Type[] getTypes(String alias, Criteria crit, CriteriaQuery criteriaQuery) {
-		return null; //unsupported
+		return null;
 	}
 
+	@Override
 	public String[] getColumnAliases(String alias, int loc) {
-		return null; //unsupported
+		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java
index be272b166a..b8d80f0651 100644
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleExpression.java
@@ -1,118 +1,123 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2012, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2008, 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
 
 import java.sql.Types;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.type.Type;
 
 /**
  * superclass for "simple" comparisons (with SQL binary operators)
  *
  * @author Gavin King
  */
 public class SimpleExpression implements Criterion {
 	private final String propertyName;
 	private final Object value;
 	private boolean ignoreCase;
 	private final String op;
 
 	protected SimpleExpression(String propertyName, Object value, String op) {
 		this.propertyName = propertyName;
 		this.value = value;
 		this.op = op;
 	}
 
 	protected SimpleExpression(String propertyName, Object value, String op, boolean ignoreCase) {
 		this.propertyName = propertyName;
 		this.value = value;
 		this.ignoreCase = ignoreCase;
 		this.op = op;
 	}
 
+	protected final String getOp() {
+		return op;
+	}
+
+	public String getPropertyName() {
+		return propertyName;
+	}
+
+	public Object getValue() {
+		return value;
+	}
+
+	/**
+	 * Make case insensitive.  No effect for non-String values
+	 *
+	 * @return {@code this}, for method chaining
+	 */
 	public SimpleExpression ignoreCase() {
 		ignoreCase = true;
 		return this;
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-			throws HibernateException {
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final String[] columns = criteriaQuery.findColumns( propertyName, criteria );
+		final Type type = criteriaQuery.getTypeUsingProjection( criteria, propertyName );
+		final StringBuilder fragment = new StringBuilder();
 
-		String[] columns = criteriaQuery.findColumns( propertyName, criteria );
-		Type type = criteriaQuery.getTypeUsingProjection( criteria, propertyName );
-		StringBuilder fragment = new StringBuilder();
 		if ( columns.length > 1 ) {
 			fragment.append( '(' );
 		}
-		SessionFactoryImplementor factory = criteriaQuery.getFactory();
-		int[] sqlTypes = type.sqlTypes( factory );
+		final SessionFactoryImplementor factory = criteriaQuery.getFactory();
+		final int[] sqlTypes = type.sqlTypes( factory );
 		for ( int i = 0; i < columns.length; i++ ) {
-			boolean lower = ignoreCase &&
-					(sqlTypes[i] == Types.VARCHAR || sqlTypes[i] == Types.CHAR);
+			final boolean lower = ignoreCase && (sqlTypes[i] == Types.VARCHAR || sqlTypes[i] == Types.CHAR);
 			if ( lower ) {
-				fragment.append( factory.getDialect().getLowercaseFunction() )
-						.append( '(' );
+				fragment.append( factory.getDialect().getLowercaseFunction() ).append( '(' );
 			}
 			fragment.append( columns[i] );
 			if ( lower ) {
 				fragment.append( ')' );
 			}
+
 			fragment.append( getOp() ).append( "?" );
 			if ( i < columns.length - 1 ) {
 				fragment.append( " and " );
 			}
 		}
 		if ( columns.length > 1 ) {
 			fragment.append( ')' );
 		}
 		return fragment.toString();
-
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
-			throws HibernateException {
-		Object icvalue = ignoreCase ? value.toString().toLowerCase() : value;
-		return new TypedValue[] {criteriaQuery.getTypedValue( criteria, propertyName, icvalue )};
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final Object casedValue = ignoreCase ? value.toString().toLowerCase() : value;
+		return new TypedValue[] { criteriaQuery.getTypedValue( criteria, propertyName, casedValue ) };
 	}
 
+	@Override
 	public String toString() {
 		return propertyName + getOp() + value;
 	}
 
-	protected final String getOp() {
-		return op;
-	}
-
-	public String getPropertyName() {
-		return propertyName;
-	}
-
-	public Object getValue() {
-		return value;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleProjection.java b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleProjection.java
index b9a9062f3b..135298ce97 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleProjection.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleProjection.java
@@ -1,111 +1,133 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 import org.hibernate.type.Type;
 
-
 /**
  * A single-column projection that may be aliased
+ *
  * @author Gavin King
  */
 public abstract class SimpleProjection implements EnhancedProjection {
-
 	private static final int NUM_REUSABLE_ALIASES = 40;
-	private static final String[] reusableAliases = initializeReusableAliases();
-
-	public Projection as(String alias) {
-		return Projections.alias(this, alias);
-	}
+	private static final String[] REUSABLE_ALIASES = initializeReusableAliases();
 
 	private static String[] initializeReusableAliases() {
-		String[] aliases = new String[NUM_REUSABLE_ALIASES];
+		final String[] aliases = new String[NUM_REUSABLE_ALIASES];
 		for ( int i = 0; i < NUM_REUSABLE_ALIASES; i++ ) {
 			aliases[i] = aliasForLocation( i );
 		}
 		return aliases;
 	}
 
 	private static String aliasForLocation(final int loc) {
 		return "y" + loc + "_";
 	}
 
 	private static String getAliasForLocation(final int loc) {
 		if ( loc >= NUM_REUSABLE_ALIASES ) {
 			return aliasForLocation( loc );
 		}
 		else {
-			return reusableAliases[loc];
+			return REUSABLE_ALIASES[loc];
 		}
 	}
 
+	/**
+	 * Create an aliased form of this projection
+	 *
+	 * @param alias The alias to apply
+	 *
+	 * @return The aliased projection
+	 */
+	public Projection as(String alias) {
+		return Projections.alias( this, alias );
+	}
+
+	@Override
 	public String[] getColumnAliases(String alias, int loc) {
 		return null;
 	}
 
+	@Override
 	public String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
 		return getColumnAliases( alias, loc );
 	}
 
+	@Override
 	public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery) {
 		return null;
 	}
 
+	@Override
 	public String[] getColumnAliases(int loc) {
 		return new String[] { getAliasForLocation( loc ) };
 	}
 
+	/**
+	 * Count the number of columns this projection uses.
+	 *
+	 * @param criteria The criteria
+	 * @param criteriaQuery The query
+	 *
+	 * @return The number of columns
+	 */
 	public int getColumnCount(Criteria criteria, CriteriaQuery criteriaQuery) {
-		Type types[] = getTypes( criteria, criteriaQuery );
+		final Type[] types = getTypes( criteria, criteriaQuery );
 		int count = 0;
-		for ( int i=0; i<types.length; i++ ) {
-			count += types[ i ].getColumnSpan( criteriaQuery.getFactory() );
+		for ( Type type : types ) {
+			count += type.getColumnSpan( criteriaQuery.getFactory() );
 		}
 		return count;
 	}
 
+	@Override
 	public String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
-		int numColumns =  getColumnCount( criteria, criteriaQuery );
-		String[] aliases = new String[ numColumns ];
+		final int numColumns =  getColumnCount( criteria, criteriaQuery );
+		final String[] aliases = new String[ numColumns ];
 		for (int i = 0; i < numColumns; i++) {
 			aliases[i] = getAliasForLocation( loc );
 			loc++;
 		}
 		return aliases;
 	}
 
+	@Override
 	public String[] getAliases() {
 		return new String[1];
 	}
 
+	@Override
 	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
-		throw new UnsupportedOperationException("not a grouping projection");
+		throw new UnsupportedOperationException( "not a grouping projection" );
 	}
 
+	@Override
 	public boolean isGrouped() {
 		return false;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
index 4ee289765e..3fd1c53c8d 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SimpleSubqueryExpression.java
@@ -1,57 +1,56 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
  * A comparison between a constant value and the the result of a subquery
+ *
  * @author Gavin King
  */
 public class SimpleSubqueryExpression extends SubqueryExpression {
-	
 	private Object value;
-	
+
 	protected SimpleSubqueryExpression(Object value, String op, String quantifier, DetachedCriteria dc) {
-		super(op, quantifier, dc);
+		super( op, quantifier, dc );
 		this.value = value;
 	}
-	
-	
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		TypedValue[] superTv = super.getTypedValues(criteria, criteriaQuery);
-		TypedValue[] result = new TypedValue[superTv.length+1];
-		System.arraycopy(superTv, 0, result, 1, superTv.length);
-		result[0] = new TypedValue( getTypes()[0], value, EntityMode.POJO );
+
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final TypedValue[] subQueryTypedValues = super.getTypedValues( criteria, criteriaQuery );
+		final TypedValue[] result = new TypedValue[subQueryTypedValues.length+1];
+		System.arraycopy( subQueryTypedValues, 0, result, 1, subQueryTypedValues.length );
+		result[0] = new TypedValue( getTypes()[0], value );
 		return result;
 	}
-	
+
+	@Override
 	protected String toLeftSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
 		return "?";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
index f8d9067f1f..b4dfd5e56e 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SizeExpression.java
@@ -1,84 +1,82 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2008, 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.criterion;
 
 import org.hibernate.Criteria;
-import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.sql.ConditionFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
+ * Used to define a restriction on a collection property based on its size.
+ *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public class SizeExpression implements Criterion {
-	
 	private final String propertyName;
 	private final int size;
 	private final String op;
 	
 	protected SizeExpression(String propertyName, int size, String op) {
 		this.propertyName = propertyName;
 		this.size = size;
 		this.op = op;
 	}
 
-	public String toString() {
-		return propertyName + ".size" + op + size;
+	@Override
+	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final String entityName =criteriaQuery.getEntityName( criteria, propertyName );
+		final String role = entityName + '.' + criteriaQuery.getPropertyName( propertyName );
+		final QueryableCollection cp = (QueryableCollection) criteriaQuery.getFactory().getCollectionPersister( role );
+
+		final String[] fk = cp.getKeyColumnNames();
+		final String[] pk = ( (Loadable) cp.getOwnerEntityPersister() ).getIdentifierColumnNames();
+
+		final ConditionFragment subQueryRestriction = new ConditionFragment()
+				.setTableAlias( criteriaQuery.getSQLAlias( criteria, propertyName ) )
+				.setCondition( pk, fk );
+
+		return String.format(
+				"? %s (select count(*) from %s where %s)",
+				op,
+				cp.getTableName(),
+				subQueryRestriction.toFragmentString()
+		);
 	}
 
-	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
-	throws HibernateException {
-		String role = criteriaQuery.getEntityName(criteria, propertyName) + 
-				'.' +  
-				criteriaQuery.getPropertyName(propertyName);
-		QueryableCollection cp = (QueryableCollection) criteriaQuery.getFactory()
-				.getCollectionPersister(role);
-		//String[] fk = StringHelper.qualify( "collection_", cp.getKeyColumnNames() );
-		String[] fk = cp.getKeyColumnNames();
-		String[] pk = ( (Loadable) cp.getOwnerEntityPersister() ).getIdentifierColumnNames(); //TODO: handle property-ref
-		return "? " + 
-				op + 
-				" (select count(*) from " +
-				cp.getTableName() +
-				//" collection_ where " +
-				" where " +
-				new ConditionFragment()
-						.setTableAlias( criteriaQuery.getSQLAlias(criteria, propertyName) )
-						.setCondition(pk, fk)
-						.toFragmentString() +
-				")";
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		return new TypedValue[] { new TypedValue( StandardBasicTypes.INTEGER, size ) };
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
-		return new TypedValue[] {
-			new TypedValue( StandardBasicTypes.INTEGER, size, EntityMode.POJO )
-		};
+	@Override
+	public String toString() {
+		return propertyName + ".size" + op + size;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java b/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
index c179d739d8..f6f848ddbb 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
@@ -1,201 +1,648 @@
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
 package org.hibernate.criterion;
 
 /**
  * Factory class for criterion instances that represent expressions
  * involving subqueries.
  * 
  * @see Restrictions
  * @see Projection
  * @see org.hibernate.Criteria
  *
  * @author Gavin King
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class Subqueries {
-		
+
+	/**
+	 * Creates a criterion which checks for the existence of rows in the subquery result
+	 *
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see ExistsSubqueryExpression
+	 */
 	public static Criterion exists(DetachedCriteria dc) {
-		return new ExistsSubqueryExpression("exists", dc);
+		return new ExistsSubqueryExpression( "exists", dc );
 	}
-	
+
+	/**
+	 * Creates a criterion which checks for the non-existence of rows in the subquery result
+	 *
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see ExistsSubqueryExpression
+	 */
 	public static Criterion notExists(DetachedCriteria dc) {
-		return new ExistsSubqueryExpression("not exists", dc);
+		return new ExistsSubqueryExpression( "not exists", dc );
 	}
-	
+
+	/**
+	 * Creates a criterion which checks that the value of a given property equals ALL the values in the
+	 * subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
 	public static Criterion propertyEqAll(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "=", "all", dc);
+		return new PropertySubqueryExpression( propertyName, "=", "all", dc );
 	}
-	
-	public static Criterion propertyIn(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "in", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is greater-than ALL the values in the
+	 * subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyGtAll(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, ">", "all", dc );
 	}
-	
-	public static Criterion propertyNotIn(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "not in", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is less-than ALL the values in the
+	 * subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyLtAll(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, "<", "all", dc );
 	}
-	
-	public static Criterion propertyEq(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "=", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is greater-than-or-equal-to ALL the
+	 * values in the subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyGeAll(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, ">=", "all", dc );
 	}
 
-	public static Criterion propertiesEq(String[] propertyNames, DetachedCriteria dc) {
-		return new PropertiesSubqueryExpression(propertyNames, "=", dc);
+	/**
+	 * Creates a criterion which checks that the value of a given property is less-than-or-equal-to ALL the
+	 * values in the subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyLeAll(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, "<=", "all", dc );
 	}
 
-	public static Criterion propertiesNotEq(String[] propertyNames, DetachedCriteria dc) {
-		return new PropertiesSubqueryExpression(propertyNames, "<>", dc);
+	/**
+	 * Creates a criterion which checks that the value of a given property is greater-than SOME of the
+	 * values in the subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyGtSome(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, ">", "some", dc );
 	}
 
-	public static Criterion propertiesIn(String[] propertyNames, DetachedCriteria dc) {
-		return new PropertiesSubqueryExpression(propertyNames, "in", dc);
+	/**
+	 * Creates a criterion which checks that the value of a given property is less-than SOME of the
+	 * values in the subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyLtSome(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, "<", "some", dc );
 	}
 
-	public static Criterion propertiesNotIn(String[] propertyNames, DetachedCriteria dc) {
-		return new PropertiesSubqueryExpression(propertyNames, "not in", dc);
+	/**
+	 * Creates a criterion which checks that the value of a given property is greater-than-or-equal-to SOME of the
+	 * values in the subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyGeSome(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, ">=", "some", dc );
+	}
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is less-than-or-equal-to SOME of the
+	 * values in the subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyLeSome(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, "<=", "some", dc );
+	}
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is in the set of values in the
+	 * subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyIn(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, "in", null, dc );
+	}
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is not-in the set of values in
+	 * the subquery result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyNotIn(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, "not in", null, dc );
 	}
-	
+
+	/**
+	 * Creates a criterion which checks that the value of a given property as being equal to the set of values in
+	 * the subquery result.  The implication is that the subquery returns a single result..
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
+	public static Criterion propertyEq(String propertyName, DetachedCriteria dc) {
+		return new PropertySubqueryExpression( propertyName, "=", null, dc );
+	}
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is not equal to the value in the
+	 * subquery result.  The assumption is that the subquery returns a single result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 */
 	public static Criterion propertyNe(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<>", null, dc);
 	}
-	
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is greater-than the value in the
+	 * subquery result.  The assumption is that the subquery returns a single result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 * @see #propertyGtAll
+	 * @see #propertyGtSome
+	 */
 	public static Criterion propertyGt(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, ">", null, dc);
+		return new PropertySubqueryExpression( propertyName, ">", null, dc );
 	}
-	
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is less-than the value in the
+	 * subquery result.  The assumption is that the subquery returns a single result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 * @see #propertyLtAll
+	 * @see #propertyLtSome
+	 */
 	public static Criterion propertyLt(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "<", null, dc);
+		return new PropertySubqueryExpression( propertyName, "<", null, dc );
 	}
-	
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is greater-than-or-equal-to the value
+	 * in the subquery result.  The assumption is that the subquery returns a single result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 * @see #propertyGeAll
+	 * @see #propertyGeSome
+	 */
 	public static Criterion propertyGe(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, ">=", null, dc);
+		return new PropertySubqueryExpression( propertyName, ">=", null, dc );
 	}
-	
+
+	/**
+	 * Creates a criterion which checks that the value of a given property is less-than-or-equal-to the value
+	 * in the subquery result.  The assumption is that the subquery returns a single result.
+	 *
+	 * @param propertyName The name of the property to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertySubqueryExpression
+	 * @see #propertyLeAll
+	 * @see #propertyLeSome
+	 */
 	public static Criterion propertyLe(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "<=", null, dc);
+		return new PropertySubqueryExpression( propertyName, "<=", null, dc );
 	}
-	
-	public static Criterion propertyGtAll(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, ">", "all", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of multiple given properties as being equal to the set of
+	 * values in the subquery result.  The implication is that the subquery returns a single result.  This form is
+	 * however implicitly using tuple comparisons
+	 *
+	 * @param propertyNames The names of the properties to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertiesSubqueryExpression
+	 */
+	public static Criterion propertiesEq(String[] propertyNames, DetachedCriteria dc) {
+		return new PropertiesSubqueryExpression( propertyNames, "=", dc );
 	}
-	
-	public static Criterion propertyLtAll(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "<", "all", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of multiple given properties as being not-equal to the set of
+	 * values in the subquery result.  The assumption is that the subquery returns a single result.  This form is
+	 * however implicitly using tuple comparisons
+	 *
+	 * @param propertyNames The names of the properties to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertiesSubqueryExpression
+	 */
+	public static Criterion propertiesNotEq(String[] propertyNames, DetachedCriteria dc) {
+		return new PropertiesSubqueryExpression( propertyNames, "<>", dc );
 	}
-	
-	public static Criterion propertyGeAll(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, ">=", "all", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of multiple given properties as being in to the set of
+	 * values in the subquery result.  This form is implicitly using tuple comparisons
+	 *
+	 * @param propertyNames The names of the properties to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertiesSubqueryExpression
+	 */
+	public static Criterion propertiesIn(String[] propertyNames, DetachedCriteria dc) {
+		return new PropertiesSubqueryExpression( propertyNames, "in", dc );
 	}
-	
-	public static Criterion propertyLeAll(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "<=", "all", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of multiple given properties as being not-in to the set of
+	 * values in the subquery result.  This form is implicitly using tuple comparisons
+	 *
+	 * @param propertyNames The names of the properties to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see PropertiesSubqueryExpression
+	 */
+	public static Criterion propertiesNotIn(String[] propertyNames, DetachedCriteria dc) {
+		return new PropertiesSubqueryExpression( propertyNames, "not in", dc );
 	}
-	
-	public static Criterion propertyGtSome(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, ">", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal equals ALL the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion eqAll(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "=", "all", dc );
 	}
-	
-	public static Criterion propertyLtSome(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "<", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is greater-than ALL the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion gtAll(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, ">", "all", dc );
 	}
-	
-	public static Criterion propertyGeSome(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, ">=", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is less-than ALL the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion ltAll(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "<", "all", dc );
 	}
-	
-	public static Criterion propertyLeSome(String propertyName, DetachedCriteria dc) {
-		return new PropertySubqueryExpression(propertyName, "<=", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is greater-than-or-equal-to ALL the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion geAll(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, ">=", "all", dc );
 	}
-	
-	public static Criterion eqAll(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "=", "all", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is less-than-or-equal-to ALL the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion leAll(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "<=", "all", dc );
 	}
-	
-	public static Criterion in(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "in", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is greater-than SOME of the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion gtSome(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, ">", "some", dc );
 	}
-	
-	public static Criterion notIn(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "not in", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is less-than SOME of the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion ltSome(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "<", "some", dc );
 	}
-	
-	public static Criterion eq(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "=", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is greater-than-or-equal-to SOME of the values
+	 * in the subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion geSome(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, ">=", "some", dc );
 	}
-	
-	public static Criterion gt(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, ">", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is less-than-or-equal-to SOME of the values
+	 * in the subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion leSome(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "<=", "some", dc );
 	}
-	
-	public static Criterion lt(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "<", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is IN the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion in(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "in", null, dc );
 	}
-	
-	public static Criterion ge(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, ">=", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a literal is NOT IN the values in the
+	 * subquery result.
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion notIn(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "not in", null, dc );
 	}
-	
-	public static Criterion le(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "<=", null, dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given literal as being equal to the value in
+	 * the subquery result.  The implication is that the subquery returns a single result..
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion eq(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "=", null, dc );
 	}
-	
+
+	/**
+	 * Creates a criterion which checks that the value of a given literal as being not-equal to the value in
+	 * the subquery result.  The implication is that the subquery returns a single result..
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
 	public static Criterion ne(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<>", null, dc);
 	}
-	
-	public static Criterion gtAll(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, ">", "all", dc);
-	}
-	
-	public static Criterion ltAll(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "<", "all", dc);
-	}
-	
-	public static Criterion geAll(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, ">=", "all", dc);
-	}
-	
-	public static Criterion leAll(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "<=", "all", dc);
-	}
-	
-	public static Criterion gtSome(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, ">", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given literal as being greater-than the value in
+	 * the subquery result.  The implication is that the subquery returns a single result..
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion gt(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, ">", null, dc );
 	}
-	
-	public static Criterion ltSome(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "<", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given literal as being less-than the value in
+	 * the subquery result.  The implication is that the subquery returns a single result..
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion lt(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "<", null, dc );
 	}
-	
-	public static Criterion geSome(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, ">=", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given literal as being greater-than-or-equal-to the
+	 * value in the subquery result.  The implication is that the subquery returns a single result..
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion ge(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, ">=", null, dc );
 	}
-	
-	public static Criterion leSome(Object value, DetachedCriteria dc) {
-		return new SimpleSubqueryExpression(value, "<=", "some", dc);
+
+	/**
+	 * Creates a criterion which checks that the value of a given literal as being less-than-or-equal-to the
+	 * value in the subquery result.  The implication is that the subquery returns a single result..
+	 *
+	 * @param value The literal value to use in comparison
+	 * @param dc The detached criteria representing the subquery
+	 *
+	 * @return The Criterion
+	 *
+	 * @see SimpleSubqueryExpression
+	 */
+	public static Criterion le(Object value, DetachedCriteria dc) {
+		return new SimpleSubqueryExpression( value, "<=", null, dc );
 	}
-	
 
+	private Subqueries() {
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
index 5a6615bf3c..8d059532dd 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
@@ -1,155 +1,155 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.loader.criteria.CriteriaJoinWalker;
 import org.hibernate.loader.criteria.CriteriaQueryTranslator;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.type.Type;
 
 /**
+ * A criterion that involves a subquery
+ *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public abstract class SubqueryExpression implements Criterion {
-	
 	private CriteriaImpl criteriaImpl;
 	private String quantifier;
 	private String op;
 	private QueryParameters params;
 	private Type[] types;
 	private CriteriaQueryTranslator innerQuery;
 
-	protected Type[] getTypes() {
-		return types;
-	}
-	
 	protected SubqueryExpression(String op, String quantifier, DetachedCriteria dc) {
 		this.criteriaImpl = dc.getCriteriaImpl();
 		this.quantifier = quantifier;
 		this.op = op;
 	}
-	
+
+	protected Type[] getTypes() {
+		return types;
+	}
+
 	protected abstract String toLeftSqlString(Criteria criteria, CriteriaQuery outerQuery);
 
+	@Override
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
+		final StringBuilder buf = new StringBuilder( toLeftSqlString( criteria, criteriaQuery ) );
+		if ( op != null ) {
+			buf.append( ' ' ).append( op ).append( ' ' );
+		}
+		if ( quantifier != null ) {
+			buf.append( quantifier ).append( ' ' );
+		}
+
 		final SessionFactoryImplementor factory = criteriaQuery.getFactory();
 		final OuterJoinLoadable persister =
-				( OuterJoinLoadable ) factory.getEntityPersister( criteriaImpl.getEntityOrClassName() );
+				(OuterJoinLoadable) factory.getEntityPersister( criteriaImpl.getEntityOrClassName() );
 
 		createAndSetInnerQuery( criteriaQuery, factory );
 		criteriaImpl.setSession( deriveRootSession( criteria ) );
 
-		CriteriaJoinWalker walker = new CriteriaJoinWalker(
+		final CriteriaJoinWalker walker = new CriteriaJoinWalker(
 				persister,
 				innerQuery,
 				factory,
 				criteriaImpl,
 				criteriaImpl.getEntityOrClassName(),
 				criteriaImpl.getSession().getLoadQueryInfluencers(),
 				innerQuery.getRootSQLALias()
 		);
 
-		String sql = walker.getSQLString();
-
-		final StringBuilder buf = new StringBuilder( toLeftSqlString(criteria, criteriaQuery) );
-		if ( op != null ) {
-			buf.append( ' ' ).append( op ).append( ' ' );
-		}
-		if ( quantifier != null ) {
-			buf.append( quantifier ).append( ' ' );
-		}
-		return buf.append( '(' ).append( sql ).append( ')' )
-				.toString();
+		return buf.append( '(' ).append( walker.getSQLString() ).append( ')' ).toString();
 	}
 
 	private SessionImplementor deriveRootSession(Criteria criteria) {
 		if ( criteria instanceof CriteriaImpl ) {
-			return ( ( CriteriaImpl ) criteria ).getSession();
+			return ( (CriteriaImpl) criteria ).getSession();
 		}
 		else if ( criteria instanceof CriteriaImpl.Subcriteria ) {
-			return deriveRootSession( ( ( CriteriaImpl.Subcriteria ) criteria ).getParent() );
+			return deriveRootSession( ( (CriteriaImpl.Subcriteria) criteria ).getParent() );
 		}
 		else {
 			// could happen for custom Criteria impls.  Not likely, but...
 			// 		for long term solution, see HHH-3514
 			return null;
 		}
 	}
 
-	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
-	throws HibernateException {
+	@Override
+	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		//the following two lines were added to ensure that this.params is not null, which
 		//can happen with two-deep nested subqueries
-		SessionFactoryImplementor factory = criteriaQuery.getFactory();
-		createAndSetInnerQuery(criteriaQuery, factory);
-		
-		Type[] ppTypes = params.getPositionalParameterTypes();
-		Object[] ppValues = params.getPositionalParameterValues();
-		TypedValue[] tv = new TypedValue[ppTypes.length];
+		final SessionFactoryImplementor factory = criteriaQuery.getFactory();
+		createAndSetInnerQuery( criteriaQuery, factory );
+
+		final Type[] ppTypes = params.getPositionalParameterTypes();
+		final Object[] ppValues = params.getPositionalParameterValues();
+		final TypedValue[] tv = new TypedValue[ppTypes.length];
 		for ( int i=0; i<ppTypes.length; i++ ) {
-			tv[i] = new TypedValue( ppTypes[i], ppValues[i], EntityMode.POJO );
+			tv[i] = new TypedValue( ppTypes[i], ppValues[i] );
 		}
 		return tv;
 	}
 
 	/**
 	 * Creates the inner query used to extract some useful information about types, since it is needed in both methods.
 	 *
 	 * @param criteriaQuery The criteria query
 	 * @param factory The session factory.
 	 */
 	private void createAndSetInnerQuery(CriteriaQuery criteriaQuery, SessionFactoryImplementor factory) {
 		if ( innerQuery == null ) {
 			//with two-deep subqueries, the same alias would get generated for
 			//both using the old method (criteriaQuery.generateSQLAlias()), so
 			//that is now used as a fallback if the main criteria alias isn't set
 			String alias;
 			if ( this.criteriaImpl.getAlias() == null ) {
 				alias = criteriaQuery.generateSQLAlias();
 			}
 			else {
 				alias = this.criteriaImpl.getAlias() + "_";
 			}
 
 			innerQuery = new CriteriaQueryTranslator(
 					factory,
 					criteriaImpl,
-					criteriaImpl.getEntityOrClassName(), //implicit polymorphism not supported (would need a union)
+					criteriaImpl.getEntityOrClassName(),
 					alias,
 					criteriaQuery
 				);
 
 			params = innerQuery.getQueryParameters();
 			types = innerQuery.getProjectedTypes();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
index b4c043de2c..0e4ff5ab48 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
@@ -1,775 +1,775 @@
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
 package org.hibernate.internal.util;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.BitSet;
 import java.util.Iterator;
 import java.util.StringTokenizer;
 import java.util.UUID;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 public final class StringHelper {
 
 	private static final int ALIAS_TRUNCATE_LENGTH = 10;
 	public static final String WHITESPACE = " \n\r\f\t";
 
 	private StringHelper() { /* static methods only - hide constructor */
 	}
 	
 	/*public static boolean containsDigits(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			if ( Character.isDigit( string.charAt(i) ) ) return true;
 		}
 		return false;
 	}*/
 
 	public static int lastIndexOfLetter(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			char character = string.charAt(i);
 			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
 		}
 		return string.length()-1;
 	}
 
 	public static String join(String seperator, String[] strings) {
 		int length = strings.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * strings[0].length() )
 				.append( strings[0] );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( seperator ).append( strings[i] );
 		}
 		return buf.toString();
 	}
 
 	public static String joinWithQualifier(String[] values, String qualifier, String deliminator) {
 		int length = values.length;
 		if ( length == 0 ) return "";
 		StringBuilder buf = new StringBuilder( length * values[0].length() )
 				.append( qualify( qualifier, values[0] ) );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( deliminator ).append( qualify( qualifier, values[i] ) );
 		}
 		return buf.toString();
 	}
 
 	public static String join(String seperator, Iterator objects) {
 		StringBuilder buf = new StringBuilder();
 		if ( objects.hasNext() ) buf.append( objects.next() );
 		while ( objects.hasNext() ) {
 			buf.append( seperator ).append( objects.next() );
 		}
 		return buf.toString();
 	}
 
 	public static String[] add(String[] x, String sep, String[] y) {
-		String[] result = new String[x.length];
+		final String[] result = new String[x.length];
 		for ( int i = 0; i < x.length; i++ ) {
 			result[i] = x[i] + sep + y[i];
 		}
 		return result;
 	}
 
 	public static String repeat(String string, int times) {
 		StringBuilder buf = new StringBuilder( string.length() * times );
 		for ( int i = 0; i < times; i++ ) buf.append( string );
 		return buf.toString();
 	}
 
 	public static String repeat(String string, int times, String deliminator) {
 		StringBuilder buf = new StringBuilder(  ( string.length() * times ) + ( deliminator.length() * (times-1) ) )
 				.append( string );
 		for ( int i = 1; i < times; i++ ) {
 			buf.append( deliminator ).append( string );
 		}
 		return buf.toString();
 	}
 
 	public static String repeat(char character, int times) {
 		char[] buffer = new char[times];
 		Arrays.fill( buffer, character );
 		return new String( buffer );
 	}
 
 
 	public static String replace(String template, String placeholder, String replacement) {
 		return replace( template, placeholder, replacement, false );
 	}
 
 	public static String[] replace(String templates[], String placeholder, String replacement) {
 		String[] result = new String[templates.length];
 		for ( int i =0; i<templates.length; i++ ) {
 			result[i] = replace( templates[i], placeholder, replacement );
 		}
 		return result;
 	}
 
 	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
 		return replace( template, placeholder, replacement, wholeWords, false );
 	}
 
 	public static String replace(String template,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		if ( template == null ) {
 			return template;
 		}
 		int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			String beforePlaceholder = template.substring( 0, loc );
 			String afterPlaceholder = template.substring( loc + placeholder.length() );
 			return replace( beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary );
 		}
 	}
 
 
 	public static String replace(String beforePlaceholder,
 								 String afterPlaceholder,
 								 String placeholder,
 								 String replacement,
 								 boolean wholeWords,
 								 boolean encloseInParensIfNecessary) {
 		final boolean actuallyReplace =
 				! wholeWords ||
 				afterPlaceholder.length() == 0 ||
 				! Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
 		boolean encloseInParens =
 				actuallyReplace &&
 				encloseInParensIfNecessary &&
 				! ( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' ) &&
 				! ( getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')' );		
 		StringBuilder buf = new StringBuilder( beforePlaceholder );
 		if ( encloseInParens ) {
 			buf.append( '(' );
 		}
 		buf.append( actuallyReplace ? replacement : placeholder );
 		if ( encloseInParens ) {
 			buf.append( ')' );
 		}
 		buf.append(
 				replace(
 						afterPlaceholder,
 						placeholder,
 						replacement,
 						wholeWords,
 						encloseInParensIfNecessary
 				)
 		);
 		return buf.toString();
 	}
 
 	public static char getLastNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = str.length() - 1 ; i >= 0 ; i-- ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static char getFirstNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = 0 ; i < str.length() ; i++ ) {
 				char ch = str.charAt( i );
 				if ( ! Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static String replaceOnce(String template, String placeholder, String replacement) {
 		if ( template == null ) {
 			return template; // returnign null!
 		}
         int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			return new StringBuilder( template.substring( 0, loc ) )
 					.append( replacement )
 					.append( template.substring( loc + placeholder.length() ) )
 					.toString();
 		}
 	}
 
 
 	public static String[] split(String seperators, String list) {
 		return split( seperators, list, false );
 	}
 
 	public static String[] split(String seperators, String list, boolean include) {
 		StringTokenizer tokens = new StringTokenizer( list, seperators, include );
 		String[] result = new String[ tokens.countTokens() ];
 		int i = 0;
 		while ( tokens.hasMoreTokens() ) {
 			result[i++] = tokens.nextToken();
 		}
 		return result;
 	}
 
 	public static String unqualify(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1 );
 	}
 
 	public static String qualifier(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
 	}
 
 	/**
 	 * Collapses a name.  Mainly intended for use with classnames, where an example might serve best to explain.
 	 * Imagine you have a class named <samp>'org.hibernate.internal.util.StringHelper'</samp>; calling collapse on that
 	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
 	 *
 	 * @param name The name to collapse.
 	 * @return The collapsed name.
 	 */
 	public static String collapse(String name) {
 		if ( name == null ) {
 			return null;
 		}
 		int breakPoint = name.lastIndexOf( '.' );
 		if ( breakPoint < 0 ) {
 			return name;
 		}
 		return collapseQualifier( name.substring( 0, breakPoint ), true ) + name.substring( breakPoint ); // includes last '.'
 	}
 
 	/**
 	 * Given a qualifier, collapse it.
 	 *
 	 * @param qualifier The qualifier to collapse.
 	 * @param includeDots Should we include the dots in the collapsed form?
 	 *
 	 * @return The collapsed form.
 	 */
 	public static String collapseQualifier(String qualifier, boolean includeDots) {
 		StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
 		String collapsed = Character.toString( tokenizer.nextToken().charAt( 0 ) );
 		while ( tokenizer.hasMoreTokens() ) {
 			if ( includeDots ) {
 				collapsed += '.';
 			}
 			collapsed += tokenizer.nextToken().charAt( 0 );
 		}
 		return collapsed;
 	}
 
 	/**
 	 * Partially unqualifies a qualified name.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself, or the partially unqualified form if it begins with the qualifier base.
 	 */
 	public static String partiallyUnqualify(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return name;
 		}
 		return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
 	}
 
 	/**
 	 * Cross between {@link #collapse} and {@link #partiallyUnqualify}.  Functions much like {@link #collapse}
 	 * except that only the qualifierBase is collapsed.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'o.h.util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself if it does not begin with the qualifierBase, or the properly collapsed form otherwise.
 	 */
 	public static String collapseQualifierBase(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return collapse( name );
 		}
 		return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
 	}
 
 	public static String[] suffix(String[] columns, String suffix) {
 		if ( suffix == null ) return columns;
 		String[] qualified = new String[columns.length];
 		for ( int i = 0; i < columns.length; i++ ) {
 			qualified[i] = suffix( columns[i], suffix );
 		}
 		return qualified;
 	}
 
 	private static String suffix(String name, String suffix) {
 		return ( suffix == null ) ? name : name + suffix;
 	}
 
 	public static String root(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
 	}
 
 	public static String unroot(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc+1, qualifiedName.length() );
 	}
 
 	public static boolean booleanValue(String tfString) {
 		String trimmed = tfString.trim().toLowerCase();
 		return trimmed.equals( "true" ) || trimmed.equals( "t" );
 	}
 
 	public static String toString(Object[] array) {
 		int len = array.length;
 		if ( len == 0 ) return "";
 		StringBuilder buf = new StringBuilder( len * 12 );
 		for ( int i = 0; i < len - 1; i++ ) {
 			buf.append( array[i] ).append(", ");
 		}
 		return buf.append( array[len - 1] ).toString();
 	}
 
 	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
 		String[] result = new String[]{string};
 		while ( placeholders.hasNext() ) {
 			result = multiply( result, ( String ) placeholders.next(), ( String[] ) replacements.next() );
 		}
 		return result;
 	}
 
 	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
 		String[] results = new String[replacements.length * strings.length];
 		int n = 0;
 		for ( int i = 0; i < replacements.length; i++ ) {
 			for ( int j = 0; j < strings.length; j++ ) {
 				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
 			}
 		}
 		return results;
 	}
 
 	public static int countUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null)
 			return 0;
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int count = 0;
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				count++;
 			}
 		}
 		return count;
 	}
 
 	public static int[] locateUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null) {
 			return new int[0];
 		}
 
 		ArrayList locations = new ArrayList( 20 );
 
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				locations.add( indx );
 			}
 		}
 		return ArrayHelper.toIntArray( locations );
 	}
 
 	public static boolean isNotEmpty(String string) {
 		return string != null && string.length() > 0;
 	}
 
 	public static boolean isEmpty(String string) {
 		return string == null || string.length() == 0;
 	}
 
 	public static String qualify(String prefix, String name) {
 		if ( name == null || prefix == null ) {
 			throw new NullPointerException( "prefix or name were null attempting to build qualified name" );
 		}
 		return prefix + '.' + name;
 	}
 
 	public static String[] qualify(String prefix, String[] names) {
 		if ( prefix == null ) return names;
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			qualified[i] = qualify( prefix, names[i] );
 		}
 		return qualified;
 	}
 	public static int firstIndexOfChar(String sqlString, BitSet keys, int startindex) {
 		for ( int i = startindex, size = sqlString.length(); i < size; i++ ) {
 			if ( keys.get( sqlString.charAt( i ) ) ) {
 				return i;
 			}
 		}
 		return -1;
 
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
 		BitSet keys = new BitSet();
 		for ( int i = 0, size = string.length(); i < size; i++ ) {
 			keys.set( string.charAt( i ) );
 		}
 		return firstIndexOfChar( sqlString, keys, startindex );
 
 	}
 
 	public static String truncate(String string, int length) {
 		if ( string.length() <= length ) {
 			return string;
 		}
 		else {
 			return string.substring( 0, length );
 		}
 	}
 
 	public static String generateAlias(String description) {
 		return generateAliasRoot(description) + '_';
 	}
 
 	/**
 	 * Generate a nice alias for the given class name or collection role name and unique integer. Subclasses of
 	 * Loader do <em>not</em> have to use aliases of this form.
 	 *
 	 * @param description The base name (usually an entity-name or collection-role)
 	 * @param unique A uniquing value
 	 *
 	 * @return an alias of the form <samp>foo1_</samp>
 	 */
 	public static String generateAlias(String description, int unique) {
 		return generateAliasRoot(description) +
 			Integer.toString(unique) +
 			'_';
 	}
 
 	/**
 	 * Generates a root alias by truncating the "root name" defined by
 	 * the incoming decription and removing/modifying any non-valid
 	 * alias characters.
 	 *
 	 * @param description The root name from which to generate a root alias.
 	 * @return The generated root alias.
 	 */
 	private static String generateAliasRoot(String description) {
 		String result = truncate( unqualifyEntityName(description), ALIAS_TRUNCATE_LENGTH )
 				.toLowerCase()
 		        .replace( '/', '_' ) // entityNames may now include slashes for the representations
 				.replace( '$', '_' ); //classname may be an inner class
 		result = cleanAlias( result );
 		if ( Character.isDigit( result.charAt(result.length()-1) ) ) {
 			return result + "x"; //ick!
 		}
 		else {
 			return result;
 		}
 	}
 
 	/**
 	 * Clean the generated alias by removing any non-alpha characters from the
 	 * beginning.
 	 *
 	 * @param alias The generated alias to be cleaned.
 	 * @return The cleaned alias, stripped of any leading non-alpha characters.
 	 */
 	private static String cleanAlias(String alias) {
 		char[] chars = alias.toCharArray();
 		// short cut check...
 		if ( !Character.isLetter( chars[0] ) ) {
 			for ( int i = 1; i < chars.length; i++ ) {
 				// as soon as we encounter our first letter, return the substring
 				// from that position
 				if ( Character.isLetter( chars[i] ) ) {
 					return alias.substring( i );
 				}
 			}
 		}
 		return alias;
 	}
 
 	public static String unqualifyEntityName(String entityName) {
 		String result = unqualify(entityName);
 		int slashPos = result.indexOf( '/' );
 		if ( slashPos > 0 ) {
 			result = result.substring( 0, slashPos - 1 );
 		}
 		return result;
 	}
 	
 	public static String toUpperCase(String str) {
 		return str==null ? null : str.toUpperCase();
 	}
 	
 	public static String toLowerCase(String str) {
 		return str==null ? null : str.toLowerCase();
 	}
 
 	public static String moveAndToBeginning(String filter) {
 		if ( filter.trim().length()>0 ){
 			filter += " and ";
 			if ( filter.startsWith(" and ") ) filter = filter.substring(4);
 		}
 		return filter;
 	}
 
 	/**
 	 * Determine if the given string is quoted (wrapped by '`' characters at beginning and end).
 	 *
 	 * @param name The name to check.
 	 * @return True if the given string starts and ends with '`'; false otherwise.
 	 */
 	public static boolean isQuoted(String name) {
 		return name != null && name.length() != 0 && name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`';
 	}
 
 	/**
 	 * Return a representation of the given name ensuring quoting (wrapped with '`' characters).  If already wrapped
 	 * return name.
 	 *
 	 * @param name The name to quote.
 	 * @return The quoted version.
 	 */
 	public static String quote(String name) {
 		if ( isEmpty( name ) || isQuoted( name ) ) {
 			return name;
 		}
 // Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
         else if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) {
             name = name.substring( 1, name.length() - 1 );
         }
 
 		return new StringBuilder( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
 	}
 
 	/**
 	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
 	 *
 	 * @param name The name to be unquoted.
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name) {
 		return isQuoted( name ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Determine if the given name is quoted.  It is considered quoted if either:
 	 * <ol>
 	 * <li>starts AND ends with backticks (`)</li>
 	 * <li>starts with dialect-specified {@link org.hibernate.dialect.Dialect#openQuote() open-quote}
 	 * 		AND ends with dialect-specified {@link org.hibernate.dialect.Dialect#closeQuote() close-quote}</li>
 	 * </ol>
 	 *
 	 * @param name The name to check
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return True if quoted, false otherwise
 	 */
 	public static boolean isQuoted(String name, Dialect dialect) {
 		return name != null
 				&&
 					name.length() != 0
 				&& (
 					name.charAt( 0 ) == '`'
 					&&
 					name.charAt( name.length() - 1 ) == '`'
 					||
 					name.charAt( 0 ) == dialect.openQuote()
 					&&
 					name.charAt( name.length() - 1 ) == dialect.closeQuote()
 				);
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param name The name to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name, Dialect dialect) {
 		return isQuoted( name, dialect ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param names The names to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted versions.
 	 */
 	public static String[] unquote(String[] names, Dialect dialect) {
 		if ( names == null ) {
 			return null;
 		}
 		String[] unquoted = new String[ names.length ];
 		for ( int i = 0; i < names.length; i++ ) {
 			unquoted[i] = unquote( names[i], dialect );
 		}
 		return unquoted;
 	}
 
 
 	public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";
 
 	public static StringBuilder buildBatchFetchRestrictionFragment(
 			String alias,
 			String[] columnNames,
 			Dialect dialect) {
 		// the general idea here is to just insert a placeholder that we can easily find later...
 		if ( columnNames.length == 1 ) {
 			// non-composite key
 			return new StringBuilder( StringHelper.qualify( alias, columnNames[0] ) )
 					.append( " in (" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 		}
 		else {
 			// composite key - the form to use here depends on what the dialect supports.
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				// use : (col1, col2) in ( (?,?), (?,?), ... )
 				StringBuilder builder = new StringBuilder();
 				builder.append( "(" );
 				boolean firstPass = true;
 				String deliminator = "";
 				for ( String columnName : columnNames ) {
 					builder.append( deliminator ).append( StringHelper.qualify( alias, columnName ) );
 					if ( firstPass ) {
 						firstPass = false;
 						deliminator = ",";
 					}
 				}
 				builder.append( ") in (" );
 				builder.append( BATCH_ID_PLACEHOLDER );
 				builder.append( ")" );
 				return builder;
 			}
 			else {
 				// use : ( (col1 = ? and col2 = ?) or (col1 = ? and col2 = ?) or ... )
 				//		unfortunately most of this building needs to be held off until we know
 				//		the exact number of ids :(
 				return new StringBuilder( "(" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 			}
 		}
 	}
 
 	public static String expandBatchIdPlaceholder(
 			String sql,
 			Serializable[] ids,
 			String alias,
 			String[] keyColumnNames,
 			Dialect dialect) {
 		if ( keyColumnNames.length == 1 ) {
 			// non-composite
 			return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( "?", ids.length, "," ) );
 		}
 		else {
 			// composite
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				final String tuple = "(" + StringHelper.repeat( "?", keyColumnNames.length, "," );
 				return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( tuple, ids.length, "," ) );
 			}
 			else {
 				final String keyCheck = joinWithQualifier( keyColumnNames, alias, " and " );
 				return replace( sql, BATCH_ID_PLACEHOLDER, repeat( keyCheck, ids.length, " or " ) );
 			}
 		}
 	}
 	
 	/**
 	 * Takes a String s and returns a new String[1] with s as the only element.
 	 * If s is null or "", return String[0].
 	 * 
 	 * @param s
 	 * @return String[]
 	 */
 	public static String[] toArrayElement(String s) {
 		return ( s == null || s.length() == 0 ) ? new String[0] : new String[] { s };
 	}
 
 	// Oracle restricts identifier lengths to 30.  Rather than tie this to
 	// Dialect, simply restrict randomly-generated constrain names across
 	// the board.
 	private static final int MAX_NAME_LENGTH = 30;
 	public static String randomFixedLengthHex(String prefix) {
 		int length = MAX_NAME_LENGTH - prefix.length();
 		String s = UUID.randomUUID().toString();
 		s = s.replace( "-", "" );
 		if (s.length() > length) {
 			s = s.substring( 0, length );
 		}
 		return prefix + s;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
index 0aca3b9bf0..2f0ede7069 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
@@ -1,691 +1,683 @@
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
 package org.hibernate.loader.criteria;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.criterion.CriteriaQuery;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.EnhancedProjection;
 import org.hibernate.criterion.Projection;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.StringRepresentableType;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public class CriteriaQueryTranslator implements CriteriaQuery {
 
 	public static final String ROOT_SQL_ALIAS = Criteria.ROOT_ALIAS + '_';
 
 	private CriteriaQuery outerQueryTranslator;
 
 	private final CriteriaImpl rootCriteria;
 	private final String rootEntityName;
 	private final String rootSQLAlias;
 	private int aliasCount = 0;
 
 	private final Map /* <Criteria, CriteriaInfoProvider> */ criteriaInfoMap = new LinkedHashMap();
 	private final Map /* <String, CriteriaInfoProvider> */ nameCriteriaInfoMap = new LinkedHashMap();
 	private final Map criteriaSQLAliasMap = new HashMap();
 	private final Map aliasCriteriaMap = new HashMap();
 	private final Map associationPathCriteriaMap = new LinkedHashMap();
 	private final Map<String,JoinType> associationPathJoinTypesMap = new LinkedHashMap<String,JoinType>();
 	private final Map withClauseMap = new HashMap();
 	
 	private final SessionFactoryImplementor sessionFactory;
 	private final SessionFactoryHelper helper;
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias,
 	        CriteriaQuery outerQuery) throws HibernateException {
 		this( factory, criteria, rootEntityName, rootSQLAlias );
 		outerQueryTranslator = outerQuery;
 	}
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias) throws HibernateException {
 		this.rootCriteria = criteria;
 		this.rootEntityName = rootEntityName;
 		this.sessionFactory = factory;
 		this.rootSQLAlias = rootSQLAlias;
 		this.helper = new SessionFactoryHelper(factory);
 		createAliasCriteriaMap();
 		createAssociationPathCriteriaMap();
 		createCriteriaEntityNameMap();
 		createCriteriaSQLAliasMap();
 	}
 
 	public String generateSQLAlias() {
 		return StringHelper.generateAlias( Criteria.ROOT_ALIAS, aliasCount ) + '_';
 	}
 
 	public String getRootSQLALias() {
 		return rootSQLAlias;
 	}
 
 	private Criteria getAliasedCriteria(String alias) {
 		return ( Criteria ) aliasCriteriaMap.get( alias );
 	}
 
 	public boolean isJoin(String path) {
 		return associationPathCriteriaMap.containsKey( path );
 	}
 
 	public JoinType getJoinType(String path) {
 		JoinType result = associationPathJoinTypesMap.get( path );
 		return ( result == null ? JoinType.INNER_JOIN : result );
 	}
 
 	public Criteria getCriteria(String path) {
 		return ( Criteria ) associationPathCriteriaMap.get( path );
 	}
 
 	public Set getQuerySpaces() {
 		Set result = new HashSet();
 		Iterator iter = criteriaInfoMap.values().iterator();
 		while ( iter.hasNext() ) {
 			CriteriaInfoProvider info = ( CriteriaInfoProvider )iter.next();
 			result.addAll( Arrays.asList( info.getSpaces() ) );
 		}
 		return result;
 	}
 
 	private void createAliasCriteriaMap() {
 		aliasCriteriaMap.put( rootCriteria.getAlias(), rootCriteria );
 		Iterator iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			Criteria subcriteria = ( Criteria ) iter.next();
 			if ( subcriteria.getAlias() != null ) {
 				Object old = aliasCriteriaMap.put( subcriteria.getAlias(), subcriteria );
 				if ( old != null ) {
 					throw new QueryException( "duplicate alias: " + subcriteria.getAlias() );
 				}
 			}
 		}
 	}
 
 	private void createAssociationPathCriteriaMap() {
 		Iterator iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria crit = ( CriteriaImpl.Subcriteria ) iter.next();
 			String wholeAssociationPath = getWholeAssociationPath( crit );
 			Object old = associationPathCriteriaMap.put( wholeAssociationPath, crit );
 			if ( old != null ) {
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			JoinType joinType = crit.getJoinType();
 			old = associationPathJoinTypesMap.put( wholeAssociationPath, joinType );
 			if ( old != null ) {
 				// TODO : not so sure this is needed...
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			if ( crit.getWithClause() != null )
 			{
 				this.withClauseMap.put(wholeAssociationPath, crit.getWithClause());
 			}
 		}
 	}
 
 	private String getWholeAssociationPath(CriteriaImpl.Subcriteria subcriteria) {
 		String path = subcriteria.getPath();
 
 		// some messy, complex stuff here, since createCriteria() can take an
 		// aliased path, or a path rooted at the creating criteria instance
 		Criteria parent = null;
 		if ( path.indexOf( '.' ) > 0 ) {
 			// if it is a compound path
 			String testAlias = StringHelper.root( path );
 			if ( !testAlias.equals( subcriteria.getAlias() ) ) {
 				// and the qualifier is not the alias of this criteria
 				//      -> check to see if we belong to some criteria other
 				//          than the one that created us
 				parent = ( Criteria ) aliasCriteriaMap.get( testAlias );
 			}
 		}
 		if ( parent == null ) {
 			// otherwise assume the parent is the the criteria that created us
 			parent = subcriteria.getParent();
 		}
 		else {
 			path = StringHelper.unroot( path );
 		}
 
 		if ( parent.equals( rootCriteria ) ) {
 			// if its the root criteria, we are done
 			return path;
 		}
 		else {
 			// otherwise, recurse
 			return getWholeAssociationPath( ( CriteriaImpl.Subcriteria ) parent ) + '.' + path;
 		}
 	}
 
 	private void createCriteriaEntityNameMap() {
 		// initialize the rootProvider first
 		CriteriaInfoProvider rootProvider = new EntityCriteriaInfoProvider(( Queryable ) sessionFactory.getEntityPersister( rootEntityName ) );
 		criteriaInfoMap.put( rootCriteria, rootProvider);
 		nameCriteriaInfoMap.put ( rootProvider.getName(), rootProvider );
 
 		Iterator iter = associationPathCriteriaMap.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			CriteriaInfoProvider info = getPathInfo((String)me.getKey());
 
 			criteriaInfoMap.put(
 					me.getValue(), //the criteria instance
 					info
 			);
 
 			nameCriteriaInfoMap.put( info.getName(), info );
 		}
 	}
 
 
 	private CriteriaInfoProvider getPathInfo(String path) {
 		StringTokenizer tokens = new StringTokenizer( path, "." );
 		String componentPath = "";
 
 		// start with the 'rootProvider'
 		CriteriaInfoProvider provider = ( CriteriaInfoProvider )nameCriteriaInfoMap.get( rootEntityName );
 
 		while ( tokens.hasMoreTokens() ) {
 			componentPath += tokens.nextToken();
 			Type type = provider.getType( componentPath );
 			if ( type.isAssociationType() ) {
 				// CollectionTypes are always also AssociationTypes - but there's not always an associated entity...
 				AssociationType atype = ( AssociationType ) type;
 				CollectionType ctype = type.isCollectionType() ? (CollectionType)type : null;
 				Type elementType = (ctype != null) ? ctype.getElementType( sessionFactory ) : null;
 				// is the association a collection of components or value-types? (i.e a colloction of valued types?)
 				if ( ctype != null  && elementType.isComponentType() ) {
 					provider = new ComponentCollectionCriteriaInfoProvider( helper.getCollectionPersister(ctype.getRole()) );
 				}
 				else if ( ctype != null && !elementType.isEntityType() ) {
 					provider = new ScalarCollectionCriteriaInfoProvider( helper, ctype.getRole() );
 				}
 				else {
 					provider = new EntityCriteriaInfoProvider(( Queryable ) sessionFactory.getEntityPersister(
 											  atype.getAssociatedEntityName( sessionFactory )
 											  ));
 				}
 				
 				componentPath = "";
 			}
 			else if ( type.isComponentType() ) {
 				if (!tokens.hasMoreTokens()) {
 					throw new QueryException("Criteria objects cannot be created directly on components.  Create a criteria on owning entity and use a dotted property to access component property: "+path);
 				} else {
 					componentPath += '.';
 				}
 			}
 			else {
 				throw new QueryException( "not an association: " + componentPath );
 			}
 		}
 		
 		return provider;
 	}
 
 	public int getSQLAliasCount() {
 		return criteriaSQLAliasMap.size();
 	}
 
 	private void createCriteriaSQLAliasMap() {
 		int i = 0;
 		Iterator criteriaIterator = criteriaInfoMap.entrySet().iterator();
 		while ( criteriaIterator.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) criteriaIterator.next();
 			Criteria crit = ( Criteria ) me.getKey();
 			String alias = crit.getAlias();
 			if ( alias == null ) {
 				alias = (( CriteriaInfoProvider ) me.getValue()).getName(); // the entity name
 			}
 			criteriaSQLAliasMap.put( crit, StringHelper.generateAlias( alias, i++ ) );
 		}
 		criteriaSQLAliasMap.put( rootCriteria, rootSQLAlias );
 	}
 
 	public CriteriaImpl getRootCriteria() {
 		return rootCriteria;
 	}
 
 	public QueryParameters getQueryParameters() {
 		LockOptions lockOptions = new LockOptions();
 		RowSelection selection = new RowSelection();
 		selection.setFirstRow( rootCriteria.getFirstResult() );
 		selection.setMaxRows( rootCriteria.getMaxResults() );
 		selection.setTimeout( rootCriteria.getTimeout() );
 		selection.setFetchSize( rootCriteria.getFetchSize() );
 
 		Iterator iter = rootCriteria.getLockModes().entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			final Criteria subcriteria = getAliasedCriteria( ( String ) me.getKey() );
 			lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), (LockMode)me.getValue() );
 		}
 		List values = new ArrayList();
 		List types = new ArrayList();
 		iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria subcriteria = ( CriteriaImpl.Subcriteria ) iter.next();
 			LockMode lm = subcriteria.getLockMode();
 			if ( lm != null ) {
 				lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), lm );
 			}
 			if ( subcriteria.getWithClause() != null )
 			{
 				TypedValue[] tv = subcriteria.getWithClause().getTypedValues( subcriteria, this );
 				for ( int i = 0; i < tv.length; i++ ) {
 					values.add( tv[i].getValue() );
 					types.add( tv[i].getType() );
 				}
 			}
 		}
 
 		// Type and value gathering for the WHERE clause needs to come AFTER lock mode gathering,
 		// because the lock mode gathering loop now contains join clauses which can contain
 		// parameter bindings (as in the HQL WITH clause).
 		iter = rootCriteria.iterateExpressionEntries();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.CriterionEntry ce = ( CriteriaImpl.CriterionEntry ) iter.next();
 			TypedValue[] tv = ce.getCriterion().getTypedValues( ce.getCriteria(), this );
 			for ( int i = 0; i < tv.length; i++ ) {
 				values.add( tv[i].getValue() );
 				types.add( tv[i].getType() );
 			}
 		}
 
 		Object[] valueArray = values.toArray();
 		Type[] typeArray = ArrayHelper.toTypeArray( types );
 		return new QueryParameters(
 				typeArray,
 		        valueArray,
 		        lockOptions,
 		        selection,
 		        rootCriteria.isReadOnlyInitialized(),
 		        ( rootCriteria.isReadOnlyInitialized() ? rootCriteria.isReadOnly() : false ),
 		        rootCriteria.getCacheable(),
 		        rootCriteria.getCacheRegion(),
 		        rootCriteria.getComment(),
 		        rootCriteria.isLookupByNaturalKey(),
 		        rootCriteria.getResultTransformer()
 		);
 	}
 
 	public boolean hasProjection() {
 		return rootCriteria.getProjection() != null;
 	}
 
 	public String getGroupBy() {
 		if ( rootCriteria.getProjection().isGrouped() ) {
 			return rootCriteria.getProjection()
 					.toGroupSqlString( rootCriteria.getProjectionCriteria(), this );
 		}
 		else {
 			return "";
 		}
 	}
 
 	public String getSelect() {
 		return rootCriteria.getProjection().toSqlString(
 				rootCriteria.getProjectionCriteria(),
 		        0,
 		        this
 		);
 	}
 
 	/* package-protected */
 	Type getResultType(Criteria criteria) {
 		return getFactory().getTypeResolver().getTypeFactory().manyToOne( getEntityName( criteria ) );
 	}
 
 	public Type[] getProjectedTypes() {
 		return rootCriteria.getProjection().getTypes( rootCriteria, this );
 	}
 
 	public String[] getProjectedColumnAliases() {
 		return rootCriteria.getProjection() instanceof EnhancedProjection ?
 				( ( EnhancedProjection ) rootCriteria.getProjection() ).getColumnAliases( 0, rootCriteria, this ) :
 				rootCriteria.getProjection().getColumnAliases( 0 );
 	}
 
 	public String[] getProjectedAliases() {
 		return rootCriteria.getProjection().getAliases();
 	}
 
 	public String getWhereCondition() {
 		StringBuilder condition = new StringBuilder( 30 );
 		Iterator criterionIterator = rootCriteria.iterateExpressionEntries();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.CriterionEntry entry = ( CriteriaImpl.CriterionEntry ) criterionIterator.next();
 			String sqlString = entry.getCriterion().toSqlString( entry.getCriteria(), this );
 			condition.append( sqlString );
 			if ( criterionIterator.hasNext() ) {
 				condition.append( " and " );
 			}
 		}
 		return condition.toString();
 	}
 
 	public String getOrderBy() {
 		StringBuilder orderBy = new StringBuilder( 30 );
 		Iterator criterionIterator = rootCriteria.iterateOrderings();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.OrderEntry oe = ( CriteriaImpl.OrderEntry ) criterionIterator.next();
 			orderBy.append( oe.getOrder().toSqlString( oe.getCriteria(), this ) );
 			if ( criterionIterator.hasNext() ) {
 				orderBy.append( ", " );
 			}
 		}
 		return orderBy.toString();
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return sessionFactory;
 	}
 
 	public String getSQLAlias(Criteria criteria) {
 		return ( String ) criteriaSQLAliasMap.get( criteria );
 	}
 
 	public String getEntityName(Criteria criteria) {
 		final CriteriaInfoProvider infoProvider = ( CriteriaInfoProvider ) criteriaInfoMap.get( criteria );
 		return infoProvider != null ? infoProvider.getName() : null;
 	}
 
 	public String getColumn(Criteria criteria, String propertyName) {
 		String[] cols = getColumns( propertyName, criteria );
 		if ( cols.length != 1 ) {
 			throw new QueryException( "property does not map to a single column: " + propertyName );
 		}
 		return cols[0];
 	}
 
 	/**
 	 * Get the names of the columns constrained
 	 * by this criterion.
 	 */
 	public String[] getColumnsUsingProjection(
 			Criteria subcriteria,
 	        String propertyName) throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		String[] projectionColumns = null;
 		if ( projection != null ) {
 			projectionColumns = ( projection instanceof EnhancedProjection ?
 					( ( EnhancedProjection ) projection ).getColumnAliases( propertyName, 0, rootCriteria, this ) :
 					projection.getColumnAliases( propertyName, 0 )
 			);
 		}
 		if ( projectionColumns == null ) {
 			//it does not refer to an alias of a projection,
 			//look for a property
 			try {
 				return getColumns( propertyName, subcriteria );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getColumnsUsingProjection( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			//it refers to an alias of a projection
 			return projectionColumns;
 		}
 	}
 
-	public String[] getIdentifierColumns(Criteria subcriteria) {
+	public String[] getIdentifierColumns(Criteria criteria) {
 		String[] idcols =
-				( ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) ) ).getIdentifierColumnNames();
-		return StringHelper.qualify( getSQLAlias( subcriteria ), idcols );
+				( ( Loadable ) getPropertyMapping( getEntityName( criteria ) ) ).getIdentifierColumnNames();
+		return StringHelper.qualify( getSQLAlias( criteria ), idcols );
 	}
 
-	public Type getIdentifierType(Criteria subcriteria) {
-		return ( ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) ) ).getIdentifierType();
+	public Type getIdentifierType(Criteria criteria) {
+		return ( ( Loadable ) getPropertyMapping( getEntityName( criteria ) ) ).getIdentifierType();
 	}
 
-	public TypedValue getTypedIdentifierValue(Criteria subcriteria, Object value) {
-		final Loadable loadable = ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) );
+	public TypedValue getTypedIdentifierValue(Criteria criteria, Object value) {
+		final Loadable loadable = ( Loadable ) getPropertyMapping( getEntityName( criteria ) );
 		return new TypedValue(
 				loadable.getIdentifierType(),
 		        value,
 		        EntityMode.POJO
 		);
 	}
 
 	public String[] getColumns(
 			String propertyName,
 	        Criteria subcriteria) throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toColumns(
 						getSQLAlias( subcriteria, propertyName ),
 				        getPropertyName( propertyName )
 				);
 	}
 
 	/**
 	 * Get the names of the columns mapped by a property path; if the
 	 * property path is not found in subcriteria, try the "outer" query.
 	 * Projection aliases are ignored.
 	 */
 	public String[] findColumns(String propertyName, Criteria subcriteria )
 	throws HibernateException {
 		try {
 			return getColumns( propertyName, subcriteria );
 		}
 		catch ( HibernateException he ) {
 			//not found in inner query, try the outer query
 			if ( outerQueryTranslator != null ) {
 				return outerQueryTranslator.findColumns( propertyName, subcriteria );
 			}
 			else {
 				throw he;
 			}
 		}
 	}
 
 	public Type getTypeUsingProjection(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		Type[] projectionTypes = projection == null ?
 		                         null :
 		                         projection.getTypes( propertyName, subcriteria, this );
 
 		if ( projectionTypes == null ) {
 			try {
 				//it does not refer to an alias of a projection,
 				//look for a property
 				return getType( subcriteria, propertyName );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getType( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			if ( projectionTypes.length != 1 ) {
 				//should never happen, i think
 				throw new QueryException( "not a single-length projection: " + propertyName );
 			}
 			return projectionTypes[0];
 		}
 	}
 
 	public Type getType(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toType( getPropertyName( propertyName ) );
 	}
 
 	/**
 	 * Get the a typed value for the given property value.
 	 */
 	public TypedValue getTypedValue(Criteria subcriteria, String propertyName, Object value)
 			throws HibernateException {
 		// Detect discriminator values...
 		if ( value instanceof Class ) {
 			Class entityClass = ( Class ) value;
 			Queryable q = SessionFactoryHelper.findQueryableUsingImports( sessionFactory, entityClass.getName() );
 			if ( q != null ) {
 				Type type = q.getDiscriminatorType();
 				String stringValue = q.getDiscriminatorSQLValue();
 				if (stringValue != null && stringValue.length() > 2
 						&& stringValue.startsWith("'")
 						&& stringValue.endsWith("'")) { // remove the single
 														// quotes
 					stringValue = stringValue.substring(1,
 							stringValue.length() - 1);
 				}
 				
 				// Convert the string value into the proper type.
 				if ( type instanceof StringRepresentableType ) {
 					StringRepresentableType nullableType = (StringRepresentableType) type;
 					value = nullableType.fromStringValue( stringValue );
 				}
 				else {
 					throw new QueryException( "Unsupported discriminator type " + type );
 				}
-				return new TypedValue(
-						type,
-				        value,
-				        EntityMode.POJO
-				);
+				return new TypedValue( type, value );
 			}
 		}
 		// Otherwise, this is an ordinary value.
-		return new TypedValue(
-				getTypeUsingProjection( subcriteria, propertyName ),
-		        value,
-		        EntityMode.POJO
-		);
+		return new TypedValue( getTypeUsingProjection( subcriteria, propertyName ), value );
 	}
 
 	private PropertyMapping getPropertyMapping(String entityName)
 			throws MappingException {
 		CriteriaInfoProvider info = ( CriteriaInfoProvider )nameCriteriaInfoMap.get(entityName);
 		if (info==null) {
 			throw new HibernateException( "Unknown entity: " + entityName );
 		}
 		return info.getPropertyMapping();
 	}
 
 	//TODO: use these in methods above
 
 	public String getEntityName(Criteria subcriteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return getEntityName( crit );
 			}
 		}
 		return getEntityName( subcriteria );
 	}
 
 	public String getSQLAlias(Criteria criteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria subcriteria = getAliasedCriteria( root );
 			if ( subcriteria != null ) {
 				return getSQLAlias( subcriteria );
 			}
 		}
 		return getSQLAlias( criteria );
 	}
 
 	public String getPropertyName(String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return propertyName.substring( root.length() + 1 );
 			}
 		}
 		return propertyName;
 	}
 
 	public String getWithClause(String path)
 	{
 		final Criterion crit = (Criterion)this.withClauseMap.get(path);
 		return crit == null ? null : crit.toSqlString(getCriteria(path), this);
 	}
 
 	public boolean hasRestriction(String path)
 	{
 		final CriteriaImpl.Subcriteria crit = ( CriteriaImpl.Subcriteria ) getCriteria( path );
 		return crit == null ? false : crit.hasRestriction();
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java
index d5e1d8cd1c..83ee2327ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/JoinFragment.java
@@ -1,125 +1,209 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * An abstract SQL join fragment renderer
  *
  * @author Gavin King
  */
 public abstract class JoinFragment {
+	/**
+	 * Specifies an inner join.
+	 *
+	 * @deprecated use {@link JoinType#INNER_JOIN} instead.
+	 */
+	@Deprecated
+	public static final int INNER_JOIN = JoinType.INNER_JOIN.getJoinTypeValue();
 
+	/**
+	 * Specifies a full join
+	 *
+	 * @deprecated use {@link JoinType#FULL_JOIN} instead.
+	 */
+	@Deprecated
+	@SuppressWarnings("UnusedDeclaration")
+	public static final int FULL_JOIN = JoinType.FULL_JOIN.getJoinTypeValue();
+
+	/**
+	 * Specifies a left join.
+	 *
+	 * @deprecated use {@link JoinType#LEFT_OUTER_JOIN} instead.
+	 */
+	@Deprecated
+	public static final int LEFT_OUTER_JOIN = JoinType.LEFT_OUTER_JOIN.getJoinTypeValue();
+
+	/**
+	 * Specifies a right join.
+	 *
+	 * @deprecated use {@link JoinType#RIGHT_OUTER_JOIN} instead.
+	 */
+	@Deprecated
+	@SuppressWarnings("UnusedDeclaration")
+	public static final int RIGHT_OUTER_JOIN = JoinType.RIGHT_OUTER_JOIN.getJoinTypeValue();
+
+
+	private boolean hasFilterCondition;
+	private boolean hasThetaJoins;
+
+
+	/**
+	 * Adds a join.
+	 *
+	 * @param tableName The name of the table to be joined
+	 * @param alias The alias to apply to the joined table
+	 * @param fkColumns The names of the columns which reference the joined table
+	 * @param pkColumns The columns in the joined table being referenced
+	 * @param joinType The type of join
+	 */
 	public abstract void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType);
 
+	/**
+	 * Adds a join, with an additional ON clause fragment
+	 *
+	 * @param tableName The name of the table to be joined
+	 * @param alias The alias to apply to the joined table
+	 * @param fkColumns The names of the columns which reference the joined table
+	 * @param pkColumns The columns in the joined table being referenced
+	 * @param joinType The type of join
+	 * @param on The additional ON fragment
+	 */
 	public abstract void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType, String on);
 
+	/**
+	 * Adds a cross join to the specified table.
+	 *
+	 * @param tableName The name of the table to be joined
+	 * @param alias The alias to apply to the joined table
+	 */
 	public abstract void addCrossJoin(String tableName, String alias);
 
+	/**
+	 * Free-form form of adding theta-style joins taking the necessary FROM and WHERE clause fragments
+	 *
+	 * @param fromFragment The FROM clause fragment
+	 * @param whereFragment The WHERE clause fragment
+	 */
 	public abstract void addJoins(String fromFragment, String whereFragment);
 
+	/**
+	 * Render this fragment to its FROM clause portion
+	 *
+	 * @return The FROM clause portion of this fragment
+	 */
 	public abstract String toFromFragmentString();
 
+	/**
+	 * Render this fragment to its WHERE clause portion
+	 *
+	 * @return The WHERE clause portion of this fragment
+	 */
 	public abstract String toWhereFragmentString();
 
-	// --Commented out by Inspection (12/4/04 9:10 AM): public abstract void addCondition(String alias, String[] columns, String condition);
-	public abstract void addCondition(String alias, String[] fkColumns, String[] pkColumns);
-
-	public abstract boolean addCondition(String condition);
-	// --Commented out by Inspection (12/4/04 9:10 AM): public abstract void addFromFragmentString(String fromFragmentString);
-
-	public abstract JoinFragment copy();
-
 	/**
-	 * @deprecated use {@link JoinType#INNER_JOIN} instead.
+	 * Adds a condition to the join fragment.
+	 *
+	 * @param alias The alias of the joined table
+	 * @param fkColumns The names of the columns which reference the joined table
+	 * @param pkColumns The columns in the joined table being referenced
 	 */
-	@Deprecated
-	public static final int INNER_JOIN = 0;
+	public abstract void addCondition(String alias, String[] fkColumns, String[] pkColumns);
+
 	/**
-	 * @deprecated use {@link JoinType#FULL_JOIN} instead.
+	 * Adds a free-form condition fragment
+	 *
+	 * @param condition The fragment
+	 *
+	 * @return {@code true} if the condition was added
 	 */
-	@Deprecated
-	public static final int FULL_JOIN = 4;
+	public abstract boolean addCondition(String condition);
+
 	/**
-	 * @deprecated use {@link JoinType#LEFT_OUTER_JOIN} instead.
+	 * Make a copy.
+	 *
+	 * @return The copy.
 	 */
-	@Deprecated
-	public static final int LEFT_OUTER_JOIN = 1;
+	public abstract JoinFragment copy();
+
 	/**
-	 * @deprecated use {@link JoinType#RIGHT_OUTER_JOIN} instead.
+	 * Adds another join fragment to this one.
+	 *
+	 * @param ojf The other join fragment
 	 */
-	@Deprecated
-	public static final int RIGHT_OUTER_JOIN = 2;
-	private boolean hasFilterCondition = false;
-	private boolean hasThetaJoins = false;
-
 	public void addFragment(JoinFragment ojf) {
 		if ( ojf.hasThetaJoins() ) {
 			hasThetaJoins = true;
 		}
 		addJoins( ojf.toFromFragmentString(), ojf.toWhereFragmentString() );
 	}
 
 	/**
 	 * Appends the 'on' condition to the buffer, returning true if the condition was added.
 	 * Returns false if the 'on' condition was empty.
 	 *
 	 * @param buffer The buffer to append the 'on' condition to.
 	 * @param on     The 'on' condition.
 	 * @return Returns true if the condition was added, false if the condition was already in 'on' string.
 	 */
 	protected boolean addCondition(StringBuilder buffer, String on) {
 		if ( StringHelper.isNotEmpty( on ) ) {
-			if ( !on.startsWith( " and" ) ) buffer.append( " and " );
+			if ( !on.startsWith( " and" ) ) {
+				buffer.append( " and " );
+			}
 			buffer.append( on );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * True if the where fragment is from a filter condition.
 	 *
 	 * @return True if the where fragment is from a filter condition.
 	 */
 	public boolean hasFilterCondition() {
 		return hasFilterCondition;
 	}
 
 	public void setHasFilterCondition(boolean b) {
 		this.hasFilterCondition = b;
 	}
 
+	/**
+	 * Determine if the join fragment contained any theta-joins.
+	 *
+	 * @return {@code true} if the fragment contained theta joins
+	 */
 	public boolean hasThetaJoins() {
 		return hasThetaJoins;
 	}
 
 	public void setHasThetaJoins(boolean hasThetaJoins) {
 		this.hasThetaJoins = hasThetaJoins;
 	}
 }
