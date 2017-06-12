diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
index ec2c7795e1..e72c67bb42 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
@@ -1,747 +1,753 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
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
 
 	public interface IllegalCollectionDereferenceExceptionBuilder {
 		QueryException buildIllegalCollectionDereferenceException(
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
 	 * @see org.hibernate.sql.JoinFragment
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
 
 		// determine whether we should use the table name or table alias to qualify the column names...
 		// we need to use the table-name when:
 		//		1) the top-level statement is not a SELECT
 		//		2) the LHS FromElement is *the* FromElement from the top-level statement
 		//
 		// there is a caveat here.. if the update/delete statement are "multi-table" we should continue to use
 		// the alias also, even if the FromElement is the root one...
 		//
 		// in all other cases, we should use the table alias
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
 				lhsFromElement,
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
-		boolean useFoundFromElement = found && canReuse( elem );
+		boolean useFoundFromElement = found && canReuse( classAlias, elem );
 
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
 
-	private boolean canReuse(FromElement fromElement) {
+	private boolean canReuse(String classAlias, FromElement fromElement) {
 		// if the from-clauses are the same, we can be a little more aggressive in terms of what we reuse
-		if ( fromElement.getFromClause() == getWalker().getCurrentFromClause() ) {
+		if ( fromElement.getFromClause() == getWalker().getCurrentFromClause() &&
+				areSame( classAlias, fromElement.getClassAlias() )) {
 			return true;
 		}
 
 		// otherwise (subquery case) dont reuse the fromElement if we are processing the from-clause of the subquery
 		return getWalker().getCurrentClauseType() != SqlTokenTypes.FROM;
 	}
 
+	private boolean areSame(String alias1, String alias2) {
+		// again, null != null here
+		return !StringHelper.isEmpty( alias1 ) && !StringHelper.isEmpty( alias2 ) && alias1.equals( alias2 );
+	}
+
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Child.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Child.java
new file mode 100644
index 0000000000..74667248b8
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Child.java
@@ -0,0 +1,80 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import java.util.HashSet;
+import java.util.Set;
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity1")
+public class Child {
+	@Id
+	@GeneratedValue
+	private long id;
+
+	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
+	@JoinColumn
+	private Set<GrandChild> grandChildren = new HashSet<GrandChild>();
+
+	public Child() {
+	}
+
+	public Child(String value) {
+		this.value = value;
+	}
+
+	private String value;
+
+	public long getId() {
+		return id;
+	}
+
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public Set<GrandChild> getGrandChildren() {
+		return grandChildren;
+	}
+
+	public void setGrandChildren(Set<GrandChild> grandChildren) {
+		this.grandChildren = grandChildren;
+	}
+
+	public String getValue() {
+		return value;
+	}
+
+	public void setValue(String value) {
+		this.value = value;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity1.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity1.java
new file mode 100644
index 0000000000..2e9778484b
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity1.java
@@ -0,0 +1,69 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity1")
+public class Entity1 {
+	@Id
+	@GeneratedValue
+	private long id;
+
+	@ManyToOne
+	@JoinColumn(name="entity2_id", nullable = false)
+	private Entity2 entity2;
+
+	private String value;
+
+	public long getId() {
+		return id;
+	}
+
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public Entity2 getEntity2() {
+		return entity2;
+	}
+
+	public void setEntity2(Entity2 entity2) {
+		this.entity2 = entity2;
+	}
+
+	public String getValue() {
+		return value;
+	}
+
+	public void setValue(String value) {
+		this.value = value;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity2.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity2.java
new file mode 100644
index 0000000000..3edf588156
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity2.java
@@ -0,0 +1,70 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity2")
+public class Entity2 {
+	@Id
+	@GeneratedValue
+	private long id;
+
+	@ManyToOne(fetch = FetchType.LAZY)
+	@JoinColumn(name="entity3_id")
+	private Entity3 entity3;
+
+	private String value;
+
+	public long getId() {
+		return id;
+	}
+
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public Entity3 getEntity3() {
+		return entity3;
+	}
+
+	public void setEntity3(Entity3 entity3) {
+		this.entity3 = entity3;
+	}
+
+	public String getValue() {
+		return value;
+	}
+
+	public void setValue(String value) {
+		this.value = value;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity3.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity3.java
new file mode 100644
index 0000000000..54234438a1
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Entity3.java
@@ -0,0 +1,56 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity3")
+public class Entity3 {
+	@Id
+	@GeneratedValue
+	private long id;
+
+	private String value;
+
+	public long getId() {
+		return id;
+	}
+
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public String getValue() {
+		return value;
+	}
+
+	public void setValue(String value) {
+		this.value = value;
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/GrandChild.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/GrandChild.java
new file mode 100644
index 0000000000..d135c7a7b9
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/GrandChild.java
@@ -0,0 +1,65 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity1")
+public class GrandChild {
+	@Id
+	@GeneratedValue
+	private long id;
+
+	private String value;
+
+	public GrandChild() {
+	}
+
+	public GrandChild(String value) {
+		this.value = value;
+	}
+
+	public long getId() {
+		return id;
+	}
+
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public String getValue() {
+		return value;
+	}
+
+	public void setValue(String value) {
+		this.value = value;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Parent.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Parent.java
new file mode 100644
index 0000000000..5329694146
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/Parent.java
@@ -0,0 +1,80 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import java.util.HashSet;
+import java.util.Set;
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity1")
+public class Parent {
+	@Id
+	@GeneratedValue
+	private long id;
+
+	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
+	@JoinColumn
+	private Set<Child> children = new HashSet<Child>();
+
+	private String value;
+
+	public Parent() {
+	}
+
+	public Parent(String value) {
+		this.value = value;
+	}
+
+	public long getId() {
+		return id;
+	}
+
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public Set<Child> getChildren() {
+		return children;
+	}
+
+	public void setChildren(Set<Child> children) {
+		this.children = children;
+	}
+
+	public String getValue() {
+		return value;
+	}
+
+	public void setValue(String value) {
+		this.value = value;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/ToManyFetchAndJoinTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/ToManyFetchAndJoinTest.java
new file mode 100644
index 0000000000..98541f5dd3
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/ToManyFetchAndJoinTest.java
@@ -0,0 +1,203 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import java.util.Iterator;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
+
+/**
+ * @author Gail Badner
+ */
+public class ToManyFetchAndJoinTest extends BaseCoreFunctionalTestCase {
+
+	@Before
+	public void setupData() {
+		Parent p = new Parent( "p" );
+		Child c1 = new Child( "c1" );
+		GrandChild gc11 = new GrandChild( "gc11" );
+		GrandChild gc12 = new GrandChild( "gc12" );
+		p.getChildren().add( c1 );
+		c1.getGrandChildren().add( gc11 );
+		c1.getGrandChildren().add( gc12 );
+
+		Child c2 = new Child( "c2" );
+		GrandChild gc21 = new GrandChild( "gc21" );
+		GrandChild gc22 = new GrandChild( "gc22" );
+		GrandChild gc23 = new GrandChild( "gc23" );
+		p.getChildren().add( c2 );
+		c2.getGrandChildren().add( gc21 );
+		c2.getGrandChildren().add( gc22 );
+		c2.getGrandChildren().add( gc23 );
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( p );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@After
+	public void cleanupData() {
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.createQuery( "delete Parent" ).executeUpdate();
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9637")
+	public void testExplicitJoinBeforeFetchJoins() {
+
+		Session s = openSession();
+		s.getTransaction().begin();
+
+		Parent p =
+				(Parent) s.createQuery(
+						"select p from Parent p inner join p.children cRestrict inner join fetch p.children c inner join fetch c.grandChildren where cRestrict.value = 'c1'" )
+						.uniqueResult();
+
+		assertEquals( "p", p.getValue() );
+		assertTrue( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 2, p.getChildren().size() );
+		Iterator<Child> iterator = p.getChildren().iterator();
+		Child cA = iterator.next();
+		assertTrue( Hibernate.isInitialized( cA.getGrandChildren() ) );
+		if ( cA.getValue().equals( "c1" ) ) {
+			assertEquals( 2, cA.getGrandChildren().size() );
+			Child cB = iterator.next();
+			assertTrue( Hibernate.isInitialized( cB.getGrandChildren() ) );
+			assertEquals( 3, cB.getGrandChildren().size() );
+		}
+		else if ( cA.getValue().equals( "c2" ) ) {
+			assertEquals( 3, cA.getGrandChildren().size() );
+			Child cB = iterator.next();
+			assertTrue( Hibernate.isInitialized( cB.getGrandChildren() ) );
+			assertEquals( 2, cB.getGrandChildren().size() );
+		}
+		else {
+			fail( "unexpected value" );
+		}
+
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9637")
+	public void testExplicitJoinBetweenFetchJoins() {
+
+		Session s = openSession();
+		s.getTransaction().begin();
+
+		Parent p =
+				(Parent) s.createQuery(
+						"select p from Parent p inner join fetch p.children c inner join p.children cRestrict inner join fetch c.grandChildren where cRestrict.value = 'c1'" )
+						.uniqueResult();
+
+		assertEquals( "p", p.getValue() );
+		assertTrue( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 2, p.getChildren().size() );
+		Iterator<Child> iterator = p.getChildren().iterator();
+		Child cA = iterator.next();
+		assertTrue( Hibernate.isInitialized( cA.getGrandChildren() ) );
+		if ( cA.getValue().equals( "c1" ) ) {
+			assertEquals( 2, cA.getGrandChildren().size() );
+			Child cB = iterator.next();
+			assertTrue( Hibernate.isInitialized( cB.getGrandChildren() ) );
+			assertEquals( 3, cB.getGrandChildren().size() );
+		}
+		else if ( cA.getValue().equals( "c2" ) ) {
+			assertEquals( 3, cA.getGrandChildren().size() );
+			Child cB = iterator.next();
+			assertTrue( Hibernate.isInitialized( cB.getGrandChildren() ) );
+			assertEquals( 2, cB.getGrandChildren().size() );
+		}
+		else {
+			fail( "unexpected value" );
+		}
+
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9637")
+	public void testExplicitJoinAfterFetchJoins() {
+
+		Session s = openSession();
+		s.getTransaction().begin();
+
+		Parent p =
+				(Parent) s.createQuery(
+						"select p from Parent p inner join fetch p.children c inner join fetch c.grandChildren inner join p.children cRestrict where cRestrict.value = 'c1'" )
+						.uniqueResult();
+
+		assertEquals( "p", p.getValue() );
+		assertTrue( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 2, p.getChildren().size() );
+		Iterator<Child> iterator = p.getChildren().iterator();
+		Child cA = iterator.next();
+		assertTrue( Hibernate.isInitialized( cA.getGrandChildren() ) );
+		if ( cA.getValue().equals( "c1" ) ) {
+			assertEquals( 2, cA.getGrandChildren().size() );
+			Child cB = iterator.next();
+			assertTrue( Hibernate.isInitialized( cB.getGrandChildren() ) );
+			assertEquals( 3, cB.getGrandChildren().size() );
+		}
+		else if ( cA.getValue().equals( "c2" ) ) {
+			assertEquals( 3, cA.getGrandChildren().size() );
+			Child cB = iterator.next();
+			assertTrue( Hibernate.isInitialized( cB.getGrandChildren() ) );
+			assertEquals( 2, cB.getGrandChildren().size() );
+		}
+		else {
+			fail( "unexpected value" );
+		}
+
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[]{
+				Parent.class,
+				Child.class,
+				GrandChild.class
+		};
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/ToOneFetchAndJoinTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/ToOneFetchAndJoinTest.java
new file mode 100644
index 0000000000..4a6f7cbd59
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/fetchAndJoin/ToOneFetchAndJoinTest.java
@@ -0,0 +1,159 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.hql.fetchAndJoin;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Gail Badner
+ */
+public class ToOneFetchAndJoinTest extends BaseCoreFunctionalTestCase {
+
+	@Before
+	public void setupData() {
+		Entity1 e1 = new Entity1();
+		e1.setValue( "entity1" );
+		Entity2 e2 = new Entity2();
+		e2.setValue( "entity2" );
+		Entity3 e3 = new Entity3();
+		e3.setValue( "entity3" );
+
+		e1.setEntity2( e2 );
+		e2.setEntity3( e3 );
+
+		Entity2 e2a = new Entity2();
+		e2a.setValue( "entity2a" );
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( e3 );
+		s.persist( e2 );
+		s.persist( e1 );
+		s.persist( e2a );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@After
+	public void cleanupData() {
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.createQuery( "delete Entity1" ).executeUpdate();
+		s.createQuery( "delete Entity2" ).executeUpdate();
+		s.createQuery( "delete Entity3" ).executeUpdate();
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9637")
+	public void testFetchJoinsWithImplicitJoinInRestriction() {
+
+		Session s = openSession();
+		s.getTransaction().begin();
+
+		Entity1 e1Queryied =
+				(Entity1) s.createQuery(
+						"select e1 from Entity1 e1 inner join fetch e1.entity2 e2 inner join fetch e2.entity3 where e1.entity2.value = 'entity2'" )
+						.uniqueResult();
+		assertEquals( "entity1", e1Queryied.getValue() );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2() ) );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2().getEntity3() ) );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9637")
+	public void testExplicitJoinBeforeFetchJoins() {
+
+		Session s = openSession();
+		s.getTransaction().begin();
+
+		Entity1 e1Queryied =
+				(Entity1) s.createQuery(
+						"select e1 from Entity1 e1 inner join e1.entity2 e1Restrict inner join fetch e1.entity2 e2 inner join fetch e2.entity3 where e1Restrict.value = 'entity2'" )
+						.uniqueResult();
+		assertEquals( "entity1", e1Queryied.getValue() );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2() ) );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2().getEntity3() ) );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9637")
+	public void testExplicitJoinBetweenFetchJoins() {
+
+		Session s = openSession();
+		s.getTransaction().begin();
+
+		Entity1 e1Queryied =
+				(Entity1) s.createQuery(
+						"select e1 from Entity1 e1 inner join fetch e1.entity2 e2 inner join e1.entity2 e1Restrict inner join fetch e2.entity3 where e1Restrict.value = 'entity2'" )
+						.uniqueResult();
+		assertEquals( "entity1", e1Queryied.getValue() );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2() ) );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2().getEntity3() ) );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9637")
+	public void testExplicitJoinAfterFetchJoins() {
+
+		Session s = openSession();
+		s.getTransaction().begin();
+
+		Entity1 e1Queryied =
+				(Entity1) s.createQuery(
+						"select e1 from Entity1 e1 inner join fetch e1.entity2 e2 inner join fetch e2.entity3 inner join e1.entity2 e1Restrict where e1Restrict.value = 'entity2'" )
+						.uniqueResult();
+		assertEquals( "entity1", e1Queryied.getValue() );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2() ) );
+		assertTrue( Hibernate.isInitialized( e1Queryied.getEntity2().getEntity3() ) );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[]{
+				Entity1.class,
+				Entity2.class,
+				Entity3.class
+		};
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/paths/FetchAndJoinTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/paths/FetchAndJoinTest.java
new file mode 100644
index 0000000000..a71f330e65
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/criteria/paths/FetchAndJoinTest.java
@@ -0,0 +1,69 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test.criteria.paths;
+
+import javax.persistence.EntityManager;
+import javax.persistence.criteria.CriteriaBuilder;
+import javax.persistence.criteria.CriteriaQuery;
+import javax.persistence.criteria.Fetch;
+import javax.persistence.criteria.Join;
+import javax.persistence.criteria.JoinType;
+import javax.persistence.criteria.Root;
+
+import org.junit.Test;
+
+import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
+import org.hibernate.jpa.test.metamodel.Entity1;
+import org.hibernate.jpa.test.metamodel.Entity1_;
+import org.hibernate.jpa.test.metamodel.Entity2;
+import org.hibernate.jpa.test.metamodel.Entity2_;
+
+/**
+ * @author Gail Badner
+ */
+public class FetchAndJoinTest extends AbstractMetamodelSpecificTest {
+
+	@Test
+	public void testImplicitJoinFromExplicitCollectionJoin() {
+		EntityManager em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+
+		final CriteriaBuilder builder = em.getCriteriaBuilder();
+		final CriteriaQuery<Entity1> criteria = builder.createQuery(Entity1.class);
+
+		final Root<Entity1> root = criteria.from(Entity1.class);
+		final Join<Entity1, Entity2> entity2Join = root.join( Entity1_.entity2, JoinType.INNER); // illegal with fetch join
+
+		final Fetch<Entity1, Entity2> entity2Fetch = root.fetch(Entity1_.entity2, JoinType.INNER); // <=== REMOVE
+		entity2Fetch.fetch( Entity2_.entity3 ); // <=== REMOVE
+
+		criteria.where(builder.equal(root.get(Entity1_.value), "test"),
+				builder.equal(entity2Join.get(Entity2_.value), "test")); // illegal with fetch join
+
+		em.createQuery(criteria).getResultList();
+
+		em.getTransaction().commit();
+		em.close();
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/AbstractMetamodelSpecificTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/AbstractMetamodelSpecificTest.java
index 2f28caf784..dc76b227c7 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/AbstractMetamodelSpecificTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/AbstractMetamodelSpecificTest.java
@@ -1,23 +1,24 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa.test.metamodel;
 import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractMetamodelSpecificTest extends BaseEntityManagerFunctionalTestCase {
 	@Override
 	public Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Address.class, Alias.class, Country.class, CreditCard.class, Customer.class,
+				Entity1.class, Entity2.class, Entity3.class,
 				Info.class, LineItem.class, Order.class, Phone.class, Product.class,
 				ShelfLife.class, Spouse.class, Thing.class, ThingWithQuantity.class,
 				VersionedEntity.class
 		};
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity1.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity1.java
new file mode 100644
index 0000000000..09d6be664b
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity1.java
@@ -0,0 +1,43 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test.metamodel;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity1")
+public class Entity1 {
+	@Id
+	private long id;
+
+	@ManyToOne
+	@JoinColumn(name="entity2_id", nullable = false)
+	private Entity2 entity2;
+
+	private String value;
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity2.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity2.java
new file mode 100644
index 0000000000..362ccc11eb
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity2.java
@@ -0,0 +1,43 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test.metamodel;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity2")
+public class Entity2 {
+	@Id
+	private long id;
+
+	@ManyToOne
+	@JoinColumn(name="entity3_id")
+	private Entity3 entity3;
+
+	private String value;
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity3.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity3.java
new file mode 100644
index 0000000000..af804816b6
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/Entity3.java
@@ -0,0 +1,37 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test.metamodel;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+@Entity
+@Table(name = "entity3")
+public class Entity3 {
+	@Id
+	private long id;
+
+	private String value;
+}
