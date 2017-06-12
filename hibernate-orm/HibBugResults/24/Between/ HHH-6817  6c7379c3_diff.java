diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
index 8bc18d6f0c..94d8da3be0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
@@ -1,282 +1,282 @@
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
 package org.hibernate.cache.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 import java.util.Set;
 import javax.persistence.EntityNotFoundException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
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
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			StandardQueryCache.class.getName()
 	);
 
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
 		LOG.startingQueryCache( regionName );
 
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
 		Long ts = session.getFactory().getSettings().getRegionFactory().nextTimestamp();
 
 		LOG.debugf( "Caching query results in region: %s; timestamp=%s", cacheRegion.getName(), ts );
 
 		List cacheable = new ArrayList( result.size() + 1 );
 		logCachedResultDetails( key, null, returnTypes, cacheable );
 		cacheable.add( ts );
 		for ( Object aResult : result ) {
 			if ( returnTypes.length == 1 ) {
 				cacheable.add( returnTypes[0].disassemble( aResult, session, null ) );
 			}
 			else {
 				cacheable.add( TypeHelper.disassemble( (Object[]) aResult, returnTypes, null, session, null ) );
 			}
 			logCachedResultRowDetails( returnTypes, aResult );
 		}
 
 		cacheRegion.put( key, cacheable );
 		return true;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public List get(
 			QueryKey key,
 			Type[] returnTypes,
 			boolean isNaturalKeyLookup,
 			Set spaces,
 			SessionImplementor session) throws HibernateException {
 		LOG.debugf( "Checking cached query results in region: %s", cacheRegion.getName() );
 
 		List cacheable = (List) cacheRegion.get( key );
 		logCachedResultDetails( key, spaces, returnTypes, cacheable );
 
 		if ( cacheable == null ) {
-			LOG.debugf( "Query results were not found in cache" );
+			LOG.debug( "Query results were not found in cache" );
 			return null;
 		}
 
 		Long timestamp = (Long) cacheable.get( 0 );
 		if ( !isNaturalKeyLookup && !isUpToDate( spaces, timestamp ) ) {
-			LOG.debugf( "Cached query results were not up-to-date" );
+			LOG.debug( "Cached query results were not up-to-date" );
 			return null;
 		}
 
-		LOG.debugf( "Returning cached query results" );
+		LOG.debug( "Returning cached query results" );
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			if ( returnTypes.length == 1 ) {
 				returnTypes[0].beforeAssemble( (Serializable) cacheable.get( i ), session );
 			}
 			else {
 				TypeHelper.beforeAssemble( (Serializable[]) cacheable.get( i ), returnTypes, session );
 			}
 		}
 		List result = new ArrayList( cacheable.size() - 1 );
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			try {
 				if ( returnTypes.length == 1 ) {
 					result.add( returnTypes[0].assemble( (Serializable) cacheable.get( i ), session, null ) );
 				}
 				else {
 					result.add(
 							TypeHelper.assemble( (Serializable[]) cacheable.get( i ), returnTypes, session, null )
 					);
 				}
 				logCachedResultRowDetails( returnTypes, result.get( i - 1 ) );
 			}
 			catch ( RuntimeException ex ) {
 				if ( isNaturalKeyLookup &&
 						( UnresolvableObjectException.class.isInstance( ex ) ||
 								EntityNotFoundException.class.isInstance( ex ) ) ) {
 					//TODO: not really completely correct, since
 					//      the uoe could occur while resolving
 					//      associations, leaving the PC in an
 					//      inconsistent state
-					LOG.debugf( "Unable to reassemble cached result set" );
+					LOG.debug( "Unable to reassemble cached result set" );
 					cacheRegion.evict( key );
 					return null;
 				}
 				throw ex;
 			}
 		}
 		return result;
 	}
 
 	protected boolean isUpToDate(Set spaces, Long timestamp) {
 		LOG.debugf( "Checking query spaces are up-to-date: %s", spaces );
 		return updateTimestampsCache.isUpToDate( spaces, timestamp );
 	}
 
 	public void destroy() {
 		try {
 			cacheRegion.destroy();
 		}
 		catch ( Exception e ) {
 			LOG.unableToDestroyQueryCache( cacheRegion.getName(), e.getMessage() );
 		}
 	}
 
 	public QueryResultsRegion getRegion() {
 		return cacheRegion;
 	}
 
 	@Override
 	public String toString() {
 		return "StandardQueryCache(" + cacheRegion.getName() + ')';
 	}
 
 	private static void logCachedResultDetails(QueryKey key, Set querySpaces, Type[] returnTypes, List result) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		LOG.trace( "key.hashCode=" + key.hashCode() );
 		LOG.trace( "querySpaces=" + querySpaces );
 		if ( returnTypes == null || returnTypes.length == 0 ) {
 			LOG.trace(
 					"Unexpected returnTypes is "
 							+ ( returnTypes == null ? "null" : "empty" ) + "! result"
 							+ ( result == null ? " is null" : ".size()=" + result.size() )
 			);
 		}
 		else {
 			StringBuilder returnTypeInfo = new StringBuilder();
 			for ( int i = 0; i < returnTypes.length; i++ ) {
 				returnTypeInfo.append( "typename=" )
 						.append( returnTypes[i].getName() )
 						.append( " class=" )
 						.append( returnTypes[i].getReturnedClass().getName() ).append( ' ' );
 			}
 			LOG.trace( "unexpected returnTypes is " + returnTypeInfo.toString() + "! result" );
 		}
 	}
 
 	private static void logCachedResultRowDetails(Type[] returnTypes, Object result) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		logCachedResultRowDetails(
 				returnTypes,
 				( result instanceof Object[] ? (Object[]) result : new Object[] { result } )
 		);
 	}
 
 	private static void logCachedResultRowDetails(Type[] returnTypes, Object[] tuple) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( tuple == null ) {
 			LOG.trace( " tuple is null; returnTypes is " + returnTypes == null ? "null" : "Type[" + returnTypes.length + "]" );
 			if ( returnTypes != null && returnTypes.length > 1 ) {
 				LOG.trace(
 						"Unexpected result tuple! tuple is null; should be Object["
 								+ returnTypes.length + "]!"
 				);
 			}
 		}
 		else {
 			if ( returnTypes == null || returnTypes.length == 0 ) {
 				LOG.trace(
 						"Unexpected result tuple! tuple is null; returnTypes is "
 								+ ( returnTypes == null ? "null" : "empty" )
 				);
 			}
 			LOG.trace( " tuple is Object[" + tuple.length + "]; returnTypes is Type[" + returnTypes.length + "]" );
 			if ( tuple.length != returnTypes.length ) {
 				LOG.trace(
 						"Unexpected tuple length! transformer= expected="
 								+ returnTypes.length + " got=" + tuple.length
 				);
 			}
 			else {
 				for ( int j = 0; j < tuple.length; j++ ) {
 					if ( tuple[j] != null && !returnTypes[j].getReturnedClass().isInstance( tuple[j] ) ) {
 						LOG.trace(
 								"Unexpected tuple value type! transformer= expected="
 										+ returnTypes[j].getReturnedClass().getName()
 										+ " got="
 										+ tuple[j].getClass().getName()
 						);
 					}
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 6d2a4e20b4..e4dc5098c0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1,1988 +1,1988 @@
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
 package org.hibernate.cfg;
 import java.lang.annotation.Annotation;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.EnumSet;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import javax.persistence.Basic;
 import javax.persistence.Cacheable;
 import javax.persistence.CollectionTable;
 import javax.persistence.Column;
 import javax.persistence.DiscriminatorType;
 import javax.persistence.DiscriminatorValue;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.GeneratedValue;
 import javax.persistence.GenerationType;
 import javax.persistence.Id;
 import javax.persistence.IdClass;
 import javax.persistence.InheritanceType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.ManyToOne;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.MapKeyJoinColumn;
 import javax.persistence.MapKeyJoinColumns;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.MapsId;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 import javax.persistence.OneToMany;
 import javax.persistence.OneToOne;
 import javax.persistence.OrderColumn;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.PrimaryKeyJoinColumns;
 import javax.persistence.SequenceGenerator;
 import javax.persistence.SharedCacheMode;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
 import javax.persistence.Table;
 import javax.persistence.TableGenerator;
 import javax.persistence.UniqueConstraint;
 import javax.persistence.Version;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.Cascade;
 import org.hibernate.annotations.CascadeType;
 import org.hibernate.annotations.Check;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.Columns;
 import org.hibernate.annotations.DiscriminatorOptions;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterDefs;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Formula;
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.annotations.GenericGenerators;
 import org.hibernate.annotations.Index;
 import org.hibernate.annotations.LazyToOne;
 import org.hibernate.annotations.LazyToOneOption;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.NaturalId;
 import org.hibernate.annotations.NotFound;
 import org.hibernate.annotations.NotFoundAction;
 import org.hibernate.annotations.OnDelete;
 import org.hibernate.annotations.OnDeleteAction;
 import org.hibernate.annotations.OrderBy;
 import org.hibernate.annotations.ParamDef;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Parent;
 import org.hibernate.annotations.Proxy;
 import org.hibernate.annotations.Sort;
 import org.hibernate.annotations.Source;
 import org.hibernate.annotations.Tuplizer;
 import org.hibernate.annotations.Tuplizers;
 import org.hibernate.annotations.TypeDef;
 import org.hibernate.annotations.TypeDefs;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XMethod;
 import org.hibernate.annotations.common.reflection.XPackage;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.annotations.CollectionBinder;
 import org.hibernate.cfg.annotations.EntityBinder;
 import org.hibernate.cfg.annotations.MapKeyColumnDelegator;
 import org.hibernate.cfg.annotations.MapKeyJoinColumnDelegator;
 import org.hibernate.cfg.annotations.Nullability;
 import org.hibernate.cfg.annotations.PropertyBinder;
 import org.hibernate.cfg.annotations.QueryBinder;
 import org.hibernate.cfg.annotations.SimpleValueBinder;
 import org.hibernate.cfg.annotations.TableBinder;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.UnionSubclass;
 
 /**
  * JSR 175 annotation binder which reads the annotations from classes, applies the
  * principles of the EJB3 spec and produces the Hibernate configuration-time metamodel
  * (the classes in the {@code org.hibernate.mapping} package)
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public final class AnnotationBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AnnotationBinder.class.getName() );
 
     /*
      * Some design description
      * I tried to remove any link to annotation except from the 2 first level of
      * method call.
      * It'll enable to:
      *   - facilitate annotation overriding
      *   - mutualize one day xml and annotation binder (probably a dream though)
      *   - split this huge class in smaller mapping oriented classes
      *
      * bindSomething usually create the mapping container and is accessed by one of the 2 first level method
      * makeSomething usually create the mapping container and is accessed by bindSomething[else]
      * fillSomething take the container into parameter and fill it.
      */
 
 	private AnnotationBinder() {
 	}
 
 	public static void bindDefaults(Mappings mappings) {
 		Map defaults = mappings.getReflectionManager().getDefaults();
 		{
 			List<SequenceGenerator> anns = ( List<SequenceGenerator> ) defaults.get( SequenceGenerator.class );
 			if ( anns != null ) {
 				for ( SequenceGenerator ann : anns ) {
 					IdGenerator idGen = buildIdGenerator( ann, mappings );
 					if ( idGen != null ) {
 						mappings.addDefaultGenerator( idGen );
 					}
 				}
 			}
 		}
 		{
 			List<TableGenerator> anns = ( List<TableGenerator> ) defaults.get( TableGenerator.class );
 			if ( anns != null ) {
 				for ( TableGenerator ann : anns ) {
 					IdGenerator idGen = buildIdGenerator( ann, mappings );
 					if ( idGen != null ) {
 						mappings.addDefaultGenerator( idGen );
 					}
 				}
 			}
 		}
 		{
 			List<NamedQuery> anns = ( List<NamedQuery> ) defaults.get( NamedQuery.class );
 			if ( anns != null ) {
 				for ( NamedQuery ann : anns ) {
 					QueryBinder.bindQuery( ann, mappings, true );
 				}
 			}
 		}
 		{
 			List<NamedNativeQuery> anns = ( List<NamedNativeQuery> ) defaults.get( NamedNativeQuery.class );
 			if ( anns != null ) {
 				for ( NamedNativeQuery ann : anns ) {
 					QueryBinder.bindNativeQuery( ann, mappings, true );
 				}
 			}
 		}
 		{
 			List<SqlResultSetMapping> anns = ( List<SqlResultSetMapping> ) defaults.get( SqlResultSetMapping.class );
 			if ( anns != null ) {
 				for ( SqlResultSetMapping ann : anns ) {
 					QueryBinder.bindSqlResultsetMapping( ann, mappings, true );
 				}
 			}
 		}
 	}
 
 	public static void bindPackage(String packageName, Mappings mappings) {
 		XPackage pckg;
 		try {
 			pckg = mappings.getReflectionManager().packageForName( packageName );
 		}
 		catch ( ClassNotFoundException cnf ) {
 			LOG.packageNotFound( packageName );
 			return;
 		}
 		if ( pckg.isAnnotationPresent( SequenceGenerator.class ) ) {
 			SequenceGenerator ann = pckg.getAnnotation( SequenceGenerator.class );
 			IdGenerator idGen = buildIdGenerator( ann, mappings );
 			mappings.addGenerator( idGen );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add sequence generator with name: {0}", idGen.getName() );
 			}
 		}
 		if ( pckg.isAnnotationPresent( TableGenerator.class ) ) {
 			TableGenerator ann = pckg.getAnnotation( TableGenerator.class );
 			IdGenerator idGen = buildIdGenerator( ann, mappings );
 			mappings.addGenerator( idGen );
 
 		}
 		bindGenericGenerators( pckg, mappings );
 		bindQueries( pckg, mappings );
 		bindFilterDefs( pckg, mappings );
 		bindTypeDefs( pckg, mappings );
 		bindFetchProfiles( pckg, mappings );
 		BinderHelper.bindAnyMetaDefs( pckg, mappings );
 	}
 
 	private static void bindGenericGenerators(XAnnotatedElement annotatedElement, Mappings mappings) {
 		GenericGenerator defAnn = annotatedElement.getAnnotation( GenericGenerator.class );
 		GenericGenerators defsAnn = annotatedElement.getAnnotation( GenericGenerators.class );
 		if ( defAnn != null ) {
 			bindGenericGenerator( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( GenericGenerator def : defsAnn.value() ) {
 				bindGenericGenerator( def, mappings );
 			}
 		}
 	}
 
 	private static void bindGenericGenerator(GenericGenerator def, Mappings mappings) {
 		IdGenerator idGen = buildIdGenerator( def, mappings );
 		mappings.addGenerator( idGen );
 	}
 
 	private static void bindQueries(XAnnotatedElement annotatedElement, Mappings mappings) {
 		{
 			SqlResultSetMapping ann = annotatedElement.getAnnotation( SqlResultSetMapping.class );
 			QueryBinder.bindSqlResultsetMapping( ann, mappings, false );
 		}
 		{
 			SqlResultSetMappings ann = annotatedElement.getAnnotation( SqlResultSetMappings.class );
 			if ( ann != null ) {
 				for ( SqlResultSetMapping current : ann.value() ) {
 					QueryBinder.bindSqlResultsetMapping( current, mappings, false );
 				}
 			}
 		}
 		{
 			NamedQuery ann = annotatedElement.getAnnotation( NamedQuery.class );
 			QueryBinder.bindQuery( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedQuery ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedQuery.class
 			);
 			QueryBinder.bindQuery( ann, mappings );
 		}
 		{
 			NamedQueries ann = annotatedElement.getAnnotation( NamedQueries.class );
 			QueryBinder.bindQueries( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedQueries ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedQueries.class
 			);
 			QueryBinder.bindQueries( ann, mappings );
 		}
 		{
 			NamedNativeQuery ann = annotatedElement.getAnnotation( NamedNativeQuery.class );
 			QueryBinder.bindNativeQuery( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedNativeQuery ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedNativeQuery.class
 			);
 			QueryBinder.bindNativeQuery( ann, mappings );
 		}
 		{
 			NamedNativeQueries ann = annotatedElement.getAnnotation( NamedNativeQueries.class );
 			QueryBinder.bindNativeQueries( ann, mappings, false );
 		}
 		{
 			org.hibernate.annotations.NamedNativeQueries ann = annotatedElement.getAnnotation(
 					org.hibernate.annotations.NamedNativeQueries.class
 			);
 			QueryBinder.bindNativeQueries( ann, mappings );
 		}
 	}
 
 	private static IdGenerator buildIdGenerator(java.lang.annotation.Annotation ann, Mappings mappings) {
 		IdGenerator idGen = new IdGenerator();
 		if ( mappings.getSchemaName() != null ) {
 			idGen.addParam( PersistentIdentifierGenerator.SCHEMA, mappings.getSchemaName() );
 		}
 		if ( mappings.getCatalogName() != null ) {
 			idGen.addParam( PersistentIdentifierGenerator.CATALOG, mappings.getCatalogName() );
 		}
 		final boolean useNewGeneratorMappings = mappings.useNewGeneratorMappings();
 		if ( ann == null ) {
 			idGen = null;
 		}
 		else if ( ann instanceof TableGenerator ) {
 			TableGenerator tabGen = ( TableGenerator ) ann;
 			idGen.setName( tabGen.name() );
 			if ( useNewGeneratorMappings ) {
 				idGen.setIdentifierGeneratorStrategy( org.hibernate.id.enhanced.TableGenerator.class.getName() );
 				idGen.addParam( org.hibernate.id.enhanced.TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, "true" );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.catalog() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.CATALOG, tabGen.catalog() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.schema() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.SCHEMA, tabGen.schema() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.table() ) ) {
 					idGen.addParam( org.hibernate.id.enhanced.TableGenerator.TABLE_PARAM, tabGen.table() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnName() ) ) {
 					idGen.addParam(
 							org.hibernate.id.enhanced.TableGenerator.SEGMENT_COLUMN_PARAM, tabGen.pkColumnName()
 					);
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnValue() ) ) {
 					idGen.addParam(
 							org.hibernate.id.enhanced.TableGenerator.SEGMENT_VALUE_PARAM, tabGen.pkColumnValue()
 					);
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.valueColumnName() ) ) {
 					idGen.addParam(
 							org.hibernate.id.enhanced.TableGenerator.VALUE_COLUMN_PARAM, tabGen.valueColumnName()
 					);
 				}
 				idGen.addParam(
 						org.hibernate.id.enhanced.TableGenerator.INCREMENT_PARAM,
 						String.valueOf( tabGen.allocationSize() )
 				);
 				// See comment on HHH-4884 wrt initialValue.  Basically initialValue is really the stated value + 1
 				idGen.addParam(
 						org.hibernate.id.enhanced.TableGenerator.INITIAL_PARAM,
 						String.valueOf( tabGen.initialValue() + 1 )
 				);
                 if (tabGen.uniqueConstraints() != null && tabGen.uniqueConstraints().length > 0) LOG.warn(tabGen.name());
 			}
 			else {
 				idGen.setIdentifierGeneratorStrategy( MultipleHiLoPerTableGenerator.class.getName() );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.table() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.ID_TABLE, tabGen.table() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.catalog() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.CATALOG, tabGen.catalog() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.schema() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.SCHEMA, tabGen.schema() );
 				}
 				//FIXME implement uniqueconstrains
                 if (tabGen.uniqueConstraints() != null && tabGen.uniqueConstraints().length > 0) LOG.ignoringTableGeneratorConstraints(tabGen.name());
 
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnName() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.PK_COLUMN_NAME, tabGen.pkColumnName() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.valueColumnName() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.VALUE_COLUMN_NAME, tabGen.valueColumnName() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( tabGen.pkColumnValue() ) ) {
 					idGen.addParam( MultipleHiLoPerTableGenerator.PK_VALUE_NAME, tabGen.pkColumnValue() );
 				}
 				idGen.addParam( TableHiLoGenerator.MAX_LO, String.valueOf( tabGen.allocationSize() - 1 ) );
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add table generator with name: {0}", idGen.getName() );
 			}
 		}
 		else if ( ann instanceof SequenceGenerator ) {
 			SequenceGenerator seqGen = ( SequenceGenerator ) ann;
 			idGen.setName( seqGen.name() );
 			if ( useNewGeneratorMappings ) {
 				idGen.setIdentifierGeneratorStrategy( SequenceStyleGenerator.class.getName() );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.catalog() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.CATALOG, seqGen.catalog() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.schema() ) ) {
 					idGen.addParam( PersistentIdentifierGenerator.SCHEMA, seqGen.schema() );
 				}
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.sequenceName() ) ) {
 					idGen.addParam( SequenceStyleGenerator.SEQUENCE_PARAM, seqGen.sequenceName() );
 				}
 				idGen.addParam( SequenceStyleGenerator.INCREMENT_PARAM, String.valueOf( seqGen.allocationSize() ) );
 				idGen.addParam( SequenceStyleGenerator.INITIAL_PARAM, String.valueOf( seqGen.initialValue() ) );
 			}
 			else {
 				idGen.setIdentifierGeneratorStrategy( "seqhilo" );
 
 				if ( !BinderHelper.isEmptyAnnotationValue( seqGen.sequenceName() ) ) {
 					idGen.addParam( org.hibernate.id.SequenceGenerator.SEQUENCE, seqGen.sequenceName() );
 				}
 				//FIXME: work on initialValue() through SequenceGenerator.PARAMETERS
 				//		steve : or just use o.h.id.enhanced.SequenceStyleGenerator
 				if ( seqGen.initialValue() != 1 ) {
 					LOG.unsupportedInitialValue( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				}
 				idGen.addParam( SequenceHiLoGenerator.MAX_LO, String.valueOf( seqGen.allocationSize() - 1 ) );
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Add sequence generator with name: {0}", idGen.getName() );
 				}
 			}
 		}
 		else if ( ann instanceof GenericGenerator ) {
 			GenericGenerator genGen = ( GenericGenerator ) ann;
 			idGen.setName( genGen.name() );
 			idGen.setIdentifierGeneratorStrategy( genGen.strategy() );
 			Parameter[] params = genGen.parameters();
 			for ( Parameter parameter : params ) {
 				idGen.addParam( parameter.name(), parameter.value() );
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Add generic generator with name: {0}", idGen.getName() );
 			}
 		}
 		else {
 			throw new AssertionFailure( "Unknown Generator annotation: " + ann );
 		}
 		return idGen;
 	}
 
 	/**
 	 * Bind a class having JSR175 annotations. Subclasses <b>have to</b> be bound after its parent class.
 	 *
 	 * @param clazzToProcess entity to bind as {@code XClass} instance
 	 * @param inheritanceStatePerClass Meta data about the inheritance relationships for all mapped classes
 	 * @param mappings Mapping meta data
 	 *
 	 * @throws MappingException in case there is an configuration error
 	 */
 	public static void bindClass(
 			XClass clazzToProcess,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings) throws MappingException {
 		//@Entity and @MappedSuperclass on the same class leads to a NPE down the road
 		if ( clazzToProcess.isAnnotationPresent( Entity.class )
 				&&  clazzToProcess.isAnnotationPresent( MappedSuperclass.class ) ) {
 			throw new AnnotationException( "An entity cannot be annotated with both @Entity and @MappedSuperclass: "
 					+ clazzToProcess.getName() );
 		}
 
 		//TODO: be more strict with secondarytable allowance (not for ids, not for secondary table join columns etc)
 		InheritanceState inheritanceState = inheritanceStatePerClass.get( clazzToProcess );
 		AnnotatedClassType classType = mappings.getClassType( clazzToProcess );
 
 		//Queries declared in MappedSuperclass should be usable in Subclasses
 		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 			bindQueries( clazzToProcess, mappings );
 			bindTypeDefs( clazzToProcess, mappings );
 			bindFilterDefs( clazzToProcess, mappings );
 		}
 
 		if ( !isEntityClassType( clazzToProcess, classType ) ) {
 			return;
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding entity from annotated class: %s", clazzToProcess.getName() );
 		}
 
 		PersistentClass superEntity = getSuperEntity(
 				clazzToProcess, inheritanceStatePerClass, mappings, inheritanceState
 		);
 
 		PersistentClass persistentClass = makePersistentClass( inheritanceState, superEntity );
 		Entity entityAnn = clazzToProcess.getAnnotation( Entity.class );
 		org.hibernate.annotations.Entity hibEntityAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.Entity.class
 		);
 		EntityBinder entityBinder = new EntityBinder(
 				entityAnn, hibEntityAnn, clazzToProcess, persistentClass, mappings
 		);
 		entityBinder.setInheritanceState( inheritanceState );
 
 		bindQueries( clazzToProcess, mappings );
 		bindFilterDefs( clazzToProcess, mappings );
 		bindTypeDefs( clazzToProcess, mappings );
 		bindFetchProfiles( clazzToProcess, mappings );
 		BinderHelper.bindAnyMetaDefs( clazzToProcess, mappings );
 
 		String schema = "";
 		String table = ""; //might be no @Table annotation on the annotated class
 		String catalog = "";
 		List<UniqueConstraintHolder> uniqueConstraints = new ArrayList<UniqueConstraintHolder>();
 		if ( clazzToProcess.isAnnotationPresent( javax.persistence.Table.class ) ) {
 			javax.persistence.Table tabAnn = clazzToProcess.getAnnotation( javax.persistence.Table.class );
 			table = tabAnn.name();
 			schema = tabAnn.schema();
 			catalog = tabAnn.catalog();
 			uniqueConstraints = TableBinder.buildUniqueConstraintHolders( tabAnn.uniqueConstraints() );
 		}
 
 		Ejb3JoinColumn[] inheritanceJoinedColumns = makeInheritanceJoinColumns(
 				clazzToProcess, mappings, inheritanceState, superEntity
 		);
 		Ejb3DiscriminatorColumn discriminatorColumn = null;
 		if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			discriminatorColumn = processDiscriminatorProperties(
 					clazzToProcess, mappings, inheritanceState, entityBinder
 			);
 		}
 
 		entityBinder.setProxy( clazzToProcess.getAnnotation( Proxy.class ) );
 		entityBinder.setBatchSize( clazzToProcess.getAnnotation( BatchSize.class ) );
 		entityBinder.setWhere( clazzToProcess.getAnnotation( Where.class ) );
 	    entityBinder.setCache( determineCacheSettings( clazzToProcess, mappings ) );
 
 		//Filters are not allowed on subclasses
 		if ( !inheritanceState.hasParents() ) {
 			bindFilters( clazzToProcess, entityBinder, mappings );
 		}
 
 		entityBinder.bindEntity();
 
 		if ( inheritanceState.hasTable() ) {
 			Check checkAnn = clazzToProcess.getAnnotation( Check.class );
 			String constraints = checkAnn == null ?
 					null :
 					checkAnn.constraints();
 			entityBinder.bindTable(
 					schema, catalog, table, uniqueConstraints,
 					constraints, inheritanceState.hasDenormalizedTable() ?
 							superEntity.getTable() :
 							null
 			);
 		}
 		else if ( clazzToProcess.isAnnotationPresent( Table.class ) ) {
 			LOG.invalidTableAnnotation( clazzToProcess.getName() );
 		}
 
 
 		PropertyHolder propertyHolder = PropertyHolderBuilder.buildPropertyHolder(
 				clazzToProcess,
 				persistentClass,
 				entityBinder, mappings, inheritanceStatePerClass
 		);
 
 		javax.persistence.SecondaryTable secTabAnn = clazzToProcess.getAnnotation(
 				javax.persistence.SecondaryTable.class
 		);
 		javax.persistence.SecondaryTables secTabsAnn = clazzToProcess.getAnnotation(
 				javax.persistence.SecondaryTables.class
 		);
 		entityBinder.firstLevelSecondaryTablesBinding( secTabAnn, secTabsAnn );
 
 		OnDelete onDeleteAnn = clazzToProcess.getAnnotation( OnDelete.class );
 		boolean onDeleteAppropriate = false;
 		if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) && inheritanceState.hasParents() ) {
 			onDeleteAppropriate = true;
 			final JoinedSubclass jsc = ( JoinedSubclass ) persistentClass;
 			SimpleValue key = new DependantValue( mappings, jsc.getTable(), jsc.getIdentifier() );
 			jsc.setKey( key );
 			ForeignKey fk = clazzToProcess.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				key.setForeignKeyName( fk.name() );
 			}
 			if ( onDeleteAnn != null ) {
 				key.setCascadeDeleteEnabled( OnDeleteAction.CASCADE.equals( onDeleteAnn.action() ) );
 			}
 			else {
 				key.setCascadeDeleteEnabled( false );
 			}
 			//we are never in a second pass at that stage, so queue it
 			SecondPass sp = new JoinedSubclassFkSecondPass( jsc, inheritanceJoinedColumns, key, mappings );
 			mappings.addSecondPass( sp );
 			mappings.addSecondPass( new CreateKeySecondPass( jsc ) );
 
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			if ( ! inheritanceState.hasParents() ) {
 				if ( inheritanceState.hasSiblings() || !discriminatorColumn.isImplicit() ) {
 					//need a discriminator column
 					bindDiscriminatorToPersistentClass(
 							( RootClass ) persistentClass,
 							discriminatorColumn,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							mappings
 					);
 					entityBinder.bindDiscriminatorValue();//bind it again since the type might have changed
 				}
 			}
 		}
 		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.getType() ) ) {
 			//nothing to do
 		}
         if (onDeleteAnn != null && !onDeleteAppropriate) LOG.invalidOnDeleteAnnotation(propertyHolder.getEntityName());
 
 		// try to find class level generators
 		HashMap<String, IdGenerator> classGenerators = buildLocalGenerators( clazzToProcess, mappings );
 
 		// check properties
 		final InheritanceState.ElementsToProcess elementsToProcess = inheritanceState.getElementsToProcess();
 		inheritanceState.postProcess( persistentClass, entityBinder );
 
 		final boolean subclassAndSingleTableStrategy = inheritanceState.getType() == InheritanceType.SINGLE_TABLE
 				&& inheritanceState.hasParents();
 		Set<String> idPropertiesIfIdClass = new HashSet<String>();
 		boolean isIdClass = mapAsIdClass(
 				inheritanceStatePerClass,
 				inheritanceState,
 				persistentClass,
 				entityBinder,
 				propertyHolder,
 				elementsToProcess,
 				idPropertiesIfIdClass,
 				mappings
 		);
 
 		if ( !isIdClass ) {
 			entityBinder.setWrapIdsInEmbeddedComponents( elementsToProcess.getIdPropertyCount() > 1 );
 		}
 
 		processIdPropertiesIfNotAlready(
 				inheritanceStatePerClass,
 				mappings,
 				persistentClass,
 				entityBinder,
 				propertyHolder,
 				classGenerators,
 				elementsToProcess,
 				subclassAndSingleTableStrategy,
 				idPropertiesIfIdClass
 		);
 
 		if ( !inheritanceState.hasParents() ) {
 			final RootClass rootClass = ( RootClass ) persistentClass;
 			mappings.addSecondPass( new CreateKeySecondPass( rootClass ) );
 		}
 		else {
 			superEntity.addSubclass( ( Subclass ) persistentClass );
 		}
 
 		mappings.addClass( persistentClass );
 
 		//Process secondary tables and complementary definitions (ie o.h.a.Table)
 		mappings.addSecondPass( new SecondaryTableSecondPass( entityBinder, propertyHolder, clazzToProcess ) );
 
 		//add process complementary Table definition (index & all)
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Table.class ) );
 		entityBinder.processComplementaryTableDefinitions( clazzToProcess.getAnnotation( org.hibernate.annotations.Tables.class ) );
 
 	}
 
 	// parse everything discriminator column relevant in case of single table inheritance
 	private static Ejb3DiscriminatorColumn processDiscriminatorProperties(XClass clazzToProcess, Mappings mappings, InheritanceState inheritanceState, EntityBinder entityBinder) {
 		Ejb3DiscriminatorColumn discriminatorColumn = null;
 		javax.persistence.DiscriminatorColumn discAnn = clazzToProcess.getAnnotation(
 				javax.persistence.DiscriminatorColumn.class
 		);
 		DiscriminatorType discriminatorType = discAnn != null ?
 				discAnn.discriminatorType() :
 				DiscriminatorType.STRING;
 
 		org.hibernate.annotations.DiscriminatorFormula discFormulaAnn = clazzToProcess.getAnnotation(
 				org.hibernate.annotations.DiscriminatorFormula.class
 		);
 		if ( !inheritanceState.hasParents() ) {
 			discriminatorColumn = Ejb3DiscriminatorColumn.buildDiscriminatorColumn(
 					discriminatorType, discAnn, discFormulaAnn, mappings
 			);
 		}
 		if ( discAnn != null && inheritanceState.hasParents() ) {
 			LOG.invalidDiscriminatorAnnotation( clazzToProcess.getName() );
 		}
 
 		String discrimValue = clazzToProcess.isAnnotationPresent( DiscriminatorValue.class ) ?
 				clazzToProcess.getAnnotation( DiscriminatorValue.class ).value() :
 				null;
 		entityBinder.setDiscriminatorValue( discrimValue );
 
 		DiscriminatorOptions discriminatorOptions = clazzToProcess.getAnnotation( DiscriminatorOptions.class );
 		if ( discriminatorOptions != null) {
 			entityBinder.setForceDiscriminator( discriminatorOptions.force() );
 			entityBinder.setInsertableDiscriminator( discriminatorOptions.insert() );
 		}
 
 		return discriminatorColumn;
 	}
 
 	private static void processIdPropertiesIfNotAlready(
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings,
 			PersistentClass persistentClass,
 			EntityBinder entityBinder,
 			PropertyHolder propertyHolder,
 			HashMap<String, IdGenerator> classGenerators,
 			InheritanceState.ElementsToProcess elementsToProcess,
 			boolean subclassAndSingleTableStrategy,
 			Set<String> idPropertiesIfIdClass) {
 		Set<String> missingIdProperties = new HashSet<String>( idPropertiesIfIdClass );
 		for ( PropertyData propertyAnnotatedElement : elementsToProcess.getElements() ) {
 			String propertyName = propertyAnnotatedElement.getPropertyName();
 			if ( !idPropertiesIfIdClass.contains( propertyName ) ) {
 				processElementAnnotations(
 						propertyHolder,
 						subclassAndSingleTableStrategy ?
 								Nullability.FORCED_NULL :
 								Nullability.NO_CONSTRAINT,
 						propertyAnnotatedElement, classGenerators, entityBinder,
 						false, false, false, mappings, inheritanceStatePerClass
 				);
 			}
 			else {
 				missingIdProperties.remove( propertyName );
 			}
 		}
 
 		if ( missingIdProperties.size() != 0 ) {
 			StringBuilder missings = new StringBuilder();
 			for ( String property : missingIdProperties ) {
 				missings.append( property ).append( ", " );
 			}
 			throw new AnnotationException(
 					"Unable to find properties ("
 							+ missings.substring( 0, missings.length() - 2 )
 							+ ") in entity annotated with @IdClass:" + persistentClass.getEntityName()
 			);
 		}
 	}
 
 	private static boolean mapAsIdClass(
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			InheritanceState inheritanceState,
 			PersistentClass persistentClass,
 			EntityBinder entityBinder,
 			PropertyHolder propertyHolder,
 			InheritanceState.ElementsToProcess elementsToProcess,
 			Set<String> idPropertiesIfIdClass,
 			Mappings mappings) {
 		/*
 		 * We are looking for @IdClass
 		 * In general we map the id class as identifier using the mapping metadata of the main entity's properties
 		 * and we create an identifier mapper containing the id properties of the main entity
 		 *
 		 * In JPA 2, there is a shortcut if the id class is the Pk of the associated class pointed to by the id
 		 * it ought to be treated as an embedded and not a real IdClass (at least in the Hibernate's internal way
 		 */
 		XClass classWithIdClass = inheritanceState.getClassWithIdClass( false );
 		if ( classWithIdClass != null ) {
 			IdClass idClass = classWithIdClass.getAnnotation( IdClass.class );
 			XClass compositeClass = mappings.getReflectionManager().toXClass( idClass.value() );
 			PropertyData inferredData = new PropertyPreloadedData(
 					entityBinder.getPropertyAccessType(), "id", compositeClass
 			);
 			PropertyData baseInferredData = new PropertyPreloadedData(
 					entityBinder.getPropertyAccessType(), "id", classWithIdClass
 			);
 			AccessType propertyAccessor = entityBinder.getPropertyAccessor( compositeClass );
 			//In JPA 2, there is a shortcut if the IdClass is the Pk of the associated class pointed to by the id
 			//it ought to be treated as an embedded and not a real IdClass (at least in the Hibernate's internal way
 			final boolean isFakeIdClass = isIdClassPkOfTheAssociatedEntity(
 					elementsToProcess,
 					compositeClass,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					inheritanceStatePerClass,
 					mappings
 			);
 
 			if ( isFakeIdClass ) {
 				return false;
 			}
 
 			boolean isComponent = true;
 			String generatorType = "assigned";
 			String generator = BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 			boolean ignoreIdAnnotations = entityBinder.isIgnoreIdAnnotations();
 			entityBinder.setIgnoreIdAnnotations( true );
 			propertyHolder.setInIdClass( true );
 			bindIdClass(
 					generatorType,
 					generator,
 					inferredData,
 					baseInferredData,
 					null,
 					propertyHolder,
 					isComponent,
 					propertyAccessor,
 					entityBinder,
 					true,
 					false,
 					mappings,
 					inheritanceStatePerClass
 			);
 			propertyHolder.setInIdClass( null );
 			inferredData = new PropertyPreloadedData(
 					propertyAccessor, "_identifierMapper", compositeClass
 			);
 			Component mapper = fillComponent(
 					propertyHolder,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					false,
 					entityBinder,
 					true,
 					true,
 					false,
 					mappings,
 					inheritanceStatePerClass
 			);
 			entityBinder.setIgnoreIdAnnotations( ignoreIdAnnotations );
 			persistentClass.setIdentifierMapper( mapper );
 
 			//If id definition is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass =
 					BinderHelper.getMappedSuperclassOrNull(
 							inferredData.getDeclaringClass(),
 							inheritanceStatePerClass,
 							mappings
 					);
 			if ( superclass != null ) {
 				superclass.setDeclaredIdentifierMapper( mapper );
 			}
 			else {
 				//we are for sure on the entity
 				persistentClass.setDeclaredIdentifierMapper( mapper );
 			}
 
 			Property property = new Property();
 			property.setName( "_identifierMapper" );
 			property.setNodeName( "id" );
 			property.setUpdateable( false );
 			property.setInsertable( false );
 			property.setValue( mapper );
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty( property );
 			entityBinder.setIgnoreIdAnnotations( true );
 
 			Iterator properties = mapper.getPropertyIterator();
 			while ( properties.hasNext() ) {
 				idPropertiesIfIdClass.add( ( ( Property ) properties.next() ).getName() );
 			}
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private static boolean isIdClassPkOfTheAssociatedEntity(
 			InheritanceState.ElementsToProcess elementsToProcess,
 			XClass compositeClass,
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			AccessType propertyAccessor,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings) {
 		if ( elementsToProcess.getIdPropertyCount() == 1 ) {
 			final PropertyData idPropertyOnBaseClass = getUniqueIdPropertyFromBaseClass(
 					inferredData, baseInferredData, propertyAccessor, mappings
 			);
 			final InheritanceState state = inheritanceStatePerClass.get( idPropertyOnBaseClass.getClassOrElement() );
 			if ( state == null ) {
 				return false; //while it is likely a user error, let's consider it is something that might happen
 			}
 			final XClass associatedClassWithIdClass = state.getClassWithIdClass( true );
 			if ( associatedClassWithIdClass == null ) {
 				//we cannot know for sure here unless we try and find the @EmbeddedId
 				//Let's not do this thorough checking but do some extra validation
 				final XProperty property = idPropertyOnBaseClass.getProperty();
 				return property.isAnnotationPresent( ManyToOne.class )
 						|| property.isAnnotationPresent( OneToOne.class );
 
 			}
 			else {
 				final XClass idClass = mappings.getReflectionManager().toXClass(
 						associatedClassWithIdClass.getAnnotation( IdClass.class ).value()
 				);
 				return idClass.equals( compositeClass );
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	private static Cache determineCacheSettings(XClass clazzToProcess, Mappings mappings) {
 		Cache cacheAnn = clazzToProcess.getAnnotation( Cache.class );
 		if ( cacheAnn != null ) {
 			return cacheAnn;
 		}
 
 		Cacheable cacheableAnn = clazzToProcess.getAnnotation( Cacheable.class );
 		SharedCacheMode mode = determineSharedCacheMode( mappings );
 		switch ( mode ) {
 			case ALL: {
 				cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				if ( cacheableAnn != null && cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				}
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				if ( cacheableAnn == null || cacheableAnn.value() ) {
 					cacheAnn = buildCacheMock( clazzToProcess.getName(), mappings );
 				}
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				break;
 			}
 		}
 		return cacheAnn;
 	}
 
 	private static SharedCacheMode determineSharedCacheMode(Mappings mappings) {
 		SharedCacheMode mode;
 		final Object value = mappings.getConfigurationProperties().get( "javax.persistence.sharedCache.mode" );
 		if ( value == null ) {
-			LOG.debugf( "No value specified for 'javax.persistence.sharedCache.mode'; using UNSPECIFIED" );
+			LOG.debug( "No value specified for 'javax.persistence.sharedCache.mode'; using UNSPECIFIED" );
 			mode = SharedCacheMode.UNSPECIFIED;
 		}
 		else {
 			if ( SharedCacheMode.class.isInstance( value ) ) {
 				mode = ( SharedCacheMode ) value;
 			}
 			else {
 				try {
 					mode = SharedCacheMode.valueOf( value.toString() );
 				}
 				catch ( Exception e ) {
 					LOG.debugf( "Unable to resolve given mode name [%s]; using UNSPECIFIED : %s", value, e );
 					mode = SharedCacheMode.UNSPECIFIED;
 				}
 			}
 		}
 		return mode;
 	}
 
 	private static Cache buildCacheMock(String region, Mappings mappings) {
 		return new LocalCacheAnnotationImpl( region, determineCacheConcurrencyStrategy( mappings ) );
 	}
 
 	private static CacheConcurrencyStrategy DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	static void prepareDefaultCacheConcurrencyStrategy(Properties properties) {
 		if ( DEFAULT_CACHE_CONCURRENCY_STRATEGY != null ) {
 			LOG.trace( "Default cache concurrency strategy already defined" );
 			return;
 		}
 
 		if ( !properties.containsKey( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY ) ) {
 			LOG.trace( "Given properties did not contain any default cache concurrency strategy setting" );
 			return;
 		}
 
 		final String strategyName = properties.getProperty( AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY );
 		LOG.tracev( "Discovered default cache concurrency strategy via config [{0}]", strategyName );
 		CacheConcurrencyStrategy strategy = CacheConcurrencyStrategy.parse( strategyName );
 		if ( strategy == null ) {
 			LOG.trace( "Discovered default cache concurrency strategy specified nothing" );
 			return;
 		}
 
 		LOG.debugf( "Setting default cache concurrency strategy via config [%s]", strategy.name() );
 		DEFAULT_CACHE_CONCURRENCY_STRATEGY = strategy;
 	}
 
 	private static CacheConcurrencyStrategy determineCacheConcurrencyStrategy(Mappings mappings) {
 		if ( DEFAULT_CACHE_CONCURRENCY_STRATEGY == null ) {
 			final RegionFactory cacheRegionFactory = SettingsFactory.createRegionFactory(
 					mappings.getConfigurationProperties(), true
 			);
 			DEFAULT_CACHE_CONCURRENCY_STRATEGY = CacheConcurrencyStrategy.fromAccessType( cacheRegionFactory.getDefaultAccessType() );
 		}
 		return DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 	}
 
 	@SuppressWarnings({ "ClassExplicitlyAnnotation" })
 	private static class LocalCacheAnnotationImpl implements Cache {
 		private final String region;
 		private final CacheConcurrencyStrategy usage;
 
 		private LocalCacheAnnotationImpl(String region, CacheConcurrencyStrategy usage) {
 			this.region = region;
 			this.usage = usage;
 		}
 
 		public CacheConcurrencyStrategy usage() {
 			return usage;
 		}
 
 		public String region() {
 			return region;
 		}
 
 		public String include() {
 			return "all";
 		}
 
 		public Class<? extends Annotation> annotationType() {
 			return Cache.class;
 		}
 	}
 
 	private static PersistentClass makePersistentClass(InheritanceState inheritanceState, PersistentClass superEntity) {
 		//we now know what kind of persistent entity it is
 		PersistentClass persistentClass;
 		//create persistent class
 		if ( !inheritanceState.hasParents() ) {
 			persistentClass = new RootClass();
 		}
 		else if ( InheritanceType.SINGLE_TABLE.equals( inheritanceState.getType() ) ) {
 			persistentClass = new SingleTableSubclass( superEntity );
 		}
 		else if ( InheritanceType.JOINED.equals( inheritanceState.getType() ) ) {
 			persistentClass = new JoinedSubclass( superEntity );
 		}
 		else if ( InheritanceType.TABLE_PER_CLASS.equals( inheritanceState.getType() ) ) {
 			persistentClass = new UnionSubclass( superEntity );
 		}
 		else {
 			throw new AssertionFailure( "Unknown inheritance type: " + inheritanceState.getType() );
 		}
 		return persistentClass;
 	}
 
 	private static Ejb3JoinColumn[] makeInheritanceJoinColumns(
 			XClass clazzToProcess,
 			Mappings mappings,
 			InheritanceState inheritanceState,
 			PersistentClass superEntity) {
 		Ejb3JoinColumn[] inheritanceJoinedColumns = null;
 		final boolean hasJoinedColumns = inheritanceState.hasParents()
 				&& InheritanceType.JOINED.equals( inheritanceState.getType() );
 		if ( hasJoinedColumns ) {
 			//@Inheritance(JOINED) subclass need to link back to the super entity
 			PrimaryKeyJoinColumns jcsAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumns.class );
 			boolean explicitInheritanceJoinedColumns = jcsAnn != null && jcsAnn.value().length != 0;
 			if ( explicitInheritanceJoinedColumns ) {
 				int nbrOfInhJoinedColumns = jcsAnn.value().length;
 				PrimaryKeyJoinColumn jcAnn;
 				inheritanceJoinedColumns = new Ejb3JoinColumn[nbrOfInhJoinedColumns];
 				for ( int colIndex = 0; colIndex < nbrOfInhJoinedColumns; colIndex++ ) {
 					jcAnn = jcsAnn.value()[colIndex];
 					inheritanceJoinedColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 							jcAnn, null, superEntity.getIdentifier(),
 							( Map<String, Join> ) null, ( PropertyHolder ) null, mappings
 					);
 				}
 			}
 			else {
 				PrimaryKeyJoinColumn jcAnn = clazzToProcess.getAnnotation( PrimaryKeyJoinColumn.class );
 				inheritanceJoinedColumns = new Ejb3JoinColumn[1];
 				inheritanceJoinedColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						jcAnn, null, superEntity.getIdentifier(),
 						( Map<String, Join> ) null, ( PropertyHolder ) null, mappings
 				);
 			}
 			LOG.trace( "Subclass joined column(s) created" );
 		}
 		else {
 			if ( clazzToProcess.isAnnotationPresent( PrimaryKeyJoinColumns.class )
 					|| clazzToProcess.isAnnotationPresent( PrimaryKeyJoinColumn.class ) ) {
 				LOG.invalidPrimaryKeyJoinColumnAnnotation();
 			}
 		}
 		return inheritanceJoinedColumns;
 	}
 
 	private static PersistentClass getSuperEntity(XClass clazzToProcess, Map<XClass, InheritanceState> inheritanceStatePerClass, Mappings mappings, InheritanceState inheritanceState) {
 		InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(
 				clazzToProcess, inheritanceStatePerClass
 		);
 		PersistentClass superEntity = superEntityState != null ?
 				mappings.getClass(
 						superEntityState.getClazz().getName()
 				) :
 				null;
 		if ( superEntity == null ) {
 			//check if superclass is not a potential persistent class
 			if ( inheritanceState.hasParents() ) {
 				throw new AssertionFailure(
 						"Subclass has to be binded after it's mother class: "
 								+ superEntityState.getClazz().getName()
 				);
 			}
 		}
 		return superEntity;
 	}
 
 	private static boolean isEntityClassType(XClass clazzToProcess, AnnotatedClassType classType) {
 		if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) //will be processed by their subentities
 				|| AnnotatedClassType.NONE.equals( classType ) //to be ignored
 				|| AnnotatedClassType.EMBEDDABLE.equals( classType ) //allow embeddable element declaration
 				) {
 			if ( AnnotatedClassType.NONE.equals( classType )
 					&& clazzToProcess.isAnnotationPresent( org.hibernate.annotations.Entity.class ) ) {
 				LOG.missingEntityAnnotation( clazzToProcess.getName() );
 			}
 			return false;
 		}
 
 		if ( !classType.equals( AnnotatedClassType.ENTITY ) ) {
 			throw new AnnotationException(
 					"Annotated class should have a @javax.persistence.Entity, @javax.persistence.Embeddable or @javax.persistence.EmbeddedSuperclass annotation: " + clazzToProcess
 							.getName()
 			);
 		}
 
 		return true;
 	}
 
 	/*
 	 * Process the filters defined on the given class, as well as all filters defined
 	 * on the MappedSuperclass(s) in the inheritance hierarchy
 	 */
 
 	private static void bindFilters(XClass annotatedClass, EntityBinder entityBinder,
 									Mappings mappings) {
 
 		bindFilters( annotatedClass, entityBinder );
 
 		XClass classToProcess = annotatedClass.getSuperclass();
 		while ( classToProcess != null ) {
 			AnnotatedClassType classType = mappings.getClassType( classToProcess );
 			if ( AnnotatedClassType.EMBEDDABLE_SUPERCLASS.equals( classType ) ) {
 				bindFilters( classToProcess, entityBinder );
 			}
 			classToProcess = classToProcess.getSuperclass();
 		}
 
 	}
 
 	private static void bindFilters(XAnnotatedElement annotatedElement, EntityBinder entityBinder) {
 
 		Filters filtersAnn = annotatedElement.getAnnotation( Filters.class );
 		if ( filtersAnn != null ) {
 			for ( Filter filter : filtersAnn.value() ) {
 				entityBinder.addFilter( filter.name(), filter.condition() );
 			}
 		}
 
 		Filter filterAnn = annotatedElement.getAnnotation( Filter.class );
 		if ( filterAnn != null ) {
 			entityBinder.addFilter( filterAnn.name(), filterAnn.condition() );
 		}
 	}
 
 	private static void bindFilterDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
 		FilterDef defAnn = annotatedElement.getAnnotation( FilterDef.class );
 		FilterDefs defsAnn = annotatedElement.getAnnotation( FilterDefs.class );
 		if ( defAnn != null ) {
 			bindFilterDef( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( FilterDef def : defsAnn.value() ) {
 				bindFilterDef( def, mappings );
 			}
 		}
 	}
 
 	private static void bindFilterDef(FilterDef defAnn, Mappings mappings) {
 		Map<String, org.hibernate.type.Type> params = new HashMap<String, org.hibernate.type.Type>();
 		for ( ParamDef param : defAnn.parameters() ) {
 			params.put( param.name(), mappings.getTypeResolver().heuristicType( param.type() ) );
 		}
 		FilterDefinition def = new FilterDefinition( defAnn.name(), defAnn.defaultCondition(), params );
 		LOG.debugf( "Binding filter definition: %s", def.getFilterName() );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void bindTypeDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
 		TypeDef defAnn = annotatedElement.getAnnotation( TypeDef.class );
 		TypeDefs defsAnn = annotatedElement.getAnnotation( TypeDefs.class );
 		if ( defAnn != null ) {
 			bindTypeDef( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for ( TypeDef def : defsAnn.value() ) {
 				bindTypeDef( def, mappings );
 			}
 		}
 	}
 
 	private static void bindFetchProfiles(XAnnotatedElement annotatedElement, Mappings mappings) {
 		FetchProfile fetchProfileAnnotation = annotatedElement.getAnnotation( FetchProfile.class );
 		FetchProfiles fetchProfileAnnotations = annotatedElement.getAnnotation( FetchProfiles.class );
 		if ( fetchProfileAnnotation != null ) {
 			bindFetchProfile( fetchProfileAnnotation, mappings );
 		}
 		if ( fetchProfileAnnotations != null ) {
 			for ( FetchProfile profile : fetchProfileAnnotations.value() ) {
 				bindFetchProfile( profile, mappings );
 			}
 		}
 	}
 
 	private static void bindFetchProfile(FetchProfile fetchProfileAnnotation, Mappings mappings) {
 		for ( FetchProfile.FetchOverride fetch : fetchProfileAnnotation.fetchOverrides() ) {
 			org.hibernate.annotations.FetchMode mode = fetch.mode();
 			if ( !mode.equals( org.hibernate.annotations.FetchMode.JOIN ) ) {
 				throw new MappingException( "Only FetchMode.JOIN is currently supported" );
 			}
 
 			SecondPass sp = new VerifyFetchProfileReferenceSecondPass( fetchProfileAnnotation.name(), fetch, mappings );
 			mappings.addSecondPass( sp );
 		}
 	}
 
 	private static void bindTypeDef(TypeDef defAnn, Mappings mappings) {
 		Properties params = new Properties();
 		for ( Parameter param : defAnn.parameters() ) {
 			params.setProperty( param.name(), param.value() );
 		}
 
 		if ( BinderHelper.isEmptyAnnotationValue( defAnn.name() ) && defAnn.defaultForType().equals( void.class ) ) {
 			throw new AnnotationException(
 					"Either name or defaultForType (or both) attribute should be set in TypeDef having typeClass " +
 							defAnn.typeClass().getName()
 			);
 		}
 
 		final String typeBindMessageF = "Binding type definition: %s";
 		if ( !BinderHelper.isEmptyAnnotationValue( defAnn.name() ) ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( typeBindMessageF, defAnn.name() );
 			}
 			mappings.addTypeDef( defAnn.name(), defAnn.typeClass().getName(), params );
 		}
 		if ( !defAnn.defaultForType().equals( void.class ) ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( typeBindMessageF, defAnn.defaultForType().getName() );
 			}
 			mappings.addTypeDef( defAnn.defaultForType().getName(), defAnn.typeClass().getName(), params );
 		}
 
 	}
 
 
 	private static void bindDiscriminatorToPersistentClass(
 			RootClass rootClass,
 			Ejb3DiscriminatorColumn discriminatorColumn,
 			Map<String, Join> secondaryTables,
 			PropertyHolder propertyHolder,
 			Mappings mappings) {
 		if ( rootClass.getDiscriminator() == null ) {
 			if ( discriminatorColumn == null ) {
 				throw new AssertionFailure( "discriminator column should have been built" );
 			}
 			discriminatorColumn.setJoins( secondaryTables );
 			discriminatorColumn.setPropertyHolder( propertyHolder );
 			SimpleValue discrim = new SimpleValue( mappings, rootClass.getTable() );
 			rootClass.setDiscriminator( discrim );
 			discriminatorColumn.linkWithValue( discrim );
 			discrim.setTypeName( discriminatorColumn.getDiscriminatorTypeName() );
 			rootClass.setPolymorphic( true );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Setting discriminator for entity {0}", rootClass.getEntityName() );
 			}
 		}
 	}
 
 	/**
 	 * @param elements List of {@code ProperyData} instances
 	 * @param defaultAccessType The default value access strategy which has to be used in case no explicit local access
 	 * strategy is used
 	 * @param propertyContainer Metadata about a class and its properties
 	 * @param mappings Mapping meta data
 	 *
 	 * @return the number of id properties found while iterating the elements of {@code annotatedClass} using
 	 *         the determined access strategy, {@code false} otherwise.
 	 */
 	static int addElementsOfClass(
 			List<PropertyData> elements,
 			AccessType defaultAccessType,
 			PropertyContainer propertyContainer,
 			Mappings mappings) {
 		int idPropertyCounter = 0;
 		AccessType accessType = defaultAccessType;
 
 		if ( propertyContainer.hasExplicitAccessStrategy() ) {
 			accessType = propertyContainer.getExplicitAccessStrategy();
 		}
 
 		Collection<XProperty> properties = propertyContainer.getProperties( accessType );
 		for ( XProperty p : properties ) {
 			final int currentIdPropertyCounter = addProperty(
 					propertyContainer, p, elements, accessType.getType(), mappings
 			);
 			idPropertyCounter += currentIdPropertyCounter;
 		}
 		return idPropertyCounter;
 	}
 
 	private static int addProperty(
 			PropertyContainer propertyContainer,
 			XProperty property,
 			List<PropertyData> annElts,
 			String propertyAccessor,
 			Mappings mappings) {
 		final XClass declaringClass = propertyContainer.getDeclaringClass();
 		final XClass entity = propertyContainer.getEntityAtStake();
 		int idPropertyCounter = 0;
 		PropertyData propertyAnnotatedElement = new PropertyInferredData(
 				declaringClass, property, propertyAccessor,
 				mappings.getReflectionManager()
 		);
 
 		/*
 		 * put element annotated by @Id in front
 		 * since it has to be parsed before any association by Hibernate
 		 */
 		final XAnnotatedElement element = propertyAnnotatedElement.getProperty();
 		if ( element.isAnnotationPresent( Id.class ) || element.isAnnotationPresent( EmbeddedId.class ) ) {
 			annElts.add( 0, propertyAnnotatedElement );
 			/**
 			 * The property must be put in hibernate.properties as it's a system wide property. Fixable?
 			 * TODO support true/false/default on the property instead of present / not present
 			 * TODO is @Column mandatory?
 			 * TODO add method support
 			 */
 			if ( mappings.isSpecjProprietarySyntaxEnabled() ) {
 				if ( element.isAnnotationPresent( Id.class ) && element.isAnnotationPresent( Column.class ) ) {
 					String columnName = element.getAnnotation( Column.class ).name();
 					for ( XProperty prop : declaringClass.getDeclaredProperties( AccessType.FIELD.getType() ) ) {
 						if ( prop.isAnnotationPresent( JoinColumn.class )
 								&& prop.getAnnotation( JoinColumn.class ).name().equals( columnName )
 								&& !prop.isAnnotationPresent( MapsId.class ) ) {
 							//create a PropertyData fpr the specJ property holding the mapping
 							PropertyData specJPropertyData = new PropertyInferredData(
 									declaringClass,  //same dec
 									prop, // the actual @XToOne property
 									propertyAccessor, //TODO we should get the right accessor but the same as id would do
 									mappings.getReflectionManager()
 							);
 							mappings.addPropertyAnnotatedWithMapsIdSpecj( entity, specJPropertyData, element.toString() );
 						}
 					}
 				}
 			}
 
 			if ( element.isAnnotationPresent( ManyToOne.class ) || element.isAnnotationPresent( OneToOne.class ) ) {
 				mappings.addToOneAndIdProperty( entity, propertyAnnotatedElement );
 			}
 			idPropertyCounter++;
 		}
 		else {
 			annElts.add( propertyAnnotatedElement );
 		}
 		if ( element.isAnnotationPresent( MapsId.class ) ) {
 			mappings.addPropertyAnnotatedWithMapsId( entity, propertyAnnotatedElement );
 		}
 
 		return idPropertyCounter;
 	}
 
 	/*
 	 * Process annotation of a particular property
 	 */
 
 	private static void processElementAnnotations(
 			PropertyHolder propertyHolder,
 			Nullability nullability,
 			PropertyData inferredData,
 			HashMap<String, IdGenerator> classGenerators,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			boolean isComponentEmbedded,
 			boolean inSecondPass,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) throws MappingException {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Processing annotations of {0}.{1}" , propertyHolder.getEntityName(), inferredData.getPropertyName() );
 		}
 
 		final XProperty property = inferredData.getProperty();
 		if ( property.isAnnotationPresent( Parent.class ) ) {
 			if ( propertyHolder.isComponent() ) {
 				propertyHolder.setParentProperty( property.getName() );
 			}
 			else {
 				throw new AnnotationException(
 						"@Parent cannot be applied outside an embeddable object: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 			return;
 		}
 
 		ColumnsBuilder columnsBuilder = new ColumnsBuilder(
 				propertyHolder, nullability, property, inferredData, entityBinder, mappings
 		).extractMetadata();
 		Ejb3Column[] columns = columnsBuilder.getColumns();
 		Ejb3JoinColumn[] joinColumns = columnsBuilder.getJoinColumns();
 
 		final XClass returnedClass = inferredData.getClassOrElement();
 
 		//prepare PropertyBinder
 		PropertyBinder propertyBinder = new PropertyBinder();
 		propertyBinder.setName( inferredData.getPropertyName() );
 		propertyBinder.setReturnedClassName( inferredData.getTypeName() );
 		propertyBinder.setAccessType( inferredData.getDefaultAccess() );
 		propertyBinder.setHolder( propertyHolder );
 		propertyBinder.setProperty( property );
 		propertyBinder.setReturnedClass( inferredData.getPropertyClass() );
 		propertyBinder.setMappings( mappings );
 		if ( isIdentifierMapper ) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		propertyBinder.setDeclaringClass( inferredData.getDeclaringClass() );
 		propertyBinder.setEntityBinder( entityBinder );
 		propertyBinder.setInheritanceStatePerClass( inheritanceStatePerClass );
 
 		boolean isId = !entityBinder.isIgnoreIdAnnotations() &&
 				( property.isAnnotationPresent( Id.class )
 						|| property.isAnnotationPresent( EmbeddedId.class ) );
 		propertyBinder.setId( isId );
 
 		if ( property.isAnnotationPresent( Version.class ) ) {
 			if ( isIdentifierMapper ) {
 				throw new AnnotationException(
 						"@IdClass class should not have @Version property"
 				);
 			}
 			if ( !( propertyHolder.getPersistentClass() instanceof RootClass ) ) {
 				throw new AnnotationException(
 						"Unable to define/override @Version on a subclass: "
 								+ propertyHolder.getEntityName()
 				);
 			}
 			if ( !propertyHolder.isEntity() ) {
 				throw new AnnotationException(
 						"Unable to define @Version on an embedded class: "
 								+ propertyHolder.getEntityName()
 				);
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "{0} is a version property", inferredData.getPropertyName() );
 			}
 			RootClass rootClass = ( RootClass ) propertyHolder.getPersistentClass();
 			propertyBinder.setColumns( columns );
 			Property prop = propertyBinder.makePropertyValueAndBind();
 			setVersionInformation( property, propertyBinder );
 			rootClass.setVersion( prop );
 
 			//If version is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					inferredData.getDeclaringClass(),
 					inheritanceStatePerClass,
 					mappings
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredVersion( prop );
 			}
 			else {
 				//we know the property is on the actual entity
 				rootClass.setDeclaredVersion( prop );
 			}
 
 			SimpleValue simpleValue = ( SimpleValue ) prop.getValue();
 			simpleValue.setNullValue( "undefined" );
 			rootClass.setOptimisticLockMode( Versioning.OPTIMISTIC_LOCK_VERSION );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Version name: {0}, unsavedValue: {1}", rootClass.getVersion().getName(),
 						( (SimpleValue) rootClass.getVersion().getValue() ).getNullValue() );
 			}
 		}
 		else {
 			final boolean forcePersist = property.isAnnotationPresent( MapsId.class )
 					|| property.isAnnotationPresent( Id.class );
 			if ( property.isAnnotationPresent( ManyToOne.class ) ) {
 				ManyToOne ann = property.getAnnotation( ManyToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @ManyToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindManyToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, false, forcePersist ),
 						joinColumns,
 						!mandatory,
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData, false, isIdentifierMapper,
 						inSecondPass, propertyBinder, mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToOne.class ) ) {
 				OneToOne ann = property.getAnnotation( OneToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @OneToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				//FIXME support a proper PKJCs
 				boolean trueOneToOne = property.isAnnotationPresent( PrimaryKeyJoinColumn.class )
 						|| property.isAnnotationPresent( PrimaryKeyJoinColumns.class );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				//MapsId means the columns belong to the pk => not null
 				//@OneToOne with @PKJC can still be optional
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindOneToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, ann.orphanRemoval(), forcePersist ),
 						joinColumns,
 						!mandatory,
 						getFetchMode( ann.fetch() ),
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData,
 						ann.mappedBy(),
 						trueOneToOne,
 						isIdentifierMapper,
 						inSecondPass,
 						propertyBinder,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( org.hibernate.annotations.Any.class ) ) {
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @Any property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				bindAny(
 						getCascadeStrategy( null, hibernateCascade, false, forcePersist ),
 						//@Any has not cascade attribute
 						joinColumns,
 						onDeleteCascade,
 						nullability,
 						propertyHolder,
 						inferredData,
 						entityBinder,
 						isIdentifierMapper,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToMany.class )
 					|| property.isAnnotationPresent( ManyToMany.class )
 					|| property.isAnnotationPresent( ElementCollection.class )
 					|| property.isAnnotationPresent( ManyToAny.class ) ) {
 				OneToMany oneToManyAnn = property.getAnnotation( OneToMany.class );
 				ManyToMany manyToManyAnn = property.getAnnotation( ManyToMany.class );
 				ElementCollection elementCollectionAnn = property.getAnnotation( ElementCollection.class );
 
 				final IndexColumn indexColumn;
 
 				if ( property.isAnnotationPresent( OrderColumn.class ) ) {
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( OrderColumn.class ),
 							propertyHolder,
 							inferredData,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				else {
 					//if @IndexColumn is not there, the generated IndexColumn is an implicit column and not used.
 					//so we can leave the legacy processing as the default
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( org.hibernate.annotations.IndexColumn.class ),
 							propertyHolder,
 							inferredData,
 							mappings
 					);
 				}
 				CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(
 						propertyHolder.getEntityName(),
 						property,
 						!indexColumn.isImplicit(),
 						property.isAnnotationPresent( MapKeyType.class )
 
 						// || property.isAnnotationPresent( ManyToAny.class )
 				);
 				collectionBinder.setIndexColumn( indexColumn );
 				collectionBinder.setMapKey( property.getAnnotation( MapKey.class ) );
 				collectionBinder.setPropertyName( inferredData.getPropertyName() );
 				BatchSize batchAnn = property.getAnnotation( BatchSize.class );
 				collectionBinder.setBatchSize( batchAnn );
 				javax.persistence.OrderBy ejb3OrderByAnn = property.getAnnotation( javax.persistence.OrderBy.class );
 				OrderBy orderByAnn = property.getAnnotation( OrderBy.class );
 				collectionBinder.setEjb3OrderBy( ejb3OrderByAnn );
 				collectionBinder.setSqlOrderBy( orderByAnn );
 				Sort sortAnn = property.getAnnotation( Sort.class );
 				collectionBinder.setSort( sortAnn );
 				Cache cachAnn = property.getAnnotation( Cache.class );
 				collectionBinder.setCache( cachAnn );
 				collectionBinder.setPropertyHolder( propertyHolder );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				collectionBinder.setIgnoreNotFound( ignoreNotFound );
 				collectionBinder.setCollectionType( inferredData.getProperty().getElementClass() );
 				collectionBinder.setMappings( mappings );
 				collectionBinder.setAccessType( inferredData.getDefaultAccess() );
 
 				Ejb3Column[] elementColumns;
 				//do not use "element" if you are a JPA 2 @ElementCollection only for legacy Hibernate mappings
 				boolean isJPA2ForValueMapping = property.isAnnotationPresent( ElementCollection.class );
 				PropertyData virtualProperty = isJPA2ForValueMapping ? inferredData : new WrappedInferredData(
 						inferredData, "element"
 				);
 				if ( property.isAnnotationPresent( Column.class ) || property.isAnnotationPresent(
 						Formula.class
 				) ) {
 					Column ann = property.getAnnotation( Column.class );
 					Formula formulaAnn = property.getAnnotation( Formula.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							new Column[] { ann },
 							formulaAnn,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				else if ( property.isAnnotationPresent( Columns.class ) ) {
 					Columns anns = property.getAnnotation( Columns.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							anns.columns(), null, nullability, propertyHolder, virtualProperty,
 							entityBinder.getSecondaryTables(), mappings
 					);
 				}
 				else {
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							null,
 							null,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				{
 					Column[] keyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						keyColumns = new Column[] { new MapKeyColumnDelegator( property.getAnnotation( MapKeyColumn.class ) ) };
 					}
 
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					//nullify empty array
 					keyColumns = keyColumns != null && keyColumns.length > 0 ? keyColumns : null;
 
 					//"mapkey" is the legacy column name of the key column pre JPA 2
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3Column[] mapColumns = Ejb3Column.buildColumnFromAnnotation(
 							keyColumns,
 							null,
 							Nullability.FORCED_NOT_NULL,
 							propertyHolder,
 							isJPA2 ? inferredData : mapKeyVirtualProperty,
 							isJPA2 ? "_KEY" : null,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 					collectionBinder.setMapKeyColumns( mapColumns );
 				}
 				{
 					JoinColumn[] joinKeyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyJoinColumns.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						final MapKeyJoinColumn[] mapKeyJoinColumns = property.getAnnotation( MapKeyJoinColumns.class )
 								.value();
 						joinKeyColumns = new JoinColumn[mapKeyJoinColumns.length];
 						int index = 0;
 						for ( MapKeyJoinColumn joinColumn : mapKeyJoinColumns ) {
 							joinKeyColumns[index] = new MapKeyJoinColumnDelegator( joinColumn );
 							index++;
 						}
 						if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 							throw new AnnotationException(
 									"@MapKeyJoinColumn and @MapKeyJoinColumns used on the same property: "
 											+ BinderHelper.getPath( propertyHolder, inferredData )
 							);
 						}
 					}
 					else if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						joinKeyColumns = new JoinColumn[] {
 								new MapKeyJoinColumnDelegator(
 										property.getAnnotation(
 												MapKeyJoinColumn.class
 										)
 								)
 						};
 					}
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3JoinColumn[] mapJoinColumns = Ejb3JoinColumn.buildJoinColumnsWithDefaultColumnSuffix(
 							joinKeyColumns,
 							null,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							isJPA2 ? inferredData.getPropertyName() : mapKeyVirtualProperty.getPropertyName(),
 							isJPA2 ? "_KEY" : null,
 							mappings
 					);
 					collectionBinder.setMapKeyManyToManyColumns( mapJoinColumns );
 				}
 
 				//potential element
 				collectionBinder.setEmbedded( property.isAnnotationPresent( Embedded.class ) );
 				collectionBinder.setElementColumns( elementColumns );
 				collectionBinder.setProperty( property );
 
 				//TODO enhance exception with @ManyToAny and @CollectionOfElements
 				if ( oneToManyAnn != null && manyToManyAnn != null ) {
 					throw new AnnotationException(
 							"@OneToMany and @ManyToMany on the same property is not allowed: "
 									+ propertyHolder.getEntityName() + "." + inferredData.getPropertyName()
 					);
 				}
 				String mappedBy = null;
 				if ( oneToManyAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = oneToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( oneToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									oneToManyAnn.cascade(), hibernateCascade, oneToManyAnn.orphanRemoval(), false
 							)
 					);
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( elementCollectionAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = "";
 					final Class<?> targetElement = elementCollectionAnn.targetClass();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( targetElement )
 					);
 					//collectionBinder.setCascadeStrategy( getCascadeStrategy( embeddedCollectionAnn.cascade(), hibernateCascade ) );
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( manyToManyAnn != null ) {
 					mappedBy = manyToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( manyToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									manyToManyAnn.cascade(), hibernateCascade, false, false
 							)
 					);
 					collectionBinder.setOneToMany( false );
 				}
 				else if ( property.isAnnotationPresent( ManyToAny.class ) ) {
 					mappedBy = "";
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( void.class )
 					);
 					collectionBinder.setCascadeStrategy( getCascadeStrategy( null, hibernateCascade, false, false ) );
 					collectionBinder.setOneToMany( false );
 				}
 				collectionBinder.setMappedBy( mappedBy );
 
 				bindJoinedTableAssociation(
 						property, mappings, entityBinder, collectionBinder, propertyHolder, inferredData, mappedBy
 				);
 
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				collectionBinder.setCascadeDeleteEnabled( onDeleteCascade );
 				if ( isIdentifierMapper ) {
 					collectionBinder.setInsertable( false );
 					collectionBinder.setUpdatable( false );
 				}
 				if ( property.isAnnotationPresent( CollectionId.class ) ) { //do not compute the generators unless necessary
 					HashMap<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 					localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 					collectionBinder.setLocalGenerators( localGenerators );
 
 				}
 				collectionBinder.setInheritanceStatePerClass( inheritanceStatePerClass );
 				collectionBinder.setDeclaringClass( inferredData.getDeclaringClass() );
 				collectionBinder.bind();
 
 			}
 			//Either a regular property or a basic @Id or @EmbeddedId while not ignoring id annotations
 			else if ( !isId || !entityBinder.isIgnoreIdAnnotations() ) {
 				//define whether the type is a component or not
 
 				boolean isComponent = false;
 
 				//Overrides from @MapsId if needed
 				boolean isOverridden = false;
 				if ( isId || propertyHolder.isOrWithinEmbeddedId() || propertyHolder.isInIdClass() ) {
 					//the associated entity could be using an @IdClass making the overridden property a component
 					final PropertyData overridingProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), mappings
 					);
 					if ( overridingProperty != null ) {
 						isOverridden = true;
 						final InheritanceState state = inheritanceStatePerClass.get( overridingProperty.getClassOrElement() );
 						if ( state != null ) {
 							isComponent = isComponent || state.hasIdClassOrEmbeddedId();
 						}
 						//Get the new column
 						columns = columnsBuilder.overrideColumnFromMapperOrMapsIdProperty( isId );
 					}
 				}
 
 				isComponent = isComponent
 						|| property.isAnnotationPresent( Embedded.class )
 						|| property.isAnnotationPresent( EmbeddedId.class )
 						|| returnedClass.isAnnotationPresent( Embeddable.class );
 
 
 				if ( isComponent ) {
 					String referencedEntityName = null;
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), mappings
 						);
 						referencedEntityName = mapsIdProperty.getClassOrElementName();
 					}
 					AccessType propertyAccessor = entityBinder.getPropertyAccessor( property );
 					propertyBinder = bindComponent(
 							inferredData,
 							propertyHolder,
 							propertyAccessor,
 							entityBinder,
 							isIdentifierMapper,
 							mappings,
 							isComponentEmbedded,
 							isId,
 							inheritanceStatePerClass,
 							referencedEntityName,
 							isOverridden ? ( Ejb3JoinColumn[] ) columns : null
 					);
 				}
 				else {
 					//provide the basic property mapping
 					boolean optional = true;
 					boolean lazy = false;
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
index 36a7ba82c9..36cc015132 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionSecondPass.java
@@ -1,96 +1,96 @@
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
 package org.hibernate.cfg;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Value;
 
 /**
  * Collection second pass
  *
  * @author Emmanuel Bernard
  */
 public abstract class CollectionSecondPass implements SecondPass {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionSecondPass.class.getName());
 
 	Mappings mappings;
 	Collection collection;
 	private Map localInheritedMetas;
 
 	public CollectionSecondPass(Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 		this.collection = collection;
 		this.mappings = mappings;
 		this.localInheritedMetas = inheritedMetas;
 	}
 
 	public CollectionSecondPass(Mappings mappings, Collection collection) {
 		this(mappings, collection, Collections.EMPTY_MAP);
 	}
 
 	public void doSecondPass(java.util.Map persistentClasses)
 			throws MappingException {
 		LOG.debugf( "Second pass for collection: %s", collection.getRole() );
 
 		secondPass( persistentClasses, localInheritedMetas ); // using local since the inheritedMetas at this point is not the correct map since it is always the empty map
 		collection.createAllKeys();
 
 		if ( LOG.isDebugEnabled() ) {
 			String msg = "Mapped collection key: " + columns( collection.getKey() );
 			if ( collection.isIndexed() )
 				msg += ", index: " + columns( ( (IndexedCollection) collection ).getIndex() );
 			if ( collection.isOneToMany() ) {
 				msg += ", one-to-many: "
 					+ ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			}
 			else {
 				msg += ", element: " + columns( collection.getElement() );
 			}
-			LOG.debugf( msg );
+			LOG.debug( msg );
 		}
 	}
 
 	abstract public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 			throws MappingException;
 
 	private static String columns(Value val) {
 		StringBuffer columns = new StringBuffer();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index dc58e3f6ee..fa841f5c64 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -389,3120 +389,3120 @@ public class Configuration implements Serializable {
 	 * Get the mapping for a particular collection role
 	 *
 	 * @param role a collection role
 	 * @return The collection mapping information
 	 */
 	public Collection getCollectionMapping(String role) {
 		return collections.get( role );
 	}
 
 	/**
 	 * Set a custom entity resolver. This entity resolver must be
 	 * set before addXXX(misc) call.
 	 * Default value is {@link org.hibernate.internal.util.xml.DTDEntityResolver}
 	 *
 	 * @param entityResolver entity resolver to use
 	 */
 	public void setEntityResolver(EntityResolver entityResolver) {
 		this.entityResolver = entityResolver;
 	}
 
 	public EntityResolver getEntityResolver() {
 		return entityResolver;
 	}
 
 	/**
 	 * Retrieve the user-supplied delegate to handle non-existent entity
 	 * scenarios.  May be null.
 	 *
 	 * @return The user-supplied delegate
 	 */
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	/**
 	 * Specify a user-supplied delegate to be used to handle scenarios where an entity could not be
 	 * located by specified id.  This is mainly intended for EJB3 implementations to be able to
 	 * control how proxy initialization errors should be handled...
 	 *
 	 * @param entityNotFoundDelegate The delegate to use
 	 */
 	public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.entityNotFoundDelegate = entityNotFoundDelegate;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates inability to locate or parse
 	 * the specified mapping file.
 	 * @see #addFile(java.io.File)
 	 */
 	public Configuration addFile(String xmlFile) throws MappingException {
 		return addFile( new File( xmlFile ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates inability to locate the specified mapping file.  Historically this could
 	 * have indicated a problem parsing the XML document, but that is now delayed until after {@link #buildMappings}
 	 */
 	public Configuration addFile(final File xmlFile) throws MappingException {
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		final String name =  xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 		add( inputSource, "file", name );
 		return this;
 	}
 
 	private XmlDocument add(InputSource inputSource, String originType, String originName) {
 		return add( inputSource, new OriginImpl( originType, originName ) );
 	}
 
 	private XmlDocument add(InputSource inputSource, Origin origin) {
 		XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument( entityResolver, inputSource, origin );
 		add( metadataXml );
 		return metadataXml;
 	}
 
 	public void add(XmlDocument metadataXml) {
 		if ( inSecondPass || !isOrmXml( metadataXml ) ) {
 			metadataSourceQueue.add( metadataXml );
 		}
 		else {
 			final MetadataProvider metadataProvider = ( (MetadataProviderInjector) reflectionManager ).getMetadataProvider();
 			JPAMetadataProvider jpaMetadataProvider = ( JPAMetadataProvider ) metadataProvider;
 			List<String> classNames = jpaMetadataProvider.getXMLContext().addDocument( metadataXml.getDocumentTree() );
 			for ( String className : classNames ) {
 				try {
 					metadataSourceQueue.add( reflectionManager.classForName( className, this.getClass() ) );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to load class defined in XML: " + className, e );
 				}
 			}
 		}
 	}
 
 	private static boolean isOrmXml(XmlDocument xmlDocument) {
 		return "entity-mappings".equals( xmlDocument.getDocumentTree().getRootElement().getName() );
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation
 	 * of the DOM structure of a particular mapping.  It is saved from a previous
 	 * call as a file with the name <tt>xmlFile + ".bin"</tt> where xmlFile is
 	 * the name of the original mapping file.
 	 * </p>
 	 * If a cached <tt>xmlFile + ".bin"</tt> exists and is newer than
 	 * <tt>xmlFile</tt> the <tt>".bin"</tt> file will be read directly. Otherwise
 	 * xmlFile is read and then serialized to <tt>xmlFile + ".bin"</tt> for use
 	 * the next time.
 	 *
 	 * @param xmlFile The cacheable mapping file to be added.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 */
 	public Configuration addCacheableFile(File xmlFile) throws MappingException {
 		File cachedFile = determineCachedDomFile( xmlFile );
 
 		try {
 			return addCacheableFileStrictly( xmlFile );
 		}
 		catch ( SerializationException e ) {
 			LOG.unableToDeserializeCache( cachedFile.getPath(), e );
 		}
 		catch ( FileNotFoundException e ) {
 			LOG.cachedFileNotFound( cachedFile.getPath(), e );
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
 			LOG.debugf( "Writing cache file for: %s to: %s", xmlFile, cachedFile );
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
 		}
 		catch ( Exception e ) {
 			LOG.unableToWriteCachedFile( cachedFile.getPath(), e.getMessage() );
 		}
 
 		return this;
 	}
 
 	private File determineCachedDomFile(File xmlFile) {
 		return new File( xmlFile.getAbsolutePath() + ".bin" );
 	}
 
 	/**
 	 * <b>INTENDED FOR TESTSUITE USE ONLY!</b>
 	 * <p/>
 	 * Much like {@link #addCacheableFile(File)} except that here we will fail immediately if
 	 * the cache version cannot be found or used for whatever reason
 	 *
 	 * @param xmlFile The xml file, not the bin!
 	 *
 	 * @return The dom "deserialized" from the cached file.
 	 *
 	 * @throws SerializationException Indicates a problem deserializing the cached dom tree
 	 * @throws FileNotFoundException Indicates that the cached file was not found or was not usable.
 	 */
 	public Configuration addCacheableFileStrictly(File xmlFile) throws SerializationException, FileNotFoundException {
 		final File cachedFile = determineCachedDomFile( xmlFile );
 
 		final boolean useCachedFile = xmlFile.exists()
 				&& cachedFile.exists()
 				&& xmlFile.lastModified() < cachedFile.lastModified();
 
 		if ( ! useCachedFile ) {
 			throw new FileNotFoundException( "Cached file could not be found or could not be used" );
 		}
 
 		LOG.readingCachedMappings( cachedFile );
 		Document document = ( Document ) SerializationHelper.deserialize( new FileInputStream( cachedFile ) );
 		add( new XmlDocumentImpl( document, "file", xmlFile.getAbsolutePath() ) );
 		return this;
 	}
 
 	/**
 	 * Add a cacheable mapping file.
 	 *
 	 * @param xmlFile The name of the file to be added.  This must be in a form
 	 * useable to simply construct a {@link java.io.File} instance.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public Configuration addCacheableFile(String xmlFile) throws MappingException {
 		return addCacheableFile( new File( xmlFile ) );
 	}
 
 
 	/**
 	 * Read mappings from a <tt>String</tt>
 	 *
 	 * @param xml an XML string
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates problems parsing the
 	 * given XML string
 	 */
 	public Configuration addXML(String xml) throws MappingException {
 		LOG.debugf( "Mapping XML:\n%s", xml );
 		final InputSource inputSource = new InputSource( new StringReader( xml ) );
 		add( inputSource, "string", "XML String" );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a <tt>URL</tt>
 	 *
 	 * @param url The url for the mapping document to be read.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the URL or processing
 	 * the mapping document.
 	 */
 	public Configuration addURL(URL url) throws MappingException {
 		final String urlExternalForm = url.toExternalForm();
 
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		try {
 			add( url.openStream(), "URL", urlExternalForm );
 		}
 		catch ( IOException e ) {
 			throw new InvalidMappingException( "Unable to open url stream [" + urlExternalForm + "]", "URL", urlExternalForm, e );
 		}
 		return this;
 	}
 
 	private XmlDocument add(InputStream inputStream, final String type, final String name) {
 		final InputSource inputSource = new InputSource( inputStream );
 		try {
 			return add( inputSource, type, name );
 		}
 		finally {
 			try {
 				inputStream.close();
 			}
 			catch ( IOException ignore ) {
 				LOG.trace( "Was unable to close input stream");
 			}
 		}
 	}
 
 	/**
 	 * Read mappings from a DOM <tt>Document</tt>
 	 *
 	 * @param doc The DOM document
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the DOM or processing
 	 * the mapping document.
 	 */
 	public Configuration addDocument(org.w3c.dom.Document doc) throws MappingException {
 		LOG.debugf( "Mapping Document:\n%s", doc );
 
 		final Document document = xmlHelper.createDOMReader().read( doc );
 		add( new XmlDocumentImpl( document, "unknown", null ) );
 
 		return this;
 	}
 
 	/**
 	 * Read mappings from an {@link java.io.InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the stream, or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		add( xmlInputStream, "input stream", null );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resource (i.e. classpath lookup).
 	 *
 	 * @param resourceName The resource name
 	 * @param classLoader The class loader to use.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		InputStream resourceInputStream = classLoader.getResourceAsStream( resourceName );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup)
 	 * trying different class loaders.
 	 *
 	 * @param resourceName The resource name
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
 		InputStream resourceInputStream = null;
 		if ( contextClassLoader != null ) {
 			resourceInputStream = contextClassLoader.getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			resourceInputStream = Environment.class.getClassLoader().getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class
 	 * named <tt>foo.bar.Foo</tt> is mapped by a file <tt>foo/bar/Foo.hbm.xml</tt>
 	 * which can be resolved as a classpath resource.
 	 *
 	 * @param persistentClass The mapped class
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addClass(Class persistentClass) throws MappingException {
 		String mappingResourceName = persistentClass.getName().replace( '.', '/' ) + ".hbm.xml";
 		LOG.readingMappingsFromResource( mappingResourceName );
 		return addResource( mappingResourceName, persistentClass.getClassLoader() );
 	}
 
 	/**
 	 * Read metadata from the annotations associated with this class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Configuration addAnnotatedClass(Class annotatedClass) {
 		XClass xClass = reflectionManager.toXClass( annotatedClass );
 		metadataSourceQueue.add( xClass );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
 	 * @param packageName java package name
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws MappingException in case there is an error in the mapping data
 	 */
 	public Configuration addPackage(String packageName) throws MappingException {
 		LOG.debugf( "Mapping Package %s", packageName );
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
 			LOG.unableToParseMetadata( packageName );
 			throw me;
 		}
 	}
 
 	/**
 	 * Read all mappings from a jar file
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addJar(File jar) throws MappingException {
 		LOG.searchingForMappingDocuments( jar.getName() );
 		JarFile jarFile = null;
 		try {
 			try {
 				jarFile = new JarFile( jar );
 			}
 			catch (IOException ioe) {
 				throw new InvalidMappingException(
 						"Could not read mapping documents from jar: " + jar.getName(), "jar", jar.getName(),
 						ioe
 				);
 			}
 			Enumeration jarEntries = jarFile.entries();
 			while ( jarEntries.hasMoreElements() ) {
 				ZipEntry ze = (ZipEntry) jarEntries.nextElement();
 				if ( ze.getName().endsWith( ".hbm.xml" ) ) {
 					LOG.foundMappingDocument( ze.getName() );
 					try {
 						addInputStream( jarFile.getInputStream( ze ) );
 					}
 					catch (Exception e) {
 						throw new InvalidMappingException(
 								"Could not read mapping documents from jar: " + jar.getName(),
 								"jar",
 								jar.getName(),
 								e
 						);
 					}
 				}
 			}
 		}
 		finally {
 			try {
 				if ( jarFile != null ) {
 					jarFile.close();
 				}
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseJar( ioe.getMessage() );
 			}
 		}
 
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addDirectory(File dir) throws MappingException {
 		File[] files = dir.listFiles();
 		for ( File file : files ) {
 			if ( file.isDirectory() ) {
 				addDirectory( file );
 			}
 			else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 				addFile( file );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Create a new <tt>Mappings</tt> to add class and collection mappings to.
 	 *
 	 * @return The created mappings
 	 */
 	public Mappings createMappings() {
 		return new MappingsImpl();
 	}
 
 
 	@SuppressWarnings({ "unchecked" })
 	private Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
 		TreeMap generators = new TreeMap();
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		for ( PersistentClass pc : classes.values() ) {
 			if ( !pc.isInherited() ) {
 				IdentifierGenerator ig = pc.getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						(RootClass) pc
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 				else if ( ig instanceof IdentifierGeneratorAggregator ) {
 					( (IdentifierGeneratorAggregator) ig ).registerPersistentGenerators( generators );
 				}
 			}
 		}
 
 		for ( Collection collection : collections.values() ) {
 			if ( collection.isIdentified() ) {
 				IdentifierGenerator ig = ( ( IdentifierCollection ) collection ).getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						null
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 			}
 		}
 
 		return generators.values().iterator();
 	}
 
 	/**
 	 * Generate DDL for dropping tables
 	 *
 	 * @param dialect The dialect for which to generate the drop script
 
 	 * @return The sequence of DDL commands to drop the schema objects
 
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		// drop them in reverse order in case db needs it done that way...
 		{
 			ListIterator itr = auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 			while ( itr.hasPrevious() ) {
 				AuxiliaryDatabaseObject object = (AuxiliaryDatabaseObject) itr.previous();
 				if ( object.appliesToDialect( dialect ) ) {
 					script.add( object.sqlDropString( dialect, defaultCatalog, defaultSchema ) );
 				}
 			}
 		}
 
 		if ( dialect.dropConstraints() ) {
 			Iterator itr = getTableMappings();
 			while ( itr.hasNext() ) {
 				Table table = (Table) itr.next();
 				if ( table.isPhysicalTable() ) {
 					Iterator subItr = table.getForeignKeyIterator();
 					while ( subItr.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subItr.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlDropString(
 											dialect,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 			}
 		}
 
 
 		Iterator itr = getTableMappings();
 		while ( itr.hasNext() ) {
 
 			Table table = (Table) itr.next();
 			if ( table.isPhysicalTable() ) {
 
 				/*Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
 						script.add( index.sqlDropString(dialect) );
 					}
 				}*/
 
 				script.add(
 						table.sqlDropString(
 								dialect,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 
 			}
 
 		}
 
 		itr = iterateGenerators( dialect );
 		while ( itr.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) itr.next() ).sqlDropStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 *
 	 * @return The sequence of DDL commands to create the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 				script.add(
 						table.sqlCreateString(
 								dialect,
 								mapping,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 					Iterator subIter = table.getUniqueKeyIterator();
 					while ( subIter.hasNext() ) {
 						UniqueKey uk = (UniqueKey) subIter.next();
 						String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 						if (constraintString != null) script.add( constraintString );
 					}
 				}
 
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlCreateString(
 											dialect, mapping,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) iter.next() ).sqlCreateStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : auxiliaryDatabaseObjects ) {
 			if ( auxiliaryDatabaseObject.appliesToDialect( dialect ) ) {
 				script.add( auxiliaryDatabaseObject.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 * @param databaseMetadata The database catalog information for the database to be updated; needed to work out what
 	 * should be created/altered
 	 *
 	 * @return The sequence of DDL commands to apply the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted()
 
 					);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							defaultCatalog,
 							defaultSchema
 						);
 					while ( subiter.hasNext() ) {
 						script.add( subiter.next() );
 					}
 				}
 
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						table.getSchema(),
 						table.getCatalog(),
 						table.isQuoted()
 					);
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || (
 									tableInfo.getForeignKeyMetadata( fk ) == null && (
 											//Icky workaround for MySQL bug:
 											!( dialect instanceof MySQLDialect ) ||
 													tableInfo.getIndexMetadata( fk.getName() ) == null
 										)
 								);
 							if ( create ) {
 								script.add(
 										fk.sqlCreateString(
 												dialect,
 												mapping,
 												defaultCatalog,
 												defaultSchema
 											)
 									);
 							}
 						}
 					}
 				}
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					final Index index = (Index) subIter.next();
 					// Skip if index already exists
 					if ( tableInfo != null && StringHelper.isNotEmpty( index.getName() ) ) {
 						final IndexMetadata meta = tableInfo.getIndexMetadata( index.getName() );
 						if ( meta != null ) {
 							continue;
 						}
 					}
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 							)
 					);
 				}
 
 //broken, 'cos we don't generate these with names in SchemaExport
 //				subIter = table.getUniqueKeyIterator();
 //				while ( subIter.hasNext() ) {
 //					UniqueKey uk = (UniqueKey) subIter.next();
 //					if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getFilterName() ) == null ) {
 //						script.add( uk.sqlCreateString(dialect, mapping) );
 //					}
 //				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				script.addAll( Arrays.asList( lines ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted());
 				if ( tableInfo == null ) {
 					throw new HibernateException( "Missing table: " + table.getName() );
 				}
 				else {
 					table.validateColumns( dialect, mapping, tableInfo );
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
 	}
 
 	private void validate() throws MappingException {
 		Iterator iter = classes.values().iterator();
 		while ( iter.hasNext() ) {
 			( (PersistentClass) iter.next() ).validate( mapping );
 		}
 		iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			( (Collection) iter.next() ).validate( mapping );
 		}
 	}
 
 	/**
 	 * Call this to ensure the mappings are fully compiled/built. Usefull to ensure getting
 	 * access to all information in the metamodel when calling e.g. getClassMappings().
 	 */
 	public void buildMappings() {
 		secondPassCompile();
 	}
 
 	protected void secondPassCompile() throws MappingException {
 		LOG.trace( "Starting secondPassCompile() processing" );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
 		}
 
 		// process cache queue
 		{
 			for ( CacheHolder holder : caches ) {
 				if ( holder.isClass ) {
 					applyCacheConcurrencyStrategy( holder );
 				}
 				else {
 					applyCollectionCacheConcurrencyStrategy( holder );
 				}
 			}
 			caches.clear();
 		}
 
 		try {
 			inSecondPass = true;
 			processSecondPassesOfType( PkDrivenByDefaultMapsIdSecondPass.class );
 			processSecondPassesOfType( SetSimpleValueTypeSecondPass.class );
 			processSecondPassesOfType( CopyIdentifierComponentSecondPass.class );
 			processFkSecondPassInOrder();
 			processSecondPassesOfType( CreateKeySecondPass.class );
 			processSecondPassesOfType( SecondaryTableSecondPass.class );
 
 			originalSecondPassCompile();
 
 			inSecondPass = false;
 		}
 		catch ( RecoverableException e ) {
 			//the exception was not recoverable after all
 			throw ( RuntimeException ) e.getCause();
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "key" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 	}
 
 	private void processSecondPassesOfType(Class<? extends SecondPass> type) {
 		Iterator iter = secondPasses.iterator();
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of simple value types first and remove them
 			if ( type.isInstance( sp ) ) {
 				sp.doSecondPass( classes );
 				iter.remove();
 			}
 		}
 	}
 
 	/**
 	 * Processes FKSecondPass instances trying to resolve any
 	 * graph circularity (ie PK made of a many to one linking to
 	 * an entity having a PK made of a ManyToOne ...).
 	 */
 	private void processFkSecondPassInOrder() {
-        LOG.debugf("Processing fk mappings (*ToOne and JoinedSubclass)");
+		LOG.debug("Processing fk mappings (*ToOne and JoinedSubclass)");
 		List<FkSecondPass> fkSecondPasses = getFKSecondPassesOnly();
 
 		if ( fkSecondPasses.size() == 0 ) {
 			return; // nothing to do here
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( FkSecondPass sp : fkSecondPasses ) {
 			if ( sp.isInPrimaryKey() ) {
 				String referenceEntityName = sp.getReferencedEntityName();
 				PersistentClass classMapping = getClassMapping( referenceEntityName );
 				String dependentTable = classMapping.getTable().getQuotedName();
 				if ( !isADependencyOf.containsKey( dependentTable ) ) {
 					isADependencyOf.put( dependentTable, new HashSet<FkSecondPass>() );
 				}
 				isADependencyOf.get( dependentTable ).add( sp );
 			}
 			else {
 				endOfQueueFkSecondPasses.add( sp );
 			}
 		}
 
 		// using the isADependencyOf map we order the FkSecondPass recursively instances into the right order for processing
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( classes );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 	}
 
 	/**
 	 * @return Returns a list of all <code>secondPasses</code> instances which are a instance of
 	 *         <code>FkSecondPass</code>.
 	 */
 	private List<FkSecondPass> getFKSecondPassesOnly() {
 		Iterator iter = secondPasses.iterator();
 		List<FkSecondPass> fkSecondPasses = new ArrayList<FkSecondPass>( secondPasses.size() );
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of fk before the others and remove them
 			if ( sp instanceof FkSecondPass ) {
 				fkSecondPasses.add( ( FkSecondPass ) sp );
 				iter.remove();
 			}
 		}
 		return fkSecondPasses;
 	}
 
 	/**
 	 * Recursively builds a list of FkSecondPass instances ready to be processed in this order.
 	 * Checking all dependencies recursively seems quite expensive, but the original code just relied
 	 * on some sort of table name sorting which failed in certain circumstances.
 	 * <p/>
 	 * See <tt>ANN-722</tt> and <tt>ANN-730</tt>
 	 *
 	 * @param orderedFkSecondPasses The list containing the <code>FkSecondPass<code> instances ready
 	 * for processing.
 	 * @param isADependencyOf Our lookup data structure to determine dependencies between tables
 	 * @param startTable Table name to start recursive algorithm.
 	 * @param currentTable The current table name used to check for 'new' dependencies.
 	 */
 	private void buildRecursiveOrderedFkSecondPasses(
 			List<FkSecondPass> orderedFkSecondPasses,
 			Map<String, Set<FkSecondPass>> isADependencyOf,
 			String startTable,
 			String currentTable) {
 
 		Set<FkSecondPass> dependencies = isADependencyOf.get( currentTable );
 
 		// bottom out
 		if ( dependencies == null || dependencies.size() == 0 ) {
 			return;
 		}
 
 		for ( FkSecondPass sp : dependencies ) {
 			String dependentTable = sp.getValue().getTable().getQuotedName();
 			if ( dependentTable.compareTo( startTable ) == 0 ) {
 				StringBuilder sb = new StringBuilder(
 						"Foreign key circularity dependency involving the following tables: "
 				);
 				throw new AnnotationException( sb.toString() );
 			}
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, startTable, dependentTable );
 			if ( !orderedFkSecondPasses.contains( sp ) ) {
 				orderedFkSecondPasses.add( 0, sp );
 			}
 		}
 	}
 
 	private void processEndOfQueue(List<FkSecondPass> endOfQueueFkSecondPasses) {
 		/*
 		 * If a second pass raises a recoverableException, queue it for next round
 		 * stop of no pass has to be processed or if the number of pass to processes
 		 * does not diminish between two rounds.
 		 * If some failing pass remain, raise the original exception
 		 */
 		boolean stopProcess = false;
 		RuntimeException originalException = null;
 		while ( !stopProcess ) {
 			List<FkSecondPass> failingSecondPasses = new ArrayList<FkSecondPass>();
 			Iterator<FkSecondPass> it = endOfQueueFkSecondPasses.listIterator();
 			while ( it.hasNext() ) {
 				final FkSecondPass pass = it.next();
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch ( RecoverableException e ) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = ( RuntimeException ) e.getCause();
 					}
 				}
 			}
 			stopProcess = failingSecondPasses.size() == 0 || failingSecondPasses.size() == endOfQueueFkSecondPasses.size();
 			endOfQueueFkSecondPasses = failingSecondPasses;
 		}
 		if ( endOfQueueFkSecondPasses.size() > 0 ) {
 			throw originalException;
 		}
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		UniqueKey uc;
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = normalizer.normalizeIdentifierQuoting( columnNames[index] );
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( logicalColumnName, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( logicalColumnName ) );
 			}
 		}
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uc = table.getOrCreateUniqueKey( keyName );
 				uc.addColumn( table.getColumn( column ) );
 				unbound.remove( column );
 			}
 		}
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": database column " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
-		LOG.debugf( "Processing extends queue" );
+		LOG.debug( "Processing extends queue" );
 		processExtendsQueue();
 
-		LOG.debugf( "Processing collection mappings" );
+		LOG.debug( "Processing collection mappings" );
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
-		LOG.debugf( "Processing native query and ResultSetMapping mappings" );
+		LOG.debug( "Processing native query and ResultSetMapping mappings" );
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
-		LOG.debugf( "Processing association property references" );
+		LOG.debug( "Processing association property references" );
 
 		itr = propertyReferences.iterator();
 		while ( itr.hasNext() ) {
 			Mappings.PropertyReference upr = (Mappings.PropertyReference) itr.next();
 
 			PersistentClass clazz = getClassMapping( upr.referencedClass );
 			if ( clazz == null ) {
 				throw new MappingException(
 						"property-ref to unmapped class: " +
 						upr.referencedClass
 					);
 			}
 
 			Property prop = clazz.getReferencedProperty( upr.propertyName );
 			if ( upr.unique ) {
 				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 
 		//TODO: Somehow add the newly created foreign keys to the internal collection
 
-		LOG.debugf( "Processing foreign key constraints" );
+		LOG.debug( "Processing foreign key constraints" );
 
 		itr = getTableMappings();
 		Set done = new HashSet();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
-		LOG.debugf( "Processing extends queue" );
+		LOG.debug( "Processing extends queue" );
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
 			StringBuffer buf = new StringBuffer( "Following super classes referenced in extends not found: " );
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = ( ExtendsQueueEntry ) iterator.next();
 				buf.append( entry.getExplicitName() );
 				if ( entry.getMappingPackage() != null ) {
 					buf.append( "[" ).append( entry.getMappingPackage() ).append( "]" );
 				}
 				if ( iterator.hasNext() ) {
 					buf.append( "," );
 				}
 			}
 			throw new MappingException( buf.toString() );
 		}
 
 		return added;
 	}
 
 	protected ExtendsQueueEntry findPossibleExtends() {
 		Iterator<ExtendsQueueEntry> itr = extendsQueue.keySet().iterator();
 		while ( itr.hasNext() ) {
 			final ExtendsQueueEntry entry = itr.next();
 			boolean found = getClassMapping( entry.getExplicitName() ) != null
 					|| getClassMapping( HbmBinder.getClassName( entry.getExplicitName(), entry.getMappingPackage() ) ) != null;
 			if ( found ) {
 				itr.remove();
 				return entry;
 			}
 		}
 		return null;
 	}
 
 	protected void secondPassCompileForeignKeys(Table table, Set done) throws MappingException {
 		table.createForeignKeys();
 		Iterator iter = table.getForeignKeyIterator();
 		while ( iter.hasNext() ) {
 
 			ForeignKey fk = (ForeignKey) iter.next();
 			if ( !done.contains( fk ) ) {
 				done.add( fk );
 				final String referencedEntityName = fk.getReferencedEntityName();
 				if ( referencedEntityName == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" does not specify the referenced entity"
 						);
 				}
 				LOG.debugf( "Resolving reference to class: %s", referencedEntityName );
 				PersistentClass referencedClass = classes.get( referencedEntityName );
 				if ( referencedClass == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" refers to an unmapped class: " +
 							referencedEntityName
 						);
 				}
 				if ( referencedClass.isJoinedSubclass() ) {
 					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done );
 				}
 				fk.setReferencedTable( referencedClass.getTable() );
 				fk.alignColumns();
 			}
 		}
 	}
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		LOG.debugf( "Preparing to build session factory with filters : %s", filterDefinitions );
 
 		secondPassCompile();
 		if ( !metadataSourceQueue.isEmpty() ) {
 			LOG.incompleteMappingMetadataCacheProcessing();
 		}
 
 		validate();
 
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
 		Settings settings = buildSettings( copy, serviceRegistry );
 
 		return new SessionFactoryImpl(
 				this,
 				mapping,
 				serviceRegistry,
 				settings,
 				sessionFactoryObserver
 			);
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 *
 	 * @deprecated Use {@link #buildSessionFactory(ServiceRegistry)} instead
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		final ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder()
 				.applySettings( properties )
 				.buildServiceRegistry();
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	/**
 	 * Rterieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory) built}
 	 * {@link SessionFactory}.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setInterceptor(Interceptor interceptor) {
 		this.interceptor = interceptor;
 		return this;
 	}
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
 	}
 
 	/**
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value curently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		return properties.getProperty( propertyName );
 	}
 
 	/**
 	 * Specify a completely new set of properties
 	 *
 	 * @param properties The new set of properties
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperties(Properties properties) {
 		this.properties = properties;
 		return this;
 	}
 
 	/**
 	 * Add the given properties to ours.
 	 *
 	 * @param extraProperties The properties to add.
 	 *
 	 * @return this for method chaining
 	 *
 	 */
 	public Configuration addProperties(Properties extraProperties) {
 		this.properties.putAll( extraProperties );
 		return this;
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for ethod chaining
 	 */
 	public Configuration mergeProperties(Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( this.properties.containsKey( entry.getKey() ) ) {
 				continue;
 			}
 			this.properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
 		}
 		return this;
 	}
 
 	/**
 	 * Set a property value by name
 	 *
 	 * @param propertyName The name of the property to set
 	 * @param value The new property value
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperty(String propertyName, String value) {
 		properties.setProperty( propertyName, value );
 		return this;
 	}
 
 	private void addProperties(Element parent) {
 		Iterator itr = parent.elementIterator( "property" );
 		while ( itr.hasNext() ) {
 			Element node = (Element) itr.next();
 			String name = node.attributeValue( "name" );
 			String value = node.getText().trim();
 			LOG.debugf( "%s=%s", name, value );
 			properties.setProperty( name, value );
 			if ( !name.startsWith( "hibernate" ) ) {
 				properties.setProperty( "hibernate." + name, value );
 			}
 		}
 		Environment.verifyProperties( properties );
 	}
 
 	/**
 	 * Use the mappings and properties specified in an application resource named <tt>hibernate.cfg.xml</tt>.
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find <tt>hibernate.cfg.xml</tt>
 	 *
 	 * @see #configure(String)
 	 */
 	public Configuration configure() throws HibernateException {
 		configure( "/hibernate.cfg.xml" );
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 * <p/>
 	 * The resource is found via {@link #getConfigurationInputStream}
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(String resource) throws HibernateException {
 		LOG.configuringFromResource( resource );
 		InputStream stream = getConfigurationInputStream( resource );
 		return doConfigure( stream, resource );
 	}
 
 	/**
 	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
 	 * by subclasses to allow the configuration to be located by some arbitrary
 	 * mechanism.
 	 * <p/>
 	 * By default here we use classpath resource resolution
 	 *
 	 * @param resource The resource to locate
 	 *
 	 * @return The stream
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
 		LOG.configurationResource( resource );
 		return ConfigHelper.getResourceAsStream( resource );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(URL url) throws HibernateException {
 		LOG.configuringFromUrl( url );
 		try {
 			return doConfigure( url.openStream(), url.toString() );
 		}
 		catch (IOException ioe) {
 			throw new HibernateException( "could not configure from URL: " + url, ioe );
 		}
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application file. The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param configFile File from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the file
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
 		LOG.configuringFromFile( configFile.getName() );
 		try {
 			return doConfigure( new FileInputStream( configFile ), configFile.toString() );
 		}
 		catch (FileNotFoundException fnfe) {
 			throw new HibernateException( "could not find file: " + configFile, fnfe );
 		}
 	}
 
 	/**
 	 * Configure this configuration's state from the contents of the given input stream.  The expectation is that
 	 * the stream contents represent an XML document conforming to the Hibernate Configuration DTD.  See
 	 * {@link #doConfigure(Document)} for further details.
 	 *
 	 * @param stream The input stream from which to read
 	 * @param resourceName The name to use in warning/error messages
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem reading the stream contents.
 	 */
 	protected Configuration doConfigure(InputStream stream, String resourceName) throws HibernateException {
 		try {
 			List errors = new ArrayList();
 			Document document = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errors.size() != 0 ) {
 				throw new MappingException( "invalid configuration", (Throwable) errors.get( 0 ) );
 			}
 			doConfigure( document );
 		}
 		catch (DocumentException e) {
 			throw new HibernateException( "Could not parse configuration: " + resourceName, e );
 		}
 		finally {
 			try {
 				stream.close();
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseInputStreamForResource( resourceName, ioe );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given XML document.
 	 * The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param document an XML document from which you wish to load the configuration
 	 * @return A configuration configured via the <tt>Document</tt>
 	 * @throws HibernateException if there is problem in accessing the file.
 	 */
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
 		LOG.configuringFromXmlDocument();
 		return doConfigure( xmlHelper.createDOMReader().read( document ) );
 	}
 
 	/**
 	 * Parse a dom4j document conforming to the Hibernate Configuration DTD (<tt>hibernate-configuration-3.0.dtd</tt>)
 	 * and use its information to configure this {@link Configuration}'s state
 	 *
 	 * @param doc The dom4j document
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem performing the configuration task
 	 */
 	protected Configuration doConfigure(Document doc) throws HibernateException {
 		Element sfNode = doc.getRootElement().element( "session-factory" );
 		String name = sfNode.attributeValue( "name" );
 		if ( name != null ) {
 			properties.setProperty( Environment.SESSION_FACTORY_NAME, name );
 		}
 		addProperties( sfNode );
 		parseSessionFactory( sfNode, name );
 
 		Element secNode = doc.getRootElement().element( "security" );
 		if ( secNode != null ) {
 			parseSecurity( secNode );
 		}
 
 		LOG.configuredSessionFactory( name );
 		LOG.debugf( "Properties: %s", properties );
 
 		return this;
 	}
 
 
 	private void parseSessionFactory(Element sfNode, String name) {
 		Iterator elements = sfNode.elementIterator();
 		while ( elements.hasNext() ) {
 			Element subelement = (Element) elements.next();
 			String subelementName = subelement.getName();
 			if ( "mapping".equals( subelementName ) ) {
 				parseMappingElement( subelement, name );
 			}
 			else if ( "class-cache".equals( subelementName ) ) {
 				String className = subelement.attributeValue( "class" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? className : regionNode.getValue();
 				boolean includeLazy = !"non-lazy".equals( subelement.attributeValue( "include" ) );
 				setCacheConcurrencyStrategy( className, subelement.attributeValue( "usage" ), region, includeLazy );
 			}
 			else if ( "collection-cache".equals( subelementName ) ) {
 				String role = subelement.attributeValue( "collection" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? role : regionNode.getValue();
 				setCollectionCacheConcurrencyStrategy( role, subelement.attributeValue( "usage" ), region );
 			}
 		}
 	}
 
 	private void parseMappingElement(Element mappingElement, String name) {
 		final Attribute resourceAttribute = mappingElement.attribute( "resource" );
 		final Attribute fileAttribute = mappingElement.attribute( "file" );
 		final Attribute jarAttribute = mappingElement.attribute( "jar" );
 		final Attribute packageAttribute = mappingElement.attribute( "package" );
 		final Attribute classAttribute = mappingElement.attribute( "class" );
 
 		if ( resourceAttribute != null ) {
 			final String resourceName = resourceAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named resource [%s] for mapping", name, resourceName );
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named file [%s] for mapping", name, fileName );
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName );
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named package [%s] for mapping", name, packageName );
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named class [%s] for mapping", name, className );
 			try {
 				addAnnotatedClass( ReflectHelper.classForName( className ) );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Unable to load class [ " + className + "] declared in Hibernate configuration <mapping/> entry",
 						e
 				);
 			}
 		}
 		else {
 			throw new MappingException( "<mapping> element in configuration specifies no known attributes" );
 		}
 	}
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 		LOG.jaccContextId( contextId );
 		JACCConfiguration jcfg = new JACCConfiguration( contextId );
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			Element grantElement = (Element) grantElements.next();
 			String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jcfg.addPermission(
 						grantElement.attributeValue( "role" ),
 						grantElement.attributeValue( "entity-name" ),
 						grantElement.attributeValue( "actions" )
 					);
 			}
 		}
 	}
 
 	RootClass getRootClassMapping(String clazz) throws MappingException {
 		try {
 			return (RootClass) getClassMapping( clazz );
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "You may only specify a cache for root <class> mappings" );
 		}
 	}
 
 	/**
 	 * Set up a cache for an entity class
 	 *
 	 * @param entityName The name of the entity to which we shoudl associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, entityName );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for an entity class, giving an explicit region name
 	 *
 	 * @param entityName The name of the entity to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy, String region) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, region, true );
 		return this;
 	}
 
 	public void setCacheConcurrencyStrategy(
 			String entityName,
 			String concurrencyStrategy,
 			String region,
 			boolean cacheLazyProperty) throws MappingException {
 		caches.add( new CacheHolder( entityName, concurrencyStrategy, region, true, cacheLazyProperty ) );
 	}
 
 	private void applyCacheConcurrencyStrategy(CacheHolder holder) {
 		RootClass rootClass = getRootClassMapping( holder.role );
 		if ( rootClass == null ) {
 			throw new MappingException( "Cannot cache an unknown entity: " + holder.role );
 		}
 		rootClass.setCacheConcurrencyStrategy( holder.usage );
 		rootClass.setCacheRegionName( holder.region );
 		rootClass.setLazyPropertiesCacheable( holder.cacheLazy );
 	}
 
 	/**
 	 * Set up a cache for a collection role
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy) {
 		setCollectionCacheConcurrencyStrategy( collectionRole, concurrencyStrategy, collectionRole );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for a collection role, giving an explicit region name
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region) {
 		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
 	}
 
 	private void applyCollectionCacheConcurrencyStrategy(CacheHolder holder) {
 		Collection collection = getCollectionMapping( holder.role );
 		if ( collection == null ) {
 			throw new MappingException( "Cannot cache an unknown collection: " + holder.role );
 		}
 		collection.setCacheConcurrencyStrategy( holder.usage );
 		collection.setCacheRegionName( holder.region );
 	}
 
 	/**
 	 * Get the query language imports
 	 *
 	 * @return a mapping from "import" names to fully qualified class names
 	 */
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	/**
 	 * Create an object-oriented view of the configuration properties
 	 *
 	 * @return The build settings
 	 */
 	public Settings buildSettings(ServiceRegistry serviceRegistry) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
 		return buildSettingsInternal( clone, serviceRegistry );
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) throws HibernateException {
 		return buildSettingsInternal( props, serviceRegistry );
 	}
 
 	private Settings buildSettingsInternal(Properties props, ServiceRegistry serviceRegistry) {
 		final Settings settings = settingsFactory.buildSettings( props, serviceRegistry );
 		settings.setEntityTuplizerFactory( this.getEntityTuplizerFactory() );
 //		settings.setComponentTuplizerFactory( this.getComponentTuplizerFactory() );
 		return settings;
 	}
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	/**
 	 * Set a custom naming strategy
 	 *
 	 * @param namingStrategy the NamingStrategy to set
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		this.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this configuration.
 	 *
 	 * @return This configuration's IdentifierGeneratorFactory.
 	 */
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	public Mapping buildMapping() {
 		return new Mapping() {
 			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 				return identifierGeneratorFactory;
 			}
 
 			/**
 			 * Returns the identifier type of a mapped class
 			 */
 			public Type getIdentifierType(String entityName) throws MappingException {
 				PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				return pc.getIdentifier().getType();
 			}
 
 			public String getIdentifierPropertyName(String entityName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				if ( !pc.hasIdentifierProperty() ) {
 					return null;
 				}
 				return pc.getIdentifierProperty().getName();
 			}
 
 			public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				Property prop = pc.getReferencedProperty( propertyName );
 				if ( prop == null ) {
 					throw new MappingException(
 							"property not known: " +
 							entityName + '.' + propertyName
 						);
 				}
 				return prop.getType();
 			}
 		};
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		//we need  reflectionManager before reading the other components (MetadataSourceQueue in particular)
 		final MetadataProvider metadataProvider = (MetadataProvider) ois.readObject();
 		this.mapping = buildMapping();
 		xmlHelper = new XMLHelper();
 		createReflectionManager(metadataProvider);
 		ois.defaultReadObject();
 	}
 
 	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 		//We write MetadataProvider first as we need  reflectionManager before reading the other components
 		final MetadataProvider metadataProvider = ( ( MetadataProviderInjector ) reflectionManager ).getMetadataProvider();
 		out.writeObject( metadataProvider );
 		out.defaultWriteObject();
 	}
 
 	private void createReflectionManager() {
 		createReflectionManager( new JPAMetadataProvider() );
 	}
 
 	private void createReflectionManager(MetadataProvider metadataProvider) {
 		reflectionManager = new JavaReflectionManager();
 		( ( MetadataProviderInjector ) reflectionManager ).setMetadataProvider( metadataProvider );
 	}
 
 	public Map getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		filterDefinitions.put( definition.getFilterName(), definition );
 	}
 
 	public Iterator iterateFetchProfiles() {
 		return fetchProfiles.values().iterator();
 	}
 
 	public void addFetchProfile(FetchProfile fetchProfile) {
 		fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		auxiliaryDatabaseObjects.add( object );
 	}
 
 	public Map getSqlFunctions() {
 		return sqlFunctions;
 	}
 
 	public void addSqlFunction(String functionName, SQLFunction function) {
 		sqlFunctions.put( functionName, function );
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public void registerTypeOverride(BasicType type) {
 		getTypeResolver().registerTypeOverride( type );
 	}
 
 
 	public void registerTypeOverride(UserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public void registerTypeOverride(CompositeUserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
 	protected class MappingsImpl implements ExtendedMappings, Serializable {
 
 		private String schemaName;
 
 		public String getSchemaName() {
 			return schemaName;
 		}
 
 		public void setSchemaName(String schemaName) {
 			this.schemaName = schemaName;
 		}
 
 
 		private String catalogName;
 
 		public String getCatalogName() {
 			return catalogName;
 		}
 
 		public void setCatalogName(String catalogName) {
 			this.catalogName = catalogName;
 		}
 
 
 		private String defaultPackage;
 
 		public String getDefaultPackage() {
 			return defaultPackage;
 		}
 
 		public void setDefaultPackage(String defaultPackage) {
 			this.defaultPackage = defaultPackage;
 		}
 
 
 		private boolean autoImport;
 
 		public boolean isAutoImport() {
 			return autoImport;
 		}
 
 		public void setAutoImport(boolean autoImport) {
 			this.autoImport = autoImport;
 		}
 
 
 		private boolean defaultLazy;
 
 		public boolean isDefaultLazy() {
 			return defaultLazy;
 		}
 
 		public void setDefaultLazy(boolean defaultLazy) {
 			this.defaultLazy = defaultLazy;
 		}
 
 
 		private String defaultCascade;
 
 		public String getDefaultCascade() {
 			return defaultCascade;
 		}
 
 		public void setDefaultCascade(String defaultCascade) {
 			this.defaultCascade = defaultCascade;
 		}
 
 
 		private String defaultAccess;
 
 		public String getDefaultAccess() {
 			return defaultAccess;
 		}
 
 		public void setDefaultAccess(String defaultAccess) {
 			this.defaultAccess = defaultAccess;
 		}
 
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		public void setNamingStrategy(NamingStrategy namingStrategy) {
 			Configuration.this.namingStrategy = namingStrategy;
 		}
 
 		public TypeResolver getTypeResolver() {
 			return typeResolver;
 		}
 
 		public Iterator<PersistentClass> iterateClasses() {
 			return classes.values().iterator();
 		}
 
 		public PersistentClass getClass(String entityName) {
 			return classes.get( entityName );
 		}
 
 		public PersistentClass locatePersistentClassByEntityName(String entityName) {
 			PersistentClass persistentClass = classes.get( entityName );
 			if ( persistentClass == null ) {
 				String actualEntityName = imports.get( entityName );
 				if ( StringHelper.isNotEmpty( actualEntityName ) ) {
 					persistentClass = classes.get( actualEntityName );
 				}
 			}
 			return persistentClass;
 		}
 
 		public void addClass(PersistentClass persistentClass) throws DuplicateMappingException {
 			Object old = classes.put( persistentClass.getEntityName(), persistentClass );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "class/entity", persistentClass.getEntityName() );
 			}
 		}
 
 		public void addImport(String entityName, String rename) throws DuplicateMappingException {
 			String existing = imports.put( rename, entityName );
 			if ( existing != null ) {
                 if (existing.equals(entityName)) LOG.duplicateImport(entityName, rename);
                 else throw new DuplicateMappingException("duplicate import: " + rename + " refers to both " + entityName + " and "
                                                          + existing + " (try using auto-import=\"false\")", "import", rename);
 			}
 		}
 
 		public Collection getCollection(String role) {
 			return collections.get( role );
 		}
 
 		public Iterator<Collection> iterateCollections() {
 			return collections.values().iterator();
 		}
 
 		public void addCollection(Collection collection) throws DuplicateMappingException {
 			Object old = collections.put( collection.getRole(), collection );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "collection role", collection.getRole() );
 			}
 		}
 
 		public Table getTable(String schema, String catalog, String name) {
 			String key = Table.qualify(catalog, schema, name);
 			return tables.get(key);
 		}
 
 		public Iterator<Table> iterateTables() {
 			return tables.values().iterator();
 		}
 
 		public Table addTable(
 				String schema,
 				String catalog,
 				String name,
 				String subselect,
 				boolean isAbstract) {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify( catalog, schema, name ) : subselect;
 			Table table = tables.get( key );
 
 			if ( table == null ) {
 				table = new Table();
 				table.setAbstract( isAbstract );
 				table.setName( name );
 				table.setSchema( schema );
 				table.setCatalog( catalog );
 				table.setSubselect( subselect );
 				tables.put( key, table );
 			}
 			else {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 			}
 
 			return table;
 		}
 
 		public Table addDenormalizedTable(
 				String schema,
 				String catalog,
 				String name,
 				boolean isAbstract,
 				String subselect,
 				Table includedTable) throws DuplicateMappingException {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify(catalog, schema, name) : subselect;
 			if ( tables.containsKey( key ) ) {
 				throw new DuplicateMappingException( "table", name );
 			}
 
 			Table table = new DenormalizedTable( includedTable );
 			table.setAbstract( isAbstract );
 			table.setName( name );
 			table.setSchema( schema );
 			table.setCatalog( catalog );
 			table.setSubselect( subselect );
 
 			tables.put( key, table );
 			return table;
 		}
 
 		public NamedQueryDefinition getQuery(String name) {
 			return namedQueries.get( name );
 		}
 
 		public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedQueryNames.contains( name ) ) {
 				applyQuery( name, query );
 			}
 		}
 
 		private void applyQuery(String name, NamedQueryDefinition query) {
 			checkQueryName( name );
 			namedQueries.put( name.intern(), query );
 		}
 
 		private void checkQueryName(String name) throws DuplicateMappingException {
 			if ( namedQueries.containsKey( name ) || namedSqlQueries.containsKey( name ) ) {
 				throw new DuplicateMappingException( "query", name );
 			}
 		}
 
 		public void addDefaultQuery(String name, NamedQueryDefinition query) {
 			applyQuery( name, query );
 			defaultNamedQueryNames.add( name );
 		}
 
 		public NamedSQLQueryDefinition getSQLQuery(String name) {
 			return namedSqlQueries.get( name );
 		}
 
 		public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedNativeQueryNames.contains( name ) ) {
 				applySQLQuery( name, query );
 			}
 		}
 
 		private void applySQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			checkQueryName( name );
 			namedSqlQueries.put( name.intern(), query );
 		}
 
 		public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query) {
 			applySQLQuery( name, query );
 			defaultNamedNativeQueryNames.add( name );
 		}
 
 		public ResultSetMappingDefinition getResultSetMapping(String name) {
 			return sqlResultSetMappings.get(name);
 		}
 
 		public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			if ( !defaultSqlResultSetMappingNames.contains( sqlResultSetMapping.getName() ) ) {
 				applyResultSetMapping( sqlResultSetMapping );
 			}
 		}
 
 		public void applyResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			Object old = sqlResultSetMappings.put( sqlResultSetMapping.getName(), sqlResultSetMapping );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "resultSet",  sqlResultSetMapping.getName() );
 			}
 		}
 
 		public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 			final String name = definition.getName();
 			if ( !defaultSqlResultSetMappingNames.contains( name ) && getResultSetMapping( name ) != null ) {
 				removeResultSetMapping( name );
 			}
 			applyResultSetMapping( definition );
 			defaultSqlResultSetMappingNames.add( name );
 		}
 
 		protected void removeResultSetMapping(String name) {
 			sqlResultSetMappings.remove( name );
 		}
 
 		public TypeDef getTypeDef(String typeName) {
 			return typeDefs.get( typeName );
 		}
 
 		public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
 			TypeDef def = new TypeDef( typeClass, paramMap );
 			typeDefs.put( typeName, def );
 			LOG.debugf( "Added %s with class %s", typeName, typeClass );
 		}
 
 		public Map getFilterDefinitions() {
 			return filterDefinitions;
 		}
 
 		public FilterDefinition getFilterDefinition(String name) {
 			return filterDefinitions.get( name );
 		}
 
 		public void addFilterDefinition(FilterDefinition definition) {
 			filterDefinitions.put( definition.getFilterName(), definition );
 		}
 
 		public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source) {
 			FetchProfile profile = fetchProfiles.get( name );
 			if ( profile == null ) {
 				profile = new FetchProfile( name, source );
 				fetchProfiles.put( name, profile );
 			}
 			return profile;
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects() {
 			return iterateAuxiliaryDatabaseObjects();
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects() {
 			return auxiliaryDatabaseObjects.iterator();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse() {
 			return iterateAuxiliaryDatabaseObjectsInReverse();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse() {
 			return auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 		}
 
 		public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 			auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 		}
 
 		/**
 		 * Internal struct used to help track physical table names to logical table names.
 		 */
 		private class TableDescription implements Serializable {
 			final String logicalName;
 			final Table denormalizedSupertable;
 
 			TableDescription(String logicalName, Table denormalizedSupertable) {
 				this.logicalName = logicalName;
 				this.denormalizedSupertable = denormalizedSupertable;
 			}
 		}
 
 		public String getLogicalTableName(Table table) throws MappingException {
 			return getLogicalTableName( table.getQuotedSchema(), table.getCatalog(), table.getQuotedName() );
 		}
 
 		private String getLogicalTableName(String schema, String catalog, String physicalName) throws MappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription descriptor = (TableDescription) tableNameBinding.get( key );
 			if (descriptor == null) {
 				throw new MappingException( "Unable to find physical table: " + physicalName);
 			}
 			return descriptor.logicalName;
 		}
 
 		public void addTableBinding(
 				String schema,
 				String catalog,
 				String logicalName,
 				String physicalName,
 				Table denormalizedSuperTable) throws DuplicateMappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription tableDescription = new TableDescription( logicalName, denormalizedSuperTable );
 			TableDescription oldDescriptor = ( TableDescription ) tableNameBinding.put( key, tableDescription );
 			if ( oldDescriptor != null && ! oldDescriptor.logicalName.equals( logicalName ) ) {
 				//TODO possibly relax that
 				throw new DuplicateMappingException(
 						"Same physical table name [" + physicalName + "] references several logical table names: [" +
 								oldDescriptor.logicalName + "], [" + logicalName + ']',
 						"table",
 						physicalName
 				);
 			}
 		}
 
 		private String buildTableNameKey(String schema, String catalog, String finalName) {
 			StringBuffer keyBuilder = new StringBuffer();
 			if (schema != null) keyBuilder.append( schema );
 			keyBuilder.append( ".");
 			if (catalog != null) keyBuilder.append( catalog );
 			keyBuilder.append( ".");
 			keyBuilder.append( finalName );
 			return keyBuilder.toString();
 		}
 
 		/**
 		 * Internal struct used to maintain xref between physical and logical column
 		 * names for a table.  Mainly this is used to ensure that the defined
 		 * {@link NamingStrategy} is not creating duplicate column names.
 		 */
 		private class TableColumnNameBinding implements Serializable {
 			private final String tableName;
 			private Map/*<String, String>*/ logicalToPhysical = new HashMap();
 			private Map/*<String, String>*/ physicalToLogical = new HashMap();
 
 			private TableColumnNameBinding(String tableName) {
 				this.tableName = tableName;
 			}
 
 			public void addBinding(String logicalName, Column physicalColumn) {
 				bindLogicalToPhysical( logicalName, physicalColumn );
 				bindPhysicalToLogical( logicalName, physicalColumn );
 			}
 
 			private void bindLogicalToPhysical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String logicalKey = logicalName.toLowerCase();
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingPhysicalName = ( String ) logicalToPhysical.put( logicalKey, physicalName );
 				if ( existingPhysicalName != null ) {
 					boolean areSamePhysicalColumn = physicalColumn.isQuoted()
 							? existingPhysicalName.equals( physicalName )
 							: existingPhysicalName.equalsIgnoreCase( physicalName );
 					if ( ! areSamePhysicalColumn ) {
 						throw new DuplicateMappingException(
 								" Table [" + tableName + "] contains logical column name [" + logicalName
 										+ "] referenced by multiple physical column names: [" + existingPhysicalName
 										+ "], [" + physicalName + "]",
 								"column-binding",
 								tableName + "." + logicalName
 						);
 					}
 				}
 			}
 
 			private void bindPhysicalToLogical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingLogicalName = ( String ) physicalToLogical.put( physicalName, logicalName );
 				if ( existingLogicalName != null && ! existingLogicalName.equals( logicalName ) ) {
 					throw new DuplicateMappingException(
 							" Table [" + tableName + "] contains phyical column name [" + physicalName
 									+ "] represented by different logical column names: [" + existingLogicalName
 									+ "], [" + logicalName + "]",
 							"column-binding",
 							tableName + "." + physicalName
 					);
 				}
 			}
 		}
 
 		public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException {
 			TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( table );
 			if ( binding == null ) {
 				binding = new TableColumnNameBinding( table.getName() );
 				columnNameBindingPerTable.put( table, binding );
 			}
 			binding.addBinding( logicalName, physicalColumn );
 		}
 
 		public String getPhysicalColumnName(String logicalName, Table table) throws MappingException {
 			logicalName = logicalName.toLowerCase();
 			String finalName = null;
 			Table currentTable = table;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					finalName = ( String ) binding.logicalToPhysical.get( logicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
 				);
 				TableDescription description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			} while ( finalName == null && currentTable != null );
 
 			if ( finalName == null ) {
 				throw new MappingException(
 						"Unable to find column with logical name " + logicalName + " in table " + table.getName()
 				);
 			}
 			return finalName;
 		}
 
 		public String getLogicalColumnName(String physicalName, Table table) throws MappingException {
 			String logical = null;
 			Table currentTable = table;
 			TableDescription description = null;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					logical = ( String ) binding.physicalToLogical.get( physicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
 				);
 				description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			}
 			while ( logical == null && currentTable != null && description != null );
 			if ( logical == null ) {
 				throw new MappingException(
 						"Unable to find logical column name from physical name "
 								+ physicalName + " in table " + table.getName()
 				);
 			}
 			return logical;
 		}
 
 		public void addSecondPass(SecondPass sp) {
 			addSecondPass( sp, false );
 		}
 
 		public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue) {
 			if ( onTopOfTheQueue ) {
 				secondPasses.add( 0, sp );
 			}
 			else {
 				secondPasses.add( sp );
 			}
 		}
 
 		public void addPropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, false ) );
 		}
 
 		public void addUniquePropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, true ) );
 		}
 
 		public void addToExtendsQueue(ExtendsQueueEntry entry) {
 			extendsQueue.put( entry, null );
 		}
 
 		public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 			return identifierGeneratorFactory;
 		}
 
 		public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
 			mappedSuperClasses.put( type, mappedSuperclass );
 		}
 
 		public MappedSuperclass getMappedSuperclass(Class type) {
 			return mappedSuperClasses.get( type );
 		}
 
 		public ObjectNameNormalizer getObjectNameNormalizer() {
 			return normalizer;
 		}
 
 		public Properties getConfigurationProperties() {
 			return properties;
 		}
 
 
 		private Boolean useNewGeneratorMappings;
 
 		public void addDefaultGenerator(IdGenerator generator) {
 			this.addGenerator( generator );
 			defaultNamedGenerators.add( generator.getName() );
 		}
 
 		public boolean isInSecondPass() {
 			return inSecondPass;
 		}
 
 		public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( property.getProperty().getAnnotation( MapsId.class ).value(), property );
 		}
 
 		public boolean isSpecjProprietarySyntaxEnabled() {
 			return specjProprietarySyntaxEnabled;
 		}
 
 		public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( mapsIdValue, property );
 		}
 
 		public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithIdAndToOne.put( entityType, map );
 			}
 			map.put( property.getPropertyName(), property );
 		}
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties().getProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
 		}
 
 		public IdGenerator getGenerator(String name) {
 			return getGenerator( name, null );
 		}
 
 		public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators) {
 			if ( localGenerators != null ) {
 				IdGenerator result = localGenerators.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return namedGenerators.get( name );
 		}
 
 		public void addGenerator(IdGenerator generator) {
 			if ( !defaultNamedGenerators.contains( generator.getName() ) ) {
 				IdGenerator old = namedGenerators.put( generator.getName(), generator );
 				if ( old != null ) {
 					LOG.duplicateGeneratorName( old.getName() );
 				}
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
 			if ( old != null ) {
 				LOG.duplicateGeneratorTable( name );
 			}
 		}
 
 		public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables) {
 			if ( localGeneratorTables != null ) {
 				Properties result = localGeneratorTables.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return generatorTables.get( name );
 		}
 
 		public Map<String, Join> getJoins(String entityName) {
 			return joins.get( entityName );
 		}
 
 		public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) {
 			Object old = Configuration.this.joins.put( persistentClass.getEntityName(), joins );
 			if ( old != null ) {
 				LOG.duplicateJoins( persistentClass.getEntityName() );
 			}
 		}
 
 		public AnnotatedClassType getClassType(XClass clazz) {
 			AnnotatedClassType type = classTypes.get( clazz.getName() );
 			if ( type == null ) {
 				return addClassType( clazz );
 			}
 			else {
 				return type;
 			}
 		}
 
 		//FIXME should be private but is part of the ExtendedMapping contract
 
 		public AnnotatedClassType addClassType(XClass clazz) {
 			AnnotatedClassType type;
 			if ( clazz.isAnnotationPresent( Entity.class ) ) {
 				type = AnnotatedClassType.ENTITY;
 			}
 			else if ( clazz.isAnnotationPresent( Embeddable.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE;
 			}
 			else if ( clazz.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE_SUPERCLASS;
 			}
 			else {
 				type = AnnotatedClassType.NONE;
 			}
 			classTypes.put( clazz.getName(), type );
 			return type;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Map<Table, List<String[]>> getTableUniqueConstraints() {
 			final Map<Table, List<String[]>> deprecatedStructure = new HashMap<Table, List<String[]>>(
 					CollectionHelper.determineProperSizing( getUniqueConstraintHoldersByTable() ),
 					CollectionHelper.LOAD_FACTOR
 			);
 			for ( Map.Entry<Table, List<UniqueConstraintHolder>> entry : getUniqueConstraintHoldersByTable().entrySet() ) {
 				List<String[]> columnsPerConstraint = new ArrayList<String[]>(
 						CollectionHelper.determineProperSizing( entry.getValue().size() )
 				);
 				deprecatedStructure.put( entry.getKey(), columnsPerConstraint );
 				for ( UniqueConstraintHolder holder : entry.getValue() ) {
 					columnsPerConstraint.add( holder.getColumns() );
 				}
 			}
 			return deprecatedStructure;
 		}
 
 		public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable() {
 			return uniqueConstraintHoldersByTable;
 		}
 
 		@SuppressWarnings({ "unchecked" })
 		public void addUniqueConstraints(Table table, List uniqueConstraints) {
 			List<UniqueConstraintHolder> constraintHolders = new ArrayList<UniqueConstraintHolder>(
 					CollectionHelper.determineProperSizing( uniqueConstraints.size() )
 			);
 
 			int keyNameBase = determineCurrentNumberOfUniqueConstraintHolders( table );
 			for ( String[] columns : ( List<String[]> ) uniqueConstraints ) {
 				final String keyName = "key" + keyNameBase++;
 				constraintHolders.add(
 						new UniqueConstraintHolder().setName( keyName ).setColumns( columns )
 				);
 			}
 			addUniqueConstraintHolders( table, constraintHolders );
 		}
 
 		private int determineCurrentNumberOfUniqueConstraintHolders(Table table) {
 			List currentHolders = getUniqueConstraintHoldersByTable().get( table );
 			return currentHolders == null
 					? 0
 					: currentHolders.size();
 		}
 
 		public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
 			List<UniqueConstraintHolder> holderList = getUniqueConstraintHoldersByTable().get( table );
 			if ( holderList == null ) {
 				holderList = new ArrayList<UniqueConstraintHolder>();
 				getUniqueConstraintHoldersByTable().put( table, holderList );
 			}
 			holderList.addAll( uniqueConstraintHolders );
 		}
 
 		public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
 			mappedByResolver.put( entityName + "." + propertyName, inversePropertyName );
 		}
 
 		public String getFromMappedBy(String entityName, String propertyName) {
 			return mappedByResolver.get( entityName + "." + propertyName );
 		}
 
 		public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
 			propertyRefResolver.put( entityName + "." + propertyName, propertyRef );
 		}
 
 		public String getPropertyReferencedAssociation(String entityName, String propertyName) {
 			return propertyRefResolver.get( entityName + "." + propertyName );
 		}
 
 		public ReflectionManager getReflectionManager() {
 			return reflectionManager;
 		}
 
 		public Map getClasses() {
 			return classes;
 		}
 
 		public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException {
 			if ( anyMetaDefs.containsKey( defAnn.name() ) ) {
 				throw new AnnotationException( "Two @AnyMetaDef with the same name defined: " + defAnn.name() );
 			}
 			anyMetaDefs.put( defAnn.name(), defAnn );
 		}
 
 		public AnyMetaDef getAnyMetaDef(String name) {
 			return anyMetaDefs.get( name );
 		}
 	}
 
 	final ObjectNameNormalizer normalizer = new ObjectNameNormalizerImpl();
 
 	final class ObjectNameNormalizerImpl extends ObjectNameNormalizer implements Serializable {
 		public boolean isUseQuotedIdentifiersGlobally() {
 			//Do not cache this value as we lazily set it in Hibernate Annotation (AnnotationConfiguration)
 			//TODO use a dedicated protected useQuotedIdentifier flag in Configuration (overriden by AnnotationConfiguration)
 			String setting = (String) properties.get( Environment.GLOBALLY_QUOTED_IDENTIFIERS );
 			return setting != null && Boolean.valueOf( setting ).booleanValue();
 		}
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 	}
 
 	protected class MetadataSourceQueue implements Serializable {
 		private LinkedHashMap<XmlDocument, Set<String>> hbmMetadataToEntityNamesMap
 				= new LinkedHashMap<XmlDocument, Set<String>>();
 		private Map<String, XmlDocument> hbmMetadataByEntityNameXRef = new HashMap<String, XmlDocument>();
 
 		//XClass are not serializable by default
 		private transient List<XClass> annotatedClasses = new ArrayList<XClass>();
 		//only used during the secondPhaseCompile pass, hence does not need to be serialized
 		private transient Map<String, XClass> annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 			ois.defaultReadObject();
 			annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 			//build back annotatedClasses
 			@SuppressWarnings( "unchecked" )
 			List<Class> serializableAnnotatedClasses = (List<Class>) ois.readObject();
 			annotatedClasses = new ArrayList<XClass>( serializableAnnotatedClasses.size() );
 			for ( Class clazz : serializableAnnotatedClasses ) {
 				annotatedClasses.add( reflectionManager.toXClass( clazz ) );
 			}
 		}
 
 		private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 			out.defaultWriteObject();
 			List<Class> serializableAnnotatedClasses = new ArrayList<Class>( annotatedClasses.size() );
 			for ( XClass xClass : annotatedClasses ) {
 				serializableAnnotatedClasses.add( reflectionManager.toClass( xClass ) );
 			}
 			out.writeObject( serializableAnnotatedClasses );
 		}
 
 		public void add(XmlDocument metadataXml) {
 			final Document document = metadataXml.getDocumentTree();
 			final Element hmNode = document.getRootElement();
 			Attribute packNode = hmNode.attribute( "package" );
 			String defaultPackage = packNode != null ? packNode.getValue() : "";
 			Set<String> entityNames = new HashSet<String>();
 			findClassNames( defaultPackage, hmNode, entityNames );
 			for ( String entity : entityNames ) {
 				hbmMetadataByEntityNameXRef.put( entity, metadataXml );
 			}
 			this.hbmMetadataToEntityNamesMap.put( metadataXml, entityNames );
 		}
 
 		private void findClassNames(String defaultPackage, Element startNode, Set<String> names) {
 			// if we have some extends we need to check if those classes possibly could be inside the
 			// same hbm.xml file...
 			Iterator[] classes = new Iterator[4];
 			classes[0] = startNode.elementIterator( "class" );
 			classes[1] = startNode.elementIterator( "subclass" );
 			classes[2] = startNode.elementIterator( "joined-subclass" );
 			classes[3] = startNode.elementIterator( "union-subclass" );
 
 			Iterator classIterator = new JoinedIterator( classes );
 			while ( classIterator.hasNext() ) {
 				Element element = ( Element ) classIterator.next();
 				String entityName = element.attributeValue( "entity-name" );
 				if ( entityName == null ) {
 					entityName = getClassName( element.attribute( "name" ), defaultPackage );
 				}
 				names.add( entityName );
 				findClassNames( defaultPackage, element, names );
 			}
 		}
 
 		private String getClassName(Attribute name, String defaultPackage) {
 			if ( name == null ) {
 				return null;
 			}
 			String unqualifiedName = name.getValue();
 			if ( unqualifiedName == null ) {
 				return null;
 			}
 			if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 				return defaultPackage + '.' + unqualifiedName;
 			}
 			return unqualifiedName;
 		}
 
 		public void add(XClass annotatedClass) {
 			annotatedClasses.add( annotatedClass );
 		}
 
 		protected void syncAnnotatedClasses() {
 			final Iterator<XClass> itr = annotatedClasses.iterator();
 			while ( itr.hasNext() ) {
 				final XClass annotatedClass = itr.next();
 				if ( annotatedClass.isAnnotationPresent( Entity.class ) ) {
 					annotatedClassesByEntityNameMap.put( annotatedClass.getName(), annotatedClass );
 					continue;
 				}
 
 				if ( !annotatedClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 					itr.remove();
 				}
 			}
 		}
 
 		protected void processMetadata(List<MetadataSourceType> order) {
 			syncAnnotatedClasses();
 
 			for ( MetadataSourceType type : order ) {
 				if ( MetadataSourceType.HBM.equals( type ) ) {
 					processHbmXmlQueue();
 				}
 				else if ( MetadataSourceType.CLASS.equals( type ) ) {
 					processAnnotatedClassesQueue();
 				}
 			}
 		}
 
 		private void processHbmXmlQueue() {
-			LOG.debugf( "Processing hbm.xml files" );
+			LOG.debug( "Processing hbm.xml files" );
 			for ( Map.Entry<XmlDocument, Set<String>> entry : hbmMetadataToEntityNamesMap.entrySet() ) {
 				// Unfortunately we have to create a Mappings instance for each iteration here
 				processHbmXml( entry.getKey(), entry.getValue() );
 			}
 			hbmMetadataToEntityNamesMap.clear();
 			hbmMetadataByEntityNameXRef.clear();
 		}
 
 		private void processHbmXml(XmlDocument metadataXml, Set<String> entityNames) {
 			try {
 				HbmBinder.bindRoot( metadataXml, createMappings(), CollectionHelper.EMPTY_MAP, entityNames );
 			}
 			catch ( MappingException me ) {
 				throw new InvalidMappingException(
 						metadataXml.getOrigin().getType(),
 						metadataXml.getOrigin().getName(),
 						me
 				);
 			}
 
 			for ( String entityName : entityNames ) {
 				if ( annotatedClassesByEntityNameMap.containsKey( entityName ) ) {
 					annotatedClasses.remove( annotatedClassesByEntityNameMap.get( entityName ) );
 					annotatedClassesByEntityNameMap.remove( entityName );
 				}
 			}
 		}
 
 		private void processAnnotatedClassesQueue() {
-			LOG.debugf( "Process annotated classes" );
+			LOG.debug( "Process annotated classes" );
 			//bind classes in the correct order calculating some inheritance state
 			List<XClass> orderedClasses = orderAndFillHierarchy( annotatedClasses );
 			Mappings mappings = createMappings();
 			Map<XClass, InheritanceState> inheritanceStatePerClass = AnnotationBinder.buildInheritanceStates(
 					orderedClasses, mappings
 			);
 
 
 			for ( XClass clazz : orderedClasses ) {
 				AnnotationBinder.bindClass( clazz, inheritanceStatePerClass, mappings );
 
 				final String entityName = clazz.getName();
 				if ( hbmMetadataByEntityNameXRef.containsKey( entityName ) ) {
 					hbmMetadataToEntityNamesMap.remove( hbmMetadataByEntityNameXRef.get( entityName ) );
 					hbmMetadataByEntityNameXRef.remove( entityName );
 				}
 			}
 			annotatedClasses.clear();
 			annotatedClassesByEntityNameMap.clear();
 		}
 
 		private List<XClass> orderAndFillHierarchy(List<XClass> original) {
 			List<XClass> copy = new ArrayList<XClass>( original );
 			insertMappedSuperclasses( original, copy );
 
 			// order the hierarchy
 			List<XClass> workingCopy = new ArrayList<XClass>( copy );
 			List<XClass> newList = new ArrayList<XClass>( copy.size() );
 			while ( workingCopy.size() > 0 ) {
 				XClass clazz = workingCopy.get( 0 );
 				orderHierarchy( workingCopy, newList, copy, clazz );
 			}
 			return newList;
 		}
 
 		private void insertMappedSuperclasses(List<XClass> original, List<XClass> copy) {
 			for ( XClass clazz : original ) {
 				XClass superClass = clazz.getSuperclass();
 				while ( superClass != null
 						&& !reflectionManager.equals( superClass, Object.class )
 						&& !copy.contains( superClass ) ) {
 					if ( superClass.isAnnotationPresent( Entity.class )
 							|| superClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 						copy.add( superClass );
 					}
 					superClass = superClass.getSuperclass();
 				}
 			}
 		}
 
 		private void orderHierarchy(List<XClass> copy, List<XClass> newList, List<XClass> original, XClass clazz) {
 			if ( clazz == null || reflectionManager.equals( clazz, Object.class ) ) {
 				return;
 			}
 			//process superclass first
 			orderHierarchy( copy, newList, original, clazz.getSuperclass() );
 			if ( original.contains( clazz ) ) {
 				if ( !newList.contains( clazz ) ) {
 					newList.add( clazz );
 				}
 				copy.remove( clazz );
 			}
 		}
 
 		public boolean isEmpty() {
 			return hbmMetadataToEntityNamesMap.isEmpty() && annotatedClasses.isEmpty();
 		}
 
 	}
 
 
 	public static final MetadataSourceType[] DEFAULT_ARTEFACT_PROCESSING_ORDER = new MetadataSourceType[] {
 			MetadataSourceType.HBM,
 			MetadataSourceType.CLASS
 	};
 
 	private List<MetadataSourceType> metadataSourcePrecedence;
 
 	private List<MetadataSourceType> determineMetadataSourcePrecedence() {
 		if ( metadataSourcePrecedence.isEmpty()
 				&& StringHelper.isNotEmpty( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) ) ) {
 			metadataSourcePrecedence = parsePrecedence( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) );
 		}
 		if ( metadataSourcePrecedence.isEmpty() ) {
 			metadataSourcePrecedence = Arrays.asList( DEFAULT_ARTEFACT_PROCESSING_ORDER );
 		}
 		metadataSourcePrecedence = Collections.unmodifiableList( metadataSourcePrecedence );
 
 		return metadataSourcePrecedence;
 	}
 
 	public void setPrecedence(String precedence) {
 		this.metadataSourcePrecedence = parsePrecedence( precedence );
 	}
 
 	private List<MetadataSourceType> parsePrecedence(String s) {
 		if ( StringHelper.isEmpty( s ) ) {
 			return Collections.emptyList();
 		}
 		StringTokenizer precedences = new StringTokenizer( s, ",; ", false );
 		List<MetadataSourceType> tmpPrecedences = new ArrayList<MetadataSourceType>();
 		while ( precedences.hasMoreElements() ) {
 			tmpPrecedences.add( MetadataSourceType.parsePrecedence( ( String ) precedences.nextElement() ) );
 		}
 		return tmpPrecedences;
 	}
 
 	private static class CacheHolder {
 		public CacheHolder(String role, String usage, String region, boolean isClass, boolean cacheLazy) {
 			this.role = role;
 			this.usage = usage;
 			this.region = region;
 			this.isClass = isClass;
 			this.cacheLazy = cacheLazy;
 		}
 
 		public String role;
 		public String usage;
 		public String region;
 		public boolean isClass;
 		public boolean cacheLazy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 6cade49d1e..660d4e45ee 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -323,2001 +323,2001 @@ public final class HbmBinder {
 	private static void bindRootPersistentClassCommonValues(Element node,
 			java.util.Map inheritedMetas, Mappings mappings, RootClass entity)
 			throws MappingException {
 
 		// DB-OBJECTNAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( entity, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 		        entity.isAbstract() != null && entity.isAbstract().booleanValue()
 			);
 		entity.setTable( table );
 		bindComment(table, node);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class: %s -> %s", entity.getEntityName(), entity.getTable().getName() );
 		}
 
 		// MUTABLE
 		Attribute mutableNode = node.attribute( "mutable" );
 		entity.setMutable( ( mutableNode == null ) || mutableNode.getValue().equals( "true" ) );
 
 		// WHERE
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) entity.setWhere( whereNode.getValue() );
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) table.addCheckConstraint( chNode.getValue() );
 
 		// POLYMORPHISM
 		Attribute polyNode = node.attribute( "polymorphism" );
 		entity.setExplicitPolymorphism( ( polyNode != null )
 			&& polyNode.getValue().equals( "explicit" ) );
 
 		// ROW ID
 		Attribute rowidNode = node.attribute( "rowid" );
 		if ( rowidNode != null ) table.setRowId( rowidNode.getValue() );
 
 		Iterator subnodes = node.elementIterator();
 		while ( subnodes.hasNext() ) {
 
 			Element subnode = (Element) subnodes.next();
 			String name = subnode.getName();
 
 			if ( "id".equals( name ) ) {
 				// ID
 				bindSimpleId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "composite-id".equals( name ) ) {
 				// COMPOSITE-ID
 				bindCompositeId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "version".equals( name ) || "timestamp".equals( name ) ) {
 				// VERSION / TIMESTAMP
 				bindVersioningProperty( table, subnode, mappings, name, entity, inheritedMetas );
 			}
 			else if ( "discriminator".equals( name ) ) {
 				// DISCRIMINATOR
 				bindDiscriminatorProperty( table, entity, subnode, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				entity.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				entity.setCacheRegionName( subnode.attributeValue( "region" ) );
 				entity.setLazyPropertiesCacheable( !"non-lazy".equals( subnode.attributeValue( "include" ) ) );
 			}
 
 		}
 
 		// Primary key constraint
 		entity.createPrimaryKey();
 
 		createClassProperties( node, entity, mappings, inheritedMetas );
 	}
 
 	private static void bindSimpleId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 
 		SimpleValue id = new SimpleValue( mappings, entity.getTable() );
 		entity.setIdentifier( id );
 
 		// if ( propertyName == null || entity.getPojoRepresentation() == null ) {
 		// bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		// if ( !id.isTypeSpecified() ) {
 		// throw new MappingException( "must specify an identifier type: " + entity.getEntityName()
 		// );
 		// }
 		// }
 		// else {
 		// bindSimpleValue( idNode, id, false, propertyName, mappings );
 		// PojoRepresentation pojo = entity.getPojoRepresentation();
 		// id.setTypeUsingReflection( pojo.getClassName(), propertyName );
 		//
 		// Property prop = new Property();
 		// prop.setValue( id );
 		// bindProperty( idNode, prop, mappings, inheritedMetas );
 		// entity.setIdentifierProperty( prop );
 		// }
 
 		if ( propertyName == null ) {
 			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		}
 		else {
 			bindSimpleValue( idNode, id, false, propertyName, mappings );
 		}
 
 		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 			if ( !id.isTypeSpecified() ) {
 				throw new MappingException( "must specify an identifier type: "
 					+ entity.getEntityName() );
 			}
 		}
 		else {
 			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 		}
 
 		if ( propertyName != null ) {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 		Component id = new Component( mappings, entity );
 		entity.setIdentifier( id );
 		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 		if ( propertyName == null ) {
 			entity.setEmbeddedIdentifier( id.isEmbedded() );
 			if ( id.isEmbedded() ) {
 				// todo : what is the implication of this?
 				id.setDynamic( !entity.hasPojoRepresentation() );
 				/*
 				 * Property prop = new Property(); prop.setName("id");
 				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 				 * entity.setIdentifierProperty(prop);
 				 */
 			}
 		}
 		else {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 		}
 
 		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private static void bindVersioningProperty(Table table, Element subnode, Mappings mappings,
 			String name, RootClass entity, java.util.Map inheritedMetas) {
 
 		String propertyName = subnode.attributeValue( "name" );
 		SimpleValue val = new SimpleValue( mappings, table );
 		bindSimpleValue( subnode, val, false, propertyName, mappings );
 		if ( !val.isTypeSpecified() ) {
 			// this is either a <version/> tag with no type attribute,
 			// or a <timestamp/> tag
 			if ( "version".equals( name ) ) {
 				val.setTypeName( "integer" );
 			}
 			else {
 				if ( "db".equals( subnode.attributeValue( "source" ) ) ) {
 					val.setTypeName( "dbtimestamp" );
 				}
 				else {
 					val.setTypeName( "timestamp" );
 				}
 			}
 		}
 		Property prop = new Property();
 		prop.setValue( val );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure...
 		if ( prop.getGeneration() == PropertyGeneration.INSERT ) {
 			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 		}
 		makeVersion( subnode, val );
 		entity.setVersion( prop );
 		entity.addProperty( prop );
 	}
 
 	private static void bindDiscriminatorProperty(Table table, RootClass entity, Element subnode,
 			Mappings mappings) {
 		SimpleValue discrim = new SimpleValue( mappings, table );
 		entity.setDiscriminator( discrim );
 		bindSimpleValue(
 				subnode,
 				discrim,
 				false,
 				RootClass.DEFAULT_DISCRIMINATOR_COLUMN_NAME,
 				mappings
 			);
 		if ( !discrim.isTypeSpecified() ) {
 			discrim.setTypeName( "string" );
 			// ( (Column) discrim.getColumnIterator().next() ).setType(type);
 		}
 		entity.setPolymorphic( true );
 		if ( "true".equals( subnode.attributeValue( "force" ) ) )
 			entity.setForceDiscriminator( true );
 		if ( "false".equals( subnode.attributeValue( "insert" ) ) )
 			entity.setDiscriminatorInsertable( false );
 	}
 
 	public static void bindClass(Element node, PersistentClass persistentClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		// transfer an explicitly defined entity name
 		// handle the lazy attribute
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean lazy = lazyNode == null ?
 				mappings.isDefaultLazy() :
 				"true".equals( lazyNode.getValue() );
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		persistentClass.setLazy( lazy );
 
 		String entityName = node.attributeValue( "entity-name" );
 		if ( entityName == null ) entityName = getClassName( node.attribute("name"), mappings );
 		if ( entityName==null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 		persistentClass.setEntityName( entityName );
 
 		bindPojoRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindDom4jRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindMapRepresentation( node, persistentClass, mappings, inheritedMetas );
 
 		Iterator itr = node.elementIterator( "fetch-profile" );
 		while ( itr.hasNext() ) {
 			final Element profileElement = ( Element ) itr.next();
 			parseFetchProfile( profileElement, mappings, entityName );
 		}
 
 		bindPersistentClassCommonValues( node, persistentClass, mappings, inheritedMetas );
 	}
 
 	private static void bindPojoRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map metaTags) {
 
 		String className = getClassName( node.attribute( "name" ), mappings );
 		String proxyName = getClassName( node.attribute( "proxy" ), mappings );
 
 		entity.setClassName( className );
 
 		if ( proxyName != null ) {
 			entity.setProxyInterfaceName( proxyName );
 			entity.setLazy( true );
 		}
 		else if ( entity.isLazy() ) {
 			entity.setProxyInterfaceName( className );
 		}
 
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.POJO );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.POJO, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	private static void bindDom4jRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = StringHelper.unqualify( entity.getEntityName() );
 		entity.setNodeName(nodeName);
 
 //		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
 //		if ( tuplizer != null ) {
 //			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
 //		}
 	}
 
 	private static void bindMapRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.MAP );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.MAP, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	/**
 	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
 	 *
 	 * @param container The containing element (representing the entity/component)
 	 * @param entityMode The entity-mode for which to locate the tuplizer element
 	 * @return The tuplizer element, or null.
 	 */
 	private static Element locateTuplizerDefinition(Element container, EntityMode entityMode) {
 		Iterator itr = container.elements( "tuplizer" ).iterator();
 		while( itr.hasNext() ) {
 			final Element tuplizerElem = ( Element ) itr.next();
 			if ( entityMode.toString().equals( tuplizerElem.attributeValue( "entity-mode") ) ) {
 				return tuplizerElem;
 			}
 		}
 		return null;
 	}
 
 	private static void bindPersistentClassCommonValues(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		// DISCRIMINATOR
 		Attribute discriminatorNode = node.attribute( "discriminator-value" );
 		entity.setDiscriminatorValue( ( discriminatorNode == null )
 			? entity.getEntityName()
 			: discriminatorNode.getValue() );
 
 		// DYNAMIC UPDATE
 		Attribute dynamicNode = node.attribute( "dynamic-update" );
 		entity.setDynamicUpdate(
 				dynamicNode != null && "true".equals( dynamicNode.getValue() )
 		);
 
 		// DYNAMIC INSERT
 		Attribute insertNode = node.attribute( "dynamic-insert" );
 		entity.setDynamicInsert(
 				insertNode != null && "true".equals( insertNode.getValue() )
 		);
 
 		// IMPORT
 		mappings.addImport( entity.getEntityName(), entity.getEntityName() );
 		if ( mappings.isAutoImport() && entity.getEntityName().indexOf( '.' ) > 0 ) {
 			mappings.addImport(
 					entity.getEntityName(),
 					StringHelper.unqualify( entity.getEntityName() )
 				);
 		}
 
 		// BATCH SIZE
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) entity.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 
 		// SELECT BEFORE UPDATE
 		Attribute sbuNode = node.attribute( "select-before-update" );
 		if ( sbuNode != null ) entity.setSelectBeforeUpdate( "true".equals( sbuNode.getValue() ) );
 
 		// OPTIMISTIC LOCK MODE
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		entity.setOptimisticLockMode( getOptimisticLockMode( olNode ) );
 
 		entity.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				entity.setEntityPersisterClass( ReflectHelper.classForName(
 						persisterNode
 								.getValue()
 				) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, entity );
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			entity.addSynchronizedTable( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Attribute abstractNode = node.attribute( "abstract" );
 		Boolean isAbstract = abstractNode == null
 				? null
 		        : "true".equals( abstractNode.getValue() )
 						? Boolean.TRUE
 	                    : "false".equals( abstractNode.getValue() )
 								? Boolean.FALSE
 	                            : null;
 		entity.setAbstract( isAbstract );
 	}
 
 	private static void handleCustomSQL(Element node, PersistentClass model)
 			throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "loader" );
 		if ( element != null ) {
 			model.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Join model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Collection model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete-all" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDeleteAll( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static boolean isCallable(Element e) throws MappingException {
 		return isCallable( e, true );
 	}
 
 	private static boolean isCallable(Element element, boolean supportsCallable)
 			throws MappingException {
 		Attribute attrib = element.attribute( "callable" );
 		if ( attrib != null && "true".equals( attrib.getValue() ) ) {
 			if ( !supportsCallable ) {
 				throw new MappingException( "callable attribute not supported yet!" );
 			}
 			return true;
 		}
 		return false;
 	}
 
 	private static ExecuteUpdateResultCheckStyle getResultCheckStyle(Element element, boolean callable) throws MappingException {
 		Attribute attr = element.attribute( "check" );
 		if ( attr == null ) {
 			// use COUNT as the default.  This mimics the old behavior, although
 			// NONE might be a better option moving forward in the case of callable
 			return ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		return ExecuteUpdateResultCheckStyle.fromExternalName( attr.getValue() );
 	}
 
 	public static void bindUnionSubclass(Element node, UnionSubclass unionSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, unionSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table denormalizedSuperTable = unionSubclass.getSuperclass().getTable();
 		Table mytable = mappings.addDenormalizedTable(
 				schema,
 				catalog,
 				getClassTableName(unionSubclass, node, schema, catalog, denormalizedSuperTable, mappings ),
 		        unionSubclass.isAbstract() != null && unionSubclass.isAbstract().booleanValue(),
 				getSubselect( node ),
 				denormalizedSuperTable
 			);
 		unionSubclass.setTable( mytable );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping union-subclass: %s -> %s", unionSubclass.getEntityName(), unionSubclass.getTable().getName() );
 		}
 
 		createClassProperties( node, unionSubclass, mappings, inheritedMetas );
 
 	}
 
 	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, subclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping subclass: %s -> %s", subclass.getEntityName(), subclass.getTable().getName() );
 		}
 
 		// properties
 		createClassProperties( node, subclass, mappings, inheritedMetas );
 	}
 
 	private static String getClassTableName(
 			PersistentClass model,
 			Element node,
 			String schema,
 			String catalog,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		Attribute tableNameNode = node.attribute( "table" );
 		String logicalTableName;
 		String physicalTableName;
 		if ( tableNameNode == null ) {
 			logicalTableName = StringHelper.unqualify( model.getEntityName() );
 			physicalTableName = mappings.getNamingStrategy().classToTableName( model.getEntityName() );
 		}
 		else {
 			logicalTableName = tableNameNode.getValue();
 			physicalTableName = mappings.getNamingStrategy().tableName( logicalTableName );
 		}
 		mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	public static void bindJoinedSubclass(Element node, JoinedSubclass joinedSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, joinedSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from
 																	// <joined-subclass>
 
 		// joined subclasses
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table mytable = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( joinedSubclass, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 				false
 			);
 		joinedSubclass.setTable( mytable );
 		bindComment(mytable, node);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping joined-subclass: %s -> %s", joinedSubclass.getEntityName(), joinedSubclass.getTable().getName() );
 		}
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, mytable, joinedSubclass.getIdentifier() );
 		joinedSubclass.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, joinedSubclass.getEntityName(), mappings );
 
 		// model.getKey().setType( new Type( model.getIdentifier() ) );
 		joinedSubclass.createPrimaryKey();
 		joinedSubclass.createForeignKey();
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) mytable.addCheckConstraint( chNode.getValue() );
 
 		// properties
 		createClassProperties( node, joinedSubclass, mappings, inheritedMetas );
 
 	}
 
 	private static void bindJoin(Element node, Join join, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		PersistentClass persistentClass = join.getPersistentClass();
 		String path = persistentClass.getEntityName();
 
 		// TABLENAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 		Table primaryTable = persistentClass.getTable();
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( persistentClass, node, schema, catalog, primaryTable, mappings ),
 				getSubselect( node ),
 				false
 			);
 		join.setTable( table );
 		bindComment(table, node);
 
 		Attribute fetchNode = node.attribute( "fetch" );
 		if ( fetchNode != null ) {
 			join.setSequentialSelect( "select".equals( fetchNode.getValue() ) );
 		}
 
 		Attribute invNode = node.attribute( "inverse" );
 		if ( invNode != null ) {
 			join.setInverse( "true".equals( invNode.getValue() ) );
 		}
 
 		Attribute nullNode = node.attribute( "optional" );
 		if ( nullNode != null ) {
 			join.setOptional( "true".equals( nullNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class join: %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		}
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, table, persistentClass.getIdentifier() );
 		join.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, persistentClass.getEntityName(), mappings );
 
 		// join.getKey().setType( new Type( lazz.getIdentifier() ) );
 		join.createPrimaryKey();
 		join.createForeignKey();
 
 		// PROPERTIES
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			Value value = null;
 			if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, true, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, true, propertyName, mappings );
 			}
 			else if ( "component".equals( name ) || "dynamic-component".equals( name ) ) {
 				String subpath = StringHelper.qualify( path, propertyName );
 				value = new Component( mappings, join );
 				bindComponent(
 						subnode,
 						(Component) value,
 						join.getPersistentClass().getClassName(),
 						propertyName,
 						subpath,
 						true,
 						false,
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 
 			if ( value != null ) {
 				Property prop = createProperty( value, propertyName, persistentClass
 					.getEntityName(), subnode, mappings, inheritedMetas );
 				prop.setOptional( join.isOptional() );
 				join.addProperty( prop );
 			}
 
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, join );
 
 	}
 
 	public static void bindColumns(final Element node, final SimpleValue simpleValue,
 			final boolean isNullable, final boolean autoColumn, final String propertyPath,
 			final Mappings mappings) throws MappingException {
 
 		Table table = simpleValue.getTable();
 
 		// COLUMN(S)
 		Attribute columnAttribute = node.attribute( "column" );
 		if ( columnAttribute == null ) {
 			Iterator itr = node.elementIterator();
 			int count = 0;
 			while ( itr.hasNext() ) {
 				Element columnElement = (Element) itr.next();
 				if ( columnElement.getName().equals( "column" ) ) {
 					Column column = new Column();
 					column.setValue( simpleValue );
 					column.setTypeIndex( count++ );
 					bindColumn( columnElement, column, isNullable );
 					final String columnName = columnElement.attributeValue( "name" );
 					String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 							columnName, propertyPath
 					);
 					column.setName( mappings.getNamingStrategy().columnName(
 						columnName ) );
 					if ( table != null ) {
 						table.addColumn( column ); // table=null -> an association
 						                           // - fill it in later
 						//TODO fill in the mappings for table == null
 						mappings.addColumnBinding( logicalColumnName, column, table );
 					}
 
 
 					simpleValue.addColumn( column );
 					// column index
 					bindIndex( columnElement.attribute( "index" ), table, column, mappings );
 					bindIndex( node.attribute( "index" ), table, column, mappings );
 					//column unique-key
 					bindUniqueKey( columnElement.attribute( "unique-key" ), table, column, mappings );
 					bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 				}
 				else if ( columnElement.getName().equals( "formula" ) ) {
 					Formula formula = new Formula();
 					formula.setFormula( columnElement.getText() );
 					simpleValue.addFormula( formula );
 				}
 			}
 
 			// todo : another GoodThing would be to go back after all parsing and see if all the columns
 			// (and no formulas) are contained in a defined unique key that only contains these columns.
 			// That too would mark this as a logical one-to-one
 			final Attribute uniqueAttribute = node.attribute( "unique" );
 			if ( uniqueAttribute != null
 					&& "true".equals( uniqueAttribute.getValue() )
 					&& ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 		}
 		else {
 			if ( node.elementIterator( "column" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <column> subelement" );
 			}
 			if ( node.elementIterator( "formula" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <formula> subelement" );
 			}
 
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			if ( column.isUnique() && ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 			final String columnName = columnAttribute.getValue();
 			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 					columnName, propertyPath
 			);
 			column.setName( mappings.getNamingStrategy().columnName( columnName ) );
 			if ( table != null ) {
 				table.addColumn( column ); // table=null -> an association - fill
 				                           // it in later
 				//TODO fill in the mappings for table == null
 				mappings.addColumnBinding( logicalColumnName, column, table );
 			}
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 		if ( autoColumn && simpleValue.getColumnSpan() == 0 ) {
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			column.setName( mappings.getNamingStrategy().propertyToColumnName( propertyPath ) );
 			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
 			mappings.addColumnBinding( logicalName, column, table );
 			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
 			 * slightly higer level in the stack (to get all the information we need)
 			 * Right now HbmMetadataSourceProcessorImpl does not support the
 			 */
 			simpleValue.getTable().addColumn( column );
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 	}
 
 	private static void bindIndex(Attribute indexAttribute, Table table, Column column, Mappings mappings) {
 		if ( indexAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( indexAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateIndex( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	private static void bindUniqueKey(Attribute uniqueKeyAttribute, Table table, Column column, Mappings mappings) {
 		if ( uniqueKeyAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( uniqueKeyAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateUniqueKey( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	// automatically makes a column with the default name if none is specifed by XML
 	public static void bindSimpleValue(Element node, SimpleValue simpleValue, boolean isNullable,
 			String path, Mappings mappings) throws MappingException {
 		bindSimpleValueType( node, simpleValue, mappings );
 
 		bindColumnsOrFormula( node, simpleValue, path, isNullable, mappings );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) simpleValue.setForeignKeyName( fkNode.getValue() );
 	}
 
 	private static void bindSimpleValueType(Element node, SimpleValue simpleValue, Mappings mappings)
 			throws MappingException {
 		String typeName = null;
 
 		Properties parameters = new Properties();
 
 		Attribute typeNode = node.attribute( "type" );
         if ( typeNode == null ) {
             typeNode = node.attribute( "id-type" ); // for an any
         }
         else {
             typeName = typeNode.getValue();
         }
 
 		Element typeChild = node.element( "type" );
 		if ( typeName == null && typeChild != null ) {
 			typeName = typeChild.attribute( "name" ).getValue();
 			Iterator typeParameters = typeChild.elementIterator( "param" );
 
 			while ( typeParameters.hasNext() ) {
 				Element paramElement = (Element) typeParameters.next();
 				parameters.setProperty(
 						paramElement.attributeValue( "name" ),
 						paramElement.getTextTrim()
 					);
 			}
 		}
 
 		TypeDef typeDef = mappings.getTypeDef( typeName );
 		if ( typeDef != null ) {
 			typeName = typeDef.getTypeClass();
 			// parameters on the property mapping should
 			// override parameters in the typedef
 			Properties allParameters = new Properties();
 			allParameters.putAll( typeDef.getParameters() );
 			allParameters.putAll( parameters );
 			parameters = allParameters;
 		}
 
 		if ( !parameters.isEmpty() ) simpleValue.setTypeParameters( parameters );
 
 		if ( typeName != null ) simpleValue.setTypeName( typeName );
 	}
 
 	public static void bindProperty(
 			Element node,
 	        Property property,
 	        Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		String propName = node.attributeValue( "name" );
 		property.setName( propName );
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = propName;
 		property.setNodeName( nodeName );
 
 		// TODO:
 		//Type type = model.getValue().getType();
 		//if (type==null) throw new MappingException(
 		//"Could not determine a property type for: " + model.getName() );
 
 		Attribute accessNode = node.attribute( "access" );
 		if ( accessNode != null ) {
 			property.setPropertyAccessorName( accessNode.getValue() );
 		}
 		else if ( node.getName().equals( "properties" ) ) {
 			property.setPropertyAccessorName( "embedded" );
 		}
 		else {
 			property.setPropertyAccessorName( mappings.getDefaultAccess() );
 		}
 
 		Attribute cascadeNode = node.attribute( "cascade" );
 		property.setCascade( cascadeNode == null ? mappings.getDefaultCascade() : cascadeNode
 			.getValue() );
 
 		Attribute updateNode = node.attribute( "update" );
 		property.setUpdateable( updateNode == null || "true".equals( updateNode.getValue() ) );
 
 		Attribute insertNode = node.attribute( "insert" );
 		property.setInsertable( insertNode == null || "true".equals( insertNode.getValue() ) );
 
 		Attribute lockNode = node.attribute( "optimistic-lock" );
 		property.setOptimisticLocked( lockNode == null || "true".equals( lockNode.getValue() ) );
 
 		Attribute generatedNode = node.attribute( "generated" );
         String generationName = generatedNode == null ? null : generatedNode.getValue();
         PropertyGeneration generation = PropertyGeneration.parse( generationName );
 		property.setGeneration( generation );
 
         if ( generation == PropertyGeneration.ALWAYS || generation == PropertyGeneration.INSERT ) {
 	        // generated properties can *never* be insertable...
 	        if ( property.isInsertable() ) {
 		        if ( insertNode == null ) {
 			        // insertable simply because that is the user did not specify
 			        // anything; just override it
 					property.setInsertable( false );
 		        }
 		        else {
 			        // the user specifically supplied insert="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both insert=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
 
 	        // properties generated on update can never be updateable...
 	        if ( property.isUpdateable() && generation == PropertyGeneration.ALWAYS ) {
 		        if ( updateNode == null ) {
 			        // updateable only because the user did not specify
 			        // anything; just override it
 			        property.setUpdateable( false );
 		        }
 		        else {
 			        // the user specifically supplied update="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
         }
 
 		boolean isLazyable = "property".equals( node.getName() ) ||
 				"component".equals( node.getName() ) ||
 				"many-to-one".equals( node.getName() ) ||
 				"one-to-one".equals( node.getName() ) ||
 				"any".equals( node.getName() );
 		if ( isLazyable ) {
 			Attribute lazyNode = node.attribute( "lazy" );
 			property.setLazy( lazyNode != null && "true".equals( lazyNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
-			LOG.debugf( msg );
+			LOG.debug( msg );
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuffer columns = new StringBuffer();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollection(Element node, Collection collection, String className,
 			String path, Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		// ROLENAME
 		collection.setRole(path);
 
 		Attribute inverseNode = node.attribute( "inverse" );
 		if ( inverseNode != null ) {
 			collection.setInverse( "true".equals( inverseNode.getValue() ) );
 		}
 
 		Attribute mutableNode = node.attribute( "mutable" );
 		if ( mutableNode != null ) {
 			collection.setMutable( !"false".equals( mutableNode.getValue() ) );
 		}
 
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		collection.setOptimisticLocked( olNode == null || "true".equals( olNode.getValue() ) );
 
 		Attribute orderNode = node.attribute( "order-by" );
 		if ( orderNode != null ) {
 			collection.setOrderBy( orderNode.getValue() );
 		}
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) {
 			collection.setWhere( whereNode.getValue() );
 		}
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) {
 			collection.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		collection.setNodeName( nodeName );
 		String embed = node.attributeValue( "embed-xml" );
 		collection.setEmbedded( embed==null || "true".equals(embed) );
 
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				collection.setCollectionPersisterClass( ReflectHelper.classForName( persisterNode
 					.getValue() ) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find collection persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		Attribute typeNode = node.attribute( "collection-type" );
 		if ( typeNode != null ) {
 			String typeName = typeNode.getValue();
 			TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 			else {
 				collection.setTypeName( typeName );
 			}
 		}
 
 		// FETCH STRATEGY
 
 		initOuterJoinFetchSetting( node, collection );
 
 		if ( "subselect".equals( node.attributeValue("fetch") ) ) {
 			collection.setSubselectLoadable(true);
 			collection.getOwner().setSubselectLoadableCollections(true);
 		}
 
 		initLaziness( node, collection, mappings, "true", mappings.isDefaultLazy() );
 		//TODO: suck this into initLaziness!
 		if ( "extra".equals( node.attributeValue("lazy") ) ) {
 			collection.setLazy(true);
 			collection.setExtraLazy(true);
 		}
 
 		Element oneToManyNode = node.element( "one-to-many" );
 		if ( oneToManyNode != null ) {
 			OneToMany oneToMany = new OneToMany( mappings, collection.getOwner() );
 			collection.setElement( oneToMany );
 			bindOneToMany( oneToManyNode, oneToMany, mappings );
 			// we have to set up the table later!! yuck
 		}
 		else {
 			// TABLE
 			Attribute tableNode = node.attribute( "table" );
 			String tableName;
 			if ( tableNode != null ) {
 				tableName = mappings.getNamingStrategy().tableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
 				tableName = mappings.getNamingStrategy().collectionTableName(
 						collection.getOwner().getEntityName(),
 						logicalOwnerTableName ,
 						null,
 						null,
 						path
 				);
 				if ( ownerTable.isQuoted() ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 			Attribute schemaNode = node.attribute( "schema" );
 			String schema = schemaNode == null ?
 					mappings.getSchemaName() : schemaNode.getValue();
 
 			Attribute catalogNode = node.attribute( "catalog" );
 			String catalog = catalogNode == null ?
 					mappings.getCatalogName() : catalogNode.getValue();
 
 			Table table = mappings.addTable(
 					schema,
 					catalog,
 					tableName,
 					getSubselect( node ),
 					false
 				);
 			collection.setCollectionTable( table );
 			bindComment(table, node);
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// SORT
 		Attribute sortedAtt = node.attribute( "sort" );
 		// unsorted, natural, comparator.class.name
 		if ( sortedAtt == null || sortedAtt.getValue().equals( "unsorted" ) ) {
 			collection.setSorted( false );
 		}
 		else {
 			collection.setSorted( true );
 			String comparatorClassName = sortedAtt.getValue();
 			if ( !comparatorClassName.equals( "natural" ) ) {
 				collection.setComparatorClassName(comparatorClassName);
 			}
 		}
 
 		// ORPHAN DELETE (used for programmer error detection)
 		Attribute cascadeAtt = node.attribute( "cascade" );
 		if ( cascadeAtt != null && cascadeAtt.getValue().indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, collection );
 		// set up second pass
 		if ( collection instanceof List ) {
 			mappings.addSecondPass( new ListSecondPass( node, mappings, (List) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof Map ) {
 			mappings.addSecondPass( new MapSecondPass( node, mappings, (Map) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof IdentifierCollection ) {
 			mappings.addSecondPass( new IdentifierCollectionSecondPass(
 					node,
 					mappings,
 					collection,
 					inheritedMetas
 				) );
 		}
 		else {
 			mappings.addSecondPass( new CollectionSecondPass( node, mappings, collection, inheritedMetas ) );
 		}
 
 		Iterator iter = node.elementIterator( "filter" );
 		while ( iter.hasNext() ) {
 			final Element filter = (Element) iter.next();
 			parseFilter( filter, collection, mappings );
 		}
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			collection.getSynchronizedTables().add(
 				( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Element element = node.element( "loader" );
 		if ( element != null ) {
 			collection.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 
 		collection.setReferencedPropertyName( node.element( "key" ).attributeValue( "property-ref" ) );
 	}
 
 	private static void initLaziness(
 			Element node,
 			Fetchable fetchable,
 			Mappings mappings,
 			String proxyVal,
 			boolean defaultLazy
 	) {
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean isLazyTrue = lazyNode == null ?
 				defaultLazy && fetchable.isLazy() : //fetch="join" overrides default laziness
 				lazyNode.getValue().equals(proxyVal); //fetch="join" overrides default laziness
 		fetchable.setLazy( isLazyTrue );
 	}
 
 	private static void initLaziness(
 			Element node,
 			ToOne fetchable,
 			Mappings mappings,
 			boolean defaultLazy
 	) {
 		if ( "no-proxy".equals( node.attributeValue( "lazy" ) ) ) {
 			fetchable.setUnwrapProxy(true);
 			fetchable.setLazy(true);
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
 			initLaziness(node, fetchable, mappings, "proxy", defaultLazy);
 		}
 	}
 
 	private static void bindColumnsOrFormula(Element node, SimpleValue simpleValue, String path,
 			boolean isNullable, Mappings mappings) {
 		Attribute formulaNode = node.attribute( "formula" );
 		if ( formulaNode != null ) {
 			Formula f = new Formula();
 			f.setFormula( formulaNode.getText() );
 			simpleValue.addFormula( f );
 		}
 		else {
 			bindColumns( node, simpleValue, isNullable, true, path, mappings );
 		}
 	}
 
 	private static void bindComment(Table table, Element node) {
 		Element comment = node.element("comment");
 		if (comment!=null) table.setComment( comment.getTextTrim() );
 	}
 
 	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
 			boolean isNullable, Mappings mappings) throws MappingException {
 
 		bindColumnsOrFormula( node, manyToOne, path, isNullable, mappings );
 		initOuterJoinFetchSetting( node, manyToOne );
 		initLaziness( node, manyToOne, mappings, true );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) {
 			manyToOne.setReferencedPropertyName( ukName.getValue() );
 		}
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		manyToOne.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 		if( ukName != null && !manyToOne.isIgnoreNotFound() ) {
 			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
 				mappings.addSecondPass( new ManyToOneSecondPass(manyToOne) );
 			}
 		}
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( !manyToOne.isLogicalOneToOne() ) {
 				throw new MappingException(
 						"many-to-one attribute [" + path + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 	}
 
 	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
 			throws MappingException {
 		any.setIdentifierType( getTypeFromXML( node ) );
 		Attribute metaAttribute = node.attribute( "meta-type" );
 		if ( metaAttribute != null ) {
 			any.setMetaType( metaAttribute.getValue() );
 
 			Iterator iter = node.elementIterator( "meta-value" );
 			if ( iter.hasNext() ) {
 				HashMap values = new HashMap();
 				org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( any.getMetaType() );
 				while ( iter.hasNext() ) {
 					Element metaValue = (Element) iter.next();
 					try {
 						Object value = ( (DiscriminatorType) metaType ).stringToObject( metaValue
 							.attributeValue( "value" ) );
 						String entityName = getClassName( metaValue.attribute( "class" ), mappings );
 						values.put( value, entityName );
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
 				}
 				any.setMetaValues( values );
 			}
 
 		}
 
 		bindColumns( node, any, isNullable, false, null, mappings );
 	}
 
 	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
 			Mappings mappings) throws MappingException {
 
 		bindColumns( node, oneToOne, isNullable, false, null, mappings );
 
 		Attribute constrNode = node.attribute( "constrained" );
 		boolean constrained = constrNode != null && constrNode.getValue().equals( "true" );
 		oneToOne.setConstrained( constrained );
 
 		oneToOne.setForeignKeyType( constrained ?
 				ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 				ForeignKeyDirection.FOREIGN_KEY_TO_PARENT );
 
 		initOuterJoinFetchSetting( node, oneToOne );
 		initLaziness( node, oneToOne, mappings, true );
 
 		oneToOne.setEmbedded( "true".equals( node.attributeValue( "embed-xml" ) ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 
 		oneToOne.setPropertyName( node.attributeValue( "name" ) );
 
 		oneToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( oneToOne.isConstrained() ) {
 				throw new MappingException(
 						"one-to-one attribute [" + path + "] does not support orphan delete as it is constrained"
 				);
 			}
 		}
 	}
 
 	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
 			throws MappingException {
 
 		oneToMany.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		oneToMany.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 	}
 
 	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
 		Attribute lengthNode = node.attribute( "length" );
 		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
 		Attribute scalNode = node.attribute( "scale" );
 		if ( scalNode != null ) column.setScale( Integer.parseInt( scalNode.getValue() ) );
 		Attribute precNode = node.attribute( "precision" );
 		if ( precNode != null ) column.setPrecision( Integer.parseInt( precNode.getValue() ) );
 
 		Attribute nullNode = node.attribute( "not-null" );
 		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
 
 		Attribute unqNode = node.attribute( "unique" );
 		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );
 
 		column.setCheckConstraint( node.attributeValue( "check" ) );
 		column.setDefaultValue( node.attributeValue( "default" ) );
 
 		Attribute typeNode = node.attribute( "sql-type" );
 		if ( typeNode != null ) column.setSqlType( typeNode.getValue() );
 
 		String customWrite = node.attributeValue( "write" );
 		if(customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
 			throw new MappingException("write expression must contain exactly one value placeholder ('?') character");
 		}
 		column.setCustomWrite( customWrite );
 		column.setCustomRead( node.attributeValue( "read" ) );
 
 		Element comment = node.element("comment");
 		if (comment!=null) column.setComment( comment.getTextTrim() );
 
 	}
 
 	/**
 	 * Called for arrays and primitive arrays
 	 */
 	public static void bindArray(Element node, Array array, String prefix, String path,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollection( node, array, prefix, path, mappings, inheritedMetas );
 
 		Attribute att = node.attribute( "element-class" );
 		if ( att != null ) array.setElementClassName( getClassName( att, mappings ) );
 
 	}
 
 	private static Class reflectedPropertyClass(String className, String propertyName)
 			throws MappingException {
 		if ( className == null ) return null;
 		return ReflectHelper.reflectedPropertyClass( className, propertyName );
 	}
 
 	public static void bindComposite(Element node, Component component, String path,
 			boolean isNullable, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 		bindComponent(
 				node,
 				component,
 				null,
 				null,
 				path,
 				isNullable,
 				false,
 				mappings,
 				inheritedMetas,
 				false
 			);
 	}
 
 	public static void bindCompositeId(Element node, Component component,
 			PersistentClass persistentClass, String propertyName, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		component.setKey( true );
 
 		String path = StringHelper.qualify(
 				persistentClass.getEntityName(),
 				propertyName == null ? "id" : propertyName );
 
 		bindComponent(
 				node,
 				component,
 				persistentClass.getClassName(),
 				propertyName,
 				path,
 				false,
 				node.attribute( "class" ) == null
 						&& propertyName == null,
 				mappings,
 				inheritedMetas,
 				false
 			);
 
 		if ( "true".equals( node.attributeValue("mapped") ) ) {
 			if ( propertyName!=null ) {
 				throw new MappingException("cannot combine mapped=\"true\" with specified name");
 			}
 			Component mapper = new Component( mappings, persistentClass );
 			bindComponent(
 					node,
 					mapper,
 					persistentClass.getClassName(),
 					null,
 					path,
 					false,
 					true,
 					mappings,
 					inheritedMetas,
 					true
 				);
 			persistentClass.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName("_identifierMapper");
 			property.setNodeName("id");
 			property.setUpdateable(false);
 			property.setInsertable(false);
 			property.setValue(mapper);
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty(property);
 		}
 
 	}
 
 	public static void bindComponent(
 			Element node,
 			Component component,
 			String ownerClassName,
 			String parentProperty,
 			String path,
 			boolean isNullable,
 			boolean isEmbedded,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			boolean isIdentifierMapper) throws MappingException {
 
 		component.setEmbedded( isEmbedded );
 		component.setRoleName( path );
 
 		inheritedMetas = getMetas( node, inheritedMetas );
 		component.setMetaAttributes( inheritedMetas );
 
 		Attribute classNode = isIdentifierMapper ? null : node.attribute( "class" );
 		if ( classNode != null ) {
 			component.setComponentClassName( getClassName( classNode, mappings ) );
 		}
 		else if ( "dynamic-component".equals( node.getName() ) ) {
 			component.setDynamic( true );
 		}
 		else if ( isEmbedded ) {
 			// an "embedded" component (composite ids and unique)
 			// note that this does not handle nested components
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				component.setComponentClassName( component.getOwner().getClassName() );
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 		else {
 			// todo : again, how *should* this work for non-pojo entities?
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				Class reflectedClass = reflectedPropertyClass( ownerClassName, parentProperty );
 				if ( reflectedClass != null ) {
 					component.setComponentClassName( reflectedClass.getName() );
 				}
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setName("_UniqueKey");
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, persistentClass
 					.getClassName(), subnode, mappings, inheritedMetas );
 				if ( !mutable ) property.setUpdateable(false);
 				if ( naturalId ) property.setNaturalIdentifier(true);
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) uniqueKey.addColumns( property.getColumnIterator() );
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
 			);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 4803cb5447..a98d453da6 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,450 +1,450 @@
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
 package org.hibernate.cfg;
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
 	private static final long serialVersionUID = -1194386144994524825L;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	public SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final boolean debugEnabled =  LOG.isDebugEnabled();
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		}
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		}
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				Environment.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		}
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		}
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		if ( debugEnabled ) {
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		}
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
 		if ( statementFetchSize != null && debugEnabled ) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
 		if ( debugEnabled ) {
 			LOG.debugf( "Connection release mode: %s", releaseModeName );
 		}
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
 				LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		if ( defaultSchema != null && debugEnabled ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
 		if ( defaultCatalog != null && debugEnabled ) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
 		if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		}
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		}
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		}
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		}
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		}
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		}
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		}
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		}
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		}
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
 		if ( prefix != null && debugEnabled ) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		}
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		}
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		}
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
 		if ( "validate".equals(autoSchemaExport) ) {
 			settings.setAutoValidateSchema( true );
 		}
 		if ( "update".equals(autoSchemaExport) ) {
 			settings.setAutoUpdateSchema( true );
 		}
 		if ( "create".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema( true );
 		}
 		if ( "create-drop".equals( autoSchemaExport ) ) {
 			settings.setAutoCreateSchema( true );
 			settings.setAutoDropSchema( true );
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
 		if ( debugEnabled ) {
 			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		}
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		}
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
 		if ( debugEnabled ) {
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		}
 		settings.setCheckNullability(checkNullability);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		}
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
-//            LOG.debugf("Using javassist as bytecode provider by default");
+//            LOG.debug("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
 		);
 		LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
 			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( queryCacheFactoryClassName )
 					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 
 	private static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
 						Environment.CACHE_REGION_FACTORY, properties, null
 				)
 		);
 		if ( regionFactoryClassName == null || !cachingEnabled) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
 		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
 				LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 	//todo remove this once we move to new metamodel
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
 						Environment.CACHE_REGION_FACTORY, properties, null
 				)
 		);
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
 		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
 				LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
 		);
 		LOG.debugf( "Query translator: %s", className );
 		try {
 			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( className )
 					.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
index 5cc4a9baf5..ed13771fe7 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
@@ -1,581 +1,581 @@
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
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import javax.persistence.UniqueConstraint;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.Index;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexOrUniqueKeySecondPass;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 
 /**
  * Table related operations
  *
  * @author Emmanuel Bernard
  */
 @SuppressWarnings("unchecked")
 public class TableBinder {
 	//TODO move it to a getter/setter strategy
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TableBinder.class.getName());
 
 	private String schema;
 	private String catalog;
 	private String name;
 	private boolean isAbstract;
 	private List<UniqueConstraintHolder> uniqueConstraints;
 //	private List<String[]> uniqueConstraints;
 	String constraints;
 	Table denormalizedSuperTable;
 	Mappings mappings;
 	private String ownerEntityTable;
 	private String associatedEntityTable;
 	private String propertyName;
 	private String ownerEntity;
 	private String associatedEntity;
 	private boolean isJPA2ElementCollection;
 
 	public void setSchema(String schema) {
 		this.schema = schema;
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = catalog;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public void setAbstract(boolean anAbstract) {
 		isAbstract = anAbstract;
 	}
 
 	public void setUniqueConstraints(UniqueConstraint[] uniqueConstraints) {
 		this.uniqueConstraints = TableBinder.buildUniqueConstraintHolders( uniqueConstraints );
 	}
 
 	public void setConstraints(String constraints) {
 		this.constraints = constraints;
 	}
 
 	public void setDenormalizedSuperTable(Table denormalizedSuperTable) {
 		this.denormalizedSuperTable = denormalizedSuperTable;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public void setJPA2ElementCollection(boolean isJPA2ElementCollection) {
 		this.isJPA2ElementCollection = isJPA2ElementCollection;
 	}
 
 	private static class AssociationTableNameSource implements ObjectNameSource {
 		private final String explicitName;
 		private final String logicalName;
 
 		private AssociationTableNameSource(String explicitName, String logicalName) {
 			this.explicitName = explicitName;
 			this.logicalName = logicalName;
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return logicalName;
 		}
 	}
 
 	// only bind association table currently
 	public Table bind() {
 		//logicalName only accurate for assoc table...
 		final String unquotedOwnerTable = StringHelper.unquote( ownerEntityTable );
 		final String unquotedAssocTable = StringHelper.unquote( associatedEntityTable );
 
 		//@ElementCollection use ownerEntity_property instead of the cleaner ownerTableName_property
 		// ownerEntity can be null when the table name is explicitly set
 		final String ownerObjectName = isJPA2ElementCollection && ownerEntity != null ?
 				StringHelper.unqualify( ownerEntity ) : unquotedOwnerTable;
 		final ObjectNameSource nameSource = buildNameContext(
 				ownerObjectName,
 				unquotedAssocTable );
 
 		final boolean ownerEntityTableQuoted = StringHelper.isQuoted( ownerEntityTable );
 		final boolean associatedEntityTableQuoted = StringHelper.isQuoted( associatedEntityTable );
 		final ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper = new ObjectNameNormalizer.NamingStrategyHelper() {
 			public String determineImplicitName(NamingStrategy strategy) {
 
 				final String strategyResult = strategy.collectionTableName(
 						ownerEntity,
 						ownerObjectName,
 						associatedEntity,
 						unquotedAssocTable,
 						propertyName
 
 				);
 				return ownerEntityTableQuoted || associatedEntityTableQuoted
 						? StringHelper.quote( strategyResult )
 						: strategyResult;
 			}
 
 			public String handleExplicitName(NamingStrategy strategy, String name) {
 				return strategy.tableName( name );
 			}
 		};
 
 		return buildAndFillTable(
 				schema,
 				catalog,
 				nameSource,
 				namingStrategyHelper,
 				isAbstract,
 				uniqueConstraints,
 				constraints,
 				denormalizedSuperTable,
 				mappings,
 				null
 		);
 	}
 
 	private ObjectNameSource buildNameContext(String unquotedOwnerTable, String unquotedAssocTable) {
 		String logicalName = mappings.getNamingStrategy().logicalCollectionTableName(
 				name,
 				unquotedOwnerTable,
 				unquotedAssocTable,
 				propertyName
 		);
 		if ( StringHelper.isQuoted( ownerEntityTable ) || StringHelper.isQuoted( associatedEntityTable ) ) {
 			logicalName = StringHelper.quote( logicalName );
 		}
 
 		return new AssociationTableNameSource( name, logicalName );
 	}
 
 	public static Table buildAndFillTable(
 			String schema,
 			String catalog,
 			ObjectNameSource nameSource,
 			ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper,
 			boolean isAbstract,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings,
 			String subselect) {
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 
 		String realTableName = mappings.getObjectNameNormalizer().normalizeDatabaseIdentifier(
 				nameSource.getExplicitName(),
 				namingStrategyHelper
 		);
 
 		final Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					subselect,
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					subselect,
 					isAbstract
 			);
 		}
 
 		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
 			mappings.addUniqueConstraintHolders( table, uniqueConstraints );
 		}
 
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 
 		// logicalName is null if we are in the second pass
 		final String logicalName = nameSource.getLogicalName();
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 	/**
 	 *
 	 * @param schema
 	 * @param catalog
 	 * @param realTableName
 	 * @param logicalName
 	 * @param isAbstract
 	 * @param uniqueConstraints
 	 * @param constraints
 	 * @param denormalizedSuperTable
 	 * @param mappings
 	 * @return
 	 *
 	 * @deprecated Use {@link #buildAndFillTable} instead.
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public static Table fillTable(
 			String schema,
 			String catalog,
 			String realTableName,
 			String logicalName,
 			boolean isAbstract,
 			List uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 		Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					null, //subselect
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					null, //subselect
 					isAbstract
 			);
 		}
 		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
 			mappings.addUniqueConstraints( table, uniqueConstraints );
 		}
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 		//logicalName is null if we are in the second pass
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 	public static void bindFk(
 			PersistentClass referencedEntity,
 			PersistentClass destinationEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			Mappings mappings) {
 		PersistentClass associatedClass;
 		if ( destinationEntity != null ) {
 			//overridden destination
 			associatedClass = destinationEntity;
 		}
 		else {
 			associatedClass = columns[0].getPropertyHolder() == null
 					? null
 					: columns[0].getPropertyHolder().getPersistentClass();
 		}
 		final String mappedByProperty = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedByProperty ) ) {
 			/**
 			 * Get the columns of the mapped-by property
 			 * copy them and link the copy to the actual value
 			 */
 			LOG.debugf( "Retrieving property %s.%s", associatedClass.getEntityName(), mappedByProperty );
 
 			final Property property = associatedClass.getRecursiveProperty( columns[0].getMappedBy() );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				Collection collection = ( (Collection) property.getValue() );
 				Value element = collection.getElement();
 				if ( element == null ) {
 					throw new AnnotationException(
 							"Illegal use of mappedBy on both sides of the relationship: "
 									+ associatedClass.getEntityName() + "." + mappedByProperty
 					);
 				}
 				mappedByColumns = element.getColumnIterator();
 			}
 			else {
 				mappedByColumns = property.getValue().getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 		}
 		else if ( columns[0].isImplicit() ) {
 			/**
 			 * if columns are implicit, then create the columns based on the
 			 * referenced entity id columns
 			 */
 			Iterator idColumns;
 			if ( referencedEntity instanceof JoinedSubclass ) {
 				idColumns = referencedEntity.getKey().getColumnIterator();
 			}
 			else {
 				idColumns = referencedEntity.getIdentifier().getColumnIterator();
 			}
 			while ( idColumns.hasNext() ) {
 				Column column = (Column) idColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingDefaultColumnNaming( column, referencedEntity, value );
 			}
 		}
 		else {
 			int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, referencedEntity, mappings );
 
 			if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 				String referencedPropertyName;
 				if ( value instanceof ToOne ) {
 					referencedPropertyName = ( (ToOne) value ).getReferencedPropertyName();
 				}
 				else if ( value instanceof DependantValue ) {
 					String propertyName = columns[0].getPropertyName();
 					if ( propertyName != null ) {
 						Collection collection = (Collection) referencedEntity.getRecursiveProperty( propertyName )
 								.getValue();
 						referencedPropertyName = collection.getReferencedPropertyName();
 					}
 					else {
 						throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 					}
 
 				}
 				else {
 					throw new AssertionFailure(
 							"Do a property ref on an unexpected Value type: "
 									+ value.getClass().getName()
 					);
 				}
 				if ( referencedPropertyName == null ) {
 					throw new AssertionFailure(
 							"No property ref found while expected"
 					);
 				}
 				Property synthProp = referencedEntity.getRecursiveProperty( referencedPropertyName );
 				if ( synthProp == null ) {
 					throw new AssertionFailure(
 							"Cannot find synthProp: " + referencedEntity.getEntityName() + "." + referencedPropertyName
 					);
 				}
 				linkJoinColumnWithValueOverridingNameIfImplicit(
 						referencedEntity, synthProp.getColumnIterator(), columns, value
 				);
 
 			}
 			else {
 				if ( Ejb3JoinColumn.NO_REFERENCE == fkEnum ) {
 					//implicit case, we hope PK and FK columns are in the same order
 					if ( columns.length != referencedEntity.getIdentifier().getColumnSpan() ) {
 						throw new AnnotationException(
 								"A Foreign key refering " + referencedEntity.getEntityName()
 										+ " from " + associatedClass.getEntityName()
 										+ " has the wrong number of column. should be " + referencedEntity.getIdentifier()
 										.getColumnSpan()
 						);
 					}
 					linkJoinColumnWithValueOverridingNameIfImplicit(
 							referencedEntity,
 							referencedEntity.getIdentifier().getColumnIterator(),
 							columns,
 							value
 					);
 				}
 				else {
 					//explicit referencedColumnName
 					Iterator idColItr = referencedEntity.getKey().getColumnIterator();
 					org.hibernate.mapping.Column col;
 					Table table = referencedEntity.getTable(); //works cause the pk has to be on the primary table
 					if ( !idColItr.hasNext() ) {
-						LOG.debugf( "No column in the identifier!" );
+						LOG.debug( "No column in the identifier!" );
 					}
 					while ( idColItr.hasNext() ) {
 						boolean match = false;
 						//for each PK column, find the associated FK column.
 						col = (org.hibernate.mapping.Column) idColItr.next();
 						for (Ejb3JoinColumn joinCol : columns) {
 							String referencedColumn = joinCol.getReferencedColumn();
 							referencedColumn = mappings.getPhysicalColumnName( referencedColumn, table );
 							//In JPA 2 referencedColumnName is case insensitive
 							if ( referencedColumn.equalsIgnoreCase( col.getQuotedName() ) ) {
 								//proper join column
 								if ( joinCol.isNameDeferred() ) {
 									joinCol.linkValueUsingDefaultColumnNaming(
 											col, referencedEntity, value
 									);
 								}
 								else {
 									joinCol.linkWithValue( value );
 								}
 								joinCol.overrideFromReferencedColumnIfNecessary( col );
 								match = true;
 								break;
 							}
 						}
 						if ( !match ) {
 							throw new AnnotationException(
 									"Column name " + col.getName() + " of "
 											+ referencedEntity.getEntityName() + " not found in JoinColumns.referencedColumnName"
 							);
 						}
 					}
 				}
 			}
 		}
 		value.createForeignKey();
 		if ( unique ) {
 			createUniqueConstraint( value );
 		}
 	}
 
 	public static void linkJoinColumnWithValueOverridingNameIfImplicit(
 			PersistentClass referencedEntity,
 			Iterator columnIterator,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value) {
 		for (Ejb3JoinColumn joinCol : columns) {
 			Column synthCol = (Column) columnIterator.next();
 			if ( joinCol.isNameDeferred() ) {
 				//this has to be the default value
 				joinCol.linkValueUsingDefaultColumnNaming( synthCol, referencedEntity, value );
 			}
 			else {
 				joinCol.linkWithValue( value );
 				joinCol.overrideFromReferencedColumnIfNecessary( synthCol );
 			}
 		}
 	}
 
 	public static void createUniqueConstraint(Value value) {
 		Iterator iter = value.getColumnIterator();
 		ArrayList cols = new ArrayList();
 		while ( iter.hasNext() ) {
 			cols.add( iter.next() );
 		}
 		value.getTable().createUniqueKey( cols );
 	}
 
 	public static void addIndexes(Table hibTable, Index[] indexes, Mappings mappings) {
 		for (Index index : indexes) {
 			//no need to handle inSecondPass here since it is only called from EntityBinder
 			mappings.addSecondPass(
 					new IndexOrUniqueKeySecondPass( hibTable, index.name(), index.columnNames(), mappings )
 			);
 		}
 	}
 
 	/**
 	 * @deprecated Use {@link #buildUniqueConstraintHolders} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public static List<String[]> buildUniqueConstraints(UniqueConstraint[] constraintsArray) {
 		List<String[]> result = new ArrayList<String[]>();
 		if ( constraintsArray.length != 0 ) {
 			for (UniqueConstraint uc : constraintsArray) {
 				result.add( uc.columnNames() );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Build a list of {@link org.hibernate.cfg.UniqueConstraintHolder} instances given a list of
 	 * {@link UniqueConstraint} annotations.
 	 *
 	 * @param annotations The {@link UniqueConstraint} annotations.
 	 *
 	 * @return The built {@link org.hibernate.cfg.UniqueConstraintHolder} instances.
 	 */
 	public static List<UniqueConstraintHolder> buildUniqueConstraintHolders(UniqueConstraint[] annotations) {
 		List<UniqueConstraintHolder> result;
 		if ( annotations == null || annotations.length == 0 ) {
 			result = java.util.Collections.emptyList();
 		}
 		else {
 			result = new ArrayList<UniqueConstraintHolder>( CollectionHelper.determineProperSizing( annotations.length ) );
 			for ( UniqueConstraint uc : annotations ) {
 				result.add(
 						new UniqueConstraintHolder()
 								.setName( uc.name() )
 								.setColumns( uc.columnNames() )
 				);
 			}
 		}
 		return result;
 	}
 
 	public void setDefaultName(
 			String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable,
 			String propertyName
 	) {
 		this.ownerEntity = ownerEntity;
 		this.ownerEntityTable = ownerEntityTable;
 		this.associatedEntity = associatedEntity;
 		this.associatedEntityTable = associatedEntityTable;
 		this.propertyName = propertyName;
 		this.name = null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index 5a1a1c9558..82eccb094f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,1684 +1,1684 @@
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
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.commons.collections.map.AbstractReferenceMap;
 import org.apache.commons.collections.map.ReferenceMap;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
 
 /**
  * A <tt>PersistenceContext</tt> represents the state of persistent "stuff" which
  * Hibernate is tracking.  This includes persistent entities, collections,
  * as well as proxies generated.
  * </p>
  * There is meant to be a one-to-one correspondence between a SessionImpl and
  * a PersistentContext.  The SessionImpl uses the PersistentContext to track
  * the current state of its context.  Event-listeners then use the
  * PersistentContext to drive their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 
 	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatefulPersistenceContext.class.getName() );
 
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map entitiesByUniqueKey;
 
 	// Identity map of EntityEntry instances, by the entity instance
 	private Map entityEntries;
 
 	// Entity proxies, by EntityKey
 	private Map proxiesByKey;
 
 	// Snapshots of current database state for entities
 	// that have *not* been loaded
 	private Map entitySnapshotsByKey;
 
 	// Identity map of array holder ArrayHolder instances, by the array instance
 	private Map arrayHolders;
 
 	// Identity map of CollectionEntry instances, by the collection wrapper
 	private Map collectionEntries;
 
 	// Collection wrappers, by the CollectionKey
 	private Map collectionsByKey; //key=CollectionKey, value=PersistentCollection
 
 	// Set of EntityKeys of deleted objects
 	private HashSet nullifiableEntityKeys;
 
 	// properties that we have tried to load, and not found in the database
 	private HashSet nullAssociations;
 
 	// A list of collection wrappers that were instantiating during result set
 	// processing, that we will need to initialize at the end of the query
 	private List nonlazyCollections;
 
 	// A container for collections we load up when the owning entity is not
 	// yet loaded ... for now, this is purely transient!
 	private Map<CollectionKey,PersistentCollection> unownedCollections;
 
 	// Parent entities cache by their child for cascading
 	// May be empty or not contains all relation
 	private Map parentsByChild;
 
 	private int cascading = 0;
 	private int loadCounter = 0;
 	private boolean flushing = false;
 
 	private boolean defaultReadOnly = false;
 	private boolean hasNonReadOnlyEntities = false;
 
 	private LoadContexts loadContexts;
 	private BatchFetchQueue batchFetchQueue;
 
 
 
 	/**
 	 * Constructs a PersistentContext, bound to the given session.
 	 *
 	 * @param session The session "owning" this context.
 	 */
 	public StatefulPersistenceContext(SessionImplementor session) {
 		this.session = session;
 
 		entitiesByKey = new HashMap( INIT_COLL_SIZE );
 		entitiesByUniqueKey = new HashMap( INIT_COLL_SIZE );
 		proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK );
 		entitySnapshotsByKey = new HashMap( INIT_COLL_SIZE );
 
 		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionsByKey = new HashMap( INIT_COLL_SIZE );
 		arrayHolders = IdentityMap.instantiate( INIT_COLL_SIZE );
 		parentsByChild = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 
 		nullifiableEntityKeys = new HashSet();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
 		nullAssociations = new HashSet( INIT_COLL_SIZE );
 		nonlazyCollections = new ArrayList( INIT_COLL_SIZE );
 	}
 
 	public boolean isStateless() {
 		return false;
 	}
 
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(8);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		if ( unownedCollections == null ) {
 			return null;
 		}
 		else {
 			return unownedCollections.remove(key);
 		}
 	}
 
 	/**
 	 * Get the <tt>BatchFetchQueue</tt>, instantiating one if
 	 * necessary.
 	 */
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
 	public void clear() {
 		for ( Object o : proxiesByKey.values() ) {
 			final LazyInitializer li = ((HibernateProxy) o).getHibernateLazyInitializer();
 			li.unsetSession();
 		}
 		Map.Entry[] collectionEntryArray = IdentityMap.concurrentEntries( collectionEntries );
 		for ( Map.Entry aCollectionEntryArray : collectionEntryArray ) {
 			((PersistentCollection) aCollectionEntryArray.getKey()).unsetSession( getSession() );
 		}
 		arrayHolders.clear();
 		entitiesByKey.clear();
 		entitiesByUniqueKey.clear();
 		entityEntries.clear();
 		parentsByChild.clear();
 		entitySnapshotsByKey.clear();
 		collectionsByKey.clear();
 		collectionEntries.clear();
 		if ( unownedCollections != null ) {
 			unownedCollections.clear();
 		}
 		proxiesByKey.clear();
 		nullifiableEntityKeys.clear();
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.clear();
 		}
 		// defaultReadOnly is unaffected by clear()
 		hasNonReadOnlyEntities = false;
 		if ( loadContexts != null ) {
 			loadContexts.cleanup();
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus(status);
 		setHasNonReadOnlyEnties(status);
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		// Downgrade locks
 		for ( Object o : entityEntries.values() ) {
 			((EntityEntry) o).setLockMode( LockMode.NONE );
 		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		final EntityKey key = session.generateEntityKey( id, persister );
 		Object cached = entitySnapshotsByKey.get(key);
 		if (cached!=null) {
 			return cached==NO_ROW ? null : (Object[]) cached;
 		}
 		else {
 			Object[] snapshot = persister.getDatabaseSnapshot( id, session );
 			entitySnapshotsByKey.put( key, snapshot==null ? NO_ROW : snapshot );
 			return snapshot;
 		}
 	}
 
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// if the natural-id is marked as non-mutable, it is not retrieved during a
 		// normal database-snapshot operation...
 		int[] props = persister.getNaturalIdentifierProperties();
 		boolean[] updateable = persister.getPropertyUpdateability();
 		boolean allNatualIdPropsAreUpdateable = true;
 		for ( int i = 0; i < props.length; i++ ) {
 			if ( !updateable[ props[i] ] ) {
 				allNatualIdPropsAreUpdateable = false;
 				break;
 			}
 		}
 
 		if ( allNatualIdPropsAreUpdateable ) {
 			// do this when all the properties are updateable since there is
 			// a certain likelihood that the information will already be
 			// snapshot-cached.
 			Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
 			if ( entitySnapshot == NO_ROW ) {
 				return null;
 			}
 			Object[] naturalIdSnapshot = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshot[i] = entitySnapshot[ props[i] ];
 			}
 			return naturalIdSnapshot;
 		}
 		else {
 			return persister.getNaturalIdentifierSnapshot( id, session );
 		}
 	}
 
 	/**
 	 * Retrieve the cached database snapshot for the requested entity key.
 	 * <p/>
 	 * This differs from {@link #getDatabaseSnapshot} is two important respects:<ol>
 	 * <li>no snapshot is obtained from the database if not already cached</li>
 	 * <li>an entry of {@link #NO_ROW} here is interpretet as an exception</li>
 	 * </ol>
 	 * @param key The entity key for which to retrieve the cached snapshot
 	 * @return The cached snapshot
 	 * @throws IllegalStateException if the cached snapshot was == {@link #NO_ROW}.
 	 */
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException( "persistence context reported no row snapshot for " + MessageHelper.infoString( key.getEntityName(), key.getIdentifier() ) );
 		}
 		return ( Object[] ) snapshot;
 	}
 
 	/*public void removeDatabaseSnapshot(EntityKey key) {
 		entitySnapshotsByKey.remove(key);
 	}*/
 
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put(key, entity);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 	}
 
 	/**
 	 * Get the entity instance associated with the given
 	 * <tt>EntityKey</tt>
 	 */
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get(key);
 	}
 
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey(key);
 	}
 
 	/**
 	 * Remove an entity from the session cache, also clear
 	 * up other state associated with the entity, all except
 	 * for the <tt>EntityEntry</tt>
 	 */
 	public Object removeEntity(EntityKey key) {
 		Object entity = entitiesByKey.remove(key);
 		Iterator iter = entitiesByUniqueKey.values().iterator();
 		while ( iter.hasNext() ) {
 			if ( iter.next()==entity ) iter.remove();
 		}
 		// Clear all parent cache
 		parentsByChild.clear();
 		entitySnapshotsByKey.remove(key);
 		nullifiableEntityKeys.remove(key);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 		getBatchFetchQueue().removeSubselect(key);
 		return entity;
 	}
 
 	/**
 	 * Get an entity cached by unique key
 	 */
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get(euk);
 	}
 
 	/**
 	 * Add an entity to the cache by unique key
 	 */
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put(euk, entity);
 	}
 
 	/**
 	 * Retreive the EntityEntry representation of the given entity.
 	 *
 	 * @param entity The entity for which to locate the EntityEntry.
 	 * @return The EntityEntry for the given entity.
 	 */
 	public EntityEntry getEntry(Object entity) {
 		return (EntityEntry) entityEntries.get(entity);
 	}
 
 	/**
 	 * Remove an entity entry from the session cache
 	 */
 	public EntityEntry removeEntry(Object entity) {
 		return (EntityEntry) entityEntries.remove(entity);
 	}
 
 	/**
 	 * Is there an EntityEntry for this instance?
 	 */
 	public boolean isEntryFor(Object entity) {
 		return entityEntries.containsKey(entity);
 	}
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 */
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return (CollectionEntry) collectionEntries.get(coll);
 	}
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
 	public EntityEntry addEntity(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final EntityKey entityKey,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched
 	) {
 
 		addEntity( entityKey, entity );
 
 		return addEntry(
 				entity,
 				status,
 				loadedState,
 				null,
 				entityKey.getIdentifier(),
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
 			);
 	}
 
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it
 	 * to the event source's internal caches.
 	 */
 	public EntityEntry addEntry(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 
 		EntityEntry e = new EntityEntry(
 				status,
 				loadedState,
 				rowId,
 				id,
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				persister.getEntityMode(),
 				session.getTenantIdentifier(),
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
 		);
 		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties(status);
 		return e;
 	}
 
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey(collection);
 	}
 
 	public boolean containsProxy(Object entity) {
 		return proxiesByKey.containsValue( entity );
 	}
 
 	/**
 	 * Takes the given object and, if it represents a proxy, reassociates it with this event source.
 	 *
 	 * @param value The possible proxy to be reassociated.
 	 * @return Whether the passed value represented an actual proxy which got initialized.
 	 * @throws MappingException
 	 */
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( !Hibernate.isInitialized(value) ) {
 			HibernateProxy proxy = (HibernateProxy) value;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy(li, proxy);
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * If a deleted entity instance is re-saved, and it has a proxy, we need to
 	 * reset the identifier of the proxy
 	 */
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
 			HibernateProxy proxy = (HibernateProxy) value;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			li.setIdentifier(id);
 			reassociateProxy(li, proxy);
 		}
 	}
 
 	/**
 	 * Associate a proxy that was instantiated by another session with this session
 	 *
 	 * @param li The proxy initializer.
 	 * @param proxy The proxy to reassociate.
 	 */
 	private void reassociateProxy(LazyInitializer li, HibernateProxy proxy) {
 		if ( li.getSession() != this.getSession() ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( li.getEntityName() );
 			final EntityKey key = session.generateEntityKey( li.getIdentifier(), persister );
 		  	// any earlier proxy takes precedence
 			if ( !proxiesByKey.containsKey( key ) ) {
 				proxiesByKey.put( key, proxy );
 			}
 			proxy.getHibernateLazyInitializer().setSession( session );
 		}
 	}
 
 	/**
 	 * Get the entity instance underlying the given proxy, throwing
 	 * an exception if the proxy is uninitialized. If the given object
 	 * is not a proxy, simply return the argument.
 	 */
 	public Object unproxy(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				throw new PersistentObjectException(
 						"object was an uninitialized proxy for " +
 						li.getEntityName()
 				);
 			}
 			return li.getImplementation(); //unwrap the object
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	/**
 	 * Possibly unproxy the given reference and reassociate it with the current session.
 	 *
 	 * @param maybeProxy The reference to be unproxied if it currently represents a proxy.
 	 * @return The unproxied instance.
 	 * @throws HibernateException
 	 */
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy(li, proxy);
 			return li.getImplementation(); //initialize + unwrap the object
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	/**
 	 * Attempts to check whether the given key represents an entity already loaded within the
 	 * current session.
 	 * @param object The entity reference against which to perform the uniqueness check.
 	 * @throws HibernateException
 	 */
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
 		Object entity = getEntity(key);
 		if ( entity == object ) {
 			throw new AssertionFailure( "object already associated, but no entry was found" );
 		}
 		if ( entity != null ) {
 			throw new NonUniqueObjectException( key.getIdentifier(), key.getEntityName() );
 		}
 	}
 
 	/**
 	 * If the existing proxy is insufficiently "narrow" (derived), instantiate a new proxy
 	 * and overwrite the registration of the old one. This breaks == and occurs only for
 	 * "class" proxies rather than "interface" proxies. Also init the proxy to point to
 	 * the given target implementation if necessary.
 	 *
 	 * @param proxy The proxy instance to be narrowed.
 	 * @param persister The persister for the proxied entity.
 	 * @param key The internal cache key for the proxied entity.
 	 * @param object (optional) the actual proxied entity instance.
 	 * @return An appropriately narrowed instance.
 	 * @throws HibernateException
 	 */
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
 		boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
 			if ( object != null ) {
 				proxiesByKey.remove(key);
 				return object; //return the proxied object
 			}
 			else {
 				proxy = persister.createProxy( key.getIdentifier(), session );
 				Object proxyOrig = proxiesByKey.put(key, proxy); //overwrite old proxy
 				if ( proxyOrig != null ) {
 					if ( ! ( proxyOrig instanceof HibernateProxy ) ) {
 						throw new AssertionFailure(
 								"proxy not of type HibernateProxy; it is " + proxyOrig.getClass()
 						);
 					}
 					// set the read-only/modifiable mode in the new proxy to what it was in the original proxy
 					boolean readOnlyOrig = ( ( HibernateProxy ) proxyOrig ).getHibernateLazyInitializer().isReadOnly();
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
 				}
 				return proxy;
 			}
 		}
 		else {
 
 			if ( object != null ) {
 				LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 				li.setImplementation(object);
 			}
 
 			return proxy;
 
 		}
 
 	}
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * third argument (the entity associated with the key) if no proxy exists. Init
 	 * the proxy to the target implementation, if necessary.
 	 */
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)
 	throws HibernateException {
 		if ( !persister.hasProxy() ) return impl;
 		Object proxy = proxiesByKey.get(key);
 		if ( proxy != null ) {
 			return narrowProxy(proxy, persister, key, impl);
 		}
 		else {
 			return impl;
 		}
 	}
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * argument (the entity associated with the key) if no proxy exists.
 	 * (slower than the form above)
 	 */
 	public Object proxyFor(Object impl) throws HibernateException {
 		EntityEntry e = getEntry(impl);
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
 		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 	}
 
 	/**
 	 * Get the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner, if its entity ID is available from the collection's loaded key
 	 * and the owner entity is in the persistence context; otherwise, returns null
 	 */
 	public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
 		CollectionEntry ce = getCollectionEntry( collection );
 		if ( ce.getLoadedPersister() == null ) {
 			return null; // early exit...
 		}
 		Object loadedOwner = null;
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// return collection.getOwner()
 		Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
 		if ( entityId != null ) {
 			loadedOwner = getCollectionOwner( entityId, ce.getLoadedPersister() );
 		}
 		return loadedOwner;
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection) {
 		return getLoadedCollectionOwnerIdOrNull( getCollectionEntry( collection ) );
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param ce The collection entry
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	private Serializable getLoadedCollectionOwnerIdOrNull(CollectionEntry ce) {
 		if ( ce == null || ce.getLoadedKey() == null || ce.getLoadedPersister() == null ) {
 			return null;
 		}
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// get the ID from collection.getOwner()
 		return ce.getLoadedPersister().getCollectionType().getIdOfOwnerOrNull( ce.getLoadedKey(), session );
 	}
 
 	/**
 	 * add a collection we just loaded up (still needs initializing)
 	 */
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		addCollection(collection, ce, id);
 	}
 
 	/**
 	 * add a detached uninitialized collection
 	 */
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 	}
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 	throws HibernateException {
 		addCollection(collection, persister);
 	}
 
 	/**
 	 * Add an collection to the cache, with a given collection entry.
 	 *
 	 * @param coll The collection for which we are adding an entry.
 	 * @param entry The entry representing the collection.
 	 * @param key The key of the collection's entry.
 	 */
 	private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
 		collectionEntries.put( coll, entry );
 		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
 		PersistentCollection old = ( PersistentCollection ) collectionsByKey.put( collectionKey, coll );
 		if ( old != null ) {
 			if ( old == coll ) {
 				throw new AssertionFailure("bug adding collection twice");
 			}
 			// or should it actually throw an exception?
 			old.unsetSession( session );
 			collectionEntries.remove( old );
 			// watch out for a case where old is still referenced
 			// somewhere in the object graph! (which is a user error)
 		}
 	}
 
 	/**
 	 * Add a collection to the cache, creating a new collection entry for it
 	 *
 	 * @param collection The collection for which we are adding an entry.
 	 * @param persister The collection persister
 	 */
 	private void addCollection(PersistentCollection collection, CollectionPersister persister) {
 		CollectionEntry ce = new CollectionEntry( persister, collection );
 		collectionEntries.put( collection, ce );
 	}
 
 	/**
 	 * add an (initialized) collection that was created by another session and passed
 	 * into update() (ie. one with a snapshot and existing state on the database)
 	 */
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection)
 	throws HibernateException {
 		if ( collection.isUnreferenced() ) {
 			//treat it just like a new collection
 			addCollection( collection, collectionPersister );
 		}
 		else {
 			CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
 			addCollection( collection, ce, collection.getKey() );
 		}
 	}
 
 	/**
 	 * add a collection we just pulled out of the cache (does not need initializing)
 	 */
 	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id)
 	throws HibernateException {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		ce.postInitialize(collection);
 		addCollection(collection, ce, id);
 		return ce;
 	}
 
 	/**
 	 * Get the collection instance associated with the <tt>CollectionKey</tt>
 	 */
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return (PersistentCollection) collectionsByKey.get(collectionKey);
 	}
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add(collection);
 	}
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
-			LOG.debugf( "Initializing non-lazy collections" );
+			LOG.debug( "Initializing non-lazy collections" );
 			//do this work only at the very highest level of the load
 			loadCounter++; //don't let this method be called recursively
 			try {
 				int size;
 				while ( ( size = nonlazyCollections.size() ) > 0 ) {
 					//note that each iteration of the loop may add new elements
 					( (PersistentCollection) nonlazyCollections.remove( size - 1 ) ).forceInitialization();
 				}
 			}
 			finally {
 				loadCounter--;
 				clearNullProperties();
 			}
 		}
 	}
 
 
 	/**
 	 * Get the <tt>PersistentCollection</tt> object for an array
 	 */
 	public PersistentCollection getCollectionHolder(Object array) {
 		return (PersistentCollection) arrayHolders.get(array);
 	}
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading
 	 * array, since the array instance is not created until endLoad().
 	 */
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return (PersistentCollection) arrayHolders.remove(array);
 	}
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry(coll).getSnapshot();
 	}
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
 	public CollectionEntry getCollectionEntryOrNull(Object collection) {
 		PersistentCollection coll;
 		if ( collection instanceof PersistentCollection ) {
 			coll = (PersistentCollection) collection;
 			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
 		}
 		else {
 			coll = getCollectionHolder(collection);
 			if ( coll == null ) {
 				//it might be an unwrapped collection reference!
 				//try to find a wrapper (slowish)
 				Iterator wrappers = IdentityMap.keyIterator(collectionEntries);
 				while ( wrappers.hasNext() ) {
 					PersistentCollection pc = (PersistentCollection) wrappers.next();
 					if ( pc.isWrapper(collection) ) {
 						coll = pc;
 						break;
 					}
 				}
 			}
 		}
 
 		return (coll == null) ? null : getCollectionEntry(coll);
 	}
 
 	/**
 	 * Get an existing proxy by key
 	 */
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get(key);
 	}
 
 	/**
 	 * Add a proxy to the session cache
 	 */
 	public void addProxy(EntityKey key, Object proxy) {
 		proxiesByKey.put(key, proxy);
 	}
 
 	/**
 	 * Remove a proxy from the session cache.
 	 * <p/>
 	 * Additionally, ensure that any load optimization references
 	 * such as batch or subselect loading get cleaned up as well.
 	 *
 	 * @param key The key of the entity proxy to be removed
 	 * @return The proxy reference.
 	 */
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	/**
 	 * Record the fact that an entity does not exist in the database
 	 *
 	 * @param key the primary key of the entity
 	 */
 	/*public void addNonExistantEntityKey(EntityKey key) {
 		nonExistantEntityKeys.add(key);
 	}*/
 
 	/**
 	 * Record the fact that an entity does not exist in the database
 	 *
 	 * @param key a unique key of the entity
 	 */
 	/*public void addNonExistantEntityUniqueKey(EntityUniqueKey key) {
 		nonExistentEntityUniqueKeys.add(key);
 	}*/
 
 	/*public void removeNonExist(EntityKey key) {
 		nonExistantEntityKeys.remove(key);
 	}*/
 
 	/**
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
 	public Map getEntityEntries() {
 		return entityEntries;
 	}
 
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
 	/**
 	 * Do we already know that the entity does not exist in the
 	 * database?
 	 */
 	/*public boolean isNonExistant(EntityKey key) {
 		return nonExistantEntityKeys.contains(key);
 	}*/
 
 	/**
 	 * Do we already know that the entity does not exist in the
 	 * database?
 	 */
 	/*public boolean isNonExistant(EntityUniqueKey key) {
 		return nonExistentEntityUniqueKeys.contains(key);
 	}*/
 
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
 	public boolean isFlushing() {
 		return flushing;
 	}
 
 	public void setFlushing(boolean flushing) {
 		this.flushing = flushing;
 	}
 
 	/**
 	 * Call this before beginning a two-phase load
 	 */
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	public void afterLoad() {
 		loadCounter--;
 	}
 
 	public boolean isLoadFinished() {
 		return loadCounter == 0;
 	}
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	@Override
     public String toString() {
 		return new StringBuffer()
 				.append("PersistenceContext[entityKeys=")
 				.append(entitiesByKey.keySet())
 				.append(",collectionKeys=")
 				.append(collectionsByKey.keySet())
 				.append("]")
 				.toString();
 	}
 
 	/**
 	 * Search <tt>this</tt> persistence context for an associated entity instance which is considered the "owner" of
 	 * the given <tt>childEntity</tt>, and return that owner's id value.  This is performed in the scenario of a
 	 * uni-directional, non-inverse one-to-many collection (which means that the collection elements do not maintain
 	 * a direct reference to the owner).
 	 * <p/>
 	 * As such, the processing here is basically to loop over every entity currently associated with this persistence
 	 * context and for those of the correct entity (sub) type to extract its collection role property value and see
 	 * if the child is contained within that collection.  If so, we have found the owner; if not, we go on.
 	 * <p/>
 	 * Also need to account for <tt>mergeMap</tt> which acts as a local copy cache managed for the duration of a merge
 	 * operation.  It represents a map of the detached entity instances pointing to the corresponding managed instance.
 	 *
 	 * @param entityName The entity name for the entity type which would own the child
 	 * @param propertyName The name of the property on the owning entity type which would name this child association.
 	 * @param childEntity The child entity instance for which to locate the owner instance id.
 	 * @param mergeMap A map of non-persistent instances from an on-going merge operation (possibly null).
 	 *
 	 * @return The id of the entityName instance which is said to own the child; null if an appropriate owner not
 	 * located.
 	 */
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = ( EntityEntry ) entityEntries.get( parent );
 			//there maybe more than one parent, filter by type
 			if ( 	persister.isSubclassEntityName(entityEntry.getEntityName() )
 					&& isFoundInParent( propertyName, childEntity, persister, collectionPersister, parent ) ) {
 				return getEntry( parent ).getId();
 			}
 			else {
 				parentsByChild.remove( childEntity ); // remove wrong entry
 			}
 		}
 
 		//not found in case, proceed
 		// iterate all the entities currently associated with the persistence context.
 		Iterator entities = IdentityMap.entries(entityEntries).iterator();
 		while ( entities.hasNext() ) {
 			final Map.Entry me = ( Map.Entry ) entities.next();
 			final EntityEntry entityEntry = ( EntityEntry ) me.getValue();
 			// does this entity entry pertain to the entity persister in which we are interested (owner)?
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				final Object entityEntryInstance = me.getKey();
 
 				//check if the managed object is the parent
 				boolean found = isFoundInParent(
 						propertyName,
 						childEntity,
 						persister,
 						collectionPersister,
 						entityEntryInstance
 				);
 
 				if ( !found && mergeMap != null ) {
 					//check if the detached object being merged is the parent
 					Object unmergedInstance = mergeMap.get( entityEntryInstance );
 					Object unmergedChild = mergeMap.get( childEntity );
 					if ( unmergedInstance != null && unmergedChild != null ) {
 						found = isFoundInParent(
 								propertyName,
 								unmergedChild,
 								persister,
 								collectionPersister,
 								unmergedInstance
 						);
 					}
 				}
 
 				if ( found ) {
 					return entityEntry.getId();
 				}
 
 			}
 		}
 
 		// if we get here, it is possible that we have a proxy 'in the way' of the merge map resolution...
 		// 		NOTE: decided to put this here rather than in the above loop as I was nervous about the performance
 		//		of the loop-in-loop especially considering this is far more likely the 'edge case'
 		if ( mergeMap != null ) {
 			Iterator mergeMapItr = mergeMap.entrySet().iterator();
 			while ( mergeMapItr.hasNext() ) {
 				final Map.Entry mergeMapEntry = ( Map.Entry ) mergeMapItr.next();
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
 					final HibernateProxy proxy = ( HibernateProxy ) mergeMapEntry.getKey();
 					if ( persister.isSubclassEntityName( proxy.getHibernateLazyInitializer().getEntityName() ) ) {
 						boolean found = isFoundInParent(
 								propertyName,
 								childEntity,
 								persister,
 								collectionPersister,
 								mergeMap.get( proxy )
 						);
 						if ( !found ) {
 							found = isFoundInParent(
 									propertyName,
 									mergeMap.get( childEntity ),
 									persister,
 									collectionPersister,
 									mergeMap.get( proxy )
 							);
 						}
 						if ( found ) {
 							return proxy.getHibernateLazyInitializer().getIdentifier();
 						}
 					}
 				}
 			}
 		}
 
 		return null;
 	}
 
 	private boolean isFoundInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent) {
 		Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	/**
 	 * Search the persistence context for an index of the child object,
 	 * given a collection role
 	 */
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 
 		EntityPersister persister = session.getFactory()
 				.getEntityPersister(entity);
 		CollectionPersister cp = session.getFactory()
 				.getCollectionPersister(entity + '.' + property);
 
 	    // try cache lookup first
 	    Object parent = parentsByChild.get(childEntity);
 		if (parent != null) {
 			final EntityEntry entityEntry = (EntityEntry) entityEntries.get(parent);
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				Object index = getIndexInParent(property, childEntity, persister, cp, parent);
 
 				if (index==null && mergeMap!=null) {
 					Object unmergedInstance = mergeMap.get(parent);
 					Object unmergedChild = mergeMap.get(childEntity);
 					if ( unmergedInstance!=null && unmergedChild!=null ) {
 						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
 					}
 				}
 				if (index!=null) {
 					return index;
 				}
 			}
 			else {
 				parentsByChild.remove(childEntity); // remove wrong entry
 			}
 		}
 
 		//Not found in cache, proceed
 		Iterator entities = IdentityMap.entries(entityEntries).iterator();
 		while ( entities.hasNext() ) {
 			Map.Entry me = (Map.Entry) entities.next();
 			EntityEntry ee = (EntityEntry) me.getValue();
 			if ( persister.isSubclassEntityName( ee.getEntityName() ) ) {
 				Object instance = me.getKey();
 
 				Object index = getIndexInParent(property, childEntity, persister, cp, instance);
 
 				if (index==null && mergeMap!=null) {
 					Object unmergedInstance = mergeMap.get(instance);
 					Object unmergedChild = mergeMap.get(childEntity);
 					if ( unmergedInstance!=null && unmergedChild!=null ) {
 						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
 					}
 				}
 
 				if (index!=null) return index;
 			}
 		}
 		return null;
 	}
 
 	private Object getIndexInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent
 	){
 		Object collection = persister.getPropertyValue( potentialParent, property );
 		if ( collection!=null && Hibernate.isInitialized(collection) ) {
 			return collectionPersister.getCollectionType().indexOf(collection, childEntity);
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Record the fact that the association belonging to the keyed
 	 * entity is null.
 	 */
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	private void clearNullProperties() {
 		nullAssociations.clear();
 	}
 
 	public boolean isReadOnly(Object entityOrProxy) {
 		if ( entityOrProxy == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		boolean isReadOnly;
 		if ( entityOrProxy instanceof HibernateProxy ) {
 			isReadOnly = ( ( HibernateProxy ) entityOrProxy ).getHibernateLazyInitializer().isReadOnly();
 		}
 		else {
 			EntityEntry ee =  getEntry( entityOrProxy );
 			if ( ee == null ) {
 				throw new TransientObjectException("Instance was not associated with this persistence context" );
 			}
 			isReadOnly = ee.isReadOnly();
 		}
 		return isReadOnly;
 	}
 
 	public void setReadOnly(Object object, boolean readOnly) {
 		if ( object == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		if ( isReadOnly( object ) == readOnly ) {
 			return;
 		}
 		if ( object instanceof HibernateProxy ) {
 			HibernateProxy proxy = ( HibernateProxy ) object;
 			setProxyReadOnly( proxy, readOnly );
 			if ( Hibernate.isInitialized( proxy ) ) {
 				setEntityReadOnly(
 						proxy.getHibernateLazyInitializer().getImplementation(),
 						readOnly
 				);
 			}
 		}
 		else {
 			setEntityReadOnly( object, readOnly );
 			// PersistenceContext.proxyFor( entity ) returns entity if there is no proxy for that entity
 			// so need to check the return value to be sure it is really a proxy
 			Object maybeProxy = getSession().getPersistenceContext().proxyFor( object );
 			if ( maybeProxy instanceof HibernateProxy ) {
 				setProxyReadOnly( ( HibernateProxy ) maybeProxy, readOnly );
 			}
 		}
 	}
 
 	private void setProxyReadOnly(HibernateProxy proxy, boolean readOnly) {
 		if ( proxy.getHibernateLazyInitializer().getSession() != getSession() ) {
 			throw new AssertionFailure(
 					"Attempt to set a proxy to read-only that is associated with a different session" );
 		}
 		proxy.getHibernateLazyInitializer().setReadOnly( readOnly );
 	}
 
 	private void setEntityReadOnly(Object entity, boolean readOnly) {
 		EntityEntry entry = getEntry(entity);
 		if (entry == null) {
 			throw new TransientObjectException("Instance was not associated with this persistence context" );
 		}
 		entry.setReadOnly(readOnly, entity );
 		hasNonReadOnlyEntities = hasNonReadOnlyEntities || ! readOnly;
 	}
 
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		Object entity = entitiesByKey.remove( oldKey );
 		EntityEntry oldEntry = ( EntityEntry ) entityEntries.remove( entity );
 		parentsByChild.clear();
 
 		final EntityKey newKey = session.generateEntityKey( generatedId, oldEntry.getPersister() );
 		addEntity( newKey, entity );
 		addEntry(
 				entity,
 		        oldEntry.getStatus(),
 		        oldEntry.getLoadedState(),
 		        oldEntry.getRowId(),
 		        generatedId,
 		        oldEntry.getVersion(),
 		        oldEntry.getLockMode(),
 		        oldEntry.isExistsInDatabase(),
 		        oldEntry.getPersister(),
 		        oldEntry.isBeingReplicated(),
 		        oldEntry.isLoadedWithLazyPropertiesUnfetched()
 		);
 	}
 
 	/**
 	 * Used by the owning session to explicitly control serialization of the
 	 * persistence context.
 	 *
 	 * @param oos The stream to which the persistence context should get written
 	 * @throws IOException serialization errors.
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace( "Serializing persistent-context" );
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries");
 		Iterator itr = entitiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitiesByUniqueKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries");
 		itr = entitiesByUniqueKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityUniqueKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( proxiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries");
 		itr = proxiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( (EntityKey) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitySnapshotsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries");
 		itr = entitySnapshotsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entityEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entityEntries.size() + "] entityEntries entries");
 		itr = entityEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( EntityEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( collectionsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries");
 		itr = collectionsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( CollectionKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( collectionEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries");
 		itr = collectionEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( CollectionEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( arrayHolders.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries");
 		itr = arrayHolders.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( nullifiableEntityKeys.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries");
 		itr = nullifiableEntityKeys.iterator();
 		while ( itr.hasNext() ) {
 			EntityKey entry = ( EntityKey ) itr.next();
 			entry.serialize( oos );
 		}
 	}
 
 	public static StatefulPersistenceContext deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace("Serializing persistent-context");
 		StatefulPersistenceContext rtn = new StatefulPersistenceContext( session );
 
 		// during deserialization, we need to reconnect all proxies and
 		// collections to this session, as well as the EntityEntry and
 		// CollectionEntry instances; these associations are transient
 		// because serialization is used for different things.
 
 		try {
 			rtn.defaultReadOnly = ois.readBoolean();
 			// todo : we can actually just determine this from the incoming EntityEntry-s
 			rtn.hasNonReadOnlyEntities = ois.readBoolean();
 
 			int count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByKey entries");
 			rtn.entitiesByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
 			rtn.entitiesByUniqueKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
 			rtn.proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK, count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count, .75f );
 			for ( int i = 0; i < count; i++ ) {
 				EntityKey ek = EntityKey.deserialize( ois, session );
 				Object proxy = ois.readObject();
 				if ( proxy instanceof HibernateProxy ) {
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setSession( session );
 					rtn.proxiesByKey.put( ek, proxy );
 				} else {
 					if ( tracing ) LOG.trace("Encountered prunded proxy");
 				}
 				// otherwise, the proxy was pruned during the serialization process
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitySnapshotsByKey entries");
 			rtn.entitySnapshotsByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entityEntries entries");
 			rtn.entityEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				Object entity = ois.readObject();
 				EntityEntry entry = EntityEntry.deserialize( ois, session );
 				rtn.entityEntries.put( entity, entry );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
 			rtn.collectionsByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.collectionsByKey.put( CollectionKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionEntries entries");
 			rtn.collectionEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				final PersistentCollection pc = ( PersistentCollection ) ois.readObject();
 				final CollectionEntry ce = CollectionEntry.deserialize( ois, session );
 				pc.setCurrentSession( session );
 				rtn.collectionEntries.put( pc, ce );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] arrayHolders entries");
 			rtn.arrayHolders = IdentityMap.instantiate( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.arrayHolders.put( ois.readObject(), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] nullifiableEntityKey entries");
 			rtn.nullifiableEntityKeys = new HashSet();
 			for ( int i = 0; i < count; i++ ) {
 				rtn.nullifiableEntityKeys.add( EntityKey.deserialize( ois, session ) );
 			}
 
 		}
 		catch ( HibernateException he ) {
 			throw new InvalidObjectException( he.getMessage() );
 		}
 
 		return rtn;
 	}
 
 	/**
 	 * @see org.hibernate.engine.spi.PersistenceContext#addChildParent(java.lang.Object, java.lang.Object)
 	 */
 	public void addChildParent(Object child, Object parent) {
 		parentsByChild.put(child, parent);
 	}
 
 	/**
 	 * @see org.hibernate.engine.spi.PersistenceContext#removeChildParent(java.lang.Object)
 	 */
 	public void removeChildParent(Object child) {
 	   parentsByChild.remove(child);
 	}
 
 
 	private HashMap<String,List<Serializable>> insertedKeysMap;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
 		// we only are about regsitering these if the persister defines caching
 		if ( persister.hasCache() ) {
 			if ( insertedKeysMap == null ) {
 				insertedKeysMap = new HashMap<String, List<Serializable>>();
 			}
 			final String rootEntityName = persister.getRootEntityName();
 			List<Serializable> insertedEntityIds = insertedKeysMap.get( rootEntityName );
 			if ( insertedEntityIds == null ) {
 				insertedEntityIds = new ArrayList<Serializable>();
 				insertedKeysMap.put( rootEntityName, insertedEntityIds );
 			}
 			insertedEntityIds.add( id );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id) {
 		// again, we only really care if the entity is cached
 		if ( persister.hasCache() ) {
 			if ( insertedKeysMap != null ) {
 				List<Serializable> insertedEntityIds = insertedKeysMap.get( persister.getRootEntityName() );
 				if ( insertedEntityIds != null ) {
 					return insertedEntityIds.contains( id );
 				}
 			}
 		}
 		return false;
 	}
 
 	private void cleanUpInsertedKeysAfterTransaction() {
 		if ( insertedKeysMap != null ) {
 			insertedKeysMap.clear();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
index 160d27ef78..1f7454a41d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/AbstractBatchImpl.java
@@ -1,209 +1,209 @@
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
 package org.hibernate.engine.jdbc.batch.internal;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.batch.spi.BatchObserver;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Convenience base class for implementors of the Batch interface.
  *
  * @author Steve Ebersole
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
 public abstract class AbstractBatchImpl implements Batch {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, AbstractBatchImpl.class.getName());
 
 	private final BatchKey key;
 	private final JdbcCoordinator jdbcCoordinator;
 	private LinkedHashMap<String,PreparedStatement> statements = new LinkedHashMap<String,PreparedStatement>();
 	private LinkedHashSet<BatchObserver> observers = new LinkedHashSet<BatchObserver>();
 
 	protected AbstractBatchImpl(BatchKey key, JdbcCoordinator jdbcCoordinator) {
 		if ( key == null ) {
 			throw new IllegalArgumentException( "batch key cannot be null" );
 		}
 		if ( jdbcCoordinator == null ) {
 			throw new IllegalArgumentException( "JDBC coordinator cannot be null" );
 		}
 		this.key = key;
 		this.jdbcCoordinator = jdbcCoordinator;
 	}
 
 	/**
 	 * Perform batch execution.
 	 * <p/>
 	 * This is called from the explicit {@link #execute() execution}, but may also be called from elsewhere
 	 * depending on the exact implementation.
 	 */
 	protected abstract void doExecuteBatch();
 
 	/**
 	 * Convenience access to the SQLException helper.
 	 *
 	 * @return The underlying SQLException helper.
 	 */
 	protected SqlExceptionHelper sqlExceptionHelper() {
 		return jdbcCoordinator.getTransactionCoordinator()
 				.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getSqlExceptionHelper();
 	}
 
 	/**
 	 * Convenience access to the SQL statement logger.
 	 *
 	 * @return The underlying JDBC services.
 	 */
 	protected SqlStatementLogger sqlStatementLogger() {
 		return jdbcCoordinator.getTransactionCoordinator()
 				.getTransactionContext()
 				.getTransactionEnvironment()
 				.getJdbcServices()
 				.getSqlStatementLogger();
 	}
 
 	/**
 	 * Access to the batch's map of statements (keyed by SQL statement string).
 	 *
 	 * @return This batch's statements.
 	 */
 	protected LinkedHashMap<String,PreparedStatement> getStatements() {
 		return statements;
 	}
 
 	@Override
 	public final BatchKey getKey() {
 		return key;
 	}
 
 	@Override
 	public void addObserver(BatchObserver observer) {
 		observers.add( observer );
 	}
 
 	@Override
 	public PreparedStatement getBatchStatement(String sql, boolean callable) {
 		if ( sql == null ) {
 			throw new IllegalArgumentException( "sql must be non-null." );
 		}
 		PreparedStatement statement = statements.get( sql );
 		if ( statement == null ) {
 			statement = buildBatchStatement( sql, callable );
 			statements.put( sql, statement );
 		}
 		else {
-			LOG.debugf( "Reusing batch statement" );
+			LOG.debug( "Reusing batch statement" );
 			sqlStatementLogger().logStatement( sql );
 		}
 		return statement;
 	}
 
 	private PreparedStatement buildBatchStatement(String sql, boolean callable) {
 		sql = jdbcCoordinator.getTransactionCoordinator().getTransactionContext().onPrepareStatement( sql );
 		try {
 			if ( callable ) {
 				return jdbcCoordinator.getLogicalConnection().getShareableConnectionProxy().prepareCall( sql );
 			}
 			else {
 				return jdbcCoordinator.getLogicalConnection().getShareableConnectionProxy().prepareStatement( sql );
 			}
 		}
 		catch ( SQLException sqle ) {
 			LOG.sqlExceptionEscapedProxy( sqle );
 			throw sqlExceptionHelper().convert( sqle, "could not prepare batch statement", sql );
 		}
 	}
 
 	@Override
 	public final void execute() {
 		notifyObserversExplicitExecution();
 		if ( statements.isEmpty() ) {
 			return;
 		}
 		try {
 			try {
 				doExecuteBatch();
 			}
 			finally {
 				releaseStatements();
 			}
 		}
 		finally {
 			statements.clear();
 		}
 	}
 
 	private void releaseStatements() {
 		for ( PreparedStatement statement : getStatements().values() ) {
 			try {
 				statement.close();
 			}
 			catch ( SQLException e ) {
 				LOG.unableToReleaseBatchStatement();
 				LOG.sqlExceptionEscapedProxy( e );
 			}
 		}
 		getStatements().clear();
 	}
 
 	/**
 	 * Convenience method to notify registered observers of an explicit execution of this batch.
 	 */
 	protected final void notifyObserversExplicitExecution() {
 		for ( BatchObserver observer : observers ) {
 			observer.batchExplicitlyExecuted();
 		}
 	}
 
 	/**
 	 * Convenience method to notify registered observers of an implicit execution of this batch.
 	 */
 	protected final void notifyObserversImplicitExecution() {
 		for ( BatchObserver observer : observers ) {
 			observer.batchImplicitlyExecuted();
 		}
 	}
 
 	@Override
 	public void release() {
         if (getStatements() != null && !getStatements().isEmpty()) LOG.batchContainedStatementsOnRelease();
 		releaseStatements();
 		observers.clear();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java
index 706f1c98b4..4af15f8562 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchingBatch.java
@@ -1,136 +1,136 @@
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
 package org.hibernate.engine.jdbc.batch.internal;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * A {@link org.hibernate.engine.jdbc.batch.spi.Batch} implementation which does bathing based on a given size.  Once
  * the batch size is reached for a statement in the batch, the entire batch is implicitly executed.
  *
  * @author Steve Ebersole
  */
 public class BatchingBatch extends AbstractBatchImpl {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, BatchingBatch.class.getName() );
 
 	// IMPL NOTE : Until HHH-5797 is fixed, there will only be 1 statement in a batch
 
 	private final int batchSize;
 	private int batchPosition;
 	private int statementPosition;
 
 	public BatchingBatch(
 			BatchKey key,
 			JdbcCoordinator jdbcCoordinator,
 			int batchSize) {
 		super( key, jdbcCoordinator );
 		if ( ! key.getExpectation().canBeBatched() ) {
 			throw new HibernateException( "attempting to batch an operation which cannot be batched" );
 		}
 		this.batchSize = batchSize;
 	}
 
 	private String currentStatementSql;
 	private PreparedStatement currentStatement;
 
 	@Override
 	public PreparedStatement getBatchStatement(String sql, boolean callable) {
 		currentStatementSql = sql;
 		currentStatement = super.getBatchStatement( sql, callable );
 		return currentStatement;
 	}
 
 	@Override
 	public void addToBatch() {
 		try {
 			currentStatement.addBatch();
 		}
 		catch ( SQLException e ) {
 			LOG.debugf( "SQLException escaped proxy", e );
 			throw sqlExceptionHelper().convert( e, "could not perform addBatch", currentStatementSql );
 		}
 		statementPosition++;
 		if ( statementPosition >= getKey().getBatchedStatementCount() ) {
 			batchPosition++;
 			if ( batchPosition == batchSize ) {
 				notifyObserversImplicitExecution();
 				performExecution();
 				batchPosition = 0;
 			}
 			statementPosition = 0;
 		}
 	}
 
 	@Override
 	protected void doExecuteBatch() {
 		if ( batchPosition == 0 ) {
-			LOG.debugf( "No batched statements to execute" );
+			LOG.debug( "No batched statements to execute" );
 		}
 		else {
 			LOG.debugf( "Executing batch size: %s", batchPosition );
 			performExecution();
 		}
 	}
 
 	private void performExecution() {
 		try {
 			for ( Map.Entry<String,PreparedStatement> entry : getStatements().entrySet() ) {
 				try {
 					final PreparedStatement statement = entry.getValue();
 					checkRowCounts( statement.executeBatch(), statement );
 				}
 				catch ( SQLException e ) {
-					LOG.debugf( "SQLException escaped proxy", e );
+					LOG.debug( "SQLException escaped proxy", e );
 					throw sqlExceptionHelper().convert( e, "could not perform addBatch", entry.getKey() );
 				}
 			}
 		}
 		catch ( RuntimeException re ) {
 			LOG.unableToExecuteBatch( re.getMessage() );
 			throw re;
 		}
 		finally {
 			batchPosition = 0;
 		}
 	}
 
 	private void checkRowCounts(int[] rowCounts, PreparedStatement ps) throws SQLException, HibernateException {
 		int numberOfRowCounts = rowCounts.length;
 		if ( numberOfRowCounts != batchPosition ) {
 			LOG.unexpectedRowCounts();
 		}
 		for ( int i = 0; i < numberOfRowCounts; i++ ) {
 			getKey().getExpectation().verifyOutcome( rowCounts[i], ps, i );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
index 35f2359086..8848fe1404 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
@@ -1,449 +1,449 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.jdbc.spi.NonDurableConnectionObserver;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.CollectionHelper;
 
 /**
  * Standard Hibernate {@link org.hibernate.engine.jdbc.spi.LogicalConnection} implementation
  * <p/>
  * IMPL NOTE : Custom serialization handling!
  *
  * @author Steve Ebersole
  */
 public class LogicalConnectionImpl implements LogicalConnectionImplementor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, LogicalConnectionImpl.class.getName() );
 
 	private transient Connection physicalConnection;
 	private transient Connection shareableConnectionProxy;
 
 	private final transient ConnectionReleaseMode connectionReleaseMode;
 	private final transient JdbcServices jdbcServices;
 	private final transient JdbcConnectionAccess jdbcConnectionAccess;
 	private final transient JdbcResourceRegistry jdbcResourceRegistry;
 	private final transient List<ConnectionObserver> observers;
 
 	private boolean releasesEnabled = true;
 
 	private final boolean isUserSuppliedConnection;
 
 	private boolean isClosed;
 
 	public LogicalConnectionImpl(
 			Connection userSuppliedConnection,
 			ConnectionReleaseMode connectionReleaseMode,
 			JdbcServices jdbcServices,
 			JdbcConnectionAccess jdbcConnectionAccess) {
 		this(
 				connectionReleaseMode,
 				jdbcServices,
 				jdbcConnectionAccess,
 				(userSuppliedConnection != null),
 				false,
 				new ArrayList<ConnectionObserver>()
 		);
 		this.physicalConnection = userSuppliedConnection;
 	}
 
 	private LogicalConnectionImpl(
 			ConnectionReleaseMode connectionReleaseMode,
 			JdbcServices jdbcServices,
 			JdbcConnectionAccess jdbcConnectionAccess,
 			boolean isUserSuppliedConnection,
 			boolean isClosed,
 			List<ConnectionObserver> observers) {
 		this.connectionReleaseMode = determineConnectionReleaseMode(
 				jdbcServices, isUserSuppliedConnection, connectionReleaseMode
 		);
 		this.jdbcServices = jdbcServices;
 		this.jdbcConnectionAccess = jdbcConnectionAccess;
 		this.jdbcResourceRegistry = new JdbcResourceRegistryImpl( getJdbcServices().getSqlExceptionHelper() );
 		this.observers = observers;
 
 		this.isUserSuppliedConnection = isUserSuppliedConnection;
 		this.isClosed = isClosed;
 	}
 
 	private static ConnectionReleaseMode determineConnectionReleaseMode(
 			JdbcServices jdbcServices,
 			boolean isUserSuppliedConnection,
 			ConnectionReleaseMode connectionReleaseMode) {
 		if ( isUserSuppliedConnection ) {
 			return ConnectionReleaseMode.ON_CLOSE;
 		}
 		else if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 				! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
-			LOG.debugf( "Connection provider reports to not support aggressive release; overriding" );
+			LOG.debug( "Connection provider reports to not support aggressive release; overriding" );
 			return ConnectionReleaseMode.AFTER_TRANSACTION;
 		}
 		else {
 			return connectionReleaseMode;
 		}
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	@Override
 	public JdbcResourceRegistry getResourceRegistry() {
 		return jdbcResourceRegistry;
 	}
 
 	@Override
 	public void addObserver(ConnectionObserver observer) {
 		observers.add( observer );
 	}
 
 	@Override
 	public void removeObserver(ConnectionObserver connectionObserver) {
 		observers.remove( connectionObserver );
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed;
 	}
 
 	@Override
 	public boolean isPhysicallyConnected() {
 		return physicalConnection != null;
 	}
 
 	@Override
 	public Connection getConnection() throws HibernateException {
 		if ( isClosed ) {
 			throw new HibernateException( "Logical connection is closed" );
 		}
 		if ( physicalConnection == null ) {
 			if ( isUserSuppliedConnection ) {
 				// should never happen
 				throw new HibernateException( "User-supplied connection was null" );
 			}
 			obtainConnection();
 		}
 		return physicalConnection;
 	}
 
 	@Override
 	public Connection getShareableConnectionProxy() {
 		if ( shareableConnectionProxy == null ) {
 			shareableConnectionProxy = buildConnectionProxy();
 		}
 		return shareableConnectionProxy;
 	}
 
 	private Connection buildConnectionProxy() {
 		return ProxyBuilder.buildConnection( this );
 	}
 
 	@Override
 	public Connection getDistinctConnectionProxy() {
 		return buildConnectionProxy();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection close() {
 		LOG.trace( "Closing logical connection" );
 		Connection c = isUserSuppliedConnection ? physicalConnection : null;
 		try {
 			releaseProxies();
 			jdbcResourceRegistry.close();
 			if ( !isUserSuppliedConnection && physicalConnection != null ) {
 				releaseConnection();
 			}
 			return c;
 		}
 		finally {
 			// no matter what
 			physicalConnection = null;
 			isClosed = true;
 			LOG.trace( "Logical connection closed" );
 			for ( ConnectionObserver observer : observers ) {
 				observer.logicalConnectionClosed();
 			}
 			observers.clear();
 		}
 	}
 
 	private void releaseProxies() {
 		if ( shareableConnectionProxy != null ) {
 			try {
 				shareableConnectionProxy.close();
 			}
 			catch (SQLException e) {
 				LOG.debug( "Error releasing shared connection proxy", e );
 			}
 		}
 		shareableConnectionProxy = null;
 	}
 
 	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public void afterStatementExecution() {
 		LOG.tracev( "Starting after statement execution processing [{0}]", connectionReleaseMode );
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			if ( ! releasesEnabled ) {
-				LOG.debugf( "Skipping aggressive release due to manual disabling" );
+				LOG.debug( "Skipping aggressive release due to manual disabling" );
 				return;
 			}
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
-				LOG.debugf( "Skipping aggressive release due to registered resources" );
+				LOG.debug( "Skipping aggressive release due to registered resources" );
 				return;
 			}
 			releaseConnection();
 		}
 	}
 
 	@Override
 	public void afterTransaction() {
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ||
 				connectionReleaseMode == ConnectionReleaseMode.AFTER_TRANSACTION ) {
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
 				LOG.forcingContainerResourceCleanup();
 				jdbcResourceRegistry.releaseResources();
 			}
 			aggressiveRelease();
 		}
 	}
 
 	@Override
 	public void disableReleases() {
 		LOG.trace( "Disabling releases" );
 		releasesEnabled = false;
 	}
 
 	@Override
 	public void enableReleases() {
 		LOG.trace( "(Re)enabling releases" );
 		releasesEnabled = true;
 		afterStatementExecution();
 	}
 
 	/**
 	 * Force aggressive release of the underlying connection.
 	 */
 	public void aggressiveRelease() {
 		if ( isUserSuppliedConnection ) {
-			LOG.debugf( "Cannot aggressively release user-supplied connection; skipping" );
+			LOG.debug( "Cannot aggressively release user-supplied connection; skipping" );
 		}
 		else {
-			LOG.debugf( "Aggressively releasing JDBC connection" );
+			LOG.debug( "Aggressively releasing JDBC connection" );
 			if ( physicalConnection != null ) {
 				releaseConnection();
 			}
 		}
 	}
 
 
 	/**
 	 * Physically opens a JDBC Connection.
 	 *
 	 * @throws org.hibernate.JDBCException Indicates problem opening a connection
 	 */
 	private void obtainConnection() throws JDBCException {
-		LOG.debugf( "Obtaining JDBC connection" );
+		LOG.debug( "Obtaining JDBC connection" );
 		try {
 			physicalConnection = jdbcConnectionAccess.obtainConnection();
 			for ( ConnectionObserver observer : observers ) {
 				observer.physicalConnectionObtained( physicalConnection );
 			}
-			LOG.debugf( "Obtained JDBC connection" );
+			LOG.debug( "Obtained JDBC connection" );
 		}
 		catch ( SQLException sqle) {
 			throw getJdbcServices().getSqlExceptionHelper().convert( sqle, "Could not open connection" );
 		}
 	}
 
 	/**
 	 * Physically closes the JDBC Connection.
 	 *
 	 * @throws JDBCException Indicates problem closing a connection
 	 */
 	private void releaseConnection() throws JDBCException {
-		LOG.debugf( "Releasing JDBC connection" );
+		LOG.debug( "Releasing JDBC connection" );
 		if ( physicalConnection == null ) {
 			return;
 		}
 		try {
 			if ( !physicalConnection.isClosed() ) {
 				getJdbcServices().getSqlExceptionHelper().logAndClearWarnings( physicalConnection );
 			}
 			if ( !isUserSuppliedConnection ) {
 				jdbcConnectionAccess.releaseConnection( physicalConnection );
 			}
 		}
 		catch (SQLException e) {
 			throw getJdbcServices().getSqlExceptionHelper().convert( e, "Could not close connection" );
 		}
 		finally {
 			physicalConnection = null;
 		}
-		LOG.debugf( "Released JDBC connection" );
+		LOG.debug( "Released JDBC connection" );
 		for ( ConnectionObserver observer : observers ) {
 			observer.physicalConnectionReleased();
 		}
 		releaseNonDurableObservers();
 	}
 
 	private void releaseNonDurableObservers() {
 		Iterator observers = this.observers.iterator();
 		while ( observers.hasNext() ) {
 			if ( NonDurableConnectionObserver.class.isInstance( observers.next() ) ) {
 				observers.remove();
 			}
 		}
 	}
 
 	@Override
 	public Connection manualDisconnect() {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually disconnect because logical connection is already closed" );
 		}
 		releaseProxies();
 		Connection c = physicalConnection;
 		jdbcResourceRegistry.releaseResources();
 		releaseConnection();
 		return c;
 	}
 
 	@Override
 	public void manualReconnect(Connection suppliedConnection) {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually reconnect because logical connection is already closed" );
 		}
 		if ( !isUserSuppliedConnection ) {
 			throw new IllegalStateException( "cannot manually reconnect unless Connection was originally supplied" );
 		}
 		else {
 			if ( suppliedConnection == null ) {
 				throw new IllegalArgumentException( "cannot reconnect a null user-supplied connection" );
 			}
 			else if ( suppliedConnection == physicalConnection ) {
 				LOG.debug( "reconnecting the same connection that is already connected; should this connection have been disconnected?" );
 			}
 			else if ( physicalConnection != null ) {
 				throw new IllegalArgumentException(
 						"cannot reconnect to a new user-supplied connection because currently connected; must disconnect before reconnecting."
 				);
 			}
 			physicalConnection = suppliedConnection;
-			LOG.debugf( "Reconnected JDBC connection" );
+			LOG.debug( "Reconnected JDBC connection" );
 		}
 	}
 
 	@Override
 	public boolean isAutoCommit() {
 		if ( !isOpen() || ! isPhysicallyConnected() ) {
 			return true;
 		}
 
 		try {
 			return getConnection().getAutoCommit();
 		}
 		catch (SQLException e) {
 			throw jdbcServices.getSqlExceptionHelper().convert( e, "could not inspect JDBC autocommit mode" );
 		}
 	}
 
 	@Override
 	public void notifyObserversStatementPrepared() {
 		for ( ConnectionObserver observer : observers ) {
 			observer.statementPrepared();
 		}
 	}
 
 	@Override
 	public boolean isReadyForSerialization() {
 		return isUserSuppliedConnection
 				? ! isPhysicallyConnected()
 				: ! getResourceRegistry().hasRegisteredResources();
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeBoolean( isUserSuppliedConnection );
 		oos.writeBoolean( isClosed );
 		List<ConnectionObserver> durableConnectionObservers = new ArrayList<ConnectionObserver>();
 		for ( ConnectionObserver observer : observers ) {
 			if ( ! NonDurableConnectionObserver.class.isInstance( observer ) ) {
 				durableConnectionObservers.add( observer );
 			}
 		}
 		oos.writeInt( durableConnectionObservers.size() );
 		for ( ConnectionObserver observer : durableConnectionObservers ) {
 			oos.writeObject( observer );
 		}
 	}
 
 	public static LogicalConnectionImpl deserialize(
 			ObjectInputStream ois,
 			TransactionContext transactionContext) throws IOException, ClassNotFoundException {
 		boolean isUserSuppliedConnection = ois.readBoolean();
 		boolean isClosed = ois.readBoolean();
 		int observerCount = ois.readInt();
 		List<ConnectionObserver> observers = CollectionHelper.arrayList( observerCount );
 		for ( int i = 0; i < observerCount; i++ ) {
 			observers.add( (ConnectionObserver) ois.readObject() );
 		}
 		return new LogicalConnectionImpl(
 				transactionContext.getConnectionReleaseMode(),
 				transactionContext.getTransactionEnvironment().getJdbcServices(),
 				transactionContext.getJdbcConnectionAccess(),
 				isUserSuppliedConnection,
 				isClosed,
 				observers
 		);
  	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
index 61e89c67cb..72a5e68898 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
@@ -1,359 +1,359 @@
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
 package org.hibernate.engine.loading.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Represents state associated with the processing of a given {@link ResultSet}
  * in regards to loading collections.
  * <p/>
  * Another implementation option to consider is to not expose {@link ResultSet}s
  * directly (in the JDBC redesign) but to always "wrap" them and apply a
  * [series of] context[s] to that wrapper.
  *
  * @author Steve Ebersole
  */
 public class CollectionLoadContext {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionLoadContext.class.getName());
 
 	private final LoadContexts loadContexts;
 	private final ResultSet resultSet;
 	private Set localLoadingCollectionKeys = new HashSet();
 
 	/**
 	 * Creates a collection load context for the given result set.
 	 *
 	 * @param loadContexts Callback to other collection load contexts.
 	 * @param resultSet The result set this is "wrapping".
 	 */
 	public CollectionLoadContext(LoadContexts loadContexts, ResultSet resultSet) {
 		this.loadContexts = loadContexts;
 		this.resultSet = resultSet;
 	}
 
 	public ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	public LoadContexts getLoadContext() {
 		return loadContexts;
 	}
 
 	/**
 	 * Retrieve the collection that is being loaded as part of processing this
 	 * result set.
 	 * <p/>
 	 * Basically, there are two valid return values from this method:<ul>
 	 * <li>an instance of {@link org.hibernate.collection.spi.PersistentCollection} which indicates to
 	 * continue loading the result set row data into that returned collection
 	 * instance; this may be either an instance already associated and in the
 	 * midst of being loaded, or a newly instantiated instance as a matching
 	 * associated collection was not found.</li>
 	 * <li><i>null</i> indicates to ignore the corresponding result set row
 	 * data relating to the requested collection; this indicates that either
 	 * the collection was found to already be associated with the persistence
 	 * context in a fully loaded state, or it was found in a loading state
 	 * associated with another result set processing context.</li>
 	 * </ul>
 	 *
 	 * @param persister The persister for the collection being requested.
 	 * @param key The key of the collection being requested.
 	 *
 	 * @return The loading collection (see discussion above).
 	 */
 	public PersistentCollection getLoadingCollection(final CollectionPersister persister, final Serializable key) {
 		final EntityMode em = persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode();
 		final CollectionKey collectionKey = new CollectionKey( persister, key, em );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Starting attempt to find loading collection [{0}]",
 					MessageHelper.collectionInfoString( persister.getRole(), key ) );
 		}
 		final LoadingCollectionEntry loadingCollectionEntry = loadContexts.locateLoadingCollectionEntry( collectionKey );
 		if ( loadingCollectionEntry == null ) {
 			// look for existing collection as part of the persistence context
 			PersistentCollection collection = loadContexts.getPersistenceContext().getCollection( collectionKey );
 			if ( collection != null ) {
 				if ( collection.wasInitialized() ) {
 					LOG.trace( "Collection already initialized; ignoring" );
 					return null; // ignore this row of results! Note the early exit
 				}
 				LOG.trace( "Collection not yet initialized; initializing" );
 			}
 			else {
 				Object owner = loadContexts.getPersistenceContext().getCollectionOwner( key, persister );
 				final boolean newlySavedEntity = owner != null
 						&& loadContexts.getPersistenceContext().getEntry( owner ).getStatus() != Status.LOADING;
 				if ( newlySavedEntity ) {
 					// important, to account for newly saved entities in query
 					// todo : some kind of check for new status...
 					LOG.trace( "Owning entity already loaded; ignoring" );
 					return null;
 				}
 				// create one
 				LOG.tracev( "Instantiating new collection [key={0}, rs={1}]", key, resultSet );
 				collection = persister.getCollectionType().instantiate(
 						loadContexts.getPersistenceContext().getSession(), persister, key );
 			}
 			collection.beforeInitialize( persister, -1 );
 			collection.beginRead();
 			localLoadingCollectionKeys.add( collectionKey );
 			loadContexts.registerLoadingCollectionXRef( collectionKey, new LoadingCollectionEntry( resultSet, persister, key, collection ) );
 			return collection;
 		}
 		if ( loadingCollectionEntry.getResultSet() == resultSet ) {
 			LOG.trace( "Found loading collection bound to current result set processing; reading row" );
 			return loadingCollectionEntry.getCollection();
 		}
 		// ignore this row, the collection is in process of
 		// being loaded somewhere further "up" the stack
 		LOG.trace( "Collection is already being initialized; ignoring row" );
 		return null;
 	}
 
 	/**
 	 * Finish the process of collection-loading for this bound result set.  Mainly this
 	 * involves cleaning up resources and notifying the collections that loading is
 	 * complete.
 	 *
 	 * @param persister The persister for which to complete loading.
 	 */
 	public void endLoadingCollections(CollectionPersister persister) {
 		SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		if ( !loadContexts.hasLoadingCollectionEntries()
 				&& localLoadingCollectionKeys.isEmpty() ) {
 			return;
 		}
 
 		// in an effort to avoid concurrent-modification-exceptions (from
 		// potential recursive calls back through here as a result of the
 		// eventual call to PersistentCollection#endRead), we scan the
 		// internal loadingCollections map for matches and store those matches
 		// in a temp collection.  the temp collection is then used to "drive"
 		// the #endRead processing.
 		List matches = null;
 		Iterator iter = localLoadingCollectionKeys.iterator();
 		while ( iter.hasNext() ) {
 			final CollectionKey collectionKey = (CollectionKey) iter.next();
 			final LoadingCollectionEntry lce = loadContexts.locateLoadingCollectionEntry( collectionKey );
 			if ( lce == null ) {
 				LOG.loadingCollectionKeyNotFound( collectionKey );
 			}
 			else if ( lce.getResultSet() == resultSet && lce.getPersister() == persister ) {
 				if ( matches == null ) {
 					matches = new ArrayList();
 				}
 				matches.add( lce );
 				if ( lce.getCollection().getOwner() == null ) {
 					session.getPersistenceContext().addUnownedCollection(
 							new CollectionKey(
 									persister,
 									lce.getKey(),
 									persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode()
 							),
 							lce.getCollection()
 					);
 				}
 				LOG.tracev( "Removing collection load entry [{0}]", lce );
 
 				// todo : i'd much rather have this done from #endLoadingCollection(CollectionPersister,LoadingCollectionEntry)...
 				loadContexts.unregisterLoadingCollectionXRef( collectionKey );
 				iter.remove();
 			}
 		}
 
 		endLoadingCollections( persister, matches );
 		if ( localLoadingCollectionKeys.isEmpty() ) {
 			// todo : hack!!!
 			// NOTE : here we cleanup the load context when we have no more local
 			// LCE entries.  This "works" for the time being because really
 			// only the collection load contexts are implemented.  Long term,
 			// this cleanup should become part of the "close result set"
 			// processing from the (sandbox/jdbc) jdbc-container code.
 			loadContexts.cleanup( resultSet );
 		}
 	}
 
 	private void endLoadingCollections(CollectionPersister persister, List matchedCollectionEntries) {
 		if ( matchedCollectionEntries == null ) {
 			if ( LOG.isDebugEnabled()) LOG.debugf( "No collections were found in result set for role: %s", persister.getRole() );
 			return;
 		}
 
 		final int count = matchedCollectionEntries.size();
 		if ( LOG.isDebugEnabled()) LOG.debugf("%s collections were found in result set for role: %s", count, persister.getRole());
 
 		for ( int i = 0; i < count; i++ ) {
 			LoadingCollectionEntry lce = ( LoadingCollectionEntry ) matchedCollectionEntries.get( i );
 			endLoadingCollection( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "%s collections initialized for role: %s", count, persister.getRole() );
 	}
 
 	private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
 		LOG.tracev( "Ending loading collection [{0}]", lce );
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 
 		boolean hasNoQueuedAdds = lce.getCollection().endRead(); // warning: can cause a recursive calls! (proxy initialization)
 
 		if ( persister.getCollectionType().hasHolder() ) {
 			getLoadContext().getPersistenceContext().addCollectionHolder( lce.getCollection() );
 		}
 
 		CollectionEntry ce = getLoadContext().getPersistenceContext().getCollectionEntry( lce.getCollection() );
 		if ( ce == null ) {
 			ce = getLoadContext().getPersistenceContext().addInitializedCollection( persister, lce.getCollection(), lce.getKey() );
 		}
 		else {
 			ce.postInitialize( lce.getCollection() );
 		}
 
 		boolean addToCache = hasNoQueuedAdds && // there were no queued additions
 				persister.hasCache() &&             // and the role has a cache
 				session.getCacheMode().isPutEnabled() &&
 				!ce.isDoremove();                   // and this is not a forced initialization during flush
 		if ( addToCache ) {
 			addCollectionToCache( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Collection fully initialized: %s",
 					MessageHelper.collectionInfoString(persister, lce.getKey(), session.getFactory())
 			);
 		}
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 			session.getFactory().getStatisticsImplementor().loadCollection(persister.getRole());
 		}
 	}
 
 	/**
 	 * Add the collection to the second-level cache
 	 *
 	 * @param lce The entry representing the collection to add
 	 * @param persister The persister
 	 */
 	private void addCollectionToCache(LoadingCollectionEntry lce, CollectionPersister persister) {
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Caching collection: %s", MessageHelper.collectionInfoString( persister, lce.getKey(), factory ) );
 		}
 
 		if ( !session.getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
 			// some filters affecting the collection are enabled on the session, so do not do the put into the cache.
-			LOG.debugf( "Refusing to add to cache due to enabled filters" );
+			LOG.debug( "Refusing to add to cache due to enabled filters" );
 			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
 			//      but CacheKey is currently used for both collections and entities; would ideally need to define two seperate ones;
 			//      currently this works in conjuction with the check on
 			//      DefaultInitializeCollectionEventHandler.initializeCollectionFromCache() (which makes sure to not read from
 			//      cache with enabled filters).
 			return; // EARLY EXIT!!!!!
 		}
 
 		final Object version;
 		if ( persister.isVersioned() ) {
 			Object collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( lce.getKey(), persister );
 			if ( collectionOwner == null ) {
 				// generally speaking this would be caused by the collection key being defined by a property-ref, thus
 				// the collection key and the owner key would not match up.  In this case, try to use the key of the
 				// owner instance associated with the collection itself, if one.  If the collection does already know
 				// about its owner, that owner should be the same instance as associated with the PC, but we do the
 				// resolution against the PC anyway just to be safe since the lookup should not be costly.
 				if ( lce.getCollection() != null ) {
 					Object linkedOwner = lce.getCollection().getOwner();
 					if ( linkedOwner != null ) {
 						final Serializable ownerKey = persister.getOwnerEntityPersister().getIdentifier( linkedOwner, session );
 						collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( ownerKey, persister );
 					}
 				}
 				if ( collectionOwner == null ) {
 					throw new HibernateException(
 							"Unable to resolve owner of loading collection [" +
 									MessageHelper.collectionInfoString( persister, lce.getKey(), factory ) +
 									"] for second level caching"
 					);
 				}
 			}
 			version = getLoadContext().getPersistenceContext().getEntry( collectionOwner ).getVersion();
 		}
 		else {
 			version = null;
 		}
 
 		CollectionCacheEntry entry = new CollectionCacheEntry( lce.getCollection(), persister );
 		CacheKey cacheKey = session.generateCacheKey( lce.getKey(), persister.getKeyType(), persister.getRole() );
 		boolean put = persister.getCacheAccessStrategy().putFromLoad(
 				cacheKey,
 				persister.getCacheEntryStructure().structure(entry),
 				session.getTimestamp(),
 				version,
 				factory.getSettings().isMinimalPutsEnabled() && session.getCacheMode()!= CacheMode.REFRESH
 		);
 
 		if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 		}
 	}
 
 	void cleanup() {
 		if ( !localLoadingCollectionKeys.isEmpty() ) {
 			LOG.localLoadingCollectionKeysCount( localLoadingCollectionKeys.size() );
 		}
 		loadContexts.cleanupCollectionXRefs( localLoadingCollectionKeys );
 		localLoadingCollectionKeys.clear();
 	}
 
 
 	@Override
     public String toString() {
 		return super.toString() + "<rs=" + resultSet + ">";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java
index 5959b7683f..19f33d9bf4 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java
@@ -1,741 +1,741 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.action.internal.CollectionAction;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
 import org.hibernate.action.internal.EntityAction;
 import org.hibernate.action.internal.EntityDeleteAction;
 import org.hibernate.action.internal.EntityIdentityInsertAction;
 import org.hibernate.action.internal.EntityInsertAction;
 import org.hibernate.action.internal.EntityUpdateAction;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.CacheException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for maintaining the queue of actions related to events.
  * </p>
  * The ActionQueue holds the DML operations queued as part of a session's
  * transactional-write-behind semantics.  DML operations are queued here
  * until a flush forces them to be executed against the database.
  *
  * @author Steve Ebersole
  */
 public class ActionQueue {
 
 	static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ActionQueue.class.getName());
 	private static final int INIT_QUEUE_LIST_SIZE = 5;
 
 	private SessionImplementor session;
 
 	// Object insertions, updates, and deletions have list semantics because
 	// they must happen in the right order so as to respect referential
 	// integrity
 	private ArrayList insertions;
 	private ArrayList deletions;
 	private ArrayList updates;
 	// Actually the semantics of the next three are really "Bag"
 	// Note that, unlike objects, collection insertions, updates,
 	// deletions are not really remembered between flushes. We
 	// just re-use the same Lists for convenience.
 	private ArrayList collectionCreations;
 	private ArrayList collectionUpdates;
 	private ArrayList collectionRemovals;
 
 	private AfterTransactionCompletionProcessQueue afterTransactionProcesses;
 	private BeforeTransactionCompletionProcessQueue beforeTransactionProcesses;
 
 	/**
 	 * Constructs an action queue bound to the given session.
 	 *
 	 * @param session The session "owning" this queue.
 	 */
 	public ActionQueue(SessionImplementor session) {
 		this.session = session;
 		init();
 	}
 
 	private void init() {
 		insertions = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		deletions = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		updates = new ArrayList( INIT_QUEUE_LIST_SIZE );
 
 		collectionCreations = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		collectionRemovals = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		collectionUpdates = new ArrayList( INIT_QUEUE_LIST_SIZE );
 
 		afterTransactionProcesses = new AfterTransactionCompletionProcessQueue( session );
 		beforeTransactionProcesses = new BeforeTransactionCompletionProcessQueue( session );
 	}
 
 	public void clear() {
 		updates.clear();
 		insertions.clear();
 		deletions.clear();
 
 		collectionCreations.clear();
 		collectionRemovals.clear();
 		collectionUpdates.clear();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityInsertAction action) {
 		insertions.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityDeleteAction action) {
 		deletions.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityUpdateAction action) {
 		updates.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionRecreateAction action) {
 		collectionCreations.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionRemoveAction action) {
 		collectionRemovals.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionUpdateAction action) {
 		collectionUpdates.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityIdentityInsertAction insert) {
 		insertions.add( insert );
 	}
 
 	public void addAction(BulkOperationCleanupAction cleanupAction) {
 		registerCleanupActions( cleanupAction );
 	}
 
 	public void registerProcess(AfterTransactionCompletionProcess process) {
 		afterTransactionProcesses.register( process );
 	}
 
 	public void registerProcess(BeforeTransactionCompletionProcess process) {
 		beforeTransactionProcesses.register( process );
 	}
 
 	/**
 	 * Perform all currently queued entity-insertion actions.
 	 *
 	 * @throws HibernateException error executing queued insertion actions.
 	 */
 	public void executeInserts() throws HibernateException {
 		executeActions( insertions );
 	}
 
 	/**
 	 * Perform all currently queued actions.
 	 *
 	 * @throws HibernateException error executing queued actions.
 	 */
 	public void executeActions() throws HibernateException {
 		executeActions( insertions );
 		executeActions( updates );
 		executeActions( collectionRemovals );
 		executeActions( collectionUpdates );
 		executeActions( collectionCreations );
 		executeActions( deletions );
 	}
 
 	/**
 	 * Prepares the internal action queues for execution.
 	 *
 	 * @throws HibernateException error preparing actions.
 	 */
 	public void prepareActions() throws HibernateException {
 		prepareActions( collectionRemovals );
 		prepareActions( collectionUpdates );
 		prepareActions( collectionCreations );
 	}
 
 	/**
 	 * Performs cleanup of any held cache softlocks.
 	 *
 	 * @param success Was the transaction successful.
 	 */
 	public void afterTransactionCompletion(boolean success) {
 		afterTransactionProcesses.afterTransactionCompletion( success );
 	}
 
 	/**
 	 * Execute any registered {@link org.hibernate.action.spi.BeforeTransactionCompletionProcess}
 	 */
 	public void beforeTransactionCompletion() {
 		beforeTransactionProcesses.beforeTransactionCompletion();
 	}
 
 	/**
 	 * Check whether the given tables/query-spaces are to be executed against
 	 * given the currently queued actions.
 	 *
 	 * @param tables The table/query-spaces to check.
 	 *
 	 * @return True if we contain pending actions against any of the given
 	 *         tables; false otherwise.
 	 */
 	public boolean areTablesToBeUpdated(Set tables) {
 		return areTablesToUpdated( updates, tables ) ||
 				areTablesToUpdated( insertions, tables ) ||
 				areTablesToUpdated( deletions, tables ) ||
 				areTablesToUpdated( collectionUpdates, tables ) ||
 				areTablesToUpdated( collectionCreations, tables ) ||
 				areTablesToUpdated( collectionRemovals, tables );
 	}
 
 	/**
 	 * Check whether any insertion or deletion actions are currently queued.
 	 *
 	 * @return True if insertions or deletions are currently queued; false otherwise.
 	 */
 	public boolean areInsertionsOrDeletionsQueued() {
 		return ( insertions.size() > 0 || deletions.size() > 0 );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private static boolean areTablesToUpdated(List actions, Set tableSpaces) {
 		for ( Executable action : (List<Executable>) actions ) {
 			final Serializable[] spaces = action.getPropertySpaces();
 			for ( Serializable space : spaces ) {
 				if ( tableSpaces.contains( space ) ) {
 					LOG.debugf( "Changes must be flushed to space: %s", space );
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void executeActions(List list) throws HibernateException {
 		int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			execute( (Executable) list.get( i ) );
 		}
 		list.clear();
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 	}
 
 	public void execute(Executable executable) {
 		try {
 			executable.execute();
 		}
 		finally {
 			registerCleanupActions( executable );
 		}
 	}
 
 	private void registerCleanupActions(Executable executable) {
 		beforeTransactionProcesses.register( executable.getBeforeTransactionCompletionProcess() );
 		if ( session.getFactory().getSettings().isQueryCacheEnabled() ) {
 			final String[] spaces = (String[]) executable.getPropertySpaces();
 			if ( spaces != null && spaces.length > 0 ) { //HHH-6286
 				afterTransactionProcesses.addSpacesToInvalidate( spaces );
 				session.getFactory().getUpdateTimestampsCache().preinvalidate( spaces );
 			}
 		}
 		afterTransactionProcesses.register( executable.getAfterTransactionCompletionProcess() );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void prepareActions(List queue) throws HibernateException {
 		for ( Executable executable : (List<Executable>) queue ) {
 			executable.beforeExecutions();
 		}
 	}
 
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	@Override
     public String toString() {
 		return new StringBuilder()
 				.append( "ActionQueue[insertions=" ).append( insertions )
 				.append( " updates=" ).append( updates )
 				.append( " deletions=" ).append( deletions )
 				.append( " collectionCreations=" ).append( collectionCreations )
 				.append( " collectionRemovals=" ).append( collectionRemovals )
 				.append( " collectionUpdates=" ).append( collectionUpdates )
 				.append( "]" )
 				.toString();
 	}
 
 	public int numberOfCollectionRemovals() {
 		return collectionRemovals.size();
 	}
 
 	public int numberOfCollectionUpdates() {
 		return collectionUpdates.size();
 	}
 
 	public int numberOfCollectionCreations() {
 		return collectionCreations.size();
 	}
 
 	public int numberOfDeletions() {
 		return deletions.size();
 	}
 
 	public int numberOfUpdates() {
 		return updates.size();
 	}
 
 	public int numberOfInsertions() {
 		return insertions.size();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void sortCollectionActions() {
 		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
 			//sort the updates by fk
 			java.util.Collections.sort( collectionCreations );
 			java.util.Collections.sort( collectionUpdates );
 			java.util.Collections.sort( collectionRemovals );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void sortActions() {
 		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
 			//sort the updates by pk
 			java.util.Collections.sort( updates );
 		}
 		if ( session.getFactory().getSettings().isOrderInsertsEnabled() ) {
 			sortInsertActions();
 		}
 	}
 
 	/**
 	 * Order the {@link #insertions} queue such that we group inserts
 	 * against the same entity together (without violating constraints).  The
 	 * original order is generated by cascade order, which in turn is based on
 	 * the directionality of foreign-keys.  So even though we will be changing
 	 * the ordering here, we need to make absolutely certain that we do not
 	 * circumvent this FK ordering to the extent of causing constraint
 	 * violations
 	 */
 	private void sortInsertActions() {
 		new InsertActionSorter().sort();
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public ArrayList cloneDeletions() {
 		return ( ArrayList ) deletions.clone();
 	}
 
 	public void clearFromFlushNeededCheck(int previousCollectionRemovalSize) {
 		collectionCreations.clear();
 		collectionUpdates.clear();
 		updates.clear();
 		// collection deletions are a special case since update() can add
 		// deletions of collections not loaded by the session.
 		for ( int i = collectionRemovals.size() - 1; i >= previousCollectionRemovalSize; i-- ) {
 			collectionRemovals.remove( i );
 		}
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public boolean hasAfterTransactionActions() {
 		return afterTransactionProcesses.processes.size() > 0;
 	}
 
 	public boolean hasBeforeTransactionActions() {
 		return beforeTransactionProcesses.processes.size() > 0;
 	}
 
 	public boolean hasAnyQueuedActions() {
 		return updates.size() > 0 ||
 				insertions.size() > 0 ||
 				deletions.size() > 0 ||
 				collectionUpdates.size() > 0 ||
 				collectionRemovals.size() > 0 ||
 				collectionCreations.size() > 0;
 	}
 
 	/**
 	 * Used by the owning session to explicitly control serialization of the
 	 * action queue
 	 *
 	 * @param oos The stream to which the action queue should get written
 	 *
 	 * @throws IOException Indicates an error writing to the stream
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		LOG.trace( "Serializing action-queue" );
 
 		int queueSize = insertions.size();
 		LOG.tracev( "Starting serialization of [{0}] insertions entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( insertions.get( i ) );
 		}
 
 		queueSize = deletions.size();
 		LOG.tracev( "Starting serialization of [{0}] deletions entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( deletions.get( i ) );
 		}
 
 		queueSize = updates.size();
 		LOG.tracev( "Starting serialization of [{0}] updates entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( updates.get( i ) );
 		}
 
 		queueSize = collectionUpdates.size();
 		LOG.tracev( "Starting serialization of [{0}] collectionUpdates entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionUpdates.get( i ) );
 		}
 
 		queueSize = collectionRemovals.size();
 		LOG.tracev( "Starting serialization of [{0}] collectionRemovals entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionRemovals.get( i ) );
 		}
 
 		queueSize = collectionCreations.size();
 		LOG.tracev( "Starting serialization of [{0}] collectionCreations entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionCreations.get( i ) );
 		}
 	}
 
 	/**
 	 * Used by the owning session to explicitly control deserialization of the
 	 * action queue
 	 *
 	 * @param ois The stream from which to read the action queue
 	 * @param session The session to which the action queue belongs
 	 *
 	 * @return The deserialized action queue
 	 *
 	 * @throws IOException indicates a problem reading from the stream
 	 * @throws ClassNotFoundException Generally means we were unable to locate user classes.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public static ActionQueue deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
-		LOG.tracev( "Dedeserializing action-queue" );
+		LOG.trace( "Dedeserializing action-queue" );
 		ActionQueue rtn = new ActionQueue( session );
 
 		int queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] insertions entries", queueSize );
 		rtn.insertions = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			EntityAction action = ( EntityAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.insertions.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] deletions entries", queueSize );
 		rtn.deletions = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			EntityAction action = ( EntityAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.deletions.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] updates entries", queueSize );
 		rtn.updates = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			EntityAction action = ( EntityAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.updates.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] collectionUpdates entries", queueSize );
 		rtn.collectionUpdates = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			CollectionAction action = (CollectionAction) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.collectionUpdates.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] collectionRemovals entries", queueSize );
 		rtn.collectionRemovals = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			CollectionAction action = ( CollectionAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.collectionRemovals.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] collectionCreations entries", queueSize );
 		rtn.collectionCreations = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			CollectionAction action = ( CollectionAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.collectionCreations.add( action );
 		}
 		return rtn;
 	}
 
 	private static class BeforeTransactionCompletionProcessQueue {
 		private SessionImplementor session;
 		private List<BeforeTransactionCompletionProcess> processes = new ArrayList<BeforeTransactionCompletionProcess>();
 
 		private BeforeTransactionCompletionProcessQueue(SessionImplementor session) {
 			this.session = session;
 		}
 
 		public void register(BeforeTransactionCompletionProcess process) {
 			if ( process == null ) {
 				return;
 			}
 			processes.add( process );
 		}
 
 		public void beforeTransactionCompletion() {
 			final int size = processes.size();
 			for ( int i = 0; i < size; i++ ) {
 				try {
 					BeforeTransactionCompletionProcess process = processes.get( i );
 					process.doBeforeTransactionCompletion( session );
 				}
 				catch ( HibernateException he ) {
 					throw he;
 				}
 				catch ( Exception e ) {
 					throw new AssertionFailure( "Unable to perform beforeTransactionCompletion callback", e );
 				}
 			}
 			processes.clear();
 		}
 	}
 
 	private static class AfterTransactionCompletionProcessQueue {
 		private SessionImplementor session;
 		private Set<String> querySpacesToInvalidate = new HashSet<String>();
 		private List<AfterTransactionCompletionProcess> processes
 				= new ArrayList<AfterTransactionCompletionProcess>( INIT_QUEUE_LIST_SIZE * 3 );
 
 		private AfterTransactionCompletionProcessQueue(SessionImplementor session) {
 			this.session = session;
 		}
 
 		public void addSpacesToInvalidate(String[] spaces) {
 			for ( String space : spaces ) {
 				addSpaceToInvalidate( space );
 			}
 		}
 
 		public void addSpaceToInvalidate(String space) {
 			querySpacesToInvalidate.add( space );
 		}
 
 		public void register(AfterTransactionCompletionProcess process) {
 			if ( process == null ) {
 				return;
 			}
 			processes.add( process );
 		}
 
 		public void afterTransactionCompletion(boolean success) {
 			for ( AfterTransactionCompletionProcess process : processes ) {
 				try {
 					process.doAfterTransactionCompletion( success, session );
 				}
 				catch ( CacheException ce ) {
 					LOG.unableToReleaseCacheLock( ce );
 					// continue loop
 				}
 				catch ( Exception e ) {
 					throw new AssertionFailure( "Exception releasing cache locks", e );
 				}
 			}
 			processes.clear();
 
 			if ( session.getFactory().getSettings().isQueryCacheEnabled() ) {
 				session.getFactory().getUpdateTimestampsCache().invalidate(
 						querySpacesToInvalidate.toArray( new String[ querySpacesToInvalidate.size()] )
 				);
 			}
 			querySpacesToInvalidate.clear();
 		}
 	}
 
 	/**
 	 * Sorts the insert actions using more hashes.
 	 *
 	 * @author Jay Erb
 	 */
 	private class InsertActionSorter {
 		// the mapping of entity names to their latest batch numbers.
 		private HashMap<String,Integer> latestBatches = new HashMap<String,Integer>();
 		private HashMap<Object,Integer> entityBatchNumber;
 
 		// the map of batch numbers to EntityInsertAction lists
 		private HashMap<Integer,List<EntityInsertAction>> actionBatches = new HashMap<Integer,List<EntityInsertAction>>();
 
 		public InsertActionSorter() {
 			//optimize the hash size to eliminate a rehash.
 			entityBatchNumber = new HashMap<Object,Integer>( insertions.size() + 1, 1.0f );
 		}
 
 		/**
 		 * Sort the insert actions.
 		 */
 		@SuppressWarnings({ "unchecked", "UnnecessaryBoxing" })
 		public void sort() {
 			// the list of entity names that indicate the batch number
 			for ( EntityInsertAction action : (List<EntityInsertAction>) insertions ) {
 				// remove the current element from insertions. It will be added back later.
 				String entityName = action.getEntityName();
 
 				// the entity associated with the current action.
 				Object currentEntity = action.getInstance();
 
 				Integer batchNumber;
 				if ( latestBatches.containsKey( entityName ) ) {
 					// There is already an existing batch for this type of entity.
 					// Check to see if the latest batch is acceptable.
 					batchNumber = findBatchNumber( action, entityName );
 				}
 				else {
 					// add an entry for this type of entity.
 					// we can be assured that all referenced entities have already
 					// been processed,
 					// so specify that this entity is with the latest batch.
 					// doing the batch number before adding the name to the list is
 					// a faster way to get an accurate number.
 
 					batchNumber = actionBatches.size();
 					latestBatches.put( entityName, batchNumber );
 				}
 				entityBatchNumber.put( currentEntity, batchNumber );
 				addToBatch( batchNumber, action );
 			}
 			insertions.clear();
 
 			// now rebuild the insertions list. There is a batch for each entry in the name list.
 			for ( int i = 0; i < actionBatches.size(); i++ ) {
 				List<EntityInsertAction> batch = actionBatches.get( i );
 				for ( EntityInsertAction action : batch ) {
 					insertions.add( action );
 				}
 			}
 		}
 
 		/**
 		 * Finds an acceptable batch for this entity to be a member as part of the {@link InsertActionSorter}
 		 *
 		 * @param action The action being sorted
 		 * @param entityName The name of the entity affected by the action
 		 *
 		 * @return An appropriate batch number; todo document this process better
 		 */
 		@SuppressWarnings({ "UnnecessaryBoxing", "unchecked" })
 		private Integer findBatchNumber(
 				EntityInsertAction action,
 				String entityName) {
 			// loop through all the associated entities and make sure they have been
 			// processed before the latest
 			// batch associated with this entity type.
 
 			// the current batch number is the latest batch for this entity type.
 			Integer latestBatchNumberForType = latestBatches.get( entityName );
 
 			// loop through all the associations of the current entity and make sure that they are processed
 			// before the current batch number
 			Object[] propertyValues = action.getState();
 			Type[] propertyTypes = action.getPersister().getClassMetadata()
 					.getPropertyTypes();
 
 			for ( int i = 0; i < propertyValues.length; i++ ) {
 				Object value = propertyValues[i];
 				Type type = propertyTypes[i];
 				if ( type.isEntityType() && value != null ) {
 					// find the batch number associated with the current association, if any.
 					Integer associationBatchNumber = entityBatchNumber.get( value );
 					if ( associationBatchNumber != null && associationBatchNumber.compareTo( latestBatchNumberForType ) > 0 ) {
 						// create a new batch for this type. The batch number is the number of current batches.
 						latestBatchNumberForType = actionBatches.size();
 						latestBatches.put( entityName, latestBatchNumberForType );
 						// since this entity will now be processed in the latest possible batch,
 						// we can be assured that it will come after all other associations,
 						// there's not need to continue checking.
 						break;
 					}
 				}
 			}
 			return latestBatchNumberForType;
 		}
 
 		@SuppressWarnings({ "unchecked" })
 		private void addToBatch(Integer batchNumber, EntityInsertAction action) {
 			List<EntityInsertAction> actions = actionBatches.get( batchNumber );
 
 			if ( actions == null ) {
 				actions = new LinkedList<EntityInsertAction>();
 				actionBatches.put( batchNumber, actions );
 			}
 			actions.add( action );
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
index d72179c088..db3a4a0f46 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
@@ -1,388 +1,388 @@
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
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.Collections;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.EntityPrinter;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.internal.util.collections.LazyIterator;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A convenience base class for listeners whose functionality results in flushing.
  *
- * @author Steve Eberole
+ * @author Steve Ebersole
  */
 public abstract class AbstractFlushingEventListener implements Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AbstractFlushingEventListener.class.getName() );
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Pre-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Coordinates the processing necessary to get things ready for executions
 	 * as db calls by preping the session caches and moving the appropriate
 	 * entities and collections to their respective execution queues.
 	 *
 	 * @param event The flush event.
 	 * @throws HibernateException Error flushing caches to execution queues.
 	 */
 	protected void flushEverythingToExecutions(FlushEvent event) throws HibernateException {
 
 		LOG.trace( "Flushing session" );
 
 		EventSource session = event.getSession();
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		session.getInterceptor().preFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 		prepareEntityFlushes(session);
 		// we could move this inside if we wanted to
 		// tolerate collection initializations during
 		// collection dirty checking:
 		prepareCollectionFlushes(session);
 		// now, any collections that are initialized
 		// inside this block do not get updated - they
 		// are ignored until the next flush
 
 		persistenceContext.setFlushing(true);
 		try {
 			flushEntities(event);
 			flushCollections(session);
 		}
 		finally {
 			persistenceContext.setFlushing(false);
 		}
 
 		//some statistics
 		logFlushResults( event );
 	}
 
 	@SuppressWarnings( value = {"unchecked"} )
 	private void logFlushResults(FlushEvent event) {
 		if ( !LOG.isDebugEnabled() ) {
 			return;
 		}
 		final EventSource session = event.getSession();
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		LOG.debugf(
 				"Flushed: %s insertions, %s updates, %s deletions to %s objects",
 				session.getActionQueue().numberOfInsertions(),
 				session.getActionQueue().numberOfUpdates(),
 				session.getActionQueue().numberOfDeletions(),
 				persistenceContext.getEntityEntries().size()
 		);
 		LOG.debugf(
 				"Flushed: %s (re)creations, %s updates, %s removals to %s collections",
 				session.getActionQueue().numberOfCollectionCreations(),
 				session.getActionQueue().numberOfCollectionUpdates(),
 				session.getActionQueue().numberOfCollectionRemovals(),
 				persistenceContext.getCollectionEntries().size()
 		);
 		new EntityPrinter( session.getFactory() ).toString(
 				persistenceContext.getEntitiesByKey().entrySet()
 		);
 	}
 
 	/**
 	 * process cascade save/update at the start of a flush to discover
 	 * any newly referenced entity that must be passed to saveOrUpdate(),
 	 * and also apply orphan delete
 	 */
 	private void prepareEntityFlushes(EventSource session) throws HibernateException {
 
-		LOG.debugf( "Processing flush-time cascades" );
+		LOG.debug( "Processing flush-time cascades" );
 
 		final Map.Entry[] list = IdentityMap.concurrentEntries( session.getPersistenceContext().getEntityEntries() );
 		//safe from concurrent modification because of how entryList() is implemented on IdentityMap
 		final int size = list.length;
 		final Object anything = getAnything();
 		for ( int i=0; i<size; i++ ) {
 			Map.Entry me = list[i];
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 			if ( status == Status.MANAGED || status == Status.SAVING || status == Status.READ_ONLY ) {
 				cascadeOnFlush( session, entry.getPersister(), me.getKey(), anything );
 			}
 		}
 	}
 
 	private void cascadeOnFlush(EventSource session, EntityPersister persister, Object object, Object anything)
 	throws HibernateException {
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadingAction(), Cascade.BEFORE_FLUSH, session )
 			.cascade( persister, object, anything );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected Object getAnything() { return null; }
 
 	protected CascadingAction getCascadingAction() {
 		return CascadingAction.SAVE_UPDATE;
 	}
 
 	/**
 	 * Initialize the flags of the CollectionEntry, including the
 	 * dirty check.
 	 */
 	private void prepareCollectionFlushes(SessionImplementor session) throws HibernateException {
 
 		// Initialize dirty flags for arrays + collections with composite elements
 		// and reset reached, doupdate, etc.
 
-		LOG.debugf( "Dirty checking collections" );
+		LOG.debug( "Dirty checking collections" );
 
 		final List list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		final int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry e = ( Map.Entry ) list.get( i );
 			( (CollectionEntry) e.getValue() ).preFlush( (PersistentCollection) e.getKey() );
 		}
 	}
 
 	/**
 	 * 1. detect any dirty entities
 	 * 2. schedule any entity updates
 	 * 3. search out any reachable collections
 	 */
 	private void flushEntities(FlushEvent event) throws HibernateException {
 
 		LOG.trace( "Flushing entities and processing referenced collections" );
 
 		// Among other things, updateReachables() will recursively load all
 		// collections that are moving roles. This might cause entities to
 		// be loaded.
 
 		// So this needs to be safe from concurrent modification problems.
 		// It is safe because of how IdentityMap implements entrySet()
 
 		final EventSource source = event.getSession();
 
 		final Map.Entry[] list = IdentityMap.concurrentEntries( source.getPersistenceContext().getEntityEntries() );
 		final int size = list.length;
 		for ( int i = 0; i < size; i++ ) {
 
 			// Update the status of the object and if necessary, schedule an update
 
 			Map.Entry me = list[i];
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 
 			if ( status != Status.LOADING && status != Status.GONE ) {
 				final FlushEntityEvent entityEvent = new FlushEntityEvent( source, me.getKey(), entry );
 				final EventListenerGroup<FlushEntityEventListener> listenerGroup = source
 						.getFactory()
 						.getServiceRegistry()
 						.getService( EventListenerRegistry.class )
 						.getEventListenerGroup( EventType.FLUSH_ENTITY );
 				for ( FlushEntityEventListener listener : listenerGroup.listeners() ) {
 					listener.onFlushEntity( entityEvent );
 				}
 			}
 		}
 
 		source.getActionQueue().sortActions();
 	}
 
 	/**
 	 * process any unreferenced collections and then inspect all known collections,
 	 * scheduling creates/removes/updates
 	 */
 	private void flushCollections(EventSource session) throws HibernateException {
 
 		LOG.trace( "Processing unreferenced collections" );
 
 		List list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry me = ( Map.Entry ) list.get( i );
 			CollectionEntry ce = (CollectionEntry) me.getValue();
 			if ( !ce.isReached() && !ce.isIgnore() ) {
 				Collections.processUnreachableCollection( (PersistentCollection) me.getKey(), session );
 			}
 		}
 
 		// Schedule updates to collections:
 
 		LOG.trace( "Scheduling collection removes/(re)creates/updates" );
 
 		list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		size = list.size();
 		ActionQueue actionQueue = session.getActionQueue();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry me = (Map.Entry) list.get(i);
 			PersistentCollection coll = (PersistentCollection) me.getKey();
 			CollectionEntry ce = (CollectionEntry) me.getValue();
 
 			if ( ce.isDorecreate() ) {
 				session.getInterceptor().onCollectionRecreate( coll, ce.getCurrentKey() );
 				actionQueue.addAction(
 						new CollectionRecreateAction(
 								coll,
 								ce.getCurrentPersister(),
 								ce.getCurrentKey(),
 								session
 							)
 					);
 			}
 			if ( ce.isDoremove() ) {
 				session.getInterceptor().onCollectionRemove( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionRemoveAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 			if ( ce.isDoupdate() ) {
 				session.getInterceptor().onCollectionUpdate( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionUpdateAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 
 		}
 
 		actionQueue.sortCollectionActions();
 
 	}
 
 	/**
 	 * Execute all SQL and second-level cache updates, in a
 	 * special order so that foreign-key constraints cannot
 	 * be violated:
 	 * <ol>
 	 * <li> Inserts, in the order they were performed
 	 * <li> Updates
 	 * <li> Deletion of collection elements
 	 * <li> Insertion of collection elements
 	 * <li> Deletes, in the order they were performed
 	 * </ol>
 	 */
 	protected void performExecutions(EventSource session) throws HibernateException {
 
 		LOG.trace( "Executing flush" );
 
 		try {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushBeginning();
 			// we need to lock the collection caches before
 			// executing entity inserts/updates in order to
 			// account for bidi associations
 			session.getActionQueue().prepareActions();
 			session.getActionQueue().executeActions();
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushEnding();
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Post-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * 1. Recreate the collection key -> collection map
 	 * 2. rebuild the collection entries
 	 * 3. call Interceptor.postFlush()
 	 */
 	protected void postFlush(SessionImplementor session) throws HibernateException {
 
 		LOG.trace( "Post flush" );
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		persistenceContext.getCollectionsByKey().clear();
 		persistenceContext.getBatchFetchQueue()
 				.clearSubselects(); //the database has changed now, so the subselect results need to be invalidated
 
 		Iterator iter = persistenceContext.getCollectionEntries().entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			CollectionEntry collectionEntry = (CollectionEntry) me.getValue();
 			PersistentCollection persistentCollection = (PersistentCollection) me.getKey();
 			collectionEntry.postFlush(persistentCollection);
 			if ( collectionEntry.getLoadedPersister() == null ) {
 				//if the collection is dereferenced, remove from the session cache
 				//iter.remove(); //does not work, since the entrySet is not backed by the set
 				persistenceContext.getCollectionEntries()
 						.remove(persistentCollection);
 			}
 			else {
 				//otherwise recreate the mapping between the collection and its key
 				CollectionKey collectionKey = new CollectionKey(
 						collectionEntry.getLoadedPersister(),
 						collectionEntry.getLoadedKey()
 				);
 				persistenceContext.getCollectionsByKey().put(collectionKey, persistentCollection);
 			}
 		}
 
 		session.getInterceptor().postFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
index 0f6955ade7..54813bfcad 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractSaveEventListener.java
@@ -1,528 +1,528 @@
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
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.action.internal.EntityIdentityInsertAction;
 import org.hibernate.action.internal.EntityInsertAction;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.service.instrumentation.spi.InstrumentationService;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * A convenience bas class for listeners responding to save events.
  *
  * @author Steve Ebersole.
  */
 public abstract class AbstractSaveEventListener extends AbstractReassociateEventListener {
     public enum EntityState{
         PERSISTENT, TRANSIENT, DETACHED, DELETED;
     }
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractSaveEventListener.class.getName());
 
 	/**
 	 * Prepares the save call using the given requested id.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param requestedId The id to which to associate the entity.
 	 * @param entityName The name of the entity being saved.
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 *
 	 * @return The id used to save the entity.
 	 */
 	protected Serializable saveWithRequestedId(
 			Object entity,
 			Serializable requestedId,
 			String entityName,
 			Object anything,
 			EventSource source) {
 		return performSave(
 				entity,
 				requestedId,
 				source.getEntityPersister( entityName, entity ),
 				false,
 				anything,
 				source,
 				true
 		);
 	}
 
 	/**
 	 * Prepares the save call using a newly generated id.
 	 *
 	 * @param entity The entity to be saved
 	 * @param entityName The entity-name for the entity to be saved
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable saveWithGeneratedId(
 			Object entity,
 			String entityName,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 		EntityPersister persister = source.getEntityPersister( entityName, entity );
 		Serializable generatedId = persister.getIdentifierGenerator().generate( source, entity );
 		if ( generatedId == null ) {
 			throw new IdentifierGenerationException( "null id generated for:" + entity.getClass() );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR ) {
 			return source.getIdentifier( entity );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			return performSave( entity, null, persister, true, anything, source, requiresImmediateIdAccess );
 		}
 		else {
 			// TODO: define toString()s for generators
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Generated identifier: %s, using strategy: %s",
 						persister.getIdentifierType().toLoggableString( generatedId, source.getFactory() ),
 						persister.getIdentifierGenerator().getClass().getName() );
 			}
 
 			return performSave( entity, generatedId, persister, false, anything, source, true );
 		}
 	}
 
 	/**
 	 * Prepares the save call by checking the session caches for a pre-existing
 	 * entity and performing any lifecycle callbacks.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param id The id by which to save the entity.
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Is an identity column being used?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session from which the event originated.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSave(
 			Object entity,
 			Serializable id,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Saving {0}", MessageHelper.infoString( persister, id, source.getFactory() ) );
 		}
 
 		final EntityKey key;
 		if ( !useIdentityColumn ) {
 			key = source.generateEntityKey( id, persister );
 			Object old = source.getPersistenceContext().getEntity( key );
 			if ( old != null ) {
 				if ( source.getPersistenceContext().getEntry( old ).getStatus() == Status.DELETED ) {
 					source.forceFlush( source.getPersistenceContext().getEntry( old ) );
 				}
 				else {
 					throw new NonUniqueObjectException( id, persister.getEntityName() );
 				}
 			}
 			persister.setIdentifier( entity, id, source );
 		}
 		else {
 			key = null;
 		}
 
 		if ( invokeSaveLifecycle( entity, persister, source ) ) {
 			return id; //EARLY EXIT
 		}
 
 		return performSaveOrReplicate(
 				entity,
 				key,
 				persister,
 				useIdentityColumn,
 				anything,
 				source,
 				requiresImmediateIdAccess
 		);
 	}
 
 	protected boolean invokeSaveLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		// Sub-insertions should occur before containing insertion so
 		// Try to do the callback now
 		if ( persister.implementsLifecycle() ) {
-			LOG.debugf( "Calling onSave()" );
+			LOG.debug( "Calling onSave()" );
 			if ( ( ( Lifecycle ) entity ).onSave( source ) ) {
-				LOG.debugf( "Insertion vetoed by onSave()" );
+				LOG.debug( "Insertion vetoed by onSave()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Performs all the actual work needed to save an entity (well to get the save moved to
 	 * the execution queue).
 	 *
 	 * @param entity The entity to be saved
 	 * @param key The id to be used for saving the entity (or null, in the case of identity columns)
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Should an identity column be used for id generation?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of the current event.
 	 * @param requiresImmediateIdAccess Is access to the identifier required immediately
 	 * after the completion of the save?  persist(), for example, does not require this...
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSaveOrReplicate(
 			Object entity,
 			EntityKey key,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		Serializable id = key == null ? null : key.getIdentifier();
 
 		boolean inTxn = source.getTransactionCoordinator().isTransactionInProgress();
 		boolean shouldDelayIdentityInserts = !inTxn && !requiresImmediateIdAccess;
 
 		// Put a placeholder in entries, so we don't recurse back and try to save() the
 		// same object again. QUESTION: should this be done before onSave() is called?
 		// likewise, should it be done before onUpdate()?
 		source.getPersistenceContext().addEntry(
 				entity,
 				Status.SAVING,
 				null,
 				null,
 				id,
 				null,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				false,
 				false
 		);
 
 		cascadeBeforeSave( source, persister, entity, anything );
 
 		if ( useIdentityColumn && !shouldDelayIdentityInserts ) {
 			LOG.trace( "Executing insertions" );
 			source.getActionQueue().executeInserts();
 		}
 
 		Object[] values = persister.getPropertyValuesToInsert( entity, getMergeMap( anything ), source );
 		Type[] types = persister.getPropertyTypes();
 
 		boolean substitute = substituteValuesIfNecessary( entity, id, values, persister, source );
 
 		if ( persister.hasCollections() ) {
 			substitute = substitute || visitCollectionsBeforeSave( entity, id, values, types, source );
 		}
 
 		if ( substitute ) {
 			persister.setPropertyValues( entity, values );
 		}
 
 		TypeHelper.deepCopy(
 				values,
 				types,
 				persister.getPropertyUpdateability(),
 				values,
 				source
 		);
 
 		new ForeignKeys.Nullifier( entity, false, useIdentityColumn, source )
 				.nullifyTransientReferences( values, types );
 		new Nullability( source ).checkNullability( values, persister, false );
 
 		if ( useIdentityColumn ) {
 			EntityIdentityInsertAction insert = new EntityIdentityInsertAction(
 					values, entity, persister, source, shouldDelayIdentityInserts
 			);
 			if ( !shouldDelayIdentityInserts ) {
-				LOG.debugf( "Executing identity-insert immediately" );
+				LOG.debug( "Executing identity-insert immediately" );
 				source.getActionQueue().execute( insert );
 				id = insert.getGeneratedId();
 				key = source.generateEntityKey( id, persister );
 				source.getPersistenceContext().checkUniqueness( key, entity );
 			}
 			else {
-				LOG.debugf( "Delaying identity-insert due to no transaction in progress" );
+				LOG.debug( "Delaying identity-insert due to no transaction in progress" );
 				source.getActionQueue().addAction( insert );
 				key = insert.getDelayedEntityKey();
 			}
 		}
 
 		Object version = Versioning.getVersion( values, persister );
 		source.getPersistenceContext().addEntity(
 				entity,
 				( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				values,
 				key,
 				version,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				isVersionIncrementDisabled(),
 				false
 		);
 		//source.getPersistenceContext().removeNonExist( new EntityKey( id, persister, source.getEntityMode() ) );
 
 		if ( !useIdentityColumn ) {
 			source.getActionQueue().addAction(
 					new EntityInsertAction( id, values, entity, version, persister, source )
 			);
 		}
 
 		cascadeAfterSave( source, persister, entity, anything );
 
 		markInterceptorDirty( entity, persister, source );
 
 		return id;
 	}
 
 	private void markInterceptorDirty(Object entity, EntityPersister persister, EventSource source) {
 		InstrumentationService instrumentationService = persister.getFactory()
 				.getServiceRegistry()
 				.getService( InstrumentationService.class );
 		if ( instrumentationService.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.injectFieldInterceptor(
 					entity,
 					persister.getEntityName(),
 					null,
 					source
 			);
 			interceptor.dirty();
 		}
 	}
 
 	protected Map getMergeMap(Object anything) {
 		return null;
 	}
 
 	/**
 	 * After the save, will te version number be incremented
 	 * if the instance is modified?
 	 *
 	 * @return True if the version will be incremented on an entity change after save;
 	 *         false otherwise.
 	 */
 	protected boolean isVersionIncrementDisabled() {
 		return false;
 	}
 
 	protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
 		WrapVisitor visitor = new WrapVisitor( source );
 		// substitutes into values by side-effect
 		visitor.processEntityPropertyValues( values, types );
 		return visitor.isSubstitutionRequired();
 	}
 
 	/**
 	 * Perform any property value substitution that is necessary
 	 * (interceptor callback, version initialization...)
 	 *
 	 * @param entity The entity
 	 * @param id The entity identifier
 	 * @param values The snapshot entity state
 	 * @param persister The entity persister
 	 * @param source The originating session
 	 *
 	 * @return True if the snapshot state changed such that
 	 * reinjection of the values into the entity is required.
 	 */
 	protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		boolean substitute = source.getInterceptor().onSave(
 				entity,
 				id,
 				values,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		//keep the existing version number in the case of replicate!
 		if ( persister.isVersioned() ) {
 			substitute = Versioning.seedVersion(
 					values,
 					persister.getVersionProperty(),
 					persister.getVersionType(),
 					source
 			) || substitute;
 		}
 		return substitute;
 	}
 
 	/**
 	 * Handles the calls needed to perform pre-save cascades for the given entity.
 	 *
 	 * @param source The session from whcih the save event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity to be saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeBeforeSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to many-to-one BEFORE the parent is saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.BEFORE_INSERT_AFTER_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	/**
 	 * Handles to calls needed to perform post-save cascades.
 	 *
 	 * @param source The session from which the event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity beng saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeAfterSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to collections AFTER the collection owner was saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.AFTER_INSERT_BEFORE_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected abstract CascadingAction getCascadeAction();
 
 	/**
 	 * Determine whether the entity is persistent, detached, or transient
 	 *
 	 * @param entity The entity to check
 	 * @param entityName The name of the entity
 	 * @param entry The entity's entry in the persistence context
 	 * @param source The originating session.
 	 *
 	 * @return The state.
 	 */
 	protected EntityState getEntityState(
 			Object entity,
 			String entityName,
 			EntityEntry entry, //pass this as an argument only to avoid double looking
 			SessionImplementor source) {
 
 		if ( entry != null ) { // the object is persistent
 
 			//the entity is associated with the session, so check its status
 			if ( entry.getStatus() != Status.DELETED ) {
 				// do nothing for persistent instances
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Persistent instance of: {0}", getLoggableName( entityName, entity ) );
 				}
 				return EntityState.PERSISTENT;
 			}
 			// ie. e.status==DELETED
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Deleted instance of: {0}", getLoggableName( entityName, entity ) );
 			}
 			return EntityState.DELETED;
 		}
 		// the object is transient or detached
 
 		// the entity is not associated with the session, so
 		// try interceptor and unsaved-value
 
 		if ( ForeignKeys.isTransient( entityName, entity, getAssumedUnsaved(), source )) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Transient instance of: {0}", getLoggableName( entityName, entity ) );
 			}
 			return EntityState.TRANSIENT;
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Detached instance of: {0}", getLoggableName( entityName, entity ) );
 		}
 		return EntityState.DETACHED;
 	}
 
 	protected String getLoggableName(String entityName, Object entity) {
 		return entityName == null ? entity.getClass().getName() : entityName;
 	}
 
 	protected Boolean getAssumedUnsaved() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
index 0a317ce2a4..491b0820e0 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDeleteEventListener.java
@@ -1,352 +1,352 @@
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
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.TransientObjectException;
 import org.hibernate.action.internal.EntityDeleteAction;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.DeleteEvent;
 import org.hibernate.event.spi.DeleteEventListener;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default delete event listener used by hibernate for deleting entities
  * from the datastore in response to generated delete events.
  *
  * @author Steve Ebersole
  */
 public class DefaultDeleteEventListener implements DeleteEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultDeleteEventListener.class.getName());
 
 	/**
 	 * Handle the given delete event.
 	 *
 	 * @param event The delete event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onDelete(DeleteEvent event) throws HibernateException {
 		onDelete( event, new IdentitySet() );
 	}
 
 	/**
 	 * Handle the given delete event.  This is the cascaded form.
 	 *
 	 * @param event The delete event.
 	 * @param transientEntities The cache of entities already deleted
 	 *
 	 * @throws HibernateException
 	 */
 	public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
 
 		final EventSource source = event.getSession();
 
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
 		Object entity = persistenceContext.unproxyAndReassociate( event.getObject() );
 
 		EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		final EntityPersister persister;
 		final Serializable id;
 		final Object version;
 
 		if ( entityEntry == null ) {
 			LOG.trace( "Entity was not persistent in delete processing" );
 
 			persister = source.getEntityPersister( event.getEntityName(), entity );
 
 			if ( ForeignKeys.isTransient( persister.getEntityName(), entity, null, source ) ) {
 				deleteTransientEntity( source, entity, event.isCascadeDeleteEnabled(), persister, transientEntities );
 				// EARLY EXIT!!!
 				return;
 			}
 			performDetachedEntityDeletionCheck( event );
 
 			id = persister.getIdentifier( entity, source );
 
 			if ( id == null ) {
 				throw new TransientObjectException(
 						"the detached instance passed to delete() had a null identifier"
 				);
 			}
 
 			final EntityKey key = source.generateEntityKey( id, persister );
 
 			persistenceContext.checkUniqueness( key, entity );
 
 			new OnUpdateVisitor( source, id, entity ).process( entity, persister );
 
 			version = persister.getVersion( entity );
 
 			entityEntry = persistenceContext.addEntity(
 					entity,
 					( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 					persister.getPropertyValues( entity ),
 					key,
 					version,
 					LockMode.NONE,
 					true,
 					persister,
 					false,
 					false
 			);
 		}
 		else {
 			LOG.trace( "Deleting a persistent instance" );
 
 			if ( entityEntry.getStatus() == Status.DELETED || entityEntry.getStatus() == Status.GONE ) {
 				LOG.trace( "Object was already deleted" );
 				return;
 			}
 			persister = entityEntry.getPersister();
 			id = entityEntry.getId();
 			version = entityEntry.getVersion();
 		}
 
 		/*if ( !persister.isMutable() ) {
 			throw new HibernateException(
 					"attempted to delete an object of immutable class: " +
 					MessageHelper.infoString(persister)
 				);
 		}*/
 
 		if ( invokeDeleteLifecycle( source, entity, persister ) ) {
 			return;
 		}
 
 		deleteEntity( source, entity, entityEntry, event.isCascadeDeleteEnabled(), persister, transientEntities );
 
 		if ( source.getFactory().getSettings().isIdentifierRollbackEnabled() ) {
 			persister.resetIdentifier( entity, id, version, source );
 		}
 	}
 
 	/**
 	 * Called when we have recognized an attempt to delete a detached entity.
 	 * <p/>
 	 * This is perfectly valid in Hibernate usage; JPA, however, forbids this.
 	 * Thus, this is a hook for HEM to affect this behavior.
 	 *
 	 * @param event The event.
 	 */
 	protected void performDetachedEntityDeletionCheck(DeleteEvent event) {
 		// ok in normal Hibernate usage to delete a detached entity; JPA however
 		// forbids it, thus this is a hook for HEM to affect this behavior
 	}
 
 	/**
 	 * We encountered a delete request on a transient instance.
 	 * <p/>
 	 * This is a deviation from historical Hibernate (pre-3.2) behavior to
 	 * align with the JPA spec, which states that transient entities can be
 	 * passed to remove operation in which case cascades still need to be
 	 * performed.
 	 *
 	 * @param session The session which is the source of the event
 	 * @param entity The entity being delete processed
 	 * @param cascadeDeleteEnabled Is cascading of deletes enabled
 	 * @param persister The entity persister
 	 * @param transientEntities A cache of already visited transient entities
 	 * (to avoid infinite recursion).
 	 */
 	protected void deleteTransientEntity(
 			EventSource session,
 			Object entity,
 			boolean cascadeDeleteEnabled,
 			EntityPersister persister,
 			Set transientEntities) {
 		LOG.handlingTransientEntity();
 		if ( transientEntities.contains( entity ) ) {
 			LOG.trace( "Already handled transient entity; skipping" );
 			return;
 		}
 		transientEntities.add( entity );
 		cascadeBeforeDelete( session, persister, entity, null, transientEntities );
 		cascadeAfterDelete( session, persister, entity, transientEntities );
 	}
 
 	/**
 	 * Perform the entity deletion.  Well, as with most operations, does not
 	 * really perform it; just schedules an action/execution with the
 	 * {@link org.hibernate.engine.spi.ActionQueue} for execution during flush.
 	 *
 	 * @param session The originating session
 	 * @param entity The entity to delete
 	 * @param entityEntry The entity's entry in the {@link PersistenceContext}
 	 * @param isCascadeDeleteEnabled Is delete cascading enabled?
 	 * @param persister The entity persister.
 	 * @param transientEntities A cache of already deleted entities.
 	 */
 	protected final void deleteEntity(
 			final EventSource session,
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean isCascadeDeleteEnabled,
 			final EntityPersister persister,
 			final Set transientEntities) {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Deleting {0}", MessageHelper.infoString( persister, entityEntry.getId(), session.getFactory() ) );
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final Type[] propTypes = persister.getPropertyTypes();
 		final Object version = entityEntry.getVersion();
 
 		final Object[] currentState;
 		if ( entityEntry.getLoadedState() == null ) { //ie. the entity came in from update()
 			currentState = persister.getPropertyValues( entity );
 		}
 		else {
 			currentState = entityEntry.getLoadedState();
 		}
 
 		final Object[] deletedState = createDeletedState( persister, currentState, session );
 		entityEntry.setDeletedState( deletedState );
 
 		session.getInterceptor().onDelete(
 				entity,
 				entityEntry.getId(),
 				deletedState,
 				persister.getPropertyNames(),
 				propTypes
 		);
 
 		// before any callbacks, etc, so subdeletions see that this deletion happened first
 		persistenceContext.setEntryStatus( entityEntry, Status.DELETED );
 		final EntityKey key = session.generateEntityKey( entityEntry.getId(), persister );
 
 		cascadeBeforeDelete( session, persister, entity, entityEntry, transientEntities );
 
 		new ForeignKeys.Nullifier( entity, true, false, session )
 				.nullifyTransientReferences( entityEntry.getDeletedState(), propTypes );
 		new Nullability( session ).checkNullability( entityEntry.getDeletedState(), persister, true );
 		persistenceContext.getNullifiableEntityKeys().add( key );
 
 		// Ensures that containing deletions happen before sub-deletions
 		session.getActionQueue().addAction(
 				new EntityDeleteAction(
 						entityEntry.getId(),
 						deletedState,
 						version,
 						entity,
 						persister,
 						isCascadeDeleteEnabled,
 						session
 				)
 		);
 
 		cascadeAfterDelete( session, persister, entity, transientEntities );
 
 		// the entry will be removed after the flush, and will no longer
 		// override the stale snapshot
 		// This is now handled by removeEntity() in EntityDeleteAction
 		//persistenceContext.removeDatabaseSnapshot(key);
 	}
 
 	private Object[] createDeletedState(EntityPersister persister, Object[] currentState, EventSource session) {
 		Type[] propTypes = persister.getPropertyTypes();
 		final Object[] deletedState = new Object[propTypes.length];
 //		TypeFactory.deepCopy( currentState, propTypes, persister.getPropertyUpdateability(), deletedState, session );
 		boolean[] copyability = new boolean[propTypes.length];
 		java.util.Arrays.fill( copyability, true );
 		TypeHelper.deepCopy( currentState, propTypes, copyability, deletedState, session );
 		return deletedState;
 	}
 
 	protected boolean invokeDeleteLifecycle(EventSource session, Object entity, EntityPersister persister) {
 		if ( persister.implementsLifecycle() ) {
-			LOG.debugf( "Calling onDelete()" );
+			LOG.debug( "Calling onDelete()" );
 			if ( ( ( Lifecycle ) entity ).onDelete( session ) ) {
-				LOG.debugf( "Deletion vetoed by onDelete()" );
+				LOG.debug( "Deletion vetoed by onDelete()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected void cascadeBeforeDelete(
 			EventSource session,
 			EntityPersister persister,
 			Object entity,
 			EntityEntry entityEntry,
 			Set transientEntities) throws HibernateException {
 
 		CacheMode cacheMode = session.getCacheMode();
 		session.setCacheMode( CacheMode.GET );
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			// cascade-delete to collections BEFORE the collection owner is deleted
 			new Cascade( CascadingAction.DELETE, Cascade.AFTER_INSERT_BEFORE_DELETE, session )
 					.cascade( persister, entity, transientEntities );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 			session.setCacheMode( cacheMode );
 		}
 	}
 
 	protected void cascadeAfterDelete(
 			EventSource session,
 			EntityPersister persister,
 			Object entity,
 			Set transientEntities) throws HibernateException {
 
 		CacheMode cacheMode = session.getCacheMode();
 		session.setCacheMode( CacheMode.GET );
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			// cascade-delete to many-to-one AFTER the parent was deleted
 			new Cascade( CascadingAction.DELETE, Cascade.BEFORE_INSERT_AFTER_DELETE, session )
 					.cascade( persister, entity, transientEntities );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 			session.setCacheMode( cacheMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDirtyCheckEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDirtyCheckEventListener.java
index 5763885a1a..6dd7e5cbf3 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDirtyCheckEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultDirtyCheckEventListener.java
@@ -1,68 +1,68 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.event.spi.DirtyCheckEvent;
 import org.hibernate.event.spi.DirtyCheckEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Defines the default dirty-check event listener used by hibernate for
  * checking the session for dirtiness in response to generated dirty-check
  * events.
  *
  * @author Steve Ebersole
  */
 public class DefaultDirtyCheckEventListener extends AbstractFlushingEventListener implements DirtyCheckEventListener {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DefaultDirtyCheckEventListener.class.getName() );
 
 	/**
 	 * Handle the given dirty-check event.
 	 * 
 	 * @param event The dirty-check event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onDirtyCheck(DirtyCheckEvent event) throws HibernateException {
 
 		int oldSize = event.getSession().getActionQueue().numberOfCollectionRemovals();
 
 		try {
 			flushEverythingToExecutions(event);
 			boolean wasNeeded = event.getSession().getActionQueue().hasAnyQueuedActions();
 			if ( wasNeeded )
-				LOG.debugf( "Session dirty" );
+				LOG.debug( "Session dirty" );
 			else
-				LOG.debugf( "Session not dirty" );
+				LOG.debug( "Session not dirty" );
 			event.setDirty( wasNeeded );
 		}
 		finally {
 			event.getSession().getActionQueue().clearFromFlushNeededCheck( oldSize );
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
index ac0ea15cce..6683a34115 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
@@ -1,654 +1,654 @@
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
         if (!persister.hasProxy()) return load(event, persister, keyToLoad, options);
         final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
 
 		// look for a proxy
         Object proxy = persistenceContext.getProxy(keyToLoad);
         if (proxy != null) return returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
         if (options.isAllowProxyCreation()) return createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
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
-			LOG.debugf( "Load request found matching entity in context, but it is scheduled for removal; returning null" );
+			LOG.debug( "Load request found matching entity in context, but it is scheduled for removal; returning null" );
 			return null;
 		}
 		if ( entity == INCONSISTENT_RTN_CLASS_MARKER ) {
-			LOG.debugf( "Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null" );
+			LOG.debug( "Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null" );
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
 			return entity;
 		}
 
 		if ( traceEnabled ) LOG.tracev( "Object not resolved in any cache: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 
 		return loadFromDatasource(event, persister, keyToLoad, options);
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java
index 0fb74616e1..d265931c03 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultPersistEventListener.java
@@ -1,194 +1,194 @@
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
 
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.id.ForeignGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * Defines the default create event listener used by hibernate for creating
  * transient entities in response to generated create events.
  *
  * @author Gavin King
  */
 public class DefaultPersistEventListener extends AbstractSaveEventListener implements PersistEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultPersistEventListener.class.getName());
 
 	/**
 	 * Handle the given create event.
 	 *
 	 * @param event The create event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onPersist(PersistEvent event) throws HibernateException {
 		onPersist( event, IdentityMap.instantiate(10) );
 	}
 
 
 	/**
 	 * Handle the given create event.
 	 *
 	 * @param event The create event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onPersist(PersistEvent event, Map createCache) throws HibernateException {
 		final SessionImplementor source = event.getSession();
 		final Object object = event.getObject();
 
 		final Object entity;
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				if ( li.getSession() == source ) {
 					return; //NOTE EARLY EXIT!
 				}
 				else {
 					throw new PersistentObjectException( "uninitialized proxy passed to persist()" );
 				}
 			}
 			entity = li.getImplementation();
 		}
 		else {
 			entity = object;
 		}
 
 		final String entityName;
 		if ( event.getEntityName() != null ) {
 			entityName = event.getEntityName();
 		}
 		else {
 			entityName = source.bestGuessEntityName( entity );
 			event.setEntityName( entityName );
 		}
 
 		final EntityEntry entityEntry = source.getPersistenceContext().getEntry( entity );
 		EntityState entityState = getEntityState( entity, entityName, entityEntry, source );
 		if ( entityState == EntityState.DETACHED ) {
 			// JPA 2, in its version of a "foreign generated", allows the id attribute value
 			// to be manually set by the user, even though this manual value is irrelevant.
 			// The issue is that this causes problems with the Hibernate unsaved-value strategy
 			// which comes into play here in determining detached/transient state.
 			//
 			// Detect if we have this situation and if so null out the id value and calculate the
 			// entity state again.
 
 			// NOTE: entityEntry must be null to get here, so we cannot use any of its values
 			EntityPersister persister = source.getFactory().getEntityPersister( entityName );
 			if ( ForeignGenerator.class.isInstance( persister.getIdentifierGenerator() ) ) {
 				if ( LOG.isDebugEnabled() && persister.getIdentifier( entity, source ) != null ) {
-					LOG.debugf( "Resetting entity id attribute to null for foreign generator" );
+					LOG.debug( "Resetting entity id attribute to null for foreign generator" );
 				}
 				persister.setIdentifier( entity, null, source );
 				entityState = getEntityState( entity, entityName, entityEntry, source );
 			}
 		}
 
 		switch ( entityState ) {
 			case DETACHED:
 				throw new PersistentObjectException(
 						"detached entity passed to persist: " +
 								getLoggableName( event.getEntityName(), entity )
 				);
 			case PERSISTENT:
 				entityIsPersistent( event, createCache );
 				break;
 			case TRANSIENT:
 				entityIsTransient( event, createCache );
 				break;
 			default:
 				throw new ObjectDeletedException(
 						"deleted entity passed to persist",
 						null,
 						getLoggableName( event.getEntityName(), entity )
 				);
 		}
 
 	}
 
 	protected void entityIsPersistent(PersistEvent event, Map createCache) {
 		LOG.trace( "Ignoring persistent instance" );
 		final EventSource source = event.getSession();
 
 		//TODO: check that entry.getIdentifier().equals(requestedId)
 
 		final Object entity = source.getPersistenceContext().unproxy( event.getObject() );
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		if ( createCache.put(entity, entity)==null ) {
 			//TODO: merge into one method!
 			cascadeBeforeSave(source, persister, entity, createCache);
 			cascadeAfterSave(source, persister, entity, createCache);
 		}
 
 	}
 
 	/**
 	 * Handle the given create event.
 	 *
 	 * @param event The save event to be handled.
 	 * @throws HibernateException
 	 */
 	protected void entityIsTransient(PersistEvent event, Map createCache) throws HibernateException {
 
 		LOG.trace( "Saving transient instance" );
 
 		final EventSource source = event.getSession();
 
 		final Object entity = source.getPersistenceContext().unproxy( event.getObject() );
 
 		if ( createCache.put(entity, entity)==null ) {
 			saveWithGeneratedId( entity, event.getEntityName(), createCache, source, false );
 		}
 
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingAction.PERSIST;
 	}
 
 	@Override
     protected Boolean getAssumedUnsaved() {
 		return Boolean.TRUE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
index 3e6be44dce..1e8276f7c8 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultSaveOrUpdateEventListener.java
@@ -1,373 +1,373 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 
 /**
  * Defines the default listener used by Hibernate for handling save-update
  * events.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class DefaultSaveOrUpdateEventListener extends AbstractSaveEventListener implements SaveOrUpdateEventListener {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DefaultSaveOrUpdateEventListener.class.getName() );
 
 	/**
 	 * Handle the given update event.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
 		final SessionImplementor source = event.getSession();
 		final Object object = event.getObject();
 		final Serializable requestedId = event.getRequestedId();
 
 		if ( requestedId != null ) {
 			//assign the requested id to the proxy, *before*
 			//reassociating the proxy
 			if ( object instanceof HibernateProxy ) {
 				( ( HibernateProxy ) object ).getHibernateLazyInitializer().setIdentifier( requestedId );
 			}
 		}
 
 		// For an uninitialized proxy, noop, don't even need to return an id, since it is never a save()
 		if ( reassociateIfUninitializedProxy( object, source ) ) {
 			LOG.trace( "Reassociated uninitialized proxy" );
 		}
 		else {
 			//initialize properties of the event:
 			final Object entity = source.getPersistenceContext().unproxyAndReassociate( object );
 			event.setEntity( entity );
 			event.setEntry( source.getPersistenceContext().getEntry( entity ) );
 			//return the id in the event object
 			event.setResultId( performSaveOrUpdate( event ) );
 		}
 
 	}
 
 	protected boolean reassociateIfUninitializedProxy(Object object, SessionImplementor source) {
 		return source.getPersistenceContext().reassociateIfUninitializedProxy( object );
 	}
 
 	protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
 		EntityState entityState = getEntityState(
 				event.getEntity(),
 				event.getEntityName(),
 				event.getEntry(),
 				event.getSession()
 		);
 
 		switch ( entityState ) {
 			case DETACHED:
 				entityIsDetached( event );
 				return null;
 			case PERSISTENT:
 				return entityIsPersistent( event );
 			default: //TRANSIENT or DELETED
 				return entityIsTransient( event );
 		}
 	}
 
 	protected Serializable entityIsPersistent(SaveOrUpdateEvent event) throws HibernateException {
 		LOG.trace( "Ignoring persistent instance" );
 
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "entity was transient or detached" );
 		}
 		else {
 
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				throw new AssertionFailure( "entity was deleted" );
 			}
 
 			final SessionFactoryImplementor factory = event.getSession().getFactory();
 
 			Serializable requestedId = event.getRequestedId();
 
 			Serializable savedId;
 			if ( requestedId == null ) {
 				savedId = entityEntry.getId();
 			}
 			else {
 
 				final boolean isEqual = !entityEntry.getPersister().getIdentifierType()
 						.isEqual( requestedId, entityEntry.getId(), factory );
 
 				if ( isEqual ) {
 					throw new PersistentObjectException(
 							"object passed to save() was already persistent: " +
 									MessageHelper.infoString( entityEntry.getPersister(), requestedId, factory )
 					);
 				}
 
 				savedId = requestedId;
 
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Object already associated with session: {0}", MessageHelper.infoString( entityEntry.getPersister(), savedId, factory ) );
 			}
 
 			return savedId;
 
 		}
 	}
 
 	/**
 	 * The given save-update event named a transient entity.
 	 * <p/>
 	 * Here, we will perform the save processing.
 	 *
 	 * @param event The save event to be handled.
 	 *
 	 * @return The entity's identifier after saving.
 	 */
 	protected Serializable entityIsTransient(SaveOrUpdateEvent event) {
 
 		LOG.trace( "Saving transient instance" );
 
 		final EventSource source = event.getSession();
 
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry != null ) {
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				source.forceFlush( entityEntry );
 			}
 			else {
 				throw new AssertionFailure( "entity was persistent" );
 			}
 		}
 
 		Serializable id = saveWithGeneratedOrRequestedId( event );
 
 		source.getPersistenceContext().reassociateProxy( event.getObject(), id );
 
 		return id;
 	}
 
 	/**
 	 * Save the transient instance, assigning the right identifier
 	 *
 	 * @param event The initiating event.
 	 *
 	 * @return The entity's identifier value after saving.
 	 */
 	protected Serializable saveWithGeneratedOrRequestedId(SaveOrUpdateEvent event) {
 		return saveWithGeneratedId(
 				event.getEntity(),
 				event.getEntityName(),
 				null,
 				event.getSession(),
 				true
 		);
 	}
 
 	/**
 	 * The given save-update event named a detached entity.
 	 * <p/>
 	 * Here, we will perform the update processing.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	protected void entityIsDetached(SaveOrUpdateEvent event) {
 
 		LOG.trace( "Updating detached instance" );
 
 		if ( event.getSession().getPersistenceContext().isEntryFor( event.getEntity() ) ) {
 			//TODO: assertion only, could be optimized away
 			throw new AssertionFailure( "entity was persistent" );
 		}
 
 		Object entity = event.getEntity();
 
 		EntityPersister persister = event.getSession().getEntityPersister( event.getEntityName(), entity );
 
 		event.setRequestedId(
 				getUpdateId(
 						entity, persister, event.getRequestedId(), event.getSession()
 				)
 		);
 
 		performUpdate( event, entity, persister );
 
 	}
 
 	/**
 	 * Determine the id to use for updating.
 	 *
 	 * @param entity The entity.
 	 * @param persister The entity persister
 	 * @param requestedId The requested identifier
 	 * @param session The session
 	 *
 	 * @return The id.
 	 *
 	 * @throws TransientObjectException If the entity is considered transient.
 	 */
 	protected Serializable getUpdateId(
 			Object entity,
 			EntityPersister persister,
 			Serializable requestedId,
 			SessionImplementor session) {
 		// use the id assigned to the instance
 		Serializable id = persister.getIdentifier( entity, session );
 		if ( id == null ) {
 			// assume this is a newly instantiated transient object
 			// which should be saved rather than updated
 			throw new TransientObjectException(
 					"The given object has a null identifier: " +
 							persister.getEntityName()
 			);
 		}
 		else {
 			return id;
 		}
 
 	}
 
 	protected void performUpdate(
 			SaveOrUpdateEvent event,
 			Object entity,
 			EntityPersister persister) throws HibernateException {
 
 		if ( !persister.isMutable() ) {
 			LOG.trace( "Immutable instance passed to performUpdate()" );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating {0}",
 					MessageHelper.infoString( persister, event.getRequestedId(), event.getSession().getFactory() ) );
 		}
 
 		final EventSource source = event.getSession();
 		final EntityKey key = source.generateEntityKey( event.getRequestedId(), persister );
 
 		source.getPersistenceContext().checkUniqueness(key, entity);
 
 		if (invokeUpdateLifecycle(entity, persister, source)) {
             reassociate(event, event.getObject(), event.getRequestedId(), persister);
             return;
         }
 
 		// this is a transient object with existing persistent state not loaded by the session
 
 		new OnUpdateVisitor(source, event.getRequestedId(), entity).process(entity, persister);
 
 		// TODO: put this stuff back in to read snapshot from
         // the second-level cache (needs some extra work)
         /*Object[] cachedState = null;
 
         if ( persister.hasCache() ) {
         	CacheEntry entry = (CacheEntry) persister.getCache()
         			.get( event.getRequestedId(), source.getTimestamp() );
             cachedState = entry==null ?
             		null :
             		entry.getState(); //TODO: half-assemble this stuff
         }*/
 
 		source.getPersistenceContext().addEntity(
 				entity,
 				(persister.isMutable() ? Status.MANAGED : Status.READ_ONLY),
 				null, // cachedState,
 				key,
 				persister.getVersion( entity ),
 				LockMode.NONE,
 				true,
 				persister,
 				false,
 				true // assume true, since we don't really know, and it doesn't matter
 				);
 
 		persister.afterReassociate(entity, source);
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating {0}", MessageHelper.infoString( persister, event.getRequestedId(), source.getFactory() ) );
 		}
 
 		cascadeOnUpdate( event, persister, entity );
 	}
 
 	protected boolean invokeUpdateLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		if ( persister.implementsLifecycle() ) {
-			LOG.debugf( "Calling onUpdate()" );
+			LOG.debug( "Calling onUpdate()" );
 			if ( ( (Lifecycle) entity ).onUpdate( source ) ) {
-				LOG.debugf( "Update vetoed by onUpdate()" );
+				LOG.debug( "Update vetoed by onUpdate()" );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Handles the calls needed to perform cascades as part of an update request
 	 * for the given entity.
 	 *
 	 * @param event The event currently being processed.
 	 * @param persister The defined persister for the entity being updated.
 	 * @param entity The entity being updated.
 	 */
 	private void cascadeOnUpdate(SaveOrUpdateEvent event, EntityPersister persister, Object entity) {
 		EventSource source = event.getSession();
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( CascadingAction.SAVE_UPDATE, Cascade.AFTER_UPDATE, source )
 					.cascade( persister, entity );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingAction.SAVE_UPDATE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ErrorCounter.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ErrorCounter.java
index 8df335750a..b2a88395f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ErrorCounter.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/ErrorCounter.java
@@ -1,84 +1,84 @@
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
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import antlr.RecognitionException;
 import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * An error handler that counts parsing errors and warnings.
  */
 public class ErrorCounter implements ParseErrorHandler {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ErrorCounter.class.getName());
 
 	private List errorList = new ArrayList();
 	private List warningList = new ArrayList();
 	private List recognitionExceptions = new ArrayList();
 
 	public void reportError(RecognitionException e) {
 		reportError( e.toString() );
 		recognitionExceptions.add( e );
         LOG.error(e.toString(), e);
 	}
 
 	public void reportError(String message) {
         LOG.error(message);
 		errorList.add( message );
 	}
 
 	public int getErrorCount() {
 		return errorList.size();
 	}
 
 	public void reportWarning(String message) {
-        LOG.debugf(message);
+		LOG.debug(message);
 		warningList.add( message );
 	}
 
 	private String getErrorString() {
 		StringBuffer buf = new StringBuffer();
 		for ( Iterator iterator = errorList.iterator(); iterator.hasNext(); ) {
 			buf.append( ( String ) iterator.next() );
 			if ( iterator.hasNext() ) buf.append( "\n" );
 
 		}
 		return buf.toString();
 	}
 
 	public void throwQueryException() throws QueryException {
 		if ( getErrorCount() > 0 ) {
             if (recognitionExceptions.size() > 0) throw QuerySyntaxException.convert((RecognitionException)recognitionExceptions.get(0));
             throw new QueryException(getErrorString());
         }
-        LOG.debugf("throwQueryException() : no errors");
+		LOG.debug("throwQueryException() : no errors");
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
index 1d744f10f3..eb67ef858b 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
@@ -1,1252 +1,1252 @@
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import antlr.ASTFactory;
 import antlr.RecognitionException;
 import antlr.SemanticException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.engine.internal.ParameterBinder;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.HqlSqlBaseWalker;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.tree.AggregateNode;
 import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
 import org.hibernate.hql.internal.ast.tree.CollectionFunction;
 import org.hibernate.hql.internal.ast.tree.ConstructorNode;
 import org.hibernate.hql.internal.ast.tree.DeleteStatement;
 import org.hibernate.hql.internal.ast.tree.DotNode;
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
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.param.CollectionFilterKeyParameterSpecification;
 import org.hibernate.param.NamedParameterSpecification;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.param.PositionalParameterSpecification;
 import org.hibernate.param.VersionTypeSeedParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.DbTimestampType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 import org.hibernate.usertype.UserVersionType;
 
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HqlSqlWalker.class.getName());
 
 	private final QueryTranslatorImpl queryTranslatorImpl;
 	private final HqlParser hqlParser;
 	private final SessionFactoryHelper sessionFactoryHelper;
 	private final Map tokenReplacements;
 	private final AliasGenerator aliasGenerator = new AliasGenerator();
 	private final LiteralProcessor literalProcessor;
 	private final ParseErrorHandler parseErrorHandler;
 	private final ASTPrinter printer;
 	private final String collectionFilterRole;
 
 	private FromClause currentFromClause = null;
 	private SelectClause selectClause;
 
 	/**
 	 * Maps each top-level result variable to its SelectExpression;
 	 * (excludes result variables defined in subqueries)
 	 **/
 	private Map<String, SelectExpression> selectExpressionsByResultVariable = new HashMap();
 
 	private Set querySpaces = new HashSet();
 
 	private int parameterCount;
 	private Map namedParameters = new HashMap();
 	private ArrayList parameters = new ArrayList();
 	private int numberOfParametersInSetClause;
 	private int positionalParameterCount;
 
 	private ArrayList assignmentSpecifications = new ArrayList();
 
 	private JoinType impliedJoinType = JoinType.INNER_JOIN;
 
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
 		this.parseErrorHandler = new ErrorCounter();
 		this.queryTranslatorImpl = qti;
 		this.sessionFactoryHelper = new SessionFactoryHelper( sfi );
 		this.literalProcessor = new LiteralProcessor( this );
 		this.tokenReplacements = tokenReplacements;
 		this.collectionFilterRole = collectionRole;
 		this.hqlParser = parser;
 		this.printer = new ASTPrinter( SqlTokenTypes.class );
 	}
 
 
 	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int traceDepth = 0;
 
 	@Override
 	public void traceIn(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) return;
 		if ( inputState.guessing > 0 ) return;
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
 		if ( !LOG.isTraceEnabled() ) return;
 		if ( inputState.guessing > 0 ) return;
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
 				AST fromElement = ASTUtil.create( inputAstFactory, HqlTokenTypes.FILTER_ENTITY, collectionElementEntityName );
 				ASTUtil.createSibling( inputAstFactory, HqlTokenTypes.ALIAS, "this", fromElement );
 				fromClauseInput.addChild( fromElement );
 				// Show the modified AST.
-                LOG.debugf("prepareFromClauseInputTree() : Filter - Added 'this' as a from element...");
+                LOG.debug("prepareFromClauseInputTree() : Filter - Added 'this' as a from element...");
 				queryTranslatorImpl.showHqlAst( hqlParser.getAST() );
 
 				// Create a parameter specification for the collection filter...
 				Type collectionFilterKeyType = sessionFactoryHelper.requireQueryableCollection( collectionFilterRole ).getKeyType();
 				ParameterNode collectionFilterKeyParameter = ( ParameterNode ) astFactory.create( PARAM, "?" );
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
 	public Set getQuerySpaces() {
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
-        LOG.debugf("createFromFilterElement() : processed filter FROM element.");
+        LOG.debug("createFromFilterElement() : processed filter FROM element.");
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
 
         if (LOG.isDebugEnabled()) LOG.debugf("createFromJoinElement() : %s",
                                              getASTPrinter().showAsString(fromElement, "-- join tree --"));
 	}
 
 	private void handleWithFragment(FromElement fromElement, AST hqlWithNode) throws SemanticException {
 		try {
 			withClause( hqlWithNode );
 			AST hqlSqlWithNode = returnAST;
             if (LOG.isDebugEnabled()) LOG.debugf("handleWithFragment() : %s",
                                                  getASTPrinter().showAsString(hqlSqlWithNode, "-- with clause --"));
 			WithClauseVisitor visitor = new WithClauseVisitor( fromElement );
 			NodeTraverser traverser = new NodeTraverser( visitor );
 			traverser.traverseDepthFirst( hqlSqlWithNode );
 
 			String withClauseJoinAlias = visitor.getJoinAlias();
 			if ( withClauseJoinAlias == null ) {
 				withClauseJoinAlias = fromElement.getCollectionTableAlias();
 			}
 			else {
 				FromElement referencedFromElement = visitor.getReferencedFromElement();
 				if ( referencedFromElement != fromElement ) {
 					throw new InvalidWithClauseException( "with-clause expressions did not reference from-clause element to which the with-clause was associated" );
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
 		private FromElement referencedFromElement;
 		private String joinAlias;
 
 		public WithClauseVisitor(FromElement fromElement) {
 			this.joinFragment = fromElement;
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
 						throw new InvalidWithClauseException( "with clause can only reference columns in the driving table" );
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
 				for ( int i = 0; i < specs.length; i++ ) {
 					applyParameterSpecification( specs[i] );
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
 
 	public static boolean supportsIdGenWithBulkInsertion(IdentifierGenerator generator) {
 		return SequenceGenerator.class.isAssignableFrom( generator.getClass() )
 		        || PostInsertIdentifierGenerator.class.isAssignableFrom( generator.getClass() );
 	}
 
 	@Override
     protected void postProcessInsert(AST insert) throws SemanticException, QueryException {
 		InsertStatement insertStatement = ( InsertStatement ) insert;
 		insertStatement.validate();
 
 		SelectClause selectClause = insertStatement.getSelectClause();
 		Queryable persister = insertStatement.getIntoClause().getQueryable();
 
 		if ( !insertStatement.getIntoClause().isExplicitIdInsertion() ) {
 			// We need to generate ids as part of this bulk insert.
 			//
 			// Note that this is only supported for sequence-style generators and
 			// post-insert-style generators; basically, only in-db generators
 			IdentifierGenerator generator = persister.getIdentifierGenerator();
 			if ( !supportsIdGenWithBulkInsertion( generator ) ) {
 				throw new QueryException( "can only generate ids as part of bulk insert with either sequence or post-insert style generators" );
 			}
 
 			AST idSelectExprNode = null;
 
 			if ( SequenceGenerator.class.isAssignableFrom( generator.getClass() ) ) {
 				String seqName = ( String ) ( ( SequenceGenerator ) generator ).generatorKey();
 				String nextval = sessionFactoryHelper.getFactory().getDialect().getSelectSequenceNextValString( seqName );
 				idSelectExprNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, nextval );
 			}
 			else {
 				//Don't need this, because we should never ever be selecting no columns in an insert ... select...
 				//and because it causes a bug on DB2
 				/*String idInsertString = sessionFactoryHelper.getFactory().getDialect().getIdentityInsertString();
 				if ( idInsertString != null ) {
 					idSelectExprNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, idInsertString );
 				}*/
 			}
 
 			if ( idSelectExprNode != null ) {
 				AST currentFirstSelectExprNode = selectClause.getFirstChild();
 				selectClause.setFirstChild( idSelectExprNode );
 				idSelectExprNode.setNextSibling( currentFirstSelectExprNode );
 
 				insertStatement.getIntoClause().prependIdColumnSpec();
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
-		LOG.debugf( "Derived SELECT clause created." );
+		LOG.debug( "Derived SELECT clause created." );
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
 			QueryException qe = new QueryException( QueryTranslator.ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name );
 			qe.setQueryString( queryTranslatorImpl.getQueryString() );
 			throw qe;
 		}
 		if ( o instanceof Integer ) {
 			return new int[]{( ( Integer ) o ).intValue()};
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
 
 	public static void panic() {
 		throw new QueryException( "TreeWalker: panic" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
index 719e1ddef0..fbf56021b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
@@ -1,600 +1,600 @@
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import antlr.ANTLRException;
 import antlr.RecognitionException;
 import antlr.TokenStreamException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
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
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.Type;
 
 /**
  * A QueryTranslator that uses an Antlr-based parser.
  *
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 public class QueryTranslatorImpl implements FilterTranslator {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryTranslatorImpl.class.getName());
 
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
-			LOG.debugf( "compile() : The query is already compiled, skipping..." );
+			LOG.debug( "compile() : The query is already compiled, skipping..." );
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
 			LOG.trace( "Converted antlr.RecognitionException", e );
 			throw QuerySyntaxException.convert( e, hql );
 		}
 		catch ( ANTLRException e ) {
 			// we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
 			LOG.trace( "Converted antlr.ANTLRException", e );
 			throw new QueryException( e.getMessage(), hql );
 		}
 
 		this.enabledFilters = null; //only needed during compilation phase...
 	}
 
 	private void generate(AST sqlAst) throws QueryException, RecognitionException {
 		if ( sql == null ) {
 			SqlGenerator gen = new SqlGenerator(factory);
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
 
 	private HqlSqlWalker analyze(HqlParser parser, String collectionRole) throws QueryException, RecognitionException {
 		HqlSqlWalker w = new HqlSqlWalker( this, factory, parser, tokenReplacements, collectionRole );
 		AST hqlAst = parser.getAST();
 
 		// Transform the tree.
 		w.statement( hqlAst );
 
 		if ( LOG.isDebugEnabled() ) {
 			ASTPrinter printer = new ASTPrinter( SqlTokenTypes.class );
 			LOG.debug( printer.showAsString( w.getAST(), "--- SQL AST ---" ) );
 		}
 
 		w.getParseErrorHandler().throwQueryException();
 
 		return w;
 	}
 
 	private HqlParser parse(boolean filter) throws TokenStreamException, RecognitionException {
 		// Parse the query string into an HQL AST.
 		HqlParser parser = HqlParser.getInstance( hql );
 		parser.setFilter( filter );
 
 		LOG.debugf( "parse() - HQL: %s", hql );
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
 		if ( LOG.isDebugEnabled() ) {
 			ASTPrinter printer = new ASTPrinter( HqlTokenTypes.class );
 			LOG.debug( printer.showAsString( hqlAst, "--- HQL AST ---" ) );
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
 
 	@Override
 	public Class getDynamicInstantiationResultType() {
 		AggregatedSelectExpression aggregation = queryLoader.getAggregatedSelectExpression();
 		return aggregation == null ? null : aggregation.getAggregationResultType();
 	}
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/AbstractStatementExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/AbstractStatementExecutor.java
index 6b596d3af3..d745bac252 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/AbstractStatementExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/AbstractStatementExecutor.java
@@ -1,310 +1,310 @@
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
 package org.hibernate.hql.internal.ast.exec;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 import java.util.Collections;
 import java.util.List;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.InsertSelect;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 
 /**
  * Implementation of AbstractStatementExecutor.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStatementExecutor implements StatementExecutor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractStatementExecutor.class.getName());
 
 	private final HqlSqlWalker walker;
 	private List idSelectParameterSpecifications = Collections.EMPTY_LIST;
 
     public AbstractStatementExecutor( HqlSqlWalker walker,
                                       CoreMessageLogger log ) {
 		this.walker = walker;
 	}
 
 	protected HqlSqlWalker getWalker() {
 		return walker;
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return walker.getSessionFactoryHelper().getFactory();
 	}
 
 	protected List getIdSelectParameterSpecifications() {
 		return idSelectParameterSpecifications;
 	}
 
 	protected abstract Queryable[] getAffectedQueryables();
 
 	protected String generateIdInsertSelect(Queryable persister, String tableAlias, AST whereClause) {
 		Select select = new Select( getFactory().getDialect() );
 		SelectFragment selectFragment = new SelectFragment()
 				.addColumns( tableAlias, persister.getIdentifierColumnNames(), persister.getIdentifierColumnNames() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 
 		String rootTableName = persister.getTableName();
 		String fromJoinFragment = persister.fromJoinFragment( tableAlias, true, false );
 		String whereJoinFragment = persister.whereJoinFragment( tableAlias, true, false );
 
 		select.setFromClause( rootTableName + ' ' + tableAlias + fromJoinFragment );
 
 		if ( whereJoinFragment == null ) {
 			whereJoinFragment = "";
 		}
 		else {
 			whereJoinFragment = whereJoinFragment.trim();
 			if ( whereJoinFragment.startsWith( "and" ) ) {
 				whereJoinFragment = whereJoinFragment.substring( 4 );
 			}
 		}
 
 		String userWhereClause = "";
 		if ( whereClause.getNumberOfChildren() != 0 ) {
 			// If a where clause was specified in the update/delete query, use it to limit the
 			// returned ids here...
 			try {
 				SqlGenerator sqlGenerator = new SqlGenerator( getFactory() );
 				sqlGenerator.whereClause( whereClause );
 				userWhereClause = sqlGenerator.getSQL().substring( 7 );  // strip the " where "
 				idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
 			}
 			catch ( RecognitionException e ) {
 				throw new HibernateException( "Unable to generate id select for DML operation", e );
 			}
 			if ( whereJoinFragment.length() > 0 ) {
 				whereJoinFragment += " and ";
 			}
 		}
 
 		select.setWhereClause( whereJoinFragment + userWhereClause );
 
 		InsertSelect insert = new InsertSelect( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert-select for " + persister.getEntityName() + " ids" );
 		}
 		insert.setTableName( persister.getTemporaryIdTableName() );
 		insert.setSelect( select );
 		return insert.toStatementString();
 	}
 
 	protected String generateIdSubselect(Queryable persister) {
 		return "select " + StringHelper.join( ", ", persister.getIdentifierColumnNames() ) +
 			        " from " + persister.getTemporaryIdTableName();
 	}
 
 	private static class TemporaryTableCreationWork extends AbstractWork {
 		private final Queryable persister;
 
 		private TemporaryTableCreationWork(Queryable persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public void execute(Connection connection) {
 			try {
 				Statement statement = connection.createStatement();
 				try {
 					statement.executeUpdate( persister.getTemporaryIdTableDDL() );
 					persister.getFactory()
 							.getServiceRegistry()
 							.getService( JdbcServices.class )
 							.getSqlExceptionHelper()
 							.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
 				}
 				finally {
 					try {
 						statement.close();
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 			catch( Exception e ) {
 				LOG.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
 			}
 		}
 	}
 	protected void createTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
 		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
 		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
 		TemporaryTableCreationWork work = new TemporaryTableCreationWork( persister );
 		if ( shouldIsolateTemporaryTableDDL() ) {
 			session.getTransactionCoordinator()
 					.getTransaction()
 					.createIsolationDelegate()
 					.delegateWork( work, getFactory().getSettings().isDataDefinitionInTransactionSupported() );
 		}
 		else {
 			final Connection connection = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getLogicalConnection()
 					.getShareableConnectionProxy();
 			work.execute( connection );
 			session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getLogicalConnection()
 					.afterStatementExecution();
 		}
 	}
 
 	private static SqlExceptionHelper.WarningHandler CREATION_WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport() {
 		public boolean doProcess() {
 			return LOG.isDebugEnabled();
 		}
 
 		public void prepare(SQLWarning warning) {
 			LOG.warningsCreatingTempTable( warning );
 		}
 
 		@Override
 		protected void logWarning(String description, String message) {
-			LOG.debugf( description );
-			LOG.debugf( message );
+			LOG.debug( description );
+			LOG.debug( message );
 		}
 	};
 
 	private static class TemporaryTableDropWork extends AbstractWork {
 		private final Queryable persister;
 		private final SessionImplementor session;
 
 		private TemporaryTableDropWork(Queryable persister, SessionImplementor session) {
 			this.persister = persister;
 			this.session = session;
 		}
 
 		@Override
 		public void execute(Connection connection) {
 			final String command = session.getFactory().getDialect().getDropTemporaryTableString()
 					+ ' ' + persister.getTemporaryIdTableName();
 			try {
 				Statement statement = connection.createStatement();
 				try {
 					statement = connection.createStatement();
 					statement.executeUpdate( command );
 				}
 				finally {
 					try {
 						statement.close();
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 			catch( Exception e ) {
 				LOG.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
 			}
 		}
 	}
 
 	protected void dropTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
 		if ( getFactory().getDialect().dropTemporaryTableAfterUse() ) {
 			TemporaryTableDropWork work = new TemporaryTableDropWork( persister, session );
 			if ( shouldIsolateTemporaryTableDDL() ) {
 				session.getTransactionCoordinator()
 						.getTransaction()
 						.createIsolationDelegate()
 						.delegateWork( work, getFactory().getSettings().isDataDefinitionInTransactionSupported() );
 			}
 			else {
 				final Connection connection = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getLogicalConnection()
 						.getShareableConnectionProxy();
 				work.execute( connection );
 				session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getLogicalConnection()
 						.afterStatementExecution();
 			}
 		}
 		else {
 			// at the very least cleanup the data :)
 			PreparedStatement ps = null;
 			try {
 				final String sql = "delete from " + persister.getTemporaryIdTableName();
 				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
 				ps.executeUpdate();
 			}
 			catch( Throwable t ) {
                 LOG.unableToCleanupTemporaryIdTable(t);
 			}
 			finally {
 				if ( ps != null ) {
 					try {
 						ps.close();
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 		}
 	}
 
 	protected void coordinateSharedCacheCleanup(SessionImplementor session) {
 		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, getAffectedQueryables() );
 
 		if ( session.isEventSource() ) {
 			( ( EventSource ) session ).getActionQueue().addAction( action );
 		}
 		else {
 			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	protected boolean shouldIsolateTemporaryTableDDL() {
 		Boolean dialectVote = getFactory().getDialect().performTemporaryTableDDLInIsolation();
         if (dialectVote != null) return dialectVote.booleanValue();
         return getFactory().getSettings().isDataDefinitionImplicitCommit();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
index 0e0aea1e85..4929b6f205 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
@@ -1,516 +1,516 @@
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
-			LOG.debugf( "createFromElementInSubselect() : creating a new FROM element..." );
+			LOG.debug( "createFromElementInSubselect() : creating a new FROM element..." );
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
 
 	FromElement createCollection(
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
 
 	FromElement createEntityJoin(
 	        String entityClass,
 	        String tableAlias,
 	        JoinSequence joinSequence,
 	        boolean fetchFlag,
 	        boolean inFrom,
 	        EntityType type) throws SemanticException {
 		FromElement elem = createJoin( entityClass, tableAlias, joinSequence, type, false );
 		elem.setFetch( fetchFlag );
 		EntityPersister entityPersister = elem.getEntityPersister();
 		int numberOfTables = entityPersister.getQuerySpaces().length;
 		if ( numberOfTables > 1 && implied && !elem.useFromFragment() ) {
-			LOG.debugf( "createEntityJoin() : Implied multi-table entity join" );
+			LOG.debug( "createEntityJoin() : Implied multi-table entity join" );
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
 
 		return elem;
 	}
 
 	public FromElement createComponentJoin(ComponentType type) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/QueryNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/QueryNode.java
index 180bca35f2..b45398679a 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/QueryNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/QueryNode.java
@@ -1,167 +1,167 @@
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
 import antlr.SemanticException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.Type;
 
 /**
  * Defines a top-level AST node representing an HQL select statement.
  *
  * @author Joshua Davis
  */
 public class QueryNode extends AbstractRestrictableStatement implements SelectExpression {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryNode.class.getName());
 
 	private OrderByClause orderByClause;
 	private int scalarColumnIndex = -1;
 
 	/**
 	 * @see Statement#getStatementType()
 	 */
 	public int getStatementType() {
 		return HqlSqlTokenTypes.QUERY;
 	}
 
 	/**
 	 * @see Statement#needsExecutor()
 	 */
 	public boolean needsExecutor() {
 		return false;
 	}
 
 	@Override
     protected int getWhereClauseParentTokenType() {
 		return SqlTokenTypes.FROM;
 	}
 
 	@Override
     protected CoreMessageLogger getLog() {
         return LOG;
 	}
 
 	/**
 	 * Locate the select clause that is part of this select statement.
 	 * </p>
 	 * Note, that this might return null as derived select clauses (i.e., no
 	 * select clause at the HQL-level) get generated much later than when we
 	 * get created; thus it depends upon lifecycle.
 	 *
 	 * @return Our select clause, or null.
 	 */
 	public final SelectClause getSelectClause() {
 		// Due to the complexity in initializing the SelectClause, do not generate one here.
 		// If it is not found; simply return null...
 		//
 		// Also, do not cache since it gets generated well after we are created.
 		return ( SelectClause ) ASTUtil.findTypeInChildren( this, SqlTokenTypes.SELECT_CLAUSE );
 	}
 
 	public final boolean hasOrderByClause() {
 		OrderByClause orderByClause = locateOrderByClause();
 		return orderByClause != null && orderByClause.getNumberOfChildren() > 0;
 	}
 
 	public final OrderByClause getOrderByClause() {
 		if ( orderByClause == null ) {
 			orderByClause = locateOrderByClause();
 
 			// if there is no order by, make one
 			if ( orderByClause == null ) {
-				LOG.debugf( "getOrderByClause() : Creating a new ORDER BY clause" );
+				LOG.debug( "getOrderByClause() : Creating a new ORDER BY clause" );
 				orderByClause = ( OrderByClause ) ASTUtil.create( getWalker().getASTFactory(), SqlTokenTypes.ORDER, "ORDER" );
 
 				// Find the WHERE; if there is no WHERE, find the FROM...
 				AST prevSibling = ASTUtil.findTypeInChildren( this, SqlTokenTypes.WHERE );
 				if ( prevSibling == null ) {
 					prevSibling = ASTUtil.findTypeInChildren( this, SqlTokenTypes.FROM );
 				}
 
 				// Now, inject the newly built ORDER BY into the tree
 				orderByClause.setNextSibling( prevSibling.getNextSibling() );
 				prevSibling.setNextSibling( orderByClause );
 			}
 		}
 		return orderByClause;
 	}
 
 	private OrderByClause locateOrderByClause() {
 		return ( OrderByClause ) ASTUtil.findTypeInChildren( this, SqlTokenTypes.ORDER );
 	}
 
 
 	private String alias;
 
 	public String getAlias() {
 		return alias;
 	}
 
 	public FromElement getFromElement() {
 		return null;
 	}
 
 	public boolean isConstructor() {
 		return false;
 	}
 
 	public boolean isReturnableEntity() throws SemanticException {
 		return false;
 	}
 
 	public boolean isScalar() throws SemanticException {
 		return true;
 	}
 
 	public void setAlias(String alias) {
 		this.alias = alias;
 	}
 
 	public void setScalarColumn(int i) throws SemanticException {
 		scalarColumnIndex = i;
 		setScalarColumnText( i );
 	}
 
 	public int getScalarColumnIndex() {
 		return scalarColumnIndex;
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		ColumnHelper.generateSingleScalarColumn( this, i );
 	}
 
 	@Override
     public Type getDataType() {
 		return ( (SelectExpression) getSelectClause().getFirstSelectExpression() ).getDataType();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java
index 2f2bae2563..54d4fd42eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java
@@ -1,183 +1,183 @@
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
 package org.hibernate.internal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.NoSuchElementException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.HibernateIterator;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * An implementation of <tt>java.util.Iterator</tt> that is
  * returned by <tt>iterate()</tt> query execution methods.
  * @author Gavin King
  */
 public final class IteratorImpl implements HibernateIterator {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, IteratorImpl.class.getName());
 
 	private ResultSet rs;
 	private final EventSource session;
 	private boolean readOnly;
 	private final Type[] types;
 	private final boolean single;
 	private Object currentResult;
 	private boolean hasNext;
 	private final String[][] names;
 	private PreparedStatement ps;
 	private HolderInstantiator holderInstantiator;
 
 	public IteratorImpl(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        EventSource sess,
 	        boolean readOnly,
 	        Type[] types,
 	        String[][] columnNames,
 	        HolderInstantiator holderInstantiator)
 	throws HibernateException, SQLException {
 
 		this.rs=rs;
 		this.ps=ps;
 		this.session = sess;
 		this.readOnly = readOnly;
 		this.types = types;
 		this.names = columnNames;
 		this.holderInstantiator = holderInstantiator;
 
 		single = types.length==1;
 
 		postNext();
 	}
 
 	public void close() throws JDBCException {
 		if (ps!=null) {
 			try {
-                LOG.debugf("Closing iterator");
+				LOG.debug("Closing iterator");
 				ps.close();
 				ps = null;
 				rs = null;
 				hasNext = false;
 			}
 			catch (SQLException e) {
                 LOG.unableToCloseIterator(e);
 				throw session.getFactory().getSQLExceptionHelper().convert(
 				        e,
 				        "Unable to close iterator"
 					);
 			}
 			finally {
 				try {
 					session.getPersistenceContext().getLoadContexts().cleanup( rs );
 				}
 				catch( Throwable ignore ) {
 					// ignore this error for now
                     LOG.debugf("Exception trying to cleanup load context : %s", ignore.getMessage());
 				}
 			}
 		}
 	}
 
 	private void postNext() throws SQLException {
-        LOG.debugf("Attempting to retrieve next results");
+		LOG.debug("Attempting to retrieve next results");
 		this.hasNext = rs.next();
 		if (!hasNext) {
-            LOG.debugf("Exhausted results");
+			LOG.debug("Exhausted results");
 			close();
-        } else LOG.debugf("Retrieved next results");
+		} else LOG.debug("Retrieved next results");
 	}
 
 	public boolean hasNext() {
 		return hasNext;
 	}
 
 	public Object next() throws HibernateException {
 		if ( !hasNext ) throw new NoSuchElementException("No more results");
 		boolean sessionDefaultReadOnlyOrig = session.isDefaultReadOnly();
 		session.setDefaultReadOnly( readOnly );
 		try {
 			boolean isHolder = holderInstantiator.isRequired();
 
 			LOG.debugf( "Assembling results" );
 			if ( single && !isHolder ) {
 				currentResult = types[0].nullSafeGet( rs, names[0], session, null );
 			}
 			else {
 				Object[] currentResults = new Object[types.length];
 				for (int i=0; i<types.length; i++) {
 					currentResults[i] = types[i].nullSafeGet( rs, names[i], session, null );
 				}
 
 				if (isHolder) {
 					currentResult = holderInstantiator.instantiate(currentResults);
 				}
 				else {
 					currentResult = currentResults;
 				}
 			}
 
 			postNext();
 			LOG.debugf( "Returning current results" );
 			return currentResult;
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not get next iterator result"
 				);
 		}
 		finally {
 			session.setDefaultReadOnly( sessionDefaultReadOnlyOrig );
 		}
 	}
 
 	public void remove() {
 		if (!single) {
 			throw new UnsupportedOperationException("Not a single column hibernate query result set");
 		}
 		if (currentResult==null) {
 			throw new IllegalStateException("Called Iterator.remove() before next()");
 		}
 		if ( !( types[0] instanceof EntityType ) ) {
 			throw new UnsupportedOperationException("Not an entity");
 		}
 
 		session.delete(
 				( (EntityType) types[0] ).getAssociatedEntityName(),
 				currentResult,
 				false,
 		        null
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 59ad4646f2..035a892d4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1788 +1,1788 @@
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
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 
 /**
  * Concrete implementation of the <tt>SessionFactory</tt> interface. Has the following
  * responsibilities
  * <ul>
  * <li>caches configuration settings (immutably)
  * <li>caches "compiled" mappings ie. <tt>EntityPersister</tt>s and
  *     <tt>CollectionPersister</tt>s (immutable)
  * <li>caches "compiled" queries (memory sensitive cache)
  * <li>manages <tt>PreparedStatement</tt>s
  * <li> delegates JDBC <tt>Connection</tt> management to the <tt>ConnectionProvider</tt>
  * <li>factory for instances of <tt>SessionImpl</tt>
  * </ul>
  * This class must appear immutable to clients, even if it does all kinds of caching
  * and pooling under the covers. It is crucial that the class is not only thread
  * safe, but also highly concurrent. Synchronization must be used extremely sparingly.
  *
  * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactoryImplementor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map collectionPersisters;
 	private final transient Map collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient Map<String, NamedQueryDefinition> namedQueries;
 	private final transient Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	private final transient Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient JdbcServices jdbcServices;
 	private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient ConcurrentMap<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient Cache cacheAccess = new CacheImpl();
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 
 	@SuppressWarnings( {"unchecked"} )
 	public SessionFactoryImpl(
 			final Configuration cfg,
 			Mapping mapping,
 			ServiceRegistry serviceRegistry,
 			Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
 			LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
 			public Interceptor getInterceptor() {
 				return cfg.getInterceptor();
 			}
 
 			@Override
 			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 				if ( entityNotFoundDelegate == null ) {
 					if ( cfg.getEntityNotFoundDelegate() != null ) {
 						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 					}
 					else {
 						entityNotFoundDelegate = new EntityNotFoundDelegate() {
 							public void handleEntityNotFound(String entityName, Serializable id) {
 								throw new ObjectNotFoundException( id, entityName );
 							}
 						};
 					}
 				}
 				return entityNotFoundDelegate;
 			}
 		};
 
 		this.settings = settings;
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
         this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
         this.dialect = this.jdbcServices.getDialect();
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 		// Caches
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		// todo : everything above here consider implementing as standard SF service.  specifically: stats, caches, types, function-reg
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		Iterator classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			PersistentClass model = (PersistentClass) classes.next();
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						cfg.getIdentifierGeneratorFactory(),
 						getDialect(),
 				        settings.getDefaultCatalogName(),
 				        settings.getDefaultSchemaName(),
 				        (RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 
 		entityPersisters = new HashMap();
 		Map entityAccessStrategies = new HashMap();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			final PersistentClass model = (PersistentClass) classes.next();
 			model.prepareTemporaryTables( mapping, getDialect() );
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) entityAccessStrategies.get( cacheRegionName );
 			if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 				if ( accessType != null ) {
 					if ( LOG.isTraceEnabled() ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntityName() );
 					}
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model,
 					accessStrategy,
 					this,
 					mapping
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev("Building cache for collection data [{0}]", model.getRole() );
 				}
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry.getService( PersisterFactory.class ).createCollectionPersister(
 					cfg,
 					model,
 					accessStrategy,
 					this
 			) ;
 			collectionPersisters.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		namedQueries = new HashMap<String, NamedQueryDefinition>( cfg.getNamedQueries() );
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>( cfg.getNamedSQLQueries() );
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>( cfg.getSqlResultSetMappings() );
 		imports = new HashMap<String,String>( cfg.getImports() );
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, serviceRegistry.getService( JndiService.class ) );
 
-		LOG.debugf( "Instantiated session factory" );
+		LOG.debug( "Instantiated session factory" );
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(settings, properties, this);
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache(null, updateTimestampsCache, settings, properties);
 			queryCaches = new ConcurrentHashMap<String, QueryCache>();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			Map errors = checkNamedQueries();
 			if ( !errors.isEmpty() ) {
 				Set keys = errors.keySet();
 				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
 				for ( Iterator iterator = keys.iterator() ; iterator.hasNext() ; ) {
 					String queryName = ( String ) iterator.next();
 					HibernateException e = ( HibernateException ) errors.get( queryName );
 					failingQueries.append( queryName );
 					if ( iterator.hasNext() ) failingQueries.append( ", " );
 					LOG.namedQueryError( queryName, e );
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			Iterator fetches = mappingProfile.getFetches().iterator();
 			while ( fetches.hasNext() ) {
 				final org.hibernate.mapping.FetchProfile.Fetch mappingFetch =
 						( org.hibernate.mapping.FetchProfile.Fetch ) fetches.next();
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = ( EntityPersister ) ( entityName == null ? null : entityPersisters.get( entityName ) );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || !associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
 		LOG.debug( "Building session factory" );
 
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		this.properties = createPropertiesFromMap(
 				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
 		// TODO: these should be moved into SessionFactoryOptions
 		this.settings = new SettingsFactory().buildSettings(
 				properties,
 				metadata.getServiceRegistry()
 		);
 
 		this.serviceRegistry =
 				metadata.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 
 		// TODO: get SQL functions from JdbcServices (HHH-6559)
 		//this.sqlFunctionRegistry = new SQLFunctionRegistry( this.jdbcServices.getSqlFunctions() );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( this.dialect, new HashMap<String, SQLFunction>() );
 
 		// TODO: get SQL functions from a new service
 		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		for ( FilterDefinition filterDefinition : metadata.getFilterDefinitions() ) {
 			filters.put( filterDefinition.getFilterName(), filterDefinition );
 		}
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 		// TODO: get RegionFactory from service registry
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
         final IntegratorObserver integratorObserver = new IntegratorObserver();
         this.observer.addObserver(integratorObserver);
         for (Integrator integrator : serviceRegistry.getService(IntegratorService.class).getIntegrators()) {
             integrator.integrate(metadata, this, this.serviceRegistry);
             integratorObserver.integrators.add(integrator);
         }
 
 
 		//Generators:
 
 		identifierGenerators = new HashMap<String,IdentifierGenerator>();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				identifierGenerators.put(
 						entityBinding.getEntity().getName(),
 						entityBinding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator()
 				);
 			}
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		StringBuilder stringBuilder = new StringBuilder();
 		if ( settings.getCacheRegionPrefix() != null) {
 			stringBuilder
 					.append( settings.getCacheRegionPrefix() )
 					.append( '.' );
 		}
 		final String cacheRegionPrefix = stringBuilder.toString();
 
 		entityPersisters = new HashMap();
 		Map<String, RegionAccessStrategy> entityAccessStrategies = new HashMap<String, RegionAccessStrategy>();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		for ( EntityBinding model : metadata.getEntityBindings() ) {
 			// TODO: should temp table prep happen when metadata is being built?
 			//model.prepareTemporaryTables( metadata, getDialect() );
 			// cache region is defined by the root-class in the hierarchy...
 			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
 			EntityRegionAccessStrategy accessStrategy = null;
 			if ( settings.isSecondLevelCacheEnabled() &&
 					rootEntityBinding.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getHierarchyDetails().getCaching().getRegion();
 				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getHierarchyDetails().getCaching().getAccessType();
 					if ( LOG.isTraceEnabled() ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntity().getName() );
 					}
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion(
 							cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 					);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model, accessStrategy, this, metadata
 			);
 			entityPersisters.put( model.getEntity().getName(), cp );
 			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap();
 		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a AbstractPluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"AbstractPluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			final String cacheRegionName = cacheRegionPrefix + model.getCaching().getRegion();
 			final AccessType accessType = model.getCaching().getAccessType();
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev( "Building cache for collection data [{0}]", model.getAttribute().getRole() );
 				}
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion(
 						cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry
 					.getService( PersisterFactory.class )
 					.createCollectionPersister( metadata, model, accessStrategy, this );
 			collectionPersisters.put( model.getAttribute().getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		for ( NamedQueryDefinition namedQueryDefinition :  metadata.getNamedQueryDefinitions() ) {
 			namedQueries.put( namedQueryDefinition.getName(), namedQueryDefinition );
 		}
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>();
 		for ( NamedSQLQueryDefinition namedNativeQueryDefinition: metadata.getNamedNativeQueryDefinitions() ) {
 			namedSqlQueries.put( namedNativeQueryDefinition.getName(), namedNativeQueryDefinition );
 		}
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 		for( ResultSetMappingDefinition resultSetMappingDefinition : metadata.getResultSetMappingDefinitions() ) {
 			sqlResultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 		}
 		imports = new HashMap<String,String>();
 		for ( Map.Entry<String,String> importEntry : metadata.getImports() ) {
 			imports.put( importEntry.getKey(), importEntry.getValue() );
 		}
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, serviceRegistry.getService( JndiService.class ) );
 
-		LOG.debugf("Instantiated session factory");
+		LOG.debug("Instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		/*
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( metadata ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( metadata ).validate();
 		}
 		*/
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache( settings, properties, this );
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache( null, updateTimestampsCache, settings, properties );
 			queryCaches = new ConcurrentHashMap<String, QueryCache>();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			Map errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				Set keys = errors.keySet();
 				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
 				for ( Iterator<String> iterator = keys.iterator() ; iterator.hasNext() ; ) {
 					String queryName = iterator.next();
 					HibernateException e = ( HibernateException ) errors.get( queryName );
 					failingQueries.append( queryName );
                     if ( iterator.hasNext() ) failingQueries.append( ", " );
 					LOG.namedQueryError( queryName, e );
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.metamodel.binding.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.metamodel.binding.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = ( EntityPersister ) ( entityName == null ? null : entityPersisters.get( entityName ) );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || ! associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
 	}
 
 	public Session openSession() throws HibernateException {
 		return withOptions().openSession();
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return withOptions()
 				.autoClose( false )
 				.flushBeforeCompletion( false )
 				.connectionReleaseMode( ConnectionReleaseMode.AFTER_STATEMENT )
 				.openSession();
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
 	public TransactionEnvironment getTransactionEnvironment() {
 		return transactionEnvironment;
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	private void registerEntityNameResolvers(EntityPersister persister) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map checkNamedQueries() throws HibernateException {
 		Map errors = new HashMap();
 
 		// Check named HQL queries
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named HQL queries", namedQueries.size() );
 		}
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named query: %s", queryName );
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, CollectionHelper.EMPTY_MAP );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named SQL queries", namedSqlQueries.size() );
 		}
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named SQL query: %s", queryName );
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( qd.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = sqlResultSetMappings.get( qd.getResultSetRef() );
 					if ( definition == null ) {
 						throw new MappingException( "Unable to find resultset-ref definition: " + qd.getResultSetRef() );
 					}
 					spec = new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        definition.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				else {
 					spec =  new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        qd.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				queryPlanCache.getNativeSQLQueryPlan( spec );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 
 		return errors;
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = (EntityPersister) entityPersisters.get(entityName);
 		if (result==null) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = (CollectionPersister) collectionPersisters.get(role);
 		if (result==null) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	@Override
 	public Reference getReference() {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
 		LOG.trace( "Resolving serialized SessionFactory" );
 		// look for the instance by uuid
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( result == null ) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( result == null ) {
 				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 			}
-			LOG.debugf( "Resolved SessionFactory by name" );
+			LOG.debug( "Resolved SessionFactory by name" );
 		}
 		else {
-			LOG.debugf( "Resolved SessionFactory by UUID" );
+			LOG.debug( "Resolved SessionFactory by UUID" );
 		}
 		return result;
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueries.get(queryName);
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedSqlQueries.get(queryName);
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return sqlResultSetMappings.get(resultSetName);
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing" );
 		in.defaultReadObject();
 		LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		LOG.debugf( "Serializing: %s", uuid );
 		out.defaultWriteObject();
 		LOG.trace( "Serialized" );
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return (CollectionMetadata) collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get(entityName);
 	}
 
 	/**
      * @param className
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList results = new ArrayList();
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			//test this entity to see if we must query it
 			EntityPersister testPersister = (EntityPersister) iter.next();
 			if ( testPersister instanceof Queryable ) {
 				Queryable testQueryable = (Queryable) testPersister;
 				String testClassName = testQueryable.getEntityName();
 				boolean isMappedClass = className.equals(testClassName);
 				if ( testQueryable.isExplicitPolymorphism() ) {
 					if ( isMappedClass ) {
 						return new String[] {className}; //NOTE EARLY EXIT
 					}
 				}
 				else {
 					if (isMappedClass) {
 						results.add(testClassName);
 					}
 					else {
 						final Class mappedClass = testQueryable.getMappedClass();
 						if ( mappedClass!=null && clazz.isAssignableFrom( mappedClass ) ) {
 							final boolean assignableSuperclass;
 							if ( testQueryable.isInherited() ) {
 								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass();
 								assignableSuperclass = clazz.isAssignableFrom(mappedSuperclass);
 							}
 							else {
 								assignableSuperclass = false;
 							}
 							if ( !assignableSuperclass ) {
 								results.add( testClassName );
 							}
 						}
 					}
 				}
 			}
 		}
 		return (String[]) results.toArray( new String[ results.size() ] );
 	}
 
 	public String getImportedClassName(String className) {
 		String result = imports.get(className);
 		if (result==null) {
 			try {
 				ReflectHelper.classForName( className );
 				return className;
 			}
 			catch (ClassNotFoundException cnfe) {
 				return null;
 			}
 		}
 		else {
 			return result;
 		}
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return classMetadata;
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return collectionMetadata;
 	}
 
 	public Type getReferencedPropertyType(String className, String propertyName)
 		throws MappingException {
 		return getEntityPersister( className ).getPropertyType( propertyName );
 	}
 
 	public ConnectionProvider getConnectionProvider() {
 		return jdbcServices.getConnectionProvider();
 	}
 
 	/**
 	 * Closes the session factory, releasing all held resources.
 	 *
 	 * <ol>
 	 * <li>cleans up used cache regions and "stops" the cache provider.
 	 * <li>close the JDBC connection
 	 * <li>remove the JNDI binding
 	 * </ol>
 	 *
 	 * Note: Be aware that the sessionFactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			EntityPersister p = (EntityPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = (CollectionPersister) iter.next();
 			if ( p.hasCache() ) {
 				p.getCacheAccessStrategy().getRegion().destroy();
 			}
 		}
 
 		if ( settings.isQueryCacheEnabled() )  {
 			queryCache.destroy();
 
 			iter = queryCaches.values().iterator();
 			while ( iter.hasNext() ) {
 				QueryCache cache = (QueryCache) iter.next();
 				cache.destroy();
 			}
 			updateTimestampsCache.destroy();
 		}
 
 		settings.getRegionFactory().stop();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid, name, serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
 	}
 
 	private class CacheImpl implements Cache {
 		public boolean containsEntity(Class entityClass, Serializable identifier) {
 			return containsEntity( entityClass.getName(), identifier );
 		}
 
 		public boolean containsEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( identifier, p ) );
 		}
 
 		public void evictEntity(Class entityClass, Serializable identifier) {
 			evictEntity( entityClass.getName(), identifier );
 		}
 
 		public void evictEntity(String entityName, Serializable identifier) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s",
 							MessageHelper.infoString( p, identifier, SessionFactoryImpl.this ) );
 				}
 				p.getCacheAccessStrategy().evict( buildCacheKey( identifier, p ) );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable identifier, EntityPersister p) {
 			return new CacheKey(
 					identifier,
 					p.getIdentifierType(),
 					p.getRootEntityName(),
 					null, 						// have to assume non tenancy
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictEntityRegion(Class entityClass) {
 			evictEntityRegion( entityClass.getName() );
 		}
 
 		public void evictEntityRegion(String entityName) {
 			EntityPersister p = getEntityPersister( entityName );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s", p.getEntityName() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictEntityRegions() {
 			Iterator entityNames = entityPersisters.keySet().iterator();
 			while ( entityNames.hasNext() ) {
 				evictEntityRegion( ( String ) entityNames.next() );
 			}
 		}
 
 		public boolean containsCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			return p.hasCache() &&
 					p.getCacheAccessStrategy().getRegion().contains( buildCacheKey( ownerIdentifier, p ) );
 		}
 
 		public void evictCollection(String role, Serializable ownerIdentifier) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s",
 							MessageHelper.collectionInfoString( p, ownerIdentifier, SessionFactoryImpl.this ) );
 				}
 				CacheKey cacheKey = buildCacheKey( ownerIdentifier, p );
 				p.getCacheAccessStrategy().evict( cacheKey );
 			}
 		}
 
 		private CacheKey buildCacheKey(Serializable ownerIdentifier, CollectionPersister p) {
 			return new CacheKey(
 					ownerIdentifier,
 					p.getKeyType(),
 					p.getRole(),
 					null,						// have to assume non tenancy
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictCollectionRegion(String role) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s", p.getRole() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictCollectionRegions() {
 			Iterator collectionRoles = collectionPersisters.keySet().iterator();
 			while ( collectionRoles.hasNext() ) {
 				evictCollectionRegion( ( String ) collectionRoles.next() );
 			}
 		}
 
 		public boolean containsQuery(String regionName) {
 			return queryCaches.containsKey( regionName );
 		}
 
 		public void evictDefaultQueryRegion() {
 			if ( settings.isQueryCacheEnabled() ) {
 				queryCache.clear();
 			}
 		}
 
 		public void evictQueryRegion(String regionName) {
             if (regionName == null) throw new NullPointerException(
                                                                    "Region-name cannot be null (use Cache#evictDefaultQueryRegion to evict the default query cache)");
             if (settings.isQueryCacheEnabled()) {
                 QueryCache namedQueryCache = queryCaches.get(regionName);
                 // TODO : cleanup entries in queryCaches + allCacheRegions ?
                 if (namedQueryCache != null) namedQueryCache.clear();
 			}
 		}
 
 		public void evictQueryRegions() {
 			if ( queryCaches != null ) {
 				for ( QueryCache queryCache : queryCaches.values() ) {
 					queryCache.clear();
 					// TODO : cleanup entries in queryCaches + allCacheRegions ?
 				}
 			}
 		}
 	}
 
 	public Cache getCache() {
 		return cacheAccess;
 	}
 
 	public void evictEntity(String entityName, Serializable id) throws HibernateException {
 		getCache().evictEntity( entityName, id );
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getCache().evictEntityRegion( entityName );
 	}
 
 	public void evict(Class persistentClass, Serializable id) throws HibernateException {
 		getCache().evictEntity( persistentClass, id );
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getCache().evictEntityRegion( persistentClass );
 	}
 
 	public void evictCollection(String roleName, Serializable id) throws HibernateException {
 		getCache().evictCollection( roleName, id );
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getCache().evictCollectionRegion( roleName );
 	}
 
 	public void evictQueries() throws HibernateException {
 		if ( settings.isQueryCacheEnabled() ) {
 			queryCache.clear();
 		}
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return updateTimestampsCache;
 	}
 
 	public QueryCache getQueryCache() {
 		return queryCache;
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			return getQueryCache();
 		}
 
 		if ( !settings.isQueryCacheEnabled() ) {
 			return null;
 		}
 
 		QueryCache currentQueryCache = queryCaches.get( regionName );
 		if ( currentQueryCache == null ) {
 			synchronized ( allCacheRegions ) {
 				currentQueryCache = queryCaches.get( regionName );
 				if ( currentQueryCache == null ) {
 					currentQueryCache = settings.getQueryCacheFactory()
 							.getQueryCache( regionName, updateTimestampsCache, settings, properties );
 					queryCaches.put( regionName, currentQueryCache );
 					allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 				} else {
 					return currentQueryCache;
 				}
 			}
 		}
 		return currentQueryCache;
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	public Map getAllSecondLevelCacheRegions() {
 		return new HashMap( allCacheRegions );
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return getStatisticsImplementor();
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return serviceRegistry.getService( StatisticsImplementor.class );
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = filters.get( filterName );
 		if ( def == null ) {
 			throw new HibernateException( "No such filter configured [" + filterName + "]" );
 		}
 		return def;
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return fetchProfiles.containsKey( name );
 	}
 
 	public Set getDefinedFilterNames() {
 		return filters.keySet();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return identifierGenerators.get(rootEntityName);
 	}
 
 	private org.hibernate.engine.transaction.spi.TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( org.hibernate.engine.transaction.spi.TransactionFactory.class );
 	}
 
 	private boolean canAccessTransactionManager() {
 		try {
 			return serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager() != null;
 		}
 		catch (Exception e) {
 			return false;
 		}
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatibility
 		if ( impl == null ) {
 			if ( canAccessTransactionManager() ) {
 				impl = "jta";
 			}
 			else {
 				return null;
 			}
 		}
 
 		if ( "jta".equals( impl ) ) {
 			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
 				LOG.autoFlushWillNotWork();
 			}
 			return new JTASessionContext( this );
 		}
 		else if ( "thread".equals( impl ) ) {
 			return new ThreadLocalSessionContext( this );
 		}
 		else if ( "managed".equals( impl ) ) {
 			return new ManagedSessionContext( this );
 		}
 		else {
 			try {
 				Class implClass = ReflectHelper.classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
 				LOG.unableToConstructCurrentSessionContext( impl, t );
 				return null;
 			}
 		}
 	}
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return sessionFactoryOptions.getEntityNotFoundDelegate();
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return ( FetchProfile ) fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	/**
 	 * Custom serialization hook used during Session serialization.
 	 *
 	 * @param oos The stream to which to write the factory
 	 * @throws IOException Indicates problems writing out the serial data stream
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeUTF( uuid );
 		oos.writeBoolean( name != null );
 		if ( name != null ) {
 			oos.writeUTF( name );
 		}
 	}
 
 	/**
 	 * Custom deserialization hook used during Session deserialization.
 	 *
 	 * @param ois The stream from which to "read" the factory
 	 * @return The deserialized factory
 	 * @throws IOException indicates problems reading back serial data stream
 	 * @throws ClassNotFoundException indicates problems reading back serial data stream
 	 */
 	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( result == null ) {
 			LOG.tracev( "Could not locate session factory by uuid [{0}] during session deserialization; trying name", uuid );
 			if ( isNamed ) {
 				result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			}
 			if ( result == null ) {
 				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
 			}
 		}
 		return ( SessionFactoryImpl ) result;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 		}
 
 		@Override
 		public SessionBuilder interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			this.flushBeforeCompletion = flushBeforeCompletion;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 933a7a4a20..a0ae6315bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,2065 +1,2065 @@
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
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
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
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.query.spi.FilterQueryPlan;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.NonFlushedChanges;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEvent;
 import org.hibernate.event.spi.AutoFlushEventListener;
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
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
 import org.hibernate.internal.util.collections.CollectionHelper;
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
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.stat.internal.SessionStatisticsImpl;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 
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
  */
 public final class SessionImpl extends AbstractSessionImpl implements EventSource {
 
 	// todo : need to find a clean way to handle the "event source" role
 	// a separate class responsible for generating/dispatching events just duplicates most of the Session methods...
 	// passing around separate interceptor, factory, actionQueue, and persistentContext is not manageable...
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionImpl.class.getName());
 
 	private transient long timestamp;
 
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
 
 	private transient int dontFlushFromFind = 0;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
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
 		this.interceptor = interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.connectionReleaseMode = connectionReleaseMode;
 		this.autoJoinTransactions = autoJoinTransactions;
 
 		if ( transactionCoordinator == null ) {
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
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
 
 		LOG.debugf( "Opened session at timestamp: %s", timestamp );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
 	}
 
 	public void clear() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.clear();
 		actionQueue.clear();
 	}
 
 	public long getTimestamp() {
 		checkTransactionSynchStatus();
 		return timestamp;
 	}
 
 	public Connection close() throws HibernateException {
 		LOG.trace( "Closing session" );
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed" );
 		}
 
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().closeSession();
 		}
 
 		try {
 			return transactionCoordinator.close();
 		}
 		finally {
 			setClosed();
 			cleanup();
 		}
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return autoJoinTransactions;
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	public boolean isOpen() {
 		checkTransactionSynchStatus();
 		return !isClosed();
 	}
 
 	public boolean isFlushModeNever() {
 		return FlushMode.isManualFlushMode( getFlushMode() );
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
 	public void managedFlush() {
 		if ( isClosed() ) {
 			LOG.trace( "Skipping auto-flush due to session closed" );
 			return;
 		}
 		LOG.trace( "Automatically flushing session" );
 		flush();
 	}
 
 	/**
 	 * Return changes to this session and its child sessions that have not been flushed yet.
 	 * <p/>
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new NonFlushedChangesImpl( this );
 	}
 
 	/**
 	 * Apply non-flushed changes from a different session to this session. It is assumed
 	 * that this SessionImpl is "clean" (e.g., has no non-flushed changes, no cached entities,
 	 * no cached collections, no queued actions). The specified NonFlushedChanges object cannot
 	 * be bound to any session.
 	 * <p/>
 	 * @param nonFlushedChanges the non-flushed changes
 	 */
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		// todo : why aren't these just part of the NonFlushedChanges API ?
 		replacePersistenceContext( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getPersistenceContext() );
 		replaceActionQueue( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getActionQueue() );
 	}
 
 	private void replacePersistenceContext(StatefulPersistenceContext persistenceContextNew) {
 		if ( persistenceContextNew.getSession() != null ) {
 			throw new IllegalStateException( "new persistence context is already connected to a session " );
 		}
 		persistenceContext.clear();
 		ObjectInputStream ois = null;
 		try {
 			ois = new ObjectInputStream( new ByteArrayInputStream( serializePersistenceContext( persistenceContextNew ) ) );
 			this.persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not deserialize the persistence context",  ex );
 		}
 		catch (ClassNotFoundException ex) {
 			throw new SerializationException( "could not deserialize the persistence context", ex );
 		}
 		finally {
 			try {
 				if (ois != null) ois.close();
 			}
 			catch (IOException ex) {}
 		}
 	}
 
 	private static byte[] serializePersistenceContext(StatefulPersistenceContext pc) {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream( 512 );
 		ObjectOutputStream oos = null;
 		try {
 			oos = new ObjectOutputStream( baos );
 			( pc ).serialize( oos );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not serialize persistence context", ex );
 		}
 		finally {
 			if ( oos != null ) {
 				try {
 					oos.close();
 				}
 				catch( IOException ex ) {
 					//ignore
 				}
 			}
 		}
 		return baos.toByteArray();
 	}
 
 	private void replaceActionQueue(ActionQueue actionQueueNew) {
 		if ( actionQueue.hasAnyQueuedActions() ) {
 			throw new IllegalStateException( "cannot replace an ActionQueue with queued actions " );
 		}
 		actionQueue.clear();
 		ObjectInputStream ois = null;
 		try {
 			ois = new ObjectInputStream( new ByteArrayInputStream( serializeActionQueue( actionQueueNew ) ) );
 			actionQueue = ActionQueue.deserialize( ois, this );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not deserialize the action queue",  ex );
 		}
 		catch (ClassNotFoundException ex) {
 			throw new SerializationException( "could not deserialize the action queue", ex );
 		}
 		finally {
 			try {
 				if (ois != null) ois.close();
 			}
 			catch (IOException ex) {}
 		}
 	}
 
 	private static byte[] serializeActionQueue(ActionQueue actionQueue) {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream( 512 );
 		ObjectOutputStream oos = null;
 		try {
 			oos = new ObjectOutputStream( baos );
 			actionQueue.serialize( oos );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not serialize action queue", ex );
 		}
 		finally {
 			if ( oos != null ) {
 				try {
 					oos.close();
 				}
 				catch( IOException ex ) {
 					//ignore
 				}
 			}
 		}
 		return baos.toByteArray();
 	}
 
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 	public void managedClose() {
 		LOG.trace( "Automatically closing session" );
 		close();
 	}
 
 	public Connection connection() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getDistinctConnectionProxy();
 	}
 
 	public boolean isConnected() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isOpen();
 	}
 
 	public boolean isTransactionInProgress() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.isTransactionInProgress();
 	}
 
 	@Override
 	public Connection disconnect() throws HibernateException {
 		errorIfClosed();
-		LOG.debugf( "Disconnecting session" );
+		LOG.debug( "Disconnecting session" );
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
-		LOG.debugf( "Reconnecting session" );
+		LOG.debug( "Reconnecting session" );
 		checkTransactionSynchStatus();
 		transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualReconnect( conn );
 	}
 
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
 		if ( hibernateTransaction != null ) {
 			try {
 				interceptor.afterTransactionCompletion( hibernateTransaction );
 			}
 			catch (Throwable t) {
 				LOG.exceptionInAfterTransactionCompletionInterceptor( t );
 			}
 		}
 		if ( autoClear ) {
 			clear();
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
 
 	/**
 	 * clear all the internal collections, just
 	 * to help the garbage collector, does not
 	 * clear anything that is needed during the
 	 * afterTransactionCompletion() phase
 	 */
 	private void cleanup() {
 		persistenceContext.clear();
 	}
 
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
 
 
 	// saveOrUpdate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void saveOrUpdate(Object object) throws HibernateException {
 		saveOrUpdate( null, object );
 	}
 
 	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
 		fireSaveOrUpdate( new SaveOrUpdateEvent( entityName, obj, this ) );
 	}
 
 	private void fireSaveOrUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE_UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 	}
 
 	private <T> Iterable<T> listeners(EventType<T> type) {
 		return eventListenerGroup( type ).listeners();
 	}
 
 	private <T> EventListenerGroup<T> eventListenerGroup(EventType<T> type) {
 		return factory.getServiceRegistry().getService( EventListenerRegistry.class ).getEventListenerGroup( type );
 	}
 
 
 	// save() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Serializable save(Object obj) throws HibernateException {
 		return save( null, obj );
 	}
 
 	public Serializable save(String entityName, Object object) throws HibernateException {
 		return fireSave( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private Serializable fireSave(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		return event.getResultId();
 	}
 
 
 	// update() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void update(Object obj) throws HibernateException {
 		update(null, obj);
 	}
 
 	public void update(String entityName, Object object) throws HibernateException {
 		fireUpdate( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private void fireUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 	}
 
 
 	// lock() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( entityName, object, lockMode, this ) );
 	}
 
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return new LockRequestImpl(lockOptions);
 	}
 
 	public void lock(Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent(object, lockMode, this) );
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
 	}
 
 
 	// persist() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persist(String entityName, Object object) throws HibernateException {
 		firePersist( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persist(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	public void persist(String entityName, Object object, Map copiedAlready)
 	throws HibernateException {
 		firePersist( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersist(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 	}
 
 	private void firePersist(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event );
 		}
 	}
 
 
 	// persistOnFlush() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persistOnFlush(String entityName, Object object)
 			throws HibernateException {
 		firePersistOnFlush( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persistOnFlush(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
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
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
 		}
 	}
 
 
 	// merge() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object merge(String entityName, Object object) throws HibernateException {
 		return fireMerge( new MergeEvent( entityName, object, this ) );
 	}
 
 	public Object merge(Object object) throws HibernateException {
 		return merge( null, object );
 	}
 
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		fireMerge( copiedAlready, new MergeEvent( entityName, object, this ) );
 	}
 
 	private Object fireMerge(MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event );
 		}
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event, copiedAlready );
 		}
 	}
 
 
 	// delete() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Delete a persistent object
 	 */
 	public void delete(Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( object, this ) );
 	}
 
 	/**
 	 * Delete a persistent object (by explicit entity name)
 	 */
 	public void delete(String entityName, Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, this ) );
 	}
 
 	/**
 	 * Delete a persistent object
 	 */
 	public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled, Set transientEntities) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, isCascadeDeleteEnabled, this ), transientEntities );
 	}
 
 	private void fireDelete(DeleteEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event );
 		}
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event, transientEntities );
 		}
 	}
 
 
 	// load()/get() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void load(Object object, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, object, this);
 		fireLoad( event, LoadEventListener.RELOAD );
 	}
 
 	public Object load(Class entityClass, Serializable id) throws HibernateException {
 		return load( entityClass.getName(), id );
 	}
 
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, false, this);
 		boolean success = false;
 		try {
 			fireLoad( event, LoadEventListener.LOAD );
 			if ( event.getResult() == null ) {
 				getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityName, id );
 			}
 			success = true;
 			return event.getResult();
 		}
 		finally {
 			afterOperation(success);
 		}
 	}
 
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
 		return get( entityClass.getName(), id );
 	}
 
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, false, this);
 		boolean success = false;
 		try {
 			fireLoad(event, LoadEventListener.GET);
 			success = true;
 			return event.getResult();
 		}
 		finally {
 			afterOperation(success);
 		}
 	}
 
 	/**
 	 * Load the data for the object with the specified id into a newly created object.
 	 * This is only called when lazily initializing a proxy.
 	 * Do NOT return a proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			EntityPersister persister = getFactory().getEntityPersister(entityName);
 			LOG.debugf( "Initializing proxy: %s", MessageHelper.infoString( persister, id, getFactory() ) );
 		}
 
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, LoadEventListener.IMMEDIATE_LOAD);
 		return event.getResult();
 	}
 
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		// todo : remove
 		LoadEventListener.LoadType type = nullable
 				? LoadEventListener.INTERNAL_LOAD_NULLABLE
 				: eager
 						? LoadEventListener.INTERNAL_LOAD_EAGER
 						: LoadEventListener.INTERNAL_LOAD_LAZY;
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, type);
 		if ( !nullable ) {
 			UnresolvableObjectException.throwIfNull( event.getResult(), id, entityName );
 		}
 		return event.getResult();
 	}
 
 	public Object load(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return load( entityClass.getName(), id, lockMode );
 	}
 
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return load( entityClass.getName(), id, lockOptions );
 	}
 
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
 		fireLoad( event, LoadEventListener.LOAD );
 		return event.getResult();
 	}
 
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockOptions, this);
 		fireLoad( event, LoadEventListener.LOAD );
 		return event.getResult();
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return get( entityClass.getName(), id, lockOptions );
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
 	   	fireLoad(event, LoadEventListener.GET);
 		return event.getResult();
 	}
 
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockOptions, this);
 	   	fireLoad( event, LoadEventListener.GET );
 		return event.getResult();
 	}
 
 	private void fireLoad(LoadEvent event, LoadType loadType) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
 		}
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void refresh(Object object) throws HibernateException {
 		refresh( null, object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, this ) );
 	}
 
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, lockMode, this) );
 	}
 
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
 		refresh( null, object, lockOptions );
 	}
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, lockOptions, this ) );
 	}
 
 	public void refresh(Object object, Map refreshedAlready) throws HibernateException {
 		fireRefresh( refreshedAlready, new RefreshEvent( object, this ) );
 	}
 
 	private void fireRefresh(RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event );
 		}
 	}
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event, refreshedAlready );
 		}
 	}
 
 
 	// replicate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
 		fireReplicate( new ReplicateEvent( obj, replicationMode, this ) );
 	}
 
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
 	}
 
 
 	// evict() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * remove any hard references to the entity that are held by the infrastructure
 	 * (references held by application or other persistant instances are okay)
 	 */
 	public void evict(Object object) throws HibernateException {
 		fireEvict( new EvictEvent( object, this ) );
 	}
 
 	private void fireEvict(EvictEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( EvictEventListener listener : listeners( EventType.EVICT ) ) {
 			listener.onEvict( event );
 		}
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
 
 	public boolean isDirty() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		LOG.debugf( "Checking session dirtiness" );
+		LOG.debug( "Checking session dirtiness" );
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
-			LOG.debugf( "Session dirty (scheduled updates and insertions)" );
+			LOG.debug( "Session dirty (scheduled updates and insertions)" );
 			return true;
 		}
 		DirtyCheckEvent event = new DirtyCheckEvent( this );
 		for ( DirtyCheckEventListener listener : listeners( EventType.DIRTY_CHECK ) ) {
 			listener.onDirtyCheck( event );
 		}
 		return event.isDirty();
 	}
 
 	public void flush() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new HibernateException("Flush during cascade is dangerous");
 		}
 		for ( FlushEventListener listener : listeners( EventType.FLUSH ) ) {
 			listener.onFlush( new FlushEvent( this ) );
 		}
 	}
 
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
 
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		List results = CollectionHelper.EMPTY_LIST;
 		boolean success = false;
 
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
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
 		}
 		return result;
 	}
 
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
         }
         return result;
     }
 
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
 			dontFlushFromFind--;
 		}
 	}
 
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
 			dontFlushFromFind--;
 		}
 	}
 
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
 		return filter;
 	}
 
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.getNamedQuery( queryName );
 	}
 
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( factory.getEntityPersister( entityName ), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Object result = interceptor.instantiate( persister.getEntityName(), persister.getEntityMetamodel().getEntityMode(), id );
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		return result;
 	}
 
 	public void setFlushMode(FlushMode flushMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting flush mode to: {0}", flushMode );
 		this.flushMode = flushMode;
 	}
 
 	public FlushMode getFlushMode() {
 		checkTransactionSynchStatus();
 		return flushMode;
 	}
 
 	public CacheMode getCacheMode() {
 		checkTransactionSynchStatus();
 		return cacheMode;
 	}
 
 	public void setCacheMode(CacheMode cacheMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting cache mode to: {0}", cacheMode );
 		this.cacheMode= cacheMode;
 	}
 
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getTransaction();
 	}
 
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
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
 
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, false );
 		List results = CollectionHelper.EMPTY_LIST;
 
 		boolean success = false;
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		return plan.performIterate( queryParameters, this );
 	}
 
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteria.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteria,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public List list(CriteriaImpl criteria) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteria.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteria,
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
 		}
 
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
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
 		return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 	}
 
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createQuery( queryString );
 	}
 
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createSQLQuery( sql );
 	}
 
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
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
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
 			afterOperation(success);
 		}
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		checkTransactionSynchStatus();
 		return factory;
 	}
 
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		InitializeCollectionEvent event = new InitializeCollectionEvent( collection, this );
 		for ( InitializeCollectionEventListener listener : listeners( EventType.INIT_COLLECTION ) ) {
 			listener.onInitializeCollection( event );
 		}
 	}
 
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
 
 	public String guessEntityName(Object object) throws HibernateException {
 		errorIfClosed();
 		return entityNameResolver.resolveEntityName( object );
 	}
 
 	public void cancelQuery() throws HibernateException {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().cancelLastQuery();
 	}
 
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
 	public String toString() {
 		StringBuffer buf = new StringBuffer(500)
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
 
 	public ActionQueue getActionQueue() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 
 	public PersistenceContext getPersistenceContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl(this);
 	}
 
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
 	public boolean isReadOnly(Object entityOrProxy) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );
 	}
 
 	public void setReadOnly(Object entity, boolean readOnly) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly(entity, readOnly);
 	}
 
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
 
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		errorIfClosed();
 		return transactionCoordinator;
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter enableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void disableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object getFilterParameterValue(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterValue( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type getFilterParameterType(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterType( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Map getEnabledFilters() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilters();
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String getFetchProfile() {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getInternalFetchProfile();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setFetchProfile(String fetchProfile) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.setInternalFetchProfile( fetchProfile );
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 
 	private void checkTransactionSynchStatus() {
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
 		if ( ! transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isReadyForSerialization() ) {
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
 
 		transactionCoordinator.serialize( oos );
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
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
 			return session.getFactory().getJdbcServices().getLobCreator( session );
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
 			return connection(
 					session.transactionCoordinator
 							.getJdbcCoordinator()
 							.getLogicalConnection()
 							.getDistinctConnectionProxy()
 			);
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
 
 		@Override
 		public SharedSessionBuilder transactionContext() {
 			this.shareTransactionContext = true;
 			return this;
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java b/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java
index 229e78dc2c..73a790c5d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/EntityPrinter.java
@@ -1,128 +1,128 @@
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
 package org.hibernate.internal.util;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Renders entities and query parameters to a nicely readable string.
  * @author Gavin King
  */
 public final class EntityPrinter {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityPrinter.class.getName());
 
     private SessionFactoryImplementor factory;
 
 	/**
 	 * Renders an entity to a string.
 	 *
 	 * @param entityName the entity name
 	 * @param entity an actual entity object, not a proxy!
 	 * @return the entity rendered to a string
 	 */
 	public String toString(String entityName, Object entity) throws HibernateException {
 		EntityPersister entityPersister = factory.getEntityPersister( entityName );
 
 		if ( entityPersister == null ) {
 			return entity.getClass().getName();
 		}
 
 		Map<String,String> result = new HashMap<String,String>();
 
 		if ( entityPersister.hasIdentifierProperty() ) {
 			result.put(
 				entityPersister.getIdentifierPropertyName(),
 				entityPersister.getIdentifierType().toLoggableString( entityPersister.getIdentifier( entity ), factory )
 			);
 		}
 
 		Type[] types = entityPersister.getPropertyTypes();
 		String[] names = entityPersister.getPropertyNames();
 		Object[] values = entityPersister.getPropertyValues( entity );
 		for ( int i=0; i<types.length; i++ ) {
 			if ( !names[i].startsWith("_") ) {
 				String strValue = values[i]==LazyPropertyInitializer.UNFETCHED_PROPERTY ?
 					values[i].toString() :
 					types[i].toLoggableString( values[i], factory );
 				result.put( names[i], strValue );
 			}
 		}
 		return entityName + result.toString();
 	}
 
 	public String toString(Type[] types, Object[] values) throws HibernateException {
 		StringBuilder buffer = new StringBuilder();
 		for ( int i=0; i<types.length; i++ ) {
 			if ( types[i]!=null ) {
 				buffer.append( types[i].toLoggableString( values[i], factory ) ).append( ", " );
 			}
 		}
 		return buffer.toString();
 	}
 
 	public String toString(Map<String,TypedValue> namedTypedValues) throws HibernateException {
 		Map<String,String> result = new HashMap<String,String>();
 		for ( Map.Entry<String, TypedValue> entry : namedTypedValues.entrySet() ) {
 			result.put(
 					entry.getKey(), entry.getValue().getType().toLoggableString(
 					entry.getValue().getValue(),
 					factory
 			)
 			);
 		}
 		return result.toString();
 	}
 
 	// Cannot use Map as an argument because it clashes with the previous method (due to type erasure)
 	public void toString(Iterable<Map.Entry<EntityKey,Object>> entitiesByEntityKey) throws HibernateException {
         if ( ! LOG.isDebugEnabled() || ! entitiesByEntityKey.iterator().hasNext() ) return;
-        LOG.debugf( "Listing entities:" );
+        LOG.debug( "Listing entities:" );
 		int i=0;
 		for (  Map.Entry<EntityKey,Object> entityKeyAndEntity : entitiesByEntityKey ) {
 			if (i++>20) {
-                LOG.debugf("More......");
+                LOG.debug("More......");
 				break;
 			}
-            LOG.debugf( toString( entityKeyAndEntity.getKey().getEntityName(), entityKeyAndEntity.getValue() ) );
+            LOG.debug( toString( entityKeyAndEntity.getKey().getEntityName(), entityKeyAndEntity.getValue() ) );
 		}
 	}
 
 	public EntityPrinter(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/DTDEntityResolver.java b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/DTDEntityResolver.java
index 9a223e0ba6..03f4f1eaf3 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/DTDEntityResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/DTDEntityResolver.java
@@ -1,127 +1,127 @@
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
 package org.hibernate.internal.util.xml;
 
 import java.io.InputStream;
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ConfigHelper;
 
 /**
  * An {@link EntityResolver} implementation which attempts to resolve
  * various systemId URLs to local classpath look ups<ol>
  * <li>Any systemId URL beginning with <tt>http://www.hibernate.org/dtd/</tt> is
  * searched for as a classpath resource in the classloader which loaded the
  * Hibernate classes.</li>
  * <li>Any systemId URL using <tt>classpath</tt> as the scheme (i.e. starting
  * with <tt>classpath://</tt> is searched for as a classpath resource using first
  * the current thread context classloader and then the classloader which loaded
  * the Hibernate classes.
  * </ol>
  * <p/>
  * Any entity references which cannot be resolved in relation to the above
  * rules result in returning null, which should force the SAX reader to
  * handle the entity reference in its default manner.
  *
  * @author Markus Meissner
  * @author Gavin King
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class DTDEntityResolver implements EntityResolver, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DTDEntityResolver.class.getName() );
 
 	private static final String HIBERNATE_NAMESPACE = "http://www.hibernate.org/dtd/";
 	private static final String OLD_HIBERNATE_NAMESPACE = "http://hibernate.sourceforge.net/";
 	private static final String USER_NAMESPACE = "classpath://";
 
 	public InputSource resolveEntity(String publicId, String systemId) {
 		InputSource source = null; // returning null triggers default behavior
 		if ( systemId != null ) {
 			LOG.debugf( "Trying to resolve system-id [%s]", systemId );
 			if ( systemId.startsWith( HIBERNATE_NAMESPACE ) ) {
-				LOG.debugf( "Recognized hibernate namespace; attempting to resolve on classpath under org/hibernate/" );
+				LOG.debug( "Recognized hibernate namespace; attempting to resolve on classpath under org/hibernate/" );
 				source = resolveOnClassPath( publicId, systemId, HIBERNATE_NAMESPACE );
 			}
 			else if ( systemId.startsWith( OLD_HIBERNATE_NAMESPACE ) ) {
 				LOG.recognizedObsoleteHibernateNamespace( OLD_HIBERNATE_NAMESPACE, HIBERNATE_NAMESPACE );
-				LOG.debugf( "Attempting to resolve on classpath under org/hibernate/" );
+				LOG.debug( "Attempting to resolve on classpath under org/hibernate/" );
 				source = resolveOnClassPath( publicId, systemId, OLD_HIBERNATE_NAMESPACE );
 			}
 			else if ( systemId.startsWith( USER_NAMESPACE ) ) {
-				LOG.debugf( "Recognized local namespace; attempting to resolve on classpath" );
+				LOG.debug( "Recognized local namespace; attempting to resolve on classpath" );
 				String path = systemId.substring( USER_NAMESPACE.length() );
 				InputStream stream = resolveInLocalNamespace( path );
 				if ( stream == null ) {
 					LOG.debugf( "Unable to locate [%s] on classpath", systemId );
 				}
 				else {
 					LOG.debugf( "Located [%s] in classpath", systemId );
 					source = new InputSource( stream );
 					source.setPublicId( publicId );
 					source.setSystemId( systemId );
 				}
 			}
 		}
 		return source;
 	}
 
 	private InputSource resolveOnClassPath(String publicId, String systemId, String namespace) {
 		InputSource source = null;
 		String path = "org/hibernate/" + systemId.substring( namespace.length() );
 		InputStream dtdStream = resolveInHibernateNamespace( path );
 		if ( dtdStream == null ) {
 			LOG.debugf( "Unable to locate [%s] on classpath", systemId );
 			if ( systemId.substring( namespace.length() ).indexOf( "2.0" ) > -1 ) {
 				LOG.usingOldDtd();
 			}
 		}
 		else {
 			LOG.debugf( "Located [%s] in classpath", systemId );
 			source = new InputSource( dtdStream );
 			source.setPublicId( publicId );
 			source.setSystemId( systemId );
 		}
 		return source;
 	}
 
 	protected InputStream resolveInHibernateNamespace(String path) {
 		return this.getClass().getClassLoader().getResourceAsStream( path );
 	}
 
 	protected InputStream resolveInLocalNamespace(String path) {
 		try {
 			return ConfigHelper.getUserResourceAsStream( path );
 		}
 		catch ( Throwable t ) {
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
index 4dd71a9f4e..c0738d9b6c 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
@@ -1,245 +1,245 @@
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
 package org.hibernate.jmx;
 
 import java.io.InvalidObjectException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Set;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryRegistry;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.service.jndi.internal.JndiServiceImpl;
 import org.hibernate.stat.Statistics;
 
 /**
  * A flyweight for <tt>SessionFactory</tt>. If the MBean itself does not
  * have classpath to the persistent classes, then a stub will be registered
  * with JNDI and the actual <tt>SessionFactoryImpl</tt> built upon first
  * access.
  *
  * @author Gavin King
  *
  * @deprecated See <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6190">HHH-6190</a> for details
  */
 @Deprecated
 public class SessionFactoryStub implements SessionFactory {
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryStub.class.getName());
 
 	private transient SessionFactory impl;
 	private transient HibernateService service;
 	private String uuid;
 	private String name;
 
 	SessionFactoryStub(HibernateService service) {
 		this.service = service;
 		this.name = service.getJndiName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 
 		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, new JndiServiceImpl( service.getProperties() )  );
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return impl.getSessionFactoryOptions();
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return getImpl().withOptions();
 	}
 
 	public Session openSession() throws HibernateException {
 		return getImpl().openSession();
 	}
 
 	public Session getCurrentSession() {
 		return getImpl().getCurrentSession();
 	}
 
 	private synchronized SessionFactory getImpl() {
 		if (impl==null) impl = service.buildSessionFactory();
 		return impl;
 	}
 
 	//readResolveObject
 	private Object readResolve() throws ObjectStreamException {
 		// look for the instance by uuid
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid ) ;
 		if ( result == null ) {
             // in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( result == null ) {
 				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 			}
-            LOG.debugf("Resolved stub SessionFactory by name");
-        }
+			LOG.debug("Resolved stub SessionFactory by name");
+		}
 		else {
-			LOG.debugf("Resolved stub SessionFactory by UUID");
+			LOG.debug("Resolved stub SessionFactory by UUID");
 		}
 		return result;
 	}
 
 	/**
 	 * @see javax.naming.Referenceable#getReference()
 	 */
 	@Override
 	public Reference getReference() throws NamingException {
 		return new Reference(
 				SessionFactoryStub.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getImpl().getClassMetadata(persistentClass);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName)
 	throws HibernateException {
 		return getImpl().getClassMetadata(entityName);
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return getImpl().getCollectionMetadata(roleName);
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return getImpl().getAllClassMetadata();
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return getImpl().getAllCollectionMetadata();
 	}
 
 	public void close() throws HibernateException {
 	}
 
 	public boolean isClosed() {
 		return false;
 	}
 
 	public Cache getCache() {
 		return getImpl().getCache();
 	}
 
 	public void evict(Class persistentClass, Serializable id)
 		throws HibernateException {
 		getImpl().evict(persistentClass, id);
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getImpl().evict(persistentClass);
 	}
 
 	public void evictEntity(String entityName, Serializable id)
 	throws HibernateException {
 		getImpl().evictEntity(entityName, id);
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getImpl().evictEntity(entityName);
 	}
 
 	public void evictCollection(String roleName, Serializable id)
 		throws HibernateException {
 		getImpl().evictCollection(roleName, id);
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getImpl().evictCollection(roleName);
 	}
 
 	public void evictQueries() throws HibernateException {
 		getImpl().evictQueries();
 	}
 
 	public void evictQueries(String cacheRegion) throws HibernateException {
 		getImpl().evictQueries(cacheRegion);
 	}
 
 	public Statistics getStatistics() {
 		return getImpl().getStatistics();
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return getImpl().withStatelessOptions();
 	}
 
 	public StatelessSession openStatelessSession() {
 		return getImpl().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection conn) {
 		return getImpl().openStatelessSession(conn);
 	}
 
 	public Set getDefinedFilterNames() {
 		return getImpl().getDefinedFilterNames();
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		return getImpl().getFilterDefinition( filterName );
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return getImpl().containsFetchProfileDefinition( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return getImpl().getTypeHelper();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 28943af31b..7cd003339e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1070,1539 +1070,1539 @@ public abstract class Loader {
 	 */
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(Object[] row,
 														 ResultSet rs,
 														 SessionImplementor session)
 			throws SQLException, HibernateException {
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
 	        final EntityKey key,
 	        final Object object,
 	        final LockMode lockMode,
 	        final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
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
 		return selection != null && selection.getMaxRows() != null && selection.getMaxRows() > 0;
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
 		boolean useLimitOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		final boolean useScrollableResultSetToSkip = hasFirstRow &&
 				!useLimitOffset && canScroll;
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimit, queryParameters );
 //
 //		if(canScroll && ( scroll || useScrollableResultSetToSkip )){
 //			 scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;
 //		}else{
 //			scrollMode = null;
 //		}
 		if ( useLimit ) {
 			sql = dialect.getLimitString(
 					sql.trim(), //use of trim() here is ugly?
 					useLimitOffset ? getFirstRow(selection) : 0,
 					getMaxOrLimit(selection, dialect)
 				);
 		}
 
 		sql = preprocessSQL( sql, queryParameters, dialect );
 
 		PreparedStatement st = null;
 
 
 		st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
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
                     if (!dialect.supportsLockTimeouts()) LOG.debugf("Lock timeout [%s] requested but dialect reported to not support lock timeouts",
                                                                     lockOptions.getTimeOut());
                     else if (dialect.isLockTimeoutParameterized()) st.setInt(col++, lockOptions.getTimeOut());
 				}
 			}
 
 			LOG.tracev( "Bound [{0}] parameters total", col );
 		}
 		catch ( SQLException sqle ) {
 			st.close();
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			st.close();
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
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
 					if ( debugEnabled ) LOG.debugf( "bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex );
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
 			rs = st.executeQuery();
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
 			st.close();
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
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			LOG.trace( "Building columnName->columnIndex cache" );
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
 
-		LOG.debugf( "Done entity load" );
+		LOG.debug( "Done entity load" );
 
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
 
-		LOG.debugf( "Loading collection element by index" );
+		LOG.debug( "Loading collection element by index" );
 
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
 			        "could not collection element by index",
 			        getSQLString()
 				);
 		}
 
-		LOG.debugf( "Done entity load" );
+		LOG.debug( "Done entity load" );
 
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
 
-		LOG.debugf( "Done entity batch load" );
+		LOG.debug( "Done entity batch load" );
 
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
 
-		LOG.debugf( "Done loading collection" );
+		LOG.debug( "Done loading collection" );
 
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
 
-		LOG.debugf( "Done batch load" );
+		LOG.debug( "Done batch load" );
 
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
 				resultTypes,
 				queryCache,
 				key
 			);
 
 		if ( result == null ) {
 			result = doList( session, queryParameters, key.getResultTransformer() );
 
 			putResultInQueryCache(
 					session,
 					queryParameters,
 					resultTypes,
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
 			final Set querySpaces,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key) {
 		List result = null;
 
 		if ( session.getCacheMode().isGetEnabled() ) {
 			boolean isImmutableNaturalKeyLookup =
 					queryParameters.isNaturalKeyLookup() &&
 							resultTypes.length == 1 &&
 							resultTypes[0].isEntityType() &&
 							getEntityPersister( EntityType.class.cast( resultTypes[0] ) )
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
 						key.getResultTransformer().getCachedResultTypes( resultTypes ),
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
 
 	private void putResultInQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		if ( session.getCacheMode().isPutEnabled() ) {
 			boolean put = queryCache.put(
 					key,
 					key.getResultTransformer().getCachedResultTypes( resultTypes ),
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 5ff636db00..c318525ee9 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -126,1734 +126,1734 @@ public abstract class AbstractCollectionPersister
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
 
 	private final boolean hasManyToManyOrder;
 	private final String manyToManyOrderByTemplate;
 
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
 			ColumnMapper mapper = new ColumnMapper() {
 
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			sqlOrderByStringTemplate = Template.renderOrderByStringTemplate(
 					collection.getOrderBy(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 					);
 		}
 		else {
 			sqlOrderByStringTemplate = null;
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
 			ColumnMapper mapper = new ColumnMapper() {
 
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			manyToManyOrderByTemplate = Template.renderOrderByStringTemplate(
 					collection.getManyToManyOrdering(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 					);
 		}
 		else {
 			manyToManyOrderByTemplate = null;
 		}
 
 		initCollectionPropertyMap();
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
 				? StringHelper.replace( sqlOrderByStringTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? StringHelper.replace( manyToManyOrderByTemplate, Template.TEMPLATE, alias )
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
 
-				LOG.debugf( "Done deleting collection" );
+				LOG.debug( "Done deleting collection" );
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
-					LOG.debugf( "Collection was empty" );
+					LOG.debug( "Collection was empty" );
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
-					LOG.debugf( "No rows to delete" );
+					LOG.debug( "No rows to delete" );
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
index d524388eee..c6cdae1e38 100755
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
@@ -1,86 +1,86 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.FlushMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.AbstractQueryImpl;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 
 /**
  * Not really a <tt>Loader</tt>, just a wrapper around a
  * named query.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class NamedQueryLoader implements UniqueEntityLoader {
 	private final String queryName;
 	private final EntityPersister persister;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, NamedQueryLoader.class.getName());
 
 	public NamedQueryLoader(String queryName, EntityPersister persister) {
 		super();
 		this.queryName = queryName;
 		this.persister = persister;
 	}
 
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
-        if (lockOptions != null) LOG.debugf("Ignoring lock-options passed to named query loader");
+		if (lockOptions != null) LOG.debug("Ignoring lock-options passed to named query loader");
 		return load( id, optionalObject, session );
 	}
 
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
         LOG.debugf("Loading entity: %s using named query: %s", persister.getEntityName(), queryName);
 
 		AbstractQueryImpl query = (AbstractQueryImpl) session.getNamedQuery(queryName);
 		if ( query.hasNamedParameters() ) {
 			query.setParameter(
 					query.getNamedParameters()[0],
 					id,
 					persister.getIdentifierType()
 				);
 		}
 		else {
 			query.setParameter( 0, id, persister.getIdentifierType() );
 		}
 		query.setOptionalId(id);
 		query.setOptionalEntityName( persister.getEntityName() );
 		query.setOptionalObject(optionalObject);
 		query.setFlushMode( FlushMode.MANUAL );
 		query.list();
 
 		// now look up the object we are really interested in!
 		// (this lets us correctly handle proxies and multi-row or multi-column queries)
 		return session.getPersistenceContext().getEntity( session.generateEntityKey( id, persister ) );
 
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
index 73a4653a2f..f9e4745d43 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
@@ -1,217 +1,217 @@
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
 package org.hibernate.service.jdbc.connections.internal;
 
 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.UnknownUnwrapTypeException;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.Stoppable;
 
 /**
  * A connection provider that uses the {@link java.sql.DriverManager} directly to open connections and provides
  * a very rudimentary connection pool.
  * <p/>
  * IMPL NOTE : not intended for production use!
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnnecessaryUnboxing"})
 public class DriverManagerConnectionProviderImpl implements ConnectionProvider, Configurable, Stoppable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DriverManagerConnectionProviderImpl.class.getName() );
 
 	private String url;
 	private Properties connectionProps;
 	private Integer isolation;
 	private int poolSize;
 	private boolean autocommit;
 
 	private final ArrayList<Connection> pool = new ArrayList<Connection>();
 	private int checkedOut = 0;
 
 	private boolean stopped;
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return ConnectionProvider.class.equals( unwrapType ) ||
 				DriverManagerConnectionProviderImpl.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( ConnectionProvider.class.equals( unwrapType ) ||
 				DriverManagerConnectionProviderImpl.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	public void configure(Map configurationValues) {
         LOG.usingHibernateBuiltInConnectionPool();
 
 		String driverClassName = (String) configurationValues.get( AvailableSettings.DRIVER );
         if (driverClassName == null) LOG.jdbcDriverNotSpecified(AvailableSettings.DRIVER);
 		else {
 			try {
 				// trying via forName() first to be as close to DriverManager's semantics
 				Class.forName( driverClassName );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				try {
 					ReflectHelper.classForName( driverClassName );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new HibernateException( "Specified JDBC Driver " + driverClassName + " class not found", e );
 				}
 			}
 		}
 
 		poolSize = ConfigurationHelper.getInt( AvailableSettings.POOL_SIZE, configurationValues, 20 ); // default pool size 20
         LOG.hibernateConnectionPoolSize(poolSize);
 
 		autocommit = ConfigurationHelper.getBoolean( AvailableSettings.AUTOCOMMIT, configurationValues );
         LOG.autoCommitMode( autocommit );
 
 		isolation = ConfigurationHelper.getInteger( AvailableSettings.ISOLATION, configurationValues );
         if (isolation != null) LOG.jdbcIsolationLevel(Environment.isolationLevelToString(isolation.intValue()));
 
 		url = (String) configurationValues.get( AvailableSettings.URL );
 		if ( url == null ) {
             String msg = LOG.jdbcUrlNotSpecified(AvailableSettings.URL);
             LOG.error(msg);
 			throw new HibernateException( msg );
 		}
 
 		connectionProps = ConnectionProviderInitiator.getConnectionProperties( configurationValues );
 
 		LOG.usingDriver( driverClassName, url );
 		// if debug level is enabled, then log the password, otherwise mask it
 		if ( LOG.isDebugEnabled() )
 			LOG.connectionProperties( connectionProps );
 		else
 			LOG.connectionProperties( ConfigurationHelper.maskOut( connectionProps, "password" ) );
 	}
 
 	public void stop() {
 		LOG.cleaningUpConnectionPool( url );
 
 		for ( Connection connection : pool ) {
 			try {
 				connection.close();
 			}
 			catch (SQLException sqle) {
 				LOG.unableToClosePooledConnection( sqle );
 			}
 		}
 		pool.clear();
 		stopped = true;
 	}
 
 	public Connection getConnection() throws SQLException {
 		LOG.tracev( "Total checked-out connections: {0}", checkedOut );
 
 		// essentially, if we have available connections in the pool, use one...
 		synchronized (pool) {
 			if ( !pool.isEmpty() ) {
 				int last = pool.size() - 1;
 				LOG.tracev( "Using pooled JDBC connection, pool size: {0}", last );
 				Connection pooled = pool.remove( last );
 				if ( isolation != null ) {
 					pooled.setTransactionIsolation( isolation.intValue() );
 				}
 				if ( pooled.getAutoCommit() != autocommit ) {
 					pooled.setAutoCommit( autocommit );
 				}
 				checkedOut++;
 				return pooled;
 			}
 		}
 
 		// otherwise we open a new connection...
 
-		LOG.debugf( "Opening new JDBC connection" );
+		LOG.debug( "Opening new JDBC connection" );
 		Connection conn = DriverManager.getConnection( url, connectionProps );
 		if ( isolation != null ) {
 			conn.setTransactionIsolation( isolation.intValue() );
 		}
 		if ( conn.getAutoCommit() != autocommit ) {
 			conn.setAutoCommit(autocommit);
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Created connection to: %s, Isolation Level: %s", url, conn.getTransactionIsolation() );
 		}
 
 		checkedOut++;
 		return conn;
 	}
 
 	public void closeConnection(Connection conn) throws SQLException {
 		checkedOut--;
 
 		// add to the pool if the max size is not yet reached.
 		synchronized (pool) {
 			int currentSize = pool.size();
 			if ( currentSize < poolSize ) {
 				LOG.tracev( "Returning connection to pool, pool size: {0}", ( currentSize + 1 ) );
 				pool.add(conn);
 				return;
 			}
 		}
 
-		LOG.debugf( "Closing JDBC connection" );
+		LOG.debug( "Closing JDBC connection" );
 		conn.close();
 	}
 
 	@Override
 	protected void finalize() throws Throwable {
 		if ( !stopped ) {
 			stop();
 		}
 		super.finalize();
 	}
 
 	public boolean supportsAggressiveRelease() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index 7dc828e736..dc1620a9c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,622 +1,622 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.hibernate.internal.util.StringHelper;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * Commandline tool to export table schema to the database. This class may also be called from inside an application.
  *
  * @author Daniel Bradby
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class SchemaExport {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaExport.class.getName());
 	private static final String DEFAULT_IMPORT_FILE = "/import.sql";
 
 	public static enum Type {
 		CREATE,
 		DROP,
 		NONE,
 		BOTH;
 
 		public boolean doCreate() {
 			return this == BOTH || this == CREATE;
 		}
 
 		public boolean doDrop() {
 			return this == BOTH || this == DROP;
 		}
 	}
 
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final String[] dropSQL;
 	private final String[] createSQL;
 	private final String importFiles;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 	private ImportSqlCommandExtractor importSqlCommandExtractor = ImportSqlCommandExtractorInitiator.DEFAULT_EXTRACTOR;
 
 	private String outputFile = null;
 	private String delimiter;
 	private boolean haltOnError = false;
 
 	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				serviceRegistry.getService( ConnectionProvider.class )
 		);
 		this.sqlStatementLogger = serviceRegistry.getService( JdbcServices.class ).getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = serviceRegistry.getService( JdbcServices.class ).getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	public SchemaExport(MetadataImplementor metadata) {
 		ServiceRegistry serviceRegistry = metadata.getServiceRegistry();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				serviceRegistry.getService( ConnectionProvider.class )
 		);
         JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				serviceRegistry.getService( ConfigurationService.class ).getSettings(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = jdbcServices.getDialect();
 		this.dropSQL = metadata.getDatabase().generateDropSchemaScript( dialect );
 		this.createSQL = metadata.getDatabase().generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration) {
 		this( configuration, configuration.getProperties() );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, with the given
 	 * database connection properties.
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @param properties The properties from which to configure connectivity etc.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 *
 	 * @deprecated properties may be specified via the Configuration object
 	 */
 	@Deprecated
     public SchemaExport(Configuration configuration, Properties properties) throws HibernateException {
 		final Dialect dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				properties,
 				DEFAULT_IMPORT_FILE
 		);
 
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, using the supplied connection for connectivity.
 	 *
 	 * @param configuration The configuration to use.
 	 * @param connection The JDBC connection to use.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration, Connection connection) throws HibernateException {
 		this.connectionHelper = new SuppliedConnectionHelper( connection );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				AvailableSettings.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = Dialect.getDialect( configuration.getProperties() );
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	public SchemaExport(
 			ConnectionHelper connectionHelper,
 			String[] dropSql,
 			String[] createSql) {
 		this.connectionHelper = connectionHelper;
 		this.dropSQL = dropSql;
 		this.createSQL = createSql;
 		this.importFiles = "";
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.formatter = FormatStyle.DDL.getFormatter();
 	}
 
 	/**
 	 * For generating a export script file, this is the file which will be written.
 	 *
 	 * @param filename The name of the file to which to write the export script.
 	 * @return this
 	 */
 	public SchemaExport setOutputFile(String filename) {
 		outputFile = filename;
 		return this;
 	}
 
 	/**
 	 * Set the end of statement delimiter
 	 *
 	 * @param delimiter The delimiter
 	 * @return this
 	 */
 	public SchemaExport setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 		return this;
 	}
 
 	/**
 	 * Should we format the sql strings?
 	 *
 	 * @param format Should we format SQL strings
 	 * @return this
 	 */
 	public SchemaExport setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		return this;
 	}
 
 	/**
 	 * Set <i>import.sql</i> command extractor. By default {@link SingleLineSqlCommandExtractor} is used.
 	 *
 	 * @param importSqlCommandExtractor <i>import.sql</i> command extractor.
 	 * @return this
 	 */
 	public SchemaExport setImportSqlCommandExtractor(ImportSqlCommandExtractor importSqlCommandExtractor) {
 		this.importSqlCommandExtractor = importSqlCommandExtractor;
 		return this;
 	}
 
 	/**
 	 * Should we stop once an error occurs?
 	 *
 	 * @param haltOnError True if export should stop after error.
 	 * @return this
 	 */
 	public SchemaExport setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 		return this;
 	}
 
 	/**
 	 * Run the schema creation script; drop script is automatically
 	 * executed before running the creation script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void create(boolean script, boolean export) {
 		create( Target.interpret( script, export ) );
 	}
 
 	/**
 	 * Run the schema creation script; drop script is automatically
 	 * executed before running the creation script.
 	 *
 	 * @param output the target of the script.
 	 */
 	public void create(Target output) {
 		// need to drop tables before creating so need to specify Type.BOTH
 		execute( output, Type.BOTH );
 	}
 
 	/**
 	 * Run the drop schema script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void drop(boolean script, boolean export) {
 		drop( Target.interpret( script, export ) );
 	}
 
 	public void drop(Target output) {
 		execute( output, Type.DROP );
 	}
 
 	public void execute(boolean script, boolean export, boolean justDrop, boolean justCreate) {
 		execute( Target.interpret( script, export ), interpretType( justDrop, justCreate ) );
 	}
 
 	private Type interpretType(boolean justDrop, boolean justCreate) {
 		if ( justDrop ) {
 			return Type.DROP;
 		}
 		else if ( justCreate ) {
 			return Type.CREATE;
 		}
 		else {
 			return Type.BOTH;
 		}
 	}
 
 	public void execute(Target output, Type type) {
 		if ( output == Target.NONE || type == SchemaExport.Type.NONE ) {
 			return;
 		}
 		exceptions.clear();
 
 		LOG.runningHbm2ddlSchemaExport();
 
 		final List<NamedReader> importFileReaders = new ArrayList<NamedReader>();
 		for ( String currentFile : importFiles.split(",") ) {
 			try {
 				final String resourceName = currentFile.trim();
 				InputStream stream = ConfigHelper.getResourceAsStream( resourceName );
 				importFileReaders.add( new NamedReader( resourceName, stream ) );
 			}
 			catch ( HibernateException e ) {
 				LOG.debugf("Import file not found: %s", currentFile);
 			}
 		}
 
 		final List<Exporter> exporters = new ArrayList<Exporter>();
 		try {
 			// prepare exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( output.doScript() ) {
 				exporters.add( new ScriptExporter() );
 			}
 			if ( outputFile != null ) {
 				exporters.add( new FileExporter( outputFile ) );
 			}
 			if ( output.doExport() ) {
 				exporters.add( new DatabaseExporter( connectionHelper, sqlExceptionHelper ) );
 			}
 
 			// perform exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( type.doDrop() ) {
 				perform( dropSQL, exporters );
 			}
 			if ( type.doCreate() ) {
 				perform( createSQL, exporters );
 				if ( ! importFileReaders.isEmpty() ) {
 					for ( NamedReader namedReader : importFileReaders ) {
 						importScript( namedReader, exporters );
 					}
 				}
 			}
 		}
 		catch (Exception e) {
 			exceptions.add( e );
 			LOG.schemaExportUnsuccessful( e );
 		}
 		finally {
 			// release exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.release();
 				}
 				catch (Exception ignore) {
 				}
 			}
 
 			// release the named readers from import scripts
 			for ( NamedReader namedReader : importFileReaders ) {
 				try {
 					namedReader.getReader().close();
 				}
 				catch (Exception ignore) {
 				}
 			}
             LOG.schemaExportComplete();
 		}
 	}
 
 	private void perform(String[] sqlCommands, List<Exporter> exporters) {
 		for ( String sqlCommand : sqlCommands ) {
 			String formatted = formatter.format( sqlCommand );
 	        if ( delimiter != null ) {
 				formatted += delimiter;
 			}
 			sqlStatementLogger.logStatement( sqlCommand, formatter );
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.export( formatted );
 				}
 				catch (Exception e) {
 					if ( haltOnError ) {
 						throw new HibernateException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
 					LOG.unsuccessfulCreate( sqlCommand );
 					LOG.error( e.getMessage() );
 				}
 			}
 		}
 	}
 
 	private void importScript(NamedReader namedReader, List<Exporter> exporters) throws Exception {
 		BufferedReader reader = new BufferedReader( namedReader.getReader() );
 		String[] statements = importSqlCommandExtractor.extractCommands( reader );
 		if (statements != null) {
 			for ( String statement : statements ) {
 				if ( statement != null ) {
 					String trimmedSql = statement.trim();
 					if ( trimmedSql.endsWith( ";" )) {
 						trimmedSql = trimmedSql.substring( 0, statement.length() - 1 );
 					}
 					if ( !StringHelper.isEmpty( trimmedSql ) ) {
 						try {
 							for ( Exporter exporter : exporters ) {
 								if ( exporter.acceptsImportScripts() ) {
 									exporter.export( trimmedSql );
 								}
 							}
 						}
 						catch ( Exception e ) {
 							throw new ImportScriptException( "Error during statement execution (file: '" + namedReader.getName() + "'): " + trimmedSql, e );
 						}
 					}
 				}
 			}
 		}
 	}
 
 	private static class NamedReader {
 		private final Reader reader;
 		private final String name;
 
 		public NamedReader(String name, InputStream stream) {
 			this.name = name;
 			this.reader = new InputStreamReader( stream );
 		}
 
 		public Reader getReader() {
 			return reader;
 		}
 
 		public String getName() {
 			return name;
 		}
 	}
 
 	private void execute(boolean script, boolean export, Writer fileOutput, Statement statement, final String sql)
 			throws IOException, SQLException {
 		final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
 
 		String formatted = formatter.format( sql );
         if (delimiter != null) formatted += delimiter;
         if (script) System.out.println(formatted);
-        LOG.debugf(formatted);
+        LOG.debug(formatted);
 		if ( outputFile != null ) {
 			fileOutput.write( formatted + "\n" );
 		}
 		if ( export ) {
 
 			statement.executeUpdate( sql );
 			try {
 				SQLWarning warnings = statement.getWarnings();
 				if ( warnings != null) {
 					sqlExceptionHelper.logAndClearWarnings( connectionHelper.getConnection() );
 				}
 			}
 			catch( SQLException sqle ) {
                 LOG.unableToLogSqlWarnings(sqle);
 			}
 		}
 
 	}
 
 	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		return (StandardServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			boolean drop = false;
 			boolean create = false;
 			boolean halt = false;
 			boolean export = true;
 			String outFile = null;
 			String importFile = DEFAULT_IMPORT_FILE;
 			String propFile = null;
 			boolean format = false;
 			String delim = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].equals( "--drop" ) ) {
 						drop = true;
 					}
 					else if ( args[i].equals( "--create" ) ) {
 						create = true;
 					}
 					else if ( args[i].equals( "--haltonerror" ) ) {
 						halt = true;
 					}
 					else if ( args[i].equals( "--text" ) ) {
 						export = false;
 					}
 					else if ( args[i].startsWith( "--output=" ) ) {
 						outFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--import=" ) ) {
 						importFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].equals( "--format" ) ) {
 						format = true;
 					}
 					else if ( args[i].startsWith( "--delimiter=" ) ) {
 						delim = args[i].substring( 12 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) )
 										.newInstance()
 						);
 					}
 				}
 				else {
 					String filename = args[i];
 					if ( filename.endsWith( ".jar" ) ) {
 						cfg.addJar( new File( filename ) );
 					}
 					else {
 						cfg.addFile( filename );
 					}
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			if (importFile != null) {
 				cfg.setProperty( AvailableSettings.HBM2DDL_IMPORT_FILES, importFile );
 			}
 
 			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				SchemaExport se = new SchemaExport( serviceRegistry, cfg )
 						.setHaltOnError( halt )
 						.setOutputFile( outFile )
 						.setDelimiter( delim )
 						.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 				if ( format ) {
 					se.setFormat( true );
 				}
 				se.execute( script, export, drop, create );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToCreateSchema(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
index c8f17979fc..713370850d 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
@@ -1,295 +1,295 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.StandardServiceRegistryImpl;
 
 /**
  * A commandline tool to update a database schema. May also be called from inside an application.
  *
  * @author Christoph Sturm
  * @author Steve Ebersole
  */
 public class SchemaUpdate {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaUpdate.class.getName());
 
 	private final Configuration configuration;
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final Dialect dialect;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 
 	private boolean haltOnError = false;
 	private boolean format = true;
 	private String outputFile = null;
 	private String delimiter;
 
 	public SchemaUpdate(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaUpdate(Configuration configuration, Properties properties) throws HibernateException {
 		this.configuration = configuration;
 		this.dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 	}
 
 	public SchemaUpdate(ServiceRegistry serviceRegistry, Configuration cfg) throws HibernateException {
 		this.configuration = cfg;
 
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.dialect = jdbcServices.getDialect();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	private static StandardServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		return (StandardServiceRegistryImpl) new ServiceRegistryBuilder().applySettings( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			// If true then execute db updates, otherwise just generate and display updates
 			boolean doUpdate = true;
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--text" ) ) {
 						doUpdate = false;
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			StandardServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaUpdate( serviceRegistry, cfg ).execute( script, doUpdate );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Execute the schema updates
 	 *
 	 * @param script print all DDL to the console
 	 */
 	public void execute(boolean script, boolean doUpdate) {
 		execute( Target.interpret( script, doUpdate ) );
 	}
 	
 	public void execute(Target target) {
         LOG.runningHbm2ddlSchemaUpdate();
 
 		Connection connection = null;
 		Statement stmt = null;
 		Writer outputFileWriter = null;
 
 		exceptions.clear();
 
 		try {
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect );
 				stmt = connection.createStatement();
 			}
 			catch ( SQLException sqle ) {
 				exceptions.add( sqle );
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
             LOG.updatingSchema();
 
 			if ( outputFile != null ) {
                 LOG.writingGeneratedSchemaToFile( outputFile );
 				outputFileWriter = new FileWriter( outputFile );
 			}
 
 			String[] sqlStrings = configuration.generateSchemaUpdateScript( dialect, meta );
 			for ( String sql : sqlStrings ) {
 				String formatted = formatter.format( sql );
 				try {
 					if ( delimiter != null ) {
 						formatted += delimiter;
 					}
 					if ( target.doScript() ) {
 						System.out.println( formatted );
 					}
 					if ( outputFile != null ) {
 						outputFileWriter.write( formatted + "\n" );
 					}
 					if ( target.doExport() ) {
-                        LOG.debugf( sql );
+                        LOG.debug( sql );
 						stmt.executeUpdate( formatted );
 					}
 				}
 				catch ( SQLException e ) {
 					if ( haltOnError ) {
 						throw new JDBCException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
                     LOG.unsuccessful(sql);
                     LOG.error(e.getMessage());
 				}
 			}
 
             LOG.schemaUpdateComplete();
 
 		}
 		catch ( Exception e ) {
 			exceptions.add( e );
             LOG.unableToCompleteSchemaUpdate(e);
 		}
 		finally {
 
 			try {
 				if ( stmt != null ) {
 					stmt.close();
 				}
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
 				exceptions.add( e );
                 LOG.unableToCloseConnection(e);
 			}
 			try {
 				if( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch(Exception e) {
 				exceptions.add(e);
                 LOG.unableToCloseConnection(e);
 			}
 		}
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 
 	public void setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 	}
 
 	public void setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public void setOutputFile(String outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	public void setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
index 07aaf64716..08f707df13 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/DbTimestampType.java
@@ -1,154 +1,154 @@
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
 
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Timestamp;
 import java.util.Date;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * <tt>dbtimestamp</tt>: An extension of {@link TimestampType} which
  * maps to the database's current timestamp, rather than the jvm's
  * current timestamp.
  * <p/>
  * Note: May/may-not cause issues on dialects which do not properly support
  * a true notion of timestamp (Oracle < 8, for example, where only its DATE
  * datatype is supported).  Depends on the frequency of DML operations...
  *
  * @author Steve Ebersole
  */
 public class DbTimestampType extends TimestampType {
 	public static final DbTimestampType INSTANCE = new DbTimestampType();
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DbTimestampType.class.getName() );
 
 	@Override
 	public String getName() {
 		return "dbtimestamp";
 	}
 
 	@Override
 	public String[] getRegistrationKeys() {
 		return new String[] { getName() };
 	}
 
 	@Override
 	public Date seed(SessionImplementor session) {
 		if ( session == null ) {
 			LOG.trace( "Incoming session was null; using current jvm time" );
 			return super.seed( session );
 		}
 		else if ( !session.getFactory().getDialect().supportsCurrentTimestampSelection() ) {
-			LOG.debugf( "Falling back to vm-based timestamp, as dialect does not support current timestamp selection" );
+			LOG.debug( "Falling back to vm-based timestamp, as dialect does not support current timestamp selection" );
 			return super.seed( session );
 		}
 		else {
 			return getCurrentTimestamp( session );
 		}
 	}
 
 	private Date getCurrentTimestamp(SessionImplementor session) {
 		Dialect dialect = session.getFactory().getDialect();
 		String timestampSelectString = dialect.getCurrentTimestampSelectString();
         if (dialect.isCurrentTimestampSelectStringCallable()) return useCallableStatement(timestampSelectString, session);
         return usePreparedStatement(timestampSelectString, session);
 	}
 
 	private Timestamp usePreparedStatement(String timestampSelectString, SessionImplementor session) {
 		PreparedStatement ps = null;
 		try {
 			ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( timestampSelectString, false );
 			ResultSet rs = ps.executeQuery();
 			rs.next();
 			Timestamp ts = rs.getTimestamp( 1 );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Current timestamp retreived from db : {0} (nanos={1}, time={2})", ts, ts.getNanos(), ts.getTime() );
 			}
 			return ts;
 		}
 		catch( SQLException e ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "could not select current db timestamp",
 			        timestampSelectString
 			);
 		}
 		finally {
 			if ( ps != null ) {
 				try {
 					ps.close();
 				}
 				catch( SQLException sqle ) {
 					LOG.unableToCleanUpPreparedStatement( sqle );
 				}
 			}
 		}
 	}
 
 	private Timestamp useCallableStatement(String callString, SessionImplementor session) {
 		CallableStatement cs = null;
 		try {
 			cs = (CallableStatement) session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( callString, true );
 			cs.registerOutParameter( 1, java.sql.Types.TIMESTAMP );
 			cs.execute();
 			Timestamp ts = cs.getTimestamp( 1 );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Current timestamp retreived from db : {0} (nanos={1}, time={2})", ts, ts.getNanos(), ts.getTime() );
 			}
 			return ts;
 		}
 		catch( SQLException e ) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "could not call current db timestamp function",
 			        callString
 			);
 		}
 		finally {
 			if ( cs != null ) {
 				try {
 					cs.close();
 				}
 				catch( SQLException sqle ) {
 					LOG.unableToCleanUpCallableStatement( sqle );
 				}
 			}
 		}
 	}
 }
