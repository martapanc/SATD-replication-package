diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
index 189d3f60a8..e48f4c549b 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/NaturalIdentifier.java
@@ -1,56 +1,64 @@
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
 package org.hibernate.criterion;
+import java.util.HashMap;
+import java.util.Map;
+
 import org.hibernate.Criteria;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.TypedValue;
 
 /**
  * @author Gavin King
- *
  * @see Session#byNaturalId(Class)
  * @see Session#byNaturalId(String)
  * @see Session#bySimpleNaturalId(Class)
  * @see Session#bySimpleNaturalId(String)
  */
 public class NaturalIdentifier implements Criterion {
 		
-	private Junction conjunction = new Conjunction();
+	private final Junction conjunction = new Conjunction();
+	private final Map<String, Object> naturalIdValues = new HashMap<String, Object>();
 
 	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return conjunction.getTypedValues(criteria, criteriaQuery);
 	}
 
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		return conjunction.toSqlString(criteria, criteriaQuery);
 	}
 	
+	public Map<String, Object> getNaturalIdValues() {
+		return naturalIdValues;
+	}
+
 	public NaturalIdentifier set(String property, Object value) {
 		conjunction.add( Restrictions.eq(property, value) );
+		naturalIdValues.put( property, value );
 		return this;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
index 92ffc323da..91e11fc854 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
@@ -1,131 +1,154 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
+import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.spi.PersistenceContext.CachedNaturalIdValueSource;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Defines the default load event listeners used by hibernate for loading entities
  * in response to generated load events.
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
 public class DefaultResolveNaturalIdEventListener
 		extends AbstractLockUpgradeEventListener
 		implements ResolveNaturalIdEventListener {
 
 	public static final Object REMOVED_ENTITY_MARKER = new Object();
 	public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			DefaultResolveNaturalIdEventListener.class.getName()
 	);
 
 	@Override
 	public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException {
 		final Serializable entityId = resolveNaturalId( event );
 		event.setEntityId( entityId );
 	}
 
 	/**
 	 * Coordinates the efforts to load a given entity. First, an attempt is
 	 * made to load the entity from the session-level cache. If not found there,
 	 * an attempt is made to locate it in second-level cache. Lastly, an
 	 * attempt is made to load it directly from the datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The loaded entity, or null.
 	 */
 	protected Serializable resolveNaturalId(final ResolveNaturalIdEvent event) {
 		final EntityPersister persister = event.getEntityPersister();
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled )
 			LOG.tracev( "Attempting to resolve: {0}",
 					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
 		Serializable entityId = resolveFromCache( event );
 		if ( entityId != null ) {
 			if ( traceEnabled )
 				LOG.tracev( "Resolved object in cache: {0}",
 						MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 			return entityId;
 		}
 
 		if ( traceEnabled )
 			LOG.tracev( "Object not resolved in any cache: {0}",
 					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
 		return loadFromDatasource( event );
 	}
 
 	/**
 	 * Attempts to resolve the entity id corresponding to the event's natural id values from the session
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The entity from the cache, or null.
 	 */
 	protected Serializable resolveFromCache(final ResolveNaturalIdEvent event) {
 		return event.getSession().getPersistenceContext().findCachedNaturalIdResolution(
 				event.getEntityPersister(),
 				event.getOrderedNaturalIdValues()
 		);
 	}
 
 	/**
 	 * Performs the process of loading an entity from the configured
 	 * underlying datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The object loaded from the datasource, or null if not found.
 	 */
 	protected Serializable loadFromDatasource(final ResolveNaturalIdEvent event) {
+		final SessionFactoryImplementor factory = event.getSession().getFactory();
+		final boolean stats = factory.getStatistics().isStatisticsEnabled();
+		long startTime = 0;
+		if ( stats ) {
+			startTime = System.currentTimeMillis();
+		}
+		
 		final Serializable pk = event.getEntityPersister().loadEntityIdByNaturalId(
 				event.getOrderedNaturalIdValues(),
 				event.getLockOptions(),
 				event.getSession()
 		);
-		event.getSession().getPersistenceContext().cacheNaturalIdResolution(
-				event.getEntityPersister(),
-				pk,
-				event.getOrderedNaturalIdValues(),
-				CachedNaturalIdValueSource.LOAD
-		);
+		
+		if ( stats ) {
+			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = event.getEntityPersister().getNaturalIdCacheAccessStrategy();
+			final String regionName = naturalIdCacheAccessStrategy == null ? null : naturalIdCacheAccessStrategy.getRegion().getName();
+			
+			factory.getStatisticsImplementor().naturalIdQueryExecuted(
+					regionName,
+					System.currentTimeMillis() - startTime );
+		}
+		
+		//PK can be null if the entity doesn't exist
+		if (pk != null) {
+			event.getSession().getPersistenceContext().cacheNaturalIdResolution(
+					event.getEntityPersister(),
+					pk,
+					event.getOrderedNaturalIdValues(),
+					CachedNaturalIdValueSource.LOAD
+			);
+		}
+		
 		return pk;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
index 6ce864900c..130e1a542c 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
@@ -565,1001 +565,1021 @@ public interface CoreMessageLogger extends BasicLogger {
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error in named query: %s", id = 177)
 	void namedQueryError(String queryName,
 						 @Cause HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Naming exception occurred accessing factory: %s", id = 178)
 	void namingExceptionAccessingFactory(NamingException exception);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Narrowing proxy to %s - this operation breaks ==", id = 179)
 	void narrowingProxy(Class concreteProxyClass);
 
 	@LogMessage(level = WARN)
 	@Message(value = "FirstResult/maxResults specified on polymorphic query; applying in memory!", id = 180)
 	void needsLimit();
 
 	@LogMessage(level = WARN)
 	@Message(value = "No appropriate connection provider encountered, assuming application will be supplying connections",
 			id = 181)
 	void noAppropriateConnectionProvider();
 
 	@LogMessage(level = INFO)
 	@Message(value = "No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)",
 			id = 182)
 	void noDefaultConstructor(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "no persistent classes found for query class: %s", id = 183)
 	void noPersistentClassesFound(String query);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "No session factory with JNDI name %s", id = 184)
 	void noSessionFactoryWithJndiName(String sfJNDIName,
 									  @Cause NameNotFoundException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Optimistic lock failures: %s", id = 187)
 	void optimisticLockFailures(long optimisticFailureCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@OrderBy not allowed for an indexed collection, annotation ignored.", id = 189)
 	void orderByAnnotationIndexedCollection();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Overriding %s is dangerous, this might break the EJB3 specification implementation", id = 193)
 	void overridingTransactionStrategyDangerous(String transactionStrategy);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Package not found or wo package-info.java: %s", id = 194)
 	void packageNotFound(String packageName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Parameter position [%s] occurred as both JPA and Hibernate positional parameter", id = 195)
 	void parameterPositionOccurredAsBothJpaAndHibernatePositionalParameter(Integer position);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error parsing XML (%s) : %s", id = 196)
 	void parsingXmlError(int lineNumber,
 						 String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error parsing XML: %s(%s) %s", id = 197)
 	void parsingXmlErrorForFile(String file,
 								int lineNumber,
 								String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Warning parsing XML (%s) : %s", id = 198)
 	void parsingXmlWarning(int lineNumber,
 						   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Warning parsing XML: %s(%s) %s", id = 199)
 	void parsingXmlWarningForFile(String file,
 								  int lineNumber,
 								  String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Persistence provider caller does not implement the EJB3 spec correctly."
 			+ "PersistenceUnitInfo.getNewTempClassLoader() is null.", id = 200)
 	void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended",
 			id = 201)
 	void pooledOptimizerReportedInitialValue(IntegralDataTypeHolder value);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "PreparedStatement was already in the batch, [%s].", id = 202)
 	void preparedStatementAlreadyInBatch(String sql);
 
 	@LogMessage(level = WARN)
 	@Message(value = "processEqualityExpression() : No expression to process!", id = 203)
 	void processEqualityExpression();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Processing PersistenceUnitInfo [\n\tname: %s\n\t...]", id = 204)
 	void processingPersistenceUnitInfoName(String persistenceUnitName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Loaded properties from resource hibernate.properties: %s", id = 205)
 	void propertiesLoaded(Properties maskOut);
 
 	@LogMessage(level = INFO)
 	@Message(value = "hibernate.properties not found", id = 206)
 	void propertiesNotFound();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Property %s not found in class but described in <mapping-file/> (possible typo error)", id = 207)
 	void propertyNotFound(String property);
 
 	@LogMessage(level = WARN)
 	@Message(value = "%s has been deprecated in favor of %s; that provider will be used instead.", id = 208)
 	void providerClassDeprecated(String providerClassName,
 								 String actualProviderClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.",
 			id = 209)
 	void proxoolProviderClassNotFound(String proxoolProviderClassName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Queries executed to database: %s", id = 210)
 	void queriesExecuted(long queryExecutionCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache hits: %s", id = 213)
 	void queryCacheHits(long queryCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache misses: %s", id = 214)
 	void queryCacheMisses(long queryCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache puts: %s", id = 215)
 	void queryCachePuts(long queryCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "RDMSOS2200Dialect version: 1.0", id = 218)
 	void rdmsOs2200Dialect();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from cache file: %s", id = 219)
 	void readingCachedMappings(File cachedFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from file: %s", id = 220)
 	void readingMappingsFromFile(String path);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from resource: %s", id = 221)
 	void readingMappingsFromResource(String resourceName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "read-only cache configured for mutable collection [%s]", id = 222)
 	void readOnlyCacheConfiguredForMutableCollection(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!",
 			id = 223)
 	void recognizedObsoleteHibernateNamespace(String oldHibernateNamespace,
 											  String hibernateNamespace);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Property [%s] has been renamed to [%s]; update your properties appropriately", id = 225)
 	void renamedProperty(Object propertyName,
 						 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Required a different provider: %s", id = 226)
 	void requiredDifferentProvider(String provider);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema export", id = 227)
 	void runningHbm2ddlSchemaExport();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema update", id = 228)
 	void runningHbm2ddlSchemaUpdate();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running schema validator", id = 229)
 	void runningSchemaValidator();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema export complete", id = 230)
 	void schemaExportComplete();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Schema export unsuccessful", id = 231)
 	void schemaExportUnsuccessful(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema update complete", id = 232)
 	void schemaUpdateComplete();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Scoping types to session factory %s after already scoped %s", id = 233)
 	void scopingTypesToSessionFactoryAfterAlreadyScoped(SessionFactoryImplementor factory,
 														SessionFactoryImplementor factory2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Searching for mapping documents in jar: %s", id = 235)
 	void searchingForMappingDocuments(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache hits: %s", id = 237)
 	void secondLevelCacheHits(long secondLevelCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache misses: %s", id = 238)
 	void secondLevelCacheMisses(long secondLevelCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache puts: %s", id = 239)
 	void secondLevelCachePuts(long secondLevelCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Service properties: %s", id = 240)
 	void serviceProperties(Properties properties);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions closed: %s", id = 241)
 	void sessionsClosed(long sessionCloseCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions opened: %s", id = 242)
 	void sessionsOpened(long sessionOpenCount);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Setters of lazy classes cannot be final: %s.%s", id = 243)
 	void settersOfLazyClassesCannotBeFinal(String entityName,
 										   String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@Sort not allowed for an indexed collection, annotation ignored.", id = 244)
 	void sortAnnotationIndexedCollection();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Manipulation query [%s] resulted in [%s] split queries", id = 245)
 	void splitQueries(String sourceQuery,
 					  int length);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "SQLException escaped proxy", id = 246)
 	void sqlExceptionEscapedProxy(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "SQL Error: %s, SQLState: %s", id = 247)
 	void sqlWarning(int errorCode,
 					String sqlState);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting query cache at region: %s", id = 248)
 	void startingQueryCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting service at JNDI name: %s", id = 249)
 	void startingServiceAtJndiName(String boundName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting update timestamps cache at region: %s", id = 250)
 	void startingUpdateTimestampsCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Start time: %s", id = 251)
 	void startTime(long startTime);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements closed: %s", id = 252)
 	void statementsClosed(long closeStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements prepared: %s", id = 253)
 	void statementsPrepared(long prepareStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Stopping service", id = 255)
 	void stoppingService();
 
 	@LogMessage(level = INFO)
 	@Message(value = "sub-resolver threw unexpected exception, continuing to next : %s", id = 257)
 	void subResolverException(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Successful transactions: %s", id = 258)
 	void successfulTransactions(long committedTransactionCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Synchronization [%s] was already registered", id = 259)
 	void synchronizationAlreadyRegistered(Synchronization synchronization);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception calling user Synchronization [%s] : %s", id = 260)
 	void synchronizationFailed(Synchronization synchronization,
 							   Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table found: %s", id = 261)
 	void tableFound(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table not found: %s", id = 262)
 	void tableNotFound(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transactions: %s", id = 266)
 	void transactions(long transactionCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Transaction started on non-root session", id = 267)
 	void transactionStartedOnNonRootSession();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transaction strategy: %s", id = 268)
 	void transactionStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Type [%s] defined no registration keys; ignoring", id = 269)
 	void typeDefinedNoRegistrationKeys(BasicType type);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Type registration [%s] overrides previous : %s", id = 270)
 	void typeRegistrationOverridesPrevious(String key,
 										   Type old);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Naming exception occurred accessing Ejb3Configuration", id = 271)
 	void unableToAccessEjb3Configuration(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error while accessing session factory with JNDI name %s", id = 272)
 	void unableToAccessSessionFactory(String sfJNDIName,
 									  @Cause NamingException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error accessing type info result set : %s", id = 273)
 	void unableToAccessTypeInfoResultSet(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to apply constraints on DDL for %s", id = 274)
 	void unableToApplyConstraints(String className,
 								  @Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind Ejb3Configuration to JNDI", id = 276)
 	void unableToBindEjb3ConfigurationToJndi(@Cause JndiException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind factory to JNDI", id = 277)
 	void unableToBindFactoryToJndi(@Cause JndiException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not bind value '%s' to parameter: %s; %s", id = 278)
 	void unableToBindValueToParameter(String nullSafeToString,
 									  int index,
 									  String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to build enhancement metamodel for %s", id = 279)
 	void unableToBuildEnhancementMetamodel(String className);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s",
 			id = 280)
 	void unableToBuildSessionFactoryUsingMBeanClasspath(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up callable statement", id = 281)
 	void unableToCleanUpCallableStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up prepared statement", id = 282)
 	void unableToCleanUpPreparedStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to cleanup temporary id table after use [%s]", id = 283)
 	void unableToCleanupTemporaryIdTable(Throwable t);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing connection", id = 284)
 	void unableToCloseConnection(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error closing InitialContext [%s]", id = 285)
 	void unableToCloseInitialContext(String string);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing input files: %s", id = 286)
 	void unableToCloseInputFiles(String name,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream", id = 287)
 	void unableToCloseInputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream for %s", id = 288)
 	void unableToCloseInputStreamForResource(String resourceName,
 											 @Cause IOException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to close iterator", id = 289)
 	void unableToCloseIterator(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close jar: %s", id = 290)
 	void unableToCloseJar(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing output file: %s", id = 291)
 	void unableToCloseOutputFile(String outputFile,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing output stream", id = 292)
 	void unableToCloseOutputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Problem closing pooled connection", id = 293)
 	void unableToClosePooledConnection(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session", id = 294)
 	void unableToCloseSession(@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session during rollback", id = 295)
 	void unableToCloseSessionDuringRollback(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing stream", id = 296)
 	void unableToCloseStream(@Cause IOException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close stream on hibernate.properties: %s", id = 297)
 	void unableToCloseStreamError(IOException error);
 
 	@Message(value = "JTA commit failed", id = 298)
 	String unableToCommitJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema update", id = 299)
 	void unableToCompleteSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema validation", id = 300)
 	void unableToCompleteSchemaValidation(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to configure SQLExceptionConverter : %s", id = 301)
 	void unableToConfigureSqlExceptionConverter(HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to construct current session context [%s]", id = 302)
 	void unableToConstructCurrentSessionContext(String impl,
 												@Cause Throwable e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to construct instance of specified SQLExceptionConverter : %s", id = 303)
 	void unableToConstructSqlExceptionConverter(Throwable t);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not copy system properties, system properties will be ignored", id = 304)
 	void unableToCopySystemProperties();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not create proxy factory for:%s", id = 305)
 	void unableToCreateProxyFactory(String entityName,
 									@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error creating schema ", id = 306)
 	void unableToCreateSchema(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not deserialize cache file: %s : %s", id = 307)
 	void unableToDeserializeCache(String path,
 								  SerializationException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy cache: %s", id = 308)
 	void unableToDestroyCache(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy query cache: %s: %s", id = 309)
 	void unableToDestroyQueryCache(String region,
 								   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy update timestamps cache: %s: %s", id = 310)
 	void unableToDestroyUpdateTimestampsCache(String region,
 											  String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to determine lock mode value : %s -> %s", id = 311)
 	void unableToDetermineLockModeValue(String hintName,
 										Object value);
 
 	@Message(value = "Could not determine transaction status", id = 312)
 	String unableToDetermineTransactionStatus();
 
 	@Message(value = "Could not determine transaction status after commit", id = 313)
 	String unableToDetermineTransactionStatusAfterCommit();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to drop temporary id table after use [%s]", id = 314)
 	void unableToDropTemporaryIdTable(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception executing batch [%s]", id = 315)
 	void unableToExecuteBatch(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error executing resolver [%s] : %s", id = 316)
 	void unableToExecuteResolver(AbstractDialectResolver abstractDialectResolver,
 								 String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not find any META-INF/persistence.xml file in the classpath", id = 318)
 	void unableToFindPersistenceXmlInClasspath();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not get database metadata", id = 319)
 	void unableToGetDatabaseMetadata(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate configured schema name resolver [%s] %s", id = 320)
 	void unableToInstantiateConfiguredSchemaNameResolver(String resolverClassName,
 														 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to interpret specified optimizer [%s], falling back to noop", id = 321)
 	void unableToLocateCustomOptimizerClass(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate specified optimizer [%s], falling back to noop", id = 322)
 	void unableToInstantiateOptimizer(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate UUID generation strategy class : %s", id = 325)
 	void unableToInstantiateUuidGenerationStrategy(Exception ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Cannot join transaction: do not override %s", id = 326)
 	void unableToJoinTransaction(String transactionStrategy);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error performing load command : %s", id = 327)
 	void unableToLoadCommand(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to load/access derby driver class sysinfo to check versions : %s", id = 328)
 	void unableToLoadDerbyDriver(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Problem loading properties from hibernate.properties", id = 329)
 	void unableToLoadProperties();
 
 	@Message(value = "Unable to locate config file: %s", id = 330)
 	String unableToLocateConfigFile(String path);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate configured schema name resolver class [%s] %s", id = 331)
 	void unableToLocateConfiguredSchemaNameResolver(String resolverClassName,
 													String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate MBeanServer on JMX service shutdown", id = 332)
 	void unableToLocateMBeanServer();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not locate 'java.sql.NClob' class; assuming JDBC 3", id = 333)
 	void unableToLocateNClobClass();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate requested UUID generation strategy class : %s", id = 334)
 	void unableToLocateUuidGenerationStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to log SQLWarnings : %s", id = 335)
 	void unableToLogSqlWarnings(SQLException sqle);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not log warnings", id = 336)
 	void unableToLogWarnings(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on PersistenceException: ", id = 337)
 	void unableToMarkForRollbackOnPersistenceException(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on TransientObjectException: ", id = 338)
 	void unableToMarkForRollbackOnTransientObjectException(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata: %s", id = 339)
 	void unableToObjectConnectionMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata: %s", id = 340)
 	void unableToObjectConnectionToQueryMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata : %s", id = 341)
 	void unableToObtainConnectionMetadata(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata : %s", id = 342)
 	void unableToObtainConnectionToQueryMetadata(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not obtain initial context", id = 343)
 	void unableToObtainInitialContext(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not parse the package-level metadata [%s]", id = 344)
 	void unableToParseMetadata(String packageName);
 
 	@Message(value = "JDBC commit failed", id = 345)
 	String unableToPerformJdbcCommit();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error during managed flush [%s]", id = 346)
 	void unableToPerformManagedFlush(String message);
 
 	@Message(value = "Unable to query java.sql.DatabaseMetaData", id = 347)
 	String unableToQueryDatabaseMetadata();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to read class: %s", id = 348)
 	void unableToReadClass(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not read column value from result set: %s; %s", id = 349)
 	void unableToReadColumnValueFromResultSet(String name,
 											  String message);
 
 	@Message(value = "Could not read a hi value - you need to populate the table: %s", id = 350)
 	String unableToReadHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not read or init a hi value", id = 351)
 	void unableToReadOrInitHiValue(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to release batch statement...", id = 352)
 	void unableToReleaseBatchStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not release a cache lock : %s", id = 353)
 	void unableToReleaseCacheLock(CacheException ce);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release initial context: %s", id = 354)
 	void unableToReleaseContext(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release created MBeanServer : %s", id = 355)
 	void unableToReleaseCreatedMBeanServer(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release isolated connection [%s]", id = 356)
 	void unableToReleaseIsolatedConnection(Throwable ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release type info result set", id = 357)
 	void unableToReleaseTypeInfoResultSet();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to erase previously added bag join fetch", id = 358)
 	void unableToRemoveBagJoinFetch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not resolve aggregate function [%s]; using standard definition", id = 359)
 	void unableToResolveAggregateFunction(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to resolve mapping file [%s]", id = 360)
 	void unableToResolveMappingFile(String xmlFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to retreive cache from JNDI [%s]: %s", id = 361)
 	void unableToRetrieveCache(String namespace,
 							   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to retrieve type info result set : %s", id = 362)
 	void unableToRetrieveTypeInfoResultSet(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback connection on exception [%s]", id = 363)
 	void unableToRollbackConnection(Exception ignore);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback isolated transaction on error [%s] : [%s]", id = 364)
 	void unableToRollbackIsolatedTransaction(Exception e,
 											 Exception ignore);
 
 	@Message(value = "JTA rollback failed", id = 365)
 	String unableToRollbackJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error running schema update", id = 366)
 	void unableToRunSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not set transaction to rollback only", id = 367)
 	void unableToSetTransactionToRollbackOnly(@Cause SystemException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while stopping service", id = 368)
 	void unableToStopHibernateService(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error stopping service [%s] : %s", id = 369)
 	void unableToStopService(Class class1,
 							 String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]",
 			id = 370)
 	void unableToSwitchToMethodUsingColumnIndex(Method method);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not synchronize database state with session: %s", id = 371)
 	void unableToSynchronizeDatabaseStateWithSession(HibernateException he);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not toggle autocommit", id = 372)
 	void unableToToggleAutoCommit(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to transform class: %s", id = 373)
 	void unableToTransformClass(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not unbind factory from JNDI", id = 374)
 	void unableToUnbindFactoryFromJndi(@Cause JndiException e);
 
 	@Message(value = "Could not update hi value in: %s", id = 375)
 	Object unableToUpdateHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not updateQuery hi value in: %s", id = 376)
 	void unableToUpdateQueryHiValue(String tableName,
 									@Cause SQLException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error wrapping result set", id = 377)
 	void unableToWrapResultSet(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "I/O reported error writing cached file : %s: %s", id = 378)
 	void unableToWriteCachedFile(String path,
 								 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unexpected literal token type [%s] passed for numeric processing", id = 380)
 	void unexpectedLiteralTokenType(int type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "JDBC driver did not return the expected number of row counts", id = 381)
 	void unexpectedRowCounts();
 
 	@LogMessage(level = WARN)
 	@Message(value = "unrecognized bytecode provider [%s], using javassist by default", id = 382)
 	void unknownBytecodeProvider(String providerName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Ingres major version [%s]; using Ingres 9.2 dialect", id = 383)
 	void unknownIngresVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Oracle major version [%s]", id = 384)
 	void unknownOracleVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Microsoft SQL Server major version [%s] using SQL Server 2000 dialect", id = 385)
 	void unknownSqlServerVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "ResultSet had no statement associated with it, but was not yet registered", id = 386)
 	void unregisteredResultSetWithoutStatement();
 
 	@LogMessage(level = WARN)
 	@Message(value = "ResultSet's statement was not registered", id = 387)
 	void unregisteredStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 388)
 	void unsuccessful(String sql);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 389)
 	void unsuccessfulCreate(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Overriding release mode as connection provider does not support 'after_statement'", id = 390)
 	void unsupportedAfterStatement();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ingres 10 is not yet fully supported; using Ingres 9.3 dialect", id = 391)
 	void unsupportedIngresVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Hibernate does not support SequenceGenerator.initialValue() unless '%s' set", id = 392)
 	void unsupportedInitialValue(String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly",
 			id = 393)
 	void unsupportedMultiTableBulkHqlJpaql(int majorVersion,
 										   int minorVersion,
 										   int buildId);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Oracle 11g is not yet fully supported; using Oracle 10g dialect", id = 394)
 	void unsupportedOracleVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Usage of obsolete property: %s no longer supported, use: %s", id = 395)
 	void unsupportedProperty(Object propertyName,
 							 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Updating schema", id = 396)
 	void updatingSchema();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using ASTQueryTranslatorFactory", id = 397)
 	void usingAstQueryTranslatorFactory();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Explicit segment value for id generator [%s.%s] suggested; using default [%s]", id = 398)
 	void usingDefaultIdGeneratorSegmentValue(String tableName,
 											 String segmentColumnName,
 											 String defaultToUse);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using default transaction strategy (direct JDBC transactions)", id = 399)
 	void usingDefaultTransactionStrategy();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using dialect: %s", id = 400)
 	void usingDialect(Dialect dialect);
 
 	@LogMessage(level = INFO)
 	@Message(value = "using driver [%s] at URL [%s]", id = 401)
 	void usingDriver(String driverClassName,
 					 String url);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using Hibernate built-in connection pool (not for production use!)", id = 402)
 	void usingHibernateBuiltInConnectionPool();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Don't use old DTDs, read the Hibernate 3.x Migration Guide!", id = 404)
 	void usingOldDtd();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using bytecode reflection optimizer", id = 406)
 	void usingReflectionOptimizer();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using java.io streams to persist binary types", id = 407)
 	void usingStreams();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using workaround for JVM bug in java.sql.Timestamp", id = 408)
 	void usingTimestampWorkaround();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead",
 			id = 409)
 	void usingUuidHexGenerator(String name,
 							   String name2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Validator not found: ignoring", id = 410)
 	void validatorNotFound();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Core {%s}", id = 412)
 	void version(String versionString);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Warnings creating temp table : %s", id = 413)
 	void warningsCreatingTempTable(SQLWarning warning);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.",
 			id = 414)
 	void willNotRegisterListeners();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Write locks via update not supported for non-versioned entities [%s]", id = 416)
 	void writeLocksNotSupported(String entityName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Writing generated schema to file: %s", id = 417)
 	void writingGeneratedSchemaToFile(String outputFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Adding override for %s: %s", id = 418)
 	void addingOverrideFor(String name,
 						   String name2);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s",
 			id = 419)
 	void resolvedSqlTypeDescriptorForDifferentSqlCode(String name,
 													  String valueOf,
 													  String name2,
 													  String valueOf2);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Closing un-released batch", id = 420)
 	void closingUnreleasedBatch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as %s is true", id = 421)
 	void disablingContextualLOBCreation(String nonContextualLobCreation);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as connection was null", id = 422)
 	void disablingContextualLOBCreationSinceConnectionNull();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4",
 			id = 423)
 	void disablingContextualLOBCreationSinceOldJdbcVersion(int jdbcMajorVersion);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as createClob() method threw error : %s", id = 424)
 	void disablingContextualLOBCreationSinceCreateClobFailed(Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not close session; swallowing exception[%s] as transaction completed", id = 425)
 	void unableToCloseSessionButSwallowingError(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "You should set hibernate.transaction.manager_lookup_class if cache is enabled", id = 426)
 	void setManagerLookupClass();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Using deprecated %s strategy [%s], use newer %s strategy instead [%s]", id = 427)
 	void deprecatedTransactionManagerStrategy(String name,
 											  String transactionManagerStrategy,
 											  String name2,
 											  String jtaPlatform);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting",
 			id = 428)
 	void legacyTransactionManagerStrategy(String name,
 										  String jtaPlatform);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Setting entity-identifier value binding where one already existed : %s.", id = 429)
 	void entityIdentifierValueBindingExists(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead",
 			id = 430)
 	void deprecatedDerbyDialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to determine H2 database version, certain features may not work", id = 431)
 	void undeterminedH2Version();
 
 	@LogMessage(level = WARN)
 	@Message(value = "There were not column names specified for index %s on table %s", id = 432)
 	void noColumnsSpecifiedForIndex(String indexName, String tableName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache puts: %s", id = 433)
 	void timestampCachePuts(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache hits: %s", id = 434)
 	void timestampCacheHits(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache misses: %s", id = 435)
 	void timestampCacheMisses(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity manager factory name (%s) is already registered.  If entity manager will be clustered "+
 			"or passivated, specify a unique value for property '%s'", id = 436)
 	void entityManagerFactoryAlreadyRegistered(String emfName, String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n" +
 			"\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)" , id = 437)
 	void cannotResolveNonNullableTransientDependencies(String transientEntityString,
 													   Set<String> dependentEntityStrings,
 													   Set<String> nonNullableAssociationPaths);
+
+	@LogMessage(level = INFO)
+	@Message(value = "NaturalId cache puts: %s", id = 438)
+	void naturalIdCachePuts(long naturalIdCachePutCount);
+
+	@LogMessage(level = INFO)
+	@Message(value = "NaturalId cache hits: %s", id = 439)
+	void naturalIdCacheHits(long naturalIdCacheHitCount);
+
+	@LogMessage(level = INFO)
+	@Message(value = "NaturalId cache misses: %s", id = 440)
+	void naturalIdCacheMisses(long naturalIdCacheMissCount);
+
+	@LogMessage(level = INFO)
+	@Message(value = "Max NaturalId query time: %sms", id = 441)
+	void naturalIdMaxQueryTime(long naturalIdQueryExecutionMaxTime);
+	
+	@LogMessage(level = INFO)
+	@Message(value = "NaturalId queries executed to database: %s", id = 442)
+	void naturalIdQueriesExecuted(long naturalIdQueriesExecutionCount);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 632deefc5a..20715b2ee0 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,2425 +1,2492 @@
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
+import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
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
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.criterion.NaturalIdentifier;
+import org.hibernate.criterion.Restrictions;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
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
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
+import org.hibernate.internal.CriteriaImpl.CriterionEntry;
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
 			catch (IOException ignore) {
 			}
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
 				catch( IOException ignore ) {
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
 			catch (IOException ignore) {
 			}
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
 		LOG.debug( "Disconnecting session" );
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
 		LOG.debug( "Reconnecting session" );
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
 
 	private void checkNoUnresolvedActionsBeforeOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 && actionQueue.hasUnresolvedEntityInsertActions() ) {
 			throw new IllegalStateException( "There are delayed insert actions before operation as cascade level 0." );
 		}
 	}
 
 	private void checkNoUnresolvedActionsAfterOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 ) {
 			actionQueue.checkNoUnresolvedActionsAfterOperation();
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
 
 	public Serializable save(Object obj) throws HibernateException {
 		return save( null, obj );
 	}
 
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
 
 	public void update(Object obj) throws HibernateException {
 		update(null, obj);
 	}
 
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
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
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
 		return this.byId( entityClass ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).getReference( id );
 	}
 
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).load( id );
 	}
 
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).load( id );
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
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).getReference( id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).load( id );
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
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
 	}
 
 	private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
 			listener.onResolveNaturalId( event );
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
 		fireRefresh( new RefreshEvent( object, lockMode, this ) );
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
 		LOG.debug( "Checking session dirtiness" );
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
 			LOG.debug( "Session dirty (scheduled updates and insertions)" );
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
 		FlushEvent flushEvent = new FlushEvent( this );
 		for ( FlushEventListener listener : listeners( EventType.FLUSH ) ) {
 			listener.onFlush( flushEvent );
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
+	
+	/**
+	 * Checks to see if the CriteriaImpl is a naturalId lookup that can be done via
+	 * NaturalIdLoadAccess
+	 * 
+	 * @return A fully configured NaturalIdLoadAccess or null, if null is returned the standard CriteriaImpl execution
+	 *         should be performed
+	 */
+	private NaturalIdLoadAccess tryNaturalIdLoadAccess(CriteriaImpl criteria) {
+		// See if the criteria lookup is by naturalId
+		if ( !criteria.isLookupByNaturalKey() ) {
+			return null;
+		}
+		
+		final String entityName = criteria.getEntityOrClassName();
+		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
+
+		// Verify the entity actually has a natural id, needed for legacy support as NaturalIdentifier criteria
+		// queries did no natural id validation
+		if ( !entityPersister.hasNaturalIdentifier() ) {
+			return null;
+		}
+		
+		// Since isLookupByNaturalKey is true there can be only one CriterionEntry and getCriterion() will 
+		// return an instanceof NaturalIdentifier
+		final CriterionEntry criterionEntry = (CriterionEntry) criteria.iterateExpressionEntries().next();
+		final NaturalIdentifier naturalIdentifier = (NaturalIdentifier) criterionEntry.getCriterion();
+
+		final Map<String, Object> naturalIdValues = naturalIdentifier.getNaturalIdValues();
+		final int[] naturalIdentifierProperties = entityPersister.getNaturalIdentifierProperties();
+
+		// Verify the NaturalIdentifier criterion includes all naturalId properties, first check that the property counts match
+		if ( naturalIdentifierProperties.length != naturalIdValues.size() ) {
+			return null;
+		}
+
+		final String[] propertyNames = entityPersister.getPropertyNames();
+		final NaturalIdLoadAccess naturalIdLoader = this.byNaturalId( entityName );
+
+		// Build NaturalIdLoadAccess and in the process verify all naturalId properties were specified
+		for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
+			final String naturalIdProperty = propertyNames[naturalIdentifierProperties[i]];
+			final Object naturalIdValue = naturalIdValues.get( naturalIdProperty );
+
+			if ( naturalIdValue == null ) {
+				// A NaturalId property is missing from the critera query, can't use NaturalIdLoadAccess
+				return null;
+			}
+
+			naturalIdLoader.using( naturalIdProperty, naturalIdValue );
+		}
+
+		// Critera query contains a valid naturalId, use the new API
+		LOG.warn( "Session.byNaturalId(" + entityName
+				+ ") should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria" );
+
+		return naturalIdLoader;
+	}
 
 	public List list(CriteriaImpl criteria) throws HibernateException {
+		final NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess( criteria );
+		if ( naturalIdLoadAccess != null ) {
+			return Arrays.asList( naturalIdLoadAccess.load() );
+		}
+
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
 		persistenceContext.setReadOnly( entity, readOnly );
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
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
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
 
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode(lockMode);
 			return this;
 		}
 
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut(timeout);
 			return this;
 		}
 
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope(scope);
 			return this;
 		}
 
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
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
 
 	private class NaturalIdLoadAccessImpl implements NaturalIdLoadAccess {
 		private final EntityPersister entityPersister;
 		private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
 		private LockOptions lockOptions;
 
 		private NaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 
 			if ( ! entityPersister.hasNaturalIdentifier() ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a natural id", entityPersister.getEntityName() )
 				);
 			}
 		}
 
 		private NaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private NaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		@Override
 		public final NaturalIdLoadAccess with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
 		public NaturalIdLoadAccess using(String attributeName, Object value) {
 			naturalIdParameters.put( attributeName, value );
 			return this;
 		}
 
 		protected Serializable resolveNaturalId() {
 			final ResolveNaturalIdEvent event =
 					new ResolveNaturalIdEvent( naturalIdParameters, entityPersister, SessionImpl.this );
 			fireResolveNaturalId( event );
 			return event.getEntityId();
 		}
 
 		protected IdentifierLoadAccess getIdentifierLoadAccess() {
 			final IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl( entityPersister );
 			if ( this.lockOptions != null ) {
 				identifierLoadAccess.with( lockOptions );
 			}
 			return identifierLoadAccess;
 		}
 
 		@Override
 		public final Object getReference() {
 			final Serializable entityId = resolveNaturalId();
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public final Object load() {
 			final Serializable entityId = resolveNaturalId();
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().load( entityId );
 		}
 	}
 
 	private class SimpleNaturalIdLoadAccessImpl implements SimpleNaturalIdLoadAccess {
 		private final EntityPersister entityPersister;
 		private final String naturalIdAttributeName;
 		private LockOptions lockOptions;
 
 		private SimpleNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 
 			if ( ! entityPersister.hasNaturalIdentifier() ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a natural id", entityPersister.getEntityName() )
 				);
 			}
 
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
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
 		public Object getReference(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( naturalIdValue );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public Object load(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( naturalIdValue );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().load( entityId );
 		}
 
 		private Serializable resolveNaturalId(Object naturalIdValue) {
 			final Map<String,Object> naturalIdValueMap = Collections.singletonMap( naturalIdAttributeName, naturalIdValue );
 			final ResolveNaturalIdEvent event =
 					new ResolveNaturalIdEvent( naturalIdValueMap, entityPersister, SessionImpl.this );
 			fireResolveNaturalId( event );
 			return event.getEntityId();
 		}
 
 		private IdentifierLoadAccess getIdentifierLoadAccess() {
 			final IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl( entityPersister );
 			if ( this.lockOptions != null ) {
 				identifierLoadAccess.with( lockOptions );
 			}
 			return identifierLoadAccess;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
index cba1ef5466..2895997b41 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
@@ -1,352 +1,367 @@
 package org.hibernate.jmx;
 
 import javax.naming.InitialContext;
 import javax.naming.NameNotFoundException;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryRegistry;
 import org.hibernate.stat.CollectionStatistics;
 import org.hibernate.stat.EntityStatistics;
 import org.hibernate.stat.NaturalIdCacheStatistics;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
 
 /**
  * JMX service for Hibernate statistics<br>
  * <br>
  * Register this MBean in your JMX server for a specific session factory
  * <pre>
  * //build the ObjectName you want
  * Hashtable tb = new Hashtable();
  * tb.put("type", "statistics");
  * tb.put("sessionFactory", "myFinancialApp");
  * ObjectName on = new ObjectName("hibernate", tb);
  * StatisticsService stats = new StatisticsService();
  * stats.setSessionFactory(sessionFactory);
  * server.registerMBean(stats, on);
  * </pre>
  * And call the MBean the way you want<br>
  * <br>
  * Register this MBean in your JMX server with no specific session factory
  * <pre>
  * //build the ObjectName you want
  * Hashtable tb = new Hashtable();
  * tb.put("type", "statistics");
  * tb.put("sessionFactory", "myFinancialApp");
  * ObjectName on = new ObjectName("hibernate", tb);
  * StatisticsService stats = new StatisticsService();
  * server.registerMBean(stats, on);
  * </pre>
  * And call the MBean by providing the <code>SessionFactoryJNDIName</code> first.
  * Then the session factory will be retrieved from JNDI and the statistics
  * loaded.
  *
  * @author Emmanuel Bernard
  * @deprecated See <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6190">HHH-6190</a> for details
  */
 @Deprecated
 public class StatisticsService implements StatisticsServiceMBean {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatisticsService.class.getName() );
 	//TODO: We probably should have a StatisticsNotPublishedException, to make it clean
 
 	SessionFactory sf;
 	String sfJNDIName;
 	Statistics stats = new ConcurrentStatisticsImpl();
 
 	/**
 	 * @see StatisticsServiceMBean#setSessionFactoryJNDIName(java.lang.String)
 	 */
 	public void setSessionFactoryJNDIName(String sfJNDIName) {
 		this.sfJNDIName = sfJNDIName;
 		try {
 			final SessionFactory sessionFactory;
 			final Object jndiValue = new InitialContext().lookup( sfJNDIName );
 			if ( jndiValue instanceof Reference ) {
 				final String uuid = (String) ( (Reference) jndiValue ).get( 0 ).getContent();
 				sessionFactory = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 			}
 			else {
 				sessionFactory = (SessionFactory) jndiValue;
 			}
 			setSessionFactory( sessionFactory );
 		}
 		catch (NameNotFoundException e) {
 			LOG.noSessionFactoryWithJndiName( sfJNDIName, e );
 			setSessionFactory(null);
 		}
 		catch (NamingException e) {
 			LOG.unableToAccessSessionFactory( sfJNDIName, e );
 			setSessionFactory(null);
 		}
 		catch (ClassCastException e) {
 			LOG.jndiNameDoesNotHandleSessionFactoryReference( sfJNDIName, e );
 			setSessionFactory(null);
 		}
 	}
 
 	/**
 	 * Useful to init this MBean wo a JNDI session factory name
 	 *
 	 * @param sf session factory to register
 	 */
 	public void setSessionFactory(SessionFactory sf) {
 		this.sf = sf;
 		if (sf == null) {
 			stats = new ConcurrentStatisticsImpl();
 		}
 		else {
 			stats = sf.getStatistics();
 		}
 
 	}
 	/**
 	 * @see StatisticsServiceMBean#clear()
 	 */
 	public void clear() {
 		stats.clear();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityStatistics(java.lang.String)
 	 */
 	public EntityStatistics getEntityStatistics(String entityName) {
 		return stats.getEntityStatistics(entityName);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionStatistics(java.lang.String)
 	 */
 	public CollectionStatistics getCollectionStatistics(String role) {
 		return stats.getCollectionStatistics(role);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheStatistics(java.lang.String)
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
 		return stats.getSecondLevelCacheStatistics(regionName);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getQueryStatistics(java.lang.String)
 	 */
 	public QueryStatistics getQueryStatistics(String hql) {
 		return stats.getQueryStatistics(hql);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityDeleteCount()
 	 */
 	public long getEntityDeleteCount() {
 		return stats.getEntityDeleteCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityInsertCount()
 	 */
 	public long getEntityInsertCount() {
 		return stats.getEntityInsertCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityLoadCount()
 	 */
 	public long getEntityLoadCount() {
 		return stats.getEntityLoadCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityFetchCount()
 	 */
 	public long getEntityFetchCount() {
 		return stats.getEntityFetchCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityUpdateCount()
 	 */
 	public long getEntityUpdateCount() {
 		return stats.getEntityUpdateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getQueryExecutionCount()
 	 */
 	public long getQueryExecutionCount() {
 		return stats.getQueryExecutionCount();
 	}
 	public long getQueryCacheHitCount() {
 		return stats.getQueryCacheHitCount();
 	}
 	public long getQueryExecutionMaxTime() {
 		return stats.getQueryExecutionMaxTime();
 	}
 	public long getQueryCacheMissCount() {
 		return stats.getQueryCacheMissCount();
 	}
 	public long getQueryCachePutCount() {
 		return stats.getQueryCachePutCount();
 	}
 
 	public long getUpdateTimestampsCacheHitCount() {
 		return stats.getUpdateTimestampsCacheHitCount();
 	}
 
 	public long getUpdateTimestampsCacheMissCount() {
 		return stats.getUpdateTimestampsCacheMissCount();
 	}
 
 	public long getUpdateTimestampsCachePutCount() {
 		return stats.getUpdateTimestampsCachePutCount();
 	}
 
 	/**
 	 * @see StatisticsServiceMBean#getFlushCount()
 	 */
 	public long getFlushCount() {
 		return stats.getFlushCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getConnectCount()
 	 */
 	public long getConnectCount() {
 		return stats.getConnectCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheHitCount()
 	 */
 	public long getSecondLevelCacheHitCount() {
 		return stats.getSecondLevelCacheHitCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheMissCount()
 	 */
 	public long getSecondLevelCacheMissCount() {
 		return stats.getSecondLevelCacheMissCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCachePutCount()
 	 */
 	public long getSecondLevelCachePutCount() {
 		return stats.getSecondLevelCachePutCount();
 	}
 		
 	public NaturalIdCacheStatistics getNaturalIdCacheStatistics(String regionName) {
 		return stats.getNaturalIdCacheStatistics( regionName );
 	}
 
 	public long getNaturalIdCacheHitCount() {
 		return stats.getNaturalIdCacheHitCount();
 	}
 
 	public long getNaturalIdCacheMissCount() {
 		return stats.getNaturalIdCacheMissCount();
 	}
 
 	public long getNaturalIdCachePutCount() {
 		return stats.getNaturalIdCachePutCount();
 	}
+	
+	@Override
+	public long getNaturalIdQueryExecutionCount() {
+		return stats.getNaturalIdQueryExecutionCount();
+	}
+
+	@Override
+	public long getNaturalIdQueryExecutionMaxTime() {
+		return stats.getNaturalIdQueryExecutionMaxTime();
+	}
+
+	@Override
+	public String getNaturalIdQueryExecutionMaxTimeRegion() {
+		return stats.getNaturalIdQueryExecutionMaxTimeRegion();
+	}
 
 	/**
 	 * @see StatisticsServiceMBean#getSessionCloseCount()
 	 */
 	public long getSessionCloseCount() {
 		return stats.getSessionCloseCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSessionOpenCount()
 	 */
 	public long getSessionOpenCount() {
 		return stats.getSessionOpenCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionLoadCount()
 	 */
 	public long getCollectionLoadCount() {
 		return stats.getCollectionLoadCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionFetchCount()
 	 */
 	public long getCollectionFetchCount() {
 		return stats.getCollectionFetchCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionUpdateCount()
 	 */
 	public long getCollectionUpdateCount() {
 		return stats.getCollectionUpdateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionRemoveCount()
 	 */
 	public long getCollectionRemoveCount() {
 		return stats.getCollectionRemoveCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionRecreateCount()
 	 */
 	public long getCollectionRecreateCount() {
 		return stats.getCollectionRecreateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getStartTime()
 	 */
 	public long getStartTime() {
 		return stats.getStartTime();
 	}
 
 	/**
 	 * @see StatisticsServiceMBean#isStatisticsEnabled()
 	 */
 	public boolean isStatisticsEnabled() {
 		return stats.isStatisticsEnabled();
 	}
 
 	/**
 	 * @see StatisticsServiceMBean#setStatisticsEnabled(boolean)
 	 */
 	public void setStatisticsEnabled(boolean enable) {
 		stats.setStatisticsEnabled(enable);
 	}
 
 	public void logSummary() {
 		stats.logSummary();
 	}
 
 	public String[] getCollectionRoleNames() {
 		return stats.getCollectionRoleNames();
 	}
 
 	public String[] getEntityNames() {
 		return stats.getEntityNames();
 	}
 
 	public String[] getQueries() {
 		return stats.getQueries();
 	}
 
 	public String[] getSecondLevelCacheRegionNames() {
 		return stats.getSecondLevelCacheRegionNames();
 	}
 
 	public long getSuccessfulTransactionCount() {
 		return stats.getSuccessfulTransactionCount();
 	}
 	public long getTransactionCount() {
 		return stats.getTransactionCount();
 	}
 
 	public long getCloseStatementCount() {
 		return stats.getCloseStatementCount();
 	}
 	public long getPrepareStatementCount() {
 		return stats.getPrepareStatementCount();
 	}
 
 	public long getOptimisticFailureCount() {
 		return stats.getOptimisticFailureCount();
 	}
 
 	public String getQueryExecutionMaxTimeQueryString() {
 		return stats.getQueryExecutionMaxTimeQueryString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/NaturalIdCacheStatistics.java b/hibernate-core/src/main/java/org/hibernate/stat/NaturalIdCacheStatistics.java
index f0eb35dbc9..cfc10f3bbc 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/NaturalIdCacheStatistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/NaturalIdCacheStatistics.java
@@ -1,50 +1,58 @@
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
 package org.hibernate.stat;
 
 import java.io.Serializable;
 import java.util.Map;
 
 /**
  * NaturalId query statistics
  * <p/>
  * Note that for a cached natural id, the cache miss is equals to the db count
  *
  * @author Eric Dalquist
  */
 public interface NaturalIdCacheStatistics extends Serializable {
 	long getHitCount();
 
 	long getMissCount();
 
 	long getPutCount();
+	
+	long getExecutionCount();
+	
+	long getExecutionAvgTime();
+	
+	long getExecutionMaxTime();
+	
+	long getExecutionMinTime();
 
 	long getElementCountInMemory();
 
 	long getElementCountOnDisk();
 
 	long getSizeInMemory();
 
 	Map getEntries();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java b/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
index 33cd475080..f870e18f17 100755
--- a/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/Statistics.java
@@ -1,263 +1,275 @@
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
 package org.hibernate.stat;
 
 /**
  * Exposes statistics for a particular {@link org.hibernate.SessionFactory}.  Beware of milliseconds metrics, they
  * are dependent of the JVM precision: you may then encounter a 10 ms approximation depending on you OS platform.
  * Please refer to the JVM documentation for more information.
  * 
  * @author Emmanuel Bernard
  */
 public interface Statistics {
 	/**
 	 * reset all statistics
 	 */
 	public void clear();
 
     /**
 	 * find entity statistics per name
 	 * 
 	 * @param entityName entity name
 	 * @return EntityStatistics object
 	 */
 	public EntityStatistics getEntityStatistics(String entityName);
 	/**
 	 * Get collection statistics per role
 	 * 
 	 * @param role collection role
 	 * @return CollectionStatistics
 	 */
 	public CollectionStatistics getCollectionStatistics(String role);
 
     /**
 	 * Second level cache statistics per region
 	 * 
 	 * @param regionName region name
 	 * @return SecondLevelCacheStatistics
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName);
 
     /**
 	 * Natural id cache statistics per region
 	 * 
 	 * @param regionName region name
 	 * @return NaturalIdCacheStatistics
 	 */
 	public NaturalIdCacheStatistics getNaturalIdCacheStatistics(String regionName);
 
     /**
 	 * Query statistics from query string (HQL or SQL)
 	 * 
 	 * @param queryString query string
 	 * @return QueryStatistics
 	 */
 	public QueryStatistics getQueryStatistics(String queryString);
 
     /**
      * Get global number of entity deletes
 	 * @return entity deletion count
 	 */
 	public long getEntityDeleteCount();
 
     /**
      * Get global number of entity inserts
 	 * @return entity insertion count
 	 */
 	public long getEntityInsertCount();
 
     /**
      * Get global number of entity loads
 	 * @return entity load (from DB)
 	 */
 	public long getEntityLoadCount();
 	/**
      * Get global number of entity fetchs
 	 * @return entity fetch (from DB)
 	 */
 	public long getEntityFetchCount();
 
 	/**
      * Get global number of entity updates
 	 * @return entity update
 	 */
 	public long getEntityUpdateCount();
 
     /**
      * Get global number of executed queries
 	 * @return query execution count
 	 */
 	public long getQueryExecutionCount();
 
     /**
      * Get the time in milliseconds of the slowest query.
      */
 	public long getQueryExecutionMaxTime();
 	/**
 	 * Get the query string for the slowest query.
 	 */
 	public String getQueryExecutionMaxTimeQueryString();
 
     /**
      * Get the global number of cached queries successfully retrieved from cache
      */
 	public long getQueryCacheHitCount();
     /**
      * Get the global number of cached queries *not* found in cache
      */
 	public long getQueryCacheMissCount();
     /**
      * Get the global number of cacheable queries put in cache
      */
 	public long getQueryCachePutCount();
+	/**
+	 * Get the global number of naturalId queries executed against the database
+	 */
+	public long getNaturalIdQueryExecutionCount();
+	/**
+	 * Get the global maximum query time for naturalId queries executed against the database
+	 */
+	public long getNaturalIdQueryExecutionMaxTime();
+	/**
+	 * Get the region for the maximum naturalId query time 
+	 */
+	public String getNaturalIdQueryExecutionMaxTimeRegion();
     /**
      * Get the global number of cached naturalId lookups successfully retrieved from cache
      */
 	public long getNaturalIdCacheHitCount();
     /**
      * Get the global number of cached naturalId lookups *not* found in cache
      */
 	public long getNaturalIdCacheMissCount();
     /**
      * Get the global number of cacheable naturalId lookups put in cache
      */
 	public long getNaturalIdCachePutCount();
     /**
      * Get the global number of timestamps successfully retrieved from cache
      */
 	public long getUpdateTimestampsCacheHitCount();
     /**
      * Get the global number of tables for which no update timestamps was *not* found in cache
      */
 	public long getUpdateTimestampsCacheMissCount();
     /**
      * Get the global number of timestamps put in cache
      */
 	public long getUpdateTimestampsCachePutCount();
 	/**
      * Get the global number of flush executed by sessions (either implicit or explicit)
      */
 	public long getFlushCount();
 	/**
 	 * Get the global number of connections asked by the sessions
      * (the actual number of connections used may be much smaller depending
      * whether you use a connection pool or not)
 	 */
 	public long getConnectCount();
 	/**
      * Global number of cacheable entities/collections successfully retrieved from the cache
      */
 	public long getSecondLevelCacheHitCount();
 	/**
      * Global number of cacheable entities/collections not found in the cache and loaded from the database.
      */
 	public long getSecondLevelCacheMissCount();
 	/**
 	 * Global number of cacheable entities/collections put in the cache
 	 */
 	public long getSecondLevelCachePutCount();
 	/**
 	 * Global number of sessions closed
 	 */
 	public long getSessionCloseCount();
 	/**
 	 * Global number of sessions opened
 	 */
 	public long getSessionOpenCount();
 	/**
 	 * Global number of collections loaded
 	 */
 	public long getCollectionLoadCount();
 	/**
 	 * Global number of collections fetched
 	 */
 	public long getCollectionFetchCount();
 	/**
 	 * Global number of collections updated
 	 */
 	public long getCollectionUpdateCount();
 	/**
 	 * Global number of collections removed
 	 */
     //even on inverse="true"
 	public long getCollectionRemoveCount();
 	/**
 	 * Global number of collections recreated
 	 */
 	public long getCollectionRecreateCount();
 	/**
 	 * @return start time in ms (JVM standards {@link System#currentTimeMillis()})
 	 */
 	public long getStartTime();
 	/**
 	 * log in info level the main statistics
 	 */
 	public void logSummary();
 	/**
 	 * Are statistics logged
 	 */
 	public boolean isStatisticsEnabled();
 	/**
 	 * Enable statistics logs (this is a dynamic parameter)
 	 */
 	public void setStatisticsEnabled(boolean b);
 
 	/**
 	 * Get all executed query strings
 	 */
 	public String[] getQueries();
 	/**
 	 * Get the names of all entities
 	 */
 	public String[] getEntityNames();
 	/**
 	 * Get the names of all collection roles
 	 */
 	public String[] getCollectionRoleNames();
 	/**
 	 * Get all second-level cache region names
 	 */
 	public String[] getSecondLevelCacheRegionNames();
 	/**
 	 * The number of transactions we know to have been successful
 	 */
 	public long getSuccessfulTransactionCount();
 	/**
 	 * The number of transactions we know to have completed
 	 */
 	public long getTransactionCount();
 	/**
 	 * The number of prepared statements that were acquired
 	 */
 	public long getPrepareStatementCount();
 	/**
 	 * The number of prepared statements that were released
 	 */
 	public long getCloseStatementCount();
 	/**
 	 * The number of <tt>StaleObjectStateException</tt>s 
 	 * that occurred
 	 */
 	public long getOptimisticFailureCount();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java
index e60d456bb0..bf84b3f21c 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java
@@ -1,112 +1,201 @@
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
 package org.hibernate.stat.internal;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicLong;
+import java.util.concurrent.locks.Lock;
+import java.util.concurrent.locks.ReadWriteLock;
+import java.util.concurrent.locks.ReentrantReadWriteLock;
 
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.stat.NaturalIdCacheStatistics;
 
 /**
- * Second level cache statistics of a specific region
- *
- * @author Alex Snaps
+ * NaturalId cache statistics of a specific region
+ * 
+ * @author Eric Dalquist
  */
 public class ConcurrentNaturalIdCacheStatisticsImpl extends CategorizedStatistics implements NaturalIdCacheStatistics {
+	private static final long serialVersionUID = 1L;
 	private final transient Region region;
-	private AtomicLong hitCount = new AtomicLong();
-	private AtomicLong missCount = new AtomicLong();
-	private AtomicLong putCount = new AtomicLong();
+	private final AtomicLong hitCount = new AtomicLong();
+	private final AtomicLong missCount = new AtomicLong();
+	private final AtomicLong putCount = new AtomicLong();
+	private final AtomicLong executionCount = new AtomicLong();
+	private final AtomicLong executionMaxTime = new AtomicLong();
+	private final AtomicLong executionMinTime = new AtomicLong( Long.MAX_VALUE );
+	private final AtomicLong totalExecutionTime = new AtomicLong();
+
+	private final Lock readLock;
+	private final Lock writeLock;
+	{
+		final ReadWriteLock lock = new ReentrantReadWriteLock();
+		this.readLock = lock.readLock();
+		this.writeLock = lock.writeLock();
+	}
 
 	ConcurrentNaturalIdCacheStatisticsImpl(Region region) {
 		super( region.getName() );
 		this.region = region;
 	}
 
+	@Override
 	public long getHitCount() {
-		return hitCount.get();
+		return this.hitCount.get();
 	}
 
+	@Override
 	public long getMissCount() {
-		return missCount.get();
+		return this.missCount.get();
 	}
 
+	@Override
 	public long getPutCount() {
-		return putCount.get();
+		return this.putCount.get();
+	}
+
+	/**
+	 * queries executed to the DB
+	 */
+	@Override
+	public long getExecutionCount() {
+		return this.executionCount.get();
+	}
+
+	/**
+	 * average time in ms taken by the excution of this query onto the DB
+	 */
+	@Override
+	public long getExecutionAvgTime() {
+		// We write lock here to be sure that we always calculate the average time
+		// with all updates from the executed applied: executionCount and totalExecutionTime
+		// both used in the calculation
+		this.writeLock.lock();
+		try {
+			long avgExecutionTime = 0;
+			if ( this.executionCount.get() > 0 ) {
+				avgExecutionTime = this.totalExecutionTime.get() / this.executionCount.get();
+			}
+			return avgExecutionTime;
+		}
+		finally {
+			this.writeLock.unlock();
+		}
 	}
 
+	/**
+	 * max time in ms taken by the excution of this query onto the DB
+	 */
+	@Override
+	public long getExecutionMaxTime() {
+		return this.executionMaxTime.get();
+	}
+
+	/**
+	 * min time in ms taken by the excution of this query onto the DB
+	 */
+	@Override
+	public long getExecutionMinTime() {
+		return this.executionMinTime.get();
+	}
+
+	@Override
 	public long getElementCountInMemory() {
-		return region.getElementCountInMemory();
+		return this.region.getElementCountInMemory();
 	}
 
+	@Override
 	public long getElementCountOnDisk() {
-		return region.getElementCountOnDisk();
+		return this.region.getElementCountOnDisk();
 	}
 
+	@Override
 	public long getSizeInMemory() {
-		return region.getSizeInMemory();
+		return this.region.getSizeInMemory();
 	}
 
+	@Override
 	public Map getEntries() {
-		Map map = new HashMap();
-		Iterator iter = region.toMap().entrySet().iterator();
-		while (iter.hasNext()) {
-			Map.Entry me = (Map.Entry) iter.next();
-			map.put(((CacheKey) me.getKey()).getKey(), me.getValue());
+		final Map map = new HashMap();
+		final Iterator iter = this.region.toMap().entrySet().iterator();
+		while ( iter.hasNext() ) {
+			final Map.Entry me = (Map.Entry) iter.next();
+			map.put( ( (CacheKey) me.getKey() ).getKey(), me.getValue() );
 		}
 		return map;
 	}
 
+	@Override
 	public String toString() {
-		StringBuilder buf = new StringBuilder()
-				.append("NaturalIdCacheStatistics")
-				.append("[hitCount=").append(this.hitCount)
-				.append(",missCount=").append(this.missCount)
-				.append(",putCount=").append(this.putCount);
-		//not sure if this would ever be null but wanted to be careful
-		if (region != null) {
-			buf.append(",elementCountInMemory=").append(this.getElementCountInMemory())
-					.append(",elementCountOnDisk=").append(this.getElementCountOnDisk())
-					.append(",sizeInMemory=").append(this.getSizeInMemory());
+		final StringBuilder buf = new StringBuilder()
+			.append( "NaturalIdCacheStatistics" )
+			.append( "[hitCount=" ).append( this.hitCount )
+			.append( ",missCount=" ).append( this.missCount )
+			.append( ",putCount=" ).append( this.putCount )
+			.append( ",executionCount=" ).append( this.executionCount )
+			.append( ",executionAvgTime=" ).append( this.getExecutionAvgTime() )
+			.append( ",executionMinTime=" ).append( this.executionMinTime )
+			.append( ",executionMaxTime=" ).append( this.executionMaxTime );
+		// not sure if this would ever be null but wanted to be careful
+		if ( this.region != null ) {
+			buf.append( ",elementCountInMemory=" ).append( this.getElementCountInMemory() )
+				.append( ",elementCountOnDisk=" ).append( this.getElementCountOnDisk() )
+				.append( ",sizeInMemory=" ).append( this.getSizeInMemory() );
 		}
-		buf.append(']');
+		buf.append( ']' );
 		return buf.toString();
 	}
 
 	void incrementHitCount() {
-		hitCount.getAndIncrement();
+		this.hitCount.getAndIncrement();
 	}
 
 	void incrementMissCount() {
-		missCount.getAndIncrement();
+		this.missCount.getAndIncrement();
 	}
 
 	void incrementPutCount() {
-		putCount.getAndIncrement();
+		this.putCount.getAndIncrement();
+	}
+
+	void queryExecuted(long time) {
+		// read lock is enough, concurrent updates are supported by the underlying type AtomicLong
+		// this only guards executed(long, long) to be called, when another thread is executing getExecutionAvgTime()
+		this.readLock.lock();
+		try {
+			// Less chances for a context switch
+			for ( long old = this.executionMinTime.get(); time < old && !this.executionMinTime.compareAndSet( old, time ); old = this.executionMinTime.get() ) {;}
+			for ( long old = this.executionMaxTime.get(); time > old && !this.executionMaxTime.compareAndSet( old, time ); old = this.executionMaxTime.get() ) {;}
+			this.executionCount.getAndIncrement();
+			this.totalExecutionTime.addAndGet( time );
+		}
+		finally {
+			this.readLock.unlock();
+		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
index acf6a9377b..be6e9c31c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
@@ -1,831 +1,879 @@
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
 package org.hibernate.stat.internal;
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import java.util.concurrent.atomic.AtomicLong;
 
 import org.hibernate.cache.spi.Region;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.service.Service;
 import org.hibernate.stat.CollectionStatistics;
 import org.hibernate.stat.EntityStatistics;
 import org.hibernate.stat.NaturalIdCacheStatistics;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.jboss.logging.Logger;
 
 /**
  * Implementation of {@link org.hibernate.stat.Statistics} based on the {@link java.util.concurrent} package.
  *
  * @author Alex Snaps
  */
 @SuppressWarnings({ "unchecked" })
 public class ConcurrentStatisticsImpl implements StatisticsImplementor, Service {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ConcurrentStatisticsImpl.class.getName());
 
 	private SessionFactoryImplementor sessionFactory;
 
 	private volatile boolean isStatisticsEnabled;
 	private volatile long startTime;
 	private AtomicLong sessionOpenCount = new AtomicLong();
 	private AtomicLong sessionCloseCount = new AtomicLong();
 	private AtomicLong flushCount = new AtomicLong();
 	private AtomicLong connectCount = new AtomicLong();
 
 	private AtomicLong prepareStatementCount = new AtomicLong();
 	private AtomicLong closeStatementCount = new AtomicLong();
 
 	private AtomicLong entityLoadCount = new AtomicLong();
 	private AtomicLong entityUpdateCount = new AtomicLong();
 	private AtomicLong entityInsertCount = new AtomicLong();
 	private AtomicLong entityDeleteCount = new AtomicLong();
 	private AtomicLong entityFetchCount = new AtomicLong();
 	private AtomicLong collectionLoadCount = new AtomicLong();
 	private AtomicLong collectionUpdateCount = new AtomicLong();
 	private AtomicLong collectionRemoveCount = new AtomicLong();
 	private AtomicLong collectionRecreateCount = new AtomicLong();
 	private AtomicLong collectionFetchCount = new AtomicLong();
 
 	private AtomicLong secondLevelCacheHitCount = new AtomicLong();
 	private AtomicLong secondLevelCacheMissCount = new AtomicLong();
 	private AtomicLong secondLevelCachePutCount = new AtomicLong();
 	
 	private AtomicLong naturalIdCacheHitCount = new AtomicLong();
 	private AtomicLong naturalIdCacheMissCount = new AtomicLong();
 	private AtomicLong naturalIdCachePutCount = new AtomicLong();
-
+	private AtomicLong naturalIdQueryExecutionCount = new AtomicLong();
+	private AtomicLong naturalIdQueryExecutionMaxTime = new AtomicLong();
+	private volatile String naturalIdQueryExecutionMaxTimeRegion;
+	
 	private AtomicLong queryExecutionCount = new AtomicLong();
 	private AtomicLong queryExecutionMaxTime = new AtomicLong();
 	private volatile String queryExecutionMaxTimeQueryString;
 	private AtomicLong queryCacheHitCount = new AtomicLong();
 	private AtomicLong queryCacheMissCount = new AtomicLong();
 	private AtomicLong queryCachePutCount = new AtomicLong();
 
 	private AtomicLong updateTimestampsCacheHitCount = new AtomicLong();
 	private AtomicLong updateTimestampsCacheMissCount = new AtomicLong();
 	private AtomicLong updateTimestampsCachePutCount = new AtomicLong();
 
 	private AtomicLong committedTransactionCount = new AtomicLong();
 	private AtomicLong transactionCount = new AtomicLong();
 
 	private AtomicLong optimisticFailureCount = new AtomicLong();
 
 	/**
 	 * natural id cache statistics per region
 	 */
 	private final ConcurrentMap naturalIdCacheStatistics = new ConcurrentHashMap();
 	/**
 	 * second level cache statistics per region
 	 */
 	private final ConcurrentMap secondLevelCacheStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per name
 	 */
 	private final ConcurrentMap entityStatistics = new ConcurrentHashMap();
 	/**
 	 * collection statistics per name
 	 */
 	private final ConcurrentMap collectionStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per query string (HQL or SQL)
 	 */
 	private final ConcurrentMap queryStatistics = new ConcurrentHashMap();
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public ConcurrentStatisticsImpl() {
 		clear();
 	}
 
 	public ConcurrentStatisticsImpl(SessionFactoryImplementor sessionFactory) {
 		clear();
 		this.sessionFactory = sessionFactory;
 	}
 
 	/**
 	 * reset all statistics
 	 */
 	public void clear() {
 		secondLevelCacheHitCount.set( 0 );
 		secondLevelCacheMissCount.set( 0 );
 		secondLevelCachePutCount.set( 0 );
 		
 		naturalIdCacheHitCount.set( 0 );
 		naturalIdCacheMissCount.set( 0 );
 		naturalIdCachePutCount.set( 0 );
+		naturalIdQueryExecutionCount.set( 0 );
+		naturalIdQueryExecutionMaxTime.set( 0 );
+		naturalIdQueryExecutionMaxTimeRegion = null;
 
 		sessionCloseCount.set( 0 );
 		sessionOpenCount.set( 0 );
 		flushCount.set( 0 );
 		connectCount.set( 0 );
 
 		prepareStatementCount.set( 0 );
 		closeStatementCount.set( 0 );
 
 		entityDeleteCount.set( 0 );
 		entityInsertCount.set( 0 );
 		entityUpdateCount.set( 0 );
 		entityLoadCount.set( 0 );
 		entityFetchCount.set( 0 );
 
 		collectionRemoveCount.set( 0 );
 		collectionUpdateCount.set( 0 );
 		collectionRecreateCount.set( 0 );
 		collectionLoadCount.set( 0 );
 		collectionFetchCount.set( 0 );
 
 		queryExecutionCount.set( 0 );
 		queryCacheHitCount.set( 0 );
 		queryExecutionMaxTime.set( 0 );
 		queryExecutionMaxTimeQueryString = null;
 		queryCacheMissCount.set( 0 );
 		queryCachePutCount.set( 0 );
 
 		updateTimestampsCacheMissCount.set( 0 );
 		updateTimestampsCacheHitCount.set( 0 );
 		updateTimestampsCachePutCount.set( 0 );
 
 		transactionCount.set( 0 );
 		committedTransactionCount.set( 0 );
 
 		optimisticFailureCount.set( 0 );
 
 		secondLevelCacheStatistics.clear();
 		entityStatistics.clear();
 		collectionStatistics.clear();
 		queryStatistics.clear();
 
 		startTime = System.currentTimeMillis();
 	}
 
 	public void openSession() {
 		sessionOpenCount.getAndIncrement();
 	}
 
 	public void closeSession() {
 		sessionCloseCount.getAndIncrement();
 	}
 
 	public void flush() {
 		flushCount.getAndIncrement();
 	}
 
 	public void connect() {
 		connectCount.getAndIncrement();
 	}
 
 	public void loadEntity(String entityName) {
 		entityLoadCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementLoadCount();
 	}
 
 	public void fetchEntity(String entityName) {
 		entityFetchCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementFetchCount();
 	}
 
 	/**
 	 * find entity statistics per name
 	 *
 	 * @param entityName entity name
 	 *
 	 * @return EntityStatistics object
 	 */
 	public EntityStatistics getEntityStatistics(String entityName) {
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) entityStatistics.get( entityName );
 		if ( es == null ) {
 			es = new ConcurrentEntityStatisticsImpl( entityName );
 			ConcurrentEntityStatisticsImpl previous;
 			if ( ( previous = (ConcurrentEntityStatisticsImpl) entityStatistics.putIfAbsent(
 					entityName, es
 			) ) != null ) {
 				es = previous;
 			}
 		}
 		return es;
 	}
 
 	public void updateEntity(String entityName) {
 		entityUpdateCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementUpdateCount();
 	}
 
 	public void insertEntity(String entityName) {
 		entityInsertCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementInsertCount();
 	}
 
 	public void deleteEntity(String entityName) {
 		entityDeleteCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementDeleteCount();
 	}
 
 	/**
 	 * Get collection statistics per role
 	 *
 	 * @param role collection role
 	 *
 	 * @return CollectionStatistics
 	 */
 	public CollectionStatistics getCollectionStatistics(String role) {
 		ConcurrentCollectionStatisticsImpl cs = (ConcurrentCollectionStatisticsImpl) collectionStatistics.get( role );
 		if ( cs == null ) {
 			cs = new ConcurrentCollectionStatisticsImpl( role );
 			ConcurrentCollectionStatisticsImpl previous;
 			if ( ( previous = (ConcurrentCollectionStatisticsImpl) collectionStatistics.putIfAbsent(
 					role, cs
 			) ) != null ) {
 				cs = previous;
 			}
 		}
 		return cs;
 	}
 
 	public void loadCollection(String role) {
 		collectionLoadCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementLoadCount();
 	}
 
 	public void fetchCollection(String role) {
 		collectionFetchCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementFetchCount();
 	}
 
 	public void updateCollection(String role) {
 		collectionUpdateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementUpdateCount();
 	}
 
 	public void recreateCollection(String role) {
 		collectionRecreateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRecreateCount();
 	}
 
 	public void removeCollection(String role) {
 		collectionRemoveCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRemoveCount();
 	}
 	
 
 	@Override
 	public NaturalIdCacheStatistics getNaturalIdCacheStatistics(String regionName) {
 		ConcurrentNaturalIdCacheStatisticsImpl nics =
 				(ConcurrentNaturalIdCacheStatisticsImpl) naturalIdCacheStatistics.get( regionName );
 		
 		if ( nics == null ) {
 			if ( sessionFactory == null ) {
 				return null;
 			}
 			Region region = sessionFactory.getNaturalIdCacheRegion( regionName );
 			if ( region == null ) {
 				return null;
 			}
 			nics = new ConcurrentNaturalIdCacheStatisticsImpl( region );
 			ConcurrentNaturalIdCacheStatisticsImpl previous;
 			if ( ( previous = (ConcurrentNaturalIdCacheStatisticsImpl) naturalIdCacheStatistics.putIfAbsent(
 					regionName, nics
 			) ) != null ) {
 				nics = previous;
 			}
 		}
 		return nics;
 	}
 
 	/**
 	 * Second level cache statistics per region
 	 *
 	 * @param regionName region name
 	 *
 	 * @return SecondLevelCacheStatistics
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
 		ConcurrentSecondLevelCacheStatisticsImpl slcs
 				= (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.get( regionName );
 		if ( slcs == null ) {
 			if ( sessionFactory == null ) {
 				return null;
 			}
 			Region region = sessionFactory.getSecondLevelCacheRegion( regionName );
 			if ( region == null ) {
 				return null;
 			}
 			slcs = new ConcurrentSecondLevelCacheStatisticsImpl( region );
 			ConcurrentSecondLevelCacheStatisticsImpl previous;
 			if ( ( previous = (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.putIfAbsent(
 					regionName, slcs
 			) ) != null ) {
 				slcs = previous;
 			}
 		}
 		return slcs;
 	}
 
 	public void secondLevelCachePut(String regionName) {
 		secondLevelCachePutCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementPutCount();
 	}
 
 	public void secondLevelCacheHit(String regionName) {
 		secondLevelCacheHitCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementHitCount();
 	}
 
 	public void secondLevelCacheMiss(String regionName) {
 		secondLevelCacheMissCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementMissCount();
 	}
 	
 	@Override
 	public void naturalIdCachePut(String regionName) {
 		naturalIdCachePutCount.getAndIncrement();
 		( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).incrementPutCount();
 	}
 
 	@Override
 	public void naturalIdCacheHit(String regionName) {
 		naturalIdCacheHitCount.getAndIncrement();
 		( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).incrementHitCount();
 	}
 
 	@Override
 	public void naturalIdCacheMiss(String regionName) {
 		naturalIdCacheMissCount.getAndIncrement();
 		( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).incrementMissCount();
 	}
+	
+	@Override
+	public void naturalIdQueryExecuted(String regionName, long time) {
+		naturalIdQueryExecutionCount.getAndIncrement();
+		boolean isLongestQuery = false;
+		for ( long old = naturalIdQueryExecutionMaxTime.get();
+			  ( isLongestQuery = time > old ) && ( !naturalIdQueryExecutionMaxTime.compareAndSet( old, time ) );
+			  old = naturalIdQueryExecutionMaxTime.get() ) {
+			// nothing to do here given the odd loop structure...
+		}
+		if ( isLongestQuery && regionName != null ) {
+			naturalIdQueryExecutionMaxTimeRegion = regionName;
+		}
+		if ( regionName != null ) {
+			( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).queryExecuted( time );
+		}
+	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public void queryExecuted(String hql, int rows, long time) {
         LOG.hql(hql, Long.valueOf(time), Long.valueOf(rows));
 		queryExecutionCount.getAndIncrement();
 		boolean isLongestQuery = false;
 		for ( long old = queryExecutionMaxTime.get();
 			  ( isLongestQuery = time > old ) && ( !queryExecutionMaxTime.compareAndSet( old, time ) );
 			  old = queryExecutionMaxTime.get() ) {
 			// nothing to do here given the odd loop structure...
 		}
 		if ( isLongestQuery ) {
 			queryExecutionMaxTimeQueryString = hql;
 		}
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.executed( rows, time );
 		}
 	}
 
 	public void queryCacheHit(String hql, String regionName) {
 		queryCacheHitCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheHitCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementHitCount();
 	}
 
 	public void queryCacheMiss(String hql, String regionName) {
 		queryCacheMissCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheMissCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementMissCount();
 	}
 
 	public void queryCachePut(String hql, String regionName) {
 		queryCachePutCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCachePutCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementPutCount();
 	}
 
 	@Override
 	public void updateTimestampsCacheHit() {
 		updateTimestampsCacheHitCount.getAndIncrement();
 	}
 
 	@Override
 	public void updateTimestampsCacheMiss() {
 		updateTimestampsCacheMissCount.getAndIncrement();
 	}
 
 	@Override
 	public void updateTimestampsCachePut() {
 		updateTimestampsCachePutCount.getAndIncrement();
 	}
 
 	/**
 	 * Query statistics from query string (HQL or SQL)
 	 *
 	 * @param queryString query string
 	 *
 	 * @return QueryStatistics
 	 */
 	public QueryStatistics getQueryStatistics(String queryString) {
 		ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) queryStatistics.get( queryString );
 		if ( qs == null ) {
 			qs = new ConcurrentQueryStatisticsImpl( queryString );
 			ConcurrentQueryStatisticsImpl previous;
 			if ( ( previous = (ConcurrentQueryStatisticsImpl) queryStatistics.putIfAbsent(
 					queryString, qs
 			) ) != null ) {
 				qs = previous;
 			}
 		}
 		return qs;
 	}
 
 	/**
 	 * @return entity deletion count
 	 */
 	public long getEntityDeleteCount() {
 		return entityDeleteCount.get();
 	}
 
 	/**
 	 * @return entity insertion count
 	 */
 	public long getEntityInsertCount() {
 		return entityInsertCount.get();
 	}
 
 	/**
 	 * @return entity load (from DB)
 	 */
 	public long getEntityLoadCount() {
 		return entityLoadCount.get();
 	}
 
 	/**
 	 * @return entity fetch (from DB)
 	 */
 	public long getEntityFetchCount() {
 		return entityFetchCount.get();
 	}
 
 	/**
 	 * @return entity update
 	 */
 	public long getEntityUpdateCount() {
 		return entityUpdateCount.get();
 	}
 
 	public long getQueryExecutionCount() {
 		return queryExecutionCount.get();
 	}
 
 	public long getQueryCacheHitCount() {
 		return queryCacheHitCount.get();
 	}
 
 	public long getQueryCacheMissCount() {
 		return queryCacheMissCount.get();
 	}
 
 	public long getQueryCachePutCount() {
 		return queryCachePutCount.get();
 	}
 
 	public long getUpdateTimestampsCacheHitCount() {
 		return updateTimestampsCacheHitCount.get();
 	}
 
 	public long getUpdateTimestampsCacheMissCount() {
 		return updateTimestampsCacheMissCount.get();
 	}
 
 	public long getUpdateTimestampsCachePutCount() {
 		return updateTimestampsCachePutCount.get();
 	}
 
 	/**
 	 * @return flush
 	 */
 	public long getFlushCount() {
 		return flushCount.get();
 	}
 
 	/**
 	 * @return session connect
 	 */
 	public long getConnectCount() {
 		return connectCount.get();
 	}
 
 	/**
 	 * @return second level cache hit
 	 */
 	public long getSecondLevelCacheHitCount() {
 		return secondLevelCacheHitCount.get();
 	}
 
 	/**
 	 * @return second level cache miss
 	 */
 	public long getSecondLevelCacheMissCount() {
 		return secondLevelCacheMissCount.get();
 	}
 
 	/**
 	 * @return second level cache put
 	 */
 	public long getSecondLevelCachePutCount() {
 		return secondLevelCachePutCount.get();
 	}
+
+	@Override
+	public long getNaturalIdQueryExecutionCount() {
+		return naturalIdQueryExecutionCount.get();
+	}
+
+	@Override
+	public long getNaturalIdQueryExecutionMaxTime() {
+		return naturalIdQueryExecutionMaxTime.get();
+	}
+	
+	@Override
+	public String getNaturalIdQueryExecutionMaxTimeRegion() {
+		return naturalIdQueryExecutionMaxTimeRegion;
+	}
 	
 	@Override
 	public long getNaturalIdCacheHitCount() {
 		return naturalIdCacheHitCount.get();
 	}
 
 	@Override
 	public long getNaturalIdCacheMissCount() {
 		return naturalIdCacheMissCount.get();
 	}
 
 	@Override
 	public long getNaturalIdCachePutCount() {
 		return naturalIdCachePutCount.get();
 	}
 
 	/**
 	 * @return session closing
 	 */
 	public long getSessionCloseCount() {
 		return sessionCloseCount.get();
 	}
 
 	/**
 	 * @return session opening
 	 */
 	public long getSessionOpenCount() {
 		return sessionOpenCount.get();
 	}
 
 	/**
 	 * @return collection loading (from DB)
 	 */
 	public long getCollectionLoadCount() {
 		return collectionLoadCount.get();
 	}
 
 	/**
 	 * @return collection fetching (from DB)
 	 */
 	public long getCollectionFetchCount() {
 		return collectionFetchCount.get();
 	}
 
 	/**
 	 * @return collection update
 	 */
 	public long getCollectionUpdateCount() {
 		return collectionUpdateCount.get();
 	}
 
 	/**
 	 * @return collection removal
 	 *         FIXME: even if isInverse="true"?
 	 */
 	public long getCollectionRemoveCount() {
 		return collectionRemoveCount.get();
 	}
 
 	/**
 	 * @return collection recreation
 	 */
 	public long getCollectionRecreateCount() {
 		return collectionRecreateCount.get();
 	}
 
 	/**
 	 * @return start time in ms (JVM standards {@link System#currentTimeMillis()})
 	 */
 	public long getStartTime() {
 		return startTime;
 	}
 
 	/**
 	 * log in info level the main statistics
 	 */
 	public void logSummary() {
 		LOG.loggingStatistics();
 		LOG.startTime( startTime );
 		LOG.sessionsOpened( sessionOpenCount.get() );
 		LOG.sessionsClosed( sessionCloseCount.get() );
 		LOG.transactions( transactionCount.get() );
 		LOG.successfulTransactions( committedTransactionCount.get() );
 		LOG.optimisticLockFailures( optimisticFailureCount.get() );
 		LOG.flushes( flushCount.get() );
 		LOG.connectionsObtained( connectCount.get() );
 		LOG.statementsPrepared( prepareStatementCount.get() );
 		LOG.statementsClosed( closeStatementCount.get() );
 		LOG.secondLevelCachePuts( secondLevelCachePutCount.get() );
 		LOG.secondLevelCacheHits( secondLevelCacheHitCount.get() );
 		LOG.secondLevelCacheMisses( secondLevelCacheMissCount.get() );
 		LOG.entitiesLoaded( entityLoadCount.get() );
 		LOG.entitiesUpdated( entityUpdateCount.get() );
 		LOG.entitiesInserted( entityInsertCount.get() );
 		LOG.entitiesDeleted( entityDeleteCount.get() );
 		LOG.entitiesFetched( entityFetchCount.get() );
 		LOG.collectionsLoaded( collectionLoadCount.get() );
 		LOG.collectionsUpdated( collectionUpdateCount.get() );
 		LOG.collectionsRemoved( collectionRemoveCount.get() );
 		LOG.collectionsRecreated( collectionRecreateCount.get() );
 		LOG.collectionsFetched( collectionFetchCount.get() );
+		LOG.naturalIdCachePuts( naturalIdCachePutCount.get() );
+		LOG.naturalIdCacheHits( naturalIdCacheHitCount.get() );
+		LOG.naturalIdCacheMisses( naturalIdCacheMissCount.get() );
+		LOG.naturalIdMaxQueryTime( naturalIdQueryExecutionMaxTime.get() );
+		LOG.naturalIdQueriesExecuted( naturalIdQueryExecutionCount.get() );
 		LOG.queriesExecuted( queryExecutionCount.get() );
 		LOG.queryCachePuts( queryCachePutCount.get() );
 		LOG.timestampCachePuts( updateTimestampsCachePutCount.get() );
 		LOG.timestampCacheHits( updateTimestampsCacheHitCount.get() );
 		LOG.timestampCacheMisses( updateTimestampsCacheMissCount.get() );
 		LOG.queryCacheHits( queryCacheHitCount.get() );
 		LOG.queryCacheMisses( queryCacheMissCount.get() );
 		LOG.maxQueryTime( queryExecutionMaxTime.get() );
 	}
 
 	/**
 	 * Are statistics logged
 	 */
 	public boolean isStatisticsEnabled() {
 		return isStatisticsEnabled;
 	}
 
 	/**
 	 * Enable statistics logs (this is a dynamic parameter)
 	 */
 	public void setStatisticsEnabled(boolean b) {
 		isStatisticsEnabled = b;
 	}
 
 	/**
 	 * @return Returns the max query execution time,
 	 *         for all queries
 	 */
 	public long getQueryExecutionMaxTime() {
 		return queryExecutionMaxTime.get();
 	}
 
 	/**
 	 * Get all executed query strings
 	 */
 	public String[] getQueries() {
 		return ArrayHelper.toStringArray( queryStatistics.keySet() );
 	}
 
 	/**
 	 * Get the names of all entities
 	 */
 	public String[] getEntityNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( entityStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllClassMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get the names of all collection roles
 	 */
 	public String[] getCollectionRoleNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( collectionStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllCollectionMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get all second-level cache region names
 	 */
 	public String[] getSecondLevelCacheRegionNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( secondLevelCacheStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllSecondLevelCacheRegions().keySet() );
 		}
 	}
 
 	public void endTransaction(boolean success) {
 		transactionCount.getAndIncrement();
 		if ( success ) {
 			committedTransactionCount.getAndIncrement();
 		}
 	}
 
 	public long getSuccessfulTransactionCount() {
 		return committedTransactionCount.get();
 	}
 
 	public long getTransactionCount() {
 		return transactionCount.get();
 	}
 
 	public void closeStatement() {
 		closeStatementCount.getAndIncrement();
 	}
 
 	public void prepareStatement() {
 		prepareStatementCount.getAndIncrement();
 	}
 
 	public long getCloseStatementCount() {
 		return closeStatementCount.get();
 	}
 
 	public long getPrepareStatementCount() {
 		return prepareStatementCount.get();
 	}
 
 	public void optimisticFailure(String entityName) {
 		optimisticFailureCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementOptimisticFailureCount();
 	}
 
 	public long getOptimisticFailureCount() {
 		return optimisticFailureCount.get();
 	}
 
 	@Override
     public String toString() {
 		return new StringBuilder()
 				.append( "Statistics[" )
 				.append( "start time=" ).append( startTime )
 				.append( ",sessions opened=" ).append( sessionOpenCount )
 				.append( ",sessions closed=" ).append( sessionCloseCount )
 				.append( ",transactions=" ).append( transactionCount )
 				.append( ",successful transactions=" ).append( committedTransactionCount )
 				.append( ",optimistic lock failures=" ).append( optimisticFailureCount )
 				.append( ",flushes=" ).append( flushCount )
 				.append( ",connections obtained=" ).append( connectCount )
 				.append( ",statements prepared=" ).append( prepareStatementCount )
 				.append( ",statements closed=" ).append( closeStatementCount )
 				.append( ",second level cache puts=" ).append( secondLevelCachePutCount )
 				.append( ",second level cache hits=" ).append( secondLevelCacheHitCount )
 				.append( ",second level cache misses=" ).append( secondLevelCacheMissCount )
 				.append( ",entities loaded=" ).append( entityLoadCount )
 				.append( ",entities updated=" ).append( entityUpdateCount )
 				.append( ",entities inserted=" ).append( entityInsertCount )
 				.append( ",entities deleted=" ).append( entityDeleteCount )
 				.append( ",entities fetched=" ).append( entityFetchCount )
 				.append( ",collections loaded=" ).append( collectionLoadCount )
 				.append( ",collections updated=" ).append( collectionUpdateCount )
 				.append( ",collections removed=" ).append( collectionRemoveCount )
 				.append( ",collections recreated=" ).append( collectionRecreateCount )
 				.append( ",collections fetched=" ).append( collectionFetchCount )
+				.append( ",naturalId queries executed to database=" ).append( naturalIdQueryExecutionCount )
+				.append( ",naturalId cache puts=" ).append( naturalIdCachePutCount )
+				.append( ",naturalId cache hits=" ).append( naturalIdCacheHitCount )
+				.append( ",naturalId cache misses=" ).append( naturalIdCacheMissCount )
+				.append( ",naturalId max query time=" ).append( naturalIdQueryExecutionMaxTime )
 				.append( ",queries executed to database=" ).append( queryExecutionCount )
 				.append( ",query cache puts=" ).append( queryCachePutCount )
 				.append( ",query cache hits=" ).append( queryCacheHitCount )
 				.append( ",query cache misses=" ).append( queryCacheMissCount )
 				.append(",update timestamps cache puts=").append(updateTimestampsCachePutCount)
 				.append(",update timestamps cache hits=").append(updateTimestampsCacheHitCount)
 				.append(",update timestamps cache misses=").append(updateTimestampsCacheMissCount)
 				.append( ",max query time=" ).append( queryExecutionMaxTime )
 				.append( ']' )
 				.toString();
 	}
 
 	public String getQueryExecutionMaxTimeQueryString() {
 		return queryExecutionMaxTimeQueryString;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsImplementor.java b/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsImplementor.java
index cff7ad444e..7629a9ed91 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/spi/StatisticsImplementor.java
@@ -1,245 +1,253 @@
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
 package org.hibernate.stat.spi;
 
 import org.hibernate.service.Service;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.internal.ConcurrentSecondLevelCacheStatisticsImpl;
 
 /**
  * Statistics SPI for the Hibernate core.  This is essentially the "statistic collector" API, its the contract
  * called to collect various stats.
  * 
  * @author Emmanuel Bernard
  */
 public interface StatisticsImplementor extends Statistics, Service {
 	/**
 	 * Callback about a session being opened.
 	 */
 	public void openSession();
 
 	/**
 	 * Callback about a session being closed.
 	 */
 	public void closeSession();
 
 	/**
 	 * Callback about a flush occurring
 	 */
 	public void flush();
 
 	/**
 	 * Callback about a connection being obtained from {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider}
 	 */
 	public void connect();
 
 	/**
 	 * Callback about a statement being prepared.
 	 */
 	public void prepareStatement();
 
 	/**
 	 * Callback about a statement being closed.
 	 */
 	public void closeStatement();
 
 	/**
 	 * Callback about a transaction completing.
 	 *
 	 * @param success Was the transaction successful?
 	 */
 	public void endTransaction(boolean success);
 
 	/**
 	 * Callback about an entity being loaded.  This might indicate a proxy or a fully initialized entity, but in either
 	 * case it means without a separate SQL query being needed.
 	 *
 	 * @param entityName The name of the entity loaded.
 	 */
 	public void loadEntity(String entityName);
 
 	/**
 	 * Callback about an entity being fetched.  Unlike {@link #loadEntity} this indicates a separate query being
 	 * performed.
 	 *
 	 * @param entityName The name of the entity fetched.
 	 */
 	public void fetchEntity(String entityName);
 
 	/**
 	 * Callback about an entity being updated.
 	 *
 	 * @param entityName The name of the entity updated.
 	 */
 	public void updateEntity(String entityName);
 
 	/**
 	 * Callback about an entity being inserted
 	 *
 	 * @param entityName The name of the entity inserted
 	 */
 	public void insertEntity(String entityName);
 
 	/**
 	 * Callback about an entity being deleted.
 	 *
 	 * @param entityName The name of the entity deleted.
 	 */
 	public void deleteEntity(String entityName);
 
 	/**
 	 * Callback about an optimistic lock failure on an entity
 	 *
 	 * @param entityName The name of the entity.
 	 */
 	public void optimisticFailure(String entityName);
 
 	/**
 	 * Callback about a collection loading.  This might indicate a lazy collection or an initialized collection being
 	 * created, but in either case it means without a separate SQL query being needed.
 	 *
 	 * @param role The collection role.
 	 */
 	public void loadCollection(String role);
 
 	/**
 	 * Callback to indicate a collection being fetched.  Unlike {@link #loadCollection}, this indicates a separate
 	 * query was needed.
 	 *
 	 * @param role The collection role.
 	 */
 	public void fetchCollection(String role);
 
 	/**
 	 * Callback indicating a collection was updated.
 	 *
 	 * @param role The collection role.
 	 */
 	public void updateCollection(String role);
 
 	/**
 	 * Callback indicating a collection recreation (full deletion + full (re-)insertion).
 	 *
 	 * @param role The collection role.
 	 */
 	public void recreateCollection(String role);
 
 	/**
 	 * Callback indicating a collection removal.
 	 *
 	 * @param role The collection role.
 	 */
 	public void removeCollection(String role);
 
 	/**
 	 * Callback indicating a put into second level cache.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void secondLevelCachePut(String regionName);
 
 	/**
 	 * Callback indicating a get from second level cache resulted in a hit.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void secondLevelCacheHit(String regionName);
 
 	/**
 	 * Callback indicating a get from second level cache resulted in a miss.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void secondLevelCacheMiss(String regionName);
 	
 	/**
 	 * Callback indicating a put into natural id cache.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void naturalIdCachePut(String regionName);
 	
 	/**
 	 * Callback indicating a get from natural id cache resulted in a hit.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void naturalIdCacheHit(String regionName);
 	
 	/**
 	 * Callback indicating a get from natural id cache resulted in a miss.
 	 *
 	 * @param regionName The name of the cache region
 	 */
 	public void naturalIdCacheMiss(String regionName);
 
 	/**
+	 * Callback indicating execution of a natural id query
+	 *
+	 * @param regionName The name of the cache region
+	 * @param time execution time
+	 */
+	public void naturalIdQueryExecuted(String regionName, long time);
+
+	/**
 	 * Callback indicating a put into the query cache.
 	 *
 	 * @param hql The query
 	 * @param regionName The cache region
 	 */
 	public void queryCachePut(String hql, String regionName);
 
 	/**
 	 * Callback indicating a get from the query cache resulted in a hit.
 	 *
 	 * @param hql The query
 	 * @param regionName The name of the cache region
 	 */
 	public void queryCacheHit(String hql, String regionName);
 
 	/**
 	 * Callback indicating a get from the query cache resulted in a miss.
 	 *
 	 * @param hql The query
 	 * @param regionName The name of the cache region
 	 */
 	public void queryCacheMiss(String hql, String regionName);
 
 	/**
 	 * Callback indicating execution of a sql/hql query
 	 *
 	 * @param hql The query
 	 * @param rows Number of rows returned
 	 * @param time execution time
 	 */
 	public void queryExecuted(String hql, int rows, long time);
 
 
 	/**
 	 * Callback indicating a hit to the timestamp cache
 	 */
 	public void updateTimestampsCacheHit();
 
 	/**
 	 * Callback indicating a miss to the timestamp cache
 	 */
 	public void updateTimestampsCacheMiss();
 
 	/**
 	 * Callback indicating a put to the timestamp cache
 	 */
 	public void updateTimestampsCachePut();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/ImmutableNaturalKeyLookupTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/ImmutableNaturalKeyLookupTest.java
index 4368e7e2ec..76f9301790 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/ImmutableNaturalKeyLookupTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/ImmutableNaturalKeyLookupTest.java
@@ -1,339 +1,336 @@
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
 package org.hibernate.test.annotations.naturalid;
 
 import org.junit.Assert;
 import org.junit.Test;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.sql.JoinType;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Guenther Demetz
  */
 public class ImmutableNaturalKeyLookupTest extends BaseCoreFunctionalTestCase {
 
 	@TestForIssue(jiraKey = "HHH-4838")
 	@Test
 	public void testSimpleImmutableNaturalKeyLookup() {
 		Session s = openSession();
 		Transaction newTx = s.getTransaction();
 
 		newTx.begin();
 		A a1 = new A();
 		a1.setName( "name1" );
 		s.persist( a1 );
 		newTx.commit();
-
+		
 		newTx = s.beginTransaction();
 		getCriteria( s ).uniqueResult(); // put query-result into cache
 		A a2 = new A();
 		a2.setName( "xxxxxx" );
 		s.persist( a2 );
 		newTx.commit();	  // Invalidates space A in UpdateTimeStamps region
+		
+		//Create new session to avoid the session cache which can't be tracked
+		s.close();
+		s = openSession();
 
 		newTx = s.beginTransaction();
 
-		// please enable
-		// log4j.logger.org.hibernate.cache.StandardQueryCache=DEBUG
-		// log4j.logger.org.hibernate.cache.UpdateTimestampsCache=DEBUG
-		// to see that isUpToDate is called where not appropriated
-
 		Assert.assertTrue( s.getSessionFactory().getStatistics().isStatisticsEnabled() );
 		s.getSessionFactory().getStatistics().clear();
 
 		getCriteria( s ).uniqueResult(); // should produce a hit in StandardQuery cache region
 
 		Assert.assertEquals(
 				"query is not considered as isImmutableNaturalKeyLookup, despite fullfilling all conditions",
-				1, s.getSessionFactory().getStatistics().getQueryCacheHitCount()
+				1, s.getSessionFactory().getStatistics().getNaturalIdCacheHitCount()
 		);
 
 		s.createQuery( "delete from A" ).executeUpdate();
 		newTx.commit();
 		// Shutting down the application
 		s.close();
 	}
 
 	@TestForIssue(jiraKey = "HHH-4838")
 	@Test
 	public void testNaturalKeyLookupWithConstraint() {
 		Session s = openSession();
 		Transaction newTx = s.getTransaction();
 
 		newTx.begin();
 		A a1 = new A();
 		a1.setName( "name1" );
 		s.persist( a1 );
 		newTx.commit();
 
 		newTx = s.beginTransaction();
 		getCriteria( s ).add( Restrictions.isNull( "singleD" ) ).uniqueResult(); // put query-result into cache
 		A a2 = new A();
 		a2.setName( "xxxxxx" );
 		s.persist( a2 );
 		newTx.commit();	  // Invalidates space A in UpdateTimeStamps region
 
 		newTx = s.beginTransaction();
 
-		// please enable
-		// log4j.logger.org.hibernate.cache.StandardQueryCache=DEBUG
-		// log4j.logger.org.hibernate.cache.UpdateTimestampsCache=DEBUG
-		// to see that isUpToDate is called where not appropriated
-
 		Assert.assertTrue( s.getSessionFactory().getStatistics().isStatisticsEnabled() );
 		s.getSessionFactory().getStatistics().clear();
 
 		// should not produce a hit in StandardQuery cache region because there is a constraint
 		getCriteria( s ).add( Restrictions.isNull( "singleD" ) ).uniqueResult();
 
 		Assert.assertEquals( 0, s.getSessionFactory().getStatistics().getQueryCacheHitCount() );
 
 		s.createQuery( "delete from A" ).executeUpdate();
 		newTx.commit();
 		// Shutting down the application
 		s.close();
 	}
 
 	@TestForIssue(jiraKey = "HHH-4838")
 	@Test
 	public void testCriteriaWithFetchModeJoinCollection() {
 		Session s = openSession();
 		Transaction newTx = s.getTransaction();
 
 		newTx.begin();
 		A a1 = new A();
 		a1.setName( "name1" );
 		D d1 = new D();
 		a1.getDs().add( d1 );
 		d1.setA( a1 );
 		s.persist( d1 );
 		s.persist( a1 );
 		newTx.commit();
 
 		newTx = s.beginTransaction();
 		getCriteria( s ).setFetchMode( "ds", FetchMode.JOIN ).uniqueResult(); // put query-result into cache
 		A a2 = new A();
 		a2.setName( "xxxxxx" );
 		s.persist( a2 );
 		newTx.commit();	  // Invalidates space A in UpdateTimeStamps region
+		
+		//Create new session to avoid the session cache which can't be tracked
+		s.close();
+		s = openSession();
 
 		newTx = s.beginTransaction();
 
 		// please enable
 		// log4j.logger.org.hibernate.cache.StandardQueryCache=DEBUG
 		// log4j.logger.org.hibernate.cache.UpdateTimestampsCache=DEBUG
 		// to see that isUpToDate is called where not appropriated
 
 		Assert.assertTrue( s.getSessionFactory().getStatistics().isStatisticsEnabled() );
 		s.getSessionFactory().getStatistics().clear();
 
 		// should produce a hit in StandardQuery cache region
 		getCriteria( s ).setFetchMode( "ds", FetchMode.JOIN ).uniqueResult();
 
 		Assert.assertEquals(
 				"query is not considered as isImmutableNaturalKeyLookup, despite fullfilling all conditions",
-				1, s.getSessionFactory().getStatistics().getQueryCacheHitCount()
+				1, s.getSessionFactory().getStatistics().getNaturalIdCacheHitCount()
 		);
 		s.createQuery( "delete from D" ).executeUpdate();
 		s.createQuery( "delete from A" ).executeUpdate();
 
 		newTx.commit();
 		// Shutting down the application
 		s.close();
 	}
 
 	@TestForIssue(jiraKey = "HHH-4838")
 	@Test
 	public void testCriteriaWithFetchModeJoinOnetoOne() {
 		Session s = openSession();
 		Transaction newTx = s.getTransaction();
 
 		newTx.begin();
 		A a1 = new A();
 		a1.setName( "name1" );
 		D d1 = new D();
 		a1.setSingleD( d1 );
 		d1.setSingleA( a1 );
 		s.persist( d1 );
 		s.persist( a1 );
 		newTx.commit();
 
 		newTx = s.beginTransaction();
 		getCriteria( s ).setFetchMode( "singleD", FetchMode.JOIN ).uniqueResult(); // put query-result into cache
 		A a2 = new A();
 		a2.setName( "xxxxxx" );
 		s.persist( a2 );
 		newTx.commit();	  // Invalidates space A in UpdateTimeStamps region
 
-		newTx = s.beginTransaction();
+		//Create new session to avoid the session cache which can't be tracked
+		s.close();
+		s = openSession();
 
-		// please enable
-		// log4j.logger.org.hibernate.cache.StandardQueryCache=DEBUG
-		// log4j.logger.org.hibernate.cache.UpdateTimestampsCache=DEBUG
-		// to see that isUpToDate is called where not appropriated
+		newTx = s.beginTransaction();
 
 		Assert.assertTrue( s.getSessionFactory().getStatistics().isStatisticsEnabled() );
 		s.getSessionFactory().getStatistics().clear();
 
 		// should produce a hit in StandardQuery cache region
 		getCriteria( s ).setFetchMode( "singleD", FetchMode.JOIN ).uniqueResult();
 
 		Assert.assertEquals(
 				"query is not considered as isImmutableNaturalKeyLookup, despite fullfilling all conditions",
-				1, s.getSessionFactory().getStatistics().getQueryCacheHitCount()
+				1, s.getSessionFactory().getStatistics().getNaturalIdCacheHitCount()
 		);
 		s.createQuery( "delete from A" ).executeUpdate();
 		s.createQuery( "delete from D" ).executeUpdate();
 
 		newTx.commit();
 		// Shutting down the application
 		s.close();
 	}
 
 	@TestForIssue(jiraKey = "HHH-4838")
 	@Test
 	public void testCriteriaWithAliasOneToOneJoin() {
 		Session s = openSession();
 		Transaction newTx = s.getTransaction();
 
 		newTx.begin();
 		A a1 = new A();
 		a1.setName( "name1" );
 		D d1 = new D();
 		a1.setSingleD( d1 );
 		d1.setSingleA( a1 );
 		s.persist( d1 );
 		s.persist( a1 );
 		newTx.commit();
 
 		newTx = s.beginTransaction();
 		getCriteria( s ).createAlias( "singleD", "d", JoinType.LEFT_OUTER_JOIN )
 				.uniqueResult(); // put query-result into cache
 		A a2 = new A();
 		a2.setName( "xxxxxx" );
 		s.persist( a2 );
 		newTx.commit();	  // Invalidates space A in UpdateTimeStamps region
 
 		newTx = s.beginTransaction();
 
 		// please enable
 		// log4j.logger.org.hibernate.cache.StandardQueryCache=DEBUG
 		// log4j.logger.org.hibernate.cache.UpdateTimestampsCache=DEBUG
 		// to see that isUpToDate is called where not appropriated
 
 		Assert.assertTrue( s.getSessionFactory().getStatistics().isStatisticsEnabled() );
 		s.getSessionFactory().getStatistics().clear();
 
 		// should not produce a hit in StandardQuery cache region because createAlias() creates a subcriteria
 		getCriteria( s ).createAlias( "singleD", "d", JoinType.LEFT_OUTER_JOIN ).uniqueResult();
 
 		Assert.assertEquals( 0, s.getSessionFactory().getStatistics().getQueryCacheHitCount() );
 		s.createQuery( "delete from A" ).executeUpdate();
 		s.createQuery( "delete from D" ).executeUpdate();
 
 		newTx.commit();
 		// Shutting down the application
 		s.close();
 	}
 
 	@TestForIssue(jiraKey = "HHH-4838")
 	@Test
 	public void testSubCriteriaOneToOneJoin() {
 		Session s = openSession();
 		Transaction newTx = s.getTransaction();
 
 		newTx.begin();
 		A a1 = new A();
 		a1.setName( "name1" );
 		D d1 = new D();
 		a1.setSingleD( d1 );
 		d1.setSingleA( a1 );
 		s.persist( d1 );
 		s.persist( a1 );
 		newTx.commit();
 
 		newTx = s.beginTransaction();
 		getCriteria( s ).createCriteria( "singleD", "d", JoinType.LEFT_OUTER_JOIN )
 				.uniqueResult(); // put query-result into cache
 		A a2 = new A();
 		a2.setName( "xxxxxx" );
 		s.persist( a2 );
 		newTx.commit();	  // Invalidates space A in UpdateTimeStamps region
 
 		newTx = s.beginTransaction();
 
 		// please enable
 		// log4j.logger.org.hibernate.cache.StandardQueryCache=DEBUG
 		// log4j.logger.org.hibernate.cache.UpdateTimestampsCache=DEBUG
 		// to see that isUpToDate is called where not appropriated
 
 		Assert.assertTrue( s.getSessionFactory().getStatistics().isStatisticsEnabled() );
 		s.getSessionFactory().getStatistics().clear();
 
 		// should not produce a hit in StandardQuery cache region because createCriteria() creates a subcriteria
 		getCriteria( s ).createCriteria( "singleD", "d", JoinType.LEFT_OUTER_JOIN ).uniqueResult();
 
 		Assert.assertEquals( 0, s.getSessionFactory().getStatistics().getQueryCacheHitCount() );
 		s.createQuery( "delete from A" ).executeUpdate();
 		s.createQuery( "delete from D" ).executeUpdate();
 
 		newTx.commit();
 		// Shutting down the application
 		s.close();
 	}
 
 	private Criteria getCriteria(Session s) {
 		Criteria crit = s.createCriteria( A.class, "anAlias" );
 		crit.add( Restrictions.naturalId().set( "name", "name1" ) );
 		crit.setFlushMode( FlushMode.COMMIT );
 		crit.setCacheable( true );
 		return crit;
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				A.class,
 				D.class
 		};
 	}
 
 	@Override
 	protected void configure(Configuration cfg) {
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 	}
 
 
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnManyToOne.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnManyToOne.java
index 9ce26a88b6..b02cc0e35a 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnManyToOne.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnManyToOne.java
@@ -1,41 +1,43 @@
 package org.hibernate.test.annotations.naturalid;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.ManyToOne;
 
 import org.hibernate.annotations.NaturalId;
+import org.hibernate.annotations.NaturalIdCache;
 
 @Entity
+@NaturalIdCache
 /**
  * Test case for NaturalId annotation - ANN-750
  * 
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 class NaturalIdOnManyToOne {
 
     @Id
     @GeneratedValue
     int id;
 
     @NaturalId
     @ManyToOne
     Citizen citizen;
     
 	public int getId() {
 		return id;
 	}
 
 	public void setId(int id) {
 		this.id = id;
 	}
 
 	public Citizen getCitizen() {
 		return citizen;
 	}
 
 	public void setCitizen(Citizen citizen) {
 		this.citizen = citizen;
 	}
 } 
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
index 5f69f2a3ae..8435a46f4c 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
@@ -1,146 +1,161 @@
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
 package org.hibernate.test.annotations.naturalid;
 
 import java.util.List;
 
 import org.jboss.logging.Logger;
 import org.junit.Test;
 
 import org.hibernate.Criteria;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.stat.Statistics;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test case for NaturalId annotation. See ANN-750.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 @TestForIssue( jiraKey = "ANN-750" )
 public class NaturalIdOnSingleManyToOneTest extends BaseCoreFunctionalTestCase {
 	private static final Logger log = Logger.getLogger( NaturalIdOnSingleManyToOneTest.class );
 
 	@Test
 	public void testMappingProperties() {
         log.warn("Commented out test");
 
 		ClassMetadata metaData = sessionFactory().getClassMetadata(
 				NaturalIdOnManyToOne.class
 		);
 		assertTrue(
 				"Class should have a natural key", metaData
 						.hasNaturalIdentifier()
 		);
 		int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
 		assertTrue( "Wrong number of elements", propertiesIndex.length == 1 );
 	}
 
 	@Test
 	public void testManyToOneNaturalIdCached() {
 		NaturalIdOnManyToOne singleManyToOne = new NaturalIdOnManyToOne();
 		Citizen c1 = new Citizen();
 		c1.setFirstname( "Emmanuel" );
 		c1.setLastname( "Bernard" );
 		c1.setSsn( "1234" );
 
 		State france = new State();
 		france.setName( "Ile de France" );
 		c1.setState( france );
 
 		singleManyToOne.setCitizen( c1 );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( france );
 		s.persist( c1 );
 		s.persist( singleManyToOne );
 		tx.commit();
 		s.close();
-
+		
+		//Clear naturalId cache that was populated when putting the data
+		s.getSessionFactory().getCache().evictNaturalIdRegions();
+		
 		s = openSession();
 		tx = s.beginTransaction();
 		Criteria criteria = s.createCriteria( NaturalIdOnManyToOne.class );
 		criteria.add( Restrictions.naturalId().set( "citizen", c1 ) );
 		criteria.setCacheable( true );
 
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"First query should be a miss", 1, stats
-						.getQueryCacheMissCount()
+						.getNaturalIdCacheMissCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 1, stats
-						.getQueryCachePutCount()
+						.getNaturalIdCachePutCount()
+		);
+		assertEquals(
+				"Query count should be one", 1, stats
+						.getNaturalIdQueryExecutionCount()
 		);
 
-		// query a second time - result should be cached
+		// query a second time - result should be in session cache
 		criteria.list();
 		assertEquals(
-				"Cache hits should be empty", 1, stats
-						.getQueryCacheHitCount()
+				"Cache hits should be empty", 0, stats
+						.getNaturalIdCacheHitCount()
+		);
+		assertEquals(
+				"Second query should not be a miss", 1, stats
+						.getNaturalIdCacheMissCount()
+		);
+		assertEquals(
+				"Query count should be one", 1, stats
+						.getNaturalIdQueryExecutionCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
     protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Citizen.class, State.class,
 				NaturalIdOnManyToOne.class
 		};
 	}
 
 	@Override
     protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
index 04a43eb100..2fff3bdd9a 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
@@ -1,298 +1,317 @@
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
 package org.hibernate.test.annotations.naturalid;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 import java.util.List;
 
 import org.hibernate.Criteria;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.stat.Statistics;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * Test case for NaturalId annotation
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public class NaturalIdTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testMappingProperties() {
 		ClassMetadata metaData = sessionFactory().getClassMetadata(
 				Citizen.class
 		);
 		assertTrue(
 				"Class should have a natural key", metaData
 						.hasNaturalIdentifier()
 		);
 		int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
 		assertTrue( "Wrong number of elements", propertiesIndex.length == 2 );
 	}
 
 	@Test
 	public void testNaturalIdCached() {
 		saveSomeCitizens();
-
+		
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = ( State ) s.load( State.class, 2 );
 		Criteria criteria = s.createCriteria( Citizen.class );
-		criteria.add(
-				Restrictions.naturalId().set( "ssn", "1234" ).set(
-						"state",
-						france
-				)
-		);
+		criteria.add( Restrictions.naturalId().set( "ssn", "1234" ).set( "state", france ) );
 		criteria.setCacheable( true );
+		
+		s.getSessionFactory().getCache().evictNaturalIdRegions();
 
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"First query should be a miss", 1, stats
-						.getQueryCacheMissCount()
+						.getNaturalIdCacheMissCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 1, stats
-						.getQueryCachePutCount()
+						.getNaturalIdCachePutCount()
+		);
+		assertEquals(
+				"Query execution count should be one", 1, stats
+						.getNaturalIdQueryExecutionCount()
 		);
 
-		// query a second time - result should be cached
+		// query a second time - result should be cached in session
 		criteria.list();
 		assertEquals(
-				"Cache hits should be empty", 1, stats
-						.getQueryCacheHitCount()
+				"Cache hits should be empty", 0, stats
+						.getNaturalIdCacheHitCount()
+		);
+		assertEquals(
+				"Second query should not be a miss", 1, stats
+						.getNaturalIdCacheMissCount()
+		);
+		assertEquals(
+				"Query execution count should be one", 1, stats
+						.getNaturalIdQueryExecutionCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdLoaderNotCached() {
 		saveSomeCitizens();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = ( State ) s.load( State.class, 2 );
 		final NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Citizen.class );
 		naturalIdLoader.using( "ssn", "1234" ).using( "state", france );
 
 		//NaturalId cache gets populated during entity loading, need to clear it out
 		sessionFactory().getCache().evictNaturalIdRegions();
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 
 		// first query
 		Citizen citizen = (Citizen)naturalIdLoader.load();
 		assertNotNull( citizen );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"First load should be a miss", 1, stats
 						.getNaturalIdCacheMissCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 1, stats
 						.getNaturalIdCachePutCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdLoaderCached() {
 		saveSomeCitizens();
 		
 		Statistics stats = sessionFactory().getStatistics();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"First load should be a miss", 1, stats
 						.getNaturalIdCacheMissCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 3, stats
 						.getNaturalIdCachePutCount()
 		);
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = ( State ) s.load( State.class, 2 );
 		final NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Citizen.class );
 		naturalIdLoader.using( "ssn", "1234" ).using( "state", france );
 
 		//Not clearing naturalId caches, should be warm from entity loading
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 
 		// first query
 		Citizen citizen = (Citizen)naturalIdLoader.load();
 		assertNotNull( citizen );
 		assertEquals(
 				"Cache hits should be empty", 1, stats
 						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"First load should be a miss", 0, stats
 						.getNaturalIdCacheMissCount()
 		);
 		assertEquals(
 				"Query result should be added to cache", 0, stats
 						.getNaturalIdCachePutCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdUncached() {
 		saveSomeCitizens();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = ( State ) s.load( State.class, 2 );
 		Criteria criteria = s.createCriteria( Citizen.class );
 		criteria.add(
 				Restrictions.naturalId().set( "ssn", "1234" ).set(
 						"state",
 						france
 				)
 		);
 		criteria.setCacheable( false );
+		
+		s.getSessionFactory().getCache().evictNaturalIdRegions();
 
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
-				"Query result should be added to cache", 0, stats
-						.getQueryCachePutCount()
+				"Query execution count should be one", 1, stats
+						.getNaturalIdQueryExecutionCount()
 		);
 
-		// query a second time
+		// query a second time - result should be cached in session
 		criteria.list();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
-						.getQueryCacheHitCount()
+						.getNaturalIdCacheHitCount()
+		);
+		assertEquals(
+				"Second query should not be a miss", 1, stats
+						.getNaturalIdCacheMissCount()
+		);
+		assertEquals(
+				"Query execution count should be one", 1, stats
+						.getNaturalIdQueryExecutionCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Citizen.class, State.class,
 				NaturalIdOnManyToOne.class
 		};
 	}
 
 	private void saveSomeCitizens() {
 		Citizen c1 = new Citizen();
 		c1.setFirstname( "Emmanuel" );
 		c1.setLastname( "Bernard" );
 		c1.setSsn( "1234" );
 
 		State france = new State();
 		france.setName( "Ile de France" );
 		c1.setState( france );
 
 		Citizen c2 = new Citizen();
 		c2.setFirstname( "Gavin" );
 		c2.setLastname( "King" );
 		c2.setSsn( "000" );
 		State australia = new State();
 		australia.setName( "Australia" );
 		c2.setState( australia );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( australia );
 		s.persist( france );
 		s.persist( c1 );
 		s.persist( c2 );
 		tx.commit();
 		s.close();
 	}
 
 	@Override
 	protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java
index 055ce882a3..6b5f6873b7 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java
@@ -1,350 +1,350 @@
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
 package org.hibernate.test.jpa.naturalid;
 
 import org.junit.Test;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.test.jpa.AbstractJPATest;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * copied from {@link org.hibernate.test.naturalid.immutable.ImmutableNaturalIdTest}
  *
  * @author Steve Ebersole
  */
 public class ImmutableNaturalIdTest extends AbstractJPATest {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "jpa/naturalid/User.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Test
 	public void testUpdate() {
 		// prepare some test data...
 		Session session = openSession();
     	session.beginTransaction();
 	  	User user = new User();
     	user.setUserName( "steve" );
     	user.setEmail( "steve@hibernate.org" );
     	user.setPassword( "brewhaha" );
 		session.save( user );
     	session.getTransaction().commit();
     	session.close();
 
 		// 'user' is now a detached entity, so lets change a property and reattch...
 		user.setPassword( "homebrew" );
 		session = openSession();
 		session.beginTransaction();
 		session.update( user );
 		session.getTransaction().commit();
 		session.close();
 
 		// clean up
 		session = openSession();
 		session.beginTransaction();
 		session.delete( user );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testNaturalIdCheck() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		u.setUserName( "Steve" );
 		try {
 			s.flush();
 			fail();
 		}
 		catch ( HibernateException he ) {
 		}
 		u.setUserName( "steve" );
 		s.delete( u );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSimpleNaturalIdLoadAccessCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		u = (User) s.bySimpleNaturalId( User.class ).load( "steve" );
 		assertNotNull( u );
 		User u2 = (User) s.bySimpleNaturalId( User.class ).getReference( "steve" );
 		assertTrue( u == u2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete User" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdLoadAccessCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = (User) s.byNaturalId( User.class ).using( "userName", "steve" ).load();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getEntityLoadCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCacheMissCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCacheHitCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCachePutCount() );
-		assertEquals( 0, sessionFactory().getStatistics().getQueryExecutionCount() );
-		assertEquals( 0, sessionFactory().getStatistics().getQueryCacheHitCount() );
-		assertEquals( 0, sessionFactory().getStatistics().getQueryCachePutCount() );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() );
 
 		s = openSession();
 		s.beginTransaction();
 		User v = new User( "gavin", "supsup" );
 		s.persist( v );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = (User) s.byNaturalId( User.class ).using( "userName", "steve" ).load();
 		assertNotNull( u );
 		assertEquals( 1, sessionFactory().getStatistics().getEntityLoadCount() );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );//0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 		u = (User) s.byNaturalId( User.class ).using( "userName", "steve" ).load();
 		assertNotNull( u );
 		assertEquals( 1, sessionFactory().getStatistics().getEntityLoadCount() );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );//0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete User" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() );//1: no stats since hbm.xml can't enable NaturalId caching
 
 		s = openSession();
 		s.beginTransaction();
 		User v = new User( "gavin", "supsup" );
 		s.persist( v );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );//0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );//0: no stats since hbm.xml can't enable NaturalId caching
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 2 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );//0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );//0: no stats since hbm.xml can't enable NaturalId caching
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete User" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdDeleteUsingCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() );//0: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );//0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );//1: incorrect stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNull( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdRecreateUsingCache() {
 		testNaturalIdDeleteUsingCache();
 
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() );//1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 		s.getTransaction().commit();
 		s.close();
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );//0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );//1: incorrect stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutable/ImmutableNaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutable/ImmutableNaturalIdTest.java
index eb566efac3..1833a307cb 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutable/ImmutableNaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutable/ImmutableNaturalIdTest.java
@@ -1,261 +1,261 @@
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
 package org.hibernate.test.naturalid.immutable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Restrictions;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;
 
 /**
  * @author Steve Ebersole
  */
 public class ImmutableNaturalIdTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "naturalid/immutable/User.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Test
 	public void testUpdate() {
 		// prepare some test data...
 		Session session = openSession();
     	session.beginTransaction();
 	  	User user = new User();
     	user.setUserName( "steve" );
     	user.setEmail( "steve@hibernate.org" );
     	user.setPassword( "brewhaha" );
 		session.save( user );
     	session.getTransaction().commit();
     	session.close();
 
 		// 'user' is now a detached entity, so lets change a property and reattch...
 		user.setPassword( "homebrew" );
 		session = openSession();
 		session.beginTransaction();
 		session.update( user );
 		session.getTransaction().commit();
 		session.close();
 
 		// clean up
 		session = openSession();
 		session.beginTransaction();
 		session.delete( user );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testNaturalIdCheck() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		u.setUserName( "Steve" );
 		try {
 			s.flush();
 			fail();
 		}
 		catch ( HibernateException he ) {
 		}
 		u.setUserName( "steve" );
 		s.delete( u );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //0: no stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s = openSession();
 		s.beginTransaction();
 		User v = new User( "gavin", "supsup" );
 		s.persist( v );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 2 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //2: no stats since hbm.xml can't enable NaturalId caching
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete User" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdDeleteUsingCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNull( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdRecreateUsingCache() {
 		testNaturalIdDeleteUsingCache();
 
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 		s.getTransaction().commit();
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java
index 5b85d9ca44..e15ae44d3d 100755
--- a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java
@@ -1,403 +1,403 @@
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
 package org.hibernate.test.naturalid.mutable;
 import java.lang.reflect.Field;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Restrictions;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 
 /**
  * @author Gavin King
  */
 public class MutableNaturalIdTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "naturalid/mutable/User.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
 		cfg.setProperty(Environment.USE_QUERY_CACHE, "true");
 		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
 	}
 
 	@Test
 	public void testReattachmentNaturalIdCheck() throws Throwable {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "gavin", "hb", "secret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		Field name = u.getClass().getDeclaredField("name");
 		name.setAccessible(true);
 		name.set(u, "Gavin");
 		s = openSession();
 		s.beginTransaction();
 		try {
 			s.update( u );
 			s.getTransaction().commit();
 		}
 		catch( HibernateException expected ) {
 			s.getTransaction().rollback();
 		}
 		catch( Throwable t ) {
 			try {
 				s.getTransaction().rollback();
 			}
 			catch ( Throwable ignore ) {
 			}
 			throw t;
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNonexistentNaturalIdCache() {
 		sessionFactory().getStatistics().clear();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Object nullUser = s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			)
 			.setCacheable(true)
 			.uniqueResult();
 
 		assertNull(nullUser);
 
 		t.commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 0 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount(), 1 );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount(), 0 );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount(), 0 );
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		User u = new User("gavin", "hb", "secret");
 		s.persist(u);
 
 		t.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		u = (User) s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			)
 			.setCacheable(true)
 			.uniqueResult();
 
 		assertNotNull(u);
 
 		t.commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		u = (User) s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			).setCacheable(true)
 			.uniqueResult();
 
 		s.delete(u);
 
 		t.commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		nullUser = s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			)
 			.setCacheable(true)
 			.uniqueResult();
 
 		assertNull(nullUser);
 
 		t.commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 0 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() );
 	}
 
 	@Test
 	public void testNaturalIdCache() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User u = new User( "gavin", "hb", "secret" );
 		s.persist( u );
 		t.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set( "name", "gavin" )
 						.set( "org", "hb" )
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		t.commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s = openSession();
 		t = s.beginTransaction();
 		User v = new User("xam", "hb", "foobar");
 		s.persist(v);
 		t.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "gavin")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull(u);
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "gavin")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull(u);
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.createQuery("delete User").executeUpdate();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdDeleteUsingCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "hb", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNull( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdRecreateUsingCache() {
 		testNaturalIdDeleteUsingCache();
 
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "hb", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 		s.getTransaction().commit();
 		s.close();
 		
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
-		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
+		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
+		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQuerying() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		User u = new User("emmanuel", "hb", "bh");
 		s.persist(u);
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		u = (User) s.createQuery( "from User u where u.name = :name" )
 			.setParameter( "name", "emmanuel" ).uniqueResult();
 		assertEquals( "emmanuel", u.getName() );
 		s.delete( u );
 
 		t.commit();
 		s.close();
 	}
 }
 
