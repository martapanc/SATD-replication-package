diff --git a/core/src/main/java/org/hibernate/cache/QueryKey.java b/core/src/main/java/org/hibernate/cache/QueryKey.java
index e66271f420..6dfa6125df 100644
--- a/core/src/main/java/org/hibernate/cache/QueryKey.java
+++ b/core/src/main/java/org/hibernate/cache/QueryKey.java
@@ -1,291 +1,292 @@
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
 package org.hibernate.cache;
 
 import java.io.Serializable;
 import java.io.IOException;
 import java.util.Map;
 import java.util.Set;
 import java.util.Iterator;
 
 import org.hibernate.EntityMode;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TypedValue;
+import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 import org.hibernate.util.EqualsHelper;
 import org.hibernate.util.CollectionHelper;
 
 /**
  * A key that identifies a particular query with bound parameter values.  This is the object Hibernate uses
  * as its key into its query cache.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class QueryKey implements Serializable {
 	private final String sqlQueryString;
 	private final Type[] positionalParameterTypes;
 	private final Object[] positionalParameterValues;
 	private final Map namedParameters;
 	private final Integer firstRow;
 	private final Integer maxRows;
 	private final EntityMode entityMode;
 	private final Set filterKeys;
 	
 	// the user provided resulttransformer, not the one used with "select new". Here to avoid mangling
 	// transformed/non-transformed results.
-	private final ResultTransformer customTransformer;
+	private final CacheableResultTransformer customTransformer;
 
 	/**
 	 * For performance reasons, the hashCode is cached; however, it is marked transient so that it can be
 	 * recalculated as part of the serialization process which allows distributed query caches to work properly.
 	 */
 	private transient int hashCode;
 
 	/**
 	 * Generates a QueryKey.
 	 *
 	 * @param queryString The sql query string.
 	 * @param queryParameters The query parameters
 	 * @param filterKeys The keys of any enabled filters.
 	 * @param session The current session.
 	 * @param customTransformer The result transformer; should be
 	 *            null if data is not transformed before being cached.
 	 *
 	 * @return The generate query cache key.
 	 */
 	public static QueryKey generateQueryKey(
 			String queryString,
 			QueryParameters queryParameters,
 			Set filterKeys,
 			SessionImplementor session,
-			ResultTransformer customTransformer) {
+			CacheableResultTransformer customTransformer) {
 		// disassemble positional parameters
 		final int positionalParameterCount = queryParameters.getPositionalParameterTypes().length;
 		final Type[] types = new Type[positionalParameterCount];
 		final Object[] values = new Object[positionalParameterCount];
 		for ( int i = 0; i < positionalParameterCount; i++ ) {
 			types[i] = queryParameters.getPositionalParameterTypes()[i];
 			values[i] = types[i].disassemble( queryParameters.getPositionalParameterValues()[i], session, null );
 		}
 
 		// disassemble named parameters
 		final Map namedParameters;
 		if ( queryParameters.getNamedParameters() == null ) {
 			namedParameters = null;
 		}
 		else {
 			namedParameters = CollectionHelper.mapOfSize( queryParameters.getNamedParameters().size() );
 			Iterator itr = queryParameters.getNamedParameters().entrySet().iterator();
 			while ( itr.hasNext() ) {
 				final Map.Entry namedParameterEntry = ( Map.Entry ) itr.next();
 				final TypedValue original = ( TypedValue ) namedParameterEntry.getValue();
 				namedParameters.put(
 						namedParameterEntry.getKey(),
 						new TypedValue(
 								original.getType(),
 								original.getType().disassemble( original.getValue(), session, null ),
 								session.getEntityMode()
 						)
 				);
 			}
 		}
 
 		// decode row selection...
 		final RowSelection selection = queryParameters.getRowSelection();
 		final Integer firstRow;
 		final Integer maxRows;
 		if ( selection != null ) {
 			firstRow = selection.getFirstRow();
 			maxRows = selection.getMaxRows();
 		}
 		else {
 			firstRow = null;
 			maxRows = null;
 		}
 
 		return new QueryKey(
 				queryString,
 				types,
 				values,
 				namedParameters,
 				firstRow,
 				maxRows,
 				filterKeys,
 				session.getEntityMode(),
 				customTransformer
 		);
 	}
 
 	/**
 	 * Package-protected constructor.
 	 *
 	 * @param sqlQueryString The sql query string.
 	 * @param positionalParameterTypes Positional parameter types.
 	 * @param positionalParameterValues Positional parameter values.
 	 * @param namedParameters Named parameters.
 	 * @param firstRow First row selection, if any.
 	 * @param maxRows Max-rows selection, if any.
 	 * @param filterKeys Enabled filter keys, if any.
 	 * @param entityMode The entity mode.
 	 * @param customTransformer Custom result transformer, if one.
 	 */
 	QueryKey(
 			String sqlQueryString,
 			Type[] positionalParameterTypes,
 			Object[] positionalParameterValues,
 			Map namedParameters,
 			Integer firstRow,
 			Integer maxRows, 
 			Set filterKeys,
 			EntityMode entityMode,
-			ResultTransformer customTransformer) {
+			CacheableResultTransformer customTransformer) {
 		this.sqlQueryString = sqlQueryString;
 		this.positionalParameterTypes = positionalParameterTypes;
 		this.positionalParameterValues = positionalParameterValues;
 		this.namedParameters = namedParameters;
 		this.firstRow = firstRow;
 		this.maxRows = maxRows;
 		this.entityMode = entityMode;
 		this.filterKeys = filterKeys;
 		this.customTransformer = customTransformer;
 		this.hashCode = generateHashCode();
 	}
 
-	public ResultTransformer getResultTransformer() {
+	public CacheableResultTransformer getResultTransformer() {
 		return customTransformer;
 	}
 
 	/**
 	 * Deserialization hook used to re-init the cached hashcode which is needed for proper clustering support.
 	 *
 	 * @param in The object input stream.
 	 *
 	 * @throws IOException Thrown by normal deserialization
 	 * @throws ClassNotFoundException Thrown by normal deserialization
 	 */
 	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
 		in.defaultReadObject();
 		this.hashCode = generateHashCode();
 	}
 
 	private int generateHashCode() {
 		int result = 13;
 		result = 37 * result + ( firstRow==null ? 0 : firstRow.hashCode() );
 		result = 37 * result + ( maxRows==null ? 0 : maxRows.hashCode() );
 		for ( int i=0; i< positionalParameterValues.length; i++ ) {
 			result = 37 * result + ( positionalParameterValues[i]==null ? 0 : positionalParameterTypes[i].getHashCode( positionalParameterValues[i], entityMode ) );
 		}
 		result = 37 * result + ( namedParameters==null ? 0 : namedParameters.hashCode() );
 		result = 37 * result + ( filterKeys ==null ? 0 : filterKeys.hashCode() );
 		result = 37 * result + ( customTransformer==null ? 0 : customTransformer.hashCode() );
 		result = 37 * result + sqlQueryString.hashCode();
 		return result;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean equals(Object other) {
 		if ( !( other instanceof QueryKey ) ) {
 			return false;
 		}
 		QueryKey that = ( QueryKey ) other;
 		if ( !sqlQueryString.equals( that.sqlQueryString ) ) {
 			return false;
 		}
 		if ( !EqualsHelper.equals( firstRow, that.firstRow ) || !EqualsHelper.equals( maxRows, that.maxRows ) ) {
 			return false;
 		}
 		if ( !EqualsHelper.equals( customTransformer, that.customTransformer ) ) {
 			return false;
 		}
 		if ( positionalParameterTypes == null ) {
 			if ( that.positionalParameterTypes != null ) {
 				return false;
 			}
 		}
 		else {
 			if ( that.positionalParameterTypes == null ) {
 				return false;
 			}
 			if ( positionalParameterTypes.length != that.positionalParameterTypes.length ) {
 				return false;
 			}
 			for ( int i = 0; i < positionalParameterTypes.length; i++ ) {
 				if ( positionalParameterTypes[i].getReturnedClass() != that.positionalParameterTypes[i].getReturnedClass() ) {
 					return false;
 				}
 				if ( !positionalParameterTypes[i].isEqual( positionalParameterValues[i], that.positionalParameterValues[i], entityMode ) ) {
 					return false;
 				}
 			}
 		}
 
 		return EqualsHelper.equals( filterKeys, that.filterKeys )
 				&& EqualsHelper.equals( namedParameters, that.namedParameters );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int hashCode() {
 		return hashCode;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String toString() {
 		StringBuffer buf = new StringBuffer()
 				.append( "sql: " )
 				.append( sqlQueryString );
 		if ( positionalParameterValues != null ) {
 			buf.append( "; parameters: " );
 			for ( int i = 0; i < positionalParameterValues.length; i++ ) {
 				buf.append( positionalParameterValues[i] ).append( ", " );
 			}
 		}
 		if ( namedParameters != null ) {
 			buf.append( "; named parameters: " ).append( namedParameters );
 		}
 		if ( filterKeys != null ) {
 			buf.append( "; filterKeys: " ).append( filterKeys );
 		}
 		if ( firstRow != null ) {
 			buf.append( "; first row: " ).append( firstRow );
 		}
 		if ( maxRows != null ) {
 			buf.append( "; max rows: " ).append( maxRows );
 		}
 		if ( customTransformer != null ) {
 			buf.append( "; transformer: " ).append( customTransformer );
 		}
 		return buf.toString();
 	}
 	
 }
diff --git a/core/src/main/java/org/hibernate/cache/StandardQueryCache.java b/core/src/main/java/org/hibernate/cache/StandardQueryCache.java
index 6f8c19c5bf..429718b353 100644
--- a/core/src/main/java/org/hibernate/cache/StandardQueryCache.java
+++ b/core/src/main/java/org/hibernate/cache/StandardQueryCache.java
@@ -1,205 +1,284 @@
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
 package org.hibernate.cache;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 import java.util.Set;
 
 import javax.persistence.EntityNotFoundException;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * The standard implementation of the Hibernate QueryCache interface.  This
  * implementation is very good at recognizing stale query results and
  * and re-running queries when it detects this condition, recaching the new
  * results.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class StandardQueryCache implements QueryCache {
 
 	private static final Logger log = LoggerFactory.getLogger( StandardQueryCache.class );
 
 	private QueryResultsRegion cacheRegion;
 	private UpdateTimestampsCache updateTimestampsCache;
 
 	public void clear() throws CacheException {
 		cacheRegion.evictAll();
 	}
 
 	public StandardQueryCache(
 			final Settings settings,
 			final Properties props,
 			final UpdateTimestampsCache updateTimestampsCache,
 			String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			regionName = StandardQueryCache.class.getName();
 		}
 		String prefix = settings.getCacheRegionPrefix();
 		if ( prefix != null ) {
 			regionName = prefix + '.' + regionName;
 		}
 		log.info( "starting query cache at region: " + regionName );
 
 		this.cacheRegion = settings.getRegionFactory().buildQueryResultsRegion( regionName, props );
 		this.updateTimestampsCache = updateTimestampsCache;
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing", "unchecked" })
 	public boolean put(
 			QueryKey key,
 			Type[] returnTypes,
 			List result,
 			boolean isNaturalKeyLookup,
 			SessionImplementor session) throws HibernateException {
 		if ( isNaturalKeyLookup && result.size() == 0 ) {
 			return false;
 		}
 		else {
 			Long ts = new Long( session.getFactory().getSettings().getRegionFactory().nextTimestamp());
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( "caching query results in region: " + cacheRegion.getName() + "; timestamp=" + ts );
 			}
 
 			List cacheable = new ArrayList( result.size() + 1 );
+			if ( log.isTraceEnabled() ) {
+				logCachedResultDetails( key, null, returnTypes, cacheable );
+			}
 			cacheable.add( ts );
 			for ( Object aResult : result ) {
 				if ( returnTypes.length == 1 ) {
 					cacheable.add( returnTypes[0].disassemble( aResult, session, null ) );
 				}
 				else {
 					cacheable.add(
 							TypeHelper.disassemble( (Object[]) aResult, returnTypes, null, session, null )
 					);
 				}
+				if ( log.isTraceEnabled() ) {
+					logCachedResultRowDetails( returnTypes, aResult );
+				}
 			}
 
 			cacheRegion.put( key, cacheable );
 			return true;
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public List get(
 			QueryKey key,
 			Type[] returnTypes,
 			boolean isNaturalKeyLookup,
 			Set spaces,
 			SessionImplementor session) throws HibernateException {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "checking cached query results in region: " + cacheRegion.getName() );
 		}
 
 		List cacheable = ( List ) cacheRegion.get( key );
+		if ( log.isTraceEnabled() ) {
+			logCachedResultDetails( key, spaces, returnTypes, cacheable );
+		}
+
 		if ( cacheable == null ) {
 			log.debug( "query results were not found in cache" );
 			return null;
 		}
 
 		Long timestamp = ( Long ) cacheable.get( 0 );
 		if ( !isNaturalKeyLookup && !isUpToDate( spaces, timestamp ) ) {
 			log.debug( "cached query results were not up to date" );
 			return null;
 		}
 
 		log.debug( "returning cached query results" );
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			if ( returnTypes.length == 1 ) {
 				returnTypes[0].beforeAssemble( ( Serializable ) cacheable.get( i ), session );
 			}
 			else {
 				TypeHelper.beforeAssemble( ( Serializable[] ) cacheable.get( i ), returnTypes, session );
 			}
 		}
 		List result = new ArrayList( cacheable.size() - 1 );
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			try {
 				if ( returnTypes.length == 1 ) {
 					result.add( returnTypes[0].assemble( ( Serializable ) cacheable.get( i ), session, null ) );
 				}
 				else {
 					result.add(
 							TypeHelper.assemble( ( Serializable[] ) cacheable.get( i ), returnTypes, session, null )
 					);
 				}
+				if ( log.isTraceEnabled() ) {
+					logCachedResultRowDetails( returnTypes, result.get( i - 1 ));
+				}
 			}
 			catch ( RuntimeException ex ) {
 				if ( isNaturalKeyLookup &&
 						( UnresolvableObjectException.class.isInstance( ex ) ||
 						EntityNotFoundException.class.isInstance( ex ) ) ) {
 					//TODO: not really completely correct, since
 					//      the uoe could occur while resolving
 					//      associations, leaving the PC in an
 					//      inconsistent state
 					log.debug( "could not reassemble cached result set" );
 					cacheRegion.evict( key );
 					return null;
 				}
 				else {
 					throw ex;
 				}
 			}
 		}
 		return result;
 	}
 
 	protected boolean isUpToDate(Set spaces, Long timestamp) {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "Checking query spaces for up-to-dateness: " + spaces );
 		}
 		return updateTimestampsCache.isUpToDate( spaces, timestamp );
 	}
 
 	public void destroy() {
 		try {
 			cacheRegion.destroy();
 		}
 		catch ( Exception e ) {
 			log.warn( "could not destroy query cache: " + cacheRegion.getName(), e );
 		}
 	}
 
 	public QueryResultsRegion getRegion() {
 		return cacheRegion;
 	}
 
 	public String toString() {
 		return "StandardQueryCache(" + cacheRegion.getName() + ')';
 	}
 
+	private static void logCachedResultDetails(QueryKey key, Set querySpaces, Type[] returnTypes, List result) {
+		if ( ! log.isTraceEnabled() ) {
+			return;
+		}
+		log.trace( "key.hashCode="+key.hashCode() );
+		log.trace( "querySpaces="+querySpaces );
+		if ( returnTypes == null || returnTypes.length == 0 ) {
+				log.trace( "unexpected returnTypes is "+( returnTypes == null ? "null" : "empty" )+
+						"! result"+( result == null ? " is null": ".size()=" + result.size() ) );
+		}
+		else {
+			StringBuffer returnTypeInfo = new StringBuffer();
+			for ( int i=0; i<returnTypes.length; i++ ) {
+				returnTypeInfo.append( "typename=" )
+						.append( returnTypes[ i ].getName() )
+						.append(" class=" )
+						.append( returnTypes[ i ].getReturnedClass().getName() ).append(' ');
+			}
+			log.trace( " returnTypeInfo="+returnTypeInfo );
+		}
+	}
+
+	private static void logCachedResultRowDetails(Type[] returnTypes, Object result) {
+		if ( ! log.isTraceEnabled() ) {
+			return;
+		}
+		logCachedResultRowDetails(
+				returnTypes,
+				( result instanceof Object[] ? ( Object[] ) result : new Object[] { result } )
+		);
+	}
+
+	private static void logCachedResultRowDetails(Type[] returnTypes, Object[] tuple) {
+		if ( ! log.isTraceEnabled() ) {
+			return;
+		}
+		if ( tuple == null ) {
+			log.trace( " tuple is null; returnTypes is "+( returnTypes == null ? "null" : "Type["+returnTypes.length+"]" ) );
+			if ( returnTypes != null && returnTypes.length > 1 ) {
+				log.trace( "unexpected result tuple! "+
+						"tuple is null; should be Object["+returnTypes.length+"]!" );
+			}
+		}
+		else {
+			if ( returnTypes == null || returnTypes.length == 0 ) {
+				log.trace( "unexpected result tuple! "+
+						"tuple is non-null; returnTypes is "+( returnTypes == null ? "null" : "empty" ) );
+			}
+			log.trace( " tuple is Object["+tuple.length+
+					"]; returnTypes is Type["+returnTypes.length+"]" );
+			if ( tuple.length != returnTypes.length ) {
+				log.trace( "unexpected tuple length! transformer="+
+					" expected="+returnTypes.length+
+					" got="+tuple.length );
+			}
+			else {
+				for ( int j = 0; j < tuple.length; j++ ) {
+					if ( tuple[ j ] != null && ! returnTypes[ j ].getReturnedClass().isInstance( tuple[ j ] ) ) {
+						log.trace( "unexpected tuple value type! transformer="+
+								" expected="+returnTypes[ j ].getReturnedClass().getName()+
+								" got="+tuple[ j ].getClass().getName() );
+					}
+				}
+			}
+		}
+	}
 }
diff --git a/core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java b/core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
index 78915efaa7..16bc2894a4 100644
--- a/core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+++ b/core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
@@ -1,1206 +1,1217 @@
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
 package org.hibernate.hql.classic;
 
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
+import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.JoinSequence;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.FilterTranslator;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.hql.NameGenerator;
 import org.hibernate.hql.ParameterTranslations;
 import org.hibernate.impl.IteratorImpl;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.QuerySelect;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
-import org.hibernate.type.TypeFactory;
 import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 
 /**
  * An instance of <tt>QueryTranslator</tt> translates a Hibernate
  * query string to SQL.
  */
 public class QueryTranslatorImpl extends BasicLoader implements FilterTranslator {
 
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
 
 	private static final Logger log = LoggerFactory.getLogger( QueryTranslatorImpl.class );
 
 	/**
 	 * Construct a query translator
 	 *
 	 * @param queryIdentifier A unique identifier for the query of which this
 	 * translation is part; typically this is the original, user-supplied query string.
 	 * @param queryString The "preprocessed" query string; at the very least
 	 * already processed by {@link org.hibernate.hql.QuerySplitter}.
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
 
 		log.trace( "compiling query" );
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
 			log.debug( "unexpected query compilation problem", e );
 			e.printStackTrace();
 			QueryException qe = new QueryException( "Incorrect query syntax", e );
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 
 		postInstantiate();
 
 		compiled = true;
 
 	}
 
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
 		if ( log.isDebugEnabled() ) {
 			log.debug( "HQL: " + hql );
 			log.debug( "SQL: " + sql );
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
 		if ( name != null ) {
 			return name + path.substring( alias.length() );
 		}
 		else {
 			return path;
 		}
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
 	public CollectionPersister[] getCollectionPersisters() {
 		return collectionPersister == null ? null : new CollectionPersister[] { collectionPersister };
 	}
 
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
 
 	protected String[] getSuffixes() {
 		return suffixes;
 	}
 
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
 			throw JDBCExceptionHelper.convert( 
 					getFactory().getSQLExceptionConverter(),
 					sqle,
 					"could not execute query using iterate",
 					getSQLString() 
 				);
 		}
 
 	}
 
 	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {
 		throw new UnsupportedOperationException( "Not supported!  Use the AST translator...");
 	}
 
+	protected boolean[] includeInResultRow() {
+		boolean[] isResultReturned = includeInSelect;
+		if ( hasScalars ) {
+			isResultReturned = new boolean[ returnedTypes.size() ];
+			Arrays.fill( isResultReturned, true );
+		}
+		return isResultReturned;
+	}
+
+
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveClassicResultTransformer(
 				holderConstructor,
 				resultTransformer
 		);
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
-		row = toResultRow( row );
+		Object[] resultRow = getResultRow( row, rs, session );
+		return ( holderClass == null && resultRow.length == 1 ?
+				resultRow[ 0 ] :
+				resultRow
+		);
+	}
+
+	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
+			throws SQLException, HibernateException {
+		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = getColumnNames();
 			int queryCols = returnTypes.length;
-			if ( holderClass == null && queryCols == 1 ) {
-				return returnTypes[0].nullSafeGet( rs, scalarColumns[0], session, null );
-			}
-			else {
-				row = new Object[queryCols];
-				for ( int i = 0; i < queryCols; i++ )
-					row[i] = returnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
-				return row;
+			resultRow = new Object[queryCols];
+			for ( int i = 0; i < queryCols; i++ ) {
+				resultRow[i] = returnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
-		else if ( holderClass == null ) {
-			return row.length == 1 ? row[0] : row;
-		}
 		else {
-			return row;
+			resultRow = toResultRow( row );
 		}
-
+		return resultRow;
 	}
 
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
 
 	protected boolean upgradeLocks() {
 		return true;
 	}
 
 	protected int[] getCollectionOwners() {
 		return new int[] { collectionOwnerColumn };
 	}
 
 	protected boolean isCompiled() {
 		return compiled;
 	}
 
 	public String toString() {
 		return queryString;
 	}
 
 	protected int[] getOwners() {
 		return owners;
 	}
 
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
 
 	public String getQueryIdentifier() {
 		return queryIdentifier;
 	}
 
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
diff --git a/core/src/main/java/org/hibernate/loader/Loader.java b/core/src/main/java/org/hibernate/loader/Loader.java
index 70d8d0b797..437d85d6fd 100644
--- a/core/src/main/java/org/hibernate/loader/Loader.java
+++ b/core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2680 +1,2664 @@
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
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.LockOptions;
 import org.hibernate.cache.FilterKey;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.QueryKey;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.EntityUniqueKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.TwoPhaseLoad;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.impl.FetchingScrollableResultsImpl;
 import org.hibernate.impl.ScrollableResultsImpl;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 import org.hibernate.util.StringHelper;
 
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
 
 	private static final Logger log = LoggerFactory.getLogger( Loader.class );
 
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
 	protected abstract String getSQLString();
 
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
 	protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws HibernateException {
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
 	protected String preprocessSQL(String sql, QueryParameters parameters, Dialect dialect)
 			throws HibernateException {
 
 		sql = applyLocks( sql, parameters.getLockOptions(), dialect );
 		
 		return getFactory().getSettings().isCommentsEnabled() ?
 				prependComment( sql, parameters ) : sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuffer( comment.length() + sql.length() + 5 )
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
 	private List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
+		return doQueryAndInitializeNonLazyCollections(
+				session,
+				queryParameters,
+				returnProxies,
+				null
+		);
+	}
+
+	private List doQueryAndInitializeNonLazyCollections(
+			final SessionImplementor session,
+			final QueryParameters queryParameters,
+			final boolean returnProxies,
+			final ResultTransformer forcedResultTransformer)
+			throws HibernateException, SQLException {
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
-				result = doQuery( session, queryParameters, returnProxies );
+				result = doQuery( session, queryParameters, returnProxies, forcedResultTransformer );
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
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
 				if ( result == null ) {
 					result = loaded;
 				}
 			} 
 			while ( keyToRead.equals( loadedKeys[0] ) && resultSet.next() );
 		}
 		catch ( SQLException sqle ) {
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
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
 			// key value upon which to doAfterTransactionCompletion the breaking logic.  However,
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
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
 			return new EntityKey( 
 					optionalId,
 					session.getEntityPersister( optionalEntityName, optionalObject ), 
 					session.getEntityMode()
 				);
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
+		return getRowFromResultSet(
+				resultSet,
+				session,
+				queryParameters,
+				lockModesArray,
+				optionalObjectKey,
+				hydratedObjects,
+				keys,
+				returnProxies,
+				null
+		);
+	}
 
+	private Object getRowFromResultSet(
+	        final ResultSet resultSet,
+	        final SessionImplementor session,
+	        final QueryParameters queryParameters,
+	        final LockMode[] lockModesArray,
+	        final EntityKey optionalObjectKey,
+	        final List hydratedObjects,
+	        final EntityKey[] keys,
+	        boolean returnProxies,
+	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
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
 
-		return getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session );
-
+		return forcedResultTransformer == null ?
+				getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session ) :
+				forcedResultTransformer.transformTuple(
+						getResultRow( row, resultSet, session ),
+						getResultRowAliases()
+				)
+		;
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
 			keys[ entitySpan - 1 ] = new EntityKey( optionalId, persisters[ entitySpan - 1 ], session.getEntityMode() );
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
 							keys[targetIndex] = new EntityKey( targetId, persisters[targetIndex], session.getEntityMode() );
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
 							object = instanceNotYetLoaded(
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
 			keys[i] = resolvedId == null ? null : new EntityKey( resolvedId, persisters[i], session.getEntityMode() );
 		}
 	}
 
 	private Serializable determineResultId(SessionImplementor session, Serializable optionalId, Type idType, Serializable resolvedId) {
 		final boolean idIsResultId = optionalId != null
 				&& resolvedId != null
 				&& idType.isEqual( optionalId, resolvedId, session.getEntityMode(), factory );
 		final Serializable resultId = idIsResultId ? optionalId : resolvedId;
 		return resultId;
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
-			final boolean returnProxies) throws SQLException, HibernateException {
+			final boolean returnProxies,
+			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = hasMaxRows( selection ) ?
 				selection.getMaxRows().intValue() :
 				Integer.MAX_VALUE;
 
 		final int entitySpan = getEntityPersisters().length;
 
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 		final ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), selection, session );
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final List results = new ArrayList();
 
 		try {
 
 			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 
 			EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 
 			if ( log.isTraceEnabled() ) log.trace( "processing result set" );
 
 			int count;
 			for ( count = 0; count < maxRows && rs.next(); count++ ) {
 				
 				if ( log.isTraceEnabled() ) log.debug("result set row: " + count);
 
 				Object result = getRowFromResultSet( 
 						rs,
 						session,
 						queryParameters,
 						lockModesArray,
 						optionalObjectKey,
 						hydratedObjects,
 						keys,
-						returnProxies 
+						returnProxies,
+						forcedResultTransformer
 				);
 				results.add( result );
 
 				if ( createSubselects ) {
 					subselectResultKeys.add(keys);
 					keys = new EntityKey[entitySpan]; //can't reuse in this case
 				}
 				
 			}
 
 			if ( log.isTraceEnabled() ) {
 				log.trace( "done processing result set (" + count + " rows)" );
 			}
 
 		}
 		finally {
 			session.getBatcher().closeQueryStatement( st, rs );
 		}
 
 		initializeEntitiesAndCollections( hydratedObjects, rs, session, queryParameters.isReadOnly( session ) );
 
 		if ( createSubselects ) createSubselects( subselectResultKeys, queryParameters, session );
 
 		return results; //getResultList(results);
 
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
 			final boolean readOnly) 
 	throws HibernateException {
 		
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
 			if ( log.isTraceEnabled() ) {
 				log.trace( "total objects hydrated: " + hydratedObjectsSize );
 			}
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
-	
+
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
-	 * @param transformer, the specified transformer
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
-	protected boolean areResultSetRowsTransformedImmediately(ResultTransformer transformer) {
+	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
+	 * Returns the aliases that corresponding to a result row.
+	 * @return Returns the aliases that corresponding to a result row.
+	 */
+	protected String[] getResultRowAliases() {
+		 return null;
+	}
+
+	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return row;
 	}
 
