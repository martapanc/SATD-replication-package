diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java
index 34d22b56ba..84830547d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityJoinableAssociationImpl.java
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
 			boolean hasRestriction,
 			Map<String, Filter> enabledFilters) throws MappingException {
 		super(
 				entityFetch,
 				entityFetch,
 				currentCollectionReference,
 				withClause,
 				hasRestriction,
 				enabledFilters
 		);
-		this.joinableType = entityFetch.getAssociationType();
+		this.joinableType = entityFetch.getEntityType();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
index ef71c92038..a22fb8171e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
@@ -1,82 +1,81 @@
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
 package org.hibernate.loader.plan.internal;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.plan.spi.AbstractFetchOwner;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public class LoadPlanBuildingHelper {
 	public static CollectionFetch buildStandardCollectionFetch(
 			FetchOwner fetchOwner,
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return new CollectionFetch(
 				loadPlanBuildingContext.getSessionFactory(),
 				LockMode.NONE, // todo : for now
 				fetchOwner,
 				fetchStrategy,
 				attributeDefinition.getName()
 		);
 	}
 
 	public static EntityFetch buildStandardEntityFetch(
 			FetchOwner fetchOwner,
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 
 		return new EntityFetch(
 				loadPlanBuildingContext.getSessionFactory(),
 				LockMode.NONE, // todo : for now
 				fetchOwner,
 				attributeDefinition.getName(),
-				(EntityType) attributeDefinition.getType(),
 				fetchStrategy
 		);
 	}
 
 	public static CompositeFetch buildStandardCompositeFetch(
 			FetchOwner fetchOwner,
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return new CompositeFetch(
 				loadPlanBuildingContext.getSessionFactory(),
 				fetchOwner,
 				attributeDefinition.getName()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
index 2e6293c1b5..83e01a9ebf 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
@@ -1,145 +1,157 @@
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
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.Type;
 
 /**
+ * This is a class for fetch owners, providing functionality related to the owned
+ * fetches.
+ *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public abstract class AbstractFetchOwner extends AbstractPlanNode implements FetchOwner {
 
+	// TODO: I removed lockMode from this method because I *think* it only relates to EntityFetch and EntityReturn.
+	//       lockMode should be moved back here if it applies to all fetch owners.
+
 	private List<Fetch> fetches;
 
 	public AbstractFetchOwner(SessionFactoryImplementor factory) {
 		super( factory );
 		validate();
 	}
 
 	private void validate() {
 	}
 
 	/**
 	 * A "copy" constructor.  Used while making clones/copies of this.
 	 *
 	 * @param original - the original object to copy.
+	 * @param copyContext - the copy context.
 	 */
 	protected AbstractFetchOwner(AbstractFetchOwner original, CopyContext copyContext) {
 		super( original );
 		validate();
 
+		// TODO: I don't think this is correct; shouldn't the fetches from original be copied into this???
 		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
 		if ( fetches == null || fetches.size() == 0 ) {
 			this.fetches = Collections.emptyList();
 		}
 		else {
-			// TODO: don't think this is correct...
 			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
 			for ( Fetch fetch : fetches ) {
 				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
 			}
 			this.fetches = fetchesCopy;
 		}
 		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
 	}
 
+	@Override
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
 
+	/**
+	 * Abstract method returning the delegate for obtaining details about an owned fetch.
+	 * @return the delegate
+	 */
 	protected abstract FetchOwnerDelegate getFetchOwnerDelegate();
 
 	@Override
 	public boolean isNullable(Fetch fetch) {
 		return getFetchOwnerDelegate().isNullable( fetch );
 	}
 
 	@Override
 	public Type getType(Fetch fetch) {
 		return getFetchOwnerDelegate().getType( fetch );
 	}
 
 	@Override
 	public String[] getColumnNames(Fetch fetch) {
 		return getFetchOwnerDelegate().getColumnNames( fetch );
 	}
 
 	@Override
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
 				loadPlanBuildingContext
 		);
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
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
index 0b3689844d..7382e08c29 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
@@ -1,111 +1,122 @@
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
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 
 /**
+ * Represents a singular attribute that is both a {@link FetchOwner} and a {@link Fetch}.
+ *
  * @author Steve Ebersole
+ * @author Gail Badner
  */
 public abstract class AbstractSingularAttributeFetch extends AbstractFetchOwner implements Fetch {
 	private final FetchOwner owner;
 	private final String ownerProperty;
 	private final FetchStrategy fetchStrategy;
 
 	private final PropertyPath propertyPath;
 
+	/**
+	 * Constructs an {@link AbstractSingularAttributeFetch} object.
+	 *
+	 * @param factory - the session factory.
+	 * @param owner - the fetch owner for this fetch.
+	 * @param ownerProperty - the owner's property referring to this fetch.
+	 * @param fetchStrategy - the fetch strategy for this fetch.
+	 */
 	public AbstractSingularAttributeFetch(
 			SessionFactoryImplementor factory,
 			FetchOwner owner,
 			String ownerProperty,
 			FetchStrategy fetchStrategy) {
 		super( factory );
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
 	public boolean isNullable() {
 		return owner.isNullable( this );
 	}
 
 	@Override
 	public String[] getColumnNames() {
 		return owner.getColumnNames( this );
 	}
 
 	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
index e2d23540df..29314ac755 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
@@ -1,83 +1,93 @@
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.type.CompositeType;
 
 /**
+ * Represents the {@link FetchOwner} for a composite collection element.
+ *
  * @author Steve Ebersole
+ * @author Gail Badner
  */
 public class CompositeElementGraph extends AbstractFetchOwner implements FetchableCollectionElement {
 	private final CollectionReference collectionReference;
 	private final PropertyPath propertyPath;
 	private final CollectionPersister collectionPersister;
 	private final FetchOwnerDelegate fetchOwnerDelegate;
 
+	/**
+	 * Constructs a {@link CompositeElementGraph}.
+	 *
+	 * @param sessionFactory - the session factory.
+	 * @param collectionReference - the collection reference.
+	 * @param collectionPath - the {@link PropertyPath} for the collection.
+	 */
 	public CompositeElementGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath collectionPath) {
 		super( sessionFactory );
 
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.propertyPath = collectionPath.append( "<elements>" );
 		this.fetchOwnerDelegate = new CompositeFetchOwnerDelegate(
 				sessionFactory,
 				(CompositeType) collectionPersister.getElementType(),
 				( (QueryableCollection) collectionPersister ).getElementColumnNames()
 		);
 	}
 
 	public CompositeElementGraph(CompositeElementGraph original, CopyContext copyContext) {
 		super( original, copyContext );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionPersister;
 		this.propertyPath = original.propertyPath;
 		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
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
 	public CompositeElementGraph makeCopy(CopyContext copyContext) {
 		return new CompositeElementGraph( this, copyContext );
 	}
 
 	@Override
 	protected FetchOwnerDelegate getFetchOwnerDelegate() {
 		return fetchOwnerDelegate;
 	}
 
 	@Override
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		throw new HibernateException( "Collection composite element cannot define collections" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
index c1c043308c..0a6c449bea 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
@@ -1,119 +1,99 @@
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
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.CompositeType;
 
 /**
+ * Represents a {@link Fetch} for a composite attribute as well as a
+ * {@link FetchOwner} for any sub-attributes fetches.
+ *
  * @author Steve Ebersole
+ * @author Gail Badner
  */
 public class CompositeFetch extends AbstractSingularAttributeFetch {
 	public static final FetchStrategy FETCH_PLAN = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 
 	private final FetchOwnerDelegate delegate;
 
+	/**
+	 * Constructs a {@link CompositeFetch} object.
+	 *
+	 * @param sessionFactory - the session factory.
+	 * @param owner - the fetch owner for this fetch.
+	 * @param ownerProperty - the owner's property referring to this fetch.
+	 */
 	public CompositeFetch(
 			SessionFactoryImplementor sessionFactory,
 			FetchOwner owner,
 			String ownerProperty) {
 		super( sessionFactory, owner, ownerProperty, FETCH_PLAN );
 		this.delegate = new CompositeFetchOwnerDelegate(
 				sessionFactory,
 				(CompositeType) getOwner().getType( this ),
 				getOwner().getColumnNames( this )
 		);
 	}
 
 	public CompositeFetch(CompositeFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
 		this.delegate = original.getFetchOwnerDelegate();
 	}
 
 	@Override
 	protected FetchOwnerDelegate getFetchOwnerDelegate() {
 		return delegate;
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return getOwner().retrieveFetchSourcePersister();
 	}
 
 	@Override
-	public CollectionFetch buildCollectionFetch(
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy,
-			LoadPlanBuildingContext loadPlanBuildingContext) {
-		return null;  //To change body of implemented methods use File | Settings | File Templates.
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
-			CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
-		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
-	}
-
-	@Override
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
index 8c530eb5d2..f081387fe8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetchOwnerDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetchOwnerDelegate.java
@@ -1,98 +1,107 @@
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
 
 import java.util.Arrays;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
+ * This interface provides a delegate for a composite fetch owner to
+ * obtain details about an owned sub-attribute fetch.
+ *
  * @author Gail Badner
  */
 public class CompositeFetchOwnerDelegate implements FetchOwnerDelegate {
 	private final SessionFactoryImplementor sessionFactory;
 	private final CompositeType compositeType;
 	private final String[] columnNames;
 
+	/**
+	 * Constructs a {@link CompositeFetchOwnerDelegate}.
+	 * @param sessionFactory - the session factory.
+	 * @param compositeType - the composite type.
+	 * @param columnNames - the column names used by sub-attribute fetches.
+	 */
 	public CompositeFetchOwnerDelegate(
 			SessionFactoryImplementor sessionFactory,
 			CompositeType compositeType,
 			String[] columnNames) {
 		this.sessionFactory = sessionFactory;
 		this.compositeType = compositeType;
 		this.columnNames = columnNames;
 	}
 
 	@Override
 	public boolean isNullable(Fetch fetch) {
 		return compositeType.getPropertyNullability()[ determinePropertyIndex( fetch ) ];
 	}
 
 	@Override
 	public Type getType(Fetch fetch) {
 		return compositeType.getSubtypes()[ determinePropertyIndex( fetch ) ];
 	}
 
 	@Override
 	public String[] getColumnNames(Fetch fetch) {
 		// TODO: probably want to cache this
 		int begin = 0;
 		String[] subColumnNames = null;
 		for ( int i = 0; i < compositeType.getSubtypes().length; i++ ) {
 			final int columnSpan = compositeType.getSubtypes()[i].getColumnSpan( sessionFactory );
 			subColumnNames = ArrayHelper.slice( columnNames, begin, columnSpan );
 			if ( compositeType.getPropertyNames()[ i ].equals( fetch.getOwnerPropertyName() ) ) {
 				break;
 			}
 			begin += columnSpan;
 		}
 		return subColumnNames;
 	}
 
 	private int determinePropertyIndex(Fetch fetch) {
 		// TODO: probably want to cache this
 		final String[] subAttributeNames = compositeType.getPropertyNames();
 		int subAttributeIndex = -1;
 		for ( int i = 0; i < subAttributeNames.length ; i++ ) {
 			if ( subAttributeNames[ i ].equals( fetch.getOwnerPropertyName() ) ) {
 				subAttributeIndex = i;
 				break;
 			}
 		}
 		if ( subAttributeIndex == -1 ) {
 			throw new WalkingException(
 					String.format(
 							"Owner property [%s] not found in composite properties [%s]",
 							fetch.getOwnerPropertyName(),
 							Arrays.asList( subAttributeNames )
 					)
 			);
 		}
 		return subAttributeIndex;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
index f4f4eb7fe4..7f8e29aaed 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
@@ -1,81 +1,93 @@
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.type.CompositeType;
 
 /**
+ *  Represents the {@link FetchOwner} for a composite collection index.
+ *
  * @author Steve Ebersole
+ * @author Gail Badner
  */
 public class CompositeIndexGraph extends AbstractFetchOwner implements FetchableCollectionIndex {
 	private final CollectionReference collectionReference;
 	private final PropertyPath propertyPath;
 	private final CollectionPersister collectionPersister;
 	private final FetchOwnerDelegate fetchOwnerDelegate;
 
+	/**
+	 * Constructs a {@link CompositeElementGraph}.
+	 *
+	 * @param sessionFactory - the session factory.
+	 * @param collectionReference - the collection reference.
+	 * @param collectionPath - the {@link PropertyPath} for the collection.
+	 */
 	public CompositeIndexGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
-			PropertyPath propertyPath) {
+			PropertyPath collectionPath) {
 		super( sessionFactory );
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
-		this.propertyPath = propertyPath.append( "<index>" );
+		this.propertyPath = collectionPath.append( "<index>" );
 		this.fetchOwnerDelegate = new CompositeFetchOwnerDelegate(
 				sessionFactory,
 				(CompositeType) collectionPersister.getIndexType(),
 				( (QueryableCollection) collectionPersister ).getIndexColumnNames()
 		);
 	}
 
 	protected CompositeIndexGraph(CompositeIndexGraph original, CopyContext copyContext) {
 		super( original, copyContext );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionPersister;
 		this.propertyPath = original.propertyPath;
 		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return collectionPersister.getOwnerEntityPersister();
 	}
 
+	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
 	public CompositeIndexGraph makeCopy(CopyContext copyContext) {
 		return new CompositeIndexGraph( this, copyContext );
 	}
 
 	@Override
 	protected FetchOwnerDelegate getFetchOwnerDelegate() {
 		return fetchOwnerDelegate;
 	}
 
+	@Override
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		throw new HibernateException( "Composite index cannot define collections" );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
index a3ccde17d7..b318efb667 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
@@ -1,107 +1,117 @@
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.AssociationType;
 
 /**
+ *  Represents the {@link FetchOwner} for a collection element that is
+ *  an entity association type.
+ *
  * @author Steve Ebersole
  */
 public class EntityElementGraph extends AbstractFetchOwner implements FetchableCollectionElement, EntityReference {
 	private final CollectionReference collectionReference;
 	private final CollectionPersister collectionPersister;
 	private final AssociationType elementType;
 	private final EntityPersister elementPersister;
 	private final PropertyPath propertyPath;
 	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	private IdentifierDescription identifierDescription;
 
+	/**
+	 * Constructs an {@link EntityElementGraph}.
+	 *
+	 * @param sessionFactory - the session factory.
+	 * @param collectionReference - the collection reference.
+	 * @param collectionPath - the {@link PropertyPath} for the collection.
+	 */
 	public EntityElementGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath collectionPath) {
 		super( sessionFactory );
 
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.elementType = (AssociationType) collectionPersister.getElementType();
 		this.elementPersister = (EntityPersister) this.elementType.getAssociatedJoinable( sessionFactory() );
 		this.propertyPath = collectionPath;
 		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( elementPersister );
 	}
 
 	public EntityElementGraph(EntityElementGraph original, CopyContext copyContext) {
 		super( original, copyContext );
 
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionReference.getCollectionPersister();
 		this.elementType = original.elementType;
 		this.elementPersister = original.elementPersister;
 		this.propertyPath = original.propertyPath;
 		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
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
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
 	@Override
 	public EntityElementGraph makeCopy(CopyContext copyContext) {
 		return new EntityElementGraph( this, copyContext );
 	}
 
 	@Override
 	public CollectionReference getCollectionReference() {
 		return collectionReference;
 	}
 
 	@Override
 	public String toString() {
 		return "EntityElementGraph(collection=" + collectionPersister.getRole() + ", type=" + elementPersister.getEntityName() + ")";
 	}
 
 	@Override
 	protected FetchOwnerDelegate getFetchOwnerDelegate() {
 		return fetchOwnerDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
index 9842166561..ed18f21465 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
@@ -1,249 +1,273 @@
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
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.EntityType;
 
 /**
+ * Represents a {@link Fetch} for an entity association attribute as well as a
+ * {@link FetchOwner} of the entity association sub-attribute fetches.
+
  * @author Steve Ebersole
  */
 public class EntityFetch extends AbstractSingularAttributeFetch implements EntityReference, Fetch {
 
-	private final EntityType associationType;
 	private final EntityPersister persister;
 	private final LockMode lockMode;
 	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	private IdentifierDescription identifierDescription;
 
+	/**
+	 * Constructs an {@link EntityFetch} object.
+	 *
+	 * @param sessionFactory - the session factory.
+	 * @param lockMode - the lock mode.
+	 * @param owner - the fetch owner for this fetch.
+	 * @param ownerProperty - the owner's property referring to this fetch.
+	 * @param fetchStrategy - the fetch strategy for this fetch.
+	 */
 	public EntityFetch(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			FetchOwner owner,
 			String ownerProperty,
-			EntityType entityType,
 			FetchStrategy fetchStrategy) {
 		super( sessionFactory, owner, ownerProperty, fetchStrategy );
 
-		this.associationType = entityType;
-		this.persister = sessionFactory.getEntityPersister( associationType.getAssociatedEntityName() );
+		this.persister = sessionFactory.getEntityPersister(
+				getEntityType().getAssociatedEntityName()
+		);
 		this.lockMode = lockMode;
 		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( persister );
 	}
 
 	/**
 	 * Copy constructor.
 	 *
 	 * @param original The original fetch
 	 * @param copyContext Access to contextual needs for the copy operation
 	 */
 	protected EntityFetch(EntityFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
-		this.associationType = original.associationType;
 		this.persister = original.persister;
 		this.lockMode = original.lockMode;
 		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
-	public EntityType getAssociationType() {
-		return associationType;
+	/**
+	 * Returns the entity type for this fetch.
+	 * @return the entity type for this fetch.
+	 */
+	public final EntityType getEntityType() {
+		return (EntityType) getOwner().getType( this );
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
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return persister;
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
-				if ( associationType.isOneToOne() ) {
+				if ( getEntityType().isOneToOne() ) {
 					// first, find our owner's entity-key...
 					final EntityKey ownersEntityKey = context.getIdentifierResolutionContext( (EntityReference) getOwner() ).getEntityKey();
 					if ( ownersEntityKey != null ) {
 						context.getSession().getPersistenceContext()
-								.addNullProperty( ownersEntityKey, associationType.getPropertyName() );
+								.addNullProperty( ownersEntityKey, getEntityType().getPropertyName() );
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
 
+	/**
+	 * Resolve any fetches required to resolve the identifier as well
+	 * as the entity key for this fetch..
+	 *
+	 * @param resultSet - the result set.
+	 * @param context - the result set processing context.
+	 * @return the entity key for this fetch.
+	 *
+	 * @throws SQLException
+	 */
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
-					associationType
+					getEntityType()
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
 
 	@Override
 	protected FetchOwnerDelegate getFetchOwnerDelegate() {
 		return fetchOwnerDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetchOwnerDelegate.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetchOwnerDelegate.java
index 7ef801df07..956471717e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetchOwnerDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetchOwnerDelegate.java
@@ -1,72 +1,80 @@
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
 
 import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 
 /**
+ * This interface provides a delegate for an entity fetch owner to
+ * obtain details about an owned attribute fetch.
+ *
  * @author Gail Badner
  */
 public class EntityFetchOwnerDelegate implements FetchOwnerDelegate {
 	private final EntityPersister entityPersister;
 
+	/**
+	 * Constructs an {@link EntityFetchOwnerDelegate}.
+	 *
+	 * @param entityPersister - the entity persister.
+	 */
 	public EntityFetchOwnerDelegate(EntityPersister entityPersister) {
 		this.entityPersister = entityPersister;
 	}
 
 	@Override
 	public boolean isNullable(Fetch fetch) {
 		return entityPersister.getPropertyNullability()[ determinePropertyIndex( fetch ) ];
 	}
 
 	@Override
 	public Type getType(Fetch fetch) {
 		return entityPersister.getPropertyTypes()[ determinePropertyIndex( fetch ) ];
 	}
 
 	@Override
 	public String[] getColumnNames(Fetch fetch) {
 		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) entityPersister;
 		Type fetchType = getType( fetch );
 		if ( fetchType.isAssociationType() ) {
 			return JoinHelper.getLHSColumnNames(
 					(AssociationType) fetchType,
 					determinePropertyIndex( fetch ),
 					outerJoinLoadable,
 					outerJoinLoadable.getFactory()
 			);
 		}
 		else {
 			return outerJoinLoadable.getPropertyColumnNames( determinePropertyIndex( fetch ) );
 		}
 	}
 
 	private int determinePropertyIndex(Fetch fetch) {
 		return entityPersister.getEntityMetamodel().getPropertyIndex( fetch.getOwnerPropertyName() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
index 384b8e98d4..df16559c81 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
@@ -1,118 +1,135 @@
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
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.AssociationType;
 
 /**
+ *  Represents the {@link FetchOwner} for a collection index that is an entity.
+ *
  * @author Steve Ebersole
  */
 public class EntityIndexGraph extends AbstractFetchOwner implements FetchableCollectionIndex, EntityReference {
 	private final CollectionReference collectionReference;
 	private final CollectionPersister collectionPersister;
 	private final AssociationType indexType;
 	private final EntityPersister indexPersister;
 	private final PropertyPath propertyPath;
 	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	private IdentifierDescription identifierDescription;
 
+	/**
+	 * Constructs an {@link EntityIndexGraph}.
+	 *
+	 * @param sessionFactory - the session factory.
+	 * @param collectionReference - the collection reference.
+	 * @param collectionPath - the {@link PropertyPath} for the collection.
+	 */
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
 		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( indexPersister );
 	}
 
 	public EntityIndexGraph(EntityIndexGraph original, CopyContext copyContext) {
 		super( original, copyContext );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionReference.getCollectionPersister();
 		this.indexType = original.indexType;
 		this.indexPersister = original.indexPersister;
 		this.propertyPath = original.propertyPath;
 		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
+	/**
+	 * TODO: Does lock mode apply to a collection index that is an entity?
+	 */
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
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
 	@Override
 	public EntityIndexGraph makeCopy(CopyContext copyContext) {
 		return new EntityIndexGraph( this, copyContext );
 	}
 
 	@Override
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
+	}
+
+	@Override
 	protected FetchOwnerDelegate getFetchOwnerDelegate() {
 		return fetchOwnerDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
index 7680e4a30f..db6321aa7d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
@@ -1,186 +1,198 @@
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
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 
 import static org.hibernate.loader.spi.ResultSetProcessingContext.IdentifierResolutionContext;
 
 /**
+ * Represents an entity return value in the query results.  Not the same
+ * as a result (column) in the JDBC ResultSet!
+ *
+ * @see Return
+ *
  * @author Steve Ebersole
  */
 public class EntityReturn extends AbstractFetchOwner implements Return, EntityReference, CopyableReturn {
 
 	private final EntityPersister persister;
 
-	private final PropertyPath propertyPath = new PropertyPath(); // its a root
+	private final PropertyPath propertyPath = new PropertyPath(); // it's a root
 
 	private final LockMode lockMode;
 
 	private final FetchOwnerDelegate fetchOwnerDelegate;
 
 	private IdentifierDescription identifierDescription;
 
+	/**
+	 * Construct an {@link EntityReturn}.
+	 *
+	 * @param sessionFactory - the session factory.
+	 * @param lockMode - the lock mode.
+	 * @param entityName - the entity name.
+	 */
 	public EntityReturn(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			String entityName) {
 		super( sessionFactory );
 		this.persister = sessionFactory.getEntityPersister( entityName );
 		this.lockMode = lockMode;
 		this.fetchOwnerDelegate = new EntityFetchOwnerDelegate( persister );
 	}
 
 	protected EntityReturn(EntityReturn original, CopyContext copyContext) {
 		super( original, copyContext );
 		this.persister = original.persister;
 		this.lockMode = original.lockMode;
 		this.fetchOwnerDelegate = original.fetchOwnerDelegate;
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return lockMode;
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
 
 	@Override
 	protected FetchOwnerDelegate getFetchOwnerDelegate() {
 		return fetchOwnerDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
index 9d34ff51ad..8eed47bf01 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
@@ -1,74 +1,89 @@
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
 
+	/**
+	 * Is this fetch nullable?
+	 *
+	 * @return true, if this fetch is nullable; false, otherwise.
+	 */
 	public boolean isNullable();
 
+	/**
+	 * Gets the column names used for this fetch.
+	 *
+	 * @return the column names used for this fetch.
+	 */
 	public String[] getColumnNames();
 
+	/**
+	 * Gets the fetch strategy for this fetch.
+	 *
+	 * @return the fetch strategy for this fetch.
+	 */
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
index 9558e987d3..81ac8685a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
@@ -1,102 +1,123 @@
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
 import org.hibernate.type.Type;
 
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
 
+	/**
+	 * Returns the type of the specified fetch.
+	 *
+	 * @param fetch - the owned fetch.
+	 *
+	 * @return the type of the specified fetch.
+	 */
 	public Type getType(Fetch fetch);
 
+	/**
+	 * Is the specified fetch nullable?
+	 *
+	 * @param fetch - the owned fetch.
+	 *
+	 * @return true, if the fetch is nullable; false, otherwise.
+	 */
 	public boolean isNullable(Fetch fetch);
 
+	/**
+	 * Returns the column names used for loading the specified fetch.
+	 *
+	 * @param fetch - the owned fetch.
+	 *
+	 * @return the column names used for loading the specified fetch.
+	 */
 	public String[] getColumnNames(Fetch fetch);
 
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
index 523891b3ef..31916efe06 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwnerDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwnerDelegate.java
@@ -1,38 +1,61 @@
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
 
 import org.hibernate.type.Type;
 
 /**
+ * This interface provides a delegate for a fetch owner to obtain details about an owned fetch.
+ *
  * @author Gail Badner
  */
 public interface FetchOwnerDelegate {
 
+	/**
+	 * Is the specified fetch nullable?
+	 *
+	 * @param fetch - the owned fetch.
+	 *
+	 * @return true, if the fetch is nullable; false, otherwise.
+	 */
 	public boolean isNullable(Fetch fetch);
 
+	/**
+	 * Returns the type of the specified fetch.
+	 *
+	 * @param fetch - the owned fetch.
+	 *
+	 * @return the type of the specified fetch.
+	 */
 	public Type getType(Fetch fetch);
 
+	/**
+	 * Returns the column names used for loading the specified fetch.
+	 *
+	 * @param fetch - the owned fetch.
+	 *
+	 * @return the column names used for loading the specified fetch.
+	 */
 	public String[] getColumnNames(Fetch fetch);
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java
index c4f090f9e5..f714ad3240 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionElement.java
@@ -1,36 +1,39 @@
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
 
-import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * @author Steve Ebersole
  */
 public interface FetchableCollectionElement extends FetchOwner, CopyableReturn {
 	@Override
 	public FetchableCollectionElement makeCopy(CopyContext copyContext);
 
+	/**
+	 * Returns the collection reference.
+	 * @return the collection reference.
+	 */
 	public CollectionReference getCollectionReference();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionIndex.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionIndex.java
index 7fe443bb2f..d051fb3099 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionIndex.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchableCollectionIndex.java
@@ -1,34 +1,40 @@
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
 
 /**
  * A collection index which can be a fetch owner.
  *
  * @author Steve Ebersole
  */
 public interface FetchableCollectionIndex extends FetchOwner, CopyableReturn {
 	@Override
 	public FetchableCollectionIndex makeCopy(CopyContext copyContext);
+
+	/**
+	 * Returns the collection reference.
+	 * @return the collection reference.
+	 */
+	public CollectionReference getCollectionReference();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
index d7c12c762d..039a0d515d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
@@ -1,888 +1,888 @@
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
 import org.hibernate.loader.plan.spi.CompositeElementGraph;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.CompositeFetchOwnerDelegate;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.FetchOwnerDelegate;
 import org.hibernate.loader.plan.spi.IdentifierDescription;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
+import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
-import org.hibernate.persister.walking.spi.CompositionElementDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.CompositeType;
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
 			if ( entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getIdentifierType().isComponentType() ) {
 				identifierAttributeCollector = new EncapsulatedCompositeIdentifierAttributeCollector( entityReference );
 			}
 			else {
 				identifierAttributeCollector = new EncapsulatedIdentifierAttributeCollector( entityReference );
 			}
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
-	public void startingCompositeElement(CompositionElementDefinition compositeElementDefinition) {
+	public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositeElementDefinition) {
 		System.out.println(
 				String.format(
 						"%s Starting composite collection element for (%s)",
 						StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 						compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 				)
 		);
 	}
 
 	@Override
-	public void finishingCompositeElement(CompositionElementDefinition compositeElementDefinition) {
+	public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositeElementDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this composition
 		final FetchOwner poppedFetchOwner = popFromStack();
 
 		if ( ! CompositeElementGraph.class.isInstance( poppedFetchOwner ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
 
 		log.tracef(
 				"%s Finished composite element for  : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
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
 		public boolean isNullable(Fetch fetch) {
 			return false;
 		}
 
 		@Override
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
 
 	protected static abstract class AbstractCompositeIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
 
 		public AbstractCompositeIdentifierAttributeCollector(EntityReference entityReference) {
 			super( entityReference );
 		}
 	}
 
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
 		public Type getType(Fetch fetch) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public String[] getColumnNames(Fetch fetch) {
 			return ( (Loadable) entityReference.getEntityPersister() ).getIdentifierColumnNames();
 		}
 
 		@Override
 		public PropertyPath getPropertyPath() {
 			return propertyPath;
 		}
 	}
 
 	protected static class EncapsulatedCompositeIdentifierAttributeCollector extends AbstractCompositeIdentifierAttributeCollector {
 		private final PropertyPath propertyPath;
 
 		public EncapsulatedCompositeIdentifierAttributeCollector(EntityReference entityReference) {
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
 		public PropertyPath getPropertyPath() {
 			return propertyPath;
 		}
 
 		@Override
 		public Type getType(Fetch fetch) {
 			if ( !fetch.getOwnerPropertyName().equals( entityReference.getEntityPersister().getIdentifierPropertyName() ) ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Fetch owner property name [%s] is not the same as the identifier property name [%s].",
 								fetch.getOwnerPropertyName(),
 								entityReference.getEntityPersister().getIdentifierPropertyName()
 						)
 				);
 			}
 			return entityReference.getEntityPersister().getIdentifierType();
 		}
 
 		@Override
 		public String[] getColumnNames(Fetch fetch) {
 			return ( (Loadable) entityReference.getEntityPersister() ).getIdentifierColumnNames();
 		}
 	}
 
 	protected static class NonEncapsulatedIdentifierAttributeCollector extends AbstractCompositeIdentifierAttributeCollector {
 		private final PropertyPath propertyPath;
 		private final FetchOwnerDelegate fetchOwnerDelegate;
 
 		public NonEncapsulatedIdentifierAttributeCollector(EntityReference entityReference) {
 			super( entityReference );
 			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath().append( "<id>" );
 			this.fetchOwnerDelegate = new CompositeFetchOwnerDelegate(
 					entityReference.getEntityPersister().getFactory(),
 					(CompositeType) entityReference.getEntityPersister().getIdentifierType(),
 					( (Loadable) entityReference.getEntityPersister() ).getIdentifierColumnNames()
 			);
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
 
 
 		public Type getType(Fetch fetch) {
 			if ( !fetch.getOwnerPropertyName().equals( entityReference.getEntityPersister().getIdentifierPropertyName() ) ) {
 				throw new IllegalArgumentException(
 						String.format(
 								"Fetch owner property name [%s] is not the same as the identifier property name [%s].",
 								fetch.getOwnerPropertyName(),
 								entityReference.getEntityPersister().getIdentifierPropertyName()
 						)
 				);
 			}
 			return fetchOwnerDelegate.getType( fetch );
 		}
 
 		public String[] getColumnNames(Fetch fetch) {
 			return fetchOwnerDelegate.getColumnNames( fetch );
 		}
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
index aa1831b121..160057e76c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2052 +1,2050 @@
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
 import org.hibernate.persister.walking.internal.CompositionSingularSubAttributesHelper;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
+import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
-import org.hibernate.persister.walking.spi.CompositionElementDefinition;
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
-			public CompositionElementDefinition toCompositeElementDefinition() {
-				final String propertyName = role.substring( entityName.length() + 1 );
-				final int propertyIndex = ownerPersister.getEntityMetamodel().getPropertyIndex( propertyName );
+			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
 				}
 
-				return new CompositionElementDefinition() {
+				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "";
 					}
 
 					@Override
 					public Type getType() {
 						return getElementType();
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
-						return CompositionSingularSubAttributesHelper.getCompositionElementSubAttributes( this );
+						return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes( this );
 					}
 
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
index 8cac9860c0..3a68c09b7b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
@@ -1,212 +1,252 @@
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
 package org.hibernate.persister.walking.internal;
 
 import java.util.Iterator;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.AbstractEntityPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
-import org.hibernate.persister.walking.spi.CompositionElementDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
+ * A helper for getting attributes from a composition that is known
+ * to have only singular attributes; for example, sub-attributes of a
+ * composite ID or a composite collection element.
+ *
+ * TODO: This should be refactored into a delegate and renamed.
+ *
  * @author Gail Badner
  */
 public class CompositionSingularSubAttributesHelper {
 
+	/**
+	 * Get composite ID sub-attribute definitions.
+	 *
+	 * @param entityPersister - the entity persister.
+	 * @return composite ID sub-attribute definitions.
+	 */
 	public static Iterable<AttributeDefinition> getIdentifierSubAttributes(
 			final AbstractEntityPersister entityPersister) {
 		return getSingularSubAttributes(
 				entityPersister,
 				entityPersister,
 				(CompositeType) entityPersister.getIdentifierType(),
 				entityPersister.getTableName(),
 				entityPersister.getRootTableIdentifierColumnNames()
 		);
 	}
 
-	public static Iterable<AttributeDefinition> getCompositionElementSubAttributes(
-			CompositionElementDefinition compositionElementDefinition) {
+	/**
+	 * Get sub-attribute definitions for a composite collection element.
+	 * @param compositionElementDefinition - composite collection element definition.
+	 * @return sub-attribute definitions for a composite collection element.
+	 */
+	public static Iterable<AttributeDefinition> getCompositeCollectionElementSubAttributes(
+			CompositeCollectionElementDefinition compositionElementDefinition) {
 		final QueryableCollection collectionPersister =
 				(QueryableCollection) compositionElementDefinition.getCollectionDefinition().getCollectionPersister();
 		return getSingularSubAttributes(
 				compositionElementDefinition.getSource(),
 				(OuterJoinLoadable) collectionPersister.getOwnerEntityPersister(),
 				(CompositeType) collectionPersister.getElementType(),
 				collectionPersister.getTableName(),
 				collectionPersister.getElementColumnNames()
 		);
 	}
 
 	private static Iterable<AttributeDefinition> getSingularSubAttributes(
 			final AttributeSource source,
 			final OuterJoinLoadable ownerEntityPersister,
 			final CompositeType compositeType,
 			final String lhsTableName,
 			final String[] lhsColumns) {
 		return new Iterable<AttributeDefinition>() {
 			@Override
 			public Iterator<AttributeDefinition> iterator() {
 				return new Iterator<AttributeDefinition>() {
 					private final int numberOfAttributes = compositeType.getSubtypes().length;
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
 
 						final String name = compositeType.getPropertyNames()[subAttributeNumber];
 						final Type type = compositeType.getSubtypes()[subAttributeNumber];
 
 						final int columnPosition = currentColumnPosition;
 						final int columnSpan = type.getColumnSpan( ownerEntityPersister.getFactory() );
 						final String[] subAttributeLhsColumns = ArrayHelper.slice( lhsColumns, columnPosition, columnSpan );
 
 						currentColumnPosition += columnSpan;
 
 						if ( type.isAssociationType() ) {
 							final AssociationType aType = (AssociationType) type;
 							return new AssociationAttributeDefinition() {
 								@Override
 								public AssociationKey getAssociationKey() {
 									/* TODO: is this always correct? */
 									//return new AssociationKey(
 									//		joinable.getTableName(),
 									//		JoinHelper.getRHSColumnNames( aType, getEntityPersister().getFactory() )
 									//);
 									return new AssociationKey(
 											lhsTableName,
 											subAttributeLhsColumns
 									);
 								}
 
 								@Override
 								public boolean isCollection() {
 									return false;
 								}
 
 								@Override
 								public EntityDefinition toEntityDefinition() {
 									return (EntityPersister) aType.getAssociatedJoinable( ownerEntityPersister.getFactory() );
 								}
 
 								@Override
 								public CollectionDefinition toCollectionDefinition() {
 									throw new WalkingException( "A collection cannot be mapped to a composite ID sub-attribute." );
 								}
 
 								@Override
 								public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
 									return new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 								}
 
 								@Override
 								public CascadeStyle determineCascadeStyle() {
 									return CascadeStyles.NONE;
 								}
 
 								@Override
 								public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
 									return null;
 								}
 
 								@Override
 								public String getName() {
 									return name;
 								}
 
 								@Override
 								public Type getType() {
 									return type;
 								}
 
 								@Override
 								public AttributeSource getSource() {
 									return source;
 								}
 							};
 						}
 						else if ( type.isComponentType() ) {
 							return new CompositionDefinition() {
 								@Override
 								public String getName() {
 									return name;
 								}
 
 								@Override
 								public Type getType() {
 									return type;
 								}
 
 								@Override
 								public AttributeSource getSource() {
 									return this;
 								}
 
 								@Override
 								public Iterable<AttributeDefinition> getAttributes() {
 									return CompositionSingularSubAttributesHelper.getSingularSubAttributes(
 											this,
 											ownerEntityPersister,
 											(CompositeType) type,
 											lhsTableName,
 											subAttributeLhsColumns
 									);
 								}
 							};
 						}
 						else {
 							return new AttributeDefinition() {
 								@Override
 								public String getName() {
 									return name;
 								}
 
 								@Override
 								public Type getType() {
 									return type;
 								}
 
 								@Override
 								public AttributeSource getSource() {
 									return source;
 								}
 							};
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
index 6bf7d9a156..d9d475a445 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
@@ -1,63 +1,63 @@
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
 
-	public void startingCompositeElement(CompositionElementDefinition compositionElementDefinition);
-	public void finishingCompositeElement(CompositionElementDefinition compositionElementDefinition);
+	public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition);
+	public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition);
 
 	public boolean startingAttribute(AttributeDefinition attributeDefinition);
 	public void finishingAttribute(AttributeDefinition attributeDefinition);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
index ff138f76f4..94e05cc242 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
@@ -1,39 +1,74 @@
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
+ * Represents a collection element.
+ *
  * @author Steve Ebersole
  */
 public interface CollectionElementDefinition {
+
+	/**
+	 * Returns the collection definition.
+	 * @return  the collection definition.
+	 */
 	public CollectionDefinition getCollectionDefinition();
 
+	/**
+	 * Returns the collection element type.
+	 * @return the collection element type
+	 */
 	public Type getType();
 
+	/**
+	 * If the element type returned by {@link #getType()} is an
+	 * {@link org.hibernate.type.EntityType}, then the entity
+	 * definition for the collection element is returned;
+	 * otherwise, IllegalStateException is thrown.
+	 *
+	 * @return the entity definition for the collection element.
+	 *
+	 * @throws IllegalStateException if the collection element type
+	 * returned by {@link #getType()} is not of type
+	 * {@link org.hibernate.type.EntityType}.
+	 */
 	public EntityDefinition toEntityDefinition();
 
-	public CompositionElementDefinition toCompositeElementDefinition();
+	/**
+	 * If the element type returned by {@link #getType()} is a
+	 * {@link org.hibernate.type.CompositeType}, then the composite
+	 * element definition for the collection element is returned;
+	 * otherwise, IllegalStateException is thrown.
+	 *
+	 * @return the composite element definition for the collection element.
+	 *
+	 * @throws IllegalStateException if the collection element type
+	 * returned by {@link #getType()} is not of type
+	 * {@link org.hibernate.type.CompositeType}.
+	 */
+	public CompositeCollectionElementDefinition toCompositeElementDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionElementDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeCollectionElementDefinition.java
similarity index 83%
rename from hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionElementDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeCollectionElementDefinition.java
index cd073881c8..f7354e4c3a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionElementDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeCollectionElementDefinition.java
@@ -1,31 +1,37 @@
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
+ * The definition for a composite collection element.
+ *
  * @author Gail Badner
  */
-public interface CompositionElementDefinition extends CompositionDefinition{
+public interface CompositeCollectionElementDefinition extends CompositionDefinition{
+	/**
+	 * Returns the collection definition.
+	 * @return the collection definition.
+	 */
 	public CollectionDefinition getCollectionDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
index ffdccb2b99..ee6e494d8a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
@@ -1,247 +1,247 @@
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
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
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
-			visitCompositeElementDefinition( elementDefinition.toCompositeElementDefinition() );
+			visitCompositeCollectionElementDefinition( elementDefinition.toCompositeElementDefinition() );
 		}
 		else if ( elementDefinition.getType().isEntityType() ) {
 			visitEntityDefinition( elementDefinition.toEntityDefinition() );
 		}
 
 		strategy.finishingCollectionElements( elementDefinition );
 	}
 
-	private void visitCompositeElementDefinition(CompositionElementDefinition compositionElementDefinition) {
-		strategy.startingCompositeElement( compositionElementDefinition );
+	private void visitCompositeCollectionElementDefinition(CompositeCollectionElementDefinition compositionElementDefinition) {
+		strategy.startingCompositeCollectionElement( compositionElementDefinition );
 
 		visitAttributes( compositionElementDefinition );
 
-		strategy.finishingCompositeElement( compositionElementDefinition );
+		strategy.finishingCompositeCollectionElement( compositionElementDefinition );
 	}
 
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
index 9dbcbc7558..f9371d0649 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
@@ -1,61 +1,63 @@
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
+ * A base class for a sub-attribute of a composite, non-identifier attribute.
+ *
  * @author Steve Ebersole
  */
 public abstract class AbstractCompositeBasedAttribute
 		extends AbstractNonIdentifierAttribute
 		implements NonIdentifierAttribute {
 
 	private final int ownerAttributeNumber;
 
 	public AbstractCompositeBasedAttribute(
 			AbstractCompositionAttribute source,
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
 	public AbstractCompositionAttribute getSource() {
 		return (AbstractCompositionAttribute) super.getSource();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
index e563a9dd9e..d2bf2c4526 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
@@ -1,208 +1,210 @@
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
+ * A base class for a composite, non-identifier attribute.
+ *
  * @author Steve Ebersole
  */
 public abstract class AbstractCompositionAttribute extends AbstractNonIdentifierAttribute implements
 																						   CompositionDefinition {
 	protected AbstractCompositionAttribute(
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
 									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									(AssociationType) type,
 									new BaselineAttributeInformation.Builder()
 											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
 											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
 											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
 											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[subAttributeNumber] )
 											.setDirtyCheckable( true )
 											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation(),
 									AbstractCompositionAttribute.this.attributeNumber(),
 									associationKey
 							);
 						}
 						else if ( type.isComponentType() ) {
 							return new CompositionBasedCompositionAttribute(
 									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									(CompositeType) type,
 									new BaselineAttributeInformation.Builder()
 											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
 											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
 											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
 											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[subAttributeNumber] )
 											.setDirtyCheckable( true )
 											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation()
 							);
 						}
 						else {
 							return new CompositeBasedBasicAttribute(
 									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									type,
 									new BaselineAttributeInformation.Builder()
 											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
 											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
 											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
 											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[subAttributeNumber] )
 											.setDirtyCheckable( true )
 											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
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
 			return ( (AbstractCompositionAttribute) getSource() ).locateOwningPersister();
 		}
 	}
 
 	@Override
 	protected String loggableMetadata() {
 		return super.loggableMetadata() + ",composition";
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
index 411a42f714..90788b3911 100644
--- a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
@@ -1,233 +1,233 @@
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
+import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
-import org.hibernate.persister.walking.spi.CompositionElementDefinition;
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
-					public void startingCompositeElement(CompositionElementDefinition compositionElementDefinition) {
+					public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting composite (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										compositionElementDefinition.toString()
 								)
 						);
 					}
 
 					@Override
-					public void finishingCompositeElement(CompositionElementDefinition compositionElementDefinition) {
+					public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing composite (%s)",
 										StringHelper.repeat( ">>", depth-- ),
 										compositionElementDefinition.toString()
 								)
 						);
 					}
 
 					@Override
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/graph/internal/advisor/AdviceHelper.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/graph/internal/advisor/AdviceHelper.java
index 3f7bcab15b..1e65c7a037 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/graph/internal/advisor/AdviceHelper.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/graph/internal/advisor/AdviceHelper.java
@@ -1,81 +1,76 @@
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
 package org.hibernate.jpa.graph.internal.advisor;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.jpa.graph.spi.AttributeNodeImplementor;
 import org.hibernate.jpa.internal.metamodel.Helper;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public class AdviceHelper {
 	private AdviceHelper() {
 	}
 
 	static Fetch buildFetch(FetchOwner fetchOwner, AttributeNodeImplementor attributeNode) {
 		if ( attributeNode.getAttribute().isAssociation() ) {
 			if ( attributeNode.getAttribute().isCollection() ) {
 				return new CollectionFetch(
 						(SessionFactoryImplementor) attributeNode.entityManagerFactory().getSessionFactory(),
 						LockMode.NONE,
 						fetchOwner,
 						new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.SELECT ),
 						attributeNode.getAttributeName()
 				);
 			}
 			else {
-				EntityType entityType = (EntityType) Helper.resolveType(
-						(SessionFactoryImplementor) attributeNode.entityManagerFactory().getSessionFactory(),
-						attributeNode.getAttribute()
-				);
 				return new EntityFetch(
 						(SessionFactoryImplementor) attributeNode.entityManagerFactory().getSessionFactory(),
 						LockMode.NONE,
 						fetchOwner,
 						attributeNode.getAttributeName(),
-						entityType,
 						new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.SELECT )
 				);
 			}
 		}
 		else {
 			return new CompositeFetch(
 					(SessionFactoryImplementor) attributeNode.entityManagerFactory().getSessionFactory(),
 					fetchOwner,
 					attributeNode.getAttributeName()
 			);
 		}
 	}
 }
