diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
index f328599990..e9745d860f 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractEntityLoadQueryImpl.java
@@ -1,123 +1,128 @@
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
 package org.hibernate.loader.internal;
 import java.util.List;
 
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.Select;
 
 /**
- * Abstract walker for walkers which begin at an entity (criteria
- * queries and entity loaders).
+ * Represents an entity load query for criteria
+ * queries and entity loaders, used for generating SQL.
+ *
+ * This code is based on the SQL generation code originally in
+ * org.hibernate.loader.AbstractEntityJoinWalker.
  *
  * @author Gavin King
+ * @author Gail Badner
  */
 public abstract class AbstractEntityLoadQueryImpl extends AbstractLoadQueryImpl {
 
 	private final EntityReturn entityReturn;
 
-	public AbstractEntityLoadQueryImpl(
-			SessionFactoryImplementor factory,
-			EntityReturn entityReturn,
-			List<JoinableAssociationImpl> associations) {
-		super( factory, associations );
+	public AbstractEntityLoadQueryImpl(EntityReturn entityReturn, List<JoinableAssociation> associations) {
+		super( associations );
 		this.entityReturn = entityReturn;
 	}
 
 	protected final String generateSql(
 			final String whereString,
 			final String orderByString,
 			final LockOptions lockOptions,
+			final SessionFactoryImplementor factory,
 			final LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
-		return generateSql( null, whereString, orderByString, "", lockOptions, aliasResolutionContext );
+		return generateSql( null, whereString, orderByString, "", lockOptions, factory, aliasResolutionContext );
 	}
 
 	private String generateSql(
 			final String projection,
 			final String condition,
 			final String orderBy,
 			final String groupBy,
 			final LockOptions lockOptions,
+			final SessionFactoryImplementor factory,
 			final LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
 
-		JoinFragment ojf = mergeOuterJoins( aliasResolutionContext );
+		JoinFragment ojf = mergeOuterJoins( factory, aliasResolutionContext );
 
 		// If no projection, then the last suffix should be for the entity return.
 		// TODO: simplify how suffixes are generated/processed.
 
-
-		Select select = new Select( getDialect() )
+		final String entityReturnAlias = resolveEntityReturnAlias( aliasResolutionContext );
+		Select select = new Select( factory.getDialect() )
 				.setLockOptions( lockOptions )
 				.setSelectClause(
 						projection == null ?
 								getPersister().selectFragment(
-										getAlias( aliasResolutionContext ),
+										entityReturnAlias,
 										aliasResolutionContext.resolveEntityColumnAliases( entityReturn ).getSuffix()
 								) + associationSelectString( aliasResolutionContext ) :
 								projection
 				)
 				.setFromClause(
-						getDialect().appendLockHint( lockOptions, getPersister().fromTableFragment( getAlias( aliasResolutionContext ) ) ) +
-								getPersister().fromJoinFragment( getAlias( aliasResolutionContext), true, true )
+						factory.getDialect().appendLockHint(
+								lockOptions,
+								getPersister().fromTableFragment( entityReturnAlias )
+						) + getPersister().fromJoinFragment( entityReturnAlias, true, true )
 				)
 				.setWhereClause( condition )
 				.setOuterJoins(
 						ojf.toFromFragmentString(),
 						ojf.toWhereFragmentString() + getWhereFragment( aliasResolutionContext )
 				)
 				.setOrderByClause( orderBy( orderBy, aliasResolutionContext ) )
 				.setGroupByClause( groupBy );
 
-		if ( getFactory().getSettings().isCommentsEnabled() ) {
+		if ( factory.getSettings().isCommentsEnabled() ) {
 			select.setComment( getComment() );
 		}
 		return select.toStatementString();
 	}
 
 	protected String getWhereFragment(LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
 		// here we do not bother with the discriminator.
-		return getPersister().whereJoinFragment( getAlias( aliasResolutionContext ), true, true );
+		return getPersister().whereJoinFragment( resolveEntityReturnAlias( aliasResolutionContext ), true, true );
 	}
 
-	public abstract String getComment();
+	protected abstract String getComment();
 
-	public final OuterJoinLoadable getPersister() {
+	protected final OuterJoinLoadable getPersister() {
 		return (OuterJoinLoadable) entityReturn.getEntityPersister();
 	}
 
-	public final String getAlias(LoadQueryAliasResolutionContext aliasResolutionContext) {
-		return aliasResolutionContext.resolveEntitySqlTableAlias( entityReturn );
+	protected final String resolveEntityReturnAlias(LoadQueryAliasResolutionContext aliasResolutionContext) {
+		return aliasResolutionContext.resolveEntityTableAlias( entityReturn );
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getPersister().getEntityName() + ')';
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractJoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractJoinableAssociationImpl.java
new file mode 100644
index 0000000000..74c9c31ad0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractJoinableAssociationImpl.java
@@ -0,0 +1,118 @@
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
+import java.util.Map;
+
+import org.hibernate.Filter;
+import org.hibernate.MappingException;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.spi.JoinableAssociation;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.sql.JoinType;
+
+/**
+ * This class represents a joinable association.
+ *
+ * @author Gavin King
+ * @author Gail Badner
+ */
+public abstract class AbstractJoinableAssociationImpl implements JoinableAssociation {
+	private final PropertyPath propertyPath;
+	private final Fetch currentFetch;
+	private final EntityReference currentEntityReference;
+	private final CollectionReference currentCollectionReference;
+	private final JoinType joinType;
+	private final String withClause;
+	private final Map<String, Filter> enabledFilters;
+	private final boolean hasRestriction;
+
+	public AbstractJoinableAssociationImpl(
+			Fetch currentFetch,
+			EntityReference currentEntityReference,
+			CollectionReference currentCollectionReference,
+			String withClause,
+			boolean hasRestriction,
+			Map<String, Filter> enabledFilters) throws MappingException {
+		this.propertyPath = currentFetch.getPropertyPath();
+		final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) currentFetch.getOwner().retrieveFetchSourcePersister();
+		final int propertyNumber = ownerPersister.getEntityMetamodel().getPropertyIndex( currentFetch.getOwnerPropertyName() );
+		final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
+		if ( currentFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
+			joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
+		}
+		else {
+			joinType = JoinType.NONE;
+		}
+		this.currentFetch = currentFetch;
+		this.currentEntityReference = currentEntityReference;
+		this.currentCollectionReference = currentCollectionReference;
+		this.withClause = withClause;
+		this.hasRestriction = hasRestriction;
+		this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public JoinType getJoinType() {
+		return joinType;
+	}
+
+	@Override
+	public Fetch getCurrentFetch() {
+		return currentFetch;
+	}
+
+	@Override
+	public EntityReference getCurrentEntityReference() {
+		return currentEntityReference;
+	}
+
+	@Override
+	public CollectionReference getCurrentCollectionReference() {
+		return currentCollectionReference;
+	}
+
+	@Override
+	public boolean hasRestriction() {
+		return hasRestriction;
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
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
index 83ddbd352a..37e6a8d3d7 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
@@ -1,292 +1,313 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
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
-import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.sql.ConditionFragment;
 import org.hibernate.sql.DisjunctionFragment;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 
 /**
- * Walks the metamodel, searching for joins, and collecting
- * together information needed by <tt>OuterJoinLoader</tt>.
- * 
- * @see org.hibernate.loader.OuterJoinLoader
- * @author Gavin King, Jon Lipsky
+ * Represents a generic load query used for generating SQL.
+ *
+ * This code is based on the SQL generation code originally in
+ * org.hibernate.loader.JoinWalker.
+ *
+ * @author Gavin King
+ * @author Jon Lipsky
+ * @author Gail Badner
  */
 public abstract class AbstractLoadQueryImpl {
 
-	private final SessionFactoryImplementor factory;
-	private final List<JoinableAssociationImpl> associations;
+	private final List<JoinableAssociation> associations;
 
-	protected AbstractLoadQueryImpl(
-			SessionFactoryImplementor factory,
-			List<JoinableAssociationImpl> associations) {
-		this.factory = factory;
+	protected AbstractLoadQueryImpl(List<JoinableAssociation> associations) {
 		this.associations = associations;
 	}
 
-	protected SessionFactoryImplementor getFactory() {
-		return factory;
-	}
-
-	protected Dialect getDialect() {
-		return factory.getDialect();
-	}
-
 	protected String orderBy(final String orderBy, LoadQueryAliasResolutionContext aliasResolutionContext) {
 		return mergeOrderings( orderBy( associations, aliasResolutionContext ), orderBy );
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
-	protected final JoinFragment mergeOuterJoins(LoadQueryAliasResolutionContext aliasResolutionContext)
+	protected final JoinFragment mergeOuterJoins(SessionFactoryImplementor factory, LoadQueryAliasResolutionContext aliasResolutionContext)
 	throws MappingException {
-		JoinFragment joinFragment = getDialect().createOuterJoinFragment();
+		JoinFragment joinFragment = factory.getDialect().createOuterJoinFragment();
 		JoinableAssociation previous = null;
 		for ( JoinableAssociation association : associations ) {
+			final String rhsAlias = aliasResolutionContext.resolveAssociationRhsTableAlias( association );
+			final String[] aliasedLhsColumnNames = aliasResolutionContext.resolveAssociationAliasedLhsColumnNames(
+					association
+			);
+			final String[] rhsColumnNames = JoinHelper.getRHSColumnNames( association.getAssociationType(), factory );
+			final String on = resolveOnCondition( factory, association, aliasResolutionContext );
 			if ( previous != null && previous.isManyToManyWith( association ) ) {
-				addManyToManyJoin( joinFragment, association, ( QueryableCollection ) previous.getJoinable(), aliasResolutionContext );
+				addManyToManyJoin(
+						joinFragment,
+						association,
+						( QueryableCollection ) previous.getJoinable(),
+						rhsAlias,
+						aliasedLhsColumnNames,
+						rhsColumnNames,
+						on
+				);
 			}
 			else {
-				addJoins( joinFragment, association, aliasResolutionContext);
+				addJoins(
+						joinFragment,
+						association,
+						rhsAlias,
+						aliasedLhsColumnNames,
+						rhsColumnNames,
+						on
+				);
 			}
 			previous = association;
 		}
 		return joinFragment;
 	}
 
 	/**
 	 * Get the order by string required for collection fetching
 	 */
+	// TODO: why is this static?
 	protected static String orderBy(
-			List<JoinableAssociationImpl> associations,
+			List<JoinableAssociation> associations,
 			LoadQueryAliasResolutionContext aliasResolutionContext)
 	throws MappingException {
 		StringBuilder buf = new StringBuilder();
 		JoinableAssociation previous = null;
 		for ( JoinableAssociation association : associations ) {
-			final String rhsAlias = aliasResolutionContext.resolveRhsAlias( association );
+			final String rhsAlias = aliasResolutionContext.resolveAssociationRhsTableAlias( association );
 			if ( association.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
 				if ( association.getJoinable().isCollection() ) {
 					final QueryableCollection queryableCollection = (QueryableCollection) association.getJoinable();
 					if ( queryableCollection.hasOrdering() ) {
 						final String orderByString = queryableCollection.getSQLOrderByString( rhsAlias );
 						buf.append( orderByString ).append(", ");
 					}
 				}
 				else {
 					// it might still need to apply a collection ordering based on a
 					// many-to-many defined order-by...
 					if ( previous != null && previous.getJoinable().isCollection() ) {
 						final QueryableCollection queryableCollection = (QueryableCollection) previous.getJoinable();
 						if ( queryableCollection.isManyToMany() && previous.isManyToManyWith( association ) ) {
 							if ( queryableCollection.hasManyToManyOrdering() ) {
 								final String orderByString = queryableCollection.getManyToManyOrderByString( rhsAlias );
 								buf.append( orderByString ).append(", ");
 							}
 						}
 					}
 				}
 			}
 			previous = association;
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
 	protected final String associationSelectString(LoadQueryAliasResolutionContext aliasResolutionContext)
 	throws MappingException {
 
 		if ( associations.size() == 0 ) {
 			return "";
 		}
 		else {
 			StringBuilder buf = new StringBuilder( associations.size() * 100 );
 			for ( int i=0; i<associations.size(); i++ ) {
 				JoinableAssociation association = associations.get( i );
 				JoinableAssociation next = ( i == associations.size() - 1 )
 				        ? null
 				        : associations.get( i + 1 );
 				final Joinable joinable = association.getJoinable();
-				final EntityAliases currentEntityAliases = aliasResolutionContext.resolveCurrentEntityAliases( association );
-				final CollectionAliases currentCollectionAliases = aliasResolutionContext.resolveCurrentCollectionAliases( association );
+				final EntityAliases currentEntityAliases =
+						association.getCurrentEntityReference() == null ?
+								null :
+								aliasResolutionContext.resolveEntityColumnAliases( association.getCurrentEntityReference() );
+				final CollectionAliases currentCollectionAliases =
+						association.getCurrentCollectionReference() == null ?
+								null :
+								aliasResolutionContext.resolveCollectionColumnAliases( association.getCurrentCollectionReference() );
 				final String selectFragment = joinable.selectFragment(
 						next == null ? null : next.getJoinable(),
-						next == null ? null : aliasResolutionContext.resolveRhsAlias( next ),
-						aliasResolutionContext.resolveRhsAlias( association ),
+						next == null ? null : aliasResolutionContext.resolveAssociationRhsTableAlias( next ),
+						aliasResolutionContext.resolveAssociationRhsTableAlias( association ),
 						currentEntityAliases == null ? null : currentEntityAliases.getSuffix(),
 						currentCollectionAliases == null ? null : currentCollectionAliases.getSuffix(),
 						association.getJoinType()==JoinType.LEFT_OUTER_JOIN
 				);
 				if (selectFragment.trim().length() > 0) {
 					// TODO: shouldn't the append of selectFragment be outside this if statement???
 					buf.append(", ").append( selectFragment );
 				}
 			}
 			return buf.toString();
 		}
 	}
 
 	private void addJoins(
 			JoinFragment joinFragment,
 			JoinableAssociation association,
-			LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
-		final String rhsAlias = aliasResolutionContext.resolveRhsAlias( association );
+			String rhsAlias,
+			String[] aliasedLhsColumnNames,
+			String[] rhsColumnNames,
+			String on) throws MappingException {
 		joinFragment.addJoin(
 				association.getJoinable().getTableName(),
 				rhsAlias,
-				aliasResolutionContext.resolveAliasedLhsColumnNames( association ),
-				association.getRhsColumns(),
+				aliasedLhsColumnNames,
+				rhsColumnNames,
 				association.getJoinType(),
-				resolveOnCondition( association, aliasResolutionContext )
+				on
 		);
 		joinFragment.addJoins(
 				association.getJoinable().fromJoinFragment( rhsAlias, false, true ),
 				association.getJoinable().whereJoinFragment( rhsAlias, false, true )
 		);
 	}
 
-	private String resolveOnCondition(JoinableAssociation joinableAssociation,
-									  LoadQueryAliasResolutionContext aliasResolutionContext) {
+	private String resolveOnCondition(
+			SessionFactoryImplementor factory,
+			JoinableAssociation joinableAssociation,
+			LoadQueryAliasResolutionContext aliasResolutionContext) {
 		final String withClause = StringHelper.isEmpty( joinableAssociation.getWithClause() ) ?
 				"" :
 				" and ( " + joinableAssociation.getWithClause() + " )";
-		return joinableAssociation.getJoinableType().getOnCondition(
-				aliasResolutionContext.resolveRhsAlias( joinableAssociation ),
+		return joinableAssociation.getAssociationType().getOnCondition(
+				aliasResolutionContext.resolveAssociationRhsTableAlias( joinableAssociation ),
 				factory,
 				joinableAssociation.getEnabledFilters()
 		) + withClause;
 	}
 
-
-
 	/*
 	public void validateJoin(String path) throws MappingException {
 		if ( rhsColumns==null || lhsColumns==null
 				|| lhsColumns.length!=rhsColumns.length || lhsColumns.length==0 ) {
 			throw new MappingException("invalid join columns for association: " + path);
 		}
 	}
 	*/
 
 	private void addManyToManyJoin(
 			JoinFragment outerjoin,
 			JoinableAssociation association,
 			QueryableCollection collection,
-			LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
-		final String rhsAlias = aliasResolutionContext.resolveRhsAlias( association );
-		final String[] aliasedLhsColumnNames = aliasResolutionContext.resolveAliasedLhsColumnNames( association );
+			String rhsAlias,
+			String[] aliasedLhsColumnNames,
+			String[] rhsColumnNames,
+			String on) throws MappingException {
 		final String manyToManyFilter = collection.getManyToManyFilterFragment(
 				rhsAlias,
 				association.getEnabledFilters()
 		);
-		final String on = resolveOnCondition( association, aliasResolutionContext );
 		String condition = "".equals( manyToManyFilter )
 				? on
 				: "".equals( on )
 				? manyToManyFilter
 				: on + " and " + manyToManyFilter;
 		outerjoin.addJoin(
 				association.getJoinable().getTableName(),
 				rhsAlias,
 				aliasedLhsColumnNames,
-				association.getRhsColumns(),
+				rhsColumnNames,
 				association.getJoinType(),
 				condition
 		);
 		outerjoin.addJoins(
 				association.getJoinable().fromJoinFragment( rhsAlias, false, true ),
 				association.getJoinable().whereJoinFragment( rhsAlias, false, true )
 		);
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
index 688d1bc9bd..aef90267f1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryBuilderImpl.java
@@ -1,215 +1,227 @@
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
+import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 import org.hibernate.loader.spi.LoadQueryBuilder;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.walking.spi.WalkingException;
 
 /**
  * @author Gail Badner
  */
 public class EntityLoadQueryBuilderImpl implements LoadQueryBuilder {
-	private final SessionFactoryImplementor sessionFactory;
 	private final LoadQueryInfluencers loadQueryInfluencers;
 	private final LoadPlan loadPlan;
-	private final LoadQueryAliasResolutionContext aliasResolutionContext;
-	private final List<JoinableAssociationImpl> associations;
+	private final List<JoinableAssociation> associations;
 
 	public EntityLoadQueryBuilderImpl(
-			SessionFactoryImplementor sessionFactory,
 			LoadQueryInfluencers loadQueryInfluencers,
-			LoadPlan loadPlan,
-			LoadQueryAliasResolutionContext aliasResolutionContext) {
-		this.sessionFactory = sessionFactory;
+			LoadPlan loadPlan) {
 		this.loadQueryInfluencers = loadQueryInfluencers;
 		this.loadPlan = loadPlan;
 
-		// TODO: remove reliance on aliasResolutionContext; it should only be needed when generating the SQL.
-		this.aliasResolutionContext = aliasResolutionContext;
+	    // TODO: the whole point of the following is to build associations.
+		// this could be done while building loadPlan (and be a part of the LoadPlan).
+		// Should it be?
 		LocalVisitationStrategy strategy = new LocalVisitationStrategy();
 		LoadPlanVisitor.visit( loadPlan, strategy );
 		this.associations = strategy.associations;
 	}
 
 	@Override
-	public String generateSql(int batchSize) {
-		return generateSql( batchSize, getOuterJoinLoadable().getKeyColumnNames() );
+	public String generateSql(
+			int batchSize,
+			SessionFactoryImplementor sessionFactory,
+			LoadQueryAliasResolutionContext aliasResolutionContext) {
+		return generateSql(
+				batchSize,
+				getOuterJoinLoadable().getKeyColumnNames(),
+				sessionFactory,
+				aliasResolutionContext
+		);
 	}
 
-	public String generateSql(int batchSize, String[] uniqueKey) {
+	public String generateSql(
+			int batchSize,
+			String[] uniqueKey,
+			SessionFactoryImplementor sessionFactory,
+			LoadQueryAliasResolutionContext aliasResolutionContext) {
 		final EntityLoadQueryImpl loadQuery = new EntityLoadQueryImpl(
-				sessionFactory,
 				getRootEntityReturn(),
 				associations
 		);
-		return loadQuery.generateSql( uniqueKey, batchSize, getRootEntityReturn().getLockMode(), aliasResolutionContext );
+		return loadQuery.generateSql(
+				uniqueKey,
+				batchSize,
+				getRootEntityReturn().getLockMode(),
+				sessionFactory,
+				aliasResolutionContext );
 	}
 
 	private EntityReturn getRootEntityReturn() {
 		return (EntityReturn) loadPlan.getReturns().get( 0 );
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable() {
 		return (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 	}
+
 	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
-		private final List<JoinableAssociationImpl> associations = new ArrayList<JoinableAssociationImpl>();
+		private final List<JoinableAssociation> associations = new ArrayList<JoinableAssociation>();
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
-			JoinableAssociationImpl assoc = new JoinableAssociationImpl(
+			EntityJoinableAssociationImpl assoc = new EntityJoinableAssociationImpl(
 					entityFetch,
 					getCurrentCollectionReference(),
 					"",    // getWithClause( entityFetch.getPropertyPath() )
 					false, // hasRestriction( entityFetch.getPropertyPath() )
-					sessionFactory,
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
-			JoinableAssociationImpl assoc = new JoinableAssociationImpl(
+			CollectionJoinableAssociationImpl assoc = new CollectionJoinableAssociationImpl(
 					collectionFetch,
 					getCurrentEntityReference(),
 					"",    // getWithClause( entityFetch.getPropertyPath() )
 					false, // hasRestriction( entityFetch.getPropertyPath() )
-					sessionFactory,
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
index 904ce1c479..148c6126c6 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/EntityLoadQueryImpl.java
@@ -1,65 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
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
+import org.hibernate.loader.spi.JoinableAssociation;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
 
 /**
- * A walker for loaders that fetch entities
+ * Represents an load query for fetching an entity, used for generating SQL.
+ *
+ * This code is based on the SQL generation code originally in
+ * org.hibernate.loader.EntityJoinWalker.
  *
- * @see org.hibernate.loader.entity.EntityLoader
  * @author Gavin King
+ * @author Gail Badner
  */
 public class EntityLoadQueryImpl extends AbstractEntityLoadQueryImpl {
 
 	public EntityLoadQueryImpl(
-			final SessionFactoryImplementor factory,
 			EntityReturn entityReturn,
-			List<JoinableAssociationImpl> associations) throws MappingException {
-		super( factory, entityReturn, associations );
+			List<JoinableAssociation> associations) throws MappingException {
+		super( entityReturn, associations );
 	}
 
 	public String generateSql(
 			String[] uniqueKey,
 			int batchSize,
 			LockMode lockMode,
+			SessionFactoryImplementor factory,
 			LoadQueryAliasResolutionContext aliasResolutionContext) {
-		StringBuilder whereCondition = whereString( getAlias( aliasResolutionContext ), uniqueKey, batchSize )
+		StringBuilder whereCondition = whereString( resolveEntityReturnAlias( aliasResolutionContext ), uniqueKey, batchSize )
 				//include the discriminator and class-level where, but not filters
-				.append( getPersister().filterFragment( getAlias( aliasResolutionContext ), Collections.EMPTY_MAP ) );
-		return generateSql( whereCondition.toString(), "",  new LockOptions().setLockMode( lockMode ), aliasResolutionContext );
+				.append( getPersister().filterFragment( resolveEntityReturnAlias( aliasResolutionContext ), Collections.EMPTY_MAP ) );
+		return generateSql( whereCondition.toString(), "",  new LockOptions().setLockMode( lockMode ), factory, aliasResolutionContext );
 	}
 
-	public String getComment() {
+	protected String getComment() {
 		return "load " + getPersister().getEntityName();
 	}
-
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
deleted file mode 100644
index b9b07bed56..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/JoinableAssociationImpl.java
+++ /dev/null
@@ -1,303 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
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
- */
-package org.hibernate.loader.internal;
-import java.util.Map;
-
-import org.hibernate.Filter;
-import org.hibernate.MappingException;
-import org.hibernate.engine.FetchStyle;
-import org.hibernate.engine.internal.JoinHelper;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan.spi.CollectionFetch;
-import org.hibernate.loader.plan.spi.CollectionReference;
-import org.hibernate.loader.plan.spi.EntityFetch;
-import org.hibernate.loader.plan.spi.EntityReference;
-import org.hibernate.loader.plan.spi.Fetch;
-import org.hibernate.loader.spi.JoinableAssociation;
-import org.hibernate.persister.collection.QueryableCollection;
-import org.hibernate.persister.entity.Joinable;
-import org.hibernate.persister.entity.OuterJoinLoadable;
-import org.hibernate.sql.JoinType;
-import org.hibernate.type.AssociationType;
-import org.hibernate.type.EntityType;
-
-/**
- * Part of the Hibernate SQL rendering internals.  This class represents
- * a joinable association.
- *
- * @author Gavin King
- * @author Gail Badner
- */
-public class JoinableAssociationImpl implements JoinableAssociation {
-	private final PropertyPath propertyPath;
-	private final Joinable joinable;
-	private final AssociationType joinableType;
-	private final String[] rhsColumns;
-	private final Fetch currentFetch;
-	private final EntityReference currentEntityReference;
-	private final CollectionReference currentCollectionReference;
-	private final JoinType joinType;
-	private final String withClause;
-	private final Map<String, Filter> enabledFilters;
-	private final boolean hasRestriction;
-
-	public JoinableAssociationImpl(
-			EntityFetch entityFetch,
-			CollectionReference currentCollectionReference,
-			String withClause,
-			boolean hasRestriction,
-			SessionFactoryImplementor factory,
-			Map<String, Filter> enabledFilters) throws MappingException {
-		this(
-				entityFetch,
-				entityFetch.getAssociationType(),
-				entityFetch,
-				currentCollectionReference,
-				withClause,
-				hasRestriction,
-				factory,
-				enabledFilters
-		);
-	}
-
-	public JoinableAssociationImpl(
-			CollectionFetch collectionFetch,
-			EntityReference currentEntityReference,
-			String withClause,
-			boolean hasRestriction,
-			SessionFactoryImplementor factory,
-			Map<String, Filter> enabledFilters) throws MappingException {
-		this(
-				collectionFetch,
-				collectionFetch.getCollectionPersister().getCollectionType(),
-				currentEntityReference,
-				collectionFetch,
-				withClause,
-				hasRestriction,
-				factory,
-				enabledFilters
-		);
-	}
-
-	private JoinableAssociationImpl(
-			Fetch currentFetch,
-			AssociationType associationType,
-			EntityReference currentEntityReference,
-			CollectionReference currentCollectionReference,
-			String withClause,
-			boolean hasRestriction,
-			SessionFactoryImplementor factory,
-			Map<String, Filter> enabledFilters) throws MappingException {
-		this.propertyPath = currentFetch.getPropertyPath();
-		this.joinableType = associationType;
-		final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) currentFetch.getOwner().retrieveFetchSourcePersister();
-		final int propertyNumber = ownerPersister.getEntityMetamodel().getPropertyIndex( currentFetch.getOwnerPropertyName() );
-		final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
-		if ( currentFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
-			joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
-		}
-		else {
-			joinType = JoinType.NONE;
-		}
-		this.joinable = joinableType.getAssociatedJoinable(factory);
-		this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
-		this.currentFetch = currentFetch;
-		this.currentEntityReference = currentEntityReference;
-		this.currentCollectionReference = currentCollectionReference;
-		this.withClause = withClause;
-		this.hasRestriction = hasRestriction;
-		this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
-	}
-
-
-	/*
-					public JoinableAssociationImpl(
-						EntityFetch entityFetch,
-						String currentCollectionSuffix,
-						String withClause,
-						boolean hasRestriction,
-						SessionFactoryImplementor factory,
-						Map enabledFilters,
-						LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
-					this.propertyPath = entityFetch.getPropertyPath();
-					this.joinableType = entityFetch.getAssociationType();
-					// TODO: this is not correct
-					final EntityPersister fetchSourcePersister = entityFetch.getOwner().retrieveFetchSourcePersister();
-					final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( entityFetch.getOwnerPropertyName() );
-
-					if ( EntityReference.class.isInstance( entityFetch.getOwner() ) ) {
-						this.lhsAlias = aliasResolutionContext.resolveEntitySqlTableAlias( (EntityReference) entityFetch.getOwner() );
-					}
-					else {
-						throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
-					}
-					final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) entityFetch.getOwner().retrieveFetchSourcePersister();
-					this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
-							entityFetch.getAssociationType(), lhsAlias, propertyNumber, ownerPersister, factory
-					);
-					this.rhsAlias = aliasResolutionContext.resolveEntitySqlTableAlias( entityFetch );
-
-					final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
-					if ( entityFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
-						joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
-					}
-					else {
-						joinType = JoinType.NONE;
-					}
-					this.joinable = joinableType.getAssociatedJoinable(factory);
-					this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
-					this.currentEntitySuffix = 	aliasResolutionContext.resolveEntityColumnAliases( entityFetch ).getSuffix();
-					this.currentCollectionSuffix = currentCollectionSuffix;
-					this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
-							+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
-					this.hasRestriction = hasRestriction;
-					this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
-				}
-
-				public JoinableAssociationImpl(
-						CollectionFetch collectionFetch,
-						String currentEntitySuffix,
-						String withClause,
-						boolean hasRestriction,
-						SessionFactoryImplementor factory,
-						Map enabledFilters,
-						LoadQueryAliasResolutionContext aliasResolutionContext) throws MappingException {
-					this.propertyPath = collectionFetch.getPropertyPath();
-					final CollectionType collectionType =  collectionFetch.getCollectionPersister().getCollectionType();
-					this.joinableType = collectionType;
-					// TODO: this is not correct
-					final EntityPersister fetchSourcePersister = collectionFetch.getOwner().retrieveFetchSourcePersister();
-					final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( collectionFetch.getOwnerPropertyName() );
-
-					if ( EntityReference.class.isInstance( collectionFetch.getOwner() ) ) {
-						this.lhsAlias = aliasResolutionContext.resolveEntitySqlTableAlias( (EntityReference) collectionFetch.getOwner() );
-					}
-					else {
-						throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference." );
-					}
-					final OuterJoinLoadable ownerPersister = (OuterJoinLoadable) collectionFetch.getOwner().retrieveFetchSourcePersister();
-					this.lhsColumns = JoinHelper.getAliasedLHSColumnNames(
-							collectionType, lhsAlias, propertyNumber, ownerPersister, factory
-					);
-					this.rhsAlias = aliasResolutionContext.resolveCollectionSqlTableAlias( collectionFetch );
-
-					final boolean isNullable = ownerPersister.isSubclassPropertyNullable( propertyNumber );
-					if ( collectionFetch.getFetchStrategy().getStyle() == FetchStyle.JOIN ) {
-						joinType = isNullable ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN;
-					}
-					else {
-						joinType = JoinType.NONE;
-					}
-					this.joinable = joinableType.getAssociatedJoinable(factory);
-					this.rhsColumns = JoinHelper.getRHSColumnNames( joinableType, factory );
-					this.currentEntitySuffix = currentEntitySuffix;
-					this.currentCollectionSuffix = aliasResolutionContext.resolveCollectionColumnAliases( collectionFetch ).getSuffix();
-					this.on = joinableType.getOnCondition( rhsAlias, factory, enabledFilters )
-							+ ( withClause == null || withClause.trim().length() == 0 ? "" : " and ( " + withClause + " )" );
-					this.hasRestriction = hasRestriction;
-					this.enabledFilters = enabledFilters; // needed later for many-to-many/filter application
-				}
-				 */
-
-	@Override
-	public PropertyPath getPropertyPath() {
-		return propertyPath;
-	}
-
-	@Override
-	public JoinType getJoinType() {
-		return joinType;
-	}
-
-	@Override
-	public Fetch getCurrentFetch() {
-		return currentFetch;
-	}
-
-	@Override
-	public EntityReference getCurrentEntityReference() {
-		return currentEntityReference;
-	}
-
-	@Override
-	public CollectionReference getCurrentCollectionReference() {
-		return currentCollectionReference;
-	}
-
-	private boolean isOneToOne() {
-		if ( joinableType.isEntityType() )  {
-			EntityType etype = (EntityType) joinableType;
-			return etype.isOneToOne() /*&& etype.isReferenceToPrimaryKey()*/;
-		}
-		else {
-			return false;
-		}
-	}
-
-	@Override
-	public AssociationType getJoinableType() {
-		return joinableType;
-	}
-
-	@Override
-	public boolean isCollection() {
-		return getJoinableType().isCollectionType();
-	}
-
-	@Override
-	public Joinable getJoinable() {
-		return joinable;
-	}
-
-	public boolean hasRestriction() {
-		return hasRestriction;
-	}
-
-	@Override
-	public boolean isManyToManyWith(JoinableAssociation other) {
-		if ( joinable.isCollection() ) {
-			QueryableCollection persister = ( QueryableCollection ) joinable;
-			if ( persister.isManyToMany() ) {
-				return persister.getElementType() == other.getJoinableType();
-			}
-		}
-		return false;
-	}
-
-	@Override
-	public String[] getRhsColumns() {
-		return rhsColumns;
-	}
-
-	@Override
-	public String getWithClause() {
-		return withClause;
-	}
-
-	@Override
-	public Map<String, Filter> getEnabledFilters() {
-		return enabledFilters;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
index 688f57cce5..d829669a29 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/LoadQueryAliasResolutionContextImpl.java
@@ -1,325 +1,313 @@
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
 import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.DefaultEntityAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.GeneratedCollectionAliases;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CollectionReturn;
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
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.type.EntityType;
 
 /**
+ * Provides aliases that are used by load queries and ResultSet processors.
+ *
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
-	public String resolveEntitySqlTableAlias(EntityReference entityReference) {
+	public String resolveEntityTableAlias(EntityReference entityReference) {
 		return getOrGenerateLoadQueryEntityAliases( entityReference ).tableAlias;
 	}
 
 	@Override
 	public EntityAliases resolveEntityColumnAliases(EntityReference entityReference) {
 		return getOrGenerateLoadQueryEntityAliases( entityReference ).columnAliases;
 	}
 
 	@Override
-	public String resolveCollectionSqlTableAlias(CollectionReference collectionReference) {
+	public String resolveCollectionTableAlias(CollectionReference collectionReference) {
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
-	public String resolveRhsAlias(JoinableAssociation joinableAssociation) {
+	public String resolveAssociationRhsTableAlias(JoinableAssociation joinableAssociation) {
 		return getOrGenerateJoinAssocationAliases( joinableAssociation ).rhsAlias;
 	}
 
 	@Override
-	public String resolveLhsAlias(JoinableAssociation joinableAssociation) {
+	public String resolveAssociationLhsTableAlias(JoinableAssociation joinableAssociation) {
 		return getOrGenerateJoinAssocationAliases( joinableAssociation ).lhsAlias;
 	}
 
 	@Override
-	public String[] resolveAliasedLhsColumnNames(JoinableAssociation joinableAssociation) {
+	public String[] resolveAssociationAliasedLhsColumnNames(JoinableAssociation joinableAssociation) {
 		return getOrGenerateJoinAssocationAliases( joinableAssociation ).aliasedLhsColumnNames;
 	}
 
-	@Override
-	public EntityAliases resolveCurrentEntityAliases(JoinableAssociation joinableAssociation) {
-		return joinableAssociation.getCurrentEntityReference() == null ?
-				null:
-				resolveEntityColumnAliases( joinableAssociation.getCurrentEntityReference() );
-	}
-
-	@Override
-	public CollectionAliases resolveCurrentCollectionAliases(JoinableAssociation joinableAssociation) {
-		return joinableAssociation.getCurrentCollectionReference() == null ?
-				null:
-				resolveCollectionColumnAliases( joinableAssociation.getCurrentCollectionReference() );
-	}
-
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
-				lhsAlias = resolveEntitySqlTableAlias( (EntityReference) currentFetch.getOwner() );
+				lhsAlias = resolveEntityTableAlias( (EntityReference) currentFetch.getOwner() );
 			}
 			else {
 				throw new NotYetImplementedException( "Cannot determine LHS alias for a FetchOwner that is not an EntityReference yet." );
 			}
 			final String rhsAlias;
 			if ( EntityReference.class.isInstance( currentFetch ) ) {
-				rhsAlias = resolveEntitySqlTableAlias( (EntityReference) currentFetch );
+				rhsAlias = resolveEntityTableAlias( (EntityReference) currentFetch );
 			}
 			else if ( CollectionReference.class.isInstance( joinableAssociation.getCurrentFetch() ) ) {
-				rhsAlias = resolveCollectionSqlTableAlias( (CollectionReference) currentFetch );
+				rhsAlias = resolveCollectionTableAlias( (CollectionReference) currentFetch );
 			}
 			else {
 				throw new NotYetImplementedException( "Cannot determine RHS alis for a fetch that is not an EntityReference or CollectionReference." );
 			}
 
 			// TODO: can't this be found in CollectionAliases or EntityAliases? should be moved to LoadQueryAliasResolutionContextImpl
 			final OuterJoinLoadable fetchSourcePersister = (OuterJoinLoadable) currentFetch.getOwner().retrieveFetchSourcePersister();
 			final int propertyNumber = fetchSourcePersister.getEntityMetamodel().getPropertyIndex( currentFetch.getOwnerPropertyName() );
 			final String[] aliasedLhsColumnNames = JoinHelper.getAliasedLHSColumnNames(
-					joinableAssociation.getJoinableType(),
+					joinableAssociation.getAssociationType(),
 					lhsAlias,
 					propertyNumber,
 					fetchSourcePersister,
 					sessionFactory
 			);
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/JoinableAssociation.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/JoinableAssociation.java
index 5e4355091a..d25098bc72 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/spi/JoinableAssociation.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/JoinableAssociation.java
@@ -1,66 +1,172 @@
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
 
 import java.util.Map;
 
 import org.hibernate.Filter;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 
 /**
+ * Represents a joinable (entity or collection) association.
+ *
  * @author Gail Badner
  */
 public interface JoinableAssociation {
+
+	/**
+	 * Returns the property path of the association.
+	 *
+ 	 * @return the property path of the association.
+	 */
 	PropertyPath getPropertyPath();
 
+	/**
+	 * Returns the type of join used for the association.
+	 *
+	 * @return the type of join used for the association.
+	 *
+	 * @see JoinType
+	 */
 	JoinType getJoinType();
 
+	/**
+	 * Returns the current association fetch object.
+	 *
+	 * @return the current association fetch object.
+	 *
+	 * @see Fetch
+	 */
 	Fetch getCurrentFetch();
 
+	/**
+	 * Return the current {@link EntityReference}, or null if none.
+     * <p/>
+	 * If {@link #getCurrentFetch()} returns an
+	 * {@link org.hibernate.loader.plan.spi.EntityFetch}, this method will
+	 * return the same object as {@link #getCurrentFetch()}.
+	 * <p/>
+	 * If {@link #getCurrentFetch()} returns a
+	 * {@link org.hibernate.loader.plan.spi.CollectionFetch} and
+	 * the collection's owner is returned or fetched, this
+	 * method will return the {@link EntityReference} that owns the
+	 * {@link Fetch} returned by {@link #getCurrentFetch()};
+	 * otherwise this method returns null.
+	 *
+	 * @return the current {@link EntityReference}, or null if none.
+	 *
+	 * @see #getCurrentFetch()
+	 * @see Fetch
+	 * @see org.hibernate.loader.plan.spi.CollectionFetch
+	 * @see org.hibernate.loader.plan.spi.EntityFetch
+	 * @see EntityReference
+	 */
 	EntityReference getCurrentEntityReference();
 
+	/**
+	 * Return the current {@link CollectionReference}, or null if none.
+	 * <p/>
+	 * If {@link #getCurrentFetch()} returns a
+	 * {@link org.hibernate.loader.plan.spi.CollectionFetch}, this method
+	 * will return the same object as {@link #getCurrentFetch()}.
+	 * <p/>
+	 * If {@link #getCurrentFetch()} returns an
+	 * {@link org.hibernate.loader.plan.spi.EntityFetch} that is
+	 * a collection element (or part of a composite collection element),
+	 * and that collection is being returned or fetched, this
+	 * method will return the {@link CollectionReference};
+	 * otherwise this method returns null.
+	 *
+	 * @return the current {@link CollectionReference}, or null if none.
+	 *
+	 * @see #getCurrentFetch()
+	 * @see Fetch
+	 * @see org.hibernate.loader.plan.spi.EntityFetch
+	 * @see org.hibernate.loader.plan.spi.CollectionFetch
+	 * @see CollectionReference
+	 */
 	CollectionReference getCurrentCollectionReference();
 
-	AssociationType getJoinableType();
+	/**
+	 * Returns the association type.
+	 *
+	 * @return the association type.
+	 *
+	 * @see AssociationType
+	 */
+	AssociationType getAssociationType();
 
+	/**
+	 * Return the persister for creating the join for the association.
+	 *
+	 * @return the persister for creating the join for the association.
+	 */
 	Joinable getJoinable();
 
+	/**
+	 * Is this a collection association?
+	 *
+	 * @return true, if this is a collection association; false otherwise.
+	 */
 	boolean isCollection();
 
-	public String[] getRhsColumns();
-
+	/**
+	 * Does this association have a restriction?
+	 *
+	 * @return true if this association has a restriction; false, otherwise.
+	 */
 	boolean hasRestriction();
 
+	/**
+	 * Does this association have a many-to-many association
+	 * with the specified association?
+     *
+	 * @param other - the other association.
+	 * @return true, if this association has a many-to-many association
+	 *         with the other association; false otherwise.
+	 */
 	boolean isManyToManyWith(JoinableAssociation other);
 
+	/**
+	 * Returns the with clause for this association.
+	 *
+	 * @return the with clause for this association.
+	 */
 	String getWithClause();
 
+	/**
+	 * Returns the filters that are enabled for this association.
+	 *
+	 * @return the filters that are enabled for this association.
+	 *
+	 * @see Filter
+	 */
 	Map<String,Filter> getEnabledFilters();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryAliasResolutionContext.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryAliasResolutionContext.java
index c2555bbc54..20672f94e3 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryAliasResolutionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryAliasResolutionContext.java
@@ -1,81 +1,134 @@
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
 
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.ScalarReturn;
 
 /**
+ * Provides aliases that are used by load queries and ResultSet processors.
+ *
  * @author Gail Badner
  */
 public interface LoadQueryAliasResolutionContext {
 
+	/**
+	 * Resolve the alias associated with the specified {@link EntityReturn}.
+	 *
+	 * @param entityReturn - the {@link EntityReturn}.
+	 *
+	 * @return the alias associated with the specified {@link EntityReturn}.
+	 */
 	public String resolveEntityReturnAlias(EntityReturn entityReturn);
 
+	/**
+	 * Resolve the alias associated with the specified {@link CollectionReturn}.
+	 *
+	 * @param collectionReturn - the {@link CollectionReturn}.
+	 *
+	 * @return the alias associated with {@link CollectionReturn}.
+	 */
 	public String resolveCollectionReturnAlias(CollectionReturn collectionReturn);
 
+	/**
+	 * Resolve the aliases associated with the specified {@link ScalarReturn}.
+	 *
+	 * @param scalarReturn - the {@link ScalarReturn}.
+	 *
+	 * @return the alias associated with {@link ScalarReturn}.
+	 */
 	String[] resolveScalarReturnAliases(ScalarReturn scalarReturn);
 
 	/**
-	 * Retrieve the SQL table alias.
+	 * Resolve the SQL table alias for the specified {@link EntityReference}.
 	 *
-	 * @return The SQL table alias
+	 * @param entityReference - the {@link EntityReference}.
+	 * @return The SQL table alias for the specified {@link EntityReference}.
 	 */
-	String resolveEntitySqlTableAlias(EntityReference entityReference);
+	String resolveEntityTableAlias(EntityReference entityReference);
 
+	/**
+	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to
+	 * an entity.
+	 *
+	 * @param entityReference - the {@link EntityReference} for the entity.
+	 *
+	 * @return The ResultSet alias descriptor for the {@link EntityReference}
+	 */
 	EntityAliases resolveEntityColumnAliases(EntityReference entityReference);
 
-	String resolveCollectionSqlTableAlias(CollectionReference collectionReference);
+	/**
+	 * Resolve the SQL table alias for the specified {@link CollectionReference}.
+	 *
+	 * @param collectionReference - the {@link CollectionReference}.
+	 * @return The SQL table alias for the specified {@link CollectionReference}.
+	 */
+	String resolveCollectionTableAlias(CollectionReference collectionReference);
 
 	/**
-	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to the
-	 * this collection.
+	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to
+	 * the specified {@link CollectionReference}.
 	 *
-	 * @return The ResultSet alias descriptor for the collection
+	 * @return The ResultSet alias descriptor for the {@link CollectionReference}
 	 */
 	CollectionAliases resolveCollectionColumnAliases(CollectionReference collectionReference);
 
 	/**
 	 * If the elements of this collection are entities, this methods returns the JDBC ResultSet alias descriptions
 	 * for that entity; {@code null} indicates a non-entity collection.
 	 *
 	 * @return The ResultSet alias descriptor for the collection's entity element, or {@code null}
 	 */
 	EntityAliases resolveCollectionElementColumnAliases(CollectionReference collectionReference);
 
-	String resolveRhsAlias(JoinableAssociation joinableAssociation);
-
-	String resolveLhsAlias(JoinableAssociation joinableAssociation);
-
-	String[] resolveAliasedLhsColumnNames(JoinableAssociation joinableAssociation);
+	/**
+	 * Resolve the table alias on the right-hand-side of the specified association.
+	 *
+	 * @param association - the joinable association.
+	 *
+	 * @return the table alias on the right-hand-side of the specified association.
+	 */
+	String resolveAssociationRhsTableAlias(JoinableAssociation association);
 
-	EntityAliases resolveCurrentEntityAliases(JoinableAssociation joinableAssociation);
+	/**
+	 * Resolve the table alias on the left-hand-side of the specified association.
+	 *
+	 * @param association - the joinable association.
+	 *
+	 * @return the table alias on the left-hand-side of the specified association.
+	 */
+	String resolveAssociationLhsTableAlias(JoinableAssociation association);
 
-	CollectionAliases resolveCurrentCollectionAliases(JoinableAssociation joinableAssociation);
+	/**
+	 * Resolve the column aliases on the left-hand-side of the specified association.
+	 * @param association - the joinable association
+	 * @return the column aliases on the left-hand-side of the specified association.
+	 */
+	String[] resolveAssociationAliasedLhsColumnNames(JoinableAssociation association);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryBuilder.java
index 773f145ddb..a88548f587 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/LoadQueryBuilder.java
@@ -1,32 +1,44 @@
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
 package org.hibernate.loader.spi;
 
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+
 /**
+ * Builds a load query for generating SQL.
+ *
  * @author Gail Badner
  */
 public interface LoadQueryBuilder {
 
-	String generateSql(int batchSize);
+	/**
+	 * Generates SQL for the performing the load.
+	 * @param batchSize - the batch size.
+	 * @param factory - the session factory.
+	 * @param aliasResolutionContext - the alias resolution context.
+	 *
+	 * @return the SQL string for performing the load
+	 */
+	String generateSql(int batchSize, SessionFactoryImplementor factory, LoadQueryAliasResolutionContext aliasResolutionContext);
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
index d223f9bff0..33c32e0504 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
@@ -1,330 +1,326 @@
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
 import java.util.Collections;
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
 import org.hibernate.loader.internal.LoadQueryAliasResolutionContextImpl;
 import org.hibernate.loader.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
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
-					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan,
-					aliasResolutionContext
+					plan
 			);
-			final String sql = queryBuilder.generateSql( 1 );
+			final String sql = queryBuilder.generateSql( 1, sessionFactory(), aliasResolutionContext );
 
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
-					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan,
-					aliasResolutionContext
+					plan
 			);
-			final String sql = queryBuilder.generateSql( 1 );
+			final String sql = queryBuilder.generateSql( 1, sessionFactory(), aliasResolutionContext );
 
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
index 182f145257..da1186e0f6 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithCollectionResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithCollectionResultSetProcessorTest.java
@@ -1,186 +1,178 @@
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
 import java.util.Collections;
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
 import org.hibernate.loader.internal.LoadQueryAliasResolutionContextImpl;
 import org.hibernate.loader.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
 import org.hibernate.loader.spi.LoadQueryAliasResolutionContext;
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
 
-		session = openSession();
-		session.beginTransaction();
-		session.get( Person.class, person.id );
-		session.getTransaction().commit();
-		session.close();
-
 		{
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
-					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan,
-					aliasResolutionContext
+					plan
 			);
-			final String sql = queryBuilder.generateSql( 1 );
+			final String sql = queryBuilder.generateSql( 1, sessionFactory(), aliasResolutionContext );
 
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
index c9c17a699a..2256b881c1 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
@@ -1,170 +1,168 @@
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
 import java.util.Collections;
 import java.util.List;
 
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
-					sessionFactory(),
 					LoadQueryInfluencers.NONE,
-					plan,
-					aliasResolutionContext
+					plan
 			);
-			final String sql = queryBuilder.generateSql( 1 );
+			final String sql = queryBuilder.generateSql( 1, sessionFactory(), aliasResolutionContext );
 
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
