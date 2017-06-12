diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
index 01d340f101..c91b5d308f 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
@@ -1,755 +1,765 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Represents a reference to a property or alias expression.  This should duplicate the relevant behaviors in
  * PathExpressionParser.
  *
  * @author Joshua Davis
  */
 public class DotNode extends FromReferenceNode implements DisplayableNode, SelectExpression {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DotNode.class );
 
 	///////////////////////////////////////////////////////////////////////////
 	// USED ONLY FOR REGRESSION TESTING!!!!
 	//
 	// todo : obviously get rid of all this junk ;)
 	///////////////////////////////////////////////////////////////////////////
 	public static boolean useThetaStyleImplicitJoins;
 	public static boolean regressionStyleJoinSuppression;
 
 	public static interface IllegalCollectionDereferenceExceptionBuilder {
 		public QueryException buildIllegalCollectionDereferenceException(
 				String collectionPropertyName,
 				FromReferenceNode lhs);
 	}
 
 	public static final IllegalCollectionDereferenceExceptionBuilder DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER = new IllegalCollectionDereferenceExceptionBuilder() {
 		public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
 			String lhsPath = ASTUtil.getPathText( lhs );
 			return new QueryException( "illegal attempt to dereference collection [" + lhsPath + "] with element property reference [" + propertyName + "]" );
 		}
 	};
 	public static IllegalCollectionDereferenceExceptionBuilder ILLEGAL_COLL_DEREF_EXCP_BUILDER = DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
 	///////////////////////////////////////////////////////////////////////////
 
 	public static enum DereferenceType {
 		UNKNOWN,
 		ENTITY,
 		COMPONENT,
 		COLLECTION,
 		PRIMITIVE,
 		IDENTIFIER,
 		JAVA_CONSTANT
 	}
 
 	/**
 	 * The identifier that is the name of the property.
 	 */
 	private String propertyName;
 	/**
 	 * The full path, to the root alias of this dot node.
 	 */
 	private String path;
 	/**
 	 * The unresolved property path relative to this dot node.
 	 */
 	private String propertyPath;
 
 	/**
 	 * The column names that this resolves to.
 	 */
 	private String[] columns;
 
 	/**
 	 * The type of join to create.   Default is an inner join.
 	 */
 	private JoinType joinType = JoinType.INNER_JOIN;
 
 	/**
 	 * Fetch join or not.
 	 */
 	private boolean fetch;
 
 	/**
 	 * The type of dereference that hapened
 	 */
 	private DereferenceType dereferenceType = DereferenceType.UNKNOWN;
 
 	private FromElement impliedJoin;
 
 	/**
 	 * Sets the join type for this '.' node structure.
 	 *
 	 * @param joinType The type of join to use.
 	 *
 	 * @see JoinFragment
 	 */
 	public void setJoinType(JoinType joinType) {
 		this.joinType = joinType;
 	}
 
 	private String[] getColumns() throws QueryException {
 		if ( columns == null ) {
 			// Use the table fromElement and the property name to get the array of column names.
 			String tableAlias = getLhs().getFromElement().getTableAlias();
 			columns = getFromElement().toColumns( tableAlias, propertyPath, false );
 		}
 		return columns;
 	}
 
 	@Override
 	public String getDisplayText() {
 		StringBuilder buf = new StringBuilder();
 		FromElement fromElement = getFromElement();
 		buf.append( "{propertyName=" ).append( propertyName );
 		buf.append( ",dereferenceType=" ).append( dereferenceType.name() );
 		buf.append( ",getPropertyPath=" ).append( propertyPath );
 		buf.append( ",path=" ).append( getPath() );
 		if ( fromElement != null ) {
 			buf.append( ",tableAlias=" ).append( fromElement.getTableAlias() );
 			buf.append( ",className=" ).append( fromElement.getClassName() );
 			buf.append( ",classAlias=" ).append( fromElement.getClassAlias() );
 		}
 		else {
 			buf.append( ",no from element" );
 		}
 		buf.append( '}' );
 		return buf.toString();
 	}
 
 	/**
 	 * Resolves the left hand side of the DOT.
 	 *
 	 * @throws SemanticException
 	 */
 	@Override
 	public void resolveFirstChild() throws SemanticException {
 		FromReferenceNode lhs = (FromReferenceNode) getFirstChild();
 		SqlNode property = (SqlNode) lhs.getNextSibling();
 
 		// Set the attributes of the property reference expression.
 		String propName = property.getText();
 		propertyName = propName;
 		// If the uresolved property path isn't set yet, just use the property name.
 		if ( propertyPath == null ) {
 			propertyPath = propName;
 		}
 		// Resolve the LHS fully, generate implicit joins.  Pass in the property name so that the resolver can
 		// discover foreign key (id) properties.
 		lhs.resolve( true, true, null, this );
 		setFromElement( lhs.getFromElement() );            // The 'from element' that the property is in.
 
 		checkSubclassOrSuperclassPropertyReference( lhs, propName );
 	}
 
 	@Override
 	public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs();            // Prepare the left hand side and get the data type.
 		if ( propertyType != null && propertyType.isCollectionType() ) {
 			resolveIndex( null );
 		}
 		else {
 			resolveFirstChild();
 			super.resolve( generateJoin, implicitJoin );
 		}
 	}
 
 
 	public void resolveIndex(AST parent) throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs();            // Prepare the left hand side and get the data type.
 		dereferenceCollection( (CollectionType) propertyType, true, true, null, parent );
 	}
 
 	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
 			throws SemanticException {
 		// If this dot has already been resolved, stop now.
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs(); // Prepare the left hand side and get the data type.
 
 		// If there is no data type for this node, and we're at the end of the path (top most dot node), then
 		// this might be a Java constant.
 		if ( propertyType == null ) {
 			if ( parent == null ) {
 				getWalker().getLiteralProcessor().lookupConstant( this );
 			}
 			// If the propertyType is null and there isn't a parent, just
 			// stop now... there was a problem resolving the node anyway.
 			return;
 		}
 
 		if ( propertyType.isComponentType() ) {
 			// The property is a component...
 			checkLhsIsNotCollection();
 			dereferenceComponent( parent );
 			initText();
 		}
 		else if ( propertyType.isEntityType() ) {
 			// The property is another class..
 			checkLhsIsNotCollection();
 			dereferenceEntity( (EntityType) propertyType, implicitJoin, classAlias, generateJoin, parent );
 			initText();
 		}
 		else if ( propertyType.isCollectionType() ) {
 			// The property is a collection...
 			checkLhsIsNotCollection();
 			dereferenceCollection( (CollectionType) propertyType, implicitJoin, false, classAlias, parent );
 		}
 		else {
 			// Otherwise, this is a primitive type.
 			if ( !CollectionProperties.isAnyCollectionProperty( propertyName ) ) {
 				checkLhsIsNotCollection();
 			}
 			dereferenceType = DereferenceType.PRIMITIVE;
 			initText();
 		}
 		setResolved();
 	}
 
 	private void initText() {
 		String[] cols = getColumns();
 		String text = StringHelper.join( ", ", cols );
 		boolean countDistinct = getWalker().isInCountDistinct()
 				&& getWalker().getSessionFactoryHelper().getFactory().getDialect().requiresParensForTupleDistinctCounts();
 		if ( cols.length > 1 &&
 				( getWalker().isComparativeExpressionClause() || countDistinct ) ) {
 			text = "(" + text + ")";
 		}
 		setText( text );
 	}
 
 	private Type prepareLhs() throws SemanticException {
 		FromReferenceNode lhs = getLhs();
 		lhs.prepareForDot( propertyName );
 		return getDataType();
 	}
 
 	private void dereferenceCollection(
 			CollectionType collectionType,
 			boolean implicitJoin,
 			boolean indexed,
 			String classAlias,
 			AST parent)
 			throws SemanticException {
 
 		dereferenceType = DereferenceType.COLLECTION;
 		String role = collectionType.getRole();
 
 		//foo.bars.size (also handles deprecated stuff like foo.bars.maxelement for backwardness)
 		boolean isSizeProperty = getNextSibling() != null &&
 				CollectionProperties.isAnyCollectionProperty( getNextSibling().getText() );
 
 		if ( isSizeProperty ) {
 			indexed = true; //yuck!
 		}
 
 		QueryableCollection queryableCollection = getSessionFactoryHelper().requireQueryableCollection( role );
 		String propName = getPath();
 		FromClause currentFromClause = getWalker().getCurrentFromClause();
 
+		// If the lhs of the join is a "component join", we need to go back to the
+		// first non-component-join as the origin to properly link aliases and
+		// join columns
+		FromElement lhsFromElement = getLhs().getFromElement();
+		while ( lhsFromElement != null && ComponentJoin.class.isInstance( lhsFromElement ) ) {
+			lhsFromElement = lhsFromElement.getOrigin();
+		}
+		if ( lhsFromElement == null ) {
+			throw new QueryException( "Unable to locate appropriate lhs" );
+		}
+
 		// determine whether we should use the table name or table alias to qualify the column names...
 		// we need to use the table-name when:
 		//		1) the top-level statement is not a SELECT
 		//		2) the LHS FromElement is *the* FromElement from the top-level statement
 		//
 		// there is a caveat here.. if the update/delete statement are "multi-table" we should continue to use
 		// the alias also, even if the FromElement is the root one...
 		//
 		// in all other cases, we should use the table alias
-		final FromElement lhsFromElement = getLhs().getFromElement();
 		if ( getWalker().getStatementType() != SqlTokenTypes.SELECT ) {
 			if ( isFromElementUpdateOrDeleteRoot( lhsFromElement ) ) {
 				// at this point we know we have the 2 conditions above,
 				// lets see if we have the mentioned "multi-table" caveat...
 				boolean useAlias = false;
 				if ( getWalker().getStatementType() != SqlTokenTypes.INSERT ) {
 					final Queryable persister = lhsFromElement.getQueryable();
 					if ( persister.isMultiTable() ) {
 						useAlias = true;
 					}
 				}
 				if ( !useAlias ) {
 					final String lhsTableName = lhsFromElement.getQueryable().getTableName();
 					columns = getFromElement().toColumns( lhsTableName, propertyPath, false, true );
 				}
 			}
 		}
 
 		// We do not look for an existing join on the same path, because
 		// it makes sense to join twice on the same collection role
 		FromElementFactory factory = new FromElementFactory(
 				currentFromClause,
-				getLhs().getFromElement(),
+				lhsFromElement,
 				propName,
 				classAlias,
 				getColumns(),
 				implicitJoin
 		);
 		FromElement elem = factory.createCollection( queryableCollection, role, joinType, fetch, indexed );
 
 		LOG.debugf( "dereferenceCollection() : Created new FROM element for %s : %s", propName, elem );
 
 		setImpliedJoin( elem );
 		setFromElement( elem );    // This 'dot' expression now refers to the resulting from element.
 
 		if ( isSizeProperty ) {
 			elem.setText( "" );
 			elem.setUseWhereFragment( false );
 		}
 
 		if ( !implicitJoin ) {
 			EntityPersister entityPersister = elem.getEntityPersister();
 			if ( entityPersister != null ) {
 				getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
 			}
 		}
 		getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );    // Always add the collection's query spaces.
 	}
 
 	private void dereferenceEntity(
 			EntityType entityType,
 			boolean implicitJoin,
 			String classAlias,
 			boolean generateJoin,
 			AST parent) throws SemanticException {
 		checkForCorrelatedSubquery( "dereferenceEntity" );
 		// three general cases we check here as to whether to render a physical SQL join:
 		// 1) is our parent a DotNode as well?  If so, our property reference is
 		// 		being further de-referenced...
 		// 2) is this a DML statement
 		// 3) we were asked to generate any needed joins (generateJoins==true) *OR*
 		//		we are currently processing a select or from clause
 		// (an additional check is the regressionStyleJoinSuppression check solely intended for the test suite)
 		//
 		// The regressionStyleJoinSuppression is an additional check
 		// intended solely for use within the test suite.  This forces the
 		// implicit join resolution to behave more like the classic parser.
 		// The underlying issue is that classic translator is simply wrong
 		// about its decisions on whether or not to render an implicit join
 		// into a physical SQL join in a lot of cases.  The piece it generally
 		// tends to miss is that INNER joins effect the results by further
 		// restricting the data set!  A particular manifestation of this is
 		// the fact that the classic translator will skip the physical join
 		// for ToOne implicit joins *if the query is shallow*; the result
 		// being that Query.list() and Query.iterate() could return
 		// different number of results!
 		DotNode parentAsDotNode = null;
 		String property = propertyName;
 		final boolean joinIsNeeded;
 
 		if ( isDotNode( parent ) ) {
 			// our parent is another dot node, meaning we are being further dereferenced.
 			// thus we need to generate a join unless the parent refers to the associated
 			// entity's PK (because 'our' table would know the FK).
 			parentAsDotNode = (DotNode) parent;
 			property = parentAsDotNode.propertyName;
 			joinIsNeeded = generateJoin && !isReferenceToPrimaryKey( parentAsDotNode.propertyName, entityType );
 		}
 		else if ( !getWalker().isSelectStatement() ) {
 			// in non-select queries, the only time we should need to join is if we are in a subquery from clause
 			joinIsNeeded = getWalker().getCurrentStatementType() == SqlTokenTypes.SELECT && getWalker().isInFrom();
 		}
 		else if ( regressionStyleJoinSuppression ) {
 			// this is the regression style determination which matches the logic of the classic translator
 			joinIsNeeded = generateJoin && ( !getWalker().isInSelect() || !getWalker().isShallowQuery() );
 		}
 		else {
 			joinIsNeeded = generateJoin || ( getWalker().isInSelect() || getWalker().isInFrom() );
 		}
 
 		if ( joinIsNeeded ) {
 			dereferenceEntityJoin( classAlias, entityType, implicitJoin, parent );
 		}
 		else {
 			dereferenceEntityIdentifier( property, parentAsDotNode );
 		}
 
 	}
 
 	private boolean isDotNode(AST n) {
 		return n != null && n.getType() == SqlTokenTypes.DOT;
 	}
 
 	private void dereferenceEntityJoin(String classAlias, EntityType propertyType, boolean impliedJoin, AST parent)
 			throws SemanticException {
 		dereferenceType = DereferenceType.ENTITY;
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"dereferenceEntityJoin() : generating join for %s in %s (%s) parent = %s",
 					propertyName,
 					getFromElement().getClassName(),
 					classAlias == null ? "<no alias>" : classAlias,
 					ASTUtil.getDebugString( parent )
 			);
 		}
 		// Create a new FROM node for the referenced class.
 		String associatedEntityName = propertyType.getAssociatedEntityName();
 		String tableAlias = getAliasGenerator().createName( associatedEntityName );
 
 		String[] joinColumns = getColumns();
 		String joinPath = getPath();
 
 		if ( impliedJoin && getWalker().isInFrom() ) {
 			joinType = getWalker().getImpliedJoinType();
 		}
 
 		FromClause currentFromClause = getWalker().getCurrentFromClause();
 		FromElement elem = currentFromClause.findJoinByPath( joinPath );
 
 ///////////////////////////////////////////////////////////////////////////////
 //
 // This is the piece which recognizes the condition where an implicit join path
 // resolved earlier in a correlated subquery is now being referenced in the
 // outer query.  For 3.0final, we just let this generate a second join (which
 // is exactly how the old parser handles this).  Eventually we need to add this
 // logic back in and complete the logic in FromClause.promoteJoin; however,
 // FromClause.promoteJoin has its own difficulties (see the comments in
 // FromClause.promoteJoin).
 //
 //		if ( elem == null ) {
 //			// see if this joinPath has been used in a "child" FromClause, and if so
 //			// promote that element to the outer query
 //			FromClause currentNodeOwner = getFromElement().getFromClause();
 //			FromClause currentJoinOwner = currentNodeOwner.locateChildFromClauseWithJoinByPath( joinPath );
 //			if ( currentJoinOwner != null && currentNodeOwner != currentJoinOwner ) {
 //				elem = currentJoinOwner.findJoinByPathLocal( joinPath );
 //				if ( elem != null ) {
 //					currentFromClause.promoteJoin( elem );
 //					// EARLY EXIT!!!
 //					return;
 //				}
 //			}
 //		}
 //
 ///////////////////////////////////////////////////////////////////////////////
 
 		boolean found = elem != null;
 		// even though we might find a pre-existing element by join path, we may not be able to reuse it...
 		boolean useFoundFromElement = found && canReuse( elem );
 
 		if ( !useFoundFromElement ) {
 			// If this is an implied join in a from element, then use the impled join type which is part of the
 			// tree parser's state (set by the gramamar actions).
 			JoinSequence joinSequence = getSessionFactoryHelper()
 					.createJoinSequence( impliedJoin, propertyType, tableAlias, joinType, joinColumns );
 
 			// If the lhs of the join is a "component join", we need to go back to the
 			// first non-component-join as the origin to properly link aliases and
 			// join columns
 			FromElement lhsFromElement = getLhs().getFromElement();
 			while ( lhsFromElement != null && ComponentJoin.class.isInstance( lhsFromElement ) ) {
 				lhsFromElement = lhsFromElement.getOrigin();
 			}
 			if ( lhsFromElement == null ) {
 				throw new QueryException( "Unable to locate appropriate lhs" );
 			}
 
 			String role = lhsFromElement.getClassName() + "." + propertyName;
 
 			FromElementFactory factory = new FromElementFactory(
 					currentFromClause,
 					lhsFromElement,
 					joinPath,
 					classAlias,
 					joinColumns,
 					impliedJoin
 			);
 			elem = factory.createEntityJoin(
 					associatedEntityName,
 					tableAlias,
 					joinSequence,
 					fetch,
 					getWalker().isInFrom(),
 					propertyType,
 					role,
 					joinPath
 			);
 		}
 		else {
 			// NOTE : addDuplicateAlias() already performs nullness checks on the alias.
 			currentFromClause.addDuplicateAlias( classAlias, elem );
 		}
 		setImpliedJoin( elem );
 		getWalker().addQuerySpaces( elem.getEntityPersister().getQuerySpaces() );
 		setFromElement( elem );    // This 'dot' expression now refers to the resulting from element.
 	}
 
 	private boolean canReuse(FromElement fromElement) {
 		// if the from-clauses are the same, we can be a little more aggressive in terms of what we reuse
 		if ( fromElement.getFromClause() == getWalker().getCurrentFromClause() ) {
 			return true;
 		}
 
 		// otherwise (subquery case) dont reuse the fromElement if we are processing the from-clause of the subquery
 		return getWalker().getCurrentClauseType() != SqlTokenTypes.FROM;
 	}
 
 	private void setImpliedJoin(FromElement elem) {
 		this.impliedJoin = elem;
 		if ( getFirstChild().getType() == SqlTokenTypes.DOT ) {
 			DotNode dotLhs = (DotNode) getFirstChild();
 			if ( dotLhs.getImpliedJoin() != null ) {
 				this.impliedJoin = dotLhs.getImpliedJoin();
 			}
 		}
 	}
 
 	@Override
 	public FromElement getImpliedJoin() {
 		return impliedJoin;
 	}
 
 	/**
 	 * Is the given property name a reference to the primary key of the associated
 	 * entity construed by the given entity type?
 	 * <p/>
 	 * For example, consider a fragment like order.customer.id
 	 * (where order is a from-element alias).  Here, we'd have:
 	 * propertyName = "id" AND
 	 * owningType = ManyToOneType(Customer)
 	 * and are being asked to determine whether "customer.id" is a reference
 	 * to customer's PK...
 	 *
 	 * @param propertyName The name of the property to check.
 	 * @param owningType The type represeting the entity "owning" the property
 	 *
 	 * @return True if propertyName references the entity's (owningType->associatedEntity)
 	 *         primary key; false otherwise.
 	 */
 	private boolean isReferenceToPrimaryKey(String propertyName, EntityType owningType) {
 		EntityPersister persister = getSessionFactoryHelper()
 				.getFactory()
 				.getEntityPersister( owningType.getAssociatedEntityName() );
 		if ( persister.getEntityMetamodel().hasNonIdentifierPropertyNamedId() ) {
 			// only the identifier property field name can be a reference to the associated entity's PK...
 			return propertyName.equals( persister.getIdentifierPropertyName() ) && owningType.isReferenceToPrimaryKey();
 		}
 		// here, we have two possibilities:
 		// 1) the property-name matches the explicitly identifier property name
 		// 2) the property-name matches the implicit 'id' property name
 		// the referenced node text is the special 'id'
 		if ( EntityPersister.ENTITY_ID.equals( propertyName ) ) {
 			return owningType.isReferenceToPrimaryKey();
 		}
 		String keyPropertyName = getSessionFactoryHelper().getIdentifierOrUniqueKeyPropertyName( owningType );
 		return keyPropertyName != null && keyPropertyName.equals( propertyName ) && owningType.isReferenceToPrimaryKey();
 	}
 
 	private void checkForCorrelatedSubquery(String methodName) {
 		if ( isCorrelatedSubselect() ) {
 			LOG.debugf( "%s() : correlated subquery", methodName );
 		}
 	}
 
 	private boolean isCorrelatedSubselect() {
 		return getWalker().isSubQuery() &&
 				getFromElement().getFromClause() != getWalker().getCurrentFromClause();
 	}
 
 	private void checkLhsIsNotCollection() throws SemanticException {
 		if ( getLhs().getDataType() != null && getLhs().getDataType().isCollectionType() ) {
 			throw ILLEGAL_COLL_DEREF_EXCP_BUILDER.buildIllegalCollectionDereferenceException( propertyName, getLhs() );
 		}
 	}
 
 	private void dereferenceComponent(AST parent) {
 		dereferenceType = DereferenceType.COMPONENT;
 		setPropertyNameAndPath( parent );
 	}
 
 	private void dereferenceEntityIdentifier(String propertyName, DotNode dotParent) {
 		// special shortcut for id properties, skip the join!
 		// this must only occur at the _end_ of a path expression
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"dereferenceShortcut() : property %s in %s does not require a join.",
 					propertyName,
 					getFromElement().getClassName()
 			);
 		}
 
 		initText();
 		setPropertyNameAndPath( dotParent ); // Set the unresolved path in this node and the parent.
 		// Set the text for the parent.
 		if ( dotParent != null ) {
 			dotParent.dereferenceType = DereferenceType.IDENTIFIER;
 			dotParent.setText( getText() );
 			dotParent.columns = getColumns();
 		}
 	}
 
 	private void setPropertyNameAndPath(AST parent) {
 		if ( isDotNode( parent ) ) {
 			DotNode dotNode = (DotNode) parent;
 			AST lhs = dotNode.getFirstChild();
 			AST rhs = lhs.getNextSibling();
 			propertyName = rhs.getText();
 			propertyPath = propertyPath + "." + propertyName; // Append the new property name onto the unresolved path.
 			dotNode.propertyPath = propertyPath;
 			LOG.debugf( "Unresolved property path is now '%s'", dotNode.propertyPath );
 		}
 		else {
 			LOG.debugf( "Terminal getPropertyPath = [%s]", propertyPath );
 		}
 	}
 
 	@Override
 	public Type getDataType() {
 		if ( super.getDataType() == null ) {
 			FromElement fromElement = getLhs().getFromElement();
 			if ( fromElement == null ) {
 				return null;
 			}
 			// If the lhs is a collection, use CollectionPropertyMapping
 			Type propertyType = fromElement.getPropertyType( propertyName, propertyPath );
 			LOG.debugf( "getDataType() : %s -> %s", propertyPath, propertyType );
 			super.setDataType( propertyType );
 		}
 		return super.getDataType();
 	}
 
 	public void setPropertyPath(String propertyPath) {
 		this.propertyPath = propertyPath;
 	}
 
 	public String getPropertyPath() {
 		return propertyPath;
 	}
 
 	public FromReferenceNode getLhs() {
 		FromReferenceNode lhs = ( (FromReferenceNode) getFirstChild() );
 		if ( lhs == null ) {
 			throw new IllegalStateException( "DOT node with no left-hand-side!" );
 		}
 		return lhs;
 	}
 
 	/**
 	 * Returns the full path of the node.
 	 *
 	 * @return the full path of the node.
 	 */
 	@Override
 	public String getPath() {
 		if ( path == null ) {
 			FromReferenceNode lhs = getLhs();
 			if ( lhs == null ) {
 				path = getText();
 			}
 			else {
 				SqlNode rhs = (SqlNode) lhs.getNextSibling();
 				path = lhs.getPath() + "." + rhs.getOriginalText();
 			}
 		}
 		return path;
 	}
 
 	public void setFetch(boolean fetch) {
 		this.fetch = fetch;
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		String[] sqlColumns = getColumns();
 		ColumnHelper.generateScalarColumns( this, sqlColumns, i );
 	}
 
 	/**
 	 * Special method to resolve expressions in the SELECT list.
 	 *
 	 * @throws SemanticException if this cannot be resolved.
 	 */
 	public void resolveSelectExpression() throws SemanticException {
 		if ( getWalker().isShallowQuery() || getWalker().getCurrentFromClause().isSubQuery() ) {
 			resolve( false, true );
 		}
 		else {
 			resolve( true, false );
 			Type type = getDataType();
 			if ( type.isEntityType() ) {
 				FromElement fromElement = getFromElement();
 				fromElement.setIncludeSubclasses( true ); // Tell the destination fromElement to 'includeSubclasses'.
 				if ( useThetaStyleImplicitJoins ) {
 					fromElement.getJoinSequence().setUseThetaStyle( true );    // Use theta style (for regression)
 					// Move the node up, after the origin node.
 					FromElement origin = fromElement.getOrigin();
 					if ( origin != null ) {
 						ASTUtil.makeSiblingOfParent( origin, fromElement );
 					}
 				}
 			}
 		}
 
 		FromReferenceNode lhs = getLhs();
 		while ( lhs != null ) {
 			checkSubclassOrSuperclassPropertyReference( lhs, lhs.getNextSibling().getText() );
 			lhs = (FromReferenceNode) lhs.getFirstChild();
 		}
 	}
 
 	public void setResolvedConstant(String text) {
 		path = text;
 		dereferenceType = DereferenceType.JAVA_CONSTANT;
 		setResolved(); // Don't resolve the node again.
 	}
 
 	private boolean checkSubclassOrSuperclassPropertyReference(FromReferenceNode lhs, String propertyName) {
 		if ( lhs != null && !( lhs instanceof IndexNode ) ) {
 			final FromElement source = lhs.getFromElement();
 			if ( source != null ) {
 				source.handlePropertyBeingDereferenced( lhs.getDataType(), propertyName );
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java
index 513c3730bd..610c094242 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/embedded/EmbeddedTest.java
@@ -1,559 +1,619 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.test.annotations.embedded;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.List;
 import java.util.Set;
 import java.util.UUID;
 
+import org.hibernate.Hibernate;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.boot.MetadataBuilder;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.hibernate.test.annotations.embedded.FloatLeg.RateIndex;
 import org.hibernate.test.annotations.embedded.Leg.Frequency;
 import org.hibernate.test.util.SchemaUtil;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Emmanuel Bernard
  */
 public class EmbeddedTest extends BaseNonConfigCoreFunctionalTestCase {
 	@Test
 	public void testSimple() throws Exception {
 		Session s;
 		Transaction tx;
 		Person p = new Person();
 		Address a = new Address();
 		Country c = new Country();
 		Country bornCountry = new Country();
 		c.setIso2( "DM" );
 		c.setName( "Matt Damon Land" );
 		bornCountry.setIso2( "US" );
 		bornCountry.setName( "United States of America" );
 
 		a.address1 = "colorado street";
 		a.city = "Springfield";
 		a.country = c;
 		p.address = a;
 		p.bornIn = bornCountry;
 		p.name = "Homer";
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( p );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		p = (Person) s.get( Person.class, p.id );
 		assertNotNull( p );
 		assertNotNull( p.address );
 		assertEquals( "Springfield", p.address.city );
 		assertNotNull( p.address.country );
 		assertEquals( "DM", p.address.country.getIso2() );
 		assertNotNull( p.bornIn );
 		assertEquals( "US", p.bornIn.getIso2() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeId() throws Exception {
 		Session s;
 		Transaction tx;
 		RegionalArticlePk pk = new RegionalArticlePk();
 		pk.iso2 = "FR";
 		pk.localUniqueKey = "1234567890123";
 		RegionalArticle reg = new RegionalArticle();
 		reg.setName( "Je ne veux pes rester sage - Dolly" );
 		reg.setPk( pk );
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( reg );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		reg = (RegionalArticle) s.get( RegionalArticle.class, reg.getPk() );
 		assertNotNull( reg );
 		assertNotNull( reg.getPk() );
 		assertEquals( "Je ne veux pes rester sage - Dolly", reg.getName() );
 		assertEquals( "FR", reg.getPk().iso2 );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOneInsideComponent() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Person p = new Person();
 		Country bornIn = new Country();
 		bornIn.setIso2( "FR" );
 		bornIn.setName( "France" );
 		p.bornIn = bornIn;
 		p.name = "Emmanuel";
 		AddressType type = new AddressType();
 		type.setName( "Primary Home" );
 		s.persist( type );
 		Country currentCountry = new Country();
 		currentCountry.setIso2( "US" );
 		currentCountry.setName( "USA" );
 		Address add = new Address();
 		add.address1 = "4 square street";
 		add.city = "San diego";
 		add.country = currentCountry;
 		add.type = type;
 		p.address = add;
 		s.persist( p );
 		tx.commit();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "select p from Person p where p.address.city = :city" );
 		q.setString( "city", add.city );
 		List result = q.list();
 		Person samePerson = (Person) result.get( 0 );
 		assertNotNull( samePerson.address.type );
 		assertEquals( type.getName(), samePerson.address.type.getName() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedSuperclass() {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		VanillaSwap swap = new VanillaSwap();
 		swap.setInstrumentId( "US345421" );
 		swap.setCurrency( VanillaSwap.Currency.EUR );
 		FixedLeg fixed = new FixedLeg();
 		fixed.setPaymentFrequency( Leg.Frequency.SEMIANNUALLY );
 		fixed.setRate( 5.6 );
 		FloatLeg floating = new FloatLeg();
 		floating.setPaymentFrequency( Leg.Frequency.QUARTERLY );
 		floating.setRateIndex( FloatLeg.RateIndex.LIBOR );
 		floating.setRateSpread( 1.1 );
 		swap.setFixedLeg( fixed );
 		swap.setFloatLeg( floating );
 		s.persist( swap );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		swap = (VanillaSwap) s.get( VanillaSwap.class, swap.getInstrumentId() );
 		// All fields must be filled with non-default values
 		fixed = swap.getFixedLeg();
 		assertNotNull( "Fixed leg retrieved as null", fixed );
 		floating = swap.getFloatLeg();
 		assertNotNull( "Floating leg retrieved as null", floating );
 		assertEquals( Leg.Frequency.SEMIANNUALLY, fixed.getPaymentFrequency() );
 		assertEquals( Leg.Frequency.QUARTERLY, floating.getPaymentFrequency() );
 		s.delete( swap );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDottedProperty() {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		// Create short swap
 		Swap shortSwap = new Swap();
 		shortSwap.setTenor( 2 );
 		FixedLeg shortFixed = new FixedLeg();
 		shortFixed.setPaymentFrequency( Frequency.SEMIANNUALLY );
 		shortFixed.setRate( 5.6 );
 		FloatLeg shortFloating = new FloatLeg();
 		shortFloating.setPaymentFrequency( Frequency.QUARTERLY );
 		shortFloating.setRateIndex( RateIndex.LIBOR );
 		shortFloating.setRateSpread( 1.1 );
 		shortSwap.setFixedLeg( shortFixed );
 		shortSwap.setFloatLeg( shortFloating );
 		// Create medium swap
 		Swap swap = new Swap();
 		swap.setTenor( 7 );
 		FixedLeg fixed = new FixedLeg();
 		fixed.setPaymentFrequency( Frequency.MONTHLY );
 		fixed.setRate( 7.6 );
 		FloatLeg floating = new FloatLeg();
 		floating.setPaymentFrequency( Frequency.MONTHLY );
 		floating.setRateIndex( RateIndex.TIBOR );
 		floating.setRateSpread( 0.8 );
 		swap.setFixedLeg( fixed );
 		swap.setFloatLeg( floating );
 		// Create long swap
 		Swap longSwap = new Swap();
 		longSwap.setTenor( 7 );
 		FixedLeg longFixed = new FixedLeg();
 		longFixed.setPaymentFrequency( Frequency.MONTHLY );
 		longFixed.setRate( 7.6 );
 		FloatLeg longFloating = new FloatLeg();
 		longFloating.setPaymentFrequency( Frequency.MONTHLY );
 		longFloating.setRateIndex( RateIndex.TIBOR );
 		longFloating.setRateSpread( 0.8 );
 		longSwap.setFixedLeg( longFixed );
 		longSwap.setFloatLeg( longFloating );
 		// Compose a curve spread deal
 		SpreadDeal deal = new SpreadDeal();
 		deal.setId( "FX45632" );
 		deal.setNotional( 450000.0 );
 		deal.setShortSwap( shortSwap );
 		deal.setSwap( swap );
 		deal.setLongSwap( longSwap );
 		s.persist( deal );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		deal = (SpreadDeal) s.get( SpreadDeal.class, deal.getId() );
 		// All fields must be filled with non-default values
 		assertNotNull( "Short swap is null.", deal.getShortSwap() );
 		assertNotNull( "Swap is null.", deal.getSwap() );
 		assertNotNull( "Long swap is null.", deal.getLongSwap() );
 		assertEquals( 2, deal.getShortSwap().getTenor() );
 		assertEquals( 7, deal.getSwap().getTenor() );
 		assertEquals( 7, deal.getLongSwap().getTenor() );
 		assertNotNull( "Short fixed leg is null.", deal.getShortSwap().getFixedLeg() );
 		assertNotNull( "Short floating leg is null.", deal.getShortSwap().getFloatLeg() );
 		assertNotNull( "Fixed leg is null.", deal.getSwap().getFixedLeg() );
 		assertNotNull( "Floating leg is null.", deal.getSwap().getFloatLeg() );
 		assertNotNull( "Long fixed leg is null.", deal.getLongSwap().getFixedLeg() );
 		assertNotNull( "Long floating leg is null.", deal.getLongSwap().getFloatLeg() );
 		assertEquals( Frequency.SEMIANNUALLY, deal.getShortSwap().getFixedLeg().getPaymentFrequency() );
 		assertEquals( Frequency.QUARTERLY, deal.getShortSwap().getFloatLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getSwap().getFixedLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getSwap().getFloatLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getLongSwap().getFixedLeg().getPaymentFrequency() );
 		assertEquals( Frequency.MONTHLY, deal.getLongSwap().getFloatLeg().getPaymentFrequency() );
 		assertEquals( 5.6, deal.getShortSwap().getFixedLeg().getRate(), 0.01 );
 		assertEquals( 7.6, deal.getSwap().getFixedLeg().getRate(), 0.01 );
 		assertEquals( 7.6, deal.getLongSwap().getFixedLeg().getRate(), 0.01 );
 		assertEquals( RateIndex.LIBOR, deal.getShortSwap().getFloatLeg().getRateIndex() );
 		assertEquals( RateIndex.TIBOR, deal.getSwap().getFloatLeg().getRateIndex() );
 		assertEquals( RateIndex.TIBOR, deal.getLongSwap().getFloatLeg().getRateIndex() );
 		assertEquals( 1.1, deal.getShortSwap().getFloatLeg().getRateSpread(), 0.01 );
 		assertEquals( 0.8, deal.getSwap().getFloatLeg().getRateSpread(), 0.01 );
 		assertEquals( 0.8, deal.getLongSwap().getFloatLeg().getRateSpread(), 0.01 );
 		s.delete( deal );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedInSecondaryTable() throws Exception {
 		Session s;
 		s = openSession();
 		s.getTransaction().begin();
 		Book book = new Book();
 		book.setIsbn( "1234" );
 		book.setName( "HiA Second Edition" );
 		Summary summary = new Summary();
 		summary.setText( "This is a HiA SE summary" );
 		summary.setSize( summary.getText().length() );
 		book.setSummary( summary );
 		s.persist( book );
 		s.getTransaction().commit();
 
 		s.clear();
 
 		Transaction tx = s.beginTransaction();
 		Book loadedBook = (Book) s.get( Book.class, book.getIsbn() );
 		assertNotNull( loadedBook.getSummary() );
 		assertEquals( book.getSummary().getText(), loadedBook.getSummary().getText() );
 		s.delete( loadedBook );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParent() throws Exception {
 		Session s;
 		s = openSession();
 		s.getTransaction().begin();
 		Book book = new Book();
 		book.setIsbn( "1234" );
 		book.setName( "HiA Second Edition" );
 		Summary summary = new Summary();
 		summary.setText( "This is a HiA SE summary" );
 		summary.setSize( summary.getText().length() );
 		book.setSummary( summary );
 		s.persist( book );
 		s.getTransaction().commit();
 
 		s.clear();
 
 		Transaction tx = s.beginTransaction();
 		Book loadedBook = (Book) s.get( Book.class, book.getIsbn() );
 		assertNotNull( loadedBook.getSummary() );
 		assertEquals( loadedBook, loadedBook.getSummary().getSummarizedBook() );
 		s.delete( loadedBook );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedAndMultipleManyToOne() throws Exception {
 		Session s;
 		s = openSession();
 		Transaction tx = s.beginTransaction();
 		CorpType type = new CorpType();
 		type.setType( "National" );
 		s.persist( type );
 		Nationality nat = new Nationality();
 		nat.setName( "Canadian" );
 		s.persist( nat );
 		InternetProvider provider = new InternetProvider();
 		provider.setBrandName( "Fido" );
 		LegalStructure structure = new LegalStructure();
 		structure.setCorporationType( type );
 		structure.setCountry( "Canada" );
 		structure.setName( "Rogers" );
 		provider.setOwner( structure );
 		structure.setOrigin( nat );
 		s.persist( provider );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		provider = (InternetProvider) s.get( InternetProvider.class, provider.getId() );
 		assertNotNull( provider.getOwner() );
 		assertNotNull( "Many to one not set", provider.getOwner().getCorporationType() );
 		assertEquals( "Wrong link", type.getType(), provider.getOwner().getCorporationType().getType() );
 		assertNotNull( "2nd Many to one not set", provider.getOwner().getOrigin() );
 		assertEquals( "Wrong 2nd link", nat.getName(), provider.getOwner().getOrigin().getName() );
 		s.delete( provider );
 		s.delete( provider.getOwner().getCorporationType() );
 		s.delete( provider.getOwner().getOrigin() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedAndOneToMany() throws Exception {
 		Session s;
 		s = openSession();
 		Transaction tx = s.beginTransaction();
 		InternetProvider provider = new InternetProvider();
 		provider.setBrandName( "Fido" );
 		LegalStructure structure = new LegalStructure();
 		structure.setCountry( "Canada" );
 		structure.setName( "Rogers" );
 		provider.setOwner( structure );
 		s.persist( provider );
 		Manager manager = new Manager();
 		manager.setName( "Bill" );
 		manager.setEmployer( provider );
 		structure.getTopManagement().add( manager );
 		s.persist( manager );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		provider = (InternetProvider) s.get( InternetProvider.class, provider.getId() );
 		assertNotNull( provider.getOwner() );
 		Set<Manager> topManagement = provider.getOwner().getTopManagement();
 		assertNotNull( "OneToMany not set", topManagement );
 		assertEquals( "Wrong number of elements", 1, topManagement.size() );
 		manager = topManagement.iterator().next();
 		assertEquals( "Wrong element", "Bill", manager.getName() );
 		s.delete( manager );
 		s.delete( provider );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
+	@TestForIssue( jiraKey = "HHH-9642")
+	public void testEmbeddedAndOneToManyHql() throws Exception {
+		Session s;
+		s = openSession();
+		Transaction tx = s.beginTransaction();
+		InternetProvider provider = new InternetProvider();
+		provider.setBrandName( "Fido" );
+		LegalStructure structure = new LegalStructure();
+		structure.setCountry( "Canada" );
+		structure.setName( "Rogers" );
+		provider.setOwner( structure );
+		s.persist( provider );
+		Manager manager = new Manager();
+		manager.setName( "Bill" );
+		manager.setEmployer( provider );
+		structure.getTopManagement().add( manager );
+		s.persist( manager );
+		tx.commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		InternetProvider internetProviderQueried =
+				(InternetProvider) s.createQuery( "from InternetProvider" ).uniqueResult();
+		assertFalse( Hibernate.isInitialized( internetProviderQueried.getOwner().getTopManagement() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		internetProviderQueried =
+				(InternetProvider) s.createQuery( "from InternetProvider i join fetch i.owner.topManagement" )
+						.uniqueResult();
+		assertTrue( Hibernate.isInitialized( internetProviderQueried.getOwner().getTopManagement() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		internetProviderQueried =
+				(InternetProvider) s.createQuery( "from InternetProvider i join fetch i.owner o join fetch o.topManagement" )
+						.uniqueResult();
+		assertTrue( Hibernate.isInitialized( internetProviderQueried.getOwner().getTopManagement() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		tx = s.beginTransaction();
+		provider = (InternetProvider) s.get( InternetProvider.class, provider.getId() );
+		manager = provider.getOwner().getTopManagement().iterator().next();
+		s.delete( manager );
+		s.delete( provider );
+		tx.commit();
+		s.close();
+	}
+
+
+	@Test
 	public void testDefaultCollectionTable() throws Exception {
 		//are the tables correct?
 		assertTrue( SchemaUtil.isTablePresent("WealthyPerson_vacationHomes", metadata() ) );
 		assertTrue( SchemaUtil.isTablePresent("WealthyPerson_legacyVacationHomes", metadata() ) );
 		assertTrue( SchemaUtil.isTablePresent("WelPers_VacHomes", metadata() ) );
 
 		//just to make sure, use the mapping
 		Session s;
 		Transaction tx;
 		WealthyPerson p = new WealthyPerson();
 		Address a = new Address();
 		Address vacation = new Address();
 		Country c = new Country();
 		Country bornCountry = new Country();
 		c.setIso2( "DM" );
 		c.setName( "Matt Damon Land" );
 		bornCountry.setIso2( "US" );
 		bornCountry.setName( "United States of America" );
 
 		a.address1 = "colorado street";
 		a.city = "Springfield";
 		a.country = c;
 		vacation.address1 = "rock street";
 		vacation.city = "Plymouth";
 		vacation.country = c;
 		p.vacationHomes.add(vacation);
 		p.address = a;
 		p.bornIn = bornCountry;
 		p.name = "Homer";
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( p );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		p = (WealthyPerson) s.get( WealthyPerson.class, p.id );
 		assertNotNull( p );
 		assertNotNull( p.address );
 		assertEquals( "Springfield", p.address.city );
 		assertNotNull( p.address.country );
 		assertEquals( "DM", p.address.country.getIso2() );
 		assertNotNull( p.bornIn );
 		assertEquals( "US", p.bornIn.getIso2() );
 		tx.commit();
 		s.close();
 	}
 
 	// make sure we support collection of embeddable objects inside embeddable objects
 	@Test
 	public void testEmbeddableInsideEmbeddable() throws Exception {
 		Session s;
 		Transaction tx;
 
 		Collection<URLFavorite> urls = new ArrayList<URLFavorite>();
 		URLFavorite urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://highscalability.com/" );
 		urls.add(urlFavorite);
 
 		urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://www.jboss.org/" );
 		urls.add(urlFavorite);
 
 		urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://www.hibernate.org/" );
 		urls.add(urlFavorite);
 
 		urlFavorite = new URLFavorite();
 		urlFavorite.setUrl( "http://www.jgroups.org/" );
 		urls.add( urlFavorite );
 
  		Collection<String>ideas = new ArrayList<String>();
 		ideas.add( "lionheart" );
 		ideas.add( "xforms" );
 		ideas.add( "dynamic content" );
 		ideas.add( "http" );
 
 		InternetFavorites internetFavorites = new InternetFavorites();
 		internetFavorites.setLinks( urls );
 		internetFavorites.setIdeas( ideas );
 
 		FavoriteThings favoriteThings = new FavoriteThings();
 		favoriteThings.setWeb( internetFavorites );
 
 		s = openSession();
 
 		tx = s.beginTransaction();
 		s.persist(favoriteThings);
 		tx.commit();
 
 		tx = s.beginTransaction();
 		s.flush();
 		favoriteThings = (FavoriteThings) s.get( FavoriteThings.class,  favoriteThings.getId() );
 		assertTrue( "has web", favoriteThings.getWeb() != null );
 		assertTrue( "has ideas", favoriteThings.getWeb().getIdeas() != null );
 		assertTrue( "has favorite idea 'http'",favoriteThings.getWeb().getIdeas().contains("http") );
 		assertTrue( "has favorite idea 'http'",favoriteThings.getWeb().getIdeas().contains("dynamic content") );
 
 		urls = favoriteThings.getWeb().getLinks();
 		assertTrue( "has urls", urls != null);
 		URLFavorite[] favs = new URLFavorite[4];
 		urls.toArray(favs);
 		assertTrue( "has http://www.hibernate.org url favorite link",
 			"http://www.hibernate.org/".equals( favs[0].getUrl() ) ||
 			"http://www.hibernate.org/".equals( favs[1].getUrl() ) ||
 			"http://www.hibernate.org/".equals( favs[2].getUrl() ) ||
 			"http://www.hibernate.org/".equals( favs[3].getUrl() ));
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-3868")
 	public void testTransientMergeComponentParent() {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Book b = new Book();
 		b.setIsbn( UUID.randomUUID().toString() );
 		b.setSummary( new Summary() );
 		b = (Book) s.merge( b );
 		tx.commit();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Person.class,
 				WealthyPerson.class,
 				RegionalArticle.class,
 				AddressType.class,
 				VanillaSwap.class,
 				SpreadDeal.class,
 				Book.class,
 				InternetProvider.class,
 				CorpType.class,
 				Nationality.class,
 				Manager.class,
 				FavoriteThings.class
 		};
 	}
 
 	@Override
 	protected void configureMetadataBuilder(MetadataBuilder metadataBuilder) {
 		super.configureMetadataBuilder( metadataBuilder );
 		metadataBuilder.with( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
index 106fc78f77..404fa01327 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
@@ -1,1320 +1,1389 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.test.hql;
 
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.sql.Date;
 import java.sql.Time;
 import java.sql.Timestamp;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.CUBRIDDialect;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SQLServer2008Dialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.DiscriminatorType;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 import org.hibernate.transform.Transformers;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.ManyToOneType;
 import org.hibernate.type.Type;
 
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.any.IntegerPropertyValue;
 import org.hibernate.test.any.PropertySet;
 import org.hibernate.test.any.PropertyValue;
 import org.hibernate.test.any.StringPropertyValue;
 import org.hibernate.test.cid.Customer;
 import org.hibernate.test.cid.LineItem;
 import org.hibernate.test.cid.LineItem.Id;
 import org.hibernate.test.cid.Order;
 import org.hibernate.test.cid.Product;
 import org.junit.Test;
 
 import org.jboss.logging.Logger;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertClassAssignability;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Tests the integration of the new AST parser into the loading of query results using
  * the Hibernate persisters and loaders.
  * <p/>
  * Also used to test the syntax of the resulting sql against the underlying
  * database, specifically for functionality not supported by the classic
  * parser.
  *
  * @author Steve
  */
 @SkipForDialect(
         value = CUBRIDDialect.class,
         comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" +
                 "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables"
 )
 public class ASTParserLoadingTest extends BaseCoreFunctionalTestCase {
 	private static final Logger log = Logger.getLogger( ASTParserLoadingTest.class );
 
 	private List<Long> createdAnimalIds = new ArrayList<Long>();
 	@Override
 	protected boolean isCleanupTestDataRequired() {
 		return true;
 	}
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"hql/Animal.hbm.xml",
 				"hql/FooBarCopy.hbm.xml",
 				"hql/SimpleEntityWithAssociation.hbm.xml",
 				"hql/CrazyIdFieldNames.hbm.xml",
 				"hql/Image.hbm.xml",
 				"hql/ComponentContainer.hbm.xml",
 				"hql/VariousKeywordPropertyEntity.hbm.xml",
 				"hql/Constructor.hbm.xml",
 				"batchfetch/ProductLine.hbm.xml",
 				"cid/Customer.hbm.xml",
 				"cid/Order.hbm.xml",
 				"cid/LineItem.hbm.xml",
 				"cid/Product.hbm.xml",
 				"any/Properties.hbm.xml",
 				"legacy/Commento.hbm.xml",
 				"legacy/Marelo.hbm.xml"
 		};
 	}
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] {
 				Department.class,
 				Employee.class,
 				Title.class
 		};
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.QUERY_TRANSLATOR, ASTQueryTranslatorFactory.class.getName() );
 	}
 
 	@Test
 	public void testSubSelectAsArithmeticOperand() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		// first a control
 		s.createQuery( "from Zoo z where ( select count(*) from Zoo ) = 0" ).list();
 
 		// now as operands singly:
 		s.createQuery( "from Zoo z where ( select count(*) from Zoo ) + 0 = 0" ).list();
 		s.createQuery( "from Zoo z where 0 + ( select count(*) from Zoo ) = 0" ).list();
 
 		// and doubly:
 		s.createQuery( "from Zoo z where ( select count(*) from Zoo ) + ( select count(*) from Zoo ) = 0" ).list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-8432" )
 	public void testExpandListParameter() {
 		final Object[] namesArray = new Object[] {
 				"ZOO 1", "ZOO 2", "ZOO 3", "ZOO 4", "ZOO 5", "ZOO 6", "ZOO 7",
 				"ZOO 8", "ZOO 9", "ZOO 10", "ZOO 11", "ZOO 12"
 		};
 		final Object[] citiesArray = new Object[] {
 				"City 1", "City 2", "City 3", "City 4", "City 5", "City 6", "City 7",
 				"City 8", "City 9", "City 10", "City 11", "City 12"
 		};
 
 		Session session = openSession();
 
 		session.getTransaction().begin();
 		Address address = new Address();
 		Zoo zoo = new Zoo( "ZOO 1", address );
 		address.setCity( "City 1" );
 		session.save( zoo );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		List result = session.createQuery( "FROM Zoo z WHERE z.name IN (?1) and z.address.city IN (?11)" )
 				.setParameterList( "1", namesArray )
 				.setParameterList( "11", citiesArray )
 				.list();
 		assertEquals( 1, result.size() );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		zoo = (Zoo) session.get( Zoo.class, zoo.getId() );
 		session.delete( zoo );
 		session.getTransaction().commit();
 
 		session.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8699")
 	// For now, restrict to H2.  Selecting w/ predicate functions cause issues for too many dialects.
 	@RequiresDialect(value = H2Dialect.class, jiraKey = "HHH-9052")
 	public void testBooleanPredicate() {
 		final Session session = openSession();
 
 		session.getTransaction().begin();
 		final Constructor constructor = new Constructor();
 		session.save( constructor );
 		session.getTransaction().commit();
 
 		session.clear();
 		Constructor.resetConstructorExecutionCount();
 
 		session.getTransaction().begin();
 		final Constructor result = (Constructor) session.createQuery(
 				"select new Constructor( c.id, c.id is not null, c.id = c.id, c.id + 1, concat( c.id, 'foo' ) ) from Constructor c where c.id = :id"
 		).setParameter( "id", constructor.getId() ).uniqueResult();
 		session.getTransaction().commit();
 
 		assertEquals( 1, Constructor.getConstructorExecutionCount() );
 		assertEquals( new Constructor( constructor.getId(), true, true, constructor.getId() + 1, constructor.getId() + "foo" ), result );
 
 		session.close();
 	}
 
 	@Test
 	public void testJpaTypeOperator() {
 		// just checking syntax here...
 		Session s = openSession();
 		s.beginTransaction();
 
 		///////////////////////////////////////////////////////////////
 		// where clause
 		// control
 		s.createQuery( "from Animal a where a.class = Dog" ).list();
         // test
 		s.createQuery( "from Animal a where type(a) = Dog" ).list();
 
 		///////////////////////////////////////////////////////////////
 		// select clause (at some point we should unify these)
 		// control
 		Query query = s.createQuery( "select a.class from Animal a where a.class = Dog" );
 		query.list(); // checks syntax
 		assertEquals( 1, query.getReturnTypes().length );
 		assertEquals( Integer.class, query.getReturnTypes()[0].getReturnedClass() ); // always integer for joined
         // test
 		query = s.createQuery( "select type(a) from Animal a where type(a) = Dog" );
 		query.list(); // checks syntax
 		assertEquals( 1, query.getReturnTypes().length );
 		assertEquals( DiscriminatorType.class, query.getReturnTypes()[0].getClass() );
 		assertEquals( Class.class, query.getReturnTypes()[0].getReturnedClass() );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentJoins() {
 		Session s = openSession();
 		s.beginTransaction();
 		ComponentContainer root = new ComponentContainer(
 				new ComponentContainer.Address(
 						"123 Main",
 						"Anywhere",
 						"USA",
 						new ComponentContainer.Address.Zip( 12345, 6789 )
 				)
 		);
 		s.save( root );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List result = s.createQuery( "select a from ComponentContainer c join c.address a" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( ComponentContainer.Address.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select a.zip from ComponentContainer c join c.address a" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( ComponentContainer.Address.Zip.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select z from ComponentContainer c join c.address a join a.zip z" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( ComponentContainer.Address.Zip.class.isInstance( result.get( 0 ) ) );
 
 		result = s.createQuery( "select z.code from ComponentContainer c join c.address a join a.zip z" ).list();
 		assertEquals( 1, result.size() );
 		assertTrue( Integer.class.isInstance( result.get( 0 ) ) );
 		s.delete( root );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
+	@TestForIssue( jiraKey = "HHH-9642")
+	public void testLazyAssociationInComponent() {
+		Session session = openSession();
+		session.getTransaction().begin();
+
+		Address address = new Address();
+		Zoo zoo = new Zoo( "ZOO 1", address );
+		address.setCity( "City 1" );
+		StateProvince stateProvince = new StateProvince();
+		stateProvince.setName( "Illinois" );
+		session.save( stateProvince );
+		address.setStateProvince( stateProvince );
+		session.save( zoo );
+
+		session.getTransaction().commit();
+		session.close();
+
+		session = openSession();
+		session.getTransaction().begin();
+
+		zoo = (Zoo) session.createQuery( "from Zoo z" ).uniqueResult();
+		assertNotNull( zoo );
+		assertNotNull( zoo.getAddress() );
+		assertEquals( "City 1", zoo.getAddress().getCity() );
+		assertFalse( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
+		assertEquals( "Illinois", zoo.getAddress().getStateProvince().getName() );
+		assertTrue( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
+
+		session.getTransaction().commit();
+		session.close();
+
+
+		session = openSession();
+		session.getTransaction().begin();
+
+		zoo = (Zoo) session.createQuery( "from Zoo z join fetch z.address.stateProvince" ).uniqueResult();
+		assertNotNull( zoo );
+		assertNotNull( zoo.getAddress() );
+		assertEquals( "City 1", zoo.getAddress().getCity() );
+		assertTrue( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
+		assertEquals( "Illinois", zoo.getAddress().getStateProvince().getName() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		session = openSession();
+		session.getTransaction().begin();
+
+		zoo = (Zoo) session.createQuery( "from Zoo z join fetch z.address a join fetch a.stateProvince" ).uniqueResult();
+		assertNotNull( zoo );
+		assertNotNull( zoo.getAddress() );
+		assertEquals( "City 1", zoo.getAddress().getCity() );
+		assertTrue( Hibernate.isInitialized( zoo.getAddress().getStateProvince() ) );
+		assertEquals( "Illinois", zoo.getAddress().getStateProvince().getName() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		session = openSession();
+		session.getTransaction().begin();
+
+		zoo.getAddress().setStateProvince( null );
+		session.delete( stateProvince );
+		session.delete( zoo );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
 	public void testJPAQLQualifiedIdentificationVariablesControl() {
 		// just checking syntax here...
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from VariousKeywordPropertyEntity where type = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity where value = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity where key = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity where entry = 'something'" ).list();
 
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.type = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.value = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.key = 'something'" ).list();
 		s.createQuery( "from VariousKeywordPropertyEntity e where e.entry = 'something'" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJPAQLMapKeyQualifier() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human me = new Human();
 		me.setName( new Name( "Steve", null, "Ebersole" ) );
 		Human joe = new Human();
 		me.setName( new Name( "Joe", null, "Ebersole" ) );
 		me.setFamily( new HashMap() );
 		me.getFamily().put( "son", joe );
 		s.save( me );
 		s.save( joe );
 		s.getTransaction().commit();
 		s.close();
 
 		// in SELECT clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select distinct key(h.family) from Human h" ).list();
 			assertEquals( 1, results.size() );
 			Object key = results.get(0);
 			assertTrue( String.class.isAssignableFrom( key.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select distinct KEY(f) from Human h join h.family f" ).list();
 			assertEquals( 1, results.size() );
 			Object key = results.get(0);
 			assertTrue( String.class.isAssignableFrom( key.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// in WHERE clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h where KEY(h.family) = 'son'" ).uniqueResult();
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h join h.family f where key(f) = 'son'" ).uniqueResult();
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( me );
 		s.delete( joe );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJPAQLMapEntryQualifier() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human me = new Human();
 		me.setName( new Name( "Steve", null, "Ebersole" ) );
 		Human joe = new Human();
 		me.setName( new Name( "Joe", null, "Ebersole" ) );
 		me.setFamily( new HashMap() );
 		me.getFamily().put( "son", joe );
 		s.save( me );
 		s.save( joe );
 		s.getTransaction().commit();
 		s.close();
 
 		// in SELECT clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select entry(h.family) from Human h" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Map.Entry.class.isAssignableFrom( result.getClass() ) );
 			Map.Entry entry = (Map.Entry) result;
 			assertTrue( String.class.isAssignableFrom( entry.getKey().getClass() ) );
 			assertTrue( Human.class.isAssignableFrom( entry.getValue().getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select ENTRY(f) from Human h join h.family f" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Map.Entry.class.isAssignableFrom( result.getClass() ) );
 			Map.Entry entry = (Map.Entry) result;
 			assertTrue( String.class.isAssignableFrom( entry.getKey().getClass() ) );
 			assertTrue( Human.class.isAssignableFrom( entry.getValue().getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// not exactly sure of the syntax of ENTRY in the WHERE clause...
 
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( me );
 		s.delete( joe );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testJPAQLMapValueQualifier() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human me = new Human();
 		me.setName( new Name( "Steve", null, "Ebersole" ) );
 		Human joe = new Human();
 		me.setName( new Name( "Joe", null, "Ebersole" ) );
 		me.setFamily( new HashMap() );
 		me.getFamily().put( "son", joe );
 		s.save( me );
 		s.save( joe );
 		s.getTransaction().commit();
 		s.close();
 
 		// in SELECT clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select value(h.family) from Human h" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Human.class.isAssignableFrom( result.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			List results = s.createQuery( "select VALUE(f) from Human h join h.family f" ).list();
 			assertEquals( 1, results.size() );
 			Object result = results.get(0);
 			assertTrue( Human.class.isAssignableFrom( result.getClass() ) );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		// in WHERE clause
 		{
 			// hibernate-only form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h where VALUE(h.family) = :joe" ).setParameter( "joe", joe ).uniqueResult();
 			// ACTUALLY EXACTLY THE SAME AS:
 			// select count(*) from Human h where h.family = :joe
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		{
 			// jpa form
 			s = openSession();
 			s.beginTransaction();
 			Long count = (Long) s.createQuery( "select count(*) from Human h join h.family f where value(f) = :joe" ).setParameter( "joe", joe ).uniqueResult();
 			// ACTUALLY EXACTLY THE SAME AS:
 			// select count(*) from Human h join h.family f where f = :joe
 			assertEquals( (Long)1L, count );
 			s.getTransaction().commit();
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( me );
 		s.delete( joe );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SkipForDialect(
 			value = IngresDialect.class,
 			jiraKey = "HHH-4961",
 			comment = "Ingres does not support this scoping in 9.3"
 	)
 	public void testPaginationWithPolymorphicQuery() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Steve", null, "Ebersole" ) );
 		s.save( h );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from java.lang.Object" ).setMaxResults( 2 ).list();
 		assertEquals( 1, results.size() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( h );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2045" )
 	@RequiresDialect( H2Dialect.class )
 	public void testEmptyInList() {
 		Session session = openSession();
 		session.beginTransaction();
 		Human human = new Human();
 		human.setName( new Name( "Lukasz", null, "Antoniak" ) );
 		human.setNickName( "NONE" );
 		session.save( human );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		List results = session.createQuery( "from Human h where h.nickName in ()" ).list();
 		assertEquals( 0, results.size() );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		session.delete( human );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testComponentNullnessChecks() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Johnny", 'B', "Goode" ) );
 		s.save( h );
 		h = new Human();
 		h.setName( new Name( "Steve", null, "Ebersole" ) );
 		s.save( h );
 		h = new Human();
 		h.setName( new Name( "Bono", null, null ) );
 		s.save( h );
 		h = new Human();
 		h.setName( new Name( null, null, null ) );
 		s.save( h );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from Human where name is null" ).list();
 		assertEquals( 1, results.size() );
 		results = s.createQuery( "from Human where name is not null" ).list();
 		assertEquals( 3, results.size() );
 		String query =
 				( getDialect() instanceof DB2Dialect || getDialect() instanceof HSQLDialect ) ?
 						"from Human where cast(? as string) is null" :
 						"from Human where ? is null"
 				;
 		s.createQuery( query ).setParameter( 0, null ).list();
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete Human" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-4150" )
 	public void testSelectClauseCaseWithSum() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Human h1 = new Human();
 		h1.setBodyWeight( 74.0f );
 		h1.setDescription( "Me" );
 		s.persist( h1 );
 
 		Human h2 = new Human();
 		h2.setBodyWeight( 125.0f );
 		h2.setDescription( "big persion #1" );
 		s.persist( h2 );
 
 		Human h3 = new Human();
 		h3.setBodyWeight( 110.0f );
 		h3.setDescription( "big persion #2" );
 		s.persist( h3 );
 
 		s.flush();
 
 		Number count = (Number) s.createQuery( "select sum(case when bodyWeight > 100 then 1 else 0 end) from Human" ).uniqueResult();
 		assertEquals( 2, count.intValue() );
 		count = (Number) s.createQuery( "select sum(case when bodyWeight > 100 then bodyWeight else 0 end) from Human" ).uniqueResult();
 		assertEquals( h2.getBodyWeight() + h3.getBodyWeight(), count.floatValue(), 0.001 );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-4150" )
 	public void testSelectClauseCaseWithCountDistinct() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Human h1 = new Human();
 		h1.setBodyWeight( 74.0f );
 		h1.setDescription( "Me" );
 		h1.setNickName( "Oney" );
 		s.persist( h1 );
 
 		Human h2 = new Human();
 		h2.setBodyWeight( 125.0f );
 		h2.setDescription( "big persion" );
 		h2.setNickName( "big #1" );
 		s.persist( h2 );
 
 		Human h3 = new Human();
 		h3.setBodyWeight( 110.0f );
 		h3.setDescription( "big persion" );
 		h3.setNickName( "big #2" );
 		s.persist( h3 );
 
 		s.flush();
 
 		Number count = (Number) s.createQuery( "select count(distinct case when bodyWeight > 100 then description else null end) from Human" ).uniqueResult();
 		assertEquals( 1, count.intValue() );
 		count = (Number) s.createQuery( "select count(case when bodyWeight > 100 then description else null end) from Human" ).uniqueResult();
 		assertEquals( 2, count.intValue() );
 		count = (Number) s.createQuery( "select count(distinct case when bodyWeight > 100 then nickName else null end) from Human" ).uniqueResult();
 		assertEquals( 2, count.intValue() );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testInvalidCollectionDereferencesFail() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		// control group...
 		s.createQuery( "from Animal a join a.offspring o where o.description = 'xyz'" ).list();
 		s.createQuery( "from Animal a join a.offspring o where o.father.description = 'xyz'" ).list();
 		s.createQuery( "from Animal a join a.offspring o order by o.description" ).list();
 		s.createQuery( "from Animal a join a.offspring o order by o.father.description" ).list();
 
 		try {
 			s.createQuery( "from Animal a where a.offspring.description = 'xyz'" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a where a.offspring.father.description = 'xyz'" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a order by a.offspring.description" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a order by a.offspring.father.description" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             log.trace("expected failure...", qe);
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testConcatenation() {
 		// simple syntax checking...
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Human h where h.nickName = '1' || 'ov' || 'tha' || 'few'" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testExpressionWithParamInFunction() {
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal a where abs(a.bodyWeight-:param) < 2.0" ).setLong( "param", 1 ).list();
 		s.createQuery( "from Animal a where abs(:param - a.bodyWeight) < 2.0" ).setLong( "param", 1 ).list();
 		if ( ( getDialect() instanceof HSQLDialect ) || ( getDialect() instanceof DB2Dialect ) ) {
 			// HSQLDB and DB2 don't like the abs(? - ?) syntax. bit work if at least one parameter is typed...
 			s.createQuery( "from Animal where abs(cast(:x as long) - :y) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 			s.createQuery( "from Animal where abs(:x - cast(:y as long)) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 			s.createQuery( "from Animal where abs(cast(:x as long) - cast(:y as long)) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 		}
 		else {
 			s.createQuery( "from Animal where abs(:x - :y) < 2.0" ).setLong( "x", 1 ).setLong( "y", 1 ).list();
 		}
 
 		if ( getDialect() instanceof DB2Dialect ) {
 			s.createQuery( "from Animal where lower(upper(cast(:foo as string))) like 'f%'" ).setString( "foo", "foo" ).list();
 		}
 		else {
 			s.createQuery( "from Animal where lower(upper(:foo)) like 'f%'" ).setString( "foo", "foo" ).list();
 		}
 		s.createQuery( "from Animal a where abs(abs(a.bodyWeight - 1.0 + :param) * abs(length('ffobar')-3)) = 3.0" ).setLong(
 				"param", 1
 		).list();
 		if ( getDialect() instanceof DB2Dialect ) {
 			s.createQuery( "from Animal where lower(upper('foo') || upper(cast(:bar as string))) like 'f%'" ).setString( "bar", "xyz" ).list();
 		}
 		else {
 			s.createQuery( "from Animal where lower(upper('foo') || upper(:bar)) like 'f%'" ).setString( "bar", "xyz" ).list();
 		}
 		
 		if ( getDialect() instanceof AbstractHANADialect ) {
 			s.createQuery( "from Animal where abs(cast(1 as double) - cast(:param as double)) = 1.0" )
 					.setLong( "param", 1 ).list();
 		}
 		else if ( !( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof PostgreSQL81Dialect
 				|| getDialect() instanceof MySQLDialect ) ) {
 			s.createQuery( "from Animal where abs(cast(1 as float) - cast(:param as float)) = 1.0" )
 					.setLong( "param", 1 ).list();
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCrazyIdFieldNames() {
 		MoreCrazyIdFieldNameStuffEntity top = new MoreCrazyIdFieldNameStuffEntity( "top" );
 		HeresAnotherCrazyIdFieldName next = new HeresAnotherCrazyIdFieldName( "next" );
 		top.setHeresAnotherCrazyIdFieldName( next );
 		MoreCrazyIdFieldNameStuffEntity other = new MoreCrazyIdFieldNameStuffEntity( "other" );
 		Session s = openSession();
 		s.beginTransaction();
 		s.save( next );
 		s.save( top );
 		s.save( other );
 		s.flush();
 
 		List results = s.createQuery( "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null" ).list();
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertClassAssignability( HeresAnotherCrazyIdFieldName.class, result.getClass() );
 		assertSame( next, result );
 
 		results = s.createQuery( "select e.heresAnotherCrazyIdFieldName.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null" ).list();
 		assertEquals( 1, results.size() );
 		result = results.get( 0 );
 		assertClassAssignability( Long.class, result.getClass() );
 		assertEquals( next.getHeresAnotherCrazyIdFieldName(), result );
 
 		results = s.createQuery( "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e" ).list();
 		assertEquals( 1, results.size() );
 		Iterator itr = s.createQuery( "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e" ).iterate();
 		assertTrue( itr.hasNext() ); itr.next(); assertFalse( itr.hasNext() );
 
 		s.delete( top );
 		s.delete( next );
 		s.delete( other );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2257" )
 	public void testImplicitJoinsInDifferentClauses() {
 		// both the classic and ast translators output the same syntactically valid sql
 		// for all of these cases; the issue is that shallow (iterate) and
 		// non-shallow (list/scroll) queries return different results because the
 		// shallow skips the inner join which "weeds out" results from the non-shallow queries.
 		// The results were initially different depending upon the clause(s) in which the
 		// implicit join occurred
 		Session s = openSession();
 		s.beginTransaction();
 		SimpleEntityWithAssociation owner = new SimpleEntityWithAssociation( "owner" );
 		SimpleAssociatedEntity e1 = new SimpleAssociatedEntity( "thing one", owner );
 		SimpleAssociatedEntity e2 = new SimpleAssociatedEntity( "thing two" );
 		s.save( e1 );
 		s.save( e2 );
 		s.save( owner );
 		s.getTransaction().commit();
 		s.close();
 
 		checkCounts( "select e.owner from SimpleAssociatedEntity e", 1, "implicit-join in select clause" );
 		checkCounts( "select e.id, e.owner from SimpleAssociatedEntity e", 1, "implicit-join in select clause" );
 
 		// resolved to a "id short cut" when part of the order by clause -> no inner join = no weeding out...
 		checkCounts( "from SimpleAssociatedEntity e order by e.owner", 2, "implicit-join in order-by clause" );
 		// resolved to a "id short cut" when part of the group by clause -> no inner join = no weeding out...
 		checkCounts( "select e.owner.id, count(*) from SimpleAssociatedEntity e group by e.owner", 2, "implicit-join in select and group-by clauses" );
 
 	 	s = openSession();
 		s.beginTransaction();
 		s.delete( e1 );
 		s.delete( e2 );
 		s.delete( owner );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRowValueConstructorSyntaxInInList() {
 		Session s = openSession();
 		s.beginTransaction();
 		Product product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "4321" );
 		s.save( product );
 
 
 		Customer customer = new Customer();
 		customer.setCustomerId( "123456789" );
 		customer.setName( "My customer" );
 		customer.setAddress( "somewhere" );
 		s.save( customer );
 
 		Order order = customer.generateNewOrder( new BigDecimal( 1234 ) );
 		s.save( order );
 
 		LineItem li = order.generateLineItem( product, 5 );
 		s.save( li );
 		product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "1234" );
 		s.save( product );
 		li = order.generateLineItem( product, 10 );
 		s.save( li );
 
 		s.flush();
 		Query query = s.createQuery( "from LineItem l where l.id in (:idList)" );
 		List<Id> list = new ArrayList<Id>();
 		list.add( new Id( "123456789", order.getId().getOrderNumber(), "4321" ) );
 		list.add( new Id( "123456789", order.getId().getOrderNumber(), "1234" ) );
 		query.setParameterList( "idList", list );
 		assertEquals( 2, query.list().size() );
 
 		query = s.createQuery( "from LineItem l where l.id in :idList" );
 		query.setParameterList( "idList", list );
 		assertEquals( 2, query.list().size() );
 
 		s.getTransaction().rollback();
 		s.close();
 
 	}
 
 	private void checkCounts(String hql, int expected, String testCondition) {
 		Session s = openSession();
 		s.beginTransaction();
 		int count = determineCount( s.createQuery( hql ).list().iterator() );
 		assertEquals( "list() [" + testCondition + "]", expected, count );
 		count = determineCount( s.createQuery( hql ).iterate() );
 		assertEquals( "iterate() [" + testCondition + "]", expected, count );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2257" )
 	public void testImplicitSelectEntityAssociationInShallowQuery() {
 		// both the classic and ast translators output the same syntactically valid sql.
 		// the issue is that shallow and non-shallow queries return different
 		// results because the shallow skips the inner join which "weeds out" results
 		// from the non-shallow queries...
 		Session s = openSession();
 		s.beginTransaction();
 		SimpleEntityWithAssociation owner = new SimpleEntityWithAssociation( "owner" );
 		SimpleAssociatedEntity e1 = new SimpleAssociatedEntity( "thing one", owner );
 		SimpleAssociatedEntity e2 = new SimpleAssociatedEntity( "thing two" );
 		s.save( e1 );
 		s.save( e2 );
 		s.save( owner );
 		s.getTransaction().commit();
 		s.close();
 
 	 	s = openSession();
 		s.beginTransaction();
 		int count = determineCount( s.createQuery( "select e.id, e.owner from SimpleAssociatedEntity e" ).list().iterator() );
 		assertEquals( 1, count ); // thing two would be removed from the result due to the inner join
 		count = determineCount( s.createQuery( "select e.id, e.owner from SimpleAssociatedEntity e" ).iterate() );
 		assertEquals( 1, count );
 		s.getTransaction().commit();
 		s.close();
 
 	 	s = openSession();
 		s.beginTransaction();
 		s.delete( e1 );
 		s.delete( e2 );
 		s.delete( owner );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private int determineCount(Iterator iterator) {
 		int count = 0;
 		while( iterator.hasNext() ) {
 			count++;
 			iterator.next();
 		}
 		return count;
 	}
 
     @Test
     @TestForIssue( jiraKey = "HHH-6714" )
     public void testUnaryMinus(){
         Session s = openSession();
         s.beginTransaction();
         Human stliu = new Human();
         stliu.setIntValue( 26 );
 
         s.persist( stliu );
         s.getTransaction().commit();
         s.clear();
         s.beginTransaction();
         List list =s.createQuery( "from Human h where -(h.intValue - 100)=74" ).list();
         assertEquals( 1, list.size() );
         s.getTransaction().commit();
         s.close();
 
 
     }
 
 	@Test
 	public void testEntityAndOneToOneReturnedByQuery() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Gail", null, "Badner" ) );
 		s.save( h );
 		User u = new User();
 		u.setUserName( "gbadner" );
 		u.setHuman( h );
 		s.save( u );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Object [] result = ( Object [] ) s.createQuery( "from User u, Human h where u.human = h" ).uniqueResult();
 		assertNotNull( result );
 		assertEquals( u.getUserName(), ( ( User ) result[ 0 ] ).getUserName() );
 		assertEquals( h.getName().getFirst(), ((Human) result[1]).getName().getFirst() );
 		assertSame( ((User) result[0]).getHuman(), result[1] );
 		s.createQuery( "delete User" ).executeUpdate();
 		s.createQuery( "delete Human" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9305")
 	public void testExplicitToOneInnerJoin() {
 		final Employee employee1 = new Employee();
 		employee1.setFirstName( "Jane" );
 		employee1.setLastName( "Doe" );
 		final Title title1 = new Title();
 		title1.setDescription( "Jane's description" );
 		final Department dept1 = new Department();
 		dept1.setDeptName( "Jane's department" );
 		employee1.setTitle( title1 );
 		employee1.setDepartment( dept1 );
 
 		final Employee employee2 = new Employee();
 		employee2.setFirstName( "John" );
 		employee2.setLastName( "Doe" );
 		final Title title2 = new Title();
 		title2.setDescription( "John's title" );
 		employee2.setTitle( title2 );
 
 		Session s = openSession();
 		s.getTransaction().begin();
 		s.persist( title1 );
 		s.persist( dept1 );
 		s.persist( employee1 );
 		s.persist( title2 );
 		s.persist( employee2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 		Department department = (Department) s.createQuery( "select e.department from Employee e inner join e.department" ).uniqueResult();
 		assertEquals( employee1.getDepartment().getDeptName(), department.getDeptName() );
 		s.delete( employee1 );
 		s.delete( title1 );
 		s.delete( department );
 		s.delete( employee2 );
 		s.delete( title2 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testExplicitToOneOuterJoin() {
 		final Employee employee1 = new Employee();
 		employee1.setFirstName( "Jane" );
 		employee1.setLastName( "Doe" );
 		final Title title1 = new Title();
 		title1.setDescription( "Jane's description" );
 		final Department dept1 = new Department();
 		dept1.setDeptName( "Jane's department" );
 		employee1.setTitle( title1 );
 		employee1.setDepartment( dept1 );
 
 		final Employee employee2 = new Employee();
 		employee2.setFirstName( "John" );
 		employee2.setLastName( "Doe" );
 		final Title title2 = new Title();
 		title2.setDescription( "John's title" );
 		employee2.setTitle( title2 );
 
 		Session s = openSession();
 		s.getTransaction().begin();
 		s.persist( title1 );
 		s.persist( dept1 );
 		s.persist( employee1 );
 		s.persist( title2 );
 		s.persist( employee2 );
 		s.getTransaction().commit();
 		s.close();
 		s = openSession();
 		s.getTransaction().begin();
 		List list = s.createQuery( "select e.department from Employee e left join e.department" ).list();
 		assertEquals( 2, list.size() );
 		final Department dept;
 		if ( list.get( 0 ) == null ) {
 			dept = (Department) list.get( 1 );
 		}
 		else {
 			dept = (Department) list.get( 0 );
 			assertNull( list.get( 1 ) );
 		}
 		assertEquals( dept1.getDeptName(), dept.getDeptName() );
 		s.delete( employee1 );
 		s.delete( title1 );
 		s.delete( dept );
 		s.delete( employee2 );
 		s.delete( title2 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testExplicitToOneInnerJoinAndImplicitToOne() {
 		final Employee employee1 = new Employee();
 		employee1.setFirstName( "Jane" );
 		employee1.setLastName( "Doe" );
 		final Title title1 = new Title();
 		title1.setDescription( "Jane's description" );
 		final Department dept1 = new Department();
 		dept1.setDeptName( "Jane's department" );
 		employee1.setTitle( title1 );
 		employee1.setDepartment( dept1 );
 
 		final Employee employee2 = new Employee();
 		employee2.setFirstName( "John" );
 		employee2.setLastName( "Doe" );
 		final Title title2 = new Title();
 		title2.setDescription( "John's title" );
 		employee2.setTitle( title2 );
 
 		Session s = openSession();
 		s.getTransaction().begin();
 		s.persist( title1 );
 		s.persist( dept1 );
 		s.persist( employee1 );
 		s.persist( title2 );
 		s.persist( employee2 );
 		s.getTransaction().commit();
 		s.close();
 		s = openSession();
 		s.getTransaction().begin();
 		Object[] result = (Object[]) s.createQuery(
 				"select e.firstName, e.lastName, e.title.description, e.department from Employee e inner join e.department"
 		).uniqueResult();
 		assertEquals( employee1.getFirstName(), result[0] );
 		assertEquals( employee1.getLastName(), result[1] );
 		assertEquals( employee1.getTitle().getDescription(), result[2] );
 		assertEquals( employee1.getDepartment().getDeptName(), ( (Department) result[3] ).getDeptName() );
 		s.delete( employee1 );
 		s.delete( title1 );
 		s.delete( result[3] );
 		s.delete( employee2 );
 		s.delete( title2 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNestedComponentIsNull() {
 		// (1) From MapTest originally...
 		// (2) Was then moved into HQLTest...
 		// (3) However, a bug fix to EntityType#getIdentifierOrUniqueKeyType (HHH-2138)
 		// 		caused the classic parser to suddenly start throwing exceptions on
 		//		this query, apparently relying on the buggy behavior somehow; thus
 		//		moved here to at least get some syntax checking...
 		//
 		// fyi... found and fixed the problem in the classic parser; still
 		// leaving here for syntax checking
 		new SyntaxChecker( "from Commento c where c.marelo.commento.mcompr is null" ).checkAll();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-939" )
 	public void testSpecialClassPropertyReference() {
 		// this is a long standing bug in Hibernate when applied to joined-subclasses;
 		//  see HHH-939 for details and history
 		new SyntaxChecker( "from Zoo zoo where zoo.class = PettingZoo" ).checkAll();
 		new SyntaxChecker( "select a.description from Animal a where a.class = Mammal" ).checkAll();
 		new SyntaxChecker( "select a.class from Animal a" ).checkAll();
 		new SyntaxChecker( "from DomesticAnimal an where an.class = Dog" ).checkAll();
 		new SyntaxChecker( "from Animal an where an.class = Dog" ).checkAll();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2376" )
 	public void testSpecialClassPropertyReferenceFQN() {
 		new SyntaxChecker( "from Zoo zoo where zoo.class = org.hibernate.test.hql.PettingZoo" ).checkAll();
 		new SyntaxChecker( "select a.description from Animal a where a.class = org.hibernate.test.hql.Mammal" ).checkAll();
 		new SyntaxChecker( "from DomesticAnimal an where an.class = org.hibernate.test.hql.Dog" ).checkAll();
 		new SyntaxChecker( "from Animal an where an.class = org.hibernate.test.hql.Dog" ).checkAll();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1631" )
 	public void testSubclassOrSuperclassPropertyReferenceInJoinedSubclass() {
 		// this is a long standing bug in Hibernate; see HHH-1631 for details and history
 		//
 		// (1) pregnant is defined as a property of the class (Mammal) itself
 		// (2) description is defined as a property of the superclass (Animal)
 		// (3) name is defined as a property of a particular subclass (Human)
 
 		new SyntaxChecker( "from Zoo z join z.mammals as m where m.name.first = 'John'" ).checkIterate();
 
 		new SyntaxChecker( "from Zoo z join z.mammals as m where m.pregnant = false" ).checkAll();
 		new SyntaxChecker( "select m.pregnant from Zoo z join z.mammals as m where m.pregnant = false" ).checkAll();
 
 		new SyntaxChecker( "from Zoo z join z.mammals as m where m.description = 'tabby'" ).checkAll();
 		new SyntaxChecker( "select m.description from Zoo z join z.mammals as m where m.description = 'tabby'" ).checkAll();
 
 		new SyntaxChecker( "from Zoo z join z.mammals as m where m.name.first = 'John'" ).checkAll();
 		new SyntaxChecker( "select m.name from Zoo z join z.mammals as m where m.name.first = 'John'" ).checkAll();
 
 		new SyntaxChecker( "select m.pregnant from Zoo z join z.mammals as m" ).checkAll();
 		new SyntaxChecker( "select m.description from Zoo z join z.mammals as m" ).checkAll();
 		new SyntaxChecker( "select m.name from Zoo z join z.mammals as m" ).checkAll();
 
 		new SyntaxChecker( "from DomesticAnimal da join da.owner as o where o.nickName = 'Gavin'" ).checkAll();
 		new SyntaxChecker( "select da.father from DomesticAnimal da join da.owner as o where o.nickName = 'Gavin'" ).checkAll();
 		new SyntaxChecker( "select da.father from DomesticAnimal da where da.owner.nickName = 'Gavin'" ).checkAll();
 	}
 
 	/**
 	 * {@link #testSubclassOrSuperclassPropertyReferenceInJoinedSubclass} tests the implicit form of entity casting
 	 * that Hibernate has always supported.  THis method tests the explicit variety added by JPA 2.1 using the TREAT
 	 * keyword.
 	 */
 	@Test
 	public void testExplicitEntityCasting() {
 		new SyntaxChecker( "from Zoo z join treat(z.mammals as Human) as m where m.name.first = 'John'" ).checkIterate();
 		new SyntaxChecker( "from Zoo z join z.mammals as m where treat(m as Human).name.first = 'John'" ).checkIterate();
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportLimitAndOffsetCheck.class,
 			comment = "dialect does not support offset and limit combo"
 	)
 	public void testSimpleSelectWithLimitAndOffset() throws Exception {
 		// just checking correctness of param binding code...
 		Session session = openSession();
 		session.createQuery( "from Animal" )
 				.setFirstResult( 2 )
 				.setMaxResults( 1 )
 				.list();
 		session.close();
 	}
 
 	@Test
 	public void testJPAPositionalParameterList() {
 		Session s = openSession();
 		s.beginTransaction();
 		ArrayList<String> params = new ArrayList<String>();
 		params.add( "Doe" );
 		params.add( "Public" );
 		s.createQuery( "from Human where name.last in (?1)" )
 				.setParameterList( "1", params )
 				.list();
 
 		s.createQuery( "from Human where name.last in ?1" )
 				.setParameterList( "1", params )
 				.list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentQueries() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		Type[] types = s.createQuery( "select h.name from Human h" ).getReturnTypes();
 		assertEquals( 1, types.length );
 		assertTrue( types[0] instanceof ComponentType );
 
 		// Test the ability to perform comparisons between component values
 		s.createQuery( "from Human h where h.name = h.name" ).list();
 		s.createQuery( "from Human h where h.name = :name" ).setParameter( "name", new Name() ).list();
 		s.createQuery( "from Human where name = :name" ).setParameter( "name", new Name() ).list();
 		s.createQuery( "from Human h where :name = h.name" ).setParameter( "name", new Name() ).list();
 		s.createQuery( "from Human h where :name <> h.name" ).setParameter( "name", new Name() ).list();
 
 		// Test the ability to perform comparisons between a component and an explicit row-value
 		s.createQuery( "from Human h where h.name = ('John', 'X', 'Doe')" ).list();
 		s.createQuery( "from Human h where ('John', 'X', 'Doe') = h.name" ).list();
 		s.createQuery( "from Human h where ('John', 'X', 'Doe') <> h.name" ).list();
 
 		// HANA only allows '=' and '<>'/'!='
 		if ( ! ( getDialect() instanceof AbstractHANADialect ) ) {
 			s.createQuery( "from Human h where ('John', 'X', 'Doe') >= h.name" ).list();
 		}
 
 		s.createQuery( "from Human h order by h.name" ).list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-1774" )
 	@SkipForDialect(
 			value = IngresDialect.class,
 			comment = "Subselects are not supported within select target lists in Ingres",
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/Alias.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/Alias.java
new file mode 100644
index 0000000000..327b5667f2
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/Alias.java
@@ -0,0 +1,77 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test.criteria.components;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+
+/**
+ * TODO : javadoc
+ *
+ * @author Steve Ebersole
+ */
+@Entity
+public class Alias {
+	private Long id;
+	private Name name;
+	private String source;
+
+	public Alias() {
+	}
+
+	public Alias(String firstName, String lastName, String source) {
+		this( new Name( firstName, lastName ), source );
+	}
+
+	public Alias(Name name, String source) {
+		this.name = name;
+		this.source = source;
+	}
+
+	@Id @GeneratedValue
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public Name getName() {
+		return name;
+	}
+
+	public void setName(Name name) {
+		this.name = name;
+	}
+
+	public String getSource() {
+		return source;
+	}
+
+	public void setSource(String source) {
+		this.source = source;
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/ComponentCriteriaTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/ComponentCriteriaTest.java
index 791ce87ad7..32ddff5254 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/ComponentCriteriaTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/ComponentCriteriaTest.java
@@ -1,106 +1,173 @@
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
 package org.hibernate.jpa.test.criteria.components;
 
 import java.util.List;
+import java.util.Set;
 import javax.persistence.EntityManager;
+import javax.persistence.Query;
 import javax.persistence.TypedQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaQuery;
+import javax.persistence.criteria.Fetch;
 import javax.persistence.criteria.Root;
 
 import org.junit.Assert;
 import org.junit.Test;
 
+import org.hibernate.Hibernate;
 import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
 
 import org.hibernate.testing.TestForIssue;
 
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+
 /**
  * @author alan.oleary
  */
 public class ComponentCriteriaTest extends BaseEntityManagerFunctionalTestCase {
 	@Override
 	public Class[] getAnnotatedClasses() {
-		return new Class[] { Client.class };
+		return new Class[] { Client.class, Alias.class };
 	}
 
 	@Test
 	public void testEmbeddableInPath() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Client client = new Client( 111, "steve", "ebersole" );
 		em.persist(client);
 		em.getTransaction().commit();
 		em.close();
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaBuilder cb = em.getCriteriaBuilder();
 		CriteriaQuery<Client> cq = cb.createQuery(Client.class);
 		Root<Client> root = cq.from(Client.class);
 		cq.where(cb.equal(root.get("name").get("firstName"), client.getName().getFirstName()));
 		List<Client> list = em.createQuery(cq).getResultList();
 		Assert.assertEquals( 1, list.size() );
 		em.getTransaction().commit();
 		em.close();
 		
 		// HHH-5792
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		TypedQuery< Client > q = em.createQuery(
 				"SELECT c FROM Client c JOIN c.name n WHERE n.firstName = '"
 						+ client.getName().getFirstName() + "'",
                  Client.class );
 		Assert.assertEquals( 1, q.getResultList().size() );
 		em.getTransaction().commit();
 		em.close();
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.createQuery( "delete Client" ).executeUpdate();
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
+	@TestForIssue( jiraKey = "HHH-9642")
+	public void testOneToManyJoinFetchedInEmbeddable() {
+		EntityManager em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		Client client = new Client( 111, "steve", "ebersole" );
+		Alias alias = new Alias( "a", "guy", "work" );
+		client.getName().getAliases().add( alias );
+		em.persist(client);
+		em.getTransaction().commit();
+		em.close();
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		CriteriaBuilder cb = em.getCriteriaBuilder();
+		CriteriaQuery<Client> cq = cb.createQuery(Client.class);
+		Root<Client> root = cq.from(Client.class);
+		root.fetch( Client_.name ).fetch( Name_.aliases );
+		cq.where(cb.equal(root.get("name").get("firstName"), client.getName().getFirstName()));
+		List<Client> list = em.createQuery(cq).getResultList();
+		Assert.assertEquals( 1, list.size() );
+		client = list.get( 0 );
+		assertTrue( Hibernate.isInitialized( client.getName().getAliases() ) );
+		em.getTransaction().commit();
+		em.close();
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		TypedQuery< Client > q = em.createQuery(
+				"SELECT c FROM Client c JOIN FETCH c.name.aliases WHERE c.name.firstName = '"
+						+ client.getName().getFirstName() + "'",
+				Client.class
+		);
+		Assert.assertEquals( 1, q.getResultList().size() );
+		client = list.get( 0 );
+		assertTrue( Hibernate.isInitialized( client.getName().getAliases() ) );
+		em.getTransaction().commit();
+		em.close();
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		q = em.createQuery(
+				"SELECT c FROM Client c JOIN  c.name n join FETCH n.aliases WHERE c.name.firstName = '"
+						+ client.getName().getFirstName() + "'",
+				Client.class
+		);
+		Assert.assertEquals( 1, q.getResultList().size() );
+		client = list.get( 0 );
+		assertTrue( Hibernate.isInitialized( client.getName().getAliases() ) );
+		em.getTransaction().commit();
+		em.close();
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		client = em.merge( client );
+		em.remove( client );
+		em.getTransaction().commit();
+		em.close();
+	}
+
+	@Test
 	@TestForIssue( jiraKey = "HHH-4586" )
 	public void testParameterizedFunctions() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaBuilder cb = em.getCriteriaBuilder();
 		// lower
 		CriteriaQuery<Client> cq = cb.createQuery( Client.class );
 		Root<Client> root = cq.from( Client.class );
 		cq.where( cb.equal( cb.lower( root.get( Client_.name ).get( Name_.lastName ) ),"test" ) );
 		em.createQuery( cq ).getResultList();
 		// upper
 		cq = cb.createQuery( Client.class );
 		root = cq.from( Client.class );
 		cq.where( cb.equal( cb.upper( root.get( Client_.name ).get( Name_.lastName ) ),"test" ) );
 		em.createQuery( cq ).getResultList();
 		em.getTransaction().commit();
 		em.close();
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/Name.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/Name.java
index 7cfa826a9a..cbf143fe5b 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/Name.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/components/Name.java
@@ -1,64 +1,79 @@
 /*
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
  * Boston, MA  02110-1301  USA\
  */
 package org.hibernate.jpa.test.criteria.components;
 import java.io.Serializable;
+import java.util.HashSet;
+import java.util.Set;
+import javax.persistence.CascadeType;
 import javax.persistence.Column;
 import javax.persistence.Embeddable;
+import javax.persistence.JoinColumn;
+import javax.persistence.OneToMany;
 
 /**
  * The name component
  *
  * @author alan.oleary
  */
 @Embeddable
 public class Name implements Serializable {
     private static final long serialVersionUID = 8381969086665589013L;
 
     private String firstName;
     private String lastName;
+	private Set<Alias> aliases = new HashSet<Alias>(  );
 
 	public Name() {
 	}
 
 	public Name(String firstName, String lastName) {
 		this.firstName = firstName;
 		this.lastName = lastName;
 	}
 
 	@Column(name = "FIRST_NAME", nullable = false)
 	public String getFirstName() {
 		return firstName;
 	}
 
 	public void setFirstName(String firstName) {
 		this.firstName = firstName;
 	}
 
 	@Column(name = "LAST_NAME", nullable = false)
 	public String getLastName() {
 		return lastName;
 	}
 
 	public void setLastName(String lastName) {
 		this.lastName = lastName;
 	}
+
+	@OneToMany(cascade = CascadeType.ALL)
+	public Set<Alias> getAliases() {
+		return aliases;
+	}
+
+	public void setAliases(Set<Alias> aliases) {
+		this.aliases = aliases;
+	}
 }
\ No newline at end of file
