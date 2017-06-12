diff --git a/hibernate-core/src/main/antlr/hql-sql.g b/hibernate-core/src/main/antlr/hql-sql.g
index 3e6d22687c..bdd521da6a 100644
--- a/hibernate-core/src/main/antlr/hql-sql.g
+++ b/hibernate-core/src/main/antlr/hql-sql.g
@@ -1,826 +1,829 @@
 header
 {
 package org.hibernate.hql.internal.antlr;
 
 import java.util.Stack;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.jboss.logging.Logger;
 }
 
 /**
  * Hibernate Query Language to SQL Tree Transform.<br>
  * This is a tree grammar that transforms an HQL AST into a intermediate SQL AST
  * with bindings to Hibernate interfaces (Queryable, etc.).  The Hibernate specific methods
  * are all implemented in the HqlSqlWalker subclass, allowing the ANTLR-generated class
  * to have only the minimum dependencies on the Hibernate code base.   This will also allow
  * the sub-class to be easily edited using an IDE (most IDE's don't support ANTLR).
  * <br>
  * <i>NOTE:</i> The java class is generated from hql-sql.g by ANTLR.
  * <i>DO NOT EDIT THE GENERATED JAVA SOURCE CODE.</i>
  * @author Joshua Davis (joshua@hibernate.org)
  */
 class HqlSqlBaseWalker extends TreeParser;
 
 options
 {
 	// Note: importVocab and exportVocab cause ANTLR to share the token type numbers between the
 	// two grammars.  This means that the token type constants from the source tree are the same
 	// as those in the target tree.  If this is not the case, tree translation can result in
 	// token types from the *source* tree being present in the target tree.
 	importVocab=Hql;        // import definitions from "Hql"
 	exportVocab=HqlSql;     // Call the resulting definitions "HqlSql"
 	buildAST=true;
 }
 
 tokens
 {
 	FROM_FRAGMENT;	// A fragment of SQL that represents a table reference in a FROM clause.
 	IMPLIED_FROM;	// An implied FROM element.
 	JOIN_FRAGMENT;	// A JOIN fragment.
 	ENTITY_JOIN; 	// An "ad-hoc" join to an entity
 	SELECT_CLAUSE;
 	LEFT_OUTER;
 	RIGHT_OUTER;
 	ALIAS_REF;      // An IDENT that is a reference to an entity via it's alias.
 	PROPERTY_REF;   // A DOT that is a reference to a property in an entity.
 	SQL_TOKEN;      // A chunk of SQL that is 'rendered' already.
 	SELECT_COLUMNS; // A chunk of SQL representing a bunch of select columns.
 	SELECT_EXPR;    // A select expression, generated from a FROM element.
 	THETA_JOINS;	// Root of theta join condition subtree.
 	FILTERS;		// Root of the filters condition subtree.
 	METHOD_NAME;    // An IDENT that is a method name.
 	NAMED_PARAM;    // A named parameter (:foo).
 	BOGUS;          // Used for error state detection, etc.
 	RESULT_VARIABLE_REF;   // An IDENT that refers to result variable
 	                       // (i.e, an alias for a select expression) 
 }
 
 // -- Declarations --
 {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HqlSqlBaseWalker.class.getName());
 
 	private int level = 0;
 
 	private boolean inSelect = false;
 	private boolean inFunctionCall = false;
 	private boolean inCase = false;
 	private boolean inFrom = false;
 	private boolean inCount = false;
 	private boolean inCountDistinct = false;
 
 	private int statementType;
 	private String statementTypeName;
 	// Note: currentClauseType tracks the current clause within the current
 	// statement, regardless of level; currentTopLevelClauseType, on the other
 	// hand, tracks the current clause within the top (or primary) statement.
 	// Thus, currentTopLevelClauseType ignores the clauses from any subqueries.
 	private int currentClauseType;
 	private int currentTopLevelClauseType;
 	private int currentStatementType;
 	private Stack<Integer> parentClauses = new Stack<Integer>();
 
 	public final boolean isSubQuery() {
 		return level > 1;
 	}
 
 	public final boolean isInFrom() {
 		return inFrom;
 	}
 
 	public final boolean isInFunctionCall() {
 		return inFunctionCall;
 	}
 	
 	public final boolean isInSelect() {
 		return inSelect;
 	}
 
 	public final boolean isInCase() {
 		return inCase;
 	}
 
     public final boolean isInCount() {
         return inCount;
     }
 
     public final boolean isInCountDistinct() {
         return inCountDistinct;
     }
 
 	public final int getStatementType() {
 		return statementType;
 	}
 
 	public final int getCurrentClauseType() {
 		return currentClauseType;
 	}
 
 	public final int getCurrentTopLevelClauseType() {
 		return currentTopLevelClauseType;
 	}
 
 	public final int getCurrentStatementType() {
 		return currentStatementType;
 	}
 
 	public final boolean isComparativeExpressionClause() {
 		// Note: once we add support for "JOIN ... ON ...",
 		// the ON clause needs to get included here
 	    return getCurrentClauseType() == WHERE ||
 	            getCurrentClauseType() == WITH ||
 	            isInCase();
 	}
 
 	public final boolean isSelectStatement() {
 		return statementType == SELECT;
 	}
 
 	private void beforeStatement(String statementName, int statementType) {
 		inFunctionCall = false;
 		level++;
 		if ( level == 1 ) {
 			this.statementTypeName = statementName;
 			this.statementType = statementType;
 		}
 		currentStatementType = statementType;
 		LOG.debugf("%s << begin [level=%s, statement=%s]", statementName, level, this.statementTypeName);
 	}
 
 	private void beforeStatementCompletion(String statementName) {
         LOG.debugf("%s : finishing up [level=%s, statement=%s]", statementName, level, this.statementTypeName);
 	}
 
 	private void afterStatementCompletion(String statementName) {
         LOG.debugf("%s >> end [level=%s, statement=%s]", statementName, level, this.statementTypeName);
 		level--;
 	}
 
 	private void handleClauseStart(int clauseType) {
 		parentClauses.push(currentClauseType);
 		currentClauseType = clauseType;
 		if ( level == 1 ) {
 			currentTopLevelClauseType = clauseType;
 		}
 	}
 
 	private void handleClauseEnd() {
 		currentClauseType = parentClauses.pop();
 	}
 
 	///////////////////////////////////////////////////////////////////////////
 	// NOTE: The real implementations for the following are in the subclass.
 
 	protected void evaluateAssignment(AST eq) throws SemanticException { }
 	
 	/** Pre-process the from clause input tree. **/
 	protected void prepareFromClauseInputTree(AST fromClauseInput) {}
 
 	/** Sets the current 'FROM' context. **/
 	protected void pushFromClause(AST fromClause,AST inputFromNode) {}
 
 	protected AST createFromElement(String path,AST alias,AST propertyFetch) throws SemanticException {
 		return null;
 	}
 
 	protected void createFromJoinElement(AST path,AST alias,int joinType,AST fetch,AST propertyFetch,AST with) throws SemanticException {}
 
 	protected AST createFromFilterElement(AST filterEntity,AST alias) throws SemanticException	{
 		return null;
 	}
 
 	protected void processQuery(AST select,AST query) throws SemanticException { }
 
 	protected void postProcessUpdate(AST update) throws SemanticException { }
 
 	protected void postProcessDelete(AST delete) throws SemanticException { }
 
 	protected void postProcessInsert(AST insert) throws SemanticException { }
 
 	protected void beforeSelectClause() throws SemanticException { }
 
 	protected void processIndex(AST indexOp) throws SemanticException { }
 
 	protected void processConstant(AST constant) throws SemanticException { }
 
 	protected void processBoolean(AST constant) throws SemanticException { }
 
 	protected void processNumericLiteral(AST literal) throws SemanticException { }
 
 	protected void resolve(AST node) throws SemanticException { }
 
+	protected void resolve(AST node, AST predicateNode) throws SemanticException { }
+
 	protected void resolveSelectExpression(AST dotNode) throws SemanticException { }
 
 	protected void processFunction(AST functionCall,boolean inSelect) throws SemanticException { }
 
 	protected void processCastFunction(AST functionCall,boolean inSelect) throws SemanticException { }
 
 	protected void processAggregation(AST node, boolean inSelect) throws SemanticException { }
 
 	protected void processConstructor(AST constructor) throws SemanticException { }
 
 	protected AST generateNamedParameter(AST delimiterNode, AST nameNode) throws SemanticException {
 		return #( [NAMED_PARAM, nameNode.getText()] );
 	}
 
 	protected AST generatePositionalParameter(AST inputNode) throws SemanticException {
 		return #( [PARAM, "?"] );
 	}
 
 	protected void lookupAlias(AST ident) throws SemanticException { }
 
 	protected void setAlias(AST selectExpr, AST ident) { }
 
 	protected boolean isOrderExpressionResultVariableRef(AST ident) throws SemanticException {
 		return false;
 	}
 
 	protected void handleResultVariableRef(AST resultVariableRef) throws SemanticException {
 	}
 
 	protected AST lookupProperty(AST dot,boolean root,boolean inSelect) throws SemanticException {
 		return dot;
 	}
 
 	protected boolean isNonQualifiedPropertyRef(AST ident) { return false; }
 
 	protected AST lookupNonQualifiedProperty(AST property) throws SemanticException { return property; }
 
 	protected void setImpliedJoinType(int joinType) { }
 
 	protected AST createIntoClause(String path, AST propertySpec) throws SemanticException {
 		return null;
 	};
 
 	protected void prepareVersioned(AST updateNode, AST versionedNode) throws SemanticException {}
 
 	protected void prepareLogicOperator(AST operator) throws SemanticException { }
 
 	protected void prepareArithmeticOperator(AST operator) throws SemanticException { }
 
     protected void processMapComponentReference(AST node) throws SemanticException { }
 
 	protected void validateMapPropertyExpression(AST node) throws SemanticException { }
 	protected void finishFromClause (AST fromClause) throws SemanticException { }
 
 }
 
 // The main statement rule.
 statement
 	: selectStatement | updateStatement | deleteStatement | insertStatement
 	;
 
 selectStatement
 	: query
 	;
 
 // Cannot use just the fromElement rule here in the update and delete queries
 // because fromElement essentially relies on a FromClause already having been
 // built :(
 updateStatement!
 	: #( u:UPDATE { beforeStatement( "update", UPDATE ); } (v:VERSIONED)? f:fromClause s:setClause (w:whereClause)? ) {
 		#updateStatement = #(#u, #f, #s, #w);
 		beforeStatementCompletion( "update" );
 		prepareVersioned( #updateStatement, #v );
 		postProcessUpdate( #updateStatement );
 		afterStatementCompletion( "update" );
 	}
 	;
 
 deleteStatement
 	: #( DELETE { beforeStatement( "delete", DELETE ); } fromClause (whereClause)? ) {
 		beforeStatementCompletion( "delete" );
 		postProcessDelete( #deleteStatement );
 		afterStatementCompletion( "delete" );
 	}
 	;
 
 insertStatement
 	// currently only "INSERT ... SELECT ..." statements supported;
 	// do we also need support for "INSERT ... VALUES ..."?
 	//
 	: #( INSERT { beforeStatement( "insert", INSERT ); } intoClause query ) {
 		beforeStatementCompletion( "insert" );
 		postProcessInsert( #insertStatement );
 		afterStatementCompletion( "insert" );
 	}
 	;
 
 intoClause! {
 		String p = null;
 	}
 	: #( INTO { handleClauseStart( INTO ); } (p=path) ps:insertablePropertySpec ) {
 		#intoClause = createIntoClause(p, ps);
 		handleClauseEnd();
 	}
 	;
 
 insertablePropertySpec
 	: #( RANGE (IDENT)+ )
 	;
 
 setClause
 	: #( SET { handleClauseStart( SET ); } (assignment)* ) {
 		handleClauseEnd();
 	}
 	;
 
 assignment
 	// Note: the propertyRef here needs to be resolved
 	// *before* we evaluate the newValue rule...
 	: #( EQ (p:propertyRef) { resolve(#p); } (newValue) ) {
 		evaluateAssignment( #assignment );
 	}
 	;
 
 // For now, just use expr.  Revisit after ejb3 solidifies this.
 newValue
-	: expr | query
+	: expr [ null ] | query
 	;
 
 // The query / subquery rule. Pops the current 'from node' context 
 // (list of aliases).
 query!
 	: #( QUERY { beforeStatement( "select", SELECT ); }
 			// The first phase places the FROM first to make processing the SELECT simpler.
 			#(SELECT_FROM
 				f:fromClause
 				(s:selectClause)?
 			)
 			(w:whereClause)?
 			(g:groupClause)?
 			(o:orderClause)?
 		) {
 		// Antlr note: #x_in refers to the input AST, #x refers to the output AST
 		#query = #([SELECT,"SELECT"], #s, #f, #w, #g, #o);
 		beforeStatementCompletion( "select" );
 		processQuery( #s, #query );
 		afterStatementCompletion( "select" );
 	}
 	;
 
 orderClause
 	: #(ORDER { handleClauseStart( ORDER ); } orderExprs) {
 		handleClauseEnd();
 	}
 	;
 
 orderExprs
 	: orderExpr ( ASCENDING | DESCENDING )? ( nullOrdering )? (orderExprs)?
 	;
 
 nullOrdering
     : NULLS nullPrecedence
     ;
 
 nullPrecedence
     : FIRST
     | LAST
     ;
 
 orderExpr
 	: { isOrderExpressionResultVariableRef( _t ) }? resultVariableRef
