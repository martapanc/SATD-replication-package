diff --git a/hibernate-core/src/main/java/org/hibernate/engine/FetchStrategy.java b/hibernate-core/src/main/java/org/hibernate/engine/FetchStrategy.java
new file mode 100644
index 0000000000..8264eee79a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/FetchStrategy.java
@@ -0,0 +1,50 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.engine;
+
+/**
+ * Describes the strategy for fetching an association
+ * <p/>
+ * todo not really a fan of the name.  not sure of a better one though.
+ * I'd almost rather see this be called the style, but then what to call FetchStyle?  HowToFetch?
+ *
+ * @author Steve Ebersole
+ */
+public class FetchStrategy {
+	private final FetchTiming timing;
+	private final FetchStyle style;
+
+	public FetchStrategy(FetchTiming timing, FetchStyle style) {
+		this.timing = timing;
+		this.style = style;
+	}
+
+	public FetchTiming getTiming() {
+		return timing;
+	}
+
+	public FetchStyle getStyle() {
+		return style;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/CascadeLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/CascadeLoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..ce19b46079
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/CascadeLoadPlanBuilderStrategy.java
@@ -0,0 +1,60 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.loader.plan.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+
+/**
+ * A LoadPlan building strategy for cascade processing; meaning, it builds the LoadPlan for loading related to
+ * cascading a particular action across associations
+ *
+ * @author Steve Ebersole
+ */
+public class CascadeLoadPlanBuilderStrategy extends SingleRootReturnLoadPlanBuilderStrategy {
+	private static final FetchStrategy EAGER = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
+	private static final FetchStrategy DELAYED = new FetchStrategy( FetchTiming.DELAYED, FetchStyle.SELECT );
+
+	private final CascadingAction cascadeActionToMatch;
+
+	public CascadeLoadPlanBuilderStrategy(
+			CascadingAction cascadeActionToMatch,
+			SessionFactoryImplementor sessionFactory,
+			LoadQueryInfluencers loadQueryInfluencers,
+			String rootAlias,
+			int suffixSeed) {
+		super( sessionFactory, loadQueryInfluencers, rootAlias, suffixSeed );
+		this.cascadeActionToMatch = cascadeActionToMatch;
+	}
+
+	@Override
+	protected FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition) {
+		return attributeDefinition.determineCascadeStyle().doCascade( cascadeActionToMatch ) ? EAGER : DELAYED;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanImpl.java
new file mode 100644
index 0000000000..47daeb3c13
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanImpl.java
@@ -0,0 +1,59 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.loader.plan.internal;
+
+import java.util.Collections;
+import java.util.List;
+
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.Return;
+
+/**
+ * Implementation of LoadPlan.
+ *
+ * @author Steve Ebersole
+ */
+public class LoadPlanImpl implements LoadPlan {
+	private final boolean hasScalars;
+	private final List<Return> returns;
+
+	public LoadPlanImpl(boolean hasScalars, List<Return> returns) {
+		this.hasScalars = hasScalars;
+		this.returns = returns;
+	}
+
+	public LoadPlanImpl(boolean hasScalars, Return rootReturn) {
+		this( hasScalars, Collections.singletonList( rootReturn ) );
+	}
+
+	@Override
+	public boolean hasAnyScalarReturns() {
+		return hasScalars;
+	}
+
+	@Override
+	public List<Return> getReturns() {
+		return returns;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..f329b30e26
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
@@ -0,0 +1,268 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.loader.plan.internal;
+
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.DefaultEntityAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.GeneratedCollectionAliases;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.spi.AbstractFetchOwner;
+import org.hibernate.loader.plan.spi.AbstractLoadPlanBuilderStrategy;
+import org.hibernate.loader.plan.spi.CollectionFetch;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.FetchOwner;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.LoadPlanBuilderStrategy;
+import org.hibernate.loader.plan.spi.Return;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.Loadable;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.type.EntityType;
+import org.hibernate.type.Type;
+
+/**
+ * LoadPlanBuilderStrategy implementation used for building LoadPlans with a single processing RootEntity LoadPlan building.
+ *
+ * Really this is a single-root LoadPlan building strategy for building LoadPlans for:<ul>
+ *     <li>entity load plans</li>
+ *     <li>cascade load plans</li>
+ *     <li>collection initializer plans</li>
+ * </ul>
+ *
+ * @author Steve Ebersole
+ */
+public class SingleRootReturnLoadPlanBuilderStrategy
+		extends AbstractLoadPlanBuilderStrategy
+		implements LoadPlanBuilderStrategy {
+
+	private final LoadQueryInfluencers loadQueryInfluencers;
+
+	private final String rootAlias;
+	private int currentSuffixBase;
+
+	private Return rootReturn;
+
+	private PropertyPath propertyPath = new PropertyPath( "" );
+
+	public SingleRootReturnLoadPlanBuilderStrategy(
+			SessionFactoryImplementor sessionFactory,
+			LoadQueryInfluencers loadQueryInfluencers,
+			String rootAlias,
+			int suffixSeed) {
+		super( sessionFactory );
+		this.loadQueryInfluencers = loadQueryInfluencers;
+		this.rootAlias = rootAlias;
+		this.currentSuffixBase = suffixSeed;
+	}
+
+	@Override
+	protected boolean supportsRootEntityReturns() {
+		return true;
+	}
+
+	@Override
+	protected boolean supportsRootCollectionReturns() {
+		return true;
+	}
+
+	@Override
+	protected void addRootReturn(Return rootReturn) {
+		if ( this.rootReturn != null ) {
+			throw new HibernateException( "Root return already identified" );
+		}
+		this.rootReturn = rootReturn;
+	}
+
+	@Override
+	public LoadPlan buildLoadPlan() {
+		return new LoadPlanImpl( false, rootReturn );
+	}
+
+	@Override
+	protected FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition) {
+		FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan( loadQueryInfluencers, propertyPath );
+		if ( fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN ) {
+			// see if we need to alter the join fetch to another form for any reason
+			fetchStrategy = adjustJoinFetchIfNeeded( attributeDefinition, fetchStrategy );
+		}
+		return fetchStrategy;
+	}
+
+	protected FetchStrategy adjustJoinFetchIfNeeded(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy) {
+		if ( currentDepth() > sessionFactory().getSettings().getMaximumFetchDepth() ) {
+			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
+		}
+
+		if ( attributeDefinition.getType().isCollectionType() && isTooManyCollections() ) {
+			// todo : have this revert to batch or subselect fetching once "sql gen redesign" is in place
+			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
+		}
+
+		return fetchStrategy;
+	}
+
+	@Override
+	protected boolean isTooManyCollections() {
+		return false;
+	}
+
+	@Override
+	protected EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition) {
+		final String entityName = entityDefinition.getEntityPersister().getEntityName();
+		return new EntityReturn(
+				sessionFactory(),
+				rootAlias,
+				LockMode.NONE, // todo : for now
+				entityName,
+				StringHelper.generateAlias( StringHelper.unqualifyEntityName( entityName ), currentDepth() ),
+				new DefaultEntityAliases(
+						(Loadable) entityDefinition.getEntityPersister(),
+						Integer.toString( currentSuffixBase++ ) + '_'
+				)
+		);
+	}
+
+	@Override
+	protected CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition) {
+		final CollectionPersister persister = collectionDefinition.getCollectionPersister();
+		final String collectionRole = persister.getRole();
+
+		final CollectionAliases collectionAliases = new GeneratedCollectionAliases(
+				collectionDefinition.getCollectionPersister(),
+				Integer.toString( currentSuffixBase++ ) + '_'
+		);
+		final Type elementType = collectionDefinition.getCollectionPersister().getElementType();
+		final EntityAliases elementAliases;
+		if ( elementType.isEntityType() ) {
+			final EntityType entityElementType = (EntityType) elementType;
+			elementAliases = new DefaultEntityAliases(
+					(Loadable) entityElementType.getAssociatedJoinable( sessionFactory() ),
+					Integer.toString( currentSuffixBase++ ) + '_'
+			);
+		}
+		else {
+			elementAliases = null;
+		}
+
+		return new CollectionReturn(
+				sessionFactory(),
+				rootAlias,
+				LockMode.NONE, // todo : for now
+				persister.getOwnerEntityPersister().getEntityName(),
+				StringHelper.unqualify( collectionRole ),
+				collectionAliases,
+				elementAliases
+		);
+	}
+
+	@Override
+	protected CollectionFetch buildCollectionFetch(
+			FetchOwner fetchOwner,
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy) {
+		final CollectionDefinition collectionDefinition = attributeDefinition.toCollectionDefinition();
+		final CollectionAliases collectionAliases = new GeneratedCollectionAliases(
+				collectionDefinition.getCollectionPersister(),
+				Integer.toString( currentSuffixBase++ ) + '_'
+		);
+		final Type elementType = collectionDefinition.getCollectionPersister().getElementType();
+		final EntityAliases elementAliases;
+		if ( elementType.isEntityType() ) {
+			final EntityType entityElementType = (EntityType) elementType;
+			elementAliases = new DefaultEntityAliases(
+					(Loadable) entityElementType.getAssociatedJoinable( sessionFactory() ),
+					Integer.toString( currentSuffixBase++ ) + '_'
+			);
+		}
+		else {
+			elementAliases = null;
+		}
+
+		return new CollectionFetch(
+				sessionFactory(),
+				createImplicitAlias(),
+				LockMode.NONE, // todo : for now
+				(AbstractFetchOwner) fetchOwner,
+				fetchStrategy,
+				attributeDefinition.getName(),
+				collectionAliases,
+				elementAliases
+		);
+	}
+
+	@Override
+	protected EntityFetch buildEntityFetch(
+			FetchOwner fetchOwner,
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy) {
+		final EntityDefinition entityDefinition = attributeDefinition.toEntityDefinition();
+
+		return new EntityFetch(
+				sessionFactory(),
+				createImplicitAlias(),
+				LockMode.NONE, // todo : for now
+				(AbstractFetchOwner) fetchOwner,
+				attributeDefinition.getName(),
+				fetchStrategy,
+				StringHelper.generateAlias( entityDefinition.getEntityPersister().getEntityName(), currentDepth() ),
+				new DefaultEntityAliases(
+						(Loadable) entityDefinition.getEntityPersister(),
+						Integer.toString( currentSuffixBase++ ) + '_'
+				)
+		);
+	}
+
+	@Override
+	protected CompositeFetch buildCompositeFetch(FetchOwner fetchOwner, CompositeDefinition attributeDefinition) {
+		return new CompositeFetch(
+				sessionFactory(),
+				createImplicitAlias(),
+				(AbstractFetchOwner) fetchOwner,
+				attributeDefinition.getName()
+		);
+	}
+
+	private int implicitAliasUniqueness = 0;
+
+	private String createImplicitAlias() {
+		return "ia" + implicitAliasUniqueness++;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetch.java
new file mode 100644
index 0000000000..e7d2751247
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetch.java
@@ -0,0 +1,88 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractFetch extends AbstractFetchOwner implements Fetch {
+	private final FetchOwner owner;
+	private final String ownerProperty;
+	private final FetchStrategy fetchStrategy;
+
+	private final PropertyPath propertyPath;
+
+	public AbstractFetch(
+			SessionFactoryImplementor factory,
+			String alias,
+			LockMode lockMode,
+			AbstractFetchOwner owner,
+			String ownerProperty,
+			FetchStrategy fetchStrategy) {
+		super( factory, alias, lockMode );
+		this.owner = owner;
+		this.ownerProperty = ownerProperty;
+		this.fetchStrategy = fetchStrategy;
+
+		owner.addFetch( this );
+
+		this.propertyPath = owner.getPropertyPath().append( ownerProperty );
+	}
+
+	@Override
+	public FetchOwner getOwner() {
+		return owner;
+	}
+
+	@Override
+	public String getOwnerPropertyName() {
+		return ownerProperty;
+	}
+
+	@Override
+	public FetchStrategy getFetchStrategy() {
+		return fetchStrategy;
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+		if ( fetchStrategy.getStyle() == FetchStyle.JOIN ) {
+			if ( this.fetchStrategy.getStyle() != FetchStyle.JOIN ) {
+				throw new HibernateException( "Cannot specify join fetch from owner that is a non-joined fetch" );
+			}
+		}
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
new file mode 100644
index 0000000000..87063b9278
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
@@ -0,0 +1,75 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractFetchOwner extends AbstractPlanNode implements FetchOwner {
+	private final String alias;
+	private final LockMode lockMode;
+
+	private List<Fetch> fetches;
+
+	public AbstractFetchOwner(SessionFactoryImplementor factory, String alias, LockMode lockMode) {
+		super( factory );
+		this.alias = alias;
+		if ( alias == null ) {
+			throw new HibernateException( "alias must be specified" );
+		}
+		this.lockMode = lockMode;
+	}
+
+	public String getAlias() {
+		return alias;
+	}
+
+	public LockMode getLockMode() {
+		return lockMode;
+	}
+
+	void addFetch(Fetch fetch) {
+		if ( fetch.getOwner() != this ) {
+			throw new IllegalArgumentException( "Fetch and owner did not match" );
+		}
+
+		if ( fetches == null ) {
+			fetches = new ArrayList<Fetch>();
+		}
+
+		fetches.add( fetch );
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..f9324daa96
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
@@ -0,0 +1,212 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+import java.util.ArrayDeque;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractLoadPlanBuilderStrategy implements LoadPlanBuilderStrategy {
+	private final SessionFactoryImplementor sessionFactory;
+
+	private ArrayDeque<FetchOwner> fetchOwnerStack = new ArrayDeque<FetchOwner>();
+
+	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory) {
+		this.sessionFactory = sessionFactory;
+	}
+
+	public SessionFactoryImplementor sessionFactory() {
+		return sessionFactory;
+	}
+
+	protected FetchOwner currentFetchOwner() {
+		return fetchOwnerStack.peekLast();
+	}
+
+	@Override
+	public void start() {
+		// nothing to do
+	}
+
+	@Override
+	public void finish() {
+		// nothing to do
+	}
+
+	@Override
+	public void startingEntity(EntityDefinition entityDefinition) {
+		if ( fetchOwnerStack.isEmpty() ) {
+			// this is a root...
+			if ( ! supportsRootEntityReturns() ) {
+				throw new HibernateException( "This strategy does not support root entity returns" );
+			}
+			final EntityReturn entityReturn = buildRootEntityReturn( entityDefinition );
+			addRootReturn( entityReturn );
+			fetchOwnerStack.push( entityReturn );
+		}
+		// otherwise this call should represent a fetch which should have been handled in #startingAttribute
+	}
+
+	protected boolean supportsRootEntityReturns() {
+		return false;
+	}
+
+	protected abstract void addRootReturn(Return rootReturn);
+
+	@Override
+	public void finishingEntity(EntityDefinition entityDefinition) {
+		// nothing to do
+	}
+
+	@Override
+	public void startingCollection(CollectionDefinition collectionDefinition) {
+		if ( fetchOwnerStack.isEmpty() ) {
+			// this is a root...
+			if ( ! supportsRootCollectionReturns() ) {
+				throw new HibernateException( "This strategy does not support root collection returns" );
+			}
+			final CollectionReturn collectionReturn = buildRootCollectionReturn( collectionDefinition );
+			addRootReturn( collectionReturn );
+			fetchOwnerStack.push( collectionReturn );
+		}
+	}
+
+	protected boolean supportsRootCollectionReturns() {
+		return false;
+	}
+
+	@Override
+	public void finishingCollection(CollectionDefinition collectionDefinition) {
+		// nothing to do
+	}
+
+	@Override
+	public void startingComposite(CompositeDefinition compositeDefinition) {
+		if ( fetchOwnerStack.isEmpty() ) {
+			throw new HibernateException( "A component cannot be the root of a walk nor a graph" );
+		}
+	}
+
+	@Override
+	public void finishingComposite(CompositeDefinition compositeDefinition) {
+		// nothing to do
+	}
+
+	@Override
+	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
+		final Type attributeType = attributeDefinition.getType();
+
+		final boolean isComponentType = attributeType.isComponentType();
+		final boolean isBasicType = ! ( isComponentType || attributeType.isAssociationType() );
+
+		if ( isBasicType ) {
+			return true;
+		}
+		else if ( isComponentType ) {
+			return handleCompositeAttribute( (CompositeDefinition) attributeDefinition );
+		}
+		else {
+			return handleAssociationAttribute( (AssociationAttributeDefinition) attributeDefinition );
+		}
+	}
+
+
+	@Override
+	public void finishingAttribute(AttributeDefinition attributeDefinition) {
+		final Type attributeType = attributeDefinition.getType();
+
+		final boolean isComponentType = attributeType.isComponentType();
+		final boolean isBasicType = ! ( isComponentType || attributeType.isAssociationType() );
+
+		if ( ! isBasicType ) {
+			fetchOwnerStack.removeLast();
+		}
+	}
+
+	protected boolean handleCompositeAttribute(CompositeDefinition attributeDefinition) {
+		final FetchOwner fetchOwner = fetchOwnerStack.peekLast();
+		final CompositeFetch fetch = buildCompositeFetch( fetchOwner, attributeDefinition );
+		fetchOwnerStack.addLast( fetch );
+		return true;
+	}
+
+	protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
+		final FetchStrategy fetchStrategy = determineFetchPlan( attributeDefinition );
+		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
+			return false;
+		}
+
+		final FetchOwner fetchOwner = fetchOwnerStack.peekLast();
+		fetchOwner.validateFetchPlan( fetchStrategy );
+
+		final Fetch associationFetch;
+		if ( attributeDefinition.isCollection() ) {
+			associationFetch = buildCollectionFetch( fetchOwner, attributeDefinition, fetchStrategy );
+		}
+		else {
+			associationFetch = buildEntityFetch( fetchOwner, attributeDefinition, fetchStrategy );
+		}
+		fetchOwnerStack.addLast( associationFetch );
+
+		return true;
+	}
+
+	protected abstract FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition);
+
+	protected int currentDepth() {
+		return fetchOwnerStack.size();
+	}
+
+	protected boolean isTooManyCollections() {
+		return false;
+	}
+
+	protected abstract EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition);
+
+	protected abstract CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition);
+
+	protected abstract CollectionFetch buildCollectionFetch(
+			FetchOwner fetchOwner,
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy);
+
+	protected abstract EntityFetch buildEntityFetch(
+			FetchOwner fetchOwner,
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy);
+
+	protected abstract CompositeFetch buildCompositeFetch(FetchOwner fetchOwner, CompositeDefinition attributeDefinition);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractPlanNode.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractPlanNode.java
new file mode 100644
index 0000000000..eb789612a2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractPlanNode.java
@@ -0,0 +1,43 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+
+/**
+ * Base class for LoadPlan nodes to hold references to the session factory.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class AbstractPlanNode {
+	private final SessionFactoryImplementor sessionFactory;
+
+	public AbstractPlanNode(SessionFactoryImplementor sessionFactory) {
+		this.sessionFactory = sessionFactory;
+	}
+
+	protected SessionFactoryImplementor sessionFactory() {
+		return sessionFactory;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
new file mode 100644
index 0000000000..973d147795
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
@@ -0,0 +1,80 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.collection.QueryableCollection;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionFetch extends AbstractFetch implements CollectionReference {
+	private final CollectionAliases collectionAliases;
+	private final EntityAliases elementEntityAliases;
+
+	private final CollectionPersister persister;
+
+	public CollectionFetch(
+			SessionFactoryImplementor sessionFactory,
+			String alias,
+			LockMode lockMode,
+			AbstractFetchOwner owner,
+			FetchStrategy fetchStrategy,
+			String ownerProperty,
+			CollectionAliases collectionAliases,
+			EntityAliases elementEntityAliases) {
+		super( sessionFactory, alias, lockMode, owner, ownerProperty, fetchStrategy );
+		this.collectionAliases = collectionAliases;
+		this.elementEntityAliases = elementEntityAliases;
+
+		final String role = owner.retrieveFetchSourcePersister().getEntityName() + '.' + getOwnerPropertyName();
+		this.persister = sessionFactory.getCollectionPersister( role );
+	}
+
+	@Override
+	public CollectionAliases getCollectionAliases() {
+		return collectionAliases;
+	}
+
+	@Override
+	public EntityAliases getElementEntityAliases() {
+		return elementEntityAliases;
+	}
+
+	@Override
+	public CollectionPersister getCollectionPersister() {
+		return persister;
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return ( (QueryableCollection) getCollectionPersister() ).getElementPersister();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
new file mode 100644
index 0000000000..fa0460e922
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
@@ -0,0 +1,73 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.LockMode;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.persister.collection.CollectionPersister;
+
+/**
+ * Represents a reference to an owned collection either as a return or as a fetch
+ *
+ * @author Steve Ebersole
+ */
+public interface CollectionReference {
+	/**
+	 * Retrieve the alias associated with the persister (entity/collection).
+	 *
+	 * @return The alias
+	 */
+	public String getAlias();
+
+	/**
+	 * Retrieve the lock mode associated with this return.
+	 *
+	 * @return The lock mode.
+	 */
+	public LockMode getLockMode();
+
+	/**
+	 * Retrieves the CollectionPersister describing the collection associated with this Return.
+	 *
+	 * @return The CollectionPersister.
+	 */
+	public CollectionPersister getCollectionPersister();
+
+	/**
+	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to the
+	 * this collection.
+	 *
+	 * @return The ResultSet alias descriptor for the collection
+	 */
+	public CollectionAliases getCollectionAliases();
+
+	/**
+	 * If the elements of this collection are entities, this methods returns the JDBC ResultSet alias descriptions
+	 * for that entity; {@code null} indicates a non-entity collection.
+	 *
+	 * @return The ResultSet alias descriptor for the collection's entity element, or {@code null}
+	 */
+	public EntityAliases getElementEntityAliases();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
new file mode 100644
index 0000000000..0dacc39806
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
@@ -0,0 +1,113 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.collection.QueryableCollection;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionReturn extends AbstractFetchOwner implements Return, FetchOwner, CollectionReference {
+	private final String ownerEntityName;
+	private final String ownerProperty;
+	private final CollectionAliases collectionAliases;
+	private final EntityAliases elementEntityAliases;
+
+	private final CollectionPersister persister;
+
+	private final PropertyPath propertyPath = new PropertyPath(); // its a root
+
+	public CollectionReturn(
+			SessionFactoryImplementor sessionFactory,
+			String alias,
+			LockMode lockMode,
+			String ownerEntityName,
+			String ownerProperty,
+			CollectionAliases collectionAliases,
+			EntityAliases elementEntityAliases) {
+		super( sessionFactory, alias, lockMode );
+		this.ownerEntityName = ownerEntityName;
+		this.ownerProperty = ownerProperty;
+		this.collectionAliases = collectionAliases;
+		this.elementEntityAliases = elementEntityAliases;
+
+		final String role = ownerEntityName + '.' + ownerProperty;
+		this.persister = sessionFactory.getCollectionPersister( role );
+	}
+
+	/**
+	 * Returns the class owning the collection.
+	 *
+	 * @return The class owning the collection.
+	 */
+	public String getOwnerEntityName() {
+		return ownerEntityName;
+	}
+
+	/**
+	 * Returns the name of the property representing the collection from the {@link #getOwnerEntityName}.
+	 *
+	 * @return The name of the property representing the collection on the owner class.
+	 */
+	public String getOwnerProperty() {
+		return ownerProperty;
+	}
+
+	@Override
+	public CollectionAliases getCollectionAliases() {
+		return collectionAliases;
+	}
+
+	@Override
+	public EntityAliases getElementEntityAliases() {
+		return elementEntityAliases;
+	}
+
+	@Override
+	public CollectionPersister getCollectionPersister() {
+		return persister;
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return ( (QueryableCollection) persister ).getElementPersister();
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
new file mode 100644
index 0000000000..48d2f4c415
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
@@ -0,0 +1,51 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositeFetch extends AbstractFetch implements Fetch {
+	public static final FetchStrategy FETCH_PLAN = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
+
+	public CompositeFetch(
+			SessionFactoryImplementor sessionFactory,
+			String alias,
+			AbstractFetchOwner owner,
+			String ownerProperty) {
+		super( sessionFactory, alias, LockMode.NONE, owner, ownerProperty, FETCH_PLAN );
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return getOwner().retrieveFetchSourcePersister();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
new file mode 100644
index 0000000000..252e3e0288
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
@@ -0,0 +1,78 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.EntityType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityFetch extends AbstractFetch implements EntityReference {
+	private final String sqlTableAlias;
+	private final EntityAliases entityAliases;
+
+	private final EntityPersister persister;
+
+	public EntityFetch(
+			SessionFactoryImplementor sessionFactory,
+			String alias,
+			LockMode lockMode,
+			AbstractFetchOwner owner,
+			String ownerProperty,
+			FetchStrategy fetchStrategy,
+			String sqlTableAlias,
+			EntityAliases entityAliases) {
+		super( sessionFactory, alias, lockMode, owner, ownerProperty, fetchStrategy );
+		this.sqlTableAlias = sqlTableAlias;
+		this.entityAliases = entityAliases;
+
+		final EntityType type = (EntityType) owner.retrieveFetchSourcePersister().getPropertyType( ownerProperty );
+		this.persister = sessionFactory.getEntityPersister( type.getAssociatedEntityName() );
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return persister;
+	}
+
+	@Override
+	public EntityAliases getEntityAliases() {
+		return entityAliases;
+	}
+
+	@Override
+	public String getSqlTableAlias() {
+		return sqlTableAlias;
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return persister;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
new file mode 100644
index 0000000000..d1102acc7b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
@@ -0,0 +1,73 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.LockMode;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * Represents a reference to an entity either as a return or as a fetch
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityReference {
+	/**
+	 * Retrieve the alias associated with the persister (entity/collection).
+	 *
+	 * @return The alias
+	 */
+	public String getAlias();
+
+	/**
+	 * Retrieve the lock mode associated with this return.
+	 *
+	 * @return The lock mode.
+	 */
+	public LockMode getLockMode();
+
+	/**
+	 * Retrieves the EntityPersister describing the entity associated with this Return.
+	 *
+	 * @return The EntityPersister.
+	 */
+	public EntityPersister getEntityPersister();
+
+	/**
+	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to the this entity.
+	 *
+	 * @return The ResultSet alias descriptor.
+	 */
+	public EntityAliases getEntityAliases();
+
+	/**
+	 * Obtain the SQL table alias associated with this entity.
+	 *
+	 * TODO : eventually this needs to not be a String, but a representation like I did for the Antlr3 branch
+	 * 		(AliasRoot, I think it was called)
+	 *
+	 * @return The SQL table alias for this entity
+	 */
+	public String getSqlTableAlias();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
new file mode 100644
index 0000000000..eb14ecf297
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
@@ -0,0 +1,96 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityReturn extends AbstractFetchOwner implements Return, FetchOwner, EntityReference {
+	private final EntityAliases entityAliases;
+	private final String sqlTableAlias;
+
+	private final EntityPersister persister;
+
+	private final PropertyPath propertyPath = new PropertyPath(); // its a root
+
+	public EntityReturn(
+			SessionFactoryImplementor sessionFactory,
+			String alias,
+			LockMode lockMode,
+			String entityName,
+			String sqlTableAlias,
+			EntityAliases entityAliases) {
+		super( sessionFactory, alias, lockMode );
+		this.entityAliases = entityAliases;
+		this.sqlTableAlias = sqlTableAlias;
+
+		this.persister = sessionFactory.getEntityPersister( entityName );
+	}
+
+	@Override
+	public String getAlias() {
+		return super.getAlias();
+	}
+
+	@Override
+	public LockMode getLockMode() {
+		return super.getLockMode();
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return persister;
+	}
+
+	@Override
+	public EntityAliases getEntityAliases() {
+		return entityAliases;
+	}
+
+	@Override
+	public String getSqlTableAlias() {
+		return sqlTableAlias;
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return getEntityPersister();
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
new file mode 100644
index 0000000000..4be5ca8bbc
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
@@ -0,0 +1,59 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.PropertyPath;
+
+/**
+ * Contract for associations that are being fetched.
+ * <p/>
+ * NOTE : can represent components/embeddables
+ *
+ * @author Steve Ebersole
+ */
+public interface Fetch extends FetchOwner {
+	/**
+	 * Obtain the owner of this fetch.
+	 *
+	 * @return The fetch owner.
+	 */
+	public FetchOwner getOwner();
+
+	/**
+	 * Obtain the name of the property, relative to the owner, being fetched.
+	 *
+	 * @return The fetched property name.
+	 */
+	public String getOwnerPropertyName();
+
+	public FetchStrategy getFetchStrategy();
+
+	/**
+	 * Get the property path to this fetch
+	 *
+	 * @return The property path
+	 */
+	public PropertyPath getPropertyPath();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
new file mode 100644
index 0000000000..e28daf1b24
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
@@ -0,0 +1,68 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * Contract for owners of fetches.  Any non-scalar return could be a fetch owner.
+ *
+ * @author Steve Ebersole
+ */
+public interface FetchOwner {
+	/**
+	 * Convenient constant for returning no fetches from {@link #getFetches()}
+	 */
+	public static final Fetch[] NO_FETCHES = new Fetch[0];
+
+	/**
+	 * Retrieve the fetches owned by this return.
+	 *
+	 * @return The owned fetches.
+	 */
+	public Fetch[] getFetches();
+
+	/**
+	 * Is the asserted plan valid from this owner to a fetch?
+	 *
+	 * @param fetchStrategy The pla to validate
+	 */
+	public void validateFetchPlan(FetchStrategy fetchStrategy);
+
+	/**
+	 * Retrieve the EntityPersister that is the base for any property references in the fetches it owns.
+	 *
+	 * @return The EntityPersister, for property name resolution.
+	 */
+	public EntityPersister retrieveFetchSourcePersister();
+
+	/**
+	 * Get the property path to this fetch owner
+	 *
+	 * @return The property path
+	 */
+	public PropertyPath getPropertyPath();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
new file mode 100644
index 0000000000..0173b39714
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
@@ -0,0 +1,61 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import java.util.List;
+
+/**
+ * Describes a plan for performing a load of results.
+ *
+ * Generally speaking there are 3 forms of load plans:<ul>
+ *     <li>
+ *         An entity load plan for handling get/load handling.  This form will typically have a single
+ *         return (of type {@link EntityReturn}) defined by {@link #getReturns()}, possibly defining fetches.
+ *     </li>
+ *     <li>
+ *         A collection initializer, used to load the contents of a collection.  This form will typically have a
+ *         single return (of type {@link CollectionReturn} defined by {@link #getReturns()}, possibly defining fetches
+ *     </li>
+ *     <li>
+ *         A query load plan which can contain multiple returns of mixed type (though implementing {@link Return}).
+ *         Again, may possibly define fetches.
+ *     </li>
+ * </ul>
+ *
+ * @author Steve Ebersole
+ */
+public interface LoadPlan {
+	/**
+	 * Convenient form of checking {@link #getReturns()} for scalar root returns.
+	 *
+	 * @return {@code true} if {@link #getReturns()} contained any scalar returns; {@code false} otherwise.
+	 */
+	public boolean hasAnyScalarReturns();
+
+	public List<Return> getReturns();
+
+	// todo : would also like to see "call back" style access for handling "subsequent actions" such as:
+	// 		1) follow-on locking
+	//		2) join fetch conversions to subselect fetches
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuilder.java
new file mode 100644
index 0000000000..61b7b41ab1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuilder.java
@@ -0,0 +1,64 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor;
+
+/**
+ * Coordinates building of a {@link LoadPlan} between the {@link org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor} and
+ * {@link LoadPlanBuilderStrategy}
+ *
+ * @author Steve Ebersole
+ */
+public class LoadPlanBuilder {
+	/**
+	 * Coordinates building a LoadPlan that defines just a single root entity return (may have fetches).
+	 * <p/>
+	 * Typically this includes building load plans for entity loading or cascade loading.
+	 *
+	 * @param strategy The strategy defining the load plan shaping
+	 * @param persister The persister for the entity forming the root of the load plan.
+	 *
+	 * @return The built load plan.
+	 */
+	public static LoadPlan buildRootEntityLoadPlan(LoadPlanBuilderStrategy strategy, EntityPersister persister) {
+		MetadataDrivenModelGraphVisitor.visitEntity( strategy, persister );
+		return strategy.buildLoadPlan();
+	}
+
+	/**
+	 * Coordinates building a LoadPlan that defines just a single root collection return (may have fetches).
+	 *
+	 * @param strategy The strategy defining the load plan shaping
+	 * @param persister The persister for the collection forming the root of the load plan.
+	 *
+	 * @return The built load plan.
+	 */
+	public static LoadPlan buildRootCollectionLoadPlan(LoadPlanBuilderStrategy strategy, CollectionPersister persister) {
+		MetadataDrivenModelGraphVisitor.visitCollection( strategy, persister );
+		return strategy.buildLoadPlan();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..7c40540fbf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuilderStrategy.java
@@ -0,0 +1,40 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
+
+/**
+ * Specialized {@link org.hibernate.persister.walking.spi.AssociationVisitationStrategy} implementation for building {@link LoadPlan} instances.
+ *
+ * @author Steve Ebersole
+ */
+public interface LoadPlanBuilderStrategy extends AssociationVisitationStrategy {
+	/**
+	 * After visitation is done, build the load plan.
+	 *
+	 * @return The load plan
+	 */
+	public LoadPlan buildLoadPlan();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
new file mode 100644
index 0000000000..b94adfae9d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
@@ -0,0 +1,40 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+/**
+ * Represents a return value in the query results.  Not the same as a result (column) in the JDBC ResultSet!
+ * <p/>
+ * This is merely a unifying contract; it defines no behavior.
+ * <p/>
+ * Return is distinctly different from a {@link Fetch}.
+ *
+ * @see ScalarReturn
+ * @see EntityReturn
+ * @see CollectionReturn
+ *
+ * @author Steve Ebersole
+ */
+public interface Return {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitationStrategy.java
new file mode 100644
index 0000000000..4140249b3a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitationStrategy.java
@@ -0,0 +1,138 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+/**
+ * @author Steve Ebersole
+ */
+public interface ReturnVisitationStrategy {
+	/**
+	 * Notification we are preparing to start visitation.
+	 */
+	public void start();
+
+	/**
+	 * Notification we are finished visitation.
+	 */
+	public void finish();
+
+	/**
+	 * Notification that a new root return branch is being started.  Will be followed by calls to one of the following
+	 * based on the type of return:<ul>
+	 *     <li>{@link #handleScalarReturn}</li>
+	 *     <li>{@link #handleEntityReturn}</li>
+	 *     <li>{@link #handleCollectionReturn}</li>
+	 * </ul>
+	 *
+	 * @param rootReturn The root return at the root of the branch.
+	 */
+	public void startingRootReturn(Return rootReturn);
+
+	/**
+	 * Notification that we are finishing up processing a root return branch
+	 *
+	 * @param rootReturn The RootReturn we are finishing up processing.
+	 */
+	public void finishingRootReturn(Return rootReturn);
+
+	/**
+	 * Notification that a scalar return is being processed.  Will be surrounded by calls to {@link #startingRootReturn}
+	 * and {@link #finishingRootReturn}
+	 *
+	 * @param scalarReturn The scalar return
+	 */
+	public void handleScalarReturn(ScalarReturn scalarReturn);
+
+	/**
+	 * Notification that a root entity return is being processed.  Will be surrounded by calls to
+	 * {@link #startingRootReturn} and {@link #finishingRootReturn}
+	 *
+	 * @param rootEntityReturn The root entity return
+	 */
+	public void handleEntityReturn(EntityReturn rootEntityReturn);
+
+	/**
+	 * Notification that a root collection return is being processed.  Will be surrounded by calls to
+	 * {@link #startingRootReturn} and {@link #finishingRootReturn}
+	 *
+	 * @param rootCollectionReturn The root collection return
+	 */
+	public void handleCollectionReturn(CollectionReturn rootCollectionReturn);
+
+	/**
+	 * Notification that we are about to start processing the fetches for the given fetch owner.
+	 *
+	 * @param fetchOwner The fetch owner.
+	 */
+	public void startingFetches(FetchOwner fetchOwner);
+
+	/**
+	 * Notification that we are finishing up processing the fetches for the given fetch owner.
+	 *
+	 * @param fetchOwner The fetch owner.
+	 */
+	public void finishingFetches(FetchOwner fetchOwner);
+
+	/**
+	 * Notification we are starting the processing of an entity fetch
+	 *
+	 * @param entityFetch The entity fetch
+	 */
+	public void startingEntityFetch(EntityFetch entityFetch);
+
+	/**
+	 * Notification that we are finishing up the processing of an entity fetch
+	 *
+	 * @param entityFetch The entity fetch
+	 */
+	public void finishingEntityFetch(EntityFetch entityFetch);
+
+	/**
+	 * Notification we are starting the processing of a collection fetch
+	 *
+	 * @param collectionFetch The collection fetch
+	 */
+	public void startingCollectionFetch(CollectionFetch collectionFetch);
+
+	/**
+	 * Notification that we are finishing up the processing of a collection fetch
+	 *
+	 * @param collectionFetch The collection fetch
+	 */
+	public void finishingCollectionFetch(CollectionFetch collectionFetch);
+
+	/**
+	 * Notification we are starting the processing of a component fetch
+	 *
+	 * @param fetch The composite fetch
+	 */
+	public void startingCompositeFetch(CompositeFetch fetch);
+
+	/**
+	 * Notification that we are finishing up the processing of a composite fetch
+	 *
+	 * @param fetch The composite fetch
+	 */
+	public void finishingCompositeFetch(CompositeFetch fetch);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitor.java
new file mode 100644
index 0000000000..cf4aaf6ef9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitor.java
@@ -0,0 +1,116 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+/**
+ * Visitor for processing {@link Return} graphs
+ *
+ * @author Steve Ebersole
+ */
+public class ReturnVisitor {
+	public static void visit(Return[] rootReturns, ReturnVisitationStrategy strategy) {
+		new ReturnVisitor( strategy ).visitReturns( rootReturns );
+	}
+
+	private final ReturnVisitationStrategy strategy;
+
+	public ReturnVisitor(ReturnVisitationStrategy strategy) {
+		this.strategy = strategy;
+	}
+
+	private void visitReturns(Return[] rootReturns) {
+		strategy.start();
+
+		for ( Return rootReturn : rootReturns ) {
+			visitRootReturn( rootReturn );
+		}
+
+		strategy.finish();
+	}
+
+	private void visitRootReturn(Return rootReturn) {
+		strategy.startingRootReturn( rootReturn );
+
+		if ( org.hibernate.loader.plan.spi.ScalarReturn.class.isInstance( rootReturn ) ) {
+			strategy.handleScalarReturn( (ScalarReturn) rootReturn );
+		}
+		else {
+			visitNonScalarRootReturn( rootReturn );
+		}
+
+		strategy.finishingRootReturn( rootReturn );
+	}
+
+	private void visitNonScalarRootReturn(Return rootReturn) {
+		if ( EntityReturn.class.isInstance( rootReturn ) ) {
+			strategy.handleEntityReturn( (EntityReturn) rootReturn );
+			visitFetches( (EntityReturn) rootReturn );
+		}
+		else if ( CollectionReturn.class.isInstance( rootReturn ) ) {
+			strategy.handleCollectionReturn( (CollectionReturn) rootReturn );
+			visitFetches( (CollectionReturn) rootReturn );
+		}
+		else {
+			throw new IllegalStateException(
+					"Unexpected return type encountered; expecting a non-scalar root return, but found " +
+							rootReturn.getClass().getName()
+			);
+		}
+	}
+
+	private void visitFetches(FetchOwner fetchOwner) {
+		strategy.startingFetches( fetchOwner );
+
+		for ( Fetch fetch : fetchOwner.getFetches() ) {
+			visitFetch( fetch );
+		}
+
+		strategy.finishingFetches( fetchOwner );
+	}
+
+	private void visitFetch(Fetch fetch) {
+		if ( EntityFetch.class.isInstance( fetch ) ) {
+			strategy.startingEntityFetch( (EntityFetch) fetch );
+			visitFetches( fetch );
+			strategy.finishingEntityFetch( (EntityFetch) fetch );
+		}
+		else if ( CollectionFetch.class.isInstance( fetch ) ) {
+			strategy.startingCollectionFetch( (CollectionFetch) fetch );
+			visitFetches( fetch );
+			strategy.finishingCollectionFetch( (CollectionFetch) fetch );
+		}
+		else if ( CompositeFetch.class.isInstance( fetch ) ) {
+			strategy.startingCompositeFetch( (CompositeFetch) fetch );
+			visitFetches( fetch );
+			strategy.finishingCompositeFetch( (CompositeFetch) fetch );
+		}
+		else {
+			throw new IllegalStateException(
+					"Unexpected return type encountered; expecting a fetch return, but found " +
+							fetch.getClass().getName()
+			);
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
new file mode 100644
index 0000000000..9a3d434bbc
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
@@ -0,0 +1,52 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.type.Type;
+
+/**
+ * Represent a simple scalar return within a query result.  Generally this would be values of basic (String, Integer,
+ * etc) or composite types.
+ *
+ * @author Steve Ebersole
+ */
+public class ScalarReturn extends AbstractPlanNode implements Return {
+	private final Type type;
+	private final String columnAlias;
+
+	public ScalarReturn(SessionFactoryImplementor factory, Type type, String columnAlias) {
+		super( factory );
+		this.type = type;
+		this.columnAlias = columnAlias;
+	}
+
+	public Type getType() {
+		return type;
+	}
+
+	public String getColumnAlias() {
+		return columnAlias;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 5236a08712..0db82ac72c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1937 +1,2018 @@
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
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CollectionElementDefinition;
+import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
+import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
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
+import org.hibernate.type.AssociationType;
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
 			cacheEntryStructure = collection.isMap() ?
 					new StructuredMapCacheEntry() :
 					new StructuredCollectionCacheEntry();
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
 
+
+	// ColectionDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public CollectionPersister getCollectionPersister() {
+		return this;
+	}
+
+	@Override
+	public CollectionIndexDefinition getIndexDefinition() {
+		if ( ! hasIndex() ) {
+			return null;
+		}
+
+		return new CollectionIndexDefinition() {
+			@Override
+			public CollectionDefinition getCollectionDefinition() {
+				return AbstractCollectionPersister.this;
+			}
+
+			@Override
+			public Type getType() {
+				return getIndexType();
+			}
+
+			@Override
+			public EntityDefinition toEntityDefinition() {
+				if ( getType().isComponentType() ) {
+					throw new IllegalStateException( "Cannot treat composite collection index type as entity" );
+				}
+				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
+			}
+
+			@Override
+			public CompositeDefinition toCompositeDefinition() {
+				if ( ! getType().isComponentType() ) {
+					throw new IllegalStateException( "Cannot treat entity collection index type as composite" );
+				}
+				// todo : implement
+				throw new NotYetImplementedException();
+			}
+		};
+	}
+
+	@Override
+	public CollectionElementDefinition getElementDefinition() {
+		return new CollectionElementDefinition() {
+			@Override
+			public CollectionDefinition getCollectionDefinition() {
+				return AbstractCollectionPersister.this;
+			}
+
+			@Override
+			public Type getType() {
+				return getElementType();
+			}
+
+			@Override
+			public EntityDefinition toEntityDefinition() {
+				if ( getType().isComponentType() ) {
+					throw new IllegalStateException( "Cannot treat composite collection element type as entity" );
+				}
+				return getElementPersister();
+			}
+
+			@Override
+			public CompositeDefinition toCompositeDefinition() {
+				if ( ! getType().isComponentType() ) {
+					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
+				}
+				// todo : implement
+				throw new NotYetImplementedException();
+			}
+		};
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
index 3b2e3788dd..ecfe83c7d8 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
@@ -1,311 +1,312 @@
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
 package org.hibernate.persister.collection;
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * A strategy for persisting a collection role. Defines a contract between
  * the persistence strategy and the actual persistent collection framework
  * and session. Does not define operations that are required for querying
  * collections, or loading by outer join.<br>
  * <br>
  * Implements persistence of a collection instance while the instance is
  * referenced in a particular role.<br>
  * <br>
  * This class is highly coupled to the <tt>PersistentCollection</tt>
  * hierarchy, since double dispatch is used to load and update collection
  * elements.<br>
  * <br>
  * May be considered an immutable view of the mapping object
  *
  * @see QueryableCollection
  * @see org.hibernate.collection.spi.PersistentCollection
  * @author Gavin King
  */
-public interface CollectionPersister {
+public interface CollectionPersister extends CollectionDefinition {
 	/**
 	 * Initialize the given collection with the given key
 	 */
 	public void initialize(Serializable key, SessionImplementor session) //TODO: add owner argument!!
 	throws HibernateException;
 	/**
 	 * Is this collection role cacheable
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache
 	 */
 	public CollectionRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 	/**
 	 * Get the associated <tt>Type</tt>
 	 */
 	public CollectionType getCollectionType();
 	/**
 	 * Get the "key" type (the type of the foreign key)
 	 */
 	public Type getKeyType();
 	/**
 	 * Get the "index" type for a list or map (optional operation)
 	 */
 	public Type getIndexType();
 	/**
 	 * Get the "element" type
 	 */
 	public Type getElementType();
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass();
 	/**
 	 * Read the key from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the element from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readElement(
 		ResultSet rs,
 		Object owner,
 		String[] columnAliases,
 		SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the index from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the identifier from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readIdentifier(
 		ResultSet rs,
 		String columnAlias,
 		SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Is this an array or primitive values?
 	 */
 	public boolean isPrimitiveArray();
 	/**
 	 * Is this an array?
 	 */
 	public boolean isArray();
 	/**
 	 * Is this a one-to-many association?
 	 */
 	public boolean isOneToMany();
 	/**
 	 * Is this a many-to-many association?  Note that this is mainly
 	 * a convenience feature as the single persister does not
 	 * conatin all the information needed to handle a many-to-many
 	 * itself, as internally it is looked at as two many-to-ones.
 	 */
 	public boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters);
 
 	/**
 	 * Is this an "indexed" collection? (list or map)
 	 */
 	public boolean hasIndex();
 	/**
 	 * Is this collection lazyily initialized?
 	 */
 	public boolean isLazy();
 	/**
 	 * Is this collection "inverse", so state changes are not
 	 * propogated to the database.
 	 */
 	public boolean isInverse();
 	/**
 	 * Completely remove the persistent state of the collection
 	 */
 	public void remove(Serializable id, SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * (Re)create the collection's persistent state
 	 */
 	public void recreate(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Delete the persistent state of any elements that were removed from
 	 * the collection
 	 */
 	public void deleteRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Update the persistent state of any elements that were modified
 	 */
 	public void updateRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Insert the persistent state of any new collection elements
 	 */
 	public void insertRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Get the name of this collection role (the fully qualified class name,
 	 * extended by a "property path")
 	 */
 	public String getRole();
 	/**
 	 * Get the persister of the entity that "owns" this collection
 	 */
 	public EntityPersister getOwnerEntityPersister();
 	/**
 	 * Get the surrogate key generation strategy (optional operation)
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 	/**
 	 * Get the type of the surrogate key
 	 */
 	public Type getIdentifierType();
 	/**
 	 * Does this collection implement "orphan delete"?
 	 */
 	public boolean hasOrphanDelete();
 	/**
 	 * Is this an ordered collection? (An ordered collection is
 	 * ordered by the initialization operation, not by sorting
 	 * that happens in memory, as in the case of a sorted collection.)
 	 */
 	public boolean hasOrdering();
 
 	public boolean hasManyToManyOrdering();
 
 	/**
 	 * Get the "space" that holds the persistent state
 	 */
 	public Serializable[] getCollectionSpaces();
 
 	public CollectionMetadata getCollectionMetadata();
 
 	/**
 	 * Is cascade delete handled by the database-level
 	 * foreign key constraint definition?
 	 */
 	public abstract boolean isCascadeDeleteEnabled();
 	
 	/**
 	 * Does this collection cause version increment of the 
 	 * owning entity?
 	 */
 	public boolean isVersioned();
 	
 	/**
 	 * Can the elements of this collection change?
 	 */
 	public boolean isMutable();
 	
 	//public boolean isSubselectLoadable();
 	
 	public String getNodeName();
 	
 	public String getElementNodeName();
 	
 	public String getIndexNodeName();
 
 	public void postInstantiate() throws MappingException;
 	
 	public SessionFactoryImplementor getFactory();
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session);
 
 	/**
 	 * Generates the collection's key column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the key column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String[] getKeyColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's index column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the index column alias generation.
 	 * @return The key column aliases, or null if not indexed.
 	 */
 	public String[] getIndexColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's element column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the element column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String[] getElementColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's identifier column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the key column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String getIdentifierColumnAlias(String suffix);
 	
 	public boolean isExtraLazy();
 	public int getSize(Serializable key, SessionImplementor session);
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session);
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session);
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner);
 	public int getBatchSize();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 860f4b74b2..5456404f04 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1837 +1,1838 @@
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
+import org.hibernate.cfg.NotYetImplementedException;
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
-import org.hibernate.loader.entity.BatchingEntityLoader;
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
+import org.hibernate.persister.walking.spi.AttributeDefinition;
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
 
-		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
+		this.entityMetamodel = new EntityMetamodel( persistentClass, this, factory );
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
-		this.entityMetamodel = new EntityMetamodel( entityBinding, factory );
+		this.entityMetamodel = new EntityMetamodel( entityBinding, this, factory );
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
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = cascades.toArray( new CascadeStyle[ cascades.size() ] );
 		subclassPropertyFetchModeClosure = joinedFetchesList.toArray( new FetchMode[ joinedFetchesList.size() ] );
 
 		propertyDefinedOnSubclass = ArrayHelper.toBooleanArray( definedBySubclass );
 
 		List<FilterConfiguration> filterDefaultConditions = new ArrayList<FilterConfiguration>();
 		for ( FilterDefinition filterDefinition : entityBinding.getFilterDefinitions() ) {
 			filterDefaultConditions.add(new FilterConfiguration(filterDefinition.getFilterName(), 
 						filterDefinition.getDefaultFilterCondition(), true, null, null, null));
 		}
 		filterHelper = new FilterHelper( filterDefaultConditions, factory);
 
 		temporaryIdTableName = null;
 		temporaryIdTableDDL = null;
 
 		this.cacheEntryHelper = buildCacheEntryHelper();
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	public String getTemplateFromColumn(org.hibernate.metamodel.relational.Column column, SessionFactoryImplementor factory) {
 		String templateString;
 		if ( column.getReadFragment() != null ) {
 			templateString = getTemplateFromString( column.getReadFragment(), factory );
 		}
 		else {
 			String columnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			templateString = Template.TEMPLATE + '.' + columnName;
 		}
 		return templateString;
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
 			tableNumbers.add(  tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( colNumbers[j] );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( formNumbers[j] );
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString( this, id, getFactory() ), fieldName );
 		}
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
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
 
 		if ( !hasLazyProperties() ) throw new AssertionFailure( "no lazy properties" );
 
 		LOG.trace( "Initializing lazy properties from datastore" );
 
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
 						rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
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
 
 		LOG.trace( "Initializing lazy properties from second-level cache" );
 
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
 
 		LOG.trace( "Done initializing lazy properties" );
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
 		if ( snapshot != null ) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSettings().isJdbcBatchVersionedData();
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
 				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
 			}
 		}
 
 		if ( entityMetamodel.hasSubclasses() ) {
 			addDiscriminatorToSelect( select, tableAlias, suffix );
 		}
 
 		if ( hasRowId() ) {
 			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
 		}
 
 		return select;
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current persistent state for: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					//otherwise return the "hydrated" state (ie. associations are not resolved)
 					Type[] types = getPropertyTypes();
 					Object[] values = new Object[types.length];
 					boolean[] includeProperty = getPropertyUpdateability();
 					for ( int i = 0; i < types.length; i++ ) {
 						if ( includeProperty[i] ) {
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) throws HibernateException {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"resolving unique key [%s] to identifier for entity [%s]",
 					key,
 					getEntityName()
 			);
 		}
 
 		int propertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		if ( propertyIndex < 0 ) {
 			throw new HibernateException(
 					"Could not determine Type for property [" + uniquePropertyName + "] on entity [" + getEntityName() + "]"
 			);
 		}
 		Type propertyType = getSubclassPropertyType( propertyIndex );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( generateIdByUniqueKeySelectString( uniquePropertyName ) );
 			try {
 				propertyType.nullSafeSet( ps, key, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve unique property [%s] to identifier for entity [%s]",
 							uniquePropertyName,
 							getEntityName()
 					),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	protected String generateIdByUniqueKeySelectString(String uniquePropertyName) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "resolve id by unique property [" + getEntityName() + "." + uniquePropertyName + "]" );
 		}
 
 		final String rooAlias = getRootAlias();
 
 		select.setFromClause( fromTableFragment( rooAlias ) + fromJoinFragment( rooAlias, true, false ) );
 
 		SelectFragment selectFragment = new SelectFragment();
 		selectFragment.addColumns( rooAlias, getIdentifierColumnNames(), getIdentifierAliases() );
 		select.setSelectClause( selectFragment );
 
 		StringBuilder whereClauseBuffer = new StringBuilder();
 		final int uniquePropertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		final String uniquePropertyTableAlias = generateTableAlias(
 				rooAlias,
 				getSubclassPropertyTableNumber( uniquePropertyIndex )
 		);
 		String sep = "";
 		for ( String columnTemplate : getSubclassPropertyColumnReaderTemplateClosure()[uniquePropertyIndex] ) {
 			if ( columnTemplate == null ) {
 				continue;
 			}
 			final String columnReference = StringHelper.replace( columnTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( columnReference ).append( "=?" );
 			sep = " and ";
 		}
 		for ( String formulaTemplate : getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex] ) {
 			if ( formulaTemplate == null ) {
 				continue;
 			}
 			final String formulaReference = StringHelper.replace( formulaTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( formulaReference ).append( "=?" );
 			sep = " and ";
 		}
 		whereClauseBuffer.append( whereJoinFragment( rooAlias, true, false ) );
 
 		select.setWhereClause( whereClauseBuffer.toString() );
 
 		return select.setOuterJoins( "", "" ).toStatementString();
 	}
 
 
 	/**
 	 * Generate the SQL that selects the version number by id
 	 */
 	protected String generateSelectVersionString() {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getVersionedTableName() );
 		if ( isVersioned() ) {
 			select.addColumn( versionColumnName );
 		}
 		else {
 			select.addColumns( rootTableKeyColumnNames );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyInsertGenerationInclusions() );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyUpdateGenerationInclusions() );
 	}
 
 	private String generateGeneratedValuesSelectString(ValueInclusion[] inclusions) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment( getRootAlias(), inclusions );
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final ValueInclusion[] inclusions) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					// TODO : currently we really do not handle ValueInclusion.PARTIAL...
 					// ValueInclusion.PARTIAL would indicate parts of a component need to
 					// be included in the select; currently we then just render the entire
 					// component into the select clause in that case.
 					public boolean includeProperty(int propertyNumber) {
 						return inclusions[propertyNumber] != ValueInclusion.NONE;
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
         if (LOG.isTraceEnabled()) LOG.trace("Forcing version increment [" + MessageHelper.infoString(this, id, getFactory()) + "; "
                                             + getVersionType().toLoggableString(currentVersion, getFactory()) + " -> "
                                             + getVersionType().toLoggableString(nextVersion, getFactory()) + "]");
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
@@ -2819,2255 +2820,2362 @@ public abstract class AbstractEntityPersister
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 	        final Serializable id,
 	        final Object object,
 	        final Loadable rootLoadable,
 	        final String[][] suffixedPropertyColumns,
 	        final boolean allProperties,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Hydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( sequentialSelect );
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = BackrefPropertyAccessor.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialResultSet, sequentialSelect );
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( sequentialSelect );
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
 		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSettings().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException("no sequential selects");
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 	        final boolean[] notNull,
 	        String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0} (native id)", getEntityName() );
 			if ( isVersioned() ) {
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 			}
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session, false );
 			}
 			public Object getEntity() {
 				return object;
 			}
 		};
 
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
 		return getFactory().getDialect().getIdentitySelectString(
 				getTableName(0),
 				getKeyColumns(0)[0],
 				getIdentifierType().sqlTypes( getFactory() )[0]
 		);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 			.setTableName( getTableName(0) )
 			.addColumns( getKeyColumns(0) )
 			.addCondition( getPropertyColumnNames(propertyName), "=?" )
 			.toStatementString();
 	}
 
 	private BasicBatchKey inserBatchKey;
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 	        final Object[] fields,
 	        final boolean[] notNull,
 	        final int j,
 	        final String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( j == 0 && isVersioned() )
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 		}
 
 		// TODO : shouldn't inserts be Expectations.NONE?
 		final Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		if ( useBatch && inserBatchKey == null ) {
 			inserBatchKey = new BasicBatchKey(
 					getEntityName() + "#INSERT",
 					expectation
 			);
 		}
 		final boolean callable = isInsertCallable( j );
 
 		try {
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( inserBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index, false );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( insert ), insert, -1 );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( insert );
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 			);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()] );
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index, true );
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 							? getPropertyUpdateability()
 							: includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( update ), id, j, expectation, update );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( update );
 				}
 			}
 
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Version: {0}", version );
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Delete handled by foreign key constraint: {0}", getTableName( j ) );
 			}
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( delete ), id, j, expectation, delete );
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
 					session.getTransactionCoordinator().getJdbcCoordinator().release( delete );
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
         if ( LOG.isDebugEnabled() ) {
             LOG.debugf( "Static SQL for entity: %s", getEntityName() );
             if ( sqlLazySelectString != null ) {
 				LOG.debugf( " Lazy select: %s", sqlLazySelectString );
 			}
             if ( sqlVersionSelectString != null ) {
 				LOG.debugf( " Version select: %s", sqlVersionSelectString );
 			}
             if ( sqlSnapshotSelectString != null ) {
 				LOG.debugf( " Snapshot select: %s", sqlSnapshotSelectString );
 			}
 			for ( int j = 0; j < getTableSpan(); j++ ) {
                 LOG.debugf( " Insert %s: %s", j, getSQLInsertStrings()[j] );
                 LOG.debugf( " Update %s: %s", j, getSQLUpdateStrings()[j] );
                 LOG.debugf( " Delete %s: %s", j, getSQLDeleteStrings()[j] );
 			}
             if ( sqlIdentityInsertString != null ) {
 				LOG.debugf( " Identity insert: %s", sqlIdentityInsertString );
 			}
             if ( sqlUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (all fields): %s", sqlUpdateByRowIdString );
 			}
             if ( sqlLazyUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (non-lazy fields): %s", sqlLazyUpdateByRowIdString );
 			}
             if ( sqlInsertGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Insert-generated property select: %s", sqlInsertGeneratedValuesSelectString );
 			}
             if ( sqlUpdateGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Update-generated property select: %s", sqlUpdateGeneratedValuesSelectString );
 			}
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinType.LEFT_OUTER_JOIN :
 					JoinType.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 
 	}
 
 	public void postInstantiate() throws MappingException {
+		generateEntityDefinition();
+
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
-
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
 				LockMode.UPGRADE_SKIPLOCKED,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_SKIPLOCKED )
 			);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingActions.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingActions.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryHelper.getCacheEntryStructure();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 		return cacheEntryHelper.buildCacheEntry( entity, state, version, session );
 	}
 
 	public boolean hasNaturalIdCache() {
 		return naturalIdRegionAccessStrategy != null;
 	}
 	
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return naturalIdRegionAccessStrategy;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return ( VersionType ) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( ! hasNaturalIdentifier() ) {
 			return;
 		}
 
 		if ( getEntityMetamodel().hasImmutableNaturalId() ) {
 			// we assume there were no changes to natural id during detachment for now, that is validated later
 			// during flush.
 			return;
 		}
 
 		final NaturalIdHelper naturalIdHelper = session.getPersistenceContext().getNaturalIdHelper();
 		final Serializable id = getIdentifier( entity, session );
 
 		// for reattachment of mutable natural-ids, we absolutely positively have to grab the snapshot from the
 		// database, because we have no other way to know if the state changed while detached.
 		final Object[] naturalIdSnapshot;
 		final Object[] entitySnapshot = session.getPersistenceContext().getDatabaseSnapshot( id, this );
 		if ( entitySnapshot == StatefulPersistenceContext.NO_ROW ) {
 			naturalIdSnapshot = null;
 		}
 		else {
 			naturalIdSnapshot = naturalIdHelper.extractNaturalIdValues( entitySnapshot, this );
 		}
 
 		naturalIdHelper.removeSharedNaturalIdCrossReference( this, id, naturalIdSnapshot );
 		naturalIdHelper.manageLocalNaturalIdCrossReference(
 				this,
 				id,
 				naturalIdHelper.extractNaturalIdValues( entity, this ),
 				naturalIdSnapshot,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( hasCache() ) {
 			CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
 			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return entityMetamodel.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return entityMetamodel.getPropertyInsertGenerationInclusions();
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return entityMetamodel.getPropertyUpdateGenerationInclusions();
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 	        ValueInclusion[] includeds) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					for ( int i = 0; i < getPropertySpan(); i++ ) {
 						if ( includeds[i] != ValueInclusion.NONE ) {
 							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 							setPropertyValue( entity, i, state[i] );
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        sql
 			);
 		}
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(
 			Object[] naturalIdValues,
 			LockOptions lockOptions,
 			SessionImplementor session) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Resolving natural-id [%s] to id : %s ",
 					naturalIdValues,
 					MessageHelper.infoString( this )
 			);
 		}
 
 		final boolean[] valueNullness = determineValueNullness( naturalIdValues );
 		final String sqlEntityIdByNaturalIdString = determinePkByNaturalIdQuery( valueNullness );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlEntityIdByNaturalIdString );
 			try {
 				int positions = 1;
 				int loop = 0;
 				for ( int idPosition : getNaturalIdentifierProperties() ) {
 					final Object naturalIdValue = naturalIdValues[loop++];
 					if ( naturalIdValue != null ) {
 						final Type type = getPropertyTypes()[idPosition];
 						type.nullSafeSet( ps, naturalIdValue, positions, session );
 						positions += type.getColumnSpan( session.getFactory() );
 					}
 				}
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					return (Serializable) getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve natural-id [%s] to id : %s",
 							naturalIdValues,
 							MessageHelper.infoString( this )
 					),
 					sqlEntityIdByNaturalIdString
 			);
 		}
 	}
 
 	private boolean[] determineValueNullness(Object[] naturalIdValues) {
 		boolean[] nullness = new boolean[ naturalIdValues.length ];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( ! hasNaturalIdentifier() ) {
 			throw new HibernateException( "Attempt to build natural-id -> PK resolution query for entity that does not define natural id" );
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && ! ArrayHelper.isAllFalse( valueNullness ) ) {
 				throw new HibernateException( "Null value(s) passed to lookup by non-nullable natural-id" );
 			}
 			if ( cachedPkByNonNullableNaturalIdQuery == null ) {
 				cachedPkByNonNullableNaturalIdQuery = generateEntityIdByNaturalIdSql( null );
 			}
 			return cachedPkByNonNullableNaturalIdQuery;
 		}
 
 		// Otherwise, regenerate it each time
 		return generateEntityIdByNaturalIdSql( valueNullness );
 	}
 
 	@SuppressWarnings("UnnecessaryUnboxing")
 	protected boolean isNaturalIdNonNullable() {
 		if ( naturalIdIsNonNullable == null ) {
 			naturalIdIsNonNullable = determineNaturalIdNullability();
 		}
 		return naturalIdIsNonNullable.booleanValue();
 	}
 
 	private boolean determineNaturalIdNullability() {
 		boolean[] nullability = getPropertyNullability();
 		for ( int position : getNaturalIdentifierProperties() ) {
 			// if any individual property is nullable, return false
 			if ( nullability[position] ) {
 				return false;
 			}
 		}
 		// return true if we found no individually nullable properties
 		return true;
 	}
 
 	private String generateEntityIdByNaturalIdSql(boolean[] valueNullness) {
 		EntityPersister rootPersister = getFactory().getEntityPersister( getRootEntityName() );
 		if ( rootPersister != this ) {
 			if ( rootPersister instanceof AbstractEntityPersister ) {
 				return ( (AbstractEntityPersister) rootPersister ).generateEntityIdByNaturalIdSql( valueNullness );
 			}
 		}
 
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
 		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
 
 		final StringBuilder whereClause = new StringBuilder();
 		final int[] propertyTableNumbers = getPropertyTableNumbers();
 		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
 		int valuesIndex = -1;
 		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
 			valuesIndex++;
 			if ( propIdx > 0 ) {
 				whereClause.append( " and " );
 			}
 
 			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
 			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
 			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
 			final String[] aliasedPropertyColumns = StringHelper.qualify( tableAlias, propertyColumnNames );
 
 			if ( valueNullness != null && valueNullness[valuesIndex] ) {
 				whereClause.append( StringHelper.join( " is null and ", aliasedPropertyColumns ) ).append( " is null" );
 			}
 			else {
 				whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
 			}
 		}
 
 		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
 
 		return select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 	
 	public static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equalsIgnoreCase( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 	
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return entityMetamodel.getInstrumentationMetadata();
 	}
 
 	@Override
 	public String getTableAliasForColumn(String columnName, String rootAlias) {
 		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
 	}
 
 	public int determineTableNumberForColumn(String columnName) {
 		return 0;
 	}
 
 	/**
 	 * Consolidated these onto a single helper because the 2 pieces work in tandem.
 	 */
 	public static interface CacheEntryHelper {
 		public CacheEntryStructure getCacheEntryStructure();
 
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 	}
 
 	private static class StandardCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private StandardCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class ReferenceCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private ReferenceCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new ReferenceCacheEntryImpl( entity, persister.getEntityName() );
 		}
 	}
 
 	private static class StructuredCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 		private final StructuredCacheEntry structure;
 
 		private StructuredCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 			this.structure = new StructuredCacheEntry( persister );
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return structure;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class NoopCacheEntryHelper implements CacheEntryHelper {
 		public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			throw new HibernateException( "Illegal attempt to build cache entry for non-cached entity" );
 		}
 	}
