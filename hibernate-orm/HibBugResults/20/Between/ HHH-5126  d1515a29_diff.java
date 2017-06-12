diff --git a/hibernate-core/src/main/antlr/hql.g b/hibernate-core/src/main/antlr/hql.g
index 855715c9a4..5fe8917b39 100644
--- a/hibernate-core/src/main/antlr/hql.g
+++ b/hibernate-core/src/main/antlr/hql.g
@@ -1,899 +1,904 @@
 header
 {
 //   $Id: hql.g 10163 2006-07-26 15:07:50Z steve.ebersole@jboss.com $
 
 package org.hibernate.hql.antlr;
 
 import org.hibernate.hql.ast.*;
 import org.hibernate.hql.ast.util.*;
 
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
 
 	public void weakKeywords() throws TokenStreamException { }
 
 	public void processMemberOf(Token n,AST p,ASTPair currentAST) { }
 
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
 	  path (asAlias)? (propertyFetch)? (withClause)?
 	;
 
 withClause
 	: WITH^ logicalExpression
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
 	: expression ( ascendingOrDescending )?
 	;
 
 ascendingOrDescending
 	: ( "asc" | "ascending" )	{ #ascendingOrDescending.setType(ASCENDING); }
 	| ( "desc" | "descending") 	{ #ascendingOrDescending.setType(DESCENDING); }
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
 // ident qualifier ('.' ident ), array index ( [ expr ] ),
 // method call ( '.' ident '(' exprList ') )
 atom
 	 : primaryExpression
 		(
 			DOT^ identifier
 				( options { greedy=true; } :
 					( op:OPEN^ {#op.setType(METHOD_CALL);} exprList CLOSE! ) )?
 		|	lb:OPEN_BRACKET^ {#lb.setType(INDEX_OP);} expression CLOSE_BRACKET!
 		)*
 	;
 
 // level 0 - the basic element of an expression
 primaryExpression
 	:   identPrimary ( options {greedy=true;} : DOT^ "class" )?
 	|   constant
-	|   COLON^ identifier
+	|   parameter
 	// TODO: Add parens to the tree so the user can control the operator evaluation order.
 	|   OPEN! (expressionOrVector | subQuery) CLOSE!
-	|   PARAM^ (NUM_INT)?
+	;
+
+parameter
+	: COLON^ identifier
+	| PARAM^ (NUM_INT)?
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
     : i:identifier { handleDotIdent(); }
 			( options { greedy=true; } : DOT^ ( identifier | ELEMENTS | o:OBJECT { #o.setType(IDENT); } ) )*
 			( options { greedy=true; } :
 				( op:OPEN^ { #op.setType(METHOD_CALL);} e:exprList CLOSE! ) {
 				    AST path = #e.getFirstChild();
 				    if ( #i.getText().equals( "key" ) ) {
 				        #identPrimary = #( [KEY], path );
 				    }
 				    else if ( #i.getText().equals( "value" ) ) {
 				        #identPrimary = #( [VALUE], path );
 				    }
 				    else if ( #i.getText().equals( "entry" ) ) {
 				        #identPrimary = #( [ENTRY], path );
 				    }
 				}
 			)?
 	// Also allow special 'aggregate functions' such as count(), avg(), etc.
 	| aggregate
 	;
 
 aggregate
 	: ( SUM^ | AVG^ | MAX^ | MIN^ ) OPEN! additiveExpression CLOSE! { #aggregate.setType(AGGREGATE); }
 	// Special case for count - It's 'parameters' can be keywords.
 	|  COUNT^ OPEN! ( STAR { #STAR.setType(ROW_STAR); } | ( ( DISTINCT | ALL )? ( path | collectionExpr ) ) ) CLOSE!
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
 	| (OPEN! ( (expression (COMMA! expression)*) | subQuery ) CLOSE!)
+	| parameter
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
index cac26b4569..e822accfce 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
@@ -1,924 +1,956 @@
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
 package org.hibernate.impl;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueResultException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.engine.query.ParameterMetadata;
 import org.hibernate.hql.classic.ParserHelper;
 import org.hibernate.property.Getter;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.SerializableType;
 import org.hibernate.type.Type;
 import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.MarkerObject;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 
 /**
  * Abstract implementation of the Query interface.
  *
  * @author Gavin King
  * @author Max Andersen
  */
 public abstract class AbstractQueryImpl implements Query {
 
 	private static final Object UNSET_PARAMETER = new MarkerObject("<unset parameter>");
 	private static final Object UNSET_TYPE = new MarkerObject("<unset type>");
 
 	private final String queryString;
 	protected final SessionImplementor session;
 	protected final ParameterMetadata parameterMetadata;
 
 	// parameter bind values...
 	private List values = new ArrayList(4);
 	private List types = new ArrayList(4);
 	private Map namedParameters = new HashMap(4);
 	private Map namedParameterLists = new HashMap(4);
 
 	private Object optionalObject;
 	private Serializable optionalId;
 	private String optionalEntityName;
 
 	private RowSelection selection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
 	private FlushMode flushMode;
 	private CacheMode cacheMode;
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 	private Serializable collectionKey;
 	private Boolean readOnly;
 	private ResultTransformer resultTransformer;
 
 	public AbstractQueryImpl(
 			String queryString,
 	        FlushMode flushMode,
 	        SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		this.session = session;
 		this.queryString = queryString;
 		this.selection = new RowSelection();
 		this.flushMode = flushMode;
 		this.cacheMode = null;
 		this.parameterMetadata = parameterMetadata;
 	}
 
 	public ParameterMetadata getParameterMetadata() {
 		return parameterMetadata;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + queryString + ')';
 	}
 
 	public final String getQueryString() {
 		return queryString;
 	}
 
 	//TODO: maybe call it getRowSelection() ?
 	public RowSelection getSelection() {
 		return selection;
 	}
 	
 	public Query setFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 	
 	public Query setCacheMode(CacheMode cacheMode) {
 		this.cacheMode = cacheMode;
 		return this;
 	}
 
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
 	public Query setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	public Query setCacheRegion(String cacheRegion) {
 		if (cacheRegion != null)
 			this.cacheRegion = cacheRegion.trim();
 		return this;
 	}
 
 	public Query setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 	public Query setFirstResult(int firstResult) {
 		selection.setFirstRow( new Integer(firstResult) );
 		return this;
 	}
 
 	public Query setMaxResults(int maxResults) {
 		if ( maxResults < 0 ) {
 			// treat negatives specically as meaning no limit...
 			selection.setMaxRows( null );
 		}
 		else {
 			selection.setMaxRows( new Integer(maxResults) );
 		}
 		return this;
 	}
 
 	public Query setTimeout(int timeout) {
 		selection.setTimeout( new Integer(timeout) );
 		return this;
 	}
 	public Query setFetchSize(int fetchSize) {
 		selection.setFetchSize( new Integer(fetchSize) );
 		return this;
 	}
 
 	public Type[] getReturnTypes() throws HibernateException {
 		return session.getFactory().getReturnTypes( queryString );
 	}
 
 	public String[] getReturnAliases() throws HibernateException {
 		return session.getFactory().getReturnAliases( queryString );
 	}
 
 	public Query setCollectionKey(Serializable collectionKey) {
 		this.collectionKey = collectionKey;
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isReadOnly() {
 		return ( readOnly == null ?
 				getSession().getPersistenceContext().isDefaultReadOnly() :
 				readOnly.booleanValue() 
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Query setReadOnly(boolean readOnly) {
 		this.readOnly = Boolean.valueOf( readOnly );
 		return this;
 	}
 
 	public Query setResultTransformer(ResultTransformer transformer) {
 		this.resultTransformer = transformer;
 		return this;
 	}
 	
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	SessionImplementor getSession() {
 		return session;
 	}
 
 	public abstract LockOptions getLockOptions();
 
 
 	// Parameter handling code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns a shallow copy of the named parameter value map.
 	 *
 	 * @return Shallow copy of the named parameter value map
 	 */
 	protected Map getNamedParams() {
 		return new HashMap( namedParameters );
 	}
 
 	/**
 	 * Returns an array representing all named parameter names encountered
 	 * during (intial) parsing of the query.
 	 * <p/>
 	 * Note <i>initial</i> here means different things depending on whether
 	 * this is a native-sql query or an HQL/filter query.  For native-sql, a
 	 * precursory inspection of the query string is performed specifically to
 	 * locate defined parameters.  For HQL/filter queries, this is the
 	 * information returned from the query-translator.  This distinction
 	 * holds true for all parameter metadata exposed here.
 	 *
 	 * @return Array of named parameter names.
 	 * @throws HibernateException
 	 */
 	public String[] getNamedParameters() throws HibernateException {
 		return ArrayHelper.toStringArray( parameterMetadata.getNamedParameterNames() );
 	}
 
 	/**
 	 * Does this query contain named parameters?
 	 *
 	 * @return True if the query was found to contain named parameters; false
 	 * otherwise;
 	 */
 	public boolean hasNamedParameters() {
 		return parameterMetadata.getNamedParameterNames().size() > 0;
 	}
 
 	/**
 	 * Retreive the value map for any named parameter lists (i.e., for
 	 * auto-expansion) bound to this query.
 	 *
 	 * @return The parameter list value map.
 	 */
 	protected Map getNamedParameterLists() {
 		return namedParameterLists;
 	}
 
 	/**
 	 * Retreives the list of parameter values bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter values.
 	 */
 	protected List getValues() {
 		return values;
 	}
 
 	/**
 	 * Retreives the list of parameter {@link Type type}s bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter types.
 	 */
 	protected List getTypes() {
 		return types;
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @throws QueryException
 	 */
 	protected void verifyParameters() throws QueryException {
 		verifyParameters(false);
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @param reserveFirstParameter if true, the first ? will not be verified since
 	 * its needed for e.g. callable statements returning a out parameter
 	 * @throws HibernateException
 	 */
 	protected void verifyParameters(boolean reserveFirstParameter) throws HibernateException {
 		if ( parameterMetadata.getNamedParameterNames().size() != namedParameters.size() + namedParameterLists.size() ) {
 			Set missingParams = new HashSet( parameterMetadata.getNamedParameterNames() );
 			missingParams.removeAll( namedParameterLists.keySet() );
 			missingParams.removeAll( namedParameters.keySet() );
 			throw new QueryException( "Not all named parameters have been set: " + missingParams, getQueryString() );
 		}
 
 		int positionalValueSpan = 0;
 		for ( int i = 0; i < values.size(); i++ ) {
 			Object object = types.get( i );
 			if( values.get( i ) == UNSET_PARAMETER || object == UNSET_TYPE ) {
 				if ( reserveFirstParameter && i==0 ) {
 					continue;
 				}
 				else {
 					throw new QueryException( "Unset positional parameter at position: " + i, getQueryString() );
 				}
 			}
 			positionalValueSpan += ( (Type) object ).getColumnSpan( session.getFactory() );
 		}
 
 		if ( parameterMetadata.getOrdinalParameterCount() != positionalValueSpan ) {
 			if ( reserveFirstParameter && parameterMetadata.getOrdinalParameterCount() - 1 != positionalValueSpan ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		(parameterMetadata.getOrdinalParameterCount()-1) +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 			else if ( !reserveFirstParameter ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		parameterMetadata.getOrdinalParameterCount() +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 		}
 	}
 
 	public Query setParameter(int position, Object val, Type type) {
 		if ( parameterMetadata.getOrdinalParameterCount() == 0 ) {
 			throw new IllegalArgumentException("No positional parameters in query: " + getQueryString() );
 		}
 		if ( position < 0 || position > parameterMetadata.getOrdinalParameterCount() - 1 ) {
 			throw new IllegalArgumentException("Positional parameter does not exist: " + position + " in query: " + getQueryString() );
 		}
 		int size = values.size();
 		if ( position < size ) {
 			values.set( position, val );
 			types.set( position, type );
 		}
 		else {
 			// prepend value and type list with null for any positions before the wanted position.
 			for ( int i = 0; i < position - size; i++ ) {
 				values.add( UNSET_PARAMETER );
 				types.add( UNSET_TYPE );
 			}
 			values.add( val );
 			types.add( type );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val, Type type) {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		else {
 			 namedParameters.put( name, new TypedValue( type, val, session.getEntityMode() ) );
 			 return this;
 		}
 	}
 
 	public Query setParameter(int position, Object val) throws HibernateException {
 		if (val == null) {
 			setParameter( position, val, Hibernate.SERIALIZABLE );
 		}
 		else {
 			setParameter( position, val, determineType( position, val ) );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val) throws HibernateException {
 		if (val == null) {
 			Type type = parameterMetadata.getNamedParameterExpectedType( name );
 			if ( type == null ) {
 				type = Hibernate.SERIALIZABLE;
 			}
 			setParameter( name, val, type );
 		}
 		else {
 			setParameter( name, val, determineType( name, val ) );
 		}
 		return this;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Class clazz) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( clazz );
 		}
 		return type;
 	}
 
 	private Type guessType(Object param) throws HibernateException {
 		Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy( param );
 		return guessType( clazz );
 	}
 
 	private Type guessType(Class clazz) throws HibernateException {
 		String typename = clazz.getName();
 		Type type = session.getFactory().getTypeResolver().heuristicType(typename);
 		boolean serializable = type!=null && type instanceof SerializableType;
 		if (type==null || serializable) {
 			try {
 				session.getFactory().getEntityPersister( clazz.getName() );
 			}
 			catch (MappingException me) {
 				if (serializable) {
 					return type;
 				}
 				else {
 					throw new HibernateException("Could not determine a type for class: " + typename);
 				}
 			}
 			return Hibernate.entity(clazz);
 		}
 		else {
 			return type;
 		}
 	}
 
 	public Query setString(int position, String val) {
 		setParameter(position, val, Hibernate.STRING);
 		return this;
 	}
 
 	public Query setCharacter(int position, char val) {
 		setParameter(position, new Character(val), Hibernate.CHARACTER);
 		return this;
 	}
 
 	public Query setBoolean(int position, boolean val) {
 		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
 		Type typeToUse = determineType( position, valueToUse, Hibernate.BOOLEAN );
 		setParameter( position, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(int position, byte val) {
 		setParameter(position, new Byte(val), Hibernate.BYTE);
 		return this;
 	}
 
 	public Query setShort(int position, short val) {
 		setParameter(position, new Short(val), Hibernate.SHORT);
 		return this;
 	}
 
 	public Query setInteger(int position, int val) {
 		setParameter(position, new Integer(val), Hibernate.INTEGER);
 		return this;
 	}
 
 	public Query setLong(int position, long val) {
 		setParameter(position, new Long(val), Hibernate.LONG);
 		return this;
 	}
 
 	public Query setFloat(int position, float val) {
 		setParameter(position, new Float(val), Hibernate.FLOAT);
 		return this;
 	}
 
 	public Query setDouble(int position, double val) {
 		setParameter(position, new Double(val), Hibernate.DOUBLE);
 		return this;
 	}
 
 	public Query setBinary(int position, byte[] val) {
 		setParameter(position, val, Hibernate.BINARY);
 		return this;
 	}
 
 	public Query setText(int position, String val) {
 		setParameter(position, val, Hibernate.TEXT);
 		return this;
 	}
 
 	public Query setSerializable(int position, Serializable val) {
 		setParameter(position, val, Hibernate.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setDate(int position, Date date) {
 		setParameter(position, date, Hibernate.DATE);
 		return this;
 	}
 
 	public Query setTime(int position, Date date) {
 		setParameter(position, date, Hibernate.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(int position, Date date) {
 		setParameter(position, date, Hibernate.TIMESTAMP);
 		return this;
 	}
 
 	public Query setEntity(int position, Object val) {
 		setParameter( position, val, Hibernate.entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	private String resolveEntityName(Object val) {
 		if ( val == null ) {
 			throw new IllegalArgumentException( "entity for parameter binding cannot be null" );
 		}
 		return session.bestGuessEntityName( val );
 	}
 
 	public Query setLocale(int position, Locale locale) {
 		setParameter(position, locale, Hibernate.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(int position, Calendar calendar) {
 		setParameter(position, calendar, Hibernate.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(int position, Calendar calendar) {
 		setParameter(position, calendar, Hibernate.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setBinary(String name, byte[] val) {
 		setParameter(name, val, Hibernate.BINARY);
 		return this;
 	}
 
 	public Query setText(String name, String val) {
 		setParameter(name, val, Hibernate.TEXT);
 		return this;
 	}
 
 	public Query setBoolean(String name, boolean val) {
 		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
 		Type typeToUse = determineType( name, valueToUse, Hibernate.BOOLEAN );
 		setParameter( name, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(String name, byte val) {
 		setParameter(name, new Byte(val), Hibernate.BYTE);
 		return this;
 	}
 
 	public Query setCharacter(String name, char val) {
 		setParameter(name, new Character(val), Hibernate.CHARACTER);
 		return this;
 	}
 
 	public Query setDate(String name, Date date) {
 		setParameter(name, date, Hibernate.DATE);
 		return this;
 	}
 
 	public Query setDouble(String name, double val) {
 		setParameter(name, new Double(val), Hibernate.DOUBLE);
 		return this;
 	}
 
 	public Query setEntity(String name, Object val) {
 		setParameter( name, val, Hibernate.entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	public Query setFloat(String name, float val) {
 		setParameter(name, new Float(val), Hibernate.FLOAT);
 		return this;
 	}
 
 	public Query setInteger(String name, int val) {
 		setParameter(name, new Integer(val), Hibernate.INTEGER);
 		return this;
 	}
 
 	public Query setLocale(String name, Locale locale) {
 		setParameter(name, locale, Hibernate.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(String name, Calendar calendar) {
 		setParameter(name, calendar, Hibernate.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(String name, Calendar calendar) {
 		setParameter(name, calendar, Hibernate.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setLong(String name, long val) {
 		setParameter(name, new Long(val), Hibernate.LONG);
 		return this;
 	}
 
 	public Query setSerializable(String name, Serializable val) {
 		setParameter(name, val, Hibernate.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setShort(String name, short val) {
 		setParameter(name, new Short(val), Hibernate.SHORT);
 		return this;
 	}
 
 	public Query setString(String name, String val) {
 		setParameter(name, val, Hibernate.STRING);
 		return this;
 	}
 
 	public Query setTime(String name, Date date) {
 		setParameter(name, date, Hibernate.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(String name, Date date) {
 		setParameter(name, date, Hibernate.TIMESTAMP);
 		return this;
 	}
 
 	public Query setBigDecimal(int position, BigDecimal number) {
 		setParameter(position, number, Hibernate.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigDecimal(String name, BigDecimal number) {
 		setParameter(name, number, Hibernate.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigInteger(int position, BigInteger number) {
 		setParameter(position, number, Hibernate.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setBigInteger(String name, BigInteger number) {
 		setParameter(name, number, Hibernate.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		namedParameterLists.put( name, new TypedValue( type, vals, session.getEntityMode() ) );
 		return this;
 	}
 	
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	protected String expandParameterLists(Map namedParamsCopy) {
 		String query = this.queryString;
 		Iterator iter = namedParameterLists.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			query = expandParameterList( query, (String) me.getKey(), (TypedValue) me.getValue(), namedParamsCopy );
 		}
 		return query;
 	}
 
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	private String expandParameterList(String query, String name, TypedValue typedList, Map namedParamsCopy) {
 		Collection vals = (Collection) typedList.getValue();
 		Type type = typedList.getType();
-		if ( vals.size() == 1 ) {
-			// short-circuit for performance...
+
+		boolean isJpaPositionalParam = parameterMetadata.getNamedParameterDescriptor( name ).isJpaStyle();
+		String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
+		String placeholder =
+				new StringBuffer( paramPrefix.length() + name.length() )
+						.append( paramPrefix ).append(  name )
+						.toString();
+
+		if ( query == null ) {
+			return query;
+		}
+		int loc = query.indexOf( placeholder );
+
+		if ( loc < 0 ) {
+			return query;
+		}
+
+		String beforePlaceholder = query.substring( 0, loc );
+		String afterPlaceholder =  query.substring( loc + placeholder.length() );
+
+		// check if placeholder is already immediately enclosed in parentheses
+		// (ignoring whitespace)
+		boolean isEnclosedInParens =
+				StringHelper.getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' &&
+				StringHelper.getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')';
+
+		if ( vals.size() == 1  && isEnclosedInParens ) {
+			// short-circuit for performance when only 1 value and the
+			// placeholder is already enclosed in parentheses...
 			namedParamsCopy.put( name, new TypedValue( type, vals.iterator().next(), session.getEntityMode() ) );
 			return query;
 		}
 
 		StringBuffer list = new StringBuffer( 16 );
 		Iterator iter = vals.iterator();
 		int i = 0;
-		boolean isJpaPositionalParam = parameterMetadata.getNamedParameterDescriptor( name ).isJpaStyle();
 		while ( iter.hasNext() ) {
 			String alias = ( isJpaPositionalParam ? 'x' + name : name ) + i++ + '_';
 			namedParamsCopy.put( alias, new TypedValue( type, iter.next(), session.getEntityMode() ) );
 			list.append( ParserHelper.HQL_VARIABLE_PREFIX ).append( alias );
 			if ( iter.hasNext() ) {
 				list.append( ", " );
 			}
 		}
-		String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
-		return StringHelper.replace( query, paramPrefix + name, list.toString(), true );
+		return StringHelper.replace(
+				beforePlaceholder,
+				afterPlaceholder,
+				placeholder.toString(),
+				list.toString(),
+				true,
+				true
+		);
 	}
 
 	public Query setParameterList(String name, Collection vals) throws HibernateException {
 		if ( vals == null ) {
 			throw new QueryException( "Collection must be not null!" );
 		}
 
 		if( vals.size() == 0 ) {
 			setParameterList( name, vals, null );
 		}
 		else {
 			setParameterList(name, vals, determineType( name, vals.iterator().next() ) );
 		}
 
 		return this;
 	}
 
 	public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals), type );
 	}
 
 	public Query setParameterList(String name, Object[] vals) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals) );
 	}
 
 	public Query setProperties(Map map) throws HibernateException {
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 				final Object object = map.get(namedParam);
 				if(object==null) {
 					continue;
 				}
 				Class retType = object.getClass();
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 
 			
 		}
 		return this;				
 	}
 	
 	public Query setProperties(Object bean) throws HibernateException {
 		Class clazz = bean.getClass();
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 			try {
 				Getter getter = ReflectHelper.getGetter( clazz, namedParam );
 				Class retType = getter.getReturnType();
 				final Object object = getter.get( bean );
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 				 	setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 			}
 			catch (PropertyNotFoundException pnfe) {
 				// ignore
 			}
 		}
 		return this;
 	}
 
 	public Query setParameters(Object[] values, Type[] types) {
 		this.values = Arrays.asList(values);
 		this.types = Arrays.asList(types);
 		return this;
 	}
 
 
 	// Execution methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object uniqueResult() throws HibernateException {
 		return uniqueElement( list() );
 	}
 
 	static Object uniqueElement(List list) throws NonUniqueResultException {
 		int size = list.size();
 		if (size==0) return null;
 		Object first = list.get(0);
 		for ( int i=1; i<size; i++ ) {
 			if ( list.get(i)!=first ) {
 				throw new NonUniqueResultException( list.size() );
 			}
 		}
 		return first;
 	}
 
 	protected RowSelection getRowSelection() {
 		return selection;
 	}
 
 	public Type[] typeArray() {
 		return ArrayHelper.toTypeArray( getTypes() );
 	}
 	
 	public Object[] valueArray() {
 		return getValues().toArray();
 	}
 
 	public QueryParameters getQueryParameters(Map namedParams) {
 		return new QueryParameters(
 				typeArray(),
 				valueArray(),
 				namedParams,
 				getLockOptions(),
 				getSelection(),
 				true,
 				isReadOnly(),
 				cacheable,
 				cacheRegion,
 				comment,
 				collectionKey == null ? null : new Serializable[] { collectionKey },
 				optionalObject,
 				optionalEntityName,
 				optionalId,
 				resultTransformer
 		);
 	}
 	
 	protected void before() {
 		if ( flushMode!=null ) {
 			sessionFlushMode = getSession().getFlushMode();
 			getSession().setFlushMode(flushMode);
 		}
 		if ( cacheMode!=null ) {
 			sessionCacheMode = getSession().getCacheMode();
 			getSession().setCacheMode(cacheMode);
 		}
 	}
 	
 	protected void after() {
 		if (sessionFlushMode!=null) {
 			getSession().setFlushMode(sessionFlushMode);
 			sessionFlushMode = null;
 		}
 		if (sessionCacheMode!=null) {
 			getSession().setCacheMode(sessionCacheMode);
 			sessionCacheMode = null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
index e0146035ab..68f82230b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/util/StringHelper.java
@@ -1,599 +1,659 @@
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
 package org.hibernate.util;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.StringTokenizer;
 import org.hibernate.dialect.Dialect;
 
 public final class StringHelper {
 
 	private static final int ALIAS_TRUNCATE_LENGTH = 10;
 	public static final String WHITESPACE = " \n\r\f\t";
 
 	private StringHelper() { /* static methods only - hide constructor */
 	}
 	
 	/*public static boolean containsDigits(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			if ( Character.isDigit( string.charAt(i) ) ) return true;
 		}
 		return false;
 	}*/
 
 	public static int lastIndexOfLetter(String string) {
 		for ( int i=0; i<string.length(); i++ ) {
 			char character = string.charAt(i);
 			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
 		}
 		return string.length()-1;
 	}
 
 	public static String join(String seperator, String[] strings) {
 		int length = strings.length;
 		if ( length == 0 ) return "";
 		StringBuffer buf = new StringBuffer( length * strings[0].length() )
 				.append( strings[0] );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( seperator ).append( strings[i] );
 		}
 		return buf.toString();
 	}
 
 	public static String join(String seperator, Iterator objects) {
 		StringBuffer buf = new StringBuffer();
 		if ( objects.hasNext() ) buf.append( objects.next() );
 		while ( objects.hasNext() ) {
 			buf.append( seperator ).append( objects.next() );
 		}
 		return buf.toString();
 	}
 
 	public static String[] add(String[] x, String sep, String[] y) {
 		String[] result = new String[x.length];
 		for ( int i = 0; i < x.length; i++ ) {
 			result[i] = x[i] + sep + y[i];
 		}
 		return result;
 	}
 
 	public static String repeat(String string, int times) {
 		StringBuffer buf = new StringBuffer( string.length() * times );
 		for ( int i = 0; i < times; i++ ) buf.append( string );
 		return buf.toString();
 	}
 
 	public static String repeat(char character, int times) {
 		char[] buffer = new char[times];
 		Arrays.fill( buffer, character );
 		return new String( buffer );
 	}
 
 
 	public static String replace(String template, String placeholder, String replacement) {
 		return replace( template, placeholder, replacement, false );
 	}
 
 	public static String[] replace(String templates[], String placeholder, String replacement) {
 		String[] result = new String[templates.length];
 		for ( int i =0; i<templates.length; i++ ) {
 			result[i] = replace( templates[i], placeholder, replacement );
 		}
 		return result;
 	}
 
 	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
+		return replace( template, placeholder, replacement, wholeWords, false );
+	}
+
+	public static String replace(String template,
+								 String placeholder,
+								 String replacement,
+								 boolean wholeWords,
+								 boolean encloseInParensIfNecessary) {
 		if ( template == null ) {
 			return template;
 		}
 		int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
-			final boolean actuallyReplace = !wholeWords ||
-					loc + placeholder.length() == template.length() ||
-					!Character.isJavaIdentifierPart( template.charAt( loc + placeholder.length() ) );
-			String actualReplacement = actuallyReplace ? replacement : placeholder;
-			return new StringBuffer( template.substring( 0, loc ) )
-					.append( actualReplacement )
-					.append( replace( template.substring( loc + placeholder.length() ),
-							placeholder,
-							replacement,
-							wholeWords ) ).toString();
+			String beforePlaceholder = template.substring( 0, loc );
+			String afterPlaceholder = template.substring( loc + placeholder.length() );
+			return replace( beforePlaceholder, afterPlaceholder, placeholder, replacement, wholeWords, encloseInParensIfNecessary );
+		}
+	}
+
+
+	public static String replace(String beforePlaceholder,
+								 String afterPlaceholder,
+								 String placeholder,
+								 String replacement,
+								 boolean wholeWords,
+								 boolean encloseInParensIfNecessary) {
+		final boolean actuallyReplace =
+				! wholeWords ||
+				afterPlaceholder.length() == 0 ||
+				! Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
+		boolean encloseInParens =
+				actuallyReplace &&
+				encloseInParensIfNecessary &&
+				! ( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' ) &&
+				! ( getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')' );		
+		StringBuilder buf = new StringBuilder( beforePlaceholder );
+		if ( encloseInParens ) {
+			buf.append( '(' );
+		}
+		buf.append( actuallyReplace ? replacement : placeholder );
+		if ( encloseInParens ) {
+			buf.append( ')' );
+		}
+		buf.append(
+				replace(
+						afterPlaceholder,
+						placeholder,
+						replacement,
+						wholeWords,
+						encloseInParensIfNecessary
+				)
+		);
+		return buf.toString();
+	}
+
+	public static char getLastNonWhitespaceCharacter(String str) {
+		if ( str != null && str.length() > 0 ) {
+			for ( int i = str.length() - 1 ; i >= 0 ; i-- ) {
+				char ch = str.charAt( i );
+				if ( ! Character.isWhitespace( ch ) ) {
+					return ch;
+				}
+			}
 		}
+		return '\0';
 	}
 
+	public static char getFirstNonWhitespaceCharacter(String str) {
+		if ( str != null && str.length() > 0 ) {
+			for ( int i = 0 ; i < str.length() ; i++ ) {
+				char ch = str.charAt( i );
+				if ( ! Character.isWhitespace( ch ) ) {
+					return ch;
+				}
+			}
+		}
+		return '\0';
+	}
 
 	public static String replaceOnce(String template, String placeholder, String replacement) {
 		if ( template == null ) {
 			return template; // returnign null!
 		}
         int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			return new StringBuffer( template.substring( 0, loc ) )
 					.append( replacement )
 					.append( template.substring( loc + placeholder.length() ) )
 					.toString();
 		}
 	}
 
 
 	public static String[] split(String seperators, String list) {
 		return split( seperators, list, false );
 	}
 
 	public static String[] split(String seperators, String list, boolean include) {
 		StringTokenizer tokens = new StringTokenizer( list, seperators, include );
 		String[] result = new String[ tokens.countTokens() ];
 		int i = 0;
 		while ( tokens.hasMoreTokens() ) {
 			result[i++] = tokens.nextToken();
 		}
 		return result;
 	}
 
 	public static String unqualify(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( qualifiedName.lastIndexOf(".") + 1 );
 	}
 
 	public static String qualifier(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf(".");
 		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
 	}
 
 	/**
 	 * Collapses a name.  Mainly intended for use with classnames, where an example might serve best to explain.
 	 * Imagine you have a class named <samp>'org.hibernate.util.StringHelper'</samp>; calling collapse on that
 	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
 	 *
 	 * @param name The name to collapse.
 	 * @return The collapsed name.
 	 */
 	public static String collapse(String name) {
 		if ( name == null ) {
 			return null;
 		}
 		int breakPoint = name.lastIndexOf( '.' );
 		if ( breakPoint < 0 ) {
 			return name;
 		}
 		return collapseQualifier( name.substring( 0, breakPoint ), true ) + name.substring( breakPoint ); // includes last '.'
 	}
 
 	/**
 	 * Given a qualifier, collapse it.
 	 *
 	 * @param qualifier The qualifier to collapse.
 	 * @param includeDots Should we include the dots in the collapsed form?
 	 *
 	 * @return The collapsed form.
 	 */
 	public static String collapseQualifier(String qualifier, boolean includeDots) {
 		StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
 		String collapsed = Character.toString( tokenizer.nextToken().charAt( 0 ) );
 		while ( tokenizer.hasMoreTokens() ) {
 			if ( includeDots ) {
 				collapsed += '.';
 			}
 			collapsed += tokenizer.nextToken().charAt( 0 );
 		}
 		return collapsed;
 	}
 
 	/**
 	 * Partially unqualifies a qualified name.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.util.StringHelper' would become 'util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself, or the partially unqualified form if it begins with the qualifier base.
 	 */
 	public static String partiallyUnqualify(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return name;
 		}
 		return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
 	}
 
 	/**
 	 * Cross between {@link #collapse} and {@link #partiallyUnqualify}.  Functions much like {@link #collapse}
 	 * except that only the qualifierBase is collapsed.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.util.StringHelper' would become 'o.h.util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself if it does not begin with the qualifierBase, or the properly collapsed form otherwise.
 	 */
 	public static String collapseQualifierBase(String name, String qualifierBase) {
 		if ( name == null || ! name.startsWith( qualifierBase ) ) {
 			return collapse( name );
 		}
 		return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
 	}
 
 	public static String[] suffix(String[] columns, String suffix) {
 		if ( suffix == null ) return columns;
 		String[] qualified = new String[columns.length];
 		for ( int i = 0; i < columns.length; i++ ) {
 			qualified[i] = suffix( columns[i], suffix );
 		}
 		return qualified;
 	}
 
 	private static String suffix(String name, String suffix) {
 		return ( suffix == null ) ? name : name + suffix;
 	}
 
 	public static String root(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
 	}
 
 	public static String unroot(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc+1, qualifiedName.length() );
 	}
 
 	public static boolean booleanValue(String tfString) {
 		String trimmed = tfString.trim().toLowerCase();
 		return trimmed.equals( "true" ) || trimmed.equals( "t" );
 	}
 
 	public static String toString(Object[] array) {
 		int len = array.length;
 		if ( len == 0 ) return "";
 		StringBuffer buf = new StringBuffer( len * 12 );
 		for ( int i = 0; i < len - 1; i++ ) {
 			buf.append( array[i] ).append(", ");
 		}
 		return buf.append( array[len - 1] ).toString();
 	}
 
 	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
 		String[] result = new String[]{string};
 		while ( placeholders.hasNext() ) {
 			result = multiply( result, ( String ) placeholders.next(), ( String[] ) replacements.next() );
 		}
 		return result;
 	}
 
 	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
 		String[] results = new String[replacements.length * strings.length];
 		int n = 0;
 		for ( int i = 0; i < replacements.length; i++ ) {
 			for ( int j = 0; j < strings.length; j++ ) {
 				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
 			}
 		}
 		return results;
 	}
 
 	public static int countUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null)
 			return 0;
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int count = 0;
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				count++;
 			}
 		}
 		return count;
 	}
 
 	public static int[] locateUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if (string == null) {
 			return new int[0];
 		}
 
 		ArrayList locations = new ArrayList( 20 );
 
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				locations.add( new Integer( indx ) );
 			}
 		}
 		return ArrayHelper.toIntArray( locations );
 	}
 
 	public static boolean isNotEmpty(String string) {
 		return string != null && string.length() > 0;
 	}
 
 	public static boolean isEmpty(String string) {
 		return string == null || string.length() == 0;
 	}
 
 	public static String qualify(String prefix, String name) {
 		if ( name == null || prefix == null ) {
 			throw new NullPointerException();
 		}
 		return new StringBuffer( prefix.length() + name.length() + 1 )
 				.append(prefix)
 				.append('.')
 				.append(name)
 				.toString();
 	}
 
 	public static String[] qualify(String prefix, String[] names) {
 		if ( prefix == null ) return names;
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			qualified[i] = qualify( prefix, names[i] );
 		}
 		return qualified;
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
 		int matchAt = -1;
 		for ( int i = 0; i < string.length(); i++ ) {
 			int curMatch = sqlString.indexOf( string.charAt( i ), startindex );
 			if ( curMatch >= 0 ) {
 				if ( matchAt == -1 ) { // first time we find match!
 					matchAt = curMatch;
 				}
 				else {
 					matchAt = Math.min( matchAt, curMatch );
 				}
 			}
 		}
 		return matchAt;
 	}
 
 	public static String truncate(String string, int length) {
 		if ( string.length() <= length ) {
 			return string;
 		}
 		else {
 			return string.substring( 0, length );
 		}
 	}
 
 	public static String generateAlias(String description) {
 		return generateAliasRoot(description) + '_';
 	}
 
 	/**
 	 * Generate a nice alias for the given class name or collection role name and unique integer. Subclasses of
 	 * Loader do <em>not</em> have to use aliases of this form.
 	 *
 	 * @param description The base name (usually an entity-name or collection-role)
 	 * @param unique A uniquing value
 	 *
 	 * @return an alias of the form <samp>foo1_</samp>
 	 */
 	public static String generateAlias(String description, int unique) {
 		return generateAliasRoot(description) +
 			Integer.toString(unique) +
 			'_';
 	}
 
 	/**
 	 * Generates a root alias by truncating the "root name" defined by
 	 * the incoming decription and removing/modifying any non-valid
 	 * alias characters.
 	 *
 	 * @param description The root name from which to generate a root alias.
 	 * @return The generated root alias.
 	 */
 	private static String generateAliasRoot(String description) {
 		String result = truncate( unqualifyEntityName(description), ALIAS_TRUNCATE_LENGTH )
 				.toLowerCase()
 		        .replace( '/', '_' ) // entityNames may now include slashes for the representations
 				.replace( '$', '_' ); //classname may be an inner class
 		result = cleanAlias( result );
 		if ( Character.isDigit( result.charAt(result.length()-1) ) ) {
 			return result + "x"; //ick!
 		}
 		else {
 			return result;
 		}
 	}
 
 	/**
 	 * Clean the generated alias by removing any non-alpha characters from the
 	 * beginning.
 	 *
 	 * @param alias The generated alias to be cleaned.
 	 * @return The cleaned alias, stripped of any leading non-alpha characters.
 	 */
 	private static String cleanAlias(String alias) {
 		char[] chars = alias.toCharArray();
 		// short cut check...
 		if ( !Character.isLetter( chars[0] ) ) {
 			for ( int i = 1; i < chars.length; i++ ) {
 				// as soon as we encounter our first letter, return the substring
 				// from that position
 				if ( Character.isLetter( chars[i] ) ) {
 					return alias.substring( i );
 				}
 			}
 		}
 		return alias;
 	}
 
 	public static String unqualifyEntityName(String entityName) {
 		String result = unqualify(entityName);
 		int slashPos = result.indexOf( '/' );
 		if ( slashPos > 0 ) {
 			result = result.substring( 0, slashPos - 1 );
 		}
 		return result;
 	}
 	
 	public static String toUpperCase(String str) {
 		return str==null ? null : str.toUpperCase();
 	}
 	
 	public static String toLowerCase(String str) {
 		return str==null ? null : str.toLowerCase();
 	}
 
 	public static String moveAndToBeginning(String filter) {
 		if ( filter.trim().length()>0 ){
 			filter += " and ";
 			if ( filter.startsWith(" and ") ) filter = filter.substring(4);
 		}
 		return filter;
 	}
 
 	/**
 	 * Determine if the given string is quoted (wrapped by '`' characters at beginning and end).
 	 *
 	 * @param name The name to check.
 	 * @return True if the given string starts and ends with '`'; false otherwise.
 	 */
 	public static boolean isQuoted(String name) {
 		return name != null && name.length() != 0 && name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`';
 	}
 
 	/**
 	 * Return a representation of the given name ensuring quoting (wrapped with '`' characters).  If already wrapped
 	 * return name.
 	 *
 	 * @param name The name to quote.
 	 * @return The quoted version.
 	 */
 	public static String quote(String name) {
 		if ( name == null || name.length() == 0 || isQuoted( name ) ) {
 			return name;
 		}
 		else {
 			return new StringBuffer( name.length() + 2 ).append('`').append( name ).append( '`' ).toString();
 		}
 	}
 
 	/**
 	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
 	 *
 	 * @param name The name to be unquoted.
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name) {
 		if ( isQuoted( name ) ) {
 			return name.substring( 1, name.length() - 1 );
 		}
 		else {
 			return name;
 		}
 	}
 
 	/**
 	 * Determine if the given name is quoted.  It is considered quoted if either:
 	 * <ol>
 	 * <li>starts AND ends with backticks (`)</li>
 	 * <li>starts with dialect-specified {@link org.hibernate.dialect.Dialect#openQuote() open-quote}
 	 * 		AND ends with dialect-specified {@link org.hibernate.dialect.Dialect#closeQuote() close-quote}</li>
 	 * </ol>
 	 *
 	 * @param name The name to check
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return True if quoted, false otherwise
 	 */
 	public static boolean isQuoted(String name, Dialect dialect) {
 		return name != null && name.length() != 0
 				&& ( name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`'
 				|| name.charAt( 0 ) == dialect.openQuote() && name.charAt( name.length() - 1 ) == dialect.closeQuote() );
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param name The name to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name, Dialect dialect) {
 		if ( isQuoted( name, dialect ) ) {
 			return name.substring( 1, name.length() - 1 );
 		}
 		else {
 			return name;
 		}
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param names The names to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted versions.
 	 */
 	public static String[] unquote(String[] names, Dialect dialect) {
 		if ( names == null ) {
 			return null;
 		}
 		String[] unquoted = new String[ names.length ];
 		for ( int i = 0; i < names.length; i++ ) {
 			unquoted[i] = unquote( names[i], dialect );
 		}
 		return unquoted;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/cid/CompositeIdTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/cid/CompositeIdTest.java
index 77f921bc66..eade82f698 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/cid/CompositeIdTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/cid/CompositeIdTest.java
@@ -1,348 +1,348 @@
 //$Id$
 package org.hibernate.test.annotations.cid;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import org.hibernate.Criteria;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.Disjunction;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.test.annotations.TestCase;
 
 /**
  * test some composite id functionalities
  *
  * @author Emmanuel Bernard
  */
 public class CompositeIdTest extends TestCase {
 	public CompositeIdTest(String x) {
 		super( x );
 	}
 
 	public void testOneToOneInCompositePk() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		B b = new B();
 		C c = new C();
 		s.persist( b );
 		s.persist( c );
 		A a = new A();
 		a.setAId( new AId() );
 		a.getAId().setB( b );
 		a.getAId().setC( c );
 		s.persist( a );
 		s.flush();
 		s.clear();
 
 		a = (A) s.get(A.class, a.getAId() );
 		assertEquals( b.getId(), a.getAId().getB().getId() );
 
 		tx.rollback();
 		s.close();
 	}
 
 
 	/**
 	 * This feature is not supported by the EJB3
 	 * this is an hibernate extension
 	 */
 	public void testManyToOneInCompositePk() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		ParentPk ppk = new ParentPk();
 		ppk.setFirstName( "Emmanuel" );
 		ppk.setLastName( "Bernard" );
 		Parent p = new Parent();
 		p.id = ppk;
 		s.persist( p );
 		ChildPk cpk = new ChildPk();
 		cpk.parent = p;
 		cpk.nthChild = 1;
 		Child c = new Child();
 		c.id = cpk;
 		s.persist( c );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "select c from Child c where c.id.nthChild = :nth" );
 		q.setInteger( "nth", 1 );
 		List results = q.list();
 		assertEquals( 1, results.size() );
 		c = (Child) results.get( 0 );
 		assertNotNull( c );
 		assertNotNull( c.id.parent );
 		//FIXME mke it work in unambigious cases
 		//		assertNotNull(c.id.parent.id);
 		//		assertEquals(p.id.getFirstName(), c.id.parent.id.getFirstName());
 		s.delete( c );
 		s.delete( c.id.parent );
 		tx.commit();
 		s.close();
 	}
 
 	/**
 	 * This feature is not supported by the EJB3
 	 * this is an hibernate extension
 	 */
 	public void testManyToOneInCompositePkAndSubclass() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		ParentPk ppk = new ParentPk();
 		ppk.setFirstName( "Emmanuel" );
 		ppk.setLastName( "Bernard" );
 		Parent p = new Parent();
 		p.id = ppk;
 		s.persist( p );
 		ChildPk cpk = new ChildPk();
 		cpk.parent = p;
 		cpk.nthChild = 1;
 		LittleGenius c = new LittleGenius();
 		c.particularSkill = "Human Annotation parser";
 		c.id = cpk;
 		s.persist( c );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		Query q = s.createQuery( "select c from Child c where c.id.nthChild = :nth" );
 		q.setInteger( "nth", 1 );
 		List results = q.list();
 		assertEquals( 1, results.size() );
 		c = (LittleGenius) results.get( 0 );
 		assertNotNull( c );
 		assertNotNull( c.id.parent );
 		//FIXME mke it work in unambigious cases
 //		assertNotNull(c.id.parent.id);
 //		assertEquals(p.id.getFirstName(), c.id.parent.id.getFirstName());
 		s.delete( c );
 		s.delete( c.id.parent );
 		tx.commit();
 		s.close();
 	}
 
 	public void testManyToOneInCompositeId() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Channel channel = new Channel();
 		s.persist( channel );
 		Presenter pres = new Presenter();
 		pres.name = "Casimir";
 		s.persist( pres );
 		TvMagazinPk pk = new TvMagazinPk();
 		TvMagazin mag = new TvMagazin();
 		mag.time = new Date();
 		mag.id = pk;
 		pk.channel = channel;
 		pk.presenter = pres;
 		s.persist( mag );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		mag = (TvMagazin) s.createQuery( "from TvMagazin mag" ).uniqueResult();
 		assertNotNull( mag.id );
 		assertNotNull( mag.id.channel );
 		assertEquals( channel.id, mag.id.channel.id );
 		assertNotNull( mag.id.presenter );
 		assertEquals( pres.name, mag.id.presenter.name );
 		s.delete( mag );
 		s.delete( mag.id.channel );
 		s.delete( mag.id.presenter );
 		tx.commit();
 		s.close();
 	}
 
 	public void testManyToOneInCompositeIdClass() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Order order = new Order();
 		s.persist( order );
 		Product product = new Product();
 		product.name = "small car";
 		s.persist( product );
 		OrderLine orderLine = new OrderLine();
 		orderLine.order = order;
 		orderLine.product = product;
 		s.persist( orderLine );
 		s.flush();
 		s.clear();
 
 		orderLine = (OrderLine) s.createQuery( "select ol from OrderLine ol" ).uniqueResult();
 		assertNotNull( orderLine.order );
 		assertEquals( order.id, orderLine.order.id );
 		assertNotNull( orderLine.product );
 		assertEquals( product.name, orderLine.product.name );
 
 		tx.rollback();
 		s.close();
 	}
 
 	public void testSecondaryTableWithCompositeId() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Channel channel = new Channel();
 		s.persist( channel );
 		Presenter pres = new Presenter();
 		pres.name = "Tim Russet";
 		s.persist( pres );
 		TvMagazinPk pk = new TvMagazinPk();
 		TvProgram program = new TvProgram();
 		program.time = new Date();
 		program.id = pk;
 		program.text = "Award Winning Programming";
 		pk.channel = channel;
 		pk.presenter = pres;
 		s.persist( program );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		program = (TvProgram) s.createQuery( "from TvProgram pr" ).uniqueResult();
 		assertNotNull( program.id );
 		assertNotNull( program.id.channel );
 		assertEquals( channel.id, program.id.channel.id );
 		assertNotNull( program.id.presenter );
 		assertNotNull( program.text );
 		assertEquals( pres.name, program.id.presenter.name );
 		s.delete( program );
 		s.delete( program.id.channel );
 		s.delete( program.id.presenter );
 		tx.commit();
 		s.close();
 	}
 
 	public void testSecondaryTableWithIdClass() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Channel channel = new Channel();
 		s.persist( channel );
 		Presenter pres = new Presenter();
 		pres.name = "Bob";
 		s.persist( pres );
 		TvProgramIdClass program = new TvProgramIdClass();
 		program.time = new Date();
 		program.channel = channel;
 		program.presenter = pres;
 		program.text = "Jump the shark programming";
 		s.persist( program );
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		program = (TvProgramIdClass) s.createQuery( "from TvProgramIdClass pr" ).uniqueResult();
 		assertNotNull( program.channel );
 		assertEquals( channel.id, program.channel.id );
 		assertNotNull( program.presenter );
 		assertNotNull( program.text );
 		assertEquals( pres.name, program.presenter.name );
 		s.delete( program );
 		s.delete( program.channel );
 		s.delete( program.presenter );
 		tx.commit();
 		s.close();
 	}
 
 	public void testQueryInAndComposite() {
 
 		Session s = openSession(  );
 		Transaction transaction = s.beginTransaction();
 		createData( s );
         s.flush();
         List ids = new ArrayList<SomeEntityId>(2);
         ids.add( new SomeEntityId(1,12) );
         ids.add( new SomeEntityId(10,23) );
 
         Criteria criteria = s.createCriteria( SomeEntity.class );
         Disjunction disjunction = Restrictions.disjunction();
 
         disjunction.add( Restrictions.in( "id", ids  ) );
         criteria.add( disjunction );
 
         List list = criteria.list();
         assertEquals( 2, list.size() );
 		transaction.rollback();
 		s.close();
 	}
     public void testQueryInAndCompositeWithHQL() {
         Session s = openSession(  );
         Transaction transaction = s.beginTransaction();
         createData( s );
         s.flush();
         List ids = new ArrayList<SomeEntityId>(2);
         ids.add( new SomeEntityId(1,12) );
         ids.add( new SomeEntityId(10,23) );
         ids.add( new SomeEntityId(10,22) );
-        Query query=s.createQuery( "from SomeEntity e where e.id in (:idList)" );
+        Query query=s.createQuery( "from SomeEntity e where e.id in :idList" );
         query.setParameterList( "idList", ids );
         List list=query.list();
         assertEquals( 3, list.size() );
         transaction.rollback();
         s.close();
     }
 
 
 	private void createData(Session s){
         SomeEntity someEntity = new SomeEntity();
         someEntity.setId( new SomeEntityId( ) );
         someEntity.getId().setId( 1 );
         someEntity.getId().setVersion( 11 );
         someEntity.setProp( "aa" );
         s.persist( someEntity );
         
         someEntity = new SomeEntity();
         someEntity.setId( new SomeEntityId( ) );
         someEntity.getId().setId( 1 );
         someEntity.getId().setVersion( 12 );
         someEntity.setProp( "bb" );
         s.persist( someEntity );
         
         someEntity = new SomeEntity();
         someEntity.setId( new SomeEntityId( ) );
         someEntity.getId().setId( 10 );
         someEntity.getId().setVersion( 21 );
         someEntity.setProp( "cc1" );
         s.persist( someEntity );
         
         someEntity = new SomeEntity();
         someEntity.setId( new SomeEntityId( ) );
         someEntity.getId().setId( 10 );
         someEntity.getId().setVersion( 22 );
         someEntity.setProp( "cc2" );
         s.persist( someEntity );
         
         someEntity = new SomeEntity();
         someEntity.setId( new SomeEntityId( ) );
         someEntity.getId().setId( 10 );
         someEntity.getId().setVersion( 23 );
         someEntity.setProp( "cc3" );
         s.persist( someEntity );
 	}
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Parent.class,
 				Child.class,
 				Channel.class,
 				TvMagazin.class,
 				TvProgramIdClass.class,
 				TvProgram.class,
 				Presenter.class,
 				Order.class,
 				Product.class,
 				OrderLine.class,
 				OrderLinePk.class,
 				LittleGenius.class,
 				A.class,
 				B.class,
 				C.class,
 				SomeEntity.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
index f6ec05a3d6..2e8b058a59 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
@@ -1,1692 +1,1702 @@
 // $Id: ASTParserLoadingTest.java 11373 2007-03-29 19:09:07Z steve.ebersole@jboss.com $
 package org.hibernate.test.hql;
 import static org.hibernate.TestLogger.LOG;
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
 import junit.framework.Test;
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
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
 import org.hibernate.persister.entity.DiscriminatorType;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.test.any.IntegerPropertyValue;
 import org.hibernate.test.any.PropertySet;
 import org.hibernate.test.any.PropertyValue;
 import org.hibernate.test.any.StringPropertyValue;
 import org.hibernate.test.cid.Customer;
 import org.hibernate.test.cid.LineItem;
 import org.hibernate.test.cid.LineItem.Id;
 import org.hibernate.test.cid.Order;
 import org.hibernate.test.cid.Product;
 import org.hibernate.testing.junit.functional.FunctionalTestCase;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 import org.hibernate.transform.Transformers;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.ManyToOneType;
 import org.hibernate.type.Type;
 import org.hibernate.util.StringHelper;
 
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
 public class ASTParserLoadingTest extends FunctionalTestCase {
 
 	private List createdAnimalIds = new ArrayList();
 
 	public ASTParserLoadingTest(String name) {
 		super( name );
 	}
 
 	public String[] getMappings() {
 		return new String[] {
 				"hql/Animal.hbm.xml",
 				"hql/FooBarCopy.hbm.xml",
 				"hql/SimpleEntityWithAssociation.hbm.xml",
 				"hql/CrazyIdFieldNames.hbm.xml",
 				"hql/Image.hbm.xml",
 				"hql/ComponentContainer.hbm.xml",
 				"hql/VariousKeywordPropertyEntity.hbm.xml",
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
     public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.QUERY_TRANSLATOR, ASTQueryTranslatorFactory.class.getName() );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( ASTParserLoadingTest.class );
 	}
 
 	public void testSubSelectAsArtithmeticOperand() {
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
 
 	public void testJPAQLQualifiedIdentificationVariables() {
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
 
 		s = openSession();
 		s.beginTransaction();
 		results = s.createQuery( "select distinct key(h.family) from Human h" ).list();
 		assertEquals( 1, results.size() );
 		Object key = results.get(0);
 		assertTrue( String.class.isAssignableFrom( key.getClass() ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( me );
 		s.delete( joe );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testPaginationWithPolymorphicQuery() {
 		if ( getDialect() instanceof IngresDialect ) {
 			// HHH-4961 Ingres does not support this scoping in 9.3.
 			return;
 		}
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
             LOG.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a where a.offspring.father.description = 'xyz'" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             LOG.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a order by a.offspring.description" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             LOG.trace("expected failure...", qe);
 		}
 
 		try {
 			s.createQuery( "from Animal a order by a.offspring.father.description" ).list();
 			fail( "illegal collection dereference semantic did not cause failure" );
 		}
 		catch( QueryException qe ) {
             LOG.trace("expected failure...", qe);
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	/**
 	 * Copied from {@link HQLTest#testConcatenation}
 	 */
 	public void testConcatenation() {
 		// simple syntax checking...
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Human h where h.nickName = '1' || 'ov' || 'tha' || 'few'" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	/**
 	 * Copied from {@link HQLTest#testExpressionWithParamInFunction}
 	 */
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
 		s.createQuery( "from Animal a where abs(abs(a.bodyWeight - 1.0 + :param) * abs(length('ffobar')-3)) = 3.0" ).setLong( "param", 1 ).list();
 		if ( getDialect() instanceof DB2Dialect ) {
 			s.createQuery( "from Animal where lower(upper('foo') || upper(cast(:bar as string))) like 'f%'" ).setString( "bar", "xyz" ).list();
 		}
 		else {
 			s.createQuery( "from Animal where lower(upper('foo') || upper(:bar)) like 'f%'" ).setString( "bar", "xyz" ).list();
 		}
 		if ( ! ( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof MySQLDialect ) ) {
 			s.createQuery( "from Animal where abs(cast(1 as float) - cast(:param as float)) = 1.0" ).setLong( "param", 1 ).list();
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
 
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
 
 	public void testImplicitJoinsInDifferentClauses() {
 		// HHH-2257 :
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
 		List list = new ArrayList();
 		list.add( new Id("123456789", order.getId().getOrderNumber(), "4321") );
 		list.add( new Id("123456789", order.getId().getOrderNumber(), "1234") );
 		query.setParameterList( "idList", list );
 		assertEquals( 2, query.list().size() );
+
+		query = s.createQuery( "from LineItem l where l.id in :idList" );
+		query.setParameterList( "idList", list );
+		assertEquals( 2, query.list().size() );
+
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
 
 	public void testImplicitSelectEntityAssociationInShallowQuery() {
 		// HHH-2257 :
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
 
 	public void testEntityAndOneToOneReturnedByQuery() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name( "Gail", null, "Badner" ) );
 		s.save( h );
 		User u = new User();
 		u.setUserName(  "gbadner" );
 		u.setHuman( h );
 		s.save( u );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Object [] result = ( Object [] ) s.createQuery( "from User u, Human h where u.human = h" ).uniqueResult();
 		assertNotNull( result );
 		assertEquals( u.getUserName(), ( ( User ) result[ 0 ] ).getUserName() );
 		assertEquals( h.getName().getFirst(), ( ( Human ) result[ 1 ] ).getName().getFirst() );
 		assertSame( ( ( User ) result[ 0 ] ).getHuman(), result[ 1 ] );
 		s.createQuery( "delete User" ).executeUpdate();
 		s.createQuery( "delete Human" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
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
 
 	public void testSpecialClassPropertyReference() {
 		// this is a long standing bug in Hibernate when applied to joined-subclasses;
 		//  see HHH-939 for details and history
 		new SyntaxChecker( "from Zoo zoo where zoo.class = PettingZoo" ).checkAll();
 		new SyntaxChecker( "select a.description from Animal a where a.class = Mammal" ).checkAll();
 		new SyntaxChecker( "select a.class from Animal a" ).checkAll();
 		new SyntaxChecker( "from DomesticAnimal an where an.class = Dog" ).checkAll();
 		new SyntaxChecker( "from Animal an where an.class = Dog" ).checkAll();
 	}
 
 	public void testSpecialClassPropertyReferenceFQN() {
 		// tests relating to HHH-2376
 		new SyntaxChecker( "from Zoo zoo where zoo.class = org.hibernate.test.hql.PettingZoo" ).checkAll();
 		new SyntaxChecker( "select a.description from Animal a where a.class = org.hibernate.test.hql.Mammal" ).checkAll();
 		new SyntaxChecker( "from DomesticAnimal an where an.class = org.hibernate.test.hql.Dog" ).checkAll();
 		new SyntaxChecker( "from Animal an where an.class = org.hibernate.test.hql.Dog" ).checkAll();
 	}
 
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
 	}
 
 	public void testSimpleSelectWithLimitAndOffset() throws Exception {
 		if ( ! ( getDialect().supportsLimit() && getDialect().supportsLimitOffset() ) ) {
 			reportSkip( "dialect does not support offset and limit combo", "limit and offset combination" );
 			return;
 		}
 
 		// just checking correctness of param binding code...
 		Session session = openSession();
 		session.createQuery( "from Animal" )
 				.setFirstResult( 2 )
 				.setMaxResults( 1 )
 				.list();
 		session.close();
 	}
 
 	public void testJPAPositionalParameterList() {
 		Session s = openSession();
 		s.beginTransaction();
 		ArrayList params = new ArrayList();
 		params.add( "Doe" );
 		params.add( "Public" );
 		s.createQuery( "from Human where name.last in (?1)" )
 				.setParameterList( "1", params )
 				.list();
+
+		s.createQuery( "from Human where name.last in ?1" )
+				.setParameterList( "1", params )
+				.list();
+
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testComponentQueries() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		Type[] types = s.createQuery( "select h.name from Human h" ).getReturnTypes();
 		assertEquals( 1, types.length );
 		assertTrue( types[0] instanceof ComponentType );
 
 		// Test the ability to perform comparisions between component values
 		s.createQuery( "from Human h where h.name = h.name" ).list();
 		s.createQuery( "from Human h where h.name = :name" ).setParameter( "name", new Name() ).list();
 		s.createQuery( "from Human where name = :name" ).setParameter( "name", new Name() ).list();
 		s.createQuery( "from Human h where :name = h.name" ).setParameter( "name", new Name() ).list();
 		s.createQuery( "from Human h where :name <> h.name" ).setParameter( "name", new Name() ).list();
 
 		// Test the ability to perform comparisions between a component and an explicit row-value
 		s.createQuery( "from Human h where h.name = ('John', 'X', 'Doe')" ).list();
 		s.createQuery( "from Human h where ('John', 'X', 'Doe') = h.name" ).list();
 		s.createQuery( "from Human h where ('John', 'X', 'Doe') <> h.name" ).list();
 		s.createQuery( "from Human h where ('John', 'X', 'Doe') >= h.name" ).list();
 
 		s.createQuery( "from Human h order by h.name" ).list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testComponentParameterBinding() {
 		if ( getDialect() instanceof IngresDialect ) {
 			// HHH-4970 Subselects are not supported within select target lists
 			// in Ingres
 			return;
 		}
 		// HHH-1774 : parameters are bound incorrectly with component parameters...
 		Session s = openSession();
 		s.beginTransaction();
 
 		Order.Id oId = new Order.Id( "1234", 1 );
 
 		// control
 		s.createQuery("from Order o where o.customer.name =:name and o.id = :id")
 				.setParameter( "name", "oracle" )
 				.setParameter( "id", oId )
 				.list();
 
 		// this is the form that caused problems in the original case...
 		s.createQuery("from Order o where o.id = :id and o.customer.name =:name ")
 				.setParameter( "id", oId )
 				.setParameter( "name", "oracle" )
 				.list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testAnyMappingReference() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		PropertyValue redValue = new StringPropertyValue( "red" );
 		PropertyValue lonliestNumberValue = new IntegerPropertyValue( 1 );
 
 		Long id;
 		PropertySet ps = new PropertySet( "my properties" );
 		ps.setSomeSpecificProperty( redValue );
 		ps.getGeneralProperties().put( "the lonliest number", lonliestNumberValue );
 		ps.getGeneralProperties().put( "i like", new StringPropertyValue( "pina coladas" ) );
 		ps.getGeneralProperties().put( "i also like", new StringPropertyValue( "getting caught in the rain" ) );
 		s.save( ps );
 
 		s.getTransaction().commit();
 		id = ps.getId();
 		s.clear();
 		s.beginTransaction();
 
 		// TODO : setEntity() currently will not work here, but that would be *very* nice
 		// does not work because the corresponding EntityType is then used as the "bind type" rather
 		// than the "discovered" AnyType...
 		s.createQuery( "from PropertySet p where p.someSpecificProperty = :ssp" ).setParameter( "ssp", redValue ).list();
 
 		s.createQuery( "from PropertySet p where p.someSpecificProperty.id is not null" ).list();
 
 		s.createQuery( "from PropertySet p join p.generalProperties gp where gp.id is not null" ).list();
 
 		s.delete( s.load( PropertySet.class, id ) );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testJdkEnumStyleEnumConstant() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 
 		s.createQuery( "from Zoo z where z.classification = org.hibernate.test.hql.Classification.LAME" ).list();
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testParameterTypeMismatchFailureExpected() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		Query query = s.createQuery( "from Animal a where a.description = :nonstring" )
 				.setParameter( "nonstring", new Integer(1) );
 		try {
 			query.list();
 			fail( "query execution should have failed" );
 		}
 		catch( TypeMismatchException tme ) {
 			// expected behavior
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testMultipleBagFetchesFail() {
 		Session s = openSession();
 		s.beginTransaction();
 		try {
 			s.createQuery( "from Human h join fetch h.friends f join fetch f.friends fof" ).list();
 			fail( "failure expected" );
 		}
 		catch( HibernateException e ) {
 			assertTrue( "unexpected failure reason : " + e, e.getMessage().indexOf( "multiple bags" ) > 0 );
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testCollectionJoinsInSubselect() {
 		// HHH-1248 : initially FromElementFactory treated any explicit join
 		// as an implied join so that theta-style joins would always be used.
 		// This was because correlated subqueries cannot use ANSI-style joins
 		// for the correlation.  However, this special treatment was not limited
 		// to only correlated subqueries; it was applied to any subqueries ->
 		// which in-and-of-itself is not necessarily bad.  But somewhere later
 		// the choices made there caused joins to be dropped.
 		Session s = openSession();
 		String qryString =
 				"select a.id, a.description" +
 				" from Animal a" +
 				"       left join a.offspring" +
 				" where a in (" +
 				"       select a1 from Animal a1" +
 				"           left join a1.offspring o" +
 				"       where a1.id=1" +
 		        ")";
 		s.createQuery( qryString ).list();
 		qryString =
 				"select h.id, h.description" +
 		        " from Human h" +
 				"      left join h.friends" +
 				" where h in (" +
 				"      select h1" +
 				"      from Human h1" +
 				"          left join h1.friends f" +
 				"      where h1.id=1" +
 				")";
 		s.createQuery( qryString ).list();
 		qryString =
 				"select h.id, h.description" +
 		        " from Human h" +
 				"      left join h.friends f" +
 				" where f in (" +
 				"      select h1" +
 				"      from Human h1" +
 				"          left join h1.friends f1" +
 				"      where h = f1" +
 				")";
 		s.createQuery( qryString ).list();
 		s.close();
 	}
 
 	public void testCollectionFetchWithDistinctionAndLimit() {
 		// create some test data...
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		int parentCount = 30;
 		for ( int i = 0; i < parentCount; i++ ) {
 			Animal child1 = new Animal();
 			child1.setDescription( "collection fetch distinction (child1 - parent" + i + ")" );
 			s.persist( child1 );
 			Animal child2 = new Animal();
 			child2.setDescription( "collection fetch distinction (child2 - parent " + i + ")" );
 			s.persist( child2 );
 			Animal parent = new Animal();
 			parent.setDescription( "collection fetch distinction (parent" + i + ")" );
 			parent.setSerialNumber( "123-" + i );
 			parent.addOffspring( child1 );
 			parent.addOffspring( child2 );
 			s.persist( parent );
 		}
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		// Test simple distinction
 		List results;
 		results = s.createQuery( "select distinct p from Animal p inner join fetch p.offspring" ).list();
 		assertEquals( "duplicate list() returns", 30, results.size() );
 		// Test first/max
 		results = s.createQuery( "select p from Animal p inner join fetch p.offspring order by p.id" )
 				.setFirstResult( 5 )
 				.setMaxResults( 20 )
 				.list();
 		assertEquals( "duplicate returns", 20, results.size() );
 		Animal firstReturn = ( Animal ) results.get( 0 );
 		assertEquals( "firstResult not applied correctly", "123-5", firstReturn.getSerialNumber() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.createQuery( "delete Animal where mother is not null" ).executeUpdate();
 		s.createQuery( "delete Animal" ).executeUpdate();
 		t.commit();
 		s.close();
 	}
 
 	public void testFetchInSubqueryFails() {
 		Session s = openSession();
 		try {
 			s.createQuery( "from Animal a where a.mother in (select m from Animal a1 inner join a1.mother as m join fetch m.mother)" ).list();
 			fail( "fetch join allowed in subquery" );
 		}
 		catch( QueryException expected ) {
 			// expected behavior
 		}
 		s.close();
 	}
 
 	public void testQueryMetadataRetrievalWithFetching() {
 		// HHH-1464 : there was a problem due to the fact they we polled
 		// the shallow version of the query plan to get the metadata.
 		Session s = openSession();
 		Query query = s.createQuery( "from Animal a inner join fetch a.mother" );
 		assertEquals( 1, query.getReturnTypes().length );
 		assertNull( query.getReturnAliases() );
 		s.close();
 	}
 
 	public void testSuperclassPropertyReferenceAfterCollectionIndexedAccess() {
 		// note: simply performing syntax checking in the db
 		// test for HHH-429
 		Session s = openSession();
 		s.beginTransaction();
 		Mammal tiger = new Mammal();
 		tiger.setDescription( "Tiger" );
 		s.persist( tiger );
 		Mammal mother = new Mammal();
 		mother.setDescription( "Tiger's mother" );
 		mother.setBodyWeight( 4.0f );
 		mother.addOffspring( tiger );
 		s.persist( mother );
 		Zoo zoo = new Zoo();
 		zoo.setName( "Austin Zoo" );
 		zoo.setMammals( new HashMap() );
 		zoo.getMammals().put( "tiger", tiger );
 		s.persist( zoo );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from Zoo zoo where zoo.mammals['tiger'].mother.bodyWeight > 3.0f" ).list();
 		assertEquals( 1, results.size() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( tiger );
 		s.delete( mother );
 		s.delete( zoo );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testJoinFetchCollectionOfValues() {
 		// note: simply performing syntax checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "select h from Human as h join fetch h.nickNames" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testIntegerLiterals() {
 		// note: simply performing syntax checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Foo where long = 1" ).list();
 		s.createQuery( "from Foo where long = " + Integer.MIN_VALUE ).list();
 		s.createQuery( "from Foo where long = " + Integer.MAX_VALUE ).list();
 		s.createQuery( "from Foo where long = 1L" ).list();
 		s.createQuery( "from Foo where long = " + (Long.MIN_VALUE + 1) + "L" ).list();
 		s.createQuery( "from Foo where long = " + Long.MAX_VALUE + "L" ).list();
 		s.createQuery( "from Foo where integer = " + (Long.MIN_VALUE + 1) ).list();
 // currently fails due to HHH-1387
 //		s.createQuery( "from Foo where long = " + Long.MIN_VALUE ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testDecimalLiterals() {
 		// note: simply performing syntax checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal where bodyWeight > 100.0e-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.0E-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001f" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001F" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001d" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100.001D" ).list();
 		s.createQuery( "from Animal where bodyWeight > .001f" ).list();
 		s.createQuery( "from Animal where bodyWeight > 100e-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > .01E-10" ).list();
 		s.createQuery( "from Animal where bodyWeight > 1e-38" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testNakedPropertyRef() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal where bodyWeight = bodyWeight" ).list();
 		s.createQuery( "select bodyWeight from Animal" ).list();
 		s.createQuery( "select max(bodyWeight) from Animal" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testNakedComponentPropertyRef() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Human where name.first = 'Gavin'" ).list();
 		s.createQuery( "select name from Human" ).list();
 		s.createQuery( "select upper(h.name.first) from Human as h" ).list();
 		s.createQuery( "select upper(name.first) from Human" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testNakedImplicitJoins() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal where mother.father.id = 1" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testNakedEntityAssociationReference() {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Animal where mother = :mother" ).setParameter( "mother", null ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testNakedMapIndex() throws Exception {
 		// note: simply performing syntax and column/table resolution checking in the db
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Zoo where mammals['dog'].description like '%black%'" ).list();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testInvalidFetchSemantics() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		try {
 			s.createQuery( "select mother from Human a left join fetch a.mother mother" ).list();
 			fail( "invalid fetch semantic allowed!" );
 		}
 		catch( QueryException e ) {
 		}
 
 		try {
 			s.createQuery( "select mother from Human a left join fetch a.mother mother" ).list();
 			fail( "invalid fetch semantic allowed!" );
 		}
 		catch( QueryException e ) {
 		}
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testArithmetic() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("Melbourne Zoo");
 		s.persist(zoo);
 		s.createQuery("select 2*2*2*2*(2*2) from Zoo").uniqueResult();
 		s.createQuery("select 2 / (1+1) from Zoo").uniqueResult();
 		int result0 = ( (Integer) s.createQuery("select 2 - (1+1) from Zoo").uniqueResult() ).intValue();
 		int result1 = ( (Integer) s.createQuery("select 2 - 1 + 1 from Zoo").uniqueResult() ).intValue();
 		int result2 = ( (Integer) s.createQuery("select 2 * (1-1) from Zoo").uniqueResult() ).intValue();
 		int result3 = ( (Integer) s.createQuery("select 4 / (2 * 2) from Zoo").uniqueResult() ).intValue();
 		int result4 = ( (Integer) s.createQuery("select 4 / 2 * 2 from Zoo").uniqueResult() ).intValue();
 		int result5 = ( (Integer) s.createQuery("select 2 * (2/2) from Zoo").uniqueResult() ).intValue();
 		int result6 = ( (Integer) s.createQuery("select 2 * (2/2+1) from Zoo").uniqueResult() ).intValue();
 		assertEquals(result0, 0);
 		assertEquals(result1, 2);
 		assertEquals(result2, 0);
 		assertEquals(result3, 1);
 		assertEquals(result4, 4);
 		assertEquals(result5, 2);
 		assertEquals(result6, 4);
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	public void testNestedCollectionFetch() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery("from Animal a left join fetch a.offspring o left join fetch o.offspring where a.mother.id = 1 order by a.description").list();
 		s.createQuery("from Zoo z left join fetch z.animals a left join fetch a.offspring where z.name ='MZ' order by a.description").list();
 		s.createQuery("from Human h left join fetch h.pets a left join fetch a.offspring where h.name.first ='Gavin' order by a.description").list();
 		t.commit();
 		s.close();
 	}
 
 	public void testSelectClauseSubselect() {
 		if ( getDialect() instanceof IngresDialect ) {
 			// HHH-4973 Ingres 9.3 does not support sub-selects in the select
 			// list.
 			return;
 		}
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("Melbourne Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo(zoo);
 		plat.setSerialNumber("plat123");
 		zoo.getMammals().put("Platypus", plat);
 		zoo.getAnimals().put("plat123", plat);
 		s.persist( plat );
 		s.persist(zoo);
 
 		s.createQuery("select (select max(z.id) from a.zoo z) from Animal a").list();
 		s.createQuery("select (select max(z.id) from a.zoo z where z.name=:name) from Animal a")
 			.setParameter("name", "Melbourne Zoo").list();
 
 		s.delete(plat);
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	public void testInitProxy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		s.persist( plat );
 		s.flush();
 		s.clear();
 		plat = (Mammal) s.load(Mammal.class, plat.getId() );
 		assertFalse( Hibernate.isInitialized(plat) );
 		Object plat2 = s.createQuery("from Animal a").uniqueResult();
 		assertSame(plat, plat2);
 		assertTrue( Hibernate.isInitialized(plat) );
 		s.delete(plat);
 		t.commit();
 		s.close();
 	}
 
 	public void testSelectClauseImplicitJoin() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("The Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo(zoo);
 		plat.setSerialNumber("plat123");
 		zoo.getMammals().put("Platypus", plat);
 		zoo.getAnimals().put("plat123", plat);
 		s.persist( plat );
 		s.persist(zoo);
 		s.flush();
 		s.clear();
 		Query q = s.createQuery("select distinct a.zoo from Animal a where a.zoo is not null");
 		Type type = q.getReturnTypes()[0];
 		assertTrue( type instanceof ManyToOneType );
 		assertEquals( ( (ManyToOneType) type ).getAssociatedEntityName(), "org.hibernate.test.hql.Zoo" );
 		zoo = (Zoo) q.list().get(0);
 		assertEquals( zoo.getMammals().size(), 1 );
 		assertEquals( zoo.getAnimals().size(), 1 );
 		s.clear();
 		s.delete(plat);
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	public void testSelectClauseImplicitJoinWithIterate() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Zoo zoo = new Zoo();
 		zoo.setName("The Zoo");
 		zoo.setMammals( new HashMap() );
 		zoo.setAnimals( new HashMap() );
 		Mammal plat = new Mammal();
 		plat.setBodyWeight( 11f );
 		plat.setDescription( "Platypus" );
 		plat.setZoo(zoo);
 		plat.setSerialNumber("plat123");
 		zoo.getMammals().put("Platypus", plat);
 		zoo.getAnimals().put("plat123", plat);
 		s.persist( plat );
 		s.persist(zoo);
 		s.flush();
 		s.clear();
 		Query q = s.createQuery("select distinct a.zoo from Animal a where a.zoo is not null");
 		Type type = q.getReturnTypes()[0];
 		assertTrue( type instanceof ManyToOneType );
 		assertEquals( ( (ManyToOneType) type ).getAssociatedEntityName(), "org.hibernate.test.hql.Zoo" );
 		zoo = (Zoo) q
 			.iterate().next();
 		assertEquals( zoo.getMammals().size(), 1 );
 		assertEquals( zoo.getAnimals().size(), 1 );
 		s.clear();
 		s.delete(plat);
 		s.delete(zoo);
 		t.commit();
 		s.close();
 	}
 
 	public void testComponentOrderBy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Long id1 = ( Long ) s.save( genSimpleHuman( "John", "Jacob" ) );
 		Long id2 = ( Long ) s.save( genSimpleHuman( "Jingleheimer", "Schmidt" ) );
 
 		s.flush();
 
 		// the component is defined with the firstName column first...
 		List results = s.createQuery( "from Human as h order by h.name" ).list();
 		assertEquals( "Incorrect return count", 2, results.size() );
 
 		Human h1 = ( Human ) results.get( 0 );
 		Human h2 = ( Human ) results.get( 1 );
 
 		assertEquals( "Incorrect ordering", id2, h1.getId() );
 		assertEquals( "Incorrect ordering", id1, h2.getId() );
 
 		s.delete( h1 );
 		s.delete( h2 );
 
 		t.commit();
 		s.close();
 	}
 
 	public void testOrderedWithCustomColumnReadAndWrite() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		SimpleEntityWithAssociation first = new SimpleEntityWithAssociation();
 		first.setNegatedNumber(1);
 		s.save(first);
 		SimpleEntityWithAssociation second = new SimpleEntityWithAssociation();
 		second.setNegatedNumber(2);
 		s.save(second);
 		s.flush();
 
 		// Check order via SQL. Numbers are negated in the DB, so second comes first.
 		List listViaSql = s.createSQLQuery("select id from simple_1 order by negated_num").list();
 		assertEquals(2, listViaSql.size());
 		assertEquals(second.getId().longValue(), ((Number)listViaSql.get(0)).longValue());
 		assertEquals(first.getId().longValue(), ((Number)listViaSql.get(1)).longValue());
 
 		// Check order via HQL. Now first comes first b/c the read negates the DB negation.
 		List listViaHql = s.createQuery("from SimpleEntityWithAssociation order by negatedNumber").list();
 		assertEquals(2, listViaHql.size());
 		assertEquals(first.getId(), ((SimpleEntityWithAssociation)listViaHql.get(0)).getId());
 		assertEquals(second.getId(), ((SimpleEntityWithAssociation)listViaHql.get(1)).getId());
 
 		s.delete(first);
 		s.delete(second);
 		t.commit();
 		s.close();
 
 	}
 
 	public void testHavingWithCustomColumnReadAndWrite() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		SimpleEntityWithAssociation first = new SimpleEntityWithAssociation();
 		first.setNegatedNumber(5);
 		first.setName("simple");
 		s.save(first);
 		SimpleEntityWithAssociation second = new SimpleEntityWithAssociation();
 		second.setNegatedNumber(10);
 		second.setName("simple");
 		s.save(second);
 		SimpleEntityWithAssociation third = new SimpleEntityWithAssociation();
 		third.setNegatedNumber(20);
 		third.setName("complex");
 		s.save(third);
 		s.flush();
 
 		// Check order via HQL. Now first comes first b/c the read negates the DB negation.
 		Number r = (Number)s.createQuery("select sum(negatedNumber) from SimpleEntityWithAssociation " +
 				"group by name having sum(negatedNumber) < 20").uniqueResult();
 		assertEquals(r.intValue(), 15);
 
 		s.delete(first);
 		s.delete(second);
 		s.delete(third);
 		t.commit();
 		s.close();
 
 	}
 
 	public void testLoadSnapshotWithCustomColumnReadAndWrite() {
 		// Exercises entity snapshot load when select-before-update is true.
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		final double SIZE_IN_KB = 1536d;
 		final double SIZE_IN_MB = SIZE_IN_KB / 1024d;
 		Image image = new Image();
 		image.setName("picture.gif");
 		image.setSizeKb(SIZE_IN_KB);
 		s.persist(image);
 		s.flush();
 
 		Double sizeViaSql = (Double)s.createSQLQuery("select size_mb from image").uniqueResult();
 		assertEquals(SIZE_IN_MB, sizeViaSql, 0.01d);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		final double NEW_SIZE_IN_KB = 2048d;
 		final double NEW_SIZE_IN_MB = NEW_SIZE_IN_KB / 1024d;
 		image.setSizeKb(NEW_SIZE_IN_KB);
 		s.update(image);
 		s.flush();
 
 		sizeViaSql = (Double)s.createSQLQuery("select size_mb from image").uniqueResult();
 		assertEquals(NEW_SIZE_IN_MB, sizeViaSql, 0.01d);
 
 		s.delete(image);
 		t.commit();
 		s.close();
 
 	}
 
 
 	private Human genSimpleHuman(String fName, String lName) {
 		Human h = new Human();
 		h.setName( new Name( fName, 'X', lName ) );
 
 		return h;
 	}
 
 	public void testCastInSelect() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(12.4f);
 		a.setDescription("an animal");
 		s.persist(a);
 		Object bodyWeight = s.createQuery("select cast(bodyWeight as integer) from Animal").uniqueResult();
 		assertTrue( Integer.class.isInstance( bodyWeight ) );
 		assertEquals( 12, bodyWeight );
 
 		bodyWeight = s.createQuery("select cast(bodyWeight as big_decimal) from Animal").uniqueResult();
 		assertTrue( BigDecimal.class.isInstance( bodyWeight ) );
 		assertEquals( a.getBodyWeight(), ( (BigDecimal) bodyWeight ).floatValue() );
 
 		Object literal = s.createQuery("select cast(10000000 as big_integer) from Animal").uniqueResult();
 		assertTrue( BigInteger.class.isInstance( literal ) );
 		assertEquals( BigInteger.valueOf( 10000000 ), literal );
 		s.delete(a);
 		t.commit();
 		s.close();
 	}
 
 	/**
 	 * Test the numeric expression rules specified in section 4.8.6 of the JPA 2 specification
 	 */
 	public void testNumericExpressionReturnTypes() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(12.4f);
 		a.setDescription("an animal");
 		s.persist(a);
 
 		Object result;
 
 		// addition ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		result = s.createQuery( "select 1 + 1 from Animal as a" ).uniqueResult();
 		assertTrue( "int + int", Integer.class.isInstance( result ) );
 		assertEquals( 2, result );
 
 		result = s.createQuery( "select 1 + 1L from Animal a" ).uniqueResult();
 		assertTrue( "int + long", Long.class.isInstance( result ) );
 		assertEquals( Long.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1BI from Animal a" ).uniqueResult();
 		assertTrue( "int + BigInteger", BigInteger.class.isInstance( result ) );
 		assertEquals( BigInteger.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1F from Animal a" ).uniqueResult();
 		assertTrue( "int + float", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1D from Animal a" ).uniqueResult();
 		assertTrue( "int + double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1 + 1BD from Animal a" ).uniqueResult();
 		assertTrue( "int + BigDecimal", BigDecimal.class.isInstance( result ) );
 		assertEquals( BigDecimal.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1F + 1D from Animal a" ).uniqueResult();
 		assertTrue( "float + double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 2 ), result );
 
 		result = s.createQuery( "select 1F + 1BD from Animal a" ).uniqueResult();
 		assertTrue( "float + BigDecimal", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 2 ), result );
 
 		// subtraction ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		result = s.createQuery( "select 1 - 1 from Animal as a" ).uniqueResult();
 		assertTrue( "int - int", Integer.class.isInstance( result ) );
 		assertEquals( 0, result );
 
 		result = s.createQuery( "select 1 - 1L from Animal a" ).uniqueResult();
 		assertTrue( "int - long", Long.class.isInstance( result ) );
 		assertEquals( Long.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1BI from Animal a" ).uniqueResult();
 		assertTrue( "int - BigInteger", BigInteger.class.isInstance( result ) );
 		assertEquals( BigInteger.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1F from Animal a" ).uniqueResult();
 		assertTrue( "int - float", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1D from Animal a" ).uniqueResult();
 		assertTrue( "int - double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1 - 1BD from Animal a" ).uniqueResult();
 		assertTrue( "int - BigDecimal", BigDecimal.class.isInstance( result ) );
 		assertEquals( BigDecimal.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1F - 1D from Animal a" ).uniqueResult();
 		assertTrue( "float - double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 0 ), result );
 
 		result = s.createQuery( "select 1F - 1BD from Animal a" ).uniqueResult();
 		assertTrue( "float - BigDecimal", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 0 ), result );
 
 		// multiplication ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		result = s.createQuery( "select 1 * 1 from Animal as a" ).uniqueResult();
 		assertTrue( "int * int", Integer.class.isInstance( result ) );
 		assertEquals( 1, result );
 
 		result = s.createQuery( "select 1 * 1L from Animal a" ).uniqueResult();
 		assertTrue( "int * long", Long.class.isInstance( result ) );
 		assertEquals( Long.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1BI from Animal a" ).uniqueResult();
 		assertTrue( "int * BigInteger", BigInteger.class.isInstance( result ) );
 		assertEquals( BigInteger.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1F from Animal a" ).uniqueResult();
 		assertTrue( "int * float", Float.class.isInstance( result ) );
 		assertEquals( Float.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1D from Animal a" ).uniqueResult();
 		assertTrue( "int * double", Double.class.isInstance( result ) );
 		assertEquals( Double.valueOf( 1 ), result );
 
 		result = s.createQuery( "select 1 * 1BD from Animal a" ).uniqueResult();
 		assertTrue( "int * BigDecimal", BigDecimal.class.isInstance( result ) );
 		assertEquals( BigDecimal.valueOf( 1 ), result );
 
 		s.delete(a);
 		t.commit();
 		s.close();
 	}
 
 	public void testAliases() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(12.4f);
 		a.setDescription("an animal");
 		s.persist(a);
 		String[] aliases1 = s.createQuery("select a.bodyWeight as abw, a.description from Animal a").getReturnAliases();
 		assertEquals(aliases1[0], "abw");
 		assertEquals(aliases1[1], "1");
 		String[] aliases2 = s.createQuery("select count(*), avg(a.bodyWeight) as avg from Animal a").getReturnAliases();
 		assertEquals(aliases2[0], "0");
 		assertEquals(aliases2[1], "avg");
 		s.delete(a);
 		t.commit();
 		s.close();
 	}
 
 	public void testParameterMixing() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery( "from Animal a where a.description = ? and a.bodyWeight = ? or a.bodyWeight = :bw" )
 				.setString( 0, "something" )
 				.setFloat( 1, 12345f )
 				.setFloat( "bw", 123f )
 				.list();
 		t.commit();
 		s.close();
 	}
 
 	public void testOrdinalParameters() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery( "from Animal a where a.description = ? and a.bodyWeight = ?" )
 				.setString( 0, "something" )
 				.setFloat( 1, 123f )
 				.list();
 		s.createQuery( "from Animal a where a.bodyWeight in (?, ?)" )
 				.setFloat( 0, 999f )
 				.setFloat( 1, 123f )
 				.list();
 		t.commit();
 		s.close();
 	}
 
 	public void testIndexParams() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.createQuery("from Zoo zoo where zoo.mammals[:name] = :id")
 			.setParameter("name", "Walrus")
 			.setParameter("id", new Long(123))
 			.list();
 		s.createQuery("from Zoo zoo where zoo.mammals[:name].bodyWeight > :w")
 			.setParameter("name", "Walrus")
 			.setParameter("w", new Float(123.32))
 			.list();
 		s.createQuery("from Zoo zoo where zoo.animals[:sn].mother.bodyWeight < :mw")
 			.setParameter("sn", "ant-123")
 			.setParameter("mw", new Float(23.32))
 			.list();
 		/*s.createQuery("from Zoo zoo where zoo.animals[:sn].description like :desc and zoo.animals[:sn].bodyWeight > :wmin and zoo.animals[:sn].bodyWeight < :wmax")
 			.setParameter("sn", "ant-123")
 			.setParameter("desc", "%big%")
 			.setParameter("wmin", new Float(123.32))
 			.setParameter("wmax", new Float(167.89))
 			.list();*/
 		/*s.createQuery("from Human where addresses[:type].city = :city and addresses[:type].country = :country")
 			.setParameter("type", "home")
 			.setParameter("city", "Melbourne")
 			.setParameter("country", "Australia")
 			.list();*/
 		t.commit();
 		s.close();
 	}
 
 	public void testAggregation() {
 		Session s = openSession();
 		s.beginTransaction();
 		Human h = new Human();
 		h.setBodyWeight( (float) 74.0 );
 		h.setHeightInches(120.5);
 		h.setDescription("Me");
 		h.setName( new Name("Gavin", 'A', "King") );
 		h.setNickName("Oney");
 		s.persist(h);
 		Double sum = (Double) s.createQuery("select sum(h.bodyWeight) from Human h").uniqueResult();
 		Double avg = (Double) s.createQuery("select avg(h.heightInches) from Human h").uniqueResult();	// uses custom read and write for column
 		assertEquals(sum.floatValue(), 74.0, 0.01);
 		assertEquals(avg.doubleValue(), 120.5, 0.01);
 		Long id = (Long) s.createQuery("select max(a.id) from Animal a").uniqueResult();
 		assertNotNull( id );
 		s.delete( h );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		h = new Human();
 		h.setFloatValue( 2.5F );
 		h.setIntValue( 1 );
 		s.persist( h );
 		Human h2 = new Human();
 		h2.setFloatValue( 2.5F );
 		h2.setIntValue( 2 );
 		s.persist( h2 );
 		Object[] results = (Object[]) s.createQuery( "select sum(h.floatValue), avg(h.floatValue), sum(h.intValue), avg(h.intValue) from Human h" )
 				.uniqueResult();
 		// spec says sum() on a float or double value should result in double
 		assertTrue( Double.class.isInstance( results[0] ) );
 		assertEquals( 5D, results[0] );
 		// avg() should return a double
 		assertTrue( Double.class.isInstance( results[1] ) );
 		assertEquals( 2.5D, results[1] );
 		// spec says sum() on short, int or long should result in long
 		assertTrue( Long.class.isInstance( results[2] ) );
 		assertEquals( 3L, results[2] );
 		// avg() should return a double
 		assertTrue( Double.class.isInstance( results[3] ) );
 		assertEquals( 1.5D, results[3] );
 		s.delete(h);
 		s.delete(h2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testSelectClauseCase() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Human h = new Human();
 		h.setBodyWeight( (float) 74.0 );
 		h.setHeightInches(120.5);
 		h.setDescription("Me");
 		h.setName( new Name("Gavin", 'A', "King") );
 		h.setNickName("Oney");
 		s.persist(h);
 		String name = (String) s.createQuery("select case nickName when 'Oney' then 'gavin' when 'Turin' then 'christian' else nickName end from Human").uniqueResult();
 		assertEquals(name, "gavin");
 		String result = (String) s.createQuery("select case when bodyWeight > 100 then 'fat' else 'skinny' end from Human").uniqueResult();
 		assertEquals(result, "skinny");
 		s.delete(h);
 		t.commit();
 		s.close();
 	}
 
 	public void testImplicitPolymorphism() {
 		if(getDialect() instanceof IngresDialect){
 			//HHH-4976 Ingres 9.3 does not support sub-selects in the select list.
 			return;
 		}
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Product product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "4321" );
 		s.save( product );
 
 		List list = s.createQuery("from java.lang.Comparable").list();
 		assertEquals( list.size(), 0 );
 
 		list = s.createQuery("from java.lang.Object").list();
 		assertEquals( list.size(), 1 );
 
 		s.delete(product);
 
 		list = s.createQuery("from java.lang.Object").list();
 		assertEquals( list.size(), 0 );
 
 		t.commit();
 		s.close();
 	}
 
 	public void testCoalesce() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.createQuery("from Human h where coalesce(h.nickName, h.name.first, h.name.last) = 'max'").list();
 		session.createQuery("select nullif(nickName, '1e1') from Human").list();
 		txn.commit();
 		session.close();
 	}
 
 	public void testStr() {
 		Session session = openSession();
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
index 95528c7907..98c8b401b3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
@@ -1,1116 +1,1121 @@
 //$Id: HQLTest.java 11374 2007-03-29 19:09:18Z steve.ebersole@jboss.com $
 package org.hibernate.test.hql;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import junit.framework.Test;
 import org.hibernate.Hibernate;
 import org.hibernate.QueryException;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.ReturnMetadata;
 import org.hibernate.hql.QueryTranslator;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.hql.antlr.HqlTokenTypes;
 import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
 import org.hibernate.hql.ast.DetailedSemanticException;
 import org.hibernate.hql.ast.QuerySyntaxException;
 import org.hibernate.hql.ast.QueryTranslatorImpl;
 import org.hibernate.hql.ast.SqlGenerator;
 import org.hibernate.hql.ast.tree.ConstructorNode;
 import org.hibernate.hql.ast.tree.DotNode;
 import org.hibernate.hql.ast.tree.FromReferenceNode;
 import org.hibernate.hql.ast.tree.IndexNode;
 import org.hibernate.hql.ast.tree.QueryNode;
 import org.hibernate.hql.ast.tree.SelectClause;
 import org.hibernate.hql.ast.util.ASTUtil;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 import antlr.RecognitionException;
 import antlr.collections.AST;
 
 /**
  * Tests cases where the AST based query translator and the 'classic' query translator generate identical SQL.
  *
  * @author Gavin King
  */
 public class HQLTest extends QueryTranslatorTestCase {
 
 	public HQLTest(String x) {
 		super( x );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( HQLTest.class );
 	}
 
 	public boolean createSchema() {
 		return false;
 	}
 
 	public boolean recreateSchemaAfterFailure() {
 		return false;
 	}
 
 	protected void prepareTest() throws Exception {
 		super.prepareTest();
 		SelectClause.VERSION2_SQL = true;
 		DotNode.REGRESSION_STYLE_JOIN_SUPPRESSION = true;
 		DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER = new DotNode.IllegalCollectionDereferenceExceptionBuilder() {
 			public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
 				throw new QueryException( "illegal syntax near collection: " + propertyName );
 			}
 		};
 		SqlGenerator.REGRESSION_STYLE_CROSS_JOINS = true;
 	}
 
 	protected void cleanupTest() throws Exception {
 		SelectClause.VERSION2_SQL = false;
 		DotNode.REGRESSION_STYLE_JOIN_SUPPRESSION = false;
 		DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER = DotNode.DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
 		SqlGenerator.REGRESSION_STYLE_CROSS_JOINS = false;
 		super.cleanupTest();
 	}
 
 	public void testModulo() {
 		assertTranslation( "from Animal a where a.bodyWeight % 2 = 0" );
 	}
 
 	public void testInvalidCollectionDereferencesFail() {
 		// should fail with the same exceptions (because of the DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER injection)
 		assertTranslation( "from Animal a where a.offspring.description = 'xyz'" );
 		assertTranslation( "from Animal a where a.offspring.father.description = 'xyz'" );
 	}
 	
     /**
      * ClassicQueryTranslatorFactory does not support translate tuple with "in" syntax to "and/or" clause
      */
     public void testRowValueConstructorSyntaxInInListFailureExpected() {
         assertTranslation( "from LineItem l where l.id in (:idList)" );
+		assertTranslation( "from LineItem l where l.id in :idList" );
     }
 
     public void testRowValueConstructorSyntaxInInList() {
     	if (!getDialect().supportsRowValueConstructorSyntaxInInList())
     		return;
 		QueryTranslatorImpl translator = createNewQueryTranslator("from LineItem l where l.id in (?)");
 		assertInExist("'in' should be translated to 'and'", false, translator);
+		translator = createNewQueryTranslator("from LineItem l where l.id in ?");
+		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (('a1',1,'b1'),('a2',2,'b2'))");
 		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in (?)");
 		assertInExist("only translate tuple with 'in' syntax", true, translator);
+		translator = createNewQueryTranslator("from Animal a where a.id in ?");
+		assertInExist("only translate tuple with 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (select a1 from Animal a1 left join a1.offspring o where a1.id = 1)");
 		assertInExist("do not translate subqueries", true, translator);
 
     }
 
 	private void assertInExist( String message, boolean expected, QueryTranslatorImpl translator ) {
 		AST ast = translator.getSqlAST().getWalker().getAST();
 		QueryNode queryNode = (QueryNode) ast;
 		AST inNode = ASTUtil.findTypeInChildren( queryNode, HqlTokenTypes.IN );
 		assertEquals( message, expected, inNode != null );
 	}
     
 	public void testSubComponentReferences() {
 		assertTranslation( "select c.address.zip.code from ComponentContainer c" );
 		assertTranslation( "select c.address.zip from ComponentContainer c" );
 		assertTranslation( "select c.address from ComponentContainer c" );
 	}
 
 	public void testManyToAnyReferences() {
 		assertTranslation( "from PropertySet p where p.someSpecificProperty.id is not null" );
 		assertTranslation( "from PropertySet p join p.generalProperties gp where gp.id is not null" );
 	}
 
 	public void testJoinFetchCollectionOfValues() {
 		assertTranslation( "select h from Human as h join fetch h.nickNames" );
 	}
 	
 	public void testCollectionMemberDeclarations() {
 		assertTranslation( "from Customer c, in(c.orders) o" );
 		assertTranslation( "from Customer c, in(c.orders) as o" );
 		assertTranslation( "select c.name from Customer c, in(c.orders) as o where c.id = o.id.customerId" );
 	}
 	public void testCollectionMemberDeclarationsFailureExpected(){
 		// both these two query translators throw exeptions for this HQL since
 		// IN asks an alias, but the difference is that the error message from AST
 		// contains the error token location (by lines and columns), which is hardly 
 		// to get from Classic query translator --stliu
 		assertTranslation( "from Customer c, in(c.orders)" ); 
 	}
 
 	public void testCollectionJoinsInSubselect() {
 		// caused by some goofiness in FromElementFactory that tries to
 		// handle correlated subqueries (but fails miserably) even though this
 		// is not a correlated subquery.  HHH-1248
 		assertTranslation(
 				"select a.id, a.description" +
 				" from Animal a" +
 				"       left join a.offspring" +
 				" where a in (" +
 				"       select a1 from Animal a1" +
 				"           left join a1.offspring o" +
 				"       where a1.id=1" +
 		        ")"
 		);
 		assertTranslation(
 				"select h.id, h.description" +
 		        " from Human h" +
 				"      left join h.friends" +
 				" where h in (" +
 				"      select h1" +
 				"      from Human h1" +
 				"          left join h1.friends f" +
 				"      where h1.id=1" +
 				")"
 		);
 	}
 
 	public void testEmptyInListFailureExpected() {
 		assertTranslation( "select a from Animal a where a.description in ()" );
 	}
 
 	public void testDateTimeArithmeticReturnTypesAndParameterGuessing() {
 		QueryTranslatorImpl translator = createNewQueryTranslator( "select o.orderDate - o.orderDate from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", Hibernate.DOUBLE, translator.getReturnTypes()[0] );
 		translator = createNewQueryTranslator( "select o.orderDate + 2 from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", Hibernate.CALENDAR_DATE, translator.getReturnTypes()[0] );
 		translator = createNewQueryTranslator( "select o.orderDate -2 from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", Hibernate.CALENDAR_DATE, translator.getReturnTypes()[0] );
 
 		translator = createNewQueryTranslator( "from Order o where o.orderDate > ?" );
 		assertEquals( "incorrect expected param type", Hibernate.CALENDAR_DATE, translator.getParameterTranslations().getOrdinalParameterExpectedType( 1 ) );
 
 		translator = createNewQueryTranslator( "select o.orderDate + ? from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", Hibernate.CALENDAR_DATE, translator.getReturnTypes()[0] );
 		assertEquals( "incorrect expected param type", Hibernate.DOUBLE, translator.getParameterTranslations().getOrdinalParameterExpectedType( 1 ) );
 
 	}
 
 	public void testReturnMetadata() {
 		HQLQueryPlan plan = createQueryPlan( "from Animal a" );
 		check( plan.getReturnMetadata(), false, true );
 
 		plan = createQueryPlan( "select a as animal from Animal a" );
 		check( plan.getReturnMetadata(), false, false );
 
 		plan = createQueryPlan( "from java.lang.Object" );
 		check( plan.getReturnMetadata(), true, true );
 
 		plan = createQueryPlan( "select o as entity from java.lang.Object o" );
 		check( plan.getReturnMetadata(), true, false );
 	}
 
 	private void check(
 			ReturnMetadata returnMetadata,
 	        boolean expectingEmptyTypes,
 	        boolean expectingEmptyAliases) {
 		assertNotNull( "null return metadata", returnMetadata );
 		assertNotNull( "null return metadata - types", returnMetadata );
 		assertEquals( "unexpected return size", 1, returnMetadata.getReturnTypes().length );
 		if ( expectingEmptyTypes ) {
 			assertNull( "non-empty types", returnMetadata.getReturnTypes()[0] );
 		}
 		else {
 			assertNotNull( "empty types", returnMetadata.getReturnTypes()[0] );
 		}
 		if ( expectingEmptyAliases ) {
 			assertNull( "non-empty aliases", returnMetadata.getReturnAliases() );
 		}
 		else {
 			assertNotNull( "empty aliases", returnMetadata.getReturnAliases() );
 			assertNotNull( "empty aliases", returnMetadata.getReturnAliases()[0] );
 		}
 	}
 
 	public void testImplicitJoinsAlongWithCartesianProduct() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo.foo from Foo foo, Foo foo2" );
 		assertTranslation( "select foo.foo.foo from Foo foo, Foo foo2" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	public void testSubselectBetween() {
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) between :min and :max");
 		assertTranslation("from Animal x where (select max(a.description) from Animal a) like 'big%'");
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) is not null");
 		assertTranslation("from Animal x where exists (select max(a.bodyWeight) from Animal a)");
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) in (1,2,3)");
 	}
 
 	public void testFetchOrderBy() {
 		assertTranslation("from Animal a left outer join fetch a.offspring where a.mother.id = :mid order by a.description");
 	}
 
 	public void testCollectionOrderBy() {
 		assertTranslation("from Animal a join a.offspring o order by a.description");
 		assertTranslation("from Animal a join fetch a.offspring order by a.description");
 		assertTranslation("from Animal a join fetch a.offspring o order by o.description");
 		assertTranslation("from Animal a join a.offspring o order by a.description, o.description");
 	}
 
 	public void testExpressionWithParamInFunction() {
 		assertTranslation("from Animal a where abs(a.bodyWeight-:param) < 2.0");
 		assertTranslation("from Animal a where abs(:param - a.bodyWeight) < 2.0");
 		assertTranslation("from Animal where abs(:x - :y) < 2.0");
 		assertTranslation("from Animal where lower(upper(:foo)) like 'f%'");
 		if ( ! ( getDialect() instanceof SybaseDialect ) &&  ! ( getDialect() instanceof Sybase11Dialect ) &&  ! ( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SQLServerDialect ) ) {
 			// Transact-SQL dialects (except SybaseAnywhereDialect) map the length function -> len; 
 			// classic translator does not consider that *when nested*;
 			// SybaseAnywhereDialect supports the length function
 
 			assertTranslation("from Animal a where abs(abs(a.bodyWeight - 1.0 + :param) * abs(length('ffobar')-3)) = 3.0");
 		}
 		if ( !( getDialect() instanceof MySQLDialect ) && ! ( getDialect() instanceof SybaseDialect ) && ! ( getDialect() instanceof Sybase11Dialect ) && !( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SybaseAnywhereDialect ) && ! ( getDialect() instanceof SQLServerDialect ) ) {
 			assertTranslation("from Animal where lower(upper('foo') || upper(:bar)) like 'f%'");
 		}
 		if ( getDialect() instanceof PostgreSQLDialect ) {
 			return;
 		}
 		assertTranslation("from Animal where abs(cast(1 as float) - cast(:param as float)) = 1.0");
 	}
 
 	public void testCompositeKeysWithPropertyNamedId() {
 		assertTranslation( "select e.id.id from EntityWithCrazyCompositeKey e" );
 		assertTranslation( "select max(e.id.id) from EntityWithCrazyCompositeKey e" );
 	}
 
 	public void testMaxindexHqlFunctionInElementAccessorFailureExpected() {
 		//TODO: broken SQL
 		//      steve (2005.10.06) - this is perfect SQL, but fairly different from the old parser
 		//              tested : HSQLDB (1.8), Oracle8i
 		assertTranslation( "select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2" );
 		assertTranslation( "select c from Container c where c.manyToMany[ maxIndex(c.manyToMany) ].count = 2" );
 	}
 
 	public void testMultipleElementAccessorOperatorsFailureExpected() throws Exception {
 		//TODO: broken SQL
 		//      steve (2005.10.06) - Yes, this is all hosed ;)
 		assertTranslation( "select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'" );
 		assertTranslation( "select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'" );
 	}
 
 	/*public void testSelectMaxElements() throws Exception {
 		//TODO: this is almost correct, but missing a select-clause column alias!
 		assertTranslation("select max( elements(one.manies) ) from org.hibernate.test.legacy.One one");
 	}*/
 
 	public void testKeyManyToOneJoinFailureExpected() {
 		//TODO: new parser generates unnecessary joins (though the query results are correct)
 		assertTranslation( "from Order o left join fetch o.lineItems li left join fetch li.product p" );
 		assertTranslation( "from Outer o where o.id.master.id.sup.dudu is not null" );
 		assertTranslation( "from Outer o where o.id.master.id.sup.dudu is not null" );
 	}
 
 	public void testDuplicateExplicitJoinFailureExpected() throws Exception {
 		//very minor issue with select clause:
 		assertTranslation( "from Animal a join a.mother m1 join a.mother m2" );
 		assertTranslation( "from Zoo zoo join zoo.animals an join zoo.mammals m" );
 		assertTranslation( "from Zoo zoo join zoo.mammals an join zoo.mammals m" );
 	}
 
 	// TESTS THAT FAIL ONLY ON DIALECTS WITH THETA-STYLE OUTERJOINS:
 
 	public void testIndexWithExplicitJoin() throws Exception {
 		//TODO: broken on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo join zoo.animals an where zoo.mammals[ index(an) ] = an" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where zoo.mammals[ index(dog) ] = dog" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where dog = zoo.mammals[ index(dog) ]" );
 	}
 
 	public void testOneToManyMapIndex() throws Exception {
 		//TODO: this breaks on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].description like '%black%'" );
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].father.description like '%black%'" );
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].father.id = 1234" );
 		assertTranslation( "from Zoo zoo where zoo.animals['1234'].description like '%black%'" );
 	}
 
 	public void testExplicitJoinMapIndex() throws Exception {
 		//TODO: this breaks on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo, Dog dog where zoo.mammals['dog'] = dog" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where zoo.mammals['dog'] = dog" );
 	}
 
 	public void testIndexFunction() throws Exception {
 		// Instead of doing the pre-processor trick like the existing QueryTranslator, this
 		// is handled by MethodNode.
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		//TODO: broken on dialects with theta-style outerjoins:
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where index(dog) = 'dog'" );
 		assertTranslation( "from Zoo zoo join zoo.animals an where index(an) = '1234'" );
 	}
 
 	public void testSelectCollectionOfValues() throws Exception {
 		//TODO: broken on dialects with theta-style joins
 		///old parser had a bug where the collection element was not included in return types!
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "select baz, date from Baz baz join baz.stringDateMap date where index(date) = 'foo'" );
 	}
 
 	public void testCollectionOfValues() throws Exception {
 		//old parser had a bug where the collection element was not returned!
 		//TODO: broken on dialects with theta-style joins
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Baz baz join baz.stringDateMap date where index(date) = 'foo'" );
 	}
 
     public void testHHH719() throws Exception {
         assertTranslation("from Baz b order by org.bazco.SpecialFunction(b.id)");
         assertTranslation("from Baz b order by anypackage.anyFunction(b.id)");
     }
 
 
 	//PASSING TESTS:
 
 	public void testParameterListExpansion() {
 		assertTranslation( "from Animal as animal where animal.id in (:idList_1, :idList_2)" );
 	}
 
 	public void testComponentManyToOneDereferenceShortcut() {
 		assertTranslation( "from Zoo z where z.address.stateProvince.id is null" );
 	}
 
 	public void testNestedCollectionImplicitJoins() {
 		// HHH-770
 		assertTranslation( "select h.friends.offspring from Human h" );
 	}
 
 	public void testExplicitJoinsInSubquery() {
 		// test for HHH-557,
 		// TODO : this passes regardless because the only difference between the two sqls is one extra comma
 		// (commas are eaten by the tokenizer during asserTranslation when building the token maps).
 		assertTranslation(
 		        "from org.hibernate.test.hql.Animal as animal " +
 		        "where animal.id in (" +
 		        "        select a.id " +
 		        "        from org.hibernate.test.hql.Animal as a " +
 		        "               left join a.mother as mo" +
 		        ")"
 		);
 	}
 
 	public void testImplicitJoinsInGroupBy() {
 		assertTranslation(
 		        "select o.mother.bodyWeight, count(distinct o) " +
 		        "from Animal an " +
 		        "   join an.offspring as o " +
 		        "group by o.mother.bodyWeight"
 		);
 	}
 
 	public void testCrazyIdFieldNames() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		// only regress against non-scalar forms as there appears to be a bug in the classic translator
 		// in regards to this issue also.  Specifically, it interprets the wrong return type, though it gets
 		// the sql "correct" :/
 
 		String hql = "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null";
 		assertTranslation( hql, new HashMap(), false, ( String ) null );
 
 	    hql = "select e.heresAnotherCrazyIdFieldName.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null";
 		assertTranslation( hql, new HashMap(), false, ( String ) null );
 
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	public void testSizeFunctionAndProperty() {
 		assertTranslation("from Animal a where a.offspring.size > 0");
 		assertTranslation("from Animal a join a.offspring where a.offspring.size > 1");
 		assertTranslation("from Animal a where size(a.offspring) > 0");
 		assertTranslation("from Animal a join a.offspring o where size(a.offspring) > 1");
 		assertTranslation("from Animal a where size(a.offspring) > 1 and size(a.offspring) < 100");
 
 		assertTranslation("from Human a where a.family.size > 0");
 		assertTranslation("from Human a join a.family where a.family.size > 1");
 		assertTranslation("from Human a where size(a.family) > 0");
 		assertTranslation("from Human a join a.family o where size(a.family) > 1");
 		assertTranslation("from Human a where a.family.size > 0 and a.family.size < 100");
 }
 
 	// Do the simplest test first!
 	public void testFromOnly() throws Exception {
 		// 2004-06-21 [jsd] This test now works with the new AST based QueryTranslatorImpl.
 		assertTranslation( "from Animal" );
 		assertTranslation( "from Model" );
 	}
 
 	public void testJoinPathEndingInValueCollection() {
 		assertTranslation( "select h from Human as h join h.nickNames as nn where h.nickName=:nn1 and (nn=:nn2 or nn=:nn3)" );
 	}
 
 	public void testSerialJoinPathEndingInValueCollection() {
 		// HHH-242
 		assertTranslation( "select h from Human as h join h.friends as f join f.nickNames as nn where h.nickName=:nn1 and (nn=:nn2 or nn=:nn3)" );
 	}
 
 	public void testImplicitJoinContainedByCollectionFunction() {
 		// HHH-281 : Implied joins in a collection function (i.e., indices or elements)
 		assertTranslation( "from Human as h where 'shipping' in indices(h.father.addresses)" );
 		assertTranslation( "from Human as h where 'shipping' in indices(h.father.father.addresses)" );
 		assertTranslation( "from Human as h where 'sparky' in elements(h.father.nickNames)" );
 		assertTranslation( "from Human as h where 'sparky' in elements(h.father.father.nickNames)" );
 	}
 
 
 	public void testImpliedJoinInSubselectFrom() {
 		// HHH-276 : Implied joins in a from in a subselect.
 		assertTranslation( "from Animal a where exists( from a.mother.offspring )" );
 	}
 
 	public void testSubselectImplicitJoins() {
 		// HHH-276 : Implied joins in a from in a subselect.
 		assertTranslation( "from Simple s where s = some( select sim from Simple sim where sim.other.count=s.other.count )" );
 	}
 
 
 	public void testCollectionOfValuesSize() throws Exception {
 		//SQL *was* missing a comma
 		assertTranslation( "select size(baz.stringDateMap) from org.hibernate.test.legacy.Baz baz" );
 	}
 
 	public void testCollectionFunctions() throws Exception {
 		//these are both broken, a join that belongs in the subselect finds its way into the main query
 		assertTranslation( "from Zoo zoo where size(zoo.animals) > 100" );
 		assertTranslation( "from Zoo zoo where maxindex(zoo.mammals) = 'dog'" );
 	}
 
 	public void testImplicitJoinInExplicitJoin() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother.mother gm" );
 		assertTranslation( "from Animal an inner join an.mother.mother.mother ggm" );
 		assertTranslation( "from Animal an inner join an.mother.mother.mother.mother gggm" );
 	}
 
 	public void testImpliedManyToManyProperty() throws Exception {
 		//missing a table join (SQL correct for a one-to-many, not for a many-to-many)
 		assertTranslation( "select c from ContainerX c where c.manyToMany[0].name = 's'" );
 	}
 
 	public void testCollectionSize() throws Exception {
 		//SQL is correct, query spaces *was* missing a table
 		assertTranslation( "select size(zoo.animals) from Zoo zoo" );
 	}
 
 	/*public void testCollectionIndexFunctionsInSelect() throws Exception {
 		assertTranslation("select maxindex(zoo.animals) from Zoo zoo");
 		assertTranslation("select minindex(zoo.animals) from Zoo zoo");
 		assertTranslation("select indices(zoo.animals) from Zoo zoo");
 	}
 
 	public void testCollectionElementFunctionsInSelect() throws Exception {
 		assertTranslation("select maxelement(zoo.animals) from Zoo zoo");
 		assertTranslation("select minelement(zoo.animals) from Zoo zoo");
 		assertTranslation("select elements(zoo.animals) from Zoo zoo");
 	}*/
 
 	public void testFetchCollectionOfValues() throws Exception {
 		assertTranslation( "from Baz baz left join fetch baz.stringSet" );
 	}
 
 	public void testFetchList() throws Exception {
 		assertTranslation( "from User u join fetch u.permissions" );
 	}
 
 	public void testCollectionFetchWithExplicitThetaJoin() {
 		assertTranslation( "select m from Master m1, Master m left join fetch m.details where m.name=m1.name" );
 	}
 
 	/*public void testListElementFunctionInSelect() throws Exception {
 		//wrong pk column in select clause! (easy fix?)
 		assertTranslation("select maxelement(u.permissions) from User u");
 		assertTranslation("select elements(u.permissions) from User u");
 	}*/
 
 	public void testListElementFunctionInWhere() throws Exception {
 		assertTranslation( "from User u where 'read' in elements(u.permissions)" );
 		assertTranslation( "from User u where 'write' <> all elements(u.permissions)" );
 	}
 
 	/*public void testManyToManyElementFunctionInSelect() throws Exception {
 		assertTranslation("select maxelement(human.friends) from Human human");
 		assertTranslation("select elements(human.friends) from Human human");
 	}*/
 
 	public void testManyToManyMaxElementFunctionInWhere() throws Exception {
 		//completely broken!!
 		assertTranslation( "from Human human where 5 = maxelement(human.friends)" );
 	}
 
 	public void testCollectionIndexFunctionsInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 = maxindex(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 2 = minindex(zoo.animals)" );
 	}
 
 	public void testCollectionIndicesInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 > some indices(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 4 > all indices(zoo.animals)" );
 	}
 
 	public void testIndicesInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 in indices(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where exists indices(zoo.animals)" );
 	}
 
 	public void testCollectionElementInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 > some elements(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 4 > all elements(zoo.animals)" );
 	}
 
 	public void testElementsInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 in elements(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where exists elements(zoo.animals)" );
 	}
 
 	public void testNull() throws Exception {
 		assertTranslation( "from Human h where h.nickName is null" );
 		assertTranslation( "from Human h where h.nickName is not null" );
 	}
 
 	public void testSubstitutions() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		replacements.put("yes", "'Y'");
 		assertTranslation( "from Human h where h.pregnant = true", replacements );
 		assertTranslation( "from Human h where h.pregnant = yes", replacements );
 		assertTranslation( "from Human h where h.pregnant = foo", replacements );
 	}
 
 	public void testWhere() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > 10" );
 		// 2004-06-26 [jsd] This one requires NOT GT => LE transform.
 		assertTranslation( "from Animal an where not an.bodyWeight > 10" );
 		assertTranslation( "from Animal an where an.bodyWeight between 0 and 10" );
 		assertTranslation( "from Animal an where an.bodyWeight not between 0 and 10" );
 		assertTranslation( "from Animal an where sqrt(an.bodyWeight)/2 > 10" );
 		// 2004-06-27 [jsd] Recognize 'is null' properly.  Generate 'and' and 'or' as well.
 		assertTranslation( "from Animal an where (an.bodyWeight > 10 and an.bodyWeight < 100) or an.bodyWeight is null" );
 	}
 
 	public void testEscapedQuote() throws Exception {
 		assertTranslation( "from Human h where h.nickName='1 ov''tha''few'");
 	}
 
 	public void testCaseWhenElse() {
 		assertTranslation( "from Human h where case when h.nickName='1ovthafew' then 'Gavin' when h.nickName='turin' then 'Christian' else h.nickName end = h.name.first" );
 	}
 
 	public void testCaseExprWhenElse() {
 		assertTranslation( "from Human h where case h.nickName when '1ovthafew' then 'Gavin' when 'turin' then 'Christian' else h.nickName end = h.name.first" );
 	}
 
 	public void testInvalidHql() throws Exception {
 		Exception newException = compileBadHql( "from Animal foo where an.bodyWeight > 10", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select an.name from Animal foo", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from Animal foo where an.verybogus > 10", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select an.boguspropertyname from Animal foo", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select an.name", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from Animal an where (((an.bodyWeight > 10 and an.bodyWeight < 100)) or an.bodyWeight is null", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from Animal an where an.bodyWeight is null where an.bodyWeight is null", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from where name='foo'", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "from NonexistentClass where name='foo'", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select new FOO_BOGUS_Animal(an.description, an.bodyWeight) from Animal an", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 		newException = compileBadHql( "select new Animal(an.description, an.bodyWeight, 666) from Animal an", false );
 		assertTrue( "Wrong exception type!", newException instanceof QuerySyntaxException );
 
 	}
 
 	public void testWhereBetween() throws Exception {
 		// 2004-08-31 [jsd] This "just worked"! Woohoo!
 		assertTranslation( "from Animal an where an.bodyWeight between 1 and 10" );
 	}
 
 	public void testConcatenation() {
 		if ( getDialect() instanceof MySQLDialect || getDialect() instanceof SybaseDialect
 				|| getDialect() instanceof Sybase11Dialect
 				|| getDialect() instanceof SybaseASE15Dialect
 				|| getDialect() instanceof SybaseAnywhereDialect
 				|| getDialect() instanceof SQLServerDialect 
 				|| getDialect() instanceof IngresDialect) {
 			// SybaseASE15Dialect and SybaseAnywhereDialect support '||'
 			// MySQL uses concat(x, y, z)
 			// SQL Server replaces '||' with '+'
 			//
 			// this is syntax checked in {@link ASTParserLoadingTest#testConcatenation} 
 			// Ingres supports both "||" and '+' but IngresDialect originally
 			// uses '+' operator; updated Ingres9Dialect to use "||".
 			return;
 		}
 		assertTranslation("from Human h where h.nickName = '1' || 'ov' || 'tha' || 'few'");
 	}
 
 	public void testWhereLike() throws Exception {
 		assertTranslation( "from Animal a where a.description like '%black%'" );
 		assertTranslation( "from Animal an where an.description like '%fat%'" );
 		assertTranslation( "from Animal an where lower(an.description) like '%fat%'" );
 	}
 
 	public void testWhereIn() throws Exception {
 		assertTranslation( "from Animal an where an.description in ('fat', 'skinny')" );
 	}
 
 	public void testLiteralInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs(5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(-5)" );
 	}
 
 	public void testExpressionInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3-5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3/5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3+5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3*5)" );
 		SQLFunction concat = getSessionFactoryImplementor().getSqlFunctionRegistry().findSQLFunction( "concat");
 		List list = new ArrayList(); list.add("'fat'"); list.add("'skinny'");
 		assertTranslation( "from Animal an where an.description = " + concat.render(Hibernate.STRING, list, getSessionFactoryImplementor()) );
 	}
 
 	public void testNotOrWhereClause() {
 		assertTranslation( "from Simple s where 'foo'='bar' or not 'foo'='foo'" );
 		assertTranslation( "from Simple s where 'foo'='bar' or not ('foo'='foo')" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' or 'foo'='foo' )" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' and 'foo'='foo' )" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' and 'foo'='foo' ) or not ('x'='y')" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' or 'foo'='foo' ) and not ('x'='y')" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' or 'foo'='foo' ) and 'x'='y'" );
 		assertTranslation( "from Simple s where not ( 'foo'='bar' and 'foo'='foo' ) or 'x'='y'" );
 		assertTranslation( "from Simple s where 'foo'='bar' and 'foo'='foo' or not 'x'='y'" );
 		assertTranslation( "from Simple s where 'foo'='bar' or 'foo'='foo' and not 'x'='y'" );
 		assertTranslation( "from Simple s where ('foo'='bar' and 'foo'='foo') or 'x'='y'" );
 		assertTranslation( "from Simple s where ('foo'='bar' or 'foo'='foo') and 'x'='y'" );
 		assertTranslation( "from Simple s where not( upper( s.name ) ='yada' or 1=2 or 'foo'='bar' or not('foo'='foo') or 'foo' like 'bar' )" );
 	}
 
 	public void testComplexExpressionInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs((3-5)/4)" );
 	}
 
 	public void testStandardFunctions() throws Exception {
 		assertTranslation( "from Animal where current_date = current_time" );
 		assertTranslation( "from Animal a where upper(a.description) = 'FAT'" );
 		assertTranslation( "select lower(a.description) from Animal a" );
 	}
 
 	public void testOrderBy() throws Exception {
 		assertTranslation( "from Animal an order by an.bodyWeight" );
 		assertTranslation( "from Animal an order by an.bodyWeight asc" );
 		assertTranslation( "from Animal an order by an.bodyWeight desc" );
 		assertTranslation( "from Animal an order by sqrt(an.bodyWeight*4)/2" );
 		assertTranslation( "from Animal an order by an.mother.bodyWeight" );
 		assertTranslation( "from Animal an order by an.bodyWeight, an.description" );
 		assertTranslation( "from Animal an order by an.bodyWeight asc, an.description desc" );
 		if ( getDialect() instanceof HSQLDialect || getDialect() instanceof DB2Dialect ) {
 			assertTranslation( "from Human h order by sqrt(h.bodyWeight), year(h.birthdate)" );
 		}
 	}
 
 	public void testGroupByFunction() {
 		if ( getDialect() instanceof Oracle8iDialect ) return; // the new hiearchy...
 		if ( getDialect() instanceof PostgreSQLDialect ) return;
 		if ( ! H2Dialect.class.isInstance( getDialect() ) ) {
 			// H2 has no year function
 			assertTranslation( "select count(*) from Human h group by year(h.birthdate)" );
 			assertTranslation( "select count(*) from Human h group by year(sysdate)" );
 		}
 		assertTranslation( "select count(*) from Human h group by trunc( sqrt(h.bodyWeight*4)/2 )" );
 	}
 
 
 	public void testPolymorphism() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Mammal" );
 		assertTranslation( "from Dog" );
 		assertTranslation( "from Mammal m where m.pregnant = false and m.bodyWeight > 10", replacements );
 		assertTranslation( "from Dog d where d.pregnant = false and d.bodyWeight > 10", replacements );
 	}
 
 	private Map buildTrueFalseReplacementMapForDialect() {
 		HashMap replacements = new HashMap();
 		try {
 			String dialectTrueRepresentation = getDialect().toBooleanValueString( true );
 			// if this call succeeds, then the dialect is saying to represent true/false as int values...
 			Integer.parseInt( dialectTrueRepresentation );
 			replacements.put( "true", "1" );
 			replacements.put( "false", "0" );
 		}
 		catch( NumberFormatException nfe ) {
 			// the Integer#parseInt call failed...
 		}
 		return replacements;
 	}
 
 	public void testTokenReplacement() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Mammal m where m.pregnant = false and m.bodyWeight > 10", replacements );
 	}
 
 	public void testProduct() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Animal, Animal" );
 		assertTranslation( "from Animal x, Animal y where x.bodyWeight = y.bodyWeight" );
 		assertTranslation( "from Animal x, Mammal y where x.bodyWeight = y.bodyWeight and not y.pregnant = true", replacements );
 		assertTranslation( "from Mammal, Mammal" );
 	}
 
 	public void testJoinedSubclassProduct() throws Exception {
 		assertTranslation( "from PettingZoo, PettingZoo" ); //product of two subclasses
 	}
 
 	public void testProjectProduct() throws Exception {
 		assertTranslation( "select x from Human x, Human y where x.nickName = y.nickName" );
 		assertTranslation( "select x, y from Human x, Human y where x.nickName = y.nickName" );
 	}
 
 	public void testExplicitEntityJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo" );
 		assertTranslation( "from Animal an left outer join an.mother mo" );
 		assertTranslation( "from Animal an left outer join fetch an.mother" );
 	}
 
 	public void testMultipleExplicitEntityJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo inner join mo.mother gm" );
 		assertTranslation( "from Animal an left outer join an.mother mo left outer join mo.mother gm" );
 		assertTranslation( "from Animal an inner join an.mother m inner join an.father f" );
 		assertTranslation( "from Animal an left join fetch an.mother m left join fetch an.father f" );
 	}
 
 	public void testMultipleExplicitJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo inner join an.offspring os" );
 		assertTranslation( "from Animal an left outer join an.mother mo left outer join an.offspring os" );
 	}
 
 	public void testExplicitEntityJoinsWithRestriction() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	public void testIdProperty() throws Exception {
 		assertTranslation( "from Animal a where a.mother.id = 12" );
 	}
 
 	public void testSubclassAssociation() throws Exception {
 		assertTranslation( "from DomesticAnimal da join da.owner o where o.nickName = 'Gavin'" );
 		assertTranslation( "from DomesticAnimal da left join fetch da.owner" );
 		assertTranslation( "from Human h join h.pets p where p.pregnant = 1" );
 		assertTranslation( "from Human h join h.pets p where p.bodyWeight > 100" );
 		assertTranslation( "from Human h left join fetch h.pets" );
 	}
 
 	public void testExplicitCollectionJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.offspring os" );
 		assertTranslation( "from Animal an left outer join an.offspring os" );
 	}
 
 	public void testExplicitOuterJoinFetch() throws Exception {
 		assertTranslation( "from Animal an left outer join fetch an.offspring" );
 	}
 
 	public void testExplicitOuterJoinFetchWithSelect() throws Exception {
 		assertTranslation( "select an from Animal an left outer join fetch an.offspring" );
 	}
 
 	public void testExplicitJoins() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Zoo zoo join zoo.mammals mam where mam.pregnant = true and mam.description like '%white%'", replacements );
 		assertTranslation( "from Zoo zoo join zoo.animals an where an.description like '%white%'" );
 	}
 
     /**
      * Test for HHH-559
      */
     public void testMultibyteCharacterConstant() throws Exception {
         assertTranslation( "from Zoo zoo join zoo.animals an where an.description like '%\u4e2d%'" );
     }
 
 	public void testImplicitJoins() throws Exception {
 		// Two dots...
 		assertTranslation( "from Animal an where an.mother.bodyWeight > ?" );
 		assertTranslation( "from Animal an where an.mother.bodyWeight > 10" );
 		assertTranslation( "from Dog dog where dog.mother.bodyWeight > 10" );
 		// Three dots...
 		assertTranslation( "from Animal an where an.mother.mother.bodyWeight > 10" );
 		// The new QT doesn't throw an exception here, so this belongs in ASTQueryTranslator test. [jsd]
 //		assertTranslation( "from Animal an where an.offspring.mother.bodyWeight > 10" );
 		// Is not null (unary postfix operator)
 		assertTranslation( "from Animal an where an.mother is not null" );
 		// ID property shortut (no implicit join)
 		assertTranslation( "from Animal an where an.mother.id = 123" );
 	}
 
 	public void testImplicitJoinInSelect() {
 		assertTranslation( "select foo, foo.long from Foo foo" );
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo.foo from Foo foo" );
 		assertTranslation( "select foo, foo.foo from Foo foo" );
 		assertTranslation( "select foo.foo from Foo foo where foo.foo is not null" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	public void testSelectExpressions() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select an.mother.mother from Animal an" );
 		assertTranslation( "select an.mother.mother.mother from Animal an" );
 		assertTranslation( "select an.mother.mother.bodyWeight from Animal an" );
 		assertTranslation( "select an.mother.zoo.id from Animal an" );
 		assertTranslation( "select user.human.zoo.id from User user" );
 		assertTranslation( "select u.userName, u.human.name.first from User u" );
 		assertTranslation( "select u.human.name.last, u.human.name.first from User u" );
 		assertTranslation( "select bar.baz.name from Bar bar" );
 		assertTranslation( "select bar.baz.name, bar.baz.count from Bar bar" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	public void testSelectStandardFunctionsNoParens() throws Exception {
 		assertTranslation( "select current_date, current_time, current_timestamp from Animal" );
 	}
 
 	public void testMapIndex() throws Exception {
 		assertTranslation( "from User u where u.permissions['hibernate']='read'" );
 	}
 
 	/*public void testCollectionFunctionsInSelect() {
 		//sql is correct, just different order in select clause
 		assertTranslation("select baz, size(baz.stringSet), count( distinct elements(baz.stringSet) ), max( elements(baz.stringSet) ) from Baz baz group by baz");
 	}
 
 	public void testSelectElements() throws Exception {
 		assertTranslation( "select elements(fum1.friends) from org.hibernate.test.legacy.Fum fum1" );
 		assertTranslation( "select elements(one.manies) from org.hibernate.test.legacy.One one" );
 	}*/
 
 	public void testNamedParameters() throws Exception {
 		assertTranslation( "from Animal an where an.mother.bodyWeight > :weight" );
 	}
 
 	// Second set of examples....
 
 	public void testClassProperty() throws Exception {
 		// This test causes failures on theta-join dialects because the SQL is different.
 		// The queries are semantically the same however.
 		if ( getDialect() instanceof Oracle8iDialect ) return;
 		assertTranslation( "from Animal a where a.mother.class = Reptile" );
 	}
 
 	public void testComponent() throws Exception {
 		assertTranslation( "from Human h where h.name.first = 'Gavin'" );
 	}
 
 	public void testSelectEntity() throws Exception {
 		assertTranslation( "select an from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 		assertTranslation( "select mo, an from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	public void testValueAggregate() {
 		assertTranslation( "select max(p), min(p) from User u join u.permissions p" );
 	}
 
 	public void testAggregation() throws Exception {
 		assertTranslation( "select count(an) from Animal an" );
 		assertTranslation( "select count(*) from Animal an" );
 		assertTranslation( "select count(distinct an) from Animal an" );
 		assertTranslation( "select count(distinct an.id) from Animal an" );
 		assertTranslation( "select count(all an.id) from Animal an" );
 	}
 
 	public void testSelectProperty() throws Exception {
 		assertTranslation( "select an.bodyWeight, mo.bodyWeight from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	public void testSelectEntityProperty() throws Exception {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select an.mother from Animal an" );
 		assertTranslation( "select an, an.mother from Animal an" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	public void testSelectDistinctAll() throws Exception {
 		assertTranslation( "select distinct an.description, an.bodyWeight from Animal an" );
 		assertTranslation( "select all an from Animal an" );
 	}
 
 	public void testSelectAssociatedEntityId() throws Exception {
 		assertTranslation( "select an.mother.id from Animal an" );
 	}
 
 	public void testGroupBy() throws Exception {
 		assertTranslation( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id" );
 		assertTranslation( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id having max(an.bodyWeight)>1.0" );
 	}
 
 	public void testGroupByMultiple() throws Exception {
 		assertTranslation( "select s.id, s.count, count(t), max(t.date) from org.hibernate.test.legacy.Simple s, org.hibernate.test.legacy.Simple t where s.count = t.count group by s.id, s.count order by s.count" );
 	}
 
 	public void testManyToMany() throws Exception {
 		assertTranslation( "from Human h join h.friends f where f.nickName = 'Gavin'" );
 		assertTranslation( "from Human h join h.friends f where f.bodyWeight > 100" );
 	}
 
 	public void testManyToManyElementFunctionInWhere() throws Exception {
 		assertTranslation( "from Human human where human in elements(human.friends)" );
 		assertTranslation( "from Human human where human = some elements(human.friends)" );
 	}
 
 	public void testManyToManyElementFunctionInWhere2() throws Exception {
 		assertTranslation( "from Human h1, Human h2 where h2 in elements(h1.family)" );
 		assertTranslation( "from Human h1, Human h2 where 'father' in indices(h1.family)" );
 	}
 
 	public void testManyToManyFetch() throws Exception {
 		assertTranslation( "from Human h left join fetch h.friends" );
 	}
 
 	public void testManyToManyIndexAccessor() throws Exception {
 		// From ParentChildTest.testCollectionQuery()
 		assertTranslation( "select c from ContainerX c, Simple s where c.manyToMany[2] = s" );
 		assertTranslation( "select s from ContainerX c, Simple s where c.manyToMany[2] = s" );
 		assertTranslation( "from ContainerX c, Simple s where c.manyToMany[2] = s" );
 		//would be nice to have:
 		//assertTranslation( "select c.manyToMany[2] from ContainerX c" );
 	}
 
 	public void testSelectNew() throws Exception {
 		assertTranslation( "select new Animal(an.description, an.bodyWeight) from Animal an" );
 		assertTranslation( "select new org.hibernate.test.hql.Animal(an.description, an.bodyWeight) from Animal an" );
 	}
 
 	public void testSimpleCorrelatedSubselect() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight = (select o.bodyWeight from a.offspring o)" );
 		assertTranslation( "from Animal a where a = (from a.offspring o)" );
 	}
 
 	public void testSimpleUncorrelatedSubselect() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight = (select an.bodyWeight from Animal an)" );
 		assertTranslation( "from Animal a where a = (from Animal an)" );
 	}
 
 	public void testSimpleCorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a = (select o from a.offspring o)" );
 		assertTranslation( "from Animal a where a in (select o from a.offspring o)" );
 	}
 
 	public void testSimpleUncorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a = (select an from Animal an)" );
 		assertTranslation( "from Animal a where a in (select an from Animal an)" );
 	}
 
 	public void testUncorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight = (select max(an.bodyWeight) from Animal an)" );
 	}
 
 	public void testCorrelatedSubselect2() throws Exception {
 		assertTranslation( "from Animal a where a.bodyWeight > (select max(o.bodyWeight) from a.offspring o)" );
 	}
 
 	public void testManyToManyJoinInSubselect() throws Exception {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo from Foo foo where foo in (select elt from Baz baz join baz.fooArray elt)" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	public void testImplicitJoinInSubselect() throws Exception {
 		assertTranslation( "from Animal a where a = (select an.mother from Animal an)" );
 		assertTranslation( "from Animal a where a.id = (select an.mother.id from Animal an)" );
 	}
 
 	public void testManyToOneSubselect() {
 		//TODO: the join in the subselect also shows up in the outer query!
 		assertTranslation( "from Animal a where 'foo' in (select m.description from a.mother m)" );
 	}
 
 	public void testPositionalParameters() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > ?" );
 	}
 
 	public void testKeywordPropertyName() throws Exception {
 		assertTranslation( "from Glarch g order by g.order asc" );
 		assertTranslation( "select g.order from Glarch g where g.order = 3" );
 	}
 
 	public void testJavaConstant() throws Exception {
 		assertTranslation( "from org.hibernate.test.legacy.Category c where c.name = org.hibernate.test.legacy.Category.ROOT_CATEGORY" );
 		assertTranslation( "from org.hibernate.test.legacy.Category c where c.id = org.hibernate.test.legacy.Category.ROOT_ID" );
 		// todo : additional desired functionality
 		//assertTranslation( "from Category c where c.name = Category.ROOT_CATEGORY" );
 		//assertTranslation( "select c.name, Category.ROOT_ID from Category as c");
 	}
 
 	public void testClassName() throws Exception {
 		// The Zoo reference is OK; Zoo is discriminator-based;
 		// the old parser could handle these correctly
 		//
 		// However, the Animal one ares not; Animal is joined subclassing;
 		// the old parser does not handle thee correctly.  The new parser
 		// previously did not handle them correctly in that same way.  So they
 		// used to pass regression even though the output was bogus SQL...
 		//
 		// I have moved the Animal ones (plus duplicating the Zoo one)
 		// to ASTParserLoadingTest for syntax checking.
 		assertTranslation( "from Zoo zoo where zoo.class = PettingZoo" );
 //		assertTranslation( "from DomesticAnimal an where an.class = Dog" );
 //		assertTranslation( "from Animal an where an.class = Dog" );
 	}
 
 	public void testSelectDialectFunction() throws Exception {
 		// From SQLFunctionsTest.testDialectSQLFunctions...
 		if ( getDialect() instanceof HSQLDialect ) {
 			assertTranslation( "select mod(s.count, 2) from org.hibernate.test.legacy.Simple as s where s.id = 10" );
 			//assertTranslation( "from org.hibernate.test.legacy.Simple as s where mod(s.count, 2) = 0" );
 		}
 		assertTranslation( "select upper(human.name.first) from Human human" );
 		assertTranslation( "from Human human where lower(human.name.first) like 'gav%'" );
 		assertTranslation( "select upper(a.description) from Animal a" );
 		assertTranslation( "select max(a.bodyWeight) from Animal a" );
 	}
 
 	public void testTwoJoins() throws Exception {
 		assertTranslation( "from Human human join human.friends, Human h join h.mother" );
 		assertTranslation( "from Human human join human.friends f, Animal an join an.mother m where f=m" );
 		assertTranslation( "from Baz baz left join baz.fooToGlarch, Bar bar join bar.foo" );
 	}
 
 	public void testToOneToManyManyJoinSequence() throws Exception {
 		assertTranslation( "from Dog d join d.owner h join h.friends f where f.name.first like 'joe%'" );
 	}
 
 	public void testToOneToManyJoinSequence() throws Exception {
 		assertTranslation( "from Animal a join a.mother m join m.offspring" );
 		assertTranslation( "from Dog d join d.owner m join m.offspring" );
 		assertTranslation( "from Animal a join a.mother m join m.offspring o where o.bodyWeight > a.bodyWeight" );
 	}
 
 	public void testSubclassExplicitJoin() throws Exception {
 		assertTranslation( "from DomesticAnimal da join da.owner o where o.nickName = 'gavin'" );
 		assertTranslation( "from DomesticAnimal da join da.owner o where o.bodyWeight > 0" );
 	}
 
 	public void testMultipleExplicitCollectionJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.offspring os join os.offspring gc" );
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
index ca1f58f8d5..6a50a120fb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLFunctionsTest.java
@@ -1,705 +1,713 @@
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
 package org.hibernate.test.legacy;
 import static org.hibernate.TestLogger.LOG;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import junit.framework.Test;
 import org.hibernate.Query;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Transaction;
 import org.hibernate.classic.Session;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InterbaseDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle9iDialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 
 
 public class SQLFunctionsTest extends LegacyTestCase {
 
 	public SQLFunctionsTest(String name) {
 		super(name);
 	}
 
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/AltSimple.hbm.xml",
 			"legacy/Broken.hbm.xml",
 			"legacy/Blobber.hbm.xml"
 		};
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( SQLFunctionsTest.class );
 	}
 
 	public void testDialectSQLFunctions() throws Exception {
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Iterator iter = s.createQuery( "select max(s.count) from Simple s" ).iterate();
 
 		if ( getDialect() instanceof MySQLDialect ) assertTrue( iter.hasNext() && iter.next()==null );
 
 		Simple simple = new Simple();
 		simple.setName("Simple Dialect Function Test");
 		simple.setAddress("Simple Address");
 		simple.setPay(new Float(45.8));
 		simple.setCount(2);
 		s.save(simple, new Long(10) );
 
 		// Test to make sure allocating an specified object operates correctly.
 		assertTrue(
 				s.createQuery( "select new org.hibernate.test.legacy.S(s.count, s.address) from Simple s" ).list().size() == 1
 		);
 
 		// Quick check the base dialect functions operate correctly
 		assertTrue(
 				s.createQuery( "select max(s.count) from Simple s" ).list().size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select count(*) from Simple s" ).list().size() == 1
 		);
 
 		if ( getDialect() instanceof Oracle9iDialect ) {
 			// Check Oracle Dialect mix of dialect functions - no args (no parenthesis and single arg functions
 			List rset = s.createQuery( "select s.name, sysdate(), trunc(s.pay), round(s.pay) from Simple s" ).list();
 			assertNotNull("Name string should have been returned",(((Object[])rset.get(0))[0]));
 			assertNotNull("Todays Date should have been returned",(((Object[])rset.get(0))[1]));
 			assertEquals("trunc(45.8) result was incorrect ", new Float(45), ( (Object[]) rset.get(0) )[2] );
 			assertEquals("round(45.8) result was incorrect ", new Float(46), ( (Object[]) rset.get(0) )[3] );
 
 			simple.setPay(new Float(-45.8));
 			s.update(simple);
 
 			// Test type conversions while using nested functions (Float to Int).
 			rset = s.createQuery( "select abs(round(s.pay)) from Simple s" ).list();
 			assertEquals("abs(round(-45.8)) result was incorrect ", new Float(46), rset.get(0));
 
 			// Test a larger depth 3 function example - Not a useful combo other than for testing
 			assertTrue(
 					s.createQuery( "select trunc(round(sysdate())) from Simple s" ).list().size() == 1
 			);
 
 			// Test the oracle standard NVL funtion as a test of multi-param functions...
 			simple.setPay(null);
 			s.update(simple);
 			Integer value = (Integer) s.createQuery(
 					"select MOD( NVL(s.pay, 5000), 2 ) from Simple as s where s.id = 10"
 			).list()
 					.get(0);
 			assertTrue( 0 == value.intValue() );
 		}
 
 		if ( (getDialect() instanceof HSQLDialect) ) {
 			// Test the hsql standard MOD funtion as a test of multi-param functions...
 			Integer value = (Integer) s.createQuery( "select MOD(s.count, 2) from Simple as s where s.id = 10" )
 					.list()
 					.get(0);
 			assertTrue( 0 == value.intValue() );
 		}
 
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	public void testSetProperties() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save(simple, new Long(10) );
 		Query q = s.createQuery("from Simple s where s.name=:name and s.count=:count");
 		q.setProperties(simple);
 		assertTrue( q.list().get(0)==simple );
 		//misuse of "Single" as a propertyobject, but it was the first testclass i found with a collection ;)
 		Single single = new Single() { // trivial hack to test properties with arrays.
 			String[] getStuff() { return (String[]) getSeveral().toArray(new String[getSeveral().size()]); }
 		};
 
 		List l = new ArrayList();
 		l.add("Simple 1");
 		l.add("Slimeball");
 		single.setSeveral(l);
 		q = s.createQuery("from Simple s where s.name in (:several)");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
 
+		q = s.createQuery("from Simple s where s.name in :several");
+		q.setProperties(single);
+		assertTrue( q.list().get(0)==simple );
 
 		q = s.createQuery("from Simple s where s.name in (:stuff)");
 		q.setProperties(single);
 		assertTrue( q.list().get(0)==simple );
+
+		q = s.createQuery("from Simple s where s.name in :stuff");
+		q.setProperties(single);
+		assertTrue( q.list().get(0)==simple );
+
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	public void testSetPropertiesMap() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save(simple, new Long(10) );
 		Map parameters = new HashMap();
 		parameters.put("name", simple.getName());
 		parameters.put("count", new Integer(simple.getCount()));
 
 		Query q = s.createQuery("from Simple s where s.name=:name and s.count=:count");
 		q.setProperties((parameters));
 		assertTrue( q.list().get(0)==simple );
 
 		List l = new ArrayList();
 		l.add("Simple 1");
 		l.add("Slimeball");
 		parameters.put("several", l);
 		q = s.createQuery("from Simple s where s.name in (:several)");
 		q.setProperties(parameters);
 		assertTrue( q.list().get(0)==simple );
 
 
 		parameters.put("stuff", l.toArray(new String[0]));
 		q = s.createQuery("from Simple s where s.name in (:stuff)");
 		q.setProperties(parameters);
 		assertTrue( q.list().get(0)==simple );
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 	public void testBroken() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Broken b = new Fixed();
 		b.setId( new Long(123));
 		b.setOtherId("foobar");
 		s.save(b);
 		s.flush();
 		b.setTimestamp( new Date() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update(b);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		b = (Broken) s.load( Broken.class, b );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete(b);
 		t.commit();
 		s.close();
 	}
 
 	public void testNothinToUpdate() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save( simple, new Long(10) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple, new Long(10) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple, new Long(10) );
 		s.delete(simple);
 		t.commit();
 		s.close();
 	}
 
 	public void testCachedQuery() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save( simple, new Long(10) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setCacheable(true);
 		q.setString("name", "Simple 1");
 		assertTrue( q.list().size()==1 );
 		simple = (Simple) q.list().get(0);
 
 		q.setString("name", "Simple 2");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		simple.setName("Simple 2");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setString("name", "Simple 2");
 		q.setCacheable(true);
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple, new Long(10) );
 		s.delete(simple);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		t.commit();
 		s.close();
 	}
 
 	public void testCachedQueryRegion() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save( simple, new Long(10) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name=:name");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString("name", "Simple 1");
 		assertTrue( q.list().size()==1 );
 		simple = (Simple) q.list().get(0);
 
 		q.setString("name", "Simple 2");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		simple.setName("Simple 2");
 		assertTrue( q.list().size()==1 );
 		assertTrue( q.list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( simple, new Long(10) );
 		s.delete(simple);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s where s.name=?");
 		q.setCacheRegion("foo");
 		q.setCacheable(true);
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==0 );
 		assertTrue( q.list().size()==0 );
 		t.commit();
 		s.close();
 	}
 
 	public void testSQLFunctions() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save(simple, new Long(10) );
 
 		if ( getDialect() instanceof DB2Dialect) {
 			s.createQuery( "from Simple s where repeat('foo', 3) = 'foofoofoo'" ).list();
 			s.createQuery( "from Simple s where repeat(s.name, 3) = 'foofoofoo'" ).list();
 			s.createQuery( "from Simple s where repeat( lower(s.name), 3 + (1-1) / 2) = 'foofoofoo'" ).list();
 		}
 
 		assertTrue(
 				s.createQuery( "from Simple s where upper( s.name ) ='SIMPLE 1'" ).list().size()==1
 		);
 		if ( !(getDialect() instanceof HSQLDialect) ) {
 			assertTrue(
 					s.createQuery(
 							"from Simple s where not( upper( s.name ) ='yada' or 1=2 or 'foo'='bar' or not('foo'='foo') or 'foo' like 'bar' )"
 					).list()
 							.size()==1
 			);
 		}
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof SybaseDialect) && !(getDialect() instanceof SQLServerDialect) && !(getDialect() instanceof MckoiDialect) && !(getDialect() instanceof InterbaseDialect) && !(getDialect() instanceof TimesTenDialect) ) { //My SQL has a funny concatenation operator
 			assertTrue(
 					s.createQuery( "from Simple s where lower( s.name || ' foo' ) ='simple 1 foo'" ).list().size()==1
 			);
 		}
 		if ( (getDialect() instanceof SybaseDialect) ) {
 			assertTrue(
 					s.createQuery( "from Simple s where lower( s.name + ' foo' ) ='simple 1 foo'" ).list().size()==1
 			);
 		}
 		if ( (getDialect() instanceof MckoiDialect) || (getDialect() instanceof TimesTenDialect)) {
 			assertTrue(
 					s.createQuery( "from Simple s where lower( concat(s.name, ' foo') ) ='simple 1 foo'" ).list().size()==1
 			);
 		}
 
 		Simple other = new Simple();
 		other.setName("Simple 2");
 		other.setCount(12);
 		simple.setOther(other);
 		s.save( other, new Long(20) );
 		//s.find("from Simple s where s.name ## 'cat|rat|bag'");
 		assertTrue(
 				s.createQuery( "from Simple s where upper( s.other.name ) ='SIMPLE 2'" ).list().size()==1
 		);
 		assertTrue(
 				s.createQuery( "from Simple s where not ( upper( s.other.name ) ='SIMPLE 2' )" ).list().size()==0
 		);
 		assertTrue(
 				s.createQuery(
 						"select distinct s from Simple s where ( ( s.other.count + 3 ) = (15*2)/2 and s.count = 69) or ( ( s.other.count + 2 ) / 7 ) = 2"
 				).list()
 						.size()==1
 		);
 		assertTrue(
 				s.createQuery(
 						"select s from Simple s where ( ( s.other.count + 3 ) = (15*2)/2 and s.count = 69) or ( ( s.other.count + 2 ) / 7 ) = 2 order by s.other.count"
 				).list()
 						.size()==1
 		);
 		Simple min = new Simple();
 		min.setCount(-1);
 		s.save(min, new Long(30) );
 		if ( ! (getDialect() instanceof MySQLDialect) && ! (getDialect() instanceof HSQLDialect) ) { //My SQL has no subqueries
 			assertTrue(
 					s.createQuery( "from Simple s where s.count > ( select min(sim.count) from Simple sim )" )
 							.list()
 							.size()==2
 			);
 			t.commit();
 			t = s.beginTransaction();
 			assertTrue(
 					s.createQuery(
 							"from Simple s where s = some( select sim from Simple sim where sim.count>=0 ) and s.count >= 0"
 					).list()
 							.size()==2
 			);
 			assertTrue(
 					s.createQuery(
 							"from Simple s where s = some( select sim from Simple sim where sim.other.count=s.other.count ) and s.other.count > 0"
 					).list()
 							.size()==1
 			);
 		}
 
 		Iterator iter = s.createQuery( "select sum(s.count) from Simple s group by s.count having sum(s.count) > 10" )
 				.iterate();
 		assertTrue( iter.hasNext() );
 		assertEquals( new Long(12), iter.next() );
 		assertTrue( !iter.hasNext() );
 		if ( ! (getDialect() instanceof MySQLDialect) ) {
 			iter = s.createQuery( "select s.count from Simple s group by s.count having s.count = 12" ).iterate();
 			assertTrue( iter.hasNext() );
 		}
 
 		s.createQuery(
 				"select s.id, s.count, count(t), max(t.date) from Simple s, Simple t where s.count = t.count group by s.id, s.count order by s.count"
 		).iterate();
 
 		Query q = s.createQuery("from Simple s");
 		q.setMaxResults(10);
 		assertTrue( q.list().size()==3 );
 		q = s.createQuery("from Simple s");
 		q.setMaxResults(1);
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s");
 		assertTrue( q.list().size()==3 );
 		q = s.createQuery("from Simple s where s.name = ?");
 		q.setString(0, "Simple 1");
 		assertTrue( q.list().size()==1 );
 		q = s.createQuery("from Simple s where s.name = ? and upper(s.name) = ?");
 		q.setString(1, "SIMPLE 1");
 		q.setString(0, "Simple 1");
 		q.setFirstResult(0);
 		assertTrue( q.iterate().hasNext() );
 		q = s.createQuery("from Simple s where s.name = :foo and upper(s.name) = :bar or s.count=:count or s.count=:count + 1");
 		q.setParameter("bar", "SIMPLE 1");
 		q.setString("foo", "Simple 1");
 		q.setInteger("count", 69);
 		q.setFirstResult(0);
 		assertTrue( q.iterate().hasNext() );
 		q = s.createQuery("select s.id from Simple s");
 		q.setFirstResult(1);
 		q.setMaxResults(2);
 		iter = q.iterate();
 		int i=0;
 		while ( iter.hasNext() ) {
 			assertTrue( iter.next() instanceof Long );
 			i++;
 		}
 		assertTrue(i==2);
 		q = s.createQuery("select all s, s.other from Simple s where s = :s");
 		q.setParameter("s", simple);
 		assertTrue( q.list().size()==1 );
 
 
 		q = s.createQuery("from Simple s where s.name in (:name_list) and s.count > :count");
 		HashSet set = new HashSet();
 		set.add("Simple 1"); set.add("foo");
 		q.setParameterList( "name_list", set );
 		q.setParameter("count", new Integer(-1) );
 		assertTrue( q.list().size()==1 );
 
 		ScrollableResults sr = s.createQuery("from Simple s").scroll();
 		sr.next();
 		sr.get(0);
 		sr.close();
 
 		s.delete(other);
 		s.delete(simple);
 		s.delete(min);
 		t.commit();
 		s.close();
 
 	}
 
 	public void testBlobClob() throws Exception {
 		// Sybase does not support ResultSet.getBlob(String)
 		if ( getDialect() instanceof SybaseDialect || getDialect() instanceof Sybase11Dialect || getDialect() instanceof SybaseASE15Dialect || getDialect() instanceof SybaseAnywhereDialect ) {
 			return;
 		}
 		Session s = openSession();
 		Blobber b = new Blobber();
 		b.setBlob( s.getLobHelper().createBlob( "foo/bar/baz".getBytes() ) );
 		b.setClob( s.getLobHelper().createClob("foo/bar/baz") );
 		s.save(b);
 		//s.refresh(b);
 		//assertTrue( b.getClob() instanceof ClobImpl );
 		s.flush();
 
 		s.refresh(b);
 		//b.getBlob().setBytes( 2, "abc".getBytes() );
 		b.getClob().getSubString(2, 3);
 		//b.getClob().setString(2, "abc");
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 		s = openSession();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		Blobber b2 = new Blobber();
 		s.save(b2);
 		b2.setBlob( b.getBlob() );
 		b.setBlob(null);
 		//assertTrue( b.getClob().getSubString(1, 3).equals("fab") );
 		b.getClob().getSubString(1, 6);
 		//b.getClob().setString(1, "qwerty");
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 		s = openSession();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		b.setClob( s.getLobHelper().createClob("xcvfxvc xcvbx cvbx cvbx cvbxcvbxcvbxcvb") );
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 		s = openSession();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		assertTrue( b.getClob().getSubString(1, 7).equals("xcvfxvc") );
 		//b.getClob().setString(5, "1234567890");
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 
 		/*InputStream is = getClass().getClassLoader().getResourceAsStream("jdbc20.pdf");
 		s = sessionsopenSession();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		System.out.println( is.available() );
 		int size = is.available();
 		b.setBlob( Hibernate.createBlob( is, is.available() ) );
 		s.flush();
 		s.connection().commit();
 		ResultSet rs = s.connection().createStatement().executeQuery("select datalength(blob_) from blobber where id=" + b.getId() );
 		rs.next();
 		assertTrue( size==rs.getInt(1) );
 		rs.close();
 		s.close();
 
 		s = sessionsopenSession();
 		b = (Blobber) s.load( Blobber.class, new Integer( b.getId() ) );
 		File f = new File("C:/foo.pdf");
 		f.createNewFile();
 		FileOutputStream fos = new FileOutputStream(f);
 		Blob blob = b.getBlob();
 		byte[] bytes = blob.getBytes( 1, (int) blob.length() );
 		System.out.println( bytes.length );
 		fos.write(bytes);
 		fos.flush();
 		fos.close();
 		s.close();*/
 
 	}
 
 	public void testSqlFunctionAsAlias() throws Exception {
 		String functionName = locateAppropriateDialectFunctionNameForAliasTest();
 		if (functionName == null) {
             LOG.info("Dialect does not list any no-arg functions");
 			return;
 		}
 
         LOG.info("Using function named [" + functionName + "] for 'function as alias' test");
 		String query = "select " + functionName + " from Simple as " + functionName + " where " + functionName + ".id = 10";
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save( simple, new Long(10) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List result = s.createQuery( query ).list();
 		assertTrue( result.size() == 1 );
 		assertTrue(result.get(0) instanceof Simple);
 		s.delete( result.get(0) );
 		t.commit();
 		s.close();
 	}
 
 	private String locateAppropriateDialectFunctionNameForAliasTest() {
 		for (Iterator itr = getDialect().getFunctions().entrySet().iterator(); itr.hasNext(); ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			final SQLFunction function = (SQLFunction) entry.getValue();
 			if ( !function.hasArguments() && !function.hasParenthesesIfNoArguments() ) {
 				return (String) entry.getKey();
 			}
 		}
 		return null;
 	}
 
 	public void testCachedQueryOnInsert() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Simple simple = new Simple();
 		simple.setName("Simple 1");
 		s.save( simple, new Long(10) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Query q = s.createQuery("from Simple s");
 		List list = q.setCacheable(true).list();
 		assertTrue( list.size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Simple simple2 = new Simple();
 		simple2.setCount(133);
 		s.save( simple2, new Long(12) );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		q = s.createQuery("from Simple s");
 		list = q.setCacheable(true).list();
 		assertTrue( list.size()==2 );
 		Iterator i = list.iterator();
 		while ( i.hasNext() ) s.delete( i.next() );
 		t.commit();
 		s.close();
 
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLLoaderTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLLoaderTest.java
index 6269b0f074..bdb0ade782 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLLoaderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/SQLLoaderTest.java
@@ -1,658 +1,670 @@
 //$Id: SQLLoaderTest.java 11383 2007-04-02 15:34:02Z steve.ebersole@jboss.com $
 package org.hibernate.test.legacy;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import junit.framework.Test;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.Transaction;
 import org.hibernate.classic.Session;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 
 
 public class SQLLoaderTest extends LegacyTestCase {
 
 	static int nextInt = 1;
 	static long nextLong = 1;
 
 	public SQLLoaderTest(String arg) {
 		super(arg);
 	}
 
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/ABC.hbm.xml",
 			"legacy/Category.hbm.xml",
 			"legacy/Simple.hbm.xml",
 			"legacy/Fo.hbm.xml",
 			"legacy/SingleSeveral.hbm.xml",
 			"legacy/Componentizable.hbm.xml",
             "legacy/CompositeIdId.hbm.xml"
 		};
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( SQLLoaderTest.class );
 	}
 
 	public void testTS() throws Exception {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		Simple sim = new Simple();
 		sim.setDate( new Date() );
 		session.save( sim, new Long(1) );
 		Query q = session.createSQLQuery("select {sim.*} from Simple {sim} where {sim}.date_ = ?", "sim", Simple.class);
 		q.setTimestamp( 0, sim.getDate() );
 		assertTrue ( q.list().size()==1 );
 		session.delete(sim);
 		txn.commit();
 		session.close();
 	}
 
 
 	public void testFindBySQLStar() throws HibernateException, SQLException {
 		Session session = openSession();
 		session.delete("from Assignable");
 		session.delete("from Category");
 		session.delete("from Simple");
 		session.delete("from A");
 
 		Category s = new Category();
 		s.setName(String.valueOf(nextLong++));
 		session.save(s);
 
 		Simple simple = new Simple();
 		simple.init();
 		session.save(simple, new Long(nextLong++));
 
 		A a = new A();
 		session.save(a);
 
 		B b = new B();
 		session.save(b);
 		session.flush();
 
 		session.createSQLQuery("select {category.*} from category {category}", "category", Category.class).list();
 		session.createSQLQuery("select {simple.*} from Simple {simple}", "simple", Simple.class).list();
 		session.createSQLQuery("select {a.*} from TA {a}", "a", A.class).list();
 
 		session.connection().commit();
 		session.close();
 
 	}
 
 	public void testFindBySQLProperties() throws HibernateException, SQLException {
 			Session session = openSession();
 			session.delete("from Category");
 
 			Category s = new Category();
 			s.setName(String.valueOf(nextLong++));
 			session.save(s);
 
 			s = new Category();
 			s.setName("WannaBeFound");
 			session.flush();
 
 			Query query = session.createSQLQuery("select {category.*} from category {category} where {category}.name = :name", "category", Category.class);
 
 			query.setProperties(s);
 			//query.setParameter("name", s.getName());
 
 			query.list();
 
 			query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in (:names)", "category", Category.class);
 			String[] str = new String[] { "WannaBeFound", "NotThere" };
 			query.setParameterList("names", str);
-			
 			query.uniqueResult();
-			
+
+			query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in :names", "category", Category.class);
+			query.setParameterList("names", str);
+			query.uniqueResult();
+
+			query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in (:names)", "category", Category.class);
+			str = new String[] { "WannaBeFound" };
+			query.setParameterList("names", str);
+			query.uniqueResult();
+
+			query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in :names", "category", Category.class);
+			query.setParameterList("names", str);			
+			query.uniqueResult();
+
 			session.connection().commit();
 			session.close();
 			
 			
 
 		}
 
 	public void testFindBySQLAssociatedObjects() throws HibernateException, SQLException {
 		Session s = openSession();
 		s.delete("from Assignable");
 		s.delete("from Category");
 
 		Category c = new Category();
 		c.setName("NAME");
 		Assignable assn = new Assignable();
 		assn.setId("i.d.");
 		List l = new ArrayList();
 		l.add(c);
 		assn.setCategories(l);
 		c.setAssignable(assn);
 		s.save(assn);
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 		s = openSession();
 		List list = s.createSQLQuery("select {category.*} from category {category}", "category", Category.class).list();
 		list.get(0);
 		s.connection().commit();
 		s.close();
 		
 		if ( getDialect() instanceof MySQLDialect ) return;
 
 		s = openSession();
 
 		Query query = s.getNamedQuery("namedsql");
 		assertNotNull(query);
 		list = query.list();
         assertNotNull(list);
 		
 		Object[] values = (Object[]) list.get(0);
 		assertNotNull(values[0]);
 		assertNotNull(values[1]);
 		assertTrue("wrong type: " + values[0].getClass(), values[0] instanceof Category);
 		assertTrue("wrong type: " + values[1].getClass(), values[1] instanceof Assignable);
 		
 		s.connection().commit();
 		s.close();
 
 	}
 
 	public void testPropertyResultSQL() throws HibernateException, SQLException {
 		
 		if ( getDialect() instanceof MySQLDialect ) return;
 			
 		Session s = openSession();
 		s.delete("from Assignable");
 		s.delete("from Category");
 
 		Category c = new Category();
 		c.setName("NAME");
 		Assignable assn = new Assignable();
 		assn.setId("i.d.");
 		List l = new ArrayList();
 		l.add(c);
 		assn.setCategories(l);
 		c.setAssignable(assn);
 		s.save(assn);
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 		s = openSession();
 
 		Query query = s.getNamedQuery("nonaliasedsql");
 		assertNotNull(query);
 		List list = query.list();
         assertNotNull(list);
 		
 		assertTrue(list.get(0) instanceof Category);
 		
 		s.connection().commit();
 		s.close();
 
 	}
 	
 	public void testFindBySQLMultipleObject() throws HibernateException, SQLException {
 		Session s = openSession();
 		s.delete("from Assignable");
 		s.delete("from Category");
 		s.flush();
 		s.connection().commit();
 		s.close();
 		s = openSession();
 		Category c = new Category();
 		c.setName("NAME");
 		Assignable assn = new Assignable();
 		assn.setId("i.d.");
 		List l = new ArrayList();
 		l.add(c);
 		assn.setCategories(l);
 		c.setAssignable(assn);
 		s.save(assn);
 		s.flush();
 		c = new Category();
 		c.setName("NAME2");
 		assn = new Assignable();
 		assn.setId("i.d.2");
 		l = new ArrayList();
 		l.add(c);
 		assn.setCategories(l);
 		c.setAssignable(assn);
 		s.save(assn);
 		s.flush();
 
 		assn = new Assignable();
 		assn.setId("i.d.3");
 		s.save(assn);
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 		if ( getDialect() instanceof MySQLDialect ) return;
 
 		s = openSession();
 		List list = s.createSQLQuery("select {category.*}, {assignable.*} from category {category}, \"assign-able\" {assignable}", new String[] { "category", "assignable" }, new Class[] { Category.class, Assignable.class }).list();
 
 		assertTrue(list.size() == 6); // crossproduct of 2 categories x 3 assignables
 		assertTrue(list.get(0) instanceof Object[]);
 		s.connection().commit();
 		s.close();
 	}
 
 	public void testFindBySQLParameters() throws HibernateException, SQLException {
 		Session s = openSession();
 		s.delete("from Assignable");
 		s.delete("from Category");
 		s.flush();
 		s.connection().commit();
 		s.close();
 		s = openSession();
 		Category c = new Category();
 		c.setName("Good");
 		Assignable assn = new Assignable();
 		assn.setId("i.d.");
 		List l = new ArrayList();
 		l.add(c);
 		assn.setCategories(l);
 		c.setAssignable(assn);
 		s.save(assn);
 		s.flush();
 		c = new Category();
 		c.setName("Best");
 		assn = new Assignable();
 		assn.setId("i.d.2");
 		l = new ArrayList();
 		l.add(c);
 		assn.setCategories(l);
 		c.setAssignable(assn);
 		s.save(assn);
 		s.flush();
 		c = new Category();
 		c.setName("Better");
 		assn = new Assignable();
 		assn.setId("i.d.7");
 		l = new ArrayList();
 		l.add(c);
 		assn.setCategories(l);
 		c.setAssignable(assn);
 		s.save(assn);
 		s.flush();
 
 		assn = new Assignable();
 		assn.setId("i.d.3");
 		s.save(assn);
 		s.flush();
 		s.connection().commit();
 		s.close();
 
 		s = openSession();
 		Query basicParam = s.createSQLQuery("select {category.*} from category {category} where {category}.name = 'Best'", "category", Category.class);
 		List list = basicParam.list();
 		assertEquals(1, list.size());
 
 		Query unnamedParam = s.createSQLQuery("select {category.*} from category {category} where {category}.name = ? or {category}.name = ?", "category", Category.class);
 		unnamedParam.setString(0, "Good");
 		unnamedParam.setString(1, "Best");
 		list = unnamedParam.list();
 		assertEquals(2, list.size());
 
 		Query namedParam = s.createSQLQuery("select {category.*} from category {category} where ({category}.name=:firstCat or {category}.name=:secondCat)", "category", Category.class);
 		namedParam.setString("firstCat", "Better");
 		namedParam.setString("secondCat", "Best");
 		list = namedParam.list();
 		assertEquals(2, list.size());
 
 		s.connection().commit();
 		s.close();
 	}
 
 	public void testEscapedJDBC() throws HibernateException, SQLException {
 		if ( 
 				getDialect() instanceof HSQLDialect || 
 				getDialect() instanceof PostgreSQLDialect
 		) return;
 
 		Session session = openSession();
 		session.delete("from A");
 		A savedA = new A();
 		savedA.setName("Max");
 		session.save(savedA);
 
 		B savedB = new B();
 		session.save(savedB);
 		session.flush();
 
 		int count = session.createQuery("from A").list().size();
 		session.close();
 
 		session = openSession();
 
 		Query query;
 		if( getDialect() instanceof TimesTenDialect) {
             // TimesTen does not permit general expressions (like UPPER) in the second part of a LIKE expression,
             // so we execute a similar test 
             query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, count_ as {a.count}, name as {a.name} from TA where {fn ucase(name)} like 'MAX'", "a", A.class);
         } else {
             query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, count_ as {a.count}, name as {a.name} from TA where {fn ucase(name)} like {fn ucase('max')}", "a", A.class);
         }
 		List list = query.list();
 
 		assertNotNull(list);
 		assertEquals(1, list.size());
 		session.connection().commit();
 		session.close();
 	}
 
 	public void testDoubleAliasing() throws HibernateException, SQLException {	
 
 		Session session = openSession();
 		session.delete("from A");
 		A savedA = new A();
 		savedA.setName("Max");
 		session.save(savedA);
 
 		B savedB = new B();
 		session.save(savedB);
 		session.flush();
 
 		int count = session.createQuery("from A").list().size();
 		session.close();
 
 		session = openSession();
 
 		Query query = session.createSQLQuery("select a.identifier_column as {a1.id}, a.clazz_discriminata as {a1.class}, a.count_ as {a1.count}, a.name as {a1.name} " +
 											", b.identifier_column as {a2.id}, b.clazz_discriminata as {a2.class}, b.count_ as {a2.count}, b.name as {a2.name} " +
 											" from TA a, TA b" +
 											" where a.identifier_column = b.identifier_column", new String[] {"a1", "a2" }, new Class[] {A.class, A.class});
 		List list = query.list();
 
 		assertNotNull(list);
 		assertEquals(2, list.size());
 		session.connection().commit();
 		session.close();
 	}
 
 	// TODO: compositeid's - how ? (SingleSeveral.hbm.xml test)
 	public void testEmbeddedCompositeProperties() throws HibernateException, SQLException {
 	   Session session = openSession();
 
 	   Single s = new Single();
 	   s.setId("my id");
 	   s.setString("string 1");
 	   session.save(s);
 	   session.flush();
 	   session.connection().commit();
 
 	   session.clear();
 
 	   Query query = session.createSQLQuery("select {sing.*} from Single {sing}", "sing", Single.class);
 
 	   List list = query.list();
 
 	   assertTrue(list.size()==1);
 
 	   session.clear();
 
 	   query = session.createSQLQuery("select {sing.*} from Single {sing} where sing.id = ?", "sing", Single.class);
 	   query.setString(0, "my id");
 	   list = query.list();
 
 	   assertTrue(list.size()==1);
 
 	   session.clear();
 
 	   query = session.createSQLQuery("select s.id as {sing.id}, s.string_ as {sing.string}, s.prop as {sing.prop} from Single s where s.id = ?", "sing", Single.class);
 	   query.setString(0, "my id");
 	   list = query.list();
 
 	   assertTrue(list.size()==1);
 
 	   session.clear();
 
 	   query = session.createSQLQuery("select s.id as {sing.id}, s.string_ as {sing.string}, s.prop as {sing.prop} from Single s where s.id = ?", "sing", Single.class);
 	   query.setString(0, "my id");
 	   list = query.list();
 
 	   assertTrue(list.size()==1);
 
 	   session.connection().commit();
 	   session.close();
 
 	}
 
 	public void testReturnPropertyComponentRenameFailureExpected() throws HibernateException, SQLException {
 		// failure expected because this was a regression introduced previously which needs to get tracked down.
 		Session session = openSession();
 		Componentizable componentizable = setupComponentData(session);
 		
 		Query namedQuery = session.getNamedQuery("queryComponentWithOtherColumn");
 		List list = namedQuery.list();
 		
 		assertEquals(1, list.size());
 		assertEquals( "flakky comp", ( (Componentizable) list.get(0) ).getComponent().getName() );
 		
 		session.clear();
 		
 		session.delete(componentizable);
 		session.flush();
 		
 		session.connection().commit();
 		session.close();
 	}
 	
 	public void testComponentStar() throws HibernateException, SQLException {
 	    componentTest("select {comp.*} from Componentizable comp");
 	}
 	
 	public void testComponentNoStar() throws HibernateException, SQLException {
 	    componentTest("select comp.id as {comp.id}, comp.nickName as {comp.nickName}, comp.name as {comp.component.name}, comp.subName as {comp.component.subComponent.subName}, comp.subName1 as {comp.component.subComponent.subName1} from Componentizable comp");
 	}
 	
 
 	private void componentTest(String sql) throws SQLException {
         Session session = openSession();
 	    
 	    Componentizable c = setupComponentData( session );
         
 	    Query q = session.createSQLQuery(sql, "comp", Componentizable.class);
 	    List list = q.list();
 	    
 	    assertEquals(list.size(),1);
 	    
 	    Componentizable co = (Componentizable) list.get(0);
 	    
 	    assertEquals(c.getNickName(), co.getNickName());
 	    assertEquals(c.getComponent().getName(), co.getComponent().getName());
 	    assertEquals(c.getComponent().getSubComponent().getSubName(), co.getComponent().getSubComponent().getSubName());
 	    
 	    session.delete(co);
 	    session.flush();
 	    session.connection().commit();
 	    session.close();
     }
 
 	private Componentizable setupComponentData(Session session) throws SQLException {
 		Componentizable c = new Componentizable();
 	    c.setNickName("Flacky");
 	    Component component = new Component();
 	    component.setName("flakky comp");
 	    SubComponent subComponent = new SubComponent();
 	    subComponent.setSubName("subway");
         component.setSubComponent(subComponent);
 	    
         c.setComponent(component);
         
         session.save(c);
         
         session.flush();
         session.connection().commit();
         
         session.clear();
 		return c;
 	}
 
     public void testFindSimpleBySQL() throws Exception {
 		if ( getDialect() instanceof MySQLDialect ) return;
 		Session session = openSession();
 		Category s = new Category();
 		s.setName(String.valueOf(nextLong++));
 		session.save(s);
 		session.flush();
 
 		Query query = session.createSQLQuery("select s.category_key_col as {category.id}, s.name as {category.name}, s.\"assign-able-id\" as {category.assignable} from {category} s", "category", Category.class);
 		List list = query.list();
 
 		assertNotNull(list);
 		assertTrue(list.size() > 0);
 		assertTrue(list.get(0) instanceof Category);
 		session.connection().commit();
 		session.close();
 		// How do we handle objects with composite id's ? (such as Single)
 	}
 
 	public void testFindBySQLSimpleByDiffSessions() throws Exception {
 		Session session = openSession();
 		Category s = new Category();
 		s.setName(String.valueOf(nextLong++));
 		session.save(s);
 		session.flush();
 		session.connection().commit();
 		session.close();
 
 		if ( getDialect() instanceof MySQLDialect ) return;
 
 		session = openSession();
 
 		Query query = session.createSQLQuery("select s.category_key_col as {category.id}, s.name as {category.name}, s.\"assign-able-id\" as {category.assignable} from {category} s", "category", Category.class);
 		List list = query.list();
 
 		assertNotNull(list);
 		assertTrue(list.size() > 0);
 		assertTrue(list.get(0) instanceof Category);
 
 		// How do we handle objects that does not have id property (such as Simple ?)
 		// How do we handle objects with composite id's ? (such as Single)
 		session.connection().commit();
 		session.close();
 	}
 
 	public void testFindBySQLDiscriminatedSameSession() throws Exception {
 		Session session = openSession();
 		session.delete("from A");
 		A savedA = new A();
 		session.save(savedA);
 
 		B savedB = new B();
 		session.save(savedB);
 		session.flush();
 
 		Query query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, name as {a.name}, count_ as {a.count} from TA {a}", "a", A.class);
 		List list = query.list();
 
 		assertNotNull(list);
 		assertEquals(2, list.size());
 
 		A a1 = (A) list.get(0);
 		A a2 = (A) list.get(1);
 
 		assertTrue((a2 instanceof B) || (a1 instanceof B));
 		assertFalse(a1 instanceof B && a2 instanceof B);
 
 		if (a1 instanceof B) {
 			assertSame(a1, savedB);
 			assertSame(a2, savedA);
 		}
 		else {
 			assertSame(a2, savedB);
 			assertSame(a1, savedA);
 		}
 
 		session.clear();
 		List list2 = session.getNamedQuery("propertyResultDiscriminator").list();
 		assertEquals(2, list2.size());
 		
 		session.connection().commit();
 		session.close();
 	}
 
 	public void testFindBySQLDiscriminatedDiffSession() throws Exception {
 		Session session = openSession();
 		session.delete("from A");
 		A savedA = new A();
 		session.save(savedA);
 
 		B savedB = new B();
 		session.save(savedB);
 		session.flush();
 
 		int count = session.createQuery("from A").list().size();
 		session.close();
 
 		session = openSession();
 
 		Query query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, count_ as {a.count}, name as {a.name} from TA", "a", A.class);
 		List list = query.list();
 
 		assertNotNull(list);
 		assertEquals(count, list.size());
 		session.connection().commit();
 		session.close();
 	}
 
 
     public void testCompositeIdId() throws HibernateException, SQLException {
 	    // issue HHH-21
         Session s = openSession();
 
         CompositeIdId id = new CompositeIdId();
         id.setName("Max");
         id.setSystem("c64");
         id.setId("games");
 
         s.save(id);
         s.flush();
         s.connection().commit();
         s.close();
 
         s = openSession();
         // having a composite id with one property named id works since the map used by sqlloader to map names to properties handles it.
         Query query = s.createSQLQuery("select system as {c.system}, id as {c.id}, name as {c.name}, foo as {c.composite.foo}, bar as {c.composite.bar} from CompositeIdId where system=? and id=?", "c", CompositeIdId.class);
         query.setString(0, "c64");
         query.setString(1, "games");
 
         CompositeIdId id2 = (CompositeIdId) query.uniqueResult();
         check(id, id2);
 
         s.flush();
         s.connection().commit();
         s.close();
 
         s = openSession();
 
         CompositeIdId useForGet = new CompositeIdId();
         useForGet.setSystem("c64");
         useForGet.setId("games");
         // this doesn't work since the verification does not take column span into respect!
         CompositeIdId getted = (CompositeIdId) s.get(CompositeIdId.class, useForGet);
         check(id,getted);
 
 
         s.connection().commit();
         s.close();
 
     }
 
     private void check(CompositeIdId id, CompositeIdId id2) {
         assertEquals(id,id2);
         assertEquals(id.getName(), id2.getName());
         assertEquals(id.getId(), id2.getId());
         assertEquals(id.getSystem(), id2.getSystem());
     }
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
index 0f79f970ec..f73c086312 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
@@ -1,463 +1,525 @@
 //$Id$
 package org.hibernate.ejb.test.query;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
+import java.util.HashSet;
+import java.util.Set;
+
 import javax.persistence.EntityManager;
 import javax.persistence.Query;
 import javax.persistence.TemporalType;
 import org.hibernate.Hibernate;
 import org.hibernate.ejb.test.Distributor;
 import org.hibernate.ejb.test.Item;
 import org.hibernate.ejb.test.TestCase;
 import org.hibernate.ejb.test.Wallet;
 
-
 /**
  * @author Emmanuel Bernard
  */
 public class QueryTest extends TestCase {
 
 	public void testPagedQuery() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		item = new Item( "Computer", "Apple II" );
 		em.persist( item );
 		Query q = em.createQuery( "select i from " + Item.class.getName() + " i where i.name like :itemName" );
 		q.setParameter( "itemName", "%" );
 		q.setMaxResults( 1 );
 		q.getSingleResult();
 		q = em.createQuery( "select i from Item i where i.name like :itemName" );
 		q.setParameter( "itemName", "%" );
 		q.setFirstResult( 1 );
 		q.setMaxResults( 1 );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	public void testAggregationReturnType() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		item = new Item( "Computer", "Apple II" );
 		em.persist( item );
 		Query q = em.createQuery( "select count(i) from Item i where i.name like :itemName" );
 		q.setParameter( "itemName", "%" );
 		assertTrue( q.getSingleResult() instanceof Long );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	public void testParameterList() throws Exception {
 		final Item item = new Item( "Mouse", "Micro$oft mouse" );
 		final Item item2 = new Item( "Computer", "Dell computer" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		em.persist( item2 );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
+		Query q = em.createQuery( "select item from Item item where item.name in :names" );
+		//test hint in value and string
+		q.setHint( "org.hibernate.fetchSize", 10 );
+		q.setHint( "org.hibernate.fetchSize", "10" );
+		List params = new ArrayList();
+		params.add( item.getName() );
+		q.setParameter( "names", params );
+		List result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 1, result.size() );
+
+		q = em.createQuery( "select item from Item item where item.name in :names" );
+		//test hint in value and string
+		q.setHint( "org.hibernate.fetchSize", 10 );
+		q.setHint( "org.hibernate.fetchSize", "10" );
+		params.add( item2.getName() );
+		q.setParameter( "names", params );
+		result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 2, result.size() );
+
+		q = em.createQuery( "select item from Item item where item.name in ?1" );
+		params = new ArrayList();
+		params.add( item.getName() );
+		params.add( item2.getName() );
+		q.setParameter( "1", params );
+		result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 2, result.size() );
+		em.remove( result.get( 0 ) );
+		em.remove( result.get( 1 ) );
+		em.getTransaction().commit();
+
+		em.close();
+	}
+
+	public void testParameterListInExistingParens() throws Exception {
+		final Item item = new Item( "Mouse", "Micro$oft mouse" );
+		final Item item2 = new Item( "Computer", "Dell computer" );
+
+		EntityManager em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		em.persist( item );
+		em.persist( item2 );
+		assertTrue( em.contains( item ) );
+		em.getTransaction().commit();
+
+		em.getTransaction().begin();
 		Query q = em.createQuery( "select item from Item item where item.name in (:names)" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		List params = new ArrayList();
 		params.add( item.getName() );
 		params.add( item2.getName() );
 		q.setParameter( "names", params );
 		List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 
+		q = em.createQuery( "select item from Item item where item.name in ( \n :names \n)\n" );
+		//test hint in value and string
+		q.setHint( "org.hibernate.fetchSize", 10 );
+		q.setHint( "org.hibernate.fetchSize", "10" );
+		params = new ArrayList();
+		params.add( item.getName() );
+		params.add( item2.getName() );
+		q.setParameter( "names", params );
+		result = q.getResultList();
+		assertNotNull( result );
+		assertEquals( 2, result.size() );
+
 		q = em.createQuery( "select item from Item item where item.name in ( ?1 )" );
 		params = new ArrayList();
 		params.add( item.getName() );
 		params.add( item2.getName() );
 		q.setParameter( "1", params );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 		em.remove( result.get( 0 ) );
 		em.remove( result.get( 1 ) );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 //	public void testDistinct() throws Exception {
 //		Item item = new Item("Mouse", "Micro_oft mouse");
 //		Distributor fnac = new Distributor();
 //		fnac.setName("Fnac");
 //		item.addDistributor(fnac);
 //		Distributor auchan = new Distributor();
 //		auchan.setName("Auchan");
 //		item.addDistributor(auchan);
 //
 //		EntityManager em = getOrCreateEntityManager();
 //		em.getTransaction().begin();
 //		em.persist(fnac);
 //		em.persist(auchan);
 //		em.persist(item);
 //		em.getTransaction().commit();
 //
 //		em.getTransaction().begin();
 //		Query q = em.createQuery("select distinct item from Item item join fetch item.distributors");
 //		List result = q.getResultList();
 //		assertNotNull(result);
 //		assertEquals( 1, result.size() );
 //		item = (Item) result.get(0);
 //		item.getDistributors().clear();
 //		em.flush();
 //		int deleted = em.createQuery("delete from Item").executeUpdate();
 //		assertEquals( 1, deleted );
 //		deleted = em.createQuery("delete from Distributor").executeUpdate();
 //		assertEquals( 2, deleted );
 //		em.getTransaction().commit();
 //
 //		em.close();
 //	}
 
 	public void testEscapeCharacter() throws Exception {
 		final Item item = new Item( "Mouse", "Micro_oft mouse" );
 		final Item item2 = new Item( "Computer", "Dell computer" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		em.persist( item2 );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		Query q = em.createQuery( "select item from Item item where item.descr like 'Microk_oft mouse' escape 'k' " );
 		List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 1, result.size() );
 		int deleted = em.createQuery( "delete from Item" ).executeUpdate();
 		assertEquals( 2, deleted );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 	public void testNativeQueryByEntity() {
 
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		item = (Item) em.createNativeQuery( "select * from Item", Item.class ).getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 
 	}
 
 	public void testNativeQueryByResultSet() {
 
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		item = (Item) em.createNativeQuery( "select name as itemname, descr as itemdescription from Item", "getItem" )
 				.getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 
 	}
 
 	public void testExplicitPositionalParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.getTransaction().commit();
 		em.getTransaction().begin();
-		Query query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.brand in (?1)" );
+		Query query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.brand in ?1" );
 		List brands = new ArrayList();
 		brands.add( "Lacoste" );
 		query.setParameter( 1, brands );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 		query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.marketEntrance = ?1" );
 		query.setParameter( 1, new Date(), TemporalType.DATE );
 		//assertNull( query.getSingleResult() );
 		assertEquals( 0, query.getResultList().size() );
 		em.remove( w );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	public void testPositionalParameterForms() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		// first using jpa-style positional parameter
 		Query query = em.createQuery( "select w from Wallet w where w.brand = ?1" );
 		query.setParameter( 1, "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 
 		// next using jpa-style positional parameter, but as a name (which is how Hibernate core treats these
 		query = em.createQuery( "select w from Wallet w where w.brand = ?1" );
 		query.setParameter( "1", "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 
 		// finally using hql-style positional parameter
 		query = em.createQuery( "select w from Wallet w where w.brand = ?" );
 		query.setParameter( 1, "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 
 		em.remove( w );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	public void testPositionalParameterWithUserError() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.flush();
 
 
 		try {
 			Query query = em.createQuery( "select w from Wallet w where w.brand = ?1 and w.model = ?3" );
 			query.setParameter( 1, "Lacoste" );
 			query.setParameter( 2, "Expensive" );
 			query.getResultList();
 			fail("The query should fail due to a user error in parameters");
 		}
 		catch ( IllegalArgumentException e ) {
 			//success
 		}
 		finally {
 			em.getTransaction().rollback();
 			em.close();
 		}
 	}
 
 	public void testNativeQuestionMarkParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.getTransaction().commit();
 		em.getTransaction().begin();
 		Query query = em.createNativeQuery( "select * from Wallet w where w.brand = ?", Wallet.class );
 		query.setParameter( 1, "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 		em.remove( w );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	public void testNativeQueryWithPositionalParameter() {
 
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		Query query = em.createNativeQuery( "select * from Item where name = ?1", Item.class );
 		query.setParameter( 1, "Mouse" );
 		item = (Item) query.getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		query = em.createNativeQuery( "select * from Item where name = ?", Item.class );
 		query.setParameter( 1, "Mouse" );
 		item = (Item) query.getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 
 	}
 
 	public void testDistinct() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.createQuery( "delete Item" ).executeUpdate();
 		em.createQuery( "delete Distributor" ).executeUpdate();
 		Distributor d1 = new Distributor();
 		d1.setName( "Fnac" );
 		Distributor d2 = new Distributor();
 		d2.setName( "Darty" );
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		item.getDistributors().add( d1 );
 		item.getDistributors().add( d2 );
 		em.persist( d1 );
 		em.persist( d2 );
 		em.persist( item );
 		em.flush();
 		em.clear();
 		Query q = em.createQuery( "select distinct i from Item i left join fetch i.distributors" );
 		item = (Item) q.getSingleResult()
 				;
 		//assertEquals( 1, distinctResult.size() );
 		//item = (Item) distinctResult.get( 0 );
 		assertTrue( Hibernate.isInitialized( item.getDistributors() ) );
 		assertEquals( 2, item.getDistributors().size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	public void testIsNull() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Distributor d1 = new Distributor();
 		d1.setName( "Fnac" );
 		Distributor d2 = new Distributor();
 		d2.setName( "Darty" );
 		Item item = new Item( "Mouse", null );
 		Item item2 = new Item( "Mouse2", "dd" );
 		item.getDistributors().add( d1 );
 		item.getDistributors().add( d2 );
 		em.persist( d1 );
 		em.persist( d2 );
 		em.persist( item );
 		em.persist( item2 );
 		em.flush();
 		em.clear();
 		Query q = em.createQuery(
 				"select i from Item i where i.descr = :descr or (i.descr is null and cast(:descr as string) is null)"
 		);
 		//Query q = em.createQuery( "select i from Item i where (i.descr is null and :descr is null) or (i.descr = :descr");
 		q.setParameter( "descr", "dd" );
 		List result = q.getResultList();
 		assertEquals( 1, result.size() );
 		q.setParameter( "descr", null );
 		result = q.getResultList();
 		assertEquals( 1, result.size() );
 		//item = (Item) distinctResult.get( 0 );
 
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	public void testUpdateQuery() {
 
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 
 		em.flush();
 		em.clear();
 
 		assertEquals(
 				1, em.createNativeQuery(
 				"update Item set descr = 'Logitech Mouse' where name = 'Mouse'"
 		).executeUpdate()
 		);
 		item = em.find( Item.class, item.getName() );
 		assertEquals( "Logitech Mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().rollback();
 
 		em.close();
 
 	}
 
 	public void testUnavailableNamedQuery() throws Exception {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		try {
 			em.createNamedQuery( "wrong name" );
 			fail("Wrong named query should raise an exception");
 		}
 		catch (IllegalArgumentException e) {
 			//success
 		}
 		em.getTransaction().commit();
 
 		em.clear();
 
 		em.getTransaction().begin();
 		em.remove( em.find( Item.class, item.getName() ) );
 		em.getTransaction().commit();
 		em.close();
 
 	}
 
 	public void testTypedNamedNativeQuery() {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		item = em.createNamedQuery( "nativeItem1", Item.class ).getSingleResult();
 		item = em.createNamedQuery( "nativeItem2", Item.class ).getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 	public Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Item.class,
 				Distributor.class,
 				Wallet.class
 		};
 	}
 }