+	protected boolean[] includeInResultRow() {
+		return null;
+	}
+
+	protected Object[] getResultRow(Object[] row,
+														 ResultSet rs,
+														 SessionImplementor session)
+			throws SQLException, HibernateException {
+		return row;
+	}
+	
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
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"found row of collection: " +
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) 
 					);
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
 
 			if ( log.isDebugEnabled() ) {
 				log.debug( 
 						"result set contains (possibly empty) collection: " +
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) 
 					);
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
 	
 					if ( log.isDebugEnabled() ) {
 						log.debug( 
 								"result set contains (possibly empty) collection: " +
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) 
 							);
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
 					idType.isEqual( id, resultId, session.getEntityMode(), factory );
 			
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ?
 				null :
 				new EntityKey( resultId, persister, session.getEntityMode() );
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
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"result row: " + 
 					StringHelper.toString( keys ) 
 				);
 		}
 
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
 	        final LockMode lockMode,
 	        final SessionImplementor session) 
 	throws HibernateException, SQLException {
 		if ( !persister.isInstance( object, session.getEntityMode() ) ) {
 			throw new WrongClassException( 
 					"loaded object was of wrong class " + object.getClass(), 
 					key.getIdentifier(), 
 					persister.getEntityName() 
 				);
 		}
 
 		if ( LockMode.NONE != lockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 
 			final boolean isVersionCheckNeeded = persister.isVersioned() &&
 					session.getPersistenceContext().getEntry(object)
 							.getLockMode().lessThan( lockMode );
 			// we don't need to worry about existing version being uninitialized
 			// because this block isn't called by a re-entrant load (re-entrant
 			// loads _always_ have lock mode NONE)
 			if (isVersionCheckNeeded) {
 				//we only check the version when _upgrading_ lock modes
 				checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				//we need to upgrade the lock mode to the mode requested
 				session.getPersistenceContext().getEntry(object)
 						.setLockMode(lockMode);
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
 
 		if ( log.isTraceEnabled() ) {
 			log.trace( 
 					"Initializing object from ResultSet: " + 
 					MessageHelper.infoString( persister, id, getFactory() ) 
 				);
 		}
 		
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
 						session.getEntityMode(), session.getFactory()
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
 
 		final int firstRow = getFirstRow( selection );
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
 
 	private static boolean hasMaxRows(RowSelection selection) {
 		return selection != null && selection.getMaxRows() != null;
 	}
 
 	private static int getFirstRow(RowSelection selection) {
 		if ( selection == null || selection.getFirstRow() == null ) {
 			return 0;
 		}
 		else {
 			return selection.getFirstRow().intValue();
 		}
 	}
 
 	private int interpretFirstRow(int zeroBasedFirstResult) {
 		return getFactory().getDialect().convertToFirstRowValue( zeroBasedFirstResult );
 	}
 
 	/**
 	 * Should we pre-process the SQL string, adding a dialect-specific
 	 * LIMIT clause.
 	 */
 	private static boolean useLimit(final RowSelection selection, final Dialect dialect) {
 		return dialect.supportsLimit() && hasMaxRows( selection );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        final QueryParameters queryParameters,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		queryParameters.processFilters( getSQLString(), session );
 		String sql = queryParameters.getFilteredSQL();
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = useLimit( selection, dialect );
 		boolean hasFirstRow = getFirstRow( selection ) > 0;
 		boolean useOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 		
 		boolean useScrollableResultSetToSkip = hasFirstRow &&
 				!useOffset &&
 				getFactory().getSettings().isScrollableResultSetsEnabled();
 		ScrollMode scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;
 
 		if ( useLimit ) {
 			sql = dialect.getLimitString( 
 					sql.trim(), //use of trim() here is ugly?
 					useOffset ? getFirstRow(selection) : 0, 
 					getMaxOrLimit(selection, dialect) 
 				);
 		}
 
 		sql = preprocessSQL( sql, queryParameters, dialect );
 		
 		PreparedStatement st = null;
 		
 		if (callable) {
 			st = session.getBatcher()
 				.prepareCallableQueryStatement( sql, scroll || useScrollableResultSetToSkip, scrollMode );
 		} 
 		else {
 			st = session.getBatcher()
 				.prepareQueryStatement( sql, scroll || useScrollableResultSetToSkip, scrollMode );
 		}
 				
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			if ( useLimit && dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			if ( useLimit && !dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 
 			if ( !useLimit ) {
 				setMaxRows( st, selection );
 			}
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout().intValue() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize().intValue() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						log.debug(
 								"Lock timeout [" + lockOptions.getTimeOut() +
 										"] requested but dialect reported to not support lock timeouts"
 						);
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			log.trace( "Bound [" + col + "] parameters total" );
 		}
 		catch ( SQLException sqle ) {
 			session.getBatcher().closeQueryStatement( st, null );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getBatcher().closeQueryStatement( st, null );
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Some dialect-specific LIMIT clauses require the maximum last row number
 	 * (aka, first_row_number + total_row_count), while others require the maximum
 	 * returned row count (the total maximum number of rows to return).
 	 *
 	 * @param selection The selection criteria
 	 * @param dialect The dialect
 	 * @return The appropriate value to bind into the limit clause.
 	 */
 	private static int getMaxOrLimit(final RowSelection selection, final Dialect dialect) {
 		final int firstRow = dialect.convertToFirstRowValue( getFirstRow( selection ) );
 		final int lastRow = selection.getMaxRows().intValue();
 		if ( dialect.useMaxForLimit() ) {
 			return lastRow + firstRow;
 		}
 		else {
 			return lastRow;
 		}
 	}
 
 	/**
 	 * Bind parameter values needed by the dialect-specific LIMIT clause.
 	 *
 	 * @param statement The statement to which to bind limit param values.
 	 * @param index The bind position from which to start binding
 	 * @param selection The selection object containing the limit information.
 	 * @return The number of parameter values bound.
 	 * @throws java.sql.SQLException Indicates problems binding parameter values.
 	 */
 	private int bindLimitParameters(
 			final PreparedStatement statement,
 			final int index,
 			final RowSelection selection) throws SQLException {
 		Dialect dialect = getFactory().getDialect();
 		if ( !dialect.supportsVariableLimit() ) {
 			return 0;
 		}
 		if ( !hasMaxRows( selection ) ) {
 			throw new AssertionFailure( "no max results set" );
 		}
 		int firstRow = interpretFirstRow( getFirstRow( selection ) );
 		int lastRow = getMaxOrLimit( selection, dialect );
 		boolean hasFirstRow = dialect.supportsLimitOffset() && ( firstRow > 0 || dialect.forceLimitUsage() );
 		boolean reverse = dialect.bindLimitParametersInReverseOrder();
 		if ( hasFirstRow ) {
 			statement.setInt( index + ( reverse ? 1 : 0 ), firstRow );
 		}
 		statement.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
 		return hasFirstRow ? 2 : 1;
 	}
 
 	/**
 	 * Use JDBC API to limit the number of rows returned by the SQL query if necessary
 	 */
 	private void setMaxRows(
 			final PreparedStatement st,
 			final RowSelection selection) throws SQLException {
 		if ( hasMaxRows( selection ) ) {
 			st.setMaxRows( selection.getMaxRows().intValue() + interpretFirstRow( getFirstRow( selection ) ) );
 		}
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
 			final Map namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
 					if ( log.isDebugEnabled() ) {
 						log.debug(
 								"bindNamedParameters() " +
 								typedval.getValue() + " -> " + name +
 								" [" + ( locs[i] + startIndex ) + "]"
 							);
 					}
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Fetch a <tt>PreparedStatement</tt>, call <tt>setMaxRows</tt> and then execute it,
 	 * advance to the first result and return an SQL <tt>ResultSet</tt>
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final boolean autodiscovertypes,
 	        final boolean callable,
 	        final RowSelection selection,
 	        final SessionImplementor session) 
 	throws SQLException, HibernateException {
 	
 		ResultSet rs = null;
 		try {
 			Dialect dialect = getFactory().getDialect();
 			if (callable) {
 				rs = session.getBatcher().getResultSet( (CallableStatement) st, dialect );
 			} 
 			else {
 				rs = session.getBatcher().getResultSet( st );
 			}
 			rs = wrapResultSetIfEnabled( rs , session );
 			
 			if ( !dialect.supportsLimitOffset() || !useLimit( selection, dialect ) ) {
 				advance( rs, selection );
 			}
 			
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			session.getBatcher().closeQueryStatement( st, rs );
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 		
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 				log.debug("Wrapping result set [" + rs + "]");
 				return session.getFactory()
 						.getSettings()
 						.getJdbcSupport().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				log.info("Error wrapping result set", e);
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			log.trace("Building columnName->columnIndex cache");
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
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
 		
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"loading entity: " + 
 					MessageHelper.infoString( persister, id, identifierType, getFactory() ) 
 				);
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
 			        sqle,
 			        "could not load an entity: " + 
 			        MessageHelper.infoString( persisters[persisters.length-1], id, identifierType, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		log.debug("done entity load");
 		
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
 		
 		if ( log.isDebugEnabled() ) {
 			log.debug( "loading collection element by index" );
 		}
 
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
 			        sqle,
 			        "could not collection element by index",
 			        getSQLString()
 				);
 		}
 
 		log.debug("done entity load");
 		
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
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"batch loading entity: " + 
 					MessageHelper.infoString(persister, ids, getFactory() ) 
 				);
 		}
 
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
 			        sqle,
 			        "could not load an entity batch: " + 
 			        MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		log.debug("done entity batch load");
 		
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"loading collection: "+ 
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() )
 				);
 		}
 
 		Serializable[] ids = new Serializable[]{id};
 		try {
 			doQueryAndInitializeNonLazyCollections( 
 					session,
 					new QueryParameters( new Type[]{type}, ids, ids ),
 					true 
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw JDBCExceptionHelper.convert(
 					factory.getSQLExceptionConverter(),
 					sqle,
 					"could not initialize a collection: " + 
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 				);
 		}
 	
 		log.debug("done loading collection");
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Type type) throws HibernateException {
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( 
 					"batch loading collection: "+ 
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() )
 				);
 		}
 
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
 			        sqle,
 			        "could not initialize a collection batch: " + 
 			        MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 		
 		log.debug("done batch load");
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Object[] parameterValues,
 	        final Type[] parameterTypes,
 	        final Map namedParameters,
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
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
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
 	        final Set querySpaces,
 	        final Type[] resultTypes) throws HibernateException {
 
 		final boolean cacheable = factory.getSettings().isQueryCacheEnabled() && 
 			queryParameters.isCacheable();
 
 		if ( cacheable ) {
 			return listUsingQueryCache( session, queryParameters, querySpaces, resultTypes );
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
 			final Set querySpaces, 
 			final Type[] resultTypes) {
 	
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 		
-		Set filterKeys = FilterKey.createFilterKeys( 
-				session.getLoadQueryInfluencers().getEnabledFilters(),
-				session.getEntityMode() 
-		);
-		QueryKey key = QueryKey.generateQueryKey(
-				getSQLString(), 
-				queryParameters, 
-				filterKeys, 
-				session,
-				( areResultSetRowsTransformedImmediately( queryParameters.getResultTransformer() ) ?
-						queryParameters.getResultTransformer() :
-						null
-				)
-		);
-		
+		QueryKey key = generateQueryKey( session, queryParameters );
+
 		if ( querySpaces == null || querySpaces.size() == 0 ) {
 			log.trace( "unexpected querySpaces is "+( querySpaces == null ? "null" : "empty" ) );
 		}
 		else {
 			log.trace( "querySpaces is "+querySpaces.toString() );
 		}
 
 		List result = getResultFromQueryCache(
 				session, 
 				queryParameters, 
-				querySpaces, 
+				querySpaces,
 				resultTypes, 
 				queryCache, 
 				key 
 			);
 
 		if ( result == null ) {
-			result = doList( session, queryParameters );
+			result = doList( session, queryParameters, key.getResultTransformer() );
 
 			putResultInQueryCache(
 					session, 
 					queryParameters, 
 					resultTypes,
 					queryCache, 
 					key, 
 					result 
 			);
 		}
 
+		ResultTransformer resolvedTransformer = resolveResultTransformer( queryParameters.getResultTransformer() );
+		if ( resolvedTransformer != null ) {
+			result = (
+					areResultSetRowsTransformedImmediately() ?
+							key.getResultTransformer().retransformResults(
+									result,
+									getResultRowAliases(),
+									queryParameters.getResultTransformer(),
+									includeInResultRow()
+							) :
+							key.getResultTransformer().untransformToTuples(
+									result
+							)
+			);
+		}
 
 		return getResultList( result, queryParameters.getResultTransformer() );
 	}
 
+	private QueryKey generateQueryKey(
+			SessionImplementor session,
+			QueryParameters queryParameters) {
+		return QueryKey.generateQueryKey(
+				getSQLString(),
+				queryParameters,
+				FilterKey.createFilterKeys(
+						session.getLoadQueryInfluencers().getEnabledFilters(),
+						session.getEntityMode()
+				),
+				session,
+				createCacheableResultTransformer( queryParameters )
+		);
+	}
+
+	private CacheableResultTransformer createCacheableResultTransformer(QueryParameters queryParameters) {
+		return CacheableResultTransformer.create(
+				queryParameters.getResultTransformer(),
+				getResultRowAliases(),
+				includeInResultRow()
+		);
+	}
+
 	private List getResultFromQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set querySpaces,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key) {
 		List result = null;
 
 		if ( session.getCacheMode().isGetEnabled() ) {
 			boolean isImmutableNaturalKeyLookup = queryParameters.isNaturalKeyLookup()
 					&& getEntityPersisters()[0].getEntityMetamodel().hasImmutableNaturalId();
 
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
-				result = queryCache.get( key, resultTypes, isImmutableNaturalKeyLookup, querySpaces, session );
-				logCachedResultDetails(
-						key.getResultTransformer(),
-						resultTypes,
-						result
+				result = queryCache.get(
+						key,
+						key.getResultTransformer().getCachedResultTypes( resultTypes ),
+						isImmutableNaturalKeyLookup,
+						querySpaces,
+						session
 				);
 			}
 			finally {
 				persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 			}
 
-			// If there is a result transformer, but the loader is not expecting the data to be
-			// transformed yet, then the loader expects result elements that are Object[].
-			// The problem is that StandardQueryCache.get(...) does not return a tuple when
-			// resultTypes.length == 1. The following changes the data returned from the cache
-			// to be a tuple.
-			// TODO: this really doesn't belong here, but only Loader has the information
-			// to be able to do this.
-			if ( result != null &&
-					resultTypes.length == 1 &&
-					key.getResultTransformer() == null &&
-					resolveResultTransformer( queryParameters.getResultTransformer() ) != null ) {
-				for ( int i = 0 ; i < result.size() ; i++ ) {
-					result.set( i, new Object[] { result.get( i ) } );
-				}
-			}
-
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
 
 	private void putResultInQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		if ( session.getCacheMode().isPutEnabled() ) {
-			if ( log.isTraceEnabled() ) {
-				logCachedResultDetails(
-						key.getResultTransformer(),
-						resultTypes,
-						result
-				);
-			}
-			// If there is a result transformer, but the data has not been transformed yet,
-			// then result elements are Object[]. The problem is that StandardQueryCache.put(...)
-			// does not expect a tuple when resultTypes.length == 1. The following changes the
-			// data being cached to what StandardQueryCache.put(...) expects.
-			// TODO: this really doesn't belong here, but only Loader has the information
-			// to be able to do this.
-			List cachedResult = result;
-			if ( resultTypes.length == 1 &&
-					key.getResultTransformer() == null &&
-					resolveResultTransformer( queryParameters.getResultTransformer() ) != null ) {
-				cachedResult = new ArrayList( result.size() );
-				for ( int i = 0 ; i < result.size() ; i++ ) {
-					cachedResult.add( ( ( Object[] ) result.get( i ) )[ 0 ] );
-				}
-			}
-
-			boolean put = queryCache.put( key, resultTypes, cachedResult, queryParameters.isNaturalKeyLookup(), session );
+			boolean put = queryCache.put(
+					key,
+					key.getResultTransformer().getCachedResultTypes( resultTypes ),
+					result, 
+					queryParameters.isNaturalKeyLookup(),
+					session
+			);
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor()
 						.queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
-	private void logCachedResultDetails(ResultTransformer resultTransformer, Type[] returnTypes, List result) {
-		if ( ! log.isTraceEnabled() ) {
-			return;
-		}
-		if ( returnTypes == null || returnTypes.length == 0 ) {
-				log.trace( "unexpected returnTypes is "+( returnTypes == null ? "null" : "empty" )+
-						"! transformer="+( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-						" result"+( result == null ? " is null": ".size()=" + result.size() ) );
-		}
-		else {
-			StringBuffer returnTypeNames = new StringBuffer();
-			StringBuffer returnClassNames = new StringBuffer();
-			for ( int i=0; i<returnTypes.length; i++ ) {
-				returnTypeNames.append( returnTypes[ i ].getName() ).append(' ');
-				returnClassNames.append( returnTypes[ i ].getReturnedClass() ).append(' ');
-			}
-			log.trace( "transformer="+( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-					" returnTypes=[ "+returnTypeNames+"]"+" returnClasses=[ "+returnClassNames+"]" );
-		}
-		if ( result != null && result.size() != 0 ) {
-			for ( Iterator it = result.iterator(); it.hasNext(); ) {
-			 	Object value = it.next();
-				if ( value == null ) {
-					log.trace( "transformer="+( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-							" value is null; returnTypes is "+( returnTypes == null ? "null" : "Type["+returnTypes.length+"]" ) );
-					if ( returnTypes != null && returnTypes.length > 1 ) {
-						log.trace( "unexpected result value! "+
-								"transformer="+( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-								"value is null; should be Object["+returnTypes.length+"]!" );
-					}
-				}
-				else {
-					if ( returnTypes == null || returnTypes.length == 0 ) {
-						log.trace( "unexpected result value! "+
-								"transformer="+( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-								"value is non-null; returnTypes is "+( returnTypes == null ? "null" : "empty" ) );
-					}
-					else if ( Object[].class.isInstance( value ) ) {
-						Object[] tuple = ( Object[] ) value;
-						log.trace( "transformer="+( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-								" value is Object["+tuple.length+
-								"]; returnTypes is Type["+returnTypes.length+"]" );
-						if ( tuple.length != returnTypes.length ) {
-							log.trace( "unexpected tuple length! transformer="+
-								( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-								" expected="+returnTypes.length+
-								" got="+tuple.length );
-						}
-						else {
-							for ( int j = 0; j < tuple.length; j++ ) {
-								if ( tuple[ j ] != null && ! returnTypes[ j ].getReturnedClass().isInstance( tuple[ j ] ) ) {
-									log.trace( "unexpected tuple value type! transformer="+
-											( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-											" expected="+returnTypes[ j ].getReturnedClass().getName()+
-											" got="+tuple[ j ].getClass().getName() );
-								}
-							}
-						}
-					}
-					else {
-						if ( returnTypes.length != 1 ) {
-							log.trace( "unexpected number of result columns! should be Object["+returnTypes.length+"]! transformer="+
-									( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-									" value type="+value.getClass().getName()+
-									" returnTypes is Type["+returnTypes.length+"]" );
-						}
-						else if ( ! returnTypes[ 0 ].getReturnedClass().isInstance( value ) ) {
-							log.trace( "unexpected value type! transformer="+
-									( resultTransformer == null ? "null" : resultTransformer.getClass().getName() )+
-									" expected="+returnTypes[ 0 ].getReturnedClass().getName()+
-									" got="+ value.getClass().getName() );
-						}
-					}
-				}
-			}
-		}
-	}
-
-
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
+
 	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
+		return doList( session, queryParameters, null);
+	}
+
+	private List doList(final SessionImplementor session,
+						final QueryParameters queryParameters,
+						final ResultTransformer forcedResultTransformer)
+			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		List result;
 		try {
-			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true );
+			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch ( SQLException sqle ) {
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
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
 		return;
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
 	        final Type[] returnTypes,
 	        final HolderInstantiator holderInstantiator,
 	        final SessionImplementor session) throws HibernateException {
 
 		checkScrollability();
 
 		final boolean stats = getQueryIdentifier() != null &&
 				getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 
 			PreparedStatement st = prepareQueryStatement( queryParameters, true, session );
 			ResultSet rs = getResultSet(st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), queryParameters.getRowSelection(), session);
 
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
 						returnTypes,
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
 						returnTypes,
 						holderInstantiator
 					);
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw JDBCExceptionHelper.convert(
 			        factory.getSQLExceptionConverter(),
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
 
 	public String toString() {
 		return getClass().getName() + '(' + getSQLString() + ')';
 	}
 
 }
diff --git a/core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java b/core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
index 633527652e..181433be4b 100755
--- a/core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
+++ b/core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
@@ -1,241 +1,262 @@
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
 
 import java.util.ArrayList;
+import java.util.Arrays;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.impl.CriteriaImpl;
 import org.hibernate.loader.AbstractEntityJoinWalker;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.util.ArrayHelper;
 
 /**
  * A <tt>JoinWalker</tt> for <tt>Criteria</tt> queries.
  *
  * @see CriteriaLoader
  * @author Gavin King
  */
 public class CriteriaJoinWalker extends AbstractEntityJoinWalker {
 
 	//TODO: add a CriteriaImplementor interface
 	//      this class depends directly upon CriteriaImpl in the impl package...
 
 	private final CriteriaQueryTranslator translator;
 	private final Set querySpaces;
 	private final Type[] resultTypes;
+	private final boolean[] includeInResultRow;
+
 	//the user visible aliases, which are unknown to the superclass,
 	//these are not the actual "physical" SQL aliases
 	private final String[] userAliases;
 	private final List userAliasList = new ArrayList();
+	private final List resultTypeList = new ArrayList();
+	private final List includeInResultRowList = new ArrayList();
 
 	public Type[] getResultTypes() {
 		return resultTypes;
 	}
 
 	public String[] getUserAliases() {
 		return userAliases;
 	}
 
+	public boolean[] includeInResultRow() {
+		return includeInResultRow;
+	}
+
 	public CriteriaJoinWalker(
 			final OuterJoinLoadable persister, 
 			final CriteriaQueryTranslator translator,
 			final SessionFactoryImplementor factory, 
 			final CriteriaImpl criteria, 
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers) {
 		this( persister, translator, factory, criteria, rootEntityName, loadQueryInfluencers, null );
 	}
 
 	public CriteriaJoinWalker(
 			final OuterJoinLoadable persister,
 			final CriteriaQueryTranslator translator,
 			final SessionFactoryImplementor factory,
 			final CriteriaImpl criteria,
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers,
 			final String alias) {
 		super( persister, factory, loadQueryInfluencers, alias );
 
 		this.translator = translator;
 
 		querySpaces = translator.getQuerySpaces();
 
-		if ( translator.hasProjection() ) {
-			resultTypes = translator.getProjectedTypes();
-			
-			initProjection( 
+		if ( translator.hasProjection() ) {			
+			initProjection(
 					translator.getSelect(), 
 					translator.getWhereCondition(), 
 					translator.getOrderBy(),
 					translator.getGroupBy(),
 					LockOptions.NONE  
 				);
+			resultTypes = translator.getProjectedTypes();
+			userAliases = translator.getProjectedAliases();
+			includeInResultRow = new boolean[ resultTypes.length ];
+			Arrays.fill( includeInResultRow, true );
 		}
 		else {
-			resultTypes = new Type[] { factory.getTypeResolver().getTypeFactory().manyToOne( persister.getEntityName() ) };
-
 			initAll( translator.getWhereCondition(), translator.getOrderBy(), LockOptions.NONE );
+			// root entity comes last
+			userAliasList.add( criteria.getAlias() ); //root entity comes *last*
+			resultTypeList.add( translator.getResultType( criteria ) );
+			includeInResultRowList.add( true );
+			userAliases = ArrayHelper.toStringArray( userAliasList );
+			resultTypes = ArrayHelper.toTypeArray( resultTypeList );
+			includeInResultRow = ArrayHelper.toBooleanArray( includeInResultRowList );
 		}
-		
-		userAliasList.add( criteria.getAlias() ); //root entity comes *last*
-		userAliases = ArrayHelper.toStringArray(userAliasList);
-
 	}
 
 	protected int getJoinType(
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
 		if ( translator.isJoin( path.getFullPath() ) ) {
 			return translator.getJoinType( path.getFullPath() );
 		}
 		else {
 			if ( translator.hasProjection() ) {
 				return -1;
 			}
 			else {
 				FetchMode fetchMode = translator.getRootCriteria().getFetchMode( path.getFullPath() );
 				if ( isDefaultFetchMode( fetchMode ) ) {
 					if ( isJoinFetchEnabledByProfile( persister, path, propertyNumber ) ) {
 						return getJoinType( nullable, currentDepth );
 					}
 					else {
 						return super.getJoinType(
 								persister,
 								path,
 								propertyNumber,
 								associationType,
 								metadataFetchMode,
 								metadataCascadeStyle,
 								lhsTable,
 								lhsColumns,
 								nullable,
 								currentDepth
 						);
 					}
 				}
 				else {
 					if ( fetchMode == FetchMode.JOIN ) {
 						isDuplicateAssociation( lhsTable, lhsColumns, associationType ); //deliberately ignore return value!
 						return getJoinType( nullable, currentDepth );
 					}
 					else {
 						return -1;
 					}
 				}
 			}
 		}
 	}
 
 	protected int getJoinType(
 			AssociationType associationType,
 			FetchMode config,
 			PropertyPath path,
 			String lhsTable,
 			String[] lhsColumns,
 			boolean nullable,
 			int currentDepth,
 			CascadeStyle cascadeStyle) throws MappingException {
 		return ( translator.isJoin( path.getFullPath() ) ?
 				translator.getJoinType( path.getFullPath() ) :
 				super.getJoinType(
 						associationType,
 						config,
 						path,
 						lhsTable,
 						lhsColumns,
 						nullable,
 						currentDepth,
 						cascadeStyle
 				)
 		);
 	}
 
 	private static boolean isDefaultFetchMode(FetchMode fetchMode) {
 		return fetchMode==null || fetchMode==FetchMode.DEFAULT;
 	}
 
 	/**
 	 * Use the discriminator, to narrow the select to instances
 	 * of the queried subclass, also applying any filters.
 	 */
 	protected String getWhereFragment() throws MappingException {
 		return super.getWhereFragment() +
 			( (Queryable) getPersister() ).filterFragment( getAlias(), getLoadQueryInfluencers().getEnabledFilters() );
 	}
 	
 	protected String generateTableAlias(int n, PropertyPath path, Joinable joinable) {
+		// TODO: deal with side-effects (changes to includeInSelectList, userAliasList, resultTypeList)!!!
 		if ( joinable.consumesEntityAlias() ) {
 			final Criteria subcriteria = translator.getCriteria( path.getFullPath() );
 			String sqlAlias = subcriteria==null ? null : translator.getSQLAlias(subcriteria);
 			if (sqlAlias!=null) {
-				userAliasList.add( subcriteria.getAlias() ); //alias may be null
+				if ( ! translator.hasProjection() ) {
+					includeInResultRowList.add( subcriteria.getAlias() != null );
+					if ( subcriteria.getAlias() != null ) {
+						userAliasList.add( subcriteria.getAlias() ); //alias may be null
+						resultTypeList.add( translator.getResultType( subcriteria ) );
+					}
+				}
 				return sqlAlias; //EARLY EXIT
 			}
 			else {
-				userAliasList.add(null);
+				if ( ! translator.hasProjection() ) {
+					includeInResultRowList.add( false );
+				}
 			}
 		}
 		return super.generateTableAlias( n + translator.getSQLAliasCount(), path, joinable );
 	}
 
 	protected String generateRootAlias(String tableName) {
 		return CriteriaQueryTranslator.ROOT_SQL_ALIAS;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 	
 	public String getComment() {
 		return "criteria query";
 	}
 
 	protected String getWithClause(PropertyPath path) {
 		return translator.getWithClause( path.getFullPath() );
 	}
 	
 }
diff --git a/core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index 7049bdcee9..f34222372d 100644
--- a/core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,213 +1,242 @@
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
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.impl.CriteriaImpl;
 import org.hibernate.loader.OuterJoinLoader;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 import org.hibernate.util.ArrayHelper;
 
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
+	private final boolean[] includeInResultRow;
+	private final int resultRowLength;
 
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
+		includeInResultRow = walker.includeInResultRow();
+		resultRowLength = ArrayHelper.countTrue( includeInResultRow );
 
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
 
+	protected String[] getResultRowAliases() {
+		return userAliases;
+	}
+
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 
-	protected boolean areResultSetRowsTransformedImmediately( ResultTransformer transformer ) {
-		// comparing to null just in case there is no transformer
-		// (there should always be a result transformer; 
-		return resolveResultTransformer( transformer ) != null;
+	protected boolean areResultSetRowsTransformedImmediately() {
+		return true;
+	}
+
+	protected boolean[] includeInResultRow() {
+		return includeInResultRow;
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 	throws SQLException, HibernateException {
+		return resolveResultTransformer( transformer ).transformTuple(
+				getResultRow( row, rs, session),
+				getResultRowAliases()
+		);
+	}
+			
+	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
+			throws SQLException, HibernateException {
 		final Object[] result;
-		final String[] aliases;
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
-			aliases = translator.getProjectedAliases();
 		}
 		else {
-			result = row;
-			aliases = userAliases;
+			result = toResultRow( row );
+		}
+		return result;
+	}
+
+	private Object[] toResultRow(Object[] row) {
+		if ( resultRowLength == row.length ) {
+			return row;
+		}
+		else {
+			Object[] result = new Object[ resultRowLength ];
+			int j = 0;
+			for ( int i = 0; i < row.length; i++ ) {
+				if ( includeInResultRow[i] ) result[j++] = row[i];
+			}
+			return result;
 		}
-		return resolveResultTransformer( transformer ).transformTuple(result, aliases);
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	protected String applyLocks(String sqlSelectString, LockOptions lockOptions, Dialect dialect) throws QueryException {
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sqlSelectString;
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
 		return dialect.applyLocksToSql( sqlSelectString, locks, keyColumnNames );
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
 
 }
diff --git a/core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java b/core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
index bdcaad0d9a..f235f75784 100755
--- a/core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
@@ -1,640 +1,647 @@
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
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.LinkedHashMap;
 
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.LockOptions;
 import org.hibernate.criterion.EnhancedProjection;
 import org.hibernate.hql.ast.util.SessionFactoryHelper;
 import org.hibernate.criterion.CriteriaQuery;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.Projection;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.impl.CriteriaImpl;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
+import org.hibernate.transform.CacheableResultTransformer;
+import org.hibernate.transform.TupleSubsetResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.StringRepresentableType;
 import org.hibernate.type.Type;
 import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.StringHelper;
 
 /**
  * @author Gavin King
  */
 public class CriteriaQueryTranslator implements CriteriaQuery {
 
 	public static final String ROOT_SQL_ALIAS = Criteria.ROOT_ALIAS + '_';
 
 	private CriteriaQuery outerQueryTranslator;
 
 	private final CriteriaImpl rootCriteria;
 	private final String rootEntityName;
 	private final String rootSQLAlias;
 	private int aliasCount = 0;
 
 	private final Map criteriaEntityNames = new LinkedHashMap();
 	private final Map criteriaSQLAliasMap = new HashMap();
 	private final Map aliasCriteriaMap = new HashMap();
 	private final Map associationPathCriteriaMap = new LinkedHashMap();
 	private final Map associationPathJoinTypesMap = new LinkedHashMap();
 	private final Map withClauseMap = new HashMap();
 	
 	private final SessionFactoryImplementor sessionFactory;
 
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
 		createAliasCriteriaMap();
 		createAssociationPathCriteriaMap();
 		createCriteriaEntityNameMap();
 		createCriteriaSQLAliasMap();
 	}
 
 	public String generateSQLAlias() {
 		return StringHelper.generateAlias( Criteria.ROOT_ALIAS, aliasCount ) + '_';
 	}
 
 	public String getRootSQLALias() {
 		return rootSQLAlias;
 	}
 
 	private Criteria getAliasedCriteria(String alias) {
 		return ( Criteria ) aliasCriteriaMap.get( alias );
 	}
 
 	public boolean isJoin(String path) {
 		return associationPathCriteriaMap.containsKey( path );
 	}
 
 	public int getJoinType(String path) {
 		Integer result = ( Integer ) associationPathJoinTypesMap.get( path );
 		return ( result == null ? Criteria.INNER_JOIN : result.intValue() );
 	}
 
 	public Criteria getCriteria(String path) {
 		return ( Criteria ) associationPathCriteriaMap.get( path );
 	}
 
 	public Set getQuerySpaces() {
 		Set result = new HashSet();
 		Iterator iter = criteriaEntityNames.values().iterator();
 		while ( iter.hasNext() ) {
 			String entityName = ( String ) iter.next();
 			result.addAll( Arrays.asList( getFactory().getEntityPersister( entityName ).getQuerySpaces() ) );
 		}
 		return result;
 	}
 
 	private void createAliasCriteriaMap() {
 		aliasCriteriaMap.put( rootCriteria.getAlias(), rootCriteria );
 		Iterator iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			Criteria subcriteria = ( Criteria ) iter.next();
 			if ( subcriteria.getAlias() != null ) {
 				Object old = aliasCriteriaMap.put( subcriteria.getAlias(), subcriteria );
 				if ( old != null ) {
 					throw new QueryException( "duplicate alias: " + subcriteria.getAlias() );
 				}
 			}
 		}
 	}
 
 	private void createAssociationPathCriteriaMap() {
 		Iterator iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria crit = ( CriteriaImpl.Subcriteria ) iter.next();
 			String wholeAssociationPath = getWholeAssociationPath( crit );
 			Object old = associationPathCriteriaMap.put( wholeAssociationPath, crit );
 			if ( old != null ) {
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			int joinType = crit.getJoinType();
 			old = associationPathJoinTypesMap.put( wholeAssociationPath, new Integer( joinType ) );
 			if ( old != null ) {
 				// TODO : not so sure this is needed...
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			if ( crit.getWithClause() != null )
 			{
 				this.withClauseMap.put(wholeAssociationPath, crit.getWithClause());
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
 				parent = ( Criteria ) aliasCriteriaMap.get( testAlias );
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
 		criteriaEntityNames.put( rootCriteria, rootEntityName );
 		Iterator iter = associationPathCriteriaMap.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			criteriaEntityNames.put(
 					me.getValue(), //the criteria instance
 			        getPathEntityName( ( String ) me.getKey() )
 			);
 		}
 	}
 
 	private String getPathEntityName(String path) {
 		Queryable persister = ( Queryable ) sessionFactory.getEntityPersister( rootEntityName );
 		StringTokenizer tokens = new StringTokenizer( path, "." );
 		String componentPath = "";
 		while ( tokens.hasMoreTokens() ) {
 			componentPath += tokens.nextToken();
 			Type type = persister.toType( componentPath );
 			if ( type.isAssociationType() ) {
 				AssociationType atype = ( AssociationType ) type;
 				persister = ( Queryable ) sessionFactory.getEntityPersister(
 						atype.getAssociatedEntityName( sessionFactory )
 				);
 				componentPath = "";
 			}
 			else if ( type.isComponentType() ) {
 				componentPath += '.';
 			}
 			else {
 				throw new QueryException( "not an association: " + componentPath );
 			}
 		}
 		return persister.getEntityName();
 	}
 
 	public int getSQLAliasCount() {
 		return criteriaSQLAliasMap.size();
 	}
 
 	private void createCriteriaSQLAliasMap() {
 		int i = 0;
 		Iterator criteriaIterator = criteriaEntityNames.entrySet().iterator();
 		while ( criteriaIterator.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) criteriaIterator.next();
 			Criteria crit = ( Criteria ) me.getKey();
 			String alias = crit.getAlias();
 			if ( alias == null ) {
 				alias = ( String ) me.getValue(); // the entity name
 			}
 			criteriaSQLAliasMap.put( crit, StringHelper.generateAlias( alias, i++ ) );
 		}
 		criteriaSQLAliasMap.put( rootCriteria, rootSQLAlias );
 	}
 
 	public CriteriaImpl getRootCriteria() {
 		return rootCriteria;
 	}
 
 	public QueryParameters getQueryParameters() {
 		LockOptions lockOptions = new LockOptions();
 		RowSelection selection = new RowSelection();
 		selection.setFirstRow( rootCriteria.getFirstResult() );
 		selection.setMaxRows( rootCriteria.getMaxResults() );
 		selection.setTimeout( rootCriteria.getTimeout() );
 		selection.setFetchSize( rootCriteria.getFetchSize() );
 
 		Iterator iter = rootCriteria.getLockModes().entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			final Criteria subcriteria = getAliasedCriteria( ( String ) me.getKey() );
 			lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), (LockMode)me.getValue() );
 		}
 		List values = new ArrayList();
 		List types = new ArrayList();
 		iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria subcriteria = ( CriteriaImpl.Subcriteria ) iter.next();
 			LockMode lm = subcriteria.getLockMode();
 			if ( lm != null ) {
 				lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), lm );
 			}
 			if ( subcriteria.getWithClause() != null )
 			{
 				TypedValue[] tv = subcriteria.getWithClause().getTypedValues( subcriteria, this );
 				for ( int i = 0; i < tv.length; i++ ) {
 					values.add( tv[i].getValue() );
 					types.add( tv[i].getType() );
 				}
 			}
 		}
 
 		// Type and value gathering for the WHERE clause needs to come AFTER lock mode gathering,
 		// because the lock mode gathering loop now contains join clauses which can contain
 		// parameter bindings (as in the HQL WITH clause).
 		iter = rootCriteria.iterateExpressionEntries();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.CriterionEntry ce = ( CriteriaImpl.CriterionEntry ) iter.next();
 			TypedValue[] tv = ce.getCriterion().getTypedValues( ce.getCriteria(), this );
 			for ( int i = 0; i < tv.length; i++ ) {
 				values.add( tv[i].getValue() );
 				types.add( tv[i].getType() );
 			}
 		}
 
 		Object[] valueArray = values.toArray();
 		Type[] typeArray = ArrayHelper.toTypeArray( types );
 		return new QueryParameters(
 				typeArray,
 		        valueArray,
 		        lockOptions,
 		        selection,
 		        rootCriteria.isReadOnlyInitialized(),
 		        ( rootCriteria.isReadOnlyInitialized() ? rootCriteria.isReadOnly() : false ),
 		        rootCriteria.getCacheable(),
 		        rootCriteria.getCacheRegion(),
 		        rootCriteria.getComment(),
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
 
+	/* package-protected */
+	Type getResultType(Criteria criteria) {
+		return getFactory().getTypeResolver().getTypeFactory().manyToOne( getEntityName( criteria ) );
+	}
+
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
 		StringBuffer condition = new StringBuffer( 30 );
 		Iterator criterionIterator = rootCriteria.iterateExpressionEntries();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.CriterionEntry entry = ( CriteriaImpl.CriterionEntry ) criterionIterator.next();
 			String sqlString = entry.getCriterion().toSqlString( entry.getCriteria(), this );
 			condition.append( sqlString );
 			if ( criterionIterator.hasNext() ) {
 				condition.append( " and " );
 			}
 		}
 		return condition.toString();
 	}
 
 	public String getOrderBy() {
 		StringBuffer orderBy = new StringBuffer( 30 );
 		Iterator criterionIterator = rootCriteria.iterateOrderings();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.OrderEntry oe = ( CriteriaImpl.OrderEntry ) criterionIterator.next();
 			orderBy.append( oe.getOrder().toSqlString( oe.getCriteria(), this ) );
 			if ( criterionIterator.hasNext() ) {
 				orderBy.append( ", " );
 			}
 		}
 		return orderBy.toString();
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return sessionFactory;
 	}
 
 	public String getSQLAlias(Criteria criteria) {
 		return ( String ) criteriaSQLAliasMap.get( criteria );
 	}
 
 	public String getEntityName(Criteria criteria) {
 		return ( String ) criteriaEntityNames.get( criteria );
 	}
 
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
 
 	public String[] getIdentifierColumns(Criteria subcriteria) {
 		String[] idcols =
 				( ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) ) ).getIdentifierColumnNames();
 		return StringHelper.qualify( getSQLAlias( subcriteria ), idcols );
 	}
 
 	public Type getIdentifierType(Criteria subcriteria) {
 		return ( ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) ) ).getIdentifierType();
 	}
 
 	public TypedValue getTypedIdentifierValue(Criteria subcriteria, Object value) {
 		final Loadable loadable = ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) );
 		return new TypedValue(
 				loadable.getIdentifierType(),
 		        value,
 		        EntityMode.POJO
 		);
 	}
 
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
 
 	public Type getType(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toType( getPropertyName( propertyName ) );
 	}
 
 	/**
 	 * Get the a typed value for the given property value.
 	 */
 	public TypedValue getTypedValue(Criteria subcriteria, String propertyName, Object value)
 			throws HibernateException {
 		// Detect discriminator values...
 		if ( value instanceof Class ) {
 			Class entityClass = ( Class ) value;
 			Queryable q = SessionFactoryHelper.findQueryableUsingImports( sessionFactory, entityClass.getName() );
 			if ( q != null ) {
 				Type type = q.getDiscriminatorType();
 				String stringValue = q.getDiscriminatorSQLValue();
 				if (stringValue != null && stringValue.length() > 2
 						&& stringValue.startsWith("'")
 						&& stringValue.endsWith("'")) { // remove the single
 														// quotes
 					stringValue = stringValue.substring(1,
 							stringValue.length() - 1);
 				}
 				
 				// Convert the string value into the proper type.
 				if ( type instanceof StringRepresentableType ) {
 					StringRepresentableType nullableType = (StringRepresentableType) type;
 					value = nullableType.fromStringValue( stringValue );
 				}
 				else {
 					throw new QueryException( "Unsupported discriminator type " + type );
 				}
 				return new TypedValue(
 						type,
 				        value,
 				        EntityMode.POJO
 				);
 			}
 		}
 		// Otherwise, this is an ordinary value.
 		return new TypedValue(
 				getTypeUsingProjection( subcriteria, propertyName ),
 		        value,
 		        EntityMode.POJO
 		);
 	}
 
 	private PropertyMapping getPropertyMapping(String entityName)
 			throws MappingException {
 		return ( PropertyMapping ) sessionFactory.getEntityPersister( entityName );
 	}
 
 	//TODO: use these in methods above
 
 	public String getEntityName(Criteria subcriteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return getEntityName( crit );
 			}
 		}
 		return getEntityName( subcriteria );
 	}
 
 	public String getSQLAlias(Criteria criteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria subcriteria = getAliasedCriteria( root );
 			if ( subcriteria != null ) {
 				return getSQLAlias( subcriteria );
 			}
 		}
 		return getSQLAlias( criteria );
 	}
 
 	public String getPropertyName(String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return propertyName.substring( root.length() + 1 );
 			}
 		}
 		return propertyName;
 	}
 
 	public String getWithClause(String path)
 	{
 		final Criterion crit = (Criterion)this.withClauseMap.get(path);
 		return crit == null ? null : crit.toSqlString(getCriteria(path), this);
 	}
 	
 }
