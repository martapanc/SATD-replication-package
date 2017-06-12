diff --git a/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
index ef7469aa8f..bde9a8c24a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
@@ -1,155 +1,129 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.loader.entity;
 
 import java.io.Serializable;
-import java.util.Iterator;
+import java.sql.SQLException;
+import java.util.Arrays;
 import java.util.List;
 
-import org.hibernate.LockMode;
+import org.jboss.logging.Logger;
+
 import org.hibernate.LockOptions;
-import org.hibernate.MappingException;
-import org.hibernate.engine.spi.LoadQueryInfluencers;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.Loader;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
- * "Batch" loads entities, using multiple primary key values in the
- * SQL <tt>where</tt> clause.
+ * The base contract for loaders capable of performing batch-fetch loading of entities using multiple primary key
+ * values in the SQL <tt>WHERE</tt> clause.
  *
- * @see EntityLoader
  * @author Gavin King
+ * @author Steve Ebersole
+ *
+ * @see BatchingEntityLoaderBuilder
+ * @see UniqueEntityLoader
  */
-public class BatchingEntityLoader implements UniqueEntityLoader {
+public abstract class BatchingEntityLoader implements UniqueEntityLoader {
+	private static final Logger log = Logger.getLogger( BatchingEntityLoader.class );
 
-	private final Loader[] loaders;
-	private final int[] batchSizes;
 	private final EntityPersister persister;
-	private final Type idType;
 
-	public BatchingEntityLoader(EntityPersister persister, int[] batchSizes, Loader[] loaders) {
-		this.batchSizes = batchSizes;
-		this.loaders = loaders;
+	public BatchingEntityLoader(EntityPersister persister) {
 		this.persister = persister;
-		idType = persister.getIdentifierType();
 	}
 
-	private Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
-		// get the right object from the list ... would it be easier to just call getEntity() ??
-		Iterator iter = results.iterator();
-		while ( iter.hasNext() ) {
-			Object obj = iter.next();
-			final boolean equal = idType.isEqual(
-					id,
-					session.getContextEntityIdentifier(obj),
-					session.getFactory()
-			);
-			if ( equal ) return obj;
-		}
-		return null;
+	public EntityPersister persister() {
+		return persister;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
+	@Deprecated
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
-		// this form is deprecated!
 		return load( id, optionalObject, session, LockOptions.NONE );
 	}
 
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
-		Serializable[] batch = session.getPersistenceContext()
-				.getBatchFetchQueue()
-				.getEntityBatch( persister, id, batchSizes[0], persister.getEntityMode() );
+	protected QueryParameters buildQueryParameters(
+			Serializable id,
+			Serializable[] ids,
+			Object optionalObject,
+			LockOptions lockOptions) {
+		Type[] types = new Type[ids.length];
+		Arrays.fill( types, persister().getIdentifierType() );
+
+		QueryParameters qp = new QueryParameters();
+		qp.setPositionalParameterTypes( types );
+		qp.setPositionalParameterValues( ids );
+		qp.setOptionalObject( optionalObject );
+		qp.setOptionalEntityName( persister().getEntityName() );
+		qp.setOptionalId( id );
+		qp.setLockOptions( lockOptions );
+		return qp;
+	}
 
-		for ( int i=0; i<batchSizes.length-1; i++) {
-			final int smallBatchSize = batchSizes[i];
-			if ( batch[smallBatchSize-1]!=null ) {
-				Serializable[] smallBatch = new Serializable[smallBatchSize];
-				System.arraycopy(batch, 0, smallBatch, 0, smallBatchSize);
-				final List results = loaders[i].loadEntityBatch(
-						session,
-						smallBatch,
-						idType,
-						optionalObject,
-						persister.getEntityName(),
-						id,
-						persister,
-						lockOptions
-				);
-				return getObjectFromList(results, id, session); //EARLY EXIT
+	protected Object getObjectFromList(List results, Serializable id, SessionImplementor session) {
+		for ( Object obj : results ) {
+			final boolean equal = persister.getIdentifierType().isEqual(
+					id,
+					session.getContextEntityIdentifier( obj ),
+					session.getFactory()
+			);
+			if ( equal ) {
+				return obj;
 			}
 		}
-
-		return ( (UniqueEntityLoader) loaders[batchSizes.length-1] ).load(id, optionalObject, session);
-
+		return null;
 	}
 
-	public static UniqueEntityLoader createBatchingEntityLoader(
-		final OuterJoinLoadable persister,
-		final int maxBatchSize,
-		final LockMode lockMode,
-		final SessionFactoryImplementor factory,
-		final LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
-
-		if ( maxBatchSize>1 ) {
-			int[] batchSizesToCreate = ArrayHelper.getBatchSizes(maxBatchSize);
-			Loader[] loadersToCreate = new Loader[ batchSizesToCreate.length ];
-			for ( int i=0; i<batchSizesToCreate.length; i++ ) {
-				loadersToCreate[i] = new EntityLoader(persister, batchSizesToCreate[i], lockMode, factory, loadQueryInfluencers);
-			}
-			return new BatchingEntityLoader(persister, batchSizesToCreate, loadersToCreate);
-		}
-		else {
-			return new EntityLoader(persister, lockMode, factory, loadQueryInfluencers);
+	protected Object doBatchLoad(
+			Serializable id,
+			Loader loaderToUse,
+			SessionImplementor session,
+			Serializable[] ids,
+			Object optionalObject,
+			LockOptions lockOptions) {
+		if ( log.isDebugEnabled() ) {
+			log.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, session.getFactory() ) );
 		}
-	}
 
-	public static UniqueEntityLoader createBatchingEntityLoader(
-		final OuterJoinLoadable persister,
-		final int maxBatchSize,
-		final LockOptions lockOptions,
-		final SessionFactoryImplementor factory,
-		final LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
+		QueryParameters qp = buildQueryParameters( id, ids, optionalObject, lockOptions );
 
-		if ( maxBatchSize>1 ) {
-			int[] batchSizesToCreate = ArrayHelper.getBatchSizes(maxBatchSize);
-			Loader[] loadersToCreate = new Loader[ batchSizesToCreate.length ];
-			for ( int i=0; i<batchSizesToCreate.length; i++ ) {
-				loadersToCreate[i] = new EntityLoader(persister, batchSizesToCreate[i], lockOptions, factory, loadQueryInfluencers);
-			}
-			return new BatchingEntityLoader(persister, batchSizesToCreate, loadersToCreate);
+		try {
+			final List results = loaderToUse.doQueryAndInitializeNonLazyCollections( session, qp, false );
+			log.debug( "Done entity batch load" );
+			return getObjectFromList(results, id, session);
 		}
-		else {
-			return new EntityLoader(persister, lockOptions, factory, loadQueryInfluencers);
+		catch ( SQLException sqle ) {
+			throw session.getFactory().getSQLExceptionHelper().convert(
+					sqle,
+					"could not load an entity batch: " + MessageHelper.infoString( persister(), ids, session.getFactory() ),
+					loaderToUse.getSQLString()
+			);
 		}
 	}
 
 }
