diff --git a/hibernate-core/src/main/java/org/hibernate/HibernateError.java b/hibernate-core/src/main/java/org/hibernate/HibernateError.java
new file mode 100644
index 0000000000..4376031c02
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/HibernateError.java
@@ -0,0 +1,43 @@
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
+package org.hibernate;
+
+/**
+ * Marks a group of exceptions that generally indicate an internal Hibernate error or bug.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class HibernateError extends HibernateException {
+	public HibernateError(String message) {
+		super( message );
+	}
+
+	public HibernateError(Throwable root) {
+		super( root );
+	}
+
+	public HibernateError(String message, Throwable root) {
+		super( message, root );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
index deca43ace7..401b2fe4c6 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
@@ -1,1065 +1,1066 @@
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
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
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
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
+import org.hibernate.loader.spi.AfterLoadAction;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryTranslatorImpl.class.getName());
 
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
 	private final Set querySpaces = new HashSet();
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
 	private int nameCount = 0;
 	private int parameterCount = 0;
 	private boolean distinct = false;
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
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 		catch ( MappingException me ) {
 			throw me;
 		}
 		catch ( Exception e ) {
 			LOG.debug( "Unexpected query compilation problem", e );
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
 			QueryException qe = new QueryException( ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name );
 			qe.setQueryString( queryString );
 			throw qe;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 2c80db36c2..2ad4cf8479 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1530 +1,1527 @@
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
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Abstract superclass of object loading (and querying) strategies. This class implements
  * useful common functionality that concrete loaders delegate to. It is not intended that this
  * functionality would be directly accessed by client code. (Hence, all methods of this class
  * are declared <tt>protected</tt> or <tt>private</tt>.) This class relies heavily upon the
  * <tt>Loadable</tt> interface, which is the contract between this class and
  * <tt>EntityPersister</tt>s that may be loaded by it.<br>
  * <br>
  * The present implementation is able to load any number of columns of entities and at most
  * one collection role per query.
  *
  * @author Gavin King
  * @see org.hibernate.persister.entity.Loadable
  */
 public abstract class Loader {
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
 
 	private final SessionFactoryImplementor factory;
 	private ColumnNameCache columnNameCache;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	public abstract String getSQLString();
 
 	/**
 	 * An array of persisters of entity classes contained in each row of results;
 	 * implemented by all subclasses
 	 *
 	 * @return The entity persisters.
 	 */
 	protected abstract Loadable[] getEntityPersisters();
 
 	/**
 	 * An array indicating whether the entities have eager property fetching
 	 * enabled.
 	 *
 	 * @return Eager property fetching indicators.
 	 */
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return null;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner").  The
 	 * indexes contained here are relative to the result of
 	 * {@link #getEntityPersisters}.
 	 *
 	 * @return The owner indicators (see discussion above).
 	 */
 	protected int[] getOwners() {
 		return null;
 	}
 
 	/**
 	 * An array of the owner types corresponding to the {@link #getOwners()}
 	 * returns.  Indices indicating no owner would be null here.
 	 *
 	 * @return The types for the owners.
 	 */
 	protected EntityType[] getOwnerAssociationTypes() {
 		return null;
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only
 	 * collection loaders return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return null;
 	}
 
 	/**
 	 * Get the index of the entity that owns the collection, or -1
 	 * if there is no owner in the query results (ie. in the case of a
 	 * collection initializer) or no collection.
 	 */
 	protected int[] getCollectionOwners() {
 		return null;
 	}
 
 	protected int[][] getCompositeKeyManyToOneTargetIndices() {
 		return null;
 	}
 
 	/**
 	 * What lock options does this load entities with?
 	 *
 	 * @param lockOptions a collection of lock options specified dynamically via the Query interface
 	 */
 	//protected abstract LockOptions[] getLockOptions(Map lockOptions);
 	protected abstract LockMode[] getLockModes(LockOptions lockOptions);
 
 	/**
 	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
 	 * empty superclass implementation merely returns its first
 	 * argument.
 	 */
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		return sql;
 	}
 
 	/**
 	 * Does this query return objects that might be already cached
 	 * by the session, whose lock mode may need upgrading
 	 */
 	protected boolean upgradeLocks() {
 		return false;
 	}
 
 	/**
 	 * Return false is this loader is a batch entity loader
 	 */
 	protected boolean isSingleRowLoader() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL table aliases of entities whose
 	 * associations are subselect-loadable, returning
 	 * null if this loader does not support subselect
 	 * loading
 	 */
 	protected String[] getAliases() {
 		return null;
 	}
 
 	/**
 	 * Modify the SQL, adding lock hints and comments, if necessary
 	 */
 	protected String preprocessSQL(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		sql = applyLocks( sql, parameters, dialect, afterLoadActions );
 		return getFactory().getSettings().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
-	protected static interface AfterLoadAction {
-		public void afterLoad(SessionImplementor session, Object entity, Loadable persister);
-	}
-
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( dialect.useFollowOnLocking() ) {
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
 			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
 				LOG.usingFollowOnLocking();
 				lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
 				lockOptions.setScope( parameters.getLockOptions().getScope() );
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptions ).lock( persister.getEntityName(), entity );
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.hasAliasSpecificLockModes() ) {
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuilder( comment.length() + sql.length() + 5 )
 					.append( "/* " )
 					.append( comment )
 					.append( " */ " )
 					.append( sql )
 					.toString();
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	public List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
 		return doQueryAndInitializeNonLazyCollections(
 				session,
 				queryParameters,
 				returnProxies,
 				null
 		);
 	}
 
 	public List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException, SQLException {
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
 		persistenceContext.beforeLoad();
 		List result;
 		try {
 			try {
 				result = doQuery( session, queryParameters, returnProxies, forcedResultTransformer );
 			}
 			finally {
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 		return result;
 	}
 
 	/**
 	 * Loads a single row from the result set.  This is the processing used from the
 	 * ScrollableResults where no collection fetches were encountered.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		final Object result;
 		try {
 			result = getRowFromResultSet(
 			        resultSet,
 					session,
 					queryParameters,
 					getLockModes( queryParameters.getLockOptions() ),
 					null,
 					hydratedObjects,
 					new EntityKey[entitySpan],
 					returnProxies
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not read next row of results",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private Object sequentialLoad(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final EntityKey keyToRead) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		Object result = null;
 		final EntityKey[] loadedKeys = new EntityKey[entitySpan];
 
 		try {
 			do {
 				Object loaded = getRowFromResultSet(
 						resultSet,
 						session,
 						queryParameters,
 						getLockModes( queryParameters.getLockOptions() ),
 						null,
 						hydratedObjects,
 						loadedKeys,
 						returnProxies
 				);
 				if ( ! keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0] )
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 		);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SessionImplementor session) throws SQLException {
 		EntityKey currentRowKey = getKeyFromResultSet(
 				persisterIndex, getEntityPersisters()[persisterIndex], null, resultSet, session
 		);
 		return keyToRead.equals( currentRowKey );
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isAfterLast() ) {
 				// don't even bother trying to read further
 				return null;
 			}
 
 			if ( resultSet.isBeforeFirst() ) {
 				resultSet.next();
 			}
 
 			// We call getKeyFromResultSet() here so that we can know the
-			// key value upon which to doAfterTransactionCompletion the breaking logic.  However,
+			// key value upon which to perform the breaking logic.  However,
 			// it is also then called from getRowFromResultSet() which is certainly
 			// not the most efficient.  But the call here is needed, and there
 			// currently is no other way without refactoring of the doQuery()/getRowFromResultSet()
 			// methods
 			final EntityKey currentKey = getKeyFromResultSet(
 					0,
 					getEntityPersisters()[0],
 					null,
 					resultSet,
 					session
 				);
 
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, currentKey );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
-			        "could not doAfterTransactionCompletion sequential read of results (forward)",
+			        "could not perform sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final boolean isLogicallyAfterLast) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isFirst() ) {
 				// don't even bother trying to read any further
 				return null;
 			}
 
 			EntityKey keyToRead = null;
 			// This check is needed since processing leaves the cursor
 			// after the last physical row for the current logical row;
 			// thus if we are after the last physical row, this might be
 			// caused by either:
 			//      1) scrolling to the last logical row
 			//      2) scrolling past the last logical row
 			// In the latter scenario, the previous logical row
 			// really is the last logical row.
 			//
 			// In all other cases, we should process back two
 			// logical records (the current logic row, plus the
 			// previous logical row).
 			if ( resultSet.isAfterLast() && isLogicallyAfterLast ) {
 				// position cursor to the last row
 				resultSet.last();
 				keyToRead = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 			}
 			else {
 				// Since the result set cursor is always left at the first
 				// physical row after the "last processed", we need to jump
 				// back one position to get the key value we are interested
 				// in skipping
 				resultSet.previous();
 
 				// sequentially read the result set in reverse until we recognize
 				// a change in the key value.  At that point, we are pointed at
 				// the last physical sequential row for the logical row in which
 				// we are interested in processing
 				boolean firstPass = true;
 				final EntityKey lastKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 				while ( resultSet.previous() ) {
 					EntityKey checkKey = getKeyFromResultSet(
 							0,
 							getEntityPersisters()[0],
 							null,
 							resultSet,
 							session
 						);
 
 					if ( firstPass ) {
 						firstPass = false;
 						keyToRead = checkKey;
 					}
 
 					if ( !lastKey.equals( checkKey ) ) {
 						break;
 					}
 				}
 
 			}
 
 			// Read backwards until we read past the first physical sequential
 			// row with the key we are interested in loading
 			while ( resultSet.previous() ) {
 				EntityKey checkKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 
 				if ( !keyToRead.equals( checkKey ) ) {
 					break;
 				}
 			}
 
 			// Finally, read ahead one row to position result set cursor
 			// at the first physical row we are interested in loading
 			resultSet.next();
 
 			// and doAfterTransactionCompletion the load
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, keyToRead );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies) throws SQLException, HibernateException {
 		return getRowFromResultSet(
 				resultSet,
 				session,
 				queryParameters,
 				lockModesArray,
 				optionalObjectKey,
 				hydratedObjects,
 				keys,
 				returnProxies,
 				null
 		);
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies,
 	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
 		registerNonExists( keys, persisters, session );
 
 		// this call is side-effecty
 		Object[] row = getRow(
 		        resultSet,
 				persisters,
 				keys,
 				queryParameters.getOptionalObject(),
 				optionalObjectKey,
 				lockModesArray,
 				hydratedObjects,
 				session
 		);
 
 		readCollectionElements( row, resultSet, session );
 
 		if ( returnProxies ) {
 			// now get an existing proxy for each row element (if there is one)
 			for ( int i = 0; i < entitySpan; i++ ) {
 				Object entity = row[i];
 				Object proxy = session.getPersistenceContext().proxyFor( persisters[i], keys[i], entity );
 				if ( entity != proxy ) {
 					// force the proxy to resolve itself
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple( getResultRow( row, resultSet, session ), getResultRowAliases() )
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
 		}
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			if ( idType.isComponentType() && getCompositeKeyManyToOneTargetIndices() != null ) {
 				// we may need to force resolve any key-many-to-one(s)
 				int[] keyManyToOneTargetIndices = getCompositeKeyManyToOneTargetIndices()[i];
 				// todo : better solution is to order the index processing based on target indices
 				//		that would account for multiple levels whereas this scheme does not
 				if ( keyManyToOneTargetIndices != null ) {
 					for ( int targetIndex : keyManyToOneTargetIndices ) {
 						if ( targetIndex < numberOfPersistersToProcess ) {
 							final Type targetIdType = persisters[targetIndex].getIdentifierType();
 							final Serializable targetId = (Serializable) targetIdType.resolve(
 									hydratedKeyState[targetIndex],
 									session,
 									null
 							);
 							// todo : need a way to signal that this key is resolved and its data resolved
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
 						}
 
 						// this part copied from #getRow, this section could be refactored out
 						Object object = session.getEntityUsingInterceptor( keys[targetIndex] );
 						if ( object != null ) {
 							//its already loaded so don't need to hydrate it
 							instanceAlreadyLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									keys[targetIndex],
 									object,
 									lockModes[targetIndex],
 									session
 							);
 						}
 						else {
 							instanceNotYetLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									getEntityAliases()[targetIndex].getRowIdAlias(),
 									keys[targetIndex],
 									lockModes[targetIndex],
 									getOptionalObjectKey( queryParameters, session ),
 									queryParameters.getOptionalObject(),
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
 						null; //if null, owner will be retrieved from session
 
 				final CollectionPersister collectionPersister = collectionPersisters[i];
 				final Serializable key;
 				if ( owner == null ) {
 					key = null;
 				}
 				else {
 					key = collectionPersister.getCollectionType().getKeyOfOwner( owner, session );
 					//TODO: old version did not require hashmap lookup:
 					//keys[collectionOwner].getIdentifier()
 				}
 
 				readCollectionElement(
 						owner,
 						key,
 						collectionPersister,
 						descriptors[i],
 						resultSet,
 						session
 					);
 
 			}
 
 		}
 	}
 
 	private List doQuery(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final ResultSet rs = wrapper.getResultSet();
 		final Statement st = wrapper.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SessionImplementor session,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			int maxRows,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
 		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 		LOG.trace( "Processing result set" );
 		int count;
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
 			LOG.debugf( "Result set row: %s", count );
 			Object result = getRowFromResultSet(
 					rs,
 					session,
 					queryParameters,
 					lockModesArray,
 					optionalObjectKey,
 					hydratedObjects,
 					keys,
 					returnProxies,
 					forcedResultTransformer
 			);
 			results.add( result );
 			if ( createSubselects ) {
 				subselectResultKeys.add(keys);
 				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				rs,
 				session,
 				queryParameters.isReadOnly( session ),
 				afterLoadActions
 		);
 		if ( createSubselects ) {
 			createSubselects( subselectResultKeys, queryParameters, session );
 		}
 		return results;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
 								aliases[i],
 								loadables[i],
 								queryParameters,
 								keySets[i],
 								namedParameterLocMap
 							);
 
 						session.getPersistenceContext()
 								.getBatchFetchQueue()
 								.addSubselect( rowKeys[i], subselectFetch );
 					}
 
 				}
 
 			}
 		}
 	}
 
 	private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
 					);
 			}
 			return namedParameterLocMap;
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.<AfterLoadAction>emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
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
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
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
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
 					if ( LOG.isDebugEnabled() ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/collection/DynamicBatchingCollectionInitializerBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/collection/DynamicBatchingCollectionInitializerBuilder.java
index 62e82e78f6..8f470ca1e1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/collection/DynamicBatchingCollectionInitializerBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/collection/DynamicBatchingCollectionInitializerBuilder.java
@@ -1,269 +1,270 @@
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
 package org.hibernate.loader.collection;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.JoinWalker;
 import org.hibernate.loader.Loader;
+import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * A BatchingCollectionInitializerBuilder that builds CollectionInitializer instances capable of dynamically building
  * its batch-fetch SQL based on the actual number of collections keys waiting to be fetched.
  *
  * @author Steve Ebersole
  */
 public class DynamicBatchingCollectionInitializerBuilder extends BatchingCollectionInitializerBuilder {
 	public static final DynamicBatchingCollectionInitializerBuilder INSTANCE = new DynamicBatchingCollectionInitializerBuilder();
 
 	@Override
 	protected CollectionInitializer createRealBatchingCollectionInitializer(
 			QueryableCollection persister,
 			int maxBatchSize,
 			SessionFactoryImplementor factory,
 			LoadQueryInfluencers influencers) {
 		return new DynamicBatchingCollectionInitializer( persister, maxBatchSize, factory, influencers );
 	}
 
 	@Override
 	protected CollectionInitializer createRealBatchingOneToManyInitializer(
 			QueryableCollection persister,
 			int maxBatchSize,
 			SessionFactoryImplementor factory,
 			LoadQueryInfluencers influencers) {
 		return new DynamicBatchingCollectionInitializer( persister, maxBatchSize, factory, influencers );
 	}
 
 	public static class DynamicBatchingCollectionInitializer extends BatchingCollectionInitializer {
 		private final int maxBatchSize;
 		private final Loader singleKeyLoader;
 		private final DynamicBatchingCollectionLoader batchLoader;
 
 		public DynamicBatchingCollectionInitializer(
 				QueryableCollection collectionPersister,
 				int maxBatchSize,
 				SessionFactoryImplementor factory,
 				LoadQueryInfluencers influencers) {
 			super( collectionPersister );
 			this.maxBatchSize = maxBatchSize;
 
 			if ( collectionPersister.isOneToMany() ) {
 				this.singleKeyLoader = new OneToManyLoader( collectionPersister, 1, factory, influencers );
 			}
 			else {
 				this.singleKeyLoader = new BasicCollectionLoader( collectionPersister, 1, factory, influencers );
 			}
 
 			this.batchLoader = new DynamicBatchingCollectionLoader( collectionPersister, factory, influencers );
 		}
 
 		@Override
 		public void initialize(Serializable id, SessionImplementor session) throws HibernateException {
 			// first, figure out how many batchable ids we have...
 			final Serializable[] batch = session.getPersistenceContext()
 					.getBatchFetchQueue()
 					.getCollectionBatch( collectionPersister(), id, maxBatchSize );
 			final int numberOfIds = ArrayHelper.countNonNull( batch );
 			if ( numberOfIds <= 1 ) {
 				singleKeyLoader.loadCollection( session, id, collectionPersister().getKeyType() );
 				return;
 			}
 
 			final Serializable[] idsToLoad = new Serializable[numberOfIds];
 			System.arraycopy( batch, 0, idsToLoad, 0, numberOfIds );
 
 			batchLoader.doBatchedCollectionLoad( session, idsToLoad, collectionPersister().getKeyType() );
 		}
 	}
 
 	private static class DynamicBatchingCollectionLoader extends CollectionLoader {
 		// todo : this represents another case where the current Loader contract is unhelpful
 		//		the other recent case was stored procedure support.  Really any place where the SQL
 		//		generation is dynamic but the "loading plan" remains constant.  The long term plan
 		//		is to split Loader into (a) PreparedStatement generation/execution and (b) ResultSet
 		// 		processing.
 		//
 		// Same holds true for org.hibernate.loader.entity.DynamicBatchingEntityLoaderBuilder.DynamicBatchingEntityLoader
 		//
 		// for now I will essentially semi-re-implement the collection loader contract here to be able to alter
 		// 		the SQL (specifically to be able to dynamically build the WHERE-clause IN-condition) later, when
 		//		we actually know the ids to batch fetch
 
 		private final String sqlTemplate;
 		private final String alias;
 
 		public DynamicBatchingCollectionLoader(
 				QueryableCollection collectionPersister,
 				SessionFactoryImplementor factory,
 				LoadQueryInfluencers influencers) {
 			super( collectionPersister, factory, influencers );
 
 			JoinWalker walker = buildJoinWalker( collectionPersister, factory, influencers );
 			initFromWalker( walker );
 			this.sqlTemplate = walker.getSQLString();
 			this.alias = StringHelper.generateAlias( collectionPersister.getRole(), 0 );
 			postInstantiate();
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"SQL-template for dynamic collection [%s] batch-fetching : %s",
 						collectionPersister.getRole(),
 						sqlTemplate
 				);
 			}
 		}
 
 		private JoinWalker buildJoinWalker(
 				QueryableCollection collectionPersister,
 				SessionFactoryImplementor factory,
 				LoadQueryInfluencers influencers) {
 
 			if ( collectionPersister.isOneToMany() ) {
 				return new OneToManyJoinWalker( collectionPersister, -1, null, factory, influencers ) {
 					@Override
 					protected StringBuilder whereString(String alias, String[] columnNames, String subselect, int batchSize) {
 						if ( subselect != null ) {
 							return super.whereString( alias, columnNames, subselect, batchSize );
 						}
 
 						return StringHelper.buildBatchFetchRestrictionFragment( alias, columnNames, getFactory().getDialect() );
 					}
 				};
 			}
 			else {
 				return new BasicCollectionJoinWalker( collectionPersister, -1, null, factory, influencers ) {
 					@Override
 					protected StringBuilder whereString(String alias, String[] columnNames, String subselect, int batchSize) {
 						if ( subselect != null ) {
 							return super.whereString( alias, columnNames, subselect, batchSize );
 						}
 
 						return StringHelper.buildBatchFetchRestrictionFragment( alias, columnNames, getFactory().getDialect() );
 					}
 				};
 			}
 		}
 
 		public final void doBatchedCollectionLoad(
 				final SessionImplementor session,
 				final Serializable[] ids,
 				final Type type) throws HibernateException {
 
 			if ( LOG.isDebugEnabled() )
 				LOG.debugf( "Batch loading collection: %s",
 							MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ) );
 
 			final Type[] idTypes = new Type[ids.length];
 			Arrays.fill( idTypes, type );
 			final QueryParameters queryParameters = new QueryParameters( idTypes, ids, ids );
 
 			final String sql = StringHelper.expandBatchIdPlaceholder(
 					sqlTemplate,
 					ids,
 					alias,
 					collectionPersister().getKeyColumnNames(),
 					getFactory().getDialect()
 			);
 
 			try {
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
 				persistenceContext.beforeLoad();
 				try {
 					try {
 						doTheLoad( sql, queryParameters, session );
 					}
 					finally {
 						persistenceContext.afterLoad();
 					}
 					persistenceContext.initializeNonLazyCollections();
 				}
 				finally {
 					// Restore the original default
 					persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 				}
 			}
 			catch ( SQLException e ) {
 				throw getFactory().getSQLExceptionHelper().convert(
 						e,
 						"could not initialize a collection batch: " +
 								MessageHelper.collectionInfoString( collectionPersister(), ids, getFactory() ),
 						sql
 				);
 			}
 
 			LOG.debug( "Done batch load" );
 
 		}
 
 		private void doTheLoad(String sql, QueryParameters queryParameters, SessionImplementor session) throws SQLException {
 			final RowSelection selection = queryParameters.getRowSelection();
 			final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 					selection.getMaxRows() :
 					Integer.MAX_VALUE;
 
 			final List<AfterLoadAction> afterLoadActions = Collections.emptyList();
 			final SqlStatementWrapper wrapper = executeQueryStatement( sql, queryParameters, false, afterLoadActions, session );
 			final ResultSet rs = wrapper.getResultSet();
 			final Statement st = wrapper.getStatement();
 			try {
 				processResultSet( rs, queryParameters, session, true, null, maxRows, afterLoadActions );
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index e1caffb218..0b7093bc0e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,289 +1,290 @@
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
+import org.hibernate.loader.spi.AfterLoadAction;
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
 	private final Set querySpaces;
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
 	
 	public ScrollableResults scroll(SessionImplementor session, ScrollMode scrollMode) 
 	throws HibernateException {
 		QueryParameters qp = translator.getQueryParameters();
 		qp.setScrollMode(scrollMode);
 		return scroll(qp, resultTypes, null, session);
 	}
 
 	public List list(SessionImplementor session) 
 	throws HibernateException {
 		return list( session, translator.getQueryParameters(), querySpaces, resultTypes );
 
 	}
 
 	protected String[] getResultRowAliases() {
 		return userAliases;
 	}
 
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return true;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 	throws SQLException, HibernateException {
 		return resolveResultTransformer( transformer ).transformTuple(
 				getResultRow( row, rs, session),
 				getResultRowAliases()
 		);
 	}
 			
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
 
 
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.getAliasLockCount() > 1 ) {
 			// > 1 here because criteria always uses alias map for the root lock mode (under 'this_')
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
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
 
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) {
 		return resolveResultTransformer( resultTransformer ).transformList( results );
 	}
 	
 	protected String getQueryIdentifier() { 
 		return "[CRITERIA] " + getSQLString(); 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
index 239451eea5..b626e375ec 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
@@ -1,706 +1,702 @@
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
 
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.util.ArrayList;
-import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.dialect.pagination.LimitHandler;
-import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.QueryParameters;
-import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.Loader;
+import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
-import org.hibernate.persister.entity.Lockable;
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
 	private final Set querySpaces = new HashSet();
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
 
 		List entityPersisters = new ArrayList();
 		List entityOwners = new ArrayList();
 		List entityAliases = new ArrayList();
 
 		List collectionPersisters = new ArrayList();
 		List collectionOwners = new ArrayList();
 		List collectionAliases = new ArrayList();
 
 		List lockModes = new ArrayList();
 		List resultColumnProcessors = new ArrayList();
 		List nonScalarReturnList = new ArrayList();
 		List resultTypes = new ArrayList();
 		List specifiedAliases = new ArrayList();
 		int returnableCounter = 0;
 		boolean hasScalars = false;
 
 		List includeInResultRowList = new ArrayList();
 
 		Iterator itr = customQuery.getCustomQueryReturns().iterator();
 		while ( itr.hasNext() ) {
 			final Return rtn = ( Return ) itr.next();
 			if ( rtn instanceof ScalarReturn ) {
 				ScalarReturn scalarRtn = ( ScalarReturn ) rtn;
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
 			else if ( rtn instanceof RootReturn ) {
 				RootReturn rootRtn = ( RootReturn ) rtn;
 				Queryable persister = ( Queryable ) factory.getEntityPersister( rootRtn.getEntityName() );
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
 				CollectionReturn collRtn = ( CollectionReturn ) rtn;
 				String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 				QueryableCollection persister = ( QueryableCollection ) factory.getCollectionPersister( role );
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
 					Queryable elementPersister = ( Queryable ) ( ( EntityType ) elementType ).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( -1 );
 					entityAliases.add( collRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
 				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof EntityFetchReturn ) {
 				EntityFetchReturn fetchRtn = ( EntityFetchReturn ) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				entityOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				EntityType fetchedType = ( EntityType ) ownerPersister.getPropertyType( fetchRtn.getOwnerProperty() );
 				String entityName = fetchedType.getAssociatedEntityName( getFactory() );
 				Queryable persister = ( Queryable ) factory.getEntityPersister( entityName );
 				entityPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				entityAliases.add( fetchRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 				includeInResultRowList.add( false );
 			}
 			else if ( rtn instanceof CollectionFetchReturn ) {
 				CollectionFetchReturn fetchRtn = ( CollectionFetchReturn ) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				collectionOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				String role = ownerPersister.getEntityName() + '.' + fetchRtn.getOwnerProperty();
 				QueryableCollection persister = ( QueryableCollection ) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				collectionAliases.add( fetchRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = ( Queryable ) ( ( EntityType ) elementType ).getAssociatedJoinable( factory );
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
 			this.entityPersisters[i] = ( Queryable ) entityPersisters.get( i );
 		}
 		this.entiytOwners = ArrayHelper.toIntArray( entityOwners );
 		this.entityAliases = new EntityAliases[ entityAliases.size() ];
 		for ( int i = 0; i < entityAliases.size(); i++ ) {
 			this.entityAliases[i] = ( EntityAliases ) entityAliases.get( i );
 		}
 
 		this.collectionPersisters = new QueryableCollection[ collectionPersisters.size() ];
 		for ( int i = 0; i < collectionPersisters.size(); i++ ) {
 			this.collectionPersisters[i] = ( QueryableCollection ) collectionPersisters.get( i );
 		}
 		this.collectionOwners = ArrayHelper.toIntArray( collectionOwners );
 		this.collectionAliases = new CollectionAliases[ collectionAliases.size() ];
 		for ( int i = 0; i < collectionAliases.size(); i++ ) {
 			this.collectionAliases[i] = ( CollectionAliases ) collectionAliases.get( i );
 		}
 
 		this.lockModes = new LockMode[ lockModes.size() ];
 		for ( int i = 0; i < lockModes.size(); i++ ) {
 			this.lockModes[i] = ( LockMode ) lockModes.get( i );
 		}
 
 		this.resultTypes = ArrayHelper.toTypeArray( resultTypes );
 		this.transformerAliases = ArrayHelper.toStringArray( specifiedAliases );
 
 		this.rowProcessor = new ResultRowProcessor(
 				hasScalars,
 		        ( ResultColumnProcessor[] ) resultColumnProcessors.toArray( new ResultColumnProcessor[ resultColumnProcessors.size() ] )
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
 
 	public List list(SessionImplementor session, QueryParameters queryParameters) throws HibernateException {
 		return list( session, queryParameters, querySpaces, resultTypes );
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
 
 	public ScrollableResults scroll(
 			final QueryParameters queryParameters,
 			final SessionImplementor session) throws HibernateException {
 		return scroll(
 				queryParameters,
 				resultTypes,
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
 			return new int[] { ( ( Integer ) loc ).intValue() };
 		}
 		else {
 			return ArrayHelper.toIntArray( ( List ) loc );
 		}
 	}
 
 
 	public class ResultRowProcessor {
 		private final boolean hasScalars;
 		private ResultColumnProcessor[] columnProcessors;
 
 		public ResultRowProcessor(boolean hasScalars, ResultColumnProcessor[] columnProcessors) {
 			this.hasScalars = hasScalars || ( columnProcessors == null || columnProcessors.length == 0 );
 			this.columnProcessors = columnProcessors;
 		}
 
 		public void prepareForAutoDiscovery(Metadata metadata) throws SQLException {
 			if ( columnProcessors == null || columnProcessors.length == 0 ) {
 				int columns = metadata.getColumnCount();
 				columnProcessors = new ResultColumnProcessor[ columns ];
 				for ( int i = 1; i <= columns; i++ ) {
 					columnProcessors[ i - 1 ] = new ScalarResultColumnProcessor( i );
 				}
 
 			}
 		}
 
 		/**
 		 * Build a logical result row.
 		 * <p/>
 		 * At this point, Loader has already processed all non-scalar result data.  We
 		 * just need to account for scalar result data here...
 		 *
 		 * @param data Entity data defined as "root returns" and already handled by the
 		 * normal Loader mechanism.
 		 * @param resultSet The JDBC result set (positioned at the row currently being processed).
 		 * @param hasTransformer Does this query have an associated {@link ResultTransformer}
 		 * @param session The session from which the query request originated.
 		 * @return The logical result row
 		 * @throws SQLException
 		 * @throws HibernateException
 		 */
 		public Object buildResultRow(
 				Object[] data,
 				ResultSet resultSet,
 				boolean hasTransformer,
 				SessionImplementor session) throws SQLException, HibernateException {
 			Object[] resultRow = buildResultRow( data, resultSet, session );
 			return ( hasTransformer )
 			       ? resultRow
 			       : ( resultRow.length == 1 )
 			         ? resultRow[0]
 			         : resultRow;
 		}
 		public Object[] buildResultRow(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			Object[] resultRow;
 			if ( !hasScalars ) {
 				resultRow = data;
 			}
 			else {
 				// build an array with indices equal to the total number
 				// of actual returns in the result Hibernate will return
 				// for this query (scalars + non-scalars)
 				resultRow = new Object[ columnProcessors.length ];
 				for ( int i = 0; i < columnProcessors.length; i++ ) {
 					resultRow[i] = columnProcessors[i].extract( data, resultSet, session );
 				}
 			}
 
 			return resultRow;
 		}
 	}
 
 	private static interface ResultColumnProcessor {
 		public Object extract(Object[] data, ResultSet resultSet, SessionImplementor session) throws SQLException, HibernateException;
 		public void performDiscovery(Metadata metadata, List<Type> types, List<String> aliases) throws SQLException, HibernateException;
 	}
 
 	public class NonScalarResultColumnProcessor implements ResultColumnProcessor {
 		private final int position;
 
 		public NonScalarResultColumnProcessor(int position) {
 			this.position = position;
 		}
 
 		@Override
 		public Object extract(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			return data[ position ];
 		}
 
 		@Override
 		public void performDiscovery(Metadata metadata, List<Type> types, List<String> aliases) {
 		}
 
 	}
 
 	public class ScalarResultColumnProcessor implements ResultColumnProcessor {
 		private int position = -1;
 		private String alias;
 		private Type type;
 
 		public ScalarResultColumnProcessor(int position) {
 			this.position = position;
 		}
 
 		public ScalarResultColumnProcessor(String alias, Type type) {
 			this.alias = alias;
 			this.type = type;
 		}
 
 		@Override
 		public Object extract(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			return type.nullSafeGet( resultSet, alias, session, null );
 		}
 
 		@Override
 		public void performDiscovery(Metadata metadata, List<Type> types, List<String> aliases) throws SQLException {
 			if ( alias == null ) {
 				alias = metadata.getColumnName( position );
 			}
 			else if ( position < 0 ) {
 				position = metadata.resolveColumnPosition( alias );
 			}
 			if ( type == null ) {
 				type = metadata.getHibernateType( position );
 			}
 			types.add( type );
 			aliases.add( alias );
 		}
 	}
 
 	@Override
     protected void autoDiscoverTypes(ResultSet rs) {
 		try {
 			Metadata metadata = new Metadata( getFactory(), rs );
 			rowProcessor.prepareForAutoDiscovery( metadata );
 
 			List<String> aliases = new ArrayList<String>();
 			List<Type> types = new ArrayList<Type>();
 			for ( int i = 0; i < rowProcessor.columnProcessors.length; i++ ) {
 				rowProcessor.columnProcessors[i].performDiscovery( metadata, types, aliases );
 			}
 
 			// lets make sure we did not end up with duplicate aliases.  this can occur when the user supplied query
 			// did not rename same-named columns.  e.g.:
 			//		select u.username, u2.username from t_user u, t_user u2 ...
 			//
 			// the above will lead to an unworkable situation in most cases (the difference is how the driver/db
 			// handle this situation.  But if the 'aliases' variable contains duplicate names, then we have that
 			// troublesome condition, so lets throw an error.  See HHH-5992
 			final HashSet<String> aliasesSet = new HashSet<String>();
 			for ( String alias : aliases ) {
 				boolean alreadyExisted = !aliasesSet.add( alias );
 				if ( alreadyExisted ) {
 					throw new NonUniqueDiscoveredSqlAliasException(
 							"Encountered a duplicated sql alias [" + alias +
 									"] during auto-discovery of a native-sql query"
 					);
 				}
 			}
 
 			resultTypes = ArrayHelper.toTypeArray( types );
 			transformerAliases = ArrayHelper.toStringArray( aliases );
 		}
 		catch ( SQLException e ) {
 			throw new HibernateException( "Exception while trying to autodiscover types.", e );
 		}
 	}
 
 	private static class Metadata {
 		private final SessionFactoryImplementor factory;
 		private final ResultSet resultSet;
 		private final ResultSetMetaData resultSetMetaData;
 
 		public Metadata(SessionFactoryImplementor factory, ResultSet resultSet) throws HibernateException {
 			try {
 				this.factory = factory;
 				this.resultSet = resultSet;
 				this.resultSetMetaData = resultSet.getMetaData();
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not extract result set metadata", e );
 			}
 		}
 
 		public int getColumnCount() throws HibernateException {
 			try {
 				return resultSetMetaData.getColumnCount();
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not determine result set column count", e );
 			}
 		}
 
 		public int resolveColumnPosition(String columnName) throws HibernateException {
 			try {
 				return resultSet.findColumn( columnName );
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not resolve column name in result set [" + columnName + "]", e );
 			}
 		}
 
 		public String getColumnName(int position) throws HibernateException {
 			try {
 				return factory.getDialect().getColumnAliasExtractor().extractColumnAlias( resultSetMetaData, position );
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not resolve column name [" + position + "]", e );
 			}
 		}
 
 		public Type getHibernateType(int columnPos) throws SQLException {
 			int columnType = resultSetMetaData.getColumnType( columnPos );
 			int scale = resultSetMetaData.getScale( columnPos );
 			int precision = resultSetMetaData.getPrecision( columnPos );
             int length = precision;
             if ( columnType == 1 && precision == 0 ) {
                 length = resultSetMetaData.getColumnDisplaySize( columnPos );
             }
 			return factory.getTypeResolver().heuristicType(
 					factory.getDialect().getHibernateTypeName(
 							columnType,
 							length,
 							precision,
 							scale
 					)
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/entity/DynamicBatchingEntityLoaderBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/DynamicBatchingEntityLoaderBuilder.java
index af1fcc26ea..c1be46e16c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/DynamicBatchingEntityLoaderBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/DynamicBatchingEntityLoaderBuilder.java
@@ -1,269 +1,270 @@
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
 package org.hibernate.loader.entity;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
+import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * A BatchingEntityLoaderBuilder that builds UniqueEntityLoader instances capable of dynamically building
  * its batch-fetch SQL based on the actual number of entity ids waiting to be fetched.
  *
  * @author Steve Ebersole
  */
 public class DynamicBatchingEntityLoaderBuilder extends BatchingEntityLoaderBuilder {
 	private static final Logger log = Logger.getLogger( DynamicBatchingEntityLoaderBuilder.class );
 
 	public static final DynamicBatchingEntityLoaderBuilder INSTANCE = new DynamicBatchingEntityLoaderBuilder();
 
 	@Override
 	protected UniqueEntityLoader buildBatchingLoader(
 			OuterJoinLoadable persister,
 			int batchSize,
 			LockMode lockMode,
 			SessionFactoryImplementor factory,
 			LoadQueryInfluencers influencers) {
 		return new DynamicBatchingEntityLoader( persister, batchSize, lockMode, factory, influencers );
 	}
 
 	@Override
 	protected UniqueEntityLoader buildBatchingLoader(
 			OuterJoinLoadable persister,
 			int batchSize,
 			LockOptions lockOptions,
 			SessionFactoryImplementor factory,
 			LoadQueryInfluencers influencers) {
 		return new DynamicBatchingEntityLoader( persister, batchSize, lockOptions, factory, influencers );
 	}
 
 	public static class DynamicBatchingEntityLoader extends BatchingEntityLoader {
 		private final int maxBatchSize;
 		private final UniqueEntityLoader singleKeyLoader;
 		private final DynamicEntityLoader dynamicLoader;
 
 		public DynamicBatchingEntityLoader(
 				OuterJoinLoadable persister,
 				int maxBatchSize,
 				LockMode lockMode,
 				SessionFactoryImplementor factory,
 				LoadQueryInfluencers loadQueryInfluencers) {
 			super( persister );
 			this.maxBatchSize = maxBatchSize;
 			this.singleKeyLoader = new EntityLoader( persister, 1, lockMode, factory, loadQueryInfluencers );
 			this.dynamicLoader = new DynamicEntityLoader( persister, maxBatchSize, lockMode, factory, loadQueryInfluencers );
 		}
 
 		public DynamicBatchingEntityLoader(
 				OuterJoinLoadable persister,
 				int maxBatchSize,
 				LockOptions lockOptions,
 				SessionFactoryImplementor factory,
 				LoadQueryInfluencers loadQueryInfluencers) {
 			super( persister );
 			this.maxBatchSize = maxBatchSize;
 			this.singleKeyLoader = new EntityLoader( persister, 1, lockOptions, factory, loadQueryInfluencers );
 			this.dynamicLoader = new DynamicEntityLoader( persister, maxBatchSize, lockOptions, factory, loadQueryInfluencers );
 		}
 
 		@Override
 		public Object load(
 				Serializable id,
 				Object optionalObject,
 				SessionImplementor session,
 				LockOptions lockOptions) {
 			final Serializable[] batch = session.getPersistenceContext()
 					.getBatchFetchQueue()
 					.getEntityBatch( persister(), id, maxBatchSize, persister().getEntityMode() );
 
 			final int numberOfIds = ArrayHelper.countNonNull( batch );
 			if ( numberOfIds <= 1 ) {
 				return singleKeyLoader.load( id, optionalObject, session );
 			}
 
 			final Serializable[] idsToLoad = new Serializable[numberOfIds];
 			System.arraycopy( batch, 0, idsToLoad, 0, numberOfIds );
 
 			if ( log.isDebugEnabled() ) {
 				log.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister(), idsToLoad, session.getFactory() ) );
 			}
 
 			QueryParameters qp = buildQueryParameters( id, idsToLoad, optionalObject, lockOptions );
 			List results = dynamicLoader.doEntityBatchFetch( session, qp, idsToLoad );
 			return getObjectFromList( results, id, session );
 		}
 	}
 
 
 	private static class DynamicEntityLoader extends EntityLoader {
 		// todo : see the discussion on org.hibernate.loader.collection.DynamicBatchingCollectionInitializerBuilder.DynamicBatchingCollectionLoader
 
 		private final String sqlTemplate;
 		private final String alias;
 
 		public DynamicEntityLoader(
 				OuterJoinLoadable persister,
 				int maxBatchSize,
 				LockOptions lockOptions,
 				SessionFactoryImplementor factory,
 				LoadQueryInfluencers loadQueryInfluencers) {
 			this( persister, maxBatchSize, lockOptions.getLockMode(), factory, loadQueryInfluencers );
 		}
 
 		public DynamicEntityLoader(
 				OuterJoinLoadable persister,
 				int maxBatchSize,
 				LockMode lockMode,
 				SessionFactoryImplementor factory,
 				LoadQueryInfluencers loadQueryInfluencers) {
 			super( persister, -1, lockMode, factory, loadQueryInfluencers );
 
 			EntityJoinWalker walker = new EntityJoinWalker(
 					persister,
 					persister.getIdentifierColumnNames(),
 					-1,
 					lockMode,
 					factory,
 					loadQueryInfluencers
 			) {
 				@Override
 				protected StringBuilder whereString(String alias, String[] columnNames, int batchSize) {
 					return StringHelper.buildBatchFetchRestrictionFragment( alias, columnNames, getFactory().getDialect() );
 				}
 			};
 
 			initFromWalker( walker );
 			this.sqlTemplate = walker.getSQLString();
 			this.alias = walker.getAlias();
 			postInstantiate();
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"SQL-template for dynamic entity [%s] batch-fetching [%s] : %s",
 						entityName,
 						lockMode,
 						sqlTemplate
 				);
 			}
 		}
 
 		@Override
 		protected boolean isSingleRowLoader() {
 			return false;
 		}
 
 		public List doEntityBatchFetch(
 				SessionImplementor session,
 				QueryParameters queryParameters,
 				Serializable[] ids) {
 			final String sql = StringHelper.expandBatchIdPlaceholder(
 					sqlTemplate,
 					ids,
 					alias,
 					persister.getKeyColumnNames(),
 					getFactory().getDialect()
 			);
 
 			try {
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
 				persistenceContext.beforeLoad();
 				List results;
 				try {
 					try {
 						results = doTheLoad( sql, queryParameters, session );
 					}
 					finally {
 						persistenceContext.afterLoad();
 					}
 					persistenceContext.initializeNonLazyCollections();
 					log.debug( "Done batch load" );
 					return results;
 				}
 				finally {
 					// Restore the original default
 					persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw session.getFactory().getSQLExceptionHelper().convert(
 						sqle,
 						"could not load an entity batch: " + MessageHelper.infoString(
 								getEntityPersisters()[0],
 								ids,
 								session.getFactory()
 						),
 						sql
 				);
 			}
 		}
 
 		private List doTheLoad(String sql, QueryParameters queryParameters, SessionImplementor session) throws SQLException {
 			final RowSelection selection = queryParameters.getRowSelection();
 			final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 					selection.getMaxRows() :
 					Integer.MAX_VALUE;
 
 			final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 			final SqlStatementWrapper wrapper = executeQueryStatement( sql, queryParameters, false, afterLoadActions, session );
 			final ResultSet rs = wrapper.getResultSet();
 			final Statement st = wrapper.getStatement();
 			try {
 				return processResultSet( rs, queryParameters, session, false, null, maxRows, afterLoadActions );
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
index c50eed3458..3a45e39d53 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,638 +1,639 @@
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
+import org.hibernate.loader.spi.AfterLoadAction;
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
 	public String getSQLString() {
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
 		return aggregatedSelectExpression != null &&  aggregatedSelectExpression.getResultTransformer() != null;
 	}
 
 	protected String[] getResultRowAliases() {
 		return queryReturnAliases;
 	}
 	
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/OnDemandResultSetProcessorImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/OnDemandResultSetProcessorImpl.java
new file mode 100644
index 0000000000..8fc816c9e6
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/OnDemandResultSetProcessorImpl.java
@@ -0,0 +1,58 @@
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
+
+import java.sql.ResultSet;
+
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.loader.spi.OnDemandResultSetProcessor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class OnDemandResultSetProcessorImpl implements OnDemandResultSetProcessor {
+	@Override
+	public Object extractSingleRow(
+			ResultSet resultSet,
+			SessionImplementor session,
+			QueryParameters queryParameters) {
+		return null;
+	}
+
+	@Override
+	public Object extractSequentialRowsForward(
+			ResultSet resultSet, SessionImplementor session, QueryParameters queryParameters) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public Object extractSequentialRowsReverse(
+			ResultSet resultSet,
+			SessionImplementor session,
+			QueryParameters queryParameters,
+			boolean isLogicallyAfterLast) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
new file mode 100644
index 0000000000..2e390c980b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
@@ -0,0 +1,557 @@
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
+
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.StaleObjectStateException;
+import org.hibernate.WrongClassException;
+import org.hibernate.engine.internal.TwoPhaseLoad;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.EntityUniqueKey;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.SubselectFetch;
+import org.hibernate.event.spi.EventSource;
+import org.hibernate.event.spi.PostLoadEvent;
+import org.hibernate.event.spi.PreLoadEvent;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.plan.spi.CollectionFetch;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.LoadPlanVisitationStrategyAdapter;
+import org.hibernate.loader.plan.spi.LoadPlanVisitor;
+import org.hibernate.loader.spi.AfterLoadAction;
+import org.hibernate.loader.spi.NamedParameterContext;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Loadable;
+import org.hibernate.persister.entity.UniqueKeyLoadable;
+import org.hibernate.pretty.MessageHelper;
+import org.hibernate.type.EntityType;
+import org.hibernate.type.Type;
+import org.hibernate.type.VersionType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ResultSetProcessingContextImpl implements ResultSetProcessingContext {
+	private static final Logger LOG = Logger.getLogger( ResultSetProcessingContextImpl.class );
+
+	private final ResultSet resultSet;
+	private final SessionImplementor session;
+	private final LoadPlan loadPlan;
+	private final boolean readOnly;
+	private final QueryParameters queryParameters;
+	private final NamedParameterContext namedParameterContext;
+	private final boolean hadSubselectFetches;
+
+	private final EntityKey dictatedRootEntityKey;
+
+	private List<HydratedEntityRegistration> currentRowHydratedEntityRegistrationList;
+
+	private Map<EntityPersister,Set<EntityKey>> subselectLoadableEntityKeyMap;
+	private List<HydratedEntityRegistration> hydratedEntityRegistrationList;
+
+	public ResultSetProcessingContextImpl(
+			ResultSet resultSet,
+			SessionImplementor session,
+			LoadPlan loadPlan,
+			boolean readOnly,
+			boolean useOptionalEntityKey,
+			QueryParameters queryParameters,
+			NamedParameterContext namedParameterContext,
+			boolean hadSubselectFetches) {
+		this.resultSet = resultSet;
+		this.session = session;
+		this.loadPlan = loadPlan;
+		this.readOnly = readOnly;
+		this.queryParameters = queryParameters;
+		this.namedParameterContext = namedParameterContext;
+		this.hadSubselectFetches = hadSubselectFetches;
+
+		if ( useOptionalEntityKey ) {
+			this.dictatedRootEntityKey = ResultSetProcessorHelper.getOptionalObjectKey( queryParameters, session );
+			if ( this.dictatedRootEntityKey == null ) {
+				throw new HibernateException( "Unable to resolve optional entity-key" );
+			}
+		}
+		else {
+			this.dictatedRootEntityKey = null;
+		}
+	}
+
+	@Override
+	public SessionImplementor getSession() {
+		return session;
+	}
+
+	@Override
+	public QueryParameters getQueryParameters() {
+		return queryParameters;
+	}
+
+	@Override
+	public EntityKey getDictatedRootEntityKey() {
+		return dictatedRootEntityKey;
+	}
+
+	private Map<EntityReference,IdentifierResolutionContext> identifierResolutionContextMap;
+
+	@Override
+	public IdentifierResolutionContext getIdentifierResolutionContext(final EntityReference entityReference) {
+		if ( identifierResolutionContextMap == null ) {
+			identifierResolutionContextMap = new HashMap<EntityReference, IdentifierResolutionContext>();
+		}
+		IdentifierResolutionContext context = identifierResolutionContextMap.get( entityReference );
+		if ( context == null ) {
+			context = new IdentifierResolutionContext() {
+				private Serializable hydratedForm;
+				private EntityKey entityKey;
+
+				@Override
+				public EntityReference getEntityReference() {
+					return entityReference;
+				}
+
+				@Override
+				public void registerHydratedForm(Serializable hydratedForm) {
+					if ( this.hydratedForm != null ) {
+						// this could be bad...
+					}
+					this.hydratedForm = hydratedForm;
+				}
+
+				@Override
+				public Serializable getHydratedForm() {
+					return hydratedForm;
+				}
+
+				@Override
+				public void registerEntityKey(EntityKey entityKey) {
+					if ( this.entityKey != null ) {
+						// again, could be trouble...
+					}
+					this.entityKey = entityKey;
+				}
+
+				@Override
+				public EntityKey getEntityKey() {
+					return entityKey;
+				}
+			};
+			identifierResolutionContextMap.put( entityReference, context );
+		}
+
+		return context;
+	}
+
+	@Override
+	public void checkVersion(
+			ResultSet resultSet,
+			EntityPersister persister,
+			EntityAliases entityAliases,
+			EntityKey entityKey,
+			Object entityInstance) throws SQLException {
+		final Object version = session.getPersistenceContext().getEntry( entityInstance ).getVersion();
+
+		if ( version != null ) {
+			//null version means the object is in the process of being loaded somewhere else in the ResultSet
+			VersionType versionType = persister.getVersionType();
+			Object currentVersion = versionType.nullSafeGet(
+					resultSet,
+					entityAliases.getSuffixedVersionAliases(),
+					session,
+					null
+			);
+			if ( !versionType.isEqual(version, currentVersion) ) {
+				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
+					session.getFactory().getStatisticsImplementor()
+							.optimisticFailure( persister.getEntityName() );
+				}
+				throw new StaleObjectStateException( persister.getEntityName(), entityKey.getIdentifier() );
+			}
+		}
+	}
+
+	@Override
+	public String getConcreteEntityTypeName(
+			final ResultSet rs,
+			final EntityPersister persister,
+			final EntityAliases entityAliases,
+			final EntityKey entityKey) throws SQLException {
+
+		final Loadable loadable = (Loadable) persister;
+		if ( ! loadable.hasSubclasses() ) {
+			return persister.getEntityName();
+		}
+
+		final Object discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(
+				rs,
+				entityAliases.getSuffixedDiscriminatorAlias(),
+				session,
+				null
+		);
+
+		final String result = loadable.getSubclassForDiscriminatorValue( discriminatorValue );
+
+		if ( result == null ) {
+			// whoops! we got an instance of another class hierarchy branch
+			throw new WrongClassException(
+					"Discriminator: " + discriminatorValue,
+					entityKey.getIdentifier(),
+					persister.getEntityName()
+			);
+		}
+
+		return result;
+	}
+
+	@Override
+	public void loadFromResultSet(
+			ResultSet resultSet,
+			Object entityInstance,
+			String concreteEntityTypeName,
+			EntityKey entityKey,
+			EntityAliases entityAliases,
+			LockMode acquiredLockMode,
+			EntityPersister rootPersister,
+			boolean eagerFetch,
+			EntityType associationType) throws SQLException {
+
+		final Serializable id = entityKey.getIdentifier();
+
+		// Get the persister for the _subclass_
+		final Loadable persister = (Loadable) getSession().getFactory().getEntityPersister( concreteEntityTypeName );
+
+		if ( LOG.isTraceEnabled() ) {
+			LOG.tracev(
+					"Initializing object from ResultSet: {0}",
+					MessageHelper.infoString(
+							persister,
+							id,
+							getSession().getFactory()
+					)
+			);
+		}
+
+		// add temp entry so that the next step is circular-reference
+		// safe - only needed because some types don't take proper
+		// advantage of two-phase-load (esp. components)
+		TwoPhaseLoad.addUninitializedEntity(
+				entityKey,
+				entityInstance,
+				persister,
+				acquiredLockMode,
+				!eagerFetch,
+				session
+		);
+
+		// This is not very nice (and quite slow):
+		final String[][] cols = persister == rootPersister ?
+				entityAliases.getSuffixedPropertyAliases() :
+				entityAliases.getSuffixedPropertyAliases(persister);
+
+		final Object[] values = persister.hydrate(
+				resultSet,
+				id,
+				entityInstance,
+				(Loadable) rootPersister,
+				cols,
+				eagerFetch,
+				session
+		);
+
+		final Object rowId = persister.hasRowId() ? resultSet.getObject( entityAliases.getRowIdAlias() ) : null;
+
+		if ( associationType != null ) {
+			String ukName = associationType.getRHSUniqueKeyPropertyName();
+			if ( ukName != null ) {
+				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex( ukName );
+				final Type type = persister.getPropertyTypes()[index];
+
+				// polymorphism not really handled completely correctly,
+				// perhaps...well, actually its ok, assuming that the
+				// entity name used in the lookup is the same as the
+				// the one used here, which it will be
+
+				EntityUniqueKey euk = new EntityUniqueKey(
+						rootPersister.getEntityName(), //polymorphism comment above
+						ukName,
+						type.semiResolve( values[index], session, entityInstance ),
+						type,
+						persister.getEntityMode(),
+						session.getFactory()
+				);
+				session.getPersistenceContext().addEntity( euk, entityInstance );
+			}
+		}
+
+		TwoPhaseLoad.postHydrate(
+				persister,
+				id,
+				values,
+				rowId,
+				entityInstance,
+				acquiredLockMode,
+				!eagerFetch,
+				session
+		);
+
+	}
+
+	@Override
+	public void registerHydratedEntity(EntityPersister persister, EntityKey entityKey, Object entityInstance) {
+		if ( currentRowHydratedEntityRegistrationList == null ) {
+			currentRowHydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
+		}
+		currentRowHydratedEntityRegistrationList.add( new HydratedEntityRegistration( persister, entityKey, entityInstance ) );
+	}
+
+	/**
+	 * Package-protected
+	 */
+	void finishUpRow() {
+		if ( currentRowHydratedEntityRegistrationList == null ) {
+			return;
+		}
+
+
+		// managing the running list of registrations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		if ( hydratedEntityRegistrationList == null ) {
+			hydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
+		}
+		hydratedEntityRegistrationList.addAll( currentRowHydratedEntityRegistrationList );
+
+
+		// managing the map forms needed for subselect fetch generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		if ( ! hadSubselectFetches ) {
+			return;
+		}
+		if ( subselectLoadableEntityKeyMap == null ) {
+			subselectLoadableEntityKeyMap = new HashMap<EntityPersister, Set<EntityKey>>();
+		}
+		for ( HydratedEntityRegistration registration : currentRowHydratedEntityRegistrationList ) {
+			Set<EntityKey> entityKeys = subselectLoadableEntityKeyMap.get( registration.persister );
+			if ( entityKeys == null ) {
+				entityKeys = new HashSet<EntityKey>();
+				subselectLoadableEntityKeyMap.put( registration.persister, entityKeys );
+			}
+			entityKeys.add( registration.key );
+		}
+
+		// release the currentRowHydratedEntityRegistrationList entries
+		currentRowHydratedEntityRegistrationList.clear();
+	}
+
+	/**
+	 * Package-protected
+	 *
+	 * @param afterLoadActionList List of after-load actions to perform
+	 */
+	void finishUp(List<AfterLoadAction> afterLoadActionList) {
+		initializeEntitiesAndCollections( afterLoadActionList );
+		createSubselects();
+
+		if ( hydratedEntityRegistrationList != null ) {
+			hydratedEntityRegistrationList.clear();
+			hydratedEntityRegistrationList = null;
+		}
+
+		if ( subselectLoadableEntityKeyMap != null ) {
+			subselectLoadableEntityKeyMap.clear();
+			subselectLoadableEntityKeyMap = null;
+		}
+	}
+
+	private void initializeEntitiesAndCollections(List<AfterLoadAction> afterLoadActionList) {
+		// for arrays, we should end the collection load before resolving the entities, since the
+		// actual array instances are not instantiated during loading
+		finishLoadingArrays();
+
+
+		// IMPORTANT: reuse the same event instances for performance!
+		final PreLoadEvent preLoadEvent;
+		final PostLoadEvent postLoadEvent;
+		if ( session.isEventSource() ) {
+			preLoadEvent = new PreLoadEvent( (EventSource) session );
+			postLoadEvent = new PostLoadEvent( (EventSource) session );
+		}
+		else {
+			preLoadEvent = null;
+			postLoadEvent = null;
+		}
+
+		// now finish loading the entities (2-phase load)
+		performTwoPhaseLoad( preLoadEvent, postLoadEvent );
+
+		// now we can finalize loading collections
+		finishLoadingCollections();
+
+		// finally, perform post-load operations
+		postLoad( postLoadEvent, afterLoadActionList );
+	}
+
+	private void finishLoadingArrays() {
+		LoadPlanVisitor.visit(
+				loadPlan,
+				new LoadPlanVisitationStrategyAdapter() {
+					@Override
+					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
+						endLoadingArray( rootCollectionReturn.getCollectionPersister() );
+					}
+
+					@Override
+					public void startingCollectionFetch(CollectionFetch collectionFetch) {
+						endLoadingArray( collectionFetch.getCollectionPersister() );
+					}
+
+					private void endLoadingArray(CollectionPersister persister) {
+						if ( persister.isArray() ) {
+							session.getPersistenceContext()
+									.getLoadContexts()
+									.getCollectionLoadContext( resultSet )
+									.endLoadingCollections( persister );
+						}
+					}
+				}
+		);
+	}
+
+	private void performTwoPhaseLoad(PreLoadEvent preLoadEvent, PostLoadEvent postLoadEvent) {
+		final int numberOfHydratedObjects = hydratedEntityRegistrationList == null
+				? 0
+				: hydratedEntityRegistrationList.size();
+		LOG.tracev( "Total objects hydrated: {0}", numberOfHydratedObjects );
+
+		if ( hydratedEntityRegistrationList == null ) {
+			return;
+		}
+
+		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
+			TwoPhaseLoad.initializeEntity( registration.instance, readOnly, session, preLoadEvent, postLoadEvent );
+		}
+	}
+
+	private void finishLoadingCollections() {
+		LoadPlanVisitor.visit(
+				loadPlan,
+				new LoadPlanVisitationStrategyAdapter() {
+					@Override
+					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
+						endLoadingArray( rootCollectionReturn.getCollectionPersister() );
+					}
+
+					@Override
+					public void startingCollectionFetch(CollectionFetch collectionFetch) {
+						endLoadingArray( collectionFetch.getCollectionPersister() );
+					}
+
+					private void endLoadingArray(CollectionPersister persister) {
+						if ( ! persister.isArray() ) {
+							session.getPersistenceContext()
+									.getLoadContexts()
+									.getCollectionLoadContext( resultSet )
+									.endLoadingCollections( persister );
+						}
+					}
+				}
+		);
+	}
+
+	private void postLoad(PostLoadEvent postLoadEvent, List<AfterLoadAction> afterLoadActionList) {
+		// Until this entire method is refactored w/ polymorphism, postLoad was
+		// split off from initializeEntity.  It *must* occur after
+		// endCollectionLoad to ensure the collection is in the
+		// persistence context.
+		if ( hydratedEntityRegistrationList == null ) {
+			return;
+		}
+
+		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
+			TwoPhaseLoad.postLoad( registration.instance, session, postLoadEvent );
+			if ( afterLoadActionList != null ) {
+				for ( AfterLoadAction afterLoadAction : afterLoadActionList ) {
+					afterLoadAction.afterLoad( session, registration.instance, (Loadable) registration.persister );
+				}
+			}
+		}
+	}
+
+	private void createSubselects() {
+		if ( subselectLoadableEntityKeyMap.size() <= 1 ) {
+			// if we only returned one entity, query by key is more efficient; so do nothing here
+			return;
+		}
+
+		final Map<String, int[]> namedParameterLocMap =
+				ResultSetProcessorHelper.buildNamedParameterLocMap( queryParameters, namedParameterContext );
+
+		for ( Map.Entry<EntityPersister, Set<EntityKey>> entry : subselectLoadableEntityKeyMap.entrySet() ) {
+			if ( ! entry.getKey().hasSubselectLoadableCollections() ) {
+				continue;
+			}
+
+			SubselectFetch subselectFetch = new SubselectFetch(
+					//getSQLString(),
+					null, // aliases[i],
+					(Loadable) entry.getKey(),
+					queryParameters,
+					entry.getValue(),
+					namedParameterLocMap
+			);
+
+			for ( EntityKey key : entry.getValue() ) {
+				session.getPersistenceContext().getBatchFetchQueue().addSubselect( key, subselectFetch );
+			}
+
+		}
+	}
+
+	private static class HydratedEntityRegistration {
+		private final EntityPersister persister;
+		private final EntityKey key;
+		private final Object instance;
+
+		private HydratedEntityRegistration(EntityPersister persister, EntityKey key, Object instance) {
+			this.persister = persister;
+			this.key = key;
+			this.instance = instance;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorHelper.java
new file mode 100644
index 0000000000..8401218694
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorHelper.java
@@ -0,0 +1,77 @@
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
+
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Fetch;
+import org.hibernate.loader.spi.NamedParameterContext;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ResultSetProcessorHelper {
+	public static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
+		final Object optionalObject = queryParameters.getOptionalObject();
+		final Serializable optionalId = queryParameters.getOptionalId();
+		final String optionalEntityName = queryParameters.getOptionalEntityName();
+
+		if ( optionalObject != null && optionalEntityName != null ) {
+			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
+		}
+		else {
+			return null;
+		}
+	}
+
+	public static Map<String, int[]> buildNamedParameterLocMap(
+			QueryParameters queryParameters,
+			NamedParameterContext namedParameterContext) {
+		if ( queryParameters.getNamedParameters() == null || queryParameters.getNamedParameters().isEmpty() ) {
+			return null;
+		}
+
+		final Map<String, int[]> namedParameterLocMap = new HashMap<String, int[]>();
+		for ( String name : queryParameters.getNamedParameters().keySet() ) {
+			namedParameterLocMap.put(
+					name,
+					namedParameterContext.getNamedParameterLocations( name )
+			);
+		}
+		return namedParameterLocMap;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java
new file mode 100644
index 0000000000..d31278e5f5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessorImpl.java
@@ -0,0 +1,207 @@
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
+
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.dialect.pagination.LimitHelper;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.loader.plan.spi.CollectionFetch;
+import org.hibernate.loader.plan.spi.CollectionReturn;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.LoadPlanVisitationStrategyAdapter;
+import org.hibernate.loader.plan.spi.LoadPlanVisitor;
+import org.hibernate.loader.plan.spi.Return;
+import org.hibernate.loader.spi.AfterLoadAction;
+import org.hibernate.loader.spi.NamedParameterContext;
+import org.hibernate.loader.spi.OnDemandResultSetProcessor;
+import org.hibernate.loader.spi.ResultSetProcessor;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.pretty.MessageHelper;
+import org.hibernate.transform.ResultTransformer;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ResultSetProcessorImpl implements ResultSetProcessor {
+	private static final Logger LOG = Logger.getLogger( ResultSetProcessorImpl.class );
+
+	private final LoadPlan loadPlan;
+
+	private final boolean hadSubselectFetches;
+
+	public ResultSetProcessorImpl(LoadPlan loadPlan) {
+		this.loadPlan = loadPlan;
+
+		LocalVisitationStrategy strategy = new LocalVisitationStrategy();
+		LoadPlanVisitor.visit( loadPlan, strategy );
+		this.hadSubselectFetches = strategy.hadSubselectFetches;
+	}
+
+	@Override
+	public OnDemandResultSetProcessor toOnDemandForm() {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public List extractResults(
+			ResultSet resultSet,
+			final SessionImplementor session,
+			QueryParameters queryParameters,
+			NamedParameterContext namedParameterContext,
+			boolean returnProxies,
+			boolean readOnly,
+			ResultTransformer forcedResultTransformer,
+			List<AfterLoadAction> afterLoadActionList) throws SQLException {
+
+		handlePotentiallyEmptyCollectionRootReturns( queryParameters.getCollectionKeys(), resultSet, session );
+
+		final int maxRows;
+		final RowSelection selection = queryParameters.getRowSelection();
+		if ( LimitHelper.hasMaxRows( selection ) ) {
+			maxRows = selection.getMaxRows();
+			LOG.tracef( "Limiting ResultSet processing to just %s rows", maxRows );
+		}
+		else {
+			maxRows = Integer.MAX_VALUE;
+		}
+
+		final ResultSetProcessingContextImpl context = new ResultSetProcessingContextImpl(
+				resultSet,
+				session,
+				loadPlan,
+				readOnly,
+				true, // use optional entity key?  for now, always say yes
+				queryParameters,
+				namedParameterContext,
+				hadSubselectFetches
+		);
+
+		final List loadResults = new ArrayList();
+
+		final int rootReturnCount = loadPlan.getReturns().size();
+
+		LOG.trace( "Processing result set" );
+		int count;
+		for ( count = 0; count < maxRows && resultSet.next(); count++ ) {
+			LOG.debugf( "Starting ResultSet row #%s", count );
+
+			Object logicalRow;
+			if ( rootReturnCount == 1 ) {
+				loadPlan.getReturns().get( 0 ).hydrate( resultSet, context );
+				loadPlan.getReturns().get( 0 ).resolve( resultSet, context );
+
+				logicalRow = loadPlan.getReturns().get( 0 ).read( resultSet, context );
+			}
+			else {
+				for ( Return rootReturn : loadPlan.getReturns() ) {
+					rootReturn.hydrate( resultSet, context );
+				}
+				for ( Return rootReturn : loadPlan.getReturns() ) {
+					rootReturn.resolve( resultSet, context );
+				}
+
+				logicalRow = new Object[ rootReturnCount ];
+				int pos = 0;
+				for ( Return rootReturn : loadPlan.getReturns() ) {
+					( (Object[]) logicalRow )[pos] = rootReturn.read( resultSet, context );
+					pos++;
+				}
+			}
+
+			// todo : apply transformers here?
+
+			loadResults.add( logicalRow );
+
+			context.finishUpRow();
+		}
+
+		LOG.tracev( "Done processing result set ({0} rows)", count );
+
+		context.finishUp( afterLoadActionList );
+
+		session.getPersistenceContext().initializeNonLazyCollections();
+
+		return loadResults;
+	}
+
+
+	private void handlePotentiallyEmptyCollectionRootReturns(
+			Serializable[] collectionKeys,
+			ResultSet resultSet,
+			SessionImplementor session) {
+		if ( collectionKeys == null ) {
+			// this is not a collection initializer (and empty collections will be detected by looking for
+			// the owner's identifier in the result set)
+			return;
+		}
+
+		// this is a collection initializer, so we must create a collection
+		// for each of the passed-in keys, to account for the possibility
+		// that the collection is empty and has no rows in the result set
+		//
+		// todo : move this inside CollectionReturn ?
+		CollectionPersister persister = ( (CollectionReturn) loadPlan.getReturns().get( 0 ) ).getCollectionPersister();
+		for ( Serializable key : collectionKeys ) {
+			if ( LOG.isDebugEnabled() ) {
+				LOG.debugf(
+						"Preparing collection intializer : %s",
+							MessageHelper.collectionInfoString( persister, key, session.getFactory() )
+				);
+				session.getPersistenceContext()
+						.getLoadContexts()
+						.getCollectionLoadContext( resultSet )
+						.getLoadingCollection( persister, key );
+			}
+		}
+	}
+
+
+	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
+		private boolean hadSubselectFetches = false;
+
+		@Override
+		public void startingEntityFetch(EntityFetch entityFetch) {
+// only collections are currently supported for subselect fetching.
+//			hadSubselectFetches = hadSubselectFetches
+//					| entityFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
+		}
+
+		@Override
+		public void startingCollectionFetch(CollectionFetch collectionFetch) {
+			hadSubselectFetches = hadSubselectFetches
+					| collectionFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
new file mode 100644
index 0000000000..3c617da984
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanBuildingHelper.java
@@ -0,0 +1,92 @@
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
+package org.hibernate.loader.plan.internal;
+
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.plan.spi.AbstractFetchOwner;
+import org.hibernate.loader.plan.spi.CollectionFetch;
+import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.spi.EntityFetch;
+import org.hibernate.loader.plan.spi.FetchOwner;
+import org.hibernate.loader.plan.spi.LoadPlanBuildingContext;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class LoadPlanBuildingHelper {
+	public static CollectionFetch buildStandardCollectionFetch(
+			FetchOwner fetchOwner,
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		final CollectionAliases collectionAliases = loadPlanBuildingContext.resolveCollectionColumnAliases( attributeDefinition );
+		final EntityAliases elementEntityAliases = loadPlanBuildingContext.resolveEntityColumnAliases( attributeDefinition );
+
+		return new CollectionFetch(
+				loadPlanBuildingContext.getSessionFactory(),
+				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
+				LockMode.NONE, // todo : for now
+				fetchOwner,
+				fetchStrategy,
+				attributeDefinition.getName(),
+				collectionAliases,
+				elementEntityAliases
+		);
+	}
+
+	public static EntityFetch buildStandardEntityFetch(
+			FetchOwner fetchOwner,
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+
+		return new EntityFetch(
+				loadPlanBuildingContext.getSessionFactory(),
+				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
+				LockMode.NONE, // todo : for now
+				fetchOwner,
+				attributeDefinition.getName(),
+				fetchStrategy,
+				null, // sql table alias
+				loadPlanBuildingContext.resolveEntityColumnAliases( attributeDefinition )
+		);
+	}
+
+	public static CompositeFetch buildStandardCompositeFetch(
+			FetchOwner fetchOwner,
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return new CompositeFetch(
+				loadPlanBuildingContext.getSessionFactory(),
+				loadPlanBuildingContext.resolveFetchSourceAlias( attributeDefinition ),
+				(AbstractFetchOwner) fetchOwner,
+				attributeDefinition.getName()
+		);
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
index f329b30e26..fd710aa7f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/SingleRootReturnLoadPlanBuilderStrategy.java
@@ -1,268 +1,194 @@
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
 package org.hibernate.loader.plan.internal;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.DefaultEntityAliases;
 import org.hibernate.loader.EntityAliases;
-import org.hibernate.loader.GeneratedCollectionAliases;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.loader.plan.spi.AbstractFetchOwner;
 import org.hibernate.loader.plan.spi.AbstractLoadPlanBuilderStrategy;
-import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReturn;
-import org.hibernate.loader.plan.spi.CompositeFetch;
-import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReturn;
-import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.LoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.entity.Loadable;
+import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
-import org.hibernate.persister.walking.spi.CompositeDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * LoadPlanBuilderStrategy implementation used for building LoadPlans with a single processing RootEntity LoadPlan building.
  *
  * Really this is a single-root LoadPlan building strategy for building LoadPlans for:<ul>
  *     <li>entity load plans</li>
  *     <li>cascade load plans</li>
  *     <li>collection initializer plans</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public class SingleRootReturnLoadPlanBuilderStrategy
 		extends AbstractLoadPlanBuilderStrategy
 		implements LoadPlanBuilderStrategy {
 
 	private final LoadQueryInfluencers loadQueryInfluencers;
 
 	private final String rootAlias;
-	private int currentSuffixBase;
 
 	private Return rootReturn;
 
 	private PropertyPath propertyPath = new PropertyPath( "" );
 
 	public SingleRootReturnLoadPlanBuilderStrategy(
 			SessionFactoryImplementor sessionFactory,
 			LoadQueryInfluencers loadQueryInfluencers,
 			String rootAlias,
 			int suffixSeed) {
-		super( sessionFactory );
+		super( sessionFactory, suffixSeed );
 		this.loadQueryInfluencers = loadQueryInfluencers;
 		this.rootAlias = rootAlias;
-		this.currentSuffixBase = suffixSeed;
 	}
 
 	@Override
 	protected boolean supportsRootEntityReturns() {
 		return true;
 	}
 
 	@Override
 	protected boolean supportsRootCollectionReturns() {
 		return true;
 	}
 
 	@Override
 	protected void addRootReturn(Return rootReturn) {
 		if ( this.rootReturn != null ) {
 			throw new HibernateException( "Root return already identified" );
 		}
 		this.rootReturn = rootReturn;
 	}
 
 	@Override
 	public LoadPlan buildLoadPlan() {
 		return new LoadPlanImpl( false, rootReturn );
 	}
 
 	@Override
 	protected FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition) {
 		FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan( loadQueryInfluencers, propertyPath );
 		if ( fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 			// see if we need to alter the join fetch to another form for any reason
 			fetchStrategy = adjustJoinFetchIfNeeded( attributeDefinition, fetchStrategy );
 		}
 		return fetchStrategy;
 	}
 
 	protected FetchStrategy adjustJoinFetchIfNeeded(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy) {
 		if ( currentDepth() > sessionFactory().getSettings().getMaximumFetchDepth() ) {
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		if ( attributeDefinition.getType().isCollectionType() && isTooManyCollections() ) {
 			// todo : have this revert to batch or subselect fetching once "sql gen redesign" is in place
 			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
 		}
 
 		return fetchStrategy;
 	}
 
 	@Override
 	protected boolean isTooManyCollections() {
 		return false;
 	}
 
 	@Override
 	protected EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition) {
 		final String entityName = entityDefinition.getEntityPersister().getEntityName();
 		return new EntityReturn(
 				sessionFactory(),
 				rootAlias,
 				LockMode.NONE, // todo : for now
 				entityName,
 				StringHelper.generateAlias( StringHelper.unqualifyEntityName( entityName ), currentDepth() ),
-				new DefaultEntityAliases(
-						(Loadable) entityDefinition.getEntityPersister(),
-						Integer.toString( currentSuffixBase++ ) + '_'
-				)
+				generateEntityColumnAliases( entityDefinition.getEntityPersister() )
 		);
 	}
 
 	@Override
 	protected CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition) {
 		final CollectionPersister persister = collectionDefinition.getCollectionPersister();
 		final String collectionRole = persister.getRole();
 
-		final CollectionAliases collectionAliases = new GeneratedCollectionAliases(
-				collectionDefinition.getCollectionPersister(),
-				Integer.toString( currentSuffixBase++ ) + '_'
+		final CollectionAliases collectionAliases = generateCollectionColumnAliases(
+				collectionDefinition.getCollectionPersister()
 		);
+
 		final Type elementType = collectionDefinition.getCollectionPersister().getElementType();
 		final EntityAliases elementAliases;
 		if ( elementType.isEntityType() ) {
 			final EntityType entityElementType = (EntityType) elementType;
-			elementAliases = new DefaultEntityAliases(
-					(Loadable) entityElementType.getAssociatedJoinable( sessionFactory() ),
-					Integer.toString( currentSuffixBase++ ) + '_'
+			elementAliases = generateEntityColumnAliases(
+					(EntityPersister) entityElementType.getAssociatedJoinable( sessionFactory() )
 			);
 		}
 		else {
 			elementAliases = null;
 		}
 
 		return new CollectionReturn(
 				sessionFactory(),
 				rootAlias,
 				LockMode.NONE, // todo : for now
 				persister.getOwnerEntityPersister().getEntityName(),
 				StringHelper.unqualify( collectionRole ),
 				collectionAliases,
 				elementAliases
 		);
 	}
 
-	@Override
-	protected CollectionFetch buildCollectionFetch(
-			FetchOwner fetchOwner,
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy) {
-		final CollectionDefinition collectionDefinition = attributeDefinition.toCollectionDefinition();
-		final CollectionAliases collectionAliases = new GeneratedCollectionAliases(
-				collectionDefinition.getCollectionPersister(),
-				Integer.toString( currentSuffixBase++ ) + '_'
-		);
-		final Type elementType = collectionDefinition.getCollectionPersister().getElementType();
-		final EntityAliases elementAliases;
-		if ( elementType.isEntityType() ) {
-			final EntityType entityElementType = (EntityType) elementType;
-			elementAliases = new DefaultEntityAliases(
-					(Loadable) entityElementType.getAssociatedJoinable( sessionFactory() ),
-					Integer.toString( currentSuffixBase++ ) + '_'
-			);
-		}
-		else {
-			elementAliases = null;
-		}
 
-		return new CollectionFetch(
-				sessionFactory(),
-				createImplicitAlias(),
-				LockMode.NONE, // todo : for now
-				(AbstractFetchOwner) fetchOwner,
-				fetchStrategy,
-				attributeDefinition.getName(),
-				collectionAliases,
-				elementAliases
-		);
-	}
+	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
-	protected EntityFetch buildEntityFetch(
-			FetchOwner fetchOwner,
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy) {
-		final EntityDefinition entityDefinition = attributeDefinition.toEntityDefinition();
-
-		return new EntityFetch(
-				sessionFactory(),
-				createImplicitAlias(),
-				LockMode.NONE, // todo : for now
-				(AbstractFetchOwner) fetchOwner,
-				attributeDefinition.getName(),
-				fetchStrategy,
-				StringHelper.generateAlias( entityDefinition.getEntityPersister().getEntityName(), currentDepth() ),
-				new DefaultEntityAliases(
-						(Loadable) entityDefinition.getEntityPersister(),
-						Integer.toString( currentSuffixBase++ ) + '_'
-				)
-		);
+	public String resolveRootSourceAlias(EntityDefinition definition) {
+		return rootAlias;
 	}
 
 	@Override
-	protected CompositeFetch buildCompositeFetch(FetchOwner fetchOwner, CompositeDefinition attributeDefinition) {
-		return new CompositeFetch(
-				sessionFactory(),
-				createImplicitAlias(),
-				(AbstractFetchOwner) fetchOwner,
-				attributeDefinition.getName()
-		);
-	}
-
-	private int implicitAliasUniqueness = 0;
-
-	private String createImplicitAlias() {
-		return "ia" + implicitAliasUniqueness++;
+	public String resolveRootSourceAlias(CollectionDefinition definition) {
+		return rootAlias;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractCollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractCollectionReference.java
new file mode 100644
index 0000000000..e623f0a898
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractCollectionReference.java
@@ -0,0 +1,143 @@
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
+package org.hibernate.loader.plan.spi;
+
+import org.hibernate.LockMode;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractCollectionReference extends AbstractPlanNode implements CollectionReference {
+	private final String alias;
+	private final LockMode lockMode;
+	private final CollectionPersister collectionPersister;
+	private final PropertyPath propertyPath;
+
+	private final CollectionAliases collectionAliases;
+	private final EntityAliases elementEntityAliases;
+
+	private final FetchOwner indexGraph;
+	private final FetchOwner elementGraph;
+
+	protected AbstractCollectionReference(
+			SessionFactoryImplementor sessionFactory,
+			String alias,
+			LockMode lockMode,
+			CollectionPersister collectionPersister,
+			PropertyPath propertyPath,
+			CollectionAliases collectionAliases,
+			EntityAliases elementEntityAliases) {
+		super( sessionFactory );
+		this.alias = alias;
+		this.lockMode = lockMode;
+		this.collectionPersister = collectionPersister;
+		this.propertyPath = propertyPath;
+
+		this.collectionAliases = collectionAliases;
+		this.elementEntityAliases = elementEntityAliases;
+
+		this.indexGraph = buildIndexGraph( getCollectionPersister() );
+		this.elementGraph = buildElementGraph( getCollectionPersister() );
+	}
+
+	private FetchOwner buildIndexGraph(CollectionPersister persister) {
+		if ( persister.hasIndex() ) {
+			final Type type = persister.getIndexType();
+			if ( type.isAssociationType() ) {
+				if ( type.isEntityType() ) {
+					return new EntityIndexGraph( sessionFactory(), this, propertyPath() );
+				}
+			}
+			else if ( type.isComponentType() ) {
+				return new CompositeIndexGraph( sessionFactory(), this, propertyPath() );
+			}
+		}
+
+		return null;
+	}
+
+	private FetchOwner buildElementGraph(CollectionPersister persister) {
+		final Type type = persister.getElementType();
+		if ( type.isAssociationType() ) {
+			if ( type.isEntityType() ) {
+				return new EntityElementGraph( sessionFactory(), this, propertyPath() );
+			}
+		}
+		else if ( type.isComponentType() ) {
+			return new CompositeElementGraph( sessionFactory(), this, propertyPath() );
+		}
+
+		return null;
+	}
+
+	public PropertyPath propertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public String getAlias() {
+		return alias;
+	}
+
+	@Override
+	public LockMode getLockMode() {
+		return lockMode;
+	}
+
+	@Override
+	public CollectionAliases getCollectionAliases() {
+		return collectionAliases;
+	}
+
+	@Override
+	public EntityAliases getElementEntityAliases() {
+		return elementEntityAliases;
+	}
+
+	@Override
+	public CollectionPersister getCollectionPersister() {
+		return collectionPersister;
+	}
+
+	@Override
+	public FetchOwner getIndexGraph() {
+		return indexGraph;
+	}
+
+	@Override
+	public FetchOwner getElementGraph() {
+		return elementGraph;
+	}
+
+	@Override
+	public boolean hasEntityElements() {
+		return getCollectionPersister().isOneToMany() || getCollectionPersister().isManyToMany();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetch.java
index e7d2751247..bb9d9d0c18 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetch.java
@@ -1,88 +1,93 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractFetch extends AbstractFetchOwner implements Fetch {
 	private final FetchOwner owner;
 	private final String ownerProperty;
 	private final FetchStrategy fetchStrategy;
 
 	private final PropertyPath propertyPath;
 
 	public AbstractFetch(
 			SessionFactoryImplementor factory,
 			String alias,
 			LockMode lockMode,
-			AbstractFetchOwner owner,
+			FetchOwner owner,
 			String ownerProperty,
 			FetchStrategy fetchStrategy) {
 		super( factory, alias, lockMode );
 		this.owner = owner;
 		this.ownerProperty = ownerProperty;
 		this.fetchStrategy = fetchStrategy;
 
 		owner.addFetch( this );
 
 		this.propertyPath = owner.getPropertyPath().append( ownerProperty );
 	}
 
 	@Override
 	public FetchOwner getOwner() {
 		return owner;
 	}
 
 	@Override
 	public String getOwnerPropertyName() {
 		return ownerProperty;
 	}
 
 	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 		if ( fetchStrategy.getStyle() == FetchStyle.JOIN ) {
 			if ( this.fetchStrategy.getStyle() != FetchStyle.JOIN ) {
 				throw new HibernateException( "Cannot specify join fetch from owner that is a non-joined fetch" );
 			}
 		}
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
+
+	@Override
+	public String toString() {
+		return "Fetch(" + propertyPath.getFullPath() + ")";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
index 87063b9278..d0dbc8197f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractFetchOwner.java
@@ -1,75 +1,76 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractFetchOwner extends AbstractPlanNode implements FetchOwner {
 	private final String alias;
 	private final LockMode lockMode;
 
 	private List<Fetch> fetches;
 
 	public AbstractFetchOwner(SessionFactoryImplementor factory, String alias, LockMode lockMode) {
 		super( factory );
 		this.alias = alias;
 		if ( alias == null ) {
 			throw new HibernateException( "alias must be specified" );
 		}
 		this.lockMode = lockMode;
 	}
 
 	public String getAlias() {
 		return alias;
 	}
 
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
-	void addFetch(Fetch fetch) {
+	@Override
+	public void addFetch(Fetch fetch) {
 		if ( fetch.getOwner() != this ) {
 			throw new IllegalArgumentException( "Fetch and owner did not match" );
 		}
 
 		if ( fetches == null ) {
 			fetches = new ArrayList<Fetch>();
 		}
 
 		fetches.add( fetch );
 	}
 
 	@Override
 	public Fetch[] getFetches() {
 		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
index f9324daa96..2ec4457bfe 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AbstractLoadPlanBuilderStrategy.java
@@ -1,212 +1,725 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
 import java.util.ArrayDeque;
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.DefaultEntityAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.GeneratedCollectionAliases;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.Loadable;
+import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
-import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.CollectionElementDefinition;
+import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
+import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.Type;
 
+import static org.hibernate.loader.spi.ResultSetProcessingContext.IdentifierResolutionContext;
+
 /**
  * @author Steve Ebersole
  */
-public abstract class AbstractLoadPlanBuilderStrategy implements LoadPlanBuilderStrategy {
+public abstract class AbstractLoadPlanBuilderStrategy implements LoadPlanBuilderStrategy, LoadPlanBuildingContext {
+	private static final Logger log = Logger.getLogger( AbstractLoadPlanBuilderStrategy.class );
+
 	private final SessionFactoryImplementor sessionFactory;
 
 	private ArrayDeque<FetchOwner> fetchOwnerStack = new ArrayDeque<FetchOwner>();
+	private ArrayDeque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
 
-	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory) {
+	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory, int suffixSeed) {
 		this.sessionFactory = sessionFactory;
+		this.currentSuffixBase = suffixSeed;
 	}
 
 	public SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected FetchOwner currentFetchOwner() {
-		return fetchOwnerStack.peekLast();
+		return fetchOwnerStack.peekFirst();
 	}
 
 	@Override
 	public void start() {
-		// nothing to do
+		if ( ! fetchOwnerStack.isEmpty() ) {
+			throw new WalkingException(
+					"Fetch owner stack was not empty on start; " +
+							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
+			);
+		}
+		if ( ! collectionReferenceStack.isEmpty() ) {
+			throw new WalkingException(
+					"Collection reference stack was not empty on start; " +
+							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
+			);
+		}
 	}
 
 	@Override
 	public void finish() {
-		// nothing to do
+		fetchOwnerStack.clear();
+		collectionReferenceStack.clear();
 	}
 
 	@Override
 	public void startingEntity(EntityDefinition entityDefinition) {
+		log.tracef(
+				"%s Starting entity : %s",
+				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
+				entityDefinition.getEntityPersister().getEntityName()
+		);
+
 		if ( fetchOwnerStack.isEmpty() ) {
 			// this is a root...
 			if ( ! supportsRootEntityReturns() ) {
 				throw new HibernateException( "This strategy does not support root entity returns" );
 			}
 			final EntityReturn entityReturn = buildRootEntityReturn( entityDefinition );
 			addRootReturn( entityReturn );
-			fetchOwnerStack.push( entityReturn );
+			pushToStack( entityReturn );
 		}
 		// otherwise this call should represent a fetch which should have been handled in #startingAttribute
 	}
 
 	protected boolean supportsRootEntityReturns() {
 		return false;
 	}
 
 	protected abstract void addRootReturn(Return rootReturn);
 
 	@Override
 	public void finishingEntity(EntityDefinition entityDefinition) {
-		// nothing to do
+		// pop the current fetch owner, and make sure what we just popped represents this entity
+		final FetchOwner poppedFetchOwner = popFromStack();
+
+		if ( ! EntityReference.class.isInstance( poppedFetchOwner ) ) {
+			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
+		}
+
+		final EntityReference entityReference = (EntityReference) poppedFetchOwner;
+		// NOTE : this is not the most exhaustive of checks because of hierarchical associations (employee/manager)
+		if ( ! entityReference.getEntityPersister().equals( entityDefinition.getEntityPersister() ) ) {
+			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
+		}
+
+		log.tracef(
+				"%s Finished entity : %s",
+				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
+				entityDefinition.getEntityPersister().getEntityName()
+		);
+	}
+
+	@Override
+	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
+		log.tracef(
+				"%s Starting entity identifier : %s",
+				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
+				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+		);
+
+		final EntityReference entityReference = (EntityReference) currentFetchOwner();
+
+		// perform some stack validation
+		if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
+			throw new WalkingException(
+					String.format(
+							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
+							entityReference.getEntityPersister().getEntityName(),
+							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+					)
+			);
+		}
+
+		final FetchOwner identifierAttributeCollector;
+		if ( entityIdentifierDefinition.isEncapsulated() ) {
+			identifierAttributeCollector = new EncapsulatedIdentifierAttributeCollector( entityReference );
+		}
+		else {
+			identifierAttributeCollector = new NonEncapsulatedIdentifierAttributeCollector( entityReference );
+		}
+		pushToStack( identifierAttributeCollector );
+	}
+
+	@Override
+	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
+		// perform some stack validation on exit, first on the current stack element we want to pop
+		{
+			final FetchOwner poppedFetchOwner = popFromStack();
+
+			if ( ! AbstractIdentifierAttributeCollector.class.isInstance( poppedFetchOwner ) ) {
+				throw new WalkingException( "Unexpected state in FetchOwner stack" );
+			}
+
+			final EntityReference entityReference = (EntityReference) poppedFetchOwner;
+			if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
+				throw new WalkingException(
+						String.format(
+								"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
+								entityReference.getEntityPersister().getEntityName(),
+								entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+						)
+				);
+			}
+		}
+
+		// and then on the element before it
+		{
+			final FetchOwner currentFetchOwner = currentFetchOwner();
+			if ( ! EntityReference.class.isInstance( currentFetchOwner ) ) {
+				throw new WalkingException( "Unexpected state in FetchOwner stack" );
+			}
+			final EntityReference entityReference = (EntityReference) currentFetchOwner;
+			if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
+				throw new WalkingException(
+						String.format(
+								"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
+								entityReference.getEntityPersister().getEntityName(),
+								entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+						)
+				);
+			}
+		}
+
+		log.tracef(
+				"%s Finished entity identifier : %s",
+				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
+				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+		);
 	}
 
 	@Override
 	public void startingCollection(CollectionDefinition collectionDefinition) {
+		log.tracef(
+				"%s Starting collection : %s",
+				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
+				collectionDefinition.getCollectionPersister().getRole()
+		);
+
 		if ( fetchOwnerStack.isEmpty() ) {
 			// this is a root...
 			if ( ! supportsRootCollectionReturns() ) {
 				throw new HibernateException( "This strategy does not support root collection returns" );
 			}
 			final CollectionReturn collectionReturn = buildRootCollectionReturn( collectionDefinition );
 			addRootReturn( collectionReturn );
-			fetchOwnerStack.push( collectionReturn );
+			pushToCollectionStack( collectionReturn );
 		}
 	}
 
 	protected boolean supportsRootCollectionReturns() {
 		return false;
 	}
 
 	@Override
+	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+		final Type indexType = collectionIndexDefinition.getType();
+		if ( indexType.isAssociationType() || indexType.isComponentType() ) {
+			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
+			final FetchOwner indexGraph = collectionReference.getIndexGraph();
+			if ( indexGraph == null ) {
+				throw new WalkingException( "Collection reference did not return index handler" );
+			}
+			pushToStack( indexGraph );
+		}
+	}
+
+	@Override
+	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+		// nothing to do here
+		// 	- the element graph pushed while starting would be popped in finishing/Entity/finishingComposite
+	}
+
+	@Override
+	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
+		if ( elementDefinition.getType().isAssociationType() || elementDefinition.getType().isComponentType() ) {
+			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
+			final FetchOwner elementGraph = collectionReference.getElementGraph();
+			if ( elementGraph == null ) {
+				throw new WalkingException( "Collection reference did not return element handler" );
+			}
+			pushToStack( elementGraph );
+		}
+	}
+
+	@Override
+	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
+		// nothing to do here
+		// 	- the element graph pushed while starting would be popped in finishing/Entity/finishingComposite
+	}
+
+	@Override
 	public void finishingCollection(CollectionDefinition collectionDefinition) {
-		// nothing to do
+		// pop the current fetch owner, and make sure what we just popped represents this collection
+		final CollectionReference collectionReference = popFromCollectionStack();
+		if ( ! collectionReference.getCollectionPersister().equals( collectionDefinition.getCollectionPersister() ) ) {
+			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
+		}
+
+		log.tracef(
+				"%s Finished collection : %s",
+				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
+				collectionDefinition.getCollectionPersister().getRole()
+		);
 	}
 
 	@Override
-	public void startingComposite(CompositeDefinition compositeDefinition) {
+	public void startingComposite(CompositionDefinition compositionDefinition) {
+		log.tracef(
+				"%s Starting composition : %s",
+				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
+				compositionDefinition.getName()
+		);
+
 		if ( fetchOwnerStack.isEmpty() ) {
 			throw new HibernateException( "A component cannot be the root of a walk nor a graph" );
 		}
 	}
 
 	@Override
-	public void finishingComposite(CompositeDefinition compositeDefinition) {
-		// nothing to do
+	public void finishingComposite(CompositionDefinition compositionDefinition) {
+		// pop the current fetch owner, and make sure what we just popped represents this composition
+		final FetchOwner poppedFetchOwner = popFromStack();
+
+		if ( ! CompositeFetch.class.isInstance( poppedFetchOwner ) ) {
+			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
+		}
+
+		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
+
+		log.tracef(
+				"%s Finished composition : %s",
+				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
+				compositionDefinition.getName()
+		);
 	}
 
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
+		log.tracef(
+				"%s Starting attribute %s",
+				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
+				attributeDefinition
+		);
+
 		final Type attributeType = attributeDefinition.getType();
 
 		final boolean isComponentType = attributeType.isComponentType();
 		final boolean isBasicType = ! ( isComponentType || attributeType.isAssociationType() );
 
 		if ( isBasicType ) {
 			return true;
 		}
 		else if ( isComponentType ) {
-			return handleCompositeAttribute( (CompositeDefinition) attributeDefinition );
+			return handleCompositeAttribute( (CompositionDefinition) attributeDefinition );
 		}
 		else {
 			return handleAssociationAttribute( (AssociationAttributeDefinition) attributeDefinition );
 		}
 	}
 
-
 	@Override
 	public void finishingAttribute(AttributeDefinition attributeDefinition) {
-		final Type attributeType = attributeDefinition.getType();
-
-		final boolean isComponentType = attributeType.isComponentType();
-		final boolean isBasicType = ! ( isComponentType || attributeType.isAssociationType() );
-
-		if ( ! isBasicType ) {
-			fetchOwnerStack.removeLast();
-		}
+		log.tracef(
+				"%s Finishing up attribute : %s",
+				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
+				attributeDefinition
+		);
 	}
 
-	protected boolean handleCompositeAttribute(CompositeDefinition attributeDefinition) {
-		final FetchOwner fetchOwner = fetchOwnerStack.peekLast();
-		final CompositeFetch fetch = buildCompositeFetch( fetchOwner, attributeDefinition );
-		fetchOwnerStack.addLast( fetch );
+	protected boolean handleCompositeAttribute(CompositionDefinition attributeDefinition) {
+		final FetchOwner fetchOwner = currentFetchOwner();
+		final CompositeFetch fetch = fetchOwner.buildCompositeFetch( attributeDefinition, this );
+		pushToStack( fetch );
 		return true;
 	}
 
 	protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
 		final FetchStrategy fetchStrategy = determineFetchPlan( attributeDefinition );
 		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
 			return false;
 		}
 
-		final FetchOwner fetchOwner = fetchOwnerStack.peekLast();
+		final FetchOwner fetchOwner = currentFetchOwner();
 		fetchOwner.validateFetchPlan( fetchStrategy );
 
 		final Fetch associationFetch;
 		if ( attributeDefinition.isCollection() ) {
-			associationFetch = buildCollectionFetch( fetchOwner, attributeDefinition, fetchStrategy );
+			associationFetch = fetchOwner.buildCollectionFetch( attributeDefinition, fetchStrategy, this );
 		}
 		else {
-			associationFetch = buildEntityFetch( fetchOwner, attributeDefinition, fetchStrategy );
+			associationFetch = fetchOwner.buildEntityFetch( attributeDefinition, fetchStrategy, this );
+		}
+
+		if ( FetchOwner.class.isInstance( associationFetch ) ) {
+			pushToStack( (FetchOwner) associationFetch );
 		}
-		fetchOwnerStack.addLast( associationFetch );
 
 		return true;
 	}
 
 	protected abstract FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition);
 
 	protected int currentDepth() {
 		return fetchOwnerStack.size();
 	}
 
 	protected boolean isTooManyCollections() {
 		return false;
 	}
 
+	private void pushToStack(FetchOwner fetchOwner) {
+		log.trace( "Pushing fetch owner to stack : " + fetchOwner );
+		fetchOwnerStack.addFirst( fetchOwner );
+	}
+
+	private FetchOwner popFromStack() {
+		final FetchOwner last = fetchOwnerStack.removeFirst();
+		log.trace( "Popped fetch owner from stack : " + last );
+		if ( FetchStackAware.class.isInstance( last ) ) {
+			( (FetchStackAware) last ).poppedFromStack();
+		}
+		return last;
+	}
+
+	private void pushToCollectionStack(CollectionReference collectionReference) {
+		log.trace( "Pushing collection reference to stack : " + collectionReference );
+		collectionReferenceStack.addFirst( collectionReference );
+	}
+
+	private CollectionReference popFromCollectionStack() {
+		final CollectionReference last = collectionReferenceStack.removeFirst();
+		log.trace( "Popped collection reference from stack : " + last );
+		if ( FetchStackAware.class.isInstance( last ) ) {
+			( (FetchStackAware) last ).poppedFromStack();
+		}
+		return last;
+	}
+
 	protected abstract EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition);
 
 	protected abstract CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition);
 
-	protected abstract CollectionFetch buildCollectionFetch(
-			FetchOwner fetchOwner,
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy);
 
-	protected abstract EntityFetch buildEntityFetch(
-			FetchOwner fetchOwner,
-			AssociationAttributeDefinition attributeDefinition,
-			FetchStrategy fetchStrategy);
 
-	protected abstract CompositeFetch buildCompositeFetch(FetchOwner fetchOwner, CompositeDefinition attributeDefinition);
+	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private int currentSuffixBase;
+	private int implicitAliasUniqueness = 0;
+
+	private String createImplicitAlias() {
+		return "ia" + implicitAliasUniqueness++;
+	}
+
+	@Override
+	public SessionFactoryImplementor getSessionFactory() {
+		return sessionFactory();
+	}
+
+	@Override
+	public EntityAliases resolveEntityColumnAliases(AssociationAttributeDefinition attributeDefinition) {
+		return generateEntityColumnAliases( attributeDefinition.toEntityDefinition().getEntityPersister() );
+	}
+
+	protected EntityAliases generateEntityColumnAliases(EntityPersister persister) {
+		return new DefaultEntityAliases( (Loadable) persister, Integer.toString( currentSuffixBase++ ) + '_' );
+	}
+
+	@Override
+	public CollectionAliases resolveCollectionColumnAliases(AssociationAttributeDefinition attributeDefinition) {
+		return generateCollectionColumnAliases( attributeDefinition.toCollectionDefinition().getCollectionPersister() );
+	}
+
+	protected CollectionAliases generateCollectionColumnAliases(CollectionPersister persister) {
+		return new GeneratedCollectionAliases( persister, Integer.toString( currentSuffixBase++ ) + '_' );
+	}
+
+	@Override
+	public String resolveRootSourceAlias(EntityDefinition definition) {
+		return createImplicitAlias();
+	}
+
+	@Override
+	public String resolveRootSourceAlias(CollectionDefinition definition) {
+		return createImplicitAlias();
+	}
+
+	@Override
+	public String resolveFetchSourceAlias(AssociationAttributeDefinition attributeDefinition) {
+		return createImplicitAlias();
+	}
+
+	@Override
+	public String resolveFetchSourceAlias(CompositionDefinition compositionDefinition) {
+		return createImplicitAlias();
+	}
+
+	public static interface FetchStackAware {
+		public void poppedFromStack();
+	}
+
+	protected static abstract class AbstractIdentifierAttributeCollector
+			implements FetchOwner, EntityReference, FetchStackAware {
+
+		protected final EntityReference entityReference;
+		private final PropertyPath propertyPath;
+
+		protected final List<EntityFetch> identifierFetches = new ArrayList<EntityFetch>();
+		protected final Map<EntityFetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap
+				= new HashMap<EntityFetch, HydratedCompoundValueHandler>();
+
+		public AbstractIdentifierAttributeCollector(EntityReference entityReference) {
+			this.entityReference = entityReference;
+			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath().append( "<id>" );
+		}
+
+		@Override
+		public String getAlias() {
+			return entityReference.getAlias();
+		}
+
+		@Override
+		public LockMode getLockMode() {
+			return entityReference.getLockMode();
+		}
+
+		@Override
+		public EntityPersister getEntityPersister() {
+			return entityReference.getEntityPersister();
+		}
+
+		@Override
+		public IdentifierDescription getIdentifierDescription() {
+			return entityReference.getIdentifierDescription();
+		}
+
+		@Override
+		public CollectionFetch buildCollectionFetch(
+				AssociationAttributeDefinition attributeDefinition,
+				FetchStrategy fetchStrategy,
+				LoadPlanBuildingContext loadPlanBuildingContext) {
+			throw new WalkingException( "Entity identifier cannot contain persistent collections" );
+		}
+
+		@Override
+		public EntityFetch buildEntityFetch(
+				AssociationAttributeDefinition attributeDefinition,
+				FetchStrategy fetchStrategy,
+				LoadPlanBuildingContext loadPlanBuildingContext) {
+			// we have a key-many-to-one
+			//
+			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back throw our #addFetch
+			// 		impl.  We collect them there and later build the IdentifierDescription
+			final EntityFetch fetch = LoadPlanBuildingHelper.buildStandardEntityFetch(
+					this,
+					attributeDefinition,
+					fetchStrategy,
+					loadPlanBuildingContext
+			);
+			fetchToHydratedStateExtractorMap.put( fetch, attributeDefinition.getHydratedCompoundValueExtractor() );
+
+			return fetch;
+		}
+
+		@Override
+		public CompositeFetch buildCompositeFetch(
+				CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
+			// nested composition.  Unusual, but not disallowed.
+			//
+			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back throw our #addFetch
+			// 		impl.  We collect them there and later build the IdentifierDescription
+			return LoadPlanBuildingHelper.buildStandardCompositeFetch(
+					this,
+					attributeDefinition,
+					loadPlanBuildingContext
+			);
+		}
+
+		@Override
+		public void poppedFromStack() {
+			final IdentifierDescription identifierDescription = buildIdentifierDescription();
+			entityReference.injectIdentifierDescription( identifierDescription );
+		}
+
+		protected abstract IdentifierDescription buildIdentifierDescription();
+
+		@Override
+		public void addFetch(Fetch fetch) {
+			identifierFetches.add( (EntityFetch) fetch );
+		}
+
+		@Override
+		public Fetch[] getFetches() {
+			return ( (FetchOwner) entityReference ).getFetches();
+		}
+
+		@Override
+		public void validateFetchPlan(FetchStrategy fetchStrategy) {
+			( (FetchOwner) entityReference ).validateFetchPlan( fetchStrategy );
+		}
+
+		@Override
+		public EntityPersister retrieveFetchSourcePersister() {
+			return ( (FetchOwner) entityReference ).retrieveFetchSourcePersister();
+		}
+
+		@Override
+		public PropertyPath getPropertyPath() {
+			return propertyPath;
+		}
+
+		@Override
+		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+			throw new WalkingException(
+					"IdentifierDescription collector should not get injected with IdentifierDescription"
+			);
+		}
+	}
+
+	protected static class EncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
+		public EncapsulatedIdentifierAttributeCollector(EntityReference entityReference) {
+			super( entityReference );
+		}
+
+		@Override
+		protected IdentifierDescription buildIdentifierDescription() {
+			return new IdentifierDescriptionImpl(
+					entityReference,
+					identifierFetches.toArray( new EntityFetch[ identifierFetches.size() ] ),
+					null
+			);
+		}
+	}
+
+	protected static class NonEncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
+		public NonEncapsulatedIdentifierAttributeCollector(EntityReference entityReference) {
+			super( entityReference );
+		}
+
+		@Override
+		protected IdentifierDescription buildIdentifierDescription() {
+			return new IdentifierDescriptionImpl(
+					entityReference,
+					identifierFetches.toArray( new EntityFetch[ identifierFetches.size() ] ),
+					fetchToHydratedStateExtractorMap
+			);
+		}
+	}
+
+	private static class IdentifierDescriptionImpl implements IdentifierDescription {
+		private final EntityReference entityReference;
+		private final EntityFetch[] identifierFetches;
+		private final Map<EntityFetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap;
+
+		private IdentifierDescriptionImpl(
+				EntityReference entityReference, EntityFetch[] identifierFetches,
+				Map<EntityFetch, HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap) {
+			this.entityReference = entityReference;
+			this.identifierFetches = identifierFetches;
+			this.fetchToHydratedStateExtractorMap = fetchToHydratedStateExtractorMap;
+		}
+
+		@Override
+		public Fetch[] getFetches() {
+			return identifierFetches;
+		}
+
+		@Override
+		public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+			final IdentifierResolutionContext ownerIdentifierResolutionContext =
+					context.getIdentifierResolutionContext( entityReference );
+			final Serializable ownerIdentifierHydratedState = ownerIdentifierResolutionContext.getHydratedForm();
+
+			for ( EntityFetch fetch : identifierFetches ) {
+				final IdentifierResolutionContext identifierResolutionContext =
+						context.getIdentifierResolutionContext( fetch );
+				// if the identifier was already hydrated, nothing to do
+				if ( identifierResolutionContext.getHydratedForm() != null ) {
+					continue;
+				}
+
+				// try to extract the sub-hydrated value from the owners tuple array
+				if ( fetchToHydratedStateExtractorMap != null && ownerIdentifierHydratedState != null ) {
+					Serializable extracted = (Serializable) fetchToHydratedStateExtractorMap.get( fetch )
+							.extract( ownerIdentifierHydratedState );
+					identifierResolutionContext.registerHydratedForm( extracted );
+					continue;
+				}
+
+				// if we can't, then read from result set
+				fetch.hydrate( resultSet, context );
+			}
+		}
+
+		@Override
+		public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+			for ( EntityFetch fetch : identifierFetches ) {
+				final IdentifierResolutionContext identifierResolutionContext =
+						context.getIdentifierResolutionContext( fetch );
+				if ( identifierResolutionContext.getEntityKey() != null ) {
+					continue;
+				}
+
+				EntityKey fetchKey = fetch.resolveInIdentifier( resultSet, context );
+				identifierResolutionContext.registerEntityKey( fetchKey );
+			}
+
+			final IdentifierResolutionContext ownerIdentifierResolutionContext =
+					context.getIdentifierResolutionContext( entityReference );
+			Serializable hydratedState = ownerIdentifierResolutionContext.getHydratedForm();
+			Serializable resolvedId = (Serializable) entityReference.getEntityPersister()
+					.getIdentifierType()
+					.resolve( hydratedState, context.getSession(), null );
+			return context.getSession().generateEntityKey( resolvedId, entityReference.getEntityPersister() );
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
index 973d147795..b5ad2e9e04 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
@@ -1,80 +1,97 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.collection.QueryableCollection;
-import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
 
 /**
  * @author Steve Ebersole
  */
-public class CollectionFetch extends AbstractFetch implements CollectionReference {
-	private final CollectionAliases collectionAliases;
-	private final EntityAliases elementEntityAliases;
-
-	private final CollectionPersister persister;
+public class CollectionFetch extends AbstractCollectionReference implements CollectionReference, Fetch {
+	private final FetchOwner fetchOwner;
+	private final FetchStrategy fetchStrategy;
 
 	public CollectionFetch(
 			SessionFactoryImplementor sessionFactory,
 			String alias,
 			LockMode lockMode,
-			AbstractFetchOwner owner,
+			FetchOwner fetchOwner,
 			FetchStrategy fetchStrategy,
 			String ownerProperty,
 			CollectionAliases collectionAliases,
 			EntityAliases elementEntityAliases) {
-		super( sessionFactory, alias, lockMode, owner, ownerProperty, fetchStrategy );
-		this.collectionAliases = collectionAliases;
-		this.elementEntityAliases = elementEntityAliases;
+		super(
+				sessionFactory,
+				alias,
+				lockMode,
+				sessionFactory.getCollectionPersister(
+						fetchOwner.retrieveFetchSourcePersister().getEntityName() + '.' + ownerProperty
+				),
+				fetchOwner.getPropertyPath().append( ownerProperty ),
+				collectionAliases,
+				elementEntityAliases
+		);
+		this.fetchOwner = fetchOwner;
+		this.fetchStrategy = fetchStrategy;
+	}
 
-		final String role = owner.retrieveFetchSourcePersister().getEntityName() + '.' + getOwnerPropertyName();
-		this.persister = sessionFactory.getCollectionPersister( role );
+	@Override
+	public FetchOwner getOwner() {
+		return fetchOwner;
+	}
+
+	@Override
+	public String getOwnerPropertyName() {
+		return getPropertyPath().getProperty();
 	}
 
 	@Override
-	public CollectionAliases getCollectionAliases() {
-		return collectionAliases;
+	public FetchStrategy getFetchStrategy() {
+		return fetchStrategy;
 	}
 
 	@Override
-	public EntityAliases getElementEntityAliases() {
-		return elementEntityAliases;
+	public PropertyPath getPropertyPath() {
+		return propertyPath();
 	}
 
 	@Override
-	public CollectionPersister getCollectionPersister() {
-		return persister;
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		//To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
-	public EntityPersister retrieveFetchSourcePersister() {
-		return ( (QueryableCollection) getCollectionPersister() ).getElementPersister();
+	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
index fa0460e922..da4acdcbb3 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReference.java
@@ -1,73 +1,79 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.LockMode;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Represents a reference to an owned collection either as a return or as a fetch
  *
  * @author Steve Ebersole
  */
 public interface CollectionReference {
 	/**
 	 * Retrieve the alias associated with the persister (entity/collection).
 	 *
 	 * @return The alias
 	 */
 	public String getAlias();
 
 	/**
 	 * Retrieve the lock mode associated with this return.
 	 *
 	 * @return The lock mode.
 	 */
 	public LockMode getLockMode();
 
 	/**
 	 * Retrieves the CollectionPersister describing the collection associated with this Return.
 	 *
 	 * @return The CollectionPersister.
 	 */
 	public CollectionPersister getCollectionPersister();
 
+	public FetchOwner getIndexGraph();
+
+	public FetchOwner getElementGraph();
+
+	public boolean hasEntityElements();
+
 	/**
 	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to the
 	 * this collection.
 	 *
 	 * @return The ResultSet alias descriptor for the collection
 	 */
 	public CollectionAliases getCollectionAliases();
 
 	/**
 	 * If the elements of this collection are entities, this methods returns the JDBC ResultSet alias descriptions
 	 * for that entity; {@code null} indicates a non-entity collection.
 	 *
 	 * @return The ResultSet alias descriptor for the collection's entity element, or {@code null}
 	 */
 	public EntityAliases getElementEntityAliases();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReferenceImplementor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReferenceImplementor.java
new file mode 100644
index 0000000000..1738802aef
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReferenceImplementor.java
@@ -0,0 +1,30 @@
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
+package org.hibernate.loader.plan.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface CollectionReferenceImplementor {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
index 0dacc39806..5bb106c0af 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionReturn.java
@@ -1,113 +1,101 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
 import org.hibernate.LockMode;
-import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.collection.QueryableCollection;
-import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
 
 /**
  * @author Steve Ebersole
  */
-public class CollectionReturn extends AbstractFetchOwner implements Return, FetchOwner, CollectionReference {
+public class CollectionReturn extends AbstractCollectionReference implements Return, CollectionReference {
 	private final String ownerEntityName;
 	private final String ownerProperty;
-	private final CollectionAliases collectionAliases;
-	private final EntityAliases elementEntityAliases;
-
-	private final CollectionPersister persister;
-
-	private final PropertyPath propertyPath = new PropertyPath(); // its a root
 
 	public CollectionReturn(
 			SessionFactoryImplementor sessionFactory,
 			String alias,
 			LockMode lockMode,
 			String ownerEntityName,
 			String ownerProperty,
 			CollectionAliases collectionAliases,
 			EntityAliases elementEntityAliases) {
-		super( sessionFactory, alias, lockMode );
+		super(
+				sessionFactory,
+				alias,
+				lockMode,
+				sessionFactory.getCollectionPersister( ownerEntityName + '.' + ownerProperty ),
+				new PropertyPath(), // its a root
+				collectionAliases,
+				elementEntityAliases
+		);
 		this.ownerEntityName = ownerEntityName;
 		this.ownerProperty = ownerProperty;
-		this.collectionAliases = collectionAliases;
-		this.elementEntityAliases = elementEntityAliases;
-
-		final String role = ownerEntityName + '.' + ownerProperty;
-		this.persister = sessionFactory.getCollectionPersister( role );
 	}
 
 	/**
 	 * Returns the class owning the collection.
 	 *
 	 * @return The class owning the collection.
 	 */
 	public String getOwnerEntityName() {
 		return ownerEntityName;
 	}
 
 	/**
 	 * Returns the name of the property representing the collection from the {@link #getOwnerEntityName}.
 	 *
 	 * @return The name of the property representing the collection on the owner class.
 	 */
 	public String getOwnerProperty() {
 		return ownerProperty;
 	}
 
 	@Override
-	public CollectionAliases getCollectionAliases() {
-		return collectionAliases;
-	}
-
-	@Override
-	public EntityAliases getElementEntityAliases() {
-		return elementEntityAliases;
-	}
-
-	@Override
-	public CollectionPersister getCollectionPersister() {
-		return persister;
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		// todo : anything to do here?
 	}
 
 	@Override
-	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		// todo : anything to do here?
 	}
 
 	@Override
-	public EntityPersister retrieveFetchSourcePersister() {
-		return ( (QueryableCollection) persister ).getElementPersister();
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 
 	@Override
-	public PropertyPath getPropertyPath() {
-		return propertyPath;
+	public String toString() {
+		return "CollectionReturn(" + getCollectionPersister().getRole() + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
new file mode 100644
index 0000000000..c1c450793b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeElementGraph.java
@@ -0,0 +1,90 @@
+package org.hibernate.loader.plan.spi;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositeElementGraph extends AbstractPlanNode implements FetchOwner {
+	private final CollectionReference collectionReference;
+	private final PropertyPath propertyPath;
+	private final CollectionPersister collectionPersister;
+
+	private List<Fetch> fetches;
+	public CompositeElementGraph(
+			SessionFactoryImplementor sessionFactory,
+			CollectionReference collectionReference,
+			PropertyPath collectionPath) {
+		super( sessionFactory );
+
+		this.collectionReference = collectionReference;
+		this.collectionPersister = collectionReference.getCollectionPersister();
+		this.propertyPath = collectionPath.append( "<elements>" );
+	}
+
+	@Override
+	public void addFetch(Fetch fetch) {
+		if ( fetches == null ) {
+			fetches = new ArrayList<Fetch>();
+		}
+		fetches.add( fetch );
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return collectionPersister.getOwnerEntityPersister();
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		throw new HibernateException( "Collection composite element cannot define collections" );
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardEntityFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
index 48d2f4c415..77eb3ebd2f 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
@@ -1,51 +1,89 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeFetch extends AbstractFetch implements Fetch {
 	public static final FetchStrategy FETCH_PLAN = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 
 	public CompositeFetch(
 			SessionFactoryImplementor sessionFactory,
 			String alias,
 			AbstractFetchOwner owner,
 			String ownerProperty) {
 		super( sessionFactory, alias, LockMode.NONE, owner, ownerProperty, FETCH_PLAN );
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return getOwner().retrieveFetchSourcePersister();
 	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
new file mode 100644
index 0000000000..8b482ac2e1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeIndexGraph.java
@@ -0,0 +1,90 @@
+package org.hibernate.loader.plan.spi;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositeIndexGraph extends AbstractPlanNode implements FetchOwner {
+	private final CollectionReference collectionReference;
+	private final PropertyPath propertyPath;
+	private final CollectionPersister collectionPersister;
+
+	private List<Fetch> fetches;
+
+	public CompositeIndexGraph(
+			SessionFactoryImplementor sessionFactory,
+			CollectionReference collectionReference,
+			PropertyPath propertyPath) {
+		super( sessionFactory );
+		this.collectionReference = collectionReference;
+		this.collectionPersister = collectionReference.getCollectionPersister();
+		this.propertyPath = propertyPath.append( "<index>" );
+	}
+
+	@Override
+	public void addFetch(Fetch fetch) {
+		if ( fetches == null ) {
+			fetches = new ArrayList<Fetch>();
+		}
+		fetches.add( fetch );
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return collectionPersister.getOwnerEntityPersister();
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		throw new HibernateException( "Composite index cannot define collections" );
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardEntityFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
new file mode 100644
index 0000000000..b7da167727
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityElementGraph.java
@@ -0,0 +1,133 @@
+package org.hibernate.loader.plan.spi;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.AssociationType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityElementGraph extends AbstractPlanNode implements FetchOwner, EntityReference {
+	private final CollectionReference collectionReference;
+	private final CollectionPersister collectionPersister;
+	private final AssociationType elementType;
+	private final EntityPersister elementPersister;
+	private final PropertyPath propertyPath;
+
+	private List<Fetch> fetches;
+
+	private IdentifierDescription identifierDescription;
+
+	public EntityElementGraph(
+			SessionFactoryImplementor sessionFactory,
+			CollectionReference collectionReference,
+			PropertyPath collectionPath) {
+		super( sessionFactory );
+
+		this.collectionReference = collectionReference;
+		this.collectionPersister = collectionReference.getCollectionPersister();
+		this.elementType = (AssociationType) collectionPersister.getElementType();
+		this.elementPersister = (EntityPersister) this.elementType.getAssociatedJoinable( sessionFactory() );
+		this.propertyPath = collectionPath.append( "<elements>" );
+	}
+
+	@Override
+	public String getAlias() {
+		return null;
+	}
+
+	@Override
+	public LockMode getLockMode() {
+		return null;
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return elementPersister;
+	}
+
+	@Override
+	public IdentifierDescription getIdentifierDescription() {
+		return identifierDescription;
+	}
+
+	@Override
+	public void addFetch(Fetch fetch) {
+		if ( fetches == null ) {
+			fetches = new ArrayList<Fetch>();
+		}
+		fetches.add( fetch );
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return elementPersister;
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardEntityFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	}
+
+	@Override
+	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+		this.identifierDescription = identifierDescription;
+	}
+
+	@Override
+	public String toString() {
+		return "EntityElementGraph(collection=" + collectionPersister.getRole() + ", type=" + elementPersister.getEntityName() + ")";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
index 252e3e0288..dac6350468 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
@@ -1,78 +1,248 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
 import org.hibernate.LockMode;
+import org.hibernate.WrongClassException;
 import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
-public class EntityFetch extends AbstractFetch implements EntityReference {
+public class EntityFetch extends AbstractFetch implements EntityReference, FetchOwner {
 	private final String sqlTableAlias;
 	private final EntityAliases entityAliases;
 
+	private final EntityType associationType;
 	private final EntityPersister persister;
 
+	private IdentifierDescription identifierDescription;
+
 	public EntityFetch(
 			SessionFactoryImplementor sessionFactory,
 			String alias,
 			LockMode lockMode,
-			AbstractFetchOwner owner,
+			FetchOwner owner,
 			String ownerProperty,
 			FetchStrategy fetchStrategy,
 			String sqlTableAlias,
 			EntityAliases entityAliases) {
 		super( sessionFactory, alias, lockMode, owner, ownerProperty, fetchStrategy );
 		this.sqlTableAlias = sqlTableAlias;
 		this.entityAliases = entityAliases;
 
-		final EntityType type = (EntityType) owner.retrieveFetchSourcePersister().getPropertyType( ownerProperty );
-		this.persister = sessionFactory.getEntityPersister( type.getAssociatedEntityName() );
+		this.associationType = (EntityType) owner.retrieveFetchSourcePersister().getPropertyType( ownerProperty );
+		this.persister = sessionFactory.getEntityPersister( associationType.getAssociatedEntityName() );
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	@Override
-	public EntityAliases getEntityAliases() {
-		return entityAliases;
+	public IdentifierDescription getIdentifierDescription() {
+		return identifierDescription;
 	}
 
 	@Override
-	public String getSqlTableAlias() {
-		return sqlTableAlias;
+	public EntityPersister retrieveFetchSourcePersister() {
+		return persister;
 	}
 
+
 	@Override
-	public EntityPersister retrieveFetchSourcePersister() {
-		return persister;
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardEntityFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	}
+
+	@Override
+	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+		this.identifierDescription = identifierDescription;
+	}
+
+	@Override
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		EntityKey entityKey = context.getDictatedRootEntityKey();
+		if ( entityKey != null ) {
+			context.getIdentifierResolutionContext( this ).registerEntityKey( entityKey );
+			return;
+		}
+
+		identifierDescription.hydrate( resultSet, context );
+
+		for ( Fetch fetch : getFetches() ) {
+			fetch.hydrate( resultSet, context );
+		}
+	}
+
+	@Override
+	public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		final ResultSetProcessingContext.IdentifierResolutionContext identifierResolutionContext = context.getIdentifierResolutionContext( this );
+		EntityKey entityKey = identifierResolutionContext.getEntityKey();
+		if ( entityKey == null ) {
+			entityKey = identifierDescription.resolve( resultSet, context );
+			if ( entityKey == null ) {
+				// register the non-existence (though only for one-to-one associations)
+				if ( associationType.isOneToOne() ) {
+					// first, find our owner's entity-key...
+					final EntityKey ownersEntityKey = context.getIdentifierResolutionContext( (EntityReference) getOwner() ).getEntityKey();
+					if ( ownersEntityKey != null ) {
+						context.getSession().getPersistenceContext()
+								.addNullProperty( ownersEntityKey, associationType.getPropertyName() );
+					}
+				}
+			}
+
+			identifierResolutionContext.registerEntityKey( entityKey );
+
+			for ( Fetch fetch : getFetches() ) {
+				fetch.resolve( resultSet, context );
+			}
+		}
+
+		return entityKey;
+	}
+
+	public EntityKey resolveInIdentifier(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		// todo : may not need to do this if entitykey is already part of the resolution context
+
+		final EntityKey entityKey = resolve( resultSet, context );
+
+		final Object existing = context.getSession().getEntityUsingInterceptor( entityKey );
+
+		if ( existing != null ) {
+			if ( !persister.isInstance( existing ) ) {
+				throw new WrongClassException(
+						"loaded object was of wrong class " + existing.getClass(),
+						entityKey.getIdentifier(),
+						persister.getEntityName()
+				);
+			}
+
+			if ( getLockMode() != null && getLockMode() != LockMode.NONE ) {
+				final boolean isVersionCheckNeeded = persister.isVersioned()
+						&& context.getSession().getPersistenceContext().getEntry( existing ).getLockMode().lessThan( getLockMode() );
+
+				// we don't need to worry about existing version being uninitialized because this block isn't called
+				// by a re-entrant load (re-entrant loads _always_ have lock mode NONE)
+				if ( isVersionCheckNeeded ) {
+					//we only check the version when _upgrading_ lock modes
+					context.checkVersion(
+							resultSet,
+							persister,
+							entityAliases,
+							entityKey,
+							existing
+					);
+					//we need to upgrade the lock mode to the mode requested
+					context.getSession().getPersistenceContext().getEntry( existing ).setLockMode( getLockMode() );
+				}
+			}
+		}
+		else {
+			final String concreteEntityTypeName = context.getConcreteEntityTypeName(
+					resultSet,
+					persister,
+					entityAliases,
+					entityKey
+			);
+
+			final Object entityInstance = context.getSession().instantiate(
+					concreteEntityTypeName,
+					entityKey.getIdentifier()
+			);
+
+			//need to hydrate it.
+
+			// grab its state from the ResultSet and keep it in the Session
+			// (but don't yet initialize the object itself)
+			// note that we acquire LockMode.READ even if it was not requested
+			LockMode acquiredLockMode = getLockMode() == LockMode.NONE ? LockMode.READ : getLockMode();
+
+			context.loadFromResultSet(
+					resultSet,
+					entityInstance,
+					concreteEntityTypeName,
+					entityKey,
+					entityAliases,
+					acquiredLockMode,
+					persister,
+					getFetchStrategy().getTiming() == FetchTiming.IMMEDIATE,
+					associationType
+			);
+
+			// materialize associations (and initialize the object) later
+			context.registerHydratedEntity( persister, entityKey, entityInstance );
+		}
+
+		return entityKey;
+	}
+
+	@Override
+	public String toString() {
+		return "EntityFetch(" + getPropertyPath().getFullPath() + " -> " + persister.getEntityName() + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
new file mode 100644
index 0000000000..8d7c50de55
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityIndexGraph.java
@@ -0,0 +1,150 @@
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
+package org.hibernate.loader.plan.spi;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.LockMode;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.AssociationType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityIndexGraph extends AbstractPlanNode implements FetchOwner, EntityReference {
+	private final CollectionReference collectionReference;
+	private final CollectionPersister collectionPersister;
+	private final AssociationType indexType;
+	private final EntityPersister indexPersister;
+	private final PropertyPath propertyPath;
+
+	private List<Fetch> fetches;
+
+	private IdentifierDescription identifierDescription;
+
+	public EntityIndexGraph(
+			SessionFactoryImplementor sessionFactory,
+			CollectionReference collectionReference,
+			PropertyPath collectionPath) {
+		super( sessionFactory );
+		this.collectionReference = collectionReference;
+		this.collectionPersister = collectionReference.getCollectionPersister();
+		this.indexType = (AssociationType) collectionPersister.getIndexType();
+		this.indexPersister = (EntityPersister) this.indexType.getAssociatedJoinable( sessionFactory() );
+		this.propertyPath = collectionPath.append( "<index>" ); // todo : do we want the <index> part?
+	}
+
+	@Override
+	public String getAlias() {
+		return null;
+	}
+
+	@Override
+	public LockMode getLockMode() {
+		return null;
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return indexPersister;
+	}
+
+	@Override
+	public IdentifierDescription getIdentifierDescription() {
+		return identifierDescription;
+	}
+
+	@Override
+	public void addFetch(Fetch fetch) {
+		if ( fetches == null ) {
+			fetches = new ArrayList<Fetch>();
+		}
+		fetches.add( fetch );
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	}
+
+	@Override
+	public EntityPersister retrieveFetchSourcePersister() {
+		return indexPersister;
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardEntityFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	}
+
+	@Override
+	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+		this.identifierDescription = identifierDescription;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
index d1102acc7b..401a478eed 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
@@ -1,73 +1,57 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.LockMode;
-import org.hibernate.loader.EntityAliases;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Represents a reference to an entity either as a return or as a fetch
  *
  * @author Steve Ebersole
  */
-public interface EntityReference {
+public interface EntityReference extends IdentifierDescriptionInjectable {
 	/**
 	 * Retrieve the alias associated with the persister (entity/collection).
 	 *
 	 * @return The alias
 	 */
 	public String getAlias();
 
 	/**
 	 * Retrieve the lock mode associated with this return.
 	 *
 	 * @return The lock mode.
 	 */
 	public LockMode getLockMode();
 
 	/**
 	 * Retrieves the EntityPersister describing the entity associated with this Return.
 	 *
 	 * @return The EntityPersister.
 	 */
 	public EntityPersister getEntityPersister();
 
-	/**
-	 * Returns the description of the aliases in the JDBC ResultSet that identify values "belonging" to the this entity.
-	 *
-	 * @return The ResultSet alias descriptor.
-	 */
-	public EntityAliases getEntityAliases();
-
-	/**
-	 * Obtain the SQL table alias associated with this entity.
-	 *
-	 * TODO : eventually this needs to not be a String, but a representation like I did for the Antlr3 branch
-	 * 		(AliasRoot, I think it was called)
-	 *
-	 * @return The SQL table alias for this entity
-	 */
-	public String getSqlTableAlias();
+	public IdentifierDescription getIdentifierDescription();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
index eb14ecf297..7ec852d714 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn.java
@@ -1,96 +1,185 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
+import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.internal.ResultSetProcessorHelper;
+import org.hibernate.loader.plan.internal.LoadPlanBuildingHelper;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+
+import static org.hibernate.loader.spi.ResultSetProcessingContext.IdentifierResolutionContext;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReturn extends AbstractFetchOwner implements Return, FetchOwner, EntityReference {
 	private final EntityAliases entityAliases;
 	private final String sqlTableAlias;
 
 	private final EntityPersister persister;
 
 	private final PropertyPath propertyPath = new PropertyPath(); // its a root
 
+	private IdentifierDescription identifierDescription;
+
 	public EntityReturn(
 			SessionFactoryImplementor sessionFactory,
 			String alias,
 			LockMode lockMode,
 			String entityName,
 			String sqlTableAlias,
 			EntityAliases entityAliases) {
 		super( sessionFactory, alias, lockMode );
 		this.entityAliases = entityAliases;
 		this.sqlTableAlias = sqlTableAlias;
 
 		this.persister = sessionFactory.getEntityPersister( entityName );
 	}
 
 	@Override
 	public String getAlias() {
 		return super.getAlias();
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return super.getLockMode();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	@Override
-	public EntityAliases getEntityAliases() {
-		return entityAliases;
-	}
-
-	@Override
-	public String getSqlTableAlias() {
-		return sqlTableAlias;
+	public IdentifierDescription getIdentifierDescription() {
+		return identifierDescription;
 	}
 
 	@Override
 	public void validateFetchPlan(FetchStrategy fetchStrategy) {
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return getEntityPersister();
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCollectionFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardEntityFetch(
+				this,
+				attributeDefinition,
+				fetchStrategy,
+				loadPlanBuildingContext
+		);
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return LoadPlanBuildingHelper.buildStandardCompositeFetch( this, attributeDefinition, loadPlanBuildingContext );
+	}
+
+	@Override
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		EntityKey entityKey = context.getDictatedRootEntityKey();
+		if ( entityKey != null ) {
+			context.getIdentifierResolutionContext( this ).registerEntityKey( entityKey );
+			return;
+		}
+
+		identifierDescription.hydrate( resultSet, context );
+
+		for ( Fetch fetch : getFetches() ) {
+			fetch.hydrate( resultSet, context );
+		}
+	}
+
+	@Override
+	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		final IdentifierResolutionContext identifierResolutionContext = context.getIdentifierResolutionContext( this );
+		EntityKey entityKey = identifierResolutionContext.getEntityKey();
+		if ( entityKey == null ) {
+			return;
+		}
+
+		entityKey = identifierDescription.resolve( resultSet, context );
+		identifierResolutionContext.registerEntityKey( entityKey );
+
+		for ( Fetch fetch : getFetches() ) {
+			fetch.resolve( resultSet, context );
+		}
+	}
+
+	@Override
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		return null;
+	}
+
+	@Override
+	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+		this.identifierDescription = identifierDescription;
+	}
+
+	@Override
+	public String toString() {
+		return "EntityReturn(" + persister.getEntityName() + ")";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
index 4be5ca8bbc..7213e18336 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
@@ -1,59 +1,67 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
 
 /**
  * Contract for associations that are being fetched.
  * <p/>
  * NOTE : can represent components/embeddables
  *
  * @author Steve Ebersole
  */
-public interface Fetch extends FetchOwner {
+public interface Fetch {
 	/**
 	 * Obtain the owner of this fetch.
 	 *
 	 * @return The fetch owner.
 	 */
 	public FetchOwner getOwner();
 
 	/**
 	 * Obtain the name of the property, relative to the owner, being fetched.
 	 *
 	 * @return The fetched property name.
 	 */
 	public String getOwnerPropertyName();
 
 	public FetchStrategy getFetchStrategy();
 
 	/**
 	 * Get the property path to this fetch
 	 *
 	 * @return The property path
 	 */
 	public PropertyPath getPropertyPath();
+
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+
+	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
index e28daf1b24..a01e88ab2e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/FetchOwner.java
@@ -1,68 +1,93 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 
 /**
  * Contract for owners of fetches.  Any non-scalar return could be a fetch owner.
  *
  * @author Steve Ebersole
  */
 public interface FetchOwner {
 	/**
 	 * Convenient constant for returning no fetches from {@link #getFetches()}
 	 */
 	public static final Fetch[] NO_FETCHES = new Fetch[0];
 
 	/**
+	 * Contract to add fetches to this owner.  Care should be taken in calling this method; it is intended
+	 * for Hibernate usage
+	 *
+	 * @param fetch The fetch to add
+	 */
+	public void addFetch(Fetch fetch);
+
+	/**
 	 * Retrieve the fetches owned by this return.
 	 *
 	 * @return The owned fetches.
 	 */
 	public Fetch[] getFetches();
 
 	/**
 	 * Is the asserted plan valid from this owner to a fetch?
 	 *
 	 * @param fetchStrategy The pla to validate
 	 */
 	public void validateFetchPlan(FetchStrategy fetchStrategy);
 
 	/**
 	 * Retrieve the EntityPersister that is the base for any property references in the fetches it owns.
 	 *
 	 * @return The EntityPersister, for property name resolution.
 	 */
 	public EntityPersister retrieveFetchSourcePersister();
 
 	/**
 	 * Get the property path to this fetch owner
 	 *
 	 * @return The property path
 	 */
 	public PropertyPath getPropertyPath();
+
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext);
+
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext);
+
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescription.java
new file mode 100644
index 0000000000..62d8371196
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescription.java
@@ -0,0 +1,41 @@
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
+package org.hibernate.loader.plan.spi;
+
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface IdentifierDescription {
+	public Fetch[] getFetches();
+
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+
+	public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescriptionInjectable.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescriptionInjectable.java
new file mode 100644
index 0000000000..4528bfa605
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescriptionInjectable.java
@@ -0,0 +1,33 @@
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
+package org.hibernate.loader.plan.spi;
+
+/**
+ * Ugh
+ *
+ * @author Steve Ebersole
+ */
+public interface IdentifierDescriptionInjectable {
+	public void injectIdentifierDescription(IdentifierDescription identifierDescription);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
index 0173b39714..035f6c0a84 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
@@ -1,61 +1,62 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.util.List;
 
 /**
  * Describes a plan for performing a load of results.
  *
  * Generally speaking there are 3 forms of load plans:<ul>
  *     <li>
  *         An entity load plan for handling get/load handling.  This form will typically have a single
  *         return (of type {@link EntityReturn}) defined by {@link #getReturns()}, possibly defining fetches.
  *     </li>
  *     <li>
  *         A collection initializer, used to load the contents of a collection.  This form will typically have a
  *         single return (of type {@link CollectionReturn} defined by {@link #getReturns()}, possibly defining fetches
  *     </li>
  *     <li>
  *         A query load plan which can contain multiple returns of mixed type (though implementing {@link Return}).
  *         Again, may possibly define fetches.
  *     </li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public interface LoadPlan {
 	/**
 	 * Convenient form of checking {@link #getReturns()} for scalar root returns.
 	 *
 	 * @return {@code true} if {@link #getReturns()} contained any scalar returns; {@code false} otherwise.
 	 */
 	public boolean hasAnyScalarReturns();
 
 	public List<Return> getReturns();
 
+
 	// todo : would also like to see "call back" style access for handling "subsequent actions" such as:
 	// 		1) follow-on locking
 	//		2) join fetch conversions to subselect fetches
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuildingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuildingContext.java
new file mode 100644
index 0000000000..152133f44c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanBuildingContext.java
@@ -0,0 +1,48 @@
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
+package org.hibernate.loader.plan.spi;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.CollectionAliases;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface LoadPlanBuildingContext {
+	public SessionFactoryImplementor getSessionFactory();
+
+	public CollectionAliases resolveCollectionColumnAliases(AssociationAttributeDefinition attributeDefinition);
+	public EntityAliases resolveEntityColumnAliases(AssociationAttributeDefinition attributeDefinition);
+
+	public String resolveRootSourceAlias(EntityDefinition definition);
+	public String resolveRootSourceAlias(CollectionDefinition definition);
+
+	public String resolveFetchSourceAlias(AssociationAttributeDefinition attributeDefinition);
+	public String resolveFetchSourceAlias(CompositionDefinition compositionDefinition);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitationStrategy.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitationStrategy.java
index 4140249b3a..976177ed33 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitationStrategy.java
@@ -1,138 +1,138 @@
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
 package org.hibernate.loader.plan.spi;
 
 /**
  * @author Steve Ebersole
  */
-public interface ReturnVisitationStrategy {
+public interface LoadPlanVisitationStrategy {
 	/**
 	 * Notification we are preparing to start visitation.
 	 */
-	public void start();
+	public void start(LoadPlan loadPlan);
 
 	/**
 	 * Notification we are finished visitation.
 	 */
-	public void finish();
+	public void finish(LoadPlan loadPlan);
 
 	/**
 	 * Notification that a new root return branch is being started.  Will be followed by calls to one of the following
 	 * based on the type of return:<ul>
 	 *     <li>{@link #handleScalarReturn}</li>
 	 *     <li>{@link #handleEntityReturn}</li>
 	 *     <li>{@link #handleCollectionReturn}</li>
 	 * </ul>
 	 *
 	 * @param rootReturn The root return at the root of the branch.
 	 */
 	public void startingRootReturn(Return rootReturn);
 
 	/**
 	 * Notification that we are finishing up processing a root return branch
 	 *
 	 * @param rootReturn The RootReturn we are finishing up processing.
 	 */
 	public void finishingRootReturn(Return rootReturn);
 
 	/**
 	 * Notification that a scalar return is being processed.  Will be surrounded by calls to {@link #startingRootReturn}
 	 * and {@link #finishingRootReturn}
 	 *
 	 * @param scalarReturn The scalar return
 	 */
 	public void handleScalarReturn(ScalarReturn scalarReturn);
 
 	/**
 	 * Notification that a root entity return is being processed.  Will be surrounded by calls to
 	 * {@link #startingRootReturn} and {@link #finishingRootReturn}
 	 *
 	 * @param rootEntityReturn The root entity return
 	 */
 	public void handleEntityReturn(EntityReturn rootEntityReturn);
 
 	/**
 	 * Notification that a root collection return is being processed.  Will be surrounded by calls to
 	 * {@link #startingRootReturn} and {@link #finishingRootReturn}
 	 *
 	 * @param rootCollectionReturn The root collection return
 	 */
 	public void handleCollectionReturn(CollectionReturn rootCollectionReturn);
 
 	/**
 	 * Notification that we are about to start processing the fetches for the given fetch owner.
 	 *
 	 * @param fetchOwner The fetch owner.
 	 */
 	public void startingFetches(FetchOwner fetchOwner);
 
 	/**
 	 * Notification that we are finishing up processing the fetches for the given fetch owner.
 	 *
 	 * @param fetchOwner The fetch owner.
 	 */
 	public void finishingFetches(FetchOwner fetchOwner);
 
 	/**
 	 * Notification we are starting the processing of an entity fetch
 	 *
 	 * @param entityFetch The entity fetch
 	 */
 	public void startingEntityFetch(EntityFetch entityFetch);
 
 	/**
 	 * Notification that we are finishing up the processing of an entity fetch
 	 *
 	 * @param entityFetch The entity fetch
 	 */
 	public void finishingEntityFetch(EntityFetch entityFetch);
 
 	/**
 	 * Notification we are starting the processing of a collection fetch
 	 *
 	 * @param collectionFetch The collection fetch
 	 */
 	public void startingCollectionFetch(CollectionFetch collectionFetch);
 
 	/**
 	 * Notification that we are finishing up the processing of a collection fetch
 	 *
 	 * @param collectionFetch The collection fetch
 	 */
 	public void finishingCollectionFetch(CollectionFetch collectionFetch);
 
 	/**
 	 * Notification we are starting the processing of a component fetch
 	 *
 	 * @param fetch The composite fetch
 	 */
 	public void startingCompositeFetch(CompositeFetch fetch);
 
 	/**
 	 * Notification that we are finishing up the processing of a composite fetch
 	 *
 	 * @param fetch The composite fetch
 	 */
 	public void finishingCompositeFetch(CompositeFetch fetch);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitationStrategyAdapter.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitationStrategyAdapter.java
new file mode 100644
index 0000000000..6734ef990c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitationStrategyAdapter.java
@@ -0,0 +1,104 @@
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
+package org.hibernate.loader.plan.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public class LoadPlanVisitationStrategyAdapter implements LoadPlanVisitationStrategy {
+	@Override
+	public void start(LoadPlan loadPlan) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void finish(LoadPlan loadPlan) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void startingRootReturn(Return rootReturn) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void finishingRootReturn(Return rootReturn) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void handleScalarReturn(ScalarReturn scalarReturn) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void handleEntityReturn(EntityReturn rootEntityReturn) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void startingFetches(FetchOwner fetchOwner) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void finishingFetches(FetchOwner fetchOwner) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void startingEntityFetch(EntityFetch entityFetch) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void finishingEntityFetch(EntityFetch entityFetch) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void startingCollectionFetch(CollectionFetch collectionFetch) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void finishingCollectionFetch(CollectionFetch collectionFetch) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void startingCompositeFetch(CompositeFetch fetch) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void finishingCompositeFetch(CompositeFetch fetch) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitor.java
similarity index 78%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitor.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitor.java
index cf4aaf6ef9..9a371aa011 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ReturnVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlanVisitor.java
@@ -1,116 +1,119 @@
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
 package org.hibernate.loader.plan.spi;
 
 /**
  * Visitor for processing {@link Return} graphs
  *
  * @author Steve Ebersole
  */
-public class ReturnVisitor {
-	public static void visit(Return[] rootReturns, ReturnVisitationStrategy strategy) {
-		new ReturnVisitor( strategy ).visitReturns( rootReturns );
+public class LoadPlanVisitor {
+	public static void visit(LoadPlan loadPlan, LoadPlanVisitationStrategy strategy) {
+		new LoadPlanVisitor( strategy ).visit( loadPlan );
 	}
 
-	private final ReturnVisitationStrategy strategy;
+	private final LoadPlanVisitationStrategy strategy;
 
-	public ReturnVisitor(ReturnVisitationStrategy strategy) {
+	public LoadPlanVisitor(LoadPlanVisitationStrategy strategy) {
 		this.strategy = strategy;
 	}
 
-	private void visitReturns(Return[] rootReturns) {
-		strategy.start();
+	private void visit(LoadPlan loadPlan) {
+		strategy.start( loadPlan );
 
-		for ( Return rootReturn : rootReturns ) {
+		for ( Return rootReturn : loadPlan.getReturns() ) {
 			visitRootReturn( rootReturn );
 		}
 
-		strategy.finish();
+		strategy.finish( loadPlan );
 	}
 
 	private void visitRootReturn(Return rootReturn) {
 		strategy.startingRootReturn( rootReturn );
 
 		if ( org.hibernate.loader.plan.spi.ScalarReturn.class.isInstance( rootReturn ) ) {
 			strategy.handleScalarReturn( (ScalarReturn) rootReturn );
 		}
 		else {
 			visitNonScalarRootReturn( rootReturn );
 		}
 
 		strategy.finishingRootReturn( rootReturn );
 	}
 
 	private void visitNonScalarRootReturn(Return rootReturn) {
 		if ( EntityReturn.class.isInstance( rootReturn ) ) {
 			strategy.handleEntityReturn( (EntityReturn) rootReturn );
 			visitFetches( (EntityReturn) rootReturn );
 		}
 		else if ( CollectionReturn.class.isInstance( rootReturn ) ) {
 			strategy.handleCollectionReturn( (CollectionReturn) rootReturn );
-			visitFetches( (CollectionReturn) rootReturn );
+			final CollectionReturn collectionReturn = (CollectionReturn) rootReturn;
+			visitFetches( collectionReturn.getIndexGraph() );
+			visitFetches( collectionReturn.getElementGraph() );
 		}
 		else {
 			throw new IllegalStateException(
 					"Unexpected return type encountered; expecting a non-scalar root return, but found " +
 							rootReturn.getClass().getName()
 			);
 		}
 	}
 
 	private void visitFetches(FetchOwner fetchOwner) {
 		strategy.startingFetches( fetchOwner );
 
 		for ( Fetch fetch : fetchOwner.getFetches() ) {
 			visitFetch( fetch );
 		}
 
 		strategy.finishingFetches( fetchOwner );
 	}
 
 	private void visitFetch(Fetch fetch) {
 		if ( EntityFetch.class.isInstance( fetch ) ) {
 			strategy.startingEntityFetch( (EntityFetch) fetch );
-			visitFetches( fetch );
+			visitFetches( (EntityFetch) fetch );
 			strategy.finishingEntityFetch( (EntityFetch) fetch );
 		}
 		else if ( CollectionFetch.class.isInstance( fetch ) ) {
 			strategy.startingCollectionFetch( (CollectionFetch) fetch );
-			visitFetches( fetch );
+			visitFetches( ( (CollectionFetch) fetch ).getIndexGraph() );
+			visitFetches( ( (CollectionFetch) fetch ).getElementGraph() );
 			strategy.finishingCollectionFetch( (CollectionFetch) fetch );
 		}
 		else if ( CompositeFetch.class.isInstance( fetch ) ) {
 			strategy.startingCompositeFetch( (CompositeFetch) fetch );
-			visitFetches( fetch );
+			visitFetches( (CompositeFetch) fetch );
 			strategy.finishingCompositeFetch( (CompositeFetch) fetch );
 		}
 		else {
 			throw new IllegalStateException(
 					"Unexpected return type encountered; expecting a fetch return, but found " +
 							fetch.getClass().getName()
 			);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
index b94adfae9d..19c700f57c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Return.java
@@ -1,40 +1,67 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
+import org.hibernate.loader.spi.ResultSetProcessingContext;
+
 /**
  * Represents a return value in the query results.  Not the same as a result (column) in the JDBC ResultSet!
  * <p/>
- * This is merely a unifying contract; it defines no behavior.
- * <p/>
- * Return is distinctly different from a {@link Fetch}.
+ * Return is distinctly different from a {@link Fetch} and so modeled as completely separate hierarchy.
  *
  * @see ScalarReturn
  * @see EntityReturn
  * @see CollectionReturn
  *
  * @author Steve Ebersole
  */
 public interface Return {
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+
+	/**
+	 * Effectively performs first phase of two-phase loading.  For scalar results first/second phase is one.  For
+	 * entities, first phase is to resolve identifiers; second phase is to resolve the entity instances.
+	 *
+	 * @param resultSet The result set being processed
+	 * @param context The context for the processing
+	 *
+	 * @throws SQLException Indicates a problem access the JDBC result set
+	 */
+	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+
+	/**
+	 * Essentially performs the second phase of two-phase loading.
+	 *
+	 * @param resultSet The result set being processed
+	 * @param context The context for the processing
+	 *
+	 * @return The read object
+	 *
+	 * @throws SQLException Indicates a problem access the JDBC result set
+	 */
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
index 9a3d434bbc..d8a86ef117 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/ScalarReturn.java
@@ -1,52 +1,68 @@
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
 package org.hibernate.loader.plan.spi;
 
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.spi.ResultSetProcessingContext;
 import org.hibernate.type.Type;
 
 /**
  * Represent a simple scalar return within a query result.  Generally this would be values of basic (String, Integer,
  * etc) or composite types.
  *
  * @author Steve Ebersole
  */
 public class ScalarReturn extends AbstractPlanNode implements Return {
 	private final Type type;
-	private final String columnAlias;
+	private final String[] columnAliases;
 
-	public ScalarReturn(SessionFactoryImplementor factory, Type type, String columnAlias) {
+	public ScalarReturn(SessionFactoryImplementor factory, Type type, String[] columnAliases) {
 		super( factory );
 		this.type = type;
-		this.columnAlias = columnAlias;
+		this.columnAliases = columnAliases;
 	}
 
 	public Type getType() {
 		return type;
 	}
 
-	public String getColumnAlias() {
-		return columnAlias;
+	@Override
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) {
+		// nothing to do
+	}
+
+	@Override
+	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) {
+		// nothing to do
+	}
+
+	@Override
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+		return type.nullSafeGet( resultSet, columnAliases, context.getSession(), null );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/AfterLoadAction.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/AfterLoadAction.java
new file mode 100644
index 0000000000..3fa9257b9a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/AfterLoadAction.java
@@ -0,0 +1,34 @@
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
+package org.hibernate.loader.spi;
+
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.persister.entity.Loadable;
+
+/**
+* @author Steve Ebersole
+*/
+public interface AfterLoadAction {
+	public void afterLoad(SessionImplementor session, Object entity, Loadable persister);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/Loader.java
index 28c5cc36be..1513d61a82 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/spi/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/Loader.java
@@ -1,65 +1,59 @@
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
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.List;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.transform.ResultTransformer;
 
 /**
- * Definition of the Loader contract.
- * <p/>
- * Capabilities I'd like to see added (todo):<ul>
- *     <li>
- *         expose the underlying "query" (although what I see here relies heavily on
- *         https://github.com/hibernate/hibernate-orm/wiki/Proposal---SQL-generation)
- *     </li>
- * </ul>
- *
+ * Definition of the Loader contract.  A Loader is intended to perform loading based on a query and a load-plan.
+ * Under the covers it uses many delegates to perform that work that might be better used individually in
+ * different situations.  In general, Loader is intended for being fed a set of results and processing through
+ * all of those result rows in one swoop.  For cases that do not fit that template, it is probably better to
+ * individually use the delegates to perform the work.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface Loader {
-	public LoadPlan getLoadPlan();
-
 	/**
-	 * Obtain the on-demand form of this loader, if possible.
+	 * Obtain the LoadPlan this Loader is following.
 	 *
-	 * @return The on-demand version of this loader
+	 * @return
 	 */
-	public OnDemandLoader asOnDemandLoader();
+	public LoadPlan getLoadPlan();
 
 	public List extractResults(
 			ResultSet resultSet,
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/NamedParameterContext.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/NamedParameterContext.java
new file mode 100644
index 0000000000..d8b33e98ee
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/NamedParameterContext.java
@@ -0,0 +1,36 @@
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
+package org.hibernate.loader.spi;
+
+/**
+ * The context for named parameters.
+ * <p/>
+ * NOTE : the hope with the SQL-redesign stuff is that this whole concept goes away, the idea being that
+ * the parameters are encoded into the query tree and "bind themselves".
+ *
+ * @author Steve Ebersole
+ */
+public interface NamedParameterContext {
+	public int[] getNamedParameterLocations(String name);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/OnDemandLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/OnDemandResultSetProcessor.java
similarity index 74%
rename from hibernate-core/src/main/java/org/hibernate/loader/spi/OnDemandLoader.java
rename to hibernate-core/src/main/java/org/hibernate/loader/spi/OnDemandResultSetProcessor.java
index 1a87626f76..27ba73b5a6 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/spi/OnDemandLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/OnDemandResultSetProcessor.java
@@ -1,105 +1,107 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 
 import java.sql.ResultSet;
 
-import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
- * Represents an on-demand loading strategy as need for processing single *logical* rows one at a time as required
- * for {@link org.hibernate.ScrollableResults} implementations.
+ * Contract for processing JDBC ResultSets a single logical row at a time.  These are intended for use by
+ * {@link org.hibernate.ScrollableResults} implementations.
+ *
+ * NOTE : these methods initially taken directly from {@link org.hibernate.loader.Loader} counterparts in an effort
+ * to break Loader into manageable pieces, especially in regards to the processing of result sets.
  *
  * @author Steve Ebersole
  */
-public interface OnDemandLoader {
+public interface OnDemandResultSetProcessor {
 
 	/**
-	 * Given a ResultSet, extract just a single result row.
+	 * Give a ResultSet, extract just a single result row.
 	 *
-	 * Copy of {@link org.hibernate.loader.Loader#loadSingleRow(ResultSet, SessionImplementor, QueryParameters, boolean)}
+	 * Copy of {@link org.hibernate.loader.Loader#loadSingleRow(java.sql.ResultSet, org.hibernate.engine.spi.SessionImplementor, org.hibernate.engine.spi.QueryParameters, boolean)}
 	 * but dropping the 'returnProxies' (that method has only one use in the entire codebase and it always passes in
 	 * false...)
 	 *
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 *
 	 * @return The extracted result row
 	 *
-	 * @throws HibernateException Indicates a problem extracting values from the result set.
+	 * @throws org.hibernate.HibernateException Indicates a problem extracting values from the result set.
 	 */
 	public Object extractSingleRow(
 			ResultSet resultSet,
 			SessionImplementor session,
-			QueryParameters queryParameters) throws HibernateException;
+			QueryParameters queryParameters);
 
 	/**
 	 * Given a ResultSet extract "sequential rows".  This is used in cases where we have multi-row fetches that
 	 * are sequential within the ResultSet due to ordering.  Multiple ResultSet rows are read into a single query
 	 * result "row".
 	 *
-	 * Copy of {@link org.hibernate.loader.Loader#loadSequentialRowsForward(ResultSet, SessionImplementor, QueryParameters, boolean)}
+	 * Copy of {@link org.hibernate.loader.Loader#loadSequentialRowsForward(java.sql.ResultSet, org.hibernate.engine.spi.SessionImplementor, org.hibernate.engine.spi.QueryParameters, boolean)}
 	 * but dropping the 'returnProxies' (that method has only one use in the entire codebase and it always passes in
 	 * false...)
 	 *
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 *
 	 * @return The extracted result row
 	 *
 	 * @throws org.hibernate.HibernateException Indicates a problem extracting values from the result set.
 	 */
 	public Object extractSequentialRowsForward(
 			final ResultSet resultSet,
 			final SessionImplementor session,
-			final QueryParameters queryParameters) throws HibernateException;
+			final QueryParameters queryParameters);
 
 	/**
 	 * Like {@link #extractSequentialRowsForward} but here moving back through the ResultSet.
 	 *
-	 * Copy of {@link org.hibernate.loader.Loader#loadSequentialRowsReverse(ResultSet, SessionImplementor, QueryParameters, boolean, boolean)}
+	 * Copy of {@link org.hibernate.loader.Loader#loadSequentialRowsReverse(java.sql.ResultSet, org.hibernate.engine.spi.SessionImplementor, org.hibernate.engine.spi.QueryParameters, boolean, boolean)}
 	 * but dropping the 'returnProxies' (that method has only one use in the entire codebase and it always passes in
 	 * false...).
 	 *
 	 * todo : is 'logicallyAfterLastRow really needed?  Can't that be deduced?  In fact pretty positive it is not needed.
 	 *
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 * @param isLogicallyAfterLast Is the result set currently positioned after the last row; again, is this really needed?  How is it any diff
 	 *
 	 * @return The extracted result row
 	 *
 	 * @throws org.hibernate.HibernateException Indicates a problem extracting values from the result set.
 	 */
 	public Object extractSequentialRowsReverse(
 			ResultSet resultSet,
 			SessionImplementor session,
 			QueryParameters queryParameters,
-			boolean isLogicallyAfterLast) throws HibernateException;
+			boolean isLogicallyAfterLast);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultBuilder.java
new file mode 100644
index 0000000000..157e6e22d8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultBuilder.java
@@ -0,0 +1,7 @@
+package org.hibernate.loader.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ResultBuilder {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessingContext.java
new file mode 100644
index 0000000000..6af2fb8da1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessingContext.java
@@ -0,0 +1,91 @@
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
+package org.hibernate.loader.spi;
+
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.List;
+
+import org.hibernate.LockMode;
+import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.plan.spi.EntityReference;
+import org.hibernate.loader.plan.spi.Return;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.AssociationType;
+import org.hibernate.type.EntityType;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface ResultSetProcessingContext {
+	public SessionImplementor getSession();
+
+	public QueryParameters getQueryParameters();
+
+	public EntityKey getDictatedRootEntityKey();
+
+	public IdentifierResolutionContext getIdentifierResolutionContext(EntityReference entityReference);
+
+	public static interface IdentifierResolutionContext {
+		public EntityReference getEntityReference();
+
+		public void registerHydratedForm(Serializable hydratedForm);
+
+		public Serializable getHydratedForm();
+
+		public void registerEntityKey(EntityKey entityKey);
+
+		public EntityKey getEntityKey();
+	}
+
+	public void registerHydratedEntity(EntityPersister persister, EntityKey entityKey, Object entityInstance);
+
+	public void checkVersion(
+			ResultSet resultSet,
+			EntityPersister persister,
+			EntityAliases entityAliases,
+			EntityKey entityKey,
+			Object entityInstance) throws SQLException;
+
+	public String getConcreteEntityTypeName(
+			ResultSet resultSet,
+			EntityPersister persister,
+			EntityAliases entityAliases,
+			EntityKey entityKey) throws SQLException;
+
+	public void loadFromResultSet(
+			ResultSet resultSet,
+			Object entityInstance,
+			String concreteEntityTypeName,
+			EntityKey entityKey,
+			EntityAliases entityAliases,
+			LockMode acquiredLockMode,
+			EntityPersister persister,
+			boolean eagerFetch,
+			EntityType associationType) throws SQLException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessor.java
new file mode 100644
index 0000000000..4dc630c469
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/spi/ResultSetProcessor.java
@@ -0,0 +1,70 @@
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
+package org.hibernate.loader.spi;
+
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.List;
+
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.transform.ResultTransformer;
+
+/**
+ * Contract for processing JDBC ResultSets.  Separated because ResultSets can be chained and we'd really like to
+ * reuse this logic across all result sets.
+ * <p/>
+ * todo : investigate having this work with non-JDBC results; maybe just typed as Object? or a special Result contract?
+ *
+ * @author Steve Ebersole
+ */
+public interface ResultSetProcessor {
+
+	public OnDemandResultSetProcessor toOnDemandForm();
+
+	/**
+	 * Process an entire ResultSet, performing all extractions.
+	 *
+	 * Semi-copy of {@link org.hibernate.loader.Loader#doQuery}, with focus on just the ResultSet processing bit.
+	 *
+	 * @param resultSet The result set being processed.
+	 * @param session The originating session
+	 * @param queryParameters The "parameters" used to build the query
+	 * @param returnProxies Can proxies be returned (not the same as can they be created!)
+	 * @param forcedResultTransformer My old "friend" ResultTransformer...
+	 *
+	 * @return The extracted results list.
+	 *
+	 * @throws java.sql.SQLException Indicates a problem access the JDBC ResultSet
+	 */
+	public List extractResults(
+			ResultSet resultSet,
+			SessionImplementor session,
+			QueryParameters queryParameters,
+			NamedParameterContext namedParameterContext,
+			boolean returnProxies,
+			boolean readOnly,
+			ResultTransformer forcedResultTransformer,
+			List<AfterLoadAction> afterLoadActions) throws SQLException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 0db82ac72c..3503962eea 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,2018 +1,2018 @@
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
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
-import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
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
 			cacheEntryStructure = collection.isMap() ?
 					new StructuredMapCacheEntry() :
 					new StructuredCollectionCacheEntry();
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
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
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
 
 	public int getBatchSize() {
 		return batchSize;
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
-			public CompositeDefinition toCompositeDefinition() {
+			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection index type as composite" );
 				}
 				// todo : implement
 				throw new NotYetImplementedException();
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
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat composite collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
-			public CompositeDefinition toCompositeDefinition() {
+			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
 				}
 				// todo : implement
 				throw new NotYetImplementedException();
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 5456404f04..5f4c423a2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1112 +1,1117 @@
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
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
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
 import org.hibernate.cfg.NotYetImplementedException;
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
 import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeSource;
+import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
+import org.hibernate.persister.walking.spi.NonEncapsulatedEntityIdentifierDefinition;
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
 
 	private final Set affectingFetchProfileNames = new HashSet();
 
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
 			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
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
 
 	protected boolean canUseReferenceCacheEntries() {
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
@@ -4079,1103 +4084,1137 @@ public abstract class AbstractEntityPersister
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
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
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					return (Serializable) getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
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
 	
 	public static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equalsIgnoreCase( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
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
 
 	@Override
 	public String getTableAliasForColumn(String columnName, String rootAlias) {
 		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
 	}
 
 	public int determineTableNumberForColumn(String columnName) {
 		return 0;
 	}
 
 	/**
 	 * Consolidated these onto a single helper because the 2 pieces work in tandem.
 	 */
 	public static interface CacheEntryHelper {
 		public CacheEntryStructure getCacheEntryStructure();
 
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 	}
 
 	private static class StandardCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private StandardCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class ReferenceCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private ReferenceCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new ReferenceCacheEntryImpl( entity, persister.getEntityName() );
 		}
 	}
 
 	private static class StructuredCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 		private final StructuredCacheEntry structure;
 
 		private StructuredCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 			this.structure = new StructuredCacheEntry( persister );
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return structure;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class NoopCacheEntryHelper implements CacheEntryHelper {
 		public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			throw new HibernateException( "Illegal attempt to build cache entry for non-cached entity" );
 		}
 	}
 
 
 	// EntityDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	private EntityIdentifierDefinition entityIdentifierDefinition;
 	private Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes;
 	private Iterable<AttributeDefinition> attributeDefinitions;
 
 	protected void generateEntityDefinition() {
-		collectEmbeddedCompositeIdentifierAttributeDefinitions();
+		prepareEntityIdentifierDefinition();
 		collectAttributeDefinitions();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
-	public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes() {
-		return embeddedCompositeIdentifierAttributes;
+	public EntityIdentifierDefinition getEntityKeyDefinition() {
+		return entityIdentifierDefinition;
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return attributeDefinitions;
 	}
 
-	private synchronized void collectEmbeddedCompositeIdentifierAttributeDefinitions() {
+
+	private void prepareEntityIdentifierDefinition() {
 		final Type idType = getIdentifierType();
+
 		if ( !idType.isComponentType() ) {
+			entityIdentifierDefinition = buildEncapsulatedIdentifierDefinition();
 			return;
 		}
 
 		final CompositeType cidType = (CompositeType) idType;
 		if ( !cidType.isEmbedded() ) {
+			entityIdentifierDefinition = buildEncapsulatedIdentifierDefinition();
 			return;
 		}
 
-		// we have an embedded composite identifier.  Most likely we need to process the composite
-		// properties separately, although there is an edge case where the identifier is really
-		// a simple identifier (single value) wrapped in a JPA @IdClass or even in the case of a
-		// a simple identifier (single value) wrapped in a Hibernate composite type.
-		//
-		// We really do not have a built-in method to determine that.  However, generally the
-		// persister would report that there is single, physical identifier property which is
-		// explicitly at odds with the notion of "embedded composite".  So we use that for now
-		if ( getEntityMetamodel().getIdentifierProperty().isEmbedded() ) {
-			this.embeddedCompositeIdentifierAttributes = new Iterable<AttributeDefinition>() {
-				@Override
-				public Iterator<AttributeDefinition> iterator() {
-					return new Iterator<AttributeDefinition>() {
-						private final int numberOfAttributes = countSubclassProperties();
-						private int currentAttributeNumber = 0;
-
-						@Override
-						public boolean hasNext() {
-							return currentAttributeNumber < numberOfAttributes;
-						}
+		entityIdentifierDefinition = new NonEncapsulatedEntityIdentifierDefinition() {
+			@Override
+			public Iterable<AttributeDefinition> getAttributes() {
+				// todo : implement
+				throw new NotYetImplementedException();
+			}
 
-						@Override
-						public AttributeDefinition next() {
-							// todo : implement
-							throw new NotYetImplementedException();
-						}
+			@Override
+			public Class getSeparateIdentifierMappingClass() {
+				// todo : implement
+				throw new NotYetImplementedException();
+			}
 
-						@Override
-						public void remove() {
-							throw new UnsupportedOperationException( "Remove operation not supported here" );
-						}
-					};
-				}
-			};
-		}
+			@Override
+			public boolean isEncapsulated() {
+				return false;
+			}
+
+			@Override
+			public EntityDefinition getEntityDefinition() {
+				return AbstractEntityPersister.this;
+			}
+		};
+	}
+
+	private EntityIdentifierDefinition buildEncapsulatedIdentifierDefinition() {
+		final AttributeDefinition simpleIdentifierAttributeAdapter = new AttributeDefinition() {
+			@Override
+			public String getName() {
+				return entityMetamodel.getIdentifierProperty().getName();
+			}
+
+			@Override
+			public Type getType() {
+				return entityMetamodel.getIdentifierProperty().getType();
+			}
+
+			@Override
+			public AttributeSource getSource() {
+				return AbstractEntityPersister.this;
+			}
+
+			@Override
+			public String toString() {
+				return "<identifier-property:" + getName() + ">";
+			}
+		};
+
+		return new EncapsulatedEntityIdentifierDefinition() {
+			@Override
+			public AttributeDefinition getAttributeDefinition() {
+				return simpleIdentifierAttributeAdapter;
+			}
+
+			@Override
+			public boolean isEncapsulated() {
+				return true;
+			}
+
+			@Override
+			public EntityDefinition getEntityDefinition() {
+				return AbstractEntityPersister.this;
+			}
+		};
 	}
 
 	private void collectAttributeDefinitions() {
 		// todo : leverage the attribute definitions housed on EntityMetamodel
 		// 		for that to work, we'd have to be able to walk our super entity persister(s)
 		attributeDefinitions = new Iterable<AttributeDefinition>() {
 			@Override
 			public Iterator<AttributeDefinition> iterator() {
 				return new Iterator<AttributeDefinition>() {
 //					private final int numberOfAttributes = countSubclassProperties();
 					private final int numberOfAttributes = entityMetamodel.getPropertySpan();
 					private int currentAttributeNumber = 0;
 
 					@Override
 					public boolean hasNext() {
 						return currentAttributeNumber < numberOfAttributes;
 					}
 
 					@Override
 					public AttributeDefinition next() {
 						final int attributeNumber = currentAttributeNumber;
 						currentAttributeNumber++;
 						return entityMetamodel.getProperties()[ attributeNumber ];
 					}
 
 					@Override
 					public void remove() {
 						throw new UnsupportedOperationException( "Remove operation not supported here" );
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/HydratedCompoundValueHandler.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/HydratedCompoundValueHandler.java
new file mode 100644
index 0000000000..4c723a6b2f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/HydratedCompoundValueHandler.java
@@ -0,0 +1,44 @@
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
+package org.hibernate.persister.spi;
+
+/**
+ * Where to begin... :)
+ *
+ * This gets to the internal concept of 2-phase loading of entity data and how specifically it is done.  Essentially
+ * for composite values, the process of hydration results in a tuple array comprising the composition "atomic" values.
+ * For example, a Name component's hydrated state might look like {@code ["Steve", "L", "Ebersole"]}.
+ *
+ * There are times when we need to be able to extract individual pieces out of the hydrated tuple array.  For example,
+ * for an entity with a composite identifier part of which is an association (a key-many-to-one) we often need to
+ * attempt 2-phase processing on the association portion of the identifier's hydrated tuple array.
+ *
+ * This contract allows us access to portions of the hydrated tuple state.
+ *
+ * @author Steve Ebersole
+ */
+public interface HydratedCompoundValueHandler {
+	public Object extract(Object hydratedState);
+	public void inject(Object hydratedState, Object value);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationAttributeDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationAttributeDefinition.java
index 383b3d4e38..719eb51803 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationAttributeDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationAttributeDefinition.java
@@ -1,46 +1,49 @@
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
 package org.hibernate.persister.walking.spi;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 
 /**
  * @author Steve Ebersole
  */
 public interface AssociationAttributeDefinition extends AttributeDefinition {
 	public AssociationKey getAssociationKey();
 
 	public boolean isCollection();
 
 	public EntityDefinition toEntityDefinition();
 
 	public CollectionDefinition toCollectionDefinition();
 
 	public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath);
 
 	public CascadeStyle determineCascadeStyle();
+
+	public HydratedCompoundValueHandler getHydratedCompoundValueExtractor();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
index 1cb649b0d8..285ec22270 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
@@ -1,51 +1,60 @@
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
 package org.hibernate.persister.walking.spi;
 
 /**
  * @author Steve Ebersole
  */
 public interface AssociationVisitationStrategy {
 	/**
 	 * Notification we are preparing to start visitation.
 	 */
 	public void start();
 
 	/**
 	 * Notification we are finished visitation.
 	 */
 	public void finish();
 
 	public void startingEntity(EntityDefinition entityDefinition);
 	public void finishingEntity(EntityDefinition entityDefinition);
 
+	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
+	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
+
 	public void startingCollection(CollectionDefinition collectionDefinition);
 	public void finishingCollection(CollectionDefinition collectionDefinition);
 
-	public void startingComposite(CompositeDefinition compositeDefinition);
-	public void finishingComposite(CompositeDefinition compositeDefinition);
+	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
+	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
+
+	public void startingCollectionElements(CollectionElementDefinition elementDefinition);
+	public void finishingCollectionElements(CollectionElementDefinition elementDefinition);
+
+	public void startingComposite(CompositionDefinition compositionDefinition);
+	public void finishingComposite(CompositionDefinition compositionDefinition);
 
 	public boolean startingAttribute(AttributeDefinition attributeDefinition);
 	public void finishingAttribute(AttributeDefinition attributeDefinition);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
index fd48366c3a..b42f8b0ddc 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionElementDefinition.java
@@ -1,39 +1,39 @@
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
 package org.hibernate.persister.walking.spi;
 
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public interface CollectionElementDefinition {
 	public CollectionDefinition getCollectionDefinition();
 
 	public Type getType();
 
 	public EntityDefinition toEntityDefinition();
 
-	public CompositeDefinition toCompositeDefinition();
+	public CompositionDefinition toCompositeDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
index 700e2b5070..ece06e6e36 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CollectionIndexDefinition.java
@@ -1,39 +1,39 @@
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
 package org.hibernate.persister.walking.spi;
 
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public interface CollectionIndexDefinition {
 	public CollectionDefinition getCollectionDefinition();
 
 	public Type getType();
 
 	public EntityDefinition toEntityDefinition();
 
-	public CompositeDefinition toCompositeDefinition();
+	public CompositionDefinition toCompositeDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionDefinition.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionDefinition.java
index 51c88333f4..e9d7c251cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositeDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionDefinition.java
@@ -1,30 +1,30 @@
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
 package org.hibernate.persister.walking.spi;
 
 /**
  * @author Steve Ebersole
  */
-public interface CompositeDefinition extends AttributeDefinition, AttributeSource {
+public interface CompositionDefinition extends AttributeDefinition, AttributeSource {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EncapsulatedEntityIdentifierDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EncapsulatedEntityIdentifierDefinition.java
new file mode 100644
index 0000000000..657f2da50d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EncapsulatedEntityIdentifierDefinition.java
@@ -0,0 +1,31 @@
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
+package org.hibernate.persister.walking.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface EncapsulatedEntityIdentifierDefinition extends EntityIdentifierDefinition {
+	public AttributeDefinition getAttributeDefinition();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityDefinition.java
index 1e02b42106..38faf26c4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityDefinition.java
@@ -1,36 +1,36 @@
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
 package org.hibernate.persister.walking.spi;
 
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Defines the contract for walking the attributes defined by an entity
  *
  * @author Steve Ebersole
  */
 public interface EntityDefinition extends AttributeSource {
 	public EntityPersister getEntityPersister();
-	public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes();
+	public EntityIdentifierDefinition getEntityKeyDefinition();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityIdentifierDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityIdentifierDefinition.java
new file mode 100644
index 0000000000..8ecb616a64
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/EntityIdentifierDefinition.java
@@ -0,0 +1,43 @@
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
+package org.hibernate.persister.walking.spi;
+
+/**
+ * Describes aspects of the identifier for an entity
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityIdentifierDefinition {
+	/**
+	 * Is the entity identifier encapsulated?  Meaning, is it represented by a single attribute?
+	 *
+	 * @return {@code true} indicates the identifier is encapsulated (and therefore this is castable to
+	 * {@link EncapsulatedEntityIdentifierDefinition}); {@code false} means it is not encapsulated (and therefore
+	 * castable to {@link NonEncapsulatedEntityIdentifierDefinition}).
+	 *
+	 */
+	public boolean isEncapsulated();
+
+	public EntityDefinition getEntityDefinition();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
index ccbbceb6bb..dd205aa9e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
@@ -1,208 +1,216 @@
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
 package org.hibernate.persister.walking.spi;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Provides model graph visitation based on the defined metadata (as opposed to based on the incoming graph
  * as we see in cascade processing).  In layman terms, we are walking the graph of the users model as defined by
  * mapped associations.
  * <p/>
  * Re-implementation of the legacy {@link org.hibernate.loader.JoinWalker} contract to leverage load plans.
  *
  * @author Steve Ebersole
  */
 public class MetadataDrivenModelGraphVisitor {
 	private static final Logger log = Logger.getLogger( MetadataDrivenModelGraphVisitor.class );
 
 	public static void visitEntity(AssociationVisitationStrategy strategy, EntityPersister persister) {
 		strategy.start();
 		try {
 			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
 					.visitEntityDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	public static void visitCollection(AssociationVisitationStrategy strategy, CollectionPersister persister) {
 		strategy.start();
 		try {
 			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
 					.visitCollectionDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	private final AssociationVisitationStrategy strategy;
 	private final SessionFactoryImplementor factory;
 
 	// todo : add a getDepth() method to PropertyPath
 	private PropertyPath currentPropertyPath = new PropertyPath();
 
 	public MetadataDrivenModelGraphVisitor(AssociationVisitationStrategy strategy, SessionFactoryImplementor factory) {
 		this.strategy = strategy;
 		this.factory = factory;
 	}
 
 	private void visitEntityDefinition(EntityDefinition entityDefinition) {
 		strategy.startingEntity( entityDefinition );
-		try {
-			visitAttributes( entityDefinition );
-			optionallyVisitEmbeddedCompositeIdentifier( entityDefinition );
-		}
-		finally {
-			strategy.finishingEntity( entityDefinition );
-		}
+
+		visitAttributes( entityDefinition );
+		visitIdentifierDefinition( entityDefinition.getEntityKeyDefinition() );
+
+		strategy.finishingEntity( entityDefinition );
 	}
 
-	private void optionallyVisitEmbeddedCompositeIdentifier(EntityDefinition entityDefinition) {
-		// if the entity has a composite identifier, see if we need to handle its sub-properties separately
-		final Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes =
-				entityDefinition.getEmbeddedCompositeIdentifierAttributes();
-		if ( embeddedCompositeIdentifierAttributes == null ) {
-			return;
-		}
+	private void visitIdentifierDefinition(EntityIdentifierDefinition entityIdentifierDefinition) {
+		strategy.startingEntityIdentifier( entityIdentifierDefinition );
 
-		for ( AttributeDefinition attributeDefinition : embeddedCompositeIdentifierAttributes ) {
-			visitAttributeDefinition( attributeDefinition );
+		if ( entityIdentifierDefinition.isEncapsulated() ) {
+			visitAttributeDefinition( ( (EncapsulatedEntityIdentifierDefinition) entityIdentifierDefinition).getAttributeDefinition() );
 		}
+		else {
+			for ( AttributeDefinition attributeDefinition : ( (NonEncapsulatedEntityIdentifierDefinition) entityIdentifierDefinition).getAttributes() ) {
+				visitAttributeDefinition( attributeDefinition );
+			}
+		}
+
+		strategy.finishingEntityIdentifier( entityIdentifierDefinition );
 	}
 
 	private void visitAttributes(AttributeSource attributeSource) {
 		for ( AttributeDefinition attributeDefinition : attributeSource.getAttributes() ) {
 			visitAttributeDefinition( attributeDefinition );
 		}
 	}
 
 	private void visitAttributeDefinition(AttributeDefinition attributeDefinition) {
 		final PropertyPath subPath = currentPropertyPath.append( attributeDefinition.getName() );
 		log.debug( "Visiting attribute path : " + subPath.getFullPath() );
 
 		final boolean continueWalk = strategy.startingAttribute( attributeDefinition );
 		if ( continueWalk ) {
 			final PropertyPath old = currentPropertyPath;
 			currentPropertyPath = subPath;
 			try {
 				if ( attributeDefinition.getType().isAssociationType() ) {
 					visitAssociation( (AssociationAttributeDefinition) attributeDefinition );
 				}
 				else if ( attributeDefinition.getType().isComponentType() ) {
-					visitCompositeDefinition( (CompositeDefinition) attributeDefinition );
+					visitCompositeDefinition( (CompositionDefinition) attributeDefinition );
 				}
 			}
 			finally {
 				currentPropertyPath = old;
 			}
 		}
 		strategy.finishingAttribute( attributeDefinition );
 	}
 
 	private void visitAssociation(AssociationAttributeDefinition attribute) {
 		// todo : do "too deep" checks; but see note about adding depth to PropertyPath
 
 		if ( isDuplicateAssociation( attribute.getAssociationKey() ) ) {
 			log.debug( "Property path deemed to be circular : " + currentPropertyPath.getFullPath() );
 			return;
 		}
 
 		if ( attribute.isCollection() ) {
 			visitCollectionDefinition( attribute.toCollectionDefinition() );
 		}
 		else {
 			visitEntityDefinition( attribute.toEntityDefinition() );
 		}
 	}
 
-	private void visitCompositeDefinition(CompositeDefinition compositeDefinition) {
-		strategy.startingComposite( compositeDefinition );
-		try {
-			visitAttributes( compositeDefinition );
-		}
-		finally {
-			strategy.finishingComposite( compositeDefinition );
-		}
+	private void visitCompositeDefinition(CompositionDefinition compositionDefinition) {
+		strategy.startingComposite( compositionDefinition );
+
+		visitAttributes( compositionDefinition );
+
+		strategy.finishingComposite( compositionDefinition );
 	}
 
 	private void visitCollectionDefinition(CollectionDefinition collectionDefinition) {
 		strategy.startingCollection( collectionDefinition );
 
-		try {
-			visitCollectionIndex( collectionDefinition.getIndexDefinition() );
+		visitCollectionIndex( collectionDefinition );
+		visitCollectionElements( collectionDefinition );
 
-			final CollectionElementDefinition elementDefinition = collectionDefinition.getElementDefinition();
-			if ( elementDefinition.getType().isComponentType() ) {
-				visitCompositeDefinition( elementDefinition.toCompositeDefinition() );
-			}
-			else {
-				visitEntityDefinition( elementDefinition.toEntityDefinition() );
-			}
-		}
-		finally {
-			strategy.finishingCollection( collectionDefinition );
-		}
+		strategy.finishingCollection( collectionDefinition );
 	}
 
-	private void visitCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+	private void visitCollectionIndex(CollectionDefinition collectionDefinition) {
+		final CollectionIndexDefinition collectionIndexDefinition = collectionDefinition.getIndexDefinition();
 		if ( collectionIndexDefinition == null ) {
 			return;
 		}
 
-		log.debug( "Visiting collection index :  " + currentPropertyPath.getFullPath() );
-		currentPropertyPath = currentPropertyPath.append( "<key>" );
+		strategy.startingCollectionIndex( collectionIndexDefinition );
+
+		log.debug( "Visiting index for collection :  " + currentPropertyPath.getFullPath() );
+		currentPropertyPath = currentPropertyPath.append( "<index>" );
+
 		try {
 			final Type collectionIndexType = collectionIndexDefinition.getType();
 			if ( collectionIndexType.isComponentType() ) {
 				visitCompositeDefinition( collectionIndexDefinition.toCompositeDefinition() );
 			}
 			else if ( collectionIndexType.isAssociationType() ) {
 				visitEntityDefinition( collectionIndexDefinition.toEntityDefinition() );
 			}
 		}
 		finally {
 			currentPropertyPath = currentPropertyPath.getParent();
 		}
+
+		strategy.finishingCollectionIndex( collectionIndexDefinition );
+	}
+
+	private void visitCollectionElements(CollectionDefinition collectionDefinition) {
+		final CollectionElementDefinition elementDefinition = collectionDefinition.getElementDefinition();
+		strategy.startingCollectionElements( elementDefinition );
+
+		if ( elementDefinition.getType().isComponentType() ) {
+			visitCompositeDefinition( elementDefinition.toCompositeDefinition() );
+		}
+		else {
+			visitEntityDefinition( elementDefinition.toEntityDefinition() );
+		}
+
+		strategy.finishingCollectionElements( elementDefinition );
 	}
 
 
 	private final Set<AssociationKey> visitedAssociationKeys = new HashSet<AssociationKey>();
 
 	protected boolean isDuplicateAssociation(AssociationKey associationKey) {
 		return !visitedAssociationKeys.add( associationKey );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/NonEncapsulatedEntityIdentifierDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/NonEncapsulatedEntityIdentifierDefinition.java
new file mode 100644
index 0000000000..4a07579867
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/NonEncapsulatedEntityIdentifierDefinition.java
@@ -0,0 +1,33 @@
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
+package org.hibernate.persister.walking.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface NonEncapsulatedEntityIdentifierDefinition extends EntityIdentifierDefinition {
+	public Iterable<AttributeDefinition> getAttributes();
+
+	public Class getSeparateIdentifierMappingClass();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/WalkingException.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/WalkingException.java
new file mode 100644
index 0000000000..881c32c392
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/WalkingException.java
@@ -0,0 +1,41 @@
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
+package org.hibernate.persister.walking.spi;
+
+import org.hibernate.HibernateError;
+
+/**
+ * Indicates a problem walking the domain tree.  Almost always this indicates an internal error in Hibernate
+ *
+ * @author Steve Ebersole
+ */
+public class WalkingException extends HibernateError {
+	public WalkingException(String message) {
+		super( message );
+	}
+
+	public WalkingException(String message, Throwable root) {
+		super( message, root );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/result/internal/ResultImpl.java b/hibernate-core/src/main/java/org/hibernate/result/internal/ResultImpl.java
index fe405ec56f..9377303efa 100644
--- a/hibernate-core/src/main/java/org/hibernate/result/internal/ResultImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/result/internal/ResultImpl.java
@@ -1,292 +1,293 @@
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
 package org.hibernate.result.internal;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.JDBCException;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.loader.custom.sql.SQLQueryReturnProcessor;
+import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.result.NoMoreReturnsException;
 import org.hibernate.result.Result;
 import org.hibernate.result.Return;
 import org.hibernate.result.spi.ResultContext;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultImpl implements Result {
 	private final ResultContext context;
 	private final PreparedStatement jdbcStatement;
 	private final CustomLoaderExtension loader;
 
 	private CurrentReturnDescriptor currentReturnDescriptor;
 
 	private boolean executed = false;
 
 	public ResultImpl(ResultContext context, PreparedStatement jdbcStatement) {
 		this.context = context;
 		this.jdbcStatement = jdbcStatement;
 
 		// For now...
 		this.loader = buildSpecializedCustomLoader( context );
 	}
 
 	@Override
 	public boolean hasMoreReturns() {
 		if ( currentReturnDescriptor == null ) {
 			final boolean isResultSet;
 
 			if ( executed ) {
 				try {
 					isResultSet = jdbcStatement.getMoreResults();
 				}
 				catch (SQLException e) {
 					throw convert( e, "Error calling CallableStatement.getMoreResults" );
 				}
 			}
 			else {
 				try {
 					isResultSet = jdbcStatement.execute();
 				}
 				catch (SQLException e) {
 					throw convert( e, "Error calling CallableStatement.execute" );
 				}
 				executed = true;
 			}
 
 			int updateCount = -1;
 			if ( ! isResultSet ) {
 				try {
 					updateCount = jdbcStatement.getUpdateCount();
 				}
 				catch (SQLException e) {
 					throw convert( e, "Error calling CallableStatement.getUpdateCount" );
 				}
 			}
 
 			currentReturnDescriptor = buildCurrentReturnDescriptor( isResultSet, updateCount );
 		}
 
 		return hasMoreReturns( currentReturnDescriptor );
 	}
 
 	protected CurrentReturnDescriptor buildCurrentReturnDescriptor(boolean isResultSet, int updateCount) {
 		return new CurrentReturnDescriptor( isResultSet, updateCount );
 	}
 
 	protected boolean hasMoreReturns(CurrentReturnDescriptor descriptor) {
 		return descriptor.isResultSet
 				|| descriptor.updateCount >= 0;
 	}
 
 	@Override
 	public Return getNextReturn() {
 		if ( currentReturnDescriptor == null ) {
 			if ( executed ) {
 				throw new IllegalStateException( "Unexpected condition" );
 			}
 			else {
 				throw new IllegalStateException( "hasMoreReturns() not called before getNextReturn()" );
 			}
 		}
 
 		if ( ! hasMoreReturns( currentReturnDescriptor ) ) {
 			throw new NoMoreReturnsException( "Results have been exhausted" );
 		}
 
 		CurrentReturnDescriptor copyReturnDescriptor = currentReturnDescriptor;
 		currentReturnDescriptor = null;
 
 		if ( copyReturnDescriptor.isResultSet ) {
 			try {
 				return new ResultSetReturn( this, jdbcStatement.getResultSet() );
 			}
 			catch (SQLException e) {
 				throw convert( e, "Error calling CallableStatement.getResultSet" );
 			}
 		}
 		else if ( copyReturnDescriptor.updateCount >= 0 ) {
 			return new UpdateCountReturn( this, copyReturnDescriptor.updateCount );
 		}
 		else {
 			return buildExtendedReturn( copyReturnDescriptor );
 		}
 	}
 
 	protected Return buildExtendedReturn(CurrentReturnDescriptor copyReturnDescriptor) {
 		throw new NoMoreReturnsException( "Results have been exhausted" );
 	}
 
 	protected JDBCException convert(SQLException e, String message) {
 		return context.getSession().getFactory().getSQLExceptionHelper().convert(
 				e,
 				message,
 				context.getSql()
 		);
 	}
 
 	protected static class CurrentReturnDescriptor {
 		private final boolean isResultSet;
 		private final int updateCount;
 
 		protected CurrentReturnDescriptor(boolean isResultSet, int updateCount) {
 			this.isResultSet = isResultSet;
 			this.updateCount = updateCount;
 		}
 	}
 
 	protected static class ResultSetReturn implements org.hibernate.result.ResultSetReturn {
 		private final ResultImpl storedProcedureOutputs;
 		private final ResultSet resultSet;
 
 		public ResultSetReturn(ResultImpl storedProcedureOutputs, ResultSet resultSet) {
 			this.storedProcedureOutputs = storedProcedureOutputs;
 			this.resultSet = resultSet;
 		}
 
 		@Override
 		public boolean isResultSet() {
 			return true;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public List getResultList() {
 			try {
 				return storedProcedureOutputs.loader.processResultSet( resultSet );
 			}
 			catch (SQLException e) {
 				throw storedProcedureOutputs.convert( e, "Error calling ResultSet.next" );
 			}
 		}
 
 		@Override
 		public Object getSingleResult() {
 			List results = getResultList();
 			if ( results == null || results.isEmpty() ) {
 				return null;
 			}
 			else {
 				return results.get( 0 );
 			}
 		}
 	}
 
 	protected static class UpdateCountReturn implements org.hibernate.result.UpdateCountReturn {
 		private final ResultImpl result;
 		private final int updateCount;
 
 		public UpdateCountReturn(ResultImpl result, int updateCount) {
 			this.result = result;
 			this.updateCount = updateCount;
 		}
 
 		@Override
 		public int getUpdateCount() {
 			return updateCount;
 		}
 
 		@Override
 		public boolean isResultSet() {
 			return false;
 		}
 	}
 
 	private static CustomLoaderExtension buildSpecializedCustomLoader(final ResultContext context) {
 		final SQLQueryReturnProcessor processor = new SQLQueryReturnProcessor(
 				context.getQueryReturns(),
 				context.getSession().getFactory()
 		);
 		processor.process();
 		final List<org.hibernate.loader.custom.Return> customReturns = processor.generateCustomReturns( false );
 
 		CustomQuery customQuery = new CustomQuery() {
 			@Override
 			public String getSQL() {
 				return context.getSql();
 			}
 
 			@Override
 			public Set<String> getQuerySpaces() {
 				return context.getSynchronizedQuerySpaces();
 			}
 
 			@Override
 			public Map getNamedParameterBindPoints() {
 				// no named parameters in terms of embedded in the SQL string
 				return null;
 			}
 
 			@Override
 			public List<org.hibernate.loader.custom.Return> getCustomQueryReturns() {
 				return customReturns;
 			}
 		};
 
 		return new CustomLoaderExtension(
 				customQuery,
 				context.getQueryParameters(),
 				context.getSession()
 		);
 	}
 
 	private static class CustomLoaderExtension extends CustomLoader {
 		private QueryParameters queryParameters;
 		private SessionImplementor session;
 
 		public CustomLoaderExtension(
 				CustomQuery customQuery,
 				QueryParameters queryParameters,
 				SessionImplementor session) {
 			super( customQuery, session.getFactory() );
 			this.queryParameters = queryParameters;
 			this.session = session;
 		}
 
 		// todo : this would be a great way to add locking to stored procedure support (at least where returning entities).
 
 		public List processResultSet(ResultSet resultSet) throws SQLException {
 			super.autoDiscoverTypes( resultSet );
 			return super.processResultSet(
 					resultSet,
 					queryParameters,
 					session,
 					true,
 					null,
 					Integer.MAX_VALUE,
 					Collections.<AfterLoadAction>emptyList()
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/AbstractNonIdentifierAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/AbstractNonIdentifierAttribute.java
index b01458fe64..9b9a161370 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/AbstractNonIdentifierAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/AbstractNonIdentifierAttribute.java
@@ -1,133 +1,137 @@
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
 package org.hibernate.tuple;
 
 import org.hibernate.FetchMode;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractNonIdentifierAttribute extends AbstractAttribute implements NonIdentifierAttribute {
 	private final AttributeSource source;
 	private final SessionFactoryImplementor sessionFactory;
 
 	private final int attributeNumber;
 
 	private final BaselineAttributeInformation attributeInformation;
 
 	protected AbstractNonIdentifierAttribute(
 			AttributeSource source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			Type attributeType,
 			BaselineAttributeInformation attributeInformation) {
 		super( attributeName, attributeType );
 		this.source = source;
 		this.sessionFactory = sessionFactory;
 		this.attributeNumber = attributeNumber;
 		this.attributeInformation = attributeInformation;
 	}
 
 	@Override
 	public AttributeSource getSource() {
 		return source();
 	}
 
 	protected AttributeSource source() {
 		return source;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected int attributeNumber() {
 		return attributeNumber;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return attributeInformation.isLazy();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return attributeInformation.isInsertable();
 	}
 
 	@Override
 	public boolean isUpdateable() {
 		return attributeInformation.isUpdateable();
 	}
 
 	@Override
 	public boolean isInsertGenerated() {
 		return attributeInformation.isInsertGenerated();
 	}
 
 	@Override
 	public boolean isUpdateGenerated() {
 		return attributeInformation.isUpdateGenerated();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return attributeInformation.isNullable();
 	}
 
 	@Override
 	public boolean isDirtyCheckable() {
 		return attributeInformation.isDirtyCheckable();
 	}
 
 	@Override
 	public boolean isDirtyCheckable(boolean hasUninitializedProperties) {
 		return isDirtyCheckable() && ( !hasUninitializedProperties || !isLazy() );
 	}
 
 	@Override
 	public boolean isVersionable() {
 		return attributeInformation.isVersionable();
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle() {
 		return attributeInformation.getCascadeStyle();
 	}
 
 	@Override
 	public FetchMode getFetchMode() {
 		return attributeInformation.getFetchMode();
 	}
 
+	protected String loggableMetadata() {
+		return "non-identifier";
+	}
+
 	@Override
 	public String toString() {
-		return "Attribute[non-identifier]( " + getName() + ")";
+		return "Attribute(name=" + getName() + ", type=" + getType().getName() + " [" + loggableMetadata() + "])";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
index 8db4f2048e..977ab5350c 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/PropertyFactory.java
@@ -1,528 +1,528 @@
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
 package org.hibernate.tuple;
 
 import java.lang.reflect.Constructor;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.internal.UnsavedValueFactory;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.IdentifierValue;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.VersionValue;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AbstractPluralAttributeBinding;
 import org.hibernate.metamodel.binding.AssociationAttributeBinding;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleValueBinding;
 import org.hibernate.metamodel.binding.SingularAttributeBinding;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.property.PropertyAccessorFactory;
 import org.hibernate.tuple.entity.EntityBasedAssociationAttribute;
 import org.hibernate.tuple.entity.EntityBasedBasicAttribute;
-import org.hibernate.tuple.entity.EntityBasedCompositeAttribute;
+import org.hibernate.tuple.entity.EntityBasedCompositionAttribute;
 import org.hibernate.tuple.entity.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Responsible for generation of runtime metamodel {@link Property} representations.
  * Makes distinction between identifier, version, and other (standard) properties.
  *
  * @author Steve Ebersole
  */
 public class PropertyFactory {
 	/**
 	 * Generates the attribute representation of the identifier for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierAttribute(
 			PersistentClass mappedEntity,
 			IdentifierGenerator generator) {
 		String mappedUnsavedValue = mappedEntity.getIdentifier().getNullValue();
 		Type type = mappedEntity.getIdentifier().getType();
 		Property property = mappedEntity.getIdentifierProperty();
 		
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor(mappedEntity)
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					mappedEntity.hasIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getName(),
 					property.getNodeName(),
 					type,
 					mappedEntity.hasEmbeddedIdentifier(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates an IdentifierProperty representation of the for a given entity mapping.
 	 *
 	 * @param mappedEntity The mapping definition of the entity.
 	 * @param generator The identifier value generator to use for this identifier.
 	 * @return The appropriate IdentifierProperty definition.
 	 */
 	public static IdentifierProperty buildIdentifierProperty(
 			EntityBinding mappedEntity,
 			IdentifierGenerator generator) {
 
 		final BasicAttributeBinding property = mappedEntity.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 
 		// TODO: the following will cause an NPE with "virtual" IDs; how should they be set?
 		// (steve) virtual attributes will still be attributes, they will simply be marked as virtual.
 		//		see org.hibernate.metamodel.domain.AbstractAttributeContainer.locateOrCreateVirtualAttribute()
 
 		final String mappedUnsavedValue = property.getUnsavedValue();
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		IdentifierValue unsavedValue = UnsavedValueFactory.getUnsavedIdentifierValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				type,
 				getConstructor( mappedEntity )
 			);
 
 		if ( property == null ) {
 			// this is a virtual id property...
 			return new IdentifierProperty(
 			        type,
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isIdentifierMapper(),
 					unsavedValue,
 					generator
 				);
 		}
 		else {
 			return new IdentifierProperty(
 					property.getAttribute().getName(),
 					null,
 					type,
 					mappedEntity.getHierarchyDetails().getEntityIdentifier().isEmbedded(),
 					unsavedValue,
 					generator
 				);
 		}
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(
 			EntityPersister persister,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			Property property,
 			boolean lazyAvailable) {
 		String mappedUnsavedValue = ( (KeyValue) property.getValue() ).getNullValue();
 		
 		VersionValue unsavedValue = UnsavedValueFactory.getUnsavedVersionValue(
 				mappedUnsavedValue,
 				getGetter( property ),
 				(VersionType) property.getType(),
 				getConstructor( property.getPersistentClass() )
 		);
 
 		boolean lazy = lazyAvailable && property.isLazy();
 
 		return new VersionProperty(
 				persister,
 				sessionFactory,
 				attributeNumber,
 		        property.getName(),
 		        property.getValue().getType(),
 				new BaselineAttributeInformation.Builder()
 						.setLazy( lazy )
 						.setInsertable( property.isInsertable() )
 						.setUpdateable( property.isUpdateable() )
 						.setInsertGenerated( property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS )
 						.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
 						.setNullable( property.isOptional() )
 						.setDirtyCheckable( property.isUpdateable() && !lazy )
 						.setVersionable( property.isOptimisticLocked() )
 						.setCascadeStyle( property.getCascadeStyle() )
 						.createInformation(),
 		        unsavedValue
 			);
 	}
 
 	/**
 	 * Generates a VersionProperty representation for an entity mapping given its
 	 * version mapping Property.
 	 *
 	 * @param property The version mapping Property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate VersionProperty definition.
 	 */
 	public static VersionProperty buildVersionProperty(
 			EntityPersister persister,
 			BasicAttributeBinding property,
 			boolean lazyAvailable) {
 		throw new NotYetImplementedException();
 	}
 
 	public static enum NonIdentifierAttributeNature {
 		BASIC,
 		COMPOSITE,
 		ANY,
 		ENTITY,
 		COLLECTION
 	}
 
 	/**
 	 * Generate a non-identifier (and non-version) attribute based on the given mapped property from the given entity
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate NonIdentifierProperty definition.
 	 */
 	public static NonIdentifierAttribute buildEntityBasedAttribute(
 			EntityPersister persister,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			Property property,
 			boolean lazyAvailable) {
 		final Type type = property.getValue().getType();
 
 		final NonIdentifierAttributeNature nature = decode( type );
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 		
 		// we need to dirty check many-to-ones with not-found="ignore" in order 
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 		
 		boolean alwaysDirtyCheck = type.isAssociationType() && 
 				( (AssociationType) type ).isAlwaysDirtyChecked(); 
 
 		switch ( nature ) {
 			case BASIC: {
 				return new EntityBasedBasicAttribute(
 						persister,
 						sessionFactory,
 						attributeNumber,
 						property.getName(),
 						type,
 						new BaselineAttributeInformation.Builder()
 								.setLazy( lazyAvailable && property.isLazy() )
 								.setInsertable( property.isInsertable() )
 								.setUpdateable( property.isUpdateable() )
 								.setInsertGenerated(
 										property.getGeneration() == PropertyGeneration.INSERT
 												|| property.getGeneration() == PropertyGeneration.ALWAYS
 								)
 								.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
 								.setNullable( property.isOptional() )
 								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
 								.setVersionable( property.isOptimisticLocked() )
 								.setCascadeStyle( property.getCascadeStyle() )
 								.setFetchMode( property.getValue().getFetchMode() )
 								.createInformation()
 				);
 			}
 			case COMPOSITE: {
-				return new EntityBasedCompositeAttribute(
+				return new EntityBasedCompositionAttribute(
 						persister,
 						sessionFactory,
 						attributeNumber,
 						property.getName(),
 						(CompositeType) type,
 						new BaselineAttributeInformation.Builder()
 								.setLazy( lazyAvailable && property.isLazy() )
 								.setInsertable( property.isInsertable() )
 								.setUpdateable( property.isUpdateable() )
 								.setInsertGenerated(
 										property.getGeneration() == PropertyGeneration.INSERT
 												|| property.getGeneration() == PropertyGeneration.ALWAYS
 								)
 								.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
 								.setNullable( property.isOptional() )
 								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
 								.setVersionable( property.isOptimisticLocked() )
 								.setCascadeStyle( property.getCascadeStyle() )
 								.setFetchMode( property.getValue().getFetchMode() )
 								.createInformation()
 				);
 			}
 			case ENTITY:
 			case ANY:
 			case COLLECTION: {
 				return new EntityBasedAssociationAttribute(
 						persister,
 						sessionFactory,
 						attributeNumber,
 						property.getName(),
 						(AssociationType) type,
 						new BaselineAttributeInformation.Builder()
 								.setLazy( lazyAvailable && property.isLazy() )
 								.setInsertable( property.isInsertable() )
 								.setUpdateable( property.isUpdateable() )
 								.setInsertGenerated(
 										property.getGeneration() == PropertyGeneration.INSERT
 												|| property.getGeneration() == PropertyGeneration.ALWAYS
 								)
 								.setUpdateGenerated( property.getGeneration() == PropertyGeneration.ALWAYS )
 								.setNullable( property.isOptional() )
 								.setDirtyCheckable( alwaysDirtyCheck || property.isUpdateable() )
 								.setVersionable( property.isOptimisticLocked() )
 								.setCascadeStyle( property.getCascadeStyle() )
 								.setFetchMode( property.getValue().getFetchMode() )
 								.createInformation()
 				);
 			}
 			default: {
 				throw new HibernateException( "Internal error" );
 			}
 		}
 	}
 
 	private static NonIdentifierAttributeNature decode(Type type) {
 		if ( type.isAssociationType() ) {
 			AssociationType associationType = (AssociationType) type;
 
 			if ( type.isComponentType() ) {
 				// an any type is both an association and a composite...
 				return NonIdentifierAttributeNature.ANY;
 			}
 
 			return type.isCollectionType()
 					? NonIdentifierAttributeNature.COLLECTION
 					: NonIdentifierAttributeNature.ENTITY;
 		}
 		else {
 			if ( type.isComponentType() ) {
 				return NonIdentifierAttributeNature.COMPOSITE;
 			}
 
 			return NonIdentifierAttributeNature.BASIC;
 		}
 	}
 
 	@Deprecated
 	public static StandardProperty buildStandardProperty(Property property, boolean lazyAvailable) {
 		final Type type = property.getValue().getType();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		boolean alwaysDirtyCheck = type.isAssociationType() &&
 				( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		return new StandardProperty(
 				property.getName(),
 				type,
 				lazyAvailable && property.isLazy(),
 				property.isInsertable(),
 				property.isUpdateable(),
 				property.getGeneration() == PropertyGeneration.INSERT || property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.getGeneration() == PropertyGeneration.ALWAYS,
 				property.isOptional(),
 				alwaysDirtyCheck || property.isUpdateable(),
 				property.isOptimisticLocked(),
 				property.getCascadeStyle(),
 				property.getValue().getFetchMode()
 		);
 	}
 
 
 	/**
 	 * Generate a "standard" (i.e., non-identifier and non-version) based on the given
 	 * mapped property.
 	 *
 	 * @param property The mapped property.
 	 * @param lazyAvailable Is property lazy loading currently available.
 	 * @return The appropriate NonIdentifierProperty definition.
 	 */
 	public static StandardProperty buildStandardProperty(AttributeBinding property, boolean lazyAvailable) {
 
 		final Type type = property.getHibernateTypeDescriptor().getResolvedTypeMapping();
 
 		// we need to dirty check collections, since they can cause an owner
 		// version number increment
 
 		// we need to dirty check many-to-ones with not-found="ignore" in order
 		// to update the cache (not the database), since in this case a null
 		// entity reference can lose information
 
 		final boolean alwaysDirtyCheck = type.isAssociationType() && ( (AssociationType) type ).isAlwaysDirtyChecked();
 
 		if ( property.getAttribute().isSingular() ) {
 			final SingularAttributeBinding singularAttributeBinding = ( SingularAttributeBinding ) property;
 			final CascadeStyle cascadeStyle = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getCascadeStyle()
 					: CascadeStyles.NONE;
 			final FetchMode fetchMode = singularAttributeBinding.isAssociation()
 					? ( (AssociationAttributeBinding) singularAttributeBinding ).getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					singularAttributeBinding.getAttribute().getName(),
 					type,
 					lazyAvailable && singularAttributeBinding.isLazy(),
 					true, // insertable
 					true, // updatable
 					singularAttributeBinding.getGeneration() == PropertyGeneration.INSERT
 							|| singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.getGeneration() == PropertyGeneration.ALWAYS,
 					singularAttributeBinding.isNullable(),
 					alwaysDirtyCheck || areAllValuesIncludedInUpdate( singularAttributeBinding ),
 					singularAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 			);
 		}
 		else {
 			final AbstractPluralAttributeBinding pluralAttributeBinding = (AbstractPluralAttributeBinding) property;
 			final CascadeStyle cascadeStyle = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getCascadeStyle()
 					: CascadeStyles.NONE;
 			final FetchMode fetchMode = pluralAttributeBinding.isAssociation()
 					? pluralAttributeBinding.getFetchMode()
 					: FetchMode.DEFAULT;
 
 			return new StandardProperty(
 					pluralAttributeBinding.getAttribute().getName(),
 					type,
 					lazyAvailable && pluralAttributeBinding.isLazy(),
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					true, // pluralAttributeBinding.isInsertable(),
 					true, //pluralAttributeBinding.isUpdatable(),
 					false,
 					false,
 					false, // nullable - not sure what that means for a collection
 					// TODO: fix this when HHH-6356 is fixed; for now assume AbstractPluralAttributeBinding is updatable and insertable
 					//alwaysDirtyCheck || pluralAttributeBinding.isUpdatable(),
 					true,
 					pluralAttributeBinding.isIncludedInOptimisticLocking(),
 					cascadeStyle,
 					fetchMode
 				);
 		}
 	}
 
 	private static boolean areAllValuesIncludedInUpdate(SingularAttributeBinding attributeBinding) {
 		if ( attributeBinding.hasDerivedValue() ) {
 			return false;
 		}
 		for ( SimpleValueBinding valueBinding : attributeBinding.getSimpleValueBindings() ) {
 			if ( ! valueBinding.isIncludeInUpdate() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	private static Constructor getConstructor(PersistentClass persistentClass) {
 		if ( persistentClass == null || !persistentClass.hasPojoRepresentation() ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( persistentClass.getMappedClass() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Constructor getConstructor(EntityBinding entityBinding) {
 		if ( entityBinding == null || entityBinding.getEntity() == null ) {
 			return null;
 		}
 
 		try {
 			return ReflectHelper.getDefaultConstructor( entityBinding.getEntity().getClassReference() );
 		}
 		catch( Throwable t ) {
 			return null;
 		}
 	}
 
 	private static Getter getGetter(Property mappingProperty) {
 		if ( mappingProperty == null || !mappingProperty.getPersistentClass().hasPojoRepresentation() ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter( mappingProperty.getPersistentClass().getMappedClass(), mappingProperty.getName() );
 	}
 
 	private static Getter getGetter(AttributeBinding mappingProperty) {
 		if ( mappingProperty == null || mappingProperty.getContainer().getClassReference() == null ) {
 			return null;
 		}
 
 		PropertyAccessor pa = PropertyAccessorFactory.getPropertyAccessor( mappingProperty, EntityMode.POJO );
 		return pa.getGetter(
 				mappingProperty.getContainer().getClassReference(),
 				mappingProperty.getAttribute().getName()
 		);
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
index 5c62859939..a225b8a05f 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeBasedAttribute.java
@@ -1,61 +1,61 @@
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
 package org.hibernate.tuple.component;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.tuple.AbstractNonIdentifierAttribute;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractCompositeBasedAttribute
 		extends AbstractNonIdentifierAttribute
 		implements NonIdentifierAttribute {
 
 	private final int ownerAttributeNumber;
 
 	public AbstractCompositeBasedAttribute(
-			AbstractCompositeDefinition source,
+			AbstractCompositionDefinition source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			Type attributeType,
 			BaselineAttributeInformation baselineInfo,
 			int ownerAttributeNumber) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 		this.ownerAttributeNumber = ownerAttributeNumber;
 	}
 
 	protected int ownerAttributeNumber() {
 		return ownerAttributeNumber;
 	}
 
 	@Override
-	public AbstractCompositeDefinition getSource() {
-		return (AbstractCompositeDefinition) super.getSource();
+	public AbstractCompositionDefinition getSource() {
+		return (AbstractCompositionDefinition) super.getSource();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeDefinition.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionDefinition.java
similarity index 76%
rename from hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeDefinition.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionDefinition.java
index 17b4b0ccb1..0584f843ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositeDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionDefinition.java
@@ -1,202 +1,208 @@
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
 package org.hibernate.tuple.component;
 
 import java.util.Iterator;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
-import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.AbstractNonIdentifierAttribute;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 import static org.hibernate.engine.internal.JoinHelper.getLHSColumnNames;
 import static org.hibernate.engine.internal.JoinHelper.getLHSTableName;
 import static org.hibernate.engine.internal.JoinHelper.getRHSColumnNames;
 
 /**
  * @author Steve Ebersole
  */
-public abstract class AbstractCompositeDefinition extends AbstractNonIdentifierAttribute implements CompositeDefinition {
-	protected AbstractCompositeDefinition(
+public abstract class AbstractCompositionDefinition extends AbstractNonIdentifierAttribute implements
+																						   CompositionDefinition {
+	protected AbstractCompositionDefinition(
 			AttributeSource source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 
 	@Override
 	public CompositeType getType() {
 		return (CompositeType) super.getType();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return new Iterable<AttributeDefinition>() {
 			@Override
 			public Iterator<AttributeDefinition> iterator() {
 				return new Iterator<AttributeDefinition>() {
 					private final int numberOfAttributes = getType().getSubtypes().length;
 					private int currentAttributeNumber = 0;
 					private int currentColumnPosition = 0;
 
 					@Override
 					public boolean hasNext() {
 						return currentAttributeNumber < numberOfAttributes;
 					}
 
 					@Override
 					public AttributeDefinition next() {
 						final int attributeNumber = currentAttributeNumber;
 						currentAttributeNumber++;
 
 						final String name = getType().getPropertyNames()[attributeNumber];
 						final Type type = getType().getSubtypes()[attributeNumber];
 
 						int columnPosition = currentColumnPosition;
 						currentColumnPosition += type.getColumnSpan( sessionFactory() );
 
 						if ( type.isAssociationType() ) {
 							// we build the association-key here because of the "goofiness" with 'currentColumnPosition'
 							final AssociationKey associationKey;
 							final AssociationType aType = (AssociationType) type;
 							final Joinable joinable = aType.getAssociatedJoinable( sessionFactory() );
 							if ( aType.getForeignKeyDirection() == ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT ) {
 								associationKey = new AssociationKey(
 										getLHSTableName(
 												aType,
 												attributeNumber(),
 												(OuterJoinLoadable) joinable
 										),
 										getLHSColumnNames(
 												aType,
 												attributeNumber(),
 												columnPosition,
 												(OuterJoinLoadable) joinable,
 												sessionFactory()
 										)
 								);
 							}
 							else {
 								associationKey = new AssociationKey(
 										joinable.getTableName(),
 										getRHSColumnNames( aType, sessionFactory() )
 								);
 							}
 
 							return new CompositeBasedAssociationAttribute(
-									AbstractCompositeDefinition.this,
+									AbstractCompositionDefinition.this,
 									sessionFactory(),
 									currentAttributeNumber,
 									name,
 									(AssociationType) type,
 									new BaselineAttributeInformation.Builder()
-											.setInsertable( AbstractCompositeDefinition.this.isInsertable() )
-											.setUpdateable( AbstractCompositeDefinition.this.isUpdateable() )
-											.setInsertGenerated( AbstractCompositeDefinition.this.isInsertGenerated() )
-											.setUpdateGenerated( AbstractCompositeDefinition.this.isUpdateGenerated() )
+											.setInsertable( AbstractCompositionDefinition.this.isInsertable() )
+											.setUpdateable( AbstractCompositionDefinition.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositionDefinition.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositionDefinition.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[currentAttributeNumber] )
 											.setDirtyCheckable( true )
-											.setVersionable( AbstractCompositeDefinition.this.isVersionable() )
+											.setVersionable( AbstractCompositionDefinition.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( currentAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( currentAttributeNumber ) )
 											.createInformation(),
-									AbstractCompositeDefinition.this.attributeNumber(),
+									AbstractCompositionDefinition.this.attributeNumber(),
 									associationKey
 							);
 						}
 						else if ( type.isComponentType() ) {
-							return new CompositeBasedCompositeAttribute(
-									AbstractCompositeDefinition.this,
+							return new CompositionBasedCompositionAttribute(
+									AbstractCompositionDefinition.this,
 									sessionFactory(),
 									currentAttributeNumber,
 									name,
 									(CompositeType) type,
 									new BaselineAttributeInformation.Builder()
-											.setInsertable( AbstractCompositeDefinition.this.isInsertable() )
-											.setUpdateable( AbstractCompositeDefinition.this.isUpdateable() )
-											.setInsertGenerated( AbstractCompositeDefinition.this.isInsertGenerated() )
-											.setUpdateGenerated( AbstractCompositeDefinition.this.isUpdateGenerated() )
+											.setInsertable( AbstractCompositionDefinition.this.isInsertable() )
+											.setUpdateable( AbstractCompositionDefinition.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositionDefinition.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositionDefinition.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[currentAttributeNumber] )
 											.setDirtyCheckable( true )
-											.setVersionable( AbstractCompositeDefinition.this.isVersionable() )
+											.setVersionable( AbstractCompositionDefinition.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( currentAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( currentAttributeNumber ) )
 											.createInformation()
 							);
 						}
 						else {
 							return new CompositeBasedBasicAttribute(
-									AbstractCompositeDefinition.this,
+									AbstractCompositionDefinition.this,
 									sessionFactory(),
 									currentAttributeNumber,
 									name,
 									type,
 									new BaselineAttributeInformation.Builder()
-											.setInsertable( AbstractCompositeDefinition.this.isInsertable() )
-											.setUpdateable( AbstractCompositeDefinition.this.isUpdateable() )
-											.setInsertGenerated( AbstractCompositeDefinition.this.isInsertGenerated() )
-											.setUpdateGenerated( AbstractCompositeDefinition.this.isUpdateGenerated() )
+											.setInsertable( AbstractCompositionDefinition.this.isInsertable() )
+											.setUpdateable( AbstractCompositionDefinition.this.isUpdateable() )
+											.setInsertGenerated( AbstractCompositionDefinition.this.isInsertGenerated() )
+											.setUpdateGenerated( AbstractCompositionDefinition.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[currentAttributeNumber] )
 											.setDirtyCheckable( true )
-											.setVersionable( AbstractCompositeDefinition.this.isVersionable() )
+											.setVersionable( AbstractCompositionDefinition.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( currentAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( currentAttributeNumber ) )
 											.createInformation()
 							);
 						}
 					}
 
 					@Override
 					public void remove() {
 						throw new UnsupportedOperationException( "Remove operation not supported here" );
 					}
 				};
 			}
 		};
 	}
 
 	public EntityPersister locateOwningPersister() {
 		if ( EntityDefinition.class.isInstance( getSource() ) ) {
 			return ( (EntityDefinition) getSource() ).getEntityPersister();
 		}
 		else {
-			return ( (AbstractCompositeDefinition) getSource() ).locateOwningPersister();
+			return ( (AbstractCompositionDefinition) getSource() ).locateOwningPersister();
 		}
 	}
+
+	@Override
+	protected String loggableMetadata() {
+		return super.loggableMetadata() + ",composition";
+	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
index 0a8be97a92..eb7224d94d 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedAssociationAttribute.java
@@ -1,157 +1,185 @@
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
 package org.hibernate.tuple.component;
 
+import java.io.Serializable;
+
 import org.hibernate.FetchMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
+import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.internal.Helper;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositeBasedAssociationAttribute
 		extends AbstractCompositeBasedAttribute
 		implements AssociationAttributeDefinition {
 
 	private final AssociationKey associationKey;
 	private Joinable joinable;
 
 	public CompositeBasedAssociationAttribute(
-			AbstractCompositeDefinition source,
+			AbstractCompositionDefinition source,
 			SessionFactoryImplementor factory,
 			int attributeNumber,
 			String attributeName,
 			AssociationType attributeType,
 			BaselineAttributeInformation baselineInfo,
 			int ownerAttributeNumber,
 			AssociationKey associationKey) {
 		super( source, factory, attributeNumber, attributeName, attributeType, baselineInfo, ownerAttributeNumber );
 		this.associationKey = associationKey;
 	}
 
 	@Override
 	public AssociationType getType() {
 		return (AssociationType) super.getType();
 	}
 
 	protected Joinable getJoinable() {
 		if ( joinable == null ) {
 			joinable = getType().getAssociatedJoinable( sessionFactory() );
 		}
 		return joinable;
 	}
 
 	@Override
 	public AssociationKey getAssociationKey() {
 		return associationKey;
 	}
 
 	@Override
 	public boolean isCollection() {
 		return getJoinable().isCollection();
 	}
 
 	@Override
 	public EntityDefinition toEntityDefinition() {
 		if ( isCollection() ) {
 			throw new IllegalStateException( "Cannot treat collection attribute as entity type" );
 		}
 		return (EntityPersister) getJoinable();
 	}
 
 	@Override
 	public CollectionDefinition toCollectionDefinition() {
 		if ( isCollection() ) {
 			throw new IllegalStateException( "Cannot treat entity attribute as collection type" );
 		}
 		return (CollectionPersister) getJoinable();
 	}
 
 	@Override
 	public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
 		final EntityPersister owningPersister = locateOwningPersister();
 
 		FetchStyle style = determineFetchStyleByProfile(
 				loadQueryInfluencers,
 				owningPersister,
 				propertyPath,
 				ownerAttributeNumber()
 		);
 		if ( style == null ) {
 			style = determineFetchStyleByMetadata(
 					getSource().getType().getFetchMode( attributeNumber() ),
 					getType()
 			);
 		}
 
 		return new FetchStrategy( determineFetchTiming( style ), style );
 	}
 
 	protected FetchStyle determineFetchStyleByProfile(
 			LoadQueryInfluencers loadQueryInfluencers,
 			EntityPersister owningPersister,
 			PropertyPath propertyPath,
 			int ownerAttributeNumber) {
 		return Helper.determineFetchStyleByProfile(
 				loadQueryInfluencers,
 				owningPersister,
 				propertyPath,
 				ownerAttributeNumber
 		);
 	}
 
 	protected FetchStyle determineFetchStyleByMetadata(FetchMode fetchMode, AssociationType type) {
 		return Helper.determineFetchStyleByMetadata( fetchMode, type, sessionFactory() );
 	}
 
 	private FetchTiming determineFetchTiming(FetchStyle style) {
 		return Helper.determineFetchTiming( style, getType(), sessionFactory() );
 	}
 
 	private EntityPersister locateOwningPersister() {
 		return getSource().locateOwningPersister();
 	}
 
 	@Override
 	public CascadeStyle determineCascadeStyle() {
 		final CompositeType compositeType = (CompositeType) locateOwningPersister().getPropertyType( getName() );
 		return compositeType.getCascadeStyle( attributeNumber() );
 	}
+
+	private HydratedCompoundValueHandler hydratedCompoundValueHandler;
+
+	@Override
+	public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
+		if ( hydratedCompoundValueHandler == null ) {
+			hydratedCompoundValueHandler = new HydratedCompoundValueHandler() {
+				@Override
+				public Object extract(Object hydratedState) {
+					return ( (Object[] ) hydratedState )[ attributeNumber() ];
+				}
+
+				@Override
+				public void inject(Object hydratedState, Object value) {
+					( (Object[] ) hydratedState )[ attributeNumber() ] = value;
+				}
+			};
+		}
+		return hydratedCompoundValueHandler;
+	}
+
+	@Override
+	protected String loggableMetadata() {
+		return super.loggableMetadata() + ",association";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedCompositeAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
similarity index 85%
rename from hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedCompositeAttribute.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
index 4094ededd1..7beee25984 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositeBasedCompositeAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
@@ -1,46 +1,46 @@
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
 package org.hibernate.tuple.component;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
-public class CompositeBasedCompositeAttribute
-		extends AbstractCompositeDefinition
-		implements CompositeDefinition {
-	public CompositeBasedCompositeAttribute(
-			CompositeDefinition source,
+public class CompositionBasedCompositionAttribute
+		extends AbstractCompositionDefinition
+		implements CompositionDefinition {
+	public CompositionBasedCompositionAttribute(
+			CompositionDefinition source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedAssociationAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedAssociationAttribute.java
index 7177552bbd..d5641ebd87 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedAssociationAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedAssociationAttribute.java
@@ -1,155 +1,181 @@
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
 package org.hibernate.tuple.entity;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.internal.Helper;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.ForeignKeyDirection;
 
 import static org.hibernate.engine.internal.JoinHelper.getLHSColumnNames;
 import static org.hibernate.engine.internal.JoinHelper.getLHSTableName;
 import static org.hibernate.engine.internal.JoinHelper.getRHSColumnNames;
 
 /**
 * @author Steve Ebersole
 */
 public class EntityBasedAssociationAttribute
 		extends AbstractEntityBasedAttribute
 		implements AssociationAttributeDefinition {
 
 	private Joinable joinable;
 
 	public EntityBasedAssociationAttribute(
 			EntityPersister source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			AssociationType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 
 	@Override
 	public AssociationType getType() {
 		return (AssociationType) super.getType();
 	}
 
 	protected Joinable getJoinable() {
 		if ( joinable == null ) {
 			joinable = getType().getAssociatedJoinable( sessionFactory() );
 		}
 		return joinable;
 	}
 
 	@Override
 	public AssociationKey getAssociationKey() {
 		final AssociationType type = getType();
 		final Joinable joinable = type.getAssociatedJoinable( sessionFactory() );
 
 		if ( type.getForeignKeyDirection() == ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT ) {
 			final String lhsTableName;
 			final String[] lhsColumnNames;
 
 			if ( joinable.isCollection() ) {
 				final QueryableCollection collectionPersister = (QueryableCollection) joinable;
 				lhsTableName = collectionPersister.getTableName();
 				lhsColumnNames = collectionPersister.getElementColumnNames();
 			}
 			else {
 				final OuterJoinLoadable entityPersister = (OuterJoinLoadable) joinable;
 				lhsTableName = getLHSTableName( type, attributeNumber(), entityPersister );
 				lhsColumnNames = getLHSColumnNames( type, attributeNumber(), entityPersister, sessionFactory() );
 			}
 			return new AssociationKey( lhsTableName, lhsColumnNames );
 		}
 		else {
 			return new AssociationKey( joinable.getTableName(), getRHSColumnNames( type, sessionFactory() ) );
 		}
 	}
 
 	@Override
 	public boolean isCollection() {
 		return getJoinable().isCollection();
 	}
 
 	@Override
 	public EntityDefinition toEntityDefinition() {
 		if ( isCollection() ) {
 			throw new IllegalStateException( "Cannot treat collection-valued attribute as entity type" );
 		}
 		return (EntityPersister) getJoinable();
 	}
 
 	@Override
 	public CollectionDefinition toCollectionDefinition() {
 		if ( ! isCollection() ) {
 			throw new IllegalStateException( "Cannot treat entity-valued attribute as collection type" );
 		}
 		return (QueryableCollection) getJoinable();
 	}
 
 	@Override
 	public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
 		final EntityPersister owningPersister = getSource().getEntityPersister();
 
 		FetchStyle style = Helper.determineFetchStyleByProfile(
 				loadQueryInfluencers,
 				owningPersister,
 				propertyPath,
 				attributeNumber()
 		);
 		if ( style == null ) {
 			style = Helper.determineFetchStyleByMetadata(
 					((OuterJoinLoadable) getSource().getEntityPersister()).getFetchMode( attributeNumber() ),
 					getType(),
 					sessionFactory()
 			);
 		}
 
 		return new FetchStrategy(
 				Helper.determineFetchTiming( style, getType(), sessionFactory() ),
 				style
 		);
 	}
 
 	@Override
 	public CascadeStyle determineCascadeStyle() {
 		return getSource().getEntityPersister().getPropertyCascadeStyles()[attributeNumber()];
 	}
+
+	private HydratedCompoundValueHandler hydratedCompoundValueHandler;
+
+	@Override
+	public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
+		if ( hydratedCompoundValueHandler == null ) {
+			hydratedCompoundValueHandler = new HydratedCompoundValueHandler() {
+				@Override
+				public Object extract(Object hydratedState) {
+					return ( (Object[] ) hydratedState )[ attributeNumber() ];
+				}
+
+				@Override
+				public void inject(Object hydratedState, Object value) {
+					( (Object[] ) hydratedState )[ attributeNumber() ] = value;
+				}
+			};
+		}
+		return hydratedCompoundValueHandler;
+	}
+
+	@Override
+	protected String loggableMetadata() {
+		return super.loggableMetadata() + ",association";
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositeAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositionAttribute.java
similarity index 84%
rename from hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositeAttribute.java
rename to hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositionAttribute.java
index 8f3a9a6e80..e283cae94b 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositeAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityBasedCompositionAttribute.java
@@ -1,49 +1,49 @@
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
 package org.hibernate.tuple.entity;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.tuple.component.AbstractCompositeDefinition;
-import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.tuple.component.AbstractCompositionDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
-public class EntityBasedCompositeAttribute
-		extends AbstractCompositeDefinition
-		implements CompositeDefinition {
+public class EntityBasedCompositionAttribute
+		extends AbstractCompositionDefinition
+		implements CompositionDefinition {
 
-	public EntityBasedCompositeAttribute(
+	public EntityBasedCompositionAttribute(
 			EntityPersister source,
 			SessionFactoryImplementor factory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, factory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
index 597b900344..9491d61b95 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
@@ -1,149 +1,149 @@
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
 package org.hibernate.loader.plan.spi;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.util.List;
 
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.loader.plan.internal.CascadeLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class LoadPlanBuilderTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class };
 	}
 
 	@Test
 	public void testSimpleBuild() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				sessionFactory(),
 				LoadQueryInfluencers.NONE,
 				"abc",
 				0
 		);
 		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
 		assertEquals( "abc", entityReturn.getAlias() );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 	}
 
 	@Test
 	public void testCascadeBasedBuild() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		CascadeLoadPlanBuilderStrategy strategy = new CascadeLoadPlanBuilderStrategy(
 				CascadingActions.MERGE,
 				sessionFactory(),
 				LoadQueryInfluencers.NONE,
 				"abc",
 				0
 		);
 		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
 		assertEquals( "abc", entityReturn.getAlias() );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 	}
 
 	@Test
 	public void testCollectionInitializerCase() {
 		CollectionPersister cp = sessionFactory().getCollectionPersister( Poster.class.getName() + ".messages" );
 		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				sessionFactory(),
 				LoadQueryInfluencers.NONE,
 				"abc",
 				0
 		);
 		LoadPlan plan = LoadPlanBuilder.buildRootCollectionLoadPlan( strategy, cp );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		CollectionReturn collectionReturn = ExtraAssertions.assertTyping( CollectionReturn.class, rtn );
 		assertEquals( "abc", collectionReturn.getAlias() );
 
-		assertNotNull( collectionReturn.getFetches() );
-		assertEquals( 1, collectionReturn.getFetches().length ); // the collection elements are fetched
-		Fetch fetch = collectionReturn.getFetches()[0];
+		assertNotNull( collectionReturn.getElementGraph().getFetches() );
+		assertEquals( 1, collectionReturn.getElementGraph().getFetches().length ); // the collection elements are fetched
+		Fetch fetch = collectionReturn.getElementGraph().getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
-		private Integer id;
-		private String name;
+		private Integer mid;
+		private String msgTxt;
 		@ManyToOne( cascade = CascadeType.MERGE )
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
-		private Integer id;
+		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages;
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
index 07c193bb13..da8bc7f961 100644
--- a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
@@ -1,177 +1,210 @@
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
 package org.hibernate.persister.walking;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.util.List;
 
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
-import org.hibernate.persister.walking.spi.CompositeDefinition;
+import org.hibernate.persister.walking.spi.CollectionElementDefinition;
+import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicWalkingTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class };
 	}
 
 	@Test
 	public void testIt() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		MetadataDrivenModelGraphVisitor.visitEntity(
 				new AssociationVisitationStrategy() {
 					private int depth = 0;
 
 					@Override
 					public void start() {
 						System.out.println( ">> Start" );
 					}
 
 					@Override
 					public void finish() {
 						System.out.println( "<< Finish" );
 					}
 
 					@Override
 					public void startingEntity(EntityDefinition entityDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting entity (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										entityDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingEntity(EntityDefinition entityDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing entity (%s)",
 										StringHelper.repeat( "<<", depth-- ),
 										entityDefinition.toString()
 								)
 						);
 					}
 
 					@Override
+					public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
+						//To change body of implemented methods use File | Settings | File Templates.
+					}
+
+					@Override
+					public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
+						//To change body of implemented methods use File | Settings | File Templates.
+					}
+
+					@Override
 					public void startingCollection(CollectionDefinition collectionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting collection (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										collectionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingCollection(CollectionDefinition collectionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing collection (%s)",
 										StringHelper.repeat( ">>", depth-- ),
 										collectionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
-					public void startingComposite(CompositeDefinition compositeDefinition) {
+					public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+						//To change body of implemented methods use File | Settings | File Templates.
+					}
+
+					@Override
+					public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+						//To change body of implemented methods use File | Settings | File Templates.
+					}
+
+					@Override
+					public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
+						//To change body of implemented methods use File | Settings | File Templates.
+					}
+
+					@Override
+					public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
+						//To change body of implemented methods use File | Settings | File Templates.
+					}
+
+					@Override
+					public void startingComposite(CompositionDefinition compositionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting composite (%s)",
 										StringHelper.repeat( ">>", ++depth ),
-										compositeDefinition.toString()
+										compositionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
-					public void finishingComposite(CompositeDefinition compositeDefinition) {
+					public void finishingComposite(CompositionDefinition compositionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing composite (%s)",
 										StringHelper.repeat( ">>", depth-- ),
-										compositeDefinition.toString()
+										compositionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 						System.out.println(
 								String.format(
 										"%s Handling attribute (%s)",
 										StringHelper.repeat( ">>", depth + 1 ),
 										attributeDefinition.toString()
 								)
 						);
 						return true;
 					}
 
 					@Override
 					public void finishingAttribute(AttributeDefinition attributeDefinition) {
 						// nothing to do
 					}
 				},
 				ep
 		);
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer id;
 		private String name;
 		@ManyToOne
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer id;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 6af678c8a5..cd85045871 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,852 +1,853 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.test.cfg.persister;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
+import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( null );
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 		
 		@Override
 		public boolean hasNaturalIdCache() {
 			return false;
 		}
 
 		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(
 				Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			// TODO Auto-generated method stub
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
-		public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes() {
-			throw new NotYetImplementedException();
+		public EntityIdentifierDefinition getEntityKeyDefinition() {
+			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			throw new NotYetImplementedException();
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.spi.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   SessionFactoryImplementor sf) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public CollectionPersister getCollectionPersister() {
 			return this;
 		}
 
 		public CollectionType getCollectionType() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionIndexDefinition getIndexDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionElementDefinition getElementDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public int getBatchSize() {
 			return 0;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index 55b96323ab..ba4ff61bf5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,691 +1,692 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping mapping) {
 		this.factory = factory;
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
 					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session ),
 					new PostLoadEvent( (EventSource) session )
 				);
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return StandardBasicTypes.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 	
 	public boolean hasNaturalIdCache() {
 		return false;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 		throw new UnsupportedOperationException( "not supported" );
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return new UnstructuredCacheEntry();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(
 			Object entity, Object[] state, Object version, SessionImplementor session) {
 		return new StandardCacheEntryImpl(
 				state,
 				this,
 				this.hasUninitializedLazyProperties( entity ),
 				version,
 				session,
 				entity
 		);
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean isInstrumented() {
 		return false;
 	}
 
 	@Override
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	@Override
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 			SessionImplementor session) {
 		return null;
 	}
 
 	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return new NonPojoInstrumentationMetadata( getEntityName() );
 	}
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
-	public Iterable<AttributeDefinition> getEmbeddedCompositeIdentifierAttributes() {
+	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		throw new NotYetImplementedException();
 	}
 }
diff --git a/hibernate-core/src/test/resources/log4j.properties b/hibernate-core/src/test/resources/log4j.properties
index 686aae8fc2..73d5935aea 100644
--- a/hibernate-core/src/test/resources/log4j.properties
+++ b/hibernate-core/src/test/resources/log4j.properties
@@ -1,17 +1,19 @@
 log4j.appender.stdout=org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target=System.out
 log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
 
 
 log4j.rootLogger=info, stdout
 
 log4j.logger.org.hibernate.tool.hbm2ddl=trace
 log4j.logger.org.hibernate.testing.cache=debug
 
+log4j.logger.org.hibernate.loader.plan=trace
+
 # SQL Logging - HHH-6833
 log4j.logger.org.hibernate.SQL=debug
 
 log4j.logger.org.hibernate.hql.internal.ast=debug
 
 log4j.logger.org.hibernate.sql.ordering.antlr=debug
\ No newline at end of file
