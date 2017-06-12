diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractJoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractJoinableAssociationImpl.java
index 11bf50aaf5..acc41f7839 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractJoinableAssociationImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractJoinableAssociationImpl.java
@@ -1,116 +1,115 @@
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
 package org.hibernate.loader.internal;
 import java.util.Map;
 
 import org.hibernate.Filter;
 import org.hibernate.MappingException;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.sql.JoinType;
 
 /**
  * This class represents a joinable association.
  *
  * @author Gavin King
  * @author Gail Badner
  */
 public abstract class AbstractJoinableAssociationImpl implements JoinableAssociation {
 	private final PropertyPath propertyPath;
 	private final Fetch currentFetch;
 	private final EntityReference currentEntityReference;
 	private final CollectionReference currentCollectionReference;
 	private final JoinType joinType;
 	private final String withClause;
 	private final Map<String, Filter> enabledFilters;
 	private final boolean hasRestriction;
 
 	public AbstractJoinableAssociationImpl(
 			Fetch currentFetch,
 			EntityReference currentEntityReference,
 			CollectionReference currentCollectionReference,
 			String withClause,
-			boolean isNullable,
 			boolean hasRestriction,
 			Map<String, Filter> enabledFilters) throws MappingException {
 		this.propertyPath = currentFetch.getPropertyPath();
 		if ( currentFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
-			joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
+			joinType = currentFetch.isNullable() ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
 		}
 		else {
 			joinType = JoinType.NONE;
 		}
 		this.currentFetch = currentFetch;
 		this.currentEntityReference = currentEntityReference;
 		this.currentCollectionReference = currentCollectionReference;
 		this.withClause = withClause;
 		this.hasRestriction = hasRestriction;
 		this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
 	public JoinType getJoinType() {
 		return joinType;
 	}
 
 	@Override
 	public Fetch getCurrentFetch() {
 		return currentFetch;
 	}
 
 	@Override
 	public EntityReference getCurrentEntityReference() {
 		return currentEntityReference;
 	}
 
 	@Override
 	public CollectionReference getCurrentCollectionReference() {
 		return currentCollectionReference;
 	}
 
 	@Override
 	public boolean hasRestriction() {
 		return hasRestriction;
 	}
 
 	@Override
 	public String getWithClause() {
 		return withClause;
 	}
 
 	@Override
 	public Map<String, Filter> getEnabledFilters() {
 		return enabledFilters;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/CollectionJoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/CollectionJoinableAssociationImpl.java
index df49038b35..254c1a9c33 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/CollectionJoinableAssociationImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/CollectionJoinableAssociationImpl.java
@@ -1,94 +1,93 @@
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
 package org.hibernate.loader.internal;
 
 import java.util.Map;
 
 import org.hibernate.Filter;
 import org.hibernate.MappingException;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.type.AssociationType;
 
 /**
  * This class represents a joinable collection association.
  *
  * @author Gail Badner
  */
 
 public class CollectionJoinableAssociationImpl extends AbstractJoinableAssociationImpl {
 
 	private final AssociationType joinableType;
 	private final Joinable joinable;
 
 	public CollectionJoinableAssociationImpl(
 			CollectionFetch collectionFetch,
 			EntityReference currentEntityReference,
 			String withClause,
 			boolean hasRestriction,
 			Map<String, Filter> enabledFilters) throws MappingException {
 		super(
 				collectionFetch,
 				currentEntityReference,
 				collectionFetch,
 				withClause,
-				true,
 				hasRestriction,
 				enabledFilters
 		);
 		this.joinableType = collectionFetch.getCollectionPersister().getCollectionType();
 		this.joinable = (Joinable) collectionFetch.getCollectionPersister();
 	}
 
 	@Override
 	public AssociationType getAssociationType() {
 		return joinableType;
 	}
 
 	@Override
 	public Joinable getJoinable() {
 		return joinable;
 	}
 
 	@Override
 	public boolean isCollection() {
 		return true;
 	}
 
 	@Override
 	public boolean isManyToManyWith(JoinableAssociation other) {
 		QueryableCollection persister = ( QueryableCollection ) joinable;
 		if ( persister.isManyToMany() ) {
 			return persister.getElementType() == other.getAssociationType();
 		}
 		return false;
 	}
 
 	protected boolean isOneToOne() {
 		return false;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java
index d52323bc09..34d22b56ba 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java
@@ -1,91 +1,89 @@
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
 package org.hibernate.loader.internal;
 
 import java.util.Map;
 
 import org.hibernate.Filter;
 import org.hibernate.MappingException;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 
 /**
  * This class represents a joinable entity association.
  *
  * @author Gavin King
  */
 public class EntityJoinableAssociationImpl extends AbstractJoinableAssociationImpl {
 
 	private final AssociationType joinableType;
 	private final Joinable joinable;
 
 	public EntityJoinableAssociationImpl(
 			EntityFetch entityFetch,
 			CollectionReference currentCollectionReference,
 			String withClause,
-			boolean isNullable,
 			boolean hasRestriction,
 			Map<String, Filter> enabledFilters) throws MappingException {
 		super(
 				entityFetch,
 				entityFetch,
 				currentCollectionReference,
 				withClause,
-				isNullable,
 				hasRestriction,
 				enabledFilters
 		);
 		this.joinableType = entityFetch.getAssociationType();
 		this.joinable = (Joinable) entityFetch.getEntityPersister();
 	}
 
 	@Override
 	public AssociationType getAssociationType() {
 		return joinableType;
 	}
 
 	@Override
 	public Joinable getJoinable() {
 		return joinable;
 	}
 
 	@Override
 	public boolean isCollection() {
 		return false;
 	}
 
 	@Override
 	public boolean isManyToManyWith(JoinableAssociation other) {
 		return false;
 	}
 
 	protected boolean isOneToOne() {
 		EntityType entityType = (EntityType) joinableType;
 		return entityType.isOneToOne() /*&& entityType.isReferenceToPrimaryKey()*/;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
index 769f419583..aef90267f1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
@@ -1,231 +1,227 @@
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
 package org.hibernate.loader.internal;
 
 import java.util.ArrayDeque;
 import java.util.ArrayList;
 import java.util.Deque;
 import java.util.List;
 
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitor;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.LoadQueryBuilder;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.walking.spi.WalkingException;
 
 /**
  * @author Gail Badner
  */
 public class EntityLoadQueryBuilderImpl implements LoadQueryBuilder {
 	private final LoadQueryInfluencers loadQueryInfluencers;
 	private final LoadPlan loadPlan;
 	private final List<JoinableAssociation> associations;
 
 	public EntityLoadQueryBuilderImpl(
 			LoadQueryInfluencers loadQueryInfluencers,
 			LoadPlan loadPlan) {
 		this.loadQueryInfluencers = loadQueryInfluencers;
 		this.loadPlan = loadPlan;
 
 	    // TODO: the whole point of the following is to build associations.
 		// this could be done while building loadPlan (and be a part of the LoadPlan).
 		// Should it be?
 		LocalVisitationStrategy strategy = new LocalVisitationStrategy();
 		LoadPlanVisitor.visit( loadPlan, strategy );
 		this.associations = strategy.associations;
 	}
 
 	@Override
 	public String generateSql(
 			int batchSize,
 			SessionFactoryImplementor sessionFactory,
 			LoadQueryAliasResolutionContext aliasResolutionContext) {
 		return generateSql(
 				batchSize,
 				getOuterJoinLoadable().getKeyColumnNames(),
 				sessionFactory,
 				aliasResolutionContext
 		);
 	}
 
 	public String generateSql(
 			int batchSize,
 			String[] uniqueKey,
 			SessionFactoryImplementor sessionFactory,
 			LoadQueryAliasResolutionContext aliasResolutionContext) {
 		final EntityLoadQueryImpl loadQuery = new EntityLoadQueryImpl(
 				getRootEntityReturn(),
 				associations
 		);
 		return loadQuery.generateSql(
 				uniqueKey,
 				batchSize,
 				getRootEntityReturn().getLockMode(),
 				sessionFactory,
 				aliasResolutionContext );
 	}
 
 	private EntityReturn getRootEntityReturn() {
 		return (EntityReturn) loadPlan.getReturns().get( 0 );
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable() {
 		return (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 	}
 
 	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
 		private final List<JoinableAssociation> associations = new ArrayList<JoinableAssociation>();
 		private Deque<EntityReference> entityReferenceStack = new ArrayDeque<EntityReference>();
 		private Deque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
 
 		private EntityReturn entityRootReturn;
 
 		@Override
 		public void handleEntityReturn(EntityReturn rootEntityReturn) {
 			this.entityRootReturn = rootEntityReturn;
 		}
 
 		@Override
 		public void startingRootReturn(Return rootReturn) {
 			if ( !EntityReturn.class.isInstance( rootReturn ) ) {
 				throw new WalkingException(
 						String.format(
 								"Unexpected type of return; expected [%s]; instead it was [%s]",
 								EntityReturn.class.getName(),
 								rootReturn.getClass().getName()
 						)
 				);
 			}
 			this.entityRootReturn = (EntityReturn) rootReturn;
 			pushToStack( entityReferenceStack, entityRootReturn );
 		}
 
 		@Override
 		public void finishingRootReturn(Return rootReturn) {
 			if ( !EntityReturn.class.isInstance( rootReturn ) ) {
 				throw new WalkingException(
 						String.format(
 								"Unexpected type of return; expected [%s]; instead it was [%s]",
 								EntityReturn.class.getName(),
 								rootReturn.getClass().getName()
 						)
 				);
 			}
 			popFromStack( entityReferenceStack, entityRootReturn );
 		}
 
 		@Override
 		public void startingEntityFetch(EntityFetch entityFetch) {
-			final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) entityFetch.getOwner().retrieveFetchSourcePersister();
-			final int propertyNumber = ownerPersister.getEntityMetamodel().getPropertyIndex( entityFetch.getOwnerPropertyName() );
-			final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
 			EntityJoinableAssociationImpl assoc = new EntityJoinableAssociationImpl(
 					entityFetch,
 					getCurrentCollectionReference(),
 					"",    // getWithClause( entityFetch.getPropertyPath() )
-					isNullable,
 					false, // hasRestriction( entityFetch.getPropertyPath() )
 					loadQueryInfluencers.getEnabledFilters()
 			);
 			associations.add( assoc );
 			pushToStack( entityReferenceStack, entityFetch );
 		}
 
 		@Override
 		public void finishingEntityFetch(EntityFetch entityFetch) {
 			popFromStack( entityReferenceStack, entityFetch );
 		}
 
 		@Override
 		public void startingCollectionFetch(CollectionFetch collectionFetch) {
 			CollectionJoinableAssociationImpl assoc = new CollectionJoinableAssociationImpl(
 					collectionFetch,
 					getCurrentEntityReference(),
 					"",    // getWithClause( entityFetch.getPropertyPath() )
 					false, // hasRestriction( entityFetch.getPropertyPath() )
 					loadQueryInfluencers.getEnabledFilters()
 			);
 			associations.add( assoc );
 			pushToStack( collectionReferenceStack, collectionFetch );
 		}
 
 		@Override
 		public void finishingCollectionFetch(CollectionFetch collectionFetch) {
 			popFromStack( collectionReferenceStack, collectionFetch );
 		}
 
 		@Override
 		public void startingCompositeFetch(CompositeFetch fetch) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public void finishingCompositeFetch(CompositeFetch fetch) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public void finish(LoadPlan loadPlan) {
 			entityReferenceStack.clear();
 			collectionReferenceStack.clear();
 		}
 
 		private EntityReference getCurrentEntityReference() {
 			return entityReferenceStack.peekFirst() == null ? null : entityReferenceStack.peekFirst();
 		}
 
 		private CollectionReference getCurrentCollectionReference() {
 			return collectionReferenceStack.peekFirst() == null ? null : collectionReferenceStack.peekFirst();
 		}
 
 		private <T> void pushToStack(Deque<T> stack, T value) {
 			stack.push( value );
 		}
 
 		private <T> void popFromStack(Deque<T> stack, T expectedValue) {
 			T poppedValue = stack.pop();
 			if ( poppedValue != expectedValue ) {
 				throw new WalkingException(
 						String.format(
 								"Unexpected value from stack. Expected=[%s]; instead it was [%s].",
 								expectedValue,
 								poppedValue
 						)
 				);
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
index d829669a29..193a7e42f5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
@@ -1,313 +1,313 @@
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
 package org.hibernate.loader.internal;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.cfg.NotYetImplementedException;
-import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.DefaultEntityAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.GeneratedCollectionAliases;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.CompositeElementGraph;
+import org.hibernate.loader.plan.spi.CompositeIndexGraph;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.plan.spi.ScalarReturn;
 import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
-import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.type.EntityType;
 
 /**
  * Provides aliases that are used by load queries and ResultSet processors.
  *
  * @author Gail Badner
  */
 public class LoadQueryAliasResolutionContextImpl implements LoadQueryAliasResolutionContext {
 	private final Map<Return,String[]> aliasesByReturn;
 	private final Map<EntityReference,LoadQueryEntityAliasesImpl> aliasesByEntityReference =
 			new HashMap<EntityReference,LoadQueryEntityAliasesImpl>();
 	private final Map<CollectionReference,LoadQueryCollectionAliasesImpl> aliasesByCollectionReference =
 			new HashMap<CollectionReference,LoadQueryCollectionAliasesImpl>();
 	private final Map<JoinableAssociation,JoinableAssociationAliasesImpl> aliasesByJoinableAssociation =
 			new HashMap<JoinableAssociation, JoinableAssociationAliasesImpl>();
 	private final SessionFactoryImplementor sessionFactory;
 
 	private int currentAliasSuffix = 0;
 
 	public LoadQueryAliasResolutionContextImpl(
 			SessionFactoryImplementor sessionFactory,
 			int suffixSeed,
 			Map<Return,String[]> aliasesByReturn) {
 		this.sessionFactory = sessionFactory;
 		this.currentAliasSuffix = suffixSeed;
 
 		checkAliasesByReturn( aliasesByReturn );
 		this.aliasesByReturn = new HashMap<Return, String[]>( aliasesByReturn );
 	}
 
 	private static void checkAliasesByReturn(Map<Return, String[]> aliasesByReturn) {
 		if ( aliasesByReturn == null || aliasesByReturn.size() == 0 ) {
 			throw new IllegalArgumentException( "No return aliases defined" );
 		}
 		for ( Map.Entry<Return,String[]> entry : aliasesByReturn.entrySet() ) {
 			final Return aReturn = entry.getKey();
 			final String[] aliases = entry.getValue();
 			if ( aReturn == null ) {
 				throw new IllegalArgumentException( "null key found in aliasesByReturn" );
 			}
 			if ( aliases == null || aliases.length == 0 ) {
 				throw new IllegalArgumentException(
 						String.format( "No alias defined for [%s]", aReturn )
 				);
 			}
 			if ( ( aliases.length > 1 ) &&
 					( aReturn instanceof EntityReturn || aReturn instanceof CollectionReturn ) ) {
 				throw new IllegalArgumentException( String.format( "More than 1 alias defined for [%s]", aReturn ) );
 			}
 			for ( String alias : aliases ) {
 				if ( StringHelper.isEmpty( alias ) ) {
 					throw new IllegalArgumentException( String.format( "An alias for [%s] is null or empty.", aReturn ) );
 				}
 			}
 		}
 	}
 
 	@Override
 	public String resolveEntityReturnAlias(EntityReturn entityReturn) {
 		return getAndCheckReturnAliasExists( entityReturn )[ 0 ];
 	}
 
 	@Override
 	public String resolveCollectionReturnAlias(CollectionReturn collectionReturn) {
 		return getAndCheckReturnAliasExists( collectionReturn )[ 0 ];
 	}
 
 	@Override
 	public String[] resolveScalarReturnAliases(ScalarReturn scalarReturn) {
 		throw new NotYetImplementedException( "Cannot resolve scalar column aliases yet." );
 	}
 
 	private String[] getAndCheckReturnAliasExists(Return aReturn) {
 		// There is already a check for the appropriate number of aliases stored in aliasesByReturn,
 		// so just check for existence here.
 		final String[] aliases = aliasesByReturn.get( aReturn );
 		if ( aliases == null ) {
 			throw new IllegalStateException(
 					String.format( "No alias is defined for [%s]", aReturn )
 			);
 		}
 		return aliases;
 	}
 
 	@Override
 	public String resolveEntityTableAlias(EntityReference entityReference) {
 		return getOrGenerateLoadQueryEntityAliases( entityReference ).tableAlias;
 	}
 
 	@Override
 	public EntityAliases resolveEntityColumnAliases(EntityReference entityReference) {
 		return getOrGenerateLoadQueryEntityAliases( entityReference ).columnAliases;
 	}
 
 	@Override
 	public String resolveCollectionTableAlias(CollectionReference collectionReference) {
 		return getOrGenerateLoadQueryCollectionAliases( collectionReference ).tableAlias;
 	}
 
 	@Override
 	public CollectionAliases resolveCollectionColumnAliases(CollectionReference collectionReference) {
 		return getOrGenerateLoadQueryCollectionAliases( collectionReference ).collectionAliases;
 	}
 
 	@Override
 	public EntityAliases resolveCollectionElementColumnAliases(CollectionReference collectionReference) {
 		return getOrGenerateLoadQueryCollectionAliases( collectionReference ).collectionElementAliases;
 	}
 
 	@Override
 	public String resolveAssociationRhsTableAlias(JoinableAssociation joinableAssociation) {
 		return getOrGenerateJoinAssocationAliases( joinableAssociation ).rhsAlias;
 	}
 
 	@Override
 	public String resolveAssociationLhsTableAlias(JoinableAssociation joinableAssociation) {
 		return getOrGenerateJoinAssocationAliases( joinableAssociation ).lhsAlias;
 	}
 
 	@Override
 	public String[] resolveAssociationAliasedLhsColumnNames(JoinableAssociation joinableAssociation) {
 		return getOrGenerateJoinAssocationAliases( joinableAssociation ).aliasedLhsColumnNames;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	private String createSuffix() {
 		return Integer.toString( currentAliasSuffix++ ) + '_';
 	}
 
 	private LoadQueryEntityAliasesImpl getOrGenerateLoadQueryEntityAliases(EntityReference entityReference) {
 		LoadQueryEntityAliasesImpl aliases = aliasesByEntityReference.get( entityReference );
 		if ( aliases == null ) {
 			final EntityPersister entityPersister = entityReference.getEntityPersister();
 			aliases = new LoadQueryEntityAliasesImpl(
 					createTableAlias( entityPersister ),
 					createEntityAliases( entityPersister )
 			);
 			aliasesByEntityReference.put( entityReference, aliases );
 		}
 		return aliases;
 	}
 
 	private LoadQueryCollectionAliasesImpl getOrGenerateLoadQueryCollectionAliases(CollectionReference collectionReference) {
 		LoadQueryCollectionAliasesImpl aliases = aliasesByCollectionReference.get( collectionReference );
 		if ( aliases == null ) {
 			final CollectionPersister collectionPersister = collectionReference.getCollectionPersister();
 			aliases = new LoadQueryCollectionAliasesImpl(
 					createTableAlias( collectionPersister.getRole() ),
 					createCollectionAliases( collectionPersister ),
 					createCollectionElementAliases( collectionPersister )
 			);
 			aliasesByCollectionReference.put( collectionReference, aliases );
 		}
 		return aliases;
 	}
 
 	private JoinableAssociationAliasesImpl getOrGenerateJoinAssocationAliases(JoinableAssociation joinableAssociation) {
 		JoinableAssociationAliasesImpl aliases = aliasesByJoinableAssociation.get( joinableAssociation );
 		if ( aliases == null ) {
 			final Fetch currentFetch = joinableAssociation.getCurrentFetch();
 			final String lhsAlias;
 			if ( EntityReference.class.isInstance( currentFetch.getOwner() ) ) {
 				lhsAlias = resolveEntityTableAlias( (EntityReference) currentFetch.getOwner() );
 			}
+			else if ( CompositeElementGraph.class.isInstance( currentFetch.getOwner() ) ) {
+				CompositeElementGraph compositeElementGraph = (CompositeElementGraph) currentFetch.getOwner();
+				lhsAlias = resolveCollectionTableAlias( compositeElementGraph.getCollectionReference() );
+			}
+			else if ( CompositeIndexGraph.class.isInstance( currentFetch.getOwner() ) ) {
+				CompositeIndexGraph compositeIndexGraph = (CompositeIndexGraph) currentFetch.getOwner();
+				lhsAlias = resolveCollectionTableAlias( compositeIndexGraph.getCollectionReference() );
+			}
 			else {
-				throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference yet." );
+				throw new NotYetImplementedException( "Cannot determine LHS alias for FetchOwner." );
 			}
+			final String[] aliasedLhsColumnNames = StringHelper.qualify( lhsAlias, currentFetch.getColumnNames() );
 			final String rhsAlias;
 			if ( EntityReference.class.isInstance( currentFetch ) ) {
 				rhsAlias = resolveEntityTableAlias( (EntityReference) currentFetch );
 			}
 			else if ( CollectionReference.class.isInstance( joinableAssociation.getCurrentFetch() ) ) {
 				rhsAlias = resolveCollectionTableAlias( (CollectionReference) currentFetch );
 			}
 			else {
 				throw new NotYetImplementedException( "Cannot determine RHS alis for a fetch that is not an EntityReference or CollectionReference." );
 			}
 
 			// TODO: can't this be found in CollectionAliases or EntityAliases? should be moved to LoadQueryAliasResolutionContextImpl
-			final OuterJoinLoadable fetchSourcePersister = (OuterJoinLoadable) currentFetch.getOwner().retrieveFetchSourcePersister();
-			final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( currentFetch.getOwnerPropertyName() );
-			final String[] aliasedLhsColumnNames = JoinHelper.getAliasedLHSColumnNames(
-					joinableAssociation.getAssociationType(),
-					lhsAlias,
-					propertyNumber,
-					fetchSourcePersister,
-					sessionFactory
-			);
 
 			aliases = new JoinableAssociationAliasesImpl( lhsAlias, aliasedLhsColumnNames, rhsAlias );
 			aliasesByJoinableAssociation.put( joinableAssociation, aliases );
 		}
 		return aliases;
 	}
 
 	private String createTableAlias(EntityPersister entityPersister) {
 		return createTableAlias( StringHelper.unqualifyEntityName( entityPersister.getEntityName() ) );
 	}
 
 	private String createTableAlias(String name) {
 		return StringHelper.generateAlias( name ) + createSuffix();
 	}
 
 	private EntityAliases createEntityAliases(EntityPersister entityPersister) {
 		return new DefaultEntityAliases( (Loadable) entityPersister, createSuffix() );
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
 
 	private static class LoadQueryEntityAliasesImpl {
 		private final String tableAlias;
 		private final EntityAliases columnAliases;
 
 		public LoadQueryEntityAliasesImpl(String tableAlias, EntityAliases columnAliases) {
 			this.tableAlias = tableAlias;
 			this.columnAliases = columnAliases;
 		}
 	}
 
 	private static class LoadQueryCollectionAliasesImpl {
 		private final String tableAlias;
 		private final CollectionAliases collectionAliases;
 		private final EntityAliases collectionElementAliases;
 
 		public LoadQueryCollectionAliasesImpl(
 				String tableAlias,
 				CollectionAliases collectionAliases,
 				EntityAliases collectionElementAliases) {
 			this.tableAlias = tableAlias;
 			this.collectionAliases = collectionAliases;
 			this.collectionElementAliases = collectionElementAliases;
 		}
 	}
 
 	private static class JoinableAssociationAliasesImpl {
 		private final String lhsAlias;
 		private final String[] aliasedLhsColumnNames;
 		private final String rhsAlias;
 
 		public JoinableAssociationAliasesImpl(
 				String lhsAlias,
 				String[] aliasedLhsColumnNames,
 				String rhsAlias) {
 			this.lhsAlias = lhsAlias;
 			this.aliasedLhsColumnNames = aliasedLhsColumnNames;
 			this.rhsAlias = rhsAlias;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
index ffb5656f12..2e6293c1b5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
@@ -1,95 +1,145 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
-import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
+ * @author Gail Badner
  */
 public abstract class AbstractFetchOwner extends AbstractPlanNode implements FetchOwner {
-	private final LockMode lockMode;
 
 	private List<Fetch> fetches;
 
-	public AbstractFetchOwner(SessionFactoryImplementor factory, LockMode lockMode) {
+	public AbstractFetchOwner(SessionFactoryImplementor factory) {
 		super( factory );
-		this.lockMode = lockMode;
 		validate();
 	}
 
 	private void validate() {
 	}
 
 	/**
 	 * A "copy" constructor.  Used while making clones/copies of this.
 	 *
-	 * @param original
+	 * @param original - the original object to copy.
 	 */
 	protected AbstractFetchOwner(AbstractFetchOwner original, CopyContext copyContext) {
 		super( original );
-		this.lockMode = original.lockMode;
 		validate();
 
 		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
 		if ( fetches == null || fetches.size() == 0 ) {
 			this.fetches = Collections.emptyList();
 		}
 		else {
+			// TODO: don't think this is correct...
 			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
 			for ( Fetch fetch : fetches ) {
 				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
 			}
 			this.fetches = fetchesCopy;
 		}
 		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
 	}
 
-	public LockMode getLockMode() {
-		return lockMode;
-	}
-
-	@Override
 	public void addFetch(Fetch fetch) {
 		if ( fetch.getOwner() != this ) {
 			throw new IllegalArgumentException( "Fetch and owner did not match" );
 		}
 
 		if ( fetches == null ) {
 			fetches = new ArrayList<Fetch>();
 		}
 
 		fetches.add( fetch );
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
 	}
+
+	protected abstract FetchOwnerDelegate getFetchOwnerDelegate();
+
+	@Override
+	public boolean isNullable(Fetch fetch) {
+		return getFetchOwnerDelegate().isNullable( fetch );
+	}
+
+	@Override
+	public Type getType(Fetch fetch) {
+		return getFetchOwnerDelegate().getType( fetch );
+	}
+
+	@Override
+	public String[] getColumnNames(Fetch fetch) {
+		return getFetchOwnerDelegate().getColumnNames( fetch );
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardEntityFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
index ddb4b4491f..0b3689844d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
@@ -1,103 +1,111 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.HibernateException;
-import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractSingularAttributeFetch extends AbstractFetchOwner implements Fetch {
 	private final FetchOwner owner;
 	private final String ownerProperty;
 	private final FetchStrategy fetchStrategy;
 
 	private final PropertyPath propertyPath;
 
 	public AbstractSingularAttributeFetch(
 			SessionFactoryImplementor factory,
-			LockMode lockMode,
 			FetchOwner owner,
 			String ownerProperty,
 			FetchStrategy fetchStrategy) {
-		super( factory, lockMode );
+		super( factory );
 		this.owner = owner;
 		this.ownerProperty = ownerProperty;
 		this.fetchStrategy = fetchStrategy;
 
 		owner.addFetch( this );
 
 		this.propertyPath = owner.getPropertyPath().append( ownerProperty );
 	}
 
 	public AbstractSingularAttributeFetch(
 			AbstractSingularAttributeFetch original,
 			CopyContext copyContext,
 			FetchOwner fetchOwnerCopy) {
 		super( original, copyContext );
 		this.owner = fetchOwnerCopy;
 		this.ownerProperty = original.ownerProperty;
 		this.fetchStrategy = original.fetchStrategy;
 		this.propertyPath = original.propertyPath;
 	}
 
 	@Override
 	public FetchOwner getOwner() {
 		return owner;
 	}
 
 	@Override
 	public String getOwnerPropertyName() {
 		return ownerProperty;
 	}
 
 	@Override
+	public boolean isNullable() {
+		return owner.isNullable( this );
+	}
+
+	@Override
+	public String[] getColumnNames() {
+		return owner.getColumnNames( this );
+	}
+
+	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 		if ( fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 			if ( this.fetchStrategy.getStyle() != FetchStyle.JOIN ) {
 				throw new HibernateException( "Cannot specify join fetch from owner that is a non-joined fetch" );
 			}
 		}
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
 	public String toString() {
 		return "Fetch(" + propertyPath.getFullPath() + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
index fef9cd1b65..242daf427c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
@@ -1,99 +1,111 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
+import org.hibernate.persister.entity.Joinable;
 import org.hibernate.type.CollectionType;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionFetch extends AbstractCollectionReference implements Fetch {
 	private final FetchOwner fetchOwner;
 	private final FetchStrategy fetchStrategy;
 
 	public CollectionFetch(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			FetchOwner fetchOwner,
 			FetchStrategy fetchStrategy,
 			String ownerProperty) {
 		super(
 				sessionFactory,
 				lockMode,
 				sessionFactory.getCollectionPersister(
 						fetchOwner.retrieveFetchSourcePersister().getEntityName() + '.' + ownerProperty
 				),
 				fetchOwner.getPropertyPath().append( ownerProperty )
 		);
 		this.fetchOwner = fetchOwner;
 		this.fetchStrategy = fetchStrategy;
 		fetchOwner.addFetch( this );
 	}
 
 	protected CollectionFetch(CollectionFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext );
 		this.fetchOwner = fetchOwnerCopy;
 		this.fetchStrategy = original.fetchStrategy;
 	}
 
 	@Override
 	public FetchOwner getOwner() {
 		return fetchOwner;
 	}
 
 	@Override
 	public String getOwnerPropertyName() {
 		return getPropertyPath().getProperty();
 	}
 
 	@Override
+	public boolean isNullable() {
+		return true;
+	}
+
+	@Override
+	public String[] getColumnNames() {
+		return getOwner().getColumnNames( this );
+	}
+
+	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
 	@Override
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		//To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public CollectionFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		copyContext.getReturnGraphVisitationStrategy().startingCollectionFetch( this );
 		final CollectionFetch copy = new CollectionFetch( this, copyContext, fetchOwnerCopy );
 		copyContext.getReturnGraphVisitationStrategy().finishingCollectionFetch( this );
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
index b1ddd79d30..e2d23540df 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
@@ -1,118 +1,83 @@
 package org.hibernate.loader.plan.spi;
 
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.List;
-
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
-public class CompositeElementGraph extends AbstractPlanNode implements FetchableCollectionElement {
+public class CompositeElementGraph extends AbstractFetchOwner implements FetchableCollectionElement {
 	private final CollectionReference collectionReference;
 	private final PropertyPath propertyPath;
 	private final CollectionPersister collectionPersister;
-
-	private List<Fetch> fetches;
+	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	public CompositeElementGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath collectionPath) {
 		super( sessionFactory );
 
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.propertyPath = collectionPath.append( "<elements>" );
+		this.fetchOwnerDelegate = new CompositeFetchOwnerDelegate(
+				sessionFactory,
+				(CompositeType) collectionPersister.getElementType(),
+				( (QueryableCollection) collectionPersister ).getElementColumnNames()
+		);
 	}
 
 	public CompositeElementGraph(CompositeElementGraph original, CopyContext copyContext) {
-		super( original );
+		super( original, copyContext );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionPersister;
 		this.propertyPath = original.propertyPath;
-
-		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
-		if ( fetches == null || fetches.size() == 0 ) {
-			this.fetches = Collections.emptyList();
-		}
-		else {
-			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
-			for ( Fetch fetch : fetches ) {
-				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
-			}
-			this.fetches = fetchesCopy;
-		}
-		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
-	}
-
-	@Override
-	public void addFetch(Fetch fetch) {
-		if ( fetches == null ) {
-			fetches = new ArrayList<Fetch>();
-		}
-		fetches.add( fetch );
+		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	@Override
-	public Fetch[] getFetches() {
-		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return collectionPersister.getOwnerEntityPersister();
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
-	public CollectionFetch buildCollectionFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		throw new HibernateException( "Collection composite element cannot define collections" );
+	public CompositeElementGraph makeCopy(CopyContext copyContext) {
+		return new CompositeElementGraph( this, copyContext );
 	}
 
 	@Override
-	public EntityFetch buildEntityFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardEntityFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
+	protected FetchOwnerDelegate getFetchOwnerDelegate() {
+		return fetchOwnerDelegate;
 	}
 
 	@Override
-	public CompositeFetch buildCompositeFetch(
-			CompositionDefinition attributeDefinition,
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
-	}
-
-	@Override
-	public CompositeElementGraph makeCopy(CopyContext copyContext) {
-		return new CompositeElementGraph( this, copyContext );
+		throw new HibernateException( "Collection composite element cannot define collections" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
index b002c7b567..c1c043308c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
@@ -1,107 +1,119 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
-
-import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeFetch extends AbstractSingularAttributeFetch {
 	public static final FetchStrategy FETCH_PLAN = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 
+	private final FetchOwnerDelegate delegate;
+
 	public CompositeFetch(
 			SessionFactoryImplementor sessionFactory,
 			FetchOwner owner,
 			String ownerProperty) {
-		super( sessionFactory, LockMode.NONE, owner, ownerProperty, FETCH_PLAN );
+		super( sessionFactory, owner, ownerProperty, FETCH_PLAN );
+		this.delegate = new CompositeFetchOwnerDelegate(
+				sessionFactory,
+				(CompositeType) getOwner().getType( this ),
+				getOwner().getColumnNames( this )
+		);
 	}
 
 	public CompositeFetch(CompositeFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
+		this.delegate = original.getFetchOwnerDelegate();
+	}
+
+	@Override
+	protected FetchOwnerDelegate getFetchOwnerDelegate() {
+		return delegate;
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return getOwner().retrieveFetchSourcePersister();
 	}
 
 	@Override
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public EntityFetch buildEntityFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardEntityFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
 				loadPlanBuildingContext
 		);
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
 	}
 
 	@Override
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		//To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public CompositeFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		copyContext.getReturnGraphVisitationStrategy().startingCompositeFetch( this );
 		final CompositeFetch copy = new CompositeFetch( this, copyContext, fetchOwnerCopy );
 		copyContext.getReturnGraphVisitationStrategy().finishingCompositeFetch( this );
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetchOwnerDelegate.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetchOwnerDelegate.java
new file mode 100644
index 0000000000..8c530eb5d2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetchOwnerDelegate.java
@@ -0,0 +1,98 @@
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
+package org.hibernate.loader.plan.spi;
+
+import java.util.Arrays;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.Type;
+
+/**
+ * @author Gail Badner
+ */
+public class CompositeFetchOwnerDelegate implements FetchOwnerDelegate {
+	private final SessionFactoryImplementor sessionFactory;
+	private final CompositeType compositeType;
+	private final String[] columnNames;
+
+	public CompositeFetchOwnerDelegate(
+			SessionFactoryImplementor sessionFactory,
+			CompositeType compositeType,
+			String[] columnNames) {
+		this.sessionFactory = sessionFactory;
+		this.compositeType = compositeType;
+		this.columnNames = columnNames;
+	}
+
+	@Override
+	public boolean isNullable(Fetch fetch) {
+		return compositeType.getPropertyNullability()[ determinePropertyIndex( fetch ) ];
+	}
+
+	@Override
+	public Type getType(Fetch fetch) {
+		return compositeType.getSubtypes()[ determinePropertyIndex( fetch ) ];
+	}
+
+	@Override
+	public String[] getColumnNames(Fetch fetch) {
+		// TODO: probably want to cache this
+		int begin = 0;
+		String[] subColumnNames = null;
+		for ( int i = 0; i < compositeType.getSubtypes().length; i++ ) {
+			final int columnSpan = compositeType.getSubtypes()[i].getColumnSpan( sessionFactory );
+			subColumnNames = ArrayHelper.slice( columnNames, begin, columnSpan );
+			if ( compositeType.getPropertyNames()[ i ].equals( fetch.getOwnerPropertyName() ) ) {
+				break;
+			}
+			begin += columnSpan;
+		}
+		return subColumnNames;
+	}
+
+	private int determinePropertyIndex(Fetch fetch) {
+		// TODO: probably want to cache this
+		final String[] subAttributeNames = compositeType.getPropertyNames();
+		int subAttributeIndex = -1;
+		for ( int i = 0; i < subAttributeNames.length ; i++ ) {
+			if ( subAttributeNames[ i ].equals( fetch.getOwnerPropertyName() ) ) {
+				subAttributeIndex = i;
+				break;
+			}
+		}
+		if ( subAttributeIndex == -1 ) {
+			throw new WalkingException(
+					String.format(
+							"Owner property [%s] not found in composite properties [%s]",
+							fetch.getOwnerPropertyName(),
+							Arrays.asList( subAttributeNames )
+					)
+			);
+		}
+		return subAttributeIndex;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
index bccc7b1cd7..f4f4eb7fe4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
@@ -1,117 +1,81 @@
 package org.hibernate.loader.plan.spi;
 
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.List;
-
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
-public class CompositeIndexGraph extends AbstractPlanNode implements FetchableCollectionIndex {
+public class CompositeIndexGraph extends AbstractFetchOwner implements FetchableCollectionIndex {
 	private final CollectionReference collectionReference;
 	private final PropertyPath propertyPath;
 	private final CollectionPersister collectionPersister;
-
-	private List<Fetch> fetches;
+	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	public CompositeIndexGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath propertyPath) {
 		super( sessionFactory );
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.propertyPath = propertyPath.append( "<index>" );
+		this.fetchOwnerDelegate = new CompositeFetchOwnerDelegate(
+				sessionFactory,
+				(CompositeType) collectionPersister.getIndexType(),
+				( (QueryableCollection) collectionPersister ).getIndexColumnNames()
+		);
 	}
 
 	protected CompositeIndexGraph(CompositeIndexGraph original, CopyContext copyContext) {
-		super( original );
+		super( original, copyContext );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionPersister;
 		this.propertyPath = original.propertyPath;
-
-		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
-		if ( fetches == null || fetches.size() == 0 ) {
-			this.fetches = Collections.emptyList();
-		}
-		else {
-			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
-			for ( Fetch fetch : fetches ) {
-				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
-			}
-			this.fetches = fetchesCopy;
-		}
-		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
-	}
-
-	@Override
-	public void addFetch(Fetch fetch) {
-		if ( fetches == null ) {
-			fetches = new ArrayList<Fetch>();
-		}
-		fetches.add( fetch );
-	}
-
-	@Override
-	public Fetch[] getFetches() {
-		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return collectionPersister.getOwnerEntityPersister();
 	}
 
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
+	}
+
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
-	public CollectionFetch buildCollectionFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		throw new HibernateException( "Composite index cannot define collections" );
+	public CompositeIndexGraph makeCopy(CopyContext copyContext) {
+		return new CompositeIndexGraph( this, copyContext );
 	}
 
 	@Override
-	public EntityFetch buildEntityFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardEntityFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
+	protected FetchOwnerDelegate getFetchOwnerDelegate() {
+		return fetchOwnerDelegate;
 	}
 
-	@Override
-	public CompositeFetch buildCompositeFetch(
-			CompositionDefinition attributeDefinition,
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+		throw new HibernateException( "Composite index cannot define collections" );
 	}
 
-	@Override
-	public CompositeIndexGraph makeCopy(CopyContext copyContext) {
-		return new CompositeIndexGraph( this, copyContext );
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
index d779a119ef..a3ccde17d7 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
@@ -1,163 +1,107 @@
 package org.hibernate.loader.plan.spi;
 
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.List;
-
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.AssociationType;
 
 /**
  * @author Steve Ebersole
  */
-public class EntityElementGraph extends AbstractPlanNode implements FetchableCollectionElement, EntityReference {
+public class EntityElementGraph extends AbstractFetchOwner implements FetchableCollectionElement, EntityReference {
 	private final CollectionReference collectionReference;
 	private final CollectionPersister collectionPersister;
 	private final AssociationType elementType;
 	private final EntityPersister elementPersister;
 	private final PropertyPath propertyPath;
-
-	private List<Fetch> fetches;
+	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	private IdentifierDescription identifierDescription;
 
 	public EntityElementGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath collectionPath) {
 		super( sessionFactory );
 
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.elementType = (AssociationType) collectionPersister.getElementType();
 		this.elementPersister = (EntityPersister) this.elementType.getAssociatedJoinable( sessionFactory() );
-		this.propertyPath = collectionPath.append( "<elements>" );
+		this.propertyPath = collectionPath;
+		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( elementPersister );
 	}
 
 	public EntityElementGraph(EntityElementGraph original, CopyContext copyContext) {
-		super( original );
+		super( original, copyContext );
 
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionReference.getCollectionPersister();
 		this.elementType = original.elementType;
 		this.elementPersister = original.elementPersister;
 		this.propertyPath = original.propertyPath;
-
-		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
-		if ( fetches == null || fetches.size() == 0 ) {
-			this.fetches = Collections.emptyList();
-		}
-		else {
-			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
-			for ( Fetch fetch : fetches ) {
-				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
-			}
-			this.fetches = fetchesCopy;
-		}
-		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
+		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return null;
 	}
 
 	@Override
 	public EntityReference getEntityReference() {
 		return this;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return elementPersister;
 	}
 
 	@Override
 	public IdentifierDescription getIdentifierDescription() {
 		return identifierDescription;
 	}
 
 	@Override
-	public void addFetch(Fetch fetch) {
-		if ( fetches == null ) {
-			fetches = new ArrayList<Fetch>();
-		}
-		fetches.add( fetch );
-	}
-
-	@Override
-	public Fetch[] getFetches() {
-		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
-	}
-
-	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return elementPersister;
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
-	public CollectionFetch buildCollectionFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
-	}
-
-	@Override
-	public EntityFetch buildEntityFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardEntityFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
-	}
-
-	@Override
-	public CompositeFetch buildCompositeFetch(
-			CompositionDefinition attributeDefinition,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
-	}
-
-	@Override
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
 	@Override
 	public EntityElementGraph makeCopy(CopyContext copyContext) {
 		return new EntityElementGraph( this, copyContext );
 	}
 
 	@Override
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
+	}
+
+	@Override
 	public String toString() {
 		return "EntityElementGraph(collection=" + collectionPersister.getRole() + ", type=" + elementPersister.getEntityName() + ")";
 	}
+
+	@Override
+	protected FetchOwnerDelegate getFetchOwnerDelegate() {
+		return fetchOwnerDelegate;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
index 124b3f7944..9842166561 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
@@ -1,271 +1,249 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.LockMode;
 import org.hibernate.WrongClassException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
-public class EntityFetch extends AbstractSingularAttributeFetch implements EntityReference {
+public class EntityFetch extends AbstractSingularAttributeFetch implements EntityReference, Fetch {
 
 	private final EntityType associationType;
 	private final EntityPersister persister;
+	private final LockMode lockMode;
+	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	private IdentifierDescription identifierDescription;
 
 	public EntityFetch(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			FetchOwner owner,
 			String ownerProperty,
 			EntityType entityType,
 			FetchStrategy fetchStrategy) {
-		super( sessionFactory, lockMode, owner, ownerProperty, fetchStrategy );
+		super( sessionFactory, owner, ownerProperty, fetchStrategy );
 
 		this.associationType = entityType;
 		this.persister = sessionFactory.getEntityPersister( associationType.getAssociatedEntityName() );
+		this.lockMode = lockMode;
+		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( persister );
 	}
 
 	/**
 	 * Copy constructor.
 	 *
 	 * @param original The original fetch
 	 * @param copyContext Access to contextual needs for the copy operation
 	 */
 	protected EntityFetch(EntityFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
 		this.associationType = original.associationType;
 		this.persister = original.persister;
+		this.lockMode = original.lockMode;
+		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	public EntityType getAssociationType() {
 		return associationType;
 	}
 
 	@Override
 	public EntityReference getEntityReference() {
 		return this;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	@Override
 	public IdentifierDescription getIdentifierDescription() {
 		return identifierDescription;
 	}
 
 	@Override
-	public EntityPersister retrieveFetchSourcePersister() {
-		return persister;
-	}
-
-
-	@Override
-	public CollectionFetch buildCollectionFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
-	}
-
-	@Override
-	public EntityFetch buildEntityFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardEntityFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
+	public LockMode getLockMode() {
+		return lockMode;
 	}
 
 	@Override
-	public CompositeFetch buildCompositeFetch(
-			CompositionDefinition attributeDefinition,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	public EntityPersister retrieveFetchSourcePersister() {
+		return persister;
 	}
 
 	@Override
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
 	@Override
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		EntityKey entityKey = context.getDictatedRootEntityKey();
 		if ( entityKey != null ) {
 			context.getIdentifierResolutionContext( this ).registerEntityKey( entityKey );
 			return;
 		}
 
 		identifierDescription.hydrate( resultSet, context );
 
 		for ( Fetch fetch : getFetches() ) {
 			fetch.hydrate( resultSet, context );
 		}
 	}
 
 	@Override
 	public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		final ResultSetProcessingContext.IdentifierResolutionContext identifierResolutionContext = context.getIdentifierResolutionContext( this );
 		EntityKey entityKey = identifierResolutionContext.getEntityKey();
 		if ( entityKey == null ) {
 			entityKey = identifierDescription.resolve( resultSet, context );
 			if ( entityKey == null ) {
 				// register the non-existence (though only for one-to-one associations)
 				if ( associationType.isOneToOne() ) {
 					// first, find our owner's entity-key...
 					final EntityKey ownersEntityKey = context.getIdentifierResolutionContext( (EntityReference) getOwner() ).getEntityKey();
 					if ( ownersEntityKey != null ) {
 						context.getSession().getPersistenceContext()
 								.addNullProperty( ownersEntityKey, associationType.getPropertyName() );
 					}
 				}
 			}
 
 			identifierResolutionContext.registerEntityKey( entityKey );
 
 			for ( Fetch fetch : getFetches() ) {
 				fetch.resolve( resultSet, context );
 			}
 		}
 
 		return entityKey;
 	}
 
 	public EntityKey resolveInIdentifier(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		// todo : may not need to do this if entitykey is already part of the resolution context
 
 		final EntityKey entityKey = resolve( resultSet, context );
 
 		final Object existing = context.getSession().getEntityUsingInterceptor( entityKey );
 
 		if ( existing != null ) {
 			if ( !persister.isInstance( existing ) ) {
 				throw new WrongClassException(
 						"loaded object was of wrong class " + existing.getClass(),
 						entityKey.getIdentifier(),
 						persister.getEntityName()
 				);
 			}
 
 			if ( getLockMode() != null && getLockMode() != LockMode.NONE ) {
 				final boolean isVersionCheckNeeded = persister.isVersioned()
 						&& context.getSession().getPersistenceContext().getEntry( existing ).getLockMode().lessThan( getLockMode() );
 
 				// we don't need to worry about existing version being uninitialized because this block isn't called
 				// by a re-entrant load (re-entrant loads _always_ have lock mode NONE)
 				if ( isVersionCheckNeeded ) {
 					//we only check the version when _upgrading_ lock modes
 					context.checkVersion(
 							resultSet,
 							persister,
 							context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( this ),
 							entityKey,
 							existing
 					);
 					//we need to upgrade the lock mode to the mode requested
 					context.getSession().getPersistenceContext().getEntry( existing ).setLockMode( getLockMode() );
 				}
 			}
 		}
 		else {
 			final String concreteEntityTypeName = context.getConcreteEntityTypeName(
 					resultSet,
 					persister,
 					context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( this ),
 					entityKey
 			);
 
 			final Object entityInstance = context.getSession().instantiate(
 					concreteEntityTypeName,
 					entityKey.getIdentifier()
 			);
 
 			//need to hydrate it.
 
 			// grab its state from the ResultSet and keep it in the Session
 			// (but don't yet initialize the object itself)
 			// note that we acquire LockMode.READ even if it was not requested
 			LockMode acquiredLockMode = getLockMode() == LockMode.NONE ? LockMode.READ : getLockMode();
 
 			context.loadFromResultSet(
 					resultSet,
 					entityInstance,
 					concreteEntityTypeName,
 					entityKey,
 					context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( this ),
 					acquiredLockMode,
 					persister,
 					getFetchStrategy().getTiming() == FetchTiming.IMMEDIATE,
 					associationType
 			);
 
 			// materialize associations (and initialize the object) later
 			context.registerHydratedEntity( persister, entityKey, entityInstance );
 		}
 
 		return entityKey;
 	}
 
 	@Override
 	public String toString() {
 		return "EntityFetch(" + getPropertyPath().getFullPath() + " -> " + persister.getEntityName() + ")";
 	}
 
 	@Override
 	public EntityFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		copyContext.getReturnGraphVisitationStrategy().startingEntityFetch( this );
 		final EntityFetch copy = new EntityFetch( this, copyContext, fetchOwnerCopy );
 		copyContext.getReturnGraphVisitationStrategy().finishingEntityFetch( this );
 		return copy;
 	}
+
+	@Override
+	protected FetchOwnerDelegate getFetchOwnerDelegate() {
+		return fetchOwnerDelegate;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetchOwnerDelegate.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetchOwnerDelegate.java
new file mode 100644
index 0000000000..7ef801df07
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetchOwnerDelegate.java
@@ -0,0 +1,72 @@
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
+package org.hibernate.loader.plan.spi;
+
+import org.hibernate.engine.internal.JoinHelper;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.type.AssociationType;
+import org.hibernate.type.Type;
+
+/**
+ * @author Gail Badner
+ */
+public class EntityFetchOwnerDelegate implements FetchOwnerDelegate {
+	private final EntityPersister entityPersister;
+
+	public EntityFetchOwnerDelegate(EntityPersister entityPersister) {
+		this.entityPersister = entityPersister;
+	}
+
+	@Override
+	public boolean isNullable(Fetch fetch) {
+		return entityPersister.getPropertyNullability()[ determinePropertyIndex( fetch ) ];
+	}
+
+	@Override
+	public Type getType(Fetch fetch) {
+		return entityPersister.getPropertyTypes()[ determinePropertyIndex( fetch ) ];
+	}
+
+	@Override
+	public String[] getColumnNames(Fetch fetch) {
+		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) entityPersister;
+		Type fetchType = getType( fetch );
+		if ( fetchType.isAssociationType() ) {
+			return JoinHelper.getLHSColumnNames(
+					(AssociationType) fetchType,
+					determinePropertyIndex( fetch ),
+					outerJoinLoadable,
+					outerJoinLoadable.getFactory()
+			);
+		}
+		else {
+			return outerJoinLoadable.getPropertyColumnNames( determinePropertyIndex( fetch ) );
+		}
+	}
+
+	private int determinePropertyIndex(Fetch fetch) {
+		return entityPersister.getEntityMetamodel().getPropertyIndex( fetch.getOwnerPropertyName() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
index ed12d31a99..384b8e98d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
@@ -1,179 +1,118 @@
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
 package org.hibernate.loader.plan.spi;
 
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.List;
-
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.AssociationType;
 
 /**
  * @author Steve Ebersole
  */
-public class EntityIndexGraph extends AbstractPlanNode implements FetchableCollectionIndex, EntityReference {
+public class EntityIndexGraph extends AbstractFetchOwner implements FetchableCollectionIndex, EntityReference {
 	private final CollectionReference collectionReference;
 	private final CollectionPersister collectionPersister;
 	private final AssociationType indexType;
 	private final EntityPersister indexPersister;
 	private final PropertyPath propertyPath;
-
-	private List<Fetch> fetches;
+	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	private IdentifierDescription identifierDescription;
 
 	public EntityIndexGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath collectionPath) {
 		super( sessionFactory );
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.indexType = (AssociationType) collectionPersister.getIndexType();
 		this.indexPersister = (EntityPersister) this.indexType.getAssociatedJoinable( sessionFactory() );
 		this.propertyPath = collectionPath.append( "<index>" ); // todo : do we want the <index> part?
+		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( indexPersister );
 	}
 
 	public EntityIndexGraph(EntityIndexGraph original, CopyContext copyContext) {
-		super( original );
+		super( original, copyContext );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionReference.getCollectionPersister();
 		this.indexType = original.indexType;
 		this.indexPersister = original.indexPersister;
 		this.propertyPath = original.propertyPath;
-
-		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
-		if ( fetches == null || fetches.size() == 0 ) {
-			this.fetches = Collections.emptyList();
-		}
-		else {
-			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
-			for ( Fetch fetch : fetches ) {
-				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
-			}
-			this.fetches = fetchesCopy;
-		}
-		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
+		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return null;
 	}
 
 	@Override
 	public EntityReference getEntityReference() {
 		return this;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return indexPersister;
 	}
 
 	@Override
 	public IdentifierDescription getIdentifierDescription() {
 		return identifierDescription;
 	}
 
 	@Override
-	public void addFetch(Fetch fetch) {
-		if ( fetches == null ) {
-			fetches = new ArrayList<Fetch>();
-		}
-		fetches.add( fetch );
-	}
-
-	@Override
-	public Fetch[] getFetches() {
-		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
-	}
-
-	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return indexPersister;
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
-	public CollectionFetch buildCollectionFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
-	}
-
-	@Override
-	public EntityFetch buildEntityFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardEntityFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
-	}
-
-	@Override
-	public CompositeFetch buildCompositeFetch(
-			CompositionDefinition attributeDefinition,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
-	}
-
-	@Override
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
 	@Override
 	public EntityIndexGraph makeCopy(CopyContext copyContext) {
 		return new EntityIndexGraph( this, copyContext );
 	}
+
+	@Override
+	protected FetchOwnerDelegate getFetchOwnerDelegate() {
+		return fetchOwnerDelegate;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
index cb15874c8b..7680e4a30f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
@@ -1,210 +1,186 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 import static org.hibernate.loader.spi.ResultSetProcessingContext.IdentifierResolutionContext;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReturn extends AbstractFetchOwner implements Return, EntityReference, CopyableReturn {
 
 	private final EntityPersister persister;
 
 	private final PropertyPath propertyPath = new PropertyPath(); // its a root
 
+	private final LockMode lockMode;
+
+	private final FetchOwnerDelegate fetchOwnerDelegate;
+
 	private IdentifierDescription identifierDescription;
 
 	public EntityReturn(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			String entityName) {
-		super( sessionFactory, lockMode );
-
+		super( sessionFactory );
 		this.persister = sessionFactory.getEntityPersister( entityName );
+		this.lockMode = lockMode;
+		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( persister );
 	}
 
 	protected EntityReturn(EntityReturn original, CopyContext copyContext) {
 		super( original, copyContext );
 		this.persister = original.persister;
+		this.lockMode = original.lockMode;
+		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
+
 	@Override
 	public LockMode getLockMode() {
-		return super.getLockMode();
+		return lockMode;
 	}
 
 	@Override
 	public EntityReference getEntityReference() {
 		return this;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	@Override
 	public IdentifierDescription getIdentifierDescription() {
 		return identifierDescription;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return getEntityPersister();
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
-	public CollectionFetch buildCollectionFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
-	}
-
-	@Override
-	public EntityFetch buildEntityFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardEntityFetch(
-				this,
-				attributeDefinition,
-				fetchStrategy,
-				loadPlanBuildingContext
-		);
-	}
-
-	@Override
-	public CompositeFetch buildCompositeFetch(
-			CompositionDefinition attributeDefinition,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
-	}
-
-	@Override
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		EntityKey entityKey = getEntityKeyFromContext( context );
 		if ( entityKey != null ) {
 			context.getIdentifierResolutionContext( this ).registerEntityKey( entityKey );
 			return;
 		}
 
 		identifierDescription.hydrate( resultSet, context );
 
 		for ( Fetch fetch : getFetches() ) {
 			fetch.hydrate( resultSet, context );
 		}
 	}
 
 	private EntityKey getEntityKeyFromContext(ResultSetProcessingContext context) {
 		if ( context.getDictatedRootEntityKey() != null ) {
 			return context.getDictatedRootEntityKey();
 		}
 		else if ( context.getQueryParameters().getOptionalId() != null ) {
 			return context.getSession().generateEntityKey( 
 					context.getQueryParameters().getOptionalId(),
 					getEntityPersister() 
 			);
 		}
 		return null;
 	}
 	
 	@Override
 	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		final IdentifierResolutionContext identifierResolutionContext = context.getIdentifierResolutionContext( this );
 		EntityKey entityKey = identifierResolutionContext.getEntityKey();
 		if ( entityKey != null ) {
 			return;
 		}
 
 		entityKey = identifierDescription.resolve( resultSet, context );
 		identifierResolutionContext.registerEntityKey( entityKey );
 
 		for ( Fetch fetch : getFetches() ) {
 			fetch.resolve( resultSet, context );
 		}
 	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		Object objectForThisEntityReturn = null;
 		for ( IdentifierResolutionContext identifierResolutionContext : context.getIdentifierResolutionContexts() ) {
 			final EntityReference entityReference = identifierResolutionContext.getEntityReference();
 			final EntityKey entityKey = identifierResolutionContext.getEntityKey();
 			if ( entityKey == null ) {
 				throw new AssertionFailure( "Could not locate resolved EntityKey");
 			}
 			final Object object =  context.resolveEntityKey( entityKey, entityReference );
 			if ( this == entityReference ) {
 				objectForThisEntityReturn = object;
 			}
 		}
 		return objectForThisEntityReturn;
 	}
 
 	@Override
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
 	@Override
 	public String toString() {
 		return "EntityReturn(" + persister.getEntityName() + ")";
 	}
 
 	@Override
 	public EntityReturn makeCopy(CopyContext copyContext) {
 		return new EntityReturn( this, copyContext );
 	}
+
+	@Override
+	protected FetchOwnerDelegate getFetchOwnerDelegate() {
+		return fetchOwnerDelegate;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
index ab344a17f2..9d34ff51ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
@@ -1,70 +1,74 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 
 /**
  * Contract for associations that are being fetched.
  * <p/>
  * NOTE : can represent components/embeddables
  *
  * @author Steve Ebersole
  */
 public interface Fetch extends CopyableFetch {
 	/**
 	 * Obtain the owner of this fetch.
 	 *
 	 * @return The fetch owner.
 	 */
 	public FetchOwner getOwner();
 
 	/**
 	 * Obtain the name of the property, relative to the owner, being fetched.
 	 *
 	 * @return The fetched property name.
 	 */
 	public String getOwnerPropertyName();
 
+	public boolean isNullable();
+
+	public String[] getColumnNames();
+
 	public FetchStrategy getFetchStrategy();
 
 	/**
 	 * Get the property path to this fetch
 	 *
 	 * @return The property path
 	 */
 	public PropertyPath getPropertyPath();
 
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
 
 	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
 
 	@Override
 	public Fetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
index 102180bdb1..9558e987d3 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
@@ -1,95 +1,102 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.Type;
 
 /**
  * Contract for owners of fetches.  Any non-scalar return could be a fetch owner.
  *
  * @author Steve Ebersole
  */
 public interface FetchOwner {
 	/**
 	 * Convenient constant for returning no fetches from {@link #getFetches()}
 	 */
 	public static final Fetch[] NO_FETCHES = new Fetch[0];
 
 	/**
 	 * Contract to add fetches to this owner.  Care should be taken in calling this method; it is intended
 	 * for Hibernate usage
 	 *
 	 * @param fetch The fetch to add
 	 */
 	public void addFetch(Fetch fetch);
 
 	/**
 	 * Retrieve the fetches owned by this return.
 	 *
 	 * @return The owned fetches.
 	 */
 	public Fetch[] getFetches();
 
+	public Type getType(Fetch fetch);
+
+	public boolean isNullable(Fetch fetch);
+
+	public String[] getColumnNames(Fetch fetch);
+
 	/**
 	 * Is the asserted plan valid from this owner to a fetch?
 	 *
 	 * @param fetchStrategy The pla to validate
 	 */
 	public void validateFetchPlan(FetchStrategy fetchStrategy);
 
 	/**
 	 * Retrieve the EntityPersister that is the base for any property references in the fetches it owns.
 	 *
 	 * @return The EntityPersister, for property name resolution.
 	 */
 	public EntityPersister retrieveFetchSourcePersister();
 
 	/**
 	 * Get the property path to this fetch owner
 	 *
 	 * @return The property path
 	 */
 	public PropertyPath getPropertyPath();
 
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext);
 
 	public EntityFetch buildEntityFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext);
 
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext);
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwnerDelegate.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwnerDelegate.java
new file mode 100644
index 0000000000..523891b3ef
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwnerDelegate.java
@@ -0,0 +1,38 @@
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
+package org.hibernate.loader.plan.spi;
+
+import org.hibernate.type.Type;
+
+/**
+ * @author Gail Badner
+ */
+public interface FetchOwnerDelegate {
+
+	public boolean isNullable(Fetch fetch);
+
+	public Type getType(Fetch fetch);
+
+	public String[] getColumnNames(Fetch fetch);
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java
index 5b18a01029..c4f090f9e5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java
@@ -1,32 +1,36 @@
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
 package org.hibernate.loader.plan.spi;
 
+import org.hibernate.persister.collection.CollectionPersister;
+
 /**
  * @author Steve Ebersole
  */
 public interface FetchableCollectionElement extends FetchOwner, CopyableReturn {
 	@Override
 	public FetchableCollectionElement makeCopy(CopyContext copyContext);
+
+	public CollectionReference getCollectionReference();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
index a15dd2a877..d7c12c762d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
@@ -1,759 +1,888 @@
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
 package org.hibernate.loader.plan.spi.build;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayDeque;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.MDC;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.AbstractSingularAttributeFetch;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.CompositeElementGraph;
 import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.spi.CompositeFetchOwnerDelegate;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
+import org.hibernate.loader.plan.spi.FetchOwnerDelegate;
 import org.hibernate.loader.plan.spi.IdentifierDescription;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.persister.walking.spi.CompositionElementDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 import static org.hibernate.loader.spi.ResultSetProcessingContext.IdentifierResolutionContext;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractLoadPlanBuilderStrategy implements LoadPlanBuilderStrategy, LoadPlanBuildingContext {
 	private static final Logger log = Logger.getLogger( AbstractLoadPlanBuilderStrategy.class );
 	private static final String MDC_KEY = "hibernateLoadPlanWalkPath";
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private ArrayDeque<FetchOwner> fetchOwnerStack = new ArrayDeque<FetchOwner>();
 	private ArrayDeque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
 
 	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	public SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected FetchOwner currentFetchOwner() {
 		return fetchOwnerStack.peekFirst();
 	}
 
 	@Override
 	public void start() {
 		if ( ! fetchOwnerStack.isEmpty() ) {
 			throw new WalkingException(
 					"Fetch owner stack was not empty on start; " +
 							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
 			);
 		}
 		if ( ! collectionReferenceStack.isEmpty() ) {
 			throw new WalkingException(
 					"Collection reference stack was not empty on start; " +
 							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
 			);
 		}
 		MDC.put( MDC_KEY, new MDCStack() );
 	}
 
 	@Override
 	public void finish() {
 		MDC.remove( MDC_KEY );
 		fetchOwnerStack.clear();
 		collectionReferenceStack.clear();
 	}
 
 	@Override
 	public void startingEntity(EntityDefinition entityDefinition) {
 		log.tracef(
 				"%s Starting entity : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 
 		if ( fetchOwnerStack.isEmpty() ) {
 			// this is a root...
 			if ( ! supportsRootEntityReturns() ) {
 				throw new HibernateException( "This strategy does not support root entity returns" );
 			}
 			final EntityReturn entityReturn = buildRootEntityReturn( entityDefinition );
 			addRootReturn( entityReturn );
 			pushToStack( entityReturn );
 		}
 		// otherwise this call should represent a fetch which should have been handled in #startingAttribute
 	}
 
 	protected boolean supportsRootEntityReturns() {
 		return false;
 	}
 
 	protected abstract void addRootReturn(Return rootReturn);
 
 	@Override
 	public void finishingEntity(EntityDefinition entityDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this entity
 		final FetchOwner poppedFetchOwner = popFromStack();
 
 		if ( ! EntityReference.class.isInstance( poppedFetchOwner ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		final EntityReference entityReference = (EntityReference) poppedFetchOwner;
 		// NOTE : this is not the most exhaustive of checks because of hierarchical associations (employee/manager)
 		if ( ! entityReference.getEntityPersister().equals( entityDefinition.getEntityPersister() ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		log.tracef(
 				"%s Finished entity : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 	}
 
 	@Override
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		log.tracef(
 				"%s Starting entity identifier : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 
 		final EntityReference entityReference = (EntityReference) currentFetchOwner();
 
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
 
 		final FetchOwner identifierAttributeCollector;
 		if ( entityIdentifierDefinition.isEncapsulated() ) {
-			identifierAttributeCollector = new EncapsulatedIdentifierAttributeCollector( entityReference );
+			if ( entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getIdentifierType().isComponentType() ) {
+				identifierAttributeCollector = new EncapsulatedCompositeIdentifierAttributeCollector( entityReference );
+			}
+			else {
+				identifierAttributeCollector = new EncapsulatedIdentifierAttributeCollector( entityReference );
+			}
 		}
 		else {
 			identifierAttributeCollector = new NonEncapsulatedIdentifierAttributeCollector( entityReference );
 		}
 		pushToStack( identifierAttributeCollector );
 	}
 
 	@Override
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		// perform some stack validation on exit, first on the current stack element we want to pop
 		{
 			final FetchOwner poppedFetchOwner = popFromStack();
 
 			if ( ! AbstractIdentifierAttributeCollector.class.isInstance( poppedFetchOwner ) ) {
 				throw new WalkingException( "Unexpected state in FetchOwner stack" );
 			}
 
 			final EntityReference entityReference = (EntityReference) poppedFetchOwner;
 			if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
 				throw new WalkingException(
 						String.format(
 								"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 								entityReference.getEntityPersister().getEntityName(),
 								entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 						)
 				);
 			}
 		}
 
 		// and then on the element before it
 		{
 			final FetchOwner currentFetchOwner = currentFetchOwner();
 			if ( ! EntityReference.class.isInstance( currentFetchOwner ) ) {
 				throw new WalkingException( "Unexpected state in FetchOwner stack" );
 			}
 			final EntityReference entityReference = (EntityReference) currentFetchOwner;
 			if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
 				throw new WalkingException(
 						String.format(
 								"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 								entityReference.getEntityPersister().getEntityName(),
 								entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 						)
 				);
 			}
 		}
 
 		log.tracef(
 				"%s Finished entity identifier : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 	}
 
 	@Override
 	public void startingCollection(CollectionDefinition collectionDefinition) {
 		log.tracef(
 				"%s Starting collection : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 
 		if ( fetchOwnerStack.isEmpty() ) {
 			// this is a root...
 			if ( ! supportsRootCollectionReturns() ) {
 				throw new HibernateException( "This strategy does not support root collection returns" );
 			}
 			final CollectionReturn collectionReturn = buildRootCollectionReturn( collectionDefinition );
 			addRootReturn( collectionReturn );
 			pushToCollectionStack( collectionReturn );
 		}
 	}
 
 	protected boolean supportsRootCollectionReturns() {
 		return false;
 	}
 
 	@Override
 	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		final Type indexType = collectionIndexDefinition.getType();
 		if ( indexType.isAssociationType() || indexType.isComponentType() ) {
 			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
 			final FetchOwner indexGraph = collectionReference.getIndexGraph();
 			if ( indexGraph == null ) {
 				throw new WalkingException( "Collection reference did not return index handler" );
 			}
 			pushToStack( indexGraph );
 		}
 	}
 
 	@Override
 	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		// nothing to do here
 		// 	- the element graph pushed while starting would be popped in finishing/Entity/finishingComposite
 	}
 
 	@Override
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 		if ( elementDefinition.getType().isAssociationType() || elementDefinition.getType().isComponentType() ) {
 			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
 			final FetchOwner elementGraph = collectionReference.getElementGraph();
 			if ( elementGraph == null ) {
 				throw new WalkingException( "Collection reference did not return element handler" );
 			}
 			pushToStack( elementGraph );
 		}
 	}
 
 	@Override
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 		// nothing to do here
 		// 	- the element graph pushed while starting would be popped in finishing/Entity/finishingComposite
 	}
 
 	@Override
+	public void startingCompositeElement(CompositionElementDefinition compositeElementDefinition) {
+		System.out.println(
+				String.format(
+						"%s Starting composite collection element for (%s)",
+						StringHelper.repeat( ">>", fetchOwnerStack.size() ),
+						compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
+				)
+		);
+	}
+
+	@Override
+	public void finishingCompositeElement(CompositionElementDefinition compositeElementDefinition) {
+		// pop the current fetch owner, and make sure what we just popped represents this composition
+		final FetchOwner poppedFetchOwner = popFromStack();
+
+		if ( ! CompositeElementGraph.class.isInstance( poppedFetchOwner ) ) {
+			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
+		}
+
+		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
+
+		log.tracef(
+				"%s Finished composite element for  : %s",
+				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
+				compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
+		);
+	}
+
+	@Override
 	public void finishingCollection(CollectionDefinition collectionDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this collection
 		final CollectionReference collectionReference = popFromCollectionStack();
 		if ( ! collectionReference.getCollectionPersister().equals( collectionDefinition.getCollectionPersister() ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		log.tracef(
 				"%s Finished collection : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void startingComposite(CompositionDefinition compositionDefinition) {
 		log.tracef(
 				"%s Starting composition : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				compositionDefinition.getName()
 		);
 
 		if ( fetchOwnerStack.isEmpty() ) {
 			throw new HibernateException( "A component cannot be the root of a walk nor a graph" );
 		}
 	}
 
 	@Override
 	public void finishingComposite(CompositionDefinition compositionDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this composition
 		final FetchOwner poppedFetchOwner = popFromStack();
 
 		if ( ! CompositeFetch.class.isInstance( poppedFetchOwner ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
 
 		log.tracef(
 				"%s Finished composition : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				compositionDefinition.getName()
 		);
 	}
 
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 		log.tracef(
 				"%s Starting attribute %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				attributeDefinition
 		);
 
 		final Type attributeType = attributeDefinition.getType();
 
 		final boolean isComponentType = attributeType.isComponentType();
 		final boolean isBasicType = ! ( isComponentType || attributeType.isAssociationType() );
 
 		if ( isBasicType ) {
 			return true;
 		}
 		else if ( isComponentType ) {
 			return handleCompositeAttribute( (CompositionDefinition) attributeDefinition );
 		}
 		else {
 			return handleAssociationAttribute( (AssociationAttributeDefinition) attributeDefinition );
 		}
 	}
 
 	@Override
 	public void finishingAttribute(AttributeDefinition attributeDefinition) {
 		log.tracef(
 				"%s Finishing up attribute : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				attributeDefinition
 		);
 	}
 
 	protected boolean handleCompositeAttribute(CompositionDefinition attributeDefinition) {
 		final FetchOwner fetchOwner = currentFetchOwner();
 		final CompositeFetch fetch = fetchOwner.buildCompositeFetch( attributeDefinition, this );
 		pushToStack( fetch );
 		return true;
 	}
 
 	protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
 		final FetchStrategy fetchStrategy = determineFetchPlan( attributeDefinition );
 		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
 			return false;
 		}
 
 		final FetchOwner fetchOwner = currentFetchOwner();
 		fetchOwner.validateFetchPlan( fetchStrategy );
 
 		final Fetch associationFetch;
 		if ( attributeDefinition.isCollection() ) {
 			associationFetch = fetchOwner.buildCollectionFetch( attributeDefinition, fetchStrategy, this );
 			pushToCollectionStack( (CollectionReference) associationFetch );
 		}
 		else {
 			associationFetch = fetchOwner.buildEntityFetch(
 					attributeDefinition,
 					fetchStrategy,
 					this
 			);
 		}
 
 		if ( FetchOwner.class.isInstance( associationFetch) ) {
 			pushToStack( (FetchOwner) associationFetch );
 		}
 
 		return true;
 	}
 
 	protected abstract FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition);
 
 	protected int currentDepth() {
 		return fetchOwnerStack.size();
 	}
 
 	protected boolean isTooManyCollections() {
 		return false;
 	}
 
 	private void pushToStack(FetchOwner fetchOwner) {
 		log.trace( "Pushing fetch owner to stack : " + fetchOwner );
 		mdcStack().push( fetchOwner.getPropertyPath() );
 		fetchOwnerStack.addFirst( fetchOwner );
 	}
 
 	private MDCStack mdcStack() {
 		return (MDCStack) MDC.get( MDC_KEY );
 	}
 
 	private FetchOwner popFromStack() {
 		final FetchOwner last = fetchOwnerStack.removeFirst();
 		log.trace( "Popped fetch owner from stack : " + last );
 		mdcStack().pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 		return last;
 	}
 
 	private void pushToCollectionStack(CollectionReference collectionReference) {
 		log.trace( "Pushing collection reference to stack : " + collectionReference );
 		mdcStack().push( collectionReference.getPropertyPath() );
 		collectionReferenceStack.addFirst( collectionReference );
 	}
 
 	private CollectionReference popFromCollectionStack() {
 		final CollectionReference last = collectionReferenceStack.removeFirst();
 		log.trace( "Popped collection reference from stack : " + last );
 		mdcStack().pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 		return last;
 	}
 
 	protected abstract EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition);
 
 	protected abstract CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition);
 
 
 
 	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory();
 	}
 
 	public static interface FetchStackAware {
 		public void poppedFromStack();
 	}
 
 	protected static abstract class AbstractIdentifierAttributeCollector
 			implements FetchOwner, EntityReference, FetchStackAware {
 
 		protected final EntityReference entityReference;
 
 		protected final List<AbstractSingularAttributeFetch> identifierFetches = new ArrayList<AbstractSingularAttributeFetch>();
 		protected final Map<AbstractSingularAttributeFetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap
 				= new HashMap<AbstractSingularAttributeFetch, HydratedCompoundValueHandler>();
 
 		public AbstractIdentifierAttributeCollector(EntityReference entityReference) {
 			this.entityReference = entityReference;
 		}
 
 		@Override
 		public LockMode getLockMode() {
 			return entityReference.getLockMode();
 		}
 
 		@Override
 		public EntityReference getEntityReference() {
 			return this;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return entityReference.getEntityPersister();
 		}
 
 		@Override
+		public boolean isNullable(Fetch fetch) {
+			return false;
+		}
+
+		@Override
 		public IdentifierDescription getIdentifierDescription() {
 			return entityReference.getIdentifierDescription();
 		}
 
 		@Override
 		public CollectionFetch buildCollectionFetch(
 				AssociationAttributeDefinition attributeDefinition,
 				FetchStrategy fetchStrategy,
 				LoadPlanBuildingContext loadPlanBuildingContext) {
 			throw new WalkingException( "Entity identifier cannot contain persistent collections" );
 		}
 
 		@Override
 		public EntityFetch buildEntityFetch(
 				AssociationAttributeDefinition attributeDefinition,
 				FetchStrategy fetchStrategy,
 				LoadPlanBuildingContext loadPlanBuildingContext) {
 			// we have a key-many-to-one
 			//
 			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back through our #addFetch
 			// 		impl.  We collect them there and later build the IdentifierDescription
 			final EntityFetch fetch = LoadPlanBuildingHelper.buildStandardEntityFetch(
 					this,
 					attributeDefinition,
 					fetchStrategy,
 					loadPlanBuildingContext
 			);
 			fetchToHydratedStateExtractorMap.put( fetch, attributeDefinition.getHydratedCompoundValueExtractor() );
 
 			return fetch;
 		}
 
 		@Override
 		public CompositeFetch buildCompositeFetch(
 				CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
 			// nested composition.  Unusual, but not disallowed.
 			//
 			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back through our #addFetch
 			// 		impl.  We collect them there and later build the IdentifierDescription
 			return LoadPlanBuildingHelper.buildStandardCompositeFetch(
 					this,
 					attributeDefinition,
 					loadPlanBuildingContext
 			);
 		}
 
 		@Override
 		public void poppedFromStack() {
 			final IdentifierDescription identifierDescription = buildIdentifierDescription();
 			entityReference.injectIdentifierDescription( identifierDescription );
 		}
 
 		protected abstract IdentifierDescription buildIdentifierDescription();
 
 		@Override
 		public void addFetch(Fetch fetch) {
 			identifierFetches.add( (AbstractSingularAttributeFetch) fetch );
 		}
 
 		@Override
 		public Fetch[] getFetches() {
 			return ( (FetchOwner) entityReference ).getFetches();
 		}
 
 		@Override
 		public void validateFetchPlan(FetchStrategy fetchStrategy) {
 			( (FetchOwner) entityReference ).validateFetchPlan( fetchStrategy );
 		}
 
 		@Override
 		public EntityPersister retrieveFetchSourcePersister() {
 			return ( (FetchOwner) entityReference ).retrieveFetchSourcePersister();
 		}
 
 		@Override
 		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 			throw new WalkingException(
 					"IdentifierDescription collector should not get injected with IdentifierDescription"
 			);
 		}
 	}
 
+	protected static abstract class AbstractCompositeIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
+
+		public AbstractCompositeIdentifierAttributeCollector(EntityReference entityReference) {
+			super( entityReference );
+		}
+	}
+
 	protected static class EncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
 		private final PropertyPath propertyPath;
 
 		public EncapsulatedIdentifierAttributeCollector(EntityReference entityReference) {
 			super( entityReference );
 			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath();
 		}
 
 		@Override
 		protected IdentifierDescription buildIdentifierDescription() {
 			return new IdentifierDescriptionImpl(
 					entityReference,
 					identifierFetches.toArray( new AbstractSingularAttributeFetch[ identifierFetches.size() ] ),
 					null
 			);
 		}
 
 		@Override
+		public Type getType(Fetch fetch) {
+			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		}
+
+		@Override
+		public String[] getColumnNames(Fetch fetch) {
+			return ( (Loadable) entityReference.getEntityPersister() ).getIdentifierColumnNames();
+		}
+
+		@Override
 		public PropertyPath getPropertyPath() {
 			return propertyPath;
 		}
 	}
 
-	protected static class NonEncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
+	protected static class EncapsulatedCompositeIdentifierAttributeCollector extends AbstractCompositeIdentifierAttributeCollector {
 		private final PropertyPath propertyPath;
+
+		public EncapsulatedCompositeIdentifierAttributeCollector(EntityReference entityReference) {
+			super( entityReference );
+			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath();
+		}
+
+		@Override
+		protected IdentifierDescription buildIdentifierDescription() {
+			return new IdentifierDescriptionImpl(
+					entityReference,
+					identifierFetches.toArray( new AbstractSingularAttributeFetch[ identifierFetches.size() ] ),
+					null
+			);
+		}
+
+		@Override
+		public PropertyPath getPropertyPath() {
+			return propertyPath;
+		}
+
+		@Override
+		public Type getType(Fetch fetch) {
+			if ( !fetch.getOwnerPropertyName().equals( entityReference.getEntityPersister().getIdentifierPropertyName() ) ) {
+				throw new IllegalArgumentException(
+						String.format(
+								"Fetch owner property name [%s] is not the same as the identifier property name [%s].",
+								fetch.getOwnerPropertyName(),
+								entityReference.getEntityPersister().getIdentifierPropertyName()
+						)
+				);
+			}
+			return entityReference.getEntityPersister().getIdentifierType();
+		}
+
+		@Override
+		public String[] getColumnNames(Fetch fetch) {
+			return ( (Loadable) entityReference.getEntityPersister() ).getIdentifierColumnNames();
+		}
+	}
+
+	protected static class NonEncapsulatedIdentifierAttributeCollector extends AbstractCompositeIdentifierAttributeCollector {
+		private final PropertyPath propertyPath;
+		private final FetchOwnerDelegate fetchOwnerDelegate;
+
 		public NonEncapsulatedIdentifierAttributeCollector(EntityReference entityReference) {
 			super( entityReference );
 			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath().append( "<id>" );
+			this.fetchOwnerDelegate = new CompositeFetchOwnerDelegate(
+					entityReference.getEntityPersister().getFactory(),
+					(CompositeType) entityReference.getEntityPersister().getIdentifierType(),
+					( (Loadable) entityReference.getEntityPersister() ).getIdentifierColumnNames()
+			);
 		}
 
 		@Override
 		protected IdentifierDescription buildIdentifierDescription() {
 			return new IdentifierDescriptionImpl(
 					entityReference,
 					identifierFetches.toArray( new AbstractSingularAttributeFetch[ identifierFetches.size() ] ),
 					fetchToHydratedStateExtractorMap
 			);
 		}
 
 		@Override
 		public PropertyPath getPropertyPath() {
 			return propertyPath;
 		}
+
+
+		public Type getType(Fetch fetch) {
+			if ( !fetch.getOwnerPropertyName().equals( entityReference.getEntityPersister().getIdentifierPropertyName() ) ) {
+				throw new IllegalArgumentException(
+						String.format(
+								"Fetch owner property name [%s] is not the same as the identifier property name [%s].",
+								fetch.getOwnerPropertyName(),
+								entityReference.getEntityPersister().getIdentifierPropertyName()
+						)
+				);
+			}
+			return fetchOwnerDelegate.getType( fetch );
+		}
+
+		public String[] getColumnNames(Fetch fetch) {
+			return fetchOwnerDelegate.getColumnNames( fetch );
+		}
 	}
 
 	private static class IdentifierDescriptionImpl implements IdentifierDescription {
 		private final EntityReference entityReference;
 		private final AbstractSingularAttributeFetch[] identifierFetches;
 		private final Map<AbstractSingularAttributeFetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap;
 
 		private IdentifierDescriptionImpl(
 				EntityReference entityReference,
 				AbstractSingularAttributeFetch[] identifierFetches,
 				Map<AbstractSingularAttributeFetch, HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap) {
 			this.entityReference = entityReference;
 			this.identifierFetches = identifierFetches;
 			this.fetchToHydratedStateExtractorMap = fetchToHydratedStateExtractorMap;
 		}
 
 		@Override
 		public Fetch[] getFetches() {
 			return identifierFetches;
 		}
 
 		@Override
 		public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 			final IdentifierResolutionContext ownerIdentifierResolutionContext =
 					context.getIdentifierResolutionContext( entityReference );
 			final Object ownerIdentifierHydratedState = ownerIdentifierResolutionContext.getHydratedForm();
 
 			if ( ownerIdentifierHydratedState != null ) {
 				for ( AbstractSingularAttributeFetch fetch : identifierFetches ) {
 					if ( fetch instanceof EntityFetch ) {
 						final IdentifierResolutionContext identifierResolutionContext =
 								context.getIdentifierResolutionContext( (EntityFetch) fetch );
 						// if the identifier was already hydrated, nothing to do
 						if ( identifierResolutionContext.getHydratedForm() != null ) {
 							continue;
 						}
 						// try to extract the sub-hydrated value from the owners tuple array
 						if ( fetchToHydratedStateExtractorMap != null && ownerIdentifierHydratedState != null ) {
 							Serializable extracted = (Serializable) fetchToHydratedStateExtractorMap.get( fetch )
 									.extract( ownerIdentifierHydratedState );
 							identifierResolutionContext.registerHydratedForm( extracted );
 							continue;
 						}
 
 						// if we can't, then read from result set
 						fetch.hydrate( resultSet, context );
 					}
 					else {
 						throw new NotYetImplementedException( "Cannot hydrate identifier Fetch that is not an EntityFetch" );
 					}
 				}
 				return;
 			}
 
 			final Object hydratedIdentifierState = entityReference.getEntityPersister().getIdentifierType().hydrate(
 					resultSet,
 					context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( entityReference ).getSuffixedKeyAliases(),
 					context.getSession(),
 					null
 			);
 			context.getIdentifierResolutionContext( entityReference ).registerHydratedForm( hydratedIdentifierState );
 		}
 
 		@Override
 		public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 			for ( AbstractSingularAttributeFetch fetch : identifierFetches ) {
 				resolveIdentifierFetch( resultSet, context, fetch );
 			}
 
 			final IdentifierResolutionContext ownerIdentifierResolutionContext =
 					context.getIdentifierResolutionContext( entityReference );
 			Object hydratedState = ownerIdentifierResolutionContext.getHydratedForm();
 			Serializable resolvedId = (Serializable) entityReference.getEntityPersister()
 					.getIdentifierType()
 					.resolve( hydratedState, context.getSession(), null );
 			return context.getSession().generateEntityKey( resolvedId, entityReference.getEntityPersister() );
 		}
 	}
 
 	private static void resolveIdentifierFetch(
 			ResultSet resultSet,
 			ResultSetProcessingContext context,
 			AbstractSingularAttributeFetch fetch) throws SQLException {
 		if ( fetch instanceof EntityFetch ) {
 			EntityFetch entityFetch = (EntityFetch) fetch;
 			final IdentifierResolutionContext identifierResolutionContext =
 					context.getIdentifierResolutionContext( entityFetch );
 			if ( identifierResolutionContext.getEntityKey() != null ) {
 				return;
 			}
 
 			EntityKey fetchKey = entityFetch.resolveInIdentifier( resultSet, context );
 			identifierResolutionContext.registerEntityKey( fetchKey );
 		}
 		else if ( fetch instanceof CompositeFetch ) {
 			for ( Fetch subFetch : fetch.getFetches() ) {
 				resolveIdentifierFetch( resultSet, context, (AbstractSingularAttributeFetch) subFetch );
 			}
 		}
 	}
 
 	public static class MDCStack {
 		private ArrayDeque<PropertyPath> pathStack = new ArrayDeque<PropertyPath>();
 
 		public void push(PropertyPath path) {
 			pathStack.addFirst( path );
 		}
 
 		public void pop() {
 			pathStack.removeFirst();
 		}
 
 		public String toString() {
 			final PropertyPath path = pathStack.peekFirst();
 			return path == null ? "<no-path>" : path.getFullPath();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 819ad1b0a4..aa1831b121 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2019 +1,2052 @@
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
 import org.hibernate.cfg.NotYetImplementedException;
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
 import org.hibernate.internal.FilterAliasGenerator;
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
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
+import org.hibernate.persister.walking.internal.CompositionSingularSubAttributesHelper;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.persister.walking.spi.CompositionElementDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.sql.ordering.antlr.ColumnReference;
 import org.hibernate.sql.ordering.antlr.FormulaReference;
 import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
 import org.hibernate.sql.ordering.antlr.OrderByTranslation;
 import org.hibernate.sql.ordering.antlr.SqlValueReference;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
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
 
 	protected final boolean hasWhere;
 	protected final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	private final boolean hasOrder;
 	private final OrderByTranslation orderByTranslation;
 
 	private final boolean hasManyToManyOrder;
 	private final OrderByTranslation manyToManyOrderByTranslation;
 
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
 	protected final boolean isInverse;
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
 	protected final SqlExceptionHelper sqlExceptionHelper;
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
 			cacheEntryStructure = collection.isMap()
 					? StructuredMapCacheEntry.INSTANCE
 					: StructuredCollectionCacheEntry.INSTANCE;
 		}
 		else {
 			cacheEntryStructure = UnstructuredCacheEntry.INSTANCE;
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
 			elementColumnAliases[j] = selectable.getAlias( dialect, table );
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
 			orderByTranslation = Template.translateOrderBy(
 					collection.getOrderBy(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			orderByTranslation = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilters(), factory);
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilters(), factory);
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			manyToManyOrderByTranslation = Template.translateOrderBy(
 					collection.getManyToManyOrdering(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTranslation = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	private class ColumnMapperImpl implements ColumnMapper {
 		@Override
 		public SqlValueReference[] map(String reference) {
 			final String[] columnNames;
 			final String[] formulaTemplates;
 
 			// handle the special "$element$" property name...
 			if ( "$element$".equals( reference ) ) {
 				columnNames = elementColumnNames;
 				formulaTemplates = elementFormulaTemplates;
 			}
 			else {
 				columnNames = elementPropertyMapping.toColumns( reference );
 				formulaTemplates = formulaTemplates( reference, columnNames.length );
 			}
 
 			final SqlValueReference[] result = new SqlValueReference[ columnNames.length ];
 			int i = 0;
 			for ( final String columnName : columnNames ) {
 				if ( columnName == null ) {
 					// if the column name is null, it indicates that this index in the property value mapping is
 					// actually represented by a formula.
 //					final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 					final String formulaTemplate = formulaTemplates[i];
 					result[i] = new FormulaReference() {
 						@Override
 						public String getFormulaFragment() {
 							return formulaTemplate;
 						}
 					};
 				}
 				else {
 					result[i] = new ColumnReference() {
 						@Override
 						public String getColumnName() {
 							return columnName;
 						}
 					};
 				}
 				i++;
 			}
 			return result;
 		}
 	}
 
 	private String[] formulaTemplates(String reference, int expectedSize) {
 		try {
 			final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 			return  ( (Queryable) elementPersister ).getSubclassPropertyFormulaTemplateClosure()[propertyIndex];
 		}
 		catch (Exception e) {
 			return new String[expectedSize];
 		}
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
 				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
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
             index = (Integer)index - baseIndex;
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
             index = (Integer)index + baseIndex;
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
 						expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 					}
 				}
 
 				LOG.debug( "Done deleting collection" );
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
 
 	protected BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
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
 									expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 									session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 								}
 							}
 
 						}
 						i++;
 					}
 
 					LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 				}
 				else {
 					LOG.debug( "Collection was empty" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
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
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
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
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 							}
 						}
 
 						LOG.debugf( "Done deleting collection rows: %s deleted", count );
 					}
 				}
 				else {
 					LOG.debug( "No rows to delete" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection rows: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
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
 					MessageHelper.collectionInfoString( this, collection, id, session ) );
 
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
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 								MessageHelper.collectionInfoString( this, collection, id, session ),
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
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
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
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next();
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public boolean isExtraLazy() {
 		return isExtraLazy;
 	}
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	/**
 	 * Intended for internal use only. In fact really only currently used from
 	 * test suite for assertion purposes.
 	 * 
 	 * @return The default collection initializer for this persister/collection.
 	 */
 	public CollectionInitializer getInitializer() {
 		return initializer;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
 		private final String rootAlias;
 
 		private StandardOrderByAliasResolver(String rootAlias) {
 			this.rootAlias = rootAlias;
 		}
 
 		@Override
 		public String resolveTableAlias(String columnReference) {
 			if ( elementPersister == null ) {
 				// we have collection of non-entity elements...
 				return rootAlias;
 			}
 			else {
 				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
 			}
 		}
 	}
 	
 	public abstract FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 
-
 	// ColectionDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return this;
 	}
 
 	@Override
 	public CollectionIndexDefinition getIndexDefinition() {
 		if ( ! hasIndex() ) {
 			return null;
 		}
 
 		return new CollectionIndexDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getIndexType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat composite collection index type as entity" );
 				}
 				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
 			}
 
 			@Override
 			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection index type as composite" );
 				}
 				// todo : implement
 				throw new NotYetImplementedException();
 			}
 		};
 	}
 
 	@Override
 	public CollectionElementDefinition getElementDefinition() {
 		return new CollectionElementDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getElementType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat composite collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
-			public CompositionDefinition toCompositeDefinition() {
+			public CompositionElementDefinition toCompositeElementDefinition() {
+				final String propertyName = role.substring( entityName.length() + 1 );
+				final int propertyIndex = ownerPersister.getEntityMetamodel().getPropertyIndex( propertyName );
+
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
 				}
-				// todo : implement
-				throw new NotYetImplementedException();
+
+				return new CompositionElementDefinition() {
+					@Override
+					public String getName() {
+						return "";
+					}
+
+					@Override
+					public Type getType() {
+						return getElementType();
+					}
+
+					@Override
+					public AttributeSource getSource() {
+						// TODO: what if this is a collection w/in an encapsulated composition attribute?
+						// should return the encapsulated composition attribute instead???
+						return getOwnerEntityPersister();
+					}
+
+					@Override
+					public Iterable<AttributeDefinition> getAttributes() {
+						return CompositionSingularSubAttributesHelper.getCompositionElementSubAttributes( this );
+					}
+
+					@Override
+					public CollectionDefinition getCollectionDefinition() {
+						return AbstractCollectionPersister.this;
+					}
+				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 51062e1524..9552291f79 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1135 +1,1128 @@
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
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
-import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext.NaturalIdHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metamodel.binding.AssociationAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.Value;
 import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
-import org.hibernate.persister.walking.spi.AttributeSource;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
-import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
-import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
-import org.hibernate.persister.walking.spi.NonEncapsulatedEntityIdentifierDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
-import org.hibernate.type.ComponentType;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractEntityPersister.class.getName());
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryHelper cacheEntryHelper;
 	private final EntityMetamodel entityMetamodel;
 	private final EntityTuplizer entityTuplizer;
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
 	
 	private final List<Integer> lobProperties = new ArrayList<Integer>();
 
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
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, this, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
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
 
 		final boolean lazyAvailable = isInstrumented();
 
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
 				lazyNumbers.add( i );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 			
 			if (prop.isLob() && getFactory().getDialect().forceLobAsLastValue() ) {
 				lobProperties.add( i );
 			}
 
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
 		filterHelper = new FilterHelper( persistentClass.getFilters(), factory );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 
 		this.cacheEntryHelper = buildCacheEntryHelper();
 	}
 
 	protected CacheEntryHelper buildCacheEntryHelper() {
 		if ( cacheAccessStrategy == null ) {
 			// the entity defined no caching...
 			return NoopCacheEntryHelper.INSTANCE;
 		}
 
 		if ( canUseReferenceCacheEntries() ) {
 			entityMetamodel.setLazy( false );
 			// todo : do we also need to unset proxy factory?
 			return new ReferenceCacheEntryHelper( this );
 		}
 
 		return factory.getSettings().isStructuredCacheEntriesEnabled()
 				? new StructuredCacheEntryHelper( this )
 				: new StandardCacheEntryHelper( this );
 	}
 
 	protected boolean canUseReferenceCacheEntries() {
 		// todo : should really validate that the cache access type is read-only
 
 		if ( ! factory.getSettings().isDirectReferenceCacheEntriesEnabled() ) {
 			return false;
 		}
 
 		// for now, limit this to just entities that:
 		// 		1) are immutable
 		if ( entityMetamodel.isMutable() ) {
 			return false;
 		}
 
 		//		2)  have no associations.  Eventually we want to be a little more lenient with associations.
 		for ( Type type : getSubclassPropertyTypeClosure() ) {
 			if ( type.isAssociationType() ) {
 				return false;
 			}
 		}
 
 		return true;
 	}
 
 
 	public AbstractEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		this.isLazyPropertiesCacheable =
 				entityBinding.getHierarchyDetails().getCaching() == null ?
 						false :
 						entityBinding.getHierarchyDetails().getCaching().isCacheLazyProperties();
 		this.entityMetamodel = new EntityMetamodel( entityBinding, this, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
 		int batch = entityBinding.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = entityBinding.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding().getSimpleValueSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = entityBinding.getRowId();
 
 		loaderName = entityBinding.getCustomLoaderName();
 
 		int i = 0;
 		for ( org.hibernate.metamodel.relational.Column col : entityBinding.getPrimaryTable().getPrimaryKey().getColumns() ) {
 			rootTableKeyColumnNames[i] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			if ( col.getReadFragment() == null ) {
 				rootTableKeyColumnReaders[i] = rootTableKeyColumnNames[i];
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromColumn( col, factory );
 			}
 			else {
 				rootTableKeyColumnReaders[i] = col.getReadFragment();
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromString( rootTableKeyColumnReaders[i], factory );
 			}
 			identifierAliases[i] = col.getAlias( factory.getDialect() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( entityBinding.isVersioned() ) {
 			final Value versioningValue = entityBinding.getHierarchyDetails().getVersioningAttributeBinding().getValue();
 			if ( ! org.hibernate.metamodel.relational.Column.class.isInstance( versioningValue ) ) {
 				throw new AssertionFailure( "Bad versioning attribute binding : " + versioningValue );
 			}
 			org.hibernate.metamodel.relational.Column versionColumn = org.hibernate.metamodel.relational.Column.class.cast( versioningValue );
 			versionColumnName = versionColumn.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( entityBinding.getWhereFilter() ) ? "( " + entityBinding.getWhereFilter() + ") " : null;
 		sqlWhereStringTemplate = getTemplateFromString( sqlWhereString, factory );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
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
 
 		i = 0;
 		boolean foundFormula = false;
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			thisClassProperties.add( singularAttributeBinding );
 
 			propertySubclassNames[i] = ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName();
 
 			int span = singularAttributeBinding.getSimpleValueSpan();
 			propertyColumnSpans[i] = span;
 
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			boolean[] propertyColumnInsertability = new boolean[span];
 			boolean[] propertyColumnUpdatability = new boolean[span];
 
 			int k = 0;
 
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				colAliases[k] = valueBinding.getSimpleValue().getAlias( factory.getDialect() );
 				if ( valueBinding.isDerived() ) {
 					foundFormula = true;
 					formulaTemplates[ k ] = getTemplateFromString( ( (DerivedValue) valueBinding.getSimpleValue() ).getExpression(), factory );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = ( org.hibernate.metamodel.relational.Column ) valueBinding.getSimpleValue();
 					colNames[k] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colReaderTemplates[k] = getTemplateFromColumn( col, factory );
 					colWriters[k] = col.getWriteFragment() == null ? "?" : col.getWriteFragment();
 				}
 				propertyColumnInsertability[k] = valueBinding.isIncludeInInsert();
 				propertyColumnUpdatability[k] = valueBinding.isIncludeInUpdate();
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			propertyColumnUpdateable[i] = propertyColumnUpdatability;
 			propertyColumnInsertable[i] = propertyColumnInsertability;
 
 			if ( lazyAvailable && singularAttributeBinding.isLazy() ) {
 				lazyProperties.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNames.add( singularAttributeBinding.getAttribute().getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping());
 				lazyColAliases.add( colAliases );
 			}
 
 
 			// TODO: fix this when backrefs are working
 			//propertySelectable[i] = singularAttributeBinding.isBackRef();
 			propertySelectable[i] = true;
 
 			propertyUniqueness[i] = singularAttributeBinding.isAlternateUniqueKey();
 			
 			// TODO: Does this need AttributeBindings wired into lobProperties?  Currently in Property only.
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		List<String> columns = new ArrayList<String>();
 		List<Boolean> columnsLazy = new ArrayList<Boolean>();
 		List<String> columnReaderTemplates = new ArrayList<String>();
 		List<String> aliases = new ArrayList<String>();
 		List<String> formulas = new ArrayList<String>();
 		List<String> formulaAliases = new ArrayList<String>();
 		List<String> formulaTemplates = new ArrayList<String>();
 		List<Boolean> formulasLazy = new ArrayList<Boolean>();
 		List<Type> types = new ArrayList<Type>();
 		List<String> names = new ArrayList<String>();
 		List<String> classes = new ArrayList<String>();
 		List<String[]> templates = new ArrayList<String[]>();
 		List<String[]> propColumns = new ArrayList<String[]>();
 		List<String[]> propColumnReaders = new ArrayList<String[]>();
 		List<String[]> propColumnReaderTemplates = new ArrayList<String[]>();
 		List<FetchMode> joinedFetchesList = new ArrayList<FetchMode>();
 		List<CascadeStyle> cascades = new ArrayList<CascadeStyle>();
 		List<Boolean> definedBySubclass = new ArrayList<Boolean>();
 		List<int[]> propColumnNumbers = new ArrayList<int[]>();
 		List<int[]> propFormulaNumbers = new ArrayList<int[]>();
 		List<Boolean> columnSelectables = new ArrayList<Boolean>();
 		List<Boolean> propNullables = new ArrayList<Boolean>();
 
 		for ( AttributeBinding attributeBinding : entityBinding.getSubEntityAttributeBindingClosure() ) {
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				// collections handled separately
 				continue;
 			}
 
 			final SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			names.add( singularAttributeBinding.getAttribute().getName() );
 			classes.add( ( (EntityBinding) singularAttributeBinding.getContainer() ).getEntity().getName() );
 			boolean isDefinedBySubclass = ! thisClassProperties.contains( singularAttributeBinding );
 			definedBySubclass.add( isDefinedBySubclass );
 			propNullables.add( singularAttributeBinding.isNullable() || isDefinedBySubclass ); //TODO: is this completely correct?
 			types.add( singularAttributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping() );
 
 			final int span = singularAttributeBinding.getSimpleValueSpan();
 			String[] cols = new String[ span ];
 			String[] readers = new String[ span ];
 			String[] readerTemplates = new String[ span ];
 			String[] forms = new String[ span ];
 			int[] colnos = new int[ span ];
 			int[] formnos = new int[ span ];
 			int l = 0;
 			Boolean lazy = singularAttributeBinding.isLazy() && lazyAvailable;
 			for ( SimpleValueBinding valueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				if ( valueBinding.isDerived() ) {
 					DerivedValue derivedValue = DerivedValue.class.cast( valueBinding.getSimpleValue() );
 					String template = getTemplateFromString( derivedValue.getExpression(), factory );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( derivedValue.getExpression() );
 					formulaAliases.add( derivedValue.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = org.hibernate.metamodel.relational.Column.class.cast( valueBinding.getSimpleValue() );
 					String colName = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( col.getAlias( factory.getDialect() ) );
 					columnsLazy.add( lazy );
 					// TODO: properties only selectable if they are non-plural???
 					columnSelectables.add( singularAttributeBinding.getAttribute().isSingular() );
 
 					readers[l] =
 							col.getReadFragment() == null ?
 									col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 									col.getReadFragment();
 					String readerTemplate = getTemplateFromColumn( col, factory );
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
 
 			if ( singularAttributeBinding.isAssociation() ) {
 				AssociationAttributeBinding associationAttributeBinding =
 						( AssociationAttributeBinding ) singularAttributeBinding;
 				cascades.add( associationAttributeBinding.getCascadeStyle() );
 				joinedFetchesList.add( associationAttributeBinding.getFetchMode() );
 			}
 			else {
 				cascades.add( CascadeStyles.NONE );
 				joinedFetchesList.add( FetchMode.SELECT );
 			}
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
new file mode 100644
index 0000000000..8cac9860c0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
@@ -0,0 +1,212 @@
+package org.hibernate.persister.walking.internal;
+
+import java.util.Iterator;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.CascadeStyles;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.QueryableCollection;
+import org.hibernate.persister.entity.AbstractEntityPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.persister.spi.HydratedCompoundValueHandler;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AssociationKey;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeSource;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.persister.walking.spi.CompositionElementDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.AssociationType;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.Type;
+
+/**
+ * @author Gail Badner
+ */
+public class CompositionSingularSubAttributesHelper {
+
+	public static Iterable<AttributeDefinition> getIdentifierSubAttributes(
+			final AbstractEntityPersister entityPersister) {
+		return getSingularSubAttributes(
+				entityPersister,
+				entityPersister,
+				(CompositeType) entityPersister.getIdentifierType(),
+				entityPersister.getTableName(),
+				entityPersister.getRootTableIdentifierColumnNames()
+		);
+	}
+
+	public static Iterable<AttributeDefinition> getCompositionElementSubAttributes(
+			CompositionElementDefinition compositionElementDefinition) {
+		final QueryableCollection collectionPersister =
+				(QueryableCollection) compositionElementDefinition.getCollectionDefinition().getCollectionPersister();
+		return getSingularSubAttributes(
+				compositionElementDefinition.getSource(),
+				(OuterJoinLoadable) collectionPersister.getOwnerEntityPersister(),
+				(CompositeType) collectionPersister.getElementType(),
+				collectionPersister.getTableName(),
+				collectionPersister.getElementColumnNames()
+		);
+	}
+
+	private static Iterable<AttributeDefinition> getSingularSubAttributes(
+			final AttributeSource source,
+			final OuterJoinLoadable ownerEntityPersister,
+			final CompositeType compositeType,
+			final String lhsTableName,
+			final String[] lhsColumns) {
+		return new Iterable<AttributeDefinition>() {
+			@Override
+			public Iterator<AttributeDefinition> iterator() {
+				return new Iterator<AttributeDefinition>() {
+					private final int numberOfAttributes = compositeType.getSubtypes().length;
+					private int currentSubAttributeNumber = 0;
+					private int currentColumnPosition = 0;
+
+					@Override
+					public boolean hasNext() {
+						return currentSubAttributeNumber < numberOfAttributes;
+					}
+
+					@Override
+					public AttributeDefinition next() {
+						final int subAttributeNumber = currentSubAttributeNumber;
+						currentSubAttributeNumber++;
+
+						final String name = compositeType.getPropertyNames()[subAttributeNumber];
+						final Type type = compositeType.getSubtypes()[subAttributeNumber];
+
+						final int columnPosition = currentColumnPosition;
+						final int columnSpan = type.getColumnSpan( ownerEntityPersister.getFactory() );
+						final String[] subAttributeLhsColumns = ArrayHelper.slice( lhsColumns, columnPosition, columnSpan );
+
+						currentColumnPosition += columnSpan;
+
+						if ( type.isAssociationType() ) {
+							final AssociationType aType = (AssociationType) type;
+							return new AssociationAttributeDefinition() {
+								@Override
+								public AssociationKey getAssociationKey() {
+									/* TODO: is this always correct? */
+									//return new AssociationKey(
+									//		joinable.getTableName(),
+									//		JoinHelper.getRHSColumnNames( aType, getEntityPersister().getFactory() )
+									//);
+									return new AssociationKey(
+											lhsTableName,
+											subAttributeLhsColumns
+									);
+								}
+
+								@Override
+								public boolean isCollection() {
+									return false;
+								}
+
+								@Override
+								public EntityDefinition toEntityDefinition() {
+									return (EntityPersister) aType.getAssociatedJoinable( ownerEntityPersister.getFactory() );
+								}
+
+								@Override
+								public CollectionDefinition toCollectionDefinition() {
+									throw new WalkingException( "A collection cannot be mapped to a composite ID sub-attribute." );
+								}
+
+								@Override
+								public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
+									return new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
+								}
+
+								@Override
+								public CascadeStyle determineCascadeStyle() {
+									return CascadeStyles.NONE;
+								}
+
+								@Override
+								public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
+									return null;
+								}
+
+								@Override
+								public String getName() {
+									return name;
+								}
+
+								@Override
+								public Type getType() {
+									return type;
+								}
+
+								@Override
+								public AttributeSource getSource() {
+									return source;
+								}
+							};
+						}
+						else if ( type.isComponentType() ) {
+							return new CompositionDefinition() {
+								@Override
+								public String getName() {
+									return name;
+								}
+
+								@Override
+								public Type getType() {
+									return type;
+								}
+
+								@Override
+								public AttributeSource getSource() {
+									return this;
+								}
+
+								@Override
+								public Iterable<AttributeDefinition> getAttributes() {
+									return CompositionSingularSubAttributesHelper.getSingularSubAttributes(
+											this,
+											ownerEntityPersister,
+											(CompositeType) type,
+											lhsTableName,
+											subAttributeLhsColumns
+									);
+								}
+							};
+						}
+						else {
+							return new AttributeDefinition() {
+								@Override
+								public String getName() {
+									return name;
+								}
+
+								@Override
+								public Type getType() {
+									return type;
+								}
+
+								@Override
+								public AttributeSource getSource() {
+									return source;
+								}
+							};
+						}
+					}
+
+					@Override
+					public void remove() {
+						throw new UnsupportedOperationException( "Remove operation not supported here" );
+					}
+				};
+			}
+		};
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java
index abb5e5b958..bec4320dc2 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java
@@ -1,313 +1,153 @@
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
 package org.hibernate.persister.walking.internal;
 
-import java.util.Iterator;
-
-import org.hibernate.engine.FetchStrategy;
-import org.hibernate.engine.FetchStyle;
-import org.hibernate.engine.FetchTiming;
-import org.hibernate.engine.spi.CascadeStyle;
-import org.hibernate.engine.spi.CascadeStyles;
-import org.hibernate.engine.spi.LoadQueryInfluencers;
-import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.entity.AbstractEntityPersister;
-import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.entity.Joinable;
-import org.hibernate.persister.spi.HydratedCompoundValueHandler;
-import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
-import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.NonEncapsulatedEntityIdentifierDefinition;
-import org.hibernate.persister.walking.spi.WalkingException;
-import org.hibernate.type.AssociationType;
-import org.hibernate.type.ComponentType;
 import org.hibernate.type.Type;
 
-import static org.hibernate.engine.internal.JoinHelper.getLHSColumnNames;
-import static org.hibernate.engine.internal.JoinHelper.getLHSTableName;
-
 /**
  * @author Gail Badner
  */
 public class EntityIdentifierDefinitionHelper {
 
 	public static EntityIdentifierDefinition buildSimpleEncapsulatedIdentifierDefinition(final AbstractEntityPersister entityPersister) {
 		return new EncapsulatedEntityIdentifierDefinition() {
 			@Override
 			public AttributeDefinition getAttributeDefinition() {
 				return new AttributeDefinitionAdapter( entityPersister);
 			}
 
 			@Override
 			public boolean isEncapsulated() {
 				return true;
 			}
 
 			@Override
 			public EntityDefinition getEntityDefinition() {
 				return entityPersister;
 			}
 		};
 	}
 
-	public static EntityIdentifierDefinition buildEncapsulatedCompositeIdentifierDefinition(final AbstractEntityPersister entityPersister) {
+	public static EntityIdentifierDefinition buildEncapsulatedCompositeIdentifierDefinition(
+			final AbstractEntityPersister entityPersister) {
 
 		return new EncapsulatedEntityIdentifierDefinition() {
 			@Override
 			public AttributeDefinition getAttributeDefinition() {
-				return new CompositeAttributeDefinitionAdapter( entityPersister );
+				return new CompositionDefinitionAdapter( entityPersister );
 			}
 
 			@Override
 			public boolean isEncapsulated() {
 				return true;
 			}
 
 			@Override
 			public EntityDefinition getEntityDefinition() {
 				return entityPersister;
 			}
 		};
 	}
 
 	public static EntityIdentifierDefinition buildNonEncapsulatedCompositeIdentifierDefinition(final AbstractEntityPersister entityPersister) {
 		return new NonEncapsulatedEntityIdentifierDefinition() {
 			@Override
 			public Iterable<AttributeDefinition> getAttributes() {
-				return new CompositeAttributeDefinitionAdapter( entityPersister ).getAttributes();
+				return CompositionSingularSubAttributesHelper.getIdentifierSubAttributes( entityPersister );
 			}
 
 			@Override
 			public Class getSeparateIdentifierMappingClass() {
 				return entityPersister.getEntityMetamodel().getIdentifierProperty().getType().getReturnedClass();
 			}
 
 			@Override
 			public boolean isEncapsulated() {
 				return false;
 			}
 
 			@Override
 			public EntityDefinition getEntityDefinition() {
 				return entityPersister;
 			}
 		};
 	}
 
 	private static class AttributeDefinitionAdapter implements AttributeDefinition {
 		private final AbstractEntityPersister entityPersister;
 
 		AttributeDefinitionAdapter(AbstractEntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		@Override
 		public String getName() {
 			return entityPersister.getEntityMetamodel().getIdentifierProperty().getName();
 		}
 
 		@Override
 		public Type getType() {
 			return entityPersister.getEntityMetamodel().getIdentifierProperty().getType();
 		}
 
 		@Override
 		public AttributeSource getSource() {
 			return entityPersister;
 		}
 
 		@Override
 		public String toString() {
 			return "<identifier-property:" + getName() + ">";
 		}
 
 		protected AbstractEntityPersister getEntityPersister() {
 			return entityPersister;
 		}
 	}
 
-	private static class CompositeAttributeDefinitionAdapter extends AttributeDefinitionAdapter implements CompositionDefinition {
+	private static class CompositionDefinitionAdapter extends AttributeDefinitionAdapter implements CompositionDefinition {
 
-		CompositeAttributeDefinitionAdapter(AbstractEntityPersister entityPersister) {
+		CompositionDefinitionAdapter(AbstractEntityPersister entityPersister) {
 			super( entityPersister );
 		}
 
 		@Override
-		public Iterable<AttributeDefinition> getAttributes() {
-			return new Iterable<AttributeDefinition>() {
-				@Override
-				public Iterator<AttributeDefinition> iterator() {
-					final ComponentType componentType = (ComponentType) getType();
-					return new Iterator<AttributeDefinition>() {
-						private final int numberOfAttributes = componentType.getSubtypes().length;
-						private int currentSubAttributeNumber = 0;
-						private int currentColumnPosition = 0;
-
-						@Override
-						public boolean hasNext() {
-							return currentSubAttributeNumber < numberOfAttributes;
-						}
-
-						@Override
-						public AttributeDefinition next() {
-							final int subAttributeNumber = currentSubAttributeNumber;
-							currentSubAttributeNumber++;
-
-							final AttributeSource source = getSource();
-							final String name = componentType.getPropertyNames()[subAttributeNumber];
-							final Type type = componentType.getSubtypes()[subAttributeNumber];
-
-							final int columnPosition = currentColumnPosition;
-							currentColumnPosition += type.getColumnSpan( getEntityPersister().getFactory() );
-
-							if ( type.isAssociationType() ) {
-								final AssociationType aType = (AssociationType) type;
-								final Joinable joinable = aType.getAssociatedJoinable( getEntityPersister().getFactory() );
-								return new AssociationAttributeDefinition() {
-									@Override
-									public AssociationKey getAssociationKey() {
-										/* TODO: is this always correct? */
-										//return new AssociationKey(
-										//		joinable.getTableName(),
-										//		JoinHelper.getRHSColumnNames( aType, getEntityPersister().getFactory() )
-										//);
-										return new AssociationKey(
-												getEntityPersister().getTableName(),
-												getLHSColumnNames(
-														aType,
-														-1,
-														columnPosition,
-														getEntityPersister(),
-														getEntityPersister().getFactory()
-												)
-										);
-
-									}
-
-									@Override
-									public boolean isCollection() {
-										return false;
-									}
-
-									@Override
-									public EntityDefinition toEntityDefinition() {
-										return (EntityPersister) joinable;
-									}
-
-									@Override
-									public CollectionDefinition toCollectionDefinition() {
-										throw new WalkingException( "A collection cannot be mapped to a composite ID sub-attribute." );
-									}
-
-									@Override
-									public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
-										return new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
-									}
-
-									@Override
-									public CascadeStyle determineCascadeStyle() {
-										return CascadeStyles.NONE;
-									}
-
-									@Override
-									public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
-										return null;
-									}
-
-									@Override
-									public String getName() {
-										return name;
-									}
-
-									@Override
-									public Type getType() {
-										return type;
-									}
-
-									@Override
-									public AttributeSource getSource() {
-										return source;
-									}
-								};
-							}
-							else if ( type.isComponentType() ) {
-								return new CompositionDefinition() {
-									@Override
-									public String getName() {
-										return name;
-									}
-
-									@Override
-									public Type getType() {
-										return type;
-									}
-
-									@Override
-									public AttributeSource getSource() {
-										return source;
-									}
-
-									@Override
-									public Iterable<AttributeDefinition> getAttributes() {
-										return null;  //To change body of implemented methods use File | Settings | File Templates.
-									}
-								};
-							}
-							else {
-								return new AttributeDefinition() {
-									@Override
-									public String getName() {
-										return name;
-									}
-
-									@Override
-									public Type getType() {
-										return type;
-									}
-
-									@Override
-									public AttributeSource getSource() {
-										return source;
-									}
-								};
-							}
-						}
+		public String toString() {
+			return "<identifier-property:" + getName() + ">";
+		}
 
-						@Override
-						public void remove() {
-							throw new UnsupportedOperationException( "Remove operation not supported here" );
-						}
-					};
-				}
-			};
+		@Override
+		public Iterable<AttributeDefinition> getAttributes() {
+			return  CompositionSingularSubAttributesHelper.getIdentifierSubAttributes( getEntityPersister() );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
index 285ec22270..6bf7d9a156 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
@@ -1,60 +1,63 @@
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
 
 /**
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
 
 	public void startingEntity(EntityDefinition entityDefinition);
 	public void finishingEntity(EntityDefinition entityDefinition);
 
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
 
 	public void startingCollection(CollectionDefinition collectionDefinition);
 	public void finishingCollection(CollectionDefinition collectionDefinition);
 
 	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
 	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
 
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition);
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition);
 
 	public void startingComposite(CompositionDefinition compositionDefinition);
 	public void finishingComposite(CompositionDefinition compositionDefinition);
 
+	public void startingCompositeElement(CompositionElementDefinition compositionElementDefinition);
+	public void finishingCompositeElement(CompositionElementDefinition compositionElementDefinition);
+
 	public boolean startingAttribute(AttributeDefinition attributeDefinition);
 	public void finishingAttribute(AttributeDefinition attributeDefinition);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
index b42f8b0ddc..ff138f76f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
@@ -1,39 +1,39 @@
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
 package org.hibernate.persister.walking.spi;
 
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public interface CollectionElementDefinition {
 	public CollectionDefinition getCollectionDefinition();
 
 	public Type getType();
 
 	public EntityDefinition toEntityDefinition();
 
-	public CompositionDefinition toCompositeDefinition();
+	public CompositionElementDefinition toCompositeElementDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionElementDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionElementDefinition.java
new file mode 100644
index 0000000000..cd073881c8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionElementDefinition.java
@@ -0,0 +1,31 @@
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
+package org.hibernate.persister.walking.spi;
+
+/**
+ * @author Gail Badner
+ */
+public interface CompositionElementDefinition extends CompositionDefinition{
+	public CollectionDefinition getCollectionDefinition();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
index 3b5caa44ae..ffdccb2b99 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
@@ -1,249 +1,247 @@
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
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
-import org.hibernate.engine.FetchStrategy;
-import org.hibernate.engine.FetchStyle;
-import org.hibernate.engine.FetchTiming;
-import org.hibernate.engine.spi.CascadeStyle;
-import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.entity.Joinable;
-import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.type.Type;
 
-import static org.hibernate.engine.internal.JoinHelper.getRHSColumnNames;
-
 /**
  * Provides model graph visitation based on the defined metadata (as opposed to based on the incoming graph
  * as we see in cascade processing).  In layman terms, we are walking the graph of the users model as defined by
  * mapped associations.
  * <p/>
  * Re-implementation of the legacy {@link org.hibernate.loader.JoinWalker} contract to leverage load plans.
  *
  * @author Steve Ebersole
  */
 public class MetadataDrivenModelGraphVisitor {
 	private static final Logger log = Logger.getLogger( MetadataDrivenModelGraphVisitor.class );
 
 	public static void visitEntity(AssociationVisitationStrategy strategy, EntityPersister persister) {
 		strategy.start();
 		try {
 			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
 					.visitEntityDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	public static void visitCollection(AssociationVisitationStrategy strategy, CollectionPersister persister) {
 		strategy.start();
 		try {
 			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
 					.visitCollectionDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	private final AssociationVisitationStrategy strategy;
 	private final SessionFactoryImplementor factory;
 
 	// todo : add a getDepth() method to PropertyPath
 	private PropertyPath currentPropertyPath = new PropertyPath();
 
 	public MetadataDrivenModelGraphVisitor(AssociationVisitationStrategy strategy, SessionFactoryImplementor factory) {
 		this.strategy = strategy;
 		this.factory = factory;
 	}
 
 	private void visitEntityDefinition(EntityDefinition entityDefinition) {
 		strategy.startingEntity( entityDefinition );
 
 		visitAttributes( entityDefinition );
 		visitIdentifierDefinition( entityDefinition.getEntityKeyDefinition() );
 
 		strategy.finishingEntity( entityDefinition );
 	}
 
 	private void visitIdentifierDefinition(EntityIdentifierDefinition entityIdentifierDefinition) {
 		strategy.startingEntityIdentifier( entityIdentifierDefinition );
 
 		if ( entityIdentifierDefinition.isEncapsulated() ) {
 			visitAttributeDefinition( ( (EncapsulatedEntityIdentifierDefinition) entityIdentifierDefinition).getAttributeDefinition() );
 		}
 		else {
 			for ( AttributeDefinition attributeDefinition : ( (NonEncapsulatedEntityIdentifierDefinition) entityIdentifierDefinition).getAttributes() ) {
 				visitAttributeDefinition( attributeDefinition );
 			}
 		}
 
 		strategy.finishingEntityIdentifier( entityIdentifierDefinition );
 	}
 
 	private void visitAttributes(AttributeSource attributeSource) {
 		for ( AttributeDefinition attributeDefinition : attributeSource.getAttributes() ) {
 			visitAttributeDefinition( attributeDefinition );
 		}
 	}
 
 	private void visitAttributeDefinition(AttributeDefinition attributeDefinition) {
 		final PropertyPath subPath = currentPropertyPath.append( attributeDefinition.getName() );
 		log.debug( "Visiting attribute path : " + subPath.getFullPath() );
 
 		final boolean continueWalk;
 		if ( attributeDefinition.getType().isAssociationType() &&
 				isDuplicateAssociationKey( ( (AssociationAttributeDefinition) attributeDefinition ).getAssociationKey() ) ) {
 			log.debug( "Property path deemed to be circular : " + subPath.getFullPath() );
 			continueWalk = false;
 		}
 		else {
 			continueWalk = strategy.startingAttribute( attributeDefinition );
 		}
 		if ( continueWalk ) {
 			final PropertyPath old = currentPropertyPath;
 			currentPropertyPath = subPath;
 			try {
 				if ( attributeDefinition.getType().isAssociationType() ) {
 					visitAssociation( (AssociationAttributeDefinition) attributeDefinition );
 				}
 				else if ( attributeDefinition.getType().isComponentType() ) {
 					visitCompositeDefinition( (CompositionDefinition) attributeDefinition );
 				}
 			}
 			finally {
 				currentPropertyPath = old;
 			}
 		}
 		strategy.finishingAttribute( attributeDefinition );
 	}
 
 	private void visitAssociation(AssociationAttributeDefinition attribute) {
 		// todo : do "too deep" checks; but see note about adding depth to PropertyPath
 
 		addAssociationKey( attribute.getAssociationKey() );
 
 		if ( attribute.isCollection() ) {
 			visitCollectionDefinition( attribute.toCollectionDefinition() );
 		}
 		else {
 			visitEntityDefinition( attribute.toEntityDefinition() );
 		}
 	}
 
 	private void visitCompositeDefinition(CompositionDefinition compositionDefinition) {
 		strategy.startingComposite( compositionDefinition );
 
 		visitAttributes( compositionDefinition );
 
 		strategy.finishingComposite( compositionDefinition );
 	}
 
 	private void visitCollectionDefinition(CollectionDefinition collectionDefinition) {
 		strategy.startingCollection( collectionDefinition );
 
 		visitCollectionIndex( collectionDefinition );
 		visitCollectionElements( collectionDefinition );
 
 		strategy.finishingCollection( collectionDefinition );
 	}
 
 	private void visitCollectionIndex(CollectionDefinition collectionDefinition) {
 		final CollectionIndexDefinition collectionIndexDefinition = collectionDefinition.getIndexDefinition();
 		if ( collectionIndexDefinition == null ) {
 			return;
 		}
 
 		strategy.startingCollectionIndex( collectionIndexDefinition );
 
 		log.debug( "Visiting index for collection :  " + currentPropertyPath.getFullPath() );
 		currentPropertyPath = currentPropertyPath.append( "<index>" );
 
 		try {
 			final Type collectionIndexType = collectionIndexDefinition.getType();
 			if ( collectionIndexType.isComponentType() ) {
 				visitCompositeDefinition( collectionIndexDefinition.toCompositeDefinition() );
 			}
 			else if ( collectionIndexType.isAssociationType() ) {
 				visitEntityDefinition( collectionIndexDefinition.toEntityDefinition() );
 			}
 		}
 		finally {
 			currentPropertyPath = currentPropertyPath.getParent();
 		}
 
 		strategy.finishingCollectionIndex( collectionIndexDefinition );
 	}
 
 	private void visitCollectionElements(CollectionDefinition collectionDefinition) {
 		final CollectionElementDefinition elementDefinition = collectionDefinition.getElementDefinition();
 		strategy.startingCollectionElements( elementDefinition );
 
 		if ( elementDefinition.getType().isComponentType() ) {
-			visitCompositeDefinition( elementDefinition.toCompositeDefinition() );
+			visitCompositeElementDefinition( elementDefinition.toCompositeElementDefinition() );
 		}
 		else if ( elementDefinition.getType().isEntityType() ) {
 			visitEntityDefinition( elementDefinition.toEntityDefinition() );
 		}
 
 		strategy.finishingCollectionElements( elementDefinition );
 	}
 
+	private void visitCompositeElementDefinition(CompositionElementDefinition compositionElementDefinition) {
+		strategy.startingCompositeElement( compositionElementDefinition );
+
+		visitAttributes( compositionElementDefinition );
+
+		strategy.finishingCompositeElement( compositionElementDefinition );
+	}
 
 	private final Set<AssociationKey> visitedAssociationKeys = new HashSet<AssociationKey>();
 
 	/**
 	 * Add association key to indicate the association is being visited.
 	 * @param associationKey - the association key.
 	 * @throws WalkingException if the association with the specified association key
 	 *                          has already been visited.
 	 */
 	protected void addAssociationKey(AssociationKey associationKey) {
 		if ( ! visitedAssociationKeys.add( associationKey ) ) {
 			throw new WalkingException(
 					String.format( "Association has already been visited: %s", associationKey )
 			);
 		}
 	}
 
 	/**
 	 * Has an association with the specified key been visited already?
 	 * @param associationKey - the association key.
 	 * @return true, if the association with the specified association key has already been visited;
 	 *         false, otherwise.
 	 */
 	protected boolean isDuplicateAssociationKey(AssociationKey associationKey) {
 		return visitedAssociationKeys.contains( associationKey );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
index a225b8a05f..9dbcbc7558 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
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
 package org.hibernate.tuple.component;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.tuple.AbstractNonIdentifierAttribute;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractCompositeBasedAttribute
 		extends AbstractNonIdentifierAttribute
 		implements NonIdentifierAttribute {
 
 	private final int ownerAttributeNumber;
 
 	public AbstractCompositeBasedAttribute(
-			AbstractCompositionDefinition source,
+			AbstractCompositionAttribute source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			Type attributeType,
 			BaselineAttributeInformation baselineInfo,
 			int ownerAttributeNumber) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 		this.ownerAttributeNumber = ownerAttributeNumber;
 	}
 
 	protected int ownerAttributeNumber() {
 		return ownerAttributeNumber;
 	}
 
 	@Override
-	public AbstractCompositionDefinition getSource() {
-		return (AbstractCompositionDefinition) super.getSource();
+	public AbstractCompositionAttribute getSource() {
+		return (AbstractCompositionAttribute) super.getSource();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionDefinition.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
similarity index 79%
rename from hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
index 554a630ed6..e563a9dd9e 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
@@ -1,208 +1,208 @@
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
 package org.hibernate.tuple.component;
 
 import java.util.Iterator;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.AbstractNonIdentifierAttribute;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 import static org.hibernate.engine.internal.JoinHelper.getLHSColumnNames;
 import static org.hibernate.engine.internal.JoinHelper.getLHSTableName;
 import static org.hibernate.engine.internal.JoinHelper.getRHSColumnNames;
 
 /**
  * @author Steve Ebersole
  */
-public abstract class AbstractCompositionDefinition extends AbstractNonIdentifierAttribute implements
+public abstract class AbstractCompositionAttribute extends AbstractNonIdentifierAttribute implements
 																						   CompositionDefinition {
-	protected AbstractCompositionDefinition(
+	protected AbstractCompositionAttribute(
 			AttributeSource source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 
 	@Override
 	public CompositeType getType() {
 		return (CompositeType) super.getType();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return new Iterable<AttributeDefinition>() {
 			@Override
 			public Iterator<AttributeDefinition> iterator() {
 				return new Iterator<AttributeDefinition>() {
 					private final int numberOfAttributes = getType().getSubtypes().length;
 					private int currentSubAttributeNumber = 0;
 					private int currentColumnPosition = 0;
 
 					@Override
 					public boolean hasNext() {
 						return currentSubAttributeNumber < numberOfAttributes;
 					}
 
 					@Override
 					public AttributeDefinition next() {
 						final int subAttributeNumber = currentSubAttributeNumber;
 						currentSubAttributeNumber++;
 
 						final String name = getType().getPropertyNames()[subAttributeNumber];
 						final Type type = getType().getSubtypes()[subAttributeNumber];
 
 						int columnPosition = currentColumnPosition;
 						currentColumnPosition += type.getColumnSpan( sessionFactory() );
 
 						if ( type.isAssociationType() ) {
 							// we build the association-key here because of the "goofiness" with 'currentColumnPosition'
 							final AssociationKey associationKey;
 							final AssociationType aType = (AssociationType) type;
 							final Joinable joinable = aType.getAssociatedJoinable( sessionFactory() );
 							if ( aType.getForeignKeyDirection() == ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT ) {
 								associationKey = new AssociationKey(
 										getLHSTableName(
 												aType,
 												attributeNumber(),
 												(OuterJoinLoadable) locateOwningPersister()
 										),
 										getLHSColumnNames(
 												aType,
 												attributeNumber(),
 												columnPosition,
 												(OuterJoinLoadable) locateOwningPersister(),
 												sessionFactory()
 										)
 								);
 							}
 							else {
 								associationKey = new AssociationKey(
 										joinable.getTableName(),
 										getRHSColumnNames( aType, sessionFactory() )
 								);
 							}
 
 							return new CompositeBasedAssociationAttribute(
-									AbstractCompositionDefinition.this,
+									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									(AssociationType) type,
 									new BaselineAttributeInformation.Builder()
-											.setInsertable( AbstractCompositionDefinition.this.isInsertable() )
-											.setUpdateable( AbstractCompositionDefinition.this.isUpdateable() )
-											.setInsertGenerated( AbstractCompositionDefinition.this.isInsertGenerated() )
-											.setUpdateGenerated( AbstractCompositionDefinition.this.isUpdateGenerated() )
+											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
+											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[subAttributeNumber] )
 											.setDirtyCheckable( true )
-											.setVersionable( AbstractCompositionDefinition.this.isVersionable() )
+											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation(),
-									AbstractCompositionDefinition.this.attributeNumber(),
+									AbstractCompositionAttribute.this.attributeNumber(),
 									associationKey
 							);
 						}
 						else if ( type.isComponentType() ) {
 							return new CompositionBasedCompositionAttribute(
-									AbstractCompositionDefinition.this,
+									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									(CompositeType) type,
 									new BaselineAttributeInformation.Builder()
-											.setInsertable( AbstractCompositionDefinition.this.isInsertable() )
-											.setUpdateable( AbstractCompositionDefinition.this.isUpdateable() )
-											.setInsertGenerated( AbstractCompositionDefinition.this.isInsertGenerated() )
-											.setUpdateGenerated( AbstractCompositionDefinition.this.isUpdateGenerated() )
+											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
+											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[subAttributeNumber] )
 											.setDirtyCheckable( true )
-											.setVersionable( AbstractCompositionDefinition.this.isVersionable() )
+											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation()
 							);
 						}
 						else {
 							return new CompositeBasedBasicAttribute(
-									AbstractCompositionDefinition.this,
+									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									type,
 									new BaselineAttributeInformation.Builder()
-											.setInsertable( AbstractCompositionDefinition.this.isInsertable() )
-											.setUpdateable( AbstractCompositionDefinition.this.isUpdateable() )
-											.setInsertGenerated( AbstractCompositionDefinition.this.isInsertGenerated() )
-											.setUpdateGenerated( AbstractCompositionDefinition.this.isUpdateGenerated() )
+											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
+											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[subAttributeNumber] )
 											.setDirtyCheckable( true )
-											.setVersionable( AbstractCompositionDefinition.this.isVersionable() )
+											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation()
 							);
 						}
 					}
 
 					@Override
 					public void remove() {
 						throw new UnsupportedOperationException( "Remove operation not supported here" );
 					}
 				};
 			}
 		};
 	}
 
 	public EntityPersister locateOwningPersister() {
 		if ( EntityDefinition.class.isInstance( getSource() ) ) {
 			return ( (EntityDefinition) getSource() ).getEntityPersister();
 		}
 		else {
-			return ( (AbstractCompositionDefinition) getSource() ).locateOwningPersister();
+			return ( (AbstractCompositionAttribute) getSource() ).locateOwningPersister();
 		}
 	}
 
 	@Override
 	protected String loggableMetadata() {
 		return super.loggableMetadata() + ",composition";
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
index 81c7f343f8..72eb5eac0b 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
@@ -1,183 +1,183 @@
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
 package org.hibernate.tuple.component;
 
 import org.hibernate.FetchMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.internal.FetchStrategyHelper;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeBasedAssociationAttribute
 		extends AbstractCompositeBasedAttribute
 		implements AssociationAttributeDefinition {
 
 	private final AssociationKey associationKey;
 	private Joinable joinable;
 
 	public CompositeBasedAssociationAttribute(
-			AbstractCompositionDefinition source,
+			AbstractCompositionAttribute source,
 			SessionFactoryImplementor factory,
 			int attributeNumber,
 			String attributeName,
 			AssociationType attributeType,
 			BaselineAttributeInformation baselineInfo,
 			int ownerAttributeNumber,
 			AssociationKey associationKey) {
 		super( source, factory, attributeNumber, attributeName, attributeType, baselineInfo, ownerAttributeNumber );
 		this.associationKey = associationKey;
 	}
 
 	@Override
 	public AssociationType getType() {
 		return (AssociationType) super.getType();
 	}
 
 	protected Joinable getJoinable() {
 		if ( joinable == null ) {
 			joinable = getType().getAssociatedJoinable( sessionFactory() );
 		}
 		return joinable;
 	}
 
 	@Override
 	public AssociationKey getAssociationKey() {
 		return associationKey;
 	}
 
 	@Override
 	public boolean isCollection() {
 		return getJoinable().isCollection();
 	}
 
 	@Override
 	public EntityDefinition toEntityDefinition() {
 		if ( isCollection() ) {
 			throw new IllegalStateException( "Cannot treat collection attribute as entity type" );
 		}
 		return (EntityPersister) getJoinable();
 	}
 
 	@Override
 	public CollectionDefinition toCollectionDefinition() {
 		if ( isCollection() ) {
 			throw new IllegalStateException( "Cannot treat entity attribute as collection type" );
 		}
 		return (CollectionPersister) getJoinable();
 	}
 
 	@Override
 	public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
 		final EntityPersister owningPersister = locateOwningPersister();
 
 		FetchStyle style = determineFetchStyleByProfile(
 				loadQueryInfluencers,
 				owningPersister,
 				propertyPath,
 				ownerAttributeNumber()
 		);
 		if ( style == null ) {
 			style = determineFetchStyleByMetadata(
 					getSource().getType().getFetchMode( attributeNumber() ),
 					getType()
 			);
 		}
 
 		return new FetchStrategy( determineFetchTiming( style ), style );
 	}
 
 	protected FetchStyle determineFetchStyleByProfile(
 			LoadQueryInfluencers loadQueryInfluencers,
 			EntityPersister owningPersister,
 			PropertyPath propertyPath,
 			int ownerAttributeNumber) {
 		return FetchStrategyHelper.determineFetchStyleByProfile(
 				loadQueryInfluencers,
 				owningPersister,
 				propertyPath,
 				ownerAttributeNumber
 		);
 	}
 
 	protected FetchStyle determineFetchStyleByMetadata(FetchMode fetchMode, AssociationType type) {
 		return FetchStrategyHelper.determineFetchStyleByMetadata( fetchMode, type, sessionFactory() );
 	}
 
 	private FetchTiming determineFetchTiming(FetchStyle style) {
 		return FetchStrategyHelper.determineFetchTiming( style, getType(), sessionFactory() );
 	}
 
 	private EntityPersister locateOwningPersister() {
 		return getSource().locateOwningPersister();
 	}
 
 	@Override
 	public CascadeStyle determineCascadeStyle() {
 		final CompositeType compositeType = (CompositeType) locateOwningPersister().getPropertyType( getName() );
 		return compositeType.getCascadeStyle( attributeNumber() );
 	}
 
 	private HydratedCompoundValueHandler hydratedCompoundValueHandler;
 
 	@Override
 	public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
 		if ( hydratedCompoundValueHandler == null ) {
 			hydratedCompoundValueHandler = new HydratedCompoundValueHandler() {
 				@Override
 				public Object extract(Object hydratedState) {
 					return ( (Object[] ) hydratedState )[ attributeNumber() ];
 				}
 
 				@Override
 				public void inject(Object hydratedState, Object value) {
 					( (Object[] ) hydratedState )[ attributeNumber() ] = value;
 				}
 			};
 		}
 		return hydratedCompoundValueHandler;
 	}
 
 	@Override
 	protected String loggableMetadata() {
 		return super.loggableMetadata() + ",association";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
index 7beee25984..4a154f130d 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
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
 package org.hibernate.tuple.component;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositionBasedCompositionAttribute
-		extends AbstractCompositionDefinition
+		extends AbstractCompositionAttribute
 		implements CompositionDefinition {
 	public CompositionBasedCompositionAttribute(
 			CompositionDefinition source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositionAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositionAttribute.java
index e283cae94b..66a7242a5f 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositionAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositionAttribute.java
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
 package org.hibernate.tuple.entity;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.tuple.component.AbstractCompositionDefinition;
+import org.hibernate.tuple.component.AbstractCompositionAttribute;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityBasedCompositionAttribute
-		extends AbstractCompositionDefinition
+		extends AbstractCompositionAttribute
 		implements CompositionDefinition {
 
 	public EntityBasedCompositionAttribute(
 			EntityPersister source,
 			SessionFactoryImplementor factory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, factory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java
index 3f76e0607b..ca0cd1aeab 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java
@@ -1,338 +1,352 @@
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
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Date;
 import java.util.List;
 import javax.persistence.Column;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.Entity;
 import javax.persistence.EnumType;
 import javax.persistence.Enumerated;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
+import javax.persistence.ManyToOne;
 
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.internal.EntityLoadQueryBuilderImpl;
 import org.hibernate.loader.internal.LoadQueryAliasResolutionContextImpl;
 import org.hibernate.loader.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.test.component.cascading.toone.PersonalInfo;
 import org.hibernate.testing.FailureExpected;
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
 
 		session = openSession();
 		session.beginTransaction();
 		Person personGotten = (Person) session.get( Person.class, person.id );
 		assertEquals( person.id, personGotten.id );
 		assertEquals( person.address.address1, personGotten.address.address1 );
 		assertEquals( person.address.city, personGotten.address.city );
 		assertEquals( person.address.country, personGotten.address.country );
 		assertEquals( person.address.type.typeName, personGotten.address.type.typeName );
 		session.getTransaction().commit();
 		session.close();
 
 		List results = getResults( sessionFactory().getEntityPersister( Person.class.getName() ) );
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Person personWork = ExtraAssertions.assertTyping( Person.class, result );
 		assertEquals( person.id, personWork.id );
 		assertEquals( person.address.address1, personWork.address.address1 );
 		assertEquals( person.address.city, personWork.address.city );
 		assertEquals( person.address.country, personWork.address.country );
 		assertEquals( person.address.type.typeName, personGotten.address.type.typeName );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete Person" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
-	/*
 	@Test
 	public void testNestedCompositeElementCollectionProcessing() throws Exception {
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
+		Person person = new Person();
+		person.id = 1;
+		person.name = "Joe Blow";
+		session.save( person );
 		Customer customer = new Customer();
 		customer.id = 1L;
 		Investment investment1 = new Investment();
 		investment1.description = "stock";
 		investment1.date = new Date();
 		investment1.monetaryAmount = new MonetaryAmount();
 		investment1.monetaryAmount.currency = MonetaryAmount.CurrencyCode.USD;
 		investment1.monetaryAmount.amount = BigDecimal.valueOf( 1234, 2 );
+		investment1.performedBy = person;
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
 
 		session = openSession();
 		session.beginTransaction();
 		Customer customerGotten = (Customer) session.get( Customer.class, customer.id );
 		assertEquals( customer.id, customerGotten.id );
 		session.getTransaction().commit();
 		session.close();
 
 		List results = getResults( sessionFactory().getEntityPersister( Customer.class.getName() ) );
 
 		assertEquals( 2, results.size() );
 		assertSame( results.get( 0 ), results.get( 1 ) );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Customer customerWork = ExtraAssertions.assertTyping( Customer.class, result );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
-		session.createQuery( "delete Customer" ).executeUpdate();
+		session.delete( customerWork.investments.get( 0 ).performedBy );
+		session.delete( customerWork );
 		session.getTransaction().commit();
 		session.close();
 	}
-	*/
 
 	private List<?> getResults(EntityPersister entityPersister ) {
 			final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE
 			);
 			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
 			final LoadQueryAliasResolutionContext aliasResolutionContext =
 					new LoadQueryAliasResolutionContextImpl(
 							sessionFactory(),
 							0,
 							Collections.singletonMap( plan.getReturns().get( 0 ), new String[] { "abc" } )
 					);
 			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
 					LoadQueryInfluencers.NONE,
 					plan
 			);
 			final String sql = queryBuilder.generateSql( 1, sessionFactory(), aliasResolutionContext );
 
 			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan );
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
 											NoOpLoadPlanAdvisor.INSTANCE,
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
 											aliasResolutionContext,
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
+		private Person performedBy;
 
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
+		@ManyToOne
+		public Person getPerformedBy() {
+			return performedBy;
+		}
+		public void setPerformedBy(Person performedBy) {
+			this.performedBy = performedBy;
+		}
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
diff --git a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
index da8bc7f961..411a42f714 100644
--- a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
@@ -1,210 +1,233 @@
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
 package org.hibernate.persister.walking;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.util.List;
 
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.persister.walking.spi.CompositionElementDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicWalkingTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class };
 	}
 
 	@Test
 	public void testIt() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		MetadataDrivenModelGraphVisitor.visitEntity(
 				new AssociationVisitationStrategy() {
 					private int depth = 0;
 
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
 										entityDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingEntity(EntityDefinition entityDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing entity (%s)",
 										StringHelper.repeat( "<<", depth-- ),
 										entityDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void startingCollection(CollectionDefinition collectionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting collection (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										collectionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingCollection(CollectionDefinition collectionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing collection (%s)",
 										StringHelper.repeat( ">>", depth-- ),
 										collectionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void startingComposite(CompositionDefinition compositionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting composite (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										compositionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingComposite(CompositionDefinition compositionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing composite (%s)",
 										StringHelper.repeat( ">>", depth-- ),
 										compositionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
+					public void startingCompositeElement(CompositionElementDefinition compositionElementDefinition) {
+						System.out.println(
+								String.format(
+										"%s Starting composite (%s)",
+										StringHelper.repeat( ">>", ++depth ),
+										compositionElementDefinition.toString()
+								)
+						);
+					}
+
+					@Override
+					public void finishingCompositeElement(CompositionElementDefinition compositionElementDefinition) {
+						System.out.println(
+								String.format(
+										"%s Finishing composite (%s)",
+										StringHelper.repeat( ">>", depth-- ),
+										compositionElementDefinition.toString()
+								)
+						);
+					}
+
+					@Override
 					public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 						System.out.println(
 								String.format(
 										"%s Handling attribute (%s)",
 										StringHelper.repeat( ">>", depth + 1 ),
 										attributeDefinition.toString()
 								)
 						);
 						return true;
 					}
 
 					@Override
 					public void finishingAttribute(AttributeDefinition attributeDefinition) {
 						// nothing to do
 					}
 				},
 				ep
 		);
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer id;
 		private String name;
 		@ManyToOne
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer id;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages;
 	}
 }