diff --git a/core/src/main/java/org/hibernate/loader/custom/CustomLoader.java b/core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
index c0d0cfc751..7064025fea 100755
--- a/core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
@@ -1,600 +1,629 @@
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
 package org.hibernate.loader.custom;
 
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.HashSet;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.Loader;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
-import org.hibernate.type.TypeFactory;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.StringHelper;
 
 
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
 
+	private boolean[] includeInResultRow;
+
 //	private final String[] sqlAliases;
 //	private final String[] sqlAliasSuffixes;
 	private final ResultRowProcessor rowProcessor;
 
 	// this is only needed (afaict) for processing results from the query cache;
 	// however, this cannot possibly work in the case of discovered types...
 	private Type[] resultTypes;
 
 	// this is only needed (afaict) for ResultTransformer processing...
 	private String[] transformerAliases;
 
-
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
 
+		List includeInResultRowList = new ArrayList();
+
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
+				includeInResultRowList.add( true );
 				hasScalars = true;
 			}
 			else if ( rtn instanceof RootReturn ) {
 				RootReturn rootRtn = ( RootReturn ) rtn;
 				Queryable persister = ( Queryable ) factory.getEntityPersister( rootRtn.getEntityName() );
 				entityPersisters.add( persister );
 				lockModes.add( (rootRtn.getLockMode()) );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				entityOwners.add( new Integer( -1 ) );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( rootRtn.getAlias() );
 				entityAliases.add( rootRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
+				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof CollectionReturn ) {
 				CollectionReturn collRtn = ( CollectionReturn ) rtn;
 				String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 				QueryableCollection persister = ( QueryableCollection ) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				lockModes.add( collRtn.getLockMode() );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				collectionOwners.add( new Integer( -1 ) );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( collRtn.getAlias() );
 				collectionAliases.add( collRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = ( Queryable ) ( ( EntityType ) elementType ).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( new Integer( -1 ) );
 					entityAliases.add( collRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
+				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof EntityFetchReturn ) {
 				EntityFetchReturn fetchRtn = ( EntityFetchReturn ) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				entityOwners.add( new Integer( ownerIndex ) );
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
+				includeInResultRowList.add( false );
 			}
 			else if ( rtn instanceof CollectionFetchReturn ) {
 				CollectionFetchReturn fetchRtn = ( CollectionFetchReturn ) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				collectionOwners.add( new Integer( ownerIndex ) );
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
 					entityOwners.add( new Integer( ownerIndex ) );
 					entityAliases.add( fetchRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
+				includeInResultRowList.add( false );
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
+
+		this.includeInResultRow = ArrayHelper.toBooleanArray( includeInResultRowList );
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
 
 	protected String getQueryIdentifier() {
 		return sql;
 	}
 
 	protected String getSQLString() {
 		return sql;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		return lockModes;
 	}
 
 	protected Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 	
 	protected int[] getOwners() {
 		return entiytOwners;
 	}
 
 	public List list(SessionImplementor session, QueryParameters queryParameters) throws HibernateException {
 		return list( session, queryParameters, querySpaces, resultTypes );
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
 
+	protected String[] getResultRowAliases() {
+		return transformerAliases;
+	}
+
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveResultTransformer( null, resultTransformer );
 	}
-	
+
+	protected boolean[] includeInResultRow() {
+		return includeInResultRow;
+	}
+
 	protected Object getResultColumnOrRow(
 			Object[] row,
 	        ResultTransformer transformer,
 	        ResultSet rs,
 	        SessionImplementor session) throws SQLException, HibernateException {
 		return rowProcessor.buildResultRow( row, rs, transformer != null, session );
 	}
 
+	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
+			throws SQLException, HibernateException {
+		return rowProcessor.buildResultRow( row, rs, session );
+	}
+
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
 	
 	protected EntityAliases[] getEntityAliases() {
 		return entityAliases;
 	}
 
 	protected CollectionAliases[] getCollectionAliases() {
 		return collectionAliases;
 	}
 	
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
+			Object[] resultRow = buildResultRow( data, resultSet, session );
+			return ( hasTransformer )
+			       ? resultRow
+			       : ( resultRow.length == 1 )
+			         ? resultRow[0]
+			         : resultRow;
+		}
+		public Object[] buildResultRow(
+				Object[] data,
+				ResultSet resultSet,
+				SessionImplementor session) throws SQLException, HibernateException {
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
 
-			return ( hasTransformer )
-			       ? resultRow
-			       : ( resultRow.length == 1 )
-			         ? resultRow[0]
-			         : resultRow;
+			return resultRow;
 		}
 	}
 
 	private static interface ResultColumnProcessor {
 		public Object extract(Object[] data, ResultSet resultSet, SessionImplementor session) throws SQLException, HibernateException;
 		public void performDiscovery(Metadata metadata, List types, List aliases) throws SQLException, HibernateException;
 	}
 
 	public class NonScalarResultColumnProcessor implements ResultColumnProcessor {
 		private final int position;
 
 		public NonScalarResultColumnProcessor(int position) {
 			this.position = position;
 		}
 
 		public Object extract(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			return data[ position ];
 		}
 
 		public void performDiscovery(Metadata metadata, List types, List aliases) {
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
 
 		public Object extract(
 				Object[] data,
 				ResultSet resultSet,
 				SessionImplementor session) throws SQLException, HibernateException {
 			return type.nullSafeGet( resultSet, alias, session, null );
 		}
 
 		public void performDiscovery(Metadata metadata, List types, List aliases) throws SQLException {
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
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		try {
 			Metadata metadata = new Metadata( getFactory(), rs );
 			List aliases = new ArrayList();
 			List types = new ArrayList();
 
 			rowProcessor.prepareForAutoDiscovery( metadata );
 
 			for ( int i = 0; i < rowProcessor.columnProcessors.length; i++ ) {
 				rowProcessor.columnProcessors[i].performDiscovery( metadata, types, aliases );
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
 				return resultSetMetaData.getColumnName( position );
 			}
 			catch( SQLException e ) {
 				throw new HibernateException( "Could not resolve column name [" + position + "]", e );
 			}
 		}
 
 		public Type getHibernateType(int columnPos) throws SQLException {
 			int columnType = resultSetMetaData.getColumnType( columnPos );
 			int scale = resultSetMetaData.getScale( columnPos );
 			int precision = resultSetMetaData.getPrecision( columnPos );
 			return factory.getTypeResolver().heuristicType(
 					factory.getDialect().getHibernateTypeName(
 							columnType,
 							precision,
 							precision,
 							scale
 					)
 			);
 		}
 	}
 }
diff --git a/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
index 2916a62664..b5c4b183a3 100644
--- a/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,600 +1,612 @@
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
 package org.hibernate.loader.hql;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
+import java.util.Arrays;
 import java.util.HashMap;
-import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.LockOptions;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.exception.JDBCExceptionHelper;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.hql.ast.QueryTranslatorImpl;
 import org.hibernate.hql.ast.tree.FromElement;
 import org.hibernate.hql.ast.tree.SelectClause;
 import org.hibernate.hql.ast.tree.QueryNode;
 import org.hibernate.hql.ast.tree.AggregatedSelectExpression;
 import org.hibernate.impl.IteratorImpl;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.util.ArrayHelper;
 
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
 
 	private ResultTransformer implicitResultTransformer;
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
 
 		AggregatedSelectExpression aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
 		implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
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
 		return implicitResultTransformer != null;
 	}
 
+	protected String[] getResultRowAliases() {
+		return queryReturnAliases;
+	}
+	
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveResultTransformer( implicitResultTransformer, resultTransformer );
 	}
-	
+
+	protected boolean[] includeInResultRow() {
+		boolean[] includeInResultTuple = includeInSelect;
+		if ( hasScalars ) {
+			includeInResultTuple = new boolean[ queryReturnTypes.length ];
+			Arrays.fill( includeInResultTuple, true );
+		}
+		return includeInResultTuple;
+	}
+
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 
-		row = toResultRow( row );
+		Object[] resultRow = getResultRow( row, rs, session );
 		boolean hasTransform = hasSelectNew() || transformer!=null;
+		return ( ! hasTransform && resultRow.length == 1 ?
+				resultRow[ 0 ] :
+				resultRow
+		);
+	}
+
+	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
+			throws SQLException, HibernateException {
+		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = scalarColumnNames;
 			int queryCols = queryReturnTypes.length;
-			if ( !hasTransform && queryCols == 1 ) {
-				return queryReturnTypes[0].nullSafeGet( rs, scalarColumns[0], session, null );
+			resultRow = new Object[queryCols];
+			for ( int i = 0; i < queryCols; i++ ) {
+				resultRow[i] = queryReturnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
-			else {
-				row = new Object[queryCols];
-				for ( int i = 0; i < queryCols; i++ ) {
-					row[i] = queryReturnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
-				}
-				return row;
-			}
-		}
-		else if ( !hasTransform ) {
-			return row.length == 1 ? row[0] : row;
 		}
 		else {
-			return row;
+			resultRow = toResultRow( row );
 		}
-
+		return resultRow;
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
 			throw JDBCExceptionHelper.convert(
 					getFactory().getSQLExceptionConverter(),
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
diff --git a/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java b/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
index 58d93cbaf4..c560ec1511 100644
--- a/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/AliasToBeanResultTransformer.java
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
 
-import java.io.Serializable;
 import java.util.Arrays;
-import java.util.List;
 
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
-public class AliasToBeanResultTransformer implements ResultTransformer, Serializable {
+public class AliasToBeanResultTransformer extends AliasedTupleSubsetResultTransformer {
 
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
 
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
+		return false;
+	}	
+
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
 
-	public List transformList(List collection) {
-		return collection;
-	}
-
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
 
 	public int hashCode() {
 		int result = resultClass.hashCode();
 		result = 31 * result + ( aliases != null ? Arrays.hashCode( aliases ) : 0 );
 		return result;
 	}
 }
diff --git a/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java b/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
index 3c4babff00..0b00b76b3c 100644
--- a/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
@@ -1,73 +1,79 @@
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
-import java.io.Serializable;
 
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
-public class AliasToEntityMapResultTransformer extends BasicTransformerAdapter implements Serializable {
+public class AliasToEntityMapResultTransformer extends AliasedTupleSubsetResultTransformer {
 
 	public static final AliasToEntityMapResultTransformer INSTANCE = new AliasToEntityMapResultTransformer();
 
 	/**
 	 * Disallow instantiation of AliasToEntityMapResultTransformer.
 	 */
 	private AliasToEntityMapResultTransformer() {
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
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
 
 	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
+		return false;
+	}
+
+	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 }
diff --git a/core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java b/core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
new file mode 100644
index 0000000000..8c1badf196
--- /dev/null
+++ b/core/src/main/java/org/hibernate/transform/AliasedTupleSubsetResultTransformer.java
@@ -0,0 +1,58 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.transform;
+
+/**
+ * An implementation of TupleSubsetResultTransformer that ignores a
+ * tuple element if its corresponding alias is null.
+ *
+ * @author Gail Badner
+ */
+public abstract class AliasedTupleSubsetResultTransformer
+		extends BasicTransformerAdapter
+		implements TupleSubsetResultTransformer {
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
+		if ( aliases == null ) {
+			throw new IllegalArgumentException( "aliases cannot be null" );
+		}
+		if ( aliases.length != tupleLength ) {
+			throw new IllegalArgumentException(
+					"aliases and tupleLength must have the same length; " +
+							"aliases.length=" + aliases.length + "tupleLength=" + tupleLength
+			);
+		}
+		boolean[] includeInTransform = new boolean[tupleLength];
+		for ( int i = 0 ; i < aliases.length ; i++ ) {
+			if ( aliases[ i ] != null ) {
+				includeInTransform[ i ] = true;
+			}
+		}
+		return includeInTransform;
+	}
+}
\ No newline at end of file
diff --git a/core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java b/core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
new file mode 100644
index 0000000000..046143182d
--- /dev/null
+++ b/core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
@@ -0,0 +1,347 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.transform;
+
+import java.lang.reflect.Array;
+import java.util.Arrays;
+import java.util.List;
+
+import org.hibernate.type.Type;
+import org.hibernate.util.ArrayHelper;
+
+/**
+ * A ResultTransformer that is used to transfor tuples to a value(s)
+ * that can be cached.
+ *
+ * @author Gail Badner
+ */
+public class CacheableResultTransformer implements ResultTransformer {
+
+	// would be nice to be able to have this class extend
+	// PassThroughResultTransformer, but the default constructor
+	// is private (as it should be for a singleton)
+	private final static PassThroughResultTransformer ACTUAL_TRANSFORMER =
+			PassThroughResultTransformer.INSTANCE;
+	private final int tupleLength;
+	private final int tupleSubsetLength;
+
+	// array with the i-th element indicating whether the i-th
+	// expression returned by a query is included in the tuple;
+	// IMPLLEMENTATION NOTE:
+	// "joined" and "fetched" associations may use the same SQL,
+	// but result in different tuple and cached values. This is
+	// because "fetched" associations are excluded from the tuple.
+	//  includeInTuple provides a way to distinguish these 2 cases.
+	private final boolean[] includeInTuple;
+
+	// indexes for tuple that are included in the transformation;
+	// set to null if all elements in the tuple are included
+	private final int[] includeInTransformIndex;
+
+	/**
+	 * Returns a CacheableResultTransformer that is used to transform
+	 * tuples to a value(s) that can be cached.
+	 *
+	 * @param transformer - result transformer that will ultimately be
+	 *        be used (after caching results)
+	 * @param aliases - the aliases that correspond to the tuple;
+	 *        if it is non-null, its length must equal the number
+	 *        of true elements in includeInTuple[]
+	 * @param includeInTuple - array with the i-th element indicating
+	 *        whether the i-th expression returned by a query is
+	 *        included in the tuple; the number of true values equals
+	 *        the length of the tuple that will be transformed;
+	 *        must be non-null
+	 * @return a CacheableResultTransformer that is used to transform
+	 *         tuples to a value(s) that can be cached.
+	 */
+	public static CacheableResultTransformer create(ResultTransformer transformer,
+													String[] aliases,
+													boolean[] includeInTuple) {
+		return transformer instanceof TupleSubsetResultTransformer ?
+				create( ( TupleSubsetResultTransformer ) transformer, aliases, includeInTuple ) :
+				create( includeInTuple )
+		;
+	}
+
+	/**
+	 * Returns a CacheableResultTransformer that is used to transform
+	 * tuples to a value(s) that can be cached.
+	 *
+	 * @param transformer - a tuple subset result transformer;
+	 *        must be non-null;
+	 * @param aliases - the aliases that correspond to the tuple;
+	 *        if it is non-null, its length must equal the number
+	 *        of true elements in includeInTuple[]
+	 * @param includeInTuple - array with the i-th element indicating
+	 *        whether the i-th expression returned by a query is
+	 *        included in the tuple; the number of true values equals
+	 *        the length of the tuple that will be transformed;
+	 *        must be non-null
+	 * @return a CacheableResultTransformer that is used to transform
+	 *         tuples to a value(s) that can be cached.
+	 */
+	private static CacheableResultTransformer create(TupleSubsetResultTransformer transformer,
+													 String[] aliases,
+													 boolean[] includeInTuple) {
+		if ( transformer == null ) {
+			throw new IllegalArgumentException( "transformer cannot be null" );
+		}
+		int tupleLength = ArrayHelper.countTrue( includeInTuple );
+		if ( aliases != null && aliases.length != tupleLength ) {
+			throw new IllegalArgumentException(
+					"if aliases is not null, then the length of aliases[] must equal the number of true elements in includeInTuple; " +
+							"aliases.length=" + aliases.length + "tupleLength=" + tupleLength
+			);
+		}
+		return new CacheableResultTransformer(
+				includeInTuple,
+				transformer.includeInTransform( aliases, tupleLength )
+		);
+	}
+
+	/**
+	 * Returns a CacheableResultTransformer that is used to transform
+	 * tuples to a value(s) that can be cached.
+	 *
+	 * @param includeInTuple - array with the i-th element indicating
+	 *        whether the i-th expression returned by a query is
+	 *        included in the tuple; the number of true values equals
+	 *        the length of the tuple that will be transformed;
+	 *        must be non-null
+	 * @return a CacheableResultTransformer that is used to transform
+	 *         tuples to a value(s) that can be cached.
+	 */
+	private static CacheableResultTransformer create(boolean[] includeInTuple) {
+		return new CacheableResultTransformer( includeInTuple, null );
+	}
+
+	private CacheableResultTransformer(boolean[] includeInTuple, boolean[] includeInTransform) {
+		if ( includeInTuple == null ) {
+			throw new IllegalArgumentException( "includeInTuple cannot be null" );
+		}
+		this.includeInTuple = includeInTuple;
+		tupleLength = ArrayHelper.countTrue( includeInTuple );
+		tupleSubsetLength = (
+				includeInTransform == null ?
+						tupleLength :
+						ArrayHelper.countTrue( includeInTransform )
+		);
+		if ( tupleSubsetLength == tupleLength ) {
+			includeInTransformIndex = null;
+		}
+		else {
+			includeInTransformIndex = new int[tupleSubsetLength];
+			for ( int i = 0, j = 0 ; i < includeInTransform.length ; i++ ) {
+				if ( includeInTransform[ i ] ) {
+					includeInTransformIndex[ j ] =  i;
+					j++;
+				}
+			}
+		}
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public Object transformTuple(Object[] tuple, String aliases[]) {
+		if ( aliases != null && aliases.length != tupleLength ) {
+			throw new IllegalStateException(
+					"aliases expected length is " + tupleLength +
+					"; actual length is " + aliases.length );
+		}
+		// really more correct to pass index( aliases.getClass(), aliases )
+		// as the 2nd arg to the following statement;
+		// passing null instead because it ends up being ignored.
+		return ACTUAL_TRANSFORMER.transformTuple( index( tuple.getClass(), tuple ), null );
+	}
+
+	/**
+	 * Re-transforms, if necessary, a List of values previously
+	 * transformed by this (or an equivalent) CacheableResultTransformer.
+	 * Each element of the list is re-transformed in place (i.e, List
+	 * elements are replaced with re-transformed values) and the original
+	 * List is returned.
+	 * <p/>
+	 * If re-transformation is unnecessary, the original List is returned
+	 * unchanged.
+	 *
+	 * @param transformedResults - results that were previously transformed
+	 * @param aliases - the aliases that correspond to the untransformed tuple;
+	 * @param transformer - the transformer for the re-transformation
+	 * @return transformedResults, with each element re-transformed (if nececessary)
+	 */
+	public List retransformResults(List transformedResults,
+								   String aliases[],
+								   ResultTransformer transformer,
+								   boolean[] includeInTuple) {
+		if ( transformer == null ) {
+			throw new IllegalArgumentException( "transformer cannot be null" );
+		}
+		if ( ! this.equals( create( transformer, aliases, includeInTuple ) ) ) {
+			throw new IllegalStateException(
+					"this CacheableResultTransformer is inconsistent with specified arguments; cannot re-transform"
+			);
+		}
+		boolean requiresRetransform = true;
+		String[] aliasesToUse = aliases == null ? null : index( ( aliases.getClass() ), aliases );
+		if ( transformer == ACTUAL_TRANSFORMER ) {
+			requiresRetransform = false;
+		}
+		else if ( transformer instanceof TupleSubsetResultTransformer ) {
+			requiresRetransform =  ! ( ( TupleSubsetResultTransformer ) transformer ).isTransformedValueATupleElement(
+					aliasesToUse,
+					tupleLength
+			);
+		}
+		if ( requiresRetransform ) {
+			for ( int i = 0 ; i < transformedResults.size() ; i++ ) {
+				Object[] tuple = ACTUAL_TRANSFORMER.untransformToTuple(
+									transformedResults.get( i ),
+									tupleSubsetLength == 1
+				);
+				transformedResults.set( i, transformer.transformTuple( tuple, aliasesToUse ) );
+			}
+		}
+		return transformedResults;
+	}
+
+	/**
+	 * Untransforms, if necessary, a List of values previously
+	 * transformed by this (or an equivalent) CacheableResultTransformer.
+	 * Each element of the list is untransformed in place (i.e, List
+	 * elements are replaced with untransformed values) and the original
+	 * List is returned.
+	 * <p/>
+	 * If not unnecessary, the original List is returned
+	 * unchanged.
+	 * <p/>
+	 * NOTE: If transformed values are a subset of the original
+	 *       tuple, then, on return, elements corresponding to
+	 *       excluded tuple elements will be null.
+	 * @param results - results that were previously transformed
+	 * @return results, with each element untransformed (if nececessary)
+	 */
+	public List untransformToTuples(List results) {
+		if ( includeInTransformIndex == null ) {
+			results = ACTUAL_TRANSFORMER.untransformToTuples(
+					results,
+					tupleSubsetLength == 1
+			);
+		}
+		else {
+			for ( int i = 0 ; i < results.size() ; i++ ) {
+				Object[] tuple = ACTUAL_TRANSFORMER.untransformToTuple(
+									results.get( i ),
+									tupleSubsetLength == 1
+				);
+				results.set( i, unindex( tuple.getClass(), tuple ) );
+			}
+
+		}
+		return results;
+	}
+
+	/**
+	 * Returns the result types for the transformed value.
+	 * @param tupleResultTypes
+	 * @return
+	 */
+	public Type[] getCachedResultTypes(Type[] tupleResultTypes) {
+		return tupleLength != tupleSubsetLength ?
+				index( tupleResultTypes.getClass(), tupleResultTypes ) :
+				tupleResultTypes
+		;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public List transformList(List list) {
+		return list;
+	}
+
+	private <T> T[] index(Class<? extends T[]> clazz, T[] objects) {
+		T[] objectsIndexed = objects;
+		if ( objects != null &&
+				includeInTransformIndex != null &&
+				objects.length != tupleSubsetLength ) {
+			objectsIndexed = clazz.cast( Array.newInstance( clazz.getComponentType(), tupleSubsetLength ) );
+			for ( int i = 0 ; i < tupleSubsetLength; i++ ) {
+				objectsIndexed[ i ] = objects[ includeInTransformIndex[ i ] ];
+			}
+		}
+		return objectsIndexed;
+	}
+
+	private <T> T[] unindex(Class<? extends T[]> clazz, T[] objects) {
+		T[] objectsUnindexed = objects;
+		if ( objects != null &&
+				includeInTransformIndex != null &&
+				objects.length != tupleLength ) {
+			objectsUnindexed = clazz.cast( Array.newInstance( clazz.getComponentType(), tupleLength ) );
+			for ( int i = 0 ; i < tupleSubsetLength; i++ ) {
+				objectsUnindexed[ includeInTransformIndex[ i ] ] = objects[ i ];
+			}
+		}
+		return objectsUnindexed;
+	}
+
+	@Override
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+
+		CacheableResultTransformer that = ( CacheableResultTransformer ) o;
+
+		if ( tupleLength != that.tupleLength ) {
+			return false;
+		}
+		if ( tupleSubsetLength != that.tupleSubsetLength ) {
+			return false;
+		}
+		if ( !Arrays.equals( includeInTuple, that.includeInTuple ) ) {
+			return false;
+		}
+		if ( !Arrays.equals( includeInTransformIndex, that.includeInTransformIndex ) ) {
+			return false;
+		}
+
+		return true;
+	}
+
+	@Override
+	public int hashCode() {
+		int result = tupleLength;
+		result = 31 * result + tupleSubsetLength;
+		result = 31 * result + ( includeInTuple != null ? Arrays.hashCode( includeInTuple ) : 0 );
+		result = 31 * result + ( includeInTransformIndex != null ? Arrays.hashCode( includeInTransformIndex ) : 0 );
+		return result;
+	}
+}
diff --git a/core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java b/core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
index f56a490159..e255dedb09 100644
--- a/core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/DistinctResultTransformer.java
@@ -1,113 +1,113 @@
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
 import java.util.ArrayList;
 import java.util.Set;
 import java.util.HashSet;
 import java.io.Serializable;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Distinctions the result tuples in the final result based on the defined
  * equality of the tuples.
  * <p/>
  * Since this transformer is stateless, all instances would be considered equal.
  * So for optimization purposes we limit it to a single, singleton {@link #INSTANCE instance}.
  *
  * @author Steve Ebersole
  */
-public class DistinctResultTransformer extends BasicTransformerAdapter implements Serializable {
+public class DistinctResultTransformer extends BasicTransformerAdapter {
 
 	public static final DistinctResultTransformer INSTANCE = new DistinctResultTransformer();
 
 	private static final Logger log = LoggerFactory.getLogger( DistinctResultTransformer.class );
 
 	/**
 	 * Helper class to handle distincting
 	 */
 	private static final class Identity {
 		final Object entity;
 
 		private Identity(Object entity) {
 			this.entity = entity;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public boolean equals(Object other) {
 			return Identity.class.isInstance( other )
 					&& this.entity == ( ( Identity ) other ).entity;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public int hashCode() {
 			return System.identityHashCode( entity );
 		}
 	}
 
 	/**
 	 * Disallow instantiation of DistinctResultTransformer.
 	 */
 	private DistinctResultTransformer() {
 	}
 
 	/**
 	 * Uniquely distinct each tuple row here.
 	 */
 	public List transformList(List list) {
 		List result = new ArrayList( list.size() );
 		Set distinct = new HashSet();
 		for ( int i = 0; i < list.size(); i++ ) {
 			Object entity = list.get( i );
 			if ( distinct.add( new Identity( entity ) ) ) {
 				result.add( entity );
 			}
 		}
 		if ( log.isDebugEnabled() ) {
 			log.debug(
 					"transformed: " +
 							list.size() + " rows to: " +
 							result.size() + " distinct results"
 			);
 		}
 		return result;
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
diff --git a/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java b/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
index 656b39b0ff..1295f737bd 100644
--- a/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
@@ -1,80 +1,93 @@
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
-import java.io.Serializable;
 
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
-public class DistinctRootEntityResultTransformer implements ResultTransformer, Serializable {
+public class DistinctRootEntityResultTransformer implements TupleSubsetResultTransformer {
 
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
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return RootEntityResultTransformer.INSTANCE.transformTuple( tuple, aliases );
 	}
 
 	/**
 	 * Simply delegates to {@link DistinctResultTransformer#transformList}.
 	 *
 	 * @param list The list to transform.
 	 * @return The transformed List.
 	 */
 	public List transformList(List list) {
 		return DistinctResultTransformer.INSTANCE.transformList( list );
 	}
 
 	/**
+	 * {@inheritDoc}
+	 */
+	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
+		return RootEntityResultTransformer.INSTANCE.includeInTransform( aliases, tupleLength );
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
+		return RootEntityResultTransformer.INSTANCE.isTransformedValueATupleElement( null, tupleLength );
+	}
+
+	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 
 }
diff --git a/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java b/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
index 44cb9c41ee..299641b498 100644
--- a/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
@@ -1,60 +1,96 @@
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
 
-import java.io.Serializable;
+import java.util.Arrays;
+import java.util.List;
+
+import org.hibernate.util.ArrayHelper;
 
 /**
  * ???
  *
  * @author max
  */
-public class PassThroughResultTransformer extends BasicTransformerAdapter implements Serializable {
+public class PassThroughResultTransformer extends BasicTransformerAdapter implements TupleSubsetResultTransformer {
 
 	public static final PassThroughResultTransformer INSTANCE = new PassThroughResultTransformer();
 
 	/**
 	 * Disallow instantiation of PassThroughResultTransformer.
 	 */
 	private PassThroughResultTransformer() {
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return tuple.length==1 ? tuple[0] : tuple;
 	}
 
 	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
+		return tupleLength == 1;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
+		boolean[] includeInTransformedResult = new boolean[tupleLength];
+		Arrays.fill( includeInTransformedResult, true );
+		return includeInTransformedResult;
+	}
+
+	/* package-protected */
+	List untransformToTuples(List results, boolean isSingleResult) {
+		// untransform only if necessary; if transformed, do it in place;
+		if ( isSingleResult ) {
+			for ( int i = 0 ; i < results.size() ; i++ ) {
+				Object[] tuple = untransformToTuple( results.get( i ), isSingleResult);
+				results.set( i, tuple );
+			}
+		}
+		return results;
+	}
+
+	/* package-protected */
+	Object[] untransformToTuple(Object transformed, boolean isSingleResult ) {
+		return isSingleResult ? new Object[] { transformed } : ( Object[] ) transformed;
+	}
+
+	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 
 }
diff --git a/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java b/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
index 66a982951f..35b3b97889 100644
--- a/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
@@ -1,64 +1,87 @@
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
 
-import java.io.Serializable;
+import org.hibernate.util.ArrayHelper;
 
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
-public final class RootEntityResultTransformer extends BasicTransformerAdapter implements Serializable {
+public final class RootEntityResultTransformer extends BasicTransformerAdapter implements TupleSubsetResultTransformer {
 
 	public static final RootEntityResultTransformer INSTANCE = new RootEntityResultTransformer();
 
 	/**
 	 * Disallow instantiation of RootEntityResultTransformer.
 	 */
 	private RootEntityResultTransformer() {
 	}
 
 	/**
 	 * Return just the root entity from the row tuple.
 	 */
 	public Object transformTuple(Object[] tuple, String[] aliases) {
 		return tuple[ tuple.length-1 ];
 	}
 
 	/**
+	 * {@inheritDoc}
+	 */
+	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
+		return true;
+	}
+
+	/**
+	 * {@inheritDoc}
+	 */
+	public boolean[] includeInTransform(String[] aliases, int tupleLength) {
+
+		boolean[] includeInTransform;
+		if ( tupleLength == 1 ) {
+			includeInTransform = ArrayHelper.TRUE;
+		}
+		else {
+			includeInTransform = new boolean[tupleLength];
+			includeInTransform[ tupleLength - 1 ] = true;
+		}
+		return includeInTransform;
+	}
+
+	/**
 	 * Serialization hook for ensuring singleton uniqueing.
 	 *
 	 * @return The singleton instance : {@link #INSTANCE}
 	 */
 	private Object readResolve() {
 		return INSTANCE;
 	}
 }
diff --git a/core/src/main/java/org/hibernate/transform/ToListResultTransformer.java b/core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
index f28e411316..b32a0df1a3 100644
--- a/core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
+++ b/core/src/main/java/org/hibernate/transform/ToListResultTransformer.java
@@ -1,60 +1,59 @@
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
-import java.io.Serializable;
 
 /**
  * Tranforms each result row from a tuple into a {@link List}, such that what
  * you end up with is a {@link List} of {@link List Lists}.
  */
-public class ToListResultTransformer extends BasicTransformerAdapter implements Serializable {
+public class ToListResultTransformer extends BasicTransformerAdapter {
 
 	public static final ToListResultTransformer INSTANCE = new ToListResultTransformer();
 
 	/**
 	 * Disallow instantiation of ToListResultTransformer.
 	 */
 	private ToListResultTransformer() {
 	}
 	
 	/**
 	 * {@inheritDoc}
 	 */
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
diff --git a/core/src/main/java/org/hibernate/transform/TupleSubsetResultTransformer.java b/core/src/main/java/org/hibernate/transform/TupleSubsetResultTransformer.java
new file mode 100644
index 0000000000..a7b48de839
--- /dev/null
+++ b/core/src/main/java/org/hibernate/transform/TupleSubsetResultTransformer.java
@@ -0,0 +1,84 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
+package org.hibernate.transform;
+
+/**
+ * A ResultTransformer that operates on "well-defined" and consistent
+ * subset of a tuple's elements.
+ *
+ * "Well-defined" means that:
+ * <ol>
+ *     <li>
+ *         the indexes of tuple elements accessed by a
+ *         TupleSubsetResultTransformer depends only on the aliases
+ *         and the number of elements in the tuple; i.e, it does
+ *         not depend on the value of the tuple being transformed;
+ *     </li>
+ *     <li>
+ *         any tuple elements included in the transformed value are
+ *         unmodified by the transformation;
+ *     </li>
+ *     <li>
+ *         transforming equivalent tuples with the same aliases multiple
+ *         times results in transformed values that are equivalent;
+ *     </li>
+ *     <li>
+ *         the result of transforming the tuple subset (only those
+ *         elements accessed by the transformer) using only the
+ *         corresponding aliases is equivalent to transforming the
+ *         full tuple with the full array of aliases;
+ *     </li>
+ *     <li>
+ *         the result of transforming a tuple with non-accessed tuple
+ *         elements and corresponding aliases set to null
+ *         is equivalent to transforming the full tuple with the
+ *         full array of aliases;
+ *     </li>
+ * </ol>
+ *
+ * @author Gail Badner
+ */
+public interface TupleSubsetResultTransformer extends ResultTransformer {
+	/**
+	 * When a tuple is transformed, is the result a single element of the tuple?
+	 *
+	 * @param aliases - the aliases that correspond to the tuple
+	 * @param tupleLength - the number of elements in the tuple
+	 * @return true, if the transformed value is a single element of the tuple;
+	 *         false, otherwise.
+	 */
+	boolean isTransformedValueATupleElement(String[] aliases, int tupleLength);
+
+	/**
+	 * Returns an array with the i-th element indicating whether the i-th
+	 * element of the tuple is included in the transformed value.
+	 *
+	 * @param aliases - the aliases that correspond to the tuple
+	 * @param tupleLength - the number of elements in the tuple
+	 * @return array with the i-th element indicating whether the i-th
+	 *         element of the tuple is included in the transformed value.
+	 */
+	boolean[] includeInTransform(String[] aliases, int tupleLength);
+}
\ No newline at end of file
diff --git a/core/src/test/java/org/hibernate/cache/QueryKeyTest.java b/core/src/test/java/org/hibernate/cache/QueryKeyTest.java
index bad6a570bc..6925231db3 100644
--- a/core/src/test/java/org/hibernate/cache/QueryKeyTest.java
+++ b/core/src/test/java/org/hibernate/cache/QueryKeyTest.java
@@ -1,117 +1,274 @@
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
 package org.hibernate.cache;
 
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashMap;
+import java.util.Map;
 
 import junit.framework.TestCase;
 
 import org.hibernate.EntityMode;
+import org.hibernate.HibernateException;
+import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
 import org.hibernate.transform.AliasToBeanResultTransformer;
-import org.hibernate.transform.RootEntityResultTransformer;
+import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
+import org.hibernate.transform.CacheableResultTransformer;
+import org.hibernate.transform.DistinctResultTransformer;
 import org.hibernate.transform.ResultTransformer;
+import org.hibernate.transform.RootEntityResultTransformer;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 import org.hibernate.transform.AliasToEntityMapResultTransformer;
 import org.hibernate.transform.PassThroughResultTransformer;
-import org.hibernate.transform.DistinctResultTransformer;
+import org.hibernate.transform.ToListResultTransformer;
+import org.hibernate.transform.TupleSubsetResultTransformer;
+import org.hibernate.type.SerializationException;
 import org.hibernate.util.SerializationHelper;
 import org.hibernate.util.ArrayHelper;
 
 /**
  * Tests relating to {@link QueryKey} instances.
  *
  * @author Steve Ebersole
  */
 public class QueryKeyTest extends TestCase {
 	private static final String QUERY_STRING = "the query string";
 
 	public static class AClass implements Serializable {
 		private String propAccessedByField;
 		private String propAccessedByMethod;
+		private int propValue;
+
+		public AClass() {
+		}
+
+		public AClass(String propAccessedByField) {
+			this.propAccessedByField = propAccessedByField;
+		}
 
 		public String getPropAccessedByMethod() {
 			return propAccessedByMethod;
 		}
 
 		public void setPropAccessedByMethod(String propAccessedByMethod) {
 			this.propAccessedByMethod = propAccessedByMethod;
 		}
 	}
-	public void testSerializedEquality() {
-		doTest( buildBasicKey( null ) );
-	}
-
-	public void testSerializedEqualityWithResultTransformer() {
-		doTest( buildBasicKey( RootEntityResultTransformer.INSTANCE ) );
-		doTest( buildBasicKey( DistinctRootEntityResultTransformer.INSTANCE ) );
-		doTest( buildBasicKey( DistinctResultTransformer.INSTANCE ) );
-		doTest( buildBasicKey( AliasToEntityMapResultTransformer.INSTANCE ) );
-		doTest( buildBasicKey( PassThroughResultTransformer.INSTANCE ) );
 
+	public void testSerializedEqualityResultTransformer() throws Exception {
 		// settings are lazily initialized when calling transformTuple(),
 		// so they have not been initialized for the following test
 		// (it *should* be initialized before creating a QueryKey)
-		doTest( buildBasicKey( new AliasToBeanResultTransformer( AClass.class ) ) );
+		doResultTransformerTest( new AliasToBeanResultTransformer( AClass.class ), false );
 
 		// initialize settings for the next test
 		AliasToBeanResultTransformer transformer = new AliasToBeanResultTransformer( AClass.class );
 		transformer.transformTuple(
-				new Object[] { "abc", "def" },  
-				new String[] { "propAccessedByField", "propAccessedByMethod" } );
-		doTest( buildBasicKey( transformer ) );
+				new Object[] { "abc", "def" },
+				new String[] { "propAccessedByField", "propAccessedByMethod" }
+		);
+		doResultTransformerTest( transformer, false );
+
+		doResultTransformerTest( AliasToEntityMapResultTransformer.INSTANCE, true );
+		doResultTransformerTest( DistinctResultTransformer.INSTANCE, true );
+		doResultTransformerTest( DistinctRootEntityResultTransformer.INSTANCE, true );
+		doResultTransformerTest( PassThroughResultTransformer.INSTANCE, true );
+		doResultTransformerTest( RootEntityResultTransformer.INSTANCE, true );
+		doResultTransformerTest( ToListResultTransformer.INSTANCE, true );
+	}
+
+	// Reproduces HHH-5628; commented out because FailureExpected is not working here...
+	/*
+	public void testAliasToBeanConstructorFailureExpected() throws Exception {
+		// AliasToBeanConstructorResultTransformer is not Serializable because
+		// java.lang.reflect.Constructor is not Serializable;
+		doResultTransformerTest(
+				new AliasToBeanConstructorResultTransformer( AClass.class.getConstructor( String.class ) ), false
+		);
+	}
+	*/
+
+	private void doResultTransformerTest(ResultTransformer transformer, boolean isSingleton) {
+		Map transformerMap = new HashMap();
+
+		transformerMap.put( transformer, "" );
+		assert transformerMap.size() == 1 : "really messed up";
+		Object old = transformerMap.put( transformer, "value" );
+		assert old != null && transformerMap.size() == 1 : "apparent QueryKey equals/hashCode issue";
+
+		// finally, lets serialize it and see what happens
+		ResultTransformer transformer2 = ( ResultTransformer ) SerializationHelper.clone( transformer );
+		old = transformerMap.put( transformer2, "new value" );
+		assert old != null && transformerMap.size() == 1 : "deserialization did not set hashCode or equals properly";
+		if ( isSingleton ) {
+			assert transformer == transformer2: "deserialization issue for singleton transformer";			
+		}
+		else {
+			assert transformer != transformer2: "deserialization issue for non-singleton transformer";
+		}
+		assert transformer.equals( transformer2 ): "deep copy issue";
+	}
+
+	public void testSerializedEquality() throws Exception {
+		doTest( buildBasicKey( null ) );
+
+		doTest( buildBasicKey( CacheableResultTransformer.create( null, null, new boolean[] { true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( null, new String[] { null }, new boolean[] { true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( null, new String[] { "a" }, new boolean[] { true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( null, null, new boolean[] { false, true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( null, new String[] { "a" }, new boolean[] { true, false } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( null, new String[] { "a", null }, new boolean[] { true, true } ) ) );
 	}
 
-	private QueryKey buildBasicKey(ResultTransformer resultTransformer) {
+	public void testSerializedEqualityWithTupleSubsetResultTransfprmer() throws Exception {
+		doTestWithTupleSubsetResultTransformer(
+				new AliasToBeanResultTransformer( AClass.class ),
+				new String[] { "propAccessedByField", "propAccessedByMethod" }
+		);
+		doTestWithTupleSubsetResultTransformer( AliasToEntityMapResultTransformer.INSTANCE, new String[] { "a", "b" } );
+		doTestWithTupleSubsetResultTransformer( DistinctRootEntityResultTransformer.INSTANCE, new String[] { "a", "b" } );
+		doTestWithTupleSubsetResultTransformer( PassThroughResultTransformer.INSTANCE, new String[] { "a", "b" } );
+		doTestWithTupleSubsetResultTransformer( RootEntityResultTransformer.INSTANCE, new String[] { "a", "b" } );
+		// The following are not TupleSubsetResultTransformers:
+		// DistinctResultTransformer.INSTANCE
+		// ToListResultTransformer.INSTANCE
+	}
+
+	public void doTestWithTupleSubsetResultTransformer(TupleSubsetResultTransformer transformer,
+													   String[] aliases) throws Exception {
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { aliases[ 0 ], aliases[ 1 ] },
+						new boolean[] { true, true } )
+		) );
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { aliases[ 0 ], aliases[ 1 ] },
+						new boolean[] { true, true, false } ) 
+		) );
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { aliases[ 1 ] },
+						new boolean[] { true } )
+		) );
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { null, aliases[ 1 ] },
+						new boolean[] { true, true } )
+		) );
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { aliases[ 0 ], null },
+						new boolean[] { true, true } )
+		) );
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { aliases[ 0 ] },
+						new boolean[] { false, true } )
+		) );
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { aliases[ 0 ] },
+						new boolean[] { true, false } )
+		) );
+		doTest( buildBasicKey(
+				CacheableResultTransformer.create(
+						transformer,
+						new String[] { aliases[ 0 ] },
+						new boolean[] { false, true, false } )
+		) );
+		if ( ! ( transformer instanceof AliasedTupleSubsetResultTransformer ) ) {
+			doTestWithTupleSubsetResultTransformerNullAliases( transformer );
+		}
+	}
+
+	public void doTestWithTupleSubsetResultTransformerNullAliases(TupleSubsetResultTransformer transformer) throws Exception {
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] { true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] { true, true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] { true, true, true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] { false, true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] { true, false } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] { false, true, true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] {true, false, true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] {true, true, false } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] {false, false, true } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] {false, true, false } ) ) );
+		doTest( buildBasicKey( CacheableResultTransformer.create( transformer, null, new boolean[] {false, false, true } ) ) );
+	}
+
+	private QueryKey buildBasicKey(CacheableResultTransformer resultTransformer) {
 		return new QueryKey(
 				QUERY_STRING,
 				ArrayHelper.EMPTY_TYPE_ARRAY, 		// positional param types
 				ArrayHelper.EMPTY_OBJECT_ARRAY,		// positional param values
 				Collections.EMPTY_MAP,				// named params
 				null,								// firstRow selection
 				null,								// maxRows selection
 				Collections.EMPTY_SET, 				// filter keys
 				EntityMode.POJO,					// entity mode
 				resultTransformer					// the result transformer
 		);
 	}
 
 	private void doTest(QueryKey key) {
-		HashMap map = new HashMap();
+		Map keyMap = new HashMap();
+		Map transformerMap = new HashMap();
 
-		map.put( key, "" );
-		assert map.size() == 1 : "really messed up";
+		keyMap.put( key, "" );
+		assert keyMap.size() == 1 : "really messed up";
+		Object old = keyMap.put( key, "value" );
+		assert old != null && keyMap.size() == 1 : "apparent QueryKey equals/hashCode issue";
 
-		Object old = map.put( key, "value" );
-		assert old != null && map.size() == 1 : "apparent QueryKey equals/hashCode issue";
+		if ( key.getResultTransformer() != null ) {
+			transformerMap.put( key.getResultTransformer(), "" );
+			assert transformerMap.size() == 1 : "really messed up";
+			old = transformerMap.put( key.getResultTransformer(), "value" );
+			assert old != null && transformerMap.size() == 1 : "apparent QueryKey equals/hashCode issue";
+		}
 
 		// finally, lets serialize it and see what happens
 		QueryKey key2 = ( QueryKey ) SerializationHelper.clone( key );
 		assert key != key2 : "deep copy issue";
-		old = map.put( key2, "new value" );
-		assert old != null && map.size() == 1 : "deserialization did not set hashCode or equals properly";
+		old = keyMap.put( key2, "new value" );
+		assert old != null && keyMap.size() == 1 : "deserialization did not set hashCode or equals properly";
+		if ( key.getResultTransformer() == null ) {
+			assert key2.getResultTransformer() == null;
+		}
+		else {
+			old = transformerMap.put( key2.getResultTransformer(), "new value" );
+			assert old != null && transformerMap.size() == 1 : "deserialization did not set hashCode or equals properly";
+				assert key.getResultTransformer() != key2.getResultTransformer(): "deserialization issue for non-singleton transformer";
+				assert key.getResultTransformer().equals( key2.getResultTransformer() ): "deep copy issue";
+		}
 	}
 }
