diff --git a/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/AbstractLoadPlanBasedCollectionInitializer.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/AbstractLoadPlanBasedCollectionInitializer.java
index e894d99462..71a3bf41e4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/AbstractLoadPlanBasedCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/AbstractLoadPlanBasedCollectionInitializer.java
@@ -1,147 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.loader.collection.plan;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.collection.CollectionInitializer;
-import org.hibernate.loader.plan2.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.spi.MetamodelDrivenLoadPlanBuilder;
-import org.hibernate.loader.plan2.exec.internal.AbstractLoadPlanBasedLoader;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.exec.spi.BasicCollectionLoadQueryDetails;
-import org.hibernate.loader.plan2.exec.spi.CollectionLoadQueryDetails;
-import org.hibernate.loader.plan2.exec.spi.OneToManyLoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
+import org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.exec.spi.BasicCollectionLoadQueryDetails;
+import org.hibernate.loader.plan.exec.spi.CollectionLoadQueryDetails;
+import org.hibernate.loader.plan.exec.spi.OneToManyLoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * A CollectionInitializer implementation based on using LoadPlans
  *
  * @author Gail Badner
  */
 public abstract class AbstractLoadPlanBasedCollectionInitializer
 		extends AbstractLoadPlanBasedLoader  implements CollectionInitializer {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( AbstractLoadPlanBasedCollectionInitializer.class );
 
 	private final QueryableCollection collectionPersister;
 	private final LoadPlan plan;
 	private final CollectionLoadQueryDetails staticLoadQuery;
 
 	public AbstractLoadPlanBasedCollectionInitializer(
 			QueryableCollection collectionPersister,
 			QueryBuildingParameters buildingParameters) {
 		super( collectionPersister.getFactory() );
 		this.collectionPersister = collectionPersister;
 
 		final FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy =
 				new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
 						collectionPersister.getFactory(),
 						buildingParameters.getQueryInfluencers(),
 						buildingParameters.getLockMode() != null
 								? buildingParameters.getLockMode()
 								: buildingParameters.getLockOptions().getLockMode()
 		);
 
 		this.plan = MetamodelDrivenLoadPlanBuilder.buildRootCollectionLoadPlan( strategy, collectionPersister );
 		this.staticLoadQuery = collectionPersister.isOneToMany() ?
 				OneToManyLoadQueryDetails.makeForBatching(
 						plan,
 						buildingParameters,
 						collectionPersister.getFactory()
 				) :
 				BasicCollectionLoadQueryDetails.makeForBatching(
 						plan,
 						buildingParameters,
 						collectionPersister.getFactory()
 				);
 	}
 
 	@Override
 	public void initialize(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		if ( log.isDebugEnabled() ) {
 			log.debugf( "Loading collection: %s",
 					MessageHelper.collectionInfoString( collectionPersister, id, getFactory() ) );
 		}
 
 
 		final Serializable[] ids = new Serializable[]{id};
 		try {
 			final QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[]{ collectionPersister.getKeyType() } );
 			qp.setPositionalParameterValues( ids );
 			qp.setCollectionKeys( ids );
 
 			executeLoad(
 					session,
 					qp,
 					staticLoadQuery,
 					true,
 					null
 
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 							MessageHelper.collectionInfoString( collectionPersister, id, getFactory() ),
 					staticLoadQuery.getSqlStatement()
 			);
 		}
 
 		log.debug( "Done loading collection" );
 	}
 
 	protected QueryableCollection collectionPersister() {
 		return collectionPersister;
 	}
 
 	@Override
 	protected CollectionLoadQueryDetails getStaticLoadQuery() {
 		return staticLoadQuery;
 	}
 
 	@Override
 	protected int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	@Override
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/CollectionLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/CollectionLoader.java
index 1af8e634ef..6cb72d891f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/CollectionLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/plan/CollectionLoader.java
@@ -1,129 +1,129 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.loader.collection.plan;
 
 import java.sql.ResultSet;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.Type;
 
 /**
  * Superclass for loaders that initialize collections
  * 
  * @see org.hibernate.loader.collection.OneToManyLoader
  * @see org.hibernate.loader.collection.BasicCollectionLoader
  * @author Gavin King
  * @author Gail Badner
  */
 public class CollectionLoader extends AbstractLoadPlanBasedCollectionInitializer {
 	private static final Logger log = CoreLogging.logger( CollectionLoader.class );
 
 	public static Builder forCollection(QueryableCollection collectionPersister) {
 		return new Builder( collectionPersister );
 	}
 
 	@Override
 	protected int[] getNamedParameterLocs(String name) {
 		return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	protected void autoDiscoverTypes(ResultSet rs) {
 		//To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	protected static class Builder {
 		private final QueryableCollection collectionPersister;
 		private int batchSize = 1;
 		private LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
 
 		private Builder(QueryableCollection collectionPersister) {
 			this.collectionPersister = collectionPersister;
 		}
 
 		public Builder withBatchSize(int batchSize) {
 			this.batchSize = batchSize;
 			return this;
 		}
 
 		public Builder withInfluencers(LoadQueryInfluencers influencers) {
 			this.influencers = influencers;
 			return this;
 		}
 
 		public CollectionLoader byKey() {
 			final QueryBuildingParameters buildingParameters = new QueryBuildingParameters() {
 				@Override
 				public LoadQueryInfluencers getQueryInfluencers() {
 					return influencers;
 				}
 
 				@Override
 				public int getBatchSize() {
 					return batchSize;
 				}
 
 				@Override
 				public LockMode getLockMode() {
 					return LockMode.NONE;
 				}
 
 				@Override
 				public LockOptions getLockOptions() {
 					return null;
 				}
 			};
 			return new CollectionLoader( collectionPersister, buildingParameters ) ;
 		}
 	}
 
 	public CollectionLoader(
 			QueryableCollection collectionPersister,
 			QueryBuildingParameters buildingParameters) {
 		super( collectionPersister, buildingParameters );
 		if ( log.isDebugEnabled() ) {
 			log.debugf(
 					"Static select for collection %s: %s",
 					collectionPersister.getRole(),
 					getStaticLoadQuery().getSqlStatement()
 			);
 		}
 	}
 
 	protected Type getKeyType() {
 		return collectionPersister().getKeyType();
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + collectionPersister().getRole() + ')';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java
index ceccedba77..1ef488745a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java
@@ -1,305 +1,305 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.loader.entity.plan;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.entity.UniqueEntityLoader;
-import org.hibernate.loader.plan2.build.internal.FetchGraphLoadPlanBuildingStrategy;
-import org.hibernate.loader.plan2.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.internal.LoadGraphLoadPlanBuildingStrategy;
-import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.spi.MetamodelDrivenLoadPlanBuilder;
-import org.hibernate.loader.plan2.exec.internal.AbstractLoadPlanBasedLoader;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.exec.spi.EntityLoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.build.internal.FetchGraphLoadPlanBuildingStrategy;
+import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.internal.LoadGraphLoadPlanBuildingStrategy;
+import org.hibernate.loader.plan.build.spi.LoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
+import org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A UniqueEntityLoader implementation based on using LoadPlans
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractLoadPlanBasedEntityLoader extends AbstractLoadPlanBasedLoader implements UniqueEntityLoader {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( AbstractLoadPlanBasedEntityLoader.class );
 
 	private final OuterJoinLoadable entityPersister;
 	private final Type uniqueKeyType;
 	private final String entityName;
 
 	private final LoadPlan plan;
 	private final EntityLoadQueryDetails staticLoadQuery;
 
 	public AbstractLoadPlanBasedEntityLoader(
 			OuterJoinLoadable entityPersister,
 			SessionFactoryImplementor factory,
 			String[] uniqueKeyColumnNames,
 			Type uniqueKeyType,
 			QueryBuildingParameters buildingParameters) {
 		super( factory );
 		this.entityPersister = entityPersister;
 		this.uniqueKeyType = uniqueKeyType;
 		this.entityName = entityPersister.getEntityName();
 
 		final LoadPlanBuildingAssociationVisitationStrategy strategy;
 		if ( buildingParameters.getQueryInfluencers().getFetchGraph() != null ) {
 			strategy = new FetchGraphLoadPlanBuildingStrategy(
 					factory, buildingParameters.getQueryInfluencers(),buildingParameters.getLockMode()
 			);
 		}
 		else if ( buildingParameters.getQueryInfluencers().getLoadGraph() != null ) {
 			strategy = new LoadGraphLoadPlanBuildingStrategy(
 					factory, buildingParameters.getQueryInfluencers(),buildingParameters.getLockMode()
 			);
 		}
 		else {
 			strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
 					factory, buildingParameters.getQueryInfluencers(),buildingParameters.getLockMode()
 			);
 		}
 
 		this.plan = MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
 		this.staticLoadQuery = EntityLoadQueryDetails.makeForBatching(
 				plan,
 				uniqueKeyColumnNames,
 				buildingParameters,
 				factory
 		);
 	}
 
 	@Override
 	protected EntityLoadQueryDetails getStaticLoadQuery() {
 		return staticLoadQuery;
 	}
 
 	protected String getEntityName() {
 		return entityName;
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
 
 		if ( log.isDebugEnabled() ) {
 			log.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 		}
 
 		final Type[] types = new Type[ids.length];
 		Arrays.fill( types, idType );
 		List result;
 		try {
 			final QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( types );
 			qp.setPositionalParameterValues( ids );
 			qp.setLockOptions( lockOptions );
 
 			result = executeLoad(
 					session,
 					qp,
 					staticLoadQuery,
 					false,
 					null
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity batch: " + MessageHelper.infoString( entityPersister, ids, getFactory() ),
 					staticLoadQuery.getSqlStatement()
 			);
 		}
 
 		log.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	@Override
 	@Deprecated
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session) throws HibernateException {
 		return load( id, optionalObject, session, LockOptions.NONE );
 	}
 
 	@Override
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
 
 		final Object result;
 		try {
 			final QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] { entityPersister.getIdentifierType() } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( entityPersister.getEntityName() );
 			qp.setOptionalId( id );
 			qp.setLockOptions( lockOptions );
 
 			final List results = executeLoad(
 					session,
 					qp,
 					staticLoadQuery,
 					false,
 					null
 			);
 			result = extractEntityResult( results );
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity: " + MessageHelper.infoString(
 							entityPersister,
 							id,
 							entityPersister.getIdentifierType(),
 							getFactory()
 					),
 					staticLoadQuery.getSqlStatement()
 			);
 		}
 
 		log.debugf( "Done entity load : %s#%s", getEntityName(), id );
 		return result;
 	}
 
 	protected Object extractEntityResult(List results) {
 		if ( results.size() == 0 ) {
 			return null;
 		}
 		else if ( results.size() == 1 ) {
 			return results.get( 0 );
 		}
 		else {
 			final Object row = results.get( 0 );
 			if ( row.getClass().isArray() ) {
 				// the logical type of the result list is List<Object[]>.  See if the contained
 				// array contains just one element, and return that if so
 				final Object[] rowArray = (Object[]) row;
 				if ( rowArray.length == 1 ) {
 					return rowArray[0];
 				}
 			}
 			else {
 				return row;
 			}
 		}
 
 		throw new HibernateException( "Unable to interpret given query results in terms of a load-entity query" );
 	}
 
 	protected Object doQueryAndLoadEntity(
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			EntityLoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException {
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 
 		try {
 			final List results = loadQueryDetails.getResultSetProcessor().extractResults(
 					wrapper.getResultSet(),
 					session,
 					queryParameters,
 					new NamedParameterContext() {
 						@Override
 						public int[] getNamedParameterLocations(String name) {
 							return AbstractLoadPlanBasedEntityLoader.this.getNamedParameterLocs( name );
 						}
 					},
 					returnProxies,
 					queryParameters.isReadOnly(),
 					forcedResultTransformer,
 					afterLoadActions
 			);
 
 
 			if ( results.size() == 0 ) {
 				return null;
 			}
 			else if ( results.size() == 1 ) {
 				return results.get( 0 );
 			}
 			else {
 				final Object row = results.get( 0 );
 				if ( row.getClass().isArray() ) {
 					// the logical type of the result list is List<Object[]>.  See if the contained
 					// array contains just one element, and return that if so
 					final Object[] rowArray = (Object[]) row;
 					if ( rowArray.length == 1 ) {
 						return rowArray[0];
 					}
 				}
 				else {
 					return row;
 				}
 			}
 
 			throw new HibernateException( "Unable to interpret given query results in terms of a load-entity query" );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( wrapper.getStatement() );
 		}
 	}
 
 	protected int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/EntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/EntityLoader.java
index 58f2e91d4f..62fb98cedc 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/EntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/EntityLoader.java
@@ -1,155 +1,155 @@
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
 package org.hibernate.loader.entity.plan;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.type.Type;
 
 /**
  * UniqueEntityLoader implementation that is the main functionality for LoadPlan-based Entity loading.
  * <p/>
  * Can handle batch-loading as well as non-pk, unique-key loading,
  * <p/>
  * Much is ultimately delegated to its superclass, AbstractLoadPlanBasedEntityLoader.  However:
  *
  * Loads an entity instance using outerjoin fetching to fetch associated entities.
  * <br>
  * The <tt>EntityPersister</tt> must implement <tt>Loadable</tt>. For other entities,
  * create a customized subclass of <tt>Loader</tt>.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class EntityLoader extends AbstractLoadPlanBasedEntityLoader  {
 	private static final Logger log = CoreLogging.logger( EntityLoader.class );
 
 	public static Builder forEntity(OuterJoinLoadable persister) {
 		return new Builder( persister );
 	}
 
 	public static class Builder {
 		private final OuterJoinLoadable persister;
 		private int batchSize = 1;
 		private LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
 		private LockMode lockMode = LockMode.NONE;
 		private LockOptions lockOptions;
 
 		public Builder(OuterJoinLoadable persister) {
 			this.persister = persister;
 		}
 
 		public Builder withBatchSize(int batchSize) {
 			this.batchSize = batchSize;
 			return this;
 		}
 
 		public Builder withInfluencers(LoadQueryInfluencers influencers) {
 			this.influencers = influencers;
 			return this;
 		}
 
 		public Builder withLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public Builder withLockOptions(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		public EntityLoader byPrimaryKey() {
 			return byUniqueKey( persister.getIdentifierColumnNames(), persister.getIdentifierType() );
 		}
 
 		public EntityLoader byUniqueKey(String[] keyColumnNames, Type keyType) {
 			return new EntityLoader(
 					persister.getFactory(),
 					persister,
 					keyColumnNames,
 					keyType,
 					new QueryBuildingParameters() {
 						@Override
 						public LoadQueryInfluencers getQueryInfluencers() {
 							return influencers;
 						}
 
 						@Override
 						public int getBatchSize() {
 							return batchSize;
 						}
 
 						@Override
 						public LockMode getLockMode() {
 							return lockMode;
 						}
 
 						@Override
 						public LockOptions getLockOptions() {
 							return lockOptions;
 						}
 					}
 			);
 		}
 	}
 
 	private EntityLoader(
 			SessionFactoryImplementor factory,
 			OuterJoinLoadable persister,
 			String[] uniqueKeyColumnNames,
 			Type uniqueKeyType,
 			QueryBuildingParameters buildingParameters) throws MappingException {
 		super( persister, factory, uniqueKeyColumnNames, uniqueKeyType, buildingParameters );
 		if ( log.isDebugEnabled() ) {
 			if ( buildingParameters.getLockOptions() != null ) {
 				log.debugf(
 						"Static select for entity %s [%s:%s]: %s",
 						getEntityName(),
 						buildingParameters.getLockOptions().getLockMode(),
 						buildingParameters.getLockOptions().getTimeOut(),
 						getStaticLoadQuery().getSqlStatement()
 				);
 			}
 			else if ( buildingParameters.getLockMode() != null ) {
 				log.debugf(
 						"Static select for entity %s [%s]: %s",
 						getEntityName(),
 						buildingParameters.getLockMode(),
 						getStaticLoadQuery().getSqlStatement()
 				);
 			}
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/AbstractEntityGraphVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/AbstractEntityGraphVisitationStrategy.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/AbstractEntityGraphVisitationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/AbstractEntityGraphVisitationStrategy.java
index a59d84f4f8..81ed6c0c10 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/AbstractEntityGraphVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/AbstractEntityGraphVisitationStrategy.java
@@ -1,359 +1,359 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
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
-package org.hibernate.loader.plan2.build.internal;
+package org.hibernate.loader.plan.build.internal;
 
 import java.util.ArrayDeque;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.AttributeNode;
 import javax.persistence.Subgraph;
 import javax.persistence.metamodel.Attribute;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.graph.spi.AttributeNodeImplementor;
 import org.hibernate.graph.spi.GraphNodeImplementor;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.build.spi.AbstractLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.build.spi.AbstractLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 
 /**
  * Abstract strategy of building loadplan based on entity graph.
  *
  * The problem we're resolving here is, we have TWO trees to walk (only entity loading here):
  * <ul>
  * <ol>entity metadata and its associations</ol>
  * <ol>entity graph and attribute nodes</ol>
  * </ul>
  *
  * And most time, the entity graph tree is partial of entity metadata tree.
  *
  * So, the idea here is, we walk the entity metadata tree, just as how we build the static loadplan from mappings,
  * and we try to match the node to entity graph ( and subgraph ), if there is a match, then the attribute is fetched,
  * it is not, then depends on which property is used to apply this entity graph.
  *
  * @author Strong Liu <stliu@hibernate.org>
  */
 public abstract class AbstractEntityGraphVisitationStrategy
 		extends AbstractLoadPlanBuildingAssociationVisitationStrategy {
 	private static final Logger LOG = CoreLogging.logger( AbstractEntityGraphVisitationStrategy.class );
 	/**
 	 * The JPA 2.1 SPEC's Entity Graph only defines _WHEN_ to load an attribute, it doesn't define _HOW_ to load it
 	 * So I'm here just making an assumption that when it is EAGER, then we use JOIN, and when it is LAZY, then we use SELECT.
 	 *
 	 * NOTE: this may be changed in the near further, though ATM I have no idea how this will be changed to :)
 	 * -- stliu
 	 */
 	protected static final FetchStrategy DEFAULT_EAGER = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 	protected static final FetchStrategy DEFAULT_LAZY = new FetchStrategy( FetchTiming.DELAYED, FetchStyle.SELECT );
 	protected final LoadQueryInfluencers loadQueryInfluencers;
 	protected final ArrayDeque<GraphNodeImplementor> graphStack = new ArrayDeque<GraphNodeImplementor>();
 	protected final ArrayDeque<AttributeNodeImplementor> attributeStack = new ArrayDeque<AttributeNodeImplementor>();
 	//the attribute nodes defined in the current graph node (entity graph or subgraph) we're working on
 	protected Map<String, AttributeNodeImplementor> attributeNodeImplementorMap = Collections.emptyMap();
 	private EntityReturn rootEntityReturn;
 	private final LockMode lockMode;
 
 	protected AbstractEntityGraphVisitationStrategy(
 			final SessionFactoryImplementor sessionFactory, final LoadQueryInfluencers loadQueryInfluencers,
 			final LockMode lockMode) {
 		super( sessionFactory );
 		this.loadQueryInfluencers = loadQueryInfluencers;
 		this.lockMode = lockMode;
 	}
 
 	@Override
 	public void start() {
 		super.start();
 		graphStack.addLast( getRootEntityGraph() );
 	}
 
 	@Override
 	public void finish() {
 		super.finish();
 		graphStack.removeLast();
 		//applying a little internal stack checking
 		if ( !graphStack.isEmpty() || !attributeStack.isEmpty() || !attributeNodeImplementorMap.isEmpty() ) {
 			throw new WalkingException( "Internal stack error" );
 		}
 	}
 
 	@Override
 	public void startingEntity(final EntityDefinition entityDefinition) {
 		//TODO check if the passed in entity definition is the same as the root entity graph (a.k.a they are came from same entity class)?
 		//this maybe the root entity graph or a sub graph.
 		attributeNodeImplementorMap = buildAttributeNodeMap();
 		super.startingEntity( entityDefinition );
 	}
 
 	/**
 	 * Build "name" -- "attribute node" map from the current entity graph we're visiting.
 	 */
 	protected Map<String, AttributeNodeImplementor> buildAttributeNodeMap() {
 		GraphNodeImplementor graphNode = graphStack.peekLast();
 		List<AttributeNodeImplementor<?>> attributeNodeImplementors = graphNode.attributeImplementorNodes();
 		Map<String, AttributeNodeImplementor> attributeNodeImplementorMap = attributeNodeImplementors.isEmpty() ? Collections
 				.<String, AttributeNodeImplementor>emptyMap() : new HashMap<String, AttributeNodeImplementor>(
 				attributeNodeImplementors.size()
 		);
 		for ( AttributeNodeImplementor attribute : attributeNodeImplementors ) {
 			attributeNodeImplementorMap.put( attribute.getAttributeName(), attribute );
 		}
 		return attributeNodeImplementorMap;
 	}
 
 	@Override
 	public void finishingEntity(final EntityDefinition entityDefinition) {
 		attributeNodeImplementorMap = Collections.emptyMap();
 		super.finishingEntity( entityDefinition );
 	}
 
 	/**
 	 * I'm using NULL-OBJECT pattern here, for attributes that not existing in the EntityGraph,
 	 * a predefined NULL-ATTRIBUTE-NODE is pushed to the stack.
 	 *
 	 * and for an not existing sub graph, a predefined NULL-SUBGRAPH is pushed to the stack.
 	 *
 	 * So, whenever we're start visiting an attribute, there will be a attribute node pushed to the attribute stack,
 	 * and a subgraph node pushed to the graph stack.
 	 *
 	 * when we're finish visiting an attribute, these two will be poped from each stack.
 	 */
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 		final String attrName = attributeDefinition.getName();
 		AttributeNodeImplementor attributeNode = NON_EXIST_ATTRIBUTE_NODE;
 		GraphNodeImplementor subGraphNode = NON_EXIST_SUBGRAPH_NODE;
 		//the attribute is in the EntityGraph, so, let's continue
 		if ( attributeNodeImplementorMap.containsKey( attrName ) ) {
 			attributeNode = attributeNodeImplementorMap.get( attrName );
 			//here we need to check if there is a subgraph (or sub key graph if it is an indexed attribute )
 			Map<Class, Subgraph> subGraphs = attributeNode.getSubgraphs();
 			Class javaType = attributeDefinition.getType().getReturnedClass();
 			if ( !subGraphs.isEmpty() && subGraphs.containsKey( javaType ) ) {
 				subGraphNode = (GraphNodeImplementor) subGraphs.get( javaType );
 			}
 
 		}
 		attributeStack.addLast( attributeNode );
 		graphStack.addLast( subGraphNode );
 		return super.startingAttribute( attributeDefinition );
 	}
 
 
 	@Override
 	public void finishingAttribute(final AttributeDefinition attributeDefinition) {
 		attributeStack.removeLast();
 		graphStack.removeLast();
 		super.finishingAttribute( attributeDefinition );
 	}
 
 	@Override
 	protected boolean handleAssociationAttribute(
 			final AssociationAttributeDefinition attributeDefinition) {
 		return super.handleAssociationAttribute( attributeDefinition );
 	}
 
 	@Override
 	protected boolean handleCompositeAttribute(
 			final AttributeDefinition attributeDefinition) {
 		return super.handleCompositeAttribute( attributeDefinition );
 	}
 
 
 	@Override
 	public void startingComposite(final CompositionDefinition compositionDefinition) {
 		super.startingComposite( compositionDefinition );
 	}
 
 
 	@Override
 	public void finishingComposite(final CompositionDefinition compositionDefinition) {
 		super.finishingComposite( compositionDefinition );
 	}
 
 
 	@Override
 	public void startingCollection(final CollectionDefinition collectionDefinition) {
 		super.startingCollection( collectionDefinition );
 	}
 
 	@Override
 	public void finishingCollection(final CollectionDefinition collectionDefinition) {
 		super.finishingCollection( collectionDefinition );
 	}
 
 
 	@Override
 	public void startingCollectionElements(
 			final CollectionElementDefinition elementDefinition) {
 		super.startingCollectionElements( elementDefinition );
 	}
 
 	@Override
 	public void finishingCollectionElements(
 			final CollectionElementDefinition elementDefinition) {
 		super.finishingCollectionElements( elementDefinition );
 	}
 
 
 	@Override
 	public void startingCollectionIndex(final CollectionIndexDefinition indexDefinition) {
 		AttributeNodeImplementor attributeNode = attributeStack.peekLast();
 		GraphNodeImplementor subGraphNode = NON_EXIST_SUBGRAPH_NODE;
 		Map<Class, Subgraph> subGraphs = attributeNode.getKeySubgraphs();
 		Class javaType = indexDefinition.getType().getReturnedClass();
 		if ( !subGraphs.isEmpty() && subGraphs.containsKey( javaType ) ) {
 			subGraphNode = (GraphNodeImplementor) subGraphs.get( javaType );
 		}
 		graphStack.addLast( subGraphNode );
 		super.startingCollectionIndex( indexDefinition );
 	}
 
 	@Override
 	public void finishingCollectionIndex(final CollectionIndexDefinition indexDefinition) {
 		super.finishingCollectionIndex( indexDefinition );
 		graphStack.removeLast();
 	}
 
 
 	@Override
 	protected boolean supportsRootCollectionReturns() {
 		return false; //entity graph doesn't support root collection.
 	}
 
 
 	@Override
 	protected void addRootReturn(final Return rootReturn) {
 		if ( this.rootEntityReturn != null ) {
 			throw new HibernateException( "Root return already identified" );
 		}
 		if ( !( rootReturn instanceof EntityReturn ) ) {
 			throw new HibernateException( "Load entity graph only supports EntityReturn" );
 		}
 		this.rootEntityReturn = (EntityReturn) rootReturn;
 	}
 
 	@Override
 	protected FetchStrategy determineFetchStrategy(
 			final AssociationAttributeDefinition attributeDefinition) {
 		return attributeStack.peekLast() != NON_EXIST_ATTRIBUTE_NODE ? DEFAULT_EAGER : resolveImplicitFetchStrategyFromEntityGraph(
 				attributeDefinition
 		);
 	}
 
 	protected abstract FetchStrategy resolveImplicitFetchStrategyFromEntityGraph(
 			final AssociationAttributeDefinition attributeDefinition);
 
 	protected FetchStrategy adjustJoinFetchIfNeeded(
 			AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy) {
 		if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		final Integer maxFetchDepth = sessionFactory().getSettings().getMaximumFetchDepth();
 		if ( maxFetchDepth != null && currentDepth() > maxFetchDepth ) {
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		if ( attributeDefinition.getType().isCollectionType() && isTooManyCollections() ) {
 			// todo : have this revert to batch or subselect fetching once "sql gen redesign" is in place
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		return fetchStrategy;
 	}
 
 
 	@Override
 	public LoadPlan buildLoadPlan() {
 		LOG.debug( "Building LoadPlan..." );
 		return new LoadPlanImpl( rootEntityReturn, getQuerySpaces() );
 	}
 
 	abstract protected GraphNodeImplementor getRootEntityGraph();
 
 	private static final AttributeNodeImplementor NON_EXIST_ATTRIBUTE_NODE = new AttributeNodeImplementor() {
 		@Override
 		public Attribute getAttribute() {
 			return null;
 		}
 
 		@Override
 		public AttributeNodeImplementor makeImmutableCopy() {
 			return this;
 		}
 
 		@Override
 		public String getAttributeName() {
 			return null;
 		}
 
 		@Override
 		public Map<Class, Subgraph> getSubgraphs() {
 			return Collections.emptyMap();
 		}
 
 		@Override
 		public Map<Class, Subgraph> getKeySubgraphs() {
 			return Collections.emptyMap();
 		}
 
 		@Override
 		public String toString() {
 			return "Mocked NON-EXIST attribute node";
 		}
 	};
 	private static final GraphNodeImplementor NON_EXIST_SUBGRAPH_NODE = new GraphNodeImplementor() {
 		@Override
 		public List<AttributeNodeImplementor<?>> attributeImplementorNodes() {
 			return Collections.EMPTY_LIST;
 		}
 
 		@Override
 		public List<AttributeNode<?>> attributeNodes() {
 			return Collections.EMPTY_LIST;
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CascadeStyleLoadPlanBuildingAssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/CascadeStyleLoadPlanBuildingAssociationVisitationStrategy.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CascadeStyleLoadPlanBuildingAssociationVisitationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/CascadeStyleLoadPlanBuildingAssociationVisitationStrategy.java
index e8a2da68a5..15f92f604e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CascadeStyleLoadPlanBuildingAssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/CascadeStyleLoadPlanBuildingAssociationVisitationStrategy.java
@@ -1,61 +1,61 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal;
+package org.hibernate.loader.plan.build.internal;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 
 /**
  * A LoadPlan building strategy for cascade processing; meaning, it builds the LoadPlan for loading related to
  * cascading a particular action across associations
  *
  * @author Steve Ebersole
  */
 public class CascadeStyleLoadPlanBuildingAssociationVisitationStrategy
 		extends FetchStyleLoadPlanBuildingAssociationVisitationStrategy {
 	private static final FetchStrategy EAGER = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 	private static final FetchStrategy DELAYED = new FetchStrategy( FetchTiming.DELAYED, FetchStyle.SELECT );
 
 	private final CascadingAction cascadeActionToMatch;
 
 	public CascadeStyleLoadPlanBuildingAssociationVisitationStrategy(
 			CascadingAction cascadeActionToMatch,
 			SessionFactoryImplementor sessionFactory,
 			LoadQueryInfluencers loadQueryInfluencers,
 			LockMode lockMode) {
 		super( sessionFactory, loadQueryInfluencers, lockMode );
 		this.cascadeActionToMatch = cascadeActionToMatch;
 	}
 
 	@Override
 	protected FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition) {
 		return attributeDefinition.determineCascadeStyle().doCascade( cascadeActionToMatch ) ? EAGER : DELAYED;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/FetchGraphLoadPlanBuildingStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/FetchGraphLoadPlanBuildingStrategy.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/FetchGraphLoadPlanBuildingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/FetchGraphLoadPlanBuildingStrategy.java
index 0b70f00be3..526128756c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/FetchGraphLoadPlanBuildingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/FetchGraphLoadPlanBuildingStrategy.java
@@ -1,61 +1,61 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
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
-package org.hibernate.loader.plan2.build.internal;
+package org.hibernate.loader.plan.build.internal;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.graph.spi.GraphNodeImplementor;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 
 /**
  * Loadplan building strategy for {@link javax.persistence.EntityGraph} is applied in {@code javax.persistence.fetchgraph} mode.
  *
  * @author Strong Liu <stliu@hibernate.org>
  */
 public class FetchGraphLoadPlanBuildingStrategy extends AbstractEntityGraphVisitationStrategy {
 	private final GraphNodeImplementor rootEntityGraph;
 
 	public FetchGraphLoadPlanBuildingStrategy(
 			final SessionFactoryImplementor sessionFactory, final LoadQueryInfluencers loadQueryInfluencers,
 			final LockMode lockMode) {
 		super( sessionFactory, loadQueryInfluencers, lockMode );
 		this.rootEntityGraph = (GraphNodeImplementor) loadQueryInfluencers.getFetchGraph();
 	}
 
 	@Override
 	protected GraphNodeImplementor getRootEntityGraph() {
 		return rootEntityGraph;
 	}
 
 	@Override
 	protected FetchStrategy resolveImplicitFetchStrategyFromEntityGraph(
 			final AssociationAttributeDefinition attributeDefinition) {
 		//under fetchgraph mode, according to the SPEC, all other attributes that no in entity graph are supposed to be lazily loaded
 		return DEFAULT_LAZY;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/FetchStyleLoadPlanBuildingAssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/FetchStyleLoadPlanBuildingAssociationVisitationStrategy.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/FetchStyleLoadPlanBuildingAssociationVisitationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/FetchStyleLoadPlanBuildingAssociationVisitationStrategy.java
index 2bcd1fd86f..af69f0c054 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/FetchStyleLoadPlanBuildingAssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/FetchStyleLoadPlanBuildingAssociationVisitationStrategy.java
@@ -1,156 +1,156 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal;
+package org.hibernate.loader.plan.build.internal;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.build.spi.AbstractLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.build.spi.AbstractLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 
 /**
  * LoadPlanBuilderStrategy implementation used for building LoadPlans based on metamodel-defined fetching.  Built
  * LoadPlans contain a single root return object, either an {@link EntityReturn} or a {@link CollectionReturn}.
  *
  * @author Steve Ebersole
  */
 public class FetchStyleLoadPlanBuildingAssociationVisitationStrategy
 		extends AbstractLoadPlanBuildingAssociationVisitationStrategy {
 	private static final Logger log = CoreLogging.logger( FetchStyleLoadPlanBuildingAssociationVisitationStrategy.class );
 
 	private final LoadQueryInfluencers loadQueryInfluencers;
 	private final LockMode lockMode;
 
 	private Return rootReturn;
 
 	public FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
 			SessionFactoryImplementor sessionFactory,
 			LoadQueryInfluencers loadQueryInfluencers,
 			LockMode lockMode) {
 		super( sessionFactory );
 		this.loadQueryInfluencers = loadQueryInfluencers;
 		this.lockMode = lockMode;
 	}
 
 	@Override
 	protected boolean supportsRootEntityReturns() {
 		return true;
 	}
 
 	@Override
 	protected boolean supportsRootCollectionReturns() {
 		return true;
 	}
 
 	@Override
 	protected void addRootReturn(Return rootReturn) {
 		if ( this.rootReturn != null ) {
 			throw new HibernateException( "Root return already identified" );
 		}
 		this.rootReturn = rootReturn;
 	}
 
 	@Override
 	public LoadPlan buildLoadPlan() {
 		log.debug( "Building LoadPlan..." );
 
 		if ( EntityReturn.class.isInstance( rootReturn ) ) {
 			return new LoadPlanImpl( (EntityReturn) rootReturn, getQuerySpaces() );
 		}
 		else if ( CollectionReturn.class.isInstance( rootReturn ) ) {
 			return new LoadPlanImpl( (CollectionReturn) rootReturn, getQuerySpaces() );
 		}
 		else {
 			throw new IllegalStateException( "Unexpected root Return type : " + rootReturn );
 		}
 	}
 
 	@Override
 	protected FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition) {
 		FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan( loadQueryInfluencers, currentPropertyPath );
 		if ( fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 			// see if we need to alter the join fetch to another form for any reason
 			fetchStrategy = adjustJoinFetchIfNeeded( attributeDefinition, fetchStrategy );
 		}
 		return fetchStrategy;
 	}
 
 	protected FetchStrategy adjustJoinFetchIfNeeded(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy) {
 		if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		final Integer maxFetchDepth = sessionFactory().getSettings().getMaximumFetchDepth();
 		if ( maxFetchDepth != null && currentDepth() > maxFetchDepth ) {
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		if ( attributeDefinition.getType().isCollectionType() && isTooManyCollections() ) {
 			// todo : have this revert to batch or subselect fetching once "sql gen redesign" is in place
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		return fetchStrategy;
 	}
 
 	@Override
 	protected boolean isTooManyCollections() {
 		return CollectionReturn.class.isInstance( rootReturn );
 	}
 
 //	@Override
 //	protected EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition) {
 //		final String entityName = entityDefinition.getEntityPersister().getEntityName();
 //		return new EntityReturn(
 //				sessionFactory(),
 //				LockMode.NONE, // todo : for now
 //				entityName
 //		);
 //	}
 //
 //	@Override
 //	protected CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition) {
 //		final CollectionPersister persister = collectionDefinition.getCollectionPersister();
 //		final String collectionRole = persister.getRole();
 //		return new CollectionReturn(
 //				sessionFactory(),
 //				LockMode.NONE, // todo : for now
 //				persister.getOwnerEntityPersister().getEntityName(),
 //				StringHelper.unqualify( collectionRole )
 //		);
 //	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadGraphLoadPlanBuildingStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/LoadGraphLoadPlanBuildingStrategy.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadGraphLoadPlanBuildingStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/LoadGraphLoadPlanBuildingStrategy.java
index e4ab504b41..0335e09b76 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadGraphLoadPlanBuildingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/LoadGraphLoadPlanBuildingStrategy.java
@@ -1,71 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
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
-package org.hibernate.loader.plan2.build.internal;
+package org.hibernate.loader.plan.build.internal;
 
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.graph.spi.GraphNodeImplementor;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 
 /**
  * Loadplan building strategy for {@link javax.persistence.EntityGraph} is applied in {@code javax.persistence.loadgraph} mode.
  *
  * @author Strong Liu <stliu@hibernate.org>
  */
 public class LoadGraphLoadPlanBuildingStrategy extends AbstractEntityGraphVisitationStrategy {
 	private final GraphNodeImplementor rootEntityGraph;
 
 	public LoadGraphLoadPlanBuildingStrategy(
 			final SessionFactoryImplementor sessionFactory, final LoadQueryInfluencers loadQueryInfluencers,final LockMode lockMode) {
 		super( sessionFactory, loadQueryInfluencers , lockMode);
 		this.rootEntityGraph = (GraphNodeImplementor) loadQueryInfluencers.getLoadGraph();
 	}
 
 	@Override
 	protected GraphNodeImplementor getRootEntityGraph() {
 		return rootEntityGraph;
 	}
 
 	@Override
 	protected FetchStrategy resolveImplicitFetchStrategyFromEntityGraph(
 			final AssociationAttributeDefinition attributeDefinition) {
 		FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan(
 				loadQueryInfluencers,
 				currentPropertyPath
 		);
 		if ( fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 			// see if we need to alter the join fetch to another form for any reason
 			fetchStrategy = adjustJoinFetchIfNeeded( attributeDefinition, fetchStrategy );
 		}
 
 		return fetchStrategy;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadPlanImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/LoadPlanImpl.java
similarity index 87%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadPlanImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/LoadPlanImpl.java
index ad0c9e54ca..2521d2128f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadPlanImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/LoadPlanImpl.java
@@ -1,122 +1,117 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal;
+package org.hibernate.loader.plan.build.internal;
 
-import java.io.ByteArrayOutputStream;
-import java.io.PrintStream;
 import java.util.Collections;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.build.spi.LoadPlanTreePrinter;
-import org.hibernate.loader.plan2.build.spi.QuerySpaceTreePrinter;
-import org.hibernate.loader.plan2.build.spi.ReturnGraphTreePrinter;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.QuerySpaces;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.QuerySpaces;
+import org.hibernate.loader.plan.spi.Return;
 
 /**
  * @author Steve Ebersole
  */
 public class LoadPlanImpl implements LoadPlan {
 	private static final Logger log = CoreLogging.logger( LoadPlanImpl.class );
 
 	private final List<? extends Return> returns;
 	private final QuerySpaces querySpaces;
 	private final Disposition disposition;
 	private final boolean areLazyAttributesForceFetched;
 
 	protected LoadPlanImpl(
 			List<? extends Return> returns,
 			QuerySpaces querySpaces,
 			Disposition disposition,
 			boolean areLazyAttributesForceFetched) {
 		this.returns = returns;
 		this.querySpaces = querySpaces;
 		this.disposition = disposition;
 		this.areLazyAttributesForceFetched = areLazyAttributesForceFetched;
 	}
 
 	/**
 	 * Creates a {@link Disposition#ENTITY_LOADER} LoadPlan.
 	 *
 	 * @param rootReturn The EntityReturn representation of the entity being loaded.
 	 */
 	public LoadPlanImpl(EntityReturn rootReturn, QuerySpaces querySpaces) {
 		this( Collections.singletonList( rootReturn ), querySpaces, Disposition.ENTITY_LOADER, false );
 	}
 
 	/**
 	 * Creates a {@link Disposition#COLLECTION_INITIALIZER} LoadPlan.
 	 *
 	 * @param rootReturn The CollectionReturn representation of the collection being initialized.
 	 */
 	public LoadPlanImpl(CollectionReturn rootReturn, QuerySpaces querySpaces) {
 		this( Collections.singletonList( rootReturn ), querySpaces, Disposition.COLLECTION_INITIALIZER, false );
 	}
 
 	/**
 	 * Creates a {@link Disposition#MIXED} LoadPlan.
 	 *
 	 * @param returns The mixed Return references
 	 * @param areLazyAttributesForceFetched Should lazy attributes (bytecode enhanced laziness) be fetched also?  This
 	 * effects the eventual SQL SELECT-clause which is why we have it here.  Currently this is "all-or-none"; you
 	 * can request that all lazy properties across all entities in the loadplan be force fetched or none.  There is
 	 * no entity-by-entity option.  {@code FETCH ALL PROPERTIES} is the way this is requested in HQL.  Would be nice to
 	 * consider this entity-by-entity, as opposed to all-or-none.  For example, "fetch the LOB value for the Item.image
 	 * attribute, but no others (leave them lazy)".  Not too concerned about having it at the attribute level.
 	 */
 	public LoadPlanImpl(List<? extends Return> returns, QuerySpaces querySpaces, boolean areLazyAttributesForceFetched) {
 		this( returns, querySpaces, Disposition.MIXED, areLazyAttributesForceFetched );
 	}
 
 	@Override
 	public List<? extends Return> getReturns() {
 		return returns;
 	}
 
 	@Override
 	public QuerySpaces getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	public Disposition getDisposition() {
 		return disposition;
 	}
 
 	@Override
 	public boolean areLazyAttributesForceFetched() {
 		return areLazyAttributesForceFetched;
 	}
 
 	@Override
 	public boolean hasAnyScalarReturns() {
 		return disposition == Disposition.MIXED;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractAnyReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractAnyReference.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractAnyReference.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractAnyReference.java
index 3893b929fb..f8265f5bc5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractAnyReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractAnyReference.java
@@ -1,72 +1,72 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.spi.BidirectionalEntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.FetchSource;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractAnyReference implements FetchSource {
 	/**
 	 * Convenient constant for returning no fetches from {@link #getFetches()}
 	 */
 	private static final Fetch[] NO_FETCHES = new Fetch[0];
 
 	/**
 	 * Convenient constant for returning no fetches from {@link #getFetches()}
 	 */
 	private static final BidirectionalEntityReference[] NO_BIDIRECTIONAL_ENTITY_REFERENCES =
 			new BidirectionalEntityReference[0];
 
 	private final PropertyPath propertyPath;
 
 	public AbstractAnyReference(PropertyPath propertyPath) {
 		this.propertyPath = propertyPath;
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return NO_FETCHES;
 	}
 
 	@Override
 	public BidirectionalEntityReference[] getBidirectionalEntityReferences() {
 		return NO_BIDIRECTIONAL_ENTITY_REFERENCES;
 	}
 
 	@Override
 	public String getQuerySpaceUid() {
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCollectionReference.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCollectionReference.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCollectionReference.java
index 0a5aaba805..bffdcc4417 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCollectionReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCollectionReference.java
@@ -1,181 +1,181 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.internal.spaces.CompositePropertyMapping;
-import org.hibernate.loader.plan2.build.internal.spaces.QuerySpaceHelper;
-import org.hibernate.loader.plan2.build.spi.ExpandingCollectionQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
-import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
-import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
+import org.hibernate.loader.plan.build.internal.spaces.QuerySpaceHelper;
+import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.CollectionPropertyNames;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractCollectionReference implements CollectionReference {
 	private final ExpandingCollectionQuerySpace collectionQuerySpace;
 	private final PropertyPath propertyPath;
 
 	private final CollectionFetchableIndex index;
 	private final CollectionFetchableElement element;
 
 	protected AbstractCollectionReference(
 			ExpandingCollectionQuerySpace collectionQuerySpace,
 			PropertyPath propertyPath,
 			boolean shouldIncludeJoins) {
 		this.collectionQuerySpace = collectionQuerySpace;
 		this.propertyPath = propertyPath;
 
 		this.index = buildIndexGraph( collectionQuerySpace, shouldIncludeJoins );
 		this.element = buildElementGraph( collectionQuerySpace, shouldIncludeJoins );
 	}
 
 	private CollectionFetchableIndex buildIndexGraph(
 			ExpandingCollectionQuerySpace collectionQuerySpace,
 			boolean shouldIncludeJoins) {
 		final CollectionPersister persister = collectionQuerySpace.getCollectionPersister();
 		if ( persister.hasIndex() ) {
 			final Type type = persister.getIndexType();
 			if ( type.isAssociationType() ) {
 				if ( type.isEntityType() ) {
 					final EntityPersister indexPersister = persister.getFactory().getEntityPersister(
 							( (EntityType) type ).getAssociatedEntityName()
 					);
 
 					final ExpandingEntityQuerySpace entityQuerySpace = QuerySpaceHelper.INSTANCE.makeEntityQuerySpace(
 							collectionQuerySpace,
 							indexPersister,
 							CollectionPropertyNames.COLLECTION_INDICES,
 							(EntityType) persister.getIndexType(),
 							collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(),
 							collectionQuerySpace.canJoinsBeRequired(),
 							shouldIncludeJoins
 					);
 					return new CollectionFetchableIndexEntityGraph( this, entityQuerySpace );
 				}
 				else if ( type.isAnyType() ) {
 					return new CollectionFetchableIndexAnyGraph( this );
 				}
 			}
 			else if ( type.isComponentType() ) {
 				final ExpandingCompositeQuerySpace compositeQuerySpace = QuerySpaceHelper.INSTANCE.makeCompositeQuerySpace(
 						collectionQuerySpace,
 						new CompositePropertyMapping(
 								(CompositeType) persister.getIndexType(),
 								(PropertyMapping) persister,
 								""
 						),
 						CollectionPropertyNames.COLLECTION_INDICES,
 						(CompositeType) persister.getIndexType(),
 						collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(),
 						collectionQuerySpace.canJoinsBeRequired(),
 						shouldIncludeJoins
 				);
 				return new CollectionFetchableIndexCompositeGraph( this, compositeQuerySpace );
 			}
 		}
 
 		return null;
 	}
 
 	private CollectionFetchableElement buildElementGraph(
 			ExpandingCollectionQuerySpace collectionQuerySpace,
 			boolean shouldIncludeJoins) {
 		final CollectionPersister persister = collectionQuerySpace.getCollectionPersister();
 		final Type type = persister.getElementType();
 		if ( type.isAssociationType() ) {
 			if ( type.isEntityType() ) {
 				final EntityPersister elementPersister = persister.getFactory().getEntityPersister(
 						( (EntityType) type ).getAssociatedEntityName()
 				);
 				final ExpandingEntityQuerySpace entityQuerySpace = QuerySpaceHelper.INSTANCE.makeEntityQuerySpace(
 						collectionQuerySpace,
 						elementPersister,
 						CollectionPropertyNames.COLLECTION_ELEMENTS,
 						(EntityType) persister.getElementType(),
 						collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(),
 						collectionQuerySpace.canJoinsBeRequired(),
 						shouldIncludeJoins
 				);
 				return new CollectionFetchableElementEntityGraph( this, entityQuerySpace );
 			}
 			else if ( type.isAnyType() ) {
 				return new CollectionFetchableElementAnyGraph( this );
 			}
 		}
 		else if ( type.isComponentType() ) {
 			final ExpandingCompositeQuerySpace compositeQuerySpace = QuerySpaceHelper.INSTANCE.makeCompositeQuerySpace(
 					collectionQuerySpace,
 					new CompositePropertyMapping(
 							(CompositeType) persister.getElementType(),
 							(PropertyMapping) persister,
 							""
 					),
 					CollectionPropertyNames.COLLECTION_ELEMENTS,
 					(CompositeType) persister.getElementType(),
 					collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(),
 					collectionQuerySpace.canJoinsBeRequired(),
 					shouldIncludeJoins
 			);
 			return new CollectionFetchableElementCompositeGraph( this, compositeQuerySpace );
 		}
 
 		return null;
 	}
 
 	@Override
 	public String getQuerySpaceUid() {
 		return collectionQuerySpace.getUid();
 	}
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return collectionQuerySpace.getCollectionPersister();
 	}
 
 	@Override
 	public CollectionFetchableIndex getIndexGraph() {
 		return index;
 	}
 
 	@Override
 	public CollectionFetchableElement getElementGraph() {
 		return element;
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeEntityIdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeEntityIdentifierDescription.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeEntityIdentifierDescription.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeEntityIdentifierDescription.java
index b4205677a2..35d506fa3c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeEntityIdentifierDescription.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeEntityIdentifierDescription.java
@@ -1,86 +1,86 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityIdentifierDescription;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityIdentifierDescription;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public abstract class AbstractCompositeEntityIdentifierDescription
 		extends AbstractCompositeFetch
 		implements ExpandingEntityIdentifierDescription {
 
 	private final EntityReference entityReference;
 	private final CompositeType identifierType;
 
 	protected AbstractCompositeEntityIdentifierDescription(
 			EntityReference entityReference,
 			ExpandingCompositeQuerySpace compositeQuerySpace,
 			CompositeType identifierType,
 			PropertyPath propertyPath) {
 		super( compositeQuerySpace, false, propertyPath );
 		this.entityReference = entityReference;
 		this.identifierType = identifierType;
 	}
 
 	@Override
 	public boolean hasFetches() {
 		return getFetches().length > 0;
 	}
 
 	@Override
 	public boolean hasBidirectionalEntityReferences() {
 		return getBidirectionalEntityReferences().length > 0;
 	}
 
 	@Override
 	public FetchSource getSource() {
 		// the source for this (as a Fetch) is the entity reference
 		return entityReference;
 	}
 
 	@Override
 	public Type getFetchedType() {
 		return identifierType;
 	}
 
 	@Override
 	public boolean isNullable() {
 		return false;
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return entityReference;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeFetch.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeFetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeFetch.java
index 0ab92dec91..dc61735b5f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeFetch.java
@@ -1,62 +1,62 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeFetch;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public abstract class AbstractCompositeFetch extends AbstractCompositeReference implements CompositeFetch {
 	protected static final FetchStrategy FETCH_STRATEGY = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 
 	protected AbstractCompositeFetch(
 			ExpandingCompositeQuerySpace compositeQuerySpace,
 			boolean allowCollectionFetches,
 			PropertyPath propertyPath) {
 		super( compositeQuerySpace, allowCollectionFetches, propertyPath );
 	}
 
 	@Override
 	public FetchStrategy getFetchStrategy() {
 		return FETCH_STRATEGY;
 	}
 
 	@Override
 	public String getAdditionalJoinConditions() {
 		return null;
 	}
 
 	// this is being removed to be more ogm/search friendly
 	@Override
 	public String[] toSqlSelectFragments(String alias) {
 		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeReference.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeReference.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeReference.java
index 5a8d375447..a2ecbd086d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractCompositeReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractCompositeReference.java
@@ -1,81 +1,81 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
-import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public abstract class AbstractCompositeReference extends AbstractExpandingFetchSource {
 
 	private final boolean allowCollectionFetches;
 
 	protected AbstractCompositeReference(
 			ExpandingCompositeQuerySpace compositeQuerySpace,
 			boolean allowCollectionFetches,
 			PropertyPath propertyPath) {
 		super( compositeQuerySpace, propertyPath );
 		this.allowCollectionFetches = allowCollectionFetches;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
 		// anything to do here?
 	}
 
 	protected CompositeAttributeFetch createCompositeAttributeFetch(
 			AttributeDefinition attributeDefinition,
 			ExpandingCompositeQuerySpace compositeQuerySpace) {
 		return new NestedCompositeAttributeFetchImpl(
 				this,
 				attributeDefinition,
 				compositeQuerySpace,
 				allowCollectionFetches
 		);
 	}
 
 	@Override
 	public CollectionAttributeFetch buildCollectionAttributeFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy) {
 		if ( !allowCollectionFetches ) {
 			throw new WalkingException(
 					String.format(
 							"This composite path [%s] does not allow collection fetches (composite id or composite collection index/element",
 							getPropertyPath().getFullPath()
 					)
 			);
 		}
 		return super.buildCollectionAttributeFetch( attributeDefinition, fetchStrategy );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractEntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractEntityReference.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractEntityReference.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractEntityReference.java
index 31b909b563..b729a3f8f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractEntityReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractEntityReference.java
@@ -1,126 +1,126 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
-import org.hibernate.loader.plan2.spi.EntityIdentifierDescription;
-import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
+import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
+import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractEntityReference extends AbstractExpandingFetchSource implements EntityReference {
 
 	private final EntityIdentifierDescription identifierDescription;
 
 	public AbstractEntityReference(
 			ExpandingEntityQuerySpace entityQuerySpace,
 			PropertyPath propertyPath) {
 		super( entityQuerySpace, propertyPath );
 		this.identifierDescription = buildIdentifierDescription();
 	}
 
 	private ExpandingEntityQuerySpace expandingEntityQuerySpace() {
 		return (ExpandingEntityQuerySpace) expandingQuerySpace();
 	}
 
 	/**
 	 * Builds just the first level of identifier description.  This will be either a simple id descriptor (String,
 	 * Long, etc) or some form of composite id (either encapsulated or not).
 	 *
 	 * @return the descriptor for the identifier
 	 */
 	private EntityIdentifierDescription buildIdentifierDescription() {
 		final EntityIdentifierDefinition identifierDefinition = getEntityPersister().getEntityKeyDefinition();
 
 		if ( identifierDefinition.isEncapsulated() ) {
 			final EncapsulatedEntityIdentifierDefinition encapsulatedIdentifierDefinition = (EncapsulatedEntityIdentifierDefinition) identifierDefinition;
 			final Type idAttributeType = encapsulatedIdentifierDefinition.getAttributeDefinition().getType();
 			if ( ! CompositeType.class.isInstance( idAttributeType ) ) {
 				return new SimpleEntityIdentifierDescriptionImpl();
 			}
 		}
 
 		// if we get here, we know we have a composite identifier...
 		final ExpandingCompositeQuerySpace querySpace = expandingEntityQuerySpace().makeCompositeIdentifierQuerySpace();
 		return identifierDefinition.isEncapsulated()
 				? buildEncapsulatedCompositeIdentifierDescription( querySpace )
 				: buildNonEncapsulatedCompositeIdentifierDescription( querySpace );
 	}
 
 	private NonEncapsulatedEntityIdentifierDescription buildNonEncapsulatedCompositeIdentifierDescription(
 			ExpandingCompositeQuerySpace compositeQuerySpace) {
 		return new NonEncapsulatedEntityIdentifierDescription(
 				this,
 				compositeQuerySpace,
 				(CompositeType) getEntityPersister().getIdentifierType(),
 				getPropertyPath().append( EntityPersister.ENTITY_ID )
 		);
 	}
 
 	private EncapsulatedEntityIdentifierDescription buildEncapsulatedCompositeIdentifierDescription(
 			ExpandingCompositeQuerySpace compositeQuerySpace) {
 		return new EncapsulatedEntityIdentifierDescription(
 				this,
 				compositeQuerySpace,
 				(CompositeType) getEntityPersister().getIdentifierType(),
 				getPropertyPath().append( EntityPersister.ENTITY_ID )
 		);
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return this;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return expandingEntityQuerySpace().getEntityPersister();
 	}
 
 	@Override
 	public EntityIdentifierDescription getIdentifierDescription() {
 		return identifierDescription;
 	}
 
 	protected CompositeAttributeFetch createCompositeAttributeFetch(
 			AttributeDefinition attributeDefinition,
 			ExpandingCompositeQuerySpace compositeQuerySpace) {
 		return new CompositeAttributeFetchImpl(
 				this,
 				attributeDefinition,
 				compositeQuerySpace,
 				true
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractExpandingFetchSource.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractExpandingFetchSource.java
similarity index 87%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractExpandingFetchSource.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractExpandingFetchSource.java
index 1c954ff440..7d74112a0e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AbstractExpandingFetchSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AbstractExpandingFetchSource.java
@@ -1,216 +1,216 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.internal.spaces.QuerySpaceHelper;
-import org.hibernate.loader.plan2.build.spi.ExpandingCollectionQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.AnyAttributeFetch;
-import org.hibernate.loader.plan2.spi.BidirectionalEntityReference;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
-import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan.build.internal.spaces.QuerySpaceHelper;
+import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.AnyAttributeFetch;
+import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractExpandingFetchSource implements ExpandingFetchSource {
 	/**
 	 * Convenient constant for returning no fetches from {@link #getFetches()}
 	 */
 	private static final Fetch[] NO_FETCHES = new Fetch[0];
 
 	/**
 	 * Convenient constant for returning no fetches from {@link #getFetches()}
 	 */
 	private static final BidirectionalEntityReference[] NO_BIDIRECTIONAL_ENTITY_REFERENCES =
 			new BidirectionalEntityReference[0];
 
 	private final ExpandingQuerySpace querySpace;
 	private final PropertyPath propertyPath;
 	private List<Fetch> fetches;
 	private List<BidirectionalEntityReference> bidirectionalEntityReferences;
 
 	public AbstractExpandingFetchSource(ExpandingQuerySpace querySpace, PropertyPath propertyPath) {
 		this.querySpace = querySpace;
 		this.propertyPath = propertyPath;
 	}
 
 	@Override
 	public final String getQuerySpaceUid() {
 		return querySpace.getUid();
 	}
 
 	protected final ExpandingQuerySpace expandingQuerySpace() {
 		return querySpace;
 	}
 
 	@Override
 	public final PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
 	}
 
 	private void addFetch(Fetch fetch) {
 		if ( fetches == null ) {
 			fetches = new ArrayList<Fetch>();
 		}
 		fetches.add( fetch );
 	}
 
 	@Override
 	public BidirectionalEntityReference[] getBidirectionalEntityReferences() {
 		return bidirectionalEntityReferences == null ?
 				NO_BIDIRECTIONAL_ENTITY_REFERENCES :
 				bidirectionalEntityReferences.toArray(
 						new BidirectionalEntityReference[ bidirectionalEntityReferences.size() ]
 				);
 	}
 
 	private void addBidirectionalEntityReference(BidirectionalEntityReference bidirectionalEntityReference) {
 		if ( bidirectionalEntityReferences == null ) {
 			bidirectionalEntityReferences = new ArrayList<BidirectionalEntityReference>();
 		}
 		bidirectionalEntityReferences.add( bidirectionalEntityReference );
 	}
 
 	@Override
 	public EntityFetch buildEntityAttributeFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy) {
 
 		final ExpandingEntityQuerySpace entityQuerySpace = QuerySpaceHelper.INSTANCE.makeEntityQuerySpace(
 				expandingQuerySpace(),
 				attributeDefinition,
 				getQuerySpaces().generateImplicitUid(),
 				fetchStrategy
 		);
 		final EntityFetch fetch = new EntityAttributeFetchImpl( this, attributeDefinition, fetchStrategy, entityQuerySpace );
 		addFetch( fetch );
 		return fetch;
 	}
 
 	@Override
 	public BidirectionalEntityReference buildBidirectionalEntityReference(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			EntityReference targetEntityReference) {
 		final EntityType fetchedType = (EntityType) attributeDefinition.getType();
 		final EntityPersister fetchedPersister = attributeDefinition.toEntityDefinition().getEntityPersister();
 
 		if ( fetchedPersister == null ) {
 			throw new WalkingException(
 					String.format(
 							"Unable to locate EntityPersister [%s] for bidirectional entity reference [%s]",
 							fetchedType.getAssociatedEntityName(),
 							attributeDefinition.getName()
 					)
 			);
 		}
 
 		final BidirectionalEntityReference bidirectionalEntityReference =
 				new BidirectionalEntityReferenceImpl( this, attributeDefinition, targetEntityReference );
 		addBidirectionalEntityReference( bidirectionalEntityReference );
 		return bidirectionalEntityReference;
 	}
 
 	protected abstract CompositeAttributeFetch createCompositeAttributeFetch(
 			AttributeDefinition compositeType,
 			ExpandingCompositeQuerySpace compositeQuerySpace);
 
 	protected ExpandingQuerySpaces getQuerySpaces() {
 		return querySpace.getExpandingQuerySpaces();
 	}
 
 	@Override
 	public CompositeAttributeFetch buildCompositeAttributeFetch(
 			AttributeDefinition attributeDefinition) {
 		final ExpandingCompositeQuerySpace compositeQuerySpace = QuerySpaceHelper.INSTANCE.makeCompositeQuerySpace(
 				expandingQuerySpace(),
 				attributeDefinition,
 				getQuerySpaces().generateImplicitUid(),
 				true
 		);
 
 		final CompositeAttributeFetch fetch = createCompositeAttributeFetch( attributeDefinition, compositeQuerySpace );
 		addFetch( fetch );
 		return fetch;
 	}
 
 	@Override
 	public CollectionAttributeFetch buildCollectionAttributeFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy) {
 
 		final ExpandingCollectionQuerySpace collectionQuerySpace = QuerySpaceHelper.INSTANCE.makeCollectionQuerySpace(
 				querySpace,
 				attributeDefinition,
 				getQuerySpaces().generateImplicitUid(),
 				fetchStrategy
 		);
 
 		final CollectionAttributeFetch fetch = new CollectionAttributeFetchImpl(
 				this,
 				attributeDefinition,
 				fetchStrategy,
 				collectionQuerySpace
 		);
 		addFetch( fetch );
 		return fetch;
 	}
 
 	@Override
 	public AnyAttributeFetch buildAnyAttributeFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy) {
 
 		final AnyAttributeFetch fetch = new AnyAttributeFetchImpl(
 				this,
 				attributeDefinition,
 				fetchStrategy
 		);
 		addFetch( fetch );
 		return fetch;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AnyAttributeFetchImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AnyAttributeFetchImpl.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AnyAttributeFetchImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AnyAttributeFetchImpl.java
index d99ab048a5..f707ae37a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/AnyAttributeFetchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/AnyAttributeFetchImpl.java
@@ -1,95 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
-import org.hibernate.loader.plan2.spi.AnyAttributeFetch;
-import org.hibernate.loader.plan2.spi.AttributeFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.spi.AnyAttributeFetch;
+import org.hibernate.loader.plan.spi.AttributeFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.type.AnyType;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class AnyAttributeFetchImpl extends AbstractAnyReference implements AnyAttributeFetch, AttributeFetch {
 	private final FetchSource fetchSource;
 	private final AssociationAttributeDefinition fetchedAttribute;
 	private final FetchStrategy fetchStrategy;
 
 	public AnyAttributeFetchImpl(
 			FetchSource fetchSource,
 			AssociationAttributeDefinition fetchedAttribute,
 			FetchStrategy fetchStrategy) {
 		super( fetchSource.getPropertyPath().append( fetchedAttribute.getName() ) );
 
 		this.fetchSource = fetchSource;
 		this.fetchedAttribute = fetchedAttribute;
 		this.fetchStrategy = fetchStrategy;
 	}
 
 	@Override
 	public FetchSource getSource() {
 		return fetchSource;
 	}
 
 	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
 	@Override
 	public AnyType getFetchedType() {
 		return (AnyType) fetchedAttribute.getType();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return fetchedAttribute.isNullable();
 	}
 
 	@Override
 	public String[] toSqlSelectFragments(String alias) {
 		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public String getAdditionalJoinConditions() {
 		// only pertinent for HQL...
 		return null;
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return fetchSource.resolveEntityReference();
 	}
 
 	@Override
 	public AttributeDefinition getFetchedAttributeDefinition() {
 		return fetchedAttribute;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/BidirectionalEntityReferenceImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/BidirectionalEntityReferenceImpl.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/BidirectionalEntityReferenceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/BidirectionalEntityReferenceImpl.java
index 7f99cccf57..95e613346a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/BidirectionalEntityReferenceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/BidirectionalEntityReferenceImpl.java
@@ -1,95 +1,94 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
-import org.hibernate.loader.plan2.spi.BidirectionalEntityReference;
-import org.hibernate.loader.plan2.spi.EntityIdentifierDescription;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
+import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 
 /**
  * Represents an entity fetch that is bi-directionally join fetched.
  * <p/>
  * For example, consider an Order entity whose primary key is partially made up of the Customer entity to which
  * it is associated.  When we join fetch Customer -> Order(s) and then Order -> Customer we have a bi-directional
  * fetch.  This class would be used to represent the Order -> Customer part of that link.
  *
  * @author Steve Ebersole
  */
 public class BidirectionalEntityReferenceImpl implements BidirectionalEntityReference {
 	private final EntityReference targetEntityReference;
 	private final PropertyPath propertyPath;
 
 	public BidirectionalEntityReferenceImpl(
 			ExpandingFetchSource fetchSource,
 			AssociationAttributeDefinition fetchedAttribute,
 			EntityReference targetEntityReference) {
 		this.targetEntityReference = targetEntityReference;
 		this.propertyPath = fetchSource.getPropertyPath().append( fetchedAttribute.getName() );
 	}
 
 	public EntityReference getTargetEntityReference() {
 		return targetEntityReference;
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
 	public String getQuerySpaceUid() {
 		return targetEntityReference.getQuerySpaceUid();
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return targetEntityReference.getFetches();
 	}
 
 	@Override
 	public BidirectionalEntityReference[] getBidirectionalEntityReferences() {
 		return targetEntityReference.getBidirectionalEntityReferences();
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return this;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return targetEntityReference.getEntityPersister();
 	}
 
 	@Override
 	public EntityIdentifierDescription getIdentifierDescription() {
 		return targetEntityReference.getIdentifierDescription();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionAttributeFetchImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionAttributeFetchImpl.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionAttributeFetchImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionAttributeFetchImpl.java
index 3b25ebf2fa..3ebabe17e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionAttributeFetchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionAttributeFetchImpl.java
@@ -1,188 +1,188 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
-import org.hibernate.loader.plan2.build.internal.spaces.QuerySpaceHelper;
-import org.hibernate.loader.plan2.build.spi.ExpandingCollectionQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.build.internal.spaces.QuerySpaceHelper;
+import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.type.CollectionType;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionAttributeFetchImpl extends AbstractCollectionReference implements CollectionAttributeFetch {
 	private final ExpandingFetchSource fetchSource;
 	private final AttributeDefinition fetchedAttribute;
 	private final FetchStrategy fetchStrategy;
 
 	public CollectionAttributeFetchImpl(
 			ExpandingFetchSource fetchSource,
 			AssociationAttributeDefinition fetchedAttribute,
 			FetchStrategy fetchStrategy,
 			ExpandingCollectionQuerySpace collectionQuerySpace) {
 		super(
 				collectionQuerySpace,
 				fetchSource.getPropertyPath().append( fetchedAttribute.getName() ),
 				QuerySpaceHelper.INSTANCE.shouldIncludeJoin( fetchStrategy )
 
 		);
 
 		this.fetchSource = fetchSource;
 		this.fetchedAttribute = fetchedAttribute;
 		this.fetchStrategy = fetchStrategy;
 	}
 
 	@Override
 	public FetchSource getSource() {
 		return fetchSource;
 	}
 
 	@Override
 	public CollectionType getFetchedType() {
 		return (CollectionType) fetchedAttribute.getType();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return true;
 	}
 
 	@Override
 	public String getAdditionalJoinConditions() {
 		// only pertinent for HQL...
 		return null;
 	}
 
 	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
 
 
 
 
 
 	@Override
 	public String[] toSqlSelectFragments(String alias) {
 		return null;
 	}
 
 //	@Override
 //	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 //		//To change body of implemented methods use File | Settings | File Templates.
 //	}
 //
 //	@Override
 //	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 //		return null;
 //	}
 //
 //	@Override
 //	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
 //		final Serializable collectionRowKey = (Serializable) getCollectionPersister().readKey(
 //				resultSet,
 //				context.getAliasResolutionContext().resolveAliases( this ).getCollectionColumnAliases().getSuffixedKeyAliases(),
 //				context.getSession()
 //		);
 //
 //		final PersistenceContext persistenceContext = context.getSession().getPersistenceContext();
 //
 //		if ( collectionRowKey == null ) {
 //			// we did not find a collection element in the result set, so we
 //			// ensure that a collection is created with the owner's identifier,
 //			// since what we have is an empty collection
 //			final EntityKey ownerEntityKey = findOwnerEntityKey( context );
 //			if ( ownerEntityKey == null ) {
 //				// should not happen
 //				throw new IllegalStateException(
 //						"Could not locate owner's EntityKey during attempt to read collection element fro JDBC row : " +
 //								getPropertyPath().getFullPath()
 //				);
 //			}
 //
 //			if ( log.isDebugEnabled() ) {
 //				log.debugf(
 //						"Result set contains (possibly empty) collection: %s",
 //						MessageHelper.collectionInfoString(
 //								getCollectionPersister(),
 //								ownerEntityKey,
 //								context.getSession().getFactory()
 //						)
 //				);
 //			}
 //
 //			persistenceContext.getLoadContexts()
 //					.getCollectionLoadContext( resultSet )
 //					.getLoadingCollection( getCollectionPersister(), ownerEntityKey );
 //		}
 //		else {
 //			// we found a collection element in the result set
 //			if ( log.isDebugEnabled() ) {
 //				log.debugf(
 //						"Found row of collection: %s",
 //						MessageHelper.collectionInfoString(
 //								getCollectionPersister(),
 //								collectionRowKey,
 //								context.getSession().getFactory()
 //						)
 //				);
 //			}
 //
 //			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 //					.getCollectionLoadContext( resultSet )
 //					.getLoadingCollection( getCollectionPersister(), collectionRowKey );
 //
 //			final CollectionAliases descriptor = context.getAliasResolutionContext().resolveAliases( this ).getCollectionColumnAliases();
 //
 //			if ( rowCollection != null ) {
 //				final Object element = rowCollection.readFrom( resultSet, getCollectionPersister(), descriptor, owner );
 //
 //				if ( getElementGraph() != null ) {
 //					for ( Fetch fetch : getElementGraph().getFetches() ) {
 //						fetch.read( resultSet, context, element );
 //					}
 //				}
 //			}
 //		}
 //	}
 //
 //	private EntityKey findOwnerEntityKey(ResultSetProcessingContext context) {
 //		return context.getProcessingState( findOwnerEntityReference( getOwner() ) ).getEntityKey();
 //	}
 //
 //	private EntityReference findOwnerEntityReference(FetchOwner owner) {
 //		return Helper.INSTANCE.findOwnerEntityReference( owner );
 //	}
 
 	@Override
 	public AttributeDefinition getFetchedAttributeDefinition() {
 		return fetchedAttribute;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementAnyGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementAnyGraph.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementAnyGraph.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementAnyGraph.java
index 3f14d4f480..8b9d1a4a8a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementAnyGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementAnyGraph.java
@@ -1,56 +1,56 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
-import org.hibernate.loader.plan2.spi.CollectionReference;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
 
 /**
  * @author Gail Badner
  */
 public class CollectionFetchableElementAnyGraph extends AbstractAnyReference implements CollectionFetchableElement {
 	private final CollectionReference collectionReference;
 
 	public CollectionFetchableElementAnyGraph(CollectionReference collectionReference) {
 		super(
 				// this property path is just informational...
 				collectionReference.getPropertyPath().append( "<element>" )
 		);
 		this.collectionReference = collectionReference;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return Fetch.class.isInstance( collectionReference ) ?
 				Fetch.class.cast( collectionReference ).getSource().resolveEntityReference() :
 				null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementCompositeGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementCompositeGraph.java
similarity index 85%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementCompositeGraph.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementCompositeGraph.java
index 6cc39b9283..28309d040d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementCompositeGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementCompositeGraph.java
@@ -1,66 +1,66 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
-import org.hibernate.loader.plan2.spi.CollectionReference;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
 
 /**
  * Models the element graph of a collection, where the elements are composite
  *
  * @author Steve Ebersole
  */
 public class CollectionFetchableElementCompositeGraph
 		extends AbstractCompositeReference
 		implements CollectionFetchableElement {
 
 	private final CollectionReference collectionReference;
 
 	public CollectionFetchableElementCompositeGraph(
 			CollectionReference collectionReference,
 			ExpandingCompositeQuerySpace compositeQuerySpace) {
 		super(
 				compositeQuerySpace,
 				false,
 				// these property paths are just informational...
 				collectionReference.getPropertyPath().append( "<element>" )
 		);
 		this.collectionReference = collectionReference;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return Fetch.class.isInstance( collectionReference ) ?
 				Fetch.class.cast( collectionReference ).getSource().resolveEntityReference() :
 				null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementEntityGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementEntityGraph.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementEntityGraph.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementEntityGraph.java
index d0b185c617..1b3cbd86fb 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableElementEntityGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableElementEntityGraph.java
@@ -1,58 +1,58 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
-import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionFetchableElementEntityGraph extends AbstractEntityReference implements CollectionFetchableElement {
 	private final CollectionReference collectionReference;
 
 	public CollectionFetchableElementEntityGraph(
 			CollectionReference collectionReference,
 			ExpandingEntityQuerySpace entityQuerySpace) {
 		super(
 				entityQuerySpace,
 				collectionReference.getPropertyPath().append( "<elements>" )
 		);
 
 		this.collectionReference = collectionReference;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
 		//To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexAnyGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexAnyGraph.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexAnyGraph.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexAnyGraph.java
index 7b0d039341..ca5eb6badc 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexAnyGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexAnyGraph.java
@@ -1,56 +1,56 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
-import org.hibernate.loader.plan2.spi.CollectionReference;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
 
 /**
  * @author Gail Badner
  */
 public class CollectionFetchableIndexAnyGraph extends AbstractAnyReference implements CollectionFetchableIndex {
 	private final CollectionReference collectionReference;
 
 	public CollectionFetchableIndexAnyGraph(CollectionReference collectionReference) {
 		super(
 				// this property path is just informational...
 				collectionReference.getPropertyPath().append( "<index>" )
 		);
 		this.collectionReference = collectionReference;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return Fetch.class.isInstance( collectionReference ) ?
 				Fetch.class.cast( collectionReference ).getSource().resolveEntityReference() :
 				null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexCompositeGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexCompositeGraph.java
similarity index 85%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexCompositeGraph.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexCompositeGraph.java
index 94e6b78ddf..635b5d81a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexCompositeGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexCompositeGraph.java
@@ -1,66 +1,66 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
-import org.hibernate.loader.plan2.spi.CollectionReference;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
 
 /**
  * Models the index graph of a collection, where the index are composite.  This can only be a Map, where the keys are
  * composite
  *
  * @author Steve Ebersole
  */
 public class CollectionFetchableIndexCompositeGraph
 		extends AbstractCompositeReference
 		implements CollectionFetchableIndex {
 
 	private final CollectionReference collectionReference;
 
 	public CollectionFetchableIndexCompositeGraph(
 			CollectionReference collectionReference,
 			ExpandingCompositeQuerySpace compositeQuerySpace) {
 		super(
 				compositeQuerySpace,
 				false,
 				collectionReference.getPropertyPath().append( "<index>" )
 		);
 		this.collectionReference = collectionReference;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return Fetch.class.isInstance( collectionReference ) ?
 				Fetch.class.cast( collectionReference ).getSource().resolveEntityReference() :
 				null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexEntityGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexEntityGraph.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexEntityGraph.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexEntityGraph.java
index 38cdba0c99..5101d8073a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionFetchableIndexEntityGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionFetchableIndexEntityGraph.java
@@ -1,57 +1,57 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
-import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionFetchableIndexEntityGraph extends AbstractEntityReference implements CollectionFetchableIndex {
 	private final CollectionReference collectionReference;
 
 	public CollectionFetchableIndexEntityGraph(
 			CollectionReference collectionReference,
 			ExpandingEntityQuerySpace entityQuerySpace) {
 		super(
 				entityQuerySpace,
 				collectionReference.getPropertyPath().append( "<index>" )
 		);
 
 		this.collectionReference = collectionReference;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionReturnImpl.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionReturnImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionReturnImpl.java
index 88bff55bca..5c600ce2e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CollectionReturnImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CollectionReturnImpl.java
@@ -1,46 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionReturnImpl extends AbstractCollectionReference implements CollectionReturn {
 
 	public CollectionReturnImpl(CollectionDefinition collectionDefinition, ExpandingQuerySpaces querySpaces) {
 		super(
 				querySpaces.makeRootCollectionQuerySpace(
 						querySpaces.generateImplicitUid(),
 						collectionDefinition.getCollectionPersister()
 				),
 				new PropertyPath( "[" + collectionDefinition.getCollectionPersister().getRole() + "]" ),
 				true
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CompositeAttributeFetchImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CompositeAttributeFetchImpl.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CompositeAttributeFetchImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CompositeAttributeFetchImpl.java
index 3d7641c2c2..24f2e26067 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/CompositeAttributeFetchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/CompositeAttributeFetchImpl.java
@@ -1,79 +1,79 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class CompositeAttributeFetchImpl extends AbstractCompositeFetch implements CompositeAttributeFetch {
 	private final FetchSource source;
 	private final AttributeDefinition fetchedAttribute;
 
 	protected CompositeAttributeFetchImpl(
 			FetchSource source,
 			AttributeDefinition attributeDefinition,
 			ExpandingCompositeQuerySpace compositeQuerySpace,
 			boolean allowCollectionFetches) {
 		super(
 				compositeQuerySpace,
 				allowCollectionFetches,
 				source.getPropertyPath().append( attributeDefinition.getName() )
 		);
 		this.source = source;
 		this.fetchedAttribute = attributeDefinition;
 	}
 
 	@Override
 	public FetchSource getSource() {
 		return source;
 	}
 
 	@Override
 	public AttributeDefinition getFetchedAttributeDefinition() {
 		return fetchedAttribute;
 	}
 
 	@Override
 	public Type getFetchedType() {
 		return fetchedAttribute.getType();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return fetchedAttribute.isNullable();
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return source.resolveEntityReference();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EncapsulatedEntityIdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EncapsulatedEntityIdentifierDescription.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EncapsulatedEntityIdentifierDescription.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EncapsulatedEntityIdentifierDescription.java
index 6dcb852ba5..4dca40bdb2 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EncapsulatedEntityIdentifierDescription.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EncapsulatedEntityIdentifierDescription.java
@@ -1,57 +1,57 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityIdentifierDescription;
-import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityIdentifierDescription;
+import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.type.CompositeType;
 
 /**
  * Models a composite entity identifier that is encapsulated (meaning there is a composite class and a single
  * attribute that encapsulates the composite value).
  *
  * @author Steve Ebersole
  */
 public class EncapsulatedEntityIdentifierDescription
 		extends AbstractCompositeEntityIdentifierDescription
 		implements ExpandingEntityIdentifierDescription {
 
 	/**
 	 * Build an encapsulated version of a composite EntityIdentifierDescription
 	 *
 	 * @param entityReference The entity whose identifier we describe
 	 * @param compositeQuerySpace The query space we are mapped to.
 	 * @param compositeType The type representing this composition
 	 * @param propertyPath The property path (informational)
 	 */
 	protected EncapsulatedEntityIdentifierDescription(
 			EntityReference entityReference,
 			ExpandingCompositeQuerySpace compositeQuerySpace,
 			CompositeType compositeType,
 			PropertyPath propertyPath) {
 		super( entityReference, compositeQuerySpace, compositeType, propertyPath );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EntityAttributeFetchImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EntityAttributeFetchImpl.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EntityAttributeFetchImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EntityAttributeFetchImpl.java
index b8d164daa7..03f83f8764 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EntityAttributeFetchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EntityAttributeFetchImpl.java
@@ -1,100 +1,99 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
-import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityAttributeFetchImpl extends AbstractEntityReference implements EntityFetch {
 	private final FetchSource fetchSource;
 	private final AttributeDefinition fetchedAttribute;
 	private final FetchStrategy fetchStrategy;
 
 	public EntityAttributeFetchImpl(
 			ExpandingFetchSource fetchSource,
 			AssociationAttributeDefinition fetchedAttribute,
 			FetchStrategy fetchStrategy,
 			ExpandingEntityQuerySpace entityQuerySpace) {
 		super(
 				entityQuerySpace,
 				fetchSource.getPropertyPath().append( fetchedAttribute.getName() )
 		);
 
 		this.fetchSource = fetchSource;
 		this.fetchedAttribute = fetchedAttribute;
 		this.fetchStrategy = fetchStrategy;
 	}
 
 	@Override
 	public FetchSource getSource() {
 		return fetchSource;
 	}
 
 	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
 	@Override
 	public EntityType getFetchedType() {
 		return (EntityType) fetchedAttribute.getType();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return fetchedAttribute.isNullable();
 	}
 
 	@Override
 	public String getAdditionalJoinConditions() {
 		return null;
 	}
 
 	@Override
 	public String[] toSqlSelectFragments(String alias) {
 		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
 		// todo : allow bi-directional key-many-to-one fetches?
 		//		those do cause problems in Loader; question is whether those are indicative of that situation or
 		// 		of Loaders ability to handle it.
 	}
 
 	@Override
 	public AttributeDefinition getFetchedAttributeDefinition() {
 		return fetchedAttribute;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EntityReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EntityReturnImpl.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EntityReturnImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EntityReturnImpl.java
index 515a20a310..a43b320753 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/EntityReturnImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/EntityReturnImpl.java
@@ -1,52 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.EntityReturn;
+import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReturnImpl extends AbstractEntityReference implements EntityReturn, ExpandingFetchSource {
 	public EntityReturnImpl(EntityDefinition entityDefinition, ExpandingQuerySpaces querySpaces) {
 		super(
 				querySpaces.makeRootEntityQuerySpace(
 						querySpaces.generateImplicitUid(),
 						entityDefinition.getEntityPersister()
 				),
 				new PropertyPath( entityDefinition.getEntityPersister().getEntityName() )
 		);
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
 		// nothing to do here really
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/NestedCompositeAttributeFetchImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/NestedCompositeAttributeFetchImpl.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/NestedCompositeAttributeFetchImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/NestedCompositeAttributeFetchImpl.java
index fc27669859..f16e95d96a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/NestedCompositeAttributeFetchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/NestedCompositeAttributeFetchImpl.java
@@ -1,79 +1,79 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class NestedCompositeAttributeFetchImpl extends AbstractCompositeFetch implements CompositeAttributeFetch {
 	private final FetchSource source;
 	private final AttributeDefinition fetchedAttributeDefinition;
 
 	public NestedCompositeAttributeFetchImpl(
 			FetchSource source,
 			AttributeDefinition fetchedAttributeDefinition,
 			ExpandingCompositeQuerySpace compositeQuerySpace,
 			boolean allowCollectionFetches) {
 		super(
 				compositeQuerySpace,
 				allowCollectionFetches,
 				source.getPropertyPath().append( fetchedAttributeDefinition.getName() )
 		);
 		this.source = source;
 		this.fetchedAttributeDefinition = fetchedAttributeDefinition;
 	}
 
 	@Override
 	public FetchSource getSource() {
 		return source;
 	}
 
 	@Override
 	public Type getFetchedType() {
 		return fetchedAttributeDefinition.getType();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return fetchedAttributeDefinition.isNullable();
 	}
 
 	@Override
 	public AttributeDefinition getFetchedAttributeDefinition() {
 		return fetchedAttributeDefinition;
 	}
 
 	@Override
 	public EntityReference resolveEntityReference() {
 		return source.resolveEntityReference();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/NonEncapsulatedEntityIdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/NonEncapsulatedEntityIdentifierDescription.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/NonEncapsulatedEntityIdentifierDescription.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/NonEncapsulatedEntityIdentifierDescription.java
index 9aaf0739ab..a3ae80e2c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/NonEncapsulatedEntityIdentifierDescription.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/NonEncapsulatedEntityIdentifierDescription.java
@@ -1,60 +1,58 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.type.CompositeType;
-import org.hibernate.type.Type;
 
 /**
  * Models a composite entity identifier that is non-encapsulated (meaning there is no composite class, no
  * single attribute that encapsulates the composite value).
  *
  * @author Steve Ebersole
  */
 public class NonEncapsulatedEntityIdentifierDescription extends AbstractCompositeEntityIdentifierDescription {
 	/**
 	 * Build a non-encapsulated version of a composite EntityIdentifierDescription
 	 *
 	 * @param entityReference The entity whose identifier we describe
 	 * @param compositeQuerySpace The query space we are mapped to.
 	 * @param compositeType The type representing this composition
 	 * @param propertyPath The property path (informational)
 	 */
 	public NonEncapsulatedEntityIdentifierDescription(
 			EntityReference entityReference,
 			ExpandingCompositeQuerySpace compositeQuerySpace,
 			CompositeType compositeType,
 			PropertyPath propertyPath) {
 		super(
 				entityReference,
 				compositeQuerySpace,
 				compositeType,
 				propertyPath
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/ScalarReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/ScalarReturnImpl.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/ScalarReturnImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/ScalarReturnImpl.java
index 31ae0ecf93..dcc86b388e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/ScalarReturnImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/ScalarReturnImpl.java
@@ -1,30 +1,30 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
 /**
  * @author Steve Ebersole
  */
 public class ScalarReturnImpl {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/SimpleEntityIdentifierDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/SimpleEntityIdentifierDescriptionImpl.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/SimpleEntityIdentifierDescriptionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/SimpleEntityIdentifierDescriptionImpl.java
index 05a81b5903..466307840d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/returns/SimpleEntityIdentifierDescriptionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/returns/SimpleEntityIdentifierDescriptionImpl.java
@@ -1,41 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.returns;
+package org.hibernate.loader.plan.build.internal.returns;
 
-import org.hibernate.loader.plan2.spi.EntityIdentifierDescription;
+import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
 
 /**
  * @author Steve Ebersole
  */
 public class SimpleEntityIdentifierDescriptionImpl implements EntityIdentifierDescription {
 	@Override
 	public boolean hasFetches() {
 		return false;
 	}
 
 	@Override
 	public boolean hasBidirectionalEntityReferences() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/AbstractExpandingSourceQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/AbstractExpandingSourceQuerySpace.java
similarity index 84%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/AbstractExpandingSourceQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/AbstractExpandingSourceQuerySpace.java
index 7442aef053..6e91cc5dcf 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/AbstractExpandingSourceQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/AbstractExpandingSourceQuerySpace.java
@@ -1,53 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.Join;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractExpandingSourceQuerySpace extends AbstractQuerySpace implements ExpandingQuerySpace {
 
 	public AbstractExpandingSourceQuerySpace(
 			String uid,
 			Disposition disposition,
 			ExpandingQuerySpaces querySpaces,
 			boolean canJoinsBeRequired) {
 		super( uid, disposition, querySpaces, canJoinsBeRequired );
 	}
 
 	@Override
 	public void addJoin(Join join) {
 		internalGetJoins().add( join );
 	}
 
 	@Override
 	public ExpandingQuerySpaces getExpandingQuerySpaces() {
 		return super.getExpandingQuerySpaces();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/AbstractQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/AbstractQuerySpace.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/AbstractQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/AbstractQuerySpace.java
index 3385f4e80b..0093f99057 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/AbstractQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/AbstractQuerySpace.java
@@ -1,115 +1,115 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.AbstractPlanNode;
-import org.hibernate.loader.plan2.spi.Join;
-import org.hibernate.loader.plan2.spi.QuerySpace;
-import org.hibernate.loader.plan2.spi.QuerySpaces;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.AbstractPlanNode;
+import org.hibernate.loader.plan.spi.Join;
+import org.hibernate.loader.plan.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.QuerySpaces;
 
 /**
  * Convenience base class for QuerySpace implementations.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractQuerySpace extends AbstractPlanNode implements QuerySpace {
 	private final String uid;
 	private final Disposition disposition;
 	private final ExpandingQuerySpaces querySpaces;
 	private final boolean canJoinsBeRequired;
 
 	private List<Join> joins;
 
 	public AbstractQuerySpace(
 			String uid,
 			Disposition disposition,
 			ExpandingQuerySpaces querySpaces,
 			boolean canJoinsBeRequired) {
 		super( querySpaces.getSessionFactory() );
 		this.uid = uid;
 		this.disposition = disposition;
 		this.querySpaces = querySpaces;
 		this.canJoinsBeRequired = canJoinsBeRequired;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return super.sessionFactory();
 	}
 
 	// todo : copy ctor - that depends how graph copying works here...
 
 
 	/**
 	 * Can any joins created from here (with this as the left-hand side) be required joins?
 	 *
 	 * @return {@code true} indicates joins can be required; {@code false} indicates they cannot.
 	 */
 	public boolean canJoinsBeRequired() {
 		return canJoinsBeRequired;
 	}
 
 	/**
 	 * Provides subclasses access to the spaces to which this space belongs.
 	 *
 	 * @return The query spaces
 	 */
 	public QuerySpaces getQuerySpaces() {
 		return querySpaces;
 	}
 
 	protected ExpandingQuerySpaces getExpandingQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	public String getUid() {
 		return uid;
 	}
 
 	@Override
 	public Disposition getDisposition() {
 		return disposition;
 	}
 
 	@Override
 	public Iterable<Join> getJoins() {
 		return joins == null
 				? Collections.<Join>emptyList()
 				: joins;
 	}
 
 	protected List<Join> internalGetJoins() {
 		if ( joins == null ) {
 			joins = new ArrayList<Join>();
 		}
 
 		return joins;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CollectionQuerySpaceImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CollectionQuerySpaceImpl.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CollectionQuerySpaceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CollectionQuerySpaceImpl.java
index 2e27a99255..e8eab8310e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CollectionQuerySpaceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CollectionQuerySpaceImpl.java
@@ -1,89 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
-import org.hibernate.loader.plan2.build.spi.ExpandingCollectionQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.Join;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.CollectionPropertyNames;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.PropertyMapping;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionQuerySpaceImpl extends AbstractQuerySpace implements ExpandingCollectionQuerySpace {
 	private final CollectionPersister persister;
 
 	public CollectionQuerySpaceImpl(
 			CollectionPersister persister,
 			String uid,
 			ExpandingQuerySpaces querySpaces,
 			boolean canJoinsBeRequired) {
 		super( uid, Disposition.COLLECTION, querySpaces, canJoinsBeRequired );
 		this.persister = persister;
 	}
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return persister;
 	}
 
 	@Override
 	public PropertyMapping getPropertyMapping() {
 		return (PropertyMapping) persister;
 	}
 
 	public String[] toAliasedColumns(String alias, String propertyName) {
 		final QueryableCollection queryableCollection = (QueryableCollection) persister;
 		if ( propertyName.equals( CollectionPropertyNames.COLLECTION_ELEMENTS ) ) {
 			return queryableCollection.getElementColumnNames( alias );
 		}
 		else if ( propertyName.equals( CollectionPropertyNames.COLLECTION_INDICES ) ) {
 			return queryableCollection.getIndexColumnNames( alias );
 		}
 		else {
 			throw new IllegalArgumentException(
 					String.format(
 							"Collection propertyName must be either %s or %s; instead it was %s.",
 							CollectionPropertyNames.COLLECTION_ELEMENTS,
 							CollectionPropertyNames.COLLECTION_INDICES,
 							propertyName
 					)
 			);
 		}
 	}
 
 	@Override
 	public void addJoin(Join join) {
 		internalGetJoins().add( join );
 	}
 
 	@Override
 	public ExpandingQuerySpaces getExpandingQuerySpaces() {
 		return super.getExpandingQuerySpaces();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CompositePropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CompositePropertyMapping.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CompositePropertyMapping.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CompositePropertyMapping.java
index 11eef3e919..22797c52f1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CompositePropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CompositePropertyMapping.java
@@ -1,136 +1,136 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
 import org.hibernate.QueryException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * A PropertyMapping for handling composites!  Woohoo!
  * <p/>
  * TODO : Consider moving this into the attribute/association walking SPI (org.hibernate.persister.walking) and
  * having the notion of PropertyMapping built and exposed there
  * <p/>
  * There is duplication here too wrt {@link org.hibernate.hql.internal.ast.tree.ComponentJoin.ComponentPropertyMapping}.
  * like above, consider moving to a singly-defined CompositePropertyMapping in the attribute/association walking SPI
  *
  * @author Steve Ebersole
  */
 public class CompositePropertyMapping implements PropertyMapping {
 	private final CompositeType compositeType;
 	private final PropertyMapping parentPropertyMapping;
 	private final String parentPropertyName;
 
 	/**
 	 * Builds a CompositePropertyMapping
 	 *
 	 * @param compositeType The composite being described by this PropertyMapping
 	 * @param parentPropertyMapping The PropertyMapping of our parent (composites have to have a parent/owner)
 	 * @param parentPropertyName The name of this composite within the parentPropertyMapping
 	 */
 	public CompositePropertyMapping(
 			CompositeType compositeType,
 			PropertyMapping parentPropertyMapping,
 			String parentPropertyName) {
 		this.compositeType = compositeType;
 		this.parentPropertyMapping = parentPropertyMapping;
 		this.parentPropertyName = parentPropertyName;
 	}
 
 	@Override
 	public Type toType(String propertyName) throws QueryException {
 		return parentPropertyMapping.toType( toParentPropertyPath( propertyName ) );
 	}
 
 	/**
 	 * Used to build a property path relative to {@link #parentPropertyMapping}.  First, the incoming
 	 * propertyName argument is validated (using {@link #checkIncomingPropertyName}).  Then the
 	 * relative path is built (using {@link #resolveParentPropertyPath}).
 	 *
 	 * @param propertyName The incoming propertyName.
 	 *
 	 * @return The relative path.
 	 */
 	protected String toParentPropertyPath(String propertyName) {
 		checkIncomingPropertyName( propertyName );
 		return resolveParentPropertyPath( propertyName );
 	}
 
 	/**
 	 * Used to check the validity of the propertyName argument passed into {@link #toType(String)},
 	 * {@link #toColumns(String, String)} and {@link #toColumns(String)}.
 	 *
 	 * @param propertyName The incoming propertyName argument to validate
 	 */
 	protected void checkIncomingPropertyName(String propertyName) {
 		if ( propertyName == null ) {
 			throw new NullPointerException( "Provided property name cannot be null" );
 		}
 
 		//if ( propertyName.contains( "." ) ) {
 		//	throw new IllegalArgumentException(
 		//			"Provided property name cannot contain paths (dots) [" + propertyName + "]"
 		//	);
 		//}
 	}
 
 	/**
 	 * Builds the relative path.  Used to delegate {@link #toType(String)},
 	 * {@link #toColumns(String, String)} and {@link #toColumns(String)} calls out to {@link #parentPropertyMapping}.
 	 * <p/>
 	 * Called from {@link #toParentPropertyPath}.
 	 * <p/>
 	 * Override this to adjust how the relative property path is built for this mapping.
 	 *
 	 * @param propertyName The incoming property name to "path append".
 	 *
 	 * @return The relative path
 	 */
 	protected String resolveParentPropertyPath(String propertyName) {
 		if ( StringHelper.isEmpty( parentPropertyName ) ) {
 			return propertyName;
 		}
 		else {
 			return parentPropertyName + '.' + propertyName;
 		}
 	}
 
 	@Override
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return parentPropertyMapping.toColumns( alias, toParentPropertyPath( propertyName ) );
 	}
 
 	@Override
 	public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
 		return parentPropertyMapping.toColumns( toParentPropertyPath( propertyName ) );
 	}
 
 	@Override
 	public CompositeType getType() {
 		return compositeType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CompositeQuerySpaceImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CompositeQuerySpaceImpl.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CompositeQuerySpaceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CompositeQuerySpaceImpl.java
index 454447902f..38d1dbced6 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/CompositeQuerySpaceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/CompositeQuerySpaceImpl.java
@@ -1,54 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
 import org.hibernate.persister.entity.PropertyMapping;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeQuerySpaceImpl extends AbstractExpandingSourceQuerySpace implements ExpandingCompositeQuerySpace {
 	private final CompositePropertyMapping compositeSubPropertyMapping;
 
 	public CompositeQuerySpaceImpl(
 			CompositePropertyMapping compositeSubPropertyMapping,
 			String uid,
 			ExpandingQuerySpaces querySpaces,
 			boolean canJoinsBeRequired) {
 		super( uid, Disposition.COMPOSITE, querySpaces, canJoinsBeRequired );
 		this.compositeSubPropertyMapping = compositeSubPropertyMapping;
 	}
 
 	@Override
 	public PropertyMapping getPropertyMapping() {
 		return compositeSubPropertyMapping;
 	}
 
 	@Override
 	public String[] toAliasedColumns(String alias, String propertyName) {
 		return compositeSubPropertyMapping.toColumns( alias,propertyName );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/EntityQuerySpaceImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/EntityQuerySpaceImpl.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/EntityQuerySpaceImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/EntityQuerySpaceImpl.java
index 4f5e119dd5..68d7ba57a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/EntityQuerySpaceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/EntityQuerySpaceImpl.java
@@ -1,95 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityQuerySpaceImpl extends AbstractExpandingSourceQuerySpace implements ExpandingEntityQuerySpace {
 	private final EntityPersister persister;
 
 	public EntityQuerySpaceImpl(
 			EntityPersister persister,
 			String uid,
 			ExpandingQuerySpaces querySpaces,
 			boolean canJoinsBeRequired) {
 		super( uid, Disposition.ENTITY, querySpaces, canJoinsBeRequired );
 		this.persister = persister;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return super.sessionFactory();
 	}
 
 	@Override
 	public PropertyMapping getPropertyMapping() {
 		// entity persisters are typically PropertyMapping implementors, but this is part of the funky
 		// "optional interface hierarchy" for entity persisters.  The internal ones all implement
 		// PropertyMapping...
 		return (PropertyMapping) persister;
 	}
 
 	@Override
 	public String[] toAliasedColumns(String alias, String propertyName) {
 		return getPropertyMapping().toColumns( alias, propertyName );
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	@Override
 	public ExpandingCompositeQuerySpace makeCompositeIdentifierQuerySpace() {
 		final String compositeQuerySpaceUid = getUid() + "-id";
 		final ExpandingCompositeQuerySpace rhs = getExpandingQuerySpaces().makeCompositeQuerySpace(
 				compositeQuerySpaceUid,
 				new CompositePropertyMapping(
 						(CompositeType) getEntityPersister().getIdentifierType(),
 						(PropertyMapping) getEntityPersister(),
 						getEntityPersister().getIdentifierPropertyName()
 				),
 				canJoinsBeRequired()
 		);
 		final JoinDefinedByMetadata join = JoinHelper.INSTANCE.createCompositeJoin(
 				this,
 				EntityPersister.ENTITY_ID,
 				rhs,
 				canJoinsBeRequired(),
 				(CompositeType) persister.getIdentifierType()
 		);
 		internalGetJoins().add( join );
 
 		return rhs;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/JoinHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/JoinHelper.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/JoinHelper.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/JoinHelper.java
index 4ca781c681..79d0204515 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/JoinHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/JoinHelper.java
@@ -1,107 +1,107 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.spi.CollectionQuerySpace;
-import org.hibernate.loader.plan2.spi.CompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.EntityQuerySpace;
-import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
-import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.CollectionQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeQuerySpace;
+import org.hibernate.loader.plan.spi.EntityQuerySpace;
+import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan.spi.QuerySpace;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class JoinHelper {
 	/**
 	 * Singleton access
 	 */
 	public static final JoinHelper INSTANCE = new JoinHelper();
 
 	private JoinHelper() {
 	}
 
 	public JoinDefinedByMetadata createEntityJoin(
 			QuerySpace leftHandSide,
 			String lhsPropertyName,
 			EntityQuerySpace rightHandSide,
 			boolean rightHandSideRequired,
 			EntityType joinedPropertyType,
 			SessionFactoryImplementor sessionFactory) {
 		return new JoinImpl(
 				leftHandSide,
 				lhsPropertyName,
 				rightHandSide,
 				determineRhsColumnNames( joinedPropertyType, sessionFactory ),
 				joinedPropertyType,
 				rightHandSideRequired
 		);
 	}
 
 	public JoinDefinedByMetadata createCollectionJoin(
 			QuerySpace leftHandSide,
 			String lhsPropertyName,
 			CollectionQuerySpace rightHandSide,
 			boolean rightHandSideRequired,
 			CollectionType joinedPropertyType,
 			SessionFactoryImplementor sessionFactory) {
 		return new JoinImpl(
 				leftHandSide,
 				lhsPropertyName,
 				rightHandSide,
 				joinedPropertyType.getAssociatedJoinable( sessionFactory ).getKeyColumnNames(),
 				joinedPropertyType,
 				rightHandSideRequired
 		);
 	}
 
 	public JoinDefinedByMetadata createCompositeJoin(
 			QuerySpace leftHandSide,
 			String lhsPropertyName,
 			CompositeQuerySpace rightHandSide,
 			boolean rightHandSideRequired,
 			CompositeType joinedPropertyType) {
 		return new JoinImpl(
 				leftHandSide,
 				lhsPropertyName,
 				rightHandSide,
 				null,
 				joinedPropertyType,
 				rightHandSideRequired
 		);
 	}
 
 	private static String[] determineRhsColumnNames(EntityType entityType, SessionFactoryImplementor sessionFactory) {
 		final Joinable persister = entityType.getAssociatedJoinable( sessionFactory );
 		return entityType.getRHSUniqueKeyPropertyName() == null ?
 				persister.getKeyColumnNames() :
 				( (PropertyMapping) persister ).toColumns( entityType.getRHSUniqueKeyPropertyName() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/JoinImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/JoinImpl.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/JoinImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/JoinImpl.java
index 2220612128..87fea0ad77 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/JoinImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/JoinImpl.java
@@ -1,110 +1,110 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
-import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan.spi.QuerySpace;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public class JoinImpl implements JoinDefinedByMetadata {
 	private final QuerySpace leftHandSide;
 	private final QuerySpace rightHandSide;
 
 	private final String lhsPropertyName;
 
 	private final String[] rhsColumnNames;
 	private final boolean rightHandSideRequired;
 	private final Type joinedPropertyType;
 
 	public JoinImpl(
 			QuerySpace leftHandSide,
 			String lhsPropertyName,
 			QuerySpace rightHandSide,
 			String[] rhsColumnNames,
 			Type joinedPropertyType,
 			boolean rightHandSideRequired) {
 		this.leftHandSide = leftHandSide;
 		this.lhsPropertyName = lhsPropertyName;
 		this.rightHandSide = rightHandSide;
 		this.rhsColumnNames = rhsColumnNames;
 		this.rightHandSideRequired = rightHandSideRequired;
 		this.joinedPropertyType = joinedPropertyType;
 		if ( StringHelper.isEmpty( lhsPropertyName ) ) {
 			throw new IllegalArgumentException( "Incoming 'lhsPropertyName' parameter was empty" );
 		}
 	}
 
 	@Override
 	public QuerySpace getLeftHandSide() {
 		return leftHandSide;
 	}
 
 	@Override
 	public QuerySpace getRightHandSide() {
 		return rightHandSide;
 	}
 
 	@Override
 	public boolean isRightHandSideRequired() {
 		return rightHandSideRequired;
 	}
 
 	@Override
 	public String[] resolveAliasedLeftHandSideJoinConditionColumns(String leftHandSideTableAlias) {
 		return getLeftHandSide().toAliasedColumns( leftHandSideTableAlias, getJoinedPropertyName() );
 	}
 
 	@Override
 	public String[] resolveNonAliasedRightHandSideJoinConditionColumns() {
 		// for composite joins (joins whose rhs is a composite) we'd have no columns here.
 		// processing of joins tries to root out all composite joins, so the expectation
 		// is that this method would never be called on them
 		if ( rhsColumnNames == null ) {
 			throw new IllegalStateException(
 					"rhsColumnNames were null.  Generally that indicates a composite join, in which case calls to " +
 							"resolveAliasedLeftHandSideJoinConditionColumns are not allowed"
 			);
 		}
 		return rhsColumnNames;
 	}
 
 	@Override
 	public String getAnyAdditionalJoinConditions(String rhsTableAlias) {
 		return null;
 	}
 
 	@Override
 	public String getJoinedPropertyName() {
 		return lhsPropertyName;
 	}
 
 	@Override
 	public Type getJoinedPropertyType() {
 		return joinedPropertyType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/QuerySpaceHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/QuerySpaceHelper.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/QuerySpaceHelper.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/QuerySpaceHelper.java
index 780eac9d69..2df29ae426 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/QuerySpaceHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/QuerySpaceHelper.java
@@ -1,214 +1,213 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
-import org.hibernate.loader.plan2.build.spi.ExpandingCollectionQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpace;
-import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
+import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Gail Badner
  */
 public class QuerySpaceHelper {
 	/**
 	 * Singleton access
 	 */
 	public static final QuerySpaceHelper INSTANCE = new QuerySpaceHelper();
 
 	private QuerySpaceHelper() {
 	}
 
 	public ExpandingEntityQuerySpace makeEntityQuerySpace(
 			ExpandingQuerySpace lhsQuerySpace,
 			AssociationAttributeDefinition attribute,
 			String querySpaceUid,
 			FetchStrategy fetchStrategy) {
 		final EntityType fetchedType = (EntityType) attribute.getType();
 		final EntityPersister fetchedPersister = attribute.toEntityDefinition().getEntityPersister();
 
 		if ( fetchedPersister == null ) {
 			throw new WalkingException(
 					String.format(
 							"Unable to locate EntityPersister [%s] for fetch [%s]",
 							fetchedType.getAssociatedEntityName(),
 							attribute.getName()
 					)
 			);
 		}
 		// TODO: Queryable.isMultiTable() may be more broad than it needs to be...
 		final boolean isMultiTable = Queryable.class.cast( fetchedPersister ).isMultiTable();
 		final boolean required = lhsQuerySpace.canJoinsBeRequired() && !isMultiTable && !attribute.isNullable();
 
 		return makeEntityQuerySpace(
 				lhsQuerySpace,
 				fetchedPersister,
 				attribute.getName(),
 				(EntityType) attribute.getType(),
 				querySpaceUid,
 				required,
 				shouldIncludeJoin( fetchStrategy )
 		);
 	}
 
 	public ExpandingEntityQuerySpace makeEntityQuerySpace(
 			ExpandingQuerySpace lhsQuerySpace,
 			EntityPersister fetchedPersister,
 			String attributeName,
 			EntityType attributeType,
 			String querySpaceUid,
 			boolean required,
 			boolean shouldIncludeJoin) {
 
 		final ExpandingEntityQuerySpace rhs = lhsQuerySpace.getExpandingQuerySpaces().makeEntityQuerySpace(
 				querySpaceUid,
 				fetchedPersister,
 				required
 		);
 
 		if ( shouldIncludeJoin ) {
 			final JoinDefinedByMetadata join = JoinHelper.INSTANCE.createEntityJoin(
 					lhsQuerySpace,
 					attributeName,
 					rhs,
 					required,
 					attributeType,
 					fetchedPersister.getFactory()
 			);
 			lhsQuerySpace.addJoin( join );
 		}
 
 		return rhs;
 	}
 
 	public ExpandingCompositeQuerySpace makeCompositeQuerySpace(
 			ExpandingQuerySpace lhsQuerySpace,
 			AttributeDefinition attributeDefinition,
 			String querySpaceUid,
 			boolean shouldIncludeJoin) {
 		final boolean required = lhsQuerySpace.canJoinsBeRequired() && !attributeDefinition.isNullable();
  		return makeCompositeQuerySpace(
 				 lhsQuerySpace,
 				 new CompositePropertyMapping(
 						 (CompositeType) attributeDefinition.getType(),
 						 lhsQuerySpace.getPropertyMapping(),
 						 attributeDefinition.getName()
 				 ),
 				 attributeDefinition.getName(),
 				 (CompositeType) attributeDefinition.getType(),
 				 querySpaceUid,
 				 required,
 				 shouldIncludeJoin
 		 );
 	}
 
 	public ExpandingCompositeQuerySpace makeCompositeQuerySpace(
 			ExpandingQuerySpace lhsQuerySpace,
 			CompositePropertyMapping compositePropertyMapping,
 			String attributeName,
 			CompositeType attributeType,
 			String querySpaceUid,
 			boolean required,
 			boolean shouldIncludeJoin) {
 
 		final ExpandingCompositeQuerySpace rhs = lhsQuerySpace.getExpandingQuerySpaces().makeCompositeQuerySpace(
 				querySpaceUid,
 				compositePropertyMapping,
 				required
 		);
 
 		if ( shouldIncludeJoin ) {
 			final JoinDefinedByMetadata join = JoinHelper.INSTANCE.createCompositeJoin(
 					lhsQuerySpace,
 					attributeName,
 					rhs,
 					required,
 					attributeType
 			);
 			lhsQuerySpace.addJoin( join );
 		}
 
 		return rhs;
 	}
 
 	public ExpandingCollectionQuerySpace makeCollectionQuerySpace(
 			ExpandingQuerySpace lhsQuerySpace,
 			AssociationAttributeDefinition attributeDefinition,
 			String querySpaceUid,
 			FetchStrategy fetchStrategy) {
 
 		final CollectionType fetchedType = (CollectionType) attributeDefinition.getType();
 		final CollectionPersister fetchedPersister = attributeDefinition.toCollectionDefinition().getCollectionPersister();
 
 		if ( fetchedPersister == null ) {
 			throw new WalkingException(
 					String.format(
 							"Unable to locate CollectionPersister [%s] for fetch [%s]",
 							fetchedType.getRole(),
 							attributeDefinition.getName()
 					)
 			);
 		}
 
 		final boolean required = lhsQuerySpace.canJoinsBeRequired() && !attributeDefinition.isNullable();
 
 		final ExpandingCollectionQuerySpace rhs = lhsQuerySpace.getExpandingQuerySpaces().makeCollectionQuerySpace(
 				querySpaceUid,
 				fetchedPersister,
 				required
 		);
 
 		if ( shouldIncludeJoin( fetchStrategy ) ) {
 			final JoinDefinedByMetadata join = JoinHelper.INSTANCE.createCollectionJoin(
 					lhsQuerySpace,
 					attributeDefinition.getName(),
 					rhs,
 					required,
 					(CollectionType) attributeDefinition.getType(),
 					fetchedPersister.getFactory()
 			);
 			lhsQuerySpace.addJoin( join );
 		}
 
 		return rhs;
 	}
 
 	public boolean shouldIncludeJoin(FetchStrategy fetchStrategy) {
 		return fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/QuerySpacesImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/QuerySpacesImpl.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/QuerySpacesImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/QuerySpacesImpl.java
index 5289674d9a..5455f6634d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/spaces/QuerySpacesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/internal/spaces/QuerySpacesImpl.java
@@ -1,188 +1,188 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.internal.spaces;
+package org.hibernate.loader.plan.build.internal.spaces;
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.build.spi.ExpandingCollectionQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingCompositeQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingEntityQuerySpace;
-import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
-import org.hibernate.loader.plan2.spi.QuerySpace;
-import org.hibernate.loader.plan2.spi.QuerySpaceUidNotRegisteredException;
+import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
+import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.QuerySpaceUidNotRegisteredException;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Steve Ebersole
  */
 public class QuerySpacesImpl implements ExpandingQuerySpaces {
 	private static final Logger log = CoreLogging.logger( QuerySpacesImpl.class );
 
 	private final SessionFactoryImplementor sessionFactory;
 	private final List<QuerySpace> roots = new ArrayList<QuerySpace>();
 	private final Map<String,QuerySpace> querySpaceByUid = new ConcurrentHashMap<String, QuerySpace>();
 
 	public QuerySpacesImpl(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 
 	// QuerySpaces impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public List<QuerySpace> getRootQuerySpaces() {
 		return roots;
 	}
 
 	@Override
 	public QuerySpace findQuerySpaceByUid(String uid) {
 		return querySpaceByUid.get( uid );
 	}
 
 	@Override
 	public QuerySpace getQuerySpaceByUid(String uid) {
 		final QuerySpace space = findQuerySpaceByUid( uid );
 		if ( space == null ) {
 			throw new QuerySpaceUidNotRegisteredException( uid );
 		}
 		return space;
 	}
 
 // ExpandingQuerySpaces impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int implicitUidBase = 0;
 
 	@Override
 	public String generateImplicitUid() {
 		return "<gen:" + implicitUidBase++ + ">";
 	}
 
 	@Override
 	public ExpandingEntityQuerySpace makeRootEntityQuerySpace(String uid, EntityPersister entityPersister) {
 		final ExpandingEntityQuerySpace space = makeEntityQuerySpace( uid, entityPersister, true );
 		roots.add( space );
 		return space;
 	}
 
 	@Override
 	public ExpandingEntityQuerySpace makeEntityQuerySpace(
 			String uid,
 			EntityPersister entityPersister,
 			boolean canJoinsBeRequired) {
 
 		checkQuerySpaceDoesNotExist( uid );
 
 		final EntityQuerySpaceImpl space = new EntityQuerySpaceImpl(
 				entityPersister,
 				uid,
 				this,
 				canJoinsBeRequired
 		);
 		registerQuerySpace( space );
 
 		return space;
 	}
 
 	@Override
 	public ExpandingCollectionQuerySpace makeRootCollectionQuerySpace(String uid, CollectionPersister collectionPersister) {
 		final ExpandingCollectionQuerySpace space = makeCollectionQuerySpace( uid, collectionPersister, true );
 		roots.add( space );
 		return space;
 	}
 
 	@Override
 	public ExpandingCollectionQuerySpace makeCollectionQuerySpace(
 			String uid,
 			CollectionPersister collectionPersister,
 			boolean canJoinsBeRequired) {
 
 		checkQuerySpaceDoesNotExist( uid );
 
 		final ExpandingCollectionQuerySpace space = new CollectionQuerySpaceImpl(
 				collectionPersister,
 				uid,
 				this,
 				canJoinsBeRequired
 		);
 		registerQuerySpace( space );
 
 		return space;
 	}
 
 	@Override
 	public ExpandingCompositeQuerySpace makeCompositeQuerySpace(
 			String uid,
 			CompositePropertyMapping compositePropertyMapping,
 			boolean canJoinsBeRequired) {
 
 		checkQuerySpaceDoesNotExist( uid );
 
 		final ExpandingCompositeQuerySpace space = new CompositeQuerySpaceImpl(
 				compositePropertyMapping,
 				uid,
 				this,
 				canJoinsBeRequired
 		);
 		registerQuerySpace( space );
 
 		return space;
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	private void checkQuerySpaceDoesNotExist(String uid) {
 		if ( querySpaceByUid.containsKey( uid ) ) {
 			throw new IllegalStateException( "Encountered duplicate QuerySpace uid : " + uid );
 		}
 	}
 
 	/**
 	 * Feeds a QuerySpace into this spaces group.
 	 *
 	 * @param querySpace The space
 	 */
 	private void registerQuerySpace(QuerySpace querySpace) {
 		log.debugf(
 				"Adding QuerySpace : uid = %s -> %s]",
 				querySpace.getUid(),
 				querySpace
 		);
 		final QuerySpace previous = querySpaceByUid.put( querySpace.getUid(), querySpace );
 		if ( previous != null ) {
 			throw new IllegalStateException( "Encountered duplicate QuerySpace uid : " + querySpace.getUid() );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
index 978d4497b4..5a5092f612 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/AbstractLoadPlanBuildingAssociationVisitationStrategy.java
@@ -1,933 +1,933 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import java.util.ArrayDeque;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.MDC;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan2.build.internal.spaces.QuerySpacesImpl;
-import org.hibernate.loader.plan2.build.internal.returns.CollectionReturnImpl;
-import org.hibernate.loader.plan2.build.internal.returns.EntityReturnImpl;
-import org.hibernate.loader.plan2.spi.AttributeFetch;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
-import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
-import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
-import org.hibernate.loader.plan2.spi.CollectionReference;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
-import org.hibernate.loader.plan2.spi.CompositeFetch;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.FetchSource;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.build.internal.spaces.QuerySpacesImpl;
+import org.hibernate.loader.plan.build.internal.returns.CollectionReturnImpl;
+import org.hibernate.loader.plan.build.internal.returns.EntityReturnImpl;
+import org.hibernate.loader.plan.spi.AttributeFetch;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
+import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.FetchSource;
+import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.Type;
 
 /**
  * A LoadPlanBuilderStrategy is a strategy for building a LoadPlan.  LoadPlanBuilderStrategy is also a
  * AssociationVisitationStrategy, which is used in conjunction with visiting associations via walking
  * metamodel definitions.
  * <p/>
  * So this strategy defines a AssociationVisitationStrategy that walks the metamodel defined associations after
  * which is can then build a LoadPlan based on the visited associations.  {@link #determineFetchStrategy} Is the
  * main decision point
  *
  * @author Steve Ebersole
  *
- * @see org.hibernate.loader.plan2.build.spi.LoadPlanBuildingAssociationVisitationStrategy
+ * @see org.hibernate.loader.plan.build.spi.LoadPlanBuildingAssociationVisitationStrategy
  * @see org.hibernate.persister.walking.spi.AssociationVisitationStrategy
  */
 public abstract class AbstractLoadPlanBuildingAssociationVisitationStrategy
 		implements LoadPlanBuildingAssociationVisitationStrategy, LoadPlanBuildingContext {
 	private static final Logger log = Logger.getLogger( AbstractLoadPlanBuildingAssociationVisitationStrategy.class );
 	private static final String MDC_KEY = "hibernateLoadPlanWalkPath";
 
 	private final SessionFactoryImplementor sessionFactory;
 	private final QuerySpacesImpl querySpaces;
 
 	private final PropertyPathStack propertyPathStack = new PropertyPathStack();
 
 	private final ArrayDeque<ExpandingFetchSource> fetchSourceStack = new ArrayDeque<ExpandingFetchSource>();
 
 	protected AbstractLoadPlanBuildingAssociationVisitationStrategy(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 		this.querySpaces = new QuerySpacesImpl( sessionFactory );
 	}
 
 	public SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	@Override
 	public ExpandingQuerySpaces getQuerySpaces() {
 		return querySpaces;
 	}
 
 
 	// stack management ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static interface FetchStackAware {
 		public void poppedFromStack();
 	}
 
 	private void pushToStack(ExpandingFetchSource fetchSource) {
 		log.trace( "Pushing fetch source to stack : " + fetchSource );
 		propertyPathStack.push( fetchSource.getPropertyPath() );
 		fetchSourceStack.addFirst( fetchSource );
 	}
 
 	private ExpandingFetchSource popFromStack() {
 		final ExpandingFetchSource last = fetchSourceStack.removeFirst();
 		log.trace( "Popped fetch owner from stack : " + last );
 		propertyPathStack.pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 
 		return last;
 	}
 
 	private ExpandingFetchSource currentSource() {
 		return fetchSourceStack.peekFirst();
 	}
 
 	// top-level AssociationVisitationStrategy hooks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void start() {
 		if ( ! fetchSourceStack.isEmpty() ) {
 			throw new WalkingException(
 					"Fetch owner stack was not empty on start; " +
 							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
 			);
 		}
 		propertyPathStack.push( new PropertyPath() );
 	}
 
 	@Override
 	public void finish() {
 		propertyPathStack.pop();
 		MDC.remove( MDC_KEY );
 		fetchSourceStack.clear();
 	}
 
 
 	protected abstract void addRootReturn(Return rootReturn);
 
 
 	// Entity-level AssociationVisitationStrategy hooks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected boolean supportsRootEntityReturns() {
 		return true;
 	}
 
 	// Entities  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void startingEntity(EntityDefinition entityDefinition) {
 		// see if the EntityDefinition is a root...
 		final boolean isRoot = fetchSourceStack.isEmpty();
 		if ( ! isRoot ) {
 			// if not, this call should represent a fetch which should have been handled in #startingAttribute
 			return;
 		}
 
 		// if we get here, it is a root
 
 		log.tracef(
 				"%s Starting root entity : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 
 		if ( !supportsRootEntityReturns() ) {
 			throw new HibernateException( "This strategy does not support root entity returns" );
 		}
 
 		final EntityReturnImpl entityReturn = new EntityReturnImpl( entityDefinition, querySpaces );
 		addRootReturn( entityReturn );
 		pushToStack( entityReturn );
 
 		// also add an AssociationKey for the root so we can later on recognize circular references back to the root.
 		final Joinable entityPersister = (Joinable) entityDefinition.getEntityPersister();
 		associationKeyRegistered(
 				new AssociationKey( entityPersister.getTableName(), entityPersister.getKeyColumnNames() )
 		);
 	}
 
 	@Override
 	public void finishingEntity(EntityDefinition entityDefinition) {
 		// Only process the entityDefinition if it is for the root return.
 		final FetchSource currentSource = currentSource();
 		final boolean isRoot = EntityReturn.class.isInstance( currentSource ) &&
 				entityDefinition.getEntityPersister().equals( EntityReturn.class.cast( currentSource ).getEntityPersister() );
 		if ( !isRoot ) {
 			// if not, this call should represent a fetch which will be handled in #finishingAttribute
 			return;
 		}
 
 		// if we get here, it is a root
 		final ExpandingFetchSource popped = popFromStack();
 		checkPoppedEntity( popped, entityDefinition );
 
 		log.tracef(
 				"%s Finished root entity : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 	}
 
 	private void checkPoppedEntity(ExpandingFetchSource fetchSource, EntityDefinition entityDefinition) {
 		// make sure what we just fetchSource represents entityDefinition
 		if ( ! EntityReference.class.isInstance( fetchSource ) ) {
 			throw new WalkingException(
 					String.format(
 							"Mismatched FetchSource from stack on pop.  Expecting EntityReference(%s), but found %s",
 							entityDefinition.getEntityPersister().getEntityName(),
 							fetchSource
 					)
 			);
 		}
 
 		final EntityReference entityReference = (EntityReference) fetchSource;
 		// NOTE : this is not the most exhaustive of checks because of hierarchical associations (employee/manager)
 		if ( ! entityReference.getEntityPersister().equals( entityDefinition.getEntityPersister() ) ) {
 			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
 		}
 	}
 
 
 	// entity identifiers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		log.tracef(
 				"%s Starting entity identifier : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 
 		final EntityReference entityReference = (EntityReference) currentSource();
 
 		// perform some stack validation
 		if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
 			throw new WalkingException(
 					String.format(
 							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 							entityReference.getEntityPersister().getEntityName(),
 							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 					)
 			);
 		}
 
 		if ( ExpandingEntityIdentifierDescription.class.isInstance( entityReference.getIdentifierDescription() ) ) {
 			pushToStack( (ExpandingEntityIdentifierDescription) entityReference.getIdentifierDescription() );
 		}
 	}
 
 	@Override
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		// only pop from stack if the current source is ExpandingEntityIdentifierDescription..
 		final ExpandingFetchSource currentSource = currentSource();
 		if ( ! ExpandingEntityIdentifierDescription.class.isInstance( currentSource ) ) {
 			// in this case, the current source should be the entity that owns entityIdentifierDefinition
 			if ( ! EntityReference.class.isInstance( currentSource ) ) {
 				throw new WalkingException( "Unexpected state in FetchSource stack" );
 			}
 			final EntityReference entityReference = (EntityReference) currentSource;
 			if ( entityReference.getEntityPersister().getEntityKeyDefinition() != entityIdentifierDefinition ) {
 				throw new WalkingException(
 						String.format(
 								"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 								entityReference.getEntityPersister().getEntityName(),
 								entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 						)
 				);
 			}
 			return;
 		}
 
 		// the current source is ExpandingEntityIdentifierDescription...
 		final ExpandingEntityIdentifierDescription identifierDescription =
 				(ExpandingEntityIdentifierDescription) popFromStack();
 
 		// and then on the node before it (which should be the entity that owns the identifier being described)
 		final ExpandingFetchSource entitySource = currentSource();
 		if ( ! EntityReference.class.isInstance( entitySource ) ) {
 			throw new WalkingException( "Unexpected state in FetchSource stack" );
 		}
 		final EntityReference entityReference = (EntityReference) entitySource;
 		if ( entityReference.getIdentifierDescription() != identifierDescription ) {
 			throw new WalkingException(
 					String.format(
 							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 							entityReference.getEntityPersister().getEntityName(),
 							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 					)
 			);
 		}
 
 		log.tracef(
 				"%s Finished entity identifier : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 	}
 	// Collections ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private ArrayDeque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
 
 	private void pushToCollectionStack(CollectionReference collectionReference) {
 		log.trace( "Pushing collection reference to stack : " + collectionReference );
 		propertyPathStack.push( collectionReference.getPropertyPath() );
 		collectionReferenceStack.addFirst( collectionReference );
 	}
 
 	private CollectionReference popFromCollectionStack() {
 		final CollectionReference last = collectionReferenceStack.removeFirst();
 		log.trace( "Popped collection reference from stack : " + last );
 		propertyPathStack.pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 		return last;
 	}
 
 	private CollectionReference currentCollection() {
 		return collectionReferenceStack.peekFirst();
 	}
 
 	@Override
 	public void startingCollection(CollectionDefinition collectionDefinition) {
 		// see if the EntityDefinition is a root...
 		final boolean isRoot = fetchSourceStack.isEmpty();
 		if ( ! isRoot ) {
 			// if not, this call should represent a fetch which should have been handled in #startingAttribute
 			return;
 		}
 
 		log.tracef(
 				"%s Starting root collection : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 
 		// if we get here, it is a root
 		if ( ! supportsRootCollectionReturns() ) {
 			throw new HibernateException( "This strategy does not support root collection returns" );
 		}
 
 		final CollectionReturn collectionReturn = new CollectionReturnImpl( collectionDefinition, querySpaces );
 		pushToCollectionStack( collectionReturn );
 		addRootReturn( collectionReturn );
 
 		associationKeyRegistered(
 				new AssociationKey(
 						( (Joinable) collectionDefinition.getCollectionPersister() ).getTableName(),
 						( (Joinable) collectionDefinition.getCollectionPersister() ).getKeyColumnNames()
 				)
 		);
 	}
 
 	protected boolean supportsRootCollectionReturns() {
 		return true;
 	}
 
 	@Override
 	public void finishingCollection(CollectionDefinition collectionDefinition) {
 		final boolean isRoot = fetchSourceStack.isEmpty() && collectionReferenceStack.size() == 1;
 		if ( !isRoot ) {
 			// if not, this call should represent a fetch which will be handled in #finishingAttribute
 			return;
 		}
 
 		final CollectionReference popped = popFromCollectionStack();
 		checkedPoppedCollection( popped, collectionDefinition );
 
 		log.tracef(
 				"%s Finished root collection : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 	}
 
 	private void checkedPoppedCollection(CollectionReference poppedCollectionReference, CollectionDefinition collectionDefinition) {
 		// make sure what we just poppedCollectionReference represents collectionDefinition.
 		if ( ! poppedCollectionReference.getCollectionPersister().equals( collectionDefinition.getCollectionPersister() ) ) {
 			throw new WalkingException( "Mismatched CollectionReference from stack on pop" );
 		}
 	}
 
 	@Override
 	public void startingCollectionIndex(CollectionIndexDefinition indexDefinition) {
 		final Type indexType = indexDefinition.getType();
 		log.tracef(
 				"%s Starting collection index graph : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 
 		final CollectionReference collectionReference = currentCollection();
 		final CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
 
 		if ( indexType.isEntityType() || indexType.isComponentType() ) {
 			if ( indexGraph == null ) {
 				throw new WalkingException(
 						"CollectionReference did not return an expected index graph : " +
 								indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 			if ( !indexType.isAnyType() ) {
 				pushToStack( (ExpandingFetchSource) indexGraph );
 			}
 		}
 		else {
 			if ( indexGraph != null ) {
 				throw new WalkingException(
 						"CollectionReference returned an unexpected index graph : " +
 								indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 		}
 	}
 
 	@Override
 	public void finishingCollectionIndex(CollectionIndexDefinition indexDefinition) {
 		final Type indexType = indexDefinition.getType();
 
 		if ( indexType.isAnyType() ) {
 			// nothing to do because the index graph was not pushed in #startingCollectionIndex.
 		}
 		else if ( indexType.isEntityType() || indexType.isComponentType() ) {
 			// todo : validate the stack?
 			final ExpandingFetchSource fetchSource = popFromStack();
 			if ( !CollectionFetchableIndex.class.isInstance( fetchSource ) ) {
 				throw new WalkingException(
 						"CollectionReference did not return an expected index graph : " +
 								indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 		}
 
 		log.tracef(
 				"%s Finished collection index graph : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				indexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 		final Type elementType = elementDefinition.getType();
 		log.tracef(
 				"%s Starting collection element graph : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 
 		final CollectionReference collectionReference = currentCollection();
 		final CollectionFetchableElement elementGraph = collectionReference.getElementGraph();
 
 		if ( elementType.isAssociationType() || elementType.isComponentType() ) {
 			if ( elementGraph == null ) {
 				throw new IllegalStateException(
 						"CollectionReference did not return an expected element graph : " +
 								elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 			if ( !elementType.isAnyType() ) {
 				pushToStack( (ExpandingFetchSource) elementGraph );
 			}
 		}
 		else {
 			if ( elementGraph != null ) {
 				throw new IllegalStateException(
 						"CollectionReference returned an unexpected element graph : " +
 								elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				);
 			}
 		}
 	}
 
 	@Override
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 		final Type elementType = elementDefinition.getType();
 
 		if ( elementType.isAnyType() ) {
 			// nothing to do because the element graph was not pushed in #startingCollectionElement..
 		}
 		else if ( elementType.isComponentType() || elementType.isAssociationType()) {
 			// pop it from the stack
 			final ExpandingFetchSource popped = popFromStack();
 
 			// validation
 			if ( ! CollectionFetchableElement.class.isInstance( popped ) ) {
 				throw new WalkingException( "Mismatched FetchSource from stack on pop" );
 			}
 		}
 
 		log.tracef(
 				"%s Finished collection element graph : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void startingComposite(CompositionDefinition compositionDefinition) {
 		log.tracef(
 				"%s Starting composite : %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				compositionDefinition.getName()
 		);
 
 		if ( fetchSourceStack.isEmpty() && collectionReferenceStack.isEmpty() ) {
 			throw new HibernateException( "A component cannot be the root of a walk nor a graph" );
 		}
 
 		// No need to push anything here; it should have been pushed by
 		// #startingAttribute, #startingCollectionElements, #startingCollectionIndex, or #startingEntityIdentifier
 		final FetchSource currentSource = currentSource();
 		if ( !CompositeFetch.class.isInstance( currentSource ) &&
 				!CollectionFetchableElement.class.isInstance( currentSource ) &&
 				!CollectionFetchableIndex.class.isInstance( currentSource ) &&
 				!ExpandingEntityIdentifierDescription.class.isInstance( currentSource ) ) {
 			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
 		}
 	}
 
 	@Override
 	public void finishingComposite(CompositionDefinition compositionDefinition) {
 		// No need to pop anything here; it will be popped by
 		// #finishingAttribute, #finishingCollectionElements, #finishingCollectionIndex, or #finishingEntityIdentifier
 
 		log.tracef(
 				"%s Finishing composite : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				compositionDefinition.getName()
 		);
 	}
 
 	protected PropertyPath currentPropertyPath = new PropertyPath( "" );
 
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 		log.tracef(
 				"%s Starting attribute %s",
 				StringHelper.repeat( ">>", fetchSourceStack.size() ),
 				attributeDefinition
 		);
 
 		final Type attributeType = attributeDefinition.getType();
 
 		final boolean isComponentType = attributeType.isComponentType();
 		final boolean isAssociationType = attributeType.isAssociationType();
 		final boolean isBasicType = ! ( isComponentType || isAssociationType );
 		currentPropertyPath = currentPropertyPath.append( attributeDefinition.getName() );
 		if ( isBasicType ) {
 			return true;
 		}
 		else if ( isAssociationType ) {
 			// also handles any type attributes...
 			return handleAssociationAttribute( (AssociationAttributeDefinition) attributeDefinition );
 		}
 		else {
 			return handleCompositeAttribute( attributeDefinition );
 		}
 	}
 
 	@Override
 	public void finishingAttribute(AttributeDefinition attributeDefinition) {
 		final Type attributeType = attributeDefinition.getType();
 
 		if ( attributeType.isAssociationType() ) {
 			final AssociationAttributeDefinition associationAttributeDefinition =
 					(AssociationAttributeDefinition) attributeDefinition;
 			if ( attributeType.isAnyType() ) {
 				// Nothing to do because AnyFetch does not implement ExpandingFetchSource (i.e., it cannot be pushed/popped).
 			}
 			else if ( attributeType.isEntityType() ) {
 				final ExpandingFetchSource source = currentSource();
 				// One way to find out if the fetch was pushed is to check the fetch strategy; rather than recomputing
 				// the fetch strategy, simply check if current source's fetched attribute definition matches
 				// associationAttributeDefinition.
 				if ( AttributeFetch.class.isInstance( source ) &&
 						associationAttributeDefinition.equals( AttributeFetch.class.cast( source ).getFetchedAttributeDefinition() ) ) {
 					final ExpandingFetchSource popped = popFromStack();
 					checkPoppedEntity( popped, associationAttributeDefinition.toEntityDefinition() );
 				}
 			}
 			else if ( attributeType.isCollectionType() ) {
 				final CollectionReference currentCollection = currentCollection();
 				// One way to find out if the fetch was pushed is to check the fetch strategy; rather than recomputing
 				// the fetch strategy, simply check if current collection's fetched attribute definition matches
 				// associationAttributeDefinition.
 				if ( AttributeFetch.class.isInstance( currentCollection ) &&
 						associationAttributeDefinition.equals( AttributeFetch.class.cast( currentCollection ).getFetchedAttributeDefinition() ) ) {
 					final CollectionReference popped = popFromCollectionStack();
 					checkedPoppedCollection( popped, associationAttributeDefinition.toCollectionDefinition() );
 				}
 			}
 		}
 		else if ( attributeType.isComponentType() ) {
 			// CompositeFetch is always pushed, during #startingAttribute(),
 			// so pop the current fetch owner, and make sure what we just popped represents this composition
 			final ExpandingFetchSource popped = popFromStack();
 			if ( !CompositeAttributeFetch.class.isInstance( popped ) ) {
 				throw new WalkingException(
 						String.format(
 								"Mismatched FetchSource from stack on pop; expected: CompositeAttributeFetch; actual: [%s]",
 								popped
 						)
 				);
 			}
 			final CompositeAttributeFetch poppedAsCompositeAttributeFetch = (CompositeAttributeFetch) popped;
 			if ( !attributeDefinition.equals( poppedAsCompositeAttributeFetch.getFetchedAttributeDefinition() ) ) {
 				throw new WalkingException(
 						String.format(
 								"Mismatched CompositeAttributeFetch from stack on pop; expected fetch for attribute: [%s]; actual: [%s]",
 								attributeDefinition,
 								poppedAsCompositeAttributeFetch.getFetchedAttributeDefinition()
 						)
 				);
 			}
 		}
 
 		log.tracef(
 				"%s Finishing up attribute : %s",
 				StringHelper.repeat( "<<", fetchSourceStack.size() ),
 				attributeDefinition
 		);
 		currentPropertyPath = currentPropertyPath.getParent();
 	}
 
 	private Map<AssociationKey,FetchSource> fetchedAssociationKeySourceMap = new HashMap<AssociationKey, FetchSource>();
 
 	@Override
 	public boolean isDuplicateAssociationKey(AssociationKey associationKey) {
 		return fetchedAssociationKeySourceMap.containsKey( associationKey );
 	}
 
 	@Override
 	public void associationKeyRegistered(AssociationKey associationKey) {
 		// todo : use this information to maintain a map of AssociationKey->FetchSource mappings (associationKey + current FetchSource stack entry)
 		//		that mapping can then be used in #foundCircularAssociationKey to build the proper BiDirectionalEntityFetch
 		//		based on the mapped owner
 		log.tracef(
 				"%s Registering AssociationKey : %s -> %s",
 				StringHelper.repeat( "..", fetchSourceStack.size() ),
 				associationKey,
 				currentSource()
 		);
 		fetchedAssociationKeySourceMap.put( associationKey, currentSource() );
 	}
 
 	@Override
 	public FetchSource registeredFetchSource(AssociationKey associationKey) {
 		return fetchedAssociationKeySourceMap.get( associationKey );
 	}
 
 	@Override
 	public void foundCircularAssociation(AssociationAttributeDefinition attributeDefinition) {
 		final FetchStrategy fetchStrategy = determineFetchStrategy( attributeDefinition );
 		if ( fetchStrategy.getStyle() != FetchStyle.JOIN ) {
 			return; // nothing to do
 		}
 
 		final AssociationKey associationKey = attributeDefinition.getAssociationKey();
 
 		// go ahead and build the bidirectional fetch
 		if ( attributeDefinition.getAssociationNature() == AssociationAttributeDefinition.AssociationNature.ENTITY ) {
 			final Joinable currentEntityPersister = (Joinable) currentSource().resolveEntityReference().getEntityPersister();
 			final AssociationKey currentEntityReferenceAssociationKey =
 					new AssociationKey( currentEntityPersister.getTableName(), currentEntityPersister.getKeyColumnNames() );
 			// if associationKey is equal to currentEntityReferenceAssociationKey
 			// that means that the current EntityPersister has a single primary key attribute
 			// (i.e., derived attribute) which is mapped by attributeDefinition.
 			// This is not a bidirectional association.
 			// TODO: AFAICT, to avoid an overflow, the associated entity must already be loaded into the session, or
 			// it must be loaded when the ID for the dependent entity is resolved. Is there some other way to
 			// deal with this???
 			final FetchSource registeredFetchSource = registeredFetchSource( associationKey );
 			if ( registeredFetchSource != null && ! associationKey.equals( currentEntityReferenceAssociationKey ) ) {
 				currentSource().buildBidirectionalEntityReference(
 						attributeDefinition,
 						fetchStrategy,
 						registeredFetchSource( associationKey ).resolveEntityReference()
 				);
 			}
 		}
 		else {
 			// Do nothing for collection
 		}
 	}
 
 // TODO: is the following still useful???
 //	@Override
 //	public void foundCircularAssociationKey(AssociationKey associationKey, AttributeDefinition attributeDefinition) {
 //		// use this information to create the bi-directional EntityReference (as EntityFetch) instances
 //		final FetchSource owningFetchSource = fetchedAssociationKeySourceMap.get( associationKey );
 //		if ( owningFetchSource == null ) {
 //			throw new IllegalStateException(
 //					String.format(
 //							"Expecting AssociationKey->FetchSource mapping for %s",
 //							associationKey.toString()
 //					)
 //			);
 //		}
 //
 //		final FetchSource currentFetchSource = currentSource();
 //		( (ExpandingFetchSource) currentFetchSource ).addCircularFetch( new CircularFetch(  ))
 //
 //		currentFetchOwner().addFetch( new CircularFetch( currentSource(), fetchSource, attributeDefinition ) );
 //	}
 //
 //	public static class CircularFetch implements EntityFetch, EntityReference {
 //		private final FetchOwner circularFetchOwner;
 //		private final FetchOwner associationOwner;
 //		private final AttributeDefinition attributeDefinition;
 //
 //		private final EntityReference targetEntityReference;
 //
 //		private final FetchStrategy fetchStrategy = new FetchStrategy(
 //				FetchTiming.IMMEDIATE,
 //				FetchStyle.JOIN
 //		);
 //
 //		public CircularFetch(FetchOwner circularFetchOwner, FetchOwner associationOwner, AttributeDefinition attributeDefinition) {
 //			this.circularFetchOwner = circularFetchOwner;
 //			this.associationOwner = associationOwner;
 //			this.attributeDefinition = attributeDefinition;
 //			this.targetEntityReference = resolveEntityReference( associationOwner );
 //		}
 //
 //		@Override
 //		public EntityReference getTargetEntityReference() {
 //			return targetEntityReference;
 //		}
 //
 //		protected static EntityReference resolveEntityReference(FetchOwner owner) {
 //			if ( EntityReference.class.isInstance( owner ) ) {
 //				return (EntityReference) owner;
 //			}
 //			if ( CompositeFetch.class.isInstance( owner ) ) {
 //				return resolveEntityReference( ( (CompositeFetch) owner ).getOwner() );
 //			}
 //			// todo : what others?
 //
 //			throw new UnsupportedOperationException(
 //					"Unexpected FetchOwner type [" + owner + "] encountered trying to build circular fetch"
 //			);
 //
 //		}
 //
 //		@Override
 //		public FetchOwner getSource() {
 //			return circularFetchOwner;
 //		}
 //
 //		@Override
 //		public PropertyPath getPropertyPath() {
 //			return null;  //To change body of implemented methods use File | Settings | File Templates.
 //		}
 //
 //		@Override
 //		public Type getFetchedType() {
 //			return attributeDefinition.getType();
 //		}
 //
 //		@Override
 //		public FetchStrategy getFetchStrategy() {
 //			return fetchStrategy;
 //		}
 //
 //		@Override
 //		public boolean isNullable() {
 //			return attributeDefinition.isNullable();
 //		}
 //
 //		@Override
 //		public String getAdditionalJoinConditions() {
 //			return null;
 //		}
 //
 //		@Override
 //		public String[] toSqlSelectFragments(String alias) {
 //			return new String[0];
 //		}
 //
 //		@Override
 //		public Fetch makeCopy(CopyContext copyContext, FetchOwner fetchSourceCopy) {
 //			// todo : will need this implemented
 //			return null;
 //		}
 //
 //		@Override
 //		public LockMode getLockMode() {
 //			return targetEntityReference.getLockMode();
 //		}
 //
 //		@Override
 //		public EntityReference getEntityReference() {
 //			return targetEntityReference;
 //		}
 //
 //		@Override
 //		public EntityPersister getEntityPersister() {
 //			return targetEntityReference.getEntityPersister();
 //		}
 //
 //		@Override
 //		public IdentifierDescription getIdentifierDescription() {
 //			return targetEntityReference.getIdentifierDescription();
 //		}
 //
 //		@Override
 //		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 //			throw new IllegalStateException( "IdentifierDescription should never be injected from circular fetch side" );
 //		}
 //	}
 
 	@Override
 	public void foundAny(AnyMappingDefinition anyDefinition) {
 		// do nothing.
 	}
 
 	protected boolean handleCompositeAttribute(AttributeDefinition attributeDefinition) {
 		final CompositeFetch compositeFetch = currentSource().buildCompositeAttributeFetch( attributeDefinition );
 		pushToStack( (ExpandingFetchSource) compositeFetch );
 		return true;
 	}
 
 	protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
 		// todo : this seems to not be correct for one-to-one
 		final FetchStrategy fetchStrategy = determineFetchStrategy( attributeDefinition );
 		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
 			return false;
 		}
 
 		final ExpandingFetchSource currentSource = currentSource();
 		currentSource.validateFetchPlan( fetchStrategy, attributeDefinition );
 
 		final AssociationAttributeDefinition.AssociationNature nature = attributeDefinition.getAssociationNature();
 		if ( nature == AssociationAttributeDefinition.AssociationNature.ANY ) {
 			// for ANY mappings we need to build a Fetch:
 			//		1) fetch type is SELECT
 			//		2) (because the fetch cannot be a JOIN...) do not push it to the stack
 			currentSource.buildAnyAttributeFetch(
 					attributeDefinition,
 					fetchStrategy
 			);
 			return false;
 		}
 		else if ( nature == AssociationAttributeDefinition.AssociationNature.ENTITY ) {
 			EntityFetch fetch = currentSource.buildEntityAttributeFetch(
 					attributeDefinition,
 					fetchStrategy
 			);
 			if ( fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 				pushToStack( (ExpandingFetchSource) fetch );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else {
 			// Collection
 			CollectionAttributeFetch fetch = currentSource.buildCollectionAttributeFetch( attributeDefinition, fetchStrategy );
 			if ( fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 				pushToCollectionStack( fetch );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 	}
 
 	protected abstract FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition);
 
 	protected int currentDepth() {
 		return fetchSourceStack.size();
 	}
 
 	protected boolean isTooManyCollections() {
 		return false;
 	}
 
 	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory();
 	}
 
 	/**
 	 * Maintains stack information for the property paths we are processing for logging purposes.  Because of the
 	 * recursive calls it is often useful (while debugging) to be able to see the "property path" as part of the
 	 * logging output.
 	 */
 	public static class PropertyPathStack {
 		private ArrayDeque<PropertyPath> pathStack = new ArrayDeque<PropertyPath>();
 
 		public void push(PropertyPath path) {
 			pathStack.addFirst( path );
 			MDC.put( MDC_KEY, extractFullPath( path ) );
 		}
 
 		private String extractFullPath(PropertyPath path) {
 			return path == null ? "<no-path>" : path.getFullPath();
 		}
 
 		public void pop() {
 			pathStack.removeFirst();
 			PropertyPath newHead = pathStack.peekFirst();
 			MDC.put( MDC_KEY, extractFullPath( newHead ) );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingCollectionQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingCollectionQuerySpace.java
similarity index 85%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingCollectionQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingCollectionQuerySpace.java
index bfee72d58c..e3f6403df7 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingCollectionQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingCollectionQuerySpace.java
@@ -1,34 +1,32 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
-import org.hibernate.loader.plan2.spi.CollectionQuerySpace;
-import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.type.CompositeType;
+import org.hibernate.loader.plan.spi.CollectionQuerySpace;
 
 /**
  * @author Gail Badner
  */
 public interface ExpandingCollectionQuerySpace extends CollectionQuerySpace, ExpandingQuerySpace {
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingCompositeQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingCompositeQuerySpace.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingCompositeQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingCompositeQuerySpace.java
index 9bf2599610..0f3201407e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingCompositeQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingCompositeQuerySpace.java
@@ -1,32 +1,32 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
-import org.hibernate.loader.plan2.spi.CompositeQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeQuerySpace;
 
 /**
  * @author Gail Badner
  */
 public interface ExpandingCompositeQuerySpace extends CompositeQuerySpace, ExpandingQuerySpace {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityIdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingEntityIdentifierDescription.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityIdentifierDescription.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingEntityIdentifierDescription.java
index d727b4545b..e76bdf2735 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityIdentifierDescription.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingEntityIdentifierDescription.java
@@ -1,33 +1,33 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
-import org.hibernate.loader.plan2.spi.EntityIdentifierDescription;
+import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
 
 /**
  * @author Steve Ebersole
  */
 public interface ExpandingEntityIdentifierDescription extends EntityIdentifierDescription, ExpandingFetchSource {
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingEntityQuerySpace.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingEntityQuerySpace.java
index 665bc6a478..f3006f9678 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingEntityQuerySpace.java
@@ -1,33 +1,33 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
-import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan.spi.EntityQuerySpace;
 
 /**
  * @author Steve Ebersole
  */
 public interface ExpandingEntityQuerySpace extends EntityQuerySpace, ExpandingQuerySpace {
 	public ExpandingCompositeQuerySpace makeCompositeIdentifierQuerySpace();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingFetchSource.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingFetchSource.java
similarity index 84%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingFetchSource.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingFetchSource.java
index 79b851b852..3fb3648ada 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingFetchSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingFetchSource.java
@@ -1,72 +1,72 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import org.hibernate.engine.FetchStrategy;
-import org.hibernate.loader.plan2.spi.AnyAttributeFetch;
-import org.hibernate.loader.plan2.spi.BidirectionalEntityReference;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
-import org.hibernate.loader.plan2.spi.CompositeAttributeFetch;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.spi.AnyAttributeFetch;
+import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 
 /**
  * Describes the internal contract for things which can contain fetches.  Used to request building
  * the different types of fetches.
  *
  * @author Steve Ebersole
  */
 public interface ExpandingFetchSource extends FetchSource {
 	/**
 	 * Is the asserted plan valid from this owner to a fetch?
 	 *
 	 * @param fetchStrategy The type of fetch to validate
 	 * @param attributeDefinition The attribute to be fetched
 	 */
 	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition);
 
 	public EntityFetch buildEntityAttributeFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy);
 
 	public BidirectionalEntityReference buildBidirectionalEntityReference(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			EntityReference targetEntityReference);
 
 	public CompositeAttributeFetch buildCompositeAttributeFetch(
 			AttributeDefinition attributeDefinition);
 
 	public CollectionAttributeFetch buildCollectionAttributeFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy);
 
 	public AnyAttributeFetch buildAnyAttributeFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingQuerySpace.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingQuerySpace.java
index 0ebb5a7c0d..62f11d5235 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingQuerySpace.java
@@ -1,39 +1,39 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
-import org.hibernate.loader.plan2.spi.Join;
-import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.Join;
+import org.hibernate.loader.plan.spi.QuerySpace;
 
 /**
  * @author Steve Ebersole
  */
 public interface ExpandingQuerySpace extends QuerySpace {
 
 	public boolean canJoinsBeRequired();
 
 	public void addJoin(Join join);
 
 	public ExpandingQuerySpaces getExpandingQuerySpaces();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpaces.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingQuerySpaces.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpaces.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingQuerySpaces.java
index 9cafbfbdd0..f9eee0f9b3 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpaces.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ExpandingQuerySpaces.java
@@ -1,62 +1,62 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.build.internal.spaces.CompositePropertyMapping;
-import org.hibernate.loader.plan2.spi.QuerySpaces;
+import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
+import org.hibernate.loader.plan.spi.QuerySpaces;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Steve Ebersole
  */
 public interface ExpandingQuerySpaces extends QuerySpaces {
 	public String generateImplicitUid();
 
 	public ExpandingEntityQuerySpace makeRootEntityQuerySpace(
 			String uid,
 			EntityPersister entityPersister);
 
 	public ExpandingEntityQuerySpace makeEntityQuerySpace(
 			String uid,
 			EntityPersister entityPersister,
 			boolean canJoinsBeRequired);
 
 	public ExpandingCollectionQuerySpace makeRootCollectionQuerySpace(
 			String uid,
 			CollectionPersister collectionPersister);
 
 	public ExpandingCollectionQuerySpace makeCollectionQuerySpace(
 			String uid,
 			CollectionPersister collectionPersister,
 			boolean canJoinsBeRequired);
 
 	public ExpandingCompositeQuerySpace makeCompositeQuerySpace(
 			String uid,
 			CompositePropertyMapping compositePropertyMapping,
 			boolean canJoinsBeRequired);
 
 	public SessionFactoryImplementor getSessionFactory();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingAssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanBuildingAssociationVisitationStrategy.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingAssociationVisitationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanBuildingAssociationVisitationStrategy.java
index feb0fc20c7..925a6a1b29 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingAssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanBuildingAssociationVisitationStrategy.java
@@ -1,42 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
 
 /**
  * Specialized {@link org.hibernate.persister.walking.spi.AssociationVisitationStrategy} implementation for
- * building {@link org.hibernate.loader.plan2.spi.LoadPlan} instances.
+ * building {@link org.hibernate.loader.plan.spi.LoadPlan} instances.
  *
  * @author Steve Ebersole
  */
 public interface LoadPlanBuildingAssociationVisitationStrategy extends AssociationVisitationStrategy {
 	/**
 	 * After visitation is done, build the load plan.
 	 *
 	 * @return The load plan
 	 */
 	public LoadPlan buildLoadPlan();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanBuildingContext.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingContext.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanBuildingContext.java
index 1df4f8fcb4..0557e6dc65 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanBuildingContext.java
@@ -1,47 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AssociationKey;
 
 /**
  * Provides access to context needed in building a LoadPlan.
  *
  * @author Steve Ebersole
  */
 public interface LoadPlanBuildingContext {
 	/**
 	 * Access to the SessionFactory
 	 *
 	 * @return The SessionFactory
 	 */
 	public SessionFactoryImplementor getSessionFactory();
 
 	public ExpandingQuerySpaces getQuerySpaces();
 
 	public FetchSource registeredFetchSource(AssociationKey associationKey);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanTreePrinter.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanTreePrinter.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanTreePrinter.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanTreePrinter.java
index c1e2d3e406..d157151871 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanTreePrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/LoadPlanTreePrinter.java
@@ -1,121 +1,121 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.exec.spi.AliasResolutionContext;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.Return;
 
 /**
- * Prints a {@link org.hibernate.loader.plan2.spi.QuerySpaces} graph as a tree structure.
+ * Prints a {@link org.hibernate.loader.plan.spi.QuerySpaces} graph as a tree structure.
  * <p/>
  * Intended for use in debugging, logging, etc.
  * <p/>
  * Aggregates calls to the {@link QuerySpaceTreePrinter} and {@link ReturnGraphTreePrinter}
  *
  * @author Steve Ebersole
  */
 public class LoadPlanTreePrinter {
 	private static final Logger log = CoreLogging.logger( LoadPlanTreePrinter.class );
 
 	/**
 	 * Singleton access
 	 */
 	public static final LoadPlanTreePrinter INSTANCE = new LoadPlanTreePrinter();
 
 	private String toString(LoadPlan loadPlan) {
 		return toString( loadPlan, null );
 	}
 
 	private String toString(LoadPlan loadPlan, AliasResolutionContext aliasResolutionContext) {
 		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 		final PrintStream printStream = new PrintStream( byteArrayOutputStream );
 		final PrintWriter printWriter = new PrintWriter( printStream );
 
 		logTree( loadPlan, aliasResolutionContext, printWriter );
 
 		printWriter.flush();
 		printStream.flush();
 
 		return new String( byteArrayOutputStream.toByteArray() );
 	}
 
 	public void logTree(LoadPlan loadPlan, AliasResolutionContext aliasResolutionContext) {
 		if ( ! log.isDebugEnabled() ) {
 			return;
 		}
 
 		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 		final PrintStream printStream = new PrintStream( byteArrayOutputStream );
 		final PrintWriter printWriter = new PrintWriter( printStream );
 
 		logTree( loadPlan, aliasResolutionContext, printWriter );
 
 		printWriter.flush();
 		printStream.flush();
 		log.debug( new String( byteArrayOutputStream.toByteArray() ) );
 	}
 
 	private void logTree(
 			LoadPlan loadPlan,
 			AliasResolutionContext aliasResolutionContext,
 			PrintWriter printWriter) {
 		printWriter.println( "LoadPlan(" + extractDetails( loadPlan ) + ")" );
 		printWriter.println( TreePrinterHelper.INSTANCE.generateNodePrefix( 1 ) + "Returns" );
 		for ( Return rtn : loadPlan.getReturns() ) {
 			ReturnGraphTreePrinter.INSTANCE.write( rtn, 2, printWriter );
 			printWriter.flush();
 		}
 
 		QuerySpaceTreePrinter.INSTANCE.write( loadPlan.getQuerySpaces(), 1, aliasResolutionContext, printWriter );
 
 		printWriter.flush();
 	}
 
 	private String extractDetails(LoadPlan loadPlan) {
 		switch ( loadPlan.getDisposition() ) {
 			case MIXED: {
 				return "mixed";
 			}
 			case ENTITY_LOADER: {
 				return "entity=" + ( (EntityReturn) loadPlan.getReturns().get( 0 ) ).getEntityPersister().getEntityName();
 			}
 			case COLLECTION_INITIALIZER: {
 				return "collection=" + ( (CollectionReturn) loadPlan.getReturns().get( 0 ) ).getCollectionPersister().getRole();
 			}
 			default: {
 				return "???";
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/MetamodelDrivenLoadPlanBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/MetamodelDrivenLoadPlanBuilder.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/MetamodelDrivenLoadPlanBuilder.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/MetamodelDrivenLoadPlanBuilder.java
index 1e80f0badc..0229308605 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/MetamodelDrivenLoadPlanBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/MetamodelDrivenLoadPlanBuilder.java
@@ -1,71 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.MetamodelGraphWalker;
 
 /**
  * A metadata-driven builder of LoadPlans.  Coordinates between the {@link MetamodelGraphWalker} and a
  * {@link LoadPlanBuildingAssociationVisitationStrategy}.
  *
  * @author Steve Ebersole
  *
  * @see org.hibernate.persister.walking.spi.MetamodelGraphWalker
  */
 public class MetamodelDrivenLoadPlanBuilder {
 	/**
 	 * Coordinates building a LoadPlan that defines just a single root entity return (may have fetches).
 	 * <p/>
 	 * Typically this includes building load plans for entity loading or cascade loading.
 	 *
 	 * @param strategy The strategy defining the load plan shaping
 	 * @param persister The persister for the entity forming the root of the load plan.
 	 *
 	 * @return The built load plan.
 	 */
 	public static LoadPlan buildRootEntityLoadPlan(
 			LoadPlanBuildingAssociationVisitationStrategy strategy,
 			EntityPersister persister) {
 		MetamodelGraphWalker.visitEntity( strategy, persister );
 		return strategy.buildLoadPlan();
 	}
 
 	/**
 	 * Coordinates building a LoadPlan that defines just a single root collection return (may have fetches).
 	 *
 	 * @param strategy The strategy defining the load plan shaping
 	 * @param persister The persister for the collection forming the root of the load plan.
 	 *
 	 * @return The built load plan.
 	 */
 	public static LoadPlan buildRootCollectionLoadPlan(
 			LoadPlanBuildingAssociationVisitationStrategy strategy,
 			CollectionPersister persister) {
 		MetamodelGraphWalker.visitCollection( strategy, persister );
 		return strategy.buildLoadPlan();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/QuerySpaceTreePrinter.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/QuerySpaceTreePrinter.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/QuerySpaceTreePrinter.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/QuerySpaceTreePrinter.java
index 5d73fc8653..e68bd5e6db 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/QuerySpaceTreePrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/QuerySpaceTreePrinter.java
@@ -1,228 +1,228 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.EntityAliases;
-import org.hibernate.loader.plan2.exec.spi.AliasResolutionContext;
-import org.hibernate.loader.plan2.exec.spi.CollectionReferenceAliases;
-import org.hibernate.loader.plan2.exec.spi.EntityReferenceAliases;
-import org.hibernate.loader.plan2.spi.CollectionQuerySpace;
-import org.hibernate.loader.plan2.spi.CompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.EntityQuerySpace;
-import org.hibernate.loader.plan2.spi.Join;
-import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
-import org.hibernate.loader.plan2.spi.QuerySpace;
-import org.hibernate.loader.plan2.spi.QuerySpaces;
+import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
+import org.hibernate.loader.plan.spi.CollectionQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeQuerySpace;
+import org.hibernate.loader.plan.spi.EntityQuerySpace;
+import org.hibernate.loader.plan.spi.Join;
+import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.QuerySpaces;
 
 /**
  * Prints a {@link QuerySpaces} graph as a tree structure.
  * <p/>
  * Intended for use in debugging, logging, etc.
  *
  * @author Steve Ebersole
  */
 public class QuerySpaceTreePrinter {
 	/**
 	 * Singleton access
 	 */
 	public static final QuerySpaceTreePrinter INSTANCE = new QuerySpaceTreePrinter();
 
 	public String asString(QuerySpaces spaces, AliasResolutionContext aliasResolutionContext) {
 		return asString( spaces, 0, aliasResolutionContext );
 	}
 
 	public String asString(QuerySpaces spaces, int depth, AliasResolutionContext aliasResolutionContext) {
 		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 		final PrintStream printStream = new PrintStream( byteArrayOutputStream );
 		write( spaces, depth, aliasResolutionContext, printStream );
 		printStream.flush();
 		return new String( byteArrayOutputStream.toByteArray() );
 	}
 
 	public void write(
 			QuerySpaces spaces,
 			int depth,
 			AliasResolutionContext aliasResolutionContext,
 			PrintStream printStream) {
 		write( spaces, depth, aliasResolutionContext, new PrintWriter( printStream ) );
 	}
 
 	public void write(
 			QuerySpaces spaces,
 			int depth,
 			AliasResolutionContext aliasResolutionContext,
 			PrintWriter printWriter) {
 		if ( spaces == null ) {
 			printWriter.println( "QuerySpaces is null!" );
 			return;
 		}
 
 		printWriter.println( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "QuerySpaces" );
 
 		for ( QuerySpace querySpace : spaces.getRootQuerySpaces() ) {
 			writeQuerySpace( querySpace, depth + 1, aliasResolutionContext, printWriter );
 		}
 
 		printWriter.flush();
 	}
 
 	private void writeQuerySpace(
 			QuerySpace querySpace,
 			int depth,
 			AliasResolutionContext aliasResolutionContext,
 			PrintWriter printWriter) {
 		generateDetailLines( querySpace, depth, aliasResolutionContext, printWriter );
 		writeJoins( querySpace.getJoins(), depth + 1, aliasResolutionContext, printWriter );
 	}
 
 	final int detailDepthOffset = 1;
 
 	private void generateDetailLines(
 			QuerySpace querySpace,
 			int depth,
 			AliasResolutionContext aliasResolutionContext,
 			PrintWriter printWriter) {
 		printWriter.println(
 				TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + extractDetails( querySpace )
 		);
 
 		if ( aliasResolutionContext == null ) {
 			return;
 		}
 
 		printWriter.println(
 				TreePrinterHelper.INSTANCE.generateNodePrefix( depth + detailDepthOffset )
 						+ "SQL table alias mapping - " + aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(
 						querySpace.getUid()
 				)
 		);
 
 		final EntityReferenceAliases entityAliases = aliasResolutionContext.resolveEntityReferenceAliases( querySpace.getUid() );
 		final CollectionReferenceAliases collectionReferenceAliases = aliasResolutionContext.resolveCollectionReferenceAliases( querySpace.getUid() );
 
 		if ( entityAliases != null ) {
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth + detailDepthOffset )
 							+ "alias suffix - " + entityAliases.getColumnAliases().getSuffix()
 			);
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth + detailDepthOffset )
 							+ "suffixed key columns - {"
 							+ StringHelper.join( ", ", entityAliases.getColumnAliases().getSuffixedKeyAliases() )
 							+ "}"
 			);
 		}
 
 		if ( collectionReferenceAliases != null ) {
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth + detailDepthOffset )
 							+ "alias suffix - " + collectionReferenceAliases.getCollectionColumnAliases().getSuffix()
 			);
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth + detailDepthOffset )
 							+ "suffixed key columns - {"
 							+ StringHelper.join( ", ", collectionReferenceAliases.getCollectionColumnAliases().getSuffixedKeyAliases() )
 							+ "}"
 			);
 			final EntityAliases elementAliases = collectionReferenceAliases.getEntityElementColumnAliases();
 			if ( elementAliases != null ) {
 				printWriter.println(
 						TreePrinterHelper.INSTANCE.generateNodePrefix( depth + detailDepthOffset )
 								+ "entity-element alias suffix - " + elementAliases.getSuffix()
 				);
 				printWriter.println(
 						TreePrinterHelper.INSTANCE.generateNodePrefix( depth + detailDepthOffset )
 								+ elementAliases.getSuffix()
 								+ "entity-element suffixed key columns - "
 								+ StringHelper.join( ", ", elementAliases.getSuffixedKeyAliases() )
 				);
 			}
 		}
 	}
 
 	private void writeJoins(
 			Iterable<Join> joins,
 			int depth,
 			AliasResolutionContext aliasResolutionContext,
 			PrintWriter printWriter) {
 		for ( Join join : joins ) {
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + extractDetails( join )
 			);
 			writeQuerySpace( join.getRightHandSide(), depth+1, aliasResolutionContext, printWriter );
 		}
 	}
 
 	public String extractDetails(QuerySpace space) {
 		if ( EntityQuerySpace.class.isInstance( space ) ) {
 			final EntityQuerySpace entityQuerySpace = (EntityQuerySpace) space;
 			return String.format(
 					"%s(uid=%s, entity=%s)",
 					entityQuerySpace.getClass().getSimpleName(),
 					entityQuerySpace.getUid(),
 					entityQuerySpace.getEntityPersister().getEntityName()
 			);
 		}
 		else if ( CompositeQuerySpace.class.isInstance( space ) ) {
 			final CompositeQuerySpace compositeQuerySpace = (CompositeQuerySpace) space;
 			return String.format(
 					"%s(uid=%s)",
 					compositeQuerySpace.getClass().getSimpleName(),
 					compositeQuerySpace.getUid()
 			);
 		}
 		else if ( CollectionQuerySpace.class.isInstance( space ) ) {
 			final CollectionQuerySpace collectionQuerySpace = (CollectionQuerySpace) space;
 			return String.format(
 					"%s(uid=%s, collection=%s)",
 					collectionQuerySpace.getClass().getSimpleName(),
 					collectionQuerySpace.getUid(),
 					collectionQuerySpace.getCollectionPersister().getRole()
 			);
 		}
 		return space.toString();
 	}
 
 	private String extractDetails(Join join) {
 		return String.format(
 				"JOIN (%s) : %s -> %s",
 				determineJoinType( join ),
 				join.getLeftHandSide().getUid(),
 				join.getRightHandSide().getUid()
 		);
 	}
 
 	private String determineJoinType(Join join) {
 		if ( JoinDefinedByMetadata.class.isInstance( join ) ) {
 			return "JoinDefinedByMetadata(" + ( (JoinDefinedByMetadata) join ).getJoinedPropertyName() + ")";
 		}
 
 		return join.getClass().getSimpleName();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ReturnGraphTreePrinter.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ReturnGraphTreePrinter.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ReturnGraphTreePrinter.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ReturnGraphTreePrinter.java
index 9f76120b66..23042dcffb 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ReturnGraphTreePrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ReturnGraphTreePrinter.java
@@ -1,235 +1,235 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 
-import org.hibernate.loader.plan2.spi.BidirectionalEntityReference;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
-import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
-import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
-import org.hibernate.loader.plan2.spi.CollectionReference;
-import org.hibernate.loader.plan2.spi.CompositeFetch;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
-import org.hibernate.loader.plan2.spi.Return;
-import org.hibernate.loader.plan2.spi.ScalarReturn;
+import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.FetchSource;
+import org.hibernate.loader.plan.spi.Return;
+import org.hibernate.loader.plan.spi.ScalarReturn;
 
 /**
  * Prints a {@link Return} graph as a tree structure.
  * <p/>
  * Intended for use in debugging, logging, etc.
  *
  * @author Steve Ebersole
  */
 public class ReturnGraphTreePrinter {
 	/**
 	 * Singleton access
 	 */
 	public static final ReturnGraphTreePrinter INSTANCE = new ReturnGraphTreePrinter();
 
 	private ReturnGraphTreePrinter() {
 	}
 
 	public String asString(Return rootReturn) {
 		return asString( rootReturn, 0 );
 	}
 
 	public String asString(Return rootReturn, int depth) {
 		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 		final PrintStream ps = new PrintStream( byteArrayOutputStream );
 		write( rootReturn,  depth, ps );
 		ps.flush();
 		return new String( byteArrayOutputStream.toByteArray() );
 
 	}
 
 	public void write(Return rootReturn, PrintStream printStream) {
 		write( rootReturn, new PrintWriter( printStream ) );
 	}
 
 	public void write(Return rootReturn, int depth, PrintStream printStream) {
 		write( rootReturn, depth, new PrintWriter( printStream ) );
 	}
 
 	// todo : see ASTPrinter and try to apply its awesome tree structuring here.
 	//		I mean the stuff it does with '|' and '\\-' and '+-' etc as
 	//		prefixes for the tree nodes actual text to visually render the tree
 
 	public void write(Return rootReturn, PrintWriter printWriter) {
 		write( rootReturn, 0, printWriter );
 	}
 
 	public void write(Return rootReturn, int depth, PrintWriter printWriter) {
 		if ( rootReturn == null ) {
 			printWriter.println( "Return is null!" );
 			return;
 		}
 
 		printWriter.write( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) );
 
 
 		if ( ScalarReturn.class.isInstance( rootReturn ) ) {
 			printWriter.println( extractDetails( (ScalarReturn) rootReturn ) );
 		}
 		else if ( EntityReturn.class.isInstance( rootReturn ) ) {
 			final EntityReturn entityReturn = (EntityReturn) rootReturn;
 			printWriter.println( extractDetails( entityReturn ) );
 			writeEntityReferenceFetches( entityReturn, depth+1, printWriter );
 		}
 		else if ( CollectionReference.class.isInstance( rootReturn ) ) {
 			final CollectionReference collectionReference = (CollectionReference) rootReturn;
 			printWriter.println( extractDetails( collectionReference ) );
 			writeCollectionReferenceFetches( collectionReference, depth+1, printWriter );
 		}
 
 		printWriter.flush();
 	}
 
 	private String extractDetails(ScalarReturn rootReturn) {
 		return String.format(
 				"%s(name=%s, type=%s)",
 				rootReturn.getClass().getSimpleName(),
 				rootReturn.getName(),
 				rootReturn.getType().getName()
 		);
 	}
 
 	private String extractDetails(EntityReference entityReference) {
 		return String.format(
 				"%s(entity=%s, querySpaceUid=%s, path=%s)",
 				entityReference.getClass().getSimpleName(),
 				entityReference.getEntityPersister().getEntityName(),
 				entityReference.getQuerySpaceUid(),
 				entityReference.getPropertyPath().getFullPath()
 		);
 	}
 
 	private String extractDetails(CollectionReference collectionReference) {
 		// todo : include some form of parameterized type signature?  i.e., List<String>, Set<Person>, etc
 		return String.format(
 				"%s(collection=%s, querySpaceUid=%s, path=%s)",
 				collectionReference.getClass().getSimpleName(),
 				collectionReference.getCollectionPersister().getRole(),
 				collectionReference.getQuerySpaceUid(),
 				collectionReference.getPropertyPath().getFullPath()
 		);
 	}
 
 	private String extractDetails(CompositeFetch compositeFetch) {
 		return String.format(
 				"%s(composite=%s, querySpaceUid=%s, path=%s)",
 				compositeFetch.getClass().getSimpleName(),
 				compositeFetch.getFetchedType().getReturnedClass().getName(),
 				compositeFetch.getQuerySpaceUid(),
 				compositeFetch.getPropertyPath().getFullPath()
 		);
 	}
 
 	private void writeEntityReferenceFetches(EntityReference entityReference, int depth, PrintWriter printWriter) {
 		if ( BidirectionalEntityReference.class.isInstance( entityReference ) ) {
 			return;
 		}
 		if ( entityReference.getIdentifierDescription().hasFetches() ) {
 			printWriter.println( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "(entity id) " );
 			writeFetches( ( (FetchSource) entityReference.getIdentifierDescription() ).getFetches(), depth+1, printWriter );
 		}
 
 		writeFetches( entityReference.getFetches(), depth, printWriter );
 	}
 
 	private void writeFetches(Fetch[] fetches, int depth, PrintWriter printWriter) {
 		for ( Fetch fetch : fetches ) {
 			writeFetch( fetch, depth, printWriter );
 		}
 	}
 
 	private void writeFetch(Fetch fetch, int depth, PrintWriter printWriter) {
 		printWriter.print( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) );
 
 		if ( EntityFetch.class.isInstance( fetch ) ) {
 			final EntityFetch entityFetch = (EntityFetch) fetch;
 			printWriter.println( extractDetails( entityFetch ) );
 			writeEntityReferenceFetches( entityFetch, depth+1, printWriter );
 		}
 		else if ( CompositeFetch.class.isInstance( fetch ) ) {
 			final CompositeFetch compositeFetch = (CompositeFetch) fetch;
 			printWriter.println( extractDetails( compositeFetch ) );
 			writeCompositeFetchFetches( compositeFetch, depth+1, printWriter );
 		}
 		else if ( CollectionAttributeFetch.class.isInstance( fetch ) ) {
 			final CollectionAttributeFetch collectionFetch = (CollectionAttributeFetch) fetch;
 			printWriter.println( extractDetails( collectionFetch ) );
 			writeCollectionReferenceFetches( collectionFetch, depth+1, printWriter );
 		}
 	}
 
 	private void writeCompositeFetchFetches(CompositeFetch compositeFetch, int depth, PrintWriter printWriter) {
 		writeFetches( compositeFetch.getFetches(), depth, printWriter );
 	}
 
 	private void writeCollectionReferenceFetches(
 			CollectionReference collectionReference,
 			int depth,
 			PrintWriter printWriter) {
 		{
 			final CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
 			if ( indexGraph != null ) {
 				printWriter.print( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "(collection index) " );
 
 				if ( EntityReference.class.isInstance( indexGraph ) ) {
 					final EntityReference indexGraphAsEntityReference = (EntityReference) indexGraph;
 					printWriter.println( extractDetails( indexGraphAsEntityReference ) );
 					writeEntityReferenceFetches( indexGraphAsEntityReference, depth+1, printWriter );
 				}
 				else if ( CompositeFetch.class.isInstance( indexGraph ) ) {
 					final CompositeFetch indexGraphAsCompositeFetch = (CompositeFetch) indexGraph;
 					printWriter.println( extractDetails( indexGraphAsCompositeFetch ) );
 					writeCompositeFetchFetches( indexGraphAsCompositeFetch, depth+1, printWriter );
 				}
 			}
 		}
 
 		final CollectionFetchableElement elementGraph = collectionReference.getElementGraph();
 		if ( elementGraph != null ) {
 			printWriter.print( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "(collection element) " );
 
 			if ( EntityReference.class.isInstance( elementGraph ) ) {
 				final EntityReference elementGraphAsEntityReference = (EntityReference) elementGraph;
 				printWriter.println( extractDetails( elementGraphAsEntityReference ) );
 				writeEntityReferenceFetches( elementGraphAsEntityReference, depth+1, printWriter );
 			}
 			else if ( CompositeFetch.class.isInstance( elementGraph ) ) {
 				final CompositeFetch elementGraphAsCompositeFetch = (CompositeFetch) elementGraph;
 				printWriter.println( extractDetails( elementGraphAsCompositeFetch ) );
 				writeCompositeFetchFetches( elementGraphAsCompositeFetch, depth+1, printWriter );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/TreePrinterHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/TreePrinterHelper.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/TreePrinterHelper.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/TreePrinterHelper.java
index 52d07eee94..edfb69419d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/TreePrinterHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/TreePrinterHelper.java
@@ -1,42 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Steve Ebersole
  */
 public class TreePrinterHelper {
 	public static final int INDENTATION = 3;
 
 	/**
 	 * Singleton access
 	 */
 	public static final TreePrinterHelper INSTANCE = new TreePrinterHelper();
 
 	public String generateNodePrefix(int depth) {
 		return StringHelper.repeat( ' ', depth * INDENTATION ) + " - ";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/package-info.java
similarity index 59%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/package-info.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/package-info.java
index dfb031e0a6..1e941e1416 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/package-info.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/package-info.java
@@ -1,4 +1,4 @@
 /**
  * Defines the SPI for building a metamodel-driven LoadPlan
  */
-package org.hibernate.loader.plan2.build.spi;
+package org.hibernate.loader.plan.build.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/AbstractLoadPlanBasedLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AbstractLoadPlanBasedLoader.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/AbstractLoadPlanBasedLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AbstractLoadPlanBasedLoader.java
index 3b5f924650..53a5881ee9 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/AbstractLoadPlanBasedLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AbstractLoadPlanBasedLoader.java
@@ -1,527 +1,527 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Middleware LLC or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.internal;
+package org.hibernate.loader.plan.exec.internal;
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.ScrollMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * todo How much of AbstractLoadPlanBasedEntityLoader is actually needed?
 
  * @author Gail Badner
  */
 public abstract class AbstractLoadPlanBasedLoader {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( AbstractLoadPlanBasedLoader.class );
 
 	private final SessionFactoryImplementor factory;
 
 	private ColumnNameCache columnNameCache;
 
 	public AbstractLoadPlanBasedLoader(
 			SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected abstract LoadQueryDetails getStaticLoadQuery();
 
 	protected abstract int[] getNamedParameterLocs(String name);
 
 	protected abstract void autoDiscoverTypes(ResultSet rs);
 
 	protected List executeLoad(
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			LoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException {
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 		return executeLoad(
 				session,
 				queryParameters,
 				loadQueryDetails,
 				returnProxies,
 				forcedResultTransformer,
 				afterLoadActions
 		);
 	}
 
 	protected List executeLoad(
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			LoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
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
 		try {
 			List results = null;
 			final String sql = loadQueryDetails.getSqlStatement();
 			SqlStatementWrapper wrapper = null;
 			try {
 				wrapper = executeQueryStatement( sql, queryParameters, false, afterLoadActions, session );
 				results = loadQueryDetails.getResultSetProcessor().extractResults(
 						wrapper.getResultSet(),
 						session,
 						queryParameters,
 						new NamedParameterContext() {
 							@Override
 							public int[] getNamedParameterLocations(String name) {
 								return AbstractLoadPlanBasedLoader.this.getNamedParameterLocs( name );
 							}
 						},
 						returnProxies,
 						queryParameters.isReadOnly(),
 						forcedResultTransformer,
 						afterLoadActions
 				);
 			}
 			finally {
 				if ( wrapper != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release(
 							wrapper.getResultSet(),
 							wrapper.getStatement()
 					);
 				}
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 			return results;
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getStaticLoadQuery().getSqlStatement(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
 				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.getProcessedSql();
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper( st, getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session ) );
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link org.hibernate.dialect.pagination.NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
 	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
 	}
 
 	private String preprocessSQL(
 			String sql,
 			QueryParameters queryParameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		return getFactory().getSettings().isCommentsEnabled()
 				? prependComment( sql, queryParameters )
 				: sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		final String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return "/* " + comment + " */ " + sql;
 		}
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 			final String sql,
 			final QueryParameters queryParameters,
 			final LimitHandler limitHandler,
 			final boolean scroll,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		final boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		final boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		final boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		final boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator()
 				.getStatementPreparer().prepareQueryStatement( sql, callable, scrollMode );
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
 
 			limitHandler.setMaxRows( st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			final LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( log.isDebugEnabled() ) {
 							log.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( log.isTraceEnabled() ) {
 				log.tracev( "Bound [{0}] parameters total", col );
 			}
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw he;
 		}
 
 		return st;
 	}
 
 	protected ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
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
 			final Iterator itr = namedParams.entrySet().iterator();
 			final boolean debugEnabled = log.isDebugEnabled();
 			int result = 0;
 			while ( itr.hasNext() ) {
 				final Map.Entry e = (Map.Entry) itr.next();
 				final String name = (String) e.getKey();
 				final TypedValue typedval = (TypedValue) e.getValue();
 				final int[] locs = getNamedParameterLocs( name );
 				for ( int loc : locs ) {
 					if ( debugEnabled ) {
 						log.debugf(
 								"bindNamedParameters() %s -> %s [%s]",
 								typedval.getValue(),
 								name,
 								loc + startIndex
 						);
 					}
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), loc + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 			final PreparedStatement st,
 			final RowSelection selection,
 			final LimitHandler limitHandler,
 			final boolean autodiscovertypes,
 			final SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		try {
 			ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	protected void advance(final ResultSet rs, final RowSelection selection) throws SQLException {
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) {
 					rs.next();
 				}
 			}
 		}
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 				if ( log.isDebugEnabled() ) {
 					log.debugf( "Wrapping result set [%s]", rs );
 				}
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				log.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			log.trace( "Building columnName->columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Wrapper class for {@link java.sql.Statement} and associated {@link java.sql.ResultSet}.
 	 */
 	protected static class SqlStatementWrapper {
 		private final Statement statement;
 		private final ResultSet resultSet;
 
 		private SqlStatementWrapper(Statement statement, ResultSet resultSet) {
 			this.resultSet = resultSet;
 			this.statement = statement;
 		}
 
 		public ResultSet getResultSet() {
 			return resultSet;
 		}
 
 		public Statement getStatement() {
 			return statement;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/AliasResolutionContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AliasResolutionContextImpl.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/AliasResolutionContextImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AliasResolutionContextImpl.java
index fc2d2fd881..848b127b6a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/AliasResolutionContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/AliasResolutionContextImpl.java
@@ -1,333 +1,333 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.internal;
+package org.hibernate.loader.plan.exec.internal;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.DefaultEntityAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.GeneratedCollectionAliases;
-import org.hibernate.loader.plan2.build.spi.QuerySpaceTreePrinter;
-import org.hibernate.loader.plan2.build.spi.TreePrinterHelper;
-import org.hibernate.loader.plan2.exec.spi.AliasResolutionContext;
-import org.hibernate.loader.plan2.exec.spi.CollectionReferenceAliases;
-import org.hibernate.loader.plan2.exec.spi.EntityReferenceAliases;
-import org.hibernate.loader.plan2.spi.Join;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan.build.spi.QuerySpaceTreePrinter;
+import org.hibernate.loader.plan.build.spi.TreePrinterHelper;
+import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
+import org.hibernate.loader.plan.spi.Join;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.QuerySpace;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.type.EntityType;
 
 /**
  * Provides aliases that are used by load queries and ResultSet processors.
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class AliasResolutionContextImpl implements AliasResolutionContext {
 	private static final Logger log = CoreLogging.logger( AliasResolutionContextImpl.class );
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	// Used to generate unique selection value aliases (column/formula renames)
 	private int currentAliasSuffix;
 	// Used to generate unique table aliases
 	private int currentTableAliasSuffix;
 
 	private Map<String,EntityReferenceAliases> entityReferenceAliasesMap;
 	private Map<String,CollectionReferenceAliases> collectionReferenceAliasesMap;
 	private Map<String,String> querySpaceUidToSqlTableAliasMap;
 
 	private Map<String,String> compositeQuerySpaceUidToSqlTableAliasMap;
 
 	/**
 	 * Constructs a AliasResolutionContextImpl without any source aliases.  This form is used in
 	 * non-query (HQL, criteria, etc) contexts.
 	 *
 	 * @param sessionFactory The session factory
 	 */
 	public AliasResolutionContextImpl(SessionFactoryImplementor sessionFactory) {
 		this( sessionFactory, 0 );
 	}
 
 	/**
 	 * Constructs a AliasResolutionContextImpl without any source aliases.  This form is used in
 	 * non-query (HQL, criteria, etc) contexts.
 	 * <p/>
 	 * See the notes on
-	 * {@link org.hibernate.loader.plan2.exec.spi.AliasResolutionContext#getSourceAlias} for discussion of
+	 * {@link org.hibernate.loader.plan.exec.spi.AliasResolutionContext#getSourceAlias} for discussion of
 	 * "source aliases".  They are not implemented here yet.
 	 *
 	 * @param sessionFactory The session factory
 	 * @param suffixSeed The seed value to use for generating the suffix used when generating SQL aliases.
 	 */
 	public AliasResolutionContextImpl(SessionFactoryImplementor sessionFactory, int suffixSeed) {
 		this.sessionFactory = sessionFactory;
 		this.currentAliasSuffix = suffixSeed;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	public EntityReferenceAliases generateEntityReferenceAliases(String uid, EntityPersister entityPersister) {
 		final EntityReferenceAliasesImpl entityReferenceAliases = new EntityReferenceAliasesImpl(
 				createTableAlias( entityPersister ),
 				createEntityAliases( entityPersister )
 		);
 		registerQuerySpaceAliases( uid, entityReferenceAliases );
 		return entityReferenceAliases;
 	}
 
 	private String createTableAlias(EntityPersister entityPersister) {
 		return createTableAlias( StringHelper.unqualifyEntityName( entityPersister.getEntityName() ) );
 	}
 
 	private String createTableAlias(String name) {
 		return StringHelper.generateAlias( name, currentTableAliasSuffix++ );
 	}
 
 	private EntityAliases createEntityAliases(EntityPersister entityPersister) {
 		return new DefaultEntityAliases( (Loadable) entityPersister, createSuffix() );
 	}
 
 	private String createSuffix() {
 		return Integer.toString( currentAliasSuffix++ ) + '_';
 	}
 
 	public CollectionReferenceAliases generateCollectionReferenceAliases(String uid, CollectionPersister persister) {
 		final String manyToManyTableAlias;
 		final String tableAlias;
 		if ( persister.isManyToMany() ) {
 			manyToManyTableAlias = createTableAlias( persister.getRole() );
 			tableAlias = createTableAlias( persister.getElementDefinition().toEntityDefinition().getEntityPersister() );
 		}
 		else {
 			manyToManyTableAlias = null;
 			tableAlias = createTableAlias( persister.getRole() );
 		}
 
 		final CollectionReferenceAliasesImpl aliases = new CollectionReferenceAliasesImpl(
 				tableAlias,
 				manyToManyTableAlias,
 				createCollectionAliases( persister ),
 				createCollectionElementAliases( persister )
 		);
 
 		registerQuerySpaceAliases( uid, aliases );
 		return aliases;
 	}
 
 	private CollectionAliases createCollectionAliases(CollectionPersister collectionPersister) {
 		return new GeneratedCollectionAliases( collectionPersister, createSuffix() );
 	}
 
 	private EntityAliases createCollectionElementAliases(CollectionPersister collectionPersister) {
 		if ( !collectionPersister.getElementType().isEntityType() ) {
 			return null;
 		}
 		else {
 			final EntityType entityElementType = (EntityType) collectionPersister.getElementType();
 			return createEntityAliases( (EntityPersister) entityElementType.getAssociatedJoinable( sessionFactory() ) );
 		}
 	}
 
 	public void registerQuerySpaceAliases(String querySpaceUid, EntityReferenceAliases entityReferenceAliases) {
 		if ( entityReferenceAliasesMap == null ) {
 			entityReferenceAliasesMap = new HashMap<String, EntityReferenceAliases>();
 		}
 		entityReferenceAliasesMap.put( querySpaceUid, entityReferenceAliases );
 		registerSqlTableAliasMapping( querySpaceUid, entityReferenceAliases.getTableAlias() );
 	}
 
 	public void registerSqlTableAliasMapping(String querySpaceUid, String sqlTableAlias) {
 		if ( querySpaceUidToSqlTableAliasMap == null ) {
 			querySpaceUidToSqlTableAliasMap = new HashMap<String, String>();
 		}
 		String old = querySpaceUidToSqlTableAliasMap.put( querySpaceUid, sqlTableAlias );
 		if ( old != null ) {
 			if ( old.equals( sqlTableAlias ) ) {
 				// silently ignore...
 			}
 			else {
 				throw new IllegalStateException(
 						String.format(
 								"Attempt to register multiple SQL table aliases [%s, %s, etc] against query space uid [%s]",
 								old,
 								sqlTableAlias,
 								querySpaceUid
 						)
 				);
 			}
 		}
 	}
 
 	@Override
 	public String resolveSqlTableAliasFromQuerySpaceUid(String querySpaceUid) {
 		String alias = null;
 		if ( querySpaceUidToSqlTableAliasMap != null ) {
 			alias = querySpaceUidToSqlTableAliasMap.get( querySpaceUid );
 		}
 
 		if ( alias == null ) {
 			if ( compositeQuerySpaceUidToSqlTableAliasMap != null ) {
 				alias = compositeQuerySpaceUidToSqlTableAliasMap.get( querySpaceUid );
 			}
 		}
 
 		return alias;
 	}
 
 	@Override
 	public EntityReferenceAliases resolveEntityReferenceAliases(String querySpaceUid) {
 		return entityReferenceAliasesMap == null ? null : entityReferenceAliasesMap.get( querySpaceUid );
 	}
 
 	public void registerQuerySpaceAliases(String querySpaceUid, CollectionReferenceAliases collectionReferenceAliases) {
 		if ( collectionReferenceAliasesMap == null ) {
 			collectionReferenceAliasesMap = new HashMap<String, CollectionReferenceAliases>();
 		}
 		collectionReferenceAliasesMap.put( querySpaceUid, collectionReferenceAliases );
 		registerSqlTableAliasMapping( querySpaceUid, collectionReferenceAliases.getCollectionTableAlias() );
 	}
 
 	@Override
 	public CollectionReferenceAliases resolveCollectionReferenceAliases(String querySpaceUid) {
 		return collectionReferenceAliasesMap == null ? null : collectionReferenceAliasesMap.get( querySpaceUid );
 	}
 
 	public void registerCompositeQuerySpaceUidResolution(String rightHandSideUid, String leftHandSideTableAlias) {
 		if ( compositeQuerySpaceUidToSqlTableAliasMap == null ) {
 			compositeQuerySpaceUidToSqlTableAliasMap = new HashMap<String, String>();
 		}
 		compositeQuerySpaceUidToSqlTableAliasMap.put( rightHandSideUid, leftHandSideTableAlias );
 	}
 
 	/**
 	 * USes its defined logger to generate a resolution report.
 	 *
 	 * @param loadPlan The loadplan that was processed.
 	 */
 	public void dumpResolutions(LoadPlan loadPlan) {
 		if ( log.isDebugEnabled() ) {
 			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 			final PrintStream printStream = new PrintStream( byteArrayOutputStream );
 			final PrintWriter printWriter = new PrintWriter( printStream );
 
 			printWriter.println( "LoadPlan QuerySpace resolutions" );
 
 			for ( QuerySpace querySpace : loadPlan.getQuerySpaces().getRootQuerySpaces() ) {
 				dumpQuerySpace( querySpace, 1, printWriter );
 			}
 
 			printWriter.flush();
 			printStream.flush();
 
 			log.debug( new String( byteArrayOutputStream.toByteArray() ) );
 		}
 	}
 
 	private void dumpQuerySpace(QuerySpace querySpace, int depth, PrintWriter printWriter) {
 		generateDetailLines( querySpace, depth, printWriter );
 		dumpJoins( querySpace.getJoins(), depth + 1, printWriter );
 	}
 
 	private void generateDetailLines(QuerySpace querySpace, int depth, PrintWriter printWriter) {
 		printWriter.println(
 				TreePrinterHelper.INSTANCE.generateNodePrefix( depth )
 						+ querySpace.getUid() + " -> " + extractDetails( querySpace )
 		);
 		printWriter.println(
 				TreePrinterHelper.INSTANCE.generateNodePrefix( depth+3 )
 						+ "SQL table alias mapping - " + resolveSqlTableAliasFromQuerySpaceUid( querySpace.getUid() )
 		);
 
 		final EntityReferenceAliases entityAliases = resolveEntityReferenceAliases( querySpace.getUid() );
 		final CollectionReferenceAliases collectionReferenceAliases = resolveCollectionReferenceAliases( querySpace.getUid() );
 
 		if ( entityAliases != null ) {
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth+3 )
 							+ "alias suffix - " + entityAliases.getColumnAliases().getSuffix()
 			);
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth+3 )
 							+ "suffixed key columns - "
 							+ StringHelper.join( ", ", entityAliases.getColumnAliases().getSuffixedKeyAliases() )
 			);
 		}
 
 		if ( collectionReferenceAliases != null ) {
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth+3 )
 							+ "alias suffix - " + collectionReferenceAliases.getCollectionColumnAliases().getSuffix()
 			);
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth+3 )
 							+ "suffixed key columns - "
 							+ StringHelper.join( ", ", collectionReferenceAliases.getCollectionColumnAliases().getSuffixedKeyAliases() )
 			);
 			final EntityAliases elementAliases = collectionReferenceAliases.getEntityElementColumnAliases();
 			if ( elementAliases != null ) {
 				printWriter.println(
 						TreePrinterHelper.INSTANCE.generateNodePrefix( depth+3 )
 								+ "entity-element alias suffix - " + elementAliases.getSuffix()
 				);
 				printWriter.println(
 						TreePrinterHelper.INSTANCE.generateNodePrefix( depth+3 )
 								+ elementAliases.getSuffix()
 								+ "entity-element suffixed key columns - "
 								+ StringHelper.join( ", ", elementAliases.getSuffixedKeyAliases() )
 				);
 			}
 		}
 	}
 
 	private String extractDetails(QuerySpace querySpace) {
 		return QuerySpaceTreePrinter.INSTANCE.extractDetails( querySpace );
 	}
 
 	private void dumpJoins(Iterable<Join> joins, int depth, PrintWriter printWriter) {
 		for ( Join join : joins ) {
 			printWriter.println(
 					TreePrinterHelper.INSTANCE.generateNodePrefix( depth )
 							+ "JOIN (" + join.getLeftHandSide().getUid() + " -> " + join.getRightHandSide()
 							.getUid() + ")"
 			);
 			dumpQuerySpace( join.getRightHandSide(), depth+1, printWriter );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/CollectionReferenceAliasesImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/CollectionReferenceAliasesImpl.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/CollectionReferenceAliasesImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/CollectionReferenceAliasesImpl.java
index 6d9812e50d..dc88b50a43 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/CollectionReferenceAliasesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/CollectionReferenceAliasesImpl.java
@@ -1,72 +1,72 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.internal;
+package org.hibernate.loader.plan.exec.internal;
 
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
-import org.hibernate.loader.plan2.exec.spi.CollectionReferenceAliases;
+import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionReferenceAliasesImpl implements CollectionReferenceAliases {
 	private final String tableAlias;
 	private final String manyToManyAssociationTableAlias;
 	private final CollectionAliases collectionAliases;
 	private final EntityAliases entityElementAliases;
 
 	public CollectionReferenceAliasesImpl(
 			String tableAlias,
 			String manyToManyAssociationTableAlias,
 			CollectionAliases collectionAliases,
 			EntityAliases entityElementAliases) {
 		this.tableAlias = tableAlias;
 		this.manyToManyAssociationTableAlias = manyToManyAssociationTableAlias;
 		this.collectionAliases = collectionAliases;
 		this.entityElementAliases = entityElementAliases;
 	}
 
 	@Override
 	public String getCollectionTableAlias() {
 		return StringHelper.isNotEmpty( manyToManyAssociationTableAlias )
 				? manyToManyAssociationTableAlias
 				: tableAlias;
 	}
 
 	@Override
 	public String getElementTableAlias() {
 		return tableAlias;
 	}
 
 	@Override
 	public CollectionAliases getCollectionColumnAliases() {
 		return collectionAliases;
 	}
 
 	@Override
 	public EntityAliases getEntityElementColumnAliases() {
 		return entityElementAliases;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/EntityReferenceAliasesImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/EntityReferenceAliasesImpl.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/EntityReferenceAliasesImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/EntityReferenceAliasesImpl.java
index b5cb89ed3d..d7d5680d7f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/EntityReferenceAliasesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/EntityReferenceAliasesImpl.java
@@ -1,51 +1,51 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.internal;
+package org.hibernate.loader.plan.exec.internal;
 
 import org.hibernate.loader.EntityAliases;
-import org.hibernate.loader.plan2.exec.spi.EntityReferenceAliases;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
 
 /**
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class EntityReferenceAliasesImpl implements EntityReferenceAliases {
 	private final String tableAlias;
 	private final EntityAliases columnAliases;
 
 	public EntityReferenceAliasesImpl(String tableAlias, EntityAliases columnAliases) {
 		this.tableAlias = tableAlias;
 		this.columnAliases = columnAliases;
 	}
 
 	@Override
 	public String getTableAlias() {
 		return tableAlias;
 	}
 
 	@Override
 	public EntityAliases getColumnAliases() {
 		return columnAliases;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/FetchStats.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/FetchStats.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/FetchStats.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/FetchStats.java
index fb42bdd031..c2679cf510 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/FetchStats.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/FetchStats.java
@@ -1,39 +1,39 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.internal;
+package org.hibernate.loader.plan.exec.internal;
 
 /**
  * Contract used to report collected information about fetches.  For now that is only whether there were
  * subselect fetches found
  *
  * @author Steve Ebersole
  */
 public interface FetchStats {
 	/**
 	 * Were any subselect fetches encountered?
 	 *
 	 * @return {@code true} if subselect fetches were encountered; {@code false} otherwise.
 	 */
 	public boolean hasSubselectFetches();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/Helper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/Helper.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/Helper.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/Helper.java
index c474014168..ab9c8f8f47 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/Helper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/Helper.java
@@ -1,117 +1,117 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.internal;
+package org.hibernate.loader.plan.exec.internal;
 
-import org.hibernate.loader.plan2.spi.EntityQuerySpace;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.QuerySpace;
-import org.hibernate.loader.plan2.spi.QuerySpaces;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.spi.EntityQuerySpace;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.QuerySpaces;
+import org.hibernate.loader.plan.spi.Return;
 
 /**
  * @author Steve Ebersole
  */
 public class Helper {
 	/**
 	 * Singleton access
 	 */
 	public static final Helper INSTANCE = new Helper();
 
 	/**
 	 * Disallow direct instantiation
 	 */
 	private Helper() {
 	}
 
 
 	/**
 	 * Extract the root return of the LoadPlan, assuming there is just one.
 	 *
 	 * @param loadPlan The LoadPlan from which to extract the root return
 	 * @param returnType The Return type expected, passed as an argument
 	 * @param <T> The parameterized type of the specific Return type expected
 	 *
 	 * @return The root Return
 	 *
 	 * @throws IllegalStateException If there is no root, more than one root or the single root
 	 * is not of the expected type.
 	 */
 	@SuppressWarnings("unchecked")
 	public <T extends Return> T extractRootReturn(LoadPlan loadPlan, Class<T> returnType) {
 		if ( loadPlan.getReturns().size() == 0 ) {
 			throw new IllegalStateException( "LoadPlan contained no root returns" );
 		}
 		else if ( loadPlan.getReturns().size() > 1 ) {
 			throw new IllegalStateException( "LoadPlan contained more than one root returns" );
 		}
 
 		final Return rootReturn = loadPlan.getReturns().get( 0 );
 		if ( !returnType.isInstance( rootReturn ) ) {
 			throw new IllegalStateException(
 					String.format(
 							"Unexpected LoadPlan root return; expecting %s, but found %s",
 							returnType.getName(),
 							rootReturn.getClass().getName()
 					)
 			);
 		}
 
 		return (T) rootReturn;
 	}
 
 	/**
 	 * Extract the root QuerySpace of the LoadPlan, assuming there is just one.
 	 *
 	 *
 	 * @param querySpaces The QuerySpaces from which to extract the root.
 	 * @param returnType The QuerySpace type expected, passed as an argument
 	 *
 	 * @return The root QuerySpace
 	 *
 	 * @throws IllegalStateException If there is no root, more than one root or the single root
 	 * is not of the expected type.
 	 */
 	@SuppressWarnings("unchecked")
 	public <T extends QuerySpace> T extractRootQuerySpace(QuerySpaces querySpaces, Class<EntityQuerySpace> returnType) {
 		if ( querySpaces.getRootQuerySpaces().size() == 0 ) {
 			throw new IllegalStateException( "LoadPlan contained no root query-spaces" );
 		}
 		else if ( querySpaces.getRootQuerySpaces().size() > 1 ) {
 			throw new IllegalStateException( "LoadPlan contained more than one root query-space" );
 		}
 
 		final QuerySpace querySpace = querySpaces.getRootQuerySpaces().get( 0 );
 		if ( !returnType.isInstance( querySpace ) ) {
 			throw new IllegalStateException(
 					String.format(
 							"Unexpected LoadPlan root query-space; expecting %s, but found %s",
 							returnType.getName(),
 							querySpace.getClass().getName()
 					)
 			);
 		}
 
 		return (T) querySpace;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
index 263ad743c5..60570635de 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
@@ -1,661 +1,661 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.internal;
+package org.hibernate.loader.plan.exec.internal;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.loader.plan2.exec.process.internal.CollectionReferenceInitializerImpl;
-import org.hibernate.loader.plan2.exec.process.internal.EntityReferenceInitializerImpl;
-import org.hibernate.loader.plan2.exec.process.spi.ReaderCollector;
-import org.hibernate.loader.plan2.exec.query.internal.SelectStatementBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.exec.spi.AliasResolutionContext;
-import org.hibernate.loader.plan2.exec.spi.CollectionReferenceAliases;
-import org.hibernate.loader.plan2.exec.spi.EntityReferenceAliases;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
-import org.hibernate.loader.plan2.spi.CollectionQuerySpace;
-import org.hibernate.loader.plan2.spi.CompositeQuerySpace;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityQuerySpace;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
-import org.hibernate.loader.plan2.spi.Join;
-import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
-import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceInitializerImpl;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
+import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.CollectionQuerySpace;
+import org.hibernate.loader.plan.spi.CompositeQuerySpace;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityQuerySpace;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.FetchSource;
+import org.hibernate.loader.plan.spi.Join;
+import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan.spi.QuerySpace;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.CollectionPropertyNames;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.walking.internal.FetchStrategyHelper;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 
 /**
  * Helper for implementors of entity and collection based query building based on LoadPlans providing common
  * functionality, especially in regards to handling QuerySpace {@link Join}s and {@link Fetch}es.
  * <p/>
  * Exposes 2 main methods:<ol>
  *     <li>{@link #processQuerySpaceJoins(QuerySpace, SelectStatementBuilder)}</li>
- *     <li>{@link #processFetches(FetchSource, SelectStatementBuilder, org.hibernate.loader.plan2.exec.process.spi.ReaderCollector)}li>
+ *     <li>{@link #processFetches(FetchSource, SelectStatementBuilder, org.hibernate.loader.plan.exec.process.spi.ReaderCollector)}li>
  * </ol>
  *
  * @author Steve Ebersole
  */
 public class LoadQueryJoinAndFetchProcessor {
 	private static final Logger LOG = CoreLogging.logger( LoadQueryJoinAndFetchProcessor.class );
 
 	private final AliasResolutionContextImpl aliasResolutionContext;
 	private final QueryBuildingParameters buildingParameters;
 	private final SessionFactoryImplementor factory;
 
 	/**
 	 * Instantiates a LoadQueryBuilderHelper with the given information
 	 *
 	 * @param aliasResolutionContext
 	 * @param buildingParameters
 	 * @param factory
 	 */
 	public LoadQueryJoinAndFetchProcessor(
 			AliasResolutionContextImpl aliasResolutionContext,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		this.aliasResolutionContext = aliasResolutionContext;
 		this.buildingParameters = buildingParameters;
 		this.factory = factory;
 	}
 
 	public AliasResolutionContext getAliasResolutionContext() {
 		return aliasResolutionContext;
 	}
 
 	public QueryBuildingParameters getQueryBuildingParameters() {
 		return buildingParameters;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return factory;
 	}
 
 	public void processQuerySpaceJoins(QuerySpace querySpace, SelectStatementBuilder selectStatementBuilder) {
 		LOG.debug( "processing queryspace " + querySpace.getUid() );
 		final JoinFragment joinFragment = factory.getDialect().createOuterJoinFragment();
 		processQuerySpaceJoins( querySpace, joinFragment );
 
 		selectStatementBuilder.setOuterJoins(
 				joinFragment.toFromFragmentString(),
 				joinFragment.toWhereFragmentString()
 		);
 	}
 
 	private void processQuerySpaceJoins(QuerySpace querySpace, JoinFragment joinFragment) {
 		// IMPL NOTES:
 		//
 		// 1) The querySpace and the left-hand-side of each of the querySpace's joins should really be the same.
 		// validate that?  any cases where they wont be the same?
 		//
 		// 2) Assume that the table fragments for the left-hand-side have already been rendered.  We just need to
 		// figure out the proper lhs table alias to use and the column/formula from the lhs to define the join
 		// condition, which can be different per Join
 
 		for ( Join join : querySpace.getJoins() ) {
 			processQuerySpaceJoin( join, joinFragment );
 		}
 	}
 
 	private void processQuerySpaceJoin(Join join, JoinFragment joinFragment) {
 		renderJoin( join, joinFragment );
 		processQuerySpaceJoins( join.getRightHandSide(), joinFragment );
 	}
 
 	private void renderJoin(Join join, JoinFragment joinFragment) {
 		if ( CompositeQuerySpace.class.isInstance( join.getRightHandSide() ) ) {
 			handleCompositeJoin( join, joinFragment );
 		}
 		else if ( EntityQuerySpace.class.isInstance( join.getRightHandSide() ) ) {
 			// do not render the entity join for a one-to-many association, since the collection join
 			// already joins to the associated entity table (see doc in renderCollectionJoin()).
 			if ( join.getLeftHandSide().getDisposition() == QuerySpace.Disposition.COLLECTION ) {
 				if ( CollectionQuerySpace.class.cast( join.getLeftHandSide() ).getCollectionPersister().isManyToMany() ) {
 					renderManyToManyJoin( join, joinFragment );
 				}
 				else if ( JoinDefinedByMetadata.class.isInstance( join ) &&
 						CollectionPropertyNames.COLLECTION_INDICES.equals( JoinDefinedByMetadata.class.cast( join ).getJoinedPropertyName() ) ) {
 					renderManyToManyJoin( join, joinFragment );
 				}
 			}
 			else {
 				renderEntityJoin( join, joinFragment );
 			}
 		}
 		else if ( CollectionQuerySpace.class.isInstance( join.getRightHandSide() ) ) {
 			renderCollectionJoin( join, joinFragment );
 		}
 	}
 
 	private void handleCompositeJoin(Join join, JoinFragment joinFragment) {
 		final String leftHandSideUid = join.getLeftHandSide().getUid();
 		final String rightHandSideUid = join.getRightHandSide().getUid();
 
 		final String leftHandSideTableAlias = aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid( leftHandSideUid );
 		if ( leftHandSideTableAlias == null ) {
 			throw new IllegalStateException(
 					"QuerySpace with that UID was not yet registered in the AliasResolutionContext"
 			);
 		}
 
 		aliasResolutionContext.registerCompositeQuerySpaceUidResolution( rightHandSideUid, leftHandSideTableAlias );
 	}
 
 	private void renderEntityJoin(Join join, JoinFragment joinFragment) {
 		final EntityQuerySpace rightHandSide = (EntityQuerySpace) join.getRightHandSide();
 
 		// see if there is already aliases registered for this entity query space (collection joins)
 		EntityReferenceAliases aliases = aliasResolutionContext.resolveEntityReferenceAliases( rightHandSide.getUid() );
 		if ( aliases == null ) {
 			aliasResolutionContext.generateEntityReferenceAliases(
 					rightHandSide.getUid(),
 					rightHandSide.getEntityPersister()
 			);
 		}
 
 		final Joinable joinable = (Joinable) rightHandSide.getEntityPersister();
 		addJoins(
 				join,
 				joinFragment,
 				joinable
 		);
 	}
 
 	private AssociationType getJoinedAssociationTypeOrNull(Join join) {
 
 		if ( !JoinDefinedByMetadata.class.isInstance( join ) ) {
 			return null;
 		}
 		final Type joinedType = ( (JoinDefinedByMetadata) join ).getJoinedPropertyType();
 		return joinedType.isAssociationType()
 				? (AssociationType) joinedType
 				: null;
 	}
 
 	private String resolveAdditionalJoinCondition(String rhsTableAlias, String withClause, Joinable joinable, AssociationType associationType) {
 		// turns out that the call to AssociationType#getOnCondition in the initial code really just translates to
 		// calls to the Joinable.filterFragment() method where the Joinable is either the entity or
 		// collection persister
 		final String filter = associationType!=null?
 				associationType.getOnCondition( rhsTableAlias, factory, buildingParameters.getQueryInfluencers().getEnabledFilters() ):
 				joinable.filterFragment(
 					rhsTableAlias,
 					buildingParameters.getQueryInfluencers().getEnabledFilters()
 		);
 
 		if ( StringHelper.isEmpty( withClause ) && StringHelper.isEmpty( filter ) ) {
 			return "";
 		}
 		else if ( StringHelper.isNotEmpty( withClause ) && StringHelper.isNotEmpty( filter ) ) {
 			return filter + " and " + withClause;
 		}
 		else {
 			// only one is non-empty...
 			return StringHelper.isNotEmpty( filter ) ? filter : withClause;
 		}
 	}
 
 	private void addJoins(
 			Join join,
 			JoinFragment joinFragment,
 			Joinable joinable) {
 
 		final String rhsTableAlias = aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(
 				join.getRightHandSide().getUid()
 		);
 		if ( StringHelper.isEmpty( rhsTableAlias ) ) {
 			throw new IllegalStateException( "Join's RHS table alias cannot be empty" );
 		}
 
 		final String lhsTableAlias = aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(
 				join.getLeftHandSide().getUid()
 		);
 		if ( lhsTableAlias == null ) {
 			throw new IllegalStateException( "QuerySpace with that UID was not yet registered in the AliasResolutionContext" );
 		}
 
 		// add join fragments from the collection table -> element entity table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		final String additionalJoinConditions = resolveAdditionalJoinCondition(
 				rhsTableAlias,
 				join.getAnyAdditionalJoinConditions( rhsTableAlias ),
 				joinable,
 				getJoinedAssociationTypeOrNull( join )
 		);
 
 		joinFragment.addJoin(
 				joinable.getTableName(),
 				rhsTableAlias,
 				join.resolveAliasedLeftHandSideJoinConditionColumns( lhsTableAlias ),
 				join.resolveNonAliasedRightHandSideJoinConditionColumns(),
 				join.isRightHandSideRequired() ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN,
 				additionalJoinConditions
 		);
 		joinFragment.addJoins(
 				joinable.fromJoinFragment( rhsTableAlias, false, true ),
 				joinable.whereJoinFragment( rhsTableAlias, false, true )
 		);
 	}
 
 	private void renderCollectionJoin(Join join, JoinFragment joinFragment) {
 		final CollectionQuerySpace rightHandSide = (CollectionQuerySpace) join.getRightHandSide();
 		final CollectionReferenceAliases aliases = aliasResolutionContext.generateCollectionReferenceAliases(
 				rightHandSide.getUid(),
 				rightHandSide.getCollectionPersister()
 		);
 
 		// The SQL join to the "collection table" needs to be rendered.
 		//
 		// In the case of a basic collection, that's the only join needed.
 		//
 		// For one-to-many/many-to-many, we need to render the "collection table join"
 		// here (as already stated). There will be a follow-on join (rhs will have a join) for the associated entity.
 		// For many-to-many, the follow-on join will join to the associated entity element table. For one-to-many,
 		// the collection table is the associated entity table, so the follow-on join will not be rendered..
 
 		if ( rightHandSide.getCollectionPersister().isOneToMany()
 				|| rightHandSide.getCollectionPersister().isManyToMany() ) {
 			// relatedly, for collections with entity elements (one-to-many, many-to-many) we need to register the
 			// sql aliases to use for the entity.
 			//
 			// currently we do not explicitly track the joins under the CollectionQuerySpace to know which is
 			// the element join and which is the index join (maybe we should?).  Another option here is to have the
 			// "collection join" act as the entity element join in this case (much like I do with entity identifiers).
 			// The difficulty there is that collections can theoretically could be multiple joins in that case (one
 			// for element, one for index).  However, that's a bit of future-planning as today Hibernate does not
 			// properly deal with the index anyway in terms of allowing dynamic fetching across a collection index...
 			//
 			// long story short, for now we'll use an assumption that the last join in the CollectionQuerySpace is the
 			// element join (that's how the joins are built as of now..)
 			//
 			// todo : remove this assumption ^^; maybe we make CollectionQuerySpace "special" and rather than have it
 			// hold a list of joins, we have it expose the 2 (index, element) separately.
 
 			Join collectionElementJoin = null;
 			for ( Join collectionJoin : rightHandSide.getJoins() ) {
 				collectionElementJoin = collectionJoin;
 			}
 			if ( collectionElementJoin == null ) {
 				throw new IllegalStateException(
 						String.format(
 								"Could not locate collection element join within collection join [%s : %s]",
 								rightHandSide.getUid(),
 								rightHandSide.getCollectionPersister()
 						)
 				);
 			}
 			aliasResolutionContext.registerQuerySpaceAliases(
 					collectionElementJoin.getRightHandSide().getUid(),
 					new EntityReferenceAliasesImpl(
 							aliases.getElementTableAlias(),
 							aliases.getEntityElementColumnAliases()
 					)
 			);
 		}
 
 		addJoins(
 				join,
 				joinFragment,
 				(Joinable) rightHandSide.getCollectionPersister()
 		);
 	}
 
 	private void renderManyToManyJoin(
 			Join join,
 			JoinFragment joinFragment) {
 
 		// for many-to-many we have 3 table aliases.  By way of example, consider a normal m-n: User<->Role
 		// where User is the FetchOwner and Role (User.roles) is the Fetch.  We'd have:
 		//		1) the owner's table : user - in terms of rendering the joins (not the fetch select fragments), the
 		// 			lhs table alias is only needed to qualify the lhs join columns, but we already have the qualified
 		// 			columns here (aliasedLhsColumnNames)
 		//final String ownerTableAlias = ...;
 		//		2) the m-n table : user_role
 		//		3) the element table : role
 		final EntityPersister entityPersister = ( (EntityQuerySpace) join.getRightHandSide() ).getEntityPersister();
 		final String entityTableAlias = aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(
 			join.getRightHandSide().getUid()
 		);
 
 		if ( StringHelper.isEmpty( entityTableAlias ) ) {
 			throw new IllegalStateException( "Collection element (many-to-many) table alias cannot be empty" );
 		}
 		if ( JoinDefinedByMetadata.class.isInstance( join ) &&
 				CollectionPropertyNames.COLLECTION_ELEMENTS.equals( ( (JoinDefinedByMetadata) join ).getJoinedPropertyName() ) ) {
 			final CollectionQuerySpace leftHandSide = (CollectionQuerySpace) join.getLeftHandSide();
 			final CollectionPersister persister = leftHandSide.getCollectionPersister();
 			final String manyToManyFilter = persister.getManyToManyFilterFragment(
 					entityTableAlias,
 					buildingParameters.getQueryInfluencers().getEnabledFilters()
 			);
 			joinFragment.addCondition( manyToManyFilter );
 		}
 
 		addJoins(
 				join,
 				joinFragment,
 				(Joinable) entityPersister
 		);
 	}
 
 	public FetchStats processFetches(
 			FetchSource fetchSource,
 			SelectStatementBuilder selectStatementBuilder,
 			ReaderCollector readerCollector) {
 		final FetchStatsImpl fetchStats = new FetchStatsImpl();
 
 		// if the fetchSource is an entityReference, we should also walk its identifier fetches here...
 		//
 		// what if fetchSource is a composite fetch (as it would be in the case of a key-many-to-one)?
 		if ( EntityReference.class.isInstance( fetchSource ) ) {
 			final EntityReference fetchOwnerAsEntityReference = (EntityReference) fetchSource;
 			if ( fetchOwnerAsEntityReference.getIdentifierDescription().hasFetches() ) {
 				final FetchSource entityIdentifierAsFetchSource = (FetchSource) fetchOwnerAsEntityReference.getIdentifierDescription();
 				for ( Fetch fetch : entityIdentifierAsFetchSource.getFetches() ) {
 					processFetch(
 							selectStatementBuilder,
 							fetchSource,
 							fetch,
 							readerCollector,
 							fetchStats
 					);
 				}
 			}
 		}
 
 		processFetches( fetchSource, selectStatementBuilder, readerCollector, fetchStats );
 		return fetchStats;
 	}
 
 	private void processFetches(
 			FetchSource fetchSource,
 			SelectStatementBuilder selectStatementBuilder,
 			ReaderCollector readerCollector,
 			FetchStatsImpl fetchStats) {
 		for ( Fetch fetch : fetchSource.getFetches() ) {
 			processFetch(
 					selectStatementBuilder,
 					fetchSource,
 					fetch,
 					readerCollector,
 					fetchStats
 			);
 		}
 	}
 
 
 	private void processFetch(
 			SelectStatementBuilder selectStatementBuilder,
 			FetchSource fetchSource,
 			Fetch fetch,
 			ReaderCollector readerCollector,
 			FetchStatsImpl fetchStats) {
 		if ( ! FetchStrategyHelper.isJoinFetched( fetch.getFetchStrategy() ) ) {
 			return;
 		}
 
 		if ( EntityFetch.class.isInstance( fetch ) ) {
 			final EntityFetch entityFetch = (EntityFetch) fetch;
 			processEntityFetch(
 					selectStatementBuilder,
 					fetchSource,
 					entityFetch,
 					readerCollector,
 					fetchStats
 			);
 		}
 		else if ( CollectionAttributeFetch.class.isInstance( fetch ) ) {
 			final CollectionAttributeFetch collectionFetch = (CollectionAttributeFetch) fetch;
 			processCollectionFetch(
 					selectStatementBuilder,
 					fetchSource,
 					collectionFetch,
 					readerCollector,
 					fetchStats
 			);
 		}
 		else {
 			// could also be a CompositeFetch, we ignore those here
 			// but do still need to visit their fetches...
 			if ( FetchSource.class.isInstance( fetch ) ) {
 				processFetches(
 						(FetchSource) fetch,
 						selectStatementBuilder,
 						readerCollector,
 						fetchStats
 				);
 			}
 		}
 	}
 
 	private void processEntityFetch(
 			SelectStatementBuilder selectStatementBuilder,
 			FetchSource fetchSource,
 			EntityFetch fetch,
 			ReaderCollector readerCollector,
 			FetchStatsImpl fetchStats) {
 		// todo : still need to think through expressing bi-directionality in the new model...
 //		if ( BidirectionalEntityFetch.class.isInstance( fetch ) ) {
 //			log.tracef( "Skipping bi-directional entity fetch [%s]", fetch );
 //			return;
 //		}
 
 		fetchStats.processingFetch( fetch );
 
 		// First write out the SQL SELECT fragments
 		final Joinable joinable = (Joinable) fetch.getEntityPersister();
 		EntityReferenceAliases aliases = aliasResolutionContext.resolveEntityReferenceAliases(
 				fetch.getQuerySpaceUid()
 		);
 
 		// the null arguments here relate to many-to-many fetches
 		selectStatementBuilder.appendSelectClauseFragment(
 				joinable.selectFragment(
 						null,
 						null,
 						aliases.getTableAlias(),
 						aliases.getColumnAliases().getSuffix(),
 						null,
 						true
 				)
 		);
 
 		// process its identifier fetches first (building EntityReferenceInitializers for them if needed)
 		if ( fetch.getIdentifierDescription().hasFetches() ) {
 			final FetchSource entityIdentifierAsFetchSource = (FetchSource) fetch.getIdentifierDescription();
 			for ( Fetch identifierFetch : entityIdentifierAsFetchSource.getFetches() ) {
 				processFetch(
 						selectStatementBuilder,
 						fetch,
 						identifierFetch,
 						readerCollector,
 						fetchStats
 				);
 			}
 		}
 
 		// build an EntityReferenceInitializers for the incoming fetch itself
 		readerCollector.add( new EntityReferenceInitializerImpl( fetch, aliases ) );
 
 		// then visit each of our (non-identifier) fetches
 		processFetches( fetch, selectStatementBuilder, readerCollector, fetchStats );
 	}
 
 	private void processCollectionFetch(
 			SelectStatementBuilder selectStatementBuilder,
 			FetchSource fetchSource,
 			CollectionAttributeFetch fetch,
 			ReaderCollector readerCollector,
 			FetchStatsImpl fetchStats) {
 		fetchStats.processingFetch( fetch );
 
 		final CollectionReferenceAliases aliases = aliasResolutionContext.resolveCollectionReferenceAliases(
 				fetch.getQuerySpaceUid()
 		);
 
 		final QueryableCollection queryableCollection = (QueryableCollection) fetch.getCollectionPersister();
 		final Joinable joinableCollection = (Joinable) fetch.getCollectionPersister();
 
 		if ( fetch.getCollectionPersister().isManyToMany() ) {
 			// todo : better way to access `ownerTableAlias` here.
 			// 		when processing the Join part of this we are able to look up the "lhs table alias" because we know
 			// 		the 'lhs' QuerySpace.
 			//
 			// Good idea to be able resolve a Join by lookup on the rhs and lhs uid?  If so, Fetch
 
 			// for many-to-many we have 3 table aliases.  By way of example, consider a normal m-n: User<->Role
 			// where User is the FetchOwner and Role (User.roles) is the Fetch.  We'd have:
 			//		1) the owner's table : user
 			final String ownerTableAlias = aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid( fetchSource.getQuerySpaceUid() );
 			//		2) the m-n table : user_role
 			final String collectionTableAlias = aliases.getCollectionTableAlias();
 			//		3) the element table : role
 			final String elementTableAlias = aliases.getElementTableAlias();
 
 			// add select fragments from the collection table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			selectStatementBuilder.appendSelectClauseFragment(
 					joinableCollection.selectFragment(
 							(Joinable) queryableCollection.getElementPersister(),
 							elementTableAlias,
 							collectionTableAlias,
 
 							aliases.getEntityElementColumnAliases().getSuffix(),
 							aliases.getCollectionColumnAliases().getSuffix(),
 							true
 					)
 			);
 
 			// add select fragments from the element entity table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			final OuterJoinLoadable elementPersister = (OuterJoinLoadable) queryableCollection.getElementPersister();
 			selectStatementBuilder.appendSelectClauseFragment(
 					elementPersister.selectFragment(
 							elementTableAlias,
 							aliases.getEntityElementColumnAliases().getSuffix()
 					)
 			);
 
 			// add SQL ORDER-BY fragments
 			final String manyToManyOrdering = queryableCollection.getManyToManyOrderByString( collectionTableAlias );
 			if ( StringHelper.isNotEmpty( manyToManyOrdering ) ) {
 				selectStatementBuilder.appendOrderByFragment( manyToManyOrdering );
 			}
 
 			final String ordering = queryableCollection.getSQLOrderByString( collectionTableAlias );
 			if ( StringHelper.isNotEmpty( ordering ) ) {
 				selectStatementBuilder.appendOrderByFragment( ordering );
 			}
 
 			// add an EntityReferenceInitializer for the collection elements (keys also?)
 			final EntityReferenceAliases entityReferenceAliases = new EntityReferenceAliasesImpl(
 					aliases.getCollectionTableAlias(),
 					aliases.getEntityElementColumnAliases()
 			);
 			aliasResolutionContext.registerQuerySpaceAliases( fetch.getQuerySpaceUid(), entityReferenceAliases );
 			readerCollector.add(
 					new EntityReferenceInitializerImpl(
 							(EntityReference) fetch.getElementGraph(),
 							entityReferenceAliases
 					)
 			);
 		}
 		else {
 			// select the "collection columns"
 			selectStatementBuilder.appendSelectClauseFragment(
 					queryableCollection.selectFragment(
 							aliases.getElementTableAlias(),
 							aliases.getCollectionColumnAliases().getSuffix()
 					)
 			);
 
 			if ( fetch.getCollectionPersister().isOneToMany() ) {
 				// if the collection elements are entities, select the entity columns as well
 				final OuterJoinLoadable elementPersister = (OuterJoinLoadable) queryableCollection.getElementPersister();
 				selectStatementBuilder.appendSelectClauseFragment(
 						elementPersister.selectFragment(
 								aliases.getElementTableAlias(),
 								aliases.getEntityElementColumnAliases().getSuffix()
 						)
 				);
 				final EntityReferenceAliases entityReferenceAliases = new EntityReferenceAliasesImpl(
 						aliases.getElementTableAlias(),
 						aliases.getEntityElementColumnAliases()
 				);
 				aliasResolutionContext.registerQuerySpaceAliases( fetch.getQuerySpaceUid(), entityReferenceAliases );
 				readerCollector.add(
 						new EntityReferenceInitializerImpl(
 								(EntityReference) fetch.getElementGraph(),
 								entityReferenceAliases
 						)
 				);
 			}
 
 			final String ordering = queryableCollection.getSQLOrderByString( aliases.getElementTableAlias() );
 			if ( StringHelper.isNotEmpty( ordering ) ) {
 				selectStatementBuilder.appendOrderByFragment( ordering );
 			}
 		}
 
 		if ( fetch.getElementGraph() != null ) {
 			processFetches( fetch.getElementGraph(), selectStatementBuilder, readerCollector );
 		}
 
 		readerCollector.add( new CollectionReferenceInitializerImpl( fetch, aliases ) );
 	}
 
 	/**
 	 * Implementation of FetchStats
 	 */
 	private static class FetchStatsImpl implements FetchStats {
 		private boolean hasSubselectFetch;
 
 		public void processingFetch(Fetch fetch) {
 			if ( ! hasSubselectFetch ) {
 				if ( fetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT
 						&& fetch.getFetchStrategy().getTiming() != FetchTiming.IMMEDIATE ) {
 					hasSubselectFetch = true;
 				}
 			}
 		}
 
 		@Override
 		public boolean hasSubselectFetches() {
 			return hasSubselectFetch;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/package-info.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/package-info.java
similarity index 73%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/package-info.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/package-info.java
index b148f34bb4..57302dcd75 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/package-info.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/package-info.java
@@ -1,4 +1,4 @@
 /**
  * This package supports converting a LoadPlan to SQL and generating readers for the resulting ResultSet
  */
-package org.hibernate.loader.plan2.exec;
+package org.hibernate.loader.plan.exec;
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/CollectionReferenceInitializerImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReferenceInitializerImpl.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/CollectionReferenceInitializerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReferenceInitializerImpl.java
index 834d5b729c..d5743b9836 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/CollectionReferenceInitializerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReferenceInitializerImpl.java
@@ -1,173 +1,173 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.exec.process.spi.CollectionReferenceInitializer;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.loader.plan2.exec.spi.CollectionReferenceAliases;
-import org.hibernate.loader.plan2.spi.CollectionReference;
-import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
+import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionReferenceInitializerImpl implements CollectionReferenceInitializer {
 	private static final Logger log = CoreLogging.logger( CollectionReferenceInitializerImpl.class );
 
 	private final CollectionReference collectionReference;
 	private final CollectionReferenceAliases aliases;
 
 	public CollectionReferenceInitializerImpl(CollectionReference collectionReference, CollectionReferenceAliases aliases) {
 		this.collectionReference = collectionReference;
 		this.aliases = aliases;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
 
 		try {
 			// read the collection key for this reference for the current row.
 			final PersistenceContext persistenceContext = context.getSession().getPersistenceContext();
 			final Serializable collectionRowKey = (Serializable) collectionReference.getCollectionPersister().readKey(
 					resultSet,
 					aliases.getCollectionColumnAliases().getSuffixedKeyAliases(),
 					context.getSession()
 			);
 
 			if ( collectionRowKey != null ) {
 				// we found a collection element in the result set
 
 				if ( log.isDebugEnabled() ) {
 					log.debugf(
 							"Found row of collection: %s",
 							MessageHelper.collectionInfoString(
 									collectionReference.getCollectionPersister(),
 									collectionRowKey,
 									context.getSession().getFactory()
 							)
 					);
 				}
 
 				Object collectionOwner = findCollectionOwner( collectionRowKey, resultSet, context );
 
 				PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 						.getCollectionLoadContext( resultSet )
 						.getLoadingCollection( collectionReference.getCollectionPersister(), collectionRowKey );
 
 				if ( rowCollection != null ) {
 					rowCollection.readFrom(
 							resultSet,
 							collectionReference.getCollectionPersister(),
 							aliases.getCollectionColumnAliases(),
 							collectionOwner
 					);
 				}
 
 			}
 			else {
 				final Serializable optionalKey = findCollectionOwnerKey( context );
 				if ( optionalKey != null ) {
 					// we did not find a collection element in the result set, so we
 					// ensure that a collection is created with the owner's identifier,
 					// since what we have is an empty collection
 					if ( log.isDebugEnabled() ) {
 						log.debugf(
 								"Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString(
 										collectionReference.getCollectionPersister(),
 										optionalKey,
 										context.getSession().getFactory()
 								)
 						);
 					}
 					// handle empty collection
 					persistenceContext.getLoadContexts()
 							.getCollectionLoadContext( resultSet )
 							.getLoadingCollection( collectionReference.getCollectionPersister(), optionalKey );
 
 				}
 			}
 			// else no collection element, but also no owner
 		}
 		catch ( SQLException sqle ) {
 			// TODO: would be nice to have the SQL string that failed...
 			throw context.getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not read next row of results"
 			);
 		}
 	}
 
 	protected Object findCollectionOwner(
 			Serializable collectionRowKey,
 			ResultSet resultSet,
 			ResultSetProcessingContextImpl context) {
 		final Object collectionOwner = context.getSession().getPersistenceContext().getCollectionOwner(
 				collectionRowKey,
 				collectionReference.getCollectionPersister()
 		);
-		// todo : try org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext.getOwnerProcessingState() ??
+		// todo : try org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.getOwnerProcessingState() ??
 		//			-- specifically to return its ResultSetProcessingContext.EntityReferenceProcessingState#getEntityInstance()
 		if ( collectionOwner == null ) {
 			//TODO: This is assertion is disabled because there is a bug that means the
 			//	  original owner of a transient, uninitialized collection is not known
 			//	  if the collection is re-referenced by a different object associated
 			//	  with the current Session
 			//throw new AssertionFailure("bug loading unowned collection");
 		}
 		return collectionOwner;
 	}
 
 	protected Serializable findCollectionOwnerKey(ResultSetProcessingContextImpl context) {
 		ResultSetProcessingContext.EntityReferenceProcessingState ownerState = context.getOwnerProcessingState( (Fetch) collectionReference );
 
 		if(ownerState == null || ownerState.getEntityKey()==null){
 			return null;
 		}
 		return ownerState.getEntityKey().getIdentifier();
 	}
 
 	@Override
 	public void endLoading(ResultSetProcessingContextImpl context) {
 		context.getSession().getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( context.getResultSet() )
 				.endLoadingCollections( collectionReference.getCollectionPersister() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/CollectionReturnReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReturnReader.java
similarity index 85%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/CollectionReturnReader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReturnReader.java
index e05703b780..f209b8fa6a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/CollectionReturnReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReturnReader.java
@@ -1,47 +1,47 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.loader.plan2.exec.process.spi.ReturnReader;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
+import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
+import org.hibernate.loader.plan.spi.CollectionReturn;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionReturnReader implements ReturnReader {
 	private final CollectionReturn collectionReturn;
 
 	public CollectionReturnReader(CollectionReturn collectionReturn) {
 		this.collectionReturn = collectionReturn;
 	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/EntityReferenceInitializerImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceInitializerImpl.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/EntityReferenceInitializerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceInitializerImpl.java
index 54a89d0a23..030dff694d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/EntityReferenceInitializerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceInitializerImpl.java
@@ -1,511 +1,511 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.loader.EntityAliases;
-import org.hibernate.loader.plan2.exec.process.spi.EntityReferenceInitializer;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.loader.plan2.exec.spi.EntityReferenceAliases;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
-import static org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
+import static org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReferenceInitializerImpl implements EntityReferenceInitializer {
 	private static final Logger log = CoreLogging.logger( EntityReferenceInitializerImpl.class );
 
 	private final EntityReference entityReference;
 	private final EntityReferenceAliases entityReferenceAliases;
 	private final boolean isReturn;
 
 	public EntityReferenceInitializerImpl(
 			EntityReference entityReference,
 			EntityReferenceAliases entityReferenceAliases) {
 		this( entityReference, entityReferenceAliases, false );
 	}
 
 	public EntityReferenceInitializerImpl(
 			EntityReference entityReference,
 			EntityReferenceAliases entityReferenceAliases,
 			boolean isRoot) {
 		this.entityReference = entityReference;
 		this.entityReferenceAliases = entityReferenceAliases;
 		isReturn = isRoot;
 	}
 
 	@Override
 	public EntityReference getEntityReference() {
 		return entityReference;
 	}
 
 	@Override
 	public void hydrateIdentifier(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 
 		final EntityReferenceProcessingState processingState = context.getProcessingState( entityReference );
 
 		// get any previously registered identifier hydrated-state
 		Object identifierHydratedForm = processingState.getIdentifierHydratedForm();
 		if ( identifierHydratedForm == null ) {
 			// if there is none, read it from the result set
 			identifierHydratedForm = readIdentifierHydratedState( resultSet, context );
 
 			// broadcast the fact that a hydrated identifier value just became associated with
 			// this entity reference
 			processingState.registerIdentifierHydratedForm( identifierHydratedForm );
 		}
 	}
 
 	/**
 	 * Read the identifier state for the entity reference for the currently processing row in the ResultSet
 	 *
 	 * @param resultSet The ResultSet being processed
 	 * @param context The processing context
 	 *
 	 * @return The hydrated state
 	 *
 	 * @throws java.sql.SQLException Indicates a problem accessing the ResultSet
 	 */
 	private Object readIdentifierHydratedState(ResultSet resultSet, ResultSetProcessingContext context)
 			throws SQLException {
 		try {
 			return entityReference.getEntityPersister().getIdentifierType().hydrate(
 					resultSet,
 					entityReferenceAliases.getColumnAliases().getSuffixedKeyAliases(),
 					context.getSession(),
 					null
 			);
 		}
 		catch (Exception e) {
 			throw new HibernateException(
 					"Encountered problem trying to hydrate identifier for entity ["
 							+ entityReference.getEntityPersister() + "]",
 					e
 			);
 		}
 	}
 
 	@Override
 	public void resolveEntityKey(ResultSet resultSet, ResultSetProcessingContextImpl context) {
 
 		final EntityReferenceProcessingState processingState = context.getProcessingState( entityReference );
 
 		// see if we already have an EntityKey associated with this EntityReference in the processing state.
 		// if we do, this should have come from the optional entity identifier...
 		final EntityKey entityKey = processingState.getEntityKey();
 		if ( entityKey != null ) {
 			log.debugf(
 					"On call to EntityIdentifierReaderImpl#resolve [for %s], EntityKey was already known; " +
 							"should only happen on root returns with an optional identifier specified"
 			);
 			return;
 		}
 
 		// Look for the hydrated form
 		final Object identifierHydratedForm = processingState.getIdentifierHydratedForm();
 		if ( identifierHydratedForm == null ) {
 			// we need to register the missing identifier, but that happens later after all readers have had a chance
 			// to resolve its EntityKey
 			return;
 		}
 
 		final Type identifierType = entityReference.getEntityPersister().getIdentifierType();
 		final Serializable resolvedId = (Serializable) identifierType.resolve(
 				identifierHydratedForm,
 				context.getSession(),
 				null
 		);
 		if ( resolvedId != null ) {
 			processingState.registerEntityKey(
 					context.getSession().generateEntityKey( resolvedId, entityReference.getEntityPersister() )
 			);
 		}
 	}
 
 	@Override
 	public void hydrateEntityState(ResultSet resultSet, ResultSetProcessingContextImpl context) {
 		final EntityReferenceProcessingState processingState = context.getProcessingState( entityReference );
 
 		// If there is no identifier for this entity reference for this row, nothing to do
 		if ( processingState.isMissingIdentifier() ) {
 			handleMissingIdentifier( context );
 			return;
 		}
 
 		// make sure we have the EntityKey
 		final EntityKey entityKey = processingState.getEntityKey();
 		if ( entityKey == null ) {
 			handleMissingIdentifier( context );
 			return;
 		}
 
 		// Have we already hydrated this entity's state?
 		if ( processingState.getEntityInstance() != null ) {
 			return;
 		}
 
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// In getting here, we know that:
 		// 		1) We need to hydrate the entity state
 		//		2) We have a valid EntityKey for the entity
 
 		// see if we have an existing entry in the session for this EntityKey
 		final Object existing = context.getSession().getEntityUsingInterceptor( entityKey );
 		if ( existing != null ) {
 			// It is previously associated with the Session, perform some checks
 			if ( ! entityReference.getEntityPersister().isInstance( existing ) ) {
 				throw new WrongClassException(
 						"loaded object was of wrong class " + existing.getClass(),
 						entityKey.getIdentifier(),
 						entityReference.getEntityPersister().getEntityName()
 				);
 			}
 			checkVersion( resultSet, context, entityKey, existing );
 
 			// use the existing association as the hydrated state
 			processingState.registerEntityInstance( existing );
 			//context.registerHydratedEntity( entityReference, entityKey, existing );
 			return;
 		}
 
 		// Otherwise, we need to load it from the ResultSet...
 
 		// determine which entity instance to use.  Either the supplied one, or instantiate one
 		Object optionalEntityInstance = null;
 		if ( isReturn && context.shouldUseOptionalEntityInformation() ) {
 			final EntityKey optionalEntityKey = ResultSetProcessorHelper.getOptionalObjectKey(
 					context.getQueryParameters(),
 					context.getSession()
 			);
 			if ( optionalEntityKey != null ) {
 				if ( optionalEntityKey.equals( entityKey ) ) {
 					optionalEntityInstance = context.getQueryParameters().getOptionalObject();
 				}
 			}
 		}
 
 		final String concreteEntityTypeName = getConcreteEntityTypeName( resultSet, context, entityKey );
 
 		final Object entityInstance = optionalEntityInstance != null
 				? optionalEntityInstance
 				: context.getSession().instantiate( concreteEntityTypeName, entityKey.getIdentifier() );
 
 		processingState.registerEntityInstance( entityInstance );
 
 		// need to hydrate it.
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		log.trace( "hydrating entity state" );
 		final LockMode requestedLockMode = context.resolveLockMode( entityReference );
 		final LockMode lockModeToAcquire = requestedLockMode == LockMode.NONE
 				? LockMode.READ
 				: requestedLockMode;
 
 		loadFromResultSet(
 				resultSet,
 				context,
 				entityInstance,
 				concreteEntityTypeName,
 				entityKey,
 				lockModeToAcquire
 		);
 	}
 
 	private void handleMissingIdentifier(ResultSetProcessingContext context) {
 		if ( EntityFetch.class.isInstance( entityReference ) ) {
 			final EntityFetch fetch = (EntityFetch) entityReference;
 			final EntityType fetchedType = fetch.getFetchedType();
 			if ( ! fetchedType.isOneToOne() ) {
 				return;
 			}
 
 			final EntityReferenceProcessingState fetchOwnerState = context.getOwnerProcessingState( fetch );
 			if ( fetchOwnerState == null ) {
 				throw new IllegalStateException( "Could not locate fetch owner state" );
 			}
 
 			final EntityKey ownerEntityKey = fetchOwnerState.getEntityKey();
 			if ( ownerEntityKey != null ) {
 				context.getSession().getPersistenceContext().addNullProperty(
 						ownerEntityKey,
 						fetchedType.getPropertyName()
 				);
 			}
 		}
 	}
 
 	private void loadFromResultSet(
 			ResultSet resultSet,
 			ResultSetProcessingContext context,
 			Object entityInstance,
 			String concreteEntityTypeName,
 			EntityKey entityKey,
 			LockMode lockModeToAcquire) {
 		final Serializable id = entityKey.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable concreteEntityPersister = (Loadable) context.getSession().getFactory().getEntityPersister( concreteEntityTypeName );
 
 		if ( log.isTraceEnabled() ) {
 			log.tracev(
 					"Initializing object from ResultSet: {0}",
 					MessageHelper.infoString(
 							concreteEntityPersister,
 							id,
 							context.getSession().getFactory()
 					)
 			);
 		}
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				entityKey,
 				entityInstance,
 				concreteEntityPersister,
 				lockModeToAcquire,
 				!context.getLoadPlan().areLazyAttributesForceFetched(),
 				context.getSession()
 		);
 
 		final EntityPersister rootEntityPersister = context.getSession().getFactory().getEntityPersister(
 				concreteEntityPersister.getRootEntityName()
 		);
 		final Object[] values;
 		try {
 			values = concreteEntityPersister.hydrate(
 					resultSet,
 					id,
 					entityInstance,
 					(Loadable) entityReference.getEntityPersister(),
 					concreteEntityPersister == rootEntityPersister
 							? entityReferenceAliases.getColumnAliases().getSuffixedPropertyAliases()
 							: entityReferenceAliases.getColumnAliases().getSuffixedPropertyAliases( concreteEntityPersister ),
 					context.getLoadPlan().areLazyAttributesForceFetched(),
 					context.getSession()
 			);
 
 			context.getProcessingState( entityReference ).registerHydratedState( values );
 		}
 		catch (SQLException e) {
 			throw context.getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity state from ResultSet : " + entityKey
 			);
 		}
 
 		final Object rowId;
 		try {
 			rowId = concreteEntityPersister.hasRowId()
 					? resultSet.getObject( entityReferenceAliases.getColumnAliases().getRowIdAlias() )
 					: null;
 		}
 		catch (SQLException e) {
 			throw context.getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity row-id from ResultSet : " + entityKey
 			);
 		}
 
 		final EntityType entityType = EntityFetch.class.isInstance( entityReference )
 				? ( (EntityFetch) entityReference ).getFetchedType()
 				: entityReference.getEntityPersister().getEntityMetamodel().getEntityType();
 
 		if ( entityType != null ) {
 			String ukName = entityType.getRHSUniqueKeyPropertyName();
 			if ( ukName != null ) {
 				final int index = ( (UniqueKeyLoadable) concreteEntityPersister ).getPropertyIndex( ukName );
 				final Type type = concreteEntityPersister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						entityReference.getEntityPersister().getEntityName(),
 						ukName,
 						type.semiResolve( values[index], context.getSession(), entityInstance ),
 						type,
 						concreteEntityPersister.getEntityMode(),
 						context.getSession().getFactory()
 				);
 				context.getSession().getPersistenceContext().addEntity( euk, entityInstance );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				concreteEntityPersister,
 				id,
 				values,
 				rowId,
 				entityInstance,
 				lockModeToAcquire,
 				!context.getLoadPlan().areLazyAttributesForceFetched(),
 				context.getSession()
 		);
 
 		context.registerHydratedEntity( entityReference, entityKey, entityInstance );
 	}
 
 	private String getConcreteEntityTypeName(
 			ResultSet resultSet,
 			ResultSetProcessingContext context,
 			EntityKey entityKey) {
 		final Loadable loadable = (Loadable) entityReference.getEntityPersister();
 		if ( ! loadable.hasSubclasses() ) {
 			return entityReference.getEntityPersister().getEntityName();
 		}
 
 		final Object discriminatorValue;
 		try {
 			discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(
 					resultSet,
 					entityReferenceAliases.getColumnAliases().getSuffixedDiscriminatorAlias(),
 					context.getSession(),
 					null
 			);
 		}
 		catch (SQLException e) {
 			throw context.getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read discriminator value from ResultSet"
 			);
 		}
 
 		final String result = loadable.getSubclassForDiscriminatorValue( discriminatorValue );
 
 		if ( result == null ) {
 			// whoops! we got an instance of another class hierarchy branch
 			throw new WrongClassException(
 					"Discriminator: " + discriminatorValue,
 					entityKey.getIdentifier(),
 					entityReference.getEntityPersister().getEntityName()
 			);
 		}
 
 		return result;
 	}
 
 	private void checkVersion(
 			ResultSet resultSet,
 			ResultSetProcessingContext context,
 			EntityKey entityKey,
 			Object existing) {
 		final LockMode requestedLockMode = context.resolveLockMode( entityReference );
 		if ( requestedLockMode != LockMode.NONE ) {
 			final LockMode currentLockMode = context.getSession().getPersistenceContext().getEntry( existing ).getLockMode();
 			final boolean isVersionCheckNeeded = entityReference.getEntityPersister().isVersioned()
 					&& currentLockMode.lessThan( requestedLockMode );
 
 			// we don't need to worry about existing version being uninitialized because this block isn't called
 			// by a re-entrant load (re-entrant loads *always* have lock mode NONE)
 			if ( isVersionCheckNeeded ) {
 				//we only check the version when *upgrading* lock modes
 				checkVersion(
 						context.getSession(),
 						resultSet,
 						entityReference.getEntityPersister(),
 						entityReferenceAliases.getColumnAliases(),
 						entityKey,
 						existing
 				);
 				//we need to upgrade the lock mode to the mode requested
 				context.getSession().getPersistenceContext().getEntry( existing ).setLockMode( requestedLockMode );
 			}
 		}
 	}
 
 	private void checkVersion(
 			SessionImplementor session,
 			ResultSet resultSet,
 			EntityPersister persister,
 			EntityAliases entityAliases,
 			EntityKey entityKey,
 			Object entityInstance) {
 		final Object version = session.getPersistenceContext().getEntry( entityInstance ).getVersion();
 
 		if ( version != null ) {
 			//null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			final Object currentVersion;
 			try {
 				currentVersion = versionType.nullSafeGet(
 						resultSet,
 						entityAliases.getSuffixedVersionAliases(),
 						session,
 						null
 				);
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 						e,
 						"Could not read version value from result set"
 				);
 			}
 
 			if ( !versionType.isEqual( version, currentVersion ) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor().optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), entityKey.getIdentifier() );
 			}
 		}
 	}
 
 	@Override
 	public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
 		// cant remember exactly what I was thinking here.  Maybe managing the row value caching stuff that is currently
 		// done in ResultSetProcessingContextImpl.finishUpRow()
 		//
 		// anything else?
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/EntityReturnReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReturnReader.java
similarity index 88%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/EntityReturnReader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReturnReader.java
index ccbc417f62..d417c25d14 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/EntityReturnReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReturnReader.java
@@ -1,85 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.engine.spi.EntityKey;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.loader.plan2.exec.process.spi.ReturnReader;
-import org.hibernate.loader.plan2.spi.EntityReturn;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
+import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
+import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.proxy.HibernateProxy;
 
-import static org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
+import static org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReturnReader implements ReturnReader {
 	private final EntityReturn entityReturn;
 
 	public EntityReturnReader(EntityReturn entityReturn) {
 		this.entityReturn = entityReturn;
 	}
 
 	public EntityReferenceProcessingState getIdentifierResolutionContext(ResultSetProcessingContext context) {
 		final EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState( entityReturn );
 
 		if ( entityReferenceProcessingState == null ) {
 			throw new AssertionFailure(
 					String.format(
 							"Could not locate EntityReferenceProcessingState for root entity return [%s (%s)]",
 							entityReturn.getPropertyPath().getFullPath(),
 							entityReturn.getEntityPersister().getEntityName()
 					)
 			);
 		}
 
 		return entityReferenceProcessingState;
 	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		final EntityReferenceProcessingState processingState = getIdentifierResolutionContext( context );
 
 		final EntityKey entityKey = processingState.getEntityKey();
 		final Object entityInstance = context.getProcessingState( entityReturn ).getEntityInstance();
 
 		if ( context.shouldReturnProxies() ) {
 			final Object proxy = context.getSession().getPersistenceContext().proxyFor(
 					entityReturn.getEntityPersister(),
 					entityKey,
 					entityInstance
 			);
 			if ( proxy != entityInstance ) {
 				( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation( proxy );
 				return proxy;
 			}
 		}
 
 		return entityInstance;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/HydratedEntityRegistration.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/HydratedEntityRegistration.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/HydratedEntityRegistration.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/HydratedEntityRegistration.java
index 2a819bc735..c8f616d665 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/HydratedEntityRegistration.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/HydratedEntityRegistration.java
@@ -1,54 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import org.hibernate.engine.spi.EntityKey;
-import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan.spi.EntityReference;
 
 /**
  * @author Steve Ebersole
  */
 public class HydratedEntityRegistration {
 	private final EntityReference entityReference;
 	private final EntityKey key;
 	private Object instance;
 
 	HydratedEntityRegistration(EntityReference entityReference, EntityKey key, Object instance) {
 		this.entityReference = entityReference;
 		this.key = key;
 		this.instance = instance;
 	}
 
 	public EntityReference getEntityReference() {
 		return entityReference;
 	}
 
 	public EntityKey getKey() {
 		return key;
 	}
 
 	public Object getInstance() {
 		return instance;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessingContextImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
index b504acdbc2..fc00616dde 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessingContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
@@ -1,380 +1,378 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.spi.CompositeFetch;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessingContextImpl implements ResultSetProcessingContext {
 	private static final Logger LOG = Logger.getLogger( ResultSetProcessingContextImpl.class );
 
 	private final ResultSet resultSet;
 	private final SessionImplementor session;
 	private final LoadPlan loadPlan;
 	private final boolean readOnly;
 	private final boolean shouldUseOptionalEntityInformation;
 	private final boolean forceFetchLazyAttributes;
 	private final boolean shouldReturnProxies;
 	private final QueryParameters queryParameters;
 	private final NamedParameterContext namedParameterContext;
 	private final boolean hadSubselectFetches;
 
 	private List<HydratedEntityRegistration> currentRowHydratedEntityRegistrationList;
 
 	private Map<EntityPersister,Set<EntityKey>> subselectLoadableEntityKeyMap;
 	private List<HydratedEntityRegistration> hydratedEntityRegistrationList;
 
 	/**
 	 * Builds a ResultSetProcessingContextImpl
 	 *
 	 * @param resultSet
 	 * @param session
 	 * @param loadPlan
 	 * @param readOnly
 	 * @param shouldUseOptionalEntityInformation There are times when the "optional entity information" on
 	 * QueryParameters should be used and times when they should not.  Collection initializers, batch loaders, etc
 	 * are times when it should NOT be used.
 	 * @param forceFetchLazyAttributes
 	 * @param shouldReturnProxies
 	 * @param queryParameters
 	 * @param namedParameterContext
 	 * @param hadSubselectFetches
 	 */
 	public ResultSetProcessingContextImpl(
 			final ResultSet resultSet,
 			final SessionImplementor session,
 			final LoadPlan loadPlan,
 			final boolean readOnly,
 			final boolean shouldUseOptionalEntityInformation,
 			final boolean forceFetchLazyAttributes,
 			final boolean shouldReturnProxies,
 			final QueryParameters queryParameters,
 			final NamedParameterContext namedParameterContext,
 			final boolean hadSubselectFetches) {
 		this.resultSet = resultSet;
 		this.session = session;
 		this.loadPlan = loadPlan;
 		this.readOnly = readOnly;
 		this.shouldUseOptionalEntityInformation = shouldUseOptionalEntityInformation;
 		this.forceFetchLazyAttributes = forceFetchLazyAttributes;
 		this.shouldReturnProxies = shouldReturnProxies;
 		this.queryParameters = queryParameters;
 		this.namedParameterContext = namedParameterContext;
 		this.hadSubselectFetches = hadSubselectFetches;
 
 		if ( shouldUseOptionalEntityInformation ) {
 			if ( queryParameters.getOptionalId() != null ) {
 				// make sure we have only one return
 				if ( loadPlan.getReturns().size() > 1 ) {
 					throw new IllegalStateException( "Cannot specify 'optional entity' values with multi-return load plans" );
 				}
 			}
 		}
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public boolean shouldUseOptionalEntityInformation() {
 		return shouldUseOptionalEntityInformation;
 	}
 
 	@Override
 	public QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	@Override
 	public boolean shouldReturnProxies() {
 		return shouldReturnProxies;
 	}
 
 	@Override
 	public LoadPlan getLoadPlan() {
 		return loadPlan;
 	}
 
 	public ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	@Override
 	public LockMode resolveLockMode(EntityReference entityReference) {
 		if ( queryParameters.getLockOptions() != null && queryParameters.getLockOptions()
 				.getLockMode() != null ) {
 			return queryParameters.getLockOptions().getLockMode();
 		}
 		return LockMode.READ;
 	}
 
 	private Map<EntityReference,EntityReferenceProcessingState> identifierResolutionContextMap;
 
 	@Override
 	public EntityReferenceProcessingState getProcessingState(final EntityReference entityReference) {
 		if ( identifierResolutionContextMap == null ) {
 			identifierResolutionContextMap = new IdentityHashMap<EntityReference, EntityReferenceProcessingState>();
 		}
 
 		EntityReferenceProcessingState context = identifierResolutionContextMap.get( entityReference );
 		if ( context == null ) {
 			context = new EntityReferenceProcessingState() {
 				private boolean wasMissingIdentifier;
 				private Object identifierHydratedForm;
 				private EntityKey entityKey;
 				private Object[] hydratedState;
 				private Object entityInstance;
 
 				@Override
 				public EntityReference getEntityReference() {
 					return entityReference;
 				}
 
 				@Override
 				public void registerMissingIdentifier() {
 					if ( !EntityFetch.class.isInstance( entityReference ) ) {
 						throw new IllegalStateException( "Missing return row identifier" );
 					}
 					ResultSetProcessingContextImpl.this.registerNonExists( (EntityFetch) entityReference );
 					wasMissingIdentifier = true;
 				}
 
 				@Override
 				public boolean isMissingIdentifier() {
 					return wasMissingIdentifier;
 				}
 
 				@Override
 				public void registerIdentifierHydratedForm(Object identifierHydratedForm) {
 					this.identifierHydratedForm = identifierHydratedForm;
 				}
 
 				@Override
 				public Object getIdentifierHydratedForm() {
 					return identifierHydratedForm;
 				}
 
 				@Override
 				public void registerEntityKey(EntityKey entityKey) {
 					this.entityKey = entityKey;
 				}
 
 				@Override
 				public EntityKey getEntityKey() {
 					return entityKey;
 				}
 
 				@Override
 				public void registerHydratedState(Object[] hydratedState) {
 					this.hydratedState = hydratedState;
 				}
 
 				@Override
 				public Object[] getHydratedState() {
 					return hydratedState;
 				}
 
 				@Override
 				public void registerEntityInstance(Object entityInstance) {
 					this.entityInstance = entityInstance;
 				}
 
 				@Override
 				public Object getEntityInstance() {
 					return entityInstance;
 				}
 			};
 			identifierResolutionContextMap.put( entityReference, context );
 		}
 
 		return context;
 	}
 
 	private void registerNonExists(EntityFetch fetch) {
 		final EntityType fetchedType = fetch.getFetchedType();
 		if ( ! fetchedType.isOneToOne() ) {
 			return;
 		}
 
 		final EntityReferenceProcessingState fetchOwnerState = getOwnerProcessingState( fetch );
 		if ( fetchOwnerState == null ) {
 			throw new IllegalStateException( "Could not locate fetch owner state" );
 		}
 
 		final EntityKey ownerEntityKey = fetchOwnerState.getEntityKey();
 		if ( ownerEntityKey == null ) {
 			throw new IllegalStateException( "Could not locate fetch owner EntityKey" );
 		}
 
 		session.getPersistenceContext().addNullProperty(
 				ownerEntityKey,
 				fetchedType.getPropertyName()
 		);
 	}
 
 	@Override
 	public EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch) {
 		return getProcessingState( fetch.getSource().resolveEntityReference() );
 	}
 
 	@Override
 	public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance) {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			currentRowHydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		currentRowHydratedEntityRegistrationList.add(
 				new HydratedEntityRegistration(
 						entityReference,
 						entityKey,
 						entityInstance
 				)
 		);
 	}
 
 	/**
 	 * Package-protected
 	 */
 	void finishUpRow() {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			if ( identifierResolutionContextMap != null ) {
 				identifierResolutionContextMap.clear();
 			}
 			return;
 		}
 
 
 		// managing the running list of registrations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( hydratedEntityRegistrationList == null ) {
 			hydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		hydratedEntityRegistrationList.addAll( currentRowHydratedEntityRegistrationList );
 
 
 		// managing the map forms needed for subselect fetch generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( hadSubselectFetches ) {
 			if ( subselectLoadableEntityKeyMap == null ) {
 				subselectLoadableEntityKeyMap = new HashMap<EntityPersister, Set<EntityKey>>();
 			}
 			for ( HydratedEntityRegistration registration : currentRowHydratedEntityRegistrationList ) {
 				Set<EntityKey> entityKeys = subselectLoadableEntityKeyMap.get( registration.getEntityReference()
 																					   .getEntityPersister() );
 				if ( entityKeys == null ) {
 					entityKeys = new HashSet<EntityKey>();
 					subselectLoadableEntityKeyMap.put( registration.getEntityReference().getEntityPersister(), entityKeys );
 				}
 				entityKeys.add( registration.getKey() );
 			}
 		}
 
 		// release the currentRowHydratedEntityRegistrationList entries
 		currentRowHydratedEntityRegistrationList.clear();
 
 		identifierResolutionContextMap.clear();
 	}
 
 	public List<HydratedEntityRegistration> getHydratedEntityRegistrationList() {
 		return hydratedEntityRegistrationList;
 	}
 
 	/**
 	 * Package-protected
 	 */
 	void wrapUp() {
 		createSubselects();
 
 		if ( hydratedEntityRegistrationList != null ) {
 			hydratedEntityRegistrationList.clear();
 			hydratedEntityRegistrationList = null;
 		}
 
 		if ( subselectLoadableEntityKeyMap != null ) {
 			subselectLoadableEntityKeyMap.clear();
 			subselectLoadableEntityKeyMap = null;
 		}
 	}
 
 	private void createSubselects() {
 		if ( subselectLoadableEntityKeyMap == null || subselectLoadableEntityKeyMap.size() <= 1 ) {
 			// if we only returned one entity, query by key is more efficient; so do nothing here
 			return;
 		}
 
 		final Map<String, int[]> namedParameterLocMap =
 				ResultSetProcessorHelper.buildNamedParameterLocMap( queryParameters, namedParameterContext );
 
 		for ( Map.Entry<EntityPersister, Set<EntityKey>> entry : subselectLoadableEntityKeyMap.entrySet() ) {
 			if ( ! entry.getKey().hasSubselectLoadableCollections() ) {
 				continue;
 			}
 
 			SubselectFetch subselectFetch = new SubselectFetch(
 					//getSQLString(),
 					null, // aliases[i],
 					(Loadable) entry.getKey(),
 					queryParameters,
 					entry.getValue(),
 					namedParameterLocMap
 			);
 
 			for ( EntityKey key : entry.getValue() ) {
 				session.getPersistenceContext().getBatchFetchQueue().addSubselect( key, subselectFetch );
 			}
 
 		}
 	}
 
 	public boolean isReadOnly() {
 		return readOnly;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessorHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorHelper.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessorHelper.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorHelper.java
index d534ee70ad..546cdda204 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessorHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorHelper.java
@@ -1,103 +1,103 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessorHelper {
 	/**
 	 * Singleton access
 	 */
 	public static final ResultSetProcessorHelper INSTANCE = new ResultSetProcessorHelper();
 
 	public static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		return INSTANCE.interpretEntityKey( session, optionalEntityName, optionalId, optionalObject );
 	}
 
 	public EntityKey interpretEntityKey(
 			SessionImplementor session,
 			String optionalEntityName,
 			Serializable optionalId,
 			Object optionalObject) {
 		if ( optionalEntityName != null ) {
 			final EntityPersister entityPersister;
 			if ( optionalObject != null ) {
 				entityPersister = session.getEntityPersister( optionalEntityName, optionalObject );
 			}
 			else {
 				entityPersister = session.getFactory().getEntityPersister( optionalEntityName );
 			}
 			if ( entityPersister.isInstance( optionalId ) &&
 					!entityPersister.getEntityMetamodel().getIdentifierProperty().isVirtual() &&
 					entityPersister.getEntityMetamodel().getIdentifierProperty().isEmbedded() ) {
 				// non-encapsulated composite identifier
 				final Serializable identifierState = ((CompositeType) entityPersister.getIdentifierType()).getPropertyValues(
 						optionalId,
 						session
 				);
 				return session.generateEntityKey( identifierState, entityPersister );
 			}
 			else {
 				return session.generateEntityKey( optionalId, entityPersister );
 			}
 		}
 		else {
 			return null;
 		}
 	}
 
 	public static Map<String, int[]> buildNamedParameterLocMap(
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext) {
 		if ( queryParameters.getNamedParameters() == null || queryParameters.getNamedParameters().isEmpty() ) {
 			return null;
 		}
 
 		final Map<String, int[]> namedParameterLocMap = new HashMap<String, int[]>();
 		for ( String name : queryParameters.getNamedParameters().keySet() ) {
 			namedParameterLocMap.put(
 					name,
 					namedParameterContext.getNamedParameterLocations( name )
 			);
 		}
 		return namedParameterLocMap;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessorImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorImpl.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorImpl.java
index 78817d733a..bdfb66087f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/ResultSetProcessorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorImpl.java
@@ -1,302 +1,302 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.internal;
+package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.process.spi.ScrollableResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.process.spi.RowReader;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.process.spi.ScrollableResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.process.spi.RowReader;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessorImpl implements ResultSetProcessor {
 	private static final Logger LOG = Logger.getLogger( ResultSetProcessorImpl.class );
 
 	private final LoadPlan loadPlan;
 	private final RowReader rowReader;
 
 	private final boolean hadSubselectFetches;
 
 	public ResultSetProcessorImpl(LoadPlan loadPlan, RowReader rowReader, boolean hadSubselectFetches) {
 		this.loadPlan = loadPlan;
 		this.rowReader = rowReader;
 		this.hadSubselectFetches = hadSubselectFetches;
 	}
 
 	public RowReader getRowReader() {
 		return rowReader;
 	}
 
 	@Override
 	public ScrollableResultSetProcessor toOnDemandForm() {
 		// todo : implement
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public List extractResults(
 			ResultSet resultSet,
 			final SessionImplementor session,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
 			boolean returnProxies,
 			boolean readOnly,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActionList) throws SQLException {
 
 		handlePotentiallyEmptyCollectionRootReturns( loadPlan, queryParameters.getCollectionKeys(), resultSet, session );
 
 		final int maxRows;
 		final RowSelection selection = queryParameters.getRowSelection();
 		if ( LimitHelper.hasMaxRows( selection ) ) {
 			maxRows = selection.getMaxRows();
 			LOG.tracef( "Limiting ResultSet processing to just %s rows", maxRows );
 		}
 		else {
 			maxRows = Integer.MAX_VALUE;
 		}
 
 		// There are times when the "optional entity information" on QueryParameters should be used and
 		// times when they should be ignored.  Loader uses its isSingleRowLoader method to allow
 		// subclasses to override that.  Collection initializers, batch loaders, e.g. override that
 		// it to be false.  The 'shouldUseOptionalEntityInstance' setting is meant to fill that same role.
 		final boolean shouldUseOptionalEntityInstance = true;
 
 		// Handles the "FETCH ALL PROPERTIES" directive in HQL
 		final boolean forceFetchLazyAttributes = false;
 
 		final ResultSetProcessingContextImpl context = new ResultSetProcessingContextImpl(
 				resultSet,
 				session,
 				loadPlan,
 				readOnly,
 				shouldUseOptionalEntityInstance,
 				forceFetchLazyAttributes,
 				returnProxies,
 				queryParameters,
 				namedParameterContext,
 				hadSubselectFetches
 		);
 
 		final List loadResults = new ArrayList();
 
 		LOG.trace( "Processing result set" );
 		int count;
 		for ( count = 0; count < maxRows && resultSet.next(); count++ ) {
 			LOG.debugf( "Starting ResultSet row #%s", count );
 
 			Object logicalRow = rowReader.readRow( resultSet, context );
 
 			// todo : apply transformers here?
 
 			loadResults.add( logicalRow );
 
 			context.finishUpRow();
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		rowReader.finishUp( context, afterLoadActionList );
 		context.wrapUp();
 
 		session.getPersistenceContext().initializeNonLazyCollections();
 
 		return loadResults;
 	}
 
 
 	private void handlePotentiallyEmptyCollectionRootReturns(
 			LoadPlan loadPlan,
 			Serializable[] collectionKeys,
 			ResultSet resultSet,
 			SessionImplementor session) {
 		if ( collectionKeys == null ) {
 			// this is not a collection initializer (and empty collections will be detected by looking for
 			// the owner's identifier in the result set)
 			return;
 		}
 
 		// this is a collection initializer, so we must create a collection
 		// for each of the passed-in keys, to account for the possibility
 		// that the collection is empty and has no rows in the result set
 		//
 		// todo : move this inside CollectionReturn ?
 		CollectionPersister persister = ( (CollectionReturn) loadPlan.getReturns().get( 0 ) ).getCollectionPersister();
 		for ( Serializable key : collectionKeys ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"Preparing collection intializer : %s",
 							MessageHelper.collectionInfoString( persister, key, session.getFactory() )
 				);
 			}
 			session.getPersistenceContext()
 					.getLoadContexts()
 					.getCollectionLoadContext( resultSet )
 					.getLoadingCollection( persister, key );
 		}
 	}
 
 
 //	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
 //		private boolean hadSubselectFetches = false;
 //
 //		@Override
 //		public void startingEntityFetch(EntityFetch entityFetch) {
 //		// only collections are currently supported for subselect fetching.
 //		//			hadSubselectFetches = hadSubselectFetches
 //		//					|| entityFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
 //		}
 //
 //		@Override
 //		public void startingCollectionFetch(CollectionFetch collectionFetch) {
 //			hadSubselectFetches = hadSubselectFetches
 //					|| collectionFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
 //		}
 //	}
 //
 //	private class MixedReturnRowReader extends AbstractRowReader implements RowReader {
 //		private final List<ReturnReader> returnReaders;
 //		private List<EntityReferenceReader> entityReferenceReaders = new ArrayList<EntityReferenceReader>();
 //		private List<CollectionReferenceReader> collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
 //
 //		private final int numberOfReturns;
 //
 //		public MixedReturnRowReader(LoadPlan loadPlan) {
 //			LoadPlanVisitor.visit(
 //					loadPlan,
 //					new LoadPlanVisitationStrategyAdapter() {
 //						@Override
 //						public void startingEntityFetch(EntityFetch entityFetch) {
 //							entityReferenceReaders.add( new EntityReferenceReader( entityFetch ) );
 //						}
 //
 //						@Override
 //						public void startingCollectionFetch(CollectionFetch collectionFetch) {
 //							collectionReferenceReaders.add( new CollectionReferenceReader( collectionFetch ) );
 //						}
 //					}
 //			);
 //
 //			final List<ReturnReader> readers = new ArrayList<ReturnReader>();
 //
 //			for ( Return rtn : loadPlan.getReturns() ) {
 //				final ReturnReader returnReader = buildReturnReader( rtn );
 //				if ( EntityReferenceReader.class.isInstance( returnReader ) ) {
 //					entityReferenceReaders.add( (EntityReferenceReader) returnReader );
 //				}
 //				readers.add( returnReader );
 //			}
 //
 //			this.returnReaders = readers;
 //			this.numberOfReturns = readers.size();
 //		}
 //
 //		@Override
 //		protected List<EntityReferenceReader> getEntityReferenceReaders() {
 //			return entityReferenceReaders;
 //		}
 //
 //		@Override
 //		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
 //			return collectionReferenceReaders;
 //		}
 //
 //		@Override
 //		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 //			Object[] logicalRow = new Object[ numberOfReturns ];
 //			int pos = 0;
 //			for ( ReturnReader reader : returnReaders ) {
 //				logicalRow[pos] = reader.read( resultSet, context );
 //				pos++;
 //			}
 //			return logicalRow;
 //		}
 //	}
 //
 //	private class CollectionInitializerRowReader extends AbstractRowReader implements RowReader {
 //		private final CollectionReturnReader returnReader;
 //
 //		private List<EntityReferenceReader> entityReferenceReaders = null;
 //		private final List<CollectionReferenceReader> collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
 //
 //		public CollectionInitializerRowReader(LoadPlan loadPlan) {
 //			returnReader = (CollectionReturnReader) buildReturnReader( loadPlan.getReturns().get( 0 ) );
 //
 //			LoadPlanVisitor.visit(
 //					loadPlan,
 //					new LoadPlanVisitationStrategyAdapter() {
 //						@Override
 //						public void startingEntityFetch(EntityFetch entityFetch) {
 //							if ( entityReferenceReaders == null ) {
 //								entityReferenceReaders = new ArrayList<EntityReferenceReader>();
 //							}
 //							entityReferenceReaders.add( new EntityReferenceReader( entityFetch ) );
 //						}
 //
 //						@Override
 //						public void startingCollectionFetch(CollectionFetch collectionFetch) {
 //							collectionReferenceReaders.add( new CollectionReferenceReader( collectionFetch ) );
 //						}
 //					}
 //			);
 //
 //			collectionReferenceReaders.add( returnReader );
 //		}
 //
 //		@Override
 //		protected List<EntityReferenceReader> getEntityReferenceReaders() {
 //			return entityReferenceReaders;
 //		}
 //
 //		@Override
 //		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
 //			return collectionReferenceReaders;
 //		}
 //
 //		@Override
 //		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 //			return returnReader.read( resultSet, context );
 //		}
 //	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/package-info.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/package-info.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/package-info.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/package-info.java
index b111193d88..28558dde7d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/package-info.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/package-info.java
@@ -1,28 +1,28 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 
 /**
  * Defines support for processing ResultSet values as defined by a LoadPlan
  */
-package org.hibernate.loader.plan2.exec.process;
+package org.hibernate.loader.plan.exec.process;
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/AbstractRowReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/AbstractRowReader.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/AbstractRowReader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/AbstractRowReader.java
index 9041f17ccb..09ea084196 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/AbstractRowReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/AbstractRowReader.java
@@ -1,285 +1,285 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.loader.plan2.exec.process.internal.HydratedEntityRegistration;
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessingContextImpl;
-import org.hibernate.loader.plan2.spi.BidirectionalEntityReference;
-import org.hibernate.loader.plan2.spi.CompositeFetch;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityIdentifierDescription;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.exec.process.internal.HydratedEntityRegistration;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
+import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.entity.Loadable;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractRowReader implements RowReader {
 	private static final Logger log = CoreLogging.logger( AbstractRowReader.class );
 
 	private final List<EntityReferenceInitializer> entityReferenceInitializers;
 	private final List<CollectionReferenceInitializer> arrayReferenceInitializers;
 	private final List<CollectionReferenceInitializer> collectionReferenceInitializers;
 
 	public AbstractRowReader(ReaderCollector readerCollector) {
 		this.entityReferenceInitializers = readerCollector.getEntityReferenceInitializers() != null
 				? new ArrayList<EntityReferenceInitializer>( readerCollector.getEntityReferenceInitializers() )
 				: Collections.<EntityReferenceInitializer>emptyList();
 		this.arrayReferenceInitializers = readerCollector.getArrayReferenceInitializers() != null
 				? new ArrayList<CollectionReferenceInitializer>( readerCollector.getArrayReferenceInitializers() )
 				: Collections.<CollectionReferenceInitializer>emptyList();
 		this.collectionReferenceInitializers = readerCollector.getNonArrayCollectionReferenceInitializers() != null
 				? new ArrayList<CollectionReferenceInitializer>( readerCollector.getNonArrayCollectionReferenceInitializers() )
 				: Collections.<CollectionReferenceInitializer>emptyList();
 	}
 
 	protected abstract Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context)
 			throws SQLException;
 
 	@Override
 	public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 
 		final boolean hasEntityReferenceInitializers = CollectionHelper.isNotEmpty( entityReferenceInitializers );
 
 		if ( hasEntityReferenceInitializers ) {
 			// 	1) allow entity references to resolve identifiers (in 2 steps)
 			for ( EntityReferenceInitializer entityReferenceInitializer : entityReferenceInitializers ) {
 				entityReferenceInitializer.hydrateIdentifier( resultSet, context );
 			}
 			final Map<EntityReference,EntityReferenceInitializer> initializerByEntityReference =
 					new HashMap<EntityReference, EntityReferenceInitializer>( entityReferenceInitializers.size() );
 			for ( EntityReferenceInitializer entityReferenceInitializerFromMap : entityReferenceInitializers ) {
 				initializerByEntityReference.put( entityReferenceInitializerFromMap.getEntityReference(), entityReferenceInitializerFromMap );
 			}
 			for ( EntityReferenceInitializer entityReferenceInitializer : entityReferenceInitializers ) {
 				resolveEntityKey(
 						resultSet,
 						context,
 						entityReferenceInitializer,
 						initializerByEntityReference
 				);
 			}
 
 			// 2) allow entity references to resolve their non-identifier hydrated state and entity instance
 			for ( EntityReferenceInitializer entityReferenceInitializer : entityReferenceInitializers ) {
 				entityReferenceInitializer.hydrateEntityState( resultSet, context );
 			}
 		}
 
 
 		// 3) read the logical row
 
 		Object logicalRow = readLogicalRow( resultSet, context );
 
 
 		// 4) allow arrays, entities and collections after row callbacks
 		if ( hasEntityReferenceInitializers ) {
 			for ( EntityReferenceInitializer entityReferenceInitializer : entityReferenceInitializers ) {
 				entityReferenceInitializer.finishUpRow( resultSet, context );
 			}
 		}
 		if ( collectionReferenceInitializers != null ) {
 			for ( CollectionReferenceInitializer collectionReferenceInitializer : collectionReferenceInitializers ) {
 				collectionReferenceInitializer.finishUpRow( resultSet, context );
 			}
 		}
 		if ( arrayReferenceInitializers != null ) {
 			for ( CollectionReferenceInitializer arrayReferenceInitializer : arrayReferenceInitializers ) {
 				arrayReferenceInitializer.finishUpRow( resultSet, context );
 			}
 		}
 
 		return logicalRow;
 	}
 
 	private void resolveEntityKey(
 			ResultSet resultSet,
 			ResultSetProcessingContextImpl context,
 			EntityReferenceInitializer entityReferenceInitializer,
 			Map<EntityReference,EntityReferenceInitializer> initializerByEntityReference) throws SQLException {
 		final EntityReference entityReference = entityReferenceInitializer.getEntityReference();
 		final EntityIdentifierDescription identifierDescription = entityReference.getIdentifierDescription();
 
 		if ( identifierDescription.hasFetches() || identifierDescription.hasBidirectionalEntityReferences() ) {
 			resolveEntityKey( resultSet, context, (FetchSource) identifierDescription, initializerByEntityReference );
 		}
 		entityReferenceInitializer.resolveEntityKey( resultSet, context );
 	}
 
 	private void resolveEntityKey(
 			ResultSet resultSet,
 			ResultSetProcessingContextImpl context,
 			FetchSource fetchSource,
 			Map<EntityReference,EntityReferenceInitializer> initializerByEntityReference) throws SQLException {
 		// Resolve any bidirectional entity references first.
 		for ( BidirectionalEntityReference bidirectionalEntityReference : fetchSource.getBidirectionalEntityReferences() ) {
 			final EntityReferenceInitializer targetEntityReferenceInitializer = initializerByEntityReference.get(
 					bidirectionalEntityReference.getTargetEntityReference()
 			);
 			resolveEntityKey(
 					resultSet,
 					context,
 					targetEntityReferenceInitializer,
 					initializerByEntityReference
 			);
 			targetEntityReferenceInitializer.hydrateEntityState( resultSet, context );
 		}
 		for ( Fetch fetch : fetchSource.getFetches() ) {
 			if ( EntityFetch.class.isInstance( fetch ) ) {
 				final EntityFetch entityFetch = (EntityFetch) fetch;
 				final EntityReferenceInitializer  entityReferenceInitializer = initializerByEntityReference.get( entityFetch );
 				if ( entityReferenceInitializer != null ) {
 					resolveEntityKey(
 							resultSet,
 							context,
 							entityReferenceInitializer,
 							initializerByEntityReference
 					);
 					entityReferenceInitializer.hydrateEntityState( resultSet, context );
 				}
 			}
 			else if ( CompositeFetch.class.isInstance( fetch ) ) {
 				resolveEntityKey(
 						resultSet,
 						context,
 						(CompositeFetch) fetch,
 						initializerByEntityReference );
 			}
 		}
 	}
 
 	@Override
 	public void finishUp(ResultSetProcessingContextImpl context, List<AfterLoadAction> afterLoadActionList) {
 		final List<HydratedEntityRegistration> hydratedEntityRegistrations = context.getHydratedEntityRegistrationList();
 
 		// for arrays, we should end the collection load before resolving the entities, since the
 		// actual array instances are not instantiated during loading
 		finishLoadingArrays( context );
 
 
 		// IMPORTANT: reuse the same event instances for performance!
 		final PreLoadEvent preLoadEvent;
 		final PostLoadEvent postLoadEvent;
 		if ( context.getSession().isEventSource() ) {
 			preLoadEvent = new PreLoadEvent( (EventSource) context.getSession() );
 			postLoadEvent = new PostLoadEvent( (EventSource) context.getSession() );
 		}
 		else {
 			preLoadEvent = null;
 			postLoadEvent = null;
 		}
 
 		// now finish loading the entities (2-phase load)
 		performTwoPhaseLoad( preLoadEvent, context, hydratedEntityRegistrations );
 
 		// now we can finalize loading collections
 		finishLoadingCollections( context );
 
 		// finally, perform post-load operations
 		postLoad( postLoadEvent, context, hydratedEntityRegistrations, afterLoadActionList );
 	}
 
 	private void finishLoadingArrays(ResultSetProcessingContextImpl context) {
 		for ( CollectionReferenceInitializer arrayReferenceInitializer : arrayReferenceInitializers ) {
 			arrayReferenceInitializer.endLoading( context );
 		}
 	}
 
 	private void performTwoPhaseLoad(
 			PreLoadEvent preLoadEvent,
 			ResultSetProcessingContextImpl context,
 			List<HydratedEntityRegistration> hydratedEntityRegistrations) {
 		final int numberOfHydratedObjects = hydratedEntityRegistrations == null
 				? 0
 				: hydratedEntityRegistrations.size();
 		log.tracev( "Total objects hydrated: {0}", numberOfHydratedObjects );
 
 		if ( hydratedEntityRegistrations == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrations ) {
 			TwoPhaseLoad.initializeEntity(
 					registration.getInstance(),
 					context.isReadOnly(),
 					context.getSession(),
 					preLoadEvent
 			);
 		}
 	}
 
 	private void finishLoadingCollections(ResultSetProcessingContextImpl context) {
 		for ( CollectionReferenceInitializer collectionReferenceInitializer : collectionReferenceInitializers ) {
 			collectionReferenceInitializer.endLoading( context );
 		}
 	}
 
 	private void postLoad(
 			PostLoadEvent postLoadEvent,
 			ResultSetProcessingContextImpl context,
 			List<HydratedEntityRegistration> hydratedEntityRegistrations,
 			List<AfterLoadAction> afterLoadActionList) {
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedEntityRegistrations == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrations ) {
 			TwoPhaseLoad.postLoad( registration.getInstance(), context.getSession(), postLoadEvent );
 			if ( afterLoadActionList != null ) {
 				for ( AfterLoadAction afterLoadAction : afterLoadActionList ) {
 					afterLoadAction.afterLoad(
 							context.getSession(),
 							registration.getInstance(),
 							(Loadable) registration.getEntityReference().getEntityPersister()
 					);
 				}
 			}
 		}
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/CollectionReferenceInitializer.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/CollectionReferenceInitializer.java
similarity index 87%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/CollectionReferenceInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/CollectionReferenceInitializer.java
index 6839d42e2c..a67ba9239b 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/CollectionReferenceInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/CollectionReferenceInitializer.java
@@ -1,41 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessingContextImpl;
-import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.spi.CollectionReference;
 
 /**
  * @author Steve Ebersole
  */
 public interface CollectionReferenceInitializer {
 	// again, not sure.  ResultSetProcessingContextImpl.initializeEntitiesAndCollections() stuff?
 	void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context);
 
 	CollectionReference getCollectionReference();
 
 	void endLoading(ResultSetProcessingContextImpl context);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/EntityReferenceInitializer.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/EntityReferenceInitializer.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/EntityReferenceInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/EntityReferenceInitializer.java
index 5944eba49b..a1c97b78bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/EntityReferenceInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/EntityReferenceInitializer.java
@@ -1,45 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessingContextImpl;
-import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.spi.EntityReference;
 
 /**
  * @author Steve Ebersole
  */
 public interface EntityReferenceInitializer {
 	EntityReference getEntityReference();
 
 	void hydrateIdentifier(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException;
 
 	void resolveEntityKey(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException;
 
 	void hydrateEntityState(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException;
 
 	void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ReaderCollector.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReaderCollector.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ReaderCollector.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReaderCollector.java
index d9201f4182..05c8df7b46 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ReaderCollector.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReaderCollector.java
@@ -1,45 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.util.List;
 
 /**
  * Used as a callback mechanism while building the SQL statement to collect the needed ResultSet initializers.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public interface ReaderCollector {
 	public ReturnReader getReturnReader();
 
 	public void add(CollectionReferenceInitializer collectionReferenceInitializer);
 	public List<CollectionReferenceInitializer> getArrayReferenceInitializers();
 	public List<CollectionReferenceInitializer> getNonArrayCollectionReferenceInitializers();
 
 	public void add(EntityReferenceInitializer entityReferenceInitializer);
 	public List<EntityReferenceInitializer> getEntityReferenceInitializers();
 
 	public RowReader buildRowReader();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ResultSetProcessingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessingContext.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ResultSetProcessingContext.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessingContext.java
index 4c8d67564a..8bdfa66f8e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ResultSetProcessingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessingContext.java
@@ -1,162 +1,162 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.loader.plan2.exec.spi.LockModeResolver;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.spi.LockModeResolver;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Steve Ebersole
  */
 public interface ResultSetProcessingContext extends LockModeResolver {
 	public SessionImplementor getSession();
 
 	public QueryParameters getQueryParameters();
 
 	public boolean shouldUseOptionalEntityInformation();
 
 	public boolean shouldReturnProxies();
 
 	public LoadPlan getLoadPlan();
 
 	/**
 	 * Holds all pieces of information known about an entity reference in relation to each row as we process the
 	 * result set.  Caches these values and makes it easy for access while processing Fetches.
 	 */
 	public static interface EntityReferenceProcessingState {
 		/**
 		 * The EntityReference for which this is collecting process state
 		 *
 		 * @return The EntityReference
 		 */
 		public EntityReference getEntityReference();
 
 		/**
 		 * Register the fact that no identifier was found on attempt to hydrate it from ResultSet
 		 */
 		public void registerMissingIdentifier();
 
 		/**
 		 *
 		 * @return
 		 */
 		public boolean isMissingIdentifier();
 
 		/**
 		 * Register the hydrated form (raw Type-read ResultSet values) of the entity's identifier for the row
 		 * currently being processed.
 		 *
 		 * @param hydratedForm The entity identifier hydrated state
 		 */
 		public void registerIdentifierHydratedForm(Object hydratedForm);
 
 		/**
 		 * Obtain the hydrated form (the raw Type-read ResultSet values) of the entity's identifier
 		 *
 		 * @return The entity identifier hydrated state
 		 */
 		public Object getIdentifierHydratedForm();
 
 		/**
 		 * Register the processed EntityKey for this Entity for the row currently being processed.
 		 *
 		 * @param entityKey The processed EntityKey for this EntityReference
 		 */
 		public void registerEntityKey(EntityKey entityKey);
 
 		/**
 		 * Obtain the registered EntityKey for this EntityReference for the row currently being processed.
 		 *
 		 * @return The registered EntityKey for this EntityReference
 		 */
 		public EntityKey getEntityKey();
 
 		public void registerHydratedState(Object[] hydratedState);
 		public Object[] getHydratedState();
 
 		// usually uninitialized at this point
 		public void registerEntityInstance(Object instance);
 
 		// may be uninitialized
 		public Object getEntityInstance();
 
 	}
 
 	public EntityReferenceProcessingState getProcessingState(EntityReference entityReference);
 
 	/**
 	 * Find the EntityReferenceProcessingState for the FetchOwner of the given Fetch.
 	 *
 	 * @param fetch The Fetch for which to find the EntityReferenceProcessingState of its FetchOwner.
 	 *
 	 * @return The FetchOwner's EntityReferenceProcessingState
 	 */
 	public EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch);
 
 
 	public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance);
 
 	public static interface EntityKeyResolutionContext {
 		public EntityPersister getEntityPersister();
 		public LockMode getLockMode();
 		public EntityReference getEntityReference();
 	}
 
 //	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext);
 
 
 	// should be able to get rid of the methods below here from the interface ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 //	public void checkVersion(
 //			ResultSet resultSet,
 //			EntityPersister persister,
 //			EntityAliases entityAliases,
 //			EntityKey entityKey,
 //			Object entityInstance) throws SQLException;
 //
 //	public String getConcreteEntityTypeName(
 //			ResultSet resultSet,
 //			EntityPersister persister,
 //			EntityAliases entityAliases,
 //			EntityKey entityKey) throws SQLException;
 //
 //	public void loadFromResultSet(
 //			ResultSet resultSet,
 //			Object entityInstance,
 //			String concreteEntityTypeName,
 //			EntityKey entityKey,
 //			EntityAliases entityAliases,
 //			LockMode acquiredLockMode,
 //			EntityPersister persister,
 //			FetchStrategy fetchStrategy,
 //			boolean eagerFetch,
 //			EntityType associationType) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ResultSetProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessor.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ResultSetProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessor.java
index 339ef9b5be..e84d9eabf3 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ResultSetProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessor.java
@@ -1,80 +1,80 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.List;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * Contract for processing JDBC ResultSets.  Separated because ResultSets can be chained and we'd really like to
  * reuse this logic across all result sets.
  * <p/>
  * todo : investigate having this work with non-JDBC results; maybe just typed as Object? or a special Result contract?
  *
  * @author Steve Ebersole
  */
 public interface ResultSetProcessor {
 
 	/**
 	 * Make this go somewhere else.  These aren't really linked this way anymore.  ScrollableResultSetProcessor is
 	 * not tied in yet, so not sure yet exactly how that will play out.
 	 *
 	 * @deprecated Going away!
 	 */
 	@Deprecated
 	public ScrollableResultSetProcessor toOnDemandForm();
 
 	/**
 	 * Process an entire ResultSet, performing all extractions.
 	 *
 	 * Semi-copy of {@link org.hibernate.loader.Loader#doQuery}, with focus on just the ResultSet processing bit.
 	 *
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 * @param returnProxies Can proxies be returned (not the same as can they be created!)
 	 * @param forcedResultTransformer My old "friend" ResultTransformer...
 	 * @param afterLoadActions Actions to be performed after loading an entity.
 	 *
 	 * @return The extracted results list.
 	 *
 	 * @throws java.sql.SQLException Indicates a problem access the JDBC ResultSet
 	 */
 	public List extractResults(
 			ResultSet resultSet,
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
 			boolean returnProxies,
 			boolean readOnly,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActions) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ReturnReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReturnReader.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ReturnReader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReturnReader.java
index 54ad80815b..1af5685130 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ReturnReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReturnReader.java
@@ -1,46 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
  * Handles reading a single root Return object
  *
  * @author Steve Ebersole
  */
 public interface ReturnReader {
 	/**
 	 * Essentially performs the second phase of two-phase loading.
 	 *
 	 * @param resultSet The result set being processed
 	 * @param context The context for the processing
 	 *
 	 * @return The read object
 	 *
 	 * @throws java.sql.SQLException Indicates a problem access the JDBC result set
 	 */
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/RowReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/RowReader.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/RowReader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/RowReader.java
index b53ffd014d..3e11292360 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/RowReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/RowReader.java
@@ -1,41 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.List;
 
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
 import org.hibernate.loader.spi.AfterLoadAction;
 
 /**
  * @author Steve Ebersole
  */
 public interface RowReader {
 	// why the context *impl*?
 	Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException;
 
 	void finishUp(ResultSetProcessingContextImpl context, List<AfterLoadAction> afterLoadActionList);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ScrollableResultSetProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ScrollableResultSetProcessor.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ScrollableResultSetProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ScrollableResultSetProcessor.java
index 3a2a00e5b6..e35a749bf8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/spi/ScrollableResultSetProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ScrollableResultSetProcessor.java
@@ -1,107 +1,107 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.process.spi;
+package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Contract for processing JDBC ResultSets a single logical row at a time.  These are intended for use by
  * {@link org.hibernate.ScrollableResults} implementations.
  *
  * NOTE : these methods initially taken directly from {@link org.hibernate.loader.Loader} counterparts in an effort
  * to break Loader into manageable pieces, especially in regards to the processing of result sets.
  *
  * @author Steve Ebersole
  */
 public interface ScrollableResultSetProcessor {
 
 	/**
 	 * Give a ResultSet, extract just a single result row.
 	 *
 	 * Copy of {@link org.hibernate.loader.Loader#loadSingleRow(java.sql.ResultSet, org.hibernate.engine.spi.SessionImplementor, org.hibernate.engine.spi.QueryParameters, boolean)}
 	 * but dropping the 'returnProxies' (that method has only one use in the entire codebase and it always passes in
 	 * false...)
 	 *
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 *
 	 * @return The extracted result row
 	 *
 	 * @throws org.hibernate.HibernateException Indicates a problem extracting values from the result set.
 	 */
 	public Object extractSingleRow(
 			ResultSet resultSet,
 			SessionImplementor session,
 			QueryParameters queryParameters);
 
 	/**
 	 * Given a scrollable ResultSet, extract a logical row.  The assumption here is that the ResultSet is already
 	 * properly ordered to account for any to-many fetches.  Multiple ResultSet rows are read into a single query
 	 * result "row".
 	 *
 	 * Copy of {@link org.hibernate.loader.Loader#loadSequentialRowsForward(java.sql.ResultSet, org.hibernate.engine.spi.SessionImplementor, org.hibernate.engine.spi.QueryParameters, boolean)}
 	 * but dropping the 'returnProxies' (that method has only one use in the entire codebase and it always passes in
 	 * false...)
 	 *
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 *
 	 * @return The extracted result row
 	 *
 	 * @throws org.hibernate.HibernateException Indicates a problem extracting values from the result set.
 	 */
 	public Object extractLogicalRowForward(
 			final ResultSet resultSet,
 			final SessionImplementor session,
 			final QueryParameters queryParameters);
 
 	/**
 	 * Like {@link #extractLogicalRowForward} but here moving through the ResultSet in reverse.
 	 *
 	 * Copy of {@link org.hibernate.loader.Loader#loadSequentialRowsReverse(java.sql.ResultSet, org.hibernate.engine.spi.SessionImplementor, org.hibernate.engine.spi.QueryParameters, boolean, boolean)}
 	 * but dropping the 'returnProxies' (that method has only one use in the entire codebase and it always passes in
 	 * false...).
 	 *
 	 * todo : is 'logicallyAfterLastRow really needed?  Can't that be deduced?  In fact pretty positive it is not needed.
 	 *
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 * @param isLogicallyAfterLast Is the result set currently positioned after the last row; again, is this really needed?  How is it any diff
 	 *
 	 * @return The extracted result row
 	 *
 	 * @throws org.hibernate.HibernateException Indicates a problem extracting values from the result set.
 	 */
 	public Object extractLogicalRowReverse(
 			ResultSet resultSet,
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			boolean isLogicallyAfterLast);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/internal/SelectStatementBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/SelectStatementBuilder.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/internal/SelectStatementBuilder.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/SelectStatementBuilder.java
index 50d088a03c..de3c5aa8dc 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/internal/SelectStatementBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/SelectStatementBuilder.java
@@ -1,232 +1,232 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.query.internal;
+package org.hibernate.loader.plan.exec.query.internal;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.SelectFragment;
 
 /**
  * Largely a copy of the {@link org.hibernate.sql.Select} class, but changed up slightly to better meet needs
  * of building a SQL SELECT statement from a LoadPlan
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class SelectStatementBuilder {
 	public final Dialect dialect;
 
 	private StringBuilder selectClause = new StringBuilder();
 	private StringBuilder fromClause = new StringBuilder();
 //	private StringBuilder outerJoinsAfterFrom;
 	private String outerJoinsAfterFrom;
 	private StringBuilder whereClause;
 //	private StringBuilder outerJoinsAfterWhere;
 	private String outerJoinsAfterWhere;
 	private StringBuilder orderByClause;
 	private String comment;
 	private LockOptions lockOptions = new LockOptions();
 
 	private int guesstimatedBufferSize = 20;
 
 	public SelectStatementBuilder(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	/**
 	 * Appends a select clause fragment
 	 *
 	 * @param selection The selection fragment
 	 */
 	public void appendSelectClauseFragment(String selection) {
 		if ( this.selectClause.length() > 0 ) {
 			this.selectClause.append( ", " );
 			this.guesstimatedBufferSize += 2;
 		}
 		this.selectClause.append( selection );
 		this.guesstimatedBufferSize += selection.length();
 	}
 
 	public void appendSelectClauseFragment(SelectFragment selectFragment) {
 		appendSelectClauseFragment( selectFragment.toFragmentString().substring( 2 ) );
 	}
 
 	public void appendFromClauseFragment(String fragment) {
 		if ( this.fromClause.length() > 0 ) {
 			this.fromClause.append( ", " );
 			this.guesstimatedBufferSize += 2;
 		}
 		this.fromClause.append( fragment );
 		this.guesstimatedBufferSize += fragment.length();
 	}
 
 	public void appendFromClauseFragment(String tableName, String alias) {
 		appendFromClauseFragment( tableName + ' ' + alias );
 	}
 
 	public void appendRestrictions(String restrictions) {
 		final String cleaned = cleanRestrictions( restrictions );
 		if ( StringHelper.isEmpty( cleaned ) ) {
 			return;
 		}
 
 		this.guesstimatedBufferSize += cleaned.length();
 
 		if ( whereClause == null ) {
 			whereClause = new StringBuilder( cleaned );
 		}
 		else {
 			whereClause.append( " and " ).append( cleaned );
 			this.guesstimatedBufferSize += 5;
 		}
 	}
 
 	private String cleanRestrictions(String restrictions) {
 		restrictions = restrictions.trim();
 		if ( restrictions.startsWith( "and" ) ) {
 			restrictions = restrictions.substring( 4 );
 		}
 		if ( restrictions.endsWith( "and" ) ) {
 			restrictions = restrictions.substring( 0, restrictions.length()-4 );
 		}
 
 		return restrictions;
 	}
 
 //	public void appendOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
 //		appendOuterJoinsAfterFrom( outerJoinsAfterFrom );
 //		appendOuterJoinsAfterWhere( outerJoinsAfterWhere );
 //	}
 //
 //	private void appendOuterJoinsAfterFrom(String outerJoinsAfterFrom) {
 //		if ( this.outerJoinsAfterFrom == null ) {
 //			this.outerJoinsAfterFrom = new StringBuilder( outerJoinsAfterFrom );
 //		}
 //		else {
 //			this.outerJoinsAfterFrom.append( ' ' ).append( outerJoinsAfterFrom );
 //		}
 //	}
 //
 //	private void appendOuterJoinsAfterWhere(String outerJoinsAfterWhere) {
 //		final String cleaned = cleanRestrictions( outerJoinsAfterWhere );
 //
 //		if ( this.outerJoinsAfterWhere == null ) {
 //			this.outerJoinsAfterWhere = new StringBuilder( cleaned );
 //		}
 //		else {
 //			this.outerJoinsAfterWhere.append( " and " ).append( cleaned );
 //			this.guesstimatedBufferSize += 5;
 //		}
 //
 //		this.guesstimatedBufferSize += cleaned.length();
 //	}
 
 	public void setOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
 		this.outerJoinsAfterFrom = outerJoinsAfterFrom;
 
 		final String cleanRestrictions = cleanRestrictions( outerJoinsAfterWhere );
 		this.outerJoinsAfterWhere = cleanRestrictions;
 
 		this.guesstimatedBufferSize += outerJoinsAfterFrom.length() + cleanRestrictions.length();
 	}
 
 	public void appendOrderByFragment(String ordering) {
 		if ( this.orderByClause == null ) {
 			this.orderByClause = new StringBuilder();
 		}
 		else {
 			this.orderByClause.append( ", " );
 			this.guesstimatedBufferSize += 2;
 		}
 		this.orderByClause.append( ordering );
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 		this.guesstimatedBufferSize += comment.length();
 	}
 
 	public void setLockMode(LockMode lockMode) {
 		this.lockOptions.setLockMode( lockMode );
 	}
 
 	public void setLockOptions(LockOptions lockOptions) {
 		LockOptions.copy( lockOptions, this.lockOptions );
 	}
 
 	/**
 	 * Construct an SQL <tt>SELECT</tt> statement from the given clauses
 	 */
 	public String toStatementString() {
 		final StringBuilder buf = new StringBuilder( guesstimatedBufferSize );
 
 		if ( StringHelper.isNotEmpty( comment ) ) {
 			buf.append( "/* " ).append( comment ).append( " */ " );
 		}
 
 		buf.append( "select " )
 				.append( selectClause )
 				.append( " from " )
 				.append( fromClause );
 
 		if ( StringHelper.isNotEmpty( outerJoinsAfterFrom ) ) {
 			buf.append( outerJoinsAfterFrom );
 		}
 
 		if ( isNotEmpty( whereClause ) || isNotEmpty( outerJoinsAfterWhere ) ) {
 			buf.append( " where " );
 			// the outerJoinsAfterWhere needs to come before where clause to properly
 			// handle dynamic filters
 			if ( StringHelper.isNotEmpty( outerJoinsAfterWhere ) ) {
 				buf.append( outerJoinsAfterWhere );
 				if ( isNotEmpty( whereClause ) ) {
 					buf.append( " and " );
 				}
 			}
 			if ( isNotEmpty( whereClause ) ) {
 				buf.append( whereClause );
 			}
 		}
 
 		if ( orderByClause != null ) {
 			buf.append( " order by " ).append( orderByClause );
 		}
 
 		if ( lockOptions.getLockMode() != LockMode.NONE ) {
 			buf.append( dialect.getForUpdateString( lockOptions ) );
 		}
 
 		return dialect.transformSelectString( buf.toString() );
 	}
 
 	private boolean isNotEmpty(String string) {
 		return StringHelper.isNotEmpty( string );
 	}
 
 	private boolean isNotEmpty(StringBuilder builder) {
 		return builder != null && builder.length() > 0;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/package-info.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/package-info.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/package-info.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/package-info.java
index 1649e887ed..b6e4e2c508 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/package-info.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/package-info.java
@@ -1,28 +1,28 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 
 /**
  * Defines support for build a query (SQL string specifically for now) based on a LoadPlan.
  */
-package org.hibernate.loader.plan2.exec.query;
+package org.hibernate.loader.plan.exec.query;
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/spi/NamedParameterContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/NamedParameterContext.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/spi/NamedParameterContext.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/NamedParameterContext.java
index 58b9bc61e1..0054a35141 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/spi/NamedParameterContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/NamedParameterContext.java
@@ -1,36 +1,36 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.query.spi;
+package org.hibernate.loader.plan.exec.query.spi;
 
 /**
  * The context for named parameters.
  * <p/>
  * NOTE : the hope with the SQL-redesign stuff is that this whole concept goes away, the idea being that
  * the parameters are encoded into the query tree and "bind themselves"; see {@link org.hibernate.param.ParameterSpecification}.
  *
  * @author Steve Ebersole
  */
 public interface NamedParameterContext {
 	public int[] getNamedParameterLocations(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/spi/QueryBuildingParameters.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/QueryBuildingParameters.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/spi/QueryBuildingParameters.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/QueryBuildingParameters.java
index c93f878a36..4ad8f88cd2 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/query/spi/QueryBuildingParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/QueryBuildingParameters.java
@@ -1,40 +1,40 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.query.spi;
+package org.hibernate.loader.plan.exec.query.spi;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 
 /**
  * @author Steve Ebersole
  */
 public interface QueryBuildingParameters {
 	public LoadQueryInfluencers getQueryInfluencers();
 	public int getBatchSize();
 
 	// ultimately it would be better to have a way to resolve the LockMode for a given Return/Fetch...
 	public LockMode getLockMode();
 	public LockOptions getLockOptions();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/AbstractLoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/AbstractLoadQueryDetails.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/AbstractLoadQueryDetails.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/AbstractLoadQueryDetails.java
index fa2e22ce9f..b089af49e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/AbstractLoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/AbstractLoadQueryDetails.java
@@ -1,292 +1,292 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.build.spi.LoadPlanTreePrinter;
-import org.hibernate.loader.plan2.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan2.exec.internal.FetchStats;
-import org.hibernate.loader.plan2.exec.internal.LoadQueryJoinAndFetchProcessor;
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan2.exec.process.spi.CollectionReferenceInitializer;
-import org.hibernate.loader.plan2.exec.process.spi.EntityReferenceInitializer;
-import org.hibernate.loader.plan2.exec.process.spi.ReaderCollector;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.internal.SelectStatementBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.FetchSource;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.QuerySpace;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.build.spi.LoadPlanTreePrinter;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.internal.FetchStats;
+import org.hibernate.loader.plan.exec.internal.LoadQueryJoinAndFetchProcessor;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
+import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
+import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
+import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.FetchSource;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.QuerySpace;
+import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.sql.ConditionFragment;
 import org.hibernate.sql.DisjunctionFragment;
 import org.hibernate.sql.InFragment;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractLoadQueryDetails implements LoadQueryDetails {
 
 	private final LoadPlan loadPlan;
 	private final String[] keyColumnNames;
 	private final Return rootReturn;
 	private final LoadQueryJoinAndFetchProcessor queryProcessor;
 	private String sqlStatement;
 	private ResultSetProcessor resultSetProcessor;
 
 	/**
 	 * @param rootReturn The root return reference we are processing
 	 * @param select The SelectStatementBuilder
 	 * @param helper The Join/Fetch helper
 	 * @param factory The SessionFactory
 	 * @param buildingParameters The query building context
 	 * @param rootAlias The table alias to use
 	 * @param rootLoadable The persister
 	 * @param readerCollector Collector for EntityReferenceInitializer and CollectionReferenceInitializer references
 	*/
 	protected AbstractLoadQueryDetails(
 			LoadPlan loadPlan,
 			AliasResolutionContextImpl aliasResolutionContext,
 			QueryBuildingParameters buildingParameters,
 			String[] keyColumnNames,
 			Return rootReturn,
 			SessionFactoryImplementor factory) {
 		this.keyColumnNames = keyColumnNames;
 		this.rootReturn = rootReturn;
 		this.loadPlan = loadPlan;
 		this.queryProcessor = new LoadQueryJoinAndFetchProcessor( aliasResolutionContext, buildingParameters, factory );
 	}
 
 	protected QuerySpace getQuerySpace(String querySpaceUid) {
 		return loadPlan.getQuerySpaces().getQuerySpaceByUid( querySpaceUid );
 	}
 
 	@Override
 	public String getSqlStatement() {
 		return sqlStatement;
 	}
 
 	@Override
 	public ResultSetProcessor getResultSetProcessor() {
 		return resultSetProcessor;
 	}
 
 	protected final Return getRootReturn() {
 		return rootReturn;
 	}
 
 	protected final AliasResolutionContext getAliasResolutionContext() {
 		return queryProcessor.getAliasResolutionContext();
 	}
 
 	protected final QueryBuildingParameters getQueryBuildingParameters() {
 		return queryProcessor.getQueryBuildingParameters();
 	}
 
 	protected final SessionFactoryImplementor getSessionFactory() {
 		return queryProcessor.getSessionFactory();
 	}
 	/**
 	 * Main entry point for properly handling the FROM clause and and joins and restrictions
 	 *
 	 */
 	protected void generate() {
 		// There are 2 high-level requirements to perform here:
 		// 	1) Determine the SQL required to carry out the given LoadPlan (and fulfill
 		// 		{@code LoadQueryDetails#getSqlStatement()}).  SelectStatementBuilder collects the ongoing efforts to
 		//		build the needed SQL.
 		// 	2) Determine how to read information out of the ResultSet resulting from executing the indicated SQL
 		//		(the SQL aliases).  ReaderCollector and friends are where this work happens, ultimately
 		//		producing a ResultSetProcessor
 
 		final SelectStatementBuilder select = new SelectStatementBuilder( queryProcessor.getSessionFactory().getDialect() );
 
 		// LoadPlan is broken down into 2 high-level pieces that we need to process here.
 		//
 		// First is the QuerySpaces, which roughly equates to the SQL FROM-clause.  We'll cycle through
 		// those first, generating aliases into the AliasContext in addition to writing SQL FROM-clause information
 		// into SelectStatementBuilder.  The AliasContext is populated here and the reused while process the SQL
 		// SELECT-clause into the SelectStatementBuilder and then again also to build the ResultSetProcessor
 
 		applyRootReturnTableFragments( select );
 
 		if ( shouldApplyRootReturnFilterBeforeKeyRestriction() ) {
 			applyRootReturnFilterRestrictions( select );
 			// add restrictions...
 			// first, the load key restrictions (which entity(s)/collection(s) do we want to load?)
 			applyKeyRestriction(
 					select,
 					getRootTableAlias(),
 					keyColumnNames,
 					getQueryBuildingParameters().getBatchSize()
 			);
 		}
 		else {
 			// add restrictions...
 			// first, the load key restrictions (which entity(s)/collection(s) do we want to load?)
 			applyKeyRestriction(
 					select,
 					getRootTableAlias(),
 					keyColumnNames,
 					getQueryBuildingParameters().getBatchSize()
 			);
 			applyRootReturnFilterRestrictions( select );
 		}
 
 
 		applyRootReturnWhereJoinRestrictions( select );
 
 		applyRootReturnOrderByFragments( select );
 		// then move on to joins...
 
 		applyRootReturnSelectFragments( select );
 
 		queryProcessor.processQuerySpaceJoins( getRootQuerySpace(), select );
 
 		// Next, we process the Returns and Fetches building the SELECT clause and at the same time building
 		// Readers for reading the described results out of a SQL ResultSet
 
 		FetchStats fetchStats = null;
 		if ( FetchSource.class.isInstance( rootReturn ) ) {
 			fetchStats = queryProcessor.processFetches(
 					(FetchSource) rootReturn,
 					select,
 					getReaderCollector()
 			);
 		}
 		else if ( CollectionReturn.class.isInstance( rootReturn ) ) {
 			final CollectionReturn collectionReturn = (CollectionReturn) rootReturn;
 			if ( collectionReturn.getElementGraph() != null ) {
 				fetchStats = queryProcessor.processFetches(
 						collectionReturn.getElementGraph(),
 						select,
 						getReaderCollector()
 				);
 			}
 			// TODO: what about index???
 		}
 
 		LoadPlanTreePrinter.INSTANCE.logTree( loadPlan, queryProcessor.getAliasResolutionContext() );
 
 		this.sqlStatement = select.toStatementString();
 		this.resultSetProcessor = new ResultSetProcessorImpl(
 				loadPlan,
 				getReaderCollector().buildRowReader(),
 				fetchStats != null && fetchStats.hasSubselectFetches()
 		);
 	}
 
 	protected abstract ReaderCollector getReaderCollector();
 	protected abstract QuerySpace getRootQuerySpace();
 	protected abstract String getRootTableAlias();
 	protected abstract boolean shouldApplyRootReturnFilterBeforeKeyRestriction();
 	protected abstract void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder );
 	protected abstract void applyRootReturnTableFragments(SelectStatementBuilder selectStatementBuilder);
 	protected abstract void applyRootReturnFilterRestrictions(SelectStatementBuilder selectStatementBuilder);
 	protected abstract void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder);
 	protected abstract void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder);
 
 
 		private static void applyKeyRestriction(SelectStatementBuilder select, String alias, String[] keyColumnNames, int batchSize) {
 		if ( keyColumnNames.length==1 ) {
 			// NOT A COMPOSITE KEY
 			// 		for batching, use "foo in (?, ?, ?)" for batching
 			//		for no batching, use "foo = ?"
 			// (that distinction is handled inside InFragment)
 			final InFragment in = new InFragment().setColumn( alias, keyColumnNames[0] );
 			for ( int i = 0; i < batchSize; i++ ) {
 				in.addValue( "?" );
 			}
 			select.appendRestrictions( in.toFragmentString() );
 		}
 		else {
 			// A COMPOSITE KEY...
 			final ConditionFragment keyRestrictionBuilder = new ConditionFragment()
 					.setTableAlias( alias )
 					.setCondition( keyColumnNames, "?" );
 			final String keyRestrictionFragment = keyRestrictionBuilder.toFragmentString();
 
 			StringBuilder restrictions = new StringBuilder();
 			if ( batchSize==1 ) {
 				// for no batching, use "foo = ? and bar = ?"
 				restrictions.append( keyRestrictionFragment );
 			}
 			else {
 				// for batching, use "( (foo = ? and bar = ?) or (foo = ? and bar = ?) )"
 				restrictions.append( '(' );
 				DisjunctionFragment df = new DisjunctionFragment();
 				for ( int i=0; i<batchSize; i++ ) {
 					df.addCondition( keyRestrictionFragment );
 				}
 				restrictions.append( df.toFragmentString() );
 				restrictions.append( ')' );
 			}
 			select.appendRestrictions( restrictions.toString() );
 		}
 	}
 
 	protected abstract static class ReaderCollectorImpl implements ReaderCollector {
 		private final List<EntityReferenceInitializer> entityReferenceInitializers = new ArrayList<EntityReferenceInitializer>();
 		private List<CollectionReferenceInitializer> arrayReferenceInitializers;
 		private List<CollectionReferenceInitializer> collectionReferenceInitializers;
 
 		@Override
 		public void add(CollectionReferenceInitializer collectionReferenceInitializer) {
 			if ( collectionReferenceInitializer.getCollectionReference().getCollectionPersister().isArray() ) {
 				if ( arrayReferenceInitializers == null ) {
 					arrayReferenceInitializers = new ArrayList<CollectionReferenceInitializer>();
 				}
 				arrayReferenceInitializers.add( collectionReferenceInitializer );
 			}
 			else {
 				if ( collectionReferenceInitializers == null ) {
 					collectionReferenceInitializers = new ArrayList<CollectionReferenceInitializer>();
 				}
 				collectionReferenceInitializers.add( collectionReferenceInitializer );
 			}
 		}
 
 		@Override
 		public void add(EntityReferenceInitializer entityReferenceInitializer) {
 			entityReferenceInitializers.add( entityReferenceInitializer );
 		}
 
 		public final List<EntityReferenceInitializer> getEntityReferenceInitializers() {
 			return entityReferenceInitializers;
 		}
 
 		public List<CollectionReferenceInitializer> getArrayReferenceInitializers() {
 			return arrayReferenceInitializers;
 
 		}
 
 		public List<CollectionReferenceInitializer> getNonArrayCollectionReferenceInitializers() {
 			return collectionReferenceInitializers;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/AliasResolutionContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/AliasResolutionContext.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/AliasResolutionContext.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/AliasResolutionContext.java
index 9d030a43be..7256285487 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/AliasResolutionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/AliasResolutionContext.java
@@ -1,61 +1,61 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 /**
  * Provides aliases that are used by load queries and ResultSet processors.
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public interface AliasResolutionContext {
 	public String resolveSqlTableAliasFromQuerySpaceUid(String querySpaceUid);
 
 	/**
 	 * Resolve the given QuerySpace UID to the EntityReferenceAliases representing the SQL aliases used in
 	 * building the SQL query.
 	 * <p/>
 	 * Assumes that a QuerySpace has already been registered.  As such this method simply returns {@code null}  if
 	 * no QuerySpace with that UID has yet been resolved in the context.
 	 *
 	 * @param querySpaceUid The QuerySpace UID whose EntityReferenceAliases we want to look up.
 	 *
 	 * @return The corresponding QuerySpace UID, or {@code null}.
 	 */
 	public EntityReferenceAliases resolveEntityReferenceAliases(String querySpaceUid);
 
 	/**
 	 * Resolve the given QuerySpace UID to the CollectionReferenceAliases representing the SQL aliases used in
 	 * building the SQL query.
 	 * <p/>
 	 * Assumes that a QuerySpace has already been registered.  As such this method simply returns {@code null}  if
 	 * no QuerySpace with that UID has yet been resolved in the context.
 	 *
 	 * @param querySpaceUid The QuerySpace UID whose CollectionReferenceAliases we want to look up.
 	 *
 	 * @return The corresponding QuerySpace UID, or {@code null}.
 	 */
 	public CollectionReferenceAliases resolveCollectionReferenceAliases(String querySpaceUid);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/BasicCollectionLoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/BasicCollectionLoadQueryDetails.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/BasicCollectionLoadQueryDetails.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/BasicCollectionLoadQueryDetails.java
index 5c913b54f6..3f05174b73 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/BasicCollectionLoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/BasicCollectionLoadQueryDetails.java
@@ -1,130 +1,126 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.loader.plan2.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan2.exec.internal.Helper;
-import org.hibernate.loader.plan2.exec.query.internal.SelectStatementBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.spi.CollectionQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.persister.collection.CollectionPropertyNames;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.internal.Helper;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 /**
  * @author Gail Badner
  */
 public class BasicCollectionLoadQueryDetails extends CollectionLoadQueryDetails {
 
 	/**
 	 * Constructs a EntityLoadQueryDetails object from the given inputs.
 	 *
 	 * @param loadPlan The load plan
 	 * @param buildingParameters And influencers that would affect the generated SQL (mostly we are concerned with those
 	 * that add additional joins here)
 	 * @param factory The SessionFactory
 	 *
 	 * @return The EntityLoadQueryDetails
 	 */
 	public static CollectionLoadQueryDetails makeForBatching(
 			LoadPlan loadPlan,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		final CollectionReturn rootReturn = Helper.INSTANCE.extractRootReturn( loadPlan, CollectionReturn.class );
 		final AliasResolutionContextImpl aliasResolutionContext = new AliasResolutionContextImpl( factory );
 		return new BasicCollectionLoadQueryDetails(
 						loadPlan,
 						aliasResolutionContext,
 						rootReturn,
 						buildingParameters,
 						factory
 				);
 	}
 
 	protected BasicCollectionLoadQueryDetails(
 			LoadPlan loadPlan,
 			AliasResolutionContextImpl aliasResolutionContext,
 			CollectionReturn rootReturn,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		super(
 				loadPlan,
 				aliasResolutionContext,
 				rootReturn,
 				buildingParameters,
 				factory
 		);
 		generate();
 	}
 
 	@Override
 	protected String getRootTableAlias() {
 		return getCollectionReferenceAliases().getCollectionTableAlias();
 	}
 
 	@Override
 	protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
 		selectStatementBuilder.appendSelectClauseFragment(
 			getQueryableCollection().selectFragment(
 					getCollectionReferenceAliases().getCollectionTableAlias(),
 					getCollectionReferenceAliases().getCollectionColumnAliases().getSuffix()
 			)
 		);
 		if ( getQueryableCollection().isManyToMany() ) {
 			final OuterJoinLoadable elementPersister = (OuterJoinLoadable) getQueryableCollection().getElementPersister();
 			selectStatementBuilder.appendSelectClauseFragment(
 					elementPersister.selectFragment(
 							getCollectionReferenceAliases().getElementTableAlias(),
 							getCollectionReferenceAliases().getEntityElementColumnAliases().getSuffix()
 					)
 			);
 		}
 		super.applyRootReturnSelectFragments( selectStatementBuilder );
 	}
 
 	@Override
 	protected void applyRootReturnTableFragments(SelectStatementBuilder selectStatementBuilder) {
 		selectStatementBuilder.appendFromClauseFragment(
 				getQueryableCollection().getTableName(),
 				getCollectionReferenceAliases().getCollectionTableAlias()
 		);
 	}
 
 	@Override
 	protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
 		final String manyToManyOrdering = getQueryableCollection().getManyToManyOrderByString(
 				getCollectionReferenceAliases().getElementTableAlias()
 		);
 		if ( StringHelper.isNotEmpty( manyToManyOrdering ) ) {
 			selectStatementBuilder.appendOrderByFragment( manyToManyOrdering );
 		}
 		super.applyRootReturnOrderByFragments( selectStatementBuilder );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/CollectionLoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/CollectionLoadQueryDetails.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/CollectionLoadQueryDetails.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/CollectionLoadQueryDetails.java
index 42a5f1c16f..6c2ee3616f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/CollectionLoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/CollectionLoadQueryDetails.java
@@ -1,215 +1,215 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.loader.plan2.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan2.exec.internal.EntityReferenceAliasesImpl;
-import org.hibernate.loader.plan2.exec.process.internal.CollectionReferenceInitializerImpl;
-import org.hibernate.loader.plan2.exec.process.internal.CollectionReturnReader;
-import org.hibernate.loader.plan2.exec.process.internal.EntityReferenceInitializerImpl;
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessingContextImpl;
-import org.hibernate.loader.plan2.exec.process.spi.AbstractRowReader;
-import org.hibernate.loader.plan2.exec.process.spi.CollectionReferenceInitializer;
-import org.hibernate.loader.plan2.exec.process.spi.ReaderCollector;
-import org.hibernate.loader.plan2.exec.process.spi.RowReader;
-import org.hibernate.loader.plan2.exec.query.internal.SelectStatementBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.spi.CollectionQuerySpace;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.internal.EntityReferenceAliasesImpl;
+import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceInitializerImpl;
+import org.hibernate.loader.plan.exec.process.internal.CollectionReturnReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.AbstractRowReader;
+import org.hibernate.loader.plan.exec.process.spi.CollectionReferenceInitializer;
+import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
+import org.hibernate.loader.plan.exec.process.spi.RowReader;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.spi.CollectionQuerySpace;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 /**
  * Handles interpreting a LoadPlan (for loading of a collection) by:<ul>
  *     <li>generating the SQL query to perform</li>
  *     <li>creating the readers needed to read the results from the SQL's ResultSet</li>
  * </ul>
  *
  * @author Gail Badner
  */
 public abstract class CollectionLoadQueryDetails extends AbstractLoadQueryDetails {
 	private final CollectionReferenceAliases collectionReferenceAliases;
 	private final ReaderCollector readerCollector;
 
 	protected CollectionLoadQueryDetails(
 			LoadPlan loadPlan,
 			AliasResolutionContextImpl aliasResolutionContext,
 			CollectionReturn rootReturn,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		super(
 				loadPlan,
 				aliasResolutionContext,
 				buildingParameters,
 				( (QueryableCollection) rootReturn.getCollectionPersister() ).getKeyColumnNames(),
 				rootReturn,
 //				collectionReferenceAliases.getCollectionTableAlias(),
 //				collectionReferenceAliases.getCollectionColumnAliases().getSuffix(),
 //				loadPlan.getQuerySpaces().getQuerySpaceByUid( rootReturn.getQuerySpaceUid() ),
 //				(OuterJoinLoadable) rootReturn.getCollectionPersister(),
 				factory
 		);
 		this.collectionReferenceAliases = aliasResolutionContext.generateCollectionReferenceAliases(
 				rootReturn.getQuerySpaceUid(),
 				rootReturn.getCollectionPersister()
 		);
 		this.readerCollector = new CollectionLoaderReaderCollectorImpl(
 				new CollectionReturnReader( rootReturn ),
 				new CollectionReferenceInitializerImpl( rootReturn, collectionReferenceAliases )
 		);
 		if ( rootReturn.getCollectionPersister().getElementType().isEntityType() ) {
 			final EntityReference elementEntityReference = rootReturn.getElementGraph().resolveEntityReference();
 			final EntityReferenceAliases elementEntityReferenceAliases = new EntityReferenceAliasesImpl(
 					collectionReferenceAliases.getElementTableAlias(),
 					collectionReferenceAliases.getEntityElementColumnAliases()
 			);
 			aliasResolutionContext.registerQuerySpaceAliases(
 					elementEntityReference.getQuerySpaceUid(),
 					elementEntityReferenceAliases
 			);
 			readerCollector.add(
 				new EntityReferenceInitializerImpl( elementEntityReference, elementEntityReferenceAliases )
 			);
 		}
 		if ( rootReturn.getCollectionPersister().hasIndex() &&
 				rootReturn.getCollectionPersister().getIndexType().isEntityType() ) {
 			final EntityReference indexEntityReference = rootReturn.getIndexGraph().resolveEntityReference();
 			final EntityReferenceAliases indexEntityReferenceAliases = aliasResolutionContext.generateEntityReferenceAliases(
 					indexEntityReference.getQuerySpaceUid(),
 					indexEntityReference.getEntityPersister()
 			);
 			readerCollector.add(
 					new EntityReferenceInitializerImpl( indexEntityReference, indexEntityReferenceAliases )
 			);
 		}
 	}
 
 	protected CollectionReturn getRootCollectionReturn() {
 		return (CollectionReturn) getRootReturn();
 	}
 
 	@Override
 	protected ReaderCollector getReaderCollector() {
 		return readerCollector;
 	}
 
 	@Override
 	protected CollectionQuerySpace getRootQuerySpace() {
 		return (CollectionQuerySpace) getQuerySpace( getRootCollectionReturn().getQuerySpaceUid() );
 	}
 
 	protected CollectionReferenceAliases getCollectionReferenceAliases() {
 		return collectionReferenceAliases;
 	}
 
 	protected QueryableCollection getQueryableCollection() {
 		return (QueryableCollection) getRootCollectionReturn().getCollectionPersister();
 	}
 
 	@Override
 	protected boolean shouldApplyRootReturnFilterBeforeKeyRestriction() {
 		return true;
 	}
 
 	@Override
 	protected  void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
 		if ( getQueryableCollection().hasIndex() &&
 				getQueryableCollection().getIndexType().isEntityType() ) {
 			final EntityReference indexEntityReference = getRootCollectionReturn().getIndexGraph().resolveEntityReference();
 			final EntityReferenceAliases indexEntityReferenceAliases = getAliasResolutionContext().resolveEntityReferenceAliases(
 					indexEntityReference.getQuerySpaceUid()
 			);
 			selectStatementBuilder.appendSelectClauseFragment(
 					( (OuterJoinLoadable) indexEntityReference.getEntityPersister() ).selectFragment(
 							indexEntityReferenceAliases.getTableAlias(),
 							indexEntityReferenceAliases.getColumnAliases().getSuffix()
 					)
 			);
 		}
 	}
 
 	@Override
 	protected void applyRootReturnFilterRestrictions(SelectStatementBuilder selectStatementBuilder) {
 		selectStatementBuilder.appendRestrictions(
 				getQueryableCollection().filterFragment(
 						getRootTableAlias(),
 						getQueryBuildingParameters().getQueryInfluencers().getEnabledFilters()
 				)
 		);
 	}
 
 	@Override
 	protected void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder) {
 	}
 
 	@Override
 	protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
 		final String ordering = getQueryableCollection().getSQLOrderByString( getRootTableAlias() );
 		if ( StringHelper.isNotEmpty( ordering ) ) {
 			selectStatementBuilder.appendOrderByFragment( ordering );
 		}
 	}
 
 	private static class CollectionLoaderReaderCollectorImpl extends ReaderCollectorImpl {
 		private final CollectionReturnReader collectionReturnReader;
 
 		public CollectionLoaderReaderCollectorImpl(
 				CollectionReturnReader collectionReturnReader,
 				CollectionReferenceInitializer collectionReferenceInitializer) {
 			this.collectionReturnReader = collectionReturnReader;
 			add( collectionReferenceInitializer );
 		}
 
 		@Override
 		public RowReader buildRowReader() {
 			return new CollectionLoaderRowReader( this );
 		}
 
 		@Override
 		public CollectionReturnReader getReturnReader() {
 			return collectionReturnReader;
 		}
 	}
 
 	public static class CollectionLoaderRowReader extends AbstractRowReader {
 		private final CollectionReturnReader rootReturnReader;
 
 		public CollectionLoaderRowReader(CollectionLoaderReaderCollectorImpl collectionLoaderReaderCollector) {
 			super( collectionLoaderReaderCollector );
 			this.rootReturnReader = collectionLoaderReaderCollector.getReturnReader();
 		}
 
 		@Override
 		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 			return rootReturnReader.read( resultSet, context );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/CollectionReferenceAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/CollectionReferenceAliases.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/CollectionReferenceAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/CollectionReferenceAliases.java
index 0d89a59084..611c7e45ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/CollectionReferenceAliases.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/CollectionReferenceAliases.java
@@ -1,69 +1,69 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 
 /**
  * @author Steve Ebersole
  */
 public interface CollectionReferenceAliases {
 	/**
 	 * Obtain the table alias used for the collection table of the CollectionReference.
 	 *
 	 * @return The collection table alias.
 	 */
 	public String getCollectionTableAlias();
 
 	/**
 	 * Obtain the alias of the table that contains the collection element values.
 	 * <p/>
 	 * Unlike in the legacy Loader case, CollectionReferences in the LoadPlan code refer to both the
 	 * collection and the elements *always*.  In Loader the elements were handled by EntityPersister associations
 	 * entries for one-to-many and many-to-many.  In LoadPlan we need to describe the collection table/columns
 	 * as well as the entity element table/columns.  For "basic collections" and one-to-many collections, the
 	 * "element table" and the "collection table" are actually the same.  For the many-to-many case this will be
 	 * different and we need to track it separately.
 	 *
 	 * @return The element table alias.  Only different from {@link #getCollectionTableAlias()} in the case of
 	 * many-to-many.
 	 */
 	public String getElementTableAlias();
 
 	/**
 	 * Obtain the aliases for the columns related to the collection structure such as the FK, index/key, or identifier
 	 * (idbag).
 	 *
 	 * @return The collection column aliases.
 	 */
 	public CollectionAliases getCollectionColumnAliases();
 
 	/**
 	 * Obtain the column aliases for the element values when the element of the collection is an entity.
 	 *
 	 * @return The column aliases for the entity element; {@code null} if the collection element is not an entity.
 	 */
 	public EntityAliases getEntityElementColumnAliases();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/EntityLoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityLoadQueryDetails.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/EntityLoadQueryDetails.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityLoadQueryDetails.java
index a08e02b4ca..62a1356436 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/EntityLoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityLoadQueryDetails.java
@@ -1,303 +1,303 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Collections;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.plan2.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan2.exec.internal.Helper;
-import org.hibernate.loader.plan2.exec.process.internal.EntityReferenceInitializerImpl;
-import org.hibernate.loader.plan2.exec.process.internal.EntityReturnReader;
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessingContextImpl;
-import org.hibernate.loader.plan2.exec.process.internal.ResultSetProcessorHelper;
-import org.hibernate.loader.plan2.exec.process.spi.AbstractRowReader;
-import org.hibernate.loader.plan2.exec.process.spi.EntityReferenceInitializer;
-import org.hibernate.loader.plan2.exec.process.spi.ReaderCollector;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.loader.plan2.exec.process.spi.RowReader;
-import org.hibernate.loader.plan2.exec.query.internal.SelectStatementBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.internal.Helper;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
+import org.hibernate.loader.plan.exec.process.internal.EntityReturnReader;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorHelper;
+import org.hibernate.loader.plan.exec.process.spi.AbstractRowReader;
+import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
+import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
+import org.hibernate.loader.plan.exec.process.spi.RowReader;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.QuerySpace;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.Type;
 
 /**
  * Handles interpreting a LoadPlan (for loading of an entity) by:<ul>
  *     <li>generating the SQL query to perform</li>
  *     <li>creating the readers needed to read the results from the SQL's ResultSet</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public class EntityLoadQueryDetails extends AbstractLoadQueryDetails {
 	private static final Logger log = CoreLogging.logger( EntityLoadQueryDetails.class );
 
 	/**
 	 * Constructs a EntityLoadQueryDetails object from the given inputs.
 	 *
 	 * @param loadPlan The load plan
 	 * @param keyColumnNames The columns to load the entity by (the PK columns or some other unique set of columns)
 	 * @param buildingParameters And influencers that would affect the generated SQL (mostly we are concerned with those
 	 * that add additional joins here)
 	 * @param factory The SessionFactory
 	 *
 	 * @return The EntityLoadQueryDetails
 	 */
 	public static EntityLoadQueryDetails makeForBatching(
 			LoadPlan loadPlan,
 			String[] keyColumnNames,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		final int batchSize = buildingParameters.getBatchSize();
 		final boolean shouldUseOptionalEntityInformation = batchSize == 1;
 
 		final EntityReturn rootReturn = Helper.INSTANCE.extractRootReturn( loadPlan, EntityReturn.class );
 		final String[] keyColumnNamesToUse = keyColumnNames != null
 				? keyColumnNames
 				: ( (Queryable) rootReturn.getEntityPersister() ).getIdentifierColumnNames();
 		// Should be just one querySpace (of type EntityQuerySpace) in querySpaces.  Should we validate that?
 		// Should we make it a util method on Helper like we do for extractRootReturn ?
 		final AliasResolutionContextImpl aliasResolutionContext = new AliasResolutionContextImpl( factory );
 		return new EntityLoadQueryDetails(
 				loadPlan,
 				keyColumnNamesToUse,
 				aliasResolutionContext,
 				rootReturn,
 				buildingParameters,
 				factory
 		);
 	}
 
 	private final EntityReferenceAliases entityReferenceAliases;
 	private final ReaderCollector readerCollector;
 
 	protected EntityLoadQueryDetails(
 			LoadPlan loadPlan,
 			String[] keyColumnNames,
 			AliasResolutionContextImpl aliasResolutionContext,
 			EntityReturn rootReturn,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		super(
 				loadPlan,
 				aliasResolutionContext,
 				buildingParameters,
 				keyColumnNames,
 				rootReturn,
 				factory
 		);
 		this.entityReferenceAliases = aliasResolutionContext.generateEntityReferenceAliases(
 				rootReturn.getQuerySpaceUid(),
 				rootReturn.getEntityPersister()
 		);
 		this.readerCollector = new EntityLoaderReaderCollectorImpl(
 				new EntityReturnReader( rootReturn ),
 				new EntityReferenceInitializerImpl( rootReturn, entityReferenceAliases, true )
 		);
 		generate();
 	}
 
 	private EntityReturn getRootEntityReturn() {
 		return (EntityReturn) getRootReturn();
 	}
 
 	/**
 	 * Applies "table fragments" to the FROM-CLAUSE of the given SelectStatementBuilder for the given Loadable
 	 *
 	 * @param select The SELECT statement builder
 	 *
 	 * @see org.hibernate.persister.entity.OuterJoinLoadable#fromTableFragment(java.lang.String)
 	 * @see org.hibernate.persister.entity.Joinable#fromJoinFragment(java.lang.String, boolean, boolean)
 	 */
 	protected void applyRootReturnTableFragments(SelectStatementBuilder select) {
 		final String fromTableFragment;
 		final String rootAlias = entityReferenceAliases.getTableAlias();
 		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 		if ( getQueryBuildingParameters().getLockOptions() != null ) {
 			fromTableFragment = getSessionFactory().getDialect().appendLockHint(
 					getQueryBuildingParameters().getLockOptions(),
 					outerJoinLoadable.fromTableFragment( rootAlias )
 			);
 			select.setLockOptions( getQueryBuildingParameters().getLockOptions() );
 		}
 		else if ( getQueryBuildingParameters().getLockMode() != null ) {
 			fromTableFragment = getSessionFactory().getDialect().appendLockHint(
 					getQueryBuildingParameters().getLockMode(),
 					outerJoinLoadable.fromTableFragment( rootAlias )
 			);
 			select.setLockMode( getQueryBuildingParameters().getLockMode() );
 		}
 		else {
 			fromTableFragment = outerJoinLoadable.fromTableFragment( rootAlias );
 		}
 		select.appendFromClauseFragment( fromTableFragment + outerJoinLoadable.fromJoinFragment( rootAlias, true, true ) );
 	}
 
 	protected void applyRootReturnFilterRestrictions(SelectStatementBuilder selectStatementBuilder) {
 		final Queryable rootQueryable = (Queryable) getRootEntityReturn().getEntityPersister();
 		selectStatementBuilder.appendRestrictions(
 				rootQueryable.filterFragment(
 						entityReferenceAliases.getTableAlias(),
 						Collections.emptyMap()
 				)
 		);
 	}
 
 	protected void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder) {
 		final Joinable joinable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 		selectStatementBuilder.appendRestrictions(
 				joinable.whereJoinFragment(
 						entityReferenceAliases.getTableAlias(),
 						true,
 						true
 				)
 		);
 	}
 
 	@Override
 	protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
 	}
 
 	@Override
 	protected ReaderCollector getReaderCollector() {
 		return readerCollector;
 	}
 
 	@Override
 	protected QuerySpace getRootQuerySpace() {
 		return getQuerySpace( getRootEntityReturn().getQuerySpaceUid() );
 	}
 
 	@Override
 	protected String getRootTableAlias() {
 		return entityReferenceAliases.getTableAlias();
 	}
 
 	@Override
 	protected boolean shouldApplyRootReturnFilterBeforeKeyRestriction() {
 		return false;
 	}
 
 	protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
 		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 		selectStatementBuilder.appendSelectClauseFragment(
 				outerJoinLoadable.selectFragment(
 						entityReferenceAliases.getTableAlias(),
 						entityReferenceAliases.getColumnAliases().getSuffix()
 
 				)
 		);
 	}
 
 	private static class EntityLoaderReaderCollectorImpl extends ReaderCollectorImpl {
 		private final EntityReturnReader entityReturnReader;
 
 		public EntityLoaderReaderCollectorImpl(
 				EntityReturnReader entityReturnReader,
 				EntityReferenceInitializer entityReferenceInitializer) {
 			this.entityReturnReader = entityReturnReader;
 			add( entityReferenceInitializer );
 		}
 
 		@Override
 		public RowReader buildRowReader() {
 			return new EntityLoaderRowReader( this );
 		}
 
 		@Override
 		public EntityReturnReader getReturnReader() {
 			return entityReturnReader;
 		}
 	}
 
 	public static class EntityLoaderRowReader extends AbstractRowReader {
 		private final EntityReturnReader rootReturnReader;
 
 		public EntityLoaderRowReader(EntityLoaderReaderCollectorImpl entityLoaderReaderCollector) {
 			super( entityLoaderReaderCollector );
 			this.rootReturnReader = entityLoaderReaderCollector.getReturnReader();
 		}
 
 		@Override
 		public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 			final ResultSetProcessingContext.EntityReferenceProcessingState processingState =
 					rootReturnReader.getIdentifierResolutionContext( context );
 			// if the entity reference we are hydrating is a Return, it is possible that its EntityKey is
 			// supplied by the QueryParameter optional entity information
 			if ( context.shouldUseOptionalEntityInformation() && context.getQueryParameters().getOptionalId() != null ) {
 				EntityKey entityKey = ResultSetProcessorHelper.getOptionalObjectKey(
 						context.getQueryParameters(),
 						context.getSession()
 				);
 				processingState.registerIdentifierHydratedForm( entityKey.getIdentifier() );
 				processingState.registerEntityKey( entityKey );
 				final EntityPersister entityPersister = processingState.getEntityReference().getEntityPersister();
 				if ( entityPersister.getIdentifierType().isComponentType()  ) {
 					final ComponentType identifierType = (ComponentType) entityPersister.getIdentifierType();
 					if ( !identifierType.isEmbedded() ) {
 						addKeyManyToOnesToSession(
 								context,
 								identifierType,
 								entityKey.getIdentifier()
 						);
 					}
 				}
 			}
 			return super.readRow( resultSet, context );
 		}
 
 		private void addKeyManyToOnesToSession(ResultSetProcessingContextImpl context, ComponentType componentType, Object component ) {
 			for ( int i = 0 ; i < componentType.getSubtypes().length ; i++ ) {
 				final Type subType = componentType.getSubtypes()[ i ];
 				final Object subValue = componentType.getPropertyValue( component, i, context.getSession() );
 				if ( subType.isEntityType() ) {
 					( (Session) context.getSession() ).buildLockRequest( LockOptions.NONE ).lock( subValue );
 				}
 				else if ( subType.isComponentType() ) {
 					addKeyManyToOnesToSession( context, (ComponentType) subType, subValue  );
 				}
 			}
 		}
 
 		@Override
 		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 			return rootReturnReader.read( resultSet, context );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/EntityReferenceAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityReferenceAliases.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/EntityReferenceAliases.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityReferenceAliases.java
index 94f3caad2d..e5828f9e78 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/EntityReferenceAliases.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityReferenceAliases.java
@@ -1,54 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import org.hibernate.loader.EntityAliases;
 
 /**
- * Aggregates the alias/suffix information in relation to an {@link org.hibernate.loader.plan2.spi.EntityReference}
+ * Aggregates the alias/suffix information in relation to an {@link org.hibernate.loader.plan.spi.EntityReference}
  *
  * todo : add a contract (interface) that can be shared by entity and collection alias info objects as lhs/rhs of a join ?
  *
  * @author Steve Ebersole
  */
 public interface EntityReferenceAliases {
 	/**
 	 * Obtain the table alias used for referencing the table of the EntityReference.
 	 * <p/>
 	 * Note that this currently just returns the "root alias" whereas sometimes an entity reference covers
 	 * multiple tables.  todo : to help manage this, consider a solution like TableAliasRoot from the initial ANTLR re-work
 	 * see http://anonsvn.jboss.org/repos/hibernate/core/branches/antlr3/src/main/java/org/hibernate/sql/ast/alias/TableAliasGenerator.java
 	 *
 	 * @return The (root) table alias for the described entity reference.
 	 */
 	public String getTableAlias();
 
 	/**
 	 * Obtain the column aliases for the select fragment columns associated with the described entity reference.  These
 	 * are the column renames by which the values can be extracted from the SQL result set.
 	 *
 	 * @return The column aliases associated with the described entity reference.
 	 */
 	public EntityAliases getColumnAliases();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/LoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LoadQueryDetails.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/LoadQueryDetails.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LoadQueryDetails.java
index d081cd7946..70f702c47e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/LoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LoadQueryDetails.java
@@ -1,36 +1,36 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 
 /**
  * @author Steve Ebersole
  */
 public interface LoadQueryDetails {
 	public String getSqlStatement();
 
 	public ResultSetProcessor getResultSetProcessor();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/LockModeResolver.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LockModeResolver.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/LockModeResolver.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LockModeResolver.java
index 7f4650adf4..55397a2a16 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/LockModeResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LockModeResolver.java
@@ -1,34 +1,34 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import org.hibernate.LockMode;
-import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan.spi.EntityReference;
 
 /**
  * @author Steve Ebersole
  */
 public interface LockModeResolver {
 	public LockMode resolveLockMode(EntityReference entityReference);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/OneToManyLoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/OneToManyLoadQueryDetails.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/OneToManyLoadQueryDetails.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/OneToManyLoadQueryDetails.java
index 074f7ae5d9..2c9e0f2bf3 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/spi/OneToManyLoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/OneToManyLoadQueryDetails.java
@@ -1,124 +1,124 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.exec.spi;
+package org.hibernate.loader.plan.exec.spi;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan2.exec.internal.Helper;
-import org.hibernate.loader.plan2.exec.query.internal.SelectStatementBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.internal.Helper;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 /**
  * @author Gail Badner
  */
 public class OneToManyLoadQueryDetails extends CollectionLoadQueryDetails {
 
 	/**
 	 * Constructs a EntityLoadQueryDetails object from the given inputs.
 	 *
 	 * @param loadPlan The load plan
 	 * @param buildingParameters And influencers that would affect the generated SQL (mostly we are concerned with those
 	 * that add additional joins here)
 	 * @param factory The SessionFactory
 	 *
 	 * @return The EntityLoadQueryDetails
 	 */
 	public static CollectionLoadQueryDetails makeForBatching(
 			LoadPlan loadPlan,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		final CollectionReturn rootReturn = Helper.INSTANCE.extractRootReturn( loadPlan, CollectionReturn.class );
 		final AliasResolutionContextImpl aliasResolutionContext = new AliasResolutionContextImpl( factory );
 		return new OneToManyLoadQueryDetails(
 						loadPlan,
 						aliasResolutionContext,
 						rootReturn,
 						buildingParameters,
 						factory
 				);
 	}
 
 	protected OneToManyLoadQueryDetails(
 			LoadPlan loadPlan,
 			AliasResolutionContextImpl aliasResolutionContext,
 			CollectionReturn rootReturn,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		super(
 				loadPlan,
 				aliasResolutionContext,
 				rootReturn,
 				buildingParameters,
 				factory
 		);
 		generate();
 	}
 
 	@Override
 	protected String getRootTableAlias() {
 		return getElementEntityReferenceAliases().getTableAlias();
 	}
 
 	@Override
 	protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
 
 		selectStatementBuilder.appendSelectClauseFragment(
 				getQueryableCollection().selectFragment(
 						null,
 						null,
 						//getCollectionReferenceAliases().getCollectionTableAlias(),
 						getElementEntityReferenceAliases().getTableAlias(),
 						getElementEntityReferenceAliases().getColumnAliases().getSuffix(),
 						getCollectionReferenceAliases().getCollectionColumnAliases().getSuffix(),
 						true
 				)
 		);
 		super.applyRootReturnSelectFragments( selectStatementBuilder );
 	}
 
 	@Override
 	protected void applyRootReturnTableFragments(SelectStatementBuilder selectStatementBuilder) {
 		final OuterJoinLoadable elementOuterJoinLoadable =
 				(OuterJoinLoadable) getElementEntityReference().getEntityPersister();
 		//final String tableAlias = getCollectionReferenceAliases().getCollectionTableAlias();
 		final String tableAlias = getElementEntityReferenceAliases().getTableAlias();
 		final String fragment =
 				elementOuterJoinLoadable.fromTableFragment( tableAlias ) +
 						elementOuterJoinLoadable.fromJoinFragment( tableAlias, true, true);
 		selectStatementBuilder.appendFromClauseFragment( fragment );
 	}
 
 	private EntityReference getElementEntityReference() {
 		return getRootCollectionReturn().getElementGraph().resolveEntityReference();
 	}
 
 	private EntityReferenceAliases getElementEntityReferenceAliases() {
 		return getAliasResolutionContext().resolveEntityReferenceAliases( getElementEntityReference().getQuerySpaceUid() );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AbstractPlanNode.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractPlanNode.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AbstractPlanNode.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractPlanNode.java
index d379363f15..538ffdbb31 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AbstractPlanNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractPlanNode.java
@@ -1,47 +1,47 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * Base class for LoadPlan nodes to hold references to the session factory.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractPlanNode {
 	private final SessionFactoryImplementor sessionFactory;
 
 	public AbstractPlanNode(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	public AbstractPlanNode(AbstractPlanNode original) {
 		this( original.sessionFactory() );
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AnyAttributeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AnyAttributeFetch.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AnyAttributeFetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AnyAttributeFetch.java
index df9419c5d3..5f2d200aa8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AnyAttributeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AnyAttributeFetch.java
@@ -1,30 +1,30 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * @author Gail Badner
  */
 public interface AnyAttributeFetch extends Fetch, FetchSource {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AttributeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AttributeFetch.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AttributeFetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AttributeFetch.java
index cf27455f29..1b75d69315 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/AttributeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AttributeFetch.java
@@ -1,35 +1,35 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 
 /**
  * Models a {@link Fetch} that is specifically for an attribute.
  *
  * @author Gail Badner
  */
 public interface AttributeFetch extends Fetch {
 	public AttributeDefinition getFetchedAttributeDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/BidirectionalEntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/BidirectionalEntityReference.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/BidirectionalEntityReference.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/BidirectionalEntityReference.java
index 998abcb134..3cd6c0d4b5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/BidirectionalEntityReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/BidirectionalEntityReference.java
@@ -1,48 +1,48 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Represents the circular side of a bi-directional entity fetch.  Wraps a reference to an EntityReference
  * as an EntityFetch.  We can use the special type as a trigger in AliasResolutionContext, etc to lookup information
  * based on the wrapped reference.
  * <p/>
  * This relies on reference lookups against the EntityReference instances, therefore this allows representation of the
  * circularity but with a little protection against potential stack overflows.  This is unfortunately still a cyclic
  * graph.  An alternative approach is to make the graph acyclic (DAG) would be to follow the process I adopted in the
  * original HQL Antlr v3 work with regard to always applying an alias to the "persister reference", even where that
  * meant creating a generated, unique identifier as the alias.  That allows other parts of the tree to refer to the
  * "persister reference" by that alias without the need for potentially cyclic graphs (think ALIAS_REF in the current
  * ORM parser).  Those aliases can then be mapped/catalogued against the "persister reference" for retrieval as needed.
  *
  * @author Steve Ebersole
  */
 public interface BidirectionalEntityReference extends EntityReference {
 	/**
 	 * Get the targeted EntityReference
 	 *
 	 * @return The targeted EntityReference
 	 */
 	public EntityReference getTargetEntityReference();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionAttributeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionAttributeFetch.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionAttributeFetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionAttributeFetch.java
index 3129ac7465..fc4e7cabaf 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionAttributeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionAttributeFetch.java
@@ -1,37 +1,37 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.type.CollectionType;
 
 /**
  * Models the requested fetching of a persistent collection attribute.
  *
  * @author Steve Ebersole
  */
 public interface CollectionAttributeFetch extends AttributeFetch, CollectionReference {
 	@Override
 	public CollectionType getFetchedType();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableElement.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetchableElement.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableElement.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetchableElement.java
index de16f5ebb3..1e064aeb66 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetchableElement.java
@@ -1,38 +1,38 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * A collection element which can be a FetchSource.
  *
  * @author Steve Ebersole
  */
 public interface CollectionFetchableElement extends FetchSource {
 	/**
 	 * Reference back to the collection to which this element belongs
 	 *
 	 * @return the collection reference.
 	 */
 	public CollectionReference getCollectionReference();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableIndex.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetchableIndex.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableIndex.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetchableIndex.java
index 8c2e6d0c22..968b1612e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableIndex.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetchableIndex.java
@@ -1,38 +1,38 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * A collection index which can be a FetchSource.
  *
  * @author Steve Ebersole
  */
 public interface CollectionFetchableIndex extends FetchSource {
 	/**
 	 * Reference back to the collection to which this index belongs
 	 *
 	 * @return the collection reference.
 	 */
 	public CollectionReference getCollectionReference();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionQuerySpace.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionQuerySpace.java
index 9aa768f09d..b54e8acc09 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionQuerySpace.java
@@ -1,42 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Models a QuerySpace for a persistent collection.
  * <p/>
  * It's {@link #getDisposition()} result will be {@link Disposition#COLLECTION}
  *
  * @author Steve Ebersole
  */
 public interface CollectionQuerySpace extends QuerySpace {
 	/**
 	 * Retrieve the collection persister this QuerySpace refers to.
 	 *
 	 * @return The collection persister.
 	 */
 	public CollectionPersister getCollectionPersister();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReference.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
index 1b404ac004..007dbd132a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
@@ -1,80 +1,80 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Represents a reference to a persistent collection either as a Return or as a Fetch
  *
  * @author Steve Ebersole
  */
 public interface CollectionReference {
 	/**
 	 * Obtain the UID of the QuerySpace (specifically a {@link CollectionQuerySpace}) that this CollectionReference
 	 * refers to.
 	 *
 	 * @return The UID
 	 */
 	public String getQuerySpaceUid();
 
 	/**
 	 * Retrieves the CollectionPersister describing the collection associated with this Return.
 	 *
 	 * @return The CollectionPersister.
 	 */
 	public CollectionPersister getCollectionPersister();
 
 	/**
 	 * Retrieve the metadata about the index of this collection *as a FetchSource*.  Will return
 	 * {@code null} when:<ul>
 	 *     <li>the collection is not indexed</li>
 	 *     <li>the index is not a composite or entity (cannot act as a FetchSource)</li>
 	 * </ul>
 	 * <p/>
 	 * Works only for map keys, since a List index (int type) cannot act as a FetchSource.
 	 * <p/>
 	 *
 	 * @return The collection index metadata as a FetchSource, or {@code null}.
 	 */
 	public CollectionFetchableIndex getIndexGraph();
 
 	/**
 	 * Retrieve the metadata about the elements of this collection *as a FetchSource*.  Will return
 	 * {@code null} when the element is not a composite or entity (cannot act as a FetchSource).
 	 * Works only for map keys, since a List index cannot be anything other than an int which cannot be a FetchSource.
 	 * <p/>
 	 *
 	 * @return The collection element metadata as a FetchSource, or {@code null}.
 	 */
 	public CollectionFetchableElement getElementGraph();
 
 	/**
 	 * Retrieve the PropertyPath to this reference.
 	 *
 	 * @return The PropertyPath
 	 */
 	public PropertyPath getPropertyPath();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
index d49e56d13b..635046f794 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
@@ -1,33 +1,33 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Models the a persistent collection as root {@link Return}.  Pertinent to collection initializer
- * ({@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#COLLECTION_INITIALIZER}) LoadPlans only,
+ * ({@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#COLLECTION_INITIALIZER}) LoadPlans only,
  *
  * @author Steve Ebersole
  */
 public interface CollectionReturn extends CollectionReference, Return {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeAttributeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeAttributeFetch.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeAttributeFetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeAttributeFetch.java
index b7556e7fd0..431c2e0d2c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeAttributeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeAttributeFetch.java
@@ -1,30 +1,30 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * @author Gail Badner
  */
 public interface CompositeAttributeFetch extends CompositeFetch, AttributeFetch {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeFetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
index 3bc26b9dc3..1142f87bec 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
@@ -1,32 +1,32 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Models the requested fetching of a composite attribute.
  *
  * @author Steve Ebersole
  */
 public interface CompositeFetch extends Fetch, FetchSource {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeQuerySpace.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeQuerySpace.java
index af4778501a..26163ee5c3 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeQuerySpace.java
@@ -1,34 +1,34 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Models a QuerySpace for a composition (component/embeddable).
  * <p/>
  * It's {@link #getDisposition()} result will be {@link Disposition#COMPOSITE}
  *
  * @author Steve Ebersole
  */
 public interface CompositeQuerySpace extends QuerySpace {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityFetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
index e042415501..d5dc27da16 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
@@ -1,34 +1,34 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public interface EntityFetch extends AttributeFetch, EntityReference {
 	@Override
 	EntityType getFetchedType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityIdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIdentifierDescription.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityIdentifierDescription.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIdentifierDescription.java
index b336bcc91a..4bd5575202 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityIdentifierDescription.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIdentifierDescription.java
@@ -1,49 +1,49 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Descriptor for the identifier of an entity as a FetchSource (which allows for key-many-to-one handling).
  *
  * @author Steve Ebersole
  */
 public interface EntityIdentifierDescription {
 	/**
 	 * Can this EntityIdentifierDescription be treated as a FetchSource and if so does it have any
 	 * fetches?
 	 *
 	 * @return {@code true} iff {@code this} can be cast to {@link FetchSource} and (after casting) it returns
 	 * non-empty results for {@link FetchSource#getFetches()}
 	 */
 	public boolean hasFetches();
 
 	/**
 	 * Can this EntityIdentifierDescription be treated as a FetchSource and if so does it have any
 	 * bidirectional entity references?
 	 *
 	 * @return {@code true} iff {@code this} can be cast to {@link FetchSource} and (after casting) it returns
 	 * non-empty results for {@link FetchSource#getBidirectionalEntityReferences()}
 	 */
 	public boolean hasBidirectionalEntityReferences();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityQuerySpace.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityQuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityQuerySpace.java
index e955bd66dc..57e8350737 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityQuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityQuerySpace.java
@@ -1,42 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Models a QuerySpace specific to an entity (EntityPersister).
  * <p/>
  * It's {@link #getDisposition()} result will be {@link Disposition#ENTITY}
  *
  * @author Steve Ebersole
  */
 public interface EntityQuerySpace extends QuerySpace {
 	 /**
 	 * Retrieve the EntityPersister that this QuerySpace refers to.
 	  *
 	 * @return The entity persister
 	 */
 	public EntityPersister getEntityPersister();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReference.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
index 8357638535..bbad9420c5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
@@ -1,56 +1,56 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Represents a reference to an entity either as a return or as a fetch
  *
  * @author Steve Ebersole
  */
 public interface EntityReference extends FetchSource {
 
 	/**
 	 * Obtain the UID of the QuerySpace (specifically a {@link EntityQuerySpace}) that this EntityReference
 	 * refers to.
 	 *
 	 * @return The UID
 	 */
 	public String getQuerySpaceUid();
 
 	/**
 	 * Retrieves the EntityPersister describing the entity associated with this Return.
 	 *
 	 * @return The EntityPersister.
 	 */
 	public EntityPersister getEntityPersister();
 
 	/**
 	 * Get the description of this entity's identifier.
 	 *
 	 * @return The identifier description.
 	 */
 	public EntityIdentifierDescription getIdentifierDescription();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
similarity index 85%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
index 170d63ca44..a8dd92d755 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
@@ -1,34 +1,34 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Models the an entity as root {@link Return}.  Pertinent to entity loader
- * ({@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#ENTITY_LOADER}) and mixed
- * ({@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#MIXED}) LoadPlans
+ * ({@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#ENTITY_LOADER}) and mixed
+ * ({@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#MIXED}) LoadPlans
  *
  * @author Steve Ebersole
  */
 public interface EntityReturn extends EntityReference, Return {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Fetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Fetch.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
index b2477bbb16..f0c1b54ae4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Fetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
@@ -1,85 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.type.Type;
 
 /**
  * Contract for associations that are being fetched.
  * <p/>
  * NOTE : can represent components/embeddables
  *
  * @author Steve Ebersole
  */
 public interface Fetch {
 	/**
 	 * Obtain the owner of this fetch.
 	 *
 	 * @return The fetch owner.
 	 */
 	public FetchSource getSource();
 
 	/**
 	 * Get the property path to this fetch
 	 *
 	 * @return The property path
 	 */
 	public PropertyPath getPropertyPath();
 
 	/**
 	 * Gets the fetch strategy for this fetch.
 	 *
 	 * @return the fetch strategy for this fetch.
 	 */
 	public FetchStrategy getFetchStrategy();
 
 	/**
 	 * Get the Hibernate Type that describes the fetched attribute
 	 *
 	 * @return The Type of the fetched attribute
 	 */
 	public Type getFetchedType();
 
 	/**
 	 * Is this fetch nullable?
 	 *
 	 * @return true, if this fetch is nullable; false, otherwise.
 	 */
 	public boolean isNullable();
 
 
 
 	// Hoping to make these go away ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getAdditionalJoinConditions();
 
 	/**
 	 * Generates the SQL select fragments for this fetch.  A select fragment is the column and formula references.
 	 *
 	 * @return the select fragments
 	 */
 	public String[] toSqlSelectFragments(String alias);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/FetchSource.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchSource.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/FetchSource.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchSource.java
index 46239961c1..d48d6c1a6d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/FetchSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchSource.java
@@ -1,131 +1,131 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.loader.PropertyPath;
 
 /**
  * Contract for a FetchSource (aka, the thing that owns the fetched attribute).
  *
  *
  * @author Steve Ebersole
  */
 public interface FetchSource {
 
 	/**
 	 * Get the property path to this fetch owner
 	 *
 	 * @return The property path
 	 */
 	public PropertyPath getPropertyPath();
 
 	public String getQuerySpaceUid();
 
 	/**
 	 * Retrieve the fetches owned by this fetch source.
 	 *
 	 * @return The owned fetches.
 	 */
 	public Fetch[] getFetches();
 
 	/**
 	 * Retrieve the bidirectional entity references owned by this fetch source.
 	 *
 	 * @return The owned bidirectional entity references.
 	 */
 	public BidirectionalEntityReference[] getBidirectionalEntityReferences();
 
 	/**
 	 * Resolve the "current" {@link EntityReference}, or null if none.
 	 *
 	 * If this object is an {@link EntityReference}, then this object is returned.
 	 *
 	 * If this object is a {@link CompositeFetch}, then the nearest {@link EntityReference}
 	 * will be resolved from its source, if possible.
 	 *
 	 * If no EntityReference can be resolved, null is return.
 	 *
 	 *  @return the "current" EntityReference or null if none.
 	 *  otherwise, if this object is also a {@link Fetch}, then
 	 * .
-	 * @see org.hibernate.loader.plan2.spi.Fetch#getSource().
+	 * @see org.hibernate.loader.plan.spi.Fetch#getSource().
 	 * 	 */
 	public EntityReference resolveEntityReference();
 
 	// Stuff I can hopefully remove ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
-	 * The idea of addFetch() below has moved to {@link org.hibernate.loader.plan2.build.spi.ExpandingFetchSource}.
+	 * The idea of addFetch() below has moved to {@link org.hibernate.loader.plan.build.spi.ExpandingFetchSource}.
 	 * <p/>
 	 * Most of the others are already part of Fetch
 	 */
 
 
 //
 //
 //
 //	/**
 //	 * Returns the type of the specified fetch.
 //	 *
 //	 * @param fetch - the owned fetch.
 //	 *
 //	 * @return the type of the specified fetch.
 //	 */
 //	public Type getType(Fetch fetch);
 //
 //	/**
 //	 * Is the specified fetch nullable?
 //	 *
 //	 * @param fetch - the owned fetch.
 //	 *
 //	 * @return true, if the fetch is nullable; false, otherwise.
 //	 */
 //	public boolean isNullable(Fetch fetch);
 //
 //	/**
 //	 * Generates the SQL select fragments for the specified fetch.  A select fragment is the column and formula
 //	 * references.
 //	 *
 //	 * @param fetch - the owned fetch.
 //	 * @param alias The table alias to apply to the fragments (used to qualify column references)
 //	 *
 //	 * @return the select fragments
 //	 */
 //	public String[] toSqlSelectFragments(Fetch fetch, String alias);
 //
 //	/**
 //	 * Contract to add fetches to this owner.  Care should be taken in calling this method; it is intended
 //	 * for Hibernate usage
 //	 *
 //	 * @param fetch The fetch to add
 //	 */
 //	public void addFetch(Fetch fetch);
 //
 //
 //	public SqlSelectFragmentResolver toSqlSelectFragmentResolver();
 //
 //
 //
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Join.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Join.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Join.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Join.java
index fa37fd379e..b88dc6c336 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Join.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Join.java
@@ -1,50 +1,50 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Represents a join in the QuerySpace-sense.  In HQL/JP-QL, this would be an implicit/explicit join; in
  * metamodel-driven LoadPlans, this would be joins indicated by the metamodel.
  */
 public interface Join {
 	// todo : would be good to have the SQL alias info here because we know it when we would be building this Join,
 	// and to do it afterwards would require lot of logic to recreate.
 	// But we do want this model to be workable in Search/OGM as well, plus the HQL parser has shown time-and-again
 	// that it is best to put off resolving and injecting physical aliases etc until as-late-as-possible.
 
 	// todo : do we know enough here to declare the "owner" side?  aka, the "fk direction"
 	// and if we do ^^, is that enough to figure out the SQL aliases more easily (see above)?
 
 	public QuerySpace getLeftHandSide();
 
 	public QuerySpace getRightHandSide();
 
 	public boolean isRightHandSideRequired();
 
 	// Ugh!  This part will unfortunately be SQL specific :( ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String[] resolveAliasedLeftHandSideJoinConditionColumns(String leftHandSideTableAlias);
 	public String[] resolveNonAliasedRightHandSideJoinConditionColumns();
 	public String getAnyAdditionalJoinConditions(String rhsTableAlias);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/JoinDefinedByMetadata.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/JoinDefinedByMetadata.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/JoinDefinedByMetadata.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/JoinDefinedByMetadata.java
index a9e70ff24d..3dbaa717b5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/JoinDefinedByMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/JoinDefinedByMetadata.java
@@ -1,43 +1,43 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.type.Type;
 
 /**
  * Specialization of a Join that is defined by the metadata.
  *
  * @author Steve Ebersole
  */
 public interface JoinDefinedByMetadata extends Join {
 	/**
 	 * Obtain the name of the property that defines the join, relative to the PropertyMapping
 	 * ({@link QuerySpace#toAliasedColumns(String, String)}) of the left-hand-side
 	 * ({@link #getLeftHandSide()}) of the join
 	 *
 	 * @return The property name
 	 */
 	public String getJoinedPropertyName();
 	public Type getJoinedPropertyType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/LoadPlan.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
similarity index 76%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/LoadPlan.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
index 5ca4ff4726..f55556e98e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/LoadPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
@@ -1,139 +1,139 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import java.util.List;
 
 /**
  * Describes a plan for performing a load of results.
  *
  * Generally speaking there are 3 forms of load plans:<ul>
  *     <li>
- *         {@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#ENTITY_LOADER} - An entity load plan for
- *         handling get/load handling.  This form will typically have a single return (of type {@link org.hibernate.loader.plan2.spi.EntityReturn})
+ *         {@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#ENTITY_LOADER} - An entity load plan for
+ *         handling get/load handling.  This form will typically have a single return (of type {@link org.hibernate.loader.plan.spi.EntityReturn})
  *         defined by {@link #getReturns()}, possibly defining fetches.
  *     </li>
  *     <li>
- *         {@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#COLLECTION_INITIALIZER} - A collection initializer,
+ *         {@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#COLLECTION_INITIALIZER} - A collection initializer,
  *         used to load the contents of a collection.  This form will typically have a single return (of
- *         type {@link org.hibernate.loader.plan2.spi.CollectionReturn}) defined by {@link #getReturns()}, possibly defining fetches
+ *         type {@link org.hibernate.loader.plan.spi.CollectionReturn}) defined by {@link #getReturns()}, possibly defining fetches
  *     </li>
  *     <li>
- *         {@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#MIXED} - A query load plan which can contain
- *         multiple returns of mixed type (though all implementing {@link org.hibernate.loader.plan2.spi.Return}).  Again, may possibly define fetches.
+ *         {@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#MIXED} - A query load plan which can contain
+ *         multiple returns of mixed type (though all implementing {@link org.hibernate.loader.plan.spi.Return}).  Again, may possibly define fetches.
  *     </li>
  * </ul>
  * <p/>
  * todo : would also like to see "call back" style access for handling "subsequent actions" such as...<ul>
  *     <li>follow-on locking</li>
  *     <li>join fetch conversions to subselect fetches</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public interface LoadPlan {
 
 	/**
 	 * What is the disposition of this LoadPlan, in terms of its returns.
 	 *
 	 * @return The LoadPlan's disposition
 	 */
 	public Disposition getDisposition();
 
 	/**
 	 * Get the returns indicated by this LoadPlan.<ul>
 	 *     <li>
-	 *         A {@link Disposition#ENTITY_LOADER} LoadPlan would have just a single Return of type {@link org.hibernate.loader.plan2.spi.EntityReturn}.
+	 *         A {@link Disposition#ENTITY_LOADER} LoadPlan would have just a single Return of type {@link org.hibernate.loader.plan.spi.EntityReturn}.
 	 *     </li>
 	 *     <li>
 	 *         A {@link Disposition#COLLECTION_INITIALIZER} LoadPlan would have just a single Return of type
-	 *         {@link org.hibernate.loader.plan2.spi.CollectionReturn}.
+	 *         {@link org.hibernate.loader.plan.spi.CollectionReturn}.
 	 *     </li>
 	 *     <li>
-	 *         A {@link Disposition#MIXED} LoadPlan would contain a mix of {@link org.hibernate.loader.plan2.spi.EntityReturn} and
-	 *         {@link org.hibernate.loader.plan2.spi.ScalarReturn} elements, but no {@link org.hibernate.loader.plan2.spi.CollectionReturn}.
+	 *         A {@link Disposition#MIXED} LoadPlan would contain a mix of {@link org.hibernate.loader.plan.spi.EntityReturn} and
+	 *         {@link org.hibernate.loader.plan.spi.ScalarReturn} elements, but no {@link org.hibernate.loader.plan.spi.CollectionReturn}.
 	 *     </li>
 	 * </ul>
 	 *
 	 * @return The Returns for this LoadPlan.
 	 *
 	 * @see Disposition
 	 */
 	public List<? extends Return> getReturns();
 
 	/**
 	 * todo : document this...
 	 * <p/>
-	 * this is the stuff that was added in this plan2 package...  splitting the "from clause" and "select clause"
+	 * this is the stuff that was added in this plan package...  splitting the "from clause" and "select clause"
 	 * graphs and removing all (started) SQL references.  QuerySpaces represents the "from clause".  The
 	 * "select clause" is represented by {@link #getReturns()}.
 	 *
 	 * @return The QuerySpaces
 	 */
 	public QuerySpaces getQuerySpaces();
 
 	/**
 	 * Does this load plan indicate that lazy attributes are to be force fetched?
 	 * <p/>
 	 * Here we are talking about laziness in regards to the legacy bytecode enhancement which adds support for
 	 * partial selects of an entity's state (e.g., skip loading a lob initially, wait until/if it is needed)
 	 * <p/>
 	 * This one would effect the SQL that needs to get generated as well as how the result set would be read.
 	 * Therefore we make this part of the LoadPlan contract.
 	 * <p/>
 	 * NOTE that currently this is only relevant for HQL loaders when the HQL has specified the {@code FETCH ALL PROPERTIES}
 	 * key-phrase.  In all other cases, this returns false.
 
 	 * @return Whether or not to
 	 */
 	public boolean areLazyAttributesForceFetched();
 
 	/**
 	 * Convenient form of checking {@link #getReturns()} for scalar root returns.
 	 *
 	 * @return {@code true} if {@link #getReturns()} contained any scalar returns; {@code false} otherwise.
 	 */
 	public boolean hasAnyScalarReturns();
 
 	/**
 	 * Enumerated possibilities for describing the disposition of this LoadPlan.
 	 */
 	public static enum Disposition {
 		/**
 		 * This is an "entity loader" load plan, which describes a plan for loading one or more entity instances of
-		 * the same entity type.  There is a single return, which will be of type {@link org.hibernate.loader.plan2.spi.EntityReturn}
+		 * the same entity type.  There is a single return, which will be of type {@link org.hibernate.loader.plan.spi.EntityReturn}
 		 */
 		ENTITY_LOADER,
 		/**
 		 * This is a "collection initializer" load plan, which describes a plan for loading one or more entity instances of
-		 * the same collection type.  There is a single return, which will be of type {@link org.hibernate.loader.plan2.spi.CollectionReturn}
+		 * the same collection type.  There is a single return, which will be of type {@link org.hibernate.loader.plan.spi.CollectionReturn}
 		 */
 		COLLECTION_INITIALIZER,
 		/**
-		 * We have a mixed load plan, which will have one or more returns of {@link org.hibernate.loader.plan2.spi.EntityReturn} and {@link org.hibernate.loader.plan2.spi.ScalarReturn}
-		 * (NOT {@link org.hibernate.loader.plan2.spi.CollectionReturn}).
+		 * We have a mixed load plan, which will have one or more returns of {@link org.hibernate.loader.plan.spi.EntityReturn} and {@link org.hibernate.loader.plan.spi.ScalarReturn}
+		 * (NOT {@link org.hibernate.loader.plan.spi.CollectionReturn}).
 		 */
 		MIXED
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpace.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpace.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpace.java
index f8d160232b..a249706ac4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpace.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpace.java
@@ -1,109 +1,109 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.persister.entity.PropertyMapping;
 
 /**
  * Defines a persister reference (either entity or collection) or a composite reference. In JPA terms this is
  * an "abstract schema type" when discussing JPQL or JPA Criteria queries.  This models a single source of attributes
  * (and fetches).
  *
  * @author Steve Ebersole
  */
 public interface QuerySpace {
 	/**
 	 * The uid/alias which uniquely identifies this QuerySpace.  Can be used to uniquely reference this
 	 * QuerySpace elsewhere.
 	 *
 	 * @return The uid
 	 *
 	 * @see QuerySpaces#findQuerySpaceByUid(java.lang.String)
 	 */
 	public String getUid();
 
 	/**
 	 * Get the QuerySpaces object that is our owner.
 	 *
 	 * @return The QuerySpaces containing this QuerySpace
 	 */
 	public QuerySpaces getQuerySpaces();
 
 	/**
 	 * Get the PropertyMapping for this QuerySpace.
 	 *
 	 * @return The PropertyMapping
 	 */
 	public PropertyMapping getPropertyMapping();
 
 
 	/**
 	 * Get the aliased column names for the specified property in the query space..
 	 *
 	 * @return the aliased column names for the specified property
 	 * @param alias - the table alias
 	 * @param propertyName - the property name
 	 */
 	public String[] toAliasedColumns(String alias, String propertyName);
 
 	/**
 	 * Enumeration of the different types of QuerySpaces we can have.
 	 */
 	public static enum Disposition {
 		// todo : account for special distinctions too like COLLECTION INDEX/ELEMENT too?
 		/**
 		 * We have an entity-based QuerySpace.  It is castable to {@link EntityQuerySpace} for more details.
 		 */
 		ENTITY,
 		/**
 		 * We have a collection-based QuerySpace.  It is castable to {@link CollectionQuerySpace} for more details.
 		 */
 		COLLECTION,
 		/**
 		 * We have a composition-based QuerySpace.  It is castable to {@link CompositeQuerySpace} for more details.
 		 */
 		COMPOSITE
 	}
 
 	/**
 	 * What type of QuerySpace (more-specific) is this?
 	 *
 	 * @return The enum value representing the more-specific type of QuerySpace
 	 */
 	public Disposition getDisposition();
 
 	/**
 	 * Obtain all joins which originate from this QuerySpace, in other words, all the joins which this QuerySpace is
 	 * the right-hand-side of.
 	 * <p/>
 	 * For all the joins returned here, {@link Join#getRightHandSide()} should point back to this QuerySpace such that
 	 * <code>
 	 *     space.getJoins().forEach{ join -> join.getRightHandSide() == space }
 	 * </code>
 	 * is true for all.
 	 *
 	 * @return The joins which originate from this query space.
 	 */
 	public Iterable<Join> getJoins();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaceUidNotRegisteredException.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpaceUidNotRegisteredException.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaceUidNotRegisteredException.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpaceUidNotRegisteredException.java
index 7992526d5f..594dfcbad8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaceUidNotRegisteredException.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpaceUidNotRegisteredException.java
@@ -1,45 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.HibernateException;
 
 /**
  * Indicates an attempt to lookup a QuerySpace by its uid, when no registration has been made under that uid.
  *
  * @author Steve Ebersole
  */
 public class QuerySpaceUidNotRegisteredException extends HibernateException {
 	public QuerySpaceUidNotRegisteredException(String uid) {
 		super( generateMessage( uid ) );
 	}
 
 	private static String generateMessage(String uid) {
 		return "Given uid [" + uid + "] could not be resolved to QuerySpace";
 	}
 
 	public QuerySpaceUidNotRegisteredException(String uid, Throwable cause) {
 		super( generateMessage( uid ), cause );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaces.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpaces.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaces.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpaces.java
index e2b3e74ec2..722011984e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaces.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/QuerySpaces.java
@@ -1,67 +1,64 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import java.util.List;
 
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.internal.SessionFactoryImpl;
-
 /**
  * Models a collection of {@link QuerySpace} references and exposes the ability to find a {@link QuerySpace} by its UID
  * <p/>
  * todo : make this hierarchical... that would be needed to truly work for hql parser
  *
  * @author Steve Ebersole
  */
 public interface QuerySpaces {
 	/**
 	 * Gets the root QuerySpace references.
 	 *
 	 * @return The roots
 	 */
 	public List<QuerySpace> getRootQuerySpaces();
 
 	/**
 	 * Locate a QuerySpace by its uid.
 	 *
 	 * @param uid The QuerySpace uid to match
 	 *
 	 * @return The match, {@code null} is returned if no match.
 	 *
 	 * @see QuerySpace#getUid()
 	 */
 	public QuerySpace findQuerySpaceByUid(String uid);
 
 	/**
 	 * Like {@link #findQuerySpaceByUid}, except that here an exception is thrown if the uid cannot be resolved.
 	 *
 	 * @param uid The uid to resolve
 	 *
 	 * @return The QuerySpace
 	 *
 	 * @throws QuerySpaceUidNotRegisteredException Rather than return {@code null}
 	 */
 	public QuerySpace getQuerySpaceByUid(String uid);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Return.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Return.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
index 9fc0cc8074..3f1b32d6a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Return.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
@@ -1,38 +1,38 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 /**
  * Represents a return value in the query results.  Not the same as a result (column) in the JDBC ResultSet!
  * <p/>
- * Return is distinctly different from a {@link org.hibernate.loader.plan2.spi.Fetch} and so modeled as completely separate hierarchy.
+ * Return is distinctly different from a {@link org.hibernate.loader.plan.spi.Fetch} and so modeled as completely separate hierarchy.
  *
  * @see ScalarReturn
  * @see EntityReturn
  * @see CollectionReturn
  *
  * @author Steve Ebersole
  */
 public interface Return {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/ScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/ScalarReturn.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
index 6104dadc8b..bb24104e28 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/ScalarReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
@@ -1,52 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.loader.plan2.spi;
+package org.hibernate.loader.plan.spi;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Represent a simple scalar return within a query result.  Generally this would be values of basic (String, Integer,
  * etc) or composite types.
  *
  * @author Steve Ebersole
  */
 public class ScalarReturn extends AbstractPlanNode implements Return {
 	private final String name;
 	private final Type type;
 
 	public ScalarReturn(String name, Type type, SessionFactoryImplementor factory) {
 		super( factory );
 		this.name = name;
 		this.type = type;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Type getType() {
 		return type;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
index 4d83bcbaa3..62ef6d2d56 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
@@ -1,63 +1,63 @@
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
 package org.hibernate.persister.entity;
 import org.hibernate.QueryException;
 import org.hibernate.type.Type;
 
 /**
  * Contract for all things that know how to map a property to the needed bits of SQL.
  * <p/>
  * The column/formula fragments that represent a property in the table defining the property be obtained by
  * calling either {@link #toColumns(String, String)} or {@link #toColumns(String)} to obtain SQL-aliased
  * column/formula fragments aliased or un-aliased, respectively.
  *
  *
  * <p/>
  * Note, the methods here are generally ascribed to accept "property paths".  That is a historical necessity because
  * of how Hibernate originally understood composites (embeddables) internally.  That is in the process of changing
- * as Hibernate has added {@link org.hibernate.loader.plan2.build.internal.spaces.CompositePropertyMapping}
+ * as Hibernate has added {@link org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface PropertyMapping {
 	/**
 	 * Given a component path expression, get the type of the property
 	 */
 	public Type toType(String propertyName) throws QueryException;
 
 	/**
 	 * Obtain aliased column/formula fragments for the specified property path.
 	 */
 	public String[] toColumns(String alias, String propertyName) throws QueryException;
 	/**
 	 * Given a property path, return the corresponding column name(s).
 	 */
 	public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException;
 	/**
 	 * Get the type of the thing containing the properties
 	 */
 	public Type getType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
index 8810c83524..2b2816ddad 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
@@ -1,175 +1,175 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.persister.walking.spi;
 
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.spi.FetchSource;
 
 /**
  * Strategy for walking associations as defined by the Hibernate metamodel.  Is essentially a callback listener for
  * interesting events while walking a metamodel graph
  * <p/>
  * {@link #start()} and {@link #finish()} are called at the start and at the finish of the process.
  * <p/>
  * Walking might start with an entity or a collection depending on where the walker is asked to start.  When starting
  * with an entity, {@link #startingEntity}/{@link #finishingEntity} ()} will be the outer set of calls.  When starting
  * with a collection, {@link #startingCollection}/{@link #finishingCollection} will be the outer set of calls.
  *
  * @author Steve Ebersole
  */
 public interface AssociationVisitationStrategy {
 	/**
 	 * Notification we are preparing to start visitation.
 	 */
 	public void start();
 
 	/**
 	 * Notification we are finished visitation.
 	 */
 	public void finish();
 
 	/**
 	 * Notification we are starting to walk an entity.
 	 *
 	 * @param entityDefinition The entity we are preparing to walk
 	 */
 	public void startingEntity(EntityDefinition entityDefinition);
 
 	/**
 	 * Notification we are finishing walking an entity.
 	 *
 	 * @param entityDefinition The entity we are finishing walking.
 	 */
 	public void finishingEntity(EntityDefinition entityDefinition);
 
 	/**
 	 * Notification we are starting to walk the identifier of an entity.
 	 *
 	 * @param entityIdentifierDefinition The identifier we are preparing to walk
 	 */
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
 
 	/**
 	 * Notification we are finishing walking an entity.
 	 *
 	 * @param entityIdentifierDefinition The identifier we are finishing walking.
 	 */
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
 
 	/**
 	 * Notification that we are starting to walk a collection
 	 *
 	 * @param collectionDefinition The collection we are preparing to walk
 	 */
 	public void startingCollection(CollectionDefinition collectionDefinition);
 
 	/**
 	 * Notification that we are finishing walking a collection
 	 *
 	 * @param collectionDefinition The collection we are finishing
 	 */
 	public void finishingCollection(CollectionDefinition collectionDefinition);
 
 	/**
 	 * Notification that we are starting to walk the index of a collection (List/Map).  In the case of a Map,
 	 * if the indices (the keys) are entities this will be followed up by a call to {@link #startingEntity}
 	 *
 	 * @param collectionIndexDefinition The collection index we are preparing to walk.
 	 */
 	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
 
 	/**
 	 * Notification that we are finishing walking the index of a collection (List/Map).
 	 *
 	 * @param collectionIndexDefinition The collection index we are finishing
 	 */
 	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
 
 	/**
 	 * Notification that we are starting to look at the element definition for the collection.  If the collection
 	 * elements are entities this will be followed up by a call to {@link #startingEntity}
 	 *
 	 * @param elementDefinition The collection element we are preparing to walk..
 	 */
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition);
 
 	/**
 	 * Notification that we are finishing walking the elements of a collection (List/Map).
 	 *
 	 * @param elementDefinition The collection element we are finishing
 	 */
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition);
 
 	/**
 	 * Notification that we are preparing to walk a composite.  This is called only for:<ul>
 	 *     <li>
 	 *         top-level composites for entity attributes. composite entity identifiers do not route through here, see
 	 *         {@link #startingEntityIdentifier} if you need to hook into walking the top-level cid composite.
 	 *     </li>
 	 *     <li>
 	 *         All forms of nested composite paths
 	 *     </li>
 	 * </ul>
 	 *
 	 * @param compositionDefinition The composite we are preparing to walk.
 	 */
 	public void startingComposite(CompositionDefinition compositionDefinition);
 
 	/**
 	 * Notification that we are done walking a composite.  Called on the back-end of the situations listed
 	 * on {@link #startingComposite}
 	 *
 	 * @param compositionDefinition The composite we are finishing
 	 */
 	public void finishingComposite(CompositionDefinition compositionDefinition);
 
 	// get rid of these ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 //	public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition);
 //	public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition);
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Notification that we are preparing to walk an attribute.  May be followed by calls to {@link #startingEntity}
 	 * (one-to-one, many-to-one), {@link #startingComposite}, or {@link #startingCollection}.
 	 *
 	 * @param attributeDefinition The attribute we are preparing to walk.
 	 *
 	 * @return {@code true} if the walking should continue; {@code false} if walking should stop.
 	 */
 	public boolean startingAttribute(AttributeDefinition attributeDefinition);
 
 	/**
 	 * Notification that we are finishing walking an attribute.
 	 *
 	 * @param attributeDefinition The attribute we are done walking
 	 */
 	public void finishingAttribute(AttributeDefinition attributeDefinition);
 
 	public void foundAny(AnyMappingDefinition anyDefinition);
 
 	public void associationKeyRegistered(AssociationKey associationKey);
 	public FetchSource registeredFetchSource(AssociationKey associationKey);
 	public void foundCircularAssociation(AssociationAttributeDefinition attributeDefinition);
 	public boolean isDuplicateAssociationKey(AssociationKey associationKey);
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanBuilderTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanBuilderTest.java
index 3d12404867..78023d18d3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanBuilderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanBuilderTest.java
@@ -1,162 +1,162 @@
 /*
  * jDocBook, processing of DocBook sources
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.plans;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.util.List;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
-import org.hibernate.loader.plan2.build.internal.CascadeStyleLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.spi.LoadPlanTreePrinter;
-import org.hibernate.loader.plan2.build.spi.MetamodelDrivenLoadPlanBuilder;
-import org.hibernate.loader.plan2.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan2.spi.CollectionReturn;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReference;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.Fetch;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.loader.plan.build.internal.CascadeStyleLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.spi.LoadPlanTreePrinter;
+import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class LoadPlanBuilderTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class };
 	}
 
 	@Test
 	public void testSimpleBuild() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
 				sessionFactory(),
 				LoadQueryInfluencers.NONE,
 				LockMode.NONE
 		);
 		LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 
 		LoadPlanTreePrinter.INSTANCE.logTree( plan, new AliasResolutionContextImpl( sessionFactory() ) );
 	}
 
 	@Test
 	public void testCascadeBasedBuild() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		CascadeStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new CascadeStyleLoadPlanBuildingAssociationVisitationStrategy(
 				CascadingActions.MERGE,
 				sessionFactory(),
 				LoadQueryInfluencers.NONE,
 				LockMode.NONE
 		);
 		LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 
 		LoadPlanTreePrinter.INSTANCE.logTree( plan, new AliasResolutionContextImpl( sessionFactory() ) );
 	}
 
 	@Test
 	public void testCollectionInitializerCase() {
 		CollectionPersister cp = sessionFactory().getCollectionPersister( Poster.class.getName() + ".messages" );
 		FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
 				sessionFactory(),
 				LoadQueryInfluencers.NONE,
 				LockMode.NONE
 		);
 		LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootCollectionLoadPlan( strategy, cp );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		CollectionReturn collectionReturn = ExtraAssertions.assertTyping( CollectionReturn.class, rtn );
 
 		assertNotNull( collectionReturn.getElementGraph() );
 		assertNotNull( collectionReturn.getElementGraph().getFetches() );
 		// the collection Message elements are fetched, but Message.poster is not fetched
 		// (because that collection is owned by that Poster)
 		assertEquals( 0, collectionReturn.getElementGraph().getFetches().length );
 		EntityReference entityReference = ExtraAssertions.assertTyping( EntityReference.class, collectionReturn.getElementGraph() );
 		assertNotNull( entityReference.getFetches() );
 		assertEquals( 0, entityReference.getFetches().length );
 
 		LoadPlanTreePrinter.INSTANCE.logTree( plan, new AliasResolutionContextImpl( sessionFactory() ) );
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne( cascade = CascadeType.MERGE )
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionHelper.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionHelper.java
index 651c48441f..4b7e4532e2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionHelper.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionHelper.java
@@ -1,139 +1,139 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.plans;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.JoinWalker;
 import org.hibernate.loader.entity.EntityJoinWalker;
-import org.hibernate.loader.plan2.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.spi.MetamodelDrivenLoadPlanBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.exec.spi.EntityLoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 /**
  * Perform assertions based on a LoadPlan, specifically against the outputs/expectations of the legacy Loader approach.
  * <p/>
  * Mainly this is intended to be a transitory set of help since it is expected that Loader will go away replaced by
  * LoadPlans, QueryBuilders and ResultSetProcessors.  For now I want to make sure that the outputs (e.g., the SQL,
  * the extraction aliases) are the same given the same input.  That makes sure we have the best possibility of success
  * in designing and implementing the "replacement parts".
  *
  * @author Steve Ebersole
  */
 public class LoadPlanStructureAssertionHelper {
 	/**
 	 * Singleton access to the helper
 	 */
 	public static final LoadPlanStructureAssertionHelper INSTANCE = new LoadPlanStructureAssertionHelper();
 
 	/**
 	 * Performs a basic comparison.  Builds a LoadPlan for the given persister and compares it against the
 	 * expectations according to the Loader/Walker corollary.
 	 *
 	 * @param sf The SessionFactory
 	 * @param persister The entity persister for which to build a LoadPlan and compare against the Loader/Walker
 	 * expectations.
 	 */
 	public void performBasicComparison(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
 		// todo : allow these to be passed in by tests?
 		final LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
 		final LockMode lockMode = LockMode.NONE;
 		final int batchSize = 1;
 
 		// legacy Loader-based contracts...
 		final EntityJoinWalker walker = new EntityJoinWalker(
 				persister,
 				persister.getKeyColumnNames(),
 				batchSize,
 				lockMode,
 				sf,
 				influencers
 		);
 //		final EntityLoader loader = new EntityLoader( persister, lockMode, sf, influencers );
 
 		LoadPlan plan = buildLoadPlan( sf, persister, influencers, lockMode );
 		EntityLoadQueryDetails details = EntityLoadQueryDetails.makeForBatching(
 				plan, persister.getKeyColumnNames(),
 				new QueryBuildingParameters() {
 					@Override
 					public LoadQueryInfluencers getQueryInfluencers() {
 						return influencers;
 					}
 
 					@Override
 					public int getBatchSize() {
 						return batchSize;
 					}
 
 					@Override
 					public LockMode getLockMode() {
 						return lockMode;
 					}
 
 					@Override
 					public LockOptions getLockOptions() {
 						return null;
 					}
 				}, sf
 		);
 
 		compare( walker, details );
 	}
 
 	public LoadPlan buildLoadPlan(
 			SessionFactoryImplementor sf,
 			OuterJoinLoadable persister,
 			LoadQueryInfluencers influencers,
 			LockMode lockMode) {
 		FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
 				sf,
 				influencers,
 				lockMode
 				);
 		return MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, persister );
 	}
 
 	public LoadPlan buildLoadPlan(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
 		return buildLoadPlan( sf, persister, LoadQueryInfluencers.NONE, LockMode.NONE );
 	}
 
 	private void compare(JoinWalker walker, EntityLoadQueryDetails details) {
 		System.out.println( "------ SQL -----------------------------------------------------------------" );
 		System.out.println( "WALKER    : " + walker.getSQLString() );
 		System.out.println( "LOAD-PLAN : " + details.getSqlStatement() );
 		System.out.println( "----------------------------------------------------------------------------" );
 		System.out.println( );
 		System.out.println( "------ SUFFIXES ------------------------------------------------------------" );
 		System.out.println( "WALKER    : " + StringHelper.join( ", ",  walker.getSuffixes() ) + " : "
 									+ StringHelper.join( ", ", walker.getCollectionSuffixes() ) );
 		System.out.println( "----------------------------------------------------------------------------" );
 		System.out.println( );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionTest.java
index b392e60f61..d13e5d874d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/plans/LoadPlanStructureAssertionTest.java
@@ -1,285 +1,285 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.plans;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
-import org.hibernate.loader.plan2.spi.BidirectionalEntityReference;
-import org.hibernate.loader.plan2.spi.CollectionAttributeFetch;
+import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
+import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
 import org.hibernate.test.annotations.Country;
 import org.hibernate.test.annotations.collectionelement.Boy;
 import org.hibernate.test.annotations.collectionelement.Matrix;
 import org.hibernate.test.annotations.collectionelement.TestCourse;
 import org.hibernate.test.loadplans.process.EncapsulatedCompositeIdResultSetProcessorTest;
 
-//import org.hibernate.loader.plan2.spi.BidirectionalEntityFetch;
-import org.hibernate.loader.plan2.build.internal.returns.CollectionFetchableElementEntityGraph;
-import org.hibernate.loader.plan2.spi.EntityFetch;
-import org.hibernate.loader.plan2.spi.EntityReturn;
-import org.hibernate.loader.plan2.spi.FetchSource;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+//import org.hibernate.loader.plan.spi.BidirectionalEntityFetch;
+import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableElementEntityGraph;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.FetchSource;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.annotations.cid.keymanytoone.Card;
 import org.hibernate.test.annotations.cid.keymanytoone.CardField;
 import org.hibernate.test.annotations.cid.keymanytoone.Key;
 import org.hibernate.test.annotations.cid.keymanytoone.PrimaryKey;
 
 import static junit.framework.Assert.assertNotNull;
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Used to assert that "fetch graphs" between JoinWalker and LoadPlan are same.
  *
  * @author Steve Ebersole
  */
 public class LoadPlanStructureAssertionTest extends BaseUnitTestCase {
 	@Test
 	public void testJoinedOneToOne() {
 		// tests the mappings defined in org.hibernate.test.onetoone.joined.JoinedSubclassOneToOneTest
 		Configuration cfg = new Configuration();
 		cfg.addResource( "org/hibernate/test/onetoone/joined/Person.hbm.xml" );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 //			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.joined.Person.class ) );
 			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.joined.Entity.class ) );
 
 //			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.joined.Address.class ) );
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testSpecialOneToOne() {
 		// tests the mappings defined in org.hibernate.test.onetoone.joined.JoinedSubclassOneToOneTest
 		Configuration cfg = new Configuration();
 		cfg.addResource( "org/hibernate/test/onetoone/formula/Person.hbm.xml" );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.formula.Person.class ) );
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testEncapsulatedCompositeIdNoFetches1() {
 		// CardField is an entity with a composite identifier mapped via a @EmbeddedId class (CardFieldPK) defining
 		// a @ManyToOne
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( EncapsulatedCompositeIdResultSetProcessorTest.CardField.class );
 		cfg.addAnnotatedClass( EncapsulatedCompositeIdResultSetProcessorTest.Card.class );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( EncapsulatedCompositeIdResultSetProcessorTest.CardField.class ) );
 			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( EncapsulatedCompositeIdResultSetProcessorTest.Card.class ) );
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testEncapsulatedCompositeIdNoFetches2() {
 		// Parent is an entity with a composite identifier mapped via a @EmbeddedId class (ParentPK) which is defined
 		// using just basic types (strings, ints, etc)
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( EncapsulatedCompositeIdResultSetProcessorTest.Parent.class );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( EncapsulatedCompositeIdResultSetProcessorTest.Parent.class ) );
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testEncapsulatedCompositeIdWithFetches1() {
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Card.class );
 		cfg.addAnnotatedClass( CardField.class );
 		cfg.addAnnotatedClass( Key.class );
 		cfg.addAnnotatedClass( PrimaryKey.class );
 
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 			final OuterJoinLoadable cardFieldPersister = (OuterJoinLoadable) sf.getClassMetadata( CardField.class );
 			doCompare( sf, cardFieldPersister );
 
 			final LoadPlan loadPlan = LoadPlanStructureAssertionHelper.INSTANCE.buildLoadPlan( sf, cardFieldPersister );
 			assertEquals( LoadPlan.Disposition.ENTITY_LOADER, loadPlan.getDisposition() );
 			assertEquals( 1, loadPlan.getReturns().size() );
 			final EntityReturn cardFieldReturn = assertTyping( EntityReturn.class, loadPlan.getReturns().get( 0 ) );
 			assertEquals( 0, cardFieldReturn.getFetches().length );
 
 			// CardField defines a composite pk with 2 fetches : Card and Key (the id description acts as the composite)
 			assertTrue( cardFieldReturn.getIdentifierDescription().hasFetches() );
 			final FetchSource cardFieldIdAsFetchSource = assertTyping( FetchSource.class, cardFieldReturn.getIdentifierDescription() );
 			assertEquals( 2, cardFieldIdAsFetchSource.getFetches().length );
 
 			// First the key-many-to-one to Card...
 			final EntityFetch cardFieldIdCardFetch = assertTyping(
 					EntityFetch.class,
 					cardFieldIdAsFetchSource.getFetches()[0]
 			);
 			assertFalse( cardFieldIdCardFetch.getIdentifierDescription().hasFetches() );
 			// i think this one might be a mistake; i think the collection reader still needs to be registered.  Its zero
 			// because the inverse of the key-many-to-one already had a registered AssociationKey and so saw the
 			// CollectionFetch as a circularity (I think)
 			assertEquals( 0, cardFieldIdCardFetch.getFetches().length );
 
 			// then the Key..
 			final EntityFetch cardFieldIdKeyFetch = assertTyping(
 					EntityFetch.class,
 					cardFieldIdAsFetchSource.getFetches()[1]
 			);
 			assertFalse( cardFieldIdKeyFetch.getIdentifierDescription().hasFetches() );
 			assertEquals( 0, cardFieldIdKeyFetch.getFetches().length );
 
 
 			// we need the readers ordered in a certain manner.  Here specifically: Fetch(Card), Fetch(Key), Return(CardField)
 			//
 			// additionally, we need Fetch(Card) and Fetch(Key) to be hydrated/semi-resolved before attempting to
 			// resolve the EntityKey for Return(CardField)
 			//
 			// together those sound like argument enough to continue keeping readers for "identifier fetches" as part of
 			// a special "identifier reader".  generated aliases could help here too to remove cyclic-ness from the graph.
 			// but at any rate, we need to know still when this becomes circularity
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testEncapsulatedCompositeIdWithFetches2() {
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Card.class );
 		cfg.addAnnotatedClass( CardField.class );
 		cfg.addAnnotatedClass( Key.class );
 		cfg.addAnnotatedClass( PrimaryKey.class );
 
 		final SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 			final OuterJoinLoadable cardPersister = (OuterJoinLoadable) sf.getClassMetadata( Card.class );
 			doCompare( sf, cardPersister );
 
 			final LoadPlan cardLoadPlan = LoadPlanStructureAssertionHelper.INSTANCE.buildLoadPlan( sf, cardPersister );
 			assertEquals( LoadPlan.Disposition.ENTITY_LOADER, cardLoadPlan.getDisposition() );
 			assertEquals( 1, cardLoadPlan.getReturns().size() );
 
 			// Check the root EntityReturn(Card)
 			final EntityReturn cardReturn = assertTyping( EntityReturn.class, cardLoadPlan.getReturns().get( 0 ) );
 			assertFalse( cardReturn.getIdentifierDescription().hasFetches() );
 
 			// Card should have one fetch, the fields collection
 			assertEquals( 1, cardReturn.getFetches().length );
 			final CollectionAttributeFetch fieldsFetch = assertTyping( CollectionAttributeFetch.class, cardReturn.getFetches()[0] );
 			assertNotNull( fieldsFetch.getElementGraph() );
 
 			// the Card.fields collection has entity elements of type CardField...
 			final CollectionFetchableElementEntityGraph cardFieldElementGraph = assertTyping( CollectionFetchableElementEntityGraph.class, fieldsFetch.getElementGraph() );
 			// CardField should have no fetches
 			assertEquals( 0, cardFieldElementGraph.getFetches().length );
 			// But it should have 1 key-many-to-one fetch for Key (Card is already handled)
 			assertTrue( cardFieldElementGraph.getIdentifierDescription().hasFetches() );
 			final FetchSource cardFieldElementGraphIdAsFetchSource = assertTyping(
 					FetchSource.class,
 					cardFieldElementGraph.getIdentifierDescription()
 			);
 			assertEquals( 1, cardFieldElementGraphIdAsFetchSource.getFetches().length );
 			assertEquals( 1, cardFieldElementGraphIdAsFetchSource.getBidirectionalEntityReferences().length );
 
 			BidirectionalEntityReference circularCardFetch = assertTyping(
 					BidirectionalEntityReference.class,
 					cardFieldElementGraphIdAsFetchSource.getBidirectionalEntityReferences()[0]
 			);
 			assertSame( circularCardFetch.getTargetEntityReference(), cardReturn );
 
 			// the fetch above is to the other key-many-to-one for CardField.primaryKey composite: key
 			EntityFetch keyFetch = assertTyping(
 					EntityFetch.class,
 					cardFieldElementGraphIdAsFetchSource.getFetches()[0]
 			);
 			assertEquals( Key.class.getName(), keyFetch.getEntityPersister().getEntityName() );
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testManyToMany() {
 		Configuration cfg = new Configuration();
 		cfg.addResource( "org/hibernate/test/immutable/entitywithmutablecollection/inverse/ContractVariation.hbm.xml" );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.immutable.entitywithmutablecollection.Contract.class ) );
 		}
 		finally {
 			sf.close();
 		}
 
 	}
 
 	@Test
 	public void testAnotherBasicCollection() {
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Boy.class );
 		cfg.addAnnotatedClass( Country.class );
 		cfg.addAnnotatedClass( TestCourse.class );
 		cfg.addAnnotatedClass( Matrix.class );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		try {
 			doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( Boy.class ) );
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	private void doCompare(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
 		LoadPlanStructureAssertionHelper.INSTANCE.performBasicComparison( sf, persister );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeAttributeResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeAttributeResultSetProcessorTest.java
index 499ce5d6cf..ec8abda0ca 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeAttributeResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeAttributeResultSetProcessorTest.java
@@ -1,373 +1,373 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import javax.persistence.CollectionTable;
 import javax.persistence.Column;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.Entity;
 import javax.persistence.EnumType;
 import javax.persistence.Enumerated;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.JoinWalker;
 import org.hibernate.loader.entity.EntityJoinWalker;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.EntityLoadQueryDetails;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Gail Badner
  */
 public class EncapsulatedCompositeAttributeResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Person.class, Customer.class };
 	}
 
 	@Test
 	public void testSimpleNestedCompositeAttributeProcessing() throws Exception {
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Person person = new Person();
 		person.id = 1;
 		person.name = "Joe Blow";
 		person.address = new Address();
 		person.address.address1 = "1313 Mockingbird Lane";
 		person.address.city = "Pleasantville";
 		person.address.country = "USA";
 		AddressType addressType = new AddressType();
 		addressType.typeName = "snail mail";
 		person.address.type = addressType;
 		session.save( person );
 		session.getTransaction().commit();
 		session.close();
 
 //		session = openSession();
 //		session.beginTransaction();
 //		Person personGotten = (Person) session.get( Person.class, person.id );
 //		assertEquals( person.id, personGotten.id );
 //		assertEquals( person.address.address1, personGotten.address.address1 );
 //		assertEquals( person.address.city, personGotten.address.city );
 //		assertEquals( person.address.country, personGotten.address.country );
 //		assertEquals( person.address.type.typeName, personGotten.address.type.typeName );
 //		session.getTransaction().commit();
 //		session.close();
 
 		List results = getResults( sessionFactory().getEntityPersister( Person.class.getName() ) );
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Person personWork = ExtraAssertions.assertTyping( Person.class, result );
 		assertEquals( person.id, personWork.id );
 		assertEquals( person.address.address1, personWork.address.address1 );
 		assertEquals( person.address.city, personWork.address.city );
 		assertEquals( person.address.country, personWork.address.country );
 		assertEquals( person.address.type.typeName, person.address.type.typeName );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete Person" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testNestedCompositeElementCollectionQueryBuilding() {
 		doCompare(
 				sessionFactory(),
 				(OuterJoinLoadable) sessionFactory().getClassMetadata( Customer.class )
 		);
 	}
 
 	private void doCompare(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
 		final LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
 		final LockMode lockMode = LockMode.NONE;
 		final int batchSize = 1;
 
 		final EntityJoinWalker walker = new EntityJoinWalker(
 				persister,
 				persister.getKeyColumnNames(),
 				batchSize,
 				lockMode,
 				sf,
 				influencers
 		);
 
 		final EntityLoadQueryDetails details = Helper.INSTANCE.buildLoadQueryDetails( persister, sf );
 
 		compare( walker, details );
 	}
 
 	private void compare(JoinWalker walker, EntityLoadQueryDetails details) {
 		System.out.println( "WALKER    : " + walker.getSQLString() );
 		System.out.println( "LOAD-PLAN : " + details.getSqlStatement() );
 		System.out.println();
 	}
 
 	@Test
 	public void testNestedCompositeElementCollectionProcessing() throws Exception {
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Person person = new Person();
 		person.id = 1;
 		person.name = "Joe Blow";
 		session.save( person );
 		Customer customer = new Customer();
 		customer.id = 1L;
 		Investment investment1 = new Investment();
 		investment1.description = "stock";
 		investment1.date = new Date();
 		investment1.monetaryAmount = new MonetaryAmount();
 		investment1.monetaryAmount.currency = MonetaryAmount.CurrencyCode.USD;
 		investment1.monetaryAmount.amount = BigDecimal.valueOf( 1234, 2 );
 		investment1.performedBy = person;
 		Investment investment2 = new Investment();
 		investment2.description = "bond";
 		investment2.date = new Date();
 		investment2.monetaryAmount = new MonetaryAmount();
 		investment2.monetaryAmount.currency = MonetaryAmount.CurrencyCode.EUR;
 		investment2.monetaryAmount.amount = BigDecimal.valueOf( 98176, 1 );
 		customer.investments.add( investment1 );
 		customer.investments.add( investment2 );
 		session.save( customer );
 		session.getTransaction().commit();
 		session.close();
 
 //		session = openSession();
 //		session.beginTransaction();
 //		Customer customerGotten = (Customer) session.get( Customer.class, customer.id );
 //		assertEquals( customer.id, customerGotten.id );
 //		session.getTransaction().commit();
 //		session.close();
 
 		List results = getResults( sessionFactory().getEntityPersister( Customer.class.getName() ) );
 
 		assertEquals( 2, results.size() );
 		assertSame( results.get( 0 ), results.get( 1 ) );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Customer customerWork = ExtraAssertions.assertTyping( Customer.class, result );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( customerWork.investments.get( 0 ).performedBy );
 		session.delete( customerWork );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private List<?> getResults(EntityPersister entityPersister ) {
 		final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 		final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 		final String sql = queryDetails.getSqlStatement();
 		final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 		final List results = new ArrayList();
 
 		final Session workSession = openSession();
 		workSession.beginTransaction();
 		workSession.doWork(
 				new Work() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						PreparedStatement ps = connection.prepareStatement( sql );
 						ps.setInt( 1, 1 );
 						ResultSet resultSet = ps.executeQuery();
 						results.addAll(
 								resultSetProcessor.extractResults(
 										resultSet,
 										(SessionImplementor) workSession,
 										new QueryParameters(),
 										new NamedParameterContext() {
 											@Override
 											public int[] getNamedParameterLocations(String name) {
 												return new int[0];
 											}
 										},
 										true,
 										false,
 										null,
 										null
 								)
 						);
 						resultSet.close();
 						ps.close();
 					}
 				}
 		);
 		workSession.getTransaction().commit();
 		workSession.close();
 		return results;
 	}
 
 	@Entity( name = "Person" )
 	public static class Person implements Serializable {
 		@Id
 		Integer id;
 		String name;
 
 		@Embedded
 		Address address;
 	}
 
 	@Embeddable
 	public static class Address implements Serializable {
 		String address1;
 		String city;
 		String country;
 		AddressType type;
 	}
 
 	@Embeddable
 	public static class AddressType {
 		String typeName;
 	}
 
 	@Entity( name = "Customer" )
 	public static class Customer {
 		private Long id;
 		private List<Investment> investments = new ArrayList<Investment>();
 
 		@Id
 		public Long getId() {
 			return id;
 		}
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		@ElementCollection(fetch = FetchType.EAGER)
 		@CollectionTable( name = "investments", joinColumns = @JoinColumn( name = "customer_id" ) )
 		public List<Investment> getInvestments() {
 			return investments;
 		}
 		public void setInvestments(List<Investment> investments) {
 			this.investments = investments;
 		}
 	}
 
 	@Embeddable
 	public static class Investment {
 		private MonetaryAmount monetaryAmount;
 		private String description;
 		private Date date;
 		private Person performedBy;
 
 		@Embedded
 		public MonetaryAmount getMonetaryAmount() {
 			return monetaryAmount;
 		}
 		public void setMonetaryAmount(MonetaryAmount monetaryAmount) {
 			this.monetaryAmount = monetaryAmount;
 		}
 		public String getDescription() {
 			return description;
 		}
 		public void setDescription(String description) {
 			this.description = description;
 		}
 		public Date getDate() {
 			return date;
 		}
 		public void setDate(Date date) {
 			this.date = date;
 		}
 		@ManyToOne
 		public Person getPerformedBy() {
 			return performedBy;
 		}
 		public void setPerformedBy(Person performedBy) {
 			this.performedBy = performedBy;
 		}
 	}
 
 	@Embeddable
 	public static class MonetaryAmount {
 		public static enum CurrencyCode {
 			USD,
 			EUR
 		}
 		private BigDecimal amount;
 		@Column(length = 3)
 		@Enumerated(EnumType.STRING)
 		private CurrencyCode currency;
 
 		public BigDecimal getAmount() {
 			return amount;
 		}
 		public void setAmount(BigDecimal amount) {
 			this.amount = amount;
 		}
 
 		public CurrencyCode getCurrency() {
 			return currency;
 		}
 		public void setCurrency(CurrencyCode currency) {
 			this.currency = currency;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeIdResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeIdResultSetProcessorTest.java
index 81a465fe5b..15fc3816a6 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeIdResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EncapsulatedCompositeIdResultSetProcessorTest.java
@@ -1,434 +1,434 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import javax.persistence.Embeddable;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.ManyToOne;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Gail Badner
  */
 public class EncapsulatedCompositeIdResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Parent.class, CardField.class, Card.class };
 	}
 
 	@Test
 	public void testSimpleCompositeId() throws Exception {
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Parent parent = new Parent();
 		parent.id = new ParentPK();
 		parent.id.firstName = "Joe";
 		parent.id.lastName = "Blow";
 		session.save( parent );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Parent parentGotten = (Parent) session.get( Parent.class, parent.id );
 		assertEquals( parent, parentGotten );
 		session.getTransaction().commit();
 		session.close();
 
 		final List results = getResults(
 				sessionFactory().getEntityPersister( Parent.class.getName() ),
 				new Callback() {
 					@Override
 					public void bind(PreparedStatement ps) throws SQLException {
 						ps.setString( 1, "Joe" );
 						ps.setString( 2, "Blow" );
 					}
 
 					@Override
 					public QueryParameters getQueryParameters() {
 						return new QueryParameters();
 					}
 
 				}
 		);
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Parent parentWork = ExtraAssertions.assertTyping( Parent.class, result );
 		assertEquals( parent, parentWork );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete Parent" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testCompositeIdWithKeyManyToOne() throws Exception {
 		final String cardId = "ace-of-spades";
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Card card = new Card( cardId );
 		final CardField cardField = new CardField( card, 1 );
 		session.persist( card );
 		session.persist( cardField );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Card cardProxy = (Card) session.load( Card.class, cardId );
 		final CardFieldPK cardFieldPK = new CardFieldPK( cardProxy, 1 );
 		CardField cardFieldGotten = (CardField) session.get( CardField.class, cardFieldPK );
 
 		//assertEquals( card, cardGotten );
 		session.getTransaction().commit();
 		session.close();
 
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( CardField.class.getName() );
 
 		final List results = getResults(
 				entityPersister,
 				new Callback() {
 					@Override
 					public void bind(PreparedStatement ps) throws SQLException {
 						ps.setString( 1, cardField.primaryKey.card.id );
 						ps.setInt( 2, cardField.primaryKey.fieldNumber );
 					}
 
 					@Override
 					public QueryParameters getQueryParameters() {
 						QueryParameters qp = new QueryParameters();
 						qp.setPositionalParameterTypes( new Type[] { entityPersister.getIdentifierType() } );
 						qp.setPositionalParameterValues( new Object[] { cardFieldPK } );
 						qp.setOptionalObject( null );
 						qp.setOptionalEntityName( entityPersister.getEntityName() );
 						qp.setOptionalId( cardFieldPK );
 						qp.setLockOptions( LockOptions.NONE );
 						return qp;
 					}
 
 				}
 		);
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		CardField cardFieldWork = ExtraAssertions.assertTyping( CardField.class, result );
 		assertEquals( cardFieldGotten, cardFieldWork );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete CardField" ).executeUpdate();
 		session.createQuery( "delete Card" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private List getResults(final EntityPersister entityPersister, final Callback callback) {
 		final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 		final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 		final String sql = queryDetails.getSqlStatement();
 		final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 		final List results = new ArrayList();
 
 		final Session workSession = openSession();
 		workSession.beginTransaction();
 		workSession.doWork(
 				new Work() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						PreparedStatement ps = connection.prepareStatement( sql );
 						callback.bind( ps );
 						ResultSet resultSet = ps.executeQuery();
 						//callback.beforeExtractResults( workSession );
 						results.addAll(
 								resultSetProcessor.extractResults(
 										resultSet,
 										(SessionImplementor) workSession,
 										callback.getQueryParameters(),
 										new NamedParameterContext() {
 											@Override
 											public int[] getNamedParameterLocations(String name) {
 												return new int[0];
 											}
 										},
 										true,
 										false,
 										null,
 										null
 								)
 						);
 						resultSet.close();
 						ps.close();
 					}
 				}
 		);
 		workSession.getTransaction().commit();
 		workSession.close();
 
 		return results;
 	}
 
 
 	private interface Callback {
 		void bind(PreparedStatement ps) throws SQLException;
 		QueryParameters getQueryParameters ();
 	}
 
 	@Entity ( name = "Parent" )
 	public static class Parent {
 		@EmbeddedId
 		public ParentPK id;
 
 		public boolean equals(Object o) {
 			if ( this == o ) return true;
 			if ( !( o instanceof Parent ) ) return false;
 
 			final Parent parent = (Parent) o;
 
 			if ( !id.equals( parent.id ) ) return false;
 
 			return true;
 		}
 
 		public int hashCode() {
 			return id.hashCode();
 		}
 	}
 
 	@Embeddable
 	public static class ParentPK implements Serializable {
 		private String firstName;
 		private String lastName;
 
 		public boolean equals(Object o) {
 			if ( this == o ) return true;
 			if ( !( o instanceof ParentPK ) ) return false;
 
 			final ParentPK parentPk = (ParentPK) o;
 
 			if ( !firstName.equals( parentPk.firstName ) ) return false;
 			if ( !lastName.equals( parentPk.lastName ) ) return false;
 
 			return true;
 		}
 
 		public int hashCode() {
 			int result;
 			result = firstName.hashCode();
 			result = 29 * result + lastName.hashCode();
 			return result;
 		}
 	}
 
 	@Entity ( name = "CardField" )
 	public static class CardField implements Serializable {
 
 		@EmbeddedId
 		private CardFieldPK primaryKey;
 
 		CardField(Card card, int fieldNumber) {
 			this.primaryKey = new CardFieldPK(card, fieldNumber);
 		}
 
 		CardField() {
 		}
 
 		public CardFieldPK getPrimaryKey() {
 			return primaryKey;
 		}
 
 		public void setPrimaryKey(CardFieldPK primaryKey) {
 			this.primaryKey = primaryKey;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			CardField cardField = (CardField) o;
 
 			if ( primaryKey != null ? !primaryKey.equals( cardField.primaryKey ) : cardField.primaryKey != null ) {
 				return false;
 			}
 
 			return true;
 		}
 
 		@Override
 		public int hashCode() {
 			return primaryKey != null ? primaryKey.hashCode() : 0;
 		}
 	}
 
 	@Embeddable
 	public static class CardFieldPK implements Serializable {
 		@ManyToOne(optional = false)
 		private Card card;
 
 		private int fieldNumber;
 
 		public CardFieldPK(Card card, int fieldNumber) {
 			this.card = card;
 			this.fieldNumber = fieldNumber;
 		}
 
 		CardFieldPK() {
 		}
 
 		public Card getCard() {
 			return card;
 		}
 
 		public void setCard(Card card) {
 			this.card = card;
 		}
 
 		public int getFieldNumber() {
 			return fieldNumber;
 		}
 
 		public void setFieldNumber(int fieldNumber) {
 			this.fieldNumber = fieldNumber;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			CardFieldPK that = (CardFieldPK) o;
 
 			if ( fieldNumber != that.fieldNumber ) {
 				return false;
 			}
 			if ( card != null ? !card.equals( that.card ) : that.card != null ) {
 				return false;
 			}
 
 			return true;
 		}
 
 		@Override
 		public int hashCode() {
 			int result = card != null ? card.hashCode() : 0;
 			result = 31 * result + fieldNumber;
 			return result;
 		}
 	}
 
 	@Entity ( name = "Card" )
 	public static class Card implements Serializable {
 		@Id
 		private String id;
 
 		public Card(String id) {
 			this();
 			this.id = id;
 		}
 
 		Card() {
 		}
 
 		public String getId() {
 			return id;
 		}
 
 		public void setId(String id) {
 			this.id = id;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			Card card = (Card) o;
 
 			if ( !id.equals( card.id ) ) {
 				return false;
 			}
 
 			return true;
 		}
 
 		@Override
 		public int hashCode() {
 			return id.hashCode();
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityAssociationResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityAssociationResultSetProcessorTest.java
index a89f00f3f4..640a694c8e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityAssociationResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityAssociationResultSetProcessorTest.java
@@ -1,292 +1,292 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class EntityAssociationResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class, ReportedMessage.class };
 	}
 
 	@Test
 	public void testManyToOneEntityProcessing() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Message.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Message message = new Message( 1, "the message" );
 		Poster poster = new Poster( 2, "the poster" );
 		session.save( message );
 		session.save( poster );
 		message.poster = poster;
 		poster.messages.add( message );
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 			final String sql = queryDetails.getSqlStatement();
 			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 1 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 
 			Message workMessage = ExtraAssertions.assertTyping( Message.class, result );
 			assertEquals( 1, workMessage.mid.intValue() );
 			assertEquals( "the message", workMessage.msgTxt );
 			assertTrue( Hibernate.isInitialized( workMessage.poster ) );
 			Poster workPoster = workMessage.poster;
 			assertEquals( 2, workPoster.pid.intValue() );
 			assertEquals( "the poster", workPoster.name );
 			assertFalse( Hibernate.isInitialized( workPoster.messages ) );
 
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete Message" ).executeUpdate();
 		session.createQuery( "delete Poster" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testNestedManyToOneEntityProcessing() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( ReportedMessage.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Message message = new Message( 1, "the message" );
 		Poster poster = new Poster( 2, "the poster" );
 		session.save( message );
 		session.save( poster );
 		message.poster = poster;
 		poster.messages.add( message );
 		ReportedMessage reportedMessage = new ReportedMessage( 0, "inappropriate", message );
 		session.save( reportedMessage );
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 			final String sql = queryDetails.getSqlStatement();
 			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 0 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 
 			ReportedMessage workReportedMessage = ExtraAssertions.assertTyping( ReportedMessage.class, result );
 			assertEquals( 0, workReportedMessage.id.intValue() );
 			assertEquals( "inappropriate", workReportedMessage.reason );
 			Message workMessage = workReportedMessage.message;
 			assertNotNull( workMessage );
 			assertTrue( Hibernate.isInitialized( workMessage ) );
 			assertEquals( 1, workMessage.mid.intValue() );
 			assertEquals( "the message", workMessage.msgTxt );
 			assertTrue( Hibernate.isInitialized( workMessage.poster ) );
 			Poster workPoster = workMessage.poster;
 			assertEquals( 2, workPoster.pid.intValue() );
 			assertEquals( "the poster", workPoster.name );
 			assertFalse( Hibernate.isInitialized( workPoster.messages ) );
 
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete ReportedMessage" ).executeUpdate();
 		session.createQuery( "delete Message" ).executeUpdate();
 		session.createQuery( "delete Poster" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "ReportedMessage" )
 	public static class ReportedMessage {
 		@Id
 		private Integer id;
 		private String reason;
 		@ManyToOne
 		@JoinColumn
 		private Message message;
 
 		public ReportedMessage() {}
 
 		public ReportedMessage(Integer id, String reason, Message message) {
 			this.id = id;
 			this.reason = reason;
 			this.message = message;
 		}
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne( cascade = CascadeType.MERGE )
 		@JoinColumn
 		private Poster poster;
 
 		public Message() {}
 
 		public Message(Integer mid, String msgTxt) {
 			this.mid = mid;
 			this.msgTxt = msgTxt;
 		}
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages = new ArrayList<Message>();
 
 		public Poster() {}
 
 		public Poster(Integer pid, String name) {
 			this.pid = pid;
 			this.name = name;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyCollectionResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyCollectionResultSetProcessorTest.java
index a8c37c4f33..3694d91430 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyCollectionResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyCollectionResultSetProcessorTest.java
@@ -1,163 +1,163 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import javax.persistence.CollectionTable;
 import javax.persistence.Column;
 import javax.persistence.ElementCollection;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class EntityWithNonLazyCollectionResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Person.class };
 	}
 
 	@Test
 	public void testEntityWithSet() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Person.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Person person = new Person();
 		person.id = 1;
 		person.name = "John Doe";
 		person.nickNames.add( "Jack" );
 		person.nickNames.add( "Johnny" );
 		session.save( person );
 		session.getTransaction().commit();
 		session.close();
 
 		{
 
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 			final String sql = queryDetails.getSqlStatement();
 			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 1 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 2, results.size() );
 			Object result1 = results.get( 0 );
 			assertSame( result1, results.get( 1 ) );
 			assertNotNull( result1 );
 
 			Person workPerson = ExtraAssertions.assertTyping( Person.class, result1 );
 			assertEquals( 1, workPerson.id.intValue() );
 			assertEquals( person.name, workPerson.name );
 			assertTrue( Hibernate.isInitialized( workPerson.nickNames ) );
 			assertEquals( 2, workPerson.nickNames.size() );
 			assertEquals( person.nickNames, workPerson.nickNames );
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( person );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "Person" )
 	public static class Person {
 		@Id
 		private Integer id;
 		private String name;
 		@ElementCollection( fetch = FetchType.EAGER )
 		@CollectionTable( name = "nick_names", joinColumns = @JoinColumn( name = "pid" ) )
 		@Column( name = "nick" )
 		private Set<String> nickNames = new HashSet<String>();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManyListResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManyListResultSetProcessorTest.java
index 80b877995e..859a6cdf28 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManyListResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManyListResultSetProcessorTest.java
@@ -1,196 +1,196 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class EntityWithNonLazyOneToManyListResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Poster.class, Message.class };
 	}
 
 	@Test
 	public void testEntityWithList() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Poster.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Poster poster = new Poster();
 		poster.pid = 0;
 		poster.name = "John Doe";
 		Message message1 = new Message();
 		message1.mid = 1;
 		message1.msgTxt = "Howdy!";
 		message1.poster = poster;
 		poster.messages.add( message1 );
 		Message message2 = new Message();
 		message2.mid = 2;
 		message2.msgTxt = "Bye!";
 		message2.poster = poster;
 		poster.messages.add( message2 );
 		session.save( poster );
 		session.getTransaction().commit();
 		session.close();
 
 //		session = openSession();
 //		session.beginTransaction();
 //		Poster posterGotten = (Poster) session.get( Poster.class, poster.pid );
 //		assertEquals( 0, posterGotten.pid.intValue() );
 //		assertEquals( poster.name, posterGotten.name );
 //		assertTrue( Hibernate.isInitialized( posterGotten.messages ) );
 //		assertEquals( 2, posterGotten.messages.size() );
 //		assertEquals( message1.msgTxt, posterGotten.messages.get( 0 ).msgTxt );
 //		assertEquals( message2.msgTxt, posterGotten.messages.get( 1 ).msgTxt );
 //		assertSame( posterGotten, posterGotten.messages.get( 0 ).poster );
 //		assertSame( posterGotten, posterGotten.messages.get( 1 ).poster );
 //		session.getTransaction().commit();
 //		session.close();
 
 		{
 
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 			final String sql = queryDetails.getSqlStatement();
 			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 0 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 2, results.size() );
 			Object result1 = results.get( 0 );
 			assertNotNull( result1 );
 			assertSame( result1, results.get( 1 ) );
 
 			Poster workPoster = ExtraAssertions.assertTyping( Poster.class, result1 );
 			assertEquals( 0, workPoster.pid.intValue() );
 			assertEquals( poster.name, workPoster.name );
 			assertTrue( Hibernate.isInitialized( workPoster.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			assertTrue( Hibernate.isInitialized( workPoster.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			assertEquals( message1.msgTxt, workPoster.messages.get( 0 ).msgTxt );
 			assertEquals( message2.msgTxt, workPoster.messages.get( 1 ).msgTxt );
 			assertSame( workPoster, workPoster.messages.get( 0 ).poster );
 			assertSame( workPoster, workPoster.messages.get( 1 ).poster );
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( poster );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster", fetch = FetchType.EAGER, cascade = CascadeType.ALL )
 		private List<Message> messages = new ArrayList<Message>();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManySetResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManySetResultSetProcessorTest.java
index c757f2642c..13f866ad6d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManySetResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/EntityWithNonLazyOneToManySetResultSetProcessorTest.java
@@ -1,214 +1,214 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gail Badner
  */
 public class EntityWithNonLazyOneToManySetResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Poster.class, Message.class };
 	}
 
 	@Test
 	public void testEntityWithSet() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Poster.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Poster poster = new Poster();
 		poster.pid = 0;
 		poster.name = "John Doe";
 		Message message1 = new Message();
 		message1.mid = 1;
 		message1.msgTxt = "Howdy!";
 		message1.poster = poster;
 		poster.messages.add( message1 );
 		Message message2 = new Message();
 		message2.mid = 2;
 		message2.msgTxt = "Bye!";
 		message2.poster = poster;
 		poster.messages.add( message2 );
 		session.save( poster );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Poster posterGotten = (Poster) session.get( Poster.class, poster.pid );
 		assertEquals( 0, posterGotten.pid.intValue() );
 		assertEquals( poster.name, posterGotten.name );
 		assertTrue( Hibernate.isInitialized( posterGotten.messages ) );
 		assertEquals( 2, posterGotten.messages.size() );
 		for ( Message message : posterGotten.messages ) {
 			if ( message.mid == 1 ) {
 				assertEquals( message1.msgTxt, message.msgTxt );
 			}
 			else if ( message.mid == 2 ) {
 				assertEquals( message2.msgTxt, message.msgTxt );
 			}
 			else {
 				fail( "unexpected message id." );
 			}
 			assertSame( posterGotten, message.poster );
 		}
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 			final String sql = queryDetails.getSqlStatement();
 			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 0 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 2, results.size() );
 			Object result1 = results.get( 0 );
 			assertNotNull( result1 );
 			assertSame( result1, results.get( 1 ) );
 
 			Poster workPoster = ExtraAssertions.assertTyping( Poster.class, result1 );
 			assertEquals( 0, workPoster.pid.intValue() );
 			assertEquals( poster.name, workPoster.name );
 			assertTrue( Hibernate.isInitialized( workPoster.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			assertTrue( Hibernate.isInitialized( posterGotten.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			for ( Message message : workPoster.messages ) {
 				if ( message.mid == 1 ) {
 					assertEquals( message1.msgTxt, message.msgTxt );
 				}
 				else if ( message.mid == 2 ) {
 					assertEquals( message2.msgTxt, message.msgTxt );
 				}
 				else {
 					fail( "unexpected message id." );
 				}
 				assertSame( workPoster, message.poster );
 			}
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( poster );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster", fetch = FetchType.EAGER, cascade = CascadeType.ALL )
 		private Set<Message> messages = new HashSet<Message>();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/Helper.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/Helper.java
index fcb550ce8c..734d5a92b2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/Helper.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/Helper.java
@@ -1,93 +1,93 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan2.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.spi.MetamodelDrivenLoadPlanBuilder;
-import org.hibernate.loader.plan2.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan2.exec.spi.EntityLoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Steve Ebersole
  */
 public class Helper implements QueryBuildingParameters {
 	/**
 	 * Singleton access
 	 */
 	public static final Helper INSTANCE = new Helper();
 
 	private Helper() {
 	}
 
 	public LoadPlan buildLoadPlan(SessionFactoryImplementor sf, EntityPersister entityPersister) {
 		final FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
 				sf,
 				LoadQueryInfluencers.NONE,
 				LockMode.NONE
 		);
 		return MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
 	}
 
 	public EntityLoadQueryDetails buildLoadQueryDetails(EntityPersister entityPersister, SessionFactoryImplementor sf) {
 		return buildLoadQueryDetails(
 				buildLoadPlan( sf, entityPersister ),
 				sf
 		);
 	}
 
 	public EntityLoadQueryDetails buildLoadQueryDetails(LoadPlan loadPlan, SessionFactoryImplementor sf) {
 		return EntityLoadQueryDetails.makeForBatching(
 				loadPlan,
 				null,
 				this,
 				sf
 		);
 	}
 
 	@Override
 	public LoadQueryInfluencers getQueryInfluencers() {
 		return LoadQueryInfluencers.NONE;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return 1;
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return null;
 	}
 
 	@Override
 	public LockOptions getLockOptions() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/NonEncapsulatedCompositeIdResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/NonEncapsulatedCompositeIdResultSetProcessorTest.java
index c256a39ddd..388233493b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/NonEncapsulatedCompositeIdResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/NonEncapsulatedCompositeIdResultSetProcessorTest.java
@@ -1,211 +1,211 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.onetoone.formula.Address;
 import org.hibernate.test.onetoone.formula.Person;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class NonEncapsulatedCompositeIdResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected String[] getMappings() {
 		return new String[] { "onetoone/formula/Person.hbm.xml" };
 	}
 
 	@Test
 	public void testCompositeIdWithKeyManyToOne() throws Exception {
 		final String personId = "John Doe";
 
 		Person p = new Person();
 		p.setName( personId );
 		final Address a = new Address();
 		a.setPerson( p );
 		p.setAddress( a );
 		a.setType( "HOME" );
 		a.setStreet( "Main St" );
 		a.setState( "Sweet Home Alabama" );
 		a.setZip( "3181" );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( p );
 		t.commit();
 		s.close();
 
 		final EntityPersister personPersister = sessionFactory().getEntityPersister( Person.class.getName() );
 		final EntityPersister addressPersister = sessionFactory().getEntityPersister( Address.class.getName() );
 
 		{
 			final List results = getResults(
 					addressPersister,
 					new Callback() {
 						@Override
 						public void bind(PreparedStatement ps) throws SQLException {
 							ps.setString( 1, personId );
 							ps.setString( 2, "HOME" );
 						}
 
 						@Override
 						public QueryParameters getQueryParameters() {
 							QueryParameters qp = new QueryParameters();
 							qp.setPositionalParameterTypes( new Type[] { addressPersister.getIdentifierType() } );
 							qp.setPositionalParameterValues( new Object[] { a } );
 							qp.setOptionalObject( a );
 							qp.setOptionalEntityName( addressPersister.getEntityName() );
 							qp.setOptionalId( a );
 							qp.setLockOptions( LockOptions.NONE );
 							return qp;
 						}
 
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 		}
 
 		// test loading the Person (the entity with normal id def, but mixed composite fk to Address)
 		{
 			final List results = getResults(
 					personPersister,
 					new Callback() {
 						@Override
 						public void bind(PreparedStatement ps) throws SQLException {
 							ps.setString( 1, personId );
 						}
 
 						@Override
 						public QueryParameters getQueryParameters() {
 							QueryParameters qp = new QueryParameters();
 							qp.setPositionalParameterTypes( new Type[] { personPersister.getIdentifierType() } );
 							qp.setPositionalParameterValues( new Object[] { personId } );
 							qp.setOptionalObject( null );
 							qp.setOptionalEntityName( personPersister.getEntityName() );
 							qp.setOptionalId( personId );
 							qp.setLockOptions( LockOptions.NONE );
 							return qp;
 						}
 
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 		}
 
 //		CardField cardFieldWork = ExtraAssertions.assertTyping( CardField.class, result );
 //		assertEquals( cardFieldGotten, cardFieldWork );
 
 		// clean up test data
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete Address" ).executeUpdate();
 		s.createQuery( "delete Person" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private List getResults(final EntityPersister entityPersister, final Callback callback) {
 		final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 		final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 		final String sql = queryDetails.getSqlStatement();
 		final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 		final List results = new ArrayList();
 
 		final Session workSession = openSession();
 		workSession.beginTransaction();
 		workSession.doWork(
 				new Work() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						System.out.println( "SQL : " + sql );
 						PreparedStatement ps = connection.prepareStatement( sql );
 						callback.bind( ps );
 						ResultSet resultSet = ps.executeQuery();
 						//callback.beforeExtractResults( workSession );
 						results.addAll(
 								resultSetProcessor.extractResults(
 										resultSet,
 										(SessionImplementor) workSession,
 										callback.getQueryParameters(),
 										new NamedParameterContext() {
 											@Override
 											public int[] getNamedParameterLocations(String name) {
 												return new int[0];
 											}
 										},
 										true,
 										false,
 										null,
 										null
 								)
 						);
 						resultSet.close();
 						ps.close();
 					}
 				}
 		);
 		workSession.getTransaction().commit();
 		workSession.close();
 
 		return results;
 	}
 
 
 	private interface Callback {
 		void bind(PreparedStatement ps) throws SQLException;
 		QueryParameters getQueryParameters();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/SimpleResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/SimpleResultSetProcessorTest.java
index d945048e66..d18af51ab5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/SimpleResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/process/SimpleResultSetProcessorTest.java
@@ -1,147 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.process;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan2.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan2.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan2.exec.spi.LoadQueryDetails;
-import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class SimpleResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { SimpleEntity.class };
 	}
 
 	@Test
 	public void testSimpleEntityProcessing() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( SimpleEntity.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( new SimpleEntity( 1, "the only" ) );
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
 			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
 			final String sql = queryDetails.getSqlStatement();
 			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							( (SessionImplementor) workSession ).getFactory().getJdbcServices().getSqlStatementLogger().logStatement( sql );
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 1 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 
 			SimpleEntity workEntity = ExtraAssertions.assertTyping( SimpleEntity.class, result );
 			assertEquals( 1, workEntity.id.intValue() );
 			assertEquals( "the only", workEntity.name );
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete SimpleEntity" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity(name = "SimpleEntity")
 	public static class SimpleEntity {
 		@Id public Integer id;
 		public String name;
 
 		public SimpleEntity() {
 		}
 
 		public SimpleEntity(Integer id, String name) {
 			this.id = id;
 			this.name = name;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java b/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java
index 8467fad1a3..584d308703 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/loadplans/walking/LoggingAssociationVisitationStrategy.java
@@ -1,246 +1,246 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.loadplans.walking;
 
 import org.hibernate.annotations.common.util.StringHelper;
-import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class LoggingAssociationVisitationStrategy implements AssociationVisitationStrategy {
 	private int depth = 1;
 
 	@Override
 	public void start() {
 		System.out.println( ">> Start" );
 	}
 
 	@Override
 	public void finish() {
 		System.out.println( "<< Finish" );
 	}
 
 	@Override
 	public void startingEntity(EntityDefinition entityDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting entity (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						entityDefinition.getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public void finishingEntity(EntityDefinition entityDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing entity (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						entityDefinition.getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting [%s] entity identifier (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						entityIdentifierDefinition.isEncapsulated() ? "encapsulated" : "non-encapsulated",
 						entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing entity identifier (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 				)
 		);
 	}
 
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 		System.out.println(
 				String.format(
 						"%s Handling attribute (%s)",
 						StringHelper.repeat( ">>", depth + 1 ),
 						attributeDefinition.getName()
 				)
 		);
 		return true;
 	}
 
 	@Override
 	public void finishingAttribute(AttributeDefinition attributeDefinition) {
 		// nothing to do
 	}
 
 	@Override
 	public void startingComposite(CompositionDefinition compositionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting composite (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						compositionDefinition.getName()
 				)
 		);
 	}
 
 	@Override
 	public void finishingComposite(CompositionDefinition compositionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing composite (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						compositionDefinition.getName()
 				)
 		);
 	}
 
 	@Override
 	public void startingCollection(CollectionDefinition collectionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting collection (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						collectionDefinition.getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void finishingCollection(CollectionDefinition collectionDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing collection (%s)",
 						StringHelper.repeat( ">>", depth-- ),
 						collectionDefinition.getCollectionPersister().getRole()
 				)
 		);
 	}
 
 
 	@Override
 	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting collection index (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						collectionIndexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing collection index (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						collectionIndexDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting collection elements (%s)",
 						StringHelper.repeat( ">>", ++depth ),
 						elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 		System.out.println(
 				String.format(
 						"%s Finishing collection elements (%s)",
 						StringHelper.repeat( "<<", depth-- ),
 						elementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
 	public void foundAny(AnyMappingDefinition anyDefinition) {
 		// nothing to do
 	}
 
 	@Override
 	public void associationKeyRegistered(AssociationKey associationKey) {
 		System.out.println(
 				String.format(
 						"%s AssociationKey registered : %s",
 						StringHelper.repeat( ">>", depth + 1 ),
 						associationKey.toString()
 				)
 		);
 	}
 
 	@Override
 	public FetchSource registeredFetchSource(AssociationKey associationKey) {
 		return null;
 	}
 
 	@Override
 	public void foundCircularAssociation(
 			AssociationAttributeDefinition attributeDefinition) {
 		System.out.println(
 				String.format(
 						"%s Handling circular association attribute (%s) : %s",
 						StringHelper.repeat( ">>", depth + 1 ),
 						attributeDefinition.toString(),
 						attributeDefinition.getAssociationKey().toString()
 				)
 		);
 	}
 
 	@Override
 	public boolean isDuplicateAssociationKey(AssociationKey associationKey) {
 		return false;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/graphs/EntityGraphLoadPlanBuilderTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/graphs/EntityGraphLoadPlanBuilderTest.java
index e52d174e5e..d98de9968f 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/graphs/EntityGraphLoadPlanBuilderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/graphs/EntityGraphLoadPlanBuilderTest.java
@@ -1,355 +1,351 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
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
 package org.hibernate.jpa.test.graphs;
 
 import java.util.Iterator;
 import java.util.Set;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.Entity;
 import javax.persistence.EntityGraph;
 import javax.persistence.EntityManager;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 
 
 import org.junit.Test;
 
 import static org.junit.Assert.*;
 
 import org.hibernate.LockMode;
-import org.hibernate.SessionFactory;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.jpa.graph.internal.EntityGraphImpl;
-import org.hibernate.jpa.graph.internal.SubgraphImpl;
 import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
-import org.hibernate.loader.plan2.build.internal.FetchGraphLoadPlanBuildingStrategy;
-import org.hibernate.loader.plan2.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.internal.LoadGraphLoadPlanBuildingStrategy;
-import org.hibernate.loader.plan2.build.spi.AbstractLoadPlanBuildingAssociationVisitationStrategy;
-import org.hibernate.loader.plan2.build.spi.LoadPlanTreePrinter;
-import org.hibernate.loader.plan2.build.spi.MetamodelDrivenLoadPlanBuilder;
-import org.hibernate.loader.plan2.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan2.spi.Join;
-import org.hibernate.loader.plan2.spi.LoadPlan;
-import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan.build.internal.FetchGraphLoadPlanBuildingStrategy;
+import org.hibernate.loader.plan.build.internal.LoadGraphLoadPlanBuildingStrategy;
+import org.hibernate.loader.plan.build.spi.AbstractLoadPlanBuildingAssociationVisitationStrategy;
+import org.hibernate.loader.plan.build.spi.LoadPlanTreePrinter;
+import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.spi.Join;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.QuerySpace;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 public class EntityGraphLoadPlanBuilderTest extends BaseEntityManagerFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Cat.class, Person.class, Country.class, Dog.class, ExpressCompany.class };
 	}
 
 	@Entity
 	public static class Dog {
 		@Id
 		String name;
 		@ElementCollection
 		Set<String> favorites;
 	}
 
 	@Entity
 	public static class Cat {
 		@Id
 		String name;
 		@ManyToOne(fetch = FetchType.LAZY)
 		Person owner;
 
 	}
 
 	@Entity
 	public static class Person {
 		@Id
 		String name;
 		@OneToMany(mappedBy = "owner")
 		Set<Cat> pets;
 		@Embedded
 		Address homeAddress;
 	}
 
 	@Embeddable
 	public static class Address {
 		@ManyToOne
 		Country country;
 	}
 
 	@Entity
 	public static class ExpressCompany {
 		@Id
 		String name;
 		@ElementCollection
 		Set<Address> shipAddresses;
 	}
 
 	@Entity
 	public static class Country {
 		@Id
 		String name;
 	}
 
 	/**
 	 * EntityGraph(1):
 	 *
 	 * Cat
 	 *
 	 * LoadPlan:
 	 *
 	 * Cat
 	 *
 	 * ---------------------
 	 *
 	 * EntityGraph(2):
 	 *
 	 * Cat
 	 * owner -- Person
 	 *
 	 * LoadPlan:
 	 *
 	 * Cat
 	 * owner -- Person
 	 * address --- Address
 	 */
 	@Test
 	public void testBasicFetchLoadPlanBuilding() {
 		EntityManager em = getOrCreateEntityManager();
 		EntityGraph eg = em.createEntityGraph( Cat.class );
 		LoadPlan plan = buildLoadPlan( eg, Mode.FETCH, Cat.class );
 		LoadPlanTreePrinter.INSTANCE.logTree( plan, new AliasResolutionContextImpl( sfi() ) );
 		QuerySpace rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get( 0 );
 		assertFalse(
 				"With fetchgraph property and an empty EntityGraph, there should be no join at all",
 				rootQuerySpace.getJoins().iterator().hasNext()
 		);
 		// -------------------------------------------------- another a little more complicated case
 		eg = em.createEntityGraph( Cat.class );
 		eg.addSubgraph( "owner", Person.class );
 		plan = buildLoadPlan( eg, Mode.FETCH, Cat.class );
 		LoadPlanTreePrinter.INSTANCE.logTree( plan, new AliasResolutionContextImpl( sfi() ) );
 		rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get( 0 );
 		Iterator<Join> iterator = rootQuerySpace.getJoins().iterator();
 		assertTrue(
 				"With fetchgraph property and an empty EntityGraph, there should be no join at all", iterator.hasNext()
 		);
 		Join personJoin = iterator.next();
 		assertNotNull( personJoin );
 		QuerySpace.Disposition disposition = personJoin.getRightHandSide().getDisposition();
 		assertEquals(
 				"This should be an entity join which fetches Person", QuerySpace.Disposition.ENTITY, disposition
 		);
 
 		iterator = personJoin.getRightHandSide().getJoins().iterator();
 		assertTrue( "The composite address should be fetched", iterator.hasNext() );
 		Join addressJoin = iterator.next();
 		assertNotNull( addressJoin );
 		disposition = addressJoin.getRightHandSide().getDisposition();
 		assertEquals( QuerySpace.Disposition.COMPOSITE, disposition );
 		assertFalse( iterator.hasNext() );
 		assertFalse(
 				"The ManyToOne attribute in composite should not be fetched",
 				addressJoin.getRightHandSide().getJoins().iterator().hasNext()
 		);
 		em.close();
 	}
 
 	/**
 	 * EntityGraph(1):
 	 *
 	 * Cat
 	 *
 	 * LoadPlan:
 	 *
 	 * Cat
 	 *
 	 * ---------------------
 	 *
 	 * EntityGraph(2):
 	 *
 	 * Cat
 	 * owner -- Person
 	 *
 	 * LoadPlan:
 	 *
 	 * Cat
 	 * owner -- Person
 	 * address --- Address
 	 * country -- Country
 	 */
 	@Test
 	public void testBasicLoadLoadPlanBuilding() {
 		EntityManager em = getOrCreateEntityManager();
 		EntityGraph eg = em.createEntityGraph( Cat.class );
 		LoadPlan plan = buildLoadPlan( eg, Mode.LOAD, Cat.class );
 		LoadPlanTreePrinter.INSTANCE.logTree( plan, new AliasResolutionContextImpl( sfi() ) );
 		QuerySpace rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get( 0 );
 		assertFalse(
 				"With fetchgraph property and an empty EntityGraph, there should be no join at all",
 				rootQuerySpace.getJoins().iterator().hasNext()
 		);
 		// -------------------------------------------------- another a little more complicated case
 		eg = em.createEntityGraph( Cat.class );
 		eg.addSubgraph( "owner", Person.class );
 		plan = buildLoadPlan( eg, Mode.LOAD, Cat.class );
 		LoadPlanTreePrinter.INSTANCE.logTree( plan, new AliasResolutionContextImpl( sfi() ) );
 		rootQuerySpace = plan.getQuerySpaces().getRootQuerySpaces().get( 0 );
 		Iterator<Join> iterator = rootQuerySpace.getJoins().iterator();
 		assertTrue(
 				"With fetchgraph property and an empty EntityGraph, there should be no join at all", iterator.hasNext()
 		);
 		Join personJoin = iterator.next();
 		assertNotNull( personJoin );
 		QuerySpace.Disposition disposition = personJoin.getRightHandSide().getDisposition();
 		assertEquals(
 				"This should be an entity join which fetches Person", QuerySpace.Disposition.ENTITY, disposition
 		);
 
 		iterator = personJoin.getRightHandSide().getJoins().iterator();
 		assertTrue( "The composite address should be fetched", iterator.hasNext() );
 		Join addressJoin = iterator.next();
 		assertNotNull( addressJoin );
 		disposition = addressJoin.getRightHandSide().getDisposition();
 		assertEquals( QuerySpace.Disposition.COMPOSITE, disposition );
 		iterator = addressJoin.getRightHandSide().getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		Join countryJoin = iterator.next();
 		assertNotNull( countryJoin );
 		disposition = countryJoin.getRightHandSide().getDisposition();
 		assertEquals( QuerySpace.Disposition.ENTITY, disposition );
 		assertFalse(
 				"The ManyToOne attribute in composite should not be fetched",
 				countryJoin.getRightHandSide().getJoins().iterator().hasNext()
 		);
 		em.close();
 	}
 
 
 	@Test
 	public void testBasicElementCollections() {
 		EntityManager em = getOrCreateEntityManager();
 		EntityGraph eg = em.createEntityGraph( Dog.class );
 		eg.addAttributeNodes( "favorites" );
 		LoadPlan loadLoadPlan = buildLoadPlan( eg, Mode.LOAD, Dog.class ); //WTF name!!!
 		LoadPlanTreePrinter.INSTANCE.logTree( loadLoadPlan, new AliasResolutionContextImpl( sfi() ) );
 		QuerySpace querySpace = loadLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
 		Iterator<Join> iterator = querySpace.getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		Join collectionJoin = iterator.next();
 		assertEquals( QuerySpace.Disposition.COLLECTION, collectionJoin.getRightHandSide().getDisposition() );
 		assertFalse( iterator.hasNext() );
 		//----------------------------------------------------------------
 		LoadPlan fetchLoadPlan = buildLoadPlan( eg, Mode.FETCH, Dog.class );
 		LoadPlanTreePrinter.INSTANCE.logTree( fetchLoadPlan, new AliasResolutionContextImpl( sfi() ) );
 		querySpace = fetchLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
 		iterator = querySpace.getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		collectionJoin = iterator.next();
 		assertEquals( QuerySpace.Disposition.COLLECTION, collectionJoin.getRightHandSide().getDisposition() );
 		assertFalse( iterator.hasNext() );
 		em.close();
 	}
 
 
 	@Test
 	public void testEmbeddedCollection() {
 		EntityManager em = getOrCreateEntityManager();
 		EntityGraph eg = em.createEntityGraph( ExpressCompany.class );
 		eg.addAttributeNodes( "shipAddresses" );
 
 		LoadPlan loadLoadPlan = buildLoadPlan( eg, Mode.LOAD, ExpressCompany.class ); //WTF name!!!
 		LoadPlanTreePrinter.INSTANCE.logTree( loadLoadPlan, new AliasResolutionContextImpl( sfi() ) );
 
 		QuerySpace querySpace = loadLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
 		Iterator<Join> iterator = querySpace.getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		Join collectionJoin = iterator.next();
 		assertEquals( QuerySpace.Disposition.COLLECTION, collectionJoin.getRightHandSide().getDisposition() );
 		assertFalse( iterator.hasNext() );
 
 		iterator = collectionJoin.getRightHandSide().getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		Join collectionElementJoin = iterator.next();
 		assertFalse( iterator.hasNext() );
 		assertEquals( QuerySpace.Disposition.COMPOSITE, collectionElementJoin.getRightHandSide().getDisposition() );
 
 		iterator = collectionElementJoin.getRightHandSide().getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		Join countryJoin = iterator.next();
 		assertFalse( iterator.hasNext() );
 		assertEquals( QuerySpace.Disposition.ENTITY, countryJoin.getRightHandSide().getDisposition() );
 
 		//----------------------------------------------------------------
 		LoadPlan fetchLoadPlan = buildLoadPlan( eg, Mode.FETCH, ExpressCompany.class );
 		LoadPlanTreePrinter.INSTANCE.logTree( fetchLoadPlan, new AliasResolutionContextImpl( sfi() ) );
 
 
 		querySpace = fetchLoadPlan.getQuerySpaces().getRootQuerySpaces().iterator().next();
 		iterator = querySpace.getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		collectionJoin = iterator.next();
 		assertEquals( QuerySpace.Disposition.COLLECTION, collectionJoin.getRightHandSide().getDisposition() );
 		assertFalse( iterator.hasNext() );
 
 		iterator = collectionJoin.getRightHandSide().getJoins().iterator();
 		assertTrue( iterator.hasNext() );
 		collectionElementJoin = iterator.next();
 		assertFalse( iterator.hasNext() );
 		assertEquals( QuerySpace.Disposition.COMPOSITE, collectionElementJoin.getRightHandSide().getDisposition() );
 
 		iterator = collectionElementJoin.getRightHandSide().getJoins().iterator();
 		assertFalse( iterator.hasNext() );
 		//----------------------------------------------------------------
 		em.close();
 	}
 
 
 	private SessionFactoryImplementor sfi() {
 		return entityManagerFactory().unwrap( SessionFactoryImplementor.class );
 	}
 
 	private LoadPlan buildLoadPlan(EntityGraph entityGraph, Mode mode, Class clazz) {
 
 		LoadQueryInfluencers loadQueryInfluencers = new LoadQueryInfluencers( sfi() );
 		if ( Mode.FETCH == mode ) {
 			loadQueryInfluencers.setFetchGraph( entityGraph );
 		}
 		else {
 			loadQueryInfluencers.setLoadGraph( entityGraph );
 		}
 		EntityPersister ep = (EntityPersister) sfi().getClassMetadata( clazz );
 		AbstractLoadPlanBuildingAssociationVisitationStrategy strategy = Mode.FETCH == mode ? new FetchGraphLoadPlanBuildingStrategy(
 				sfi(), loadQueryInfluencers, LockMode.NONE
 		) : new LoadGraphLoadPlanBuildingStrategy( sfi(), loadQueryInfluencers, LockMode.NONE );
 		return MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 	}
 
 	public static enum Mode {FETCH, LOAD}
 }
