diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/OnUpdateVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/internal/OnUpdateVisitor.java
index 48932d6bfd..cefbb167ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/OnUpdateVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/OnUpdateVisitor.java
@@ -1,91 +1,89 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.CollectionType;
 
 /**
  * When an entity is passed to update(), we must inspect all its collections and
  * 1. associate any uninitialized PersistentCollections with this session
  * 2. associate any initialized PersistentCollections with this session, using the
  *    existing snapshot
  * 3. execute a collection removal (SQL DELETE) for each null collection property
  *    or "new" collection
  *
  * @author Gavin King
  */
 public class OnUpdateVisitor extends ReattachVisitor {
 
 	OnUpdateVisitor(EventSource session, Serializable key, Object owner) {
 		super( session, key, owner );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	Object processCollection(Object collection, CollectionType type) throws HibernateException {
 
 		if ( collection == CollectionType.UNFETCHED_COLLECTION ) {
 			return null;
 		}
 
 		EventSource session = getSession();
 		CollectionPersister persister = session.getFactory().getCollectionPersister( type.getRole() );
 
 		final Serializable collectionKey = extractCollectionKeyFromOwner( persister );
 		if ( collection!=null && (collection instanceof PersistentCollection) ) {
 			PersistentCollection wrapper = (PersistentCollection) collection;
 			if ( wrapper.setCurrentSession(session) ) {
 				//a "detached" collection!
 				if ( !isOwnerUnchanged( wrapper, persister, collectionKey ) ) {
 					// if the collection belonged to a different entity,
 					// clean up the existing state of the collection
 					removeCollection( persister, collectionKey, session );
 				}
 				reattachCollection(wrapper, type);
 			}
 			else {
 				// a collection loaded in the current session
 				// can not possibly be the collection belonging
 				// to the entity passed to update()
 				removeCollection(persister, collectionKey, session);
 			}
 		}
 		else {
 			// null or brand new collection
 			// this will also (inefficiently) handle arrays, which have
 			// no snapshot, so we can't do any better
 			removeCollection(persister, collectionKey, session);
 		}
 
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java
index 51962f1421..79b5e44711 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlParser.java
@@ -1,486 +1,486 @@
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
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.QueryException;
 import org.hibernate.hql.internal.antlr.HqlBaseParser;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 import antlr.ASTPair;
 import antlr.MismatchedTokenException;
 import antlr.RecognitionException;
 import antlr.Token;
 import antlr.TokenStreamException;
 import antlr.collections.AST;
 
 /**
  * Implements the semantic action methods defined in the HQL base parser to keep the grammar
  * source file a little cleaner.  Extends the parser class generated by ANTLR.
  *
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 public final class HqlParser extends HqlBaseParser {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( HqlParser.class );
 
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
 
 	private int traceDepth;
 
 	@Override
 	public void traceIn(String ruleName) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = StringHelper.repeat( '-', ( traceDepth++ * 2 ) ) + "-> ";
 		LOG.trace( prefix + ruleName );
 	}
 
 	@Override
 	public void traceOut(String ruleName) {
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
 	 * @param ex The recognition exception.
 	 *
 	 * @return AST - The new AST.
 	 *
 	 * @throws antlr.RecognitionException if the substitution was not possible.
 	 * @throws antlr.TokenStreamException if the substitution was not possible.
 	 */
 	@Override
 	public AST handleIdentifierError(Token token, RecognitionException ex)
 			throws RecognitionException, TokenStreamException {
 		// If the token can tell us if it could be an identifier...
 		if ( token instanceof HqlToken ) {
 			HqlToken hqlToken = (HqlToken) token;
 			// ... and the token could be an identifer and the error is
 			// a mismatched token error ...
 			if ( hqlToken.isPossibleID() && ( ex instanceof MismatchedTokenException ) ) {
 				MismatchedTokenException mte = (MismatchedTokenException) ex;
 				// ... and the expected token type was an identifier, then:
 				if ( mte.expecting == HqlTokenTypes.IDENT ) {
 					// Use the token as an identifier.
 					reportWarning(
 							"Keyword  '"
 									+ token.getText()
 									+ "' is being interpreted as an identifier due to: " + mte.getMessage()
 					);
 					// Add the token to the AST.
 					ASTPair currentAST = new ASTPair();
 					token.setType( HqlTokenTypes.WEIRD_IDENT );
 					astFactory.addASTChild( currentAST, astFactory.create( token ) );
 					consume();
 					return currentAST.root;
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
 	 *
 	 * @return AST - The equivalent sub-tree.
 	 */
 	@Override
 	public AST negateNode(AST x) {
 		//TODO: switch statements are always evil! We already had bugs because
 		//      of forgotten token types. Use polymorphism for this!
 		switch ( x.getType() ) {
 			case OR: {
 				x.setType( AND );
 				x.setText( "{and}" );
 				x.setFirstChild( negateNode( x.getFirstChild() ) );
 				x.getFirstChild().setNextSibling( negateNode( x.getFirstChild().getNextSibling() ) );
 				return x;
 			}
 			case AND: {
 				x.setType( OR );
 				x.setText( "{or}" );
 				x.setFirstChild( negateNode( x.getFirstChild() ) );
 				x.getFirstChild().setNextSibling( negateNode( x.getFirstChild().getNextSibling() ) );
 				return x;
 			}
 			case EQ: {
 				// (NOT (EQ a b) ) => (NE a b)
 				x.setType( NE );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case NE: {
 				// (NOT (NE a b) ) => (EQ a b)
 				x.setType( EQ );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case GT: {
 				// (NOT (GT a b) ) => (LE a b)
 				x.setType( LE );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case LT: {
 				// (NOT (LT a b) ) => (GE a b)
 				x.setType( GE );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case GE: {
 				// (NOT (GE a b) ) => (LT a b)
 				x.setType( LT );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case LE: {
 				// (NOT (LE a b) ) => (GT a b)
 				x.setType( GT );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case LIKE: {
 				// (NOT (LIKE a b) ) => (NOT_LIKE a b)
 				x.setType( NOT_LIKE );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case NOT_LIKE: {
 				// (NOT (NOT_LIKE a b) ) => (LIKE a b)
 				x.setType( LIKE );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case IN: {
 				x.setType( NOT_IN );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case NOT_IN: {
 				x.setType( IN );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case IS_NULL: {
 				// (NOT (IS_NULL a b) ) => (IS_NOT_NULL a b)
 				x.setType( IS_NOT_NULL );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case IS_NOT_NULL: {
 				// (NOT (IS_NOT_NULL a b) ) => (IS_NULL a b)
 				x.setType( IS_NULL );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case BETWEEN: {
 				// (NOT (BETWEEN a b) ) => (NOT_BETWEEN a b)
 				x.setType( NOT_BETWEEN );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 			case NOT_BETWEEN: {
 				// (NOT (NOT_BETWEEN a b) ) => (BETWEEN a b)
 				x.setType( BETWEEN );
 				x.setText( "{not}" + x.getText() );
 				return x;
 			}
 /* This can never happen because this rule will always eliminate the child NOT.
 			case NOT: {
 				// (NOT (NOT x) ) => (x)
 				return x.getFirstChild();
 			}
 */
 			default: {
 				// Just add a 'not' parent.
 				AST not = super.negateNode( x );
 				if ( not != x ) {
 					// relink the next sibling to the new 'not' parent
 					not.setNextSibling( x.getNextSibling() );
 					x.setNextSibling( null );
 				}
 				return not;
 			}
 		}
 	}
 
 	/**
 	 * Post process equality expressions, clean up the subtree.
 	 *
 	 * @param x The equality expression.
 	 *
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
 			if ( t.isPossibleID() ) {
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
 
 	private Map<String, Set<String>> treatMap;
 
 	@Override
 	protected void registerTreat(AST pathToTreat, AST treatAs) {
 		final String path = toPathText( pathToTreat );
 		final String subclassName = toPathText( treatAs );
 		LOG.debugf( "Registering discovered request to treat(%s as %s)", path, subclassName );
 
 		if ( treatMap == null ) {
 			treatMap = new HashMap<String, Set<String>>();
 		}
 
 		Set<String> subclassNames = treatMap.get( path );
 		if ( subclassNames == null ) {
 			subclassNames = new HashSet<String>();
 			treatMap.put( path, subclassNames );
 		}
 		subclassNames.add( subclassName );
 	}
 
 	private String toPathText(AST node) {
 		final String text = node.getText();
 		if ( text.equals( "." )
 				&& node.getFirstChild() != null
 				&& node.getFirstChild().getNextSibling() != null
 				&& node.getFirstChild().getNextSibling().getNextSibling() == null ) {
 			return toPathText( node.getFirstChild() ) + '.' + toPathText( node.getFirstChild().getNextSibling() );
 		}
 		return text;
 	}
 
 	public Map<String, Set<String>> getTreatMap() {
 		return treatMap == null ? Collections.<String, Set<String>>emptyMap() : treatMap;
 	}
 
-	static public void panic() {
+	public static void panic() {
 		//overriden to avoid System.exit
 		throw new QueryException( "Parser: panic" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
index ceda7f4a2f..a2e3a85443 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
@@ -1,639 +1,639 @@
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
 package org.hibernate.hql.internal.ast;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.query.spi.EntityGraphQueryHint;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.QueryExecutionRequestException;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.exec.BasicExecutor;
 import org.hibernate.hql.internal.ast.exec.DeleteExecutor;
 import org.hibernate.hql.internal.ast.exec.MultiTableDeleteExecutor;
 import org.hibernate.hql.internal.ast.exec.MultiTableUpdateExecutor;
 import org.hibernate.hql.internal.ast.exec.StatementExecutor;
 import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.InsertStatement;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.Statement;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.NodeTraverser;
 import org.hibernate.hql.spi.FilterTranslator;
 import org.hibernate.hql.spi.ParameterTranslations;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.loader.hql.QueryLoader;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 import antlr.ANTLRException;
 import antlr.RecognitionException;
 import antlr.TokenStreamException;
 import antlr.collections.AST;
 
 /**
  * A QueryTranslator that uses an Antlr-based parser.
  *
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 public class QueryTranslatorImpl implements FilterTranslator {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			QueryTranslatorImpl.class.getName()
 	);
 
 	private SessionFactoryImplementor factory;
 
 	private final String queryIdentifier;
 	private String hql;
 	private boolean shallowQuery;
 	private Map tokenReplacements;
 
 	//TODO:this is only needed during compilation .. can we eliminate the instvar?
 	private Map enabledFilters;
 
 	private boolean compiled;
 	private QueryLoader queryLoader;
 	private StatementExecutor statementExecutor;
 
 	private Statement sqlAst;
 	private String sql;
 
 	private ParameterTranslations paramTranslations;
 	private List<ParameterSpecification> collectedParameterSpecifications;
 	
 	private EntityGraphQueryHint entityGraphQueryHint;
 
 
 	/**
 	 * Creates a new AST-based query translator.
 	 *
 	 * @param queryIdentifier The query-identifier (used in stats collection)
 	 * @param query The hql query to translate
 	 * @param enabledFilters Currently enabled filters
 	 * @param factory The session factory constructing this translator instance.
 	 */
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 			String query,
 			Map enabledFilters,
 			SessionFactoryImplementor factory) {
 		this.queryIdentifier = queryIdentifier;
 		this.hql = query;
 		this.compiled = false;
 		this.shallowQuery = false;
 		this.enabledFilters = enabledFilters;
 		this.factory = factory;
 	}
 	
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 			String query,
 			Map enabledFilters,
 			SessionFactoryImplementor factory,
 			EntityGraphQueryHint entityGraphQueryHint) {
 		this( queryIdentifier, query, enabledFilters, factory );
 		this.entityGraphQueryHint = entityGraphQueryHint;
 	}
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param replacements Defined query substitutions.
 	 * @param shallow      Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	@Override
 	public void compile(
 			Map replacements,
 			boolean shallow) throws QueryException, MappingException {
 		doCompile( replacements, shallow, null );
 	}
 
 	/**
 	 * Compile a filter. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param collectionRole the role name of the collection used as the basis for the filter.
 	 * @param replacements   Defined query substitutions.
 	 * @param shallow        Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	@Override
 	public void compile(
 			String collectionRole,
 			Map replacements,
 			boolean shallow) throws QueryException, MappingException {
 		doCompile( replacements, shallow, collectionRole );
 	}
 
 	/**
 	 * Performs both filter and non-filter compiling.
 	 *
 	 * @param replacements   Defined query substitutions.
 	 * @param shallow        Does this represent a shallow (scalar or entity-id) select?
 	 * @param collectionRole the role name of the collection used as the basis for the filter, NULL if this
 	 *                       is not a filter.
 	 */
 	private synchronized void doCompile(Map replacements, boolean shallow, String collectionRole) {
 		// If the query is already compiled, skip the compilation.
 		if ( compiled ) {
 			LOG.debug( "compile() : The query is already compiled, skipping..." );
 			return;
 		}
 
 		// Remember the parameters for the compilation.
 		this.tokenReplacements = replacements;
 		if ( tokenReplacements == null ) {
 			tokenReplacements = new HashMap();
 		}
 		this.shallowQuery = shallow;
 
 		try {
 			// PHASE 1 : Parse the HQL into an AST.
 			final HqlParser parser = parse( true );
 
 			// PHASE 2 : Analyze the HQL AST, and produce an SQL AST.
 			final HqlSqlWalker w = analyze( parser, collectionRole );
 
 			sqlAst = (Statement) w.getAST();
 
 			// at some point the generate phase needs to be moved out of here,
 			// because a single object-level DML might spawn multiple SQL DML
 			// command executions.
 			//
 			// Possible to just move the sql generation for dml stuff, but for
 			// consistency-sake probably best to just move responsiblity for
 			// the generation phase completely into the delegates
 			// (QueryLoader/StatementExecutor) themselves.  Also, not sure why
 			// QueryLoader currently even has a dependency on this at all; does
 			// it need it?  Ideally like to see the walker itself given to the delegates directly...
 
 			if ( sqlAst.needsExecutor() ) {
 				statementExecutor = buildAppropriateStatementExecutor( w );
 			}
 			else {
 				// PHASE 3 : Generate the SQL.
 				generate( (QueryNode) sqlAst );
 				queryLoader = new QueryLoader( this, factory, w.getSelectClause() );
 			}
 
 			compiled = true;
 		}
 		catch ( QueryException qe ) {
 			if ( qe.getQueryString() == null ) {
 				throw qe.wrapWithQueryString( hql );
 			}
 			else {
 				throw qe;
 			}
 		}
 		catch ( RecognitionException e ) {
 			// we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
 			LOG.trace( "Converted antlr.RecognitionException", e );
 			throw QuerySyntaxException.convert( e, hql );
 		}
 		catch ( ANTLRException e ) {
 			// we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
 			LOG.trace( "Converted antlr.ANTLRException", e );
 			throw new QueryException( e.getMessage(), hql );
 		}
 
 		//only needed during compilation phase...
 		this.enabledFilters = null;
 	}
 
 	private void generate(AST sqlAst) throws QueryException, RecognitionException {
 		if ( sql == null ) {
 			final SqlGenerator gen = new SqlGenerator( factory );
 			gen.statement( sqlAst );
 			sql = gen.getSQL();
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "HQL: %s", hql );
 				LOG.debugf( "SQL: %s", sql );
 			}
 			gen.getParseErrorHandler().throwQueryException();
 			collectedParameterSpecifications = gen.getCollectedParameters();
 		}
 	}
 
 	private static final ASTPrinter SQL_TOKEN_PRINTER = new ASTPrinter( SqlTokenTypes.class );
 
 	private HqlSqlWalker analyze(HqlParser parser, String collectionRole) throws QueryException, RecognitionException {
 		final HqlSqlWalker w = new HqlSqlWalker( this, factory, parser, tokenReplacements, collectionRole );
 		final AST hqlAst = parser.getAST();
 
 		// Transform the tree.
 		w.statement( hqlAst );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( SQL_TOKEN_PRINTER.showAsString( w.getAST(), "--- SQL AST ---" ) );
 		}
 
 		w.getParseErrorHandler().throwQueryException();
 
 		return w;
 	}
 
 	private HqlParser parse(boolean filter) throws TokenStreamException, RecognitionException {
 		// Parse the query string into an HQL AST.
 		final HqlParser parser = HqlParser.getInstance( hql );
 		parser.setFilter( filter );
 
 		LOG.debugf( "parse() - HQL: %s", hql );
 		parser.statement();
 
 		final AST hqlAst = parser.getAST();
 
 		final NodeTraverser walker = new NodeTraverser( new JavaConstantConverter() );
 		walker.traverseDepthFirst( hqlAst );
 
 		showHqlAst( hqlAst );
 
 		parser.getParseErrorHandler().throwQueryException();
 		return parser;
 	}
 
 	private static final ASTPrinter HQL_TOKEN_PRINTER = new ASTPrinter( HqlTokenTypes.class );
 
 	void showHqlAst(AST hqlAst) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( HQL_TOKEN_PRINTER.showAsString( hqlAst, "--- HQL AST ---" ) );
 		}
 	}
 
 	private void errorIfDML() throws HibernateException {
 		if ( sqlAst.needsExecutor() ) {
 			throw new QueryExecutionRequestException( "Not supported for DML operations", hql );
 		}
 	}
 
 	private void errorIfSelect() throws HibernateException {
 		if ( !sqlAst.needsExecutor() ) {
 			throw new QueryExecutionRequestException( "Not supported for select queries", hql );
 		}
 	}
 	@Override
 	public String getQueryIdentifier() {
 		return queryIdentifier;
 	}
 
 	public Statement getSqlAST() {
 		return sqlAst;
 	}
 
 	private HqlSqlWalker getWalker() {
 		return sqlAst.getWalker();
 	}
 
 	/**
 	 * Types of the return values of an <tt>iterate()</tt> style query.
 	 *
 	 * @return an array of <tt>Type</tt>s.
 	 */
 	@Override
 	public Type[] getReturnTypes() {
 		errorIfDML();
 		return getWalker().getReturnTypes();
 	}
 	@Override
 	public String[] getReturnAliases() {
 		errorIfDML();
 		return getWalker().getReturnAliases();
 	}
 	@Override
 	public String[][] getColumnNames() {
 		errorIfDML();
 		return getWalker().getSelectClause().getColumnNames();
 	}
 	@Override
 	public Set<Serializable> getQuerySpaces() {
 		return getWalker().getQuerySpaces();
 	}
 
 	@Override
 	public List list(SessionImplementor session, QueryParameters queryParameters)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		QueryNode query = ( QueryNode ) sqlAst;
 		boolean hasLimit = queryParameters.getRowSelection() != null && queryParameters.getRowSelection().definesLimits();
 		boolean needsDistincting = ( query.getSelectClause().isDistinct() || hasLimit ) && containsCollectionFetches();
 
 		QueryParameters queryParametersToUse;
 		if ( hasLimit && containsCollectionFetches() ) {
 			LOG.firstOrMaxResultsSpecifiedWithCollectionFetch();
 			RowSelection selection = new RowSelection();
 			selection.setFetchSize( queryParameters.getRowSelection().getFetchSize() );
 			selection.setTimeout( queryParameters.getRowSelection().getTimeout() );
 			queryParametersToUse = queryParameters.createCopyUsing( selection );
 		}
 		else {
 			queryParametersToUse = queryParameters;
 		}
 
 		List results = queryLoader.list( session, queryParametersToUse );
 
 		if ( needsDistincting ) {
 			int includedCount = -1;
 			// NOTE : firstRow is zero-based
 			int first = !hasLimit || queryParameters.getRowSelection().getFirstRow() == null
 						? 0
 						: queryParameters.getRowSelection().getFirstRow();
 			int max = !hasLimit || queryParameters.getRowSelection().getMaxRows() == null
 						? -1
 						: queryParameters.getRowSelection().getMaxRows();
 			List tmp = new ArrayList();
 			IdentitySet distinction = new IdentitySet();
 			for ( final Object result : results ) {
 				if ( !distinction.add( result ) ) {
 					continue;
 				}
 				includedCount++;
 				if ( includedCount < first ) {
 					continue;
 				}
 				tmp.add( result );
 				// NOTE : ( max - 1 ) because first is zero-based while max is not...
 				if ( max >= 0 && ( includedCount - first ) >= ( max - 1 ) ) {
 					break;
 				}
 			}
 			results = tmp;
 		}
 
 		return results;
 	}
 
 	/**
 	 * Return the query results as an iterator
 	 */
 	@Override
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.iterate( queryParameters, session );
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 */
 	@Override
 	public ScrollableResults scroll(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.scroll( queryParameters, session );
 	}
 	@Override
 	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException {
 		errorIfSelect();
 		return statementExecutor.execute( queryParameters, session );
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 */
 	@Override
 	public String getSQLString() {
 		return sql;
 	}
 	@Override
 	public List<String> collectSqlStrings() {
 		ArrayList<String> list = new ArrayList<String>();
 		if ( isManipulationStatement() ) {
 			String[] sqlStatements = statementExecutor.getSqlStatements();
 			Collections.addAll( list, sqlStatements );
 		}
 		else {
 			list.add( sql );
 		}
 		return list;
 	}
 
 	// -- Package local methods for the QueryLoader delegate --
 
 	public boolean isShallowQuery() {
 		return shallowQuery;
 	}
 	@Override
 	public String getQueryString() {
 		return hql;
 	}
 	@Override
 	public Map getEnabledFilters() {
 		return enabledFilters;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		return getWalker().getNamedParameterLocations( name );
 	}
 	@Override
 	public boolean containsCollectionFetches() {
 		errorIfDML();
 		List collectionFetches = ( ( QueryNode ) sqlAst ).getFromClause().getCollectionFetches();
 		return collectionFetches != null && collectionFetches.size() > 0;
 	}
 	@Override
 	public boolean isManipulationStatement() {
 		return sqlAst.needsExecutor();
 	}
 	@Override
 	public void validateScrollability() throws HibernateException {
 		// Impl Note: allows multiple collection fetches as long as the
 		// entire fecthed graph still "points back" to a single
 		// root entity for return
 
 		errorIfDML();
 
 		QueryNode query = ( QueryNode ) sqlAst;
 
 		// If there are no collection fetches, then no further checks are needed
 		List collectionFetches = query.getFromClause().getCollectionFetches();
 		if ( collectionFetches.isEmpty() ) {
 			return;
 		}
 
 		// A shallow query is ok (although technically there should be no fetching here...)
 		if ( isShallowQuery() ) {
 			return;
 		}
 
 		// Otherwise, we have a non-scalar select with defined collection fetch(es).
 		// Make sure that there is only a single root entity in the return (no tuples)
 		if ( getReturnTypes().length > 1 ) {
 			throw new HibernateException( "cannot scroll with collection fetches and returned tuples" );
 		}
 
 		FromElement owner = null;
 		for ( Object o : query.getSelectClause().getFromElementsForLoad() ) {
 			// should be the first, but just to be safe...
 			final FromElement fromElement = (FromElement) o;
 			if ( fromElement.getOrigin() == null ) {
 				owner = fromElement;
 				break;
 			}
 		}
 
 		if ( owner == null ) {
 			throw new HibernateException( "unable to locate collection fetch(es) owner for scrollability checks" );
 		}
 
 		// This is not strictly true.  We actually just need to make sure that
 		// it is ordered by root-entity PK and that that order-by comes before
 		// any non-root-entity ordering...
 
 		AST primaryOrdering = query.getOrderByClause().getFirstChild();
 		if ( primaryOrdering != null ) {
 			// TODO : this is a bit dodgy, come up with a better way to check this (plus see above comment)
 			String [] idColNames = owner.getQueryable().getIdentifierColumnNames();
 			String expectedPrimaryOrderSeq = StringHelper.join(
 					", ",
 					StringHelper.qualify( owner.getTableAlias(), idColNames )
 			);
 			if (  !primaryOrdering.getText().startsWith( expectedPrimaryOrderSeq ) ) {
 				throw new HibernateException( "cannot scroll results with collection fetches which are not ordered primarily by the root entity's PK" );
 			}
 		}
 	}
 
 	private StatementExecutor buildAppropriateStatementExecutor(HqlSqlWalker walker) {
 		Statement statement = ( Statement ) walker.getAST();
 		if ( walker.getStatementType() == HqlSqlTokenTypes.DELETE ) {
 			FromElement fromElement = walker.getFinalFromClause().getFromElement();
 			Queryable persister = fromElement.getQueryable();
 			if ( persister.isMultiTable() ) {
 				return new MultiTableDeleteExecutor( walker );
 			}
 			else {
 				return new DeleteExecutor( walker, persister );
 			}
 		}
 		else if ( walker.getStatementType() == HqlSqlTokenTypes.UPDATE ) {
 			FromElement fromElement = walker.getFinalFromClause().getFromElement();
 			Queryable persister = fromElement.getQueryable();
 			if ( persister.isMultiTable() ) {
 				// even here, if only properties mapped to the "base table" are referenced
 				// in the set and where clauses, this could be handled by the BasicDelegate.
 				// TODO : decide if it is better performance-wise to doAfterTransactionCompletion that check, or to simply use the MultiTableUpdateDelegate
 				return new MultiTableUpdateExecutor( walker );
 			}
 			else {
 				return new BasicExecutor( walker, persister );
 			}
 		}
 		else if ( walker.getStatementType() == HqlSqlTokenTypes.INSERT ) {
 			return new BasicExecutor( walker, ( ( InsertStatement ) statement ).getIntoClause().getQueryable() );
 		}
 		else {
 			throw new QueryException( "Unexpected statement type" );
 		}
 	}
 	@Override
 	public ParameterTranslations getParameterTranslations() {
 		if ( paramTranslations == null ) {
 			paramTranslations = new ParameterTranslationsImpl( getWalker().getParameters() );
 		}
 		return paramTranslations;
 	}
 
 	public List<ParameterSpecification> getCollectedParameterSpecifications() {
 		return collectedParameterSpecifications;
 	}
 
 	@Override
 	public Class getDynamicInstantiationResultType() {
 		AggregatedSelectExpression aggregation = queryLoader.getAggregatedSelectExpression();
 		return aggregation == null ? null : aggregation.getAggregationResultType();
 	}
 
 	public static class JavaConstantConverter implements NodeTraverser.VisitationStrategy {
 		private AST dotRoot;
 		@Override
 		public void visit(AST node) {
 			if ( dotRoot != null ) {
 				// we are already processing a dot-structure
-				if ( ASTUtil.isSubtreeChild(dotRoot, node) ) {
+				if ( ASTUtil.isSubtreeChild( dotRoot, node ) ) {
 					return;
 				}
 				// we are now at a new tree level
 				dotRoot = null;
 			}
 
 			if ( node.getType() == HqlTokenTypes.DOT ) {
 				dotRoot = node;
 				handleDotStructure( dotRoot );
 			}
 		}
 		private void handleDotStructure(AST dotStructureRoot) {
 			String expression = ASTUtil.getPathText( dotStructureRoot );
 			Object constant = ReflectHelper.getConstantValue( expression );
 			if ( constant != null ) {
 				dotStructureRoot.setFirstChild( null );
 				dotStructureRoot.setType( HqlTokenTypes.JAVA_CONSTANT );
 				dotStructureRoot.setText( expression );
 			}
 		}
 	}
 
 	public EntityGraphQueryHint getEntityGraphQueryHint() {
 		return entityGraphQueryHint;
 	}
 
 	public void setEntityGraphQueryHint(EntityGraphQueryHint entityGraphQueryHint) {
 		this.entityGraphQueryHint = entityGraphQueryHint;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AssignmentSpecification.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AssignmentSpecification.java
index 5b98f9109a..3d0d89684c 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AssignmentSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AssignmentSpecification.java
@@ -1,165 +1,165 @@
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
 
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.entity.UnionSubclassEntityPersister;
 
 import antlr.collections.AST;
 
 /**
  * Encapsulates the information relating to an individual assignment within the
  * set clause of an HQL update statement.  This information is used during execution
  * of the update statements when the updates occur against "multi-table" stuff.
  *
  * @author Steve Ebersole
  */
 public class AssignmentSpecification {
 	private final Set tableNames;
 	private final ParameterSpecification[] hqlParameters;
 	private final AST eq;
 	private final SessionFactoryImplementor factory;
 
 	private String sqlAssignmentString;
 
 	public AssignmentSpecification(AST eq, Queryable persister) {
 		if ( eq.getType() != HqlSqlTokenTypes.EQ ) {
 			throw new QueryException( "assignment in set-clause not associated with equals" );
 		}
 
 		this.eq = eq;
 		this.factory = persister.getFactory();
 
 		// Needed to bump this up to DotNode, because that is the only thing which currently
 		// knows about the property-ref path in the correct format; it is either this, or
 		// recurse over the DotNodes constructing the property path just like DotNode does
 		// internally
 		DotNode lhs = ( DotNode ) eq.getFirstChild();
 		SqlNode rhs = ( SqlNode ) lhs.getNextSibling();
 
 		validateLhs( lhs );
 
 		final String propertyPath = lhs.getPropertyPath();
 		Set temp = new HashSet();
 		// yuck!
 		if ( persister instanceof UnionSubclassEntityPersister ) {
 			UnionSubclassEntityPersister usep = ( UnionSubclassEntityPersister ) persister;
 			String[] tables = persister.getConstraintOrderedTableNameClosure();
 			int size = tables.length;
 			for ( int i = 0; i < size; i ++ ) {
 				temp.add( tables[i] );
 			}
 		}
 		else {
 			temp.add(
 					persister.getSubclassTableName( persister.getSubclassPropertyTableNumber( propertyPath ) )
 			);
 		}
 		this.tableNames = Collections.unmodifiableSet( temp );
 
 		if (rhs==null) {
 			hqlParameters = new ParameterSpecification[0];
 		}
 		else if ( isParam( rhs ) ) {
-			hqlParameters = new ParameterSpecification[] { ( ( ParameterNode ) rhs ).getHqlParameterSpecification() };
+			hqlParameters = new ParameterSpecification[] { ( (ParameterNode) rhs ).getHqlParameterSpecification() };
 		}
 		else {
 			List parameterList = ASTUtil.collectChildren(
 					rhs,
 					new ASTUtil.IncludePredicate() {
 						public boolean include(AST node) {
 							return isParam( node );
 						}
 					}
 			);
 			hqlParameters = new ParameterSpecification[ parameterList.size() ];
 			Iterator itr = parameterList.iterator();
 			int i = 0;
 			while( itr.hasNext() ) {
-				hqlParameters[i++] = ( ( ParameterNode ) itr.next() ).getHqlParameterSpecification();
+				hqlParameters[i++] = ( (ParameterNode) itr.next() ).getHqlParameterSpecification();
 			}
 		}
 	}
 
 	public boolean affectsTable(String tableName) {
 		return this.tableNames.contains( tableName );
 	}
 
 	public ParameterSpecification[] getParameters() {
 		return hqlParameters;
 	}
 
 	public String getSqlAssignmentFragment() {
 		if ( sqlAssignmentString == null ) {
 			try {
 				SqlGenerator sqlGenerator = new SqlGenerator( factory );
 				sqlGenerator.comparisonExpr( eq, false );  // false indicates to not generate parens around the assignment
 				sqlAssignmentString = sqlGenerator.getSQL();
 			}
 			catch( Throwable t ) {
 				throw new QueryException( "cannot interpret set-clause assignment" );
 			}
 		}
 		return sqlAssignmentString;
 	}
 
 	private static boolean isParam(AST node) {
 		return node.getType() == HqlSqlTokenTypes.PARAM || node.getType() == HqlSqlTokenTypes.NAMED_PARAM;
 	}
 
 	private void validateLhs(FromReferenceNode lhs) {
 		// make sure the lhs is "assignable"...
 		if ( !lhs.isResolved() ) {
 			throw new UnsupportedOperationException( "cannot validate assignablity of unresolved node" );
 		}
 
 		if ( lhs.getDataType().isCollectionType() ) {
 			throw new QueryException( "collections not assignable in update statements" );
 		}
 		else if ( lhs.getDataType().isComponentType() ) {
 			throw new QueryException( "Components currently not assignable in update statements" );
 		}
 		else if ( lhs.getDataType().isEntityType() ) {
 			// currently allowed...
 		}
 
 		// TODO : why aren't these the same?
 		if ( lhs.getImpliedJoin() != null || lhs.getFromElement().isImplied() ) {
 			throw new QueryException( "Implied join paths are not assignable in update statements" );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryArithmeticOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryArithmeticOperatorNode.java
index 4e54bf9550..60e447c38c 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryArithmeticOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryArithmeticOperatorNode.java
@@ -1,229 +1,229 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 import antlr.SemanticException;
 
 /**
  * Nodes which represent binary arithmetic operators.
  *
  * @author Gavin King
  */
 public class BinaryArithmeticOperatorNode extends AbstractSelectExpression implements BinaryOperatorNode, DisplayableNode {
 
 	@Override
 	public void initialize() throws SemanticException {
 		final Node lhs = getLeftHandOperand();
 		final Node rhs = getRightHandOperand();
 		if ( lhs == null ) {
 			throw new SemanticException( "left-hand operand of a binary operator was null" );
 		}
 		if ( rhs == null ) {
 			throw new SemanticException( "right-hand operand of a binary operator was null" );
 		}
 
-		final Type lhType = ( lhs instanceof SqlNode ) ? ( ( SqlNode ) lhs ).getDataType() : null;
-		final Type rhType = ( rhs instanceof SqlNode ) ? ( ( SqlNode ) rhs ).getDataType() : null;
+		final Type lhType = ( lhs instanceof SqlNode ) ? ( (SqlNode) lhs ).getDataType() : null;
+		final Type rhType = ( rhs instanceof SqlNode ) ? ( (SqlNode) rhs ).getDataType() : null;
 
 		if ( ExpectedTypeAwareNode.class.isAssignableFrom( lhs.getClass() ) && rhType != null ) {
 			Type expectedType = null;
 			// we have something like : "? [op] rhs"
 			if ( isDateTimeType( rhType ) ) {
 				// more specifically : "? [op] datetime"
 				//      1) if the operator is MINUS, the param needs to be of
 				//          some datetime type
 				//      2) if the operator is PLUS, the param needs to be of
 				//          some numeric type
 				expectedType = getType() == HqlSqlTokenTypes.PLUS ? StandardBasicTypes.DOUBLE : rhType;
 			}
 			else {
 				expectedType = rhType;
 			}
 			( ( ExpectedTypeAwareNode ) lhs ).setExpectedType( expectedType );
 		}
 		else if ( ParameterNode.class.isAssignableFrom( rhs.getClass() ) && lhType != null ) {
 			Type expectedType = null;
 			// we have something like : "lhs [op] ?"
 			if ( isDateTimeType( lhType ) ) {
 				// more specifically : "datetime [op] ?"
 				//      1) if the operator is MINUS, we really cannot determine
 				//          the expected type as either another datetime or
 				//          numeric would be valid
 				//      2) if the operator is PLUS, the param needs to be of
 				//          some numeric type
 				if ( getType() == HqlSqlTokenTypes.PLUS ) {
 					expectedType = StandardBasicTypes.DOUBLE;
 				}
 			}
 			else {
 				expectedType = lhType;
 			}
-			( ( ExpectedTypeAwareNode ) rhs ).setExpectedType( expectedType );
+			( (ExpectedTypeAwareNode) rhs ).setExpectedType( expectedType );
 		}
 	}
 
 	/**
 	 * Figure out the type of the binary expression by looking at
 	 * the types of the operands. Sometimes we don't know both types,
 	 * if, for example, one is a parameter.
 	 */
 	@Override
 	public Type getDataType() {
 		if ( super.getDataType() == null ) {
 			super.setDataType( resolveDataType() );
 		}
 		return super.getDataType();
 	}
 
 	private Type resolveDataType() {
 		// TODO : we may also want to check that the types here map to exactly one column/JDBC-type
 		//      can't think of a situation where arithmetic expression between multi-column mappings
 		//      makes any sense.
 		Node lhs = getLeftHandOperand();
 		Node rhs = getRightHandOperand();
-		Type lhType = ( lhs instanceof SqlNode ) ? ( ( SqlNode ) lhs ).getDataType() : null;
-		Type rhType = ( rhs instanceof SqlNode ) ? ( ( SqlNode ) rhs ).getDataType() : null;
+		Type lhType = ( lhs instanceof SqlNode ) ? ( (SqlNode) lhs ).getDataType() : null;
+		Type rhType = ( rhs instanceof SqlNode ) ? ( (SqlNode) rhs ).getDataType() : null;
 		if ( isDateTimeType( lhType ) || isDateTimeType( rhType ) ) {
 			return resolveDateTimeArithmeticResultType( lhType, rhType );
 		}
 		else {
 			if ( lhType == null ) {
 				if ( rhType == null ) {
 					// we do not know either type
 					return StandardBasicTypes.DOUBLE; //BLIND GUESS!
 				}
 				else {
 					// we know only the rhs-hand type, so use that
 					return rhType;
 				}
 			}
 			else {
 				if ( rhType == null ) {
 					// we know only the lhs-hand type, so use that
 					return lhType;
 				}
 				else {
 					if ( lhType== StandardBasicTypes.DOUBLE || rhType==StandardBasicTypes.DOUBLE ) {
 						return StandardBasicTypes.DOUBLE;
 					}
 					if ( lhType==StandardBasicTypes.FLOAT || rhType==StandardBasicTypes.FLOAT ) {
 						return StandardBasicTypes.FLOAT;
 					}
 					if ( lhType==StandardBasicTypes.BIG_DECIMAL || rhType==StandardBasicTypes.BIG_DECIMAL ) {
 						return StandardBasicTypes.BIG_DECIMAL;
 					}
 					if ( lhType==StandardBasicTypes.BIG_INTEGER || rhType==StandardBasicTypes.BIG_INTEGER ) {
 						return StandardBasicTypes.BIG_INTEGER;
 					}
 					if ( lhType==StandardBasicTypes.LONG || rhType==StandardBasicTypes.LONG ) {
 						return StandardBasicTypes.LONG;
 					}
 					if ( lhType==StandardBasicTypes.INTEGER || rhType==StandardBasicTypes.INTEGER ) {
 						return StandardBasicTypes.INTEGER;
 					}
 					return lhType;
 				}
 			}
 		}
 	}
 
 	private boolean isDateTimeType(Type type) {
 		return type != null
 				&& ( java.util.Date.class.isAssignableFrom( type.getReturnedClass() )
 				|| java.util.Calendar.class.isAssignableFrom( type.getReturnedClass() ) );
 	}
 
 	private Type resolveDateTimeArithmeticResultType(Type lhType, Type rhType) {
 		// here, we work under the following assumptions:
 		//      ------------ valid cases --------------------------------------
 		//      1) datetime + {something other than datetime} : always results
 		//              in a datetime ( db will catch invalid conversions )
 		//      2) datetime - datetime : always results in a DOUBLE
 		//      3) datetime - {something other than datetime} : always results
 		//              in a datetime ( db will catch invalid conversions )
 		//      ------------ invalid cases ------------------------------------
 		//      4) datetime + datetime
 		//      5) {something other than datetime} - datetime
 		//      6) datetime * {any type}
 		//      7) datetime / {any type}
 		//      8) {any type} / datetime
 		// doing so allows us to properly handle parameters as either the left
 		// or right side here in the majority of cases
 		boolean lhsIsDateTime = isDateTimeType( lhType );
 		boolean rhsIsDateTime = isDateTimeType( rhType );
 
 		// handle the (assumed) valid cases:
 		// #1 - the only valid datetime addition synatx is one or the other is a datetime (but not both)
 		if ( getType() == HqlSqlTokenTypes.PLUS ) {
 			// one or the other needs to be a datetime for us to get into this method in the first place...
 			return lhsIsDateTime ? lhType : rhType;
 		}
 		else if ( getType() == HqlSqlTokenTypes.MINUS ) {
 			// #3 - note that this is also true of "datetime - :param"...
 			if ( lhsIsDateTime && !rhsIsDateTime ) {
 				return lhType;
 			}
 			// #2
 			if ( lhsIsDateTime && rhsIsDateTime ) {
 				return StandardBasicTypes.DOUBLE;
 			}
 		}
 		return null;
 	}
 
 	@Override
 	public void setScalarColumnText(int i) throws SemanticException {
 		ColumnHelper.generateSingleScalarColumn( this, i );
 	}
 
 	/**
 	 * Retrieves the left-hand operand of the operator.
 	 *
 	 * @return The left-hand operand
 	 */
 	@Override
 	public Node getLeftHandOperand() {
-		return ( Node ) getFirstChild();
+		return (Node) getFirstChild();
 	}
 
 	/**
 	 * Retrieves the right-hand operand of the operator.
 	 *
 	 * @return The right-hand operand
 	 */
 	@Override
 	public Node getRightHandOperand() {
-		return ( Node ) getFirstChild().getNextSibling();
+		return (Node) getFirstChild().getNextSibling();
 	}
 
 	@Override
 	public String getDisplayText() {
 		return "{dataType=" + getDataType() + "}";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryLogicOperatorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryLogicOperatorNode.java
index 1dddc185dd..709a844c8e 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryLogicOperatorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/BinaryLogicOperatorNode.java
@@ -1,270 +1,270 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.Arrays;
 
 import org.hibernate.HibernateException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.type.OneToOneType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Contract for nodes representing binary operators.
  *
  * @author Steve Ebersole
  */
 public class BinaryLogicOperatorNode extends HqlSqlWalkerNode implements BinaryOperatorNode {
 	/**
 	 * Performs the operator node initialization by seeking out any parameter
 	 * nodes and setting their expected type, if possible.
 	 */
 	@Override
 	public void initialize() throws SemanticException {
 		Node lhs = getLeftHandOperand();
 		if ( lhs == null ) {
 			throw new SemanticException( "left-hand operand of a binary operator was null" );
 		}
 		Node rhs = getRightHandOperand();
 		if ( rhs == null ) {
 			throw new SemanticException( "right-hand operand of a binary operator was null" );
 		}
 
 		Type lhsType = extractDataType( lhs );
 		Type rhsType = extractDataType( rhs );
 
 		if ( lhsType == null ) {
 			lhsType = rhsType;
 		}
 		if ( rhsType == null ) {
 			rhsType = lhsType;
 		}
 
 		if ( ExpectedTypeAwareNode.class.isAssignableFrom( lhs.getClass() ) ) {
 			( ( ExpectedTypeAwareNode ) lhs ).setExpectedType( rhsType );
 		}
 		if ( ExpectedTypeAwareNode.class.isAssignableFrom( rhs.getClass() ) ) {
 			( ( ExpectedTypeAwareNode ) rhs ).setExpectedType( lhsType );
 		}
 
 		mutateRowValueConstructorSyntaxesIfNecessary( lhsType, rhsType );
 	}
 
 	protected final void mutateRowValueConstructorSyntaxesIfNecessary(Type lhsType, Type rhsType) {
 		// TODO : this really needs to be delayed until after we definitively know all node types
 		// where this is currently a problem is parameters for which where we cannot unequivocally
 		// resolve an expected type
 		SessionFactoryImplementor sessionFactory = getSessionFactoryHelper().getFactory();
 		if ( lhsType != null && rhsType != null ) {
 			int lhsColumnSpan = getColumnSpan( lhsType, sessionFactory );
 			if ( lhsColumnSpan != getColumnSpan( rhsType, sessionFactory ) ) {
 				throw new TypeMismatchException(
 						"left and right hand sides of a binary logic operator were incompatibile [" +
 						lhsType.getName() + " : "+ rhsType.getName() + "]"
 				);
 			}
 			if ( lhsColumnSpan > 1 ) {
 				// for dialects which are known to not support ANSI-SQL row-value-constructor syntax,
 				// we should mutate the tree.
 				if ( !sessionFactory.getDialect().supportsRowValueConstructorSyntax() ) {
 					mutateRowValueConstructorSyntax( lhsColumnSpan );
 				}
 			}
 		}
 	}
 
 	private int getColumnSpan(Type type, SessionFactoryImplementor sfi) {
 		int columnSpan = type.getColumnSpan( sfi );
 		if ( columnSpan == 0 && type instanceof OneToOneType ) {
 			columnSpan = ( ( OneToOneType ) type ).getIdentifierOrUniqueKeyType( sfi ).getColumnSpan( sfi );
 		}
 		return columnSpan;
 	}
 
 	/**
 	 * Mutate the subtree relating to a row-value-constructor to instead use
 	 * a series of ANDed predicates.  This allows multi-column type comparisons
 	 * and explicit row-value-constructor syntax even on databases which do
 	 * not support row-value-constructor.
 	 * <p/>
 	 * For example, here we'd mutate "... where (col1, col2) = ('val1', 'val2) ..." to
 	 * "... where col1 = 'val1' and col2 = 'val2' ..."
 	 *
 	 * @param valueElements The number of elements in the row value constructor list.
 	 */
 	private void mutateRowValueConstructorSyntax(int valueElements) {
 		// mutation depends on the types of nodes involved...
 		int comparisonType = getType();
 		String comparisonText = getText();
 		setType( HqlSqlTokenTypes.AND );
 		setText( "AND" );
 		String[] lhsElementTexts = extractMutationTexts( getLeftHandOperand(), valueElements );
 		String[] rhsElementTexts = extractMutationTexts( getRightHandOperand(), valueElements );
 
 		ParameterSpecification lhsEmbeddedCompositeParameterSpecification =
 				getLeftHandOperand() == null || ( !ParameterNode.class.isInstance( getLeftHandOperand() ) )
 						? null
-						: ( ( ParameterNode ) getLeftHandOperand() ).getHqlParameterSpecification();
+						: ( (ParameterNode) getLeftHandOperand() ).getHqlParameterSpecification();
 
 		ParameterSpecification rhsEmbeddedCompositeParameterSpecification =
 				getRightHandOperand() == null || ( !ParameterNode.class.isInstance( getRightHandOperand() ) )
 						? null
-						: ( ( ParameterNode ) getRightHandOperand() ).getHqlParameterSpecification();
+						: ( (ParameterNode) getRightHandOperand() ).getHqlParameterSpecification();
 
 		translate(
 				valueElements,
 				comparisonType,
 				comparisonText,
 				lhsElementTexts,
 				rhsElementTexts,
 				lhsEmbeddedCompositeParameterSpecification,
 				rhsEmbeddedCompositeParameterSpecification,
 				this
 		);
 	}
 
 	protected void translate(
 			int valueElements, int comparisonType,
 			String comparisonText, String[] lhsElementTexts,
 			String[] rhsElementTexts,
 			ParameterSpecification lhsEmbeddedCompositeParameterSpecification,
 			ParameterSpecification rhsEmbeddedCompositeParameterSpecification,
 			AST container) {
 		for ( int i = valueElements - 1; i > 0; i-- ) {
 			if ( i == 1 ) {
 				AST op1 = getASTFactory().create( comparisonType, comparisonText );
 				AST lhs1 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[0] );
 				AST rhs1 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[0] );
 				op1.setFirstChild( lhs1 );
 				lhs1.setNextSibling( rhs1 );
 				container.setFirstChild( op1 );
 				AST op2 = getASTFactory().create( comparisonType, comparisonText );
 				AST lhs2 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[1] );
 				AST rhs2 = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[1] );
 				op2.setFirstChild( lhs2 );
 				lhs2.setNextSibling( rhs2 );
 				op1.setNextSibling( op2 );
 
 				// "pass along" our initial embedded parameter node(s) to the first generated
 				// sql fragment so that it can be handled later for parameter binding...
 				SqlFragment fragment = (SqlFragment) lhs1;
 				if ( lhsEmbeddedCompositeParameterSpecification != null ) {
 					fragment.addEmbeddedParameter( lhsEmbeddedCompositeParameterSpecification );
 				}
 				if ( rhsEmbeddedCompositeParameterSpecification != null ) {
 					fragment.addEmbeddedParameter( rhsEmbeddedCompositeParameterSpecification );
 				}
 			}
 			else {
 				AST op = getASTFactory().create( comparisonType, comparisonText );
 				AST lhs = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, lhsElementTexts[i] );
 				AST rhs = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, rhsElementTexts[i] );
 				op.setFirstChild( lhs );
 				lhs.setNextSibling( rhs );
 				AST newContainer = getASTFactory().create( HqlSqlTokenTypes.AND, "AND" );
 				container.setFirstChild( newContainer );
 				newContainer.setNextSibling( op );
 				container = newContainer;
 			}
 		}
 	}
 
 	protected static String[] extractMutationTexts(Node operand, int count) {
 		if ( operand instanceof ParameterNode ) {
 			String[] rtn = new String[count];
 			Arrays.fill( rtn, "?" );
 			return rtn;
 		}
 		else if ( operand.getType() == HqlSqlTokenTypes.VECTOR_EXPR ) {
 			String[] rtn = new String[ operand.getNumberOfChildren() ];
 			int x = 0;
 			AST node = operand.getFirstChild();
 			while ( node != null ) {
 				rtn[ x++ ] = node.getText();
 				node = node.getNextSibling();
 			}
 			return rtn;
 		}
 		else if ( operand instanceof SqlNode ) {
 			String nodeText = operand.getText();
 			if ( nodeText.startsWith( "(" ) ) {
 				nodeText = nodeText.substring( 1 );
 			}
 			if ( nodeText.endsWith( ")" ) ) {
 				nodeText = nodeText.substring( 0, nodeText.length() - 1 );
 			}
 			String[] splits = StringHelper.split( ", ", nodeText );
 			if ( count != splits.length ) {
 				throw new HibernateException( "SqlNode's text did not reference expected number of columns" );
 			}
 			return splits;
 		}
 		else {
 			throw new HibernateException( "dont know how to extract row value elements from node : " + operand );
 		}
 	}
 
 	protected Type extractDataType(Node operand) {
 		Type type = null;
 		if ( operand instanceof SqlNode ) {
 			type = ( (SqlNode) operand ).getDataType();
 		}
 		if ( type == null && operand instanceof ExpectedTypeAwareNode ) {
 			type = ( (ExpectedTypeAwareNode) operand ).getExpectedType();
 		}
 		return type;
 	}
 
 	@Override
     public Type getDataType() {
 		// logic operators by definition resolve to booleans
 		return StandardBasicTypes.BOOLEAN;
 	}
 
 	/**
 	 * Retrieves the left-hand operand of the operator.
 	 *
 	 * @return The left-hand operand
 	 */
 	public Node getLeftHandOperand() {
 		return ( Node ) getFirstChild();
 	}
 
 	/**
 	 * Retrieves the right-hand operand of the operator.
 	 *
 	 * @return The right-hand operand
 	 */
 	public Node getRightHandOperand() {
 		return ( Node ) getFirstChild().getNextSibling();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
index 51fd5c942c..1d4ef4ab24 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
@@ -1,749 +1,749 @@
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
-	public static boolean REGRESSION_STYLE_JOIN_SUPPRESSION;
+	public static boolean regressionStyleJoinSuppression;
 
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
-	private boolean fetch = false;
+	private boolean fetch;
 
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
 				getLhs().getFromElement(),
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
-		// (an additional check is the REGRESSION_STYLE_JOIN_SUPPRESSION check solely intended for the test suite)
+		// (an additional check is the regressionStyleJoinSuppression check solely intended for the test suite)
 		//
-		// The REGRESSION_STYLE_JOIN_SUPPRESSION is an additional check
+		// The regressionStyleJoinSuppression is an additional check
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
-		else if ( REGRESSION_STYLE_JOIN_SUPPRESSION ) {
+		else if ( regressionStyleJoinSuppression ) {
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
 		// even though we might find a pre-existing element by join path, for FromElements originating in a from-clause
 		// we should only ever use the found element if the aliases match (null != null here).  Implied joins are
 		// always (?) ok to reuse.
 		boolean useFoundFromElement = found && ( elem.isImplied() || areSame( classAlias, elem.getClassAlias() ) );
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java
index 755174714e..81a52dfaa9 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementType.java
@@ -1,577 +1,569 @@
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
 
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
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
 
 import org.jboss.logging.Logger;
 
 import antlr.SemanticException;
 
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
 
 
 	public void setJoinSequence(JoinSequence joinSequence) {
 		this.joinSequence = joinSequence;
 		joinSequence.applyTreatAsDeclarations( treatAsDeclarations );
 	}
 
 	public JoinSequence getJoinSequence() {
 		if ( joinSequence != null ) {
 			return joinSequence;
 		}
 
 		// Class names in the FROM clause result in a JoinSequence (the old FromParser does this).
 		if ( persister instanceof Joinable ) {
 			Joinable joinable = ( Joinable ) persister;
 			final JoinSequence joinSequence = fromElement.getSessionFactoryHelper().createJoinSequence().setRoot( joinable, getTableAlias() );
 			joinSequence.applyTreatAsDeclarations( treatAsDeclarations );
 			return joinSequence;
 		}
 		else {
 			// TODO: Should this really return null?  If not, figure out something better to do here.
 			return null;
 		}
 	}
 
 	private Set<String> treatAsDeclarations;
 
 	public void applyTreatAsDeclarations(Set<String> treatAsDeclarations) {
 		if ( treatAsDeclarations != null && !treatAsDeclarations.isEmpty() ) {
 			if ( this.treatAsDeclarations == null ) {
 				this.treatAsDeclarations = new HashSet<String>();
 			}
 
 			for ( String treatAsSubclassName : treatAsDeclarations ) {
 				try {
 					EntityPersister subclassPersister = fromElement.getSessionFactoryHelper().requireClassPersister( treatAsSubclassName );
 					this.treatAsDeclarations.add( subclassPersister.getEntityName() );
 				}
 				catch (SemanticException e) {
 					throw new QueryException( "Unable to locate persister for subclass named in TREAT-AS : " + treatAsSubclassName );
 				}
 			}
 
 			if ( joinSequence != null ) {
 				joinSequence.applyTreatAsDeclarations( this.treatAsDeclarations );
 			}
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
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
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
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Type toType(String propertyName) throws QueryException {
 			validate( propertyName );
 			return queryableCollection.getIndexType();
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
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
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
 			validate( propertyName );
 			return queryableCollection.toColumns( propertyName );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
index 173b58bab6..88d33dd61b 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IdentNode.java
@@ -1,382 +1,382 @@
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
 
-	private boolean nakedPropertyRef = false;
+	private boolean nakedPropertyRef;
 
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
 			buf.append( "{originalText=" ).append( getOriginalText() ).append( "}" );
 		}
 		return buf.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
index f247bedc34..a924daaf91 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/CompositeNestedGeneratedValueGenerator.java
@@ -1,137 +1,134 @@
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
 package org.hibernate.id;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * For composite identifiers, defines a number of "nested" generations that
  * need to happen to "fill" the identifier property(s).
  * <p/>
  * This generator is used implicitly for all composite identifier scenarios if an
  * explicit generator is not in place.  So it make sense to discuss the various 
  * potential scenarios:<ul>
  * <li>
  * <i>"embedded" composite identifier</i> - this is possible only in HBM mappings
  * as {@code <composite-id/>} (notice the lack of both a name and class attribute
  * declarations).  The term {@link org.hibernate.mapping.Component#isEmbedded() "embedded"}
  * here refers to the Hibernate usage which is actually the exact opposite of the JPA
  * meaning of "embedded".  Essentially this means that the entity class itself holds
  * the named composite pk properties.  This is very similar to the JPA {@code @IdClass}
  * usage, though without a separate pk-class for loading.
  * </li>
  * <li>
  * <i>pk-class as entity attribute</i> - this is possible in both annotations ({@code @EmbeddedId})
  * and HBM mappings ({@code <composite-id name="idAttributeName" class="PkClassName"/>})
  * </li>
  * <li>
  * <i>"embedded" composite identifier with a pk-class</i> - this is the JPA {@code @IdClass} use case
  * and is only possible in annotations
  * </li>
  * </ul>
  * <p/>
  * Most of the grunt work is done in {@link org.hibernate.mapping.Component}.
  *
  * @author Steve Ebersole
  */
 public class CompositeNestedGeneratedValueGenerator implements IdentifierGenerator, Serializable, IdentifierGeneratorAggregator {
 	/**
 	 * Contract for declaring how to locate the context for sub-value injection.
 	 */
 	public static interface GenerationContextLocator {
 		/**
 		 * Given the incoming object, determine the context for injecting back its generated
 		 * id sub-values.
 		 *
 		 * @param session The current session
 		 * @param incomingObject The entity for which we are generating id
 		 *
 		 * @return The injection context
 		 */
 		public Serializable locateGenerationContext(SessionImplementor session, Object incomingObject);
 	}
 
 	/**
 	 * Contract for performing the actual sub-value generation, usually injecting it into the
 	 * determined {@link GenerationContextLocator#locateGenerationContext context}
 	 */
 	public static interface GenerationPlan {
 		/**
 		 * Execute the value generation.
 		 *
 		 * @param session The current session
 		 * @param incomingObject The entity for which we are generating id
 		 * @param injectionContext The context into which the generated value can be injected
 		 */
 		public void execute(SessionImplementor session, Object incomingObject, Object injectionContext);
 
 		/**
 		 * Register any sub generators which implement {@link PersistentIdentifierGenerator} by their
 		 * {@link PersistentIdentifierGenerator#generatorKey generatorKey}.
 		 *
 		 * @param generatorMap The map of generators.
 		 */
 		public void registerPersistentGenerators(Map generatorMap);
 	}
 
 	private final GenerationContextLocator generationContextLocator;
 	private List generationPlans = new ArrayList();
 
 	public CompositeNestedGeneratedValueGenerator(GenerationContextLocator generationContextLocator) {
 		this.generationContextLocator = generationContextLocator;
 	}
 
 	public void addGeneratedValuePlan(GenerationPlan plan) {
 		generationPlans.add( plan );
 	}
 
+	@Override
 	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
 		final Serializable context = generationContextLocator.locateGenerationContext( session, object );
 
-		Iterator itr = generationPlans.iterator();
-		while ( itr.hasNext() ) {
-			final GenerationPlan plan = (GenerationPlan) itr.next();
+		for ( Object generationPlan : generationPlans ) {
+			final GenerationPlan plan = (GenerationPlan) generationPlan;
 			plan.execute( session, object, context );
 		}
 
 		return context;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void registerPersistentGenerators(Map generatorMap) {
-		final Iterator itr = generationPlans.iterator();
-		while ( itr.hasNext() ) {
-			final GenerationPlan plan = (GenerationPlan) itr.next();
+		for ( Object generationPlan : generationPlans ) {
+			final GenerationPlan plan = (GenerationPlan) generationPlan;
 			plan.registerPersistentGenerators( generatorMap );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
index 5ca1592f94..5ce6fd5ca7 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/ForeignGenerator.java
@@ -1,137 +1,133 @@
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
 package org.hibernate.id;
 import java.io.Serializable;
 import java.util.Properties;
 
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.TransientObjectException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * <b>foreign</b><br>
  * <br>
  * An <tt>Identifier</tt> generator that uses the value of the id property of an
  * associated object<br>
  * <br>
  * One mapping parameter is required: property.
  *
  * @author Gavin King
  */
 public class ForeignGenerator implements IdentifierGenerator, Configurable {
 	private String entityName;
 	private String propertyName;
 
 	/**
 	 * Getter for property 'entityName'.
 	 *
 	 * @return Value for property 'entityName'.
 	 */
 	public String getEntityName() {
 		return entityName;
 	}
 
 	/**
 	 * Getter for property 'propertyName'.
 	 *
 	 * @return Value for property 'propertyName'.
 	 */
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	/**
 	 * Getter for property 'role'.  Role is the {@link #getPropertyName property name} qualified by the
 	 * {@link #getEntityName entity name}.
 	 *
 	 * @return Value for property 'role'.
 	 */
 	public String getRole() {
 		return getEntityName() + '.' + getPropertyName();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void configure(Type type, Properties params, Dialect d) {
 		propertyName = params.getProperty( "property" );
 		entityName = params.getProperty( ENTITY_NAME );
 		if ( propertyName==null ) {
 			throw new MappingException( "param named \"property\" is required for foreign id generation strategy" );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Serializable generate(SessionImplementor sessionImplementor, Object object) {
 		Session session = ( Session ) sessionImplementor;
 
 		final EntityPersister persister = sessionImplementor.getFactory().getEntityPersister( entityName );
 		Object associatedObject = persister.getPropertyValue( object, propertyName );
 		if ( associatedObject == null ) {
 			throw new IdentifierGenerationException(
 					"attempted to assign id from null one-to-one property [" + getRole() + "]"
 			);
 		}
 
 		final EntityType foreignValueSourceType;
 		final Type propertyType = persister.getPropertyType( propertyName );
 		if ( propertyType.isEntityType() ) {
 			// the normal case
 			foreignValueSourceType = (EntityType) propertyType;
 		}
 		else {
 			// try identifier mapper
 			foreignValueSourceType = (EntityType) persister.getPropertyType( PropertyPath.IDENTIFIER_MAPPER_PROPERTY + "." + propertyName );
 		}
 
 		Serializable id;
 		try {
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					foreignValueSourceType.getAssociatedEntityName(),
 					associatedObject,
 					sessionImplementor
 			);
 		}
 		catch (TransientObjectException toe) {
 			id = session.save( foreignValueSourceType.getAssociatedEntityName(), associatedObject );
 		}
 
 		if ( session.contains(object) ) {
 			//abort the save (the object is already saved by a circular cascade)
 			return IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR;
 			//throw new IdentifierGenerationException("save associated object first, or disable cascade for inverse association");
 		}
 		return id;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 1318d54b85..411a5a95ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,2698 +1,2791 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2005-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.internal;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Reader;
 import java.io.Serializable;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.NClob;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.EntityNotFoundException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.IdentifierLoadAccess;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.criterion.NaturalIdentifier;
 import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.NonContextualLobCreator;
 import org.hibernate.engine.query.spi.FilterQueryPlan;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionObserver;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEvent;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.ClearEvent;
 import org.hibernate.event.spi.ClearEventListener;
 import org.hibernate.event.spi.DeleteEvent;
 import org.hibernate.event.spi.DeleteEventListener;
 import org.hibernate.event.spi.DirtyCheckEvent;
 import org.hibernate.event.spi.DirtyCheckEventListener;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.EvictEvent;
 import org.hibernate.event.spi.EvictEventListener;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.event.spi.FlushEventListener;
 import org.hibernate.event.spi.InitializeCollectionEvent;
 import org.hibernate.event.spi.InitializeCollectionEventListener;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
 import org.hibernate.event.spi.LoadEventListener.LoadType;
 import org.hibernate.event.spi.LockEvent;
 import org.hibernate.event.spi.LockEventListener;
 import org.hibernate.event.spi.MergeEvent;
 import org.hibernate.event.spi.MergeEventListener;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.event.spi.RefreshEvent;
 import org.hibernate.event.spi.RefreshEventListener;
 import org.hibernate.event.spi.ReplicateEvent;
 import org.hibernate.event.spi.ReplicateEventListener;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
 import org.hibernate.internal.CriteriaImpl.CriterionEntry;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.stat.internal.SessionStatisticsImpl;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * Concrete implementation of a Session.
  *
  * Exposes two interfaces:<ul>
  *     <li>{@link Session} to the application</li>
  *     <li>{@link org.hibernate.engine.spi.SessionImplementor} to other Hibernate components (SPI)</li>
  * </ul>
  *
  * This class is not thread-safe.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public final class SessionImpl extends AbstractSessionImpl implements EventSource {
 
 	// todo : need to find a clean way to handle the "event source" role
 	// a separate class responsible for generating/dispatching events just duplicates most of the Session methods...
 	// passing around separate interceptor, factory, actionQueue, and persistentContext is not manageable...
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionImpl.class.getName());
 
    private static final boolean TRACE_ENABLED = LOG.isTraceEnabled();
 
 	private transient long timestamp;
 
 	private transient SessionOwner sessionOwner;
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
 	private transient TransactionCoordinatorImpl transactionCoordinator;
 	private transient Interceptor interceptor;
 	private transient EntityNameResolver entityNameResolver = new CoordinatingEntityNameResolver();
 
 	private transient ConnectionReleaseMode connectionReleaseMode;
 	private transient FlushMode flushMode = FlushMode.AUTO;
 	private transient CacheMode cacheMode = CacheMode.NORMAL;
 
 	private transient boolean autoClear; //for EJB3
 	private transient boolean autoJoinTransactions = true;
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 
 	private transient int dontFlushFromFind;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
 	private final transient boolean isTransactionCoordinatorShared;
 	private transient TransactionObserver transactionObserver;
 
 	private SessionEventListenerManagerImpl sessionEventsManager = new SessionEventListenerManagerImpl();
 
 	/**
 	 * Constructor used for openSession(...) processing, as well as construction
 	 * of sessions for getCurrentSession().
 	 *
 	 * @param connection The user-supplied connection to use for this session.
 	 * @param factory The factory from which this session was obtained
 	 * @param transactionCoordinator The transaction coordinator to use, may be null to indicate that a new transaction
 	 * coordinator should get created.
 	 * @param autoJoinTransactions Should the session automatically join JTA transactions?
 	 * @param timestamp The timestamp for this session
 	 * @param interceptor The interceptor to be applied to this session
 	 * @param flushBeforeCompletionEnabled Should we auto flush before completion of transaction
 	 * @param autoCloseSessionEnabled Should we auto close after completion of transaction
 	 * @param connectionReleaseMode The mode by which we should release JDBC connections.
 	 * @param tenantIdentifier The tenant identifier to use.  May be null
 	 */
 	SessionImpl(
 			final Connection connection,
 			final SessionFactoryImpl factory,
 			final SessionOwner sessionOwner,
 			final TransactionCoordinatorImpl transactionCoordinator,
 			final boolean autoJoinTransactions,
 			final long timestamp,
 			final Interceptor interceptor,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode,
 			final String tenantIdentifier) {
 		super( factory, tenantIdentifier );
 		this.timestamp = timestamp;
 		this.sessionOwner = sessionOwner;
 		this.interceptor = interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 
 		if ( transactionCoordinator == null ) {
 			this.isTransactionCoordinatorShared = false;
 			this.connectionReleaseMode = connectionReleaseMode;
 			this.autoJoinTransactions = autoJoinTransactions;
 
 			this.transactionCoordinator = new TransactionCoordinatorImpl( connection, this );
 			this.transactionCoordinator.getJdbcCoordinator().getLogicalConnection().addObserver(
 					new ConnectionObserverStatsBridge( factory )
 			);
 		}
 		else {
 			if ( connection != null ) {
 				throw new SessionException( "Cannot simultaneously share transaction context and specify connection" );
 			}
 			this.transactionCoordinator = transactionCoordinator;
 			this.isTransactionCoordinatorShared = true;
 			this.autoJoinTransactions = false;
 			if ( autoJoinTransactions ) {
 				LOG.debug(
 						"Session creation specified 'autoJoinTransactions', which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 			}
 			if ( connectionReleaseMode != transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnectionReleaseMode() ) {
 				LOG.debug(
 						"Session creation specified 'connectionReleaseMode', which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 			}
 			this.connectionReleaseMode = transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnectionReleaseMode();
 
 			// add a transaction observer so that we can handle delegating managed actions back to THIS session
 			// versus the session that created (and therefore "owns") the transaction coordinator
 			transactionObserver = new TransactionObserver() {
 				@Override
 				public void afterBegin(TransactionImplementor transaction) {
 				}
 
 				@Override
 				public void beforeCompletion(TransactionImplementor transaction) {
 					if ( isOpen() && flushBeforeCompletionEnabled ) {
 						SessionImpl.this.managedFlush();
 					}
 					beforeTransactionCompletion( transaction );
 				}
 
 				@Override
 				public void afterCompletion(boolean successful, TransactionImplementor transaction) {
 					afterTransactionCompletion( transaction, successful );
 					if ( isOpen() && autoCloseSessionEnabled ) {
 						managedClose();
 					}
 					transactionCoordinator.removeObserver( this );
 				}
 			};
 
 			transactionCoordinator.addObserver( transactionObserver );
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if (factory.getStatistics().isStatisticsEnabled()) {
 			factory.getStatisticsImplementor().openSession();
 		}
 
       if ( TRACE_ENABLED )
 		   LOG.tracef( "Opened session at timestamp: %s", timestamp );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
 	}
 
+	@Override
 	public void clear() {
 		errorIfClosed();
 		// Do not call checkTransactionSynchStatus() here -- if a delayed
 		// afterCompletion exists, it can cause an infinite loop.
 		pulseTransactionCoordinator();
 		internalClear();
 	}
 
 	private void internalClear() {
 		persistenceContext.clear();
 		actionQueue.clear();
 
 		final ClearEvent event = new ClearEvent( this );
 		for ( ClearEventListener listener : listeners( EventType.CLEAR ) ) {
 			listener.onClear( event );
 		}
 	}
 
+	@Override
 	public long getTimestamp() {
 		checkTransactionSynchStatus();
 		return timestamp;
 	}
 
+	@Override
 	public Connection close() throws HibernateException {
 		LOG.trace( "Closing session" );
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed" );
 		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().closeSession();
 		}
 		getEventListenerManager().end();
 
 		try {
 			if ( !isTransactionCoordinatorShared ) {
 				return transactionCoordinator.close();
 			}
 			else {
 				if ( getActionQueue().hasAfterTransactionActions() ){
 					LOG.warn( "On close, shared Session had after transaction actions that have not yet been processed" );
 				}
 				else {
 					transactionCoordinator.removeObserver( transactionObserver );
 				}
 				return null;
 			}
 		}
 		finally {
 			setClosed();
 			cleanup();
 		}
 	}
 
+	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return autoJoinTransactions;
 	}
 
+	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
+	@Override
 	public boolean isOpen() {
 		checkTransactionSynchStatus();
 		return !isClosed();
 	}
 
+	@Override
 	public boolean isFlushModeNever() {
 		return FlushMode.isManualFlushMode( getFlushMode() );
 	}
 
+	@Override
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
+	@Override
 	public void managedFlush() {
 		if ( isClosed() ) {
 			LOG.trace( "Skipping auto-flush due to session closed" );
 			return;
 		}
 		LOG.trace( "Automatically flushing session" );
 		flush();
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		if ( isClosed() ) {
 			return false;
 		}
 		else if ( sessionOwner != null ) {
 			return sessionOwner.shouldAutoCloseSession();
 		}
 		else {
 			return isAutoCloseSessionEnabled();
 		}
 	}
 
+	@Override
 	public void managedClose() {
 		LOG.trace( "Automatically closing session" );
 		close();
 	}
 
+	@Override
 	public Connection connection() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnection();
 	}
 
+	@Override
 	public boolean isConnected() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isOpen();
 	}
 
+	@Override
 	public boolean isTransactionInProgress() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.isTransactionInProgress();
 	}
 
 	@Override
 	public Connection disconnect() throws HibernateException {
 		errorIfClosed();
 		LOG.debug( "Disconnecting session" );
 		transactionCoordinator.getJdbcCoordinator().releaseResources();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
 		LOG.debug( "Reconnecting session" );
 		checkTransactionSynchStatus();
 		transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualReconnect( conn );
 	}
 
+	@Override
 	public void setAutoClear(boolean enabled) {
 		errorIfClosed();
 		autoClear = enabled;
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		errorIfClosed();
 		autoJoinTransactions = false;
 	}
 
 	/**
 	 * Check if there is a Hibernate or JTA transaction in progress and,
 	 * if there is not, flush if necessary, make sure the connection has
 	 * been committed (if it is not in autocommit mode) and run the after
 	 * completion processing
 	 *
 	 * @param success Was the operation a success
 	 */
 	public void afterOperation(boolean success) {
 		if ( ! transactionCoordinator.isTransactionInProgress() ) {
 			transactionCoordinator.afterNonTransactionalQuery( success );
 		}
 	}
 
 	@Override
 	public void afterTransactionBegin(TransactionImplementor hibernateTransaction) {
 		errorIfClosed();
 		interceptor.afterTransactionBegin( hibernateTransaction );
 	}
 
 	@Override
 	public void beforeTransactionCompletion(TransactionImplementor hibernateTransaction) {
 		LOG.trace( "before transaction completion" );
 		actionQueue.beforeTransactionCompletion();
 		try {
 			interceptor.beforeTransactionCompletion( hibernateTransaction );
 		}
 		catch (Throwable t) {
 			LOG.exceptionInBeforeTransactionCompletionInterceptor( t );
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion(TransactionImplementor hibernateTransaction, boolean successful) {
 		LOG.trace( "after transaction completion" );
 		persistenceContext.afterTransactionCompletion();
 		actionQueue.afterTransactionCompletion( successful );
 
 		getEventListenerManager().transactionCompletion( successful );
 
 		try {
 			interceptor.afterTransactionCompletion( hibernateTransaction );
 		}
 		catch (Throwable t) {
 			LOG.exceptionInAfterTransactionCompletionInterceptor( t );
 		}
 
 		if ( autoClear ) {
 			internalClear();
 		}
 	}
 
 	@Override
 	public String onPrepareStatement(String sql) {
 		errorIfClosed();
 		sql = interceptor.onPrepareStatement( sql );
 		if ( sql == null || sql.length() == 0 ) {
 			throw new AssertionFailure( "Interceptor.onPrepareStatement() returned null or empty string." );
 		}
 		return sql;
 	}
 
 	@Override
 	public SessionEventListenerManagerImpl getEventListenerManager() {
 		return sessionEventsManager;
 	}
 
 	@Override
 	public void addEventListeners(SessionEventListener... listeners) {
 		getEventListenerManager().addListener( listeners );
 	}
 
 	@Override
 	public void startPrepareStatement() {
 		getEventListenerManager().jdbcPrepareStatementStart();
 	}
 
 	@Override
 	public void endPrepareStatement() {
 		getEventListenerManager().jdbcPrepareStatementEnd();
 	}
 
 	@Override
 	public void startStatementExecution() {
 		getEventListenerManager().jdbcExecuteStatementStart();
 	}
 
 	@Override
 	public void endStatementExecution() {
 		getEventListenerManager().jdbcExecuteStatementEnd();
 	}
 
 	@Override
 	public void startBatchExecution() {
 		getEventListenerManager().jdbcExecuteBatchStart();
 	}
 
 	@Override
 	public void endBatchExecution() {
 		getEventListenerManager().jdbcExecuteBatchEnd();
 	}
 
 	/**
 	 * clear all the internal collections, just
 	 * to help the garbage collector, does not
 	 * clear anything that is needed during the
 	 * afterTransactionCompletion() phase
 	 */
 	private void cleanup() {
 		persistenceContext.clear();
 	}
 
+	@Override
 	public LockMode getCurrentLockMode(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object == null ) {
 			throw new NullPointerException( "null object passed to getCurrentLockMode()" );
 		}
 		if ( object instanceof HibernateProxy ) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation(this);
 			if ( object == null ) {
 				return LockMode.NONE;
 			}
 		}
 		EntityEntry e = persistenceContext.getEntry(object);
 		if ( e == null ) {
 			throw new TransientObjectException( "Given object not associated with the session" );
 		}
 		if ( e.getStatus() != Status.MANAGED ) {
 			throw new ObjectDeletedException(
 					"The given object was deleted",
 					e.getId(),
 					e.getPersister().getEntityName()
 				);
 		}
 		return e.getLockMode();
 	}
 
+	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		// todo : should this get moved to PersistentContext?
 		// logically, is PersistentContext the "thing" to which an interceptor gets attached?
 		final Object result = persistenceContext.getEntity(key);
 		if ( result == null ) {
 			final Object newObject = interceptor.getEntity( key.getEntityName(), key.getIdentifier() );
 			if ( newObject != null ) {
 				lock( newObject, LockMode.NONE );
 			}
 			return newObject;
 		}
 		else {
 			return result;
 		}
 	}
 
 	private void checkNoUnresolvedActionsBeforeOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 && actionQueue.hasUnresolvedEntityInsertActions() ) {
 			throw new IllegalStateException( "There are delayed insert actions before operation as cascade level 0." );
 		}
 	}
 
 	private void checkNoUnresolvedActionsAfterOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 ) {
 			actionQueue.checkNoUnresolvedActionsAfterOperation();
 		}
 		delayedAfterCompletion();
 	}
 	
 	private void delayedAfterCompletion() {
 		transactionCoordinator.getSynchronizationCallbackCoordinator().processAnyDelayedAfterCompletion();
 	}
 
 	// saveOrUpdate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void saveOrUpdate(Object object) throws HibernateException {
 		saveOrUpdate( null, object );
 	}
 
+	@Override
 	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
 		fireSaveOrUpdate( new SaveOrUpdateEvent( entityName, obj, this ) );
 	}
 
 	private void fireSaveOrUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE_UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 	private <T> Iterable<T> listeners(EventType<T> type) {
 		return eventListenerGroup( type ).listeners();
 	}
 
 	private <T> EventListenerGroup<T> eventListenerGroup(EventType<T> type) {
 		return factory.getServiceRegistry().getService( EventListenerRegistry.class ).getEventListenerGroup( type );
 	}
 
 
 	// save() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public Serializable save(Object obj) throws HibernateException {
 		return save( null, obj );
 	}
 
+	@Override
 	public Serializable save(String entityName, Object object) throws HibernateException {
 		return fireSave( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private Serializable fireSave(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 		return event.getResultId();
 	}
 
 
 	// update() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void update(Object obj) throws HibernateException {
 		update( null, obj );
 	}
 
+	@Override
 	public void update(String entityName, Object object) throws HibernateException {
 		fireUpdate( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private void fireUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// lock() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( entityName, object, lockMode, this ) );
 	}
 
+	@Override
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return new LockRequestImpl(lockOptions);
 	}
 
+	@Override
 	public void lock(Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( object, lockMode, this ) );
 	}
 
 	private void fireLock(String entityName, Object object, LockOptions options) {
 		fireLock( new LockEvent( entityName, object, options, this) );
 	}
 
 	private void fireLock( Object object, LockOptions options) {
 		fireLock( new LockEvent( object, options, this ) );
 	}
 
 	private void fireLock(LockEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LockEventListener listener : listeners( EventType.LOCK ) ) {
 			listener.onLock( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// persist() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void persist(String entityName, Object object) throws HibernateException {
 		firePersist( new PersistEvent( entityName, object, this ) );
 	}
 
+	@Override
 	public void persist(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
-	public void persist(String entityName, Object object, Map copiedAlready)
-	throws HibernateException {
+	@Override
+	public void persist(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		firePersist( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersist(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void firePersist(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// persistOnFlush() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persistOnFlush(String entityName, Object object)
 			throws HibernateException {
 		firePersistOnFlush( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persistOnFlush(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
+	@Override
 	public void persistOnFlush(String entityName, Object object, Map copiedAlready)
 			throws HibernateException {
 		firePersistOnFlush( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersistOnFlush(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// merge() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public Object merge(String entityName, Object object) throws HibernateException {
 		return fireMerge( new MergeEvent( entityName, object, this ) );
 	}
 
+	@Override
 	public Object merge(Object object) throws HibernateException {
 		return merge( null, object );
 	}
 
+	@Override
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		fireMerge( copiedAlready, new MergeEvent( entityName, object, this ) );
 	}
 
 	private Object fireMerge(MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event, copiedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// delete() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * Delete a persistent object
-	 */
+	@Override
 	public void delete(Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( object, this ) );
 	}
 
-	/**
-	 * Delete a persistent object (by explicit entity name)
-	 */
+	@Override
 	public void delete(String entityName, Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, this ) );
 	}
 
-	/**
-	 * Delete a persistent object
-	 */
+	@Override
 	public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled, Set transientEntities) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, isCascadeDeleteEnabled, this ), transientEntities );
 	}
-	
-	// TODO: The removeOrphan concept is a temporary "hack" for HHH-6484.  This should be removed once action/task
-	// ordering is improved.
+
+	@Override
 	public void removeOrphanBeforeUpdates(String entityName, Object child) {
+		// TODO: The removeOrphan concept is a temporary "hack" for HHH-6484.  This should be removed once action/task
+		// ordering is improved.
 		fireDelete( new DeleteEvent( entityName, child, false, true, this ) );
 	}
 
 	private void fireDelete(DeleteEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event, transientEntities );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// load()/get() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void load(Object object, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, object, this);
 		fireLoad( event, LoadEventListener.RELOAD );
 	}
 
+	@Override
 	public Object load(Class entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).getReference( id );
 	}
 
+	@Override
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).getReference( id );
 	}
 
+	@Override
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).load( id );
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).load( id );
 	}
 
 	/**	
 	 * Load the data for the object with the specified id into a newly created object.
 	 * This is only called when lazily initializing a proxy.
 	 * Do NOT return a proxy.
 	 */
+	@Override
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			EntityPersister persister = getFactory().getEntityPersister(entityName);
 			LOG.debugf( "Initializing proxy: %s", MessageHelper.infoString( persister, id, getFactory() ) );
 		}
 
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, LoadEventListener.IMMEDIATE_LOAD);
 		return event.getResult();
 	}
 
+	@Override
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		// todo : remove
 		LoadEventListener.LoadType type = nullable
 				? LoadEventListener.INTERNAL_LOAD_NULLABLE
 				: eager
 						? LoadEventListener.INTERNAL_LOAD_EAGER
 						: LoadEventListener.INTERNAL_LOAD_LAZY;
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad( event, type );
 		if ( !nullable ) {
 			UnresolvableObjectException.throwIfNull( event.getResult(), id, entityName );
 		}
 		return event.getResult();
 	}
 
+	@Override
 	public Object load(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
+	@Override
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).getReference( id );
 	}
 
+	@Override
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
+	@Override
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).getReference( id );
 	}
 
+	@Override
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
+	@Override
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).load( id );
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).load( id );
 	}
 	
 	@Override
 	public IdentifierLoadAccessImpl byId(String entityName) {
 		return new IdentifierLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public IdentifierLoadAccessImpl byId(Class entityClass) {
 		return new IdentifierLoadAccessImpl( entityClass );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return new NaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(Class entityClass) {
 		return new NaturalIdLoadAccessImpl( entityClass );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return new SimpleNaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass) {
 		return new SimpleNaturalIdLoadAccessImpl( entityClass );
 	}
 
 	private void fireLoad(LoadEvent event, LoadType loadType) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
 			listener.onResolveNaturalId( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void refresh(Object object) throws HibernateException {
 		refresh( null, object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, this ) );
 	}
 
+	@Override
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent( object, lockMode, this ) );
 	}
 
+	@Override
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
 		refresh( null, object, lockOptions );
 	}
+
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, lockOptions, this ) );
 	}
 
+	@Override
 	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
 		fireRefresh( refreshedAlready, new RefreshEvent( entityName, object, this ) );
 	}
 
 	private void fireRefresh(RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event, refreshedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// replicate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
 		fireReplicate( new ReplicateEvent( obj, replicationMode, this ) );
 	}
 
+	@Override
 	public void replicate(String entityName, Object obj, ReplicationMode replicationMode)
 	throws HibernateException {
 		fireReplicate( new ReplicateEvent( entityName, obj, replicationMode, this ) );
 	}
 
 	private void fireReplicate(ReplicateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( ReplicateEventListener listener : listeners( EventType.REPLICATE ) ) {
 			listener.onReplicate( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// evict() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * remove any hard references to the entity that are held by the infrastructure
 	 * (references held by application or other persistent instances are okay)
 	 */
+	@Override
 	public void evict(Object object) throws HibernateException {
 		fireEvict( new EvictEvent( object, this ) );
 	}
 
 	private void fireEvict(EvictEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( EvictEventListener listener : listeners( EventType.EVICT ) ) {
 			listener.onEvict( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	/**
 	 * detect in-memory changes, determine if the changes are to tables
 	 * named in the query and, if so, complete execution the flush
 	 */
 	protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
 		errorIfClosed();
 		if ( ! isTransactionInProgress() ) {
 			// do not auto-flush while outside a transaction
 			return false;
 		}
 		AutoFlushEvent event = new AutoFlushEvent( querySpaces, this );
 		for ( AutoFlushEventListener listener : listeners( EventType.AUTO_FLUSH ) ) {
 			listener.onAutoFlush( event );
 		}
 		return event.isFlushRequired();
 	}
 
+	@Override
 	public boolean isDirty() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.debug( "Checking session dirtiness" );
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
 			LOG.debug( "Session dirty (scheduled updates and insertions)" );
 			return true;
 		}
 		DirtyCheckEvent event = new DirtyCheckEvent( this );
 		for ( DirtyCheckEventListener listener : listeners( EventType.DIRTY_CHECK ) ) {
 			listener.onDirtyCheck( event );
 		}
 		delayedAfterCompletion();
 		return event.isDirty();
 	}
 
+	@Override
 	public void flush() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new HibernateException("Flush during cascade is dangerous");
 		}
 		FlushEvent flushEvent = new FlushEvent( this );
 		for ( FlushEventListener listener : listeners( EventType.FLUSH ) ) {
 			listener.onFlush( flushEvent );
 		}
 		delayedAfterCompletion();
 	}
 
+	@Override
 	public void forceFlush(EntityEntry entityEntry) throws HibernateException {
 		errorIfClosed();
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Flushing to force deletion of re-saved object: %s",
 					MessageHelper.infoString( entityEntry.getPersister(), entityEntry.getId(), getFactory() ) );
 		}
 
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new ObjectDeletedException(
 				"deleted object would be re-saved by cascade (remove deleted object from associations)",
 				entityEntry.getId(),
 				entityEntry.getPersister().getEntityName()
 			);
 		}
 
 		flush();
 	}
 
+	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		
 		HQLQueryPlan plan = queryParameters.getQueryPlan();
 		if (plan == null) {
 			plan = getHQLQueryPlan( query, false );
 		}
 		
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
+	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 		return result;
 	}
 
+	@Override
     public int executeNativeUpdate(NativeSQLQuerySpecification nativeQuerySpecification,
             QueryParameters queryParameters) throws HibernateException {
         errorIfClosed();
         checkTransactionSynchStatus();
         queryParameters.validateParameters();
         NativeSQLQueryPlan plan = getNativeSQLQueryPlan( nativeQuerySpecification );
 
 
         autoFlushIfRequired( plan.getCustomQuery().getQuerySpaces() );
 
         boolean success = false;
         int result = 0;
         try {
             result = plan.performExecuteUpdate(queryParameters, this);
             success = true;
         } finally {
             afterOperation(success);
     		delayedAfterCompletion();
         }
         return result;
     }
 
+	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, true );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return plan.performIterate( queryParameters, this );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
+	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return plan.performScroll( queryParameters, this );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
+	@Override
 	public Query createFilter(Object collection, String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		CollectionFilterImpl filter = new CollectionFilterImpl(
 				queryString,
 		        collection,
 		        this,
 		        getFilterQueryPlan( collection, queryString, null, false ).getParameterMetadata()
 		);
 		filter.setComment( queryString );
 		delayedAfterCompletion();
 		return filter;
 	}
 
+	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Query query = super.getNamedQuery( queryName );
 		delayedAfterCompletion();
 		return query;
 	}
 
+	@Override
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( factory.getEntityPersister( entityName ), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
+	@Override
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Object result = interceptor.instantiate( persister.getEntityName(), persister.getEntityMetamodel().getEntityMode(), id );
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		delayedAfterCompletion();
 		return result;
 	}
 
+	@Override
 	public void setFlushMode(FlushMode flushMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting flush mode to: {0}", flushMode );
 		this.flushMode = flushMode;
 	}
 
+	@Override
 	public FlushMode getFlushMode() {
 		checkTransactionSynchStatus();
 		return flushMode;
 	}
 
+	@Override
 	public CacheMode getCacheMode() {
 		checkTransactionSynchStatus();
 		return cacheMode;
 	}
 
+	@Override
 	public void setCacheMode(CacheMode cacheMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting cache mode to: {0}", cacheMode );
 		this.cacheMode= cacheMode;
 	}
 
+	@Override
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getTransaction();
 	}
 
+	@Override
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
+	@Override
 	public EntityPersister getEntityPersister(final String entityName, final Object object) {
 		errorIfClosed();
 		if (entityName==null) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			// try block is a hack around fact that currently tuplizers are not
 			// given the opportunity to resolve a subclass entity name.  this
 			// allows the (we assume custom) interceptor the ability to
 			// influence this decision if we were not able to based on the
 			// given entityName
 			try {
 				return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 			}
 			catch( HibernateException e ) {
 				try {
 					return getEntityPersister( null, object );
 				}
 				catch( HibernateException e2 ) {
 					throw e;
 				}
 			}
 		}
 	}
 
 	// not for internal use:
+	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.getSession() != this ) {
 				throw new TransientObjectException( "The proxy was not associated with this session" );
 			}
 			return li.getIdentifier();
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			if ( entry == null ) {
 				throw new TransientObjectException( "The instance was not associated with this session" );
 			}
 			return entry.getId();
 		}
 	}
 
 	/**
 	 * Get the id value for an object that is actually associated with the session. This
 	 * is a bit stricter than getEntityIdentifierIfNotUnsaved().
 	 */
+	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		if ( object instanceof HibernateProxy ) {
 			return getProxyIdentifier( object );
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			return entry != null ? entry.getId() : null;
 		}
 	}
 
 	private Serializable getProxyIdentifier(Object proxy) {
 		return ( (HibernateProxy) proxy ).getHibernateLazyInitializer().getIdentifier();
 	}
 
 	private FilterQueryPlan getFilterQueryPlan(
 			Object collection,
 			String filter,
 			QueryParameters parameters,
 			boolean shallow) throws HibernateException {
 		if ( collection == null ) {
 			throw new NullPointerException( "null collection passed to filter" );
 		}
 
 		CollectionEntry entry = persistenceContext.getCollectionEntryOrNull( collection );
 		final CollectionPersister roleBeforeFlush = (entry == null) ? null : entry.getLoadedPersister();
 
 		FilterQueryPlan plan = null;
 		if ( roleBeforeFlush == null ) {
 			// if it was previously unreferenced, we need to flush in order to
 			// get its state into the database in order to execute query
 			flush();
 			entry = persistenceContext.getCollectionEntryOrNull( collection );
 			CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 			if ( roleAfterFlush == null ) {
 				throw new QueryException( "The collection was unreferenced" );
 			}
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 		}
 		else {
 			// otherwise, we only need to flush if there are in-memory changes
 			// to the queried tables
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleBeforeFlush.getRole(), shallow, getEnabledFilters() );
 			if ( autoFlushIfRequired( plan.getQuerySpaces() ) ) {
 				// might need to run a different filter entirely after the flush
 				// because the collection role may have changed
 				entry = persistenceContext.getCollectionEntryOrNull( collection );
 				CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 				if ( roleBeforeFlush != roleAfterFlush ) {
 					if ( roleAfterFlush == null ) {
 						throw new QueryException( "The collection was dereferenced" );
 					}
 					plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 				}
 			}
 		}
 
 		if ( parameters != null ) {
 			parameters.getPositionalParameterValues()[0] = entry.getLoadedKey();
 			parameters.getPositionalParameterTypes()[0] = entry.getLoadedPersister().getKeyType();
 		}
 
 		return plan;
 	}
 
+	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, false );
 		List results = Collections.EMPTY_LIST;
 
 		boolean success = false;
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
+	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		Iterator itr = plan.performIterate( queryParameters, this );
 		delayedAfterCompletion();
 		return itr;
 	}
 
+	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
+	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
+	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
+	@Override
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, this);
 	}
 
+	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteriaImpl,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
+	@Override
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 				
 		final NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess( criteriaImpl );
 		if ( naturalIdLoadAccess != null ) {
 			// EARLY EXIT!
 			return Arrays.asList( naturalIdLoadAccess.load() );
 		}
 
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteriaImpl,
 					implementors[i],
 					getLoadQueryInfluencers()
 				);
 
 			spaces.addAll( loaders[i].getQuerySpaces() );
 
 		}
 
 		autoFlushIfRequired(spaces);
 
 		List results = Collections.EMPTY_LIST;
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			for( int i=0; i<size; i++ ) {
 				final List currentResults = loaders[i].list(this);
 				currentResults.addAll(results);
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 
 		return results;
 	}
 
 	/**
 	 * Checks to see if the CriteriaImpl is a naturalId lookup that can be done via
 	 * NaturalIdLoadAccess
 	 *
 	 * @param criteria The criteria to check as a complete natural identifier lookup.
 	 *
 	 * @return A fully configured NaturalIdLoadAccess or null, if null is returned the standard CriteriaImpl execution
 	 *         should be performed
 	 */
 	private NaturalIdLoadAccess tryNaturalIdLoadAccess(CriteriaImpl criteria) {
 		// See if the criteria lookup is by naturalId
 		if ( !criteria.isLookupByNaturalKey() ) {
 			return null;
 		}
 
 		final String entityName = criteria.getEntityOrClassName();
 		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
 
 		// Verify the entity actually has a natural id, needed for legacy support as NaturalIdentifier criteria
 		// queries did no natural id validation
 		if ( !entityPersister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// Since isLookupByNaturalKey is true there can be only one CriterionEntry and getCriterion() will
 		// return an instanceof NaturalIdentifier
 		final CriterionEntry criterionEntry = (CriterionEntry) criteria.iterateExpressionEntries().next();
 		final NaturalIdentifier naturalIdentifier = (NaturalIdentifier) criterionEntry.getCriterion();
 
 		final Map<String, Object> naturalIdValues = naturalIdentifier.getNaturalIdValues();
 		final int[] naturalIdentifierProperties = entityPersister.getNaturalIdentifierProperties();
 
 		// Verify the NaturalIdentifier criterion includes all naturalId properties, first check that the property counts match
 		if ( naturalIdentifierProperties.length != naturalIdValues.size() ) {
 			return null;
 		}
 
 		final String[] propertyNames = entityPersister.getPropertyNames();
 		final NaturalIdLoadAccess naturalIdLoader = this.byNaturalId( entityName );
 
 		// Build NaturalIdLoadAccess and in the process verify all naturalId properties were specified
 		for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
 			final String naturalIdProperty = propertyNames[naturalIdentifierProperties[i]];
 			final Object naturalIdValue = naturalIdValues.get( naturalIdProperty );
 
 			if ( naturalIdValue == null ) {
 				// A NaturalId property is missing from the critera query, can't use NaturalIdLoadAccess
 				return null;
 			}
 
 			naturalIdLoader.using( naturalIdProperty, naturalIdValue );
 		}
 
 		// Critera query contains a valid naturalId, use the new API
 		LOG.warn( "Session.byNaturalId(" + entityName
 				+ ") should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria" );
 
 		return naturalIdLoader;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
+	@Override
 	public boolean contains(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			//do not use proxiesByKey, since not all
 			//proxies that point to this session's
 			//instances are in that collection!
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				//if it is an uninitialized proxy, pointing
 				//with this session, then when it is accessed,
 				//the underlying instance will be "contained"
 				return li.getSession()==this;
 			}
 			else {
 				//if it is initialized, see if the underlying
 				//instance is contained, since we need to
 				//account for the fact that it might have been
 				//evicted
 				object = li.getImplementation();
 			}
 		}
 		// A session is considered to contain an entity only if the entity has
 		// an entry in the session's persistence context and the entry reports
 		// that the entity has not been removed
 		EntityEntry entry = persistenceContext.getEntry( object );
 		delayedAfterCompletion();
 		return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 	}
 
+	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createQuery( queryString );
 	}
 
+	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createSQLQuery( sql );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
+	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Scroll SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return loader.scroll(queryParameters, this);
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
+	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			List results = loader.list(this, queryParameters);
 			success = true;
 			return results;
 		}
 		finally {
 			dontFlushFromFind--;
 			delayedAfterCompletion();
 			afterOperation(success);
 		}
 	}
 
+	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		checkTransactionSynchStatus();
 		return factory;
 	}
 
+	@Override
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		InitializeCollectionEvent event = new InitializeCollectionEvent( collection, this );
 		for ( InitializeCollectionEventListener listener : listeners( EventType.INIT_COLLECTION ) ) {
 			listener.onInitializeCollection( event );
 		}
 		delayedAfterCompletion();
 	}
 
+	@Override
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			LazyInitializer initializer = ( ( HibernateProxy ) object ).getHibernateLazyInitializer();
 			// it is possible for this method to be called during flush processing,
 			// so make certain that we do not accidentally initialize an uninitialized proxy
 			if ( initializer.isUninitialized() ) {
 				return initializer.getEntityName();
 			}
 			object = initializer.getImplementation();
 		}
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if (entry==null) {
 			return guessEntityName(object);
 		}
 		else {
 			return entry.getPersister().getEntityName();
 		}
 	}
 
+	@Override
 	public String getEntityName(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if (object instanceof HibernateProxy) {
 			if ( !persistenceContext.containsProxy( object ) ) {
 				throw new TransientObjectException("proxy was not associated with the session");
 			}
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if ( entry == null ) {
 			throwTransientObjectException( object );
 		}
 		return entry.getPersister().getEntityName();
 	}
 
 	private void throwTransientObjectException(Object object) throws HibernateException {
 		throw new TransientObjectException(
 				"object references an unsaved transient instance - save the transient instance before flushing: " +
 				guessEntityName(object)
 			);
 	}
 
+	@Override
 	public String guessEntityName(Object object) throws HibernateException {
 		errorIfClosed();
 		return entityNameResolver.resolveEntityName( object );
 	}
 
+	@Override
 	public void cancelQuery() throws HibernateException {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().cancelLastQuery();
 	}
 
+	@Override
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
+	@Override
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
+	@Override
 	public String toString() {
 		StringBuilder buf = new StringBuilder(500)
 			.append( "SessionImpl(" );
 		if ( !isClosed() ) {
 			buf.append(persistenceContext)
 				.append(";")
 				.append(actionQueue);
 		}
 		else {
 			buf.append("<closed>");
 		}
 		return buf.append(')').toString();
 	}
 
+	@Override
 	public ActionQueue getActionQueue() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 
+	@Override
 	public PersistenceContext getPersistenceContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 
+	@Override
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl(this);
 	}
 
+	@Override
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
+	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );
 	}
 
+	@Override
 	public void setReadOnly(Object entity, boolean readOnly) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly( entity, readOnly );
 	}
 
+	@Override
 	public void doWork(final Work work) throws HibernateException {
 		WorkExecutorVisitable<Void> realWork = new WorkExecutorVisitable<Void>() {
 			@Override
 			public Void accept(WorkExecutor<Void> workExecutor, Connection connection) throws SQLException {
 				workExecutor.executeWork( work, connection );
 				return null;
 			}
 		};
 		doWork( realWork );
 	}
 
+	@Override
 	public <T> T doReturningWork(final ReturningWork<T> work) throws HibernateException {
 		WorkExecutorVisitable<T> realWork = new WorkExecutorVisitable<T>() {
 			@Override
 			public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 				return workExecutor.executeReturningWork( work, connection );
 			}
 		};
 		return doWork( realWork );
 	}
 
 	private <T> T doWork(WorkExecutorVisitable<T> work) throws HibernateException {
 		return transactionCoordinator.getJdbcCoordinator().coordinateWork( work );
 	}
 
+	@Override
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		errorIfClosed();
 		return transactionCoordinator;
 	}
 
+	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Filter enableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void disableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object getFilterParameterValue(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterValue( filterParameterName );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type getFilterParameterType(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterType( filterParameterName );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Map getEnabledFilters() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilters();
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getFetchProfile() {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getInternalFetchProfile();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setFetchProfile(String fetchProfile) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.setInternalFetchProfile( fetchProfile );
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
+	@Override
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
+	@Override
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 	private void checkTransactionSynchStatus() {
 		pulseTransactionCoordinator();
 		delayedAfterCompletion();
 	}
 
 	private void pulseTransactionCoordinator() {
 		if ( !isClosed() ) {
 			transactionCoordinator.pulse();
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param ois The input stream from which we are being read...
 	 * @throws IOException Indicates a general IO stream exception
 	 * @throws ClassNotFoundException Indicates a class resolution issue
 	 */
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing session" );
 
 		ois.defaultReadObject();
 
 		entityNameResolver = new CoordinatingEntityNameResolver();
 
 		connectionReleaseMode = ConnectionReleaseMode.parse( ( String ) ois.readObject() );
 		autoClear = ois.readBoolean();
 		autoJoinTransactions = ois.readBoolean();
 		flushMode = FlushMode.valueOf( ( String ) ois.readObject() );
 		cacheMode = CacheMode.valueOf( ( String ) ois.readObject() );
 		flushBeforeCompletionEnabled = ois.readBoolean();
 		autoCloseSessionEnabled = ois.readBoolean();
 		interceptor = ( Interceptor ) ois.readObject();
 
 		factory = SessionFactoryImpl.deserialize( ois );
 		sessionOwner = ( SessionOwner ) ois.readObject();
 
 		transactionCoordinator = TransactionCoordinatorImpl.deserialize( ois, this );
 
 		persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		actionQueue = ActionQueue.deserialize( ois, this );
 
 		loadQueryInfluencers = (LoadQueryInfluencers) ois.readObject();
 
 		// LoadQueryInfluencers.getEnabledFilters() tries to validate each enabled
 		// filter, which will fail when called before FilterImpl.afterDeserialize( factory );
 		// Instead lookup the filter by name and then call FilterImpl.afterDeserialize( factory ).
 		for ( String filterName : loadQueryInfluencers.getEnabledFilterNames() ) {
 			((FilterImpl) loadQueryInfluencers.getEnabledFilter( filterName )).afterDeserialize( factory );
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param oos The output stream to which we are being written...
 	 * @throws IOException Indicates a general IO stream exception
 	 */
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if ( ! transactionCoordinator.getJdbcCoordinator().isReadyForSerialization() ) {
 			throw new IllegalStateException( "Cannot serialize a session while connected" );
 		}
 
 		LOG.trace( "Serializing session" );
 
 		oos.defaultWriteObject();
 
 		oos.writeObject( connectionReleaseMode.toString() );
 		oos.writeBoolean( autoClear );
 		oos.writeBoolean( autoJoinTransactions );
 		oos.writeObject( flushMode.toString() );
 		oos.writeObject( cacheMode.name() );
 		oos.writeBoolean( flushBeforeCompletionEnabled );
 		oos.writeBoolean( autoCloseSessionEnabled );
 		// we need to writeObject() on this since interceptor is user defined
 		oos.writeObject( interceptor );
 
 		factory.serialize( oos );
 		oos.writeObject( sessionOwner );
 
 		transactionCoordinator.serialize( oos );
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public TypeHelper getTypeHelper() {
 		return getSessionFactory().getTypeHelper();
 	}
 
 	@Override
 	public LobHelper getLobHelper() {
 		if ( lobHelper == null ) {
 			lobHelper = new LobHelperImpl( this );
 		}
 		return lobHelper;
 	}
 
 	private transient LobHelperImpl lobHelper;
 
 	private static class LobHelperImpl implements LobHelper {
 		private final SessionImpl session;
 
 		private LobHelperImpl(SessionImpl session) {
 			this.session = session;
 		}
 
 		@Override
 		public Blob createBlob(byte[] bytes) {
 			return lobCreator().createBlob( bytes );
 		}
 
 		private LobCreator lobCreator() {
 			// Always use NonContextualLobCreator.  If ContextualLobCreator is
 			// used both here and in WrapperOptions, 
 			return NonContextualLobCreator.INSTANCE;
 		}
 
 		@Override
 		public Blob createBlob(InputStream stream, long length) {
 			return lobCreator().createBlob( stream, length );
 		}
 
 		@Override
 		public Clob createClob(String string) {
 			return lobCreator().createClob( string );
 		}
 
 		@Override
 		public Clob createClob(Reader reader, long length) {
 			return lobCreator().createClob( reader, length );
 		}
 
 		@Override
 		public NClob createNClob(String string) {
 			return lobCreator().createNClob( string );
 		}
 
 		@Override
 		public NClob createNClob(Reader reader, long length) {
 			return lobCreator().createNClob( reader, length );
 		}
 	}
 
 	private static class SharedSessionBuilderImpl extends SessionFactoryImpl.SessionBuilderImpl implements SharedSessionBuilder {
 		private final SessionImpl session;
 		private boolean shareTransactionContext;
 
 		private SharedSessionBuilderImpl(SessionImpl session) {
 			super( session.factory );
 			this.session = session;
 			super.owner( session.sessionOwner );
 			super.tenantIdentifier( session.getTenantIdentifier() );
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			// todo : is this always true?  Or just in the case of sharing JDBC resources?
 			throw new SessionException( "Cannot redefine tenant identifier on child session" );
 		}
 
 		@Override
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return shareTransactionContext ? session.transactionCoordinator : super.getTransactionCoordinator();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor() {
 			return interceptor( session.interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder connection() {
 			this.shareTransactionContext = true;
 			return this;
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode() {
 			return connectionReleaseMode( session.connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions() {
 			return autoJoinTransactions( session.autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose() {
 			return autoClose( session.autoCloseSessionEnabled );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion() {
 			return flushBeforeCompletion( session.flushBeforeCompletionEnabled );
 		}
 
 		/**
 		 * @deprecated Use {@link #connection()} instead
 		 */
 		@Override
 		@Deprecated
 		public SharedSessionBuilder transactionContext() {
 			return connection();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor(Interceptor interceptor) {
 			return (SharedSessionBuilder) super.interceptor( interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder noInterceptor() {
 			return (SharedSessionBuilder) super.noInterceptor();
 		}
 
 		@Override
 		public SharedSessionBuilder connection(Connection connection) {
 			return (SharedSessionBuilder) super.connection( connection );
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			return (SharedSessionBuilder) super.connectionReleaseMode( connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			return (SharedSessionBuilder) super.autoJoinTransactions( autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose(boolean autoClose) {
 			return (SharedSessionBuilder) super.autoClose( autoClose );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			return (SharedSessionBuilder) super.flushBeforeCompletion( flushBeforeCompletion );
 		}
 
 		@Override
 		public SharedSessionBuilder eventListeners(SessionEventListener... listeners) {
 			super.eventListeners( listeners );
 			return this;
 		}
 
 		@Override
 		public SessionBuilder clearEventListeners() {
 			super.clearEventListeners();
 			return this;
 		}
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
+		@Override
 		public String resolveEntityName(Object entity) {
 			String entityName = interceptor.getEntityName( entity );
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			for ( EntityNameResolver resolver : factory.iterateEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			// the old-time stand-by...
 			return entity.getClass().getName();
 		}
 	}
 
 	private class LockRequestImpl implements LockRequest {
 		private final LockOptions lockOptions;
 		private LockRequestImpl(LockOptions lo) {
 			lockOptions = new LockOptions();
 			LockOptions.copy(lo, lockOptions);
 		}
 
+		@Override
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
+		@Override
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode(lockMode);
 			return this;
 		}
 
+		@Override
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
+		@Override
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut(timeout);
 			return this;
 		}
 
+		@Override
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
+		@Override
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope(scope);
 			return this;
 		}
 
+		@Override
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
+
+		@Override
 		public void lock(Object object) throws HibernateException {
 			fireLock( object, lockOptions );
 		}
 	}
 
 	private class IdentifierLoadAccessImpl implements IdentifierLoadAccess {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 
 		private IdentifierLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		private IdentifierLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private IdentifierLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		@Override
 		public final IdentifierLoadAccessImpl with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
 		public final Object getReference(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.LOAD );
 				return event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.LOAD );
 				if ( event.getResult() == null ) {
 					getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityPersister.getEntityName(), id );
 				}
 				success = true;
 				return event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 
 		@Override
 		public final Object load(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.GET );
 				return event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.GET );
 				success = true;
 				return event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 	}
 
 	private EntityPersister locateEntityPersister(String entityName) {
 		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
 		if ( entityPersister == null ) {
 			throw new HibernateException( "Unable to locate persister: " + entityName );
 		}
 		return entityPersister;
 	}
 
 	private abstract class BaseNaturalIdLoadAccessImpl  {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 		private boolean synchronizationEnabled = true;
 
 		private BaseNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 
 			if ( ! entityPersister.hasNaturalIdentifier() ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a natural id", entityPersister.getEntityName() )
 				);
 			}
 		}
 
 		private BaseNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private BaseNaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		public BaseNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		protected void synchronizationEnabled(boolean synchronizationEnabled) {
 			this.synchronizationEnabled = synchronizationEnabled;
 		}
 
 		protected final Serializable resolveNaturalId(Map<String, Object> naturalIdParameters) {
 			performAnyNeededCrossReferenceSynchronizations();
 
 			final ResolveNaturalIdEvent event =
 					new ResolveNaturalIdEvent( naturalIdParameters, entityPersister, SessionImpl.this );
 			fireResolveNaturalId( event );
 
 			if ( event.getEntityId() == PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE ) {
 				return null;
 			}
 			else {
 				return event.getEntityId();
 			}
 		}
 
 		protected void performAnyNeededCrossReferenceSynchronizations() {
 			if ( ! synchronizationEnabled ) {
 				// synchronization (this process) was disabled
 				return;
 			}
 			if ( entityPersister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// only mutable natural-ids need this processing
 				return;
 			}
 			if ( ! isTransactionInProgress() ) {
 				// not in a transaction so skip synchronization
 				return;
 			}
 
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			for ( Serializable pk : getPersistenceContext().getNaturalIdHelper().getCachedPkResolutions( entityPersister ) ) {
 				final EntityKey entityKey = generateEntityKey( pk, entityPersister );
 				final Object entity = getPersistenceContext().getEntity( entityKey );
 				final EntityEntry entry = getPersistenceContext().getEntry( entity );
 
 				if ( entry == null ) {
 					if ( debugEnabled ) {
 						LOG.debug(
 								"Cached natural-id/pk resolution linked to null EntityEntry in persistence context : "
 										+ MessageHelper.infoString( entityPersister, pk, getFactory() )
 						);
 					}
 					continue;
 				}
 
 				if ( !entry.requiresDirtyCheck( entity ) ) {
 					continue;
 				}
 
 				// MANAGED is the only status we care about here...
 				if ( entry.getStatus() != Status.MANAGED ) {
 					continue;
 				}
 
 				getPersistenceContext().getNaturalIdHelper().handleSynchronization(
 						entityPersister,
 						pk,
 						entity
 				);
 			}
 		}
 
 		protected final IdentifierLoadAccess getIdentifierLoadAccess() {
 			final IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl( entityPersister );
 			if ( this.lockOptions != null ) {
 				identifierLoadAccess.with( lockOptions );
 			}
 			return identifierLoadAccess;
 		}
 
 		protected EntityPersister entityPersister() {
 			return entityPersister;
 		}
 	}
 
 	private class NaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements NaturalIdLoadAccess {
 		private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
 
 		private NaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 		}
 
 		private NaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private NaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 		
 		@Override
 		public NaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			return (NaturalIdLoadAccessImpl) super.with( lockOptions );
 		}
 
 		@Override
 		public NaturalIdLoadAccess using(String attributeName, Object value) {
 			naturalIdParameters.put( attributeName, value );
 			return this;
 		}
 
 		@Override
 		public NaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
 		public final Object getReference() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public final Object load() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			try {
 				return this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
 		}
 	}
 
 	private class SimpleNaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements SimpleNaturalIdLoadAccess {
 		private final String naturalIdAttributeName;
 
 		private SimpleNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 
 			if ( entityPersister.getNaturalIdentifierProperties().length != 1 ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a simple natural id", entityPersister.getEntityName() )
 				);
 			}
 
 			final int naturalIdAttributePosition = entityPersister.getNaturalIdentifierProperties()[0];
 			this.naturalIdAttributeName = entityPersister.getPropertyNames()[ naturalIdAttributePosition ];
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		@Override
 		public final SimpleNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			return (SimpleNaturalIdLoadAccessImpl) super.with( lockOptions );
 		}
 		
 		private Map<String, Object> getNaturalIdParameters(Object naturalIdValue) {
 			return Collections.singletonMap( naturalIdAttributeName, naturalIdValue );
 		}
 
 		@Override
 		public SimpleNaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
 		public Object getReference(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public Object load(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
 			try {
 				return this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java
index 1cd5c0a477..0ffe14c922 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java
@@ -1,185 +1,169 @@
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
 package org.hibernate.internal;
 import java.io.Serializable;
 import java.util.Properties;
 
 import org.hibernate.TypeHelper;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 
 /**
  * Implementation of {@link org.hibernate.TypeHelper}
  *
  * @todo Do we want to cache the results of {@link #entity}, {@link #custom} and {@link #any} ?
  *
  * @author Steve Ebersole
  */
 public class TypeLocatorImpl implements TypeHelper, Serializable {
 	private final TypeResolver typeResolver;
 
 	public TypeLocatorImpl(TypeResolver typeResolver) {
 		this.typeResolver = typeResolver;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public BasicType basic(String name) {
 		return typeResolver.basic( name );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public BasicType basic(Class javaType) {
 		BasicType type = typeResolver.basic( javaType.getName() );
 		if ( type == null ) {
 			final Class variant = resolvePrimitiveOrPrimitiveWrapperVariantJavaType( javaType );
 			if ( variant != null ) {
 				type = typeResolver.basic( variant.getName() );
 			}
 		}
 		return type;
 	}
 
 	private Class resolvePrimitiveOrPrimitiveWrapperVariantJavaType(Class javaType) {
 		// boolean
 		if ( Boolean.TYPE.equals( javaType ) ) {
 			return Boolean.class;
 		}
 		if ( Boolean.class.equals( javaType ) ) {
 			return Boolean.TYPE;
 		}
 
 		// char
 		if ( Character.TYPE.equals( javaType ) ) {
 			return Character.class;
 		}
 		if ( Character.class.equals( javaType ) ) {
 			return Character.TYPE;
 		}
 
 		// byte
 		if ( Byte.TYPE.equals( javaType ) ) {
 			return Byte.class;
 		}
 		if ( Byte.class.equals( javaType ) ) {
 			return Byte.TYPE;
 		}
 
 		// short
 		if ( Short.TYPE.equals( javaType ) ) {
 			return Short.class;
 		}
 		if ( Short.class.equals( javaType ) ) {
 			return Short.TYPE;
 		}
 
 		// int
 		if ( Integer.TYPE.equals( javaType ) ) {
 			return Integer.class;
 		}
 		if ( Integer.class.equals( javaType ) ) {
 			return Integer.TYPE;
 		}
 
 		// long
 		if ( Long.TYPE.equals( javaType ) ) {
 			return Long.class;
 		}
 		if ( Long.class.equals( javaType ) ) {
 			return Long.TYPE;
 		}
 
 		// float
 		if ( Float.TYPE.equals( javaType ) ) {
 			return Float.class;
 		}
 		if ( Float.class.equals( javaType ) ) {
 			return Float.TYPE;
 		}
 
 		// double
 		if ( Double.TYPE.equals( javaType ) ) {
 			return Double.class;
 		}
 		if ( Double.class.equals( javaType ) ) {
 			return Double.TYPE;
 		}
 
 		return null;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type heuristicType(String name) {
 		return typeResolver.heuristicType( name );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type entity(Class entityClass) {
 		return entity( entityClass.getName() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type entity(String entityName) {
 		return typeResolver.getTypeFactory().manyToOne( entityName );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Type custom(Class userTypeClass) {
 		return custom( userTypeClass, null );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Type custom(Class userTypeClass, Properties parameters) {
 		if ( CompositeUserType.class.isAssignableFrom( userTypeClass ) ) {
 			return typeResolver.getTypeFactory().customComponent( userTypeClass, parameters );
 		}
 		else {
 			return typeResolver.getTypeFactory().custom( userTypeClass, parameters );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type any(Type metaType, Type identifierType) {
 		return typeResolver.getTypeFactory().any( metaType, identifierType );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ConcurrentReferenceHashMap.java b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ConcurrentReferenceHashMap.java
index 7c334f2c94..9fb8a40ea8 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ConcurrentReferenceHashMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/collections/ConcurrentReferenceHashMap.java
@@ -1,1889 +1,1942 @@
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
  */
 
 /*
  * Written by Doug Lea with assistance from members of JCP JSR-166
  * Expert Group and released to the public domain, as explained at
  * http://creativecommons.org/licenses/publicdomain
  */
 
 package org.hibernate.internal.util.collections;
 
 import java.io.IOException;
 import java.io.Serializable;
 import java.lang.ref.Reference;
 import java.lang.ref.ReferenceQueue;
 import java.lang.ref.SoftReference;
 import java.lang.ref.WeakReference;
 import java.util.AbstractCollection;
 import java.util.AbstractMap;
 import java.util.AbstractSet;
 import java.util.Collection;
 import java.util.ConcurrentModificationException;
 import java.util.EnumSet;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.NoSuchElementException;
 import java.util.Set;
 import java.util.concurrent.locks.ReentrantLock;
 
 /**
  * An advanced hash table supporting configurable garbage collection semantics
  * of keys and values, optional referential-equality, full concurrency of
  * retrievals, and adjustable expected concurrency for updates.
  *
  * This table is designed around specific advanced use-cases. If there is any
  * doubt whether this table is for you, you most likely should be using
  * {@link java.util.concurrent.ConcurrentHashMap} instead.
  *
  * This table supports strong, weak, and soft keys and values. By default keys
  * are weak, and values are strong. Such a configuration offers similar behavior
  * to {@link java.util.WeakHashMap}, entries of this table are periodically
  * removed once their corresponding keys are no longer referenced outside of
  * this table. In other words, this table will not prevent a key from being
  * discarded by the garbage collector. Once a key has been discarded by the
  * collector, the corresponding entry is no longer visible to this table;
  * however, the entry may occupy space until a future table operation decides to
  * reclaim it. For this reason, summary functions such as <tt>size</tt> and
  * <tt>isEmpty</tt> might return a value greater than the observed number of
  * entries. In order to support a high level of concurrency, stale entries are
  * only reclaimed during blocking (usually mutating) operations.
  *
  * Enabling soft keys allows entries in this table to remain until their space
  * is absolutely needed by the garbage collector. This is unlike weak keys which
  * can be reclaimed as soon as they are no longer referenced by a normal strong
  * reference. The primary use case for soft keys is a cache, which ideally
  * occupies memory that is not in use for as long as possible.
  *
  * By default, values are held using a normal strong reference. This provides
  * the commonly desired guarantee that a value will always have at least the
  * same life-span as it's key. For this reason, care should be taken to ensure
  * that a value never refers, either directly or indirectly, to its key, thereby
  * preventing reclamation. If this is unavoidable, then it is recommended to use
  * the same reference type in use for the key. However, it should be noted that
  * non-strong values may disappear before their corresponding key.
  *
  * While this table does allow the use of both strong keys and values, it is
  * recommended to use {@link java.util.concurrent.ConcurrentHashMap} for such a
  * configuration, since it is optimized for that case.
  *
  * Just like {@link java.util.concurrent.ConcurrentHashMap}, this class obeys
  * the same functional specification as {@link java.util.Hashtable}, and
  * includes versions of methods corresponding to each method of
  * <tt>Hashtable</tt>. However, even though all operations are thread-safe,
  * retrieval operations do <em>not</em> entail locking, and there is
  * <em>not</em> any support for locking the entire table in a way that
  * prevents all access. This class is fully interoperable with
  * <tt>Hashtable</tt> in programs that rely on its thread safety but not on
  * its synchronization details.
  *
  * <p>
  * Retrieval operations (including <tt>get</tt>) generally do not block, so
  * may overlap with update operations (including <tt>put</tt> and
  * <tt>remove</tt>). Retrievals reflect the results of the most recently
  * <em>completed</em> update operations holding upon their onset. For
  * aggregate operations such as <tt>putAll</tt> and <tt>clear</tt>,
  * concurrent retrievals may reflect insertion or removal of only some entries.
  * Similarly, Iterators and Enumerations return elements reflecting the state of
  * the hash table at some point at or since the creation of the
  * iterator/enumeration. They do <em>not</em> throw
  * {@link ConcurrentModificationException}. However, iterators are designed to
  * be used by only one thread at a time.
  *
  * <p>
  * The allowed concurrency among update operations is guided by the optional
  * <tt>concurrencyLevel</tt> constructor argument (default <tt>16</tt>),
  * which is used as a hint for internal sizing. The table is internally
  * partitioned to try to permit the indicated number of concurrent updates
  * without contention. Because placement in hash tables is essentially random,
  * the actual concurrency will vary. Ideally, you should choose a value to
  * accommodate as many threads as will ever concurrently modify the table. Using
  * a significantly higher value than you need can waste space and time, and a
  * significantly lower value can lead to thread contention. But overestimates
  * and underestimates within an order of magnitude do not usually have much
  * noticeable impact. A value of one is appropriate when it is known that only
  * one thread will modify and all others will only read. Also, resizing this or
  * any other kind of hash table is a relatively slow operation, so, when
  * possible, it is a good idea to provide estimates of expected table sizes in
  * constructors.
  *
  * <p>
  * This class and its views and iterators implement all of the <em>optional</em>
  * methods of the {@link Map} and {@link Iterator} interfaces.
  *
  * <p>
  * Like {@link Hashtable} but unlike {@link HashMap}, this class does
  * <em>not</em> allow <tt>null</tt> to be used as a key or value.
  *
  * <p>
  * This class is a member of the <a href="{@docRoot}/../technotes/guides/collections/index.html">
  * Java Collections Framework</a>.
  *
  * @param <K> the type of keys maintained by this map
  * @param <V> the type of mapped values
  *
  * @author Doug Lea
  * @author Jason T. Greene
  */
 public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V>
 		implements java.util.concurrent.ConcurrentMap<K, V>, Serializable {
 	private static final long serialVersionUID = 7249069246763182397L;
 
 	/*
 		 * The basic strategy is to subdivide the table among Segments,
 		 * each of which itself is a concurrently readable hash table.
 		 */
 
 	/**
 	 * An option specifying which Java reference type should be used to refer
 	 * to a key and/or value.
 	 */
 	public static enum ReferenceType {
 		/**
 		 * Indicates a normal Java strong reference should be used
 		 */
 		STRONG,
 		/**
 		 * Indicates a {@link WeakReference} should be used
 		 */
 		WEAK,
 		/**
 		 * Indicates a {@link SoftReference} should be used
 		 */
 		SOFT
 	}
 
 	;
 
 
 	public static enum Option {
 		/**
 		 * Indicates that referential-equality (== instead of .equals()) should
 		 * be used when locating keys. This offers similar behavior to {@link IdentityHashMap}
 		 */
 		IDENTITY_COMPARISONS
 	}
 
 	;
 
 	/* ---------------- Constants -------------- */
 
 	static final ReferenceType DEFAULT_KEY_TYPE = ReferenceType.WEAK;
 
 	static final ReferenceType DEFAULT_VALUE_TYPE = ReferenceType.STRONG;
 
 
 	/**
 	 * The default initial capacity for this table,
 	 * used when not otherwise specified in a constructor.
 	 */
 	static final int DEFAULT_INITIAL_CAPACITY = 16;
 
 	/**
 	 * The default load factor for this table, used when not
 	 * otherwise specified in a constructor.
 	 */
 	static final float DEFAULT_LOAD_FACTOR = 0.75f;
 
 	/**
 	 * The default concurrency level for this table, used when not
 	 * otherwise specified in a constructor.
 	 */
 	static final int DEFAULT_CONCURRENCY_LEVEL = 16;
 
 	/**
 	 * The maximum capacity, used if a higher value is implicitly
 	 * specified by either of the constructors with arguments.  MUST
 	 * be a power of two <= 1<<30 to ensure that entries are indexable
 	 * using ints.
 	 */
 	static final int MAXIMUM_CAPACITY = 1 << 30;
 
 	/**
 	 * The maximum number of segments to allow; used to bound
 	 * constructor arguments.
 	 */
 	static final int MAX_SEGMENTS = 1 << 16; // slightly conservative
 
 	/**
 	 * Number of unsynchronized retries in size and containsValue
 	 * methods before resorting to locking. This is used to avoid
 	 * unbounded retries if tables undergo continuous modification
 	 * which would make it impossible to obtain an accurate result.
 	 */
 	static final int RETRIES_BEFORE_LOCK = 2;
 
 	/* ---------------- Fields -------------- */
 
 	/**
 	 * Mask value for indexing into segments. The upper bits of a
 	 * key's hash code are used to choose the segment.
 	 */
 	final int segmentMask;
 
 	/**
 	 * Shift value for indexing within segments.
 	 */
 	final int segmentShift;
 
 	/**
 	 * The segments, each of which is a specialized hash table
 	 */
 	final Segment<K, V>[] segments;
 
 	boolean identityComparisons;
 
 	transient Set<K> keySet;
 	transient Set<Map.Entry<K, V>> entrySet;
 	transient Collection<V> values;
 
 	/* ---------------- Small Utilities -------------- */
 
 	/**
 	 * Applies a supplemental hash function to a given hashCode, which
 	 * defends against poor quality hash functions.  This is critical
 	 * because ConcurrentReferenceHashMap uses power-of-two length hash tables,
 	 * that otherwise encounter collisions for hashCodes that do not
 	 * differ in lower or upper bits.
 	 */
 	private static int hash(int h) {
 		// Spread bits to regularize both segment and index locations,
 		// using variant of single-word Wang/Jenkins hash.
 		h += ( h << 15 ) ^ 0xffffcd7d;
 		h ^= ( h >>> 10 );
 		h += ( h << 3 );
 		h ^= ( h >>> 6 );
 		h += ( h << 2 ) + ( h << 14 );
 		return h ^ ( h >>> 16 );
 	}
 
 	/**
 	 * Returns the segment that should be used for key with given hash
 	 *
 	 * @param hash the hash code for the key
 	 *
 	 * @return the segment
 	 */
 	final Segment<K, V> segmentFor(int hash) {
 		return segments[( hash >>> segmentShift ) & segmentMask];
 	}
 
 	private int hashOf(Object key) {
 		return hash(
 				identityComparisons ?
 						System.identityHashCode( key ) : key.hashCode()
 		);
 	}
 
 	/* ---------------- Inner Classes -------------- */
 
 	static interface KeyReference {
 		int keyHash();
 
 		Object keyRef();
 	}
 
 	/**
 	 * A weak-key reference which stores the key hash needed for reclamation.
 	 */
 	static final class WeakKeyReference<K> extends WeakReference<K> implements KeyReference {
 		final int hash;
 
 		WeakKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
 			super( key, refQueue );
 			this.hash = hash;
 		}
 
+		@Override
 		public final int keyHash() {
 			return hash;
 		}
 
+		@Override
 		public final Object keyRef() {
 			return this;
 		}
 	}
 
 	/**
 	 * A soft-key reference which stores the key hash needed for reclamation.
 	 */
 	static final class SoftKeyReference<K> extends SoftReference<K> implements KeyReference {
 		final int hash;
 
 		SoftKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
 			super( key, refQueue );
 			this.hash = hash;
 		}
 
+		@Override
 		public final int keyHash() {
 			return hash;
 		}
 
+		@Override
 		public final Object keyRef() {
 			return this;
 		}
 	}
 
 	static final class WeakValueReference<V> extends WeakReference<V> implements KeyReference {
 		final Object keyRef;
 		final int hash;
 
 		WeakValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
 			super( value, refQueue );
 			this.keyRef = keyRef;
 			this.hash = hash;
 		}
 
+		@Override
 		public final int keyHash() {
 			return hash;
 		}
 
+		@Override
 		public final Object keyRef() {
 			return keyRef;
 		}
 	}
 
 	static final class SoftValueReference<V> extends SoftReference<V> implements KeyReference {
 		final Object keyRef;
 		final int hash;
 
 		SoftValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
 			super( value, refQueue );
 			this.keyRef = keyRef;
 			this.hash = hash;
 		}
 
+		@Override
 		public final int keyHash() {
 			return hash;
 		}
 
+		@Override
 		public final Object keyRef() {
 			return keyRef;
 		}
 	}
 
 	/**
 	 * ConcurrentReferenceHashMap list entry. Note that this is never exported
 	 * out as a user-visible Map.Entry.
 	 *
 	 * Because the value field is volatile, not final, it is legal wrt
 	 * the Java Memory Model for an unsynchronized reader to see null
 	 * instead of initial value when read via a data race.  Although a
 	 * reordering leading to this is not likely to ever actually
 	 * occur, the Segment.readValueUnderLock method is used as a
 	 * backup in case a null (pre-initialized) value is ever seen in
 	 * an unsynchronized access method.
 	 */
 	static final class HashEntry<K, V> {
 		final Object keyRef;
 		final int hash;
 		volatile Object valueRef;
 		final HashEntry<K, V> next;
 
 		HashEntry(K key, int hash, HashEntry<K, V> next, V value,
 				  ReferenceType keyType, ReferenceType valueType,
 				  ReferenceQueue<Object> refQueue) {
 			this.hash = hash;
 			this.next = next;
 			this.keyRef = newKeyReference( key, keyType, refQueue );
 			this.valueRef = newValueReference( value, valueType, refQueue );
 		}
 
 		final Object newKeyReference(K key, ReferenceType keyType,
 									 ReferenceQueue<Object> refQueue) {
 			if ( keyType == ReferenceType.WEAK ) {
 				return new WeakKeyReference<K>( key, hash, refQueue );
 			}
 			if ( keyType == ReferenceType.SOFT ) {
 				return new SoftKeyReference<K>( key, hash, refQueue );
 			}
 
 			return key;
 		}
 
 		final Object newValueReference(V value, ReferenceType valueType,
 									   ReferenceQueue<Object> refQueue) {
 			if ( valueType == ReferenceType.WEAK ) {
 				return new WeakValueReference<V>( value, keyRef, hash, refQueue );
 			}
 			if ( valueType == ReferenceType.SOFT ) {
 				return new SoftValueReference<V>( value, keyRef, hash, refQueue );
 			}
 
 			return value;
 		}
 
 		@SuppressWarnings("unchecked")
 		final K key() {
 			if ( keyRef instanceof KeyReference ) {
 				return ( (Reference<K>) keyRef ).get();
 			}
 
 			return (K) keyRef;
 		}
 
 		final V value() {
 			return dereferenceValue( valueRef );
 		}
 
 		@SuppressWarnings("unchecked")
 		final V dereferenceValue(Object value) {
 			if ( value instanceof KeyReference ) {
 				return ( (Reference<V>) value ).get();
 			}
 
 			return (V) value;
 		}
 
 		final void setValue(V value, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
 			this.valueRef = newValueReference( value, valueType, refQueue );
 		}
 
 		@SuppressWarnings("unchecked")
 		static final <K, V> HashEntry<K, V>[] newArray(int i) {
 			return new HashEntry[i];
 		}
 	}
 
 	/**
 	 * Segments are specialized versions of hash tables.  This
 	 * subclasses from ReentrantLock opportunistically, just to
 	 * simplify some locking and avoid separate construction.
 	 */
 	static final class Segment<K, V> extends ReentrantLock implements Serializable {
 		/*
 				 * Segments maintain a table of entry lists that are ALWAYS
 				 * kept in a consistent state, so can be read without locking.
 				 * Next fields of nodes are immutable (final).  All list
 				 * additions are performed at the front of each bin. This
 				 * makes it easy to check changes, and also fast to traverse.
 				 * When nodes would otherwise be changed, new nodes are
 				 * created to replace them. This works well for hash tables
 				 * since the bin lists tend to be short. (The average length
 				 * is less than two for the default load factor threshold.)
 				 *
 				 * Read operations can thus proceed without locking, but rely
 				 * on selected uses of volatiles to ensure that completed
 				 * write operations performed by other threads are
 				 * noticed. For most purposes, the "count" field, tracking the
 				 * number of elements, serves as that volatile variable
 				 * ensuring visibility.  This is convenient because this field
 				 * needs to be read in many read operations anyway:
 				 *
 				 *   - All (unsynchronized) read operations must first read the
 				 *     "count" field, and should not look at table entries if
 				 *     it is 0.
 				 *
 				 *   - All (synchronized) write operations should write to
 				 *     the "count" field after structurally changing any bin.
 				 *     The operations must not take any action that could even
 				 *     momentarily cause a concurrent read operation to see
 				 *     inconsistent data. This is made easier by the nature of
 				 *     the read operations in Map. For example, no operation
 				 *     can reveal that the table has grown but the threshold
 				 *     has not yet been updated, so there are no atomicity
 				 *     requirements for this with respect to reads.
 				 *
 				 * As a guide, all critical volatile reads and writes to the
 				 * count field are marked in code comments.
 				 */
 
 		private static final long serialVersionUID = 2249069246763182397L;
 
 		/**
 		 * The number of elements in this segment's region.
 		 */
 		transient volatile int count;
 
 		/**
 		 * Number of updates that alter the size of the table. This is
 		 * used during bulk-read methods to make sure they see a
 		 * consistent snapshot: If modCounts change during a traversal
 		 * of segments computing size or checking containsValue, then
 		 * we might have an inconsistent view of state so (usually)
 		 * must retry.
 		 */
 		transient int modCount;
 
 		/**
 		 * The table is rehashed when its size exceeds this threshold.
 		 * (The value of this field is always <tt>(int)(capacity *
 		 * loadFactor)</tt>.)
 		 */
 		transient int threshold;
 
 		/**
 		 * The per-segment table.
 		 */
 		transient volatile HashEntry<K, V>[] table;
 
 		/**
 		 * The load factor for the hash table.  Even though this value
 		 * is same for all segments, it is replicated to avoid needing
 		 * links to outer object.
 		 *
 		 * @serial
 		 */
 		final float loadFactor;
 
 		/**
 		 * The collected weak-key reference queue for this segment.
 		 * This should be (re)initialized whenever table is assigned,
 		 */
 		transient volatile ReferenceQueue<Object> refQueue;
 
 		final ReferenceType keyType;
 
 		final ReferenceType valueType;
 
 		final boolean identityComparisons;
 
 		Segment(int initialCapacity, float lf, ReferenceType keyType,
 				ReferenceType valueType, boolean identityComparisons) {
 			loadFactor = lf;
 			this.keyType = keyType;
 			this.valueType = valueType;
 			this.identityComparisons = identityComparisons;
 			setTable( HashEntry.<K, V>newArray( initialCapacity ) );
 		}
 
 		@SuppressWarnings("unchecked")
 		static final <K, V> Segment<K, V>[] newArray(int i) {
 			return new Segment[i];
 		}
 
 		private boolean keyEq(Object src, Object dest) {
 			return identityComparisons ? src == dest : src.equals( dest );
 		}
 
 		/**
 		 * Sets table to new HashEntry array.
 		 * Call only while holding lock or in constructor.
 		 */
 		void setTable(HashEntry<K, V>[] newTable) {
 			threshold = (int) ( newTable.length * loadFactor );
 			table = newTable;
 			refQueue = new ReferenceQueue<Object>();
 		}
 
 		/**
 		 * Returns properly casted first entry of bin for given hash.
 		 */
 		HashEntry<K, V> getFirst(int hash) {
 			HashEntry<K, V>[] tab = table;
 			return tab[hash & ( tab.length - 1 )];
 		}
 
 		HashEntry<K, V> newHashEntry(K key, int hash, HashEntry<K, V> next, V value) {
 			return new HashEntry<K, V>( key, hash, next, value, keyType, valueType, refQueue );
 		}
 
 		/**
 		 * Reads value field of an entry under lock. Called if value
 		 * field ever appears to be null. This is possible only if a
 		 * compiler happens to reorder a HashEntry initialization with
 		 * its table assignment, which is legal under memory model
 		 * but is not known to ever occur.
 		 */
 		V readValueUnderLock(HashEntry<K, V> e) {
 			lock();
 			try {
 				removeStale();
 				return e.value();
 			}
 			finally {
 				unlock();
 			}
 		}
 
 		/* Specialized implementations of map methods */
 
 		V get(Object key, int hash) {
 			if ( count != 0 ) { // read-volatile
 				HashEntry<K, V> e = getFirst( hash );
 				while ( e != null ) {
 					if ( e.hash == hash && keyEq( key, e.key() ) ) {
 						Object opaque = e.valueRef;
 						if ( opaque != null ) {
 							return e.dereferenceValue( opaque );
 						}
 
 						return readValueUnderLock( e );  // recheck
 					}
 					e = e.next;
 				}
 			}
 			return null;
 		}
 
 		boolean containsKey(Object key, int hash) {
 			if ( count != 0 ) { // read-volatile
 				HashEntry<K, V> e = getFirst( hash );
 				while ( e != null ) {
 					if ( e.hash == hash && keyEq( key, e.key() ) ) {
 						return true;
 					}
 					e = e.next;
 				}
 			}
 			return false;
 		}
 
 		boolean containsValue(Object value) {
 			if ( count != 0 ) { // read-volatile
 				HashEntry<K, V>[] tab = table;
 				int len = tab.length;
 				for ( int i = 0; i < len; i++ ) {
 					for ( HashEntry<K, V> e = tab[i]; e != null; e = e.next ) {
 						Object opaque = e.valueRef;
 						V v;
 
 						if ( opaque == null ) {
 							v = readValueUnderLock( e ); // recheck
 						}
 						else {
 							v = e.dereferenceValue( opaque );
 						}
 
 						if ( value.equals( v ) ) {
 							return true;
 						}
 					}
 				}
 			}
 			return false;
 		}
 
 		boolean replace(K key, int hash, V oldValue, V newValue) {
 			lock();
 			try {
 				removeStale();
 				HashEntry<K, V> e = getFirst( hash );
 				while ( e != null && ( e.hash != hash || !keyEq( key, e.key() ) ) ) {
 					e = e.next;
 				}
 
 				boolean replaced = false;
 				if ( e != null && oldValue.equals( e.value() ) ) {
 					replaced = true;
 					e.setValue( newValue, valueType, refQueue );
 				}
 				return replaced;
 			}
 			finally {
 				unlock();
 			}
 		}
 
 		V replace(K key, int hash, V newValue) {
 			lock();
 			try {
 				removeStale();
 				HashEntry<K, V> e = getFirst( hash );
 				while ( e != null && ( e.hash != hash || !keyEq( key, e.key() ) ) ) {
 					e = e.next;
 				}
 
 				V oldValue = null;
 				if ( e != null ) {
 					oldValue = e.value();
 					e.setValue( newValue, valueType, refQueue );
 				}
 				return oldValue;
 			}
 			finally {
 				unlock();
 			}
 		}
 
 
 		V put(K key, int hash, V value, boolean onlyIfAbsent) {
 			lock();
 			try {
 				removeStale();
 				int c = count;
 				if ( c++ > threshold ) {// ensure capacity
 					int reduced = rehash();
 					if ( reduced > 0 ) {
 						// adjust from possible weak cleanups
 						count = ( c -= reduced ) - 1; // write-volatile
 					}
 				}
 
 				HashEntry<K, V>[] tab = table;
 				int index = hash & ( tab.length - 1 );
 				HashEntry<K, V> first = tab[index];
 				HashEntry<K, V> e = first;
 				while ( e != null && ( e.hash != hash || !keyEq( key, e.key() ) ) ) {
 					e = e.next;
 				}
 
 				V oldValue;
 				if ( e != null ) {
 					oldValue = e.value();
 					if ( !onlyIfAbsent ) {
 						e.setValue( value, valueType, refQueue );
 					}
 				}
 				else {
 					oldValue = null;
 					++modCount;
 					tab[index] = newHashEntry( key, hash, first, value );
 					count = c; // write-volatile
 				}
 				return oldValue;
 			}
 			finally {
 				unlock();
 			}
 		}
 
 		int rehash() {
 			HashEntry<K, V>[] oldTable = table;
 			int oldCapacity = oldTable.length;
 			if ( oldCapacity >= MAXIMUM_CAPACITY ) {
 				return 0;
 			}
 
 			/*
 						 * Reclassify nodes in each list to new Map.  Because we are
 						 * using power-of-two expansion, the elements from each bin
 						 * must either stay at same index, or move with a power of two
 						 * offset. We eliminate unnecessary node creation by catching
 						 * cases where old nodes can be reused because their next
 						 * fields won't change. Statistically, at the default
 						 * threshold, only about one-sixth of them need cloning when
 						 * a table doubles. The nodes they replace will be garbage
 						 * collectable as soon as they are no longer referenced by any
 						 * reader thread that may be in the midst of traversing table
 						 * right now.
 						 */
 
 			HashEntry<K, V>[] newTable = HashEntry.newArray( oldCapacity << 1 );
 			threshold = (int) ( newTable.length * loadFactor );
 			int sizeMask = newTable.length - 1;
 			int reduce = 0;
 			for ( int i = 0; i < oldCapacity; i++ ) {
 				// We need to guarantee that any existing reads of old Map can
 				//  proceed. So we cannot yet null out each bin.
 				HashEntry<K, V> e = oldTable[i];
 
 				if ( e != null ) {
 					HashEntry<K, V> next = e.next;
 					int idx = e.hash & sizeMask;
 
 					//  Single node on list
 					if ( next == null ) {
 						newTable[idx] = e;
 					}
 
 					else {
 						// Reuse trailing consecutive sequence at same slot
 						HashEntry<K, V> lastRun = e;
 						int lastIdx = idx;
 						for ( HashEntry<K, V> last = next;
 							  last != null;
 							  last = last.next ) {
 							int k = last.hash & sizeMask;
 							if ( k != lastIdx ) {
 								lastIdx = k;
 								lastRun = last;
 							}
 						}
 						newTable[lastIdx] = lastRun;
 						// Clone all remaining nodes
 						for ( HashEntry<K, V> p = e; p != lastRun; p = p.next ) {
 							// Skip GC'd weak refs
 							K key = p.key();
 							if ( key == null ) {
 								reduce++;
 								continue;
 							}
 							int k = p.hash & sizeMask;
 							HashEntry<K, V> n = newTable[k];
 							newTable[k] = newHashEntry( key, p.hash, n, p.value() );
 						}
 					}
 				}
 			}
 			table = newTable;
 			return reduce;
 		}
 
 		/**
 		 * Remove; match on key only if value null, else match both.
 		 */
 		V remove(Object key, int hash, Object value, boolean refRemove) {
 			lock();
 			try {
 				if ( !refRemove ) {
 					removeStale();
 				}
 				int c = count - 1;
 				HashEntry<K, V>[] tab = table;
 				int index = hash & ( tab.length - 1 );
 				HashEntry<K, V> first = tab[index];
 				HashEntry<K, V> e = first;
 				// a ref remove operation compares the Reference instance
 				while ( e != null && key != e.keyRef
 						&& ( refRemove || hash != e.hash || !keyEq( key, e.key() ) ) ) {
 					e = e.next;
 				}
 
 				V oldValue = null;
 				if ( e != null ) {
 					V v = e.value();
 					if ( value == null || value.equals( v ) ) {
 						oldValue = v;
 						// All entries following removed node can stay
 						// in list, but all preceding ones need to be
 						// cloned.
 						++modCount;
 						HashEntry<K, V> newFirst = e.next;
 						for ( HashEntry<K, V> p = first; p != e; p = p.next ) {
 							K pKey = p.key();
 							if ( pKey == null ) { // Skip GC'd keys
 								c--;
 								continue;
 							}
 
 							newFirst = newHashEntry( pKey, p.hash, newFirst, p.value() );
 						}
 						tab[index] = newFirst;
 						count = c; // write-volatile
 					}
 				}
 				return oldValue;
 			}
 			finally {
 				unlock();
 			}
 		}
 
 		final void removeStale() {
 			KeyReference ref;
 			while ( ( ref = (KeyReference) refQueue.poll() ) != null ) {
 				remove( ref.keyRef(), ref.keyHash(), null, true );
 			}
 		}
 
 		void clear() {
 			if ( count != 0 ) {
 				lock();
 				try {
 					HashEntry<K, V>[] tab = table;
 					for ( int i = 0; i < tab.length; i++ ) {
 						tab[i] = null;
 					}
 					++modCount;
 					// replace the reference queue to avoid unnecessary stale cleanups
 					refQueue = new ReferenceQueue<Object>();
 					count = 0; // write-volatile
 				}
 				finally {
 					unlock();
 				}
 			}
 		}
 	}
 
 
 	/* ---------------- Public operations -------------- */
 
 	/**
 	 * Creates a new, empty map with the specified initial
 	 * capacity, reference types, load factor and concurrency level.
 	 *
 	 * Behavioral changing options such as {@link Option#IDENTITY_COMPARISONS}
 	 * can also be specified.
 	 *
 	 * @param initialCapacity the initial capacity. The implementation
 	 * performs internal sizing to accommodate this many elements.
 	 * @param loadFactor the load factor threshold, used to control resizing.
 	 * Resizing may be performed when the average number of elements per
 	 * bin exceeds this threshold.
 	 * @param concurrencyLevel the estimated number of concurrently
 	 * updating threads. The implementation performs internal sizing
 	 * to try to accommodate this many threads.
 	 * @param keyType the reference type to use for keys
 	 * @param valueType the reference type to use for values
 	 * @param options the behavioral options
 	 *
 	 * @throws IllegalArgumentException if the initial capacity is
 	 * negative or the load factor or concurrencyLevel are
 	 * nonpositive.
 	 */
 	public ConcurrentReferenceHashMap(int initialCapacity,
 									  float loadFactor, int concurrencyLevel,
 									  ReferenceType keyType, ReferenceType valueType,
 									  EnumSet<Option> options) {
 		if ( !( loadFactor > 0 ) || initialCapacity < 0 || concurrencyLevel <= 0 ) {
 			throw new IllegalArgumentException();
 		}
 
 		if ( concurrencyLevel > MAX_SEGMENTS ) {
 			concurrencyLevel = MAX_SEGMENTS;
 		}
 
 		// Find power-of-two sizes best matching arguments
 		int sshift = 0;
 		int ssize = 1;
 		while ( ssize < concurrencyLevel ) {
 			++sshift;
 			ssize <<= 1;
 		}
 		segmentShift = 32 - sshift;
 		segmentMask = ssize - 1;
 		this.segments = Segment.newArray( ssize );
 
 		if ( initialCapacity > MAXIMUM_CAPACITY ) {
 			initialCapacity = MAXIMUM_CAPACITY;
 		}
 		int c = initialCapacity / ssize;
 		if ( c * ssize < initialCapacity ) {
 			++c;
 		}
 		int cap = 1;
 		while ( cap < c ) {
 			cap <<= 1;
 		}
 
 		identityComparisons = options != null && options.contains( Option.IDENTITY_COMPARISONS );
 
 		for ( int i = 0; i < this.segments.length; ++i ) {
 			this.segments[i] = new Segment<K, V>(
 					cap, loadFactor,
 					keyType, valueType, identityComparisons
 			);
 		}
 	}
 
 	/**
 	 * Creates a new, empty map with the specified initial
 	 * capacity, load factor and concurrency level.
 	 *
 	 * @param initialCapacity the initial capacity. The implementation
 	 * performs internal sizing to accommodate this many elements.
 	 * @param loadFactor the load factor threshold, used to control resizing.
 	 * Resizing may be performed when the average number of elements per
 	 * bin exceeds this threshold.
 	 * @param concurrencyLevel the estimated number of concurrently
 	 * updating threads. The implementation performs internal sizing
 	 * to try to accommodate this many threads.
 	 *
 	 * @throws IllegalArgumentException if the initial capacity is
 	 * negative or the load factor or concurrencyLevel are
 	 * nonpositive.
 	 */
 	public ConcurrentReferenceHashMap(int initialCapacity,
 									  float loadFactor, int concurrencyLevel) {
 		this(
 				initialCapacity, loadFactor, concurrencyLevel,
 				DEFAULT_KEY_TYPE, DEFAULT_VALUE_TYPE, null
 		);
 	}
 
 	/**
 	 * Creates a new, empty map with the specified initial capacity
 	 * and load factor and with the default reference types (weak keys,
 	 * strong values), and concurrencyLevel (16).
 	 *
 	 * @param initialCapacity The implementation performs internal
 	 * sizing to accommodate this many elements.
 	 * @param loadFactor the load factor threshold, used to control resizing.
 	 * Resizing may be performed when the average number of elements per
 	 * bin exceeds this threshold.
 	 *
 	 * @throws IllegalArgumentException if the initial capacity of
 	 * elements is negative or the load factor is nonpositive
 	 * @since 1.6
 	 */
 	public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
 		this( initialCapacity, loadFactor, DEFAULT_CONCURRENCY_LEVEL );
 	}
 
 
 	/**
 	 * Creates a new, empty map with the specified initial capacity,
 	 * reference types and with default load factor (0.75) and concurrencyLevel (16).
 	 *
 	 * @param initialCapacity the initial capacity. The implementation
 	 * performs internal sizing to accommodate this many elements.
 	 * @param keyType the reference type to use for keys
 	 * @param valueType the reference type to use for values
 	 *
 	 * @throws IllegalArgumentException if the initial capacity of
 	 * elements is negative.
 	 */
 	public ConcurrentReferenceHashMap(int initialCapacity,
 									  ReferenceType keyType, ReferenceType valueType) {
 		this(
 				initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL,
 				keyType, valueType, null
 		);
 	}
 
 	/**
 	 * Creates a new, empty map with the specified initial capacity,
 	 * and with default reference types (weak keys, strong values),
 	 * load factor (0.75) and concurrencyLevel (16).
 	 *
 	 * @param initialCapacity the initial capacity. The implementation
 	 * performs internal sizing to accommodate this many elements.
 	 *
 	 * @throws IllegalArgumentException if the initial capacity of
 	 * elements is negative.
 	 */
 	public ConcurrentReferenceHashMap(int initialCapacity) {
 		this( initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL );
 	}
 
 	/**
 	 * Creates a new, empty map with a default initial capacity (16),
 	 * reference types (weak keys, strong values), default
 	 * load factor (0.75) and concurrencyLevel (16).
 	 */
 	public ConcurrentReferenceHashMap() {
 		this( DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL );
 	}
 
 	/**
 	 * Creates a new map with the same mappings as the given map.
 	 * The map is created with a capacity of 1.5 times the number
 	 * of mappings in the given map or 16 (whichever is greater),
 	 * and a default load factor (0.75) and concurrencyLevel (16).
 	 *
 	 * @param m the map
 	 */
 	public ConcurrentReferenceHashMap(Map<? extends K, ? extends V> m) {
 		this(
 				Math.max(
 						(int) ( m.size() / DEFAULT_LOAD_FACTOR ) + 1,
 						DEFAULT_INITIAL_CAPACITY
 				),
 				DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL
 		);
 		putAll( m );
 	}
 
 	/**
 	 * Returns <tt>true</tt> if this map contains no key-value mappings.
 	 *
 	 * @return <tt>true</tt> if this map contains no key-value mappings
 	 */
+	@Override
 	public boolean isEmpty() {
 		final Segment<K, V>[] segments = this.segments;
 		/*
 				 * We keep track of per-segment modCounts to avoid ABA
 				 * problems in which an element in one segment was added and
 				 * in another removed during traversal, in which case the
 				 * table was never actually empty at any point. Note the
 				 * similar use of modCounts in the size() and containsValue()
 				 * methods, which are the only other methods also susceptible
 				 * to ABA problems.
 				 */
 		int[] mc = new int[segments.length];
 		int mcsum = 0;
 		for ( int i = 0; i < segments.length; ++i ) {
 			if ( segments[i].count != 0 ) {
 				return false;
 			}
 			else {
 				mcsum += mc[i] = segments[i].modCount;
 			}
 		}
 		// If mcsum happens to be zero, then we know we got a snapshot
 		// before any modifications at all were made.  This is
 		// probably common enough to bother tracking.
 		if ( mcsum != 0 ) {
 			for ( int i = 0; i < segments.length; ++i ) {
 				if ( segments[i].count != 0 ||
 						mc[i] != segments[i].modCount ) {
 					return false;
 				}
 			}
 		}
 		return true;
 	}
 
 	/**
 	 * Returns the number of key-value mappings in this map.  If the
 	 * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
 	 * <tt>Integer.MAX_VALUE</tt>.
 	 *
 	 * @return the number of key-value mappings in this map
 	 */
+	@Override
 	public int size() {
 		final Segment<K, V>[] segments = this.segments;
 		long sum = 0;
 		long check = 0;
 		int[] mc = new int[segments.length];
 		// Try a few times to get accurate count. On failure due to
 		// continuous async changes in table, resort to locking.
 		for ( int k = 0; k < RETRIES_BEFORE_LOCK; ++k ) {
 			check = 0;
 			sum = 0;
 			int mcsum = 0;
 			for ( int i = 0; i < segments.length; ++i ) {
 				sum += segments[i].count;
 				mcsum += mc[i] = segments[i].modCount;
 			}
 			if ( mcsum != 0 ) {
 				for ( int i = 0; i < segments.length; ++i ) {
 					check += segments[i].count;
 					if ( mc[i] != segments[i].modCount ) {
 						check = -1; // force retry
 						break;
 					}
 				}
 			}
 			if ( check == sum ) {
 				break;
 			}
 		}
 		if ( check != sum ) { // Resort to locking all segments
 			sum = 0;
 			for ( int i = 0; i < segments.length; ++i ) {
 				segments[i].lock();
 			}
 			for ( int i = 0; i < segments.length; ++i ) {
 				sum += segments[i].count;
 			}
 			for ( int i = 0; i < segments.length; ++i ) {
 				segments[i].unlock();
 			}
 		}
 		if ( sum > Integer.MAX_VALUE ) {
 			return Integer.MAX_VALUE;
 		}
 		else {
 			return (int) sum;
 		}
 	}
 
 	/**
 	 * Returns the value to which the specified key is mapped,
 	 * or {@code null} if this map contains no mapping for the key.
 	 *
 	 * <p>More formally, if this map contains a mapping from a key
 	 * {@code k} to a value {@code v} such that {@code key.equals(k)},
 	 * then this method returns {@code v}; otherwise it returns
 	 * {@code null}.  (There can be at most one such mapping.)
 	 *
 	 * @throws NullPointerException if the specified key is null
 	 */
+	@Override
 	public V get(Object key) {
 		if ( key == null ) {
 			return null;
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).get( key, hash );
 	}
 
 	/**
 	 * Tests if the specified object is a key in this table.
 	 *
 	 * @param key possible key
 	 *
 	 * @return <tt>true</tt> if and only if the specified object
 	 *         is a key in this table, as determined by the
 	 *         <tt>equals</tt> method; <tt>false</tt> otherwise.
 	 *
 	 * @throws NullPointerException if the specified key is null
 	 */
+	@Override
 	public boolean containsKey(Object key) {
 		if ( key == null ) {
 			return false;
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).containsKey( key, hash );
 	}
 
 	/**
 	 * Returns <tt>true</tt> if this map maps one or more keys to the
 	 * specified value. Note: This method requires a full internal
 	 * traversal of the hash table, and so is much slower than
 	 * method <tt>containsKey</tt>.
 	 *
 	 * @param value value whose presence in this map is to be tested
 	 *
 	 * @return <tt>true</tt> if this map maps one or more keys to the
 	 *         specified value
 	 */
+	@Override
 	public boolean containsValue(Object value) {
 		if ( value == null ) {
 			return false;
 		}
 
 		// See explanation of modCount use above
 
 		final Segment<K, V>[] segments = this.segments;
 		int[] mc = new int[segments.length];
 
 		// Try a few times without locking
 		for ( int k = 0; k < RETRIES_BEFORE_LOCK; ++k ) {
 			int sum = 0;
 			int mcsum = 0;
 			for ( int i = 0; i < segments.length; ++i ) {
 				int c = segments[i].count;
 				mcsum += mc[i] = segments[i].modCount;
 				if ( segments[i].containsValue( value ) ) {
 					return true;
 				}
 			}
 			boolean cleanSweep = true;
 			if ( mcsum != 0 ) {
 				for ( int i = 0; i < segments.length; ++i ) {
 					int c = segments[i].count;
 					if ( mc[i] != segments[i].modCount ) {
 						cleanSweep = false;
 						break;
 					}
 				}
 			}
 			if ( cleanSweep ) {
 				return false;
 			}
 		}
 		// Resort to locking all segments
 		for ( int i = 0; i < segments.length; ++i ) {
 			segments[i].lock();
 		}
 		boolean found = false;
 		try {
 			for ( int i = 0; i < segments.length; ++i ) {
 				if ( segments[i].containsValue( value ) ) {
 					found = true;
 					break;
 				}
 			}
 		}
 		finally {
 			for ( int i = 0; i < segments.length; ++i ) {
 				segments[i].unlock();
 			}
 		}
 		return found;
 	}
 
 	/**
 	 * Legacy method testing if some key maps into the specified value
 	 * in this table.  This method is identical in functionality to
 	 * {@link #containsValue}, and exists solely to ensure
 	 * full compatibility with class {@link java.util.Hashtable},
 	 * which supported this method prior to introduction of the
 	 * Java Collections framework.
 	 *
 	 * @param value a value to search for
 	 *
 	 * @return <tt>true</tt> if and only if some key maps to the
 	 *         <tt>value</tt> argument in this table as
 	 *         determined by the <tt>equals</tt> method;
 	 *         <tt>false</tt> otherwise
 	 *
 	 * @throws NullPointerException if the specified value is null
 	 */
 	public boolean contains(Object value) {
 		return containsValue( value );
 	}
 
 	/**
 	 * Maps the specified key to the specified value in this table.
 	 * Neither the key nor the value can be null.
 	 *
 	 * <p> The value can be retrieved by calling the <tt>get</tt> method
 	 * with a key that is equal to the original key.
 	 *
 	 * @param key key with which the specified value is to be associated
 	 * @param value value to be associated with the specified key
 	 *
 	 * @return the previous value associated with <tt>key</tt>, or
 	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>
 	 *
 	 * @throws NullPointerException if the specified key or value is null
 	 */
+	@Override
 	public V put(K key, V value) {
 		if ( key == null || value == null ) {
 			return null;
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).put( key, hash, value, false );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @return the previous value associated with the specified key,
 	 *         or <tt>null</tt> if there was no mapping for the key
 	 *
 	 * @throws NullPointerException if the specified key or value is null
 	 */
+	@Override
 	public V putIfAbsent(K key, V value) {
 		if ( key == null || value == null ) {
 			return null;
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).put( key, hash, value, true );
 	}
 
 	/**
 	 * Copies all of the mappings from the specified map to this one.
 	 * These mappings replace any mappings that this map had for any of the
 	 * keys currently in the specified map.
 	 *
 	 * @param m mappings to be stored in this map
 	 */
+	@Override
 	public void putAll(Map<? extends K, ? extends V> m) {
 		for ( Map.Entry<? extends K, ? extends V> e : m.entrySet() ) {
 			put( e.getKey(), e.getValue() );
 		}
 	}
 
 	/**
 	 * Removes the key (and its corresponding value) from this map.
 	 * This method does nothing if the key is not in the map.
 	 *
 	 * @param key the key that needs to be removed
 	 *
 	 * @return the previous value associated with <tt>key</tt>, or
 	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>
 	 *
 	 * @throws NullPointerException if the specified key is null
 	 */
+	@Override
 	public V remove(Object key) {
 		if ( key == null ) {
 			return null;
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).remove( key, hash, null, false );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @throws NullPointerException if the specified key is null
 	 */
+	@Override
 	public boolean remove(Object key, Object value) {
 		if ( key == null || value == null ) {
 			return false;
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).remove( key, hash, value, false ) != null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @throws NullPointerException if any of the arguments are null
 	 */
+	@Override
 	public boolean replace(K key, V oldValue, V newValue) {
 		if ( key == null || oldValue == null || newValue == null ) {
 			throw new NullPointerException();
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).replace( key, hash, oldValue, newValue );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @return the previous value associated with the specified key,
 	 *         or <tt>null</tt> if there was no mapping for the key
 	 *
 	 * @throws NullPointerException if the specified key or value is null
 	 */
+	@Override
 	public V replace(K key, V value) {
 		if ( key == null || value == null ) {
 			return null;
 		}
 		int hash = hashOf( key );
 		return segmentFor( hash ).replace( key, hash, value );
 	}
 
 	/**
 	 * Removes all of the mappings from this map.
 	 */
+	@Override
 	public void clear() {
 		for ( int i = 0; i < segments.length; ++i ) {
 			segments[i].clear();
 		}
 	}
 
 	/**
 	 * Removes any stale entries whose keys have been finalized. Use of this
 	 * method is normally not necessary since stale entries are automatically
 	 * removed lazily, when blocking operations are required. However, there
 	 * are some cases where this operation should be performed eagerly, such
 	 * as cleaning up old references to a ClassLoader in a multi-classloader
 	 * environment.
 	 *
 	 * Note: this method will acquire locks, one at a time, across all segments
 	 * of this table, so if it is to be used, it should be used sparingly.
 	 */
 	public void purgeStaleEntries() {
 		for ( int i = 0; i < segments.length; ++i ) {
 			segments[i].removeStale();
 		}
 	}
 
 
 	/**
 	 * Returns a {@link Set} view of the keys contained in this map.
 	 * The set is backed by the map, so changes to the map are
 	 * reflected in the set, and vice-versa.  The set supports element
 	 * removal, which removes the corresponding mapping from this map,
 	 * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
 	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
 	 * operations.  It does not support the <tt>add</tt> or
 	 * <tt>addAll</tt> operations.
 	 *
 	 * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
 	 * that will never throw {@link ConcurrentModificationException},
 	 * and guarantees to traverse elements as they existed upon
 	 * construction of the iterator, and may (but is not guaranteed to)
 	 * reflect any modifications subsequent to construction.
 	 */
+	@Override
 	public Set<K> keySet() {
 		Set<K> ks = keySet;
 		return ( ks != null ) ? ks : ( keySet = new KeySet() );
 	}
 
 	/**
 	 * Returns a {@link Collection} view of the values contained in this map.
 	 * The collection is backed by the map, so changes to the map are
 	 * reflected in the collection, and vice-versa.  The collection
 	 * supports element removal, which removes the corresponding
 	 * mapping from this map, via the <tt>Iterator.remove</tt>,
 	 * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
 	 * <tt>retainAll</tt>, and <tt>clear</tt> operations.  It does not
 	 * support the <tt>add</tt> or <tt>addAll</tt> operations.
 	 *
 	 * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
 	 * that will never throw {@link ConcurrentModificationException},
 	 * and guarantees to traverse elements as they existed upon
 	 * construction of the iterator, and may (but is not guaranteed to)
 	 * reflect any modifications subsequent to construction.
 	 */
+	@Override
 	public Collection<V> values() {
 		Collection<V> vs = values;
 		return ( vs != null ) ? vs : ( values = new Values() );
 	}
 
 	/**
 	 * Returns a {@link Set} view of the mappings contained in this map.
 	 * The set is backed by the map, so changes to the map are
 	 * reflected in the set, and vice-versa.  The set supports element
 	 * removal, which removes the corresponding mapping from the map,
 	 * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
 	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
 	 * operations.  It does not support the <tt>add</tt> or
 	 * <tt>addAll</tt> operations.
 	 *
 	 * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
 	 * that will never throw {@link ConcurrentModificationException},
 	 * and guarantees to traverse elements as they existed upon
 	 * construction of the iterator, and may (but is not guaranteed to)
 	 * reflect any modifications subsequent to construction.
 	 */
+	@Override
 	public Set<Map.Entry<K, V>> entrySet() {
 		Set<Map.Entry<K, V>> es = entrySet;
 		return ( es != null ) ? es : ( entrySet = new EntrySet() );
 	}
 
 	/**
 	 * Returns an enumeration of the keys in this table.
 	 *
 	 * @return an enumeration of the keys in this table
 	 *
 	 * @see #keySet()
 	 */
 	public Enumeration<K> keys() {
 		return new KeyIterator();
 	}
 
 	/**
 	 * Returns an enumeration of the values in this table.
 	 *
 	 * @return an enumeration of the values in this table
 	 *
 	 * @see #values()
 	 */
 	public Enumeration<V> elements() {
 		return new ValueIterator();
 	}
 
 	/* ---------------- Iterator Support -------------- */
 
 	abstract class HashIterator {
 		int nextSegmentIndex;
 		int nextTableIndex;
 		HashEntry<K, V>[] currentTable;
 		HashEntry<K, V> nextEntry;
 		HashEntry<K, V> lastReturned;
 		K currentKey; // Strong reference to weak key (prevents gc)
 
 		HashIterator() {
 			nextSegmentIndex = segments.length - 1;
 			nextTableIndex = -1;
 			advance();
 		}
 
 		public boolean hasMoreElements() {
 			return hasNext();
 		}
 
 		final void advance() {
 			if ( nextEntry != null && ( nextEntry = nextEntry.next ) != null ) {
 				return;
 			}
 
 			while ( nextTableIndex >= 0 ) {
 				if ( ( nextEntry = currentTable[nextTableIndex--] ) != null ) {
 					return;
 				}
 			}
 
 			while ( nextSegmentIndex >= 0 ) {
 				Segment<K, V> seg = segments[nextSegmentIndex--];
 				if ( seg.count != 0 ) {
 					currentTable = seg.table;
 					for ( int j = currentTable.length - 1; j >= 0; --j ) {
 						if ( ( nextEntry = currentTable[j] ) != null ) {
 							nextTableIndex = j - 1;
 							return;
 						}
 					}
 				}
 			}
 		}
 
 		public boolean hasNext() {
 			while ( nextEntry != null ) {
 				if ( nextEntry.key() != null ) {
 					return true;
 				}
 				advance();
 			}
 
 			return false;
 		}
 
 		HashEntry<K, V> nextEntry() {
 			do {
 				if ( nextEntry == null ) {
 					throw new NoSuchElementException();
 				}
 
 				lastReturned = nextEntry;
 				currentKey = lastReturned.key();
 				advance();
 			} while ( currentKey == null ); // Skip GC'd keys
 
 			return lastReturned;
 		}
 
 		public void remove() {
 			if ( lastReturned == null ) {
 				throw new IllegalStateException();
 			}
 			ConcurrentReferenceHashMap.this.remove( currentKey );
 			lastReturned = null;
 		}
 	}
 
 	final class KeyIterator
 			extends HashIterator
 			implements Iterator<K>, Enumeration<K> {
+		@Override
 		public K next() {
 			return super.nextEntry().key();
 		}
 
+		@Override
 		public K nextElement() {
 			return super.nextEntry().key();
 		}
 	}
 
 	final class ValueIterator
 			extends HashIterator
 			implements Iterator<V>, Enumeration<V> {
+		@Override
 		public V next() {
 			return super.nextEntry().value();
 		}
 
+		@Override
 		public V nextElement() {
 			return super.nextEntry().value();
 		}
 	}
 
 	/*
 		  * This class is needed for JDK5 compatibility.
 		  */
 	static class SimpleEntry<K, V> implements Entry<K, V>,
 			java.io.Serializable {
 		private static final long serialVersionUID = -8499721149061103585L;
 
 		private final K key;
 		private V value;
 
 		public SimpleEntry(K key, V value) {
 			this.key = key;
 			this.value = value;
 		}
 
 		public SimpleEntry(Entry<? extends K, ? extends V> entry) {
 			this.key = entry.getKey();
 			this.value = entry.getValue();
 		}
 
+		@Override
 		public K getKey() {
 			return key;
 		}
 
+		@Override
 		public V getValue() {
 			return value;
 		}
 
+		@Override
 		public V setValue(V value) {
 			V oldValue = this.value;
 			this.value = value;
 			return oldValue;
 		}
 
+		@Override
 		public boolean equals(Object o) {
 			if ( !( o instanceof Map.Entry ) ) {
 				return false;
 			}
 			@SuppressWarnings("unchecked")
 			Map.Entry e = (Map.Entry) o;
 			return eq( key, e.getKey() ) && eq( value, e.getValue() );
 		}
 
+		@Override
 		public int hashCode() {
 			return ( key == null ? 0 : key.hashCode() )
 					^ ( value == null ? 0 : value.hashCode() );
 		}
 
+		@Override
 		public String toString() {
 			return key + "=" + value;
 		}
 
 		private static boolean eq(Object o1, Object o2) {
 			return o1 == null ? o2 == null : o1.equals( o2 );
 		}
 	}
 
 
 	/**
 	 * Custom Entry class used by EntryIterator.next(), that relays setValue
 	 * changes to the underlying map.
 	 */
 	final class WriteThroughEntry extends SimpleEntry<K, V> {
 		private static final long serialVersionUID = -7900634345345313646L;
 
 		WriteThroughEntry(K k, V v) {
 			super( k, v );
 		}
 
 		/**
 		 * Set our entry's value and write through to the map. The
 		 * value to return is somewhat arbitrary here. Since a
 		 * WriteThroughEntry does not necessarily track asynchronous
 		 * changes, the most recent "previous" value could be
 		 * different from what we return (or could even have been
 		 * removed in which case the put will re-establish). We do not
 		 * and cannot guarantee more.
 		 */
+		@Override
 		public V setValue(V value) {
 			if ( value == null ) {
 				throw new NullPointerException();
 			}
 			V v = super.setValue( value );
 			ConcurrentReferenceHashMap.this.put( getKey(), value );
 			return v;
 		}
 	}
 
 	final class EntryIterator
 			extends HashIterator
 			implements Iterator<Entry<K, V>> {
+		@Override
 		public Map.Entry<K, V> next() {
 			HashEntry<K, V> e = super.nextEntry();
 			return new WriteThroughEntry( e.key(), e.value() );
 		}
 	}
 
 	final class KeySet extends AbstractSet<K> {
+		@Override
 		public Iterator<K> iterator() {
 			return new KeyIterator();
 		}
 
+		@Override
 		public int size() {
 			return ConcurrentReferenceHashMap.this.size();
 		}
 
+		@Override
 		public boolean isEmpty() {
 			return ConcurrentReferenceHashMap.this.isEmpty();
 		}
 
+		@Override
 		public boolean contains(Object o) {
 			return ConcurrentReferenceHashMap.this.containsKey( o );
 		}
 
+		@Override
 		public boolean remove(Object o) {
 			return ConcurrentReferenceHashMap.this.remove( o ) != null;
 		}
 
+		@Override
 		public void clear() {
 			ConcurrentReferenceHashMap.this.clear();
 		}
 	}
 
 	final class Values extends AbstractCollection<V> {
+		@Override
 		public Iterator<V> iterator() {
 			return new ValueIterator();
 		}
 
+		@Override
 		public int size() {
 			return ConcurrentReferenceHashMap.this.size();
 		}
 
+		@Override
 		public boolean isEmpty() {
 			return ConcurrentReferenceHashMap.this.isEmpty();
 		}
 
+		@Override
 		public boolean contains(Object o) {
 			return ConcurrentReferenceHashMap.this.containsValue( o );
 		}
 
+		@Override
 		public void clear() {
 			ConcurrentReferenceHashMap.this.clear();
 		}
 	}
 
 	final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
+		@Override
 		public Iterator<Map.Entry<K, V>> iterator() {
 			return new EntryIterator();
 		}
 
+		@Override
 		public boolean contains(Object o) {
 			if ( !( o instanceof Map.Entry ) ) {
 				return false;
 			}
 			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
 			V v = ConcurrentReferenceHashMap.this.get( e.getKey() );
 			return v != null && v.equals( e.getValue() );
 		}
 
+		@Override
 		public boolean remove(Object o) {
 			if ( !( o instanceof Map.Entry ) ) {
 				return false;
 			}
 			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
 			return ConcurrentReferenceHashMap.this.remove( e.getKey(), e.getValue() );
 		}
 
+		@Override
 		public int size() {
 			return ConcurrentReferenceHashMap.this.size();
 		}
 
+		@Override
 		public boolean isEmpty() {
 			return ConcurrentReferenceHashMap.this.isEmpty();
 		}
 
+		@Override
 		public void clear() {
 			ConcurrentReferenceHashMap.this.clear();
 		}
 	}
 
 	/* ---------------- Serialization Support -------------- */
 
 	/**
 	 * Save the state of the <tt>ConcurrentReferenceHashMap</tt> instance to a
 	 * stream (i.e., serialize it).
 	 *
 	 * @param s the stream
 	 *
 	 * @serialData the key (Object) and value (Object)
 	 * for each key-value mapping, followed by a null pair.
 	 * The key-value mappings are emitted in no particular order.
 	 */
 	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
 		s.defaultWriteObject();
 
 		for ( int k = 0; k < segments.length; ++k ) {
 			Segment<K, V> seg = segments[k];
 			seg.lock();
 			try {
 				HashEntry<K, V>[] tab = seg.table;
 				for ( int i = 0; i < tab.length; ++i ) {
 					for ( HashEntry<K, V> e = tab[i]; e != null; e = e.next ) {
 						K key = e.key();
 						if ( key == null ) {
 							// Skip GC'd keys
 							continue;
 						}
 
 						s.writeObject( key );
 						s.writeObject( e.value() );
 					}
 				}
 			}
 			finally {
 				seg.unlock();
 			}
 		}
 		s.writeObject( null );
 		s.writeObject( null );
 	}
 
 	/**
 	 * Reconstitute the <tt>ConcurrentReferenceHashMap</tt> instance from a
 	 * stream (i.e., deserialize it).
 	 *
 	 * @param s the stream
 	 */
 	@SuppressWarnings("unchecked")
 	private void readObject(java.io.ObjectInputStream s)
 			throws IOException, ClassNotFoundException {
 		s.defaultReadObject();
 
 		// Initialize each segment to be minimally sized, and let grow.
 		for ( int i = 0; i < segments.length; ++i ) {
 			segments[i].setTable( new HashEntry[1] );
 		}
 
 		// Read the keys and values, and put the mappings in the table
 		for (; ; ) {
 			K key = (K) s.readObject();
 			V value = (V) s.readObject();
 			if ( key == null ) {
 				break;
 			}
 			put( key, value );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/OriginImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/OriginImpl.java
index 8f58fcc095..5bd66025cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/OriginImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/OriginImpl.java
@@ -1,55 +1,51 @@
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
 package org.hibernate.internal.util.xml;
 
 import java.io.Serializable;
 
 /**
  * Basic implementation of {@link Origin}
  *
  * @author Steve Ebersole
  */
 public class OriginImpl implements Origin, Serializable {
 	private final String type;
 	private final String name;
 
 	public OriginImpl(String type, String name) {
 		this.type = type;
 		this.name = name;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getType() {
 		return type;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getName() {
 		return name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XmlDocumentImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XmlDocumentImpl.java
index e936c92add..aac9763100 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XmlDocumentImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XmlDocumentImpl.java
@@ -1,61 +1,57 @@
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
 package org.hibernate.internal.util.xml;
 
 import java.io.Serializable;
 
 import org.dom4j.Document;
 
 /**
  * Basic implemementation of {@link XmlDocument}
  *
  * @author Steve Ebersole
  */
 public class XmlDocumentImpl implements XmlDocument, Serializable {
 	private final Document documentTree;
 	private final Origin origin;
 
 	public XmlDocumentImpl(Document documentTree, String originType, String originName) {
 		this( documentTree, new OriginImpl( originType, originName ) );
 	}
 
 	public XmlDocumentImpl(Document documentTree, Origin origin) {
 		this.documentTree = documentTree;
 		this.origin = origin;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Document getDocumentTree() {
 		return documentTree;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Origin getOrigin() {
 		return origin;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java b/hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
index ec9fdf17a5..d2b8789826 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/DefaultEntityAliases.java
@@ -1,201 +1,189 @@
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
 package org.hibernate.loader;
 
 import java.util.Collections;
 import java.util.Map;
 
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.Loadable;
 
 /**
  * EntityAliases which handles the logic of selecting user provided aliases (via return-property),
  * before using the default aliases.
  *
  * @author max
  *
  */
 public class DefaultEntityAliases implements EntityAliases {
 
 	private final String[] suffixedKeyColumns;
 	private final String[] suffixedVersionColumn;
 	private final String[][] suffixedPropertyColumns;
 	private final String suffixedDiscriminatorColumn;
 	private final String suffix;
 	private final String rowIdAlias;
 	private final Map userProvidedAliases;
 
 	/**
 	 * Calculate and cache select-clause aliases
 	 *
 	 * @param userProvidedAliases The explicit aliases provided in a result-set mapping.
 	 * @param persister The persister for which we are generating select aliases
 	 * @param suffix The calculated suffix.
 	 */
 	public DefaultEntityAliases(
 			Map userProvidedAliases,
 			Loadable persister,
 			String suffix) {
 		this.suffix = suffix;
 		this.userProvidedAliases = userProvidedAliases;
 
 		suffixedKeyColumns = determineKeyAlias( persister, suffix );
 		suffixedPropertyColumns = determinePropertyAliases( persister );
 		suffixedDiscriminatorColumn = determineDiscriminatorAlias( persister, suffix );
 		suffixedVersionColumn = determineVersionAlias( persister );
 		rowIdAlias = Loadable.ROWID_ALIAS + suffix; // TODO: not visible to the user!
 	}
 
 	public DefaultEntityAliases(Loadable persister, String suffix) {
 		this( Collections.EMPTY_MAP, persister, suffix );
 	}
 
 	private String[] determineKeyAlias(Loadable persister, String suffix) {
 		final String[] aliases;
 		final String[] keyColumnsCandidates = getUserProvidedAliases( persister.getIdentifierPropertyName(), null );
 		if ( keyColumnsCandidates == null ) {
 			aliases = getUserProvidedAliases(
 					"id",
 					getIdentifierAliases(persister, suffix)
 			);
 		}
 		else {
 			aliases = keyColumnsCandidates;
 		}
 		final String[] rtn = StringHelper.unquote( aliases, persister.getFactory().getDialect() );
 		intern( rtn );
 		return rtn;
 	}
 
 	private String[][] determinePropertyAliases(Loadable persister) {
 		return getSuffixedPropertyAliases( persister );
 	}
 
 	private String determineDiscriminatorAlias(Loadable persister, String suffix) {
 		String alias = getUserProvidedAlias( "class", getDiscriminatorAlias( persister, suffix ) );
 		return StringHelper.unquote( alias, persister.getFactory().getDialect() );
 	}
 
 	private String[] determineVersionAlias(Loadable persister) {
 		return persister.isVersioned()
 				? suffixedPropertyColumns[ persister.getVersionProperty() ]
 				: null;
 	}
 
 	protected String getDiscriminatorAlias(Loadable persister, String suffix) {
 		return persister.getDiscriminatorAlias(suffix);
 	}
 
 	protected String[] getIdentifierAliases(Loadable persister, String suffix) {
 		return persister.getIdentifierAliases(suffix);
 	}
 
 	protected String[] getPropertyAliases(Loadable persister, int j) {
 		return persister.getPropertyAliases(suffix, j);
 	}
 
 	private String[] getUserProvidedAliases(String propertyPath, String[] defaultAliases) {
 		String[] result = (String[]) userProvidedAliases.get(propertyPath);
 		if (result==null) {
 			return defaultAliases;
 		}
 		else {
 			return result;
 		}
 	}
 
 	private String getUserProvidedAlias(String propertyPath, String defaultAlias) {
 		String[] columns = (String[]) userProvidedAliases.get(propertyPath);
 		if (columns==null) {
 			return defaultAlias;
 		}
 		else {
 			return columns[0];
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[][] getSuffixedPropertyAliases(Loadable persister) {
 		final int size = persister.getPropertyNames().length;
 		final String[][] suffixedPropertyAliases = new String[size][];
 		for ( int j = 0; j < size; j++ ) {
 			suffixedPropertyAliases[j] = getUserProvidedAliases(
 					persister.getPropertyNames()[j],
 					getPropertyAliases( persister, j )
 			);
 			suffixedPropertyAliases[j] = StringHelper.unquote( suffixedPropertyAliases[j], persister.getFactory().getDialect() );
 			intern( suffixedPropertyAliases[j] );
 		}
 		return suffixedPropertyAliases;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[] getSuffixedVersionAliases() {
 		return suffixedVersionColumn;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[][] getSuffixedPropertyAliases() {
 		return suffixedPropertyColumns;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getSuffixedDiscriminatorAlias() {
 		return suffixedDiscriminatorColumn;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[] getSuffixedKeyAliases() {
 		return suffixedKeyColumns;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getRowIdAlias() {
 		return rowIdAlias;
 	}
 
 	@Override
 	public String getSuffix() {
 		return suffix;
 	}
 
 	private static void intern(String[] strings) {
 		for (int i=0; i<strings.length; i++ ) {
 			strings[i] = strings[i].intern();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
index d8fe714890..0e1f6a0610 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
@@ -1,671 +1,675 @@
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
 package org.hibernate.loader.criteria;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.criterion.CriteriaQuery;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.EnhancedProjection;
 import org.hibernate.criterion.Projection;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.StringRepresentableType;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public class CriteriaQueryTranslator implements CriteriaQuery {
 
 	public static final String ROOT_SQL_ALIAS = Criteria.ROOT_ALIAS + '_';
 
 	private CriteriaQuery outerQueryTranslator;
 
 	private final CriteriaImpl rootCriteria;
 	private final String rootEntityName;
 	private final String rootSQLAlias;
 
 	private final Map<Criteria, CriteriaInfoProvider> criteriaInfoMap = new LinkedHashMap<Criteria, CriteriaInfoProvider>();
 	private final Map<String, CriteriaInfoProvider> nameCriteriaInfoMap = new LinkedHashMap<String, CriteriaInfoProvider>();
 	private final Map<Criteria, String> criteriaSQLAliasMap = new HashMap<Criteria, String>();
 	private final Map<String, Criteria> aliasCriteriaMap = new HashMap<String, Criteria>();
 	private final Map<String, Criteria> associationPathCriteriaMap = new LinkedHashMap<String, Criteria>();
 	private final Map<String, JoinType> associationPathJoinTypesMap = new LinkedHashMap<String,JoinType>();
 	private final Map<String, Criterion> withClauseMap = new HashMap<String, Criterion>();
 	
 	private final SessionFactoryImplementor sessionFactory;
 	private final SessionFactoryHelper helper;
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias,
 	        CriteriaQuery outerQuery) throws HibernateException {
 		this( factory, criteria, rootEntityName, rootSQLAlias );
 		outerQueryTranslator = outerQuery;
 	}
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias) throws HibernateException {
 		this.rootCriteria = criteria;
 		this.rootEntityName = rootEntityName;
 		this.sessionFactory = factory;
 		this.rootSQLAlias = rootSQLAlias;
 		this.helper = new SessionFactoryHelper(factory);
 		createAliasCriteriaMap();
 		createAssociationPathCriteriaMap();
 		createCriteriaEntityNameMap();
 		createCriteriaSQLAliasMap();
 	}
 	@Override
 	public String generateSQLAlias() {
 		int aliasCount = 0;
 		return StringHelper.generateAlias( Criteria.ROOT_ALIAS, aliasCount ) + '_';
 	}
 
 	public String getRootSQLALias() {
 		return rootSQLAlias;
 	}
 
 	private Criteria getAliasedCriteria(String alias) {
 		return aliasCriteriaMap.get( alias );
 	}
 
 	public boolean isJoin(String path) {
 		return associationPathCriteriaMap.containsKey( path );
 	}
 
 	public JoinType getJoinType(String path) {
 		JoinType result = associationPathJoinTypesMap.get( path );
 		return ( result == null ? JoinType.INNER_JOIN : result );
 	}
 
 	public Criteria getCriteria(String path) {
 		return associationPathCriteriaMap.get( path );
 	}
 
 	public Set<Serializable> getQuerySpaces() {
 		Set<Serializable> result = new HashSet<Serializable>();
 		for ( CriteriaInfoProvider info : criteriaInfoMap.values() ) {
 			result.addAll( Arrays.asList( info.getSpaces() ) );
 		}
 		return result;
 	}
 
 	private void createAliasCriteriaMap() {
 		aliasCriteriaMap.put( rootCriteria.getAlias(), rootCriteria );
 		Iterator<CriteriaImpl.Subcriteria> iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			Criteria subcriteria = iter.next();
 			if ( subcriteria.getAlias() != null ) {
 				Object old = aliasCriteriaMap.put( subcriteria.getAlias(), subcriteria );
 				if ( old != null ) {
 					throw new QueryException( "duplicate alias: " + subcriteria.getAlias() );
 				}
 			}
 		}
 	}
 
 	private void createAssociationPathCriteriaMap() {
 		final Iterator<CriteriaImpl.Subcriteria> iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria crit = iter.next();
 			String wholeAssociationPath = getWholeAssociationPath( crit );
 			Object old = associationPathCriteriaMap.put( wholeAssociationPath, crit );
 			if ( old != null ) {
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			JoinType joinType = crit.getJoinType();
 			old = associationPathJoinTypesMap.put( wholeAssociationPath, joinType );
 			if ( old != null ) {
 				// TODO : not so sure this is needed...
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			if ( crit.getWithClause() != null ) {
 				this.withClauseMap.put( wholeAssociationPath, crit.getWithClause() );
 			}
 		}
 	}
 
 	private String getWholeAssociationPath(CriteriaImpl.Subcriteria subcriteria) {
 		String path = subcriteria.getPath();
 
 		// some messy, complex stuff here, since createCriteria() can take an
 		// aliased path, or a path rooted at the creating criteria instance
 		Criteria parent = null;
 		if ( path.indexOf( '.' ) > 0 ) {
 			// if it is a compound path
 			String testAlias = StringHelper.root( path );
 			if ( !testAlias.equals( subcriteria.getAlias() ) ) {
 				// and the qualifier is not the alias of this criteria
 				//      -> check to see if we belong to some criteria other
 				//          than the one that created us
 				parent = aliasCriteriaMap.get( testAlias );
 			}
 		}
 		if ( parent == null ) {
 			// otherwise assume the parent is the the criteria that created us
 			parent = subcriteria.getParent();
 		}
 		else {
 			path = StringHelper.unroot( path );
 		}
 
 		if ( parent.equals( rootCriteria ) ) {
 			// if its the root criteria, we are done
 			return path;
 		}
 		else {
 			// otherwise, recurse
 			return getWholeAssociationPath( ( CriteriaImpl.Subcriteria ) parent ) + '.' + path;
 		}
 	}
 
 	private void createCriteriaEntityNameMap() {
 		// initialize the rootProvider first
 		final CriteriaInfoProvider rootProvider = new EntityCriteriaInfoProvider(
 				(Queryable) sessionFactory.getEntityPersister( rootEntityName )
 		);
 		criteriaInfoMap.put( rootCriteria, rootProvider);
 		nameCriteriaInfoMap.put( rootProvider.getName(), rootProvider );
 
 		for ( final String key : associationPathCriteriaMap.keySet() ) {
 			final Criteria value = associationPathCriteriaMap.get( key );
 			final CriteriaInfoProvider info = getPathInfo( key );
 			criteriaInfoMap.put( value, info );
 			nameCriteriaInfoMap.put( info.getName(), info );
 		}
 	}
 
 
 	private CriteriaInfoProvider getPathInfo(String path) {
 		StringTokenizer tokens = new StringTokenizer( path, "." );
 		String componentPath = "";
 
 		// start with the 'rootProvider'
 		CriteriaInfoProvider provider = nameCriteriaInfoMap.get( rootEntityName );
 
 		while ( tokens.hasMoreTokens() ) {
 			componentPath += tokens.nextToken();
 			final Type type = provider.getType( componentPath );
 			if ( type.isAssociationType() ) {
 				// CollectionTypes are always also AssociationTypes - but there's not always an associated entity...
-				final AssociationType atype = ( AssociationType ) type;
+				final AssociationType atype = (AssociationType) type;
 				final CollectionType ctype = type.isCollectionType() ? (CollectionType)type : null;
 				final Type elementType = (ctype != null) ? ctype.getElementType( sessionFactory ) : null;
 				// is the association a collection of components or value-types? (i.e a colloction of valued types?)
 				if ( ctype != null  && elementType.isComponentType() ) {
 					provider = new ComponentCollectionCriteriaInfoProvider( helper.getCollectionPersister(ctype.getRole()) );
 				}
 				else if ( ctype != null && !elementType.isEntityType() ) {
 					provider = new ScalarCollectionCriteriaInfoProvider( helper, ctype.getRole() );
 				}
 				else {
-					provider = new EntityCriteriaInfoProvider(( Queryable ) sessionFactory.getEntityPersister(
-											  atype.getAssociatedEntityName( sessionFactory )
-											  ));
+					provider = new EntityCriteriaInfoProvider(
+							(Queryable) sessionFactory.getEntityPersister( atype.getAssociatedEntityName( sessionFactory ) )
+					);
 				}
 				
 				componentPath = "";
 			}
 			else if ( type.isComponentType() ) {
 				if (!tokens.hasMoreTokens()) {
-					throw new QueryException("Criteria objects cannot be created directly on components.  Create a criteria on owning entity and use a dotted property to access component property: "+path);
+					throw new QueryException(
+							"Criteria objects cannot be created directly on components.  Create a criteria on " +
+									"owning entity and use a dotted property to access component property: " + path
+					);
 				}
 				else {
 					componentPath += '.';
 				}
 			}
 			else {
 				throw new QueryException( "not an association: " + componentPath );
 			}
 		}
 		
 		return provider;
 	}
 
 	public int getSQLAliasCount() {
 		return criteriaSQLAliasMap.size();
 	}
 
 	private void createCriteriaSQLAliasMap() {
 		int i = 0;
 		for(final Criteria crit : criteriaInfoMap.keySet()){
 			final CriteriaInfoProvider value = criteriaInfoMap.get( crit );
 			String alias = crit.getAlias();
 			if ( alias == null ) {
 				// the entity name
 				alias = value.getName();
 			}
 			criteriaSQLAliasMap.put( crit, StringHelper.generateAlias( alias, i++ ) );
 		}
 
 		criteriaSQLAliasMap.put( rootCriteria, rootSQLAlias );
 	}
 
 	public CriteriaImpl getRootCriteria() {
 		return rootCriteria;
 	}
 
 	public QueryParameters getQueryParameters() {
 		final RowSelection selection = new RowSelection();
 		selection.setFirstRow( rootCriteria.getFirstResult() );
 		selection.setMaxRows( rootCriteria.getMaxResults() );
 		selection.setTimeout( rootCriteria.getTimeout() );
 		selection.setFetchSize( rootCriteria.getFetchSize() );
 
 		final LockOptions lockOptions = new LockOptions();
 		final Map<String, LockMode> lockModeMap = rootCriteria.getLockModes();
 		for ( final String key : lockModeMap.keySet() ) {
 			final Criteria subcriteria = getAliasedCriteria( key );
 			lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), lockModeMap.get( key ) );
 		}
 
 		final List<Object> values = new ArrayList<Object>();
 		final List<Type> types = new ArrayList<Type>();
 		final Iterator<CriteriaImpl.Subcriteria> subcriteriaIterator = rootCriteria.iterateSubcriteria();
 		while ( subcriteriaIterator.hasNext() ) {
 			final CriteriaImpl.Subcriteria subcriteria = subcriteriaIterator.next();
 			final LockMode lm = subcriteria.getLockMode();
 			if ( lm != null ) {
 				lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), lm );
 			}
 			if ( subcriteria.getWithClause() != null ) {
 				final TypedValue[] tv = subcriteria.getWithClause().getTypedValues( subcriteria, this );
 				for ( TypedValue aTv : tv ) {
 					values.add( aTv.getValue() );
 					types.add( aTv.getType() );
 				}
 			}
 		}
 
 		// Type and value gathering for the WHERE clause needs to come AFTER lock mode gathering,
 		// because the lock mode gathering loop now contains join clauses which can contain
 		// parameter bindings (as in the HQL WITH clause).
 		final Iterator<CriteriaImpl.CriterionEntry> iter = rootCriteria.iterateExpressionEntries();
 		while ( iter.hasNext() ) {
 			final CriteriaImpl.CriterionEntry ce = iter.next();
 			final TypedValue[] tv = ce.getCriterion().getTypedValues( ce.getCriteria(), this );
 			for ( TypedValue aTv : tv ) {
 				values.add( aTv.getValue() );
 				types.add( aTv.getType() );
 			}
 		}
 
 		final Object[] valueArray = values.toArray();
 		final Type[] typeArray = ArrayHelper.toTypeArray( types );
 		return new QueryParameters(
 				typeArray,
 		        valueArray,
 		        lockOptions,
 		        selection,
 		        rootCriteria.isReadOnlyInitialized(),
 		        ( rootCriteria.isReadOnlyInitialized() && rootCriteria.isReadOnly() ),
 		        rootCriteria.getCacheable(),
 		        rootCriteria.getCacheRegion(),
 		        rootCriteria.getComment(),
 		        rootCriteria.getQueryHints(),
 		        rootCriteria.isLookupByNaturalKey(),
 		        rootCriteria.getResultTransformer()
 		);
 	}
 
 	public boolean hasProjection() {
 		return rootCriteria.getProjection() != null;
 	}
 
 	public String getGroupBy() {
 		if ( rootCriteria.getProjection().isGrouped() ) {
 			return rootCriteria.getProjection()
 					.toGroupSqlString( rootCriteria.getProjectionCriteria(), this );
 		}
 		else {
 			return "";
 		}
 	}
 
 	public String getSelect() {
 		return rootCriteria.getProjection().toSqlString(
 				rootCriteria.getProjectionCriteria(),
 		        0,
 		        this
 		);
 	}
 
 	/* package-protected */
 	Type getResultType(Criteria criteria) {
 		return getFactory().getTypeResolver().getTypeFactory().manyToOne( getEntityName( criteria ) );
 	}
 
 	public Type[] getProjectedTypes() {
 		return rootCriteria.getProjection().getTypes( rootCriteria, this );
 	}
 
 	public String[] getProjectedColumnAliases() {
 		return rootCriteria.getProjection() instanceof EnhancedProjection ?
 				( ( EnhancedProjection ) rootCriteria.getProjection() ).getColumnAliases( 0, rootCriteria, this ) :
 				rootCriteria.getProjection().getColumnAliases( 0 );
 	}
 
 	public String[] getProjectedAliases() {
 		return rootCriteria.getProjection().getAliases();
 	}
 
 	public String getWhereCondition() {
 		StringBuilder condition = new StringBuilder( 30 );
 		Iterator<CriteriaImpl.CriterionEntry> criterionIterator = rootCriteria.iterateExpressionEntries();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.CriterionEntry entry = criterionIterator.next();
 			String sqlString = entry.getCriterion().toSqlString( entry.getCriteria(), this );
 			condition.append( sqlString );
 			if ( criterionIterator.hasNext() ) {
 				condition.append( " and " );
 			}
 		}
 		return condition.toString();
 	}
 
 	public String getOrderBy() {
 		StringBuilder orderBy = new StringBuilder( 30 );
 		Iterator<CriteriaImpl.OrderEntry> criterionIterator = rootCriteria.iterateOrderings();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.OrderEntry oe = criterionIterator.next();
 			orderBy.append( oe.getOrder().toSqlString( oe.getCriteria(), this ) );
 			if ( criterionIterator.hasNext() ) {
 				orderBy.append( ", " );
 			}
 		}
 		return orderBy.toString();
 	}
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return sessionFactory;
 	}
 	@Override
 	public String getSQLAlias(Criteria criteria) {
 		return criteriaSQLAliasMap.get( criteria );
 	}
 	@Override
 	public String getEntityName(Criteria criteria) {
 		final CriteriaInfoProvider infoProvider = criteriaInfoMap.get( criteria );
 		return infoProvider != null ? infoProvider.getName() : null;
 	}
 	@Override
 	public String getColumn(Criteria criteria, String propertyName) {
 		String[] cols = getColumns( propertyName, criteria );
 		if ( cols.length != 1 ) {
 			throw new QueryException( "property does not map to a single column: " + propertyName );
 		}
 		return cols[0];
 	}
 
 	/**
 	 * Get the names of the columns constrained
 	 * by this criterion.
 	 */
 	@Override
 	public String[] getColumnsUsingProjection(
 			Criteria subcriteria,
 	        String propertyName) throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		String[] projectionColumns = null;
 		if ( projection != null ) {
 			projectionColumns = ( projection instanceof EnhancedProjection ?
 					( ( EnhancedProjection ) projection ).getColumnAliases( propertyName, 0, rootCriteria, this ) :
 					projection.getColumnAliases( propertyName, 0 )
 			);
 		}
 		if ( projectionColumns == null ) {
 			//it does not refer to an alias of a projection,
 			//look for a property
 			try {
 				return getColumns( propertyName, subcriteria );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getColumnsUsingProjection( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			//it refers to an alias of a projection
 			return projectionColumns;
 		}
 	}
 	@Override
 	public String[] getIdentifierColumns(Criteria criteria) {
 		String[] idcols =
 				( ( Loadable ) getPropertyMapping( getEntityName( criteria ) ) ).getIdentifierColumnNames();
 		return StringHelper.qualify( getSQLAlias( criteria ), idcols );
 	}
 	@Override
 	public Type getIdentifierType(Criteria criteria) {
 		return ( ( Loadable ) getPropertyMapping( getEntityName( criteria ) ) ).getIdentifierType();
 	}
 	@Override
 	public TypedValue getTypedIdentifierValue(Criteria criteria, Object value) {
 		final Loadable loadable = ( Loadable ) getPropertyMapping( getEntityName( criteria ) );
 		return new TypedValue( loadable.getIdentifierType(), value );
 	}
 	@Override
 	public String[] getColumns(
 			String propertyName,
 	        Criteria subcriteria) throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toColumns(
 						getSQLAlias( subcriteria, propertyName ),
 				        getPropertyName( propertyName )
 				);
 	}
 
 	/**
 	 * Get the names of the columns mapped by a property path; if the
 	 * property path is not found in subcriteria, try the "outer" query.
 	 * Projection aliases are ignored.
 	 */
 	@Override
 	public String[] findColumns(String propertyName, Criteria subcriteria )
 	throws HibernateException {
 		try {
 			return getColumns( propertyName, subcriteria );
 		}
 		catch ( HibernateException he ) {
 			//not found in inner query, try the outer query
 			if ( outerQueryTranslator != null ) {
 				return outerQueryTranslator.findColumns( propertyName, subcriteria );
 			}
 			else {
 				throw he;
 			}
 		}
 	}
 	@Override
 	public Type getTypeUsingProjection(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		Type[] projectionTypes = projection == null ?
 		                         null :
 		                         projection.getTypes( propertyName, subcriteria, this );
 
 		if ( projectionTypes == null ) {
 			try {
 				//it does not refer to an alias of a projection,
 				//look for a property
 				return getType( subcriteria, propertyName );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getType( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			if ( projectionTypes.length != 1 ) {
 				//should never happen, i think
 				throw new QueryException( "not a single-length projection: " + propertyName );
 			}
 			return projectionTypes[0];
 		}
 	}
 	@Override
 	public Type getType(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toType( getPropertyName( propertyName ) );
 	}
 
 	/**
 	 * Get the a typed value for the given property value.
 	 */
 	@Override
 	public TypedValue getTypedValue(Criteria subcriteria, String propertyName, Object value) throws HibernateException {
 		// Detect discriminator values...
 		if ( value instanceof Class ) {
-			final Class entityClass = ( Class ) value;
+			final Class entityClass = (Class) value;
 			final Queryable q = SessionFactoryHelper.findQueryableUsingImports( sessionFactory, entityClass.getName() );
 			if ( q != null ) {
 				final Type type = q.getDiscriminatorType();
 				String stringValue = q.getDiscriminatorSQLValue();
-				if (stringValue != null && stringValue.length() > 2
+				if ( stringValue != null
+						&& stringValue.length() > 2
 						&& stringValue.startsWith( "'" )
-						&& stringValue.endsWith( "'" )) {
+						&& stringValue.endsWith( "'" ) ) {
 					// remove the single quotes
 					stringValue = stringValue.substring( 1, stringValue.length() - 1 );
 				}
 				
 				// Convert the string value into the proper type.
 				if ( type instanceof StringRepresentableType ) {
 					final StringRepresentableType nullableType = (StringRepresentableType) type;
 					value = nullableType.fromStringValue( stringValue );
 				}
 				else {
 					throw new QueryException( "Unsupported discriminator type " + type );
 				}
 				return new TypedValue( type, value );
 			}
 		}
 		// Otherwise, this is an ordinary value.
 		return new TypedValue( getTypeUsingProjection( subcriteria, propertyName ), value );
 	}
 
 	private PropertyMapping getPropertyMapping(String entityName) throws MappingException {
-		final CriteriaInfoProvider info = nameCriteriaInfoMap.get(entityName);
+		final CriteriaInfoProvider info = nameCriteriaInfoMap.get( entityName );
 		if ( info == null ) {
 			throw new HibernateException( "Unknown entity: " + entityName );
 		}
 		return info.getPropertyMapping();
 	}
 
 	//TODO: use these in methods above
 	@Override
 	public String getEntityName(Criteria subcriteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			final String root = StringHelper.root( propertyName );
 			final Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return getEntityName( crit );
 			}
 		}
 		return getEntityName( subcriteria );
 	}
 	@Override
 	public String getSQLAlias(Criteria criteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			final String root = StringHelper.root( propertyName );
 			final Criteria subcriteria = getAliasedCriteria( root );
 			if ( subcriteria != null ) {
 				return getSQLAlias( subcriteria );
 			}
 		}
 		return getSQLAlias( criteria );
 	}
 	@Override
 	public String getPropertyName(String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			final String root = StringHelper.root( propertyName );
 			final Criteria criteria = getAliasedCriteria( root );
 			if ( criteria != null ) {
 				return propertyName.substring( root.length() + 1 );
 			}
 		}
 		return propertyName;
 	}
 
 	public String getWithClause(String path) {
 		final Criterion criterion = withClauseMap.get( path );
 		return criterion == null ? null : criterion.toSqlString( getCriteria( path ), this );
 	}
 
 	public boolean hasRestriction(String path) {
 		final CriteriaImpl.Subcriteria subcriteria = (CriteriaImpl.Subcriteria) getCriteria( path );
 		return subcriteria != null && subcriteria.hasRestriction();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ReturnGraphTreePrinter.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ReturnGraphTreePrinter.java
index 23042dcffb..f29414c85e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ReturnGraphTreePrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/build/spi/ReturnGraphTreePrinter.java
@@ -1,235 +1,233 @@
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
 package org.hibernate.loader.plan.build.spi;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 
 import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
 import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
 import org.hibernate.loader.plan.spi.CollectionFetchableElement;
 import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchSource;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.plan.spi.ScalarReturn;
 
 /**
  * Prints a {@link Return} graph as a tree structure.
  * <p/>
  * Intended for use in debugging, logging, etc.
  *
  * @author Steve Ebersole
  */
 public class ReturnGraphTreePrinter {
 	/**
 	 * Singleton access
 	 */
 	public static final ReturnGraphTreePrinter INSTANCE = new ReturnGraphTreePrinter();
 
 	private ReturnGraphTreePrinter() {
 	}
 
 	public String asString(Return rootReturn) {
 		return asString( rootReturn, 0 );
 	}
 
 	public String asString(Return rootReturn, int depth) {
 		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
 		final PrintStream ps = new PrintStream( byteArrayOutputStream );
 		write( rootReturn,  depth, ps );
 		ps.flush();
 		return new String( byteArrayOutputStream.toByteArray() );
 
 	}
 
 	public void write(Return rootReturn, PrintStream printStream) {
 		write( rootReturn, new PrintWriter( printStream ) );
 	}
 
 	public void write(Return rootReturn, int depth, PrintStream printStream) {
 		write( rootReturn, depth, new PrintWriter( printStream ) );
 	}
 
 	// todo : see ASTPrinter and try to apply its awesome tree structuring here.
 	//		I mean the stuff it does with '|' and '\\-' and '+-' etc as
 	//		prefixes for the tree nodes actual text to visually render the tree
 
 	public void write(Return rootReturn, PrintWriter printWriter) {
 		write( rootReturn, 0, printWriter );
 	}
 
 	public void write(Return rootReturn, int depth, PrintWriter printWriter) {
 		if ( rootReturn == null ) {
 			printWriter.println( "Return is null!" );
 			return;
 		}
 
 		printWriter.write( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) );
 
 
 		if ( ScalarReturn.class.isInstance( rootReturn ) ) {
 			printWriter.println( extractDetails( (ScalarReturn) rootReturn ) );
 		}
 		else if ( EntityReturn.class.isInstance( rootReturn ) ) {
 			final EntityReturn entityReturn = (EntityReturn) rootReturn;
 			printWriter.println( extractDetails( entityReturn ) );
 			writeEntityReferenceFetches( entityReturn, depth+1, printWriter );
 		}
 		else if ( CollectionReference.class.isInstance( rootReturn ) ) {
 			final CollectionReference collectionReference = (CollectionReference) rootReturn;
 			printWriter.println( extractDetails( collectionReference ) );
 			writeCollectionReferenceFetches( collectionReference, depth+1, printWriter );
 		}
 
 		printWriter.flush();
 	}
 
 	private String extractDetails(ScalarReturn rootReturn) {
 		return String.format(
 				"%s(name=%s, type=%s)",
 				rootReturn.getClass().getSimpleName(),
 				rootReturn.getName(),
 				rootReturn.getType().getName()
 		);
 	}
 
 	private String extractDetails(EntityReference entityReference) {
 		return String.format(
 				"%s(entity=%s, querySpaceUid=%s, path=%s)",
 				entityReference.getClass().getSimpleName(),
 				entityReference.getEntityPersister().getEntityName(),
 				entityReference.getQuerySpaceUid(),
 				entityReference.getPropertyPath().getFullPath()
 		);
 	}
 
 	private String extractDetails(CollectionReference collectionReference) {
 		// todo : include some form of parameterized type signature?  i.e., List<String>, Set<Person>, etc
 		return String.format(
 				"%s(collection=%s, querySpaceUid=%s, path=%s)",
 				collectionReference.getClass().getSimpleName(),
 				collectionReference.getCollectionPersister().getRole(),
 				collectionReference.getQuerySpaceUid(),
 				collectionReference.getPropertyPath().getFullPath()
 		);
 	}
 
 	private String extractDetails(CompositeFetch compositeFetch) {
 		return String.format(
 				"%s(composite=%s, querySpaceUid=%s, path=%s)",
 				compositeFetch.getClass().getSimpleName(),
 				compositeFetch.getFetchedType().getReturnedClass().getName(),
 				compositeFetch.getQuerySpaceUid(),
 				compositeFetch.getPropertyPath().getFullPath()
 		);
 	}
 
 	private void writeEntityReferenceFetches(EntityReference entityReference, int depth, PrintWriter printWriter) {
 		if ( BidirectionalEntityReference.class.isInstance( entityReference ) ) {
 			return;
 		}
 		if ( entityReference.getIdentifierDescription().hasFetches() ) {
 			printWriter.println( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "(entity id) " );
 			writeFetches( ( (FetchSource) entityReference.getIdentifierDescription() ).getFetches(), depth+1, printWriter );
 		}
 
 		writeFetches( entityReference.getFetches(), depth, printWriter );
 	}
 
 	private void writeFetches(Fetch[] fetches, int depth, PrintWriter printWriter) {
 		for ( Fetch fetch : fetches ) {
 			writeFetch( fetch, depth, printWriter );
 		}
 	}
 
 	private void writeFetch(Fetch fetch, int depth, PrintWriter printWriter) {
 		printWriter.print( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) );
 
 		if ( EntityFetch.class.isInstance( fetch ) ) {
 			final EntityFetch entityFetch = (EntityFetch) fetch;
 			printWriter.println( extractDetails( entityFetch ) );
 			writeEntityReferenceFetches( entityFetch, depth+1, printWriter );
 		}
 		else if ( CompositeFetch.class.isInstance( fetch ) ) {
 			final CompositeFetch compositeFetch = (CompositeFetch) fetch;
 			printWriter.println( extractDetails( compositeFetch ) );
 			writeCompositeFetchFetches( compositeFetch, depth+1, printWriter );
 		}
 		else if ( CollectionAttributeFetch.class.isInstance( fetch ) ) {
 			final CollectionAttributeFetch collectionFetch = (CollectionAttributeFetch) fetch;
 			printWriter.println( extractDetails( collectionFetch ) );
 			writeCollectionReferenceFetches( collectionFetch, depth+1, printWriter );
 		}
 	}
 
 	private void writeCompositeFetchFetches(CompositeFetch compositeFetch, int depth, PrintWriter printWriter) {
 		writeFetches( compositeFetch.getFetches(), depth, printWriter );
 	}
 
 	private void writeCollectionReferenceFetches(
 			CollectionReference collectionReference,
 			int depth,
 			PrintWriter printWriter) {
-		{
-			final CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
-			if ( indexGraph != null ) {
-				printWriter.print( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "(collection index) " );
-
-				if ( EntityReference.class.isInstance( indexGraph ) ) {
-					final EntityReference indexGraphAsEntityReference = (EntityReference) indexGraph;
-					printWriter.println( extractDetails( indexGraphAsEntityReference ) );
-					writeEntityReferenceFetches( indexGraphAsEntityReference, depth+1, printWriter );
-				}
-				else if ( CompositeFetch.class.isInstance( indexGraph ) ) {
-					final CompositeFetch indexGraphAsCompositeFetch = (CompositeFetch) indexGraph;
-					printWriter.println( extractDetails( indexGraphAsCompositeFetch ) );
-					writeCompositeFetchFetches( indexGraphAsCompositeFetch, depth+1, printWriter );
-				}
+		final CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
+		if ( indexGraph != null ) {
+			printWriter.print( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "(collection index) " );
+
+			if ( EntityReference.class.isInstance( indexGraph ) ) {
+				final EntityReference indexGraphAsEntityReference = (EntityReference) indexGraph;
+				printWriter.println( extractDetails( indexGraphAsEntityReference ) );
+				writeEntityReferenceFetches( indexGraphAsEntityReference, depth+1, printWriter );
+			}
+			else if ( CompositeFetch.class.isInstance( indexGraph ) ) {
+				final CompositeFetch indexGraphAsCompositeFetch = (CompositeFetch) indexGraph;
+				printWriter.println( extractDetails( indexGraphAsCompositeFetch ) );
+				writeCompositeFetchFetches( indexGraphAsCompositeFetch, depth+1, printWriter );
 			}
 		}
 
 		final CollectionFetchableElement elementGraph = collectionReference.getElementGraph();
 		if ( elementGraph != null ) {
 			printWriter.print( TreePrinterHelper.INSTANCE.generateNodePrefix( depth ) + "(collection element) " );
 
 			if ( EntityReference.class.isInstance( elementGraph ) ) {
 				final EntityReference elementGraphAsEntityReference = (EntityReference) elementGraph;
 				printWriter.println( extractDetails( elementGraphAsEntityReference ) );
 				writeEntityReferenceFetches( elementGraphAsEntityReference, depth+1, printWriter );
 			}
 			else if ( CompositeFetch.class.isInstance( elementGraph ) ) {
 				final CompositeFetch elementGraphAsCompositeFetch = (CompositeFetch) elementGraph;
 				printWriter.println( extractDetails( elementGraphAsCompositeFetch ) );
 				writeCompositeFetchFetches( elementGraphAsCompositeFetch, depth+1, printWriter );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Backref.java b/hibernate-core/src/main/java/org/hibernate/mapping/Backref.java
index c3d8cedd2e..e0359ed69c 100755
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Backref.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Backref.java
@@ -1,71 +1,71 @@
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
 package org.hibernate.mapping;
+
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.property.PropertyAccessor;
 
 /**
  * @author Gavin King
  */
 public class Backref extends Property {
 	private String collectionRole;
 	private String entityName;
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isBackRef() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isSynthetic() {
 		return true;
 	}
 
 	public String getCollectionRole() {
 		return collectionRole;
 	}
 
 	public void setCollectionRole(String collectionRole) {
 		this.collectionRole = collectionRole;
 	}
 
+	@Override
 	public boolean isBasicPropertyAccessor() {
 		return false;
 	}
 
+	@Override
 	public PropertyAccessor getPropertyAccessor(Class clazz) {
 		return new BackrefPropertyAccessor(collectionRole, entityName);
 	}
 	
 	public String getEntityName() {
 		return entityName;
 	}
+
 	public void setEntityName(String entityName) {
 		this.entityName = entityName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
index 7a2bfe4ee6..1495f6be43 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Component.java
@@ -1,433 +1,452 @@
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
 package org.hibernate.mapping;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.CompositeNestedGeneratedValueGenerator;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.property.Setter;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeFactory;
 
 /**
  * The mapping for a component, composite element,
  * composite identifier, etc.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class Component extends SimpleValue implements MetaAttributable {
-	private ArrayList properties = new ArrayList();
+	private ArrayList<Property> properties = new ArrayList<Property>();
 	private String componentClassName;
 	private boolean embedded;
 	private String parentProperty;
 	private PersistentClass owner;
 	private boolean dynamic;
 	private Map metaAttributes;
 	private String nodeName;
 	private boolean isKey;
 	private String roleName;
 
-	private java.util.Map tuplizerImpls;
+	private java.util.Map<EntityMode,String> tuplizerImpls;
 
 	public Component(Mappings mappings, PersistentClass owner) throws MappingException {
 		super( mappings, owner.getTable() );
 		this.owner = owner;
 	}
 
 	public Component(Mappings mappings, Component component) throws MappingException {
 		super( mappings, component.getTable() );
 		this.owner = component.getOwner();
 	}
 
 	public Component(Mappings mappings, Join join) throws MappingException {
 		super( mappings, join.getTable() );
 		this.owner = join.getPersistentClass();
 	}
 
 	public Component(Mappings mappings, Collection collection) throws MappingException {
 		super( mappings, collection.getCollectionTable() );
 		this.owner = collection.getOwner();
 	}
 
 	public int getPropertySpan() {
 		return properties.size();
 	}
+
 	public Iterator getPropertyIterator() {
 		return properties.iterator();
 	}
+
 	public void addProperty(Property p) {
-		properties.add(p);
+		properties.add( p );
 	}
+
+	@Override
 	public void addColumn(Column column) {
 		throw new UnsupportedOperationException("Cant add a column to a component");
 	}
+
+	@Override
 	public int getColumnSpan() {
 		int n=0;
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property p = (Property) iter.next();
 			n+= p.getColumnSpan();
 		}
 		return n;
 	}
+
+	@Override
+	@SuppressWarnings("unchecked")
 	public Iterator<Selectable> getColumnIterator() {
 		Iterator[] iters = new Iterator[ getPropertySpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			iters[i++] = ( (Property) iter.next() ).getColumnIterator();
 		}
-		return new JoinedIterator(iters);
+		return new JoinedIterator( iters );
 	}
 
-	public void setTypeByReflection(String propertyClass, String propertyName) {}
-
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	public String getComponentClassName() {
 		return componentClassName;
 	}
 
 	public Class getComponentClass() throws MappingException {
 		try {
 			return ReflectHelper.classForName(componentClassName);
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("component class not found: " + componentClassName, cnfe);
 		}
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	public String getParentProperty() {
 		return parentProperty;
 	}
 
 	public void setComponentClassName(String componentClass) {
 		this.componentClassName = componentClass;
 	}
 
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public void setParentProperty(String parentProperty) {
 		this.parentProperty = parentProperty;
 	}
 
 	public boolean isDynamic() {
 		return dynamic;
 	}
 
 	public void setDynamic(boolean dynamic) {
 		this.dynamic = dynamic;
 	}
 
+	@Override
 	public Type getType() throws MappingException {
 		// TODO : temporary initial step towards HHH-1907
 		final ComponentMetamodel metamodel = new ComponentMetamodel( this );
 		final TypeFactory factory = getMappings().getTypeResolver().getTypeFactory();
 		return isEmbedded() ? factory.embeddedComponent( metamodel ) : factory.component( metamodel );
 	}
 
+	@Override
 	public void setTypeUsingReflection(String className, String propertyName)
 		throws MappingException {
 	}
-	
+
+	@Override
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
+
+	@Override
 	public MetaAttribute getMetaAttribute(String attributeName) {
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(attributeName);
 	}
 
+	@Override
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
-	
+
+	@Override
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
-	
+
+	@Override
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			boolean[] chunk = prop.getValue().getColumnInsertability();
 			if ( prop.isInsertable() ) {
 				System.arraycopy(chunk, 0, result, i, chunk.length);
 			}
 			i+=chunk.length;
 		}
 		return result;
 	}
 
+	@Override
 	public boolean[] getColumnUpdateability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		Iterator iter = getPropertyIterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			boolean[] chunk = prop.getValue().getColumnUpdateability();
 			if ( prop.isUpdateable() ) {
 				System.arraycopy(chunk, 0, result, i, chunk.length);
 			}
 			i+=chunk.length;
 		}
 		return result;
 	}
 	
 	public String getNodeName() {
 		return nodeName;
 	}
 	
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 	
 	public boolean isKey() {
 		return isKey;
 	}
 	
 	public void setKey(boolean isKey) {
 		this.isKey = isKey;
 	}
 	
 	public boolean hasPojoRepresentation() {
 		return componentClassName!=null;
 	}
 
 	public void addTuplizer(EntityMode entityMode, String implClassName) {
 		if ( tuplizerImpls == null ) {
-			tuplizerImpls = new HashMap();
+			tuplizerImpls = new HashMap<EntityMode,String>();
 		}
 		tuplizerImpls.put( entityMode, implClassName );
 	}
 
 	public String getTuplizerImplClassName(EntityMode mode) {
 		// todo : remove this once ComponentMetamodel is complete and merged
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
-		return ( String ) tuplizerImpls.get( mode );
+		return tuplizerImpls.get( mode );
 	}
 
+	@SuppressWarnings("UnusedDeclaration")
 	public Map getTuplizerMap() {
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return java.util.Collections.unmodifiableMap( tuplizerImpls );
 	}
 
 	public Property getProperty(String propertyName) throws MappingException {
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( prop.getName().equals(propertyName) ) {
 				return prop;
 			}
 		}
 		throw new MappingException("component property not found: " + propertyName);
 	}
 
 	public String getRoleName() {
 		return roleName;
 	}
 
 	public void setRoleName(String roleName) {
 		this.roleName = roleName;
 	}
 
+	@Override
 	public String toString() {
 		return getClass().getName() + '(' + properties.toString() + ')';
 	}
 
 	private IdentifierGenerator builtIdentifierGenerator;
 
+	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect,
 			String defaultCatalog,
 			String defaultSchema,
 			RootClass rootClass) throws MappingException {
 		if ( builtIdentifierGenerator == null ) {
 			builtIdentifierGenerator = buildIdentifierGenerator(
 					identifierGeneratorFactory,
 					dialect,
 					defaultCatalog,
 					defaultSchema,
 					rootClass
 			);
 		}
 		return builtIdentifierGenerator;
 	}
 
 	private IdentifierGenerator buildIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect,
 			String defaultCatalog,
 			String defaultSchema,
 			RootClass rootClass) throws MappingException {
 		final boolean hasCustomGenerator = ! DEFAULT_ID_GEN_STRATEGY.equals( getIdentifierGeneratorStrategy() );
 		if ( hasCustomGenerator ) {
 			return super.createIdentifierGenerator(
 					identifierGeneratorFactory, dialect, defaultCatalog, defaultSchema, rootClass
 			);
 		}
 
 		final Class entityClass = rootClass.getMappedClass();
 		final Class attributeDeclarer; // what class is the declarer of the composite pk attributes
 		CompositeNestedGeneratedValueGenerator.GenerationContextLocator locator;
 
 		// IMPL NOTE : See the javadoc discussion on CompositeNestedGeneratedValueGenerator wrt the
 		//		various scenarios for which we need to account here
 		if ( rootClass.getIdentifierMapper() != null ) {
 			// we have the @IdClass / <composite-id mapped="true"/> case
 			attributeDeclarer = resolveComponentClass();
 		}
 		else if ( rootClass.getIdentifierProperty() != null ) {
 			// we have the "@EmbeddedId" / <composite-id name="idName"/> case
 			attributeDeclarer = resolveComponentClass();
 		}
 		else {
 			// we have the "straight up" embedded (again the hibernate term) component identifier
 			attributeDeclarer = entityClass;
 		}
 
 		locator = new StandardGenerationContextLocator( rootClass.getEntityName() );
 		final CompositeNestedGeneratedValueGenerator generator = new CompositeNestedGeneratedValueGenerator( locator );
 
 		Iterator itr = getPropertyIterator();
 		while ( itr.hasNext() ) {
 			final Property property = (Property) itr.next();
 			if ( property.getValue().isSimpleValue() ) {
 				final SimpleValue value = (SimpleValue) property.getValue();
 
 				if ( DEFAULT_ID_GEN_STRATEGY.equals( value.getIdentifierGeneratorStrategy() ) ) {
 					// skip any 'assigned' generators, they would have been handled by
 					// the StandardGenerationContextLocator
 					continue;
 				}
 
 				final IdentifierGenerator valueGenerator = value.createIdentifierGenerator(
 						identifierGeneratorFactory,
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						rootClass
 				);
 				generator.addGeneratedValuePlan(
 						new ValueGenerationPlan(
 								property.getName(),
 								valueGenerator,
 								injector( property, attributeDeclarer )
 						)
 				);
 			}
 		}
 		return generator;
 	}
 
 	private Setter injector(Property property, Class attributeDeclarer) {
 		return property.getPropertyAccessor( attributeDeclarer )
 				.getSetter( attributeDeclarer, property.getName() );
 	}
 
 	private Class resolveComponentClass() {
 		try {
 			return getComponentClass();
 		}
 		catch ( Exception e ) {
 			return null;
 		}
 	}
 
 	public static class StandardGenerationContextLocator
 			implements CompositeNestedGeneratedValueGenerator.GenerationContextLocator {
 		private final String entityName;
 
 		public StandardGenerationContextLocator(String entityName) {
 			this.entityName = entityName;
 		}
 
+		@Override
 		public Serializable locateGenerationContext(SessionImplementor session, Object incomingObject) {
 			return session.getEntityPersister( entityName, incomingObject ).getIdentifier( incomingObject, session );
 		}
 	}
 
 	public static class ValueGenerationPlan implements CompositeNestedGeneratedValueGenerator.GenerationPlan {
 		private final String propertyName;
 		private final IdentifierGenerator subGenerator;
 		private final Setter injector;
 
 		public ValueGenerationPlan(
 				String propertyName,
 				IdentifierGenerator subGenerator,
 				Setter injector) {
 			this.propertyName = propertyName;
 			this.subGenerator = subGenerator;
 			this.injector = injector;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void execute(SessionImplementor session, Object incomingObject, Object injectionContext) {
 			final Object generatedValue = subGenerator.generate( session, incomingObject );
 			injector.set( injectionContext, generatedValue, session.getFactory() );
 		}
 
+		@Override
 		public void registerPersistentGenerators(Map generatorMap) {
 			if ( PersistentIdentifierGenerator.class.isInstance( subGenerator ) ) {
 				generatorMap.put( ( (PersistentIdentifierGenerator) subGenerator ).generatorKey(), subGenerator );
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/FetchProfile.java b/hibernate-core/src/main/java/org/hibernate/mapping/FetchProfile.java
index 028a17f56f..06448b1543 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/FetchProfile.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/FetchProfile.java
@@ -1,141 +1,137 @@
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
 package org.hibernate.mapping;
 import java.util.LinkedHashSet;
 
 /**
  * A fetch profile allows a user to dynamically modify the fetching strategy used for particular associations at
  * runtime, whereas that information was historically only statically defined in the metadata.
  * <p/>
  * This class represent the data as it is defined in their metadata.
  *
  * @author Steve Ebersole
  *
  * @see org.hibernate.engine.profile.FetchProfile
  */
 public class FetchProfile {
 	private final String name;
 	private final MetadataSource source;
 	private LinkedHashSet<Fetch> fetches = new LinkedHashSet<Fetch>();
 
 	/**
 	 * Create a fetch profile representation.
 	 *
 	 * @param name The name of the fetch profile.
 	 * @param source The source of the fetch profile (where was it defined).
 	 */
 	public FetchProfile(String name, MetadataSource source) {
 		this.name = name;
 		this.source = source;
 	}
 
 	/**
 	 * Retrieve the name of the fetch profile.
 	 *
 	 * @return The profile name
 	 */
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * Retrieve the fetch profile source.
 	 *
 	 * @return The profile source.
 	 */
 	public MetadataSource getSource() {
 		return source;
 	}
 
 	/**
 	 * Retrieve the fetches associated with this profile
 	 *
 	 * @return The fetches associated with this profile.
 	 */
 	public LinkedHashSet<Fetch> getFetches() {
 		return fetches;
 	}
 
 	/**
 	 * Adds a fetch to this profile.
 	 *
 	 * @param entity The entity which contains the association to be fetched
 	 * @param association The association to fetch
 	 * @param style The style of fetch t apply
 	 */
 	public void addFetch(String entity, String association, String style) {
 		fetches.add( new Fetch( entity, association, style ) );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
 
 		FetchProfile that = ( FetchProfile ) o;
 
 		return name.equals( that.name );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int hashCode() {
 		return name.hashCode();
 	}
 
 
 	/**
 	 * Defines an individual association fetch within the given profile.
 	 */
 	public static class Fetch {
 		private final String entity;
 		private final String association;
 		private final String style;
 
 		public Fetch(String entity, String association, String style) {
 			this.entity = entity;
 			this.association = association;
 			this.style = style;
 		}
 
 		public String getEntity() {
 			return entity;
 		}
 
 		public String getAssociation() {
 			return association;
 		}
 
 		public String getStyle() {
 			return style;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Formula.java b/hibernate-core/src/main/java/org/hibernate/mapping/Formula.java
index 9bdbe798fe..6f43e825a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Formula.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Formula.java
@@ -1,87 +1,88 @@
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
 package org.hibernate.mapping;
+
 import java.io.Serializable;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.sql.Template;
 
 /**
  * A formula is a derived column value
  * @author Gavin King
  */
 public class Formula implements Selectable, Serializable {
-	private static int formulaUniqueInteger=0;
+	private static int formulaUniqueInteger;
 
 	private String formula;
 	private int uniqueInteger;
 
 	public Formula() {
 		uniqueInteger = formulaUniqueInteger++;
 	}
 
 	@Override
 	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {
 		return Template.renderWhereStringTemplate(formula, dialect, functionRegistry);
 	}
 
 	@Override
 	public String getText(Dialect dialect) {
 		return getFormula();
 	}
 
 	@Override
 	public String getText() {
 		return getFormula();
 	}
 
 	@Override
 	public String getAlias(Dialect dialect) {
 		return "formula" + Integer.toString(uniqueInteger) + '_';
 	}
 
 	@Override
 	public String getAlias(Dialect dialect, Table table) {
 		return getAlias(dialect);
 	}
 
 	public String getFormula() {
 		return formula;
 	}
 
 	public void setFormula(String string) {
 		formula = string;
 	}
 
 	@Override
 	public boolean isFormula() {
 		return true;
 	}
 
 	@Override
 	public String toString() {
 		return this.getClass().getName() + "( " + formula + " )";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/IndexBackref.java b/hibernate-core/src/main/java/org/hibernate/mapping/IndexBackref.java
index 8a15df8f92..752bb93083 100755
--- a/hibernate-core/src/main/java/org/hibernate/mapping/IndexBackref.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/IndexBackref.java
@@ -1,69 +1,71 @@
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
 package org.hibernate.mapping;
+
 import org.hibernate.property.IndexPropertyAccessor;
 import org.hibernate.property.PropertyAccessor;
 
 /**
  * @author Gavin King
  */
 public class IndexBackref extends Property {
 	private String collectionRole;
 	private String entityName;
-	
+
+	@Override
 	public boolean isBackRef() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isSynthetic() {
 		return true;
 	}
 
 	public String getCollectionRole() {
 		return collectionRole;
 	}
 
 	public void setCollectionRole(String collectionRole) {
 		this.collectionRole = collectionRole;
 	}
 
+	@Override
 	public boolean isBasicPropertyAccessor() {
 		return false;
 	}
 
+	@Override
 	public PropertyAccessor getPropertyAccessor(Class clazz) {
 		return new IndexPropertyAccessor(collectionRole, entityName);
 	}
 	
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public void setEntityName(String entityName) {
 		this.entityName = entityName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index 7a3ca06838..00aa77fcbb 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,859 +1,859 @@
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
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 public class Table implements RelationalModel, Serializable {
 
 	private String name;
 	private String schema;
 	private String catalog;
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map foreignKeys = new LinkedHashMap();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private boolean quoted;
 	private boolean schemaQuoted;
 	private boolean catalogQuoted;
 	private List checkConstraints = new ArrayList();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables;
 	private String comment;
 	
 	static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk.columns.equals( columns ) &&
 					fkk.referencedClassName.equals( referencedClassName ) && fkk.referencedColumns
 					.equals( referencedColumns );
 		}
 	}
 
 	public Table() { }
 
 	public Table(String name) {
 		this();
 		setName( name );
 	}
 
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * returns quoted name as it would be in the mapping file.
 	 */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return quoted ?
 				dialect.openQuote() + name + dialect.closeQuote() :
 				name;
 	}
 
 	/**
 	 * returns quoted name as it is in the mapping file.
 	 */
 	public String getQuotedSchema() {
 		return schemaQuoted ?
 				"`" + schema + "`" :
 				schema;
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schemaQuoted ?
 				dialect.openQuote() + schema + dialect.closeQuote() :
 				schema;
 	}
 
 	public String getQuotedCatalog() {
 		return catalogQuoted ?
 				"`" + catalog + "`" :
 				catalog;
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalogQuoted ?
 				dialect.openQuote() + catalog + dialect.closeQuote() :
 				catalog;
 	}
 
 	public void setName(String name) {
 		if ( name.charAt( 0 ) == '`' ) {
 			quoted = true;
 			this.name = name.substring( 1, name.length() - 1 );
 		}
 		else {
 			this.name = name;
 		}
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
 	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
-	private int sizeOfUniqueKeyMapOnLastCleanse = 0;
+	private int sizeOfUniqueKeyMapOnLastCleanse;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result
 			+ ((catalog == null) ? 0 : isCatalogQuoted() ? catalog.hashCode() : catalog.toLowerCase().hashCode());
 		result = prime * result + ((name == null) ? 0 : isQuoted() ? name.hashCode() : name.toLowerCase().hashCode());
 		result = prime * result
 			+ ((schema == null) ? 0 : isSchemaQuoted() ? schema.hashCode() : schema.toLowerCase().hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return isQuoted() ? name.equals(table.getName()) : name.equalsIgnoreCase(table.getName())
 			&& ((schema == null && table.getSchema() != null) ? false : (schema == null) ? true : isSchemaQuoted() ? schema.equals(table.getSchema()) : schema.equalsIgnoreCase(table.getSchema()))
 			&& ((catalog == null && table.getCatalog() != null) ? false : (catalog == null) ? true : isCatalogQuoted() ? catalog.equals(table.getCatalog()) : catalog.equalsIgnoreCase(table.getCatalog()));
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase()
 						.startsWith( columnInfo.getTypeName().toLowerCase() )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase() +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
 									String defaultSchema)
 			throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		
 		while ( iter.hasNext() ) {
 			Column column = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( column.getName() );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
 					String keyName = Constraint.generateName( "UK_", this, column );
 					UniqueKey uk = getOrCreateUniqueKey( keyName );
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
 							.getColumnDefinitionUniquenessFragment( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				alter.append( dialect.getAddColumnSuffixString() );
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlTemporaryTableCreateString(Dialect dialect, Mapping mapping) throws HibernateException {
 		StringBuilder buffer = new StringBuilder( dialect.getCreateTemporaryTableString() )
 				.append( ' ' )
 				.append( name )
 				.append( " (" );
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Column column = (Column) itr.next();
 			buffer.append( column.getQuotedName( dialect ) ).append( ' ' );
 			buffer.append( column.getSqlType( dialect, mapping ) );
 			if ( column.isNullable() ) {
 				buffer.append( dialect.getNullColumnString() );
 			}
 			else {
 				buffer.append( " not null" );
 			}
 			if ( itr.hasNext() ) {
 				buffer.append( ", " );
 			}
 		}
 		buffer.append( ") " );
 		buffer.append( dialect.getCreateTemporaryTablePostfix() );
 		return buffer.toString();
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
 				String keyName = Constraint.generateName( "UK_", this, col );
 				UniqueKey uk = getOrCreateUniqueKey( keyName );
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
 						.getColumnDefinitionUniquenessFragment( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		buf.append( dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			Iterator chiter = checkConstraints.iterator();
 			while ( chiter.hasNext() ) {
 				buf.append( ", check (" )
 						.append( chiter.next() )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return dialect.getDropTableString( getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = Constraint.generateName( "UK_", this, keyColumns );
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
 		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName,
 									   List referencedColumns) {
 		Object key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = (ForeignKey) foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			fk.setTable( this );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 			
 			if ( keyName != null ) {
 				fk.setName( keyName );
 			}
 			else {
 				fk.setName( Constraint.generateName( fk.generatedConstraintNamePrefix(),
 						this, keyColumns ) );
 			}
 			
 			foreignKeys.put( key, fk );
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 
 	public String getSchema() {
 		return schema;
 	}
 
 	public void setSchema(String schema) {
 		if ( schema != null && schema.charAt( 0 ) == '`' ) {
 			schemaQuoted = true;
 			this.schema = schema.substring( 1, schema.length() - 1 );
 		}
 		else {
 			this.schema = schema;
 		}
 	}
 
 	public String getCatalog() {
 		return catalog;
 	}
 
 	public void setCatalog(String catalog) {
 		if ( catalog != null && catalog.charAt( 0 ) == '`' ) {
 			catalogQuoted = true;
 			this.catalog = catalog.substring( 1, catalog.length() - 1 );
 		}
 		else {
 			this.catalog = catalog;
 		}
 	}
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public boolean isSchemaQuoted() {
 		return schemaQuoted;
 	}
 	public boolean isCatalogQuoted() {
 		return catalogQuoted;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public void setQuoted(boolean quoted) {
 		this.quoted = quoted;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() + "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() + "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				StringBuilder buf = new StringBuilder()
 						.append( "comment on table " )
 						.append( tableName )
 						.append( " is '" )
 						.append( comment )
 						.append( "'" );
 				comments.add( buf.toString() );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					StringBuilder buf = new StringBuilder()
 							.append( "comment on column " )
 							.append( tableName )
 							.append( '.' )
 							.append( column.getQuotedName( dialect ) )
 							.append( " is '" )
 							.append( columnComment )
 							.append( "'" );
 					comments.add( buf.toString() );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/DerivedValue.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/DerivedValue.java
index 13e8e39bdc..2f39332ac7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/DerivedValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/DerivedValue.java
@@ -1,63 +1,58 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.metamodel.relational;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Models a value expression.  It is the result of a <tt>formula</tt> mapping.
  *
  * @author Steve Ebersole
  */
 public class DerivedValue extends AbstractSimpleValue {
 	private final String expression;
 
 	public DerivedValue(TableSpecification table, int position, String expression) {
 		super( table, position );
 		this.expression = expression;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String toLoggableString() {
 		return getTable().toLoggableString() + ".{derived-column}";
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	public String getAlias(Dialect dialect) {
 		return "formula" + Integer.toString( getPosition() ) + '_';
 	}
 
 	/**
 	 * Get the value expression.
 	 * @return the value expression
 	 */
 	public String getExpression() {
 		return expression;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
index 02efca32b2..3c347654c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/NamedParameterSpecification.java
@@ -1,85 +1,84 @@
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
 package org.hibernate.param;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
  * Parameter bind specification for an explicit named parameter.
  *
  * @author Steve Ebersole
  */
 public class NamedParameterSpecification extends AbstractExplicitParameterSpecification {
 	private final String name;
 
 	/**
 	 * Constructs a named parameter bind specification.
 	 *
 	 * @param sourceLine See {@link #getSourceLine()}
 	 * @param sourceColumn See {@link #getSourceColumn()} 
 	 * @param name The named parameter name.
 	 */
 	public NamedParameterSpecification(int sourceLine, int sourceColumn, String name) {
 		super( sourceLine, sourceColumn );
 		this.name = name;
 	}
 
 	/**
 	 * Bind the appropriate value into the given statement at the specified position.
 	 *
 	 * @param statement The statement into which the value should be bound.
 	 * @param qp The defined values for the current query execution.
 	 * @param session The session against which the current execution is occuring.
 	 * @param position The position from which to start binding value(s).
 	 *
 	 * @return The number of sql bind positions "eaten" by this bind operation.
 	 */
+	@Override
 	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
 	        throws SQLException {
 		TypedValue typedValue = qp.getNamedParameters().get( name );
 		typedValue.getType().nullSafeSet( statement, typedValue.getValue(), position, session );
 		return typedValue.getType().getColumnSpan( session.getFactory() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String renderDisplayInfo() {
 		return "name=" + name + ", expectedType=" + getExpectedType();
 	}
 
 	/**
 	 * Getter for property 'name'.
 	 *
 	 * @return Value for property 'name'.
 	 */
 	public String getName() {
 		return name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java b/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
index 24c047b96d..60a6d6042d 100644
--- a/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/param/PositionalParameterSpecification.java
@@ -1,86 +1,85 @@
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
 package org.hibernate.param;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Parameter bind specification for an explicit  positional (or ordinal) parameter.
  *
  * @author Steve Ebersole
  */
 public class PositionalParameterSpecification extends AbstractExplicitParameterSpecification  {
 	private final int hqlPosition;
 
 	/**
 	 * Constructs a position/ordinal parameter bind specification.
 	 *
 	 * @param sourceLine See {@link #getSourceLine()}
 	 * @param sourceColumn See {@link #getSourceColumn()}
 	 * @param hqlPosition The position in the source query, relative to the other source positional parameters.
 	 */
 	public PositionalParameterSpecification(int sourceLine, int sourceColumn, int hqlPosition) {
 		super( sourceLine, sourceColumn );
 		this.hqlPosition = hqlPosition;
 	}
 
 	/**
 	 * Bind the appropriate value into the given statement at the specified position.
 	 *
 	 * @param statement The statement into which the value should be bound.
 	 * @param qp The defined values for the current query execution.
 	 * @param session The session against which the current execution is occuring.
 	 * @param position The position from which to start binding value(s).
 	 *
 	 * @return The number of sql bind positions "eaten" by this bind operation.
 	 */
+	@Override
 	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position) throws SQLException {
 		Type type = qp.getPositionalParameterTypes()[hqlPosition];
 		Object value = qp.getPositionalParameterValues()[hqlPosition];
 
 		type.nullSafeSet( statement, value, position, session );
 		return type.getColumnSpan( session.getFactory() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String renderDisplayInfo() {
 		return "ordinal=" + hqlPosition + ", expectedType=" + getExpectedType();
 	}
 
 	/**
 	 * Getter for property 'hqlPosition'.
 	 *
 	 * @return Value for property 'hqlPosition'.
 	 */
 	public int getHqlPosition() {
 		return hqlPosition;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 3af96c7b4f..70e0647ce6 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2143 +1,2213 @@
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
 import java.util.Set;
 
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
 
+	@Override
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
 
+	@Override
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
 
+	@Override
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
+	@Override
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
+	@Override
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
+	@Override
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
+	@Override
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
+	@Override
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
+	@Override
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
+	@Override
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
+	@Override
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
 
+	@Override
 	public Type getKeyType() {
 		return keyType;
 	}
 
+	@Override
 	public Type getIndexType() {
 		return indexType;
 	}
 
+	@Override
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
-	 * Return the element class of an array, or null otherwise
+	 * Return the element class of an array, or null otherwise.  needed by arrays
 	 */
-	public Class getElementClass() { // needed by arrays
+	@Override
+	public Class getElementClass() {
 		return elementClass;
 	}
 
+	@Override
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
+	@Override
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
 
+	@Override
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
+	@Override
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
 
+	@Override
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
+	@Override
 	public boolean isArray() {
 		return isArray;
 	}
 
+	@Override
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
+	@Override
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
+	@Override
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
+	@Override
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
+	@Override
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
+	@Override
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
 
+	@Override
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
+	@Override
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
+	@Override
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
-
 	}
 
+	@Override
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
 
+	@Override
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
+	@Override
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
+	@Override
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
+	@Override
 	public boolean isLazy() {
 		return isLazy;
 	}
 
+	@Override
 	public boolean isInverse() {
 		return isInverse;
 	}
 
+	@Override
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
+	@Override
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
 
+	@Override
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
 
+	@Override
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
 
+	@Override
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
 
+	@Override
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
+	@Override
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
+	@Override
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
+	@Override
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
+	@Override
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
+	@Override
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
+	@Override
 	public abstract boolean isManyToMany();
 
+	@Override
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
 
+	@Override
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
+	@Override
 	public String getName() {
 		return getRole();
 	}
 
+	@Override
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
+	@Override
 	public boolean isCollection() {
 		return true;
 	}
 
+	@Override
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
+	@Override
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
 
+	@Override
 	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException {
 		if ( collection.hasQueuedOperations() ) {
 			doProcessQueuedOps( collection, key, session );
 		}
 	}
 
 	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException;
 
+	@Override
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
+	@Override
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	protected String filterFragment(String alias, Set<String> treatAsDeclarations) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
+	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(
 			String alias,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
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
 
+	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
+	@Override
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
+	@Override
 	public String getNodeName() {
 		return nodeName;
 	}
 
+	@Override
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
+	@Override
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
 
+	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
+	@Override
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return filterHelper.isAffectedBy( session.getEnabledFilters() ) ||
 				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
+	@Override
 	public boolean isMutable() {
 		return isMutable;
 	}
 
+	@Override
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String[] rawAliases = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String[] result = new String[rawAliases.length];
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
 
+	@Override
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
 
+	@Override
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
+	@Override
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
 
+	@Override
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
 
+	@Override
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
 
+	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
+	@Override
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
 				if ( !getType().isEntityType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as entity" );
 				}
 				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
 			}
 
 			@Override
 			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as composite" );
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
 
 			@Override
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
 					throw new IllegalStateException( "Cannot treat collection index type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
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
 					throw new IllegalStateException( "Cannot treat collection element type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( !getType().isEntityType() ) {
 					throw new IllegalStateException( "Cannot treat collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
 			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
index d4872b15b8..c663adac06 100755
--- a/hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/BackrefPropertyAccessor.java
@@ -1,178 +1,155 @@
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
 package org.hibernate.property;
 import java.io.Serializable;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Represents a "back-reference" to the id of a collection owner.  A "back-reference" is pertinent in mapping scenarios
  * where we have a uni-directional one-to-many association in which only the many side is mapped.  In this case it is
  * the collection itself which is responsible for the FK value.
  * <p/>
  * In this scenario, the one side has no inherent knowledge of its "owner".  So we introduce a synthetic property into
  * the one side to represent the association; a so-called back-reference.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class BackrefPropertyAccessor implements PropertyAccessor {
 
 	private final String propertyName;
 	private final String entityName;
 
 	// cache these since they are stateless
 	private final BackrefSetter setter; // this one could even be static...
 	private final BackrefGetter getter;
 
 	/**
 	 * A placeholder for a property value, indicating that
 	 * we don't know the value of the back reference
 	 */
 	public static final Serializable UNKNOWN = new Serializable() {
+		@Override
 		public String toString() {
 			return "<unknown>";
 		}
 
 		public Object readResolve() {
 			return UNKNOWN;
 		}
 	};
 
 	/**
 	 * Constructs a new instance of BackrefPropertyAccessor.
 	 *
 	 * @param collectionRole The collection role which this back ref references.
 	 * @param entityName The owner's entity name.
 	 */
 	public BackrefPropertyAccessor(String collectionRole, String entityName) {
 		this.propertyName = collectionRole.substring( entityName.length() + 1 );
 		this.entityName = entityName;
 
 		this.setter = new BackrefSetter();
 		this.getter = new BackrefGetter();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Setter getSetter(Class theClass, String propertyName) {
 		return setter;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Getter getGetter(Class theClass, String propertyName) {
 		return getter;
 	}
 
 
 	/**
 	 * Internal implementation of a property setter specific to these back-ref properties.
 	 */
 	public static final class BackrefSetter implements Setter {
-
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void set(Object target, Object value, SessionFactoryImplementor factory) {
 			// this page intentionally left blank :)
 		}
 
 	}
 
 
 	/**
 	 * Internal implementation of a property getter specific to these back-ref properties.
 	 */
 	public class BackrefGetter implements Getter {
-
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
 			if ( session == null ) {
 				return UNKNOWN;
 			}
 			else {
 				return session.getPersistenceContext().getOwnerId( entityName, propertyName, target, mergeMap );
 			}
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Member getMember() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object get(Object target) {
 			return UNKNOWN;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class getReturnType() {
 			return Object.class;
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
index 7e8286541c..ea835a736f 100644
--- a/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/BasicPropertyAccessor.java
@@ -1,390 +1,380 @@
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
 package org.hibernate.property;
 
 import java.beans.Introspector;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyAccessException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.PropertySetterAccessException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * Accesses property values via a get/set pair, which may be nonpublic.
  * The default (and recommended strategy).
  *
  * @author Gavin King
  */
 public class BasicPropertyAccessor implements PropertyAccessor {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicPropertyAccessor.class.getName());
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( BasicPropertyAccessor.class );
 
 	public static final class BasicSetter implements Setter {
 		private Class clazz;
 		private final transient Method method;
 		private final String propertyName;
 
 		private BasicSetter(Class clazz, Method method, String propertyName) {
 			this.clazz=clazz;
 			this.method=method;
 			this.propertyName=propertyName;
 		}
 
+		@Override
 		public void set(Object target, Object value, SessionFactoryImplementor factory)
 		throws HibernateException {
 			try {
 				method.invoke( target, value );
 			}
 			catch (NullPointerException npe) {
 				if ( value==null && method.getParameterTypes()[0].isPrimitive() ) {
 					throw new PropertyAccessException(
 							npe,
 							"Null value was assigned to a property of primitive type",
 							true,
 							clazz,
 							propertyName
 						);
 				}
 				else {
 					throw new PropertyAccessException(
 							npe,
 							"NullPointerException occurred while calling",
 							true,
 							clazz,
 							propertyName
 						);
 				}
 			}
 			catch (InvocationTargetException ite) {
 				throw new PropertyAccessException(
 						ite,
 						"Exception occurred inside",
 						true,
 						clazz,
 						propertyName
 					);
 			}
 			catch (IllegalAccessException iae) {
 				throw new PropertyAccessException(
 						iae,
 						"IllegalAccessException occurred while calling",
 						true,
 						clazz,
 						propertyName
 					);
 				//cannot occur
 			}
 			catch (IllegalArgumentException iae) {
 				if ( value==null && method.getParameterTypes()[0].isPrimitive() ) {
 					throw new PropertyAccessException(
 							iae,
 							"Null value was assigned to a property of primitive type",
 							true,
 							clazz,
 							propertyName
 						);
 				}
 				else {
 					final Class expectedType = method.getParameterTypes()[0];
 					LOG.illegalPropertySetterArgument( clazz.getName(), propertyName );
 					LOG.expectedType( expectedType.getName(), value == null ? null : value.getClass().getName() );
 					throw new PropertySetterAccessException(
 							iae,
 							clazz,
 							propertyName,
 							expectedType,
 							target,
 							value
 						);
 				}
 			}
 		}
 
+		@Override
 		public Method getMethod() {
 			return method;
 		}
 
+		@Override
 		public String getMethodName() {
 			return method.getName();
 		}
 
 		Object readResolve() {
 			return createSetter(clazz, propertyName);
 		}
 
 		@Override
         public String toString() {
 			return "BasicSetter(" + clazz.getName() + '.' + propertyName + ')';
 		}
 	}
 
 	public static final class BasicGetter implements Getter {
 		private Class clazz;
 		private final transient Method method;
 		private final String propertyName;
 
 		private BasicGetter(Class clazz, Method method, String propertyName) {
 			this.clazz=clazz;
 			this.method=method;
 			this.propertyName=propertyName;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object get(Object target) throws HibernateException {
 			try {
 				return method.invoke( target, (Object[]) null );
 			}
 			catch (InvocationTargetException ite) {
 				throw new PropertyAccessException(
 						ite,
 						"Exception occurred inside",
 						false,
 						clazz,
 						propertyName
 					);
 			}
 			catch (IllegalAccessException iae) {
 				throw new PropertyAccessException(
 						iae,
 						"IllegalAccessException occurred while calling",
 						false,
 						clazz,
 						propertyName
 					);
 				//cannot occur
 			}
 			catch (IllegalArgumentException iae) {
                 LOG.illegalPropertyGetterArgument(clazz.getName(), propertyName);
 				throw new PropertyAccessException(
 						iae,
 						"IllegalArgumentException occurred calling",
 						false,
 						clazz,
 						propertyName
 					);
 			}
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
 			return get( target );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class getReturnType() {
 			return method.getReturnType();
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Member getMember() {
 			return method;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return method;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return method.getName();
 		}
 
 		@Override
         public String toString() {
 			return "BasicGetter(" + clazz.getName() + '.' + propertyName + ')';
 		}
 
 		Object readResolve() {
 			return createGetter(clazz, propertyName);
 		}
 	}
 
 
-	public Setter getSetter(Class theClass, String propertyName)
-	throws PropertyNotFoundException {
+	@Override
+	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		return createSetter(theClass, propertyName);
 	}
 
-	private static Setter createSetter(Class theClass, String propertyName)
-	throws PropertyNotFoundException {
+	private static Setter createSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		BasicSetter result = getSetterOrNull(theClass, propertyName);
 		if (result==null) {
 			throw new PropertyNotFoundException(
 					"Could not find a setter for property " +
 					propertyName +
 					" in class " +
 					theClass.getName()
 				);
 		}
 		return result;
 	}
 
 	private static BasicSetter getSetterOrNull(Class theClass, String propertyName) {
 
 		if (theClass==Object.class || theClass==null) return null;
 
 		Method method = setterMethod(theClass, propertyName);
 
 		if (method!=null) {
 			if ( !ReflectHelper.isPublic(theClass, method) ) method.setAccessible(true);
 			return new BasicSetter(theClass, method, propertyName);
 		}
 		else {
 			BasicSetter setter = getSetterOrNull( theClass.getSuperclass(), propertyName );
 			if (setter==null) {
 				Class[] interfaces = theClass.getInterfaces();
 				for ( int i=0; setter==null && i<interfaces.length; i++ ) {
 					setter=getSetterOrNull( interfaces[i], propertyName );
 				}
 			}
 			return setter;
 		}
 
 	}
 
 	private static Method setterMethod(Class theClass, String propertyName) {
-
 		BasicGetter getter = getGetterOrNull(theClass, propertyName);
 		Class returnType = (getter==null) ? null : getter.getReturnType();
 
 		Method[] methods = theClass.getDeclaredMethods();
 		Method potentialSetter = null;
 		for ( Method method : methods ) {
 			final String methodName = method.getName();
 
 			if ( method.getParameterTypes().length == 1 && methodName.startsWith( "set" ) ) {
 				String testStdMethod = Introspector.decapitalize( methodName.substring( 3 ) );
 				String testOldMethod = methodName.substring( 3 );
 				if ( testStdMethod.equals( propertyName ) || testOldMethod.equals( propertyName ) ) {
 					potentialSetter = method;
 					if ( returnType == null || method.getParameterTypes()[0].equals( returnType ) ) {
 						return potentialSetter;
 					}
 				}
 			}
 		}
 		return potentialSetter;
 	}
 
+	@Override
 	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		return createGetter(theClass, propertyName);
 	}
 
 	public static Getter createGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		BasicGetter result = getGetterOrNull(theClass, propertyName);
 		if (result==null) {
 			throw new PropertyNotFoundException(
 					"Could not find a getter for " +
 					propertyName +
 					" in class " +
 					theClass.getName()
 			);
 		}
 		return result;
 	}
 
 	private static BasicGetter getGetterOrNull(Class theClass, String propertyName) {
 		if (theClass==Object.class || theClass==null) {
 			return null;
 		}
 
 		Method method = getterMethod(theClass, propertyName);
 
 		if (method!=null) {
 			if ( !ReflectHelper.isPublic( theClass, method ) ) {
 				method.setAccessible(true);
 			}
 			return new BasicGetter(theClass, method, propertyName);
 		}
 		else {
 			BasicGetter getter = getGetterOrNull( theClass.getSuperclass(), propertyName );
 			if (getter==null) {
 				Class[] interfaces = theClass.getInterfaces();
 				for ( int i=0; getter==null && i<interfaces.length; i++ ) {
 					getter=getGetterOrNull( interfaces[i], propertyName );
 				}
 			}
 			return getter;
 		}
 	}
 
 	private static Method getterMethod(Class theClass, String propertyName) {
 		Method[] methods = theClass.getDeclaredMethods();
 		for ( Method method : methods ) {
 			// if the method has parameters, skip it
 			if ( method.getParameterTypes().length != 0 ) {
 				continue;
 			}
 			// if the method is a "bridge", skip it
 			if ( method.isBridge() ) {
 				continue;
 			}
 
 			final String methodName = method.getName();
 
 			// try "get"
 			if ( methodName.startsWith( "get" ) ) {
 				String testStdMethod = Introspector.decapitalize( methodName.substring( 3 ) );
 				String testOldMethod = methodName.substring( 3 );
 				if ( testStdMethod.equals( propertyName ) || testOldMethod.equals( propertyName ) ) {
 					return method;
 				}
 			}
 
 			// if not "get", then try "is"
 			if ( methodName.startsWith( "is" ) ) {
 				String testStdMethod = Introspector.decapitalize( methodName.substring( 2 ) );
 				String testOldMethod = methodName.substring( 2 );
 				if ( testStdMethod.equals( propertyName ) || testOldMethod.equals( propertyName ) ) {
 					return method;
 				}
 			}
 		}
 
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
index 67b5670f80..1858960b27 100644
--- a/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/DirectPropertyAccessor.java
@@ -1,205 +1,189 @@
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
 package org.hibernate.property;
 import java.lang.reflect.Field;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyAccessException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * Accesses fields directly.
  * @author Gavin King
  */
 public class DirectPropertyAccessor implements PropertyAccessor {
 
 	public static final class DirectGetter implements Getter {
 		private final transient Field field;
 		private final Class clazz;
 		private final String name;
 
 		DirectGetter(Field field, Class clazz, String name) {
 			this.field = field;
 			this.clazz = clazz;
 			this.name = name;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object get(Object target) throws HibernateException {
 			try {
 				return field.get(target);
 			}
 			catch (Exception e) {
 				throw new PropertyAccessException(e, "could not get a field value by reflection", false, clazz, name);
 			}
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
 			return get( target );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Member getMember() {
 			return field;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class getReturnType() {
 			return field.getType();
 		}
 
 		Object readResolve() {
 			return new DirectGetter( getField(clazz, name), clazz, name );
 		}
-		
+
+		@Override
 		public String toString() {
 			return "DirectGetter(" + clazz.getName() + '.' + name + ')';
 		}
 	}
 
 	public static final class DirectSetter implements Setter {
 		private final transient Field field;
 		private final Class clazz;
 		private final String name;
 		DirectSetter(Field field, Class clazz, String name) {
 			this.field = field;
 			this.clazz = clazz;
 			this.name = name;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException {
 			try {
 				field.set(target, value);
 			}
 			catch (Exception e) {
 				if(value == null && field.getType().isPrimitive()) {
 					throw new PropertyAccessException(
 							e, 
 							"Null value was assigned to a property of primitive type", 
 							true, 
 							clazz, 
 							name
 						);					
 				} else {
 					throw new PropertyAccessException(e, "could not set a field value by reflection", true, clazz, name);
 				}
 			}
 		}
 
+		@Override
 		public String toString() {
 			return "DirectSetter(" + clazz.getName() + '.' + name + ')';
 		}
 		
 		Object readResolve() {
 			return new DirectSetter( getField(clazz, name), clazz, name );
 		}
 	}
 
 	private static Field getField(Class clazz, String name) throws PropertyNotFoundException {
 		if ( clazz==null || clazz==Object.class ) {
 			throw new PropertyNotFoundException("field not found: " + name); 
 		}
 		Field field;
 		try {
 			field = clazz.getDeclaredField(name);
 		}
 		catch (NoSuchFieldException nsfe) {
 			field = getField( clazz, clazz.getSuperclass(), name );
 		}
 		if ( !ReflectHelper.isPublic(clazz, field) ) field.setAccessible(true);
 		return field;
 	}
 
 	private static Field getField(Class root, Class clazz, String name) throws PropertyNotFoundException {
 		if ( clazz==null || clazz==Object.class ) {
 			throw new PropertyNotFoundException("field [" + name + "] not found on " + root.getName()); 
 		}
 		Field field;
 		try {
 			field = clazz.getDeclaredField(name);
 		}
 		catch (NoSuchFieldException nsfe) {
 			field = getField( root, clazz.getSuperclass(), name );
 		}
 		if ( !ReflectHelper.isPublic(clazz, field) ) field.setAccessible(true);
 		return field;
 	}
-	
-	public Getter getGetter(Class theClass, String propertyName)
-		throws PropertyNotFoundException {
+
+	@Override
+	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		return new DirectGetter( getField(theClass, propertyName), theClass, propertyName );
 	}
 
-	public Setter getSetter(Class theClass, String propertyName)
-		throws PropertyNotFoundException {
+	@Override
+	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		return new DirectSetter( getField(theClass, propertyName), theClass, propertyName );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
index 96127f0c52..4cd4f9cce4 100755
--- a/hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/EmbeddedPropertyAccessor.java
@@ -1,138 +1,119 @@
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
 package org.hibernate.property;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * @author Gavin King
  */
 public class EmbeddedPropertyAccessor implements PropertyAccessor {
-	
+
 	public static final class EmbeddedGetter implements Getter {
 		private final Class clazz;
 		
 		EmbeddedGetter(Class clazz) {
 			this.clazz = clazz;
 		}
-		
-		/**
-		 * {@inheritDoc}
-		 */
+
+		@Override
 		public Object get(Object target) throws HibernateException {
 			return target;
 		}
-		
-		/**
-		 * {@inheritDoc}
-		 */
+
+		@Override
 		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
 			return get( target );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Member getMember() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class getReturnType() {
 			return clazz;
 		}
-		
+
+		@Override
 		public String toString() {
 			return "EmbeddedGetter(" + clazz.getName() + ')';
 		}
 	}
 
 	public static final class EmbeddedSetter implements Setter {
 		private final Class clazz;
 		
 		EmbeddedSetter(Class clazz) {
 			this.clazz = clazz;
 		}
-		
-		/**
-		 * {@inheritDoc}
-		 */
+
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void set(Object target, Object value, SessionFactoryImplementor factory) {
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String toString() {
 			return "EmbeddedSetter(" + clazz.getName() + ')';
 		}
 	}
 
-	public Getter getGetter(Class theClass, String propertyName)
-	throws PropertyNotFoundException {
+	@Override
+	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		return new EmbeddedGetter(theClass);
 	}
 
-	public Setter getSetter(Class theClass, String propertyName)
-	throws PropertyNotFoundException {
+	@Override
+	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
 		return new EmbeddedSetter(theClass);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
index 8a3f23bbce..a80b2ede45 100755
--- a/hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/IndexPropertyAccessor.java
@@ -1,139 +1,124 @@
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
 package org.hibernate.property;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Represents a "back-reference" to the index of a collection.
  *
  * @author Gavin King
  */
 public class IndexPropertyAccessor implements PropertyAccessor {
 	private final String propertyName;
 	private final String entityName;
 
 	/**
 	 * Constructs a new instance of IndexPropertyAccessor.
 	 *
 	 * @param collectionRole The collection role which this back ref references.
 	 * @param entityName The name of the entity owning the collection.
 	 */
 	public IndexPropertyAccessor(String collectionRole, String entityName) {
 		this.propertyName = collectionRole.substring( entityName.length()+1 );
 		this.entityName = entityName;
 	}
 
+	@Override
 	public Setter getSetter(Class theClass, String propertyName) {
 		return new IndexSetter();
 	}
 
+	@Override
 	public Getter getGetter(Class theClass, String propertyName) {
 		return new IndexGetter();
 	}
 
 
 	/**
 	 * The Setter implementation for index backrefs.
 	 */
 	public static final class IndexSetter implements Setter {
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void set(Object target, Object value, SessionFactoryImplementor factory) {
 			// do nothing...
 		}
-
 	}
 
 
 	/**
 	 * The Getter implementation for index backrefs.
 	 */
 	public class IndexGetter implements Getter {
+		@Override
 		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) throws HibernateException {
-			if (session==null) {
+			if ( session == null ) {
 				return BackrefPropertyAccessor.UNKNOWN;
 			}
 			else {
-				return session.getPersistenceContext()
-						.getIndexInOwner(entityName, propertyName, target, mergeMap);
+				return session.getPersistenceContext().getIndexInOwner( entityName, propertyName, target, mergeMap );
 			}
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object get(Object target)  {
 			return BackrefPropertyAccessor.UNKNOWN;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Member getMember() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class getReturnType() {
 			return Object.class;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java
index ef6e097dd1..f239999f7a 100644
--- a/hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/MapAccessor.java
@@ -1,135 +1,114 @@
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
 package org.hibernate.property;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * @author Gavin King
  */
 public class MapAccessor implements PropertyAccessor {
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Getter getGetter(Class theClass, String propertyName)
 		throws PropertyNotFoundException {
 		return new MapGetter(propertyName);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Setter getSetter(Class theClass, String propertyName)
 		throws PropertyNotFoundException {
 		return new MapSetter(propertyName);
 	}
 
 	public static final class MapSetter implements Setter {
 		private String name;
 
 		MapSetter(String name) {
 			this.name = name;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
+		@SuppressWarnings("unchecked")
 		public void set(Object target, Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
-			( (Map) target ).put(name, value);
+			( (Map) target ).put( name, value );
 		}
 
 	}
 
 	public static final class MapGetter implements Getter {
 		private String name;
 
 		MapGetter(String name) {
 			this.name = name;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Member getMember() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object get(Object target) throws HibernateException {
 			return ( (Map) target ).get(name);
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
 			return get( target );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class getReturnType() {
 			return Object.class;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java b/hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java
index e2eee4395b..3dfa3e45c8 100755
--- a/hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/property/NoopAccessor.java
@@ -1,130 +1,111 @@
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
 package org.hibernate.property;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Used to declare properties not represented at the pojo level
  * 
  * @author Michael Bartmann
  */
 public class NoopAccessor implements PropertyAccessor {
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Getter getGetter(Class arg0, String arg1) throws PropertyNotFoundException {
 		return new NoopGetter();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Setter getSetter(Class arg0, String arg1) throws PropertyNotFoundException {
 		return new NoopSetter();
 	}
 
 	/**
 	 * A Getter which will always return null. It should not be called anyway.
 	 */
 	private static class NoopGetter implements Getter {
 		/**
 		 * {@inheritDoc}
 		 * <p/>
 		 * Here we always return <tt>null</tt>
 		 */
+		@Override
 		public Object get(Object target) throws HibernateException {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object getForInsert(Object target, Map map, SessionImplementor arg1)
 				throws HibernateException {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class getReturnType() {
 			return Object.class;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Member getMember() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 	}
 
 	/**
 	 * A Setter which will just do nothing.
 	 */
 	private static class NoopSetter implements Setter {
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public void set(Object target, Object value, SessionFactoryImplementor arg2) {
 			// nothing to do
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String getMethodName() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Method getMethod() {
 			return null;
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java
index 29aae78704..9146259162 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanConstructorResultTransformer.java
@@ -1,91 +1,92 @@
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
 package org.hibernate.transform;
 import java.lang.reflect.Constructor;
 import java.util.List;
 
 import org.hibernate.QueryException;
 
 /**
  * Wraps the tuples in a constructor call.
  *
  * todo : why Alias* in the name???
  */
 public class AliasToBeanConstructorResultTransformer implements ResultTransformer {
 
 	private final Constructor constructor;
 
 	/**
 	 * Instantiates a AliasToBeanConstructorResultTransformer.
 	 *
 	 * @param constructor The contructor in which to wrap the tuples.
 	 */
 	public AliasToBeanConstructorResultTransformer(Constructor constructor) {
 		this.constructor = constructor;
 	}
 	
 	/**
 	 * Wrap the incoming tuples in a call to our configured constructor.
 	 */
+	@Override
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		try {
 			return constructor.newInstance( tuple );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( 
 					"could not instantiate class [" + constructor.getDeclaringClass().getName() + "] from tuple",
 					e
 			);
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public List transformList(List collection) {
 		return collection;
 	}
 
 	/**
 	 * Define our hashCode by our defined constructor's hasCode.
 	 *
 	 * @return Our defined ctor hashCode
 	 */
+	@Override
 	public int hashCode() {
 		return constructor.hashCode();
 	}
 
 	/**
 	 * 2 AliasToBeanConstructorResultTransformer are considered equal if they have the same
 	 * defined constructor.
 	 *
 	 * @param other The other instance to check for equality.
 	 * @return True if both have the same defined constuctor; false otherwise.
 	 */
+	@Override
 	public boolean equals(Object other) {
 		return other instanceof AliasToBeanConstructorResultTransformer
 				&& constructor.equals( ( ( AliasToBeanConstructorResultTransformer ) other ).constructor );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
index ff36ac709c..a0ca473307 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
@@ -1,161 +1,162 @@
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
 package org.hibernate.transform;
 import java.util.Arrays;
 
 import org.hibernate.HibernateException;
 import org.hibernate.property.ChainedPropertyAccessor;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 
 /**
  * Result transformer that allows to transform a result to
  * a user specified class which will be populated via setter
  * methods or fields matching the alias names.
  * <p/>
  * <pre>
  * List resultWithAliasedBean = s.createCriteria(Enrolment.class)
  * 			.createAlias("student", "st")
  * 			.createAlias("course", "co")
  * 			.setProjection( Projections.projectionList()
  * 					.add( Projections.property("co.description"), "courseDescription" )
  * 			)
  * 			.setResultTransformer( new AliasToBeanResultTransformer(StudentDTO.class) )
  * 			.list();
  * <p/>
  *  StudentDTO dto = (StudentDTO)resultWithAliasedBean.get(0);
  * 	</pre>
  *
  * @author max
  */
 public class AliasToBeanResultTransformer extends AliasedTupleSubsetResultTransformer {
 
 	// IMPL NOTE : due to the delayed population of setters (setters cached
 	// 		for performance), we really cannot properly define equality for
 	// 		this transformer
 
 	private final Class resultClass;
 	private boolean isInitialized;
 	private String[] aliases;
 	private Setter[] setters;
 
 	public AliasToBeanResultTransformer(Class resultClass) {
 		if ( resultClass == null ) {
 			throw new IllegalArgumentException( "resultClass cannot be null" );
 		}
 		isInitialized = false;
 		this.resultClass = resultClass;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
 		return false;
-	}	
+	}
 
+	@Override
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		Object result;
 
 		try {
 			if ( ! isInitialized ) {
 				initialize( aliases );
 			}
 			else {
 				check( aliases );
 			}
 			
 			result = resultClass.newInstance();
 
 			for ( int i = 0; i < aliases.length; i++ ) {
 				if ( setters[i] != null ) {
 					setters[i].set( result, tuple[i], null );
 				}
 			}
 		}
 		catch ( InstantiationException e ) {
 			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
 		}
 		catch ( IllegalAccessException e ) {
 			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
 		}
 
 		return result;
 	}
 
 	private void initialize(String[] aliases) {
 		PropertyAccessor propertyAccessor = new ChainedPropertyAccessor(
 				new PropertyAccessor[] {
 						PropertyAccessorFactory.getPropertyAccessor( resultClass, null ),
 						PropertyAccessorFactory.getPropertyAccessor( "field" )
 				}
 		);
 		this.aliases = new String[ aliases.length ];
 		setters = new Setter[ aliases.length ];
 		for ( int i = 0; i < aliases.length; i++ ) {
 			String alias = aliases[ i ];
 			if ( alias != null ) {
 				this.aliases[ i ] = alias;
 				setters[ i ] = propertyAccessor.getSetter( resultClass, alias );
 			}
 		}
 		isInitialized = true;
 	}
 
 	private void check(String[] aliases) {
 		if ( ! Arrays.equals( aliases, this.aliases ) ) {
 			throw new IllegalStateException(
 					"aliases are different from what is cached; aliases=" + Arrays.asList( aliases ) +
 							" cached=" + Arrays.asList( this.aliases ) );
 		}
 	}
 
+	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
 
 		AliasToBeanResultTransformer that = ( AliasToBeanResultTransformer ) o;
 
 		if ( ! resultClass.equals( that.resultClass ) ) {
 			return false;
 		}
 		if ( ! Arrays.equals( aliases, that.aliases ) ) {
 			return false;
 		}
 
 		return true;
 	}
 
+	@Override
 	public int hashCode() {
 		int result = resultClass.hashCode();
 		result = 31 * result + ( aliases != null ? Arrays.hashCode( aliases ) : 0 );
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
index 419952bda1..c0712536f5 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
@@ -1,78 +1,74 @@
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
 package org.hibernate.transform;
 import java.util.HashMap;
 import java.util.Map;
 
 /**
  * {@link ResultTransformer} implementation which builds a map for each "row",
  * made up  of each aliased value where the alias is the map key.
  * <p/>
  * Since this transformer is stateless, all instances would be considered equal.
  * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class AliasToEntityMapResultTransformer extends AliasedTupleSubsetResultTransformer {
 
 	public static final AliasToEntityMapResultTransformer INSTANCE = new AliasToEntityMapResultTransformer();
 
 	/**
 	 * Disallow instantiation of AliasToEntityMapResultTransformer.
 	 */
 	private AliasToEntityMapResultTransformer() {
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		Map result = new HashMap(tuple.length);
 		for ( int i=0; i<tuple.length; i++ ) {
 			String alias = aliases[i];
 			if ( alias!=null ) {
 				result.put( alias, tuple[i] );
 			}
 		}
 		return result;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
 		return false;
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
index daadf88441..11ae7ec43d 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
@@ -1,59 +1,56 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.transform;
 
-
 /**
  * An implementation of TupleSubsetResultTransformer that ignores a
  * tuple element if its corresponding alias is null.
  *
  * @author Gail Badner
  */
 public abstract class AliasedTupleSubsetResultTransformer
 		extends BasicTransformerAdapter
 		implements TupleSubsetResultTransformer {
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
 		if ( aliases == null ) {
 			throw new IllegalArgumentException( "aliases cannot be null" );
 		}
 		if ( aliases.length != tupleLength ) {
 			throw new IllegalArgumentException(
 					"aliases and tupleLength must have the same length; " +
 							"aliases.length=" + aliases.length + "tupleLength=" + tupleLength
 			);
 		}
 		boolean[] includeInTransform = new boolean[tupleLength];
 		for ( int i = 0 ; i < aliases.length ; i++ ) {
 			if ( aliases[ i ] != null ) {
 				includeInTransform[ i ] = true;
 			}
 		}
 		return includeInTransform;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
index 658da25ef1..4c79b55ad4 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
@@ -1,92 +1,90 @@
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
 package org.hibernate.transform;
 import java.util.List;
 
 /**
  * Much like {@link RootEntityResultTransformer}, but we also distinct
  * the entity in the final result.
  * <p/>
  * Since this transformer is stateless, all instances would be considered equal.
  * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class DistinctRootEntityResultTransformer implements TupleSubsetResultTransformer {
 
 	public static final DistinctRootEntityResultTransformer INSTANCE = new DistinctRootEntityResultTransformer();
 
 	/**
 	 * Disallow instantiation of DistinctRootEntityResultTransformer.
 	 */
 	private DistinctRootEntityResultTransformer() {
 	}
 
 	/**
 	 * Simply delegates to {@link RootEntityResultTransformer#transformTuple}.
 	 *
 	 * @param tuple The tuple to transform
 	 * @param aliases The tuple aliases
 	 * @return The transformed tuple row.
 	 */
+	@Override
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return RootEntityResultTransformer.INSTANCE.transformTuple( tuple, aliases );
 	}
 
 	/**
 	 * Simply delegates to {@link DistinctResultTransformer#transformList}.
 	 *
 	 * @param list The list to transform.
 	 * @return The transformed List.
 	 */
+	@Override
 	public List transformList(List list) {
 		return DistinctResultTransformer.INSTANCE.transformList( list );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
 		return RootEntityResultTransformer.INSTANCE.includeInTransform( aliases, tupleLength );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
 		return RootEntityResultTransformer.INSTANCE.isTransformedValueATupleElement( null, tupleLength );
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
index 687d2eeb52..242e582470 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
@@ -1,93 +1,87 @@
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
 package org.hibernate.transform;
 import java.util.Arrays;
 import java.util.List;
 
 /**
  * ???
  *
  * @author max
  */
 public class PassThroughResultTransformer extends BasicTransformerAdapter implements TupleSubsetResultTransformer {
 
 	public static final PassThroughResultTransformer INSTANCE = new PassThroughResultTransformer();
 
 	/**
 	 * Disallow instantiation of PassThroughResultTransformer.
 	 */
 	private PassThroughResultTransformer() {
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return tuple.length==1 ? tuple[0] : tuple;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
 		return tupleLength == 1;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
 		boolean[] includeInTransformedResult = new boolean[tupleLength];
 		Arrays.fill( includeInTransformedResult, true );
 		return includeInTransformedResult;
 	}
 
 	/* package-protected */
 	List untransformToTuples(List results, boolean isSingleResult) {
 		// untransform only if necessary; if transformed, do it in place;
 		if ( isSingleResult ) {
 			for ( int i = 0 ; i < results.size() ; i++ ) {
 				Object[] tuple = untransformToTuple( results.get( i ), isSingleResult);
 				results.set( i, tuple );
 			}
 		}
 		return results;
 	}
 
 	/* package-protected */
 	Object[] untransformToTuple(Object transformed, boolean isSingleResult ) {
 		return isSingleResult ? new Object[] { transformed } : ( Object[] ) transformed;
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
index 0e23657c42..261f797265 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
@@ -1,88 +1,83 @@
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
 package org.hibernate.transform;
 
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 /**
  * {@link ResultTransformer} implementation which limits the result tuple
  * to only the "root entity".
  * <p/>
  * Since this transformer is stateless, all instances would be considered equal.
  * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class RootEntityResultTransformer extends BasicTransformerAdapter implements TupleSubsetResultTransformer {
 
 	public static final RootEntityResultTransformer INSTANCE = new RootEntityResultTransformer();
 
 	/**
 	 * Disallow instantiation of RootEntityResultTransformer.
 	 */
 	private RootEntityResultTransformer() {
 	}
 
 	/**
 	 * Return just the root entity from the row tuple.
 	 */
 	@Override
     public Object transformTuple(Object[] tuple, String[] aliases) {
 		return tuple[ tuple.length-1 ];
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
-
 		boolean[] includeInTransform;
 		if ( tupleLength == 1 ) {
 			includeInTransform = ArrayHelper.TRUE;
 		}
 		else {
 			includeInTransform = new boolean[tupleLength];
 			includeInTransform[ tupleLength - 1 ] = true;
 		}
 		return includeInTransform;
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/ToListResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
index 76cdbdf1f7..129f4d8587 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
@@ -1,58 +1,56 @@
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
-package org.hibernate.transform;
+package org.hibernate.transform;
+
 import java.util.Arrays;
 import java.util.List;
 
 /**
  * Tranforms each result row from a tuple into a {@link List}, such that what
  * you end up with is a {@link List} of {@link List Lists}.
  */
 public class ToListResultTransformer extends BasicTransformerAdapter {
-
 	public static final ToListResultTransformer INSTANCE = new ToListResultTransformer();
 
 	/**
 	 * Disallow instantiation of ToListResultTransformer.
 	 */
 	private ToListResultTransformer() {
 	}
 	
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return Arrays.asList( tuple );
 	}
 
 	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index b4c5208862..55546ccd94 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,800 +1,817 @@
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
 import org.hibernate.loader.PropertyPath;
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
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 
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
 
+	@Override
 	public Serializable getIdentifier(Object entity) throws HibernateException {
 		return getIdentifier( entity, null );
 	}
 
+	@Override
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		setIdentifier( entity, id, null );
 	}
 
-
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
 
+		@Override
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
+		@Override
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
 
+		@Override
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
 
+		@Override
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
 
+	@Override
 	public Object getVersion(Object entity) throws HibernateException {
 		if ( !entityMetamodel.isVersioned() ) return null;
 		return getters[ entityMetamodel.getVersionPropertyIndex() ].get( entity );
 	}
 
 	protected boolean shouldGetAllProperties(Object entity) {
 		return !hasUninitializedLazyProperties( entity );
 	}
 
+	@Override
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		boolean getAll = shouldGetAllProperties( entity );
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			NonIdentifierAttribute property = entityMetamodel.getProperties()[j];
 			if ( getAll || !property.isLazy() ) {
 				result[j] = getters[j].get( entity );
 			}
 			else {
 				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 		}
 		return result;
 	}
 
+	@Override
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
-	throws HibernateException {
+			throws HibernateException {
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			result[j] = getters[j].getForInsert( entity, mergeMap, session );
 		}
 		return result;
 	}
 
+	@Override
 	public Object getPropertyValue(Object entity, int i) throws HibernateException {
 		return getters[i].get( entity );
 	}
 
+	@Override
 	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
 		int loc = propertyPath.indexOf('.');
 		String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		if (index == null) {
 			propertyPath = PropertyPath.IDENTIFIER_MAPPER_PROPERTY + "." + propertyPath;
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
 
+	@Override
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		boolean setAll = !entityMetamodel.hasLazyProperties();
 
 		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
 			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				setters[j].set( entity, values[j], getFactory() );
 			}
 		}
 	}
 
+	@Override
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
 		setters[i].set( entity, value, getFactory() );
 	}
 
+	@Override
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
 		setters[ entityMetamodel.getPropertyIndex( propertyName ) ].set( entity, value, getFactory() );
 	}
 
+	@Override
 	public final Object instantiate(Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		return instantiate( id, null );
 	}
 
+	@Override
 	public final Object instantiate(Serializable id, SessionImplementor session) {
 		Object result = getInstantiator().instantiate( id );
 		if ( id != null ) {
 			setIdentifier( result, id, session );
 		}
 		return result;
 	}
 
+	@Override
 	public final Object instantiate() throws HibernateException {
 		return instantiate( null, null );
 	}
 
+	@Override
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
 
+	@Override
 	public boolean hasUninitializedLazyProperties(Object entity) {
 		// the default is to simply not lazy fetch properties for now...
 		return false;
 	}
 
+	@Override
 	public final boolean isInstance(Object object) {
         return getInstantiator().isInstance( object );
 	}
 
+	@Override
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
+	@Override
 	public final Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return getProxyFactory().getProxy( id, session );
 	}
 
+	@Override
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
 
+	@Override
 	public Getter getIdentifierGetter() {
 		return idGetter;
 	}
 
+	@Override
 	public Getter getVersionGetter() {
 		if ( getEntityMetamodel().isVersioned() ) {
 			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
 		}
 		return null;
 	}
 
+	@Override
 	public Getter getGetter(int i) {
 		return getters[i];
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
index 58fc652008..7e376d62b5 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
@@ -1,263 +1,216 @@
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
  */
 package org.hibernate.tuple.entity;
 
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.proxy.map.MapProxyFactory;
 import org.hibernate.tuple.DynamicMapInstantiator;
 import org.hibernate.tuple.Instantiator;
 
-import org.jboss.logging.Logger;
-
 /**
  * An {@link EntityTuplizer} specific to the dynamic-map entity mode.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class DynamicMapEntityTuplizer extends AbstractEntityTuplizer {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       DynamicMapEntityTuplizer.class.getName());
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DynamicMapEntityTuplizer.class );
 
 	DynamicMapEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super(entityMetamodel, mappedEntity);
 	}
 
 	DynamicMapEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
 		super(entityMetamodel, mappedEntity);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.MAP;
 	}
 
 	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
 		if ( mappedProperty.isBackRef() ) {
 			return mappedProperty.getPropertyAccessor(null);
 		}
 		else {
 			return PropertyAccessorFactory.getDynamicMapPropertyAccessor();
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected Instantiator buildInstantiator(PersistentClass mappingInfo) {
         return new DynamicMapInstantiator( mappingInfo );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
 
 		ProxyFactory pf = new MapProxyFactory();
 		try {
 			//TODO: design new lifecycle for ProxyFactory
 			pf.postInstantiate(
 					getEntityName(),
 					null,
 					null,
 					null,
 					null,
 					null
 			);
 		}
 		catch ( HibernateException he ) {
 			LOG.unableToCreateProxyFactory( getEntityName(), he );
 			pf = null;
 		}
 		return pf;
 	}
 
 	private PropertyAccessor buildPropertyAccessor(AttributeBinding mappedProperty) {
 		// TODO: fix when backrefs are working in new metamodel
 		//if ( mappedProperty.isBackRef() ) {
 		//	return mappedProperty.getPropertyAccessor( null );
 		//}
 		//else {
 			return PropertyAccessorFactory.getDynamicMapPropertyAccessor();
 		//}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected Getter buildPropertyGetter(AttributeBinding mappedProperty) {
 		return buildPropertyAccessor( mappedProperty ).getGetter( null, mappedProperty.getAttribute().getName() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected Setter buildPropertySetter(AttributeBinding mappedProperty) {
 		return buildPropertyAccessor( mappedProperty ).getSetter( null, mappedProperty.getAttribute().getName() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected Instantiator buildInstantiator(EntityBinding mappingInfo) {
 		return new DynamicMapInstantiator( mappingInfo );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected ProxyFactory buildProxyFactory(EntityBinding mappingInfo, Getter idGetter, Setter idSetter) {
 
 		ProxyFactory pf = new MapProxyFactory();
 		try {
 			//TODO: design new lifecycle for ProxyFactory
 			pf.postInstantiate(
 					getEntityName(),
 					null,
 					null,
 					null,
 					null,
 					null
 			);
 		}
 		catch ( HibernateException he ) {
 			LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class getMappedClass() {
 		return Map.class;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class getConcreteProxyClass() {
 		return Map.class;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return new EntityNameResolver[] { BasicEntityNameResolver.INSTANCE };
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		return extractEmbeddedEntityName( ( Map ) entityInstance );
 	}
 
 	public static String extractEmbeddedEntityName(Map entity) {
 		return ( String ) entity.get( DynamicMapInstantiator.KEY );
 	}
 
 	public static class BasicEntityNameResolver implements EntityNameResolver {
 		public static final BasicEntityNameResolver INSTANCE = new BasicEntityNameResolver();
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String resolveEntityName(Object entity) {
 			if ( ! Map.class.isInstance( entity ) ) {
 				return null;
 			}
 			final String entityName = extractEmbeddedEntityName( ( Map ) entity );
 			if ( entityName == null ) {
 				throw new HibernateException( "Could not determine type of dynamic map entity" );
 			}
 			return entityName;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
 		@Override
         public boolean equals(Object obj) {
 			return getClass().equals( obj.getClass() );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
 		@Override
         public int hashCode() {
 			return getClass().hashCode();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
index 3c90c918a6..41945c3436 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
@@ -1,568 +1,512 @@
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
 
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.spi.ReflectionOptimizer;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.PojoInstantiator;
 import org.hibernate.type.CompositeType;
 
-import org.jboss.logging.Logger;
-
 /**
  * An {@link EntityTuplizer} specific to the pojo entity mode.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class PojoEntityTuplizer extends AbstractEntityTuplizer {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PojoEntityTuplizer.class.getName());
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( PojoEntityTuplizer.class );
 
 	private final Class mappedClass;
 	private final Class proxyInterface;
 	private final boolean lifecycleImplementor;
 	private final Set lazyPropertyNames = new HashSet();
 	private final ReflectionOptimizer optimizer;
 	private final boolean isInstrumented;
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getMappedClass();
 		this.proxyInterface = mappedEntity.getProxyInterface();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 		this.isInstrumented = entityMetamodel.isInstrumented();
 
 		Iterator iter = mappedEntity.getPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property property = (Property) iter.next();
 			if ( property.isLazy() ) {
 				lazyPropertyNames.add( property.getName() );
 			}
 		}
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[i].getMethodName();
 			setterNames[i] = setters[i].getMethodName();
 			propTypes[i] = getters[i].getReturnType();
 		}
 
 		if ( hasCustomAccessors || !Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 
 	}
 
 	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
 		super( entityMetamodel, mappedEntity );
 		this.mappedClass = mappedEntity.getEntity().getClassReference();
 		this.proxyInterface = mappedEntity.getProxyInterfaceType().getValue();
 		this.lifecycleImplementor = Lifecycle.class.isAssignableFrom( mappedClass );
 		this.isInstrumented = entityMetamodel.isInstrumented();
 
 		for ( AttributeBinding property : mappedEntity.getAttributeBindingClosure() ) {
 			if ( property.isLazy() ) {
 				lazyPropertyNames.add( property.getAttribute().getName() );
 			}
 		}
 
 		String[] getterNames = new String[propertySpan];
 		String[] setterNames = new String[propertySpan];
 		Class[] propTypes = new Class[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			getterNames[i] = getters[ i ].getMethodName();
 			setterNames[i] = setters[ i ].getMethodName();
 			propTypes[i] = getters[ i ].getReturnType();
 		}
 
 		if ( hasCustomAccessors || ! Environment.useReflectionOptimizer() ) {
 			optimizer = null;
 		}
 		else {
 			// todo : YUCK!!!
 			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
 					mappedClass, getterNames, setterNames, propTypes
 			);
 //			optimizer = getFactory().getSettings().getBytecodeProvider().getReflectionOptimizer(
 //					mappedClass, getterNames, setterNames, propTypes
 //			);
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected ProxyFactory buildProxyFactory(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
         // determine all interfaces needed by the resulting proxy
 		
 		/*
 		 * We need to preserve the order of the interfaces they were put into the set, since javassist will choose the
 		 * first one's class-loader to construct the proxy class with. This is also the reason why HibernateProxy.class
 		 * should be the last one in the order (on JBossAS7 its class-loader will be org.hibernate module's class-
 		 * loader, which will not see the classes inside deployed apps.  See HHH-3078
 		 */
 		Set<Class> proxyInterfaces = new java.util.LinkedHashSet<Class>();
 
 		Class mappedClass = persistentClass.getMappedClass();
 		Class proxyInterface = persistentClass.getProxyInterface();
 
 		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
 			if ( !proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		Iterator subclasses = persistentClass.getSubclassIterator();
 		while ( subclasses.hasNext() ) {
 			final Subclass subclass = ( Subclass ) subclasses.next();
 			final Class subclassProxy = subclass.getProxyInterface();
 			final Class subclassClass = subclass.getMappedClass();
 			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
 				if ( !subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subclass.getEntityName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Iterator properties = persistentClass.getPropertyIterator();
 		Class clazz = persistentClass.getMappedClass();
 		while ( properties.hasNext() ) {
 			Property property = (Property) properties.next();
 			Method method = property.getGetter(clazz).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.gettersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 			method = property.getSetter(clazz).getMethod();
             if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
                 LOG.settersOfLazyClassesCannotBeFinal(persistentClass.getEntityName(), property.getName());
 			}
 		}
 
 		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
 		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
 
 		ProxyFactory pf = buildProxyFactoryInternal( persistentClass, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					persistentClass.hasEmbeddedIdentifier() ?
 			                (CompositeType) persistentClass.getIdentifier().getType() :
 			                null
 			);
 		}
 		catch ( HibernateException he ) {
             LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(PersistentClass persistentClass, Getter idGetter, Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected Instantiator buildInstantiator(PersistentClass persistentClass) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( persistentClass, null );
 		}
 		else {
 			return new PojoInstantiator( persistentClass, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected ProxyFactory buildProxyFactory(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
 		// determine the id getter and setter methods from the proxy interface (if any)
 		// determine all interfaces needed by the resulting proxy
 		HashSet<Class> proxyInterfaces = new HashSet<Class>();
 		proxyInterfaces.add( HibernateProxy.class );
 
 		Class mappedClass = entityBinding.getEntity().getClassReference();
 		Class proxyInterface = entityBinding.getProxyInterfaceType().getValue();
 
 		if ( proxyInterface!=null && !mappedClass.equals( proxyInterface ) ) {
 			if ( ! proxyInterface.isInterface() ) {
 				throw new MappingException(
 						"proxy must be either an interface, or the class itself: " + getEntityName()
 				);
 			}
 			proxyInterfaces.add( proxyInterface );
 		}
 
 		if ( mappedClass.isInterface() ) {
 			proxyInterfaces.add( mappedClass );
 		}
 
 		for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 			final Class subclassProxy = subEntityBinding.getProxyInterfaceType().getValue();
 			final Class subclassClass = subEntityBinding.getClassReference();
 			if ( subclassProxy!=null && !subclassClass.equals( subclassProxy ) ) {
 				if ( ! subclassProxy.isInterface() ) {
 					throw new MappingException(
 							"proxy must be either an interface, or the class itself: " + subEntityBinding.getEntity().getName()
 					);
 				}
 				proxyInterfaces.add( subclassProxy );
 			}
 		}
 
 		for ( AttributeBinding property : entityBinding.attributeBindings() ) {
 			Method method = getGetter( property ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.gettersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
 			}
 			method = getSetter( property ).getMethod();
 			if ( method != null && Modifier.isFinal( method.getModifiers() ) ) {
 				LOG.settersOfLazyClassesCannotBeFinal(entityBinding.getEntity().getName(), property.getAttribute().getName());
 			}
 		}
 
 		Method idGetterMethod = idGetter==null ? null : idGetter.getMethod();
 		Method idSetterMethod = idSetter==null ? null : idSetter.getMethod();
 
 		Method proxyGetIdentifierMethod = idGetterMethod==null || proxyInterface==null ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idGetterMethod);
 		Method proxySetIdentifierMethod = idSetterMethod==null || proxyInterface==null  ?
 				null :
 		        ReflectHelper.getMethod(proxyInterface, idSetterMethod);
 
 		ProxyFactory pf = buildProxyFactoryInternal( entityBinding, idGetter, idSetter );
 		try {
 			pf.postInstantiate(
 					getEntityName(),
 					mappedClass,
 					proxyInterfaces,
 					proxyGetIdentifierMethod,
 					proxySetIdentifierMethod,
 					entityBinding.getHierarchyDetails().getEntityIdentifier().isEmbedded()
 							? ( CompositeType ) entityBinding
 									.getHierarchyDetails()
 									.getEntityIdentifier()
 									.getValueBinding()
 									.getHibernateTypeDescriptor()
 									.getResolvedTypeMapping()
 							: null
 			);
 		}
 		catch ( HibernateException he ) {
 			LOG.unableToCreateProxyFactory(getEntityName(), he);
 			pf = null;
 		}
 		return pf;
 	}
 
 	protected ProxyFactory buildProxyFactoryInternal(EntityBinding entityBinding, Getter idGetter, Setter idSetter) {
 		// TODO : YUCK!!!  fix after HHH-1907 is complete
 		return Environment.getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 //		return getFactory().getSettings().getBytecodeProvider().getProxyFactoryFactory().buildProxyFactory();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected Instantiator buildInstantiator(EntityBinding entityBinding) {
 		if ( optimizer == null ) {
 			return new PojoInstantiator( entityBinding, null );
 		}
 		else {
 			return new PojoInstantiator( entityBinding, optimizer.getInstantiationOptimizer() );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		if ( !getEntityMetamodel().hasLazyProperties() && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			setPropertyValuesWithOptimizer( entity, values );
 		}
 		else {
 			super.setPropertyValues( entity, values );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     public Object[] getPropertyValues(Object entity) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValues( entity );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
 		if ( shouldGetAllProperties( entity ) && optimizer != null && optimizer.getAccessOptimizer() != null ) {
 			return getPropertyValuesWithOptimizer( entity );
 		}
 		else {
 			return super.getPropertyValuesToInsert( entity, mergeMap, session );
 		}
 	}
 
 	protected void setPropertyValuesWithOptimizer(Object object, Object[] values) {
 		optimizer.getAccessOptimizer().setPropertyValues( object, values );
 	}
 
 	protected Object[] getPropertyValuesWithOptimizer(Object object) {
 		return optimizer.getAccessOptimizer().getPropertyValues( object );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class getMappedClass() {
 		return mappedClass;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     public boolean isLifecycleImplementor() {
 		return lifecycleImplementor;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getGetter( mappedEntity.getMappedClass() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
 		return mappedProperty.getSetter( mappedEntity.getMappedClass() );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected Getter buildPropertyGetter(AttributeBinding mappedProperty) {
 		return getGetter( mappedProperty );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected Setter buildPropertySetter(AttributeBinding mappedProperty) {
 		return getSetter( mappedProperty );
 	}
 
 	private Getter getGetter(AttributeBinding mappedProperty)  throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor( mappedProperty ).getGetter(
 				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private Setter getSetter(AttributeBinding mappedProperty) throws PropertyNotFoundException, MappingException {
 		return getPropertyAccessor( mappedProperty ).getSetter(
 				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getAttribute().getName()
 		);
 	}
 
 	private PropertyAccessor getPropertyAccessor(AttributeBinding mappedProperty) throws MappingException {
 		// TODO: Fix this then backrefs are working in new metamodel
 		return PropertyAccessorFactory.getPropertyAccessor(
 				mappedProperty.getContainer().getClassReference(),
 				mappedProperty.getPropertyAccessorName()
 		);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class getConcreteProxyClass() {
 		return proxyInterface;
 	}
 
     //TODO: need to make the majority of this functionality into a top-level support class for custom impl support
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		if ( isInstrumented() ) {
 			Set lazyProps = lazyPropertiesAreUnfetched && getEntityMetamodel().hasLazyProperties() ?
 					lazyPropertyNames : null;
 			//TODO: if we support multiple fetch groups, we would need
 			//      to clone the set of lazy properties!
 			FieldInterceptionHelper.injectFieldInterceptor( entity, getEntityName(), lazyProps, session );
 
             //also clear the fields that are marked as dirty in the dirtyness tracker
             if(entity instanceof org.hibernate.engine.spi.SelfDirtinessTracker) {
                 ((org.hibernate.engine.spi.SelfDirtinessTracker) entity).$$_hibernate_clearDirtyAttributes();
             }
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     public boolean hasUninitializedLazyProperties(Object entity) {
 		if ( getEntityMetamodel().hasLazyProperties() ) {
 			FieldInterceptor callback = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			return callback != null && !callback.isInitialized();
 		}
 		else {
 			return false;
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isInstrumented() {
 		return isInstrumented;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
 		final Class concreteEntityClass = entityInstance.getClass();
 		if ( concreteEntityClass == getMappedClass() ) {
 			return getEntityName();
 		}
 		else {
 			String entityName = getEntityMetamodel().findEntityNameByEntityClass( concreteEntityClass );
 			if ( entityName == null ) {
 				throw new HibernateException(
 						"Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
 								+ " expected instance/subclass of [" + getEntityName() + "]"
 				);
 			}
 			return entityName;
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java b/hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java
index 86fd6173d2..4b9e08c27b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BigDecimalType.java
@@ -1,56 +1,54 @@
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
 
 import java.math.BigDecimal;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.java.BigDecimalTypeDescriptor;
 import org.hibernate.type.descriptor.sql.NumericTypeDescriptor;
 
 /**
  * A type that maps between a {@link Types#NUMERIC NUMERIC} and {@link BigDecimal}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class BigDecimalType extends AbstractSingleColumnStandardBasicType<BigDecimal> {
 	public static final BigDecimalType INSTANCE = new BigDecimalType();
 
 	public BigDecimalType() {
 		super( NumericTypeDescriptor.INSTANCE, BigDecimalTypeDescriptor.INSTANCE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getName() {
 		return "big_decimal";
 	}
 
 	@Override
 	protected boolean registerUnderJavaType() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java b/hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java
index 682742415d..905d5563a5 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BigIntegerType.java
@@ -1,74 +1,68 @@
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
 
 import java.math.BigInteger;
 import java.sql.Types;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.type.descriptor.java.BigIntegerTypeDescriptor;
 import org.hibernate.type.descriptor.sql.NumericTypeDescriptor;
 
 /**
  * A type that maps between a {@link Types#NUMERIC NUMERIC} and {@link BigInteger}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class BigIntegerType
 		extends AbstractSingleColumnStandardBasicType<BigInteger>
 		implements DiscriminatorType<BigInteger> {
 
 	public static final BigIntegerType INSTANCE = new BigIntegerType();
 
 	public BigIntegerType() {
 		super( NumericTypeDescriptor.INSTANCE, BigIntegerTypeDescriptor.INSTANCE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getName() {
 		return "big_integer";
 	}
 
 	@Override
 	protected boolean registerUnderJavaType() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String objectToSQLString(BigInteger value, Dialect dialect) {
 		return BigIntegerTypeDescriptor.INSTANCE.toString( value );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public BigInteger stringToObject(String string) {
 		return BigIntegerTypeDescriptor.INSTANCE.fromString( string );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BlobType.java b/hibernate-core/src/main/java/org/hibernate/type/BlobType.java
index 150cbd1193..aba7875878 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BlobType.java
@@ -1,61 +1,59 @@
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
 import java.sql.Blob;
 
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.descriptor.java.BlobTypeDescriptor;
 
 /**
  * A type that maps between {@link java.sql.Types#BLOB BLOB} and {@link Blob}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class BlobType extends AbstractSingleColumnStandardBasicType<Blob> {
 	public static final BlobType INSTANCE = new BlobType();
 
 	public BlobType() {
 		super( org.hibernate.type.descriptor.sql.BlobTypeDescriptor.DEFAULT, BlobTypeDescriptor.INSTANCE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getName() {
 		return "blob";
 	}
 
 	@Override
 	protected boolean registerUnderJavaType() {
 		return true;
 	}
 
 	@Override
 	protected Blob getReplacement(Blob original, Blob target, SessionImplementor session) {
 		return session.getFactory().getDialect().getLobMergeStrategy().mergeBlob( original, target, session );
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index 8acea51ff4..3a76e13f9b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,808 +1,843 @@
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
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
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
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
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
 
+	@Override
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
 
+	@Override
 	public boolean isCollectionType() {
 		return true;
 	}
 
+	@Override
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
+	@Override
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
+	@Override
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
 
+	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] { name }, session, owner );
 	}
 
+	@Override
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
+	@Override
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
+	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 	}
 
+	@Override
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
 
+	@Override
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
+	@Override
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
 
+	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
+	@Override
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
 
+	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
+	@Override
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
 
+	@Override
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
 
+	@Override
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return isOwnerVersioned( session ) && super.isDirty( old, current, session );
 		// return false;
 
 	}
 
+	@Override
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
+	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
+	@Override
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
 
+	@Override
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
+	@Override
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
 
+	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
+	@Override
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
+	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
+	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
+	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return false;
 	}
 
+	@Override
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
 
+	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
+	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
 	}
 
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
 
+	@Override
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
+	@Override
 	public boolean isXMLElement() {
 		return true;
 	}
 
+	@Override
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) 
-	throws HibernateException {
+	@Override
+	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
+			throws HibernateException {
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
+	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
+	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
index ac29b91e00..f7f2f0eeec 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,738 +1,729 @@
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
 import java.util.Set;
 
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
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
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
+	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	/**
 	 * Explicitly, an entity type is an entity type ;)
 	 *
 	 * @return True.
 	 */
+	@Override
 	public final boolean isEntityType() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	/**
 	 * Generates a string representation of this type.
 	 *
 	 * @return string rep
 	 */
+	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
 	}
 
 	/**
 	 * For entity types, the name correlates to the associated entity name.
 	 */
+	@Override
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
 
+	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		// Return null if this type references a PK.  This is important for
 		// associations' use of mappedBy referring to a derived ID.
 		return referenceToPrimaryKey ? null : uniqueKeyPropertyName;
 	}
 
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
-	throws HibernateException, SQLException {
+			throws HibernateException, SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
+	@Override
 	public final boolean isSame(Object x, Object y) {
 		return x == y;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value; //special case ... this is the leaf of the containment graph, even though not immutable
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isXMLElement() {
 		return isEmbeddedInXML;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			return getIdentifierType(factory).fromXMLNode(xml, factory);
 		}
 		else {
 			return xml;
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			getIdentifierType(factory).setToXMLNode(node, value, factory);
 		}
 		else {
 			Element elt = (Element) value;
 			replaceNode( node, new ElementWrapper(elt) );
 		}
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		return getOnCondition( alias, factory, enabledFilters, null );
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		if ( isReferenceToPrimaryKey() && ( treatAsDeclarations == null || treatAsDeclarations.isEmpty() ) ) {
 			return "";
 		}
 		else {
 			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
 		}
 	}
 
 	/**
 	 * Resolve an identifier or unique key value
 	 */
+	@Override
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
 
+	@Override
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
+	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
index b00b1eaf4b..40430f7ee4 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
@@ -1,62 +1,60 @@
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
 import java.util.LinkedHashMap;
 
 /**
  * A specialization of the map type, with (resultset-based) ordering.
  */
 public class OrderedMapType extends MapType {
 
 	/**
 	 * Constructs a map type capable of creating ordered maps of the given
 	 * role.
 	 *
 	 * @param role The collection role name.
 	 * @param propertyRef The property ref name.
 	 * @param isEmbeddedInXML Is this collection to embed itself in xml
 	 *
 	 * @deprecated Use {@link #OrderedMapType(TypeFactory.TypeScope, String, String)} instead.
 	 *  instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public OrderedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
 	public OrderedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize > 0
 				? new LinkedHashMap( anticipatedSize )
 				: new LinkedHashMap();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
index b9aae70074..7f798cdc8e 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
@@ -1,63 +1,61 @@
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
 import java.util.LinkedHashSet;
 
 /**
  * A specialization of the set type, with (resultset-based) ordering.
  */
 public class OrderedSetType extends SetType {
 
 	/**
 	 * Constructs a set type capable of creating ordered sets of the given
 	 * role.
 	 *
 	 * @param typeScope The scope for this type instance.
 	 * @param role The collection role name.
 	 * @param propertyRef The property ref name.
 	 * @param isEmbeddedInXML Is this collection to embed itself in xml
 	 *
 	 * @deprecated Use {@link #OrderedSetType(org.hibernate.type.TypeFactory.TypeScope, String, String)}
 	 * instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public OrderedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
 	public OrderedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize > 0
 				? new LinkedHashSet( anticipatedSize )
 				: new LinkedHashSet();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java b/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
index 702d14f07f..cab5c265c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SerializableToBlobType.java
@@ -1,79 +1,71 @@
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
 import java.util.Properties;
 
 import org.hibernate.MappingException;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.descriptor.java.SerializableTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * @author Brett Meyer
  */
 public class SerializableToBlobType<T extends Serializable> extends AbstractSingleColumnStandardBasicType<T> implements DynamicParameterizedType {
 	
 	public static final String CLASS_NAME = "classname";
 	
 	private static final long serialVersionUID = 1L;
 
-	/**
-	 * @param sqlTypeDescriptor
-	 * @param javaTypeDescriptor
-	 */
 	public SerializableToBlobType() {
 		super( BlobTypeDescriptor.DEFAULT, new SerializableTypeDescriptor( Serializable.class ) );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getName() {
 		return getClass().getName();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings("unchecked")
 	public void setParameterValues(Properties parameters) {
 		ParameterType reader = (ParameterType) parameters.get( PARAMETER_TYPE );
 		if ( reader != null ) {
 			setJavaTypeDescriptor( new SerializableTypeDescriptor<T>( reader.getReturnedClass() ) );
 		} else {
 			String className = parameters.getProperty( CLASS_NAME );
 			if ( className == null ) {
 				throw new MappingException( "No class name defined for type: " + SerializableToBlobType.class.getName() );
 			}
 			try {
 				setJavaTypeDescriptor( new SerializableTypeDescriptor<T>( ReflectHelper.classForName( className ) ) );
 			} catch ( ClassNotFoundException e ) {
 				throw new MappingException( "Unable to load class from " + CLASS_NAME + " parameter", e );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
index 18b43195e9..6759c13db7 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/BigIntegerTypeDescriptor.java
@@ -1,114 +1,112 @@
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
 package org.hibernate.type.descriptor.java;
 
 import java.math.BigDecimal;
 import java.math.BigInteger;
 
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Descriptor for {@link BigInteger} handling.
  *
  * @author Steve Ebersole
  */
 public class BigIntegerTypeDescriptor extends AbstractTypeDescriptor<BigInteger> {
 	public static final BigIntegerTypeDescriptor INSTANCE = new BigIntegerTypeDescriptor();
 
 	public BigIntegerTypeDescriptor() {
 		super( BigInteger.class );
 	}
 
+	@Override
 	public String toString(BigInteger value) {
 		return value.toString();
 	}
 
+	@Override
 	public BigInteger fromString(String string) {
 		return new BigInteger( string );
 	}
 
 	@Override
 	public int extractHashCode(BigInteger value) {
 		return value.intValue();
 	}
 
 	@Override
 	public boolean areEqual(BigInteger one, BigInteger another) {
 		return one == another || ( one != null && another != null && one.compareTo( another ) == 0 );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(BigInteger value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( BigInteger.class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( BigDecimal.class.isAssignableFrom( type ) ) {
 			return (X) new BigDecimal( value );
 		}
 		if ( Byte.class.isAssignableFrom( type ) ) {
 			return (X) Byte.valueOf( value.byteValue() );
 		}
 		if ( Short.class.isAssignableFrom( type ) ) {
 			return (X) Short.valueOf( value.shortValue() );
 		}
 		if ( Integer.class.isAssignableFrom( type ) ) {
 			return (X) Integer.valueOf( value.intValue() );
 		}
 		if ( Long.class.isAssignableFrom( type ) ) {
 			return (X) Long.valueOf( value.longValue() );
 		}
 		if ( Double.class.isAssignableFrom( type ) ) {
 			return (X) Double.valueOf( value.doubleValue() );
 		}
 		if ( Float.class.isAssignableFrom( type ) ) {
 			return (X) Float.valueOf( value.floatValue() );
 		}
 		throw unknownUnwrap( type );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <X> BigInteger wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( BigInteger.class.isInstance( value ) ) {
 			return (BigInteger) value;
 		}
 		if ( BigDecimal.class.isInstance( value ) ) {
 			return ( (BigDecimal) value ).toBigIntegerExact();
 		}
 		if ( Number.class.isInstance( value ) ) {
 			return BigInteger.valueOf( ( (Number) value ).longValue() );
 		}
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
index 8af63b85c9..a61cab7cd6 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
@@ -1,154 +1,157 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.engine.jdbc.BinaryStream;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BLOB BLOB} handling.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  * @author Brett Meyer
  */
 public abstract class BlobTypeDescriptor implements SqlTypeDescriptor {
 
 	private BlobTypeDescriptor() {
 	}
 
 	@Override
 	public int getSqlType() {
 		return Types.BLOB;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	@Override
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBlob( name ), options );
 			}
 
 			@Override
 			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBlob( index ), options );
 			}
 
 			@Override
-			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
+			protected X doExtract(CallableStatement statement, String name, WrapperOptions options)
+					throws SQLException {
 				return javaTypeDescriptor.wrap( statement.getBlob( name ), options );
 			}
 		};
 	}
 
 	protected abstract <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
 
+	@Override
 	public <X> BasicBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return getBlobBinder( javaTypeDescriptor );
 	}
 
-	public static final BlobTypeDescriptor DEFAULT =
-			new BlobTypeDescriptor() {
-				{
-					SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
-				}
+	public static final BlobTypeDescriptor DEFAULT = new BlobTypeDescriptor() {
+		{
+			SqlTypeDescriptorRegistry.INSTANCE.addDescriptor( this );
+		}
 
+		@Override
+		public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
-                public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-					return new BasicBinder<X>( javaTypeDescriptor, this ) {
-						@Override
-						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
-							BlobTypeDescriptor descriptor = BLOB_BINDING;
-							if ( byte[].class.isInstance( value ) ) {
-								// performance shortcut for binding BLOB data in byte[] format
-								descriptor = PRIMITIVE_ARRAY_BINDING;
-							}
-							else if ( options.useStreamForLobBinding() ) {
-								descriptor = STREAM_BINDING;
-							}
-							descriptor.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
-						}
-					};
+				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+						throws SQLException {
+					BlobTypeDescriptor descriptor = BLOB_BINDING;
+					if ( byte[].class.isInstance( value ) ) {
+						// performance shortcut for binding BLOB data in byte[] format
+						descriptor = PRIMITIVE_ARRAY_BINDING;
+					}
+					else if ( options.useStreamForLobBinding() ) {
+						descriptor = STREAM_BINDING;
+					}
+					descriptor.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 				}
 			};
+		}
+	};
 
-	public static final BlobTypeDescriptor PRIMITIVE_ARRAY_BINDING =
-			new BlobTypeDescriptor() {
+	public static final BlobTypeDescriptor PRIMITIVE_ARRAY_BINDING = new BlobTypeDescriptor() {
+		@Override
+		public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
-                public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-					return new BasicBinder<X>( javaTypeDescriptor, this ) {
-						@Override
-						public void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
-								throws SQLException {
-							st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
-						}
-					};
+				public void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+						throws SQLException {
+					st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
 				}
 			};
+		}
+	};
 
-	public static final BlobTypeDescriptor BLOB_BINDING =
-			new BlobTypeDescriptor() {
+	public static final BlobTypeDescriptor BLOB_BINDING = new BlobTypeDescriptor() {
+		@Override
+		public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
-                public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-					return new BasicBinder<X>( javaTypeDescriptor, this ) {
-						@Override
-						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
-								throws SQLException {
-							st.setBlob( index, javaTypeDescriptor.unwrap( value, Blob.class, options ) );
-						}
-					};
+				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+						throws SQLException {
+					st.setBlob( index, javaTypeDescriptor.unwrap( value, Blob.class, options ) );
 				}
 			};
+		}
+	};
 
-	public static final BlobTypeDescriptor STREAM_BINDING =
-			new BlobTypeDescriptor() {
+	public static final BlobTypeDescriptor STREAM_BINDING = new BlobTypeDescriptor() {
+		@Override
+		public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
+			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
-                public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
-					return new BasicBinder<X>( javaTypeDescriptor, this ) {
-						@Override
-						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
-								throws SQLException {
-							final BinaryStream binaryStream = javaTypeDescriptor.unwrap( value, BinaryStream.class, options );
-							st.setBinaryStream( index, binaryStream.getInputStream(), binaryStream.getLength() );
-						}
-					};
+				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
+						throws SQLException {
+					final BinaryStream binaryStream = javaTypeDescriptor.unwrap(
+							value,
+							BinaryStream.class,
+							options
+					);
+					st.setBinaryStream( index, binaryStream.getInputStream(), binaryStream.getLength() );
 				}
 			};
+		}
+	};
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
index fb8fb6b6e5..8705bc3819 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/HQLTest.java
@@ -1,1118 +1,1118 @@
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
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 import org.junit.Test;
 
 import org.hibernate.QueryException;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.AbstractHANADialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.SybaseDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.ReturnMetadata;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
 import org.hibernate.hql.internal.ast.DetailedSemanticException;
 import org.hibernate.hql.internal.ast.QuerySyntaxException;
 import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.hql.internal.ast.tree.ConstructorNode;
 import org.hibernate.hql.internal.ast.tree.DotNode;
 import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
 import org.hibernate.hql.internal.ast.tree.IndexNode;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.SelectClause;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.type.CalendarDateType;
 import org.hibernate.type.DoubleType;
 import org.hibernate.type.StringType;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests cases where the AST based query translator and the 'classic' query translator generate identical SQL.
  *
  * @author Gavin King
  */
 public class HQLTest extends QueryTranslatorTestCase {
 	@Override
 	public boolean createSchema() {
 		return false;
 	}
 
 	@Override
 	public boolean rebuildSessionFactoryOnError() {
 		return false;
 	}
 
 	@Override
 	protected void prepareTest() throws Exception {
 		super.prepareTest();
 		SelectClause.VERSION2_SQL = true;
-		DotNode.REGRESSION_STYLE_JOIN_SUPPRESSION = true;
+		DotNode.regressionStyleJoinSuppression = true;
 		DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER = new DotNode.IllegalCollectionDereferenceExceptionBuilder() {
 			public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
 				throw new QueryException( "illegal syntax near collection: " + propertyName );
 			}
 		};
 		SqlGenerator.REGRESSION_STYLE_CROSS_JOINS = true;
 	}
 
 	@Override
 	protected void cleanupTest() throws Exception {
 		SelectClause.VERSION2_SQL = false;
-		DotNode.REGRESSION_STYLE_JOIN_SUPPRESSION = false;
+		DotNode.regressionStyleJoinSuppression = false;
 		DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER = DotNode.DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
 		SqlGenerator.REGRESSION_STYLE_CROSS_JOINS = false;
 		super.cleanupTest();
 	}
 
 	@Test
 	public void testModulo() {
 		assertTranslation( "from Animal a where a.bodyWeight % 2 = 0" );
 	}
 
 	@Test
 	public void testInvalidCollectionDereferencesFail() {
 		// should fail with the same exceptions (because of the DotNode.ILLEGAL_COLL_DEREF_EXCP_BUILDER injection)
 		assertTranslation( "from Animal a where a.offspring.description = 'xyz'" );
 		assertTranslation( "from Animal a where a.offspring.father.description = 'xyz'" );
 	}
 	
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
     public void testRowValueConstructorSyntaxInInList2() {
         assertTranslation( "from LineItem l where l.id in (:idList)" );
 		assertTranslation( "from LineItem l where l.id in :idList" );
     }
 
 	@Test
 	@SkipForDialect( value = { Oracle8iDialect.class, AbstractHANADialect.class } )
     public void testRowValueConstructorSyntaxInInListBeingTranslated() {
 		QueryTranslatorImpl translator = createNewQueryTranslator("from LineItem l where l.id in (?)");
 		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in ?");
 		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (('a1',1,'b1'),('a2',2,'b2'))");
 		assertInExist("'in' should be translated to 'and'", false, translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in (?)");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in ?");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (select a1 from Animal a1 left join a1.offspring o where a1.id = 1)");
 		assertInExist("do not translate sub-queries", true, translator);
     }
 
 	@Test
 	@RequiresDialectFeature( DialectChecks.SupportsRowValueConstructorSyntaxInInListCheck.class )
     public void testRowValueConstructorSyntaxInInList() {
 		QueryTranslatorImpl translator = createNewQueryTranslator("from LineItem l where l.id in (?)");
 		assertInExist(" 'in' should be kept, since the dialect supports this syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in ?");
 		assertInExist(" 'in' should be kept, since the dialect supports this syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (('a1',1,'b1'),('a2',2,'b2'))");
 		assertInExist(" 'in' should be kept, since the dialect supports this syntax", true,translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in (?)");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from Animal a where a.id in ?");
 		assertInExist("only translated tuple has 'in' syntax", true, translator);
 		translator = createNewQueryTranslator("from LineItem l where l.id in (select a1 from Animal a1 left join a1.offspring o where a1.id = 1)");
 		assertInExist("do not translate sub-queries", true, translator);
     }
 
 	private void assertInExist( String message, boolean expected, QueryTranslatorImpl translator ) {
 		AST ast = translator.getSqlAST().getWalker().getAST();
 		QueryNode queryNode = (QueryNode) ast;
 		AST whereNode = ASTUtil.findTypeInChildren( queryNode, HqlTokenTypes.WHERE );
 		AST inNode = whereNode.getFirstChild();
 		assertEquals( message, expected, inNode != null && inNode.getType() == HqlTokenTypes.IN );
 	}
     
 	@Test
 	public void testSubComponentReferences() {
 		assertTranslation( "select c.address.zip.code from ComponentContainer c" );
 		assertTranslation( "select c.address.zip from ComponentContainer c" );
 		assertTranslation( "select c.address from ComponentContainer c" );
 	}
 
 	@Test
 	public void testManyToAnyReferences() {
 		assertTranslation( "from PropertySet p where p.someSpecificProperty.id is not null" );
 		assertTranslation( "from PropertySet p join p.generalProperties gp where gp.id is not null" );
 	}
 
 	@Test
 	public void testJoinFetchCollectionOfValues() {
 		assertTranslation( "select h from Human as h join fetch h.nickNames" );
 	}
 	
 	@Test
 	public void testCollectionMemberDeclarations2() {
 		assertTranslation( "from Customer c, in(c.orders) o" );
 		assertTranslation( "from Customer c, in(c.orders) as o" );
 		assertTranslation( "select c.name from Customer c, in(c.orders) as o where c.id = o.id.customerId" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
 	public void testCollectionMemberDeclarations(){
 		// both these two query translators throw exeptions for this HQL since
 		// IN asks an alias, but the difference is that the error message from AST
 		// contains the error token location (by lines and columns), which is hardly 
 		// to get from Classic query translator --stliu
 		assertTranslation( "from Customer c, in(c.orders)" ); 
 	}
 
 	@Test
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
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2045" )
 	public void testEmptyInList() {
 		assertTranslation( "select a from Animal a where a.description in ()" );
 	}
 
 	@Test
 	public void testDateTimeArithmeticReturnTypesAndParameterGuessing() {
 		QueryTranslatorImpl translator = createNewQueryTranslator( "select o.orderDate - o.orderDate from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0] );
 		translator = createNewQueryTranslator( "select o.orderDate + 2 from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", CalendarDateType.INSTANCE, translator.getReturnTypes()[0] );
 		translator = createNewQueryTranslator( "select o.orderDate -2 from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", CalendarDateType.INSTANCE, translator.getReturnTypes()[0] );
 
 		translator = createNewQueryTranslator( "from Order o where o.orderDate > ?" );
 		assertEquals( "incorrect expected param type", CalendarDateType.INSTANCE, translator.getParameterTranslations().getOrdinalParameterExpectedType( 1 ) );
 
 		translator = createNewQueryTranslator( "select o.orderDate + ? from Order o" );
 		assertEquals( "incorrect return type count", 1, translator.getReturnTypes().length );
 		assertEquals( "incorrect return type", CalendarDateType.INSTANCE, translator.getReturnTypes()[0] );
 		assertEquals( "incorrect expected param type", DoubleType.INSTANCE, translator.getParameterTranslations().getOrdinalParameterExpectedType( 1 ) );
 
 	}
 
 	@Test
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
 
 	@Test
 	public void testImplicitJoinsAlongWithCartesianProduct() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo.foo from Foo foo, Foo foo2" );
 		assertTranslation( "select foo.foo.foo from Foo foo, Foo foo2" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testSubselectBetween() {
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) between :min and :max");
 		assertTranslation("from Animal x where (select max(a.description) from Animal a) like 'big%'");
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) is not null");
 		assertTranslation("from Animal x where exists (select max(a.bodyWeight) from Animal a)");
 		assertTranslation("from Animal x where (select max(a.bodyWeight) from Animal a) in (1,2,3)");
 	}
 
 	@Test
 	public void testFetchOrderBy() {
 		assertTranslation("from Animal a left outer join fetch a.offspring where a.mother.id = :mid order by a.description");
 	}
 
 	@Test
 	public void testCollectionOrderBy() {
 		assertTranslation("from Animal a join a.offspring o order by a.description");
 		assertTranslation("from Animal a join fetch a.offspring order by a.description");
 		assertTranslation("from Animal a join fetch a.offspring o order by o.description");
 		assertTranslation("from Animal a join a.offspring o order by a.description, o.description");
 	}
 
 	@Test
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
 		if ( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof PostgreSQL81Dialect ) {
 			return;
 		}
 		if ( getDialect() instanceof AbstractHANADialect ) {
 			// HANA returns
 			// ...al0_7_.mammal where [abs(cast(1 as float(19))-cast(? as float(19)))=1.0]
 			return;
 		}
 		assertTranslation("from Animal where abs(cast(1 as float) - cast(:param as float)) = 1.0");
 	}
 
 	@Test
 	public void testCompositeKeysWithPropertyNamedId() {
 		assertTranslation( "select e.id.id from EntityWithCrazyCompositeKey e" );
 		assertTranslation( "select max(e.id.id) from EntityWithCrazyCompositeKey e" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
 	public void testMaxindexHqlFunctionInElementAccessor() {
 		//TODO: broken SQL
 		//      steve (2005.10.06) - this is perfect SQL, but fairly different from the old parser
 		//              tested : HSQLDB (1.8), Oracle8i
 		assertTranslation( "select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2" );
 		assertTranslation( "select c from Container c where c.manyToMany[ maxIndex(c.manyToMany) ].count = 2" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Lacking ClassicQueryTranslatorFactory support" )
 	public void testMultipleElementAccessorOperators() throws Exception {
 		//TODO: broken SQL
 		//      steve (2005.10.06) - Yes, this is all hosed ;)
 		assertTranslation( "select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'" );
 		assertTranslation( "select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Parser output mismatch" )
 	public void testKeyManyToOneJoin() {
 		//TODO: new parser generates unnecessary joins (though the query results are correct)
 		assertTranslation( "from Order o left join fetch o.lineItems li left join fetch li.product p" );
 		assertTranslation( "from Outer o where o.id.master.id.sup.dudu is not null" );
 		assertTranslation( "from Outer o where o.id.master.id.sup.dudu is not null" );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "N/A", message = "Parser output mismatch" )
 	public void testDuplicateExplicitJoin() throws Exception {
 		//very minor issue with select clause:
 		assertTranslation( "from Animal a join a.mother m1 join a.mother m2" );
 		assertTranslation( "from Zoo zoo join zoo.animals an join zoo.mammals m" );
 		assertTranslation( "from Zoo zoo join zoo.mammals an join zoo.mammals m" );
 	}
 
 	@Test
 	public void testIndexWithExplicitJoin() throws Exception {
 		//TODO: broken on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo join zoo.animals an where zoo.mammals[ index(an) ] = an" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where zoo.mammals[ index(dog) ] = dog" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where dog = zoo.mammals[ index(dog) ]" );
 	}
 
 	@Test
 	public void testOneToManyMapIndex() throws Exception {
 		//TODO: this breaks on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].description like '%black%'" );
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].father.description like '%black%'" );
 		assertTranslation( "from Zoo zoo where zoo.mammals['dog'].father.id = 1234" );
 		assertTranslation( "from Zoo zoo where zoo.animals['1234'].description like '%black%'" );
 	}
 
 	@Test
 	public void testExplicitJoinMapIndex() throws Exception {
 		//TODO: this breaks on dialects with theta-style outerjoins:
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Zoo zoo, Dog dog where zoo.mammals['dog'] = dog" );
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where zoo.mammals['dog'] = dog" );
 	}
 
 	@Test
 	public void testIndexFunction() throws Exception {
 		// Instead of doing the pre-processor trick like the existing QueryTranslator, this
 		// is handled by MethodNode.
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		//TODO: broken on dialects with theta-style outerjoins:
 		assertTranslation( "from Zoo zoo join zoo.mammals dog where index(dog) = 'dog'" );
 		assertTranslation( "from Zoo zoo join zoo.animals an where index(an) = '1234'" );
 	}
 
 	@Test
 	public void testSelectCollectionOfValues() throws Exception {
 		//TODO: broken on dialects with theta-style joins
 		///old parser had a bug where the collection element was not included in return types!
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "select baz, date from Baz baz join baz.stringDateMap date where index(date) = 'foo'" );
 	}
 
 	@Test
 	public void testCollectionOfValues() throws Exception {
 		//old parser had a bug where the collection element was not returned!
 		//TODO: broken on dialects with theta-style joins
 		//      steve (2005.10.06) - this works perfectly for me on Oracle8i
 		assertTranslation( "from Baz baz join baz.stringDateMap date where index(date) = 'foo'" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-719" )
     public void testHHH719() throws Exception {
         assertTranslation("from Baz b order by org.bazco.SpecialFunction(b.id)");
         assertTranslation("from Baz b order by anypackage.anyFunction(b.id)");
     }
 
 	@Test
 	public void testParameterListExpansion() {
 		assertTranslation( "from Animal as animal where animal.id in (:idList_1, :idList_2)" );
 	}
 
 	@Test
 	public void testComponentManyToOneDereferenceShortcut() {
 		assertTranslation( "from Zoo z where z.address.stateProvince.id is null" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-770" )
 	public void testNestedCollectionImplicitJoins() {
 		assertTranslation( "select h.friends.offspring from Human h" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-557" )
 	public void testExplicitJoinsInSubquery() {
 		assertTranslation(
 		        "from org.hibernate.test.hql.Animal as animal " +
 		        "where animal.id in (" +
 		        "        select a.id " +
 		        "        from org.hibernate.test.hql.Animal as a " +
 		        "               left join a.mother as mo" +
 		        ")"
 		);
 	}
 
 	@Test
 	public void testImplicitJoinsInGroupBy() {
 		assertTranslation(
 		        "select o.mother.bodyWeight, count(distinct o) " +
 		        "from Animal an " +
 		        "   join an.offspring as o " +
 		        "group by o.mother.bodyWeight"
 		);
 	}
 
 	@Test
 	public void testCrazyIdFieldNames() {
 		DotNode.useThetaStyleImplicitJoins = true;
 		// only regress against non-scalar forms as there appears to be a bug in the classic translator
 		// in regards to this issue also.  Specifically, it interprets the wrong return type, though it gets
 		// the sql "correct" :/
 
 		String hql = "select e.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null";
 		assertTranslation( hql, new HashMap(), false, null );
 
 	    hql = "select e.heresAnotherCrazyIdFieldName.heresAnotherCrazyIdFieldName from MoreCrazyIdFieldNameStuffEntity e where e.heresAnotherCrazyIdFieldName is not null";
 		assertTranslation( hql, new HashMap(), false, null );
 
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
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
 
 	@Test
 	public void testFromOnly() throws Exception {
 		// 2004-06-21 [jsd] This test now works with the new AST based QueryTranslatorImpl.
 		assertTranslation( "from Animal" );
 		assertTranslation( "from Model" );
 	}
 
 	@Test
 	public void testJoinPathEndingInValueCollection() {
 		assertTranslation( "select h from Human as h join h.nickNames as nn where h.nickName=:nn1 and (nn=:nn2 or nn=:nn3)" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-242" )
 	public void testSerialJoinPathEndingInValueCollection() {
 		assertTranslation( "select h from Human as h join h.friends as f join f.nickNames as nn where h.nickName=:nn1 and (nn=:nn2 or nn=:nn3)" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-281" )
 	public void testImplicitJoinContainedByCollectionFunction() {
 		assertTranslation( "from Human as h where 'shipping' in indices(h.father.addresses)" );
 		assertTranslation( "from Human as h where 'shipping' in indices(h.father.father.addresses)" );
 		assertTranslation( "from Human as h where 'sparky' in elements(h.father.nickNames)" );
 		assertTranslation( "from Human as h where 'sparky' in elements(h.father.father.nickNames)" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-276" )
 	public void testImpliedJoinInSubselectFrom() {
 		assertTranslation( "from Animal a where exists( from a.mother.offspring )" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-276" )
 	public void testSubselectImplicitJoins() {
 		assertTranslation( "from Simple s where s = some( select sim from Simple sim where sim.other.count=s.other.count )" );
 	}
 
 	@Test
 	public void testCollectionOfValuesSize() throws Exception {
 		//SQL *was* missing a comma
 		assertTranslation( "select size(baz.stringDateMap) from org.hibernate.test.legacy.Baz baz" );
 	}
 
 	@Test
 	public void testCollectionFunctions() throws Exception {
 		//these are both broken, a join that belongs in the subselect finds its way into the main query
 		assertTranslation( "from Zoo zoo where size(zoo.animals) > 100" );
 		assertTranslation( "from Zoo zoo where maxindex(zoo.mammals) = 'dog'" );
 	}
 
 	@Test
 	public void testImplicitJoinInExplicitJoin() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother.mother gm" );
 		assertTranslation( "from Animal an inner join an.mother.mother.mother ggm" );
 		assertTranslation( "from Animal an inner join an.mother.mother.mother.mother gggm" );
 	}
 
 	@Test
 	public void testImpliedManyToManyProperty() throws Exception {
 		//missing a table join (SQL correct for a one-to-many, not for a many-to-many)
 		assertTranslation( "select c from ContainerX c where c.manyToMany[0].name = 's'" );
 	}
 
 	@Test
 	public void testCollectionSize() throws Exception {
 		assertTranslation( "select size(zoo.animals) from Zoo zoo" );
 	}
 
 	@Test
 	public void testFetchCollectionOfValues() throws Exception {
 		assertTranslation( "from Baz baz left join fetch baz.stringSet" );
 	}
 
 	@Test
 	public void testFetchList() throws Exception {
 		assertTranslation( "from User u join fetch u.permissions" );
 	}
 
 	@Test
 	public void testCollectionFetchWithExplicitThetaJoin() {
 		assertTranslation( "select m from Master m1, Master m left join fetch m.details where m.name=m1.name" );
 	}
 
 	@Test
 	public void testListElementFunctionInWhere() throws Exception {
 		assertTranslation( "from User u where 'read' in elements(u.permissions)" );
 		assertTranslation( "from User u where 'write' <> all elements(u.permissions)" );
 	}
 
 	@Test
 	public void testManyToManyMaxElementFunctionInWhere() throws Exception {
 		assertTranslation( "from Human human where 5 = maxelement(human.friends)" );
 	}
 
 	@Test
 	public void testCollectionIndexFunctionsInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 = maxindex(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 2 = minindex(zoo.animals)" );
 	}
 
 	@Test
 	public void testCollectionIndicesInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 > some indices(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 4 > all indices(zoo.animals)" );
 	}
 
 	@Test
 	public void testIndicesInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 in indices(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where exists indices(zoo.animals)" );
 	}
 
 	@Test
 	public void testCollectionElementInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 > some elements(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where 4 > all elements(zoo.animals)" );
 	}
 
 	@Test
 	public void testElementsInWhere() throws Exception {
 		assertTranslation( "from Zoo zoo where 4 in elements(zoo.animals)" );
 		assertTranslation( "from Zoo zoo where exists elements(zoo.animals)" );
 	}
 
 	@Test
 	public void testNull() throws Exception {
 		assertTranslation( "from Human h where h.nickName is null" );
 		assertTranslation( "from Human h where h.nickName is not null" );
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testSubstitutions() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		replacements.put("yes", "'Y'");
 		assertTranslation( "from Human h where h.pregnant = true", replacements );
 		assertTranslation( "from Human h where h.pregnant = yes", replacements );
 		assertTranslation( "from Human h where h.pregnant = foo", replacements );
 	}
 
 	@Test
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
 
 	@Test
 	public void testEscapedQuote() throws Exception {
 		assertTranslation( "from Human h where h.nickName='1 ov''tha''few'");
 	}
 
 	@Test
 	public void testCaseWhenElse() {
 		assertTranslation(
 				"from Human h where case when h.nickName='1ovthafew' then 'Gavin' when h.nickName='turin' then 'Christian' else h.nickName end = h.name.first"
 		);
 	}
 
 	@Test
 	public void testCaseExprWhenElse() {
 		assertTranslation( "from Human h where case h.nickName when '1ovthafew' then 'Gavin' when 'turin' then 'Christian' else h.nickName end = h.name.first" );
 	}
 
 	@Test
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
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
 
 	@Test
 	public void testWhereBetween() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight between 1 and 10" );
 	}
 
 	@Test
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
 
 	@Test
 	public void testWhereLike() throws Exception {
 		assertTranslation( "from Animal a where a.description like '%black%'" );
 		assertTranslation( "from Animal an where an.description like '%fat%'" );
 		assertTranslation( "from Animal an where lower(an.description) like '%fat%'" );
 	}
 
 	@Test
 	public void testWhereIn() throws Exception {
 		assertTranslation( "from Animal an where an.description in ('fat', 'skinny')" );
 	}
 
 	@Test
 	public void testLiteralInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs(5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(-5)" );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	@Test
 	public void testExpressionInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3-5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3/5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3+5)" );
 		assertTranslation( "from Animal an where an.bodyWeight > abs(3*5)" );
 		SQLFunction concat = sessionFactory().getSqlFunctionRegistry().findSQLFunction( "concat");
 		List list = new ArrayList();
 		list.add("'fat'");
 		list.add("'skinny'");
 		assertTranslation(
 				"from Animal an where an.description = " +
 						concat.render( StringType.INSTANCE, list, sessionFactory() )
 		);
 	}
 
 	@Test
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
 
 	@Test
 	public void testComplexExpressionInFunction() throws Exception {
 		assertTranslation( "from Animal an where an.bodyWeight > abs((3-5)/4)" );
 	}
 
 	@Test
 	public void testStandardFunctions() throws Exception {
 		assertTranslation( "from Animal where current_date = current_time" );
 		assertTranslation( "from Animal a where upper(a.description) = 'FAT'" );
 		assertTranslation( "select lower(a.description) from Animal a" );
 	}
 
 	@Test
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
 
 	@Test
 	public void testGroupByFunction() {
 		if ( getDialect() instanceof Oracle8iDialect ) return; // the new hiearchy...
 		if ( getDialect() instanceof PostgreSQLDialect || getDialect() instanceof PostgreSQL81Dialect ) return;
 		if ( ! H2Dialect.class.isInstance( getDialect() ) ) {
 			// H2 has no year function
 			assertTranslation( "select count(*) from Human h group by year(h.birthdate)" );
 			assertTranslation( "select count(*) from Human h group by year(sysdate)" );
 		}
 		assertTranslation( "select count(*) from Human h group by trunc( sqrt(h.bodyWeight*4)/2 )" );
 	}
 
 	@Test
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
 
 	@Test
 	public void testTokenReplacement() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Mammal m where m.pregnant = false and m.bodyWeight > 10", replacements );
 	}
 
 	@Test
 	public void testProduct() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Animal, Animal" );
 		assertTranslation( "from Animal x, Animal y where x.bodyWeight = y.bodyWeight" );
 		assertTranslation( "from Animal x, Mammal y where x.bodyWeight = y.bodyWeight and not y.pregnant = true", replacements );
 		assertTranslation( "from Mammal, Mammal" );
 	}
 
 	@Test
 	public void testJoinedSubclassProduct() throws Exception {
 		assertTranslation( "from PettingZoo, PettingZoo" ); //product of two subclasses
 	}
 
 	@Test
 	public void testProjectProduct() throws Exception {
 		assertTranslation( "select x from Human x, Human y where x.nickName = y.nickName" );
 		assertTranslation( "select x, y from Human x, Human y where x.nickName = y.nickName" );
 	}
 
 	@Test
 	public void testExplicitEntityJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo" );
 		assertTranslation( "from Animal an left outer join an.mother mo" );
 		assertTranslation( "from Animal an left outer join fetch an.mother" );
 	}
 
 	@Test
 	public void testMultipleExplicitEntityJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo inner join mo.mother gm" );
 		assertTranslation( "from Animal an left outer join an.mother mo left outer join mo.mother gm" );
 		assertTranslation( "from Animal an inner join an.mother m inner join an.father f" );
 		assertTranslation( "from Animal an left join fetch an.mother m left join fetch an.father f" );
 	}
 
 	@Test
 	public void testMultipleExplicitJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo inner join an.offspring os" );
 		assertTranslation( "from Animal an left outer join an.mother mo left outer join an.offspring os" );
 	}
 
 	@Test
 	public void testExplicitEntityJoinsWithRestriction() throws Exception {
 		assertTranslation( "from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	@Test
 	public void testIdProperty() throws Exception {
 		assertTranslation( "from Animal a where a.mother.id = 12" );
 	}
 
 	@Test
 	public void testSubclassAssociation() throws Exception {
 		assertTranslation( "from DomesticAnimal da join da.owner o where o.nickName = 'Gavin'" );
 		assertTranslation( "from DomesticAnimal da left join fetch da.owner" );
 		assertTranslation( "from Human h join h.pets p where p.pregnant = 1" );
 		assertTranslation( "from Human h join h.pets p where p.bodyWeight > 100" );
 		assertTranslation( "from Human h left join fetch h.pets" );
 	}
 
 	@Test
 	public void testExplicitCollectionJoins() throws Exception {
 		assertTranslation( "from Animal an inner join an.offspring os" );
 		assertTranslation( "from Animal an left outer join an.offspring os" );
 	}
 
 	@Test
 	public void testExplicitOuterJoinFetch() throws Exception {
 		assertTranslation( "from Animal an left outer join fetch an.offspring" );
 	}
 
 	@Test
 	public void testExplicitOuterJoinFetchWithSelect() throws Exception {
 		assertTranslation( "select an from Animal an left outer join fetch an.offspring" );
 	}
 
 	@Test
 	public void testExplicitJoins() throws Exception {
 		Map replacements = buildTrueFalseReplacementMapForDialect();
 		assertTranslation( "from Zoo zoo join zoo.mammals mam where mam.pregnant = true and mam.description like '%white%'", replacements );
 		assertTranslation( "from Zoo zoo join zoo.animals an where an.description like '%white%'" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-559" )
     public void testMultibyteCharacterConstant() throws Exception {
         assertTranslation( "from Zoo zoo join zoo.animals an where an.description like '%\u4e2d%'" );
     }
 
 	@Test
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
 
 	@Test
 	public void testImplicitJoinInSelect() {
 		assertTranslation( "select foo, foo.long from Foo foo" );
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select foo.foo from Foo foo" );
 		assertTranslation( "select foo, foo.foo from Foo foo" );
 		assertTranslation( "select foo.foo from Foo foo where foo.foo is not null" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
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
 
 	@Test
 	public void testSelectStandardFunctionsNoParens() throws Exception {
 		assertTranslation( "select current_date, current_time, current_timestamp from Animal" );
 	}
 
 	@Test
 	public void testMapIndex() throws Exception {
 		assertTranslation( "from User u where u.permissions['hibernate']='read'" );
 	}
 
 	@Test
 	public void testNamedParameters() throws Exception {
 		assertTranslation( "from Animal an where an.mother.bodyWeight > :weight" );
 	}
 
 	@Test
 	@SkipForDialect( Oracle8iDialect.class )
 	public void testClassProperty() throws Exception {
 		assertTranslation( "from Animal a where a.mother.class = Reptile" );
 	}
 
 	@Test
 	public void testComponent() throws Exception {
 		assertTranslation( "from Human h where h.name.first = 'Gavin'" );
 	}
 
 	@Test
 	public void testSelectEntity() throws Exception {
 		assertTranslation( "select an from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 		assertTranslation( "select mo, an from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	@Test
 	public void testValueAggregate() {
 		assertTranslation( "select max(p), min(p) from User u join u.permissions p" );
 	}
 
 	@Test
 	public void testAggregation() throws Exception {
 		assertTranslation( "select count(an) from Animal an" );
 		assertTranslation( "select count(*) from Animal an" );
 		assertTranslation( "select count(distinct an) from Animal an" );
 		assertTranslation( "select count(distinct an.id) from Animal an" );
 		assertTranslation( "select count(all an.id) from Animal an" );
 	}
 
 	@Test
 	public void testSelectProperty() throws Exception {
 		assertTranslation( "select an.bodyWeight, mo.bodyWeight from Animal an inner join an.mother mo where an.bodyWeight < mo.bodyWeight" );
 	}
 
 	@Test
 	public void testSelectEntityProperty() throws Exception {
 		DotNode.useThetaStyleImplicitJoins = true;
 		assertTranslation( "select an.mother from Animal an" );
 		assertTranslation( "select an, an.mother from Animal an" );
 		DotNode.useThetaStyleImplicitJoins = false;
 	}
 
 	@Test
 	public void testSelectDistinctAll() throws Exception {
 		assertTranslation( "select distinct an.description, an.bodyWeight from Animal an" );
 		assertTranslation( "select all an from Animal an" );
 	}
 
 	@Test
 	public void testSelectAssociatedEntityId() throws Exception {
 		assertTranslation( "select an.mother.id from Animal an" );
 	}
 
 	@Test
 	public void testGroupBy() throws Exception {
 		assertTranslation( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id" );
 		assertTranslation( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id having max(an.bodyWeight)>1.0" );
 	}
 
 	@Test
 	public void testGroupByMultiple() throws Exception {
 		assertTranslation( "select s.id, s.count, count(t), max(t.date) from org.hibernate.test.legacy.Simple s, org.hibernate.test.legacy.Simple t where s.count = t.count group by s.id, s.count order by s.count" );
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		assertTranslation( "from Human h join h.friends f where f.nickName = 'Gavin'" );
 		assertTranslation( "from Human h join h.friends f where f.bodyWeight > 100" );
 	}
 
 	@Test
 	public void testManyToManyElementFunctionInWhere() throws Exception {
 		assertTranslation( "from Human human where human in elements(human.friends)" );
 		assertTranslation( "from Human human where human = some elements(human.friends)" );
 	}
 
 	@Test
 	public void testManyToManyElementFunctionInWhere2() throws Exception {
 		assertTranslation( "from Human h1, Human h2 where h2 in elements(h1.family)" );
 		assertTranslation( "from Human h1, Human h2 where 'father' in indices(h1.family)" );
 	}
 
 	@Test
 	public void testManyToManyFetch() throws Exception {
 		assertTranslation( "from Human h left join fetch h.friends" );
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
index fda32b340c..fd30a8b062 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
@@ -1,1314 +1,1310 @@
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
 package org.hibernate.jpa.boot.internal;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 import javax.persistence.AttributeConverter;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 
 import org.hibernate.Interceptor;
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.jaxb.cfg.JaxbHibernateConfiguration;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.boot.scan.internal.StandardScanOptions;
 import org.hibernate.jpa.boot.scan.internal.StandardScanner;
 import org.hibernate.jpa.boot.scan.spi.ScanOptions;
 import org.hibernate.jpa.boot.scan.spi.ScanResult;
 import org.hibernate.jpa.boot.scan.spi.Scanner;
 import org.hibernate.jpa.boot.spi.ClassDescriptor;
 import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
 import org.hibernate.jpa.boot.spi.InputStreamAccess;
 import org.hibernate.jpa.boot.spi.IntegratorProvider;
 import org.hibernate.jpa.boot.spi.MappingFileDescriptor;
 import org.hibernate.jpa.boot.spi.NamedInputStream;
 import org.hibernate.jpa.boot.spi.PackageDescriptor;
 import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
 import org.hibernate.jpa.boot.spi.StrategyRegistrationProviderList;
 import org.hibernate.jpa.boot.spi.TypeContributorList;
 import org.hibernate.jpa.event.spi.JpaIntegrator;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.schemagen.JpaSchemaGenerator;
 import org.hibernate.jpa.internal.util.LogHelper;
 import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
 import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.spi.TypeContributor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccService;
 import org.hibernate.service.ConfigLoader;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.jboss.jandex.IndexView;
 import org.jboss.jandex.Indexer;
 import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryBuilderImpl implements EntityManagerFactoryBuilder {
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
 			EntityManagerMessageLogger.class,
 			EntityManagerFactoryBuilderImpl.class.getName()
 	);
 
 	private static final String META_INF_ORM_XML = "META-INF/orm.xml";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// New settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	/**
 	 * Names a {@link IntegratorProvider}
 	 */
 	public static final String INTEGRATOR_PROVIDER = "hibernate.integrator_provider";
 	
 	/**
 	 * Names a {@link StrategyRegistrationProviderList}
 	 */
 	public static final String STRATEGY_REGISTRATION_PROVIDERS = "hibernate.strategy_registration_provider";
 	
 	/**
 	 * Names a {@link TypeContributorList}
 	 */
 	public static final String TYPE_CONTRIBUTORS = "hibernate.type_contributors";
 
 	/**
 	 * Names a Jandex {@link Index} instance to use.
 	 */
 	public static final String JANDEX_INDEX = "hibernate.jandex_index";
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Explicit "injectables"
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private Object validatorFactory;
 	private DataSource dataSource;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final PersistenceUnitDescriptor persistenceUnit;
 	private final SettingsImpl settings = new SettingsImpl();
 	private final StandardServiceRegistryBuilder serviceRegistryBuilder;
 	private final Map configurationValues;
 
 	private final List<GrantedPermission> grantedJaccPermissions = new ArrayList<GrantedPermission>();
 	private final List<CacheRegionDefinition> cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
 	// todo : would much prefer this as a local variable...
 	private final List<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping> cfgXmlNamedMappings = new ArrayList<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping>();
 	private Interceptor sessionFactoryInterceptor;
 	private NamingStrategy namingStrategy;
 	private SessionFactoryObserver suppliedSessionFactoryObserver;
 
 	private MetadataSources metadataSources;
 	private Configuration hibernateConfiguration;
 
 	private static EntityNotFoundDelegate jpaEntityNotFoundDelegate = new JpaEntityNotFoundDelegate();
 	
 	private ClassLoader providedClassLoader;
 
 	private static class JpaEntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException( "Unable to find " + entityName  + " with id " + id );
 		}
 	}
 
 	public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
 		this( persistenceUnit, integrationSettings, null );
 	}
 
 	public EntityManagerFactoryBuilderImpl(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map integrationSettings,
 			ClassLoader providedClassLoader ) {
 		
 		LogHelper.logPersistenceUnitInformation( persistenceUnit );
 
 		this.persistenceUnit = persistenceUnit;
 
 		if ( integrationSettings == null ) {
 			integrationSettings = Collections.emptyMap();
 		}
 		
 		this.providedClassLoader = providedClassLoader;
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// First we build the boot-strap service registry, which mainly handles class loader interactions
 		final BootstrapServiceRegistry bootstrapServiceRegistry = buildBootstrapServiceRegistry( integrationSettings );
 		// And the main service registry.  This is needed to start adding configuration values, etc
 		this.serviceRegistryBuilder = new StandardServiceRegistryBuilder( bootstrapServiceRegistry );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Next we build a merged map of all the configuration values
 		this.configurationValues = mergePropertySources( persistenceUnit, integrationSettings, bootstrapServiceRegistry );
 		// add all merged configuration values into the service registry builder
 		this.serviceRegistryBuilder.applySettings( configurationValues );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Next we do a preliminary pass at metadata processing, which involves:
 		//		1) scanning
 		final ScanResult scanResult = scan( bootstrapServiceRegistry );
 		final DeploymentResources deploymentResources = buildDeploymentResources( scanResult, bootstrapServiceRegistry );
 		//		2) building a Jandex index
 		final IndexView jandexIndex = locateOrBuildJandexIndex( deploymentResources );
 		//		3) building "metadata sources" to keep for later to use in building the SessionFactory
 		metadataSources = prepareMetadataSources( jandexIndex, deploymentResources, bootstrapServiceRegistry );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		withValidatorFactory( configurationValues.get( AvailableSettings.VALIDATION_FACTORY ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// push back class transformation to the environment; for the time being this only has any effect in EE
 		// container situations, calling back into PersistenceUnitInfo#addClassTransformer
 		final boolean useClassTransformer = "true".equals( configurationValues.remove( AvailableSettings.USE_CLASS_ENHANCER ) );
 		if ( useClassTransformer ) {
 			persistenceUnit.pushClassTransformer( metadataSources.collectMappingClassNames() );
 		}
 	}
 
 	private static interface DeploymentResources {
 		public Iterable<ClassDescriptor> getClassDescriptors();
 		public Iterable<PackageDescriptor> getPackageDescriptors();
 		public Iterable<MappingFileDescriptor> getMappingFileDescriptors();
 	}
 
 	private DeploymentResources buildDeploymentResources(
 			ScanResult scanResult,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 
 		// mapping files ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final ArrayList<MappingFileDescriptor> mappingFileDescriptors = new ArrayList<MappingFileDescriptor>();
 
 		final Set<String> nonLocatedMappingFileNames = new HashSet<String>();
 		final List<String> explicitMappingFileNames = persistenceUnit.getMappingFileNames();
 		if ( explicitMappingFileNames != null ) {
 			nonLocatedMappingFileNames.addAll( explicitMappingFileNames );
 		}
 
 		for ( MappingFileDescriptor mappingFileDescriptor : scanResult.getLocatedMappingFiles() ) {
 			mappingFileDescriptors.add( mappingFileDescriptor );
 			nonLocatedMappingFileNames.remove( mappingFileDescriptor.getName() );
 		}
 
 		for ( String name : nonLocatedMappingFileNames ) {
 			MappingFileDescriptor descriptor = buildMappingFileDescriptor( name, bootstrapServiceRegistry );
 			mappingFileDescriptors.add( descriptor );
 		}
 
 
 		// classes and packages ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final HashMap<String, ClassDescriptor> classDescriptorMap = new HashMap<String, ClassDescriptor>();
 		final HashMap<String, PackageDescriptor> packageDescriptorMap = new HashMap<String, PackageDescriptor>();
 
 		for ( ClassDescriptor classDescriptor : scanResult.getLocatedClasses() ) {
 			classDescriptorMap.put( classDescriptor.getName(), classDescriptor );
 		}
 
 		for ( PackageDescriptor packageDescriptor : scanResult.getLocatedPackages() ) {
 			packageDescriptorMap.put( packageDescriptor.getName(), packageDescriptor );
 		}
 
 		final List<String> explicitClassNames = persistenceUnit.getManagedClassNames();
 		if ( explicitClassNames != null ) {
 			for ( String explicitClassName : explicitClassNames ) {
 				// IMPL NOTE : explicitClassNames can contain class or package names!!!
 				if ( classDescriptorMap.containsKey( explicitClassName ) ) {
 					continue;
 				}
 				if ( packageDescriptorMap.containsKey( explicitClassName ) ) {
 					continue;
 				}
 
 				// try it as a class name first...
 				final String classFileName = explicitClassName.replace( '.', '/' ) + ".class";
 				final URL classFileUrl = bootstrapServiceRegistry.getService( ClassLoaderService.class )
 						.locateResource( classFileName );
 				if ( classFileUrl != null ) {
 					classDescriptorMap.put(
 							explicitClassName,
 							new ClassDescriptorImpl( explicitClassName, new UrlInputStreamAccess( classFileUrl ) )
 					);
 					continue;
 				}
 
 				// otherwise, try it as a package name
 				final String packageInfoFileName = explicitClassName.replace( '.', '/' ) + "/package-info.class";
 				final URL packageInfoFileUrl = bootstrapServiceRegistry.getService( ClassLoaderService.class )
 						.locateResource( packageInfoFileName );
 				if ( packageInfoFileUrl != null ) {
 					packageDescriptorMap.put(
 							explicitClassName,
 							new PackageDescriptorImpl( explicitClassName, new UrlInputStreamAccess( packageInfoFileUrl ) )
 					);
 					continue;
 				}
 
 				LOG.debugf(
 						"Unable to resolve class [%s] named in persistence unit [%s]",
 						explicitClassName,
 						persistenceUnit.getName()
 				);
 			}
 		}
 
 		return new DeploymentResources() {
 			@Override
 			public Iterable<ClassDescriptor> getClassDescriptors() {
 				return classDescriptorMap.values();
 			}
 
 			@Override
 			public Iterable<PackageDescriptor> getPackageDescriptors() {
 				return packageDescriptorMap.values();
 			}
 
 			@Override
 			public Iterable<MappingFileDescriptor> getMappingFileDescriptors() {
 				return mappingFileDescriptors;
 			}
 		};
 	}
 
 	private MappingFileDescriptor buildMappingFileDescriptor(
 			String name,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final URL url = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResource( name );
 		if ( url == null ) {
 			throw persistenceException( "Unable to resolve named mapping-file [" + name + "]" );
 		}
 
 		return new MappingFileDescriptorImpl( name, new UrlInputStreamAccess( url ) );
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// temporary!
 	@SuppressWarnings("unchecked")
 	public Map getConfigurationValues() {
 		return Collections.unmodifiableMap( configurationValues );
 	}
 
 	public Configuration getHibernateConfiguration() {
 		return hibernateConfiguration;
 	}
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	@SuppressWarnings("unchecked")
 	private MetadataSources prepareMetadataSources(
 			IndexView jandexIndex,
 			DeploymentResources deploymentResources,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		// todo : this needs to tie into the metamodel branch...
 		MetadataSources metadataSources = new MetadataSources();
 
 		for ( ClassDescriptor classDescriptor : deploymentResources.getClassDescriptors() ) {
 			final String className = classDescriptor.getName();
 			final ClassInfo classInfo = jandexIndex.getClassByName( DotName.createSimple( className ) );
 			if ( classInfo == null ) {
 				// Not really sure what this means.  Most likely it is explicitly listed in the persistence unit,
 				// but mapped via mapping file.  Anyway assume its a mapping class...
 				metadataSources.annotatedMappingClassNames.add( className );
 				continue;
 			}
 
 			// logic here assumes an entity is not also a converter...
 			AnnotationInstance converterAnnotation = JandexHelper.getSingleAnnotation(
 					classInfo.annotations(),
 					JPADotNames.CONVERTER
 			);
 			if ( converterAnnotation != null ) {
 				metadataSources.converterDescriptors.add(
 						new MetadataSources.ConverterDescriptor(
 								className,
 								JandexHelper.getValue( converterAnnotation, "autoApply", boolean.class,
 										bootstrapServiceRegistry.getService( ClassLoaderService.class ) )
 						)
 				);
 			}
 			else {
 				metadataSources.annotatedMappingClassNames.add( className );
 			}
 		}
 
 		for ( PackageDescriptor packageDescriptor : deploymentResources.getPackageDescriptors() ) {
 			metadataSources.packageNames.add( packageDescriptor.getName() );
 		}
 
 		for ( MappingFileDescriptor mappingFileDescriptor : deploymentResources.getMappingFileDescriptors() ) {
 			metadataSources.namedMappingFileInputStreams.add( mappingFileDescriptor.getStreamAccess().asNamedInputStream() );
 		}
 
 		final String explicitHbmXmls = (String) configurationValues.remove( AvailableSettings.HBXML_FILES );
 		if ( explicitHbmXmls != null ) {
 			metadataSources.mappingFileResources.addAll( Arrays.asList( StringHelper.split( ", ", explicitHbmXmls ) ) );
 		}
 
 		final List<String> explicitOrmXml = (List<String>) configurationValues.remove( AvailableSettings.XML_FILE_NAMES );
 		if ( explicitOrmXml != null ) {
 			metadataSources.mappingFileResources.addAll( explicitOrmXml );
 		}
 
 		return metadataSources;
 	}
 
 	private IndexView locateOrBuildJandexIndex(DeploymentResources deploymentResources) {
 		// for now create a whole new Index to work with, eventually we need to:
 		//		1) accept an Index as an incoming config value
 		//		2) pass that Index along to the metamodel code...
 		IndexView jandexIndex = (IndexView) configurationValues.get( JANDEX_INDEX );
 		if ( jandexIndex == null ) {
 			jandexIndex = buildJandexIndex( deploymentResources );
 		}
 		return jandexIndex;
 	}
 
 	private IndexView buildJandexIndex(DeploymentResources deploymentResources) {
 		Indexer indexer = new Indexer();
 
 		for ( ClassDescriptor classDescriptor : deploymentResources.getClassDescriptors() ) {
 			indexStream( indexer, classDescriptor.getStreamAccess() );
 		}
 
 		for ( PackageDescriptor packageDescriptor : deploymentResources.getPackageDescriptors() ) {
 			indexStream( indexer, packageDescriptor.getStreamAccess() );
 		}
 
 		// for now we just skip entities defined in (1) orm.xml files and (2) hbm.xml files.  this part really needs
 		// metamodel branch...
 
 		// for now, we also need to wrap this in a CompositeIndex until Jandex is updated to use a common interface
 		// between the 2...
 		return indexer.complete();
 	}
 
 	private void indexStream(Indexer indexer, InputStreamAccess streamAccess) {
 		try {
 			InputStream stream = streamAccess.accessInputStream();
 			try {
 				indexer.index( stream );
 			}
 			finally {
 				try {
 					stream.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw persistenceException( "Unable to index from stream " + streamAccess.getStreamName(), e );
 		}
 	}
 
 	/**
 	 * Builds the {@link BootstrapServiceRegistry} used to eventually build the {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}; mainly
 	 * used here during instantiation to define class-loading behavior.
 	 *
 	 * @param integrationSettings Any integration settings passed by the EE container or SE application
 	 *
 	 * @return The built BootstrapServiceRegistry
 	 */
 	private BootstrapServiceRegistry buildBootstrapServiceRegistry(Map integrationSettings) {
 		final BootstrapServiceRegistryBuilder bootstrapServiceRegistryBuilder = new BootstrapServiceRegistryBuilder();
 		bootstrapServiceRegistryBuilder.with( new JpaIntegrator() );
 
 		final IntegratorProvider integratorProvider = (IntegratorProvider) integrationSettings.get( INTEGRATOR_PROVIDER );
 		if ( integratorProvider != null ) {
 			for ( Integrator integrator : integratorProvider.getIntegrators() ) {
 				bootstrapServiceRegistryBuilder.with( integrator );
 			}
 		}
 		
 		final StrategyRegistrationProviderList strategyRegistrationProviderList
 				= (StrategyRegistrationProviderList) integrationSettings.get( STRATEGY_REGISTRATION_PROVIDERS );
 		if ( strategyRegistrationProviderList != null ) {
 			for ( StrategyRegistrationProvider strategyRegistrationProvider : strategyRegistrationProviderList
 					.getStrategyRegistrationProviders() ) {
 				bootstrapServiceRegistryBuilder.withStrategySelectors( strategyRegistrationProvider );
 			}
 		}
 
 		// TODO: If providedClassLoader is present (OSGi, etc.) *and*
 		// an APP_CLASSLOADER is provided, should throw an exception or
 		// warn?
 		ClassLoader classLoader;
 		ClassLoader appClassLoader = (ClassLoader) integrationSettings.get( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		if ( providedClassLoader != null ) {
 			classLoader = providedClassLoader;
 		}
 		else if ( appClassLoader != null ) {
 			classLoader = appClassLoader;
 		}
 		else {
 			classLoader = persistenceUnit.getClassLoader();
 		}
 		bootstrapServiceRegistryBuilder.with( classLoader );
 
 		return bootstrapServiceRegistryBuilder.build();
 	}
 
 	@SuppressWarnings("unchecked")
 	private Map mergePropertySources(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map integrationSettings,
 			final BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Map merged = new HashMap();
 		// first, apply persistence.xml-defined settings
 		if ( persistenceUnit.getProperties() != null ) {
 			merged.putAll( persistenceUnit.getProperties() );
 		}
 
 		merged.put( AvailableSettings.PERSISTENCE_UNIT_NAME, persistenceUnit.getName() );
 
 		// see if the persistence.xml settings named a Hibernate config file....
 		final ValueHolder<ConfigLoader> configLoaderHolder = new ValueHolder<ConfigLoader>(
 				new ValueHolder.DeferredInitializer<ConfigLoader>() {
 					@Override
 					public ConfigLoader initialize() {
 						return new ConfigLoader( bootstrapServiceRegistry );
 					}
 				}
 		);
 
-		{
-			final String cfgXmlResourceName = (String) merged.remove( AvailableSettings.CFG_FILE );
-			if ( StringHelper.isNotEmpty( cfgXmlResourceName ) ) {
-				// it does, so load those properties
-				JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue()
-						.loadConfigXmlResource( cfgXmlResourceName );
-				processHibernateConfigurationElement( configurationElement, merged );
-			}
+		final String cfgXmlResourceName1 = (String) merged.remove( AvailableSettings.CFG_FILE );
+		if ( StringHelper.isNotEmpty( cfgXmlResourceName1 ) ) {
+			// it does, so load those properties
+			JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue()
+					.loadConfigXmlResource( cfgXmlResourceName1 );
+			processHibernateConfigurationElement( configurationElement, merged );
 		}
 
 		// see if integration settings named a Hibernate config file....
-		{
-			final String cfgXmlResourceName = (String) integrationSettings.get( AvailableSettings.CFG_FILE );
-			if ( StringHelper.isNotEmpty( cfgXmlResourceName ) ) {
-				integrationSettings.remove( AvailableSettings.CFG_FILE );
-				// it does, so load those properties
-				JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue().loadConfigXmlResource(
-						cfgXmlResourceName
-				);
-				processHibernateConfigurationElement( configurationElement, merged );
-			}
+		final String cfgXmlResourceName2 = (String) integrationSettings.get( AvailableSettings.CFG_FILE );
+		if ( StringHelper.isNotEmpty( cfgXmlResourceName2 ) ) {
+			integrationSettings.remove( AvailableSettings.CFG_FILE );
+			// it does, so load those properties
+			JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue().loadConfigXmlResource(
+					cfgXmlResourceName2
+			);
+			processHibernateConfigurationElement( configurationElement, merged );
 		}
 
 		// finally, apply integration-supplied settings (per JPA spec, integration settings should override other sources)
 		merged.putAll( integrationSettings );
 
-		if ( ! merged.containsKey( AvailableSettings.VALIDATION_MODE ) ) {
+		if ( !merged.containsKey( AvailableSettings.VALIDATION_MODE ) ) {
 			if ( persistenceUnit.getValidationMode() != null ) {
 				merged.put( AvailableSettings.VALIDATION_MODE, persistenceUnit.getValidationMode() );
 			}
 		}
 
-		if ( ! merged.containsKey( AvailableSettings.SHARED_CACHE_MODE ) ) {
+		if ( !merged.containsKey( AvailableSettings.SHARED_CACHE_MODE ) ) {
 			if ( persistenceUnit.getSharedCacheMode() != null ) {
 				merged.put( AvailableSettings.SHARED_CACHE_MODE, persistenceUnit.getSharedCacheMode() );
 			}
 		}
 
 		// was getting NPE exceptions from the underlying map when just using #putAll, so going this safer route...
 		Iterator itr = merged.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			if ( entry.getValue() == null ) {
 				itr.remove();
 			}
 		}
 
 		return merged;
 	}
 
 	@SuppressWarnings("unchecked")
 	private void processHibernateConfigurationElement(
 			JaxbHibernateConfiguration configurationElement,
 			Map mergeMap) {
 		if ( ! mergeMap.containsKey( org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME ) ) {
 			String cfgName = configurationElement.getSessionFactory().getName();
 			if ( cfgName != null ) {
 				mergeMap.put( org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME, cfgName );
 			}
 		}
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbProperty jaxbProperty : configurationElement.getSessionFactory().getProperty() ) {
 			mergeMap.put( jaxbProperty.getName(), jaxbProperty.getValue() );
 		}
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping jaxbMapping : configurationElement.getSessionFactory().getMapping() ) {
 			cfgXmlNamedMappings.add( jaxbMapping );
 		}
 
 		for ( Object cacheDeclaration : configurationElement.getSessionFactory().getClassCacheOrCollectionCache() ) {
 			if ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache.class.isInstance( cacheDeclaration ) ) {
 				final JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache jaxbClassCache
 						= (JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache) cacheDeclaration;
 				cacheRegionDefinitions.add(
 						new CacheRegionDefinition(
 								CacheRegionDefinition.CacheType.ENTITY,
 								jaxbClassCache.getClazz(),
 								jaxbClassCache.getUsage().value(),
 								jaxbClassCache.getRegion(),
 								"all".equals( jaxbClassCache.getInclude() )
 						)
 				);
 			}
 			else {
 				final JaxbHibernateConfiguration.JaxbSessionFactory.JaxbCollectionCache jaxbCollectionCache
 						= (JaxbHibernateConfiguration.JaxbSessionFactory.JaxbCollectionCache) cacheDeclaration;
 				cacheRegionDefinitions.add(
 						new CacheRegionDefinition(
 								CacheRegionDefinition.CacheType.COLLECTION,
 								jaxbCollectionCache.getCollection(),
 								jaxbCollectionCache.getUsage().value(),
 								jaxbCollectionCache.getRegion(),
 								false
 						)
 				);
 			}
 		}
 
 		if ( configurationElement.getSecurity() != null ) {
 			for ( JaxbHibernateConfiguration.JaxbSecurity.JaxbGrant grant : configurationElement.getSecurity().getGrant() ) {
 				grantedJaccPermissions.add(
 						new GrantedPermission(
 								grant.getRole(),
 								grant.getEntityName(),
 								grant.getActions()
 						)
 				);
 			}
 		}
 	}
 
 	private String jaccContextId;
 
 	private void addJaccDefinition(String key, Object value) {
 		if ( jaccContextId == null ) {
 			jaccContextId = (String) configurationValues.get( AvailableSettings.JACC_CONTEXT_ID );
 			if ( jaccContextId == null ) {
 				throw persistenceException(
 						"Entities have been configured for JACC, but "
 								+ AvailableSettings.JACC_CONTEXT_ID + " has not been set"
 				);
 			}
 		}
 
 		try {
 			final int roleStart = AvailableSettings.JACC_PREFIX.length() + 1;
 			final String role = key.substring( roleStart, key.indexOf( '.', roleStart ) );
 			final int classStart = roleStart + role.length() + 1;
 			final String clazz = key.substring( classStart, key.length() );
 
 			grantedJaccPermissions.add( new GrantedPermission( role, clazz, (String) value ) );
 		}
 		catch ( IndexOutOfBoundsException e ) {
 			throw persistenceException( "Illegal usage of " + AvailableSettings.JACC_PREFIX + ": " + key );
 		}
 	}
 
 	private void addCacheRegionDefinition(String role, String value, CacheRegionDefinition.CacheType cacheType) {
 		final StringTokenizer params = new StringTokenizer( value, ";, " );
 		if ( !params.hasMoreTokens() ) {
 			StringBuilder error = new StringBuilder( "Illegal usage of " );
 			if ( cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 				error.append( AvailableSettings.CLASS_CACHE_PREFIX )
 						.append( ": " )
 						.append( AvailableSettings.CLASS_CACHE_PREFIX );
 			}
 			else {
 				error.append( AvailableSettings.COLLECTION_CACHE_PREFIX )
 						.append( ": " )
 						.append( AvailableSettings.COLLECTION_CACHE_PREFIX );
 			}
 			error.append( '.' )
 					.append( role )
 					.append( ' ' )
 					.append( value )
 					.append( ".  Was expecting configuration, but found none" );
 			throw persistenceException( error.toString() );
 		}
 
 		String usage = params.nextToken();
 		String region = null;
 		if ( params.hasMoreTokens() ) {
 			region = params.nextToken();
 		}
 		boolean lazyProperty = true;
 		if ( cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 			if ( params.hasMoreTokens() ) {
 				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
 			}
 		}
 		else {
 			lazyProperty = false;
 		}
 
 		final CacheRegionDefinition def = new CacheRegionDefinition( cacheType, role, usage, region, lazyProperty );
 		cacheRegionDefinitions.add( def );
 	}
 
 	@SuppressWarnings("unchecked")
 	private ScanResult scan(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Scanner scanner = locateOrBuildScanner( bootstrapServiceRegistry );
 		final ScanOptions scanOptions = determineScanOptions();
 
 		return scanner.scan( persistenceUnit, scanOptions );
 	}
 
 	private ScanOptions determineScanOptions() {
 		return new StandardScanOptions(
 				(String) configurationValues.get( AvailableSettings.AUTODETECTION ),
 				persistenceUnit.isExcludeUnlistedClasses()
 		);
 	}
 
 	@SuppressWarnings("unchecked")
 	private Scanner locateOrBuildScanner(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Object value = configurationValues.remove( AvailableSettings.SCANNER );
 		if ( value == null ) {
 			return new StandardScanner();
 		}
 
 		if ( Scanner.class.isInstance( value ) ) {
 			return (Scanner) value;
 		}
 
 		Class<? extends Scanner> scannerClass;
 		if ( Class.class.isInstance( value ) ) {
 			try {
 				scannerClass = (Class<? extends Scanner>) value;
 			}
 			catch ( ClassCastException e ) {
 				throw persistenceException( "Expecting Scanner implementation, but found " + ((Class) value).getName() );
 			}
 		}
 		else {
 			final String scannerClassName = value.toString();
 			try {
 				scannerClass = bootstrapServiceRegistry.getService( ClassLoaderService.class ).classForName( scannerClassName );
 			}
 			catch ( ClassCastException e ) {
 				throw persistenceException( "Expecting Scanner implementation, but found " + scannerClassName );
 			}
 		}
 
 		try {
 			return scannerClass.newInstance();
 		}
 		catch ( Exception e ) {
 			throw persistenceException( "Unable to instantiate Scanner class: " + scannerClass, e );
 		}
 	}
 
 	@Override
 	public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory) {
 		this.validatorFactory = validatorFactory;
 
 		if ( validatorFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validatorFactory );
 		}
 		return this;
 	}
 
 	@Override
 	public EntityManagerFactoryBuilder withDataSource(DataSource dataSource) {
 		this.dataSource = dataSource;
 
 		return this;
 	}
 
 	@Override
 	public void cancel() {
 		// todo : close the bootstrap registry (not critical, but nice to do)
 
 	}
 
 	@Override
 	public void generateSchema() {
 		processProperties();
 
 		final ServiceRegistry serviceRegistry = buildServiceRegistry();
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 		// IMPL NOTE : TCCL handling here is temporary.
 		//		It is needed because this code still uses Hibernate Configuration and Hibernate commons-annotations
 		// 		in turn which relies on TCCL being set.
 
 		( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
 				new ClassLoaderServiceImpl.Work() {
 					@Override
 					public Object perform() {
 						final Configuration hibernateConfiguration = buildHibernateConfiguration( serviceRegistry );
 						
 						// This seems overkill, but building the SF is necessary to get the Integrators to kick in.
 						// Metamodel will clean this up...
 						try {
 							hibernateConfiguration.buildSessionFactory( serviceRegistry );
 						}
 						catch (MappingException e) {
 							throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 						}
 						
 						JpaSchemaGenerator.performGeneration( hibernateConfiguration, serviceRegistry );
 						
 						return null;
 					}
 				}
 		);
 
 		// release this builder
 		cancel();
 	}
 
 	@SuppressWarnings("unchecked")
 	public EntityManagerFactory build() {
 		processProperties();
 
 		final ServiceRegistry serviceRegistry = buildServiceRegistry();
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 		// IMPL NOTE : TCCL handling here is temporary.
 		//		It is needed because this code still uses Hibernate Configuration and Hibernate commons-annotations
 		// 		in turn which relies on TCCL being set.
 
 		return ( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
 				new ClassLoaderServiceImpl.Work<EntityManagerFactoryImpl>() {
 					@Override
 					public EntityManagerFactoryImpl perform() {
 						hibernateConfiguration = buildHibernateConfiguration( serviceRegistry );
 
 						SessionFactoryImplementor sessionFactory;
 						try {
 							sessionFactory = (SessionFactoryImplementor) hibernateConfiguration.buildSessionFactory( serviceRegistry );
 						}
 						catch (MappingException e) {
 							throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 						}
 						
 						// must do after buildSessionFactory to let the Integrators kick in
 						JpaSchemaGenerator.performGeneration( hibernateConfiguration, serviceRegistry );
 
 						if ( suppliedSessionFactoryObserver != null ) {
 							sessionFactory.addObserver( suppliedSessionFactoryObserver );
 						}
 						sessionFactory.addObserver( new ServiceRegistryCloser() );
 
 						// NOTE : passing cfg is temporary until
 						return new EntityManagerFactoryImpl(
 								persistenceUnit.getName(),
 								sessionFactory,
 								settings,
 								configurationValues,
 								hibernateConfiguration
 						);
 					}
 				}
 		);
 	}
 
 	private void processProperties() {
 		applyJdbcConnectionProperties();
 		applyTransactionProperties();
 
 		Object validationFactory = this.validatorFactory;
 		if ( validationFactory == null ) {
 			validationFactory = configurationValues.get( AvailableSettings.VALIDATION_FACTORY );
 		}
 		if ( validationFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validationFactory );
 			serviceRegistryBuilder.applySetting( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 			configurationValues.put( AvailableSettings.VALIDATION_FACTORY, this.validatorFactory );
 		}
 
 		// flush before completion validation
 		if ( "true".equals( configurationValues.get( Environment.FLUSH_BEFORE_COMPLETION ) ) ) {
 			serviceRegistryBuilder.applySetting( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 			LOG.definingFlushBeforeCompletionIgnoredInHem( Environment.FLUSH_BEFORE_COMPLETION );
 		}
 
 		final StrategySelector strategySelector = serviceRegistryBuilder.getBootstrapServiceRegistry().getService( StrategySelector.class );
 
 		for ( Object oEntry : configurationValues.entrySet() ) {
 			Map.Entry entry = (Map.Entry) oEntry;
 			if ( entry.getKey() instanceof String ) {
 				final String keyString = (String) entry.getKey();
 
 				if ( AvailableSettings.INTERCEPTOR.equals( keyString ) ) {
 					sessionFactoryInterceptor = strategySelector.resolveStrategy( Interceptor.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.SESSION_INTERCEPTOR.equals( keyString ) ) {
 					settings.setSessionInterceptorClass(
 							loadSessionInterceptorClass( entry.getValue(), strategySelector )
 					);
 				}
 				else if ( AvailableSettings.NAMING_STRATEGY.equals( keyString ) ) {
 					namingStrategy = strategySelector.resolveStrategy( NamingStrategy.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.SESSION_FACTORY_OBSERVER.equals( keyString ) ) {
 					suppliedSessionFactoryObserver = strategySelector.resolveStrategy( SessionFactoryObserver.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.DISCARD_PC_ON_CLOSE.equals( keyString ) ) {
 					settings.setReleaseResourcesOnCloseEnabled( "true".equals( entry.getValue() ) );
 				}
 				else if ( keyString.startsWith( AvailableSettings.CLASS_CACHE_PREFIX ) ) {
 					addCacheRegionDefinition(
 							keyString.substring( AvailableSettings.CLASS_CACHE_PREFIX.length() + 1 ),
 							(String) entry.getValue(),
 							CacheRegionDefinition.CacheType.ENTITY
 					);
 				}
 				else if ( keyString.startsWith( AvailableSettings.COLLECTION_CACHE_PREFIX ) ) {
 					addCacheRegionDefinition(
 							keyString.substring( AvailableSettings.COLLECTION_CACHE_PREFIX.length() + 1 ),
 							(String) entry.getValue(),
 							CacheRegionDefinition.CacheType.COLLECTION
 					);
 				}
 				else if ( keyString.startsWith( AvailableSettings.JACC_PREFIX )
 						&& ! ( keyString.equals( AvailableSettings.JACC_CONTEXT_ID )
 						|| keyString.equals( AvailableSettings.JACC_ENABLED ) ) ) {
 					addJaccDefinition( (String) entry.getKey(), entry.getValue() );
 				}
 			}
 		}
 	}
 
 	private void applyJdbcConnectionProperties() {
 		if ( dataSource != null ) {
 			serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, dataSource );
 		}
 		else if ( persistenceUnit.getJtaDataSource() != null ) {
 			if ( ! serviceRegistryBuilder.getSettings().containsKey( org.hibernate.cfg.AvailableSettings.DATASOURCE ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, persistenceUnit.getJtaDataSource() );
 				// HHH-8121 : make the PU-defined value available to EMF.getProperties()
 				configurationValues.put( AvailableSettings.JTA_DATASOURCE, persistenceUnit.getJtaDataSource() );
 			}
 		}
 		else if ( persistenceUnit.getNonJtaDataSource() != null ) {
 			if ( ! serviceRegistryBuilder.getSettings().containsKey( org.hibernate.cfg.AvailableSettings.DATASOURCE ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 				// HHH-8121 : make the PU-defined value available to EMF.getProperties()
 				configurationValues.put( AvailableSettings.NON_JTA_DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 			}
 		}
 		else {
 			final String driver = (String) configurationValues.get( AvailableSettings.JDBC_DRIVER );
 			if ( StringHelper.isNotEmpty( driver ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DRIVER, driver );
 			}
 			final String url = (String) configurationValues.get( AvailableSettings.JDBC_URL );
 			if ( StringHelper.isNotEmpty( url ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.URL, url );
 			}
 			final String user = (String) configurationValues.get( AvailableSettings.JDBC_USER );
 			if ( StringHelper.isNotEmpty( user ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.USER, user );
 			}
 			final String pass = (String) configurationValues.get( AvailableSettings.JDBC_PASSWORD );
 			if ( StringHelper.isNotEmpty( pass ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.PASS, pass );
 			}
 		}
 	}
 
 	private void applyTransactionProperties() {
 		PersistenceUnitTransactionType txnType = PersistenceUnitTransactionTypeHelper.interpretTransactionType(
 				configurationValues.get( AvailableSettings.TRANSACTION_TYPE )
 		);
 		if ( txnType == null ) {
 			txnType = persistenceUnit.getTransactionType();
 		}
 		if ( txnType == null ) {
 			// is it more appropriate to have this be based on bootstrap entry point (EE vs SE)?
 			txnType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 		}
 		settings.setTransactionType( txnType );
 		boolean hasTxStrategy = configurationValues.containsKey( Environment.TRANSACTION_STRATEGY );
 		if ( hasTxStrategy ) {
 			LOG.overridingTransactionStrategyDangerous( Environment.TRANSACTION_STRATEGY );
 		}
 		else {
 			if ( txnType == PersistenceUnitTransactionType.JTA ) {
 				serviceRegistryBuilder.applySetting( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class );
 			}
 			else if ( txnType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 				serviceRegistryBuilder.applySetting( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class );
 			}
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	private Class<? extends Interceptor> loadSessionInterceptorClass(Object value, StrategySelector strategySelector) {
 		if ( value == null ) {
 			return null;
 		}
 
 		return Class.class.isInstance( value )
 				? (Class<? extends Interceptor>) value
 				: strategySelector.selectStrategyImplementor( Interceptor.class, value.toString() );
 	}
 
 	public ServiceRegistry buildServiceRegistry() {
 		return serviceRegistryBuilder.build();
 	}
 
 	public Configuration buildHibernateConfiguration(ServiceRegistry serviceRegistry) {
 		Properties props = new Properties();
 		props.putAll( configurationValues );
 		Configuration cfg = new Configuration();
 		cfg.getProperties().putAll( props );
 
 		cfg.setEntityNotFoundDelegate( jpaEntityNotFoundDelegate );
 
 		if ( namingStrategy != null ) {
 			cfg.setNamingStrategy( namingStrategy );
 		}
 
 		if ( sessionFactoryInterceptor != null ) {
 			cfg.setInterceptor( sessionFactoryInterceptor );
 		}
 
 		final Object strategyProviderValue = props.get( AvailableSettings.IDENTIFIER_GENERATOR_STRATEGY_PROVIDER );
 		final IdentifierGeneratorStrategyProvider strategyProvider = strategyProviderValue == null
 				? null
 				: serviceRegistry.getService( StrategySelector.class )
 						.resolveStrategy( IdentifierGeneratorStrategyProvider.class, strategyProviderValue );
 
 		if ( strategyProvider != null ) {
 			final MutableIdentifierGeneratorFactory identifierGeneratorFactory = cfg.getIdentifierGeneratorFactory();
 			for ( Map.Entry<String,Class<?>> entry : strategyProvider.getStrategies().entrySet() ) {
 				identifierGeneratorFactory.register( entry.getKey(), entry.getValue() );
 			}
 		}
 
 		if ( grantedJaccPermissions != null ) {
 			final JaccService jaccService = serviceRegistry.getService( JaccService.class );
 			for ( GrantedPermission grantedPermission : grantedJaccPermissions ) {
 				jaccService.addPermission( grantedPermission );
 			}
 		}
 
 		if ( cacheRegionDefinitions != null ) {
 			for ( CacheRegionDefinition cacheRegionDefinition : cacheRegionDefinitions ) {
 				if ( cacheRegionDefinition.cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 					cfg.setCacheConcurrencyStrategy(
 							cacheRegionDefinition.role,
 							cacheRegionDefinition.usage,
 							cacheRegionDefinition.region,
 							cacheRegionDefinition.cacheLazy
 					);
 				}
 				else {
 					cfg.setCollectionCacheConcurrencyStrategy(
 							cacheRegionDefinition.role,
 							cacheRegionDefinition.usage,
 							cacheRegionDefinition.region
 					);
 				}
 			}
 		}
 
 
 		// todo : need to have this use the metamodel codebase eventually...
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping jaxbMapping : cfgXmlNamedMappings ) {
 			if ( jaxbMapping.getClazz() != null ) {
 				cfg.addAnnotatedClass(
 						serviceRegistry.getService( ClassLoaderService.class ).classForName( jaxbMapping.getClazz() )
 				);
 			}
 			else if ( jaxbMapping.getResource() != null ) {
 				cfg.addResource( jaxbMapping.getResource() );
 			}
 			else if ( jaxbMapping.getJar() != null ) {
 				cfg.addJar( new File( jaxbMapping.getJar() ) );
 			}
 			else if ( jaxbMapping.getPackage() != null ) {
 				cfg.addPackage( jaxbMapping.getPackage() );
 			}
 		}
 
 		List<Class> loadedAnnotatedClasses = (List<Class>) configurationValues.remove( AvailableSettings.LOADED_CLASSES );
 		if ( loadedAnnotatedClasses != null ) {
 			for ( Class cls : loadedAnnotatedClasses ) {
 				if ( AttributeConverter.class.isAssignableFrom( cls ) ) {
 					cfg.addAttributeConverter( (Class<? extends AttributeConverter>) cls );
 				}
 				else {
 					cfg.addAnnotatedClass( cls );
 				}
 			}
 		}
 
 		for ( String className : metadataSources.getAnnotatedMappingClassNames() ) {
 			cfg.addAnnotatedClass( serviceRegistry.getService( ClassLoaderService.class ).classForName( className ) );
 		}
 
 		for ( MetadataSources.ConverterDescriptor converterDescriptor : metadataSources.getConverterDescriptors() ) {
 			final Class<? extends AttributeConverter> converterClass;
 			try {
 				Class theClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( converterDescriptor.converterClassName );
 				converterClass = (Class<? extends AttributeConverter>) theClass;
 			}
 			catch (ClassCastException e) {
 				throw persistenceException(
 						String.format(
 								"AttributeConverter implementation [%s] does not implement AttributeConverter interface",
 								converterDescriptor.converterClassName
 						)
 				);
 			}
 			cfg.addAttributeConverter( converterClass, converterDescriptor.autoApply );
 		}
 
 		for ( String resourceName : metadataSources.mappingFileResources ) {
 			Boolean useMetaInf = null;
 			try {
 				if ( resourceName.endsWith( META_INF_ORM_XML ) ) {
 					useMetaInf = true;
 				}
 				cfg.addResource( resourceName );
 			}
 			catch( MappingNotFoundException e ) {
 				if ( ! resourceName.endsWith( META_INF_ORM_XML ) ) {
 					throw persistenceException( "Unable to find XML mapping file in classpath: " + resourceName );
 				}
 				else {
 					useMetaInf = false;
 					//swallow it, the META-INF/orm.xml is optional
 				}
 			}
 			catch( MappingException me ) {
 				throw persistenceException( "Error while reading JPA XML file: " + resourceName, me );
 			}
 
 			if ( Boolean.TRUE.equals( useMetaInf ) ) {
 				LOG.exceptionHeaderFound( getExceptionHeader(), META_INF_ORM_XML );
 			}
 			else if (Boolean.FALSE.equals(useMetaInf)) {
 				LOG.exceptionHeaderNotFound( getExceptionHeader(), META_INF_ORM_XML );
 			}
 		}
 		for ( NamedInputStream namedInputStream : metadataSources.namedMappingFileInputStreams ) {
 			try {
 				//addInputStream has the responsibility to close the stream
 				cfg.addInputStream( new BufferedInputStream( namedInputStream.getStream() ) );
 			}
 			catch ( InvalidMappingException e ) {
 				// try our best to give the file name
 				if ( StringHelper.isNotEmpty( namedInputStream.getName() ) ) {
 					throw new InvalidMappingException(
 							"Error while parsing file: " + namedInputStream.getName(),
 							e.getType(),
 							e.getPath(),
 							e
 					);
 				}
 				else {
 					throw e;
 				}
 			}
 			catch (MappingException me) {
 				// try our best to give the file name
 				if ( StringHelper.isNotEmpty( namedInputStream.getName() ) ) {
 					throw new MappingException("Error while parsing file: " + namedInputStream.getName(), me );
 				}
 				else {
 					throw me;
 				}
 			}
 		}
 		for ( String packageName : metadataSources.packageNames ) {
 			cfg.addPackage( packageName );
 		}
 		
 		final TypeContributorList typeContributorList
 				= (TypeContributorList) configurationValues.get( TYPE_CONTRIBUTORS );
 		if ( typeContributorList != null ) {
 			configurationValues.remove( TYPE_CONTRIBUTORS );
 			for ( TypeContributor typeContributor : typeContributorList.getTypeContributors() ) {
 				cfg.registerTypeContributor( typeContributor );
 			}
 		}
 		
 		return cfg;
 	}
 
 	public static class ServiceRegistryCloser implements SessionFactoryObserver {
 		@Override
 		public void sessionFactoryCreated(SessionFactory sessionFactory) {
 			// nothing to do
 		}
 
 		@Override
 		public void sessionFactoryClosed(SessionFactory sessionFactory) {
 			SessionFactoryImplementor sfi = ( (SessionFactoryImplementor) sessionFactory );
 			sfi.getServiceRegistry().destroy();
 			ServiceRegistry basicRegistry = sfi.getServiceRegistry().getParentServiceRegistry();
 			( (ServiceRegistryImplementor) basicRegistry ).destroy();
 		}
 	}
 
 	private PersistenceException persistenceException(String message) {
 		return persistenceException( message, null );
 	}
 
 	private PersistenceException persistenceException(String message, Exception cause) {
 		return new PersistenceException(
 				getExceptionHeader() + message,
 				cause
 		);
 	}
 
 	private String getExceptionHeader() {
 		return "[PersistenceUnit: " + persistenceUnit.getName() + "] ";
 	}
 
 	public static class CacheRegionDefinition {
 		public static enum CacheType { ENTITY, COLLECTION }
 
 		public final CacheType cacheType;
 		public final String role;
 		public final String usage;
 		public final String region;
 		public final boolean cacheLazy;
 
 		public CacheRegionDefinition(
 				CacheType cacheType,
 				String role,
 				String usage,
 				String region, boolean cacheLazy) {
 			this.cacheType = cacheType;
 			this.role = role;
 			this.usage = usage;
 			this.region = region;
 			this.cacheLazy = cacheLazy;
 		}
 	}
 
 	public static class JaccDefinition {
 		public final String contextId;
 		public final String role;
 		public final String clazz;
 		public final String actions;
 
 		public JaccDefinition(String contextId, String role, String clazz, String actions) {
 			this.contextId = contextId;
 			this.role = role;
 			this.clazz = clazz;
 			this.actions = actions;
 		}
 	}
 
 	public static class MetadataSources {
 		private final List<String> annotatedMappingClassNames = new ArrayList<String>();
 		private final List<ConverterDescriptor> converterDescriptors = new ArrayList<ConverterDescriptor>();
 		private final List<NamedInputStream> namedMappingFileInputStreams = new ArrayList<NamedInputStream>();
 		private final List<String> mappingFileResources = new ArrayList<String>();
 		private final List<String> packageNames = new ArrayList<String>();
 
 		public List<String> getAnnotatedMappingClassNames() {
 			return annotatedMappingClassNames;
 		}
 
 		public List<ConverterDescriptor> getConverterDescriptors() {
 			return converterDescriptors;
 		}
 
 		public List<NamedInputStream> getNamedMappingFileInputStreams() {
 			return namedMappingFileInputStreams;
 		}
 
 		public List<String> getPackageNames() {
 			return packageNames;
 		}
 
 		public List<String> collectMappingClassNames() {
 			// todo : the complete answer to this involves looking through the mapping files as well.
 			// 		Really need the metamodel branch code to do that properly
 			return annotatedMappingClassNames;
 		}
 
 		public static class ConverterDescriptor {
 			private final String converterClassName;
 			private final boolean autoApply;
 
 			public ConverterDescriptor(String converterClassName, boolean autoApply) {
 				this.converterClassName = converterClassName;
 				this.autoApply = autoApply;
 			}
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/BinaryArithmeticOperation.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/BinaryArithmeticOperation.java
index 9243ac66ab..f3c45a3299 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/BinaryArithmeticOperation.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/BinaryArithmeticOperation.java
@@ -1,241 +1,238 @@
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
 package org.hibernate.jpa.criteria.expression;
 
 import java.io.Serializable;
 import javax.persistence.criteria.Expression;
 
 import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
 import org.hibernate.jpa.criteria.ParameterRegistry;
 import org.hibernate.jpa.criteria.Renderable;
 import org.hibernate.jpa.criteria.compile.RenderingContext;
 import org.hibernate.jpa.criteria.predicate.ImplicitNumericExpressionTypeDeterminer;
 
 /**
  * Models standard arithmetc operations with two operands.
  *
  * @author Steve Ebersole
  */
 public class BinaryArithmeticOperation<N extends Number>
 		extends ExpressionImpl<N>
 		implements BinaryOperatorExpression<N>, Serializable {
 
 	public static enum Operation {
 		ADD {
+			@Override
 			String apply(String lhs, String rhs) {
 				return applyPrimitive( lhs, '+', rhs );
 			}
 		},
 		SUBTRACT {
+			@Override
 			String apply(String lhs, String rhs) {
 				return applyPrimitive( lhs, '-', rhs );
 			}
 		},
 		MULTIPLY {
+			@Override
 			String apply(String lhs, String rhs) {
 				return applyPrimitive( lhs, '*', rhs );
 			}
 		},
 		DIVIDE {
+			@Override
 			String apply(String lhs, String rhs) {
 				return applyPrimitive( lhs, '/', rhs );
 			}
 		},
 		QUOT {
+			@Override
 			String apply(String lhs, String rhs) {
 				return applyPrimitive( lhs, '/', rhs );
 			}
 		},
 		MOD {
+			@Override
 			String apply(String lhs, String rhs) {
 //				return lhs + " % " + rhs;
 				return "mod(" + lhs + "," + rhs + ")";
 			}
 		};
+
 		abstract String apply(String lhs, String rhs);
 
 		private static final char LEFT_PAREN = '(';
 		private static final char RIGHT_PAREN = ')';
+
 		private static String applyPrimitive(String lhs, char operator, String rhs) {
-			return new StringBuffer( lhs.length() + rhs.length() + 3 )
-					.append( LEFT_PAREN )
-					.append( lhs )
-					.append( operator )
-					.append( rhs )
-					.append( RIGHT_PAREN )
-					.toString();
+			return String.valueOf( LEFT_PAREN ) + lhs + operator + rhs + RIGHT_PAREN;
 		}
 	}
 
 	private final Operation operator;
 	private final Expression<? extends N> rhs;
 	private final Expression<? extends N> lhs;
 
 	public static Class<? extends Number> determineResultType(
 			Class<? extends Number> argument1Type,
 			Class<? extends Number> argument2Type
 	) {
 		return determineResultType( argument1Type, argument2Type, false );
 	}
 
 	public static Class<? extends Number> determineResultType(
 			Class<? extends Number> argument1Type,
 			Class<? extends Number> argument2Type,
-			boolean isQuotientOperation
-	) {
+			boolean isQuotientOperation) {
 		if ( isQuotientOperation ) {
 			return Number.class;
 		}
 		return ImplicitNumericExpressionTypeDeterminer.determineResultType( argument1Type, argument2Type );
 	}
 
 	/**
 	 * Helper for determining the appropriate operation return type based on one of the operands as an expression.
 	 *
 	 * @param defaultType The default return type to use if we cannot determine the java type of 'expression' operand.
 	 * @param expression The operand.
 	 *
 	 * @return The appropriate return type.
 	 */
 	public static Class<? extends Number> determineReturnType(
 			Class<? extends Number> defaultType,
 			Expression<? extends Number> expression) {
 		return expression == null || expression.getJavaType() == null 
 				? defaultType
 				: expression.getJavaType();
 	}
 
 	/**
 	 * Helper for determining the appropriate operation return type based on one of the operands as a literal.
 	 *
 	 * @param defaultType The default return type to use if we cannot determine the java type of 'numberLiteral' operand.
 	 * @param numberLiteral The operand.
 	 *
 	 * @return The appropriate return type.
 	 */
 	public static Class<? extends Number> determineReturnType(
 			Class<? extends Number> defaultType,
 			Number numberLiteral) {
 		return numberLiteral == null ? defaultType : numberLiteral.getClass();
 	}
 
 	/**
 	 * Creates an arithmethic operation based on 2 expressions.
 	 *
 	 * @param criteriaBuilder The builder for query components.
 	 * @param resultType The operation result type
 	 * @param operator The operator (type of operation).
 	 * @param lhs The left-hand operand.
 	 * @param rhs The right-hand operand
 	 */
 	public BinaryArithmeticOperation(
 			CriteriaBuilderImpl criteriaBuilder,
 			Class<N> resultType,
 			Operation operator,
 			Expression<? extends N> lhs,
 			Expression<? extends N> rhs) {
 		super( criteriaBuilder, resultType );
 		this.operator = operator;
 		this.lhs = lhs;
 		this.rhs = rhs;
 	}
 
 	/**
 	 * Creates an arithmethic operation based on an expression and a literal.
 	 *
 	 * @param criteriaBuilder The builder for query components.
 	 * @param javaType The operation result type
 	 * @param operator The operator (type of operation).
 	 * @param lhs The left-hand operand
 	 * @param rhs The right-hand operand (the literal)
 	 */
 	public BinaryArithmeticOperation(
 			CriteriaBuilderImpl criteriaBuilder,
 			Class<N> javaType,
 			Operation operator,
 			Expression<? extends N> lhs,
 			N rhs) {
 		super( criteriaBuilder, javaType );
 		this.operator = operator;
 		this.lhs = lhs;
 		this.rhs = new LiteralExpression<N>( criteriaBuilder, rhs );
 	}
 
 	/**
 	 * Creates an arithmetic operation based on an expression and a literal.
 	 *
 	 * @param criteriaBuilder The builder for query components.
 	 * @param javaType The operation result type
 	 * @param operator The operator (type of operation).
 	 * @param lhs The left-hand operand (the literal)
 	 * @param rhs The right-hand operand
 	 */
 	public BinaryArithmeticOperation(
 			CriteriaBuilderImpl criteriaBuilder,
 			Class<N> javaType,
 			Operation operator,
 			N lhs,
 			Expression<? extends N> rhs) {
 		super( criteriaBuilder, javaType );
 		this.operator = operator;
 		this.lhs = new LiteralExpression<N>( criteriaBuilder, lhs );
 		this.rhs = rhs;
 	}
 	public Operation getOperator() {
 		return operator;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Expression<? extends N> getRightHandOperand() {
 		return rhs;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Expression<? extends N> getLeftHandOperand() {
 		return lhs;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void registerParameters(ParameterRegistry registry) {
 		Helper.possibleParameter( getRightHandOperand(), registry );
 		Helper.possibleParameter( getLeftHandOperand(), registry );
 	}
 
+	@Override
 	public String render(RenderingContext renderingContext) {
 		return getOperator().apply(
 				( (Renderable) getLeftHandOperand() ).render( renderingContext ),
 				( (Renderable) getRightHandOperand() ).render( renderingContext )
 		);
 	}
 
+	@Override
 	public String renderProjection(RenderingContext renderingContext) {
 		return render( renderingContext );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/UnaryArithmeticOperation.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/UnaryArithmeticOperation.java
index 9ad9ecde78..cdf94052a4 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/UnaryArithmeticOperation.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/criteria/expression/UnaryArithmeticOperation.java
@@ -1,86 +1,84 @@
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
 package org.hibernate.jpa.criteria.expression;
 
 import java.io.Serializable;
 import javax.persistence.criteria.Expression;
 
 import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
 import org.hibernate.jpa.criteria.ParameterRegistry;
 import org.hibernate.jpa.criteria.Renderable;
 import org.hibernate.jpa.criteria.compile.RenderingContext;
 
 /**
  * Models unary arithmetic operation (unary plus and unary minus).
  *
  * @author Steve Ebersole
  */
 public class UnaryArithmeticOperation<T> 
 		extends ExpressionImpl<T>
 		implements UnaryOperatorExpression<T>, Serializable {
 
 	public static enum Operation {
 		UNARY_PLUS, UNARY_MINUS
 	}
 
 	private final Operation operation;
 	private final Expression<T> operand;
 
 	@SuppressWarnings({ "unchecked" })
 	public UnaryArithmeticOperation(
 			CriteriaBuilderImpl criteriaBuilder,
 			Operation operation,
 			Expression<T> operand) {
 		super( criteriaBuilder, (Class)operand.getJavaType() );
 		this.operation = operation;
 		this.operand = operand;
 	}
 
 	public Operation getOperation() {
 		return operation;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Expression<T> getOperand() {
 		return operand;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void registerParameters(ParameterRegistry registry) {
 		Helper.possibleParameter( getOperand(), registry );
 	}
 
+	@Override
 	public String render(RenderingContext renderingContext) {
 		return ( getOperation() == Operation.UNARY_MINUS ? '-' : '+' )
 				+ ( (Renderable) getOperand() ).render( renderingContext );
 	}
 
+	@Override
 	public String renderProjection(RenderingContext renderingContext) {
 		return render( renderingContext );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractAttribute.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractAttribute.java
index bde7596a13..726f410977 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractAttribute.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/AbstractAttribute.java
@@ -1,142 +1,132 @@
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
 package org.hibernate.jpa.internal.metamodel;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.ManagedType;
 
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * Models the commonality of the JPA {@link Attribute} hierarchy.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttribute<X, Y>
 		implements Attribute<X, Y>, AttributeImplementor<X,Y>, Serializable {
 	private final String name;
 	private final Class<Y> javaType;
 	private final AbstractManagedType<X> declaringType;
 	private transient Member member;
 	private final PersistentAttributeType persistentAttributeType;
 
 	public AbstractAttribute(
 			String name,
 			Class<Y> javaType,
 			AbstractManagedType<X> declaringType,
 			Member member,
 			PersistentAttributeType persistentAttributeType) {
 		this.name = name;
 		this.javaType = javaType;
 		this.declaringType = declaringType;
 		this.member = member;
 		this.persistentAttributeType = persistentAttributeType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getName() {
 		return name;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public ManagedType<X> getDeclaringType() {
 		return declaringType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class<Y> getJavaType() {
 		return javaType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Member getJavaMember() {
 		return member;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public PersistentAttributeType getPersistentAttributeType() {
 		return persistentAttributeType;
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param ois The input stream from which we are being read...
 	 * @throws java.io.IOException Indicates a general IO stream exception
 	 * @throws ClassNotFoundException Indicates a class resolution issue
 	 */
 	protected void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		final String memberDeclaringClassName = ( String ) ois.readObject();
 		final String memberName = ( String ) ois.readObject();
 		final String memberType = ( String ) ois.readObject();
 
 		final Class memberDeclaringClass = Class.forName(
 				memberDeclaringClassName,
 				false,
 				declaringType.getJavaType().getClassLoader()
 		);
 		try {
 			this.member = "method".equals( memberType )
 					? memberDeclaringClass.getMethod( memberName, ReflectHelper.NO_PARAM_SIGNATURE )
 					: memberDeclaringClass.getField( memberName );
 		}
 		catch ( Exception e ) {
 			throw new IllegalStateException(
 					"Unable to locate member [" + memberDeclaringClassName + "#"
 							+ memberName + "]"
 			);
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param oos The output stream to which we are being written...
 	 * @throws IOException Indicates a general IO stream exception
 	 */
 	protected void writeObject(ObjectOutputStream oos) throws IOException {
 		oos.defaultWriteObject();
 		oos.writeObject( getJavaMember().getDeclaringClass().getName() );
 		oos.writeObject( getJavaMember().getName() );
 		// should only ever be a field or the getter-method...
 		oos.writeObject( Method.class.isInstance( getJavaMember() ) ? "method" : "field" );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/PluralAttributeImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/PluralAttributeImpl.java
index 63b4a27b6b..7c9fb05f8f 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/PluralAttributeImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/PluralAttributeImpl.java
@@ -1,260 +1,238 @@
 /*
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
  */
 package org.hibernate.jpa.internal.metamodel;
 
 import java.io.Serializable;
 import java.lang.reflect.Member;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.metamodel.CollectionAttribute;
 import javax.persistence.metamodel.ListAttribute;
 import javax.persistence.metamodel.MapAttribute;
 import javax.persistence.metamodel.PluralAttribute;
 import javax.persistence.metamodel.SetAttribute;
 import javax.persistence.metamodel.Type;
 
 import org.hibernate.mapping.Property;
 
 /**
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public abstract class PluralAttributeImpl<X, C, E>
 		extends AbstractAttribute<X,C>
 		implements PluralAttribute<X, C, E>, Serializable {
 
 	private final Type<E> elementType;
 
 	private PluralAttributeImpl(Builder<X,C,E,?> builder) {
 		super(
 				builder.property.getName(),
 				builder.collectionClass,
 				builder.type,
 				builder.member,
 				builder.persistentAttributeType
 		);
 		this.elementType = builder.attributeType;
 	}
 
 	public static class Builder<X, C, E, K> {
 		private final Type<E> attributeType;
 		private final AbstractManagedType<X> type;
 		private Member member;
 		private PersistentAttributeType persistentAttributeType;
 		private Property property;
 		private Class<C> collectionClass;
 		private Type<K> keyType;
 
 
 		private Builder(AbstractManagedType<X> ownerType, Type<E> attrType, Class<C> collectionClass, Type<K> keyType) {
 			this.type = ownerType;
 			this.attributeType = attrType;
 			this.collectionClass = collectionClass;
 			this.keyType = keyType;
 		}
 
 		public Builder<X,C,E,K> member(Member member) {
 			this.member = member;
 			return this;
 		}
 
 		public Builder<X,C,E,K> property(Property property) {
 			this.property = property;
 			return this;
 		}
 
 		public Builder<X,C,E,K> persistentAttributeType(PersistentAttributeType attrType) {
 			this.persistentAttributeType = attrType;
 			return this;
 		}
 
 		@SuppressWarnings( "unchecked" )
 		public <K> PluralAttributeImpl<X,C,E> build() {
 			//apply strict spec rules first
 			if ( Map.class.equals( collectionClass ) ) {
 				final Builder<X,Map<K,E>,E,K> builder = (Builder<X,Map<K,E>,E,K>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new MapAttributeImpl<X,K,E>(
 						builder
 				);
 			}
 			else if ( Set.class.equals( collectionClass ) ) {
 				final Builder<X,Set<E>, E,?> builder = (Builder<X, Set<E>, E,?>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new SetAttributeImpl<X,E>(
 						builder
 				);
 			}
 			else if ( List.class.equals( collectionClass ) ) {
 				final Builder<X, List<E>, E,?> builder = (Builder<X, List<E>, E,?>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new ListAttributeImpl<X,E>(
 						builder
 				);
 			}
 			else if ( Collection.class.equals( collectionClass ) ) {
 				final Builder<X, Collection<E>,E,?> builder = (Builder<X, Collection<E>, E,?>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new CollectionAttributeImpl<X, E>(
 						builder
 				);
 			}
 
 			//apply loose rules
 			if ( Map.class.isAssignableFrom( collectionClass ) ) {
 				final Builder<X,Map<K,E>,E,K> builder = (Builder<X,Map<K,E>,E,K>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new MapAttributeImpl<X,K,E>(
 						builder
 				);
 			}
 			else if ( Set.class.isAssignableFrom( collectionClass ) ) {
 				final Builder<X,Set<E>, E,?> builder = (Builder<X, Set<E>, E,?>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new SetAttributeImpl<X,E>(
 						builder
 				);
 			}
 			else if ( List.class.isAssignableFrom( collectionClass ) ) {
 				final Builder<X, List<E>, E,?> builder = (Builder<X, List<E>, E,?>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new ListAttributeImpl<X,E>(
 						builder
 				);
 			}
 			else if ( Collection.class.isAssignableFrom( collectionClass ) ) {
 				final Builder<X, Collection<E>,E,?> builder = (Builder<X, Collection<E>, E,?>) this;
 				return ( PluralAttributeImpl<X, C, E> ) new CollectionAttributeImpl<X, E>(
 						builder
 				);
 			}
 			throw new UnsupportedOperationException( "Unkown collection: " + collectionClass );
 		}
 	}
 
 	public static <X,C,E,K> Builder<X,C,E,K> create(
 			AbstractManagedType<X> ownerType,
 			Type<E> attrType,
 			Class<C> collectionClass,
 			Type<K> keyType) {
 		return new Builder<X,C,E,K>(ownerType, attrType, collectionClass, keyType);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Type<E> getElementType() {
 		return elementType;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isAssociation() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isCollection() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public BindableType getBindableType() {
 		return BindableType.PLURAL_ATTRIBUTE;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class<E> getBindableJavaType() {
 		return elementType.getJavaType();
 	}
 
 	static class SetAttributeImpl<X,E> extends PluralAttributeImpl<X,Set<E>,E> implements SetAttribute<X,E> {
 		SetAttributeImpl(Builder<X,Set<E>,E,?> xceBuilder) {
 			super( xceBuilder );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public CollectionType getCollectionType() {
 			return CollectionType.SET;
 		}
 	}
 
 	static class CollectionAttributeImpl<X,E> extends PluralAttributeImpl<X,Collection<E>,E> implements CollectionAttribute<X,E> {
 		CollectionAttributeImpl(Builder<X, Collection<E>,E,?> xceBuilder) {
 			super( xceBuilder );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public CollectionType getCollectionType() {
 			return CollectionType.COLLECTION;
 		}
 	}
 
 	static class ListAttributeImpl<X,E> extends PluralAttributeImpl<X,List<E>,E> implements ListAttribute<X,E> {
 		ListAttributeImpl(Builder<X,List<E>,E,?> xceBuilder) {
 			super( xceBuilder );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public CollectionType getCollectionType() {
 			return CollectionType.LIST;
 		}
 	}
 
 	static class MapAttributeImpl<X,K,V> extends PluralAttributeImpl<X,Map<K,V>,V> implements MapAttribute<X,K,V> {
 		private final Type<K> keyType;
 
 		MapAttributeImpl(Builder<X,Map<K,V>,V,K> xceBuilder) {
 			super( xceBuilder );
 			this.keyType = xceBuilder.keyType;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public CollectionType getCollectionType() {
 			return CollectionType.MAP;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Class<K> getKeyJavaType() {
 			return keyType.getJavaType();
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Type<K> getKeyType() {
 			return keyType;
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
index b9da64968b..91ec23e255 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/spi/AbstractEntityManagerImpl.java
@@ -1,1896 +1,1890 @@
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
 package org.hibernate.jpa.spi;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.EntityExistsException;
 import javax.persistence.EntityGraph;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.EntityTransaction;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.LockTimeoutException;
 import javax.persistence.NoResultException;
 import javax.persistence.NonUniqueResultException;
 import javax.persistence.OptimisticLockException;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PessimisticLockException;
 import javax.persistence.PessimisticLockScope;
 import javax.persistence.Query;
 import javax.persistence.QueryTimeoutException;
 import javax.persistence.StoredProcedureQuery;
 import javax.persistence.SynchronizationType;
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.Tuple;
 import javax.persistence.TupleElement;
 import javax.persistence.TypedQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaDelete;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.CriteriaUpdate;
 import javax.persistence.criteria.Selection;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.transaction.Status;
 import javax.transaction.SystemException;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.SQLQuery;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.lock.LockingStrategyException;
 import org.hibernate.dialect.lock.OptimisticEntityLockException;
 import org.hibernate.dialect.lock.PessimisticEntityLockException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.JoinStatus;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.synchronization.spi.AfterCompletionAction;
 import org.hibernate.engine.transaction.synchronization.spi.ExceptionMapper;
 import org.hibernate.engine.transaction.synchronization.spi.ManagedFlushChecker;
 import org.hibernate.engine.transaction.synchronization.spi.SynchronizationCallbackCoordinator;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.HibernateEntityManagerFactory;
 import org.hibernate.jpa.QueryHints;
 import org.hibernate.jpa.criteria.ValueHandlerFactory;
 import org.hibernate.jpa.criteria.compile.CompilableCriteria;
 import org.hibernate.jpa.criteria.compile.CriteriaCompiler;
 import org.hibernate.jpa.criteria.expression.CompoundSelectionImpl;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.HEMLogging;
 import org.hibernate.jpa.internal.QueryImpl;
 import org.hibernate.jpa.internal.StoredProcedureQueryImpl;
 import org.hibernate.jpa.internal.TransactionImpl;
 import org.hibernate.jpa.internal.util.CacheModeHelper;
 import org.hibernate.jpa.internal.util.ConfigurationHelper;
 import org.hibernate.jpa.internal.util.LockModeTypeHelper;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.UnknownSqlResultSetMappingException;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.BasicTransformerAdapter;
 import org.hibernate.type.Type;
 
 /**
  * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public abstract class AbstractEntityManagerImpl implements HibernateEntityManagerImplementor, Serializable {
 	private static final long serialVersionUID = 78818181L;
 
     private static final EntityManagerMessageLogger LOG = HEMLogging.messageLogger( AbstractEntityManagerImpl.class );
 
 	private static final List<String> ENTITY_MANAGER_SPECIFIC_PROPERTIES = new ArrayList<String>();
 
 	static {
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( AvailableSettings.LOCK_SCOPE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( AvailableSettings.LOCK_TIMEOUT );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( AvailableSettings.FLUSH_MODE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( AvailableSettings.SHARED_CACHE_STORE_MODE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( QueryHints.SPEC_HINT_TIMEOUT );
 	}
 
 	private EntityManagerFactoryImpl entityManagerFactory;
 	protected transient TransactionImpl tx = new TransactionImpl( this );
 	private SynchronizationType synchronizationType;
 	private PersistenceUnitTransactionType transactionType;
 	private Map<String, Object> properties;
 	private LockOptions lockOptions;
 
 	protected AbstractEntityManagerImpl(
 			EntityManagerFactoryImpl entityManagerFactory,
 			PersistenceContextType type,  // TODO:  remove as no longer used
 			SynchronizationType synchronizationType,
 			PersistenceUnitTransactionType transactionType,
 			Map properties) {
 		this.entityManagerFactory = entityManagerFactory;
 		this.synchronizationType = synchronizationType;
 		this.transactionType = transactionType;
 
 		this.lockOptions = new LockOptions();
 		this.properties = new HashMap<String, Object>();
 		for ( String key : ENTITY_MANAGER_SPECIFIC_PROPERTIES ) {
 			if ( entityManagerFactory.getProperties().containsKey( key ) ) {
 				this.properties.put( key, entityManagerFactory.getProperties().get( key ) );
 			}
 			if ( properties != null && properties.containsKey( key ) ) {
 				this.properties.put( key, properties.get( key ) );
 			}
 		}
 	}
 
 //	protected PersistenceUnitTransactionType transactionType() {
 //		return transactionType;
 //	}
 //
 //	protected SynchronizationType synchronizationType() {
 //		return synchronizationType;
 //	}
 //
 //	public boolean shouldAutoJoinTransactions() {
 //		// the Session should auto join only if using non-JTA transactions or if the synchronization type
 //		// was specified as SYNCHRONIZED
 //		return transactionType != PersistenceUnitTransactionType.JTA
 //				|| synchronizationType == SynchronizationType.SYNCHRONIZED;
 //	}
 
 	public PersistenceUnitTransactionType getTransactionType() {
 		return transactionType;
 	}
 
 	protected void postInit() {
 		//register in Sync if needed
 		if ( transactionType == PersistenceUnitTransactionType.JTA
 				&& synchronizationType == SynchronizationType.SYNCHRONIZED ) {
 			joinTransaction( false );
 		}
 
 		setDefaultProperties();
 		applyProperties();
 	}
 
 	private void applyProperties() {
 		getSession().setFlushMode( ConfigurationHelper.getFlushMode( properties.get( AvailableSettings.FLUSH_MODE ) ) );
 		setLockOptions( this.properties, this.lockOptions );
 		getSession().setCacheMode(
 				CacheModeHelper.interpretCacheMode(
 						currentCacheStoreMode(),
 						currentCacheRetrieveMode()
 				)
 		);
 	}
 
 	private Query applyProperties(Query query) {
 		if ( lockOptions.getLockMode() != LockMode.NONE ) {
 			query.setLockMode( getLockMode(lockOptions.getLockMode()));
 		}
 		Object queryTimeout;
 		if ( (queryTimeout = getProperties().get(QueryHints.SPEC_HINT_TIMEOUT)) != null ) {
 			query.setHint( QueryHints.SPEC_HINT_TIMEOUT, queryTimeout );
 		}
 		Object lockTimeout;
 		if( (lockTimeout = getProperties().get( AvailableSettings.LOCK_TIMEOUT ))!=null){
 			query.setHint( AvailableSettings.LOCK_TIMEOUT, lockTimeout );
 		}
 		return query;
 	}
 
 	private CacheRetrieveMode currentCacheRetrieveMode() {
 		return determineCacheRetrieveMode( properties );
 	}
 
 	private CacheRetrieveMode determineCacheRetrieveMode(Map<String, Object> settings) {
 		return ( CacheRetrieveMode ) settings.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 	}
 
 	private CacheStoreMode currentCacheStoreMode() {
 		return determineCacheStoreMode( properties );
 	}
 
 	private CacheStoreMode determineCacheStoreMode(Map<String, Object> settings) {
 		return ( CacheStoreMode ) settings.get( AvailableSettings.SHARED_CACHE_STORE_MODE );
 	}
 
 	private void setLockOptions(Map<String, Object> props, LockOptions options) {
 		Object lockScope = props.get( AvailableSettings.LOCK_SCOPE );
 		if ( lockScope instanceof String && PessimisticLockScope.valueOf( ( String ) lockScope ) == PessimisticLockScope.EXTENDED ) {
 			options.setScope( true );
 		}
 		else if ( lockScope instanceof PessimisticLockScope ) {
 			boolean extended = PessimisticLockScope.EXTENDED.equals( lockScope );
 			options.setScope( extended );
 		}
 		else if ( lockScope != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_SCOPE + ": " + lockScope );
 		}
 
 		Object lockTimeout = props.get( AvailableSettings.LOCK_TIMEOUT );
 		int timeout = 0;
 		boolean timeoutSet = false;
 		if ( lockTimeout instanceof String ) {
 			timeout = Integer.parseInt( ( String ) lockTimeout );
 			timeoutSet = true;
 		}
 		else if ( lockTimeout instanceof Number ) {
 			timeout = ( (Number) lockTimeout ).intValue();
 			timeoutSet = true;
 		}
 		else if ( lockTimeout != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_TIMEOUT + ": " + lockTimeout );
 		}
 		if ( timeoutSet ) {
             if ( timeout == LockOptions.SKIP_LOCKED ) {
                 options.setTimeOut( LockOptions.SKIP_LOCKED );
             }
 			else if ( timeout < 0 ) {
 				options.setTimeOut( LockOptions.WAIT_FOREVER );
 			}
 			else if ( timeout == 0 ) {
 				options.setTimeOut( LockOptions.NO_WAIT );
 			}
 			else {
 				options.setTimeOut( timeout );
 			}
 		}
 	}
 
 	/**
 	 * Sets the default property values for the properties the entity manager supports and which are not already explicitly
 	 * set.
 	 */
 	private void setDefaultProperties() {
 		if ( properties.get( AvailableSettings.FLUSH_MODE ) == null ) {
 			properties.put( AvailableSettings.FLUSH_MODE, getSession().getFlushMode().toString() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_SCOPE ) == null ) {
 			this.properties.put( AvailableSettings.LOCK_SCOPE, PessimisticLockScope.EXTENDED.name() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_TIMEOUT ) == null ) {
 			properties.put( AvailableSettings.LOCK_TIMEOUT, LockOptions.WAIT_FOREVER );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE, CacheModeHelper.DEFAULT_RETRIEVE_MODE );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheModeHelper.DEFAULT_STORE_MODE );
 		}
 	}
 
 	@Override
 	public Query createQuery(String jpaqlString) {
 		checkOpen();
 		try {
 			return applyProperties( new QueryImpl<Object>( internalGetSession().createQuery( jpaqlString ), this ) );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	protected abstract void checkOpen();
 
 	@Override
 	public <T> TypedQuery<T> createQuery(String jpaqlString, Class<T> resultClass) {
 		checkOpen();
 		try {
 			// do the translation
 			org.hibernate.Query hqlQuery = internalGetSession().createQuery( jpaqlString );
 
 			resultClassChecking( resultClass, hqlQuery );
 
 			// finally, build/return the query instance
 			return new QueryImpl<T>( hqlQuery, this );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	protected void resultClassChecking(Class resultClass, org.hibernate.Query hqlQuery) {
 		// make sure the query is a select -> HHH-7192
 		final SessionImplementor session = unwrap( SessionImplementor.class );
 		final HQLQueryPlan queryPlan = session.getFactory().getQueryPlanCache().getHQLQueryPlan(
 				hqlQuery.getQueryString(),
 				false,
 				session.getLoadQueryInfluencers().getEnabledFilters()
 		);
 		if ( queryPlan.getTranslators()[0].isManipulationStatement() ) {
 			throw new IllegalArgumentException( "Update/delete queries cannot be typed" );
 		}
 
 		// do some return type validation checking
 		if ( Object[].class.equals( resultClass ) ) {
 			// no validation needed
 		}
 		else if ( Tuple.class.equals( resultClass ) ) {
 			TupleBuilderTransformer tupleTransformer = new TupleBuilderTransformer( hqlQuery );
 			hqlQuery.setResultTransformer( tupleTransformer  );
 		}
 		else {
 			final Class dynamicInstantiationClass = queryPlan.getDynamicInstantiationResultType();
 			if ( dynamicInstantiationClass != null ) {
 				if ( ! resultClass.isAssignableFrom( dynamicInstantiationClass ) ) {
 					throw new IllegalArgumentException(
 							"Mismatch in requested result type [" + resultClass.getName() +
 									"] and actual result type [" + dynamicInstantiationClass.getName() + "]"
 					);
 				}
 			}
 			else if ( hqlQuery.getReturnTypes().length == 1 ) {
 				// if we have only a single return expression, its java type should match with the requested type
 				if ( !resultClass.isAssignableFrom( hqlQuery.getReturnTypes()[0].getReturnedClass() ) ) {
 					throw new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									hqlQuery.getReturnTypes()[0].getReturnedClass() + "]"
 					);
 				}
 			}
 			else {
 				throw new IllegalArgumentException(
 						"Cannot create TypedQuery for query with more than one return using requested result type [" +
 								resultClass.getName() + "]"
 				);
 			}
 		}
 	}
 
 	public static class TupleBuilderTransformer extends BasicTransformerAdapter {
 		private List<TupleElement<?>> tupleElements;
 		private Map<String,HqlTupleElementImpl> tupleElementsByAlias;
 
 		public TupleBuilderTransformer(org.hibernate.Query hqlQuery) {
 			final Type[] resultTypes = hqlQuery.getReturnTypes();
 			final int tupleSize = resultTypes.length;
 
 			this.tupleElements = CollectionHelper.arrayList( tupleSize );
 
 			final String[] aliases = hqlQuery.getReturnAliases();
 			final boolean hasAliases = aliases != null && aliases.length > 0;
 			this.tupleElementsByAlias = hasAliases
 					? CollectionHelper.<String, HqlTupleElementImpl>mapOfSize( tupleSize )
 					: Collections.<String, HqlTupleElementImpl>emptyMap();
 
 			for ( int i = 0; i < tupleSize; i++ ) {
 				final HqlTupleElementImpl tupleElement = new HqlTupleElementImpl(
 						i,
 						aliases == null ? null : aliases[i],
 						resultTypes[i]
 				);
 				tupleElements.add( tupleElement );
 				if ( hasAliases ) {
 					final String alias = aliases[i];
 					if ( alias != null ) {
 						tupleElementsByAlias.put( alias, tupleElement );
 					}
 				}
 			}
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			if ( tuple.length != tupleElements.size() ) {
 				throw new IllegalArgumentException(
 						"Size mismatch between tuple result [" + tuple.length + "] and expected tuple elements [" +
 								tupleElements.size() + "]"
 				);
 			}
 			return new HqlTupleImpl( tuple );
 		}
 
 		public static class HqlTupleElementImpl<X> implements TupleElement<X> {
 			private final int position;
 			private final String alias;
 			private final Type hibernateType;
 
 			public HqlTupleElementImpl(int position, String alias, Type hibernateType) {
 				this.position = position;
 				this.alias = alias;
 				this.hibernateType = hibernateType;
 			}
 
 			@Override
 			public Class getJavaType() {
 				return hibernateType.getReturnedClass();
 			}
 
 			@Override
 			public String getAlias() {
 				return alias;
 			}
 
 			public int getPosition() {
 				return position;
 			}
 
 			public Type getHibernateType() {
 				return hibernateType;
 			}
 		}
 
 		public class HqlTupleImpl implements Tuple {
 			private Object[] tuple;
 
 			public HqlTupleImpl(Object[] tuple) {
 				this.tuple = tuple;
 			}
 
 			@Override
 			public <X> X get(String alias, Class<X> type) {
 				final Object untyped = get( alias );
 				if ( untyped != null ) {
 					if ( ! type.isInstance( untyped ) ) {
 						throw new IllegalArgumentException(
 								String.format(
 										"Requested tuple value [alias=%s, value=%s] cannot be assigned to requested type [%s]",
 										alias,
 										untyped,
 										type.getName()
 								)
 						);
 					}
 				}
 				return (X) untyped;
 			}
 
 			@Override
 			public Object get(String alias) {
 				HqlTupleElementImpl tupleElement = tupleElementsByAlias.get( alias );
 				if ( tupleElement == null ) {
 					throw new IllegalArgumentException( "Unknown alias [" + alias + "]" );
 				}
 				return tuple[ tupleElement.getPosition() ];
 			}
 
 			@Override
 			public <X> X get(int i, Class<X> type) {
 				final Object result = get( i );
 				if ( result != null && ! type.isInstance( result ) ) {
 					throw new IllegalArgumentException(
 							String.format(
 									"Requested tuple value [index=%s, realType=%s] cannot be assigned to requested type [%s]",
 									i,
 									result.getClass().getName(),
 									type.getName()
 							)
 					);
 				}
 				return ( X ) result;
 			}
 
 			@Override
 			public Object get(int i) {
 				if ( i < 0 ) {
 					throw new IllegalArgumentException( "requested tuple index must be greater than zero" );
 				}
 				if ( i > tuple.length ) {
 					throw new IllegalArgumentException( "requested tuple index exceeds actual tuple size" );
 				}
 				return tuple[i];
 			}
 
 			@Override
 			public Object[] toArray() {
 				// todo : make a copy?
 				return tuple;
 			}
 
 			@Override
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 
 			@Override
 			public <X> X get(TupleElement<X> tupleElement) {
 				if ( HqlTupleElementImpl.class.isInstance( tupleElement ) ) {
 					return get( ( (HqlTupleElementImpl) tupleElement ).getPosition(), tupleElement.getJavaType() );
 				}
 				else {
 					return get( tupleElement.getAlias(), tupleElement.getJavaType() );
 				}
 			}
 		}
 	}
 
 	@Override
 	public <T> QueryImpl<T> createQuery(
 			String jpaqlString,
 			Class<T> resultClass,
 			Selection selection,
 			QueryOptions queryOptions) {
 		try {
 			org.hibernate.Query hqlQuery = internalGetSession().createQuery( jpaqlString );
 
 			if ( queryOptions.getValueHandlers() == null ) {
 				if ( queryOptions.getResultMetadataValidator() != null ) {
 					queryOptions.getResultMetadataValidator().validate( hqlQuery.getReturnTypes() );
 				}
 			}
 
 			// determine if we need a result transformer
 			List tupleElements = Tuple.class.equals( resultClass )
 					? ( ( CompoundSelectionImpl<Tuple> ) selection ).getCompoundSelectionItems()
 					: null;
 			if ( queryOptions.getValueHandlers() != null || tupleElements != null ) {
 				hqlQuery.setResultTransformer(
 						new CriteriaQueryTransformer( queryOptions.getValueHandlers(), tupleElements )
 				);
 			}
 			return new QueryImpl<T>( hqlQuery, this, queryOptions.getNamedParameterExplicitTypes() );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	private static class CriteriaQueryTransformer extends BasicTransformerAdapter {
 		private final List<ValueHandlerFactory.ValueHandler> valueHandlers;
 		private final List tupleElements;
 
 		private CriteriaQueryTransformer(List<ValueHandlerFactory.ValueHandler> valueHandlers, List tupleElements) {
 			// todo : should these 2 sizes match *always*?
 			this.valueHandlers = valueHandlers;
 			this.tupleElements = tupleElements;
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			final Object[] valueHandlerResult;
 			if ( valueHandlers == null ) {
 				valueHandlerResult = tuple;
 			}
 			else {
 				valueHandlerResult = new Object[tuple.length];
 				for ( int i = 0; i < tuple.length; i++ ) {
 					ValueHandlerFactory.ValueHandler valueHandler = valueHandlers.get( i );
 					valueHandlerResult[i] = valueHandler == null
 							? tuple[i]
 							: valueHandler.convert( tuple[i] );
 				}
 			}
 
 			return tupleElements == null
 					? valueHandlerResult.length == 1 ? valueHandlerResult[0] : valueHandlerResult
 					: new TupleImpl( tuple );
 
 		}
 
 		private class TupleImpl implements Tuple {
 			private final Object[] tuples;
 
 			private TupleImpl(Object[] tuples) {
 				if ( tuples.length != tupleElements.size() ) {
 					throw new IllegalArgumentException(
 							"Size mismatch between tuple result [" + tuples.length
 									+ "] and expected tuple elements [" + tupleElements.size() + "]"
 					);
 				}
 				this.tuples = tuples;
 			}
 
 			public <X> X get(TupleElement<X> tupleElement) {
 				int index = tupleElements.indexOf( tupleElement );
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Requested tuple element did not correspond to element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return ( X ) tuples[index];
 			}
 
 			public Object get(String alias) {
 				int index = -1;
 				if ( alias != null ) {
 					alias = alias.trim();
 					if ( alias.length() > 0 ) {
 						int i = 0;
 						for ( TupleElement selection : ( List<TupleElement> ) tupleElements ) {
 							if ( alias.equals( selection.getAlias() ) ) {
 								index = i;
 								break;
 							}
 							i++;
 						}
 					}
 				}
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Given alias [" + alias + "] did not correspond to an element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return tuples[index];
 			}
 
 			public <X> X get(String alias, Class<X> type) {
 				final Object untyped = get( alias );
 				if ( untyped != null ) {
 					if ( ! type.isInstance( untyped ) ) {
 						throw new IllegalArgumentException(
 								String.format(
 										"Requested tuple value [alias=%s, value=%s] cannot be assigned to requested type [%s]",
 										alias,
 										untyped,
 										type.getName()
 								)
 						);
 					}
 				}
 				return (X) untyped;
 			}
 
 			public Object get(int i) {
 				if ( i >= tuples.length ) {
 					throw new IllegalArgumentException(
 							"Given index [" + i + "] was outside the range of result tuple size [" + tuples.length + "] "
 					);
 				}
 				return tuples[i];
 			}
 
 			public <X> X get(int i, Class<X> type) {
 				final Object result = get( i );
 				if ( result != null && ! type.isInstance( result ) ) {
 					throw new IllegalArgumentException(
 							String.format(
 									"Requested tuple value [index=%s, realType=%s] cannot be assigned to requested type [%s]",
 									i,
 									result.getClass().getName(),
 									type.getName()
 							)
 					);
 				}
 				return ( X ) result;
 			}
 
 			public Object[] toArray() {
 				return tuples;
 			}
 
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 		}
 	}
 
 	private CriteriaCompiler criteriaCompiler;
 
 	protected CriteriaCompiler criteriaCompiler() {
 		if ( criteriaCompiler == null ) {
 			criteriaCompiler = new CriteriaCompiler( this );
 		}
 		return criteriaCompiler;
 	}
 
 	@Override
 	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
 		checkOpen();
 		try {
 			return (TypedQuery<T>) criteriaCompiler().compile( (CompilableCriteria) criteriaQuery );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public Query createQuery(CriteriaUpdate criteriaUpdate) {
 		checkOpen();
 		try {
 			return criteriaCompiler().compile( (CompilableCriteria) criteriaUpdate );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public Query createQuery(CriteriaDelete criteriaDelete) {
 		checkOpen();
 		try {
 			return criteriaCompiler().compile( (CompilableCriteria) criteriaDelete );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public Query createNamedQuery(String name) {
 		return buildQueryFromName( name, null );
 	}
 
 	private QueryImpl buildQueryFromName(String name, Class resultType) {
 		checkOpen();
 
 		// we can't just call Session#getNamedQuery because we need to apply stored setting at the JPA Query
 		// level too
 
 		final SessionFactoryImplementor sfi = entityManagerFactory.getSessionFactory();
 
-		// first try as hql/jpql query
-		{
-			final NamedQueryDefinition namedQueryDefinition = sfi.getNamedQueryRepository().getNamedQueryDefinition( name );
-			if ( namedQueryDefinition != null ) {
-				return createNamedJpqlQuery( namedQueryDefinition, resultType );
-			}
+		final NamedQueryDefinition jpqlDefinition = sfi.getNamedQueryRepository().getNamedQueryDefinition( name );
+		if ( jpqlDefinition != null ) {
+			return createNamedJpqlQuery( jpqlDefinition, resultType );
 		}
 
-		// then as a native (SQL) query
-		{
-			final NamedSQLQueryDefinition namedQueryDefinition = sfi.getNamedQueryRepository().getNamedSQLQueryDefinition( name );
-			if ( namedQueryDefinition != null ) {
-				return createNamedSqlQuery( namedQueryDefinition, resultType );
-			}
+		final NamedSQLQueryDefinition nativeQueryDefinition = sfi.getNamedQueryRepository().getNamedSQLQueryDefinition( name );
+		if ( nativeQueryDefinition != null ) {
+			return createNamedSqlQuery( nativeQueryDefinition, resultType );
 		}
 
 		throw convert( new IllegalArgumentException( "No query defined for that name [" + name + "]" ) );
 	}
 
 	protected QueryImpl createNamedJpqlQuery(NamedQueryDefinition namedQueryDefinition, Class resultType) {
 		final org.hibernate.Query hibQuery = ( (SessionImplementor) internalGetSession() ).createQuery( namedQueryDefinition );
 		if ( resultType != null ) {
 			resultClassChecking( resultType, hibQuery );
 		}
 
 		return wrapAsJpaQuery( namedQueryDefinition, hibQuery );
 	}
 
 	protected QueryImpl wrapAsJpaQuery(NamedQueryDefinition namedQueryDefinition, org.hibernate.Query hibQuery) {
 		try {
 			final QueryImpl jpaQuery = new QueryImpl( hibQuery, this );
 			applySavedSettings( namedQueryDefinition, jpaQuery );
 			return jpaQuery;
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	protected void applySavedSettings(NamedQueryDefinition namedQueryDefinition, QueryImpl jpaQuery) {
 		if ( namedQueryDefinition.isCacheable() ) {
 			jpaQuery.setHint( QueryHints.HINT_CACHEABLE, true );
 			if ( namedQueryDefinition.getCacheRegion() != null ) {
 				jpaQuery.setHint( QueryHints.HINT_CACHE_REGION, namedQueryDefinition.getCacheRegion() );
 			}
 		}
 
 		if ( namedQueryDefinition.getCacheMode() != null ) {
 			jpaQuery.setHint( QueryHints.HINT_CACHE_MODE, namedQueryDefinition.getCacheMode() );
 		}
 
 		if ( namedQueryDefinition.isReadOnly() ) {
 			jpaQuery.setHint( QueryHints.HINT_READONLY, true );
 		}
 
 		if ( namedQueryDefinition.getTimeout() != null ) {
 			jpaQuery.setHint( QueryHints.SPEC_HINT_TIMEOUT, namedQueryDefinition.getTimeout() * 1000 );
 		}
 
 		if ( namedQueryDefinition.getFetchSize() != null ) {
 			jpaQuery.setHint( QueryHints.HINT_FETCH_SIZE, namedQueryDefinition.getFetchSize() );
 		}
 
 		if ( namedQueryDefinition.getComment() != null ) {
 			jpaQuery.setHint( QueryHints.HINT_COMMENT, namedQueryDefinition.getComment() );
 		}
 
 		if ( namedQueryDefinition.getFirstResult() != null ) {
 			jpaQuery.setFirstResult( namedQueryDefinition.getFirstResult() );
 		}
 
 		if ( namedQueryDefinition.getMaxResults() != null ) {
 			jpaQuery.setMaxResults( namedQueryDefinition.getMaxResults() );
 		}
 
 		if ( namedQueryDefinition.getLockOptions() != null ) {
 			if ( namedQueryDefinition.getLockOptions().getLockMode() != null ) {
 				jpaQuery.setLockMode(
 						LockModeTypeHelper.getLockModeType( namedQueryDefinition.getLockOptions().getLockMode() )
 				);
 			}
 		}
 
 		if ( namedQueryDefinition.getFlushMode() != null ) {
 			if ( namedQueryDefinition.getFlushMode() == FlushMode.COMMIT ) {
 				jpaQuery.setFlushMode( FlushModeType.COMMIT );
 			}
 			else {
 				jpaQuery.setFlushMode( FlushModeType.AUTO );
 			}
 		}
 	}
 
 	protected QueryImpl createNamedSqlQuery(NamedSQLQueryDefinition namedQueryDefinition, Class resultType) {
 		if ( resultType != null ) {
 			resultClassChecking( resultType, namedQueryDefinition );
 		}
 		return wrapAsJpaQuery(
 				namedQueryDefinition,
 				( (SessionImplementor) internalGetSession() ).createSQLQuery( namedQueryDefinition )
 		);
 	}
 
 	protected void resultClassChecking(Class resultType, NamedSQLQueryDefinition namedQueryDefinition) {
 		final SessionFactoryImplementor sfi = entityManagerFactory.getSessionFactory();
 
 		final NativeSQLQueryReturn[] queryReturns;
 		if ( namedQueryDefinition.getQueryReturns() != null ) {
 			queryReturns = namedQueryDefinition.getQueryReturns();
 		}
 		else if ( namedQueryDefinition.getResultSetRef() != null ) {
 			final ResultSetMappingDefinition rsMapping = sfi.getResultSetMapping( namedQueryDefinition.getResultSetRef() );
 			queryReturns = rsMapping.getQueryReturns();
 		}
 		else {
 			throw new AssertionFailure( "Unsupported named query model. Please report the bug in Hibernate EntityManager");
 		}
 
 		if ( queryReturns.length > 1 ) {
 			throw new IllegalArgumentException( "Cannot create TypedQuery for query with more than one return" );
 		}
 
 		final NativeSQLQueryReturn nativeSQLQueryReturn = queryReturns[0];
 
 		if ( nativeSQLQueryReturn instanceof NativeSQLQueryRootReturn ) {
 			final Class<?> actualReturnedClass;
 			final String entityClassName = ( (NativeSQLQueryRootReturn) nativeSQLQueryReturn ).getReturnEntityName();
 			try {
 				actualReturnedClass = sfi.getServiceRegistry().getService( ClassLoaderService.class ).classForName( entityClassName );
 			}
 			catch ( ClassLoadingException e ) {
 				throw new AssertionFailure(
 						"Unable to load class [" + entityClassName + "] declared on named native query [" +
 								namedQueryDefinition.getName() + "]"
 				);
 			}
 			if ( !resultType.isAssignableFrom( actualReturnedClass ) ) {
 				throw buildIncompatibleException( resultType, actualReturnedClass );
 			}
 		}
 		else if ( nativeSQLQueryReturn instanceof NativeSQLQueryConstructorReturn ) {
 			final NativeSQLQueryConstructorReturn ctorRtn = (NativeSQLQueryConstructorReturn) nativeSQLQueryReturn;
 			if ( !resultType.isAssignableFrom( ctorRtn.getTargetClass() ) ) {
 				throw buildIncompatibleException( resultType, ctorRtn.getTargetClass() );
 			}
 		}
 		else {
 			//TODO support other NativeSQLQueryReturn type. For now let it go.
 		}
 	}
 
 	@Override
 	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
 		return buildQueryFromName( name, resultClass );
 	}
 
 	private IllegalArgumentException buildIncompatibleException(Class<?> resultClass, Class<?> actualResultClass) {
 		return new IllegalArgumentException(
 				"Type specified for TypedQuery [" + resultClass.getName() +
 						"] is incompatible with query return type [" + actualResultClass + "]"
 		);
 	}
 
 	@Override
 	public Query createNativeQuery(String sqlString) {
 		checkOpen();
 		try {
 			SQLQuery q = internalGetSession().createSQLQuery( sqlString );
 			return new QueryImpl( q, this );
 		}
 		catch ( RuntimeException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public Query createNativeQuery(String sqlString, Class resultClass) {
 		checkOpen();
 		try {
 			SQLQuery q = internalGetSession().createSQLQuery( sqlString );
 			q.addEntity( "alias1", resultClass.getName(), LockMode.READ );
 			return new QueryImpl( q, this );
 		}
 		catch ( RuntimeException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public Query createNativeQuery(String sqlString, String resultSetMapping) {
 		checkOpen();
 		try {
 			final SQLQuery q = internalGetSession().createSQLQuery( sqlString );
 			q.setResultSetMapping( resultSetMapping );
 			return new QueryImpl( q, this );
 		}
 		catch ( RuntimeException he ) {
 			throw convert( he );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
 		checkOpen();
 		try {
 			final ProcedureCallMemento memento = ( (SessionImplementor) internalGetSession() ).getFactory()
 					.getNamedQueryRepository().getNamedProcedureCallMemento( name );
 			if ( memento == null ) {
 				throw new IllegalArgumentException( "No @NamedStoredProcedureQuery was found with that name : " + name );
 			}
 			final StoredProcedureQueryImpl jpaImpl = new StoredProcedureQueryImpl( memento, this );
 			// apply hints
 			if ( memento.getHintsMap() != null ) {
 				for ( Map.Entry<String,Object> hintEntry : memento.getHintsMap().entrySet() ) {
 					jpaImpl.setHint( hintEntry.getKey(), hintEntry.getValue() );
 				}
 			}
 			return jpaImpl;
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
 		checkOpen();
 		try {
 			return new StoredProcedureQueryImpl(
 					internalGetSession().createStoredProcedureCall( procedureName ),
 					this
 			);
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
 		checkOpen();
 		try {
 			return new StoredProcedureQueryImpl(
 					internalGetSession().createStoredProcedureCall( procedureName, resultClasses ),
 					this
 			);
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
 		checkOpen();
 		try {
 			try {
 				return new StoredProcedureQueryImpl(
 						internalGetSession().createStoredProcedureCall( procedureName, resultSetMappings ),
 						this
 				);
 			}
 			catch (UnknownSqlResultSetMappingException unknownResultSetMapping) {
 				throw new IllegalArgumentException( unknownResultSetMapping.getMessage(), unknownResultSetMapping );
 			}
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
 		checkOpen();
 		try {
 			return ( T ) internalGetSession().load( entityClass, ( Serializable ) primaryKey );
 		}
 		catch ( MappingException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( TypeMismatchException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( ClassCastException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey) {
 		checkOpen();
 		return find( entityClass, primaryKey, null, null );
 	}
 
 	@Override
 	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
 		checkOpen();
 		return find( entityClass, primaryKey, null, properties );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType) {
 		checkOpen();
 		return find( entityClass, primaryKey, lockModeType, null );
 	}
 
 	@Override
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
 		checkOpen();
 		Session session = internalGetSession();
 		CacheMode previousCacheMode = session.getCacheMode();
 		CacheMode cacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			if ( properties != null && !properties.isEmpty() ) {
 				( (SessionImplementor) session ).getLoadQueryInfluencers()
 						.setFetchGraph( (EntityGraph) properties.get( QueryHints.HINT_FETCHGRAPH ) );
 				( (SessionImplementor) session ).getLoadQueryInfluencers()
 						.setLoadGraph( (EntityGraph) properties.get( QueryHints.HINT_LOADGRAPH ) );
 			}
 			session.setCacheMode( cacheMode );
 			if ( lockModeType != null ) {
 				lockOptions = getLockRequest( lockModeType, properties );
 				if ( !LockModeType.NONE.equals( lockModeType) ) {
 					checkTransactionNeeded();
 				}
 				return ( A ) session.get(
 						entityClass, ( Serializable ) primaryKey, 
 						lockOptions
 				);
 			}
 			else {
 				return ( A ) session.get( entityClass, ( Serializable ) primaryKey );
 			}
 		}
 		catch ( EntityNotFoundException ignored ) {
 			// DefaultLoadEventListener.returnNarrowedProxy may throw ENFE (see HHH-7861 for details),
 			// which find() should not throw.  Find() should return null if the entity was not found.
 			if ( LOG.isDebugEnabled() ) {
 				String entityName = entityClass != null ? entityClass.getName(): null;
 				String identifierValue = primaryKey != null ? primaryKey.toString() : null ;
 				LOG.ignoringEntityNotFound( entityName, identifierValue );
 			}
 			return null;
 		}
 		catch ( ObjectDeletedException e ) {
 			//the spec is silent about people doing remove() find() on the same PC
 			return null;
 		}
 		catch ( ObjectNotFoundException e ) {
 			//should not happen on the entity itself with get
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( MappingException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( TypeMismatchException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( ClassCastException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e, lockOptions );
 		}
 		finally {
 			session.setCacheMode( previousCacheMode );
 			( (SessionImplementor) session ).getLoadQueryInfluencers().setFetchGraph( null );
 			( (SessionImplementor) session ).getLoadQueryInfluencers().setLoadGraph( null );
 
 		}
 	}
 
 	public CacheMode determineAppropriateLocalCacheMode(Map<String, Object> localProperties) {
 		CacheRetrieveMode retrieveMode = null;
 		CacheStoreMode storeMode = null;
 		if ( localProperties != null ) {
 			retrieveMode = determineCacheRetrieveMode( localProperties );
 			storeMode = determineCacheStoreMode( localProperties );
 		}
 		if ( retrieveMode == null ) {
 			// use the EM setting
 			retrieveMode = determineCacheRetrieveMode( this.properties );
 		}
 		if ( storeMode == null ) {
 			// use the EM setting
 			storeMode = determineCacheStoreMode( this.properties );
 		}
 		return CacheModeHelper.interpretCacheMode( storeMode, retrieveMode );
 	}
 
 	private void checkTransactionNeeded() {
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException(
 					"no transaction is in progress"
 			);
 		}
 	}
 
 	@Override
 	public void persist(Object entity) {
 		checkOpen();
 		try {
 			internalGetSession().persist( entity );
 		}
 		catch ( MappingException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage() ) ) ;
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <A> A merge(A entity) {
 		checkOpen();
 		try {
 			return ( A ) internalGetSession().merge( entity );
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw convert( new IllegalArgumentException( sse ) );
 		}
 		catch ( MappingException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			//including HibernateException
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public void remove(Object entity) {
 		checkOpen();
 		try {
 			internalGetSession().delete( entity );
 		}
 		catch ( MappingException e ) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			//including HibernateException
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public void refresh(Object entity) {
 		refresh( entity, null, null );
 	}
 
 	@Override
 	public void refresh(Object entity, Map<String, Object> properties) {
 		refresh( entity, null, properties );
 	}
 
 	@Override
 	public void refresh(Object entity, LockModeType lockModeType) {
 		refresh( entity, lockModeType, null );
 	}
 
 	@Override
 	public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		checkOpen();
 
-		Session session = internalGetSession();
-		CacheMode previousCacheMode = session.getCacheMode();
-		CacheMode localCacheMode = determineAppropriateLocalCacheMode( properties );
+		final Session session = internalGetSession();
+		final CacheMode previousCacheMode = session.getCacheMode();
+		final CacheMode localCacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			session.setCacheMode( localCacheMode );
 			if ( !session.contains( entity ) ) {
 				throw convert ( new IllegalArgumentException( "Entity not managed" ) );
 			}
 			if ( lockModeType != null ) {
 				if ( !LockModeType.NONE.equals( lockModeType) ) {
 					checkTransactionNeeded();
 				}
 
 				lockOptions = getLockRequest( lockModeType, properties );
 				session.refresh( entity, lockOptions );
 			}
 			else {
 				session.refresh( entity );
 			}
 		}
 		catch (MappingException e) {
 			throw convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch (RuntimeException e) {
 			throw convert( e, lockOptions );
 		}
 		finally {
 			session.setCacheMode( previousCacheMode );
 		}
 	}
 
 	@Override
 	public boolean contains(Object entity) {
 		checkOpen();
 
 		try {
 			if ( entity != null
 					&& !( entity instanceof HibernateProxy )
 					&& internalGetSession().getSessionFactory().getClassMetadata( entity.getClass() ) == null ) {
 				throw convert( new IllegalArgumentException( "Not an entity:" + entity.getClass() ) );
 			}
 			return internalGetSession().contains( entity );
 		}
 		catch (MappingException e) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch (RuntimeException e) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public LockModeType getLockMode(Object entity) {
 		checkOpen();
 
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException( "Call to EntityManager#getLockMode should occur within transaction according to spec" );
 		}
 
 		if ( !contains( entity ) ) {
 			throw convert( new IllegalArgumentException( "entity not in the persistence context" ) );
 		}
 
 		return getLockModeType( internalGetSession().getCurrentLockMode( entity ) );
 	}
 
 	@Override
 	public void setProperty(String s, Object o) {
 		checkOpen();
 
 		if ( ENTITY_MANAGER_SPECIFIC_PROPERTIES.contains( s ) ) {
 			properties.put( s, o );
 			applyProperties();
         }
 		else {
 			LOG.debugf("Trying to set a property which is not supported on entity manager level");
 		}
 	}
 
 	@Override
 	public Map<String, Object> getProperties() {
 		return Collections.unmodifiableMap( properties );
 	}
 
 	@Override
 	public void flush() {
 		checkOpen();
 		checkTransactionNeeded();
 
 		try {
 			internalGetSession().flush();
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	/**
 	 * return a Session
 	 *
 	 * @throws IllegalStateException if the entity manager is closed
 	 */
 	public abstract Session getSession();
 
 	/**
 	 * Return a Session (even if the entity manager is closed).
 	 *
 	 * @return A session.
 	 * @deprecated Deprecated in favor of {@link #getRawSession()}
 	 */
 	@Deprecated
 	protected abstract Session getRawSession();
 
 	/**
 	 * Return a Session without any validation checks.
 	 *
 	 * @return A session.
 	 */
 	protected abstract Session internalGetSession();
 
 	@Override
 	public EntityTransaction getTransaction() {
 		if ( transactionType == PersistenceUnitTransactionType.JTA ) {
 			throw new IllegalStateException( "A JTA EntityManager cannot use getTransaction()" );
 		}
 		return tx;
 	}
 
 	@Override
 	public EntityManagerFactoryImpl getEntityManagerFactory() {
 		checkOpen();
 		return internalGetEntityManagerFactory();
 	}
 
 	protected EntityManagerFactoryImpl internalGetEntityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	@Override
 	public HibernateEntityManagerFactory getFactory() {
 		return entityManagerFactory;
 	}
 
 	@Override
 	public CriteriaBuilder getCriteriaBuilder() {
 
 		checkOpen();
 		return getEntityManagerFactory().getCriteriaBuilder();
 	}
 
 	@Override
 	public Metamodel getMetamodel() {
 		checkOpen();
 		return getEntityManagerFactory().getMetamodel();
 	}
 
 	@Override
 	public void setFlushMode(FlushModeType flushModeType) {
 		checkOpen();
 		if ( flushModeType == FlushModeType.AUTO ) {
 			internalGetSession().setFlushMode( FlushMode.AUTO );
 		}
 		else if ( flushModeType == FlushModeType.COMMIT ) {
 			internalGetSession().setFlushMode( FlushMode.COMMIT );
 		}
 		else {
 			throw new AssertionFailure( "Unknown FlushModeType: " + flushModeType );
 		}
 	}
 
 	@Override
 	public void clear() {
 		checkOpen();
 		try {
 			internalGetSession().clear();
 		}
 		catch (RuntimeException e) {
 			throw convert( e );
 		}
 	}
 
 	@Override
 	public void detach(Object entity) {
 		checkOpen();
 		try {
 			internalGetSession().evict( entity );
 		}
 		catch (RuntimeException e) {
 			throw convert( e );
 		}
 	}
 
 	/**
 	 * Hibernate can be set in various flush modes that are unknown to
 	 * JPA 2.0. This method can then return null.
 	 * If it returns null, do em.unwrap(Session.class).getFlushMode() to get the
 	 * Hibernate flush mode
 	 */
 	@Override
 	public FlushModeType getFlushMode() {
 		checkOpen();
 
 		FlushMode mode = internalGetSession().getFlushMode();
 		if ( mode == FlushMode.AUTO ) {
 			return FlushModeType.AUTO;
 		}
 		else if ( mode == FlushMode.COMMIT ) {
 			return FlushModeType.COMMIT;
 		}
 		else {
 			// otherwise this is an unknown mode for EJB3
 			return null;
 		}
 	}
 
 	public void lock(Object entity, LockModeType lockMode) {
 		lock( entity, lockMode, null );
 	}
 
 	public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		checkOpen();
 		checkTransactionNeeded();
 
 		LockOptions lockOptions = null;
 
 		try {
 			if ( !contains( entity ) ) {
 				throw new IllegalArgumentException( "entity not in the persistence context" );
 			}
 			lockOptions = getLockRequest( lockModeType, properties );
 			internalGetSession().buildLockRequest( lockOptions ).lock( entity );
 		}
 		catch (RuntimeException e) {
 			throw convert( e, lockOptions );
 		}
 	}
 
 	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
 		LockOptions lockOptions = new LockOptions();
 		LockOptions.copy( this.lockOptions, lockOptions );
 		lockOptions.setLockMode( getLockMode( lockModeType ) );
 		if ( properties != null ) {
 			setLockOptions( properties, lockOptions );
 		}
 		return lockOptions;
 	}
 
 	@SuppressWarnings("deprecation")
 	private static LockModeType getLockModeType(LockMode lockMode) {
 		//TODO check that if we have UPGRADE_NOWAIT we have a timeout of zero?
 		return LockModeTypeHelper.getLockModeType( lockMode );
 	}
 
 
 	private static LockMode getLockMode(LockModeType lockMode) {
 		return LockModeTypeHelper.getLockMode( lockMode );
 	}
 
 	public boolean isTransactionInProgress() {
 		return ( ( SessionImplementor ) internalGetSession() ).isTransactionInProgress();
 	}
 
 	private SessionFactoryImplementor sfi() {
 		return (SessionFactoryImplementor) internalGetSession().getSessionFactory();
 	}
 
 	@Override
 	public <T> T unwrap(Class<T> clazz) {
 		checkOpen();
 
 		if ( Session.class.isAssignableFrom( clazz ) ) {
 			return ( T ) internalGetSession();
 		}
 		if ( SessionImplementor.class.isAssignableFrom( clazz ) ) {
 			return ( T ) internalGetSession();
 		}
 		if ( EntityManager.class.isAssignableFrom( clazz ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap " + clazz );
 	}
 
 	@Override
 	public void markForRollbackOnly() {
         LOG.debugf("Mark transaction for rollback");
 		if ( tx.isActive() ) {
 			tx.setRollbackOnly();
 		}
 		else {
 			//no explicit use of the tx. boundaries methods
 			if ( PersistenceUnitTransactionType.JTA == transactionType ) {
 				TransactionManager transactionManager = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager();
 				if ( transactionManager == null ) {
 					throw new PersistenceException(
 							"Using a JTA persistence context wo setting hibernate.transaction.manager_lookup_class"
 					);
 				}
 				try {
 					if ( transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION ) {
 						transactionManager.setRollbackOnly();
 					}
 				}
 				catch (SystemException e) {
 					throw new PersistenceException( "Unable to set the JTA transaction as RollbackOnly", e );
 				}
 			}
 		}
 	}
 
 	@Override
 	public boolean isJoinedToTransaction() {
 		checkOpen();
 
 		final SessionImplementor session = (SessionImplementor) internalGetSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		return isOpen() && transaction.getJoinStatus() == JoinStatus.JOINED;
 	}
 
 	@Override
 	public void joinTransaction() {
 		checkOpen();
 		joinTransaction( true );
 	}
 
 	private void joinTransaction(boolean explicitRequest) {
 		if ( transactionType != PersistenceUnitTransactionType.JTA ) {
 			if ( explicitRequest ) {
 			    LOG.callingJoinTransactionOnNonJtaEntityManager();
 			}
 			return;
 		}
 
 		final SessionImplementor session = (SessionImplementor) internalGetSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		transaction.markForJoin();
 		transactionCoordinator.pulse();
 
 		LOG.debug( "Looking for a JTA transaction to join" );
 		if ( ! transactionCoordinator.isTransactionJoinable() ) {
 			if ( explicitRequest ) {
 				// if this is an explicit join request, log a warning so user can track underlying cause
 				// of subsequent exceptions/messages
 				LOG.unableToJoinTransaction(Environment.TRANSACTION_STRATEGY);
 			}
 		}
 
 		try {
 			if ( transaction.getJoinStatus() == JoinStatus.JOINED ) {
 				LOG.debug( "Transaction already joined" );
 				return; // noop
 			}
 
 			// join the transaction and then recheck the status
 			transaction.join();
 			if ( transaction.getJoinStatus() == JoinStatus.NOT_JOINED ) {
 				if ( explicitRequest ) {
 					throw new TransactionRequiredException( "No active JTA transaction on joinTransaction call" );
 				}
 				else {
 					LOG.debug( "Unable to join JTA transaction" );
 					return;
 				}
 			}
 			else if ( transaction.getJoinStatus() == JoinStatus.MARKED_FOR_JOINED ) {
 				throw new AssertionFailure( "Transaction MARKED_FOR_JOINED after isOpen() call" );
 			}
 
 			// register behavior changes
 			final SynchronizationCallbackCoordinator callbackCoordinator = transactionCoordinator.getSynchronizationCallbackCoordinator();
 			callbackCoordinator.setManagedFlushChecker( new ManagedFlushCheckerImpl() );
 			callbackCoordinator.setExceptionMapper( new CallbackExceptionMapperImpl() );
 			callbackCoordinator.setAfterCompletionAction( new AfterCompletionActionImpl( session, transactionType ) );
 		}
 		catch (HibernateException he) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * returns the underlying session
 	 */
 	public Object getDelegate() {
 		checkOpen();
 		return internalGetSession();
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		oos.defaultWriteObject();
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		tx = new TransactionImpl( this );
 	}
 
 	@Override
 	public void handlePersistenceException(PersistenceException e) {
 		if ( e instanceof NoResultException ) {
 			return;
 		}
 		if ( e instanceof NonUniqueResultException ) {
 			return;
 		}
 		if ( e instanceof LockTimeoutException ) {
 			return;
 		}
 		if ( e instanceof QueryTimeoutException ) {
 			return;
 		}
 
 		try {
 			markForRollbackOnly();
 		}
 		catch ( Exception ne ) {
 			//we do not want the subsequent exception to swallow the original one
             LOG.unableToMarkForRollbackOnPersistenceException(ne);
 		}
 	}
 
 	@Override
 	public void throwPersistenceException(PersistenceException e) {
 		handlePersistenceException( e );
 		throw e;
 	}
 
 	@Override
 	public RuntimeException convert(HibernateException e) {
 		//FIXME should we remove all calls to this method and use convert(RuntimeException) ?
 		return convert( e, null );
 	}
 
 	public RuntimeException convert(RuntimeException e) {
 		RuntimeException result = e;
 		if ( e instanceof HibernateException ) {
 			result = convert( (HibernateException) e );
 		}
 		else {
 			markForRollbackOnly();
 		}
 		return result;
 	}
 
 	public RuntimeException convert(RuntimeException e, LockOptions lockOptions) {
 		RuntimeException result = e;
 		if ( e instanceof HibernateException ) {
 			result = convert( (HibernateException) e , lockOptions );
 		}
 		else {
 			markForRollbackOnly();
 		}
 		return result;
 	}
 
 	@Override
 	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
 		if ( e instanceof StaleStateException ) {
 			final PersistenceException converted = wrapStaleStateException( (StaleStateException) e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof LockingStrategyException ) {
 			final PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.exception.LockTimeoutException ) {
 			final PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
 			final PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.QueryTimeoutException ) {
 			final QueryTimeoutException converted = new QueryTimeoutException( e.getMessage(), e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof ObjectNotFoundException ) {
 			final EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
-        else if ( e instanceof org.hibernate.NonUniqueObjectException ) {
+		else if ( e instanceof org.hibernate.NonUniqueObjectException ) {
 			final EntityExistsException converted = new EntityExistsException( e.getMessage() );
-            handlePersistenceException( converted );
-            return converted;
+			handlePersistenceException( converted );
+			return converted;
         }
 		else if ( e instanceof org.hibernate.NonUniqueResultException ) {
 			final NonUniqueResultException converted = new NonUniqueResultException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof UnresolvableObjectException ) {
 			final EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof QueryException ) {
 			return new IllegalArgumentException( e );
 		}
 		else if ( e instanceof TransientObjectException ) {
 			try {
 				markForRollbackOnly();
 			}
 			catch ( Exception ne ) {
 				//we do not want the subsequent exception to swallow the original one
-                LOG.unableToMarkForRollbackOnTransientObjectException(ne);
+				LOG.unableToMarkForRollbackOnTransientObjectException( ne );
 			}
 			return new IllegalStateException( e ); //Spec 3.2.3 Synchronization rules
 		}
 		else {
 			final PersistenceException converted = new PersistenceException( e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 	}
 
 	@Override
 	public void throwPersistenceException(HibernateException e) {
 		throw convert( e );
 	}
 
 	@Override
 	public PersistenceException wrapStaleStateException(StaleStateException e) {
 		PersistenceException pe;
 		if ( e instanceof StaleObjectStateException ) {
-			final StaleObjectStateException sose = ( StaleObjectStateException ) e;
+			final StaleObjectStateException sose = (StaleObjectStateException) e;
 			final Serializable identifier = sose.getIdentifier();
 			if ( identifier != null ) {
 				try {
 					final Object entity = internalGetSession().load( sose.getEntityName(), identifier );
 					if ( entity instanceof Serializable ) {
 						//avoid some user errors regarding boundary crossing
 						pe = new OptimisticLockException( e.getMessage(), e, entity );
 					}
 					else {
 						pe = new OptimisticLockException( e.getMessage(), e );
 					}
 				}
 				catch ( EntityNotFoundException enfe ) {
 					pe = new OptimisticLockException( e.getMessage(), e );
 				}
 			}
 			else {
 				pe = new OptimisticLockException( e.getMessage(), e );
 			}
 		}
 		else {
 			pe = new OptimisticLockException( e.getMessage(), e );
 		}
 		return pe;
 	}
 
 	public PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
 		final PersistenceException pe;
 		if ( e instanceof OptimisticEntityLockException ) {
 			final OptimisticEntityLockException lockException = (OptimisticEntityLockException) e;
 			pe = new OptimisticLockException( lockException.getMessage(), lockException, lockException.getEntity() );
 		}
 		else if ( e instanceof org.hibernate.exception.LockTimeoutException ) {
 			pe = new LockTimeoutException( e.getMessage(), e, null );
 		}
 		else if ( e instanceof PessimisticEntityLockException ) {
 			final PessimisticEntityLockException lockException = (PessimisticEntityLockException) e;
 			if ( lockOptions != null && lockOptions.getTimeOut() > -1 ) {
 				// assume lock timeout occurred if a timeout or NO WAIT was specified
 				pe = new LockTimeoutException( lockException.getMessage(), lockException, lockException.getEntity() );
 			}
 			else {
 				pe = new PessimisticLockException( lockException.getMessage(), lockException, lockException.getEntity() );
 			}
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
-			final org.hibernate.PessimisticLockException jdbcLockException = ( org.hibernate.PessimisticLockException ) e;
+			final org.hibernate.PessimisticLockException jdbcLockException = (org.hibernate.PessimisticLockException) e;
 			if ( lockOptions != null && lockOptions.getTimeOut() > -1 ) {
 				// assume lock timeout occurred if a timeout or NO WAIT was specified
 				pe = new LockTimeoutException( jdbcLockException.getMessage(), jdbcLockException, null );
 			}
 			else {
 				pe = new PessimisticLockException( jdbcLockException.getMessage(), jdbcLockException, null );
 			}
 		}
 		else {
 			pe = new OptimisticLockException( e );
 		}
 		return pe;
 	}
 
 	private static class AfterCompletionActionImpl implements AfterCompletionAction {
 		private final SessionImplementor session;
 		private final PersistenceUnitTransactionType transactionType;
 
 		private AfterCompletionActionImpl(SessionImplementor session, PersistenceUnitTransactionType transactionType) {
 			this.session = session;
 			this.transactionType = transactionType;
 		}
 
 		@Override
 		public void doAction(TransactionCoordinator transactionCoordinator, int status) {
 			if ( session.isClosed() ) {
                 LOG.trace("Session was closed; nothing to do");
 				return;
 			}
 
 			final boolean successful = JtaStatusHelper.isCommitted( status );
 			if ( !successful && transactionType == PersistenceUnitTransactionType.JTA ) {
 				( (Session) session ).clear();
 			}
 			session.getTransactionCoordinator().resetJoinStatus();
 		}
 	}
 
 	private static class ManagedFlushCheckerImpl implements ManagedFlushChecker {
 		@Override
 		public boolean shouldDoManagedFlush(TransactionCoordinator coordinator, int jtaStatus) {
 			return !coordinator.getTransactionContext().isClosed()
 					&& !coordinator.getTransactionContext().isFlushModeNever()
 					&& !JtaStatusHelper.isRollback( jtaStatus );
 		}
 	}
 
 	private class CallbackExceptionMapperImpl implements ExceptionMapper {
 		@Override
 		public RuntimeException mapStatusCheckFailure(String message, SystemException systemException) {
 			throw new PersistenceException( message, systemException );
 		}
 
 		@Override
 		public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure) {
 			if ( HibernateException.class.isInstance( failure ) ) {
 				throw convert( failure );
 			}
 			if ( PersistenceException.class.isInstance( failure ) ) {
 				throw failure;
 			}
 			throw new PersistenceException( message, failure );
 		}
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
index 25fc512856..966f920f79 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
@@ -1,372 +1,346 @@
 package org.hibernate.testing.cache;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.UUID;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.internal.CoreMessageLogger;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Strong Liu
  */
 abstract class AbstractReadWriteAccessStrategy extends BaseRegionAccessStrategy {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class, AbstractReadWriteAccessStrategy.class.getName()
 	);
 	private final UUID uuid = UUID.randomUUID();
 	private final AtomicLong nextLockId = new AtomicLong();
 	private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
 	protected java.util.concurrent.locks.Lock readLock = reentrantReadWriteLock.readLock();
 	protected java.util.concurrent.locks.Lock writeLock = reentrantReadWriteLock.writeLock();
 
 	/**
 	 * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
 	 * after the start of this transaction.
 	 */
 	@Override
 	public final Object get(Object key, long txTimestamp) throws CacheException {
 		LOG.debugf( "getting key[%s] from region[%s]", key, getInternalRegion().getName() );
 		try {
 			readLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 
 			boolean readable = item != null && item.isReadable( txTimestamp );
 			if ( readable ) {
 				LOG.debugf( "hit key[%s] in region[%s]", key, getInternalRegion().getName() );
 				return item.getValue();
 			}
 			else {
 				if ( item == null ) {
 					LOG.debugf( "miss key[%s] in region[%s]", key, getInternalRegion().getName());
 				} else {
 					LOG.debugf( "hit key[%s] in region[%s], but it is unreadable", key, getInternalRegion().getName() );
 				}
 				return null;
 			}
 		}
 		finally {
 			readLock.unlock();
 		}
 	}
 
 	abstract Comparator getVersionComparator();
 
 	/**
 	 * Returns <code>false</code> and fails to put the value if there is an existing un-writeable item mapped to this
 	 * key.
 	 */
 	@Override
 	public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		try {
 			LOG.debugf( "putting key[%s] -> value[%s] into region[%s]", key, value, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 			boolean writeable = item == null || item.isWriteable( txTimestamp, version, getVersionComparator() );
 			if ( writeable ) {
 				LOG.debugf( "putting key[%s] -> value[%s] into region[%s] success", key, value, getInternalRegion().getName() );
 				getInternalRegion().put( key, new Item( value, version, getInternalRegion().nextTimestamp() ) );
 				return true;
 			}
 			else {
 				LOG.debugf( "putting key[%s] -> value[%s] into region[%s] fail due to it is unwriteable", key, value, getInternalRegion().getName() );
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	/**
 	 * Soft-lock a cache item.
 	 */
+	@Override
 	public final SoftLock lockItem(Object key, Object version) throws CacheException {
 
 		try {
 			LOG.debugf( "locking key[%s] in region[%s]", key, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 			long timeout = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
 			final Lock lock = ( item == null ) ? new Lock( timeout, uuid, nextLockId(), version ) : item.lock(
 					timeout,
 					uuid,
 					nextLockId()
 			);
 			getInternalRegion().put( key, lock );
 			return lock;
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	/**
 	 * Soft-unlock a cache item.
 	 */
+	@Override
 	public final void unlockItem(Object key, SoftLock lock) throws CacheException {
 
 		try {
 			LOG.debugf( "unlocking key[%s] in region[%s]", key, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 
 			if ( ( item != null ) && item.isUnlockable( lock ) ) {
 				decrementLock( key, (Lock) item );
 			}
 			else {
 				handleLockExpiry( key, item );
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	private long nextLockId() {
 		return nextLockId.getAndIncrement();
 	}
 
 	/**
 	 * Unlock and re-put the given key, lock combination.
 	 */
 	protected void decrementLock(Object key, Lock lock) {
 		lock.unlock( getInternalRegion().nextTimestamp() );
 		getInternalRegion().put( key, lock );
 	}
 
 	/**
 	 * Handle the timeout of a previous lock mapped to this key
 	 */
 	protected void handleLockExpiry(Object key, Lockable lock) {
 		LOG.expired(key);
 		long ts = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
 		// create new lock that times out immediately
 		Lock newLock = new Lock( ts, uuid, nextLockId.getAndIncrement(), null );
 		newLock.unlock( ts );
 		getInternalRegion().put( key, newLock );
 	}
 
 	/**
 	 * Interface type implemented by all wrapper objects in the cache.
 	 */
 	protected static interface Lockable {
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be read by a transaction started at the given time.
 		 */
 		public boolean isReadable(long txTimestamp);
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be replaced with one of the given version by a
 		 * transaction started at the given time.
 		 */
 		public boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);
 
 		/**
 		 * Returns the enclosed value.
 		 */
 		public Object getValue();
 
 		/**
 		 * Returns <code>true</code> if the given lock can be unlocked using the given SoftLock instance as a handle.
 		 */
 		public boolean isUnlockable(SoftLock lock);
 
 		/**
 		 * Locks this entry, stamping it with the UUID and lockId given, with the lock timeout occuring at the specified
 		 * time.  The returned Lock object can be used to unlock the entry in the future.
 		 */
 		public Lock lock(long timeout, UUID uuid, long lockId);
 	}
 
 	/**
 	 * Wrapper type representing unlocked items.
 	 */
 	protected final static class Item implements Serializable, Lockable {
 
 		private static final long serialVersionUID = 1L;
 		private final Object value;
 		private final Object version;
 		private final long timestamp;
 
 		/**
 		 * Creates an unlocked item wrapping the given value with a version and creation timestamp.
 		 */
 		Item(Object value, Object version, long timestamp) {
 			this.value = value;
 			this.version = version;
 			this.timestamp = timestamp;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public boolean isReadable(long txTimestamp) {
 			return txTimestamp > timestamp;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			return version != null && versionComparator.compare( version, newVersion ) < 0;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object getValue() {
 			return value;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return false;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			return new Lock( timeout, uuid, lockId, version );
 		}
 	}
 
 	/**
 	 * Wrapper type representing locked items.
 	 */
 	protected final static class Lock implements Serializable, Lockable, SoftLock {
 
 		private static final long serialVersionUID = 2L;
 
 		private final UUID sourceUuid;
 		private final long lockId;
 		private final Object version;
 
 		private long timeout;
 		private boolean concurrent;
 		private int multiplicity = 1;
 		private long unlockTimestamp;
 
 		/**
 		 * Creates a locked item with the given identifiers and object version.
 		 */
 		Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
 			this.timeout = timeout;
 			this.lockId = lockId;
 			this.version = version;
 			this.sourceUuid = sourceUuid;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public boolean isReadable(long txTimestamp) {
 			return false;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			if ( txTimestamp > timeout ) {
 				// if timedout then allow write
 				return true;
 			}
 			if ( multiplicity > 0 ) {
 				// if still locked then disallow write
 				return false;
 			}
 			return version == null ? txTimestamp > unlockTimestamp : versionComparator.compare(
 					version,
 					newVersion
 			) < 0;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Object getValue() {
 			return null;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return equals( lock );
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
 		@Override
 		public boolean equals(Object o) {
 			if ( o == this ) {
 				return true;
 			}
 			else if ( o instanceof Lock ) {
 				return ( lockId == ( (Lock) o ).lockId ) && sourceUuid.equals( ( (Lock) o ).sourceUuid );
 			}
 			else {
 				return false;
 			}
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
 		@Override
 		public int hashCode() {
 			int hash = ( sourceUuid != null ? sourceUuid.hashCode() : 0 );
 			int temp = (int) lockId;
 			for ( int i = 1; i < Long.SIZE / Integer.SIZE; i++ ) {
 				temp ^= ( lockId >>> ( i * Integer.SIZE ) );
 			}
 			return hash + temp;
 		}
 
 		/**
 		 * Returns true if this Lock has been concurrently locked by more than one transaction.
 		 */
 		public boolean wasLockedConcurrently() {
 			return concurrent;
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			concurrent = true;
 			multiplicity++;
 			this.timeout = timeout;
 			return this;
 		}
 
 		/**
 		 * Unlocks this Lock, and timestamps the unlock event.
 		 */
 		public void unlock(long timestamp) {
 			if ( --multiplicity == 0 ) {
 				unlockTimestamp = timestamp;
 			}
 		}
 
-		/**
-		 * {@inheritDoc}
-		 */
+		@Override
 		public String toString() {
 			StringBuilder sb = new StringBuilder( "Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId );
 			return sb.toString();
 		}
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java
index a8098a2fa4..376b284360 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java
@@ -1,45 +1,34 @@
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 class TransactionalEntityRegionAccessStrategy extends BaseEntityRegionAccessStrategy {
 	TransactionalEntityRegionAccessStrategy(EntityRegionImpl region) {
 		super( region );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean afterInsert(Object key, Object value, Object version) {
 		return false;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
 		return false;
 	}
 
-
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	public void remove(Object key) throws CacheException {
 		evict( key );
 	}
 
-
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean update(Object key, Object value, Object currentVersion,
 						  Object previousVersion) throws CacheException {
 		return insert( key, value, currentVersion );
 	}
 }