diff --git a/testsuite/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java b/testsuite/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
index d52a8338b0..e88985e36e 100644
--- a/testsuite/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
+++ b/testsuite/src/test/java/org/hibernate/test/hql/ASTParserLoadingTest.java
@@ -1310,1318 +1310,1408 @@ public class ASTParserLoadingTest extends FunctionalTestCase {
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
 		Transaction txn = session.beginTransaction();
 		Animal an = new Animal();
 		an.setBodyWeight(123.45f);
 		session.persist(an);
 		String str = (String) session.createQuery("select str(an.bodyWeight) from Animal an where str(an.bodyWeight) like '%1%'").uniqueResult();
 		if ( getDialect() instanceof DB2Dialect ) {
 			assertTrue( str.startsWith("1.234") );
 		}
 		else if ( getDialect() instanceof SybaseDialect || getDialect() instanceof Sybase11Dialect || getDialect() instanceof SybaseASE15Dialect || getDialect() instanceof SybaseAnywhereDialect || getDialect() instanceof SQLServerDialect ) {
 			// str(val) on sybase assumes a default of 10 characters with no decimal point or decimal values
 			// str(val) on sybase result is right-justified
 			assertEquals( str.length(), 10 );
 			assertTrue( str.endsWith("123") );
 			str = (String) session.createQuery("select str(an.bodyWeight, 8, 3) from Animal an where str(an.bodyWeight, 8, 3) like '%1%'").uniqueResult();
 			assertEquals( str.length(), 8 );
 			assertTrue( str.endsWith( "123.450" ) );
 		}
 		else {
 			assertTrue( str.startsWith("123.4") );
 		}
 		if ( ! ( getDialect() instanceof SybaseDialect ) && ! ( getDialect() instanceof Sybase11Dialect ) && ! ( getDialect() instanceof SybaseASE15Dialect ) && ! ( getDialect() instanceof SybaseAnywhereDialect ) && ! ( getDialect() instanceof SQLServerDialect ) ) {
 			// In TransactSQL (the variant spoken by Sybase and SQLServer), the str() function
 			// is explicitly intended for numeric values only...
 			String dateStr1 = (String) session.createQuery("select str(current_date) from Animal").uniqueResult();
 			String dateStr2 = (String) session.createQuery("select str(year(current_date))||'-'||str(month(current_date))||'-'||str(day(current_date)) from Animal").uniqueResult();
 			System.out.println(dateStr1 + '=' + dateStr2);
 			if ( ! ( getDialect() instanceof Oracle8iDialect ) ) { //Oracle renders the name of the month :(
 				String[] dp1 = StringHelper.split("-", dateStr1);
 				String[] dp2 = StringHelper.split("-", dateStr2);
 				for (int i=0; i<3; i++) {
 					if ( dp1[i].startsWith( "0" ) ) {
 						dp1[i] = dp1[i].substring( 1 );
 					}
 					assertEquals( dp1[i], dp2[i] );
 				}
 			}
 		}
 		session.delete(an);
 		txn.commit();
 		session.close();
 	}
 
 	public void testCast() {
 		if ( ( getDialect() instanceof MySQLDialect ) || ( getDialect() instanceof DB2Dialect ) ) {
 			return;
 		}
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.createQuery("from Human h where h.nickName like 'G%'").list();
 		session.createQuery("from Animal a where cast(a.bodyWeight as string) like '1.%'").list();
 		session.createQuery("from Animal a where cast(a.bodyWeight as integer) = 1").list();
 		txn.commit();
 		session.close();
 	}
 
 	public void testExtract() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.createQuery("select second(current_timestamp()), minute(current_timestamp()), hour(current_timestamp()) from Mammal m").list();
 		session.createQuery("select day(m.birthdate), month(m.birthdate), year(m.birthdate) from Mammal m").list();
 		if ( !(getDialect() instanceof DB2Dialect) ) { //no ANSI extract
 			session.createQuery("select extract(second from current_timestamp()), extract(minute from current_timestamp()), extract(hour from current_timestamp()) from Mammal m").list();
 			session.createQuery("select extract(day from m.birthdate), extract(month from m.birthdate), extract(year from m.birthdate) from Mammal m").list();
 		}
 		txn.commit();
 		session.close();
 	}
 
 	public void testOneToManyFilter() throws Throwable {
 		if ( getDialect() instanceof IngresDialect ) {
 			// HHH-4977 Ingres 9.3 does not support sub-selects in the select
 			// list.
 			return;
 		}
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Product product = new Product();
 		product.setDescription( "My Product" );
 		product.setNumberAvailable( 10 );
 		product.setPrice( new BigDecimal( 123 ) );
 		product.setProductId( "4321" );
 		session.save( product );
 
 		Customer customer = new Customer();
 		customer.setCustomerId( "123456789" );
 		customer.setName( "My customer" );
 		customer.setAddress( "somewhere" );
 		session.save( customer );
 
 		Order order = customer.generateNewOrder( new BigDecimal( 1234 ) );
 		session.save( order );
 
 		LineItem li = order.generateLineItem( product, 5 );
 		session.save( li );
 
 		session.flush();
 
 		assertEquals( session.createFilter( customer.getOrders(), "" ).list().size(), 1 );
 
 		assertEquals( session.createFilter( order.getLineItems(), "" ).list().size(), 1 );
 		assertEquals( session.createFilter( order.getLineItems(), "where this.quantity > :quantity" ).setInteger( "quantity", 5 ).list().size(), 0 );
 
 		session.delete(li);
 		session.delete(order);
 		session.delete(product);
 		session.delete(customer);
 		txn.commit();
 		session.close();
 	}
 
 	public void testManyToManyFilter() throws Throwable {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Human human = new Human();
 		human.setName( new Name( "Steve", 'L', "Ebersole" ) );
 		session.save( human );
 
 		Human friend = new Human();
 		friend.setName( new Name( "John", 'Q', "Doe" ) );
 		friend.setBodyWeight( 11.0f );
 		session.save( friend );
 
 		human.setFriends( new ArrayList() );
 		friend.setFriends( new ArrayList() );
 		human.getFriends().add( friend );
 		friend.getFriends().add( human );
 
 		session.flush();
 
 		assertEquals( session.createFilter( human.getFriends(), "" ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.bodyWeight > ?" ).setFloat( 0, 10f ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.bodyWeight < ?" ).setFloat( 0, 10f ).list().size(), 0 );
 
 		session.delete(human);
 		session.delete(friend);
 
 		txn.commit();
 		session.close();
 	}
 	
 	public void testFilterWithCustomColumnReadAndWrite() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Human human = new Human();
 		human.setName( new Name( "Steve", 'L', "Ebersole" ) );
 		human.setHeightInches(73d);
 		session.save( human );
 
 		Human friend = new Human();
 		friend.setName( new Name( "John", 'Q', "Doe" ) );
 		friend.setHeightInches(50d);
 		session.save( friend );
 
 		human.setFriends( new ArrayList() );
 		friend.setFriends( new ArrayList() );
 		human.getFriends().add( friend );
 		friend.getFriends().add( human );
 
 		session.flush();
 
 		assertEquals( session.createFilter( human.getFriends(), "" ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.heightInches < ?" ).setDouble( 0, 51d ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.heightInches > ?" ).setDouble( 0, 51d ).list().size(), 0 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.heightInches between 49 and 51" ).list().size(), 1 );
 		assertEquals( session.createFilter( human.getFriends(), "where this.heightInches not between 49 and 51" ).list().size(), 0 );
 
 		session.delete(human);
 		session.delete(friend);
 
 		txn.commit();
 		session.close();		
 	}
 
 	public void testSelectExpressions() {
 		createTestBaseData();
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		Human h = new Human();
 		h.setName( new Name("Gavin", 'A', "King") );
 		h.setNickName("Oney");
 		h.setBodyWeight(1.0f);
 		session.persist(h);
 		List results = session.createQuery("select 'found', lower(h.name.first) from Human h where lower(h.name.first) = 'gavin'").list();
 		results = session.createQuery("select 'found', lower(h.name.first) from Human h where concat(h.name.first, ' ', h.name.initial, ' ', h.name.last) = 'Gavin A King'").list();
 		results = session.createQuery("select 'found', lower(h.name.first) from Human h where h.name.first||' '||h.name.initial||' '||h.name.last = 'Gavin A King'").list();
 		results = session.createQuery("select a.bodyWeight + m.bodyWeight from Animal a join a.mother m").list();
 		results = session.createQuery("select 2.0 * (a.bodyWeight + m.bodyWeight) from Animal a join a.mother m").list();
 		results = session.createQuery("select sum(a.bodyWeight + m.bodyWeight) from Animal a join a.mother m").list();
 		results = session.createQuery("select sum(a.mother.bodyWeight * 2.0) from Animal a").list();
 		results = session.createQuery("select concat(h.name.first, ' ', h.name.initial, ' ', h.name.last) from Human h").list();
 		results = session.createQuery("select h.name.first||' '||h.name.initial||' '||h.name.last from Human h").list();
 		results = session.createQuery("select nickName from Human").list();
 		results = session.createQuery("select lower(nickName) from Human").list();
 		results = session.createQuery("select abs(bodyWeight*-1) from Human").list();
 		results = session.createQuery("select upper(h.name.first||' ('||h.nickName||')') from Human h").list();
 		results = session.createQuery("select abs(a.bodyWeight-:param) from Animal a").setParameter("param", new Float(2.0)).list();
 		results = session.createQuery("select abs(:param - a.bodyWeight) from Animal a").setParameter("param", new Float(2.0)).list();
 		results = session.createQuery("select lower(upper('foo')) from Animal").list();
 		results = session.createQuery("select lower(upper('foo') || upper('bar')) from Animal").list();
 		results = session.createQuery("select sum(abs(bodyWeight - 1.0) * abs(length('ffobar')-3)) from Animal").list();
 		session.delete(h);
 		txn.commit();
 		session.close();
 		destroyTestBaseData();
 	}
 
 	private void createTestBaseData() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		Mammal m1 = new Mammal();
 		m1.setBodyWeight( 11f );
 		m1.setDescription( "Mammal #1" );
 
 		session.save( m1 );
 
 		Mammal m2 = new Mammal();
 		m2.setBodyWeight( 9f );
 		m2.setDescription( "Mammal #2" );
 		m2.setMother( m1 );
 
 		session.save( m2 );
 
 		txn.commit();
 		session.close();
 
 		createdAnimalIds.add( m1.getId() );
 		createdAnimalIds.add( m2.getId() );
 	}
 
 	private void destroyTestBaseData() {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 
 		for ( int i = 0; i < createdAnimalIds.size(); i++ ) {
 			Animal animal = ( Animal ) session.load( Animal.class, ( Long ) createdAnimalIds.get( i ) );
 			session.delete( animal );
 		}
 
 		txn.commit();
 		session.close();
 	}
 
 	public void testImplicitJoin() throws Exception {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 		Animal a = new Animal();
 		a.setBodyWeight(0.5f);
 		a.setBodyWeight(1.5f);
 		Animal b = new Animal();
 		Animal mother = new Animal();
 		mother.setBodyWeight(10.0f);
 		mother.addOffspring(a);
 		mother.addOffspring(b);
 		session.persist(a);
 		session.persist(b);
 		session.persist(mother);
 		List list = session.createQuery("from Animal a where a.mother.bodyWeight < 2.0 or a.mother.bodyWeight > 9.0").list();
 		assertEquals( list.size(), 2 );
 		list = session.createQuery("from Animal a where a.mother.bodyWeight > 2.0 and a.mother.bodyWeight > 9.0").list();
 		assertEquals( list.size(), 2 );
 		session.delete(b);
 		session.delete(a);
 		session.delete(mother);
 		t.commit();
 		session.close();
 	}
 
 	public void testFromOnly() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "from Animal" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testSimpleSelect() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "select a from Animal as a" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testEntityPropertySelect() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "select a.mother from Animal as a" ).list();
 //		assertEquals("Incorrect result size", 2, results.size());
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testWhere() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 		List results = null;
 
 		results = session.createQuery( "from Animal an where an.bodyWeight > 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where not an.bodyWeight > 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where an.bodyWeight between 0 and 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where an.bodyWeight not between 0 and 10" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		results = session.createQuery( "from Animal an where sqrt(an.bodyWeight)/2 > 10" ).list();
 		assertEquals( "Incorrect result size", 0, results.size() );
 
 		results = session.createQuery( "from Animal an where (an.bodyWeight > 10 and an.bodyWeight < 100) or an.bodyWeight is null" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testEntityFetching() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "from Animal an join fetch an.mother" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		Animal mother = ( ( Animal ) results.get( 0 ) ).getMother();
 		assertTrue( "fetch uninitialized", mother != null && Hibernate.isInitialized( mother ) );
 
 		results = session.createQuery( "select an from Animal an join fetch an.mother" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		mother = ( ( Animal ) results.get( 0 ) ).getMother();
 		assertTrue( "fetch uninitialized", mother != null && Hibernate.isInitialized( mother ) );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testCollectionFetching() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 		List results = session.createQuery( "from Animal an join fetch an.offspring" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		Collection os = ( ( Animal ) results.get( 0 ) ).getOffspring();
 		assertTrue( "fetch uninitialized", os != null && Hibernate.isInitialized( os ) && os.size() == 1 );
 
 		results = session.createQuery( "select an from Animal an join fetch an.offspring" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Animal );
 		os = ( ( Animal ) results.get( 0 ) ).getOffspring();
 		assertTrue( "fetch uninitialized", os != null && Hibernate.isInitialized( os ) && os.size() == 1 );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testJoinFetchedCollectionOfJoinedSubclass() throws Exception {
 		Mammal mammal = new Mammal();
 		mammal.setDescription( "A Zebra" );
 		Zoo zoo = new Zoo();
 		zoo.setName( "A Zoo" );
 		zoo.getMammals().put( "zebra", mammal );
 		mammal.setZoo( zoo );
 
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.save( mammal );
 		session.save( zoo );
 		txn.commit();
 
 		session = openSession();
 		txn = session.beginTransaction();
 		List results = session.createQuery( "from Zoo z join fetch z.mammals" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Zoo );
 		Zoo zooRead = ( Zoo ) results.get( 0 );
 		assertEquals( zoo, zooRead );
 		assertTrue( Hibernate.isInitialized( zooRead.getMammals() ) );
 		Mammal mammalRead = ( Mammal ) ( ( Map ) zooRead.getMammals() ).get( "zebra" );
 		assertEquals( mammal, mammalRead );
 		session.delete( mammalRead );
 		session.delete( zooRead );
 		txn.commit();
 		session.close();
 	}
 
 	public void testJoinedCollectionOfJoinedSubclass() throws Exception {
 		Mammal mammal = new Mammal();
 		mammal.setDescription( "A Zebra" );
 		Zoo zoo = new Zoo();
 		zoo.setName( "A Zoo" );
 		zoo.getMammals().put( "zebra", mammal );
 		mammal.setZoo( zoo );
 
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.save( mammal );
 		session.save( zoo );
 		txn.commit();
 
 		session = openSession();
 		txn = session.beginTransaction();
 		List results = session.createQuery( "from Zoo z join z.mammals m" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Object[] );
 		Object[] resultObjects = ( Object[] ) results.get( 0 );
 		Zoo zooRead = ( Zoo ) resultObjects[ 0 ];
 		Mammal mammalRead = ( Mammal ) resultObjects[ 1 ];
 		assertEquals( zoo, zooRead );
 		assertEquals( mammal, mammalRead );
 		session.delete( mammalRead );
 		session.delete( zooRead );
 		txn.commit();
 		session.close();
 	}
 
 	public void testJoinedCollectionOfJoinedSubclassProjection() throws Exception {
 		Mammal mammal = new Mammal();
 		mammal.setDescription( "A Zebra" );
 		Zoo zoo = new Zoo();
 		zoo.setName( "A Zoo" );
 		zoo.getMammals().put( "zebra", mammal );
 		mammal.setZoo( zoo );
 
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		session.save( mammal );
 		session.save( zoo );
 		txn.commit();
 
 		session = openSession();
 		txn = session.beginTransaction();
 		List results = session.createQuery( "select z, m from Zoo z join z.mammals m" ).list();
 		assertEquals( "Incorrect result size", 1, results.size() );
 		assertTrue( "Incorrect result return type", results.get( 0 ) instanceof Object[] );
 		Object[] resultObjects = ( Object[] ) results.get( 0 );
 		Zoo zooRead = ( Zoo ) resultObjects[ 0 ];
 		Mammal mammalRead = ( Mammal ) resultObjects[ 1 ];
 		assertEquals( zoo, zooRead );
 		assertEquals( mammal, mammalRead );
 		session.delete( mammalRead );
 		session.delete( zooRead );
 		txn.commit();
 		session.close();
 	}
 
 	public void testProjectionQueries() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "select an.mother.id, max(an.bodyWeight) from Animal an group by an.mother.id" ).list();
 		// mysql returns nulls in this group by
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof Object[] );
 		assertEquals( "Incorrect return dimensions", 2, ( ( Object[] ) results.get( 0 ) ).length );
 
 		session.close();
 
 		destroyTestBaseData();
 
 	}
 
 	public void testStandardFunctions() throws Exception {
 		Session session = openSession();
 		Transaction t = session.beginTransaction();
 		Product p = new Product();
 		p.setDescription("a product");
 		p.setPrice( new BigDecimal(1.0) );
 		p.setProductId("abc123");
 		session.persist(p);
 		Object[] result = (Object[]) session
 			.createQuery("select current_time(), current_date(), current_timestamp() from Product")
 			.uniqueResult();
 		assertTrue( result[0] instanceof Time );
 		assertTrue( result[1] instanceof Date );
 		assertTrue( result[2] instanceof Timestamp );
 		assertNotNull( result[0] );
 		assertNotNull( result[1] );
 		assertNotNull( result[2] );
 		session.delete(p);
 		t.commit();
 		session.close();
 
 	}
 
 	public void testDynamicInstantiationQueries() throws Exception {
 
 		createTestBaseData();
 
 		Session session = openSession();
 
 		List results = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertClassAssignability( results.get( 0 ).getClass(), Animal.class );
 
 		Iterator iter = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" ).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		assertTrue( "Incorrect return type", iter.next() instanceof Animal );
 
 		results = session.createQuery( "select new list(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof List );
 		assertEquals( "Incorrect return type", ( (List) results.get( 0 ) ).size(), 2 );
 
 		results = session.createQuery( "select new list(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof List );
 		assertEquals( "Incorrect return type", ( (List) results.get( 0 ) ).size(), 2 );
 
 		iter = session.createQuery( "select new list(an.description, an.bodyWeight) from Animal an" ).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		Object obj = iter.next();
 		assertTrue( "Incorrect return type", obj instanceof List );
 		assertEquals( "Incorrect return type", ( (List) obj ).size(), 2 );
 
 		iter = ( ( org.hibernate.classic.Session ) session ).createQuery(
 				"select new list(an.description, an.bodyWeight) from Animal an"
 		).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		obj = iter.next();
 		assertTrue( "Incorrect return type", obj instanceof List );
 		assertEquals( "Incorrect return type", ( (List) obj ).size(), 2 );
 
 		results = session.createQuery( "select new map(an.description, an.bodyWeight) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) results.get( 0 ) ).size(), 2 );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("0") );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("1") );
 
 		results = session.createQuery( "select new map(an.description as descr, an.bodyWeight as bw) from Animal an" ).list();
 		assertEquals( "Incorrect result size", 2, results.size() );
 		assertTrue( "Incorrect return type", results.get( 0 ) instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) results.get( 0 ) ).size(), 2 );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("descr") );
 		assertTrue( ( (Map) results.get( 0 ) ).containsKey("bw") );
 
 		iter = session.createQuery( "select new map(an.description, an.bodyWeight) from Animal an" ).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		obj = iter.next();
 		assertTrue( "Incorrect return type", obj instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) obj ).size(), 2 );
 
 		ScrollableResults sr = session.createQuery( "select new map(an.description, an.bodyWeight) from Animal an" ).scroll();
 		assertTrue( "Incorrect result size", sr.next() );
 		obj = sr.get(0);
 		assertTrue( "Incorrect return type", obj instanceof Map );
 		assertEquals( "Incorrect return type", ( (Map) obj ).size(), 2 );
 		sr.close();
 
 		sr = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" ).scroll();
 		assertTrue( "Incorrect result size", sr.next() );
 		assertTrue( "Incorrect return type", sr.get(0) instanceof Animal );
 		sr.close();
 
 		// caching...
 		QueryStatistics stats = getSessions().getStatistics().getQueryStatistics( "select new Animal(an.description, an.bodyWeight) from Animal an" );
 		results = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" )
 				.setCacheable( true )
 				.list();
 		assertEquals( "incorrect result size", 2, results.size() );
 		assertClassAssignability( Animal.class, results.get( 0 ).getClass() );
 		long initCacheHits = stats.getCacheHitCount();
 		results = session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" )
 				.setCacheable( true )
 				.list();
 		assertEquals( "dynamic intantiation query not served from cache", initCacheHits + 1, stats.getCacheHitCount() );
 		assertEquals( "incorrect result size", 2, results.size() );
 		assertClassAssignability( Animal.class, results.get( 0 ).getClass() );
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
+	public void testCachedJoinedAndJoinFetchedManyToOne() throws Exception {
+
+		Animal a = new Animal();
+		a.setDescription( "an animal" );
+		Animal mother = new Animal();
+		mother.setDescription( "a mother" );
+		mother.addOffspring( a );
+		a.setMother( mother );
+		Animal offspring1 = new Animal();
+		offspring1.setDescription( "offspring1" );
+		Animal offspring2 = new Animal();
+		offspring1.setDescription( "offspring2" );
+		a.addOffspring( offspring1 );
+		offspring1.setMother( a );
+		a.addOffspring( offspring2 );
+		offspring2.setMother( a );
+
+		Session s = openSession();
+		Transaction t = s.beginTransaction();
+		s.save( mother );
+		s.save( a );
+		s.save( offspring1 );
+		s.save( offspring2 );
+		t.commit();
+		s.close();
+
+		getSessions().getCache().evictQueryRegions();
+		getSessions().getStatistics().clear();
+
+		s = openSession();
+		t = s.beginTransaction();
+		List list = s.createQuery( "from Animal a left join fetch a.mother" ).setCacheable( true ).list();
+		assertEquals( 0, getSessions().getStatistics().getQueryCacheHitCount() );
+		assertEquals( 1, getSessions().getStatistics().getQueryCachePutCount() );
+		list = s.createQuery( "select a from Animal a left join fetch a.mother" ).setCacheable( true ).list();
+		assertEquals( 1, getSessions().getStatistics().getQueryCacheHitCount() );
+		assertEquals( 1, getSessions().getStatistics().getQueryCachePutCount() );
+		list = s.createQuery( "select a, m from Animal a left join a.mother m" ).setCacheable( true ).list();
+		assertEquals( 1, getSessions().getStatistics().getQueryCacheHitCount() );
+		assertEquals( 2, getSessions().getStatistics().getQueryCachePutCount() );
+		s.createQuery( "delete from Animal" ).executeUpdate();
+		t.commit();
+		s.close();
+	}
+
+	public void testCachedJoinedAndJoinFetchedOneToMany() throws Exception {
+
+		Animal a = new Animal();
+		a.setDescription( "an animal" );
+		Animal mother = new Animal();
+		mother.setDescription( "a mother" );
+		mother.addOffspring( a );
+		a.setMother( mother );
+		Animal offspring1 = new Animal();
+		offspring1.setDescription( "offspring1" );
+		Animal offspring2 = new Animal();
+		offspring1.setDescription( "offspring2" );
+		a.addOffspring( offspring1 );
+		offspring1.setMother( a );
+		a.addOffspring( offspring2 );
+		offspring2.setMother( a );
+
+		getSessions().getCache().evictQueryRegions();
+		getSessions().getStatistics().clear();
+
+		Session s = openSession();
+		Transaction t = s.beginTransaction();
+		s.save( mother );
+		s.save( a );
+		s.save( offspring1 );
+		s.save( offspring2 );
+		t.commit();
+		s.close();
+
+		s = openSession();
+		t = s.beginTransaction();
+		List list = s.createQuery( "from Animal a left join fetch a.offspring" ).setCacheable( true ).list();
+		assertEquals( 0, getSessions().getStatistics().getQueryCacheHitCount() );
+		assertEquals( 1, getSessions().getStatistics().getQueryCachePutCount() );
+		list = s.createQuery( "select a from Animal a left join fetch a.offspring" ).setCacheable( true ).list();
+		assertEquals( 1, getSessions().getStatistics().getQueryCacheHitCount() );
+		assertEquals( 1, getSessions().getStatistics().getQueryCachePutCount() );
+		list = s.createQuery( "select a, o from Animal a left join a.offspring o" ).setCacheable( true ).list();
+		assertEquals( 1, getSessions().getStatistics().getQueryCacheHitCount() );
+		assertEquals( 2, getSessions().getStatistics().getQueryCachePutCount() );
+		s.createQuery( "delete from Animal" ).executeUpdate();
+		t.commit();
+		s.close();
+	}
+
 	public void testIllegalMixedTransformerQueries() {
 		Session session = openSession();
 
 		try {
 			getSelectNewQuery( session ).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
 			fail("'select new' together with a resulttransformer should result in error!");
 		} catch(QueryException he) {
 			assertTrue(he.getMessage().indexOf("ResultTransformer")==0);
 		}
 
 		try {
 			getSelectNewQuery( session ).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).iterate();
 			fail("'select new' together with a resulttransformer should result in error!");
 		} catch(HibernateException he) {
 			assertTrue(he.getMessage().indexOf("ResultTransformer")==0);
 		}
 
 		try {
 			getSelectNewQuery( session ).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).scroll();
 			fail("'select new' together with a resulttransformer should result in error!");
 		} catch(HibernateException he) {
 			assertTrue(he.getMessage().indexOf("ResultTransformer")==0);
 		}
 
 		session.close();
 	}
 
 	private Query getSelectNewQuery(Session session) {
 		return session.createQuery( "select new Animal(an.description, an.bodyWeight) from Animal an" );
 	}
 	public void testResultTransformerScalarQueries() throws Exception {
 
 		createTestBaseData();
 
 		String query = "select an.description as description, an.bodyWeight as bodyWeight from Animal an order by bodyWeight desc";
 
 		Session session = openSession();
 
 		List results = session.createQuery( query )
 		.setResultTransformer(Transformers.aliasToBean(Animal.class)).list();
 		assertEquals( "Incorrect result size", results.size(), 2 );
 		assertTrue( "Incorrect return type", results.get(0) instanceof Animal );
 		Animal firstAnimal = (Animal) results.get(0);
 		Animal secondAnimal = (Animal) results.get(1);
 		assertEquals("Mammal #1", firstAnimal.getDescription());
 		assertEquals("Mammal #2", secondAnimal.getDescription());
 		assertFalse(session.contains(firstAnimal));
 		session.close();
 
 		session = openSession();
 
 		Iterator iter = session.createQuery( query )
 	     .setResultTransformer(Transformers.aliasToBean(Animal.class)).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		assertTrue( "Incorrect return type", iter.next() instanceof Animal );
 
 		session.close();
 
 		session = openSession();
 
 		ScrollableResults sr = session.createQuery( query )
 	     .setResultTransformer(Transformers.aliasToBean(Animal.class)).scroll();
 		assertTrue( "Incorrect result size", sr.next() );
 		assertTrue( "Incorrect return type", sr.get(0) instanceof Animal );
 		assertFalse(session.contains(sr.get(0)));
 		sr.close();
 
 		session.close();
 
 		session = openSession();
 
 		results = session.createQuery( "select a from Animal a, Animal b order by a.id" )
 				.setResultTransformer( DistinctRootEntityResultTransformer.INSTANCE )
 				.list();
 		assertEquals( "Incorrect result size", 2, results.size());
 		assertTrue( "Incorrect return type", results.get(0) instanceof Animal );
 		firstAnimal = (Animal) results.get(0);
 		secondAnimal = (Animal) results.get(1);
 		assertEquals("Mammal #1", firstAnimal.getDescription());
 		assertEquals("Mammal #2", secondAnimal.getDescription());
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testResultTransformerEntityQueries() throws Exception {
 
 		createTestBaseData();
 
 		String query = "select an as an from Animal an order by bodyWeight desc";
 
 		Session session = openSession();
 
 		List results = session.createQuery( query )
 		.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
 		assertEquals( "Incorrect result size", results.size(), 2 );
 		assertTrue( "Incorrect return type", results.get(0) instanceof Map );
 		Map map = ((Map) results.get(0));
 		assertEquals(1, map.size());
 		Animal firstAnimal = (Animal) map.get("an");
 		map = ((Map) results.get(1));
 		Animal secondAnimal = (Animal) map.get("an");
 		assertEquals("Mammal #1", firstAnimal.getDescription());
 		assertEquals("Mammal #2", secondAnimal.getDescription());
 		assertTrue(session.contains(firstAnimal));
 		assertSame(firstAnimal, session.get(Animal.class,firstAnimal.getId()));
 		session.close();
 
 		session = openSession();
 
 		Iterator iter = session.createQuery( query )
 	     .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).iterate();
 		assertTrue( "Incorrect result size", iter.hasNext() );
 		map = (Map) iter.next();
 		firstAnimal = (Animal) map.get("an");
 		assertEquals("Mammal #1", firstAnimal.getDescription());
 		assertTrue( "Incorrect result size", iter.hasNext() );
 
 		session.close();
 
 		session = openSession();
 
 		ScrollableResults sr = session.createQuery( query )
 	     .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).scroll();
 		assertTrue( "Incorrect result size", sr.next() );
 		assertTrue( "Incorrect return type", sr.get(0) instanceof Map );
 		assertFalse(session.contains(sr.get(0)));
 		sr.close();
 
 		session.close();
 
 		destroyTestBaseData();
 	}
 
 	public void testEJBQLFunctions() throws Exception {
 		Session session = openSession();
 
 		String hql = "from Animal a where a.description = concat('1', concat('2','3'), '4'||'5')||'0'";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where substring(a.description, 1, 3) = 'cat'";
 		session.createQuery(hql).list();
 
 		hql = "select substring(a.description, 1, 3) from Animal a";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where lower(a.description) = 'cat'";
 		session.createQuery(hql).list();
 
 		hql = "select lower(a.description) from Animal a";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where upper(a.description) = 'CAT'";
 		session.createQuery(hql).list();
 
 		hql = "select upper(a.description) from Animal a";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where length(a.description) = 5";
 		session.createQuery(hql).list();
 
 		hql = "select length(a.description) from Animal a";
 		session.createQuery(hql).list();
 
 		//note: postgres and db2 don't have a 3-arg form, it gets transformed to 2-args
 		hql = "from Animal a where locate('abc', a.description, 2) = 2";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where locate('abc', a.description) = 2";
 		session.createQuery(hql).list();
 
 		hql = "select locate('cat', a.description, 2) from Animal a";
 		session.createQuery(hql).list();
 
 		if ( !( getDialect() instanceof DB2Dialect ) ) {
 			hql = "from Animal a where trim(trailing '_' from a.description) = 'cat'";
 			session.createQuery(hql).list();
 
 			hql = "select trim(trailing '_' from a.description) from Animal a";
 			session.createQuery(hql).list();
 
 			hql = "from Animal a where trim(leading '_' from a.description) = 'cat'";
 			session.createQuery(hql).list();
 
 			hql = "from Animal a where trim(both from a.description) = 'cat'";
 			session.createQuery(hql).list();
 		}
 
 		if ( !(getDialect() instanceof HSQLDialect) ) { //HSQL doesn't like trim() without specification
 			hql = "from Animal a where trim(a.description) = 'cat'";
 			session.createQuery(hql).list();
 		}
 
 		hql = "from Animal a where abs(a.bodyWeight) = sqrt(a.bodyWeight)";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where mod(16, 4) = 4";
 		session.createQuery(hql).list();
 		/**
 		 * PostgreSQL >= 8.3.7 typecasts are no longer automatically allowed 
 		 * <link>http://www.postgresql.org/docs/current/static/release-8-3.html</link>
 		 */
 		if(getDialect() instanceof PostgreSQLDialect){
 			hql = "from Animal a where bit_length(str(a.bodyWeight)) = 24";
 		}else{
 			hql = "from Animal a where bit_length(a.bodyWeight) = 24";
 		}
 		
 		session.createQuery(hql).list();
 		if(getDialect() instanceof PostgreSQLDialect){
 			hql = "select bit_length(str(a.bodyWeight)) from Animal a";
 		}else{
 			hql = "select bit_length(a.bodyWeight) from Animal a";
 		}
 		
 		session.createQuery(hql).list();
 
 		/*hql = "select object(a) from Animal a where CURRENT_DATE = :p1 or CURRENT_TIME = :p2 or CURRENT_TIMESTAMP = :p3";
 		session.createQuery(hql).list();*/
 
 		// todo the following is not supported
 		//hql = "select CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP from Animal a";
 		//parse(hql, true);
 		//System.out.println("sql: " + toSql(hql));
 
 		hql = "from Animal a where a.description like '%a%'";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where a.description not like '%a%'";
 		session.createQuery(hql).list();
 
 		hql = "from Animal a where a.description like 'x%ax%' escape 'x'";
 		session.createQuery(hql).list();
 
 		session.close();
 	}
 
 	public void testSubselectBetween() {
 		if ( supportsSubselectOnLeftSideIn() ) {
 			assertResultSize( "from Animal x where (select max(a.bodyWeight) from Animal a) in (1,2,3)", 0 );
 			assertResultSize( "from Animal x where (select max(a.bodyWeight) from Animal a) between 0 and 100", 0 );
 			assertResultSize( "from Animal x where (select max(a.description) from Animal a) like 'big%'", 0 );
 			assertResultSize( "from Animal x where (select max(a.bodyWeight) from Animal a) is not null", 0 );
 		}
 		assertResultSize( "from Animal x where exists (select max(a.bodyWeight) from Animal a)", 0 );
 	}
 
 	private void assertResultSize(String hql, int size) {
 		Session session = openSession();
 		Transaction txn = session.beginTransaction();
 		assertEquals( size, session.createQuery(hql).list().size() );
 		txn.commit();
 		session.close();
 	}
 
 	private interface QueryPreparer {
 		public void prepare(Query query);
 	}
 
 	private static final QueryPreparer DEFAULT_PREPARER = new QueryPreparer() {
 		public void prepare(Query query) {
 		}
 	};
 
 	private class SyntaxChecker {
 		private final String hql;
 		private final QueryPreparer preparer;
 
 		public SyntaxChecker(String hql) {
 			this( hql, DEFAULT_PREPARER );
 		}
 
 		public SyntaxChecker(String hql, QueryPreparer preparer) {
 			this.hql = hql;
 			this.preparer = preparer;
 		}
 
 		public void checkAll() {
 			checkList();
 			checkIterate();
 			checkScroll();
 		}
 
 		public SyntaxChecker checkList() {
 			Session s = openSession();
 			s.beginTransaction();
 			Query query = s.createQuery( hql );
 			preparer.prepare( query );
 			query.list();
 			s.getTransaction().commit();
 			s.close();
 			return this;
 		}
 
 		public SyntaxChecker checkScroll() {
 			Session s = openSession();
 			s.beginTransaction();
 			Query query = s.createQuery( hql );
 			preparer.prepare( query );
 			query.scroll();
 			s.getTransaction().commit();
 			s.close();
 			return this;
 		}
 
 		public SyntaxChecker checkIterate() {
 			Session s = openSession();
 			s.beginTransaction();
 			Query query = s.createQuery( hql );
 			preparer.prepare( query );
 			query.iterate();
 			s.getTransaction().commit();
 			s.close();
 			return this;
 		}
 	}
 }
