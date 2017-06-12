diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
index b5c1243515..d2b4a1ff90 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
@@ -1,114 +1,117 @@
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
 package org.hibernate.loader.internal;
 import java.util.List;
 
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.Select;
 
 /**
  * Abstract walker for walkers which begin at an entity (criteria
  * queries and entity loaders).
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityLoadQueryImpl extends AbstractLoadQueryImpl {
 
 	private final EntityReturn entityReturn;
 
 	public AbstractEntityLoadQueryImpl(
 			SessionFactoryImplementor factory,
 			EntityReturn entityReturn,
-			List<JoinableAssociationImpl> associations,
-			List<String> suffixes) {
-		super( factory, associations, suffixes );
+			List<JoinableAssociationImpl> associations) {
+		super( factory, associations );
 		this.entityReturn = entityReturn;
 	}
 
 	protected final String generateSql(
 			final String whereString,
 			final String orderByString,
 			final LockOptions lockOptions) throws MappingException {
 		return generateSql( null, whereString, orderByString, "", lockOptions );
 	}
 
 	private String generateSql(
 			final String projection,
 			final String condition,
 			final String orderBy,
 			final String groupBy,
 			final LockOptions lockOptions) throws MappingException {
 
 		JoinFragment ojf = mergeOuterJoins();
 
+		// If no projection, then the last suffix should be for the entity return.
+		// TODO: simplify how suffixes are generated/processed.
+
+
 		Select select = new Select( getDialect() )
 				.setLockOptions( lockOptions )
 				.setSelectClause(
 						projection == null ?
 								getPersister().selectFragment( getAlias(), entityReturn.getEntityAliases().getSuffix() ) + associationSelectString() :
 								projection
 				)
 				.setFromClause(
 						getDialect().appendLockHint( lockOptions, getPersister().fromTableFragment( getAlias() ) ) +
 								getPersister().fromJoinFragment( getAlias(), true, true )
 				)
 				.setWhereClause( condition )
 				.setOuterJoins(
 						ojf.toFromFragmentString(),
 						ojf.toWhereFragmentString() + getWhereFragment()
 				)
 				.setOrderByClause( orderBy( orderBy ) )
 				.setGroupByClause( groupBy );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( getComment() );
 		}
 		return select.toStatementString();
 	}
 
 	protected String getWhereFragment() throws MappingException {
 		// here we do not bother with the discriminator.
 		return getPersister().whereJoinFragment( getAlias(), true, true );
 	}
 
 	public abstract String getComment();
 
 	public final OuterJoinLoadable getPersister() {
 		return (OuterJoinLoadable) entityReturn.getEntityPersister();
 	}
 
 	public final String getAlias() {
 		return entityReturn.getSqlTableAlias();
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getPersister().getEntityName() + ')';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
index 1ccd8ff2b8..e934434b6b 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
@@ -1,241 +1,208 @@
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
 package org.hibernate.loader.internal;
-import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.sql.ConditionFragment;
 import org.hibernate.sql.DisjunctionFragment;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 
 /**
  * Walks the metamodel, searching for joins, and collecting
  * together information needed by <tt>OuterJoinLoader</tt>.
  * 
  * @see org.hibernate.loader.OuterJoinLoader
  * @author Gavin King, Jon Lipsky
  */
 public abstract class AbstractLoadQueryImpl {
 
 	private final SessionFactoryImplementor factory;
 	private final List<JoinableAssociationImpl> associations;
-	private final List<String> suffixes;
-
-	private String[] collectionSuffixes;
 
 	protected AbstractLoadQueryImpl(
 			SessionFactoryImplementor factory,
-			List<JoinableAssociationImpl> associations,
-			List<String> suffixes) {
+			List<JoinableAssociationImpl> associations) {
 		this.factory = factory;
 		this.associations = associations;
-		// TODO: we should be able to get the suffixes out of associations.
-		this.suffixes = suffixes;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected Dialect getDialect() {
 		return factory.getDialect();
 	}
 
 	protected String orderBy(final String orderBy) {
 		return mergeOrderings( orderBy( associations ), orderBy );
 	}
 
 	protected static String mergeOrderings(String ordering1, String ordering2) {
 		if ( ordering1.length() == 0 ) {
 			return ordering2;
 		}
 		else if ( ordering2.length() == 0 ) {
 			return ordering1;
 		}
 		else {
 			return ordering1 + ", " + ordering2;
 		}
 	}
 
 	/**
 	 * Generate a sequence of <tt>LEFT OUTER JOIN</tt> clauses for the given associations.
 	 */
 	protected final JoinFragment mergeOuterJoins()
 	throws MappingException {
 		JoinFragment outerjoin = getDialect().createOuterJoinFragment();
 		JoinableAssociationImpl last = null;
 		for ( JoinableAssociationImpl oj : associations ) {
 			if ( last != null && last.isManyToManyWith( oj ) ) {
 				oj.addManyToManyJoin( outerjoin, ( QueryableCollection ) last.getJoinable() );
 			}
 			else {
 				oj.addJoins(outerjoin);
 			}
 			last = oj;
 		}
 		return outerjoin;
 	}
 
 	/**
-	 * Count the number of instances of Joinable which are actually
-	 * also instances of PersistentCollection which are being fetched
-	 * by outer join
-	 */
-	protected static final int countCollectionPersisters(List associations)
-	throws MappingException {
-		int result = 0;
-		Iterator iter = associations.iterator();
-		while ( iter.hasNext() ) {
-			JoinableAssociationImpl oj = (JoinableAssociationImpl) iter.next();
-			if ( oj.getJoinType()==JoinType.LEFT_OUTER_JOIN &&
-					oj.getJoinable().isCollection() &&
-					! oj.hasRestriction() ) {
-				result++;
-			}
-		}
-		return result;
-	}
-	
-	/**
 	 * Get the order by string required for collection fetching
 	 */
-	protected static final String orderBy(List<JoinableAssociationImpl> associations)
+	protected static String orderBy(List<JoinableAssociationImpl> associations)
 	throws MappingException {
 		StringBuilder buf = new StringBuilder();
 		JoinableAssociationImpl last = null;
 		for ( JoinableAssociationImpl oj : associations ) {
 			if ( oj.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
 				if ( oj.getJoinable().isCollection() ) {
 					final QueryableCollection queryableCollection = (QueryableCollection) oj.getJoinable();
 					if ( queryableCollection.hasOrdering() ) {
 						final String orderByString = queryableCollection.getSQLOrderByString( oj.getRHSAlias() );
 						buf.append( orderByString ).append(", ");
 					}
 				}
 				else {
 					// it might still need to apply a collection ordering based on a
 					// many-to-many defined order-by...
 					if ( last != null && last.getJoinable().isCollection() ) {
 						final QueryableCollection queryableCollection = (QueryableCollection) last.getJoinable();
 						if ( queryableCollection.isManyToMany() && last.isManyToManyWith( oj ) ) {
 							if ( queryableCollection.hasManyToManyOrdering() ) {
 								final String orderByString = queryableCollection.getManyToManyOrderByString( oj.getRHSAlias() );
 								buf.append( orderByString ).append(", ");
 							}
 						}
 					}
 				}
 			}
 			last = oj;
 		}
-		if ( buf.length()>0 ) buf.setLength( buf.length()-2 );
+		if ( buf.length() > 0 ) {
+			buf.setLength( buf.length() - 2 );
+		}
 		return buf.toString();
 	}
 
 	/**
 	 * Render the where condition for a (batch) load by identifier / collection key
 	 */
 	protected StringBuilder whereString(String alias, String[] columnNames, int batchSize) {
 		if ( columnNames.length==1 ) {
 			// if not a composite key, use "foo in (?, ?, ?)" for batching
 			// if no batch, and not a composite key, use "foo = ?"
 			InFragment in = new InFragment().setColumn( alias, columnNames[0] );
-			for ( int i=0; i<batchSize; i++ ) in.addValue("?");
+			for ( int i = 0; i < batchSize; i++ ) {
+				in.addValue( "?" );
+			}
 			return new StringBuilder( in.toFragmentString() );
 		}
 		else {
 			//a composite key
 			ConditionFragment byId = new ConditionFragment()
 					.setTableAlias(alias)
 					.setCondition( columnNames, "?" );
 	
 			StringBuilder whereString = new StringBuilder();
 			if ( batchSize==1 ) {
 				// if no batch, use "foo = ? and bar = ?"
 				whereString.append( byId.toFragmentString() );
 			}
 			else {
 				// if a composite key, use "( (foo = ? and bar = ?) or (foo = ? and bar = ?) )" for batching
 				whereString.append('('); //TODO: unnecessary for databases with ANSI-style joins
 				DisjunctionFragment df = new DisjunctionFragment();
 				for ( int i=0; i<batchSize; i++ ) {
 					df.addCondition(byId);
 				}
 				whereString.append( df.toFragmentString() );
 				whereString.append(')'); //TODO: unnecessary for databases with ANSI-style joins
 			}
 			return whereString;
 		}
 	}
 
 	/**
 	 * Generate a select list of columns containing all properties of the entity classes
 	 */
 	protected final String associationSelectString()
 	throws MappingException {
 
 		if ( associations.size() == 0 ) {
 			return "";
 		}
 		else {
 			StringBuilder buf = new StringBuilder( associations.size() * 100 );
-			int entityAliasCount=0;
-			int collectionAliasCount=0;
 			for ( int i=0; i<associations.size(); i++ ) {
 				JoinableAssociationImpl join = associations.get(i);
 				JoinableAssociationImpl next = (i == associations.size() - 1)
 				        ? null
 				        : associations.get( i + 1 );
 				final Joinable joinable = join.getJoinable();
-				final String entitySuffix = ( suffixes == null || entityAliasCount >= suffixes.size() )
-				        ? null
-				        : suffixes.get( entityAliasCount );
-				final String collectionSuffix = ( collectionSuffixes == null || collectionAliasCount >= collectionSuffixes.length )
-				        ? null
-				        : collectionSuffixes[collectionAliasCount];
 				final String selectFragment = joinable.selectFragment(
 						next == null ? null : next.getJoinable(),
 						next == null ? null : next.getRHSAlias(),
 						join.getRHSAlias(),
-						entitySuffix,
-				        collectionSuffix,
+						associations.get( i ).getCurrentEntitySuffix(),
+						associations.get( i ).getCurrentCollectionSuffix(),
 						join.getJoinType()==JoinType.LEFT_OUTER_JOIN
 				);
 				if (selectFragment.trim().length() > 0) {
 					buf.append(", ").append(selectFragment);
 				}
-				if ( joinable.consumesEntityAlias() ) entityAliasCount++;
-				if ( joinable.consumesCollectionAlias() && join.getJoinType()==JoinType.LEFT_OUTER_JOIN ) collectionAliasCount++;
 			}
 			return buf.toString();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
index e0d6e2246f..be11977031 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
@@ -1,140 +1,209 @@
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
 
+import java.util.ArrayDeque;
 import java.util.ArrayList;
+import java.util.Deque;
 import java.util.List;
 
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.LoadPlanVisitor;
+import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.LoadQueryBuilder;
 import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.persister.walking.spi.WalkingException;
 
 /**
  * @author Gail Badner
  */
 public class EntityLoadQueryBuilderImpl implements LoadQueryBuilder {
 	private final SessionFactoryImplementor sessionFactory;
 	private final LoadQueryInfluencers loadQueryInfluencers;
 	private final LoadPlan loadPlan;
 	private final List<JoinableAssociationImpl> associations;
-	private final List<String> suffixes;
 
 	public EntityLoadQueryBuilderImpl(
 			SessionFactoryImplementor sessionFactory,
 			LoadQueryInfluencers loadQueryInfluencers,
 			LoadPlan loadPlan) {
 		this.sessionFactory = sessionFactory;
 		this.loadQueryInfluencers = loadQueryInfluencers;
 		this.loadPlan = loadPlan;
 		LocalVisitationStrategy strategy = new LocalVisitationStrategy();
 		LoadPlanVisitor.visit( loadPlan, strategy );
 		this.associations = strategy.associations;
-		this.suffixes = strategy.suffixes;
 	}
 
 	@Override
 	public String generateSql(int batchSize) {
 		return generateSql( batchSize, getOuterJoinLoadable().getKeyColumnNames() );
 	}
 
 	public String generateSql(int batchSize, String[] uniqueKey) {
 		final EntityLoadQueryImpl loadQuery = new EntityLoadQueryImpl(
 				sessionFactory,
 				getRootEntityReturn(),
-				associations,
-				suffixes
+				associations
 		);
 		return loadQuery.generateSql( uniqueKey, batchSize, getRootEntityReturn().getLockMode() );
 	}
 
 	private EntityReturn getRootEntityReturn() {
 		return (EntityReturn) loadPlan.getReturns().get( 0 );
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable() {
 		return (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 	}
 	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
 		private final List<JoinableAssociationImpl> associations = new ArrayList<JoinableAssociationImpl>();
-		private final List<String> suffixes = new ArrayList<String>();
+		private Deque<EntityAliases> entityAliasStack = new ArrayDeque<EntityAliases>();
+		private Deque<CollectionAliases> collectionAliasStack = new ArrayDeque<CollectionAliases>();
 
 		private EntityReturn entityRootReturn;
 
 		@Override
 		public void handleEntityReturn(EntityReturn rootEntityReturn) {
 			this.entityRootReturn = rootEntityReturn;
 		}
 
 		@Override
+		public void startingRootReturn(Return rootReturn) {
+			if ( !EntityReturn.class.isInstance( rootReturn ) ) {
+				throw new WalkingException(
+						String.format(
+								"Unexpected type of return; expected [%s]; instead it was [%s]",
+								EntityReturn.class.getName(),
+								rootReturn.getClass().getName()
+						)
+				);
+			}
+			this.entityRootReturn = (EntityReturn) rootReturn;
+			pushToStack( entityAliasStack, entityRootReturn.getEntityAliases() );
+		}
+
+		@Override
+		public void finishingRootReturn(Return rootReturn) {
+			if ( !EntityReturn.class.isInstance( rootReturn ) ) {
+				throw new WalkingException(
+						String.format(
+								"Unexpected type of return; expected [%s]; instead it was [%s]",
+								EntityReturn.class.getName(),
+								rootReturn.getClass().getName()
+						)
+				);
+			}
+			popFromStack( entityAliasStack, ( (EntityReturn) rootReturn ).getEntityAliases() );
+		}
+
+		@Override
 		public void startingEntityFetch(EntityFetch entityFetch) {
 			JoinableAssociationImpl assoc = new JoinableAssociationImpl(
 					entityFetch,
+					getCurrentCollectionSuffix(),
 					"",    // getWithClause( entityFetch.getPropertyPath() )
 					false, // hasRestriction( entityFetch.getPropertyPath() )
 					sessionFactory,
 					loadQueryInfluencers.getEnabledFilters()
 			);
 			associations.add( assoc );
-			suffixes.add( entityFetch.getEntityAliases().getSuffix() );
+			pushToStack( entityAliasStack, entityFetch.getEntityAliases() );
 		}
 
 		@Override
 		public void finishingEntityFetch(EntityFetch entityFetch) {
-			//To change body of implemented methods use File | Settings | File Templates.
+			popFromStack( entityAliasStack, entityFetch.getEntityAliases() );
 		}
 
 		@Override
 		public void startingCollectionFetch(CollectionFetch collectionFetch) {
-			//To change body of implemented methods use File | Settings | File Templates.
+			JoinableAssociationImpl assoc = new JoinableAssociationImpl(
+					collectionFetch,
+					getCurrentEntitySuffix(),
+					"",    // getWithClause( entityFetch.getPropertyPath() )
+					false, // hasRestriction( entityFetch.getPropertyPath() )
+					sessionFactory,
+					loadQueryInfluencers.getEnabledFilters()
+			);
+			associations.add( assoc );
+			pushToStack( collectionAliasStack, collectionFetch.getCollectionAliases() );
 		}
 
 		@Override
 		public void finishingCollectionFetch(CollectionFetch collectionFetch) {
-			//To change body of implemented methods use File | Settings | File Templates.
+			popFromStack( collectionAliasStack, collectionFetch.getCollectionAliases() );
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
-			//suffixes.add( entityRootReturn.getEntityAliases().getSuffix() );
+			entityAliasStack.clear();
+			collectionAliasStack.clear();
+		}
+
+		private String getCurrentEntitySuffix() {
+			return entityAliasStack.peekFirst() == null ? null : entityAliasStack.peekFirst().getSuffix();
+		}
+
+		private String getCurrentCollectionSuffix() {
+			return collectionAliasStack.peekFirst() == null ? null : collectionAliasStack.peekFirst().getSuffix();
+		}
+
+		private <T> void pushToStack(Deque<T> stack, T value) {
+			stack.push( value );
+		}
+
+		private <T> void popFromStack(Deque<T> stack, T expectedValue) {
+			T poppedValue = stack.pop();
+			if ( poppedValue != expectedValue ) {
+				throw new WalkingException(
+						String.format(
+								"Unexpected value from stack. Expected=[%s]; instead it was [%s].",
+								expectedValue,
+								poppedValue
+						)
+				);
+			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
index a5fe1f22d4..2eedf8a88f 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
@@ -1,61 +1,60 @@
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
 package org.hibernate.loader.internal;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.spi.EntityReturn;
 
 /**
  * A walker for loaders that fetch entities
  *
  * @see org.hibernate.loader.entity.EntityLoader
  * @author Gavin King
  */
 public class EntityLoadQueryImpl extends AbstractEntityLoadQueryImpl {
 
 	public EntityLoadQueryImpl(
 			final SessionFactoryImplementor factory,
 			EntityReturn entityReturn,
-			List<JoinableAssociationImpl> associations,
-			List<String> suffixes) throws MappingException {
-		super( factory, entityReturn, associations, suffixes );
+			List<JoinableAssociationImpl> associations) throws MappingException {
+		super( factory, entityReturn, associations );
 	}
 
 	public String generateSql(String[] uniqueKey, int batchSize, LockMode lockMode) {
 		StringBuilder whereCondition = whereString( getAlias(), uniqueKey, batchSize )
 				//include the discriminator and class-level where, but not filters
 				.append( getPersister().filterFragment( getAlias(), Collections.EMPTY_MAP ) );
 		return generateSql( whereCondition.toString(), "",  new LockOptions().setLockMode( lockMode ) );
 	}
 
 	public String getComment() {
 		return "load " + getPersister().getEntityName();
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
index c853dba050..da8361304e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
@@ -1,227 +1,285 @@
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
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
+import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 
 /**
  * Part of the Hibernate SQL rendering internals.  This class represents
  * a joinable association.
  *
  * @author Gavin King
  * @author Gail Badner
  */
 public final class JoinableAssociationImpl {
 	private final PropertyPath propertyPath;
 	private final AssociationType joinableType;
 	private final Joinable joinable;
 	private final String lhsAlias; // belong to other persister
 	private final String[] lhsColumns; // belong to other persister
 	private final String rhsAlias;
 	private final String[] rhsColumns;
+	private final String currentEntitySuffix;
+	private final String currentCollectionSuffix;
 	private final JoinType joinType;
 	private final String on;
 	private final Map enabledFilters;
 	private final boolean hasRestriction;
 
 	public JoinableAssociationImpl(
 			EntityFetch entityFetch,
+			String currentCollectionSuffix,
 			String withClause,
 			boolean hasRestriction,
 			SessionFactoryImplementor factory,
 			Map enabledFilters) throws MappingException {
 		this.propertyPath = entityFetch.getPropertyPath();
 		this.joinableType = entityFetch.getAssociationType();
 		// TODO: this is not correct
 		final EntityPersister fetchSourcePersister = entityFetch.getOwner().retrieveFetchSourcePersister();
 		final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( entityFetch.getOwnerPropertyName() );
 
 		if ( EntityReference.class.isInstance( entityFetch.getOwner() ) ) {
 			this.lhsAlias = ( (EntityReference) entityFetch.getOwner() ).getSqlTableAlias();
 		}
 		else {
 			throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
 		}
 		final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) entityFetch.getOwner().retrieveFetchSourcePersister();
 		this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
 				entityFetch.getAssociationType(), lhsAlias, propertyNumber, ownerPersister, factory
 		);
 		this.rhsAlias = entityFetch.getSqlTableAlias();
 
 		final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
 		if ( entityFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
 			joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
 		}
 		else {
 			joinType = JoinType.NONE;
 		}
 		this.joinable = joinableType.getAssociatedJoinable(factory);
-		this.rhsColumns = JoinHelper.getRHSColumnNames(joinableType, factory);
+		this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
+		this.currentEntitySuffix = entityFetch.getEntityAliases().getSuffix();
+		this.currentCollectionSuffix = currentCollectionSuffix;
+		this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
+				+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
+		this.hasRestriction = hasRestriction;
+		this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
+	}
+
+	public JoinableAssociationImpl(
+			CollectionFetch collectionFetch,
+			String currentEntitySuffix,
+			String withClause,
+			boolean hasRestriction,
+			SessionFactoryImplementor factory,
+			Map enabledFilters) throws MappingException {
+		this.propertyPath = collectionFetch.getPropertyPath();
+		final CollectionType collectionType =  collectionFetch.getCollectionPersister().getCollectionType();
+		this.joinableType = collectionType;
+		// TODO: this is not correct
+		final EntityPersister fetchSourcePersister = collectionFetch.getOwner().retrieveFetchSourcePersister();
+		final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( collectionFetch.getOwnerPropertyName() );
+
+		if ( EntityReference.class.isInstance( collectionFetch.getOwner() ) ) {
+			this.lhsAlias = ( (EntityReference) collectionFetch.getOwner() ).getSqlTableAlias();
+		}
+		else {
+			throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
+		}
+		final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) collectionFetch.getOwner().retrieveFetchSourcePersister();
+		this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
+				collectionType, lhsAlias, propertyNumber, ownerPersister, factory
+		);
+		this.rhsAlias = collectionFetch.getAlias();
+
+		final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
+		if ( collectionFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
+			joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
+		}
+		else {
+			joinType = JoinType.NONE;
+		}
+		this.joinable = joinableType.getAssociatedJoinable(factory);
+		this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
+		this.currentEntitySuffix = currentEntitySuffix;
+		this.currentCollectionSuffix = collectionFetch.getCollectionAliases().getSuffix();
 		this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
 				+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
 		this.hasRestriction = hasRestriction;
 		this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
 	}
 
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	public JoinType getJoinType() {
 		return joinType;
 	}
 
 	public String getLhsAlias() {
 		return lhsAlias;
 	}
 
 	public String getRHSAlias() {
 		return rhsAlias;
 	}
 
+	public String getCurrentEntitySuffix() {
+		return currentEntitySuffix;
+	}
+
+	public String getCurrentCollectionSuffix() {
+		return currentCollectionSuffix;
+	}
+
 	private boolean isOneToOne() {
 		if ( joinableType.isEntityType() )  {
 			EntityType etype = (EntityType) joinableType;
 			return etype.isOneToOne() /*&& etype.isReferenceToPrimaryKey()*/;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public AssociationType getJoinableType() {
 		return joinableType;
 	}
 
 	public String getRHSUniqueKeyName() {
 		return joinableType.getRHSUniqueKeyPropertyName();
 	}
 
 	public boolean isCollection() {
 		return joinableType.isCollectionType();
 	}
 
 	public Joinable getJoinable() {
 		return joinable;
 	}
 
 	public boolean hasRestriction() {
 		return hasRestriction;
 	}
 
 	public int getOwner(final List associations) {
 		if ( isOneToOne() || isCollection() ) {
 			return getPosition(lhsAlias, associations);
 		}
 		else {
 			return -1;
 		}
 	}
 
 	/**
 	 * Get the position of the join with the given alias in the
 	 * list of joins
 	 */
 	private static int getPosition(String lhsAlias, List associations) {
 		int result = 0;
 		for ( int i=0; i<associations.size(); i++ ) {
 			JoinableAssociationImpl oj = (JoinableAssociationImpl) associations.get(i);
 			if ( oj.getJoinable().consumesEntityAlias() /*|| oj.getJoinable().consumesCollectionAlias() */ ) {
 				if ( oj.rhsAlias.equals(lhsAlias) ) return result;
 				result++;
 			}
 		}
 		return -1;
 	}
 
 	public void addJoins(JoinFragment outerjoin) throws MappingException {
 		outerjoin.addJoin(
 				joinable.getTableName(),
 				rhsAlias,
 				lhsColumns,
 				rhsColumns,
 				joinType,
 				on
 		);
 		outerjoin.addJoins(
 				joinable.fromJoinFragment(rhsAlias, false, true),
 				joinable.whereJoinFragment(rhsAlias, false, true)
 		);
 	}
 
 	public void validateJoin(String path) throws MappingException {
 		if ( rhsColumns==null || lhsColumns==null
 				|| lhsColumns.length!=rhsColumns.length || lhsColumns.length==0 ) {
 			throw new MappingException("invalid join columns for association: " + path);
 		}
 	}
 
 	public boolean isManyToManyWith(JoinableAssociationImpl other) {
 		if ( joinable.isCollection() ) {
 			QueryableCollection persister = ( QueryableCollection ) joinable;
 			if ( persister.isManyToMany() ) {
 				return persister.getElementType() == other.getJoinableType();
 			}
 		}
 		return false;
 	}
 
 	public void addManyToManyJoin(JoinFragment outerjoin, QueryableCollection collection) throws MappingException {
 		String manyToManyFilter = collection.getManyToManyFilterFragment( rhsAlias, enabledFilters );
 		String condition = "".equals( manyToManyFilter )
 				? on
 				: "".equals( on )
 				? manyToManyFilter
 				: on + " and " + manyToManyFilter;
 		outerjoin.addJoin(
 				joinable.getTableName(),
 				rhsAlias,
 				lhsColumns,
 				rhsColumns,
 				joinType,
 				condition
 		);
 		outerjoin.addJoins(
 				joinable.fromJoinFragment(rhsAlias, false, true),
 				joinable.whereJoinFragment(rhsAlias, false, true)
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
index 1163ef8125..a66e8acbc9 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
@@ -1,685 +1,794 @@
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
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
+import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
+import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.LoadPlanVisitor;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessingContextImpl implements ResultSetProcessingContext {
 	private static final Logger LOG = Logger.getLogger( ResultSetProcessingContextImpl.class );
 
 	private final ResultSet resultSet;
 	private final SessionImplementor session;
 	private final LoadPlan loadPlan;
 	private final boolean readOnly;
 	private final QueryParameters queryParameters;
 	private final NamedParameterContext namedParameterContext;
 	private final boolean hadSubselectFetches;
 
 	private final EntityKey dictatedRootEntityKey;
 
 	private List<HydratedEntityRegistration> currentRowHydratedEntityRegistrationList;
 
 	private Map<EntityPersister,Set<EntityKey>> subselectLoadableEntityKeyMap;
 	private List<HydratedEntityRegistration> hydratedEntityRegistrationList;
 
 	public ResultSetProcessingContextImpl(
 			ResultSet resultSet,
 			SessionImplementor session,
 			LoadPlan loadPlan,
 			boolean readOnly,
 			boolean useOptionalEntityKey,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
 			boolean hadSubselectFetches) {
 		this.resultSet = resultSet;
 		this.session = session;
 		this.loadPlan = loadPlan;
 		this.readOnly = readOnly;
 		this.queryParameters = queryParameters;
 		this.namedParameterContext = namedParameterContext;
 		this.hadSubselectFetches = hadSubselectFetches;
 
 		if ( useOptionalEntityKey ) {
 			this.dictatedRootEntityKey = ResultSetProcessorHelper.getOptionalObjectKey( queryParameters, session );
 			if ( this.dictatedRootEntityKey == null ) {
 				throw new HibernateException( "Unable to resolve optional entity-key" );
 			}
 		}
 		else {
 			this.dictatedRootEntityKey = null;
 		}
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	@Override
 	public EntityKey getDictatedRootEntityKey() {
 		return dictatedRootEntityKey;
 	}
 
 	private Map<EntityReference,IdentifierResolutionContext> identifierResolutionContextMap;
 
 	@Override
 	public IdentifierResolutionContext getIdentifierResolutionContext(final EntityReference entityReference) {
 		if ( identifierResolutionContextMap == null ) {
 			identifierResolutionContextMap = new HashMap<EntityReference, IdentifierResolutionContext>();
 		}
 		IdentifierResolutionContext context = identifierResolutionContextMap.get( entityReference );
 		if ( context == null ) {
 			context = new IdentifierResolutionContext() {
 				private Object hydratedForm;
 				private EntityKey entityKey;
 
 				@Override
 				public EntityReference getEntityReference() {
 					return entityReference;
 				}
 
 				@Override
 				public void registerHydratedForm(Object hydratedForm) {
 					if ( this.hydratedForm != null ) {
 						// this could be bad...
 					}
 					this.hydratedForm = hydratedForm;
 				}
 
 				@Override
 				public Object getHydratedForm() {
 					return hydratedForm;
 				}
 
 				@Override
 				public void registerEntityKey(EntityKey entityKey) {
 					if ( this.entityKey != null ) {
 						// again, could be trouble...
 					}
 					this.entityKey = entityKey;
 				}
 
 				@Override
 				public EntityKey getEntityKey() {
 					return entityKey;
 				}
 			};
 			identifierResolutionContextMap.put( entityReference, context );
 		}
 
 		return context;
 	}
 
 	@Override
 	public Set<IdentifierResolutionContext> getIdentifierResolutionContexts() {
 		return Collections.unmodifiableSet(
 				new HashSet<IdentifierResolutionContext>( identifierResolutionContextMap.values() )
 		);
 	}
 
 	@Override
 	public void checkVersion(
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
 				throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
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
 	public String getConcreteEntityTypeName(
 			final ResultSet rs,
 			final EntityPersister persister,
 			final EntityAliases entityAliases,
 			final EntityKey entityKey) {
 
 		final Loadable loadable = (Loadable) persister;
 		if ( ! loadable.hasSubclasses() ) {
 			return persister.getEntityName();
 		}
 
 		final Object discriminatorValue;
 		try {
 			discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(
 					rs,
 					entityAliases.getSuffixedDiscriminatorAlias(),
 					session,
 					null
 			);
 		}
 		catch (SQLException e) {
 			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
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
 					persister.getEntityName()
 			);
 		}
 
 		return result;
 	}
 
 	@Override
 	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext) {
 		final Object existing = getSession().getEntityUsingInterceptor( entityKey );
 
 		if ( existing != null ) {
 			if ( !entityKeyContext.getEntityPersister().isInstance( existing ) ) {
 				throw new WrongClassException(
 						"loaded object was of wrong class " + existing.getClass(),
 						entityKey.getIdentifier(),
 						entityKeyContext.getEntityPersister().getEntityName()
 				);
 			}
 
 			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
 					? LockMode.NONE
 					: entityKeyContext.getLockMode();
 
 			if ( requestedLockMode != LockMode.NONE ) {
 				final LockMode currentLockMode = getSession().getPersistenceContext().getEntry( existing ).getLockMode();
 				final boolean isVersionCheckNeeded = entityKeyContext.getEntityPersister().isVersioned()
 						&& currentLockMode.lessThan( requestedLockMode );
 
 				// we don't need to worry about existing version being uninitialized because this block isn't called
 				// by a re-entrant load (re-entrant loads *always* have lock mode NONE)
 				if ( isVersionCheckNeeded ) {
 					//we only check the version when *upgrading* lock modes
 					checkVersion(
 							resultSet,
 							entityKeyContext.getEntityPersister(),
 							entityKeyContext.getEntityAliases(),
 							entityKey,
 							existing
 					);
 					//we need to upgrade the lock mode to the mode requested
 					getSession().getPersistenceContext().getEntry( existing ).setLockMode( requestedLockMode );
 				}
 			}
 
 			return existing;
 		}
 		else {
 			final String concreteEntityTypeName = getConcreteEntityTypeName(
 					resultSet,
 					entityKeyContext.getEntityPersister(),
 					entityKeyContext.getEntityAliases(),
 					entityKey
 			);
 
 			final Object entityInstance = getSession().instantiate(
 					concreteEntityTypeName,
 					entityKey.getIdentifier()
 			);
 
 			//need to hydrate it.
 
 			// grab its state from the ResultSet and keep it in the Session
 			// (but don't yet initialize the object itself)
 			// note that we acquire LockMode.READ even if it was not requested
 			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
 					? LockMode.NONE
 					: entityKeyContext.getLockMode();
 			final LockMode acquiredLockMode = requestedLockMode == LockMode.NONE
 					? LockMode.READ
 					: requestedLockMode;
 
 			loadFromResultSet(
 					resultSet,
 					entityInstance,
 					concreteEntityTypeName,
 					entityKey,
 					entityKeyContext.getEntityAliases(),
 					acquiredLockMode,
 					entityKeyContext.getEntityPersister(),
 					true,
 					entityKeyContext.getEntityPersister().getEntityMetamodel().getEntityType()
 			);
 
 			// materialize associations (and initialize the object) later
 			registerHydratedEntity( entityKeyContext.getEntityPersister(), entityKey, entityInstance );
 
 			return entityInstance;
 		}
 	}
 
 	@Override
 	public void loadFromResultSet(
 			ResultSet resultSet,
 			Object entityInstance,
 			String concreteEntityTypeName,
 			EntityKey entityKey,
 			EntityAliases entityAliases,
 			LockMode acquiredLockMode,
 			EntityPersister rootPersister,
 			boolean eagerFetch,
 			EntityType associationType) {
 
 		final Serializable id = entityKey.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getSession().getFactory().getEntityPersister( concreteEntityTypeName );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Initializing object from ResultSet: {0}",
 					MessageHelper.infoString(
 							persister,
 							id,
 							getSession().getFactory()
 					)
 			);
 		}
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				entityKey,
 				entityInstance,
 				persister,
 				acquiredLockMode,
 				!eagerFetch,
 				session
 		);
 
 		// This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				entityAliases.getSuffixedPropertyAliases() :
 				entityAliases.getSuffixedPropertyAliases(persister);
 
 		final Object[] values;
 		try {
 			values = persister.hydrate(
 					resultSet,
 					id,
 					entityInstance,
 					(Loadable) rootPersister,
 					cols,
 					eagerFetch,
 					session
 			);
 		}
 		catch (SQLException e) {
 			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity state from ResultSet : " + entityKey
 			);
 		}
 
 		final Object rowId;
 		try {
 			rowId = persister.hasRowId() ? resultSet.getObject( entityAliases.getRowIdAlias() ) : null;
 		}
 		catch (SQLException e) {
 			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity row-id from ResultSet : " + entityKey
 			);
 		}
 
 		if ( associationType != null ) {
 			String ukName = associationType.getRHSUniqueKeyPropertyName();
 			if ( ukName != null ) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex( ukName );
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, entityInstance ),
 						type,
 						persister.getEntityMode(),
 						session.getFactory()
 				);
 				session.getPersistenceContext().addEntity( euk, entityInstance );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				entityInstance,
 				acquiredLockMode,
 				!eagerFetch,
 				session
 		);
 
 	}
 
+	public void readCollectionElements(final Object[] row) {
+			LoadPlanVisitor.visit(
+					loadPlan,
+					new LoadPlanVisitationStrategyAdapter() {
+						@Override
+						public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
+							readCollectionElement(
+									null,
+									null,
+									rootCollectionReturn.getCollectionPersister(),
+									rootCollectionReturn.getCollectionAliases(),
+									resultSet,
+									session
+							);
+						}
+
+						@Override
+						public void startingCollectionFetch(CollectionFetch collectionFetch) {
+							// TODO: determine which element is the owner.
+							final Object owner = row[ 0 ];
+							readCollectionElement(
+									owner,
+									collectionFetch.getCollectionPersister().getCollectionType().getKeyOfOwner( owner, session ),
+									collectionFetch.getCollectionPersister(),
+									collectionFetch.getCollectionAliases(),
+									resultSet,
+									session
+							);
+						}
+
+						private void readCollectionElement(
+								final Object optionalOwner,
+								final Serializable optionalKey,
+								final CollectionPersister persister,
+								final CollectionAliases descriptor,
+								final ResultSet rs,
+								final SessionImplementor session) {
+
+							try {
+								final PersistenceContext persistenceContext = session.getPersistenceContext();
+
+								final Serializable collectionRowKey = (Serializable) persister.readKey(
+										rs,
+										descriptor.getSuffixedKeyAliases(),
+										session
+								);
+
+								if ( collectionRowKey != null ) {
+									// we found a collection element in the result set
+
+									if ( LOG.isDebugEnabled() ) {
+										LOG.debugf( "Found row of collection: %s",
+												MessageHelper.collectionInfoString( persister, collectionRowKey, session.getFactory() ) );
+									}
+
+									Object owner = optionalOwner;
+									if ( owner == null ) {
+										owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
+										if ( owner == null ) {
+											//TODO: This is assertion is disabled because there is a bug that means the
+											//	  original owner of a transient, uninitialized collection is not known
+											//	  if the collection is re-referenced by a different object associated
+											//	  with the current Session
+											//throw new AssertionFailure("bug loading unowned collection");
+										}
+									}
+
+									PersistentCollection rowCollection = persistenceContext.getLoadContexts()
+											.getCollectionLoadContext( rs )
+											.getLoadingCollection( persister, collectionRowKey );
+
+									if ( rowCollection != null ) {
+										rowCollection.readFrom( rs, persister, descriptor, owner );
+									}
+
+								}
+								else if ( optionalKey != null ) {
+									// we did not find a collection element in the result set, so we
+									// ensure that a collection is created with the owner's identifier,
+									// since what we have is an empty collection
+
+									if ( LOG.isDebugEnabled() ) {
+										LOG.debugf( "Result set contains (possibly empty) collection: %s",
+												MessageHelper.collectionInfoString( persister, optionalKey, session.getFactory() ) );
+									}
+
+									persistenceContext.getLoadContexts()
+											.getCollectionLoadContext( rs )
+											.getLoadingCollection( persister, optionalKey ); // handle empty collection
+
+								}
+
+								// else no collection element, but also no owner
+							}
+							catch ( SQLException sqle ) {
+								// TODO: would be nice to have the SQL string that failed...
+								throw session.getFactory().getSQLExceptionHelper().convert(
+										sqle,
+										"could not read next row of results"
+								);
+							}
+						}
+
+					}
+			);
+	}
+
 	@Override
 	public void registerHydratedEntity(EntityPersister persister, EntityKey entityKey, Object entityInstance) {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			currentRowHydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		currentRowHydratedEntityRegistrationList.add( new HydratedEntityRegistration( persister, entityKey, entityInstance ) );
 	}
 
 	/**
 	 * Package-protected
 	 */
 	void finishUpRow() {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 
 		// managing the running list of registrations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( hydratedEntityRegistrationList == null ) {
 			hydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		hydratedEntityRegistrationList.addAll( currentRowHydratedEntityRegistrationList );
 
 
 		// managing the map forms needed for subselect fetch generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-		if ( ! hadSubselectFetches ) {
-			return;
-		}
-		if ( subselectLoadableEntityKeyMap == null ) {
-			subselectLoadableEntityKeyMap = new HashMap<EntityPersister, Set<EntityKey>>();
-		}
-		for ( HydratedEntityRegistration registration : currentRowHydratedEntityRegistrationList ) {
-			Set<EntityKey> entityKeys = subselectLoadableEntityKeyMap.get( registration.persister );
-			if ( entityKeys == null ) {
-				entityKeys = new HashSet<EntityKey>();
-				subselectLoadableEntityKeyMap.put( registration.persister, entityKeys );
+		if ( hadSubselectFetches ) {
+			if ( subselectLoadableEntityKeyMap == null ) {
+				subselectLoadableEntityKeyMap = new HashMap<EntityPersister, Set<EntityKey>>();
+			}
+			for ( HydratedEntityRegistration registration : currentRowHydratedEntityRegistrationList ) {
+				Set<EntityKey> entityKeys = subselectLoadableEntityKeyMap.get( registration.persister );
+				if ( entityKeys == null ) {
+					entityKeys = new HashSet<EntityKey>();
+					subselectLoadableEntityKeyMap.put( registration.persister, entityKeys );
+				}
+				entityKeys.add( registration.key );
 			}
-			entityKeys.add( registration.key );
 		}
 
 		// release the currentRowHydratedEntityRegistrationList entries
 		currentRowHydratedEntityRegistrationList.clear();
 	}
 
 	/**
 	 * Package-protected
 	 *
 	 * @param afterLoadActionList List of after-load actions to perform
 	 */
 	void finishUp(List<AfterLoadAction> afterLoadActionList) {
 		initializeEntitiesAndCollections( afterLoadActionList );
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
 
 	private void initializeEntitiesAndCollections(List<AfterLoadAction> afterLoadActionList) {
 		// for arrays, we should end the collection load before resolving the entities, since the
 		// actual array instances are not instantiated during loading
 		finishLoadingArrays();
 
 
 		// IMPORTANT: reuse the same event instances for performance!
 		final PreLoadEvent preLoadEvent;
 		final PostLoadEvent postLoadEvent;
 		if ( session.isEventSource() ) {
 			preLoadEvent = new PreLoadEvent( (EventSource) session );
 			postLoadEvent = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			preLoadEvent = null;
 			postLoadEvent = null;
 		}
 
 		// now finish loading the entities (2-phase load)
 		performTwoPhaseLoad( preLoadEvent, postLoadEvent );
 
 		// now we can finalize loading collections
 		finishLoadingCollections();
 
 		// finally, perform post-load operations
 		postLoad( postLoadEvent, afterLoadActionList );
 	}
 
 	private void finishLoadingArrays() {
 		LoadPlanVisitor.visit(
 				loadPlan,
 				new LoadPlanVisitationStrategyAdapter() {
 					@Override
 					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
 						endLoadingArray( rootCollectionReturn.getCollectionPersister() );
 					}
 
 					@Override
 					public void startingCollectionFetch(CollectionFetch collectionFetch) {
 						endLoadingArray( collectionFetch.getCollectionPersister() );
 					}
 
 					private void endLoadingArray(CollectionPersister persister) {
 						if ( persister.isArray() ) {
 							session.getPersistenceContext()
 									.getLoadContexts()
 									.getCollectionLoadContext( resultSet )
 									.endLoadingCollections( persister );
 						}
 					}
 				}
 		);
 	}
 
 	private void performTwoPhaseLoad(PreLoadEvent preLoadEvent, PostLoadEvent postLoadEvent) {
 		final int numberOfHydratedObjects = hydratedEntityRegistrationList == null
 				? 0
 				: hydratedEntityRegistrationList.size();
 		LOG.tracev( "Total objects hydrated: {0}", numberOfHydratedObjects );
 
 		if ( hydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
 			TwoPhaseLoad.initializeEntity( registration.instance, readOnly, session, preLoadEvent, postLoadEvent );
 		}
 	}
 
 	private void finishLoadingCollections() {
 		LoadPlanVisitor.visit(
 				loadPlan,
 				new LoadPlanVisitationStrategyAdapter() {
 					@Override
 					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
-						endLoadingArray( rootCollectionReturn.getCollectionPersister() );
+						endLoadingCollection( rootCollectionReturn.getCollectionPersister() );
 					}
 
 					@Override
 					public void startingCollectionFetch(CollectionFetch collectionFetch) {
-						endLoadingArray( collectionFetch.getCollectionPersister() );
+						endLoadingCollection( collectionFetch.getCollectionPersister() );
 					}
 
-					private void endLoadingArray(CollectionPersister persister) {
+					private void endLoadingCollection(CollectionPersister persister) {
 						if ( ! persister.isArray() ) {
 							session.getPersistenceContext()
 									.getLoadContexts()
 									.getCollectionLoadContext( resultSet )
 									.endLoadingCollections( persister );
 						}
 					}
 				}
 		);
 	}
 
 	private void postLoad(PostLoadEvent postLoadEvent, List<AfterLoadAction> afterLoadActionList) {
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
 			TwoPhaseLoad.postLoad( registration.instance, session, postLoadEvent );
 			if ( afterLoadActionList != null ) {
 				for ( AfterLoadAction afterLoadAction : afterLoadActionList ) {
 					afterLoadAction.afterLoad( session, registration.instance, (Loadable) registration.persister );
 				}
 			}
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
 
 	private static class HydratedEntityRegistration {
 		private final EntityPersister persister;
 		private final EntityKey key;
 		private final Object instance;
 
 		private HydratedEntityRegistration(EntityPersister persister, EntityKey key, Object instance) {
 			this.persister = persister;
 			this.key = key;
 			this.instance = instance;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java
index 479f12e6b9..e9176048e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java
@@ -1,210 +1,212 @@
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
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.LoadPlanVisitor;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.loader.spi.ScrollableResultSetProcessor;
 import org.hibernate.loader.spi.ResultSetProcessor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessorImpl implements ResultSetProcessor {
 	private static final Logger LOG = Logger.getLogger( ResultSetProcessorImpl.class );
 
 	private final LoadPlan loadPlan;
 
 	private final boolean hadSubselectFetches;
 
 	public ResultSetProcessorImpl(LoadPlan loadPlan) {
 		this.loadPlan = loadPlan;
 
 		LocalVisitationStrategy strategy = new LocalVisitationStrategy();
 		LoadPlanVisitor.visit( loadPlan, strategy );
 		this.hadSubselectFetches = strategy.hadSubselectFetches;
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
 
 		handlePotentiallyEmptyCollectionRootReturns( queryParameters.getCollectionKeys(), resultSet, session );
 
 		final int maxRows;
 		final RowSelection selection = queryParameters.getRowSelection();
 		if ( LimitHelper.hasMaxRows( selection ) ) {
 			maxRows = selection.getMaxRows();
 			LOG.tracef( "Limiting ResultSet processing to just %s rows", maxRows );
 		}
 		else {
 			maxRows = Integer.MAX_VALUE;
 		}
 
 		final ResultSetProcessingContextImpl context = new ResultSetProcessingContextImpl(
 				resultSet,
 				session,
 				loadPlan,
 				readOnly,
 //				true, // use optional entity key?  for now, always say yes
 				false, // use optional entity key?  actually for now always say no since in the simple test cases true causes failures because there is no optional key
 				queryParameters,
 				namedParameterContext,
 				hadSubselectFetches
 		);
 
 		final List loadResults = new ArrayList();
 
 		final int rootReturnCount = loadPlan.getReturns().size();
 
 		LOG.trace( "Processing result set" );
 		int count;
 		for ( count = 0; count < maxRows && resultSet.next(); count++ ) {
 			LOG.debugf( "Starting ResultSet row #%s", count );
 
 			Object logicalRow;
 			if ( rootReturnCount == 1 ) {
 				loadPlan.getReturns().get( 0 ).hydrate( resultSet, context );
 				loadPlan.getReturns().get( 0 ).resolve( resultSet, context );
 
 				logicalRow = loadPlan.getReturns().get( 0 ).read( resultSet, context );
+				context.readCollectionElements( new Object[] { logicalRow } );
 			}
 			else {
 				for ( Return rootReturn : loadPlan.getReturns() ) {
 					rootReturn.hydrate( resultSet, context );
 				}
 				for ( Return rootReturn : loadPlan.getReturns() ) {
 					rootReturn.resolve( resultSet, context );
 				}
 
 				logicalRow = new Object[ rootReturnCount ];
 				int pos = 0;
 				for ( Return rootReturn : loadPlan.getReturns() ) {
 					( (Object[]) logicalRow )[pos] = rootReturn.read( resultSet, context );
 					pos++;
 				}
+				context.readCollectionElements( (Object[]) logicalRow );
 			}
 
 			// todo : apply transformers here?
 
 			loadResults.add( logicalRow );
 
 			context.finishUpRow();
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		context.finishUp( afterLoadActionList );
 
 		session.getPersistenceContext().initializeNonLazyCollections();
 
 		return loadResults;
 	}
 
 
 	private void handlePotentiallyEmptyCollectionRootReturns(
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
 				session.getPersistenceContext()
 						.getLoadContexts()
 						.getCollectionLoadContext( resultSet )
 						.getLoadingCollection( persister, key );
 			}
 		}
 	}
 
 
 	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
 		private boolean hadSubselectFetches = false;
 
 		@Override
 		public void startingEntityFetch(EntityFetch entityFetch) {
 // only collections are currently supported for subselect fetching.
 //			hadSubselectFetches = hadSubselectFetches
 //					| entityFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
 		}
 
 		@Override
 		public void startingCollectionFetch(CollectionFetch collectionFetch) {
 			hadSubselectFetches = hadSubselectFetches
 					| collectionFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
index 35c8df1d63..0c1cc20723 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
@@ -1,93 +1,98 @@
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
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.AbstractFetchOwner;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.LoadPlanBuildingContext;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class LoadPlanBuildingHelper {
 	public static CollectionFetch buildStandardCollectionFetch(
 			FetchOwner fetchOwner,
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		final CollectionAliases collectionAliases = loadPlanBuildingContext.resolveCollectionColumnAliases( attributeDefinition );
-		final EntityAliases elementEntityAliases = loadPlanBuildingContext.resolveEntityColumnAliases( attributeDefinition );
+		final CollectionDefinition collectionDefinition = attributeDefinition.toCollectionDefinition();
+		final EntityAliases elementEntityAliases =
+				collectionDefinition.getElementDefinition().getType().isEntityType() ?
+						loadPlanBuildingContext.resolveEntityColumnAliases( attributeDefinition ) :
+						null;
 
 		return new CollectionFetch(
 				loadPlanBuildingContext.getSessionFactory(),
 				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
 				LockMode.NONE, // todo : for now
 				fetchOwner,
 				fetchStrategy,
 				attributeDefinition.getName(),
 				collectionAliases,
 				elementEntityAliases
 		);
 	}
 
 	public static EntityFetch buildStandardEntityFetch(
 			FetchOwner fetchOwner,
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 
 		return new EntityFetch(
 				loadPlanBuildingContext.getSessionFactory(),
 				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
 				LockMode.NONE, // todo : for now
 				fetchOwner,
 				attributeDefinition.getName(),
 				fetchStrategy,
 				sqlTableAlias,
 				loadPlanBuildingContext.resolveEntityColumnAliases( attributeDefinition )
 		);
 	}
 
 	public static CompositeFetch buildStandardCompositeFetch(
 			FetchOwner fetchOwner,
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return new CompositeFetch(
 				loadPlanBuildingContext.getSessionFactory(),
 				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
 				(AbstractFetchOwner) fetchOwner,
 				attributeDefinition.getName()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
index 405b318dcc..a1162cbe33 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
@@ -1,786 +1,787 @@
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
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.DefaultEntityAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.GeneratedCollectionAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
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
 
 	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory, int suffixSeed) {
 		this.sessionFactory = sessionFactory;
 		this.currentSuffixBase = suffixSeed;
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
 			identifierAttributeCollector = new EncapsulatedIdentifierAttributeCollector( entityReference );
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
+			pushToCollectionStack( (CollectionReference) associationFetch );
 		}
 		else {
 			associationFetch = fetchOwner.buildEntityFetch(
 					attributeDefinition,
 					fetchStrategy,
 					generateEntityFetchSqlTableAlias( attributeDefinition.toEntityDefinition().getEntityPersister().getEntityName() ),
 					this
 			);
 		}
 
 		if ( FetchOwner.class.isInstance( associationFetch ) ) {
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
 
 	private int currentSuffixBase;
 	private int implicitAliasUniqueness = 0;
 
 	private String createImplicitAlias() {
 		return "ia" + implicitAliasUniqueness++;
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory();
 	}
 
 	protected String generateEntityFetchSqlTableAlias(String entityName) {
 		return StringHelper.generateAlias( StringHelper.unqualifyEntityName( entityName ), currentDepth() );
 	}
 
 	@Override
 	public EntityAliases resolveEntityColumnAliases(AssociationAttributeDefinition attributeDefinition) {
 		return generateEntityColumnAliases( attributeDefinition.toEntityDefinition().getEntityPersister() );
 	}
 
 	protected EntityAliases generateEntityColumnAliases(EntityPersister persister) {
 		return new DefaultEntityAliases( (Loadable) persister, Integer.toString( currentSuffixBase++ ) + '_' );
 	}
 
 	@Override
 	public CollectionAliases resolveCollectionColumnAliases(AssociationAttributeDefinition attributeDefinition) {
 		return generateCollectionColumnAliases( attributeDefinition.toCollectionDefinition().getCollectionPersister() );
 	}
 
 	protected CollectionAliases generateCollectionColumnAliases(CollectionPersister persister) {
 		return new GeneratedCollectionAliases( persister, Integer.toString( currentSuffixBase++ ) + '_' );
 	}
 
 	@Override
 	public String resolveRootSourceAlias(EntityDefinition definition) {
 		return createImplicitAlias();
 	}
 
 	@Override
 	public String resolveRootSourceAlias(CollectionDefinition definition) {
 		return createImplicitAlias();
 	}
 
 	@Override
 	public String resolveFetchSourceAlias(AssociationAttributeDefinition attributeDefinition) {
 		return createImplicitAlias();
 	}
 
 	@Override
 	public String resolveFetchSourceAlias(CompositionDefinition compositionDefinition) {
 		return createImplicitAlias();
 	}
 
 	public static interface FetchStackAware {
 		public void poppedFromStack();
 	}
 
 	protected static abstract class AbstractIdentifierAttributeCollector
 			implements FetchOwner, EntityReference, FetchStackAware {
 
 		protected final EntityReference entityReference;
 		private final PropertyPath propertyPath;
 
 		protected final List<EntityFetch> identifierFetches = new ArrayList<EntityFetch>();
 		protected final Map<EntityFetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap
 				= new HashMap<EntityFetch, HydratedCompoundValueHandler>();
 
 		public AbstractIdentifierAttributeCollector(EntityReference entityReference) {
 			this.entityReference = entityReference;
 			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath().append( "<id>" );
 		}
 
 		@Override
 		public String getAlias() {
 			return entityReference.getAlias();
 		}
 
 		@Override
 		public String getSqlTableAlias() {
 			return entityReference.getSqlTableAlias();
 		}
 
 		@Override
 		public LockMode getLockMode() {
 			return entityReference.getLockMode();
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return entityReference.getEntityPersister();
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
 				String sqlTableAlias,
 				LoadPlanBuildingContext loadPlanBuildingContext) {
 			// we have a key-many-to-one
 			//
 			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back throw our #addFetch
 			// 		impl.  We collect them there and later build the IdentifierDescription
 			final EntityFetch fetch = LoadPlanBuildingHelper.buildStandardEntityFetch(
 					this,
 					attributeDefinition,
 					fetchStrategy,
 					sqlTableAlias,
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
 			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back throw our #addFetch
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
 			identifierFetches.add( (EntityFetch) fetch );
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
 		public PropertyPath getPropertyPath() {
 			return propertyPath;
 		}
 
 		@Override
 		public EntityAliases getEntityAliases() {
 			return entityReference.getEntityAliases();
 		}
 
 		@Override
 		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 			throw new WalkingException(
 					"IdentifierDescription collector should not get injected with IdentifierDescription"
 			);
 		}
 	}
 
 	protected static class EncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
 		public EncapsulatedIdentifierAttributeCollector(EntityReference entityReference) {
 			super( entityReference );
 		}
 
 		@Override
 		protected IdentifierDescription buildIdentifierDescription() {
 			return new IdentifierDescriptionImpl(
 					entityReference,
 					identifierFetches.toArray( new EntityFetch[ identifierFetches.size() ] ),
 					null
 			);
 		}
 	}
 
 	protected static class NonEncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
 		public NonEncapsulatedIdentifierAttributeCollector(EntityReference entityReference) {
 			super( entityReference );
 		}
 
 		@Override
 		protected IdentifierDescription buildIdentifierDescription() {
 			return new IdentifierDescriptionImpl(
 					entityReference,
 					identifierFetches.toArray( new EntityFetch[ identifierFetches.size() ] ),
 					fetchToHydratedStateExtractorMap
 			);
 		}
 	}
 
 	private static class IdentifierDescriptionImpl implements IdentifierDescription {
 		private final EntityReference entityReference;
 		private final EntityFetch[] identifierFetches;
 		private final Map<EntityFetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap;
 
 		private IdentifierDescriptionImpl(
 				EntityReference entityReference, EntityFetch[] identifierFetches,
 				Map<EntityFetch, HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap) {
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
 				for ( EntityFetch fetch : identifierFetches ) {
 					final IdentifierResolutionContext identifierResolutionContext =
 							context.getIdentifierResolutionContext( fetch );
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
 				return;
 			}
 
 			final Object hydratedIdentifierState = entityReference.getEntityPersister().getIdentifierType().hydrate(
 					resultSet,
 					entityReference.getEntityAliases().getSuffixedKeyAliases(),
 					context.getSession(),
 					null
 			);
 			context.getIdentifierResolutionContext( entityReference ).registerHydratedForm( hydratedIdentifierState );
 		}
 
 		@Override
 		public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 			for ( EntityFetch fetch : identifierFetches ) {
 				final IdentifierResolutionContext identifierResolutionContext =
 						context.getIdentifierResolutionContext( fetch );
 				if ( identifierResolutionContext.getEntityKey() != null ) {
 					continue;
 				}
 
 				EntityKey fetchKey = fetch.resolveInIdentifier( resultSet, context );
 				identifierResolutionContext.registerEntityKey( fetchKey );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
index 89d82d4370..ab4e012ece 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
@@ -1,91 +1,93 @@
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
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionFetch extends AbstractCollectionReference implements Fetch {
 	private final FetchOwner fetchOwner;
 	private final FetchStrategy fetchStrategy;
 
 	public CollectionFetch(
 			SessionFactoryImplementor sessionFactory,
 			String alias,
 			LockMode lockMode,
 			FetchOwner fetchOwner,
 			FetchStrategy fetchStrategy,
 			String ownerProperty,
 			CollectionAliases collectionAliases,
 			EntityAliases elementEntityAliases) {
 		super(
 				sessionFactory,
 				alias,
 				lockMode,
 				sessionFactory.getCollectionPersister(
 						fetchOwner.retrieveFetchSourcePersister().getEntityName() + '.' + ownerProperty
 				),
 				fetchOwner.getPropertyPath().append( ownerProperty ),
 				collectionAliases,
 				elementEntityAliases
 		);
 		this.fetchOwner = fetchOwner;
 		this.fetchStrategy = fetchStrategy;
+
+		fetchOwner.addFetch( this );
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitor.java
index 9a371aa011..304ace31ec 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitor.java
@@ -1,119 +1,121 @@
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
 
 /**
  * Visitor for processing {@link Return} graphs
  *
  * @author Steve Ebersole
  */
 public class LoadPlanVisitor {
 	public static void visit(LoadPlan loadPlan, LoadPlanVisitationStrategy strategy) {
 		new LoadPlanVisitor( strategy ).visit( loadPlan );
 	}
 
 	private final LoadPlanVisitationStrategy strategy;
 
 	public LoadPlanVisitor(LoadPlanVisitationStrategy strategy) {
 		this.strategy = strategy;
 	}
 
 	private void visit(LoadPlan loadPlan) {
 		strategy.start( loadPlan );
 
 		for ( Return rootReturn : loadPlan.getReturns() ) {
 			visitRootReturn( rootReturn );
 		}
 
 		strategy.finish( loadPlan );
 	}
 
 	private void visitRootReturn(Return rootReturn) {
 		strategy.startingRootReturn( rootReturn );
 
 		if ( org.hibernate.loader.plan.spi.ScalarReturn.class.isInstance( rootReturn ) ) {
 			strategy.handleScalarReturn( (ScalarReturn) rootReturn );
 		}
 		else {
 			visitNonScalarRootReturn( rootReturn );
 		}
 
 		strategy.finishingRootReturn( rootReturn );
 	}
 
 	private void visitNonScalarRootReturn(Return rootReturn) {
 		if ( EntityReturn.class.isInstance( rootReturn ) ) {
 			strategy.handleEntityReturn( (EntityReturn) rootReturn );
 			visitFetches( (EntityReturn) rootReturn );
 		}
 		else if ( CollectionReturn.class.isInstance( rootReturn ) ) {
 			strategy.handleCollectionReturn( (CollectionReturn) rootReturn );
 			final CollectionReturn collectionReturn = (CollectionReturn) rootReturn;
 			visitFetches( collectionReturn.getIndexGraph() );
 			visitFetches( collectionReturn.getElementGraph() );
 		}
 		else {
 			throw new IllegalStateException(
 					"Unexpected return type encountered; expecting a non-scalar root return, but found " +
 							rootReturn.getClass().getName()
 			);
 		}
 	}
 
 	private void visitFetches(FetchOwner fetchOwner) {
-		strategy.startingFetches( fetchOwner );
+		if ( fetchOwner != null ) {
+			strategy.startingFetches( fetchOwner );
 
-		for ( Fetch fetch : fetchOwner.getFetches() ) {
-			visitFetch( fetch );
-		}
+			for ( Fetch fetch : fetchOwner.getFetches() ) {
+				visitFetch( fetch );
+			}
 
-		strategy.finishingFetches( fetchOwner );
+			strategy.finishingFetches( fetchOwner );
+		}
 	}
 
 	private void visitFetch(Fetch fetch) {
 		if ( EntityFetch.class.isInstance( fetch ) ) {
 			strategy.startingEntityFetch( (EntityFetch) fetch );
 			visitFetches( (EntityFetch) fetch );
 			strategy.finishingEntityFetch( (EntityFetch) fetch );
 		}
 		else if ( CollectionFetch.class.isInstance( fetch ) ) {
 			strategy.startingCollectionFetch( (CollectionFetch) fetch );
 			visitFetches( ( (CollectionFetch) fetch ).getIndexGraph() );
 			visitFetches( ( (CollectionFetch) fetch ).getElementGraph() );
 			strategy.finishingCollectionFetch( (CollectionFetch) fetch );
 		}
 		else if ( CompositeFetch.class.isInstance( fetch ) ) {
 			strategy.startingCompositeFetch( (CompositeFetch) fetch );
 			visitFetches( (CompositeFetch) fetch );
 			strategy.finishingCompositeFetch( (CompositeFetch) fetch );
 		}
 		else {
 			throw new IllegalStateException(
 					"Unexpected return type encountered; expecting a fetch return, but found " +
 							fetch.getClass().getName()
 			);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
index dd205aa9e8..f22d172bec 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
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
 
 		final boolean continueWalk = strategy.startingAttribute( attributeDefinition );
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
 
 		if ( isDuplicateAssociation( attribute.getAssociationKey() ) ) {
 			log.debug( "Property path deemed to be circular : " + currentPropertyPath.getFullPath() );
 			return;
 		}
 
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
 			visitCompositeDefinition( elementDefinition.toCompositeDefinition() );
 		}
-		else {
+		else if ( elementDefinition.getType().isEntityType() ) {
 			visitEntityDefinition( elementDefinition.toEntityDefinition() );
 		}
 
 		strategy.finishingCollectionElements( elementDefinition );
 	}
 
 
 	private final Set<AssociationKey> visitedAssociationKeys = new HashSet<AssociationKey>();
 
 	protected boolean isDuplicateAssociation(AssociationKey associationKey) {
 		return !visitedAssociationKeys.add( associationKey );
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/AssociationResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
similarity index 98%
rename from hibernate-core/src/test/java/org/hibernate/loader/AssociationResultSetProcessorTest.java
rename to hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
index b41cf73572..f8eb3a0bcc 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/AssociationResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
@@ -1,312 +1,312 @@
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
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.internal.EntityLoadQueryBuilderImpl;
 import org.hibernate.loader.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.LoadPlanBuilder;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
- * @author Steve Ebersole
+ * @author Gail Badner
  */
-public class AssociationResultSetProcessorTest extends BaseCoreFunctionalTestCase {
+public class EntityAssociationResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
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
 			final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
 					"abc",
 					0
 			);
 			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
 			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
 					plan
 			);
 			final String sql = queryBuilder.generateSql( 1 );
 
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
 			final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
 					"abc",
 					0
 			);
 			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
 			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
 					plan
 			);
 			final String sql = queryBuilder.generateSql( 1 );
 
 			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan );
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
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithCollectionResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithCollectionResultSetProcessorTest.java
new file mode 100644
index 0000000000..d83804e815
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithCollectionResultSetProcessorTest.java
@@ -0,0 +1,174 @@
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
+package org.hibernate.loader;
+
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.HashSet;
+import java.util.List;
+import java.util.Set;
+import javax.persistence.CascadeType;
+import javax.persistence.ElementCollection;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+
+import org.junit.Test;
+
+import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.util.collections.IdentitySet;
+import org.hibernate.jdbc.Work;
+import org.hibernate.loader.internal.EntityLoadQueryBuilderImpl;
+import org.hibernate.loader.internal.ResultSetProcessorImpl;
+import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.LoadPlanBuilder;
+import org.hibernate.loader.spi.NamedParameterContext;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.testing.junit4.ExtraAssertions;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertSame;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Gail Badner
+ */
+public class EntityWithCollectionResultSetProcessorTest extends BaseCoreFunctionalTestCase {
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Person.class };
+	}
+
+	@Test
+	public void testEntityWithSet() throws Exception {
+		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Person.class.getName() );
+
+		// create some test data
+		Session session = openSession();
+		session.beginTransaction();
+		Person person = new Person();
+		person.id = 1;
+		person.name = "John Doe";
+		person.nickNames.add( "Jack" );
+		person.nickNames.add( "Johnny" );
+		session.save( person );
+		session.getTransaction().commit();
+		session.close();
+
+		{
+			final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
+					sessionFactory(),
+					LoadQueryInfluencers.NONE,
+					"abc",
+					0
+			);
+			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
+					sessionFactory(),
+					LoadQueryInfluencers.NONE,
+					plan
+			);
+			final String sql = queryBuilder.generateSql( 1 );
+
+			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan );
+			final List results = new ArrayList();
+
+			final Session workSession = openSession();
+			workSession.beginTransaction();
+			workSession.doWork(
+					new Work() {
+						@Override
+						public void execute(Connection connection) throws SQLException {
+							PreparedStatement ps = connection.prepareStatement( sql );
+							ps.setInt( 1, 1 );
+							ResultSet resultSet = ps.executeQuery();
+							results.addAll(
+									resultSetProcessor.extractResults(
+											resultSet,
+											(SessionImplementor) workSession,
+											new QueryParameters(),
+											new NamedParameterContext() {
+												@Override
+												public int[] getNamedParameterLocations(String name) {
+													return new int[0];
+												}
+											},
+											true,
+											false,
+											null,
+											null
+									)
+							);
+							resultSet.close();
+							ps.close();
+						}
+					}
+			);
+			assertEquals( 2, results.size() );
+			Object result1 = results.get( 0 );
+			assertSame( result1, results.get( 1 ) );
+			assertNotNull( result1 );
+
+			Person workPerson = ExtraAssertions.assertTyping( Person.class, result1 );
+			assertEquals( 1, workPerson.id.intValue() );
+			assertEquals( person.name, workPerson.name );
+			assertTrue( Hibernate.isInitialized( workPerson.nickNames ) );
+			assertEquals( 2, workPerson.nickNames.size() );
+			assertEquals( person.nickNames, workPerson.nickNames );
+			workSession.getTransaction().commit();
+			workSession.close();
+		}
+
+		// clean up test data
+		session = openSession();
+		session.beginTransaction();
+		session.delete( person );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Entity( name = "Person" )
+	public static class Person {
+		@Id
+		private Integer id;
+		private String name;
+		@ElementCollection( fetch = FetchType.EAGER )
+		private Set<String> nickNames = new HashSet<String>();
+	}
+}
