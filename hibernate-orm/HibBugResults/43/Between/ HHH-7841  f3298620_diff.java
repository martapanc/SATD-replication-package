diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
index d2b4a1ff90..f328599990 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
@@ -1,117 +1,123 @@
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
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
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
 			List<JoinableAssociationImpl> associations) {
 		super( factory, associations );
 		this.entityReturn = entityReturn;
 	}
 
 	protected final String generateSql(
 			final String whereString,
 			final String orderByString,
-			final LockOptions lockOptions) throws MappingException {
-		return generateSql( null, whereString, orderByString, "", lockOptions );
+			final LockOptions lockOptions,
+			final LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
+		return generateSql( null, whereString, orderByString, "", lockOptions, aliasResolutionContext );
 	}
 
 	private String generateSql(
 			final String projection,
 			final String condition,
 			final String orderBy,
 			final String groupBy,
-			final LockOptions lockOptions) throws MappingException {
+			final LockOptions lockOptions,
+			final LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
 
-		JoinFragment ojf = mergeOuterJoins();
+		JoinFragment ojf = mergeOuterJoins( aliasResolutionContext );
 
 		// If no projection, then the last suffix should be for the entity return.
 		// TODO: simplify how suffixes are generated/processed.
 
 
 		Select select = new Select( getDialect() )
 				.setLockOptions( lockOptions )
 				.setSelectClause(
 						projection == null ?
-								getPersister().selectFragment( getAlias(), entityReturn.getEntityAliases().getSuffix() ) + associationSelectString() :
+								getPersister().selectFragment(
+										getAlias( aliasResolutionContext ),
+										aliasResolutionContext.resolveEntityColumnAliases( entityReturn ).getSuffix()
+								) + associationSelectString( aliasResolutionContext ) :
 								projection
 				)
 				.setFromClause(
-						getDialect().appendLockHint( lockOptions, getPersister().fromTableFragment( getAlias() ) ) +
-								getPersister().fromJoinFragment( getAlias(), true, true )
+						getDialect().appendLockHint( lockOptions, getPersister().fromTableFragment( getAlias( aliasResolutionContext ) ) ) +
+								getPersister().fromJoinFragment( getAlias( aliasResolutionContext), true, true )
 				)
 				.setWhereClause( condition )
 				.setOuterJoins(
 						ojf.toFromFragmentString(),
-						ojf.toWhereFragmentString() + getWhereFragment()
+						ojf.toWhereFragmentString() + getWhereFragment( aliasResolutionContext )
 				)
-				.setOrderByClause( orderBy( orderBy ) )
+				.setOrderByClause( orderBy( orderBy, aliasResolutionContext ) )
 				.setGroupByClause( groupBy );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( getComment() );
 		}
 		return select.toStatementString();
 	}
 