diff --git a/testsuite/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java b/testsuite/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
index b08cec3749..3c65a4f262 100644
--- a/testsuite/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+++ b/testsuite/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
@@ -1,2215 +1,2678 @@
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
 package org.hibernate.test.querycache;
 
 import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projections;
 import org.hibernate.criterion.Property;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.test.fetchprofiles.join.Enrollment;
 import org.hibernate.testing.junit.functional.FunctionalTestCase;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.transform.Transformers;
 import org.hibernate.type.Type;
 import org.hibernate.util.ReflectHelper;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractQueryCacheResultTransformerTest extends FunctionalTestCase {
 
 	private Student yogiExpected;
 	private Student shermanExpected;
 	private CourseMeeting courseMeetingExpected1;
 	private CourseMeeting courseMeetingExpected2;
 	private Course courseExpected;
 	private Enrolment yogiEnrolmentExpected;
 	private Enrolment shermanEnrolmentExpected;
 
 	public AbstractQueryCacheResultTransformerTest(String str) {
 		super( str );
 	}
 
 	public String[] getMappings() {
 		return new String[] { "querycache/Enrolment.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "foo" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	protected abstract class CriteriaExecutor extends QueryExecutor {
 		protected abstract Criteria getCriteria(Session s) throws Exception;
 		protected Object getResults(Session s, boolean isSingleResult) throws Exception {
 			Criteria criteria = getCriteria( s ).setCacheable( getQueryCacheMode() != CacheMode.IGNORE ).setCacheMode( getQueryCacheMode() );
 			return ( isSingleResult ? criteria.uniqueResult() : criteria.list() );
 		}
 	}
 
 	protected abstract class HqlExecutor extends QueryExecutor {
 		protected abstract Query getQuery(Session s);
 		protected Object getResults(Session s, boolean isSingleResult) {
 			Query query = getQuery( s ).setCacheable( getQueryCacheMode() != CacheMode.IGNORE ).setCacheMode( getQueryCacheMode() );
 			return ( isSingleResult ? query.uniqueResult() : query.list() );
 		}
 	}
 
 	protected abstract class QueryExecutor {
 		public Object execute(boolean isSingleResult) throws Exception{
 			Session s = openSession();
 			Transaction t = s.beginTransaction();
-			Object result = getResults( s, isSingleResult );
-			t.commit();
-			s.close();
+			Object result = null;
+			try {
+				result = getResults( s, isSingleResult );
+				t.commit();
+			}
+			catch ( Exception ex ) {
+				t.rollback();
+				throw ex;
+			}
+			finally {
+				s.close();
+			}
 			return result;
 		}
 		protected abstract Object getResults(Session s, boolean isSingleResult) throws Exception;
 	}
 
 	protected interface ResultChecker {
 		void check(Object results);	
 	}
 
 	protected abstract CacheMode getQueryCacheMode();
 
 	protected boolean areDynamicNonLazyAssociationsChecked() {
 		return true;
 	}
 
 	protected void createData() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		courseExpected = new Course();
 		courseExpected.setCourseCode( "HIB" );
 		courseExpected.setDescription( "Hibernate Training" );
 		courseMeetingExpected1 = new CourseMeeting( courseExpected, "Monday", 1, "1313 Mockingbird Lane" );
 		courseMeetingExpected2 = new CourseMeeting( courseExpected, "Tuesday", 2, "1313 Mockingbird Lane" );
 		courseExpected.getCourseMeetings().add( courseMeetingExpected1 );
 		courseExpected.getCourseMeetings().add( courseMeetingExpected2 );
 		s.save( courseExpected );
 
 		yogiExpected = new Student();
 		yogiExpected.setName( new PersonName( "Yogi", "The", "Bear" ) );
 		yogiExpected.setStudentNumber( 111 );
 		yogiExpected.setPreferredCourse( courseExpected );
 		List yogiSecretCodes = new ArrayList();
 		yogiSecretCodes.add( Integer.valueOf( 0 ) );
 		yogiExpected.setSecretCodes( yogiSecretCodes );
 		s.save( yogiExpected );
 
 		Address address1 = new Address( yogiExpected, "home", "1 Main Street", "Podunk", "WA", "98000", "USA" );
 		Address address2 = new Address( yogiExpected, "work", "2 Main Street", "NotPodunk", "WA", "98001", "USA" );
 		yogiExpected.getAddresses().put( address1.getAddressType(), address1 );
 		yogiExpected.getAddresses().put( address2.getAddressType(), address2  );		
 		s.save( address1 );
 		s.save( address2 );
 
 		shermanExpected = new Student();
 		shermanExpected.setName( new PersonName( "Sherman", null, "Grote" ) );
 		shermanExpected.setStudentNumber( 999 );
 		List shermanSecretCodes = new ArrayList();
 		shermanSecretCodes.add( Integer.valueOf( 1 ) );
 		shermanSecretCodes.add( Integer.valueOf( 2 ) );
 		shermanExpected.setSecretCodes( shermanSecretCodes );
 		s.save( shermanExpected );
 
 		shermanEnrolmentExpected = new Enrolment();
 		shermanEnrolmentExpected.setCourse( courseExpected );
 		shermanEnrolmentExpected.setCourseCode( courseExpected.getCourseCode() );
 		shermanEnrolmentExpected.setSemester( ( short ) 1 );
 		shermanEnrolmentExpected.setYear( ( short ) 1999 );
 		shermanEnrolmentExpected.setStudent( shermanExpected );
 		shermanEnrolmentExpected.setStudentNumber( shermanExpected.getStudentNumber() );
 		shermanExpected.getEnrolments().add( shermanEnrolmentExpected );
 		s.save( shermanEnrolmentExpected );
 
 		yogiEnrolmentExpected = new Enrolment();
 		yogiEnrolmentExpected.setCourse( courseExpected );
 		yogiEnrolmentExpected.setCourseCode( courseExpected.getCourseCode() );
 		yogiEnrolmentExpected.setSemester( ( short ) 3 );
 		yogiEnrolmentExpected.setYear( ( short ) 1998 );
 		yogiEnrolmentExpected.setStudent( yogiExpected );
 		yogiEnrolmentExpected.setStudentNumber( yogiExpected.getStudentNumber() );
 		yogiExpected.getEnrolments().add( yogiEnrolmentExpected );
 		s.save( yogiEnrolmentExpected );
 
 		t.commit();
 		s.close();
 	}
 
 	protected void deleteData() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 /*
 		List students = s.createQuery( "from Student" ).list();
 		for ( Iterator it = students.iterator(); it.hasNext(); ) {
 			s.delete( it.next() );
 		}
 		s.createQuery( "delete from Enrolment" ).executeUpdate();
 		s.createQuery( "delete from CourseMeeting" ).executeUpdate();
 		s.createQuery( "delete from Course" ).executeUpdate();
 */
 		s.delete( yogiExpected );
 		s.delete( shermanExpected );
 		s.delete( yogiEnrolmentExpected );
 		s.delete( shermanEnrolmentExpected );
 		s.delete( courseMeetingExpected1 );
 		s.delete( courseMeetingExpected2 );
 		s.delete( courseExpected );
 		t.commit();
 		s.close();
 	}
