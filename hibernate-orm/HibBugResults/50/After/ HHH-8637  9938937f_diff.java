diff --git a/hibernate-core/src/main/antlr/hql.g b/hibernate-core/src/main/antlr/hql.g
index 4d995703da..9c6104399d 100644
--- a/hibernate-core/src/main/antlr/hql.g
+++ b/hibernate-core/src/main/antlr/hql.g
@@ -1,984 +1,991 @@
 header
 {
 package org.hibernate.hql.internal.antlr;
 
 import org.hibernate.hql.internal.ast.*;
 import org.hibernate.hql.internal.ast.util.*;
 
 }
 /**
  * Hibernate Query Language Grammar
  * <br>
  * This grammar parses the query language for Hibernate (an Open Source, Object-Relational
  * mapping library).  A partial BNF grammar description is available for reference here:
  * http://www.hibernate.org/Documentation/HQLBNF
  *
  * Text from the original reference BNF is prefixed with '//##'.
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 class HqlBaseParser extends Parser;
 
 options
 {
 	exportVocab=Hql;
 	buildAST=true;
 	k=3;    // For 'not like', 'not in', etc.
 }
 
 tokens
 {
 	// -- HQL Keyword tokens --
 	ALL="all";
 	ANY="any";
 	AND="and";
 	AS="as";
 	ASCENDING="asc";
 	AVG="avg";
 	BETWEEN="between";
 	CLASS="class";
 	COUNT="count";
 	DELETE="delete";
 	DESCENDING="desc";
 	DOT;
 	DISTINCT="distinct";
 	ELEMENTS="elements";
 	ESCAPE="escape";
 	EXISTS="exists";
 	FALSE="false";
 	FETCH="fetch";
 	FROM="from";
 	FULL="full";
 	GROUP="group";
 	HAVING="having";
 	IN="in";
 	INDICES="indices";
 	INNER="inner";
 	INSERT="insert";
 	INTO="into";
 	IS="is";
 	JOIN="join";
 	LEFT="left";
 	LIKE="like";
 	MAX="max";
 	MIN="min";
 	NEW="new";
 	NOT="not";
 	NULL="null";
 	OR="or";
 	ORDER="order";
 	OUTER="outer";
 	PROPERTIES="properties";
 	RIGHT="right";
 	SELECT="select";
 	SET="set";
 	SOME="some";
 	SUM="sum";
 	TRUE="true";
 	UNION="union";
 	UPDATE="update";
 	VERSIONED="versioned";
 	WHERE="where";
 	NULLS="nulls";
 	FIRST;
 	LAST;
 
 	// -- SQL tokens --
 	// These aren't part of HQL, but the SQL fragment parser uses the HQL lexer, so they need to be declared here.
 	CASE="case";
 	END="end";
 	ELSE="else";
 	THEN="then";
 	WHEN="when";
 	ON="on";
 	WITH="with";
 
 	// -- EJBQL tokens --
 	BOTH="both";
 	EMPTY="empty";
 	LEADING="leading";
 	MEMBER="member";
 	OBJECT="object";
 	OF="of";
 	TRAILING="trailing";
 	KEY;
 	VALUE;
 	ENTRY;
 
 	// -- Synthetic token types --
 	AGGREGATE;		// One of the aggregate functions (e.g. min, max, avg)
 	ALIAS;
 	CONSTRUCTOR;
 	CASE2;
 	EXPR_LIST;
 	FILTER_ENTITY;		// FROM element injected because of a filter expression (happens during compilation phase 2)
 	IN_LIST;
 	INDEX_OP;
 	IS_NOT_NULL;
 	IS_NULL;			// Unary 'is null' operator.
 	METHOD_CALL;
 	NOT_BETWEEN;
 	NOT_IN;
 	NOT_LIKE;
 	ORDER_ELEMENT;
 	QUERY;
 	RANGE;
 	ROW_STAR;
 	SELECT_FROM;
 	UNARY_MINUS;
 	UNARY_PLUS;
 	VECTOR_EXPR;		// ( x, y, z )
 	WEIRD_IDENT;		// Identifiers that were keywords when they came in.
 
 	// Literal tokens.
 	CONSTANT;
 	NUM_DOUBLE;
 	NUM_FLOAT;
 	NUM_LONG;
 	NUM_BIG_INTEGER;
 	NUM_BIG_DECIMAL;
 	JAVA_CONSTANT;
 }
 
 {
     /** True if this is a filter query (allow no FROM clause). **/
 	private boolean filter = false;
 
 	/**
 	 * Sets the filter flag.
 	 * @param f True for a filter query, false for a normal query.
 	 */
 	public void setFilter(boolean f) {
 		filter = f;
 	}
 
 	/**
 	 * Returns true if this is a filter query, false if not.
 	 * @return true if this is a filter query, false if not.
 	 */
 	public boolean isFilter() {
 		return filter;
 	}
 
 	/**
 	 * This method is overriden in the sub class in order to provide the
 	 * 'keyword as identifier' hack.
 	 * @param token The token to retry as an identifier.
 	 * @param ex The exception to throw if it cannot be retried as an identifier.
 	 */
 	public AST handleIdentifierError(Token token,RecognitionException ex) throws RecognitionException, TokenStreamException {
 		// Base implementation: Just re-throw the exception.
 		throw ex;
 	}
 
     /**
      * This method looks ahead and converts . <token> into . IDENT when
      * appropriate.
      */
     public void handleDotIdent() throws TokenStreamException {
     }
 
 	/**
 	 * Returns the negated equivalent of the expression.
 	 * @param x The expression to negate.
 	 */
 	public AST negateNode(AST x) {
 		// Just create a 'not' parent for the default behavior.
 		return ASTUtil.createParent(astFactory, NOT, "not", x);
 	}
 
 	/**
 	 * Returns the 'cleaned up' version of a comparison operator sub-tree.
 	 * @param x The comparison operator to clean up.
 	 */
 	public AST processEqualityExpression(AST x) throws RecognitionException {
 		return x;
 	}
 
 	public void weakKeywords() throws TokenStreamException {
 	}
 
 	public void processMemberOf(Token n,AST p,ASTPair currentAST) {
 	}
 
 	protected boolean validateSoftKeyword(String text) throws TokenStreamException {
 		return validateLookAheadText(1, text);
 	}
 
 	protected boolean validateLookAheadText(int lookAheadPosition, String text) throws TokenStreamException {
 		String text2Validate = retrieveLookAheadText( lookAheadPosition );
 		return text2Validate == null ? false : text2Validate.equalsIgnoreCase( text );
 	}
 
 	protected String retrieveLookAheadText(int lookAheadPosition) throws TokenStreamException {
 		Token token = LT(lookAheadPosition);
 		return token == null ? null : token.getText();
 	}
 
     protected String unquote(String text) {
         return text.substring( 1, text.length() - 1 );
     }