-	protected String getWhereFragment() throws MappingException {
+	protected String getWhereFragment(LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
 		// here we do not bother with the discriminator.
-		return getPersister().whereJoinFragment( getAlias(), true, true );
+		return getPersister().whereJoinFragment( getAlias( aliasResolutionContext ), true, true );
 	}
 
 	public abstract String getComment();
 
 	public final OuterJoinLoadable getPersister() {
 		return (OuterJoinLoadable) entityReturn.getEntityPersister();
 	}
 
-	public final String getAlias() {
-		return entityReturn.getSqlTableAlias();
+	public final String getAlias(LoadQueryAliasResolutionContext aliasResolutionContext) {
+		return aliasResolutionContext.resolveEntitySqlTableAlias( entityReturn );
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getPersister().getEntityName() + ')';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
index e934434b6b..83ddbd352a 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
@@ -1,208 +1,292 @@
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
 import java.util.List;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.spi.JoinableAssociation;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
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
 
 	protected AbstractLoadQueryImpl(
 			SessionFactoryImplementor factory,
 			List<JoinableAssociationImpl> associations) {
 		this.factory = factory;
 		this.associations = associations;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected Dialect getDialect() {
 		return factory.getDialect();
 	}
 
-	protected String orderBy(final String orderBy) {
-		return mergeOrderings( orderBy( associations ), orderBy );
+	protected String orderBy(final String orderBy, LoadQueryAliasResolutionContext aliasResolutionContext) {
+		return mergeOrderings( orderBy( associations, aliasResolutionContext ), orderBy );
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
-	protected final JoinFragment mergeOuterJoins()
+	protected final JoinFragment mergeOuterJoins(LoadQueryAliasResolutionContext aliasResolutionContext)
 	throws MappingException {
-		JoinFragment outerjoin = getDialect().createOuterJoinFragment();
-		JoinableAssociationImpl last = null;
-		for ( JoinableAssociationImpl oj : associations ) {
-			if ( last != null && last.isManyToManyWith( oj ) ) {
-				oj.addManyToManyJoin( outerjoin, ( QueryableCollection ) last.getJoinable() );
+		JoinFragment joinFragment = getDialect().createOuterJoinFragment();
+		JoinableAssociation previous = null;
+		for ( JoinableAssociation association : associations ) {
+			if ( previous != null && previous.isManyToManyWith( association ) ) {
+				addManyToManyJoin( joinFragment, association, ( QueryableCollection ) previous.getJoinable(), aliasResolutionContext );
 			}
 			else {
-				oj.addJoins(outerjoin);
+				addJoins( joinFragment, association, aliasResolutionContext);
 			}
-			last = oj;
+			previous = association;
 		}
-		return outerjoin;
+		return joinFragment;
 	}
 
 	/**
 	 * Get the order by string required for collection fetching
 	 */
-	protected static String orderBy(List<JoinableAssociationImpl> associations)
+	protected static String orderBy(
+			List<JoinableAssociationImpl> associations,
+			LoadQueryAliasResolutionContext aliasResolutionContext)
 	throws MappingException {
 		StringBuilder buf = new StringBuilder();
-		JoinableAssociationImpl last = null;
-		for ( JoinableAssociationImpl oj : associations ) {
-			if ( oj.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
-				if ( oj.getJoinable().isCollection() ) {
-					final QueryableCollection queryableCollection = (QueryableCollection) oj.getJoinable();
+		JoinableAssociation previous = null;
+		for ( JoinableAssociation association : associations ) {
+			final String rhsAlias = aliasResolutionContext.resolveRhsAlias( association );
+			if ( association.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
+				if ( association.getJoinable().isCollection() ) {
+					final QueryableCollection queryableCollection = (QueryableCollection) association.getJoinable();
 					if ( queryableCollection.hasOrdering() ) {
-						final String orderByString = queryableCollection.getSQLOrderByString( oj.getRHSAlias() );
+						final String orderByString = queryableCollection.getSQLOrderByString( rhsAlias );
 						buf.append( orderByString ).append(", ");
 					}
 				}
 				else {
 					// it might still need to apply a collection ordering based on a
 					// many-to-many defined order-by...
-					if ( last != null && last.getJoinable().isCollection() ) {
-						final QueryableCollection queryableCollection = (QueryableCollection) last.getJoinable();
-						if ( queryableCollection.isManyToMany() && last.isManyToManyWith( oj ) ) {
+					if ( previous != null && previous.getJoinable().isCollection() ) {
+						final QueryableCollection queryableCollection = (QueryableCollection) previous.getJoinable();
+						if ( queryableCollection.isManyToMany() && previous.isManyToManyWith( association ) ) {
 							if ( queryableCollection.hasManyToManyOrdering() ) {
-								final String orderByString = queryableCollection.getManyToManyOrderByString( oj.getRHSAlias() );
+								final String orderByString = queryableCollection.getManyToManyOrderByString( rhsAlias );
 								buf.append( orderByString ).append(", ");
 							}
 						}
 					}
 				}
 			}
-			last = oj;
+			previous = association;
 		}
 		if ( buf.length() > 0 ) {
 			buf.setLength( buf.length() - 2 );
 		}
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
 			for ( int i = 0; i < batchSize; i++ ) {
 				in.addValue( "?" );
 			}
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
-	protected final String associationSelectString()
+	protected final String associationSelectString(LoadQueryAliasResolutionContext aliasResolutionContext)
 	throws MappingException {
 
 		if ( associations.size() == 0 ) {
 			return "";
 		}
 		else {
 			StringBuilder buf = new StringBuilder( associations.size() * 100 );
 			for ( int i=0; i<associations.size(); i++ ) {
-				JoinableAssociationImpl join = associations.get(i);
-				JoinableAssociationImpl next = (i == associations.size() - 1)
+				JoinableAssociation association = associations.get( i );
+				JoinableAssociation next = ( i == associations.size() - 1 )
 				        ? null
 				        : associations.get( i + 1 );
-				final Joinable joinable = join.getJoinable();
+				final Joinable joinable = association.getJoinable();
+				final EntityAliases currentEntityAliases = aliasResolutionContext.resolveCurrentEntityAliases( association );
+				final CollectionAliases currentCollectionAliases = aliasResolutionContext.resolveCurrentCollectionAliases( association );
 				final String selectFragment = joinable.selectFragment(
 						next == null ? null : next.getJoinable(),
-						next == null ? null : next.getRHSAlias(),
-						join.getRHSAlias(),
-						associations.get( i ).getCurrentEntitySuffix(),
-						associations.get( i ).getCurrentCollectionSuffix(),
-						join.getJoinType()==JoinType.LEFT_OUTER_JOIN
+						next == null ? null : aliasResolutionContext.resolveRhsAlias( next ),
+						aliasResolutionContext.resolveRhsAlias( association ),
+						currentEntityAliases == null ? null : currentEntityAliases.getSuffix(),
+						currentCollectionAliases == null ? null : currentCollectionAliases.getSuffix(),
+						association.getJoinType()==JoinType.LEFT_OUTER_JOIN
 				);
 				if (selectFragment.trim().length() > 0) {
-					buf.append(", ").append(selectFragment);
+					// TODO: shouldn't the append of selectFragment be outside this if statement???
+					buf.append(", ").append( selectFragment );
 				}
 			}
 			return buf.toString();
 		}
 	}
-}
+
+	private void addJoins(
+			JoinFragment joinFragment,
+			JoinableAssociation association,
+			LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
+		final String rhsAlias = aliasResolutionContext.resolveRhsAlias( association );
+		joinFragment.addJoin(
+				association.getJoinable().getTableName(),
+				rhsAlias,
+				aliasResolutionContext.resolveAliasedLhsColumnNames( association ),
+				association.getRhsColumns(),
+				association.getJoinType(),
+				resolveOnCondition( association, aliasResolutionContext )
+		);
+		joinFragment.addJoins(
+				association.getJoinable().fromJoinFragment( rhsAlias, false, true ),
+				association.getJoinable().whereJoinFragment( rhsAlias, false, true )
+		);
+	}
+
+	private String resolveOnCondition(JoinableAssociation joinableAssociation,
+									  LoadQueryAliasResolutionContext aliasResolutionContext) {
+		final String withClause = StringHelper.isEmpty( joinableAssociation.getWithClause() ) ?
+				"" :
+				" and ( " + joinableAssociation.getWithClause() + " )";
+		return joinableAssociation.getJoinableType().getOnCondition(
+				aliasResolutionContext.resolveRhsAlias( joinableAssociation ),
+				factory,
+				joinableAssociation.getEnabledFilters()
+		) + withClause;
+	}
+
+
+
+	/*
+	public void validateJoin(String path) throws MappingException {
+		if ( rhsColumns==null || lhsColumns==null
+				|| lhsColumns.length!=rhsColumns.length || lhsColumns.length==0 ) {
+			throw new MappingException("invalid join columns for association: " + path);
+		}
+	}
+	*/
+
+	private void addManyToManyJoin(
+			JoinFragment outerjoin,
+			JoinableAssociation association,
+			QueryableCollection collection,
+			LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
+		final String rhsAlias = aliasResolutionContext.resolveRhsAlias( association );
+		final String[] aliasedLhsColumnNames = aliasResolutionContext.resolveAliasedLhsColumnNames( association );
+		final String manyToManyFilter = collection.getManyToManyFilterFragment(
+				rhsAlias,
+				association.getEnabledFilters()
+		);
+		final String on = resolveOnCondition( association, aliasResolutionContext );
+		String condition = "".equals( manyToManyFilter )
+				? on
+				: "".equals( on )
+				? manyToManyFilter
+				: on + " and " + manyToManyFilter;
+		outerjoin.addJoin(
+				association.getJoinable().getTableName(),
+				rhsAlias,
+				aliasedLhsColumnNames,
+				association.getRhsColumns(),
+				association.getJoinType(),
+				condition
+		);
+		outerjoin.addJoins(
+				association.getJoinable().fromJoinFragment( rhsAlias, false, true ),
+				association.getJoinable().whereJoinFragment( rhsAlias, false, true )
+		);
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
index d2860cd95d..688d1bc9bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
@@ -1,209 +1,215 @@
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
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.CollectionFetch;
+import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitor;
 import org.hibernate.loader.plan.spi.Return;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.LoadQueryBuilder;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.walking.spi.WalkingException;
 
 /**
  * @author Gail Badner
  */
 public class EntityLoadQueryBuilderImpl implements LoadQueryBuilder {
 	private final SessionFactoryImplementor sessionFactory;
 	private final LoadQueryInfluencers loadQueryInfluencers;
 	private final LoadPlan loadPlan;
+	private final LoadQueryAliasResolutionContext aliasResolutionContext;
 	private final List<JoinableAssociationImpl> associations;
 
 	public EntityLoadQueryBuilderImpl(
 			SessionFactoryImplementor sessionFactory,
 			LoadQueryInfluencers loadQueryInfluencers,
-			LoadPlan loadPlan) {
+			LoadPlan loadPlan,
+			LoadQueryAliasResolutionContext aliasResolutionContext) {
 		this.sessionFactory = sessionFactory;
 		this.loadQueryInfluencers = loadQueryInfluencers;
 		this.loadPlan = loadPlan;
+
+		// TODO: remove reliance on aliasResolutionContext; it should only be needed when generating the SQL.
+		this.aliasResolutionContext = aliasResolutionContext;
 		LocalVisitationStrategy strategy = new LocalVisitationStrategy();
 		LoadPlanVisitor.visit( loadPlan, strategy );
 		this.associations = strategy.associations;
 	}
 
 	@Override
 	public String generateSql(int batchSize) {
 		return generateSql( batchSize, getOuterJoinLoadable().getKeyColumnNames() );
 	}
 
 	public String generateSql(int batchSize, String[] uniqueKey) {
 		final EntityLoadQueryImpl loadQuery = new EntityLoadQueryImpl(
 				sessionFactory,
 				getRootEntityReturn(),
 				associations
 		);
-		return loadQuery.generateSql( uniqueKey, batchSize, getRootEntityReturn().getLockMode() );
+		return loadQuery.generateSql( uniqueKey, batchSize, getRootEntityReturn().getLockMode(), aliasResolutionContext );
 	}
 
 	private EntityReturn getRootEntityReturn() {
 		return (EntityReturn) loadPlan.getReturns().get( 0 );
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable() {
 		return (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 	}
 	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
 		private final List<JoinableAssociationImpl> associations = new ArrayList<JoinableAssociationImpl>();
-		private Deque<EntityAliases> entityAliasStack = new ArrayDeque<EntityAliases>();
-		private Deque<CollectionAliases> collectionAliasStack = new ArrayDeque<CollectionAliases>();
+		private Deque<EntityReference> entityReferenceStack = new ArrayDeque<EntityReference>();
+		private Deque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
 
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
-			pushToStack( entityAliasStack, entityRootReturn.getEntityAliases() );
+			pushToStack( entityReferenceStack, entityRootReturn );
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
-			popFromStack( entityAliasStack, ( (EntityReturn) rootReturn ).getEntityAliases() );
+			popFromStack( entityReferenceStack, entityRootReturn );
 		}
 
 		@Override
 		public void startingEntityFetch(EntityFetch entityFetch) {
 			JoinableAssociationImpl assoc = new JoinableAssociationImpl(
 					entityFetch,
-					getCurrentCollectionSuffix(),
+					getCurrentCollectionReference(),
 					"",    // getWithClause( entityFetch.getPropertyPath() )
 					false, // hasRestriction( entityFetch.getPropertyPath() )
 					sessionFactory,
 					loadQueryInfluencers.getEnabledFilters()
 			);
 			associations.add( assoc );
-			pushToStack( entityAliasStack, entityFetch.getEntityAliases() );
+			pushToStack( entityReferenceStack, entityFetch );
 		}
 
 		@Override
 		public void finishingEntityFetch(EntityFetch entityFetch) {
-			popFromStack( entityAliasStack, entityFetch.getEntityAliases() );
+			popFromStack( entityReferenceStack, entityFetch );
 		}
 
 		@Override
 		public void startingCollectionFetch(CollectionFetch collectionFetch) {
 			JoinableAssociationImpl assoc = new JoinableAssociationImpl(
 					collectionFetch,
-					getCurrentEntitySuffix(),
+					getCurrentEntityReference(),
 					"",    // getWithClause( entityFetch.getPropertyPath() )
 					false, // hasRestriction( entityFetch.getPropertyPath() )
 					sessionFactory,
 					loadQueryInfluencers.getEnabledFilters()
 			);
 			associations.add( assoc );
-			pushToStack( collectionAliasStack, collectionFetch.getCollectionAliases() );
+			pushToStack( collectionReferenceStack, collectionFetch );
 		}
 
 		@Override
 		public void finishingCollectionFetch(CollectionFetch collectionFetch) {
-			popFromStack( collectionAliasStack, collectionFetch.getCollectionAliases() );
+			popFromStack( collectionReferenceStack, collectionFetch );
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
-			entityAliasStack.clear();
-			collectionAliasStack.clear();
+			entityReferenceStack.clear();
+			collectionReferenceStack.clear();
 		}
 
-		private String getCurrentEntitySuffix() {
-			return entityAliasStack.peekFirst() == null ? null : entityAliasStack.peekFirst().getSuffix();
+		private EntityReference getCurrentEntityReference() {
+			return entityReferenceStack.peekFirst() == null ? null : entityReferenceStack.peekFirst();
 		}
 
-		private String getCurrentCollectionSuffix() {
-			return collectionAliasStack.peekFirst() == null ? null : collectionAliasStack.peekFirst().getSuffix();
+		private CollectionReference getCurrentCollectionReference() {
+			return collectionReferenceStack.peekFirst() == null ? null : collectionReferenceStack.peekFirst();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
index 2eedf8a88f..904ce1c479 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
@@ -1,60 +1,65 @@
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
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 
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
 			List<JoinableAssociationImpl> associations) throws MappingException {
 		super( factory, entityReturn, associations );
 	}
 
-	public String generateSql(String[] uniqueKey, int batchSize, LockMode lockMode) {
-		StringBuilder whereCondition = whereString( getAlias(), uniqueKey, batchSize )
+	public String generateSql(
+			String[] uniqueKey,
+			int batchSize,
+			LockMode lockMode,
+			LoadQueryAliasResolutionContext aliasResolutionContext) {
+		StringBuilder whereCondition = whereString( getAlias( aliasResolutionContext ), uniqueKey, batchSize )
 				//include the discriminator and class-level where, but not filters
-				.append( getPersister().filterFragment( getAlias(), Collections.EMPTY_MAP ) );
-		return generateSql( whereCondition.toString(), "",  new LockOptions().setLockMode( lockMode ) );
+				.append( getPersister().filterFragment( getAlias( aliasResolutionContext ), Collections.EMPTY_MAP ) );
+		return generateSql( whereCondition.toString(), "",  new LockOptions().setLockMode( lockMode ), aliasResolutionContext );
 	}
 
 	public String getComment() {
 		return "load " + getPersister().getEntityName();
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
index da8361304e..b9b07bed56 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
@@ -1,285 +1,303 @@
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
-import java.util.List;
 import java.util.Map;
 
+import org.hibernate.Filter;
 import org.hibernate.MappingException;
-import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.CollectionFetch;
+import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.persister.collection.QueryableCollection;
-import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
-import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
-import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 
 /**
  * Part of the Hibernate SQL rendering internals.  This class represents
  * a joinable association.
  *
  * @author Gavin King
  * @author Gail Badner
  */
-public final class JoinableAssociationImpl {
+public class JoinableAssociationImpl implements JoinableAssociation {
 	private final PropertyPath propertyPath;
-	private final AssociationType joinableType;
 	private final Joinable joinable;
-	private final String lhsAlias; // belong to other persister
-	private final String[] lhsColumns; // belong to other persister
-	private final String rhsAlias;
+	private final AssociationType joinableType;
 	private final String[] rhsColumns;
-	private final String currentEntitySuffix;
-	private final String currentCollectionSuffix;
+	private final Fetch currentFetch;
+	private final EntityReference currentEntityReference;
+	private final CollectionReference currentCollectionReference;
 	private final JoinType joinType;
-	private final String on;
-	private final Map enabledFilters;
+	private final String withClause;
+	private final Map<String, Filter> enabledFilters;
 	private final boolean hasRestriction;
 
 	public JoinableAssociationImpl(
 			EntityFetch entityFetch,
-			String currentCollectionSuffix,
+			CollectionReference currentCollectionReference,
 			String withClause,
 			boolean hasRestriction,
 			SessionFactoryImplementor factory,
-			Map enabledFilters) throws MappingException {
-		this.propertyPath = entityFetch.getPropertyPath();
-		this.joinableType = entityFetch.getAssociationType();
-		// TODO: this is not correct
-		final EntityPersister fetchSourcePersister = entityFetch.getOwner().retrieveFetchSourcePersister();
-		final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( entityFetch.getOwnerPropertyName() );
-
-		if ( EntityReference.class.isInstance( entityFetch.getOwner() ) ) {
-			this.lhsAlias = ( (EntityReference) entityFetch.getOwner() ).getSqlTableAlias();
-		}
-		else {
-			throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
-		}
-		final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) entityFetch.getOwner().retrieveFetchSourcePersister();
-		this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
-				entityFetch.getAssociationType(), lhsAlias, propertyNumber, ownerPersister, factory
+			Map<String, Filter> enabledFilters) throws MappingException {
+		this(
+				entityFetch,
+				entityFetch.getAssociationType(),
+				entityFetch,
+				currentCollectionReference,
+				withClause,
+				hasRestriction,
+				factory,
+				enabledFilters
 		);
-		this.rhsAlias = entityFetch.getSqlTableAlias();
-
-		final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
-		if ( entityFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
-			joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
-		}
-		else {
-			joinType = JoinType.NONE;
-		}
-		this.joinable = joinableType.getAssociatedJoinable(factory);
-		this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
-		this.currentEntitySuffix = entityFetch.getEntityAliases().getSuffix();
-		this.currentCollectionSuffix = currentCollectionSuffix;
-		this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
-				+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
-		this.hasRestriction = hasRestriction;
-		this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
 	}
 
 	public JoinableAssociationImpl(
 			CollectionFetch collectionFetch,
-			String currentEntitySuffix,
+			EntityReference currentEntityReference,
 			String withClause,
 			boolean hasRestriction,
 			SessionFactoryImplementor factory,
-			Map enabledFilters) throws MappingException {
-		this.propertyPath = collectionFetch.getPropertyPath();
-		final CollectionType collectionType =  collectionFetch.getCollectionPersister().getCollectionType();
-		this.joinableType = collectionType;
-		// TODO: this is not correct
-		final EntityPersister fetchSourcePersister = collectionFetch.getOwner().retrieveFetchSourcePersister();
-		final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( collectionFetch.getOwnerPropertyName() );
-
-		if ( EntityReference.class.isInstance( collectionFetch.getOwner() ) ) {
-			this.lhsAlias = ( (EntityReference) collectionFetch.getOwner() ).getSqlTableAlias();
-		}
-		else {
-			throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
-		}
-		final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) collectionFetch.getOwner().retrieveFetchSourcePersister();
-		this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
-				collectionType, lhsAlias, propertyNumber, ownerPersister, factory
+			Map<String, Filter> enabledFilters) throws MappingException {
+		this(
+				collectionFetch,
+				collectionFetch.getCollectionPersister().getCollectionType(),
+				currentEntityReference,
+				collectionFetch,
+				withClause,
+				hasRestriction,
+				factory,
+				enabledFilters
 		);
-		this.rhsAlias = collectionFetch.getAlias();
+	}
 
+	private JoinableAssociationImpl(
+			Fetch currentFetch,
+			AssociationType associationType,
+			EntityReference currentEntityReference,
+			CollectionReference currentCollectionReference,
+			String withClause,
+			boolean hasRestriction,
+			SessionFactoryImplementor factory,
+			Map<String, Filter> enabledFilters) throws MappingException {
+		this.propertyPath = currentFetch.getPropertyPath();
+		this.joinableType = associationType;
+		final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) currentFetch.getOwner().retrieveFetchSourcePersister();
+		final int propertyNumber = ownerPersister.getEntityMetamodel().getPropertyIndex( currentFetch.getOwnerPropertyName() );
 		final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
-		if ( collectionFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
+		if ( currentFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
 			joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
 		}
 		else {
 			joinType = JoinType.NONE;
 		}
 		this.joinable = joinableType.getAssociatedJoinable(factory);
 		this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
-		this.currentEntitySuffix = currentEntitySuffix;
-		this.currentCollectionSuffix = collectionFetch.getCollectionAliases().getSuffix();
-		this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
-				+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
+		this.currentFetch = currentFetch;
+		this.currentEntityReference = currentEntityReference;
+		this.currentCollectionReference = currentCollectionReference;
+		this.withClause = withClause;
 		this.hasRestriction = hasRestriction;
 		this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
 	}
 
+
+	/*
+					public JoinableAssociationImpl(
+						EntityFetch entityFetch,
+						String currentCollectionSuffix,
+						String withClause,
+						boolean hasRestriction,
+						SessionFactoryImplementor factory,
+						Map enabledFilters,
+						LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
+					this.propertyPath = entityFetch.getPropertyPath();
+					this.joinableType = entityFetch.getAssociationType();
+					// TODO: this is not correct
+					final EntityPersister fetchSourcePersister = entityFetch.getOwner().retrieveFetchSourcePersister();
+					final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( entityFetch.getOwnerPropertyName() );
+
+					if ( EntityReference.class.isInstance( entityFetch.getOwner() ) ) {
+						this.lhsAlias = aliasResolutionContext.resolveEntitySqlTableAlias( (EntityReference) entityFetch.getOwner() );
+					}
+					else {
+						throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
+					}
+					final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) entityFetch.getOwner().retrieveFetchSourcePersister();
+					this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
+							entityFetch.getAssociationType(), lhsAlias, propertyNumber, ownerPersister, factory
+					);
+					this.rhsAlias = aliasResolutionContext.resolveEntitySqlTableAlias( entityFetch );
+
+					final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
+					if ( entityFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
+						joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
+					}
+					else {
+						joinType = JoinType.NONE;
+					}
+					this.joinable = joinableType.getAssociatedJoinable(factory);
+					this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
+					this.currentEntitySuffix = 	aliasResolutionContext.resolveEntityColumnAliases( entityFetch ).getSuffix();
+					this.currentCollectionSuffix = currentCollectionSuffix;
+					this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
+							+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
+					this.hasRestriction = hasRestriction;
+					this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
+				}
+
+				public JoinableAssociationImpl(
+						CollectionFetch collectionFetch,
+						String currentEntitySuffix,
+						String withClause,
+						boolean hasRestriction,
+						SessionFactoryImplementor factory,
+						Map enabledFilters,
+						LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
+					this.propertyPath = collectionFetch.getPropertyPath();
+					final CollectionType collectionType =  collectionFetch.getCollectionPersister().getCollectionType();
+					this.joinableType = collectionType;
+					// TODO: this is not correct
+					final EntityPersister fetchSourcePersister = collectionFetch.getOwner().retrieveFetchSourcePersister();
+					final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( collectionFetch.getOwnerPropertyName() );
+
+					if ( EntityReference.class.isInstance( collectionFetch.getOwner() ) ) {
+						this.lhsAlias = aliasResolutionContext.resolveEntitySqlTableAlias( (EntityReference) collectionFetch.getOwner() );
+					}
+					else {
+						throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
+					}
+					final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) collectionFetch.getOwner().retrieveFetchSourcePersister();
+					this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
+							collectionType, lhsAlias, propertyNumber, ownerPersister, factory
+					);
+					this.rhsAlias = aliasResolutionContext.resolveCollectionSqlTableAlias( collectionFetch );
+
+					final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
+					if ( collectionFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
+						joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
+					}
+					else {
+						joinType = JoinType.NONE;
+					}
+					this.joinable = joinableType.getAssociatedJoinable(factory);
+					this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
+					this.currentEntitySuffix = currentEntitySuffix;
+					this.currentCollectionSuffix = aliasResolutionContext.resolveCollectionColumnAliases( collectionFetch ).getSuffix();
+					this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
+							+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
+					this.hasRestriction = hasRestriction;
+					this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
+				}
+				 */
+
+	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
+	@Override
 	public JoinType getJoinType() {
 		return joinType;
 	}
 
-	public String getLhsAlias() {
-		return lhsAlias;
-	}
-
-	public String getRHSAlias() {
-		return rhsAlias;
+	@Override
+	public Fetch getCurrentFetch() {
+		return currentFetch;
 	}
 
-	public String getCurrentEntitySuffix() {
-		return currentEntitySuffix;
+	@Override
+	public EntityReference getCurrentEntityReference() {
+		return currentEntityReference;
 	}
 
-	public String getCurrentCollectionSuffix() {
-		return currentCollectionSuffix;
+	@Override
+	public CollectionReference getCurrentCollectionReference() {
+		return currentCollectionReference;
 	}
 
 	private boolean isOneToOne() {
 		if ( joinableType.isEntityType() )  {
 			EntityType etype = (EntityType) joinableType;
 			return etype.isOneToOne() /*&& etype.isReferenceToPrimaryKey()*/;
 		}
 		else {
 			return false;
 		}
 	}
 
+	@Override
 	public AssociationType getJoinableType() {
 		return joinableType;
 	}
 
-	public String getRHSUniqueKeyName() {
-		return joinableType.getRHSUniqueKeyPropertyName();
-	}
-
+	@Override
 	public boolean isCollection() {
-		return joinableType.isCollectionType();
+		return getJoinableType().isCollectionType();
 	}
 
+	@Override
 	public Joinable getJoinable() {
 		return joinable;
 	}
 
 	public boolean hasRestriction() {
 		return hasRestriction;
 	}
 
-	public int getOwner(final List associations) {
-		if ( isOneToOne() || isCollection() ) {
-			return getPosition(lhsAlias, associations);
-		}
-		else {
-			return -1;
-		}
-	}
-
-	/**
-	 * Get the position of the join with the given alias in the
-	 * list of joins
-	 */
-	private static int getPosition(String lhsAlias, List associations) {
-		int result = 0;
-		for ( int i=0; i<associations.size(); i++ ) {
-			JoinableAssociationImpl oj = (JoinableAssociationImpl) associations.get(i);
-			if ( oj.getJoinable().consumesEntityAlias() /*|| oj.getJoinable().consumesCollectionAlias() */ ) {
-				if ( oj.rhsAlias.equals(lhsAlias) ) return result;
-				result++;
-			}
-		}
-		return -1;
-	}
-
-	public void addJoins(JoinFragment outerjoin) throws MappingException {
-		outerjoin.addJoin(
-				joinable.getTableName(),
-				rhsAlias,
-				lhsColumns,
-				rhsColumns,
-				joinType,
-				on
-		);
-		outerjoin.addJoins(
-				joinable.fromJoinFragment(rhsAlias, false, true),
-				joinable.whereJoinFragment(rhsAlias, false, true)
-		);
-	}
-
-	public void validateJoin(String path) throws MappingException {
-		if ( rhsColumns==null || lhsColumns==null
-				|| lhsColumns.length!=rhsColumns.length || lhsColumns.length==0 ) {
-			throw new MappingException("invalid join columns for association: " + path);
-		}
-	}
-
-	public boolean isManyToManyWith(JoinableAssociationImpl other) {
+	@Override
+	public boolean isManyToManyWith(JoinableAssociation other) {
 		if ( joinable.isCollection() ) {
 			QueryableCollection persister = ( QueryableCollection ) joinable;
 			if ( persister.isManyToMany() ) {
 				return persister.getElementType() == other.getJoinableType();
 			}
 		}
 		return false;
 	}
 
-	public void addManyToManyJoin(JoinFragment outerjoin, QueryableCollection collection) throws MappingException {
-		String manyToManyFilter = collection.getManyToManyFilterFragment( rhsAlias, enabledFilters );
-		String condition = "".equals( manyToManyFilter )
-				? on
-				: "".equals( on )
-				? manyToManyFilter
-				: on + " and " + manyToManyFilter;
-		outerjoin.addJoin(
-				joinable.getTableName(),
-				rhsAlias,
-				lhsColumns,
-				rhsColumns,
-				joinType,
-				condition
-		);
-		outerjoin.addJoins(
-				joinable.fromJoinFragment(rhsAlias, false, true),
-				joinable.whereJoinFragment(rhsAlias, false, true)
-		);
+	@Override
+	public String[] getRhsColumns() {
+		return rhsColumns;
+	}
+
+	@Override
+	public String getWithClause() {
+		return withClause;
+	}
+
+	@Override
+	public Map<String, Filter> getEnabledFilters() {
+		return enabledFilters;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
new file mode 100644
index 0000000000..688f57cce5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
@@ -0,0 +1,325 @@
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
+package org.hibernate.loader.internal;
+
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.cfg.NotYetImplementedException;
+import org.hibernate.engine.internal.JoinHelper;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.DefaultEntityAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.GeneratedCollectionAliases;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.plan.spi.Return;
+import org.hibernate.loader.plan.spi.ScalarReturn;
+import org.hibernate.loader.spi.JoinableAssociation;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Loadable;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.type.EntityType;
+
+/**
+ * @author Gail Badner
+ */
+public class LoadQueryAliasResolutionContextImpl implements LoadQueryAliasResolutionContext {
+	private final Map<Return,String[]> aliasesByReturn;
+	private final Map<EntityReference,LoadQueryEntityAliasesImpl> aliasesByEntityReference =
+			new HashMap<EntityReference,LoadQueryEntityAliasesImpl>();
+	private final Map<CollectionReference,LoadQueryCollectionAliasesImpl> aliasesByCollectionReference =
+			new HashMap<CollectionReference,LoadQueryCollectionAliasesImpl>();
+	private final Map<JoinableAssociation,JoinableAssociationAliasesImpl> aliasesByJoinableAssociation =
+			new HashMap<JoinableAssociation, JoinableAssociationAliasesImpl>();
+	private final SessionFactoryImplementor sessionFactory;
+
+	private int currentAliasSuffix = 0;
+
+	public LoadQueryAliasResolutionContextImpl(
+			SessionFactoryImplementor sessionFactory,
+			int suffixSeed,
+			Map<Return,String[]> aliasesByReturn) {
+		this.sessionFactory = sessionFactory;
+		this.currentAliasSuffix = suffixSeed;
+
+		checkAliasesByReturn( aliasesByReturn );
+		this.aliasesByReturn = new HashMap<Return, String[]>( aliasesByReturn );
+	}
+
+	private static void checkAliasesByReturn(Map<Return, String[]> aliasesByReturn) {
+		if ( aliasesByReturn == null || aliasesByReturn.size() == 0 ) {
+			throw new IllegalArgumentException( "No return aliases defined" );
+		}
+		for ( Map.Entry<Return,String[]> entry : aliasesByReturn.entrySet() ) {
+			final Return aReturn = entry.getKey();
+			final String[] aliases = entry.getValue();
+			if ( aReturn == null ) {
+				throw new IllegalArgumentException( "null key found in aliasesByReturn" );
+			}
+			if ( aliases == null || aliases.length == 0 ) {
+				throw new IllegalArgumentException(
+						String.format( "No alias defined for [%s]", aReturn )
+				);
+			}
+			if ( ( aliases.length > 1 ) &&
+					( aReturn instanceof EntityReturn || aReturn instanceof CollectionReturn ) ) {
+				throw new IllegalArgumentException( String.format( "More than 1 alias defined for [%s]", aReturn ) );
+			}
+			for ( String alias : aliases ) {
+				if ( StringHelper.isEmpty( alias ) ) {
+					throw new IllegalArgumentException( String.format( "An alias for [%s] is null or empty.", aReturn ) );
+				}
+			}
+		}
+	}
+
+	@Override
+	public String resolveEntityReturnAlias(EntityReturn entityReturn) {
+		return getAndCheckReturnAliasExists( entityReturn )[ 0 ];
+	}
+
+	@Override
+	public String resolveCollectionReturnAlias(CollectionReturn collectionReturn) {
+		return getAndCheckReturnAliasExists( collectionReturn )[ 0 ];
+	}
+
+	@Override
+	public String[] resolveScalarReturnAliases(ScalarReturn scalarReturn) {
+		throw new NotYetImplementedException( "Cannot resolve scalar column aliases yet." );
+	}
+
+	private String[] getAndCheckReturnAliasExists(Return aReturn) {
+		// There is already a check for the appropriate number of aliases stored in aliasesByReturn,
+		// so just check for existence here.
+		final String[] aliases = aliasesByReturn.get( aReturn );
+		if ( aliases == null ) {
+			throw new IllegalStateException(
+					String.format( "No alias is defined for [%s]", aReturn )
+			);
+		}
+		return aliases;
+	}
+
+	@Override
+	public String resolveEntitySqlTableAlias(EntityReference entityReference) {
+		return getOrGenerateLoadQueryEntityAliases( entityReference ).tableAlias;
+	}
+
+	@Override
+	public EntityAliases resolveEntityColumnAliases(EntityReference entityReference) {
+		return getOrGenerateLoadQueryEntityAliases( entityReference ).columnAliases;
+	}
+
+	@Override
+	public String resolveCollectionSqlTableAlias(CollectionReference collectionReference) {
+		return getOrGenerateLoadQueryCollectionAliases( collectionReference ).tableAlias;
+	}
+
+	@Override
+	public CollectionAliases resolveCollectionColumnAliases(CollectionReference collectionReference) {
+		return getOrGenerateLoadQueryCollectionAliases( collectionReference ).collectionAliases;
+	}
+
+	@Override
+	public EntityAliases resolveCollectionElementColumnAliases(CollectionReference collectionReference) {
+		return getOrGenerateLoadQueryCollectionAliases( collectionReference ).collectionElementAliases;
+	}
+
+	@Override
+	public String resolveRhsAlias(JoinableAssociation joinableAssociation) {
+		return getOrGenerateJoinAssocationAliases( joinableAssociation ).rhsAlias;
+	}
+
+	@Override
+	public String resolveLhsAlias(JoinableAssociation joinableAssociation) {
+		return getOrGenerateJoinAssocationAliases( joinableAssociation ).lhsAlias;
+	}
+
+	@Override
+	public String[] resolveAliasedLhsColumnNames(JoinableAssociation joinableAssociation) {
+		return getOrGenerateJoinAssocationAliases( joinableAssociation ).aliasedLhsColumnNames;
+	}
+
+	@Override
+	public EntityAliases resolveCurrentEntityAliases(JoinableAssociation joinableAssociation) {
+		return joinableAssociation.getCurrentEntityReference() == null ?
+				null:
+				resolveEntityColumnAliases( joinableAssociation.getCurrentEntityReference() );
+	}
+
+	@Override
+	public CollectionAliases resolveCurrentCollectionAliases(JoinableAssociation joinableAssociation) {
+		return joinableAssociation.getCurrentCollectionReference() == null ?
+				null:
+				resolveCollectionColumnAliases( joinableAssociation.getCurrentCollectionReference() );
+	}
+
+	protected SessionFactoryImplementor sessionFactory() {
+		return sessionFactory;
+	}
+
+	private String createSuffix() {
+		return Integer.toString( currentAliasSuffix++ ) + '_';
+	}
+
+	private LoadQueryEntityAliasesImpl getOrGenerateLoadQueryEntityAliases(EntityReference entityReference) {
+		LoadQueryEntityAliasesImpl aliases = aliasesByEntityReference.get( entityReference );
+		if ( aliases == null ) {
+			final EntityPersister entityPersister = entityReference.getEntityPersister();
+			aliases = new LoadQueryEntityAliasesImpl(
+					createTableAlias( entityPersister ),
+					createEntityAliases( entityPersister )
+			);
+			aliasesByEntityReference.put( entityReference, aliases );
+		}
+		return aliases;
+	}
+
+	private LoadQueryCollectionAliasesImpl getOrGenerateLoadQueryCollectionAliases(CollectionReference collectionReference) {
+		LoadQueryCollectionAliasesImpl aliases = aliasesByCollectionReference.get( collectionReference );
+		if ( aliases == null ) {
+			final CollectionPersister collectionPersister = collectionReference.getCollectionPersister();
+			aliases = new LoadQueryCollectionAliasesImpl(
+					createTableAlias( collectionPersister.getRole() ),
+					createCollectionAliases( collectionPersister ),
+					createCollectionElementAliases( collectionPersister )
+			);
+			aliasesByCollectionReference.put( collectionReference, aliases );
+		}
+		return aliases;
+	}
+
+	private JoinableAssociationAliasesImpl getOrGenerateJoinAssocationAliases(JoinableAssociation joinableAssociation) {
+		JoinableAssociationAliasesImpl aliases = aliasesByJoinableAssociation.get( joinableAssociation );
+		if ( aliases == null ) {
+			final Fetch currentFetch = joinableAssociation.getCurrentFetch();
+			final String lhsAlias;
+			if ( EntityReference.class.isInstance( currentFetch.getOwner() ) ) {
+				lhsAlias = resolveEntitySqlTableAlias( (EntityReference) currentFetch.getOwner() );
+			}
+			else {
+				throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference yet." );
+			}
+			final String rhsAlias;
+			if ( EntityReference.class.isInstance( currentFetch ) ) {
+				rhsAlias = resolveEntitySqlTableAlias( (EntityReference) currentFetch );
+			}
+			else if ( CollectionReference.class.isInstance( joinableAssociation.getCurrentFetch() ) ) {
+				rhsAlias = resolveCollectionSqlTableAlias( (CollectionReference) currentFetch );
+			}
+			else {
+				throw new NotYetImplementedException( "Cannot determine RHS alis for a fetch that is not an EntityReference or CollectionReference." );
+			}
+
+			// TODO: can't this be found in CollectionAliases or EntityAliases? should be moved to LoadQueryAliasResolutionContextImpl
+			final OuterJoinLoadable fetchSourcePersister = (OuterJoinLoadable) currentFetch.getOwner().retrieveFetchSourcePersister();
+			final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( currentFetch.getOwnerPropertyName() );
+			final String[] aliasedLhsColumnNames = JoinHelper.getAliasedLHSColumnNames(
+					joinableAssociation.getJoinableType(),
+					lhsAlias,
+					propertyNumber,
+					fetchSourcePersister,
+					sessionFactory
+			);
+
+			aliases = new JoinableAssociationAliasesImpl( lhsAlias, aliasedLhsColumnNames, rhsAlias );
+			aliasesByJoinableAssociation.put( joinableAssociation, aliases );
+		}
+		return aliases;
+	}
+
+	private String createTableAlias(EntityPersister entityPersister) {
+		return createTableAlias( StringHelper.unqualifyEntityName( entityPersister.getEntityName() ) );
+	}
+
+	private String createTableAlias(String name) {
+		return StringHelper.generateAlias( name ) + createSuffix();
+	}
+
+	private EntityAliases createEntityAliases(EntityPersister entityPersister) {
+		return new DefaultEntityAliases( (Loadable) entityPersister, createSuffix() );
+	}
+
+	private CollectionAliases createCollectionAliases(CollectionPersister collectionPersister) {
+		return new GeneratedCollectionAliases( collectionPersister, createSuffix() );
+	}
+
+	private EntityAliases createCollectionElementAliases(CollectionPersister collectionPersister) {
+		if ( !collectionPersister.getElementType().isEntityType() ) {
+			return null;
+		}
+		else {
+			final EntityType entityElementType = (EntityType) collectionPersister.getElementType();
+			return createEntityAliases( (EntityPersister) entityElementType.getAssociatedJoinable( sessionFactory() ) );
+		}
+	}
+
+	private static class LoadQueryEntityAliasesImpl {
+		private final String tableAlias;
+		private final EntityAliases columnAliases;
+
+		public LoadQueryEntityAliasesImpl(String tableAlias, EntityAliases columnAliases) {
+			this.tableAlias = tableAlias;
+			this.columnAliases = columnAliases;
+		}
+	}
+
+	private static class LoadQueryCollectionAliasesImpl {
+		private final String tableAlias;
+		private final CollectionAliases collectionAliases;
+		private final EntityAliases collectionElementAliases;
+
+		public LoadQueryCollectionAliasesImpl(
+				String tableAlias,
+				CollectionAliases collectionAliases,
+				EntityAliases collectionElementAliases) {
+			this.tableAlias = tableAlias;
+			this.collectionAliases = collectionAliases;
+			this.collectionElementAliases = collectionElementAliases;
+		}
+	}
+
+	private static class JoinableAssociationAliasesImpl {
+		private final String lhsAlias;
+		private final String[] aliasedLhsColumnNames;
+		private final String rhsAlias;
+
+		public JoinableAssociationAliasesImpl(
+				String lhsAlias,
+				String[] aliasedLhsColumnNames,
+				String rhsAlias) {
+			this.lhsAlias = lhsAlias;
+			this.aliasedLhsColumnNames = aliasedLhsColumnNames;
+			this.rhsAlias = rhsAlias;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
index 6899233b0b..49e51a4dfd 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
@@ -1,794 +1,803 @@
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
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitor;
 import org.hibernate.loader.spi.AfterLoadAction;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
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
+	private final LoadQueryAliasResolutionContext aliasResolutionContext;
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
+			LoadQueryAliasResolutionContext aliasResolutionContext,
 			boolean hadSubselectFetches) {
 		this.resultSet = resultSet;
 		this.session = session;
 		this.loadPlan = loadPlan;
 		this.readOnly = readOnly;
 		this.queryParameters = queryParameters;
 		this.namedParameterContext = namedParameterContext;
+		this.aliasResolutionContext = aliasResolutionContext;
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
+	public LoadQueryAliasResolutionContext getLoadQueryAliasResolutionContext() {
+		return aliasResolutionContext;
+	}
+
+	@Override
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
-							entityKeyContext.getEntityAliases(),
+							aliasResolutionContext.resolveEntityColumnAliases( entityKeyContext.getEntityReference() ),
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
-					entityKeyContext.getEntityAliases(),
+					aliasResolutionContext.resolveEntityColumnAliases( entityKeyContext.getEntityReference() ),
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
-					entityKeyContext.getEntityAliases(),
+					aliasResolutionContext.resolveEntityColumnAliases( entityKeyContext.getEntityReference() ),
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
 
 	public void readCollectionElements(final Object[] row) {
 			LoadPlanVisitor.visit(
 					loadPlan,
 					new LoadPlanVisitationStrategyAdapter() {
 						@Override
 						public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
 							readCollectionElement(
 									null,
 									null,
 									rootCollectionReturn.getCollectionPersister(),
-									rootCollectionReturn.getCollectionAliases(),
+									aliasResolutionContext.resolveCollectionColumnAliases( rootCollectionReturn ),
 									resultSet,
 									session
 							);
 						}
 
 						@Override
 						public void startingCollectionFetch(CollectionFetch collectionFetch) {
 							// TODO: determine which element is the owner.
 							final Object owner = row[ 0 ];
 							readCollectionElement(
 									owner,
 									collectionFetch.getCollectionPersister().getCollectionType().getKeyOfOwner( owner, session ),
 									collectionFetch.getCollectionPersister(),
-									collectionFetch.getCollectionAliases(),
+									aliasResolutionContext.resolveCollectionColumnAliases( collectionFetch ),
 									resultSet,
 									session
 							);
 						}
 
 						private void readCollectionElement(
 								final Object optionalOwner,
 								final Serializable optionalKey,
 								final CollectionPersister persister,
 								final CollectionAliases descriptor,
 								final ResultSet rs,
 								final SessionImplementor session) {
 
 							try {
 								final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 								final Serializable collectionRowKey = (Serializable) persister.readKey(
 										rs,
 										descriptor.getSuffixedKeyAliases(),
 										session
 								);
 
 								if ( collectionRowKey != null ) {
 									// we found a collection element in the result set
 
 									if ( LOG.isDebugEnabled() ) {
 										LOG.debugf( "Found row of collection: %s",
 												MessageHelper.collectionInfoString( persister, collectionRowKey, session.getFactory() ) );
 									}
 
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
 
 									if ( LOG.isDebugEnabled() ) {
 										LOG.debugf( "Result set contains (possibly empty) collection: %s",
 												MessageHelper.collectionInfoString( persister, optionalKey, session.getFactory() ) );
 									}
 
 									persistenceContext.getLoadContexts()
 											.getCollectionLoadContext( rs )
 											.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 								}
 
 								// else no collection element, but also no owner
 							}
 							catch ( SQLException sqle ) {
 								// TODO: would be nice to have the SQL string that failed...
 								throw session.getFactory().getSQLExceptionHelper().convert(
 										sqle,
 										"could not read next row of results"
 								);
 							}
 						}
 
 					}
 			);
 	}
 
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
 		if ( hadSubselectFetches ) {
 			if ( subselectLoadableEntityKeyMap == null ) {
 				subselectLoadableEntityKeyMap = new HashMap<EntityPersister, Set<EntityKey>>();
 			}
 			for ( HydratedEntityRegistration registration : currentRowHydratedEntityRegistrationList ) {
 				Set<EntityKey> entityKeys = subselectLoadableEntityKeyMap.get( registration.persister );
 				if ( entityKeys == null ) {
 					entityKeys = new HashSet<EntityKey>();
 					subselectLoadableEntityKeyMap.put( registration.persister, entityKeys );
 				}
 				entityKeys.add( registration.key );
 			}
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
 						endLoadingCollection( rootCollectionReturn.getCollectionPersister() );
 					}
 
 					@Override
 					public void startingCollectionFetch(CollectionFetch collectionFetch) {
 						endLoadingCollection( collectionFetch.getCollectionPersister() );
 					}
 
 					private void endLoadingCollection(CollectionPersister persister) {
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
index f21430e194..64dedf44fc 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java
@@ -1,220 +1,223 @@
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
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitor;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.loader.spi.LoadPlanAdvisor;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
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
 
 	private final LoadPlan baseLoadPlan;
 
 	private final boolean hadSubselectFetches;
 
 	public ResultSetProcessorImpl(LoadPlan loadPlan) {
 		this.baseLoadPlan = loadPlan;
 
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
 			LoadPlanAdvisor loadPlanAdvisor,
 			ResultSet resultSet,
 			final SessionImplementor session,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
+			LoadQueryAliasResolutionContext aliasResolutionContext,
 			boolean returnProxies,
 			boolean readOnly,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActionList) throws SQLException {
 
 		final LoadPlan loadPlan = loadPlanAdvisor.advise( this.baseLoadPlan );
 		if ( loadPlan == null ) {
 			throw new IllegalStateException( "LoadPlanAdvisor returned null" );
 		}
 
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
 
 		final ResultSetProcessingContextImpl context = new ResultSetProcessingContextImpl(
 				resultSet,
 				session,
 				loadPlan,
 				readOnly,
 //				true, // use optional entity key?  for now, always say yes
 				false, // use optional entity key?  actually for now always say no since in the simple test cases true causes failures because there is no optional key
 				queryParameters,
 				namedParameterContext,
+				aliasResolutionContext,
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
 				context.readCollectionElements( new Object[] { logicalRow } );
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
 				context.readCollectionElements( (Object[]) logicalRow );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/CascadeLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/CascadeLoadPlanBuilderStrategy.java
index ce19b46079..3f65720837 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/CascadeLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/CascadeLoadPlanBuilderStrategy.java
@@ -1,60 +1,58 @@
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
 package org.hibernate.loader.plan.internal;
 
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
 public class CascadeLoadPlanBuilderStrategy extends SingleRootReturnLoadPlanBuilderStrategy {
 	private static final FetchStrategy EAGER = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 	private static final FetchStrategy DELAYED = new FetchStrategy( FetchTiming.DELAYED, FetchStyle.SELECT );
 
 	private final CascadingAction cascadeActionToMatch;
 
 	public CascadeLoadPlanBuilderStrategy(
 			CascadingAction cascadeActionToMatch,
 			SessionFactoryImplementor sessionFactory,
-			LoadQueryInfluencers loadQueryInfluencers,
-			String rootAlias,
-			int suffixSeed) {
-		super( sessionFactory, loadQueryInfluencers, rootAlias, suffixSeed );
+			LoadQueryInfluencers loadQueryInfluencers) {
+		super( sessionFactory, loadQueryInfluencers );
 		this.cascadeActionToMatch = cascadeActionToMatch;
 	}
 
 	@Override
 	protected FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition) {
 		return attributeDefinition.determineCascadeStyle().doCascade( cascadeActionToMatch ) ? EAGER : DELAYED;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
index 0c85b7a1c3..8a645e1e2c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
@@ -1,98 +1,80 @@
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
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.AbstractFetchOwner;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CollectionDefinition;
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
-		final CollectionAliases collectionAliases = loadPlanBuildingContext.resolveCollectionColumnAliases( attributeDefinition );
-		final CollectionDefinition collectionDefinition = attributeDefinition.toCollectionDefinition();
-		final EntityAliases elementEntityAliases =
-				collectionDefinition.getElementDefinition().getType().isEntityType() ?
-						loadPlanBuildingContext.resolveEntityColumnAliases( attributeDefinition ) :
-						null;
-
 		return new CollectionFetch(
 				loadPlanBuildingContext.getSessionFactory(),
-				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
 				LockMode.NONE, // todo : for now
 				fetchOwner,
 				fetchStrategy,
-				attributeDefinition.getName(),
-				collectionAliases,
-				elementEntityAliases
+				attributeDefinition.getName()
 		);
 	}
 
 	public static EntityFetch buildStandardEntityFetch(
 			FetchOwner fetchOwner,
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 
 		return new EntityFetch(
 				loadPlanBuildingContext.getSessionFactory(),
-				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
 				LockMode.NONE, // todo : for now
 				fetchOwner,
 				attributeDefinition.getName(),
-				fetchStrategy,
-				sqlTableAlias,
-				loadPlanBuildingContext.resolveEntityColumnAliases( attributeDefinition )
+				fetchStrategy
 		);
 	}
 
 	public static CompositeFetch buildStandardCompositeFetch(
 			FetchOwner fetchOwner,
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return new CompositeFetch(
 				loadPlanBuildingContext.getSessionFactory(),
-				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
 				(AbstractFetchOwner) fetchOwner,
 				attributeDefinition.getName()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
index 474a108a56..aebb39fce7 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
@@ -1,194 +1,148 @@
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
 package org.hibernate.loader.plan.internal;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.build.AbstractLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
-import org.hibernate.type.EntityType;
-import org.hibernate.type.Type;
 
 /**
  * LoadPlanBuilderStrategy implementation used for building LoadPlans with a single processing RootEntity LoadPlan building.
  *
  * Really this is a single-root LoadPlan building strategy for building LoadPlans for:<ul>
  *     <li>entity load plans</li>
  *     <li>cascade load plans</li>
  *     <li>collection initializer plans</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public class SingleRootReturnLoadPlanBuilderStrategy
 		extends AbstractLoadPlanBuilderStrategy
 		implements LoadPlanBuilderStrategy {
 
 	private final LoadQueryInfluencers loadQueryInfluencers;
 
-	private final String rootAlias;
-
 	private Return rootReturn;
 
 	private PropertyPath propertyPath = new PropertyPath( "" );
 
 	public SingleRootReturnLoadPlanBuilderStrategy(
 			SessionFactoryImplementor sessionFactory,
-			LoadQueryInfluencers loadQueryInfluencers,
-			String rootAlias,
-			int suffixSeed) {
-		super( sessionFactory, suffixSeed );
+			LoadQueryInfluencers loadQueryInfluencers) {
+		super( sessionFactory );
 		this.loadQueryInfluencers = loadQueryInfluencers;
-		this.rootAlias = rootAlias;
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
 		return new LoadPlanImpl( false, rootReturn );
 	}
 
 	@Override
 	protected FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition) {
 		FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan( loadQueryInfluencers, propertyPath );
 		if ( fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 			// see if we need to alter the join fetch to another form for any reason
 			fetchStrategy = adjustJoinFetchIfNeeded( attributeDefinition, fetchStrategy );
 		}
 		return fetchStrategy;
 	}
 
 	protected FetchStrategy adjustJoinFetchIfNeeded(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy) {
 		if ( currentDepth() > sessionFactory().getSettings().getMaximumFetchDepth() ) {
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
 		return false;
 	}
 
 	@Override
 	protected EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition) {
 		final String entityName = entityDefinition.getEntityPersister().getEntityName();
 		return new EntityReturn(
 				sessionFactory(),
-				rootAlias,
 				LockMode.NONE, // todo : for now
-				entityName,
-				StringHelper.generateAlias( StringHelper.unqualifyEntityName( entityName ), currentDepth() ),
-				generateEntityColumnAliases( entityDefinition.getEntityPersister() )
+				entityName
 		);
 	}
 
 	@Override
 	protected CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition) {
 		final CollectionPersister persister = collectionDefinition.getCollectionPersister();
 		final String collectionRole = persister.getRole();
-
-		final CollectionAliases collectionAliases = generateCollectionColumnAliases(
-				collectionDefinition.getCollectionPersister()
-		);
-
-		final Type elementType = collectionDefinition.getCollectionPersister().getElementType();
-		final EntityAliases elementAliases;
-		if ( elementType.isEntityType() ) {
-			final EntityType entityElementType = (EntityType) elementType;
-			elementAliases = generateEntityColumnAliases(
-					(EntityPersister) entityElementType.getAssociatedJoinable( sessionFactory() )
-			);
-		}
-		else {
-			elementAliases = null;
-		}
-
 		return new CollectionReturn(
 				sessionFactory(),
-				rootAlias,
 				LockMode.NONE, // todo : for now
 				persister.getOwnerEntityPersister().getEntityName(),
-				StringHelper.unqualify( collectionRole ),
-				collectionAliases,
-				elementAliases
+				StringHelper.unqualify( collectionRole )
 		);
 	}
-
-
-	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	@Override
-	public String resolveRootSourceAlias(EntityDefinition definition) {
-		return rootAlias;
-	}
-
-	@Override
-	public String resolveRootSourceAlias(CollectionDefinition definition) {
-		return rootAlias;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractCollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractCollectionReference.java
index 6d413f8c23..ea1c61659d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractCollectionReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractCollectionReference.java
@@ -1,158 +1,126 @@
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
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractCollectionReference extends AbstractPlanNode implements CollectionReference {
-	private final String alias;
 	private final LockMode lockMode;
 	private final CollectionPersister collectionPersister;
 	private final PropertyPath propertyPath;
 
-	private final CollectionAliases collectionAliases;
-	private final EntityAliases elementEntityAliases;
-
 	private final FetchableCollectionIndex indexGraph;
 	private final FetchableCollectionElement elementGraph;
 
 	protected AbstractCollectionReference(
 			SessionFactoryImplementor sessionFactory,
-			String alias,
 			LockMode lockMode,
 			CollectionPersister collectionPersister,
-			PropertyPath propertyPath,
-			CollectionAliases collectionAliases,
-			EntityAliases elementEntityAliases) {
+			PropertyPath propertyPath) {
 		super( sessionFactory );
-		this.alias = alias;
 		this.lockMode = lockMode;
 		this.collectionPersister = collectionPersister;
 		this.propertyPath = propertyPath;
 
-		this.collectionAliases = collectionAliases;
-		this.elementEntityAliases = elementEntityAliases;
-
 		this.indexGraph = buildIndexGraph( getCollectionPersister() );
 		this.elementGraph = buildElementGraph( getCollectionPersister() );
 	}
 
 	private FetchableCollectionIndex buildIndexGraph(CollectionPersister persister) {
 		if ( persister.hasIndex() ) {
 			final Type type = persister.getIndexType();
 			if ( type.isAssociationType() ) {
 				if ( type.isEntityType() ) {
 					return new EntityIndexGraph( sessionFactory(), this, getPropertyPath() );
 				}
 			}
 			else if ( type.isComponentType() ) {
 				return new CompositeIndexGraph( sessionFactory(), this, getPropertyPath() );
 			}
 		}
 
 		return null;
 	}
 
 	private FetchableCollectionElement buildElementGraph(CollectionPersister persister) {
 		final Type type = persister.getElementType();
 		if ( type.isAssociationType() ) {
 			if ( type.isEntityType() ) {
 				return new EntityElementGraph( sessionFactory(), this, getPropertyPath() );
 			}
 		}
 		else if ( type.isComponentType() ) {
 			return new CompositeElementGraph( sessionFactory(), this, getPropertyPath() );
 		}
 
 		return null;
 	}
 
 	protected AbstractCollectionReference(AbstractCollectionReference original, CopyContext copyContext) {
 		super( original );
-		this.alias = original.alias;
 		this.lockMode = original.lockMode;
 		this.collectionPersister = original.collectionPersister;
 		this.propertyPath = original.propertyPath;
 
-		this.collectionAliases = original.collectionAliases;
-		this.elementEntityAliases = original.elementEntityAliases;
-
 		this.indexGraph = original.indexGraph == null ? null : original.indexGraph.makeCopy( copyContext );
 		this.elementGraph = original.elementGraph == null ? null : original.elementGraph.makeCopy( copyContext );
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
 	@Override
-	public String getAlias() {
-		return alias;
-	}
-
-	@Override
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	@Override
-	public CollectionAliases getCollectionAliases() {
-		return collectionAliases;
-	}
-
-	@Override
-	public EntityAliases getElementEntityAliases() {
-		return elementEntityAliases;
-	}
-
-	@Override
 	public CollectionPersister getCollectionPersister() {
 		return collectionPersister;
 	}
 
 	@Override
 	public FetchOwner getIndexGraph() {
 		return indexGraph;
 	}
 
 	@Override
 	public FetchOwner getElementGraph() {
 		return elementGraph;
 	}
 
 	@Override
 	public boolean hasEntityElements() {
 		return getCollectionPersister().isOneToMany() || getCollectionPersister().isManyToMany();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
index 393ae377aa..ffb5656f12 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
@@ -1,106 +1,95 @@
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
 
-import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractFetchOwner extends AbstractPlanNode implements FetchOwner {
-	private final String alias;
 	private final LockMode lockMode;
 
 	private List<Fetch> fetches;
 
-	public AbstractFetchOwner(SessionFactoryImplementor factory, String alias, LockMode lockMode) {
+	public AbstractFetchOwner(SessionFactoryImplementor factory, LockMode lockMode) {
 		super( factory );
-		this.alias = alias;
 		this.lockMode = lockMode;
 		validate();
 	}
 
 	private void validate() {
-		if ( alias == null ) {
-			throw new HibernateException( "alias must be specified" );
-		}
 	}
 
 	/**
 	 * A "copy" constructor.  Used while making clones/copies of this.
 	 *
 	 * @param original
 	 */
 	protected AbstractFetchOwner(AbstractFetchOwner original, CopyContext copyContext) {
 		super( original );
-		this.alias = original.alias;
 		this.lockMode = original.lockMode;
 		validate();
 
 		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
 		if ( fetches == null || fetches.size() == 0 ) {
 			this.fetches = Collections.emptyList();
 		}
 		else {
 			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
 			for ( Fetch fetch : fetches ) {
 				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
 			}
 			this.fetches = fetchesCopy;
 		}
 		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
 	}
 
-	public String getAlias() {
-		return alias;
-	}
-
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	@Override
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
index 2a30a69224..ddb4b4491f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractSingularAttributeFetch.java
@@ -1,104 +1,103 @@
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
 import org.hibernate.LockMode;
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
-			String alias,
 			LockMode lockMode,
 			FetchOwner owner,
 			String ownerProperty,
 			FetchStrategy fetchStrategy) {
-		super( factory, alias, lockMode );
+		super( factory, lockMode );
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
index ebecfefb23..fef9cd1b65 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
@@ -1,107 +1,99 @@
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
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
+import org.hibernate.type.CollectionType;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionFetch extends AbstractCollectionReference implements Fetch {
 	private final FetchOwner fetchOwner;
 	private final FetchStrategy fetchStrategy;
 
 	public CollectionFetch(
 			SessionFactoryImplementor sessionFactory,
-			String alias,
 			LockMode lockMode,
 			FetchOwner fetchOwner,
 			FetchStrategy fetchStrategy,
-			String ownerProperty,
-			CollectionAliases collectionAliases,
-			EntityAliases elementEntityAliases) {
+			String ownerProperty) {
 		super(
 				sessionFactory,
-				alias,
 				lockMode,
 				sessionFactory.getCollectionPersister(
 						fetchOwner.retrieveFetchSourcePersister().getEntityName() + '.' + ownerProperty
 				),
-				fetchOwner.getPropertyPath().append( ownerProperty ),
-				collectionAliases,
-				elementEntityAliases
+				fetchOwner.getPropertyPath().append( ownerProperty )
 		);
 		this.fetchOwner = fetchOwner;
 		this.fetchStrategy = fetchStrategy;
-
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
index c67f803793..83dec611cc 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
@@ -1,82 +1,58 @@
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
 
 import org.hibernate.LockMode;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Represents a reference to an owned collection either as a return or as a fetch
  *
  * @author Steve Ebersole
  */
 public interface CollectionReference {
-	/**
-	 * Retrieve the alias associated with the persister (entity/collection).
-	 *
-	 * @return The alias
-	 */
-	public String getAlias();
 
 	/**
 	 * Retrieve the lock mode associated with this return.
 	 *
 	 * @return The lock mode.
 	 */
 	public LockMode getLockMode();
 
 	/**
 	 * Retrieves the CollectionPersister describing the collection associated with this Return.
 	 *
 	 * @return The CollectionPersister.
 	 */
 	public CollectionPersister getCollectionPersister();
 
 	public FetchOwner getIndexGraph();
 
 	public FetchOwner getElementGraph();
 
 	public PropertyPath getPropertyPath();
 
 	public boolean hasEntityElements();
-
-	/**
-	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to the
-	 * this collection.
-	 *
-	 * @return The ResultSet alias descriptor for the collection
-	 */
-	public CollectionAliases getCollectionAliases();
-
-	/**
-	 * If the elements of this collection are entities, this methods returns the JDBC ResultSet alias descriptions
-	 * for that entity; {@code null} indicates a non-entity collection.
-	 *
-	 * @return The ResultSet alias descriptor for the collection's entity element, or {@code null}
-	 */
-	public EntityAliases getElementEntityAliases();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
index b131334f7f..933533ec80 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
@@ -1,112 +1,104 @@
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
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionReturn extends AbstractCollectionReference implements Return, CopyableReturn {
 	private final String ownerEntityName;
 	private final String ownerProperty;
 
 	public CollectionReturn(
 			SessionFactoryImplementor sessionFactory,
-			String alias,
 			LockMode lockMode,
 			String ownerEntityName,
-			String ownerProperty,
-			CollectionAliases collectionAliases,
-			EntityAliases elementEntityAliases) {
+			String ownerProperty) {
 		super(
 				sessionFactory,
-				alias,
 				lockMode,
 				sessionFactory.getCollectionPersister( ownerEntityName + '.' + ownerProperty ),
-				new PropertyPath(), // its a root
-				collectionAliases,
-				elementEntityAliases
+				new PropertyPath() // its a root
 		);
 		this.ownerEntityName = ownerEntityName;
 		this.ownerProperty = ownerProperty;
 	}
 
 	public CollectionReturn(CollectionReturn original, CopyContext copyContext) {
 		super( original, copyContext );
 		this.ownerEntityName = original.ownerEntityName;
 		this.ownerProperty = original.ownerProperty;
 	}
 
 	/**
 	 * Returns the class owning the collection.
 	 *
 	 * @return The class owning the collection.
 	 */
 	public String getOwnerEntityName() {
 		return ownerEntityName;
 	}
 
 	/**
 	 * Returns the name of the property representing the collection from the {@link #getOwnerEntityName}.
 	 *
 	 * @return The name of the property representing the collection on the owner class.
 	 */
 	public String getOwnerProperty() {
 		return ownerProperty;
 	}
 
 	@Override
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		// todo : anything to do here?
 	}
 
 	@Override
 	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		// todo : anything to do here?
 	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public String toString() {
 		return "CollectionReturn(" + getCollectionPersister().getRole() + ")";
 	}
 
 	@Override
 	public CollectionReturn makeCopy(CopyContext copyContext) {
 		return new CollectionReturn( this, copyContext );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
index e07fbd1fa4..b1ddd79d30 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
@@ -1,120 +1,118 @@
 package org.hibernate.loader.plan.spi;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeElementGraph extends AbstractPlanNode implements FetchableCollectionElement {
 	private final CollectionReference collectionReference;
 	private final PropertyPath propertyPath;
 	private final CollectionPersister collectionPersister;
 
 	private List<Fetch> fetches;
 
 	public CompositeElementGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath collectionPath) {
 		super( sessionFactory );
 
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.propertyPath = collectionPath.append( "<elements>" );
 	}
 
 	public CompositeElementGraph(CompositeElementGraph original, CopyContext copyContext) {
 		super( original );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionPersister;
 		this.propertyPath = original.propertyPath;
 
 		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
 		if ( fetches == null || fetches.size() == 0 ) {
 			this.fetches = Collections.emptyList();
 		}
 		else {
 			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
 			for ( Fetch fetch : fetches ) {
 				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
 			}
 			this.fetches = fetchesCopy;
 		}
 		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
 	}
 
 	@Override
 	public void addFetch(Fetch fetch) {
 		if ( fetches == null ) {
 			fetches = new ArrayList<Fetch>();
 		}
 		fetches.add( fetch );
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
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
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		throw new HibernateException( "Collection composite element cannot define collections" );
 	}
 
 	@Override
 	public EntityFetch buildEntityFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardEntityFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
-				sqlTableAlias,
 				loadPlanBuildingContext
 		);
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
 	}
 
 	@Override
 	public CompositeElementGraph makeCopy(CopyContext copyContext) {
 		return new CompositeElementGraph( this, copyContext );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
index db6f8e44be..0d1a9fddd0 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
@@ -1,102 +1,101 @@
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
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeFetch extends AbstractSingularAttributeFetch {
 	public static final FetchStrategy FETCH_PLAN = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 
 	public CompositeFetch(
 			SessionFactoryImplementor sessionFactory,
-			String alias,
 			FetchOwner owner,
 			String ownerProperty) {
-		super( sessionFactory, alias, LockMode.NONE, owner, ownerProperty, FETCH_PLAN );
+		super( sessionFactory, LockMode.NONE, owner, ownerProperty, FETCH_PLAN );
 	}
 
 	public CompositeFetch(CompositeFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
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
-			String sqlTableAlias, LoadPlanBuildingContext loadPlanBuildingContext) {
+			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
index 793215c96d..bccc7b1cd7 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
@@ -1,119 +1,117 @@
 package org.hibernate.loader.plan.spi;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeIndexGraph extends AbstractPlanNode implements FetchableCollectionIndex {
 	private final CollectionReference collectionReference;
 	private final PropertyPath propertyPath;
 	private final CollectionPersister collectionPersister;
 
 	private List<Fetch> fetches;
 
 	public CompositeIndexGraph(
 			SessionFactoryImplementor sessionFactory,
 			CollectionReference collectionReference,
 			PropertyPath propertyPath) {
 		super( sessionFactory );
 		this.collectionReference = collectionReference;
 		this.collectionPersister = collectionReference.getCollectionPersister();
 		this.propertyPath = propertyPath.append( "<index>" );
 	}
 
 	protected CompositeIndexGraph(CompositeIndexGraph original, CopyContext copyContext) {
 		super( original );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionPersister;
 		this.propertyPath = original.propertyPath;
 
 		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
 		if ( fetches == null || fetches.size() == 0 ) {
 			this.fetches = Collections.emptyList();
 		}
 		else {
 			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
 			for ( Fetch fetch : fetches ) {
 				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
 			}
 			this.fetches = fetchesCopy;
 		}
 		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
 	}
 
 	@Override
 	public void addFetch(Fetch fetch) {
 		if ( fetches == null ) {
 			fetches = new ArrayList<Fetch>();
 		}
 		fetches.add( fetch );
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
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
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		throw new HibernateException( "Composite index cannot define collections" );
 	}
 
 	@Override
 	public EntityFetch buildEntityFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardEntityFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
-				sqlTableAlias,
 				loadPlanBuildingContext
 		);
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
 	}
 
 	@Override
 	public CompositeIndexGraph makeCopy(CopyContext copyContext) {
 		return new CompositeIndexGraph( this, copyContext );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
index d713a4b819..d779a119ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
@@ -1,176 +1,163 @@
 package org.hibernate.loader.plan.spi;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.AssociationType;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityElementGraph extends AbstractPlanNode implements FetchableCollectionElement, EntityReference {
 	private final CollectionReference collectionReference;
 	private final CollectionPersister collectionPersister;
 	private final AssociationType elementType;
 	private final EntityPersister elementPersister;
 	private final PropertyPath propertyPath;
 
 	private List<Fetch> fetches;
 
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
 		this.propertyPath = collectionPath.append( "<elements>" );
 	}
 
 	public EntityElementGraph(EntityElementGraph original, CopyContext copyContext) {
 		super( original );
 
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionReference.getCollectionPersister();
 		this.elementType = original.elementType;
 		this.elementPersister = original.elementPersister;
 		this.propertyPath = original.propertyPath;
 
 		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
 		if ( fetches == null || fetches.size() == 0 ) {
 			this.fetches = Collections.emptyList();
 		}
 		else {
 			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
 			for ( Fetch fetch : fetches ) {
 				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
 			}
 			this.fetches = fetchesCopy;
 		}
 		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
 	}
 
 	@Override
-	public String getAlias() {
+	public LockMode getLockMode() {
 		return null;
 	}
 
 	@Override
-	public String getSqlTableAlias() {
-		return null;  //To change body of implemented methods use File | Settings | File Templates.
-	}
-
-	@Override
-	public LockMode getLockMode() {
-		return null;
+	public EntityReference getEntityReference() {
+		return this;
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
-	public EntityAliases getEntityAliases() {
-		return collectionReference.getElementEntityAliases();
-	}
-
-	@Override
 	public void addFetch(Fetch fetch) {
 		if ( fetches == null ) {
 			fetches = new ArrayList<Fetch>();
 		}
 		fetches.add( fetch );
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
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
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardEntityFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
-				sqlTableAlias,
 				loadPlanBuildingContext
 		);
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
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
 	public String toString() {
 		return "EntityElementGraph(collection=" + collectionPersister.getRole() + ", type=" + elementPersister.getEntityName() + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
index 66ea3a19d2..444e80cdc1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
@@ -1,287 +1,270 @@
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
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityFetch extends AbstractSingularAttributeFetch implements EntityReference {
-	private final String sqlTableAlias;
-	private final EntityAliases entityAliases;
 
 	private final EntityType associationType;
 	private final EntityPersister persister;
 
 	private IdentifierDescription identifierDescription;
 
 	public EntityFetch(
 			SessionFactoryImplementor sessionFactory,
-			String alias,
 			LockMode lockMode,
 			FetchOwner owner,
 			String ownerProperty,
-			FetchStrategy fetchStrategy,
-			String sqlTableAlias,
-			EntityAliases entityAliases) {
-		super( sessionFactory, alias, lockMode, owner, ownerProperty, fetchStrategy );
-		this.sqlTableAlias = sqlTableAlias;
-		this.entityAliases = entityAliases;
+			FetchStrategy fetchStrategy) {
+		super( sessionFactory, lockMode, owner, ownerProperty, fetchStrategy );
 
 		this.associationType = (EntityType) owner.retrieveFetchSourcePersister().getPropertyType( ownerProperty );
 		this.persister = sessionFactory.getEntityPersister( associationType.getAssociatedEntityName() );
 	}
 
 	/**
 	 * Copy constructor.
 	 *
 	 * @param original The original fetch
 	 * @param copyContext Access to contextual needs for the copy operation
 	 */
 	protected EntityFetch(EntityFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
-		this.sqlTableAlias = original.sqlTableAlias;
-		this.entityAliases = original.entityAliases;
 		this.associationType = original.associationType;
 		this.persister = original.persister;
 	}
 
 	public EntityType getAssociationType() {
 		return associationType;
 	}
 
 	@Override
+	public EntityReference getEntityReference() {
+		return this;
+	}
+
+	@Override
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	@Override
 	public IdentifierDescription getIdentifierDescription() {
 		return identifierDescription;
 	}
 
 	@Override
-	public String getSqlTableAlias() {
-		return sqlTableAlias;
-	}
-
-	@Override
-	public EntityAliases getEntityAliases() {
-		return entityAliases;
-	}
-
-	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return persister;
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
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardEntityFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
-				sqlTableAlias,
 				loadPlanBuildingContext
 		);
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
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
-							entityAliases,
+							context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( this ),
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
-					entityAliases,
+					context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( this ),
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
-					entityAliases,
+					context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( this ),
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
index fcbece5f6b..ed12d31a99 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
@@ -1,192 +1,179 @@
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.AssociationType;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityIndexGraph extends AbstractPlanNode implements FetchableCollectionIndex, EntityReference {
 	private final CollectionReference collectionReference;
 	private final CollectionPersister collectionPersister;
 	private final AssociationType indexType;
 	private final EntityPersister indexPersister;
 	private final PropertyPath propertyPath;
 
 	private List<Fetch> fetches;
 
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
 	}
 
 	public EntityIndexGraph(EntityIndexGraph original, CopyContext copyContext) {
 		super( original );
 		this.collectionReference = original.collectionReference;
 		this.collectionPersister = original.collectionReference.getCollectionPersister();
 		this.indexType = original.indexType;
 		this.indexPersister = original.indexPersister;
 		this.propertyPath = original.propertyPath;
 
 		copyContext.getReturnGraphVisitationStrategy().startingFetches( original );
 		if ( fetches == null || fetches.size() == 0 ) {
 			this.fetches = Collections.emptyList();
 		}
 		else {
 			List<Fetch> fetchesCopy = new ArrayList<Fetch>();
 			for ( Fetch fetch : fetches ) {
 				fetchesCopy.add( fetch.makeCopy( copyContext, this ) );
 			}
 			this.fetches = fetchesCopy;
 		}
 		copyContext.getReturnGraphVisitationStrategy().finishingFetches( original );
 	}
 
 	@Override
-	public String getAlias() {
+	public LockMode getLockMode() {
 		return null;
 	}
 
 	@Override
-	public String getSqlTableAlias() {
-		return null;  //To change body of implemented methods use File | Settings | File Templates.
-	}
-
-	@Override
-	public LockMode getLockMode() {
-		return null;
+	public EntityReference getEntityReference() {
+		return this;
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
-	public EntityAliases getEntityAliases() {
-		return null;
-	}
-
-	@Override
 	public void addFetch(Fetch fetch) {
 		if ( fetches == null ) {
 			fetches = new ArrayList<Fetch>();
 		}
 		fetches.add( fetch );
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
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
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardEntityFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
-				sqlTableAlias,
 				loadPlanBuildingContext
 		);
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
 	}
 
 	@Override
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
 	@Override
 	public EntityIndexGraph makeCopy(CopyContext copyContext) {
 		return new EntityIndexGraph( this, copyContext );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
index b6158cf681..3f646cc9a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
@@ -1,74 +1,52 @@
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
 
 import org.hibernate.LockMode;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Represents a reference to an entity either as a return or as a fetch
  *
  * @author Steve Ebersole
  */
 public interface EntityReference
 		extends IdentifierDescriptionInjectable, ResultSetProcessingContext.EntityKeyResolutionContext {
 	/**
-	 * Retrieve the alias associated with the persister (entity/collection).
-	 *
-	 * @return The alias
-	 */
-	public String getAlias();
-
-	/**
-	 * Retrieve the SQL table alias.
-	 *
-	 * @return The SQL table alias
-	 */
-	public String getSqlTableAlias();
-
-	/**
 	 * Retrieve the lock mode associated with this return.
 	 *
 	 * @return The lock mode.
 	 */
 	public LockMode getLockMode();
 
 	/**
 	 * Retrieves the EntityPersister describing the entity associated with this Return.
 	 *
 	 * @return The EntityPersister.
 	 */
 	public EntityPersister getEntityPersister();
 
 	public IdentifierDescription getIdentifierDescription();
-
-	/**
-	 * Ugh.  *Really* hate this here.
-	 *
-	 * @return
-	 */
-	public EntityAliases getEntityAliases();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
index b73f15f512..2d3bb88b52 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
@@ -1,221 +1,197 @@
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
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 import static org.hibernate.loader.spi.ResultSetProcessingContext.IdentifierResolutionContext;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReturn extends AbstractFetchOwner implements Return, EntityReference, CopyableReturn {
 
-	private final EntityAliases entityAliases;
-	private final String sqlTableAlias;
-
 	private final EntityPersister persister;
 
 	private final PropertyPath propertyPath = new PropertyPath(); // its a root
 
 	private IdentifierDescription identifierDescription;
 
 	public EntityReturn(
 			SessionFactoryImplementor sessionFactory,
-			String alias,
 			LockMode lockMode,
-			String entityName,
-			String sqlTableAlias,
-			EntityAliases entityAliases) {
-		super( sessionFactory, alias, lockMode );
-		this.entityAliases = entityAliases;
-		this.sqlTableAlias = sqlTableAlias;
+			String entityName) {
+		super( sessionFactory, lockMode );
 
 		this.persister = sessionFactory.getEntityPersister( entityName );
 	}
 
 	protected EntityReturn(EntityReturn original, CopyContext copyContext) {
 		super( original, copyContext );
-		this.entityAliases = original.entityAliases;
-		this.sqlTableAlias = original.sqlTableAlias;
 		this.persister = original.persister;
 	}
-
-	@Override
-	public String getAlias() {
-		return super.getAlias();
-	}
-
 	@Override
-	public String getSqlTableAlias() {
-		return sqlTableAlias;
+	public LockMode getLockMode() {
+		return super.getLockMode();
 	}
 
 	@Override
-	public LockMode getLockMode() {
-		return super.getLockMode();
+	public EntityReference getEntityReference() {
+		return this;
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
-	public EntityAliases getEntityAliases() {
-		return entityAliases;
-	}
-
-	@Override
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
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardEntityFetch(
 				this,
 				attributeDefinition,
 				fetchStrategy,
-				sqlTableAlias,
 				loadPlanBuildingContext
 		);
 	}
 
 	@Override
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
index ada44e90cc..102180bdb1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
@@ -1,96 +1,95 @@
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
-			String sqlTableAlias,
 			LoadPlanBuildingContext loadPlanBuildingContext);
 
 	public CompositeFetch buildCompositeFetch(
 			CompositionDefinition attributeDefinition,
 			LoadPlanBuildingContext loadPlanBuildingContext);
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
index d8a86ef117..39fb4f01ae 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
@@ -1,68 +1,69 @@
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
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.internal.ResultSetProcessingContextImpl;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.type.Type;
 
 /**
  * Represent a simple scalar return within a query result.  Generally this would be values of basic (String, Integer,
  * etc) or composite types.
  *
  * @author Steve Ebersole
  */
 public class ScalarReturn extends AbstractPlanNode implements Return {
 	private final Type type;
-	private final String[] columnAliases;
 
-	public ScalarReturn(SessionFactoryImplementor factory, Type type, String[] columnAliases) {
+	public ScalarReturn(SessionFactoryImplementor factory, Type type) {
 		super( factory );
 		this.type = type;
-		this.columnAliases = columnAliases;
 	}
 
 	public Type getType() {
 		return type;
 	}
 
 	@Override
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) {
 		// nothing to do
 	}
 
 	@Override
 	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) {
 		// nothing to do
 	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		return type.nullSafeGet( resultSet, columnAliases, context.getSession(), null );
+		return type.nullSafeGet(
+				resultSet,
+				context.getLoadQueryAliasResolutionContext().resolveScalarReturnAliases( this ),
+				context.getSession(),
+				null );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
index 33a3ebd194..e6968905d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
@@ -1,798 +1,729 @@
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
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.DefaultEntityAliases;
-import org.hibernate.loader.EntityAliases;
-import org.hibernate.loader.GeneratedCollectionAliases;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.IdentifierDescription;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.ResultSetProcessingContext;
-import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.entity.Loadable;
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
 
-	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory, int suffixSeed) {
+	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
-		this.currentSuffixBase = suffixSeed;
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
 			pushToCollectionStack( (CollectionReference) associationFetch );
 		}
 		else {
 			associationFetch = fetchOwner.buildEntityFetch(
 					attributeDefinition,
 					fetchStrategy,
-					generateEntityFetchSqlTableAlias( attributeDefinition.toEntityDefinition().getEntityPersister().getEntityName() ),
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
 
-	private int currentSuffixBase;
-	private int implicitAliasUniqueness = 0;
-
-	private String createImplicitAlias() {
-		return "ia" + implicitAliasUniqueness++;
-	}
-
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory();
 	}
 
-	protected String generateEntityFetchSqlTableAlias(String entityName) {
-		return StringHelper.generateAlias( StringHelper.unqualifyEntityName( entityName ), currentDepth() );
-	}
-
-	@Override
-	public EntityAliases resolveEntityColumnAliases(AssociationAttributeDefinition attributeDefinition) {
-		return generateEntityColumnAliases( attributeDefinition.toEntityDefinition().getEntityPersister() );
-	}
-
-	protected EntityAliases generateEntityColumnAliases(EntityPersister persister) {
-		return new DefaultEntityAliases( (Loadable) persister, Integer.toString( currentSuffixBase++ ) + '_' );
-	}
-
-	@Override
-	public CollectionAliases resolveCollectionColumnAliases(AssociationAttributeDefinition attributeDefinition) {
-		return generateCollectionColumnAliases( attributeDefinition.toCollectionDefinition().getCollectionPersister() );
-	}
-
-	protected CollectionAliases generateCollectionColumnAliases(CollectionPersister persister) {
-		return new GeneratedCollectionAliases( persister, Integer.toString( currentSuffixBase++ ) + '_' );
-	}
-
-	@Override
-	public String resolveRootSourceAlias(EntityDefinition definition) {
-		return createImplicitAlias();
-	}
-
-	@Override
-	public String resolveRootSourceAlias(CollectionDefinition definition) {
-		return createImplicitAlias();
-	}
-
-	@Override
-	public String resolveFetchSourceAlias(AssociationAttributeDefinition attributeDefinition) {
-		return createImplicitAlias();
-	}
-
-	@Override
-	public String resolveFetchSourceAlias(CompositionDefinition compositionDefinition) {
-		return createImplicitAlias();
-	}
-
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
-		public String getAlias() {
-			return entityReference.getAlias();
-		}
-
-		@Override
-		public String getSqlTableAlias() {
-			return entityReference.getSqlTableAlias();
+		public LockMode getLockMode() {
+			return entityReference.getLockMode();
 		}
 
 		@Override
-		public LockMode getLockMode() {
-			return entityReference.getLockMode();
+		public EntityReference getEntityReference() {
+			return this;
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
-				String sqlTableAlias,
 				LoadPlanBuildingContext loadPlanBuildingContext) {
 			// we have a key-many-to-one
 			//
 			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back throw our #addFetch
 			// 		impl.  We collect them there and later build the IdentifierDescription
 			final EntityFetch fetch = LoadPlanBuildingHelper.buildStandardEntityFetch(
 					this,
 					attributeDefinition,
 					fetchStrategy,
-					sqlTableAlias,
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
-		public EntityAliases getEntityAliases() {
-			return entityReference.getEntityAliases();
-		}
-
-		@Override
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
-					entityReference.getEntityAliases().getSuffixedKeyAliases(),
+					context.getLoadQueryAliasResolutionContext().resolveEntityColumnAliases( entityReference ).getSuffixedKeyAliases(),
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/LoadPlanBuildingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/LoadPlanBuildingContext.java
index 2fa50a9fc1..46833340d5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/LoadPlanBuildingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/LoadPlanBuildingContext.java
@@ -1,48 +1,33 @@
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
 package org.hibernate.loader.plan.spi.build;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.EntityAliases;
-import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
-import org.hibernate.persister.walking.spi.CollectionDefinition;
-import org.hibernate.persister.walking.spi.CompositionDefinition;
-import org.hibernate.persister.walking.spi.EntityDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public interface LoadPlanBuildingContext {
 	public SessionFactoryImplementor getSessionFactory();
-
-	public CollectionAliases resolveCollectionColumnAliases(AssociationAttributeDefinition attributeDefinition);
-	public EntityAliases resolveEntityColumnAliases(AssociationAttributeDefinition attributeDefinition);
-
-	public String resolveRootSourceAlias(EntityDefinition definition);
-	public String resolveRootSourceAlias(CollectionDefinition definition);
-
-	public String resolveFetchSourceAlias(AssociationAttributeDefinition attributeDefinition);
-	public String resolveFetchSourceAlias(CompositionDefinition compositionDefinition);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/JoinableAssociation.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/JoinableAssociation.java
new file mode 100644
index 0000000000..5e4355091a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/JoinableAssociation.java
@@ -0,0 +1,66 @@
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
+package org.hibernate.loader.spi;
+
+import java.util.Map;
+
+import org.hibernate.Filter;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.persister.entity.Joinable;
+import org.hibernate.sql.JoinType;
+import org.hibernate.type.AssociationType;
+
+/**
+ * @author Gail Badner
+ */
+public interface JoinableAssociation {
+	PropertyPath getPropertyPath();
+
+	JoinType getJoinType();
+
+	Fetch getCurrentFetch();
+
+	EntityReference getCurrentEntityReference();
+
+	CollectionReference getCurrentCollectionReference();
+
+	AssociationType getJoinableType();
+
+	Joinable getJoinable();
+
+	boolean isCollection();
+
+	public String[] getRhsColumns();
+
+	boolean hasRestriction();
+
+	boolean isManyToManyWith(JoinableAssociation other);
+
+	String getWithClause();
+
+	Map<String,Filter> getEnabledFilters();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryAliasResolutionContext.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryAliasResolutionContext.java
new file mode 100644
index 0000000000..c2555bbc54
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryAliasResolutionContext.java
@@ -0,0 +1,81 @@
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
+package org.hibernate.loader.spi;
+
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.ScalarReturn;
+
+/**
+ * @author Gail Badner
+ */
+public interface LoadQueryAliasResolutionContext {
+
+	public String resolveEntityReturnAlias(EntityReturn entityReturn);
+
+	public String resolveCollectionReturnAlias(CollectionReturn collectionReturn);
+
+	String[] resolveScalarReturnAliases(ScalarReturn scalarReturn);
+
+	/**
+	 * Retrieve the SQL table alias.
+	 *
+	 * @return The SQL table alias
+	 */
+	String resolveEntitySqlTableAlias(EntityReference entityReference);
+
+	EntityAliases resolveEntityColumnAliases(EntityReference entityReference);
+
+	String resolveCollectionSqlTableAlias(CollectionReference collectionReference);
+
+	/**
+	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to the
+	 * this collection.
+	 *
+	 * @return The ResultSet alias descriptor for the collection
+	 */
+	CollectionAliases resolveCollectionColumnAliases(CollectionReference collectionReference);
+
+	/**
+	 * If the elements of this collection are entities, this methods returns the JDBC ResultSet alias descriptions
+	 * for that entity; {@code null} indicates a non-entity collection.
+	 *
+	 * @return The ResultSet alias descriptor for the collection's entity element, or {@code null}
+	 */
+	EntityAliases resolveCollectionElementColumnAliases(CollectionReference collectionReference);
+
+	String resolveRhsAlias(JoinableAssociation joinableAssociation);
+
+	String resolveLhsAlias(JoinableAssociation joinableAssociation);
+
+	String[] resolveAliasedLhsColumnNames(JoinableAssociation joinableAssociation);
+
+	EntityAliases resolveCurrentEntityAliases(JoinableAssociation joinableAssociation);
+
+	CollectionAliases resolveCurrentCollectionAliases(JoinableAssociation joinableAssociation);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessingContext.java
index 2a6dce6f43..ccb12fcc1a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessingContext.java
@@ -1,101 +1,103 @@
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
 package org.hibernate.loader.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Set;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public interface ResultSetProcessingContext {
 	public SessionImplementor getSession();
 
 	public QueryParameters getQueryParameters();
 
 	public EntityKey getDictatedRootEntityKey();
 
 	public static interface IdentifierResolutionContext {
 		public EntityReference getEntityReference();
 
 		public void registerHydratedForm(Object hydratedForm);
 
 		public Object getHydratedForm();
 
 		public void registerEntityKey(EntityKey entityKey);
 
 		public EntityKey getEntityKey();
 	}
 
 	public IdentifierResolutionContext getIdentifierResolutionContext(EntityReference entityReference);
 
 	public Set<IdentifierResolutionContext> getIdentifierResolutionContexts();
 
+	public LoadQueryAliasResolutionContext getLoadQueryAliasResolutionContext();
+
 	public void registerHydratedEntity(EntityPersister persister, EntityKey entityKey, Object entityInstance);
 
 	public static interface EntityKeyResolutionContext {
 		public EntityPersister getEntityPersister();
 		public LockMode getLockMode();
-		public EntityAliases getEntityAliases();
+		public EntityReference getEntityReference();
 	}
 
 	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext);
 
 
 	// should be able to get rid of the methods below here from the interface ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void checkVersion(
 			ResultSet resultSet,
 			EntityPersister persister,
 			EntityAliases entityAliases,
 			EntityKey entityKey,
 			Object entityInstance) throws SQLException;
 
 	public String getConcreteEntityTypeName(
 			ResultSet resultSet,
 			EntityPersister persister,
 			EntityAliases entityAliases,
 			EntityKey entityKey) throws SQLException;
 
 	public void loadFromResultSet(
 			ResultSet resultSet,
 			Object entityInstance,
 			String concreteEntityTypeName,
 			EntityKey entityKey,
 			EntityAliases entityAliases,
 			LockMode acquiredLockMode,
 			EntityPersister persister,
 			boolean eagerFetch,
 			EntityType associationType) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessor.java
index c6ee085646..df733dc16c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessor.java
@@ -1,73 +1,74 @@
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
 package org.hibernate.loader.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.List;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
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
 
 	public ScrollableResultSetProcessor toOnDemandForm();
 
 	/**
 	 * Process an entire ResultSet, performing all extractions.
 	 *
 	 * Semi-copy of {@link org.hibernate.loader.Loader#doQuery}, with focus on just the ResultSet processing bit.
 	 *
 	 * @param loadPlanAdvisor A dynamic advisor on the load plan.
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
 			LoadPlanAdvisor loadPlanAdvisor,
 			ResultSet resultSet,
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
+			LoadQueryAliasResolutionContext aliasResolutionContext,
 			boolean returnProxies,
 			boolean readOnly,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActions) throws SQLException;
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
index 9f7b2fbfaf..d223f9bff0 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
@@ -1,315 +1,330 @@
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
+import java.util.Collections;
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
+import org.hibernate.loader.internal.LoadQueryAliasResolutionContextImpl;
 import org.hibernate.loader.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
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
 			final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 					sessionFactory(),
-					LoadQueryInfluencers.NONE,
-					"abc",
-					0
+					LoadQueryInfluencers.NONE
 			);
 			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+			final LoadQueryAliasResolutionContext aliasResolutionContext =
+					new LoadQueryAliasResolutionContextImpl(
+							sessionFactory(),
+							0,
+							Collections.singletonMap( plan.getReturns().get( 0 ), new String[] { "abc" } )
+					);
 			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan
+					plan,
+					aliasResolutionContext
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
+											aliasResolutionContext,
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
-					LoadQueryInfluencers.NONE,
-					"abc",
-					0
+					LoadQueryInfluencers.NONE
 			);
 			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+			final LoadQueryAliasResolutionContext aliasResolutionContext =
+					new LoadQueryAliasResolutionContextImpl(
+							sessionFactory(),
+							0,
+							Collections.singletonMap( plan.getReturns().get( 0 ), new String[] { "abc" } )
+					);
 			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan
+					plan,
+					aliasResolutionContext
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
+											aliasResolutionContext,
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
index 2edca22f3e..182f145257 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithCollectionResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithCollectionResultSetProcessorTest.java
@@ -1,171 +1,186 @@
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
+import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import javax.persistence.ElementCollection;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.internal.EntityLoadQueryBuilderImpl;
+import org.hibernate.loader.internal.LoadQueryAliasResolutionContextImpl;
 import org.hibernate.loader.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class EntityWithCollectionResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
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
 
+		session = openSession();
+		session.beginTransaction();
+		session.get( Person.class, person.id );
+		session.getTransaction().commit();
+		session.close();
+
 		{
 			final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 					sessionFactory(),
-					LoadQueryInfluencers.NONE,
-					"abc",
-					0
+					LoadQueryInfluencers.NONE
 			);
 			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+			final LoadQueryAliasResolutionContext aliasResolutionContext =
+					new LoadQueryAliasResolutionContextImpl(
+							sessionFactory(),
+							0,
+							Collections.singletonMap( plan.getReturns().get( 0 ), new String[] { "abc" } )
+					);
 			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan
+					plan,
+					aliasResolutionContext
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
+											aliasResolutionContext,
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
 		private Set<String> nickNames = new HashSet<String>();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
index 80e400f193..c9c17a699a 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
@@ -1,161 +1,170 @@
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
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
+import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.Session;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.internal.EntityLoadQueryBuilderImpl;
+import org.hibernate.loader.internal.LoadQueryAliasResolutionContextImpl;
 import org.hibernate.loader.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
+import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.NamedParameterContext;
 import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
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
 			final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 					sessionFactory(),
-					LoadQueryInfluencers.NONE,
-					"abc",
-					0
+					LoadQueryInfluencers.NONE
 			);
 			final LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+			final LoadQueryAliasResolutionContext aliasResolutionContext =
+					new LoadQueryAliasResolutionContextImpl(
+							sessionFactory(),
+							0,
+							Collections.singletonMap( plan.getReturns().get( 0 ), new String[] { "abc" } )
+					);
 			final EntityLoadQueryBuilderImpl queryBuilder = new EntityLoadQueryBuilderImpl(
 					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan
+					plan,
+					aliasResolutionContext
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
+											aliasResolutionContext,
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
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
index 01ce77bf65..a5f6f84b2e 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
@@ -1,155 +1,141 @@
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
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.util.List;
 
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
-import org.hibernate.loader.internal.EntityLoadQueryBuilderImpl;
 import org.hibernate.loader.plan.internal.CascadeLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
-import org.hibernate.loader.spi.LoadQueryBuilder;
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
 		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				sessionFactory(),
-				LoadQueryInfluencers.NONE,
-				"abc",
-				0
+				LoadQueryInfluencers.NONE
 		);
 		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
-		assertEquals( "abc", entityReturn.getAlias() );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
-
-		LoadQueryBuilder loadQueryBuilder = new EntityLoadQueryBuilderImpl( sessionFactory(), LoadQueryInfluencers.NONE, plan );
-		String sql = loadQueryBuilder.generateSql( 1 );
 	}
 
 	@Test
 	public void testCascadeBasedBuild() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		CascadeLoadPlanBuilderStrategy strategy = new CascadeLoadPlanBuilderStrategy(
 				CascadingActions.MERGE,
 				sessionFactory(),
-				LoadQueryInfluencers.NONE,
-				"abc",
-				0
+				LoadQueryInfluencers.NONE
 		);
 		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
-		assertEquals( "abc", entityReturn.getAlias() );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 	}
 
 	@Test
 	public void testCollectionInitializerCase() {
 		CollectionPersister cp = sessionFactory().getCollectionPersister( Poster.class.getName() + ".messages" );
 		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				sessionFactory(),
-				LoadQueryInfluencers.NONE,
-				"abc",
-				0
+				LoadQueryInfluencers.NONE
 		);
 		LoadPlan plan = LoadPlanBuilder.buildRootCollectionLoadPlan( strategy, cp );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		CollectionReturn collectionReturn = ExtraAssertions.assertTyping( CollectionReturn.class, rtn );
-		assertEquals( "abc", collectionReturn.getAlias() );
 
 		assertNotNull( collectionReturn.getElementGraph().getFetches() );
 		assertEquals( 1, collectionReturn.getElementGraph().getFetches().length ); // the collection elements are fetched
 		Fetch fetch = collectionReturn.getElementGraph().getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
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
\ No newline at end of file