-	
+
+
+	public void testAliasToEntityMapNoProjectionList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				return s.createCriteria( Student.class, "s" )
+						.createAlias( "s.enrolments", "e", Criteria.LEFT_JOIN )
+						.createAlias( "e.course", "c", Criteria.LEFT_JOIN )
+								.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
+						.addOrder( Order.asc( "s.studentNumber") );
+			}
+		};
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "from Student s left join s.enrolments e left join e.course c order by s.studentNumber" )
+						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Map yogiMap = ( Map ) resultList.get( 0 );
+				assertEquals( 3, yogiMap.size() );
+				Map shermanMap = ( Map ) resultList.get( 1 );
+				assertEquals( 3, shermanMap.size() );
+				assertEquals( yogiExpected, yogiMap.get( "s" ) );
+				assertEquals( yogiEnrolmentExpected, yogiMap.get( "e" ) );
+				assertEquals( courseExpected, yogiMap.get( "c" ) );
+				assertEquals( shermanExpected, shermanMap.get( "s" ) );
+				assertEquals( shermanEnrolmentExpected, shermanMap.get( "e" ) );
+				assertEquals( courseExpected, shermanMap.get( "c" ) );
+				assertSame( ( ( Map ) resultList.get( 0 ) ).get( "c" ), shermanMap.get( "c" ) );
+			}
+		};
+		runTest( hqlExecutor, criteriaExecutor, checker, false );
+	}
+
+	public void testAliasToEntityMapNoProjectionMultiAndNullList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				return s.createCriteria( Student.class, "s" )
+						.createAlias( "s.preferredCourse", "p", Criteria.LEFT_JOIN )
+						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
+								.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
+						.addOrder( Order.asc( "s.studentNumber") );
+			}
+		};
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "from Student s left join s.preferredCourse p left join s.addresses a order by s.studentNumber" )
+						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 3, resultList.size() );
+				Map yogiMap1 = ( Map ) resultList.get( 0 );
+				assertEquals( 3, yogiMap1.size() );
+				Map yogiMap2 = ( Map ) resultList.get( 1 );
+				assertEquals( 3, yogiMap2.size() );
+				Map shermanMap = ( Map ) resultList.get( 2 );
+				assertEquals( 3, shermanMap.size() );
+				assertEquals( yogiExpected, yogiMap1.get( "s" ) );
+				assertEquals( courseExpected, yogiMap1.get( "p" ) );
+				Address yogiAddress1 = ( Address ) yogiMap1.get( "a" );
+				assertEquals( yogiExpected.getAddresses().get( yogiAddress1.getAddressType() ),
+						yogiMap1.get( "a" ));
+				assertEquals( yogiExpected, yogiMap2.get( "s" ) );
+				assertEquals( courseExpected, yogiMap2.get( "p" ) );
+				Address yogiAddress2 = ( Address ) yogiMap2.get( "a" );
+				assertEquals( yogiExpected.getAddresses().get( yogiAddress2.getAddressType() ),
+						yogiMap2.get( "a" ));
+				assertSame( yogiMap1.get( "s" ), yogiMap2.get( "s" ) );
+				assertSame( yogiMap1.get( "p" ), yogiMap2.get( "p" ) );
+				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
+				assertEquals( shermanExpected, shermanMap.get( "s" ) );
+				assertEquals( shermanExpected.getPreferredCourse(), shermanMap.get( "p" ) );
+				assertNull( shermanMap.get( "a") );
+			}
+		};
+		runTest( hqlExecutor, criteriaExecutor, checker, false );
+	}	
+
+	public void testAliasToEntityMapNoProjectionNullAndNonNullAliasList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				return s.createCriteria( Student.class, "s" )
+						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
+								.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
+						.createCriteria( "s.preferredCourse", Criteria.INNER_JOIN )
+						.addOrder( Order.asc( "s.studentNumber") );
+			}
+		};
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "from Student s left join s.addresses a left join s.preferredCourse order by s.studentNumber" )
+						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Map yogiMap1 = ( Map ) resultList.get( 0 );
+				assertEquals( 2, yogiMap1.size() );
+				Map yogiMap2 = ( Map ) resultList.get( 1 );
+				assertEquals( 2, yogiMap2.size() );
+				assertEquals( yogiExpected, yogiMap1.get( "s" ) );
+				Address yogiAddress1 = ( Address ) yogiMap1.get( "a" );
+				assertEquals( yogiExpected.getAddresses().get( yogiAddress1.getAddressType() ),
+						yogiMap1.get( "a" ));
+				assertEquals( yogiExpected, yogiMap2.get( "s" ) );
+				Address yogiAddress2 = ( Address ) yogiMap2.get( "a" );
+				assertEquals( yogiExpected.getAddresses().get( yogiAddress2.getAddressType() ),
+						yogiMap2.get( "a" ));
+				assertSame( yogiMap1.get( "s" ), yogiMap2.get( "s" ) );
+				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
+			}
+		};
+		runTest( hqlExecutor, criteriaExecutor, checker, false );
+	}
+
 	public void testEntityWithNonLazyOneToManyUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Course.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Course" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Course );
 				assertEquals( courseExpected, results );
 				assertTrue( Hibernate.isInitialized( ( ( Course ) courseExpected ).getCourseMeetings() ) );
 				assertEquals( courseExpected.getCourseMeetings(), ( ( Course ) courseExpected ).getCourseMeetings() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	public void testEntityWithNonLazyManyToOneList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( CourseMeeting.class )
 						.addOrder( Order.asc( "id.day") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			protected Query getQuery(Session s) {
 				return s.createQuery( "from CourseMeeting order by id.day" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( courseMeetingExpected1, resultList.get( 0 ) );
 				assertEquals( courseMeetingExpected2, resultList.get( 1 ) );
 				assertTrue( Hibernate.isInitialized( ( ( CourseMeeting ) resultList.get( 0 ) ).getCourse() ) );
 				assertTrue( Hibernate.isInitialized( ( ( CourseMeeting ) resultList.get( 1 ) ).getCourse() ) );
 				assertEquals( courseExpected, ( ( CourseMeeting ) resultList.get( 0 ) ).getCourse() );
 				assertEquals( courseExpected, ( ( CourseMeeting ) resultList.get( 1 ) ).getCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testEntityWithLazyAssnUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.add( Restrictions.eq( "studentNumber", shermanExpected.getStudentNumber() ) );
 				}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s where s.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", shermanExpected.getStudentNumber() );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Student );
 				assertEquals( shermanExpected, results );
+				assertNotNull(  ( ( Student ) results ).getEnrolments() );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) results ).getEnrolments() ) );
 				assertNull( ( ( Student ) results ).getPreferredCourse() );				
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	// should use RootEntityTransformer by default
 	public void testEntityWithLazyAssnList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class )
 						.addOrder( Order.asc( "studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student order by studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
+				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
+				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() );
+				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
+				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() ) );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
-	public void testEntityWithJoinFetchedLazyOneToManySingleElementList() throws Exception {
+	public void testEntityWithUnaliasedJoinFetchedLazyOneToManySingleElementList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.enrolments order by s.studentNumber" );
 			}
 		};
 
-		// aliased
-		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				assertEquals( yogiExpected, resultList.get( 0 ) );
+				assertEquals( shermanExpected, resultList.get( 1 ) );
+				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
+				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
+				}
+			}
+		};
+
+		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false);
+	}
+
+	public void testJoinWithFetchJoinListCriteria() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
-				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
-						.createAlias( "s.enrolments", "e", Criteria.LEFT_JOIN )
+						.createAlias( "s.preferredCourse", "pc", Criteria.LEFT_JOIN  )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
-						.addOrder( Order.asc( "s.studentNumber") );
+						.addOrder( Order.asc( "s.studentNumber") );						
 			}
 		};
-		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
-			protected Criteria getCriteria(Session s) {
-				// should use RootEntityTransformer by default
-				return s.createCriteria( Student.class, "s" )
-						.createAlias( "s.enrolments", "e", Criteria.LEFT_JOIN )
-						.setFetchMode( "e", FetchMode.JOIN )
-						.addOrder( Order.asc( "s.studentNumber") );
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				assertEquals( yogiExpected, resultList.get( 0 ) );
+				// The following fails for criteria due to HHH-3524
+				//assertEquals( yogiExpected.getPreferredCourse(), ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() );
+				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
+						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );				
+				assertEquals( shermanExpected, resultList.get( 1 ) );
+				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
+				}
 			}
 		};
-		CriteriaExecutor criteriaExecutorAliased3 = new CriteriaExecutor() {
-			protected Criteria getCriteria(Session s) {
-				// should use RootEntityTransformer by default
-				return s.createCriteria( Student.class, "s" )
-						.createCriteria( "s.enrolments", "e", Criteria.LEFT_JOIN )
-						.setFetchMode( "enrolments", FetchMode.JOIN )
-						.addOrder( Order.asc( "s.studentNumber") );
+		runTest( null, criteriaExecutor, checker, false );
+	}
+
+	public void testJoinWithFetchJoinListHql() throws Exception {
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber" );
 			}
 		};
-		CriteriaExecutor criteriaExecutorAliased4 = new CriteriaExecutor() {
-			protected Criteria getCriteria(Session s) {
-				// should use RootEntityTransformer by default
-				return s.createCriteria( Student.class, "s" )
-						.createCriteria( "s.enrolments", "e", Criteria.LEFT_JOIN )
-						.setFetchMode( "e", FetchMode.JOIN )
-						.addOrder( Order.asc( "s.studentNumber") );
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				assertEquals( yogiExpected, yogiObjects[ 0 ] );
+				assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
+				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
+				assertEquals( shermanExpected, shermanObjects[ 0 ] );
+				assertNull( shermanObjects[ 1 ] );
+				assertNull( ( ( Student ) shermanObjects[ 0 ] ).getPreferredCourse() );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 0 ] ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
+				}
 			}
 		};
-		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
+		runTest( hqlExecutor, null, checker, false );
+	}
+
+	public void testJoinWithFetchJoinWithOwnerAndPropProjectedList() throws Exception {
+		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select s, s.name from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				assertEquals( yogiExpected, yogiObjects[ 0 ] );
+				assertEquals( yogiExpected.getName(), yogiObjects[ 1 ] );
+				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
+				assertEquals( shermanExpected, shermanObjects[ 0 ] );
+				assertEquals( shermanExpected.getName(), shermanObjects[ 1 ] );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 0 ] ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
+				}
+			}
+		};
+		runTest( hqlSelectNewMapExecutor, null, checker, false );
+	}
+
+	public void testJoinWithFetchJoinWithPropAndOwnerProjectedList() throws Exception {
+		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select s.name, s from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				assertEquals( yogiExpected.getName(), yogiObjects[ 0 ] );
+				assertEquals( yogiExpected, yogiObjects[ 1 ] );
+				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
+				assertEquals( shermanExpected.getName(), shermanObjects[ 0 ] );
+				assertEquals( shermanExpected, shermanObjects[ 1 ] );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 1 ] ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
+				}
+			}
+		};
+		runTest( hqlSelectNewMapExecutor, null, checker, false );
+	}
+
+	public void testJoinWithFetchJoinWithOwnerAndAliasedJoinedProjectedListHql() throws Exception {
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select s, pc from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				assertEquals( yogiExpected, yogiObjects[ 0 ] );
+				assertEquals(
+						yogiExpected.getPreferredCourse().getCourseCode(),
+						( ( Course ) yogiObjects[ 1 ] ).getCourseCode()
+				);
+				Object[] shermanObjects = ( Object[]  ) resultList.get( 1 );
+				assertEquals( shermanExpected, shermanObjects[ 0 ] );
+				assertNull( shermanObjects[ 1 ] );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
+				}
+			}
+		};
+		runTest( hqlExecutor, null, checker, false );
+	}
+
+	public void testJoinWithFetchJoinWithAliasedJoinedAndOwnerProjectedListHql() throws Exception {
+		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select pc, s from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				assertEquals( yogiExpected, yogiObjects[ 1 ] );
+				assertEquals(
+						yogiExpected.getPreferredCourse().getCourseCode(),
+						( ( Course ) yogiObjects[ 0 ] ).getCourseCode()
+				);
+				Object[] shermanObjects = ( Object[]  ) resultList.get( 1 );
+				assertEquals( shermanExpected, shermanObjects[ 1 ] );
+				assertNull( shermanObjects[ 0 ] );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 0 ] );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
+				}
+			}
+		};
+		runTest( hqlSelectNewMapExecutor, null, checker, false );
+	}
+
+	public void testEntityWithAliasedJoinFetchedLazyOneToManySingleElementListHql() throws Exception {
+		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.enrolments e order by s.studentNumber" );
 			}
 		};
+
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
+				assertEquals(
+						yogiExpected.getPreferredCourse().getCourseCode(),
+						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode()
+				);
 				assertEquals( shermanExpected, resultList.get( 1 ) );
+				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
-					assertEquals( shermanExpected.getEnrolments(), ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				}
 			}
 		};
 
-		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false);
-		runTest( hqlExecutorAliased, criteriaExecutorAliased1, checker, false);
-		runTest( null, criteriaExecutorAliased2, checker, false);
-		runTest( null, criteriaExecutorAliased3, checker, false);
-		runTest( null, criteriaExecutorAliased4, checker, false);
+		runTest( hqlExecutor, null, checker, false);
+	}
 
+	public void testEntityWithSelectFetchedLazyOneToManySingleElementListCriteria() throws Exception {
+		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				// should use RootEntityTransformer by default
+				return s.createCriteria( Student.class, "s" )
+						.setFetchMode( "enrolments", FetchMode.SELECT )
+						.addOrder( Order.asc( "s.studentNumber") );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				assertEquals( yogiExpected, resultList.get( 0 ) );
+				assertEquals( shermanExpected, resultList.get( 1 ) );
+				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
+				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
+				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
+				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
+			}
+		};
+
+		runTest( null, criteriaExecutorUnaliased, checker, false);
 	}
 
 	public void testEntityWithJoinFetchedLazyOneToManyMultiAndNullElementList() throws Exception {
 		//unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.addresses order by s.studentNumber" );
 			}
 		};
 
 		//aliased
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "a", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased3 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased4 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "a", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.addresses a order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertSame( resultList.get( 0 ), resultList.get( 1 ) );
 				assertEquals( shermanExpected, resultList.get( 2 ) );
+				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getAddresses() );
+				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getAddresses() );
+				assertNotNull( ( ( Student ) resultList.get( 2 ) ).getAddresses() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getAddresses() ) );
 					assertEquals( yogiExpected.getAddresses(), ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 					assertTrue( ( ( Student ) resultList.get( 2 ) ).getAddresses().isEmpty() );
 				}
 			}
 		};
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false );
 		runTest( hqlExecutorAliased, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 		runTest( null, criteriaExecutorAliased3, checker, false );
 		runTest( null, criteriaExecutorAliased4, checker, false );
 	}
 
 	public void testEntityWithJoinFetchedLazyManyToOneList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.preferredCourse order by s.studentNumber" );
 			}
 		};
 
 		// aliased
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "pCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased3 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased4 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "pCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.preferredCourse pCourse order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false );
 		runTest( hqlExecutorAliased, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 		runTest( null, criteriaExecutorAliased3, checker, false );
 		runTest( null, criteriaExecutorAliased4, checker, false );
 	}
 
+	public void testEntityWithJoinFetchedLazyManyToOneUsingProjectionList() throws Exception {
+		// unaliased
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				// should use RootEntityTransformer by default
+				return s.createCriteria( Enrolment.class, "e" )
+						.createAlias( "e.student", "s", Criteria.LEFT_JOIN )
+						.setFetchMode( "student", FetchMode.JOIN )
+						.setFetchMode( "student.preferredCourse", FetchMode.JOIN )
+						.setProjection(
+								Projections.projectionList()
+										.add( Projections.property( "s.name" ) )
+										.add( Projections.property( "e.student" ) )
+						)
+						.addOrder( Order.asc( "s.studentNumber") );
+			}
+		};
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select s.name, s from Enrolment e left join e.student s left join fetch s.preferredCourse order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
+				assertEquals( yogiExpected.getName(), yogiObjects[ 0 ] );
+				assertEquals( shermanExpected.getName(), shermanObjects[ 0 ] );
+				// The following fails for criteria due to HHH-1425
+				// assertEquals( yogiExpected, yogiObjects[ 1 ] );
+				// assertEquals( shermanExpected, shermanObjects[ 1 ] );
+				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiObjects[ 1 ] ).getStudentNumber() );
+				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanObjects[ 1 ] ).getStudentNumber() );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					// The following fails for criteria due to HHH-1425
+					//assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() ) );
+					//assertEquals( yogiExpected.getPreferredCourse(),  ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() );
+					//assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() ) );
+					//assertEquals( shermanExpected.getPreferredCourse(),  ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() );
+				}
+			}
+		};
+		runTest( hqlExecutor, criteriaExecutor, checker, false );
+	}	
+
 	public void testEntityWithJoinedLazyOneToManySingleElementListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.enrolments", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.enrolments", "e", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.enrolments", "e", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
+				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
+				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				}
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );		
 	}
 
 	public void testEntityWithJoinedLazyOneToManyMultiAndNullListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertSame( resultList.get( 0 ), resultList.get( 1 ) );
 				assertEquals( shermanExpected, resultList.get( 2 ) );
+				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getAddresses() );
+				assertNotNull( ( ( Student ) resultList.get( 2 ) ).getAddresses() );
+				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getAddresses() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getAddresses() ) );
 					assertEquals( yogiExpected.getAddresses(), ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 					assertTrue( ( ( Student ) resultList.get( 2 ) ).getAddresses().isEmpty() );
 				}
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 	}
 	
 	public void testEntityWithJoinedLazyManyToOneListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "p", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "p", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 	}
 
 	public void testEntityWithJoinedLazyOneToManySingleElementListHql() throws Exception {
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.enrolments order by s.studentNumber" );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.enrolments e order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.get( 0 ) instanceof Object[] );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals( yogiEnrolmentExpected, yogiObjects[ 1 ] );
 				assertTrue( resultList.get( 0 ) instanceof Object[] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertEquals( shermanEnrolmentExpected, shermanObjects[ 1 ] );
 			}
 		};
 		runTest( hqlExecutorUnaliased, null, checker, false );
 		runTest( hqlExecutorAliased, null, checker, false );
 	}
 
 	public void testEntityWithJoinedLazyOneToManyMultiAndNullListHql() throws Exception {
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.addresses order by s.studentNumber" );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.addresses a order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertTrue( resultList.get( 0 ) instanceof Object[] );
 				Object[] yogiObjects1 = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects1[ 0 ] );
 				Address address1 = ( Address ) yogiObjects1[ 1 ];
 				assertEquals( yogiExpected.getAddresses().get( address1.getAddressType() ), address1 );
 				Object[] yogiObjects2 = ( Object[] ) resultList.get( 1 );
 				assertSame( yogiObjects1[ 0 ], yogiObjects2[ 0 ] );
 				Address address2 = ( Address ) yogiObjects2[ 1 ];
 				assertEquals( yogiExpected.getAddresses().get( address2.getAddressType() ), address2 );
 				assertFalse( address1.getAddressType().equals( address2.getAddressType() ) );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 2 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertNull( shermanObjects[ 1 ] );
 			}
 		};
 		runTest( hqlExecutorUnaliased, null, checker, false );