+
+    protected void registerTreat(AST pathToTreat, AST treatAs) {
+    }
 }
 
 statement
 	: ( updateStatement | deleteStatement | selectStatement | insertStatement )
 	;
 
 updateStatement
 	: UPDATE^ (VERSIONED)?
 		optionalFromTokenFromClause
 		setClause
 		(whereClause)?
 	;
 
 setClause
 	: (SET^ assignment (COMMA! assignment)*)
 	;
 
 assignment
 	: stateField EQ^ newValue
 	;
 
 // "state_field" is the term used in the EJB3 sample grammar; used here for easy reference.
 // it is basically a property ref
 stateField
 	: path
 	;
 
 // this still needs to be defined in the ejb3 spec; additiveExpression is currently just a best guess,
 // although it is highly likely I would think that the spec may limit this even more tightly.
 newValue
 	: concatenation
 	;
 
 deleteStatement
 	: DELETE^
 		(optionalFromTokenFromClause)
 		(whereClause)?
 	;
 
 optionalFromTokenFromClause!
 	: (FROM!)? f:path (a:asAlias)? {
 		AST #range = #([RANGE, "RANGE"], #f, #a);
 		#optionalFromTokenFromClause = #([FROM, "FROM"], #range);
 	}
 	;
 
 selectStatement
 	: queryRule {
 		#selectStatement = #([QUERY,"query"], #selectStatement);
 	}
 	;
 
 insertStatement
 	// Would be nice if we could abstract the FromClause/FromElement logic
 	// out such that it could be reused here; something analogous to
 	// a "table" rule in sql-grammars
 	: INSERT^ intoClause selectStatement
 	;
 
 intoClause
 	: INTO^ path { weakKeywords(); } insertablePropertySpec
 	;
 
 insertablePropertySpec
 	: OPEN! primaryExpression ( COMMA! primaryExpression )* CLOSE! {
 		// Just need *something* to distinguish this on the hql-sql.g side
 		#insertablePropertySpec = #([RANGE, "column-spec"], #insertablePropertySpec);
 	}
 	;
 
 union
 	: queryRule (UNION queryRule)*
 	;
 
 //## query:
 //##     [selectClause] fromClause [whereClause] [groupByClause] [havingClause] [orderByClause];
 
 queryRule
 	: selectFrom
 		(whereClause)?
 		(groupByClause)?
 		(orderByClause)?
 		;
 
 selectFrom!
 	:  (s:selectClause)? (f:fromClause)? {
 		// If there was no FROM clause and this is a filter query, create a from clause.  Otherwise, throw
 		// an exception because non-filter queries must have a FROM clause.
 		if (#f == null) {
 			if (filter) {
 				#f = #([FROM,"{filter-implied FROM}"]);
 			}
 			else
 				throw new SemanticException("FROM expected (non-filter queries must contain a FROM clause)");
 		}
 			
 		// Create an artificial token so the 'FROM' can be placed
 		// before the SELECT in the tree to make tree processing
 		// simpler.
 		#selectFrom = #([SELECT_FROM,"SELECT_FROM"],f,s);
 	}
 	;
 
 //## selectClause:
 //##     SELECT DISTINCT? selectedPropertiesList | ( NEW className OPEN selectedPropertiesList CLOSE );
 
 selectClause
 	: SELECT^	// NOTE: The '^' after a token causes the corresponding AST node to be the root of the sub-tree.
 		{ weakKeywords(); }	// Weak keywords can appear immediately after a SELECT token.
 		(DISTINCT)? ( selectedPropertiesList | newExpression | selectObject )
 	;
 
 newExpression
 	: (NEW! path) op:OPEN^ {#op.setType(CONSTRUCTOR);} selectedPropertiesList CLOSE!
 	;
 
 selectObject
    : OBJECT^ OPEN! identifier CLOSE!
    ;
 
 //## fromClause:
 //##    FROM className AS? identifier (  ( COMMA className AS? identifier ) | ( joinType path AS? identifier ) )*;
 
 // NOTE: This *must* begin with the "FROM" token, otherwise the sub-query rule will be ambiguous
 // with the expression rule.
 // Also note: after a comma weak keywords are allowed and should be treated as identifiers.
 
 fromClause
 	: FROM^ { weakKeywords(); } fromRange ( fromJoin | COMMA! { weakKeywords(); } fromRange )*
 	;
 
 //## joinType:
 //##     ( ( 'left'|'right' 'outer'? ) | 'full' | 'inner' )? JOIN FETCH?;
 
 fromJoin
 	: ( ( ( LEFT | RIGHT ) (OUTER)? ) | FULL | INNER )? JOIN^ (FETCH)? 
 	        joinPath (asAlias)? (propertyFetch)? (withClause)?
 	;
 
 joinPath
     : { validateSoftKeyword("treat") && LA(2) == OPEN }? castedJoinPath
     | path
     ;
 
 /**
  * Represents the JPA 2.1 TREAT construct when applied to a join.  Hibernate already handles subclass
  * property references implicitly, so we simply "eat" all tokens of the TREAT construct and just return the
  * join path itself.
  *
  * Uses a validating semantic predicate to make sure the text of the matched first IDENT is the TREAT keyword
  */
 castedJoinPath
-    : i:IDENT! OPEN! p:path AS! path! CLOSE! {i.getText().equalsIgnoreCase("treat") }?
+    : i:IDENT! OPEN! p:path AS! a:path! CLOSE! {i.getText().equalsIgnoreCase("treat") }? {
+        registerTreat( #p, #a );
+    }
     ;
 
 withClause
 	: WITH^ logicalExpression
 	// JPA 2.1 support for an ON clause that isn't really an ON clause...
 	| ON! le:logicalExpression {
 	    // it's really just a WITH clause, so treat it as such...
 	    #withClause = #( [WITH, "with"], #le );
 	}
 	;
 
 fromRange
 	: fromClassOrOuterQueryPath
 	| inClassDeclaration
 	| inCollectionDeclaration
 	| inCollectionElementsDeclaration
 	;
 	
 fromClassOrOuterQueryPath!
 	: c:path { weakKeywords(); } (a:asAlias)? (p:propertyFetch)? {
 		#fromClassOrOuterQueryPath = #([RANGE, "RANGE"], #c, #a, #p);
 	}
 	;
 
 inClassDeclaration!
 	: a:alias IN! CLASS! c:path {
 		#inClassDeclaration = #([RANGE, "RANGE"], #c, #a);
 	}
 	;
 
 inCollectionDeclaration!
     : IN! OPEN! p:path CLOSE! a:asAlias {
         #inCollectionDeclaration = #([JOIN, "join"], [INNER, "inner"], #p, #a);
 	}
     ;
 
 inCollectionElementsDeclaration!
 	: a:alias IN! ELEMENTS! OPEN! p:path CLOSE! {
         #inCollectionElementsDeclaration = #([JOIN, "join"], [INNER, "inner"], #p, #a);
 	}
     ;
 
 // Alias rule - Parses the optional 'as' token and forces an AST identifier node.
 asAlias
 	: (AS!)? alias
 	;
 
 alias
 	: a:identifier { #a.setType(ALIAS); }
     ;
     
 propertyFetch
 	: FETCH ALL! PROPERTIES!
 	;
 
 //## groupByClause:
 //##     GROUP_BY path ( COMMA path )*;
 
 groupByClause
 	: GROUP^ 
 		"by"! expression ( COMMA! expression )*
 		(havingClause)?
 	;
 
 //## orderByClause:
 //##     ORDER_BY selectedPropertiesList;
 
 orderByClause
 	: ORDER^ "by"! orderElement ( COMMA! orderElement )*
 	;
 
 orderElement
 	: expression ( ascendingOrDescending )? ( nullOrdering )?
 	;
 
 ascendingOrDescending
 	: ( "asc" | "ascending" )	{ #ascendingOrDescending.setType(ASCENDING); }
 	| ( "desc" | "descending") 	{ #ascendingOrDescending.setType(DESCENDING); }
 	;
 
 nullOrdering
     : NULLS nullPrecedence
     ;
 
 nullPrecedence
     : IDENT {
             if ( "first".equalsIgnoreCase( #nullPrecedence.getText() ) ) {
                 #nullPrecedence.setType( FIRST );
             }
             else if ( "last".equalsIgnoreCase( #nullPrecedence.getText() ) ) {
                 #nullPrecedence.setType( LAST );
             }
             else {
                 throw new SemanticException( "Expecting 'first' or 'last', but found '" +  #nullPrecedence.getText() + "' as null ordering precedence." );
             }
     }
     ;
 
 //## havingClause:
 //##     HAVING logicalExpression;
 
 havingClause
 	: HAVING^ logicalExpression
 	;
 
 //## whereClause:
 //##     WHERE logicalExpression;
 
 whereClause
 	: WHERE^ logicalExpression
 	;
 
 //## selectedPropertiesList:
 //##     ( path | aggregate ) ( COMMA path | aggregate )*;
 
 selectedPropertiesList
 	: aliasedExpression ( COMMA! aliasedExpression )*
 	;
 	
 aliasedExpression
 	: expression ( AS^ identifier )?
 	;
 
 // expressions
 // Note that most of these expressions follow the pattern
 //   thisLevelExpression :
 //       nextHigherPrecedenceExpression
 //           (OPERATOR nextHigherPrecedenceExpression)*
 // which is a standard recursive definition for a parsing an expression.
 //
 // Operator precedence in HQL
 // lowest  --> ( 7)  OR
 //             ( 6)  AND, NOT
 //             ( 5)  equality: ==, <>, !=, is
 //             ( 4)  relational: <, <=, >, >=,
 //                   LIKE, NOT LIKE, BETWEEN, NOT BETWEEN, IN, NOT IN
 //             ( 3)  addition and subtraction: +(binary) -(binary)
 //             ( 2)  multiplication: * / %, concatenate: ||
 // highest --> ( 1)  +(unary) -(unary)
 //                   []   () (method call)  . (dot -- identifier qualification)
 //                   aggregate function
 //                   ()  (explicit parenthesis)
 //
 // Note that the above precedence levels map to the rules below...
 // Once you have a precedence chart, writing the appropriate rules as below
 // is usually very straightfoward
 
 logicalExpression
 	: expression
 	;
 
 // Main expression rule
 expression
 	: logicalOrExpression
 	;
 
 // level 7 - OR
 logicalOrExpression
 	: logicalAndExpression ( OR^ logicalAndExpression )*
 	;
 
 // level 6 - AND, NOT
 logicalAndExpression
 	: negatedExpression ( AND^ negatedExpression )*
 	;
 
 // NOT nodes aren't generated.  Instead, the operator in the sub-tree will be
 // negated, if possible.   Expressions without a NOT parent are passed through.
 negatedExpression!
 { weakKeywords(); } // Weak keywords can appear in an expression, so look ahead.
 	: NOT^ x:negatedExpression { #negatedExpression = negateNode(#x); }
 	| y:equalityExpression { #negatedExpression = #y; }
 	;
 
 //## OP: EQ | LT | GT | LE | GE | NE | SQL_NE | LIKE;
 
 // level 5 - EQ, NE
 equalityExpression
 	: x:relationalExpression (
 		( EQ^
 		| is:IS^	{ #is.setType(EQ); } (NOT! { #is.setType(NE); } )?
 		| NE^
 		| ne:SQL_NE^	{ #ne.setType(NE); }
 		) y:relationalExpression)* {
 			// Post process the equality expression to clean up 'is null', etc.
 			#equalityExpression = processEqualityExpression(#equalityExpression);
 		}
 	;
 
 // level 4 - LT, GT, LE, GE, LIKE, NOT LIKE, BETWEEN, NOT BETWEEN
 // NOTE: The NOT prefix for LIKE and BETWEEN will be represented in the
 // token type.  When traversing the AST, use the token type, and not the
 // token text to interpret the semantics of these nodes.
 relationalExpression
 	: concatenation (
 		( ( ( LT^ | GT^ | LE^ | GE^ ) additiveExpression )* )
 		// Disable node production for the optional 'not'.
 		| (n:NOT!)? (
 			// Represent the optional NOT prefix using the token type by
 			// testing 'n' and setting the token type accordingly.
 			(i:IN^ {
 					#i.setType( (n == null) ? IN : NOT_IN);
 					#i.setText( (n == null) ? "in" : "not in");
 				}
 				inList)
 			| (b:BETWEEN^ {
 					#b.setType( (n == null) ? BETWEEN : NOT_BETWEEN);
 					#b.setText( (n == null) ? "between" : "not between");
 				}
 				betweenList )
 			| (l:LIKE^ {
 					#l.setType( (n == null) ? LIKE : NOT_LIKE);
 					#l.setText( (n == null) ? "like" : "not like");
 				}
 				concatenation likeEscape)
 			| (MEMBER! (OF!)? p:path! {
 				processMemberOf(n,#p,currentAST);
 			  } ) )
 		)
 	;
 
 likeEscape
 	: (ESCAPE^ concatenation)?
 	;
 
 inList
 	: x:compoundExpr
 	{ #inList = #([IN_LIST,"inList"], #inList); }
 	;
 
 betweenList
 	: concatenation AND! concatenation
 	;
 
 //level 4 - string concatenation
 concatenation
 	: additiveExpression 
 	( c:CONCAT^ { #c.setType(EXPR_LIST); #c.setText("concatList"); } 
 	  additiveExpression
 	  ( CONCAT! additiveExpression )* 
 	  { #concatenation = #([METHOD_CALL, "||"], #([IDENT, "concat"]), #c ); } )?
 	;
 
 // level 3 - binary plus and minus
 additiveExpression
 	: multiplyExpression ( ( PLUS^ | MINUS^ ) multiplyExpression )*
 	;
 
 // level 2 - binary multiply and divide
 multiplyExpression
 	: unaryExpression ( ( STAR^ | DIV^ | MOD^ ) unaryExpression )*
 	;
 	
 // level 1 - unary minus, unary plus, not
 unaryExpression
 	: MINUS^ {#MINUS.setType(UNARY_MINUS);} unaryExpression
 	| PLUS^ {#PLUS.setType(UNARY_PLUS);} unaryExpression
 	| caseExpression
 	| quantifiedExpression
 	| atom
 	;
 	
 caseExpression
 	: CASE^ (whenClause)+ (elseClause)? END! 
 	| CASE^ { #CASE.setType(CASE2); } unaryExpression (altWhenClause)+ (elseClause)? END!
 	;
 	
 whenClause
 	: (WHEN^ logicalExpression THEN! unaryExpression)
 	;
 	
 altWhenClause
 	: (WHEN^ unaryExpression THEN! unaryExpression)
 	;
 	
 elseClause
 	: (ELSE^ unaryExpression)
 	;
 	
 quantifiedExpression
 	: ( SOME^ | EXISTS^ | ALL^ | ANY^ ) 
 	( identifier | collectionExpr | (OPEN! ( subQuery ) CLOSE!) )
 	;
 
 // level 0 - expression atom
 //      * ident qualifier ('.' ident )
 //      * array index ( [ expr ] )
 //      * method call ( '.' ident '(' exprList ') )
 //      * function : differentiated from method call via explicit keyword
 atom
     : { validateSoftKeyword("function") && LA(2) == OPEN && LA(3) == QUOTED_STRING }? jpaFunctionSyntax
     | primaryExpression
 		(
 			DOT^ identifier
 				( options { greedy=true; } :
 					( op:OPEN^ {#op.setType(METHOD_CALL);} exprList CLOSE! ) )?
 		|	lb:OPEN_BRACKET^ {#lb.setType(INDEX_OP);} expression CLOSE_BRACKET!
 		)*
 	;
 
 jpaFunctionSyntax!
     : i:IDENT OPEN n:QUOTED_STRING COMMA a:exprList CLOSE {
         #i.setType( METHOD_CALL );
         #i.setText( #i.getText() + " (" + #n.getText() + ")" );
         #jpaFunctionSyntax = #( #i, [IDENT, unquote( #n.getText() )], #a );
     }
     ;
 
 // level 0 - the basic element of an expression
 primaryExpression
 	:   identPrimary ( options {greedy=true;} : DOT^ "class" )?
 	|   constant
 	|   parameter
 	// TODO: Add parens to the tree so the user can control the operator evaluation order.
 	|   OPEN! (expressionOrVector | subQuery) CLOSE!
 	;
 
 parameter
 	: COLON^ identifier
 	| PARAM^ (NUM_INT)?
 	;
 
 // This parses normal expression and a list of expressions separated by commas.  If a comma is encountered
 // a parent VECTOR_EXPR node will be created for the list.
 expressionOrVector!
 	: e:expression ( v:vectorExpr )? {
 		// If this is a vector expression, create a parent node for it.
 		if (#v != null)
 			#expressionOrVector = #([VECTOR_EXPR,"{vector}"], #e, #v);
 		else
 			#expressionOrVector = #e;
 	}
 	;
 
 vectorExpr
 	: COMMA! expression (COMMA! expression)*
 	;
 
 // identifier, followed by member refs (dot ident), or method calls.
 // NOTE: handleDotIdent() is called immediately after the first IDENT is recognized because
 // the method looks a head to find keywords after DOT and turns them into identifiers.
 identPrimary
     : i:identPrimaryBase { handleDotIdent(); }
 			( options { greedy=true; } : DOT^ ( identifier | ELEMENTS | o:OBJECT { #o.setType(IDENT); } ) )*
 			( options { greedy=true; } :
 				( op:OPEN^ { #op.setType(METHOD_CALL);} e:exprList CLOSE! ) {
 				    AST path = #e.getFirstChild();
 				    if ( #i.getText().equalsIgnoreCase( "key" ) ) {
 				        #identPrimary = #( [KEY], path );
 				    }
 				    else if ( #i.getText().equalsIgnoreCase( "value" ) ) {
 				        #identPrimary = #( [VALUE], path );
 				    }
 				    else if ( #i.getText().equalsIgnoreCase( "entry" ) ) {
 				        #identPrimary = #( [ENTRY], path );
 				    }
 				}
 			)?
 	// Also allow special 'aggregate functions' such as count(), avg(), etc.
 	| aggregate
 	;
 
 identPrimaryBase
     : { validateSoftKeyword("treat") && LA(2) == OPEN }? castedIdentPrimaryBase
     | i:identifier
     ;
 
 castedIdentPrimaryBase
-    : i:IDENT! OPEN! p:path AS! path! CLOSE! { i.getText().equals("treat") }?
+    : i:IDENT! OPEN! p:path AS! a:path! CLOSE! { i.getText().equals("treat") }? {
+        registerTreat( #p, #a );
+    }
     ;
 
 aggregate
 	: ( SUM^ | AVG^ | MAX^ | MIN^ ) OPEN! additiveExpression CLOSE! { #aggregate.setType(AGGREGATE); }
 	// Special case for count - It's 'parameters' can be keywords.
 	|  COUNT^ OPEN! ( STAR { #STAR.setType(ROW_STAR); } | ( ( DISTINCT | ALL )? ( path | collectionExpr | NUM_INT | caseExpression ) ) ) CLOSE!
 	|  collectionExpr
 	;
 
 //## collection: ( OPEN query CLOSE ) | ( 'elements'|'indices' OPEN path CLOSE );
 
 collectionExpr
 	: (ELEMENTS^ | INDICES^) OPEN! path CLOSE!
 	;
                                            
 // NOTE: compoundExpr can be a 'path' where the last token in the path is '.elements' or '.indicies'
 compoundExpr
 	: collectionExpr
 	| path
 	| { LA(1) == OPEN && LA(2) == CLOSE }? OPEN! CLOSE!
 	| (OPEN! ( (expression (COMMA! expression)*) | subQuery ) CLOSE!)
 	| parameter
 	;
 
 subQuery
 	: union
 	{ #subQuery = #([QUERY,"query"], #subQuery); }
 	;
 
 exprList
 {
    AST trimSpec = null;
 }
 	: (t:TRAILING {#trimSpec = #t;} | l:LEADING {#trimSpec = #l;} | b:BOTH {#trimSpec = #b;})?
 	  		{ if(#trimSpec != null) #trimSpec.setType(IDENT); }
 	  ( 
 	  		expression ( (COMMA! expression)+ | FROM { #FROM.setType(IDENT); } expression | AS! identifier )? 
 	  		| FROM { #FROM.setType(IDENT); } expression
 	  )?
 			{ #exprList = #([EXPR_LIST,"exprList"], #exprList); }
 	;
 
 constant
 	: NUM_INT
 	| NUM_FLOAT
 	| NUM_LONG
 	| NUM_DOUBLE
 	| NUM_BIG_INTEGER
 	| NUM_BIG_DECIMAL
 	| QUOTED_STRING
 	| NULL
 	| TRUE
 	| FALSE
 	| EMPTY
 	;
 
 //## quantifiedExpression: 'exists' | ( expression 'in' ) | ( expression OP 'any' | 'some' ) collection;
 
 //## compoundPath: path ( OPEN_BRACKET expression CLOSE_BRACKET ( '.' path )? )*;
 
 //## path: identifier ( '.' identifier )*;
 
 path
 	: identifier ( DOT^ { weakKeywords(); } identifier )*
 	;
 
 // Wraps the IDENT token from the lexer, in order to provide
 // 'keyword as identifier' trickery.
 identifier
 	: IDENT
 	exception
 	catch [RecognitionException ex]
 	{
 		identifier_AST = handleIdentifierError(LT(1),ex);
 	}
 	;
 
 // **** LEXER ******************************************************************
 
 /**
  * Hibernate Query Language Lexer
  * <br>
  * This lexer provides the HQL parser with tokens.
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 class HqlBaseLexer extends Lexer;
 
 options {
 	exportVocab=Hql;      // call the vocabulary "Hql"
 	testLiterals = false;
 	k=2; // needed for newline, and to distinguish '>' from '>='.
 	// HHH-241 : Quoted strings don't allow unicode chars - This should fix it.
 	charVocabulary='\u0000'..'\uFFFE';	// Allow any char but \uFFFF (16 bit -1, ANTLR's EOF character)
 	caseSensitive = false;
 	caseSensitiveLiterals = false;
 }
 
 // -- Declarations --
 {
 	// NOTE: The real implementations are in the subclass.
 	protected void setPossibleID(boolean possibleID) {}
 }
 
 // -- Keywords --
 
 EQ: '=';
 LT: '<';
 GT: '>';
 SQL_NE: "<>";
 NE: "!=" | "^=";
 LE: "<=";
 GE: ">=";
 
 COMMA: ',';
 
 OPEN: '(';
 CLOSE: ')';
 OPEN_BRACKET: '[';
 CLOSE_BRACKET: ']';
 
 CONCAT: "||";
 PLUS: '+';
 MINUS: '-';
 STAR: '*';
 DIV: '/';
 MOD: '%';
 COLON: ':';
 PARAM: '?';
 
 IDENT options { testLiterals=true; }
 	: ID_START_LETTER ( ID_LETTER )*
 		{
     		// Setting this flag allows the grammar to use keywords as identifiers, if necessary.
 			setPossibleID(true);
 		}
 	;
 
 protected
 ID_START_LETTER
     :    '_'
     |    '$'
     |    'a'..'z'
     |    '\u0080'..'\ufffe'       // HHH-558 : Allow unicode chars in identifiers
     ;
 
 protected
 ID_LETTER
     :    ID_START_LETTER
     |    '0'..'9'
     ;
 
 QUOTED_STRING
 	  : '\'' ( (ESCqs)=> ESCqs | ~'\'' )* '\''
 	;
 
 protected
 ESCqs
 	:
 		'\'' '\''
 	;
 
 WS  :   (   ' '
 		|   '\t'
 		|   '\r' '\n' { newline(); }
 		|   '\n'      { newline(); }
 		|   '\r'      { newline(); }
 		)
 		{$setType(Token.SKIP);} //ignore this token
 	;
 
 //--- From the Java example grammar ---
 // a numeric literal
 NUM_INT
 	{boolean isDecimal=false; Token t=null;}
 	:   '.' {_ttype = DOT;}
 			(	('0'..'9')+ (EXPONENT)? (f1:FLOAT_SUFFIX {t=f1;})?
 				{
 					if ( t != null && t.getText().toUpperCase().indexOf("BD")>=0) {
 						_ttype = NUM_BIG_DECIMAL;
 					}
 					else if (t != null && t.getText().toUpperCase().indexOf('F')>=0) {
 						_ttype = NUM_FLOAT;
 					}
 					else {
 						_ttype = NUM_DOUBLE; // assume double
 					}
 				}
 			)?
 	|	(	'0' {isDecimal = true;} // special case for just '0'
 			(	('x')
 				(											// hex
 					// the 'e'|'E' and float suffix stuff look
 					// like hex digits, hence the (...)+ doesn't
 					// know when to stop: ambig.  ANTLR resolves
 					// it correctly by matching immediately.  It
 					// is therefore ok to hush warning.
 					options { warnWhenFollowAmbig=false; }
 				:	HEX_DIGIT
 				)+
 			|	('0'..'7')+									// octal
 			)?
 		|	('1'..'9') ('0'..'9')*  {isDecimal=true;}		// non-zero decimal
 		)
 		(	('l') { _ttype = NUM_LONG; }
 		|	('b''i') { _ttype = NUM_BIG_INTEGER; }
 
 		// only check to see if it's a float if looks like decimal so far
 		|	{isDecimal}?
 			(   '.' ('0'..'9')* (EXPONENT)? (f2:FLOAT_SUFFIX {t=f2;})?
 			|   EXPONENT (f3:FLOAT_SUFFIX {t=f3;})?
 			|   f4:FLOAT_SUFFIX {t=f4;}
 			)
 			{
 				if ( t != null && t.getText().toUpperCase().indexOf("BD")>=0) {
 					_ttype = NUM_BIG_DECIMAL;
 				}
 				else if (t != null && t.getText().toUpperCase() .indexOf('F') >= 0) {
 					_ttype = NUM_FLOAT;
 				}
 				else {
 					_ttype = NUM_DOUBLE; // assume double
 				}
 			}
 		)?
 	;
 
 // hexadecimal digit (again, note it's protected!)
 protected
 HEX_DIGIT
 	:	('0'..'9'|'a'..'f')
 	;
 
 // a couple protected methods to assist in matching floating point numbers
 protected
 EXPONENT
 	:	('e') ('+'|'-')? ('0'..'9')+
 	;
 
 protected
 FLOAT_SUFFIX
 	:	'f'|'d'|'b''d'
 	;
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/JoinSequence.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/JoinSequence.java
index 0c95170394..a863d570fc 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/JoinSequence.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/JoinSequence.java
@@ -1,449 +1,473 @@
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
 package org.hibernate.engine.internal;
 
 import java.util.ArrayList;
 import java.util.Collections;
+import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.QueryJoinFragment;
 import org.hibernate.type.AssociationType;
 
 /**
  * A sequence of {@link Join} delegates to make it "easier" to work with joins.  The "easier" part is obviously
  * subjective ;)
  * <p/>
  * Additionally JoinSequence is a directed graph of other JoinSequence instances, as represented by the
  * {@link #next} ({@link #setNext(JoinSequence)}) pointer.
  *
  * @author Gavin King
  * @author Steve Ebersole
  *
  * @see JoinFragment
  */
 public class JoinSequence {
     private final SessionFactoryImplementor factory;
 
 	private final StringBuilder conditions = new StringBuilder();
 	private final List<Join> joins = new ArrayList<Join>();
 
 	private boolean useThetaStyle;
 	private String rootAlias;
 	private Joinable rootJoinable;
 	private Selector selector;
 	private JoinSequence next;
 	private boolean isFromPart;
 
 	/**
 	 * Constructs a JoinSequence
 	 *
 	 * @param factory The SessionFactory
 	 */
 	public JoinSequence(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * Retrieve a JoinSequence that represents just the FROM clause parts
 	 *
 	 * @return The JoinSequence that represents just the FROM clause parts
 	 */
 	public JoinSequence getFromPart() {
 		final JoinSequence fromPart = new JoinSequence( factory );
 		fromPart.joins.addAll( this.joins );
 		fromPart.useThetaStyle = this.useThetaStyle;
 		fromPart.rootAlias = this.rootAlias;
 		fromPart.rootJoinable = this.rootJoinable;
 		fromPart.selector = this.selector;
 		fromPart.next = this.next == null ? null : this.next.getFromPart();
 		fromPart.isFromPart = true;
 		return fromPart;
 	}
 
+	private Set<String> treatAsDeclarations;
+
+	public void applyTreatAsDeclarations(Set<String> treatAsDeclarations) {
+		if ( treatAsDeclarations == null || treatAsDeclarations.isEmpty() ) {
+			return;
+		}
+
+		if ( this.treatAsDeclarations == null ) {
+			this.treatAsDeclarations = new HashSet<String>();
+		}
+
+		this.treatAsDeclarations.addAll( treatAsDeclarations );
+	}
+
+
 	/**
 	 * Create a full, although shallow, copy.
 	 *
 	 * @return The copy
 	 */
 	public JoinSequence copy() {
 		final JoinSequence copy = new JoinSequence( factory );
 		copy.joins.addAll( this.joins );
 		copy.useThetaStyle = this.useThetaStyle;
 		copy.rootAlias = this.rootAlias;
 		copy.rootJoinable = this.rootJoinable;
 		copy.selector = this.selector;
 		copy.next = this.next == null ? null : this.next.copy();
 		copy.isFromPart = this.isFromPart;
 		copy.conditions.append( this.conditions.toString() );
 		return copy;
 	}
 
 	/**
 	 * Add a join to this sequence
 	 *
 	 * @param associationType The type of the association representing the join
 	 * @param alias The RHS alias for the join
 	 * @param joinType The type of join (INNER, etc)
 	 * @param referencingKey The LHS columns for the join condition
 	 *
 	 * @return The Join memento
 	 *
 	 * @throws MappingException Generally indicates a problem resolving the associationType to a {@link Joinable}
 	 */
 	public JoinSequence addJoin(
 			AssociationType associationType,
 			String alias,
 			JoinType joinType,
 			String[] referencingKey) throws MappingException {
 		joins.add( new Join( factory, associationType, alias, joinType, referencingKey ) );
 		return this;
 	}
 
 	/**
 	 * Generate a JoinFragment
 	 *
 	 * @return The JoinFragment
 	 *
 	 * @throws MappingException Indicates a problem access the provided metadata, or incorrect metadata
 	 */
 	public JoinFragment toJoinFragment() throws MappingException {
 		return toJoinFragment( Collections.EMPTY_MAP, true );
 	}
 
 	/**
 	 * Generate a JoinFragment
 	 *
 	 * @param enabledFilters The filters associated with the originating session to properly define join conditions
-	 * @param includeExtraJoins Should {@link #addExtraJoins} to called.  Honestly I do not understand the full
-	 * ramifications of this argument
+	 * @param includeAllSubclassJoins Should all subclass joins be added to the rendered JoinFragment?
 	 *
 	 * @return The JoinFragment
 	 *
 	 * @throws MappingException Indicates a problem access the provided metadata, or incorrect metadata
 	 */
-	public JoinFragment toJoinFragment(Map enabledFilters, boolean includeExtraJoins) throws MappingException {
-		return toJoinFragment( enabledFilters, includeExtraJoins, null, null );
+	public JoinFragment toJoinFragment(Map enabledFilters, boolean includeAllSubclassJoins) throws MappingException {
+		return toJoinFragment( enabledFilters, includeAllSubclassJoins, null, null );
 	}
 
 	/**
 	 * Generate a JoinFragment
 	 *
 	 * @param enabledFilters The filters associated with the originating session to properly define join conditions
-	 * @param includeExtraJoins Should {@link #addExtraJoins} to called.  Honestly I do not understand the full
-	 * ramifications of this argument
+	 * @param includeAllSubclassJoins Should all subclass joins be added to the rendered JoinFragment?
 	 * @param withClauseFragment The with clause (which represents additional join restrictions) fragment
 	 * @param withClauseJoinAlias The
 	 *
 	 * @return The JoinFragment
 	 *
 	 * @throws MappingException Indicates a problem access the provided metadata, or incorrect metadata
 	 */
 	public JoinFragment toJoinFragment(
 			Map enabledFilters,
-			boolean includeExtraJoins,
+			boolean includeAllSubclassJoins,
 			String withClauseFragment,
 			String withClauseJoinAlias) throws MappingException {
 		final QueryJoinFragment joinFragment = new QueryJoinFragment( factory.getDialect(), useThetaStyle );
 		if ( rootJoinable != null ) {
 			joinFragment.addCrossJoin( rootJoinable.getTableName(), rootAlias );
-			final String filterCondition = rootJoinable.filterFragment( rootAlias, enabledFilters );
+			final String filterCondition = rootJoinable.filterFragment( rootAlias, enabledFilters, treatAsDeclarations );
 			// JoinProcessor needs to know if the where clause fragment came from a dynamic filter or not so it
 			// can put the where clause fragment in the right place in the SQL AST.   'hasFilterCondition' keeps track
 			// of that fact.
 			joinFragment.setHasFilterCondition( joinFragment.addCondition( filterCondition ) );
-			if ( includeExtraJoins ) {
-				//TODO: not quite sure about the full implications of this!
-				addExtraJoins( joinFragment, rootAlias, rootJoinable, true );
-			}
+			addSubclassJoins( joinFragment, rootAlias, rootJoinable, true, includeAllSubclassJoins, treatAsDeclarations );
 		}
 
 		Joinable last = rootJoinable;
 
 		for ( Join join : joins ) {
-			final String on = join.getAssociationType().getOnCondition( join.getAlias(), factory, enabledFilters );
+			// technically the treatAsDeclarations should only apply to rootJoinable or to a single Join,
+			// but that is not possible atm given how these JoinSequence and Join objects are built.
+			// However, it is generally ok given how the HQL parser builds these JoinSequences (a HQL join
+			// results in a JoinSequence with an empty rootJoinable and a single Join).  So we use that here
+			// as an assumption
+			final String on = join.getAssociationType().getOnCondition( join.getAlias(), factory, enabledFilters, treatAsDeclarations );
 			String condition;
 			if ( last != null
 					&& isManyToManyRoot( last )
 					&& ((QueryableCollection) last).getElementType() == join.getAssociationType() ) {
 				// the current join represents the join between a many-to-many association table
 				// and its "target" table.  Here we need to apply any additional filters
 				// defined specifically on the many-to-many
 				final String manyToManyFilter = ( (QueryableCollection) last ).getManyToManyFilterFragment(
 						join.getAlias(),
 						enabledFilters
 				);
 				condition = "".equals( manyToManyFilter )
 						? on
 						: "".equals( on ) ? manyToManyFilter : on + " and " + manyToManyFilter;
 			}
 			else {
 				condition = on;
 			}
 
 			if ( withClauseFragment != null && !isManyToManyRoot( join.joinable )) {
 				condition += " and " + withClauseFragment;
 			}
 
 			joinFragment.addJoin(
 					join.getJoinable().getTableName(),
 					join.getAlias(),
 					join.getLHSColumns(),
 					JoinHelper.getRHSColumnNames( join.getAssociationType(), factory ),
 					join.joinType,
 					condition
 			);
 
-			//TODO: not quite sure about the full implications of this!
-			if ( includeExtraJoins ) {
-				addExtraJoins(
-						joinFragment,
-						join.getAlias(),
-						join.getJoinable(),
-						join.joinType == JoinType.INNER_JOIN
-				);
-			}
+			addSubclassJoins(
+					joinFragment,
+					join.getAlias(),
+					join.getJoinable(),
+					join.joinType == JoinType.INNER_JOIN,
+					includeAllSubclassJoins,
+					// ugh.. this is needed because of how HQL parser (FromElementFactory/SessionFactoryHelper)
+					// builds the JoinSequence for HQL joins
+					treatAsDeclarations
+			);
 			last = join.getJoinable();
 		}
 
 		if ( next != null ) {
-			joinFragment.addFragment( next.toJoinFragment( enabledFilters, includeExtraJoins ) );
+			joinFragment.addFragment( next.toJoinFragment( enabledFilters, includeAllSubclassJoins ) );
 		}
 
 		joinFragment.addCondition( conditions.toString() );
 
 		if ( isFromPart ) {
 			joinFragment.clearWherePart();
 		}
 
 		return joinFragment;
 	}
 
 	@SuppressWarnings("SimplifiableIfStatement")
 	private boolean isManyToManyRoot(Joinable joinable) {
 		if ( joinable != null && joinable.isCollection() ) {
 			return ( (QueryableCollection) joinable ).isManyToMany();
 		}
 		return false;
 	}
 
-	private void addExtraJoins(JoinFragment joinFragment, String alias, Joinable joinable, boolean innerJoin) {
-		final boolean include = isIncluded( alias );
+	private void addSubclassJoins(
+			JoinFragment joinFragment,
+			String alias,
+			Joinable joinable,
+			boolean innerJoin,
+			boolean includeSubclassJoins,
+			Set<String> treatAsDeclarations) {
+		final boolean include = includeSubclassJoins && isIncluded( alias );
 		joinFragment.addJoins(
-				joinable.fromJoinFragment( alias, innerJoin, include ),
-				joinable.whereJoinFragment( alias, innerJoin, include )
+				joinable.fromJoinFragment( alias, innerJoin, include, treatAsDeclarations ),
+				joinable.whereJoinFragment( alias, innerJoin, include, treatAsDeclarations )
 		);
 	}
 
 	private boolean isIncluded(String alias) {
 		return selector != null && selector.includeSubclasses( alias );
 	}
 
 	/**
 	 * Add a condition to this sequence.
 	 *
 	 * @param condition The condition
 	 *
 	 * @return {@link this}, for method chaining
 	 */
 	public JoinSequence addCondition(String condition) {
 		if ( condition.trim().length() != 0 ) {
 			if ( !condition.startsWith( " and " ) ) {
 				conditions.append( " and " );
 			}
 			conditions.append( condition );
 		}
 		return this;
 	}
 
 	/**
 	 * Add a condition to this sequence.  Typical usage here might be:
 	 * <pre>
 	 *     addCondition( "a", {"c1", "c2"}, "?" )
 	 * </pre>
 	 * to represent:
 	 * <pre>
 	 *     "... a.c1 = ? and a.c2 = ? ..."
 	 * </pre>
 	 *
 	 * @param alias The alias to apply to the columns
 	 * @param columns The columns to add checks for
 	 * @param condition The conditions to check against the columns
 	 *
 	 * @return {@link this}, for method chaining
 	 */
 	public JoinSequence addCondition(String alias, String[] columns, String condition) {
 		for ( String column : columns ) {
 			conditions.append( " and " )
 					.append( alias )
 					.append( '.' )
 					.append( column )
 					.append( condition );
 		}
 		return this;
 	}
 
 	/**
 	 * Set the root of this JoinSequence.  In SQL terms, this would be the driving table.
 	 *
 	 * @param joinable The entity/collection that is the root of this JoinSequence
 	 * @param alias The alias associated with that joinable.
 	 *
 	 * @return {@link this}, for method chaining
 	 */
 	public JoinSequence setRoot(Joinable joinable, String alias) {
 		this.rootAlias = alias;
 		this.rootJoinable = joinable;
 		return this;
 	}
 
 	/**
 	 * Sets the next join sequence
 	 *
 	 * @param next The next JoinSequence in the directed graph
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public JoinSequence setNext(JoinSequence next) {
 		this.next = next;
 		return this;
 	}
 
 	/**
 	 * Set the Selector to use to determine how subclass joins should be applied.
 	 *
 	 * @param selector The selector to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public JoinSequence setSelector(Selector selector) {
 		this.selector = selector;
 		return this;
 	}
 
 	/**
 	 * Should this JoinSequence use theta-style joining (both a FROM and WHERE component) in the rendered SQL?
 	 *
 	 * @param useThetaStyle {@code true} indicates that theta-style joins should be used.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public JoinSequence setUseThetaStyle(boolean useThetaStyle) {
 		this.useThetaStyle = useThetaStyle;
 		return this;
 	}
 
 	public boolean isThetaStyle() {
 		return useThetaStyle;
 	}
 
 	public Join getFirstJoin() {
 		return joins.get( 0 );
 	}
 
 	/**
 	 * A subclass join selector
 	 */
 	public static interface Selector {
 		/**
 		 * Should subclasses be included in the rendered join sequence?
 		 *
 		 * @param alias The alias
 		 *
 		 * @return {@code true} if the subclass joins should be included
 		 */
 		public boolean includeSubclasses(String alias);
 	}
 
 	/**
 	 * Represents a join
 	 */
 	public static final class Join {
 		private final AssociationType associationType;
 		private final Joinable joinable;
 		private final JoinType joinType;
 		private final String alias;
 		private final String[] lhsColumns;
 
 		Join(
 				SessionFactoryImplementor factory,
 				AssociationType associationType,
 				String alias,
 				JoinType joinType,
 				String[] lhsColumns) throws MappingException {
 			this.associationType = associationType;
 			this.joinable = associationType.getAssociatedJoinable( factory );
 			this.alias = alias;
 			this.joinType = joinType;
 			this.lhsColumns = lhsColumns;
 		}
 
 		public String getAlias() {
 			return alias;
 		}
 
 		public AssociationType getAssociationType() {
 			return associationType;
 		}
 
 		public Joinable getJoinable() {
 			return joinable;
 		}
 
 		public JoinType getJoinType() {
 			return joinType;
 		}
 
 		public String[] getLHSColumns() {
 			return lhsColumns;
 		}
 
 		@Override
 		public String toString() {
 			return joinable.toString() + '[' + alias + ']';
 		}
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder buf = new StringBuilder();
 		buf.append( "JoinSequence{" );
 		if ( rootJoinable != null ) {
 			buf.append( rootJoinable )
 					.append( '[' )
 					.append( rootAlias )
 					.append( ']' );
 		}
 		for ( Join join : joins ) {
 			buf.append( "->" ).append( join );
 		}
 		return buf.append( '}' ).toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/EntityGraphQueryHint.java b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/EntityGraphQueryHint.java
index ef280baf8b..f3ee8a0935 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/EntityGraphQueryHint.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/EntityGraphQueryHint.java
@@ -1,137 +1,154 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.engine.query.spi;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import javax.persistence.AttributeNode;
 import javax.persistence.EntityGraph;
 import javax.persistence.Subgraph;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.tree.FromClause;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.FromElementFactory;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Encapsulates a JPA EntityGraph provided through a JPQL query hint.  Converts the fetches into a list of AST
  * FromElements.  The logic is kept here as much as possible in order to make it easy to remove this in the future,
  * once our AST is improved and this "hack" is no longer needed.
  * 
  * @author Brett Meyer
  */
 public class EntityGraphQueryHint {
 	
 	private final EntityGraph<?> originEntityGraph;
 	
 	public EntityGraphQueryHint( EntityGraph<?> originEntityGraph ) {
 		this.originEntityGraph = originEntityGraph;
 	}
 	
 	public List<FromElement> toFromElements(FromClause fromClause, HqlSqlWalker walker) {
 		// If a role already has an explicit fetch in the query, skip it in the graph.
 		Map<String, FromElement> explicitFetches = new HashMap<String, FromElement>();
 		Iterator iter = fromClause.getFromElements().iterator();
 		while ( iter.hasNext() ) {
 			final FromElement fromElement = ( FromElement ) iter.next();
 			if (fromElement.getRole() != null) {
 				explicitFetches.put( fromElement.getRole(), fromElement );
 			}
 		}
 		
-		return getFromElements( originEntityGraph.getAttributeNodes(), fromClause.getFromElement(), fromClause,
-				walker, explicitFetches );
+		return getFromElements(
+				originEntityGraph.getAttributeNodes(),
+				fromClause.getFromElement(),
+				fromClause,
+				walker,
+				explicitFetches
+		);
 	}
 	
-	private List<FromElement> getFromElements(List attributeNodes, FromElement origin, FromClause fromClause,
-			HqlSqlWalker walker, Map<String, FromElement> explicitFetches) {
+	private List<FromElement> getFromElements(
+			List attributeNodes,
+			FromElement origin,
+			FromClause fromClause,
+			HqlSqlWalker walker,
+			Map<String, FromElement> explicitFetches) {
 		final List<FromElement> fromElements = new ArrayList<FromElement>();
 		
 		for (Object obj : attributeNodes) {			
 			final AttributeNode<?> attributeNode = (AttributeNode<?>) obj;
 			
 			final String attributeName = attributeNode.getAttributeName();
 			final String className = origin.getClassName();
 			final String role = className + "." + attributeName;
 			
 			final String classAlias = origin.getClassAlias();
 			
 			final String originTableAlias = origin.getTableAlias();
 			
 			Type propertyType = origin.getPropertyType( attributeName, attributeName );
 			
 			try {
 				FromElement fromElement = null;
 				if (!explicitFetches.containsKey( role )) {
 					if ( propertyType.isEntityType() ) {
 						final EntityType entityType = (EntityType) propertyType;
 						
 						final String[] columns = origin.toColumns( originTableAlias, attributeName, false );
 						final String tableAlias = walker.getAliasGenerator().createName(
 								entityType.getAssociatedEntityName() );	
 						
 						final FromElementFactory fromElementFactory = new FromElementFactory( fromClause, origin,
 								attributeName, classAlias, columns, false);
 						final JoinSequence joinSequence = walker.getSessionFactoryHelper().createJoinSequence(
 								false, entityType, tableAlias, JoinType.LEFT_OUTER_JOIN, columns );
-						fromElement = fromElementFactory.createEntityJoin( entityType.getAssociatedEntityName(), tableAlias,
-								joinSequence, true, walker.isInFrom(), entityType, role );
+						fromElement = fromElementFactory.createEntityJoin(
+								entityType.getAssociatedEntityName(),
+								tableAlias,
+								joinSequence,
+								true,
+								walker.isInFrom(),
+								entityType,
+								role,
+								null
+						);
 					}
 					else if ( propertyType.isCollectionType() ) {					
 						final String[] columns = origin.toColumns( originTableAlias, attributeName, false );		
 						
 						final FromElementFactory fromElementFactory = new FromElementFactory( fromClause, origin,
 								attributeName, classAlias, columns, false);
 						final QueryableCollection queryableCollection = walker.getSessionFactoryHelper()
 								.requireQueryableCollection( role );
 						fromElement = fromElementFactory.createCollection(
 								queryableCollection, role, JoinType.LEFT_OUTER_JOIN, true, false ) ;
 					}
 				}
 				
 				if (fromElement != null) {
 					fromElements.add( fromElement );
 					
 					// recurse into subgraphs
 					for (Subgraph<?> subgraph : attributeNode.getSubgraphs().values()) {
 						fromElements.addAll( getFromElements( subgraph.getAttributeNodes(), fromElement,
 								fromClause, walker, explicitFetches ) );
 					}
 				}
 			}
 			catch (Exception e) {
 				throw new QueryException( "Could not apply the EntityGraph to the Query!", e );
 			}
 		}
 		
 		return fromElements;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java
index 0a886e0e13..5b71dca5f7 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java
@@ -1,405 +1,445 @@
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
 package org.hibernate.hql.internal.ast;
 
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.StringReader;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Map;
+import java.util.Set;
 
 import antlr.ASTPair;
 import antlr.MismatchedTokenException;
 import antlr.RecognitionException;
 import antlr.Token;
 import antlr.TokenStream;
 import antlr.TokenStreamException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.hql.internal.antlr.HqlBaseParser;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Implements the semantic action methods defined in the HQL base parser to keep the grammar
  * source file a little cleaner.  Extends the parser class generated by ANTLR.
  *
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 public final class HqlParser extends HqlBaseParser {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			HqlParser.class.getName()
 	);
 
 	private final ParseErrorHandler parseErrorHandler;
 	private final ASTPrinter printer = getASTPrinter();
 
 	private static ASTPrinter getASTPrinter() {
 		return new ASTPrinter( org.hibernate.hql.internal.antlr.HqlTokenTypes.class );
 	}
 
 	/**
 	 * Get a HqlParser instance for the given HQL string.
 	 *
 	 * @param hql The HQL query string
 	 *
 	 * @return The parser.
 	 */
 	public static HqlParser getInstance(String hql) {
 		return new HqlParser( hql );
 	}
 
 	private HqlParser(String hql) {
 		// The fix for HHH-558...
 		super( new HqlLexer( new StringReader( hql ) ) );
 		parseErrorHandler = new ErrorCounter( hql );
 		// Create nodes that track line and column number.
 		setASTFactory( new HqlASTFactory() );
 	}
 
 
 	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int traceDepth = 0;
 
 	@Override
 	public void traceIn(String ruleName) {
 		if ( !LOG.isTraceEnabled() ) return;
 		if ( inputState.guessing > 0 ) return;
 		String prefix = StringHelper.repeat( '-', ( traceDepth++ * 2 ) ) + "-> ";
 		LOG.trace( prefix + ruleName );
 	}
 
 	@Override
 	public void traceOut(String ruleName) {
 		if ( !LOG.isTraceEnabled() ) return;
 		if ( inputState.guessing > 0 ) return;
 		String prefix = "<-" + StringHelper.repeat( '-', ( --traceDepth * 2 ) ) + " ";
 		LOG.trace( prefix + ruleName );
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
 
 	public ParseErrorHandler getParseErrorHandler() {
 		return parseErrorHandler;
 	}
 
 	/**
 	 * Overrides the base behavior to retry keywords as identifiers.
 	 *
 	 * @param token The token.
 	 * @param ex    The recognition exception.
 	 * @return AST - The new AST.
 	 * @throws antlr.RecognitionException if the substitution was not possible.
 	 * @throws antlr.TokenStreamException if the substitution was not possible.
 	 */
 	@Override
     public AST handleIdentifierError(Token token, RecognitionException ex) throws RecognitionException, TokenStreamException {
 		// If the token can tell us if it could be an identifier...
 		if ( token instanceof HqlToken ) {
 			HqlToken hqlToken = ( HqlToken ) token;
 			// ... and the token could be an identifer and the error is
 			// a mismatched token error ...
 			if ( hqlToken.isPossibleID() && ( ex instanceof MismatchedTokenException ) ) {
 				MismatchedTokenException mte = ( MismatchedTokenException ) ex;
 				// ... and the expected token type was an identifier, then:
 				if ( mte.expecting == HqlTokenTypes.IDENT ) {
 					// Use the token as an identifier.
 					reportWarning( "Keyword  '"
 							+ token.getText()
 							+ "' is being interpreted as an identifier due to: " + mte.getMessage() );
 					// Add the token to the AST.
 					ASTPair currentAST = new ASTPair();
 					token.setType( HqlTokenTypes.WEIRD_IDENT );
 					astFactory.addASTChild( currentAST, astFactory.create( token ) );
 					consume();
 					AST identifierAST = currentAST.root;
 					return identifierAST;
 				}
 			} // if
 		} // if
 		// Otherwise, handle the error normally.
 		return super.handleIdentifierError( token, ex );
 	}
 
 	/**
 	 * Returns an equivalent tree for (NOT (a relop b) ), for example:<pre>
 	 * (NOT (GT a b) ) => (LE a b)
 	 * </pre>
 	 *
 	 * @param x The sub tree to transform, the parent is assumed to be NOT.
 	 * @return AST - The equivalent sub-tree.
 	 */
 	@Override
     public AST negateNode(AST x) {
 		//TODO: switch statements are always evil! We already had bugs because
 		//      of forgotten token types. Use polymorphism for this!
 		switch ( x.getType() ) {
 			case OR:
 				x.setType(AND);
 				x.setText("{and}");
                 x.setFirstChild(negateNode( x.getFirstChild() ));
                 x.getFirstChild().setNextSibling(negateNode( x.getFirstChild().getNextSibling() ));
                 return x;
 			case AND:
 				x.setType(OR);
 				x.setText("{or}");
                 x.setFirstChild(negateNode( x.getFirstChild() ));
                 x.getFirstChild().setNextSibling(negateNode( x.getFirstChild().getNextSibling() ));
 				return x;
 			case EQ:
 				x.setType( NE );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (EQ a b) ) => (NE a b)
 			case NE:
 				x.setType( EQ );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (NE a b) ) => (EQ a b)
 			case GT:
 				x.setType( LE );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (GT a b) ) => (LE a b)
 			case LT:
 				x.setType( GE );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (LT a b) ) => (GE a b)
 			case GE:
 				x.setType( LT );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (GE a b) ) => (LT a b)
 			case LE:
 				x.setType( GT );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (LE a b) ) => (GT a b)
 			case LIKE:
 				x.setType( NOT_LIKE );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (LIKE a b) ) => (NOT_LIKE a b)
 			case NOT_LIKE:
 				x.setType( LIKE );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (NOT_LIKE a b) ) => (LIKE a b)
 			case IN:
 				x.setType( NOT_IN );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			case NOT_IN:
 				x.setType( IN );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			case IS_NULL:
 				x.setType( IS_NOT_NULL );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (IS_NULL a b) ) => (IS_NOT_NULL a b)
 			case IS_NOT_NULL:
 				x.setType( IS_NULL );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (IS_NOT_NULL a b) ) => (IS_NULL a b)
 			case BETWEEN:
 				x.setType( NOT_BETWEEN );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (BETWEEN a b) ) => (NOT_BETWEEN a b)
 			case NOT_BETWEEN:
 				x.setType( BETWEEN );
 				x.setText( "{not}" + x.getText() );
 				return x;	// (NOT (NOT_BETWEEN a b) ) => (BETWEEN a b)
 /* This can never happen because this rule will always eliminate the child NOT.
 			case NOT:
 				return x.getFirstChild();			// (NOT (NOT x) ) => (x)
 */
 			default:
 				AST not = super.negateNode( x );		// Just add a 'not' parent.
                 if ( not != x ) {
                    // relink the next sibling to the new 'not' parent
                     not.setNextSibling(x.getNextSibling());
                     x.setNextSibling(null);
                 }
                 return not;
 		}
 	}
 
 	/**
 	 * Post process equality expressions, clean up the subtree.
 	 *
 	 * @param x The equality expression.
 	 * @return AST - The clean sub-tree.
 	 */
 	@Override
     public AST processEqualityExpression(AST x) {
 		if ( x == null ) {
             LOG.processEqualityExpression();
 			return null;
 		}
 
 		int type = x.getType();
 		if ( type == EQ || type == NE ) {
 			boolean negated = type == NE;
 			if ( x.getNumberOfChildren() == 2 ) {
 				AST a = x.getFirstChild();
 				AST b = a.getNextSibling();
 				// (EQ NULL b) => (IS_NULL b)
 				if ( a.getType() == NULL && b.getType() != NULL ) {
 					return createIsNullParent( b, negated );
 				}
 				// (EQ a NULL) => (IS_NULL a)
 				else if ( b.getType() == NULL && a.getType() != NULL ) {
 					return createIsNullParent( a, negated );
 				}
 				else if ( b.getType() == EMPTY ) {
 					return processIsEmpty( a, negated );
 				}
 				else {
 					return x;
 				}
 			}
 			else {
 				return x;
 			}
 		}
 		else {
 			return x;
 		}
 	}
 
 	private AST createIsNullParent(AST node, boolean negated) {
 		node.setNextSibling( null );
 		int type = negated ? IS_NOT_NULL : IS_NULL;
 		String text = negated ? "is not null" : "is null";
 		return ASTUtil.createParent( astFactory, type, text, node );
 	}
 
 	private AST processIsEmpty(AST node, boolean negated) {
 		node.setNextSibling( null );
 		// NOTE: Because we're using ASTUtil.createParent(), the tree must be created from the bottom up.
 		// IS EMPTY x => (EXISTS (QUERY (SELECT_FROM (FROM x) ) ) )
 		AST ast = createSubquery( node );
 		ast = ASTUtil.createParent( astFactory, EXISTS, "exists", ast );
 		// Add NOT if it's negated.
 		if ( !negated ) {
 			ast = ASTUtil.createParent( astFactory, NOT, "not", ast );
 		}
 		return ast;
 	}
 
 	private AST createSubquery(AST node) {
 		AST ast = ASTUtil.createParent( astFactory, RANGE, "RANGE", node );
 		ast = ASTUtil.createParent( astFactory, FROM, "from", ast );
 		ast = ASTUtil.createParent( astFactory, SELECT_FROM, "SELECT_FROM", ast );
 		ast = ASTUtil.createParent( astFactory, QUERY, "QUERY", ast );
 		return ast;
 	}
 
 	public void showAst(AST ast, PrintStream out) {
 		showAst( ast, new PrintWriter( out ) );
 	}
 
 	private void showAst(AST ast, PrintWriter pw) {
 		printer.showAst( ast, pw );
 	}
 
 	@Override
     public void weakKeywords() throws TokenStreamException {
 
 		int t = LA( 1 );
 		switch ( t ) {
 			case ORDER:
 			case GROUP:
 				// Case 1: Multi token keywords GROUP BY and ORDER BY
 				// The next token ( LT(2) ) should be 'by'... otherwise, this is just an ident.
 				if ( LA( 2 ) != LITERAL_by ) {
 					LT( 1 ).setType( IDENT );
 					if ( LOG.isDebugEnabled() ) {
 						LOG.debugf( "weakKeywords() : new LT(1) token - %s", LT( 1 ) );
 					}
 				}
 				break;
 			default:
 				// Case 2: The current token is after FROM and before '.'.
 			if ( LA( 0 ) == FROM && t != IDENT && LA( 2 ) == DOT ) {
 				HqlToken hqlToken = (HqlToken) LT( 1 );
 				if ( hqlToken.isPossibleID() ) {
 					hqlToken.setType( IDENT );
 					if ( LOG.isDebugEnabled() ) {
 						LOG.debugf( "weakKeywords() : new LT(1) token - %s", LT( 1 ) );
 					}
 				}
 			}
 			break;
 		}
 	}
 
 	@Override
 	public void handleDotIdent() throws TokenStreamException {
 		// This handles HHH-354, where there is a strange property name in a where clause.
 		// If the lookahead contains a DOT then something that isn't an IDENT...
 		if ( LA( 1 ) == DOT && LA( 2 ) != IDENT ) {
 			// See if the second lookahead token can be an identifier.
 			HqlToken t = (HqlToken) LT( 2 );
 			if ( t.isPossibleID() )
 			{
 				// Set it!
 				LT( 2 ).setType( IDENT );
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "handleDotIdent() : new LT(2) token - %s", LT( 1 ) );
 				}
 			}
 		}
 	}
 
 	@Override
     public void processMemberOf(Token n, AST p, ASTPair currentAST) {
 		// convert MEMBER OF to the equivalent IN ELEMENTS structure...
 		AST inNode = n == null ? astFactory.create( IN, "in" ) : astFactory.create( NOT_IN, "not in" );
 		astFactory.makeASTRoot( currentAST, inNode );
 
 		AST inListNode = astFactory.create( IN_LIST, "inList" );
 		inNode.addChild( inListNode );
 		AST elementsNode = astFactory.create( ELEMENTS, "elements" );
 		inListNode.addChild( elementsNode );
 		elementsNode.addChild( p );
 	}
 
+	private Map<String,Set<String>> treatMap;
+
+	@Override
+	protected void registerTreat(AST pathToTreat, AST treatAs) {
+		final String path = toPathText( pathToTreat );
+		final String subclassName = toPathText( treatAs );
+		LOG.debugf( "Registering discovered request to treat(%s as %s)", path, subclassName );
+
+		if ( treatMap == null ) {
+			treatMap = new HashMap<String, Set<String>>();
+		}
+
+		Set<String> subclassNames = treatMap.get( path );
+		if ( subclassNames == null ) {
+			subclassNames = new HashSet<String>();
+			treatMap.put( path, subclassNames );
+		}
+		subclassNames.add( subclassName );
+	}
+
+	private String toPathText(AST node) {
+		final String text = node.getText();
+		if ( text.equals( "." )
+				&& node.getFirstChild() != null
+				&& node.getFirstChild().getNextSibling() != null
+				&& node.getFirstChild().getNextSibling().getNextSibling() == null ) {
+			return toPathText( node.getFirstChild() ) + '.' + toPathText( node.getFirstChild().getNextSibling() );
+		}
+		return text;
+	}
+
+	public Map<String, Set<String>> getTreatMap() {
+		return treatMap == null ? Collections.<String, Set<String>>emptyMap() : treatMap;
+	}
+
 	static public void panic() {
 		//overriden to avoid System.exit
 		throw new QueryException("Parser: panic");
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
index 4cb8e6c656..ef7ed284d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
@@ -283,1004 +283,1008 @@ public class HqlSqlWalker extends HqlSqlBaseWalker implements ErrorReporter, Par
 
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
 		fromElement.setAllPropertyFetch(propertyFetch!=null);
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
 			join.addJoin( ( AssociationType ) persister.getElementType(),
 					fromElement.getTableAlias(),
 					JoinType.INNER_JOIN,
 					persister.getElementColumnNames( fkTableAlias ) );
 		}
 		join.addCondition( fkTableAlias, keyColumnNames, " = ?" );
 		fromElement.setJoinSequence( join );
 		fromElement.setFilter( true );
         LOG.debug("createFromFilterElement() : processed filter FROM element.");
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
 		// The path AST should be a DotNode, and it should have been evaluated already.
 		if ( path.getType() != SqlTokenTypes.DOT ) {
 			throw new SemanticException( "Path expected for join!" );
 		}
 		DotNode dot = ( DotNode ) path;
 		JoinType hibernateJoinType = JoinProcessor.toHibernateJoinType( joinType );
 		dot.setJoinType( hibernateJoinType );	// Tell the dot node about the join type.
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
 			fromElement = factory.createComponentJoin( (ComponentType) dot.getDataType() );
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
 			LOG.debug("createFromJoinElement() : " + getASTPrinter().showAsString(fromElement, "-- join tree --") );
 		}
 	}
 
 	private void handleWithFragment(FromElement fromElement, AST hqlWithNode) throws SemanticException {
 		try {
 			withClause( hqlWithNode );
 			AST hqlSqlWithNode = returnAST;
             if ( LOG.isDebugEnabled() ) {
 				LOG.debug( "handleWithFragment() : " + getASTPrinter().showAsString(hqlSqlWithNode, "-- with clause --") );
 			}
 			WithClauseVisitor visitor = new WithClauseVisitor( fromElement, queryTranslatorImpl );
 			NodeTraverser traverser = new NodeTraverser( visitor );
 			traverser.traverseDepthFirst( hqlSqlWithNode );
 
 			String withClauseJoinAlias = visitor.getJoinAlias();
 			if ( withClauseJoinAlias == null ) {
 				withClauseJoinAlias = fromElement.getCollectionTableAlias();
 			}
 			else {
 				FromElement referencedFromElement = visitor.getReferencedFromElement();
 				if ( referencedFromElement != fromElement ) {
 					LOG.warnf(
 							"with-clause expressions do not reference the from-clause element to which the " +
 									"with-clause was associated.  The query may not work as expected [%s]",
 							queryTranslatorImpl.getQueryString()
 					);
 				}
 			}
 
 			SqlGenerator sql = new SqlGenerator( getSessionFactoryHelper().getFactory() );
 			sql.whereExpr( hqlSqlWithNode.getFirstChild() );
 
 			fromElement.setWithClauseFragment( withClauseJoinAlias, "(" + sql.getSQL() + ")" );
 		}
 		catch( SemanticException e ) {
 			throw e;
 		}
 		catch( InvalidWithClauseException e ) {
 			throw e;
 		}
 		catch ( Exception e) {
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
 				DotNode dotNode = ( DotNode ) node;
 				FromElement fromElement = dotNode.getFromElement();
 				if ( referencedFromElement != null ) {
 					if ( fromElement != referencedFromElement ) {
 						throw new HibernateException( "with-clause referenced two different from-clause elements" );
 					}
 				}
 				else {
 					referencedFromElement = fromElement;
 					joinAlias = extractAppliedAlias( dotNode );
                     // TODO : temporary
 					//      needed because currently persister is the one that
                     // creates and renders the join fragments for inheritance
 					//      hierarchies...
 					if ( !joinAlias.equals( referencedFromElement.getTableAlias() ) ) {
 						throw new InvalidWithClauseException(
 								"with clause can only reference columns in the driving table",
 								queryTranslatorImpl.getQueryString()
 						);
 					}
 				}
 			}
 			else if ( node instanceof ParameterNode ) {
 				applyParameterSpecification( ( ( ParameterNode ) node ).getHqlParameterSpecification() );
 			}
 			else if ( node instanceof ParameterContainer ) {
 				applyParameterSpecifications( ( ParameterContainer ) node );
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
 	 * @param fromNode      The new 'FROM' context.
 	 * @param inputFromNode The from node from the input AST.
 	 */
 	@Override
     protected void pushFromClause(AST fromNode, AST inputFromNode) {
 		FromClause newFromClause = ( FromClause ) fromNode;
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
 		FromReferenceNode aliasRefNode = ( FromReferenceNode ) aliasRef;
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
 		DotNode dotNode = ( DotNode ) dot;
 		FromReferenceNode lhs = dotNode.getLhs();
 		AST rhs = lhs.getNextSibling();
 		switch ( rhs.getType() ) {
 			case SqlTokenTypes.ELEMENTS:
 			case SqlTokenTypes.INDICES:
                 if (LOG.isDebugEnabled()) LOG.debugf("lookupProperty() %s => %s(%s)",
                                                      dotNode.getPath(),
                                                      rhs.getText(),
                                                      lhs.getPath());
 				CollectionFunction f = ( CollectionFunction ) rhs;
 				// Re-arrange the tree so that the collection function is the root and the lhs is the path.
 				f.setFirstChild( lhs );
 				lhs.setNextSibling( null );
 				dotNode.setFirstChild( f );
 				resolve( lhs );			// Don't forget to resolve the argument!
 				f.resolve( inSelect );	// Resolve the collection function now.
 				return f;
 			default:
 				// Resolve everything up to this dot, but don't resolve the placeholders yet.
 				dotNode.resolveFirstChild();
 				return dotNode;
 		}
 	}
 
 	@Override
     protected boolean isNonQualifiedPropertyRef(AST ident) {
 		final String identText = ident.getText();
 		if ( currentFromClause.isFromElementAlias( identText ) ) {
 			return false;
 		}
 
 		List fromElements = currentFromClause.getExplicitFromElements();
 		if ( fromElements.size() == 1 ) {
 			final FromElement fromElement = ( FromElement ) fromElements.get( 0 );
 			try {
 				LOG.tracev( "Attempting to resolve property [{0}] as a non-qualified ref", identText );
 				return fromElement.getPropertyMapping( identText ).toType( identText ) != null;
 			}
 			catch( QueryException e ) {
 				// Should mean that no such property was found
 			}
 		}
 
 		return false;
 	}
 
 	@Override
 	protected AST lookupNonQualifiedProperty(AST property) throws SemanticException {
 		final FromElement fromElement = ( FromElement ) currentFromClause.getExplicitFromElements().get( 0 );
 		AST syntheticDotNode = generateSyntheticDotNodeForNonQualifiedPropertyRef( property, fromElement );
 		return lookupProperty( syntheticDotNode, false, getCurrentClauseType() == HqlSqlTokenTypes.SELECT );
 	}
 
 	private AST generateSyntheticDotNodeForNonQualifiedPropertyRef(AST property, FromElement fromElement) {
 		AST dot = getASTFactory().create( DOT, "{non-qualified-property-ref}" );
 		// TODO : better way?!?
 		( ( DotNode ) dot ).setPropertyPath( ( ( FromReferenceNode ) property ).getPath() );
 
 		IdentNode syntheticAlias = ( IdentNode ) getASTFactory().create( IDENT, "{synthetic-alias}" );
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
 			QueryNode qn = ( QueryNode ) query;
 
 			// Was there an explicit select expression?
 			boolean explicitSelect = select != null && select.getNumberOfChildren() > 0;
 			
 			// Add in the EntityGraph attribute nodes.
 			if (queryTranslatorImpl.getEntityGraphQueryHint() != null) {
 				qn.getFromClause().getFromElements().addAll(
 						queryTranslatorImpl.getEntityGraphQueryHint().toFromElements( qn.getFromClause(), this ) );
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
 				final FromElement fromElement = ( FromElement ) itr.next();
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
 
 		FromElement fromElement = ( FromElement ) statement.getFromClause().getFromElements().get( 0 );
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
 		if ( persister.getDiscriminatorType() != null || ! queryTranslatorImpl.getEnabledFilters().isEmpty() ) {
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
 		UpdateStatement updateStatement = ( UpdateStatement ) update;
 
 		postProcessDML( updateStatement );
 	}
 
 	@Override
     protected void postProcessDelete(AST delete) throws SemanticException {
 		postProcessDML( ( DeleteStatement ) delete );
 	}
 
 	@Override
     protected void postProcessInsert(AST insert) throws SemanticException, QueryException {
 		InsertStatement insertStatement = ( InsertStatement ) insert;
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
 			if ( ! capableGenerator.supportsBulkInsertionIdentifierGeneration() ) {
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
 			while(child != null) {
 				if(child instanceof ParameterNode) {
 					// infer the parameter type from the type listed in the INSERT INTO clause
 					((ParameterNode)child).setExpectedType(insertStatement.getIntoClause()
 							.getInsertionTypes()[selectClause.getParameterPositions().get(i)]);
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
 				int sqlTypes[] = versionType.sqlTypes( sessionFactoryHelper.getFactory() );
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
 				( ( ParameterNode ) versionValueNode ).setHqlParameterSpecification( paramSpec );
 				parameters.add( 0, paramSpec );
 
 				if ( sessionFactoryHelper.getFactory().getDialect().requiresCastingOfParametersInSelectClause() ) {
 					// we need to wrtap the param in a cast()
 					MethodNode versionMethodNode = ( MethodNode ) getASTFactory().create( HqlSqlTokenTypes.METHOD_CALL, "(" );
 					AST methodIdentNode = getASTFactory().create( HqlSqlTokenTypes.IDENT, "cast" );
 					versionMethodNode.addChild( methodIdentNode );
 					versionMethodNode.initializeMethodNode(methodIdentNode, true );
 					AST castExprListNode = getASTFactory().create( HqlSqlTokenTypes.EXPR_LIST, "exprList" );
 					methodIdentNode.setNextSibling( castExprListNode );
 					castExprListNode.addChild( versionValueNode );
 					versionValueNode.setNextSibling(
 							getASTFactory().create(
 									HqlSqlTokenTypes.IDENT,
 									sessionFactoryHelper.getFactory().getDialect().getTypeName( sqlTypes[0] ) )
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
 					catch( Throwable t ) {
 						throw new QueryException( "could not determine seed value for version on bulk insert [" + versionType + "]" );
 					}
 				}
 				else if ( isDatabaseGeneratedTimestamp( versionType ) ) {
 					String functionName = sessionFactoryHelper.getFactory().getDialect().getCurrentTimestampSQLFunctionName();
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
 		selectClause = ( SelectClause ) select;
 		selectClause.initializeExplicitSelectClause( currentFromClause );
 	}
 
 	private void createSelectClauseFromFromClause(QueryNode qn) throws SemanticException {
 		AST select = astFactory.create( SELECT_CLAUSE, "{derived select clause}" );
 		AST sibling = qn.getFromClause();
 		qn.setFirstChild( select );
 		select.setNextSibling( sibling );
 		selectClause = ( SelectClause ) select;
 		selectClause.initializeDerivedSelectClause( currentFromClause );
 		LOG.debug( "Derived SELECT clause created." );
 	}
 
 	@Override
     protected void resolve(AST node) throws SemanticException {
 		if ( node != null ) {
 			// This is called when it's time to fully resolve a path expression.
 			ResolvableNode r = ( ResolvableNode ) node;
 			if ( isInFunctionCall() ) {
 				r.resolveInFunctionCall( false, true );
 			}
 			else {
 				r.resolve( false, true );	// Generate implicit joins, only if necessary.
 			}
 		}
 	}
 
 	@Override
     protected void resolveSelectExpression(AST node) throws SemanticException {
 		// This is called when it's time to fully resolve a path expression.
 		int type = node.getType();
 		switch ( type ) {
 			case DOT: {
 				DotNode dot = ( DotNode ) node;
 				dot.resolveSelectExpression();
 				break;
 			}
 			case ALIAS_REF: {
 				// Notify the FROM element that it is being referenced by the select.
 				FromReferenceNode aliasRefNode = ( FromReferenceNode ) node;
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
 			FromElement fromElement = ( FromElement ) iterator.next();
 			fromElement.setIncludeSubclasses( false );
 		}
 	}
 
 	@Override
     protected AST generatePositionalParameter(AST inputNode) throws SemanticException {
 		if ( namedParameters.size() > 0 ) {
 			throw new SemanticException( "cannot define positional parameter after any named parameters have been defined" );
 		}
 		LOG.warnf(
 				"[DEPRECATION] Encountered positional parameter near line %s, column %s.  Positional parameter " +
 						"are considered deprecated; use named parameters or JPA-style positional parameters instead.",
 				inputNode.getLine(),
 				inputNode.getColumn()
 		);
 		ParameterNode parameter = ( ParameterNode ) astFactory.create( PARAM, "?" );
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
 		ParameterNode parameter = ( ParameterNode ) astFactory.create( NAMED_PARAM, name );
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
 			( ( ArrayList ) o ).add( loc );
 		}
 	}
 
 	@Override
     protected void processConstant(AST constant) throws SemanticException {
 		literalProcessor.processConstant( constant, true );  // Use the delegate, resolve identifiers as FROM element aliases.
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
 		IndexNode indexNode = ( IndexNode ) indexOp;
 		indexNode.resolve( true, true );
 	}
 
 	@Override
     protected void processFunction(AST functionCall, boolean inSelect) throws SemanticException {
 		MethodNode methodNode = ( MethodNode ) functionCall;
 		methodNode.resolve( inSelect );
 	}
 
 	@Override
     protected void processAggregation(AST node, boolean inSelect) throws SemanticException {
 		AggregateNode aggregateNode = ( AggregateNode ) node;
 		aggregateNode.resolve();
 	}
 
 	@Override
     protected void processConstructor(AST constructor) throws SemanticException {
 		ConstructorNode constructorNode = ( ConstructorNode ) constructor;
 		constructorNode.prepare();
 	}
 
     @Override
     protected void setAlias(AST selectExpr, AST ident) {
         ((SelectExpression) selectExpr).setAlias(ident.getText());
 		// only put the alias (i.e., result variable) in selectExpressionsByResultVariable
 		// if is not defined in a subquery.
 		if ( ! isSubQuery() ) {
 			selectExpressionsByResultVariable.put( ident.getText(), ( SelectExpression ) selectExpr );
 		}
     }
 
 	@Override
     protected boolean isOrderExpressionResultVariableRef(AST orderExpressionNode) throws SemanticException {
 		// ORDER BY is not supported in a subquery
 		// TODO: should an exception be thrown if an ORDER BY is in a subquery?
 		if ( ! isSubQuery() &&
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
 		( ( ResultVariableRefNode ) resultVariableRef ).setSelectExpression(
 				selectExpressionsByResultVariable.get( resultVariableRef.getText() )
 		);
 	}
 
 	/**
 	 * Returns the locations of all occurrences of the named parameter.
 	 */
 	public int[] getNamedParameterLocations(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			throw new QueryException( QueryTranslator.ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name, queryTranslatorImpl.getQueryString() );
 		}
 		if ( o instanceof Integer ) {
 			return new int[]{ (Integer) o };
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
 
 	public ArrayList getParameters() {
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
 		Queryable persister = ( Queryable ) getSessionFactoryHelper().requireClassPersister( path );
 
 		IntoClause intoClause = ( IntoClause ) getASTFactory().create( INTO, persister.getEntityName() );
 		intoClause.setFirstChild( propertySpec );
 		intoClause.initialize( persister );
 
 		addQuerySpaces( persister.getQuerySpaces() );
 
 		return intoClause;
 	}
 
 	@Override
     protected void prepareVersioned(AST updateNode, AST versioned) throws SemanticException {
 		UpdateStatement updateStatement = ( UpdateStatement ) updateNode;
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
 				( ( ParameterNode ) versionIncrementNode ).setHqlParameterSpecification( paramSpec );
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
 		String versionPropertyName = persister.getPropertyNames()[ persister.getVersionProperty() ];
 		AST versionPropertyRef = getASTFactory().create( HqlSqlTokenTypes.IDENT, versionPropertyName );
 		AST versionPropertyNode = lookupNonQualifiedProperty( versionPropertyRef );
 		resolve( versionPropertyNode );
 		return versionPropertyNode;
 	}
 
 	@Override
     protected void prepareLogicOperator(AST operator) throws SemanticException {
 		( ( OperatorNode ) operator ).initialize();
 	}
 
 	@Override
     protected void prepareArithmeticOperator(AST operator) throws SemanticException {
 		( ( OperatorNode ) operator ).initialize();
 	}
 
 	@Override
     protected void validateMapPropertyExpression(AST node) throws SemanticException {
 		try {
 			FromReferenceNode fromReferenceNode = (FromReferenceNode) node;
 			QueryableCollection collectionPersister = fromReferenceNode.getFromElement().getQueryableCollection();
 			if ( ! Map.class.isAssignableFrom( collectionPersister.getCollectionType().getReturnedClass() ) ) {
 				throw new SemanticException( "node did not reference a map" );
 			}
 		}
 		catch ( SemanticException se ) {
 			throw se;
 		}
 		catch ( Throwable t ) {
 			throw new SemanticException( "node did not reference a map" );
 		}
 	}
 
+	public Set<String> getTreatAsDeclarationsByPath(String path) {
+		return hqlParser.getTreatMap().get( path );
+	}
+
 	public static void panic() {
 		throw new QueryException( "TreeWalker: panic" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
index a603e1c94c..281b724425 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
@@ -1,719 +1,722 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
+import java.util.Set;
+
 import antlr.SemanticException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
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
 
 /**
  * Represents a reference to a property or alias expression.  This should duplicate the relevant behaviors in
  * PathExpressionParser.
  *
  * @author Joshua Davis
  */
 public class DotNode extends FromReferenceNode implements DisplayableNode, SelectExpression {
 
 	///////////////////////////////////////////////////////////////////////////
 	// USED ONLY FOR REGRESSION TESTING!!!!
 	//
 	// todo : obviously get rid of all this junk ;)
 	///////////////////////////////////////////////////////////////////////////
 	public static boolean useThetaStyleImplicitJoins = false;
 	public static boolean REGRESSION_STYLE_JOIN_SUPPRESSION = false;
 	public static interface IllegalCollectionDereferenceExceptionBuilder {
 		public QueryException buildIllegalCollectionDereferenceException(String collectionPropertyName, FromReferenceNode lhs);
 	}
 	public static final IllegalCollectionDereferenceExceptionBuilder DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER = new IllegalCollectionDereferenceExceptionBuilder() {
 		public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
 			String lhsPath = ASTUtil.getPathText( lhs );
 			return new QueryException( "illegal attempt to dereference collection [" + lhsPath + "] with element property reference [" + propertyName + "]" );
 		}
 	};
 	public static IllegalCollectionDereferenceExceptionBuilder ILLEGAL_COLL_DEREF_EXCP_BUILDER = DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
 	///////////////////////////////////////////////////////////////////////////
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DotNode.class.getName());
 
 	private static final int DEREF_UNKNOWN = 0;
 	private static final int DEREF_ENTITY = 1;
 	private static final int DEREF_COMPONENT = 2;
 	private static final int DEREF_COLLECTION = 3;
 	private static final int DEREF_PRIMITIVE = 4;
 	private static final int DEREF_IDENTIFIER = 5;
 	private static final int DEREF_JAVA_CONSTANT = 6;
 
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
 	private boolean fetch = false;
 
 	/**
 	 * The type of dereference that hapened (DEREF_xxx).
 	 */
 	private int dereferenceType = DEREF_UNKNOWN;
 
 	private FromElement impliedJoin;
 
 	/**
 	 * Sets the join type for this '.' node structure.
 	 *
 	 * @param joinType The type of join to use.
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
 		buf.append( ",dereferenceType=" ).append( getWalker().getASTPrinter().getTokenTypeName( dereferenceType ) );
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
 		FromReferenceNode lhs = ( FromReferenceNode ) getFirstChild();
 		SqlNode property = ( SqlNode ) lhs.getNextSibling();
 
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
 		setFromElement( lhs.getFromElement() );			// The 'from element' that the property is in.
 
 		checkSubclassOrSuperclassPropertyReference( lhs, propName );
 	}
 
 	@Override
     public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs();			// Prepare the left hand side and get the data type.
 		if ( propertyType!=null && propertyType.isCollectionType() ) {
 			resolveIndex(null);
 		}
 		else {
 			resolveFirstChild();
 			super.resolve(generateJoin, implicitJoin);
 		}
 	}
 
 
 	public void resolveIndex(AST parent) throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs();			// Prepare the left hand side and get the data type.
 		dereferenceCollection( ( CollectionType ) propertyType, true, true, null, parent );
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
 			dereferenceEntity( ( EntityType ) propertyType, implicitJoin, classAlias, generateJoin, parent );
 			initText();
 		}
 		else if ( propertyType.isCollectionType() ) {
 			// The property is a collection...
 			checkLhsIsNotCollection();
 			dereferenceCollection( ( CollectionType ) propertyType, implicitJoin, false, classAlias, parent );
 		}
 		else {
 			// Otherwise, this is a primitive type.
 			if ( ! CollectionProperties.isAnyCollectionProperty( propertyName ) ) {
 				checkLhsIsNotCollection();
 			}
 			dereferenceType = DEREF_PRIMITIVE;
 			initText();
 		}
 		setResolved();
 	}
 
 	private void initText() {
 		String[] cols = getColumns();
 		String text = StringHelper.join( ", ", cols );
 		if ( cols.length > 1 && getWalker().isComparativeExpressionClause() ) {
 			text = "(" + text + ")";
 		}
 		setText( text );
 	}
 
 	private Type prepareLhs() throws SemanticException {
 		FromReferenceNode lhs = getLhs();
 		lhs.prepareForDot( propertyName );
 		return getDataType();
 	}
 
 	private void dereferenceCollection(CollectionType collectionType, boolean implicitJoin, boolean indexed, String classAlias, AST parent)
 	throws SemanticException {
 
 		dereferenceType = DEREF_COLLECTION;
 		String role = collectionType.getRole();
 
 		//foo.bars.size (also handles deprecated stuff like foo.bars.maxelement for backwardness)
 		boolean isSizeProperty = getNextSibling()!=null &&
 			CollectionProperties.isAnyCollectionProperty( getNextSibling().getText() );
 
 		if ( isSizeProperty ) indexed = true; //yuck!
 
 		QueryableCollection queryableCollection = getSessionFactoryHelper().requireQueryableCollection( role );
 		String propName = getPath();
 		FromClause currentFromClause = getWalker().getCurrentFromClause();
 
 		// determine whether we should use the table name or table alias to qualify the column names...
 		// we need to use the table-name when:
 		//		1) the top-level statement is not a SELECT
 		//		2) the LHS FromElement is *the* FromElement from the top-level statement
 		//
 		// there is a caveat here.. if the update/delete statement are "multi-table" we should continue to use
 		// the alias also, even if the FromElement is the root one...
 		//
 		// in all other cases, we should use the table alias
 		final FromElement lhsFromElement = getLhs().getFromElement();
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
 				if ( ! useAlias ) {
 					final String lhsTableName = lhsFromElement.getQueryable().getTableName();
 					columns = getFromElement().toColumns( lhsTableName, propertyPath, false, true );
 				}
 			}
 		}
 
 		// We do not look for an existing join on the same path, because
 		// it makes sense to join twice on the same collection role
 		FromElementFactory factory = new FromElementFactory(
 		        currentFromClause,
 		        getLhs().getFromElement(),
 		        propName,
 				classAlias,
 		        getColumns(),
 		        implicitJoin
 		);
 		FromElement elem = factory.createCollection( queryableCollection, role, joinType, fetch, indexed );
 
 		LOG.debugf( "dereferenceCollection() : Created new FROM element for %s : %s", propName, elem );
 
 		setImpliedJoin( elem );
 		setFromElement( elem );	// This 'dot' expression now refers to the resulting from element.
 
 		if ( isSizeProperty ) {
 			elem.setText("");
 			elem.setUseWhereFragment(false);
 		}
 
 		if ( !implicitJoin ) {
 			EntityPersister entityPersister = elem.getEntityPersister();
 			if ( entityPersister != null ) {
 				getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
 			}
 		}
 		getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );	// Always add the collection's query spaces.
 	}
 
 	private void dereferenceEntity(EntityType entityType, boolean implicitJoin, String classAlias, boolean generateJoin, AST parent) throws SemanticException {
 		checkForCorrelatedSubquery( "dereferenceEntity" );
 		// three general cases we check here as to whether to render a physical SQL join:
 		// 1) is our parent a DotNode as well?  If so, our property reference is
 		// 		being further de-referenced...
 		// 2) is this a DML statement
 		// 3) we were asked to generate any needed joins (generateJoins==true) *OR*
 		//		we are currently processing a select or from clause
 		// (an additional check is the REGRESSION_STYLE_JOIN_SUPPRESSION check solely intended for the test suite)
 		//
 		// The REGRESSION_STYLE_JOIN_SUPPRESSION is an additional check
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
 			parentAsDotNode = ( DotNode ) parent;
 			property = parentAsDotNode.propertyName;
 			joinIsNeeded = generateJoin && !isReferenceToPrimaryKey( parentAsDotNode.propertyName, entityType );
 		}
 		else if ( ! getWalker().isSelectStatement() ) {
 			// in non-select queries, the only time we should need to join is if we are in a subquery from clause
 			joinIsNeeded = getWalker().getCurrentStatementType() == SqlTokenTypes.SELECT && getWalker().isInFrom();
 		}
 		else if ( REGRESSION_STYLE_JOIN_SUPPRESSION ) {
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
 		dereferenceType = DEREF_ENTITY;
         if (LOG.isDebugEnabled()) LOG.debugf("dereferenceEntityJoin() : generating join for %s in %s (%s) parent = %s",
                                              propertyName,
                                              getFromElement().getClassName(),
                                              classAlias == null ? "<no alias>" : classAlias,
                                              ASTUtil.getDebugString(parent));
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
 		// even though we might find a pre-existing element by join path, for FromElements originating in a from-clause
 		// we should only ever use the found element if the aliases match (null != null here).  Implied joins are
 		// always (?) ok to reuse.
 		boolean useFoundFromElement = found && ( elem.isImplied() || areSame( classAlias, elem.getClassAlias() ) );
 
 		if ( ! useFoundFromElement ) {
 			// If this is an implied join in a from element, then use the impled join type which is part of the
 			// tree parser's state (set by the gramamar actions).
 			JoinSequence joinSequence = getSessionFactoryHelper()
 				.createJoinSequence( impliedJoin, propertyType, tableAlias, joinType, joinColumns );
 
 			// If the lhs of the join is a "component join", we need to go back to the
 			// first non-component-join as the origin to properly link aliases and
 			// join columns
 			FromElement lhsFromElement = getLhs().getFromElement();
 			while ( lhsFromElement != null &&  ComponentJoin.class.isInstance( lhsFromElement ) ) {
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
-					role
+					role,
+					joinPath
 			);
 		}
 		else {
 			// NOTE : addDuplicateAlias() already performs nullness checks on the alias.
 			currentFromClause.addDuplicateAlias( classAlias, elem );
 		}
 		setImpliedJoin( elem );
 		getWalker().addQuerySpaces( elem.getEntityPersister().getQuerySpaces() );
 		setFromElement( elem );	// This 'dot' expression now refers to the resulting from element.
 	}
 
 	private boolean areSame(String alias1, String alias2) {
 		// again, null != null here
 		return !StringHelper.isEmpty( alias1 ) && !StringHelper.isEmpty( alias2 ) && alias1.equals( alias2 );
 	}
 
 	private void setImpliedJoin(FromElement elem) {
 		this.impliedJoin = elem;
 		if ( getFirstChild().getType() == SqlTokenTypes.DOT ) {
 			DotNode dotLhs = ( DotNode ) getFirstChild();
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
 	 * @return True if propertyName references the entity's (owningType->associatedEntity)
 	 * primary key; false otherwise.
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
         if (EntityPersister.ENTITY_ID.equals(propertyName)) return owningType.isReferenceToPrimaryKey();
         String keyPropertyName = getSessionFactoryHelper().getIdentifierOrUniqueKeyPropertyName(owningType);
         return keyPropertyName != null && keyPropertyName.equals(propertyName) && owningType.isReferenceToPrimaryKey();
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
 		dereferenceType = DEREF_COMPONENT;
 		setPropertyNameAndPath( parent );
 	}
 
 	private void dereferenceEntityIdentifier(String propertyName, DotNode dotParent) {
 		// special shortcut for id properties, skip the join!
 		// this must only occur at the _end_ of a path expression
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "dereferenceShortcut() : property %s in %s does not require a join.",
 					propertyName,
 					getFromElement().getClassName() );
 		}
 
 		initText();
 		setPropertyNameAndPath( dotParent ); // Set the unresolved path in this node and the parent.
 		// Set the text for the parent.
 		if ( dotParent != null ) {
 			dotParent.dereferenceType = DEREF_IDENTIFIER;
 			dotParent.setText( getText() );
 			dotParent.columns = getColumns();
 		}
 	}
 
 	private void setPropertyNameAndPath(AST parent) {
 		if ( isDotNode( parent ) ) {
 			DotNode dotNode = ( DotNode ) parent;
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
 			if ( fromElement == null ) return null;
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
 		FromReferenceNode lhs = ( ( FromReferenceNode ) getFirstChild() );
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
 				SqlNode rhs = ( SqlNode ) lhs.getNextSibling();
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
 			resolve(false, true);
 		}
 		else {
 			resolve(true, false);
 			Type type = getDataType();
 			if ( type.isEntityType() ) {
 				FromElement fromElement = getFromElement();
 				fromElement.setIncludeSubclasses( true ); // Tell the destination fromElement to 'includeSubclasses'.
 				if ( useThetaStyleImplicitJoins ) {
 					fromElement.getJoinSequence().setUseThetaStyle( true );	// Use theta style (for regression)
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
 			lhs = ( FromReferenceNode ) lhs.getFirstChild();
 		}
 	}
 
 	public void setResolvedConstant(String text) {
 		path = text;
 		dereferenceType = DEREF_JAVA_CONSTANT;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
index 376bb53d21..f6e0612490 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElement.java
@@ -1,699 +1,707 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
+import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.TypeDiscriminatorMetadata;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.DiscriminatorMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Represents a single mapped class mentioned in an HQL FROM clause.  Each
  * class reference will have the following symbols:
  * <ul>
  * <li>A class name - This is the name of the Java class that is mapped by Hibernate.</li>
  * <li>[optional] an HQL alias for the mapped class.</li>
  * <li>A table name - The name of the table that is mapped to the Java class.</li>
  * <li>A table alias - The alias for the table that will be used in the resulting SQL.</li>
  * </ul>
  *
  * @author josh
  */
 public class FromElement extends HqlSqlWalkerNode implements DisplayableNode, ParameterContainer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, FromElement.class.getName());
 
 	private String className;
 	private String classAlias;
 	private String tableAlias;
 	private String collectionTableAlias;
 	private FromClause fromClause;
 	private boolean includeSubclasses = true;
 	private boolean collectionJoin = false;
 	private FromElement origin;
 	private String[] columns;
 	private String role;
 	private boolean fetch;
 	private boolean isAllPropertyFetch;
 	private boolean filter = false;
 	private int sequence = -1;
 	private boolean useFromFragment = false;
 	private boolean initialized = false;
 	private FromElementType elementType;
 	private boolean useWhereFragment = true;
 	private List destinations = new LinkedList();
 	private boolean manyToMany = false;
 	private String withClauseFragment = null;
 	private String withClauseJoinAlias;
 	private boolean dereferencedBySuperclassProperty;
 	private boolean dereferencedBySubclassProperty;
 
 	public FromElement() {
 	}
 
 	/**
 	 * Constructor form used to initialize {@link ComponentJoin}
 	 *
 	 * @param fromClause The FROM clause to which this element belongs
 	 * @param origin The origin (LHS) of this element
 	 * @param alias The alias applied to this element
 	 */
 	protected FromElement(
 			FromClause fromClause,
 			FromElement origin,
 			String alias) {
 		this.fromClause = fromClause;
 		this.origin = origin;
 		this.classAlias = alias;
 		this.tableAlias = origin.getTableAlias();
 		super.initialize( fromClause.getWalker() );
+
 	}
 
 	protected void initializeComponentJoin(FromElementType elementType) {
-		this.elementType = elementType;
 		fromClause.registerFromElement( this );
+		elementType.applyTreatAsDeclarations( getWalker().getTreatAsDeclarationsByPath( classAlias ) );
+		this.elementType = elementType;
 		initialized = true;
 	}
 
 	public String getCollectionSuffix() {
 		return elementType.getCollectionSuffix();
 	}
 
 	public void setCollectionSuffix(String suffix) {
 		elementType.setCollectionSuffix(suffix);
 	}
 
 	public void initializeCollection(FromClause fromClause, String classAlias, String tableAlias) {
 		doInitialize( fromClause, tableAlias, null, classAlias, null, null );
 		initialized = true;
 	}
 
 	public void initializeEntity(
 	        FromClause fromClause,
 	        String className,
 	        EntityPersister persister,
 	        EntityType type,
 	        String classAlias,
 	        String tableAlias) {
 		doInitialize( fromClause, tableAlias, className, classAlias, persister, type );
 		this.sequence = fromClause.nextFromElementCounter();
 		initialized = true;
 	}
 
 	private void doInitialize(FromClause fromClause, String tableAlias, String className, String classAlias,
 							  EntityPersister persister, EntityType type) {
 		if ( initialized ) {
 			throw new IllegalStateException( "Already initialized!!" );
 		}
 		this.fromClause = fromClause;
 		this.tableAlias = tableAlias;
 		this.className = className;
 		this.classAlias = classAlias;
 		this.elementType = new FromElementType( this, persister, type );
 		// Register the FromElement with the FROM clause, now that we have the names and aliases.
 		fromClause.registerFromElement( this );
 		LOG.debugf( "%s : %s (%s) -> %s", fromClause, className, classAlias == null ? "<no alias>" : classAlias, tableAlias );
 	}
 
 	public EntityPersister getEntityPersister() {
 		return elementType.getEntityPersister();
 	}
 
 	@Override
     public Type getDataType() {
 		return elementType.getDataType();
 	}
 
 	public Type getSelectType() {
 		return elementType.getSelectType();
 	}
 
 	public Queryable getQueryable() {
 		return elementType.getQueryable();
 	}
 
 	public String getClassName() {
 		return className;
 	}
 
 	public String getClassAlias() {
 		return classAlias;
 		//return classAlias == null ? className : classAlias;
 	}
 
 	private String getTableName() {
 		Queryable queryable = getQueryable();
 		return ( queryable != null ) ? queryable.getTableName() : "{none}";
 	}
 
 	public String getTableAlias() {
 		return tableAlias;
 	}
 
 	/**
 	 * Render the identifier select, but in a 'scalar' context (i.e. generate the column alias).
 	 *
 	 * @param i the sequence of the returned type
 	 * @return the identifier select with the column alias.
 	 */
 	String renderScalarIdentifierSelect(int i) {
 		return elementType.renderScalarIdentifierSelect( i );
 	}
 
 	void checkInitialized() {
 		if ( !initialized ) {
 			throw new IllegalStateException( "FromElement has not been initialized!" );
 		}
 	}
 
 	/**
 	 * Returns the identifier select SQL fragment.
 	 *
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the identifier select SQL fragment.
 	 */
 	String renderIdentifierSelect(int size, int k) {
 		return elementType.renderIdentifierSelect( size, k );
 	}
 
 	/**
 	 * Returns the property select SQL fragment.
 	 *
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the property select SQL fragment.
 	 */
 	String renderPropertySelect(int size, int k) {
 		return elementType.renderPropertySelect( size, k, isAllPropertyFetch );
 	}
 
 	String renderCollectionSelectFragment(int size, int k) {
 		return elementType.renderCollectionSelectFragment( size, k );
 	}
 
 	String renderValueCollectionSelectFragment(int size, int k) {
 		return elementType.renderValueCollectionSelectFragment( size, k );
 	}
 
 	public FromClause getFromClause() {
 		return fromClause;
 	}
 
 	/**
 	 * Returns true if this FromElement was implied by a path, or false if this FROM element is explicitly declared in
 	 * the FROM clause.
 	 *
 	 * @return true if this FromElement was implied by a path, or false if this FROM element is explicitly declared
 	 */
 	public boolean isImplied() {
 		return false;	// This is an explicit FROM element.
 	}
 
 	/**
 	 * Returns additional display text for the AST node.
 	 *
 	 * @return String - The additional display text.
 	 */
 	public String getDisplayText() {
 		StringBuilder buf = new StringBuilder();
 		buf.append( "FromElement{" );
 		appendDisplayText( buf );
 		buf.append( "}" );
 		return buf.toString();
 	}
 
 	protected void appendDisplayText(StringBuilder buf) {
 		buf.append( isImplied() ? (
 				isImpliedInFromClause() ? "implied in FROM clause" : "implied" )
 				: "explicit" );
 		buf.append( "," ).append( isCollectionJoin() ? "collection join" : "not a collection join" );
 		buf.append( "," ).append( fetch ? "fetch join" : "not a fetch join" );
 		buf.append( "," ).append( isAllPropertyFetch ? "fetch all properties" : "fetch non-lazy properties" );
 		buf.append( ",classAlias=" ).append( getClassAlias() );
 		buf.append( ",role=" ).append( role );
 		buf.append( ",tableName=" ).append( getTableName() );
 		buf.append( ",tableAlias=" ).append( getTableAlias() );
 		FromElement origin = getRealOrigin();
 		buf.append( ",origin=" ).append( origin == null ? "null" : origin.getText() );
 		buf.append( ",columns={" );
 		if ( columns != null ) {
 			for ( int i = 0; i < columns.length; i++ ) {
 				buf.append( columns[i] );
 				if ( i < columns.length ) {
 					buf.append( " " );
 				}
 			}
 		}
 		buf.append( ",className=" ).append( className );
 		buf.append( "}" );
 	}
 
 	@Override
     public int hashCode() {
 		return super.hashCode();
 	}
 
 	@Override
     public boolean equals(Object obj) {
 		return super.equals( obj );
 	}
 
 
 	public void setJoinSequence(JoinSequence joinSequence) {
 		elementType.setJoinSequence( joinSequence );
 	}
 
 	public JoinSequence getJoinSequence() {
 		return elementType.getJoinSequence();
 	}
 
 	public void setIncludeSubclasses(boolean includeSubclasses) {
 		if ( !includeSubclasses && isDereferencedBySuperclassOrSubclassProperty() && LOG.isTraceEnabled() )
 			LOG.trace( "Attempt to disable subclass-inclusions : ", new Exception( "Stack-trace source" ) );
 		this.includeSubclasses = includeSubclasses;
 	}
 
 	public boolean isIncludeSubclasses() {
 		return includeSubclasses;
 	}
 
 	public boolean isDereferencedBySuperclassOrSubclassProperty() {
 		return dereferencedBySubclassProperty || dereferencedBySuperclassProperty;
 	}
 
 	public String getIdentityColumn() {
 		final String[] cols = getIdentityColumns();
 		if ( cols.length == 1 ) {
 			return cols[0];
 		}
 		else {
 			return "(" + StringHelper.join( ", ", cols ) + ")";
 		}
 	}
 
 	public String[] getIdentityColumns() {
 		checkInitialized();
 		final String table = getTableAlias();
 		if ( table == null ) {
 			throw new IllegalStateException( "No table alias for node " + this );
 		}
 
 		final String propertyName;
 		if ( getEntityPersister() != null && getEntityPersister().getEntityMetamodel() != null
 				&& getEntityPersister().getEntityMetamodel().hasNonIdentifierPropertyNamedId() ) {
 			propertyName = getEntityPersister().getIdentifierPropertyName();
 		}
 		else {
 			propertyName = EntityPersister.ENTITY_ID;
 		}
 
 		if ( getWalker().getStatementType() == HqlSqlTokenTypes.SELECT ) {
 			return getPropertyMapping( propertyName ).toColumns( table, propertyName );
 		}
 		else {
 			return getPropertyMapping( propertyName ).toColumns( propertyName );
 		}
 	}
 
 	public void setCollectionJoin(boolean collectionJoin) {
 		this.collectionJoin = collectionJoin;
 	}
 
 	public boolean isCollectionJoin() {
 		return collectionJoin;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
+		applyTreatAsDeclarations( getWalker().getTreatAsDeclarationsByPath( role ) );
 	}
-	
+
+	public void applyTreatAsDeclarations(Set<String> treatAsDeclarationsByPath) {
+		elementType.applyTreatAsDeclarations( treatAsDeclarationsByPath );
+	}
+
 	public String getRole() {
 		return role;
 	}
 
 	public void setQueryableCollection(QueryableCollection queryableCollection) {
 		elementType.setQueryableCollection( queryableCollection );
 	}
 
 	public QueryableCollection getQueryableCollection() {
 		return elementType.getQueryableCollection();
 	}
 
 	public void setColumns(String[] columns) {
 		this.columns = columns;
 	}
 
 	public void setOrigin(FromElement origin, boolean manyToMany) {
 		this.origin = origin;
 		this.manyToMany = manyToMany;
 		origin.addDestination( this );
 		if ( origin.getFromClause() == this.getFromClause() ) {
 			// TODO: Figure out a better way to get the FROM elements in a proper tree structure.
 			// If this is not the destination of a many-to-many, add it as a child of the origin.
 			if ( manyToMany ) {
 				ASTUtil.appendSibling( origin, this );
 			}
 			else {
 				if ( !getWalker().isInFrom() && !getWalker().isInSelect() ) {
 					getFromClause().addChild( this );
 				}
 				else {
 					origin.addChild( this );
 				}
 			}
 		}
 		else if ( !getWalker().isInFrom() ) {
 			// HHH-276 : implied joins in a subselect where clause - The destination needs to be added
 			// to the destination's from clause.
 			getFromClause().addChild( this );	// Not sure if this is will fix everything, but it works.
 		}
 		else {
 			// Otherwise, the destination node was implied by the FROM clause and the FROM clause processor
 			// will automatically add it in the right place.
 		}
 	}
 
 	public boolean isManyToMany() {
 		return manyToMany;
 	}
 
 	private void addDestination(FromElement fromElement) {
 		destinations.add( fromElement );
 	}
 
 	public List getDestinations() {
 		return destinations;
 	}
 
 	public FromElement getOrigin() {
 		return origin;
 	}
 
 	public FromElement getRealOrigin() {
 		if ( origin == null ) {
 			return null;
 		}
 		if ( origin.getText() == null || "".equals( origin.getText() ) ) {
 			return origin.getRealOrigin();
 		}
 		return origin;
 	}
 
 	public static final String DISCRIMINATOR_PROPERTY_NAME = "class";
 	private TypeDiscriminatorMetadata typeDiscriminatorMetadata;
 
 	private static class TypeDiscriminatorMetadataImpl implements TypeDiscriminatorMetadata {
 		private final DiscriminatorMetadata persisterDiscriminatorMetadata;
 		private final String alias;
 
 		private TypeDiscriminatorMetadataImpl(
 				DiscriminatorMetadata persisterDiscriminatorMetadata,
 				String alias) {
 			this.persisterDiscriminatorMetadata = persisterDiscriminatorMetadata;
 			this.alias = alias;
 		}
 
 		@Override
 		public String getSqlFragment() {
 			return persisterDiscriminatorMetadata.getSqlFragment( alias );
 		}
 
 		@Override
 		public Type getResolutionType() {
 			return persisterDiscriminatorMetadata.getResolutionType();
 		}
 	}
 
 	public TypeDiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( typeDiscriminatorMetadata == null ) {
 			typeDiscriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return typeDiscriminatorMetadata;
 	}
 
 	private TypeDiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		final String aliasToUse = getTableAlias();
 		Queryable queryable = getQueryable();
 		if ( queryable == null ) {
 			QueryableCollection collection = getQueryableCollection();
 			if ( ! collection.getElementType().isEntityType() ) {
 				throw new QueryException( "type discrimination cannot be applied to value collection [" + collection.getRole() + "]" );
 			}
 			queryable = (Queryable) collection.getElementPersister();
 		}
 
 		handlePropertyBeingDereferenced( getDataType(), DISCRIMINATOR_PROPERTY_NAME );
 
 		return new TypeDiscriminatorMetadataImpl( queryable.getTypeDiscriminatorMetadata(), aliasToUse );
 	}
 
 	public Type getPropertyType(String propertyName, String propertyPath) {
 		return elementType.getPropertyType( propertyName, propertyPath );
 	}
 
 	public String[] toColumns(String tableAlias, String path, boolean inSelect) {
 		return elementType.toColumns( tableAlias, path, inSelect );
 	}
 
 	public String[] toColumns(String tableAlias, String path, boolean inSelect, boolean forceAlias) {
 		return elementType.toColumns( tableAlias, path, inSelect, forceAlias );
 	}
 
 	public PropertyMapping getPropertyMapping(String propertyName) {
 		return elementType.getPropertyMapping( propertyName );
 	}
 
 	public void setFetch(boolean fetch) {
 		this.fetch = fetch;
 		// Fetch can't be used with scroll() or iterate().
 		if ( fetch && getWalker().isShallowQuery() ) {
 			throw new QueryException( QueryTranslator.ERROR_CANNOT_FETCH_WITH_ITERATE );
 		}
 	}
 
 	public boolean isFetch() {
 		return fetch;
 	}
 
 	public int getSequence() {
 		return sequence;
 	}
 
 	public void setFilter(boolean b) {
 		filter = b;
 	}
 
 	public boolean isFilter() {
 		return filter;
 	}
 
 	public boolean useFromFragment() {
 		checkInitialized();
 		// If it's not implied or it is implied and it's a many to many join where the target wasn't found.
 		return !isImplied() || this.useFromFragment;
 	}
 
 	public void setUseFromFragment(boolean useFromFragment) {
 		this.useFromFragment = useFromFragment;
 	}
 
 	public boolean useWhereFragment() {
 		return useWhereFragment;
 	}
 
 	public void setUseWhereFragment(boolean b) {
 		useWhereFragment = b;
 	}
 
 
 	public void setCollectionTableAlias(String collectionTableAlias) {
 		this.collectionTableAlias = collectionTableAlias;
 	}
 
 	public String getCollectionTableAlias() {
 		return collectionTableAlias;
 	}
 
 	public boolean isCollectionOfValuesOrComponents() {
 		return elementType.isCollectionOfValuesOrComponents();
 	}
 
 	public boolean isEntity() {
 		return elementType.isEntity();
 	}
 
 	public void setImpliedInFromClause(boolean flag) {
 		throw new UnsupportedOperationException( "Explicit FROM elements can't be implied in the FROM clause!" );
 	}
 
 	public boolean isImpliedInFromClause() {
 		return false;	// Since this is an explicit FROM element, it can't be implied in the FROM clause.
 	}
 
 	public void setInProjectionList(boolean inProjectionList) {
 		// Do nothing, eplicit from elements are *always* in the projection list.
 	}
 
 	public boolean inProjectionList() {
 		return !isImplied() && isFromOrJoinFragment();
 	}
 
 	public boolean isFromOrJoinFragment() {
 		return getType() == SqlTokenTypes.FROM_FRAGMENT || getType() == SqlTokenTypes.JOIN_FRAGMENT;
 	}
 
 	public boolean isAllPropertyFetch() {
 		return isAllPropertyFetch;
 	}
 
 	public void setAllPropertyFetch(boolean fetch) {
 		isAllPropertyFetch = fetch;
 	}
 
 	public String getWithClauseFragment() {
 		return withClauseFragment;
 	}
 
 	public String getWithClauseJoinAlias() {
 		return withClauseJoinAlias;
 	}
 
 	public void setWithClauseFragment(String withClauseJoinAlias, String withClauseFragment) {
 		this.withClauseJoinAlias = withClauseJoinAlias;
 		this.withClauseFragment = withClauseFragment;
 	}
 
 	public boolean hasCacheablePersister() {
 		if ( getQueryableCollection() != null ) {
 			return getQueryableCollection().hasCache();
 		}
 		else {
 			return getQueryable().hasCache();
 		}
 	}
 
 	public void handlePropertyBeingDereferenced(Type propertySource, String propertyName) {
 		if ( getQueryableCollection() != null && CollectionProperties.isCollectionProperty( propertyName ) ) {
 			// propertyName refers to something like collection.size...
 			return;
 		}
 		if ( propertySource.isComponentType() ) {
 			// property name is a sub-path of a component...
 			return;
 		}
 
 		Queryable persister = getQueryable();
 		if ( persister != null ) {
 			try {
 				Queryable.Declarer propertyDeclarer = persister.getSubclassPropertyDeclarer( propertyName );
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Handling property dereference [{0} ({1}) -> {2} ({3})]",
 							persister.getEntityName(), getClassAlias(), propertyName, propertyDeclarer );
 				}
 				if ( propertyDeclarer == Queryable.Declarer.SUBCLASS ) {
 					dereferencedBySubclassProperty = true;
 					includeSubclasses = true;
 				}
 				else if ( propertyDeclarer == Queryable.Declarer.SUPERCLASS ) {
 					dereferencedBySuperclassProperty = true;
 				}
 			}
 			catch( QueryException ignore ) {
 				// ignore it; the incoming property could not be found so we
 				// cannot be sure what to do here.  At the very least, the
 				// safest is to simply not apply any dereference toggling...
 
 			}
 		}
 	}
 
 	public boolean isDereferencedBySuperclassProperty() {
 		return dereferencedBySuperclassProperty;
 	}
 
 	public boolean isDereferencedBySubclassProperty() {
 		return dereferencedBySubclassProperty;
 	}
 
 
 	// ParameterContainer impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private List<ParameterSpecification> embeddedParameters;
 
 	@Override
 	public void addEmbeddedParameter(ParameterSpecification specification) {
 		if ( embeddedParameters == null ) {
 			embeddedParameters = new ArrayList<ParameterSpecification>();
 		}
 		embeddedParameters.add( specification );
 	}
 
 	@Override
 	public boolean hasEmbeddedParameters() {
 		return embeddedParameters != null && ! embeddedParameters.isEmpty();
 	}
 
 	@Override
 	public ParameterSpecification[] getEmbeddedParameters() {
 		return embeddedParameters.toArray( new ParameterSpecification[ embeddedParameters.size() ] );
 	}
 
 	public ParameterSpecification getIndexCollectionSelectorParamSpec() {
 		return elementType.getIndexCollectionSelectorParamSpec();
 	}
 
 	public void setIndexCollectionSelectorParamSpec(ParameterSpecification indexCollectionSelectorParamSpec) {
 		if ( indexCollectionSelectorParamSpec == null ) {
 			if ( elementType.getIndexCollectionSelectorParamSpec() != null ) {
 				embeddedParameters.remove( elementType.getIndexCollectionSelectorParamSpec() );
 				elementType.setIndexCollectionSelectorParamSpec( null );
 			}
 		}
 		else {
 			elementType.setIndexCollectionSelectorParamSpec( indexCollectionSelectorParamSpec );
 			addEmbeddedParameter( indexCollectionSelectorParamSpec );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
index f2e19bad26..60906dcbf7 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
@@ -1,519 +1,526 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
 import antlr.ASTFactory;
 import antlr.SemanticException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.AliasGenerator;
 import org.hibernate.hql.internal.ast.util.PathHelper;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Encapsulates the creation of FromElements and JoinSequences.
  *
  * @author josh
  */
 public class FromElementFactory implements SqlTokenTypes {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, FromElementFactory.class.getName());
 
 	private FromClause fromClause;
 	private FromElement origin;
 	private String path;
 
 	private String classAlias;
 	private String[] columns;
 	private boolean implied;
 	private boolean inElementsFunction;
 	private boolean collection;
 	private QueryableCollection queryableCollection;
 	private CollectionType collectionType;
 
 	/**
 	 * Creates entity from elements.
 	 */
 	public FromElementFactory(FromClause fromClause, FromElement origin, String path) {
 		this.fromClause = fromClause;
 		this.origin = origin;
 		this.path = path;
 		collection = false;
 	}
 
 	/**
 	 * Creates collection from elements.
 	 */
 	public FromElementFactory(
 	        FromClause fromClause,
 	        FromElement origin,
 	        String path,
 	        String classAlias,
 	        String[] columns,
 	        boolean implied) {
 		this( fromClause, origin, path );
 		this.classAlias = classAlias;
 		this.columns = columns;
 		this.implied = implied;
 		collection = true;
 	}
 
 	FromElement addFromElement() throws SemanticException {
 		FromClause parentFromClause = fromClause.getParentFromClause();
 		if ( parentFromClause != null ) {
 			// Look up class name using the first identifier in the path.
 			String pathAlias = PathHelper.getAlias( path );
 			FromElement parentFromElement = parentFromClause.getFromElement( pathAlias );
 			if ( parentFromElement != null ) {
 				return createFromElementInSubselect( path, pathAlias, parentFromElement, classAlias );
 			}
 		}
 
 		EntityPersister entityPersister = fromClause.getSessionFactoryHelper().requireClassPersister( path );
 
 		FromElement elem = createAndAddFromElement( path,
 				classAlias,
 				entityPersister,
 				( EntityType ) ( ( Queryable ) entityPersister ).getType(),
 				null );
 
 		// Add to the query spaces.
 		fromClause.getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
 
 		return elem;
 	}
 
 	private FromElement createFromElementInSubselect(
 	        String path,
 	        String pathAlias,
 	        FromElement parentFromElement,
 	        String classAlias) throws SemanticException {
 		LOG.debugf( "createFromElementInSubselect() : path = %s", path );
 		// Create an DotNode AST for the path and resolve it.
 		FromElement fromElement = evaluateFromElementPath( path, classAlias );
 		EntityPersister entityPersister = fromElement.getEntityPersister();
 
 		// If the first identifier in the path refers to the class alias (not the class name), then this
 		// is a correlated subselect.  If it's a correlated sub-select, use the existing table alias.  Otherwise
 		// generate a new one.
 		String tableAlias = null;
 		boolean correlatedSubselect = pathAlias.equals( parentFromElement.getClassAlias() );
 		if ( correlatedSubselect ) {
 			tableAlias = fromElement.getTableAlias();
 		}
 		else {
 			tableAlias = null;
 		}
 
 		// If the from element isn't in the same clause, create a new from element.
 		if ( fromElement.getFromClause() != fromClause ) {
 			LOG.debug( "createFromElementInSubselect() : creating a new FROM element..." );
 			fromElement = createFromElement( entityPersister );
 			initializeAndAddFromElement( fromElement,
 					path,
 					classAlias,
 					entityPersister,
 					( EntityType ) ( ( Queryable ) entityPersister ).getType(),
 					tableAlias
 			);
 		}
 		LOG.debugf( "createFromElementInSubselect() : %s -> %s", path, fromElement );
 		return fromElement;
 	}
 
 	private FromElement evaluateFromElementPath(String path, String classAlias) throws SemanticException {
 		ASTFactory factory = fromClause.getASTFactory();
 		FromReferenceNode pathNode = ( FromReferenceNode ) PathHelper.parsePath( path, factory );
 		pathNode.recursiveResolve( FromReferenceNode.ROOT_LEVEL, // This is the root level node.
 				false, // Generate an explicit from clause at the root.
 				classAlias,
 		        null
 		);
         if (pathNode.getImpliedJoin() != null) return pathNode.getImpliedJoin();
         return pathNode.getFromElement();
 	}
 
 	FromElement createCollectionElementsJoin(
 	        QueryableCollection queryableCollection,
 	        String collectionName) throws SemanticException {
 		JoinSequence collectionJoinSequence = fromClause.getSessionFactoryHelper()
 		        .createCollectionJoinSequence( queryableCollection, collectionName );
 		this.queryableCollection = queryableCollection;
 		return createCollectionJoin( collectionJoinSequence, null );
 	}
 
 	public FromElement createCollection(
 	        QueryableCollection queryableCollection,
 	        String role,
 	        JoinType joinType,
 	        boolean fetchFlag,
 	        boolean indexed)
 			throws SemanticException {
 		if ( !collection ) {
 			throw new IllegalStateException( "FromElementFactory not initialized for collections!" );
 		}
 		this.inElementsFunction = indexed;
 		FromElement elem;
 		this.queryableCollection = queryableCollection;
 		collectionType = queryableCollection.getCollectionType();
 		String roleAlias = fromClause.getAliasGenerator().createName( role );
 
 		// Correlated subqueries create 'special' implied from nodes
 		// because correlated subselects can't use an ANSI-style join
 		boolean explicitSubqueryFromElement = fromClause.isSubQuery() && !implied;
 		if ( explicitSubqueryFromElement ) {
 			String pathRoot = StringHelper.root( path );
 			FromElement origin = fromClause.getFromElement( pathRoot );
 			if ( origin == null || origin.getFromClause() != fromClause ) {
 				implied = true;
 			}
 		}
 
 		// super-duper-classic-parser-regression-testing-mojo-magic...
 		if ( explicitSubqueryFromElement && DotNode.useThetaStyleImplicitJoins ) {
 			implied = true;
 		}
 
 		Type elementType = queryableCollection.getElementType();
 		if ( elementType.isEntityType() ) { 			// A collection of entities...
 			elem = createEntityAssociation( role, roleAlias, joinType );
 		}
 		else if ( elementType.isComponentType() ) {		// A collection of components...
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			elem = createCollectionJoin( joinSequence, roleAlias );
 		}
 		else {											// A collection of scalar elements...
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			elem = createCollectionJoin( joinSequence, roleAlias );
 		}
 
 		elem.setRole( role );
 		elem.setQueryableCollection( queryableCollection );
 		// Don't include sub-classes for implied collection joins or subquery joins.
 		if ( implied ) {
 			elem.setIncludeSubclasses( false );
 		}
 
 		if ( explicitSubqueryFromElement ) {
 			elem.setInProjectionList( true );	// Treat explict from elements in sub-queries properly.
 		}
 
 		if ( fetchFlag ) {
 			elem.setFetch( true );
 		}
 		return elem;
 	}
 
 	public FromElement createEntityJoin(
 	        String entityClass,
 	        String tableAlias,
 	        JoinSequence joinSequence,
 	        boolean fetchFlag,
 	        boolean inFrom,
 	        EntityType type,
-	        String role) throws SemanticException {
+	        String role,
+			String joinPath) throws SemanticException {
 		FromElement elem = createJoin( entityClass, tableAlias, joinSequence, type, false );
 		elem.setFetch( fetchFlag );
+
+		if ( joinPath != null ) {
+			elem.applyTreatAsDeclarations( fromClause.getWalker().getTreatAsDeclarationsByPath( joinPath ) );
+		}
+
 		EntityPersister entityPersister = elem.getEntityPersister();
 		int numberOfTables = entityPersister.getQuerySpaces().length;
 		if ( numberOfTables > 1 && implied && !elem.useFromFragment() ) {
 			LOG.debug( "createEntityJoin() : Implied multi-table entity join" );
 			elem.setUseFromFragment( true );
 		}
 
 		// If this is an implied join in a FROM clause, then use ANSI-style joining, and set the
 		// flag on the FromElement that indicates that it was implied in the FROM clause itself.
 		if ( implied && inFrom ) {
 			joinSequence.setUseThetaStyle( false );
 			elem.setUseFromFragment( true );
 			elem.setImpliedInFromClause( true );
 		}
 		if ( elem.getWalker().isSubQuery() ) {
 			// two conditions where we need to transform this to a theta-join syntax:
 			//      1) 'elem' is the "root from-element" in correlated subqueries
 			//      2) The DotNode.useThetaStyleImplicitJoins has been set to true
 			//          and 'elem' represents an implicit join
 			if ( elem.getFromClause() != elem.getOrigin().getFromClause() ||
 //			        ( implied && DotNode.useThetaStyleImplicitJoins ) ) {
 			        DotNode.useThetaStyleImplicitJoins ) {
 				// the "root from-element" in correlated subqueries do need this piece
 				elem.setType( FROM_FRAGMENT );
 				joinSequence.setUseThetaStyle( true );
 				elem.setUseFromFragment( false );
 			}
 		}
 		
 		elem.setRole( role );
 
 		return elem;
 	}
 
 	public FromElement createComponentJoin(ComponentType type) {
+
 		// need to create a "place holder" from-element that can store the component/alias for this
 		// 		component join
 		return new ComponentJoin( fromClause, origin, classAlias, path, type );
 	}
 
 	FromElement createElementJoin(QueryableCollection queryableCollection) throws SemanticException {
 		FromElement elem;
 
 		implied = true; //TODO: always true for now, but not if we later decide to support elements() in the from clause
 		inElementsFunction = true;
 		Type elementType = queryableCollection.getElementType();
 		if ( !elementType.isEntityType() ) {
 			throw new IllegalArgumentException( "Cannot create element join for a collection of non-entities!" );
 		}
 		this.queryableCollection = queryableCollection;
 		SessionFactoryHelper sfh = fromClause.getSessionFactoryHelper();
 		FromElement destination = null;
 		String tableAlias = null;
 		EntityPersister entityPersister = queryableCollection.getElementPersister();
 		tableAlias = fromClause.getAliasGenerator().createName( entityPersister.getEntityName() );
 		String associatedEntityName = entityPersister.getEntityName();
 		EntityPersister targetEntityPersister = sfh.requireClassPersister( associatedEntityName );
 		// Create the FROM element for the target (the elements of the collection).
 		destination = createAndAddFromElement(
 				associatedEntityName,
 				classAlias,
 				targetEntityPersister,
 				( EntityType ) queryableCollection.getElementType(),
 				tableAlias
 			);
 		// If the join is implied, then don't include sub-classes on the element.
 		if ( implied ) {
 			destination.setIncludeSubclasses( false );
 		}
 		fromClause.addCollectionJoinFromElementByPath( path, destination );
 //		origin.addDestination(destination);
 		// Add the query spaces.
 		fromClause.getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
 
 		CollectionType type = queryableCollection.getCollectionType();
 		String role = type.getRole();
 		String roleAlias = origin.getTableAlias();
 
 		String[] targetColumns = sfh.getCollectionElementColumns( role, roleAlias );
 		AssociationType elementAssociationType = sfh.getElementAssociationType( type );
 
 		// Create the join element under the from element.
 		JoinType joinType = JoinType.INNER_JOIN;
 		JoinSequence joinSequence = sfh.createJoinSequence( implied, elementAssociationType, tableAlias, joinType, targetColumns );
 		elem = initializeJoin( path, destination, joinSequence, targetColumns, origin, false );
 		elem.setUseFromFragment( true );	// The associated entity is implied, but it must be included in the FROM.
 		elem.setCollectionTableAlias( roleAlias );	// The collection alias is the role.
 		return elem;
 	}
 
 	private FromElement createCollectionJoin(JoinSequence collectionJoinSequence, String tableAlias) throws SemanticException {
 		String text = queryableCollection.getTableName();
 		AST ast = createFromElement( text );
 		FromElement destination = ( FromElement ) ast;
 		Type elementType = queryableCollection.getElementType();
 		if ( elementType.isCollectionType() ) {
 			throw new SemanticException( "Collections of collections are not supported!" );
 		}
 		destination.initializeCollection( fromClause, classAlias, tableAlias );
 		destination.setType( JOIN_FRAGMENT );		// Tag this node as a JOIN.
 		destination.setIncludeSubclasses( false );	// Don't include subclasses in the join.
 		destination.setCollectionJoin( true );		// This is a clollection join.
 		destination.setJoinSequence( collectionJoinSequence );
 		destination.setOrigin( origin, false );
 		destination.setCollectionTableAlias(tableAlias);
 //		origin.addDestination( destination );
 // This was the cause of HHH-242
 //		origin.setType( FROM_FRAGMENT );			// Set the parent node type so that the AST is properly formed.
 		origin.setText( "" );						// The destination node will have all the FROM text.
 		origin.setCollectionJoin( true );			// The parent node is a collection join too (voodoo - see JoinProcessor)
 		fromClause.addCollectionJoinFromElementByPath( path, destination );
 		fromClause.getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );
 		return destination;
 	}
 
 	private FromElement createEntityAssociation(
 	        String role,
 	        String roleAlias,
 	        JoinType joinType) throws SemanticException {
 		FromElement elem;
 		Queryable entityPersister = ( Queryable ) queryableCollection.getElementPersister();
 		String associatedEntityName = entityPersister.getEntityName();
 		// Get the class name of the associated entity.
 		if ( queryableCollection.isOneToMany() ) {
 			LOG.debugf( "createEntityAssociation() : One to many - path = %s role = %s associatedEntityName = %s",
 					path,
 					role,
 					associatedEntityName );
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 
 			elem = createJoin( associatedEntityName, roleAlias, joinSequence, ( EntityType ) queryableCollection.getElementType(), false );
 		}
 		else {
 			LOG.debugf( "createManyToMany() : path = %s role = %s associatedEntityName = %s", path, role, associatedEntityName );
 			elem = createManyToMany( role, associatedEntityName,
 					roleAlias, entityPersister, ( EntityType ) queryableCollection.getElementType(), joinType );
 			fromClause.getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );
 		}
 		elem.setCollectionTableAlias( roleAlias );
 		return elem;
 	}
 
 	private FromElement createJoin(
 	        String entityClass,
 	        String tableAlias,
 	        JoinSequence joinSequence,
 	        EntityType type,
 	        boolean manyToMany) throws SemanticException {
 		//  origin, path, implied, columns, classAlias,
 		EntityPersister entityPersister = fromClause.getSessionFactoryHelper().requireClassPersister( entityClass );
 		FromElement destination = createAndAddFromElement( entityClass,
 				classAlias,
 				entityPersister,
 				type,
 				tableAlias );
 		return initializeJoin( path, destination, joinSequence, getColumns(), origin, manyToMany );
 	}
 
 	private FromElement createManyToMany(
 	        String role,
 	        String associatedEntityName,
 	        String roleAlias,
 	        Queryable entityPersister,
 	        EntityType type,
 	        JoinType joinType) throws SemanticException {
 		FromElement elem;
 		SessionFactoryHelper sfh = fromClause.getSessionFactoryHelper();
 		if ( inElementsFunction /*implied*/ ) {
 			// For implied many-to-many, just add the end join.
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			elem = createJoin( associatedEntityName, roleAlias, joinSequence, type, true );
 		}
 		else {
 			// For an explicit many-to-many relationship, add a second join from the intermediate
 			// (many-to-many) table to the destination table.  Also, make sure that the from element's
 			// idea of the destination is the destination table.
 			String tableAlias = fromClause.getAliasGenerator().createName( entityPersister.getEntityName() );
 			String[] secondJoinColumns = sfh.getCollectionElementColumns( role, roleAlias );
 			// Add the second join, the one that ends in the destination table.
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			joinSequence.addJoin( sfh.getElementAssociationType( collectionType ), tableAlias, joinType, secondJoinColumns );
 			elem = createJoin( associatedEntityName, tableAlias, joinSequence, type, false );
 			elem.setUseFromFragment( true );
 		}
 		return elem;
 	}
 
 	private JoinSequence createJoinSequence(String roleAlias, JoinType joinType) {
 		SessionFactoryHelper sessionFactoryHelper = fromClause.getSessionFactoryHelper();
 		String[] joinColumns = getColumns();
 		if ( collectionType == null ) {
 			throw new IllegalStateException( "collectionType is null!" );
 		}
 		return sessionFactoryHelper.createJoinSequence( implied, collectionType, roleAlias, joinType, joinColumns );
 	}
 
 	private FromElement createAndAddFromElement(
 	        String className,
 	        String classAlias,
 	        EntityPersister entityPersister,
 	        EntityType type,
 	        String tableAlias) {
 		if ( !( entityPersister instanceof Joinable ) ) {
 			throw new IllegalArgumentException( "EntityPersister " + entityPersister + " does not implement Joinable!" );
 		}
 		FromElement element = createFromElement( entityPersister );
 		initializeAndAddFromElement( element, className, classAlias, entityPersister, type, tableAlias );
 		return element;
 	}
 
 	private void initializeAndAddFromElement(
 	        FromElement element,
 	        String className,
 	        String classAlias,
 	        EntityPersister entityPersister,
 	        EntityType type,
 	        String tableAlias) {
 		if ( tableAlias == null ) {
 			AliasGenerator aliasGenerator = fromClause.getAliasGenerator();
 			tableAlias = aliasGenerator.createName( entityPersister.getEntityName() );
 		}
 		element.initializeEntity( fromClause, className, entityPersister, type, classAlias, tableAlias );
 	}
 
 	private FromElement createFromElement(EntityPersister entityPersister) {
 		Joinable joinable = ( Joinable ) entityPersister;
 		String text = joinable.getTableName();
 		AST ast = createFromElement( text );
 		FromElement element = ( FromElement ) ast;
 		return element;
 	}
 
 	private AST createFromElement(String text) {
 		AST ast = ASTUtil.create( fromClause.getASTFactory(),
 				implied ? IMPLIED_FROM : FROM_FRAGMENT, // This causes the factory to instantiate the desired class.
 				text );
 		// Reset the node type, because the rest of the system is expecting FROM_FRAGMENT, all we wanted was
 		// for the factory to create the right sub-class.  This might get reset again later on anyway to make the
 		// SQL generation simpler.
 		ast.setType( FROM_FRAGMENT );
 		return ast;
 	}
 
 	private FromElement initializeJoin(
 	        String path,
 	        FromElement destination,
 	        JoinSequence joinSequence,
 	        String[] columns,
 	        FromElement origin,
 	        boolean manyToMany) {
 		destination.setType( JOIN_FRAGMENT );
 		destination.setJoinSequence( joinSequence );
 		destination.setColumns( columns );
 		destination.setOrigin( origin, manyToMany );
 		fromClause.addJoinByPathMap( path, destination );
 		return destination;
 	}
 
 	private String[] getColumns() {
 		if ( columns == null ) {
 			throw new IllegalStateException( "No foriegn key columns were supplied!" );
 		}
 		return columns;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java
index 92d3cfeb26..8cb791a437 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java
@@ -1,544 +1,577 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
+import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
+import java.util.Set;
+
+import antlr.SemanticException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.CollectionSubqueryFactory;
 import org.hibernate.hql.internal.NameGenerator;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.CollectionPropertyMapping;
 import org.hibernate.persister.collection.CollectionPropertyNames;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Delegate that handles the type and join sequence information for a FromElement.
  *
  * @author josh
  */
 class FromElementType {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, FromElementType.class.getName());
 
 	private FromElement fromElement;
 	private EntityType entityType;
 	private EntityPersister persister;
 	private QueryableCollection queryableCollection;
 	private CollectionPropertyMapping collectionPropertyMapping;
 	private JoinSequence joinSequence;
 	private String collectionSuffix;
 	private ParameterSpecification indexCollectionSelectorParamSpec;
 
 	public FromElementType(FromElement fromElement, EntityPersister persister, EntityType entityType) {
 		this.fromElement = fromElement;
 		this.persister = persister;
 		this.entityType = entityType;
 		if ( persister != null ) {
 			fromElement.setText( ( ( Queryable ) persister ).getTableName() + " " + getTableAlias() );
 		}
 	}
 
 	protected FromElementType(FromElement fromElement) {
 		this.fromElement = fromElement;
 	}
 
 	private String getTableAlias() {
 		return fromElement.getTableAlias();
 	}
 
 	private String getCollectionTableAlias() {
 		return fromElement.getCollectionTableAlias();
 	}
 
 	public String getCollectionSuffix() {
 		return collectionSuffix;
 	}
 
 	public void setCollectionSuffix(String suffix) {
 		collectionSuffix = suffix;
 	}
 
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	public Type getDataType() {
 		if ( persister == null ) {
 			if ( queryableCollection == null ) {
 				return null;
 			}
 			return queryableCollection.getType();
 		}
 		else {
 			return entityType;
 		}
 	}
 
 	public Type getSelectType() {
 		if (entityType==null) return null;
 		boolean shallow = fromElement.getFromClause().getWalker().isShallowQuery();
 		return fromElement.getSessionFactoryHelper()
 				.getFactory()
 				.getTypeResolver()
 				.getTypeFactory().manyToOne( entityType.getAssociatedEntityName(), shallow );
 	}
 
 	/**
 	 * Returns the Hibernate queryable implementation for the HQL class.
 	 *
 	 * @return the Hibernate queryable implementation for the HQL class.
 	 */
 	public Queryable getQueryable() {
 		return ( persister instanceof Queryable ) ? ( Queryable ) persister : null;
 	}
 
 	/**
 	 * Render the identifier select, but in a 'scalar' context (i.e. generate the column alias).
 	 *
 	 * @param i the sequence of the returned type
 	 * @return the identifier select with the column alias.
 	 */
 	String renderScalarIdentifierSelect(int i) {
 		checkInitialized();
 		String[] cols = getPropertyMapping( EntityPersister.ENTITY_ID ).toColumns( getTableAlias(), EntityPersister.ENTITY_ID );
 		StringBuilder buf = new StringBuilder();
 		// For property references generate <tablealias>.<columnname> as <projectionalias>
 		for ( int j = 0; j < cols.length; j++ ) {
 			String column = cols[j];
 			if ( j > 0 ) {
 				buf.append( ", " );
 			}
 			buf.append( column ).append( " as " ).append( NameGenerator.scalarName( i, j ) );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Returns the identifier select SQL fragment.
 	 *
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the identifier select SQL fragment.
 	 */
 	String renderIdentifierSelect(int size, int k) {
 		checkInitialized();
 		// Render the identifier select fragment using the table alias.
 		if ( fromElement.getFromClause().isSubQuery() ) {
 			// TODO: Replace this with a more elegant solution.
 			String[] idColumnNames = ( persister != null ) ?
 					( ( Queryable ) persister ).getIdentifierColumnNames() : new String[0];
 			StringBuilder buf = new StringBuilder();
 			for ( int i = 0; i < idColumnNames.length; i++ ) {
 				buf.append( fromElement.getTableAlias() ).append( '.' ).append( idColumnNames[i] );
 				if ( i != idColumnNames.length - 1 ) buf.append( ", " );
 			}
 			return buf.toString();
 		}
 		else {
 			if (persister==null) {
 				throw new QueryException( "not an entity" );
 			}
 			String fragment = ( ( Queryable ) persister ).identifierSelectFragment( getTableAlias(), getSuffix( size, k ) );
 			return trimLeadingCommaAndSpaces( fragment );
 		}
 	}
 
 	private String getSuffix(int size, int sequence) {
 		return generateSuffix( size, sequence );
 	}
 
 	private static String generateSuffix(int size, int k) {
 		String suffix = size == 1 ? "" : Integer.toString( k ) + '_';
 		return suffix;
 	}
 
 	private void checkInitialized() {
 		fromElement.checkInitialized();
 	}
 
 	/**
 	 * Returns the property select SQL fragment.
 	 * @param size The total number of returned types.
 	 * @param k    The sequence of the current returned type.
 	 * @return the property select SQL fragment.
 	 */
 	String renderPropertySelect(int size, int k, boolean allProperties) {
 		checkInitialized();
 		if ( persister == null ) {
 			return "";
 		}
 		else {
 			String fragment =  ( ( Queryable ) persister ).propertySelectFragment(
 					getTableAlias(),
 					getSuffix( size, k ),
 					allProperties
 			);
 			return trimLeadingCommaAndSpaces( fragment );
 		}
 	}
 
 	String renderCollectionSelectFragment(int size, int k) {
 		if ( queryableCollection == null ) {
 			return "";
 		}
 		else {
 			if ( collectionSuffix == null ) {
 				collectionSuffix = generateSuffix( size, k );
 			}
 			String fragment = queryableCollection.selectFragment( getCollectionTableAlias(), collectionSuffix );
 			return trimLeadingCommaAndSpaces( fragment );
 		}
 	}
 
 	public String renderValueCollectionSelectFragment(int size, int k) {
 		if ( queryableCollection == null ) {
 			return "";
 		}
 		else {
 			if ( collectionSuffix == null ) {
 				collectionSuffix = generateSuffix( size, k );
 			}
 			String fragment =  queryableCollection.selectFragment( getTableAlias(), collectionSuffix );
 			return trimLeadingCommaAndSpaces( fragment );
 		}
 	}
 
 	/**
 	 * This accounts for a quirk in Queryable, where it sometimes generates ',  ' in front of the
 	 * SQL fragment.  :-P
 	 *
 	 * @param fragment An SQL fragment.
 	 * @return The fragment, without the leading comma and spaces.
 	 */
 	private static String trimLeadingCommaAndSpaces(String fragment) {
 		if ( fragment.length() > 0 && fragment.charAt( 0 ) == ',' ) {
 			fragment = fragment.substring( 1 );
 		}
 		fragment = fragment.trim();
 		return fragment.trim();
 	}
 
+
 	public void setJoinSequence(JoinSequence joinSequence) {
 		this.joinSequence = joinSequence;
+		joinSequence.applyTreatAsDeclarations( treatAsDeclarations );
 	}
 
 	public JoinSequence getJoinSequence() {
 		if ( joinSequence != null ) {
 			return joinSequence;
 		}
 
 		// Class names in the FROM clause result in a JoinSequence (the old FromParser does this).
 		if ( persister instanceof Joinable ) {
 			Joinable joinable = ( Joinable ) persister;
-			return fromElement.getSessionFactoryHelper().createJoinSequence().setRoot( joinable, getTableAlias() );
+			final JoinSequence joinSequence = fromElement.getSessionFactoryHelper().createJoinSequence().setRoot( joinable, getTableAlias() );
+			joinSequence.applyTreatAsDeclarations( treatAsDeclarations );
+			return joinSequence;
 		}
 		else {
-			return null;	// TODO: Should this really return null?  If not, figure out something better to do here.
+			// TODO: Should this really return null?  If not, figure out something better to do here.
+			return null;
+		}
+	}
+
+	private Set<String> treatAsDeclarations;
+
+	public void applyTreatAsDeclarations(Set<String> treatAsDeclarations) {
+		if ( treatAsDeclarations != null && !treatAsDeclarations.isEmpty() ) {
+			if ( this.treatAsDeclarations == null ) {
+				this.treatAsDeclarations = new HashSet<String>();
+			}
+
+			for ( String treatAsSubclassName : treatAsDeclarations ) {
+				try {
+					EntityPersister subclassPersister = fromElement.getSessionFactoryHelper().requireClassPersister( treatAsSubclassName );
+					this.treatAsDeclarations.add( subclassPersister.getEntityName() );
+				}
+				catch (SemanticException e) {
+					throw new QueryException( "Unable to locate persister for subclass named in TREAT-AS : " + treatAsSubclassName );
+				}
+			}
+
+			if ( joinSequence != null ) {
+				joinSequence.applyTreatAsDeclarations( this.treatAsDeclarations );
+			}
 		}
 	}
 
 	public void setQueryableCollection(QueryableCollection queryableCollection) {
 		if ( this.queryableCollection != null ) {
 			throw new IllegalStateException( "QueryableCollection is already defined for " + this + "!" );
 		}
 		this.queryableCollection = queryableCollection;
 		if ( !queryableCollection.isOneToMany() ) {
 			// For many-to-many joins, use the tablename from the queryable collection for the default text.
 			fromElement.setText( queryableCollection.getTableName() + " " + getTableAlias() );
 		}
 	}
 
 	public QueryableCollection getQueryableCollection() {
 		return queryableCollection;
 	}
 
 	/**
 	 * Returns the type of a property, given it's name (the last part) and the full path.
 	 *
 	 * @param propertyName The last part of the full path to the property.
 	 * @return The type.
 	 * @0param getPropertyPath The full property path.
 	 */
 	public Type getPropertyType(String propertyName, String propertyPath) {
 		checkInitialized();
 		Type type = null;
 		// If this is an entity and the property is the identifier property, then use getIdentifierType().
 		//      Note that the propertyName.equals( getPropertyPath ) checks whether we have a component
 		//      key reference, where the component class property name is the same as the
 		//      entity id property name; if the two are not equal, this is the case and
 		//      we'd need to "fall through" to using the property mapping.
 		if ( persister != null && propertyName.equals( propertyPath ) && propertyName.equals( persister.getIdentifierPropertyName() ) ) {
 			type = persister.getIdentifierType();
 		}
 		else {	// Otherwise, use the property mapping.
 			PropertyMapping mapping = getPropertyMapping( propertyName );
 			type = mapping.toType( propertyPath );
 		}
 		if ( type == null ) {
 			throw new MappingException( "Property " + propertyName + " does not exist in " +
 					( ( queryableCollection == null ) ? "class" : "collection" ) + " "
 					+ ( ( queryableCollection == null ) ? fromElement.getClassName() : queryableCollection.getRole() ) );
 		}
 		return type;
 	}
 
 	String[] toColumns(String tableAlias, String path, boolean inSelect) {
 		return toColumns( tableAlias, path, inSelect, false );
 	}
 
 	String[] toColumns(String tableAlias, String path, boolean inSelect, boolean forceAlias) {
 		checkInitialized();
 		PropertyMapping propertyMapping = getPropertyMapping( path );
 
 		if ( !inSelect && queryableCollection != null && CollectionProperties.isCollectionProperty( path ) ) {
 			// If this from element is a collection and the path is a collection property (maxIndex, etc.)
 			// requiring a sub-query then generate a sub-query.
 			//h
 			// Unless we are in the select clause, because some dialects do not support
 			// Note however, that some dialects do not  However, in the case of this being a collection property reference being in the select, not generating the subquery
 			// will not generally work.  The specific cases I am thinking about are the minIndex, maxIndex
 			// (most likely minElement, maxElement as well) cases.
 			//	todo : if ^^ is the case we should thrown an exception here rather than waiting for the sql error
 			//		if the dialect supports select-clause subqueries we could go ahead and generate the subquery also
 			Map enabledFilters = fromElement.getWalker().getEnabledFilters();
 			String subquery = CollectionSubqueryFactory.createCollectionSubquery(
 					joinSequence.copy().setUseThetaStyle( true ),
 					enabledFilters,
 					propertyMapping.toColumns( tableAlias, path )
 			);
 			LOG.debugf( "toColumns(%s,%s) : subquery = %s", tableAlias, path, subquery );
 			return new String[]{"(" + subquery + ")"};
 		}
 
         if (forceAlias) {
             return propertyMapping.toColumns(tableAlias, path);
         }
 
 		if (fromElement.getWalker().getStatementType() == HqlSqlTokenTypes.SELECT) {
             return propertyMapping.toColumns(tableAlias, path);
         }
 
 		if (fromElement.getWalker().isSubQuery()) {
             // for a subquery, the alias to use depends on a few things (we
             // already know this is not an overall SELECT):
             // 1) if this FROM_ELEMENT represents a correlation to the
             // outer-most query
             // A) if the outer query represents a multi-table
             // persister, we need to use the given alias
             // in anticipation of one of the multi-table
             // executors being used (as this subquery will
             // actually be used in the "id select" phase
             // of that multi-table executor)
             // B) otherwise, we need to use the persister's
             // table name as the column qualification
             // 2) otherwise (not correlated), use the given alias
             if (isCorrelation()) {
                 if (isMultiTable()) {
 					return propertyMapping.toColumns(tableAlias, path);
 				}
                 return propertyMapping.toColumns(extractTableName(), path);
 			}
             return propertyMapping.toColumns(tableAlias, path);
         }
 
 		if (fromElement.getWalker().getCurrentTopLevelClauseType() == HqlSqlTokenTypes.SELECT) {
             return propertyMapping.toColumns(tableAlias, path);
         }
 
 		if ( isManipulationQuery() && isMultiTable() && inWhereClause() ) {
 			// the actual where-clause will end up being ripped out the update/delete and used in
 			// a select to populate the temp table, so its ok to use the table alias to qualify the table refs
 			// and safer to do so to protect from same-named columns
 			return propertyMapping.toColumns( tableAlias, path );
 		}
 
 		String[] columns = propertyMapping.toColumns( path );
 		LOG.tracev( "Using non-qualified column reference [{0} -> ({1})]", path, ArrayHelper.toString( columns ) );
 		return columns;
 	}
 
 	private boolean isCorrelation() {
 		FromClause top = fromElement.getWalker().getFinalFromClause();
 		return fromElement.getFromClause() != fromElement.getWalker().getCurrentFromClause() &&
 	           fromElement.getFromClause() == top;
 	}
 
 	private boolean isMultiTable() {
 		// should be safe to only ever expect EntityPersister references here
 		return fromElement.getQueryable() != null &&
 	           fromElement.getQueryable().isMultiTable();
 	}
 
 	private String extractTableName() {
 		// should be safe to only ever expect EntityPersister references here
 		return fromElement.getQueryable().getTableName();
 	}
 
 	private boolean isManipulationQuery() {
 		return fromElement.getWalker().getStatementType() == HqlSqlTokenTypes.UPDATE
 				|| fromElement.getWalker().getStatementType() == HqlSqlTokenTypes.DELETE;
 	}
 
 	private boolean inWhereClause() {
 		return fromElement.getWalker().getCurrentTopLevelClauseType() == HqlSqlTokenTypes.WHERE;
 	}
 
 	private static final List SPECIAL_MANY2MANY_TREATMENT_FUNCTION_NAMES = java.util.Arrays.asList(
 			CollectionPropertyNames.COLLECTION_INDEX,
 			CollectionPropertyNames.COLLECTION_MIN_INDEX,
 			CollectionPropertyNames.COLLECTION_MAX_INDEX
 	);
 
 	PropertyMapping getPropertyMapping(String propertyName) {
 		checkInitialized();
 		if ( queryableCollection == null ) {		// Not a collection?
 			return ( PropertyMapping ) persister;	// Return the entity property mapping.
 		}
 
 		// indexed, many-to-many collections must be treated specially here if the property to
 		// be mapped touches on the index as we must adjust the alias to use the alias from
 		// the association table (which i different than the one passed in
 		if ( queryableCollection.isManyToMany()
 				&& queryableCollection.hasIndex()
 				&& SPECIAL_MANY2MANY_TREATMENT_FUNCTION_NAMES.contains( propertyName ) ) {
 			return new SpecialManyToManyCollectionPropertyMapping();
 		}
 
 		// If the property is a special collection property name, return a CollectionPropertyMapping.
 		if ( CollectionProperties.isCollectionProperty( propertyName ) ) {
 			if ( collectionPropertyMapping == null ) {
 				collectionPropertyMapping = new CollectionPropertyMapping( queryableCollection );
 			}
 			return collectionPropertyMapping;
 		}
 
 		if ( queryableCollection.getElementType().isAnyType() ) {
 			// collection of <many-to-any/> mappings...
 			// used to circumvent the component-collection check below...
 			return queryableCollection;
 
 		}
 
 		if ( queryableCollection.getElementType().isComponentType() ) {
 			// Collection of components.
 			if ( propertyName.equals( EntityPersister.ENTITY_ID ) ) {
 				return ( PropertyMapping ) queryableCollection.getOwnerEntityPersister();
 			}
 		}
 		return queryableCollection;
 	}
 
 	public boolean isCollectionOfValuesOrComponents() {
 		return persister == null
 				&& queryableCollection != null
 				&& !queryableCollection.getElementType().isEntityType();
 	}
 
 	public boolean isEntity() {
 		return persister != null;
 	}
 
 	public ParameterSpecification getIndexCollectionSelectorParamSpec() {
 		return indexCollectionSelectorParamSpec;
 	}
 
 	public void setIndexCollectionSelectorParamSpec(ParameterSpecification indexCollectionSelectorParamSpec) {
 		this.indexCollectionSelectorParamSpec = indexCollectionSelectorParamSpec;
 	}
 
 	private class SpecialManyToManyCollectionPropertyMapping implements PropertyMapping {
 		/**
 		 * {@inheritDoc}
 		 */
 		public Type getType() {
 			return queryableCollection.getCollectionType();
 		}
 
 		private void validate(String propertyName) {
 			if ( ! ( CollectionPropertyNames.COLLECTION_INDEX.equals( propertyName )
 					|| CollectionPropertyNames.COLLECTION_MAX_INDEX.equals( propertyName )
 					|| CollectionPropertyNames.COLLECTION_MIN_INDEX.equals( propertyName ) ) ) {
 				throw new IllegalArgumentException( "Expecting index-related function call" );
 			}
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Type toType(String propertyName) throws QueryException {
 			validate( propertyName );
 			return queryableCollection.getIndexType();
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public String[] toColumns(String alias, String propertyName) throws QueryException {
 			validate( propertyName );
 			final String joinTableAlias = joinSequence.getFirstJoin().getAlias();
 			if ( CollectionPropertyNames.COLLECTION_INDEX.equals( propertyName ) ) {
 				return queryableCollection.toColumns( joinTableAlias, propertyName );
 			}
 
 			final String[] cols = queryableCollection.getIndexColumnNames( joinTableAlias );
 			if ( CollectionPropertyNames.COLLECTION_MIN_INDEX.equals( propertyName ) ) {
 				if ( cols.length != 1 ) {
 					throw new QueryException( "composite collection index in minIndex()" );
 				}
 				return new String[] { "min(" + cols[0] + ')' };
 			}
 			else {
 				if ( cols.length != 1 ) {
 					throw new QueryException( "composite collection index in maxIndex()" );
 				}
 				return new String[] { "max(" + cols[0] + ')' };
 			}
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
 			validate( propertyName );
 			return queryableCollection.toColumns( propertyName );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
index 119838102b..154f7b8a6f 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
@@ -1,377 +1,381 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.List;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 import org.hibernate.QueryException;
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
 
 /**
  * Represents an identifier all by itself, which may be a function name,
  * a class alias, or a form of naked property-ref depending on the
  * context.
  *
  * @author josh
  */
 public class IdentNode extends FromReferenceNode implements SelectExpression {
 
 	private static final int UNKNOWN = 0;
 	private static final int PROPERTY_REF = 1;
 	private static final int COMPONENT_REF = 2;
 
 	private boolean nakedPropertyRef = false;
 
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
 
 	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent) {
 		if (!isResolved()) {
 			if (getWalker().getCurrentFromClause().isFromElementAlias(getText())) {
 				if (resolveAsAlias()) {
 					setResolved();
 					// We represent a from-clause alias
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
 				int result = resolveAsNakedPropertyRef();
 				if (result == PROPERTY_REF) {
 					// we represent a naked (simple) prop-ref
 					setResolved();
 				}
 				else if (result == COMPONENT_REF) {
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
+		final String alias = getText();
+
 		// This is not actually a constant, but a reference to FROM element.
-		final FromElement element = getWalker().getCurrentFromClause().getFromElement( getText() );
+		final FromElement element = getWalker().getCurrentFromClause().getFromElement( alias );
 		if ( element == null ) {
 			return false;
 		}
 
+		element.applyTreatAsDeclarations( getWalker().getTreatAsDeclarationsByPath( alias ) );
+
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
 
 		final boolean isInNonDistinctCount = getWalker().isInCount() && ! getWalker().isInCountDistinct();
 		final boolean isCompositeValue = columnExpressions.length > 1;
 		if ( isCompositeValue ) {
 			if ( isInNonDistinctCount && ! getWalker().getSessionFactoryHelper().getFactory().getDialect().supportsTupleCounts() ) {
 				setText( columnExpressions[0] );
 			}
 			else {
 				String joinedFragment = StringHelper.join( ", ", columnExpressions );
 				// avoid wrapping in parenthesis (explicit tuple treatment) if possible due to varied support for
 				// tuple syntax across databases..
 				final boolean shouldSkipWrappingInParenthesis =
 						getWalker().isInCount()
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
 		catch (Throwable t) {
 		}
 		return propertyType;
 	}
 
 	private int resolveAsNakedPropertyRef() {
 		FromElement fromElement = locateSingleFromElement();
 		if (fromElement == null) {
 			return UNKNOWN;
 		}
 		Queryable persister = fromElement.getQueryable();
 		if (persister == null) {
 			return UNKNOWN;
 		}
 		Type propertyType = getNakedPropertyType(fromElement);
 		if (propertyType == null) {
 			// assume this ident's text does *not* refer to a property on the given persister
 			return UNKNOWN;
 		}
 
 		if ((propertyType.isComponentType() || propertyType.isAssociationType() )) {
 			return COMPONENT_REF;
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
 
 		return PROPERTY_REF;
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
 
 		Type propertyType = null;  // used to set the type of the parent dot node
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
 
 		Type propertyType = null;
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
 				setText(fe.renderScalarIdentifierSelect(i));
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
 			buf.append("{originalText=" + getOriginalText()).append("}");
 		}
 		return buf.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java
index c9a9551b19..807a26d860 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ArrayHelper.java
@@ -1,424 +1,434 @@
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
 package org.hibernate.internal.util.collections;
 
 import java.io.Serializable;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.type.Type;
 
 public final class ArrayHelper {
 	
 	/*public static boolean contains(Object[] array, Object object) {
 		for ( int i=0; i<array.length; i++ ) {
 			if ( array[i].equals(object) ) return true;
 		}
 		return false;
 	}*/
 	
 	public static int indexOf(Object[] array, Object object) {
 		for ( int i=0; i<array.length; i++ ) {
 			if ( array[i].equals(object) ) return i;
 		}
 		return -1;
 	}
 	
 	/*public static Object[] clone(Class elementClass, Object[] array) {
 		Object[] result = (Object[]) Array.newInstance( elementClass, array.length );
 		System.arraycopy(array, 0, result, 0, array.length);
 		return result;
 	}*/
 
 	public static String[] toStringArray(Object[] objects) {
 		int length=objects.length;
 		String[] result = new String[length];
 		for (int i=0; i<length; i++) {
 			result[i] = objects[i].toString();
 		}
 		return result;
 	}
 
 	public static String[] fillArray(String value, int length) {
 		String[] result = new String[length];
 		Arrays.fill(result, value);
 		return result;
 	}
 
 	public static int[] fillArray(int value, int length) {
 		int[] result = new int[length];
 		Arrays.fill(result, value);
 		return result;
 	}
 
 	public static LockMode[] fillArray(LockMode lockMode, int length) {
 		LockMode[] array = new LockMode[length];
 		Arrays.fill(array, lockMode);
 		return array;
 	}
 
 	public static LockOptions[] fillArray(LockOptions lockOptions, int length) {
 		LockOptions[] array = new LockOptions[length];
 		Arrays.fill(array, lockOptions);
 		return array;
 	}
 
 	public static String[] toStringArray(Collection coll) {
 		return (String[]) coll.toArray( new String[coll.size()] );
 	}
 	
 	public static String[][] to2DStringArray(Collection coll) {
 		return (String[][]) coll.toArray( new String[ coll.size() ][] );
 	}
 	
 	public static int[][] to2DIntArray(Collection coll) {
 		return (int[][]) coll.toArray( new int[ coll.size() ][] );
 	}
 	
 	public static Type[] toTypeArray(Collection coll) {
 		return (Type[]) coll.toArray( new Type[coll.size()] );
 	}
 
 	public static int[] toIntArray(Collection coll) {
 		Iterator iter = coll.iterator();
 		int[] arr = new int[ coll.size() ];
 		int i=0;
 		while( iter.hasNext() ) {
 			arr[i++] = (Integer) iter.next();
 		}
 		return arr;
 	}
 
 	public static boolean[] toBooleanArray(Collection coll) {
 		Iterator iter = coll.iterator();
 		boolean[] arr = new boolean[ coll.size() ];
 		int i=0;
 		while( iter.hasNext() ) {
 			arr[i++] = (Boolean) iter.next();
 		}
 		return arr;
 	}
 
 	public static Object[] typecast(Object[] array, Object[] to) {
 		return java.util.Arrays.asList(array).toArray(to);
 	}
 
 	//Arrays.asList doesn't do primitive arrays
 	public static List toList(Object array) {
 		if ( array instanceof Object[] ) return Arrays.asList( (Object[]) array ); //faster?
 		int size = Array.getLength(array);
 		ArrayList list = new ArrayList(size);
 		for (int i=0; i<size; i++) {
 			list.add( Array.get(array, i) );
 		}
 		return list;
 	}
 
 	public static String[] slice(String[] strings, int begin, int length) {
 		String[] result = new String[length];
 		System.arraycopy( strings, begin, result, 0, length );
 		return result;
 	}
 
 	public static Object[] slice(Object[] objects, int begin, int length) {
 		Object[] result = new Object[length];
 		System.arraycopy( objects, begin, result, 0, length );
 		return result;
 	}
 
 	public static List toList(Iterator iter) {
 		List list = new ArrayList();
 		while ( iter.hasNext() ) {
 			list.add( iter.next() );
 		}
 		return list;
 	}
 
 	public static String[] join(String[] x, String[] y) {
 		String[] result = new String[ x.length + y.length ];
 		System.arraycopy( x, 0, result, 0, x.length );
 		System.arraycopy( y, 0, result, x.length, y.length );
 		return result;
 	}
 
 	public static String[] join(String[] x, String[] y, boolean[] use) {
 		String[] result = new String[ x.length + countTrue(use) ];
 		System.arraycopy( x, 0, result, 0, x.length );
 		int k = x.length;
 		for ( int i=0; i<y.length; i++ ) {
 			if ( use[i] ) {
 				result[k++] = y[i];
 			}
 		}
 		return result;
 	}
 
 	public static int[] join(int[] x, int[] y) {
 		int[] result = new int[ x.length + y.length ];
 		System.arraycopy( x, 0, result, 0, x.length );
 		System.arraycopy( y, 0, result, x.length, y.length );
 		return result;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public static <T> T[] join(T[] x, T... y) {
 		T[] result = (T[]) Array.newInstance( x.getClass().getComponentType(), x.length + y.length );
 		System.arraycopy( x, 0, result, 0, x.length );
 		System.arraycopy( y, 0, result, x.length, y.length );
 		return result;
 	}
 
 	public static final boolean[] TRUE = { true };
 	public static final boolean[] FALSE = { false };
 
 	private ArrayHelper() {}
 
 	public static String toString( Object[] array ) {
 		StringBuilder sb = new StringBuilder();
 		sb.append("[");
 		for (int i = 0; i < array.length; i++) {
 			sb.append( array[i] );
 			if( i<array.length-1 ) sb.append(",");
 		}
 		sb.append("]");
 		return sb.toString();
 	}
 
 	public static boolean isAllNegative(int[] array) {
 		for ( int anArray : array ) {
 			if ( anArray >= 0 ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public static boolean isAllTrue(boolean[] array) {
 		for ( boolean anArray : array ) {
 			if ( !anArray ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public static int countTrue(boolean[] array) {
 		int result=0;
 		for ( boolean anArray : array ) {
 			if ( anArray ) {
 				result++;
 			}
 		}
 		return result;
 	}
 
 	/*public static int countFalse(boolean[] array) {
 		int result=0;
 		for ( int i=0; i<array.length; i++ ) {
 			if ( !array[i] ) result++;
 		}
 		return result;
 	}*/
 
 	public static boolean isAllFalse(boolean[] array) {
 		for ( boolean anArray : array ) {
 			if ( anArray ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public static void addAll(Collection collection, Object[] array) {
 		collection.addAll( Arrays.asList( array ) );
 	}
 
 	public static final String[] EMPTY_STRING_ARRAY = {};
 	public static final int[] EMPTY_INT_ARRAY = {};
 	public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};
 	public static final Class[] EMPTY_CLASS_ARRAY = {};
 	public static final Object[] EMPTY_OBJECT_ARRAY = {};
 	public static final Type[] EMPTY_TYPE_ARRAY = {};
 	public static final byte[] EMPTY_BYTE_ARRAY = {};
 	
 	public static int[] getBatchSizes(int maxBatchSize) {
 		int batchSize = maxBatchSize;
 		int n=1;
 		while ( batchSize>1 ) {
 			batchSize = getNextBatchSize(batchSize);
 			n++;
 		}
 		int[] result = new int[n];
 		batchSize = maxBatchSize;
 		for ( int i=0; i<n; i++ ) {
 			result[i] = batchSize;
 			batchSize = getNextBatchSize(batchSize);
 		}
 		return result;
 	}
 	
 	private static int getNextBatchSize(int batchSize) {
 		if (batchSize<=10) {
 			return batchSize-1; //allow 9,8,7,6,5,4,3,2,1
 		}
 		else if (batchSize/2 < 10) {
 			return 10;
 		}
 		else {
 			return batchSize / 2;
 		}
 	}
 
 	private static int SEED = 23;
 	private static int PRIME_NUMER = 37;
 
 	/**
 	 * calculate the array hash (only the first level)
 	 */
 	public static int hash(Object[] array) {
 		int length = array.length;
 		int seed = SEED;
 		for ( Object anArray : array ) {
 			seed = hash( seed, anArray == null ? 0 : anArray.hashCode() );
 		}
 		return seed;
 	}
 
 	/**
 	 * calculate the array hash (only the first level)
 	 */
 	public static int hash(char[] array) {
 		int length = array.length;
 		int seed = SEED;
 		for ( char anArray : array ) {
 			seed = hash( seed, anArray );
 		}
 		return seed;
 	}
 
 	/**
 	 * calculate the array hash (only the first level)
 	 */
 	public static int hash(byte[] bytes) {
 		int length = bytes.length;
 		int seed = SEED;
 		for ( byte aByte : bytes ) {
 			seed = hash( seed, aByte );
 		}
 		return seed;
 	}
 
 	private static int hash(int seed, int i) {
 		return PRIME_NUMER * seed + i;
 	}
 
 	/**
 	 * Compare 2 arrays only at the first level
 	 */
 	public static boolean isEquals(Object[] o1, Object[] o2) {
 		if (o1 == o2) return true;
 		if (o1 == null || o2 == null) return false;
 		int length = o1.length;
 		if (length != o2.length) return false;
 		for (int index = 0 ; index < length ; index++) {
 			if ( ! o1[index].equals( o2[index] ) ) return false;
 		}
         return true;
 	}
 
 	/**
 	 * Compare 2 arrays only at the first level
 	 */
 	public static boolean isEquals(char[] o1, char[] o2) {
 		if (o1 == o2) return true;
 		if (o1 == null || o2 == null) return false;
 		int length = o1.length;
 		if (length != o2.length) return false;
 		for (int index = 0 ; index < length ; index++) {
 			if ( ! ( o1[index] == o2[index] ) ) return false;
 		}
         return true;
 	}
 
 	/**
 	 * Compare 2 arrays only at the first level
 	 */
 	public static boolean isEquals(byte[] b1, byte[] b2) {
 		if (b1 == b2) return true;
 		if (b1 == null || b2 == null) return false;
 		int length = b1.length;
 		if (length != b2.length) return false;
 		for (int index = 0 ; index < length ; index++) {
 			if ( ! ( b1[index] == b2[index] ) ) return false;
 		}
         return true;
 	}
 
 	public static Serializable[] extractNonNull(Serializable[] array) {
 		final int nonNullCount = countNonNull( array );
 		final Serializable[] result = new Serializable[nonNullCount];
 		int i = 0;
 		for ( Serializable element : array ) {
 			if ( element != null ) {
 				result[i++] = element;
 			}
 		}
 		if ( i != nonNullCount ) {
 			throw new HibernateException( "Number of non-null elements varied between iterations" );
 		}
 		return result;
 	}
 
 	public static int countNonNull(Serializable[] array) {
 		int i = 0;
 		for ( Serializable element : array ) {
 			if ( element != null ) {
 				i++;
 			}
 		}
 		return i;
 	}
 
+	public static String[] reverse(String[] source) {
+		final int length = source.length;
+		final String[] destination = new String[length];
+		for ( int i = 0; i < length; i++ ) {
+			final int x = length - i - 1;
+			destination[x] = source[i];
+		}
+		return destination;
+	}
+
 	public static void main(String... args) {
 		int[] batchSizes = ArrayHelper.getBatchSizes( 32 );
 
 		System.out.println( "Forward ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
 		for ( int i = 0; i < batchSizes.length; i++ ) {
 			System.out.println( "[" + i + "] -> " + batchSizes[i] );
 		}
 
 		System.out.println( "Backward ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
 		for ( int i = batchSizes.length-1; i >= 0; i-- ) {
 			System.out.println( "[" + i + "] -> " + batchSizes[i] );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index a07a4bf3f4..ce5f1015a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2114 +1,2135 @@
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
+import java.util.Set;
 
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
 import org.hibernate.persister.walking.internal.StandardAnyTypeDefinition;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
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
 import org.hibernate.type.AnyType;
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
 	private String mappedByProperty;
 
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
 		mappedByProperty = collection.getMappedByProperty();
 
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
 
 	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException {
 		if ( collection.hasQueuedOperations() ) {
 			doProcessQueuedOps( collection, key, session );
 		}
 	}
 
 	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
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
 
-	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
+	protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
+		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
+	}
 
+	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
+	@Override
+	public String filterFragment(
+			String alias,
+			Map enabledFilters,
+			Set<String> treatAsDeclarations) {
+		StringBuilder sessionFilterFragment = new StringBuilder();
+		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
+
+		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
+	}
+
+	@Override
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
+	@Override
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
+		return oneToManyFilterFragment( alias );
+	}
+
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
 			throw getSQLExceptionHelper().convert(
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
 			throw getSQLExceptionHelper().convert(
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
 			throw getSQLExceptionHelper().convert(
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
 
 	public String getMappedByProperty() {
 		return mappedByProperty;
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
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "index";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getIndexType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionIndexSubAttributes( this );
 					}
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
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
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
 					throw new WalkingException( "Cannot treat collection element type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new WalkingException( "Cannot treat composite collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
 			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
 					throw new WalkingException( "Cannot treat entity collection element type as composite" );
 				}
 
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "";
 					}
 
 					@Override
 					public CompositeType getType() {
 						return (CompositeType) getElementType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes( this );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
index 4778bb733e..f306f011e2 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
@@ -1,372 +1,385 @@
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
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
+import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.BatchingCollectionInitializer;
 import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.loader.collection.SubselectCollectionLoader;
 import org.hibernate.mapping.Collection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.Update;
 import org.hibernate.type.AssociationType;
 
 /**
  * Collection persister for collections of values and many-to-many associations.
  *
  * @author Gavin King
  */
 public class BasicCollectionPersister extends AbstractCollectionPersister {
 
 	public boolean isCascadeDeleteEnabled() {
 		return false;
 	}
 
 	public BasicCollectionPersister(
 			Collection collection,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			Configuration cfg,
 			SessionFactoryImplementor factory) throws MappingException, CacheException {
 		super( collection, cacheAccessStrategy, cfg, factory );
 	}
 
 	/**
 	 * Generate the SQL DELETE that deletes all rows
 	 */
 	@Override
     protected String generateDeleteString() {
 		
 		Delete delete = new Delete()
 				.setTableName( qualifiedTableName )
 				.addPrimaryKeyColumns( keyColumnNames );
 		
 		if ( hasWhere ) delete.setWhere( sqlWhereString );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete collection " + getRole() );
 		}
 		
 		return delete.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL INSERT that creates a new row
 	 */
 	@Override
     protected String generateInsertRowString() {
 		
 		Insert insert = new Insert( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames );
 		
 		if ( hasIdentifier) insert.addColumn( identifierColumnName );
 		
 		if ( hasIndex /*&& !indexIsFormula*/ ) {
 			insert.addColumns( indexColumnNames, indexColumnIsSettable );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert collection row " + getRole() );
 		}
 		
 		//if ( !elementIsFormula ) {
 			insert.addColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		//}
 		
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a row
 	 */
 	@Override
     protected String generateUpdateRowString() {
 		
 		Update update = new Update( getDialect() )
 			.setTableName( qualifiedTableName );
 		
 		//if ( !elementIsFormula ) {
 			update.addColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		//}
 		
 		if ( hasIdentifier ) {
 			update.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		else if ( hasIndex && !indexContainsFormula ) {
 			update.addPrimaryKeyColumns( ArrayHelper.join( keyColumnNames, indexColumnNames ) );
 		}
 		else {
 			update.addPrimaryKeyColumns( keyColumnNames );
 			update.addPrimaryKeyColumns( elementColumnNames, elementColumnIsInPrimaryKey, elementColumnWriters );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update collection row " + getRole() );
 		}
 		
 		return update.toStatementString();
 	}
 	
 	@Override
 	protected void doProcessQueuedOps(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		// nothing to do
 	}
 
 	/**
 	 * Generate the SQL DELETE that deletes a particular row
 	 */
 	@Override
     protected String generateDeleteRowString() {
 		
 		Delete delete = new Delete()
 			.setTableName( qualifiedTableName );
 		
 		if ( hasIdentifier ) {
 			delete.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		else if ( hasIndex && !indexContainsFormula ) {
 			delete.addPrimaryKeyColumns( ArrayHelper.join( keyColumnNames, indexColumnNames ) );
 		}
 		else {
 			delete.addPrimaryKeyColumns( keyColumnNames );
 			delete.addPrimaryKeyColumns( elementColumnNames, elementColumnIsInPrimaryKey, elementColumnWriters );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete collection row " + getRole() );
 		}
 		
 		return delete.toStatementString();
 	}
 
 	public boolean consumesEntityAlias() {
 		return false;
 	}
 
 	public boolean consumesCollectionAlias() {
 //		return !isOneToMany();
 		return true;
 	}
 
 	public boolean isOneToMany() {
 		return false;
 	}
 
 	@Override
     public boolean isManyToMany() {
 		return elementType.isEntityType(); //instanceof AssociationType;
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	@Override
     protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException {
 		
 		if ( ArrayHelper.isAllFalse(elementColumnIsSettable) ) return 0;
 
 		try {
 			PreparedStatement st = null;
 			Expectation expectation = Expectations.appropriateExpectation( getUpdateCheckStyle() );
 			boolean callable = isUpdateCallable();
 			boolean useBatch = expectation.canBeBatched();
 			Iterator entries = collection.entries( this );
 			String sql = getSQLUpdateRowString();
 			int i = 0;
 			int count = 0;
 			while ( entries.hasNext() ) {
 				Object entry = entries.next();
 				if ( collection.needsUpdating( entry, i, elementType ) ) {
 					int offset = 1;
 
 					if ( useBatch ) {
 						if ( updateBatchKey == null ) {
 							updateBatchKey = new BasicBatchKey(
 									getRole() + "#UPDATE",
 									expectation
 							);
 						}
 						st = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( updateBatchKey )
 								.getBatchStatement( sql, callable );
 					}
 					else {
 						st = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( sql, callable );
 					}
 
 					try {
 						offset+= expectation.prepare( st );
 						int loc = writeElement( st, collection.getElement( entry ), offset, session );
 						if ( hasIdentifier ) {
 							writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 						}
 						else {
 							loc = writeKey( st, id, loc, session );
 							if ( hasIndex && !indexContainsFormula ) {
 								writeIndexToWhere( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 							else {
 								writeElementToWhere( st, collection.getSnapshotElement( entry, i ), loc, session );
 							}
 						}
 
 						if ( useBatch ) {
 							session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getBatch( updateBatchKey )
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
 					count++;
 				}
 				i++;
 			}
 			return count;
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not update collection rows: " + MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLUpdateRowString()
 				);
 		}
 	}
 
 	public String selectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String entitySuffix,
 	        String collectionSuffix,
 	        boolean includeCollectionColumns) {
 		// we need to determine the best way to know that two joinables
 		// represent a single many-to-many...
 		if ( rhs != null && isManyToMany() && !rhs.isCollection() ) {
 			AssociationType elementType = ( ( AssociationType ) getElementType() );
 			if ( rhs.equals( elementType.getAssociatedJoinable( getFactory() ) ) ) {
 				return manyToManySelectFragment( rhs, rhsAlias, lhsAlias, collectionSuffix );
 			}
 		}
 		return includeCollectionColumns ? selectFragment( lhsAlias, collectionSuffix ) : "";
 	}
 
 	private String manyToManySelectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String collectionSuffix) {
 		SelectFragment frag = generateSelectFragment( lhsAlias, collectionSuffix );
 
 		String[] elementColumnNames = rhs.getKeyColumnNames();
 		frag.addColumns( rhsAlias, elementColumnNames, elementColumnAliases );
 		appendIndexColumns( frag, lhsAlias );
 		appendIdentifierColumns( frag, lhsAlias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); //strip leading ','
 	}
 
 	/**
 	 * Create the <tt>CollectionLoader</tt>
 	 *
 	 * @see org.hibernate.loader.collection.BasicCollectionLoader
 	 */
 	@Override
     protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException {
 		return BatchingCollectionInitializerBuilder.getBuilder( getFactory() )
 				.createBatchingCollectionInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
 	}
 
+	@Override
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return "";
 	}
 
+	@Override
+	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
+		return "";
+	}
+
+	@Override
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return "";
 	}
 
 	@Override
+	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
+		return "";
+	}
+
+	@Override
     protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session) {
 		return new SubselectCollectionLoader( 
 				this,
 				subselect.toSubselectString( getCollectionType().getLHSPropertyName() ),
 				subselect.getResult(),
 				subselect.getQueryParameters(),
 				subselect.getNamedParameterLocMap(),
 				session.getFactory(),
 				session.getLoadQueryInfluencers() 
 		);
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
index 2da0fe4a3b..cf86a134e7 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
@@ -1,532 +1,558 @@
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
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
+import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.loader.collection.SubselectOneToManyLoader;
 import org.hibernate.loader.entity.CollectionElementLoader;
 import org.hibernate.mapping.Collection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 
 /**
  * Collection persister for one-to-many associations.
  *
  * @author Gavin King
  * @author Brett Meyer
  */
 public class OneToManyPersister extends AbstractCollectionPersister {
 
 	private final boolean cascadeDeleteEnabled;
 	private final boolean keyIsNullable;
 	private final boolean keyIsUpdateable;
 
 	@Override
     protected boolean isRowDeleteEnabled() {
 		return keyIsUpdateable && keyIsNullable;
 	}
 
 	@Override
     protected boolean isRowInsertEnabled() {
 		return keyIsUpdateable;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public OneToManyPersister(
 			Collection collection,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			Configuration cfg,
 			SessionFactoryImplementor factory) throws MappingException, CacheException {
 		super( collection, cacheAccessStrategy, cfg, factory );
 		cascadeDeleteEnabled = collection.getKey().isCascadeDeleteEnabled() &&
 				factory.getDialect().supportsCascadeDelete();
 		keyIsNullable = collection.getKey().isNullable();
 		keyIsUpdateable = collection.getKey().isUpdateable();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates all the foreign keys to null
 	 */
 	@Override
     protected String generateDeleteString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames, "null" )
 				.addPrimaryKeyColumns( keyColumnNames );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames, "null" );
 		
 		if ( hasWhere ) update.setWhere( sqlWhereString );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "delete one-to-many " + getRole() );
 		}
 		
 		return update.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a foreign key to a value
 	 */
 	@Override
     protected String generateInsertRowString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames );
 		
 		//identifier collections not supported for 1-to-many
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "create one-to-many row " + getRole() );
 		}
 		
 		return update.addPrimaryKeyColumns( elementColumnNames, elementColumnWriters )
 				.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that inserts a collection index
 	 */
 	@Override
     protected String generateUpdateRowString() {
 		Update update = new Update( getDialect() ).setTableName( qualifiedTableName );
 		update.addPrimaryKeyColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		if ( hasIdentifier ) {
 			update.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		if ( hasIndex && !indexContainsFormula ) {
 			update.addColumns( indexColumnNames );
 		}
 		
 		return update.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a particular row's foreign
 	 * key to null
 	 */
 	@Override
     protected String generateDeleteRowString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames, "null" );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames, "null" );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "delete one-to-many row " + getRole() );
 		}
 		
 		//use a combination of foreign key columns and pk columns, since
 		//the ordering of removal and addition is not guaranteed when
 		//a child moves from one parent to another
 		String[] rowSelectColumnNames = ArrayHelper.join( keyColumnNames, elementColumnNames );
 		return update.addPrimaryKeyColumns( rowSelectColumnNames )
 				.toStatementString();
 	}
 	
 	@Override
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		super.recreate( collection, id, session );
 		writeIndex( collection, collection.entries( this ), id, session );
 	}
 	
 	@Override
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		super.insertRows( collection, id, session );
 		writeIndex( collection, collection.entries( this ), id, session );
 	}
 	
 	@Override
 	protected void doProcessQueuedOps(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		writeIndex( collection, collection.queuedAdditionIterator(), id, session );
 	}
 	
 	private void writeIndex(PersistentCollection collection, Iterator entries, Serializable id, SessionImplementor session) {
 		// If one-to-many and inverse, still need to create the index.  See HHH-5732.
 		if ( isInverse && hasIndex && !indexContainsFormula ) {
 			try {
 				if ( entries.hasNext() ) {
 					Expectation expectation = Expectations.appropriateExpectation( getUpdateCheckStyle() );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( entry != null && collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isUpdateCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLUpdateRowString();
 
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
 								if ( hasIdentifier ) {
 									offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 								}
 								offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 								offset = writeElement( st, collection.getElement( entry ), offset, session );
 
 								if ( useBatch ) {
 									session.getTransactionCoordinator()
 											.getJdbcCoordinator()
 											.getBatch( recreateBatchKey )
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
 
 						}
 						i++;
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not update collection: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLUpdateRowString()
 						);
 			}
 		}
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 	public boolean consumesCollectionAlias() {
 		return true;
 	}
 
 	public boolean isOneToMany() {
 		return true;
 	}
 
 	@Override
     public boolean isManyToMany() {
 		return false;
 	}
 
 	private BasicBatchKey deleteRowBatchKey;
 	private BasicBatchKey insertRowBatchKey;
 
 	@Override
     protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session) {
 
 		// we finish all the "removes" first to take care of possible unique
 		// constraints and so that we can take better advantage of batching
 		
 		try {
 			int count = 0;
 			if ( isRowDeleteEnabled() ) {
 				final Expectation deleteExpectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 				final boolean useBatch = deleteExpectation.canBeBatched();
 				if ( useBatch && deleteRowBatchKey == null ) {
 					deleteRowBatchKey = new BasicBatchKey(
 							getRole() + "#DELETEROW",
 							deleteExpectation
 					);
 				}
 				final String sql = getSQLDeleteRowString();
 
 				PreparedStatement st = null;
 				// update removed rows fks to null
 				try {
 					int i = 0;
 					Iterator entries = collection.entries( this );
 					int offset = 1;
 					while ( entries.hasNext() ) {
 						Object entry = entries.next();
 						if ( collection.needsUpdating( entry, i, elementType ) ) {  // will still be issued when it used to be null
 							if ( useBatch ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteRowBatchKey )
 										.getBatchStatement( sql, isDeleteCallable() );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, isDeleteCallable() );
 							}
 							int loc = writeKey( st, id, offset, session );
 							writeElementToWhere( st, collection.getSnapshotElement(entry, i), loc, session );
 							if ( useBatch ) {
 								session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteRowBatchKey )
 										.addToBatch();
 							}
 							else {
 								deleteExpectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 							count++;
 						}
 						i++;
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 					}
 				}
 			}
 			
 			if ( isRowInsertEnabled() ) {
 				final Expectation insertExpectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean useBatch = insertExpectation.canBeBatched();
 				boolean callable = isInsertCallable();
 				if ( useBatch && insertRowBatchKey == null ) {
 					insertRowBatchKey = new BasicBatchKey(
 							getRole() + "#INSERTROW",
 							insertExpectation
 					);
 				}
 				final String sql = getSQLInsertRowString();
 
 				PreparedStatement st = null;
 				// now update all changed or added rows fks
 				try {
 					int i = 0;
 					Iterator entries = collection.entries( this );
 					while ( entries.hasNext() ) {
 						Object entry = entries.next();
 						int offset = 1;
 						if ( collection.needsUpdating( entry, i, elementType ) ) {
 							if ( useBatch ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( insertRowBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
 							}
 
 							offset += insertExpectation.prepare( st );
 
 							int loc = writeKey( st, id, offset, session );
 							if ( hasIndex && !indexContainsFormula ) {
 								loc = writeIndexToWhere( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 
 							writeElementToWhere( st, collection.getElement( entry ), loc, session );
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().getBatch( insertRowBatchKey ).addToBatch();
 							}
 							else {
 								insertExpectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 							count++;
 						}
 						i++;
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
 			}
 
 			return count;
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not update collection rows: " + 
 					MessageHelper.collectionInfoString( this, collection, id, session ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	public String selectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String entitySuffix,
 	        String collectionSuffix,
 	        boolean includeCollectionColumns) {
 		StringBuilder buf = new StringBuilder();
 		if ( includeCollectionColumns ) {
 //			buf.append( selectFragment( lhsAlias, "" ) )//ignore suffix for collection columns!
 			buf.append( selectFragment( lhsAlias, collectionSuffix ) )
 					.append( ", " );
 		}
 		OuterJoinLoadable ojl = ( OuterJoinLoadable ) getElementPersister();
 		return buf.append( ojl.selectFragment( lhsAlias, entitySuffix ) )//use suffix for the entity columns
 				.toString();
 	}
 
 	/**
 	 * Create the <tt>OneToManyLoader</tt>
 	 *
 	 * @see org.hibernate.loader.collection.OneToManyLoader
 	 */
 	@Override
     protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException {
 		return BatchingCollectionInitializerBuilder.getBuilder( getFactory() )
 				.createBatchingOneToManyInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
 	}
 
-	public String fromJoinFragment(String alias,
-								   boolean innerJoin,
-								   boolean includeSubclasses) {
-		return ( ( Joinable ) getElementPersister() ).fromJoinFragment( alias, innerJoin, includeSubclasses );
+	@Override
+	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
+		return ( (Joinable) getElementPersister() ).fromJoinFragment( alias, innerJoin, includeSubclasses );
+	}
+
+	@Override
+	public String fromJoinFragment(
+			String alias,
+			boolean innerJoin,
+			boolean includeSubclasses,
+			Set<String> treatAsDeclarations) {
+		return ( (Joinable) getElementPersister() ).fromJoinFragment( alias, innerJoin, includeSubclasses, treatAsDeclarations );
 	}
 
-	public String whereJoinFragment(String alias,
-									boolean innerJoin,
-									boolean includeSubclasses) {
-		return ( ( Joinable ) getElementPersister() ).whereJoinFragment( alias, innerJoin, includeSubclasses );
+	@Override
+	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
+		return ( (Joinable) getElementPersister() ).whereJoinFragment( alias, innerJoin, includeSubclasses );
+	}
+
+	@Override
+	public String whereJoinFragment(
+			String alias,
+			boolean innerJoin,
+			boolean includeSubclasses,
+			Set<String> treatAsDeclarations) {
+		return ( (Joinable) getElementPersister() ).whereJoinFragment( alias, innerJoin, includeSubclasses, treatAsDeclarations );
 	}
 
 	@Override
     public String getTableName() {
-		return ( ( Joinable ) getElementPersister() ).getTableName();
+		return ( (Joinable) getElementPersister() ).getTableName();
 	}
 
 	@Override
     public String filterFragment(String alias) throws MappingException {
 		String result = super.filterFragment( alias );
 		if ( getElementPersister() instanceof Joinable ) {
 			result += ( ( Joinable ) getElementPersister() ).oneToManyFilterFragment( alias );
 		}
 		return result;
 
 	}
 
 	@Override
+	protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
+		String result = super.filterFragment( alias );
+		if ( getElementPersister() instanceof Joinable ) {
+			result += ( ( Joinable ) getElementPersister() ).oneToManyFilterFragment( alias, treatAsDeclarations );
+		}
+		return result;
+	}
+
+	@Override
     protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session) {
 		return new SubselectOneToManyLoader( 
 				this,
 				subselect.toSubselectString( getCollectionType().getLHSPropertyName() ),
 				subselect.getResult(),
 				subselect.getQueryParameters(),
 				subselect.getNamedParameterLocMap(),
 				session.getFactory(),
 				session.getLoadQueryInfluencers()
 			);
 	}
 
 	@Override
     public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		return new CollectionElementLoader( this, getFactory(), session.getLoadQueryInfluencers() )
 				.loadElement( session, key, incrementIndexByBase(index) );
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return getElementPersister().getFilterAliasGenerator(rootAlias);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 4d37951475..b64765509f 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1320 +1,1322 @@
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
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
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
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
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
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
 import org.hibernate.tuple.InMemoryValueGenerationStrategy;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.ValueGeneration;
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
 
 	private final Set<String> affectingFetchProfileNames = new HashSet<String>();
 
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
 
+	protected abstract String filterFragment(String alias, Set<String> treatAsDeclarations);
+
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
 			propertyDefinedOnSubclass[j++] = (Boolean) iter.next();
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
 
 	public boolean canUseReferenceCacheEntries() {
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
@@ -2700,2063 +2702,2153 @@ public abstract class AbstractEntityPersister
 					else if ( includeProperty[i] ) {
 						insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 					}
 				}
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns(
 						getPropertyColumnNames(i),
 						propertyColumnInsertable[i],
 						propertyColumnWriters[i]
 				);
 			}
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
 			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties except lobs
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) && !lobProperties.contains( i ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// HHH-4635 & HHH-8103
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session,
 			boolean isUpdate) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1, isUpdate );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final boolean[][] includeColumns,
 	        final int j,
 	        final PreparedStatement ps,
 	        final SessionImplementor session,
 	        int index,
 	        boolean isUpdate ) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Dehydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j )
 					&& !lobProperties.contains( i )) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( !isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 		
 		// HHH-4635
 		// Oracle expects all Lob properties to be last in inserts
 		// and updates.  Insert them at the end.
 		for ( int i : lobProperties ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 		
 		if ( isUpdate ) {
 			index += dehydrateId( id, rowId, ps, session, index );
 		}
 
 		return index;
 
 	}
 	
 	private int dehydrateId( 
 			final Serializable id,
 			final Object rowId,
 			final PreparedStatement ps,
 	        final SessionImplementor session,
 			int index ) throws SQLException {
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			return 1;
 		} else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			return getIdentifierColumnSpan();
 		}
 		return 0;
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
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Version: {0}", version );
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( traceEnabled ) {
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
 
 		// apply any pre-update in-memory value generation
 		if ( getEntityMetamodel().hasPreUpdateGeneratedValues() ) {
 			final InMemoryValueGenerationStrategy[] strategies = getEntityMetamodel().getInMemoryValueGenerationStrategies();
 			for ( int i = 0; i < strategies.length; i++ ) {
 				if ( strategies[i] != null && strategies[i].getGenerationTiming().includesUpdate() ) {
 					fields[i] = strategies[i].getValueGenerator().generateValue( session, object );
 					setPropertyValue( object, i, fields[i] );
 					// todo : probably best to add to dirtyFields if not-null
 				}
 			}
 		}
 
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
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		// apply any pre-insert in-memory value generation
 		if ( getEntityMetamodel().hasPreInsertGeneratedValues() ) {
 			final InMemoryValueGenerationStrategy[] strategies = getEntityMetamodel().getInMemoryValueGenerationStrategies();
 			for ( int i = 0; i < strategies.length; i++ ) {
 				if ( strategies[i] != null && strategies[i].getGenerationTiming().includesInsert() ) {
 					fields[i] = strategies[i].getValueGenerator().generateValue( session, object );
 					setPropertyValue( object, i, fields[i] );
 				}
 			}
 		}
 
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
 
+	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
+	@Override
+	public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) {
+		final StringBuilder sessionFilterFragment = new StringBuilder();
+		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
+		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
+	}
+
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
+	@Override
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
+		return oneToManyFilterFragment( alias );
+	}
+
+	@Override
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
-		return getSubclassTableSpan() == 1 ?
-				"" : //just a performance opt!
-				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
+		// NOTE : Not calling createJoin here is just a performance optimization
+		return getSubclassTableSpan() == 1
+				? ""
+				: createJoin( alias, innerJoin, includeSubclasses, Collections.<String>emptySet() ).toFromFragmentString();
 	}
 
+	@Override
+	public String fromJoinFragment(
+			String alias,
+			boolean innerJoin,
+			boolean includeSubclasses,
+			Set<String> treatAsDeclarations) {
+		// NOTE : Not calling createJoin here is just a performance optimization
+		return getSubclassTableSpan() == 1
+				? ""
+				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toFromFragmentString();
+	}
+
+	@Override
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
-		return getSubclassTableSpan() == 1 ?
-				"" : //just a performance opt!
-				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
+		// NOTE : Not calling createJoin here is just a performance optimization
+		return getSubclassTableSpan() == 1
+				? ""
+				: createJoin( alias, innerJoin, includeSubclasses, Collections.<String>emptySet() ).toWhereFragmentString();
+	}
+
+	@Override
+	public String whereJoinFragment(
+			String alias,
+			boolean innerJoin,
+			boolean includeSubclasses,
+			Set<String> treatAsDeclarations) {
+		// NOTE : Not calling createJoin here is just a performance optimization
+		return getSubclassTableSpan() == 1
+				? ""
+				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
-	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
-		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
+	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
+		// IMPL NOTE : all joins join to the pk of the driving table
+		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() );
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
-		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
-			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
-					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
-			if ( joinIsIncluded ) {
-				join.addJoin( getSubclassTableName( j ),
+		// IMPL NOTE : notice that we skip the first table; it is the driving table!
+		for ( int j = 1; j < tableSpan; j++ ) {
+			final JoinType joinType = determineSubclassTableJoinType(
+					j,
+					innerJoin,
+					includeSubclasses,
+					treatAsDeclarations
+			);
+
+			if ( joinType != null && joinType != JoinType.NONE ) {
+				join.addJoin(
+						getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
-						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
-						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
-						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
-					);
+						joinType
+				);
 			}
 		}
 		return join;
 	}
 
+	protected JoinType determineSubclassTableJoinType(
+			int subclassTableNumber,
+			boolean canInnerJoin,
+			boolean includeSubclasses,
+			Set<String> treatAsDeclarations) {
+
+		if ( isClassOrSuperclassTable( subclassTableNumber ) ) {
+			final boolean shouldInnerJoin = canInnerJoin
+					&& !isInverseTable( subclassTableNumber )
+					&& !isNullableTable( subclassTableNumber );
+			// the table is either this persister's driving table or (one of) its super class persister's driving
+			// tables which can be inner joined as long as the `shouldInnerJoin` condition resolves to true
+			return shouldInnerJoin ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN;
+		}
+
+		// otherwise we have a subclass table and need to look a little deeper...
+
+		// IMPL NOTE : By default includeSubclasses indicates that all subclasses should be joined and that each
+		// subclass ought to be joined by outer-join.  However, TREAT-AS always requires that an inner-join be used
+		// so we give TREAT-AS higher precedence...
+
+		if ( isSubclassTableIndicatedByTreatAsDeclarations( subclassTableNumber, treatAsDeclarations ) ) {
+			return JoinType.INNER_JOIN;
+		}
+
+		if ( includeSubclasses
+				&& !isSubclassTableSequentialSelect( subclassTableNumber )
+				&& !isSubclassTableLazy( subclassTableNumber ) ) {
+			return JoinType.LEFT_OUTER_JOIN;
+		}
+
+		return JoinType.NONE;
+	}
+
+	protected boolean isSubclassTableIndicatedByTreatAsDeclarations(
+			int subclassTableNumber,
+			Set<String> treatAsDeclarations) {
+		return false;
+	}
+
+
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
-		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
+		// IMPL NOTE : notice that we skip the first table; it is the driving table!
+		for ( int i = 1; i < tableNumbers.length; i++ ) {
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
-					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
-					JoinType.LEFT_OUTER_JOIN :
-					JoinType.INNER_JOIN );
+					isInverseSubclassTable( j ) || isNullableSubclassTable( j )
+							? JoinType.LEFT_OUTER_JOIN
+							: JoinType.INNER_JOIN
+			);
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
 
 	/**
 	 * Post-construct is a callback for AbstractEntityPersister subclasses to call after they are all done with their
 	 * constructor processing.  It allows AbstractEntityPersister to extend its construction after all subclass-specific
 	 * details have been handled.
 	 *
 	 * @param mapping The mapping
 	 *
 	 * @throws MappingException Indicates a problem accessing the Mapping
 	 */
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths( mapping );
 
 		//doLateInit();
 		prepareEntityIdentifierDefinition();
 	}
 
 	private void doLateInit() {
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
 
 	public final void postInstantiate() throws MappingException {
 		doLateInit();
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 		doPostInstantiate();
 	}
 
 	protected void doPostInstantiate() {
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
 
 	private boolean isAffectedByEntityGraph(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().getFetchGraph() != null || session.getLoadQueryInfluencers()
 				.getLoadGraph() != null;
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		for ( String s : session.getLoadQueryInfluencers().getEnabledFetchProfileNames() ) {
 			if ( affectingFetchProfileNames.contains( s ) ) {
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
 		else if ( isAffectedByEntityGraph( session ) ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
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
 		return isVersioned() && getEntityMetamodel().isVersionGenerated();
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
 
 	@Deprecated
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return null;
 	}
 
 	@Deprecated
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return null;
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
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, GenerationTiming.INSERT );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, GenerationTiming.ALWAYS );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 			GenerationTiming matchTiming) {
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
 					int propertyIndex = -1;
 					for ( NonIdentifierAttribute attribute : entityMetamodel.getProperties() ) {
 						propertyIndex++;
 						final ValueGeneration valueGeneration = attribute.getValueGenerationStrategy();
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/Joinable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Joinable.java
index ef92fad99d..2d6eff2f22 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/Joinable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/Joinable.java
@@ -1,93 +1,117 @@
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
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.MappingException;
 
 /**
  * Anything that can be loaded by outer join - namely
  * persisters for classes or collections.
  *
  * @author Gavin King
  */
 public interface Joinable {
 	//should this interface extend PropertyMapping?
 
 	/**
 	 * An identifying name; a class name or collection role name.
 	 */
 	public String getName();
 	/**
 	 * The table to join to.
 	 */
 	public String getTableName();
 
 	/**
 	 * All columns to select, when loading.
 	 */
 	public String selectFragment(Joinable rhs, String rhsAlias, String lhsAlias, String currentEntitySuffix, String currentCollectionSuffix, boolean includeCollectionColumns);
 
 	/**
 	 * Get the where clause part of any joins
 	 * (optional operation)
 	 */
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses);
+
+	/**
+	 * Get the where clause part of any joins
+	 * (optional operation)
+	 */
+	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations);
+
 	/**
 	 * Get the from clause part of any joins
 	 * (optional operation)
 	 */
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses);
+
+	/**
+	 * Get the from clause part of any joins
+	 * (optional operation)
+	 */
+	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations);
+
 	/**
 	 * The columns to join on
 	 */
 	public String[] getKeyColumnNames();
+
 	/**
 	 * Get the where clause filter, given a query alias and considering enabled session filters
 	 */
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException;
 
+	/**
+	 * Get the where clause filter, given a query alias and considering enabled session filters
+	 */
+	public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) throws MappingException;
+
 	public String oneToManyFilterFragment(String alias) throws MappingException;
+
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations);
+
 	/**
 	 * Is this instance actually a CollectionPersister?
 	 */
 	public boolean isCollection();
 
 	/**
 	 * Very, very, very ugly...
 	 *
 	 * @return Does this persister "consume" entity column aliases in the result
 	 * set?
 	 */
 	public boolean consumesEntityAlias();
 
 	/**
 	 * Very, very, very ugly...
 	 *
 	 * @return Does this persister "consume" collection column aliases in the result
 	 * set?
 	 */
 	public boolean consumesCollectionAlias();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
index 04c388e400..803fc63c1e 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
@@ -1,862 +1,1065 @@
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
+import java.lang.reflect.Array;
 import java.util.ArrayList;
+import java.util.Arrays;
 import java.util.HashMap;
+import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.DynamicFilterAliasGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
+import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
  * mapping strategy
  *
  * @author Gavin King
  */
 public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int tableSpan;
 	private final String[] tableNames;
 	private final String[] naturalOrderTableNames;
 	private final String[][] tableKeyColumns;
 	private final String[][] tableKeyColumnReaders;
 	private final String[][] tableKeyColumnReaderTemplates;
 	private final String[][] naturalOrderTableKeyColumns;
 	private final String[][] naturalOrderTableKeyColumnReaders;
 	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
 	private final boolean[] naturalOrderCascadeDeleteEnabled;
 
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] naturalOrderPropertyTableNumbers;
 	private final int[] propertyTableNumbers;
 
 	// the closure of all properties in the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	private final boolean[] subclassTableSequentialSelect;
 	private final boolean[] subclassTableIsLazyClosure;
 
 	// subclass discrimination works by assigning particular
 	// values to certain combinations of null primary key
 	// values in the outer join using an SQL CASE
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final String[] discriminatorValues;
 	private final String[] notNullColumnNames;
 	private final int[] notNullColumnTableNumbers;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	private final Object discriminatorValue;
 	private final String discriminatorSQLString;
 
 	// Span of the tables directly mapped by this entity and super-classes, if any
 	private final int coreTableSpan;
 	// only contains values for SecondaryTables, ie. not tables part of the "coreTableSpan"
 	private final boolean[] isNullableTable;
 
 	//INITIALIZATION:
 
 	public JoinedSubclassEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory );
 
 		// DISCRIMINATOR
 
 		if ( persistentClass.isPolymorphic() ) {
 			try {
 				discriminatorValue = persistentClass.getSubclassId();
 				discriminatorSQLString = discriminatorValue.toString();
 			}
 			catch ( Exception e ) {
 				throw new MappingException( "Could not format discriminator value to SQL string", e );
 			}
 		}
 		else {
 			discriminatorValue = null;
 			discriminatorSQLString = null;
 		}
 
 		if ( optimisticLockStyle() == OptimisticLockStyle.ALL || optimisticLockStyle() == OptimisticLockStyle.DIRTY ) {
 			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
 		}
 
 		//MULTITABLES
 
 		final int idColumnSpan = getIdentifierColumnSpan();
 
 		ArrayList tables = new ArrayList();
 		ArrayList keyColumns = new ArrayList();
 		ArrayList keyColumnReaders = new ArrayList();
 		ArrayList keyColumnReaderTemplates = new ArrayList();
 		ArrayList cascadeDeletes = new ArrayList();
 		Iterator titer = persistentClass.getTableClosureIterator();
 		Iterator kiter = persistentClass.getKeyClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			KeyValue key = (KeyValue) kiter.next();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add( tabname );
 			String[] keyCols = new String[idColumnSpan];
 			String[] keyColReaders = new String[idColumnSpan];
 			String[] keyColReaderTemplates = new String[idColumnSpan];
 			Iterator citer = key.getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
 		//Span of the tables directly mapped by this entity and super-classes, if any
 		coreTableSpan = tables.size();
 
 		isNullableTable = new boolean[persistentClass.getJoinClosureSpan()];
 
 		int tableIndex = 0;
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
 			isNullableTable[tableIndex++] = join.isOptional();
 
 			Table table = join.getTable();
 
 			String tableName = table.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add( tableName );
 
 			KeyValue key = join.getKey();
 			int joinIdColumnSpan = key.getColumnSpan();
 
 			String[] keyCols = new String[joinIdColumnSpan];
 			String[] keyColReaders = new String[joinIdColumnSpan];
 			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
 
 			Iterator citer = key.getColumnIterator();
 
 			for ( int k = 0; k < joinIdColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
 		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
 		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray( keyColumns );
 		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray( keyColumnReaders );
 		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray( keyColumnReaderTemplates );
 		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray( cascadeDeletes );
 
 		ArrayList subtables = new ArrayList();
 		ArrayList isConcretes = new ArrayList();
 		ArrayList isDeferreds = new ArrayList();
 		ArrayList isLazies = new ArrayList();
 
 		keyColumns = new ArrayList();
 		titer = persistentClass.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
 			isDeferreds.add( Boolean.FALSE );
 			isLazies.add( Boolean.FALSE );
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add( tabname );
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
 		//Add joins
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
 			Table tab = join.getTable();
 
 			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isLazies.add( join.isLazy() );
 
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add( tabname );
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
 		String[] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray( subtables );
 		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( keyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray( isConcretes );
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray( isDeferreds );
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray( isLazies );
 
 		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
 		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
 		int currentPosition = 0;
 		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0; i--, currentPosition++ ) {
 			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
 			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
 		}
 
 		/**
 		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
 		 * For the Client entity:
 		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
 		 * added to the meta-data when the annotated entities are processed.
 		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
 		 * the first table as it will the driving table.
 		 * tableNames -> CLIENT, PERSON
 		 */
 
 		tableSpan = naturalOrderTableNames.length;
 		tableNames = reverse( naturalOrderTableNames, coreTableSpan );
 		tableKeyColumns = reverse( naturalOrderTableKeyColumns, coreTableSpan );
 		tableKeyColumnReaders = reverse( naturalOrderTableKeyColumnReaders, coreTableSpan );
 		tableKeyColumnReaderTemplates = reverse( naturalOrderTableKeyColumnReaderTemplates, coreTableSpan );
 		subclassTableNameClosure = reverse( naturalOrderSubclassTableNameClosure, coreTableSpan );
 		subclassTableKeyColumnClosure = reverse( naturalOrderSubclassTableKeyColumnClosure, coreTableSpan );
 
 		spaces = ArrayHelper.join(
 				tableNames,
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 
 		// Custom sql
 		customSQLInsert = new String[tableSpan];
 		customSQLUpdate = new String[tableSpan];
 		customSQLDelete = new String[tableSpan];
 		insertCallable = new boolean[tableSpan];
 		updateCallable = new boolean[tableSpan];
 		deleteCallable = new boolean[tableSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 
 		PersistentClass pc = persistentClass;
 		int jk = coreTableSpan - 1;
 		while ( pc != null ) {
 			customSQLInsert[jk] = pc.getCustomSQLInsert();
 			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
 			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault(
 					customSQLInsert[jk], insertCallable[jk]
 			)
 					: pc.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
 			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
 			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
 					: pc.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[jk] = pc.getCustomSQLDelete();
 			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
 			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
 					: pc.getCustomSQLDeleteCheckStyle();
 			jk--;
 			pc = pc.getSuperclass();
 		}
 
 		if ( jk != -1 ) {
 			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
 		}
 
 		joinIter = persistentClass.getJoinClosureIterator();
 		int j = coreTableSpan;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
 			customSQLInsert[j] = join.getCustomSQLInsert();
 			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
 			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
 					: join.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[j] = join.getCustomSQLUpdate();
 			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
 			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
 					: join.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[j] = join.getCustomSQLDelete();
 			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
 			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
 					: join.getCustomSQLDeleteCheckStyle();
 			j++;
 		}
 
 		// PROPERTIES
 		int hydrateSpan = getPropertySpan();
 		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
 		propertyTableNumbers = new int[hydrateSpan];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			String tabname = prop.getValue().getTable().getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			propertyTableNumbers[i] = getTableId( tabname, tableNames );
 			naturalOrderPropertyTableNumbers[i] = getTableId( tabname, naturalOrderTableNames );
 			i++;
 		}
 
 		// subclass closure properties
 
 		//TODO: code duplication with SingleTableEntityPersister
 
 		ArrayList columnTableNumbers = new ArrayList();
 		ArrayList formulaTableNumbers = new ArrayList();
 		ArrayList propTableNumbers = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Table tab = prop.getValue().getTable();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			Integer tabnum = getTableId( tabname, subclassTableNameClosure );
 			propTableNumbers.add( tabnum );
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaTableNumbers.add( tabnum );
 				}
 				else {
 					columnTableNumbers.add( tabnum );
 				}
 			}
 
 		}
 
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray( columnTableNumbers );
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray( propTableNumbers );
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray( formulaTableNumbers );
 
 		// SUBCLASSES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[subclassSpan - 1] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 			discriminatorValues = new String[subclassSpan];
 			discriminatorValues[subclassSpan - 1] = discriminatorSQLString;
 			notNullColumnTableNumbers = new int[subclassSpan];
 			final int id = getTableId(
 					persistentClass.getTable().getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					),
 					subclassTableNameClosure
 			);
 			notNullColumnTableNumbers[subclassSpan - 1] = id;
 			notNullColumnNames = new String[subclassSpan];
 			notNullColumnNames[subclassSpan - 1] = subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 		}
 		else {
 			discriminatorValues = null;
 			notNullColumnTableNumbers = null;
 			notNullColumnNames = null;
 		}
 
 		iter = persistentClass.getSubclassIterator();
 		int k = 0;
 		while ( iter.hasNext() ) {
 			Subclass sc = (Subclass) iter.next();
 			subclassClosure[k] = sc.getEntityName();
 			try {
 				if ( persistentClass.isPolymorphic() ) {
 					// we now use subclass ids that are consistent across all
 					// persisters for a class hierarchy, so that the use of
 					// "foo.class = Bar" works in HQL
 					Integer subclassId = sc.getSubclassId();
 					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
 					discriminatorValues[k] = subclassId.toString();
 					int id = getTableId(
 							sc.getTable().getQualifiedName(
 									factory.getDialect(),
 									factory.getSettings().getDefaultCatalogName(),
 									factory.getSettings().getDefaultSchemaName()
 							),
 							subclassTableNameClosure
 					);
 					notNullColumnTableNumbers[k] = id;
 					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 				}
 			}
 			catch ( Exception e ) {
 				throw new MappingException( "Error parsing discriminator value", e );
 			}
 			k++;
 		}
 
+		subclassNamesBySubclassTable = buildSubclassNamesBySubclassTableMapping( persistentClass, factory );
+
 		initLockers();
 
 		initSubclassPropertyAliasesMap( persistentClass );
 
 		postConstruct( mapping );
 
 	}
 
+
+	/**
+	 * Used to hold the name of subclasses that each "subclass table" is part of.  For example, given a hierarchy like:
+	 * {@code JoinedEntity <- JoinedEntitySubclass <- JoinedEntitySubSubclass}..
+	 * <p/>
+	 * For the persister for JoinedEntity, we'd have:
+	 * <pre>
+	 *     subclassClosure[0] = "JoinedEntitySubSubclass"
+	 *     subclassClosure[1] = "JoinedEntitySubclass"
+	 *     subclassClosure[2] = "JoinedEntity"
+	 *
+	 *     subclassTableNameClosure[0] = "T_JoinedEntity"
+	 *     subclassTableNameClosure[1] = "T_JoinedEntitySubclass"
+	 *     subclassTableNameClosure[2] = "T_JoinedEntitySubSubclass"
+	 *
+	 *     subclassNameClosureBySubclassTable[0] = ["JoinedEntitySubSubclass", "JoinedEntitySubclass"]
+	 *     subclassNameClosureBySubclassTable[1] = ["JoinedEntitySubSubclass"]
+	 * </pre>
+	 * Note that there are only 2 entries in subclassNameClosureBySubclassTable.  That is because there are really only
+	 * 2 tables here that make up the subclass mapping, the others make up the class/superclass table mappings.  We
+	 * do not need to account for those here.  The "offset" is defined by the value of {@link #getTableSpan()}.
+	 * Therefore the corresponding row in subclassNameClosureBySubclassTable for a given row in subclassTableNameClosure
+	 * is calculated as {@code subclassTableNameClosureIndex - getTableSpan()}.
+	 * <p/>
+	 * As we consider each subclass table we can look into this array based on the subclass table's index and see
+	 * which subclasses would require it to be included.  E.g., given {@code TREAT( x AS JoinedEntitySubSubclass )},
+	 * when trying to decide whether to include join to "T_JoinedEntitySubclass" (subclassTableNameClosureIndex = 1),
+	 * we'd look at {@code subclassNameClosureBySubclassTable[0]} and see if the TREAT-AS subclass name is included in
+	 * its values.  Since {@code subclassNameClosureBySubclassTable[1]} includes "JoinedEntitySubSubclass", we'd
+	 * consider it included.
+	 * <p/>
+	 * {@link #subclassTableNameClosure} also accounts for secondary tables and we properly handle those as we
+	 * build the subclassNamesBySubclassTable array and they are therefore properly handled when we use it
+	 */
+	private final String[][] subclassNamesBySubclassTable;
+
+	/**
+	 * Essentially we are building a mapping that we can later use to determine whether a given "subclass table"
+	 * should be included in joins when JPA TREAT-AS is used.
+	 *
+	 * @param persistentClass
+	 * @param factory
+	 * @return
+	 */
+	private String[][] buildSubclassNamesBySubclassTableMapping(PersistentClass persistentClass, SessionFactoryImplementor factory) {
+		// this value represents the number of subclasses (and not the class itself)
+		final int numberOfSubclassTables = subclassTableNameClosure.length - coreTableSpan;
+		if ( numberOfSubclassTables == 0 ) {
+			return new String[0][];
+		}
+
+		final String[][] mapping = new String[numberOfSubclassTables][];
+		processPersistentClassHierarchy( persistentClass, true, factory, mapping );
+		return mapping;
+	}
+
+	private Set<String> processPersistentClassHierarchy(
+			PersistentClass persistentClass,
+			boolean isBase,
+			SessionFactoryImplementor factory,
+			String[][] mapping) {
+
+		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		// collect all the class names that indicate that the "main table" of the given PersistentClass should be
+		// included when one of the collected class names is used in TREAT
+		final Set<String> classNames = new HashSet<String>();
+
+		final Iterator itr = persistentClass.getDirectSubclasses();
+		while ( itr.hasNext() ) {
+			final Subclass subclass = (Subclass) itr.next();
+			final Set<String> subclassSubclassNames = processPersistentClassHierarchy(
+					subclass,
+					false,
+					factory,
+					mapping
+			);
+			classNames.addAll( subclassSubclassNames );
+		}
+
+		classNames.add( persistentClass.getEntityName() );
+
+		if ( ! isBase ) {
+			MappedSuperclass msc = persistentClass.getSuperMappedSuperclass();
+			while ( msc != null ) {
+				classNames.add( msc.getMappedClass().getName() );
+				msc = msc.getSuperMappedSuperclass();
+			}
+
+			associateSubclassNamesToSubclassTableIndexes( persistentClass, classNames, mapping, factory );
+		}
+
+		return classNames;
+	}
+
+	private void associateSubclassNamesToSubclassTableIndexes(
+			PersistentClass persistentClass,
+			Set<String> classNames,
+			String[][] mapping,
+			SessionFactoryImplementor factory) {
+
+		final String tableName = persistentClass.getTable().getQualifiedName(
+				factory.getDialect(),
+				factory.getSettings().getDefaultCatalogName(),
+				factory.getSettings().getDefaultSchemaName()
+		);
+
+		associateSubclassNamesToSubclassTableIndex( tableName, classNames, mapping );
+
+		Iterator itr = persistentClass.getJoinIterator();
+		while ( itr.hasNext() ) {
+			final Join join = (Join) itr.next();
+			final String secondaryTableName = join.getTable().getQualifiedName(
+					factory.getDialect(),
+					factory.getSettings().getDefaultCatalogName(),
+					factory.getSettings().getDefaultSchemaName()
+			);
+			associateSubclassNamesToSubclassTableIndex( secondaryTableName, classNames, mapping );
+		}
+	}
+
+	private void associateSubclassNamesToSubclassTableIndex(
+			String tableName,
+			Set<String> classNames,
+			String[][] mapping) {
+		// find the table's entry in the subclassTableNameClosure array
+		boolean found = false;
+		for ( int i = 0; i < subclassTableNameClosure.length; i++ ) {
+			if ( subclassTableNameClosure[i].equals( tableName ) ) {
+				found = true;
+				final int index = i - coreTableSpan;
+				if ( index < 0 || index >= mapping.length ) {
+					throw new IllegalStateException(
+							String.format(
+									"Encountered 'subclass table index' [%s] was outside expected range ( [%s] < i < [%s] )",
+									index,
+									0,
+									mapping.length
+							)
+					);
+				}
+				mapping[index] = classNames.toArray( new String[ classNames.size() ] );
+				break;
+			}
+		}
+		if ( !found ) {
+			throw new IllegalStateException(
+					String.format(
+							"Was unable to locate subclass table [%s] in 'subclassTableNameClosure'",
+							tableName
+					)
+			);
+		}
+	}
+
 	public JoinedSubclassEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 		super( entityBinding, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory );
 		// TODO: implement!!! initializing final fields to null to make compiler happy
 		tableSpan = -1;
 		tableNames = null;
 		naturalOrderTableNames = null;
 		tableKeyColumns = null;
 		tableKeyColumnReaders = null;
 		tableKeyColumnReaderTemplates = null;
 		naturalOrderTableKeyColumns = null;
 		naturalOrderTableKeyColumnReaders = null;
 		naturalOrderTableKeyColumnReaderTemplates = null;
 		naturalOrderCascadeDeleteEnabled = null;
 		spaces = null;
 		subclassClosure = null;
 		subclassTableNameClosure = null;
 		subclassTableKeyColumnClosure = null;
 		isClassOrSuperclassTable = null;
 		naturalOrderPropertyTableNumbers = null;
 		propertyTableNumbers = null;
 		subclassPropertyTableNumberClosure = null;
 		subclassColumnTableNumberClosure = null;
 		subclassFormulaTableNumberClosure = null;
 		subclassTableSequentialSelect = null;
 		subclassTableIsLazyClosure = null;
 		discriminatorValues = null;
 		notNullColumnNames = null;
 		notNullColumnTableNumbers = null;
 		constraintOrderedTableNames = null;
 		constraintOrderedKeyColumnNames = null;
 		discriminatorValue = null;
 		discriminatorSQLString = null;
 		coreTableSpan = -1;
 		isNullableTable = null;
+		subclassNamesBySubclassTable = null;
 	}
 
 	protected boolean isNullableTable(int j) {
 		if ( j < coreTableSpan ) {
 			return false;
 		}
 		return isNullableTable[j - coreTableSpan];
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 
 	/*public void postInstantiate() throws MappingException {
 		super.postInstantiate();
 		//TODO: other lock modes?
 		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
 	}*/
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[subclassPropertyTableNumberClosure[i]];
 	}
 
 	public Type getDiscriminatorType() {
 		return StandardBasicTypes.INTEGER;
 	}
 
 	public Object getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLString;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassesByDiscriminatorValue.get( value );
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces; // don't need subclass tables, because they can't appear in conditions
 	}
 
 
 	protected String getTableName(int j) {
 		return naturalOrderTableNames[j];
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return naturalOrderTableKeyColumns[j];
 	}
 
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return naturalOrderCascadeDeleteEnabled[j];
 	}
 
 	protected boolean isPropertyOfTable(int property, int j) {
 		return naturalOrderPropertyTableNumbers[property] == j;
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException {
 
 		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
 
 		final UniqueEntityLoader loader = hasQueryLoader() ?
 				getQueryLoader() :
 				this.loader;
 		try {
 
 			final Object result = loader.load(id, optionalObject, session);
 
 			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
 			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
 		}
 	}*/
 	private static final void reverse(Object[] objects, int len) {
 		Object[] temp = new Object[len];
 		for ( int i = 0; i < len; i++ ) {
 			temp[i] = objects[len - i - 1];
 		}
 		for ( int i = 0; i < len; i++ ) {
 			objects[i] = temp[i];
 		}
 	}
 
 
 	/**
 	 * Reverse the first n elements of the incoming array
 	 *
 	 * @param objects
 	 * @param n
 	 *
 	 * @return New array with the first n elements in reversed order
 	 */
 	private static String[] reverse(String[] objects, int n) {
 
 		int size = objects.length;
 		String[] temp = new String[size];
 
 		for ( int i = 0; i < n; i++ ) {
 			temp[i] = objects[n - i - 1];
 		}
 
 		for ( int i = n; i < size; i++ ) {
 			temp[i] = objects[i];
 		}
 
 		return temp;
 	}
 
 	/**
 	 * Reverse the first n elements of the incoming array
 	 *
 	 * @param objects
 	 * @param n
 	 *
 	 * @return New array with the first n elements in reversed order
 	 */
 	private static String[][] reverse(String[][] objects, int n) {
 		int size = objects.length;
 		String[][] temp = new String[size][];
 		for ( int i = 0; i < n; i++ ) {
 			temp[i] = objects[n - i - 1];
 		}
 
 		for ( int i = n; i < size; i++ ) {
 			temp[i] = objects[i];
 		}
 
 		return temp;
 	}
 
 
 	public String fromTableFragment(String alias) {
 		return getTableName() + ' ' + alias;
 	}
 
 	public String getTableName() {
 		return tableNames[0];
 	}
 
 	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( hasSubclasses() ) {
 			select.setExtraSelectList( discriminatorFragment( name ), getDiscriminatorAlias() );
 		}
 	}
 
 	private CaseFragment discriminatorFragment(String alias) {
 		CaseFragment cases = getFactory().getDialect().createCaseFragment();
 
 		for ( int i = 0; i < discriminatorValues.length; i++ ) {
 			cases.addWhenColumnNotNull(
 					generateTableAlias( alias, notNullColumnTableNumbers[i] ),
 					notNullColumnNames[i],
 					discriminatorValues[i]
 			);
 		}
 
 		return cases;
 	}
 
+	@Override
 	public String filterFragment(String alias) {
-		return hasWhere() ?
-				" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
-				"";
+		return hasWhere()
+				? " and " + getSQLWhereString( generateFilterConditionAlias( alias ) )
+				: "";
+	}
+
+	@Override
+	public String filterFragment(String alias, Set<String> treatAsDeclarations) {
+		return filterFragment( alias );
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return generateTableAlias( rootAlias, tableSpan - 1 );
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return tableKeyColumns[0];
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return tableKeyColumnReaderTemplates[0];
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return tableKeyColumnReaders[0];
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( ENTITY_CLASS.equals( propertyName ) ) {
 			// This doesn't actually seem to work but it *might*
 			// work on some dbs. Also it doesn't work if there
 			// are multiple columns of results because it
 			// is not accounting for the suffix:
 			// return new String[] { getDiscriminatorColumnName() };
 
 			return new String[] { discriminatorFragment( alias ).toFragmentString() };
 		}
 		else {
 			return super.toColumns( alias, propertyName );
 		}
 	}
 
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return tableSpan;
 	}
 
 	public boolean isMultiTable() {
 		return true;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return naturalOrderPropertyTableNumbers;
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		return subclassTableKeyColumnClosure[j];
 	}
 
 	public String getSubclassTableName(int j) {
 		return subclassTableNameClosure[j];
 	}
 
 	public int getSubclassTableSpan() {
 		return subclassTableNameClosure.length;
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
+	@Override
+	protected boolean isSubclassTableIndicatedByTreatAsDeclarations(
+			int subclassTableNumber,
+			Set<String> treatAsDeclarations) {
+		if ( treatAsDeclarations == null || treatAsDeclarations.isEmpty() ) {
+			return false;
+		}
+
+		final String[] inclusionSubclassNameClosure = getSubclassNameClosureBySubclassTable( subclassTableNumber );
+
+		// NOTE : we assume the entire hierarchy is joined-subclass here
+		for ( String subclassName : treatAsDeclarations ) {
+			for ( String inclusionSubclassName : inclusionSubclassNameClosure ) {
+				if ( inclusionSubclassName.equals( subclassName ) ) {
+					return true;
+				}
+			}
+		}
+
+		return false;
+	}
+
+	private String[] getSubclassNameClosureBySubclassTable(int subclassTableNumber) {
+		final int index = subclassTableNumber - getTableSpan();
+
+		if ( index > subclassNamesBySubclassTable.length ) {
+			throw new IllegalArgumentException(
+					"Given subclass table number is outside expected range [" + subclassNamesBySubclassTable.length
+							+ "] as defined by subclassTableNameClosure/subclassClosure"
+			);
+		}
+
+		return subclassNamesBySubclassTable[index];
+	}
+
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull( propertyName );
 		if ( index == null ) {
 			return null;
 		}
 		return tableNames[propertyTableNumbers[index]];
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 
 	public String getRootTableName() {
 		return naturalOrderTableNames[0];
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		if ( "class".equals( propertyPath ) ) {
 			// special case where we need to force include all subclass joins
 			return Declarer.SUBCLASS;
 		}
 		return super.getSubclassPropertyDeclarer( propertyPath );
 	}
 
 	@Override
 	public int determineTableNumberForColumn(String columnName) {
 		final String[] subclassColumnNameClosure = getSubclassColumnClosure();
 		for ( int i = 0, max = subclassColumnNameClosure.length; i < max; i++ ) {
 			final boolean quoted = subclassColumnNameClosure[i].startsWith( "\"" )
 					&& subclassColumnNameClosure[i].endsWith( "\"" );
 			if ( quoted ) {
 				if ( subclassColumnNameClosure[i].equals( columnName ) ) {
 					return getSubclassColumnTableNumberClosure()[i];
 				}
 			}
 			else {
 				if ( subclassColumnNameClosure[i].equalsIgnoreCase( columnName ) ) {
 					return getSubclassColumnTableNumberClosure()[i];
 				}
 			}
 		}
 		throw new HibernateException( "Could not locate table which owns column [" + columnName + "] referenced in order-by mapping" );
 	}
 
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new DynamicFilterAliasGenerator(subclassTableNameClosure, rootAlias);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index 8e107d60e7..8196261aaa 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,1056 +1,1105 @@
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
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
+import java.util.List;
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.DynamicFilterAliasGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.Type;
 
 /**
  * The default implementation of the <tt>EntityPersister</tt> interface.
  * Implements the "table-per-class-hierarchy" or "roll-up" mapping strategy
  * for an entity class and its inheritence hierarchy.  This is implemented
  * as a single table holding all classes in the hierarchy with a discrimator
  * column used to determine which concrete class is referenced.
  *
  * @author Gavin King
  */
 public class SingleTableEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int joinSpan;
 	private final String[] qualifiedTableNames;
 	private final boolean[] isInverseTable;
 	private final boolean[] isNullableTable;
 	private final String[][] keyColumnNames;
 	private final boolean[] cascadeDeleteEnabled;
 	private final boolean hasSequentialSelects;
 	
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final boolean[] subclassTableIsLazyClosure;
 	private final boolean[] isInverseSubclassTable;
 	private final boolean[] isNullableSubclassTable;
 	private final boolean[] subclassTableSequentialSelect;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] propertyTableNumbers;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	// discriminator column
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final boolean forceDiscriminator;
 	private final String discriminatorColumnName;
 	private final String discriminatorColumnReaders;
 	private final String discriminatorColumnReaderTemplate;
 	private final String discriminatorFormula;
 	private final String discriminatorFormulaTemplate;
 	private final String discriminatorAlias;
 	private final Type discriminatorType;
 	private final Object discriminatorValue;
 	private final String discriminatorSQLValue;
 	private final boolean discriminatorInsertable;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//private final Map propertyTableNumbersByName = new HashMap();
 	private final Map propertyTableNumbersByNameAndSubclass = new HashMap();
 	
 	private final Map sequentialSelectStringsByEntityName = new HashMap();
 
 	private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
 	private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
 	private static final String NULL_STRING = "null";
 	private static final String NOT_NULL_STRING = "not null";
 
 	//INITIALIZATION:
 
 	public SingleTableEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		joinSpan = persistentClass.getJoinClosureSpan()+1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 		final Table table = persistentClass.getRootTable();
 		qualifiedTableNames[0] = table.getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		customSQLInsert[0] = persistentClass.getCustomSQLInsert();
 		insertCallable[0] = customSQLInsert[0] != null && persistentClass.isCustomInsertCallable();
 		insertResultCheckStyles[0] = persistentClass.getCustomSQLInsertCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[0], insertCallable[0] )
 									  : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLUpdate[0] = persistentClass.getCustomSQLUpdate();
 		updateCallable[0] = customSQLUpdate[0] != null && persistentClass.isCustomUpdateCallable();
 		updateResultCheckStyles[0] = persistentClass.getCustomSQLUpdateCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[0], updateCallable[0] )
 									  : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLDelete[0] = persistentClass.getCustomSQLDelete();
 		deleteCallable[0] = customSQLDelete[0] != null && persistentClass.isCustomDeleteCallable();
 		deleteResultCheckStyles[0] = persistentClass.getCustomSQLDeleteCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[0], deleteCallable[0] )
 									  : persistentClass.getCustomSQLDeleteCheckStyle();
 
 		// JOINS
 
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		int j = 1;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			qualifiedTableNames[j] = join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			);
 			isInverseTable[j] = join.isInverse();
 			isNullableTable[j] = join.isOptional();
 			cascadeDeleteEnabled[j] = join.getKey().isCascadeDeleteEnabled() && 
 				factory.getDialect().supportsCascadeDelete();
 
 			customSQLInsert[j] = join.getCustomSQLInsert();
 			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
 			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
 		                                  : join.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[j] = join.getCustomSQLUpdate();
 			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
 			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
 		                                  : join.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[j] = join.getCustomSQLDelete();
 			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
 			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
 		                                  : join.getCustomSQLDeleteCheckStyle();
 
 			Iterator iter = join.getKey().getColumnIterator();
 			keyColumnNames[j] = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyColumnNames[j][i++] = col.getQuotedName( factory.getDialect() );
 			}
 
 			j++;
 		}
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames, 
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 		
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassJoin(join) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isInverses.add( join.isInverse() );
 			isNullables.add( join.isOptional() );
 			isLazies.add( lazyAvailable && join.isLazy() );
 			if ( join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin(join) ) hasDeferred = true;
 			subclassTables.add( join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 			Iterator iter = join.getKey().getColumnIterator();
 			String[] keyCols = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyCols[i++] = col.getQuotedName( factory.getDialect() );
 			}
 			joinKeyColumns.add(keyCols);
 		}
 		
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		if ( persistentClass.isPolymorphic() ) {
 			Value discrimValue = persistentClass.getDiscriminator();
 			if (discrimValue==null) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = persistentClass.isForceDiscriminator();
 			Selectable selectable = (Selectable) discrimValue.getColumnIterator().next();
 			if ( discrimValue.hasFormula() ) {
 				Formula formula = (Formula) selectable;
 				discriminatorFormula = formula.getFormula();
 				discriminatorFormulaTemplate = formula.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				Column column = (Column) selectable;
 				discriminatorColumnName = column.getQuotedName( factory.getDialect() );
 				discriminatorColumnReaders = column.getReadExpr( factory.getDialect() );
 				discriminatorColumnReaderTemplate = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorAlias = column.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 			discriminatorType = persistentClass.getDiscriminator().getType();
 			if ( persistentClass.isDiscriminatorValueNull() ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( persistentClass.isDiscriminatorValueNotNull() ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = persistentClass.isDiscriminatorInsertable() && !discrimValue.hasFormula();
 				try {
 					DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 					discriminatorValue = dtype.stringToObject( persistentClass.getDiscriminatorValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i=0;
 		while( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			propertyTableNumbers[i++] = persistentClass.getJoinNumber(prop);
 
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 		
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 		
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Integer join = persistentClass.getJoinNumber(prop);
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( prop.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put( 
 					prop.getPersistentClass().getEntityName() + '.' + prop.getName(), 
 					join 
 			);
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaJoinedNumbers.add(join);
 				}
 				else {
 					columnJoinNumbers.add(join);
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			addSubclassByDiscriminatorValue( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( persistentClass.isPolymorphic() ) {
 			iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				if ( sc.isDiscriminatorValueNull() ) {
 					addSubclassByDiscriminatorValue( NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else if ( sc.isDiscriminatorValueNotNull() ) {
 					addSubclassByDiscriminatorValue( NOT_NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						addSubclassByDiscriminatorValue(
 							dtype.stringToObject( sc.getDiscriminatorValue() ),
 							sc.getEntityName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException("Error parsing discriminator value", e);
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
 	private void addSubclassByDiscriminatorValue(Object discriminatorValue, String entityName) {
 		String mappedEntityName = (String) subclassesByDiscriminatorValue.put( discriminatorValue, entityName );
 		if ( mappedEntityName != null ) {
 			throw new MappingException(
 					"Entities [" + entityName + "] and [" + mappedEntityName
 							+ "] are mapped with the same discriminator value '" + discriminatorValue + "'."
 			);
 		}
 	}
 
 	public SingleTableEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( entityBinding, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		// TODO: fix when joins are working (HHH-6391)
 		//joinSpan = entityBinding.getJoinClosureSpan() + 1;
 		joinSpan = 1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 
 		final TableSpecification table = entityBinding.getPrimaryTable();
 		qualifiedTableNames[0] = table.getQualifiedName( factory.getDialect() );
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		initializeCustomSql( entityBinding.getCustomInsert(), 0, customSQLInsert, insertCallable, insertResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomUpdate(), 0, customSQLUpdate, updateCallable, updateResultCheckStyles );
 		initializeCustomSql( entityBinding.getCustomDelete(), 0, customSQLDelete, deleteCallable, deleteResultCheckStyles );
 
 		// JOINS
 
 		// TODO: add join stuff when HHH-6391 is working
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames,
 				ArrayHelper.toStringArray( entityBinding.getSynchronizedTableNames() )
 		);
 
 		final boolean lazyAvailable = isInstrumented();
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 
 		// TODO: add join stuff when HHH-6391 is working
 
 
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		if ( entityBinding.isPolymorphic() ) {
 			SimpleValue discriminatorRelationalValue = entityBinding.getHierarchyDetails().getEntityDiscriminator().getBoundValue();
 			if ( discriminatorRelationalValue == null ) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = entityBinding.getHierarchyDetails().getEntityDiscriminator().isForced();
 			if ( DerivedValue.class.isInstance( discriminatorRelationalValue ) ) {
 				DerivedValue formula = ( DerivedValue ) discriminatorRelationalValue;
 				discriminatorFormula = formula.getExpression();
 				discriminatorFormulaTemplate = getTemplateFromString( formula.getExpression(), factory );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				org.hibernate.metamodel.relational.Column column = ( org.hibernate.metamodel.relational.Column ) discriminatorRelationalValue;
 				discriminatorColumnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 				discriminatorColumnReaders =
 						column.getReadFragment() == null ?
 								column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() ) :
 								column.getReadFragment();
 				discriminatorColumnReaderTemplate = getTemplateFromColumn( column, factory );
 				discriminatorAlias = column.getAlias( factory.getDialect() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 
 			discriminatorType = entityBinding.getHierarchyDetails()
 					.getEntityDiscriminator()
 					.getExplicitHibernateTypeDescriptor()
 					.getResolvedTypeMapping();
 			if ( entityBinding.getDiscriminatorMatchValue() == null ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorMatchValue().equals( NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( entityBinding.getDiscriminatorMatchValue().equals( NOT_NULL_STRING ) ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = entityBinding.getHierarchyDetails().getEntityDiscriminator().isInserted()
 						&& ! DerivedValue.class.isInstance( discriminatorRelationalValue );
 				try {
 					DiscriminatorType dtype = ( DiscriminatorType ) discriminatorType;
 					discriminatorValue = dtype.stringToObject( entityBinding.getDiscriminatorMatchValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		int i=0;
 		for( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			// TODO: fix when joins are working (HHH-6391)
 			//propertyTableNumbers[i++] = entityBinding.getJoinNumber( attributeBinding);
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding() ) {
 				continue; // skip identifier binding
 			}
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				continue;
 			}
 			propertyTableNumbers[ i++ ] = 0;
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 
 		for ( AttributeBinding attributeBinding : entityBinding.getSubEntityAttributeBindingClosure() ) {
 			if ( ! attributeBinding.getAttribute().isSingular() ) {
 				continue;
 			}
 			SingularAttributeBinding singularAttributeBinding = (SingularAttributeBinding) attributeBinding;
 
 			// TODO: fix when joins are working (HHH-6391)
 			//int join = entityBinding.getJoinNumber(singularAttributeBinding);
 			int join = 0;
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( singularAttributeBinding.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put(
 					singularAttributeBinding.getContainer().getPathBase() + '.' + singularAttributeBinding.getAttribute().getName(),
 					join
 			);
 
 			for ( SimpleValueBinding simpleValueBinding : singularAttributeBinding.getSimpleValueBindings() ) {
 				if ( DerivedValue.class.isInstance( simpleValueBinding.getSimpleValue() ) ) {
 					formulaJoinedNumbers.add( join );
 				}
 				else {
 					columnJoinNumbers.add( join );
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		int subclassSpan = entityBinding.getSubEntityBindingClosureSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( entityBinding.isPolymorphic() ) {
 			addSubclassByDiscriminatorValue( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( entityBinding.isPolymorphic() ) {
 			int k=1;
 			for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 				subclassClosure[k++] = subEntityBinding.getEntity().getName();
 				if ( subEntityBinding.isDiscriminatorMatchValueNull() ) {
 					addSubclassByDiscriminatorValue( NULL_DISCRIMINATOR, subEntityBinding.getEntity().getName() );
 				}
 				else if ( subEntityBinding.isDiscriminatorMatchValueNotNull() ) {
 					addSubclassByDiscriminatorValue( NOT_NULL_DISCRIMINATOR, subEntityBinding.getEntity().getName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						addSubclassByDiscriminatorValue(
 							dtype.stringToObject( subEntityBinding.getDiscriminatorMatchValue() ),
 							subEntityBinding.getEntity().getName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException("Error parsing discriminator value", e);
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( entityBinding );
 
 		postConstruct( mapping );
 	}
 
 	private static void initializeCustomSql(
 			CustomSQL customSql,
 			int i,
 			String[] sqlStrings,
 			boolean[] callable,
 			ExecuteUpdateResultCheckStyle[] checkStyles) {
 		sqlStrings[i] = customSql != null ?  customSql.getSql(): null;
 		callable[i] = sqlStrings[i] != null && customSql.isCallable();
 		checkStyles[i] = customSql != null && customSql.getCheckStyle() != null ?
 				customSql.getCheckStyle() :
 				ExecuteUpdateResultCheckStyle.determineDefault( sqlStrings[i], callable[i] );
 	}
 
 	protected boolean isInverseTable(int j) {
 		return isInverseTable[j];
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return isInverseSubclassTable[j];
 	}
 
 	public String getDiscriminatorColumnName() {
 		return discriminatorColumnName;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return discriminatorColumnReaders;
 	}			
 	
 	public String getDiscriminatorColumnReaderTemplate() {
 		return discriminatorColumnReaderTemplate;
 	}	
 	
 	protected String getDiscriminatorAlias() {
 		return discriminatorAlias;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return discriminatorFormulaTemplate;
 	}
 
 	public String getTableName() {
 		return qualifiedTableNames[0];
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 	public Object getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		if (value==null) {
 			return (String) subclassesByDiscriminatorValue.get(NULL_DISCRIMINATOR);
 		}
 		else {
 			String result = (String) subclassesByDiscriminatorValue.get(value);
 			if (result==null) result = (String) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
 			return result;
 		}
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	//Access cached SQL
 
 	protected boolean isDiscriminatorFormula() {
 		return discriminatorColumnName==null;
 	}
 
 	protected String getDiscriminatorFormula() {
 		return discriminatorFormula;
 	}
 
 	protected String getTableName(int j) {
 		return qualifiedTableNames[j];
 	}
 	
 	protected String[] getKeyColumns(int j) {
 		return keyColumnNames[j];
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return cascadeDeleteEnabled[j];
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return propertyTableNumbers[property]==j;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 	
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' ' + name;
 	}
 
+	@Override
 	public String filterFragment(String alias) throws MappingException {
 		String result = discriminatorFilterFragment(alias);
 		if ( hasWhere() ) result += " and " + getSQLWhereString(alias);
 		return result;
 	}
+
+	private String discriminatorFilterFragment(String alias) throws MappingException {
+		return discriminatorFilterFragment( alias, null );
+	}
 	
 	public String oneToManyFilterFragment(String alias) throws MappingException {
-		return forceDiscriminator ?
-			discriminatorFilterFragment(alias) :
-			"";
+		return forceDiscriminator
+				? discriminatorFilterFragment( alias, null )
+				: "";
 	}
 
-	private String discriminatorFilterFragment(String alias) throws MappingException {
-		if ( needsDiscriminator() ) {
-			InFragment frag = new InFragment();
+	@Override
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
+		return needsDiscriminator()
+				? discriminatorFilterFragment( alias, treatAsDeclarations )
+				: "";
+	}
 
-			if ( isDiscriminatorFormula() ) {
-				frag.setFormula( alias, getDiscriminatorFormulaTemplate() );
-			}
-			else {
-				frag.setColumn( alias, getDiscriminatorColumnName() );
-			}
+	@Override
+	public String filterFragment(String alias, Set<String> treatAsDeclarations) {
+		String result = discriminatorFilterFragment( alias, treatAsDeclarations );
+		if ( hasWhere() ) {
+			result += " and " + getSQLWhereString( alias );
+		}
+		return result;
+	}
 
-			String[] subclasses = getSubclassClosure();
-			for ( int i=0; i<subclasses.length; i++ ) {
-				final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclasses[i] );
-				if ( !queryable.isAbstract() ) frag.addValue( queryable.getDiscriminatorSQLValue() );
-			}
+	private String discriminatorFilterFragment(String alias, Set<String> treatAsDeclarations)  {
+		final boolean hasTreatAs = treatAsDeclarations != null && !treatAsDeclarations.isEmpty();
 
-			StringBuilder buf = new StringBuilder(50)
-				.append(" and ")
-				.append( frag.toFragmentString() );
+		if ( !needsDiscriminator() && !hasTreatAs) {
+			return "";
+		}
 
-			return buf.toString();
+		final InFragment frag = new InFragment();
+		if ( isDiscriminatorFormula() ) {
+			frag.setFormula( alias, getDiscriminatorFormulaTemplate() );
 		}
 		else {
-			return "";
+			frag.setColumn( alias, getDiscriminatorColumnName() );
+		}
+
+		if ( hasTreatAs ) {
+			frag.addValues( decodeTreatAsRequests( treatAsDeclarations ) );
+		}
+		else {
+			frag.addValues( fullDiscriminatorValues() );
 		}
+
+		return " and " + frag.toFragmentString();
 	}
 
 	private boolean needsDiscriminator() {
 		return forceDiscriminator || isInherited();
 	}
 
+	private String[] decodeTreatAsRequests(Set<String> treatAsDeclarations) {
+		final List<String> values = new ArrayList<String>();
+		for ( String subclass : treatAsDeclarations ) {
+			final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclass );
+			if ( !queryable.isAbstract() ) {
+				values.add( queryable.getDiscriminatorSQLValue() );
+			}
+		}
+		return values.toArray( new String[ values.size() ] );
+	}
+
+	private String[] fullDiscriminatorValues;
+
+	private String[] fullDiscriminatorValues() {
+		if ( fullDiscriminatorValues == null ) {
+			// first access; build it
+			final List<String> values = new ArrayList<String>();
+			for ( String subclass : getSubclassClosure() ) {
+				final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclass );
+				if ( !queryable.isAbstract() ) {
+					values.add( queryable.getDiscriminatorSQLValue() );
+				}
+			}
+			fullDiscriminatorValues = values.toArray( new String[values.size() ] );
+		}
+
+		return fullDiscriminatorValues;
+	}
+
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( isDiscriminatorFormula() ) {
 			select.addFormula( name, getDiscriminatorFormulaTemplate(), getDiscriminatorAlias() );
 		}
 		else {
 			select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 		}
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return joinSpan;
 	}
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 
 		if (discriminatorInsertable) {
 			insert.addColumn( getDiscriminatorColumnName(), discriminatorSQLValue );
 		}
 
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return propertyTableNumbers;
 	}
 		
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return hasSequentialSelects && 
 			isSubclassTableSequentialSelect( getSubclassPropertyTableNumber(propertyName, entityName) );
 	}
 	
 	public boolean hasSequentialSelect() {
 		return hasSequentialSelects;
 	}
 	
 	private int getSubclassPropertyTableNumber(String propertyName, String entityName) {
 		Type type = propertyMapping.toType(propertyName);
 		if ( type.isAssociationType() && ( (AssociationType) type ).useLHSPrimaryKey() ) return 0;
 		final Integer tabnum = (Integer) propertyTableNumbersByNameAndSubclass.get(entityName + '.' + propertyName);
 		return tabnum==null ? 0 : tabnum;
 	}
 	
 	protected String getSequentialSelect(String entityName) {
 		return (String) sequentialSelectStringsByEntityName.get(entityName);
 	}
 
 	private String generateSequentialSelect(Loadable persister) {
 		//if ( this==persister || !hasSequentialSelects ) return null;
 
 		//note that this method could easily be moved up to BasicEntityPersister,
 		//if we ever needed to reuse it from other subclasses
 		
 		//figure out which tables need to be fetched
 		AbstractEntityPersister subclassPersister = (AbstractEntityPersister) persister;
 		HashSet tableNumbers = new HashSet();
 		String[] props = subclassPersister.getPropertyNames();
 		String[] classes = subclassPersister.getPropertySubclassNames();
 		for ( int i=0; i<props.length; i++ ) {
 			int propTableNumber = getSubclassPropertyTableNumber( props[i], classes[i] );
 			if ( isSubclassTableSequentialSelect(propTableNumber) && !isSubclassTableLazy(propTableNumber) ) {
 				tableNumbers.add( propTableNumber);
 			}
 		}
 		if ( tableNumbers.isEmpty() ) return null;
 		
 		//figure out which columns are needed
 		ArrayList columnNumbers = new ArrayList();
 		final int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassColumnClosure().length; i++ ) {
 			if ( tableNumbers.contains( columnTableNumbers[i] ) ) {
 				columnNumbers.add( i );
 			}
 		}
 		
 		//figure out which formulas are needed
 		ArrayList formulaNumbers = new ArrayList();
 		final int[] formulaTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassFormulaTemplateClosure().length; i++ ) {
 			if ( tableNumbers.contains( formulaTableNumbers[i] ) ) {
 				formulaNumbers.add( i );
 			}
 		}
 		
 		//render the SQL
 		return renderSelect( 
 			ArrayHelper.toIntArray(tableNumbers),
 			ArrayHelper.toIntArray(columnNumbers),
 			ArrayHelper.toIntArray(formulaNumbers)
 		);
 	}
 		
 		
 	protected String[] getSubclassTableKeyColumns(int j) {
 		return subclassTableKeyColumnClosure[j];
 	}
 
 	public String getSubclassTableName(int j) {
 		return subclassTableNameClosure[j];
 	}
 
 	public int getSubclassTableSpan() {
 		return subclassTableNameClosure.length;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 	
 	protected boolean isNullableTable(int j) {
 		return isNullableTable[j];
 	}
 	
 	protected boolean isNullableSubclassTable(int j) {
 		return isNullableSubclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
 		if (index==null) return null;
 		return qualifiedTableNames[ propertyTableNumbers[index] ];
 	}
 	
 	protected void doPostInstantiate() {
 		if (hasSequentialSelects) {
 			String[] entityNames = getSubclassClosure();
 			for ( int i=1; i<entityNames.length; i++ ) {
 				Loadable loadable = (Loadable) getFactory().getEntityPersister( entityNames[i] );
 				if ( !loadable.isAbstract() ) { //perhaps not really necessary...
 					String sequentialSelect = generateSequentialSelect(loadable);
 					sequentialSelectStringsByEntityName.put( entityNames[i], sequentialSelect );
 				}
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return getTableSpan() > 1;
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new DynamicFilterAliasGenerator(qualifiedTableNames, rootAlias);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
index 546a264098..06521b13f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
@@ -1,517 +1,524 @@
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
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the "table-per-concrete-class" or "roll-down" mapping 
  * strategy for an entity and its inheritence hierarchy.
  *
  * @author Gavin King
  */
 public class UnionSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final String subquery;
 	private final String tableName;
 	//private final String rootTableName;
 	private final String[] subclassClosure;
 	private final String[] spaces;
 	private final String[] subclassSpaces;
 	private final Object discriminatorValue;
 	private final String discriminatorSQLValue;
 	private final Map subclassByDiscriminatorValue = new HashMap();
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//INITIALIZATION:
 
 	public UnionSubclassEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory );
 		
 		if ( getIdentifierGenerator() instanceof IdentityGenerator ) {
 			throw new MappingException(
 					"Cannot use identity column key generation with <union-subclass> mapping for: " + 
 					getEntityName() 
 			);
 		}
 
 		// TABLE
 
 		tableName = persistentClass.getTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		/*rootTableName = persistentClass.getRootTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getDefaultCatalog(), 
 				factory.getDefaultSchema() 
 		);*/
 
 		//Custom SQL
 
 		String sql;
 		boolean callable = false;
 		ExecuteUpdateResultCheckStyle checkStyle = null;
 		sql = persistentClass.getCustomSQLInsert();
 		callable = sql != null && persistentClass.isCustomInsertCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLInsertCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLInsert = new String[] { sql };
 		insertCallable = new boolean[] { callable };
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLUpdate();
 		callable = sql != null && persistentClass.isCustomUpdateCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLUpdateCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLUpdate = new String[] { sql };
 		updateCallable = new boolean[] { callable };
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLDelete();
 		callable = sql != null && persistentClass.isCustomDeleteCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLDeleteCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLDeleteCheckStyle();
 		customSQLDelete = new String[] { sql };
 		deleteCallable = new boolean[] { callable };
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		discriminatorValue = persistentClass.getSubclassId();
 		discriminatorSQLValue = String.valueOf( persistentClass.getSubclassId() );
 
 		// PROPERTIES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 
 		// SUBCLASSES
 		subclassByDiscriminatorValue.put( 
 				persistentClass.getSubclassId(),
 				persistentClass.getEntityName() 
 		);
 		if ( persistentClass.isPolymorphic() ) {
 			Iterator iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				subclassByDiscriminatorValue.put( sc.getSubclassId(), sc.getEntityName() );
 			}
 		}
 		
 		//SPACES
 		//TODO: i'm not sure, but perhaps we should exclude
 		//      abstract denormalized tables?
 		
 		int spacesSize = 1 + persistentClass.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = tableName;
 		Iterator iter = persistentClass.getSynchronizedTables().iterator();
 		for ( int i=1; i<spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 		
 		HashSet subclassTables = new HashSet();
 		iter = persistentClass.getSubclassTableClosureIterator();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			subclassTables.add( table.getQualifiedName(
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 		}
 		subclassSpaces = ArrayHelper.toStringArray(subclassTables);
 
 		subquery = generateSubquery(persistentClass, mapping);
 
 		if ( isMultiTable() ) {
 			int idColumnSpan = getIdentifierColumnSpan();
 			ArrayList tableNames = new ArrayList();
 			ArrayList keyColumns = new ArrayList();
 			if ( !isAbstract() ) {
 				tableNames.add( tableName );
 				keyColumns.add( getIdentifierColumnNames() );
 			}
 			iter = persistentClass.getSubclassTableClosureIterator();
 			while ( iter.hasNext() ) {
 				Table tab = ( Table ) iter.next();
 				if ( !tab.isAbstractUnionTable() ) {
 					String tableName = tab.getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					);
 					tableNames.add( tableName );
 					String[] key = new String[idColumnSpan];
 					Iterator citer = tab.getPrimaryKey().getColumnIterator();
 					for ( int k=0; k<idColumnSpan; k++ ) {
 						key[k] = ( ( Column ) citer.next() ).getQuotedName( factory.getDialect() );
 					}
 					keyColumns.add( key );
 				}
 			}
 
 			constraintOrderedTableNames = ArrayHelper.toStringArray( tableNames );
 			constraintOrderedKeyColumnNames = ArrayHelper.to2DStringArray( keyColumns );
 		}
 		else {
 			constraintOrderedTableNames = new String[] { tableName };
 			constraintOrderedKeyColumnNames = new String[][] { getIdentifierColumnNames() };
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
 	public UnionSubclassEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 		super(entityBinding, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory );
 		// TODO: implement!!! initializing final fields to null to make compiler happy.
 		subquery = null;
 		tableName = null;
 		subclassClosure = null;
 		spaces = null;
 		subclassSpaces = null;
 		discriminatorValue = null;
 		discriminatorSQLValue = null;
 		constraintOrderedTableNames = null;
 		constraintOrderedKeyColumnNames = null;
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return subclassSpaces;
 	}
 	
 	public String getTableName() {
 		return subquery;
 	}
 
 	public Type getDiscriminatorType() {
 		return StandardBasicTypes.INTEGER;
 	}
 
 	public Object getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassByDiscriminatorValue.get(value);
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	protected boolean isDiscriminatorFormula() {
 		return false;
 	}
 
 	/**
 	 * Generate the SQL that selects a row by id
 	 */
 	protected String generateSelectString(LockMode lockMode) {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 			.setLockMode(lockMode)
 			.setTableName( getTableName() )
 			.addColumns( getIdentifierColumnNames() )
 			.addColumns( 
 					getSubclassColumnClosure(), 
 					getSubclassColumnAliasClosure(),
 					getSubclassColumnLazyiness()
 			)
 			.addColumns( 
 					getSubclassFormulaClosure(), 
 					getSubclassFormulaAliasClosure(),
 					getSubclassFormulaLazyiness()
 			);
 		//TODO: include the rowids!!!!
 		if ( hasSubclasses() ) {
 			if ( isDiscriminatorFormula() ) {
 				select.addColumn( getDiscriminatorFormula(), getDiscriminatorAlias() );
 			}
 			else {
 				select.addColumn( getDiscriminatorColumnName(), getDiscriminatorAlias() );
 			}
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "load " + getEntityName() );
 		}
 		return select.addCondition( getIdentifierColumnNames(), "=?" ).toStatementString();
 	}
 
 	protected String getDiscriminatorFormula() {
 		return null;
 	}
 
 	protected String getTableName(int j) {
 		return tableName;
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return getIdentifierColumnNames();
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return false;
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return true;
 	}
 
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' '  + name;
 	}
 
+	@Override
 	public String filterFragment(String name) {
-		return hasWhere() ?
-			" and " + getSQLWhereString(name) :
-			"";
+		return hasWhere()
+				? " and " + getSQLWhereString( name )
+				: "";
+	}
+
+	@Override
+	protected String filterFragment(String alias, Set<String> treatAsDeclarations) {
+		return filterFragment( alias );
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return getTableName();//ie. the subquery! yuck!
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return 0;
 	}
 
 	public int getSubclassPropertyTableNumber(String propertyName) {
 		return 0;
 	}
 
 	public boolean isMultiTable() {
 		// This could also just be true all the time...
 		return isAbstract() || hasSubclasses();
 	}
 
 	public int getTableSpan() {
 		return 1;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return new int[ getSubclassColumnClosure().length ];
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return new int[ getSubclassFormulaClosure().length ];
 	}
 
 	protected boolean[] getTableHasColumns() {
 		return new boolean[] { true };
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected String generateSubquery(PersistentClass model, Mapping mapping) {
 
 		Dialect dialect = getFactory().getDialect();
 		Settings settings = getFactory().getSettings();
 		
 		if ( !model.hasSubclasses() ) {
 			return model.getTable().getQualifiedName(
 					dialect,
 					settings.getDefaultCatalogName(),
 					settings.getDefaultSchemaName()
 				);
 		}
 
 		HashSet columns = new LinkedHashSet();
 		Iterator titer = model.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table table = (Table) titer.next();
 			if ( !table.isAbstractUnionTable() ) {
 				Iterator citer = table.getColumnIterator();
 				while ( citer.hasNext() ) columns.add( citer.next() );
 			}
 		}
 
 		StringBuilder buf = new StringBuilder()
 			.append("( ");
 
 		Iterator siter = new JoinedIterator(
 			new SingletonIterator(model),
 			model.getSubclassIterator()
 		);
 
 		while ( siter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) siter.next();
 			Table table = clazz.getTable();
 			if ( !table.isAbstractUnionTable() ) {
 				//TODO: move to .sql package!!
 				buf.append("select ");
 				Iterator citer = columns.iterator();
 				while ( citer.hasNext() ) {
 					Column col = (Column) citer.next();
 					if ( !table.containsColumn(col) ) {
 						int sqlType = col.getSqlTypeCode(mapping);
 						buf.append( dialect.getSelectClauseNullString(sqlType) )
 							.append(" as ");
 					}
 					buf.append( col.getName() );
 					buf.append(", ");
 				}
 				buf.append( clazz.getSubclassId() )
 					.append(" as clazz_");
 				buf.append(" from ")
 					.append( table.getQualifiedName(
 							dialect,
 							settings.getDefaultCatalogName(),
 							settings.getDefaultSchemaName()
 					) );
 				buf.append(" union ");
 				if ( dialect.supportsUnionAll() ) {
 					buf.append("all ");
 				}
 			}
 		}
 		
 		if ( buf.length() > 2 ) {
 			//chop the last union (all)
 			buf.setLength( buf.length() - ( dialect.supportsUnionAll() ? 11 : 7 ) );
 		}
 
 		return buf.append(" )").toString();
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return getIdentifierColumnNames();
 	}
 
 	public String getSubclassTableName(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return tableName;
 	}
 
 	public int getSubclassTableSpan() {
 		return 1;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return true;
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		//TODO: check this....
 		return getTableName();
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 			return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
index 0236b3b6aa..0402908066 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
@@ -1,132 +1,143 @@
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
 package org.hibernate.sql;
 
 import java.util.ArrayList;
+import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * An SQL IN expression.
  * <br>
  * <code>... in(...)</code>
  * <br>
+ *
  * @author Gavin King
  */
 public class InFragment {
 
 	public static final String NULL = "null";
 	public static final String NOT_NULL = "not null";
 
 	private String columnName;
 	private List<Object> values = new ArrayList<Object>();
 
 	/**
 	 * @param value an SQL literal, NULL, or NOT_NULL
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public InFragment addValue(Object value) {
-		values.add(value);
+		values.add( value );
+		return this;
+	}
+
+	public InFragment addValues(Object[] values) {
+		Collections.addAll( this.values, values );
 		return this;
 	}
 
 	public InFragment setColumn(String columnName) {
 		this.columnName = columnName;
 		return this;
 	}
 
 	public InFragment setColumn(String alias, String columnName) {
 		this.columnName = StringHelper.qualify( alias, columnName );
-		return setColumn(this.columnName);
+		return setColumn( this.columnName );
 	}
 
 	public InFragment setFormula(String alias, String formulaTemplate) {
-		this.columnName = StringHelper.replace(formulaTemplate, Template.TEMPLATE, alias);
-		return setColumn(this.columnName);
+		this.columnName = StringHelper.replace( formulaTemplate, Template.TEMPLATE, alias );
+		return setColumn( this.columnName );
 	}
 
 	public String toFragmentString() {
-
-                if (values.size() == 0) {
-                   return "1=2";
-                }
-
-                StringBuilder buf = new StringBuilder(values.size() * 5);
-
-                if (values.size() == 1) {
-                   Object value = values.get(0);
-                   buf.append(columnName);
-
-                   if (NULL.equals(value)) {
-                      buf.append(" is null");
-                   } else {
-                      if (NOT_NULL.equals(value)) {
-                         buf.append(" is not null");
-                      } else {
-                         buf.append('=').append(value);
-                      }
-                   }
-                   return buf.toString();
-                }
-                   
-                boolean allowNull = false;
-
-                for (Object value : values) {
-                   if (NULL.equals(value)) {
-                      allowNull = true;
-                   } else {
-                      if (NOT_NULL.equals(value)) {
-                         throw new IllegalArgumentException("not null makes no sense for in expression");
-                      }
-                   }
-                }
-
-                if (allowNull) {
-                   buf.append('(').append(columnName).append(" is null or ").append(columnName).append(" in (");
-                } else {
-                   buf.append(columnName).append(" in (");
-                }
-
-                for (Object value : values) {
-                   if ( ! NULL.equals(value) ) {
-                      buf.append(value);
-                      buf.append(", ");
-                   }
-                }
-
-                buf.setLength(buf.length() - 2);
-
-                if (allowNull) {
-                   buf.append("))");
-                } else {
-                   buf.append(')');
-                }
-
-                return buf.toString();
+		if ( values.size() == 0 ) {
+			return "1=2";
+		}
+
+		StringBuilder buf = new StringBuilder( values.size() * 5 );
+
+		if ( values.size() == 1 ) {
+			Object value = values.get( 0 );
+			buf.append( columnName );
+
+			if ( NULL.equals( value ) ) {
+				buf.append( " is null" );
+			}
+			else {
+				if ( NOT_NULL.equals( value ) ) {
+					buf.append( " is not null" );
+				}
+				else {
+					buf.append( '=' ).append( value );
+				}
+			}
+			return buf.toString();
+		}
+
+		boolean allowNull = false;
+
+		for ( Object value : values ) {
+			if ( NULL.equals( value ) ) {
+				allowNull = true;
+			}
+			else {
+				if ( NOT_NULL.equals( value ) ) {
+					throw new IllegalArgumentException( "not null makes no sense for in expression" );
+				}
+			}
+		}
+
+		if ( allowNull ) {
+			buf.append( '(' ).append( columnName ).append( " is null or " ).append( columnName ).append( " in (" );
+		}
+		else {
+			buf.append( columnName ).append( " in (" );
+		}
+
+		for ( Object value : values ) {
+			if ( !NULL.equals( value ) ) {
+				buf.append( value );
+				buf.append( ", " );
+			}
+		}
+
+		buf.setLength( buf.length() - 2 );
+
+		if ( allowNull ) {
+			buf.append( "))" );
+		}
+		else {
+			buf.append( ')' );
+		}
+
+		return buf.toString();
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index b8a25c54ab..b3d63be7b8 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,527 +1,536 @@
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
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Map;
+import java.util.Set;
 
 import org.dom4j.Node;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
 	private final TypeFactory.TypeScope scope;
 	private final Type identifierType;
 	private final Type discriminatorType;
 
 	/**
 	 * Intended for use only from legacy {@link ObjectType} type definition
 	 *
 	 * @param discriminatorType
 	 * @param identifierType
 	 */
 	protected AnyType(Type discriminatorType, Type identifierType) {
 		this( null, discriminatorType, identifierType );
 	}
 
 	public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType) {
 		this.scope = scope;
 		this.discriminatorType = discriminatorType;
 		this.identifierType = identifierType;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 
 	// general Type metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getName() {
 		return "object";
 	}
 
 	@Override
 	public Class getReturnedClass() {
 		return Object.class;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.sqlTypes( mapping ), identifierType.sqlTypes( mapping ) );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.dictatedSizes( mapping ), identifierType.dictatedSizes( mapping ) );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.defaultSizes( mapping ), identifierType.defaultSizes( mapping ) );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isAnyType() {
 		return true;
 	}
 
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponentType() {
 		return true;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value;
 	}
 
 
 	// general Type functionality ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int compare(Object x, Object y) {
 		if ( x == null ) {
 			// if y is also null, return that they are the same (no option for "UNKNOWN")
 			// if y is not null, return that y is "greater" (-1 because the result is from the perspective of
 			// 		the first arg: x)
 			return y == null ? 0 : -1;
 		}
 		else if ( y == null ) {
 			// x is not null, but y is.  return that x is "greater"
 			return 1;
 		}
 
 		// At this point we know both are non-null.
 		final Object xId = extractIdentifier( x );
 		final Object yId = extractIdentifier( y );
 
 		return getIdentifierType().compare( xId, yId );
 	}
 
 	private Object extractIdentifier(Object entity) {
 		final EntityPersister concretePersister = guessEntityPersister( entity );
 		return concretePersister == null
 				? null
 				: concretePersister.getEntityTuplizer().getIdentifier( entity, null );
 	}
 
 	private EntityPersister guessEntityPersister(Object object) {
 		if ( scope == null ) {
 			return null;
 		}
 
 		String entityName = null;
 
 		// this code is largely copied from Session's bestGuessEntityName
 		Object entity = object;
 		if ( entity instanceof HibernateProxy ) {
 			final LazyInitializer initializer = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
 			if ( initializer.isUninitialized() ) {
 				entityName = initializer.getEntityName();
 			}
 			entity = initializer.getImplementation();
 		}
 
 		if ( entityName == null ) {
 			for ( EntityNameResolver resolver : scope.resolveFactory().iterateEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			// the old-time stand-by...
 			entityName = object.getClass().getName();
 		}
 
 		return scope.resolveFactory().getEntityPersister( entityName );
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		else if ( old == null ) {
 			return true;
 		}
 
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
 		final boolean[] idCheckable = new boolean[checkable.length-1];
 		System.arraycopy( checkable, 1, idCheckable, 0, idCheckable.length );
 		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName( current ) ) )
 				|| identifierType.isModified( holder.id, getIdentifier( current, session ), idCheckable, session );
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		final boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty( old, current, session );
 	}
 
 	@Override
 	public int getColumnSpan(Mapping session) {
 		return 2;
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		return resolveAny(
 				(String) discriminatorType.nullSafeGet( rs, names[0], session, owner ),
 				(Serializable) identifierType.nullSafeGet( rs, names[1], session, owner ),
 				session
 		);
 	}
 
 	@Override
 	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		final String entityName = (String) discriminatorType.nullSafeGet( rs, names[0], session, owner );
 		final Serializable id = (Serializable) identifierType.nullSafeGet( rs, names[1], session, owner );
 		return new ObjectTypeCacheEntry( entityName, id );
 	}
 
 	@Override
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
 		return resolveAny( holder.entityName, holder.id, session );
 	}
 
 	private Object resolveAny(String entityName, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		return entityName==null || id==null
 				? null
 				: session.internalLoad( entityName, id, false, false );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
 			throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, null, session );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Serializable id;
 		String entityName;
 		if ( value == null ) {
 			id = null;
 			entityName = null;
 		}
 		else {
 			entityName = session.bestGuessEntityName( value );
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, value, session );
 		}
 
 		// discriminatorType is assumed to be single-column type
 		if ( settable == null || settable[0] ) {
 			discriminatorType.nullSafeSet( st, entityName, index, session );
 		}
 		if ( settable == null ) {
 			identifierType.nullSafeSet( st, id, index+1, session );
 		}
 		else {
 			final boolean[] idSettable = new boolean[ settable.length-1 ];
 			System.arraycopy( settable, 1, idSettable, 0, idSettable.length );
 			identifierType.nullSafeSet( st, id, index+1, idSettable, session );
 		}
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		//TODO: terrible implementation!
 		return value == null
 				? "null"
 				: factory.getTypeHelper()
 				.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
 				.toLoggableString( value, factory );
 	}
 
 	@Override
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
 		return e == null ? null : session.internalLoad( e.entityName, e.id, false, false );
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			return new ObjectTypeCacheEntry(
 					session.bestGuessEntityName( value ),
 					ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							session.bestGuessEntityName( value ),
 							value,
 							session
 					)
 			);
 		}
 	}
 
 	@Override
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
 			throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		else {
 			final String entityName = session.bestGuessEntityName( original );
 			final Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, original, session );
 			return session.internalLoad( entityName, id, false, false );
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "object is a multicolumn type" );
 	}
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "any mappings may not form part of a property-ref" );
 	}
 
 	@Override
 	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types cannot be stringified");
 	}
 
 	@Override
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 
 
 	// CompositeType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
 	@Override
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	@Override
 	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
 		return i==0
 				? session.bestGuessEntityName( component )
 				: getIdentifier( component, session );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
 		return new Object[] {
 				session.bestGuessEntityName( component ),
 				getIdentifier( component, session )
 		};
 	}
 
 	private Serializable getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		try {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					session.bestGuessEntityName( value ),
 					value,
 					session
 			);
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	private static final boolean[] NULLABILITY = new boolean[] { false, false };
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return NULLABILITY;
 	}
 
 	@Override
 	public Type[] getSubtypes() {
 		return new Type[] {discriminatorType, identifierType };
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyles.NONE;
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.SELECT;
 	}
 
 
 	// AssociationType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
 	@Override
 	public boolean isEmbeddedInXML() {
 		return false;
 	}
 
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
+	public String getOnCondition(
+			String alias,
+			SessionFactoryImplementor factory,
+			Map enabledFilters,
+			Set<String> treatAsDeclarations) {
+		throw new UnsupportedOperationException();
+	}
 
 	/**
 	 * Used to externalize discrimination per a given identifier.  For example, when writing to
 	 * second level cache we write the discrimination resolved concrete type for each entity written.
 	 */
 	public static final class ObjectTypeCacheEntry implements Serializable {
 		final String entityName;
 		final Serializable id;
 
 		ObjectTypeCacheEntry(String entityName, Serializable id) {
 			this.entityName = entityName;
 			this.id = id;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
index 88ed066c4b..7b45399f4c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
@@ -1,101 +1,108 @@
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
 
 import java.util.Map;
+import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.Joinable;
 
 /**
  * A type that represents some kind of association between entities.
  * @see org.hibernate.engine.internal.Cascade
  * @author Gavin King
  */
 public interface AssociationType extends Type {
 
 	/**
 	 * Get the foreign key directionality of this association
 	 */
 	public ForeignKeyDirection getForeignKeyDirection();
 
 	//TODO: move these to a new JoinableType abstract class,
 	//extended by EntityType and PersistentCollectionType:
 
 	/**
 	 * Is the primary key of the owning entity table
 	 * to be used in the join?
 	 */
 	public boolean useLHSPrimaryKey();
 	/**
 	 * Get the name of a property in the owning entity 
 	 * that provides the join key (null if the identifier)
 	 */
 	public String getLHSPropertyName();
 	
 	/**
 	 * The name of a unique property of the associated entity 
 	 * that provides the join key (null if the identifier of
 	 * an entity, or key of a collection)
 	 */
 	public String getRHSUniqueKeyPropertyName();
 
 	/**
 	 * Get the "persister" for this association - a class or
 	 * collection persister
 	 */
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException;
 	
 	/**
 	 * Get the entity name of the associated entity
 	 */
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) throws MappingException;
 	
 	/**
 	 * Get the "filtering" SQL fragment that is applied in the 
 	 * SQL on clause, in addition to the usual join condition
 	 */	
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) 
 	throws MappingException;
+
+	/**
+	 * Get the "filtering" SQL fragment that is applied in the
+	 * SQL on clause, in addition to the usual join condition
+	 */
+	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters, Set<String> treatAsDeclarations);
 	
 	/**
 	 * Do we dirty check this association, even when there are
 	 * no columns to be updated?
 	 */
 	public abstract boolean isAlwaysDirtyChecked();
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public boolean isEmbeddedInXML();
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index 5fb404e3ef..46dc07fb3f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,796 +1,806 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.jboss.logging.Logger;
 
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionType.class.getName());
 
 	private static final Object NOT_NULL_COLLECTION = new MarkerObject( "NOT NULL COLLECTION" );
 	public static final Object UNFETCHED_COLLECTION = new MarkerObject( "UNFETCHED COLLECTION" );
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String role;
 	private final String foreignKeyPropertyName;
 	private final boolean isEmbeddedInXML;
 
 	/**
 	 * @deprecated Use {@link #CollectionType(TypeFactory.TypeScope, String, String)} instead
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName, boolean isEmbeddedInXML) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 		this.isEmbeddedInXML = isEmbeddedInXML;
 	}
 
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 		this.isEmbeddedInXML = true;
 	}
 
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Object indexOf(Object collection, Object element) {
 		throw new UnsupportedOperationException( "generic collections don't have indexes" );
 	}
 
 	public boolean contains(Object collection, Object childObject, SessionImplementor session) {
 		// we do not have to worry about queued additions to uninitialized
 		// collections, since they can only occur for inverse collections!
 		Iterator elems = getElementsIterator( collection, session );
 		while ( elems.hasNext() ) {
 			Object element = elems.next();
 			// worrying about proxies is perhaps a little bit of overkill here...
 			if ( element instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) element ).getHibernateLazyInitializer();
 				if ( !li.isUninitialized() ) element = li.getImplementation();
 			}
 			if ( element == childObject ) return true;
 		}
 		return false;
 	}
 
 	public boolean isCollectionType() {
 		return true;
 	}
 
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
 	public int getHashCode(Object x) {
 		throw new UnsupportedOperationException( "cannot doAfterTransactionCompletion lookups on collections" );
 	}
 
 	/**
 	 * Instantiate an uninitialized collection wrapper or holder. Callers MUST add the holder to the
 	 * persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param persister The underlying collection persister (metadata)
 	 * @param key The owner key.
 	 * @return The instantiated collection.
 	 */
 	public abstract PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key);
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] { name }, session, owner );
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DICTATED_SIZE };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DEFAULT_SIZE };
 	}
 
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( !Hibernate.isInitialized( value ) ) {
 			return "<uninitialized>";
 		}
 		else {
 			return renderLoggableString( value, factory );
 		}
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		final List<String> list = new ArrayList<String>();
 		Type elemType = getElementType( factory );
 		Iterator itr = getElementsIterator( value );
 		while ( itr.hasNext() ) {
 			list.add( elemType.toLoggableString( itr.next(), factory ) );
 		}
 		return list.toString();
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	public String getName() {
 		return getReturnedClass().getName() + '(' + getRole() + ')';
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection, which may not yet be wrapped
 	 *
 	 * @param collection The collection to be iterated
 	 * @param session The session from which the request is originating.
 	 * @return The iterator.
 	 */
 	public Iterator getElementsIterator(Object collection, SessionImplementor session) {
 		return getElementsIterator(collection);
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection in POJO mode
 	 *
 	 * @param collection The collection to be iterated
 	 * @return The iterator.
 	 */
 	protected Iterator getElementsIterator(Object collection) {
 		return ( (Collection) collection ).iterator();
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//remember the uk value
 		
 		//This solution would allow us to eliminate the owner arg to disassemble(), but
 		//what if the collection was null, and then later had elements added? seems unsafe
 		//session.getPersistenceContext().getCollectionEntry( (PersistentCollection) value ).getKey();
 		
 		final Serializable key = getKeyOfOwner(owner, session);
 		if (key==null) {
 			return null;
 		}
 		else {
 			return getPersister(session)
 					.getKeyType()
 					.disassemble( key, session, owner );
 		}
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//we must use the "remembered" uk value, since it is 
 		//not available from the EntityEntry during assembly
 		if (cached==null) {
 			return null;
 		}
 		else {
 			final Serializable key = (Serializable) getPersister(session)
 					.getKeyType()
 					.assemble( cached, session, owner);
 			return resolveKey( key, session, owner );
 		}
 	}
 
 	/**
 	 * Is the owning entity versioned?
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return True if the collection owner is versioned; false otherwise.
 	 * @throws org.hibernate.MappingException Indicates our persister could not be located.
 	 */
 	private boolean isOwnerVersioned(SessionImplementor session) throws MappingException {
 		return getPersister( session ).getOwnerEntityPersister().isVersioned();
 	}
 
 	/**
 	 * Get our underlying collection persister (using the session to access the
 	 * factory).
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return The underlying collection persister
 	 */
 	private CollectionPersister getPersister(SessionImplementor session) {
 		return session.getFactory().getCollectionPersister( role );
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return isOwnerVersioned( session ) && super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty(old, current, session);
 	}
 
 	/**
 	 * Wrap the naked collection instance in a wrapper, or instantiate a
 	 * holder. Callers <b>MUST</b> add the holder to the persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param collection The bare collection to be wrapped.
 	 * @return The wrapped collection.
 	 */
 	public abstract PersistentCollection wrap(SessionImplementor session, Object collection);
 
 	/**
 	 * Note: return true because this type is castable to <tt>AssociationType</tt>. Not because
 	 * all collections are associations.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
 	}
 
 	/**
 	 * Get the key value from the owning entity instance, usually the identifier, but might be some
 	 * other unique key, in the case of property-ref
 	 *
 	 * @param owner The collection owner
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's key
 	 */
 	public Serializable getKeyOfOwner(Object owner, SessionImplementor session) {
 		
 		EntityEntry entityEntry = session.getPersistenceContext().getEntry( owner );
 		if ( entityEntry == null ) return null; // This just handles a particular case of component
 									  // projection, perhaps get rid of it and throw an exception
 		
 		if ( foreignKeyPropertyName == null ) {
 			return entityEntry.getId();
 		}
 		else {
 			// TODO: at the point where we are resolving collection references, we don't
 			// know if the uk value has been resolved (depends if it was earlier or
 			// later in the mapping document) - now, we could try and use e.getStatus()
 			// to decide to semiResolve(), trouble is that initializeEntity() reuses
 			// the same array for resolved and hydrated values
 			Object id;
 			if ( entityEntry.getLoadedState() != null ) {
 				id = entityEntry.getLoadedValue( foreignKeyPropertyName );
 			}
 			else {
 				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName );
 			}
 
 			// NOTE VERY HACKISH WORKAROUND!!
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Type keyType = getPersister( session ).getKeyType();
 			if ( !keyType.getReturnedClass().isInstance( id ) ) {
 				id = keyType.semiResolve(
 						entityEntry.getLoadedValue( foreignKeyPropertyName ),
 						session,
 						owner 
 					);
 			}
 
 			return (Serializable) id;
 		}
 	}
 
 	/**
 	 * Get the id value from the owning entity key, usually the same as the key, but might be some
 	 * other property, in the case of property-ref
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's id, if it can be obtained from the key;
 	 * otherwise, null is returned
 	 */
 	public Serializable getIdOfOwnerOrNull(Serializable key, SessionImplementor session) {
 		Serializable ownerId = null;
 		if ( foreignKeyPropertyName == null ) {
 			ownerId = key;
 		}
 		else {
 			Type keyType = getPersister( session ).getKeyType();
 			EntityPersister ownerPersister = getPersister( session ).getOwnerEntityPersister();
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Class ownerMappedClass = ownerPersister.getMappedClass();
 			if ( ownerMappedClass.isAssignableFrom( keyType.getReturnedClass() ) &&
 					keyType.getReturnedClass().isInstance( key ) ) {
 				// the key is the owning entity itself, so get the ID from the key
 				ownerId = ownerPersister.getIdentifier( key, session );
 			}
 			else {
 				// TODO: check if key contains the owner ID
 			}
 		}
 		return ownerId;
 	}
 
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		
 		return resolveKey( getKeyOfOwner( owner, session ), session, owner );
 	}
 	
 	private Object resolveKey(Serializable key, SessionImplementor session, Object owner) {
 		// if (key==null) throw new AssertionFailure("owner identifier unknown when re-assembling
 		// collection reference");
 		return key == null ? null : // TODO: can this case really occur??
 			getCollection( key, session, owner );
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return false;
 	}
 
 	public String getAssociatedEntityName(SessionFactoryImplementor factory)
 			throws MappingException {
 		try {
 			
 			QueryableCollection collectionPersister = (QueryableCollection) factory
 					.getCollectionPersister( role );
 			
 			if ( !collectionPersister.getElementType().isEntityType() ) {
 				throw new MappingException( 
 						"collection was not an association: " + 
 						collectionPersister.getRole() 
 					);
 			}
 			
 			return collectionPersister.getElementPersister().getEntityName();
 			
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "collection role is not queryable " + role );
 		}
 	}
 
 	/**
 	 * Replace the elements of a collection with the elements of another collection.
 	 *
 	 * @param original The 'source' of the replacement elements (where we copy from)
 	 * @param target The target of the replacement elements (where we copy to)
 	 * @param owner The owner of the collection being merged
 	 * @param copyCache The map of elements already replaced.
 	 * @param session The session from which the merge event originated.
 	 * @return The merged collection.
 	 */
 	public Object replaceElements(
 			Object original,
 			Object target,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		// TODO: does not work for EntityMode.DOM4J yet!
 		java.util.Collection result = ( java.util.Collection ) target;
 		result.clear();
 
 		// copy elements into newly empty target collection
 		Type elemType = getElementType( session.getFactory() );
 		Iterator iter = ( (java.util.Collection) original ).iterator();
 		while ( iter.hasNext() ) {
 			result.add( elemType.replace( iter.next(), null, session, owner, copyCache ) );
 		}
 
 		// if the original is a PersistentCollection, and that original
 		// was not flagged as dirty, then reset the target's dirty flag
 		// here after the copy operation.
 		// </p>
 		// One thing to be careful of here is a "bare" original collection
 		// in which case we should never ever ever reset the dirty flag
 		// on the target because we simply do not know...
 		if ( original instanceof PersistentCollection ) {
 			if ( result instanceof PersistentCollection ) {
 				final PersistentCollection originalPersistentCollection = (PersistentCollection) original;
 				final PersistentCollection resultPersistentCollection = (PersistentCollection) result;
 
 				preserveSnapshot( originalPersistentCollection, resultPersistentCollection, elemType, owner, copyCache, session );
 
 				if ( ! originalPersistentCollection.isDirty() ) {
 					resultPersistentCollection.clearDirty();
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private void preserveSnapshot(
 			PersistentCollection original,
 			PersistentCollection result,
 			Type elemType,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		Serializable originalSnapshot = original.getStoredSnapshot();
 		Serializable resultSnapshot = result.getStoredSnapshot();
 		Serializable targetSnapshot;
 
 		if ( originalSnapshot instanceof List ) {
 			targetSnapshot = new ArrayList(
 					( (List) originalSnapshot ).size() );
 			for ( Object obj : (List) originalSnapshot ) {
 				( (List) targetSnapshot ).add( elemType.replace( obj, null, session, owner, copyCache ) );
 			}
 
 		}
 		else if ( originalSnapshot instanceof Map ) {
 			if ( originalSnapshot instanceof SortedMap ) {
 				targetSnapshot = new TreeMap( ( (SortedMap) originalSnapshot ).comparator() );
 			}
 			else {
 				targetSnapshot = new HashMap(
 						CollectionHelper.determineProperSizing( ( (Map) originalSnapshot ).size() ),
 						CollectionHelper.LOAD_FACTOR
 				);
 			}
 
 			for ( Map.Entry<Object, Object> entry : ( (Map<Object, Object>) originalSnapshot ).entrySet() ) {
 				Object key = entry.getKey();
 				Object value = entry.getValue();
 				Object resultSnapshotValue = ( resultSnapshot == null )
 						? null
 						: ( (Map<Object, Object>) resultSnapshot ).get( key );
 
 				Object newValue = elemType.replace( value, resultSnapshotValue, session, owner, copyCache );
 
 				if ( key == value ) {
 					( (Map) targetSnapshot ).put( newValue, newValue );
 
 				}
 				else {
 					( (Map) targetSnapshot ).put( key, newValue );
 				}
 
 			}
 
 		}
 		else if ( originalSnapshot instanceof Object[] ) {
 			Object[] arr = (Object[]) originalSnapshot;
 			for ( int i = 0; i < arr.length; i++ ) {
 				arr[i] = elemType.replace( arr[i], null, session, owner, copyCache );
 			}
 			targetSnapshot = originalSnapshot;
 
 		}
 		else {
 			// retain the same snapshot
 			targetSnapshot = resultSnapshot;
 
 		}
 
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( result );
 		if ( ce != null ) {
 			ce.resetStoredSnapshot( result, targetSnapshot );
 		}
 
 	}
 
 	/**
 	 * Instantiate a new "underlying" collection exhibiting the same capacity
 	 * charactersitcs and the passed "original".
 	 *
 	 * @param original The original collection.
 	 * @return The newly instantiated collection.
 	 */
 	protected Object instantiateResult(Object original) {
 		// by default just use an unanticipated capacity since we don't
 		// know how to extract the capacity to use from original here...
 		return instantiate( -1 );
 	}
 
 	/**
 	 * Instantiate an empty instance of the "underlying" collection (not a wrapper),
 	 * but with the given anticipated size (i.e. accounting for initial capacity
 	 * and perhaps load factor).
 	 *
 	 * @param anticipatedSize The anticipated size of the instaniated collection
 	 * after we are done populating it.
 	 * @return A newly instantiated collection to be wrapped.
 	 */
 	public abstract Object instantiate(int anticipatedSize);
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			final Object original,
 			final Object target,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		if ( !Hibernate.isInitialized( original ) ) {
 			return target;
 		}
 
 		// for a null target, or a target which is the same as the original, we
 		// need to put the merged elements in a new collection
 		Object result = target == null || target == original ? instantiateResult( original ) : target;
 		
 		//for arrays, replaceElements() may return a different reference, since
 		//the array length might not match
 		result = replaceElements( original, result, owner, copyCache, session );
 
 		if ( original == target ) {
 			// get the elements back into the target making sure to handle dirty flag
 			boolean wasClean = PersistentCollection.class.isInstance( target ) && !( ( PersistentCollection ) target ).isDirty();
 			//TODO: this is a little inefficient, don't need to do a whole
 			//      deep replaceElements() call
 			replaceElements( result, target, owner, copyCache, session );
 			if ( wasClean ) {
 				( ( PersistentCollection ) target ).clearDirty();
 			}
 			result = target;
 		}
 
 		return result;
 	}
 
 	/**
 	 * Get the Hibernate type of the collection elements
 	 *
 	 * @param factory The session factory.
 	 * @return The type of the collection elements
 	 * @throws MappingException Indicates the underlying persister could not be located.
 	 */
 	public final Type getElementType(SessionFactoryImplementor factory) throws MappingException {
 		return factory.getCollectionPersister( getRole() ).getElementType();
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
+	@Override
+	public String getOnCondition(
+			String alias,
+			SessionFactoryImplementor factory,
+			Map enabledFilters,
+			Set<String> treatAsDeclarations) {
+		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
+	}
+
 	/**
 	 * instantiate a collection wrapper (called when loading an object)
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @param owner The collection owner
 	 * @return The collection
 	 */
 	public Object getCollection(Serializable key, SessionImplementor session, Object owner) {
 
 		CollectionPersister persister = getPersister( session );
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
 
 		// check if collection is currently being loaded
 		PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection( persister, key );
 		
 		if ( collection == null ) {
 			
 			// check if it is already completely loaded, but unowned
 			collection = persistenceContext.useUnownedCollection( new CollectionKey(persister, key, entityMode) );
 			
 			if ( collection == null ) {
 				// create a new collection wrapper, to be initialized later
 				collection = instantiate( session, persister, key );
 				
 				collection.setOwner(owner);
 	
 				persistenceContext.addUninitializedCollection( persister, collection, key );
 	
 				// some collections are not lazy:
 				if ( initializeImmediately() ) {
 					session.initializeCollection( collection, false );
 				}
 				else if ( !persister.isLazy() ) {
 					persistenceContext.addNonLazyCollection( collection );
 				}
 	
 				if ( hasHolder() ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 				
 			}
 			
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracef( "Created collection wrapper: %s",
 						MessageHelper.collectionInfoString( persister, collection,
 								key, session ) );
 			}
 			
 		}
 		
 		collection.setOwner(owner);
 
 		return collection.getValue();
 	}
 
 	public boolean hasHolder() {
 		return false;
 	}
 
 	protected boolean initializeImmediately() {
 		return false;
 	}
 
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
 	public boolean isXMLElement() {
 		return true;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			node.detach();
 		}
 		else {
 			replaceNode( node, (Element) value );
 		}
 	}
 	
 	/**
 	 * We always need to dirty check the collection because we sometimes 
 	 * need to incremement version number of owner and also because of 
 	 * how assemble/disassemble is implemented for uks
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
index 68bee2f524..6944f6df37 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,727 +1,737 @@
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
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
+import java.util.Set;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.tuple.ElementWrapper;
 
 /**
  * Base for types which map associations to persistent entities.
  *
  * @author Gavin King
  */
 public abstract class EntityType extends AbstractType implements AssociationType {
 
 	private final TypeFactory.TypeScope scope;
 	private final String associatedEntityName;
 	protected final String uniqueKeyPropertyName;
 	protected final boolean isEmbeddedInXML;
 	private final boolean eager;
 	private final boolean unwrapProxy;
 	private final boolean referenceToPrimaryKey;
 
 	private transient Class returnedClass;
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param isEmbeddedInXML Should values of this mapping be embedded in XML modes?
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 *
 	 * @deprecated Use {@link #EntityType(org.hibernate.type.TypeFactory.TypeScope, String, boolean, String, boolean, boolean)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean isEmbeddedInXML,
 			boolean unwrapProxy) {
 		this( scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy );
 	}
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 * 
 	 * @deprecated Use {@link #EntityType(org.hibernate.type.TypeFactory.TypeScope, String, boolean, String, boolean, boolean)} instead.
 	 */
 	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
 		this( scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy );
 	}
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param referenceToPrimaryKey True if association references a primary key.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 */
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
 		this.scope = scope;
 		this.associatedEntityName = entityName;
 		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
 		this.isEmbeddedInXML = true;
 		this.eager = eager;
 		this.unwrapProxy = unwrapProxy;
 		this.referenceToPrimaryKey = referenceToPrimaryKey;
 	}
 
 	protected TypeFactory.TypeScope scope() {
 		return scope;
 	}
 
 	/**
 	 * An entity type is a type of association type
 	 *
 	 * @return True.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	/**
 	 * Explicitly, an entity type is an entity type ;)
 	 *
 	 * @return True.
 	 */
 	public final boolean isEntityType() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isMutable() {
 		return false;
 	}
 
 	/**
 	 * Generates a string representation of this type.
 	 *
 	 * @return string rep
 	 */
 	public String toString() {
 		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
 	}
 
 	/**
 	 * For entity types, the name correlates to the associated entity name.
 	 */
 	public String getName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * Does this association foreign key reference the primary key of the other table?
 	 * Otherwise, it references a property-ref.
 	 *
 	 * @return True if this association reference the PK of the associated entity.
 	 */
 	public boolean isReferenceToPrimaryKey() {
 		return referenceToPrimaryKey;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		// Return null if this type references a PK.  This is important for
 		// associations' use of mappedBy referring to a derived ID.
 		return referenceToPrimaryKey ? null : uniqueKeyPropertyName;
 	}
 
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public String getPropertyName() {
 		return null;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @return The associated entity name.
 	 */
 	public final String getAssociatedEntityName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @param factory The session factory, for resolution.
 	 * @return The associated entity name.
 	 */
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		return getAssociatedEntityName();
 	}
 
 	/**
 	 * Retrieves the {@link Joinable} defining the associated entity.
 	 *
 	 * @param factory The session factory.
 	 * @return The associated joinable
 	 * @throws MappingException Generally indicates an invalid entity name.
 	 */
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
 		return ( Joinable ) factory.getEntityPersister( associatedEntityName );
 	}
 
 	/**
 	 * This returns the wrong class for an entity with a proxy, or for a named
 	 * entity.  Theoretically it should return the proxy class, but it doesn't.
 	 * <p/>
 	 * The problem here is that we do not necessarily have a ref to the associated
 	 * entity persister (nor to the session factory, to look it up) which is really
 	 * needed to "do the right thing" here...
 	 *
 	 * @return The entiyt class.
 	 */
 	public final Class getReturnedClass() {
 		if ( returnedClass == null ) {
 			returnedClass = determineAssociatedEntityClass();
 		}
 		return returnedClass;
 	}
 
     private Class determineAssociatedEntityClass() {
         final String entityName = getAssociatedEntityName();
         try {
             return ReflectHelper.classForName(entityName);
         }
         catch ( ClassNotFoundException cnfe ) {
             return this.scope.resolveFactory().getEntityPersister(entityName).
                 getEntityTuplizer().getMappedClass();
         }
     }
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return resolve( hydrate(rs, names, session, owner), session, owner );
 	}
 
 	/**
 	 * Two entities are considered the same when their instances are the same.
 	 *
 	 *
 	 * @param x One entity instance
 	 * @param y Another entity instance
 	 * @return True if x == y; false otherwise.
 	 */
 	public final boolean isSame(Object x, Object y) {
 		return x == y;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value; //special case ... this is the leaf of the containment graph, even though not immutable
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		Object cached = copyCache.get(original);
 		if ( cached != null ) {
 			return cached;
 		}
 		else {
 			if ( original == target ) {
 				return target;
 			}
 			if ( session.getContextEntityIdentifier( original ) == null  &&
 					ForeignKeys.isTransient( associatedEntityName, original, Boolean.FALSE, session ) ) {
 				final Object copy = session.getEntityPersister( associatedEntityName, original )
 						.instantiate( null, session );
 				copyCache.put( original, copy );
 				return copy;
 			}
 			else {
 				Object id = getIdentifier( original, session );
 				if ( id == null ) {
 					throw new AssertionFailure("non-transient entity has a null id");
 				}
 				id = getIdentifierOrUniqueKeyType( session.getFactory() )
 						.replace(id, null, session, owner, copyCache);
 				return resolve( id, session, owner );
 			}
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.getHashCode( x );
 		}
 
 		final Serializable id;
 		if (x instanceof HibernateProxy) {
 			id = ( (HibernateProxy) x ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			final Class mappedClass = persister.getMappedClass();
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				id = persister.getIdentifier( x );
 			}
 			else {
 				id = (Serializable) x;
 			}
 		}
 		return persister.getIdentifierType().getHashCode( id, factory );
 	}
 
 	@Override
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		// associations (many-to-one and one-to-one) can be null...
 		if ( x == null || y == null ) {
 			return x == y;
 		}
 
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.isEqual(x, y );
 		}
 
 		final Class mappedClass = persister.getMappedClass();
 		Serializable xid;
 		if (x instanceof HibernateProxy) {
 			xid = ( (HibernateProxy) x ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				xid = persister.getIdentifier( x );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				xid = (Serializable) x;
 			}
 		}
 
 		Serializable yid;
 		if (y instanceof HibernateProxy) {
 			yid = ( (HibernateProxy) y ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( y.getClass() ) ) {
 				yid = persister.getIdentifier( y );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				yid = (Serializable) y;
 			}
 		}
 
 		return persister.getIdentifierType()
 				.isEqual(xid, yid, factory);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isXMLElement() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			return getIdentifierType(factory).fromXMLNode(xml, factory);
 		}
 		else {
 			return xml;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			getIdentifierType(factory).setToXMLNode(node, value, factory);
 		}
 		else {
 			Element elt = (Element) value;
 			replaceNode( node, new ElementWrapper(elt) );
 		}
 	}
 
-	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
-	throws MappingException {
-		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
+	@Override
+	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
+		return getOnCondition( alias, factory, enabledFilters, null );
+	}
+
+	@Override
+	public String getOnCondition(
+			String alias,
+			SessionFactoryImplementor factory,
+			Map enabledFilters,
+			Set<String> treatAsDeclarations) {
+		if ( isReferenceToPrimaryKey() && ( treatAsDeclarations == null || treatAsDeclarations.isEmpty() ) ) {
 			return "";
 		}
 		else {
-			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
+			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
 		}
 	}
 
 	/**
 	 * Resolve an identifier or unique key value
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		if ( isNotEmbedded( session ) ) {
 			return value;
 		}
 
 		if ( value != null && !isNull( owner, session ) ) {
 			if ( isReferenceToPrimaryKey() ) {
 				return resolveIdentifier( (Serializable) value, session );
 			}
 			else if ( uniqueKeyPropertyName != null ) {
 				return loadByUniqueKey( getAssociatedEntityName(), uniqueKeyPropertyName, value, session );
 			}
 		}
 		
 		return null;
 	}
 
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return factory.getEntityPersister( associatedEntityName ).getIdentifierType();
 	}
 
 	protected final Object getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		if ( isNotEmbedded(session) ) {
 			return value;
 		}
 
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved( getAssociatedEntityName(), value, session ); //tolerates nulls
 		}
 		else if ( value == null ) {
 			return null;
 		}
 		else {
 			EntityPersister entityPersister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
 			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName );
 			// We now have the value of the property-ref we reference.  However,
 			// we need to dig a little deeper, as that property might also be
 			// an entity type, in which case we need to resolve its identitifier
 			Type type = entityPersister.getPropertyType( uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				propertyValue = ( ( EntityType ) type ).getIdentifier( propertyValue, session );
 			}
 
 			return propertyValue;
 		}
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	protected boolean isNotEmbedded(SessionImplementor session) {
 //		return !isEmbeddedInXML;
 		return false;
 	}
 
 	/**
 	 * Generate a loggable representation of an instance of the value mapped by this type.
 	 *
 	 * @param value The instance to be logged.
 	 * @param factory The session factory.
 	 * @return The loggable string.
 	 * @throws HibernateException Generally some form of resolution problem.
 	 */
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		if ( value == null ) {
 			return "null";
 		}
 		
 		EntityPersister persister = factory.getEntityPersister( associatedEntityName );
 		StringBuilder result = new StringBuilder().append( associatedEntityName );
 
 		if ( persister.hasIdentifierProperty() ) {
 			final EntityMode entityMode = persister.getEntityMode();
 			final Serializable id;
 			if ( entityMode == null ) {
 				if ( isEmbeddedInXML ) {
 					throw new ClassCastException( value.getClass().getName() );
 				}
 				id = ( Serializable ) value;
 			} else if ( value instanceof HibernateProxy ) {
 				HibernateProxy proxy = ( HibernateProxy ) value;
 				id = proxy.getHibernateLazyInitializer().getIdentifier();
 			}
 			else {
 				id = persister.getIdentifier( value );
 			}
 			
 			result.append( '#' )
 				.append( persister.getIdentifierType().toLoggableString( id, factory ) );
 		}
 		
 		return result.toString();
 	}
 
 	/**
 	 * Is the association modeled here defined as a 1-1 in the database (physical model)?
 	 *
 	 * @return True if a 1-1 in the database; false otherwise.
 	 */
 	public abstract boolean isOneToOne();
 
 	/**
 	 * Is the association modeled here a 1-1 according to the logical moidel?
 	 *
 	 * @return True if a 1-1 in the logical model; false otherwise.
 	 */
 	public boolean isLogicalOneToOne() {
 		return isOneToOne();
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param factory The mappings...
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(Mapping factory) {
 		return factory.getIdentifierType( getAssociatedEntityName() );
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param session The originating session
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(SessionImplementor session) {
 		return getIdentifierType( session.getFactory() );
 	}
 
 	/**
 	 * Determine the type of either (1) the identifier if we reference the
 	 * associated entity's PK or (2) the unique key to which we refer (i.e.
 	 * the property-ref).
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate type.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 * or unique key property name.
 	 */
 	public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return getIdentifierType(factory);
 		}
 		else {
 			Type type = factory.getReferencedPropertyType( getAssociatedEntityName(), uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				type = ( ( EntityType ) type).getIdentifierOrUniqueKeyType( factory );
 			}
 			return type;
 		}
 	}
 
 	/**
 	 * The name of the property on the associated entity to which our FK
 	 * refers
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate property name.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 */
 	public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory)
 	throws MappingException {
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return factory.getIdentifierPropertyName( getAssociatedEntityName() );
 		}
 		else {
 			return uniqueKeyPropertyName;
 		}
 	}
 	
 	protected abstract boolean isNullable();
 
 	/**
 	 * Resolve an identifier via a load.
 	 *
 	 * @param id The entity id to resolve
 	 * @param session The orginating session.
 	 * @return The resolved identifier (i.e., loaded entity).
 	 * @throws org.hibernate.HibernateException Indicates problems performing the load.
 	 */
 	protected final Object resolveIdentifier(Serializable id, SessionImplementor session) throws HibernateException {
 		boolean isProxyUnwrapEnabled = unwrapProxy &&
 				session.getFactory()
 						.getEntityPersister( getAssociatedEntityName() )
 						.isInstrumented();
 
 		Object proxyOrEntity = session.internalLoad(
 				getAssociatedEntityName(),
 				id,
 				eager,
 				isNullable() && !isProxyUnwrapEnabled
 		);
 
 		if ( proxyOrEntity instanceof HibernateProxy ) {
 			( ( HibernateProxy ) proxyOrEntity ).getHibernateLazyInitializer()
 					.setUnwrap( isProxyUnwrapEnabled );
 		}
 
 		return proxyOrEntity;
 	}
 
 	protected boolean isNull(Object owner, SessionImplementor session) {
 		return false;
 	}
 
 	/**
 	 * Load an instance by a unique key that is not the primary key.
 	 *
 	 * @param entityName The name of the entity to load
 	 * @param uniqueKeyPropertyName The name of the property defining the uniqie key.
 	 * @param key The unique key property value.
 	 * @param session The originating session.
 	 * @return The loaded entity
 	 * @throws HibernateException generally indicates problems performing the load.
 	 */
 	public Object loadByUniqueKey(
 			String entityName, 
 			String uniqueKeyPropertyName, 
 			Object key, 
 			SessionImplementor session) throws HibernateException {
 		final SessionFactoryImplementor factory = session.getFactory();
 		UniqueKeyLoadable persister = ( UniqueKeyLoadable ) factory.getEntityPersister( entityName );
 
 		//TODO: implement caching?! proxies?!
 
 		EntityUniqueKey euk = new EntityUniqueKey(
 				entityName, 
 				uniqueKeyPropertyName, 
 				key, 
 				getIdentifierOrUniqueKeyType( factory ),
 				persister.getEntityMode(),
 				session.getFactory()
 		);
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		Object result = persistenceContext.getEntity( euk );
 		if ( result == null ) {
 			result = persister.loadByUniqueKey( uniqueKeyPropertyName, key, session );
 		}
 		return result == null ? null : persistenceContext.proxyFor( result );
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java b/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java
index 4c6ad98c23..17a675d0fd 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/ql/TreatKeywordTest.java
@@ -1,43 +1,310 @@
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
 package org.hibernate.test.jpa.ql;
 
+import javax.persistence.DiscriminatorColumn;
+import javax.persistence.DiscriminatorType;
+import javax.persistence.DiscriminatorValue;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.Id;
+import javax.persistence.Inheritance;
+import javax.persistence.InheritanceType;
+import javax.persistence.ManyToOne;
+import javax.persistence.Table;
+import java.util.List;
+
 import org.hibernate.Session;
 
 import org.junit.Test;
 
-import org.hibernate.test.jpa.AbstractJPATest;
+import org.hibernate.testing.FailureExpected;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
-public class TreatKeywordTest extends AbstractJPATest {
+public class TreatKeywordTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] {
+				JoinedEntity.class, JoinedEntitySubclass.class, JoinedEntitySubSubclass.class,
+				JoinedEntitySubclass2.class, JoinedEntitySubSubclass2.class,
+				DiscriminatorEntity.class, DiscriminatorEntitySubclass.class, DiscriminatorEntitySubSubclass.class
+		};
+	}
+
 	@Test
-	public void testUsageInSelect() {
+	public void testBasicUsageInJoin() {
+		// todo : assert invalid naming of non-subclasses in TREAT statement
 		Session s = openSession();
-		s.createQuery( "from MyEntity e join treat(e.other as MySubclassEntity) o" ).list();
-		s.createQuery( "from MyEntity e join TREAT(e.other as MySubclassEntity) o" ).list();
+
+		s.createQuery( "from DiscriminatorEntity e join treat(e.other as DiscriminatorEntitySubclass) o" ).list();
+		s.createQuery( "from DiscriminatorEntity e join treat(e.other as DiscriminatorEntitySubSubclass) o" ).list();
+		s.createQuery( "from DiscriminatorEntitySubclass e join treat(e.other as DiscriminatorEntitySubSubclass) o" ).list();
+
+		s.createQuery( "from JoinedEntity e join treat(e.other as JoinedEntitySubclass) o" ).list();
+		s.createQuery( "from JoinedEntity e join treat(e.other as JoinedEntitySubSubclass) o" ).list();
+		s.createQuery( "from JoinedEntitySubclass e join treat(e.other as JoinedEntitySubSubclass) o" ).list();
+
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-8637" )
+	public void testFilteringDiscriminatorSubclasses() {
+		Session s = openSession();
+		s.beginTransaction();
+		DiscriminatorEntity root = new DiscriminatorEntity( 1, "root" );
+		s.save( root );
+		DiscriminatorEntitySubclass child = new DiscriminatorEntitySubclass( 2, "child", root );
+		s.save( child );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+
+		// in select clause
+		List result = s.createQuery( "select e from DiscriminatorEntity e" ).list();
+		assertEquals( 2, result.size() );
+		result = s.createQuery( "select treat (e as DiscriminatorEntitySubclass) from DiscriminatorEntity e" ).list();
+		assertEquals( 1, result.size() );
+		result = s.createQuery( "select treat (e as DiscriminatorEntitySubSubclass) from DiscriminatorEntity e" ).list();
+		assertEquals( 0, result.size() );
+
+		// in join
+		result = s.createQuery( "from DiscriminatorEntity e inner join e.other" ).list();
+		assertEquals( 1, result.size() );
+		result = s.createQuery( "from DiscriminatorEntity e inner join treat (e.other as DiscriminatorEntitySubclass)" ).list();
+		assertEquals( 0, result.size() );
+		result = s.createQuery( "from DiscriminatorEntity e inner join treat (e.other as DiscriminatorEntitySubSubclass)" ).list();
+		assertEquals( 0, result.size() );
+
 		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		s.delete( root );
+		s.delete( child );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-8637" )
+	public void testFilteringJoinedSubclasses() {
+		Session s = openSession();
+		s.beginTransaction();
+		JoinedEntity root = new JoinedEntity( 1, "root" );
+		s.save( root );
+		JoinedEntitySubclass child = new JoinedEntitySubclass( 2, "child", root );
+		s.save( child );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+
+		// in the select clause which causes an implicit inclusion of subclass joins, the test here makes sure that
+		// the TREAT-AS effects the join-type used.
+		List result = s.createQuery( "select e from JoinedEntity e" ).list();
+		assertEquals( 2, result.size() );
+		result = s.createQuery( "select treat (e as JoinedEntitySubclass) from JoinedEntity e" ).list();
+		assertEquals( 1, result.size() );
+		result = s.createQuery( "select treat (e as JoinedEntitySubSubclass) from JoinedEntity e" ).list();
+		assertEquals( 0, result.size() );
+
+		// in join
+		result = s.createQuery( "from JoinedEntity e inner join e.other" ).list();
+		assertEquals( 1, result.size() );
+		result = s.createQuery( "from JoinedEntity e inner join treat (e.other as JoinedEntitySubclass)" ).list();
+		assertEquals( 0, result.size() );
+		result = s.createQuery( "from JoinedEntity e inner join treat (e.other as JoinedEntitySubSubclass)" ).list();
+		assertEquals( 0, result.size() );
+
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		s.delete( child );
+		s.delete( root );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Entity( name = "JoinedEntity" )
+	@Table( name = "JoinedEntity" )
+	@Inheritance( strategy = InheritanceType.JOINED )
+	public static class JoinedEntity {
+		@Id
+		public Integer id;
+		public String name;
+		@ManyToOne( fetch = FetchType.LAZY )
+		public JoinedEntity other;
+
+		public JoinedEntity() {
+		}
+
+		public JoinedEntity(Integer id, String name) {
+			this.id = id;
+			this.name = name;
+		}
+
+		public JoinedEntity(Integer id, String name, JoinedEntity other) {
+			this.id = id;
+			this.name = name;
+			this.other = other;
+		}
+	}
+
+	@Entity( name = "JoinedEntitySubclass" )
+	@Table( name = "JoinedEntitySubclass" )
+	public static class JoinedEntitySubclass extends JoinedEntity {
+		public JoinedEntitySubclass() {
+		}
+
+		public JoinedEntitySubclass(Integer id, String name) {
+			super( id, name );
+		}
+
+		public JoinedEntitySubclass(Integer id, String name, JoinedEntity other) {
+			super( id, name, other );
+		}
+	}
+
+	@Entity( name = "JoinedEntitySubSubclass" )
+	@Table( name = "JoinedEntitySubSubclass" )
+	public static class JoinedEntitySubSubclass extends JoinedEntitySubclass {
+		public JoinedEntitySubSubclass() {
+		}
+
+		public JoinedEntitySubSubclass(Integer id, String name) {
+			super( id, name );
+		}
+
+		public JoinedEntitySubSubclass(Integer id, String name, JoinedEntity other) {
+			super( id, name, other );
+		}
+	}
+
+	@Entity( name = "JoinedEntitySubclass2" )
+	@Table( name = "JoinedEntitySubclass2" )
+	public static class JoinedEntitySubclass2 extends JoinedEntity {
+		public JoinedEntitySubclass2() {
+		}
+
+		public JoinedEntitySubclass2(Integer id, String name) {
+			super( id, name );
+		}
+
+		public JoinedEntitySubclass2(Integer id, String name, JoinedEntity other) {
+			super( id, name, other );
+		}
+	}
+
+	@Entity( name = "JoinedEntitySubSubclass2" )
+	@Table( name = "JoinedEntitySubSubclass2" )
+	public static class JoinedEntitySubSubclass2 extends JoinedEntitySubclass2 {
+		public JoinedEntitySubSubclass2() {
+		}
+
+		public JoinedEntitySubSubclass2(Integer id, String name) {
+			super( id, name );
+		}
+
+		public JoinedEntitySubSubclass2(Integer id, String name, JoinedEntity other) {
+			super( id, name, other );
+		}
+	}
+
+	@Entity( name = "DiscriminatorEntity" )
+	@Table( name = "DiscriminatorEntity" )
+	@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
+	@DiscriminatorColumn( name = "e_type", discriminatorType = DiscriminatorType.STRING )
+	@DiscriminatorValue( "B" )
+	public static class DiscriminatorEntity {
+		@Id
+		public Integer id;
+		public String name;
+		@ManyToOne( fetch = FetchType.LAZY )
+		public DiscriminatorEntity other;
+
+		public DiscriminatorEntity() {
+		}
+
+		public DiscriminatorEntity(Integer id, String name) {
+			this.id = id;
+			this.name = name;
+		}
+
+		public DiscriminatorEntity(
+				Integer id,
+				String name,
+				DiscriminatorEntity other) {
+			this.id = id;
+			this.name = name;
+			this.other = other;
+		}
+	}
+
+	@Entity( name = "DiscriminatorEntitySubclass" )
+	@DiscriminatorValue( "S" )
+	public static class DiscriminatorEntitySubclass extends DiscriminatorEntity {
+		public DiscriminatorEntitySubclass() {
+		}
+
+		public DiscriminatorEntitySubclass(Integer id, String name) {
+			super( id, name );
+		}
+
+		public DiscriminatorEntitySubclass(
+				Integer id,
+				String name,
+				DiscriminatorEntity other) {
+			super( id, name, other );
+		}
+	}
+
+	@Entity( name = "DiscriminatorEntitySubSubclass" )
+	@DiscriminatorValue( "SS" )
+	public static class DiscriminatorEntitySubSubclass extends DiscriminatorEntitySubclass {
+		public DiscriminatorEntitySubSubclass() {
+		}
+
+		public DiscriminatorEntitySubSubclass(Integer id, String name) {
+			super( id, name );
+		}
+
+		public DiscriminatorEntitySubSubclass(
+				Integer id,
+				String name,
+				DiscriminatorEntity other) {
+			super( id, name, other );
+		}
 	}
 }
