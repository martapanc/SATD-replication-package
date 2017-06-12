49/report.java
Satd-method: public SingleTableEntityPersister(
********************************************
********************************************
49/After/ HHH-10133 35712181_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+public class JdbcMocks {
+	public static Connection createConnection(String databaseName, int majorVersion) {
+	public static Connection createConnection(String databaseName, int majorVersion, int minorVersion) {
+		public void setMetadataProxy(DatabaseMetaData metadataProxy) {
+		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
+		public void setConnectionProxy(Connection connectionProxy) {
+		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
+public class QualifiedTableNamingTest extends BaseNonConfigCoreFunctionalTestCase {
+	public void testQualifiedNameSeparator() throws Exception {
+	public static class Box {
+		public Integer id;
+		public String value;
+	public static class TestDialect extends Dialect {
+		public NameQualifierSupport getNameQualifierSupport() {
+	public static class MockedConnectionProvider implements ConnectionProvider {
+		public Connection getConnection() throws SQLException {
+		public void closeConnection(Connection conn) throws SQLException {
+		public boolean supportsAggressiveRelease() {
+		public boolean isUnwrappableAs(Class unwrapType) {
+		public <T> T unwrap(Class<T> unwrapType) {

Lines added containing method: 20. Lines removed containing method: 0. Tot = 20
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public Object getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getColumnSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getColumnIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-11241 7469df8d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public Object getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-11375 34d92507_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public Object getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-11612 68fa5d8f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public Object getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-9411  b8a3774c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public void testTreatWithRestrictionOnAbstractClass() {
+	public static abstract class Animal {
+	public static abstract class Dog extends Animal {
+		public final boolean isFast() {
+	public static class Dachshund extends Dog {
+		public Dachshund() {
+	public static class Greyhound extends Dog {
+		public Greyhound() {
+	public void testTreatWithRestrictionOnAbstractClass() {
+	public static abstract class TreatAnimal {
+	public static abstract class Dog extends TreatAnimal {
+		public final boolean isFast() {
+	public static class Dachshund extends Dog {
+		public Dachshund() {
+	public static class Greyhound extends Dog {
+		public Greyhound() {

Lines added containing method: 16. Lines removed containing method: 0. Tot = 16
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public Object getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-9490  9caca0ce_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public String getExternalName() {
+	public String toExternalForm() {
+	public static LockMode fromExternalForm(String externalForm) {
+		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
+		public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
+		public SessionFactoryObserver[] getSessionFactoryObservers();
+		public EntityNameResolver[] getEntityNameResolvers();
+		public Settings getSettings();
+		public Map<String, SQLFunction> getCustomSqlFunctionMap();
+		public Object getBeanManagerReference();
+		public Object getValidatorFactoryReference();
+public class CacheRegionDefinition {
+	public static enum CacheRegionType {
+	public CacheRegionDefinition(
+	public CacheRegionType getRegionType() {
+	public String getRole() {
+	public String getUsage() {
+	public String getRegion() {
+	public boolean isCacheLazy() {
+public class InvalidMappingException extends org.hibernate.InvalidMappingException {
+	public InvalidMappingException(Origin origin) {
+	public InvalidMappingException(Origin origin, Throwable e) {
+	public Origin getOrigin() {
+public class JaccPermissionDefinition {
+	public final String contextId;
+	public final String role;
+	public final String clazz;
+	public final String actions;
+	public JaccPermissionDefinition(String contextId, String role, String clazz, String actions) {
+public class MappingException extends org.hibernate.MappingException {
+	public MappingException(String message, Origin origin) {
+	public MappingException(String message, Throwable root, Origin origin) {
+	public String getMessage() {
+	public Origin getOrigin() {
+public class MappingNotFoundException extends MappingException {
+	public MappingNotFoundException(String message, Origin origin) {
+	public MappingNotFoundException(Origin origin) {
+	public MappingNotFoundException(String message, Throwable root, Origin origin) {
+	public MappingNotFoundException(Throwable root, Origin origin) {
+public interface Metadata extends Mapping {
+public interface MetadataBuilder {
+	public MetadataBuilder withImplicitSchemaName(String implicitSchemaName);
+	public MetadataBuilder withImplicitCatalogName(String implicitCatalogName);
+	public MetadataBuilder with(ImplicitNamingStrategy namingStrategy);
+	public MetadataBuilder with(PhysicalNamingStrategy namingStrategy);
+	public MetadataBuilder with(ReflectionManager reflectionManager);
+	public MetadataBuilder with(SharedCacheMode cacheMode);
+	public MetadataBuilder with(AccessType accessType);
+	public MetadataBuilder with(IndexView jandexView);
+	public MetadataBuilder with(ScanOptions scanOptions);
+	public MetadataBuilder with(ScanEnvironment scanEnvironment);
+	public MetadataBuilder with(Scanner scanner);
+	public MetadataBuilder with(ArchiveDescriptorFactory factory);
+	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled);
+	public MetadataBuilder withExplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
+	public MetadataBuilder withImplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
+	public MetadataBuilder withImplicitForcingOfDiscriminatorsInSelect(boolean supported);
+	public MetadataBuilder withNationalizedCharacterData(boolean enabled);
+	public MetadataBuilder with(BasicType type);
+	public MetadataBuilder with(UserType type, String[] keys);
+	public MetadataBuilder with(CompositeUserType type, String[] keys);
+	public MetadataBuilder with(TypeContributor typeContributor);
+	public MetadataBuilder with(CacheRegionDefinition cacheRegionDefinition);
+	public MetadataBuilder with(ClassLoader tempClassLoader);
+	public MetadataBuilder setSourceProcessOrdering(List<MetadataSourceType> ordering);
+//	public MetadataBuilder with(PersistentAttributeMemberResolver resolver);
+	public Metadata build();
+public class MetadataSources implements Serializable {
+	public MetadataSources() {
+	public MetadataSources(ServiceRegistry serviceRegistry) {
+	public List<Binding> getXmlBindings() {
+	public Collection<String> getAnnotatedPackages() {
+	public Collection<Class<?>> getAnnotatedClasses() {
+	public Collection<String> getAnnotatedClassNames() {
+	public Collection<AttributeConverterDefinition> getAttributeConverters() {
+	public ServiceRegistry getServiceRegistry() {
+	public List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList() {
+	public Map<String, SQLFunction> getSqlFunctions() {
+	public MetadataBuilder getMetadataBuilder() {
+	public MetadataBuilder getMetadataBuilder(StandardServiceRegistry serviceRegistry) {
+	public Metadata buildMetadata() {
+	public Metadata buildMetadata(StandardServiceRegistry serviceRegistry) {
+	public MetadataSources addAnnotatedClass(Class annotatedClass) {
+	public MetadataSources addAnnotatedClassName(String annotatedClassName) {
+	public MetadataSources addPackage(String packageName) {
+	public MetadataSources addResource(String name) {
+	public MetadataSources addClass(Class entityClass) {
+	public MetadataSources addFile(String path) {;
+	public MetadataSources addFile(File file) {
+	public MetadataSources addCacheableFile(String path) {
+	public MetadataSources addCacheableFile(File file) {
+	public MetadataSources addCacheableFileStrictly(File file) throws SerializationException, FileNotFoundException {
+	public MetadataSources addInputStream(InputStreamAccess xmlInputStreamAccess) {
+	public MetadataSources addInputStream(InputStream xmlInputStream) {
+	public MetadataSources addURL(URL url) {
+	public MetadataSources addDocument(Document document) {
+	public MetadataSources addJar(File jar) {
+	public MetadataSources addDirectory(File dir) {
+	public MetadataSources addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
+	public MetadataSources addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
+	public MetadataSources addAttributeConverter(AttributeConverter attributeConverter) {
+	public MetadataSources addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
+	public MetadataSources addAttributeConverter(AttributeConverterDefinition definition) {
+	public MetadataSources addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
+	public void addSqlFunction(String functionName, SQLFunction function) {
+public interface SessionFactoryBuilder {
+	public SessionFactoryBuilder with(Interceptor interceptor);
+	public SessionFactoryBuilder with(CustomEntityDirtinessStrategy customEntityDirtinessStrategy);
+	public SessionFactoryBuilder with(CurrentTenantIdentifierResolver currentTenantIdentifierResolver);
+	public SessionFactoryBuilder add(SessionFactoryObserver... observers);
+	public SessionFactoryBuilder add(EntityNameResolver... entityNameResolvers);
+	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate);
+	public SessionFactoryBuilder with(EntityTuplizerFactory entityTuplizerFactory);
+	public SessionFactoryBuilder with(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass);
+	public SessionFactoryBuilder withValidatorFactory(Object validatorFactory);
+	public SessionFactoryBuilder withBeanManager(Object beanManager);
+	public SessionFactoryBuilder with(String registrationName, SQLFunction sqlFunction);
+	public SessionFactory build();
+public class UnsupportedOrmXsdVersionException extends MappingException {
+	public UnsupportedOrmXsdVersionException(String requestedVersion, Origin origin) {
+	public String getRequestedVersion() {
+public class CfgXmlAccessServiceImpl implements CfgXmlAccessService {
+	public CfgXmlAccessServiceImpl(Map configurationValues) {
+	public LoadedConfig getAggregatedConfig() {
-public class ResultSetMappingSecondPass extends ResultSetMappingBinder implements QuerySecondPass {
+public class CfgXmlAccessServiceInitiator implements StandardServiceInitiator<CfgXmlAccessService> {
+	public static final CfgXmlAccessServiceInitiator INSTANCE = new CfgXmlAccessServiceInitiator();
-	public ResultSetMappingSecondPass(Element element, String path, Mappings mappings) {
+	public CfgXmlAccessService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
-	public void doSecondPass(Map persistentClasses) throws MappingException {
+	public Class<CfgXmlAccessService> getServiceInitiated() {
+public class ConfigLoader {
+				public JaxbCfgProcessor initialize() {
+	public ConfigLoader(BootstrapServiceRegistry bootstrapServiceRegistry) {
+	public LoadedConfig loadConfigXmlResource(String cfgXmlResourceName) {
+	public LoadedConfig loadConfigXmlFile(File cfgXmlFile) {
+	public LoadedConfig loadConfigXmlUrl(URL url) {
+	public Properties loadProperties(String resourceName) {
+	public Properties loadProperties(File file) {
-public class JaxbProcessor {
+public class JaxbCfgProcessor {
-	public static final String HIBERNATE_CONFIGURATION_URI = "http://www.hibernate.org/xsd/hibernate-configuration";
+	public static final String HIBERNATE_CONFIGURATION_URI = "http://www.hibernate.org/xsd/orm/cfg";
-	public JaxbProcessor(ClassLoaderService classLoaderService) {
+	public JaxbCfgProcessor(ClassLoaderService classLoaderService) {
-	public JaxbHibernateConfiguration unmarshal(InputStream stream, Origin origin) {
+	public JaxbCfgHibernateConfiguration unmarshal(InputStream stream, Origin origin) {
+public interface CfgXmlAccessService extends Service {
+	public static final String LOADED_CONFIG_KEY = "hibernate.boot.CfgXmlAccessService.key";
+public class LoadedConfig {
+	public String getSessionFactoryName() {
+	public Map getConfigurationValues() {
+	public Map<String, JaccPermissionDeclarations> getJaccPermissionsByContextId() {
+	public JaccPermissionDeclarations getJaccPermissions(String jaccContextId) {
+	public List<CacheRegionDefinition> getCacheRegionDefinitions() {
+	public List<MappingReference> getMappingReferences() {
+	public Map<EventType, Set<String>> getEventListenerMap() {
+	public static LoadedConfig consume(JaxbCfgHibernateConfiguration jaxbCfg) {
+	public void addCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
+	public void addEventListener(EventType eventType, String listenerClass) {
+	public JaccPermissionDeclarations getOrCreateJaccPermissions(String contextId) {
+	public void merge(LoadedConfig incoming) {
+	public static LoadedConfig baseline() {
+public class MappingReference {
+	public static enum Type {
+	public MappingReference(Type type, String reference) {
+	public Type getType() {
+	public String getReference() {
+	public static MappingReference consume(JaxbCfgMappingReferenceType jaxbMapping) {
+	public void apply(MetadataSources metadataSources) {
+public class ClassLoaderAccessImpl implements ClassLoaderAccess {
+	public ClassLoaderAccessImpl(
+	public ClassLoaderAccessImpl(ClassLoader tempClassLoader, ServiceRegistry serviceRegistry) {
+	public Class<?> classForName(String name) {
+	public URL locateResource(String resourceName) {
+public class DefaultCustomEntityDirtinessStrategy implements CustomEntityDirtinessStrategy {
+	public static final DefaultCustomEntityDirtinessStrategy INSTANCE = new DefaultCustomEntityDirtinessStrategy();
+	public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
+	public boolean isDirty(Object entity, EntityPersister persister, Session session) {
+	public void resetDirty(Object entity, EntityPersister persister, Session session) {
+	public void findDirty(
+public class DeploymentResourcesInterpreter {
+	public static final DeploymentResourcesInterpreter INSTANCE = new DeploymentResourcesInterpreter();
+	public static interface DeploymentResources {
+		public Iterable<ClassDescriptor> getClassDescriptors();
+		public Iterable<PackageDescriptor> getPackageDescriptors();
+		public Iterable<MappingFileDescriptor> getMappingFileDescriptors();
+	public DeploymentResources buildDeploymentResources(
+			public Iterable<ClassDescriptor> getClassDescriptors() {
+			public Iterable<PackageDescriptor> getPackageDescriptors() {
+			public Iterable<MappingFileDescriptor> getMappingFileDescriptors() {
+public class InFlightMetadataCollectorImpl implements InFlightMetadataCollector {
+	public InFlightMetadataCollectorImpl(
+	public UUID getUUID() {
+	public MetadataBuildingOptions getMetadataBuildingOptions() {
+	public TypeResolver getTypeResolver() {
+	public Database getDatabase() {
+	public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory) {
+	public Map<String, SQLFunction> getSqlFunctionMap() {
+	public void validate() throws MappingException {
+	public Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
+	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
+	public SessionFactoryBuilder getSessionFactoryBuilder() {
+	public SessionFactory buildSessionFactory() {
+	public java.util.Collection<PersistentClass> getEntityBindings() {
+	public Map<String, PersistentClass> getEntityBindingMap() {
+	public PersistentClass getEntityBinding(String entityName) {
+	public void addEntityBinding(PersistentClass persistentClass) throws DuplicateMappingException {
+	public java.util.Collection<Collection> getCollectionBindings() {
+	public Collection getCollectionBinding(String role) {
+	public void addCollectionBinding(Collection collection) throws DuplicateMappingException {
+	public TypeDefinition getTypeDefinition(String registrationKey) {
+	public void addTypeDefinition(TypeDefinition typeDefinition) {
+	public void addAttributeConverter(AttributeConverterDefinition definition) {
+	public static AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
+	public static AttributeConverterDefinition toAttributeConverterDefinition(AttributeConverter attributeConverter) {
+	public void addAttributeConverter(Class<? extends AttributeConverter> converterClass) {
+	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
+	public Map<String, FilterDefinition> getFilterDefinitions() {
+	public FilterDefinition getFilterDefinition(String name) {
+	public void addFilterDefinition(FilterDefinition filterDefinition) {
+	public java.util.Collection<FetchProfile> getFetchProfiles() {
+	public FetchProfile getFetchProfile(String name) {
+	public void addFetchProfile(FetchProfile profile) {
+	public IdentifierGeneratorDefinition getIdentifierGenerator(String name) {
+	public java.util.Collection<Table> collectTableMappings() {
+	public void addIdentifierGenerator(IdentifierGeneratorDefinition generator) {
+	public void addDefaultIdentifierGenerator(IdentifierGeneratorDefinition generator) {
+	public NamedEntityGraphDefinition getNamedEntityGraph(String name) {
+	public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs() {
+	public void addNamedEntityGraph(NamedEntityGraphDefinition definition) {
+	public NamedQueryDefinition getNamedQueryDefinition(String name) {
+	public java.util.Collection<NamedQueryDefinition> getNamedQueryDefinitions() {
+	public void addNamedQuery(NamedQueryDefinition def) {
+	public void addDefaultQuery(NamedQueryDefinition queryDefinition) {
+	public NamedSQLQueryDefinition getNamedNativeQueryDefinition(String name) {
+	public java.util.Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
+	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
+	public void addDefaultNamedNativeQuery(NamedSQLQueryDefinition query) {
+	public java.util.Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions() {
+	public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
+	public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
+	public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions() {
+	public ResultSetMappingDefinition getResultSetMapping(String name) {
+	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
+	public void applyResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
+	public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
+	public Map<String,String> getImports() {
+	public void addImport(String importName, String entityName) {
+	public Table addTable(
+	public Table addDenormalizedTable(
+	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
+	public String getIdentifierPropertyName(String entityName) throws MappingException {
+	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
+	public void addTableNameBinding(Identifier logicalName, Table table) {
+	public void addTableNameBinding(String schema, String catalog, String logicalName, String realTableName, Table denormalizedSuperTable) {
+	public String getLogicalTableName(Table ownerTable) {
+	public String getPhysicalTableName(Identifier logicalName) {
+	public String getPhysicalTableName(String logicalName) {
+		public void addBinding(Identifier logicalName, Column physicalColumn) {
+	public void addColumnNameBinding(Table table, String logicalName, Column column) throws DuplicateMappingException {
+	public void addColumnNameBinding(Table table, Identifier logicalName, Column column) throws DuplicateMappingException {
+	public String getPhysicalColumnName(Table table, String logicalName) throws MappingException {
+	public String getPhysicalColumnName(Table table, Identifier logicalName) throws MappingException {
+	public String getLogicalColumnName(Table table, String physicalName) throws MappingException {
+	public String getLogicalColumnName(Table table, Identifier physicalName) throws MappingException {
+	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
+	public AnnotatedClassType getClassType(XClass clazz) {
+	public AnnotatedClassType addClassType(XClass clazz) {
+	public void addAnyMetaDef(AnyMetaDef defAnn) {
+	public AnyMetaDef getAnyMetaDef(String name) {
+	public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
+	public MappedSuperclass getMappedSuperclass(Class type) {
+	public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
+	public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
+	public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
+	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
+	public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
+	public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
+	public String getFromMappedBy(String entityName, String propertyName) {
+	public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
+	public String getPropertyReferencedAssociation(String entityName, String propertyName) {
+		public final String referencedClass;
+		public final String propertyName;
+		public final boolean unique;
+		public DelayedPropertyReferenceHandlerAnnotationImpl(String referencedClass, String propertyName, boolean unique) {
+		public void process(InFlightMetadataCollector metadataCollector) {
+	public void addPropertyReference(String referencedClass, String propertyName) {
+	public void addDelayedPropertyReferenceHandler(DelayedPropertyReferenceHandler handler) {
+	public void addUniquePropertyReference(String referencedClass, String propertyName) {
+	public void addUniqueConstraints(Table table, List uniqueConstraints) {
+	public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
+	public void addJpaIndexHolders(Table table, List<JPAIndexHolder> holders) {
+	public EntityTableXref getEntityTableXref(String entityName) {
+	public EntityTableXref addEntityTableXref(
+	public Map<String, Join> getJoins(String entityName) {
+	public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) {
+		public EntityTableXrefImpl(Identifier primaryTableLogicalName, Table primaryTable, EntityTableXrefImpl superEntityTableXref) {
+		public void addSecondaryTable(LocalMetadataBuildingContext buildingContext, Identifier logicalName, Join secondaryTableJoin) {
+		public void addSecondaryTable(Identifier logicalName, Join secondaryTableJoin) {
+		public Table getPrimaryTable() {
+		public Table resolveTable(Identifier tableName) {
+		public Join locateJoin(Identifier tableName) {
+	public void addSecondPass(SecondPass secondPass) {
+	public void addSecondPass(SecondPass secondPass, boolean onTopOfTheQueue) {
+								public Identifier getTableName() {
+								public List<Identifier> getColumnNames() {
+								public Identifier getReferencedTableName() {
+								public List<Identifier> getReferencedColumnNames() {
+								public MetadataBuildingContext getBuildingContext() {
+							public MetadataBuildingContext getBuildingContext() {
+							public Identifier getTableName() {
+							public List<Identifier> getColumnNames() {
+							public MetadataBuildingContext getBuildingContext() {
+							public Identifier getTableName() {
+							public List<Identifier> getColumnNames() {
+	public NaturalIdUniqueKeyBinder locateNaturalIdUniqueKeyBinder(String entityName) {
+	public void registerNaturalIdUniqueKeyBinder(String entityName, NaturalIdUniqueKeyBinder ukBinder) {
+	public boolean isInSecondPass() {
+	public MetadataImpl buildMetadataInstance(MetadataBuildingContext buildingContext) {
+public class MetadataBuilderImpl implements MetadataBuilder, TypeContributions {
+	public MetadataBuilderImpl(MetadataSources sources) {
+	public MetadataBuilderImpl(MetadataSources sources, StandardServiceRegistry serviceRegistry) {
+	public MetadataBuilder withImplicitSchemaName(String implicitSchemaName) {
+	public MetadataBuilder withImplicitCatalogName(String implicitCatalogName) {
+	public MetadataBuilder with(ImplicitNamingStrategy namingStrategy) {
+	public MetadataBuilder with(PhysicalNamingStrategy namingStrategy) {
+	public MetadataBuilder with(ReflectionManager reflectionManager) {
+	public MetadataBuilder with(SharedCacheMode sharedCacheMode) {
+	public MetadataBuilder with(AccessType implicitCacheAccessType) {
+	public MetadataBuilder with(IndexView jandexView) {
+	public MetadataBuilder with(ScanOptions scanOptions) {
+	public MetadataBuilder with(ScanEnvironment scanEnvironment) {
+	public MetadataBuilder with(Scanner scanner) {
+	public MetadataBuilder with(ArchiveDescriptorFactory factory) {
+	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled) {
+	public MetadataBuilder withExplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
+	public MetadataBuilder withImplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
+	public MetadataBuilder withImplicitForcingOfDiscriminatorsInSelect(boolean supported) {
+	public MetadataBuilder withNationalizedCharacterData(boolean enabled) {
+	public MetadataBuilder with(BasicType type) {
+	public MetadataBuilder with(UserType type, String[] keys) {
+	public MetadataBuilder with(CompositeUserType type, String[] keys) {
+	public MetadataBuilder with(TypeContributor typeContributor) {
+	public void contributeType(BasicType type) {
+	public void contributeType(UserType type, String[] keys) {
+	public void contributeType(CompositeUserType type, String[] keys) {
+	public MetadataBuilder with(CacheRegionDefinition cacheRegionDefinition) {
+	public MetadataBuilder with(ClassLoader tempClassLoader) {
+	public MetadataBuilder setSourceProcessOrdering(List<MetadataSourceType> sourceProcessOrdering) {
+	public MetadataBuilder allowSpecjSyntax() {
+//	public MetadataBuilder with(PersistentAttributeMemberResolver resolver) {
+	public MetadataImpl build() {
+	public static class MappingDefaultsImpl implements MappingDefaults {
+		public MappingDefaultsImpl(StandardServiceRegistry serviceRegistry) {
+						public AccessType convert(Object value) {
+		public String getImplicitSchemaName() {
+		public String getImplicitCatalogName() {
+		public boolean shouldImplicitlyQuoteIdentifiers() {
+		public String getImplicitIdColumnName() {
+		public String getImplicitTenantIdColumnName() {
+		public String getImplicitDiscriminatorColumnName() {
+		public String getImplicitPackageName() {
+		public boolean isAutoImportEnabled() {
+		public String getImplicitCascadeStyleName() {
+		public String getImplicitPropertyAccessorName() {
+		public boolean areEntitiesImplicitlyLazy() {
+		public boolean areCollectionsImplicitlyLazy() {
+		public AccessType getImplicitCacheAccessType() {
+	public static class MetadataBuildingOptionsImpl implements MetadataBuildingOptions {
+		public MetadataBuildingOptionsImpl(StandardServiceRegistry serviceRegistry) {
+						public SharedCacheMode convert(Object value) {
+						public AccessType convert(Object value) {
+		public StandardServiceRegistry getServiceRegistry() {
+		public MappingDefaults getMappingDefaults() {
+		public List<BasicType> getBasicTypeRegistrations() {
+		public IndexView getJandexView() {
+		public ScanOptions getScanOptions() {
+		public ScanEnvironment getScanEnvironment() {
+		public Object getScanner() {
+		public ArchiveDescriptorFactory getArchiveDescriptorFactory() {
+		public ClassLoader getTempClassLoader() {
+		public ImplicitNamingStrategy getImplicitNamingStrategy() {
+		public PhysicalNamingStrategy getPhysicalNamingStrategy() {
+		public ReflectionManager getReflectionManager() {
+		public SharedCacheMode getSharedCacheMode() {
+		public AccessType getImplicitCacheAccessType() {
+		public boolean isUseNewIdentifierGenerators() {
+		public MultiTenancyStrategy getMultiTenancyStrategy() {
+		public List<CacheRegionDefinition> getCacheRegionDefinitions() {
+		public boolean ignoreExplicitDiscriminatorsForJoinedInheritance() {
+		public boolean createImplicitDiscriminatorsForJoinedInheritance() {
+		public boolean shouldImplicitlyForceDiscriminatorInSelect() {
+		public boolean useNationalizedCharacterData() {
+		public boolean isSpecjProprietarySyntaxEnabled() {
+		public List<MetadataSourceType> getSourceProcessOrdering() {
+		public static interface JpaOrmXmlPersistenceUnitDefaults {
+			public String getDefaultSchemaName();
+			public String getDefaultCatalogName();
+			public boolean shouldImplicitlyQuoteIdentifiers();
+		public void apply(JpaOrmXmlPersistenceUnitDefaults jpaOrmXmlPersistenceUnitDefaults) {
+//		public PersistentAttributeMemberResolver getPersistentAttributeMemberResolver() {
+public class MetadataBuildingContextRootImpl implements MetadataBuildingContext {
+	public MetadataBuildingContextRootImpl(
+	public MetadataBuildingOptions getBuildingOptions() {
+	public MappingDefaults getMappingDefaults() {
+	public InFlightMetadataCollector getMetadataCollector() {
+	public ClassLoaderAccess getClassLoaderAccess() {
+	public ObjectNameNormalizer getObjectNameNormalizer() {
+public class MetadataBuildingProcess {
+	public static MetadataImpl build(
+						public JandexInitializer getJandexInitializer() {
+			public void prepare() {
+			public void processTypeDefinitions() {
+			public void processQueryRenames() {
+			public void processNamedQueries() {
+			public void processAuxiliaryDatabaseObjectDefinitions() {
+			public void processIdentifierGenerators() {
+			public void processFilterDefinitions() {
+			public void processFetchProfiles() {
+			public void prepareForEntityHierarchyProcessing() {
+			public void processEntityHierarchies(Set<String> processedEntityNames) {
+			public void postProcessEntityHierarchies() {
+			public void processResultSetMappings() {
+			public void finishUp() {
+//			public IndexView getJandexIndex() {
+//			public StandardServiceRegistry getServiceRegistry() {
+			public void contributeType(org.hibernate.type.BasicType type) {
+			public void contributeType(UserType type, String[] keys) {
+			public void contributeType(CompositeUserType type, String[] keys) {
+public class MetadataImpl implements MetadataImplementor, Serializable {
+	public MetadataImpl(
+	public MetadataBuildingOptions getMetadataBuildingOptions() {
+	public TypeResolver getTypeResolver() {
+	public SessionFactoryBuilder getSessionFactoryBuilder() {
+	public SessionFactory buildSessionFactory() {
+	public UUID getUUID() {
+	public Database getDatabase() {
+	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
+	public java.util.Collection<PersistentClass> getEntityBindings() {
+	public PersistentClass getEntityBinding(String entityName) {
+	public java.util.Collection<Collection> getCollectionBindings() {
+	public Collection getCollectionBinding(String role) {
+	public Map<String, String> getImports() {
+	public NamedQueryDefinition getNamedQueryDefinition(String name) {
+	public java.util.Collection<NamedQueryDefinition> getNamedQueryDefinitions() {
+	public NamedSQLQueryDefinition getNamedNativeQueryDefinition(String name) {
+	public java.util.Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
+	public java.util.Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions() {
+	public ResultSetMappingDefinition getResultSetMapping(String name) {
+	public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions() {
+	public TypeDefinition getTypeDefinition(String typeName) {
+	public Map<String, FilterDefinition> getFilterDefinitions() {
+	public FilterDefinition getFilterDefinition(String name) {
+	public FetchProfile getFetchProfile(String name) {
+	public java.util.Collection<FetchProfile> getFetchProfiles() {
+	public NamedEntityGraphDefinition getNamedEntityGraph(String name) {
+	public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs() {
+	public IdentifierGeneratorDefinition getIdentifierGenerator(String name) {
+	public Map<String, SQLFunction> getSqlFunctionMap() {
+	public java.util.Collection<Table> collectTableMappings() {
+	public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory) {
+	public Map<String, ProcedureCallMemento> buildProcedureCallMementos(SessionFactoryImpl sessionFactory) {
+	public void validate() throws MappingException {
+	public Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
+	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
+	public String getIdentifierPropertyName(String entityName) throws MappingException {
+	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
+public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
+	public SessionFactoryBuilder with(Interceptor interceptor) {
+	public SessionFactoryBuilder with(CustomEntityDirtinessStrategy dirtinessStrategy) {
+	public SessionFactoryBuilder with(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
+	public SessionFactoryBuilder add(SessionFactoryObserver... observers) {
+	public SessionFactoryBuilder add(EntityNameResolver... entityNameResolvers) {
+	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate) {
+	public SessionFactoryBuilder with(EntityTuplizerFactory entityTuplizerFactory) {
+	public SessionFactoryBuilder with(EntityMode entityMode, Class<? extends EntityTuplizer> tuplizerClass) {
+	public SessionFactoryBuilder withValidatorFactory(Object validatorFactory) {
+	public SessionFactoryBuilder withBeanManager(Object beanManager) {
+	public SessionFactoryBuilder with(String registrationName, SQLFunction sqlFunction) {
+	public SessionFactory build() {
+		public SessionFactoryOptionsImpl(StandardServiceRegistry serviceRegistry) {
+		public StandardServiceRegistry getServiceRegistry() {
+		public Interceptor getInterceptor() {
+		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
+		public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
+		public Settings getSettings() {
+		public SessionFactoryObserver[] getSessionFactoryObservers() {
+		public EntityNameResolver[] getEntityNameResolvers() {
+		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
+		public Map<String, SQLFunction> getCustomSqlFunctionMap() {
+		public Object getBeanManagerReference() {
+		public Object getValidatorFactoryReference() {
+public class StandardEntityNotFoundDelegate implements EntityNotFoundDelegate {
+	public static final StandardEntityNotFoundDelegate INSTANCE = new StandardEntityNotFoundDelegate();
+	public void handleEntityNotFound(String entityName, Serializable id) {
+	public static final String UNKNOWN_FILE_PATH = "<unknown>";
+	public boolean equals(Object o) {
+	public int hashCode() {
+	public String toString() {
+public enum SourceType {
+	public String getLegacyTypeText() {
+public class CacheAccessTypeConverter {
+	public static AccessType fromXml(String name) {
+	public static String toXml(AccessType accessType) {
+public class CacheModeConverter {
+	public static CacheMode fromXml(String name) {
+	public static String toXml(CacheMode cacheMode) {
+public class EntityModeConverter {
+	public static EntityMode fromXml(String name) {
+	public static String toXml(EntityMode entityMode) {
+public class ExecuteUpdateResultCheckStyleConverter {
+	public static ExecuteUpdateResultCheckStyle fromXml(String name) {
+	public static String toXml(ExecuteUpdateResultCheckStyle style) {
+public class FlushModeConverter {
+	public static FlushMode fromXml(String name) {
+	public static String toXml(FlushMode mode) {
+public class GenerationTimingConverter {
+	public static GenerationTiming fromXml(String name) {
+	public static String toXml(GenerationTiming generationTiming) {
+public class ImplicitResultSetMappingDefinition implements ResultSetMappingBindingDefinition {
+	public ImplicitResultSetMappingDefinition(
+	public String getName() {
+	public List getValueMappingSources() {
+	public static class Builder {
+		public Builder(String queryName) {
+		public void addReturn(JaxbHbmNativeQueryScalarReturnType scalarReturn) {
+		public void addReturn(JaxbHbmNativeQueryReturnType rootReturn) {
+		public void addReturn(JaxbHbmNativeQueryJoinReturnType joinReturn) {
+		public void addReturn(JaxbHbmNativeQueryCollectionLoadReturnType collectionLoadReturn) {
+		public boolean hasAnyReturns() {
+		public ImplicitResultSetMappingDefinition build() {
+public class LockModeConverter {
+	public static LockMode fromXml(String name) {
+	public static String toXml(LockMode lockMode) {
+public class OptimisticLockStyleConverter {
+	public static OptimisticLockStyle fromXml(String name) {
+	public static String toXml(OptimisticLockStyle lockMode) {
+public interface AttributeMapping {
+	public String getName();
+	public String getAccess();
-public interface MetaAttributeContainer {
-	public List<JaxbMetaElement> getMeta();
+public interface ConfigParameterContainer {
+	public List<JaxbHbmConfigParameterType> getConfigParameters();
-public interface EntityElement extends MetaAttributeContainer {
+public interface EntityInfo extends ToolingHintContainer {
-    public String getBatchSize();
+    public int getBatchSize();
-	public List<JaxbTuplizerElement> getTuplizer();
+	public List<JaxbHbmTuplizerType> getTuplizer();
-	public JaxbLoaderElement getLoader();
-	public JaxbSqlInsertElement getSqlInsert();
-	public JaxbSqlUpdateElement getSqlUpdate();
-	public JaxbSqlDeleteElement getSqlDelete();
+	public JaxbHbmLoaderType getLoader();
+	public JaxbHbmCustomSqlDmlType getSqlInsert();
+	public JaxbHbmCustomSqlDmlType getSqlUpdate();
+	public JaxbHbmCustomSqlDmlType getSqlDelete();
-	public List<JaxbSynchronizeElement> getSynchronize();
+	public List<JaxbHbmSynchronizeType> getSynchronize();
-	public List<JaxbFetchProfileElement> getFetchProfile();
+	public List<JaxbHbmFetchProfileType> getFetchProfile();
-    public List<JaxbResultsetElement> getResultset();
+    public List<JaxbHbmResultSetMappingType> getResultset();
-    public List<Object> getQueryOrSqlQuery();
+	public List<JaxbHbmNamedNativeQueryType> getSqlQuery();
+	public List<JaxbHbmNamedQueryType> getQuery();
-	public List<Object> getPropertyOrManyToOneOrOneToOne();
+	public List getAttributes();
+public interface NativeQueryNonScalarRootReturn {
-public interface PluralAttributeElement extends MetaAttributeContainer {
-	public String getName();
-	public String getAccess();
+public interface PluralAttributeInfo extends AttributeMapping, TableInformationContainer, ToolingHintContainer {
+	public JaxbHbmKeyType getKey();
-	public JaxbKeyElement getKey();
+	public JaxbHbmBasicCollectionElementType getElement();
+	public JaxbHbmCompositeCollectionElementType getCompositeElement();
+	public JaxbHbmOneToManyCollectionElementType getOneToMany();
+	public JaxbHbmManyToManyCollectionElementType getManyToMany();
+    public JaxbHbmManyToAnyCollectionElementType getManyToAny();
-	public JaxbElementElement getElement();
-	public JaxbCompositeElementElement getCompositeElement();
-	public JaxbOneToManyElement getOneToMany();
-	public JaxbManyToManyElement getManyToMany();
-    public JaxbManyToAnyElement getManyToAny();
-	public String getSchema();
-	public String getCatalog();
-	public String getTable();
-	public String getSubselect();
-	public String getSubselectAttribute();
-	public JaxbLoaderElement getLoader();
-	public JaxbSqlInsertElement getSqlInsert();
-    public JaxbSqlUpdateElement getSqlUpdate();
-    public JaxbSqlDeleteElement getSqlDelete();
-    public JaxbSqlDeleteAllElement getSqlDeleteAll();
+	public JaxbHbmLoaderType getLoader();
+	public JaxbHbmCustomSqlDmlType getSqlInsert();
+    public JaxbHbmCustomSqlDmlType getSqlUpdate();
+    public JaxbHbmCustomSqlDmlType getSqlDelete();
+    public JaxbHbmCustomSqlDmlType getSqlDeleteAll();
-	public List<JaxbSynchronizeElement> getSynchronize();
+	public List<JaxbHbmSynchronizeType> getSynchronize();
-	public JaxbCacheElement getCache();
-	public List<JaxbFilterElement> getFilter();
+	public JaxbHbmCacheType getCache();
+	public List<JaxbHbmFilterType> getFilter();
-	public JaxbFetchAttributeWithSubselect getFetch();
-	public JaxbLazyAttributeWithExtra getLazy();
-	public JaxbOuterJoinAttribute getOuterJoin();
+	public JaxbHbmFetchStyleWithSubselectEnum getFetch();
+	public JaxbHbmLazyWithExtraEnum getLazy();
+	public JaxbHbmOuterJoinEnum getOuterJoin();
-	public String getBatchSize();
+	public int getBatchSize();
-public abstract class IdBagPluralAttributeElementAdapter implements PluralAttributeElement {
-	public JaxbOneToManyElement getOneToMany() {
+public abstract class PluralAttributeInfoIdBagAdapter
+	public JaxbHbmOneToManyCollectionElementType getOneToMany() {
+public abstract class PluralAttributeInfoPrimitiveArrayAdapter
+	public boolean isInverse() {
+	public JaxbHbmLazyWithExtraEnum getLazy() {
+	public JaxbHbmOneToManyCollectionElementType getOneToMany() {
+	public JaxbHbmCompositeCollectionElementType getCompositeElement() {
+	public JaxbHbmManyToManyCollectionElementType getManyToMany() {
+	public JaxbHbmManyToAnyCollectionElementType getManyToAny() {
+	public List<JaxbHbmFilterType> getFilter() {
+	public String getCascade() {
+public interface ResultSetMappingBindingDefinition {
+public interface SecondaryTableContainer {
+	public List<JaxbHbmSecondaryTableType> getJoin();
+public interface SimpleValueTypeInfo {
+	public String getTypeAttribute();
+	public JaxbHbmTypeSpecificationType getType();
+public interface SingularAttributeInfo extends AttributeMapping, ToolingHintContainer {
-public interface SubEntityElement extends EntityElement {
+public interface SubEntityInfo extends EntityInfo {
+public interface TableInformationContainer {
+	public String getSchema();
+	public String getCatalog();
+	public String getTable();
+	public String getSubselect();
+	public String getSubselectAttribute();
+public interface ToolingHintContainer {
+	public List<JaxbHbmToolingHintType> getToolingHints();
+public interface TypeContainer {
+public abstract class AbstractBinder implements Binder {
+	public boolean isValidationEnabled() {
+	public Binding bind(InputStream stream, Origin origin) {
+	public Binding bind(Source source, Origin origin) {
+public class CacheableFileXmlSource extends XmlSource {
+	public CacheableFileXmlSource(Origin origin, File xmlFile, boolean strict) {
+	public Binding doBind(Binder binder) {
+	public boolean handleEvent(ValidationEvent validationEvent) {
+	public int getLineNumber() {
+	public int getColumnNumber() {
+	public String getMessage() {
-public class MyLegacyNamingStrategyDelegator extends LegacyNamingStrategyDelegator {
+public class FileXmlSource extends XmlSource {
-	public MyLegacyNamingStrategyDelegator() {
+	public FileXmlSource(Origin origin, File file) {
-	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
+	public Binding doBind(Binder binder) {
-		public String toPhysicalColumnName(String columnName) {
+	public static Binding doBind(Binder binder, File file, Origin origin) {
+public class InputStreamXmlSource extends XmlSource {
+	public InputStreamXmlSource(Origin origin, InputStream inputStream, boolean autoClose) {
+	public Binding doBind(Binder binder) {
+	public static Binding doBind(Binder binder, InputStream inputStream, Origin origin, boolean autoClose) {
+public class JarFileEntryXmlSource extends XmlSource {
+	public JarFileEntryXmlSource(
+	public Binding doBind(Binder binder) {
+public class JaxpSourceXmlSource extends XmlSource {
+	public JaxpSourceXmlSource(Origin origin, Source jaxpSource) {
+	public Binding doBind(Binder binder) {
+public class MappingBinder extends AbstractBinder {
+	public MappingBinder() {
+	public MappingBinder(boolean validateXml) {
+			public Node readNode(XMLEventReader reader) throws XMLStreamException {
+	public static class DelayedOrmXmlData {
+		public DelayedOrmXmlData(
+		public XMLEventReader getStaxEventReader() {
+		public StartElement getRootElementStartEvent() {
+		public Origin getOrigin() {
+public class UrlXmlSource extends XmlSource {
+	public UrlXmlSource(Origin origin, URL url) {
+	public Binding doBind(Binder binder) {
+public abstract class BaseXMLEventReader extends EventReaderDelegate {
+	public BaseXMLEventReader(XMLEventReader reader) {
+	public final XMLEvent nextEvent() throws XMLStreamException {
+	public final Object next() {
+	public final String getElementText() throws XMLStreamException {
+	public final XMLEvent nextTag() throws XMLStreamException {
+public class BufferedXMLEventReader extends BaseXMLEventReader {
+	public BufferedXMLEventReader(XMLEventReader reader) {
+	public BufferedXMLEventReader(XMLEventReader reader, int eventLimit) {
+	public List<XMLEvent> getBuffer() {
+	public boolean hasNext() {
+	public XMLEvent peek() throws XMLStreamException {
+	public void mark() {
+	public void mark(int eventLimit) {
+	public void reset() {
+	public void close() throws XMLStreamException {
+	public int bufferSize() {
+	public void remove() {
+public abstract class FilteringXMLEventReader extends BaseXMLEventReader {
+	public FilteringXMLEventReader(XMLEventReader reader) {
+	public boolean hasNext() {
+	public final XMLEvent peek() throws XMLStreamException {
+public class HbmEventReader extends EventReaderDelegate {
+	public HbmEventReader(XMLEventReader reader) {
+	public HbmEventReader(XMLEventReader reader, XMLEventFactory xmlEventFactory) {
+	public XMLEvent peek() throws XMLStreamException {
+	public XMLEvent nextEvent() throws XMLStreamException {
+public class JpaOrmXmlEventReader extends EventReaderDelegate {
+	public JpaOrmXmlEventReader(XMLEventReader reader) {
+	public JpaOrmXmlEventReader(XMLEventReader reader, XMLEventFactory xmlEventFactory) {
+	public XMLEvent peek() throws XMLStreamException {
+	public XMLEvent nextEvent() throws XMLStreamException {
+	public static class BadVersionException extends RuntimeException {
+		public BadVersionException(String requestedVersion) {
+		public String getRequestedVersion() {
+public enum LocalSchema {
+	public String getNamespaceUri() {
+	public String getCurrentVersion() {
+	public Schema getSchema() {
+public class LocalSchemaLocator {
+	public static URL resolveLocalSchemaUrl(String schemaResourceName) {
+	public static Schema resolveLocalSchema(String schemaName){
+	public static Schema resolveLocalSchema(URL schemaUrl) {
+public class LocalXmlResourceResolver implements javax.xml.stream.XMLResolver {
+	public static final LocalXmlResourceResolver INSTANCE = new LocalXmlResourceResolver();
+	public static final String CLASSPATH_EXTENSION_URL_BASE = "classpath://";
+	public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
+	public static final NamespaceSchemaMapping INITIAL_JPA_XSD_MAPPING = new NamespaceSchemaMapping(
+	public static final NamespaceSchemaMapping JPA_XSD_MAPPING = new NamespaceSchemaMapping(
+	public static final NamespaceSchemaMapping HBM_XSD_MAPPING = new NamespaceSchemaMapping(
+	public static final NamespaceSchemaMapping HBM_XSD_MAPPING2 = new NamespaceSchemaMapping(
+	public static final NamespaceSchemaMapping CFG_XSD_MAPPING = new NamespaceSchemaMapping(
+	public static final DtdMapping HBM_DTD_MAPPING = new DtdMapping(
+	public static final DtdMapping LEGACY_HBM_DTD_MAPPING = new DtdMapping(
+	public static final DtdMapping CFG_DTD_MAPPING = new DtdMapping(
+	public static class NamespaceSchemaMapping {
+		public NamespaceSchemaMapping(String namespace, String resourceName) {
+		public boolean matches(String namespace) {
+		public URL getMappedLocalUrl() {
+	public static class DtdMapping {
+		public DtdMapping(String identifierBase, String resourceName) {
+		public String getIdentifierBase() {
+		public boolean matches(String publicId, String systemId) {
+		public URL getMappedLocalUrl() {
+public enum SupportedOrmXsdVersion {
+	public static SupportedOrmXsdVersion parse(String name, Origin origin) {
+	public URL getSchemaUrl() {
+	public Schema getSchema() {
+public class UnsupportedOrmXsdVersionException extends HibernateException {
+	public UnsupportedOrmXsdVersionException(String requestedVersion, Origin origin) {
+public final class XMLStreamConstantsUtils {
+	public static String getEventName(int eventId) {
+public class XmlInfrastructureException extends HibernateException {
+	public XmlInfrastructureException(String message) {
+	public XmlInfrastructureException(String message, Throwable root) {
+public interface Binder {
+	public Binding bind(Source source, Origin origin);
+	public Binding bind(InputStream stream, Origin origin);
-public class JaxbRoot<T> {
+public class Binding<T> implements Serializable {
-	public JaxbRoot(T root, Origin origin) {
+	public Binding(T root, Origin origin) {
+public abstract class XmlSource {
+	public Origin getOrigin() {
+	public abstract Binding doBind(Binder binder);
+public class Caching {
+	public Caching(TruthValue requested) {
+	public Caching(String region, AccessType accessType, boolean cacheLazyProperties) {
+	public Caching(String region, AccessType accessType, boolean cacheLazyProperties, TruthValue requested) {
+	public String getRegion() {
+	public void setRegion(String region) {
+	public AccessType getAccessType() {
+	public void setAccessType(AccessType accessType) {
+	public boolean isCacheLazyProperties() {
+	public void setCacheLazyProperties(boolean cacheLazyProperties) {
+	public TruthValue getRequested() {
+	public void setRequested(TruthValue requested) {
+	public void overlay(CacheRegionDefinition overrides) {
+	public void overlay(Caching overrides) {
+	public String toString() {
+public class CustomSql {
+	public CustomSql(String sql, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
+	public String getSql() {
+	public boolean isCallable() {
+	public ExecuteUpdateResultCheckStyle getCheckStyle() {
+public class IdentifierGeneratorDefinition implements Serializable {
+	public IdentifierGeneratorDefinition(
+	public IdentifierGeneratorDefinition(String name) {
+	public IdentifierGeneratorDefinition(String name, String strategy) {
+	public String getStrategy() {
+	public String getName() {
+	public Map<String, String> getParameters() {
+	public boolean equals(Object o) {
+	public int hashCode() {
+	public String toString() {
+	public static class Builder {
+		public String getName() {
+		public void setName(String name) {
+		public String getStrategy() {
+		public void setStrategy(String strategy) {
+		public void addParam(String name, String value) {
+		public void addParams(Map<String,String> parameters) {
+		public IdentifierGeneratorDefinition build() {
+public interface JavaTypeDescriptor {
+	public String getName();
+public enum TruthValue {
+	public boolean toBoolean(boolean defaultValue) {
+	public static boolean toBoolean(TruthValue value, boolean defaultValue) {
+public class TypeDefinition implements Serializable {
+	public TypeDefinition(
+	public TypeDefinition(
+	public String getName() {
+	public Class getTypeImplementorClass() {
+	public String[] getRegistrationKeys() {
+	public Map<String, String> getParameters() {
+	public Properties getParametersAsProperties() {
+	public boolean equals(Object o) {
+	public int hashCode() {
+	public String toString() {
+public interface EntityNaming {
+	public String getClassName();
+	public String getEntityName();
+	public String getJpaEntityName();
+public class Identifier {
+	public static Identifier toIdentifier(String text) {
+	public static Identifier toIdentifier(String text, boolean quote) {
+	public static boolean isQuoted(String name) {
+	public Identifier(String text, boolean quoted) {
+	public String getText() {
+	public boolean isQuoted() {
+	public String render(Dialect dialect) {
+	public String render() {
+	public String getCanonicalName() {
+	public String toString() {
+	public boolean equals(Object o) {
+	public int hashCode() {
+	public static boolean areEqual(Identifier id1, Identifier id2) {
+	public static Identifier quote(Identifier identifier) {
+public class IllegalIdentifierException extends HibernateException {
+	public IllegalIdentifierException(String s) {
+public interface ImplicitAnyDiscriminatorColumnNameSource extends ImplicitNameSource {
+public interface ImplicitAnyKeyColumnNameSource extends ImplicitNameSource {
+public interface ImplicitBasicColumnNameSource extends ImplicitNameSource {
+	public AttributePath getAttributePath();
+	public boolean isCollectionElement();
+public interface ImplicitCollectionTableNameSource extends ImplicitNameSource {
+	public Identifier getOwningPhysicalTableName();
+	public EntityNaming getOwningEntityNaming();
+	public AttributePath getOwningAttributePath();
+public interface ImplicitConstraintNameSource extends ImplicitNameSource {
+	public Identifier getTableName();
+	public List<Identifier> getColumnNames();
+public interface ImplicitDiscriminatorColumnNameSource extends ImplicitNameSource {
+	public EntityNaming getEntityNaming();
+public interface ImplicitEntityNameSource extends ImplicitNameSource {
+	public EntityNaming getEntityNaming();
+public interface ImplicitForeignKeyNameSource extends ImplicitConstraintNameSource {
+	public Identifier getReferencedTableName();
+	public List<Identifier> getReferencedColumnNames();
+public interface ImplicitIdentifierColumnNameSource extends ImplicitNameSource {
+	public EntityNaming getEntityNaming();
+	public AttributePath getIdentifierAttributePath();
+public interface ImplicitIndexColumnNameSource {
+	public AttributePath getPluralAttributePath();
+	public MetadataBuildingContext getBuildingContext();
+public interface ImplicitIndexNameSource extends ImplicitConstraintNameSource {
+public interface ImplicitJoinColumnNameSource extends ImplicitNameSource {
+	public static enum Nature {
+	public Nature getNature();
+	public EntityNaming getEntityNaming();
+	public AttributePath getAttributePath();
+	public Identifier getReferencedTableName();
+	public Identifier getReferencedColumnName();
+public interface ImplicitJoinTableNameSource extends ImplicitNameSource {
+	public String getOwningPhysicalTableName();
+	public EntityNaming getOwningEntityNaming();
+	public String getNonOwningPhysicalTableName();
+	public EntityNaming getNonOwningEntityNaming();
+	public AttributePath getAssociationOwningAttributePath();
+public interface ImplicitMapKeyColumnNameSource extends ImplicitNameSource {
+	public AttributePath getPluralAttributePath();
+public interface ImplicitNameSource {
+public interface ImplicitNamingStrategy {
+	public Identifier determinePrimaryTableName(ImplicitEntityNameSource source);
+	public Identifier determineJoinTableName(ImplicitJoinTableNameSource source);
+	public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source);
+	public Identifier determineDiscriminatorColumnName(ImplicitDiscriminatorColumnNameSource source);
+	public Identifier determineTenantIdColumnName(ImplicitTenantIdColumnNameSource source);
+	public Identifier determineIdentifierColumnName(ImplicitIdentifierColumnNameSource source);
+	public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source);
+	public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source);
+	public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource source);
+public class ImplicitNamingStrategyComponentPathImpl extends ImplicitNamingStrategyJpaCompliantImpl {
+	public static final ImplicitNamingStrategyComponentPathImpl INSTANCE = new ImplicitNamingStrategyComponentPathImpl();
+	public static void process(AttributePath attributePath, StringBuilder sb) {
+public class ImplicitNamingStrategyJpaCompliantImpl implements ImplicitNamingStrategy, Serializable {
+	public static final ImplicitNamingStrategy INSTANCE = new ImplicitNamingStrategyJpaCompliantImpl();
+	public ImplicitNamingStrategyJpaCompliantImpl() {
+	public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
+	public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
+	public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
+	public Identifier determineIdentifierColumnName(ImplicitIdentifierColumnNameSource source) {
+	public Identifier determineDiscriminatorColumnName(ImplicitDiscriminatorColumnNameSource source) {
+	public Identifier determineTenantIdColumnName(ImplicitTenantIdColumnNameSource source) {
+	public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
+	public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
+	public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource source) {
+	public Identifier determineAnyDiscriminatorColumnName(ImplicitAnyDiscriminatorColumnNameSource source) {
+	public Identifier determineAnyKeyColumnName(ImplicitAnyKeyColumnNameSource source) {
+	public Identifier determineMapKeyColumnName(ImplicitMapKeyColumnNameSource source) {
+	public Identifier determineListIndexColumnName(ImplicitIndexColumnNameSource source) {
+	public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
+	public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource source) {
+	public Identifier determineIndexName(ImplicitIndexNameSource source) {
+public class ImplicitNamingStrategyLegacyHbmImpl extends ImplicitNamingStrategyJpaCompliantImpl {
+	public static final ImplicitNamingStrategyLegacyHbmImpl INSTANCE = new ImplicitNamingStrategyLegacyHbmImpl();
+	public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
+	public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
+public class ImplicitNamingStrategyLegacyJpaImpl extends ImplicitNamingStrategyJpaCompliantImpl {
+	public static final ImplicitNamingStrategyLegacyJpaImpl INSTANCE = new ImplicitNamingStrategyLegacyJpaImpl();
+	public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
+	public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
+	public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
+public interface ImplicitPrimaryKeyJoinColumnNameSource extends ImplicitNameSource {
+public interface ImplicitTenantIdColumnNameSource extends ImplicitNameSource {
+	public EntityNaming getEntityNaming();
-public interface JoinElementSource {
-	public List<JaxbJoinElement> getJoin();
+public interface ImplicitUniqueKeyNameSource extends ImplicitConstraintNameSource {
+public class NamingHelper {
+	public static final NamingHelper INSTANCE = new NamingHelper();
+	public String generateHashedFkName(
+	public String generateHashedFkName(
+					public int compare(Identifier o1, Identifier o2) {
+	public String generateHashedConstraintName(String prefix, Identifier tableName, Identifier... columnNames ) {
+					public int compare(Identifier o1, Identifier o2) {
+	public String generateHashedConstraintName(String prefix, Identifier tableName, List<Identifier> columnNames) {
+	public String hashedName(String s) {
+public interface NamingStrategyHelper {
+	public Identifier determineImplicitName(MetadataBuildingContext buildingContext);
+	public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext);
+	public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext);
+public abstract class ObjectNameNormalizer {
+	public Identifier normalizeIdentifierQuoting(String identifierText) {
+	public Identifier normalizeIdentifierQuoting(Identifier identifier) {
+	public String normalizeIdentifierQuotingAsString(String identifierText) {
+	public String toDatabaseIdentifierText(String identifierText) {
+	public Identifier determineLogicalName(String explicitName, NamingStrategyHelper namingStrategyHelper) {
+public interface PhysicalNamingStrategy {
+	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment);
+	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment);
+	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment);
+	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment);
+	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment);
+public class PhysicalNamingStrategyStandardImpl implements PhysicalNamingStrategy, Serializable {
+	public static final PhysicalNamingStrategyStandardImpl INSTANCE = new PhysicalNamingStrategyStandardImpl();
+	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
+	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
+	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
+	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
+	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
-public abstract class AbstractAuxiliaryDatabaseObject implements AuxiliaryDatabaseObject {
+public abstract class AbstractAuxiliaryDatabaseObject
+	public AbstractAuxiliaryDatabaseObject(boolean beforeTables) {
+	public String getExportIdentifier() {
-	public HashSet getDialectScopes() {
+	public Set getDialectScopes() {
+	public boolean beforeTablesOnCreation() {
+public class AbstractQualifiedName extends QualifiedNameParser.NameParts implements QualifiedName {
+	public AbstractQualifiedName(Schema.Name schemaName, Identifier objectName) {
+	public AbstractQualifiedName(Identifier catalogName, Identifier schemaName, Identifier objectName) {
+public interface AuxiliaryDatabaseObject extends Exportable, Serializable {
+	public boolean appliesToDialect(Dialect dialect);
+	public boolean beforeTablesOnCreation();
+	public String[] sqlCreateStrings(Dialect dialect);
+	public String[] sqlDropStrings(Dialect dialect);
+	public static interface Expandable {
+		public void addDialectScope(String dialectName);
+public class Database {
+	public Database(MetadataBuildingOptions buildingOptions) {
+	public Database(MetadataBuildingOptions buildingOptions, JdbcEnvironment jdbcEnvironment) {
+	public MetadataBuildingOptions getBuildingOptions() {
+	public Dialect getDialect() {
+	public JdbcEnvironment getJdbcEnvironment() {
+	public Identifier toIdentifier(String text) {
+	public PhysicalNamingStrategy getPhysicalNamingStrategy() {
+	public Iterable<Schema> getSchemas() {
+	public Schema getDefaultSchema() {
+	public Schema locateSchema(Identifier catalogName, Identifier schemaName) {
+	public Schema adjustDefaultSchema(Identifier catalogName, Identifier schemaName) {
+	public Schema adjustDefaultSchema(String implicitCatalogName, String implicitSchemaName) {
+	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
+	public Collection<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjects() {
+	public Collection<InitCommand> getInitCommands() {
+	public void addInitCommand(InitCommand initCommand) {
+public interface Exportable {
+	public String getExportIdentifier();
+public interface ExportableProducer {
+	public void registerExportables(Database database);
+public class InitCommand {
+	public InitCommand(String... initCommands) {
+	public String[] getInitCommands() {
+public interface Loggable {
+	public String toLoggableString();
+public interface QualifiedName {
+public class QualifiedNameParser {
+	public static final QualifiedNameParser INSTANCE = new QualifiedNameParser();
+	public static class NameParts implements QualifiedName {
+		public NameParts(Identifier catalogName, Identifier schemaName, Identifier objectName) {
+		public Identifier getCatalogName() {
+		public Identifier getSchemaName() {
+		public Identifier getObjectName() {
+		public String render() {
+		public String toString() {
+		public boolean equals(Object o) {
+		public int hashCode() {
+	public NameParts parse(String text, Identifier defaultCatalog, Identifier defaultSchema) {
+	public NameParts parse(String text) {
+public class QualifiedSequenceName extends AbstractQualifiedName {
+	public QualifiedSequenceName(Identifier catalogName, Identifier schemaName, Identifier sequenceName) {
+	public QualifiedSequenceName(Schema.Name schemaName, Identifier sequenceName) {
+	public Identifier getSequenceName() {
+public class QualifiedTableName extends AbstractQualifiedName {
+	public QualifiedTableName(Identifier catalogName, Identifier schemaName, Identifier tableName) {
+	public QualifiedTableName(Schema.Name schemaName, Identifier tableName) {
+	public Identifier getTableName() {
+public class Schema {
+	public Schema(Database database, Name name) {
+	public Name getName() {
+	public Name getPhysicalName() {
+	public Collection<Table> getTables() {
+	public Table locateTable(Identifier logicalTableName) {
+	public Table createTable(Identifier logicalTableName, boolean isAbstract) {
+	public DenormalizedTable createDenormalizedTable(Identifier logicalTableName, boolean isAbstract, Table includedTable) {
+	public Sequence locateSequence(Identifier name) {
+	public Sequence createSequence(Identifier logicalName, int initialValue, int increment) {
+	public String toString() {
+	public boolean equals(Object o) {
+	public int hashCode() {
+	public Iterable<Sequence> getSequences() {
+	public static class Name {
+		public Name(Identifier catalog, Identifier schema) {
+		public Identifier getCatalog() {
+		public Identifier getSchema() {
+		public String toString() {
+		public boolean equals(Object o) {
+		public int hashCode() {
+public class Sequence implements Exportable {
+	public static class Name extends QualifiedNameParser.NameParts {
+		public Name(
+	public Sequence(Identifier catalogName, Identifier schemaName, Identifier sequenceName) {
+	public Sequence(
+	public QualifiedSequenceName getName() {
+	public String getExportIdentifier() {
+	public int getInitialValue() {
+	public int getIncrementSize() {
+	public void validate(int initialValue, int incrementSize) {
+public class SimpleAuxiliaryDatabaseObject extends AbstractAuxiliaryDatabaseObject {
+	public SimpleAuxiliaryDatabaseObject(
+	public SimpleAuxiliaryDatabaseObject(
+	public SimpleAuxiliaryDatabaseObject(
+	public String[] sqlCreateStrings(Dialect dialect) {
+	public String[] sqlDropStrings(Dialect dialect) {
+public class ConstraintSecondPass implements SecondPass {
+	public ConstraintSecondPass(
+	public void doSecondPass(Map persistentClasses) throws MappingException {
+public interface ImplicitColumnNamingSecondPass extends SecondPass {
+public class OverriddenMappingDefaults implements MappingDefaults {
+	public OverriddenMappingDefaults(
+	public String getImplicitSchemaName() {
+	public String getImplicitCatalogName() {
+	public boolean shouldImplicitlyQuoteIdentifiers() {
+	public String getImplicitIdColumnName() {
+	public String getImplicitTenantIdColumnName() {
+	public String getImplicitDiscriminatorColumnName() {
+	public String getImplicitPackageName() {
+	public boolean isAutoImportEnabled() {
+	public String getImplicitCascadeStyleName() {
+	public String getImplicitPropertyAccessorName() {
+	public boolean areEntitiesImplicitlyLazy() {
+	public boolean areCollectionsImplicitlyLazy() {
+	public AccessType getImplicitCacheAccessType() {
+	public static class Builder {
+		public Builder(MappingDefaults parentDefaults) {
+		public Builder setImplicitSchemaName(String implicitSchemaName) {
+		public Builder setImplicitCatalogName(String implicitCatalogName) {
+		public Builder setImplicitlyQuoteIdentifiers(boolean implicitlyQuoteIdentifiers) {
+		public Builder setImplicitIdColumnName(String implicitIdColumnName) {
+		public Builder setImplicitTenantIdColumnName(String implicitTenantIdColumnName) {
+		public Builder setImplicitDiscriminatorColumnName(String implicitDiscriminatorColumnName) {
+		public Builder setImplicitPackageName(String implicitPackageName) {
+		public Builder setAutoImportEnabled(boolean autoImportEnabled) {
+		public Builder setImplicitCascadeStyleName(String implicitCascadeStyleName) {
+		public Builder setImplicitPropertyAccessorName(String implicitPropertyAccessorName) {
+		public Builder setEntitiesImplicitlyLazy(boolean entitiesImplicitlyLazy) {
+		public Builder setPluralAttributesImplicitlyLazy(boolean pluralAttributesImplicitlyLazy) {
+		public Builder setImplicitCacheAccessType(AccessType implicitCacheAccessType) {
+		public OverriddenMappingDefaults build() {
+public class AnnotationMetadataSourceProcessorImpl implements MetadataSourceProcessor {
+	public AnnotationMetadataSourceProcessorImpl(
+	public void prepare() {
+					public String getDefaultSchemaName() {
+					public String getDefaultCatalogName() {
+					public boolean shouldImplicitlyQuoteIdentifiers() {
+	public void processTypeDefinitions() {
+	public void processQueryRenames() {
+	public void processNamedQueries() {
+	public void processAuxiliaryDatabaseObjectDefinitions() {
+	public void processIdentifierGenerators() {
+	public void processFilterDefinitions() {
+	public void processFetchProfiles() {
+	public void prepareForEntityHierarchyProcessing() {
+	public void processEntityHierarchies(Set<String> processedEntityNames) {
+	public void postProcessEntityHierarchies() {
+	public void processResultSetMappings() {
+	public void finishUp() {
+		public AttributeConverterManager(MetadataBuildingContextRootImpl rootMetadataBuildingContext) {
+		public void addAttributeConverter(AttributeConverterDefinition definition) {
+		public void addAttributeConverter(Class<? extends AttributeConverter> converterClass) {
+	public String name() {
+	public String getTableName() {
+	public List<String> columnNames() {
+	public void addColumnName( String columnName ) {
+	public boolean equals(Object o) {
+	public int hashCode() {
+public abstract class AbstractEntitySourceImpl
+	public static EntityNamingSourceImpl extractEntityNamingSource(
+	public String getXmlNodeName() {
+	public LocalMetadataBuildingContext getLocalMetadataBuildingContext() {
+	public String getTypeName() {
+	public AttributePath getAttributePathBase() {
+	public AttributeRole getAttributeRoleBase() {
+	public Collection<IdentifiableTypeSource> getSubTypes() {
+	public FilterSource[] getFilterSources() {
+	public String inferInLineViewName() {
+			public AttributeSourceContainer getAttributeSourceContainer() {
+			public void addAttributeSource(AttributeSource attributeSource) {
+			public void registerIndexColumn(String constraintName, String logicalTableName, String columnName) {
+			public void registerUniqueKeyColumn(String constraintName, String logicalTableName, String columnName) {
+						public AttributeSourceContainer getAttributeSourceContainer() {
+						public void addAttributeSource(AttributeSource attributeSource) {
+						public void registerIndexColumn(
+						public void registerUniqueKeyColumn(
+	public Origin getOrigin() {
+	public EntityNamingSource getEntityNamingSource() {
+	public Boolean isAbstract() {
+	public boolean isLazy() {
+	public String getProxy() {
+	public int getBatchSize() {
+	public boolean isDynamicInsert() {
+	public boolean isDynamicUpdate() {
+	public boolean isSelectBeforeUpdate() {
+	public Map<EntityMode, String> getTuplizerClassMap() {
+	public String getCustomPersisterClassName() {
+	public String getCustomLoaderName() {
+	public CustomSql getCustomSqlInsert() {
+	public CustomSql getCustomSqlUpdate() {
+	public CustomSql getCustomSqlDelete() {
+	public String[] getSynchronizedTableNames() {
+	public ToolingHintContext getToolingHintContext() {
+	public List<AttributeSource> attributeSources() {
+	public void injectHierarchy(EntityHierarchySourceImpl entityHierarchy) {
+	public EntityHierarchySource getHierarchy() {
+	public Collection<ConstraintSource> getConstraints() {
+	public Map<String,SecondaryTableSource> getSecondaryTableMap() {
+	public List<JpaCallbackSource> getJpaCallbackClasses() {
+	public List<JaxbHbmNamedQueryType> getNamedQueries() {
+	public List<JaxbHbmNamedNativeQueryType> getNamedNativeQueries() {
+	public TruthValue quoteIdentifiersLocalToEntity() {
+public abstract class AbstractHbmSourceNode {
+public abstract class AbstractPluralAssociationElementSourceImpl
+	public AbstractPluralAssociationElementSourceImpl(
+	public AttributeSource getAttributeSource() {
+	public boolean isMappedBy() {
+public abstract class AbstractPluralAttributeSourceImpl
+	public AttributePath getAttributePath() {
+	public AttributeRole getAttributeRole() {
+	public boolean usesJoinTable() {
+	public FilterSource[] getFilterSources() {
+	public PluralAttributeKeySource getKeySource() {
+	public PluralAttributeElementSource getElementSource() {
+	public String getCascadeStyleName() {
+	public boolean isMutable() {
+	public String getMappedBy() {
+	public String inferInLineViewName() {
+	public CollectionIdSource getCollectionIdSource() {
+	public TableSpecificationSource getCollectionTableSpecificationSource() {
+	public String getCollectionTableComment() {
+	public String getCollectionTableCheck() {
+	public String[] getSynchronizedTableNames() {
+	public Caching getCaching() {
+	public String getWhere() {
+	public String getName() {
+	public boolean isSingular() {
+	public HibernateTypeSource getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public boolean isIncludedInOptimisticLocking() {
+	public boolean isInverse() {
+	public String getCustomPersisterClassName() {
+	public String getCustomLoaderName() {
+	public CustomSql getCustomSqlInsert() {
+	public CustomSql getCustomSqlUpdate() {
+	public CustomSql getCustomSqlDelete() {
+	public CustomSql getCustomSqlDeleteAll() {
+	public ToolingHintContext getToolingHintContext() {
+	public FetchCharacteristicsPluralAttribute getFetchCharacteristics() {
+public abstract class AbstractSingularAttributeSourceEmbeddedImpl
+							public AttributeRole getAttributeRoleBase() {
+							public AttributePath getAttributePathBase() {
+							public ToolingHintContext getToolingHintContextBaselineForEmbeddable() {
+							public void registerIndexConstraintColumn(
+							public void registerUniqueKeyConstraintColumn(
+	public AbstractSingularAttributeSourceEmbeddedImpl(
+	public EmbeddableSource getEmbeddableSource() {
+	public String getName() {
+	public boolean isSingular() {
+	public boolean isVirtualAttribute() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public HibernateTypeSource getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public NaturalIdMutability getNaturalIdMutability() {
+	public GenerationTiming getGenerationTiming() {
+public abstract class AbstractToOneAttributeSourceImpl
+	public NaturalIdMutability getNaturalIdMutability() {
+	public boolean isSingular() {
+	public boolean isVirtualAttribute() {
+	public GenerationTiming getGenerationTiming() {
+	public boolean isIgnoreNotFound() {
+	public boolean isMappedBy() {
+	public AttributeSource getAttributeSource() {
+	public boolean createForeignKeyConstraint() {
+public class AttributesHelper {
+	public static interface Callback {
+	public static void processAttributes(
+	public static void processCompositeKeySubAttributes(
+					public AttributeRole getAttributeRoleBase() {
+					public AttributePath getAttributePathBase() {
+					public ToolingHintContext getToolingHintContextBaselineForEmbeddable() {
+					public void registerIndexConstraintColumn(
+					public void registerUniqueKeyConstraintColumn(
+			public String getClazz() {
+			public List<JaxbHbmTuplizerType> getTuplizer() {
+			public String getParent() {
+			public boolean isUnique() {
+			public EmbeddableMapping getEmbeddableMapping() {
+			public String getName() {
+			public String getAccess() {
+			public List<JaxbHbmToolingHintType> getToolingHints() {
+			public boolean isVirtualAttribute() {
+			public Boolean isInsertable() {
+			public Boolean isUpdatable() {
+			public boolean isBytecodeLazy() {
+			public XmlElementMetadata getSourceType() {
+			public String getXmlNodeName() {
+			public AttributePath getAttributePath() {
+			public AttributeRole getAttributeRole() {
+			public boolean isIncludedInOptimisticLocking() {
+			public ToolingHintContext getToolingHintContext() {
+	public static void processBasicAttribute(
+	public static void processEmbeddedAttribute(
+	public static void processDynamicComponentAttribute(
+	public static void processManyToOneAttribute(
+	public static void processOneToOneAttribute(
+	public static void processAnyAttribute(
+	public static void processMapAttribute(
+	public static void processListAttribute(
+	public static void processArrayAttribute(
+	public static void processPrimitiveArrayAttribute(
+	public static void processSetAttribute(
+	public static void processBagAttribute(
+	public static void processIdBagAttribute(
+public class AuxiliaryDatabaseObjectBinder {
+	public static void processAuxiliaryDatabaseObject(
+public class BasicAttributeColumnsAndFormulasSource
+	public BasicAttributeColumnsAndFormulasSource(JaxbHbmBasicAttributeType basicAttributeMapping) {
+	public XmlElementMetadata getSourceType() {
+	public String getSourceName() {
+	public String getFormulaAttribute() {
+	public String getColumnAttribute() {
+	public List getColumnOrFormulaElements() {
+	public SizeSource getSizeSource() {
+	public Boolean isNullable() {
+	public String getIndex() {
+	public boolean isUnique() {
+	public String getUniqueKey() {
+public class BasicAttributePropertySource implements PropertySource {
+	public BasicAttributePropertySource(JaxbHbmBasicAttributeType basicAttributeMapping) {
+	public XmlElementMetadata getSourceType() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public String getPropertyAccessorName() {
+	public String getCascadeStyleName() {
+	public GenerationTiming getGenerationTiming() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isUsedInOptimisticLocking() {
+	public boolean isLazy() {
+	public List<JaxbHbmToolingHintType> getToolingHints() {
+	public Nature getNature() {
+	public String getContainingTableName() {
+	public String getName() {
+	public TruthValue isNullable() {
+	public String getDefaultValue() {
+	public String getSqlType() {
+	public JdbcDataType getDatatype() {
+	public SizeSource getSizeSource() {
+	public String getReadFragment() {
+	public String getWriteFragment() {
+	public boolean isUnique() {
+	public String getCheckCondition() {
+	public String getComment() {
+	public Nature getNature() {
+	public String getName() {
+	public TruthValue isNullable() {
+	public String getDefaultValue() {
+	public String getSqlType() {
+	public JdbcDataType getDatatype() {
+	public SizeSource getSizeSource() {
+	public String getReadFragment() {
+	public String getWriteFragment() {
+	public boolean isUnique() {
+	public String getCheckCondition() {
+	public String getComment() {
+	public String getContainingTableName() {
+public class CompositeIdentifierSingularAttributeSourceBasicImpl
+	public CompositeIdentifierSingularAttributeSourceBasicImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public SizeSource getSizeSource() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public XmlElementMetadata getSourceType() {
+	public boolean isSingular() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public AttributePath getAttributePath() {
+	public boolean isCollectionElement() {
+	public AttributeRole getAttributeRole() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public boolean isVirtualAttribute() {
+	public boolean isIncludedInOptimisticLocking() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public GenerationTiming getGenerationTiming() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public NaturalIdMutability getNaturalIdMutability() {
+	public ToolingHintContext getToolingHintContext() {
+	public MetadataBuildingContext getBuildingContext() {
+public class CompositeIdentifierSingularAttributeSourceManyToOneImpl
+	public CompositeIdentifierSingularAttributeSourceManyToOneImpl(
+			public String getName() {
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public XmlElementMetadata getSourceType() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public AttributePath getAttributePath() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public AttributeRole getAttributeRole() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean isIncludedInOptimisticLocking() {
+	public FetchCharacteristicsSingularAssociationImpl getFetchCharacteristics() {
+	public boolean isVirtualAttribute() {
+	public boolean areValuesNullableByDefault() {
+	public String getReferencedEntityAttributeName() {
+	public String getReferencedEntityName() {
+	public boolean isEmbedXml() {
+	public boolean isUnique() {
+	public ForeignKeyDirection getForeignKeyDirection() {
+	public String getCascadeStyleName() {
+	public String getExplicitForeignKeyName() {
+	public boolean isCascadeDeleteEnabled() {
+	public ToolingHintContext getToolingHintContext() {
+public class ConfigParameterHelper {
+	public static Map<String, String> extractConfigParameters(ConfigParameterContainer container) {
+public interface EmbeddableSourceContainer {
+	public AttributeRole getAttributeRoleBase();
+	public AttributePath getAttributePathBase();
+	public ToolingHintContext getToolingHintContextBaselineForEmbeddable();
+	public void registerIndexConstraintColumn(String constraintName, String logicalTableName, String columnName);
+	public void registerUniqueKeyConstraintColumn(String constraintName, String logicalTableName, String columnName);
+public class EmbeddableSourceImpl extends AbstractHbmSourceNode implements EmbeddableSource {
+	public EmbeddableSourceImpl(
+			public String getName() {
+					public AttributeSourceContainer getAttributeSourceContainer() {
+					public void addAttributeSource(AttributeSource attributeSource) {
+					public void registerIndexColumn(String constraintName, String logicalTableName, String columnName) {
+					public void registerUniqueKeyColumn(
+	public JavaTypeDescriptor getTypeDescriptor() {
+	public String getParentReferenceAttributeName() {
+	public Map<EntityMode,String> getTuplizerClassMap() {
+	public boolean isDynamic() {
+	public boolean isUnique() {
+	public AttributePath getAttributePathBase() {
+	public AttributeRole getAttributeRoleBase() {
+	public List<AttributeSource> attributeSources() {
+	public LocalMetadataBuildingContext getLocalMetadataBuildingContext() {
+	public ToolingHintContext getToolingHintContext() {
+public class EmbeddableSourceVirtualImpl extends AbstractHbmSourceNode implements EmbeddableSource {
+		public String getName() {
+	public EmbeddableSourceVirtualImpl(
+					public AttributeSourceContainer getAttributeSourceContainer() {
+					public void addAttributeSource(AttributeSource attributeSource) {
+					public void registerIndexColumn(
+					public void registerUniqueKeyColumn(
+	public JavaTypeDescriptor getTypeDescriptor() {
+	public String getParentReferenceAttributeName() {
+	public Map<EntityMode,String> getTuplizerClassMap() {
+	public boolean isDynamic() {
+	public boolean isUnique() {
+	public AttributePath getAttributePathBase() {
+	public AttributeRole getAttributeRoleBase() {
+	public List<AttributeSource> attributeSources() {
+	public LocalMetadataBuildingContext getLocalMetadataBuildingContext() {
+	public ToolingHintContext getToolingHintContext() {
+public class EntityHierarchyBuilder {
+	public EntityHierarchyBuilder() {
+	public List<EntityHierarchySourceImpl> buildHierarchies() throws HibernateException {
+	public void indexMappingDocument(MappingDocument mappingDocument) {
+		public ExtendsQueueEntry(
+public class EntityHierarchySourceImpl implements EntityHierarchySource {
+	public EntityHierarchySourceImpl(RootEntitySourceImpl rootEntitySource) {
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public SizeSource getSizeSource() {
+					public String getFormulaAttribute() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public Boolean isNullable() {
+			public EntityNaming getEntityNaming() {
+			public MetadataBuildingContext getBuildingContext() {
+			public RelationalValueSource getDiscriminatorRelationalValueSource() {
+			public String getExplicitHibernateTypeName() {
+			public boolean isForced() {
+			public boolean isInserted() {
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getFormulaAttribute() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public Boolean isNullable() {
+			public RelationalValueSource getRelationalValueSource() {
+			public boolean isShared() {
+			public boolean bindAsParameter() {
+	public InheritanceType getHierarchyInheritanceType() {
+	public RootEntitySourceImpl getRoot() {
+	public void processSubclass(SubclassEntitySourceImpl subclassEntitySource) {
+	public IdentifierSource getIdentifierSource() {
+	public VersionAttributeSource getVersionAttributeSource() {
+	public EntityMode getEntityMode() {
+	public boolean isMutable() {
+	public boolean isExplicitPolymorphism() {
+	public String getWhere() {
+	public String getRowId() {
+	public OptimisticLockStyle getOptimisticLockStyle() {
+	public Caching getCaching() {
+	public Caching getNaturalIdCaching() {
+	public DiscriminatorSource getDiscriminatorSource() {
+	public MultiTenancySource getMultiTenancySource() {
+	public Set<String> getContainedEntityNames() {
+	public EntityNamingSourceImpl(String entityName, String className, String jpaEntityName) {
+	public EntityNamingSourceImpl(PersistentClass entityBinding) {
+	public String getEntityName() {
+	public String getClassName() {
+	public String getJpaEntityName() {
+	public String getTypeName() {
+public class FetchCharacteristicsPluralAttributeImpl implements FetchCharacteristicsPluralAttribute {
+	public FetchCharacteristicsPluralAttributeImpl(
+	public FetchTiming getFetchTiming() {
+	public FetchStyle getFetchStyle() {
+	public Integer getBatchSize() {
+	public boolean isExtraLazy() {
+	public static class Builder {
+		public Builder(MappingDefaults mappingDefaults) {
+		public Builder setFetchTiming(FetchTiming fetchTiming) {
+		public Builder setFetchStyle(FetchStyle fetchStyle) {
+		public Builder setBatchSize(Integer batchSize) {
+		public void setExtraLazy(boolean extraLazy) {
+		public FetchCharacteristicsPluralAttributeImpl createPluralAttributeFetchCharacteristics() {
+	public static FetchCharacteristicsPluralAttributeImpl interpret(
+public class FetchCharacteristicsSingularAssociationImpl implements FetchCharacteristicsSingularAssociation {
+	public FetchTiming getFetchTiming() {
+	public FetchStyle getFetchStyle() {
+	public boolean isUnwrapProxies() {
+	public static class Builder {
+		public Builder(MappingDefaults mappingDefaults) {
+		public Builder setFetchTiming(FetchTiming fetchTiming) {
+		public Builder setFetchStyle(FetchStyle fetchStyle) {
+		public Builder setUnwrapProxies(boolean unwrapProxies) {
+		public FetchCharacteristicsSingularAssociationImpl createFetchCharacteristics() {
+	public static FetchCharacteristicsSingularAssociationImpl interpretManyToOne(
+	public static FetchCharacteristicsSingularAssociationImpl interpretManyToManyElement(
+	public static FetchCharacteristicsSingularAssociationImpl interpretOneToOne(
+public class FetchProfileBinder {
+	public static void processFetchProfile(
+	public static void processFetchProfile(
+public class FilterDefinitionBinder {
+	public static void processFilterDefinition(
+public class FilterSourceImpl
+	public FilterSourceImpl(
+	public String getName() {
+	public String getCondition() {
+	public boolean shouldAutoInjectAliases() {
+	public Map<String, String> getAliasToTableMap() {
+	public Map<String, String> getAliasToEntityMap() {
+	public Nature getNature() {
+	public String getExpression() {
+	public String getContainingTableName() {
+public interface HbmLocalMetadataBuildingContext extends LocalMetadataBuildingContext {
+	public ToolingHintContext getToolingHintContext();
+	public String determineEntityName(EntityInfo entityElement);
+	public String determineEntityName(String entityName, String clazz);
+	public String qualifyClassName(String name);
+	public PersistentClass findEntityBinding(String entityName, String clazz);
+public class HbmMetadataSourceProcessorImpl implements MetadataSourceProcessor {
+	public HbmMetadataSourceProcessorImpl(
+	public HbmMetadataSourceProcessorImpl(
+	public void prepare() {
+	public void processTypeDefinitions() {
+	public void processQueryRenames() {
+	public void processNamedQueries() {
+	public void processAuxiliaryDatabaseObjectDefinitions() {
+	public void processFilterDefinitions() {
+	public void processFetchProfiles() {
+	public void processIdentifierGenerators() {
+	public void prepareForEntityHierarchyProcessing() {
+	public void processEntityHierarchies(Set<String> processedEntityNames) {
+	public void postProcessEntityHierarchies() {
+	public void processResultSetMappings() {
+	public void finishUp() {
+public class Helper {
+	public static InheritanceType interpretInheritanceType(JaxbHbmEntityBaseDefinition entityElement) {
+	public static CustomSql buildCustomSql(JaxbHbmCustomSqlDmlType customSqlElement) {
+	public static Caching createCaching(JaxbHbmCacheType cacheElement) {
+	public static Caching createNaturalIdCaching(JaxbHbmNaturalIdCacheType cacheElement) {
+	public static String getPropertyAccessorName(String access, boolean isEmbedded, String defaultAccess) {
+	public static <T> T getValue(T value, T defaultValue){
+	public static Map<String, String> extractParameters(List<JaxbHbmConfigParameterType> xmlParamElements) {
+	public static <T> T coalesce(T... values) {
+	public static TableSpecificationSource createTableSource(
+	public static interface InLineViewNameInferrer {
+		public String inferInLineViewName();
+	public static TableSpecificationSource createTableSource(
+	public static SizeSource interpretSizeSource(Integer length, Integer scale, Integer precision) {
+	public static SizeSource interpretSizeSource(Integer length, String scale, String precision) {
+	public static Class reflectedPropertyClass(
+	public static Class reflectedPropertyClass(
+//					public Class processBeanInfo(BeanInfo beanInfo) throws Exception {
+public class HibernateTypeSourceImpl implements HibernateTypeSource, JavaTypeDescriptorResolvable {
+	public HibernateTypeSourceImpl(TypeContainer typeContainer) {
+	public HibernateTypeSourceImpl(String name) {
+	public HibernateTypeSourceImpl(String name, Map<String, String> parameters) {
+	public HibernateTypeSourceImpl(JavaTypeDescriptor javaTypeDescriptor) {
+	public HibernateTypeSourceImpl(String name, JavaTypeDescriptor javaTypeDescriptor) {
+	public String getName() {
+	public Map<String, String> getParameters() {
+	public JavaTypeDescriptor getJavaType() {
+	public void resolveJavaTypeDescriptor(JavaTypeDescriptor descriptor) {
+	public JavaTypeDescriptor getTypeDescriptor() {
+	public String getParentReferenceAttributeName() {
+	public Map<EntityMode, String> getTuplizerClassMap() {
+	public boolean isDynamic() {
+	public boolean isUnique() {
+	public AttributePath getAttributePathBase() {
+	public AttributeRole getAttributeRoleBase() {
+	public List<AttributeSource> attributeSources() {
+	public LocalMetadataBuildingContext getLocalMetadataBuildingContext() {
+	public ToolingHintContext getToolingHintContext() {
+public class IdentifierGeneratorDefinitionBinder {
+	public static void processIdentifierGeneratorDefinition(
+	public IdentifierSourceAggregatedCompositeImpl(final RootEntitySourceImpl rootEntitySource) {
+	public SingularAttributeSourceEmbedded getIdentifierAttributeSource() {
+	public List<MapsIdSource> getMapsIdSources() {
+	public IdentifierGeneratorDefinition getIndividualAttributeIdGenerator(String identifierAttributeName) {
+	public IdentifierGeneratorDefinition getIdentifierGeneratorDescriptor() {
+	public EntityIdentifierNature getNature() {
+	public EmbeddableSource getEmbeddableSource() {
+	public ToolingHintContext getToolingHintContext() {
+		public Boolean isInsertable() {
+		public Boolean isUpdatable() {
+		public boolean isBytecodeLazy() {
+		public XmlElementMetadata getSourceType() {
+		public String getXmlNodeName() {
+		public AttributePath getAttributePath() {
+		public AttributeRole getAttributeRole() {
+		public boolean isIncludedInOptimisticLocking() {
+		public ToolingHintContext getToolingHintContext() {
+		public String getName() {
+		public String getAccess() {
+		public String getClazz() {
+		public List<JaxbHbmTuplizerType> getTuplizer() {
+		public String getParent() {
+		public List<JaxbHbmToolingHintType> getToolingHints() {
+		public AttributeRole getAttributeRoleBase() {
+		public AttributePath getAttributePathBase() {
+		public ToolingHintContext getToolingHintContextBaselineForEmbeddable() {
+		public void registerIndexConstraintColumn(
+		public void registerUniqueKeyConstraintColumn(
+		public List getAttributes() {
+		public String getXmlNodeName() {
+		public boolean isUnique() {
+		public EmbeddableMapping getEmbeddableMapping() {
+					public AttributeSourceContainer getAttributeSourceContainer() {
+					public void addAttributeSource(AttributeSource attributeSource) {
+					public void registerIndexColumn(
+					public void registerUniqueKeyColumn(
+			public String getName() {
+	public List<SingularAttributeSource> getAttributeSourcesMakingUpIdentifier() {
+	public EmbeddableSource getIdClassSource() {
+	public IdentifierGeneratorDefinition getIndividualAttributeIdGenerator(String identifierAttributeName) {
+	public IdentifierGeneratorDefinition getIdentifierGeneratorDescriptor() {
+	public EntityIdentifierNature getNature() {
+	public JavaTypeDescriptor getTypeDescriptor() {
+	public String getParentReferenceAttributeName() {
+	public Map<EntityMode, String> getTuplizerClassMap() {
+	public boolean isDynamic() {
+	public boolean isUnique() {
+	public AttributePath getAttributePathBase() {
+	public AttributeRole getAttributeRoleBase() {
+	public List<AttributeSource> attributeSources() {
+	public LocalMetadataBuildingContext getLocalMetadataBuildingContext() {
+	public EmbeddableSource getEmbeddableSource() {
+	public ToolingHintContext getToolingHintContext() {
+	public IdentifierSourceSimpleImpl(RootEntitySourceImpl rootEntitySource) {
+	public SingularAttributeSource getIdentifierAttributeSource() {
+	public IdentifierGeneratorDefinition getIdentifierGeneratorDescriptor() {
+	public EntityIdentifierNature getNature() {
+	public String getUnsavedValue() {
+	public ToolingHintContext getToolingHintContext() {
+public class InLineViewSourceImpl
+	public InLineViewSourceImpl(
+	public String getExplicitSchemaName() {
+	public String getExplicitCatalogName() {
+	public String getSelectStatement() {
+	public String getLogicalName() {
+	public IndexConstraintSourceImpl(String name, String tableName) {
+	public String toString() {
+	public boolean isUnique() {
+public interface IndexedPluralAttributeSource extends PluralAttributeSource {
+public class JoinedSubclassEntitySourceImpl extends SubclassEntitySourceImpl implements JoinedSubclassEntitySource {
+	public JoinedSubclassEntitySourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public Boolean isNullable() {
+	public boolean isCascadeDeleteEnabled() {
+	public String getExplicitForeignKeyName() {
+	public boolean createForeignKeyConstraint() {
+	public List<ColumnSource> getPrimaryKeyColumnSources() {
+public class ManyToOneAttributeColumnsAndFormulasSource extends RelationalValueSourceHelper.AbstractColumnsAndFormulasSource {
+	public ManyToOneAttributeColumnsAndFormulasSource(JaxbHbmManyToOneType manyToOneMapping) {
+	public XmlElementMetadata getSourceType() {
+	public String getSourceName() {
+	public String getFormulaAttribute() {
+	public String getColumnAttribute() {
+	public List getColumnOrFormulaElements() {
+	public Boolean isNullable() {
+	public String getIndex() {
+	public boolean isUnique() {
+	public String getUniqueKey() {
+public class ManyToOnePropertySource implements PropertySource {
+	public ManyToOnePropertySource(JaxbHbmManyToOneType manyToOneMapping) {
+	public XmlElementMetadata getSourceType() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public String getPropertyAccessorName() {
+	public String getCascadeStyleName() {
+	public GenerationTiming getGenerationTiming() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isUsedInOptimisticLocking() {
+	public boolean isLazy() {
+	public List<JaxbHbmToolingHintType> getToolingHints() {
+public class MappingDocument implements HbmLocalMetadataBuildingContext, MetadataSourceProcessor {
+	public MappingDocument(
+	public JaxbHbmHibernateMapping getDocumentRoot() {
+	public ToolingHintContext getToolingHintContext() {
+	public String determineEntityName(EntityInfo entityElement) {
+	public String determineEntityName(String entityName, String clazz) {
+	public String qualifyClassName(String name) {
+	public PersistentClass findEntityBinding(String entityName, String clazz) {
+	public Origin getOrigin() {
+	public MetadataBuildingOptions getBuildingOptions() {
+	public MappingDefaults getMappingDefaults() {
+	public InFlightMetadataCollector getMetadataCollector() {
+	public ClassLoaderAccess getClassLoaderAccess() {
+	public ObjectNameNormalizer getObjectNameNormalizer() {
+	public void prepare() {
+	public void processTypeDefinitions() {
+	public void processQueryRenames() {
+	public void processFilterDefinitions() {
+	public void processFetchProfiles() {
+	public void processAuxiliaryDatabaseObjectDefinitions() {
+	public void processNamedQueries() {
+	public void processIdentifierGenerators() {
+	public void prepareForEntityHierarchyProcessing() {
+	public void processEntityHierarchies(Set<String> processedEntityNames) {
+	public void postProcessEntityHierarchies() {
+	public void processResultSetMappings() {
+	public void finishUp() {
+public class ModelBinder {
+	public static ModelBinder prepare(MetadataBuildingContext context) {
+	public ModelBinder(final MetadataBuildingContext context) {
+	public void finishUp(MetadataBuildingContext context) {
+	public void bindEntityHierarchy(EntityHierarchySourceImpl hierarchySource) {
+					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+					public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
+									public EntityNaming getEntityNaming() {
+									public AttributePath getIdentifierAttributePath() {
+									public MetadataBuildingContext getBuildingContext() {
+					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+					public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
+					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+				public String getPropertyAccessorName() {
+					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+	public void bindOneToOne(
+					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+		public TypeResolution(String typeName, Properties parameters) {
+					public EntityNaming getEntityNaming() {
+					public MetadataBuildingContext getBuildingContext() {
+	public static final class DelayedPropertyReferenceHandlerImpl implements InFlightMetadataCollector.DelayedPropertyReferenceHandler {
+		public final String referencedEntityName;
+		public final String referencedPropertyName;
+		public final boolean isUnique;
+		public final Origin propertyRefOrigin;
+		public DelayedPropertyReferenceHandlerImpl(
+		public void process(InFlightMetadataCollector metadataCollector) {
+		public MappingDocument getMappingDocument() {
+		public PluralAttributeSource getPluralAttributeSource() {
+		public Collection getCollectionBinding() {
+		public void doSecondPass(Map persistentClasses) throws org.hibernate.MappingException {
+							public Identifier getOwningPhysicalTableName() {
+							public EntityNaming getOwningEntityNaming() {
+							public AttributePath getOwningAttributePath() {
+							public MetadataBuildingContext getBuildingContext() {
+						public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
+//										public Nature getNature() {
+//										public EntityNaming getEntityNaming() {
+//										public AttributePath getAttributePath() {
+//										public Identifier getReferencedTableName() {
+//										public Identifier getReferencedColumnName() {
+//										public MetadataBuildingContext getBuildingContext() {
+							public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+							public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+							public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
+//											public Nature getNature() {
+//											public EntityNaming getEntityNaming() {
+//											public AttributePath getAttributePath() {
+//											public Identifier getReferencedTableName() {
+//											public Identifier getReferencedColumnName() {
+//											public MetadataBuildingContext getBuildingContext() {
+		public PluralAttributeListSecondPass(
+		public IndexedPluralAttributeSource getPluralAttributeSource() {
+		public org.hibernate.mapping.List getCollectionBinding() {
+		public PluralAttributeSetSecondPass(
+		public PluralAttributeMapSecondPass(
+		public IndexedPluralAttributeSource getPluralAttributeSource() {
+		public org.hibernate.mapping.Map getCollectionBinding() {
+		public PluralAttributeBagSecondPass(
+		public PluralAttributeIdBagSecondPass(
+		public PluralAttributeArraySecondPass(
+		public IndexedPluralAttributeSource getPluralAttributeSource() {
+		public Array getCollectionBinding() {
+		public PluralAttributePrimitiveArraySecondPass(
+		public IndexedPluralAttributeSource getPluralAttributeSource() {
+		public PrimitiveArray getCollectionBinding() {
+	public void bindListOrArrayIndex(
+					public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
+									public AttributePath getPluralAttributePath() {
+									public MetadataBuildingContext getBuildingContext() {
+						public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+						public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
+										public AttributePath getPluralAttributePath() {
+										public MetadataBuildingContext getBuildingContext() {
+		public ManyToOneColumnBinder(
+		public boolean canProcessImmediately() {
+		public void doSecondPass(Map persistentClasses) throws org.hibernate.MappingException {
+							public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
+							public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
+//											public Nature getNature() {
+//											public EntityNaming getEntityNaming() {
+//											public AttributePath getAttributePath() {
+//											public Identifier getReferencedTableName() {
+//											public Identifier getReferencedColumnName() {
+//											public MetadataBuildingContext getBuildingContext() {
+											public AttributePath getAttributePath() {
+											public boolean isCollectionElement() {
+											public MetadataBuildingContext getBuildingContext() {
+		public ManyToOneFkSecondPass(
+		public String getReferencedEntityName() {
+		public boolean isInPrimaryKey() {
+		public void doSecondPass(Map persistentClasses) throws org.hibernate.MappingException {
+		public boolean canProcessImmediately() {
+		public NaturalIdUniqueKeyBinderImpl(MappingDocument mappingDocument, PersistentClass entityBinding) {
+		public void addAttributeBinding(Property attributeBinding) {
+		public void process() {
+						public Identifier getTableName() {
+						public List<Identifier> getColumnNames() {
+						public MetadataBuildingContext getBuildingContext() {
+public class NamedQueryBinder {
+	public static void processNamedQuery(
+	public static void processNamedQuery(
+	public static void processNamedNativeQuery(
+	public static void processNamedNativeQuery(
+						public void doSecondPass(Map persistentClasses) throws MappingException {
+public class PluralAttributeElementSourceBasicImpl
+	public PluralAttributeElementSourceBasicImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public String getFormulaAttribute() {
+					public List getColumnOrFormulaElements() {
+					public SizeSource getSizeSource() {
+	public PluralAttributeElementNature getNature() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public HibernateTypeSourceImpl getExplicitHibernateTypeSource() {
+	public AttributePath getAttributePath() {
+	public boolean isCollectionElement() {
+	public MetadataBuildingContext getBuildingContext() {
+public class PluralAttributeElementSourceEmbeddedImpl
+	public PluralAttributeElementSourceEmbeddedImpl(
+					public AttributeRole getAttributeRoleBase() {
+					public AttributePath getAttributePathBase() {
+					public ToolingHintContext getToolingHintContextBaselineForEmbeddable() {
+					public void registerIndexConstraintColumn(
+					public void registerUniqueKeyConstraintColumn(
+					public String getClazz() {
+					public List<JaxbHbmTuplizerType> getTuplizer() {
+					public String getParent() {
+	public PluralAttributeElementNature getNature() {
+	public EmbeddableSource getEmbeddableSource() {
+	public ToolingHintContext getToolingHintContext() {
+public class PluralAttributeElementSourceManyToAnyImpl
+	public PluralAttributeElementSourceManyToAnyImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public List getColumnOrFormulaElements() {
+			public HibernateTypeSource getTypeSource() {
+			public RelationalValueSource getRelationalValueSource() {
+			public Map<String, String> getValueMappings() {
+			public AttributePath getAttributePath() {
+			public MetadataBuildingContext getBuildingContext() {
+			public HibernateTypeSource getTypeSource() {
+			public List<RelationalValueSource> getRelationalValueSources() {
+			public AttributePath getAttributePath() {
+			public MetadataBuildingContext getBuildingContext() {
+	public AnyDiscriminatorSource getDiscriminatorSource() {
+	public AnyKeySource getKeySource() {
+	public PluralAttributeElementNature getNature() {
+public class PluralAttributeElementSourceManyToManyImpl
+	public PluralAttributeElementSourceManyToManyImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getFormulaAttribute() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+	public PluralAttributeElementNature getNature() {
+	public String getReferencedEntityName() {
+	public FilterSource[] getFilterSources() {
+	public String getReferencedEntityAttributeName() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean isIgnoreNotFound() {
+	public String getExplicitForeignKeyName() {
+	public boolean isCascadeDeleteEnabled() {
+	public boolean isUnique() {
+	public String getWhere() {
+	public FetchCharacteristics getFetchCharacteristics() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public boolean isOrdered() {
+	public String getOrder() {
+	public boolean createForeignKeyConstraint() {
+public class PluralAttributeElementSourceOneToManyImpl
+	public PluralAttributeElementSourceOneToManyImpl(
+	public PluralAttributeElementNature getNature() {
+	public String getReferencedEntityName() {
+	public boolean isIgnoreNotFound() {
+	public String getXmlNodeName() {
+public class PluralAttributeKeySourceImpl
+	public PluralAttributeKeySourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+	public String getExplicitForeignKeyName() {
+	public boolean createForeignKeyConstraint() {
+	public String getReferencedPropertyName() {
+	public boolean isCascadeDeleteEnabled() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+public class PluralAttributeMapKeyManyToAnySourceImpl
+		public String getName() {
+		public Map<String, String> getParameters() {
+		public JavaTypeDescriptor getJavaType() {
+	public PluralAttributeMapKeyManyToAnySourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public List getColumnOrFormulaElements() {
+			public HibernateTypeSource getTypeSource() {
+			public RelationalValueSource getRelationalValueSource() {
+			public Map<String, String> getValueMappings() {
+			public AttributePath getAttributePath() {
+			public MetadataBuildingContext getBuildingContext() {
+			public HibernateTypeSource getTypeSource() {
+			public List<RelationalValueSource> getRelationalValueSources() {
+			public AttributePath getAttributePath() {
+			public MetadataBuildingContext getBuildingContext() {
+	public AnyDiscriminatorSource getDiscriminatorSource() {
+	public AnyKeySource getKeySource() {
+	public Nature getMapKeyNature() {
+	public boolean isReferencedEntityAttribute() {
+	public PluralAttributeIndexNature getNature() {
+	public HibernateTypeSource getTypeInformation() {
+	public String getXmlNodeName() {
+public class PluralAttributeMapKeyManyToManySourceImpl
+	public PluralAttributeMapKeyManyToManySourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getFormulaAttribute() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+	public PluralAttributeMapKeyManyToManySourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+	public String getReferencedEntityName() {
+	public String getExplicitForeignKeyName() {
+	public PluralAttributeIndexNature getNature() {
+	public HibernateTypeSource getTypeInformation() {
+	public String getXmlNodeName() {
+	public Nature getMapKeyNature() {
+	public boolean isReferencedEntityAttribute() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+public class PluralAttributeMapKeySourceBasicImpl
+	public PluralAttributeMapKeySourceBasicImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getFormulaAttribute() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public SizeSource getSizeSource() {
+	public PluralAttributeMapKeySourceBasicImpl(MappingDocument sourceMappingDocument, final JaxbHbmIndexType jaxbIndex) {
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public SizeSource getSizeSource() {
+					public List getColumnOrFormulaElements() {
+	public PluralAttributeIndexNature getNature() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getXmlNodeName() {
+public class PluralAttributeMapKeySourceEmbeddedImpl
+	public PluralAttributeMapKeySourceEmbeddedImpl(
+					public String getClazz() {
+					public List<JaxbHbmTuplizerType> getTuplizer() {
+					public String getParent() {
+	public PluralAttributeMapKeySourceEmbeddedImpl(
+					public String getClazz() {
+					public List<JaxbHbmTuplizerType> getTuplizer() {
+					public String getParent() {
+					public AttributeRole getAttributeRoleBase() {
+					public AttributePath getAttributePathBase() {
+					public ToolingHintContext getToolingHintContextBaselineForEmbeddable() {
+					public void registerIndexConstraintColumn(
+					public void registerUniqueKeyConstraintColumn(
+	public PluralAttributeIndexNature getNature() {
+	public EmbeddableSource getEmbeddableSource() {
+	public HibernateTypeSource getTypeInformation() {
+	public String getXmlNodeName() {
+public class PluralAttributeSequentialIndexSourceImpl
+	public PluralAttributeSequentialIndexSourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+	public PluralAttributeSequentialIndexSourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public SizeSource getSizeSource() {
+					public List getColumnOrFormulaElements() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public int getBase() {
+	public PluralAttributeIndexNature getNature() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getXmlNodeName() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+public class PluralAttributeSourceArrayImpl
+	public PluralAttributeSourceArrayImpl(
+	public PluralAttributeIndexSource getIndexSource() {
+	public PluralAttributeNature getNature() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+	public String getElementClass() {
+public class PluralAttributeSourceBagImpl extends AbstractPluralAttributeSourceImpl implements Orderable {
+	public PluralAttributeSourceBagImpl(
+	public PluralAttributeNature getNature() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+	public boolean isOrdered() {
+	public String getOrder() {
+public class PluralAttributeSourceIdBagImpl extends AbstractPluralAttributeSourceImpl implements Orderable {
+	public PluralAttributeSourceIdBagImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public SizeSource getSizeSource() {
+					public List getColumnOrFormulaElements() {
+	public PluralAttributeNature getNature() {
+	public CollectionIdSource getCollectionIdSource() {
+	public boolean isOrdered() {
+	public String getOrder() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+		public CollectionIdSourceImpl(
+		public ColumnSource getColumnSource() {
+		public HibernateTypeSourceImpl getTypeInformation() {
+		public String getGeneratorName() {
+public class PluralAttributeSourceListImpl extends AbstractPluralAttributeSourceImpl implements IndexedPluralAttributeSource {
+	public PluralAttributeSourceListImpl(
+	public PluralAttributeIndexSource getIndexSource() {
+	public PluralAttributeNature getNature() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+public class PluralAttributeSourceMapImpl extends AbstractPluralAttributeSourceImpl implements IndexedPluralAttributeSource {
+	public PluralAttributeSourceMapImpl(
+	public PluralAttributeIndexSource getIndexSource() {
+	public PluralAttributeNature getNature() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+public class PluralAttributeSourcePrimitiveArrayImpl
+	public PluralAttributeSourcePrimitiveArrayImpl(
+	public PluralAttributeIndexSource getIndexSource() {
+	public PluralAttributeNature getNature() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+	public String getElementClass() {
+public class PluralAttributeSourceSetImpl extends AbstractPluralAttributeSourceImpl implements Orderable, Sortable {
+	public PluralAttributeSourceSetImpl(
+	public PluralAttributeNature getNature() {
+	public boolean isSorted() {
+	public String getComparatorName() {
+	public boolean isOrdered() {
+	public String getOrder() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+public class RelationalObjectBinder {
+	public static interface ColumnNamingDelegate {
+		public Identifier determineImplicitName(LocalMetadataBuildingContext context);
+	public RelationalObjectBinder(MetadataBuildingContext buildingContext) {
+	public void bindColumnOrFormula(
+	public void bindColumns(
+	public void bindColumnsAndFormulas(
+	public void bindColumn(
+	public void bindFormulas(
+	public static interface RelationalObjectResolutionContext {
+	public static void bindConstraints(
+public class RelationalValueSourceHelper {
+	public static interface ColumnsAndFormulasSource  {
+	public abstract static class AbstractColumnsAndFormulasSource implements ColumnsAndFormulasSource {
+		public String getFormulaAttribute() {
+		public String getColumnAttribute() {
+		public List getColumnOrFormulaElements() {
+		public SizeSource getSizeSource() {
+		public Boolean isNullable() {
+		public String getIndex() {
+		public boolean isUnique() {
+		public String getUniqueKey() {
+	public static RelationalValueSource buildValueSource(
+	public static ColumnSource buildColumnSource(
+	public static List<ColumnSource> buildColumnSources(
+	public static List<RelationalValueSource> buildValueSources(
+public abstract class ResultSetMappingBinder {
+	public static ResultSetMappingDefinition bind(
+	public static ResultSetMappingDefinition bind(
+	public static NativeSQLQueryScalarReturn extractReturnDescription(
+	public static NativeSQLQueryRootReturn extractReturnDescription(
+	public static NativeSQLQueryJoinReturn extractReturnDescription(
+	public static NativeSQLQueryReturn extractReturnDescription(
+public class RootEntitySourceImpl extends AbstractEntitySourceImpl {
+	public TableSpecificationSource getPrimaryTable() {
+	public String getDiscriminatorMatchValue() {
+	public IdentifiableTypeSource getSuperType() {
+	public SecondaryTableSourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public Boolean isNullable() {
+	public TableSpecificationSource getTableSource() {
+	public List<ColumnSource> getPrimaryKeyColumnSources() {
+	public String getLogicalTableNameForContainedColumns() {
+	public String getComment() {
+	public FetchStyle getFetchStyle() {
+	public boolean isInverse() {
+	public boolean isOptional() {
+	public boolean isCascadeDeleteEnabled() {
+	public String getExplicitForeignKeyName() {
+	public boolean createForeignKeyConstraint() {
+	public CustomSql getCustomSqlInsert() {
+	public CustomSql getCustomSqlUpdate() {
+	public CustomSql getCustomSqlDelete() {
+public class SingularAttributeSourceAnyImpl
+	public SingularAttributeSourceAnyImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public List getColumnOrFormulaElements() {
+			public HibernateTypeSource getTypeSource() {
+			public RelationalValueSource getRelationalValueSource() {
+			public Map<String, String> getValueMappings() {
+			public AttributePath getAttributePath() {
+			public MetadataBuildingContext getBuildingContext() {
+			public HibernateTypeSource getTypeSource() {
+			public List<RelationalValueSource> getRelationalValueSources() {
+			public AttributePath getAttributePath() {
+			public MetadataBuildingContext getBuildingContext() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public XmlElementMetadata getSourceType() {
+	public boolean isSingular() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public AttributePath getAttributePath() {
+	public AttributeRole getAttributeRole() {
+	public boolean isVirtualAttribute() {
+	public GenerationTiming getGenerationTiming() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public NaturalIdMutability getNaturalIdMutability() {
+	public HibernateTypeSource getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public boolean isIncludedInOptimisticLocking() {
+	public ToolingHintContext getToolingHintContext() {
+	public AnyDiscriminatorSource getDiscriminatorSource() {
+	public AnyKeySource getKeySource() {
+	public String getCascadeStyleName() {
+	public boolean isSingular() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public XmlElementMetadata getSourceType() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public AttributePath getAttributePath() {
+	public boolean isCollectionElement() {
+	public AttributeRole getAttributeRole() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public GenerationTiming getGenerationTiming() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public NaturalIdMutability getNaturalIdMutability() {
+	public boolean isIncludedInOptimisticLocking() {
+	public boolean isVirtualAttribute() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public ToolingHintContext getToolingHintContext() {
+	public MetadataBuildingContext getBuildingContext() {
+	public SingularAttributeSourceEmbeddedImpl(
+						public String getClazz() {
+						public List<JaxbHbmTuplizerType> getTuplizer() {
+						public String getParent() {
+					public List<JaxbHbmToolingHintType> getToolingHints() {
+					public String getName() {
+					public String getAccess() {
+					public boolean isUnique() {
+					public EmbeddableMapping getEmbeddableMapping() {
+	public SingularAttributeSourceEmbeddedImpl(
+						public String getClazz() {
+						public List<JaxbHbmTuplizerType> getTuplizer() {
+						public String getParent() {
+					public List<JaxbHbmToolingHintType> getToolingHints() {
+					public String getName() {
+					public String getAccess() {
+					public boolean isUnique() {
+					public EmbeddableMapping getEmbeddableMapping() {
+	public SingularAttributeSourceEmbeddedImpl(
+						public String getClazz() {
+						public List<JaxbHbmTuplizerType> getTuplizer() {
+						public String getParent() {
+					public boolean isUnique() {
+					public String getName() {
+					public String getAccess() {
+					public EmbeddableMapping getEmbeddableMapping() {
+					public List<JaxbHbmToolingHintType> getToolingHints() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public AttributePath getAttributePath() {
+	public AttributeRole getAttributeRole() {
+	public boolean isIncludedInOptimisticLocking() {
+	public ToolingHintContext getToolingHintContext() {
+			public String getName() {
+	public XmlElementMetadata getSourceType() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public AttributePath getAttributePath() {
+	public AttributeRole getAttributeRole() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public FetchCharacteristicsSingularAssociationImpl getFetchCharacteristics() {
+	public boolean isIgnoreNotFound() {
+	public boolean isIncludedInOptimisticLocking() {
+	public String getCascadeStyleName() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public String getReferencedEntityAttributeName() {
+	public String getReferencedEntityName() {
+	public boolean isEmbedXml() {
+	public boolean isUnique() {
+	public String getExplicitForeignKeyName() {
+	public boolean isCascadeDeleteEnabled() {
+	public ForeignKeyDirection getForeignKeyDirection() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public ToolingHintContext getToolingHintContext() {
+			public String getName() {
+	public XmlElementMetadata getSourceType() {
+	public String getName() {
+	public String getXmlNodeName() {
+	public AttributePath getAttributePath() {
+	public AttributeRole getAttributeRole() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public boolean isIncludedInOptimisticLocking() {
+	public String getCascadeStyleName() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public FetchCharacteristicsSingularAssociationImpl getFetchCharacteristics() {
+	public boolean isVirtualAttribute() {
+	public String getReferencedEntityName() {
+	public boolean isUnique() {
+	public String getExplicitForeignKeyName() {
+	public boolean isCascadeDeleteEnabled() {
+	public ForeignKeyDirection getForeignKeyDirection() {
+	public List<DerivedValueSource> getFormulaSources() {
+	public ToolingHintContext getToolingHintContext() {
+	public boolean isConstrained() {
+	public String getReferencedEntityAttributeName() {
+	public boolean isEmbedXml() {
+	public SingularIdentifierAttributeSourceImpl(
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public SizeSource getSizeSource() {
+					public Boolean isNullable() {
+	public String getName() {
+	public AttributePath getAttributePath() {
+	public AttributeRole getAttributeRole() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public GenerationTiming getGenerationTiming() {
+	public boolean isBytecodeLazy() {
+	public NaturalIdMutability getNaturalIdMutability() {
+	public boolean isIncludedInOptimisticLocking() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public boolean isVirtualAttribute() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+	public ToolingHintContext getToolingHintContext() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean isSingular() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
-public class MyNamingStrategyDelegator extends ImprovedNamingStrategyDelegator {
-	public MyNamingStrategyDelegator() {
+public class SizeSourceImpl implements SizeSource {
+	public SizeSourceImpl(Integer length, Integer scale, Integer precision) {
+	public Integer getLength() {
+	public Integer getPrecision() {
-		public String toPhysicalColumnName(String columnName) {
+	public Integer getScale() {
+public class SubclassEntitySourceImpl extends AbstractEntitySourceImpl implements SubclassEntitySource {
+	public TableSpecificationSource getPrimaryTable() {
+	public String getDiscriminatorMatchValue() {
+	public IdentifiableTypeSource getSuperType() {
+public class TableSourceImpl extends AbstractHbmSourceNode implements TableSource {
+	public String getExplicitCatalogName() {
+	public String getExplicitSchemaName() {
+	public String getExplicitTableName() {
+	public String getRowId() {
+	public String getComment() {
+	public String getCheckConstraint() {
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public Boolean isNullable() {
+	public String getName() {
+	public XmlElementMetadata getSourceType() {
+	public String getXmlNodeName() {
+	public AttributePath getAttributePath() {
+	public boolean isCollectionElement() {
+	public AttributeRole getAttributeRole() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public String getPropertyAccessorName() {
+	public GenerationTiming getGenerationTiming() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public NaturalIdMutability getNaturalIdMutability() {
+	public boolean isIncludedInOptimisticLocking() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public boolean isVirtualAttribute() {
+	public boolean isSingular() {
+	public String getUnsavedValue() {
+	public ToolingHintContext getToolingHintContext() {
+	public MetadataBuildingContext getBuildingContext() {
+public class TypeDefinitionBinder {
+	public static void processTypeDefinition(
+	public String toString() {
+					public XmlElementMetadata getSourceType() {
+					public String getSourceName() {
+					public String getColumnAttribute() {
+					public List getColumnOrFormulaElements() {
+					public Boolean isNullable() {
+	public XmlElementMetadata getSourceType() {
+	public String getName() {
+	public AttributePath getAttributePath() {
+	public boolean isCollectionElement() {
+	public AttributeRole getAttributeRole() {
+	public HibernateTypeSourceImpl getTypeInformation() {
+	public String getPropertyAccessorName() {
+	public List<RelationalValueSource> getRelationalValueSources() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public String getUnsavedValue() {
+	public GenerationTiming getGenerationTiming() {
+	public Boolean isInsertable() {
+	public Boolean isUpdatable() {
+	public boolean isBytecodeLazy() {
+	public NaturalIdMutability getNaturalIdMutability() {
+	public boolean isIncludedInOptimisticLocking() {
+	public SingularAttributeNature getSingularAttributeNature() {
+	public boolean isVirtualAttribute() {
+	public boolean isSingular() {
+	public String getXmlNodeName() {
+	public MetadataBuildingContext getBuildingContext() {
+	public ToolingHintContext getToolingHintContext() {
+public enum XmlElementMetadata {
+	public String getElementName() {
+	public boolean isInherentlySingleColumn() {
+	public boolean canBeNamed() {
+public abstract class AbstractAttributeKey {
+	public int getDepth() {
+	public abstract AbstractAttributeKey append(String property);
+	public AbstractAttributeKey getParent() {
+	public String getProperty() {
+	public String getFullPath() {
+	public boolean isRoot() {
+	public String toString() {
+	public boolean equals(Object o) {
+	public int hashCode() {
+public interface AnyDiscriminatorSource extends ImplicitAnyDiscriminatorColumnNameSource {
+public interface AnyKeySource extends ImplicitAnyKeyColumnNameSource {
+public interface AnyMappingSource {
+public interface AssociationSource {
+	public AttributeSource getAttributeSource();
+	public String getReferencedEntityName();
+	public boolean isIgnoreNotFound();
+	public boolean isMappedBy();
+public class AttributePath extends AbstractAttributeKey {
+	public static final char DELIMITER = '.';
+	public AttributePath() {
+	public AttributePath append(String property) {
+	public AttributePath getParent() {
+	public AttributePath(AttributePath parent, String property) {
+	public static AttributePath parse(String path) {
+public class AttributeRole extends AbstractAttributeKey {
+	public static final char DELIMITER = '.';
+	public AttributeRole(String base) {
+	public AttributeRole append(String property) {
+	public AttributeRole getParent() {
+public interface AttributeSource extends ToolingHintContextContainer {
+	public XmlElementMetadata getSourceType();
+	public String getName();
+	public boolean isSingular();
+	public String getXmlNodeName();
+	public AttributePath getAttributePath();
+	public AttributeRole getAttributeRole();
+	public HibernateTypeSource getTypeInformation();
+	public String getPropertyAccessorName();
+	public boolean isIncludedInOptimisticLocking();
+public interface AttributeSourceContainer extends ToolingHintContextContainer {
+	public AttributePath getAttributePathBase();
+	public AttributeRole getAttributeRoleBase();
+	public List<AttributeSource> attributeSources();
+	public LocalMetadataBuildingContext getLocalMetadataBuildingContext();
+public interface CascadeStyleSource {
+	public String getCascadeStyleName();
+public interface CollectionIdSource {
+	public ColumnSource getColumnSource();
+	public HibernateTypeSource getTypeInformation();
+	public String getGeneratorName();
+public interface ColumnBindingDefaults {
+public interface ColumnSource extends RelationalValueSource {
+	public String getName();
+	public String getReadFragment();
+	public String getWriteFragment();
+	public TruthValue isNullable();
+	public String getDefaultValue();
+	public String getSqlType();
+	public JdbcDataType getDatatype();
+	public SizeSource getSizeSource();
+	public boolean isUnique();
+	public String getCheckCondition();
+	public String getComment();
+public interface ColumnsAndFormulasSourceContainer {
+public interface CompositeIdentifierSource extends IdentifierSource, EmbeddableSourceContributor {
+	public IdentifierGeneratorDefinition getIndividualAttributeIdGenerator(String identifierAttributeName);
+public interface ConstraintSource {
+	public String name();
+	public String getTableName();
+	public List<String> columnNames();
+public interface DerivedValueSource extends RelationalValueSource {
+	public String getExpression();
+public interface DiscriminatorSource extends ImplicitDiscriminatorColumnNameSource {
+	public RelationalValueSource getDiscriminatorRelationalValueSource();
+	public String getExplicitHibernateTypeName();
+public interface EmbeddableMapping {
+	public String getClazz();
+	public List<JaxbHbmTuplizerType> getTuplizer();
+	public String getParent();
+public interface EmbeddableSource extends AttributeSourceContainer {
+	public JavaTypeDescriptor getTypeDescriptor();
+	public String getParentReferenceAttributeName();
+	public Map<EntityMode,String> getTuplizerClassMap();
+	public boolean isDynamic();
+	public boolean isUnique();
+public interface EmbeddableSourceContributor {
+	public EmbeddableSource getEmbeddableSource();
+public interface EmbeddedAttributeMapping extends SingularAttributeInfo {
+public interface EntityHierarchySource {
+	public EntitySource getRoot();
+	public InheritanceType getHierarchyInheritanceType();
+	public IdentifierSource getIdentifierSource();
+	public VersionAttributeSource getVersionAttributeSource();
+	public DiscriminatorSource getDiscriminatorSource();
+	public MultiTenancySource getMultiTenancySource();
+	public EntityMode getEntityMode();
+	public boolean isMutable();
+	public boolean isExplicitPolymorphism();
+	public String getWhere();
+	public String getRowId();
+	public OptimisticLockStyle getOptimisticLockStyle();
+	public Caching getCaching();
+	public Caching getNaturalIdCaching();
+public interface EntityNamingSource extends EntityNaming {
+	public String getTypeName();
+public interface EntityNamingSourceContributor {
+	public EntityNamingSource getEntityNamingSource();
+public interface EntitySource extends IdentifiableTypeSource, ToolingHintContextContainer, EntityNamingSourceContributor {
+	public TableSpecificationSource getPrimaryTable();
+	public Map<String,SecondaryTableSource> getSecondaryTableMap();
+	public String getXmlNodeName();
+	public Map<EntityMode,String> getTuplizerClassMap();
+	public String getCustomPersisterClassName();
+	public boolean isLazy();
+	public String getProxy();
+	public int getBatchSize();
+	public Boolean isAbstract();
+	public boolean isDynamicInsert();
+	public boolean isDynamicUpdate();
+	public boolean isSelectBeforeUpdate();
+	public String getCustomLoaderName();
+	public CustomSql getCustomSqlInsert();
+	public CustomSql getCustomSqlUpdate();
+	public CustomSql getCustomSqlDelete();
+	public String[] getSynchronizedTableNames();
+	public String getDiscriminatorMatchValue();
+	public Collection<ConstraintSource> getConstraints();
+	public FilterSource[] getFilterSources();
+	public List<JaxbHbmNamedQueryType> getNamedQueries();
+	public List<JaxbHbmNamedNativeQueryType> getNamedNativeQueries();
+	public TruthValue quoteIdentifiersLocalToEntity();
+public interface FetchCharacteristics {
+	public FetchTiming getFetchTiming();
+	public FetchStyle getFetchStyle();
+public interface FetchCharacteristicsPluralAttribute extends FetchCharacteristics {
+	public Integer getBatchSize();
+	public boolean isExtraLazy();
+public interface FetchCharacteristicsSingularAssociation extends FetchCharacteristics {
+	public boolean isUnwrapProxies();
+public interface FetchableAttributeSource {
+	public FetchCharacteristics getFetchCharacteristics();
+public interface FilterSource {
+	public String getName();
+	public String getCondition();
+	public boolean shouldAutoInjectAliases();
+	public Map<String, String> getAliasToTableMap();
+	public Map<String, String> getAliasToEntityMap();
+public interface ForeignKeyContributingSource {
+	public String getExplicitForeignKeyName();
+	public boolean createForeignKeyConstraint();
+	public boolean isCascadeDeleteEnabled();
+public interface HibernateTypeSource {
+public interface IdentifiableTypeSource extends AttributeSourceContainer {
+	public Origin getOrigin();
+	public EntityHierarchySource getHierarchy();
+	public LocalMetadataBuildingContext getLocalMetadataBuildingContext();
+	public String getTypeName();
+	public IdentifiableTypeSource getSuperType();
+	public Collection<IdentifiableTypeSource> getSubTypes();
+	public List<JpaCallbackSource> getJpaCallbackClasses();
+public interface IdentifierSource extends ToolingHintContextContainer {
+	public EntityIdentifierNature getNature();
+public interface IdentifierSourceAggregatedComposite extends CompositeIdentifierSource {
+	public SingularAttributeSourceEmbedded getIdentifierAttributeSource();
+	public List<MapsIdSource> getMapsIdSources();
+public interface IdentifierSourceNonAggregatedComposite extends CompositeIdentifierSource {
+	public List<SingularAttributeSource> getAttributeSourcesMakingUpIdentifier();
+	public EmbeddableSource getIdClassSource();
+public interface IdentifierSourceSimple extends IdentifierSource {
+	public SingularAttributeSource getIdentifierAttributeSource();
+	public String getUnsavedValue();
+public interface InLineViewSource extends TableSpecificationSource {
+	public String getSelectStatement();
+	public String getLogicalName();
+public interface IndexConstraintSource extends ConstraintSource {
+	public boolean isUnique();
+public enum InheritanceType {
+public interface JavaTypeDescriptorResolvable {
+	public void resolveJavaTypeDescriptor(JavaTypeDescriptor descriptor);
+public class JdbcDataType {
+	public JdbcDataType(int typeCode, String typeName, Class javaType) {
+	public int getTypeCode() {
+	public String getTypeName() {
+	public Class getJavaType() {
+	public int hashCode() {
+	public boolean equals(Object o) {
+	public String toString() {
+public interface JoinedSubclassEntitySource extends SubclassEntitySource, ForeignKeyContributingSource {
+	public List<ColumnSource> getPrimaryKeyColumnSources();
+public interface JpaCallbackSource {
+public interface LocalMetadataBuildingContext extends MetadataBuildingContext {
+	public Origin getOrigin();
+public interface MapsIdSource {
+	public String getMappedIdAttributeName();
+	public SingularAttributeSourceToOne getAssociationAttributeSource();
+public interface MetadataSourceProcessor {
+public interface MultiTenancySource {
+	public RelationalValueSource getRelationalValueSource();
+	public boolean isShared();
+	public boolean bindAsParameter();
+public enum NaturalIdMutability {
+public interface Orderable {
+public enum PluralAttributeElementNature {
+	public boolean isAssociation() {
+	public boolean isCascadeable() {
+public interface PluralAttributeElementSource {
+	public PluralAttributeElementNature getNature();
+public interface PluralAttributeElementSourceAssociation extends PluralAttributeElementSource, AssociationSource {
+public interface PluralAttributeElementSourceBasic
+	public HibernateTypeSource getExplicitHibernateTypeSource();
+public interface PluralAttributeElementSourceEmbedded
+public interface PluralAttributeElementSourceManyToAny
+public interface PluralAttributeElementSourceManyToMany
+	public String getReferencedEntityName();
+	public String getReferencedEntityAttributeName();
+	public boolean isIgnoreNotFound();
+	public String getExplicitForeignKeyName();
+	public boolean isUnique();
+	public FilterSource[] getFilterSources();
+	public String getWhere();
+	public FetchCharacteristics getFetchCharacteristics();
+public interface PluralAttributeElementSourceOneToMany extends PluralAttributeElementSourceAssociation {
+	public String getReferencedEntityName();
+	public boolean isIgnoreNotFound();
+	public String getXmlNodeName();
+public enum PluralAttributeIndexNature {
+public interface PluralAttributeIndexSource {
+public interface PluralAttributeKeySource
+	public String getReferencedPropertyName();
+	public boolean isCascadeDeleteEnabled();
+public interface PluralAttributeMapKeyManyToAnySource
+public interface PluralAttributeMapKeyManyToManySource
+public interface PluralAttributeMapKeySource extends PluralAttributeIndexSource {
+	public static enum Nature {
+	public Nature getMapKeyNature();
+	public boolean isReferencedEntityAttribute();
+public interface PluralAttributeMapKeySourceBasic extends PluralAttributeIndexSource, RelationalValueSourceContainer {
+public interface PluralAttributeMapKeySourceEmbedded
+public enum PluralAttributeNature {
+	public Class<?> reportedJavaType() {
+	public boolean isIndexed() {
+public interface PluralAttributeSequentialIndexSource extends PluralAttributeIndexSource, RelationalValueSourceContainer {
+public interface PluralAttributeSource
+	public PluralAttributeNature getNature();
+	public CollectionIdSource getCollectionIdSource();
+	public PluralAttributeKeySource getKeySource();
+	public PluralAttributeElementSource getElementSource();
+	public FilterSource[] getFilterSources();
+	public TableSpecificationSource getCollectionTableSpecificationSource();
+	public String getCollectionTableComment();
+	public String getCollectionTableCheck();
+	public String[] getSynchronizedTableNames();
+	public Caching getCaching();
+	public String getCustomPersisterClassName();
+	public String getWhere();
+	public boolean isInverse();
+	public boolean isMutable();
+	public String getCustomLoaderName();
+	public CustomSql getCustomSqlInsert();
+	public CustomSql getCustomSqlUpdate();
+	public CustomSql getCustomSqlDelete();
+	public CustomSql getCustomSqlDeleteAll();
+	public String getMappedBy();
+	public boolean usesJoinTable();
+public interface PluralAttributeSourceArray extends IndexedPluralAttributeSource {
+	public String getElementClass();
+public interface RelationalValueSource {
+	public String getContainingTableName();
+	public Nature getNature();
+	public static enum Nature {
+		public Class<? extends RelationalValueSource> getSpecificContractClass() {
+public interface RelationalValueSourceContainer extends ColumnBindingDefaults {
+	public List<RelationalValueSource> getRelationalValueSources();
+public interface SecondaryTableSource extends ForeignKeyContributingSource {
+	public TableSpecificationSource getTableSource();
+	public List<ColumnSource> getPrimaryKeyColumnSources();
+	public String getLogicalTableNameForContainedColumns();
+	public String getComment();
+	public FetchStyle getFetchStyle();
+	public boolean isInverse();
+	public boolean isOptional();
+	public boolean isCascadeDeleteEnabled();
+	public CustomSql getCustomSqlInsert();
+	public CustomSql getCustomSqlUpdate();
+	public CustomSql getCustomSqlDelete();
+public enum SingularAttributeNature {
+public interface SingularAttributeSource extends AttributeSource {
+	public boolean isVirtualAttribute();
+	public SingularAttributeNature getSingularAttributeNature();
+	public GenerationTiming getGenerationTiming();
+	public boolean isBytecodeLazy();
+	public NaturalIdMutability getNaturalIdMutability();
-public enum SourceType {
+public interface SingularAttributeSourceAny extends SingularAttributeSource, AnyMappingSource, CascadeStyleSource {
-public interface SingularAttributeSource extends MetaAttributeContainer {
-	public String getName();
-	public String getTypeAttribute();
-    public JaxbTypeElement getType();
-	public String getAccess();
+public interface SingularAttributeSourceBasic
+public interface SingularAttributeSourceEmbedded extends SingularAttributeSource, EmbeddableSourceContributor {
-public interface CustomSqlElement {
-	public String getValue();
-	public boolean isCallable();
-	public JaxbCheckAttribute getCheck();
+public interface SingularAttributeSourceManyToOne
+public interface SingularAttributeSourceOneToOne extends SingularAttributeSourceToOne {
+	public List<DerivedValueSource> getFormulaSources();
+public interface SingularAttributeSourceToOne
+	public String getReferencedEntityAttributeName();
+	public String getReferencedEntityName();
+	public ForeignKeyDirection getForeignKeyDirection();
+	public boolean isUnique();
+	public boolean isEmbedXml();
+public interface SizeSource {
+public interface Sortable {
+public interface SubclassEntitySource extends EntitySource {
+public interface TableSource extends TableSpecificationSource {
+public interface TableSpecificationSource {
+	public String getExplicitSchemaName();
+	public String getExplicitCatalogName();
+public class ToolingHint {
+	public ToolingHint(String name, boolean inheritable) {
+	public String getName() {
+	public boolean isInheritable() {
+	public java.util.List getValues() {
+	public void addValue(String value) {
+	public String getValue() {
+	public boolean isMultiValued() {
+	public String toString() {
+	public MetaAttribute asMetaAttribute() {
+public class ToolingHintContext {
+	public ToolingHintContext(ToolingHintContext baseline) {
+	public Collection<ToolingHint> getToolingHints() {
+	public Iterable<String> getKeys() {
+	public ToolingHint getToolingHint(String key) {
+	public void add(ToolingHint toolingHint) {
+	public Map<String,MetaAttribute> getMetaAttributeMap() {
+public interface ToolingHintContextContainer {
+	public ToolingHintContext getToolingHintContext();
+public interface UniqueKeyConstraintSource extends ConstraintSource {
+public interface VersionAttributeSource
+	public String getUnsavedValue();
+	public StandardServiceRegistryBuilder(BootstrapServiceRegistry bootstrapServiceRegistry, LoadedConfig loadedConfigBaseline) {
+	public LoadedConfig getAggregatedCfgXml() {
+	public StandardServiceRegistryBuilder loadProperties(File file) {
+	public StandardServiceRegistryBuilder configure(File configurationFile) {
+	public StandardServiceRegistryBuilder configure(URL url) {
+	public StandardServiceRegistryBuilder configure(LoadedConfig loadedConfig) {
+public interface ClassLoaderAccess {
+	public <T> Class<T> classForName(String name);
+	public URL locateResource(String resourceName);
+public interface InFlightMetadataCollector extends Mapping, MetadataImplementor {
+	public static interface DelayedPropertyReferenceHandler extends Serializable {
+		public void process(InFlightMetadataCollector metadataCollector);
+	public void addDelayedPropertyReferenceHandler(DelayedPropertyReferenceHandler handler);
+	public static interface EntityTableXref {
+	public static class DuplicateSecondaryTableException extends HibernateException {
+		public DuplicateSecondaryTableException(Identifier tableName) {
+	public EntityTableXref getEntityTableXref(String entityName);
+	public EntityTableXref addEntityTableXref(String entityName, Identifier primaryTableLogicalName, Table primaryTable, EntityTableXref superEntityTableXref);
+public interface MappingDefaults {
+	public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
+	public static final String DEFAULT_TENANT_IDENTIFIER_COLUMN_NAME = "tenant_id";
+	public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
+	public static final String DEFAULT_CASCADE_NAME = "none";
+	public static final String DEFAULT_PROPERTY_ACCESS_NAME = "property";
+	public String getImplicitSchemaName();
+	public String getImplicitCatalogName();
+	public boolean shouldImplicitlyQuoteIdentifiers();
+	public String getImplicitIdColumnName();
+	public String getImplicitTenantIdColumnName();
+	public String getImplicitDiscriminatorColumnName();
+	public String getImplicitPackageName();
+	public boolean isAutoImportEnabled();
+	public String getImplicitCascadeStyleName();
+	public String getImplicitPropertyAccessorName();
+	public boolean areEntitiesImplicitlyLazy();
+	public boolean areCollectionsImplicitlyLazy();
+	public AccessType getImplicitCacheAccessType();
+public interface MetadataBuildingContext {
+	public MetadataBuildingOptions getBuildingOptions();
+	public MappingDefaults getMappingDefaults();
+	public InFlightMetadataCollector getMetadataCollector();
+	public ClassLoaderAccess getClassLoaderAccess();
+	public ObjectNameNormalizer getObjectNameNormalizer();
+public interface MetadataBuildingOptions {
+	public boolean useNationalizedCharacterData();
+public interface MetadataImplementor extends Metadata, Mapping {
+public interface MetadataSourcesContributor {
+	public void contribute(MetadataSources metadataSources, IndexView jandexIndex);
+public interface NaturalIdUniqueKeyBinder {
+	public void addAttributeBinding(Property attributeBinding);
+	public void process();
-public class CollectionCacheInvalidator implements Integrator, PostInsertEventListener, PostDeleteEventListener,
+public class CollectionCacheInvalidator
-	public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory,
+	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
-	public static void bindDefaults(Mappings mappings) {
+	public static void bindDefaults(MetadataBuildingContext context) {
-	public static void bindPackage(String packageName, Mappings mappings) {
+	public static void bindPackage(String packageName, MetadataBuildingContext context) {
-	public static boolean isDefault(XClass clazz, Mappings mappings) {
+	public static boolean isDefault(XClass clazz, MetadataBuildingContext context) {
-public class AnnotationConfiguration extends Configuration {
-	public AnnotationConfiguration() {
-	public AnnotationConfiguration addAnnotatedClass(Class annotatedClass) throws MappingException {
-	public AnnotationConfiguration addPackage(String packageName) throws MappingException {
-	public ExtendedMappings createExtendedMappings() {
-	public AnnotationConfiguration addFile(String xmlFile) throws MappingException {
-	public AnnotationConfiguration addFile(File xmlFile) throws MappingException {
-	public AnnotationConfiguration addCacheableFile(File xmlFile) throws MappingException {
-	public AnnotationConfiguration addCacheableFile(String xmlFile) throws MappingException {
-	public AnnotationConfiguration addXML(String xml) throws MappingException {
-	public AnnotationConfiguration addURL(URL url) throws MappingException {
-	public AnnotationConfiguration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
-	public AnnotationConfiguration addDocument(org.w3c.dom.Document doc) throws MappingException {
-	public AnnotationConfiguration addResource(String resourceName) throws MappingException {
-	public AnnotationConfiguration addClass(Class persistentClass) throws MappingException {
-	public AnnotationConfiguration addJar(File jar) throws MappingException {
-	public AnnotationConfiguration addDirectory(File dir) throws MappingException {
-	public AnnotationConfiguration setInterceptor(Interceptor interceptor) {
-	public AnnotationConfiguration setProperties(Properties properties) {
-	public AnnotationConfiguration addProperties(Properties extraProperties) {
-	public AnnotationConfiguration mergeProperties(Properties properties) {
-	public AnnotationConfiguration setProperty(String propertyName, String value) {
-	public AnnotationConfiguration configure() throws HibernateException {
-	public AnnotationConfiguration configure(String resource) throws HibernateException {
-	public AnnotationConfiguration configure(URL url) throws HibernateException {
-	public AnnotationConfiguration configure(File configFile) throws HibernateException {
-	public AnnotationConfiguration configure(org.w3c.dom.Document document) throws HibernateException {
-	public AnnotationConfiguration setCacheConcurrencyStrategy(String clazz, String concurrencyStrategy) {
-	public AnnotationConfiguration setCacheConcurrencyStrategy(String clazz, String concurrencyStrategy, String region) {
-	public AnnotationConfiguration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy)
-	public AnnotationConfiguration setNamingStrategy(NamingStrategy namingStrategy) {
-	public AnnotationConfiguration setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator) {
+	public static IdentifierGeneratorDefinition getIdentifierGenerator(
-	public static void bindAnyMetaDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
+	public static void bindAnyMetaDefs(XAnnotatedElement annotatedElement, MetadataBuildingContext context) {
+	public Collection getCollectionBinding() {
-	public CollectionSecondPass(Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
+	public CollectionSecondPass(MetadataBuildingContext buildingContext, Collection collection, java.util.Map inheritedMetas) {
-	public CollectionSecondPass(Mappings mappings, Collection collection) {
+	public CollectionSecondPass(MetadataBuildingContext buildingContext, Collection collection) {
-public class Configuration implements Serializable {
-	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
-	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
-	public static final String ARTEFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";
+public class Configuration {
+	public static final String ARTEFACT_PROCESSING_ORDER = AvailableSettings.ARTIFACT_PROCESSING_ORDER;
+	public Configuration() {
+	public Configuration(BootstrapServiceRegistry serviceRegistry) {
-	public Configuration() {
+	public Configuration(MetadataSources metadataSources) {
-	public EntityTuplizerFactory getEntityTuplizerFactory() {
-	public ReflectionManager getReflectionManager() {
-//	public ComponentTuplizerFactory getComponentTuplizerFactory() {
-	public Iterator<PersistentClass> getClassMappings() {
+	public Properties getProperties() {
-	public Iterator getCollectionMappings() {
+	public Configuration setProperties(Properties properties) {
-	public Iterator<Table> getTableMappings() {
+	public String getProperty(String propertyName) {
-	public Iterator<MappedSuperclass> getMappedSuperclassMappings() {
+	public Configuration setProperty(String propertyName, String value) {
-	public java.util.Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
+	public Configuration addProperties(Properties properties) {
+	public void setImplicitNamingStrategy(ImplicitNamingStrategy implicitNamingStrategy) {
+	public void setPhysicalNamingStrategy(PhysicalNamingStrategy physicalNamingStrategy) {
-	public PersistentClass getClassMapping(String entityName) {
+	public Configuration configure() throws HibernateException {
-	public Collection getCollectionMapping(String role) {
+	public Configuration configure(String resource) throws HibernateException {
+	public StandardServiceRegistryBuilder getStandardServiceRegistryBuilder() {
-	public void setEntityResolver(EntityResolver entityResolver) {
+	public Configuration configure(URL url) throws HibernateException {
-	public EntityResolver getEntityResolver() {
+	public Configuration configure(File configFile) throws HibernateException {
-	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
+	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
+	public Configuration registerTypeContributor(TypeContributor typeContributor) {
-	public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
+	public Configuration registerTypeOverride(BasicType type) {
+	public Configuration registerTypeOverride(UserType type, String[] keys) {
+	public Configuration registerTypeOverride(CompositeUserType type, String[] keys) {
-	public Configuration addFile(final File xmlFile) throws MappingException {
+	public Configuration addFile(File xmlFile) throws MappingException {
-	public Mappings createMappings() {
-	public Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
+	public Interceptor getInterceptor() {
-	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
+	public Configuration setInterceptor(Interceptor interceptor) {
+	public EntityTuplizerFactory getEntityTuplizerFactory() {
+	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
+	public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
+	public SessionFactoryObserver getSessionFactoryObserver() {
+	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
+	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
+	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
-	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
+	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
-	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
+	public SessionFactory buildSessionFactory() throws HibernateException {
-	public List<SchemaUpdateScript> generateSchemaUpdateScriptList(Dialect dialect, DatabaseMetadata databaseMetadata)
+	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
-	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
+	public Map getSqlFunctions() {
+	public void addSqlFunction(String functionName, SQLFunction function) {
-	public void buildMappings() {
+	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
+	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
+	public void addAttributeConverter(AttributeConverter attributeConverter) {
+	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
+	public void addAttributeConverter(AttributeConverterDefinition definition) {
+	public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
+	public Map getNamedSQLQueries() {
+	public Map getSqlResultSetMappings() {
+	public java.util.Collection<NamedEntityGraphDefinition> getNamedEntityGraphs() {
+	public Map<String, NamedQueryDefinition> getNamedQueries() {
+	public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
+	public void buildMappings() {
-	public Map<String, NamedQueryDefinition> getNamedQueries() {
-	public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
-	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
-			public void contributeType(BasicType type) {
-			public void contributeType(UserType type, String[] keys) {
-			public void contributeType(CompositeUserType type, String[] keys) {
-	public SessionFactory buildSessionFactory() throws HibernateException {
-					public void sessionFactoryCreated(SessionFactory factory) {
-					public void sessionFactoryClosed(SessionFactory factory) {
-	public Interceptor getInterceptor() {
-	public Configuration setInterceptor(Interceptor interceptor) {
-	public Properties getProperties() {
-	public String getProperty(String propertyName) {
-	public Configuration setProperties(Properties properties) {
+	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
-	public Configuration addProperties(Properties extraProperties) {
+	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
-	public Configuration setProperty(String propertyName, String value) {
-	public Configuration configure() throws HibernateException {
-	public Configuration configure(String resource) throws HibernateException {
-	public Configuration configure(URL url) throws HibernateException {
-	public Configuration configure(File configFile) throws HibernateException {
-	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
-	public JaccPermissionDeclarations getJaccPermissionDeclarations() {
-	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy) {
-	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy, String region) {
-	public void setCacheConcurrencyStrategy(
-	public Configuration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy) {
-	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region) {
-	public Map<String,String> getImports() {
-	public Settings buildSettings(ServiceRegistry serviceRegistry) {
-	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) throws HibernateException {
-	public Map getNamedSQLQueries() {
-	public Map getSqlResultSetMappings() {
-	public NamingStrategy getNamingStrategy() {
-	public NamingStrategyDelegator getNamingStrategyDelegator() {
-	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
-	public Configuration setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator) {
-	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
-	public Mapping buildMapping() {
-			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
-			public Type getIdentifierType(String entityName) throws MappingException {
-			public String getIdentifierPropertyName(String entityName) throws MappingException {
-			public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
-	public Map getFilterDefinitions() {
-	public void addFilterDefinition(FilterDefinition definition) {
-	public Iterator iterateFetchProfiles() {
-	public void addFetchProfile(FetchProfile fetchProfile) {
-	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
-	public Map getSqlFunctions() {
-	public void addSqlFunction(String functionName, SQLFunction function) {
-	public TypeResolver getTypeResolver() {
-	public void registerTypeOverride(BasicType type) {
-	public void registerTypeOverride(UserType type, String[] keys) {
-	public void registerTypeOverride(CompositeUserType type, String[] keys) {
-	public void registerTypeContributor(TypeContributor typeContributor) {
-	public SessionFactoryObserver getSessionFactoryObserver() {
-	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
-	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
-	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
-	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
-	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
-	public void addAttributeConverter(AttributeConverter attributeConverter) {
-	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
-	public void addAttributeConverter(AttributeConverterDefinition definition) {
-	public java.util.Collection<NamedEntityGraphDefinition> getNamedEntityGraphs() {
-		public String getSchemaName() {
-		public void setSchemaName(String schemaName) {
-		public String getCatalogName() {
-		public void setCatalogName(String catalogName) {
-		public String getDefaultPackage() {
-		public void setDefaultPackage(String defaultPackage) {
-		public boolean isAutoImport() {
-		public void setAutoImport(boolean autoImport) {
-		public boolean isDefaultLazy() {
-		public void setDefaultLazy(boolean defaultLazy) {
-		public String getDefaultCascade() {
-		public void setDefaultCascade(String defaultCascade) {
-		public String getDefaultAccess() {
-		public void setDefaultAccess(String defaultAccess) {
-		public NamingStrategy getNamingStrategy() {
-		public void setNamingStrategy(NamingStrategy namingStrategy) {
-		public NamingStrategyDelegator getNamingStrategyDelegator() {
-		public void setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator) {
-		public TypeResolver getTypeResolver() {
-		public Iterator<PersistentClass> iterateClasses() {
-		public PersistentClass getClass(String entityName) {
-		public PersistentClass locatePersistentClassByEntityName(String entityName) {
-		public void addClass(PersistentClass persistentClass) throws DuplicateMappingException {
-		public void addImport(String entityName, String rename) throws DuplicateMappingException {
-		public Collection getCollection(String role) {
-		public Iterator<Collection> iterateCollections() {
-		public void addCollection(Collection collection) throws DuplicateMappingException {
-		public Table getTable(String schema, String catalog, String name) {
-		public Iterator<Table> iterateTables() {
-		public Table addTable(
-		public Table addDenormalizedTable(
-		public NamedQueryDefinition getQuery(String name) {
-		public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException {
-		public void addDefaultQuery(String name, NamedQueryDefinition query) {
-		public NamedSQLQueryDefinition getSQLQuery(String name) {
-		public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
-		public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition)
-		public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition)
-		public void addNamedEntityGraphDefintion(NamedEntityGraphDefinition definition)
-		public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query) {
-		public ResultSetMappingDefinition getResultSetMapping(String name) {
-		public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
-		public void applyResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
-		public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
-		public TypeDef getTypeDef(String typeName) {
-		public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
-		public Map getFilterDefinitions() {
-		public FilterDefinition getFilterDefinition(String name) {
-		public void addFilterDefinition(FilterDefinition definition) {
-		public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source) {
-		public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects() {
-		public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects() {
-		public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse() {
-		public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse() {
-		public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
-		public String getLogicalTableName(Table table) throws MappingException {
-		public void addTableBinding(
-			public void addBinding(String logicalName, Column physicalColumn) {
-		public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException {
-		public String getPhysicalColumnName(String logicalName, Table table) throws MappingException {
-		public String getLogicalColumnName(String physicalName, Table table) throws MappingException {
-		public void addSecondPass(SecondPass sp) {
-		public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue) {
-		public AttributeConverterDefinition locateAttributeConverter(Class converterClass) {
-		public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
-		public void addPropertyReference(String referencedClass, String propertyName) {
-		public void addUniquePropertyReference(String referencedClass, String propertyName) {
-		public void addToExtendsQueue(ExtendsQueueEntry entry) {
-		public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
-		public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
-		public MappedSuperclass getMappedSuperclass(Class type) {
-		public ObjectNameNormalizer getObjectNameNormalizer() {
-		public Properties getConfigurationProperties() {
-		public void addDefaultGenerator(IdGenerator generator) {
-		public boolean isInSecondPass() {
-		public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
-		public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
-		public boolean isSpecjProprietarySyntaxEnabled() {
-		public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
-		public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
-		public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
-		public boolean useNewGeneratorMappings() {
-		public boolean useImplicitDiscriminatorColumnForJoinedInheritance() {
-		public boolean ignoreExplicitDiscriminatorColumnForJoinedInheritance() {
-		public boolean useNationalizedCharacterData() {
-		public boolean forceDiscriminatorInSelectsByDefault() {
-		public IdGenerator getGenerator(String name) {
-		public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators) {
-		public void addGenerator(IdGenerator generator) {
-		public void addGeneratorTable(String name, Properties params) {
-		public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables) {
-		public Map<String, Join> getJoins(String entityName) {
-		public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) {
-		public AnnotatedClassType getClassType(XClass clazz) {
-		public AnnotatedClassType addClassType(XClass clazz) {
-		public Map<Table, List<String[]>> getTableUniqueConstraints() {
-		public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable() {
-		public void addUniqueConstraints(Table table, List uniqueConstraints) {
-		public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
-		public void addJpaIndexHolders(Table table, List<JPAIndexHolder> holders) {
-		public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
-		public String getFromMappedBy(String entityName, String propertyName) {
-		public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
-		public String getPropertyReferencedAssociation(String entityName, String propertyName) {
-		public ReflectionManager getReflectionManager() {
-		public Map getClasses() {
-		public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException {
-		public AnyMetaDef getAnyMetaDef(String name) {
-		public boolean isUseQuotedIdentifiersGlobally() {
-		public NamingStrategy getNamingStrategy() {
-		public void add(XmlDocument metadataXml) {
-		public void add(XClass annotatedClass) {
-		public boolean isEmpty() {
-	public static final MetadataSourceType[] DEFAULT_ARTEFACT_PROCESSING_ORDER = new MetadataSourceType[] {
-	public void setPrecedence(String precedence) {
-		public CacheHolder(String role, String usage, String region, boolean isClass, boolean cacheLazy) {
-		public String role;
-		public String usage;
-		public String region;
-		public boolean isClass;
-		public boolean cacheLazy;
-	public String getSecondaryTableName() {
-	public void setSecondaryTableName(String explicitTableName) {
-	public void setMappings(Mappings mappings) {
+	public void setBuildingContext(MetadataBuildingContext context) {
+										public AttributePath getAttributePath() {
+										public boolean isCollectionElement() {
+										public MetadataBuildingContext getBuildingContext() {
+								public AttributePath getAttributePath() {
+								public boolean isCollectionElement() {
+								public MetadataBuildingContext getBuildingContext() {
+							public String getClassName() {
+							public String getEntityName() {
+							public String getJpaEntityName() {
+						public Nature getNature() {
+						public EntityNaming getEntityNaming() {
+						public AttributePath getAttributePath() {
+						public Identifier getReferencedTableName() {
+						public Identifier getReferencedColumnName() {
+						public MetadataBuildingContext getBuildingContext() {
+							public String getClassName() {
+							public String getEntityName() {
+							public String getJpaEntityName() {
+						public Nature getNature() {
+						public EntityNaming getEntityNaming() {
+						public AttributePath getAttributePath() {
+						public Identifier getReferencedTableName() {
+						public Identifier getReferencedColumnName() {
+						public MetadataBuildingContext getBuildingContext() {
+						public MetadataBuildingContext getBuildingContext() {
+						public Identifier getReferencedTableName() {
+						public Identifier getReferencedPrimaryKeyColumnName() {
-public final class HbmBinder {
-	public static void bindRoot(
-	public static void bindRootClass(Element node, RootClass rootClass, Mappings mappings,
-	public static void bindClass(Element node, PersistentClass persistentClass, Mappings mappings,
-	public static void bindUnionSubclass(Element node, UnionSubclass unionSubclass,
-	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
-	public static void bindJoinedSubclass(Element node, JoinedSubclass joinedSubclass,
-	public static void bindColumns(final Element node, final SimpleValue simpleValue,
-	public static void bindSimpleValue(Element node, SimpleValue simpleValue, boolean isNullable,
-	public static void bindProperty(
-	public static void bindCollection(Element node, Collection collection, String className,
-	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
-	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
-	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
-	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
-	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
-	public static void bindArray(Element node, Array array, String prefix, String path,
-	public static void bindComposite(Element node, Component component, String path,
-	public static void bindCompositeId(Element node, Component component,
-	public static void bindComponent(
-	public static String getTypeFromXML(Element node) throws MappingException {
-	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
-	public static void bindIdentifierCollectionSecondPass(Element node,
-	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
-	public static void bindCollectionSecondPass(Element node, Collection collection,
-	public static java.util.Map getParameterTypes(Element queryElem) {
-		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
-		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
-		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
-		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
-		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
-		public abstract Collection create(Element node, String path, PersistentClass owner,
-		public String toString() {
-			public Collection create(Element node, String path, PersistentClass owner,
-			public Collection create(Element node, String path, PersistentClass owner,
-			public Collection create(Element node, String path, PersistentClass owner,
-			public Collection create(Element node, String path, PersistentClass owner,
-			public Collection create(Element node, String path, PersistentClass owner,
-			public Collection create(Element node, String path, PersistentClass owner,
-			public Collection create(Element node, String path, PersistentClass owner,
-		public static CollectionType collectionTypeFromString(String xmlTagName) {
-	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
-	public static String getEntityName(Element elem, Mappings model) {
-	public static String getClassName(String unqualifiedName, Mappings model) {
-	public static String getClassName(String unqualifiedName, String defaultPackage) {
-	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
-				public void handleEntity(String entityName, String className, Mappings mappings) {
-		public void handleEntity(String entityName, String className, Mappings mappings);
-		public ResolveUserTypeMappingSecondPass(SimpleValue simpleValue,
-		public void doSecondPass(java.util.Map persistentClasses)
-	public IndexOrUniqueKeySecondPass(Table table, String indexName, String[] columns, Mappings mappings) {
+	public IndexOrUniqueKeySecondPass(Table table, String indexName, String[] columns, MetadataBuildingContext buildingContext) {
-	public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, Mappings mappings) {
+	public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, MetadataBuildingContext buildingContext) {
-	public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, Mappings mappings, boolean unique) {
+	public IndexOrUniqueKeySecondPass(String indexName, Ejb3Column column, MetadataBuildingContext buildingContext, boolean unique) {
-	public InheritanceState(XClass clazz, Map<XClass, InheritanceState> inheritanceStatePerClass, Mappings mappings) {
+	public InheritanceState(
-public interface Mappings {
-	public TypeResolver getTypeResolver();
-	public NamingStrategy getNamingStrategy();
-	public void setNamingStrategy(NamingStrategy namingStrategy);
-	public NamingStrategyDelegator getNamingStrategyDelegator();
-	public void setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator);
-	public String getSchemaName();
-	public void setSchemaName(String schemaName);
-	public String getCatalogName();
-    public void setCatalogName(String catalogName);
-	public String getDefaultPackage();
-	public void setDefaultPackage(String defaultPackage);
-	public boolean isAutoImport();
-	public void setAutoImport(boolean autoImport);
-	public boolean isDefaultLazy();
-	public void setDefaultLazy(boolean defaultLazy);
-	public String getDefaultCascade();
-	public void setDefaultCascade(String defaultCascade);
-	public String getDefaultAccess();
-	public void setDefaultAccess(String defaultAccess);
-	public Iterator<PersistentClass> iterateClasses();
-	public PersistentClass getClass(String entityName);
-	public PersistentClass locatePersistentClassByEntityName(String entityName);
-	public void addClass(PersistentClass persistentClass) throws DuplicateMappingException;
-	public void addImport(String entityName, String rename) throws DuplicateMappingException;
-	public Collection getCollection(String role);
-	public Iterator<Collection> iterateCollections();
-	public void addCollection(Collection collection) throws DuplicateMappingException;
-	public Table getTable(String schema, String catalog, String name);
-	public Iterator<Table> iterateTables();
-	public Table addTable(String schema, String catalog, String name, String subselect, boolean isAbstract);
-	public Table addDenormalizedTable(String schema, String catalog, String name, boolean isAbstract, String subselect, Table includedTable)
-	public NamedQueryDefinition getQuery(String name);
-	public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException;
-	public NamedSQLQueryDefinition getSQLQuery(String name);
-	public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException;
-	public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
-	public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
-	public void addNamedEntityGraphDefintion(NamedEntityGraphDefinition namedEntityGraphDefinition);
-	public ResultSetMappingDefinition getResultSetMapping(String name);
-	public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException;
-	public TypeDef getTypeDef(String typeName);
-	public void addTypeDef(String typeName, String typeClass, Properties paramMap);
-	public Map getFilterDefinitions();
-	public FilterDefinition getFilterDefinition(String name);
-	public void addFilterDefinition(FilterDefinition definition);
-	public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source);
-	public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects();
-	public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects();
-	public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse();
-	public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse();
-	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
-	public String getLogicalTableName(Table table) throws MappingException;
-	public void addTableBinding(
-	public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException;
-	public String getPhysicalColumnName(String logicalName, Table table) throws MappingException;
-	public String getLogicalColumnName(String physicalName, Table table) throws MappingException;
-	public void addSecondPass(SecondPass sp);
-	public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue);
-	public AttributeConverterDefinition locateAttributeConverter(Class attributeConverterClass);
-	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters();
-	public static final class PropertyReference implements Serializable {
-		public final String referencedClass;
-		public final String propertyName;
-		public final boolean unique;
-		public PropertyReference(String referencedClass, String propertyName, boolean unique) {
-	public void addPropertyReference(String referencedClass, String propertyName);
-	public void addUniquePropertyReference(String referencedClass, String propertyName);
-	public void addToExtendsQueue(ExtendsQueueEntry entry);
-	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory();
-	public void addMappedSuperclass(Class type, org.hibernate.mapping.MappedSuperclass mappedSuperclass);
-	public ObjectNameNormalizer getObjectNameNormalizer();
-	public Properties getConfigurationProperties();
-	public void addDefaultGenerator(IdGenerator generator);
-	public IdGenerator getGenerator(String name);
-	public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators);
-	public void addGenerator(IdGenerator generator);
-	public void addGeneratorTable(String name, Properties params);
-	public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables);
-	public Map<String, Join> getJoins(String entityName);
-	public void addJoins(PersistentClass persistentClass, Map<String, Join> joins);
-	public AnnotatedClassType getClassType(XClass clazz);
-	public AnnotatedClassType addClassType(XClass clazz);
-	public Map<Table, List<String[]>> getTableUniqueConstraints();
-	public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable();
-	public void addUniqueConstraints(Table table, List uniqueConstraints);
-	public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders);
-	public void addJpaIndexHolders(Table table, List<JPAIndexHolder> jpaIndexHolders);
-	public void addMappedBy(String entityName, String propertyName, String inversePropertyName);
-	public String getFromMappedBy(String entityName, String propertyName);
-	public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef);
-	public String getPropertyReferencedAssociation(String entityName, String propertyName);
-	public ReflectionManager getReflectionManager();
-	public void addDefaultQuery(String name, NamedQueryDefinition query);
-	public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query);
-	public void addDefaultResultSetMapping(ResultSetMappingDefinition definition);
-	public Map getClasses();
-	public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException;
-	public AnyMetaDef getAnyMetaDef(String name);
-	public boolean isInSecondPass();
-	public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName);
-	public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property);
-	public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue);
-	public boolean isSpecjProprietarySyntaxEnabled();
-	public boolean useNewGeneratorMappings();
-	public boolean useImplicitDiscriminatorColumnForJoinedInheritance();
-	public boolean ignoreExplicitDiscriminatorColumnForJoinedInheritance();
-	public boolean useNationalizedCharacterData();
-	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName);
-	public boolean forceDiscriminatorInSelectsByDefault();
+	public static MetadataSourceType parsePrecedence(String value) {
-public class NamedSQLQuerySecondPass extends ResultSetMappingBinder implements QuerySecondPass {
-	public NamedSQLQuerySecondPass(Element queryElem, String path, Mappings mappings) {
-	public void doSecondPass(Map persistentClasses) throws MappingException {
-public abstract class ObjectNameNormalizer {
-	public static interface NamingStrategyHelper {
-		public String determineImplicitName(NamingStrategy strategy);
-		public String handleExplicitName(NamingStrategy strategy, String name);
-		public String determineImplicitName(NamingStrategyDelegator strategyDelegator);
-		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name);
-	public String normalizeDatabaseIdentifier(final String explicitName, NamingStrategyHelper helper) {
-	public String normalizeIdentifierQuoting(String identifier) {
-public abstract class ResultSetMappingBinder {
+	public void setEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
-	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
+	public Settings buildSettings(Map props, ServiceRegistry serviceRegistry) {
-	public static String getReferenceEntityName(PropertyData propertyData, XClass targetEntity, Mappings mappings) {
+	public static String getReferenceEntityName(PropertyData propertyData, XClass targetEntity, MetadataBuildingContext buildingContext) {
-	public static String getReferenceEntityName(PropertyData propertyData, Mappings mappings) {
+	public static String getReferenceEntityName(PropertyData propertyData, MetadataBuildingContext buildingContext) {
-	public static XClass getTargetEntity(PropertyData propertyData, Mappings mappings) {
+	public static XClass getTargetEntity(PropertyData propertyData, MetadataBuildingContext buildingContext) {
+	public void setBuildingContext(MetadataBuildingContext buildingContext) {
-	public void setMappings(Mappings mappings) {
-	public void setLocalGenerators(HashMap<String, IdGenerator> localGenerators) {
+	public void setLocalGenerators(HashMap<String, IdentifierGeneratorDefinition> localGenerators) {
-		public String determineImplicitName(NamingStrategy strategy) {
+		public Identifier determineImplicitName(final MetadataBuildingContext buildingContext) {
+							public String getClassName() {
+							public String getEntityName() {
+							public String getJpaEntityName() {
+						public EntityNaming getEntityNaming() {
+						public MetadataBuildingContext getBuildingContext() {
-		public String handleExplicitName(NamingStrategy strategy, String name) {
+		public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
-		public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
+		public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
-		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
+	public void bindTableForDiscriminatedSubclass(InFlightMetadataCollector.EntityTableXref superTableXref) {
-		public String determineImplicitName(NamingStrategy strategy) {
+		public Identifier determineImplicitName(MetadataBuildingContext buildingContext) {
-		public String handleExplicitName(NamingStrategy strategy, String name) {
-		public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
+		public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
-		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
+		public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
-	public void setMappings(Mappings mappings) {
+	public void setBuildingContext(MetadataBuildingContext buildingContext) {
-	public static void bindQuery(NamedQuery queryAnn, Mappings mappings, boolean isDefault) {
+	public static void bindQuery(
-	public static void bindNativeQuery(NamedNativeQuery queryAnn, Mappings mappings, boolean isDefault) {
+	public static void bindNativeQuery(
-	public static void bindNativeQuery(org.hibernate.annotations.NamedNativeQuery queryAnn, Mappings mappings) {
+	public static void bindNativeQuery(
-	public static void bindQueries(NamedQueries queriesAnn, Mappings mappings, boolean isDefault) {
+	public static void bindQueries(NamedQueries queriesAnn, MetadataBuildingContext context, boolean isDefault) {
-	public static void bindNativeQueries(NamedNativeQueries queriesAnn, Mappings mappings, boolean isDefault) {
+	public static void bindNativeQueries(
-	public static void bindQuery(org.hibernate.annotations.NamedQuery queryAnn, Mappings mappings) {
+	public static void bindQuery(
-	public static void bindQueries(org.hibernate.annotations.NamedQueries queriesAnn, Mappings mappings) {
+	public static void bindQueries(
-	public static void bindNamedStoredProcedureQuery(NamedStoredProcedureQuery annotation, Mappings mappings, boolean isDefault) {
+	public static void bindNamedStoredProcedureQuery(
-	public static void bindSqlResultsetMappings(SqlResultSetMappings ann, Mappings mappings, boolean isDefault) {
+	public static void bindSqlResultSetMappings(
-	public static void bindSqlResultsetMapping(SqlResultSetMapping ann, Mappings mappings, boolean isDefault) {
+	public static void bindSqlResultSetMapping(
-	public ResultsetMappingSecondPass(SqlResultSetMapping ann, Mappings mappings, boolean isDefault) {
+	public ResultsetMappingSecondPass(SqlResultSetMapping ann, MetadataBuildingContext context, boolean isDefault) {
-	public void setMappings(Mappings mappings) {
+	public void setBuildingContext(MetadataBuildingContext buildingContext) {
+	public void setBuildingContext(MetadataBuildingContext buildingContext) {
-	public void setDenormalizedSuperTable(Table denormalizedSuperTable) {
-	public void setMappings(Mappings mappings) {
-			public String determineImplicitName(NamingStrategy strategy) {
-			public String handleExplicitName(NamingStrategy strategy, String name) {
-			public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
+			public Identifier determineImplicitName(final MetadataBuildingContext buildingContext) {
+									public String getClassName() {
+									public String getEntityName() {
+									public String getJpaEntityName() {
+								public Identifier getOwningPhysicalTableName() {
+								public EntityNaming getOwningEntityNaming() {
+								public AttributePath getOwningAttributePath() {
+								public MetadataBuildingContext getBuildingContext() {
+									public String getClassName() {
+									public String getEntityName() {
+									public String getJpaEntityName() {
+									public String getClassName() {
+									public String getEntityName() {
+									public String getJpaEntityName() {
+								public String getOwningPhysicalTableName() {
+								public EntityNaming getOwningEntityNaming() {
+								public String getNonOwningPhysicalTableName() {
+								public EntityNaming getNonOwningEntityNaming() {
+								public AttributePath getAssociationOwningAttributePath() {
+								public MetadataBuildingContext getBuildingContext() {
-			public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
+			public Identifier handleExplicitName(
+			public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
+							public String getClassName() {
+							public String getEntityName() {
+							public String getJpaEntityName() {
+						public Identifier getOwningPhysicalTableName() {
+						public EntityNaming getOwningEntityNaming() {
+						public AttributePath getOwningAttributePath() {
+						public MetadataBuildingContext getBuildingContext() {
+							public String getClassName() {
+							public String getEntityName() {
+							public String getJpaEntityName() {
+							public String getClassName() {
+							public String getEntityName() {
+							public String getJpaEntityName() {
+						public String getOwningPhysicalTableName() {
+						public EntityNaming getOwningEntityNaming() {
+						public String getNonOwningPhysicalTableName() {
+						public EntityNaming getNonOwningEntityNaming() {
+						public AttributePath getAssociationOwningAttributePath() {
+						public MetadataBuildingContext getBuildingContext() {
+	public static Table buildAndFillTable(
-	public static Table buildAndFillTable(
-	public static Table fillTable(
-	public static void addIndexes(Table hibTable, Index[] indexes, Mappings mappings) {
+	public static void addIndexes(Table hibTable, Index[] indexes, MetadataBuildingContext buildingContext) {
-	public static void addIndexes(Table hibTable, javax.persistence.Index[] indexes, Mappings mappings) {
+	public static void addIndexes(Table hibTable, javax.persistence.Index[] indexes, MetadataBuildingContext buildingContext) {
+public interface AttributeConverterDefinitionCollector {
-	public void applyDiscoveredAttributeConverters(Configuration configuration) {
+	public void applyDiscoveredAttributeConverters(AttributeConverterDefinitionCollector collector) {
-	public Configuration getConfiguration();
+	public Metadata getMetadata();
-	public BeanValidationEventListener(ValidatorFactory factory, Properties properties) {
+	public BeanValidationEventListener(ValidatorFactory factory, Map settings) {
-	public void initialize(Configuration cfg) {
+	public void initialize(Map settings) {
-					public Configuration getConfiguration() {
+					public Metadata getMetadata() {
-	public GroupsPerOperation(Properties properties) {
+	public GroupsPerOperation(Map settings) {
-public class HbmNamingStrategyDelegate extends NamingStrategyDelegateAdapter {
-	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
-	public String determineImplicitElementCollectionTableName(
-	public String determineImplicitElementCollectionJoinColumnName(String ownerEntityName, String ownerJpaEntityName, String ownerEntityTable, String referencedColumnName, String propertyPath) {
-	public String determineImplicitEntityAssociationJoinTableName(
-	public String determineImplicitEntityAssociationJoinColumnName(
-	public String determineLogicalElementCollectionTableName(
-	public String determineLogicalEntityAssociationJoinTableName(
-public class ImprovedNamingStrategyDelegator implements NamingStrategyDelegator, Serializable {
-	public static final NamingStrategyDelegator DEFAULT_INSTANCE = new ImprovedNamingStrategyDelegator();
-	public ImprovedNamingStrategyDelegator() {
-	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
-public class JpaNamingStrategyDelegate extends NamingStrategyDelegateAdapter {
-	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
-	public String determineImplicitElementCollectionTableName(
-	public String determineImplicitElementCollectionJoinColumnName(
-	public String determineImplicitEntityAssociationJoinTableName(
-	public String determineImplicitEntityAssociationJoinColumnName(
-	public String determineLogicalElementCollectionTableName(
-	public String determineLogicalEntityAssociationJoinTableName(
-public class LegacyHbmNamingStrategyDelegate extends LegacyNamingStrategyDelegateAdapter {
-	public LegacyHbmNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
-	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
-	public String determineImplicitElementCollectionTableName(
-	public String determineImplicitElementCollectionJoinColumnName(
-	public String determineImplicitEntityAssociationJoinTableName(
-	public String determineImplicitEntityAssociationJoinColumnName(
-	public String determineLogicalElementCollectionTableName(
-	public String determineLogicalEntityAssociationJoinTableName(
-public class LegacyJpaNamingStrategyDelegate extends LegacyNamingStrategyDelegateAdapter {
-	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
-	public String determineImplicitElementCollectionTableName(
-	public String determineImplicitElementCollectionJoinColumnName(
-	public String determineImplicitEntityAssociationJoinTableName(
-	public String determineImplicitEntityAssociationJoinColumnName(
-	public String determineLogicalElementCollectionTableName(
-	public String determineLogicalEntityAssociationJoinTableName(
-public interface LegacyNamingStrategyDelegate extends NamingStrategyDelegate {
-	public static interface LegacyNamingStrategyDelegateContext {
-		public NamingStrategy getNamingStrategy();
-public abstract class LegacyNamingStrategyDelegateAdapter implements NamingStrategyDelegate, Serializable {
-	public LegacyNamingStrategyDelegateAdapter(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
-	public String toPhysicalTableName(String tableName) {
-	public String toPhysicalColumnName(String columnName) {
-	public String determineImplicitPropertyColumnName(String propertyPath) {
-	public String toPhysicalJoinKeyColumnName(String joinedColumn, String joinedTable) {
-	public String determineLogicalColumnName(String columnName, String propertyName) {
-	public String determineLogicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
-public class LegacyNamingStrategyDelegator
-	public static final NamingStrategyDelegator DEFAULT_INSTANCE = new LegacyNamingStrategyDelegator();
-	public LegacyNamingStrategyDelegator() {
-	public LegacyNamingStrategyDelegator(NamingStrategy namingStrategy) {
-	public NamingStrategy getNamingStrategy() {
-	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
-public interface NamingStrategyDelegate {
-	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName);
-	public String determineImplicitPropertyColumnName(String propertyPath);
-	public String determineImplicitElementCollectionTableName(
-	public String determineImplicitElementCollectionJoinColumnName(
-	public String determineImplicitEntityAssociationJoinTableName(
-	public String determineImplicitEntityAssociationJoinColumnName(
-	public String toPhysicalJoinKeyColumnName(String joinedColumn, String joinedTable);
-	public String determineLogicalColumnName(String columnName, String propertyName);
-	public String determineLogicalElementCollectionTableName(
-	public String determineLogicalEntityAssociationJoinTableName(
-	public String determineLogicalCollectionColumnName(String columnName, String propertyName, String referencedColumn);
-	public String toPhysicalTableName(String tableName);
-	public String toPhysicalColumnName(String columnName);
-public abstract class NamingStrategyDelegateAdapter implements NamingStrategyDelegate, Serializable {
-	public String determineImplicitPropertyColumnName(String propertyPath) {
-	public String toPhysicalTableName(String tableName) {
-	public String toPhysicalColumnName(String columnName) {
-	public String toPhysicalJoinKeyColumnName(String joinedColumn, String joinedTable) {
-	public String determineLogicalColumnName(String columnName, String propertyName) {
-	public String determineLogicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
-public interface NamingStrategyDelegator {
-	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm);
+	public SequenceInformationExtractor getSequenceInformationExtractor() {
+	public Exporter<Table> getTableExporter() {
+	public Exporter<Table> getTemporaryTableExporter() {
+	public Exporter<Sequence> getSequenceExporter() {
+	public Exporter<Index> getIndexExporter() {
+	public Exporter<ForeignKey> getForeignKeyExporter() {
+	public Exporter<Constraint> getUniqueKeyExporter() {
+	public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
+	public String getCurrentSchemaCommand() {
+	public SchemaNameResolver getSchemaNameResolver() {
+	public SequenceInformationExtractor getSequenceInformationExtractor() {
-	public String getAlterTableToAddUniqueKeyCommand(
+	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
-	public String getAlterTableToDropUniqueKeyCommand(
+	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
-	public String getAlterTableToAddUniqueKeyCommand(
+	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
-	public String getAlterTableToDropUniqueKeyCommand(
+	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
-	public String getAlterTableToAddUniqueKeyCommand(
+	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
-	public String getAlterTableToAddUniqueKeyCommand(
+	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata);
-	public String getAlterTableToDropUniqueKeyCommand(
+	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata);
+public class StandardConverters {
+	public static final Converter<Boolean> BOOLEAN = new Converter<Boolean>() {
+		public Boolean convert(Object value) {
+	public static final Converter<String> STRING = new Converter<String>() {
+		public String convert(Object value) {
+public class DefaultSchemaNameResolver implements SchemaNameResolver {
+	public static final DefaultSchemaNameResolver INSTANCE = new DefaultSchemaNameResolver();
+	public DefaultSchemaNameResolver() {
+	public String resolveSchemaName(Connection connection, Dialect dialect) throws SQLException {
+	public static class SchemaNameResolverJava17Delegate implements SchemaNameResolver {
+		public SchemaNameResolverJava17Delegate(Method getSchemaMethod) {
+		public String resolveSchemaName(Connection connection, Dialect dialect) throws SQLException {
+	public static class SchemaNameResolverFallbackDelegate implements SchemaNameResolver {
+		public static final SchemaNameResolverFallbackDelegate INSTANCE = new SchemaNameResolverFallbackDelegate();
+		public String resolveSchemaName(Connection connection, Dialect dialect) throws SQLException {
+public class ExtractedDatabaseMetaDataImpl implements ExtractedDatabaseMetaData {
+	public boolean supportsRefCursors() {
+	public JdbcEnvironment getJdbcEnvironment() {
+	public boolean supportsNamedParameters() {
+	public boolean supportsScrollableResults() {
+	public boolean supportsGetGeneratedKeys() {
+	public boolean supportsBatchUpdates() {
+	public boolean supportsDataDefinitionInTransaction() {
+	public boolean doesDataDefinitionCauseTransactionCommit() {
+	public Set<String> getExtraKeywords() {
+	public SQLStateType getSqlStateType() {
+	public boolean doesLobLocatorUpdateCopy() {
+	public String getConnectionCatalogName() {
+	public String getConnectionSchemaName() {
+	public LinkedHashSet<TypeInfo> getTypeInfoSet() {
+	public static class Builder {
+		public Builder(JdbcEnvironment jdbcEnvironment) {
+		public Builder apply(DatabaseMetaData databaseMetaData) throws SQLException {
+		public Builder setConnectionSchemaName(String connectionSchemaName) {
+		public Builder setConnectionCatalogName(String connectionCatalogName) {
+		public Builder setExtraKeywords(Set<String> extraKeywords) {
+		public Builder addExtraKeyword(String keyword) {
+		public Builder setTypeInfoSet(LinkedHashSet<TypeInfo> typeInfoSet) {
+		public Builder addTypeInfo(TypeInfo typeInfo) {
+		public Builder setSupportsRefCursors(boolean supportsRefCursors) {
+		public Builder setSupportsNamedParameters(boolean supportsNamedParameters) {
+		public Builder setSupportsScrollableResults(boolean supportsScrollableResults) {
+		public Builder setSupportsGetGeneratedKeys(boolean supportsGetGeneratedKeys) {
+		public Builder setSupportsBatchUpdates(boolean supportsBatchUpdates) {
+		public Builder setSupportsDataDefinitionInTransaction(boolean supportsDataDefinitionInTransaction) {
+		public Builder setDoesDataDefinitionCauseTransactionCommit(boolean doesDataDefinitionCauseTransactionCommit) {
+		public Builder setSqlStateType(SQLStateType sqlStateType) {
+		public Builder setLobLocatorUpdateCopy(boolean lobLocatorUpdateCopy) {
+		public ExtractedDatabaseMetaDataImpl build() {
+public class JdbcEnvironmentImpl implements JdbcEnvironment {
+	public JdbcEnvironmentImpl(ServiceRegistryImplementor serviceRegistry, Dialect dialect) {
+	public JdbcEnvironmentImpl(DatabaseMetaData databaseMetaData, Dialect dialect) throws SQLException {
+	public JdbcEnvironmentImpl(
+	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
+	public Dialect getDialect() {
+	public ExtractedDatabaseMetaData getExtractedDatabaseMetaData() {
+	public Identifier getCurrentCatalog() {
+	public Identifier getCurrentSchema() {
+	public QualifiedObjectNameFormatter getQualifiedObjectNameFormatter() {
+	public IdentifierHelper getIdentifierHelper() {
+	public boolean isReservedWord(String word) {
+	public SqlExceptionHelper getSqlExceptionHelper() {
+	public LobCreatorBuilder getLobCreatorBuilder() {
+	public TypeInfo getTypeInfoForJdbcCode(int jdbcTypeCode) {
+public class JdbcEnvironmentInitiator implements StandardServiceInitiator<JdbcEnvironment> {
+	public static final JdbcEnvironmentInitiator INSTANCE = new JdbcEnvironmentInitiator();
+	public Class<JdbcEnvironment> getServiceInitiated() {
+	public JdbcEnvironment initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+								public DialectResolutionInfo getDialectResolutionInfo() {
+	public static JdbcConnectionAccess buildBootstrapJdbcConnectionAccess(
+		public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
+		public Connection obtainConnection() throws SQLException {
+		public void releaseConnection(Connection connection) throws SQLException {
+		public boolean supportsAggressiveRelease() {
+		public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
+		public Connection obtainConnection() throws SQLException {
+		public void releaseConnection(Connection connection) throws SQLException {
+		public boolean supportsAggressiveRelease() {
+public class LobCreatorBuilderImpl implements LobCreatorBuilder {
+	 * The public factory method for obtaining the appropriate LOB creator (according to given
+	public static LobCreatorBuilderImpl makeLobCreatorBuilder(Map configValues, Connection jdbcConnection) {
+	public static LobCreatorBuilderImpl makeLobCreatorBuilder() {
+	public LobCreator buildLobCreator(LobCreationContext lobCreationContext) {
+public class NormalizingIdentifierHelperImpl implements IdentifierHelper {
+	public NormalizingIdentifierHelperImpl(
+	public Identifier normalizeQuoting(Identifier identifier) {
+	public Identifier toIdentifier(String text) {
+	public Identifier toIdentifier(String text, boolean quoted) {
+	public String toMetaDataCatalogName(Identifier identifier) {
+	public String toMetaDataSchemaName(Identifier identifier) {
+	public String toMetaDataObjectName(Identifier identifier) {
+	public Identifier fromMetaDataCatalogName(String catalogName) {
+	public Identifier toIdentifierFromMetaData(String text) {
+	public Identifier fromMetaDataSchemaName(String schemaName) {
+	public Identifier fromMetaDataObjectName(String objectName) {
+public class QualifiedObjectNameFormatterStandardImpl implements QualifiedObjectNameFormatter {
+	public QualifiedObjectNameFormatterStandardImpl(DatabaseMetaData databaseMetaData) throws SQLException {
+	public QualifiedObjectNameFormatterStandardImpl(String catalogSeparator, boolean catalogAtEnd) {
+	public QualifiedObjectNameFormatterStandardImpl() {
+	public String format(QualifiedTableName qualifiedTableName, Dialect dialect) {
+	public String format(QualifiedSequenceName qualifiedSequenceName, Dialect dialect) {
+	public String format(QualifiedName qualifiedName, Dialect dialect) {
+	public JdbcEnvironment getJdbcEnvironment();
+	public String getConnectionCatalogName();
+	public String getConnectionSchemaName();
+	public LinkedHashSet<TypeInfo> getTypeInfoSet();
-	public enum SQLStateType {
+	public Set<String> getExtraKeywords();
-	public Set<String> getExtraKeywords();
-	public String getConnectionSchemaName();
-	public String getConnectionCatalogName();
-	public LinkedHashSet<TypeInfo> getTypeInfoSet();
+public interface IdentifierHelper {
+	public Identifier normalizeQuoting(Identifier identifier);
+	public Identifier toIdentifier(String text);
+	public Identifier toIdentifier(String text, boolean quoted);
+	public String toMetaDataCatalogName(Identifier catalogIdentifier);
+	public String toMetaDataSchemaName(Identifier schemaIdentifier);
+	public String toMetaDataObjectName(Identifier identifier);
+	public Identifier fromMetaDataCatalogName(String catalogName);
+	public Identifier fromMetaDataSchemaName(String schemaName);
+	public Identifier fromMetaDataObjectName(String name);
+public interface JdbcEnvironment extends Service {
+	public Dialect getDialect();
+	public ExtractedDatabaseMetaData getExtractedDatabaseMetaData();
+	public Identifier getCurrentCatalog();
+	public Identifier getCurrentSchema();
+	public QualifiedObjectNameFormatter getQualifiedObjectNameFormatter();
+	public IdentifierHelper getIdentifierHelper();
+	public boolean isReservedWord(String word);
+	public SqlExceptionHelper getSqlExceptionHelper();
+	public LobCreatorBuilder getLobCreatorBuilder();
+	public TypeInfo getTypeInfoForJdbcCode(int jdbcTypeCode);
+public interface LobCreatorBuilder {
+public interface QualifiedObjectNameFormatter {
+	public String format(QualifiedTableName qualifiedTableName, Dialect dialect);
+	public String format(QualifiedSequenceName qualifiedSequenceName, Dialect dialect);
+	public String format(QualifiedName qualifiedName, Dialect dialect);
+public enum SQLStateType {
+	public static SQLStateType interpretReportedSQLStateType(int sqlStateType) {
+public interface SchemaNameResolver {
+	public String resolveSchemaName(Connection connection, Dialect dialect) throws SQLException;
-								public DialectResolutionInfo getDialectResolutionInfo() {
+	public JdbcEnvironment getJdbcEnvironment() {
-		public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
-		public Connection obtainConnection() throws SQLException {
-		public void releaseConnection(Connection connection) throws SQLException {
-		public boolean supportsAggressiveRelease() {
+	public ConnectionProvider getConnectionProvider() {
-		public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
-		public Connection obtainConnection() throws SQLException {
-		public void releaseConnection(Connection connection) throws SQLException {
-		public boolean supportsAggressiveRelease() {
+	public JdbcConnectionAccess getBootstrapJdbcConnectionAccess() {
-	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
+	public Dialect getDialect() {
-		public boolean supportsRefCursors() {
-		public boolean supportsNamedParameters() {
-		public boolean supportsScrollableResults() {
-		public boolean supportsGetGeneratedKeys() {
-		public boolean supportsBatchUpdates() {
-		public boolean supportsDataDefinitionInTransaction() {
-		public boolean doesDataDefinitionCauseTransactionCommit() {
-		public Set<String> getExtraKeywords() {
-		public SQLStateType getSqlStateType() {
-		public boolean doesLobLocatorUpdateCopy() {
-		public String getConnectionSchemaName() {
-		public String getConnectionCatalogName() {
-		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
-	public ConnectionProvider getConnectionProvider() {
-	public Dialect getDialect() {
+	public JdbcEnvironment getJdbcEnvironment();
+	public JdbcConnectionAccess getBootstrapJdbcConnectionAccess();
-	public CacheImplementor initiateService(SessionFactoryImplementor sessionFactory, Configuration configuration, ServiceRegistryImplementor registry) {
+	public CacheImplementor initiateService(
+	public String externalName() {
+	public NamedQueryDefinitionBuilder addParameterType(String name, String typeName) {
+	public NamedSQLQueryDefinitionBuilder addSynchronizedQuerySpace(String table) {
+	public NamedSQLQueryDefinitionBuilder addParameterType(String name, String typeName) {
+	public StandardSQLExceptionConverter() {
+	public StandardSQLExceptionConverter(SQLExceptionConversionDelegate... delegates) {
-	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, Mappings mappings, Mapping mapping, Map settings);
+	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata);
-	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, Mappings mappings, Mapping mapping, Map settings) {
+	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata) {
-	public void configure(Type type, Properties params, Dialect d) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
-	public static interface GenerationPlan {
+	public static interface GenerationPlan extends ExportableProducer {
-		public void registerPersistentGenerators(Map generatorMap);
-	public void registerPersistentGenerators(Map generatorMap) {
+	public void registerExportables(Database database) {
-	public void configure(Type type, Properties params, Dialect d) throws MappingException;
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnvironment) throws MappingException;
+public enum EntityIdentifierNature {
+public class ExportableColumn extends Column {
+	public ExportableColumn(Database database, Table table, String name, BasicType type) {
+	public ExportableColumn(
+	public static class ValueImpl implements Value {
+		public ValueImpl(ExportableColumn column, Table table, BasicType type) {
+		public int getColumnSpan() {
+		public Iterator<Selectable> getColumnIterator() {
+		public Type getType() throws MappingException {
+		public FetchMode getFetchMode() {
+		public Table getTable() {
+		public boolean hasFormula() {
+		public boolean isAlternateUniqueKey() {
+		public boolean isNullable() {
+		public boolean[] getColumnUpdateability() {
+		public boolean[] getColumnInsertability() {
+		public void createForeignKey() throws MappingException {
+		public boolean isSimpleValue() {
+		public boolean isValid(Mapping mapping) throws MappingException {
+		public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
+		public Object accept(ValueVisitor visitor) {
+	public static class ColumnIterator implements Iterator<Selectable> {
+		public ColumnIterator(ExportableColumn column) {
+		public boolean hasNext() {
+		public ExportableColumn next() {
+		public void remove() {
-	public void configure(Type type, Properties params, Dialect d) {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
-public interface IdentifierGeneratorAggregator {
-	public void registerPersistentGenerators(Map generatorMap);
+public interface IdentifierGeneratorAggregator extends ExportableProducer {
-	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
-	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
-	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
-	public Object generatorKey() {
-	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void registerExportables(Database database) {
+	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
+	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
+	public Object generatorKey() {
-public interface PersistentIdentifierGenerator extends IdentifierGenerator {
+public interface PersistentIdentifierGenerator extends IdentifierGenerator, ExportableProducer {
-	public void configure(Type type, Properties params, Dialect d) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnvironment) throws MappingException {
-	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void registerExportables(Database database) {
-	public void configure(Type type, Properties params, Dialect d) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment d) throws MappingException {
-    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
+    public void configure(Type type, Properties params, JdbcEnvironment env) throws MappingException {
-public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
-	/* COLUMN and TABLE should be renamed but it would break the public API */
-	public static final String COLUMN = "column";
-	public static final String DEFAULT_COLUMN_NAME = "next_hi";
-	public static final String TABLE = "table";
-	public static final String DEFAULT_TABLE_NAME = "hibernate_unique_key";
-	public void configure(Type type, Properties params, Dialect dialect) {
-	public synchronized Serializable generate(SessionImplementor session, Object object) {
-					public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
-	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
-	public String[] sqlDropStrings(Dialect dialect) {
-	public Object generatorKey() {
-public class TableHiLoGenerator extends TableGenerator {
-	public static final String MAX_LO = "max_lo";
-	public void configure(Type type, Properties params, Dialect d) {
-	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
-					public IntegralDataTypeHolder getNextValue() {
-					public String getTenantIdentifier() {
-	public void configure(Type type, Properties params, Dialect d) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
-	public void configure(Type type, Properties params, Dialect d) {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
-public interface DatabaseStructure {
+public interface DatabaseStructure extends ExportableProducer {
+	public void registerExportables(Database database) {
-	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void registerExportables(Database database) {
-	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
+	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void registerExportables(Database database) {
+	public void registerExportables(Database database) {
-public class DefaultIdentifierGeneratorFactory implements MutableIdentifierGeneratorFactory, Serializable, ServiceRegistryAwareService {
+public class DefaultIdentifierGeneratorFactory
+	public NamedQueryRepository(
-	public SessionFactoryImpl(
+	public SessionFactoryImpl(final MetadataImplementor metadata, SessionFactoryOptions options) {
-			public StandardServiceRegistry getServiceRegistry() {
-			public Interceptor getInterceptor() {
-			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
-							public void handleEntityNotFound(String entityName, Serializable id) {
+			public SessionFactoryImplementor getSessionFactory() {
+			public MetadataImplementor getMetadata() {
-			public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
-			public boolean isDirty(Object entity, EntityPersister persister, Session session) {
-			public void resetDirty(Object entity, EntityPersister persister, Session session) {
-			public void findDirty(
+	public void logDeprecationOfMultipleEntityModeSupport();
+	public void logDeprecationOfDomEntityModeSupport();
+	public void logDeprecationOfEmbedXmlSupport();
+	public void logDeprecationOfNonNamedIdAttribute(String entityName);
-	public static Object deserialize(InputStream inputStream) throws SerializationException {
+	public static <T> T deserialize(InputStream inputStream) throws SerializationException {
-	public static Object doDeserialize(
+	public static <T> T doDeserialize(
+	public static final String[] EMPTY_STRINGS = new String[0];
+	public static boolean isEmptyOrWhiteSpace(String string){
+	public static String nullIfEmpty(String value) {
+	public static interface ReturningBeanInfoDelegate<T> {
+		public T processBeanInfo(BeanInfo beanInfo) throws Exception;
+	public static <T> T visitBeanInfo(Class beanClass, ReturningBeanInfoDelegate<T> delegate) {
+	public static <T> T visitBeanInfo(Class beanClass, Class stopClass, ReturningBeanInfoDelegate<T> delegate) {
-	public Any(Mappings mappings, Table table) {
+	public Any(MetadataImplementor metadata, Table table) {
-	public Array(Mappings mappings, PersistentClass owner) {
+	public Array(MetadataImplementor metadata, PersistentClass owner) {
+public interface AttributeContainer {
-	public Bag(Mappings mappings, PersistentClass owner) {
+	public Bag(MetadataImplementor metadata, PersistentClass owner) {
-	public Mappings getMappings() {
+	public MetadataImplementor getMetadata() {
-	public java.util.Set getSynchronizedTables() {
+	public java.util.Set<String> getSynchronizedTables() {
+	public void setTypeParameters(java.util.Map parameterMap) {
-	public Component(Mappings mappings, PersistentClass owner) throws MappingException {
+	public Component(MetadataImplementor metadata, PersistentClass owner) throws MappingException {
-	public Component(Mappings mappings, Component component) throws MappingException {
+	public Component(MetadataImplementor metadata, Component component) throws MappingException {
-	public Component(Mappings mappings, Join join) throws MappingException {
+	public Component(MetadataImplementor metadata, Join join) throws MappingException {
-	public Component(Mappings mappings, Collection collection) throws MappingException {
+	public Component(MetadataImplementor metadata, Collection collection) throws MappingException {
+	public Component(MetadataImplementor metadata, Table table, PersistentClass owner) throws MappingException {
-		public void registerPersistentGenerators(Map generatorMap) {
+		public void registerExportables(Database database) {
-public abstract class Constraint implements RelationalModel, Serializable {
+public abstract class Constraint implements RelationalModel, Exportable, Serializable {
+	public DenormalizedTable(Schema schema, Identifier physicalTableName, boolean isAbstract, Table includedTable) {
+	public DenormalizedTable(Schema schema, Identifier physicalTableName, String subselectFragment, boolean isAbstract, Table includedTable) {
+	public DenormalizedTable(Schema schema, String subselect, boolean isAbstract, Table includedTable) {
+	public Column getColumn(Identifier name) {
+	public Table getIncludedTable() {
-	public DependantValue(Mappings mappings, Table table, KeyValue prototype) {
+	public DependantValue(MetadataImplementor metadata, Table table, KeyValue prototype) {
+	public ForeignKey() {
+	public String getExportIdentifier() {
+	public void disableCreation() {
+	public boolean isCreationEnabled() {
+	public void setName(String name) {
+	public Formula(String formula) {
-	public IdentifierBag(Mappings mappings, PersistentClass owner) {
+	public IdentifierBag(MetadataImplementor metadata, PersistentClass owner) {
-	public IdentifierCollection(Mappings mappings, PersistentClass owner) {
+	public IdentifierCollection(MetadataImplementor metadata, PersistentClass owner) {
-public class Index implements RelationalModel, Serializable {
+public class Index implements RelationalModel, Exportable, Serializable {
+	public static String buildSqlDropIndexString(
+	public static String buildSqlCreateIndexString(
+	public static String buildSqlCreateIndexString(
+	public String getExportIdentifier() {
-	public IndexedCollection(Mappings mappings, PersistentClass owner) {
+	public IndexedCollection(MetadataImplementor metadata, PersistentClass owner) {
-public class Join implements Serializable {
+public class Join implements AttributeContainer, Serializable {
-	public List(Mappings mappings, PersistentClass owner) {
+	public List(MetadataImplementor metadata, PersistentClass owner) {
-	public ManyToOne(Mappings mappings, Table table) {
+	public ManyToOne(MetadataImplementor metadata, Table table) {
-	public Map(Mappings mappings, PersistentClass owner) {
+	public Map(MetadataImplementor metadata, PersistentClass owner) {
+	public OneToMany(MetadataImplementor metadata, PersistentClass owner) throws MappingException {
-	public OneToMany(Mappings mappings, PersistentClass owner) throws MappingException {
-	public OneToOne(Mappings mappings, Table table, PersistentClass owner) throws MappingException {
+	public OneToOne(MetadataImplementor metadata, Table table, PersistentClass owner) throws MappingException {
-public abstract class PersistentClass implements Serializable, Filterable, MetaAttributable {
+public abstract class PersistentClass implements AttributeContainer, Serializable, Filterable, MetaAttributable {
+	public String getExportIdentifier() {
-	public PrimitiveArray(Mappings mappings, PersistentClass owner) {
+	public PrimitiveArray(MetadataImplementor metadata, PersistentClass owner) {
+	public void setCachingExplicitlyRequested(boolean explicitlyRequested) {
+	public boolean isCachingExplicitlyRequested() {
+	public Set(MetadataImplementor metadata, PersistentClass owner) {
-	public Set(Mappings mappings, PersistentClass owner) {
-public class SimpleAuxiliaryDatabaseObject extends AbstractAuxiliaryDatabaseObject {
-	public SimpleAuxiliaryDatabaseObject(String sqlCreateString, String sqlDropString) {
-	public SimpleAuxiliaryDatabaseObject(String sqlCreateString, String sqlDropString, HashSet dialectScopes) {
-	public String sqlCreateString(
-	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
-	public SimpleValue(Mappings mappings) {
+	public SimpleValue(MetadataImplementor metadata) {
-	public SimpleValue(Mappings mappings, Table table) {
+	public SimpleValue(MetadataImplementor metadata, Table table) {
-	public Mappings getMappings() {
+	public MetadataImplementor getMetadata() {
-public class Table implements RelationalModel, Serializable {
+public class Table implements RelationalModel, Serializable, Exportable {
+	public Table() {
-		public int hashCode() {
+	public Table(String name) {
-		public boolean equals(Object other) {
+	public Table(
-	public Table() { }
+	public Table(
-	public Table(String name) {
+	public Table(Schema schema, Identifier physicalTableName, String subselect, boolean isAbstract) {
+	public Table(Schema schema, String subselect, boolean isAbstract) {
+	public void setName(String name) {
+	public Identifier getNameIdentifier() {
+	public QualifiedTableName getQualifiedTableName() {
+	public boolean isQuoted() {
+	public void setQuoted(boolean quoted) {
+	public void setSchema(String schema) {
+	public String getSchema() {
+	public boolean isSchemaQuoted() {
+	public void setCatalog(String catalog) {
+	public String getCatalog() {
-	public void setName(String name) {
+	public boolean isCatalogQuoted() {
+	public Column getColumn(Identifier name) {
-	public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultCatalog,
+	public Iterator sqlAlterStrings(
-	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName,
+	public ForeignKey createForeignKey(
-	public String getSchema() {
-	public void setSchema(String schema) {
-	public String getCatalog() {
-	public void setCatalog(String catalog) {
-	public boolean isSchemaQuoted() {
-	public boolean isCatalogQuoted() {
-	public boolean isQuoted() {
-	public void setQuoted(boolean quoted) {
-	public Iterator getCheckConstraintsIterator() {
+	public Iterator<String> getCheckConstraintsIterator() {
+	public String getExportIdentifier() {
+		public int hashCode() {
+		public boolean equals(Object other) {
+	public String getExportIdentifier() {
-	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
+public interface PersisterCreationContext {
+	public String getContextId() {
+	public String getContextId() {
+	public String getContextId() {
-	public Iterable<GrantedPermission> getPermissionDeclarations() {
+	public void addPermissionDeclarations(Collection<GrantedPermission> permissionDeclarations) {
+	public Collection<GrantedPermission> getPermissionDeclarations() {
+	public String getContextId();
-public class ConfigLoader {
-				public JaxbProcessor initialize() {
-	public ConfigLoader(BootstrapServiceRegistry bootstrapServiceRegistry) {
-	public JaxbHibernateConfiguration loadConfigXmlResource(String cfgXmlResourceName) {
-	public Properties loadProperties(String resourceName) {
-	public R initiateService(SessionFactoryImplementor sessionFactory, Configuration configuration, ServiceRegistryImplementor registry);
+	public R initiateService(
-public class DatabaseMetadata {
-	public DatabaseMetadata(Connection connection, Dialect dialect) throws SQLException {
-	public DatabaseMetadata(Connection connection, Dialect dialect, boolean extras) throws SQLException {
-	public DatabaseMetadata(Connection connection, Dialect dialect, Configuration config) throws SQLException {
-	public DatabaseMetadata(Connection connection, Dialect dialect, Configuration config, boolean extras)
-	public TableMetadata getTableMetadata(String name, String schema, String catalog, boolean isQuoted) throws HibernateException {
-	public boolean isSequence(Object key) {
-	public boolean isTable(Object key) throws HibernateException {
-	public String toString() {
-	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
+	public SchemaExport(MetadataImplementor metadata) {
+	public SchemaExport(ServiceRegistry serviceRegistry, MetadataImplementor metadata) {
+			public boolean acceptsImportScriptActions() {
+			public void prepare() {
+			public void accept(String command) {
+			public void release() {
-	public SchemaExport(Configuration configuration) {
+	public SchemaExport(
-    public SchemaExport(Configuration configuration, Properties properties) throws HibernateException {
+	public SchemaExport(MetadataImplementor metadata, Connection connection) throws HibernateException {
+	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
+	public SchemaExport(Configuration configuration) {
+	public static void main(String[] args) {
-	public static void main(String[] args) {
+	public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
+	public List getExceptions() {
+		public static CommandLineArgs parseCommandLineArgs(String[] args) {
-	public List getExceptions() {
+	public void setNamingStrategy(String namingStrategy) {
+	public void setImplicitNamingStrategy(String implicitNamingStrategy) {
+	public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
+	public void setHaltonerror(boolean haltOnError) {
-	public void setNamingStrategy(String namingStrategy) {
-	public void setNamingStrategyDelegator(String namingStrategyDelegator) {
-	public void setHaltonerror(boolean haltOnError) {
+	public SchemaUpdate(MetadataImplementor metadata) {
+	public SchemaUpdate(ServiceRegistry serviceRegistry, MetadataImplementor metadata) throws HibernateException {
-	public SchemaUpdate(Configuration cfg) throws HibernateException {
+	public void execute(boolean script, boolean doUpdate) {
+	public void execute(Target target) {
-	public SchemaUpdate(Configuration configuration, Properties properties) throws HibernateException {
-	public SchemaUpdate(ServiceRegistry serviceRegistry, Configuration cfg) throws HibernateException {
+	public List getExceptions() {
-	public static void main(String[] args) {
+	public void setHaltOnError(boolean haltOnError) {
+	public void setFormat(boolean format) {
+	public void setOutputFile(String outputFile) {
+	public void setDelimiter(String delimiter) {
+	public static void main(String[] args) {
-	public void execute(boolean script, boolean doUpdate) {
-	public void execute(Target target) {
+		public static CommandLineArgs parseCommandLineArgs(String[] args) {
-	public List getExceptions() {
-	public void setHaltOnError(boolean haltOnError) {
-	public void setFormat(boolean format) {
-	public void setOutputFile(String outputFile) {
-	public void setDelimiter(String delimiter) {
+	public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
+public class SchemaUpdateCommand {
+	public SchemaUpdateCommand(String sql, boolean quiet) {
+	public String getSql() {
+	public boolean isQuiet() {
-public class SchemaUpdateScript {
-	public SchemaUpdateScript(String script, boolean quiet) {
-	public String getScript() {
-	public boolean isQuiet() {
-	public static String[] toStringArray(List<SchemaUpdateScript> scripts) {
-	public static List<SchemaUpdateScript> fromStringArray(String[] scriptsArray, boolean quiet) {
-	public void addFileset(FileSet set) {
+	public void addFileset(FileSet fileSet) {
+	public void setNamingStrategy(String namingStrategy) {
+	public void setImplicitNamingStrategy(String implicitNamingStrategy) {
+	public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
+	public File getOutputFile() {
+	public void setOutputFile(File outputFile) {
+	public boolean isHaltOnError() {
+	public void setHaltOnError(boolean haltOnError) {
+	public String getDelimiter() {
+	public void setDelimiter(String delimiter) {
-	public void setNamingStrategy(String namingStrategy) {
-	public void setNamingStrategyDelegator(String namingStrategyDelegator) {
-	public File getOutputFile() {
-	public void setOutputFile(File outputFile) {
-	public boolean isHaltOnError() {
-	public void setHaltOnError(boolean haltOnError) {
-	public String getDelimiter() {
-	public void setDelimiter(String delimiter) {
-	public SchemaValidator(Configuration cfg) throws HibernateException {
+	public SchemaValidator(MetadataImplementor metadata) {
-	public SchemaValidator(Configuration cfg, Properties connectionProperties) throws HibernateException {
+	public SchemaValidator(ServiceRegistry serviceRegistry, MetadataImplementor metadata) {
-	public SchemaValidator(ServiceRegistry serviceRegistry, Configuration cfg ) throws HibernateException {
+	public void validate() {
+		public static CommandLineArgs parseCommandLineArgs(String[] args) {
-	public void validate() {
+	public static MetadataImplementor buildMetadataFromMainArgs(String[] args) throws Exception {
-	public void addFileset(FileSet set) {
+	public void addFileset(FileSet fileSet) {
+	public void setNamingStrategy(String namingStrategy) {
+	public void setImplicitNamingStrategy(String implicitNamingStrategy) {
+	public void setPhysicalNamingStrategy(String physicalNamingStrategy) {
-	public void setNamingStrategy(String namingStrategy) {
-	public void setNamingStrategyDelegator(String namingStrategyDelegator) {
+public class ColumnInformationImpl implements ColumnInformation {
+	public ColumnInformationImpl(
+	public TableInformation getContainingTableInformation() {
+	public Identifier getColumnIdentifier() {
+	public int getTypeCode() {
+	public String getTypeName() {
+	public int getColumnSize() {
+	public int getDecimalDigits() {
+	public TruthValue getNullable() {
+	public String toString() {
+public class DatabaseInformationImpl implements DatabaseInformation, ExtractionContext.RegisteredObjectAccess {
+	public DatabaseInformationImpl() {
+	public TableInformation getTableInformation(Identifier catalogName, Identifier schemaName, Identifier tableName) {
+	public TableInformation getTableInformation(Schema.Name schemaName, Identifier tableName) {
+	public TableInformation getTableInformation(QualifiedTableName tableName) {
+	public SequenceInformation getSequenceInformation(Identifier catalogName, Identifier schemaName, Identifier sequenceName) {
+	public SequenceInformation getSequenceInformation(Schema.Name schemaName, Identifier sequenceName) {
+	public SequenceInformation getSequenceInformation(QualifiedSequenceName sequenceName) {
+	public void registerTableInformation(TableInformation tableInformation) {
+	public void registerSequenceInformation(SequenceInformation sequenceInformation) {
+	public TableInformation locateRegisteredTableInformation(QualifiedTableName tableName) {
+	public SequenceInformation locateRegisteredSequenceInformation(QualifiedSequenceName sequenceName) {
+	public void registerTable(TableInformation tableInformation) {
+public class ExtractionContextImpl implements ExtractionContext {
+	public ExtractionContextImpl(
+	public ServiceRegistry getServiceRegistry() {
+	public JdbcEnvironment getJdbcEnvironment() {
+	public Connection getJdbcConnection() {
+	public DatabaseMetaData getJdbcDatabaseMetaData() {
+	public Identifier getDefaultCatalog() {
+	public Identifier getDefaultSchema() {
+	public RegisteredObjectAccess getRegisteredObjectAccess() {
+	public void cleanup() {
+public class ForeignKeyInformationImpl implements ForeignKeyInformation {
+	public ForeignKeyInformationImpl(
+	public Identifier getForeignKeyIdentifier() {
+	public Iterable<ColumnReferenceMapping> getColumnReferenceMappings() {
+	public static class ColumnReferenceMappingImpl implements ColumnReferenceMapping {
+		public ColumnReferenceMappingImpl(ColumnInformation referencing, ColumnInformation referenced) {
+		public ColumnInformation getReferencingColumnMetadata() {
+		public ColumnInformation getReferencedColumnMetadata() {
+public class IndexInformationImpl implements IndexInformation {
+	public IndexInformationImpl(Identifier indexIdentifier, List<ColumnInformation> columnList) {
+	public Identifier getIndexIdentifier() {
+	public List<ColumnInformation> getIndexedColumns() {
+	public static Builder builder(Identifier indexIdentifier) {
+	public static class Builder {
+		public Builder(Identifier indexIdentifier) {
+		public Builder addColumn(ColumnInformation columnInformation) {
+		public IndexInformationImpl build() {
+public class InformationExtractorJdbcDatabaseMetaDataImpl implements InformationExtractor {
+	public static final String ALL_CATALOGS_FILTER = null;
+	public static final String SANS_CATALOG_FILTER = "";
+	public static final String ALL_SCHEMAS_FILTER = null;
+	public static final String SANS_SCHEMA_FILTER = "";
+	public InformationExtractorJdbcDatabaseMetaDataImpl(ExtractionContext extractionContext) {
+	public Collection<TableInformation> getTables(Identifier catalog, Identifier schema) {
+	public TableInformation extractTableInformation(ResultSet resultSet) throws SQLException {
+	public TableInformation getTable(Identifier catalog, Identifier schema, Identifier tableName) {
+	public Iterable<ColumnInformation> getColumns(TableInformation tableInformation) {
+	public PrimaryKeyInformation getPrimaryKey(TableInformationImpl tableInformation) {
+	public Iterable<IndexInformation> getIndexes(TableInformation tableInformation) {
+	public Iterable<ForeignKeyInformation> getForeignKeys(TableInformation tableInformation) {
+		public ForeignKeyBuilder addColumnMapping(ColumnInformation referencing, ColumnInformation referenced);
+		public ForeignKeyInformation build();
+		public ForeignKeyBuilderImpl(Identifier fkIdentifier) {
+		public ForeignKeyBuilder addColumnMapping(ColumnInformation referencing, ColumnInformation referenced) {
+		public ForeignKeyInformationImpl build() {
+public class PrimaryKeyInformationImpl implements PrimaryKeyInformation {
+	public PrimaryKeyInformationImpl(Identifier identifier, Iterable<ColumnInformation> columns) {
+	public Identifier getPrimaryKeyIdentifier() {
+	public Iterable<ColumnInformation> getColumns() {
+public class SequenceInformationExtractorH2DatabaseImpl implements SequenceInformationExtractor {
+	public static final SequenceInformationExtractorH2DatabaseImpl INSTANCE = new SequenceInformationExtractorH2DatabaseImpl();
+	public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException {
+public class SequenceInformationExtractorLegacyImpl implements SequenceInformationExtractor {
+	public static final SequenceInformationExtractorLegacyImpl INSTANCE = new SequenceInformationExtractorLegacyImpl();
+	public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException {
+public class SequenceInformationExtractorNoOpImpl implements SequenceInformationExtractor {
+	public static final SequenceInformationExtractorNoOpImpl INSTANCE = new SequenceInformationExtractorNoOpImpl();
+	public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException {
+public class SequenceInformationImpl implements SequenceInformation {
+	public SequenceInformationImpl(QualifiedSequenceName sequenceName, int incrementSize) {
+	public QualifiedSequenceName getSequenceName() {
+	public int getIncrementSize() {
+public class TableInformationImpl implements TableInformation {
+	public TableInformationImpl(
+	public QualifiedTableName getName() {
+	public boolean isPhysicalTable() {
+	public String getComment() {
+	public Iterable<ColumnInformation> getColumns() {
+	public ColumnInformation getColumn(Identifier columnIdentifier) {
+	public PrimaryKeyInformation getPrimaryKey() {
+	public Iterable<ForeignKeyInformation> getForeignKeys() {
+	public ForeignKeyInformation getForeignKey(Identifier fkIdentifier) {
+	public Iterable<IndexInformation> getIndexes() {
+	public IndexInformation getIndex(Identifier indexName) {
+	public String toString() {
+public class DatabaseInformationImpl implements DatabaseInformation, ExtractionContext.RegisteredObjectAccess {
+	public DatabaseInformationImpl(
+	public TableInformation getTableInformation(
+	public TableInformation getTableInformation(
+	public TableInformation getTableInformation(QualifiedTableName qualifiedTableName) {
+	public SequenceInformation getSequenceInformation(
+	public SequenceInformation getSequenceInformation(
+	public SequenceInformation getSequenceInformation(QualifiedSequenceName qualifiedSequenceName) {
+	public TableInformation locateRegisteredTableInformation(QualifiedTableName tableName) {
+	public SequenceInformation locateRegisteredSequenceInformation(QualifiedSequenceName sequenceName) {
+	public void registerTable(TableInformation tableInformation) {
+		public QualifiedTableName getName() {
+		public boolean isPhysicalTable() {
+		public String getComment() {
+		public Iterable<ColumnInformation> getColumns() {
+		public ColumnInformation getColumn(Identifier columnIdentifier) {
+		public PrimaryKeyInformation getPrimaryKey() {
+		public Iterable<ForeignKeyInformation> getForeignKeys() {
+		public ForeignKeyInformation getForeignKey(Identifier keyName) {
+		public Iterable<IndexInformation> getIndexes() {
+		public IndexInformation getIndex(Identifier indexName) {
-public interface ExtendedMappings extends Mappings {
+public interface ColumnInformation {
+	public TableInformation getContainingTableInformation();
+	public Identifier getColumnIdentifier();
+	public TruthValue getNullable();
+	public int getTypeCode();
+	public String getTypeName();
+	public int getColumnSize();
+	public int getDecimalDigits();
+public interface DatabaseInformation {
+	public TableInformation getTableInformation(Identifier catalogName, Identifier schemaName, Identifier tableName);
+	public TableInformation getTableInformation(Schema.Name schemaName, Identifier tableName);
+	public TableInformation getTableInformation(QualifiedTableName tableName);
+	public void registerTable(TableInformation tableInformation);
+	public SequenceInformation getSequenceInformation(
+	public SequenceInformation getSequenceInformation(Schema.Name schemaName, Identifier sequenceName);
+	public SequenceInformation getSequenceInformation(QualifiedSequenceName sequenceName);
+public class DatabaseInformationBuilder {
+	public DatabaseInformationBuilder(
+					public Connection obtainConnection() throws SQLException {
+					public void releaseConnection(Connection connection) throws SQLException {
+					public boolean supportsAggressiveRelease() {
+	public DatabaseInformationBuilder(
+	public DatabaseInformationBuilder prepareAll() {
+	public DatabaseInformationBuilder prepareCatalogAndSchema(Schema.Name schemaName) {
+	public DatabaseInformationBuilder prepareCatalog(Identifier catalog) {
+	public DatabaseInformationBuilder prepareSchema(Identifier schema) {
+	public DatabaseInformation build() {
+public interface ExtractionContext {
+	public static interface RegisteredObjectAccess {
+		public TableInformation locateRegisteredTableInformation(QualifiedTableName tableName);
+		public SequenceInformation locateRegisteredSequenceInformation(QualifiedSequenceName sequenceName);
+public interface ForeignKeyInformation {
+	public Identifier getForeignKeyIdentifier();
+	public Iterable<ColumnReferenceMapping> getColumnReferenceMappings();
+	public static interface ColumnReferenceMapping {
+		public ColumnInformation getReferencingColumnMetadata();
+		public ColumnInformation getReferencedColumnMetadata();
+public interface IndexInformation {
+	public Identifier getIndexIdentifier();
+	public List<ColumnInformation> getIndexedColumns();
+public interface InformationExtractor {
+	public Collection<TableInformation> getTables(Identifier catalog, Identifier schema);
+	public TableInformation getTable(Identifier catalog, Identifier schema, Identifier tableName);
+	public Iterable<ColumnInformation> getColumns(TableInformation tableInformation);
+	public PrimaryKeyInformation getPrimaryKey(TableInformationImpl tableInformation);
+	public Iterable<IndexInformation> getIndexes(TableInformation tableInformation);
+	public Iterable<ForeignKeyInformation> getForeignKeys(TableInformation tableInformation);
+public interface PrimaryKeyInformation {
+	public Identifier getPrimaryKeyIdentifier();
+	public Iterable<ColumnInformation> getColumns();
+public class SchemaExtractionException extends HibernateException {
+	public SchemaExtractionException(String message) {
+	public SchemaExtractionException(String message, Throwable root) {
+public interface SequenceInformation {
+	public QualifiedSequenceName getSequenceName();
+	public int getIncrementSize();
+public interface SequenceInformationExtractor {
+	public Iterable<SequenceInformation> extractMetadata(ExtractionContext extractionContext) throws SQLException;
+public interface TableInformation {
+	public QualifiedTableName getName();
+	public boolean isPhysicalTable();
+	public String getComment();
+	public Iterable<ColumnInformation> getColumns();
+	public ColumnInformation getColumn(Identifier columnIdentifier);
+	public PrimaryKeyInformation getPrimaryKey();
+	public Iterable<ForeignKeyInformation> getForeignKeys();
+	public ForeignKeyInformation getForeignKey(Identifier keyName);
+	public Iterable<IndexInformation> getIndexes();
+	public IndexInformation getIndex(Identifier indexName);
+public class HibernateSchemaManagementTool implements SchemaManagementTool, ServiceRegistryAwareService {
+	public SchemaCreator getSchemaCreator(Map options) {
+	public SchemaDropper getSchemaDropper(Map options) {
+	public SchemaMigrator getSchemaMigrator(Map options) {
+	public SchemaValidator getSchemaValidator(Map options) {
+	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
+public class SchemaCreatorImpl implements SchemaCreator {
+	public void doCreation(Metadata metadata, boolean createSchemas, List<Target> targets) throws SchemaManagementException {
+	public void doCreation(Metadata metadata, boolean createSchemas, Dialect dialect, List<Target> targets) throws SchemaManagementException {
+	public List<String> generateCreationCommands(Metadata metadata, boolean createSchemas) {
+					public boolean acceptsImportScriptActions() {
+					public void prepare() {
+					public void accept(String action) {
+					public void release() {
+	public List<String> generateCreationCommands(Metadata metadata, boolean createSchemas, Dialect dialect) {
+					public boolean acceptsImportScriptActions() {
+					public void prepare() {
+					public void accept(String action) {
+					public void release() {
+	public void doCreation(Metadata metadata, boolean createSchemas, Target... targets)
+	public void doCreation(Metadata metadata, boolean createSchemas, Dialect dialect, Target... targets)
+public class SchemaDropperImpl implements SchemaDropper {
+	public Iterable<String> generateDropCommands(MetadataImplementor metadata, boolean dropSchemas, Dialect dialect) {
+					public boolean acceptsImportScriptActions() {
+					public void prepare() {
+					public void accept(String action) {
+					public void release() {
+	public void doDrop(Metadata metadata, boolean dropSchemas, List<Target> targets) throws SchemaManagementException {
+	public void doDrop(Metadata metadata, boolean dropSchemas, Dialect dialect, List<Target> targets) throws SchemaManagementException {
+	public void doDrop(Metadata metadata, boolean dropSchemas, Target... targets) throws SchemaManagementException {
+	public void doDrop(Metadata metadata, boolean dropSchemas, Dialect dialect, Target... targets) throws SchemaManagementException {
+public class SchemaManagementToolInitiator implements StandardServiceInitiator<SchemaManagementTool> {
+	public static final SchemaManagementToolInitiator INSTANCE = new SchemaManagementToolInitiator();
+	public SchemaManagementTool initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+	public Class<SchemaManagementTool> getServiceInitiated() {
+public class SchemaMigratorImpl implements SchemaMigrator {
+	public void doMigration(
+public class SchemaValidatorImpl implements SchemaValidator {
+	public SchemaValidatorImpl(Dialect dialect) {
+	public void doValidation(Metadata metadata, DatabaseInformation databaseInformation) {
+public class StandardAuxiliaryDatabaseObjectExporter implements Exporter<AuxiliaryDatabaseObject> {
+	public StandardAuxiliaryDatabaseObjectExporter(Dialect dialect) {
+	public String[] getSqlCreateStrings(AuxiliaryDatabaseObject object, Metadata metadata) {
+	public String[] getSqlDropStrings(AuxiliaryDatabaseObject object, Metadata metadata) {
+public class StandardForeignKeyExporter implements Exporter<ForeignKey> {
+	public StandardForeignKeyExporter(Dialect dialect) {
+	public String[] getSqlCreateStrings(ForeignKey foreignKey, Metadata metadata) {
+	public String[] getSqlDropStrings(ForeignKey foreignKey, Metadata metadata) {
+public class StandardIndexExporter implements Exporter<Index> {
+	public StandardIndexExporter(Dialect dialect) {
+	public String[] getSqlCreateStrings(Index index, Metadata metadata) {
+	public String[] getSqlDropStrings(Index index, Metadata metadata) {
+public class StandardSequenceExporter implements Exporter<Sequence> {
+	public StandardSequenceExporter(Dialect dialect) {
+	public String[] getSqlCreateStrings(Sequence sequence, Metadata metadata) {
+	public String[] getSqlDropStrings(Sequence sequence, Metadata metadata) {
+public class StandardTableExporter implements Exporter<Table> {
+	public StandardTableExporter(Dialect dialect) {
+	public String[] getSqlCreateStrings(Table table, Metadata metadata) {
+	public String[] getSqlDropStrings(Table table, Metadata metadata) {
+public class StandardUniqueKeyExporter implements Exporter<Constraint> {
+	public StandardUniqueKeyExporter(Dialect dialect) {
+	public String[] getSqlCreateStrings(Constraint constraint, Metadata metadata) {
+	public String[] getSqlDropStrings(Constraint constraint, Metadata metadata) {
+public class TargetDatabaseImpl implements Target {
+	public TargetDatabaseImpl(JdbcConnectionAccess connectionAccess) {
+	public boolean acceptsImportScriptActions() {
+	public void prepare() {
+	public void accept(String action) {
+	public void release() {
+public class TargetFileImpl implements Target {
+	public TargetFileImpl(String outputFile) {
+	public boolean acceptsImportScriptActions() {
+	public void prepare() {
+	public void accept(String action) {
+	public void release() {
+public class TargetStdoutImpl implements Target {
+	public boolean acceptsImportScriptActions() {
+	public void prepare() {
+	public void accept(String action) {
+	public void release() {
+public class TemporaryTableExporter extends StandardTableExporter {
+	public TemporaryTableExporter(Dialect dialect) {
+	public String[] getSqlCreateStrings(Table exportable, Metadata metadata) {
+	public static String generateTableName(final Dialect dialect, final Table primaryTable) {
+	public String[] getSqlDropStrings(Table exportable, Metadata metadata) {
+public interface Exporter<T extends Exportable> {
+	public static final String[] NO_COMMANDS = new String[0];
+	public String[] getSqlCreateStrings(T exportable, Metadata metadata);
+	public String[] getSqlDropStrings(T exportable, Metadata metadata);
+public interface SchemaCreator {
+	public void doCreation(
+	public void doCreation(
+	public void doCreation(
+	public void doCreation(
+public interface SchemaDropper {
+	public void doDrop(Metadata metadata, boolean dropSchemas, Target... targets) throws SchemaManagementException;
+	public void doDrop(Metadata metadata, boolean dropSchemas, Dialect dialect, Target... targets) throws SchemaManagementException;
+	public void doDrop(Metadata metadata, boolean dropSchemas, List<Target> targets) throws SchemaManagementException;
+	public void doDrop(Metadata metadata, boolean dropSchemas, Dialect dialect, List<Target> targets) throws SchemaManagementException;
+public class SchemaManagementException extends HibernateException {
+	public SchemaManagementException(String message) {
+	public SchemaManagementException(String message, Throwable root) {
+public interface SchemaManagementTool extends Service {
+	public SchemaCreator getSchemaCreator(Map options);
+	public SchemaDropper getSchemaDropper(Map options);
+	public SchemaMigrator getSchemaMigrator(Map options);
+	public SchemaValidator getSchemaValidator(Map options);
+public interface SchemaMigrator {
+	public void doMigration(
+public interface SchemaValidator {
+	public void doValidation(
+public interface Target {
+	public boolean acceptsImportScriptActions();
+	public void prepare();
+	public void accept(String action);
+	public void release();
-public abstract class ForeignKeyDirection implements Serializable {
-	public abstract boolean cascadeNow(CascadePoint cascadePoint);
+public enum ForeignKeyDirection {
-	public static final ForeignKeyDirection FOREIGN_KEY_TO_PARENT = new ForeignKeyDirection() {
-		public String toString() {
-	public static final ForeignKeyDirection FOREIGN_KEY_FROM_PARENT = new ForeignKeyDirection() {
-		public String toString() {
+	public abstract boolean cascadeNow(CascadePoint cascadePoint);
+	public static class TableDialect extends Dialect {
+	public static class SequenceDialect extends Dialect {
+	public static class PooledSequenceDialect extends SequenceDialect {
-	public void setUp() {
-	public void tearDown() {
-public class EntityTest extends BaseCoreFunctionalTestCase {
+public class EntityTest extends BaseNonConfigCoreFunctionalTestCase {
-public class BeanValidationDisabledTest extends BaseCoreFunctionalTestCase {
+public class BeanValidationDisabledTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DDLTest extends BaseCoreFunctionalTestCase {
+public class DDLTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DDLWithoutCallbackTest extends BaseCoreFunctionalTestCase {
+public class DDLWithoutCallbackTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-		public MyImprovedNamingStrategyDelegator() {
+		public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
-			public String determineImplicitElementCollectionTableName(
-			public String determineImplicitElementCollectionJoinColumnName(
+		public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
-public class DefaultNamingCollectionElementTest extends BaseCoreFunctionalTestCase {
+public class DefaultNamingCollectionElementTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-public class DerivedIdentityWithBidirectionalAssociationTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityWithBidirectionalAssociationTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentitySimpleParentIdClassDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentitySimpleParentIdClassDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentitySimpleParentEmbeddedIdDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentitySimpleParentEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class IdMapManyToOneSpecjTest extends BaseCoreFunctionalTestCase {
+public class IdMapManyToOneSpecjTest extends BaseNonConfigCoreFunctionalTestCase {
-	public IdMapManyToOneSpecjTest() {
-public class DerivedIdentitySimpleParentEmbeddedDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentitySimpleParentEmbeddedDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityIdClassParentIdClassDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityIdClassParentIdClassDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityIdClassParentEmbeddedIdDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityIdClassParentEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityEmbeddedIdParentIdClassTest  extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityEmbeddedIdParentIdClassTest  extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityEmbeddedIdParentEmbeddedIdDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityEmbeddedIdParentEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentitySimpleParentSimpleDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentitySimpleParentSimpleDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentitySimpleParentSimpleDepMapsIdTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentitySimpleParentSimpleDepMapsIdTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityIdClassParentSameIdTypeIdClassDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityIdClassParentSameIdTypeIdClassDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityIdClassParentSameIdTypeEmbeddedIdDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityIdClassParentSameIdTypeEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class ForeignGeneratorViaMapsIdTest extends BaseCoreFunctionalTestCase {
+public class ForeignGeneratorViaMapsIdTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityEmbeddedIdParentSameIdTypeIdClassDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityEmbeddedIdParentSameIdTypeIdClassDepTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DerivedIdentityEmbeddedIdParentSameIdTypeEmbeddedIdDepTest extends BaseCoreFunctionalTestCase {
+public class DerivedIdentityEmbeddedIdParentSameIdTypeEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void testWithIntegrator() {
+	public void testWithTypeContributor() {
-public class InvestorIntegrator implements Integrator {
+public class InvestorTypeContributor implements TypeContributor {
-	public void integrate(Configuration configuration,
+	public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
-	public void disintegrate(SessionFactoryImplementor sessionFactory,
-public class EmbeddedTest extends BaseCoreFunctionalTestCase {
+public class EmbeddedTest extends BaseNonConfigCoreFunctionalTestCase {
-public class NewCustomEntityMappingAnnotationsTest extends BaseCoreFunctionalTestCase {
+public class NewCustomEntityMappingAnnotationsTest extends BaseUnitTestCase {
+	public void setUp() {
+	public void tearDown() {
-public class EnumeratedTypeTest extends BaseCoreFunctionalTestCase {
+public class EnumeratedTypeTest extends BaseNonConfigCoreFunctionalTestCase {
-public class OrmXmlEnumTypeTest extends BaseCoreFunctionalTestCase {
+public class OrmXmlEnumTypeTest extends BaseUnitTestCase {
-public class FkCircularityTest {
+public class FkCircularityTest extends BaseUnitTestCase {
-public class IdTest extends BaseCoreFunctionalTestCase {
+public class IdTest extends BaseNonConfigCoreFunctionalTestCase {
-public class IdTest extends BaseCoreFunctionalTestCase {
+public class IdTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void testMiscplacedImmutableAnnotation() {
+	public void testMisplacedImmutableAnnotation() {
-public abstract class AbstractJPAIndexTest extends BaseCoreFunctionalTestCase {
+public abstract class AbstractJPAIndexTest extends BaseNonConfigCoreFunctionalTestCase {
-public class IndexedCollectionTest extends BaseCoreFunctionalTestCase {
+public class IndexedCollectionTest extends BaseNonConfigCoreFunctionalTestCase {
-public class EagerIndexedCollectionTest extends BaseCoreFunctionalTestCase {
+public class EagerIndexedCollectionTest extends BaseNonConfigCoreFunctionalTestCase {
-public class JoinTest extends BaseCoreFunctionalTestCase {
+public class JoinTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
+	public void configure(Configuration cfg) {
-public class TestNamingStrategy extends DefaultNamingStrategy {
+public class TestNamingStrategy extends PhysicalNamingStrategyStandardImpl {
-	public String tableName(String tableName) {
+	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
-public class SerializableToBlobTypeTest extends BaseCoreFunctionalTestCase {
+public class SerializableToBlobTypeTest extends BaseNonConfigCoreFunctionalTestCase {
-public class ImprovedManyToManyDefaultsTest extends DefaultNamingManyToManyTest {
+public class JpaCompliantManyToManyImplicitNamingTest extends ManyToManyImplicitNamingTest {
-	public void configure(Configuration cfg) {
-public class DefaultNamingManyToManyTest extends BaseCoreFunctionalTestCase {
+public class ManyToManyImplicitNamingTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DummyNamingStrategy extends EJB3NamingStrategy {
-	public String tableName(String tableName) {
+public class DummyNamingStrategy extends PhysicalNamingStrategyStandardImpl {
+	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
-public class NamingStrategyTest {
+public class NamingStrategyTest extends BaseUnitTestCase {
-	public void testWithEJB3NamingStrategy() throws Exception {
+	public void testWithJpaCompliantNamingStrategy() throws Exception {
-public class OneToManyTest extends BaseCoreFunctionalTestCase {
+public class OneToManyTest extends BaseNonConfigCoreFunctionalTestCase {
-public class OneToOneTest extends BaseCoreFunctionalTestCase {
+public class OneToOneTest extends BaseNonConfigCoreFunctionalTestCase {
+						public boolean acceptsImportScriptActions() {
+						public void prepare() {
+						public void accept(String action) {
+						public void release() {
-public class AssociationOverrideSchemaTest extends BaseCoreFunctionalTestCase {
+public class AssociationOverrideSchemaTest extends BaseNonConfigCoreFunctionalTestCase {
-public class AssociationOverrideTest extends BaseCoreFunctionalTestCase {
+public class AssociationOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
-public class AttributeOverrideTest extends BaseCoreFunctionalTestCase {
+public class AttributeOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
-	public CollectionPersister(Collection collection, CollectionRegionAccessStrategy cache, Configuration cfg,
+	public CollectionPersister(
-	public EntityPersister(PersistentClass persistentClass, EntityRegionAccessStrategy cache,
+	public EntityPersister(
-public class PersisterTest extends BaseCoreFunctionalTestCase {
+public class PersisterTest extends BaseNonConfigCoreFunctionalTestCase {
-public class StrategyTest extends BaseCoreFunctionalTestCase {
+public class StrategyTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void testUnNamedConstraints() {
-	public void testNonExistentColumn() {
-	public static class UniqueNoNameA {
-		public long id;
-		public String name;
-	public static class UniqueNoNameB {
-		public long id;
-		public String name;
-	public static class UniqueColumnDoesNotExist {
-		public Integer id;
-		public Set<String> strings = new HashSet<String>();
+public class UniqueConstraintUnitTests extends BaseUnitTestCase {
+	public void testUnNamedConstraints() {
+	public void testNonExistentColumn() {
+	public static class UniqueNoNameA {
+		public long id;
+		public String name;
+	public static class UniqueNoNameB {
+		public long id;
+		public String name;
+	public static class UniqueColumnDoesNotExist {
+		public Integer id;
+		public Set<String> strings = new HashSet<String>();
-public class TimestampTest extends BaseCoreFunctionalTestCase {
+public class TimestampTest extends BaseUnitTestCase {
+	public void setUp() {
+	public void tearDown() {
-public class NonExistentOrmVersionTest extends BaseCoreFunctionalTestCase {
+public class NonExistentOrmVersionTest extends BaseUnitTestCase {
-	public String getCacheConcurrencyStrategy() {
-	public String getCacheConcurrencyStrategy() {
-public class ConfigurationSerializationTest extends BaseUnitTestCase {
-	public void testConfigurationSerializability() {
-	public static class Serial {
-		public String getId() {
-		public void setId(String id) {
-		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
+		public NoopEntityPersister(
-		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
+		public NoopCollectionPersister(
-public abstract class UserCollectionTypeTest extends BaseCoreFunctionalTestCase {
+public abstract class UserCollectionTypeTest extends BaseNonConfigCoreFunctionalTestCase {
-public class PersistentSetTest extends BaseCoreFunctionalTestCase {
+public class PersistentSetTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-	public void prepare(boolean allowAggressiveRelease) {
+	public void prepare(boolean allowAggressiveRelease) throws SQLException {
+	public JdbcEnvironment getJdbcEnvironment() {
+	public JdbcConnectionAccess getBootstrapJdbcConnectionAccess() {
-		public boolean supportsRefCursors() {
-		public boolean supportsNamedParameters() {
-		public boolean supportsScrollableResults() {
-		public boolean supportsGetGeneratedKeys() {
-		public boolean supportsBatchUpdates() {
-		public boolean supportsDataDefinitionInTransaction() {
-		public boolean doesDataDefinitionCauseTransactionCommit() {
-		public Set<String> getExtraKeywords() {
-		public SQLStateType getSqlStateType() {
-		public boolean doesLobLocatorUpdateCopy() {
-		public String getConnectionSchemaName() {
-		public String getConnectionCatalogName() {
-		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
+public class MetadataBuildingContextTestingImpl implements MetadataBuildingContext {
+	public MetadataBuildingContextTestingImpl(StandardServiceRegistry serviceRegistry) {
+	public MetadataBuildingOptions getBuildingOptions() {
+	public MappingDefaults getMappingDefaults() {
+	public InFlightMetadataCollector getMetadataCollector() {
+	public ClassLoaderAccess getClassLoaderAccess() {
+	public ObjectNameNormalizer getObjectNameNormalizer() {
-public class ComponentTest extends BaseCoreFunctionalTestCase {
+public class ComponentTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-	public void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
-public class CompositeElementTest extends BaseCoreFunctionalTestCase {
+public class CompositeElementTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
-	public void configure(Configuration cfg) {
-	public void configure(Configuration cfg) {
-public abstract class ConnectionManagementTestCase extends BaseCoreFunctionalTestCase {
+public abstract class ConnectionManagementTestCase extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-	public void configure(Configuration cfg) {
-public class ConstraintTest extends BaseCoreFunctionalTestCase {
+public class ConstraintTest extends BaseNonConfigCoreFunctionalTestCase {
-public class DynamicFilterTest extends BaseCoreFunctionalTestCase {
+public class DynamicFilterTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
+	public void addSettings(Map settings) {
-	public String getCacheConcurrencyStrategy() {
-	public String getCacheConcurrencyStrategy() {
-public class ABCTest extends LegacyTestCase {
+public class ABCTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void testMeta() throws Exception {
+//	public void testMeta() throws Exception {
-public class LocaleTest extends BaseCoreFunctionalTestCase {
+public class LocaleTest extends BaseNonConfigCoreFunctionalTestCase {
-public class AliasTest extends BaseCoreFunctionalTestCase {
+public class AliasTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void testDuplicateMapping() {
-	public String getCacheConcurrencyStrategy() {
-					public void prepare(boolean needsAutoCommit) throws SQLException {
-					public Connection getConnection() throws SQLException {
-					public void release() throws SQLException {
-					public void prepare(boolean needsAutoCommit) throws SQLException {
-					public Connection getConnection() throws SQLException {
-					public void release() throws SQLException {
-public class FullyQualifiedEntityNameNamingStrategyTest extends BaseCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
+public class FullyQualifiedEntityNameNamingStrategyTest extends BaseUnitTestCase {
+	public void setUp() {
+	public void tearDown() {
-	public static class MyNamingStrategy extends EJB3NamingStrategy {
+	public static class MyNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
-		public String classToTableName(String className) {
-		public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
+		public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
-		public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
+		public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
-public class NamingStrategyTest extends BaseCoreFunctionalTestCase {
+public class NamingStrategyTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-public class TestNamingStrategy extends DefaultNamingStrategy {
-	public String propertyToColumnName(String propertyName) {
+public class TestNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl implements PhysicalNamingStrategy {
+	public static final TestNamingStrategy INSTANCE = new TestNamingStrategy();
+	public TestNamingStrategy() {
-	public String columnName(String columnName) {
+	public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
-	public String logicalColumnName(String columnName, String
+	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
-    public String tableName(String tableName) {
+	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
+	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
+	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
+	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
+public class Address {
+	public String line1;
+	public String line2;
+	public ZipCode zipCode;
+	public String getLine1() {
+	public void setLine1(String line1) {
+	public String getLine2() {
+	public void setLine2(String line2) {
+	public ZipCode getZipCode() {
+	public void setZipCode(ZipCode zipCode) {
+public abstract class BaseAnnotationBindingTests extends BaseNamingTests {
+public abstract class BaseHbmBindingTests extends BaseNamingTests {
+public abstract class BaseNamingTests extends BaseUnitTestCase {
+	public void doTest() {
+public class Customer {
+	public Integer getId() {
+	public void setId(Integer id) {
+	public Integer getVersion() {
+	public void setVersion(Integer version) {
+	public String getName() {
+	public void setName(String name) {
+	public Set<String> getRegisteredTrademarks() {
+	public void setRegisteredTrademarks(Set<String> registeredTrademarks) {
+	public Address getHqAddress() {
+	public void setHqAddress(Address hqAddress) {
+	public Set<Address> getAddresses() {
+	public void setAddresses(Set<Address> addresses) {
+	public List<Order> getOrders() {
+	public void setOrders(List<Order> orders) {
+	public Set<Industry> getIndustries() {
+	public void setIndustries(Set<Industry> industries) {
+public class Industry {
+	public Integer getId() {
+	public void setId(Integer id) {
+	public String getName() {
+	public void setName(String name) {
+public class LegacyJpaNamingWithAnnotationBindingTests extends BaseAnnotationBindingTests {
+public class LegacyJpaNamingWithHbmBindingTests extends BaseHbmBindingTests {
+public class Order {
+	public Integer getId() {
+	public void setId(Integer id) {
+	public String getReferenceCode() {
+	public void setReferenceCode(String referenceCode) {
+	public Date getPlaced() {
+	public void setPlaced(Date placed) {
+	public Date getFulfilled() {
+	public void setFulfilled(Date fulfilled) {
+	public Customer getCustomer() {
+	public void setCustomer(Customer customer) {
+public enum State {
+public class ZipCode {
+	public Integer getId() {
+	public void setId(Integer id) {
+	public String getCode() {
+	public void setCode(String code) {
+	public String getCity() {
+	public void setCity(String city) {
+	public State getState() {
+	public void setState(State state) {
-	public String getCacheConcurrencyStrategy() {
-public class SaveOrUpdateTest extends BaseCoreFunctionalTestCase {
+public class SaveOrUpdateTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-public class PaginationTest extends BaseCoreFunctionalTestCase {
+public class PaginationTest extends BaseNonConfigCoreFunctionalTestCase {
-public class PropertyRefTest extends BaseCoreFunctionalTestCase {
+public class PropertyRefTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-public class CompleteComponentPropertyRefTest extends BaseCoreFunctionalTestCase {
+public class CompleteComponentPropertyRefTest extends BaseNonConfigCoreFunctionalTestCase {
-public class PartialComponentPropertyRefTest extends BaseCoreFunctionalTestCase {
+public class PartialComponentPropertyRefTest extends BaseNonConfigCoreFunctionalTestCase {
-	public String getCacheConcurrencyStrategy() {
-public class QueryCacheTest extends BaseCoreFunctionalTestCase {
+public class QueryCacheTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-public class QuoteGlobalTest extends BaseCoreFunctionalTestCase {
+public class QuoteGlobalTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void testTableGeneratorQuoting() {
+	public void testTableGeneratorQuoting() {
-public class QuoteTest extends BaseCoreFunctionalTestCase {
+public class QuoteTest extends BaseNonConfigCoreFunctionalTestCase {
-	public String getCacheConcurrencyStrategy() {
-	public void testDefaultNamingStrategyDelegator() {
+	public void testDefaultNamingStrategy() throws Exception {
-	public void testImprovedNamingStrategyDelegator() {
+	public void testDeprecatedNamingStrategy() throws Exception {
-	public void testEJB3NamingStrategy() {
+	public void testJpaCompliantNamingStrategy() throws Exception {
-	public void testSchemaExportNamingAndNamingDelegatorSpecified() {
-	public void testSchemaUpdateNamingAndNamingDelegatorSpecified() {
-	public void testSchemaValidatorNamingAndNamingDelegatorSpecified() {
-	public void testConstraintUpdate() {
+//	public void testConstraintUpdate() {
-public class SynonymValidationTest extends BaseCoreFunctionalTestCase {
+public class SynonymValidationTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory,
+	public void integrate(
-	public static class ProcedureDefinitions implements AuxiliaryDatabaseObject {
+	public static class ProcedureDefinitions implements AuxiliaryDatabaseObject, AuxiliaryDatabaseObject.Expandable {
-		public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
+		public boolean beforeTablesOnCreation() {
+		public String getExportIdentifier() {
+		public String[] sqlCreateStrings(Dialect dialect) {
-		public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
+		public String[] sqlDropStrings(Dialect dialect) {
-		public void addDialectScope(String dialectName) {
+		public boolean appliesToDialect(Dialect dialect) {
-		public boolean appliesToDialect(Dialect dialect) {
+		public boolean beforeTablesOnCreation() {
+		public String[] sqlCreateStrings(Dialect dialect) {
-		public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
+		public String[] sqlDropStrings(Dialect dialect) {
-		public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
+		public String getExportIdentifier() {
-					public void addDialectScope(String dialectName) {
+					public String getExportIdentifier() {
-					public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
+					public boolean beforeTablesOnCreation() {
-					public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
+					public String[] sqlCreateStrings(Dialect dialect) {
+					public String[] sqlDropStrings(Dialect dialect) {
-					public void addDialectScope(String dialectName) {
+					public String getExportIdentifier() {
-					public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
+					public boolean beforeTablesOnCreation() {
-					public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
+					public String[] sqlCreateStrings(Dialect dialect) {
+					public String[] sqlDropStrings(Dialect dialect) {
-					public void addDialectScope(String dialectName) {
+					public String getExportIdentifier() {
-					public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
+					public boolean beforeTablesOnCreation() {
+					public String[] sqlCreateStrings(Dialect dialect) {
-					public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
+					public String[] sqlDropStrings(Dialect dialect) {
-		public String classToTableName(String className) {
-		public String tableName(String tableName) {
-		public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
-		public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName) {
+		public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
-	public void testCollectionFetchVsLoad() throws Exception {
+//	public void testCollectionFetchVsLoad() throws Exception {
-	public String getCacheConcurrencyStrategy() {
-	public String getCacheConcurrencyStrategy() {
-public class CMTTest extends BaseCoreFunctionalTestCase {
+public class CMTTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-	public static boolean isColumnPresent(String tableName, String columnName, Configuration cfg) {
+	public static boolean isColumnPresent(String tableName, String columnName, Metadata metadata) {
-	public static boolean isTablePresent(String tableName, Configuration cfg) {
+	public static boolean isTablePresent(String tableName, Metadata metadata) {
-public class HibernateCacheTest extends BaseCoreFunctionalTestCase {
+public class HibernateCacheTest extends BaseNonConfigCoreFunctionalTestCase {
+	public HibernateCacheTest() {
-public abstract class EhCacheTest extends BaseCoreFunctionalTestCase {
+public abstract class EhCacheTest extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-	public static final String NAMING_STRATEGY_DELEGATOR = "hibernate.ejb.naming_strategy_delegator";
-	public static SchemaGenAction interpret(String value) {
+	public static SchemaGenAction interpret(Object value) {
-	public static SchemaGenSource interpret(String value) {
+	public static SchemaGenSource interpret(Object value) {
+	public MetadataImplementor getMetadata() {
+		public static final JpaEntityNotFoundDelegate INSTANCE = new JpaEntityNotFoundDelegate();
+					public MetadataImplementor perform() {
-		public Iterable<ClassDescriptor> getClassDescriptors();
-		public Iterable<PackageDescriptor> getPackageDescriptors();
-		public Iterable<MappingFileDescriptor> getMappingFileDescriptors();
-			public Iterable<ClassDescriptor> getClassDescriptors() {
-			public Iterable<PackageDescriptor> getPackageDescriptors() {
-			public Iterable<MappingFileDescriptor> getMappingFileDescriptors() {
-	public Configuration getHibernateConfiguration() {
-	public static AnnotationInstance getSingleAnnotation(
-					public ConfigLoader initialize() {
-					public JandexInitializer getJandexInitializer() {
-	public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory) {
-	public EntityManagerFactoryBuilder withDataSource(DataSource dataSource) {
-	public void cancel() {
-	public void generateSchema() {
-					public Object perform() {
-	public EntityManagerFactory build() {
-					public EntityManagerFactoryImpl perform() {
-	public ServiceRegistry buildServiceRegistry() {
-	public Configuration buildHibernateConfiguration(ServiceRegistry serviceRegistry) {
+	public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory) {
+	public EntityManagerFactoryBuilder withDataSource(DataSource dataSource) {
+	public void cancel() {
+	public void generateSchema() {
+	public EntityManagerFactory build() {
+		public static final ServiceRegistryCloser INSTANCE = new ServiceRegistryCloser();
-	public static class CacheRegionDefinition {
-		public static enum CacheType { ENTITY, COLLECTION }
-		public final CacheType cacheType;
-		public final String role;
-		public final String usage;
-		public final String region;
-		public final boolean cacheLazy;
-		public CacheRegionDefinition(
-	public static class JaccDefinition {
-		public final String contextId;
-		public final String role;
-		public final String clazz;
-		public final String actions;
-		public JaccDefinition(String contextId, String role, String clazz, String actions) {
-	public static class MetadataSources {
+	public static class MergedSettings {
-		public List<String> getAnnotatedMappingClassNames() {
-		public List<ConverterDescriptor> getConverterDescriptors() {
+		public MergedSettings() {
-		public List<InputStreamAccess> getMappingFileInputStreamAccessList() {
+		public Map getConfigurationValues() {
-		public List<String> getPackageNames() {
+		public JaccPermissionDeclarations getJaccPermissions(String jaccContextId) {
-		public List<String> collectMappingClassNames() {
-		public static class ConverterDescriptor {
-			public ConverterDescriptor(String converterClassName, boolean autoApply) {
+		public void addCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
+public class Helper {
+	public static PersistenceException persistenceException(
+	public static PersistenceException persistenceException(
-	public EntityManagerFactoryImpl(
-	public GenerationSourceFromMetadata(Configuration hibernateConfiguration, Dialect dialect, boolean creation) {
+	public GenerationSourceFromMetadata(
-	public static void performGeneration(Configuration hibernateConfiguration, ServiceRegistry serviceRegistry) {
+	public static void performGeneration(
-	public static class Generation {
+	public static class GenerationProcess {
-		public Generation(ServiceRegistry serviceRegistry) {
+		public GenerationProcess(ServiceRegistry serviceRegistry) {
-		public void execute(Configuration hibernateConfiguration) {
+		public void execute(MetadataImplementor metadata, Map configurationValues) {
-		public Iterable<String> getCommands() {
-		public void release() {
-public class MyNamingStrategy extends EJB3NamingStrategy {
+public class MyNamingStrategy extends PhysicalNamingStrategyStandardImpl {
-	public String tableName(String tableName) {
+	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
-public class SingularAttributeJoinTest extends AbstractMetamodelSpecificTest {
+public class SingularAttributeJoinTest extends BaseEntityManagerFunctionalTestCase {
-            public List<Integrator> getIntegrators() {
-            public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
-            public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
+public class NamingStrategyConfigurationTest extends BaseUnitTestCase {
+	public void testNamingStrategyFromProperty() {
-public class NamingStrategyDelegatorConfigurationTest extends BaseUnitTestCase {
-	public void testNamingStrategyDelegatorFromProperty() {
-	public EnversSchemaGenerator(ServiceRegistry serviceRegistry, Configuration configuration) {
+	public EnversSchemaGenerator(MetadataImplementor metadata) {
+	public EnversSchemaGenerator(ServiceRegistry serviceRegistry, MetadataImplementor metadata) {
-   public NodeEnvironment(Configuration configuration) {
-   public Configuration getConfiguration() {
+   public NodeEnvironment(StandardServiceRegistryBuilder ssrb) {
-   public StandardServiceRegistryImpl getServiceRegistry() {
+   public StandardServiceRegistry getServiceRegistry() {
-	public void configure(Configuration cfg) {
-	public void configure(Configuration cfg) {
-	public void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
-	public void configure(Configuration cfg) {
-public abstract class SingleNodeTestCase extends BaseCoreFunctionalTestCase {
+public abstract class SingleNodeTestCase extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-public class BulkOperationsTestCase extends BaseCoreFunctionalTestCase {
+public class BulkOperationsTestCase extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-public abstract class DualNodeTestCase extends BaseCoreFunctionalTestCase {
+public abstract class DualNodeTestCase extends BaseNonConfigCoreFunctionalTestCase {
-	public void configure(Configuration cfg) {
-		public Configuration getConfiguration() {
-		public StandardServiceRegistryImpl getServiceRegistry() {
+		public StandardServiceRegistry getServiceRegistry() {
-			public void run() {
+				public void run() {
+				public void run() {
-			public void run() {
-			public void run() {
+				public void run() {
+				public void run() {
-			public void run() {
-   public static Class<Object>[] getAnnotatedClasses() {
-//            public Void call() throws Exception {
-//            public Void call() throws Exception {
-//            public Customer call() throws Exception {
-//            public Void call() throws Exception {
-   public static Class<Object>[] getAnnotatedClasses() {
+   public static Class[] getAnnotatedClasses() {
-//   public static class MockTimestampsRegionImpl extends TimestampsRegionImpl {
-//      public MockTimestampsRegionImpl(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager, RegionFactory factory) {
-//      public void nodeModified(CacheEntryModifiedEvent event) {
+   public static Map buildBaselineSettings(
-   public static Configuration buildConfiguration(String regionPrefix,
-   public static Configuration buildCustomQueryCacheConfiguration(String regionPrefix, String queryCacheName) {
+   public static StandardServiceRegistryBuilder buildBaselineStandardServiceRegistryBuilder(
-   public static InfinispanRegionFactory startRegionFactory(ServiceRegistry reg,
+   public static StandardServiceRegistryBuilder buildCustomQueryCacheStandardServiceRegistryBuilder(
+   public static InfinispanRegionFactory startRegionFactory(ServiceRegistry serviceRegistry) {
-   public static InfinispanRegionFactory startRegionFactory(ServiceRegistry reg,
+   public static InfinispanRegionFactory startRegionFactory(
-   public static void stopRegionFactory(InfinispanRegionFactory factory,
+   public static void stopRegionFactory(
+   public static Properties toProperties(Map map) {
+public class BaseNonConfigCoreFunctionalTestCase extends BaseUnitTestCase {
+	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
+	public void onFailure() {
+	public final void beforeTest() throws Exception {
+	public final void afterTest() throws Exception {
+	public class RollbackWork implements Work {
+		public void execute(Connection connection) throws SQLException {
-	public static final String LOGGING_DEPENDENCY_CONFIG_NAME = "jbossLoggingTool";
-	public static final String LOGGING_PROCESSOR_NAME = "org.jboss.logging.processor.apt.LoggingToolsProcessor";
-	public void addLoggingProcessor(SourceSet sourceSet) {
+	public void addMetaGenProcessor(SourceSet sourceSet) {
-	public void addMetaGenProcessor(SourceSet sourceSet) {
-	public File getDependencyCacheDir() {
-	public void setDependencyCacheDir(File dependencyCacheDir) {
-	public ExpandedJavaCompilerFactory(Logger logger) {
-	public Compiler<JavaCompileSpec> create(CompileOptions options) {
-public class ExpandedJdk6JavaCompiler extends Jdk6JavaCompiler {
-	public ExpandedJdk6JavaCompiler(Logger logger) {
-	public WorkResult execute(JavaCompileSpec spec) {
-		public DiagnosticListenerImpl(Logger logger) {
-		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
-		public boolean sawError() {

Lines added containing method: 3870. Lines removed containing method: 1140. Tot = 5010
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public String getCustomSQLUpdate() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomDeleteCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNotNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+			public String getDefaultCatalogName();
+			public String getDefaultCatalogName();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public Boolean isLazy();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+			public String getDefaultSchemaName();
+			public String getDefaultSchemaName();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	-	public String getTable();
-	public String getTable();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Value getDiscriminator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isClassOrSuperclassJoin(Join join) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Table getRootTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getJoinNumber(Property prop) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomUpdateCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorInsertable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSequentialSelect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+		public int getColumnSpan() {
+		public int getColumnSpan() {
+			return 1;
+		}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public boolean isCustomInsertCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getColumnIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCustomSQLInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public Dialect getDialect() {
+	public Dialect getDialect() {
+		return dialect;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCustomSQLDelete() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistentClass getPersistentClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static ExecuteUpdateResultCheckStyle determineDefault(String customSql, boolean callable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public JaxbHbmKeyType getKey();
+	public JaxbHbmKeyType getKey();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	+	public boolean isCascadeDeleteEnabled() {
+	public boolean isCascadeDeleteEnabled() {
+		return "cascade".equals( keyManyToOneElement.getOnDelete().value() );
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	+	public boolean isOptional() {
+	public boolean isOptional() {
+		return jaxbSecondaryTableMapping.isOptional();
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public boolean supportsCascadeDelete() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+		public boolean hasFormula() {
+		public boolean hasFormula() {
+			return false;
+		}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAlias(Dialect dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+		public Settings getSettings();
+		public Settings getSettings();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public boolean isFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getSubclassSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isForceDiscriminator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {
-		return quoted ?
-				"`" + name + "`" :
-				name;
+		return name == null ? null : name.toString();

Lines added: 1. Lines removed: 3. Tot = 4
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getJoinClosureSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public static interface IllegalCollectionDereferenceExceptionBuilder {
-		public QueryException buildIllegalCollectionDereferenceException(
+	public interface IllegalCollectionDereferenceExceptionBuilder {
-public final class SingletonIterator implements Iterator {
+public final class SingletonIterator<T> implements Iterator<T> {
-	public Object next() {
+	public T next() {
-	public SingletonIterator(Object value) {
+	public SingletonIterator(T value) {
-	public void addJoin(Join join);
-    public CollectionType getDefaultCollectionType() throws MappingException {
+	public CollectionType getDefaultCollectionType() throws MappingException {
-    public boolean isArray() {
+	public boolean isArray() {
-    public Object accept(ValueVisitor visitor) {
+	public Object accept(ValueVisitor visitor) {
-    public void setOwner(PersistentClass owner) {
+	public void setOwner(PersistentClass owner) {
-	public void setCustomSQLDeleteAll(String customSQLDeleteAll, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
+	public void setCustomSQLDeleteAll(
-	public void addFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
+	public void addFilter(
-	public void addManyToManyFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
+	public void addManyToManyFilter(
-    public String toString() {
+	public String toString() {
-    public int getSqlTypeCode(Mapping mapping) throws MappingException {
-    public Integer getSqlTypeCode() {
-    public void setSqlTypeCode(Integer typeCode) {
-    public String getSqlType(Dialect dialect, Mapping mapping) throws HibernateException {
+	public int getSqlTypeCode(Mapping mapping) throws MappingException {
+	public Integer getSqlTypeCode() {
+	public void setSqlTypeCode(Integer typeCode) {
+	public String getSqlType(Dialect dialect, Mapping mapping) throws HibernateException {
-	public DenormalizedTable(Schema schema, Identifier physicalTableName, String subselectFragment, boolean isAbstract, Table includedTable) {
+	public DenormalizedTable(
-    public void createForeignKeys() {
+	public void createForeignKeys() {
-    public Column getColumn(Column column) {
+	public Column getColumn(Column column) {
-    public Iterator getColumnIterator() {
+	public Iterator getColumnIterator() {
-    public boolean containsColumn(Column column) {
+	public boolean containsColumn(Column column) {
-    public PrimaryKey getPrimaryKey() {
+	public PrimaryKey getPrimaryKey() {
-    public Iterator getUniqueKeyIterator() {
+	public Iterator getUniqueKeyIterator() {
-    public Iterator getIndexIterator() {
+	public Iterator getIndexIterator() {
-	public String sqlConstraintString(Dialect dialect, String constraintName, String defaultCatalog, String defaultSchema) {
+	public String sqlConstraintString(
-    public String getReferencedEntityName() {
+	public String getReferencedEntityName() {
+	public void setTypeUsingReflection(String className, String propertyName) {
-	public void setTypeUsingReflection(String className, String propertyName) {}
-	public Iterator getSubclassIterator() {
+	public Iterator<Subclass> getSubclassIterator() {
-    public String toString() {
+	public String toString() {
-	public void addFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
+	public void addFilter(
-    public void setUpdateable(boolean mutable) {
+	public void setUpdateable(boolean mutable) {
-    public int getSubclassId() {
+	public int getSubclassId() {
-    public Table getTable() {
+	public Table getTable() {
-    public Property getIdentifierProperty() {
+	public Property getIdentifierProperty() {
-    public Property getDeclaredIdentifierProperty() {
+	public Property getDeclaredIdentifierProperty() {
-    public KeyValue getIdentifier() {
+	public KeyValue getIdentifier() {
-    public boolean hasIdentifierProperty() {
+	public boolean hasIdentifierProperty() {
-    public Value getDiscriminator() {
+	public Value getDiscriminator() {
-    public boolean isInherited() {
+	public boolean isInherited() {
-    public boolean isPolymorphic() {
+	public boolean isPolymorphic() {
-    public RootClass getRootClass() {
+	public RootClass getRootClass() {
-    public Iterator getPropertyClosureIterator() {
+	public Iterator getPropertyClosureIterator() {
-    public Iterator getTableClosureIterator() {
+	public Iterator getTableClosureIterator() {
-    public Iterator getKeyClosureIterator() {
+	public Iterator getKeyClosureIterator() {
-    public void addSubclass(Subclass subclass) throws MappingException {
+	public void addSubclass(Subclass subclass) throws MappingException {
-    public boolean isExplicitPolymorphism() {
+	public boolean isExplicitPolymorphism() {
-    public Property getVersion() {
+	public Property getVersion() {
-    public Property getDeclaredVersion() {
+	public Property getDeclaredVersion() {
-    public boolean isVersioned() {
+	public boolean isVersioned() {
-    public boolean isMutable() {
+	public boolean isMutable() {
-    public boolean hasEmbeddedIdentifier() {
+	public boolean hasEmbeddedIdentifier() {
-    public Class getEntityPersisterClass() {
+	public Class getEntityPersisterClass() {
-    public Table getRootTable() {
+	public Table getRootTable() {
-    public void setEntityPersisterClass(Class persister) {
+	public void setEntityPersisterClass(Class persister) {
-    public PersistentClass getSuperclass() {
+	public PersistentClass getSuperclass() {
-    public KeyValue getKey() {
+	public KeyValue getKey() {
-    public boolean isDiscriminatorInsertable() {
+	public boolean isDiscriminatorInsertable() {
-    public boolean isForceDiscriminator() {
+	public boolean isForceDiscriminator() {
-    public String getWhere() {
+	public String getWhere() {
-    public void validate(Mapping mapping) throws MappingException {
+	public void validate(Mapping mapping) throws MappingException {
-    public String getCacheConcurrencyStrategy() {
+	public String getCacheConcurrencyStrategy() {
-    public boolean isLazyPropertiesCacheable() {
+	public boolean isLazyPropertiesCacheable() {
-    public boolean isJoinedSubclass() {
+	public boolean isJoinedSubclass() {
-    public java.util.Set getSynchronizedTables() {
+	public java.util.Set getSynchronizedTables() {
-    public Object accept(PersistentClassVisitor mv) {
+	public Object accept(PersistentClassVisitor mv) {
-    public String toString() {
+	public String toString() {
+	public void validate(Mapping mapping) throws MappingException {
-    public void validate(Mapping mapping) throws MappingException {
-    public String sqlConstraintString(
+	public String sqlConstraintString(
-    public String sqlCreateString(Dialect dialect, Mapping p,
+	public String sqlCreateString(
-    public String sqlDropString(Dialect dialect, String defaultCatalog,
+	public String sqlDropString(
-	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
+	public int bind(
-	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
+	public int bind(
-    public boolean isManyToMany() {
+	public boolean isManyToMany() {
-	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
+	public String fromJoinFragment(
-	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
+	public String whereJoinFragment(
-	public void initialize(Serializable key, SessionImplementor session)
+	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
-    public boolean isManyToMany() {
+	public boolean isManyToMany() {
-    public String getTableName() {
+	public String getTableName() {
-    public String filterFragment(String alias) throws MappingException {
+	public String filterFragment(String alias) throws MappingException {
-    public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
+	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
-	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) throws HibernateException {
+	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session)
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
+	public void resetIdentifier(
-	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
+	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
-	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
+	public void processInsertGeneratedProperties(
-	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
+	public void processUpdateGeneratedProperties(
-	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
+	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session)
-    public Class<? extends EntityPersister> singleTableEntityPersister() {
+	public Class<? extends EntityPersister> singleTableEntityPersister() {
-        public String toString() {
+		public String toString() {
-        public String toString() {
+		public String toString() {
-	public Connection getPhysicalConnection();
-	public void afterStatement();
-	public void afterTransaction();
-	public Connection manualDisconnect();
-	public void manualReconnect(Connection suppliedConnection);
-	public LogicalConnectionImplementor makeShareableCopy();
-	public PhysicalJdbcTransaction getPhysicalJdbcTransaction();
-	public void serialize(ObjectOutputStream oos) throws IOException;
-	public void doAction(boolean successful);
+		public int hashCode() {
-	public List getColumns() {
+	public List<String> getColumns() {
-	public SimpleSelect setLockOptions( LockOptions lockOptions ) {
+	public SimpleSelect setLockOptions(LockOptions lockOptions) {
-    public static String renderOrderByStringTemplate(
+	public static String renderOrderByStringTemplate(
-    public void traceIn(String ruleName, AST tree) {
+	public void traceIn(String ruleName, AST tree) {
-    public void traceOut(String ruleName, AST tree) {
+	public void traceOut(String ruleName, AST tree) {
-    public void execute() throws BuildException {
+	public void execute() throws BuildException {
-    public void setText(boolean text) {
+	public void setText(boolean text) {
-    public void execute() throws BuildException {
+	public void execute() throws BuildException {
-    public void execute() throws BuildException {
+	public void execute() throws BuildException {
-    public String toString() {
+	public String toString() {
-    public void execute() throws BuildException {
+	public void execute() throws BuildException {
-        public boolean equals(Object other) {
+		public boolean equals(Object other) {
-        public int hashCode() {
+		public int hashCode() {
-    public List transformList(List list) {
+	public List transformList(List list) {
-    public Object transformTuple(Object[] tuple, String[] aliases) {
+	public Object transformTuple(Object[] tuple, String[] aliases) {
-	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
+	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
-    public String toString() {
+	public String toString() {
-        public boolean equals(Object obj) {
+		public boolean equals(Object obj) {
-        public int hashCode() {
+		public int hashCode() {
-    public String toString() {
+	public String toString() {
-    public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
+	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
-    public Object[] getPropertyValues(Object entity) throws HibernateException {
+	public Object[] getPropertyValues(Object entity) throws HibernateException {
-    public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) throws HibernateException {
+	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
-    public boolean isLifecycleImplementor() {
+	public boolean isLifecycleImplementor() {
-    public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
+	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
-    public boolean hasUninitializedLazyProperties(Object entity) {
+	public boolean hasUninitializedLazyProperties(Object entity) {
-    public VersionValue getUnsavedValue() {
+	public VersionValue getUnsavedValue() {
-    public final boolean isComponentType() {
+	public final boolean isComponentType() {
-    public boolean isSame(Object x, Object y) throws HibernateException {
+	public boolean isSame(Object x, Object y) throws HibernateException {
-	public boolean isEqual(final Object x, final Object y, final SessionFactoryImplementor factory) throws HibernateException {
+	public boolean isEqual(final Object x, final Object y, final SessionFactoryImplementor factory)
-	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SessionImplementor session) throws HibernateException {
+	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SessionImplementor session)
-	public boolean isModified(final Object old, final Object current, final boolean[] checkable, final SessionImplementor session) throws HibernateException {
+	public boolean isModified(
-    public Object replace(
+	public Object replace(
-    public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
-    public Object assemble(Serializable object, SessionImplementor session, Object owner)
+	public Object assemble(Serializable object, SessionImplementor session, Object owner)
-    public Object hydrate(
+	public Object hydrate(
-    public Object resolve(Object value, SessionImplementor session, Object owner)
+	public Object resolve(Object value, SessionImplementor session, Object owner)
-    public Object semiResolve(Object value, SessionImplementor session, Object owner)
+	public Object semiResolve(Object value, SessionImplementor session, Object owner)
-    public boolean isXMLElement() {
+	public boolean isXMLElement() {
-	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session) throws SQLException {
+	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session)
-	public static CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters, TypeScope scope) {
+	public static CompositeCustomType customComponent(
-    public static CustomType custom(Class<UserType> typeClass, Properties parameters, TypeScope scope) {
+	public static CustomType custom(Class<UserType> typeClass, Properties parameters, TypeScope scope) {
-	public EntityType oneToOne(
-	public EntityType oneToOne(
-	public EntityType specialOneToOne(
-    public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
+	public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
-    public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
+	public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
-    public Predicate in(Object... values) {
+	public Predicate in(Object... values) {
-public abstract class AbstractFromImpl<Z,X>
+public abstract class AbstractFromImpl<Z, X>
-		public void addFetch(Fetch<X,?> fetch);
+		public void addFetch(Fetch<X, ?> fetch);
-	public FromImplementor<Z,X> getCorrelationParent() {
+	public FromImplementor<Z, X> getCorrelationParent() {
-	public <X,Y> Join<X, Y> join(String attributeName) {
+	public <X, Y> Join<X, Y> join(String attributeName) {
-	public <X,Y> Join<X, Y> join(String attributeName, JoinType jt) {
+	public <X, Y> Join<X, Y> join(String attributeName, JoinType jt) {
-	public <X,Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
+	public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
-	public <X,Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
+	public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
-	public <X,Y> SetJoin<X, Y> joinSet(String attributeName) {
+	public <X, Y> SetJoin<X, Y> joinSet(String attributeName) {
-	public <X,Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
+	public <X, Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
-	public <X,Y> ListJoin<X, Y> joinList(String attributeName) {
+	public <X, Y> ListJoin<X, Y> joinList(String attributeName) {
-	public <X,Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
+	public <X, Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
-	public <X,Y> Fetch<X, Y> fetch(String attributeName) {
+	public <X, Y> Fetch<X, Y> fetch(String attributeName) {
-	public <X,Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
+	public <X, Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
-    public PathSource<?> getParentPath() {
+	public PathSource<?> getParentPath() {
-    public boolean performCallback(Object entity) {
+	public boolean performCallback(Object entity) {
-        public boolean doCascade(CascadingAction action) {
-        public String toString() {
-        public boolean areMatch(Object listener, Object original) {
-        public Action getAction() {
+		public boolean doCascade(CascadingAction action) {
+		public String toString() {
+		public boolean areMatch(Object listener, Object original) {
+		public Action getAction() {
-    public static final EntityManagerMessageLogger LOG = HEMLogging.messageLogger( EntityManagerImpl.class.getName() );
+	public static final EntityManagerMessageLogger LOG = messageLogger( EntityManagerImpl.class.getName() );
-    public Session getSession() {
+	public Session getSession() {
-public class QueryImpl<X> extends AbstractQueryImpl<X> implements TypedQuery<X>, HibernateQuery, org.hibernate.ejb.HibernateQuery {
-    public static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class, QueryImpl.class.getName());
+public class QueryImpl<X> extends AbstractQueryImpl<X>
+	public static final EntityManagerMessageLogger LOG = messageLogger( QueryImpl.class );
-    public Class<X> getJavaType() {
+	public Class<X> getJavaType() {
-    public String getTypeName() {
+	public String getTypeName() {
-	public Map<Class<?>,MappedSuperclassType<?>> getMappedSuperclassTypeMap() {
+	public Map<Class<?>, MappedSuperclassType<?>> getMappedSuperclassTypeMap() {
-    public Map<String, EntityTypeImpl<?>> getEntityTypesByEntityName() {
+	public Map<String, EntityTypeImpl<?>> getEntityTypesByEntityName() {
-   	public static MetamodelImpl buildMetamodel(
+	public static MetamodelImpl buildMetamodel(
-//	public boolean shouldAutoJoinTransactions() {
-    public void addComponent(
+	public void addComponent(
-	public EntityConfiguration(String versionsEntityName, String entityClassName, IdMappingData idMappingData,
+	public EntityConfiguration(
-	public void addToOneRelation(String fromPropertyName, String toEntityName, IdMapper idMapper, boolean insertable,
+	public void addToOneRelation(
-	public void addToOneNotOwningRelation(String fromPropertyName, String mappedByPropertyName,
+	public void addToOneNotOwningRelation(
-	public void addToManyNotOwningRelation(String fromPropertyName, String mappedByPropertyName, String toEntityName,
+	public void addToManyNotOwningRelation(
-	public static RelationDescription toOne(String fromPropertyName, RelationType relationType, String toEntityName,
+	public static RelationDescription toOne(
-	public static RelationDescription toMany(String fromPropertyName, RelationType relationType, String toEntityName,
+	public static RelationDescription toMany(
-    public static Object newInstanceOfBeanProxyForMap(String className, Map<String, Object> map, Set<PropertyData> propertyDatas, ClassLoaderService classLoaderService) {
-    public static Class classForName(String className, Map<String,Class<?>> properties, ClassLoaderService classLoaderService) {
-        sb.append("public ").append(getLastComponent(className)).append("(").append(Map.class.getName()).append(" map)").append("{")
-        sb.append("public ").append(fieldClass.getName()).append(" ")
-        sb.append("public void ").append(setterName).append("(")
+	public static Object newInstanceOfBeanProxyForMap(
+	public static Class classForName(
+		sb.append( "public " )
+		sb.append( "public " ).append( fieldClass.getName() ).append( " " )
+		sb.append( "public void " ).append( setterName ).append( "(" )
-    public static boolean isEmpty(String s) {
+	public static boolean isEmpty(String s) {
-    public static boolean isEmpty(Object o) {
+	public static boolean isEmpty(Object o) {
-    public static String getLastComponent(String s) {
+	public static String getLastComponent(String s) {
-    public static void append(StringBuilder sb, Iterator<String> contents, String separator) {
+	public static void append(StringBuilder sb, Iterator<String> contents, String separator) {
-    public static String capitalizeFirst(String fieldName) {
+	public static String capitalizeFirst(String fieldName) {
-    public AuditCriterion ilike(T value) {
+	public AuditCriterion ilike(T value) {
-    public AuditCriterion ilike(String value, MatchMode matchMode) {
+	public AuditCriterion ilike(String value, MatchMode matchMode) {
-   public void invalidateRegion() {
+	public void invalidateRegion() {
-	public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public final boolean putFromLoad(
-		public boolean isReadable(long txTimestamp);
-		public boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);
-		public Object getValue();
-		public boolean isUnlockable(SoftLock lock);
-		public Lock lock(long timeout, UUID uuid, long lockId);
-	public boolean insert(Object key, Object value ) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
-	public boolean afterInsert(Object key, Object value ) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
-	public boolean update(Object key, Object value ) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
-    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
+	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
-	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
+	public CollectionRegion buildCollectionRegion(
-	public boolean insert(Object key, Object value ) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
-	public boolean afterInsert(Object key, Object value ) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
-	public boolean update(Object key, Object value ) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
-	public boolean insert(Object key, Object value ) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
-	public boolean update(Object key, Object value ) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
-	public boolean afterInsert(Object key, Object value ) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
-	public boolean update(Object key, Object value, Object currentVersion,
+	public boolean update(
-    public CallbackException(Method method) {
+	public CallbackException(Method method) {
-    public CallbackException(String message) {
+	public CallbackException(String message) {
-    public CallbackException(Method method, Throwable cause) {
+	public CallbackException(Method method, Throwable cause) {
-    public CallbackException(String message, Throwable cause) {
+	public CallbackException(String message, Throwable cause) {
-			public int compare(FrameworkMethod o1, FrameworkMethod o2) {
+					public int compare(FrameworkMethod o1, FrameworkMethod o2) {
-	public static <S extends Annotation, P extends Annotation> List<S> collectAnnotations(Class<S> singularAnnotationClass,
+	public static <S extends Annotation, P extends Annotation> List<S> collectAnnotations(

Lines added containing method: 202. Lines removed containing method: 221. Tot = 423
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public String getCustomSQLUpdate() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomDeleteCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNotNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Table getTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public Value getDiscriminator() {
-    public Value getDiscriminator() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Type getType() throws MappingException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getReadExpr(Dialect dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isClassOrSuperclassJoin(Join join) {
-		return joins.contains(join);
+		return joins.contains( join );

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	-    public Table getRootTable() {
-    public Table getRootTable() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Iterator getSubclassPropertyClosureIterator() {
-		for ( int i=0; i<subclassJoins.size(); i++ ) {
-			Join join = (Join) subclassJoins.get(i);
+		for ( int i = 0; i < subclassJoins.size(); i++ ) {
+			Join join = (Join) subclassJoins.get( i );
-		return new JoinedIterator(iters);
+		return new JoinedIterator( iters );

Lines added: 3. Lines removed: 3. Tot = 6
—————————
Method found in diff:	public int getJoinNumber(Property prop) {
-		int result=1;
+		int result = 1;
-			if ( join.containsProperty(prop) ) {
+			if ( join.containsProperty( prop ) ) {

Lines added: 2. Lines removed: 2. Tot = 4
—————————
Method found in diff:	-    public boolean isPolymorphic() {
-    public boolean isPolymorphic() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-    public Iterator getPropertyClosureIterator() {
-    public Iterator getPropertyClosureIterator() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isCustomUpdateCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public boolean isDiscriminatorInsertable() {
-    public boolean isDiscriminatorInsertable() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String objectToSQLString(BigInteger value, Dialect dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getColumnSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomInsertCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public Iterator getColumnIterator() {
-    public Iterator getColumnIterator() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getCustomSQLInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCustomSQLDelete() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistentClass getPersistentClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public K getKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCascadeDeleteEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOptional() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAlias(Dialect dialect) {
-		final String suffix = Integer.toString(uniqueInteger) + '_';
+		final String suffix = Integer.toString( uniqueInteger ) + '_';
-				&& !quoted && !name.toLowerCase(Locale.ROOT).equals( "rowid" );
+				&& !quoted && !name.toLowerCase( Locale.ROOT ).equals( "rowid" );
-				throw new MappingException( String.format(
-						"Unique suffix [%s] length must be less than maximum [%d]",
-						suffix, dialect.getMaxAliasLength() ) );
+				throw new MappingException(
+						String.format(
+								"Unique suffix [%s] length must be less than maximum [%d]",
+								suffix, dialect.getMaxAliasLength()
+						)
+				);

Lines added: 8. Lines removed: 5. Tot = 13
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SessionFactoryOptions getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getSubclassSpan() {
-		Iterator iter = subclasses.iterator();
-		while ( iter.hasNext() ) {
-			n += ( (Subclass) iter.next() ).getSubclassSpan();
+		for ( Subclass subclass : subclasses ) {
+			n += subclass.getSubclassSpan();

Lines added: 2. Lines removed: 3. Tot = 5
—————————
Method found in diff:	public BigInteger stringToObject(String string) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public Iterator getSubclassIterator() {
-	public Iterator getSubclassIterator() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isForceDiscriminator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getJoinClosureSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-9803  7308e14f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public ScrollableResults scroll(final QueryParameters queryParameters,
+	public ScrollableResults scroll(
-	/*public static boolean containsDigits(String string) {
-	public static String replace(String template,
+	public static String replace(
-	public static String replace(String beforePlaceholder,
+	public static String replace(
-	public static void addAll(Collection collection, Object[] array) {
+	public static <T> void addAll(Collection<T> collection, T[] array) {
-		public static final AssociationInitCallback NO_CALLBACK = new AssociationInitCallback() {
-		public void associationProcessed(OuterJoinableAssociation oja, int position);
-	public final BatchingLoadQueryDetailsFactory INSTANCE = new BatchingLoadQueryDetailsFactory();
+	public static final BatchingLoadQueryDetailsFactory INSTANCE = new BatchingLoadQueryDetailsFactory();
-	public static LoadQueryDetails makeEntityLoadQueryDetails(
+	public LoadQueryDetails makeEntityLoadQueryDetails(
-	public static LoadQueryDetails makeCollectionLoadQueryDetails(
+	public LoadQueryDetails makeCollectionLoadQueryDetails(
-	public abstract String sqlConstraintString(Dialect d, String constraintName, String defaultCatalog,
+	public abstract String sqlConstraintString(
-	public Set getIdentityTables() {
+	public Set<Table> getIdentityTables() {
-	public String getEntityName();
-	public String getIdentifierPropertyName();
-	public String[] getPropertyNames();
-	public Type getIdentifierType();
-	public Type[] getPropertyTypes();
-	public Type getPropertyType(String propertyName) throws HibernateException;
-	public boolean hasProxy();
-	public boolean isMutable();
-	public boolean isVersioned();
-	public int getVersionProperty();
-	public boolean[] getPropertyNullability();
-	public boolean[] getPropertyLaziness();
-	public boolean hasIdentifierProperty();
-	public boolean hasNaturalIdentifier();
-	public int[] getNaturalIdentifierProperties();
-	public boolean hasSubclasses();
-	public boolean isInherited();
-	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
-	public Class getMappedClass();
-	public Object instantiate(Serializable id, SessionImplementor session);
-	public Object getPropertyValue(Object object, String propertyName) throws HibernateException;
-	public Object[] getPropertyValues(Object entity) throws HibernateException;
-	public void setPropertyValue(Object object, String propertyName, Object value) throws HibernateException;
-	public void setPropertyValues(Object object, Object[] values) throws HibernateException;
-	public Serializable getIdentifier(Object object) throws HibernateException;
-	public Serializable getIdentifier(Object entity, SessionImplementor session);
-	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
-	public boolean implementsLifecycle();
-	public Object getVersion(Object object) throws HibernateException;
-	public static interface CacheEntryHelper {
-		public CacheEntryStructure getCacheEntryStructure();
-		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
+	public interface CacheEntryHelper {
-    public boolean isMappedCollection(CtField field) {
+	public boolean isMappedCollection(CtField field) {
-	public EntityMode getEntityMode();
-	public Object instantiate(Serializable id) throws HibernateException;
-	public Object instantiate(Serializable id, SessionImplementor session);
-	public Serializable getIdentifier(Object entity) throws HibernateException;
-	public Serializable getIdentifier(Object entity, SessionImplementor session);
-	public void setIdentifier(Object entity, Serializable id) throws HibernateException;
-	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion);
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
-	public Object getVersion(Object entity) throws HibernateException;
-	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException;
-	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException;
-	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
-	public Object getPropertyValue(Object entity, String propertyName) throws HibernateException;
-	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
-	public boolean hasProxy();
-	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException;
-	public boolean isLifecycleImplementor();
-	public Class getConcreteProxyClass();
-	public boolean hasUninitializedLazyProperties(Object entity);
-	public boolean isInstrumented();
-	public EntityNameResolver[] getEntityNameResolvers();
-	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory);
-	public Getter getIdentifierGetter();
-	public Getter getVersionGetter();
-public abstract class AbstractBynaryType extends MutableType implements VersionType, Comparator {
-	public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
-	public int sqlType() {
-	public Object seed(SessionImplementor session) {
-	public Object next(Object current, SessionImplementor session) {
-	public Comparator getComparator() {
-	public boolean isEqual(Object x, Object y) {
-	public int getHashCode(Object x) {
-	public int compare(Object x, Object y) {
-	public abstract String getName();
-	public String toString(Object val) {
-	public Object deepCopyNotNull(Object value) {
-	public Object fromStringValue(String xml) throws HibernateException {
-public abstract class AbstractCharArrayType extends MutableType {
-	public Object get(ResultSet rs, String name) throws SQLException {
-	public abstract Class getReturnedClass();
-	public void set(PreparedStatement st, Object value, int index) throws SQLException {
-	public int sqlType() {
-	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
-	public Object stringToObject(String xml) throws Exception {
-	public String toString(Object value) {
-	public Object fromStringValue(String xml) {
-public interface AbstractComponentType extends CompositeType {
-public abstract class AbstractLobType extends AbstractType implements Serializable {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
-	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
-	public Size[] defaultSizes(Mapping mapping) throws MappingException {
-	public boolean isEqual(Object x, Object y) {
-	public int getHashCode(Object x) {
-	public String getName() {
-	public int getColumnSpan(Mapping mapping) throws MappingException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
-	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
-	public void nullSafeSet(
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
-public abstract class AbstractLongBinaryType extends AbstractBynaryType {
-	public Class getReturnedClass() {
-public abstract class AbstractLongStringType extends ImmutableType {
-	public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
-	public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
-	public Class getReturnedClass() {
-	public String toString(Object val) {
-	public Object fromStringValue(String xml) {
-	public String toXMLString(T value, SessionFactoryImplementor factory) throws HibernateException {
-	public T fromXMLString(String xml, Mapping factory) throws HibernateException {
-public class ByteArrayBlobType extends AbstractLobType {
-	public int[] sqlTypes(Mapping mapping) {
-	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
-	public int getHashCode(Object x, SessionFactoryImplementor factory) {
-	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-	public Class getReturnedClass() {
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
-	public String toString(Object val) {
-	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-	public boolean isMutable() {
-	public Object replace(
-	public boolean[] toColumnNullness(Object value, Mapping mapping) {
-public abstract class CharBooleanType extends BooleanType {
-	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
+	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) throws HibernateException {
-	public Object getPropertyValue(Object component, int i, SessionImplementor session)
+	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
-public abstract class ImmutableType extends NullableType {
-	public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
-	public final boolean isMutable() {
-	public Object replace(
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
+	public PersistentCollection instantiate(
-public abstract class MutableType extends NullableType {
-	public final boolean isMutable() {
-	public final Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
-	public Object replace(
-public abstract class NullableType extends AbstractType implements StringRepresentableType, XmlRepresentableType {
-	public abstract int sqlType();
-	public Size dictatedSize() {
-	public Size defaultSize() {
-	public abstract Object get(ResultSet rs, String name) throws HibernateException, SQLException;
-	public abstract void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException;
-	public String nullSafeToString(Object value) throws HibernateException {
-	public abstract String toString(Object value) throws HibernateException;
-	public abstract Object fromStringValue(String xml) throws HibernateException;
-	public final void nullSafeSet(
-	public final void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
-	public final void nullSafeSet(PreparedStatement st, Object value, int index)
-	public final Object nullSafeGet(
-	public final Object nullSafeGet(ResultSet rs, String[] names)
-	public final Object nullSafeGet(ResultSet rs, String name)
-	public final Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
-	public final String toXMLString(Object value, SessionFactoryImplementor pc)
-	public final Object fromXMLString(String xml, Mapping factory) throws HibernateException {
-	public final int getColumnSpan(Mapping session) {
-	public final int[] sqlTypes(Mapping session) {
-	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
-	public Size[] defaultSizes(Mapping mapping) throws MappingException {
-	public boolean isEqual(Object x, Object y) {
-	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory)
-	public boolean[] toColumnNullness(Object value, Mapping mapping) {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
-public class PrimitiveByteArrayBlobType extends ByteArrayBlobType {
-	public Class getReturnedClass() {
-	public int sqlType();
-	public String toString(T value) throws HibernateException;
-	public T fromStringValue(String xml) throws HibernateException;
-	public T nullSafeGet(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException;
-	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException;
-	public void set(PreparedStatement st, T value, int index, SessionImplementor session) throws HibernateException, SQLException;
-public class StringClobType implements UserType, Serializable {
-	public int[] sqlTypes() {
-	public Class returnedClass() {
-	public boolean equals(Object x, Object y) throws HibernateException {
-	public int hashCode(Object x) throws HibernateException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
-	public Object deepCopy(Object value) throws HibernateException {
-	public boolean isMutable() {
-	public Serializable disassemble(Object value) throws HibernateException {
-	public Object assemble(Serializable cached, Object owner) throws HibernateException {
-	public Object replace(Object original, Object target, Object owner) throws HibernateException {
-	public abstract String toString(T value) throws HibernateException;
-	public abstract T fromStringValue(String string) throws HibernateException;
-public interface XmlRepresentableType<T> {
-	public String toXMLString(T value, SessionFactoryImplementor factory) throws HibernateException;
-	public T fromXMLString(String xml, Mapping factory) throws HibernateException;
-	public AuditProjection count(String idPropertyName) {
+					public boolean accept(File pathname) {
+					public boolean accept(File pathname) {
-			public boolean accept(File pathname) {
-			public boolean accept(File pathname) {
-    public boolean isMappedCollection(CtField field) {
+	public boolean isMappedCollection(CtField field) {

Lines added containing method: 19. Lines removed containing method: 200. Tot = 219
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public String getCustomSQLUpdate() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomDeleteCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNotNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {
-			if ( !prop.isLazy() ) return false;
+			if ( !prop.isLazy() ) {
+				return false;
+			}

Lines added: 3. Lines removed: 1. Tot = 4
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Table getTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getDiscriminator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private String getType(String name) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getReadExpr(Dialect dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isClassOrSuperclassJoin(Join join) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Table getRootTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getJoinNumber(Property prop) {
-			if ( join.containsProperty(prop) ) return result;
+			if ( join.containsProperty(prop) ) {
+				return result;
+			}

Lines added: 3. Lines removed: 1. Tot = 4
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomUpdateCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorInsertable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
-	public String objectToSQLString(Object value, Dialect dialect) throws Exception {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isSequentialSelect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getColumnSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomInsertCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCustomSQLInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCustomSQLDelete() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public PersistentClass getPersistentClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public KeyValue getKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCascadeDeleteEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOptional() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasFormula() {
-			if (o instanceof Formula) return true;
+			if (o instanceof Formula) {
+				return true;
+			}

Lines added: 3. Lines removed: 1. Tot = 4
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAlias(Dialect dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getSubclassSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public Object stringToObject(String xml) throws Exception {
-	public Object stringToObject(String xml) throws Exception {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Iterator getSubclassIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isForceDiscriminator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getJoinClosureSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/After/ HHH-9803  bd256e47_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getCustomSQLUpdate
* isCustomDeleteCallable
* getDiscriminatorValue
* isDiscriminatorValueNotNull
* getDefaultCatalogName
* isLazy
* getCustomSQLInsertCheckStyle
* getDefaultSchemaName
* getTable
* getDiscriminator
* getType
* getReadExpr
* isClassOrSuperclassJoin
* getRootTable
* getSubclassPropertyClosureIterator
* getJoinNumber
* isPolymorphic
* getPropertyClosureIterator
* getFormula
* getSynchronizedTables
* to2DStringArray
* isCustomUpdateCallable
* isDiscriminatorInsertable
* toStringArray
* objectToSQLString
* isSequentialSelect
* getColumnSpan
* isCustomInsertCallable
* getColumnIterator
* getCustomSQLInsert
* getDialect
* getName
* getCustomSQLDelete
* getPersistentClass
* getJoinClosureIterator
* determineDefault
* toIntArray
* getKey
* toBooleanArray
* isCascadeDeleteEnabled
* isOptional
* supportsCascadeDelete
* hasFormula
* getCustomSQLUpdateCheckStyle
* isDiscriminatorValueNull
* getAlias
* getSqlFunctionRegistry
* getSubclassJoinClosureIterator
* getSettings
* isFormula
* getSubclassSpan
* stringToObject
* getSubclassIterator
* getEntityName
* isInverse
* getTemplate
* isForceDiscriminator
* getQuotedName
* getCustomSQLDeleteCheckStyle
* getJoinClosureSpan
—————————
Method found in diff:	public String getCustomSQLUpdate() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomDeleteCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNotNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDefaultCatalogName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDefaultSchemaName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTable();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Value getDiscriminator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Type getType();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getReadExpr(Dialect dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isClassOrSuperclassJoin(Join join) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Table getRootTable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getJoinNumber(Property prop) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomUpdateCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorInsertable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String objectToSQLString(BigInteger value, Dialect dialect) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isSequentialSelect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private int getColumnSpan(Type type, SessionFactoryImplementor sfi) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCustomInsertCallable() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getColumnIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCustomSQLInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getCustomSQLDelete() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getPersistentClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static ExecuteUpdateResultCheckStyle determineDefault(String customSql, boolean callable) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getKey() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCascadeDeleteEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOptional() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean supportsCascadeDelete() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean hasFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isDiscriminatorValueNull() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAlias();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassJoinClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Map getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getSubclassSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public BigInteger stringToObject(String string) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getSubclassIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getTemplate() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isForceDiscriminator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getQuotedName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getJoinClosureSpan() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