-		runTest( hqlExecutorAliased, null, checker, false );
-	}
-
-	public void testEntityWithJoinedLazyManyToOneListHql() throws Exception {
-		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
-			protected Query getQuery(Session s) {
-				// should use RootEntityTransformer by default
-				return s.createQuery( "from Student s left join s.preferredCourse order by s.studentNumber" );
-			}
-		};
-		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
-			protected Query getQuery(Session s) {
-				// should use RootEntityTransformer by default
-				return s.createQuery( "from Student s left join s.preferredCourse p order by s.studentNumber" );
-			}
-		};
-		ResultChecker checker = new ResultChecker() {
-			public void check(Object results) {
-				List resultList = ( List ) results;
-				assertEquals( 2, resultList.size() );
-				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
-				assertEquals( yogiExpected, yogiObjects[ 0 ] );
-				assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
-				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
-				assertEquals( shermanExpected, shermanObjects[ 0 ] );
-				assertNull( shermanObjects[ 1 ] );
-			}
-		};
-		runTest( hqlExecutorUnaliased, null, checker, false );
-		runTest( hqlExecutorAliased, null, checker, false );
-	}
-
-	public void testAliasToEntityMapNoProjectionList() throws Exception {
-		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
-			protected Criteria getCriteria(Session s) {
-				return s.createCriteria( Student.class, "s" )
-						.createAlias( "s.enrolments", "e", Criteria.LEFT_JOIN )
-						.createAlias( "e.course", "c", Criteria.LEFT_JOIN )						
-								.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
-						.addOrder( Order.asc( "s.studentNumber") );
-			}
-		};
-		HqlExecutor hqlExecutor = new HqlExecutor() {
-			public Query getQuery(Session s) {
-				return s.createQuery( "from Student s left join s.enrolments e left join e.course c order by s.studentNumber" )
-						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
-			}
-		};
-		ResultChecker checker = new ResultChecker() {
-			public void check(Object results) {
-				List resultList = ( List ) results;
-				assertEquals( 2, resultList.size() );
-				Map yogiMap = ( Map ) resultList.get( 0 );
-				assertEquals( 3, yogiMap.size() );
-				Map shermanMap = ( Map ) resultList.get( 1 );
-				assertEquals( 3, shermanMap.size() );
-				assertEquals( yogiExpected, yogiMap.get( "s" ) );
-				assertEquals( yogiEnrolmentExpected, yogiMap.get( "e" ) );
-				assertEquals( courseExpected, yogiMap.get( "c" ) );
-				assertEquals( shermanExpected, shermanMap.get( "s" ) );
-				assertEquals( shermanEnrolmentExpected, shermanMap.get( "e" ) );
-				assertEquals( courseExpected, shermanMap.get( "c" ) );
-				assertSame( ( ( Map ) resultList.get( 0 ) ).get( "c" ), shermanMap.get( "c" ) );
-			}
-		};
-		runTest( hqlExecutor, criteriaExecutor, checker, false );
+		runTest( hqlExecutorAliased, null, checker, false );
 	}
 
-	public void testAliasToEntityMapNoProjectionMultiAndNullList() throws Exception {
-		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
-			protected Criteria getCriteria(Session s) {
-				return s.createCriteria( Student.class, "s" )
-						.createAlias( "s.preferredCourse", "p", Criteria.LEFT_JOIN )
-						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
-								.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
-						.addOrder( Order.asc( "s.studentNumber") );
+	public void testEntityWithJoinedLazyManyToOneListHql() throws Exception {
+		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
+			protected Query getQuery(Session s) {
+				// should use RootEntityTransformer by default
+				return s.createQuery( "from Student s left join s.preferredCourse order by s.studentNumber" );
 			}
 		};
-		HqlExecutor hqlExecutor = new HqlExecutor() {
-			public Query getQuery(Session s) {
-				return s.createQuery( "from Student s left join s.preferredCourse p left join s.addresses a order by s.studentNumber" )
-						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
+			protected Query getQuery(Session s) {
+				// should use RootEntityTransformer by default
+				return s.createQuery( "from Student s left join s.preferredCourse p order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
-				assertEquals( 3, resultList.size() );
-				Map yogiMap1 = ( Map ) resultList.get( 0 );
-				assertEquals( 3, yogiMap1.size() );
-				Map yogiMap2 = ( Map ) resultList.get( 1 );
-				assertEquals( 3, yogiMap2.size() );
-				Map shermanMap = ( Map ) resultList.get( 2 );
-				assertEquals( 3, shermanMap.size() );
-				assertEquals( yogiExpected, yogiMap1.get( "s" ) );
-				assertEquals( courseExpected, yogiMap1.get( "p" ) );
-				Address yogiAddress1 = ( Address ) yogiMap1.get( "a" );
-				assertEquals( yogiExpected.getAddresses().get( yogiAddress1.getAddressType() ),
-						yogiMap1.get( "a" ));
-				assertEquals( yogiExpected, yogiMap2.get( "s" ) );
-				assertEquals( courseExpected, yogiMap2.get( "p" ) );
-				Address yogiAddress2 = ( Address ) yogiMap2.get( "a" );
-				assertEquals( yogiExpected.getAddresses().get( yogiAddress2.getAddressType() ),
-						yogiMap2.get( "a" ));
-				assertSame( yogiMap1.get( "s" ), yogiMap2.get( "s" ) );
-				assertSame( yogiMap1.get( "p" ), yogiMap2.get( "p" ) );
-				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
-				assertEquals( shermanExpected, shermanMap.get( "s" ) );
-				assertEquals( shermanExpected.getPreferredCourse(), shermanMap.get( "p" ) );
-				assertNull( shermanMap.get( "a") );
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				assertEquals( yogiExpected, yogiObjects[ 0 ] );
+				assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
+				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
+				assertEquals( shermanExpected, shermanObjects[ 0 ] );
+				assertNull( shermanObjects[ 1 ] );
 			}
 		};
-		runTest( hqlExecutor, criteriaExecutor, checker, false );
+		runTest( hqlExecutorUnaliased, null, checker, false );
+		runTest( hqlExecutorAliased, null, checker, false );
 	}
 
 	public void testAliasToEntityMapOneProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.student" ).as( "student" ) )
 						.addOrder( Order.asc( "e.studentNumber") )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student as student from Enrolment e order by e.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( 1, yogiMap.size() );
-				//assertTrue( yogiMap[ 0 ] instanceof HibernateProxy );
-				assertTrue( yogiMap.get( "student" ) instanceof Student );
-				if( Hibernate.isInitialized( yogiMap.get( "student" ) ) ) {
-					assertEquals( yogiExpected, yogiMap.get( "student" ) );
-				}
-				else {
-					assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
-				}
 				assertEquals( 1, shermanMap.size() );
-				//assertTrue( shermanMap[ 0 ] instanceof HibernateProxy );
+				// TODO: following are initialized for hql and uninitialied for criteria; why?
+				// assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
+				// assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
+				assertTrue( yogiMap.get( "student" ) instanceof Student );
 				assertTrue( shermanMap.get( "student" ) instanceof Student );
-				if( Hibernate.isInitialized( shermanMap.get( "student" ) ) ) {
-					assertEquals( shermanExpected, shermanMap.get( "student" ) );
-				}
-				else {
-					assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
-				}
+				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
+				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false);
 	}
 
 	public void testAliasToEntityMapMultiProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "e.student" ), "student" )
 										.add( Property.forName( "e.semester" ), "semester" )
 										.add( Property.forName( "e.year" ), "year" )
 										.add( Property.forName( "e.course" ), "course" )
 						)
 						.addOrder( Order.asc( "studentNumber") )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student as student, e.semester as semester, e.year as year, e.course as course from Enrolment e order by e.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( 4, yogiMap.size() );
-				//assertTrue( yogiMap[ 0 ] instanceof HibernateProxy );
+				assertEquals( 4, shermanMap.size() );
 				assertTrue( yogiMap.get( "student" ) instanceof Student );
-				if( Hibernate.isInitialized( yogiMap.get( "student" ) ) ) {
-					assertEquals( yogiExpected, yogiMap.get( "student" ) );
-				}
-				else {
-					assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
-				}
+				assertTrue( shermanMap.get( "student" ) instanceof Student );
+				// TODO: following are initialized for hql and uninitialied for criteria; why?
+				// assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
+				// assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
+				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
+				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
 				assertEquals( yogiEnrolmentExpected.getSemester(), yogiMap.get( "semester" ) );
 				assertEquals( yogiEnrolmentExpected.getYear(), yogiMap.get( "year" )  );
 				assertEquals( courseExpected, yogiMap.get( "course" ) );
-				assertEquals( 4, shermanMap.size() );
-				//assertTrue( shermanMap[ 0 ] instanceof HibernateProxy );
-				assertTrue( shermanMap.get( "student" ) instanceof Student );
-				if( Hibernate.isInitialized( shermanMap.get( "student" ) ) ) {
-					assertEquals( shermanExpected, shermanMap.get( "student" ) );
-				}
-				else {
-					assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
-				}
 				assertEquals( shermanEnrolmentExpected.getSemester(), shermanMap.get( "semester" ) );
 				assertEquals( shermanEnrolmentExpected.getYear(), shermanMap.get( "year" )  );
 				assertEquals( courseExpected, shermanMap.get( "course" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testAliasToEntityMapMultiProjectionWithNullAliasList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "e.student" ), "student" )
 										.add( Property.forName( "e.semester" ) )
 										.add( Property.forName( "e.year" ) )
 										.add( Property.forName( "e.course" ), "course" )
 						)
 						.addOrder( Order.asc( "e.studentNumber") )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student as student, e.semester, e.year, e.course as course from Enrolment e order by e.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				Map shermanMap = ( Map ) resultList.get( 1 );
-				//assertEquals( 2, yogiMap.size() );
-				//assertTrue( yogiMap[ 0 ] instanceof HibernateProxy );
+				// TODO: following are initialized for hql and uninitialied for criteria; why?
+				// assertFalse( Hibernate.isInitialized( yogiMap.get( "student" ) ) );
+				// assertFalse( Hibernate.isInitialized( shermanMap.get( "student" ) ) );
 				assertTrue( yogiMap.get( "student" ) instanceof Student );
-				if( Hibernate.isInitialized( yogiMap.get( "student" ) ) ) {
-					assertEquals( yogiExpected, yogiMap.get( "student" ) );
-				}
-				else {
-					assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
-				}
+				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiMap.get( "student" ) ).getStudentNumber() );
+				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
 				assertNull( yogiMap.get( "semester" ) );
 				assertNull( yogiMap.get( "year" )  );
 				assertEquals( courseExpected, yogiMap.get( "course" ) );
-				//assertEquals( 2, shermanMap.size() );
-				//assertTrue( shermanMap[ 0 ] instanceof HibernateProxy );
-				assertTrue( shermanMap.get( "student" ) instanceof Student );
-				if( Hibernate.isInitialized( shermanMap.get( "student" ) ) ) {
-					assertEquals( shermanExpected, shermanMap.get( "student" ) );
-				}
-				else {
-					assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanMap.get( "student" ) ).getStudentNumber() );
-				}
 				assertNull( shermanMap.get( "semester" ) );
 				assertNull( shermanMap.get( "year" )  );
 				assertEquals( courseExpected, shermanMap.get( "course" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testAliasToEntityMapMultiAggregatedPropProjectionSingleResult() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class )
 						.setProjection(
 								Projections.projectionList()
 									.add( Projections.min( "studentNumber" ).as( "minStudentNumber" ) )
 									.add( Projections.max( "studentNumber" ).as( "maxStudentNumber" ) )
 						)
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select min( e.studentNumber ) as minStudentNumber, max( e.studentNumber ) as maxStudentNumber from Enrolment e" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Map );
 				Map resultMap = ( Map ) results;
 				assertEquals( 2, resultMap.size() );
 				assertEquals( yogiExpected.getStudentNumber(), resultMap.get( "minStudentNumber" ) );
 				assertEquals( shermanExpected.getStudentNumber(), resultMap.get( "maxStudentNumber" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	public void testOneNonEntityProjectionUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.semester" ) )
 						.add( Restrictions.eq( "e.studentNumber", shermanEnrolmentExpected.getStudentNumber() ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.semester from Enrolment e where e.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", shermanEnrolmentExpected.getStudentNumber() );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Short );
 				assertEquals( Short.valueOf( shermanEnrolmentExpected.getSemester() ), results );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	public void testOneNonEntityProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.semester" ) )
 						.addOrder( Order.asc( "e.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.semester from Enrolment e order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiEnrolmentExpected.getSemester(), resultList.get( 0 ) );
 				assertEquals( shermanEnrolmentExpected.getSemester(), resultList.get( 1 ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testListElementsProjectionList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.secretCodes" )
 						.setProjection( Projections.property( "s.secretCodes" ) )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select elements(s.secretCodes) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertTrue( resultList.contains( yogiExpected.getSecretCodes().get( 0 ) ) );
 				assertTrue( resultList.contains( shermanExpected.getSecretCodes().get( 0 ) ) );
 				assertTrue( resultList.contains( shermanExpected.getSecretCodes().get( 1 ) ) );
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	public void testOneEntityProjectionUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class )
 						.setProjection( Projections.property( "student" ) )
 						.add( Restrictions.eq( "studentNumber", Long.valueOf( yogiExpected.getStudentNumber() ) ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student from Enrolment e where e.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", Long.valueOf( yogiExpected.getStudentNumber() ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Student );
 				Student student = ( Student ) results;
-				if ( Hibernate.isInitialized( student ) ) {
-					assertEquals( yogiExpected, student );
-				}
-				else {
-					assertEquals( yogiExpected.getStudentNumber(), student.getStudentNumber() );
-				}
+				// TODO: following is initialized for hql and uninitialied for criteria; why?
+				//assertFalse( Hibernate.isInitialized( student ) );
+				assertEquals( yogiExpected.getStudentNumber(), student.getStudentNumber() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	public void testOneEntityProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			// should use PassThroughTransformer by default
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection( Projections.property( "e.student" ) )
 						.addOrder( Order.asc( "e.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student from Enrolment e order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
-				if ( Hibernate.isInitialized( resultList.get( 0 ) ) ) {
-					assertEquals( yogiExpected, resultList.get( 0 ) );
-				}
-				else {
-					assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) resultList.get( 0 ) ).getStudentNumber() );
-				}
-				if ( Hibernate.isInitialized( resultList.get( 1 ) ) ) {
-					assertEquals( shermanExpected, resultList.get( 1 ) );
-				}
-				else {
-					assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) resultList.get( 1 ) ).getStudentNumber() );
-				}
+				// TODO: following is initialized for hql and uninitialied for criteria; why?
+				//assertFalse( Hibernate.isInitialized( resultList.get( 0 ) ) );
+				//assertFalse( Hibernate.isInitialized( resultList.get( 1 ) ) );
+				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) resultList.get( 0 ) ).getStudentNumber() );
+				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) resultList.get( 1 ) ).getStudentNumber() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testMultiEntityProjectionUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "student" ) )
 										.add( Property.forName( "semester" ) )
 										.add( Property.forName( "year" ) )
 										.add( Property.forName( "course" ) )
 						)
 						.add( Restrictions.eq( "studentNumber", Long.valueOf( shermanEnrolmentExpected.getStudentNumber() ) ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select e.student, e.semester, e.year, e.course from Enrolment e  where e.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", shermanEnrolmentExpected.getStudentNumber() );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Object[] );
 				Object shermanObjects[] = ( Object [] ) results;
 				assertEquals( 4, shermanObjects.length );
-				//assertTrue( shermanObjects[ 0 ] instanceof HibernateProxy );
+				assertNotNull( shermanObjects[ 0 ] );
 				assertTrue( shermanObjects[ 0 ] instanceof Student );
-				if ( Hibernate.isInitialized( shermanObjects[ 0 ] ) ) {
-					assertEquals( shermanExpected, shermanObjects[ 0 ] );
-				}
+				// TODO: following is initialized for hql and uninitialied for criteria; why?
+				//assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
 				assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 				assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 				assertTrue( ! ( shermanObjects[ 3 ] instanceof HibernateProxy ) );
 				assertTrue( shermanObjects[ 3 ] instanceof Course );
 				assertEquals( courseExpected, shermanObjects[ 3 ] );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	public void testMultiEntityProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use PassThroughTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.setProjection(
 								Projections.projectionList()
 										.add( Property.forName( "e.student" ) )
 										.add( Property.forName( "e.semester" ) )
 										.add( Property.forName( "e.year" ) )
 										.add( Property.forName( "e.course" ) )
 						)
 						.addOrder( Order.asc( "e.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select e.student, e.semester, e.year, e.course from Enrolment e order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( 4, yogiObjects.length );
-				//assertTrue( yogiObjects[ 0 ] instanceof HibernateProxy );
+				// TODO: following is initialized for hql and uninitialied for criteria; why?
+				//assertFalse( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
+				//assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
 				assertTrue( yogiObjects[ 0 ] instanceof Student );
-				if( Hibernate.isInitialized( yogiObjects[ 0 ] ) ) {
-					assertEquals( yogiExpected, yogiObjects[ 0 ] );
-				}
+				assertTrue( shermanObjects[ 0 ] instanceof Student );
 				assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
 				assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
 				assertEquals( courseExpected, yogiObjects[ 3 ] );
-				//assertTrue( shermanObjects[ 0 ] instanceof HibernateProxy );
+				assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
+				assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
+				assertTrue( shermanObjects[ 3 ] instanceof Course );
+				assertEquals( courseExpected, shermanObjects[ 3 ] );
+			}
+		};
+		runTest( hqlExecutor, criteriaExecutor, checker, false );
+	}
+
+	public void testMultiEntityProjectionAliasedList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				// should use PassThroughTransformer by default
+				return s.createCriteria( Enrolment.class, "e" )
+						.setProjection(
+								Projections.projectionList()
+										.add( Property.forName( "e.student" ).as( "st" ) )
+										.add( Property.forName( "e.semester" ).as("sem" ) )
+										.add( Property.forName( "e.year" ).as( "yr" ) )
+										.add( Property.forName( "e.course" ).as( "c" ) )
+						)
+						.addOrder( Order.asc( "e.studentNumber") );
+			}
+		};
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select e.student as st, e.semester as sem, e.year as yr, e.course as c from Enrolment e order by e.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
+				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
+				assertEquals( 4, yogiObjects.length );
+				// TODO: following is initialized for hql and uninitialied for criteria; why?
+				//assertFalse( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
+				//assertFalse( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
+				assertTrue( yogiObjects[ 0 ] instanceof Student );
 				assertTrue( shermanObjects[ 0 ] instanceof Student );
-				if ( Hibernate.isInitialized( shermanObjects[ 0 ] ) ) {
-					assertEquals( shermanExpected, shermanObjects[ 0 ] );
-				}
+				assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
+				assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
+				assertEquals( courseExpected, yogiObjects[ 3 ] );
 				assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 				assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