+
+
+	// EntityDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes;
+	private Iterable<AttributeDefinition> attributeDefinitions;
+
+	protected void generateEntityDefinition() {
+		collectEmbeddedCompositeIdentifierAttributeDefinitions();
+		collectAttributeDefinitions();
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return this;
+	}
+
+	@Override
+	public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes() {
+		return embeddedCompositeIdentifierAttributes;
+	}
+
+	@Override
+	public Iterable<AttributeDefinition> getAttributes() {
+		return attributeDefinitions;
+	}
+
+	private synchronized void collectEmbeddedCompositeIdentifierAttributeDefinitions() {
+		final Type idType = getIdentifierType();
+		if ( !idType.isComponentType() ) {
+			return;
+		}
+
+		final CompositeType cidType = (CompositeType) idType;
+		if ( !cidType.isEmbedded() ) {
+			return;
+		}
+
+		// we have an embedded composite identifier.  Most likely we need to process the composite
+		// properties separately, although there is an edge case where the identifier is really
+		// a simple identifier (single value) wrapped in a JPA @IdClass or even in the case of a
+		// a simple identifier (single value) wrapped in a Hibernate composite type.
+		//
+		// We really do not have a built-in method to determine that.  However, generally the
+		// persister would report that there is single, physical identifier property which is
+		// explicitly at odds with the notion of "embedded composite".  So we use that for now
+		if ( getEntityMetamodel().getIdentifierProperty().isEmbedded() ) {
+			this.embeddedCompositeIdentifierAttributes = new Iterable<AttributeDefinition>() {
+				@Override
+				public Iterator<AttributeDefinition> iterator() {
+					return new Iterator<AttributeDefinition>() {
+						private final int numberOfAttributes = countSubclassProperties();
+						private int currentAttributeNumber = 0;
+
+						@Override
+						public boolean hasNext() {
+							return currentAttributeNumber < numberOfAttributes;
+						}
+
+						@Override
+						public AttributeDefinition next() {
+							// todo : implement
+							throw new NotYetImplementedException();
+						}
+
+						@Override
+						public void remove() {
+							throw new UnsupportedOperationException( "Remove operation not supported here" );
+						}
+					};
+				}
+			};
+		}
+	}
+
+	private void collectAttributeDefinitions() {
+		// todo : leverage the attribute definitions housed on EntityMetamodel
+		// 		for that to work, we'd have to be able to walk our super entity persister(s)
+		attributeDefinitions = new Iterable<AttributeDefinition>() {
+			@Override
+			public Iterator<AttributeDefinition> iterator() {
+				return new Iterator<AttributeDefinition>() {
+//					private final int numberOfAttributes = countSubclassProperties();
+					private final int numberOfAttributes = entityMetamodel.getPropertySpan();
+					private int currentAttributeNumber = 0;
+
+					@Override
+					public boolean hasNext() {
+						return currentAttributeNumber < numberOfAttributes;
+					}
+
+					@Override
+					public AttributeDefinition next() {
+						final int attributeNumber = currentAttributeNumber;
+						currentAttributeNumber++;
+						return entityMetamodel.getProperties()[ attributeNumber ];
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
index f95ea71970..46d3fd8bee 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
@@ -1,758 +1,761 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.OptimisticCacheSource;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.metadata.ClassMetadata;
+import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
- * Implementors define mapping and persistence logic for a particular
- * strategy of entity mapping.  An instance of entity persisters corresponds
- * to a given mapped entity.
+ * Contract describing mapping information and persistence logic for a particular strategy of entity mapping.  A given
+ * persister instance corresponds to a given mapped entity class.
  * <p/>
- * Implementors must be threadsafe (preferrably immutable) and must provide a constructor
- * matching the signature of: {@link org.hibernate.mapping.PersistentClass}, {@link org.hibernate.engine.spi.SessionFactoryImplementor}
+ * Implementations must be thread-safe (preferably immutable).
  *
  * @author Gavin King
+ * @author Steve Ebersole
+ *
+ * @see org.hibernate.persister.spi.PersisterFactory
+ * @see org.hibernate.persister.spi.PersisterClassResolver
  */
-public interface EntityPersister extends OptimisticCacheSource {
+public interface EntityPersister extends OptimisticCacheSource, EntityDefinition {
 
 	/**
 	 * The property name of the "special" identifier property in HQL
 	 */
 	public static final String ENTITY_ID = "id";
 
 	/**
 	 * Finish the initialization of this object.
 	 * <p/>
 	 * Called only once per {@link org.hibernate.SessionFactory} lifecycle,
 	 * after all entity persisters have been instantiated.
 	 *
 	 * @throws org.hibernate.MappingException Indicates an issue in the metadata.
 	 */
 	public void postInstantiate() throws MappingException;
 
 	/**
 	 * Return the SessionFactory to which this persister "belongs".
 	 *
 	 * @return The owning SessionFactory.
 	 */
 	public SessionFactoryImplementor getFactory();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     // stuff that is persister-centric and/or EntityInfo-centric ~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns an object that identifies the space in which identifiers of
 	 * this entity hierarchy are unique.  Might be a table name, a JNDI URL, etc.
 	 *
 	 * @return The root entity name.
 	 */
 	public String getRootEntityName();
 
 	/**
 	 * The entity name which this persister maps.
 	 *
 	 * @return The name of the entity which this persister maps.
 	 */
 	public String getEntityName();
 
 	/**
 	 * Retrieve the underlying entity metamodel instance...
 	 *
 	 *@return The metamodel
 	 */
 	public EntityMetamodel getEntityMetamodel();
 
 	/**
 	 * Determine whether the given name represents a subclass entity
 	 * (or this entity itself) of the entity mapped by this persister.
 	 *
 	 * @param entityName The entity name to be checked.
 	 * @return True if the given entity name represents either the entity
 	 * mapped by this persister or one of its subclass entities; false
 	 * otherwise.
 	 */
 	public boolean isSubclassEntityName(String entityName);
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class only.
 	 * <p/>
 	 * For most implementations, this returns the complete set of table names
 	 * to which instances of the mapped entity are persisted (not accounting
 	 * for superclass entity mappings).
 	 *
 	 * @return The property spaces.
 	 */
 	public Serializable[] getPropertySpaces();
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class and its subclasses.
 	 * <p/>
 	 * Much like {@link #getPropertySpaces()}, except that here we include subclass
 	 * entity spaces.
 	 *
 	 * @return The query spaces.
 	 */
 	public Serializable[] getQuerySpaces();
 
 	/**
 	 * Determine whether this entity supports dynamic proxies.
 	 *
 	 * @return True if the entity has dynamic proxy support; false otherwise.
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections.
 	 *
 	 * @return True if the entity does contain persistent collections; false otherwise.
 	 */
 	public boolean hasCollections();
 
 	/**
 	 * Determine whether any properties of this entity are considered mutable.
 	 *
 	 * @return True if any properties of the entity are mutable; false otherwise (meaning none are).
 	 */
 	public boolean hasMutableProperties();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections
 	 * which are fetchable by subselect?
 	 *
 	 * @return True if the entity contains collections fetchable by subselect; false otherwise.
 	 */
 	public boolean hasSubselectLoadableCollections();
 
 	/**
 	 * Determine whether this entity has any non-none cascading.
 	 *
 	 * @return True if the entity has any properties with a cascade other than NONE;
 	 * false otherwise (aka, no cascading).
 	 */
 	public boolean hasCascades();
 
 	/**
 	 * Determine whether instances of this entity are considered mutable.
 	 *
 	 * @return True if the entity is considered mutable; false otherwise.
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Determine whether the entity is inherited one or more other entities.
 	 * In other words, is this entity a subclass of other entities.
 	 *
 	 * @return True if other entities extend this entity; false otherwise.
 	 */
 	public boolean isInherited();
 
 	/**
 	 * Are identifiers of this entity assigned known before the insert execution?
 	 * Or, are they generated (in the database) by the insert execution.
 	 *
 	 * @return True if identifiers for this entity are generated by the insert
 	 * execution.
 	 */
 	public boolean isIdentifierAssignedByInsert();
 
 	/**
 	 * Get the type of a particular property by name.
 	 *
 	 * @param propertyName The name of the property for which to retrieve
 	 * the type.
 	 * @return The type.
 	 * @throws org.hibernate.MappingException Typically indicates an unknown
 	 * property name.
 	 */
 	public Type getPropertyType(String propertyName) throws MappingException;
 
 	/**
 	 * Compare the two snapshots to determine if they represent dirty state.
 	 *
 	 * @param currentState The current snapshot
 	 * @param previousState The baseline snapshot
 	 * @param owner The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all dirty properties, or null if no properties
 	 * were dirty.
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session);
 
 	/**
 	 * Compare the two snapshots to determine if they represent modified state.
 	 *
 	 * @param old The baseline snapshot
 	 * @param current The current snapshot
 	 * @param object The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all modified properties, or null if no properties
 	 * were modified.
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session);
 
 	/**
 	 * Determine whether the entity has a particular property holding
 	 * the identifier value.
 	 *
 	 * @return True if the entity has a specific property holding identifier value.
 	 */
 	public boolean hasIdentifierProperty();
 
 	/**
 	 * Determine whether detached instances of this entity carry their own
 	 * identifier value.
 	 * <p/>
 	 * The other option is the deprecated feature where users could supply
 	 * the id during session calls.
 	 *
 	 * @return True if either (1) {@link #hasIdentifierProperty()} or
 	 * (2) the identifier is an embedded composite identifier; false otherwise.
 	 */
 	public boolean canExtractIdOutOfEntity();
 
 	/**
 	 * Determine whether optimistic locking by column is enabled for this
 	 * entity.
 	 *
 	 * @return True if optimistic locking by column (i.e., <version/> or
 	 * <timestamp/>) is enabled; false otherwise.
 	 */
 	public boolean isVersioned();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the type of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or null, if not versioned.
 	 */
 	public VersionType getVersionType();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the index of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or -66, if not versioned.
 	 */
 	public int getVersionProperty();
 
 	/**
 	 * Determine whether this entity defines a natural identifier.
 	 *
 	 * @return True if the entity defines a natural id; false otherwise.
 	 */
 	public boolean hasNaturalIdentifier();
 
 	/**
 	 * If the entity defines a natural id ({@link #hasNaturalIdentifier()}), which
 	 * properties make up the natural id.
 	 *
 	 * @return The indices of the properties making of the natural id; or
 	 * null, if no natural id is defined.
 	 */
 	public int[] getNaturalIdentifierProperties();
 
 	/**
 	 * Retrieve the current state of the natural-id properties from the database.
 	 *
 	 * @param id The identifier of the entity for which to retrieve the natural-id values.
 	 * @param session The session from which the request originated.
 	 * @return The natural-id snapshot.
 	 */
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session);
 
 	/**
 	 * Determine which identifier generation strategy is used for this entity.
 	 *
 	 * @return The identifier generation strategy.
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 
 	/**
 	 * Determine whether this entity defines any lazy properties (ala
 	 * bytecode instrumentation).
 	 *
 	 * @return True if the entity has properties mapped as lazy; false otherwise.
 	 */
 	public boolean hasLazyProperties();
 
 	/**
 	 * Load the id for the entity based on the natural id.
 	 */
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session);
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance
 	 */
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance, using a natively generated identifier (optional operation)
 	 */
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Delete a persistent instance
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Update a persistent instance
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException;
 
 	/**
 	 * Get the Hibernate types of the class properties
 	 */
 	public Type[] getPropertyTypes();
 
 	/**
 	 * Get the names of the class properties - doesn't have to be the names of the
 	 * actual Java properties (used for XML generation only)
 	 */
 	public String[] getPropertyNames();
 
 	/**
 	 * Get the "insertability" of the properties of this class
 	 * (does the property appear in an SQL INSERT)
 	 */
 	public boolean[] getPropertyInsertability();
 
 	/**
 	 * Which of the properties of this class are database generated values on insert?
 	 */
 	public ValueInclusion[] getPropertyInsertGenerationInclusions();
 
 	/**
 	 * Which of the properties of this class are database generated values on update?
 	 */
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions();
 
 	/**
 	 * Get the "updateability" of the properties of this class
 	 * (does the property appear in an SQL UPDATE)
 	 */
 	public boolean[] getPropertyUpdateability();
 
 	/**
 	 * Get the "checkability" of the properties of this class
 	 * (is the property dirty checked, does the cache need
 	 * to be updated)
 	 */
 	public boolean[] getPropertyCheckability();
 
 	/**
 	 * Get the nullability of the properties of this class
 	 */
 	public boolean[] getPropertyNullability();
 
 	/**
 	 * Get the "versionability" of the properties of this class
 	 * (is the property optimistic-locked)
 	 */
 	public boolean[] getPropertyVersionability();
 	public boolean[] getPropertyLaziness();
 	/**
 	 * Get the cascade styles of the properties (optional operation)
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles();
 
 	/**
 	 * Get the identifier type
 	 */
 	public Type getIdentifierType();
 
 	/**
 	 * Get the name of the identifier property (or return null) - need not return the
 	 * name of an actual Java property
 	 */
 	public String getIdentifierPropertyName();
 
 	/**
 	 * Should we always invalidate the cache instead of
 	 * recaching updated state
 	 */
 	public boolean isCacheInvalidationRequired();
 	/**
 	 * Should lazy properties of this entity be cached?
 	 */
 	public boolean isLazyPropertiesCacheable();
 	/**
 	 * Does this class have a cache.
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache (optional operation)
 	 */
 	public EntityRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 
 	/**
 	 * Does this class have a natural id cache
 	 */
 	public boolean hasNaturalIdCache();
 	
 	/**
 	 * Get the NaturalId cache (optional operation)
 	 */
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy();
 
 	/**
 	 * Get the user-visible metadata for the class (optional operation)
 	 */
 	public ClassMetadata getClassMetadata();
 
 	/**
 	 * Is batch loading enabled?
 	 */
 	public boolean isBatchLoadable();
 
 	/**
 	 * Is select snapshot before update enabled?
 	 */
 	public boolean isSelectBeforeUpdateRequired();
 
 	/**
 	 * Get the current database state of the object, in a "hydrated" form, without
 	 * resolving identifiers
 	 * @return null if there is no row in the database
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session);
 
 	/**
 	 * Get the current version of the object, or return null if there is no row for
 	 * the given identifier. In the case of unversioned data, return any object
 	 * if the row exists.
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Has the class actually been bytecode instrumented?
 	 */
 	public boolean isInstrumented();
 
 	/**
 	 * Does this entity define any properties as being database generated on insert?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasInsertGeneratedProperties();
 
 	/**
 	 * Does this entity define any properties as being database generated on update?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasUpdateGeneratedProperties();
 
 	/**
 	 * Does this entity contain a version property that is defined
 	 * to be database generated?
 	 *
 	 * @return true if this entity contains a version property and that
 	 * property has been marked as generated.
 	 */
 	public boolean isVersionPropertyGenerated();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is tuplizer-centric, but is passed a session ~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Called just after the entities properties have been initialized
 	 */
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
 
 	/**
 	 * Called just after the entity has been reassociated with the session
 	 */
 	public void afterReassociate(Object entity, SessionImplementor session);
 
 	/**
 	 * Create a new proxy instance
 	 */
 	public Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Is this a new transient instance?
 	 */
 	public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Return the values of the insertable properties of the object (including backrefs)
 	 */
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is Tuplizer-centric ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The persistent class, or null
 	 */
 	public Class getMappedClass();
 
 	/**
 	 * Does the class implement the {@link org.hibernate.classic.Lifecycle} interface.
 	 */
 	public boolean implementsLifecycle();
 
 	/**
 	 * Get the proxy interface that instances of <em>this</em> concrete class will be
 	 * cast to (optional operation).
 	 */
 	public Class getConcreteProxyClass();
 
 	/**
 	 * Set the given values to the mapped properties of the given object
 	 */
 	public void setPropertyValues(Object object, Object[] values);
 
 	/**
 	 * Set the value of a particular property
 	 */
 	public void setPropertyValue(Object object, int i, Object value);
 
 	/**
 	 * Return the (loaded) values of the mapped properties of the object (not including backrefs)
 	 */
 	public Object[] getPropertyValues(Object object);
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, int i) throws HibernateException;
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, String propertyName);
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	public Serializable getIdentifier(Object object) throws HibernateException;
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @param entity The entity for which to get the identifier
 	 * @param session The session from which the request originated
 	 *
 	 * @return The identifier
 	 */
 	public Serializable getIdentifier(Object entity, SessionImplementor session);
 
     /**
      * Inject the identifier value into the given entity.
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 * @param session The session from which is requests originates
      */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
 
 	/**
 	 * Get the version number (or timestamp) from the object's version property (or return null if not versioned)
 	 */
 	public Object getVersion(Object object) throws HibernateException;
 
 	/**
 	 * Create a class instance initialized with the given identifier
 	 *
 	 * @param id The identifier value to use (may be null to represent no value)
 	 * @param session The session from which the request originated.
 	 *
 	 * @return The instantiated entity.
 	 */
 	public Object instantiate(Serializable id, SessionImplementor session);
 
 	/**
 	 * Is the given object an instance of this entity?
 	 */
 	public boolean isInstance(Object object);
 
 	/**
 	 * Does the given instance have any uninitialized lazy properties?
 	 */
 	public boolean hasUninitializedLazyProperties(Object object);
 
 	/**
 	 * Set the identifier and version of the given instance back to its "unsaved" value.
 	 *
 	 * @param entity The entity instance
 	 * @param currentId The currently assigned identifier value.
 	 * @param currentVersion The currently assigned version value.
 	 * @param session The session from which the request originated.
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
 
 	/**
 	 * A request has already identified the entity-name of this persister as the mapping for the given instance.
 	 * However, we still need to account for possible subclassing and potentially re-route to the more appropriate
 	 * persister.
 	 * <p/>
 	 * For example, a request names <tt>Animal</tt> as the entity-name which gets resolved to this persister.  But the
 	 * actual instance is really an instance of <tt>Cat</tt> which is a subclass of <tt>Animal</tt>.  So, here the
 	 * <tt>Animal</tt> persister is being asked to return the persister specific to <tt>Cat</tt>.
 	 * <p/>
 	 * It is also possible that the instance is actually an <tt>Animal</tt> instance in the above example in which
 	 * case we would return <tt>this</tt> from this method.
 	 *
 	 * @param instance The entity instance
 	 * @param factory Reference to the SessionFactory
 	 *
 	 * @return The appropriate persister
 	 *
 	 * @throws HibernateException Indicates that instance was deemed to not be a subclass of the entity mapped by
 	 * this persister.
 	 */
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory);
 
 	public EntityMode getEntityMode();
 	public EntityTuplizer getEntityTuplizer();
 
 	public EntityInstrumentationMetadata getInstrumentationMetadata();
 	
 	public FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/Helper.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/Helper.java
new file mode 100644
index 0000000000..29eb9f5dbe
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/Helper.java
@@ -0,0 +1,160 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.persister.walking.internal;
+
+import java.util.Iterator;
+
+import org.hibernate.FetchMode;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.profile.Fetch;
+import org.hibernate.engine.profile.FetchProfile;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.AbstractCollectionPersister;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.type.AssociationType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class Helper {
+	/**
+	 * Determine the fetch-style (if one) explicitly set for this association via fetch profiles.
+	 * <p/>
+	 * Note that currently fetch profiles only allow specifying join fetching, so this method currently
+	 * returns either (a) FetchStyle.JOIN or (b) null
+	 *
+	 * @param loadQueryInfluencers
+	 * @param persister
+	 * @param path
+	 * @param propertyNumber
+	 *
+	 * @return
+	 */
+	public static FetchStyle determineFetchStyleByProfile(
+			LoadQueryInfluencers loadQueryInfluencers,
+			EntityPersister persister,
+			PropertyPath path,
+			int propertyNumber) {
+		if ( !loadQueryInfluencers.hasEnabledFetchProfiles() ) {
+			// perf optimization
+			return null;
+		}
+
+		// ugh, this stuff has to be made easier...
+		final String fullPath = path.getFullPath();
+		final String rootPropertyName = ( (OuterJoinLoadable) persister ).getSubclassPropertyName( propertyNumber );
+		int pos = fullPath.lastIndexOf( rootPropertyName );
+		final String relativePropertyPath = pos >= 0
+				? fullPath.substring( pos )
+				: rootPropertyName;
+		final String fetchRole = persister.getEntityName() + "." + relativePropertyPath;
+
+		Iterator profiles = loadQueryInfluencers.getEnabledFetchProfileNames().iterator();
+		while ( profiles.hasNext() ) {
+			final String profileName = ( String ) profiles.next();
+			final FetchProfile profile = loadQueryInfluencers.getSessionFactory().getFetchProfile( profileName );
+			final Fetch fetch = profile.getFetchByRole( fetchRole );
+			if ( fetch != null && Fetch.Style.JOIN == fetch.getStyle() ) {
+				return FetchStyle.JOIN;
+			}
+		}
+		return null;
+	}
+
+	/**
+	 *
+	 * @param mappingFetchMode The mapping defined fetch mode
+	 * @param type The association type
+	 * @param sessionFactory The session factory
+	 *
+	 * @return
+	 */
+	public static FetchStyle determineFetchStyleByMetadata(
+			FetchMode mappingFetchMode,
+			AssociationType type,
+			SessionFactoryImplementor sessionFactory) {
+		if ( !type.isEntityType() && !type.isCollectionType() ) {
+			return FetchStyle.SELECT;
+		}
+
+		if ( mappingFetchMode == FetchMode.JOIN ) {
+			return FetchStyle.JOIN;
+		}
+
+		if ( type.isEntityType() ) {
+			EntityPersister persister = (EntityPersister) type.getAssociatedJoinable( sessionFactory );
+			if ( persister.isBatchLoadable() ) {
+				return FetchStyle.BATCH;
+			}
+		}
+		else {
+			CollectionPersister persister = (CollectionPersister) type.getAssociatedJoinable( sessionFactory );
+			if ( persister instanceof AbstractCollectionPersister
+					&& ( (AbstractCollectionPersister) persister ).isSubselectLoadable() ) {
+				return FetchStyle.SUBSELECT;
+			}
+			else if ( persister.getBatchSize() > 0 ) {
+				return FetchStyle.BATCH;
+			}
+		}
+
+		return FetchStyle.SELECT;
+	}
+
+	public static FetchTiming determineFetchTiming(
+			FetchStyle style,
+			AssociationType type,
+			SessionFactoryImplementor sessionFactory) {
+		switch ( style ) {
+			case JOIN: {
+				return FetchTiming.IMMEDIATE;
+			}
+			case BATCH:
+			case SUBSELECT: {
+				return FetchTiming.DELAYED;
+			}
+			default: {
+				// SELECT case, can be either
+				return isSubsequentSelectDelayed( type, sessionFactory )
+						? FetchTiming.DELAYED
+						: FetchTiming.IMMEDIATE;
+			}
+		}
+	}
+
+	private static boolean isSubsequentSelectDelayed(AssociationType type, SessionFactoryImplementor sessionFactory) {
+		if ( type.isEntityType() ) {
+			return ( (EntityPersister) type.getAssociatedJoinable( sessionFactory ) ).hasProxy();
+		}
+		else {
+			final CollectionPersister cp = ( (CollectionPersister) type.getAssociatedJoinable( sessionFactory ) );
+			return cp.isLazy() || cp.isExtraLazy();
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationAttributeDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationAttributeDefinition.java
new file mode 100644
index 0000000000..383b3d4e38
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationAttributeDefinition.java
@@ -0,0 +1,46 @@
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
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.loader.PropertyPath;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface AssociationAttributeDefinition extends AttributeDefinition {
+	public AssociationKey getAssociationKey();
+
+	public boolean isCollection();
+
+	public EntityDefinition toEntityDefinition();
+
+	public CollectionDefinition toCollectionDefinition();
+
+	public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath);
+
+	public CascadeStyle determineCascadeStyle();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
new file mode 100644
index 0000000000..352c4a548f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
@@ -0,0 +1,53 @@
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
+import java.util.Arrays;
+
+/**
+ * Used to uniquely identify a foreign key, so that we don't join it more than once creating circularities.
+ * <p/>
+ * bit of a misnomer to call this an association attribute.  But this follows the legacy use of AssociationKey
+ * from old JoinWalkers to denote circular join detection
+ */
+public class AssociationKey {
+	private final String table;
+	private final String[] columns;
+
+	public AssociationKey(String table, String[] columns) {
+		this.table = table;
+		this.columns = columns;
+	}
+
+	@Override
+	public boolean equals(Object other) {
+		AssociationKey that = (AssociationKey) other;
+		return that.table.equals(table) && Arrays.equals( columns, that.columns );
+	}
+
+	@Override
+	public int hashCode() {
+		return table.hashCode(); //TODO: inefficient
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
new file mode 100644
index 0000000000..1cb649b0d8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
@@ -0,0 +1,51 @@
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
+ * @author Steve Ebersole
+ */
+public interface AssociationVisitationStrategy {
+	/**
+	 * Notification we are preparing to start visitation.
+	 */
+	public void start();
+
+	/**
+	 * Notification we are finished visitation.
+	 */
+	public void finish();
+
+	public void startingEntity(EntityDefinition entityDefinition);
+	public void finishingEntity(EntityDefinition entityDefinition);
+
+	public void startingCollection(CollectionDefinition collectionDefinition);
+	public void finishingCollection(CollectionDefinition collectionDefinition);
+
+	public void startingComposite(CompositeDefinition compositeDefinition);
+	public void finishingComposite(CompositeDefinition compositeDefinition);
+
+	public boolean startingAttribute(AttributeDefinition attributeDefinition);
+	public void finishingAttribute(AttributeDefinition attributeDefinition);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AttributeDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AttributeDefinition.java
new file mode 100644
index 0000000000..ed58612671
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AttributeDefinition.java
@@ -0,0 +1,35 @@
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
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface AttributeDefinition {
+	public String getName();
+	public Type getType();
+	public AttributeSource getSource();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AttributeSource.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AttributeSource.java
new file mode 100644
index 0000000000..a94dfb1987
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AttributeSource.java
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
+* @author Steve Ebersole
+*/
+public interface AttributeSource {
+	public Iterable<AttributeDefinition> getAttributes();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionDefinition.java
new file mode 100644
index 0000000000..f7e2fb6ad7
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionDefinition.java
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
+package org.hibernate.persister.walking.spi;
+
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.type.CollectionType;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface CollectionDefinition {
+	public CollectionPersister getCollectionPersister();
+	public CollectionType getCollectionType();
+
+	public CollectionIndexDefinition getIndexDefinition();
+	public CollectionElementDefinition getElementDefinition();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
new file mode 100644
index 0000000000..fd48366c3a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
@@ -0,0 +1,39 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface CollectionElementDefinition {
+	public CollectionDefinition getCollectionDefinition();
+
+	public Type getType();
+
+	public EntityDefinition toEntityDefinition();
+
+	public CompositeDefinition toCompositeDefinition();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
new file mode 100644
index 0000000000..700e2b5070
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
@@ -0,0 +1,39 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface CollectionIndexDefinition {
+	public CollectionDefinition getCollectionDefinition();
+
+	public Type getType();
+
+	public EntityDefinition toEntityDefinition();
+
+	public CompositeDefinition toCompositeDefinition();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeDefinition.java
new file mode 100644
index 0000000000..51c88333f4
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeDefinition.java
@@ -0,0 +1,30 @@
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
+ * @author Steve Ebersole
+ */
+public interface CompositeDefinition extends AttributeDefinition, AttributeSource {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityDefinition.java
new file mode 100644
index 0000000000..1e02b42106
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityDefinition.java
@@ -0,0 +1,36 @@
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
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * Defines the contract for walking the attributes defined by an entity
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityDefinition extends AttributeSource {
+	public EntityPersister getEntityPersister();
+	public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
new file mode 100644
index 0000000000..ccbbceb6bb
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
@@ -0,0 +1,208 @@
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
+import java.util.HashSet;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.Type;
+
+/**
+ * Provides model graph visitation based on the defined metadata (as opposed to based on the incoming graph
+ * as we see in cascade processing).  In layman terms, we are walking the graph of the users model as defined by
+ * mapped associations.
+ * <p/>
+ * Re-implementation of the legacy {@link org.hibernate.loader.JoinWalker} contract to leverage load plans.
+ *
+ * @author Steve Ebersole
+ */
+public class MetadataDrivenModelGraphVisitor {
+	private static final Logger log = Logger.getLogger( MetadataDrivenModelGraphVisitor.class );
+
+	public static void visitEntity(AssociationVisitationStrategy strategy, EntityPersister persister) {
+		strategy.start();
+		try {
+			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
+					.visitEntityDefinition( persister );
+		}
+		finally {
+			strategy.finish();
+		}
+	}
+
+	public static void visitCollection(AssociationVisitationStrategy strategy, CollectionPersister persister) {
+		strategy.start();
+		try {
+			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
+					.visitCollectionDefinition( persister );
+		}
+		finally {
+			strategy.finish();
+		}
+	}
+
+	private final AssociationVisitationStrategy strategy;
+	private final SessionFactoryImplementor factory;
+
+	// todo : add a getDepth() method to PropertyPath
+	private PropertyPath currentPropertyPath = new PropertyPath();
+
+	public MetadataDrivenModelGraphVisitor(AssociationVisitationStrategy strategy, SessionFactoryImplementor factory) {
+		this.strategy = strategy;
+		this.factory = factory;
+	}
+
+	private void visitEntityDefinition(EntityDefinition entityDefinition) {
+		strategy.startingEntity( entityDefinition );
+		try {
+			visitAttributes( entityDefinition );
+			optionallyVisitEmbeddedCompositeIdentifier( entityDefinition );
+		}
+		finally {
+			strategy.finishingEntity( entityDefinition );
+		}
+	}
+
+	private void optionallyVisitEmbeddedCompositeIdentifier(EntityDefinition entityDefinition) {
+		// if the entity has a composite identifier, see if we need to handle its sub-properties separately
+		final Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes =
+				entityDefinition.getEmbeddedCompositeIdentifierAttributes();
+		if ( embeddedCompositeIdentifierAttributes == null ) {
+			return;
+		}
+
+		for ( AttributeDefinition attributeDefinition : embeddedCompositeIdentifierAttributes ) {
+			visitAttributeDefinition( attributeDefinition );
+		}
+	}
+
+	private void visitAttributes(AttributeSource attributeSource) {
+		for ( AttributeDefinition attributeDefinition : attributeSource.getAttributes() ) {
+			visitAttributeDefinition( attributeDefinition );
+		}
+	}
+
+	private void visitAttributeDefinition(AttributeDefinition attributeDefinition) {
+		final PropertyPath subPath = currentPropertyPath.append( attributeDefinition.getName() );
+		log.debug( "Visiting attribute path : " + subPath.getFullPath() );
+
+		final boolean continueWalk = strategy.startingAttribute( attributeDefinition );
+		if ( continueWalk ) {
+			final PropertyPath old = currentPropertyPath;
+			currentPropertyPath = subPath;
+			try {
+				if ( attributeDefinition.getType().isAssociationType() ) {
+					visitAssociation( (AssociationAttributeDefinition) attributeDefinition );
+				}
+				else if ( attributeDefinition.getType().isComponentType() ) {
+					visitCompositeDefinition( (CompositeDefinition) attributeDefinition );
+				}
+			}
+			finally {
+				currentPropertyPath = old;
+			}
+		}
+		strategy.finishingAttribute( attributeDefinition );
+	}
+
+	private void visitAssociation(AssociationAttributeDefinition attribute) {
+		// todo : do "too deep" checks; but see note about adding depth to PropertyPath
+
+		if ( isDuplicateAssociation( attribute.getAssociationKey() ) ) {
+			log.debug( "Property path deemed to be circular : " + currentPropertyPath.getFullPath() );
+			return;
+		}
+
+		if ( attribute.isCollection() ) {
+			visitCollectionDefinition( attribute.toCollectionDefinition() );
+		}
+		else {
+			visitEntityDefinition( attribute.toEntityDefinition() );
+		}
+	}
+
+	private void visitCompositeDefinition(CompositeDefinition compositeDefinition) {
+		strategy.startingComposite( compositeDefinition );
+		try {
+			visitAttributes( compositeDefinition );
+		}
+		finally {
+			strategy.finishingComposite( compositeDefinition );
+		}
+	}
+
+	private void visitCollectionDefinition(CollectionDefinition collectionDefinition) {
+		strategy.startingCollection( collectionDefinition );
+
+		try {
+			visitCollectionIndex( collectionDefinition.getIndexDefinition() );
+
+			final CollectionElementDefinition elementDefinition = collectionDefinition.getElementDefinition();
+			if ( elementDefinition.getType().isComponentType() ) {
+				visitCompositeDefinition( elementDefinition.toCompositeDefinition() );
+			}
+			else {
+				visitEntityDefinition( elementDefinition.toEntityDefinition() );
+			}
+		}
+		finally {
+			strategy.finishingCollection( collectionDefinition );
+		}
+	}
+
+	private void visitCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+		if ( collectionIndexDefinition == null ) {
+			return;
+		}
+
+		log.debug( "Visiting collection index :  " + currentPropertyPath.getFullPath() );
+		currentPropertyPath = currentPropertyPath.append( "<key>" );
+		try {
+			final Type collectionIndexType = collectionIndexDefinition.getType();
+			if ( collectionIndexType.isComponentType() ) {
+				visitCompositeDefinition( collectionIndexDefinition.toCompositeDefinition() );
+			}
+			else if ( collectionIndexType.isAssociationType() ) {
+				visitEntityDefinition( collectionIndexDefinition.toEntityDefinition() );
+			}
+		}
+		finally {
+			currentPropertyPath = currentPropertyPath.getParent();
+		}
+	}
+
+
+	private final Set<AssociationKey> visitedAssociationKeys = new HashSet<AssociationKey>();
+
+	protected boolean isDuplicateAssociation(AssociationKey associationKey) {
+		return !visitedAssociationKeys.add( associationKey );
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/package-info.java
new file mode 100644
index 0000000000..90d0cd8faf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/package-info.java
@@ -0,0 +1,6 @@
+package org.hibernate.persister.walking.spi;
+
+/**
+ * Package for "walking" associations through metadata definition.  Ultimately want {@link org.hibernate.persister.walking.spi.AttributeDefinition} and
+ * {@link AttributeSource} etc to become part of the persister model.
+ */
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/AbstractAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/AbstractAttribute.java
new file mode 100644
index 0000000000..4818c33c22
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/AbstractAttribute.java
@@ -0,0 +1,55 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple;
+
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractAttribute implements Attribute, Property {
+	private final String attributeName;
+	private final Type attributeType;
+
+	protected AbstractAttribute(String attributeName, Type attributeType) {
+		this.attributeName = attributeName;
+		this.attributeType = attributeType;
+	}
+
+	@Override
+	@Deprecated
+	public String getNode() {
+		return null;
+	}
+
+	@Override
+	public String getName() {
+		return attributeName;
+	}
+
+	@Override
+	public Type getType() {
+		return attributeType;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/AbstractNonIdentifierAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/AbstractNonIdentifierAttribute.java
new file mode 100644
index 0000000000..b01458fe64
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/AbstractNonIdentifierAttribute.java
@@ -0,0 +1,133 @@
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
+package org.hibernate.tuple;
+
+import org.hibernate.FetchMode;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.walking.spi.AttributeSource;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractNonIdentifierAttribute extends AbstractAttribute implements NonIdentifierAttribute {
+	private final AttributeSource source;
+	private final SessionFactoryImplementor sessionFactory;
+
+	private final int attributeNumber;
+
+	private final BaselineAttributeInformation attributeInformation;
+
+	protected AbstractNonIdentifierAttribute(
+			AttributeSource source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			Type attributeType,
+			BaselineAttributeInformation attributeInformation) {
+		super( attributeName, attributeType );
+		this.source = source;
+		this.sessionFactory = sessionFactory;
+		this.attributeNumber = attributeNumber;
+		this.attributeInformation = attributeInformation;
+	}
+
+	@Override
+	public AttributeSource getSource() {
+		return source();
+	}
+
+	protected AttributeSource source() {
+		return source;
+	}
+
+	protected SessionFactoryImplementor sessionFactory() {
+		return sessionFactory;
+	}
+
+	protected int attributeNumber() {
+		return attributeNumber;
+	}
+
+	@Override
+	public boolean isLazy() {
+		return attributeInformation.isLazy();
+	}
+
+	@Override
+	public boolean isInsertable() {
+		return attributeInformation.isInsertable();
+	}
+
+	@Override
+	public boolean isUpdateable() {
+		return attributeInformation.isUpdateable();
+	}
+
+	@Override
+	public boolean isInsertGenerated() {
+		return attributeInformation.isInsertGenerated();
+	}
+
+	@Override
+	public boolean isUpdateGenerated() {
+		return attributeInformation.isUpdateGenerated();
+	}
+
+	@Override
+	public boolean isNullable() {
+		return attributeInformation.isNullable();
+	}
+
+	@Override
+	public boolean isDirtyCheckable() {
+		return attributeInformation.isDirtyCheckable();
+	}
+
+	@Override
+	public boolean isDirtyCheckable(boolean hasUninitializedProperties) {
+		return isDirtyCheckable() && ( !hasUninitializedProperties || !isLazy() );
+	}
+
+	@Override
+	public boolean isVersionable() {
+		return attributeInformation.isVersionable();
+	}
+
+	@Override
+	public CascadeStyle getCascadeStyle() {
+		return attributeInformation.getCascadeStyle();
+	}
+
+	@Override
+	public FetchMode getFetchMode() {
+		return attributeInformation.getFetchMode();
+	}
+
+	@Override
+	public String toString() {
+		return "Attribute[non-identifier]( " + getName() + ")";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/Attribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/Attribute.java
new file mode 100644
index 0000000000..08e7496204
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/Attribute.java
@@ -0,0 +1,37 @@
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
+package org.hibernate.tuple;
+
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.type.Type;
+
+/**
+ * Contract for attributes
+ *
+ * @author Steve Ebersole
+ */
+public interface Attribute {
+	public String getName();
+	public Type getType();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/BaselineAttributeInformation.java b/hibernate-core/src/main/java/org/hibernate/tuple/BaselineAttributeInformation.java
new file mode 100644
index 0000000000..920231b0d9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/BaselineAttributeInformation.java
@@ -0,0 +1,166 @@
+package org.hibernate.tuple;
+
+import org.hibernate.FetchMode;
+import org.hibernate.engine.spi.CascadeStyle;
+
+/**
+* @author Steve Ebersole
+*/
+public class BaselineAttributeInformation {
+	private final boolean lazy;
+	private final boolean insertable;
+	private final boolean updateable;
+	private final boolean insertGenerated;
+	private final boolean updateGenerated;
+	private final boolean nullable;
+	private final boolean dirtyCheckable;
+	private final boolean versionable;
+	private final CascadeStyle cascadeStyle;
+	private final FetchMode fetchMode;
+	private boolean checkable;
+
+	public BaselineAttributeInformation(
+			boolean lazy,
+			boolean insertable,
+			boolean updateable,
+			boolean insertGenerated,
+			boolean updateGenerated,
+			boolean nullable,
+			boolean dirtyCheckable,
+			boolean versionable,
+			CascadeStyle cascadeStyle,
+			FetchMode fetchMode) {
+		this.lazy = lazy;
+		this.insertable = insertable;
+		this.updateable = updateable;
+		this.insertGenerated = insertGenerated;
+		this.updateGenerated = updateGenerated;
+		this.nullable = nullable;
+		this.dirtyCheckable = dirtyCheckable;
+		this.versionable = versionable;
+		this.cascadeStyle = cascadeStyle;
+		this.fetchMode = fetchMode;
+	}
+
+	public boolean isLazy() {
+		return lazy;
+	}
+
+	public boolean isInsertable() {
+		return insertable;
+	}
+
+	public boolean isUpdateable() {
+		return updateable;
+	}
+
+	public boolean isInsertGenerated() {
+		return insertGenerated;
+	}
+
+	public boolean isUpdateGenerated() {
+		return updateGenerated;
+	}
+
+	public boolean isNullable() {
+		return nullable;
+	}
+
+	public boolean isDirtyCheckable() {
+		return dirtyCheckable;
+	}
+
+	public boolean isVersionable() {
+		return versionable;
+	}
+
+	public CascadeStyle getCascadeStyle() {
+		return cascadeStyle;
+	}
+
+	public FetchMode getFetchMode() {
+		return fetchMode;
+	}
+
+	public boolean isCheckable() {
+		return checkable;
+	}
+
+	public static class Builder {
+		private boolean lazy;
+		private boolean insertable;
+		private boolean updateable;
+		private boolean insertGenerated;
+		private boolean updateGenerated;
+		private boolean nullable;
+		private boolean dirtyCheckable;
+		private boolean versionable;
+		private CascadeStyle cascadeStyle;
+		private FetchMode fetchMode;
+
+		public Builder setLazy(boolean lazy) {
+			this.lazy = lazy;
+			return this;
+		}
+
+		public Builder setInsertable(boolean insertable) {
+			this.insertable = insertable;
+			return this;
+		}
+
+		public Builder setUpdateable(boolean updateable) {
+			this.updateable = updateable;
+			return this;
+		}
+
+		public Builder setInsertGenerated(boolean insertGenerated) {
+			this.insertGenerated = insertGenerated;
+			return this;
+		}
+
+		public Builder setUpdateGenerated(boolean updateGenerated) {
+			this.updateGenerated = updateGenerated;
+			return this;
+		}
+
+		public Builder setNullable(boolean nullable) {
+			this.nullable = nullable;
+			return this;
+		}
+
+		public Builder setDirtyCheckable(boolean dirtyCheckable) {
+			this.dirtyCheckable = dirtyCheckable;
+			return this;
+		}
+
+		public Builder setVersionable(boolean versionable) {
+			this.versionable = versionable;
+			return this;
+		}
+
+		public Builder setCascadeStyle(CascadeStyle cascadeStyle) {
+			this.cascadeStyle = cascadeStyle;
+			return this;
+		}
+
+		public Builder setFetchMode(FetchMode fetchMode) {
+			this.fetchMode = fetchMode;
+			return this;
+		}
+
+		public BaselineAttributeInformation createInformation() {
+			return new BaselineAttributeInformation(
+					lazy,
+					insertable,
+					updateable,
+					insertGenerated,
+					updateGenerated,
+					nullable,
+					dirtyCheckable,
+					versionable,
+					cascadeStyle,
+					fetchMode
+			);
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierAttribute.java
new file mode 100644
index 0000000000..345ef8f45c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierAttribute.java
@@ -0,0 +1,21 @@
+package org.hibernate.tuple;
+
+import org.hibernate.engine.spi.IdentifierValue;
+import org.hibernate.id.IdentifierGenerator;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface IdentifierAttribute extends Attribute, Property {
+	boolean isVirtual();
+
+	boolean isEmbedded();
+
+	IdentifierValue getUnsavedValue();
+
+	IdentifierGenerator getIdentifierGenerator();
+
+	boolean isIdentifierAssignedByInsert();
+
+	boolean hasIdentifierMapper();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
index ccd05be6ec..54ad0ad32d 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/IdentifierProperty.java
@@ -1,122 +1,133 @@
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
 package org.hibernate.tuple;
+
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.type.Type;
 
 /**
  * Represents a defined entity identifier property within the Hibernate
  * runtime-metamodel.
  *
  * @author Steve Ebersole
  */
-public class IdentifierProperty extends Property {
+public class IdentifierProperty extends AbstractAttribute implements IdentifierAttribute {
 
 	private boolean virtual;
 	private boolean embedded;
 	private IdentifierValue unsavedValue;
 	private IdentifierGenerator identifierGenerator;
 	private boolean identifierAssignedByInsert;
 	private boolean hasIdentifierMapper;
 
 	/**
 	 * Construct a non-virtual identifier property.
 	 *
 	 * @param name The name of the property representing the identifier within
 	 * its owning entity.
 	 * @param node The node name to use for XML-based representation of this
 	 * property.
 	 * @param type The Hibernate Type for the identifier property.
 	 * @param embedded Is this an embedded identifier.
 	 * @param unsavedValue The value which, if found as the value on the identifier
 	 * property, represents new (i.e., un-saved) instances of the owning entity.
 	 * @param identifierGenerator The generator to use for id value generation.
 	 */
 	public IdentifierProperty(
 			String name,
 			String node,
 			Type type,
 			boolean embedded,
 			IdentifierValue unsavedValue,
 			IdentifierGenerator identifierGenerator) {
-		super(name, node, type);
+		super( name, type );
 		this.virtual = false;
 		this.embedded = embedded;
 		this.hasIdentifierMapper = false;
 		this.unsavedValue = unsavedValue;
 		this.identifierGenerator = identifierGenerator;
 		this.identifierAssignedByInsert = identifierGenerator instanceof PostInsertIdentifierGenerator;
 	}
 
 	/**
 	 * Construct a virtual IdentifierProperty.
 	 *
 	 * @param type The Hibernate Type for the identifier property.
 	 * @param embedded Is this an embedded identifier.
 	 * @param unsavedValue The value which, if found as the value on the identifier
 	 * property, represents new (i.e., un-saved) instances of the owning entity.
 	 * @param identifierGenerator The generator to use for id value generation.
 	 */
 	public IdentifierProperty(
 	        Type type,
 	        boolean embedded,
 			boolean hasIdentifierMapper,
 			IdentifierValue unsavedValue,
 	        IdentifierGenerator identifierGenerator) {
-		super(null, null, type);
+		super( null, type );
 		this.virtual = true;
 		this.embedded = embedded;
 		this.hasIdentifierMapper = hasIdentifierMapper;
 		this.unsavedValue = unsavedValue;
 		this.identifierGenerator = identifierGenerator;
 		this.identifierAssignedByInsert = identifierGenerator instanceof PostInsertIdentifierGenerator;
 	}
 
+	@Override
 	public boolean isVirtual() {
 		return virtual;
 	}
 
+	@Override
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
+	@Override
 	public IdentifierValue getUnsavedValue() {
 		return unsavedValue;
 	}
 
+	@Override
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
+	@Override
 	public boolean isIdentifierAssignedByInsert() {
 		return identifierAssignedByInsert;
 	}
 
+	@Override
 	public boolean hasIdentifierMapper() {
 		return hasIdentifierMapper;
 	}
+
+	@Override
+	public String toString() {
+		return "IdentifierAttribute(" + getName() + ")";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/NonIdentifierAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/NonIdentifierAttribute.java
new file mode 100644
index 0000000000..508572de1d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/NonIdentifierAttribute.java
@@ -0,0 +1,55 @@
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
+package org.hibernate.tuple;
+
+import org.hibernate.FetchMode;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface NonIdentifierAttribute extends Attribute, AttributeDefinition {
+	public boolean isLazy();
+
+	public boolean isInsertable();
+
+	public boolean isUpdateable();
+
+	public boolean isInsertGenerated();
+
+	public boolean isUpdateGenerated();
+
+	public boolean isNullable();
+
+	public boolean isDirtyCheckable(boolean hasUninitializedProperties);
+
+	public boolean isDirtyCheckable();
+
+	public boolean isVersionable();
+
+	public CascadeStyle getCascadeStyle();
+
+	public FetchMode getFetchMode();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/Property.java b/hibernate-core/src/main/java/org/hibernate/tuple/Property.java
index b9f04fd924..69ea51cd20 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/Property.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/Property.java
@@ -1,71 +1,35 @@
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
 package org.hibernate.tuple;
-import java.io.Serializable;
-
-import org.hibernate.type.Type;
 
 /**
  * Defines the basic contract of a Property within the runtime metamodel.
  *
  * @author Steve Ebersole
  */
-public abstract class Property implements Serializable {
-	private String name;
-	private String node;
-	private Type type;
-
-	/**
-	 * Constructor for Property instances.
-	 *
-	 * @param name The name by which the property can be referenced within
-	 * its owner.
-	 * @param node The node name to use for XML-based representation of this
-	 * property.
-	 * @param type The Hibernate Type of this property.
-	 */
-	protected Property(String name, String node, Type type) {
-		this.name = name;
-		this.node = node;
-		this.type = type;
-	}
-
-	public String getName() {
-		return name;
-	}
-
-	public String getNode() {
-		return node;
-	}
-
-	public Type getType() {
-		return type;
-	}
-	
-	public String toString() {
-		return "Property(" + name + ':' + type.getName() + ')';
-	}
-
+@Deprecated
+public interface Property extends Attribute {
+	@Deprecated
+	public String getNode();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
index 8c1a51f931..8db4f2048e 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,407 +1,528 @@
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
 package org.hibernate.tuple;
+
 import java.lang.reflect.Constructor;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
+import org.hibernate.HibernateException;
+import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.internal.UnsavedValueFactory;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.IdentifierValue;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.AssociationAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
+import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
+import org.hibernate.tuple.entity.EntityBasedAssociationAttribute;
+import org.hibernate.tuple.entity.EntityBasedBasicAttribute;
+import org.hibernate.tuple.entity.EntityBasedCompositeAttribute;
+import org.hibernate.tuple.entity.VersionProperty;
 import org.hibernate.type.AssociationType;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Responsible for generation of runtime metamodel {@link Property} representations.
  * Makes distinction between identifier, version, and other (standard) properties.
  *
  * @author Steve Ebersole
  */
 public class PropertyFactory {
-
 	/**
-	 * Generates an IdentifierProperty representation of the for a given entity mapping.
+	 * Generates the attribute representation of the identifier for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
-	public static IdentifierProperty buildIdentifierProperty(PersistentClass mappedEntity, IdentifierGenerator generator) {
-
+	public static IdentifierProperty buildIdentifierAttribute(
+			PersistentClass mappedEntity,
+			IdentifierGenerator generator) {
 		String mappedUnsavedValue = mappedEntity.getIdentifier().getNullValue();
 		Type type = mappedEntity.getIdentifier().getType();
 		Property property = mappedEntity.getIdentifierProperty();
 		
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor(mappedEntity)
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					mappedEntity.hasIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getName(),
 					property.getNodeName(),
 					type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
-	public static IdentifierProperty buildIdentifierProperty(EntityBinding mappedEntity, IdentifierGenerator generator) {
+	public static IdentifierProperty buildIdentifierProperty(
+			EntityBinding mappedEntity,
+			IdentifierGenerator generator) {
 
 		final BasicAttributeBinding property = mappedEntity.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 
 		// TODO: the following will cause an NPE with "virtual" IDs; how should they be set?
 		// (steve) virtual attributes will still be attributes, they will simply be marked as virtual.
 		//		see org.hibernate.metamodel.domain.AbstractAttributeContainer.locateOrCreateVirtualAttribute()
 
 		final String mappedUnsavedValue = property.getUnsavedValue();
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor( mappedEntity )
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getAttribute().getName(),
 					null,
 					type,
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
-	public static VersionProperty buildVersionProperty(Property property, boolean lazyAvailable) {
+	public static VersionProperty buildVersionProperty(
+			EntityPersister persister,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			Property property,
+			boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 		
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getType(),
 				getConstructor( property.getPersistentClass() )
-			);
+		);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		return new VersionProperty(
+				persister,
+				sessionFactory,
+				attributeNumber,
 		        property.getName(),
-		        property.getNodeName(),
 		        property.getValue().getType(),
-		        lazy,
-				property.isInsertable(),
-				property.isUpdateable(),
-		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
-				property.getGeneration() == PropertyGeneration.ALWAYS,
-				property.isOptional(),
-				property.isUpdateable() && !lazy,
-				property.isOptimisticLocked(),
-		        property.getCascadeStyle(),
+				new BaselineAttributeInformation.Builder()
+						.setLazy( lazy )
+						.setInsertable( property.isInsertable() )
+						.setUpdateable( property.isUpdateable() )
+						.setInsertGenerated( property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS )
+						.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
+						.setNullable( property.isOptional() )
+						.setDirtyCheckable( property.isUpdateable() && !lazy )
+						.setVersionable( property.isOptimisticLocked() )
+						.setCascadeStyle( property.getCascadeStyle() )
+						.createInformation(),
 		        unsavedValue
 			);
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
-	public static VersionProperty buildVersionProperty(BasicAttributeBinding property, boolean lazyAvailable) {
-		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
-
-		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
-				mappedUnsavedValue,
-				getGetter( property ),
-				(VersionType) property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
-				getConstructor( (EntityBinding) property.getContainer() )
-		);
-
-		boolean lazy = lazyAvailable && property.isLazy();
-
-		final CascadeStyle cascadeStyle = property.isAssociation()
-				? ( (AssociationAttributeBinding) property ).getCascadeStyle()
-				: CascadeStyles.NONE;
+	public static VersionProperty buildVersionProperty(
+			EntityPersister persister,
+			BasicAttributeBinding property,
+			boolean lazyAvailable) {
+		throw new NotYetImplementedException();
+	}
 
-		return new VersionProperty(
-		        property.getAttribute().getName(),
-		        null,
-		        property.getHibernateTypeDescriptor().getResolvedTypeMapping(),
-		        lazy,
-				true, // insertable
-				true, // updatable
-		        property.getGeneration() == PropertyGeneration.INSERT
-						|| property.getGeneration() == PropertyGeneration.ALWAYS,
-				property.getGeneration() == PropertyGeneration.ALWAYS,
-				property.isNullable(),
-				!lazy,
-				property.isIncludedInOptimisticLocking(),
-				cascadeStyle,
-		        unsavedValue
-			);
+	public static enum NonIdentifierAttributeNature {
+		BASIC,
+		COMPOSITE,
+		ANY,
+		ENTITY,
+		COLLECTION
 	}
 
 	/**
-	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
-	 * mapped property.
+	 * Generate a non-identifier (and non-version) attribute based on the given mapped property from the given entity
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
-	 * @return The appropriate StandardProperty definition.
+	 * @return The appropriate NonIdentifierProperty definition.
 	 */
-	public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
-		
+	public static NonIdentifierAttribute buildEntityBasedAttribute(
+			EntityPersister persister,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			Property property,
+			boolean lazyAvailable) {
 		final Type type = property.getValue().getType();
-		
+
+		final NonIdentifierAttributeNature nature = decode( type );
+
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 		
 		// we need to dirty check many-to-ones with not-found="ignore" in order 
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 		
 		boolean alwaysDirtyCheck = type.isAssociationType() && 
 				( (AssociationType) type ).isAlwaysDirtyChecked(); 
 
+		switch ( nature ) {
+			case BASIC: {
+				return new EntityBasedBasicAttribute(
+						persister,
+						sessionFactory,
+						attributeNumber,
+						property.getName(),
+						type,
+						new BaselineAttributeInformation.Builder()
+								.setLazy( lazyAvailable && property.isLazy() )
+								.setInsertable( property.isInsertable() )
+								.setUpdateable( property.isUpdateable() )
+								.setInsertGenerated(
+										property.getGeneration() == PropertyGeneration.INSERT
+												|| property.getGeneration() == PropertyGeneration.ALWAYS
+								)
+								.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
+								.setNullable( property.isOptional() )
+								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
+								.setVersionable( property.isOptimisticLocked() )
+								.setCascadeStyle( property.getCascadeStyle() )
+								.setFetchMode( property.getValue().getFetchMode() )
+								.createInformation()
+				);
+			}
+			case COMPOSITE: {
+				return new EntityBasedCompositeAttribute(
+						persister,
+						sessionFactory,
+						attributeNumber,
+						property.getName(),
+						(CompositeType) type,
+						new BaselineAttributeInformation.Builder()
+								.setLazy( lazyAvailable && property.isLazy() )
+								.setInsertable( property.isInsertable() )
+								.setUpdateable( property.isUpdateable() )
+								.setInsertGenerated(
+										property.getGeneration() == PropertyGeneration.INSERT
+												|| property.getGeneration() == PropertyGeneration.ALWAYS
+								)
+								.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
+								.setNullable( property.isOptional() )
+								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
+								.setVersionable( property.isOptimisticLocked() )
+								.setCascadeStyle( property.getCascadeStyle() )
+								.setFetchMode( property.getValue().getFetchMode() )
+								.createInformation()
+				);
+			}
+			case ENTITY:
+			case ANY:
+			case COLLECTION: {
+				return new EntityBasedAssociationAttribute(
+						persister,
+						sessionFactory,
+						attributeNumber,
+						property.getName(),
+						(AssociationType) type,
+						new BaselineAttributeInformation.Builder()
+								.setLazy( lazyAvailable && property.isLazy() )
+								.setInsertable( property.isInsertable() )
+								.setUpdateable( property.isUpdateable() )
+								.setInsertGenerated(
+										property.getGeneration() == PropertyGeneration.INSERT
+												|| property.getGeneration() == PropertyGeneration.ALWAYS
+								)
+								.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
+								.setNullable( property.isOptional() )
+								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
+								.setVersionable( property.isOptimisticLocked() )
+								.setCascadeStyle( property.getCascadeStyle() )
+								.setFetchMode( property.getValue().getFetchMode() )
+								.createInformation()
+				);
+			}
+			default: {
+				throw new HibernateException( "Internal error" );
+			}
+		}
+	}
+
+	private static NonIdentifierAttributeNature decode(Type type) {
+		if ( type.isAssociationType() ) {
+			AssociationType associationType = (AssociationType) type;
+
+			if ( type.isComponentType() ) {
+				// an any type is both an association and a composite...
+				return NonIdentifierAttributeNature.ANY;
+			}
+
+			return type.isCollectionType()
+					? NonIdentifierAttributeNature.COLLECTION
+					: NonIdentifierAttributeNature.ENTITY;
+		}
+		else {
+			if ( type.isComponentType() ) {
+				return NonIdentifierAttributeNature.COMPOSITE;
+			}
+
+			return NonIdentifierAttributeNature.BASIC;
+		}
+	}
+
+	@Deprecated
+	public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
+		final Type type = property.getValue().getType();
+
+		// we need to dirty check collections, since they can cause an owner
+		// version number increment
+
+		// we need to dirty check many-to-ones with not-found="ignore" in order
+		// to update the cache (not the database), since in this case a null
+		// entity reference can lose information
+
+		boolean alwaysDirtyCheck = type.isAssociationType() &&
+				( (AssociationType) type ).isAlwaysDirtyChecked();
+
 		return new StandardProperty(
 				property.getName(),
-				property.getNodeName(),
 				type,
 				lazyAvailable && property.isLazy(),
 				property.isInsertable(),
 				property.isUpdateable(),
-		        property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
+				property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				alwaysDirtyCheck || property.isUpdateable(),
 				property.isOptimisticLocked(),
 				property.getCascadeStyle(),
-		        property.getValue().getFetchMode()
-			);
+				property.getValue().getFetchMode()
+		);
 	}
 
+
 	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
-	 * @return The appropriate StandardProperty definition.
+	 * @return The appropriate NonIdentifierProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(AttributeBinding property, boolean lazyAvailable) {
 
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		final boolean alwaysDirtyCheck = type.isAssociationType() && ( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		if ( property.getAttribute().isSingular() ) {
 			final SingularAttributeBinding singularAttributeBinding = ( SingularAttributeBinding ) property;
 			final CascadeStyle cascadeStyle = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getCascadeStyle()
 					: CascadeStyles.NONE;
 			final FetchMode fetchMode = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					singularAttributeBinding.getAttribute().getName(),
-					null,
 					type,
 					lazyAvailable && singularAttributeBinding.isLazy(),
 					true, // insertable
 					true, // updatable
 					singularAttributeBinding.getGeneration() == PropertyGeneration.INSERT
 							|| singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.isNullable(),
 					alwaysDirtyCheck || areAllValuesIncludedInUpdate( singularAttributeBinding ),
 					singularAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 			);
 		}
 		else {
 			final AbstractPluralAttributeBinding pluralAttributeBinding = (AbstractPluralAttributeBinding) property;
 			final CascadeStyle cascadeStyle = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getCascadeStyle()
 					: CascadeStyles.NONE;
 			final FetchMode fetchMode = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					pluralAttributeBinding.getAttribute().getName(),
-					null,
 					type,
 					lazyAvailable && pluralAttributeBinding.isLazy(),
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					true, // pluralAttributeBinding.isInsertable(),
 					true, //pluralAttributeBinding.isUpdatable(),
 					false,
 					false,
 					false, // nullable - not sure what that means for a collection
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					//alwaysDirtyCheck || pluralAttributeBinding.isUpdatable(),
 					true,
 					pluralAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 				);
 		}
 	}
 
 	private static boolean areAllValuesIncludedInUpdate(SingularAttributeBinding attributeBinding) {
 		if ( attributeBinding.hasDerivedValue() ) {
 			return false;
 		}
 		for ( SimpleValueBinding valueBinding : attributeBinding.getSimpleValueBindings() ) {
 			if ( ! valueBinding.isIncludeInUpdate() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	private static Constructor getConstructor(PersistentClass persistentClass) {
 		if ( persistentClass == null || !persistentClass.hasPojoRepresentation() ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( persistentClass.getMappedClass() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Constructor getConstructor(EntityBinding entityBinding) {
 		if ( entityBinding == null || entityBinding.getEntity() == null ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( entityBinding.getEntity().getClassReference() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Getter getGetter(Property mappingProperty) {
 		if ( mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation() ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter( mappingProperty.getPersistentClass().getMappedClass(), mappingProperty.getName() );
 	}
 
 	private static Getter getGetter(AttributeBinding mappingProperty) {
 		if ( mappingProperty == null || mappingProperty.getContainer().getClassReference() == null ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter(
 				mappingProperty.getContainer().getClassReference(),
 				mappingProperty.getAttribute().getName()
 		);
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java
index 30eb8da053..a4c1adb8c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/StandardProperty.java
@@ -1,137 +1,88 @@
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
 package org.hibernate.tuple;
+
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.type.Type;
 
 /**
- * Represents a basic property within the Hibernate runtime-metamodel.
+ * Represents a non-identifier property within the Hibernate runtime-metamodel.
  *
  * @author Steve Ebersole
  */
-public class StandardProperty extends Property {
-
-    private final boolean lazy;
-    private final boolean insertable;
-    private final boolean updateable;
-	private final boolean insertGenerated;
-	private final boolean updateGenerated;
-    private final boolean nullable;
-    private final boolean dirtyCheckable;
-    private final boolean versionable;
-    private final CascadeStyle cascadeStyle;
-	private final FetchMode fetchMode;
-
-    /**
-     * Constructs StandardProperty instances.
-     *
-     * @param name The name by which the property can be referenced within
-     * its owner.
-     * @param node The node name to use for XML-based representation of this
-     * property.
-     * @param type The Hibernate Type of this property.
-     * @param lazy Should this property be handled lazily?
-     * @param insertable Is this property an insertable value?
-     * @param updateable Is this property an updateable value?
-     * @param insertGenerated Is this property generated in the database on insert?
-     * @param updateGenerated Is this property generated in the database on update?
-     * @param nullable Is this property a nullable value?
-     * @param checkable Is this property a checkable value?
-     * @param versionable Is this property a versionable value?
-     * @param cascadeStyle The cascade style for this property's value.
-     * @param fetchMode Any fetch mode defined for this property
-     */
-    public StandardProperty(
-            String name,
-            String node,
-            Type type,
-            boolean lazy,
-            boolean insertable,
-            boolean updateable,
-            boolean insertGenerated,
-            boolean updateGenerated,
-            boolean nullable,
-            boolean checkable,
-            boolean versionable,
-            CascadeStyle cascadeStyle,
-            FetchMode fetchMode) {
-        super(name, node, type);
-        this.lazy = lazy;
-        this.insertable = insertable;
-        this.updateable = updateable;
-        this.insertGenerated = insertGenerated;
-	    this.updateGenerated = updateGenerated;
-        this.nullable = nullable;
-        this.dirtyCheckable = checkable;
-        this.versionable = versionable;
-        this.cascadeStyle = cascadeStyle;
-	    this.fetchMode = fetchMode;
-    }
-
-    public boolean isLazy() {
-        return lazy;
-    }
-
-    public boolean isInsertable() {
-        return insertable;
-    }
-
-    public boolean isUpdateable() {
-        return updateable;
-    }
-
-	public boolean isInsertGenerated() {
-		return insertGenerated;
-	}
-
-	public boolean isUpdateGenerated() {
-		return updateGenerated;
-	}
-
-    public boolean isNullable() {
-        return nullable;
-    }
-
-    public boolean isDirtyCheckable(boolean hasUninitializedProperties) {
-        return isDirtyCheckable() && ( !hasUninitializedProperties || !isLazy() );
-    }
-
-    public boolean isDirtyCheckable() {
-        return dirtyCheckable;
-    }
-
-    public boolean isVersionable() {
-        return versionable;
-    }
-
-    public CascadeStyle getCascadeStyle() {
-        return cascadeStyle;
-    }
+@Deprecated
+public class StandardProperty extends AbstractNonIdentifierAttribute implements NonIdentifierAttribute {
 
-	public FetchMode getFetchMode() {
-		return fetchMode;
+	/**
+	 * Constructs NonIdentifierProperty instances.
+	 *
+	 * @param name The name by which the property can be referenced within
+	 * its owner.
+	 * @param type The Hibernate Type of this property.
+	 * @param lazy Should this property be handled lazily?
+	 * @param insertable Is this property an insertable value?
+	 * @param updateable Is this property an updateable value?
+	 * @param insertGenerated Is this property generated in the database on insert?
+	 * @param updateGenerated Is this property generated in the database on update?
+	 * @param nullable Is this property a nullable value?
+	 * @param checkable Is this property a checkable value?
+	 * @param versionable Is this property a versionable value?
+	 * @param cascadeStyle The cascade style for this property's value.
+	 * @param fetchMode Any fetch mode defined for this property
+	 */
+	public StandardProperty(
+			String name,
+			Type type,
+			boolean lazy,
+			boolean insertable,
+			boolean updateable,
+			boolean insertGenerated,
+			boolean updateGenerated,
+			boolean nullable,
+			boolean checkable,
+			boolean versionable,
+			CascadeStyle cascadeStyle,
+			FetchMode fetchMode) {
+		super(
+				null,
+				null,
+				-1,
+				name,
+				type,
+				new BaselineAttributeInformation.Builder()
+						.setLazy( lazy )
+						.setInsertable( insertable )
+						.setUpdateable( updateable )
+						.setInsertGenerated( insertGenerated )
+						.setUpdateGenerated( updateGenerated )
+						.setNullable( nullable )
+						.setDirtyCheckable( checkable )
+						.setVersionable( versionable )
+						.setCascadeStyle( cascadeStyle )
+						.setFetchMode( fetchMode )
+						.createInformation()
+		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/VersionProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/VersionProperty.java
deleted file mode 100644
index 639939eb4c..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/tuple/VersionProperty.java
+++ /dev/null
@@ -1,81 +0,0 @@
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
-package org.hibernate.tuple;
-import org.hibernate.engine.spi.CascadeStyle;
-import org.hibernate.engine.spi.VersionValue;
-import org.hibernate.type.Type;
-
-/**
- * Represents a version property within the Hibernate runtime-metamodel.
- *
- * @author Steve Ebersole
- */
-public class VersionProperty extends StandardProperty {
-
-    private final VersionValue unsavedValue;
-
-    /**
-     * Constructs VersionProperty instances.
-     *
-     * @param name The name by which the property can be referenced within
-     * its owner.
-     * @param node The node name to use for XML-based representation of this
-     * property.
-     * @param type The Hibernate Type of this property.
-     * @param lazy Should this property be handled lazily?
-     * @param insertable Is this property an insertable value?
-     * @param updateable Is this property an updateable value?
-     * @param insertGenerated Is this property generated in the database on insert?
-     * @param updateGenerated Is this property generated in the database on update?
-     * @param nullable Is this property a nullable value?
-     * @param checkable Is this property a checkable value?
-     * @param versionable Is this property a versionable value?
-     * @param cascadeStyle The cascade style for this property's value.
-     * @param unsavedValue The value which, if found as the value of
-     * this (i.e., the version) property, represents new (i.e., un-saved)
-     * instances of the owning entity.
-     */
-    public VersionProperty(
-            String name,
-            String node,
-            Type type,
-            boolean lazy,
-            boolean insertable,
-            boolean updateable,
-            boolean insertGenerated,
-            boolean updateGenerated,
-            boolean nullable,
-            boolean checkable,
-            boolean versionable,
-            CascadeStyle cascadeStyle,
-            VersionValue unsavedValue) {
-        super( name, node, type, lazy, insertable, updateable, insertGenerated, updateGenerated, nullable, checkable, versionable, cascadeStyle, null );
-        this.unsavedValue = unsavedValue;
-    }
-
-    public VersionValue getUnsavedValue() {
-        return unsavedValue;
-    }
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
new file mode 100644
index 0000000000..9dd83c8885
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
@@ -0,0 +1,61 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.component;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.tuple.AbstractNonIdentifierAttribute;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.tuple.NonIdentifierAttribute;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractCompositeBasedAttribute
+		extends AbstractNonIdentifierAttribute
+		implements NonIdentifierAttribute {
+
+	private final int ownerAttributeNumber;
+
+	public AbstractCompositeBasedAttribute(
+			AbstractCompositeDefinition source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			Type attributeType,
+			BaselineAttributeInformation baselineInfo,
+			int ownerAttributeNumber) {
+		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
+		this.ownerAttributeNumber = ownerAttributeNumber;
+	}
+
+	protected int ownerAttributeNumber() {
+		return ownerAttributeNumber;
+	}
+
+	@Override
+	public AbstractCompositeDefinition getSource() {
+		return (AbstractCompositeDefinition) super.getSource();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeDefinition.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeDefinition.java
new file mode 100644
index 0000000000..87ca927f9d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeDefinition.java
@@ -0,0 +1,202 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.component;
+
+import java.util.Iterator;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Joinable;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.persister.walking.spi.AssociationKey;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeSource;
+import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.tuple.AbstractNonIdentifierAttribute;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.AssociationType;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.ForeignKeyDirection;
+import org.hibernate.type.Type;
+
+import static org.hibernate.engine.internal.JoinHelper.getLHSColumnNames;
+import static org.hibernate.engine.internal.JoinHelper.getLHSTableName;
+import static org.hibernate.engine.internal.JoinHelper.getRHSColumnNames;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractCompositeDefinition extends AbstractNonIdentifierAttribute implements CompositeDefinition {
+	protected AbstractCompositeDefinition(
+			AttributeSource source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			CompositeType attributeType,
+			BaselineAttributeInformation baselineInfo) {
+		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
+	}
+
+	@Override
+	public CompositeType getType() {
+		return (CompositeType) super.getType();
+	}
+
+	@Override
+	public Iterable<AttributeDefinition> getAttributes() {
+		return new Iterable<AttributeDefinition>() {
+			@Override
+			public Iterator<AttributeDefinition> iterator() {
+				return new Iterator<AttributeDefinition>() {
+					private final int numberOfAttributes = getType().getSubtypes().length;
+					private int currentAttributeNumber = 0;
+					private int currentColumnPosition = 0;
+
+					@Override
+					public boolean hasNext() {
+						return currentAttributeNumber < numberOfAttributes;
+					}
+
+					@Override
+					public AttributeDefinition next() {
+						final int attributeNumber = currentAttributeNumber;
+						currentAttributeNumber++;
+
+						final String name = getType().getPropertyNames()[attributeNumber];
+						final Type type = getType().getSubtypes()[attributeNumber];
+
+						int columnPosition = currentColumnPosition;
+						currentColumnPosition += type.getColumnSpan( sessionFactory() );
+
+						if ( type.isAssociationType() ) {
+							// we build the association-key here because of the "goofiness" with 'currentColumnPosition'
+							final AssociationKey associationKey;
+							final AssociationType aType = (AssociationType) type;
+							final Joinable joinable = aType.getAssociatedJoinable( sessionFactory() );
+							if ( aType.getForeignKeyDirection() == ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT ) {
+								associationKey = new AssociationKey(
+										getLHSTableName(
+												aType,
+												attributeNumber(),
+												(OuterJoinLoadable) joinable
+										),
+										getLHSColumnNames(
+												aType,
+												attributeNumber(),
+												columnPosition,
+												(OuterJoinLoadable) joinable,
+												sessionFactory()
+										)
+								);
+							}
+							else {
+								associationKey = new AssociationKey(
+										joinable.getTableName(),
+										getRHSColumnNames( aType, sessionFactory() )
+								);
+							}
+
+							return new CompositeBasedAssociationAttribute(
+									AbstractCompositeDefinition.this,
+									sessionFactory(),
+									currentAttributeNumber,
+									name,
+									(AssociationType) type,
+									new BaselineAttributeInformation.Builder()
+											.setInsertable( AbstractCompositeDefinition.this.isInsertable() )
+											.setUpdateable( AbstractCompositeDefinition.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositeDefinition.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositeDefinition.this.isUpdateGenerated() )
+											.setNullable( getType().getPropertyNullability()[currentAttributeNumber] )
+											.setDirtyCheckable( true )
+											.setVersionable( AbstractCompositeDefinition.this.isVersionable() )
+											.setCascadeStyle( getType().getCascadeStyle( currentAttributeNumber ) )
+											.setFetchMode( getType().getFetchMode( currentAttributeNumber ) )
+											.createInformation(),
+									AbstractCompositeDefinition.this.attributeNumber(),
+									associationKey
+							);
+						}
+						else if ( type.isComponentType() ) {
+							return new CompositeBasedCompositeAttribute(
+									AbstractCompositeDefinition.this,
+									sessionFactory(),
+									currentAttributeNumber,
+									name,
+									(CompositeType) type,
+									new BaselineAttributeInformation.Builder()
+											.setInsertable( AbstractCompositeDefinition.this.isInsertable() )
+											.setUpdateable( AbstractCompositeDefinition.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositeDefinition.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositeDefinition.this.isUpdateGenerated() )
+											.setNullable( getType().getPropertyNullability()[currentAttributeNumber] )
+											.setDirtyCheckable( true )
+											.setVersionable( AbstractCompositeDefinition.this.isVersionable() )
+											.setCascadeStyle( getType().getCascadeStyle( currentAttributeNumber ) )
+											.setFetchMode( getType().getFetchMode( currentAttributeNumber ) )
+											.createInformation()
+							);
+						}
+						else {
+							return new CompositeBasedBasicAttribute(
+									AbstractCompositeDefinition.this,
+									sessionFactory(),
+									currentAttributeNumber,
+									name,
+									type,
+									new BaselineAttributeInformation.Builder()
+											.setInsertable( AbstractCompositeDefinition.this.isInsertable() )
+											.setUpdateable( AbstractCompositeDefinition.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositeDefinition.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositeDefinition.this.isUpdateGenerated() )
+											.setNullable( getType().getPropertyNullability()[currentAttributeNumber] )
+											.setDirtyCheckable( true )
+											.setVersionable( AbstractCompositeDefinition.this.isVersionable() )
+											.setCascadeStyle( getType().getCascadeStyle( currentAttributeNumber ) )
+											.setFetchMode( getType().getFetchMode( currentAttributeNumber ) )
+											.createInformation()
+							);
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
+
+	public EntityPersister locateOwningPersister() {
+		if ( EntityDefinition.class.isInstance( getSource() ) ) {
+			return ( (EntityDefinition) getSource() ).getEntityPersister();
+		}
+		else {
+			return ( (AbstractCompositeDefinition) getSource() ).locateOwningPersister();
+		}
+	}
+}
+
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
index 2de23be05a..2ccd7ffebe 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/ComponentMetamodel.java
@@ -1,125 +1,125 @@
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
 package org.hibernate.tuple.component;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Property;
-import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.StandardProperty;
+import org.hibernate.tuple.PropertyFactory;
 
 /**
  * Centralizes metamodel information about a component.
  *
  * @author Steve Ebersole
  */
 public class ComponentMetamodel implements Serializable {
 
 	// TODO : will need reference to session factory to fully complete HHH-1907
 
 //	private final SessionFactoryImplementor sessionFactory;
 	private final String role;
 	private final boolean isKey;
 	private final StandardProperty[] properties;
 
 	private final EntityMode entityMode;
 	private final ComponentTuplizer componentTuplizer;
 
 	// cached for efficiency...
 	private final int propertySpan;
 	private final Map propertyIndexes = new HashMap();
 
 //	public ComponentMetamodel(Component component, SessionFactoryImplementor sessionFactory) {
 	public ComponentMetamodel(Component component) {
 //		this.sessionFactory = sessionFactory;
 		this.role = component.getRoleName();
 		this.isKey = component.isKey();
 		propertySpan = component.getPropertySpan();
 		properties = new StandardProperty[propertySpan];
 		Iterator itr = component.getPropertyIterator();
 		int i = 0;
 		while ( itr.hasNext() ) {
 			Property property = ( Property ) itr.next();
 			properties[i] = PropertyFactory.buildStandardProperty( property, false );
 			propertyIndexes.put( property.getName(), i );
 			i++;
 		}
 
 		entityMode = component.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 
 		// todo : move this to SF per HHH-3517; also see HHH-1907 and ComponentMetamodel
 		final ComponentTuplizerFactory componentTuplizerFactory = new ComponentTuplizerFactory();
 		final String tuplizerClassName = component.getTuplizerImplClassName( entityMode );
 		this.componentTuplizer = tuplizerClassName == null ? componentTuplizerFactory.constructDefaultTuplizer(
 				entityMode,
 				component
 		) : componentTuplizerFactory.constructTuplizer( tuplizerClassName, component );
 	}
 
 	public boolean isKey() {
 		return isKey;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public StandardProperty[] getProperties() {
 		return properties;
 	}
 
 	public StandardProperty getProperty(int index) {
 		if ( index < 0 || index >= propertySpan ) {
 			throw new IllegalArgumentException( "illegal index value for component property access [request=" + index + ", span=" + propertySpan + "]" );
 		}
 		return properties[index];
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = ( Integer ) propertyIndexes.get( propertyName );
 		if ( index == null ) {
 			throw new HibernateException( "component does not contain such a property [" + propertyName + "]" );
 		}
 		return index;
 	}
 
 	public StandardProperty getProperty(String propertyName) {
 		return getProperty( getPropertyIndex( propertyName ) );
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public ComponentTuplizer getComponentTuplizer() {
 		return componentTuplizer;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
new file mode 100644
index 0000000000..c064c6e457
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
@@ -0,0 +1,157 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.component;
+
+import org.hibernate.FetchMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Joinable;
+import org.hibernate.persister.walking.internal.Helper;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AssociationKey;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.AssociationType;
+import org.hibernate.type.CompositeType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositeBasedAssociationAttribute
+		extends AbstractCompositeBasedAttribute
+		implements AssociationAttributeDefinition {
+
+	private final AssociationKey associationKey;
+	private Joinable joinable;
+
+	public CompositeBasedAssociationAttribute(
+			AbstractCompositeDefinition source,
+			SessionFactoryImplementor factory,
+			int attributeNumber,
+			String attributeName,
+			AssociationType attributeType,
+			BaselineAttributeInformation baselineInfo,
+			int ownerAttributeNumber,
+			AssociationKey associationKey) {
+		super( source, factory, attributeNumber, attributeName, attributeType, baselineInfo, ownerAttributeNumber );
+		this.associationKey = associationKey;
+	}
+
+	@Override
+	public AssociationType getType() {
+		return (AssociationType) super.getType();
+	}
+
+	protected Joinable getJoinable() {
+		if ( joinable == null ) {
+			joinable = getType().getAssociatedJoinable( sessionFactory() );
+		}
+		return joinable;
+	}
+
+	@Override
+	public AssociationKey getAssociationKey() {
+		return associationKey;
+	}
+
+	@Override
+	public boolean isCollection() {
+		return getJoinable().isCollection();
+	}
+
+	@Override
+	public EntityDefinition toEntityDefinition() {
+		if ( isCollection() ) {
+			throw new IllegalStateException( "Cannot treat collection attribute as entity type" );
+		}
+		return (EntityPersister) getJoinable();
+	}
+
+	@Override
+	public CollectionDefinition toCollectionDefinition() {
+		if ( isCollection() ) {
+			throw new IllegalStateException( "Cannot treat entity attribute as collection type" );
+		}
+		return (CollectionPersister) getJoinable();
+	}
+
+	@Override
+	public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
+		final EntityPersister owningPersister = locateOwningPersister();
+
+		FetchStyle style = determineFetchStyleByProfile(
+				loadQueryInfluencers,
+				owningPersister,
+				propertyPath,
+				ownerAttributeNumber()
+		);
+		if ( style == null ) {
+			style = determineFetchStyleByMetadata(
+					getSource().getType().getFetchMode( attributeNumber() ),
+					getType()
+			);
+		}
+
+		return new FetchStrategy( determineFetchTiming( style ), style );
+	}
+
+	protected FetchStyle determineFetchStyleByProfile(
+			LoadQueryInfluencers loadQueryInfluencers,
+			EntityPersister owningPersister,
+			PropertyPath propertyPath,
+			int ownerAttributeNumber) {
+		return Helper.determineFetchStyleByProfile(
+				loadQueryInfluencers,
+				owningPersister,
+				propertyPath,
+				ownerAttributeNumber
+		);
+	}
+
+	protected FetchStyle determineFetchStyleByMetadata(FetchMode fetchMode, AssociationType type) {
+		return Helper.determineFetchStyleByMetadata( fetchMode, type, sessionFactory() );
+	}
+
+	private FetchTiming determineFetchTiming(FetchStyle style) {
+		return Helper.determineFetchTiming( style, getType(), sessionFactory() );
+	}
+
+	private EntityPersister locateOwningPersister() {
+		return getSource().locateOwningPersister();
+	}
+
+	@Override
+	public CascadeStyle determineCascadeStyle() {
+		final CompositeType compositeType = (CompositeType) locateOwningPersister().getPropertyType( getName() );
+		return compositeType.getCascadeStyle( attributeNumber() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedBasicAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedBasicAttribute.java
new file mode 100644
index 0000000000..a975635599
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedBasicAttribute.java
@@ -0,0 +1,45 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.component;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.walking.spi.AttributeSource;
+import org.hibernate.tuple.AbstractNonIdentifierAttribute;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositeBasedBasicAttribute extends AbstractNonIdentifierAttribute {
+	protected CompositeBasedBasicAttribute(
+			AttributeSource source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			Type attributeType,
+			BaselineAttributeInformation baselineInfo) {
+		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedCompositeAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedCompositeAttribute.java
new file mode 100644
index 0000000000..1cca99c828
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedCompositeAttribute.java
@@ -0,0 +1,46 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.component;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.CompositeType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositeBasedCompositeAttribute
+		extends AbstractCompositeDefinition
+		implements CompositeDefinition {
+	public CompositeBasedCompositeAttribute(
+			CompositeDefinition source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			CompositeType attributeType,
+			BaselineAttributeInformation baselineInfo) {
+		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityBasedAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityBasedAttribute.java
new file mode 100644
index 0000000000..64db5884b3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityBasedAttribute.java
@@ -0,0 +1,50 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.entity;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.tuple.AbstractNonIdentifierAttribute;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractEntityBasedAttribute extends AbstractNonIdentifierAttribute {
+	protected AbstractEntityBasedAttribute(
+			EntityPersister source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			Type attributeType,
+			BaselineAttributeInformation attributeInformation) {
+		super( source, sessionFactory, attributeNumber, attributeName, attributeType, attributeInformation );
+	}
+
+	@Override
+	public EntityPersister getSource() {
+		return (EntityPersister) super.getSource();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index d82567ac21..6c1c08a6f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,800 +1,801 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.id.Assigned;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
+import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.StandardProperty;
-import org.hibernate.tuple.VersionProperty;
+import org.hibernate.tuple.entity.VersionProperty;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Support for tuplizers relating to entities.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public abstract class AbstractEntityTuplizer implements EntityTuplizer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractEntityTuplizer.class.getName()
 	);
 
 	//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter() and getSetter() are implemented currently; yuck!
 
 	private final EntityMetamodel entityMetamodel;
 
 	private final Getter idGetter;
 	private final Setter idSetter;
 
 	protected final Getter[] getters;
 	protected final Setter[] setters;
 	protected final int propertySpan;
 	protected final boolean hasCustomAccessors;
 	private final Instantiator instantiator;
 	private final ProxyFactory proxyFactory;
 	private final CompositeType identifierMapperType;
 
 	public Type getIdentifierMapperType() {
 		return identifierMapperType;
 	}
 
 	/**
 	 * Build an appropriate Getter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Getter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(PersistentClass mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
 
 	/**
 	 * Build an appropriate Getter for the given property.
 	 *
 	 *
 	 * @param mappedProperty The property to be accessed via the built Getter.
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(AttributeBinding mappedProperty);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(AttributeBinding mappedProperty);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(EntityBinding mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(EntityBinding mappingInfo, Getter idGetter, Setter idSetter);
 
 	/**
 	 * Constructs a new AbstractEntityTuplizer instance.
 	 *
 	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
 	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
 	 */
 	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
 		this.entityMetamodel = entityMetamodel;
 
 		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
 			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 		}
 		else {
 			idGetter = null;
 			idSetter = null;
 		}
 
 		propertySpan = entityMetamodel.getPropertySpan();
 
         getters = new Getter[propertySpan];
 		setters = new Setter[propertySpan];
 
 		Iterator itr = mappingInfo.getPropertyClosureIterator();
 		boolean foundCustomAccessor=false;
 		int i=0;
 		while ( itr.hasNext() ) {
 			//TODO: redesign how PropertyAccessors are acquired...
 			Property property = (Property) itr.next();
 			getters[i] = buildPropertyGetter(property, mappingInfo);
 			setters[i] = buildPropertySetter(property, mappingInfo);
 			if ( !property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
         instantiator = buildInstantiator( mappingInfo );
 
 		if ( entityMetamodel.isLazy() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if (proxyFactory == null) {
 				entityMetamodel.setLazy( false );
 			}
 		}
 		else {
 			proxyFactory = null;
 		}
 
 		Component mapper = mappingInfo.getIdentifierMapper();
 		if ( mapper == null ) {
 			identifierMapperType = null;
 			mappedIdentifierValueMarshaller = null;
 		}
 		else {
 			identifierMapperType = (CompositeType) mapper.getType();
 			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
 					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
 					(ComponentType) identifierMapperType
 			);
 		}
 	}
 
 	/**
 	 * Constructs a new AbstractEntityTuplizer instance.
 	 *
 	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
 	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
 	 */
 	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappingInfo) {
 		this.entityMetamodel = entityMetamodel;
 
 		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
 			idGetter = buildPropertyGetter( mappingInfo.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 			idSetter = buildPropertySetter( mappingInfo.getHierarchyDetails().getEntityIdentifier().getValueBinding() );
 		}
 		else {
 			idGetter = null;
 			idSetter = null;
 		}
 
 		propertySpan = entityMetamodel.getPropertySpan();
 
 		getters = new Getter[ propertySpan ];
 		setters = new Setter[ propertySpan ];
 
 		boolean foundCustomAccessor = false;
 		int i = 0;
 		for ( AttributeBinding property : mappingInfo.getAttributeBindingClosure() ) {
 			if ( property == mappingInfo.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				continue; // ID binding processed above
 			}
 
 			//TODO: redesign how PropertyAccessors are acquired...
 			getters[ i ] = buildPropertyGetter( property );
 			setters[ i ] = buildPropertySetter( property );
 			if ( ! property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
 		instantiator = buildInstantiator( mappingInfo );
 
 		if ( entityMetamodel.isLazy() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if ( proxyFactory == null ) {
 				entityMetamodel.setLazy( false );
 			}
 		}
 		else {
 			proxyFactory = null;
 		}
 
 
 		// TODO: Fix this when components are working (HHH-6173)
 		//Component mapper = mappingInfo.getEntityIdentifier().getIdentifierMapper();
 		Component mapper = null;
 		if ( mapper == null ) {
 			identifierMapperType = null;
 			mappedIdentifierValueMarshaller = null;
 		}
 		else {
 			identifierMapperType = ( CompositeType ) mapper.getType();
 			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
 					( ComponentType ) entityMetamodel.getIdentifierProperty().getType(),
 					( ComponentType ) identifierMapperType
 			);
 		}
 	}
 
 	/** Retreives the defined entity-name for the tuplized entity.
 	 *
 	 * @return The entity-name.
 	 */
 	protected String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	/**
 	 * Retrieves the defined entity-names for any subclasses defined for this
 	 * entity.
 	 *
 	 * @return Any subclass entity-names.
 	 */
 	protected Set getSubclassEntityNames() {
 		return entityMetamodel.getSubclassEntityNames();
 	}
 
 	public Serializable getIdentifier(Object entity) throws HibernateException {
 		return getIdentifier( entity, null );
 	}
 
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		final Object id;
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			id = entity;
 		}
 		else if ( HibernateProxy.class.isInstance( entity ) ) {
 			id = ( (HibernateProxy) entity ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			if ( idGetter == null ) {
 				if (identifierMapperType==null) {
 					throw new HibernateException( "The class has no identifier property: " + getEntityName() );
 				}
 				else {
 					id = mappedIdentifierValueMarshaller.getIdentifier( entity, getEntityMode(), session );
 				}
 			}
 			else {
                 id = idGetter.get( entity );
             }
         }
 
 		try {
 			return (Serializable) id;
 		}
 		catch ( ClassCastException cce ) {
 			StringBuilder msg = new StringBuilder( "Identifier classes must be serializable. " );
 			if ( id != null ) {
 				msg.append( id.getClass().getName() ).append( " is not serializable. " );
 			}
 			if ( cce.getMessage() != null ) {
 				msg.append( cce.getMessage() );
 			}
 			throw new ClassCastException( msg.toString() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		setIdentifier( entity, id, null );
 	}
 
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			if ( entity != id ) {
 				CompositeType copier = (CompositeType) entityMetamodel.getIdentifierProperty().getType();
 				copier.setPropertyValues( entity, copier.getPropertyValues( id, getEntityMode() ), getEntityMode() );
 			}
 		}
 		else if ( idSetter != null ) {
 			idSetter.set( entity, id, getFactory() );
 		}
 		else if ( identifierMapperType != null ) {
 			mappedIdentifierValueMarshaller.setIdentifier( entity, id, getEntityMode(), session );
 		}
 	}
 
 	private static interface MappedIdentifierValueMarshaller {
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
 	}
 
 	private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;
 
 	private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(
 			ComponentType mappedIdClassComponentType,
 			ComponentType virtualIdComponent) {
 		// so basically at this point we know we have a "mapped" composite identifier
 		// which is an awful way to say that the identifier is represented differently
 		// in the entity and in the identifier value.  The incoming value should
 		// be an instance of the mapped identifier class (@IdClass) while the incoming entity
 		// should be an instance of the entity class as defined by metamodel.
 		//
 		// However, even within that we have 2 potential scenarios:
 		//		1) @IdClass types and entity @Id property types match
 		//			- return a NormalMappedIdentifierValueMarshaller
 		//		2) They do not match
 		//			- return a IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
 		boolean wereAllEquivalent = true;
 		// the sizes being off is a much bigger problem that should have been caught already...
 		for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 			if ( virtualIdComponent.getSubtypes()[i].isEntityType()
 					&& ! mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
 				wereAllEquivalent = false;
 				break;
 			}
 		}
 
 		return wereAllEquivalent
 				? new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
 				: new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType );
 	}
 
 	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private NormalMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			virtualIdComponent.setPropertyValues(
 					entity,
 					mappedIdentifierType.getPropertyValues( id, session ),
 					entityMode
 			);
 		}
 	}
 
 	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			final Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			final Type[] subTypes = virtualIdComponent.getSubtypes();
 			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
 			final Iterable<PersistEventListener> persistEventListeners = persistEventListeners( session );
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			final int length = subTypes.length;
 			for ( int i = 0 ; i < length; i++ ) {
 				if ( propertyValues[i] == null ) {
 					throw new HibernateException( "No part of a composite identifier may be null" );
 				}
 				//JPA 2 @MapsId + @IdClass points to the pk of the entity
 				if ( subTypes[i].isAssociationType() && ! copierSubTypes[i].isAssociationType() ) {
 					// we need a session to handle this use case
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final Object subId;
 					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
 						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
 					}
 					else {
 						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
 						if ( pcEntry != null ) {
 							subId = pcEntry.getId();
 						}
 						else {
 							LOG.debug( "Performing implicit derived identity cascade" );
 							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
 							for ( PersistEventListener listener : persistEventListeners ) {
 								listener.onPersist( event );
 							}
 							pcEntry = persistenceContext.getEntry( propertyValues[i] );
 							if ( pcEntry == null || pcEntry.getId() == null ) {
 								throw new HibernateException( "Unable to process implicit derived identity cascade" );
 							}
 							else {
 								subId = pcEntry.getId();
 							}
 						}
 					}
 					propertyValues[i] = subId;
 				}
 			}
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
 			final Object[] injectionValues = new Object[ extractedValues.length ];
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
 				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
 				if ( virtualPropertyType.isEntityType() && ! idClassPropertyType.isEntityType() ) {
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
 					final EntityKey entityKey = session.generateEntityKey(
 							(Serializable) extractedValues[i],
 							session.getFactory().getEntityPersister( associatedEntityName )
 					);
 					// it is conceivable there is a proxy, so check that first
 					Object association = persistenceContext.getProxy( entityKey );
 					if ( association == null ) {
 						// otherwise look for an initialized version
 						association = persistenceContext.getEntity( entityKey );
 					}
 					injectionValues[i] = association;
 				}
 				else {
 					injectionValues[i] = extractedValues[i];
 				}
 			}
 			virtualIdComponent.setPropertyValues( entity, injectionValues, entityMode );
 		}
 	}
 
 	private static Iterable<PersistEventListener> persistEventListeners(SessionImplementor session) {
 		return session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.PERSIST )
 				.listeners();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SessionImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned ) {
 		}
 		else {
 			//reset the id
 			Serializable result = entityMetamodel.getIdentifierProperty()
 					.getUnsavedValue()
 					.getDefaultValue( currentId );
 			setIdentifier( entity, result, session );
 			//reset the version
 			VersionProperty versionProperty = entityMetamodel.getVersionProperty();
 			if ( entityMetamodel.isVersioned() ) {
 				setPropertyValue(
 				        entity,
 				        entityMetamodel.getVersionPropertyIndex(),
 						versionProperty.getUnsavedValue().getDefaultValue( currentVersion )
 				);
 			}
 		}
 	}
 
 	public Object getVersion(Object entity) throws HibernateException {
 		if ( !entityMetamodel.isVersioned() ) return null;
 		return getters[ entityMetamodel.getVersionPropertyIndex() ].get( entity );
 	}
 
 	protected boolean shouldGetAllProperties(Object entity) {
 		return !hasUninitializedLazyProperties( entity );
 	}
 
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		boolean getAll = shouldGetAllProperties( entity );
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
-			StandardProperty property = entityMetamodel.getProperties()[j];
+			NonIdentifierAttribute property = entityMetamodel.getProperties()[j];
 			if ( getAll || !property.isLazy() ) {
 				result[j] = getters[j].get( entity );
 			}
 			else {
 				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 		}
 		return result;
 	}
 
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 	throws HibernateException {
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			result[j] = getters[j].getForInsert( entity, mergeMap, session );
 		}
 		return result;
 	}
 
 	public Object getPropertyValue(Object entity, int i) throws HibernateException {
 		return getters[i].get( entity );
 	}
 
 	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
 		int loc = propertyPath.indexOf('.');
 		String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		if (index == null) {
 			propertyPath = "_identifierMapper." + propertyPath;
 			loc = propertyPath.indexOf('.');
 			basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		}
 		index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		final Object baseValue = getPropertyValue( entity, index );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) entityMetamodel.getPropertyTypes()[index],
 					baseValue,
 					propertyPath.substring(loc+1)
 			);
 		}
 		else {
 			return baseValue;
 		}
 	}
 
 	/**
 	 * Extract a component property value.
 	 *
 	 * @param type The component property types.
 	 * @param component The component instance itself.
 	 * @param propertyPath The property path for the property to be extracted.
 	 * @return The property value extracted.
 	 */
 	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
 		final int loc = propertyPath.indexOf( '.' );
 		final String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		final int index = findSubPropertyIndex( type, basePropertyName );
 		final Object baseValue = type.getPropertyValue( component, index, getEntityMode() );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) type.getSubtypes()[index],
 					baseValue,
 					propertyPath.substring(loc+1)
 			);
 		}
 		else {
 			return baseValue;
 		}
 
 	}
 
 	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
 		final String[] propertyNames = type.getPropertyNames();
 		for ( int index = 0; index<propertyNames.length; index++ ) {
 			if ( subPropertyName.equals( propertyNames[index] ) ) {
 				return index;
 			}
 		}
 		throw new MappingException( "component property not found: " + subPropertyName );
 	}
 
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		boolean setAll = !entityMetamodel.hasLazyProperties();
 
 		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
 			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				setters[j].set( entity, values[j], getFactory() );
 			}
 		}
 	}
 
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
 		setters[i].set( entity, value, getFactory() );
 	}
 
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
 		setters[ entityMetamodel.getPropertyIndex( propertyName ) ].set( entity, value, getFactory() );
 	}
 
 	public final Object instantiate(Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		return instantiate( id, null );
 	}
 
 	public final Object instantiate(Serializable id, SessionImplementor session) {
 		Object result = getInstantiator().instantiate( id );
 		if ( id != null ) {
 			setIdentifier( result, id, session );
 		}
 		return result;
 	}
 
 	public final Object instantiate() throws HibernateException {
 		return instantiate( null, null );
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
 
 	public boolean hasUninitializedLazyProperties(Object entity) {
 		// the default is to simply not lazy fetch properties for now...
 		return false;
 	}
 
 	public final boolean isInstance(Object object) {
         return getInstantiator().isInstance( object );
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public final Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return getProxyFactory().getProxy( id, session );
 	}
 
 	public boolean isLifecycleImplementor() {
 		return false;
 	}
 
 	protected final EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	protected final SessionFactoryImplementor getFactory() {
 		return entityMetamodel.getSessionFactory();
 	}
 
 	protected final Instantiator getInstantiator() {
 		return instantiator;
 	}
 
 	protected final ProxyFactory getProxyFactory() {
 		return proxyFactory;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getEntityMetamodel().getName() + ')';
 	}
 
 	public Getter getIdentifierGetter() {
 		return idGetter;
 	}
 
 	public Getter getVersionGetter() {
 		if ( getEntityMetamodel().isVersioned() ) {
 			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
 		}
 		return null;
 	}
 
 	public Getter getGetter(int i) {
 		return getters[i];
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedAssociationAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedAssociationAttribute.java
new file mode 100644
index 0000000000..f19f1d8439
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedAssociationAttribute.java
@@ -0,0 +1,132 @@
+package org.hibernate.tuple.entity;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.spi.CascadeStyle;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.QueryableCollection;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Joinable;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.persister.walking.internal.Helper;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AssociationKey;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.AssociationType;
+import org.hibernate.type.ForeignKeyDirection;
+
+import static org.hibernate.engine.internal.JoinHelper.getLHSColumnNames;
+import static org.hibernate.engine.internal.JoinHelper.getLHSTableName;
+import static org.hibernate.engine.internal.JoinHelper.getRHSColumnNames;
+
+/**
+* @author Steve Ebersole
+*/
+public class EntityBasedAssociationAttribute
+		extends AbstractEntityBasedAttribute
+		implements AssociationAttributeDefinition {
+
+	private Joinable joinable;
+
+	public EntityBasedAssociationAttribute(
+			EntityPersister source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			AssociationType attributeType,
+			BaselineAttributeInformation baselineInfo) {
+		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
+	}
+
+	@Override
+	public AssociationType getType() {
+		return (AssociationType) super.getType();
+	}
+
+	protected Joinable getJoinable() {
+		if ( joinable == null ) {
+			joinable = getType().getAssociatedJoinable( sessionFactory() );
+		}
+		return joinable;
+	}
+
+	@Override
+	public AssociationKey getAssociationKey() {
+		final AssociationType type = getType();
+		final Joinable joinable = type.getAssociatedJoinable( sessionFactory() );
+
+		if ( type.getForeignKeyDirection() == ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT ) {
+			final String lhsTableName;
+			final String[] lhsColumnNames;
+
+			if ( joinable.isCollection() ) {
+				final QueryableCollection collectionPersister = (QueryableCollection) joinable;
+				lhsTableName = collectionPersister.getTableName();
+				lhsColumnNames = collectionPersister.getElementColumnNames();
+			}
+			else {
+				final OuterJoinLoadable entityPersister = (OuterJoinLoadable) joinable;
+				lhsTableName = getLHSTableName( type, attributeNumber(), entityPersister );
+				lhsColumnNames = getLHSColumnNames( type, attributeNumber(), entityPersister, sessionFactory() );
+			}
+			return new AssociationKey( lhsTableName, lhsColumnNames );
+		}
+		else {
+			return new AssociationKey( joinable.getTableName(), getRHSColumnNames( type, sessionFactory() ) );
+		}
+	}
+
+	@Override
+	public boolean isCollection() {
+		return getJoinable().isCollection();
+	}
+
+	@Override
+	public EntityDefinition toEntityDefinition() {
+		if ( isCollection() ) {
+			throw new IllegalStateException( "Cannot treat collection-valued attribute as entity type" );
+		}
+		return (EntityPersister) getJoinable();
+	}
+
+	@Override
+	public CollectionDefinition toCollectionDefinition() {
+		if ( ! isCollection() ) {
+			throw new IllegalStateException( "Cannot treat entity-valued attribute as collection type" );
+		}
+		return (QueryableCollection) getJoinable();
+	}
+
+	@Override
+	public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
+		final EntityPersister owningPersister = getSource().getEntityPersister();
+
+		FetchStyle style = Helper.determineFetchStyleByProfile(
+				loadQueryInfluencers,
+				owningPersister,
+				propertyPath,
+				attributeNumber()
+		);
+		if ( style == null ) {
+			style = Helper.determineFetchStyleByMetadata(
+					((OuterJoinLoadable) getSource().getEntityPersister()).getFetchMode( attributeNumber() ),
+					getType(),
+					sessionFactory()
+			);
+		}
+
+		return new FetchStrategy(
+				Helper.determineFetchTiming( style, getType(), sessionFactory() ),
+				style
+		);
+	}
+
+	@Override
+	public CascadeStyle determineCascadeStyle() {
+		return getSource().getEntityPersister().getPropertyCascadeStyles()[attributeNumber()];
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedBasicAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedBasicAttribute.java
new file mode 100644
index 0000000000..ed08bfca29
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedBasicAttribute.java
@@ -0,0 +1,44 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.entity;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityBasedBasicAttribute extends AbstractEntityBasedAttribute {
+	public EntityBasedBasicAttribute(
+			EntityPersister source,
+			SessionFactoryImplementor factory,
+			int attributeNumber,
+			String attributeName,
+			Type attributeType,
+			BaselineAttributeInformation baselineInfo) {
+		super( source, factory, attributeNumber, attributeName, attributeType, baselineInfo );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositeAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositeAttribute.java
new file mode 100644
index 0000000000..514bdd5dd5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositeAttribute.java
@@ -0,0 +1,49 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.tuple.entity;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.tuple.component.AbstractCompositeDefinition;
+import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.CompositeType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityBasedCompositeAttribute
+		extends AbstractCompositeDefinition
+		implements CompositeDefinition {
+
+	public EntityBasedCompositeAttribute(
+			EntityPersister source,
+			SessionFactoryImplementor factory,
+			int attributeNumber,
+			String attributeName,
+			CompositeType attributeType,
+			BaselineAttributeInformation baselineInfo) {
+		super( source, factory, attributeNumber, attributeName, attributeType, baselineInfo );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index ac2d02a4d0..7d0b2717fe 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,963 +1,987 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
+import org.hibernate.persister.entity.AbstractEntityPersister;
 import org.hibernate.tuple.IdentifierProperty;
+import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.StandardProperty;
-import org.hibernate.tuple.VersionProperty;
+import org.hibernate.tuple.entity.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes metamodel information about an entity.
  *
  * @author Steve Ebersole
  */
 public class EntityMetamodel implements Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityMetamodel.class.getName());
 
 	private static final int NO_VERSION_INDX = -66;
 
 	private final SessionFactoryImplementor sessionFactory;
+	private final AbstractEntityPersister persister;
 
 	private final String name;
 	private final String rootName;
 	private final EntityType entityType;
 
-	private final IdentifierProperty identifierProperty;
+	private final IdentifierProperty identifierAttribute;
 	private final boolean versioned;
 
 	private final int propertySpan;
 	private final int versionPropertyIndex;
-	private final StandardProperty[] properties;
+	private final NonIdentifierAttribute[] properties;
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyLaziness;
 	private final boolean[] propertyUpdateability;
 	private final boolean[] nonlazyPropertyUpdateability;
 	private final boolean[] propertyCheckability;
 	private final boolean[] propertyInsertability;
 	private final ValueInclusion[] insertInclusions;
 	private final ValueInclusion[] updateInclusions;
 	private final boolean[] propertyNullability;
 	private final boolean[] propertyVersionability;
 	private final CascadeStyle[] cascadeStyles;
 	private final boolean hasInsertGeneratedValues;
 	private final boolean hasUpdateGeneratedValues;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
 	private final boolean hasCollections;
 	private final boolean hasMutableProperties;
 	private final boolean hasLazyProperties;
 	private final boolean hasNonIdentifierPropertyNamedId;
 
 	private final int[] naturalIdPropertyNumbers;
 	private final boolean hasImmutableNaturalId;
 	private final boolean hasCacheableNaturalId;
 
 	private boolean lazy; //not final because proxy factory creation can fail
 	private final boolean hasCascades;
 	private final boolean mutable;
 	private final boolean isAbstract;
 	private final boolean selectBeforeUpdate;
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 	private final OptimisticLockStyle optimisticLockStyle;
 
 	private final boolean polymorphic;
 	private final String superclass;  // superclass entity-name
 	private final boolean explicitPolymorphism;
 	private final boolean inherited;
 	private final boolean hasSubclasses;
 	private final Set subclassEntityNames = new HashSet();
 	private final Map entityNameByInheritenceClassMap = new HashMap();
 
 	private final EntityMode entityMode;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityInstrumentationMetadata instrumentationMetadata;
 
-	public EntityMetamodel(PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
+	public EntityMetamodel(
+			PersistentClass persistentClass,
+			AbstractEntityPersister persister,
+			SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
+		this.persister = persister;
 
 		name = persistentClass.getEntityName();
 		rootName = persistentClass.getRootClass().getEntityName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
-		identifierProperty = PropertyFactory.buildIdentifierProperty(
-		        persistentClass,
-		        sessionFactory.getIdentifierGenerator( rootName )
-			);
+		identifierAttribute = PropertyFactory.buildIdentifierAttribute(
+				persistentClass,
+				sessionFactory.getIdentifierGenerator( rootName )
+		);
 
 		versioned = persistentClass.isVersioned();
 
 		instrumentationMetadata = persistentClass.hasPojoRepresentation()
 				? Environment.getBytecodeProvider().getEntityInstrumentationMetadata( persistentClass.getMappedClass() )
 				: new NonPojoInstrumentationMetadata( persistentClass.getEntityName() );
 
 		boolean hasLazy = false;
 
 		propertySpan = persistentClass.getPropertyClosureSpan();
-		properties = new StandardProperty[propertySpan];
+		properties = new NonIdentifierAttribute[propertySpan];
 		List<Integer> naturalIdNumbers = new ArrayList<Integer>();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 
 			if ( prop == persistentClass.getVersion() ) {
 				tempVersionProperty = i;
-				properties[i] = PropertyFactory.buildVersionProperty( prop, instrumentationMetadata.isInstrumented() );
+				properties[i] = PropertyFactory.buildVersionProperty(
+						persister,
+						sessionFactory,
+						i,
+						prop,
+						instrumentationMetadata.isInstrumented()
+				);
 			}
 			else {
-				properties[i] = PropertyFactory.buildStandardProperty( prop, instrumentationMetadata.isInstrumented() );
+				properties[i] = PropertyFactory.buildEntityBasedAttribute(
+						persister,
+						sessionFactory,
+						i,
+						prop,
+						instrumentationMetadata.isInstrumented()
+				);
 			}
 
 			if ( prop.isNaturalIdentifier() ) {
 				naturalIdNumbers.add( i );
 				if ( prop.isUpdateable() ) {
 					foundUpdateableNaturalIdProperty = true;
 				}
 			}
 
 			if ( "id".equals( prop.getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = prop.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyles.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(prop, i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = persistentClass.getNaturalIdCacheRegionName() != null;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
         if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
 
 		lazy = persistentClass.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				!persistentClass.hasPojoRepresentation() ||
 				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
 		);
 		mutable = persistentClass.isMutable();
 		if ( persistentClass.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = persistentClass.hasPojoRepresentation() &&
 			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
 		}
 		else {
 			isAbstract = persistentClass.isAbstract().booleanValue();
 			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
 			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
                 LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
 		dynamicUpdate = persistentClass.useDynamicUpdate();
 		dynamicInsert = persistentClass.useDynamicInsert();
 
 		polymorphic = persistentClass.isPolymorphic();
 		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
 		inherited = persistentClass.isInherited();
 		superclass = inherited ?
 				persistentClass.getSuperclass().getEntityName() :
 				null;
 		hasSubclasses = persistentClass.hasSubclasses();
 
 		optimisticLockStyle = interpretOptLockMode( persistentClass.getOptimisticLockMode() );
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		iter = persistentClass.getSubclassIterator();
 		while ( iter.hasNext() ) {
 			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		}
 		subclassEntityNames.add( name );
 
 		if ( persistentClass.hasPojoRepresentation() ) {
 			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
 			iter = persistentClass.getSubclassIterator();
 			while ( iter.hasNext() ) {
 				final PersistentClass pc = ( PersistentClass ) iter.next();
 				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
 			}
 		}
 
 		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
 		if ( tuplizerClassName == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
 		}
 	}
 
 	private OptimisticLockStyle interpretOptLockMode(int optimisticLockMode) {
 		switch ( optimisticLockMode ) {
 			case Versioning.OPTIMISTIC_LOCK_NONE: {
 				return OptimisticLockStyle.NONE;
 			}
 			case Versioning.OPTIMISTIC_LOCK_DIRTY: {
 				return OptimisticLockStyle.DIRTY;
 			}
 			case Versioning.OPTIMISTIC_LOCK_ALL: {
 				return OptimisticLockStyle.ALL;
 			}
 			default: {
 				return OptimisticLockStyle.VERSION;
 			}
 		}
 	}
 
-	public EntityMetamodel(EntityBinding entityBinding, SessionFactoryImplementor sessionFactory) {
+	public EntityMetamodel(
+			EntityBinding entityBinding,
+			AbstractEntityPersister persister,
+			SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
+		this.persister = persister;
 
 		name = entityBinding.getEntity().getName();
 
 		rootName = entityBinding.getHierarchyDetails().getRootEntityBinding().getEntity().getName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
-		identifierProperty = PropertyFactory.buildIdentifierProperty(
+		identifierAttribute = PropertyFactory.buildIdentifierProperty(
 		        entityBinding,
 		        sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = entityBinding.isVersioned();
 
 		boolean hasPojoRepresentation = false;
 		Class<?> mappedClass = null;
 		Class<?> proxyInterfaceClass = null;
 		if ( entityBinding.getEntity().getClassReferenceUnresolved() != null ) {
 			hasPojoRepresentation = true;
 			mappedClass = entityBinding.getEntity().getClassReference();
 			proxyInterfaceClass = entityBinding.getProxyInterfaceType().getValue();
 		}
 		instrumentationMetadata = Environment.getBytecodeProvider().getEntityInstrumentationMetadata( mappedClass );
 
 		boolean hasLazy = false;
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		BasicAttributeBinding rootEntityIdentifier = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 		// entityBinding.getAttributeClosureSpan() includes the identifier binding;
 		// "properties" here excludes the ID, so subtract 1 if the identifier binding is non-null
 		propertySpan = rootEntityIdentifier == null ?
 				entityBinding.getAttributeBindingClosureSpan() :
 				entityBinding.getAttributeBindingClosureSpan() - 1;
 
-		properties = new StandardProperty[propertySpan];
+		properties = new NonIdentifierAttribute[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == rootEntityIdentifier ) {
 				// skip the identifier attribute binding
 				continue;
 			}
 
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getVersioningAttributeBinding() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty(
+						persister,
 						entityBinding.getHierarchyDetails().getVersioningAttributeBinding(),
 						instrumentationMetadata.isInstrumented()
 				);
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, instrumentationMetadata.isInstrumented() );
 			}
 
 			// TODO: fix when natural IDs are added (HHH-6354)
 			//if ( attributeBinding.isNaturalIdentifier() ) {
 			//	naturalIdNumbers.add( i );
 			//	if ( attributeBinding.isUpdateable() ) {
 			//		foundUpdateableNaturalIdProperty = true;
 			//	}
 			//}
 
 			if ( "id".equals( attributeBinding.getAttribute().getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = attributeBinding.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( attributeBinding, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( attributeBinding, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyles.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(attributeBinding.getAttribute(), i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = false; //See previous TODO and HHH-6354
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
 		if (hasLazyProperties) {
 			LOG.lazyPropertyFetchingAvailable( name );
 		}
 
 		lazy = entityBinding.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				! hasPojoRepresentation ||
 				! ReflectHelper.isFinalClass( proxyInterfaceClass )
 		);
 		mutable = entityBinding.isMutable();
 		if ( entityBinding.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = hasPojoRepresentation &&
 			             ReflectHelper.isAbstractClass( mappedClass );
 		}
 		else {
 			isAbstract = entityBinding.isAbstract().booleanValue();
 			if ( !isAbstract && hasPojoRepresentation &&
 					ReflectHelper.isAbstractClass( mappedClass ) ) {
 				LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = entityBinding.isSelectBeforeUpdate();
 		dynamicUpdate = entityBinding.isDynamicUpdate();
 		dynamicInsert = entityBinding.isDynamicInsert();
 
 		hasSubclasses = entityBinding.hasSubEntityBindings();
 		polymorphic = entityBinding.isPolymorphic();
 
 		explicitPolymorphism = entityBinding.getHierarchyDetails().isExplicitPolymorphism();
 		inherited = ! entityBinding.isRoot();
 		superclass = inherited ?
 				entityBinding.getEntity().getSuperType().getName() :
 				null;
 
 		optimisticLockStyle = entityBinding.getHierarchyDetails().getOptimisticLockStyle();
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 			subclassEntityNames.add( subEntityBinding.getEntity().getName() );
 			if ( subEntityBinding.getEntity().getClassReference() != null ) {
 				entityNameByInheritenceClassMap.put(
 						subEntityBinding.getEntity().getClassReference(),
 						subEntityBinding.getEntity().getName() );
 			}
 		}
 		subclassEntityNames.add( name );
 		if ( mappedClass != null ) {
 			entityNameByInheritenceClassMap.put( mappedClass, name );
 		}
 
 		entityMode = hasPojoRepresentation ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		Class<? extends EntityTuplizer> tuplizerClass = entityBinding.getCustomEntityTuplizerClass();
 
 		if ( tuplizerClass == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
 		}
 	}
 
-	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
+	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
-	private ValueInclusion determineInsertValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
+	private ValueInclusion determineInsertValueGenerationType(AttributeBinding mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialInsertComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialInsertComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
-	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
+	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
-	private ValueInclusion determineUpdateValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
+	private ValueInclusion determineUpdateValueGenerationType(AttributeBinding mappingProperty, NonIdentifierAttribute runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialUpdateComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialUpdateComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void mapPropertyToIndex(Property prop, int i) {
 		propertyIndexes.put( prop.getName(), i );
 		if ( prop.getValue() instanceof Component ) {
 			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
 			while ( iter.hasNext() ) {
 				Property subprop = (Property) iter.next();
 				propertyIndexes.put(
 						prop.getName() + '.' + subprop.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	private void mapPropertyToIndex(Attribute attribute, int i) {
 		propertyIndexes.put( attribute.getName(), i );
 		if ( attribute.isSingular() &&
 				( ( SingularAttribute ) attribute ).getSingularAttributeType().isComponent() ) {
 			org.hibernate.metamodel.domain.Component component =
 					( org.hibernate.metamodel.domain.Component ) ( ( SingularAttribute ) attribute ).getSingularAttributeType();
 			for ( Attribute subAttribute : component.attributes() ) {
 				propertyIndexes.put(
 						attribute.getName() + '.' + subAttribute.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	public EntityTuplizer getTuplizer() {
 		return entityTuplizer;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return naturalIdPropertyNumbers;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return naturalIdPropertyNumbers!=null;
 	}
 	
 	public boolean isNaturalIdentifierCached() {
 		return hasNaturalIdentifier() && hasCacheableNaturalId;
 	}
 
 	public boolean hasImmutableNaturalId() {
 		return hasImmutableNaturalId;
 	}
 
 	public Set getSubclassEntityNames() {
 		return subclassEntityNames;
 	}
 
 	private boolean indicatesCollection(Type type) {
 		if ( type.isCollectionType() ) {
 			return true;
 		}
 		else if ( type.isComponentType() ) {
 			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
 			for ( int i = 0; i < subtypes.length; i++ ) {
 				if ( indicatesCollection( subtypes[i] ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getRootName() {
 		return rootName;
 	}
 
 	public EntityType getEntityType() {
 		return entityType;
 	}
 
 	public IdentifierProperty getIdentifierProperty() {
-		return identifierProperty;
+		return identifierAttribute;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public int getVersionPropertyIndex() {
 		return versionPropertyIndex;
 	}
 
 	public VersionProperty getVersionProperty() {
 		if ( NO_VERSION_INDX == versionPropertyIndex ) {
 			return null;
 		}
 		else {
 			return ( VersionProperty ) properties[ versionPropertyIndex ];
 		}
 	}
 
-	public StandardProperty[] getProperties() {
+	public NonIdentifierAttribute[] getProperties() {
 		return properties;
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			throw new HibernateException("Unable to resolve property: " + propertyName);
 		}
 		return index;
 	}
 
 	public Integer getPropertyIndexOrNull(String propertyName) {
 		return propertyIndexes.get( propertyName );
 	}
 
 	public boolean hasCollections() {
 		return hasCollections;
 	}
 
 	public boolean hasMutableProperties() {
 		return hasMutableProperties;
 	}
 
 	public boolean hasNonIdentifierPropertyNamedId() {
 		return hasNonIdentifierPropertyNamedId;
 	}
 
 	public boolean hasLazyProperties() {
 		return hasLazyProperties;
 	}
 
 	public boolean hasCascades() {
 		return hasCascades;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public String getSuperclass() {
 		return superclass;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public boolean isInherited() {
 		return inherited;
 	}
 
 	public boolean hasSubclasses() {
 		return hasSubclasses;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	/**
 	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
 	 *
 	 * @param inheritenceClass The class for which to resolve the entity-name.
 	 * @return The mapped entity-name, or null if no such mapping was found.
 	 */
 	public String findEntityNameByEntityClass(Class inheritenceClass) {
 		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
 	}
 
 	@Override
     public String toString() {
 		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Type[] getPropertyTypes() {
 		return propertyTypes;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return propertyLaziness;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return propertyUpdateability;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return propertyCheckability;
 	}
 
 	public boolean[] getNonlazyPropertyUpdateability() {
 		return nonlazyPropertyUpdateability;
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return propertyInsertability;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return insertInclusions;
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return updateInclusions;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return propertyVersionability;
 	}
 
 	public CascadeStyle[] getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	public boolean hasInsertGeneratedValues() {
 		return hasInsertGeneratedValues;
 	}
 
 	public boolean hasUpdateGeneratedValues() {
 		return hasUpdateGeneratedValues;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	/**
 	 * Whether or not this class can be lazy (ie intercepted)
 	 */
 	public boolean isInstrumented() {
 		return instrumentationMetadata.isInstrumented();
 	}
 
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return instrumentationMetadata;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/VersionProperty.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/VersionProperty.java
new file mode 100644
index 0000000000..51a8e67d4e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/VersionProperty.java
@@ -0,0 +1,70 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.tuple.entity;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.VersionValue;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.tuple.AbstractNonIdentifierAttribute;
+import org.hibernate.tuple.BaselineAttributeInformation;
+import org.hibernate.type.Type;
+
+/**
+ * Represents a version property within the Hibernate runtime-metamodel.
+ *
+ * @author Steve Ebersole
+ */
+public class VersionProperty extends AbstractNonIdentifierAttribute {
+
+    private final VersionValue unsavedValue;
+
+	/**
+	 * Constructs VersionProperty instances.
+	 *
+	 * @param source Reference back to the source of this attribute (the persister)
+	 * @param sessionFactory The session factory this is part of.
+	 * @param attributeNumber The attribute number within thje
+	 * @param attributeName The name by which the property can be referenced within
+	 * its owner.
+	 * @param attributeType The Hibernate Type of this property.
+	 * @param attributeInformation The basic attribute information.
+	 * @param unsavedValue The value which, if found as the value of
+	 * this (i.e., the version) property, represents new (i.e., un-saved)
+	 * instances of the owning entity.
+	 */
+	public VersionProperty(
+			EntityPersister source,
+			SessionFactoryImplementor sessionFactory,
+			int attributeNumber,
+			String attributeName,
+			Type attributeType,
+			BaselineAttributeInformation attributeInformation, VersionValue unsavedValue) {
+		super( source, sessionFactory, attributeNumber, attributeName, attributeType, attributeInformation );
+		this.unsavedValue = unsavedValue;
+	}
+
+    public VersionValue getUnsavedValue() {
+        return unsavedValue;
+    }
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
index df23ae8603..126e792d2a 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java
@@ -1,363 +1,363 @@
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
 
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.property.BackrefPropertyAccessor;
-import org.hibernate.tuple.StandardProperty;
+import org.hibernate.tuple.NonIdentifierAttribute;
 
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
 					target[i] = types[i].deepCopy( values[i], session
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
-			final StandardProperty[] properties,
+			final NonIdentifierAttribute[] properties,
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
-			final StandardProperty[] properties,
+			final NonIdentifierAttribute[] properties,
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
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
new file mode 100644
index 0000000000..597b900344
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
@@ -0,0 +1,149 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+import java.util.List;
+
+import org.hibernate.engine.spi.CascadingActions;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.loader.plan.internal.CascadeLoadPlanBuilderStrategy;
+import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.testing.junit4.ExtraAssertions;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertNotNull;
+
+/**
+ * @author Steve Ebersole
+ */
+public class LoadPlanBuilderTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Message.class, Poster.class };
+	}
+
+	@Test
+	public void testSimpleBuild() {
+		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
+		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
+				sessionFactory(),
+				LoadQueryInfluencers.NONE,
+				"abc",
+				0
+		);
+		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
+		assertFalse( plan.hasAnyScalarReturns() );
+		assertEquals( 1, plan.getReturns().size() );
+		Return rtn = plan.getReturns().get( 0 );
+		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
+		assertEquals( "abc", entityReturn.getAlias() );
+		assertNotNull( entityReturn.getFetches() );
+		assertEquals( 1, entityReturn.getFetches().length );
+		Fetch fetch = entityReturn.getFetches()[0];
+		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
+		assertNotNull( entityFetch.getFetches() );
+		assertEquals( 0, entityFetch.getFetches().length );
+	}
+
+	@Test
+	public void testCascadeBasedBuild() {
+		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
+		CascadeLoadPlanBuilderStrategy strategy = new CascadeLoadPlanBuilderStrategy(
+				CascadingActions.MERGE,
+				sessionFactory(),
+				LoadQueryInfluencers.NONE,
+				"abc",
+				0
+		);
+		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
+		assertFalse( plan.hasAnyScalarReturns() );
+		assertEquals( 1, plan.getReturns().size() );
+		Return rtn = plan.getReturns().get( 0 );
+		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
+		assertEquals( "abc", entityReturn.getAlias() );
+		assertNotNull( entityReturn.getFetches() );
+		assertEquals( 1, entityReturn.getFetches().length );
+		Fetch fetch = entityReturn.getFetches()[0];
+		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
+		assertNotNull( entityFetch.getFetches() );
+		assertEquals( 0, entityFetch.getFetches().length );
+	}
+
+	@Test
+	public void testCollectionInitializerCase() {
+		CollectionPersister cp = sessionFactory().getCollectionPersister( Poster.class.getName() + ".messages" );
+		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
+				sessionFactory(),
+				LoadQueryInfluencers.NONE,
+				"abc",
+				0
+		);
+		LoadPlan plan = LoadPlanBuilder.buildRootCollectionLoadPlan( strategy, cp );
+		assertFalse( plan.hasAnyScalarReturns() );
+		assertEquals( 1, plan.getReturns().size() );
+		Return rtn = plan.getReturns().get( 0 );
+		CollectionReturn collectionReturn = ExtraAssertions.assertTyping( CollectionReturn.class, rtn );
+		assertEquals( "abc", collectionReturn.getAlias() );
+
+		assertNotNull( collectionReturn.getFetches() );
+		assertEquals( 1, collectionReturn.getFetches().length ); // the collection elements are fetched
+		Fetch fetch = collectionReturn.getFetches()[0];
+		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
+		assertNotNull( entityFetch.getFetches() );
+		assertEquals( 0, entityFetch.getFetches().length );
+	}
+
+	@Entity( name = "Message" )
+	public static class Message {
+		@Id
+		private Integer id;
+		private String name;
+		@ManyToOne( cascade = CascadeType.MERGE )
+		@JoinColumn
+		private Poster poster;
+	}
+
+	@Entity( name = "Poster" )
+	public static class Poster {
+		@Id
+		private Integer id;
+		private String name;
+		@OneToMany(mappedBy = "poster")
+		private List<Message> messages;
+	}
+
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
new file mode 100644
index 0000000000..07c193bb13
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
@@ -0,0 +1,177 @@
+/*
+ * jDocBook, processing of DocBook sources
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
+package org.hibernate.persister.walking;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+import java.util.List;
+
+import org.hibernate.annotations.common.util.StringHelper;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicWalkingTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Message.class, Poster.class };
+	}
+
+	@Test
+	public void testIt() {
+		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
+		MetadataDrivenModelGraphVisitor.visitEntity(
+				new AssociationVisitationStrategy() {
+					private int depth = 0;
+
+					@Override
+					public void start() {
+						System.out.println( ">> Start" );
+					}
+
+					@Override
+					public void finish() {
+						System.out.println( "<< Finish" );
+					}
+
+					@Override
+					public void startingEntity(EntityDefinition entityDefinition) {
+						System.out.println(
+								String.format(
+										"%s Starting entity (%s)",
+										StringHelper.repeat( ">>", ++depth ),
+										entityDefinition.toString()
+								)
+						);
+					}
+
+					@Override
+					public void finishingEntity(EntityDefinition entityDefinition) {
+						System.out.println(
+								String.format(
+										"%s Finishing entity (%s)",
+										StringHelper.repeat( "<<", depth-- ),
+										entityDefinition.toString()
+								)
+						);
+					}
+
+					@Override
+					public void startingCollection(CollectionDefinition collectionDefinition) {
+						System.out.println(
+								String.format(
+										"%s Starting collection (%s)",
+										StringHelper.repeat( ">>", ++depth ),
+										collectionDefinition.toString()
+								)
+						);
+					}
+
+					@Override
+					public void finishingCollection(CollectionDefinition collectionDefinition) {
+						System.out.println(
+								String.format(
+										"%s Finishing collection (%s)",
+										StringHelper.repeat( ">>", depth-- ),
+										collectionDefinition.toString()
+								)
+						);
+					}
+
+					@Override
+					public void startingComposite(CompositeDefinition compositeDefinition) {
+						System.out.println(
+								String.format(
+										"%s Starting composite (%s)",
+										StringHelper.repeat( ">>", ++depth ),
+										compositeDefinition.toString()
+								)
+						);
+					}
+
+					@Override
+					public void finishingComposite(CompositeDefinition compositeDefinition) {
+						System.out.println(
+								String.format(
+										"%s Finishing composite (%s)",
+										StringHelper.repeat( ">>", depth-- ),
+										compositeDefinition.toString()
+								)
+						);
+					}
+
+					@Override
+					public boolean startingAttribute(AttributeDefinition attributeDefinition) {
+						System.out.println(
+								String.format(
+										"%s Handling attribute (%s)",
+										StringHelper.repeat( ">>", depth + 1 ),
+										attributeDefinition.toString()
+								)
+						);
+						return true;
+					}
+
+					@Override
+					public void finishingAttribute(AttributeDefinition attributeDefinition) {
+						// nothing to do
+					}
+				},
+				ep
+		);
+	}
+
+	@Entity( name = "Message" )
+	public static class Message {
+		@Id
+		private Integer id;
+		private String name;
+		@ManyToOne
+		@JoinColumn
+		private Poster poster;
+	}
+
+	@Entity( name = "Poster" )
+	public static class Poster {
+		@Id
+		private Integer id;
+		private String name;
+		@OneToMany(mappedBy = "poster")
+		private List<Message> messages;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 3e32129981..6af678c8a5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,818 +1,852 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.cfg.persister;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
+import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CollectionElementDefinition;
+import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( null );
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 		
 		@Override
 		public boolean hasNaturalIdCache() {
 			return false;
 		}
 
 		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(
 				Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			// TODO Auto-generated method stub
 			return null;
 		}
+
+		@Override
+		public EntityPersister getEntityPersister() {
+			return this;
+		}
+
+		@Override
+		public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes() {
+			throw new NotYetImplementedException();
+		}
+
+		@Override
+		public Iterable<AttributeDefinition> getAttributes() {
+			throw new NotYetImplementedException();
+		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.spi.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   SessionFactoryImplementor sf) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
+		@Override
+		public CollectionPersister getCollectionPersister() {
+			return this;
+		}
+
 		public CollectionType getCollectionType() {
-			return null;  //To change body of implemented methods use File | Settings | File Templates.
+			throw new NotYetImplementedException();
+		}
+
+		@Override
+		public CollectionIndexDefinition getIndexDefinition() {
+			throw new NotYetImplementedException();
+		}
+
+		@Override
+		public CollectionElementDefinition getElementDefinition() {
+			throw new NotYetImplementedException();
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public int getBatchSize() {
 			return 0;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index 62258c377b..55b96323ab 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,674 +1,691 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
+import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping mapping) {
 		this.factory = factory;
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session ),
 					new PostLoadEvent( (EventSource) session )
 				);
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 	
 	public boolean hasNaturalIdCache() {
 		return false;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 		throw new UnsupportedOperationException( "not supported" );
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return new UnstructuredCacheEntry();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(
 			Object entity, Object[] state, Object version, SessionImplementor session) {
 		return new StandardCacheEntryImpl(
 				state,
 				this,
 				this.hasUninitializedLazyProperties( entity ),
 				version,
 				session,
 				entity
 		);
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	@Override
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session) {
 		return null;
 	}
 
 	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return new NonPojoInstrumentationMetadata( getEntityName() );
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return this;
+	}
+
+	@Override
+	public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes() {
+		throw new NotYetImplementedException();
+	}
+
+	@Override
+	public Iterable<AttributeDefinition> getAttributes() {
+		throw new NotYetImplementedException();
+	}
 }
