diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
index 81e7939c63..69acd2e7f5 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
@@ -1,1243 +1,1243 @@
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
 package org.hibernate.hql.internal.classic;
 
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.hql.internal.NameGenerator;
 import org.hibernate.hql.spi.FilterTranslator;
 import org.hibernate.hql.spi.ParameterTranslations;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.QuerySelect;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * An instance of <tt>QueryTranslator</tt> translates a Hibernate
  * query string to SQL.
  */
 public class QueryTranslatorImpl extends BasicLoader implements FilterTranslator {
     private static final CoreMessageLogger LOG = CoreLogging.messageLogger( QueryTranslatorImpl.class );
 
 	private static final String[] NO_RETURN_ALIASES = new String[] {};
 
 	private final String queryIdentifier;
 	private final String queryString;
 
 	private final Map typeMap = new LinkedHashMap();
 	private final Map collections = new LinkedHashMap();
 	private List returnedTypes = new ArrayList();
 	private final List fromTypes = new ArrayList();
 	private final List scalarTypes = new ArrayList();
 	private final Map namedParameters = new HashMap();
 	private final Map aliasNames = new HashMap();
 	private final Map oneToOneOwnerNames = new HashMap();
 	private final Map uniqueKeyOwnerReferences = new HashMap();
 	private final Map decoratedPropertyMappings = new HashMap();
 
 	private final List scalarSelectTokens = new ArrayList();
 	private final List whereTokens = new ArrayList();
 	private final List havingTokens = new ArrayList();
 	private final Map joins = new LinkedHashMap();
 	private final List orderByTokens = new ArrayList();
 	private final List groupByTokens = new ArrayList();
 	private final Set<Serializable> querySpaces = new HashSet<Serializable>();
 	private final Set entitiesToFetch = new HashSet();
 
 	private final Map pathAliases = new HashMap();
 	private final Map pathJoins = new HashMap();
 
 	private Queryable[] persisters;
 	private int[] owners;
 	private EntityType[] ownerAssociationTypes;
 	private String[] names;
 	private boolean[] includeInSelect;
 	private int selectLength;
 	private Type[] returnTypes;
 	private Type[] actualReturnTypes;
 	private String[][] scalarColumnNames;
 	private Map tokenReplacements;
 	private int nameCount;
 	private int parameterCount;
 	private boolean distinct;
 	private boolean compiled;
 	private String sqlString;
 	private Class holderClass;
 	private Constructor holderConstructor;
 	private boolean hasScalars;
 	private boolean shallowQuery;
 	private QueryTranslatorImpl superQuery;
 
 	private QueryableCollection collectionPersister;
 	private int collectionOwnerColumn = -1;
 	private String collectionOwnerName;
 	private String fetchName;
 
 	private String[] suffixes;
 
 	private Map enabledFilters;
 
 	/**
 	 * Construct a query translator
 	 *
 	 * @param queryIdentifier A unique identifier for the query of which this
 	 * translation is part; typically this is the original, user-supplied query string.
 	 * @param queryString The "preprocessed" query string; at the very least
 	 * already processed by {@link org.hibernate.hql.internal.QuerySplitter}.
 	 * @param enabledFilters Any enabled filters.
 	 * @param factory The session factory.
 	 */
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 	        String queryString,
 	        Map enabledFilters,
 	        SessionFactoryImplementor factory) {
 		super( factory );
 		this.queryIdentifier = queryIdentifier;
 		this.queryString = queryString;
 		this.enabledFilters = enabledFilters;
 	}
 
 	/**
 	 * Construct a query translator; this form used internally.
 	 *
 	 * @param queryString The query string to process.
 	 * @param enabledFilters Any enabled filters.
 	 * @param factory The session factory.
 	 */
 	public QueryTranslatorImpl(
 	        String queryString,
 	        Map enabledFilters,
 	        SessionFactoryImplementor factory) {
 		this( queryString, queryString, enabledFilters, factory );
 	}
 
 	/**
 	 * Compile a subquery.
 	 *
 	 * @param superquery The containing query of the query to be compiled.
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems resolving
 	 * things referenced in the query.
 	 * @throws org.hibernate.QueryException Generally some form of syntatic
 	 * failure.
 	 */
 	void compile(QueryTranslatorImpl superquery) throws QueryException, MappingException {
 		this.tokenReplacements = superquery.tokenReplacements;
 		this.superQuery = superquery;
 		this.shallowQuery = true;
 		this.enabledFilters = superquery.getEnabledFilters();
 		compile();
 	}
 
 
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
 		LOG.trace( "Compiling query" );
 		try {
 			ParserHelper.parse( new PreprocessingParser( tokenReplacements ),
 					queryString,
 					ParserHelper.HQL_SEPARATORS,
 					this );
 			renderSQL();
 		}
 		catch ( QueryException qe ) {
 			if ( qe.getQueryString() == null ) {
 				throw qe.wrapWithQueryString( queryString );
 			}
 			else {
 				throw qe;
 			}
 		}
 		catch ( MappingException me ) {
 			throw me;
 		}
 		catch ( Exception e ) {
 			LOG.debug( "Unexpected query compilation problem", e );
 			e.printStackTrace();
 			throw new QueryException( "Incorrect query syntax", queryString, e );
 		}
 
 		postInstantiate();
 
 		compiled = true;
 
 	}
 
 	@Override
     public String getSQLString() {
 		return sqlString;
 	}
 
 	public List<String> collectSqlStrings() {
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
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "HQL: %s", hql );
 			LOG.debugf( "SQL: %s", sql );
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
     public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			throw new QueryException( ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name, queryString );
 		}
 		if ( o instanceof Integer ) return new int[] { (Integer) o };
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
 
 		StringBuilder buf = new StringBuilder( 20 );
 
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
 
 	public final Set<Serializable> getQuerySpaces() {
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
 		Collections.addAll( querySpaces, spaces );
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
 						JoinType.INNER_JOIN,
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
 
 	@Override
 	public List list(SessionImplementor session, QueryParameters queryParameters)
 			throws HibernateException {
-		return list( session, queryParameters, getQuerySpaces() );
+		return list( session, queryParameters, getQuerySpaces(), actualReturnTypes );
 	}
 
 	/**
 	 * Return the query results as an iterator
 	 */
 	@Override
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 
 		boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 			final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
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
     protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		final LockOptions lockOptions = parameters.getLockOptions();
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
 		HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(
 				holderConstructor, queryParameters.getResultTransformer()
 		);
-		return scroll( queryParameters, hi, session );
+		return scroll( queryParameters, returnTypes, hi, session );
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
 
 	@Override
 	public Class getDynamicInstantiationResultType() {
 		return holderClass;
 	}
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 1392aca864..2ac3bfc7bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1085,1637 +1085,1634 @@ public abstract class Loader {
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 		//important: reuse the same event instances for performance!
 		final PreLoadEvent pre;
 		final PostLoadEvent post;
 		if ( session.isEventSource() ) {
 			pre = new PreLoadEvent( (EventSource) session );
 			post = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			pre = null;
 			post = null;
 		}
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 		
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedObjects != null ) {
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.postLoad( hydratedObject, session, post );
 				if ( afterLoadActions != null ) {
 					for ( AfterLoadAction afterLoadAction : afterLoadActions ) {
 						final EntityEntry entityEntry = session.getPersistenceContext().getEntry( hydratedObject );
 						if ( entityEntry == null ) {
 							// big problem
 							throw new HibernateException( "Could not locate EntityEntry immediately after two-phase load" );
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
 				}
 			}
 		}
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SessionImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		 return null;
 	}
 
 	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
 	private void registerNonExists(
 			final EntityKey[] keys,
 			final Loadable[] persisters,
 			final SessionImplementor session) {
 
 		final int[] owners = getOwners();
 		if ( owners != null ) {
 
 			EntityType[] ownerAssociationTypes = getOwnerAssociationTypes();
 			for ( int i = 0; i < keys.length; i++ ) {
 
 				int owner = owners[i];
 				if ( owner > -1 ) {
 					EntityKey ownerKey = keys[owner];
 					if ( keys[i] == null && ownerKey != null ) {
 
 						final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 						/*final boolean isPrimaryKey;
 						final boolean isSpecialOneToOne;
 						if ( ownerAssociationTypes == null || ownerAssociationTypes[i] == null ) {
 							isPrimaryKey = true;
 							isSpecialOneToOne = false;
 						}
 						else {
 							isPrimaryKey = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()==null;
 							isSpecialOneToOne = ownerAssociationTypes[i].getLHSPropertyName()!=null;
 						}*/
 
 						//TODO: can we *always* use the "null property" approach for everything?
 						/*if ( isPrimaryKey && !isSpecialOneToOne ) {
 							persistenceContext.addNonExistantEntityKey(
 									new EntityKey( ownerKey.getIdentifier(), persisters[i], session.getEntityMode() )
 							);
 						}
 						else if ( isSpecialOneToOne ) {*/
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
 						}
 						/*}
 						else {
 							persistenceContext.addNonExistantEntityUniqueKey( new EntityUniqueKey(
 									persisters[i].getEntityName(),
 									ownerAssociationTypes[i].getRHSUniqueKeyPropertyName(),
 									ownerKey.getIdentifier(),
 									persisters[owner].getIdentifierType(),
 									session.getEntityMode()
 							) );
 						}*/
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read one collection element from the current row of the JDBC result set
 	 */
 	private void readCollectionElement(
 			final Object optionalOwner,
 			final Serializable optionalKey,
 			final CollectionPersister persister,
 			final CollectionAliases descriptor,
 			final ResultSet rs,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 
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
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) );
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
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) );
 			}
 
 			persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 		}
 
 		// else no collection element, but also no owner
 
 	}
 
 	/**
 	 * If this is a collection initializer, we need to tell the session that a collection
 	 * is being initialized, to account for the possibility of the collection having
 	 * no elements (hence no rows in the result set).
 	 */
 	private void handleEmptyCollections(
 			final Serializable[] keys,
 			final Object resultSetId,
 			final SessionImplementor session) {
 
 		if ( keys != null ) {
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
 					if ( debugEnabled ) {
 						LOG.debugf( "Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) );
 					}
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( ( ResultSet ) resultSetId )
 							.getLoadingCollection( collectionPersisters[j], keys[i] );
 				}
 			}
 		}
 
 		// else this is not a collection initializer (and empty collections will
 		// be detected by looking for the owner's identifier in the result set)
 	}
 
 	/**
 	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array.
 	 * Warning: this method is side-effecty.
 	 * <p/>
 	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
 	 */
 	private EntityKey getKeyFromResultSet(
 			final int i,
 			final Loadable persister,
 			final Serializable id,
 			final ResultSet rs,
 			final SessionImplementor session) throws HibernateException, SQLException {
 
 		Serializable resultId;
 
 		// if we know there is exactly 1 row, we can skip.
 		// it would be great if we could _always_ skip this;
 		// it is a problem for <key-many-to-one>
 
 		if ( isSingleRowLoader() && id != null ) {
 			resultId = id;
 		}
 		else {
 
 			Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 				);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ? null : session.generateEntityKey( resultId, persister );
 	}
 
 	/**
 	 * Check the version of the object in the <tt>ResultSet</tt> against
 	 * the object version in the session cache, throwing an exception
 	 * if the version numbers are different
 	 */
 	private void checkVersion(
 			final int i,
 			final Loadable persister,
 			final Serializable id,
 			final Object entity,
 			final ResultSet rs,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 				);
 			if ( !versionType.isEqual(version, currentVersion) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 		}
 
 	}
 
 	/**
 	 * Resolve any IDs for currently loaded objects, duplications within the
 	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the
 	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an
 	 * array of booleans (by side-effect) that determine whether the corresponding
 	 * object should be initialized.
 	 */
 	private Object[] getRow(
 			final ResultSet rs,
 			final Loadable[] persisters,
 			final EntityKey[] keys,
 			final Object optionalObject,
 			final EntityKey optionalObjectKey,
 			final LockMode[] lockModes,
 			final List hydratedObjects,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 
 		final Object[] rowResults = new Object[cols];
 
 		for ( int i = 0; i < cols; i++ ) {
 
 			Object object = null;
 			EntityKey key = keys[i];
 
 			if ( keys[i] == null ) {
 				//do nothing
 			}
 			else {
 
 				//If the object is already loaded, return the loaded one
 				object = session.getEntityUsingInterceptor( key );
 				if ( object != null ) {
 					//its already loaded so don't need to hydrate it
 					instanceAlreadyLoaded(
 							rs,
 							i,
 							persisters[i],
 							key,
 							object,
 							lockModes[i],
 							session
 						);
 				}
 				else {
 					object = instanceNotYetLoaded(
 							rs,
 							i,
 							persisters[i],
 							descriptors[i].getRowIdAlias(),
 							key,
 							lockModes[i],
 							optionalObjectKey,
 							optionalObject,
 							hydratedObjects,
 							session
 						);
 				}
 
 			}
 
 			rowResults[i] = object;
 
 		}
 
 		return rowResults;
 	}
 
 	/**
 	 * The entity instance is already in the session cache
 	 */
 	private void instanceAlreadyLoaded(
 			final ResultSet rs,
 			final int i,
 			final Loadable persister,
 			final EntityKey key,
 			final Object object,
 			final LockMode requestedLockMode,
 			final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 			);
 		}
 
 		if ( LockMode.NONE != requestedLockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 			final EntityEntry entry = session.getPersistenceContext().getEntry( object );
 			if ( entry.getLockMode().lessThan( requestedLockMode ) ) {
 				//we only check the version when _upgrading_ lock modes
 				if ( persister.isVersioned() ) {
 					checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				}
 				//we need to upgrade the lock mode to the mode requested
 				entry.setLockMode( requestedLockMode );
 			}
 		}
 	}
 
 
 	/**
 	 * The entity instance is not in the session cache
 	 */
 	private Object instanceNotYetLoaded(
 			final ResultSet rs,
 			final int i,
 			final Loadable persister,
 			final String rowIdAlias,
 			final EntityKey key,
 			final LockMode lockMode,
 			final EntityKey optionalObjectKey,
 			final Object optionalObject,
 			final List hydratedObjects,
 			final SessionImplementor session)
 	throws HibernateException, SQLException {
 		final String instanceClass = getInstanceClass(
 				rs,
 				i,
 				persister,
 				key.getIdentifier(),
 				session
 		);
 
 		// see if the entity defines reference caching, and if so use the cached reference (if one).
 		if ( persister.canUseReferenceCacheEntries() ) {
 			final Object cachedEntry = CacheHelper.fromSharedCache(
 					session,
 					session.generateCacheKey(
 							key.getIdentifier(),
 							persister.getEntityMetamodel().getEntityType(),
 							key.getEntityName()
 					),
 					persister.getCacheAccessStrategy()
 			);
 			if ( cachedEntry != null ) {
 				CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( cachedEntry, factory );
 				return ( (ReferenceCacheEntryImpl) entry ).getReference();
 			}
 		}
 
 		final Object object;
 		if ( optionalObjectKey != null && key.equals( optionalObjectKey ) ) {
 			//its the given optional object
 			object = optionalObject;
 		}
 		else {
 			// instantiate a new instance
 			object = session.instantiate( instanceClass, key.getIdentifier() );
 		}
 
 		//need to hydrate it.
 
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		LockMode acquiredLockMode = lockMode == LockMode.NONE ? LockMode.READ : lockMode;
 		loadFromResultSet(
 				rs,
 				i,
 				object,
 				instanceClass,
 				key,
 				rowIdAlias,
 				acquiredLockMode,
 				persister,
 				session
 			);
 
 		//materialize associations (and initialize the object) later
 		hydratedObjects.add( object );
 
 		return object;
 	}
 
 	private boolean isEagerPropertyFetchEnabled(int i) {
 		boolean[] array = getEntityEagerPropertyFetches();
 		return array!=null && array[i];
 	}
 
 
 	/**
 	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
 	 * an array or "hydrated" values (do not resolve associations yet),
 	 * and pass the hydrates state to the session.
 	 */
 	private void loadFromResultSet(
 			final ResultSet rs,
 			final int i,
 			final Object object,
 			final String instanceEntityName,
 			final EntityKey key,
 			final String rowIdAlias,
 			final LockMode lockMode,
 			final Loadable rootPersister,
 			final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
 		if ( LOG.isTraceEnabled() )
 			LOG.tracev( "Initializing object from ResultSet: {0}", MessageHelper.infoString( persister, id, getFactory() ) );
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled(i);
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 			);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases(persister);
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 			);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if (ukName!=null) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex(ukName);
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, object ),
 						type,
 						persister.getEntityMode(),
 						session.getFactory()
 				);
 				session.getPersistenceContext().addEntity( euk, object );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				object,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 		);
 
 	}
 
 	/**
 	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
 	 */
 	private String getInstanceClass(
 	        final ResultSet rs,
 	        final int i,
 	        final Loadable persister,
 	        final Serializable id,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedDiscriminatorAlias(),
 					session,
 					null
 				);
 
 			final String result = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 
 			if ( result == null ) {
 				//woops we got an instance of another class hierarchy branch
 				throw new WrongClassException(
 						"Discriminator: " + discriminatorValue,
 						id,
 						persister.getEntityName()
 					);
 			}
 
 			return result;
 
 		}
 		else {
 			return persister.getEntityName();
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	private void advance(final ResultSet rs, final RowSelection selection)
 			throws SQLException {
 
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) rs.next();
 			}
 		}
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
 	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
 	}
 
 	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Process query string by applying filters, LIMIT clause, locks and comments if necessary.
 	 * Finally execute SQL statement and advance to the first row.
 	 */
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
 				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.getProcessedSql();
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper( st, getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session ) );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        String sql,
 	        final QueryParameters queryParameters,
 	        final LimitHandler limitHandler,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 		
 		PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
 
 			limitHandler.setMaxRows( st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( LOG.isDebugEnabled() ) {
 							LOG.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Bound [{0}] parameters total", col );
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SessionImplementor session) throws SQLException {
 		int span = 0;
 		span += bindPositionalParameters( statement, queryParameters, startIndex, session );
 		span += bindNamedParameters( statement, queryParameters.getNamedParameters(), startIndex + span, session );
 		return span;
 	}
 
 	/**
 	 * Bind positional parameter values to the JDBC prepared statement.
 	 * <p/>
 	 * Positional parameters are those specified by JDBC-style ? parameters
 	 * in the source query.  It is (currently) expected that these come
 	 * before any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( statement, values[i], startIndex + span, session );
 			span += types[i].getColumnSpan( getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Bind named parameters to the JDBC prepared statement.
 	 * <p/>
 	 * This is a generic implementation, the problem being that in the
 	 * general case we do not know enough information about the named
 	 * parameters to perform this in a complete manner here.  Thus this
 	 * is generally overridden on subclasses allowing named parameters to
 	 * apply the specific behavior.  The most usual limitation here is that
 	 * we need to assume the type span is always one...
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param namedParams A map of parameter names to values
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map<String, TypedValue> namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		int result = 0;
 		if ( CollectionHelper.isEmpty( namedParams ) ) {
 			return result;
 		}
 
 		for ( String name : namedParams.keySet() ) {
 			TypedValue typedValue = namedParams.get( name );
 			int columnSpan = typedValue.getType().getColumnSpan( getFactory() );
 			int[] locs = getNamedParameterLocs( name );
 			for ( int loc : locs ) {
 				if ( DEBUG_ENABLED ) {
 					LOG.debugf(
 							"bindNamedParameters() %s -> %s [%s]",
 							typedValue.getValue(),
 							name,
 							loc + startIndex
 					);
 				}
 				int start = loc * columnSpan + startIndex;
 				typedValue.getType().nullSafeSet( statement, typedValue.getValue(), start, session );
 			}
 			result += locs.length;
 		}
 		return result;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 			final PreparedStatement st,
 			final RowSelection selection,
 			final LimitHandler limitHandler,
 			final boolean autodiscovertypes,
 			final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		try {
 			ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
-	}
-	
-	/**
-	 * Return types must be retrieved on-demand, rather than, as an example, provided as an argument on the original
-	 * #list call.  They can be overriden later by {@link #autoDiscoverTypes(ResultSet)}, etc.
-	 * 
-	 * @return Type[]
-	 */
-	protected Type[] getReturnTypes() {
-		return new Type[0];
+
 	}
 
 	private ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 				LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(final ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			synchronized ( this ) {
 				if ( columnNameCache == null ) {
 					LOG.trace( "Building columnName -> columnIndex cache" );
 					columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 				}
 			}
 		}
 		return columnNameCache;
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	protected final List loadEntity(
 			final SessionImplementor session,
 			final Object id,
 			final Type identifierType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalIdentifier,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Loading entity: %s", MessageHelper.infoString( persister, id, identifierType, getFactory() ) );
 		}
 
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] { identifierType } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity: " +
 			        MessageHelper.infoString( persisters[persisters.length-1], id, identifierType, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 	        final SessionImplementor session,
 	        final Object key,
 	        final Object index,
 	        final Type keyType,
 	        final Type indexType,
 	        final EntityPersister persister) throws HibernateException {
 
 		LOG.debug( "Loading collection element by index" );
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] { keyType, indexType },
 							new Object[] { key, index }
 					),
 					false
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load collection element by index",
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by wrappers that batch load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
 	 */
 	public final List loadEntityBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type idType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 
 		Type[] types = new Type[ids.length];
 		Arrays.fill( types, idType );
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( types );
 			qp.setPositionalParameterValues( ids );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalId );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity batch: " +
 			        MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ) );
 
 		Serializable[] ids = new Serializable[]{id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[]{type}, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 				);
 		}
 
 		LOG.debug( "Done loading collection" );
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ) );
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection batch: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 					getSQLString()
 				);
 		}
 
 		LOG.debug( "Done batch load" );
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Object[] parameterValues,
 			final Type[] parameterTypes,
 			final Map<String, TypedValue> namedParameters,
 			final Type type) throws HibernateException {
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections( session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load collection by subselect: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 					getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Return the query results, using the query cache, called
 	 * by subclasses that implement cacheable queries
 	 */
 	protected List list(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
-			final Set<Serializable> querySpaces) throws HibernateException {
+			final Set<Serializable> querySpaces,
+			final Type[] resultTypes) throws HibernateException {
 
 		final boolean cacheable = factory.getSettings().isQueryCacheEnabled() &&
 			queryParameters.isCacheable();
 
 		if ( cacheable ) {
-			return listUsingQueryCache( session, queryParameters, querySpaces );
+			return listUsingQueryCache( session, queryParameters, querySpaces, resultTypes );
 		}
 		else {
 			return listIgnoreQueryCache( session, queryParameters );
 		}
 	}
 
 	private List listIgnoreQueryCache(SessionImplementor session, QueryParameters queryParameters) {
 		return getResultList( doList( session, queryParameters ), queryParameters.getResultTransformer() );
 	}
 
 	private List listUsingQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
-			final Set<Serializable> querySpaces) {
+			final Set<Serializable> querySpaces,
+			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
 		if ( querySpaces == null || querySpaces.size() == 0 )
 			LOG.tracev( "Unexpected querySpaces is {0}", ( querySpaces == null ? querySpaces : "empty" ) );
 		else {
 			LOG.tracev( "querySpaces is {0}", querySpaces );
 		}
 
 		List result = getResultFromQueryCache(
 				session,
 				queryParameters,
 				querySpaces,
+				resultTypes,
 				queryCache,
 				key
 			);
 
 		if ( result == null ) {
 			result = doList( session, queryParameters, key.getResultTransformer() );
 
 			putResultInQueryCache(
 					session,
 					queryParameters,
+					resultTypes,
 					queryCache,
 					key,
 					result
 			);
 		}
 
 		ResultTransformer resolvedTransformer = resolveResultTransformer( queryParameters.getResultTransformer() );
 		if ( resolvedTransformer != null ) {
 			result = (
 					areResultSetRowsTransformedImmediately() ?
 							key.getResultTransformer().retransformResults(
 									result,
 									getResultRowAliases(),
 									queryParameters.getResultTransformer(),
 									includeInResultRow()
 							) :
 							key.getResultTransformer().untransformToTuples(
 									result
 							)
 			);
 		}
 
 		return getResultList( result, queryParameters.getResultTransformer() );
 	}
 
 	private QueryKey generateQueryKey(
 			SessionImplementor session,
 			QueryParameters queryParameters) {
 		return QueryKey.generateQueryKey(
 				getSQLString(),
 				queryParameters,
 				FilterKey.createFilterKeys( session.getLoadQueryInfluencers().getEnabledFilters() ),
 				session,
 				createCacheableResultTransformer( queryParameters )
 		);
 	}
 
 	private CacheableResultTransformer createCacheableResultTransformer(QueryParameters queryParameters) {
 		return CacheableResultTransformer.create(
 				queryParameters.getResultTransformer(),
 				getResultRowAliases(),
 				includeInResultRow()
 		);
 	}
 
 	private List getResultFromQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
+			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key) {
 		List result = null;
-		final Type[] returnTypes = getReturnTypes();
-		
+
 		if ( session.getCacheMode().isGetEnabled() ) {
 			boolean isImmutableNaturalKeyLookup =
 					queryParameters.isNaturalKeyLookup() &&
-							returnTypes.length == 1 &&
-							returnTypes[0].isEntityType() &&
-							getEntityPersister( EntityType.class.cast( returnTypes[0] ) )
+							resultTypes.length == 1 &&
+							resultTypes[0].isEntityType() &&
+							getEntityPersister( EntityType.class.cast( resultTypes[0] ) )
 									.getEntityMetamodel()
 									.hasImmutableNaturalId();
 
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
 			if ( queryParameters.isReadOnlyInitialized() ) {
 				// The read-only/modifiable mode for the query was explicitly set.
 				// Temporarily set the default read-only/modifiable setting to the query's setting.
 				persistenceContext.setDefaultReadOnly( queryParameters.isReadOnly() );
 			}
 			else {
 				// The read-only/modifiable setting for the query was not initialized.
 				// Use the default read-only/modifiable from the persistence context instead.
 				queryParameters.setReadOnly( persistenceContext.isDefaultReadOnly() );
 			}
 			try {
 				result = queryCache.get(
 						key,
-						key.getResultTransformer().getCachedResultTypes( returnTypes ),
+						key.getResultTransformer().getCachedResultTypes( resultTypes ),
 						isImmutableNaturalKeyLookup,
 						querySpaces,
 						session
 				);
 			}
 			finally {
 				persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 			}
 
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				if ( result == null ) {
 					factory.getStatisticsImplementor()
 							.queryCacheMiss( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 				else {
 					factory.getStatisticsImplementor()
 							.queryCacheHit( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private EntityPersister getEntityPersister(EntityType entityType) {
 		return factory.getEntityPersister( entityType.getAssociatedEntityName() );
 	}
 
-	private void putResultInQueryCache(
+	protected void putResultInQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
+			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		if ( session.getCacheMode().isPutEnabled() ) {
 			boolean put = queryCache.put(
 					key,
-					key.getResultTransformer().getCachedResultTypes( getReturnTypes() ),
+					key.getResultTransformer().getCachedResultTypes( resultTypes ),
 					result,
 					queryParameters.isNaturalKeyLookup(),
 					session
 			);
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor()
 						.queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
 
 	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
 		return doList( session, queryParameters, null);
 	}
 
 	private List doList(final SessionImplementor session,
 						final QueryParameters queryParameters,
 						final ResultTransformer forcedResultTransformer)
 			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query",
 					getSQLString()
 				);
 		}
 
 		if ( stats ) {
 			getFactory().getStatisticsImplementor().queryExecuted(
 					getQueryIdentifier(),
 					result.size(),
 					System.currentTimeMillis() - startTime
 				);
 		}
 
 		return result;
 	}
 
 	/**
 	 * Check whether the current loader can support returning ScrollableResults.
 	 *
 	 * @throws HibernateException
 	 */
 	protected void checkScrollability() throws HibernateException {
 		// Allows various loaders (ok mainly the QueryLoader :) to check
 		// whether scrolling of their result set should be allowed.
 		//
 		// By default it is allowed.
 	}
 
 	/**
 	 * Does the result set to be scrolled contain collection fetches?
 	 *
 	 * @return True if it does, and thus needs the special fetching scroll
 	 * functionality; false otherwise.
 	 */
 	protected boolean needsFetchingScroll() {
 		return false;
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 *
 	 * @param queryParameters The parameters with which the query should be executed.
 	 * @param returnTypes The expected return types of the query
 	 * @param holderInstantiator If the return values are expected to be wrapped
 	 * in a holder, this is the thing that knows how to wrap them.
 	 * @param session The session from which the scroll request originated.
 	 * @return The ScrollableResults instance.
 	 * @throws HibernateException Indicates an error executing the query, or constructing
 	 * the ScrollableResults.
 	 */
 	protected ScrollableResults scroll(
 			final QueryParameters queryParameters,
+			final Type[] returnTypes,
 			final HolderInstantiator holderInstantiator,
 			final SessionImplementor session) throws HibernateException {
 
 		checkScrollability();
 
 		final boolean stats = getQueryIdentifier() != null &&
 				getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 			// Don't use Collections#emptyList() here -- follow on locking potentially adds AfterLoadActions,
 			// so the list cannot be immutable.
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, true, new ArrayList<AfterLoadAction>(), session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 
 			if ( stats ) {
 				getFactory().getStatisticsImplementor().queryExecuted(
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
 					);
 			}
 
 			if ( needsFetchingScroll() ) {
 				return new FetchingScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
-						getReturnTypes(),
+						returnTypes,
 						holderInstantiator
 					);
 			}
 			else {
 				return new ScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
-						getReturnTypes(),
+						returnTypes,
 						holderInstantiator
 					);
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query using scroll",
 					getSQLString()
 				);
 		}
 
 	}
 
 	/**
 	 * Calculate and cache select-clause suffixes. Must be
 	 * called by subclasses after instantiation.
 	 */
 	protected void postInstantiate() {}
 
 	/**
 	 * Get the result set descriptor
 	 */
 	protected abstract EntityAliases[] getEntityAliases();
 
 	protected abstract CollectionAliases[] getCollectionAliases();
 
 	/**
 	 * Identifies the query for statistics reporting, if null,
 	 * no statistics will be reported
 	 */
 	protected String getQueryIdentifier() {
 		return null;
 	}
 
 	public final SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getSQLString() + ')';
 	}
 
 	/**
 	 * Wrapper class for {@link Statement} and associated {@link ResultSet}.
 	 */
 	protected static class SqlStatementWrapper {
 		private final Statement statement;
 		private final ResultSet resultSet;
 
 		private SqlStatementWrapper(Statement statement, ResultSet resultSet) {
 			this.resultSet = resultSet;
 			this.statement = statement;
 		}
 
 		public ResultSet getResultSet() {
 			return resultSet;
 		}
 
 		public Statement getStatement() {
 			return statement;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index 6d30695d8e..8bb73cc05a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,295 +1,291 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
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
  *
  */
 package org.hibernate.loader.criteria;
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.OuterJoinLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A <tt>Loader</tt> for <tt>Criteria</tt> queries. Note that criteria queries are
  * more like multi-object <tt>load()</tt>s than like HQL queries.
  *
  * @author Gavin King
  */
 public class CriteriaLoader extends OuterJoinLoader {
 
 	//TODO: this class depends directly upon CriteriaImpl, 
 	//      in the impl package ... add a CriteriaImplementor 
 	//      interface
 
 	//NOTE: unlike all other Loaders, this one is NOT
 	//      multithreaded, or cacheable!!
 
 	private final CriteriaQueryTranslator translator;
 	private final Set<Serializable> querySpaces;
 	private final Type[] resultTypes;
 	//the user visible aliases, which are unknown to the superclass,
 	//these are not the actual "physical" SQL aliases
 	private final String[] userAliases;
 	private final boolean[] includeInResultRow;
 	private final int resultRowLength;
 
 	public CriteriaLoader(
 			final OuterJoinLoadable persister, 
 			final SessionFactoryImplementor factory, 
 			final CriteriaImpl criteria, 
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers) throws HibernateException {
 		super( factory, loadQueryInfluencers );
 
 		translator = new CriteriaQueryTranslator(
 				factory, 
 				criteria, 
 				rootEntityName, 
 				CriteriaQueryTranslator.ROOT_SQL_ALIAS
 			);
 
 		querySpaces = translator.getQuerySpaces();
 		
 		CriteriaJoinWalker walker = new CriteriaJoinWalker(
 				persister, 
 				translator,
 				factory, 
 				criteria, 
 				rootEntityName, 
 				loadQueryInfluencers
 			);
 
 		initFromWalker(walker);
 		
 		userAliases = walker.getUserAliases();
 		resultTypes = walker.getResultTypes();
 		includeInResultRow = walker.includeInResultRow();
 		resultRowLength = ArrayHelper.countTrue( includeInResultRow );
 
 		postInstantiate();
 
 	}
 	
-	protected Type[] getReturnTypes() {
-		return resultTypes;
-	}
-	
 	public ScrollableResults scroll(SessionImplementor session, ScrollMode scrollMode) 
 	throws HibernateException {
 		QueryParameters qp = translator.getQueryParameters();
 		qp.setScrollMode(scrollMode);
-		return scroll(qp, null, session);
+		return scroll(qp, resultTypes, null, session);
 	}
 
 	public List list(SessionImplementor session) 
 	throws HibernateException {
-		return list( session, translator.getQueryParameters(), querySpaces );
+		return list( session, translator.getQueryParameters(), querySpaces, resultTypes );
 
 	}
 	@Override
 	protected String[] getResultRowAliases() {
 		return userAliases;
 	}
 	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 	@Override
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return true;
 	}
 	@Override
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 	@Override
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 	throws SQLException, HibernateException {
 		return resolveResultTransformer( transformer ).transformTuple(
 				getResultRow( row, rs, session),
 				getResultRowAliases()
 		);
 	}
 	@Override
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		final Object[] result;
 		if ( translator.hasProjection() ) {
 			Type[] types = translator.getProjectedTypes();
 			result = new Object[types.length];
 			String[] columnAliases = translator.getProjectedColumnAliases();
 			for ( int i=0, pos=0; i<result.length; i++ ) {
 				int numColumns = types[i].getColumnSpan( session.getFactory() );
 				if ( numColumns > 1 ) {
 			    	String[] typeColumnAliases = ArrayHelper.slice( columnAliases, pos, numColumns );
 					result[i] = types[i].nullSafeGet(rs, typeColumnAliases, session, null);
 				}
 				else {
 					result[i] = types[i].nullSafeGet(rs, columnAliases[pos], session, null);
 				}
 				pos += numColumns;
 			}
 		}
 		else {
 			result = toResultRow( row );
 		}
 		return result;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( resultRowLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[ resultRowLength ];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInResultRow[i] ) result[j++] = row[i];
 			}
 			return result;
 		}
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		final LockOptions lockOptions = parameters.getLockOptions();
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 		if ( dialect.useFollowOnLocking() ) {
             final LockMode lockMode = determineFollowOnLockMode( lockOptions );
             if( lockMode != LockMode.UPGRADE_SKIPLOCKED ) {
 				// Dialect prefers to perform locking in a separate step
 				LOG.usingFollowOnLocking();
 
 				final LockOptions lockOptionsToUse = new LockOptions( lockMode );
 				lockOptionsToUse.setTimeOut( lockOptions.getTimeOut() );
 				lockOptionsToUse.setScope( lockOptions.getScope() );
 
 				afterLoadActions.add(
 						new AfterLoadAction() {
 								@Override
 								public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 									( (Session) session ).buildLockRequest( lockOptionsToUse )
 										.lock( persister.getEntityName(), entity );
 								}
 				        }
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return sql;
 			}
 		}
 		final LockOptions locks = new LockOptions(lockOptions.getLockMode());
 		locks.setScope( lockOptions.getScope());
 		locks.setTimeOut( lockOptions.getTimeOut());
 
 		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 		final String[] drivingSqlAliases = getAliases();
 		for ( int i = 0; i < drivingSqlAliases.length; i++ ) {
 			final LockMode lockMode = lockOptions.getAliasSpecificLockMode( drivingSqlAliases[i] );
 			if ( lockMode != null ) {
 				final Lockable drivingPersister = ( Lockable ) getEntityPersisters()[i];
 				final String rootSqlAlias = drivingPersister.getRootTableAlias( drivingSqlAliases[i] );
 				locks.setAliasSpecificLockMode( rootSqlAlias, lockMode );
 				if ( keyColumnNames != null ) {
 					keyColumnNames.put( rootSqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 				}
 			}
 		}
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
 
 	@Override
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.getAliasLockCount() > 1 ) {
 			// > 1 here because criteria always uses alias map for the root lock mode (under 'this_')
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 	@Override
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		final String[] entityAliases = getAliases();
 		if ( entityAliases == null ) {
 			return null;
 		}
 		final int size = entityAliases.length;
 		LockMode[] lockModesArray = new LockMode[size];
 		for ( int i=0; i<size; i++ ) {
 			LockMode lockMode = lockOptions.getAliasSpecificLockMode( entityAliases[i] );
 			lockModesArray[i] = lockMode==null ? lockOptions.getLockMode() : lockMode;
 		}
 		return lockModesArray;
 	}
 	@Override
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 	@Override
 	protected List getResultList(List results, ResultTransformer resultTransformer) {
 		return resolveResultTransformer( resultTransformer ).transformList( results );
 	}
 	@Override
 	protected String getQueryIdentifier() { 
 		return "[CRITERIA] " + getSQLString(); 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
index 1c2efd9724..8e85de6033 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
@@ -1,536 +1,557 @@
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
 package org.hibernate.loader.custom;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
+import org.hibernate.cache.spi.QueryCache;
+import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.Loader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Extension point for loaders which use a SQL result set with "unexpected" column aliases.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CustomLoader extends Loader {
 
 	// Currently *not* cachable if autodiscover types is in effect (e.g. "select * ...")
 
 	private final String sql;
 	private final Set<Serializable> querySpaces = new HashSet<Serializable>();
 	private final Map namedParameterBindPoints;
 
 	private final Queryable[] entityPersisters;
 	private final int[] entiytOwners;
 	private final EntityAliases[] entityAliases;
 
 	private final QueryableCollection[] collectionPersisters;
 	private final int[] collectionOwners;
 	private final CollectionAliases[] collectionAliases;
 
 	private final LockMode[] lockModes;
 
 	private boolean[] includeInResultRow;
 
 //	private final String[] sqlAliases;
 //	private final String[] sqlAliasSuffixes;
 	private final ResultRowProcessor rowProcessor;
 
 	// this is only needed (afaict) for processing results from the query cache;
 	// however, this cannot possibly work in the case of discovered types...
 	private Type[] resultTypes;
 
 	// this is only needed (afaict) for ResultTransformer processing...
 	private String[] transformerAliases;
 
 	public CustomLoader(CustomQuery customQuery, SessionFactoryImplementor factory) {
 		super( factory );
 
 		this.sql = customQuery.getSQL();
 		this.querySpaces.addAll( customQuery.getQuerySpaces() );
 		this.namedParameterBindPoints = customQuery.getNamedParameterBindPoints();
 
 		List<Queryable> entityPersisters = new ArrayList<Queryable>();
 		List<Integer> entityOwners = new ArrayList<Integer>();
 		List<EntityAliases> entityAliases = new ArrayList<EntityAliases>();
 
 		List<QueryableCollection> collectionPersisters = new ArrayList<QueryableCollection>();
 		List<Integer> collectionOwners = new ArrayList<Integer>();
 		List<CollectionAliases> collectionAliases = new ArrayList<CollectionAliases>();
 
 		List<LockMode> lockModes = new ArrayList<LockMode>();
 		List<ResultColumnProcessor> resultColumnProcessors = new ArrayList<ResultColumnProcessor>();
 		List<Return> nonScalarReturnList = new ArrayList<Return>();
 		List<Type> resultTypes = new ArrayList<Type>();
 		List<String> specifiedAliases = new ArrayList<String>();
 
 		int returnableCounter = 0;
 		boolean hasScalars = false;
 
 		List<Boolean> includeInResultRowList = new ArrayList<Boolean>();
 
 		for ( Return rtn : customQuery.getCustomQueryReturns() ) {
 			if ( rtn instanceof ScalarReturn ) {
 				ScalarReturn scalarRtn = (ScalarReturn) rtn;
 				resultTypes.add( scalarRtn.getType() );
 				specifiedAliases.add( scalarRtn.getColumnAlias() );
 				resultColumnProcessors.add(
 						new ScalarResultColumnProcessor(
 								StringHelper.unquote( scalarRtn.getColumnAlias(), factory.getDialect() ),
 								scalarRtn.getType()
 						)
 				);
 				includeInResultRowList.add( true );
 				hasScalars = true;
 			}
 			else if ( ConstructorReturn.class.isInstance( rtn ) ) {
 				final ConstructorReturn constructorReturn = (ConstructorReturn) rtn;
 				resultTypes.add( null ); // this bit makes me nervous
 				includeInResultRowList.add( true );
 				hasScalars = true;
 
 				ScalarResultColumnProcessor[] scalarProcessors = new ScalarResultColumnProcessor[constructorReturn.getScalars().length];
 				int i = 0;
 				for ( ScalarReturn scalarReturn : constructorReturn.getScalars() ) {
 					scalarProcessors[i++] = new ScalarResultColumnProcessor(
 							StringHelper.unquote( scalarReturn.getColumnAlias(), factory.getDialect() ),
 							scalarReturn.getType()
 					);
 				}
 
 				resultColumnProcessors.add(
 						new ConstructorResultColumnProcessor( constructorReturn.getTargetClass(), scalarProcessors )
 				);
 			}
 			else if ( rtn instanceof RootReturn ) {
 				RootReturn rootRtn = (RootReturn) rtn;
 				Queryable persister = (Queryable) factory.getEntityPersister( rootRtn.getEntityName() );
 				entityPersisters.add( persister );
 				lockModes.add( (rootRtn.getLockMode()) );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				entityOwners.add( -1 );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( rootRtn.getAlias() );
 				entityAliases.add( rootRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof CollectionReturn ) {
 				CollectionReturn collRtn = (CollectionReturn) rtn;
 				String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 				QueryableCollection persister = (QueryableCollection) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				lockModes.add( collRtn.getLockMode() );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				collectionOwners.add( -1 );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( collRtn.getAlias() );
 				collectionAliases.add( collRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = (Queryable) ((EntityType) elementType).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( -1 );
 					entityAliases.add( collRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
 				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof EntityFetchReturn ) {
 				EntityFetchReturn fetchRtn = (EntityFetchReturn) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				entityOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				EntityType fetchedType = (EntityType) ownerPersister.getPropertyType( fetchRtn.getOwnerProperty() );
 				String entityName = fetchedType.getAssociatedEntityName( getFactory() );
 				Queryable persister = (Queryable) factory.getEntityPersister( entityName );
 				entityPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				entityAliases.add( fetchRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 				includeInResultRowList.add( false );
 			}
 			else if ( rtn instanceof CollectionFetchReturn ) {
 				CollectionFetchReturn fetchRtn = (CollectionFetchReturn) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				collectionOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				String role = ownerPersister.getEntityName() + '.' + fetchRtn.getOwnerProperty();
 				QueryableCollection persister = (QueryableCollection) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				collectionAliases.add( fetchRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = (Queryable) ((EntityType) elementType).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( ownerIndex );
 					entityAliases.add( fetchRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
 				includeInResultRowList.add( false );
 			}
 			else {
 				throw new HibernateException( "unexpected custom query return type : " + rtn.getClass().getName() );
 			}
 		}
 
 		this.entityPersisters = new Queryable[ entityPersisters.size() ];
 		for ( int i = 0; i < entityPersisters.size(); i++ ) {
 			this.entityPersisters[i] = entityPersisters.get( i );
 		}
 		this.entiytOwners = ArrayHelper.toIntArray( entityOwners );
 		this.entityAliases = new EntityAliases[ entityAliases.size() ];
 		for ( int i = 0; i < entityAliases.size(); i++ ) {
 			this.entityAliases[i] = entityAliases.get( i );
 		}
 
 		this.collectionPersisters = new QueryableCollection[ collectionPersisters.size() ];
 		for ( int i = 0; i < collectionPersisters.size(); i++ ) {
 			this.collectionPersisters[i] = collectionPersisters.get( i );
 		}
 		this.collectionOwners = ArrayHelper.toIntArray( collectionOwners );
 		this.collectionAliases = new CollectionAliases[ collectionAliases.size() ];
 		for ( int i = 0; i < collectionAliases.size(); i++ ) {
 			this.collectionAliases[i] = collectionAliases.get( i );
 		}
 
 		this.lockModes = new LockMode[ lockModes.size() ];
 		for ( int i = 0; i < lockModes.size(); i++ ) {
 			this.lockModes[i] = lockModes.get( i );
 		}
 
 		this.resultTypes = ArrayHelper.toTypeArray( resultTypes );
 		this.transformerAliases = ArrayHelper.toStringArray( specifiedAliases );
 
 		this.rowProcessor = new ResultRowProcessor(
 				hasScalars,
 				resultColumnProcessors.toArray( new ResultColumnProcessor[ resultColumnProcessors.size() ] )
 		);
 
 		this.includeInResultRow = ArrayHelper.toBooleanArray( includeInResultRowList );
 	}
 
 	private Queryable determineAppropriateOwnerPersister(NonScalarReturn ownerDescriptor) {
 		String entityName = null;
 		if ( ownerDescriptor instanceof RootReturn ) {
 			entityName = ( ( RootReturn ) ownerDescriptor ).getEntityName();
 		}
 		else if ( ownerDescriptor instanceof CollectionReturn ) {
 			CollectionReturn collRtn = ( CollectionReturn ) ownerDescriptor;
 			String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 			CollectionPersister persister = getFactory().getCollectionPersister( role );
 			EntityType ownerType = ( EntityType ) persister.getElementType();
 			entityName = ownerType.getAssociatedEntityName( getFactory() );
 		}
 		else if ( ownerDescriptor instanceof FetchReturn ) {
 			FetchReturn fetchRtn = ( FetchReturn ) ownerDescriptor;
 			Queryable persister = determineAppropriateOwnerPersister( fetchRtn.getOwner() );
 			Type ownerType = persister.getPropertyType( fetchRtn.getOwnerProperty() );
 			if ( ownerType.isEntityType() ) {
 				entityName = ( ( EntityType ) ownerType ).getAssociatedEntityName( getFactory() );
 			}
 			else if ( ownerType.isCollectionType() ) {
 				Type ownerCollectionElementType = ( ( CollectionType ) ownerType ).getElementType( getFactory() );
 				if ( ownerCollectionElementType.isEntityType() ) {
 					entityName = ( ( EntityType ) ownerCollectionElementType ).getAssociatedEntityName( getFactory() );
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			throw new HibernateException( "Could not determine fetch owner : " + ownerDescriptor );
 		}
 
 		return ( Queryable ) getFactory().getEntityPersister( entityName );
 	}
 
 	@Override
     protected String getQueryIdentifier() {
 		return sql;
 	}
 
 	@Override
     public String getSQLString() {
 		return sql;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
     protected LockMode[] getLockModes(LockOptions lockOptions) {
 		return lockModes;
 	}
 
 	@Override
     protected Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	@Override
     protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
     protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 
 	@Override
     protected int[] getOwners() {
 		return entiytOwners;
 	}
-	
-	protected Type[] getReturnTypes() {
-		return resultTypes;
-	}
 
 	public List list(SessionImplementor session, QueryParameters queryParameters) throws HibernateException {
-		return list( session, queryParameters, querySpaces );
+		return list( session, queryParameters, querySpaces, resultTypes );
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		final LockOptions lockOptions = parameters.getLockOptions();
 		if ( lockOptions == null ||
 				( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 		// user is request locking, lets see if we can apply locking directly to the SQL...
 
 		// 		some dialects wont allow locking with paging...
 		afterLoadActions.add(
 				new AfterLoadAction() {
 					private final LockOptions originalLockOptions = lockOptions.makeCopy();
 					@Override
 					public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 						( (Session) session ).buildLockRequest( originalLockOptions ).lock( persister.getEntityName(), entity );
 					}
 				}
 		);
 		parameters.getLockOptions().setLockMode( LockMode.READ );
 
 		return sql;
 	}
 
 	public ScrollableResults scroll(final QueryParameters queryParameters, final SessionImplementor session)
 			throws HibernateException {
 		return scroll(
 				queryParameters,
+				resultTypes,
 				getHolderInstantiator( queryParameters.getResultTransformer(), getReturnAliasesForTransformer() ),
 				session
 		);
 	}
 
 	static private HolderInstantiator getHolderInstantiator(ResultTransformer resultTransformer, String[] queryReturnAliases) {
 		if ( resultTransformer == null ) {
 			return HolderInstantiator.NOOP_INSTANTIATOR;
 		}
 		else {
 			return new HolderInstantiator(resultTransformer, queryReturnAliases);
 		}
 	}
 
 	@Override
     protected String[] getResultRowAliases() {
 		return transformerAliases;
 	}
 
 	@Override
     protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveResultTransformer( null, resultTransformer );
 	}
 
 	@Override
     protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	@Override
     protected Object getResultColumnOrRow(
 			Object[] row,
 	        ResultTransformer transformer,
 	        ResultSet rs,
 	        SessionImplementor session) throws SQLException, HibernateException {
 		return rowProcessor.buildResultRow( row, rs, transformer != null, session );
 	}
 
 	@Override
     protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return rowProcessor.buildResultRow( row, rs, session );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
     protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		// meant to handle dynamic instantiation queries...(Copy from QueryLoader)
 		HolderInstantiator holderInstantiator = HolderInstantiator.getHolderInstantiator(
 				null,
 				resultTransformer,
 				getReturnAliasesForTransformer()
 		);
 		if ( holderInstantiator.isRequired() ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = ( Object[] ) results.get( i );
 				Object result = holderInstantiator.instantiate(row);
 				results.set( i, result );
 			}
 
 			return resultTransformer.transformList(results);
 		}
 		else {
 			return results;
 		}
 	}
 
 	private String[] getReturnAliasesForTransformer() {
 		return transformerAliases;
 	}
 
 	@Override
     protected EntityAliases[] getEntityAliases() {
 		return entityAliases;
 	}
 
 	@Override
     protected CollectionAliases[] getCollectionAliases() {
 		return collectionAliases;
 	}
 
 	@Override
     public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object loc = namedParameterBindPoints.get( name );
 		if ( loc == null ) {
 			throw new QueryException(
 					"Named parameter does not appear in Query: " + name,
 					sql
 			);
 		}
 		if ( loc instanceof Integer ) {
 			return new int[] { (Integer) loc };
 		}
 		else {
 			return ArrayHelper.toIntArray( ( List ) loc );
 		}
 	}
 
 
 	@Override
     protected void autoDiscoverTypes(ResultSet rs) {
 		try {
 			JdbcResultMetadata metadata = new JdbcResultMetadata( getFactory(), rs );
 			rowProcessor.prepareForAutoDiscovery( metadata );
 
 			List<String> aliases = new ArrayList<String>();
 			List<Type> types = new ArrayList<Type>();
 			for ( ResultColumnProcessor resultProcessor : rowProcessor.getColumnProcessors() ) {
 				resultProcessor.performDiscovery( metadata, types, aliases );
 			}
 
 			validateAliases( aliases );
 
 			resultTypes = ArrayHelper.toTypeArray( types );
 			transformerAliases = ArrayHelper.toStringArray( aliases );
 		}
 		catch ( SQLException e ) {
 			throw new HibernateException( "Exception while trying to autodiscover types.", e );
 		}
 	}
 
 	private void validateAliases(List<String> aliases) {
 		// lets make sure we did not end up with duplicate aliases.  this can occur when the user supplied query
 		// did not rename same-named columns.  e.g.:
 		//		select u.username, u2.username from t_user u, t_user u2 ...
 		//
 		// the above will lead to an unworkable situation in most cases (the difference is how the driver/db
 		// handle this situation.  But if the 'aliases' variable contains duplicate names, then we have that
 		// troublesome condition, so lets throw an error.  See HHH-5992
 		final HashSet<String> aliasesSet = new HashSet<String>();
 		for ( String alias : aliases ) {
 			validateAlias( alias );
 			boolean alreadyExisted = !aliasesSet.add( alias );
 			if ( alreadyExisted ) {
 				throw new NonUniqueDiscoveredSqlAliasException(
 						"Encountered a duplicated sql alias [" + alias + "] during auto-discovery of a native-sql query"
 				);
 			}
 		}
 	}
 
 	@SuppressWarnings("UnusedParameters")
 	protected void validateAlias(String alias) {
 	}
+	
+	/**
+	 * {@link #resultTypes} can be overridden by {@link #autoDiscoverTypes(ResultSet)},
+	 * *after* {@link #list(SessionImplementor, QueryParameters)} has already been called.  It's a bit of a
+	 * chicken-and-the-egg issue since {@link #autoDiscoverTypes(ResultSet)} needs the {@link ResultSet}.
+	 * 
+	 * As a hacky workaround, override
+	 * {@link #putResultInQueryCache(SessionImplementor, QueryParameters, Type[], QueryCache, QueryKey, List)} here
+	 * and provide the {@link #resultTypes}.
+	 * 
+	 * @see HHH-3051
+	 */
+	@Override
+	protected void putResultInQueryCache(
+			final SessionImplementor session,
+			final QueryParameters queryParameters,
+			final Type[] resultTypes,
+			final QueryCache queryCache,
+			final QueryKey key,
+			final List result) {
+		super.putResultInQueryCache( session, queryParameters, this.resultTypes, queryCache, key, result );
+	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
index a4c1430b80..e3a4c73136 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,620 +1,617 @@
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
 package org.hibernate.loader.hql;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
 import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.SelectClause;
 import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
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
 
 	private final Map<String, String> sqlAliasByEntityAlias = new HashMap<String, String>(8);
 
 	private EntityType[] ownerAssociationTypes;
 	private int[] owners;
 	private boolean[] entityEagerPropertyFetches;
 
 	private int[] collectionOwners;
 	private QueryableCollection[] collectionPersisters;
 
 	private int selectLength;
 
 	private AggregatedSelectExpression aggregatedSelectExpression;
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
 
 		aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
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
 
 	public AggregatedSelectExpression getAggregatedSelectExpression() {
 		return aggregatedSelectExpression;
 	}
 
 
 	// -- Loader implementation --
 
 	public final void validateScrollability() throws HibernateException {
 		queryTranslator.validateScrollability();
 	}
 	@Override
 	protected boolean needsFetchingScroll() {
 		return queryTranslator.containsCollectionFetches();
 	}
 	@Override
 	public Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 	@Override
 	public String[] getAliases() {
 		return sqlAliases;
 	}
 
 	public String[] getSqlAliasSuffixes() {
 		return sqlAliasSuffixes;
 	}
 	@Override
 	public String[] getSuffixes() {
 		return getSqlAliasSuffixes();
 	}
 	@Override
 	public String[] getCollectionSuffixes() {
 		return collectionSuffixes;
 	}
 	@Override
 	protected String getQueryIdentifier() {
 		return queryTranslator.getQueryIdentifier();
 	}
 
 	/**
 	 * The SQL query string to be called.
 	 */
 	@Override
 	public String getSQLString() {
 		return queryTranslator.getSQLString();
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only collection loaders
 	 * return a non-null value
 	 */
 	@Override
 	protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 	@Override
 	protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 	@Override
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return entityEagerPropertyFetches;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner")
 	 */
 	@Override
 	protected int[] getOwners() {
 		return owners;
 	}
 	@Override
 	protected EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	// -- Loader overrides --
 	@Override
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	/**
 	 * @param lockOptions a collection of lock modes specified dynamically via the Query interface
 	 */
 	@Override
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
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		// we are given a map of user-alias -> lock mode
 		// create a new map of sql-alias -> lock mode
 
 		final LockOptions lockOptions = parameters.getLockOptions();
 
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 
 		// user is request locking, lets see if we can apply locking directly to the SQL...
 
 		// 		some dialects wont allow locking with paging...
 		if ( shouldUseFollowOnLocking( parameters, dialect, afterLoadActions ) ) {
 			return sql;
 		}
 
 		//		there are other conditions we might want to add here, such as checking the result types etc
 		//		but those are better served after we have redone the SQL generation to use ASTs.
 
 
 		// we need both the set of locks and the columns to reference in locks
 		// as the ultimate output of this section...
 		final LockOptions locks = new LockOptions( lockOptions.getLockMode() );
 		final Map<String, String[]> keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap<String, String[]>() : null;
 
 		locks.setScope( lockOptions.getScope() );
 		locks.setTimeOut( lockOptions.getTimeOut() );
 
 		for ( Map.Entry<String, String> entry : sqlAliasByEntityAlias.entrySet() ) {
 			final String userAlias =  entry.getKey();
 			final String drivingSqlAlias = entry.getValue();
 			if ( drivingSqlAlias == null ) {
 				throw new IllegalArgumentException( "could not locate alias to apply lock mode : " + userAlias );
 			}
 			// at this point we have (drivingSqlAlias) the SQL alias of the driving table
 			// corresponding to the given user alias.  However, the driving table is not
 			// (necessarily) the table against which we want to apply locks.  Mainly,
 			// the exception case here is joined-subclass hierarchies where we instead
 			// want to apply the lock against the root table (for all other strategies,
 			// it just happens that driving and root are the same).
 			final QueryNode select = (QueryNode) queryTranslator.getSqlAST();
 			final Lockable drivingPersister = (Lockable) select.getFromClause()
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
 	@Override
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
 	@Override
 	protected boolean upgradeLocks() {
 		return true;
 	}
 
 	private boolean hasSelectNew() {
 		return aggregatedSelectExpression != null &&  aggregatedSelectExpression.getResultTransformer() != null;
 	}
 	@Override
 	protected String[] getResultRowAliases() {
 		return queryReturnAliases;
 	}
 	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.resolveResultTransformer( implicitResultTransformer, resultTransformer );
 	}
 	@Override
 	protected boolean[] includeInResultRow() {
 		boolean[] includeInResultTuple = includeInSelect;
 		if ( hasScalars ) {
 			includeInResultTuple = new boolean[ queryReturnTypes.length ];
 			Arrays.fill( includeInResultTuple, true );
 		}
 		return includeInResultTuple;
 	}
 	@Override
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		Object[] resultRow = getResultRow( row, rs, session );
 		boolean hasTransform = hasSelectNew() || transformer!=null;
 		return ( ! hasTransform && resultRow.length == 1 ?
 				resultRow[ 0 ] :
 				resultRow
 		);
 	}
 
 	@Override
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
 
 	@SuppressWarnings("unchecked")
 	@Override
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
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
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
-		return list( session, queryParameters, queryTranslator.getQuerySpaces() );
-	}
-	
-	protected Type[] getReturnTypes() {
-		return queryReturnTypes;
+		return list( session, queryParameters, queryTranslator.getQuerySpaces(), queryReturnTypes );
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
 			if ( queryParameters.isCallable() ) {
 				throw new QueryException("iterate() not supported for callable statements");
 			}
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, Collections.<AfterLoadAction>emptyList(), session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
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
+				queryReturnTypes,
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
 	@Override
 	public int[] getNamedParameterLocs(String name) throws QueryException {
 		return queryTranslator.getParameterTranslations().getNamedParameterSqlLocations( name );
 	}
 
 	/**
 	 * We specifically override this method here, because in general we know much more
 	 * about the parameters and their appropriate bind positions here then we do in
 	 * our super because we track them explicitly here through the ParameterSpecification
 	 * interface.
 	 *
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	@Override
 	protected int bindParameterValues(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException {
 		int position = startIndex;
 		List<ParameterSpecification> parameterSpecs = queryTranslator.getCollectedParameterSpecifications();
 		for ( ParameterSpecification spec : parameterSpecs ) {
 			position += spec.bind( statement, queryParameters, session, position );
 		}
 		return position - startIndex;
 	}
 }
