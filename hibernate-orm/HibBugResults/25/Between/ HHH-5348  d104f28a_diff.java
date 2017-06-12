diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
index 4855673df6..a4e41bc5ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
@@ -1,334 +1,339 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.engine.query;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.FilterTranslator;
 import org.hibernate.hql.ParameterTranslations;
 import org.hibernate.hql.QuerySplitter;
 import org.hibernate.hql.QueryTranslator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
 
 /**
  * Defines a query execution plan for an HQL query (or filter).
  *
  * @author Steve Ebersole
  */
 public class HQLQueryPlan implements Serializable {
 
     // TODO : keep separate notions of QT[] here for shallow/non-shallow queries...
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, HQLQueryPlan.class.getName());
 
 	private final String sourceQuery;
 	private final QueryTranslator[] translators;
 	private final String[] sqlStrings;
 
 	private final ParameterMetadata parameterMetadata;
 	private final ReturnMetadata returnMetadata;
 	private final Set querySpaces;
 
 	private final Set enabledFilterNames;
 	private final boolean shallow;
 
 
 	public HQLQueryPlan(String hql, boolean shallow, Map enabledFilters, SessionFactoryImplementor factory) {
 		this( hql, null, shallow, enabledFilters, factory );
 	}
 
 	protected HQLQueryPlan(String hql, String collectionRole, boolean shallow, Map enabledFilters, SessionFactoryImplementor factory) {
 		this.sourceQuery = hql;
 		this.shallow = shallow;
 
 		Set copy = new HashSet();
 		copy.addAll( enabledFilters.keySet() );
 		this.enabledFilterNames = java.util.Collections.unmodifiableSet( copy );
 
 		Set combinedQuerySpaces = new HashSet();
 		String[] concreteQueryStrings = QuerySplitter.concreteQueries( hql, factory );
 		final int length = concreteQueryStrings.length;
 		translators = new QueryTranslator[length];
 		List sqlStringList = new ArrayList();
 		for ( int i=0; i<length; i++ ) {
 			if ( collectionRole == null ) {
 				translators[i] = factory.getSettings()
 						.getQueryTranslatorFactory()
 						.createQueryTranslator( hql, concreteQueryStrings[i], enabledFilters, factory );
 				translators[i].compile( factory.getSettings().getQuerySubstitutions(), shallow );
 			}
 			else {
 				translators[i] = factory.getSettings()
 						.getQueryTranslatorFactory()
 						.createFilterTranslator( hql, concreteQueryStrings[i], enabledFilters, factory );
 				( ( FilterTranslator ) translators[i] ).compile( collectionRole, factory.getSettings().getQuerySubstitutions(), shallow );
 			}
 			combinedQuerySpaces.addAll( translators[i].getQuerySpaces() );
 			sqlStringList.addAll( translators[i].collectSqlStrings() );
 		}
 
 		this.sqlStrings = ArrayHelper.toStringArray( sqlStringList );
 		this.querySpaces = combinedQuerySpaces;
 
 		if ( length == 0 ) {
 			parameterMetadata = new ParameterMetadata( null, null );
 			returnMetadata = null;
 		}
 		else {
 			this.parameterMetadata = buildParameterMetadata( translators[0].getParameterTranslations(), hql );
 			if ( translators[0].isManipulationStatement() ) {
 				returnMetadata = null;
 			}
 			else {
 				if ( length > 1 ) {
 					final int returns = translators[0].getReturnTypes().length;
 					returnMetadata = new ReturnMetadata( translators[0].getReturnAliases(), new Type[returns] );
 				}
 				else {
 					returnMetadata = new ReturnMetadata( translators[0].getReturnAliases(), translators[0].getReturnTypes() );
 				}
 			}
 		}
 	}
 
 	public String getSourceQuery() {
 		return sourceQuery;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	public ParameterMetadata getParameterMetadata() {
 		return parameterMetadata;
 	}
 
 	public ReturnMetadata getReturnMetadata() {
 		return returnMetadata;
 	}
 
 	public Set getEnabledFilterNames() {
 		return enabledFilterNames;
 	}
 
 	public String[] getSqlStrings() {
 		return sqlStrings;
 	}
 
 	public Set getUtilizedFilterNames() {
 		// TODO : add this info to the translator and aggregate it here...
 		return null;
 	}
 
 	public boolean isShallow() {
 		return shallow;
 	}
 
 	public List performList(
 			QueryParameters queryParameters,
 	        SessionImplementor session) throws HibernateException {
         if (LOG.isTraceEnabled()) {
             LOG.trace("Find: " + getSourceQuery());
 			queryParameters.traceParameters( session.getFactory() );
 		}
 		boolean hasLimit = queryParameters.getRowSelection() != null &&
 		                   queryParameters.getRowSelection().definesLimits();
 		boolean needsLimit = hasLimit && translators.length > 1;
 		QueryParameters queryParametersToUse;
 		if ( needsLimit ) {
             LOG.needsLimit();
 			RowSelection selection = new RowSelection();
 			selection.setFetchSize( queryParameters.getRowSelection().getFetchSize() );
 			selection.setTimeout( queryParameters.getRowSelection().getTimeout() );
 			queryParametersToUse = queryParameters.createCopyUsing( selection );
 		}
 		else {
 			queryParametersToUse = queryParameters;
 		}
 
 		List combinedResults = new ArrayList();
 		IdentitySet distinction = new IdentitySet();
 		int includedCount = -1;
 		translator_loop: for ( int i = 0; i < translators.length; i++ ) {
 			List tmp = translators[i].list( session, queryParametersToUse );
 			if ( needsLimit ) {
 				// NOTE : firstRow is zero-based
 				int first = queryParameters.getRowSelection().getFirstRow() == null
 				            ? 0
 			                : queryParameters.getRowSelection().getFirstRow().intValue();
 				int max = queryParameters.getRowSelection().getMaxRows() == null
 				            ? -1
 			                : queryParameters.getRowSelection().getMaxRows().intValue();
 				final int size = tmp.size();
 				for ( int x = 0; x < size; x++ ) {
 					final Object result = tmp.get( x );
 					if ( ! distinction.add( result ) ) {
 						continue;
 					}
 					includedCount++;
 					if ( includedCount < first ) {
 						continue;
 					}
 					combinedResults.add( result );
 					if ( max >= 0 && includedCount > max ) {
 						// break the outer loop !!!
 						break translator_loop;
 					}
 				}
 			}
 			else {
 				combinedResults.addAll( tmp );
 			}
 		}
 		return combinedResults;
 	}
 
 	public Iterator performIterate(
 			QueryParameters queryParameters,
 	        EventSource session) throws HibernateException {
         if (LOG.isTraceEnabled()) {
             LOG.trace("Iterate: " + getSourceQuery());
 			queryParameters.traceParameters( session.getFactory() );
 		}
 		if ( translators.length == 0 ) {
 			return EmptyIterator.INSTANCE;
 		}
 
 		Iterator[] results = null;
 		boolean many = translators.length > 1;
 		if (many) {
 			results = new Iterator[translators.length];
 		}
 
 		Iterator result = null;
 		for ( int i = 0; i < translators.length; i++ ) {
 			result = translators[i].iterate( queryParameters, session );
 			if (many) results[i] = result;
 		}
 
 		return many ? new JoinedIterator(results) : result;
 	}
 
 	public ScrollableResults performScroll(
 			QueryParameters queryParameters,
 	        SessionImplementor session) throws HibernateException {
         if (LOG.isTraceEnabled()) {
             LOG.trace("Iterate: " + getSourceQuery());
 			queryParameters.traceParameters( session.getFactory() );
 		}
 		if ( translators.length != 1 ) {
 			throw new QueryException( "implicit polymorphism not supported for scroll() queries" );
 		}
 		if ( queryParameters.getRowSelection().definesLimits() && translators[0].containsCollectionFetches() ) {
 			throw new QueryException( "firstResult/maxResults not supported in conjunction with scroll() of a query containing collection fetches" );
 		}
 
 		return translators[0].scroll( queryParameters, session );
 	}
 
 	public int performExecuteUpdate(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException {
         if (LOG.isTraceEnabled()) {
             LOG.trace("Execute update: " + getSourceQuery());
 			queryParameters.traceParameters( session.getFactory() );
 		}
         if (translators.length != 1) LOG.splitQueries(getSourceQuery(), translators.length);
 		int result = 0;
 		for ( int i = 0; i < translators.length; i++ ) {
 			result += translators[i].executeUpdate( queryParameters, session );
 		}
 		return result;
 	}
 
 	private ParameterMetadata buildParameterMetadata(ParameterTranslations parameterTranslations, String hql) {
 		long start = System.currentTimeMillis();
 		ParamLocationRecognizer recognizer = ParamLocationRecognizer.parseLocations( hql );
 		long end = System.currentTimeMillis();
         LOG.trace("HQL param location recognition took " + (end - start) + " mills (" + hql + ")");
 
 		int ordinalParamCount = parameterTranslations.getOrdinalParameterCount();
 		int[] locations = ArrayHelper.toIntArray( recognizer.getOrdinalParameterLocationList() );
 		if ( parameterTranslations.supportsOrdinalParameterMetadata() && locations.length != ordinalParamCount ) {
 			throw new HibernateException( "ordinal parameter mismatch" );
 		}
 		ordinalParamCount = locations.length;
 		OrdinalParameterDescriptor[] ordinalParamDescriptors = new OrdinalParameterDescriptor[ordinalParamCount];
 		for ( int i = 1; i <= ordinalParamCount; i++ ) {
 			ordinalParamDescriptors[ i - 1 ] = new OrdinalParameterDescriptor(
 					i,
 			        parameterTranslations.supportsOrdinalParameterMetadata()
 		                    ? parameterTranslations.getOrdinalParameterExpectedType( i )
 		                    : null,
 			        locations[ i - 1 ]
 			);
 		}
 
 		Iterator itr = recognizer.getNamedParameterDescriptionMap().entrySet().iterator();
 		Map namedParamDescriptorMap = new HashMap();
 		while( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String name = ( String ) entry.getKey();
 			final ParamLocationRecognizer.NamedParameterDescription description =
 					( ParamLocationRecognizer.NamedParameterDescription ) entry.getValue();
 			namedParamDescriptorMap.put(
 					name,
 					new NamedParameterDescriptor(
 							name,
 					        parameterTranslations.getNamedParameterExpectedType( name ),
 					        description.buildPositionsArray(),
 					        description.isJpaStyle()
 					)
 			);
 		}
 
 		return new ParameterMetadata( ordinalParamDescriptors, namedParamDescriptorMap );
 	}
 
 	public QueryTranslator[] getTranslators() {
 		QueryTranslator[] copy = new QueryTranslator[translators.length];
 		System.arraycopy(translators, 0, copy, 0, copy.length);
 		return copy;
 	}
+
+	public Class getDynamicInstantiationResultType() {
+		return translators[0].getDynamicInstantiationResultType();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java
index b6dee910de..71ce4e4801 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/QueryTranslator.java
@@ -1,188 +1,191 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.hql;
+
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.type.Type;
 
 /**
  * Defines the contract of an HQL->SQL translator.
  *
  * @author josh
  */
 public interface QueryTranslator {
 
 	// Error message constants.
 	public static final String ERROR_CANNOT_FETCH_WITH_ITERATE = "fetch may not be used with scroll() or iterate()";
 	public static final String ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR = "Named parameter does not appear in Query: ";
     public static final String ERROR_CANNOT_DETERMINE_TYPE = "Could not determine type of: ";
 	public static final String ERROR_CANNOT_FORMAT_LITERAL =  "Could not format constant value to SQL literal: ";
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param replacements Defined query substitutions.
 	 * @param shallow      Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	void compile(Map replacements, boolean shallow) throws QueryException, MappingException;
 
 	/**
 	 * Perform a list operation given the underlying query definition.
 	 *
 	 * @param session         The session owning this query.
 	 * @param queryParameters The query bind parameters.
 	 * @return The query list results.
 	 * @throws HibernateException
 	 */
 	List list(SessionImplementor session, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Perform an iterate operation given the underlying query definition.
 	 *
 	 * @param queryParameters The query bind parameters.
 	 * @param session         The session owning this query.
 	 * @return An iterator over the query results.
 	 * @throws HibernateException
 	 */
 	Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException;
 
 	/**
 	 * Perform a scroll operation given the underlying query definition.
 	 *
 	 * @param queryParameters The query bind parameters.
 	 * @param session         The session owning this query.
 	 * @return The ScrollableResults wrapper around the query results.
 	 * @throws HibernateException
 	 */
 	ScrollableResults scroll(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Perform a bulk update/delete operation given the underlying query definition.
 	 *
 	 * @param queryParameters The query bind parameters.
 	 * @param session         The session owning this query.
 	 * @return The number of entities updated or deleted.
 	 * @throws HibernateException
 	 */
 	int executeUpdate(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Returns the set of query spaces (table names) that the query refers to.
 	 *
 	 * @return A set of query spaces (table names).
 	 */
 	Set getQuerySpaces();
 
 	/**
 	 * Retrieve the query identifier for this translator.  The query identifier is
 	 * used in states collection.
 	 *
 	 * @return the identifier
 	 */
 	String getQueryIdentifier();
 
 	/**
 	 * Returns the SQL string generated by the translator.
 	 *
 	 * @return the SQL string generated by the translator.
 	 */
 	String getSQLString();
 
 	List collectSqlStrings();
 
 	/**
 	 * Returns the HQL string processed by the translator.
 	 *
 	 * @return the HQL string processed by the translator.
 	 */
 	String getQueryString();
 
 	/**
 	 * Returns the filters enabled for this query translator.
 	 *
 	 * @return Filters enabled for this query execution.
 	 */
 	Map getEnabledFilters();
 
 	/**
 	 * Returns an array of Types represented in the query result.
 	 *
 	 * @return Query return types.
 	 */
 	Type[] getReturnTypes();
 	
 	/**
 	 * Returns an array of HQL aliases
 	 */
 	String[] getReturnAliases();
 
 	/**
 	 * Returns the column names in the generated SQL.
 	 *
 	 * @return the column names in the generated SQL.
 	 */
 	String[][] getColumnNames();
 
 	/**
 	 * Return information about any parameters encountered during
 	 * translation.
 	 *
 	 * @return The parameter information.
 	 */
 	ParameterTranslations getParameterTranslations();
 
 	/**
 	 * Validate the scrollability of the translated query.
 	 *
 	 * @throws HibernateException
 	 */
 	void validateScrollability() throws HibernateException;
 
 	/**
 	 * Does the translated query contain collection fetches?
 	 *
 	 * @return true if the query does contain collection fetched;
 	 * false otherwise.
 	 */
 	boolean containsCollectionFetches();
 
 	boolean isManipulationStatement();
+
+	public Class getDynamicInstantiationResultType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
index 96b348fbdf..fbe4b527b2 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/QueryTranslatorImpl.java
@@ -1,592 +1,600 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.hql.ast;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+
+import antlr.ANTLRException;
+import antlr.RecognitionException;
+import antlr.TokenStreamException;
+import antlr.collections.AST;
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.FilterTranslator;
 import org.hibernate.hql.ParameterTranslations;
 import org.hibernate.hql.QueryExecutionRequestException;
 import org.hibernate.hql.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.antlr.HqlTokenTypes;
 import org.hibernate.hql.antlr.SqlTokenTypes;
 import org.hibernate.hql.ast.exec.BasicExecutor;
 import org.hibernate.hql.ast.exec.MultiTableDeleteExecutor;
 import org.hibernate.hql.ast.exec.MultiTableUpdateExecutor;
 import org.hibernate.hql.ast.exec.StatementExecutor;
+import org.hibernate.hql.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.ast.tree.FromElement;
 import org.hibernate.hql.ast.tree.InsertStatement;
 import org.hibernate.hql.ast.tree.QueryNode;
 import org.hibernate.hql.ast.tree.Statement;
 import org.hibernate.hql.ast.util.ASTPrinter;
 import org.hibernate.hql.ast.util.ASTUtil;
 import org.hibernate.hql.ast.util.NodeTraverser;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.loader.hql.QueryLoader;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
-import antlr.ANTLRException;
-import antlr.RecognitionException;
-import antlr.TokenStreamException;
-import antlr.collections.AST;
 
 /**
  * A QueryTranslator that uses an Antlr-based parser.
  *
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 public class QueryTranslatorImpl implements FilterTranslator {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, QueryTranslatorImpl.class.getName());
 
 	private SessionFactoryImplementor factory;
 
 	private final String queryIdentifier;
 	private String hql;
 	private boolean shallowQuery;
 	private Map tokenReplacements;
 
 	private Map enabledFilters; //TODO:this is only needed during compilation .. can we eliminate the instvar?
 
 	private boolean compiled;
 	private QueryLoader queryLoader;
 	private StatementExecutor statementExecutor;
 
 	private Statement sqlAst;
 	private String sql;
 
 	private ParameterTranslations paramTranslations;
 	private List collectedParameterSpecifications;
 
 
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
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param replacements Defined query substitutions.
 	 * @param shallow      Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
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
             LOG.debugf("compile() : The query is already compiled, skipping...");
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
 			HqlParser parser = parse( true );
 
 			// PHASE 2 : Analyze the HQL AST, and produce an SQL AST.
 			HqlSqlWalker w = analyze( parser, collectionRole );
 
 			sqlAst = ( Statement ) w.getAST();
 
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
 				generate( ( QueryNode ) sqlAst );
 				queryLoader = new QueryLoader( this, factory, w.getSelectClause() );
 			}
 
 			compiled = true;
 		}
 		catch ( QueryException qe ) {
 			qe.setQueryString( hql );
 			throw qe;
 		}
 		catch ( RecognitionException e ) {
             // we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
             LOG.trace("Converted antlr.RecognitionException", e);
 			throw QuerySyntaxException.convert( e, hql );
 		}
 		catch ( ANTLRException e ) {
             // we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
             LOG.trace("Converted antlr.ANTLRException", e);
 			throw new QueryException( e.getMessage(), hql );
 		}
 
 		this.enabledFilters = null; //only needed during compilation phase...
 	}
 
 	private void generate(AST sqlAst) throws QueryException, RecognitionException {
 		if ( sql == null ) {
 			SqlGenerator gen = new SqlGenerator(factory);
 			gen.statement( sqlAst );
 			sql = gen.getSQL();
             if (LOG.isDebugEnabled()) {
                 LOG.debugf("HQL: %s", hql);
                 LOG.debugf("SQL: %s", sql);
 			}
 			gen.getParseErrorHandler().throwQueryException();
 			collectedParameterSpecifications = gen.getCollectedParameters();
 		}
 	}
 
 	private HqlSqlWalker analyze(HqlParser parser, String collectionRole) throws QueryException, RecognitionException {
 		HqlSqlWalker w = new HqlSqlWalker( this, factory, parser, tokenReplacements, collectionRole );
 		AST hqlAst = parser.getAST();
 
 		// Transform the tree.
 		w.statement( hqlAst );
 
         if (LOG.isDebugEnabled()) {
 			ASTPrinter printer = new ASTPrinter( SqlTokenTypes.class );
             LOG.debugf(printer.showAsString(w.getAST(), "--- SQL AST ---"));
 		}
 
 		w.getParseErrorHandler().throwQueryException();
 
 		return w;
 	}
 
 	private HqlParser parse(boolean filter) throws TokenStreamException, RecognitionException {
 		// Parse the query string into an HQL AST.
 		HqlParser parser = HqlParser.getInstance( hql );
 		parser.setFilter( filter );
 
         LOG.debugf("parse() - HQL: %s", hql);
 		parser.statement();
 
 		AST hqlAst = parser.getAST();
 
 		JavaConstantConverter converter = new JavaConstantConverter();
 		NodeTraverser walker = new NodeTraverser( converter );
 		walker.traverseDepthFirst( hqlAst );
 
 		showHqlAst( hqlAst );
 
 		parser.getParseErrorHandler().throwQueryException();
 		return parser;
 	}
 
 	void showHqlAst(AST hqlAst) {
         if (LOG.isDebugEnabled()) {
 			ASTPrinter printer = new ASTPrinter( HqlTokenTypes.class );
             LOG.debugf(printer.showAsString(hqlAst, "--- HQL AST ---"));
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
 	public Type[] getReturnTypes() {
 		errorIfDML();
 		return getWalker().getReturnTypes();
 	}
 
 	public String[] getReturnAliases() {
 		errorIfDML();
 		return getWalker().getReturnAliases();
 	}
 
 	public String[][] getColumnNames() {
 		errorIfDML();
 		return getWalker().getSelectClause().getColumnNames();
 	}
 
 	public Set getQuerySpaces() {
 		return getWalker().getQuerySpaces();
 	}
 
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
 						: queryParameters.getRowSelection().getFirstRow().intValue();
 			int max = !hasLimit || queryParameters.getRowSelection().getMaxRows() == null
 						? -1
 						: queryParameters.getRowSelection().getMaxRows().intValue();
 			int size = results.size();
 			List tmp = new ArrayList();
 			IdentitySet distinction = new IdentitySet();
 			for ( int i = 0; i < size; i++ ) {
 				final Object result = results.get( i );
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
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.iterate( queryParameters, session );
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 */
 	public ScrollableResults scroll(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.scroll( queryParameters, session );
 	}
 
 	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)
 			throws HibernateException {
 		errorIfSelect();
 		return statementExecutor.execute( queryParameters, session );
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 */
 	public String getSQLString() {
 		return sql;
 	}
 
 	public List collectSqlStrings() {
 		ArrayList list = new ArrayList();
 		if ( isManipulationStatement() ) {
 			String[] sqlStatements = statementExecutor.getSqlStatements();
 			for ( int i = 0; i < sqlStatements.length; i++ ) {
 				list.add( sqlStatements[i] );
 			}
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
 
 	public String getQueryString() {
 		return hql;
 	}
 
 	public Map getEnabledFilters() {
 		return enabledFilters;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		return getWalker().getNamedParameterLocations( name );
 	}
 
 	public boolean containsCollectionFetches() {
 		errorIfDML();
 		List collectionFetches = ( ( QueryNode ) sqlAst ).getFromClause().getCollectionFetches();
 		return collectionFetches != null && collectionFetches.size() > 0;
 	}
 
 	public boolean isManipulationStatement() {
 		return sqlAst.needsExecutor();
 	}
 
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
 		Iterator itr = query.getSelectClause().getFromElementsForLoad().iterator();
 		while ( itr.hasNext() ) {
 			// should be the first, but just to be safe...
 			final FromElement fromElement = ( FromElement ) itr.next();
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
 				return new BasicExecutor( walker, persister );
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
 
 	public ParameterTranslations getParameterTranslations() {
 		if ( paramTranslations == null ) {
 			paramTranslations = new ParameterTranslationsImpl( getWalker().getParameters() );
 //			paramTranslations = new ParameterTranslationsImpl( collectedParameterSpecifications );
 		}
 		return paramTranslations;
 	}
 
 	public List getCollectedParameterSpecifications() {
 		return collectedParameterSpecifications;
 	}
 
+	@Override
+	public Class getDynamicInstantiationResultType() {
+		AggregatedSelectExpression aggregation = queryLoader.getAggregatedSelectExpression();
+		return aggregation == null ? null : aggregation.getAggregationResultType();
+	}
+
 	public static class JavaConstantConverter implements NodeTraverser.VisitationStrategy {
 		private AST dotRoot;
 		public void visit(AST node) {
 			if ( dotRoot != null ) {
 				// we are already processing a dot-structure
                 if (ASTUtil.isSubtreeChild(dotRoot, node)) return;
                 // we are now at a new tree level
                 dotRoot = null;
 			}
 
 			if ( dotRoot == null && node.getType() == HqlTokenTypes.DOT ) {
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java
index 1556d938de..c43ca11474 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/AggregatedSelectExpression.java
@@ -1,53 +1,64 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
  */
 package org.hibernate.hql.ast.tree;
+
 import java.util.List;
+
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * Contract for a select expression which aggregates other select expressions together into a single return
  *
  * @author Steve Ebersole
  */
 public interface AggregatedSelectExpression extends SelectExpression {
 	/**
 	 * Retrieves a list of the selection {@link org.hibernate.type.Type types} being aggregated
 	 *
 	 * @return The list of types.
 	 */
 	public List getAggregatedSelectionTypeList();
 
 	/**
 	 * Retrieve the aliases for the columns aggregated here.
 	 *
 	 * @return The column aliases.
 	 */
 	public String[] getAggregatedAliases();
 
 	/**
 	 * Retrieve the {@link ResultTransformer} responsible for building aggregated select expression results into their
 	 * aggregated form.
 	 *
 	 * @return The appropriate transformer
 	 */
 	public ResultTransformer getResultTransformer();
+
+	/**
+	 * Obtain the java type of the aggregation
+	 *
+	 * @return The java type.
+	 */
+	public Class getAggregationResultType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
index 7232a3deea..c8f5f302ec 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/ConstructorNode.java
@@ -1,225 +1,227 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.hql.ast.tree;
 
 import java.lang.reflect.Constructor;
 import java.util.Arrays;
 import java.util.List;
+import java.util.Map;
+
+import antlr.SemanticException;
+import antlr.collections.AST;
+
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.hql.ast.DetailedSemanticException;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.transform.Transformers;
 import org.hibernate.type.Type;
-import antlr.SemanticException;
-import antlr.collections.AST;
 
 /**
  * Represents a constructor (new) in a SELECT.
  *
  * @author josh
  */
 public class ConstructorNode extends SelectExpressionList implements AggregatedSelectExpression {
+	private Class resultType;
 	private Constructor constructor;
 	private Type[] constructorArgumentTypes;
 	private boolean isMap;
 	private boolean isList;
-	private int scalarColumnIndex = -1;
 
 	public ResultTransformer getResultTransformer() {
 		if ( constructor != null ) {
 			return new AliasToBeanConstructorResultTransformer( constructor );
 		}
 		else if ( isMap ) {
 			return Transformers.ALIAS_TO_ENTITY_MAP;
 		}
 		else if ( isList ) {
 			return Transformers.TO_LIST;
 		}
 		throw new QueryException( "Unable to determine proper dynamic-instantiation tranformer to use." );
 	}
 
-	public boolean isMap() {
-		return isMap;
-	}
-
-	public boolean isList() {
-		return isList;
-	}
-
 	private String[] aggregatedAliases;
 
 	public String[] getAggregatedAliases() {
 		if ( aggregatedAliases == null ) {
 			aggregatedAliases = buildAggregatedAliases();
 		}
 		return aggregatedAliases;
 	}
 
 	private String[] buildAggregatedAliases() {
 		SelectExpression[] selectExpressions = collectSelectExpressions();
 		String[] aliases = new String[selectExpressions.length] ;
 		for ( int i=0; i<selectExpressions.length; i++ ) {
 			String alias = selectExpressions[i].getAlias();
 			aliases[i] = alias==null ? Integer.toString(i) : alias;
 		}
 		return aliases;
 	}
 
 	public void setScalarColumn(int i) throws SemanticException {
 		SelectExpression[] selectExpressions = collectSelectExpressions();
 		// Invoke setScalarColumnText on each constructor argument.
 		for ( int j = 0; j < selectExpressions.length; j++ ) {
 			SelectExpression selectExpression = selectExpressions[j];
 			selectExpression.setScalarColumn( j );
 		}
 	}
 
 	public int getScalarColumnIndex() {
-		return scalarColumnIndex;
+		return -1;
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		SelectExpression[] selectExpressions = collectSelectExpressions();
 		// Invoke setScalarColumnText on each constructor argument.
 		for ( int j = 0; j < selectExpressions.length; j++ ) {
 			SelectExpression selectExpression = selectExpressions[j];
 			selectExpression.setScalarColumnText( j );
 		}
 	}
 
 	@Override
     protected AST getFirstSelectExpression() {
 		// Collect the select expressions, skip the first child because it is the class name.
 		return getFirstChild().getNextSibling();
 	}
 
+	@Override
+	public Class getAggregationResultType() {
+		return resultType;
+	}
+
 	/**
 	 * @deprecated (tell clover to ignore this method)
 	 */
 	@Deprecated
     @Override
     public Type getDataType() {
 /*
 		// Return the type of the object created by the constructor.
 		AST firstChild = getFirstChild();
 		String text = firstChild.getText();
 		if ( firstChild.getType() == SqlTokenTypes.DOT ) {
 			DotNode dot = ( DotNode ) firstChild;
 			text = dot.getPath();
 		}
 		return getSessionFactoryHelper().requireEntityType( text );
 */
 		throw new UnsupportedOperationException( "getDataType() is not supported by ConstructorNode!" );
 	}
 
 	public void prepare() throws SemanticException {
 		constructorArgumentTypes = resolveConstructorArgumentTypes();
 		String path = ( ( PathNode ) getFirstChild() ).getPath();
 		if ( "map".equals( path.toLowerCase() ) ) {
 			isMap = true;
+			resultType = Map.class;
 		}
 		else if ( "list".equals( path.toLowerCase() ) ) {
 			isList = true;
+			resultType = List.class;
 		}
 		else {
-			constructor = resolveConstructor(path);
+			constructor = resolveConstructor( path );
+			resultType = constructor.getDeclaringClass();
 		}
 	}
 
 	private Type[] resolveConstructorArgumentTypes() throws SemanticException {
 		SelectExpression[] argumentExpressions = collectSelectExpressions();
 		if ( argumentExpressions == null ) {
 			// return an empty Type array
 			return new Type[]{};
 		}
 
 		Type[] types = new Type[argumentExpressions.length];
 		for ( int x = 0; x < argumentExpressions.length; x++ ) {
 			types[x] = argumentExpressions[x].getDataType();
 		}
 		return types;
 	}
 
 	private Constructor resolveConstructor(String path) throws SemanticException {
 		String importedClassName = getSessionFactoryHelper().getImportedClassName( path );
 		String className = StringHelper.isEmpty( importedClassName ) ? path : importedClassName;
 		if ( className == null ) {
 			throw new SemanticException( "Unable to locate class [" + path + "]" );
 		}
 		try {
 			Class holderClass = ReflectHelper.classForName( className );
 			return ReflectHelper.getConstructor( holderClass, constructorArgumentTypes );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new DetailedSemanticException( "Unable to locate class [" + className + "]", e );
 		}
 		catch ( PropertyNotFoundException e ) {
 			// this is the exception returned by ReflectHelper.getConstructor() if it cannot
 			// locate an appropriate constructor
 			throw new DetailedSemanticException( "Unable to locate appropriate constructor on class [" + className + "]", e );
 		}
 	}
 
 	public Constructor getConstructor() {
 		return constructor;
 	}
 
 	public List getConstructorArgumentTypeList() {
 		return Arrays.asList( constructorArgumentTypes );
 	}
 
 	public List getAggregatedSelectionTypeList() {
 		return getConstructorArgumentTypeList();
 	}
 
 	public FromElement getFromElement() {
 		return null;
 	}
 
 	public boolean isConstructor() {
 		return true;
 	}
 
 	public boolean isReturnableEntity() throws SemanticException {
 		return false;
 	}
 
 	public boolean isScalar() {
 		// Constructors are always considered scalar results.
 		return true;
 	}
 
 	public void setAlias(String alias) {
 		throw new UnsupportedOperationException("constructor may not be aliased");
 	}
 
 	public String getAlias() {
 		throw new UnsupportedOperationException("constructor may not be aliased");
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
index e1485a03f4..6ebe7f84be 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/tree/MapEntryNode.java
@@ -1,280 +1,285 @@
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
 package org.hibernate.hql.ast.tree;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.hql.NameGenerator;
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
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class MapEntryNode extends AbstractMapComponentNode implements AggregatedSelectExpression {
 	private static class LocalAliasGenerator implements AliasGenerator {
 		private final int base;
 		private int counter = 0;
 
 		private LocalAliasGenerator(int base) {
 			this.base = base;
 		}
 
 		public String generateAlias(String sqlExpression) {
 			return NameGenerator.scalarName( base, counter++ );
 		}
 	}
 
 	private int scalarColumnIndex = -1;
 
 	protected String expressionDescription() {
 		return "entry(*)";
 	}
 
+	@Override
+	public Class getAggregationResultType() {
+		return Map.Entry.class;
+	}
+
 	protected Type resolveType(QueryableCollection collectionPersister) {
 		Type keyType = collectionPersister.getIndexType();
 		Type valueType = collectionPersister.getElementType();
 		types.add( keyType );
 		types.add( valueType );
 		mapEntryBuilder = new MapEntryBuilder();
 
 		// an entry (as an aggregated select expression) does not have a type...
 		return null;
 	}
 
 	protected String[] resolveColumns(QueryableCollection collectionPersister) {
 		List selections = new ArrayList();
 		determineKeySelectExpressions( collectionPersister, selections );
 		determineValueSelectExpressions( collectionPersister, selections );
 
 		String text = "";
 		String[] columns = new String[selections.size()];
 		for ( int i = 0; i < selections.size(); i++ ) {
 			SelectExpression selectExpression = (SelectExpression) selections.get(i);
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
 		if ( keyType.isAssociationType() ) {
 			EntityType entityType = (EntityType) keyType;
 			Queryable keyEntityPersister = ( Queryable ) sfi().getEntityPersister(
 					entityType.getAssociatedEntityName( sfi() )
 			);
 			SelectFragment fragment = keyEntityPersister.propertySelectFragmentFragment(
 					collectionTableAlias(),
 					null,
 					false
 			);
 			appendSelectExpressions( fragment, selections, aliasGenerator );
 		}
 	}
 
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
 			Queryable valueEntityPersister = ( Queryable ) sfi().getEntityPersister(
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
 
 		public String getExpression() {
 			return expression;
 		}
 
 		public String getAlias() {
 			return alias;
 		}
 	}
 
 	public SessionFactoryImplementor sfi() {
 		return getSessionFactoryHelper().getFactory();
 	}
 
 	public void setText(String s) {
 		if ( isResolved() ) {
 			return;
 		}
 		super.setText( s );
 	}
 
 	public void setScalarColumn(int i) throws SemanticException {
 		this.scalarColumnIndex = i;
 	}
 
 	public int getScalarColumnIndex() {
 		return scalarColumnIndex;
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 	}
 
 	public boolean isScalar() {
 		// Constructors are always considered scalar results.
 		return true;
 	}
 
 	private List types = new ArrayList(4); // size=4 to prevent resizing
 
 	public List getAggregatedSelectionTypeList() {
 		return types;
 	}
 
 	private static final String[] ALIASES = { null, null };
 
 	public String[] getAggregatedAliases() {
 		return ALIASES;
 	}
 
 	private MapEntryBuilder mapEntryBuilder;
 
 	public ResultTransformer getResultTransformer() {
 		return mapEntryBuilder;
 	}
 
 	private static class MapEntryBuilder extends BasicTransformerAdapter {
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
 
 		public Object getValue() {
 			return value;
 		}
 
 		public Object getKey() {
 			return key;
 		}
 
 		public Object setValue(Object value) {
 			Object old = this.value;
 			this.value = value;
 			return old;
 		}
 
 		public boolean equals(Object o) {
 			// IMPL NOTE : nulls are considered equal for keys and values according to Map.Entry contract
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 			EntryAdapter that = ( EntryAdapter ) o;
 
 			// make sure we have the same types...
 			return ( key == null ? that.key == null : key.equals( that.key ) )
 					&& ( value == null ? that.value == null : value.equals( that.value ) );
 
 		}
 
 		public int hashCode() {
 			int keyHash = key == null ? 0 : key.hashCode();
 			int valueHash = value == null ? 0 : value.hashCode();
 			return keyHash ^ valueHash;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
index 0a16636291..acb54eca99 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
@@ -196,1035 +196,1040 @@ public class QueryTranslatorImpl extends BasicLoader implements FilterTranslator
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 */
 	public synchronized void compile(
 			Map replacements,
 			boolean scalar) throws QueryException, MappingException {
 		if ( !compiled ) {
 			this.tokenReplacements = replacements;
 			this.shallowQuery = scalar;
 			compile();
 		}
 	}
 
 	/**
 	 * Compile a filter. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 */
 	public synchronized void compile(
 			String collectionRole,
 			Map replacements,
 			boolean scalar) throws QueryException, MappingException {
 
 		if ( !isCompiled() ) {
 			addFromAssociation( "this", collectionRole );
 			compile( replacements, scalar );
 		}
 	}
 
 	/**
 	 * Compile the query (generate the SQL).
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems resolving
 	 * things referenced in the query.
 	 * @throws org.hibernate.QueryException Generally some form of syntatic
 	 * failure.
 	 */
 	private void compile() throws QueryException, MappingException {
 
         LOG.trace("Compiling query");
 		try {
 			ParserHelper.parse( new PreprocessingParser( tokenReplacements ),
 					queryString,
 					ParserHelper.HQL_SEPARATORS,
 					this );
 			renderSQL();
 		}
 		catch ( QueryException qe ) {
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 		catch ( MappingException me ) {
 			throw me;
 		}
 		catch ( Exception e ) {
             LOG.debug("Unexpected query compilation problem", e);
 			e.printStackTrace();
 			QueryException qe = new QueryException( "Incorrect query syntax", e );
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 
 		postInstantiate();
 
 		compiled = true;
 
 	}
 
 	@Override
     public String getSQLString() {
 		return sqlString;
 	}
 
 	public List collectSqlStrings() {
 		return ArrayHelper.toList( new String[] { sqlString } );
 	}
 
 	public String getQueryString() {
 		return queryString;
 	}
 
 	/**
 	 * Persisters for the return values of a <tt>find()</tt> style query.
 	 *
 	 * @return an array of <tt>EntityPersister</tt>s.
 	 */
 	@Override
     protected Loadable[] getEntityPersisters() {
 		return persisters;
 	}
 
 	/**
 	 * Types of the return values of an <tt>iterate()</tt> style query.
 	 *
 	 * @return an array of <tt>Type</tt>s.
 	 */
 	public Type[] getReturnTypes() {
 		return actualReturnTypes;
 	}
 
 	public String[] getReturnAliases() {
 		// return aliases not supported in classic translator!
 		return NO_RETURN_ALIASES;
 	}
 
 	public String[][] getColumnNames() {
 		return scalarColumnNames;
 	}
 
 	private static void logQuery(String hql, String sql) {
         if (LOG.isDebugEnabled()) {
             LOG.debugf("HQL: %s", hql);
             LOG.debugf("SQL: %s", sql);
 		}
 	}
 
 	void setAliasName(String alias, String name) {
 		aliasNames.put( alias, name );
 	}
 
 	public String getAliasName(String alias) {
 		String name = ( String ) aliasNames.get( alias );
 		if ( name == null ) {
 			if ( superQuery != null ) {
 				name = superQuery.getAliasName( alias );
 			}
 			else {
 				name = alias;
 			}
 		}
 		return name;
 	}
 
 	String unalias(String path) {
 		String alias = StringHelper.root( path );
 		String name = getAliasName( alias );
         if (name != null) return name + path.substring(alias.length());
         return path;
 	}
 
 	void addEntityToFetch(String name, String oneToOneOwnerName, AssociationType ownerAssociationType) {
 		addEntityToFetch( name );
 		if ( oneToOneOwnerName != null ) oneToOneOwnerNames.put( name, oneToOneOwnerName );
 		if ( ownerAssociationType != null ) uniqueKeyOwnerReferences.put( name, ownerAssociationType );
 	}
 
 	private void addEntityToFetch(String name) {
 		entitiesToFetch.add( name );
 	}
 
 	private int nextCount() {
 		return ( superQuery == null ) ? nameCount++ : superQuery.nameCount++;
 	}
 
 	String createNameFor(String type) {
 		return StringHelper.generateAlias( type, nextCount() );
 	}
 
 	String createNameForCollection(String role) {
 		return StringHelper.generateAlias( role, nextCount() );
 	}
 
 	private String getType(String name) {
 		String type = ( String ) typeMap.get( name );
 		if ( type == null && superQuery != null ) {
 			type = superQuery.getType( name );
 		}
 		return type;
 	}
 
 	private String getRole(String name) {
 		String role = ( String ) collections.get( name );
 		if ( role == null && superQuery != null ) {
 			role = superQuery.getRole( name );
 		}
 		return role;
 	}
 
 	boolean isName(String name) {
 		return aliasNames.containsKey( name ) ||
 				typeMap.containsKey( name ) ||
 				collections.containsKey( name ) || (
 				superQuery != null && superQuery.isName( name )
 				);
 	}
 
 	PropertyMapping getPropertyMapping(String name) throws QueryException {
 		PropertyMapping decorator = getDecoratedPropertyMapping( name );
 		if ( decorator != null ) return decorator;
 
 		String type = getType( name );
 		if ( type == null ) {
 			String role = getRole( name );
 			if ( role == null ) {
 				throw new QueryException( "alias not found: " + name );
 			}
 			return getCollectionPersister( role ); //.getElementPropertyMapping();
 		}
 		else {
 			Queryable persister = getEntityPersister( type );
 			if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
 			return persister;
 		}
 	}
 
 	private PropertyMapping getDecoratedPropertyMapping(String name) {
 		return ( PropertyMapping ) decoratedPropertyMappings.get( name );
 	}
 
 	void decoratePropertyMapping(String name, PropertyMapping mapping) {
 		decoratedPropertyMappings.put( name, mapping );
 	}
 
 	private Queryable getEntityPersisterForName(String name) throws QueryException {
 		String type = getType( name );
 		Queryable persister = getEntityPersister( type );
 		if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
 		return persister;
 	}
 
 	Queryable getEntityPersisterUsingImports(String className) {
 		final String importedClassName = getFactory().getImportedClassName( className );
 		if ( importedClassName == null ) {
 			return null;
 		}
 		try {
 			return ( Queryable ) getFactory().getEntityPersister( importedClassName );
 		}
 		catch ( MappingException me ) {
 			return null;
 		}
 	}
 
 	Queryable getEntityPersister(String entityName) throws QueryException {
 		try {
 			return ( Queryable ) getFactory().getEntityPersister( entityName );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "persistent class not found: " + entityName );
 		}
 	}
 
 	QueryableCollection getCollectionPersister(String role) throws QueryException {
 		try {
 			return ( QueryableCollection ) getFactory().getCollectionPersister( role );
 		}
 		catch ( ClassCastException cce ) {
 			throw new QueryException( "collection role is not queryable: " + role );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "collection role not found: " + role );
 		}
 	}
 
 	void addType(String name, String type) {
 		typeMap.put( name, type );
 	}
 
 	void addCollection(String name, String role) {
 		collections.put( name, role );
 	}
 
 	void addFrom(String name, String type, JoinSequence joinSequence)
 			throws QueryException {
 		addType( name, type );
 		addFrom( name, joinSequence );
 	}
 
 	void addFromCollection(String name, String collectionRole, JoinSequence joinSequence)
 			throws QueryException {
 		//register collection role
 		addCollection( name, collectionRole );
 		addJoin( name, joinSequence );
 	}
 
 	void addFrom(String name, JoinSequence joinSequence)
 			throws QueryException {
 		fromTypes.add( name );
 		addJoin( name, joinSequence );
 	}
 
 	void addFromClass(String name, Queryable classPersister)
 			throws QueryException {
 		JoinSequence joinSequence = new JoinSequence( getFactory() )
 				.setRoot( classPersister, name );
 		//crossJoins.add(name);
 		addFrom( name, classPersister.getEntityName(), joinSequence );
 	}
 
 	void addSelectClass(String name) {
 		returnedTypes.add( name );
 	}
 
 	void addSelectScalar(Type type) {
 		scalarTypes.add( type );
 	}
 
 	void appendWhereToken(String token) {
 		whereTokens.add( token );
 	}
 
 	void appendHavingToken(String token) {
 		havingTokens.add( token );
 	}
 
 	void appendOrderByToken(String token) {
 		orderByTokens.add( token );
 	}
 
 	void appendGroupByToken(String token) {
 		groupByTokens.add( token );
 	}
 
 	void appendScalarSelectToken(String token) {
 		scalarSelectTokens.add( token );
 	}
 
 	void appendScalarSelectTokens(String[] tokens) {
 		scalarSelectTokens.add( tokens );
 	}
 
 	void addFromJoinOnly(String name, JoinSequence joinSequence) throws QueryException {
 		addJoin( name, joinSequence.getFromPart() );
 	}
 
 	void addJoin(String name, JoinSequence joinSequence) throws QueryException {
 		if ( !joins.containsKey( name ) ) joins.put( name, joinSequence );
 	}
 
 	void addNamedParameter(String name) {
 		if ( superQuery != null ) superQuery.addNamedParameter( name );
 		Integer loc = new Integer( parameterCount++ );
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
     public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			QueryException qe = new QueryException( ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name );
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 		if ( o instanceof Integer ) {
 			return new int[]{ ( ( Integer ) o ).intValue() };
 		}
 		else {
 			return ArrayHelper.toIntArray( ( ArrayList ) o );
 		}
 	}
 
 	private void renderSQL() throws QueryException, MappingException {
 
 		final int rtsize;
 		if ( returnedTypes.size() == 0 && scalarTypes.size() == 0 ) {
 			//ie no select clause in HQL
 			returnedTypes = fromTypes;
 			rtsize = returnedTypes.size();
 		}
 		else {
 			rtsize = returnedTypes.size();
 			Iterator iter = entitiesToFetch.iterator();
 			while ( iter.hasNext() ) {
 				returnedTypes.add( iter.next() );
 			}
 		}
 		int size = returnedTypes.size();
 		persisters = new Queryable[size];
 		names = new String[size];
 		owners = new int[size];
 		ownerAssociationTypes = new EntityType[size];
 		suffixes = new String[size];
 		includeInSelect = new boolean[size];
 		for ( int i = 0; i < size; i++ ) {
 			String name = ( String ) returnedTypes.get( i );
 			//if ( !isName(name) ) throw new QueryException("unknown type: " + name);
 			persisters[i] = getEntityPersisterForName( name );
 			// TODO: cannot use generateSuffixes() - it handles the initial suffix differently.
 			suffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + '_';
 			names[i] = name;
 			includeInSelect[i] = !entitiesToFetch.contains( name );
 			if ( includeInSelect[i] ) selectLength++;
 			if ( name.equals( collectionOwnerName ) ) collectionOwnerColumn = i;
 			String oneToOneOwner = ( String ) oneToOneOwnerNames.get( name );
 			owners[i] = ( oneToOneOwner == null ) ? -1 : returnedTypes.indexOf( oneToOneOwner );
 			ownerAssociationTypes[i] = (EntityType) uniqueKeyOwnerReferences.get( name );
 		}
 
 		if ( ArrayHelper.isAllNegative( owners ) ) owners = null;
 
 		String scalarSelect = renderScalarSelect(); //Must be done here because of side-effect! yuck...
 
 		int scalarSize = scalarTypes.size();
 		hasScalars = scalarTypes.size() != rtsize;
 
 		returnTypes = new Type[scalarSize];
 		for ( int i = 0; i < scalarSize; i++ ) {
 			returnTypes[i] = ( Type ) scalarTypes.get( i );
 		}
 
 		QuerySelect sql = new QuerySelect( getFactory().getDialect() );
 		sql.setDistinct( distinct );
 
 		if ( !shallowQuery ) {
 			renderIdentifierSelect( sql );
 			renderPropertiesSelect( sql );
 		}
 
 		if ( collectionPersister != null ) {
 			sql.addSelectFragmentString( collectionPersister.selectFragment( fetchName, "__" ) );
 		}
 
 		if ( hasScalars || shallowQuery ) sql.addSelectFragmentString( scalarSelect );
 
 		//TODO: for some dialects it would be appropriate to add the renderOrderByPropertiesSelect() to other select strings
 		mergeJoins( sql.getJoinFragment() );
 
 		sql.setWhereTokens( whereTokens.iterator() );
 
 		sql.setGroupByTokens( groupByTokens.iterator() );
 		sql.setHavingTokens( havingTokens.iterator() );
 		sql.setOrderByTokens( orderByTokens.iterator() );
 
 		if ( collectionPersister != null && collectionPersister.hasOrdering() ) {
 			sql.addOrderBy( collectionPersister.getSQLOrderByString( fetchName ) );
 		}
 
 		scalarColumnNames = NameGenerator.generateColumnNames( returnTypes, getFactory() );
 
 		// initialize the Set of queried identifier spaces (ie. tables)
 		Iterator iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = getCollectionPersister( ( String ) iter.next() );
 			addQuerySpaces( p.getCollectionSpaces() );
 		}
 		iter = typeMap.keySet().iterator();
 		while ( iter.hasNext() ) {
 			Queryable p = getEntityPersisterForName( ( String ) iter.next() );
 			addQuerySpaces( p.getQuerySpaces() );
 		}
 
 		sqlString = sql.toQueryString();
 
 		if ( holderClass != null ) holderConstructor = ReflectHelper.getConstructor( holderClass, returnTypes );
 
 		if ( hasScalars ) {
 			actualReturnTypes = returnTypes;
 		}
 		else {
 			actualReturnTypes = new Type[selectLength];
 			int j = 0;
 			for ( int i = 0; i < persisters.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					actualReturnTypes[j++] = getFactory().getTypeResolver()
 							.getTypeFactory()
 							.manyToOne( persisters[i].getEntityName(), shallowQuery );
 				}
 			}
 		}
 
 	}
 
 	private void renderIdentifierSelect(QuerySelect sql) {
 		int size = returnedTypes.size();
 
 		for ( int k = 0; k < size; k++ ) {
 			String name = ( String ) returnedTypes.get( k );
 			String suffix = size == 1 ? "" : Integer.toString( k ) + '_';
 			sql.addSelectFragmentString( persisters[k].identifierSelectFragment( name, suffix ) );
 		}
 
 	}
 
 	/*private String renderOrderByPropertiesSelect() {
 		StringBuffer buf = new StringBuffer(10);
 
 		//add the columns we are ordering by to the select ID select clause
 		Iterator iter = orderByTokens.iterator();
 		while ( iter.hasNext() ) {
 			String token = (String) iter.next();
 			if ( token.lastIndexOf(".") > 0 ) {
 				//ie. it is of form "foo.bar", not of form "asc" or "desc"
 				buf.append(StringHelper.COMMA_SPACE).append(token);
 			}
 		}
 
 		return buf.toString();
 	}*/
 
 	private void renderPropertiesSelect(QuerySelect sql) {
 		int size = returnedTypes.size();
 		for ( int k = 0; k < size; k++ ) {
 			String suffix = size == 1 ? "" : Integer.toString( k ) + '_';
 			String name = ( String ) returnedTypes.get( k );
 			sql.addSelectFragmentString( persisters[k].propertySelectFragment( name, suffix, false ) );
 		}
 	}
 
 	/**
 	 * WARNING: side-effecty
 	 */
 	private String renderScalarSelect() {
 
 		boolean isSubselect = superQuery != null;
 
 		StringBuffer buf = new StringBuffer( 20 );
 
 		if ( scalarTypes.size() == 0 ) {
 			//ie. no select clause
 			int size = returnedTypes.size();
 			for ( int k = 0; k < size; k++ ) {
 
 				scalarTypes.add(
 						getFactory().getTypeResolver().getTypeFactory().manyToOne( persisters[k].getEntityName(), shallowQuery )
 				);
 
 				String[] idColumnNames = persisters[k].getIdentifierColumnNames();
 				for ( int i = 0; i < idColumnNames.length; i++ ) {
 					buf.append( returnedTypes.get( k ) ).append( '.' ).append( idColumnNames[i] );
 					if ( !isSubselect ) buf.append( " as " ).append( NameGenerator.scalarName( k, i ) );
 					if ( i != idColumnNames.length - 1 || k != size - 1 ) buf.append( ", " );
 				}
 
 			}
 
 		}
 		else {
 			//there _was_ a select clause
 			Iterator iter = scalarSelectTokens.iterator();
 			int c = 0;
 			boolean nolast = false; //real hacky...
 			int parenCount = 0; // used to count the nesting of parentheses
 			while ( iter.hasNext() ) {
 				Object next = iter.next();
 				if ( next instanceof String ) {
 					String token = ( String ) next;
 
 					if ( "(".equals( token ) ) {
 						parenCount++;
 					}
 					else if ( ")".equals( token ) ) {
 						parenCount--;
 					}
 
 					String lc = token.toLowerCase();
 					if ( lc.equals( ", " ) ) {
 						if ( nolast ) {
 							nolast = false;
 						}
 						else {
 							if ( !isSubselect && parenCount == 0 ) {
 								int x = c++;
 								buf.append( " as " )
 										.append( NameGenerator.scalarName( x, 0 ) );
 							}
 						}
 					}
 					buf.append( token );
 					if ( lc.equals( "distinct" ) || lc.equals( "all" ) ) {
 						buf.append( ' ' );
 					}
 				}
 				else {
 					nolast = true;
 					String[] tokens = ( String[] ) next;
 					for ( int i = 0; i < tokens.length; i++ ) {
 						buf.append( tokens[i] );
 						if ( !isSubselect ) {
 							buf.append( " as " )
 									.append( NameGenerator.scalarName( c, i ) );
 						}
 						if ( i != tokens.length - 1 ) buf.append( ", " );
 					}
 					c++;
 				}
 			}
 			if ( !isSubselect && !nolast ) {
 				int x = c++;
 				buf.append( " as " )
 						.append( NameGenerator.scalarName( x, 0 ) );
 			}
 
 		}
 
 		return buf.toString();
 	}
 
 	private void mergeJoins(JoinFragment ojf) throws MappingException, QueryException {
 
 		Iterator iter = joins.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			String name = ( String ) me.getKey();
 			JoinSequence join = ( JoinSequence ) me.getValue();
 			join.setSelector( new JoinSequence.Selector() {
 				public boolean includeSubclasses(String alias) {
 					boolean include = returnedTypes.contains( alias ) && !isShallowQuery();
 					return include;
 				}
 			} );
 
 			if ( typeMap.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 			else if ( collections.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 			else {
 				//name from a super query (a bit inelegant that it shows up here)
 			}
 
 		}
 
 	}
 
 	public final Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	/**
 	 * Is this query called by scroll() or iterate()?
 	 *
 	 * @return true if it is, false if it is called by find() or list()
 	 */
 	boolean isShallowQuery() {
 		return shallowQuery;
 	}
 
 	void addQuerySpaces(Serializable[] spaces) {
 		for ( int i = 0; i < spaces.length; i++ ) {
 			querySpaces.add( spaces[i] );
 		}
 		if ( superQuery != null ) superQuery.addQuerySpaces( spaces );
 	}
 
 	void setDistinct(boolean distinct) {
 		this.distinct = distinct;
 	}
 
 	boolean isSubquery() {
 		return superQuery != null;
 	}
 
 	/**
 	 * Overrides method from Loader
 	 */
 	@Override
     public CollectionPersister[] getCollectionPersisters() {
 		return collectionPersister == null ? null : new CollectionPersister[] { collectionPersister };
 	}
 
 	@Override
     protected String[] getCollectionSuffixes() {
 		return collectionPersister == null ? null : new String[] { "__" };
 	}
 
 	void setCollectionToFetch(String role, String name, String ownerName, String entityName)
 			throws QueryException {
 		fetchName = name;
 		collectionPersister = getCollectionPersister( role );
 		collectionOwnerName = ownerName;
 		if ( collectionPersister.getElementType().isEntityType() ) {
 			addEntityToFetch( entityName );
 		}
 	}
 
 	@Override
     protected String[] getSuffixes() {
 		return suffixes;
 	}
 
 	@Override
     protected String[] getAliases() {
 		return names;
 	}
 
 	/**
 	 * Used for collection filters
 	 */
 	private void addFromAssociation(final String elementName, final String collectionRole)
 			throws QueryException {
 		//q.addCollection(collectionName, collectionRole);
 		QueryableCollection persister = getCollectionPersister( collectionRole );
 		Type collectionElementType = persister.getElementType();
 		if ( !collectionElementType.isEntityType() ) {
 			throw new QueryException( "collection of values in filter: " + elementName );
 		}
 
 		String[] keyColumnNames = persister.getKeyColumnNames();
 		//if (keyColumnNames.length!=1) throw new QueryException("composite-key collection in filter: " + collectionRole);
 
 		String collectionName;
 		JoinSequence join = new JoinSequence( getFactory() );
 		collectionName = persister.isOneToMany() ?
 				elementName :
 				createNameForCollection( collectionRole );
 		join.setRoot( persister, collectionName );
 		if ( !persister.isOneToMany() ) {
 			//many-to-many
 			addCollection( collectionName, collectionRole );
 			try {
 				join.addJoin( ( AssociationType ) persister.getElementType(),
 						elementName,
 						JoinFragment.INNER_JOIN,
 						persister.getElementColumnNames(collectionName) );
 			}
 			catch ( MappingException me ) {
 				throw new QueryException( me );
 			}
 		}
 		join.addCondition( collectionName, keyColumnNames, " = ?" );
 		//if ( persister.hasWhere() ) join.addCondition( persister.getSQLWhereString(collectionName) );
 		EntityType elemType = ( EntityType ) collectionElementType;
 		addFrom( elementName, elemType.getAssociatedEntityName(), join );
 
 	}
 
 	String getPathAlias(String path) {
 		return ( String ) pathAliases.get( path );
 	}
 
 	JoinSequence getPathJoin(String path) {
 		return ( JoinSequence ) pathJoins.get( path );
 	}
 
 	void addPathAliasAndJoin(String path, String alias, JoinSequence joinSequence) {
 		pathAliases.put( path, alias );
 		pathJoins.put( path, joinSequence );
 	}
 
 	public List list(SessionImplementor session, QueryParameters queryParameters)
 			throws HibernateException {
 		return list( session, queryParameters, getQuerySpaces(), actualReturnTypes );
 	}
 
 	/**
 	 * Return the query results as an iterator
 	 */
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 
 		boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 
 			PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 			ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), false, queryParameters.getRowSelection(), session );
 			HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(holderConstructor, queryParameters.getResultTransformer());
 			Iterator result = new IteratorImpl( rs, st, session, queryParameters.isReadOnly( session ), returnTypes, getColumnNames(), hi );
 
 			if ( stats ) {
 				session.getFactory().getStatisticsImplementor().queryExecuted(
 						"HQL: " + queryString,
 						0,
 						System.currentTimeMillis() - startTime
 					);
 			}
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query using iterate",
 					getSQLString()
 				);
 		}
 
 	}
 
 	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {
 		throw new UnsupportedOperationException( "Not supported!  Use the AST translator...");
 	}
 
 	@Override
     protected boolean[] includeInResultRow() {
 		boolean[] isResultReturned = includeInSelect;
 		if ( hasScalars ) {
 			isResultReturned = new boolean[ returnedTypes.size() ];
 			Arrays.fill( isResultReturned, true );
 		}
 		return isResultReturned;
 	}
 
 
 	@Override
     protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveClassicResultTransformer(
 				holderConstructor,
 				resultTransformer
 		);
 	}
 
 	@Override
     protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow = getResultRow( row, rs, session );
 		return ( holderClass == null && resultRow.length == 1 ?
 				resultRow[ 0 ] :
 				resultRow
 		);
 	}
 
 	@Override
     protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = getColumnNames();
 			int queryCols = returnTypes.length;
 			resultRow = new Object[queryCols];
 			for ( int i = 0; i < queryCols; i++ ) {
 				resultRow[i] = returnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
 		else {
 			resultRow = toResultRow( row );
 		}
 		return resultRow;
 	}
 
 	@Override
     protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		if ( holderClass != null ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = ( Object[] ) results.get( i );
 				try {
 					results.set( i, holderConstructor.newInstance( row ) );
 				}
 				catch ( Exception e ) {
 					throw new QueryException( "could not instantiate: " + holderClass, e );
 				}
 			}
 		}
 		return results;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( selectLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[selectLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInSelect[i] ) result[j++] = row[i];
 			}
 			return result;
 		}
 	}
 
 	void setHolderClass(Class clazz) {
 		holderClass = clazz;
 	}
 
 	@Override
     protected LockMode[] getLockModes(LockOptions lockOptions) {
 
 		// unfortunately this stuff can't be cached because
 		// it is per-invocation, not constant for the
 		// QueryTranslator instance
 		HashMap nameLockOptions = new HashMap();
 		if ( lockOptions == null) {
 			lockOptions = LockOptions.NONE;
 		}
 
 		if ( lockOptions.getAliasLockCount() > 0 ) {
 			Iterator iter = lockOptions.getAliasLockIterator();
 			while ( iter.hasNext() ) {
 				Map.Entry me = ( Map.Entry ) iter.next();
 				nameLockOptions.put( getAliasName( ( String ) me.getKey() ),
 						me.getValue() );
 			}
 		}
 		LockMode[] lockModesArray = new LockMode[names.length];
 		for ( int i = 0; i < names.length; i++ ) {
 			LockMode lm = ( LockMode ) nameLockOptions.get( names[i] );
 			//if ( lm == null ) lm = LockOptions.NONE;
 			if ( lm == null ) lm = lockOptions.getLockMode();
 			lockModesArray[i] = lm;
 		}
 		return lockModesArray;
 	}
 
 	@Override
     protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		final String result;
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 		else {
 			LockOptions locks = new LockOptions();
 			locks.setLockMode(lockOptions.getLockMode());
 			locks.setTimeOut(lockOptions.getTimeOut());
 			locks.setScope(lockOptions.getScope());
 			Iterator iter = lockOptions.getAliasLockIterator();
 			while ( iter.hasNext() ) {
 				Map.Entry me = ( Map.Entry ) iter.next();
 				locks.setAliasSpecificLockMode( getAliasName( ( String ) me.getKey() ), (LockMode) me.getValue() );
 			}
 			Map keyColumnNames = null;
 			if ( dialect.forUpdateOfColumns() ) {
 				keyColumnNames = new HashMap();
 				for ( int i = 0; i < names.length; i++ ) {
 					keyColumnNames.put( names[i], persisters[i].getIdentifierColumnNames() );
 				}
 			}
 			result = dialect.applyLocksToSql( sql, locks, keyColumnNames );
 		}
 		logQuery( queryString, result );
 		return result;
 	}
 
 	@Override
     protected boolean upgradeLocks() {
 		return true;
 	}
 
 	@Override
     protected int[] getCollectionOwners() {
 		return new int[] { collectionOwnerColumn };
 	}
 
 	protected boolean isCompiled() {
 		return compiled;
 	}
 
 	@Override
     public String toString() {
 		return queryString;
 	}
 
 	@Override
     protected int[] getOwners() {
 		return owners;
 	}
 
 	@Override
     protected EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	public Class getHolderClass() {
 		return holderClass;
 	}
 
 	public Map getEnabledFilters() {
 		return enabledFilters;
 	}
 
 	public ScrollableResults scroll(final QueryParameters queryParameters,
 									final SessionImplementor session)
 			throws HibernateException {
 		HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(holderConstructor, queryParameters.getResultTransformer());
 		return scroll( queryParameters, returnTypes, hi, session );
 	}
 
 	@Override
     public String getQueryIdentifier() {
 		return queryIdentifier;
 	}
 
 	@Override
     protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	public void validateScrollability() throws HibernateException {
 		// This is the legacy behaviour for HQL queries...
 		if ( getCollectionPersisters() != null ) {
 			throw new HibernateException( "Cannot scroll queries which initialize collections" );
 		}
 	}
 
 	public boolean containsCollectionFetches() {
 		return false;
 	}
 
 	public boolean isManipulationStatement() {
 		// classic parser does not support bulk manipulation statements
 		return false;
 	}
 
+	@Override
+	public Class getDynamicInstantiationResultType() {
+		return holderClass;
+	}
+
 	public ParameterTranslations getParameterTranslations() {
 		return new ParameterTranslations() {
 
 			public boolean supportsOrdinalParameterMetadata() {
 				// classic translator does not support collection of ordinal
 				// param metadata
 				return false;
 			}
 
 			public int getOrdinalParameterCount() {
 				return 0; // not known!
 			}
 
 			public int getOrdinalParameterSqlLocation(int ordinalPosition) {
 				return 0; // not known!
 			}
 
 			public Type getOrdinalParameterExpectedType(int ordinalPosition) {
 				return null; // not known!
 			}
 
 			public Set getNamedParameterNames() {
 				return namedParameters.keySet();
 			}
 
 			public int[] getNamedParameterSqlLocations(String name) {
 				return getNamedParameterLocs( name );
 			}
 
 			public Type getNamedParameterExpectedType(String name) {
 				return null; // not known!
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
index cacfe4bd9c..e8294c16b5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,608 +1,617 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.loader.hql;
+
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.hql.ast.QueryTranslatorImpl;
 import org.hibernate.hql.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.ast.tree.FromElement;
 import org.hibernate.hql.ast.tree.QueryNode;
 import org.hibernate.hql.ast.tree.SelectClause;
 import org.hibernate.impl.IteratorImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A delegate that implements the Loader part of QueryTranslator.
  *
  * @author josh
  */
 public class QueryLoader extends BasicLoader {
 
 	/**
 	 * The query translator that is delegating to this object.
 	 */
 	private QueryTranslatorImpl queryTranslator;
 
 	private Queryable[] entityPersisters;
 	private String[] entityAliases;
 	private String[] sqlAliases;
 	private String[] sqlAliasSuffixes;
 	private boolean[] includeInSelect;
 
 	private String[] collectionSuffixes;
 
 	private boolean hasScalars;
 	private String[][] scalarColumnNames;
 	//private Type[] sqlResultTypes;
 	private Type[] queryReturnTypes;
 
 	private final Map sqlAliasByEntityAlias = new HashMap(8);
 
 	private EntityType[] ownerAssociationTypes;
 	private int[] owners;
 	private boolean[] entityEagerPropertyFetches;
 
 	private int[] collectionOwners;
 	private QueryableCollection[] collectionPersisters;
 
 	private int selectLength;
 
-	private ResultTransformer implicitResultTransformer;
+	private AggregatedSelectExpression aggregatedSelectExpression;
 	private String[] queryReturnAliases;
 
 	private LockMode[] defaultLockModes;
 
 
 	/**
 	 * Creates a new Loader implementation.
 	 *
 	 * @param queryTranslator The query translator that is the delegator.
 	 * @param factory The factory from which this loader is being created.
 	 * @param selectClause The AST representing the select clause for loading.
 	 */
 	public QueryLoader(
 			final QueryTranslatorImpl queryTranslator,
 	        final SessionFactoryImplementor factory,
 	        final SelectClause selectClause) {
 		super( factory );
 		this.queryTranslator = queryTranslator;
 		initialize( selectClause );
 		postInstantiate();
 	}
 
 	private void initialize(SelectClause selectClause) {
 
 		List fromElementList = selectClause.getFromElementsForLoad();
 
 		hasScalars = selectClause.isScalarSelect();
 		scalarColumnNames = selectClause.getColumnNames();
 		//sqlResultTypes = selectClause.getSqlResultTypes();
 		queryReturnTypes = selectClause.getQueryReturnTypes();
 
-		AggregatedSelectExpression aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
-		implicitResultTransformer = aggregatedSelectExpression == null
-				? null
-				: aggregatedSelectExpression.getResultTransformer();
+		aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
 		queryReturnAliases = selectClause.getQueryReturnAliases();
 
 		List collectionFromElements = selectClause.getCollectionFromElements();
 		if ( collectionFromElements != null && collectionFromElements.size()!=0 ) {
 			int length = collectionFromElements.size();
 			collectionPersisters = new QueryableCollection[length];
 			collectionOwners = new int[length];
 			collectionSuffixes = new String[length];
 			for ( int i=0; i<length; i++ ) {
 				FromElement collectionFromElement = (FromElement) collectionFromElements.get(i);
 				collectionPersisters[i] = collectionFromElement.getQueryableCollection();
 				collectionOwners[i] = fromElementList.indexOf( collectionFromElement.getOrigin() );
 //				collectionSuffixes[i] = collectionFromElement.getColumnAliasSuffix();
 //				collectionSuffixes[i] = Integer.toString( i ) + "_";
 				collectionSuffixes[i] = collectionFromElement.getCollectionSuffix();
 			}
 		}
 
 		int size = fromElementList.size();
 		entityPersisters = new Queryable[size];
 		entityEagerPropertyFetches = new boolean[size];
 		entityAliases = new String[size];
 		sqlAliases = new String[size];
 		sqlAliasSuffixes = new String[size];
 		includeInSelect = new boolean[size];
 		owners = new int[size];
 		ownerAssociationTypes = new EntityType[size];
 
 		for ( int i = 0; i < size; i++ ) {
 			final FromElement element = ( FromElement ) fromElementList.get( i );
 			entityPersisters[i] = ( Queryable ) element.getEntityPersister();
 
 			if ( entityPersisters[i] == null ) {
 				throw new IllegalStateException( "No entity persister for " + element.toString() );
 			}
 
 			entityEagerPropertyFetches[i] = element.isAllPropertyFetch();
 			sqlAliases[i] = element.getTableAlias();
 			entityAliases[i] = element.getClassAlias();
 			sqlAliasByEntityAlias.put( entityAliases[i], sqlAliases[i] );
 			// TODO should we just collect these like with the collections above?
 			sqlAliasSuffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + "_";
 //			sqlAliasSuffixes[i] = element.getColumnAliasSuffix();
 			includeInSelect[i] = !element.isFetch();
 			if ( includeInSelect[i] ) {
 				selectLength++;
 			}
 
 			owners[i] = -1; //by default
 			if ( element.isFetch() ) {
 				if ( element.isCollectionJoin() || element.getQueryableCollection() != null ) {
 					// This is now handled earlier in this method.
 				}
 				else if ( element.getDataType().isEntityType() ) {
 					EntityType entityType = ( EntityType ) element.getDataType();
 					if ( entityType.isOneToOne() ) {
 						owners[i] = fromElementList.indexOf( element.getOrigin() );
 					}
 					ownerAssociationTypes[i] = entityType;
 				}
 			}
 		}
 
 		//NONE, because its the requested lock mode, not the actual! 
 		defaultLockModes = ArrayHelper.fillArray( LockMode.NONE, size );
 	}
 
+	public AggregatedSelectExpression getAggregatedSelectExpression() {
+		return aggregatedSelectExpression;
+	}
+
+
 	// -- Loader implementation --
 
 	public final void validateScrollability() throws HibernateException {
 		queryTranslator.validateScrollability();
 	}
 
 	protected boolean needsFetchingScroll() {
 		return queryTranslator.containsCollectionFetches();
 	}
 
 	public Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	public String[] getAliases() {
 		return sqlAliases;
 	}
 
 	public String[] getSqlAliasSuffixes() {
 		return sqlAliasSuffixes;
 	}
 
 	public String[] getSuffixes() {
 		return getSqlAliasSuffixes();
 	}
 
 	public String[] getCollectionSuffixes() {
 		return collectionSuffixes;
 	}
 
 	protected String getQueryIdentifier() {
 		return queryTranslator.getQueryIdentifier();
 	}
 
 	/**
 	 * The SQL query string to be called.
 	 */
 	protected String getSQLString() {
 		return queryTranslator.getSQLString();
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only collection loaders
 	 * return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return entityEagerPropertyFetches;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner")
 	 */
 	protected int[] getOwners() {
 		return owners;
 	}
 
 	protected EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	// -- Loader overrides --
 
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	/**
 	 * @param lockOptions a collection of lock modes specified dynamically via the Query interface
 	 */
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		if ( lockOptions == null ) {
 			return defaultLockModes;
 		}
 
 		if ( lockOptions.getAliasLockCount() == 0
 				&& ( lockOptions.getLockMode() == null || LockMode.NONE.equals( lockOptions.getLockMode() ) ) ) {
 			return defaultLockModes;
 		}
 
 		// unfortunately this stuff can't be cached because
 		// it is per-invocation, not constant for the
 		// QueryTranslator instance
 
 		LockMode[] lockModesArray = new LockMode[entityAliases.length];
 		for ( int i = 0; i < entityAliases.length; i++ ) {
 			LockMode lockMode = lockOptions.getEffectiveLockMode( entityAliases[i] );
 			if ( lockMode == null ) {
 				//NONE, because its the requested lock mode, not the actual!
 				lockMode = LockMode.NONE;
 			}
 			lockModesArray[i] = lockMode;
 		}
 
 		return lockModesArray;
 	}
 
 	protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		// we are given a map of user-alias -> lock mode
 		// create a new map of sql-alias -> lock mode
 
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 		// we need both the set of locks and the columns to reference in locks
 		// as the ultimate output of this section...
 		final LockOptions locks = new LockOptions( lockOptions.getLockMode() );
 		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 
 		locks.setScope( lockOptions.getScope() );
 		locks.setTimeOut( lockOptions.getTimeOut() );
 
 		final Iterator itr = sqlAliasByEntityAlias.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			final String userAlias = (String) entry.getKey();
 			final String drivingSqlAlias = (String) entry.getValue();
 			if ( drivingSqlAlias == null ) {
 				throw new IllegalArgumentException( "could not locate alias to apply lock mode : " + userAlias );
 			}
 			// at this point we have (drivingSqlAlias) the SQL alias of the driving table
 			// corresponding to the given user alias.  However, the driving table is not
 			// (necessarily) the table against which we want to apply locks.  Mainly,
 			// the exception case here is joined-subclass hierarchies where we instead
 			// want to apply the lock against the root table (for all other strategies,
 			// it just happens that driving and root are the same).
 			final QueryNode select = ( QueryNode ) queryTranslator.getSqlAST();
 			final Lockable drivingPersister = ( Lockable ) select.getFromClause()
 					.findFromElementByUserOrSqlAlias( userAlias, drivingSqlAlias )
 					.getQueryable();
 			final String sqlAlias = drivingPersister.getRootTableAlias( drivingSqlAlias );
 
 			final LockMode effectiveLockMode = lockOptions.getEffectiveLockMode( userAlias );
 			locks.setAliasSpecificLockMode( sqlAlias, effectiveLockMode );
 
 			if ( keyColumnNames != null ) {
 				keyColumnNames.put( sqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 			}
 		}
 
 		// apply the collected locks and columns
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 		// todo : scalars???
 //		if ( row.length != lockModesArray.length ) {
 //			return;
 //		}
 //
 //		for ( int i = 0; i < lockModesArray.length; i++ ) {
 //			if ( LockMode.OPTIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //				final EntityEntry pcEntry =
 //			}
 //			else if ( LockMode.PESSIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //
 //			}
 //		}
 	}
 
 	protected boolean upgradeLocks() {
 		return true;
 	}
 
 	private boolean hasSelectNew() {
-		return implicitResultTransformer != null;
+		return aggregatedSelectExpression != null &&  aggregatedSelectExpression.getResultTransformer() != null;
 	}
 
 	protected String[] getResultRowAliases() {
 		return queryReturnAliases;
 	}
 	
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
+		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
+				? null
+				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.resolveResultTransformer( implicitResultTransformer, resultTransformer );
 	}
 
 	protected boolean[] includeInResultRow() {
 		boolean[] includeInResultTuple = includeInSelect;
 		if ( hasScalars ) {
 			includeInResultTuple = new boolean[ queryReturnTypes.length ];
 			Arrays.fill( includeInResultTuple, true );
 		}
 		return includeInResultTuple;
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		Object[] resultRow = getResultRow( row, rs, session );
 		boolean hasTransform = hasSelectNew() || transformer!=null;
 		return ( ! hasTransform && resultRow.length == 1 ?
 				resultRow[ 0 ] :
 				resultRow
 		);
 	}
 
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = scalarColumnNames;
 			int queryCols = queryReturnTypes.length;
 			resultRow = new Object[queryCols];
 			for ( int i = 0; i < queryCols; i++ ) {
 				resultRow[i] = queryReturnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
 		else {
 			resultRow = toResultRow( row );
 		}
 		return resultRow;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		// meant to handle dynamic instantiation queries...
 		HolderInstantiator holderInstantiator = buildHolderInstantiator( resultTransformer );
 		if ( holderInstantiator.isRequired() ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = ( Object[] ) results.get( i );
 				Object result = holderInstantiator.instantiate(row);
 				results.set( i, result );
 			}
 
 			if ( !hasSelectNew() && resultTransformer != null ) {
 				return resultTransformer.transformList(results);
 			}
 			else {
 				return results;
 			}
 		}
 		else {
 			return results;
 		}
 	}
 
 	private HolderInstantiator buildHolderInstantiator(ResultTransformer queryLocalResultTransformer) {
+		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
+				? null
+				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.getHolderInstantiator(
 				implicitResultTransformer,
 				queryLocalResultTransformer,
 				queryReturnAliases
 		);
 	}
 	// --- Query translator methods ---
 
 	public List list(
 			SessionImplementor session,
 			QueryParameters queryParameters) throws HibernateException {
 		checkQuery( queryParameters );
 		return list( session, queryParameters, queryTranslator.getQuerySpaces(), queryReturnTypes );
 	}
 
 	private void checkQuery(QueryParameters queryParameters) {
 		if ( hasSelectNew() && queryParameters.getResultTransformer() != null ) {
 			throw new QueryException( "ResultTransformer is not allowed for 'select new' queries." );
 		}
 	}
 
 	public Iterator iterate(
 			QueryParameters queryParameters,
 			EventSource session) throws HibernateException {
 		checkQuery( queryParameters );
 		final boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.currentTimeMillis();
 		}
 
 		try {
 			final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 			if ( queryParameters.isCallable() ) {
 				throw new QueryException("iterate() not supported for callable statements");
 			}
 			final ResultSet rs = getResultSet(st, queryParameters.hasAutoDiscoverScalarTypes(), false, queryParameters.getRowSelection(), session);
 			final Iterator result = new IteratorImpl(
 					rs,
 			        st,
 			        session,
 			        queryParameters.isReadOnly( session ),
 			        queryReturnTypes,
 			        queryTranslator.getColumnNames(),
 			        buildHolderInstantiator( queryParameters.getResultTransformer() )
 			);
 
 			if ( stats ) {
 				session.getFactory().getStatisticsImplementor().queryExecuted(
 //						"HQL: " + queryTranslator.getQueryString(),
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
 				);
 			}
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query using iterate",
 			        getSQLString()
 				);
 		}
 
 	}
 
 	public ScrollableResults scroll(
 			final QueryParameters queryParameters,
 	        final SessionImplementor session) throws HibernateException {
 		checkQuery( queryParameters );
 		return scroll( 
 				queryParameters,
 				queryReturnTypes,
 				buildHolderInstantiator( queryParameters.getResultTransformer() ),
 				session
 		);
 	}
 
 	// -- Implementation private methods --
 
 	private Object[] toResultRow(Object[] row) {
 		if ( selectLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[selectLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					result[j++] = row[i];
 				}
 			}
 			return result;
 		}
 	}
 
 	/**
 	 * Returns the locations of all occurrences of the named parameter.
 	 */
 	public int[] getNamedParameterLocs(String name) throws QueryException {
 		return queryTranslator.getParameterTranslations().getNamedParameterSqlLocations( name );
 	}
 
 	/**
 	 * We specifically override this method here, because in general we know much more
 	 * about the parameters and their appropriate bind positions here then we do in
 	 * our super because we track them explciitly here through the ParameterSpecification
 	 * interface.
 	 *
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException {
 //		int position = bindFilterParameterValues( statement, queryParameters, startIndex, session );
 		int position = startIndex;
 //		List parameterSpecs = queryTranslator.getSqlAST().getWalker().getParameters();
 		List parameterSpecs = queryTranslator.getCollectedParameterSpecifications();
 		Iterator itr = parameterSpecs.iterator();
 		while ( itr.hasNext() ) {
 			ParameterSpecification spec = ( ParameterSpecification ) itr.next();
 			position += spec.bind( statement, queryParameters, session, position );
 		}
 		return position - startIndex;
 	}
 
 	private int bindFilterParameterValues(
 			PreparedStatement st,
 			QueryParameters queryParameters,
 			int position,
 			SessionImplementor session) throws SQLException {
 		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
 		// see the discussion there in DynamicFilterParameterSpecification's javadocs as to why
 		// it is currently not done that way.
 		int filteredParamCount = queryParameters.getFilteredPositionalParameterTypes() == null
 				? 0
 				: queryParameters.getFilteredPositionalParameterTypes().length;
 		int nonfilteredParamCount = queryParameters.getPositionalParameterTypes() == null
 				? 0
 				: queryParameters.getPositionalParameterTypes().length;
 		int filterParamCount = filteredParamCount - nonfilteredParamCount;
 		for ( int i = 0; i < filterParamCount; i++ ) {
 			Type type = queryParameters.getFilteredPositionalParameterTypes()[i];
 			Object value = queryParameters.getFilteredPositionalParameterValues()[i];
 			type.nullSafeSet( st, value, position, session );
 			position += type.getColumnSpan( getFactory() );
 		}
 
 		return position;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
index 98f5a1b5b1..71a0201046 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
@@ -1,1283 +1,1454 @@
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
 package org.hibernate.ejb;
 
-import java.io.IOException;
-import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
-import java.io.Serializable;
-import java.util.ArrayList;
-import java.util.Collections;
-import java.util.HashMap;
-import java.util.List;
-import java.util.Map;
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
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
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.Tuple;
 import javax.persistence.TupleElement;
 import javax.persistence.TypedQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.Selection;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.transaction.SystemException;
 import javax.transaction.TransactionManager;
+import java.io.IOException;
+import java.io.ObjectInputStream;
+import java.io.ObjectOutputStream;
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.jboss.logging.Logger;
+
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
 import org.hibernate.cfg.Environment;
 import org.hibernate.ejb.criteria.CriteriaQueryCompiler;
 import org.hibernate.ejb.criteria.ValueHandlerFactory;
 import org.hibernate.ejb.criteria.expression.CompoundSelectionImpl;
 import org.hibernate.ejb.util.CacheModeHelper;
 import org.hibernate.ejb.util.ConfigurationHelper;
 import org.hibernate.ejb.util.LockModeTypeHelper;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.spi.JoinStatus;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.synchronization.spi.AfterCompletionAction;
 import org.hibernate.engine.transaction.synchronization.spi.ExceptionMapper;
 import org.hibernate.engine.transaction.synchronization.spi.ManagedFlushChecker;
 import org.hibernate.engine.transaction.synchronization.spi.SynchronizationCallbackCoordinator;
 import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.transform.BasicTransformerAdapter;
-import org.jboss.logging.Logger;
+import org.hibernate.type.Type;
 
 /**
  * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public abstract class AbstractEntityManagerImpl implements HibernateEntityManagerImplementor, Serializable {
 
     private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
                                                                            AbstractEntityManagerImpl.class.getName());
 
 	private static final List<String> entityManagerSpecificProperties = new ArrayList<String>();
 
 	static {
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_SCOPE );
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_TIMEOUT );
 		entityManagerSpecificProperties.add( AvailableSettings.FLUSH_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_STORE_MODE );
 		entityManagerSpecificProperties.add( QueryHints.SPEC_HINT_TIMEOUT );
 	}
 
 	private EntityManagerFactoryImpl entityManagerFactory;
 	protected transient TransactionImpl tx = new TransactionImpl( this );
 	protected PersistenceContextType persistenceContextType;
 	private PersistenceUnitTransactionType transactionType;
 	private Map<String, Object> properties;
 	private LockOptions lockOptions;
 
 	protected AbstractEntityManagerImpl(
 			EntityManagerFactoryImpl entityManagerFactory,
 			PersistenceContextType type,
 			PersistenceUnitTransactionType transactionType,
 			Map properties) {
 		this.entityManagerFactory = entityManagerFactory;
 		this.persistenceContextType = type;
 		this.transactionType = transactionType;
 
 		this.lockOptions = new LockOptions();
 		this.properties = new HashMap<String, Object>();
 		if ( properties != null ) {
 			for ( String key : entityManagerSpecificProperties ) {
 				if ( properties.containsKey( key ) ) {
 					this.properties.put( key, properties.get( key ) );
 				}
 			}
 		}
 	}
 
 	public PersistenceUnitTransactionType getTransactionType() {
 		return transactionType;
 	}
 
 	protected void postInit() {
 		//register in Sync if needed
 		if ( PersistenceUnitTransactionType.JTA.equals( transactionType ) ) {
 			joinTransaction( true );
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
 			query.setHint ( QueryHints.SPEC_HINT_TIMEOUT, queryTimeout );
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
 		return ( CacheStoreMode ) properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE );
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
 			if ( timeout < 0 ) {
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
 
 	public Query createQuery(String jpaqlString) {
 		try {
 			return applyProperties( new QueryImpl<Object>( getSession().createQuery( jpaqlString ), this ) );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public <T> TypedQuery<T> createQuery(String jpaqlString, Class<T> resultClass) {
 		try {
+			// do the translation
 			org.hibernate.Query hqlQuery = getSession().createQuery( jpaqlString );
-			if ( hqlQuery.getReturnTypes().length != 1 ) {
-				throw new IllegalArgumentException( "Cannot create TypedQuery for query with more than one return" );
+
+			// do some validation checking
+			if ( Object[].class.equals( resultClass ) ) {
+				// no validation needed
 			}
-			if ( !resultClass.isAssignableFrom( hqlQuery.getReturnTypes()[0].getReturnedClass() ) ) {
-				throw new IllegalArgumentException(
-						"Type specified for TypedQuery [" +
-								resultClass.getName() +
-								"] is incompatible with query return type [" +
-								hqlQuery.getReturnTypes()[0].getReturnedClass() + "]"
-				);
+			else if ( Tuple.class.equals( resultClass ) ) {
+				TupleBuilderTransformer tupleTransformer = new TupleBuilderTransformer( hqlQuery );
+				hqlQuery.setResultTransformer( tupleTransformer  );
 			}
+			else {
+				final HQLQueryPlan queryPlan = unwrap( SessionImplementor.class )
+						.getFactory()
+						.getQueryPlanCache()
+						.getHQLQueryPlan( jpaqlString, false, null );
+				final Class dynamicInstantiationClass = queryPlan.getDynamicInstantiationResultType();
+				if ( dynamicInstantiationClass != null ) {
+					if ( ! resultClass.isAssignableFrom( dynamicInstantiationClass ) ) {
+						throw new IllegalArgumentException(
+								"Mismatch in requested result type [" + resultClass.getName() +
+										"] and actual result type [" + dynamicInstantiationClass.getName() + "]"
+						);
+					}
+				}
+				else if ( hqlQuery.getReturnTypes().length == 1 ) {
+					// if we have only a single return expression, its java type should match with the requested type
+					if ( !resultClass.isAssignableFrom( hqlQuery.getReturnTypes()[0].getReturnedClass() ) ) {
+						throw new IllegalArgumentException(
+								"Type specified for TypedQuery [" +
+										resultClass.getName() +
+										"] is incompatible with query return type [" +
+										hqlQuery.getReturnTypes()[0].getReturnedClass() + "]"
+						);
+					}
+				}
+				else {
+					throw new IllegalArgumentException(
+							"Cannot create TypedQuery for query with more than one return using requested result type [" +
+									resultClass.getName() + "]"
+					);
+				}
+			}
+
+			// finally, build/return the query instance
 			return new QueryImpl<T>( hqlQuery, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
+	public static class TupleBuilderTransformer extends BasicTransformerAdapter {
+		private List<TupleElement<?>> tupleElements;
+		private Map<String,HqlTupleElementImpl> tupleElementsByAlias;
+
+		public TupleBuilderTransformer(org.hibernate.Query hqlQuery) {
+			final Type[] resultTypes = hqlQuery.getReturnTypes();
+			final int tupleSize = resultTypes.length;
+
+			this.tupleElements = CollectionHelper.arrayList( tupleSize );
+
+			final String[] aliases = hqlQuery.getReturnAliases();
+			final boolean hasAliases = aliases != null && aliases.length > 0;
+			this.tupleElementsByAlias = hasAliases
+					? CollectionHelper.mapOfSize( tupleSize )
+					: Collections.<String, HqlTupleElementImpl>emptyMap();
+
+			for ( int i = 0; i < tupleSize; i++ ) {
+				final HqlTupleElementImpl tupleElement = new HqlTupleElementImpl(
+						i,
+						aliases == null ? null : aliases[i],
+						resultTypes[i]
+				);
+				tupleElements.add( tupleElement );
+				if ( hasAliases ) {
+					final String alias = aliases[i];
+					if ( alias != null ) {
+						tupleElementsByAlias.put( alias, tupleElement );
+					}
+				}
+			}
+		}
+
+		@Override
+		public Object transformTuple(Object[] tuple, String[] aliases) {
+			if ( tuple.length != tupleElements.size() ) {
+				throw new IllegalArgumentException(
+						"Size mismatch between tuple result [" + tuple.length + "] and expected tuple elements [" +
+								tupleElements.size() + "]"
+				);
+			}
+			return new HqlTupleImpl( tuple );
+		}
+
+		public static class HqlTupleElementImpl<X> implements TupleElement<X> {
+			private final int position;
+			private final String alias;
+			private final Type hibernateType;
+
+			public HqlTupleElementImpl(int position, String alias, Type hibernateType) {
+				this.position = position;
+				this.alias = alias;
+				this.hibernateType = hibernateType;
+			}
+
+			@Override
+			public Class getJavaType() {
+				return hibernateType.getReturnedClass();
+			}
+
+			@Override
+			public String getAlias() {
+				return alias;
+			}
+
+			public int getPosition() {
+				return position;
+			}
+
+			public Type getHibernateType() {
+				return hibernateType;
+			}
+		}
+
+		public class HqlTupleImpl implements Tuple {
+			private Object[] tuple;
+
+			public HqlTupleImpl(Object[] tuple) {
+				this.tuple = tuple;
+			}
+
+			@Override
+			public <X> X get(String alias, Class<X> type) {
+				return (X) get( alias );
+			}
+
+			@Override
+			public Object get(String alias) {
+				HqlTupleElementImpl tupleElement = tupleElementsByAlias.get( alias );
+				if ( tupleElement == null ) {
+					throw new IllegalArgumentException( "Unknown alias [" + alias + "]" );
+				}
+				return tuple[ tupleElement.getPosition() ];
+			}
+
+			@Override
+			public <X> X get(int i, Class<X> type) {
+				return (X) get( i );
+			}
+
+			@Override
+			public Object get(int i) {
+				if ( i < 0 ) {
+					throw new IllegalArgumentException( "requested tuple index must be greater than zero" );
+				}
+				if ( i > tuple.length ) {
+					throw new IllegalArgumentException( "requested tuple index exceeds actual tuple size" );
+				}
+				return tuple[i];
+			}
+
+			@Override
+			public Object[] toArray() {
+				// todo : make a copy?
+				return tuple;
+			}
+
+			@Override
+			public List<TupleElement<?>> getElements() {
+				return tupleElements;
+			}
+
+			@Override
+			public <X> X get(TupleElement<X> tupleElement) {
+				if ( HqlTupleElementImpl.class.isInstance( tupleElement ) ) {
+					return get( ( (HqlTupleElementImpl) tupleElement ).getPosition(), tupleElement.getJavaType() );
+				}
+				else {
+					return get( tupleElement.getAlias(), tupleElement.getJavaType() );
+				}
+			}
+		}
+	}
+
 	public <T> TypedQuery<T> createQuery(
 			String jpaqlString,
 			Class<T> resultClass,
 			Selection selection,
 			Options options) {
 		try {
 			org.hibernate.Query hqlQuery = getSession().createQuery( jpaqlString );
 
 			if ( options.getValueHandlers() == null ) {
 				options.getResultMetadataValidator().validate( hqlQuery.getReturnTypes() );
 			}
 
 			// determine if we need a result transformer
 			List tupleElements = Tuple.class.equals( resultClass )
 					? ( ( CompoundSelectionImpl<Tuple> ) selection ).getCompoundSelectionItems()
 					: null;
 			if ( options.getValueHandlers() != null || tupleElements != null ) {
 				hqlQuery.setResultTransformer(
 						new CriteriaQueryTransformer( options.getValueHandlers(), tupleElements )
 				);
 			}
 			return new QueryImpl<T>( hqlQuery, this, options.getNamedParameterExplicitTypes() );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
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
 				return ( X ) get( alias );
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
 				return ( X ) get( i );
 			}
 
 			public Object[] toArray() {
 				return tuples;
 			}
 
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 		}
 	}
 
 	private CriteriaQueryCompiler criteriaQueryCompiler;
 
 	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
 		if ( criteriaQueryCompiler == null ) {
 			criteriaQueryCompiler = new CriteriaQueryCompiler( this );
 		}
 		return criteriaQueryCompiler.compile( criteriaQuery );
 	}
 
 	public Query createNamedQuery(String name) {
 		try {
 			org.hibernate.Query namedQuery = getSession().getNamedQuery( name );
 			try {
 				return new QueryImpl( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
 		try {
 			/*
 			 * Get the named query.
 			 * If the named query is a SQL query, get the expected returned type from the query definition
 			 * or its associated result set mapping
 			 * If the named query is a HQL query, use getReturnType()
 			 */
 			org.hibernate.Query namedQuery = getSession().getNamedQuery( name );
 			//TODO clean this up to avoid downcasting
 			final SessionFactoryImplementor factoryImplementor = ( SessionFactoryImplementor ) entityManagerFactory.getSessionFactory();
 			final NamedSQLQueryDefinition queryDefinition = factoryImplementor.getNamedSQLQuery( name );
 			try {
 				if ( queryDefinition != null ) {
 					Class<?> actualReturnedClass;
 
 					final NativeSQLQueryReturn[] queryReturns;
 					if ( queryDefinition.getQueryReturns() != null ) {
 						queryReturns = queryDefinition.getQueryReturns();
 					}
 					else if ( queryDefinition.getResultSetRef() != null ) {
 						final ResultSetMappingDefinition rsMapping = factoryImplementor.getResultSetMapping(
 								queryDefinition.getResultSetRef()
 						);
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
 						final String entityClassName = ( ( NativeSQLQueryRootReturn ) nativeSQLQueryReturn ).getReturnEntityName();
 						try {
 							actualReturnedClass = ReflectHelper.classForName( entityClassName, AbstractEntityManagerImpl.class );
 						}
 						catch ( ClassNotFoundException e ) {
 							throw new AssertionFailure( "Unable to instantiate class declared on named native query: " + name + " " + entityClassName );
 						}
 						if ( !resultClass.isAssignableFrom( actualReturnedClass ) ) {
 							throw buildIncompatibleException( resultClass, actualReturnedClass );
 						}
 					}
 					else {
 						//TODO support other NativeSQLQueryReturn type. For now let it go.
 					}
 				}
 				else {
 					if ( namedQuery.getReturnTypes().length != 1 ) {
 						throw new IllegalArgumentException( "Cannot create TypedQuery for query with more than one return" );
 					}
 					if ( !resultClass.isAssignableFrom( namedQuery.getReturnTypes()[0].getReturnedClass() ) ) {
 						throw buildIncompatibleException( resultClass, namedQuery.getReturnTypes()[0].getReturnedClass() );
 					}
 				}
 				return new QueryImpl<T>( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	private IllegalArgumentException buildIncompatibleException(Class<?> resultClass, Class<?> actualResultClass) {
 		return new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									actualResultClass + "]"
 					);
 	}
 
 	public Query createNativeQuery(String sqlString) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public Query createNativeQuery(String sqlString, Class resultClass) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			q.addEntity( "alias1", resultClass.getName(), LockMode.READ );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public Query createNativeQuery(String sqlString, String resultSetMapping) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			q.setResultSetMapping( resultSetMapping );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
 		try {
 			return ( T ) getSession().load( entityClass, ( Serializable ) primaryKey );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey) {
 		return find( entityClass, primaryKey, null, null );
 	}
 
 	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
 		return find( entityClass, primaryKey, null, properties );
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType) {
 		return find( entityClass, primaryKey, lockModeType, null );
 	}
 
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
 		CacheMode previousCacheMode = getSession().getCacheMode();
 		CacheMode cacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			getSession().setCacheMode( cacheMode );
 			if ( lockModeType != null ) {
 				return ( A ) getSession().get(
 						entityClass, ( Serializable ) primaryKey,
 						getLockRequest( lockModeType, properties )
 				);
 			}
 			else {
 				return ( A ) getSession().get( entityClass, ( Serializable ) primaryKey );
 			}
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
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			getSession().setCacheMode( previousCacheMode );
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
 		if ( persistenceContextType == PersistenceContextType.TRANSACTION && !isTransactionInProgress() ) {
 			//no need to mark as rollback, no tx in progress
 			throw new TransactionRequiredException(
 					"no transaction is in progress for a TRANSACTION type persistence context"
 			);
 		}
 	}
 
 	public void persist(Object entity) {
 		checkTransactionNeeded();
 		try {
 			getSession().persist( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage() );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A merge(A entity) {
 		checkTransactionNeeded();
 		try {
 			return ( A ) getSession().merge( entity );
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw new IllegalArgumentException( sse );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	public void remove(Object entity) {
 		checkTransactionNeeded();
 		try {
 			getSession().delete( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	public void refresh(Object entity) {
 		refresh( entity, null, null );
 	}
 
 	public void refresh(Object entity, Map<String, Object> properties) {
 		refresh( entity, null, properties );
 	}
 
 	public void refresh(Object entity, LockModeType lockModeType) {
 		refresh( entity, lockModeType, null );
 	}
 
 	public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		checkTransactionNeeded();
 		CacheMode previousCacheMode = getSession().getCacheMode();
 		CacheMode localCacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			getSession().setCacheMode( localCacheMode );
 			if ( !getSession().contains( entity ) ) {
 				throw new IllegalArgumentException( "Entity not managed" );
 			}
 			if ( lockModeType != null ) {
 				getSession().refresh( entity, ( lockOptions = getLockRequest( lockModeType, properties ) ) );
 			}
 			else {
 				getSession().refresh( entity );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			getSession().setCacheMode( previousCacheMode );
 		}
 	}
 
 	public boolean contains(Object entity) {
 		try {
 			if ( entity != null
 					&& !( entity instanceof HibernateProxy )
 					&& getSession().getSessionFactory().getClassMetadata( entity.getClass() ) == null ) {
 				throw new IllegalArgumentException( "Not an entity:" + entity.getClass() );
 			}
 			return getSession().contains( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public LockModeType getLockMode(Object entity) {
 		if ( !contains( entity ) ) {
 			throw new IllegalArgumentException( "entity not in the persistence context" );
 		}
 		return getLockModeType( getSession().getCurrentLockMode( entity ) );
 	}
 
 	public void setProperty(String s, Object o) {
 		if ( entityManagerSpecificProperties.contains( s ) ) {
 			properties.put( s, o );
 			applyProperties();
         } else LOG.debugf("Trying to set a property which is not supported on entity manager level");
 	}
 
 	public Map<String, Object> getProperties() {
 		return Collections.unmodifiableMap( properties );
 	}
 
 	public void flush() {
 		try {
 			if ( !isTransactionInProgress() ) {
 				throw new TransactionRequiredException( "no transaction is in progress" );
 			}
 			getSession().flush();
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
 	 */
 	protected abstract Session getRawSession();
 
 	public EntityTransaction getTransaction() {
 		if ( transactionType == PersistenceUnitTransactionType.JTA ) {
 			throw new IllegalStateException( "A JTA EntityManager cannot use getTransaction()" );
 		}
 		return tx;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityManagerFactoryImpl getEntityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public HibernateEntityManagerFactory getFactory() {
 		return entityManagerFactory;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaBuilder getCriteriaBuilder() {
 		return getEntityManagerFactory().getCriteriaBuilder();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Metamodel getMetamodel() {
 		return getEntityManagerFactory().getMetamodel();
 	}
 
 	public void setFlushMode(FlushModeType flushModeType) {
 		if ( flushModeType == FlushModeType.AUTO ) {
 			getSession().setFlushMode( FlushMode.AUTO );
 		}
 		else if ( flushModeType == FlushModeType.COMMIT ) {
 			getSession().setFlushMode( FlushMode.COMMIT );
 		}
 		else {
 			throw new AssertionFailure( "Unknown FlushModeType: " + flushModeType );
 		}
 	}
 
 	public void clear() {
 		try {
 			getSession().clear();
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public void detach(Object entity) {
 		try {
 			getSession().evict( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * Hibernate can be set in various flush modes that are unknown to
 	 * JPA 2.0. This method can then return null.
 	 * If it returns null, do em.unwrap(Session.class).getFlushMode() to get the
 	 * Hibernate flush mode
 	 */
 	public FlushModeType getFlushMode() {
 		FlushMode mode = getSession().getFlushMode();
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
 		LockOptions lockOptions = null;
 		try {
 			if ( !isTransactionInProgress() ) {
 				throw new TransactionRequiredException( "no transaction is in progress" );
 			}
 			if ( !contains( entity ) ) {
 				throw new IllegalArgumentException( "entity not in the persistence context" );
 			}
 			getSession().buildLockRequest( ( lockOptions = getLockRequest( lockModeType, properties ) ) )
 					.lock( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
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
 		return ( ( SessionImplementor ) getRawSession() ).isTransactionInProgress();
 	}
 
 	private SessionFactoryImplementor sfi() {
 		return ( SessionFactoryImplementor ) getRawSession().getSessionFactory();
 	}
 
 	protected void markAsRollback() {
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
 					transactionManager.setRollbackOnly();
 				}
 				catch ( SystemException e ) {
 					throw new PersistenceException( "Unable to set the JTA transaction as RollbackOnly", e );
 				}
 			}
 		}
 	}
 
 	public void joinTransaction() {
 		joinTransaction( false );
 	}
 
 	public <T> T unwrap(Class<T> clazz) {
 		if ( Session.class.isAssignableFrom( clazz ) ) {
 			return ( T ) getSession();
 		}
 		if ( SessionImplementor.class.isAssignableFrom( clazz ) ) {
 			return ( T ) getSession();
 		}
 		if ( EntityManager.class.isAssignableFrom( clazz ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap " + clazz );
 	}
 
 	private void joinTransaction(boolean ignoreNotJoining) {
 		if ( transactionType != PersistenceUnitTransactionType.JTA ) {
 			if ( !ignoreNotJoining ) {
 			    LOG.callingJoinTransactionOnNonJtaEntityManager();
 			}
 			return;
 		}
 
 		final SessionImplementor session = (SessionImplementor) getSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		transaction.markForJoin();
 		transactionCoordinator.pulse();
 
 		LOG.debug( "Looking for a JTA transaction to join" );
 		if ( ! transactionCoordinator.isTransactionJoinable() ) {
             LOG.unableToJoinTransaction(Environment.TRANSACTION_STRATEGY);
 		}
 
 		try {
 
 			if ( transaction.getJoinStatus() == JoinStatus.JOINED ) {
 				LOG.debug( "Transaction already joined" );
 				return; // noop
 			}
 
 			// join the transaction and then recheck the status
 			transaction.join();
 			if ( transaction.getJoinStatus() == JoinStatus.NOT_JOINED ) {
 				if ( ignoreNotJoining ) {
 					LOG.debug( "No JTA transaction found" );
 					return;
 				}
 				else {
 					throw new TransactionRequiredException( "No active JTA transaction on joinTransaction call" );
 				}
 			}
 			else if ( transaction.getJoinStatus() == JoinStatus.MARKED_FOR_JOINED ) {
 				throw new AssertionFailure( "Transaction MARKED_FOR_JOINED after isOpen() call" );
 			}
 
 			// register behavior changes
 			SynchronizationCallbackCoordinator callbackCoordinator = transactionCoordinator.getSynchronizationCallbackCoordinator();
 			callbackCoordinator.setManagedFlushChecker( new ManagedFlushCheckerImpl() );
 			callbackCoordinator.setExceptionMapper( new CallbackExceptionMapperImpl() );
 			callbackCoordinator.setAfterCompletionAction( new AfterCompletionActionImpl( session, transactionType ) );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * returns the underlying session
 	 */
 	public Object getDelegate() {
 		return getSession();
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		oos.defaultWriteObject();
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		tx = new TransactionImpl( this );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
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
 			markAsRollback();
 		}
 		catch ( Exception ne ) {
 			//we do not want the subsequent exception to swallow the original one
             LOG.unableToMarkForRollbackOnPersistenceException(ne);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void throwPersistenceException(PersistenceException e) {
 		handlePersistenceException( e );
 		throw e;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	//FIXME should we remove all calls to this method and use convert(RuntimeException) ?
 	public RuntimeException convert(HibernateException e) {
 		return convert( e, null );
 	}
 
 	public RuntimeException convert(RuntimeException e) {
 		RuntimeException result = e;
 		if ( e instanceof HibernateException ) {
 			result = convert( ( HibernateException ) e );
 		}
 		else {
 			markAsRollback();
 		}
 		return result;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
 		if ( e instanceof StaleStateException ) {
 			PersistenceException converted = wrapStaleStateException( ( StaleStateException ) e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.OptimisticLockException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.QueryTimeoutException ) {
 			QueryTimeoutException converted = new QueryTimeoutException(e.getMessage(), e);
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof ObjectNotFoundException ) {
 			EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.NonUniqueResultException ) {
 			NonUniqueResultException converted = new NonUniqueResultException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof UnresolvableObjectException ) {
 			EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof QueryException ) {
 			return new IllegalArgumentException( e );
 		}
 		else if ( e instanceof TransientObjectException ) {
 			try {
 				markAsRollback();
 			}
 			catch ( Exception ne ) {
 				//we do not want the subsequent exception to swallow the original one
                 LOG.unableToMarkForRollbackOnTransientObjectException(ne);
 			}
 			return new IllegalStateException( e ); //Spec 3.2.3 Synchronization rules
 		}
 		else {
 			PersistenceException converted = new PersistenceException( e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void throwPersistenceException(HibernateException e) {
 		throw convert( e );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public PersistenceException wrapStaleStateException(StaleStateException e) {
 		PersistenceException pe;
 		if ( e instanceof StaleObjectStateException ) {
 			StaleObjectStateException sose = ( StaleObjectStateException ) e;
 			Serializable identifier = sose.getIdentifier();
 			if ( identifier != null ) {
 				try {
 					Object entity = getRawSession().load( sose.getEntityName(), identifier );
 					if ( entity instanceof Serializable ) {
 						//avoid some user errors regarding boundary crossing
 						pe = new OptimisticLockException( null, e, entity );
 					}
 					else {
 						pe = new OptimisticLockException( e );
 					}
 				}
 				catch ( EntityNotFoundException enfe ) {
 					pe = new OptimisticLockException( e );
 				}
 			}
 			else {
 				pe = new OptimisticLockException( e );
 			}
 		}
 		else {
 			pe = new OptimisticLockException( e );
 		}
 		return pe;
 	}
 
 	public PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
 		PersistenceException pe;
 		if ( e instanceof org.hibernate.OptimisticLockException ) {
 			org.hibernate.OptimisticLockException ole = ( org.hibernate.OptimisticLockException ) e;
 			pe = new OptimisticLockException( ole.getMessage(), ole, ole.getEntity() );
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
 			org.hibernate.PessimisticLockException ple = ( org.hibernate.PessimisticLockException ) e;
 			if ( lockOptions != null && lockOptions.getTimeOut() > -1 ) {
 				// assume lock timeout occurred if a timeout or NO WAIT was specified
 				pe = new LockTimeoutException( ple.getMessage(), ple, ple.getEntity() );
 			}
 			else {
 				pe = new PessimisticLockException( ple.getMessage(), ple, ple.getEntity() );
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
 			return ! coordinator.getTransactionContext().isClosed() &&
 					! coordinator.getTransactionContext().isFlushModeNever() &&
 					! JtaStatusHelper.isRollback( jtaStatus );
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
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
index 3d17820564..36b0fd00be 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/QueryTest.java
@@ -1,533 +1,561 @@
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
 package org.hibernate.ejb.test.query;
 
 import javax.persistence.EntityManager;
 import javax.persistence.Query;
 import javax.persistence.TemporalType;
+import javax.persistence.Tuple;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import org.hibernate.Hibernate;
 import org.hibernate.ejb.test.BaseEntityManagerFunctionalTestCase;
 import org.hibernate.ejb.test.Distributor;
 import org.hibernate.ejb.test.Item;
 import org.hibernate.ejb.test.Wallet;
 
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard
  */
 public class QueryTest extends BaseEntityManagerFunctionalTestCase {
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 		Query q = em.createQuery( "select item from Item item where item.name in :names" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		List params = new ArrayList();
 		params.add( item.getName() );
 		q.setParameter( "names", params );
 		List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 1, result.size() );
 
 		q = em.createQuery( "select item from Item item where item.name in :names" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		params.add( item2.getName() );
 		q.setParameter( "names", params );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 
 		q = em.createQuery( "select item from Item item where item.name in ?1" );
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
 
 	@Test
 	public void testParameterListInExistingParens() throws Exception {
 		final Item item = new Item( "Mouse", "Micro$oft mouse" );
 		final Item item2 = new Item( "Computer", "Dell computer" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		em.persist( item2 );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
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
 
 		q = em.createQuery( "select item from Item item where item.name in ( \n :names \n)\n" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		params = new ArrayList();
 		params.add( item.getName() );
 		params.add( item2.getName() );
 		q.setParameter( "names", params );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 		Query query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.brand in ?1" );
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
 	@Test
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
 
+	@Test
+	public void testTypedScalarQueries() {
+		EntityManager em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		Item item = new Item( "Mouse", "Micro$oft mouse" );
+		em.persist( item );
+		assertTrue( em.contains( item ) );
+		em.getTransaction().commit();
+
+		em.getTransaction().begin();
+		Object[] itemData = em.createQuery( "select i.name,i.descr from Item i", Object[].class ).getSingleResult();
+		assertEquals( 2, itemData.length );
+		assertEquals( String.class, itemData[0].getClass() );
+		assertEquals( String.class, itemData[1].getClass() );
+		Tuple itemTuple = em.createQuery( "select i.name,i.descr from Item i", Tuple.class ).getSingleResult();
+		assertEquals( 2, itemTuple.getElements().size() );
+		assertEquals( String.class, itemTuple.get( 0 ).getClass() );
+		assertEquals( String.class, itemTuple.get( 1 ).getClass() );
+		Item itemView = em.createQuery( "select new Item(i.name,i.descr) from Item i", Item.class ).getSingleResult();
+		assertNotNull( itemView );
+		assertEquals( "Micro$oft mouse", itemView.getDescr() );
+		em.remove( item );
+		em.getTransaction().commit();
+
+		em.close();
+	}
+
 	@Override
 	public Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Item.class,
 				Distributor.class,
 				Wallet.class
 		};
 	}
 }