-				//assertTrue( ! ( shermanObjects[ 3 ] instanceof HibernateProxy ) );
 				assertTrue( shermanObjects[ 3 ] instanceof Course );
 				assertEquals( courseExpected, shermanObjects[ 3 ] );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testSingleAggregatedPropProjectionSingleResult() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class )
 						.setProjection( Projections.min( "studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select min( e.studentNumber ) from Enrolment e" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Long );
 				assertEquals( Long.valueOf( yogiExpected.getStudentNumber() ), results );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	public void testMultiAggregatedPropProjectionSingleResult() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class )
 						.setProjection(
 								Projections.projectionList()
 									.add( Projections.min( "studentNumber" ).as( "minStudentNumber" ) )
 									.add( Projections.max( "studentNumber" ).as( "maxStudentNumber" ) )
 						);
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select min( e.studentNumber ) as minStudentNumber, max( e.studentNumber ) as maxStudentNumber from Enrolment e" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Object[] );
 				Object[] resultObjects = ( Object[] ) results;
 				assertEquals( Long.valueOf( yogiExpected.getStudentNumber() ), resultObjects[ 0 ] );
 				assertEquals( Long.valueOf( shermanExpected.getStudentNumber() ), resultObjects[ 1 ] );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	public void testAliasToBeanDtoOneArgList() throws Exception {
 
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection( Projections.property( "st.name" ).as( "studentName" ) )
 				.addOrder( Order.asc( "st.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName from Student st order by st.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertNull( dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertNull( dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testAliasToBeanDtoMultiArgList() throws Exception {
 
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Property.forName( "co.description" ).as( "courseDescription" ) )
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testMultiProjectionListThenApplyAliasToBean() throws Exception {
 
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ) )
 								.add( Property.forName( "co.description" ) )
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				ResultTransformer transformer = Transformers.aliasToBean( StudentDTO.class );
 				String[] aliases = new String[] { "studentName", "courseDescription" };
 				for ( int i = 0 ; i < resultList.size(); i++ ) {
 					resultList.set(
 							i,
 							transformer.transformTuple( ( Object[] ) resultList.get( i ), aliases )
 					);					
 				}
 
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testAliasToBeanDtoLiteralArgList() throws Exception {
 
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Projections.sqlProjection(
 										"'lame description' as courseDescription",
 										new String[] { "courseDescription" },
 										new Type[] { Hibernate.STRING }
 								)
 						)
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, 'lame description' as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( "lame description", dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( "lame description", dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testAliasToBeanDtoWithNullAliasList() throws Exception {
 
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Enrolment.class, "e" )
 				.createAlias( "e.student", "st" )
 				.createAlias( "e.course", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Property.forName( "st.studentNumber" ) )
 								.add( Property.forName( "co.description" ).as( "courseDescription" ) )
 				)
 				.addOrder( Order.asc( "e.studentNumber" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select st.name as studentName, co.description as courseDescription from Enrolment e join e.student st join e.course co order by e.studentNumber" )
 						.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO dto = ( StudentDTO ) resultList.get( 0 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( yogiExpected.getName(), dto.getName() );
 				dto = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( courseExpected.getDescription(), dto.getDescription() );
 				assertEquals( shermanExpected.getName(), dto.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
-	public void testOneSelectNewList() throws Exception {
+	public void testOneSelectNewNoAliasesList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) throws Exception {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection( Projections.property( "s.name" ) )
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() throws NoSuchMethodException {
 				return StudentDTO.class.getConstructor( PersonName.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new org.hibernate.test.querycache.StudentDTO(s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				StudentDTO yogi = ( StudentDTO ) resultList.get( 0 );
 				assertNull( yogi.getDescription() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				StudentDTO sherman = ( StudentDTO ) resultList.get( 1 );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 				assertNull( sherman.getDescription() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
+	public void testOneSelectNewAliasesList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) throws Exception {
+				return s.createCriteria( Student.class, "s" )
+				.setProjection( Projections.property( "s.name" ).as( "name" ))
+				.addOrder( Order.asc( "s.studentNumber" ) )
+				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
+			}
+			private Constructor getConstructor() throws NoSuchMethodException {
+				return StudentDTO.class.getConstructor( PersonName.class );
+			}
+		};
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select new org.hibernate.test.querycache.StudentDTO(s.name) from Student s order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				StudentDTO yogi = ( StudentDTO ) resultList.get( 0 );
+				assertNull( yogi.getDescription() );
+				assertEquals( yogiExpected.getName(), yogi.getName() );
+				StudentDTO sherman = ( StudentDTO ) resultList.get( 1 );
+				assertEquals( shermanExpected.getName(), sherman.getName() );
+				assertNull( sherman.getDescription() );
+			}
+		};
+		runTest( hqlExecutor, criteriaExecutor, checker, false );
+	}
+
 	public void testMultiSelectNewList() throws Exception{
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) throws Exception {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
-								.add( Property.forName( "s.studentNumber" ) )
-								.add( Property.forName( "s.name" ) )
+								.add( Property.forName( "s.studentNumber" ).as( "studentNumber" ))
+								.add( Property.forName( "s.name" ).as( "name" ))
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() throws NoSuchMethodException {
 				return  Student.class.getConstructor( long.class, PersonName.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new Student(s.studentNumber, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Student yogi = ( Student ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogi.getStudentNumber() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				Student sherman = ( Student ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), sherman.getStudentNumber() );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testMultiSelectNewWithLiteralList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) throws Exception {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
-								.add( Projections.sqlProjection( "555 as sCode", new String[]{ "sCode" }, new Type[] { Hibernate.LONG } ) )
-								.add( Property.forName( "s.name" ) )
+								.add( Projections.sqlProjection( "555 as studentNumber", new String[]{ "studentNumber" }, new Type[] { Hibernate.LONG } ) )
+								.add( Property.forName( "s.name" ).as( "name" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() throws NoSuchMethodException {
 				return Student.class.getConstructor( long.class, PersonName.class );
 			}
 		};
 
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new Student(555L, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Student yogi = ( Student ) resultList.get( 0 );
 				assertEquals( 555L, yogi.getStudentNumber() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				Student sherman = ( Student ) resultList.get( 1 );
 				assertEquals( 555L, sherman.getStudentNumber() );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testMultiSelectNewListList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
-								.add( Property.forName( "s.studentNumber" ) )
-								.add( Property.forName( "s.name" ) )
+								.add( Property.forName( "s.studentNumber" ).as( "studentNumber" ))
+								.add( Property.forName( "s.name" ).as( "name" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( Transformers.TO_LIST );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new list(s.studentNumber, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				List yogiList = ( List ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogiList.get( 0 ) );
 				assertEquals( yogiExpected.getName(), yogiList.get( 1 ) );
 				List shermanList = ( List ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), shermanList.get( 0 ) );
 				assertEquals( shermanExpected.getName(), shermanList.get( 1 ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
-	public void testMultiSelectNewMapList() throws Exception {
+	public void testMultiSelectNewMapUsingAliasesList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				return s.createCriteria( Student.class, "s" )
+				.setProjection(
+						Projections.projectionList()
+								.add( Property.forName( "s.studentNumber" ).as( "sNumber" ) )
+								.add( Property.forName( "s.name" ).as( "sName" ) )
+				)
+				.addOrder( Order.asc( "s.studentNumber" ) )
+				.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+			}
+		};
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select new map(s.studentNumber as sNumber, s.name as sName) from Student s order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Map yogiMap = ( Map ) resultList.get( 0 );
+				assertEquals( yogiExpected.getStudentNumber(), yogiMap.get( "sNumber" ) );
+				assertEquals( yogiExpected.getName(), yogiMap.get( "sName" ) );
+				Map shermanMap = ( Map ) resultList.get( 1 );
+				assertEquals( shermanExpected.getStudentNumber(), shermanMap.get( "sNumber" ) );
+				assertEquals( shermanExpected.getName(), shermanMap.get( "sName" ) );
+			}
+		};
+		runTest( hqlExecutor, criteriaExecutor, checker, false );
+	}
+
+	public void testMultiSelectNewMapUsingAliasesWithFetchJoinList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				return s.createCriteria( Student.class, "s" )
+						.createAlias( "s.preferredCourse", "pc", Criteria.LEFT_JOIN  )
+						.setFetchMode( "enrolments", FetchMode.JOIN )
+						.addOrder( Order.asc( "s.studentNumber" ))
+						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+			}
+		};
+		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select new map(s as s, pc as pc) from Student s left join s.preferredCourse pc left join fetch s.enrolments order by s.studentNumber" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Map yogiMap = ( Map ) resultList.get( 0 );
+				assertEquals( yogiExpected, yogiMap.get( "s" ) );
+				assertEquals( yogiExpected.getPreferredCourse(), yogiMap.get( "pc" ) );
+				Map shermanMap = ( Map ) resultList.get( 1 );
+				assertEquals( shermanExpected, shermanMap.get( "s" ) );
+				assertNull( shermanMap.get( "pc" ) );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
+				}
+			}
+		};
+		runTest( hqlSelectNewMapExecutor, criteriaExecutor, checker, false );
+	}
+
+	public void testMultiSelectAliasToEntityMapUsingAliasesWithFetchJoinList() throws Exception {
+		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
+			protected Criteria getCriteria(Session s) {
+				return s.createCriteria( Student.class, "s" )
+						.createAlias( "s.preferredCourse", "pc", Criteria.LEFT_JOIN  )
+						.setFetchMode( "enrolments", FetchMode.JOIN )
+						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+			}
+		};
+		HqlExecutor hqlAliasToEntityMapExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select s as s, pc as pc from Student s left join s.preferredCourse pc left join fetch s.enrolments order by s.studentNumber" )
+						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				List resultList = ( List ) results;
+				assertEquals( 2, resultList.size() );
+				Map yogiMap = ( Map ) resultList.get( 0 );
+				assertEquals( yogiExpected, yogiMap.get( "s" ) );
+				assertEquals(
+						yogiExpected.getPreferredCourse().getCourseCode(),
+						( ( Course ) yogiMap.get( "pc" ) ).getCourseCode()
+				);
+				Map shermanMap = ( Map ) resultList.get( 1 );
+				assertEquals( shermanExpected, shermanMap.get( "s" ) );
+				assertNull( shermanMap.get( "pc" ) );
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertEquals( yogiExpected.getPreferredCourse(), yogiMap.get( "pc" ) );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiMap.get( "s" ) ).getEnrolments() );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
+					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanMap.get( "s" ) ).getEnrolments() ) );
+				}
+			}
+		};
+		runTest( hqlAliasToEntityMapExecutor, null, checker, false );
+	}
+
+	public void testMultiSelectUsingImplicitJoinWithFetchJoinListHql() throws Exception {
+		HqlExecutor hqlExecutor = new HqlExecutor() {
+			public Query getQuery(Session s) {
+				return s.createQuery( "select s as s, s.preferredCourse as pc from Student s left join fetch s.enrolments" );
+			}
+		};
+		ResultChecker checker = new ResultChecker() {
+			public void check(Object results) {
+				assertTrue( results instanceof Object[] );
+				Object[] yogiObjects = ( Object[] ) results;
+				assertEquals( 2, yogiObjects.length );
+				assertEquals( yogiExpected, yogiObjects[ 0 ] );
+				assertEquals(
+						yogiExpected.getPreferredCourse().getCourseCode(),
+						( ( Course ) yogiObjects[ 1 ] ).getCourseCode()
+				);
+				if ( areDynamicNonLazyAssociationsChecked() ) {
+					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
+					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() ) );
+					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
+				}
+			}
+		};
+		runTest( hqlExecutor, null, checker, true );
+	}
+
+	public void testSelectNewMapUsingAliasesList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "s.studentNumber" ).as( "sNumber" ) )
 								.add( Property.forName( "s.name" ).as( "sName" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new map(s.studentNumber as sNumber, s.name as sName) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogiMap.get( "sNumber" ) );
 				assertEquals( yogiExpected.getName(), yogiMap.get( "sName" ) );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), shermanMap.get( "sNumber" ) );
 				assertEquals( shermanExpected.getName(), shermanMap.get( "sName" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testSelectNewEntityConstructorList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 				.setProjection(
 						Projections.projectionList()
-								.add( Property.forName( "s.studentNumber" ) )
-								.add( Property.forName( "s.name" ) )
+								.add( Property.forName( "s.studentNumber" ).as( "studentNumber" ) )
+								.add( Property.forName( "s.name" ).as( "name" ) )
 				)
 				.addOrder( Order.asc( "s.studentNumber" ) )
 				.setResultTransformer( new AliasToBeanConstructorResultTransformer( getConstructor() ) );
 			}
 			private Constructor getConstructor() {
 				Type studentNametype =
 						( ( SessionFactoryImpl ) getSessions() )
 								.getEntityPersister( Student.class.getName() )
 								.getPropertyType( "name" );
 				return ReflectHelper.getConstructor( Student.class, new Type[] { Hibernate.LONG, studentNametype } );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select new Student(s.studentNumber, s.name) from Student s order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Student yogi = ( Student ) resultList.get( 0 );
 				assertEquals( yogiExpected.getStudentNumber(), yogi.getStudentNumber() );
 				assertEquals( yogiExpected.getName(), yogi.getName() );
 				Student sherman = ( Student ) resultList.get( 1 );
 				assertEquals( shermanExpected.getStudentNumber(), sherman.getStudentNumber() );
 				assertEquals( shermanExpected.getName(), sherman.getName() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testMapKeyList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a" )
 				.setProjection( Projections.property( "a.addressType" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select key(s.addresses) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.contains( "home" ) );
 				assertTrue( resultList.contains( "work" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	public void testMapValueList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a" )
 				.setProjection( Projections.property( "s.addresses" ));
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select value(s.addresses) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "home" ) ) );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "work" ) ) );
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	public void testMapEntryList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Projections.property( "a.addressType" ) )
 								.add( Projections.property( "s.addresses" ).as( "a" ) );
 				)
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select entry(s.addresses) from Student s" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Iterator it=resultList.iterator();
 				assertTrue( resultList.get( 0 ) instanceof Map.Entry );
 				Map.Entry entry = ( Map.Entry ) it.next();
 				if ( "home".equals( entry.getKey() ) ) {
 					assertTrue( yogiExpected.getAddresses().get( "home" ).equals( entry.getValue() ) );
 					entry = ( Map.Entry ) it.next();
 					assertTrue( yogiExpected.getAddresses().get( "work" ).equals( entry.getValue() ) );
 				}
 				else {
 					assertTrue( "work".equals( entry.getKey() ) );
 					assertTrue( yogiExpected.getAddresses().get( "work" ).equals( entry.getValue() ) );
 					entry = ( Map.Entry ) it.next();
 					assertTrue( yogiExpected.getAddresses().get( "home" ).equals( entry.getValue() ) );
 				}
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	public void testMapElementsList() throws Exception {
 		/*
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.INNER_JOIN )
 				.setProjection( Projections.property( "s.addresses" ) );
 			}
 		};
 		*/
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select elements(a) from Student s inner join s.addresses a" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "home" ) ) );
 				assertTrue( resultList.contains( yogiExpected.getAddresses().get( "work" ) ) );
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	protected void runTest(HqlExecutor hqlExecutor, CriteriaExecutor criteriaExecutor, ResultChecker checker, boolean isSingleResult)
 		throws Exception {
 		createData();
-		if ( criteriaExecutor != null ) {
-			runTest( criteriaExecutor, checker, isSingleResult );
+		try {
+			if ( criteriaExecutor != null ) {
+				runTest( criteriaExecutor, checker, isSingleResult );
+			}
+			if ( hqlExecutor != null ) {
+				runTest( hqlExecutor, checker, isSingleResult );
+			}
 		}
-		if ( hqlExecutor != null ) {
-			runTest( hqlExecutor, checker, isSingleResult );
+		finally {
+			deleteData();
 		}
-		deleteData();
 	}
 
 	private boolean isQueryCacheGetEnabled() {
 		return getQueryCacheMode() == CacheMode.NORMAL ||
 			getQueryCacheMode() == CacheMode.GET;
 	}
 
 	private boolean isQueryCachePutEnabled() {
 		return getQueryCacheMode() == CacheMode.NORMAL ||
 			getQueryCacheMode() == CacheMode.PUT;
 	}
 
 	protected void runTest(QueryExecutor queryExecutor, ResultChecker resultChecker, boolean isSingleResult) throws Exception{
 		clearCache();
 		clearStatistics();
 
 		Object results = queryExecutor.execute( isSingleResult );
 
 		assertHitCount( 0 );
 		assertMissCount( isQueryCacheGetEnabled() ? 1 : 0 );
 		assertPutCount( isQueryCachePutEnabled() ? 1 : 0 );
 		clearStatistics();
 
 		resultChecker.check( results );
 
 		// check again to make sure nothing got initialized while checking results;
 		assertHitCount( 0 );
 		assertMissCount( 0 );
 		assertPutCount( 0 );
 		clearStatistics();
 
 		results = queryExecutor.execute( isSingleResult );
 
 		assertHitCount( isQueryCacheGetEnabled() ? 1 : 0 );
 		assertMissCount( 0 );
 		assertPutCount( ! isQueryCacheGetEnabled() && isQueryCachePutEnabled() ? 1 : 0 );
 		clearStatistics();
 
 		resultChecker.check( results );
 
 		// check again to make sure nothing got initialized while checking results;
 		assertHitCount( 0 );
 		assertMissCount( 0 );
 		assertPutCount( 0 );
 		clearStatistics();
 	}
 
 	private void multiPropProjectionNoTransformerDynNonLazy(CacheMode sessionCacheMode,
 																 boolean isCacheableQuery) {
 		Session s = openSession();
 		s.setCacheMode( sessionCacheMode );
 		Transaction t = s.beginTransaction();
 		List resultList = s.createCriteria( Enrolment.class )
 				.setCacheable( isCacheableQuery )
 				.setFetchMode( "student", FetchMode.JOIN )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "student" ), "student" )
 								.add( Property.forName( "semester" ), "semester" )
 								.add( Property.forName( "year" ), "year" )
 								.add( Property.forName( "course" ), "course" )
 				)
 				.addOrder( Order.asc( "studentNumber") )
 				.list();
 		t.commit();
 		s.close();
 
 		assertEquals( 2, resultList.size() );
 		Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 		Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 		assertEquals( 4, yogiObjects.length );
 		assertTrue( yogiObjects[ 0 ] instanceof Student );
 		assertTrue( Hibernate.isInitialized( yogiObjects[ 0 ] ) );
 		assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
 		assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
 		assertEquals( courseExpected, yogiObjects[ 3 ] );
 		assertTrue( shermanObjects[ 0 ] instanceof Student );
 		assertTrue( Hibernate.isInitialized( shermanObjects[ 0 ] ) );
 		assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 		assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 		assertTrue( ! ( shermanObjects[ 3 ] instanceof HibernateProxy ) );
 		assertTrue( shermanObjects[ 3 ] instanceof Course );
 		assertEquals( courseExpected, shermanObjects[ 3 ] );
 	}
 
 /*
 	{
 
 		assertEquals( 2, resultList.size() );
 		Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 		Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 		assertEquals( 4, yogiObjects.length );
 		assertEquals( yogiExpected, ( Student ) yogiObjects[ 0 ] );
 		assertEquals( yogiEnrolmentExpected.getSemester(), ( (Short) yogiObjects[ 1 ] ).shortValue() );
 		assertEquals( yogiEnrolmentExpected.getYear(), ( (Short) yogiObjects[ 2 ] ).shortValue() );
 		assertEquals( courseExpected, yogiObjects[ 3 ] );
 		assertEquals( shermanExpected, ( Student ) shermanObjects[ 0 ] );
 		assertEquals( shermanEnrolmentExpected.getSemester(), ( (Short) shermanObjects[ 1 ] ).shortValue() );
 		assertEquals( shermanEnrolmentExpected.getYear(), ( (Short) shermanObjects[ 2 ] ).shortValue() );
 		assertEquals( courseExpected, shermanObjects[ 3 ] );
 
 	}
 
 */
 /*
 	private void executeProperty() {
 		resultList = s.createCriteria( Student.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Projections.id().as( "studentNumber" ) )
 								.add( Property.forName( "name" ), "name" )
 								.add( Property.forName( "cityState" ), "cityState" )
 								.add( Property.forName( "preferredCourse" ), "preferredCourse" )
 				)
 				.list();
 		assertEquals( 2, resultList.size() );
 		for ( Iterator it = resultList.iterator(); it.hasNext(); ) {
 			Object[] objects = ( Object[] ) it.next();
 			assertEquals( 4, objects.length );
 			assertTrue( objects[0] instanceof Long );
 			assertTrue( objects[1] instanceof String );
 			if ( "yogiExpected King".equals( objects[1] ) ) {
 				assertTrue( objects[2] instanceof Name );
 				assertTrue( objects[3] instanceof Course );
 			}
 			else {
 				assertNull( objects[2] );
 				assertNull( objects[3] );
 			}
 		}
 
 		Object[] aResult = ( Object[] ) s.createCriteria( Student.class )
 				.setCacheable( true )
 				.add( Restrictions.idEq( new Long( 667 ) ) )
 				.setProjection(
 						Projections.projectionList()
 								.add( Projections.id().as( "studentNumber" ) )
 								.add( Property.forName( "name" ), "name" )
 								.add( Property.forName( "cityState" ), "cityState" )
 								.add( Property.forName( "preferredCourse" ), "preferredCourse" )
 				)
 				.uniqueResult();
 		assertNotNull( aResult );
 		assertEquals( 4, aResult.length );
 		assertTrue( aResult[0] instanceof Long );
 		assertTrue( aResult[1] instanceof String );
 		assertTrue( aResult[2] instanceof Name );
 		assertTrue( aResult[3] instanceof Course );
 
 		Long count = ( Long ) s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection( Property.forName( "studentNumber" ).count().setDistinct() )
 				.uniqueResult();
 		assertEquals( count, new Long( 2 ) );
 
 		Object object = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "studentNumber" ).count() )
 								.add( Property.forName( "studentNumber" ).max() )
 								.add( Property.forName( "studentNumber" ).min() )
 								.add( Property.forName( "studentNumber" ).avg() )
 				)
 				.uniqueResult();
 		Object[] result = ( Object[] ) object;
 
 		assertEquals( new Long( 2 ), result[0] );
 		assertEquals( new Long( 667 ), result[1] );
 		assertEquals( new Long( 101 ), result[2] );
 		assertEquals( 384.0, ( ( Double ) result[3] ).doubleValue(), 0.01 );
 
 
 		s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.add( Property.forName( "studentNumber" ).gt( new Long( 665 ) ) )
 				.add( Property.forName( "studentNumber" ).lt( new Long( 668 ) ) )
 				.add( Property.forName( "courseCode" ).like( "HIB", MatchMode.START ) )
 				.add( Property.forName( "year" ).eq( new Short( ( short ) 1999 ) ) )
 				.addOrder( Property.forName( "studentNumber" ).asc() )
 				.uniqueResult();
 
 		List resultWithMaps = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "studentNumber" ).as( "stNumber" ) )
 								.add( Property.forName( "courseCode" ).as( "cCode" ) )
 				)
 				.add( Property.forName( "studentNumber" ).gt( new Long( 665 ) ) )
 				.add( Property.forName( "studentNumber" ).lt( new Long( 668 ) ) )
 				.addOrder( Property.forName( "studentNumber" ).asc() )
 				.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
 				.list();
 
 		assertEquals( 1, resultWithMaps.size() );
 		Map m1 = ( Map ) resultWithMaps.get( 0 );
 
 		assertEquals( new Long( 667 ), m1.get( "stNumber" ) );
 		assertEquals( courseExpected.getCourseCode(), m1.get( "cCode" ) );
 
 		resultWithMaps = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection( Property.forName( "studentNumber" ).as( "stNumber" ) )
 				.addOrder( Order.desc( "stNumber" ) )
 				.setResultTransformer( Criteria.ALIAS_TO_ENTITY_MAP )
 				.list();
 
 		assertEquals( 2, resultWithMaps.size() );
 		Map m0 = ( Map ) resultWithMaps.get( 0 );
 		m1 = ( Map ) resultWithMaps.get( 1 );
 
 		assertEquals( new Long( 101 ), m1.get( "stNumber" ) );
 		assertEquals( new Long( 667 ), m0.get( "stNumber" ) );
 
 		List resultWithAliasedBean = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.createAlias( "student", "st" )
 				.createAlias( "courseExpected", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "st.name" ).as( "studentName" ) )
 								.add( Property.forName( "co.description" ).as( "courseDescription" ) )
 				)
 				.addOrder( Order.desc( "studentName" ) )
 				.setResultTransformer( Transformers.aliasToBean( StudentDTO.class ) )
 				.list();
 
 		assertEquals( 2, resultWithAliasedBean.size() );
 
 		StudentDTO dto = ( StudentDTO ) resultWithAliasedBean.get( 0 );
 		assertNotNull( dto.getDescription() );
 		assertNotNull( dto.getName() );
 
 		CourseMeeting courseMeetingDto = ( CourseMeeting ) s.createCriteria( CourseMeeting.class )
 				.setCacheable( true )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "id" ).as( "id" ) )
 								.add( Property.forName( "courseExpected" ).as( "courseExpected" ) )
 				)
 				.addOrder( Order.desc( "id" ) )
 				.setResultTransformer( Transformers.aliasToBean( CourseMeeting.class ) )
 				.uniqueResult();
 
 		assertNotNull( courseMeetingDto.getId() );
 		assertEquals( courseExpected.getCourseCode(), courseMeetingDto.getId().getCourseCode() );
 		assertEquals( "Monday", courseMeetingDto.getId().getDay() );
 		assertEquals( "1313 Mockingbird Lane", courseMeetingDto.getId().getLocation() );
 		assertEquals( 1, courseMeetingDto.getId().getPeriod() );
 		assertEquals( courseExpected.getDescription(), courseMeetingDto.getCourse().getDescription() );
 
 		s.createCriteria( Student.class )
 				.setCacheable( true )
 				.add( Restrictions.like( "name", "yogiExpected", MatchMode.START ) )
 				.addOrder( Order.asc( "name" ) )
 				.createCriteria( "enrolments", "e" )
 				.addOrder( Order.desc( "year" ) )
 				.addOrder( Order.desc( "semester" ) )
 				.createCriteria( "courseExpected", "c" )
 				.addOrder( Order.asc( "description" ) )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "this.name" ) )
 								.add( Property.forName( "e.year" ) )
 								.add( Property.forName( "e.semester" ) )
 								.add( Property.forName( "c.courseCode" ) )
 								.add( Property.forName( "c.description" ) )
 				)
 				.uniqueResult();
 
 		Projection p1 = Projections.projectionList()
 				.add( Property.forName( "studentNumber" ).count() )
 				.add( Property.forName( "studentNumber" ).max() )
 				.add( Projections.rowCount() );
 
 		Projection p2 = Projections.projectionList()
 				.add( Property.forName( "studentNumber" ).min() )
 				.add( Property.forName( "studentNumber" ).avg() )
 				.add(
 						Projections.sqlProjection(
 								"1 as constOne, count(*) as countStar",
 								new String[] { "constOne", "countStar" },
 								new Type[] { Hibernate.INTEGER, Hibernate.INTEGER }
 						)
 				);
 
 		Object[] array = ( Object[] ) s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.setProjection( Projections.projectionList().add( p1 ).add( p2 ) )
 				.uniqueResult();
 
 		assertEquals( array.length, 7 );
 
 		List list = s.createCriteria( Enrolment.class )
 				.setCacheable( true )
 				.createAlias( "student", "st" )
 				.createAlias( "courseExpected", "co" )
 				.setProjection(
 						Projections.projectionList()
 								.add( Property.forName( "co.courseCode" ).group() )
 								.add( Property.forName( "st.studentNumber" ).count().setDistinct() )
 								.add( Property.forName( "year" ).group() )
 				)
 				.list();
 
 		assertEquals( list.size(), 2 );
 	}
 */
 	protected void clearCache() {
 		getSessions().getCache().evictQueryRegions();
 	}
 
 	protected void clearStatistics() {
 		getSessions().getStatistics().clear();
 	}
 
 	protected void assertEntityFetchCount(int expected) {
 		int actual = ( int ) getSessions().getStatistics().getEntityFetchCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertCount(int expected) {
 		int actual = ( int ) getSessions().getStatistics().getQueries().length;
 		assertEquals( expected, actual );
 	}
 
 	protected void assertHitCount(int expected) {
 		int actual = ( int ) getSessions().getStatistics().getQueryCacheHitCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertMissCount(int expected) {
 		int actual = ( int ) getSessions().getStatistics().getQueryCacheMissCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertPutCount(int expected) {
 		int actual = ( int ) getSessions().getStatistics().getQueryCachePutCount();
 		assertEquals( expected, actual );
 	}
 
 	protected void assertInsertCount(int expected) {
 		int inserts = ( int ) getSessions().getStatistics().getEntityInsertCount();
 		assertEquals( "unexpected insert count", expected, inserts );
 	}
 
 	protected void assertUpdateCount(int expected) {
 		int updates = ( int ) getSessions().getStatistics().getEntityUpdateCount();
 		assertEquals( "unexpected update counts", expected, updates );
 	}
 
 	protected void assertDeleteCount(int expected) {
 		int deletes = ( int ) getSessions().getStatistics().getEntityDeleteCount();
 		assertEquals( "unexpected delete counts", expected, deletes );
 	}
 }
diff --git a/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCacheIgnoreResultTransformerTest.java b/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCacheIgnoreResultTransformerTest.java
index 3ae4cf55a9..691193bf64 100644
--- a/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCacheIgnoreResultTransformerTest.java
+++ b/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCacheIgnoreResultTransformerTest.java
@@ -1,57 +1,61 @@
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
 package org.hibernate.test.querycache;
 
 import junit.framework.Test;
 
 import org.hibernate.CacheMode;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 
 /**
  * @author Gail Badner
  */
 public class CriteriaQueryCacheIgnoreResultTransformerTest extends AbstractQueryCacheResultTransformerTest {
 
 	public CriteriaQueryCacheIgnoreResultTransformerTest(String str) {
 		super( str );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( CriteriaQueryCacheIgnoreResultTransformerTest.class );
 	}
 
 	protected CacheMode getQueryCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
 	protected void runTest(HqlExecutor hqlExecutor, CriteriaExecutor criteriaExecutor, ResultChecker checker, boolean isSingleResult)
 		throws Exception {
 		createData();
-		if ( criteriaExecutor != null ) {
-			runTest( criteriaExecutor, checker, isSingleResult );
+		try {
+			if ( criteriaExecutor != null ) {
+				runTest( criteriaExecutor, checker, isSingleResult );
+			}
+		}
+		finally {
+			deleteData();
 		}
-		deleteData();
 	}
 }
diff --git a/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCachePutResultTransformerTest.java b/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCachePutResultTransformerTest.java
index 2e928e5864..cdb7da2348 100644
--- a/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCachePutResultTransformerTest.java
+++ b/testsuite/src/test/java/org/hibernate/test/querycache/CriteriaQueryCachePutResultTransformerTest.java
@@ -1,181 +1,52 @@
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
 package org.hibernate.test.querycache;
 
 import junit.framework.Test;
 
 import org.hibernate.CacheMode;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 
 /**
  * @author Gail Badner
  */
 public class CriteriaQueryCachePutResultTransformerTest extends CriteriaQueryCacheIgnoreResultTransformerTest {
 
 	public CriteriaQueryCachePutResultTransformerTest(String str) {
 		super( str );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( CriteriaQueryCachePutResultTransformerTest.class );
 	}
 
 	protected CacheMode getQueryCacheMode() {
 		return CacheMode.PUT;
 	}
 
 	protected boolean areDynamicNonLazyAssociationsChecked() {
 		return false;
-	}	
-
-	public void testAliasToEntityMapNoProjectionList() {
-		reportSkip( "Transformers.ALIAS_TO_ENTITY_MAP with Criteria fails when try put in cache",
-				"Cache results using Transformers.ALIAS_TO_ENTITY_MAP with Criteria" );
-	}
-
-	public void testAliasToEntityMapNoProjectionListFailureExpected() throws Exception {
-		super.testAliasToEntityMapNoProjectionList();
-	}
-
-	public void testAliasToEntityMapNoProjectionMultiAndNullList() {
-		reportSkip( "Transformers.ALIAS_TO_ENTITY_MAP with Criteria fails when try put in cache",
-				"Cache results using Transformers.ALIAS_TO_ENTITY_MAP with Criteria" );
-	}
-	public void testAliasToEntityMapNoProjectionMultiAndNullListFailureExpected() throws Exception {
-		super.testAliasToEntityMapNoProjectionMultiAndNullList();
-	}
-
-	public void testAliasToEntityMapOneProjectionList() {
-		reportSkip( "Transformers.ALIAS_TO_ENTITY_MAP with Criteria fails when try put in cache",
-				"Cache results using Transformers.ALIAS_TO_ENTITY_MAP with Criteria" );
-	}
-	public void testAliasToEntityMapOneProjectionListFailureExpected() throws Exception {
-		super.testAliasToEntityMapOneProjectionList();
-	}
-
-	public void testAliasToEntityMapMultiProjectionList() {
-		reportSkip( "Transformers.ALIAS_TO_ENTITY_MAP with Criteria fails when try put in cache",
-				"Cache results using Transformers.ALIAS_TO_ENTITY_MAP with Criteria" );
-	}
-	public void testAliasToEntityMapMultiProjectionListFailureExpected() throws Exception {
-		super.testAliasToEntityMapMultiProjectionList();
-	}
-
-	public void testAliasToEntityMapMultiProjectionWithNullAliasList() {
-		reportSkip( "Transformers.ALIAS_TO_ENTITY_MAP with Criteria fails when try put in cache",
-				"Cache results using Transformers.ALIAS_TO_ENTITY_MAP with Criteria" );
-	}
-	public void testAliasToEntityMapMultiProjectionWithNullAliasListFailureExpected() throws Exception {
-		super.testAliasToEntityMapMultiProjectionWithNullAliasList();
-	}
-
-	public void testAliasToEntityMapMultiAggregatedPropProjectionSingleResult() {
-		reportSkip( "Transformers.ALIAS_TO_ENTITY_MAP with Criteria fails when try put in cache",
-				"Cache results using Transformers.ALIAS_TO_ENTITY_MAP with Criteria" );
-	}
-	public void testAliasToEntityMapMultiAggregatedPropProjectionSingleResultFailureExpected() throws Exception {
-		super.testAliasToEntityMapMultiAggregatedPropProjectionSingleResult();
-	}
-
-	public void testAliasToBeanDtoOneArgList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testAliasToBeanDtoOneArgListFailureExpected() throws Exception {
-		super.testAliasToBeanDtoOneArgList();
-	}
-
-	public void testAliasToBeanDtoMultiArgList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testAliasToBeanDtoMultiArgListFailureExpected() throws Exception {
-		super.testAliasToBeanDtoMultiArgList();
-	}
-
-	public void testAliasToBeanDtoLiteralArgList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testAliasToBeanDtoLiteralArgListFailureExpected() throws Exception {
-		super.testAliasToBeanDtoLiteralArgList();
-	}
-
-	public void testAliasToBeanDtoWithNullAliasList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testAliasToBeanDtoWithNullAliasListFailureExpected() throws Exception {
-		super.testAliasToBeanDtoWithNullAliasList();
-	}
-
-	public void testOneSelectNewList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testOneSelectNewListFailureExpected() throws Exception {
-		super.testOneSelectNewList();
-	}
-
-	public void testMultiSelectNewList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testMultiSelectNewListFailureExpected() throws Exception {
-		super.testMultiSelectNewList();
-	}
-
-	public void testMultiSelectNewWithLiteralList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testMultiSelectNewWithLiteralListFailureExpected() throws Exception {
-		super.testMultiSelectNewWithLiteralList();
-	}
-
-	public void testMultiSelectNewListList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testMultiSelectNewListListFailureExpected() throws Exception {
-		super.testMultiSelectNewListList();
-	}
-
-	public void testMultiSelectNewMapList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testMultiSelectNewMapListFailureExpected() throws Exception {
-		super.testMultiSelectNewMapList();
-	}
-
-	public void testSelectNewEntityConstructorList() {
-		reportSkip( "Transformers.aliasToBean with Criteria fails when try put in cache",
-				"Cache results using Transformers.aliasToBean with Criteria" );
-	}
-	public void testSelectNewEntityConstructorListFailureExpected() throws Exception {
-		super.testMultiSelectNewMapList();
 	}
 }
diff --git a/testsuite/src/test/java/org/hibernate/test/querycache/HqlQueryCacheIgnoreResultTransformerTest.java b/testsuite/src/test/java/org/hibernate/test/querycache/HqlQueryCacheIgnoreResultTransformerTest.java
index a61cb896a6..adb3987baa 100644
--- a/testsuite/src/test/java/org/hibernate/test/querycache/HqlQueryCacheIgnoreResultTransformerTest.java
+++ b/testsuite/src/test/java/org/hibernate/test/querycache/HqlQueryCacheIgnoreResultTransformerTest.java
@@ -1,73 +1,87 @@
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
 package org.hibernate.test.querycache;
 
 import junit.framework.Test;
 
 import org.hibernate.CacheMode;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 
 /**
  * @author Gail Badner
  */
 public class HqlQueryCacheIgnoreResultTransformerTest extends AbstractQueryCacheResultTransformerTest {
 
 	public HqlQueryCacheIgnoreResultTransformerTest(String str) {
 		super( str );
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( HqlQueryCacheIgnoreResultTransformerTest.class );
 	}
 
 	protected CacheMode getQueryCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
 	protected void runTest(HqlExecutor hqlExecutor, CriteriaExecutor criteriaExecutor, ResultChecker checker, boolean isSingleResult)
 		throws Exception {
 		createData();
 		if ( hqlExecutor != null ) {
 			runTest( hqlExecutor, checker, isSingleResult );
 		}
 		deleteData();
 	}
 
 	public void testAliasToEntityMapNoProjectionList() throws Exception {
 		reportSkip( "known to fail using HQL", "HQL query using Transformers.ALIAS_TO_ENTITY_MAP with no projection" );
 	}
-
 	public void testAliasToEntityMapNoProjectionListFailureExpected() throws Exception {
 		super.testAliasToEntityMapNoProjectionList();
 	}
 
 	public void testAliasToEntityMapNoProjectionMultiAndNullList() throws Exception {
 		reportSkip( "known to fail using HQL", "HQL query using Transformers.ALIAS_TO_ENTITY_MAP with no projection" );
 	}
-
 	public void testAliasToEntityMapNoProjectionMultiAndNullListFailureExpected() throws Exception {
 		super.testAliasToEntityMapNoProjectionMultiAndNullList();
 	}
+
+	public void testAliasToEntityMapNoProjectionNullAndNonNullAliasList() throws Exception {
+		reportSkip( "known to fail using HQL", "HQL query using Transformers.ALIAS_TO_ENTITY_MAP with no projection" );
+	}
+	public void testAliasToEntityMapNoProjectionNullAndNonNullAliasListFailureExpected() throws Exception {
+		super.testAliasToEntityMapNoProjectionNullAndNonNullAliasList();
+	}
+
+	// fails due to HHH-3345
+	public void testMultiSelectNewMapUsingAliasesWithFetchJoinList() throws Exception {
+		reportSkip( "known to fail using HQL", "HQL query using 'select new' and 'join fetch'" );
+	}
+	public void testMultiSelectNewMapUsingAliasesWithFetchJoinListFailureExpected() throws Exception {
+		super.testMultiSelectNewMapUsingAliasesWithFetchJoinList();
+	}
+
 }