-	| expr
+	| expr [ null ]
 	;
 
 resultVariableRef!
 	: i:identifier {
 		// Create a RESULT_VARIABLE_REF node instead of an IDENT node.
 		#resultVariableRef = #([RESULT_VARIABLE_REF, i.getText()]);
 		handleResultVariableRef(#resultVariableRef);
 	}
 	;
 
 groupClause
-	: #(GROUP { handleClauseStart( GROUP ); } (expr)+ ( #(HAVING logicalExpr) )? ) {
+	: #(GROUP { handleClauseStart( GROUP ); } (expr [ null ])+ ( #(HAVING logicalExpr) )? ) {
 		handleClauseEnd();
 	}
 	;
 
 selectClause!
 	: #(SELECT { handleClauseStart( SELECT ); beforeSelectClause(); } (d:DISTINCT)? x:selectExprList ) {
 		#selectClause = #([SELECT_CLAUSE,"{select clause}"], #d, #x);
 		handleClauseEnd();
 	}
 	;
 
 selectExprList {
 		boolean oldInSelect = inSelect;
 		inSelect = true;
 	}
 	: ( selectExpr | aliasedSelectExpr )+ {
 		inSelect = oldInSelect;
 	}
 	;
 
 aliasedSelectExpr!
 	: #(AS se:selectExpr i:identifier) {
 		setAlias(#se,#i);
 		#aliasedSelectExpr = #se;
 	}
 	;
 
 selectExpr
 	: p:propertyRef					{ resolveSelectExpression(#p); }
 	| #(ALL ar2:aliasRef) 			{ resolveSelectExpression(#ar2); #selectExpr = #ar2; }
 	| #(OBJECT ar3:aliasRef)		{ resolveSelectExpression(#ar3); #selectExpr = #ar3; }
 	| con:constructor 				{ processConstructor(#con); }
 	| functionCall
 	| count
 	| collectionFunction			// elements() or indices()
 	| constant
-	| arithmeticExpr
+	| arithmeticExpr [ null ]
 	| logicalExpr
 	| parameter
 	| query
 	;
 
 count
     : #(COUNT  { inCount = true; } ( DISTINCT { inCountDistinct = true; } | ALL )? ( aggregateExpr | ROW_STAR ) ) {
         inCount = false;
         inCountDistinct = false;
     }
     ;
 
 constructor
 	{ String className = null; }
 	: #(CONSTRUCTOR className=path ( selectExpr | aliasedSelectExpr )* )
 	;
 
 aggregateExpr
-	: expr //p:propertyRef { resolve(#p); }
+	: expr [ null ] //p:propertyRef { resolve(#p); }
 	| collectionFunction
 	;
 
 // Establishes the list of aliases being used by this query.
 fromClause {
 		// NOTE: This references the INPUT AST! (see http://www.antlr.org/doc/trees.html#Action%20Translation)
 		// the ouput AST (#fromClause) has not been built yet.
 		prepareFromClauseInputTree(#fromClause_in);
 	}
 	: #(f:FROM { pushFromClause(#fromClause,f); handleClauseStart( FROM ); } fromElementList ) {
 		finishFromClause( #f );
 		handleClauseEnd();
 	}
 	;
 
 fromElementList {
 		boolean oldInFrom = inFrom;
 		inFrom = true;
 		}
 	: (fromElement)+ {
 		inFrom = oldInFrom;
 		}
 	;
 
 fromElement! {
 	String p = null;
 	}
 	// A simple class name, alias element.
 	: #(RANGE p=path (a:ALIAS)? (pf:FETCH)? ) {
 		#fromElement = createFromElement(p,a, pf);
 	}
 	| je:joinElement {
 		#fromElement = #je;
 	}
 	// A from element created due to filter compilation
 	| fe:FILTER_ENTITY a3:ALIAS {
 		#fromElement = createFromFilterElement(fe,a3);
 	}
 	;
 
 joinElement! {
 		int j = INNER;
 	}
 	// A from element with a join.  This time, the 'path' should be treated as an AST
 	// and resolved (like any path in a WHERE clause).   Make sure all implied joins
 	// generated by the property ref use the join type, if it was specified.
 	: #(JOIN (j=joinType { setImpliedJoinType(j); } )? (f:FETCH)? ref:propertyRef (a:ALIAS)? (pf:FETCH)? (with:WITH)? ) {
 		//createFromJoinElement(#ref,a,j,f, pf);
 		createFromJoinElement(#ref,a,j,f, pf, with);
 		setImpliedJoinType(INNER);	// Reset the implied join type.
 	}
 	;
 
 // Returns an node type integer that represents the join type
 // tokens.
 joinType returns [int j] {
 	j = INNER;
 	}
 	: ( (left:LEFT | right:RIGHT) (outer:OUTER)? ) {
 		if (left != null)       j = LEFT_OUTER;
 		else if (right != null) j = RIGHT_OUTER;
 		else if (outer != null) j = RIGHT_OUTER;
 	}
 	| FULL {
 		j = FULL;
 	}
 	| INNER {
 		j = INNER;
 	}
 	;
 
 // Matches a path and returns the normalized string for the path (usually
 // fully qualified a class name).
 path returns [String p] {
 	p = "???";
 	String x = "?x?";
 	}
 	: a:identifier { p = a.getText(); }
 	| #(DOT x=path y:identifier) {
 			StringBuilder buf = new StringBuilder();
 			buf.append(x).append(".").append(y.getText());
 			p = buf.toString();
 		}
 	;
 
 // Returns a path as a single identifier node.
 pathAsIdent {
     String text = "?text?";
     }
     : text=path {
         #pathAsIdent = #([IDENT,text]);
     }
     ;
 
 withClause
 	// Note : this is used internally from the HqlSqlWalker to
 	// parse the node recognized with the with keyword earlier.
 	// Done this way because it relies on the join it "qualifies"
 	// already having been processed, which would not be the case
 	// if withClause was simply referenced from the joinElement
 	// rule during recognition...
 	: #(w:WITH { handleClauseStart( WITH ); } b:logicalExpr ) {
 		#withClause = #(w , #b);
 		handleClauseEnd();
 	}
 	;
 
 whereClause
 	: #(w:WHERE { handleClauseStart( WHERE ); } b:logicalExpr ) {
 		// Use the *output* AST for the boolean expression!
 		#whereClause = #(w , #b);
 		handleClauseEnd();
 	}
 	;
 
 logicalExpr
 	: #(AND logicalExpr logicalExpr)
 	| #(OR logicalExpr logicalExpr)
 	| #(NOT logicalExpr)
 	| comparisonExpr
 	;
 
 // TODO: Add any other comparison operators here.
+// We pass through the comparisonExpr AST to the expressions so that joins can be avoided for EQ/IN/NULLNESS
 comparisonExpr
 	:
-	( #(EQ exprOrSubquery exprOrSubquery)
-	| #(NE exprOrSubquery exprOrSubquery)
-	| #(LT exprOrSubquery exprOrSubquery)
-	| #(GT exprOrSubquery exprOrSubquery)
-	| #(LE exprOrSubquery exprOrSubquery)
-	| #(GE exprOrSubquery exprOrSubquery)
-	| #(LIKE exprOrSubquery expr ( #(ESCAPE expr) )? )
-	| #(NOT_LIKE exprOrSubquery expr ( #(ESCAPE expr) )? )
-	| #(BETWEEN exprOrSubquery exprOrSubquery exprOrSubquery)
-	| #(NOT_BETWEEN exprOrSubquery exprOrSubquery exprOrSubquery)
-	| #(IN exprOrSubquery inRhs )
-	| #(NOT_IN exprOrSubquery inRhs )
-	| #(IS_NULL exprOrSubquery)
-	| #(IS_NOT_NULL exprOrSubquery)
-//	| #(IS_TRUE expr)
-//	| #(IS_FALSE expr)
-	| #(EXISTS ( expr | collectionFunctionOrSubselect ) )
+	( #(EQ exprOrSubquery [ currentAST.root ] exprOrSubquery [ currentAST.root ])
+	| #(NE exprOrSubquery [ currentAST.root ] exprOrSubquery [ currentAST.root ])
+	| #(LT exprOrSubquery [ null ] exprOrSubquery [ null ])
+	| #(GT exprOrSubquery [ null ] exprOrSubquery [ null ])
+	| #(LE exprOrSubquery [ null ] exprOrSubquery [ null ])
+	| #(GE exprOrSubquery [ null ] exprOrSubquery [ null ])
+	| #(LIKE exprOrSubquery [ null ] expr [ null ] ( #(ESCAPE expr [ null ]) )? )
+	| #(NOT_LIKE exprOrSubquery [ null ] expr [ null ] ( #(ESCAPE expr [ null ]) )? )
+	| #(BETWEEN exprOrSubquery [ null ] exprOrSubquery [ null ] exprOrSubquery [ null ])
+	| #(NOT_BETWEEN exprOrSubquery [ null ] exprOrSubquery [ null ] exprOrSubquery [ null ])
+	| #(IN exprOrSubquery [ currentAST.root ] inRhs [ currentAST.root ] )
+	| #(NOT_IN exprOrSubquery [ currentAST.root ] inRhs [ currentAST.root ] )
+	| #(IS_NULL exprOrSubquery [ currentAST.root ])
+	| #(IS_NOT_NULL exprOrSubquery [ currentAST.root ])
+//	| #(IS_TRUE expr [ null ])
+//	| #(IS_FALSE expr [ null ])
+	| #(EXISTS ( expr [ null ] | collectionFunctionOrSubselect ) )
 	) {
 	    prepareLogicOperator( #comparisonExpr );
 	}
 	;
 
-inRhs
-	: #(IN_LIST ( collectionFunctionOrSubselect | ( (expr)* ) ) )
+inRhs [ AST predicateNode ]
+	: #(IN_LIST ( collectionFunctionOrSubselect | ( (expr [ predicateNode ])* ) ) )
 	;
 
-exprOrSubquery
-	: expr
+exprOrSubquery [ AST predicateNode ]
+	: expr [ predicateNode ]
 	| query
 	| #(ANY collectionFunctionOrSubselect)
 	| #(ALL collectionFunctionOrSubselect)
 	| #(SOME collectionFunctionOrSubselect)
 	;
 	
 collectionFunctionOrSubselect
 	: collectionFunction
 	| query
 	;
 	
-expr
-	: ae:addrExpr [ true ] { resolve(#ae); }	// Resolve the top level 'address expression'
-	| #( VECTOR_EXPR (expr)* )
+expr [ AST predicateNode ]
+	: ae:addrExpr [ true ] { resolve(#ae, predicateNode); }	// Resolve the top level 'address expression'
+	| #( VECTOR_EXPR (expr [ predicateNode ])* )
 	| constant
-	| arithmeticExpr
+	| arithmeticExpr [ predicateNode ]
 	| functionCall							// Function call, not in the SELECT clause.
 	| parameter
 	| count										// Count, not in the SELECT clause.
 	;
 
-arithmeticExpr
-    : #(PLUS exprOrSubquery exprOrSubquery)         { prepareArithmeticOperator( #arithmeticExpr ); }
-    | #(MINUS exprOrSubquery exprOrSubquery)        { prepareArithmeticOperator( #arithmeticExpr ); }
-    | #(DIV exprOrSubquery exprOrSubquery)          { prepareArithmeticOperator( #arithmeticExpr ); }
-    | #(MOD exprOrSubquery exprOrSubquery)          { prepareArithmeticOperator( #arithmeticExpr ); }
-    | #(STAR exprOrSubquery exprOrSubquery)         { prepareArithmeticOperator( #arithmeticExpr ); }
-//	| #(CONCAT expr (expr)+ )   { prepareArithmeticOperator( #arithmeticExpr ); }
-	| #(UNARY_MINUS expr)       { prepareArithmeticOperator( #arithmeticExpr ); }
-	| caseExpr
+arithmeticExpr [ AST predicateNode ]
+    : #(PLUS exprOrSubquery [ null ] exprOrSubquery [ null ])         { prepareArithmeticOperator( #arithmeticExpr ); }
+    | #(MINUS exprOrSubquery [ null ] exprOrSubquery [ null ])        { prepareArithmeticOperator( #arithmeticExpr ); }
+    | #(DIV exprOrSubquery [ null ] exprOrSubquery [ null ])          { prepareArithmeticOperator( #arithmeticExpr ); }
+    | #(MOD exprOrSubquery [ null ] exprOrSubquery [ null ])          { prepareArithmeticOperator( #arithmeticExpr ); }
+    | #(STAR exprOrSubquery [ null ] exprOrSubquery [ null ])         { prepareArithmeticOperator( #arithmeticExpr ); }
+//	| #(CONCAT expr [ null ] (expr [ null ])+ )   { prepareArithmeticOperator( #arithmeticExpr ); }
+	| #(UNARY_MINUS expr [ null ])       { prepareArithmeticOperator( #arithmeticExpr ); }
+	| caseExpr [ predicateNode ]
 	;
 
-caseExpr
-	: simpleCaseExpression
-	| searchedCaseExpression
+caseExpr [ AST predicateNode ]
+	: simpleCaseExpression [ predicateNode ]
+	| searchedCaseExpression [ predicateNode ]
 	;
 
-expressionOrSubQuery
-	: expr
+expressionOrSubQuery [ AST predicateNode ]
+	: expr [ predicateNode ]
 	| query
 	;
 
-simpleCaseExpression
-	: #(CASE2 {inCase=true;} expressionOrSubQuery (simpleCaseWhenClause)+ (elseClause)?) {inCase=false;}
+simpleCaseExpression [ AST predicateNode ]
+	: #(CASE2 {inCase=true;} expressionOrSubQuery [ currentAST.root ] (simpleCaseWhenClause [ currentAST.root, predicateNode ])+ (elseClause [ predicateNode ])?) {inCase=false;}
 	;
 
-simpleCaseWhenClause
-	: #(WHEN expressionOrSubQuery expressionOrSubQuery)
+simpleCaseWhenClause [ AST predicateNode, AST superPredicateNode ]
+	: #(WHEN expressionOrSubQuery [ predicateNode ] expressionOrSubQuery [ superPredicateNode ])
 	;
 
-elseClause
-	: #(ELSE expressionOrSubQuery)
+elseClause [ AST predicateNode ]
+	: #(ELSE expressionOrSubQuery [ predicateNode ])
 	;
 
-searchedCaseExpression
-	: #(CASE {inCase = true;} (searchedCaseWhenClause)+ (elseClause)?) {inCase = false;}
+searchedCaseExpression [ AST predicateNode ]
+	: #(CASE {inCase = true;} (searchedCaseWhenClause [ predicateNode ])+ (elseClause [ predicateNode ])?) {inCase = false;}
 	;
 
-searchedCaseWhenClause
-	: #(WHEN logicalExpr expressionOrSubQuery)
+searchedCaseWhenClause [ AST predicateNode ]
+	: #(WHEN logicalExpr expressionOrSubQuery [ predicateNode ])
 	;
 
 
 //TODO: I don't think we need this anymore .. how is it different to 
 //      maxelements, etc, which are handled by functionCall
 collectionFunction
 	: #(e:ELEMENTS {inFunctionCall=true;} p1:propertyRef { resolve(#p1); } ) 
 		{ processFunction(#e,inSelect); } {inFunctionCall=false;}
 	| #(i:INDICES {inFunctionCall=true;} p2:propertyRef { resolve(#p2); } ) 
 		{ processFunction(#i,inSelect); } {inFunctionCall=false;}
 	;
 
 functionCall
-	: #(METHOD_CALL  {inFunctionCall=true;} pathAsIdent ( #(EXPR_LIST (exprOrSubquery)* ) )? ) {
+	: #(METHOD_CALL  {inFunctionCall=true;} pathAsIdent ( #(EXPR_LIST (exprOrSubquery [ null ])* ) )? ) {
         processFunction( #functionCall, inSelect );
         inFunctionCall=false;
     }
-    | #(CAST {inFunctionCall=true;} exprOrSubquery pathAsIdent) {
+    | #(CAST {inFunctionCall=true;} exprOrSubquery [ null ] pathAsIdent) {
     	processCastFunction( #functionCall, inSelect );
         inFunctionCall=false;
     }
 	| #(AGGREGATE aggregateExpr )
 	;
 
 constant
 	: literal
 	| NULL
 	| TRUE { processBoolean(#constant); }
 	| FALSE { processBoolean(#constant); }
 	| JAVA_CONSTANT
 	;
 
 literal
 	: NUM_INT { processNumericLiteral( #literal ); }
 	| NUM_LONG { processNumericLiteral( #literal ); }
 	| NUM_FLOAT { processNumericLiteral( #literal ); }
 	| NUM_DOUBLE { processNumericLiteral( #literal ); }
 	| NUM_BIG_INTEGER { processNumericLiteral( #literal ); }
 	| NUM_BIG_DECIMAL { processNumericLiteral( #literal ); }
 	| QUOTED_STRING
 	;
 
 identifier
 	: (IDENT | WEIRD_IDENT)
 	;
 
 addrExpr! [ boolean root ]
 	: #(d:DOT lhs:addrExprLhs rhs:propertyName )	{
 		// This gives lookupProperty() a chance to transform the tree 
 		// to process collection properties (.elements, etc).
 		#addrExpr = #(#d, #lhs, #rhs);
 		#addrExpr = lookupProperty(#addrExpr,root,false);
 	}
-	| #(i:INDEX_OP lhs2:addrExprLhs rhs2:expr)	{
+	| #(i:INDEX_OP lhs2:addrExprLhs rhs2:expr [ null ])	{
 		#addrExpr = #(#i, #lhs2, #rhs2);
 		processIndex(#addrExpr);
 	}
 	| mcr:mapComponentReference {
 	    #addrExpr = #mcr;
 	}
 	| p:identifier {
 //		#addrExpr = #p;
 //		resolve(#addrExpr);
 		// In many cases, things other than property-refs are recognized
 		// by this addrExpr rule.  Some of those I have seen:
 		//  1) select-clause from-aliases
 		//  2) sql-functions
 		if ( isNonQualifiedPropertyRef(#p) ) {
 			#addrExpr = lookupNonQualifiedProperty(#p);
 		}
 		else {
 			resolve(#p);
 			#addrExpr = #p;
 		}
 	}
 	;
 
 addrExprLhs
 	: addrExpr [ false ]
 	;
 
 propertyName
 	: identifier
 	| CLASS
 	| ELEMENTS
 	| INDICES
 	;
 
 propertyRef!
 	: mcr:mapComponentReference {
 	    resolve( #mcr );
 	    #propertyRef = #mcr;
 	}
 	| #(d:DOT lhs:propertyRefLhs rhs:propertyName )	{
 		// This gives lookupProperty() a chance to transform the tree to process collection properties (.elements, etc).
 		#propertyRef = #(#d, #lhs, #rhs);
 		#propertyRef = lookupProperty(#propertyRef,false,true);
 	}
 	|
 	p:identifier {
 		// In many cases, things other than property-refs are recognized
 		// by this propertyRef rule.  Some of those I have seen:
 		//  1) select-clause from-aliases
 		//  2) sql-functions
 		if ( isNonQualifiedPropertyRef(#p) ) {
 			#propertyRef = lookupNonQualifiedProperty(#p);
 		}
 		else {
 			resolve(#p);
 			#propertyRef = #p;
 		}
 	}
 	;
 
 propertyRefLhs
 	: propertyRef
 	;
 
 aliasRef!
 	: i:identifier {
 		#aliasRef = #([ALIAS_REF,i.getText()]);	// Create an ALIAS_REF node instead of an IDENT node.
 		lookupAlias(#aliasRef);
 		}
 	;
 
 mapComponentReference
     : #( KEY mapPropertyExpression )
     | #( VALUE mapPropertyExpression )
     | #( ENTRY mapPropertyExpression )
     ;
 
 mapPropertyExpression
-    : e:expr {
+    : e:expr [ null ] {
         validateMapPropertyExpression( #e );
     }
     ;
 
 parameter!
 	: #(c:COLON a:identifier) {
 			// Create a NAMED_PARAM node instead of (COLON IDENT).
 			#parameter = generateNamedParameter( c, a );
 //			#parameter = #([NAMED_PARAM,a.getText()]);
 //			namedParameter(#parameter);
 		}
 	| #(p:PARAM (n:NUM_INT)?) {
 			if ( n != null ) {
 				// An ejb3-style "positional parameter", which we handle internally as a named-param
 				#parameter = generateNamedParameter( p, n );
 //				#parameter = #([NAMED_PARAM,n.getText()]);
 //				namedParameter(#parameter);
 			}
 			else {
 				#parameter = generatePositionalParameter( p );
 //				#parameter = #([PARAM,"?"]);
 //				positionalParameter(#parameter);
 			}
 		}
 	;
 
 numericInteger
 	: NUM_INT
 	;
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
index f4fe6dabc3..89b4de4e0f 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
@@ -26,1396 +26,1401 @@ import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.antlr.HqlSqlBaseWalker;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.tree.AggregateNode;
 import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
 import org.hibernate.hql.internal.ast.tree.CastFunctionNode;
 import org.hibernate.hql.internal.ast.tree.CollectionFunction;
 import org.hibernate.hql.internal.ast.tree.ConstructorNode;
 import org.hibernate.hql.internal.ast.tree.DeleteStatement;
 import org.hibernate.hql.internal.ast.tree.DotNode;
 import org.hibernate.hql.internal.ast.tree.EntityJoinFromElement;
 import org.hibernate.hql.internal.ast.tree.FromClause;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.FromElementFactory;
 import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
 import org.hibernate.hql.internal.ast.tree.IdentNode;
 import org.hibernate.hql.internal.ast.tree.IndexNode;
 import org.hibernate.hql.internal.ast.tree.InsertStatement;
 import org.hibernate.hql.internal.ast.tree.IntoClause;
 import org.hibernate.hql.internal.ast.tree.MethodNode;
 import org.hibernate.hql.internal.ast.tree.OperatorNode;
 import org.hibernate.hql.internal.ast.tree.ParameterContainer;
 import org.hibernate.hql.internal.ast.tree.ParameterNode;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.ResolvableNode;
 import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
 import org.hibernate.hql.internal.ast.tree.ResultVariableRefNode;
 import org.hibernate.hql.internal.ast.tree.SelectClause;
 import org.hibernate.hql.internal.ast.tree.SelectExpression;
 import org.hibernate.hql.internal.ast.tree.UpdateStatement;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.AliasGenerator;
 import org.hibernate.hql.internal.ast.util.JoinProcessor;
 import org.hibernate.hql.internal.ast.util.LiteralProcessor;
 import org.hibernate.hql.internal.ast.util.NodeTraverser;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.hql.internal.ast.util.SyntheticAndFactory;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.param.CollectionFilterKeyParameterSpecification;
 import org.hibernate.param.NamedParameterSpecification;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.param.PositionalParameterSpecification;
 import org.hibernate.param.VersionTypeSeedParameterSpecification;
 import org.hibernate.persister.collection.CollectionPropertyNames;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.DbTimestampType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 import org.hibernate.usertype.UserVersionType;
 
 import antlr.ASTFactory;
 import antlr.RecognitionException;
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Implements methods used by the HQL->SQL tree transform grammar (a.k.a. the second phase).
  * <ul>
  * <li>Isolates the Hibernate API-specific code from the ANTLR generated code.</li>
  * <li>Handles the SQL fragments generated by the persisters in order to create the SELECT and FROM clauses,
  * taking into account the joins and projections that are implied by the mappings (persister/queryable).</li>
  * <li>Uses SqlASTFactory to create customized AST nodes.</li>
  * </ul>
  *
  * @see SqlASTFactory
  */
 public class HqlSqlWalker extends HqlSqlBaseWalker implements ErrorReporter, ParameterBinder.NamedParameterSource {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( HqlSqlWalker.class );
 
 	private final QueryTranslatorImpl queryTranslatorImpl;
 	private final HqlParser hqlParser;
 	private final SessionFactoryHelper sessionFactoryHelper;
 	private final Map tokenReplacements;
 	private final AliasGenerator aliasGenerator = new AliasGenerator();
 	private final LiteralProcessor literalProcessor;
 	private final ParseErrorHandler parseErrorHandler;
 	private final ASTPrinter printer;
 	private final String collectionFilterRole;
 
 	private FromClause currentFromClause;
 	private SelectClause selectClause;
 
 	/**
 	 * Maps each top-level result variable to its SelectExpression;
 	 * (excludes result variables defined in subqueries)
 	 */
 	private Map<String, SelectExpression> selectExpressionsByResultVariable = new HashMap<String, SelectExpression>();
 
 	private Set<Serializable> querySpaces = new HashSet<Serializable>();
 
 	private int parameterCount;
 	private Map namedParameters = new HashMap();
 	private ArrayList<ParameterSpecification> parameters = new ArrayList<ParameterSpecification>();
 	private int numberOfParametersInSetClause;
 	private int positionalParameterCount;
 
 	private ArrayList assignmentSpecifications = new ArrayList();
 
 	private JoinType impliedJoinType = JoinType.INNER_JOIN;
 
 	private boolean inEntityGraph;
 
 	/**
 	 * Create a new tree transformer.
 	 *
 	 * @param qti Back pointer to the query translator implementation that is using this tree transform.
 	 * @param sfi The session factory implementor where the Hibernate mappings can be found.
 	 * @param parser A reference to the phase-1 parser
 	 * @param tokenReplacements Registers the token replacement map with the walker.  This map will
 	 * be used to substitute function names and constants.
 	 * @param collectionRole The collection role name of the collection used as the basis for the
 	 * filter, NULL if this is not a collection filter compilation.
 	 */
 	public HqlSqlWalker(
 			QueryTranslatorImpl qti,
 			SessionFactoryImplementor sfi,
 			HqlParser parser,
 			Map tokenReplacements,
 			String collectionRole) {
 		setASTFactory( new SqlASTFactory( this ) );
 		// Initialize the error handling delegate.
 		this.parseErrorHandler = new ErrorCounter( qti.getQueryString() );
 		this.queryTranslatorImpl = qti;
 		this.sessionFactoryHelper = new SessionFactoryHelper( sfi );
 		this.literalProcessor = new LiteralProcessor( this );
 		this.tokenReplacements = tokenReplacements;
 		this.collectionFilterRole = collectionRole;
 		this.hqlParser = parser;
 		this.printer = new ASTPrinter( SqlTokenTypes.class );
 	}
 
 	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int traceDepth;
 
 	@Override
 	public void traceIn(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = StringHelper.repeat( '-', ( traceDepth++ * 2 ) ) + "-> ";
 		String traceText = ruleName + " (" + buildTraceNodeName( tree ) + ")";
 		LOG.trace( prefix + traceText );
 	}
 
 	private String buildTraceNodeName(AST tree) {
 		return tree == null
 				? "???"
 				: tree.getText() + " [" + printer.getTokenTypeName( tree.getType() ) + "]";
 	}
 
 	@Override
 	public void traceOut(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = "<-" + StringHelper.repeat( '-', ( --traceDepth * 2 ) ) + " ";
 		LOG.trace( prefix + ruleName );
 	}
 
 	@Override
 	protected void prepareFromClauseInputTree(AST fromClauseInput) {
 		if ( !isSubQuery() ) {
 //			// inject param specifications to account for dynamic filter param values
 //			if ( ! getEnabledFilters().isEmpty() ) {
 //				Iterator filterItr = getEnabledFilters().values().iterator();
 //				while ( filterItr.hasNext() ) {
 //					FilterImpl filter = ( FilterImpl ) filterItr.next();
 //					if ( ! filter.getFilterDefinition().getParameterNames().isEmpty() ) {
 //						Iterator paramItr = filter.getFilterDefinition().getParameterNames().iterator();
 //						while ( paramItr.hasNext() ) {
 //							String parameterName = ( String ) paramItr.next();
 //							// currently param filters *only* work with single-column parameter types;
 //							// if that limitation is ever lifted, this logic will need to change to account for that
 //							ParameterNode collectionFilterKeyParameter = ( ParameterNode ) astFactory.create( PARAM, "?" );
 //							DynamicFilterParameterSpecification paramSpec = new DynamicFilterParameterSpecification(
 //									filter.getName(),
 //									parameterName,
 //									filter.getFilterDefinition().getParameterType( parameterName ),
 //									 positionalParameterCount++
 //							);
 //							collectionFilterKeyParameter.setHqlParameterSpecification( paramSpec );
 //							parameters.add( paramSpec );
 //						}
 //					}
 //				}
 //			}
 
 			if ( isFilter() ) {
 				// Handle collection-filter compilation.
 				// IMPORTANT NOTE: This is modifying the INPUT (HQL) tree, not the output tree!
 				QueryableCollection persister = sessionFactoryHelper.getCollectionPersister( collectionFilterRole );
 				Type collectionElementType = persister.getElementType();
 				if ( !collectionElementType.isEntityType() ) {
 					throw new QueryException( "collection of values in filter: this" );
 				}
 
 				String collectionElementEntityName = persister.getElementPersister().getEntityName();
 				ASTFactory inputAstFactory = hqlParser.getASTFactory();
 				AST fromElement = inputAstFactory.create( HqlTokenTypes.FILTER_ENTITY, collectionElementEntityName );
 				ASTUtil.createSibling( inputAstFactory, HqlTokenTypes.ALIAS, "this", fromElement );
 				fromClauseInput.addChild( fromElement );
 				// Show the modified AST.
 				LOG.debug( "prepareFromClauseInputTree() : Filter - Added 'this' as a from element..." );
 				queryTranslatorImpl.showHqlAst( hqlParser.getAST() );
 
 				// Create a parameter specification for the collection filter...
 				Type collectionFilterKeyType = sessionFactoryHelper.requireQueryableCollection( collectionFilterRole )
 						.getKeyType();
 				ParameterNode collectionFilterKeyParameter = (ParameterNode) astFactory.create( PARAM, "?" );
 				CollectionFilterKeyParameterSpecification collectionFilterKeyParameterSpec = new CollectionFilterKeyParameterSpecification(
 						collectionFilterRole, collectionFilterKeyType, positionalParameterCount++
 				);
 				collectionFilterKeyParameter.setHqlParameterSpecification( collectionFilterKeyParameterSpec );
 				parameters.add( collectionFilterKeyParameterSpec );
 			}
 		}
 	}
 
 	public boolean isFilter() {
 		return collectionFilterRole != null;
 	}
 
 	public String getCollectionFilterRole() {
 		return collectionFilterRole;
 	}
 
 	public boolean isInEntityGraph() {
 		return inEntityGraph;
 	}
 
 	public SessionFactoryHelper getSessionFactoryHelper() {
 		return sessionFactoryHelper;
 	}
 
 	public Map getTokenReplacements() {
 		return tokenReplacements;
 	}
 
 	public AliasGenerator getAliasGenerator() {
 		return aliasGenerator;
 	}
 
 	public FromClause getCurrentFromClause() {
 		return currentFromClause;
 	}
 
 	public ParseErrorHandler getParseErrorHandler() {
 		return parseErrorHandler;
 	}
 
 	@Override
 	public void reportError(RecognitionException e) {
 		parseErrorHandler.reportError( e ); // Use the delegate.
 	}
 
 	@Override
 	public void reportError(String s) {
 		parseErrorHandler.reportError( s ); // Use the delegate.
 	}
 
 	@Override
 	public void reportWarning(String s) {
 		parseErrorHandler.reportWarning( s );
 	}
 
 	/**
 	 * Returns the set of unique query spaces (a.k.a.
 	 * table names) that occurred in the query.
 	 *
 	 * @return A set of table names (Strings).
 	 */
 	public Set<Serializable> getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	protected AST createFromElement(String path, AST alias, AST propertyFetch) throws SemanticException {
 		FromElement fromElement = currentFromClause.addFromElement( path, alias );
 		fromElement.setAllPropertyFetch( propertyFetch != null );
 		return fromElement;
 	}
 
 	@Override
 	protected AST createFromFilterElement(AST filterEntity, AST alias) throws SemanticException {
 		FromElement fromElement = currentFromClause.addFromElement( filterEntity.getText(), alias );
 		FromClause fromClause = fromElement.getFromClause();
 		QueryableCollection persister = sessionFactoryHelper.getCollectionPersister( collectionFilterRole );
 		// Get the names of the columns used to link between the collection
 		// owner and the collection elements.
 		String[] keyColumnNames = persister.getKeyColumnNames();
 		String fkTableAlias = persister.isOneToMany()
 				? fromElement.getTableAlias()
 				: fromClause.getAliasGenerator().createName( collectionFilterRole );
 		JoinSequence join = sessionFactoryHelper.createJoinSequence();
 		join.setRoot( persister, fkTableAlias );
 		if ( !persister.isOneToMany() ) {
 			join.addJoin(
 					(AssociationType) persister.getElementType(),
 					fromElement.getTableAlias(),
 					JoinType.INNER_JOIN,
 					persister.getElementColumnNames( fkTableAlias )
 			);
 		}
 		join.addCondition( fkTableAlias, keyColumnNames, " = ?" );
 		fromElement.setJoinSequence( join );
 		fromElement.setFilter( true );
 		LOG.debug( "createFromFilterElement() : processed filter FROM element." );
 		return fromElement;
 	}
 
 	@Override
 	protected void createFromJoinElement(
 			AST path,
 			AST alias,
 			int joinType,
 			AST fetchNode,
 			AST propertyFetch,
 			AST with) throws SemanticException {
 		boolean fetch = fetchNode != null;
 		if ( fetch && isSubQuery() ) {
 			throw new QueryException( "fetch not allowed in subquery from-elements" );
 		}
 
 
 		// the incoming "path" can be either:
 		//		1) an implicit join path (join p.address.city)
 		// 		2) an entity-join (join com.acme.User)
 		//
 		// so make the proper interpretation here...
 
 		final EntityPersister entityJoinReferencedPersister = resolveEntityJoinReferencedPersister( path );
 		if ( entityJoinReferencedPersister != null ) {
 			// `path` referenced an entity
 			final EntityJoinFromElement join = createEntityJoin(
 					entityJoinReferencedPersister,
 					alias,
 					joinType,
 					propertyFetch,
 					with
 			);
 
 			( (FromReferenceNode) path ).setFromElement( join );
 		}
 		else {
 			if ( path.getType() != SqlTokenTypes.DOT ) {
 				throw new SemanticException( "Path expected for join!" );
 			}
 
 			DotNode dot = (DotNode) path;
 			JoinType hibernateJoinType = JoinProcessor.toHibernateJoinType( joinType );
 			dot.setJoinType( hibernateJoinType );    // Tell the dot node about the join type.
 			dot.setFetch( fetch );
 			// Generate an explicit join for the root dot node.   The implied joins will be collected and passed up
 			// to the root dot node.
 			dot.resolve( true, false, alias == null ? null : alias.getText() );
 
 			final FromElement fromElement;
 			if ( dot.getDataType() != null && dot.getDataType().isComponentType() ) {
 				if ( dot.getDataType().isAnyType() ) {
 					throw new SemanticException( "An AnyType attribute cannot be join fetched" );
 					// ^^ because the discriminator (aka, the "meta columns") must be known to the SQL in
 					// 		a non-parameterized way.
 				}
 				FromElementFactory factory = new FromElementFactory(
 						getCurrentFromClause(),
 						dot.getLhs().getFromElement(),
 						dot.getPropertyPath(),
 						alias == null ? null : alias.getText(),
 						null,
 						false
 				);
 				fromElement = factory.createComponentJoin( (CompositeType) dot.getDataType() );
 			}
 			else {
 				fromElement = dot.getImpliedJoin();
 				fromElement.setAllPropertyFetch( propertyFetch != null );
 
 				if ( with != null ) {
 					if ( fetch ) {
 						throw new SemanticException( "with-clause not allowed on fetched associations; use filters" );
 					}
 					handleWithFragment( fromElement, with );
 				}
 			}
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debug(
 						"createFromJoinElement() : "
 								+ getASTPrinter().showAsString( fromElement, "-- join tree --" )
 				);
 			}
 		}
 	}
 
 	private EntityPersister resolveEntityJoinReferencedPersister(AST path) {
 		if ( path.getType() == IDENT ) {
 			final IdentNode pathIdentNode = (IdentNode) path;
 			String name = path.getText();
 			if ( name == null ) {
 				name = pathIdentNode.getOriginalText();
 			}
 			return sessionFactoryHelper.findEntityPersisterByName( name );
 		}
 		else if ( path.getType() == DOT ) {
 			final String pathText = ASTUtil.getPathText( path );
 			return sessionFactoryHelper.findEntityPersisterByName( pathText );
 		}
 		return null;
 	}
 
 	@Override
 	protected void finishFromClause(AST fromClause) throws SemanticException {
 		( (FromClause) fromClause ).finishInit();
 	}
 
 	private EntityJoinFromElement createEntityJoin(
 			EntityPersister entityPersister,
 			AST aliasNode,
 			int joinType,
 			AST propertyFetch,
 			AST with) throws SemanticException {
 		final String alias = aliasNode == null ? null : aliasNode.getText();
 		LOG.debugf( "Creating entity-join FromElement [%s -> %s]", alias, entityPersister.getEntityName() );
 		EntityJoinFromElement join = new EntityJoinFromElement(
 				this,
 				getCurrentFromClause(),
 				entityPersister,
 				JoinProcessor.toHibernateJoinType( joinType ),
 				propertyFetch != null,
 				alias
 		);
 
 		if ( with != null ) {
 			handleWithFragment( join, with );
 		}
 
 		return join;
 	}
 
 	private void handleWithFragment(FromElement fromElement, AST hqlWithNode) throws SemanticException {
 		try {
 			withClause( hqlWithNode );
 			AST hqlSqlWithNode = returnAST;
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debug(
 						"handleWithFragment() : " + getASTPrinter().showAsString(
 								hqlSqlWithNode,
 								"-- with clause --"
 						)
 				);
 			}
 			WithClauseVisitor visitor = new WithClauseVisitor( fromElement, queryTranslatorImpl );
 			NodeTraverser traverser = new NodeTraverser( visitor );
 			traverser.traverseDepthFirst( hqlSqlWithNode );
 
 			SqlGenerator sql = new SqlGenerator( getSessionFactoryHelper().getFactory() );
 			sql.whereExpr( hqlSqlWithNode.getFirstChild() );
 
 			fromElement.setWithClauseFragment( "(" + sql.getSQL() + ")" );
 		}
 		catch (SemanticException e) {
 			throw e;
 		}
 		catch (InvalidWithClauseException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new SemanticException( e.getMessage() );
 		}
 	}
 
 	private static class WithClauseVisitor implements NodeTraverser.VisitationStrategy {
 		private final FromElement joinFragment;
 		private final QueryTranslatorImpl queryTranslatorImpl;
 
 		private FromElement referencedFromElement;
 		private String joinAlias;
 
 		public WithClauseVisitor(FromElement fromElement, QueryTranslatorImpl queryTranslatorImpl) {
 			this.joinFragment = fromElement;
 			this.queryTranslatorImpl = queryTranslatorImpl;
 		}
 
 		public void visit(AST node) {
 			// TODO : currently expects that the individual with expressions apply to the same sql table join.
 			//      This may not be the case for joined-subclass where the property values
 			//      might be coming from different tables in the joined hierarchy.  At some
 			//      point we should expand this to support that capability.  However, that has
 			//      some difficulties:
 			//          1) the biggest is how to handle ORs when the individual comparisons are
 			//              linked to different sql joins.
 			//          2) here we would need to track each comparison individually, along with
 			//              the join alias to which it applies and then pass that information
 			//              back to the FromElement so it can pass it along to the JoinSequence
 			if ( node instanceof DotNode ) {
 				DotNode dotNode = (DotNode) node;
 				FromElement fromElement = dotNode.getFromElement();
 				if ( referencedFromElement != null ) {
 //					if ( fromElement != referencedFromElement ) {
 //						throw new HibernateException( "with-clause referenced two different from-clause elements" );
 //					}
 				}
 				else {
 					referencedFromElement = fromElement;
 					joinAlias = extractAppliedAlias( dotNode );
 					// TODO : temporary
 					//      needed because currently persister is the one that
 					// creates and renders the join fragments for inheritance
 					//      hierarchies...
 //					if ( !joinAlias.equals( referencedFromElement.getTableAlias() ) ) {
 //						throw new InvalidWithClauseException(
 //								"with clause can only reference columns in the driving table",
 //								queryTranslatorImpl.getQueryString()
 //						);
 //					}
 				}
 			}
 			else if ( node instanceof ParameterNode ) {
 				applyParameterSpecification( ( (ParameterNode) node ).getHqlParameterSpecification() );
 			}
 			else if ( node instanceof ParameterContainer ) {
 				applyParameterSpecifications( (ParameterContainer) node );
 			}
 		}
 
 		private void applyParameterSpecifications(ParameterContainer parameterContainer) {
 			if ( parameterContainer.hasEmbeddedParameters() ) {
 				ParameterSpecification[] specs = parameterContainer.getEmbeddedParameters();
 				for ( ParameterSpecification spec : specs ) {
 					applyParameterSpecification( spec );
 				}
 			}
 		}
 
 		private void applyParameterSpecification(ParameterSpecification paramSpec) {
 			joinFragment.addEmbeddedParameter( paramSpec );
 		}
 
 		private String extractAppliedAlias(DotNode dotNode) {
 			return dotNode.getText().substring( 0, dotNode.getText().indexOf( '.' ) );
 		}
 
 		public FromElement getReferencedFromElement() {
 			return referencedFromElement;
 		}
 
 		public String getJoinAlias() {
 			return joinAlias;
 		}
 	}
 
 	/**
 	 * Sets the current 'FROM' context.
 	 *
 	 * @param fromNode The new 'FROM' context.
 	 * @param inputFromNode The from node from the input AST.
 	 */
 	@Override
 	protected void pushFromClause(AST fromNode, AST inputFromNode) {
 		FromClause newFromClause = (FromClause) fromNode;
 		newFromClause.setParentFromClause( currentFromClause );
 		currentFromClause = newFromClause;
 	}
 
 	/**
 	 * Returns to the previous 'FROM' context.
 	 */
 	private void popFromClause() {
 		currentFromClause = currentFromClause.getParentFromClause();
 	}
 
 	@Override
 	protected void lookupAlias(AST aliasRef)
 			throws SemanticException {
 		FromElement alias = currentFromClause.getFromElement( aliasRef.getText() );
 		FromReferenceNode aliasRefNode = (FromReferenceNode) aliasRef;
 		aliasRefNode.setFromElement( alias );
 	}
 
 	@Override
 	protected void setImpliedJoinType(int joinType) {
 		impliedJoinType = JoinProcessor.toHibernateJoinType( joinType );
 	}
 
 	public JoinType getImpliedJoinType() {
 		return impliedJoinType;
 	}
 
 	@Override
 	protected AST lookupProperty(AST dot, boolean root, boolean inSelect) throws SemanticException {
 		DotNode dotNode = (DotNode) dot;
 		FromReferenceNode lhs = dotNode.getLhs();
 		AST rhs = lhs.getNextSibling();
 
 		// this used to be a switch statement based on the rhs's node type
 		//		expecting it to be SqlTokenTypes.ELEMENTS or
 		//		SqlTokenTypes.INDICES in the cases where the re-arranging is needed
 		//
 		// In such cases it additionally expects the RHS to be a CollectionFunction node.
 		//
 		// However, in my experience these assumptions sometimes did not works as sometimes the node
 		// 		types come in with the node type WEIRD_IDENT.  What this does now is to:
 		//			1) see if the LHS is a collection
 		//			2) see if the RHS is a reference to one of the "collection properties".
 		//		if both are true, we log a deprecation warning
 		if ( lhs.getDataType() != null
 				&& lhs.getDataType().isCollectionType() ) {
 			if ( CollectionProperties.isCollectionProperty( rhs.getText() ) ) {
 				DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfCollectionPropertiesInHql(
 						rhs.getText(),
 						lhs.getPath()
 				);
 			}
 
 			// perform the re-arrangement
 			if ( CollectionPropertyNames.COLLECTION_INDICES.equalsIgnoreCase( rhs.getText() )
 					|| CollectionPropertyNames.COLLECTION_ELEMENTS.equalsIgnoreCase( rhs.getText() ) ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf(
 							"lookupProperty() %s => %s(%s)",
 							dotNode.getPath(),
 							rhs.getText(),
 							lhs.getPath()
 					);
 				}
 
 				final CollectionFunction f;
 				if ( rhs instanceof CollectionFunction ) {
 					f = (CollectionFunction) rhs;
 				}
 				else {
 					f = new CollectionFunction();
 					f.initialize( SqlTokenTypes.METHOD_CALL, rhs.getText() );
 					f.initialize( this );
 				}
 
 				// Re-arrange the tree so that the collection function is the root and the lhs is the path.
 				f.setFirstChild( lhs );
 				lhs.setNextSibling( null );
 				dotNode.setFirstChild( f );
 				resolve( lhs );            // Don't forget to resolve the argument!
 				f.resolve( inSelect );    // Resolve the collection function now.
 				return f;
 			}
 		}
 
 		// otherwise, resolve the path and return it
 		dotNode.resolveFirstChild();
 		return dotNode;
 	}
 
 	@Override
 	protected boolean isNonQualifiedPropertyRef(AST ident) {
 		final String identText = ident.getText();
 		if ( currentFromClause.isFromElementAlias( identText ) ) {
 			return false;
 		}
 
 		List fromElements = currentFromClause.getExplicitFromElements();
 		if ( fromElements.size() == 1 ) {
 			final FromElement fromElement = (FromElement) fromElements.get( 0 );
 			try {
 				LOG.tracev( "Attempting to resolve property [{0}] as a non-qualified ref", identText );
 				return fromElement.getPropertyMapping( identText ).toType( identText ) != null;
 			}
 			catch (QueryException e) {
 				// Should mean that no such property was found
 			}
 		}
 
 		return false;
 	}
 
 	@Override
 	protected AST lookupNonQualifiedProperty(AST property) throws SemanticException {
 		final FromElement fromElement = (FromElement) currentFromClause.getExplicitFromElements().get( 0 );
 		AST syntheticDotNode = generateSyntheticDotNodeForNonQualifiedPropertyRef( property, fromElement );
 		return lookupProperty( syntheticDotNode, false, getCurrentClauseType() == HqlSqlTokenTypes.SELECT );
 	}
 
 	private AST generateSyntheticDotNodeForNonQualifiedPropertyRef(AST property, FromElement fromElement) {
 		AST dot = getASTFactory().create( DOT, "{non-qualified-property-ref}" );
 		// TODO : better way?!?
 		( (DotNode) dot ).setPropertyPath( ( (FromReferenceNode) property ).getPath() );
 
 		IdentNode syntheticAlias = (IdentNode) getASTFactory().create( IDENT, "{synthetic-alias}" );
 		syntheticAlias.setFromElement( fromElement );
 		syntheticAlias.setResolved();
 
 		dot.setFirstChild( syntheticAlias );
 		dot.addChild( property );
 
 		return dot;
 	}
 
 	@Override
 	protected void processQuery(AST select, AST query) throws SemanticException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "processQuery() : %s", query.toStringTree() );
 		}
 
 		try {
 			QueryNode qn = (QueryNode) query;
 
 			// Was there an explicit select expression?
 			boolean explicitSelect = select != null && select.getNumberOfChildren() > 0;
 
 			// Add in the EntityGraph attribute nodes.
 			if ( queryTranslatorImpl.getEntityGraphQueryHint() != null ) {
 				final boolean oldInEntityGraph = inEntityGraph;
 				try {
 					inEntityGraph = true;
 					qn.getFromClause().getFromElements().addAll(
 							queryTranslatorImpl.getEntityGraphQueryHint().toFromElements( qn.getFromClause(), this )
 					);
 				}
 				finally {
 					inEntityGraph = oldInEntityGraph;
 				}
 			}
 
 			if ( !explicitSelect ) {
 				// No explicit select expression; render the id and properties
 				// projection lists for every persister in the from clause into
 				// a single 'token node'.
 				//TODO: the only reason we need this stuff now is collection filters,
 				//      we should get rid of derived select clause completely!
 				createSelectClauseFromFromClause( qn );
 			}
 			else {
 				// Use the explicitly declared select expression; determine the
 				// return types indicated by each select token
 				useSelectClause( select );
 			}
 
 			// After that, process the JOINs.
 			// Invoke a delegate to do the work, as this is farily complex.
 			JoinProcessor joinProcessor = new JoinProcessor( this );
 			joinProcessor.processJoins( qn );
 
 			// Attach any mapping-defined "ORDER BY" fragments
 			Iterator itr = qn.getFromClause().getProjectionList().iterator();
 			while ( itr.hasNext() ) {
 				final FromElement fromElement = (FromElement) itr.next();
 //			if ( fromElement.isFetch() && fromElement.isCollectionJoin() ) {
 				if ( fromElement.isFetch() && fromElement.getQueryableCollection() != null ) {
 					// Does the collection referenced by this FromElement
 					// specify an order-by attribute?  If so, attach it to
 					// the query's order-by
 					if ( fromElement.getQueryableCollection().hasOrdering() ) {
 						String orderByFragment = fromElement
 								.getQueryableCollection()
 								.getSQLOrderByString( fromElement.getCollectionTableAlias() );
 						qn.getOrderByClause().addOrderFragment( orderByFragment );
 					}
 					if ( fromElement.getQueryableCollection().hasManyToManyOrdering() ) {
 						String orderByFragment = fromElement.getQueryableCollection()
 								.getManyToManyOrderByString( fromElement.getTableAlias() );
 						qn.getOrderByClause().addOrderFragment( orderByFragment );
 					}
 				}
 			}
 		}
 		finally {
 			popFromClause();
 		}
 	}
 
 	protected void postProcessDML(RestrictableStatement statement) throws SemanticException {
 		statement.getFromClause().resolve();
 
 		FromElement fromElement = (FromElement) statement.getFromClause().getFromElements().get( 0 );
 		Queryable persister = fromElement.getQueryable();
 		// Make #@%$^#^&# sure no alias is applied to the table name
 		fromElement.setText( persister.getTableName() );
 
 //		// append any filter fragments; the EMPTY_MAP is used under the assumption that
 //		// currently enabled filters should not affect this process
 //		if ( persister.getDiscriminatorType() != null ) {
 //			new SyntheticAndFactory( getASTFactory() ).addDiscriminatorWhereFragment(
 //			        statement,
 //			        persister,
 //			        java.util.Collections.EMPTY_MAP,
 //			        fromElement.getTableAlias()
 //			);
 //		}
 		if ( persister.getDiscriminatorType() != null || !queryTranslatorImpl.getEnabledFilters().isEmpty() ) {
 			new SyntheticAndFactory( this ).addDiscriminatorWhereFragment(
 					statement,
 					persister,
 					queryTranslatorImpl.getEnabledFilters(),
 					fromElement.getTableAlias()
 			);
 		}
 
 	}
 
 	@Override
 	protected void postProcessUpdate(AST update) throws SemanticException {
 		UpdateStatement updateStatement = (UpdateStatement) update;
 
 		postProcessDML( updateStatement );
 	}
 
 	@Override
 	protected void postProcessDelete(AST delete) throws SemanticException {
 		postProcessDML( (DeleteStatement) delete );
 	}
 
 	@Override
 	protected void postProcessInsert(AST insert) throws SemanticException, QueryException {
 		InsertStatement insertStatement = (InsertStatement) insert;
 		insertStatement.validate();
 
 		SelectClause selectClause = insertStatement.getSelectClause();
 		Queryable persister = insertStatement.getIntoClause().getQueryable();
 
 		if ( !insertStatement.getIntoClause().isExplicitIdInsertion() ) {
 			// the insert did not explicitly reference the id.  See if
 			//		1) that is allowed
 			//		2) whether we need to alter the SQL tree to account for id
 			final IdentifierGenerator generator = persister.getIdentifierGenerator();
 			if ( !BulkInsertionCapableIdentifierGenerator.class.isInstance( generator ) ) {
 				throw new QueryException(
 						"Invalid identifier generator encountered for implicit id handling as part of bulk insertions"
 				);
 			}
 			final BulkInsertionCapableIdentifierGenerator capableGenerator =
 					BulkInsertionCapableIdentifierGenerator.class.cast( generator );
 			if ( !capableGenerator.supportsBulkInsertionIdentifierGeneration() ) {
 				throw new QueryException(
 						"Identifier generator reported it does not support implicit id handling as part of bulk insertions"
 				);
 			}
 
 			final String fragment = capableGenerator.determineBulkInsertionIdentifierGenerationSelectFragment(
 					sessionFactoryHelper.getFactory().getDialect()
 			);
 			if ( fragment != null ) {
 				// we got a fragment from the generator, so alter the sql tree...
 				//
 				// first, wrap the fragment as a node
 				AST fragmentNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, fragment );
 				// next, rearrange the SQL tree to add the fragment node as the first select expression
 				AST originalFirstSelectExprNode = selectClause.getFirstChild();
 				selectClause.setFirstChild( fragmentNode );
 				fragmentNode.setNextSibling( originalFirstSelectExprNode );
 				// finally, prepend the id column name(s) to the insert-spec
 				insertStatement.getIntoClause().prependIdColumnSpec();
 			}
 		}
 
 		if ( sessionFactoryHelper.getFactory().getDialect().supportsParametersInInsertSelect() ) {
 			AST child = selectClause.getFirstChild();
 			int i = 0;
 			while ( child != null ) {
 				if ( child instanceof ParameterNode ) {
 					// infer the parameter type from the type listed in the INSERT INTO clause
 					( (ParameterNode) child ).setExpectedType(
 							insertStatement.getIntoClause()
 									.getInsertionTypes()[selectClause.getParameterPositions().get( i )]
 					);
 					i++;
 				}
 				child = child.getNextSibling();
 			}
 		}
 
 		final boolean includeVersionProperty = persister.isVersioned() &&
 				!insertStatement.getIntoClause().isExplicitVersionInsertion() &&
 				persister.isVersionPropertyInsertable();
 		if ( includeVersionProperty ) {
 			// We need to seed the version value as part of this bulk insert
 			VersionType versionType = persister.getVersionType();
 			AST versionValueNode = null;
 
 			if ( sessionFactoryHelper.getFactory().getDialect().supportsParametersInInsertSelect() ) {
 				int[] sqlTypes = versionType.sqlTypes( sessionFactoryHelper.getFactory() );
 				if ( sqlTypes == null || sqlTypes.length == 0 ) {
 					throw new IllegalStateException( versionType.getClass() + ".sqlTypes() returns null or empty array" );
 				}
 				if ( sqlTypes.length > 1 ) {
 					throw new IllegalStateException(
 							versionType.getClass() +
 									".sqlTypes() returns > 1 element; only single-valued versions are allowed."
 					);
 				}
 				versionValueNode = getASTFactory().create( HqlSqlTokenTypes.PARAM, "?" );
 				ParameterSpecification paramSpec = new VersionTypeSeedParameterSpecification( versionType );
 				( (ParameterNode) versionValueNode ).setHqlParameterSpecification( paramSpec );
 				parameters.add( 0, paramSpec );
 
 				if ( sessionFactoryHelper.getFactory().getDialect().requiresCastingOfParametersInSelectClause() ) {
 					// we need to wrtap the param in a cast()
 					MethodNode versionMethodNode = (MethodNode) getASTFactory().create(
 							HqlSqlTokenTypes.METHOD_CALL,
 							"("
 					);
 					AST methodIdentNode = getASTFactory().create( HqlSqlTokenTypes.IDENT, "cast" );
 					versionMethodNode.addChild( methodIdentNode );
 					versionMethodNode.initializeMethodNode( methodIdentNode, true );
 					AST castExprListNode = getASTFactory().create( HqlSqlTokenTypes.EXPR_LIST, "exprList" );
 					methodIdentNode.setNextSibling( castExprListNode );
 					castExprListNode.addChild( versionValueNode );
 					versionValueNode.setNextSibling(
 							getASTFactory().create(
 									HqlSqlTokenTypes.IDENT,
 									sessionFactoryHelper.getFactory().getDialect().getTypeName( sqlTypes[0] )
 							)
 					);
 					processFunction( versionMethodNode, true );
 					versionValueNode = versionMethodNode;
 				}
 			}
 			else {
 				if ( isIntegral( versionType ) ) {
 					try {
 						Object seedValue = versionType.seed( null );
 						versionValueNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, seedValue.toString() );
 					}
 					catch (Throwable t) {
 						throw new QueryException( "could not determine seed value for version on bulk insert [" + versionType + "]" );
 					}
 				}
 				else if ( isDatabaseGeneratedTimestamp( versionType ) ) {
 					String functionName = sessionFactoryHelper.getFactory()
 							.getDialect()
 							.getCurrentTimestampSQLFunctionName();
 					versionValueNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, functionName );
 				}
 				else {
 					throw new QueryException( "cannot handle version type [" + versionType + "] on bulk inserts with dialects not supporting parameters in insert-select statements" );
 				}
 			}
 
 			AST currentFirstSelectExprNode = selectClause.getFirstChild();
 			selectClause.setFirstChild( versionValueNode );
 			versionValueNode.setNextSibling( currentFirstSelectExprNode );
 
 			insertStatement.getIntoClause().prependVersionColumnSpec();
 		}
 
 		if ( insertStatement.getIntoClause().isDiscriminated() ) {
 			String sqlValue = insertStatement.getIntoClause().getQueryable().getDiscriminatorSQLValue();
 			AST discrimValue = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, sqlValue );
 			insertStatement.getSelectClause().addChild( discrimValue );
 		}
 
 	}
 
 	private boolean isDatabaseGeneratedTimestamp(Type type) {
 		// currently only the Hibernate-supplied DbTimestampType is supported here
 		return DbTimestampType.class.isAssignableFrom( type.getClass() );
 	}
 
 	private boolean isIntegral(Type type) {
 		return Long.class.isAssignableFrom( type.getReturnedClass() )
 				|| Integer.class.isAssignableFrom( type.getReturnedClass() )
 				|| long.class.isAssignableFrom( type.getReturnedClass() )
 				|| int.class.isAssignableFrom( type.getReturnedClass() );
 	}
 
 	private void useSelectClause(AST select) throws SemanticException {
 		selectClause = (SelectClause) select;
 		selectClause.initializeExplicitSelectClause( currentFromClause );
 	}
 
 	private void createSelectClauseFromFromClause(QueryNode qn) throws SemanticException {
 		AST select = astFactory.create( SELECT_CLAUSE, "{derived select clause}" );
 		AST sibling = qn.getFromClause();
 		qn.setFirstChild( select );
 		select.setNextSibling( sibling );
 		selectClause = (SelectClause) select;
 		selectClause.initializeDerivedSelectClause( currentFromClause );
 		LOG.debug( "Derived SELECT clause created." );
 	}
 
 	@Override
 	protected void resolve(AST node) throws SemanticException {
+		resolve(node, null);
+	}
+
+	@Override
+	protected void resolve(AST node, AST predicateNode) throws SemanticException {
 		if ( node != null ) {
 			// This is called when it's time to fully resolve a path expression.
 			ResolvableNode r = (ResolvableNode) node;
 			if ( isInFunctionCall() ) {
 				r.resolveInFunctionCall( false, true );
 			}
 			else {
-				r.resolve( false, true );    // Generate implicit joins, only if necessary.
+				r.resolve( false, true, null, null, predicateNode );    // Generate implicit joins, only if necessary.
 			}
 		}
 	}
 
 	@Override
 	protected void resolveSelectExpression(AST node) throws SemanticException {
 		// This is called when it's time to fully resolve a path expression.
 		int type = node.getType();
 		switch ( type ) {
 			case DOT: {
 				DotNode dot = (DotNode) node;
 				dot.resolveSelectExpression();
 				break;
 			}
 			case ALIAS_REF: {
 				// Notify the FROM element that it is being referenced by the select.
 				FromReferenceNode aliasRefNode = (FromReferenceNode) node;
 				//aliasRefNode.resolve( false, false, aliasRefNode.getText() ); //TODO: is it kosher to do it here?
 				aliasRefNode.resolve( false, false ); //TODO: is it kosher to do it here?
 				FromElement fromElement = aliasRefNode.getFromElement();
 				if ( fromElement != null ) {
 					fromElement.setIncludeSubclasses( true );
 				}
 				break;
 			}
 			default: {
 				break;
 			}
 		}
 	}
 
 	@Override
 	protected void beforeSelectClause() throws SemanticException {
 		// Turn off includeSubclasses on all FromElements.
 		FromClause from = getCurrentFromClause();
 		List fromElements = from.getFromElements();
 		for ( Iterator iterator = fromElements.iterator(); iterator.hasNext(); ) {
 			FromElement fromElement = (FromElement) iterator.next();
 			fromElement.setIncludeSubclasses( false );
 		}
 	}
 
 	@Override
 	protected AST generatePositionalParameter(AST inputNode) throws SemanticException {
 		if ( namedParameters.size() > 0 ) {
 			throw new SemanticException(
 					"cannot define positional parameter afterQuery any named parameters have been defined"
 			);
 		}
 		LOG.warnf(
 				"[DEPRECATION] Encountered positional parameter near line %s, column %s in HQL: [%s].  Positional parameter " +
 						"are considered deprecated; use named parameters or JPA-style positional parameters instead.",
 				inputNode.getLine(),
 				inputNode.getColumn(),
 				queryTranslatorImpl.getQueryString()
 		);
 		ParameterNode parameter = (ParameterNode) astFactory.create( PARAM, "?" );
 		PositionalParameterSpecification paramSpec = new PositionalParameterSpecification(
 				inputNode.getLine(),
 				inputNode.getColumn(),
 				positionalParameterCount++
 		);
 		parameter.setHqlParameterSpecification( paramSpec );
 		parameters.add( paramSpec );
 		return parameter;
 	}
 
 	@Override
 	protected AST generateNamedParameter(AST delimiterNode, AST nameNode) throws SemanticException {
 		String name = nameNode.getText();
 		trackNamedParameterPositions( name );
 
 		// create the node initially with the param name so that it shows
 		// appropriately in the "original text" attribute
 		ParameterNode parameter = (ParameterNode) astFactory.create( NAMED_PARAM, name );
 		parameter.setText( "?" );
 
 		NamedParameterSpecification paramSpec = new NamedParameterSpecification(
 				delimiterNode.getLine(),
 				delimiterNode.getColumn(),
 				name
 		);
 		parameter.setHqlParameterSpecification( paramSpec );
 		parameters.add( paramSpec );
 		return parameter;
 	}
 
 	private void trackNamedParameterPositions(String name) {
 		Integer loc = parameterCount++;
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			namedParameters.put( name, loc );
 		}
 		else if ( o instanceof Integer ) {
 			ArrayList list = new ArrayList( 4 );
 			list.add( o );
 			list.add( loc );
 			namedParameters.put( name, list );
 		}
 		else {
 			( (ArrayList) o ).add( loc );
 		}
 	}
 
 	@Override
 	protected void processConstant(AST constant) throws SemanticException {
 		literalProcessor.processConstant(
 				constant,
 				true
 		);  // Use the delegate, resolve identifiers as FROM element aliases.
 	}
 
 	@Override
 	protected void processBoolean(AST constant) throws SemanticException {
 		literalProcessor.processBoolean( constant );  // Use the delegate.
 	}
 
 	@Override
 	protected void processNumericLiteral(AST literal) {
 		literalProcessor.processNumeric( literal );
 	}
 
 	@Override
 	protected void processIndex(AST indexOp) throws SemanticException {
 		IndexNode indexNode = (IndexNode) indexOp;
 		indexNode.resolve( true, true );
 	}
 
 	@Override
 	protected void processFunction(AST functionCall, boolean inSelect) throws SemanticException {
 		MethodNode methodNode = (MethodNode) functionCall;
 		methodNode.resolve( inSelect );
 	}
 
 	@Override
 	protected void processCastFunction(AST castFunctionCall, boolean inSelect) throws SemanticException {
 		CastFunctionNode castFunctionNode = (CastFunctionNode) castFunctionCall;
 		castFunctionNode.resolve( inSelect );
 	}
 
 	@Override
 	protected void processAggregation(AST node, boolean inSelect) throws SemanticException {
 		AggregateNode aggregateNode = (AggregateNode) node;
 		aggregateNode.resolve();
 	}
 
 	@Override
 	protected void processConstructor(AST constructor) throws SemanticException {
 		ConstructorNode constructorNode = (ConstructorNode) constructor;
 		constructorNode.prepare();
 	}
 
 	@Override
 	protected void setAlias(AST selectExpr, AST ident) {
 		( (SelectExpression) selectExpr ).setAlias( ident.getText() );
 		// only put the alias (i.e., result variable) in selectExpressionsByResultVariable
 		// if is not defined in a subquery.
 		if ( !isSubQuery() ) {
 			selectExpressionsByResultVariable.put( ident.getText(), (SelectExpression) selectExpr );
 		}
 	}
 
 	@Override
 	protected boolean isOrderExpressionResultVariableRef(AST orderExpressionNode) throws SemanticException {
 		// ORDER BY is not supported in a subquery
 		// TODO: should an exception be thrown if an ORDER BY is in a subquery?
 		if ( !isSubQuery() &&
 				orderExpressionNode.getType() == IDENT &&
 				selectExpressionsByResultVariable.containsKey( orderExpressionNode.getText() ) ) {
 			return true;
 		}
 		return false;
 	}
 
 	@Override
 	protected void handleResultVariableRef(AST resultVariableRef) throws SemanticException {
 		if ( isSubQuery() ) {
 			throw new SemanticException(
 					"References to result variables in subqueries are not supported."
 			);
 		}
 		( (ResultVariableRefNode) resultVariableRef ).setSelectExpression(
 				selectExpressionsByResultVariable.get( resultVariableRef.getText() )
 		);
 	}
 
 	/**
 	 * Returns the locations of all occurrences of the named parameter.
 	 */
 	public int[] getNamedParameterLocations(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			throw new QueryException(
 					QueryTranslator.ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name,
 					queryTranslatorImpl.getQueryString()
 			);
 		}
 		if ( o instanceof Integer ) {
 			return new int[] {(Integer) o};
 		}
 		else {
 			return ArrayHelper.toIntArray( (ArrayList) o );
 		}
 	}
 
 	public void addQuerySpaces(Serializable[] spaces) {
 		querySpaces.addAll( Arrays.asList( spaces ) );
 	}
 
 	public Type[] getReturnTypes() {
 		return selectClause.getQueryReturnTypes();
 	}
 
 	public String[] getReturnAliases() {
 		return selectClause.getQueryReturnAliases();
 	}
 
 	public SelectClause getSelectClause() {
 		return selectClause;
 	}
 
 	public FromClause getFinalFromClause() {
 		FromClause top = currentFromClause;
 		while ( top.getParentFromClause() != null ) {
 			top = top.getParentFromClause();
 		}
 		return top;
 	}
 
 	public boolean isShallowQuery() {
 		// select clauses for insert statements should alwasy be treated as shallow
 		return getStatementType() == INSERT || queryTranslatorImpl.isShallowQuery();
 	}
 
 	public Map getEnabledFilters() {
 		return queryTranslatorImpl.getEnabledFilters();
 	}
 
 	public LiteralProcessor getLiteralProcessor() {
 		return literalProcessor;
 	}
 
 	public ASTPrinter getASTPrinter() {
 		return printer;
 	}
 
 	public ArrayList<ParameterSpecification> getParameters() {
 		return parameters;
 	}
 
 	public int getNumberOfParametersInSetClause() {
 		return numberOfParametersInSetClause;
 	}
 
 	@Override
 	protected void evaluateAssignment(AST eq) throws SemanticException {
 		prepareLogicOperator( eq );
 		Queryable persister = getCurrentFromClause().getFromElement().getQueryable();
 		evaluateAssignment( eq, persister, -1 );
 	}
 
 	private void evaluateAssignment(AST eq, Queryable persister, int targetIndex) {
 		if ( persister.isMultiTable() ) {
 			// no need to even collect this information if the persister is considered multi-table
 			AssignmentSpecification specification = new AssignmentSpecification( eq, persister );
 			if ( targetIndex >= 0 ) {
 				assignmentSpecifications.add( targetIndex, specification );
 			}
 			else {
 				assignmentSpecifications.add( specification );
 			}
 			numberOfParametersInSetClause += specification.getParameters().length;
 		}
 	}
 
 	public ArrayList getAssignmentSpecifications() {
 		return assignmentSpecifications;
 	}
 
 	@Override
 	protected AST createIntoClause(String path, AST propertySpec) throws SemanticException {
 		Queryable persister = (Queryable) getSessionFactoryHelper().requireClassPersister( path );
 
 		IntoClause intoClause = (IntoClause) getASTFactory().create( INTO, persister.getEntityName() );
 		intoClause.setFirstChild( propertySpec );
 		intoClause.initialize( persister );
 
 		addQuerySpaces( persister.getQuerySpaces() );
 
 		return intoClause;
 	}
 
 	@Override
 	protected void prepareVersioned(AST updateNode, AST versioned) throws SemanticException {
 		UpdateStatement updateStatement = (UpdateStatement) updateNode;
 		FromClause fromClause = updateStatement.getFromClause();
 		if ( versioned != null ) {
 			// Make sure that the persister is versioned
 			Queryable persister = fromClause.getFromElement().getQueryable();
 			if ( !persister.isVersioned() ) {
 				throw new SemanticException( "increment option specified for update of non-versioned entity" );
 			}
 
 			VersionType versionType = persister.getVersionType();
 			if ( versionType instanceof UserVersionType ) {
 				throw new SemanticException( "user-defined version types not supported for increment option" );
 			}
 
 			AST eq = getASTFactory().create( HqlSqlTokenTypes.EQ, "=" );
 			AST versionPropertyNode = generateVersionPropertyNode( persister );
 
 			eq.setFirstChild( versionPropertyNode );
 
 			AST versionIncrementNode = null;
 			if ( isTimestampBasedVersion( versionType ) ) {
 				versionIncrementNode = getASTFactory().create( HqlSqlTokenTypes.PARAM, "?" );
 				ParameterSpecification paramSpec = new VersionTypeSeedParameterSpecification( versionType );
 				( (ParameterNode) versionIncrementNode ).setHqlParameterSpecification( paramSpec );
 				parameters.add( 0, paramSpec );
 			}
 			else {
 				// Not possible to simply re-use the versionPropertyNode here as it causes
 				// OOM errors due to circularity :(
 				versionIncrementNode = getASTFactory().create( HqlSqlTokenTypes.PLUS, "+" );
 				versionIncrementNode.setFirstChild( generateVersionPropertyNode( persister ) );
 				versionIncrementNode.addChild( getASTFactory().create( HqlSqlTokenTypes.IDENT, "1" ) );
 			}
 
 			eq.addChild( versionIncrementNode );
 
 			evaluateAssignment( eq, persister, 0 );
 
 			AST setClause = updateStatement.getSetClause();
 			AST currentFirstSetElement = setClause.getFirstChild();
 			setClause.setFirstChild( eq );
 			eq.setNextSibling( currentFirstSetElement );
 		}
 	}
 
 	private boolean isTimestampBasedVersion(VersionType versionType) {
 		final Class javaType = versionType.getReturnedClass();
 		return Date.class.isAssignableFrom( javaType )
 				|| Calendar.class.isAssignableFrom( javaType );
 	}
 
 	private AST generateVersionPropertyNode(Queryable persister) throws SemanticException {
 		String versionPropertyName = persister.getPropertyNames()[persister.getVersionProperty()];
 		AST versionPropertyRef = getASTFactory().create( HqlSqlTokenTypes.IDENT, versionPropertyName );
 		AST versionPropertyNode = lookupNonQualifiedProperty( versionPropertyRef );
 		resolve( versionPropertyNode );
 		return versionPropertyNode;
 	}
 
 	@Override
 	protected void prepareLogicOperator(AST operator) throws SemanticException {
 		( (OperatorNode) operator ).initialize();
 	}
 
 	@Override
 	protected void prepareArithmeticOperator(AST operator) throws SemanticException {
 		( (OperatorNode) operator ).initialize();
 	}
 
 	@Override
 	protected void validateMapPropertyExpression(AST node) throws SemanticException {
 		try {
 			FromReferenceNode fromReferenceNode = (FromReferenceNode) node;
 			QueryableCollection collectionPersister = fromReferenceNode.getFromElement().getQueryableCollection();
 			if ( !Map.class.isAssignableFrom( collectionPersister.getCollectionType().getReturnedClass() ) ) {
 				throw new SemanticException( "node did not reference a map" );
 			}
 		}
 		catch (SemanticException se) {
 			throw se;
 		}
 		catch (Throwable t) {
 			throw new SemanticException( "node did not reference a map" );
 		}
 	}
 
 	public Set<String> getTreatAsDeclarationsByPath(String path) {
 		return hqlParser.getTreatMap().get( path );
 	}
 
 	public static void panic() {
 		throw new QueryException( "TreeWalker: panic" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java
index fe9d522e7b..63c5389b00 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java
@@ -1,126 +1,127 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.Map;
 
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Basic support for KEY, VALUE and ENTRY based "qualified identification variables".
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractMapComponentNode extends FromReferenceNode implements HqlSqlTokenTypes {
 	private FromElement mapFromElement;
 	private String[] columns;
 
 	public FromReferenceNode getMapReference() {
 		return (FromReferenceNode) getFirstChild();
 	}
 
 	public String[] getColumns() {
 		return columns;
 	}
 
 	@Override
 	public void setScalarColumnText(int i) throws SemanticException {
 		ColumnHelper.generateScalarColumns( this, getColumns(), i );
 	}
 
 	@Override
 	public void resolve(
 			boolean generateJoin,
 			boolean implicitJoin,
 			String classAlias,
-			AST parent) throws SemanticException {
+			AST parent,
+			AST parentPredicate) throws SemanticException {
 		if ( mapFromElement == null ) {
 			final FromReferenceNode mapReference = getMapReference();
 			mapReference.resolve( true, true );
 
 			FromElement sourceFromElement = null;
 			if ( isAliasRef( mapReference ) ) {
 				final QueryableCollection collectionPersister = mapReference.getFromElement().getQueryableCollection();
 				if ( Map.class.isAssignableFrom( collectionPersister.getCollectionType().getReturnedClass() ) ) {
 					sourceFromElement = mapReference.getFromElement();
 				}
 			}
 			else {
 				if ( mapReference.getDataType().isCollectionType() ) {
 					final CollectionType collectionType = (CollectionType) mapReference.getDataType();
 					if ( Map.class.isAssignableFrom( collectionType.getReturnedClass() ) ) {
 						sourceFromElement = mapReference.getFromElement();
 					}
 				}
 			}
 
 			if ( sourceFromElement == null ) {
 				throw nonMap();
 			}
 
 			mapFromElement = sourceFromElement;
 		}
 
 		setFromElement( mapFromElement );
 		setDataType( resolveType( mapFromElement.getQueryableCollection() ) );
 		this.columns = resolveColumns( mapFromElement.getQueryableCollection() );
 		initText( this.columns );
 		setFirstChild( null );
 	}
 
 	public FromElement getMapFromElement() {
 		return mapFromElement;
 	}
 
 	private boolean isAliasRef(FromReferenceNode mapReference) {
 		return ALIAS_REF == mapReference.getType();
 	}
 
 	private void initText(String[] columns) {
 		String text = StringHelper.join( ", ", columns );
 		if ( columns.length > 1 && getWalker().isComparativeExpressionClause() ) {
 			text = "(" + text + ")";
 		}
 		setText( text );
 	}
 
 	protected abstract String expressionDescription();
 	protected abstract String[] resolveColumns(QueryableCollection collectionPersister);
 	protected abstract Type resolveType(QueryableCollection collectionPersister);
 
 	protected SemanticException nonMap() {
 		return new SemanticException( expressionDescription() + " expression did not reference map property" );
 	}
 
 	@Override
 	public void resolveIndex(AST parent) throws SemanticException {
 		throw new UnsupportedOperationException( expressionDescription() + " expression cannot be the source for an index operation" );
 	}
 
 	protected MapKeyEntityFromElement findOrAddMapKeyEntityFromElement(QueryableCollection collectionPersister) {
 		if ( !collectionPersister.getIndexType().isEntityType() ) {
 			return null;
 		}
 
 
 		for ( FromElement destination : getFromElement().getDestinations() ) {
 			if ( destination instanceof MapKeyEntityFromElement ) {
 				return (MapKeyEntityFromElement) destination;
 			}
 		}
 
 		return MapKeyEntityFromElement.buildKeyJoin( getFromElement() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
index a411397fda..f2e235bc69 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
@@ -1,760 +1,765 @@
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
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.AbstractEntityPersister;
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
 
-	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
+	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate)
 			throws SemanticException {
 		// If this dot has already been resolved, stop now.
 		if ( isResolved() ) {
 			return;
 		}
 
 		Type propertyType = prepareLhs(); // Prepare the left hand side and get the data type.
 
 		if ( parent == null && AbstractEntityPersister.ENTITY_CLASS.equals( propertyName ) ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfClassEntityTypeSelector( getLhs().getPath() );
 		}
 
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
-			dereferenceEntity( (EntityType) propertyType, implicitJoin, classAlias, generateJoin, parent );
+			dereferenceEntity( (EntityType) propertyType, implicitJoin, classAlias, generateJoin, parent, parentPredicate );
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
-			AST parent) throws SemanticException {
+			AST parent,
+			AST parentPredicate) throws SemanticException {
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
+		else if ( parentPredicate != null ) {
+			// Never generate a join when we compare entities directly
+			joinIsNeeded = generateJoin;
+		}
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
 		boolean useFoundFromElement = found && canReuse( classAlias, elem );
 
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
 
 	private boolean canReuse(String classAlias, FromElement fromElement) {
 		// if the from-clauses are the same, we can be a little more aggressive in terms of what we reuse
 		if ( fromElement.getFromClause() == getWalker().getCurrentFromClause() &&
 				areSame( classAlias, fromElement.getClassAlias() )) {
 			return true;
 		}
 
 		// otherwise (subquery case) dont reuse the fromElement if we are processing the from-clause of the subquery
 		return getWalker().getCurrentClauseType() != SqlTokenTypes.FROM;
 	}
 
 	private boolean areSame(String alias1, String alias2) {
 		// again, null != null here
 		return !StringHelper.isEmpty( alias1 ) && !StringHelper.isEmpty( alias2 ) && alias1.equals( alias2 );
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
 					// Move the node up, afterQuery the origin node.
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
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromReferenceNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromReferenceNode.java
index 208f5264a7..7e44fabe97 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromReferenceNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromReferenceNode.java
@@ -1,135 +1,140 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Represents a reference to a FROM element, for example a class alias in a WHERE clause.
  *
  * @author josh
  */
 public abstract class FromReferenceNode extends AbstractSelectExpression
 		implements ResolvableNode, DisplayableNode, InitializeableNode, PathNode {
 
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( FromReferenceNode.class );
 
 	private FromElement fromElement;
 	private boolean resolved;
 
 	public static final int ROOT_LEVEL = 0;
 
 	@Override
 	public FromElement getFromElement() {
 		return fromElement;
 	}
 
 	public void setFromElement(FromElement fromElement) {
 		this.fromElement = fromElement;
 	}
 
 	/**
 	 * Resolves the left hand side of the DOT.
 	 *
 	 * @throws SemanticException
 	 */
 	public void resolveFirstChild() throws SemanticException {
 	}
 
 	@Override
 	public String getPath() {
 		return getOriginalText();
 	}
 
 	public boolean isResolved() {
 		return resolved;
 	}
 
 	public void setResolved() {
 		this.resolved = true;
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Resolved : %s -> %s", this.getPath(), this.getText() );
 		}
 	}
 
 	@Override
 	public String getDisplayText() {
 		StringBuilder buf = new StringBuilder();
 		buf.append( "{" ).append( ( fromElement == null ) ? "no fromElement" : fromElement.getDisplayText() );
 		buf.append( "}" );
 		return buf.toString();
 	}
 
 	public void recursiveResolve(int level, boolean impliedAtRoot, String classAlias) throws SemanticException {
 		recursiveResolve( level, impliedAtRoot, classAlias, this );
 	}
 
 	public void recursiveResolve(int level, boolean impliedAtRoot, String classAlias, AST parent)
 			throws SemanticException {
 		AST lhs = getFirstChild();
 		int nextLevel = level + 1;
 		if ( lhs != null ) {
 			FromReferenceNode n = (FromReferenceNode) lhs;
 			n.recursiveResolve( nextLevel, impliedAtRoot, null, this );
 		}
 		resolveFirstChild();
 		boolean impliedJoin = true;
 		if ( level == ROOT_LEVEL && !impliedAtRoot ) {
 			impliedJoin = false;
 		}
 		resolve( true, impliedJoin, classAlias, parent );
 	}
 
 	@Override
 	public boolean isReturnableEntity() throws SemanticException {
 		return !isScalar() && fromElement.isEntity();
 	}
 
 	@Override
 	public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
 		resolve( generateJoin, implicitJoin );
 	}
 
 	@Override
 	public void resolve(boolean generateJoin, boolean implicitJoin) throws SemanticException {
 		resolve( generateJoin, implicitJoin, null );
 	}
 
 	@Override
 	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias) throws SemanticException {
 		resolve( generateJoin, implicitJoin, classAlias, null );
 	}
 
+	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
+			throws SemanticException {
+		resolve( generateJoin, implicitJoin, classAlias, parent, null );
+	}
+
 	public void prepareForDot(String propertyName) throws SemanticException {
 	}
 
 	/**
 	 * Sub-classes can override this method if they produce implied joins (e.g. DotNode).
 	 *
 	 * @return an implied join created by this from reference.
 	 */
 	public FromElement getImpliedJoin() {
 		return null;
 	}
 
 	@SuppressWarnings("SimplifiableIfStatement")
 	protected boolean isFromElementUpdateOrDeleteRoot(FromElement element) {
 		if ( element.getFromClause().getParentFromClause() != null ) {
 			// its not even a root...
 			return false;
 		}
 
 		return getWalker().getStatementType() == HqlSqlTokenTypes.DELETE
 				|| getWalker().getStatementType() == HqlSqlTokenTypes.UPDATE;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
index ace7a60da8..4198a98077 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
@@ -1,424 +1,424 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Represents an identifier all by itself, which may be a function name,
  * a class alias, or a form of naked property-ref depending on the
  * context.
  *
  * @author josh
  */
 public class IdentNode extends FromReferenceNode implements SelectExpression {
 	private static enum DereferenceType {
 		UNKNOWN,
 		PROPERTY_REF,
 		COMPONENT_REF
 	}
 
 	private boolean nakedPropertyRef;
 	private String[] columns;
 	
 	public String[] getColumns() {
 		return columns;
 	}
 
 	public void resolveIndex(AST parent) throws SemanticException {
 		// An ident node can represent an index expression if the ident
 		// represents a naked property ref
 		//      *Note: this makes the assumption (which is currently the case
 		//      in the hql-sql grammar) that the ident is first resolved
 		//      itself (addrExpr -> resolve()).  The other option, if that
 		//      changes, is to call resolve from here; but it is
 		//      currently un-needed overhead.
 		if (!(isResolved() && nakedPropertyRef)) {
 			throw new UnsupportedOperationException();
 		}
 
 		String propertyName = getOriginalText();
 		if (!getDataType().isCollectionType()) {
 			throw new SemanticException("Collection expected; [" + propertyName + "] does not refer to a collection property");
 		}
 
 		// TODO : most of below was taken verbatim from DotNode; should either delegate this logic or super-type it
 		CollectionType type = (CollectionType) getDataType();
 		String role = type.getRole();
 		QueryableCollection queryableCollection = getSessionFactoryHelper().requireQueryableCollection(role);
 
 		String alias = null;  // DotNode uses null here...
 		String columnTableAlias = getFromElement().getTableAlias();
 		JoinType joinType = JoinType.INNER_JOIN;
 		boolean fetch = false;
 
 		FromElementFactory factory = new FromElementFactory(
 				getWalker().getCurrentFromClause(),
 				getFromElement(),
 				propertyName,
 				alias,
 				getFromElement().toColumns(columnTableAlias, propertyName, false),
 				true
 		);
 		FromElement elem = factory.createCollection(queryableCollection, role, joinType, fetch, true);
 		setFromElement(elem);
 		getWalker().addQuerySpaces(queryableCollection.getCollectionSpaces());	// Always add the collection's query spaces.
 	}
 
 	protected String[] resolveColumns(QueryableCollection collectionPersister) {
 		final FromElement fromElement = getFromElement();
 		return fromElement.toColumns(
 				fromElement.getCollectionTableAlias(),
 				"elements", // the JPA VALUE "qualifier" is the same concept as the HQL ELEMENTS function/property
 				getWalker().isInSelect()
 		);
 	}
 	
 	private void initText(String[] columns) {
 		String text = StringHelper.join( ", ", columns );
 		if ( columns.length > 1 && getWalker().isComparativeExpressionClause() ) {
 			text = "(" + text + ")";
 		}
 		setText( text );
 	}
 	
-	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent) {
+	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) {
 		if (!isResolved()) {
 			if ( getWalker().getCurrentFromClause().isFromElementAlias( getText() ) ) {
 				FromElement fromElement = getWalker().getCurrentFromClause().getFromElement( getText() );
 				if ( fromElement.getQueryableCollection() != null && fromElement.getQueryableCollection().getElementType().isComponentType() ) {
 					if ( getWalker().isInSelect() ) {
 						// This is a reference to an element collection
 						setFromElement( fromElement );
 						super.setDataType( fromElement.getQueryableCollection().getElementType() );
 						this.columns = resolveColumns( fromElement.getQueryableCollection() );
 						initText( getColumns() );
 						setFirstChild( null );
 						// Don't resolve it
 					}
 					else {
 						resolveAsAlias();
 						// Don't resolve it
 					}
 				}
 				else if ( resolveAsAlias() ) {
 					setResolved();
 					// We represent a from-clause alias
 				}
 			}
 			else if (
 					getColumns() != null
 					&& ( getWalker().getAST() instanceof AbstractMapComponentNode || getWalker().getAST() instanceof IndexNode )
 					&& getWalker().getCurrentFromClause().isFromElementAlias( getOriginalText() )
 					) {
 				// We might have to revert our decision that this is naked element collection reference when we encounter it is embedded in a map function
 				setText( getOriginalText() );
 				if ( resolveAsAlias() ) {
 					setResolved();
 				}
 			}
 			else if (parent != null && parent.getType() == SqlTokenTypes.DOT) {
 				DotNode dot = (DotNode) parent;
 				if (parent.getFirstChild() == this) {
 					if (resolveAsNakedComponentPropertyRefLHS(dot)) {
 						// we are the LHS of the DOT representing a naked comp-prop-ref
 						setResolved();
 					}
 				}
 				else {
 					if (resolveAsNakedComponentPropertyRefRHS(dot)) {
 						// we are the RHS of the DOT representing a naked comp-prop-ref
 						setResolved();
 					}
 				}
 			}
 			else {
 				DereferenceType result = resolveAsNakedPropertyRef();
 				if (result == DereferenceType.PROPERTY_REF) {
 					// we represent a naked (simple) prop-ref
 					setResolved();
 				}
 				else if (result == DereferenceType.COMPONENT_REF) {
 					// EARLY EXIT!!!  return so the resolve call explicitly coming from DotNode can
 					// resolve this...
 					return;
 				}
 			}
 
 			// if we are still not resolved, we might represent a constant.
 			//      needed to add this here because the allowance of
 			//      naked-prop-refs in the grammar collides with the
 			//      definition of literals/constants ("nondeterminism").
 			//      TODO: cleanup the grammar so that "processConstants" is always just handled from here
 			if (!isResolved()) {
 				try {
 					getWalker().getLiteralProcessor().processConstant(this, false);
 				}
 				catch (Throwable ignore) {
 					// just ignore it for now, it'll get resolved later...
 				}
 			}
 		}
 	}
 
 	private boolean resolveAsAlias() {
 		final String alias = getText();
 
 		// This is not actually a constant, but a reference to FROM element.
 		final FromElement element = getWalker().getCurrentFromClause().getFromElement( alias );
 		if ( element == null ) {
 			return false;
 		}
 
 		element.applyTreatAsDeclarations( getWalker().getTreatAsDeclarationsByPath( alias ) );
 
 		setType( SqlTokenTypes.ALIAS_REF );
 		setFromElement( element );
 
 		String[] columnExpressions = element.getIdentityColumns();
 
 		// determine whether to apply qualification (table alias) to the column(s)...
 		if ( ! isFromElementUpdateOrDeleteRoot( element ) ) {
 			if ( StringHelper.isNotEmpty( element.getTableAlias() ) ) {
 				// apparently we also need to check that they are not already qualified.  Ugh!
 				columnExpressions = StringHelper.qualifyIfNot( element.getTableAlias(), columnExpressions );
 			}
 		}
 
 		final Dialect dialect = getWalker().getSessionFactoryHelper().getFactory().getDialect();
 		final boolean isInCount = getWalker().isInCount();
 		final boolean isInDistinctCount = isInCount && getWalker().isInCountDistinct();
 		final boolean isInNonDistinctCount = isInCount && ! getWalker().isInCountDistinct();
 		final boolean isCompositeValue = columnExpressions.length > 1;
 		if ( isCompositeValue ) {
 			if ( isInNonDistinctCount && ! dialect.supportsTupleCounts() ) {
 				// TODO: #supportsTupleCounts currently false for all Dialects -- could this be cleaned up?
 				setText( columnExpressions[0] );
 			}
 			else {
 				String joinedFragment = StringHelper.join( ", ", columnExpressions );
 				// avoid wrapping in parenthesis (explicit tuple treatment) if possible due to varied support for
 				// tuple syntax across databases..
 				final boolean shouldSkipWrappingInParenthesis =
 						(isInDistinctCount && ! dialect.requiresParensForTupleDistinctCounts())
 						|| isInNonDistinctCount
 						|| getWalker().getCurrentTopLevelClauseType() == HqlSqlTokenTypes.ORDER
 						|| getWalker().getCurrentTopLevelClauseType() == HqlSqlTokenTypes.GROUP;
 				if ( ! shouldSkipWrappingInParenthesis ) {
 					joinedFragment = "(" + joinedFragment + ")";
 				}
 				setText( joinedFragment );
 			}
 			return true;
 		}
 		else if ( columnExpressions.length > 0 ) {
 			setText( columnExpressions[0] );
 			return true;
 		}
 
 		return false;
 	}
 
 	private Type getNakedPropertyType(FromElement fromElement) {
 		if (fromElement == null) {
 			return null;
 		}
 		String property = getOriginalText();
 		Type propertyType = null;
 		try {
 			propertyType = fromElement.getPropertyType(property, property);
 		}
 		catch (Throwable ignore) {
 		}
 		return propertyType;
 	}
 
 	private DereferenceType resolveAsNakedPropertyRef() {
 		FromElement fromElement = locateSingleFromElement();
 		if (fromElement == null) {
 			return DereferenceType.UNKNOWN;
 		}
 		Queryable persister = fromElement.getQueryable();
 		if (persister == null) {
 			return DereferenceType.UNKNOWN;
 		}
 		Type propertyType = getNakedPropertyType(fromElement);
 		if (propertyType == null) {
 			// assume this ident's text does *not* refer to a property on the given persister
 			return DereferenceType.UNKNOWN;
 		}
 
 		if ((propertyType.isComponentType() || propertyType.isAssociationType() )) {
 			return DereferenceType.COMPONENT_REF;
 		}
 
 		setFromElement(fromElement);
 		String property = getText();
 		String[] columns = getWalker().isSelectStatement()
 				? persister.toColumns(fromElement.getTableAlias(), property)
 				: persister.toColumns(property);
 		String text = StringHelper.join(", ", columns);
 		setText(columns.length == 1 ? text : "(" + text + ")");
 		setType(SqlTokenTypes.SQL_TOKEN);
 
 		// these pieces are needed for usage in select clause
 		super.setDataType(propertyType);
 		nakedPropertyRef = true;
 
 		return DereferenceType.PROPERTY_REF;
 	}
 
 	private boolean resolveAsNakedComponentPropertyRefLHS(DotNode parent) {
 		FromElement fromElement = locateSingleFromElement();
 		if (fromElement == null) {
 			return false;
 		}
 
 		Type componentType = getNakedPropertyType(fromElement);
 		if ( componentType == null ) {
 			throw new QueryException( "Unable to resolve path [" + parent.getPath() + "], unexpected token [" + getOriginalText() + "]" );
 		}
 		if (!componentType.isComponentType()) {
 			throw new QueryException("Property '" + getOriginalText() + "' is not a component.  Use an alias to reference associations or collections.");
 		}
 
 		Type propertyType;
 		String propertyPath = getText() + "." + getNextSibling().getText();
 		try {
 			// check to see if our "propPath" actually
 			// represents a property on the persister
 			propertyType = fromElement.getPropertyType(getText(), propertyPath);
 		}
 		catch (Throwable t) {
 			// assume we do *not* refer to a property on the given persister
 			return false;
 		}
 
 		setFromElement(fromElement);
 		parent.setPropertyPath(propertyPath);
 		parent.setDataType(propertyType);
 
 		return true;
 	}
 
 	private boolean resolveAsNakedComponentPropertyRefRHS(DotNode parent) {
 		FromElement fromElement = locateSingleFromElement();
 		if (fromElement == null) {
 			return false;
 		}
 
 		Type propertyType;
 		String propertyPath = parent.getLhs().getText() + "." + getText();
 		try {
 			// check to see if our "propPath" actually
 			// represents a property on the persister
 			propertyType = fromElement.getPropertyType(getText(), propertyPath);
 		}
 		catch (Throwable t) {
 			// assume we do *not* refer to a property on the given persister
 			return false;
 		}
 
 		setFromElement(fromElement);
 		// this piece is needed for usage in select clause
 		super.setDataType(propertyType);
 		nakedPropertyRef = true;
 
 		return true;
 	}
 
 	private FromElement locateSingleFromElement() {
 		List fromElements = getWalker().getCurrentFromClause().getFromElements();
 		if (fromElements == null || fromElements.size() != 1) {
 			// TODO : should this be an error?
 			return null;
 		}
 		FromElement element = (FromElement) fromElements.get(0);
 		if (element.getClassAlias() != null) {
 			// naked property-refs cannot be used with an aliased from element
 			return null;
 		}
 		return element;
 	}
 
 	@Override
 	public Type getDataType() {
 		Type type = super.getDataType();
 		if ( type != null ) {
 			return type;
 		}
 		FromElement fe = getFromElement();
 		if ( fe != null ) {
 			return fe.getDataType();
 		}
 		SQLFunction sf = getWalker().getSessionFactoryHelper().findSQLFunction( getText() );
 		if ( sf != null ) {
 			return sf.getReturnType( null, getWalker().getSessionFactoryHelper().getFactory() );
 		}
 		return null;
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		if (nakedPropertyRef) {
 			// do *not* over-write the column text, as that has already been
 			// "rendered" during resolve
 			ColumnHelper.generateSingleScalarColumn(this, i);
 		}
 		else {
 			FromElement fe = getFromElement();
 			if (fe != null) {
 				if ( fe.getQueryableCollection() != null && fe.getQueryableCollection().getElementType().isComponentType() ) {
 					ColumnHelper.generateScalarColumns( this, getColumns(), i );
 				}
 				else {
 					setText(fe.renderScalarIdentifierSelect(i));
 				}
 			}
 			else {
 				ColumnHelper.generateSingleScalarColumn(this, i);
 			}
 		}
 	}
 
 	@Override
 	public String getDisplayText() {
 		StringBuilder buf = new StringBuilder();
 
 		if (getType() == SqlTokenTypes.ALIAS_REF) {
 			buf.append("{alias=").append(getOriginalText());
 			if (getFromElement() == null) {
 				buf.append(", no from element");
 			}
 			else {
 				buf.append(", className=").append(getFromElement().getClassName());
 				buf.append(", tableAlias=").append(getFromElement().getTableAlias());
 			}
 			buf.append("}");
 		}
 		else {
 			buf.append( "{originalText=" ).append( getOriginalText() ).append( "}" );
 		}
 		return buf.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
index 72ffff7a4c..5d76d06811 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
@@ -1,201 +1,201 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.List;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 import antlr.RecognitionException;
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Represents the [] operator and provides it's semantics.
  *
  * @author josh
  */
 public class IndexNode extends FromReferenceNode {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( IndexNode.class );
 
 	@Override
 	public void setScalarColumnText(int i) throws SemanticException {
 		throw new UnsupportedOperationException( "An IndexNode cannot generate column text!" );
 	}
 
 	@Override
 	public void prepareForDot(String propertyName) throws SemanticException {
 		FromElement fromElement = getFromElement();
 		if ( fromElement == null ) {
 			throw new IllegalStateException( "No FROM element for index operator!" );
 		}
 
 		final QueryableCollection queryableCollection = fromElement.getQueryableCollection();
 		if ( queryableCollection != null && !queryableCollection.isOneToMany() ) {
 			final FromReferenceNode collectionNode = (FromReferenceNode) getFirstChild();
 			final String path = collectionNode.getPath() + "[]." + propertyName;
 			LOG.debugf( "Creating join for many-to-many elements for %s", path );
 			final FromElementFactory factory = new FromElementFactory( fromElement.getFromClause(), fromElement, path );
 			// This will add the new from element to the origin.
 			final FromElement elementJoin = factory.createElementJoin( queryableCollection );
 			setFromElement( elementJoin );
 		}
 	}
 
 	@Override
 	public void resolveIndex(AST parent) throws SemanticException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
-	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
+	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate)
 			throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		FromReferenceNode collectionNode = (FromReferenceNode) getFirstChild();
 		SessionFactoryHelper sessionFactoryHelper = getSessionFactoryHelper();
 		collectionNode.resolveIndex( this );        // Fully resolve the map reference, create implicit joins.
 
 		Type type = collectionNode.getDataType();
 		if ( !type.isCollectionType() ) {
 			throw new SemanticException( "The [] operator cannot be applied to type " + type.toString() );
 		}
 		String collectionRole = ( (CollectionType) type ).getRole();
 		QueryableCollection queryableCollection = sessionFactoryHelper.requireQueryableCollection( collectionRole );
 		if ( !queryableCollection.hasIndex() ) {
 			throw new QueryException( "unindexed fromElement beforeQuery []: " + collectionNode.getPath() );
 		}
 
 		// Generate the inner join -- The elements need to be joined to the collection they are in.
 		FromElement fromElement = collectionNode.getFromElement();
 		String elementTable = fromElement.getTableAlias();
 		FromClause fromClause = fromElement.getFromClause();
 		String path = collectionNode.getPath();
 
 		FromElement elem = fromClause.findCollectionJoin( path );
 		if ( elem == null ) {
 			FromElementFactory factory = new FromElementFactory( fromClause, fromElement, path );
 			elem = factory.createCollectionElementsJoin( queryableCollection, elementTable );
 			LOG.debugf( "No FROM element found for the elements of collection join path %s, created %s", path, elem );
 		}
 		else {
 			LOG.debugf( "FROM element found for collection join path %s", path );
 		}
 
 		// The 'from element' that represents the elements of the collection.
 		setFromElement( fromElement );
 
 		// Add the condition to the join sequence that qualifies the indexed element.
 		AST selector = collectionNode.getNextSibling();
 		if ( selector == null ) {
 			throw new QueryException( "No index value!" );
 		}
 
 		// Sometimes use the element table alias, sometimes use the... umm... collection table alias (many to many)
 		String collectionTableAlias = elementTable;
 		if ( elem.getCollectionTableAlias() != null ) {
 			collectionTableAlias = elem.getCollectionTableAlias();
 		}
 
 		// TODO: get SQL rendering out of here, create an AST for the join expressions.
 		// Use the SQL generator grammar to generate the SQL text for the index expression.
 		JoinSequence joinSequence = fromElement.getJoinSequence();
 		String[] indexCols = queryableCollection.getIndexColumnNames();
 		if ( indexCols.length != 1 ) {
 			throw new QueryException( "composite-index appears in []: " + collectionNode.getPath() );
 		}
 		SqlGenerator gen = new SqlGenerator( getSessionFactoryHelper().getFactory() );
 		try {
 			gen.simpleExpr( selector ); //TODO: used to be exprNoParens! was this needed?
 		}
 		catch (RecognitionException e) {
 			throw new QueryException( e.getMessage(), e );
 		}
 		String selectorExpression = gen.getSQL();
 		joinSequence.addCondition( collectionTableAlias + '.' + indexCols[0] + " = " + selectorExpression );
 		List<ParameterSpecification> paramSpecs = gen.getCollectedParameters();
 		if ( paramSpecs != null ) {
 			switch ( paramSpecs.size() ) {
 				case 0:
 					// nothing to do
 					break;
 				case 1:
 					ParameterSpecification paramSpec = paramSpecs.get( 0 );
 					paramSpec.setExpectedType( queryableCollection.getIndexType() );
 					fromElement.setIndexCollectionSelectorParamSpec( paramSpec );
 					break;
 				default:
 					fromElement.setIndexCollectionSelectorParamSpec(
 							new AggregatedIndexCollectionSelectorParameterSpecifications( paramSpecs )
 					);
 					break;
 			}
 		}
 
 		// Now, set the text for this node.  It should be the element columns.
 		String[] elementColumns = queryableCollection.getElementColumnNames( elementTable );
 		setText( elementColumns[0] );
 		setResolved();
 	}
 
 	/**
 	 * In the (rare?) case where the index selector contains multiple parameters...
 	 */
 	private static class AggregatedIndexCollectionSelectorParameterSpecifications implements ParameterSpecification {
 		private final List<ParameterSpecification> paramSpecs;
 
 		public AggregatedIndexCollectionSelectorParameterSpecifications(List<ParameterSpecification> paramSpecs) {
 			this.paramSpecs = paramSpecs;
 		}
 
 		@Override
 		public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position)
 				throws SQLException {
 			int bindCount = 0;
 			for ( ParameterSpecification paramSpec : paramSpecs ) {
 				bindCount += paramSpec.bind( statement, qp, session, position + bindCount );
 			}
 			return bindCount;
 		}
 
 		@Override
 		public Type getExpectedType() {
 			return null;
 		}
 
 		@Override
 		public void setExpectedType(Type expectedType) {
 		}
 
 		@Override
 		public String renderDisplayInfo() {
 			return "index-selector [" + collectDisplayInfo() + "]";
 		}
 
 		private String collectDisplayInfo() {
 			StringBuilder buffer = new StringBuilder();
 			for ( ParameterSpecification paramSpec : paramSpecs ) {
 				buffer.append( ( paramSpec ).renderDisplayInfo() );
 			}
 			return buffer.toString();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/MapEntryNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/MapEntryNode.java
index 8ab79454b6..d775376864 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/MapEntryNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/MapEntryNode.java
@@ -1,308 +1,309 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import antlr.collections.AST;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.NameGenerator;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.AliasGenerator;
 import org.hibernate.sql.SelectExpression;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.transform.BasicTransformerAdapter;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import antlr.SemanticException;
 
 /**
  * Tree node representing reference to the entry ({@link Map.Entry}) of a Map association.
  *
  * @author Steve Ebersole
  */
 public class MapEntryNode extends AbstractMapComponentNode implements AggregatedSelectExpression {
 	private static class LocalAliasGenerator implements AliasGenerator {
 		private final int base;
 		private int counter;
 
 		private LocalAliasGenerator(int base) {
 			this.base = base;
 		}
 
 		@Override
 		public String generateAlias(String sqlExpression) {
 			return NameGenerator.scalarName( base, counter++ );
 		}
 	}
 
 	private int scalarColumnIndex = -1;
 
 	@Override
 	protected String expressionDescription() {
 		return "entry(*)";
 	}
 
 	@Override
 	public Class getAggregationResultType() {
 		return Map.Entry.class;
 	}
 
 
 	@Override
 	public void resolve(
 			boolean generateJoin,
 			boolean implicitJoin,
 			String classAlias,
-			AST parent) throws SemanticException {
+			AST parent,
+			AST parentPredicate) throws SemanticException {
 		if (parent != null) {
 			throw new SemanticException( expressionDescription() + " expression cannot be further de-referenced" );
 		}
-		super.resolve(generateJoin, implicitJoin, classAlias, parent);
+		super.resolve(generateJoin, implicitJoin, classAlias, parent, parentPredicate);
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	protected Type resolveType(QueryableCollection collectionPersister) {
 		final Type keyType = collectionPersister.getIndexType();
 		final Type valueType = collectionPersister.getElementType();
 		types.add( keyType );
 		types.add( valueType );
 		mapEntryBuilder = new MapEntryBuilder();
 
 		// an entry (as an aggregated select expression) does not have a type...
 		return null;
 	}
 
 	@Override
 	protected String[] resolveColumns(QueryableCollection collectionPersister) {
 		List selections = new ArrayList();
 		determineKeySelectExpressions( collectionPersister, selections );
 		determineValueSelectExpressions( collectionPersister, selections );
 
 		String text = "";
 		String[] columns = new String[selections.size()];
 		for ( int i = 0; i < selections.size(); i++ ) {
 			SelectExpression selectExpression = (SelectExpression) selections.get( i );
 			text += ( ", " + selectExpression.getExpression() + " as " + selectExpression.getAlias() );
 			columns[i] = selectExpression.getExpression();
 		}
 
 		text = text.substring( 2 ); //strip leading ", "
 		setText( text );
 		setResolved();
 		return columns;
 	}
 
 	private void determineKeySelectExpressions(QueryableCollection collectionPersister, List selections) {
 		AliasGenerator aliasGenerator = new LocalAliasGenerator( 0 );
 		appendSelectExpressions( collectionPersister.getIndexColumnNames(), selections, aliasGenerator );
 		Type keyType = collectionPersister.getIndexType();
 		if ( keyType.isEntityType() ) {
 			MapKeyEntityFromElement mapKeyEntityFromElement = findOrAddMapKeyEntityFromElement( collectionPersister );
 			Queryable keyEntityPersister = mapKeyEntityFromElement.getQueryable();
 			SelectFragment fragment = keyEntityPersister.propertySelectFragmentFragment(
 					mapKeyEntityFromElement.getTableAlias(),
 					null,
 					false
 			);
 			appendSelectExpressions( fragment, selections, aliasGenerator );
 		}
 	}
 
 	@SuppressWarnings({"unchecked", "ForLoopReplaceableByForEach"})
 	private void appendSelectExpressions(String[] columnNames, List selections, AliasGenerator aliasGenerator) {
 		for ( int i = 0; i < columnNames.length; i++ ) {
 			selections.add(
 					new BasicSelectExpression(
 							collectionTableAlias() + '.' + columnNames[i],
 							aliasGenerator.generateAlias( columnNames[i] )
 					)
 			);
 		}
 	}
 
 	@SuppressWarnings({"unchecked", "WhileLoopReplaceableByForEach"})
 	private void appendSelectExpressions(SelectFragment fragment, List selections, AliasGenerator aliasGenerator) {
 		Iterator itr = fragment.getColumns().iterator();
 		while ( itr.hasNext() ) {
 			final String column = (String) itr.next();
 			selections.add(
 					new BasicSelectExpression( column, aliasGenerator.generateAlias( column ) )
 			);
 		}
 	}
 
 	private void determineValueSelectExpressions(QueryableCollection collectionPersister, List selections) {
 		AliasGenerator aliasGenerator = new LocalAliasGenerator( 1 );
 		appendSelectExpressions( collectionPersister.getElementColumnNames(), selections, aliasGenerator );
 		Type valueType = collectionPersister.getElementType();
 		if ( valueType.isAssociationType() ) {
 			EntityType valueEntityType = (EntityType) valueType;
 			Queryable valueEntityPersister = (Queryable) sfi().getEntityPersister(
 					valueEntityType.getAssociatedEntityName( sfi() )
 			);
 			SelectFragment fragment = valueEntityPersister.propertySelectFragmentFragment(
 					elementTableAlias(),
 					null,
 					false
 			);
 			appendSelectExpressions( fragment, selections, aliasGenerator );
 		}
 	}
 
 	private String collectionTableAlias() {
 		return getFromElement().getCollectionTableAlias() != null
 				? getFromElement().getCollectionTableAlias()
 				: getFromElement().getTableAlias();
 	}
 
 	private String elementTableAlias() {
 		return getFromElement().getTableAlias();
 	}
 
 	private static class BasicSelectExpression implements SelectExpression {
 		private final String expression;
 		private final String alias;
 
 		private BasicSelectExpression(String expression, String alias) {
 			this.expression = expression;
 			this.alias = alias;
 		}
 
 		@Override
 		public String getExpression() {
 			return expression;
 		}
 
 		@Override
 		public String getAlias() {
 			return alias;
 		}
 	}
 
 	public SessionFactoryImplementor sfi() {
 		return getSessionFactoryHelper().getFactory();
 	}
 
 	@Override
 	public void setText(String s) {
 		if ( isResolved() ) {
 			return;
 		}
 		super.setText( s );
 	}
 
 	@Override
 	public void setScalarColumn(int i) throws SemanticException {
 		this.scalarColumnIndex = i;
 	}
 
 	@Override
 	public int getScalarColumnIndex() {
 		return scalarColumnIndex;
 	}
 
 	@Override
 	public void setScalarColumnText(int i) throws SemanticException {
 	}
 
 	@Override
 	public boolean isScalar() {
 		// Constructors are always considered scalar results.
 		return true;
 	}
 
 	private List types = new ArrayList( 4 ); // size=4 to prevent resizing
 
 	@Override
 	public List getAggregatedSelectionTypeList() {
 		return types;
 	}
 
 	private static final String[] ALIASES = {null, null};
 
 	@Override
 	public String[] getAggregatedAliases() {
 		return ALIASES;
 	}
 
 	private MapEntryBuilder mapEntryBuilder;
 
 	@Override
 	public ResultTransformer getResultTransformer() {
 		return mapEntryBuilder;
 	}
 
 	private static class MapEntryBuilder extends BasicTransformerAdapter {
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			if ( tuple.length != 2 ) {
 				throw new HibernateException( "Expecting exactly 2 tuples to transform into Map.Entry" );
 			}
 			return new EntryAdapter( tuple[0], tuple[1] );
 		}
 	}
 
 	private static class EntryAdapter implements Map.Entry {
 		private final Object key;
 		private Object value;
 
 		private EntryAdapter(Object key, Object value) {
 			this.key = key;
 			this.value = value;
 		}
 
 		@Override
 		public Object getValue() {
 			return value;
 		}
 
 		@Override
 		public Object getKey() {
 			return key;
 		}
 
 		@Override
 		public Object setValue(Object value) {
 			Object old = this.value;
 			this.value = value;
 			return old;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			// IMPL NOTE : nulls are considered equal for keys and values according to Map.Entry contract
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 			EntryAdapter that = (EntryAdapter) o;
 
 			// make sure we have the same types...
 			return ( key == null ? that.key == null : key.equals( that.key ) )
 					&& ( value == null ? that.value == null : value.equals( that.value ) );
 
 		}
 
 		@Override
 		public int hashCode() {
 			int keyHash = key == null ? 0 : key.hashCode();
 			int valueHash = value == null ? 0 : value.hashCode();
 			return keyHash ^ valueHash;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ResolvableNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ResolvableNode.java
index bdbd0915bb..74efd7a4d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ResolvableNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ResolvableNode.java
@@ -1,42 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * The contract for expression sub-trees that can resolve themselves.
  *
  * @author josh
  */
 public interface ResolvableNode {
 	/**
 	 * Does the work of resolving an identifier or a dot
 	 */
+	void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) throws SemanticException;
+	/**
+	 * Does the work of resolving an identifier or a dot, but without a parent predicate node
+	 */
 	void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent) throws SemanticException;
 
 	/**
-	 * Does the work of resolving an identifier or a dot, but without a parent node
+	 * Does the work of resolving an identifier or a dot, but without a parent predicate node or parent node
 	 */
 	void resolve(boolean generateJoin, boolean implicitJoin, String classAlias) throws SemanticException;
 
 	/**
-	 * Does the work of resolving an identifier or a dot, but without a parent node or alias
+	 * Does the work of resolving an identifier or a dot, but without a parent predicate node or parent node or alias
 	 */
 	void resolve(boolean generateJoin, boolean implicitJoin) throws SemanticException;
 
 	/**
 	 * Does the work of resolving inside of the scope of a function call
 	 */
 	void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException;
 
 	/**
 	 * Does the work of resolving an an index [].
 	 */
 	void resolveIndex(AST parent) throws SemanticException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpressionImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpressionImpl.java
index 3a53500cf5..f99caf8eac 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpressionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpressionImpl.java
@@ -1,31 +1,31 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * A select expression that was generated by a FROM element.
  *
  * @author josh
  */
 public class SelectExpressionImpl extends FromReferenceNode implements SelectExpression {
 
 	public void resolveIndex(AST parent) throws SemanticException {
 		throw new UnsupportedOperationException();
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		String text = getFromElement().renderScalarIdentifierSelect( i );
 		setText( text );
 	}
 
-	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent) throws SemanticException {
+	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) throws SemanticException {
 		// Generated select expressions are already resolved, nothing to do.
 		return;
 	}
 }
