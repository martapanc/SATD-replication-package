diff --git a/hibernate-core/src/main/antlr/order-by.g b/hibernate-core/src/main/antlr/order-by.g
index cc798d4fac..dc141d1fe6 100644
--- a/hibernate-core/src/main/antlr/order-by.g
+++ b/hibernate-core/src/main/antlr/order-by.g
@@ -1,440 +1,440 @@
 header
 {
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
 package org.hibernate.sql.ordering.antlr;
 }
 /**
  * Antlr grammar for dealing with <tt>order-by</tt> mapping fragments.
 
  * @author Steve Ebersole
  */
 class GeneratedOrderByFragmentParser extends Parser;
 
 options
 {
 	exportVocab=OrderByTemplate;
 	buildAST=true;
 	k=3;
 }
 
 tokens
 {
     // synthetic tokens
     ORDER_BY;
     SORT_SPEC;
     ORDER_SPEC;
     SORT_KEY;
     EXPR_LIST;
     DOT;
     IDENT_LIST;
     COLUMN_REF;
 
     COLLATE="collate";
 	ASCENDING="asc";
 	DESCENDING="desc";
 }
 
 
 {
     /**
      * Method for logging execution trace information.
      *
      * @param msg The trace message.
      */
     protected void trace(String msg) {
         System.out.println( msg );
     }
 
     /**
      * Extract a node's text.
      *
      * @param ast The node
      *
      * @return The text.
      */
     protected final String extractText(AST ast) {
         // for some reason, within AST creation blocks "[]" I am somtimes unable to refer to the AST.getText() method
         // using #var (the #var is not interpreted as the rule's output AST).
         return ast.getText();
     }
 
     /**
      * Process the given node as a quote identifier.  These need to be quoted in the dialect-specific way.
      *
      * @param ident The quoted-identifier node.
      *
      * @return The processed node.
      *
      * @see org.hibernate.dialect.Dialect#quote
      */
     protected AST quotedIdentifier(AST ident) {
         return ident;
     }
 
     /**
      * Process the given node as a quote string.
      *
      * @param ident The quoted string.  This is used from within function param recognition, and represents a
      * SQL-quoted string.
      *
      * @return The processed node.
      */
     protected AST quotedString(AST ident) {
         return ident;
     }
 
     /**
      * A check to see if the text of the given node represents a known function name.
      *
      * @param ast The node whose text we want to check.
      *
      * @return True if the node's text is a known function name, false otherwise.
      *
      * @see org.hibernate.dialect.function.SQLFunctionRegistry
      */
     protected boolean isFunctionName(AST ast) {
         return false;
     }
 
     /**
      * Process the given node as a function.
      *
      * @param The node representing the function invocation (including parameters as subtree components).
      *
      * @return The processed node.
      */
     protected AST resolveFunction(AST ast) {
         return ast;
     }
 
     /**
      * Process the given node as an IDENT.  May represent either a column reference or a property reference.
      *
      * @param ident The node whose text represents either a column or property reference.
      *
      * @return The processed node.
      */
     protected AST resolveIdent(AST ident) {
         return ident;
     }
 
     /**
      * Allow post processing of each <tt>sort specification</tt>
      *
      * @param The grammar-built sort specification subtree.
      *
      * @return The processed sort specification subtree.
      */
     protected AST postProcessSortSpecification(AST sortSpec) {
         return sortSpec;
     }
 
 }
 
 /**
  * Main recognition rule for this grammar
  */
 orderByFragment { trace("orderByFragment"); }
     : sortSpecification ( COMMA! sortSpecification  )* {
         #orderByFragment = #( [ORDER_BY, "order-by"], #orderByFragment );
     }
     ;
 
 /**
- * Reconition rule for what ANSI SQL terms the <tt>sort specification</tt>, which is essentially each thing upon which
+ * Recognition rule for what ANSI SQL terms the <tt>sort specification</tt>, which is essentially each thing upon which
  * the results should be sorted.
  */
 sortSpecification { trace("sortSpecification"); }
     : sortKey (collationSpecification)? (orderingSpecification)? {
         #sortSpecification = #( [SORT_SPEC, "{sort specification}"], #sortSpecification );
         #sortSpecification = postProcessSortSpecification( #sortSpecification );
     }
     ;
 
 /**
- * Reconition rule for what ANSI SQL terms the <tt>sort key</tt> which is the expression (column, function, etc) upon
+ * Recognition rule for what ANSI SQL terms the <tt>sort key</tt> which is the expression (column, function, etc) upon
  * which to base the sorting.
  */
 sortKey! { trace("sortKey"); }
     : e:expression {
         #sortKey = #( [SORT_KEY, "sort key"], #e );
     }
     ;
 
 /**
- * Reconition rule what this grammar recognizes as valid <tt>sort key</tt>.
+ * Recognition rule what this grammar recognizes as valid <tt>sort key</tt>.
  */
 expression! { trace("expression"); }
     : HARD_QUOTE qi:IDENT HARD_QUOTE {
         #expression = quotedIdentifier( #qi );
     }
     | ( IDENT (DOT IDENT)* OPEN_PAREN ) => f:functionCall {
         #expression = #f;
     }
     | p:simplePropertyPath {
         #expression = resolveIdent( #p );
     }
     | i:IDENT {
         if ( isFunctionName( #i ) ) {
             #expression = resolveFunction( #i );
         }
         else {
             #expression = resolveIdent( #i );
         }
     }
     ;
 
 /**
  * Intended for use as a syntactic predicate to determine whether an IDENT represents a known SQL function name.
  */
 functionCallCheck! { trace("functionCallCheck"); }
     : IDENT (DOT IDENT)* OPEN_PAREN { true }?
     ;
 
 /**
  * Recognition rule for a function call
  */
 functionCall! { trace("functionCall"); }
     : fn:functionName OPEN_PAREN pl:functionParameterList CLOSE_PAREN {
         #functionCall = #( [IDENT, extractText( #fn )], #pl );
         #functionCall = resolveFunction( #functionCall );
     }
     ;
 
 /**
  * A function-name is an IDENT followed by zero or more (DOT IDENT) sequences
  */
 functionName {
         trace("functionName");
         StringBuilder buffer = new StringBuilder();
     }
     : i:IDENT { buffer.append( i.getText() ); }
             ( DOT i2:IDENT { buffer.append( '.').append( i2.getText() ); } )* {
         #functionName = #( [IDENT,buffer.toString()] );
     }
     ;
 
 /**
  * Recognition rule used to "wrap" all function parameters into an EXPR_LIST node
  */
 functionParameterList { trace("functionParameterList"); }
     : functionParameter ( COMMA! functionParameter )* {
         #functionParameterList = #( [EXPR_LIST, "{param list}"], #functionParameterList );
     }
     ;
 
 /**
  * Recognized function parameters.
  */
 functionParameter { trace("functionParameter"); }
     : expression
     | NUM_DOUBLE
     | NUM_FLOAT
     | NUM_INT
     | NUM_LONG
     | QUOTED_STRING {
         #functionParameter = quotedString( #functionParameter );
     }
     ;
 
 /**
- * Reconition rule for what ANSI SQL terms the <tt>collation specification</tt> used to allow specifying that sorting for
+ * Recognition rule for what ANSI SQL terms the <tt>collation specification</tt> used to allow specifying that sorting for
  * the given {@link #sortSpecification} be treated within a specific character-set.
  */
 collationSpecification! { trace("collationSpecification"); }
     : c:COLLATE cn:collationName {
         #collationSpecification = #( [COLLATE, extractText( #cn )] );
     }
     ;
 
 /**
  * The collation name wrt {@link #collationSpecification}.  Namely, the character-set.
  */
 collationName { trace("collationSpecification"); }
     : IDENT
     ;
 
 /**
- * Reconition rule for what ANSI SQL terms the <tt>ordering specification</tt>; <tt>ASCENDING</tt> or
+ * Recognition rule for what ANSI SQL terms the <tt>ordering specification</tt>; <tt>ASCENDING</tt> or
  * <tt>DESCENDING</tt>.
  */
 orderingSpecification! { trace("orderingSpecification"); }
     : ( "asc" | "ascending" ) {
         #orderingSpecification = #( [ORDER_SPEC, "asc"] );
     }
     | ( "desc" | "descending") {
         #orderingSpecification = #( [ORDER_SPEC, "desc"] );
     }
     ;
 
 /**
  * A simple-property-path is an IDENT followed by one or more (DOT IDENT) sequences
  */
 simplePropertyPath {
         trace("simplePropertyPath");
         StringBuilder buffer = new StringBuilder();
     }
     : i:IDENT { buffer.append( i.getText() ); }
             ( DOT i2:IDENT { buffer.append( '.').append( i2.getText() ); } )+ {
         #simplePropertyPath = #( [IDENT,buffer.toString()] );
     }
     ;
 
 
 // **** LEXER ******************************************************************
 
 /**
  * Lexer for the <tt>order-by</tt> fragment parser
 
  * @author Steve Ebersole
  * @author Joshua Davis
  */
 class GeneratedOrderByLexer extends Lexer;
 
 options {
 	exportVocab=OrderByTemplate;
 	testLiterals = false;
 	k=2;
 	charVocabulary='\u0000'..'\uFFFE';	// Allow any char but \uFFFF (16 bit -1, ANTLR's EOF character)
 	caseSensitive = false;
 	caseSensitiveLiterals = false;
 }
 
 // -- Keywords --
 
 OPEN_PAREN: '(';
 CLOSE_PAREN: ')';
 
 COMMA: ',';
 
 HARD_QUOTE: '`';
 
 IDENT options { testLiterals=true; }
 	: ID_START_LETTER ( ID_LETTER )*
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
 
 //--- From the Java example grammar ---
 // a numeric literal
 NUM_INT
 	{boolean isDecimal=false; Token t=null;}
 	:   '.' {_ttype = DOT;}
 			(	('0'..'9')+ (EXPONENT)? (f1:FLOAT_SUFFIX {t=f1;})?
 				{
 					if (t != null && t.getText().toUpperCase().indexOf('F')>=0)
 					{
 						_ttype = NUM_FLOAT;
 					}
 					else
 					{
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
 
 		// only check to see if it's a float if looks like decimal so far
 		|	{isDecimal}?
 			(   '.' ('0'..'9')* (EXPONENT)? (f2:FLOAT_SUFFIX {t=f2;})?
 			|   EXPONENT (f3:FLOAT_SUFFIX {t=f3;})?
 			|   f4:FLOAT_SUFFIX {t=f4;}
 			)
 			{
 				if (t != null && t.getText().toUpperCase() .indexOf('F') >= 0)
 				{
 					_ttype = NUM_FLOAT;
 				}
 				else
 				{
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
 	:	'f'|'d'
 	;
 
 WS  :   (   ' '
 		|   '\t'
 		|   '\r' '\n' { newline(); }
 		|   '\n'      { newline(); }
 		|   '\r'      { newline(); }
 		)
 		{$setType(Token.SKIP);} //ignore this token
 	;
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index ec5f982e2a..23064a82c6 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -1,1585 +1,1431 @@
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
 package org.hibernate.cfg.annotations;
 
-import java.util.ArrayList;
-import java.util.Comparator;
-import java.util.HashMap;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Map;
-import java.util.Properties;
-import java.util.StringTokenizer;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.FetchType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.OneToMany;
+import java.util.Comparator;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.Map;
+import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.CollectionType;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterJoinTable;
 import org.hibernate.annotations.FilterJoinTables;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.LazyCollection;
 import org.hibernate.annotations.LazyCollectionOption;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.OrderBy;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.Sort;
 import org.hibernate.annotations.SortType;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.WhereJoinTable;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyInferredData;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
-import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.SimpleValue;
-import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 
 /**
  * Base class for binding different types of collections to Hibernate configuration objects.
  *
  * @author inger
  * @author Emmanuel Bernard
  */
 @SuppressWarnings({"unchecked", "serial"})
 public abstract class CollectionBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionBinder.class.getName());
 
 	protected Collection collection;
 	protected String propertyName;
 	PropertyHolder propertyHolder;
 	int batchSize;
 	private String mappedBy;
 	private XClass collectionType;
 	private XClass targetEntity;
 	private Mappings mappings;
 	private Ejb3JoinColumn[] inverseJoinColumns;
 	private String cascadeStrategy;
 	String cacheConcurrencyStrategy;
 	String cacheRegionName;
 	private boolean oneToMany;
 	protected IndexColumn indexColumn;
 	private String orderBy;
 	protected String hqlOrderBy;
 	private boolean isSorted;
 	private Class comparator;
 	private boolean hasToBeSorted;
 	protected boolean cascadeDeleteEnabled;
 	protected String mapKeyPropertyName;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private Ejb3JoinColumn[] fkJoinColumns;
 	private boolean isExplicitAssociationTable;
 	private Ejb3Column[] elementColumns;
 	private boolean isEmbedded;
 	private XProperty property;
 	private boolean ignoreNotFound;
 	private TableBinder tableBinder;
 	private Ejb3Column[] mapKeyColumns;
 	private Ejb3JoinColumn[] mapKeyManyToManyColumns;
 	protected HashMap<String, IdGenerator> localGenerators;
 	protected Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private AccessType accessType;
 	private boolean hibernateExtensionMapping;
 
 	private String explicitType;
 	private Properties explicitTypeParameters = new Properties();
 
 	protected Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public void setIsHibernateExtensionMapping(boolean hibernateExtensionMapping) {
 		this.hibernateExtensionMapping = hibernateExtensionMapping;
 	}
 
 	protected boolean isHibernateExtensionMapping() {
 		return hibernateExtensionMapping;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setCascadeStrategy(String cascadeStrategy) {
 		this.cascadeStrategy = cascadeStrategy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setInverseJoinColumns(Ejb3JoinColumn[] inverseJoinColumns) {
 		this.inverseJoinColumns = inverseJoinColumns;
 	}
 
 	public void setJoinColumns(Ejb3JoinColumn[] joinColumns) {
 		this.joinColumns = joinColumns;
 	}
 
 	private Ejb3JoinColumn[] joinColumns;
 
 	public void setPropertyHolder(PropertyHolder propertyHolder) {
 		this.propertyHolder = propertyHolder;
 	}
 
 	public void setBatchSize(BatchSize batchSize) {
 		this.batchSize = batchSize == null ? -1 : batchSize.size();
 	}
 
 	public void setEjb3OrderBy(javax.persistence.OrderBy orderByAnn) {
 		if ( orderByAnn != null ) {
 			hqlOrderBy = orderByAnn.value();
 		}
 	}
 
 	public void setSqlOrderBy(OrderBy orderByAnn) {
 		if ( orderByAnn != null ) {
 			if ( !BinderHelper.isEmptyAnnotationValue( orderByAnn.clause() ) ) {
 				orderBy = orderByAnn.clause();
 			}
 		}
 	}
 
 	public void setSort(Sort sortAnn) {
 		if ( sortAnn != null ) {
 			isSorted = !SortType.UNSORTED.equals( sortAnn.type() );
 			if ( isSorted && SortType.COMPARATOR.equals( sortAnn.type() ) ) {
 				comparator = sortAnn.comparator();
 			}
 		}
 	}
 
 	/**
 	 * collection binder factory
 	 */
 	public static CollectionBinder getCollectionBinder(
 			String entityName,
 			XProperty property,
 			boolean isIndexed,
 			boolean isHibernateExtensionMapping,
 			Mappings mappings) {
 		CollectionBinder result;
 		if ( property.isArray() ) {
 			if ( property.getElementClass().isPrimitive() ) {
 				result = new PrimitiveArrayBinder();
 			}
 			else {
 				result = new ArrayBinder();
 			}
 		}
 		else if ( property.isCollection() ) {
 			//TODO consider using an XClass
 			Class returnedClass = property.getCollectionClass();
 			if ( java.util.Set.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder();
 			}
 			else if ( java.util.SortedSet.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( true );
 			}
 			else if ( java.util.Map.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder();
 			}
 			else if ( java.util.SortedMap.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( true );
 			}
 			else if ( java.util.Collection.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else if ( java.util.List.class.equals( returnedClass ) ) {
 				if ( isIndexed ) {
 					if ( property.isAnnotationPresent( CollectionId.class ) ) {
 						throw new AnnotationException(
 								"List do not support @CollectionId and @OrderColumn (or @IndexColumn) at the same time: "
 								+ StringHelper.qualify( entityName, property.getName() ) );
 					}
 					result = new ListBinder();
 				}
 				else if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else {
 				throw new AnnotationException(
 						returnedClass.getName() + " collection not yet supported: "
 								+ StringHelper.qualify( entityName, property.getName() )
 				);
 			}
 		}
 		else {
 			throw new AnnotationException(
 					"Illegal attempt to map a non collection as a @OneToMany, @ManyToMany or @CollectionOfElements: "
 							+ StringHelper.qualify( entityName, property.getName() )
 			);
 		}
 		result.setIsHibernateExtensionMapping( isHibernateExtensionMapping );
 
 		final CollectionType typeAnnotation = property.getAnnotation( CollectionType.class );
 		if ( typeAnnotation != null ) {
 			final String typeName = typeAnnotation.type();
 			// see if it names a type-def
 			final TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				result.explicitType = typeDef.getTypeClass();
 				result.explicitTypeParameters.putAll( typeDef.getParameters() );
 			}
 			else {
 				result.explicitType = typeName;
 				for ( Parameter param : typeAnnotation.parameters() ) {
 					result.explicitTypeParameters.setProperty( param.name(), param.value() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	protected CollectionBinder() {
 	}
 
 	protected CollectionBinder(boolean sorted) {
 		this.hasToBeSorted = sorted;
 	}
 
 	public void setMappedBy(String mappedBy) {
 		this.mappedBy = mappedBy;
 	}
 
 	public void setTableBinder(TableBinder tableBinder) {
 		this.tableBinder = tableBinder;
 	}
 
 	public void setCollectionType(XClass collectionType) {
 		// NOTE: really really badly named.  This is actually NOT the collection-type, but rather the collection-element-type!
 		this.collectionType = collectionType;
 	}
 
 	public void setTargetEntity(XClass targetEntity) {
 		this.targetEntity = targetEntity;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	protected abstract Collection createCollection(PersistentClass persistentClass);
 
 	public Collection getCollection() {
 		return collection;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	public void bind() {
 		this.collection = createCollection( propertyHolder.getPersistentClass() );
 		String role = StringHelper.qualify( propertyHolder.getPath(), propertyName );
 		LOG.debugf( "Collection role: %s", role );
 		collection.setRole( role );
 		collection.setNodeName( propertyName );
 
 		if ( property.isAnnotationPresent( MapKeyColumn.class )
 			&& mapKeyPropertyName != null ) {
 			throw new AnnotationException(
 					"Cannot mix @javax.persistence.MapKey and @MapKeyColumn or @org.hibernate.annotations.MapKey "
 							+ "on the same collection: " + StringHelper.qualify(
 							propertyHolder.getPath(), propertyName
 					)
 			);
 		}
 
 		// set explicit type information
 		if ( explicitType != null ) {
 			final TypeDef typeDef = mappings.getTypeDef( explicitType );
 			if ( typeDef == null ) {
 				collection.setTypeName( explicitType );
 				collection.setTypeParameters( explicitTypeParameters );
 			}
 			else {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 		}
 
 		//set laziness
 		defineFetchingStrategy();
 		collection.setBatchSize( batchSize );
 		if ( orderBy != null && hqlOrderBy != null ) {
 			throw new AnnotationException(
 					"Cannot use sql order by clause in conjunction of EJB3 order by clause: " + safeCollectionRole()
 			);
 		}
 
 		collection.setMutable( !property.isAnnotationPresent( Immutable.class ) );
 
 		//work on association
 		boolean isMappedBy = !BinderHelper.isEmptyAnnotationValue( mappedBy );
 
 		final OptimisticLock lockAnn = property.getAnnotation( OptimisticLock.class );
 		final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 				? ! lockAnn.excluded()
 				: ! isMappedBy;
 		collection.setOptimisticLocked( includeInOptimisticLockChecks );
 
 		Persister persisterAnn = property.getAnnotation( Persister.class );
 		if ( persisterAnn != null ) {
 			collection.setCollectionPersisterClass( persisterAnn.impl() );
 		}
 
 		// set ordering
 		if ( orderBy != null ) collection.setOrderBy( orderBy );
 		if ( isSorted ) {
 			collection.setSorted( true );
 			if ( comparator != null ) {
 				try {
 					collection.setComparator( (Comparator) comparator.newInstance() );
 				}
 				catch (ClassCastException e) {
 					throw new AnnotationException(
 							"Comparator not implementing java.util.Comparator class: "
 									+ comparator.getName() + "(" + safeCollectionRole() + ")"
 					);
 				}
 				catch (Exception e) {
 					throw new AnnotationException(
 							"Could not instantiate comparator class: "
 									+ comparator.getName() + "(" + safeCollectionRole() + ")"
 					);
 				}
 			}
 		}
 		else {
 			if ( hasToBeSorted ) {
 				throw new AnnotationException(
 						"A sorted collection has to define @Sort: "
 								+ safeCollectionRole()
 				);
 			}
 		}
 
 		//set cache
 		if ( StringHelper.isNotEmpty( cacheConcurrencyStrategy ) ) {
 			collection.setCacheConcurrencyStrategy( cacheConcurrencyStrategy );
 			collection.setCacheRegionName( cacheRegionName );
 		}
 
 		//SQL overriding
 		SQLInsert sqlInsert = property.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = property.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = property.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = property.getAnnotation( SQLDeleteAll.class );
 		Loader loader = property.getAnnotation( Loader.class );
 		if ( sqlInsert != null ) {
 			collection.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			collection.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			collection.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
 			);
 		}
 		if ( loader != null ) {
 			collection.setLoaderName( loader.namedQuery() );
 		}
 
 		if (isMappedBy
 				&& (property.isAnnotationPresent( JoinColumn.class )
 					|| property.isAnnotationPresent( JoinColumns.class )
 					|| propertyHolder.getJoinTable( property ) != null ) ) {
 			String message = "Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: ";
 			message += StringHelper.qualify( propertyHolder.getPath(), propertyName );
 			throw new AnnotationException( message );
 		}
 
 		collection.setInverse( isMappedBy );
 
 		//many to many may need some second pass informations
 		if ( !oneToMany && isMappedBy ) {
 			mappings.addMappedBy( getCollectionType().getName(), mappedBy, propertyName );
 		}
 		//TODO reducce tableBinder != null and oneToMany
 		XClass collectionType = getCollectionType();
 		if ( inheritanceStatePerClass == null) throw new AssertionFailure( "inheritanceStatePerClass not set" );
 		SecondPass sp = getSecondPass(
 				fkJoinColumns,
 				joinColumns,
 				inverseJoinColumns,
 				elementColumns,
 				mapKeyColumns, mapKeyManyToManyColumns, isEmbedded,
 				property, collectionType,
 				ignoreNotFound, oneToMany,
 				tableBinder, mappings
 		);
 		if ( collectionType.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( ElementCollection.class ) //JPA 2
 				) {
 			// do it right away, otherwise @ManyToOne on composite element call addSecondPass
 			// and raise a ConcurrentModificationException
 			//sp.doSecondPass( CollectionHelper.EMPTY_MAP );
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 		else {
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 
 		mappings.addCollection( collection );
 
 		//property building
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( collection );
 		binder.setCascade( cascadeStrategy );
 		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 		binder.setAccessType( accessType );
 		binder.setProperty( property );
 		binder.setInsertable( insertable );
 		binder.setUpdatable( updatable );
 		Property prop = binder.makeProperty();
 		//we don't care about the join stuffs because the column is on the association table.
 		if (! declaringClassSet) throw new AssertionFailure( "DeclaringClass is not set in CollectionBinder while binding" );
 		propertyHolder.addProperty( prop, declaringClass );
 	}
 
 	private void defineFetchingStrategy() {
 		LazyCollection lazy = property.getAnnotation( LazyCollection.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		ManyToMany manyToMany = property.getAnnotation( ManyToMany.class );
 		ElementCollection elementCollection = property.getAnnotation( ElementCollection.class ); //jpa 2
 		ManyToAny manyToAny = property.getAnnotation( ManyToAny.class );
 		FetchType fetchType;
 		if ( oneToMany != null ) {
 			fetchType = oneToMany.fetch();
 		}
 		else if ( manyToMany != null ) {
 			fetchType = manyToMany.fetch();
 		}
 		else if ( elementCollection != null ) {
 			fetchType = elementCollection.fetch();
 		}
 		else if ( manyToAny != null ) {
 			fetchType = FetchType.LAZY;
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @ManyToOne nor @OneToMany nor @CollectionOfElements"
 			);
 		}
 		if ( lazy != null ) {
 			collection.setLazy( !( lazy.value() == LazyCollectionOption.FALSE ) );
 			collection.setExtraLazy( lazy.value() == LazyCollectionOption.EXTRA );
 		}
 		else {
 			collection.setLazy( fetchType == FetchType.LAZY );
 			collection.setExtraLazy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				collection.setFetchMode( FetchMode.JOIN );
 				collection.setLazy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 				collection.setSubselectLoadable( true );
 				collection.getOwner().setSubselectLoadableCollections( true );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			collection.setFetchMode( AnnotationBinder.getFetchMode( fetchType ) );
 		}
 	}
 
 	private XClass getCollectionType() {
 		if ( AnnotationBinder.isDefault( targetEntity, mappings ) ) {
 			if ( collectionType != null ) {
 				return collectionType;
 			}
 			else {
 				String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: "
 						+ safeCollectionRole();
 				throw new AnnotationException( errorMsg );
 			}
 		}
 		else {
 			return targetEntity;
 		}
 	}
 
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final Mappings mappings) {
 		return new CollectionSecondPass( mappings, collection ) {
 			@Override
             public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, mappings
 				);
 			}
 		};
 	}
 
 	/**
 	 * return true if it's a Fk, false if it's an association table
 	 */
 	protected boolean bindStarToManySecondPass(
 			Map persistentClasses,
 			XClass collType,
 			Ejb3JoinColumn[] fkJoinColumns,
 			Ejb3JoinColumn[] keyColumns,
 			Ejb3JoinColumn[] inverseColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XProperty property,
 			boolean unique,
 			TableBinder associationTableBinder,
 			boolean ignoreNotFound,
 			Mappings mappings) {
 		PersistentClass persistentClass = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean reversePropertyInJoin = false;
 		if ( persistentClass != null && StringHelper.isNotEmpty( this.mappedBy ) ) {
 			try {
 				reversePropertyInJoin = 0 != persistentClass.getJoinNumber(
 						persistentClass.getRecursiveProperty( this.mappedBy )
 				);
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( this.mappedBy )
 						.append( " in " )
 						.append( collection.getOwnerEntityName() )
 						.append( "." )
 						.append( property.getName() );
 				throw new AnnotationException( error.toString() );
 			}
 		}
 		if ( persistentClass != null
 				&& !reversePropertyInJoin
 				&& oneToMany
 				&& !this.isExplicitAssociationTable
 				&& ( joinColumns[0].isImplicit() && !BinderHelper.isEmptyAnnotationValue( this.mappedBy ) //implicit @JoinColumn
 				|| !fkJoinColumns[0].isImplicit() ) //this is an explicit @JoinColumn
 				) {
 			//this is a Foreign key
 			bindOneToManySecondPass(
 					getCollection(),
 					persistentClasses,
 					fkJoinColumns,
 					collType,
 					cascadeDeleteEnabled,
 					ignoreNotFound, hqlOrderBy,
 					mappings,
 					inheritanceStatePerClass
 			);
 			return true;
 		}
 		else {
 			//this is an association table
 			bindManyToManySecondPass(
 					this.collection,
 					persistentClasses,
 					keyColumns,
 					inverseColumns,
 					elementColumns,
 					isEmbedded, collType,
 					ignoreNotFound, unique,
 					cascadeDeleteEnabled,
 					associationTableBinder, property, propertyHolder, hqlOrderBy, mappings
 			);
 			return false;
 		}
 	}
 
 	protected void bindOneToManySecondPass(
 			Collection collection,
 			Map persistentClasses,
 			Ejb3JoinColumn[] fkJoinColumns,
 			XClass collectionType,
 			boolean cascadeDeleteEnabled,
 			boolean ignoreNotFound,
 			String hqlOrderBy,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( mappings, collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		String orderBy = buildOrderByClauseFromHql( hqlOrderBy, associatedClass, collection.getRole() );
 		if ( orderBy != null ) collection.setOrderBy( orderBy );
 		if ( mappings == null ) {
 			throw new AssertionFailure(
 					"CollectionSecondPass for oneToMany should not be called with null mappings"
 			);
 		}
 		Map<String, Join> joins = mappings.getJoins( assocClass );
 		if ( associatedClass == null ) {
 			throw new MappingException(
 					"Association references unmapped class: " + assocClass
 			);
 		}
 		oneToMany.setAssociatedClass( associatedClass );
 		for (Ejb3JoinColumn column : fkJoinColumns) {
 			column.setPersistentClass( associatedClass, joins, inheritanceStatePerClass );
 			column.setJoins( joins );
 			collection.setCollectionTable( column.getTable() );
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 		}
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 
 	private void bindFilters(boolean hasAssociationTable) {
 		Filter simpleFilter = property.getAnnotation( Filter.class );
 		//set filtering
 		//test incompatible choices
 		//if ( StringHelper.isNotEmpty( where ) ) collection.setWhere( where );
 		if ( simpleFilter != null ) {
 			if ( hasAssociationTable ) {
 				collection.addManyToManyFilter( simpleFilter.name(), getCondition( simpleFilter ) );
 			}
 			else {
 				collection.addFilter( simpleFilter.name(), getCondition( simpleFilter ) );
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
 					collection.addManyToManyFilter( filter.name(), getCondition( filter ) );
 				}
 				else {
 					collection.addFilter( filter.name(), getCondition( filter ) );
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
 				collection.addFilter( simpleFilterJoinTable.name(), getCondition( simpleFilterJoinTable ) );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @FilterJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		FilterJoinTables filterJoinTables = property.getAnnotation( FilterJoinTables.class );
 		if ( filterJoinTables != null ) {
 			for (FilterJoinTable filter : filterJoinTables.value()) {
 				if ( hasAssociationTable ) {
 					collection.addFilter( filter.name(), getCondition( filter ) );
 				}
 				else {
 					throw new AnnotationException(
 							"Illegal use of @FilterJoinTable on an association without join table:"
 									+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 					);
 				}
 			}
 		}
 
 		Where where = property.getAnnotation( Where.class );
 		String whereClause = where == null ? null : where.clause();
 		if ( StringHelper.isNotEmpty( whereClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setManyToManyWhere( whereClause );
 			}
 			else {
 				collection.setWhere( whereClause );
 			}
 		}
 
 		WhereJoinTable whereJoinTable = property.getAnnotation( WhereJoinTable.class );
 		String whereJoinTableClause = whereJoinTable == null ? null : whereJoinTable.clause();
 		if ( StringHelper.isNotEmpty( whereJoinTableClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setWhere( whereJoinTableClause );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @WhereJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 //		This cannot happen in annotations since the second fetch is hardcoded to join
 //		if ( ( ! collection.getManyToManyFilterMap().isEmpty() || collection.getManyToManyWhere() != null ) &&
 //		        collection.getFetchMode() == FetchMode.JOIN &&
 //		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 //			throw new MappingException(
 //			        "association with join table  defining filter or where without join fetching " +
 //			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 //				);
 //		}
 	}
 
 	private String getCondition(FilterJoinTable filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(Filter filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(String cond, String name) {
 		if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 			cond = mappings.getFilterDefinition( name ).getDefaultFilterCondition();
 			if ( StringHelper.isEmpty( cond ) ) {
 				throw new AnnotationException(
 						"no filter condition found for filter " + name + " in "
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		return cond;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegionName = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ? null : cacheAnn.region();
 			cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy( cacheAnn.usage() );
 		}
 		else {
 			cacheConcurrencyStrategy = null;
 			cacheRegionName = null;
 		}
 	}
 
 	public void setOneToMany(boolean oneToMany) {
 		this.oneToMany = oneToMany;
 	}
 
 	public void setIndexColumn(IndexColumn indexColumn) {
 		this.indexColumn = indexColumn;
 	}
 
 	public void setMapKey(MapKey key) {
 		if ( key != null ) {
 			mapKeyPropertyName = key.name();
 		}
 	}
 
-	private static String buildOrderByClauseFromHql(String hqlOrderBy, PersistentClass associatedClass, String role) {
-		String orderByString = null;
-		if ( hqlOrderBy != null ) {
-			List<String> properties = new ArrayList<String>();
-			List<String> ordering = new ArrayList<String>();
-			StringBuilder orderByBuffer = new StringBuilder();
-			if ( hqlOrderBy.length() == 0 ) {
+	private static String buildOrderByClauseFromHql(String orderByFragment, PersistentClass associatedClass, String role) {
+		if ( orderByFragment != null ) {
+			if ( orderByFragment.length() == 0 ) {
 				//order by id
-				Iterator it = associatedClass.getIdentifier().getColumnIterator();
-				while ( it.hasNext() ) {
-					Selectable col = (Selectable) it.next();
-					orderByBuffer.append( col.getText() ).append( " asc" ).append( ", " );
-				}
+				return "id asc";
 			}
-			else {
-				StringTokenizer st = new StringTokenizer( hqlOrderBy, " ,", false );
-				String currentOrdering = null;
-				//FIXME make this code decent
-				while ( st.hasMoreTokens() ) {
-					String token = st.nextToken();
-					if ( isNonPropertyToken( token ) ) {
-						if ( currentOrdering != null ) {
-							throw new AnnotationException(
-									"Error while parsing HQL orderBy clause: " + hqlOrderBy
-											+ " (" + role + ")"
-							);
-						}
-						currentOrdering = token;
-					}
-					else {
-						//Add ordering of the previous
-						if ( currentOrdering == null ) {
-							//default ordering
-							ordering.add( "asc" );
-						}
-						else {
-							ordering.add( currentOrdering );
-							currentOrdering = null;
-						}
-						properties.add( token );
-					}
-				}
-				ordering.remove( 0 ); //first one is the algorithm starter
-				// add last one ordering
-				if ( currentOrdering == null ) {
-					//default ordering
-					ordering.add( "asc" );
-				}
-				else {
-					ordering.add( currentOrdering );
-					currentOrdering = null;
-				}
-				int index = 0;
-
-				for (String property : properties) {
-					Property p = BinderHelper.findPropertyByName( associatedClass, property );
-					if ( p == null ) {
-						throw new AnnotationException(
-								"property from @OrderBy clause not found: "
-										+ associatedClass.getEntityName() + "." + property
-						);
-					}
-					PersistentClass pc = p.getPersistentClass();
-					String table;
-					if ( pc == null ) {
-						//we are touching a @IdClass property, the pc is not set
-						//this means pc == associatedClass
-						//TODO check whether @ManyToOne @JoinTable in @IdClass used for @OrderBy works: doh!
-						table = "";
-					}
-
-					else if (pc == associatedClass
-							|| (associatedClass instanceof SingleTableSubclass && pc
-									.getMappedClass().isAssignableFrom(
-											associatedClass.getMappedClass()))) {
-						table = "";
-					} else {
-						table = pc.getTable().getQuotedName() + ".";
-					}
-
-					Iterator propertyColumns = p.getColumnIterator();
-					while ( propertyColumns.hasNext() ) {
-						Selectable column = (Selectable) propertyColumns.next();
-						orderByBuffer.append( table )
-								.append( column.getText() )
-								.append( " " )
-								.append( ordering.get( index ) )
-								.append( ", " );
-					}
-					index++;
-				}
+			else if ( "desc".equals( orderByFragment ) ) {
+				return "id desc";
 			}
-			orderByString = orderByBuffer.substring( 0, orderByBuffer.length() - 2 );
 		}
-		return orderByString;
+		return orderByFragment;
 	}
 
-	private static String buildOrderByClauseFromHql(String hqlOrderBy, Component component, String role) {
-		String orderByString = null;
-		if ( hqlOrderBy != null ) {
-			List<String> properties = new ArrayList<String>();
-			List<String> ordering = new ArrayList<String>();
-			StringBuilder orderByBuffer = new StringBuilder();
-			if ( hqlOrderBy.length() == 0 ) {
-				//TODO : Check that. Maybe order by key for maps
+	private static String adjustUserSuppliedValueCollectionOrderingFragment(String orderByFragment) {
+		if ( orderByFragment != null ) {
+			// NOTE: "$element$" is a specially recognized collection property recognized by the collection persister
+			if ( orderByFragment.length() == 0 ) {
+				//order by element
+				return "$element$ asc";
 			}
-			else {
-				StringTokenizer st = new StringTokenizer( hqlOrderBy, " ,", false );
-				String currentOrdering = null;
-				//FIXME make this code decent
-				while ( st.hasMoreTokens() ) {
-					String token = st.nextToken();
-					if ( isNonPropertyToken( token ) ) {
-						if ( currentOrdering != null ) {
-							throw new AnnotationException(
-									"Error while parsing HQL orderBy clause: " + hqlOrderBy
-											+ " (" + role + ")"
-							);
-						}
-						currentOrdering = token;
-					}
-					else {
-						//Add ordering of the previous
-						if ( currentOrdering == null ) {
-							//default ordering
-							ordering.add( "asc" );
-						}
-						else {
-							ordering.add( currentOrdering );
-							currentOrdering = null;
-						}
-						properties.add( token );
-					}
-				}
-				ordering.remove( 0 ); //first one is the algorithm starter
-				// add last one ordering
-				if ( currentOrdering == null ) {
-					//default ordering
-					ordering.add( "asc" );
-				}
-				else {
-					ordering.add( currentOrdering );
-					currentOrdering = null;
-				}
-				int index = 0;
-
-				for (String property : properties) {
-					Property p = BinderHelper.findPropertyByName( component, property );
-					if ( p == null ) {
-						throw new AnnotationException(
-								"property from @OrderBy clause not found: "
-										+ role + "." + property
-						);
-					}
-
-					Iterator propertyColumns = p.getColumnIterator();
-					while ( propertyColumns.hasNext() ) {
-						Selectable column = (Selectable) propertyColumns.next();
-						orderByBuffer.append( column.getText() )
-								.append( " " )
-								.append( ordering.get( index ) )
-								.append( ", " );
-					}
-					index++;
-				}
-
-				if ( orderByBuffer.length() >= 2 ) {
-					orderByString = orderByBuffer.substring( 0, orderByBuffer.length() - 2 );
-				}
+			else if ( "desc".equals( orderByFragment ) ) {
+				return "$element$ desc";
 			}
 		}
-		return orderByString;
-	}
-
-	private static boolean isNonPropertyToken(String token) {
-		if ( " ".equals( token ) ) return true;
-		if ( ",".equals( token ) ) return true;
-		if ( token.equalsIgnoreCase( "desc" ) ) return true;
-		if ( token.equalsIgnoreCase( "asc" ) ) return true;
-		return false;
+		return orderByFragment;
 	}
 
 	private static SimpleValue buildCollectionKey(
-			Collection collValue, Ejb3JoinColumn[] joinColumns, boolean cascadeDeleteEnabled,
-			XProperty property, Mappings mappings
-	) {
+			Collection collValue,
+			Ejb3JoinColumn[] joinColumns,
+			boolean cascadeDeleteEnabled,
+			XProperty property,
+			Mappings mappings) {
 		//binding key reference using column
 		KeyValue keyVal;
 		//give a chance to override the referenced property name
 		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
 		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
 			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
 					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
 					joinColumns[0].getPropertyHolder().getEntityName();
 			String propRef = mappings.getPropertyReferencedAssociation(
 					entityName,
 					joinColumns[0].getMappedBy()
 			);
 			if ( propRef != null ) {
 				collValue.setReferencedPropertyName( propRef );
 				mappings.addPropertyReference( collValue.getOwnerEntityName(), propRef );
 			}
 		}
 		String propRef = collValue.getReferencedPropertyName();
 		if ( propRef == null ) {
 			keyVal = collValue.getOwner().getIdentifier();
 		}
 		else {
 			keyVal = (KeyValue) collValue.getOwner()
 					.getRecursiveProperty( propRef )
 					.getValue();
 		}
 		DependantValue key = new DependantValue( mappings, collValue.getCollectionTable(), keyVal );
 		key.setTypeName( null );
 		Ejb3Column.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
 		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
 		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
 		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
 		collValue.setKey( key );
 		ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 		String fkName = fk != null ? fk.name() : "";
 		if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) key.setForeignKeyName( fkName );
 		return key;
 	}
 
 	protected void bindManyToManySecondPass(
 			Collection collValue,
 			Map persistentClasses,
 			Ejb3JoinColumn[] joinColumns,
 			Ejb3JoinColumn[] inverseJoinColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XClass collType,
 			boolean ignoreNotFound, boolean unique,
 			boolean cascadeDeleteEnabled,
 			TableBinder associationTableBinder,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			String hqlOrderBy,
 			Mappings mappings) throws MappingException {
 
 		PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
         if (LOG.isDebugEnabled()) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
             if (isCollectionOfEntities && unique) LOG.debugf("Binding a OneToMany: %s through an association table", path);
             else if (isCollectionOfEntities) LOG.debugf("Binding as ManyToMany: %s", path);
             else if (anyAnn != null) LOG.debugf("Binding a ManyToAny: %s", path);
             else LOG.debugf("Binding a collection of element: %s", path);
 		}
 		//check for user error
 		if ( !isCollectionOfEntities ) {
 			if ( property.isAnnotationPresent( ManyToMany.class ) || property.isAnnotationPresent( OneToMany.class ) ) {
 				String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 				throw new AnnotationException(
 						"Use of @OneToMany or @ManyToMany targeting an unmapped class: " + path + "[" + collType + "]"
 				);
 			}
 			else if ( anyAnn != null ) {
 				if ( parentPropertyHolder.getJoinTable( property ) == null ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"@JoinTable is mandatory when @ManyToAny is used: " + path
 					);
 				}
 			}
 			else {
 				JoinTable joinTableAnn = parentPropertyHolder.getJoinTable( property );
 				if ( joinTableAnn != null && joinTableAnn.inverseJoinColumns().length > 0 ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"Use of @JoinTable.inverseJoinColumns targeting an unmapped class: " + path + "[" + collType + "]"
 					);
 				}
 			}
 		}
 
 		boolean mappedBy = !BinderHelper.isEmptyAnnotationValue( joinColumns[0].getMappedBy() );
 		if ( mappedBy ) {
 			if ( !isCollectionOfEntities ) {
 				StringBuilder error = new StringBuilder( 80 )
 						.append(
 								"Collection of elements must not have mappedBy or association reference an unmapped entity: "
 						)
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Property otherSideProperty;
 			try {
 				otherSideProperty = collectionEntity.getRecursiveProperty( joinColumns[0].getMappedBy() );
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( joinColumns[0].getMappedBy() )
 						.append( " in " )
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Table table;
 			if ( otherSideProperty.getValue() instanceof Collection ) {
 				//this is a collection on the other side
 				table = ( (Collection) otherSideProperty.getValue() ).getCollectionTable();
 			}
 			else {
 				//This is a ToOne with a @JoinTable or a regular property
 				table = otherSideProperty.getValue().getTable();
 			}
 			collValue.setCollectionTable( table );
 			String entityName = collectionEntity.getEntityName();
 			for (Ejb3JoinColumn column : joinColumns) {
 				//column.setDefaultColumnHeader( joinColumns[0].getMappedBy() ); //seems not to be used, make sense
 				column.setManyToManyOwnerSideEntityName( entityName );
 			}
 		}
 		else {
 			//TODO: only for implicit columns?
 			//FIXME NamingStrategy
 			for (Ejb3JoinColumn column : joinColumns) {
 				String mappedByProperty = mappings.getFromMappedBy(
 						collValue.getOwnerEntityName(), column.getPropertyName()
 				);
 				Table ownerTable = collValue.getOwner().getTable();
 				column.setMappedBy(
 						collValue.getOwner().getEntityName(), mappings.getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getEntityName(),
 						mappings.getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
 						collectionEntity != null ? mappings.getLogicalTableName( collectionEntity.getTable() ) : null,
 						joinColumns[0].getPropertyName()
 				);
 			}
 			associationTableBinder.setJPA2ElementCollection( !isCollectionOfEntities && property.isAnnotationPresent( ElementCollection.class ));
 			collValue.setCollectionTable( associationTableBinder.bind() );
 		}
 		bindFilters( isCollectionOfEntities );
 		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, mappings );
 
 		ManyToOne element = null;
 		if ( isCollectionOfEntities ) {
 			element =
 					new ManyToOne( mappings,  collValue.getCollectionTable() );
 			collValue.setElement( element );
 			element.setReferencedEntityName( collType.getName() );
 			//element.setFetchMode( fetchMode );
 			//element.setLazy( fetchMode != FetchMode.JOIN );
 			//make the second join non lazy
 			element.setFetchMode( FetchMode.JOIN );
 			element.setLazy( false );
 			element.setIgnoreNotFound( ignoreNotFound );
 			// as per 11.1.38 of JPA-2 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 			ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 			String fkName = fk != null ? fk.inverseName() : "";
 			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) element.setForeignKeyName( fkName );
 		}
 		else if ( anyAnn != null ) {
 			//@ManyToAny
 			//Make sure that collTyp is never used during the @ManyToAny branch: it will be set to void.class
 			PropertyData inferredData = new PropertyInferredData(null, property, "unsupported", mappings.getReflectionManager() );
 			//override the table
 			for (Ejb3Column column : inverseJoinColumns) {
 				column.setTable( collValue.getCollectionTable() );
 			}
 			Any any = BinderHelper.buildAnyValue( anyAnn.metaDef(), inverseJoinColumns, anyAnn.metaColumn(),
 					inferredData, cascadeDeleteEnabled, Nullability.NO_CONSTRAINT,
 					propertyHolder, new EntityBinder(), true, mappings );
 			collValue.setElement( any );
 		}
 		else {
 			XClass elementClass;
 			AnnotatedClassType classType;
 
 			PropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 			}
 			else {
 				elementClass = collType;
 				classType = mappings.getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property, parentPropertyHolder, mappings
 				);
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
 				if ( isEmbedded || attributeOverride ) {
 					classType = AnnotatedClassType.EMBEDDABLE;
 				}
 			}
 
 			if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 				EntityBinder entityBinder = new EntityBinder();
 				PersistentClass owner = collValue.getOwner();
 				boolean isPropertyAnnotated;
 				//FIXME support @Access for collection of elements
 				//String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" );
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					isPropertyAnnotated = prop.getPropertyAccessorName().equals( "property" );
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				PropertyData inferredData;
 				if ( isMap() ) {
 					//"value" is the JPA 2 prefix for map values (used to be "element")
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "value", elementClass );
 					}
 				}
 				else {
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						//"collection&&element" is not a valid property name => placeholder
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "collection&&element", elementClass );
 					}
 				}
 				//TODO be smart with isNullable
 				Component component = AnnotationBinder.fillComponent(
 						holder, inferredData, isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD, true,
 						entityBinder, false, false,
 						true, mappings, inheritanceStatePerClass
 				);
 
 				collValue.setElement( component );
 
 				if ( StringHelper.isNotEmpty( hqlOrderBy ) ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
-					String orderBy = buildOrderByClauseFromHql( hqlOrderBy, component, path );
+					String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 					if ( orderBy != null ) {
 						collValue.setOrderBy( orderBy );
 					}
 				}
 			}
 			else {
 				SimpleValueBinder elementBinder = new SimpleValueBinder();
 				elementBinder.setMappings( mappings );
 				elementBinder.setReturnedClassName( collType.getName() );
 				if ( elementColumns == null || elementColumns.length == 0 ) {
 					elementColumns = new Ejb3Column[1];
 					Ejb3Column column = new Ejb3Column();
 					column.setImplicit( false );
 					//not following the spec but more clean
 					column.setNullable( true );
 					column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 					column.setLogicalColumnName( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
 					//TODO create an EMPTY_JOINS collection
 					column.setJoins( new HashMap<String, Join>() );
 					column.setMappings( mappings );
 					column.bind();
 					elementColumns[0] = column;
 				}
 				//override the table
 				for (Ejb3Column column : elementColumns) {
 					column.setTable( collValue.getCollectionTable() );
 				}
 				elementBinder.setColumns( elementColumns );
 				elementBinder.setType( property, elementClass );
 				collValue.setElement( elementBinder.make() );
+				String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
+				if ( orderBy != null ) {
+					collValue.setOrderBy( orderBy );
+				}
 			}
 		}
 
 		checkFilterConditions( collValue );
 
 		//FIXME: do optional = false
 		if ( isCollectionOfEntities ) {
 			bindManytoManyInverseFk( collectionEntity, inverseJoinColumns, element, unique, mappings );
 		}
 
 	}
 
 	private static void checkFilterConditions(Collection collValue) {
 		//for now it can't happen, but sometime soon...
 		if ( ( collValue.getFilterMap().size() != 0 || StringHelper.isNotEmpty( collValue.getWhere() ) ) &&
 				collValue.getFetchMode() == FetchMode.JOIN &&
 				!( collValue.getElement() instanceof SimpleValue ) && //SimpleValue (CollectionOfElements) are always SELECT but it does not matter
 				collValue.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 					"@ManyToMany or @CollectionOfElements defining filter or where without join fetching "
 							+ "not valid within collection using join fetching[" + collValue.getRole() + "]"
 			);
 		}
 	}
 
 	private static void bindCollectionSecondPass(
 			Collection collValue,
 			PersistentClass collectionEntity,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			Mappings mappings) {
 		BinderHelper.createSyntheticPropertyReference(
 				joinColumns, collValue.getOwner(), collectionEntity, collValue, false, mappings
 		);
 		SimpleValue key = buildCollectionKey( collValue, joinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( property.isAnnotationPresent( ElementCollection.class ) && joinColumns.length > 0 ) {
 			joinColumns[0].setJPA2ElementCollection( true );
 		}
 		TableBinder.bindFk( collValue.getOwner(), collectionEntity, joinColumns, key, false, mappings );
 	}
 
 	public void setCascadeDeleteEnabled(boolean onDeleteCascade) {
 		this.cascadeDeleteEnabled = onDeleteCascade;
 	}
 
 	private String safeCollectionRole() {
 		if ( propertyHolder != null ) {
 			return propertyHolder.getEntityName() + "." + propertyName;
 		}
 		else {
 			return "";
 		}
 	}
 
 
 	/**
 	 * bind the inverse FK of a ManyToMany
 	 * If we are in a mappedBy case, read the columns from the associated
 	 * collection element
 	 * Otherwise delegates to the usual algorithm
 	 */
 	public static void bindManytoManyInverseFk(
 			PersistentClass referencedEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			Mappings mappings) {
 		final String mappedBy = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedBy ) ) {
 			final Property property = referencedEntity.getRecursiveProperty( mappedBy );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				mappedByColumns = ( (Collection) property.getValue() ).getKey().getColumnIterator();
 			}
 			else {
 				//find the appropriate reference key, can be in a join
 				Iterator joinsIt = referencedEntity.getJoinIterator();
 				KeyValue key = null;
 				while ( joinsIt.hasNext() ) {
 					Join join = (Join) joinsIt.next();
 					if ( join.containsProperty( property ) ) {
 						key = join.getKey();
 						break;
 					}
 				}
 				if ( key == null ) key = property.getPersistentClass().getIdentifier();
 				mappedByColumns = key.getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 			String referencedPropertyName =
 					mappings.getPropertyReferencedAssociation(
 							"inverse__" + referencedEntity.getEntityName(), mappedBy
 					);
 			if ( referencedPropertyName != null ) {
 				//TODO always a many to one?
 				( (ManyToOne) value ).setReferencedPropertyName( referencedPropertyName );
 				mappings.addUniquePropertyReference( referencedEntity.getEntityName(), referencedPropertyName );
 			}
 			value.createForeignKey();
 		}
 		else {
 			BinderHelper.createSyntheticPropertyReference( columns, referencedEntity, null, value, true, mappings );
 			TableBinder.bindFk( referencedEntity, null, columns, value, unique, mappings );
 		}
 	}
 
 	public void setFkJoinColumns(Ejb3JoinColumn[] ejb3JoinColumns) {
 		this.fkJoinColumns = ejb3JoinColumns;
 	}
 
 	public void setExplicitAssociationTable(boolean explicitAssocTable) {
 		this.isExplicitAssociationTable = explicitAssocTable;
 	}
 
 	public void setElementColumns(Ejb3Column[] elementColumns) {
 		this.elementColumns = elementColumns;
 	}
 
 	public void setEmbedded(boolean annotationPresent) {
 		this.isEmbedded = annotationPresent;
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 	public void setMapKeyColumns(Ejb3Column[] mapKeyColumns) {
 		this.mapKeyColumns = mapKeyColumns;
 	}
 
 	public void setMapKeyManyToManyColumns(Ejb3JoinColumn[] mapJoinColumns) {
 		this.mapKeyManyToManyColumns = mapJoinColumns;
 	}
 
 	public void setLocalGenerators(HashMap<String, IdGenerator> localGenerators) {
 		this.localGenerators = localGenerators;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
index 9229b2a4a0..ea0ce79ae6 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
@@ -1,664 +1,673 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.EmbeddedComponentType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default load event listeners used by hibernate for loading entities
  * in response to generated load events.
  *
  * @author Steve Ebersole
  */
 public class DefaultLoadEventListener extends AbstractLockUpgradeEventListener implements LoadEventListener {
 
 	public static final Object REMOVED_ENTITY_MARKER = new Object();
 	public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
 	public static final LockMode DEFAULT_LOCK_MODE = LockMode.NONE;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultLoadEventListener.class.getName());
 
 
 	/**
 	 * Handle the given load event.
 	 *
 	 * @param event The load event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onLoad(LoadEvent event, LoadEventListener.LoadType loadType) throws HibernateException {
 
 		final SessionImplementor source = event.getSession();
 
 		EntityPersister persister;
 		if ( event.getInstanceToLoad() != null ) {
 			persister = source.getEntityPersister( null, event.getInstanceToLoad() ); //the load() which takes an entity does not pass an entityName
 			event.setEntityClassName( event.getInstanceToLoad().getClass().getName() );
 		}
 		else {
 			persister = source.getFactory().getEntityPersister( event.getEntityClassName() );
 		}
 
 		if ( persister == null ) {
 			throw new HibernateException(
 					"Unable to locate persister: " +
 					event.getEntityClassName()
 				);
 		}
 
 		final Class idClass = persister.getIdentifierType().getReturnedClass();
 		if ( idClass != null && ! idClass.isInstance( event.getEntityId() ) ) {
 			// we may have the kooky jpa requirement of allowing find-by-id where
 			// "id" is the "simple pk value" of a dependent objects parent.  This
 			// is part of its generally goofy "derived identity" "feature"
 			if ( persister.getEntityMetamodel().getIdentifierProperty().isEmbedded() ) {
 				final EmbeddedComponentType dependentIdType =
 						(EmbeddedComponentType) persister.getEntityMetamodel().getIdentifierProperty().getType();
 				if ( dependentIdType.getSubtypes().length == 1 ) {
 					final Type singleSubType = dependentIdType.getSubtypes()[0];
 					if ( singleSubType.isEntityType() ) {
 						final EntityType dependentParentType = (EntityType) singleSubType;
 						final Type dependentParentIdType = dependentParentType.getIdentifierOrUniqueKeyType( source.getFactory() );
 						if ( dependentParentIdType.getReturnedClass().isInstance( event.getEntityId() ) ) {
 							// yep that's what we have...
 							loadByDerivedIdentitySimplePkValue(
 									event,
 									loadType,
 									persister,
 									dependentIdType,
 									source.getFactory().getEntityPersister( dependentParentType.getAssociatedEntityName() )
 							);
 							return;
 						}
 					}
 				}
 			}
 			throw new TypeMismatchException(
 					"Provided id of the wrong type for class " + persister.getEntityName() + ". Expected: " + idClass + ", got " + event.getEntityId().getClass()
 			);
 		}
 
 		final  EntityKey keyToLoad = source.generateEntityKey( event.getEntityId(), persister );
 
 		try {
 			if ( loadType.isNakedEntityReturned() ) {
 				//do not return a proxy!
 				//(this option indicates we are initializing a proxy)
 				event.setResult( load(event, persister, keyToLoad, loadType) );
 			}
 			else {
 				//return a proxy if appropriate
 				if ( event.getLockMode() == LockMode.NONE ) {
 					event.setResult( proxyOrLoad(event, persister, keyToLoad, loadType) );
 				}
 				else {
 					event.setResult( lockAndLoad(event, persister, keyToLoad, loadType, source) );
 				}
 			}
 		}
 		catch(HibernateException e) {
 			LOG.unableToLoadCommand( e );
 			throw e;
 		}
 	}
 
 	private void loadByDerivedIdentitySimplePkValue(
 			LoadEvent event,
 			LoadEventListener.LoadType options,
 			EntityPersister dependentPersister,
 			EmbeddedComponentType dependentIdType,
 			EntityPersister parentPersister) {
 		final EntityKey parentEntityKey = event.getSession().generateEntityKey( event.getEntityId(), parentPersister );
 		final Object parent = doLoad( event, parentPersister, parentEntityKey, options );
 
 		final Serializable dependent = (Serializable) dependentIdType.instantiate( parent, event.getSession() );
 		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, dependentPersister.getEntityMode() );
 		final EntityKey dependentEntityKey = event.getSession().generateEntityKey( dependent, dependentPersister );
 		event.setEntityId( dependent );
 
 		event.setResult( doLoad( event, dependentPersister, dependentEntityKey, options ) );
 	}
 
 	/**
 	 * Performs the load of an entity.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @return The loaded entity.
 	 * @throws HibernateException
 	 */
 	protected Object load(
 		final LoadEvent event,
 		final EntityPersister persister,
 		final EntityKey keyToLoad,
 		final LoadEventListener.LoadType options) {
 
 		if ( event.getInstanceToLoad() != null ) {
 			if ( event.getSession().getPersistenceContext().getEntry( event.getInstanceToLoad() ) != null ) {
 				throw new PersistentObjectException(
 						"attempted to load into an instance that was already associated with the session: " +
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
 					);
 			}
 			persister.setIdentifier( event.getInstanceToLoad(), event.getEntityId(), event.getSession() );
 		}
 
 		Object entity = doLoad(event, persister, keyToLoad, options);
 
 		boolean isOptionalInstance = event.getInstanceToLoad() != null;
 
 		if ( !options.isAllowNulls() || isOptionalInstance ) {
 			if ( entity == null ) {
 				event.getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( event.getEntityClassName(), event.getEntityId() );
 			}
 		}
 
 		if ( isOptionalInstance && entity != event.getInstanceToLoad() ) {
 			throw new NonUniqueObjectException( event.getEntityId(), event.getEntityClassName() );
 		}
 
 		return entity;
 	}
 
 	/**
 	 * Based on configured options, will either return a pre-existing proxy,
 	 * generate a new proxy, or perform an actual load.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @return The result of the proxy/load operation.
 	 */
 	protected Object proxyOrLoad(
 		final LoadEvent event,
 		final EntityPersister persister,
 		final EntityKey keyToLoad,
 		final LoadEventListener.LoadType options) {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Loading entity: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 		}
 
         // this class has no proxies (so do a shortcut)
-        if (!persister.hasProxy()) return load(event, persister, keyToLoad, options);
+        if (!persister.hasProxy()) {
+			return load(event, persister, keyToLoad, options);
+		}
+
         final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
 
 		// look for a proxy
         Object proxy = persistenceContext.getProxy(keyToLoad);
-        if (proxy != null) return returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
-        if (options.isAllowProxyCreation()) return createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
+        if (proxy != null) {
+			return returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
+		}
+
+        if (options.isAllowProxyCreation()) {
+			return createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
+		}
+
         // return a newly loaded object
         return load(event, persister, keyToLoad, options);
 	}
 
 	/**
 	 * Given a proxy, initialize it and/or narrow it provided either
 	 * is necessary.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @param persistenceContext The originating session
 	 * @param proxy The proxy to narrow
 	 * @return The created/existing proxy
 	 */
 	private Object returnNarrowedProxy(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final PersistenceContext persistenceContext,
 			final Object proxy) {
 		LOG.trace( "Entity proxy found in session cache" );
 		LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 		if ( li.isUnwrap() ) {
 			return li.getImplementation();
 		}
 		Object impl = null;
 		if ( !options.isAllowProxyCreation() ) {
 			impl = load( event, persister, keyToLoad, options );
 			if ( impl == null ) {
 				event.getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( persister.getEntityName(), keyToLoad.getIdentifier());
 			}
 		}
 		return persistenceContext.narrowProxy( proxy, persister, keyToLoad, impl );
 	}
 
 	/**
 	 * If there is already a corresponding proxy associated with the
 	 * persistence context, return it; otherwise create a proxy, associate it
 	 * with the persistence context, and return the just-created proxy.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @param persistenceContext The originating session
 	 * @return The created/existing proxy
 	 */
 	private Object createProxyIfNecessary(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final PersistenceContext persistenceContext) {
 		Object existing = persistenceContext.getEntity( keyToLoad );
 		if ( existing != null ) {
 			// return existing object or initialized proxy (unless deleted)
 			LOG.trace( "Entity found in session cache" );
 			if ( options.isCheckDeleted() ) {
 				EntityEntry entry = persistenceContext.getEntry( existing );
 				Status status = entry.getStatus();
 				if ( status == Status.DELETED || status == Status.GONE ) {
 					return null;
 				}
 			}
 			return existing;
 		}
 		LOG.trace( "Creating new proxy for entity" );
 		// return new uninitialized proxy
 		Object proxy = persister.createProxy( event.getEntityId(), event.getSession() );
 		persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey( keyToLoad );
 		persistenceContext.addProxy( keyToLoad, proxy );
 		return proxy;
 	}
 
 	/**
 	 * If the class to be loaded has been configured with a cache, then lock
 	 * given id in that cache and then perform the load.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @param source The originating session
 	 * @return The loaded entity
 	 * @throws HibernateException
 	 */
 	protected Object lockAndLoad(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final SessionImplementor source) {
 		SoftLock lock = null;
 		final CacheKey ck;
 		if ( persister.hasCache() ) {
 			ck = source.generateCacheKey(
 					event.getEntityId(),
 					persister.getIdentifierType(),
 					persister.getRootEntityName()
 			);
 			lock = persister.getCacheAccessStrategy().lockItem( ck, null );
 		}
 		else {
 			ck = null;
 		}
 
 		Object entity;
 		try {
 			entity = load(event, persister, keyToLoad, options);
 		}
 		finally {
 			if ( persister.hasCache() ) {
 				persister.getCacheAccessStrategy().unlockItem( ck, lock );
 			}
 		}
 
 		return event.getSession().getPersistenceContext().proxyFor( persister, keyToLoad, entity );
 	}
 
 
 	/**
 	 * Coordinates the efforts to load a given entity.  First, an attempt is
 	 * made to load the entity from the session-level cache.  If not found there,
 	 * an attempt is made to locate it in second-level cache.  Lastly, an
 	 * attempt is made to load it directly from the datasource.
 	 *
 	 * @param event The load event
 	 * @param persister The persister for the entity being requested for load
 	 * @param keyToLoad The EntityKey representing the entity to be loaded.
 	 * @param options The load options.
 	 * @return The loaded entity, or null.
 	 */
 	protected Object doLoad(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) {
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled ) LOG.tracev( "Attempting to resolve: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 
 		Object entity = loadFromSessionCache( event, keyToLoad, options );
 		if ( entity == REMOVED_ENTITY_MARKER ) {
 			LOG.debug( "Load request found matching entity in context, but it is scheduled for removal; returning null" );
 			return null;
 		}
 		if ( entity == INCONSISTENT_RTN_CLASS_MARKER ) {
 			LOG.debug( "Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null" );
 			return null;
 		}
 		if ( entity != null ) {
 			if (traceEnabled) LOG.tracev("Resolved object in session cache: {0}",
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 			return entity;
 		}
 
 		entity = loadFromSecondLevelCache(event, persister, options);
 		if ( entity != null ) {
 			if ( traceEnabled ) LOG.tracev( "Resolved object in second-level cache: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 		}
 		else {
 			if ( traceEnabled ) LOG.tracev( "Object not resolved in any cache: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 			entity = loadFromDatasource(event, persister, keyToLoad, options);
 		}
 		
 		if (entity != null && persister.hasNaturalIdentifier()) {
 			event.getSession().getPersistenceContext().getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					event.getEntityId(),
 					event.getSession().getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( entity, persister )
 			);
 		}
 
 
 		return entity;
 	}
 
 	/**
 	 * Performs the process of loading an entity from the configured
 	 * underlying datasource.
 	 *
 	 * @param event The load event
 	 * @param persister The persister for the entity being requested for load
 	 * @param keyToLoad The EntityKey representing the entity to be loaded.
 	 * @param options The load options.
 	 * @return The object loaded from the datasource, or null if not found.
 	 */
 	protected Object loadFromDatasource(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) {
 		final SessionImplementor source = event.getSession();
 		Object entity = persister.load(
 				event.getEntityId(),
 				event.getInstanceToLoad(),
 				event.getLockOptions(),
 				source
 		);
 		
 		if ( event.isAssociationFetch() && source.getFactory().getStatistics().isStatisticsEnabled() ) {
 			source.getFactory().getStatisticsImplementor().fetchEntity( event.getEntityClassName() );
 		}
 
 		return entity;
 	}
 
 	/**
 	 * Attempts to locate the entity in the session-level cache.
 	 * <p/>
 	 * If allowed to return nulls, then if the entity happens to be found in
 	 * the session cache, we check the entity type for proper handling
 	 * of entity hierarchies.
 	 * <p/>
 	 * If checkDeleted was set to true, then if the entity is found in the
 	 * session-level cache, it's current status within the session cache
 	 * is checked to see if it has previously been scheduled for deletion.
 	 *
 	 * @param event The load event
 	 * @param keyToLoad The EntityKey representing the entity to be loaded.
 	 * @param options The load options.
 	 * @return The entity from the session-level cache, or null.
 	 * @throws HibernateException Generally indicates problems applying a lock-mode.
 	 */
 	protected Object loadFromSessionCache(
 			final LoadEvent event,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) throws HibernateException {
 
 		SessionImplementor session = event.getSession();
 		Object old = session.getEntityUsingInterceptor( keyToLoad );
 
 		if ( old != null ) {
 			// this object was already loaded
 			EntityEntry oldEntry = session.getPersistenceContext().getEntry( old );
 			if ( options.isCheckDeleted() ) {
 				Status status = oldEntry.getStatus();
 				if ( status == Status.DELETED || status == Status.GONE ) {
 					return REMOVED_ENTITY_MARKER;
 				}
 			}
 			if ( options.isAllowNulls() ) {
 				final EntityPersister persister = event.getSession().getFactory().getEntityPersister( keyToLoad.getEntityName() );
 				if ( ! persister.isInstance( old ) ) {
 					return INCONSISTENT_RTN_CLASS_MARKER;
 				}
 			}
 			upgradeLock( old, oldEntry, event.getLockOptions(), event.getSession() );
 		}
 
 		return old;
 	}
 
 	/**
 	 * Attempts to load the entity from the second-level cache.
 	 *
 	 * @param event The load event
 	 * @param persister The persister for the entity being requested for load
 	 * @param options The load options.
 	 * @return The entity from the second-level cache, or null.
 	 */
 	protected Object loadFromSecondLevelCache(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final LoadEventListener.LoadType options) {
 
 		final SessionImplementor source = event.getSession();
 
 		final boolean useCache = persister.hasCache()
 				&& source.getCacheMode().isGetEnabled()
 				&& event.getLockMode().lessThan(LockMode.READ);
 
 		if ( useCache ) {
 
 			final SessionFactoryImplementor factory = source.getFactory();
 
 			final CacheKey ck = source.generateCacheKey(
 					event.getEntityId(),
 					persister.getIdentifierType(),
 					persister.getRootEntityName()
 			);
 			Object ce = persister.getCacheAccessStrategy().get( ck, source.getTimestamp() );
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				if ( ce == null ) {
 					factory.getStatisticsImplementor().secondLevelCacheMiss(
 							persister.getCacheAccessStrategy().getRegion().getName()
 					);
 				}
 				else {
 					factory.getStatisticsImplementor().secondLevelCacheHit(
 							persister.getCacheAccessStrategy().getRegion().getName()
 					);
 				}
 			}
 
 			if ( ce != null ) {
 				CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( ce, factory );
 
 				// Entity was found in second-level cache...
 				return assembleCacheEntry(
 						entry,
 						event.getEntityId(),
 						persister,
 						event
 				);
 			}
 		}
 
 		return null;
 	}
 
 	private Object assembleCacheEntry(
 			final CacheEntry entry,
 			final Serializable id,
 			final EntityPersister persister,
 			final LoadEvent event) throws HibernateException {
 
 		final Object optionalObject = event.getInstanceToLoad();
 		final EventSource session = event.getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Assembling entity from second-level cache: {0}",
 					MessageHelper.infoString( persister, id, factory ) );
 		}
 
 		EntityPersister subclassPersister = factory.getEntityPersister( entry.getSubclass() );
 		Object result = optionalObject == null ?
 				session.instantiate( subclassPersister, id ) : optionalObject;
 
 		// make it circular-reference safe
 		final EntityKey entityKey = session.generateEntityKey( id, subclassPersister );
 		TwoPhaseLoad.addUninitializedCachedEntity(
 				entityKey,
 				result,
 				subclassPersister,
 				LockMode.NONE,
 				entry.areLazyPropertiesUnfetched(),
 				entry.getVersion(),
 				session
 			);
 
 		Type[] types = subclassPersister.getPropertyTypes();
 		Object[] values = entry.assemble( result, id, subclassPersister, session.getInterceptor(), session ); // intializes result by side-effect
 		TypeHelper.deepCopy(
 				values,
 				types,
 				subclassPersister.getPropertyUpdateability(),
 				values,
 				session
 		);
 
 		Object version = Versioning.getVersion( values, subclassPersister );
 		LOG.tracev( "Cached Version: {0}", version );
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		boolean isReadOnly = session.isDefaultReadOnly();
 		if ( persister.isMutable() ) {
 			Object proxy = persistenceContext.getProxy( entityKey );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		else {
 			isReadOnly = true;
 		}
 		persistenceContext.addEntry(
 				result,
 				( isReadOnly ? Status.READ_ONLY : Status.MANAGED ),
 				values,
 				null,
 				id,
 				version,
 				LockMode.NONE,
 				true,
 				subclassPersister,
 				false,
 				entry.areLazyPropertiesUnfetched()
 			);
 		subclassPersister.afterInitialize( result, entry.areLazyPropertiesUnfetched(), session );
 		persistenceContext.initializeNonLazyCollections();
 		// upgrade the lock if necessary:
 		//lock(result, lockMode);
 
 		//PostLoad is needed for EJB3
 		//TODO: reuse the PostLoadEvent...
 		PostLoadEvent postLoadEvent = new PostLoadEvent( session )
 				.setEntity( result )
 				.setId( id )
 				.setPersister( persister );
 
 		for ( PostLoadEventListener listener : postLoadEventListeners( session ) ) {
 			listener.onPostLoad( postLoadEvent );
 		}
 
 		return result;
 	}
 
 	private Iterable<PostLoadEventListener> postLoadEventListeners(EventSource session) {
 		return session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.POST_LOAD )
 				.listeners();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
index b14b98ef04..de6d11ed3e 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/JoinWalker.java
@@ -1,1089 +1,1089 @@
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
 package org.hibernate.loader;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.FetchMode;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.sql.ConditionFragment;
 import org.hibernate.sql.DisjunctionFragment;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 /**
  * Walks the metamodel, searching for joins, and collecting
  * together information needed by <tt>OuterJoinLoader</tt>.
  * 
  * @see OuterJoinLoader
  * @author Gavin King, Jon Lipsky
  */
 public class JoinWalker {
 	
 	private final SessionFactoryImplementor factory;
 	protected final List associations = new ArrayList();
 	private final Set visitedAssociationKeys = new HashSet();
 	private final LoadQueryInfluencers loadQueryInfluencers;
 
 	protected String[] suffixes;
 	protected String[] collectionSuffixes;
 	protected Loadable[] persisters;
 	protected int[] owners;
 	protected EntityType[] ownerAssociationTypes;
 	protected CollectionPersister[] collectionPersisters;
 	protected int[] collectionOwners;
 	protected String[] aliases;
 	protected LockOptions lockOptions;
 	protected LockMode[] lockModeArray;
 	protected String sql;
 
 	protected JoinWalker(
 			SessionFactoryImplementor factory,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		this.factory = factory;
 		this.loadQueryInfluencers = loadQueryInfluencers;
 
 	}
 
 
 	public String[] getCollectionSuffixes() {
 		return collectionSuffixes;
 	}
 
 	public void setCollectionSuffixes(String[] collectionSuffixes) {
 		this.collectionSuffixes = collectionSuffixes;
 	}
 
 	public LockOptions getLockModeOptions() {
 		return lockOptions;
 	}
 
 	public LockMode[] getLockModeArray() {
 		return lockModeArray;
 	}
 
 	public String[] getSuffixes() {
 		return suffixes;
 	}
 
 	public void setSuffixes(String[] suffixes) {
 		this.suffixes = suffixes;
 	}
 
 	public String[] getAliases() {
 		return aliases;
 	}
 
 	public void setAliases(String[] aliases) {
 		this.aliases = aliases;
 	}
 
 	public int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 
 	public void setCollectionOwners(int[] collectionOwners) {
 		this.collectionOwners = collectionOwners;
 	}
 
 	public CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	public void setCollectionPersisters(CollectionPersister[] collectionPersisters) {
 		this.collectionPersisters = collectionPersisters;
 	}
 
 	public EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	public void setOwnerAssociationTypes(EntityType[] ownerAssociationType) {
 		this.ownerAssociationTypes = ownerAssociationType;
 	}
 
 	public int[] getOwners() {
 		return owners;
 	}
 
 	public void setOwners(int[] owners) {
 		this.owners = owners;
 	}
 
 	public Loadable[] getPersisters() {
 		return persisters;
 	}
 
 	public void setPersisters(Loadable[] persisters) {
 		this.persisters = persisters;
 	}
 
 	public String getSQLString() {
 		return sql;
 	}
 
 	public void setSql(String sql) {
 		this.sql = sql;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected Dialect getDialect() {
 		return factory.getDialect();
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	/**
 	 * Add on association (one-to-one, many-to-one, or a collection) to a list 
 	 * of associations to be fetched by outerjoin (if necessary)
 	 */
 	private void addAssociationToJoinTreeIfNecessary(
 			final AssociationType type,
 			final String[] aliasedLhsColumns,
 			final String alias,
 			final PropertyPath path,
 			int currentDepth,
 			final JoinType joinType) throws MappingException {
 		if ( joinType != JoinType.NONE ) {
 			addAssociationToJoinTree(
 					type, 
 					aliasedLhsColumns, 
 					alias, 
 					path,
 					currentDepth,
 					joinType
 			);
 		}
 	}
 
 	protected boolean hasRestriction(PropertyPath path)	{
 		return false;
 	}
 
 	protected String getWithClause(PropertyPath path)	{
 		return "";
 	}
 	
 	/**
 	 * Add on association (one-to-one, many-to-one, or a collection) to a list 
 	 * of associations to be fetched by outerjoin 
 	 */
 	private void addAssociationToJoinTree(
 			final AssociationType type,
 			final String[] aliasedLhsColumns,
 			final String alias,
 			final PropertyPath path,
 			final int currentDepth,
 			final JoinType joinType) throws MappingException {
 
 		Joinable joinable = type.getAssociatedJoinable( getFactory() );
 
 		// important to generate alias based on size of association collection
 		// *before* adding this join to that collection
 		String subalias = generateTableAlias( associations.size() + 1, path, joinable );
 
 		// NOTE : it should be fine to continue to pass only filters below
 		// (instead of LoadQueryInfluencers) since "from that point on" we
 		// only need to worry about restrictions (and not say adding more
 		// joins)
 		OuterJoinableAssociation assoc = new OuterJoinableAssociation(
 				path,
 				type, 
 				alias, 
 				aliasedLhsColumns, 
 				subalias, 
 				joinType, 
 				getWithClause(path),
 				hasRestriction( path ),
 				getFactory(),
 				loadQueryInfluencers.getEnabledFilters()
 		);
 		assoc.validateJoin( path.getFullPath() );
 		associations.add( assoc );
 
 		int nextDepth = currentDepth + 1;
 //		path = "";
 		if ( !joinable.isCollection() ) {
 			if (joinable instanceof OuterJoinLoadable) {
 				walkEntityTree(
 					(OuterJoinLoadable) joinable, 
 					subalias,
 					path, 
 					nextDepth
 				);
 			}
 		}
 		else {
 			if (joinable instanceof QueryableCollection) {
 				walkCollectionTree(
 					(QueryableCollection) joinable, 
 					subalias, 
 					path, 
 					nextDepth
 				);
 			}
 		}
 
 	}
 
 	/**
 	 * Walk the association tree for an entity, adding associations which should
 	 * be join fetched to the {@link #associations} inst var.  This form is the
 	 * entry point into the walking for a given entity, starting the recursive
 	 * calls into {@link #walkEntityTree(org.hibernate.persister.entity.OuterJoinLoadable, String, PropertyPath ,int)}.
 	 *
 	 * @param persister The persister representing the entity to be walked.
 	 * @param alias The (root) alias to use for this entity/persister.
 	 * @throws org.hibernate.MappingException ???
 	 */
 	protected final void walkEntityTree(
 			OuterJoinLoadable persister,
 			String alias) throws MappingException {
 		walkEntityTree( persister, alias, new PropertyPath(), 0 );
 	}
 
 	/**
 	 * For a collection role, return a list of associations to be fetched by outerjoin
 	 */
 	protected final void walkCollectionTree(QueryableCollection persister, String alias) throws MappingException {
 		walkCollectionTree( persister, alias, new PropertyPath(), 0 );
 		//TODO: when this is the entry point, we should use an INNER_JOIN for fetching the many-to-many elements!
 	}
 
 	/**
 	 * For a collection role, return a list of associations to be fetched by outerjoin
 	 */
 	private void walkCollectionTree(
 			final QueryableCollection persister,
 			final String alias,
 			final PropertyPath path,
 			final int currentDepth)	throws MappingException {
 
 		if ( persister.isOneToMany() ) {
 			walkEntityTree(
 					(OuterJoinLoadable) persister.getElementPersister(),
 					alias,
 					path,
 					currentDepth
 				);
 		}
 		else {
 			Type type = persister.getElementType();
 			if ( type.isAssociationType() ) {
 				// a many-to-many;
 				// decrement currentDepth here to allow join across the association table
 				// without exceeding MAX_FETCH_DEPTH (i.e. the "currentDepth - 1" bit)
 				AssociationType associationType = (AssociationType) type;
 				String[] aliasedLhsColumns = persister.getElementColumnNames(alias);
 				String[] lhsColumns = persister.getElementColumnNames();
 				// if the current depth is 0, the root thing being loaded is the
 				// many-to-many collection itself.  Here, it is alright to use
 				// an inner join...
 				boolean useInnerJoin = currentDepth == 0;
 				final JoinType joinType = getJoinType(
 						associationType,
 						persister.getFetchMode(),
 						path,
 						persister.getTableName(),
 						lhsColumns,
 						!useInnerJoin,
 						currentDepth - 1, 
 						null //operations which cascade as far as the collection also cascade to collection elements
 				);
 				addAssociationToJoinTreeIfNecessary(
 						associationType,
 						aliasedLhsColumns,
 						alias,
 						path,
 						currentDepth - 1,
 						joinType
 					);
 			}
 			else if ( type.isComponentType() ) {
 				walkCompositeElementTree(
 						(CompositeType) type,
 						persister.getElementColumnNames(),
 						persister,
 						alias,
 						path,
 						currentDepth
 					);
 			}
 		}
 
 	}
 	
 	/**
 	 * Process a particular association owned by the entity
 	 *
 	 * @param associationType The type representing the association to be
 	 * processed.
 	 * @param persister The owner of the association to be processed.
 	 * @param propertyNumber The property number for the association
 	 * (relative to the persister).
 	 * @param alias The entity alias
 	 * @param path The path to the association
 	 * @param nullable is the association nullable (which I think is supposed
 	 * to indicate inner/outer join semantics).
 	 * @param currentDepth The current join depth
 	 * @throws org.hibernate.MappingException ???
 	 */
 	private void walkEntityAssociationTree(
 			final AssociationType associationType,
 			final OuterJoinLoadable persister,
 			final int propertyNumber,
 			final String alias,
 			final PropertyPath path,
 			final boolean nullable,
 			final int currentDepth) throws MappingException {
 		String[] aliasedLhsColumns = JoinHelper.getAliasedLHSColumnNames(
 				associationType, alias, propertyNumber, persister, getFactory()
 		);
 		String[] lhsColumns = JoinHelper.getLHSColumnNames(
 				associationType, propertyNumber, persister, getFactory()
 		);
 		String lhsTable = JoinHelper.getLHSTableName(associationType, propertyNumber, persister);
 
 		PropertyPath subPath = path.append( persister.getSubclassPropertyName(propertyNumber) );
 		JoinType joinType = getJoinType(
 				persister,
 				subPath,
 				propertyNumber,
 				associationType,
 				persister.getFetchMode( propertyNumber ),
 				persister.getCascadeStyle( propertyNumber ),
 				lhsTable,
 				lhsColumns,
 				nullable,
 				currentDepth
 		);
 		addAssociationToJoinTreeIfNecessary(
 				associationType,
 				aliasedLhsColumns,
 				alias,
 				subPath,
 				currentDepth,
 				joinType
 		);
 	}
 
 	/**
 	 * Determine the appropriate type of join (if any) to use to fetch the
 	 * given association.
 	 *
 	 * @param persister The owner of the association.
 	 * @param path The path to the association
 	 * @param propertyNumber The property number representing the association.
 	 * @param associationType The association type.
 	 * @param metadataFetchMode The metadata-defined fetch mode.
 	 * @param metadataCascadeStyle The metadata-defined cascade style.
 	 * @param lhsTable The owner table
 	 * @param lhsColumns The owner join columns
 	 * @param nullable Is the association nullable.
 	 * @param currentDepth Current join depth
 	 * @return type of join to use ({@link org.hibernate.sql.JoinType#INNER_JOIN},
 	 * {@link org.hibernate.sql.JoinType#LEFT_OUTER_JOIN}, or -1 to indicate no joining.
 	 * @throws MappingException ??
 	 */
 	protected JoinType getJoinType(
 			OuterJoinLoadable persister,
 			final PropertyPath path,
 			int propertyNumber,
 			AssociationType associationType,
 			FetchMode metadataFetchMode,
 			CascadeStyle metadataCascadeStyle,
 			String lhsTable,
 			String[] lhsColumns,
 			final boolean nullable,
 			final int currentDepth) throws MappingException {
 		return getJoinType(
 				associationType,
 				metadataFetchMode,
 				path,
 				lhsTable,
 				lhsColumns,
 				nullable,
 				currentDepth,
 				metadataCascadeStyle
 		);
 	}
 
 	/**
 	 * Determine the appropriate associationType of join (if any) to use to fetch the
 	 * given association.
 	 *
 	 * @param associationType The association associationType.
 	 * @param config The metadata-defined fetch mode.
 	 * @param path The path to the association
 	 * @param lhsTable The owner table
 	 * @param lhsColumns The owner join columns
 	 * @param nullable Is the association nullable.
 	 * @param currentDepth Current join depth
 	 * @param cascadeStyle The metadata-defined cascade style.
 	 * @return type of join to use ({@link org.hibernate.sql.JoinType#INNER_JOIN},
 	 * {@link org.hibernate.sql.JoinType#LEFT_OUTER_JOIN}, or -1 to indicate no joining.
 	 * @throws MappingException ??
 	 */
 	protected JoinType getJoinType(
 			AssociationType associationType,
 			FetchMode config,
 			PropertyPath path,
 			String lhsTable,
 			String[] lhsColumns,
 			boolean nullable,
 			int currentDepth,
 			CascadeStyle cascadeStyle) throws MappingException {
 		if  ( !isJoinedFetchEnabled( associationType, config, cascadeStyle ) ) {
 			return JoinType.NONE;
 		}
 		if ( isTooDeep(currentDepth) || ( associationType.isCollectionType() && isTooManyCollections() ) ) {
 			return JoinType.NONE;
 		}
 		if ( isDuplicateAssociation( lhsTable, lhsColumns, associationType ) ) {
 			return JoinType.NONE;
 		}
 		return getJoinType( nullable, currentDepth );
 	}
 
 	/**
 	 * Walk the association tree for an entity, adding associations which should
 	 * be join fetched to the {@link #associations} inst var.  This form is the
 	 * entry point into the walking for a given entity, starting the recursive
 	 * calls into {@link #walkEntityTree(org.hibernate.persister.entity.OuterJoinLoadable, String, PropertyPath ,int)}.
 	 *
 	 * @param persister The persister representing the entity to be walked.
 	 * @param alias The (root) alias to use for this entity/persister.
 	 * @param path The property path to the entity being walked
 	 * @param currentDepth The current join depth
 	 * @throws org.hibernate.MappingException ???
 	 */
 	private void walkEntityTree(
 			final OuterJoinLoadable persister,
 			final String alias,
 			final PropertyPath path,
 			final int currentDepth) throws MappingException {
 		int n = persister.countSubclassProperties();
 		for ( int i = 0; i < n; i++ ) {
 			Type type = persister.getSubclassPropertyType(i);
 			if ( type.isAssociationType() ) {
 				walkEntityAssociationTree(
 					( AssociationType ) type,
 					persister,
 					i,
 					alias,
 					path,
 					persister.isSubclassPropertyNullable(i),
 					currentDepth
 				);
 			}
 			else if ( type.isComponentType() ) {
 				walkComponentTree(
 						( CompositeType ) type,
 						i,
 						0,
 						persister,
 						alias,
 						path.append( persister.getSubclassPropertyName(i) ),
 						currentDepth
 				);
 			}
 		}
 	}
 
 	/**
 	 * For a component, add to a list of associations to be fetched by outerjoin
 	 *
 	 *
 	 * @param componentType The component type to be walked.
 	 * @param propertyNumber The property number for the component property (relative to
 	 * persister).
 	 * @param begin todo unknowm
 	 * @param persister The owner of the component property
 	 * @param alias The root alias
 	 * @param path The property access path
 	 * @param currentDepth The current join depth
 	 * @throws org.hibernate.MappingException ???
 	 */
 	private void walkComponentTree(
 			final CompositeType componentType,
 			final int propertyNumber,
 			int begin,
 			final OuterJoinLoadable persister,
 			final String alias,
 			final PropertyPath path,
 			final int currentDepth) throws MappingException {
 		Type[] types = componentType.getSubtypes();
 		String[] propertyNames = componentType.getPropertyNames();
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( types[i].isAssociationType() ) {
 				AssociationType associationType = (AssociationType) types[i];
 				String[] aliasedLhsColumns = JoinHelper.getAliasedLHSColumnNames(
 					associationType, alias, propertyNumber, begin, persister, getFactory()
 				);
 				String[] lhsColumns = JoinHelper.getLHSColumnNames(
 					associationType, propertyNumber, begin, persister, getFactory()
 				);
 				String lhsTable = JoinHelper.getLHSTableName(associationType, propertyNumber, persister);
 
 				final PropertyPath subPath = path.append( propertyNames[i] );
 				final boolean[] propertyNullability = componentType.getPropertyNullability();
 				final JoinType joinType = getJoinType(
 						persister,
 						subPath,
 						propertyNumber,
 						associationType,
 						componentType.getFetchMode(i),
 						componentType.getCascadeStyle(i),
 						lhsTable,
 						lhsColumns,
 						propertyNullability==null || propertyNullability[i],
 						currentDepth
 				);
 				addAssociationToJoinTreeIfNecessary(			
 						associationType,
 						aliasedLhsColumns,
 						alias,
 						subPath,
 						currentDepth,
 						joinType
 				);
 
 			}
 			else if ( types[i].isComponentType() ) {
 				final PropertyPath subPath = path.append( propertyNames[i] );
 				walkComponentTree(
 						( CompositeType ) types[i],
 						propertyNumber,
 						begin,
 						persister,
 						alias,
 						subPath,
 						currentDepth
 				);
 			}
 			begin += types[i].getColumnSpan( getFactory() );
 		}
 
 	}
 
 	/**
 	 * For a composite element, add to a list of associations to be fetched by outerjoin
 	 */
 	private void walkCompositeElementTree(
 			final CompositeType compositeType,
 			final String[] cols,
 			final QueryableCollection persister,
 			final String alias,
 			final PropertyPath path,
 			final int currentDepth) throws MappingException {
 
 		Type[] types = compositeType.getSubtypes();
 		String[] propertyNames = compositeType.getPropertyNames();
 		int begin = 0;
 		for ( int i=0; i <types.length; i++ ) {
 			int length = types[i].getColumnSpan( getFactory() );
 			String[] lhsColumns = ArrayHelper.slice(cols, begin, length);
 
 			if ( types[i].isAssociationType() ) {
 				AssociationType associationType = (AssociationType) types[i];
 
 				// simple, because we can't have a one-to-one or a collection 
 				// (or even a property-ref) in a composite-element:
 				String[] aliasedLhsColumns = StringHelper.qualify(alias, lhsColumns);
 
 				final PropertyPath subPath = path.append( propertyNames[i] );
 				final boolean[] propertyNullability = compositeType.getPropertyNullability();
 				final JoinType joinType = getJoinType(
 						associationType,
 						compositeType.getFetchMode(i),
 						subPath,
 						persister.getTableName(),
 						lhsColumns,
 						propertyNullability==null || propertyNullability[i],
 						currentDepth, 
 						compositeType.getCascadeStyle(i)
 					);
 				addAssociationToJoinTreeIfNecessary(
 						associationType,
 						aliasedLhsColumns,
 						alias,
 						subPath,
 						currentDepth,
 						joinType
 					);
 			}
 			else if ( types[i].isComponentType() ) {
 				final PropertyPath subPath = path.append( propertyNames[i] );
 				walkCompositeElementTree(
 						(CompositeType) types[i],
 						lhsColumns,
 						persister,
 						alias,
 						subPath,
 						currentDepth
 					);
 			}
 			begin+=length;
 		}
 
 	}
 
 	/**
 	 * Use an inner join if it is a non-null association and this
 	 * is the "first" join in a series
 	 */
 	protected JoinType getJoinType(boolean nullable, int currentDepth) {
 		//TODO: this is too conservative; if all preceding joins were 
 		//      also inner joins, we could use an inner join here
 		//
 		// IMPL NOTE : currentDepth might be less-than zero if this is the
 		// 		root of a many-to-many collection initializer 
 		return !nullable && currentDepth <= 0
 				? JoinType.INNER_JOIN
 				: JoinType.LEFT_OUTER_JOIN;
 	}
 
 	protected boolean isTooDeep(int currentDepth) {
 		Integer maxFetchDepth = getFactory().getSettings().getMaximumFetchDepth();
 		return maxFetchDepth!=null && currentDepth >= maxFetchDepth.intValue();
 	}
 	
 	protected boolean isTooManyCollections() {
 		return false;
 	}
 	
 	/**
 	 * Does the mapping, and Hibernate default semantics, specify that
 	 * this association should be fetched by outer joining
 	 */
 	protected boolean isJoinedFetchEnabledInMapping(FetchMode config, AssociationType type) 
 	throws MappingException {
 		if ( !type.isEntityType() && !type.isCollectionType() ) {
 			return false;
 		}
 		else {
 			if (config==FetchMode.JOIN) return true;
 			if (config==FetchMode.SELECT) return false;
 			if ( type.isEntityType() ) {
 				//TODO: look at the owning property and check that it 
 				//      isn't lazy (by instrumentation)
 				EntityType entityType =(EntityType) type;
 				EntityPersister persister = getFactory().getEntityPersister( entityType.getAssociatedEntityName() );
 				return !persister.hasProxy();
 			}
 			else {
 				return false;
 			}
 		}
 	}
 
 	/**
 	 * Override on subclasses to enable or suppress joining 
 	 * of certain association types
 	 */
 	protected boolean isJoinedFetchEnabled(AssociationType type, FetchMode config, CascadeStyle cascadeStyle) {
 		return type.isEntityType() && isJoinedFetchEnabledInMapping(config, type) ;
 	}
 	
 	protected String generateTableAlias(final int n, final PropertyPath path, final Joinable joinable) {
 		return StringHelper.generateAlias( joinable.getName(), n );
 	}
 
 	protected String generateRootAlias(final String description) {
 		return StringHelper.generateAlias(description, 0);
 	}
 
 	/**
 	 * Used to detect circularities in the joined graph, note that 
 	 * this method is side-effecty
 	 */
 	protected boolean isDuplicateAssociation(final String foreignKeyTable, final String[] foreignKeyColumns) {
 		AssociationKey associationKey = new AssociationKey(foreignKeyColumns, foreignKeyTable);
 		return !visitedAssociationKeys.add( associationKey );
 	}
 	
 	/**
 	 * Used to detect circularities in the joined graph, note that 
 	 * this method is side-effecty
 	 */
 	protected boolean isDuplicateAssociation(final String lhsTable, final String[] lhsColumnNames, final AssociationType type) {
 		final String foreignKeyTable;
 		final String[] foreignKeyColumns;
 		if ( type.getForeignKeyDirection()==ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT ) {
 			foreignKeyTable = lhsTable;
 			foreignKeyColumns = lhsColumnNames;
 		}
 		else {
 			foreignKeyTable = type.getAssociatedJoinable( getFactory() ).getTableName();
 			foreignKeyColumns = JoinHelper.getRHSColumnNames( type, getFactory() );
 		}
 		return isDuplicateAssociation(foreignKeyTable, foreignKeyColumns);
 	}
 	
 	/**
 	 * Uniquely identifier a foreign key, so that we don't
 	 * join it more than once, and create circularities
 	 */
 	private static final class AssociationKey {
 		private String[] columns;
 		private String table;
 		private AssociationKey(String[] columns, String table) {
 			this.columns = columns;
 			this.table = table;
 		}
 		@Override
         public boolean equals(Object other) {
 			AssociationKey that = (AssociationKey) other;
 			return that.table.equals(table) && Arrays.equals(columns, that.columns);
 		}
 		@Override
         public int hashCode() {
 			return table.hashCode(); //TODO: inefficient
 		}
 	}
 	
 	/**
 	 * Should we join this association?
 	 */
 	protected boolean isJoinable(
 			final JoinType joinType,
 			final Set visitedAssociationKeys,
 			final String lhsTable,
 			final String[] lhsColumnNames,
 			final AssociationType type,
 			final int depth) {
 
 		if ( joinType == JoinType.NONE ) {
 			return false;
 		}
 		
 		if ( joinType == JoinType.INNER_JOIN ) {
 			return true;
 		}
 
 		Integer maxFetchDepth = getFactory().getSettings().getMaximumFetchDepth();
 		final boolean tooDeep = maxFetchDepth!=null && depth >= maxFetchDepth.intValue();
 		
 		return !tooDeep && !isDuplicateAssociation(lhsTable, lhsColumnNames, type);
 	}
 	
 	protected String orderBy(final List associations, final String orderBy) {
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
 	protected final JoinFragment mergeOuterJoins(List associations)
 	throws MappingException {
 		JoinFragment outerjoin = getDialect().createOuterJoinFragment();
 		Iterator iter = associations.iterator();
 		OuterJoinableAssociation last = null;
 		while ( iter.hasNext() ) {
 			OuterJoinableAssociation oj = (OuterJoinableAssociation) iter.next();
 			if ( last != null && last.isManyToManyWith( oj ) ) {
 				oj.addManyToManyJoin( outerjoin, ( QueryableCollection ) last.getJoinable() );
 			}
 			else {
 				oj.addJoins(outerjoin);
 			}
 			last = oj;
 		}
 		last = null;
 		return outerjoin;
 	}
 
 	/**
 	 * Count the number of instances of Joinable which are actually
 	 * also instances of Loadable, or are one-to-many associations
 	 */
 	protected static final int countEntityPersisters(List associations)
 	throws MappingException {
 		int result = 0;
 		Iterator iter = associations.iterator();
 		while ( iter.hasNext() ) {
 			OuterJoinableAssociation oj = (OuterJoinableAssociation) iter.next();
 			if ( oj.getJoinable().consumesEntityAlias() ) {
 				result++;
 			}
 		}
 		return result;
 	}
 	
 	/**
 	 * Count the number of instances of Joinable which are actually
 	 * also instances of PersistentCollection which are being fetched
 	 * by outer join
 	 */
 	protected static final int countCollectionPersisters(List associations)
 	throws MappingException {
 		int result = 0;
 		Iterator iter = associations.iterator();
 		while ( iter.hasNext() ) {
 			OuterJoinableAssociation oj = (OuterJoinableAssociation) iter.next();
 			if ( oj.getJoinType()==JoinType.LEFT_OUTER_JOIN &&
 					oj.getJoinable().isCollection() &&
 					! oj.hasRestriction() ) {
 				result++;
 			}
 		}
 		return result;
 	}
 	
 	/**
 	 * Get the order by string required for collection fetching
 	 */
 	protected static final String orderBy(List associations)
 	throws MappingException {
 		StringBuilder buf = new StringBuilder();
 		Iterator iter = associations.iterator();
 		OuterJoinableAssociation last = null;
 		while ( iter.hasNext() ) {
 			OuterJoinableAssociation oj = (OuterJoinableAssociation) iter.next();
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
 		if ( buf.length()>0 ) buf.setLength( buf.length()-2 );
 		return buf.toString();
 	}
-	
+
 	/**
 	 * Render the where condition for a (batch) load by identifier / collection key
 	 */
 	protected StringBuilder whereString(String alias, String[] columnNames, int batchSize) {
 		if ( columnNames.length==1 ) {
 			// if not a composite key, use "foo in (?, ?, ?)" for batching
 			// if no batch, and not a composite key, use "foo = ?"
 			InFragment in = new InFragment().setColumn( alias, columnNames[0] );
 			for ( int i=0; i<batchSize; i++ ) in.addValue("?");
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
 
 
 	protected void initPersisters(final List associations, final LockMode lockMode) throws MappingException {
 		initPersisters( associations, new LockOptions(lockMode));
 	}
 
 	protected static interface AssociationInitCallback {
 		public static final AssociationInitCallback NO_CALLBACK = new AssociationInitCallback() {
 			public void associationProcessed(OuterJoinableAssociation oja, int position) {
 			}
 		};
 
 		public void associationProcessed(OuterJoinableAssociation oja, int position);
 	}
 	protected void initPersisters(final List associations, final LockOptions lockOptions) throws MappingException {
 		initPersisters( associations, lockOptions, AssociationInitCallback.NO_CALLBACK );
 	}
 
 	protected void initPersisters(
 			final List associations,
 			final LockOptions lockOptions,
 			final AssociationInitCallback callback) throws MappingException {
 		final int joins = countEntityPersisters(associations);
 		final int collections = countCollectionPersisters(associations);
 
 		collectionOwners = collections==0 ? null : new int[collections];
 		collectionPersisters = collections==0 ? null : new CollectionPersister[collections];
 		collectionSuffixes = BasicLoader.generateSuffixes( joins + 1, collections );
 
 		this.lockOptions = lockOptions;
 
 		persisters = new Loadable[joins];
 		aliases = new String[joins];
 		owners = new int[joins];
 		ownerAssociationTypes = new EntityType[joins];
 		lockModeArray = ArrayHelper.fillArray( lockOptions.getLockMode(), joins );
 
 		int i=0;
 		int j=0;
 		Iterator iter = associations.iterator();
 		while ( iter.hasNext() ) {
 			final OuterJoinableAssociation oj = (OuterJoinableAssociation) iter.next();
 			if ( !oj.isCollection() ) {
 				
 				persisters[i] = (Loadable) oj.getJoinable();
 				aliases[i] = oj.getRHSAlias();
 				owners[i] = oj.getOwner(associations);
 				ownerAssociationTypes[i] = (EntityType) oj.getJoinableType();
 				callback.associationProcessed( oj, i );
 				i++;
 				
 			}
 			else {
 				
 				QueryableCollection collPersister = (QueryableCollection) oj.getJoinable();
 				if ( oj.getJoinType()==JoinType.LEFT_OUTER_JOIN && ! oj.hasRestriction() ) {
 					//it must be a collection fetch
 					collectionPersisters[j] = collPersister;
 					collectionOwners[j] = oj.getOwner(associations);
 					j++;
 				}
 	
 				if ( collPersister.isOneToMany() ) {
 					persisters[i] = (Loadable) collPersister.getElementPersister();
 					aliases[i] = oj.getRHSAlias();
 					callback.associationProcessed( oj, i );
 					i++;
 				}
 			}
 		}
 
 		if ( ArrayHelper.isAllNegative(owners) ) owners = null;
 		if ( collectionOwners!=null && ArrayHelper.isAllNegative(collectionOwners) ) {
 			collectionOwners = null;
 		}
 	}
 
 	/**
 	 * Generate a select list of columns containing all properties of the entity classes
 	 */
 	protected final String selectString(List associations)
 	throws MappingException {
 
 		if ( associations.size()==0 ) {
 			return "";
 		}
 		else {
 			StringBuilder buf = new StringBuilder( associations.size() * 100 );
 			int entityAliasCount=0;
 			int collectionAliasCount=0;
 			for ( int i=0; i<associations.size(); i++ ) {
 				OuterJoinableAssociation join = (OuterJoinableAssociation) associations.get(i);
 				OuterJoinableAssociation next = (i == associations.size() - 1)
 				        ? null
 				        : ( OuterJoinableAssociation ) associations.get( i + 1 );
 				final Joinable joinable = join.getJoinable();
 				final String entitySuffix = ( suffixes == null || entityAliasCount >= suffixes.length )
 				        ? null
 				        : suffixes[entityAliasCount];
 				final String collectionSuffix = ( collectionSuffixes == null || collectionAliasCount >= collectionSuffixes.length )
 				        ? null
 				        : collectionSuffixes[collectionAliasCount];
 				final String selectFragment = joinable.selectFragment(
 						next == null ? null : next.getJoinable(),
 						next == null ? null : next.getRHSAlias(),
 						join.getRHSAlias(),
 						entitySuffix,
 				        collectionSuffix,
 						join.getJoinType()==JoinType.LEFT_OUTER_JOIN
 				);
 				if (selectFragment.trim().length() > 0) {
 					buf.append(", ").append(selectFragment);
 				}
 				if ( joinable.consumesEntityAlias() ) entityAliasCount++;
 				if ( joinable.consumesCollectionAlias() && join.getJoinType()==JoinType.LEFT_OUTER_JOIN ) collectionAliasCount++;
 			}
 			return buf.toString();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index c318525ee9..ae12191e7f 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1859 +1,1932 @@
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
 
 import org.jboss.logging.Logger;
 
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
+import org.hibernate.cfg.NotYetImplementedException;
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
+import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
+import org.hibernate.persister.entity.Queryable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
+import org.hibernate.sql.ordering.antlr.ColumnReference;
+import org.hibernate.sql.ordering.antlr.FormulaReference;
+import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
+import org.hibernate.sql.ordering.antlr.OrderByFragmentParser;
+import org.hibernate.sql.ordering.antlr.OrderByTranslation;
+import org.hibernate.sql.ordering.antlr.SqlValueReference;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
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
 
+	protected final boolean hasWhere;
 	protected final String sqlWhereString;
-	private final String sqlOrderByStringTemplate;
 	private final String sqlWhereStringTemplate;
+
 	private final boolean hasOrder;
-	protected final boolean hasWhere;
+	private final OrderByTranslation orderByTranslation;
+
+	private final boolean hasManyToManyOrder;
+	private final OrderByTranslation manyToManyOrderByTranslation;
+
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
 	private final boolean isInverse;
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
 	private final SqlExceptionHelper sqlExceptionHelper;
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
 
-	private final boolean hasManyToManyOrder;
-	private final String manyToManyOrderByTemplate;
-
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
 					(CacheEntryStructure) new StructuredMapCacheEntry() :
 					(CacheEntryStructure) new StructuredCollectionCacheEntry();
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
 			elementColumnAliases[j] = selectable.getAlias( dialect );
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
-			ColumnMapper mapper = new ColumnMapper() {
-
-				public String[] map(String reference) {
-					return elementPropertyMapping.toColumns( reference );
-				}
-			};
-			sqlOrderByStringTemplate = Template.renderOrderByStringTemplate(
+			orderByTranslation = Template.translateOrderBy(
 					collection.getOrderBy(),
-					mapper,
+					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
-					);
+			);
 		}
 		else {
-			sqlOrderByStringTemplate = null;
+			orderByTranslation = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
-			ColumnMapper mapper = new ColumnMapper() {
-
-				public String[] map(String reference) {
-					return elementPropertyMapping.toColumns( reference );
-				}
-			};
-			manyToManyOrderByTemplate = Template.renderOrderByStringTemplate(
+			manyToManyOrderByTranslation = Template.translateOrderBy(
 					collection.getManyToManyOrdering(),
-					mapper,
+					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
-					);
+			);
 		}
 		else {
-			manyToManyOrderByTemplate = null;
+			manyToManyOrderByTranslation = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
+	private class ColumnMapperImpl implements ColumnMapper {
+		@Override
+		public SqlValueReference[] map(String reference) {
+			final String[] columnNames;
+			final String[] formulaTemplates;
+
+			// handle the special "$element$" property name...
+			if ( "$element$".equals( reference ) ) {
+				columnNames = elementColumnNames;
+				formulaTemplates = elementFormulaTemplates;
+			}
+			else {
+				columnNames = elementPropertyMapping.toColumns( reference );
+				formulaTemplates = formulaTemplates( reference, columnNames.length );
+			}
+
+			final SqlValueReference[] result = new SqlValueReference[ columnNames.length ];
+			int i = 0;
+			for ( final String columnName : columnNames ) {
+				if ( columnName == null ) {
+					// if the column name is null, it indicates that this index in the property value mapping is
+					// actually represented by a formula.
+					final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
+					final String formulaTemplate = formulaTemplates[i];
+					result[i] = new FormulaReference() {
+						@Override
+						public String getFormulaFragment() {
+							return formulaTemplate;
+						}
+					};
+				}
+				else {
+					result[i] = new ColumnReference() {
+						@Override
+						public String getColumnName() {
+							return columnName;
+						}
+					};
+				}
+				i++;
+			}
+			return result;
+		}
+	}
+
+	private String[] formulaTemplates(String reference, int expectedSize) {
+		try {
+			final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
+			return  ( (Queryable) elementPersister ).getSubclassPropertyFormulaTemplateClosure()[propertyIndex];
+		}
+		catch (Exception e) {
+			return new String[expectedSize];
+		}
+	}
+
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
-				? StringHelper.replace( sqlOrderByStringTemplate, Template.TEMPLATE, alias )
+				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
-				? StringHelper.replace( manyToManyOrderByTemplate, Template.TEMPLATE, alias )
+				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
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
 						expectation.verifyOutcome( st.executeUpdate(), st, -1 );
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
 						st.close();
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
 
 	private BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
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
 									expectation.verifyOutcome( st.executeUpdate(), st, -1 );
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
 									st.close();
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
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
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
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
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
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
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
 								st.close();
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
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
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
 					MessageHelper.collectionInfoString( this, id, getFactory() ) );
 
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
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
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
 								st.close();
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
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
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
 		manyToManyFilterHelper.render( buffer, alias, enabledFilters );
 
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
 		filterHelper.render( sessionFilterFragment, alias, enabledFilters );
 
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
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
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
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next();
 				}
 				finally {
 					rs.close();
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				st.close();
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
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
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
+
+	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
+		private final String rootAlias;
+
+		private StandardOrderByAliasResolver(String rootAlias) {
+			this.rootAlias = rootAlias;
+		}
+
+		@Override
+		public String resolveTableAlias(String columnReference) {
+			if ( elementPersister == null ) {
+				// we have collection of non-entity elements...
+				return rootAlias;
+			}
+			else {
+				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
+			}
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index ea5a4154e9..34a9e4cd7a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -972,2001 +972,2002 @@ public abstract class AbstractEntityPersister
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
 				cascades.add( CascadeStyle.NONE );
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
 
 		Map<String, String> filterDefaultConditionsByName = new HashMap<String, String>();
 		for ( FilterDefinition filterDefinition : entityBinding.getFilterDefinitions() ) {
 			filterDefaultConditionsByName.put( filterDefinition.getFilterName(), filterDefinition.getDefaultFilterCondition() );
 		}
 		filterHelper = new FilterHelper( filterDefaultConditionsByName, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = null;
 		temporaryIdTableDDL = null;
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
 						rs = ps.executeQuery();
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
 						rs.close();
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					ps.close();
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
 				ResultSet rs = ps.executeQuery();
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
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
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
 				int rows = st.executeUpdate();
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting version: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 	}
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return ( LockingStrategy ) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockMode lockMode,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockOptions lockOptions,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root(propertyPath);
 		Type type = propertyMapping.toType(rootPropertyName);
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = ( AssociationType ) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
 		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	protected String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuilder buf = new StringBuilder().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
-	protected String[][] getSubclassPropertyFormulaTemplateClosure() {
+	@Override
+	public String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
 	protected String[] getSubclassPropertySubclassNameClosure() {
 		return subclassPropertySubclassNameClosure;
 	}
 
 	protected String[] getSubclassColumnClosure() {
 		return subclassColumnClosure;
 	}
 
 	protected String[] getSubclassColumnAliasClosure() {
 		return subclassColumnAliasClosure;
 	}
 
 	public String[] getSubclassColumnReaderTemplateClosure() {
 		return subclassColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaClosure() {
 		return subclassFormulaClosure;
 	}
 
 	protected String[] getSubclassFormulaTemplateClosure() {
 		return subclassFormulaTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaAliasClosure() {
 		return subclassFormulaAliasClosure;
 	}
 
 	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = ( String[] ) subclassPropertyAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	public String[] getSubclassPropertyColumnNames(String propertyName) {
 		//TODO: should we allow suffixes on these ?
 		return ( String[] ) subclassPropertyColumnNames.get( propertyName );
 	}
 
 
 
 	//This is really ugly, but necessary:
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
 
 		// ALIASES
 		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(EntityBinding model) throws MappingException {
 
 		// ALIASES
 
 		// TODO: Fix when subclasses are working (HHH-6337)
 		//internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = ( Property ) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
 			if ( prop.isComposite() ) {
 				Component component = ( Component ) prop.getValue();
 				Iterator compProps = component.getPropertyIterator();
 				internalInitSubclassPropertyAliasesMap( propname, compProps );
 			}
 			else {
 				String[] aliases = new String[prop.getColumnSpan()];
 				String[] cols = new String[prop.getColumnSpan()];
 				Iterator colIter = prop.getColumnIterator();
 				int l = 0;
 				while ( colIter.hasNext() ) {
 					Selectable thing = ( Selectable ) colIter.next();
 					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
 					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
 					l++;
 				}
 
 				subclassPropertyAliases.put( propname, aliases );
 				subclassPropertyColumnNames.put( propname, cols );
 			}
 		}
 
 	}
 
 	public Object loadByUniqueKey(
 			String propertyName,
 			Object uniqueKey,
 			SessionImplementor session) throws HibernateException {
 		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
 	}
 
 	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
 		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
 				&& propertyName.indexOf('.')<0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
 
 		if ( useStaticLoader ) {
 			return ( EntityLoader ) uniqueKeyLoaders.get( propertyName );
 		}
 		else {
 			return createUniqueKeyLoader(
 					propertyMapping.toType( propertyName ),
 					propertyMapping.toColumns( propertyName ),
 					session.getLoadQueryInfluencers()
 			);
 		}
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		return entityMetamodel.getPropertyIndex(propertyName);
 	}
 
 	protected void createUniqueKeyLoaders() throws MappingException {
 		Type[] propertyTypes = getPropertyTypes();
 		String[] propertyNames = getPropertyNames();
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( propertyUniqueness[i] ) {
 				//don't need filters for the static loaders
 				uniqueKeyLoaders.put(
 						propertyNames[i],
 						createUniqueKeyLoader(
 								propertyTypes[i],
 								getPropertyColumnNames( i ),
 								LoadQueryInfluencers.NONE
 						)
 				);
 				//TODO: create uk loaders for component properties
 			}
 		}
 	}
 
 	private EntityLoader createUniqueKeyLoader(
 			Type uniqueKeyType,
 			String[] columns,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		if ( uniqueKeyType.isEntityType() ) {
 			String className = ( ( EntityType ) uniqueKeyType ).getAssociatedEntityName();
 			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
 		}
 		return new EntityLoader(
 				this,
 				columns,
 				uniqueKeyType,
 				1,
 				LockMode.NONE,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	protected boolean hasWhere() {
 		return sqlWhereString != null;
 	}
 
 	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
 		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
 			propertyMapping.initPropertyPaths( getSubclassPropertyNameClosure()[i],
 					getSubclassPropertyTypeClosure()[i],
 					getSubclassPropertyColumnNameClosure()[i],
 					getSubclassPropertyColumnReaderClosure()[i],
 					getSubclassPropertyColumnReaderTemplateClosure()[i],
 					getSubclassPropertyFormulaTemplateClosure()[i],
 					mapping );
 		}
 	}
 
 	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
 		String idProp = getIdentifierPropertyName();
 		if ( idProp != null ) {
 			propertyMapping.initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			propertyMapping.initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			propertyMapping.initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths( ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[]{getDiscriminatorColumnName()},
 				new String[]{getDiscriminatorColumnReaders()},
 				new String[]{getDiscriminatorColumnReaderTemplate()},
 				new String[]{getDiscriminatorFormulaTemplate()},
 				getFactory() );
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths(mapping);
 		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths(mapping);
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 				lockMode,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 			lockOptions,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch( StaleStateException e ) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch( TooManyRowsAffectedException e ) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch ( Throwable t ) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(final boolean[] includeProperty,
 										  final int j,
 										  final Object[] oldFields,
 										  final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns( getPropertyColumnNames(i), propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 					? getPropertyUpdateability() //optimistic-lock="all", include all updatable properties
 					: includeProperty; 			 //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k=0; k<propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
         return includeProperty[ getVersionProperty() ] ||
 				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
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
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				// this property belongs on the table and is to be inserted
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
 			SessionImplementor session) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1 );
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
 	        int index) throws SQLException, HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Dehydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				//index += getPropertyColumnSpan( i );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			index += 1;
 		}
 		else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			index += getIdentifierColumnSpan();
 		}
 
 		return index;
 
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
 					sequentialResultSet = sequentialSelect.executeQuery();
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
 				sequentialResultSet.close();
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				sequentialSelect.close();
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
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session );
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
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( insert.executeUpdate(), insert, -1 );
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
 					insert.close();
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
@@ -3751,1001 +3752,1010 @@ public abstract class AbstractEntityPersister
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
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
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
 				ResultSet rs = ps.executeQuery();
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
 						rs.close();
 					}
 				}
 			}
 			finally {
 				ps.close();
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
 				ResultSet rs = ps.executeQuery();
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
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
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
 				ResultSet rs = ps.executeQuery();
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					return (Serializable) getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
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
+
+	@Override
+	public String getTableAliasForColumn(String columnName, String rootAlias) {
+		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
+	}
+
+	public int determineTableNumberForColumn(String columnName) {
+		return 0;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
index 89169a8b79..171bdacf80 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
@@ -1,843 +1,863 @@
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
 import java.util.Iterator;
 import java.util.Map;
 
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
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
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
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( persistentClass );
 
 		postConstruct( mapping );
 
 	}
 
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
 
 	private static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equals( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
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
 
 	public String filterFragment(String alias) {
 		return hasWhere() ?
 				" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
 				"";
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
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull( propertyName );
 		if ( index == null ) {
 			return null;
 		}
 		return tableNames[propertyTableNumbers[index.intValue()]];
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
 			// special case where we need to force incloude all subclass joins
 			return Declarer.SUBCLASS;
 		}
 		return super.getSubclassPropertyDeclarer( propertyPath );
 	}
+
+	@Override
+	public int determineTableNumberForColumn(String columnName) {
+		final String[] subclassColumnNameClosure = getSubclassColumnClosure();
+		for ( int i = 0, max = subclassColumnNameClosure.length; i < max; i++ ) {
+			final boolean quoted = subclassColumnNameClosure[i].startsWith( "\"" )
+					&& subclassColumnNameClosure[i].endsWith( "\"" );
+			if ( quoted ) {
+				if ( subclassColumnNameClosure[i].equals( columnName ) ) {
+					return getSubclassColumnTableNumberClosure()[i];
+				}
+			}
+			else {
+				if ( subclassColumnNameClosure[i].equalsIgnoreCase( columnName ) ) {
+					return getSubclassColumnTableNumberClosure()[i];
+				}
+			}
+		}
+		throw new HibernateException( "Could not locate table which owns column [" + columnName + "] referenced in order-by mapping" );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
index 4c6803aecf..d0cc4e3168 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/Loadable.java
@@ -1,123 +1,137 @@
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
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Implemented by a <tt>EntityPersister</tt> that may be loaded
  * using <tt>Loader</tt>.
  *
  * @see org.hibernate.loader.Loader
  * @author Gavin King
  */
 public interface Loadable extends EntityPersister {
 	
 	public static final String ROWID_ALIAS = "rowid_";
 
 	/**
 	 * Does this persistent class have subclasses?
 	 */
 	public boolean hasSubclasses();
 
 	/**
 	 * Get the discriminator type
 	 */
 	public Type getDiscriminatorType();
 
 	/**
 	 * Get the discriminator value
 	 */
 	public Object getDiscriminatorValue();
 
 	/**
 	 * Get the concrete subclass corresponding to the given discriminator
 	 * value
 	 */
 	public String getSubclassForDiscriminatorValue(Object value);
 
 	/**
 	 * Get the names of columns used to persist the identifier
 	 */
 	public String[] getIdentifierColumnNames();
 
 	/**
 	 * Get the result set aliases used for the identifier columns, given a suffix
 	 */
 	public String[] getIdentifierAliases(String suffix);
 	/**
 	 * Get the result set aliases used for the property columns, given a suffix (properties of this class, only).
 	 */
 	public String[] getPropertyAliases(String suffix, int i);
 	
 	/**
 	 * Get the result set column names mapped for this property (properties of this class, only).
 	 */
 	public String[] getPropertyColumnNames(int i);
 	
 	/**
 	 * Get the result set aliases used for the identifier columns, given a suffix
 	 */
 	public String getDiscriminatorAlias(String suffix);
 	
 	/**
 	 * @return the column name for the discriminator as specified in the mapping.
 	 */
 	public String getDiscriminatorColumnName();
 	
 	/**
 	 * Does the result set contain rowids?
 	 */
 	public boolean hasRowId();
 	
 	/**
 	 * Retrieve property values from one row of a result set
 	 */
 	public Object[] hydrate(
 			ResultSet rs,
 			Serializable id,
 			Object object,
 			Loadable rootLoadable,
 			String[][] suffixedPropertyColumns,
 			boolean allProperties, 
 			SessionImplementor session)
 	throws SQLException, HibernateException;
 
 	public boolean isAbstract();
 
 	/**
 	 * Register the name of a fetch profile determined to have an affect on the
 	 * underlying loadable in regards to the fact that the underlying load SQL
 	 * needs to be adjust when the given fetch profile is enabled.
 	 * 
 	 * @param fetchProfileName The name of the profile affecting this.
 	 */
 	public void registerAffectingFetchProfile(String fetchProfileName);
+
+	/**
+	 * Given a column name and the root table alias in use for the entity hierarchy, determine the proper table alias
+	 * for the table in that hierarchy that contains said column.
+	 * <p/>
+	 * NOTE : Generally speaking the column is not validated to exist.  Most implementations simply return the
+	 * root alias; the exception is {@link JoinedSubclassEntityPersister}
+	 *
+	 * @param columnName The column name
+	 * @param rootAlias The hierarchy root alias
+	 *
+	 * @return The proper table alias for qualifying the given column.
+	 */
+	public String getTableAliasForColumn(String columnName, String rootAlias);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
index 680e376e1f..bf92493f04 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
@@ -1,185 +1,187 @@
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
 import org.hibernate.sql.SelectFragment;
 
 /**
  * Extends the generic <tt>EntityPersister</tt> contract to add
  * operations required by the Hibernate Query Language
  *
  * @author Gavin King
  */
 public interface Queryable extends Loadable, PropertyMapping, Joinable {
 
 	/**
 	 * Is this an abstract class?
 	 */
 	public boolean isAbstract();
 	/**
 	 * Is this class explicit polymorphism only?
 	 */
 	public boolean isExplicitPolymorphism();
 	/**
 	 * Get the class that this class is mapped as a subclass of -
 	 * not necessarily the direct superclass
 	 */
 	public String getMappedSuperclass();
 	/**
 	 * Get the discriminator value for this particular concrete subclass,
 	 * as a string that may be embedded in a select statement
 	 */
 	public String getDiscriminatorSQLValue();
 
 	/**
 	 * Given a query alias and an identifying suffix, render the identifier select fragment.
 	 */
 	public String identifierSelectFragment(String name, String suffix);
 	/**
 	 * Given a query alias and an identifying suffix, render the property select fragment.
 	 */
 	public String propertySelectFragment(String alias, String suffix, boolean allProperties);
 
 	public SelectFragment propertySelectFragmentFragment(String alias, String suffix, boolean allProperties);
 	/**
 	 * Get the names of columns used to persist the identifier
 	 */
 	public String[] getIdentifierColumnNames();
 
 	/**
 	 * Is the inheritance hierarchy described by this persister contained across
 	 * multiple tables?
 	 *
 	 * @return True if the inheritance hierarchy is spread across multiple tables; false otherwise.
 	 */
 	public boolean isMultiTable();
 
 	/**
 	 * Get the names of all tables used in the hierarchy (up and down) ordered such
 	 * that deletes in the given order would not cause constraint violations.
 	 *
 	 * @return The ordered array of table names.
 	 */
 	public String[] getConstraintOrderedTableNameClosure();
 
 	/**
 	 * For each table specified in {@link #getConstraintOrderedTableNameClosure()}, get
 	 * the columns that define the key between the various hierarchy classes.
 	 * <p/>
 	 * The first dimension here corresponds to the table indexes returned in
 	 * {@link #getConstraintOrderedTableNameClosure()}.
 	 * <p/>
 	 * The second dimension should have the same length across all the elements in
 	 * the first dimension.  If not, that would be a problem ;)
 	 *
 	 */
 	public String[][] getContraintOrderedTableKeyColumnClosure();
 
 	/**
 	 * Get the name of the temporary table to be used to (potentially) store id values
 	 * when performing bulk update/deletes.
 	 *
 	 * @return The appropriate temporary table name.
 	 */
 	public String getTemporaryIdTableName();
 
 	/**
 	 * Get the appropriate DDL command for generating the temporary table to
 	 * be used to (potentially) store id values when performing bulk update/deletes.
 	 *
 	 * @return The appropriate temporary table creation command.
 	 */
 	public String getTemporaryIdTableDDL();
 
 	/**
 	 * Given a property name, determine the number of the table which contains the column
 	 * to which this property is mapped.
 	 * <p/>
 	 * Note that this is <b>not</b> relative to the results from {@link #getConstraintOrderedTableNameClosure()}.
 	 * It is relative to the subclass table name closure maintained internal to the persister (yick!).
 	 * It is also relative to the indexing used to resolve {@link #getSubclassTableName}...
 	 *
 	 * @param propertyPath The name of the property.
 	 * @return The number of the table to which the property is mapped.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath);
 
 	/**
 	 * Determine whether the given property is declared by our
 	 * mapped class, our super class, or one of our subclasses...
 	 * <p/>
 	 * Note: the method is called 'subclass property...' simply
 	 * for consistency sake (e.g. {@link #getSubclassPropertyTableNumber}
 	 *
 	 * @param propertyPath The property name.
 	 * @return The property declarer
 	 */
 	public Declarer getSubclassPropertyDeclarer(String propertyPath);
 
 	/**
 	 * Get the name of the table with the given index from the internal
 	 * array.
 	 *
 	 * @param number The index into the internal array.
 	 */
 	public String getSubclassTableName(int number);
 
 	/**
 	 * Is the version property included in insert statements?
 	 */
 	public boolean isVersionPropertyInsertable();
 
 	/**
 	 * The alias used for any filter conditions (mapped where-fragments or
 	 * enabled-filters).
 	 * </p>
 	 * This may or may not be different from the root alias depending upon the
 	 * inheritance mapping strategy.
 	 *
 	 * @param rootAlias The root alias
 	 * @return The alias used for "filter conditions" within the where clause.
 	 */
 	public String generateFilterConditionAlias(String rootAlias);
 
 	/**
 	 * Retrieve the information needed to properly deal with this entity's discriminator
 	 * in a query.
 	 *
 	 * @return The entity discriminator metadata
 	 */
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata();
 
+	String[][] getSubclassPropertyFormulaTemplateClosure();
+
 	public static class Declarer {
 		public static final Declarer CLASS = new Declarer( "class" );
 		public static final Declarer SUBCLASS = new Declarer( "subclass" );
 		public static final Declarer SUPERCLASS = new Declarer( "superclass" );
 		private final String name;
 		public Declarer(String name) {
 			this.name = name;
 		}
 		public String toString() {
 			return name;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/Template.java b/hibernate-core/src/main/java/org/hibernate/sql/Template.java
index f299a33b4d..9f062b9a72 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/Template.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/Template.java
@@ -1,734 +1,759 @@
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
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
+import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;
 import org.hibernate.sql.ordering.antlr.OrderByFragmentTranslator;
+import org.hibernate.sql.ordering.antlr.OrderByTranslation;
+import org.hibernate.sql.ordering.antlr.SqlValueReference;
 import org.hibernate.sql.ordering.antlr.TranslationContext;
 
 /**
  * Parses SQL fragments specified in mapping documents
  *
  * @author Gavin King
  */
 public final class Template {
 
 	private static final Set<String> KEYWORDS = new HashSet<String>();
 	private static final Set<String> BEFORE_TABLE_KEYWORDS = new HashSet<String>();
 	private static final Set<String> FUNCTION_KEYWORDS = new HashSet<String>();
 	static {
 		KEYWORDS.add("and");
 		KEYWORDS.add("or");
 		KEYWORDS.add("not");
 		KEYWORDS.add("like");
 		KEYWORDS.add("is");
 		KEYWORDS.add("in");
 		KEYWORDS.add("between");
 		KEYWORDS.add("null");
 		KEYWORDS.add("select");
 		KEYWORDS.add("distinct");
 		KEYWORDS.add("from");
 		KEYWORDS.add("join");
 		KEYWORDS.add("inner");
 		KEYWORDS.add("outer");
 		KEYWORDS.add("left");
 		KEYWORDS.add("right");
 		KEYWORDS.add("on");
 		KEYWORDS.add("where");
 		KEYWORDS.add("having");
 		KEYWORDS.add("group");
 		KEYWORDS.add("order");
 		KEYWORDS.add("by");
 		KEYWORDS.add("desc");
 		KEYWORDS.add("asc");
 		KEYWORDS.add("limit");
 		KEYWORDS.add("any");
 		KEYWORDS.add("some");
 		KEYWORDS.add("exists");
 		KEYWORDS.add("all");
 		KEYWORDS.add("union");
 		KEYWORDS.add("minus");
 
 		BEFORE_TABLE_KEYWORDS.add("from");
 		BEFORE_TABLE_KEYWORDS.add("join");
 
 		FUNCTION_KEYWORDS.add("as");
 		FUNCTION_KEYWORDS.add("leading");
 		FUNCTION_KEYWORDS.add("trailing");
 		FUNCTION_KEYWORDS.add("from");
 		FUNCTION_KEYWORDS.add("case");
 		FUNCTION_KEYWORDS.add("when");
 		FUNCTION_KEYWORDS.add("then");
 		FUNCTION_KEYWORDS.add("else");
 		FUNCTION_KEYWORDS.add("end");
 	}
 
 	public static final String TEMPLATE = "$PlaceHolder$";
 
 	private Template() {}
 
 	public static String renderWhereStringTemplate(String sqlWhereString, Dialect dialect, SQLFunctionRegistry functionRegistry) {
 		return renderWhereStringTemplate(sqlWhereString, TEMPLATE, dialect, functionRegistry);
 	}
 
 	/**
 	 * Same functionality as {@link #renderWhereStringTemplate(String, String, Dialect, SQLFunctionRegistry)},
 	 * except that a SQLFunctionRegistry is not provided (i.e., only the dialect-defined functions are
 	 * considered).  This is only intended for use by the annotations project until the
 	 * many-to-many/map-key-from-target-table feature is pulled into core.
 	 *
 	 * @deprecated Only intended for annotations usage; use {@link #renderWhereStringTemplate(String, String, Dialect, SQLFunctionRegistry)} instead
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public static String renderWhereStringTemplate(String sqlWhereString, String placeholder, Dialect dialect) {
 		return renderWhereStringTemplate( sqlWhereString, placeholder, dialect, new SQLFunctionRegistry( dialect, java.util.Collections.EMPTY_MAP ) );
 	}
 
 	/**
 	 * Takes the where condition provided in the mapping attribute and interpolates the alias.
 	 * Handles sub-selects, quoted identifiers, quoted strings, expressions, SQL functions,
 	 * named parameters.
 	 *
 	 * @param sqlWhereString The string into which to interpolate the placeholder value
 	 * @param placeholder The value to be interpolated into the the sqlWhereString
 	 * @param dialect The dialect to apply
 	 * @param functionRegistry The registry of all sql functions
 	 * @return The rendered sql fragment
 	 */
 	public static String renderWhereStringTemplate(String sqlWhereString, String placeholder, Dialect dialect, SQLFunctionRegistry functionRegistry ) {
 
 		// IMPL NOTE : The basic process here is to tokenize the incoming string and to iterate over each token
 		//		in turn.  As we process each token, we set a series of flags used to indicate the type of context in
 		// 		which the tokens occur.  Depending on the state of those flags we decide whether we need to qualify
 		//		identifier references.
 
 		String symbols = new StringBuilder()
 				.append( "=><!+-*/()',|&`" )
 				.append( StringHelper.WHITESPACE )
 				.append( dialect.openQuote() )
 				.append( dialect.closeQuote() )
 				.toString();
 		StringTokenizer tokens = new StringTokenizer( sqlWhereString, symbols, true );
 		StringBuilder result = new StringBuilder();
 
 		boolean quoted = false;
 		boolean quotedIdentifier = false;
 		boolean beforeTable = false;
 		boolean inFromClause = false;
 		boolean afterFromTable = false;
 
 		boolean hasMore = tokens.hasMoreTokens();
 		String nextToken = hasMore ? tokens.nextToken() : null;
 		while ( hasMore ) {
 			String token = nextToken;
 			String lcToken = token.toLowerCase();
 			hasMore = tokens.hasMoreTokens();
 			nextToken = hasMore ? tokens.nextToken() : null;
 
 			boolean isQuoteCharacter = false;
 
 			if ( !quotedIdentifier && "'".equals(token) ) {
 				quoted = !quoted;
 				isQuoteCharacter = true;
 			}
 
 			if ( !quoted ) {
 				boolean isOpenQuote;
 				if ( "`".equals(token) ) {
 					isOpenQuote = !quotedIdentifier;
 					token = lcToken = isOpenQuote
 							? Character.toString( dialect.openQuote() )
 							: Character.toString( dialect.closeQuote() );
 					quotedIdentifier = isOpenQuote;
 					isQuoteCharacter = true;
 				}
 				else if ( !quotedIdentifier && ( dialect.openQuote()==token.charAt(0) ) ) {
 					isOpenQuote = true;
 					quotedIdentifier = true;
 					isQuoteCharacter = true;
 				}
 				else if ( quotedIdentifier && ( dialect.closeQuote()==token.charAt(0) ) ) {
 					quotedIdentifier = false;
 					isQuoteCharacter = true;
 					isOpenQuote = false;
 				}
 				else {
 					isOpenQuote = false;
 				}
 
 				if ( isOpenQuote ) {
 					result.append( placeholder ).append( '.' );
 				}
 			}
 
 			// Special processing for ANSI SQL EXTRACT function
 			if ( "extract".equals( lcToken ) && "(".equals( nextToken ) ) {
 				final String field = extractUntil( tokens, "from" );
 				final String source = renderWhereStringTemplate(
 						extractUntil( tokens, ")" ),
 						placeholder,
 						dialect,
 						functionRegistry
 				);
 				result.append( "extract(" ).append( field ).append( " from " ).append( source ).append( ')' );
 
 				hasMore = tokens.hasMoreTokens();
 				nextToken = hasMore ? tokens.nextToken() : null;
 
 				continue;
 			}
 
 			// Special processing for ANSI SQL TRIM function
 			if ( "trim".equals( lcToken ) && "(".equals( nextToken ) ) {
 				List<String> operands = new ArrayList<String>();
 				StringBuilder builder = new StringBuilder();
 
 				boolean hasMoreOperands = true;
 				String operandToken = tokens.nextToken();
 				boolean quotedOperand = false;
 				while ( hasMoreOperands ) {
 					final boolean isQuote = "'".equals( operandToken );
 					if ( isQuote ) {
 						quotedOperand = !quotedOperand;
 						if ( !quotedOperand ) {
 							operands.add( builder.append( '\'' ).toString() );
 							builder.setLength( 0 );
 						}
 						else {
 							builder.append( '\'' );
 						}
 					}
 					else if ( quotedOperand ) {
 						builder.append( operandToken );
 					}
 					else if ( operandToken.length() == 1 && Character.isWhitespace( operandToken.charAt( 0 ) ) ) {
 						// do nothing
 					}
 					else {
 						operands.add( operandToken );
 					}
 					operandToken = tokens.nextToken();
 					hasMoreOperands = tokens.hasMoreTokens() && ! ")".equals( operandToken );
 				}
 
 				TrimOperands trimOperands = new TrimOperands( operands );
 				result.append( "trim(" );
 				if ( trimOperands.trimSpec != null ) {
 					result.append( trimOperands.trimSpec ).append( ' ' );
 				}
 				if ( trimOperands.trimChar != null ) {
 					if ( trimOperands.trimChar.startsWith( "'" ) && trimOperands.trimChar.endsWith( "'" ) ) {
 						result.append( trimOperands.trimChar );
 					}
 					else {
 						result.append(
 								renderWhereStringTemplate( trimOperands.trimSpec, placeholder, dialect, functionRegistry )
 						);
 					}
 					result.append( ' ' );
 				}
 				if ( trimOperands.from != null ) {
 					result.append( trimOperands.from ).append( ' ' );
 				}
 				else if ( trimOperands.trimSpec != null || trimOperands.trimChar != null ) {
 					// I think ANSI SQL says that the 'from' is not optional if either trim-spec or trim-char are specified
 					result.append( "from " );
 				}
 
 				result.append( renderWhereStringTemplate( trimOperands.trimSource, placeholder, dialect, functionRegistry ) )
 						.append( ')' );
 
 				hasMore = tokens.hasMoreTokens();
 				nextToken = hasMore ? tokens.nextToken() : null;
 
 				continue;
 			}
 
 			boolean quotedOrWhitespace = quoted || quotedIdentifier || isQuoteCharacter
 					|| Character.isWhitespace( token.charAt(0) );
 
 			if ( quotedOrWhitespace ) {
 				result.append( token );
 			}
 			else if ( beforeTable ) {
 				result.append( token );
 				beforeTable = false;
 				afterFromTable = true;
 			}
 			else if ( afterFromTable ) {
 				if ( !"as".equals(lcToken) ) {
 					afterFromTable = false;
 				}
 				result.append(token);
 			}
 			else if ( isNamedParameter(token) ) {
 				result.append(token);
 			}
 			else if ( isIdentifier(token, dialect)
 					&& !isFunctionOrKeyword(lcToken, nextToken, dialect , functionRegistry) ) {
 				result.append(placeholder)
 						.append('.')
 						.append( dialect.quote(token) );
 			}
 			else {
 				if ( BEFORE_TABLE_KEYWORDS.contains(lcToken) ) {
 					beforeTable = true;
 					inFromClause = true;
 				}
 				else if ( inFromClause && ",".equals(lcToken) ) {
 					beforeTable = true;
 				}
 				result.append(token);
 			}
 
 			//Yuck:
 			if ( inFromClause
 					&& KEYWORDS.contains( lcToken ) //"as" is not in KEYWORDS
 					&& !BEFORE_TABLE_KEYWORDS.contains( lcToken ) ) {
 				inFromClause = false;
 			}
 		}
 
 		return result.toString();
 	}
 
 //	/**
 //	 * Takes the where condition provided in the mapping attribute and interpolates the alias.
 //	 * Handles sub-selects, quoted identifiers, quoted strings, expressions, SQL functions,
 //	 * named parameters.
 //	 *
 //	 * @param sqlWhereString The string into which to interpolate the placeholder value
 //	 * @param placeholder The value to be interpolated into the the sqlWhereString
 //	 * @param dialect The dialect to apply
 //	 * @param functionRegistry The registry of all sql functions
 //	 *
 //	 * @return The rendered sql fragment
 //	 */
 //	public static String renderWhereStringTemplate(
 //			String sqlWhereString,
 //			String placeholder,
 //			Dialect dialect,
 //			SQLFunctionRegistry functionRegistry) {
 //
 //		// IMPL NOTE : The basic process here is to tokenize the incoming string and to iterate over each token
 //		//		in turn.  As we process each token, we set a series of flags used to indicate the type of context in
 //		// 		which the tokens occur.  Depending on the state of those flags we decide whether we need to qualify
 //		//		identifier references.
 //
 //		final String dialectOpenQuote = Character.toString( dialect.openQuote() );
 //		final String dialectCloseQuote = Character.toString( dialect.closeQuote() );
 //
 //		String symbols = new StringBuilder()
 //				.append( "=><!+-*/()',|&`" )
 //				.append( StringHelper.WHITESPACE )
 //				.append( dialect.openQuote() )
 //				.append( dialect.closeQuote() )
 //				.toString();
 //		StringTokenizer tokens = new StringTokenizer( sqlWhereString, symbols, true );
 //		ProcessingState state = new ProcessingState();
 //
 //		StringBuilder quotedBuffer = new StringBuilder();
 //		StringBuilder result = new StringBuilder();
 //
 //		boolean hasMore = tokens.hasMoreTokens();
 //		String nextToken = hasMore ? tokens.nextToken() : null;
 //		while ( hasMore ) {
 //			String token = nextToken;
 //			String lcToken = token.toLowerCase();
 //			hasMore = tokens.hasMoreTokens();
 //			nextToken = hasMore ? tokens.nextToken() : null;
 //
 //			// First, determine quoting which might be based on either:
 //			// 		1) back-tick
 //			// 		2) single quote (ANSI SQL standard)
 //			// 		3) or dialect defined quote character(s)
 //			QuotingCharacterDisposition quotingCharacterDisposition = QuotingCharacterDisposition.NONE;
 //			if ( "`".equals( token ) ) {
 //				state.quoted = !state.quoted;
 //				quotingCharacterDisposition = state.quoted
 //						? QuotingCharacterDisposition.OPEN
 //						: QuotingCharacterDisposition.CLOSE;
 //				// replace token with the appropriate dialect quoting char
 //				token = lcToken = ( quotingCharacterDisposition == QuotingCharacterDisposition.OPEN )
 //						? dialectOpenQuote
 //						: dialectCloseQuote;
 //			}
 //			else if ( "'".equals( token ) ) {
 //				state.quoted = !state.quoted;
 //				quotingCharacterDisposition = state.quoted
 //						? QuotingCharacterDisposition.OPEN
 //						: QuotingCharacterDisposition.CLOSE;
 //			}
 //			else if ( !state.quoted && dialectOpenQuote.equals( token ) ) {
 //				state.quoted = true;
 //				quotingCharacterDisposition = QuotingCharacterDisposition.OPEN;
 //			}
 //			else if ( state.quoted && dialectCloseQuote.equals( token ) ) {
 //				state.quoted = false;
 //				quotingCharacterDisposition = QuotingCharacterDisposition.CLOSE;
 //			}
 //
 //			if ( state.quoted ) {
 //				quotedBuffer.append( token );
 //				continue;
 //			}
 //
 //			// if we were previously processing quoted state and just encountered the close quote, then handle that
 //			// quoted text
 //			if ( quotingCharacterDisposition == QuotingCharacterDisposition.CLOSE ) {
 //				token = quotedBuffer.toString();
 //				quotedBuffer.setLength( 0 );
 //				result.append( placeholder ).append( '.' )
 //						.append( dialectOpenQuote ).append( token ).append( dialectCloseQuote );
 //				continue;
 //			}
 //
 //			// Special processing for ANSI SQL EXTRACT function
 //			if ( "extract".equals( lcToken ) && "(".equals( nextToken ) ) {
 //				final String field = extractUntil( tokens, "from" );
 //				final String source = renderWhereStringTemplate(
 //						extractUntil( tokens, ")" ),
 //						placeholder,
 //						dialect,
 //						functionRegistry
 //				);
 //				result.append( "extract(" ).append( field ).append( " from " ).append( source ).append( ')' );
 //
 //				hasMore = tokens.hasMoreTokens();
 //				nextToken = hasMore ? tokens.nextToken() : null;
 //
 //				continue;
 //			}
 //
 //			// Special processing for ANSI SQL TRIM function
 //			if ( "trim".equals( lcToken ) && "(".equals( nextToken ) ) {
 //				List<String> operands = new ArrayList<String>();
 //				StringBuilder builder = new StringBuilder();
 //
 //				boolean hasMoreOperands = true;
 //				String operandToken = tokens.nextToken();
 //				boolean quoted = false;
 //				while ( hasMoreOperands ) {
 //					final boolean isQuote = "'".equals( operandToken );
 //					if ( isQuote ) {
 //						quoted = !quoted;
 //						if ( !quoted ) {
 //							operands.add( builder.append( '\'' ).toString() );
 //							builder.setLength( 0 );
 //						}
 //						else {
 //							builder.append( '\'' );
 //						}
 //					}
 //					else if ( quoted ) {
 //						builder.append( operandToken );
 //					}
 //					else if ( operandToken.length() == 1 && Character.isWhitespace( operandToken.charAt( 0 ) ) ) {
 //						// do nothing
 //					}
 //					else {
 //						operands.add( operandToken );
 //					}
 //					operandToken = tokens.nextToken();
 //					hasMoreOperands = tokens.hasMoreTokens() && ! ")".equals( operandToken );
 //				}
 //
 //				TrimOperands trimOperands = new TrimOperands( operands );
 //				result.append( "trim(" );
 //				if ( trimOperands.trimSpec != null ) {
 //					result.append( trimOperands.trimSpec ).append( ' ' );
 //				}
 //				if ( trimOperands.trimChar != null ) {
 //					if ( trimOperands.trimChar.startsWith( "'" ) && trimOperands.trimChar.endsWith( "'" ) ) {
 //						result.append( trimOperands.trimChar );
 //					}
 //					else {
 //						result.append(
 //								renderWhereStringTemplate( trimOperands.trimSpec, placeholder, dialect, functionRegistry )
 //						);
 //					}
 //					result.append( ' ' );
 //				}
 //				if ( trimOperands.from != null ) {
 //					result.append( trimOperands.from ).append( ' ' );
 //				}
 //				else if ( trimOperands.trimSpec != null || trimOperands.trimChar != null ) {
 //					// I think ANSI SQL says that the 'from' is not optional if either trim-spec or trim-char are specified
 //					result.append( "from " );
 //				}
 //
 //				result.append( renderWhereStringTemplate( trimOperands.trimSource, placeholder, dialect, functionRegistry ) )
 //						.append( ')' );
 //
 //				hasMore = tokens.hasMoreTokens();
 //				nextToken = hasMore ? tokens.nextToken() : null;
 //
 //				continue;
 //			}
 //
 //
 //			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 //
 //			if ( Character.isWhitespace( token.charAt( 0 ) ) ) {
 //				result.append( token );
 //			}
 //			else if ( state.beforeTable ) {
 //				result.append( token );
 //				state.beforeTable = false;
 //				state.afterFromTable = true;
 //			}
 //			else if ( state.afterFromTable ) {
 //				if ( !"as".equals(lcToken) ) {
 //					state.afterFromTable = false;
 //				}
 //				result.append(token);
 //			}
 //			else if ( isNamedParameter(token) ) {
 //				result.append(token);
 //			}
 //			else if ( isIdentifier(token, dialect)
 //					&& !isFunctionOrKeyword(lcToken, nextToken, dialect , functionRegistry) ) {
 //				result.append(placeholder)
 //						.append('.')
 //						.append( dialect.quote(token) );
 //			}
 //			else {
 //				if ( BEFORE_TABLE_KEYWORDS.contains(lcToken) ) {
 //					state.beforeTable = true;
 //					state.inFromClause = true;
 //				}
 //				else if ( state.inFromClause && ",".equals(lcToken) ) {
 //					state.beforeTable = true;
 //				}
 //				result.append(token);
 //			}
 //
 //			//Yuck:
 //			if ( state.inFromClause
 //					&& KEYWORDS.contains( lcToken ) //"as" is not in KEYWORDS
 //					&& !BEFORE_TABLE_KEYWORDS.contains( lcToken ) ) {
 //				state.inFromClause = false;
 //			}
 //		}
 //
 //		return result.toString();
 //	}
 //
 //	private static class ProcessingState {
 //		boolean quoted = false;
 //		boolean quotedIdentifier = false;
 //		boolean beforeTable = false;
 //		boolean inFromClause = false;
 //		boolean afterFromTable = false;
 //	}
 //
 //	private static enum QuotingCharacterDisposition { NONE, OPEN, CLOSE }
 
 	private static class TrimOperands {
 		private final String trimSpec;
 		private final String trimChar;
 		private final String from;
 		private final String trimSource;
 
 		private TrimOperands(List<String> operands) {
 			if ( operands.size() == 1 ) {
 				trimSpec = null;
 				trimChar = null;
 				from = null;
 				trimSource = operands.get(0);
 			}
 			else if ( operands.size() == 4 ) {
 				trimSpec = operands.get(0);
 				trimChar = operands.get(1);
 				from = operands.get(2);
 				trimSource = operands.get(3);
 			}
 			else {
 				if ( operands.size() < 1 || operands.size() > 4 ) {
 					throw new HibernateException( "Unexpected number of trim function operands : " + operands.size() );
 				}
 
 				// trim-source will always be the last operand
 				trimSource = operands.get( operands.size() - 1 );
 
 				// ANSI SQL says that more than one operand means that the FROM is required
 				if ( ! "from".equals( operands.get( operands.size() - 2 ) ) ) {
 					throw new HibernateException( "Expecting FROM, found : " + operands.get( operands.size() - 2 ) );
 				}
 				from = operands.get( operands.size() - 2 );
 
 				// trim-spec, if there is one will always be the first operand
 				if ( "leading".equalsIgnoreCase( operands.get(0) )
 						|| "trailing".equalsIgnoreCase( operands.get(0) )
 						|| "both".equalsIgnoreCase( operands.get(0) ) ) {
 					trimSpec = operands.get(0);
 					trimChar = null;
 				}
 				else {
 					trimSpec = null;
 					if ( operands.size() - 2 == 0 ) {
 						trimChar = null;
 					}
 					else {
 						trimChar = operands.get( 0 );
 					}
 				}
 			}
 		}
 	}
 
 	private static String extractUntil(StringTokenizer tokens, String delimiter) {
 		StringBuilder valueBuilder = new StringBuilder();
 		String token = tokens.nextToken();
 		while ( ! delimiter.equalsIgnoreCase( token ) ) {
 			valueBuilder.append( token );
 			token = tokens.nextToken();
 		}
 		return valueBuilder.toString().trim();
 	}
 
 	public static class NoOpColumnMapper implements ColumnMapper {
 		public static final NoOpColumnMapper INSTANCE = new NoOpColumnMapper();
-		public String[] map(String reference) {
-			return new String[] { reference };
+		public SqlValueReference[] map(String reference) {
+//			return new String[] { reference };
+			return null;
 		}
 	}
 
 	/**
 	 * Performs order-by template rendering without {@link ColumnMapper column mapping}.  An <tt>ORDER BY</tt> template
 	 * has all column references "qualified" with a placeholder identified by {@link Template#TEMPLATE}
 	 *
 	 * @param orderByFragment The order-by fragment to render.
 	 * @param dialect The SQL dialect being used.
 	 * @param functionRegistry The SQL function registry
 	 *
 	 * @return The rendered <tt>ORDER BY</tt> template.
 	 *
-	 * @deprecated Use {@link #renderOrderByStringTemplate(String,ColumnMapper,SessionFactoryImplementor,Dialect,SQLFunctionRegistry)} instead
+	 * @deprecated Use {@link #translateOrderBy} instead
 	 */
 	@Deprecated
     public static String renderOrderByStringTemplate(
 			String orderByFragment,
 			Dialect dialect,
 			SQLFunctionRegistry functionRegistry) {
 		return renderOrderByStringTemplate(
 				orderByFragment,
 				NoOpColumnMapper.INSTANCE,
 				null,
 				dialect,
 				functionRegistry
 		);
 	}
 
+	public static String renderOrderByStringTemplate(
+			String orderByFragment,
+			final ColumnMapper columnMapper,
+			final SessionFactoryImplementor sessionFactory,
+			final Dialect dialect,
+			final SQLFunctionRegistry functionRegistry) {
+		return translateOrderBy(
+				orderByFragment,
+				columnMapper,
+				sessionFactory,
+				dialect,
+				functionRegistry
+		).injectAliases( LEGACY_ORDER_BY_ALIAS_RESOLVER );
+	}
+
+	public static OrderByAliasResolver LEGACY_ORDER_BY_ALIAS_RESOLVER = new OrderByAliasResolver() {
+		@Override
+		public String resolveTableAlias(String columnReference) {
+			return TEMPLATE;
+		}
+	};
+
 	/**
 	 * Performs order-by template rendering allowing {@link ColumnMapper column mapping}.  An <tt>ORDER BY</tt> template
 	 * has all column references "qualified" with a placeholder identified by {@link Template#TEMPLATE} which can later
 	 * be used to easily inject the SQL alias.
 	 *
 	 * @param orderByFragment The order-by fragment to render.
 	 * @param columnMapper The column mapping strategy to use.
 	 * @param sessionFactory The session factory.
 	 * @param dialect The SQL dialect being used.
 	 * @param functionRegistry The SQL function registry
 	 *
 	 * @return The rendered <tt>ORDER BY</tt> template.
 	 */
-	public static String renderOrderByStringTemplate(
+	public static OrderByTranslation translateOrderBy(
 			String orderByFragment,
 			final ColumnMapper columnMapper,
 			final SessionFactoryImplementor sessionFactory,
 			final Dialect dialect,
 			final SQLFunctionRegistry functionRegistry) {
 		TranslationContext context = new TranslationContext() {
 			public SessionFactoryImplementor getSessionFactory() {
 				return sessionFactory;
 			}
 
 			public Dialect getDialect() {
 				return dialect;
 			}
 
 			public SQLFunctionRegistry getSqlFunctionRegistry() {
 				return functionRegistry;
 			}
 
 			public ColumnMapper getColumnMapper() {
 				return columnMapper;
 			}
 		};
 
-		OrderByFragmentTranslator translator = new OrderByFragmentTranslator( context );
-		return translator.render( orderByFragment );
+		return OrderByFragmentTranslator.translate( context, orderByFragment );
 	}
 
 	private static boolean isNamedParameter(String token) {
 		return token.startsWith(":");
 	}
 
 	private static boolean isFunctionOrKeyword(String lcToken, String nextToken, Dialect dialect, SQLFunctionRegistry functionRegistry) {
 		return "(".equals(nextToken) ||
 			KEYWORDS.contains(lcToken) ||
 			isFunction(lcToken, nextToken, functionRegistry ) ||
 			dialect.getKeywords().contains(lcToken) ||
 			FUNCTION_KEYWORDS.contains(lcToken);
 	}
 
 	private static boolean isFunction(String lcToken, String nextToken, SQLFunctionRegistry functionRegistry) {
 		// checking for "(" is currently redundant because it is checked before getting here;
 		// doing the check anyhow, in case that earlier check goes away;
 		if ( "(".equals( nextToken ) ) {
 			return true;
 		}
 		SQLFunction function = functionRegistry.findSQLFunction(lcToken);
 		if ( function == null ) {
 			// lcToken does not refer to a function
 			return false;
 		}
 		// if function.hasParenthesesIfNoArguments() is true, then assume
 		// lcToken is not a function (since it is not followed by '(')
 		return ! function.hasParenthesesIfNoArguments();
 	}
 
 	private static boolean isIdentifier(String token, Dialect dialect) {
 		return token.charAt(0)=='`' || ( //allow any identifier quoted with backtick
 			Character.isLetter( token.charAt(0) ) && //only recognizes identifiers beginning with a letter
 			token.indexOf('.') < 0
 		);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java
index ca5b8f95aa..07e824f944 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/CollationSpecification.java
@@ -1,35 +1,33 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
 
-
 /**
  * Models a collation specification (<tt>COLLATE</tt> using a specific character-set) within a
  * {@link SortSpecification}.
  *
  * @author Steve Ebersole
  */
 public class CollationSpecification extends NodeSupport {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java
index 3363f52f40..e5f0e63b47 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnMapper.java
@@ -1,45 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
+
 import org.hibernate.HibernateException;
 
 /**
  * Contract for mapping a (an assumed) property reference to its columns.
  *
  * @author Steve Ebersole
  */
 public interface ColumnMapper {
 	/**
 	 * Resolve the property reference to its underlying columns.
 	 *
 	 * @param reference The property reference name.
 	 *
-	 * @return The underlying columns, or null if the property reference is unknown.
+	 * @return References to the columns/formulas that define the value mapping for the given property, or null
+	 * if the property reference is unknown.
 	 *
-	 * @throws HibernateException Generally indicates that the property reference is unkown; interpretation is the
-	 * same as a null return.
+	 * @throws HibernateException Generally indicates that the property reference is unknown; interpretation
+	 * should be the same as a null return.
 	 */
-	public String[] map(String reference) throws HibernateException;
+	public SqlValueReference[] map(String reference) throws HibernateException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnReference.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnReference.java
new file mode 100644
index 0000000000..881f2b7eca
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/ColumnReference.java
@@ -0,0 +1,38 @@
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
+package org.hibernate.sql.ordering.antlr;
+
+/**
+ * Reference to a column name.
+ *
+ * @author Steve Ebersole
+ */
+public interface ColumnReference extends SqlValueReference {
+	/**
+	 * Retrieve the column name.
+	 *
+	 * @return THe column name
+	 */
+	public String getColumnName();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java
index 75a089ace6..597c781c75 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Factory.java
@@ -1,53 +1,51 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
+
 import antlr.ASTFactory;
 
 /**
  * Acts as a {@link ASTFactory} for injecting our specific AST node classes into the Antlr generated trees.
  *
  * @author Steve Ebersole
  */
 public class Factory extends ASTFactory implements OrderByTemplateTokenTypes {
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class getASTNodeType(int i) {
 		switch ( i ) {
 			case ORDER_BY:
 				return OrderByFragment.class;
 			case SORT_SPEC:
 				return SortSpecification.class;
 			case ORDER_SPEC:
 				return OrderingSpecification.class;
 			case COLLATE:
 				return CollationSpecification.class;
 			case SORT_KEY:
 				return SortKey.class;
 			default:
 				return NodeSupport.class;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/FormulaReference.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/FormulaReference.java
new file mode 100644
index 0000000000..2fa29889ec
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/FormulaReference.java
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
+package org.hibernate.sql.ordering.antlr;
+
+/**
+ * Reference to a formula fragment.
+ *
+ * @author Steve Ebersole
+ */
+public interface FormulaReference extends SqlValueReference {
+	/**
+	 * Retrieve the formula fragment.  It is important to note that this is what the persister calls the
+	 * "formula template", which has the $PlaceHolder$ (see {@link org.hibernate.sql.Template#TEMPLATE})
+	 * markers injected.
+	 *
+	 * @return The formula fragment template.
+	 */
+	public String getFormulaFragment();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java
index d64ef26151..7a55af771b 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/Node.java
@@ -1,54 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
 
-
 /**
  * General contract for AST nodes.
  *
  * @author Steve Ebersole
  */
 public interface Node {
 	/**
 	 * Get the intrinsic text of this node.
 	 *
 	 * @return The node's text.
 	 */
 	public String getText();
 
 	/**
 	 * Get a string representation of this node usable for debug logging or similar.
 	 *
 	 * @return The node's debugging text.
 	 */
 	public String getDebugText();
 
 	/**
 	 * Build the node's representation for use in the resulting rendering.
 	 *
-	 * @return The renderable text.
+	 * @return The text for use in the translated output.
 	 */
 	public String getRenderableText();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java
index 3d6737d1f9..c789c3b9ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/NodeSupport.java
@@ -1,47 +1,43 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
+
 import antlr.CommonAST;
 
 /**
- * Basic implementation of a {@link Node}.
+ * Basic implementation of a {@link Node} briding to the Antlr {@link CommonAST} hierarchy.
  *
  * @author Steve Ebersole
  */
 public class NodeSupport extends CommonAST implements Node {
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getDebugText() {
 		return getText();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public String getRenderableText() {
 		return getText();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByAliasResolver.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByAliasResolver.java
new file mode 100644
index 0000000000..0ff27e7ae2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByAliasResolver.java
@@ -0,0 +1,35 @@
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
+package org.hibernate.sql.ordering.antlr;
+
+/**
+ * Given a column reference, resolve the table alias to apply to the column to qualify it.
+ */
+public interface OrderByAliasResolver {
+	/**
+	 * Given a column reference, resolve the table alias to apply to the column to qualify it.
+	 *
+	 */
+	public String resolveTableAlias(String columnReference);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java
index a9f586b578..509f723a67 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragment.java
@@ -1,34 +1,32 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
 
-
 /**
  * Represents a parsed <tt>order-by</tt> mapping fragment.  This holds the tree of all {@link SortSpecification}s.
  *
  * @author Steve Ebersole
  */
 public class OrderByFragment extends NodeSupport {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
index b1798fa1ed..90fd42fe37 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentParser.java
@@ -1,241 +1,334 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
 
 import java.util.ArrayList;
+import java.util.HashSet;
+import java.util.Set;
+import java.util.regex.Matcher;
+import java.util.regex.Pattern;
 
 import antlr.CommonAST;
 import antlr.TokenStream;
 import antlr.collections.AST;
+
 import org.jboss.logging.Logger;
 
 import org.hibernate.dialect.function.SQLFunction;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.Template;
 
 /**
- * Extension of the Antlr-generated parser for the purpose of adding our custom parsing behavior.
+ * Extension of the Antlr-generated parser for the purpose of adding our custom parsing behavior
+ * (semantic analysis, etc).
  *
  * @author Steve Ebersole
  */
 public class OrderByFragmentParser extends GeneratedOrderByFragmentParser {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, OrderByFragmentParser.class.getName());
+    private static final Logger LOG = Logger.getLogger(OrderByFragmentParser.class.getName());
 
 	private final TranslationContext context;
 
+	private Set<String> columnReferences = new HashSet<String>();
+
 	public OrderByFragmentParser(TokenStream lexer, TranslationContext context) {
 		super( lexer );
 		super.setASTFactory( new Factory() );
 		this.context = context;
 	}
 
-
-	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-    private int traceDepth = 0;
-
-
-	@Override
-    public void traceIn(String ruleName) {
-		if ( inputState.guessing > 0 ) {
-			return;
-		}
-		String prefix = StringHelper.repeat( '-', (traceDepth++ * 2) ) + "-> ";
-        LOG.trace(prefix + ruleName);
-	}
-
-	@Override
-    public void traceOut(String ruleName) {
-		if ( inputState.guessing > 0 ) {
-			return;
-		}
-		String prefix = "<-" + StringHelper.repeat( '-', (--traceDepth * 2) ) + " ";
-        LOG.trace(prefix + ruleName);
+	public Set<String> getColumnReferences() {
+		return columnReferences;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
-	@Override
-	protected void trace(String msg) {
-		LOG.trace( msg );
-	}
-
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected AST quotedIdentifier(AST ident) {
-		return getASTFactory().create(
-				OrderByTemplateTokenTypes.IDENT,
-				Template.TEMPLATE + "." + context.getDialect().quote( '`' + ident.getText() + '`' )
-		);
+		/*
+		 * Semantic action used during recognition of quoted identifiers (quoted column names)
+		 */
+		final String columnName = context.getDialect().quote( '`' + ident.getText() + '`' );
+		columnReferences.add( columnName );
+		final String marker = '{' + columnName + '}';
+		return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, marker );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
     protected AST quotedString(AST ident) {
+		/*
+		 * Semantic action used during recognition of quoted strings (string literals)
+		 */
 		return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, context.getDialect().quote( ident.getText() ) );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
-    protected boolean isFunctionName(AST ast) {
+	@SuppressWarnings("SimplifiableIfStatement")
+	protected boolean isFunctionName(AST ast) {
+		/*
+		 * Semantic predicate used to determine whether a given AST node represents a function call
+		 */
+
 		AST child = ast.getFirstChild();
 		// assume it is a function if it has parameters
 		if ( child != null && "{param list}".equals( child.getText() ) ) {
 			return true;
 		}
 
+		// otherwise, in order for this to be a function logically it has to be a function that does not
+		// have arguments.  So try to assert that using the registry of known functions
 		final SQLFunction function = context.getSqlFunctionRegistry().findSQLFunction( ast.getText() );
 		if ( function == null ) {
+			// no registered function, so we cannot know for certain
 			return false;
 		}
-
-		// if function.hasParenthesesIfNoArguments() is true, then assume
-		// ast.getText() is not a function.
-		return ! function.hasParenthesesIfNoArguments();
+		else {
+			// if function.hasParenthesesIfNoArguments() is true, then assume the node is not a function
+			return ! function.hasParenthesesIfNoArguments();
+		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@SuppressWarnings("unchecked")
 	@Override
     protected AST resolveFunction(AST ast) {
+		/*
+		 * Semantic action used during recognition of a *known* function
+		 */
 		AST child = ast.getFirstChild();
 		if ( child != null ) {
 			assert "{param list}".equals(  child.getText() );
 			child = child.getFirstChild();
 		}
 
 		final String functionName = ast.getText();
 		final SQLFunction function = context.getSqlFunctionRegistry().findSQLFunction( functionName );
 		if ( function == null ) {
 			String text = functionName;
 			if ( child != null ) {
 				text += '(';
 				while ( child != null ) {
-					text += child.getText();
+					text += resolveFunctionArgument( child );
 					child = child.getNextSibling();
 					if ( child != null ) {
 						text += ", ";
 					}
 				}
 				text += ')';
 			}
 			return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, text );
 		}
 		else {
 			ArrayList expressions = new ArrayList();
 			while ( child != null ) {
-				expressions.add( child.getText() );
+				expressions.add( resolveFunctionArgument( child ) );
 				child = child.getNextSibling();
 			}
 			final String text = function.render( null, expressions, context.getSessionFactory() );
 			return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, text );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	private String resolveFunctionArgument(AST argumentNode) {
+		final String nodeText = argumentNode.getText();
+		final String adjustedText;
+		if ( nodeText.contains( Template.TEMPLATE ) ) {
+			// we have a SQL order-by fragment
+			adjustedText = adjustTemplateReferences( nodeText );
+		}
+		else if ( nodeText.startsWith( "{" ) && nodeText.endsWith( "}" ) ) {
+			columnReferences.add( nodeText.substring( 1, nodeText.length() - 1 ) );
+			return nodeText;
+		}
+		else {
+			adjustedText = nodeText;
+			// because we did not process the node text, we need to attempt to find any column references
+			// contained in it.
+			// NOTE : uses regex for the time being; we should check the performance of this
+			Pattern pattern = Pattern.compile( "\\{(.*)\\}" );
+			Matcher matcher = pattern.matcher( adjustedText );
+			while ( matcher.find() ) {
+				columnReferences.add( matcher.group( 1 ) );
+			}
+		}
+		return adjustedText;
+	}
+
 	@Override
     protected AST resolveIdent(AST ident) {
+		/*
+		 * Semantic action used during recognition of an identifier.  This identifier might be a column name, it might
+		 * be a property name.
+		 */
 		String text = ident.getText();
-		String[] replacements;
+		SqlValueReference[] sqlValueReferences;
 		try {
-			replacements = context.getColumnMapper().map( text );
+			sqlValueReferences = context.getColumnMapper().map( text );
 		}
 		catch( Throwable t ) {
-			replacements = null;
+			sqlValueReferences = null;
 		}
 
-		if ( replacements == null || replacements.length == 0 ) {
-			return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, Template.TEMPLATE + "." + text );
+		if ( sqlValueReferences == null || sqlValueReferences.length == 0 ) {
+			return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, makeColumnReference( text ) );
 		}
-		else if ( replacements.length == 1 ) {
-			return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, Template.TEMPLATE + "." + replacements[0] );
+		else if ( sqlValueReferences.length == 1 ) {
+			return processSqlValueReference( sqlValueReferences[0] );
 		}
 		else {
 			final AST root = getASTFactory().create( OrderByTemplateTokenTypes.IDENT_LIST, "{ident list}" );
-			for ( int i = 0; i < replacements.length; i++ ) {
-				final String identText = Template.TEMPLATE + '.' + replacements[i];
-				root.addChild( getASTFactory().create( OrderByTemplateTokenTypes.IDENT, identText ) );
+			for ( SqlValueReference sqlValueReference : sqlValueReferences ) {
+				root.addChild( processSqlValueReference( sqlValueReference ) );
 			}
 			return root;
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	private AST processSqlValueReference(SqlValueReference sqlValueReference) {
+		if ( ColumnReference.class.isInstance( sqlValueReference ) ) {
+			final String columnName = ( (ColumnReference) sqlValueReference ).getColumnName();
+			return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, makeColumnReference( columnName ) );
+		}
+		else {
+			final String formulaFragment = ( (FormulaReference) sqlValueReference ).getFormulaFragment();
+			// formulas have already been "adjusted" for aliases by appending Template.TEMPLATE to places
+			// where we believe column references are.  Fixing that is beyond scope of this work.  But we need
+			// to re-adjust that to use the order-by expectation of wrapping the column names in curly
+			// braces (i.e., `{column_name}`).
+			final String adjustedText = adjustTemplateReferences( formulaFragment );
+			return getASTFactory().create( OrderByTemplateTokenTypes.IDENT, adjustedText );
+		}
+	}
+
+	private String makeColumnReference(String text) {
+		columnReferences.add( text );
+		return "{" + text + "}";
+	}
+
+	private static final int TEMPLATE_MARKER_LENGTH = Template.TEMPLATE.length();
+
+	private String adjustTemplateReferences(String template) {
+		int templateLength = template.length();
+		int startPos = template.indexOf( Template.TEMPLATE );
+		while ( startPos < templateLength ) {
+			int dotPos = startPos + TEMPLATE_MARKER_LENGTH;
+
+			// from here we need to seek the end of the qualified identifier
+			int pos = dotPos + 1;
+			while ( pos < templateLength && isValidIdentifierCharacter( template.charAt( pos ) ) ) {
+				pos++;
+			}
+
+			// At this point we know all 3 points in the template that are needed for replacement.
+			// Basically we will be replacing the whole match with the bit following the dot, but will wrap
+			// the replacement in curly braces.
+			final String columnReference = template.substring( dotPos + 1, pos );
+			final String replacement = "{" + columnReference + "}";
+			template = template.replace( template.substring( startPos, pos ), replacement );
+			columnReferences.add( columnReference );
+
+			// prep for the next seek
+			startPos = ( pos - TEMPLATE_MARKER_LENGTH ) + 1;
+			templateLength = template.length();
+		}
+
+		return template;
+	}
+
+	private static boolean isValidIdentifierCharacter(char c) {
+		return Character.isLetter( c )
+				|| Character.isDigit( c )
+				|| '_' == c
+				|| '\"' == c;
+	}
+
 	@Override
     protected AST postProcessSortSpecification(AST sortSpec) {
 		assert SORT_SPEC == sortSpec.getType();
 		SortSpecification sortSpecification = ( SortSpecification ) sortSpec;
 		AST sortKey = sortSpecification.getSortKey();
 		if ( IDENT_LIST == sortKey.getFirstChild().getType() ) {
 			AST identList = sortKey.getFirstChild();
 			AST ident = identList.getFirstChild();
 			AST holder = new CommonAST();
 			do {
 				holder.addChild(
 						createSortSpecification(
 								ident,
 								sortSpecification.getCollation(),
 								sortSpecification.getOrdering()
 						)
 				);
 				ident = ident.getNextSibling();
 			} while ( ident != null );
 			sortSpec = holder.getFirstChild();
 		}
 		return sortSpec;
 	}
 
 	private SortSpecification createSortSpecification(
 			AST ident,
 			CollationSpecification collationSpecification,
 			OrderingSpecification orderingSpecification) {
 		AST sortSpecification = getASTFactory().create( SORT_SPEC, "{{sort specification}}" );
 		AST sortKey = getASTFactory().create( SORT_KEY, "{{sort key}}" );
 		AST newIdent = getASTFactory().create( ident.getType(), ident.getText() );
 		sortKey.setFirstChild( newIdent );
 		sortSpecification.setFirstChild( sortKey );
 		if ( collationSpecification != null ) {
 			sortSpecification.addChild( collationSpecification );
 		}
 		if ( orderingSpecification != null ) {
 			sortSpecification.addChild( orderingSpecification );
 		}
 		return ( SortSpecification ) sortSpecification;
 	}
+
+
+
+	// trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private int traceDepth = 0;
+
+
+	@Override
+	public void traceIn(String ruleName) {
+		if ( inputState.guessing > 0 ) {
+			return;
+		}
+		String prefix = StringHelper.repeat( '-', (traceDepth++ * 2) ) + "-> ";
+		LOG.trace(prefix + ruleName);
+	}
+
+	@Override
+	public void traceOut(String ruleName) {
+		if ( inputState.guessing > 0 ) {
+			return;
+		}
+		String prefix = "<-" + StringHelper.repeat( '-', (--traceDepth * 2) ) + " ";
+		LOG.trace(prefix + ruleName);
+	}
+
+	@Override
+	protected void trace(String msg) {
+		LOG.trace( msg );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
index 42517a1232..8544632c46 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentRenderer.java
@@ -1,77 +1,78 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
+
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 /**
- * TODO : javadoc
+ * Extension of the Antlr-generated tree walker for rendering the parsed order-by tree back to String form.
+ * {@link #out(antlr.collections.AST)} is the sole semantic action here and it is used to utilize our
+ * split between text (tree debugging text) and "renderable text" (text to use during rendering).
  *
  * @author Steve Ebersole
  */
 public class OrderByFragmentRenderer extends GeneratedOrderByFragmentRenderer {
 
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, OrderByFragmentRenderer.class.getName() );
+	private static final Logger LOG = Logger.getLogger( OrderByFragmentRenderer.class.getName() );
 	private static final ASTPrinter printer = new ASTPrinter( GeneratedOrderByFragmentRendererTokenTypes.class );
 
 	@Override
     protected void out(AST ast) {
 		out( ( ( Node ) ast ).getRenderableText() );
 	}
 
 
 	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
     private int traceDepth = 0;
 
 	@Override
     public void traceIn(String ruleName, AST tree) {
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = StringHelper.repeat( '-', (traceDepth++ * 2) ) + "-> ";
 		String traceText = ruleName + " (" + buildTraceNodeName(tree) + ")";
 		LOG.trace( prefix + traceText );
 	}
 
 	private String buildTraceNodeName(AST tree) {
 		return tree == null
 				? "???"
 				: tree.getText() + " [" + printer.getTokenTypeName( tree.getType() ) + "]";
 	}
 
 	@Override
     public void traceOut(String ruleName, AST tree) {
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = "<-" + StringHelper.repeat( '-', (--traceDepth * 2) ) + " ";
 		LOG.trace( prefix + ruleName );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
index db6137ec21..ec88066bd1 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByFragmentTranslator.java
@@ -1,87 +1,114 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
+
 import java.io.StringReader;
+import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
-import org.hibernate.internal.CoreMessageLogger;
 
 /**
- * A translator which coordinates translation of an <tt>order-by</tt> mapping.
+ * A translator for order-by mappings, whether specified by hbm.xml files, Hibernate
+ * {@link org.hibernate.annotations.OrderBy} annotation or JPA {@link javax.persistence.OrderBy} annotation.
  *
  * @author Steve Ebersole
  */
 public class OrderByFragmentTranslator {
-
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, OrderByFragmentTranslator.class.getName() );
-
-	public final TranslationContext context;
-
-	public OrderByFragmentTranslator(TranslationContext context) {
-		this.context = context;
-	}
+	private static final Logger LOG = Logger.getLogger( OrderByFragmentTranslator.class.getName() );
 
 	/**
-	 * The main contract, performing the transaction.
+	 * Perform the translation of the user-supplied fragment, returning the translation.
+	 * <p/>
+	 * The important distinction to this split between (1) translating and (2) resolving aliases is that
+	 * both happen at different times
+	 *
 	 *
-	 * @param fragment The <tt>order-by</tt> mapping fragment to be translated.
+	 * @param context Context giving access to delegates needed during translation.
+	 * @param fragment The user-supplied order-by fragment
 	 *
-	 * @return The translated fragment.
+	 * @return The translation.
 	 */
-	public String render(String fragment) {
+	public static OrderByTranslation translate(TranslationContext context, String fragment) {
 		GeneratedOrderByLexer lexer = new GeneratedOrderByLexer( new StringReader( fragment ) );
+
+		// Perform the parsing (and some analysis/resolution).  Another important aspect is the collection
+		// of "column references" which are important later to seek out replacement points in the
+		// translated fragment.
 		OrderByFragmentParser parser = new OrderByFragmentParser( lexer, context );
 		try {
 			parser.orderByFragment();
 		}
 		catch ( HibernateException e ) {
 			throw e;
 		}
 		catch ( Throwable t ) {
 			throw new HibernateException( "Unable to parse order-by fragment", t );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			ASTPrinter printer = new ASTPrinter( OrderByTemplateTokenTypes.class );
 			LOG.trace( printer.showAsString( parser.getAST(), "--- {order-by fragment} ---" ) );
 		}
 
+		// Render the parsed tree to text.
 		OrderByFragmentRenderer renderer = new OrderByFragmentRenderer();
 		try {
 			renderer.orderByFragment( parser.getAST() );
 		}
 		catch ( HibernateException e ) {
 			throw e;
 		}
 		catch ( Throwable t ) {
 			throw new HibernateException( "Unable to render parsed order-by fragment", t );
 		}
 
-		return renderer.getRenderedFragment();
+		return new StandardOrderByTranslationImpl( renderer.getRenderedFragment(), parser.getColumnReferences() );
+	}
+
+	public static class StandardOrderByTranslationImpl implements OrderByTranslation {
+		private final String sqlTemplate;
+		private final Set<String> columnReferences;
+
+		public StandardOrderByTranslationImpl(String sqlTemplate, Set<String> columnReferences) {
+			this.sqlTemplate = sqlTemplate;
+			this.columnReferences = columnReferences;
+		}
+
+		@Override
+		public String injectAliases(OrderByAliasResolver aliasResolver) {
+			String sql = sqlTemplate;
+			for ( String columnReference : columnReferences ) {
+				final String replacementToken = "{" + columnReference + "}";
+				sql = sql.replace(
+						replacementToken,
+						aliasResolver.resolveTableAlias( columnReference ) + '.' + columnReference
+				);
+			}
+			return sql;
+		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByTranslation.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByTranslation.java
new file mode 100644
index 0000000000..431c54ebad
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderByTranslation.java
@@ -0,0 +1,41 @@
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
+package org.hibernate.sql.ordering.antlr;
+
+/**
+ * Represents the result of an order-by translation by {@link @OrderByTranslator}
+ *
+ * @author Steve Ebersole
+ */
+public interface OrderByTranslation {
+	/**
+	 * Inject table aliases into the translated fragment to properly qualify column references, using
+	 * the given 'aliasResolver' to determine the the proper table alias to use for each column reference.
+	 *
+	 * @param aliasResolver The strategy to resolver the proper table alias to use per column
+	 *
+	 * @return The fully translated and replaced fragment.
+	 */
+	public String injectAliases(OrderByAliasResolver aliasResolver);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java
index 4aa56a1135..7036a97ab0 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/OrderingSpecification.java
@@ -1,71 +1,69 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
 
-
 /**
  * Models an ordering specification (<tt>ASCENDING</tt> or <tt>DESCENDING</tt>) within a {@link SortSpecification}.
  *
  * @author Steve Ebersole
  */
 public class OrderingSpecification extends NodeSupport {
 	public static class Ordering {
 		public static final Ordering ASCENDING = new Ordering( "asc" );
 		public static final Ordering DESCENDING = new Ordering( "desc" );
 
 		private final String name;
 
 		private Ordering(String name) {
 			this.name = name;
 		}
 	}
 
 	private boolean resolved;
 	private Ordering ordering;
 
 	public Ordering getOrdering() {
 		if ( !resolved ) {
 			ordering = resolve( getText() );
 			resolved = true;
 		}
 		return ordering;
 	}
 
 	private static Ordering resolve(String text) {
 		if ( Ordering.ASCENDING.name.equals( text ) ) {
 			return Ordering.ASCENDING;
 		}
 		else if ( Ordering.DESCENDING.name.equals( text ) ) {
 			return Ordering.DESCENDING;
 		}
 		else {
 			throw new IllegalStateException( "Unknown ordering [" + text + "]" );
 		}
 	}
 
 	public String getRenderableText() {
 		return getOrdering().name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java
index cb6ea2240f..2f7e4c4320 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortKey.java
@@ -1,35 +1,34 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
 
 
 /**
  * Models the container node for the <tt>sort key</tt>, which is the term given by the ANSI SQL specification to the
  * expression upon which to sort for each {@link SortSpecification}
  *
  * @author Steve Ebersole
  */
 public class SortKey extends NodeSupport {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java
index 228ae6e811..e94db21364 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SortSpecification.java
@@ -1,79 +1,79 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
+
 import antlr.collections.AST;
 
 /**
- * Models each sorting exprersion.
+ * Models each sorting expression.
  *
  * @author Steve Ebersole
  */
 public class SortSpecification extends NodeSupport {
 	/**
 	 * Locate the specified {@link SortKey}.
 	 *
 	 * @return The sort key.
 	 */
 	public SortKey getSortKey() {
 		return ( SortKey ) getFirstChild();
 	}
 
 	/**
 	 * Locate the specified <tt>collation specification</tt>, if one.
 	 *
 	 * @return The <tt>collation specification</tt>, or null if none was specified.
 	 */
 	public CollationSpecification getCollation() {
 		AST possible = getSortKey().getNextSibling();
 		return  possible != null && OrderByTemplateTokenTypes.COLLATE == possible.getType()
 				? ( CollationSpecification ) possible
 				: null;
 	}
 
 	/**
 	 * Locate the specified <tt>ordering specification</tt>, if one.
 	 *
 	 * @return The <tt>ordering specification</tt>, or null if none was specified.
 	 */
 	public OrderingSpecification getOrdering() {
 		// IMPL NOTE : the ordering-spec would be either the 2nd or 3rd child (of the overall sort-spec), if it existed,
 		// 		depending on whether a collation-spec was specified.
 
 		AST possible = getSortKey().getNextSibling();
 		if ( possible == null ) {
 			// There was no sort-spec parts specified other then the sort-key so there can be no ordering-spec...
 			return null;
 		}
 
 		if ( OrderByTemplateTokenTypes.COLLATE == possible.getType() ) {
 			// the 2nd child was a collation-spec, so we need to check the 3rd child instead.
 			possible = possible.getNextSibling();
 		}
 
 		return possible != null && OrderByTemplateTokenTypes.ORDER_SPEC == possible.getType()
 				?  ( OrderingSpecification ) possible
 				:  null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SqlValueReference.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SqlValueReference.java
new file mode 100644
index 0000000000..6f656825cb
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/SqlValueReference.java
@@ -0,0 +1,36 @@
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
+package org.hibernate.sql.ordering.antlr;
+
+/**
+ * Unifying interface between column and formula references mainly to give more strictly typed result
+ * to {@link ColumnMapper#map(String)}
+ *
+ * @see ColumnReference
+ * @see FormulaReference
+ *
+ * @author Steve Ebersole
+ */
+public interface SqlValueReference {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
index 2003e5d17c..468f23eedb 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/ordering/antlr/TranslationContext.java
@@ -1,63 +1,63 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate.sql.ordering.antlr;
+
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * Contract for contextual information required to perform translation.
 *
 * @author Steve Ebersole
 */
 public interface TranslationContext {
 	/**
 	 * Retrieves the <tt>session factory</tt> for this context.
 	 *
 	 * @return The <tt>session factory</tt>
 	 */
 	public SessionFactoryImplementor getSessionFactory();
 
 	/**
 	 * Retrieves the <tt>dialect</tt> for this context.
 	 *
 	 * @return The <tt>dialect</tt>
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Retrieves the <tt>SQL function registry/tt> for this context.
 	 *
 	 * @return The SQL function registry.
 	 */
 	public SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieves the <tt>column mapper</tt> for this context.
 	 *
 	 * @return The <tt>column mapper</tt>
 	 */
 	public ColumnMapper getColumnMapper();
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/sql/TemplateTest.java b/hibernate-core/src/test/java/org/hibernate/sql/TemplateTest.java
index bb559d0523..80a69ce7e3 100644
--- a/hibernate-core/src/test/java/org/hibernate/sql/TemplateTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/sql/TemplateTest.java
@@ -1,234 +1,249 @@
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
 package org.hibernate.sql;
 
 import java.util.Collections;
 
 import org.junit.Test;
 
 import org.hibernate.QueryException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import org.hibernate.sql.ordering.antlr.ColumnReference;
+import org.hibernate.sql.ordering.antlr.SqlValueReference;
 import org.hibernate.type.Type;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
 public class TemplateTest extends BaseUnitTestCase {
 	private static final PropertyMapping PROPERTY_MAPPING = new PropertyMapping() {
 		public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
 			if ( "sql".equals( propertyName ) ) {
 				return new String[] { "sql" };
 			}
 			else if ( "component".equals( propertyName ) ) {
 				return new String[] { "comp_1", "comp_2" };
 			}
 			else if ( "component.prop1".equals( propertyName ) ) {
 				return new String[] { "comp_1" };
 			}
 			else if ( "component.prop2".equals( propertyName ) ) {
 				return new String[] { "comp_2" };
 			}
 			else if ( "property".equals( propertyName ) ) {
 				return new String[] { "prop" };
 			}
 			throw new QueryException( "could not resolve property: " + propertyName );
 		}
 
 		public Type toType(String propertyName) throws QueryException {
 			return null;
 		}
 
 		public String[] toColumns(String alias, String propertyName) throws QueryException {
 			return new String[0];
 		}
 
 		public Type getType() {
 			return null;
 		}
 	};
 
 	private static final ColumnMapper MAPPER = new ColumnMapper() {
-		public String[] map(String reference) {
-			return PROPERTY_MAPPING.toColumns( reference );
+		public SqlValueReference[] map(String reference) {
+			final String[] columnNames = PROPERTY_MAPPING.toColumns( reference );
+			final SqlValueReference[] result = new SqlValueReference[ columnNames.length ];
+			int i = 0;
+			for ( final String columnName : columnNames ) {
+				result[i] = new ColumnReference() {
+					@Override
+					public String getColumnName() {
+						return columnName;
+					}
+				};
+				i++;
+			}
+			return result;
 		}
-	};
+ 	};
 
 	private static final Dialect DIALECT = new HSQLDialect();
 
 	private static final SQLFunctionRegistry FUNCTION_REGISTRY = new SQLFunctionRegistry( DIALECT, Collections.EMPTY_MAP );
 
 	@Test
 	public void testSqlExtractFunction() {
 		String fragment = "extract( year from col )";
 		String template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 
 		assertEquals( "extract(year from " + Template.TEMPLATE + ".col)", template );
 	}
 
 	@Test
 	public void testSqlTrimFunction() {
 		String fragment = "trim( col )";
 		String template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 		assertEquals( "trim(" + Template.TEMPLATE + ".col)", template );
 
 		fragment = "trim( from col )";
 		template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 		assertEquals( "trim(from " + Template.TEMPLATE + ".col)", template );
 
 		fragment = "trim( both from col )";
 		template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 		assertEquals( "trim(both from " + Template.TEMPLATE + ".col)", template );
 
 		fragment = "trim( leading from col )";
 		template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 		assertEquals( "trim(leading from " + Template.TEMPLATE + ".col)", template );
 
 		fragment = "trim( TRAILING from col )";
 		template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 		assertEquals( "trim(TRAILING from " + Template.TEMPLATE + ".col)", template );
 
 		fragment = "trim( 'b' from col )";
 		template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 		assertEquals( "trim('b' from " + Template.TEMPLATE + ".col)", template );
 
 		fragment = "trim( both 'b' from col )";
 		template = Template.renderWhereStringTemplate( fragment, Template.TEMPLATE, DIALECT, FUNCTION_REGISTRY );
 		assertEquals( "trim(both 'b' from " + Template.TEMPLATE + ".col)", template );
 	}
 
 	@Test
 	public void testSQLReferences() {
 		String fragment = "sql asc, sql desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( Template.TEMPLATE + ".sql asc, " + Template.TEMPLATE + ".sql desc", template );
 	}
 
 	@Test
 	public void testQuotedSQLReferences() {
 		String fragment = "`sql` asc, `sql` desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( Template.TEMPLATE + ".\"sql\" asc, " + Template.TEMPLATE + ".\"sql\" desc", template );
 	}
 
 	@Test
 	public void testPropertyReference() {
 		String fragment = "property asc, property desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( Template.TEMPLATE + ".prop asc, " + Template.TEMPLATE + ".prop desc", template );
 	}
 
 	@Test
 	public void testFunctionReference() {
 		String fragment = "upper(sql) asc, lower(sql) desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( "upper(" + Template.TEMPLATE + ".sql) asc, lower(" + Template.TEMPLATE + ".sql) desc", template );
 	}
 
 	@Test
 	public void testQualifiedFunctionReference() {
 		String fragment = "qual.upper(property) asc, qual.lower(property) desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( "qual.upper(" + Template.TEMPLATE + ".prop) asc, qual.lower(" + Template.TEMPLATE + ".prop) desc", template );
 	}
 
 	@Test
 	public void testDoubleQualifiedFunctionReference() {
 		String fragment = "qual1.qual2.upper(property) asc, qual1.qual2.lower(property) desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( "qual1.qual2.upper(" + Template.TEMPLATE + ".prop) asc, qual1.qual2.lower(" + Template.TEMPLATE + ".prop) desc", template );
 	}
 
 	@Test
 	public void testFunctionWithPropertyReferenceAsParam() {
 		String fragment = "upper(property) asc, lower(property) desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( "upper(" + Template.TEMPLATE + ".prop) asc, lower(" + Template.TEMPLATE + ".prop) desc", template );
 	}
 
 	@Test
 	public void testNestedFunctionReferences() {
 		String fragment = "upper(lower(sql)) asc, lower(upper(sql)) desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( "upper(lower(" + Template.TEMPLATE + ".sql)) asc, lower(upper(" + Template.TEMPLATE + ".sql)) desc", template );
 	}
 
 	@Test
 	public void testComplexNestedFunctionReferences() {
 		String fragment = "mod(mod(sql,2),3) asc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( "mod(mod(" + Template.TEMPLATE + ".sql, 2), 3) asc", template );
 	}
 
 	@Test
 	public void testCollation() {
 		String fragment = "`sql` COLLATE my_collation, `sql` COLLATE your_collation";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( Template.TEMPLATE + ".\"sql\" collate my_collation, " + Template.TEMPLATE + ".\"sql\" collate your_collation", template );
 	}
 
 	@Test
 	public void testCollationAndOrdering() {
 		String fragment = "sql COLLATE my_collation, upper(prop) COLLATE your_collation asc, `sql` desc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( Template.TEMPLATE + ".sql collate my_collation, upper(" + Template.TEMPLATE + ".prop) collate your_collation asc, " + Template.TEMPLATE + ".\"sql\" desc", template );
 	}
 
 	@Test
 	public void testComponentReferences() {
 		String fragment = "component asc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( Template.TEMPLATE + ".comp_1 asc, " + Template.TEMPLATE + ".comp_2 asc", template );
 	}
 
 	@Test
 	public void testComponentDerefReferences() {
 		String fragment = "component.prop1 asc";
 		String template = doStandardRendering( fragment );
 
 		assertEquals( Template.TEMPLATE + ".comp_1 asc", template );
 	}
 
 	public String doStandardRendering(String fragment) {
 		return Template.renderOrderByStringTemplate( fragment, MAPPER, null, DIALECT, FUNCTION_REGISTRY );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/Address.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/Address.java
new file mode 100644
index 0000000000..6059807e22
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/Address.java
@@ -0,0 +1,60 @@
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
+package org.hibernate.test.annotations.collectionelement.ordered;
+
+import javax.persistence.Embeddable;
+
+/**
+ * @author Steve Ebersole
+ */
+@Embeddable
+public class Address {
+	private String street;
+	private String city;
+	private String country;
+
+	public String getStreet() {
+		return street;
+	}
+
+	public void setStreet(String street) {
+		this.street = street;
+	}
+
+	public String getCity() {
+		return city;
+	}
+
+	public void setCity(String city) {
+		this.city = city;
+	}
+
+	public String getCountry() {
+		return country;
+	}
+
+	public void setCountry(String country) {
+		this.country = country;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/ElementCollectionSortingTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/ElementCollectionSortingTest.java
new file mode 100644
index 0000000000..ceed69dca6
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/ElementCollectionSortingTest.java
@@ -0,0 +1,57 @@
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
+package org.hibernate.test.annotations.collectionelement.ordered;
+
+import org.hibernate.Session;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ElementCollectionSortingTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Person.class };
+	}
+
+	@Test
+	public void testSortingElementCollectionSyntax() {
+		Session session = openSession();
+		session.beginTransaction();
+
+		session.createQuery( "from Person p join fetch p.nickNamesAscendingNaturalSort" ).list();
+		session.createQuery( "from Person p join fetch p.nickNamesDescendingNaturalSort" ).list();
+
+		session.createQuery( "from Person p join fetch p.addressesAscendingNaturalSort" ).list();
+		session.createQuery( "from Person p join fetch p.addressesDescendingNaturalSort" ).list();
+		session.createQuery( "from Person p join fetch p.addressesCityAscendingSort" ).list();
+		session.createQuery( "from Person p join fetch p.addressesCityDescendingSort" ).list();
+
+		session.getTransaction().commit();
+		session.close();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/Person.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/Person.java
new file mode 100644
index 0000000000..3db5a4fa51
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ordered/Person.java
@@ -0,0 +1,141 @@
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
+package org.hibernate.test.annotations.collectionelement.ordered;
+
+import javax.persistence.ElementCollection;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.OrderBy;
+
+import java.util.HashSet;
+import java.util.Set;
+
+import org.hibernate.annotations.GenericGenerator;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class Person {
+	private Long id;
+	private String name;
+
+	private Set<String> nickNamesAscendingNaturalSort = new HashSet<String>();
+	private Set<String> nickNamesDescendingNaturalSort = new HashSet<String>();
+
+	private Set<Address> addressesAscendingNaturalSort = new HashSet<Address>();
+	private Set<Address> addressesDescendingNaturalSort = new HashSet<Address>();
+	private Set<Address> addressesCityAscendingSort = new HashSet<Address>();
+	private Set<Address> addressesCityDescendingSort = new HashSet<Address>();
+
+
+	@Id
+	@GeneratedValue( generator = "increment" )
+	@GenericGenerator( name = "increment", strategy = "increment" )
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+
+
+	@ElementCollection
+	@JoinColumn
+	@OrderBy
+	public Set<String> getNickNamesAscendingNaturalSort() {
+		return nickNamesAscendingNaturalSort;
+	}
+
+	public void setNickNamesAscendingNaturalSort(Set<String> nickNamesAscendingNaturalSort) {
+		this.nickNamesAscendingNaturalSort = nickNamesAscendingNaturalSort;
+	}
+
+	@ElementCollection
+	@JoinColumn
+	@OrderBy( "desc" )
+	public Set<String> getNickNamesDescendingNaturalSort() {
+		return nickNamesDescendingNaturalSort;
+	}
+
+	public void setNickNamesDescendingNaturalSort(Set<String> nickNamesDescendingNaturalSort) {
+		this.nickNamesDescendingNaturalSort = nickNamesDescendingNaturalSort;
+	}
+
+
+	@ElementCollection
+	@JoinColumn
+	@OrderBy
+	public Set<Address> getAddressesAscendingNaturalSort() {
+		return addressesAscendingNaturalSort;
+	}
+
+	public void setAddressesAscendingNaturalSort(Set<Address> addressesAscendingNaturalSort) {
+		this.addressesAscendingNaturalSort = addressesAscendingNaturalSort;
+	}
+
+	@ElementCollection
+	@JoinColumn
+	@OrderBy( "desc" )
+	public Set<Address> getAddressesDescendingNaturalSort() {
+		return addressesDescendingNaturalSort;
+	}
+
+	public void setAddressesDescendingNaturalSort(Set<Address> addressesDescendingNaturalSort) {
+		this.addressesDescendingNaturalSort = addressesDescendingNaturalSort;
+	}
+
+	@ElementCollection
+	@JoinColumn
+	@OrderBy( "city" )
+	public Set<Address> getAddressesCityAscendingSort() {
+		return addressesCityAscendingSort;
+	}
+
+	public void setAddressesCityAscendingSort(Set<Address> addressesCityAscendingSort) {
+		this.addressesCityAscendingSort = addressesCityAscendingSort;
+	}
+
+	@ElementCollection
+	@JoinColumn
+	@OrderBy( "city desc" )
+	public Set<Address> getAddressesCityDescendingSort() {
+		return addressesCityDescendingSort;
+	}
+
+	public void setAddressesCityDescendingSort(Set<Address> addressesCityDescendingSort) {
+		this.addressesCityDescendingSort = addressesCityDescendingSort;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/City.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/City.java
index d3952a625c..b03aba3071 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/City.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/City.java
@@ -1,71 +1,72 @@
 //$Id$
 package org.hibernate.test.annotations.onetomany;
 import java.util.ArrayList;
 import java.util.List;
 import javax.persistence.Entity;
+import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.OneToMany;
 import javax.persistence.OrderBy;
 
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Immutable;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 class City {
 	private Integer id;
 	private String name;
 	private List<Street> streets;
 	private List<Street> mainStreets;
 
 	@Id
 	@GeneratedValue
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	@OneToMany(mappedBy = "city")
 	@OrderBy("streetNameCopy, id")
 	public synchronized List<Street> getStreets() {
 		return streets;
 	}
 
 	public void setStreets(List<Street> streets) {
 		this.streets = streets;
 	}
 
-	@OneToMany()
+	@OneToMany
 	@JoinColumn(name = "mainstreetcity_id")
 	@ForeignKey(name = "CITYSTR_FK")
 	@OrderBy
 	@Immutable
 	public List<Street> getMainStreets() {
 		return mainStreets;
 	}
 
 	public void setMainStreets(List<Street> streets) {
 		this.mainStreets = streets;
 	}
 
 	public void addMainStreet(Street street) {
 		if ( mainStreets == null ) mainStreets = new ArrayList<Street>();
 		mainStreets.add( street );
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OneToManyTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OneToManyTest.java
index ef242dd29d..8d9be0b9e5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OneToManyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OneToManyTest.java
@@ -1,515 +1,523 @@
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
 package org.hibernate.test.annotations.onetomany;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 import java.util.SortedSet;
 import java.util.TreeSet;
 
+import org.junit.Assert;
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.test.annotations.Customer;
 import org.hibernate.test.annotations.Discount;
 import org.hibernate.test.annotations.Passport;
 import org.hibernate.test.annotations.Ticket;
 import org.hibernate.test.annotations.TicketComparator;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNotSame;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Test various case of a one to many relationship.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public class OneToManyTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testColumnDefinitionPropagation() throws Exception {
 		Session s;
 		s = openSession();
 		s.getTransaction().begin();
 		Politician casimir = new Politician();
 		casimir.setName( "Casimir" );
 		PoliticalParty dream = new PoliticalParty();
 		dream.setName( "Dream" );
 		dream.addPolitician( casimir );
 		s.persist( dream );
 		s.getTransaction().commit();
 		s.clear();
 
 		Transaction tx = s.beginTransaction();
 		s.delete( s.get( PoliticalParty.class, dream.getName() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testListWithBagSemanticAndOrderBy() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		City paris = new City();
 		paris.setName( "Paris" );
 		s.persist( paris );
 		Street rochechoir = new Street();
 		rochechoir.setStreetName( "Rochechoir" );
 		rochechoir.setCity( paris );
 		Street chmpsElysees = new Street();
 		chmpsElysees.setStreetName( "Champs Elysees" );
 		chmpsElysees.setCity( paris );
 		Street grandeArmee = new Street();
 		grandeArmee.setStreetName( "Grande Armee" );
 		grandeArmee.setCity( paris );
 		s.persist( rochechoir );
 		s.persist( chmpsElysees );
 		s.persist( grandeArmee );
 		paris.addMainStreet( chmpsElysees );
 		paris.addMainStreet( grandeArmee );
 
 		s.flush();
 		s.clear();
 
-		//testing @OrderBy with explicit values including Formula
+		// Assert the primary key value relationship amongst the 3 streets...
+		Assert.assertTrue( rochechoir.getId() < chmpsElysees.getId() );
+		Assert.assertTrue( chmpsElysees.getId() < grandeArmee.getId() );
+
 		paris = ( City ) s.get( City.class, paris.getId() );
+
+		// City.streets is defined to be ordered by name primarily...
 		assertEquals( 3, paris.getStreets().size() );
 		assertEquals( chmpsElysees.getStreetName(), paris.getStreets().get( 0 ).getStreetName() );
+		assertEquals( grandeArmee.getStreetName(), paris.getStreets().get( 1 ).getStreetName() );
+		// City.mainStreets is defined to be ordered by street id
 		List<Street> mainStreets = paris.getMainStreets();
 		assertEquals( 2, mainStreets.size() );
 		Integer previousId = -1;
 		for ( Street street : mainStreets ) {
 			assertTrue( previousId < street.getId() );
 			previousId = street.getId();
 		}
 		tx.rollback();
 		s.close();
 
 	}
 
 	@Test
 	public void testUnidirectionalDefault() throws Exception {
 		Session s;
 		Transaction tx;
 		Trainer trainer = new Trainer();
 		trainer.setName( "First trainer" );
 		Tiger regularTiger = new Tiger();
 		regularTiger.setName( "Regular Tiger" );
 		Tiger whiteTiger = new Tiger();
 		whiteTiger.setName( "White Tiger" );
 		trainer.setTrainedTigers( new HashSet<Tiger>() );
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( trainer );
 		s.persist( regularTiger );
 		s.persist( whiteTiger );
 		trainer.getTrainedTigers().add( regularTiger );
 		trainer.getTrainedTigers().add( whiteTiger );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		trainer = ( Trainer ) s.get( Trainer.class, trainer.getId() );
 		assertNotNull( trainer );
 		assertNotNull( trainer.getTrainedTigers() );
 		assertEquals( 2, trainer.getTrainedTigers().size() );
 		tx.rollback();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		trainer = new Trainer();
 		trainer.setName( "new trainer" );
 		trainer.setTrainedTigers( new HashSet<Tiger>() );
 		trainer.getTrainedTigers().add( whiteTiger );
 		try {
 			s.persist( trainer );
 			tx.commit();
 			fail( "A one to many should not allow several trainer per Tiger" );
 		}
 		catch ( HibernateException ce ) {
 			tx.rollback();
 			//success
 		}
 		s.close();
 	}
 
 	@Test
 	public void testUnidirectionalExplicit() throws Exception {
 		Session s;
 		Transaction tx;
 		Trainer trainer = new Trainer();
 		trainer.setName( "First trainer" );
 		Monkey regularMonkey = new Monkey();
 		regularMonkey.setName( "Regular Monkey" );
 		Monkey miniMonkey = new Monkey();
 		miniMonkey.setName( "Mini Monkey" );
 		trainer.setTrainedMonkeys( new HashSet<Monkey>() );
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( trainer );
 		s.persist( regularMonkey );
 		s.persist( miniMonkey );
 		trainer.getTrainedMonkeys().add( regularMonkey );
 		trainer.getTrainedMonkeys().add( miniMonkey );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		trainer = ( Trainer ) s.get( Trainer.class, trainer.getId() );
 		assertNotNull( trainer );
 		assertNotNull( trainer.getTrainedMonkeys() );
 		assertEquals( 2, trainer.getTrainedMonkeys().size() );
 
 		//test suppression of trainer wo monkey
 		final Set<Monkey> monkeySet = new HashSet( trainer.getTrainedMonkeys() );
 		s.delete( trainer );
 		s.flush();
 		tx.commit();
 
 		s.clear();
 
 		tx = s.beginTransaction();
 		for ( Monkey m : monkeySet ) {
 			final Object managedMonkey = s.get( Monkey.class, m.getId() );
 			assertNotNull( "No trainers but monkeys should still be here", managedMonkey );
 		}
 
 		//clean up
 		for ( Monkey m : monkeySet ) {
 			final Object managedMonkey = s.get( Monkey.class, m.getId() );
 			s.delete(managedMonkey);
 		}
 		s.flush();
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetching() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Troop t = new Troop();
 		t.setName( "Final cut" );
 		Soldier vandamme = new Soldier();
 		vandamme.setName( "JC Vandamme" );
 		t.addSoldier( vandamme );
 		Soldier rambo = new Soldier();
 		rambo.setName( "Rambo" );
 		t.addSoldier( rambo );
 		s.persist( t );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		t = ( Troop ) s.get( Troop.class, t.getId() );
 		assertNotNull( t.getSoldiers() );
 		assertFalse( Hibernate.isInitialized( t.getSoldiers() ) );
 		assertEquals( 2, t.getSoldiers().size() );
 		assertEquals( rambo.getName(), t.getSoldiers().iterator().next().getName() );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		t = ( Troop ) s.createQuery( "from " + Troop.class.getName() + " as t where t.id = :id" )
 				.setParameter( "id", t.getId() ).uniqueResult();
 		assertFalse( Hibernate.isInitialized( t.getSoldiers() ) );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		rambo = ( Soldier ) s.get( Soldier.class, rambo.getId() );
 		assertTrue( Hibernate.isInitialized( rambo.getTroop() ) );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		rambo = ( Soldier ) s.createQuery( "from " + Soldier.class.getName() + " as s where s.id = :rid" )
 				.setParameter( "rid", rambo.getId() ).uniqueResult();
 		assertTrue( "fetching strategy used when we do query", Hibernate.isInitialized( rambo.getTroop() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeDeleteOrphan() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Troop disney = new Troop();
 		disney.setName( "Disney" );
 		Soldier mickey = new Soldier();
 		mickey.setName( "Mickey" );
 		disney.addSoldier( mickey );
 		s.persist( disney );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		Troop troop = ( Troop ) s.get( Troop.class, disney.getId() );
 		Soldier soldier = ( Soldier ) troop.getSoldiers().iterator().next();
 		tx.commit();
 		s.close();
 		troop.getSoldiers().clear();
 		s = openSession();
 		tx = s.beginTransaction();
 		s.merge( troop );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		soldier = ( Soldier ) s.get( Soldier.class, mickey.getId() );
 		assertNull( "delete-orphan should work", soldier );
 		troop = ( Troop ) s.get( Troop.class, disney.getId() );
 		s.delete( troop );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeDelete() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Troop disney = new Troop();
 		disney.setName( "Disney" );
 		Soldier mickey = new Soldier();
 		mickey.setName( "Mickey" );
 		disney.addSoldier( mickey );
 		s.persist( disney );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		Troop troop = ( Troop ) s.get( Troop.class, disney.getId() );
 		s.delete( troop );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		Soldier soldier = ( Soldier ) s.get( Soldier.class, mickey.getId() );
 		assertNull( "delete-orphan should work", soldier );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSimpleOneToManySet() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Ticket t = new Ticket();
 		t.setNumber( "33A" );
 		Ticket t2 = new Ticket();
 		t2.setNumber( "234ER" );
 		Customer c = new Customer();
 		s.persist( c );
 		//s.persist(t);
 		SortedSet<Ticket> tickets = new TreeSet<Ticket>( new TicketComparator() );
 		tickets.add( t );
 		tickets.add( t2 );
 		c.setTickets( tickets );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		c = ( Customer ) s.load( Customer.class, c.getId() );
 		assertNotNull( c );
 		assertTrue( Hibernate.isInitialized( c.getTickets() ) );
 		assertNotNull( c.getTickets() );
 		tickets = c.getTickets();
 		assertTrue( tickets.size() > 0 );
 		assertEquals( t2.getNumber(), c.getTickets().first().getNumber() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSimpleOneToManyCollection() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Discount d = new Discount();
 		d.setDiscount( 10 );
 		Customer c = new Customer();
 		List discounts = new ArrayList();
 		discounts.add( d );
 		d.setOwner( c );
 		c.setDiscountTickets( discounts );
 		s.persist( c );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		c = ( Customer ) s.load( Customer.class, c.getId() );
 		assertNotNull( c );
 		assertFalse( Hibernate.isInitialized( c.getDiscountTickets() ) );
 		assertNotNull( c.getDiscountTickets() );
 		Collection collecDiscount = c.getDiscountTickets();
 		assertTrue( collecDiscount.size() > 0 );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testJoinColumns() throws Exception {
 		Parent parent = new Parent();
 		ParentPk pk = new ParentPk();
 		pk.firstName = "Bruce";
 		pk.lastName = "Willis";
 		pk.isMale = true;
 		parent.id = pk;
 		parent.age = 40;
 		Child child = new Child();
 		Child child2 = new Child();
 		parent.addChild( child );
 		parent.addChild( child2 );
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		s.persist( parent );
 		tx.commit();
 		s.close();
 
 		assertNotNull( child.id );
 		assertNotNull( child2.id );
 		assertNotSame( child.id, child2.id );
 
 		s = openSession();
 		tx = s.beginTransaction();
 		parent = ( Parent ) s.get( Parent.class, pk );
 		assertNotNull( parent.children );
 		Hibernate.initialize( parent.children );
 		assertEquals( 2, parent.children.size() );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
-	@FailureExpected(jiraKey = "HHH-3577")
+	@TestForIssue( jiraKey = "HHH-4394" )
 	public void testOrderByOnSuperclassProperty() {
 		OrganisationUser user = new OrganisationUser();
 		user.setFirstName( "Emmanuel" );
 		user.setLastName( "Bernard" );
 		user.setIdPerson( 1l );
 		user.setSomeText( "SomeText" );
 		Organisation org = new Organisation();
 		org.setIdOrganisation( 1l );
 		org.setName( "S Diego Zoo" );
 		user.setOrganisation( org );
 		Session s = openSession();
 		s.getTransaction().begin();
 		s.persist( user );
 		s.persist( org );
 		s.flush();
 		s.clear();
 		s.createQuery( "select org from Organisation org left join fetch org.organisationUsers" ).list();
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-4605" )
 	public void testJoinColumnConfiguredInXml() {
 		PersistentClass pc = configuration().getClassMapping( Model.class.getName() );
 		Table table = pc.getRootTable();
 		Iterator iter = table.getColumnIterator();
 		boolean joinColumnFound = false;
 		while(iter.hasNext()) {
 			Column column = (Column) iter.next();
 			if(column.getName().equals( "model_manufacturer_join" )) {
 				joinColumnFound = true;
 			}
 		}
 		assertTrue( "The mapping defines a joing column which could not be found in the metadata.", joinColumnFound );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Troop.class,
 				Soldier.class,
 				Customer.class,
 				Ticket.class,
 				Discount.class,
 				Passport.class,
 				Parent.class,
 				Child.class,
 				Trainer.class,
 				Tiger.class,
 				Monkey.class,
 				City.class,
 				Street.class,
 				PoliticalParty.class,
 				Politician.class,
 				Person.class,
 				Organisation.class,
 				OrganisationUser.class,
 				Model.class
 		};
 	}
 
 	@Override
 	protected String[] getXmlFiles() {
 		return new String[] { "org/hibernate/test/annotations/onetomany/orm.xml" };
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Animal.java b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Animal.java
new file mode 100644
index 0000000000..fab30fa761
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Animal.java
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
+package org.hibernate.test.collection.ordered.joinedInheritence;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.Inheritance;
+import javax.persistence.InheritanceType;
+
+import org.hibernate.annotations.GenericGenerator;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+@Inheritance( strategy = InheritanceType.JOINED )
+public class Animal {
+	private Long id;
+	private long weight;
+
+	@Id
+	@GeneratedValue( generator = "increment" )
+	@GenericGenerator( strategy = "increment", name = "increment" )
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public long getWeight() {
+		return weight;
+	}
+
+	public void setWeight(long weight) {
+		this.weight = weight;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Lion.java b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Lion.java
new file mode 100644
index 0000000000..7f8f3eb950
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Lion.java
@@ -0,0 +1,33 @@
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
+package org.hibernate.test.collection.ordered.joinedInheritence;
+
+import javax.persistence.Entity;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class Lion extends Animal {
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/OrderCollectionOfJoinedHierarchyTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/OrderCollectionOfJoinedHierarchyTest.java
new file mode 100644
index 0000000000..73de9d29a5
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/OrderCollectionOfJoinedHierarchyTest.java
@@ -0,0 +1,49 @@
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
+package org.hibernate.test.collection.ordered.joinedInheritence;
+
+import org.hibernate.Session;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+/**
+ * @author Steve Ebersole
+ */
+public class OrderCollectionOfJoinedHierarchyTest extends BaseCoreFunctionalTestCase {
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class[] { Animal.class, Lion.class, Tiger.class, Zoo.class };
+	}
+
+	@Test
+	public void testQuerySyntaxCheck() {
+		Session session = openSession();
+		session.beginTransaction();
+		session.get( Zoo.class, 1L );
+		session.getTransaction().commit();
+		session.close();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Tiger.java b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Tiger.java
new file mode 100644
index 0000000000..966dc3c770
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Tiger.java
@@ -0,0 +1,42 @@
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
+package org.hibernate.test.collection.ordered.joinedInheritence;
+
+import javax.persistence.Entity;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class Tiger extends Animal {
+	private int numberOfStripes;
+
+	public int getNumberOfStripes() {
+		return numberOfStripes;
+	}
+
+	public void setNumberOfStripes(int numberOfStripes) {
+		this.numberOfStripes = numberOfStripes;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Zoo.java b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Zoo.java
new file mode 100644
index 0000000000..2fc48baea4
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/ordered/joinedInheritence/Zoo.java
@@ -0,0 +1,97 @@
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
+package org.hibernate.test.collection.ordered.joinedInheritence;
+
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.OneToMany;
+
+import java.util.HashSet;
+import java.util.Set;
+
+import org.hibernate.annotations.GenericGenerator;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class Zoo {
+	private Long id;
+	private String name;
+	private String city;
+	private Set<Tiger> tigers = new HashSet<Tiger>();
+	private Set<Lion> lions = new HashSet<Lion>();
+
+	@Id
+	@GeneratedValue( generator = "increment" )
+	@GenericGenerator( strategy = "increment", name = "increment" )
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+
+	public String getCity() {
+		return city;
+	}
+
+	public void setCity(String city) {
+		this.city = city;
+	}
+
+	@OneToMany( fetch = FetchType.EAGER ) // eager so that load-by-id queries include this association
+	@JoinColumn
+	@javax.persistence.OrderBy( "weight" )
+	public Set<Tiger> getTigers() {
+		return tigers;
+	}
+
+	public void setTigers(Set<Tiger> tigers) {
+		this.tigers = tigers;
+	}
+
+	@OneToMany( fetch = FetchType.EAGER ) // eager so that load-by-id queries include this association
+	@JoinColumn
+	@org.hibernate.annotations.OrderBy( clause = "weight" )
+	public Set<Lion> getLions() {
+		return lions;
+	}
+
+	public void setLions(Set<Lion> lions) {
+		this.lions = lions;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingOrderByTest.java b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingOrderByTest.java
index f2616fca1b..da291a03d4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingOrderByTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/hql/ASTParserLoadingOrderByTest.java
@@ -1,692 +1,702 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.junit.Test;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests AST parser processing of ORDER BY clauses.
  *
  * @author Gail Badner
  */
 public class ASTParserLoadingOrderByTest extends BaseCoreFunctionalTestCase {
 	StateProvince stateProvince;
 	private Zoo zoo1;
 	private Zoo zoo2;
 	private Zoo zoo3;
 	private Zoo zoo4;
 	Set<Zoo> zoosWithSameName;
 	Set<Zoo> zoosWithSameAddress;
 	Mammal zoo1Mammal1;
 	Mammal zoo1Mammal2;
 	Human zoo2Director1;
 	Human zoo2Director2;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"hql/Animal.hbm.xml",
 		};
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "false" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.QUERY_TRANSLATOR, ASTQueryTranslatorFactory.class.getName() );
 	}
 
 	private void createData() {
 		stateProvince = new StateProvince();
 		stateProvince.setName( "IL" );
 
 	    zoo1 = new Zoo();
 		zoo1.setName( "Zoo" );
 		Address address1 = new Address();
 		address1.setStreet( "1313 Mockingbird Lane" );
 		address1.setCity( "Anywhere" );
 		address1.setStateProvince( stateProvince );
 		address1.setCountry( "USA" );
 		zoo1.setAddress( address1 );
 		zoo1Mammal1 = new Mammal();
 		zoo1Mammal1.setDescription( "zoo1Mammal1" );
 		zoo1Mammal1.setZoo( zoo1 );
 		zoo1.getMammals().put( "type1", zoo1Mammal1);
 		zoo1Mammal2 = new Mammal();
 		zoo1Mammal2.setDescription( "zoo1Mammal2" );
 		zoo1Mammal2.setZoo( zoo1 );
 		zoo1.getMammals().put( "type1", zoo1Mammal2);
 
 		zoo2 = new Zoo();
 		zoo2.setName( "A Zoo" );
 		Address address2 = new Address();
 		address2.setStreet( "1313 Mockingbird Lane" );
 		address2.setCity( "Anywhere" );
 		address2.setStateProvince( stateProvince );
 		address2.setCountry( "USA" );
 		zoo2.setAddress( address2 );
 		zoo2Director1 = new Human();
 		zoo2Director1.setName( new Name( "Duh", 'A', "Man" ) );
 		zoo2Director2 = new Human();
 		zoo2Director2.setName( new Name( "Fat", 'A', "Cat" ) );
 		zoo2.getDirectors().put( "Head Honcho", zoo2Director1 );
 		zoo2.getDirectors().put( "Asst. Head Honcho", zoo2Director2 );		
 
 		zoo3 = new Zoo();
 		zoo3.setName( "Zoo" );
 		Address address3 = new Address();
 		address3.setStreet( "1312 Mockingbird Lane" );
 		address3.setCity( "Anywhere" );
 		address3.setStateProvince( stateProvince );
 		address3.setCountry( "USA" );
 		zoo3.setAddress( address3 );
 
 		zoo4 = new Zoo();
 		zoo4.setName( "Duh Zoo" );
 		Address address4 = new Address();
 		address4.setStreet( "1312 Mockingbird Lane" );
 		address4.setCity( "Nowhere" );
 		address4.setStateProvince( stateProvince );
 		address4.setCountry( "USA" );
 		zoo4.setAddress( address4 );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.save( stateProvince );
 		s.save( zoo1Mammal1 );
 		s.save( zoo1Mammal2 );
 		s.save( zoo1 );
 		s.save( zoo2Director1 );
 		s.save( zoo2Director2 );
 		s.save( zoo2 );
 		s.save( zoo3 );
 		s.save( zoo4 );
 		t.commit();
 		s.close();
 
 		zoosWithSameName = new HashSet<Zoo>( 2 );
 		zoosWithSameName.add( zoo1 );
 		zoosWithSameName.add( zoo3 );
 		zoosWithSameAddress = new HashSet<Zoo>( 2 );
 		zoosWithSameAddress.add( zoo1 );
 		zoosWithSameAddress.add( zoo2 );
 	}
 
 	private void cleanupData() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		if ( zoo1 != null ) {
 			s.delete( zoo1 );
 			zoo1 = null;
 		}
 		if ( zoo2 != null ) {
 			s.delete( zoo2 );
 			zoo2 = null;
 		}
 		if ( zoo3 != null ) {
 			s.delete( zoo3 );
 			zoo3 = null;
 		}
 		if ( zoo4 != null ) {
 			s.delete( zoo4 );
 			zoo4 = null;
 		}
 		if ( zoo1Mammal1 != null ) {
 			s.delete( zoo1Mammal1 );
 			zoo1Mammal1 = null;
 		}
 		if ( zoo1Mammal2 != null ) {
 			s.delete( zoo1Mammal2 );
 			zoo1Mammal2 = null;
 		}
 		if ( zoo2Director1 != null ) {
 			s.delete( zoo2Director1 );
 			zoo2Director1 = null;
 		}
 		if ( zoo2Director2 != null ) {
 			s.delete( zoo2Director2 );
 			zoo2Director2 = null;			
 		}
 		if ( stateProvince != null ) {
 			s.delete( stateProvince );
 		}
 		t.commit();
 		s.close();
 	}
 
 	@Test
+	public void testOrderByOnJoinedSubclassPropertyWhoseColumnIsNotInDrivingTable() {
+		// this is simply a syntax check
+		Session s = openSession();
+		Transaction t = s.beginTransaction();
+		s.createQuery( "from Human h order by h.bodyWeight" ).list();
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
 	public void testOrderByNoSelectAliasRef() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by name, address:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select name, address from Zoo order by name, address"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address from Zoo z order by z.name, z.address"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z2.name, z2.address from Zoo z2 where z2.name in ( select name from Zoo ) order by z2.name, z2.address"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		// using ASC
 		checkTestOrderByResults(
 				s.createQuery(
 						"select name, address from Zoo order by name ASC, address ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address from Zoo z order by z.name ASC, z.address ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z2.name, z2.address from Zoo z2 where z2.name in ( select name from Zoo ) order by z2.name ASC, z2.address ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 
 		// ordered by address, name:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address from Zoo z order by z.address, z.name"
 				).list(),
 				zoo3, zoo4, zoo2, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select name, address from Zoo order by address, name"
 				).list(),
 				zoo3, zoo4, zoo2, zoo1, null
 		);
 
 		// ordered by address:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		// unordered:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address from Zoo z order by z.address"
 				).list(),
 				zoo3, zoo4, null, null, zoosWithSameAddress
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select name, address from Zoo order by address"
 				).list(),
 				zoo3, zoo4, null, null, zoosWithSameAddress
 		);
 
 		// ordered by name:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		// unordered:
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address from Zoo z order by z.name"
 				).list(),
 				zoo2, zoo4, null, null, zoosWithSameName
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select name, address from Zoo order by name"
 				).list(),
 				zoo2, zoo4, null, null, zoosWithSameName
 		);
 		t.commit();
 		s.close();
 
 		cleanupData();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testOrderByComponentDescNoSelectAliasRef() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by address DESC, name DESC:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address from Zoo z order by z.address DESC, z.name DESC"
 				).list(),
 				zoo1, zoo2, zoo4, zoo3, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select name, address from Zoo order by address DESC, name DESC"
 				).list(),
 				zoo1, zoo2, zoo4, zoo3, null
 		);
 		t.commit();
 		s.close();
 		cleanupData();
 	}
 
 	@Test
 	public void testOrderBySelectAliasRef() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by name, address:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z2.name as zname, z2.address as zooAddress from Zoo z2 where z2.name in ( select name from Zoo ) order by zname, zooAddress"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as name, z.address as address from Zoo z order by name, address"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as zooName, z.address as zooAddress from Zoo z order by zooName, zooAddress"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address as name from Zoo z order by z.name, name"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address as name from Zoo z order by z.name, name"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		// using ASC
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z2.name as zname, z2.address as zooAddress from Zoo z2 where z2.name in ( select name from Zoo ) order by zname ASC, zooAddress ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as name, z.address as address from Zoo z order by name ASC, address ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as zooName, z.address as zooAddress from Zoo z order by zooName ASC, zooAddress ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address as name from Zoo z order by z.name ASC, name ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address as name from Zoo z order by z.name ASC, name ASC"
 				).list(),
 				zoo2, zoo4, zoo3, zoo1, null
 		);
 
 		// ordered by address, name:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as address, z.address as name from Zoo z order by name, address"
 				).list(),
 				zoo3, zoo4, zoo2, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address as name from Zoo z order by name, z.name"
 				).list(),
 				zoo3, zoo4, zoo2, zoo1, null
 		);
 		// using ASC
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as address, z.address as name from Zoo z order by name ASC, address ASC"
 				).list(),
 				zoo3, zoo4, zoo2, zoo1, null
 		);
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name, z.address as name from Zoo z order by name ASC, z.name ASC"
 				).list(),
 				zoo3, zoo4, zoo2, zoo1, null
 		);
 
 		// ordered by address:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		// unordered:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as zooName, z.address as zooAddress from Zoo z order by zooAddress"
 				).list(),
 				zoo3, zoo4, null, null, zoosWithSameAddress
 		);
 
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as zooName, z.address as name from Zoo z order by name"
 				).list(),
 				zoo3, zoo4, null, null, zoosWithSameAddress
 		);
 
 		// ordered by name:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		// unordered:
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as zooName, z.address as zooAddress from Zoo z order by zooName"
 				).list(),
 				zoo2, zoo4, null, null, zoosWithSameName
 		);
 
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as address, z.address as name from Zoo z order by address"
 				).list(),
 				zoo2, zoo4, null, null, zoosWithSameName
 		);
 		t.commit();
 		s.close();
 
 		cleanupData();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown")
 	public void testOrderByComponentDescSelectAliasRefFailureExpected() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by address desc, name desc:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		// using DESC
 		checkTestOrderByResults(
 				s.createQuery(
 						"select z.name as zooName, z.address as zooAddress from Zoo z order by zooAddress DESC, zooName DESC"
 				).list(),
 				zoo1, zoo2, zoo4, zoo3, null
 		);
 
 		t.commit();
 		s.close();
 
 		cleanupData();
 	}
 
 	@Test
 	public void testOrderByEntityWithFetchJoinedCollection() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by address desc, name desc:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		// using DESC
 		List list = s.createQuery( "from Zoo z join fetch z.mammals" ).list();
 
 		t.commit();
 		s.close();
 
 		cleanupData();
 	}
 
 	@Test
 	public void testOrderBySelectNewArgAliasRef() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by name, address:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		List list =
 				s.createQuery(
 						"select new Zoo( z.name as zname, z.address as zaddress) from Zoo z order by zname, zaddress"
 				).list();
 		assertEquals( 4, list.size() );
 		assertEquals( zoo2, list.get( 0 ) );
 		assertEquals( zoo4, list.get( 1 ) );
 		assertEquals( zoo3, list.get( 2 ) );
 		assertEquals( zoo1, list.get( 3 ) );
 
 		// ordered by address, name:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		list =
 				s.createQuery(
 						"select new Zoo( z.name as zname, z.address as zaddress) from Zoo z order by zaddress, zname"
 				).list();
 		assertEquals( 4, list.size() );
 		assertEquals( zoo3, list.get( 0 ) );
 		assertEquals( zoo4, list.get( 1 ) );
 		assertEquals( zoo2, list.get( 2 ) );
 		assertEquals( zoo1, list.get( 3 ) );
 
 
 		t.commit();
 		s.close();
 
 		cleanupData();
 	}
 
 	@Test(timeout = 5 * 60 * 1000)
 	public void testOrderBySelectNewMapArgAliasRef() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by name, address:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		List list =
 				s.createQuery(
 						"select new map( z.name as zname, z.address as zaddress ) from Zoo z left join z.mammals m order by zname, zaddress"
 				).list();
 		assertEquals( 4, list.size() );
 		assertEquals( zoo2.getName(), ( ( Map ) list.get( 0 ) ).get( "zname" ) );
 		assertEquals( zoo2.getAddress(), ( ( Map ) list.get( 0 ) ).get( "zaddress" ) );
 		assertEquals( zoo4.getName(), ( ( Map ) list.get( 1 ) ).get( "zname" ) );
 		assertEquals( zoo4.getAddress(), ( ( Map ) list.get( 1 ) ).get( "zaddress" ) );
 		assertEquals( zoo3.getName(), ( ( Map ) list.get( 2 ) ).get( "zname" ) );
 		assertEquals( zoo3.getAddress(), ( ( Map ) list.get( 2 ) ).get( "zaddress" ) );
 		assertEquals( zoo1.getName(), ( ( Map ) list.get( 3 ) ).get( "zname" ) );
 		assertEquals( zoo1.getAddress(), ( ( Map ) list.get( 3 ) ).get( "zaddress" ) );
 
 		// ordered by address, name:
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		list =
 				s.createQuery(
 						"select new map( z.name as zname, z.address as zaddress ) from Zoo z left join z.mammals m order by zaddress, zname"
 				).list();
 		assertEquals( 4, list.size() );
 		assertEquals( zoo3.getName(), ( ( Map ) list.get( 0 ) ).get( "zname" ) );
 		assertEquals( zoo3.getAddress(), ( ( Map ) list.get( 0 ) ).get( "zaddress" ) );
 		assertEquals( zoo4.getName(), ( ( Map ) list.get( 1 ) ).get( "zname" ) );
 		assertEquals( zoo4.getAddress(), ( ( Map ) list.get( 1 ) ).get( "zaddress" ) );
 		assertEquals( zoo2.getName(), ( ( Map ) list.get( 2 ) ).get( "zname" ) );
 		assertEquals( zoo2.getAddress(), ( ( Map ) list.get( 2 ) ).get( "zaddress" ) );
 		assertEquals( zoo1.getName(), ( ( Map ) list.get( 3 ) ).get( "zname" ) );
 		assertEquals( zoo1.getAddress(), ( ( Map ) list.get( 3 ) ).get( "zaddress" ) );
 		t.commit();
 		s.close();
 
 		cleanupData();
 	}
 
 	@Test
 	public void testOrderByAggregatedArgAliasRef() {
 		createData();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		// ordered by name, address:
 		//   zoo2  A Zoo       1313 Mockingbird Lane, Anywhere, IL USA
 		//   zoo4  Duh Zoo     1312 Mockingbird Lane, Nowhere, IL USA
 		//   zoo3  Zoo         1312 Mockingbird Lane, Anywhere, IL USA
 		//   zoo1  Zoo         1313 Mockingbird Lane, Anywhere, IL USA
 		List list =
 				s.createQuery(
 						"select z.name as zname, count(*) as cnt from Zoo z group by z.name order by cnt desc, zname"
 				).list();
 		assertEquals( 3, list.size() );
 		assertEquals( zoo3.getName(), ( ( Object[] ) list.get( 0 ) )[ 0 ] );
 		assertEquals( Long.valueOf( 2 ), ( ( Object[] ) list.get( 0 ) )[ 1 ] );
 		assertEquals( zoo2.getName(), ( ( Object[] ) list.get( 1 ) )[ 0 ] );
 		assertEquals( Long.valueOf( 1 ), ( ( Object[] ) list.get( 1 ) )[ 1 ] );
 		assertEquals( zoo4.getName(), ( ( Object[] ) list.get( 2 ) )[ 0 ] );
 		assertEquals( Long.valueOf( 1 ), ( ( Object[] ) list.get( 2 ) )[ 1 ] );
 		t.commit();
 		s.close();
 		cleanupData();
 	}
 
 	private void checkTestOrderByResults(
 			List results,
 			Zoo zoo1,
 			Zoo zoo2,
 			Zoo zoo3,
 			Zoo zoo4,
 			Set<Zoo> zoosUnordered) {
 		assertEquals( 4, results.size() );
 		Set<Zoo> zoosUnorderedCopy = ( zoosUnordered == null ? null : new HashSet<Zoo>( zoosUnordered ) );
 		checkTestOrderByResult( results.get( 0 ), zoo1, zoosUnorderedCopy );
 		checkTestOrderByResult( results.get( 1 ), zoo2, zoosUnorderedCopy );
 		checkTestOrderByResult( results.get( 2 ), zoo3, zoosUnorderedCopy );
 		checkTestOrderByResult( results.get( 3 ), zoo4, zoosUnorderedCopy );
 		if ( zoosUnorderedCopy != null ) {
 			assertTrue( zoosUnorderedCopy.isEmpty() );
 		}
 	}
 
 	private void checkTestOrderByResult(Object result,
 										Zoo zooExpected,
 										Set<Zoo> zoosUnordered) {
 		assertTrue( result instanceof Object[] );
 		Object[] resultArray = ( Object[] ) result;
 		assertEquals( 2,  resultArray.length );
 		Hibernate.initialize( ( ( Address ) resultArray[ 1 ] ).getStateProvince() );
 		if ( zooExpected == null ) {
 			Zoo zooResult = new Zoo();
 			zooResult.setName( ( String ) resultArray[ 0 ] );
 			zooResult.setAddress( ( Address ) resultArray[ 1 ] );
 			assertTrue( zoosUnordered.remove( zooResult ) );
 		}
 		else {
 			assertEquals( zooExpected.getName(), ( ( Object[] ) result )[ 0 ] );
 			assertEquals( zooExpected.getAddress(), ( ( Object[] ) result )[ 1 ] );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/resources/log4j.properties b/hibernate-core/src/test/resources/log4j.properties
index 1fef067054..b4a9eebdab 100644
--- a/hibernate-core/src/test/resources/log4j.properties
+++ b/hibernate-core/src/test/resources/log4j.properties
@@ -1,15 +1,17 @@
 log4j.appender.stdout=org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target=System.out
 log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
 
 
 log4j.rootLogger=info, stdout
 
 log4j.logger.org.hibernate.tool.hbm2ddl=trace
 log4j.logger.org.hibernate.testing.cache=debug
 
 # SQL Logging - HHH-6833
 log4j.logger.org.hibernate.SQL=debug
 
-log4j.logger.org.hibernate.hql.internal.ast=debug
\ No newline at end of file
+log4j.logger.org.hibernate.hql.internal.ast=debug
+
+log4j.logger.org.hibernate.sql.ordering.antlr=trace
\ No newline at end of file
