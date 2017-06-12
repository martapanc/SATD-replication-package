diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
index f78d84e620..a28ecf6e79 100755
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
@@ -1,54 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.internal.AbstractQueryImpl;
+import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.collection.CollectionInitializer;
+import org.hibernate.query.spi.NativeQueryImplementor;
 
 /**
  * A wrapper around a named query.
  *
  * @author Gavin King
  */
 public final class NamedQueryCollectionInitializer implements CollectionInitializer {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( NamedQueryCollectionInitializer.class );
 
 	private final String queryName;
 	private final CollectionPersister persister;
 
 	public NamedQueryCollectionInitializer(String queryName, CollectionPersister persister) {
 		super();
 		this.queryName = queryName;
 		this.persister = persister;
 	}
 
-	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
+	public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
 		LOG.debugf( "Initializing collection: %s using named query: %s", persister.getRole(), queryName );
 
-		//TODO: is there a more elegant way than downcasting?
-		AbstractQueryImpl query = (AbstractQueryImpl) session.getNamedSQLQuery( queryName );
-		if ( query.getNamedParameters().length > 0 ) {
-			query.setParameter(
-					query.getNamedParameters()[0],
+		NativeQueryImplementor nativeQuery = session.getNamedNativeQuery( queryName );
+
+		if ( nativeQuery.getParameterMetadata().hasNamedParameters() ) {
+			nativeQuery.setParameter(
+					nativeQuery.getParameterMetadata().getNamedParameterNames().iterator().next(),
 					key,
 					persister.getKeyType()
 			);
 		}
 		else {
-			query.setParameter( 0, key, persister.getKeyType() );
+			nativeQuery.setParameter( 0, key, persister.getKeyType() );
 		}
 
-		query.setCollectionKey( key ).setFlushMode( FlushMode.MANUAL ).list();
+		nativeQuery.setCollectionKey( key ).setFlushMode( FlushMode.MANUAL ).list();
 	}
 }
