35/report.java
Satd-method: public PojoEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
********************************************
********************************************
35/After/ HHH-10267 1e44e742_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		Iterator iter = mappedEntity.getPropertyClosureIterator();
-		Set<String> tmpLazyPropertyNames = new HashSet<String>( );
-		while ( iter.hasNext() ) {
-			Property property = (Property) iter.next();
-			if ( property.isLazy() ) {
-				tmpLazyPropertyNames.add( property.getName() );
-			}
-		}
-		lazyPropertyNames = tmpLazyPropertyNames.isEmpty() ? null : Collections.unmodifiableSet( tmpLazyPropertyNames );
-

Lines added: 0. Lines removed: 10. Tot = 10
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+public @interface LazyGroup {
-	public static final Serializable UNFETCHED_PROPERTY = new Serializable() {
-	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session);
+public class LazyAttributeDescriptor {
+	public static LazyAttributeDescriptor from(
+	public int getAttributeIndex() {
+	public int getLazyIndex() {
+	public String getName() {
+	public Type getType() {
+	public String getFetchGroupName() {
-public class LazyAttributeLoadingInterceptor implements PersistentAttributeInterceptor, Consumer {
+public class LazyAttributeLoadingInterceptor
-	public LazyAttributeLoadingInterceptor(SessionImplementor session, Set<String> lazyFields, String entityName) {
+	public LazyAttributeLoadingInterceptor(
-	public boolean hasAnyUninitializedAttributes() {
-	public void setLoaded(String attributeName) {
-	public String[] getiInitializedFields() {
+	public boolean hasAnyUninitializedAttributes() {
+	public void attributeInitialized(String name) {
+	public Set<String> getInitializedLazyAttributeNames() {
+public class LazyAttributesMetadata implements Serializable {
+	public static LazyAttributesMetadata from(PersistentClass mappedEntity) {
+	public static LazyAttributesMetadata nonEnhanced(String entityName) {
+	public LazyAttributesMetadata(String entityName) {
+	public LazyAttributesMetadata(
+	public String getEntityName() {
+	public boolean hasLazyAttributes() {
+	public int lazyAttributeCount() {
+	public Set<String> getLazyAttributeNames() {
+	public Set<String> getFetchGroupNames() {
+	public boolean isLazyAttribute(String attributeName) {
+	public String getFetchGroupName(String attributeName) {
+	public Set<String> getAttributesInFetchGroup(String fetchGroupName) {
+	public List<LazyAttributeDescriptor> getFetchGroupAttributeDescriptors(String groupName) {
+	public Set<String> getAttributesInSameFetchGroup(String attributeName) {
+public interface LazyFetchGroupMetadata {
-	public boolean isReferenceEntry();
-	public String getSubclass();
-	public Object getVersion();
-	public boolean areLazyPropertiesUnfetched();
-	public Serializable[] getDisassembledState();
-	public boolean areLazyPropertiesUnfetched() {
-	public boolean areLazyPropertiesUnfetched() {
+	public static final String SUBCLASS_KEY = "_subclass";
+	public static final String VERSION_KEY = "_version";
+	public void setLazyGroup(String lazyGroup) {
-	public boolean isLoadedWithLazyPropertiesUnfetched() {
-	public EntityEntry addEntity(
-public interface PersistentAttributeInterceptor {
+public interface PersistentAttributeInterceptor extends InterceptorImplementor {
+	public String getLazyGroup() {
+	public void setLazyGroup(String lazyGroup) {
-	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
+	public void afterInitialize(Object entity, SessionImplementor session) {
-	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
-	public EnhancedGetterMethodImpl(Class containerClass, String propertyName, Method getterMethod) {
+	public EnhancedGetterMethodImpl(
-public class EnhancedSetterMethodImpl implements Setter {
+public class EnhancedSetterImpl implements Setter {
-	public EnhancedSetterMethodImpl(Class containerClass, String propertyName, Method setterMethod) {
+	public EnhancedSetterImpl(Class containerClass, String propertyName, Method setterMethod) {
-	public PojoInstantiator(Component component, ReflectionOptimizer.InstantiationOptimizer optimizer) {
+	public PojoInstantiator(
-	public PojoInstantiator(PersistentClass persistentClass, ReflectionOptimizer.InstantiationOptimizer optimizer) {
+	public PojoInstantiator(Component component, ReflectionOptimizer.InstantiationOptimizer optimizer) {
-	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
-	public boolean hasUninitializedLazyProperties(Object entity) {
+	public void afterInitialize(Object entity, SessionImplementor session) {
-public class BytecodeEnhancementMetadataNonEnhancedPojoImpl implements BytecodeEnhancementMetadata {
-	public BytecodeEnhancementMetadataNonEnhancedPojoImpl(Class entityClass) {
-	public String getEntityName() {
-	public boolean isEnhancedForLazyLoading() {
-	public LazyAttributeLoadingInterceptor injectInterceptor(
-	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
+	public LazyAttributesMetadata getLazyAttributesMetadata() {
+	public boolean hasUnFetchedAttributes(Object entity) {
+	public static BytecodeEnhancementMetadata from(PersistentClass persistentClass) {
-	public BytecodeEnhancementMetadataPojoImpl(Class entityClass) {
+	public BytecodeEnhancementMetadataPojoImpl(
+	public LazyAttributesMetadata getLazyAttributesMetadata() {
+	public boolean hasUnFetchedAttributes(Object entity) {
-	public LazyAttributeLoadingInterceptor injectInterceptor(
+	public LazyAttributeLoadingInterceptor injectInterceptor(Object entity, SessionImplementor session) {
-	public boolean isInstrumented() {
+public class PojoEntityInstantiator extends PojoInstantiator {
+	public PojoEntityInstantiator(
+	public boolean isInstance(Object object) {
-	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
+	public void afterInitialize(Object entity, SessionImplementor session) {
-	public boolean hasUninitializedLazyProperties(Object entity) {
-	public boolean isInstrumented() {
-		public DynamicEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
+	public DynamicEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
+	public void testLazyGroups() {
+	public Set<String> getInitializedLazyAttributeNames() {
+	public void attributeInitialized(String name) {
+	public Child() {
+	public Child(String name) {
+public class Child {
+	public Child() {
+	public Child(String name, String nickName) {
+	public Long getId() {
+	public void setId(Long id) {
+	public String getName() {
+	public void setName(String name) {
+	public String getNickName() {
+	public void setNickName(String nickName) {
+	public Parent getParent() {
+	public void setParent(Parent parent) {
+	public Parent getAlternateParent() {
+	public void setAlternateParent(Parent alternateParent) {
+public class LazyGroupAccessTestTask extends AbstractEnhancerTestTask {
+	public Class<?>[] getAnnotatedClasses() {
+	public void prepare() {
+	public void execute() {
+public class Parent {
+	public Parent() {
+	public Parent(String nombre) {
+	public Long getId() {
+	public void setId(Long id) {
+	public String getNombre() {
+	public void setNombre(String nombre) {
+	public List<Child> getChildren() {
+	public void setChildren(List<Child> children) {
+	public List<Child> getAlternateChildren() {
+	public void setAlternateChildren(List<Child> alternateChildren) {
-		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
+		public void afterInitialize(Object entity, SessionImplementor session) {
-	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
+	public void afterInitialize(Object entity, SessionImplementor session) {
-		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
+		public void afterInitialize(Object entity, SessionImplementor session) {
+public class BaseRegion implements Region {
+	public Map getDataMap() {

Lines added containing method: 92. Lines removed containing method: 42. Tot = 134
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	+	public String getName() {
+	public String getName() {
+		return name;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public String getMethodName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getReturnType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-10280 472f4ab9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		this.isInstrumented = entityMetamodel.isInstrumented();
+		this.isBytecodeEnhanced = entityMetamodel.getBytecodeEnhancementMetadata().isEnhancedForLazyLoading();

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-public class JavassistInstrumenter extends AbstractInstrumenter {
-	public JavassistInstrumenter(Logger logger, Options options) {
-		public CustomClassDescriptor(byte[] bytes) throws IOException {
-		public String getName() {
-		public boolean isInstrumented() {
-		public byte[] getBytes() {
-public abstract class AbstractInstrumenter implements Instrumenter {
-	public AbstractInstrumenter(Logger logger, Options options) {
-	public void execute(Set<File> files) {
-				public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
-					public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception {
-		public CustomFieldFilter(ClassDescriptor descriptor, Set classNames) {
-		public boolean shouldInstrumentField(String className, String fieldName) {
-		public boolean shouldTransformFieldAccess(
-		public void handleEntry(ZipEntry entry, byte[] byteCode) throws Exception;
-		public ZipFileProcessor(ZipEntryHandler entryHandler) {
-		public void process(File file) throws Exception {
-public class BasicClassFilter implements ClassFilter {
-	public BasicClassFilter() {
-	public BasicClassFilter(String[] includedPackages, String[] includedClassNames) {
-	public boolean shouldInstrumentClass(String className) {
-public interface ClassDescriptor {
-	public String getName();
-	public boolean isInstrumented();
-	public byte[] getBytes();
-public interface ClassFilter {
-	public boolean shouldInstrumentClass(String className);
-public class ExecutionException extends RuntimeException {
-	public ExecutionException(String message) {
-	public ExecutionException(Throwable cause) {
-	public ExecutionException(String message, Throwable cause) {
-public interface FieldFilter {
-	public boolean shouldInstrumentField(String className, String fieldName);
-	public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName);
-public interface Instrumenter {
-	public void execute(Set<File> files);
-	public static interface Options {
-		public boolean performExtendedInstrumentation();
-public interface Logger {
-	public void trace(String message);
-	public void debug(String message);
-	public void info(String message);
-	public void warn(String message);
-	public void error(String message);
-public class LazyAttributeLoader implements PersistentAttributeInterceptor, Consumer {
+public class LazyAttributeLoadingInterceptor implements PersistentAttributeInterceptor, Consumer {
-	public LazyAttributeLoader(SessionImplementor session, Set<String> lazyFields, String entityName) {
+	public LazyAttributeLoadingInterceptor(SessionImplementor session, Set<String> lazyFields, String entityName) {
-	public boolean isUninitialized() {
+	public boolean hasAnyUninitializedAttributes() {
-public class FieldInterceptionHelper {
-	public static boolean isInstrumented(Class entityClass) {
-	public static boolean isInstrumented(Object object) {
-	public static FieldInterceptor extractFieldInterceptor(Object object) {
-	public static FieldInterceptor injectFieldInterceptor(
-		public boolean isInstrumented(Class classToCheck);
-		public FieldInterceptor extractInterceptor(Object entity);
-		public FieldInterceptor injectInterceptor(Object entity, String entityName, Set uninitializedFieldNames, SessionImplementor session);
-		public static final JavassistDelegate INSTANCE = new JavassistDelegate();
-		public static final String MARKER = "org.hibernate.bytecode.internal.javassist.FieldHandled";
-		public boolean isInstrumented(Class classToCheck) {
-		public FieldInterceptor extractInterceptor(Object entity) {
-		public FieldInterceptor injectInterceptor(
-	public boolean readBoolean(Object target, String name, boolean oldValue) {
-	public byte readByte(Object target, String name, byte oldValue) {
-	public char readChar(Object target, String name, char oldValue) {
-	public double readDouble(Object target, String name, double oldValue) {
-	public float readFloat(Object target, String name, float oldValue) {
-	public int readInt(Object target, String name, int oldValue) {
-	public long readLong(Object target, String name, long oldValue) {
-	public short readShort(Object target, String name, short oldValue) {
-	public Object readObject(Object target, String name, Object oldValue) {
-	public boolean writeBoolean(Object target, String name, boolean oldValue, boolean newValue) {
-	public byte writeByte(Object target, String name, byte oldValue, byte newValue) {
-	public char writeChar(Object target, String name, char oldValue, char newValue) {
-	public double writeDouble(Object target, String name, double oldValue, double newValue) {
-	public float writeFloat(Object target, String name, float oldValue, float newValue) {
-	public int writeInt(Object target, String name, int oldValue, int newValue) {
-	public long writeLong(Object target, String name, long oldValue, long newValue) {
-	public short writeShort(Object target, String name, short oldValue, short newValue) {
-	public Object writeObject(Object target, String name, Object oldValue, Object newValue) {
-	public String toString() {
-public class JavassistHelper {
-	public static FieldInterceptor extractFieldInterceptor(Object entity) {
-	public static FieldInterceptor injectFieldInterceptor(
-public abstract class AbstractFieldInterceptor implements FieldInterceptor, Serializable {
-	public final void setSession(SessionImplementor session) {
-	public final boolean isInitialized() {
-	public final boolean isInitialized(String field) {
-	public final void dirty() {
-	public final boolean isDirty() {
-	public final void clearDirty() {
-	public final SessionImplementor getSession() {
-	public final Set getUninitializedFields() {
-	public final String getEntityName() {
-	public final boolean isInitializing() {
-public interface FieldInterceptor {
-	public void setSession(SessionImplementor session);
-	public boolean isInitialized();
-	public boolean isInitialized(String field);
-	public void dirty();
-	public boolean isDirty();
-	public void clearDirty();
-	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter) {
-	public EntityInstrumentationMetadata getEntityInstrumentationMetadata(Class entityClass) {
-		public String getEntityName() {
-		public boolean isInstrumented() {
-		public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
-		public FieldInterceptor injectInterceptor(
-public interface FieldFilter {
-public interface FieldHandled {
-	public void setFieldHandler(FieldHandler handler);
-	public FieldHandler getFieldHandler();
-public interface FieldHandler {
-public class FieldTransformer {
-	public void transform(File file) throws Exception {
-	public void transform(ClassFile classFile) throws Exception {
-public class JavassistClassTransformer extends AbstractClassTransformerImpl {
-	public JavassistClassTransformer(ClassFilter classFilter, org.hibernate.bytecode.buildtime.spi.FieldFilter fieldFilter) {
-					public boolean handleRead(String desc, String name) {
-					public boolean handleWrite(String desc, String name) {
-					public boolean handleReadAccess(String fieldOwnerClassName, String fieldName) {
-					public boolean handleWriteAccess(String fieldOwnerClassName, String fieldName) {
-public class TransformingClassLoader extends ClassLoader {
-	public void release() {
-public abstract class AbstractClassTransformerImpl implements ClassTransformer {
-	public byte[] transform(
+public interface BytecodeEnhancementMetadata {
-	public ProxyFactoryFactory getProxyFactoryFactory();
-	public ReflectionOptimizer getReflectionOptimizer(Class clazz, String[] getterNames, String[] setterNames, Class[] types);
-	public ClassTransformer getTransformer(ClassFilter classFilter, FieldFilter fieldFilter);
-	public EntityInstrumentationMetadata getEntityInstrumentationMetadata(Class entityClass);
-public interface ClassTransformer {
+public interface ClassTransformer extends javax.persistence.spi.ClassTransformer {
-public interface EntityInstrumentationMetadata {
-	public String getEntityName();
-	public boolean isInstrumented();
-	public FieldInterceptor injectInterceptor(
-	public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException;
-	public EntityInstrumentationMetadata getInstrumentationMetadata() {
+	public BytecodeEnhancementMetadata getInstrumentationMetadata() {
-	public EntityInstrumentationMetadata getInstrumentationMetadata();
+	public BytecodeEnhancementMetadata getInstrumentationMetadata();
-public abstract class BasicInstrumentationTask extends Task implements Instrumenter.Options {
-	public void addFileset(FileSet set) {
-	public boolean isExtended() {
-	public void setExtended(boolean extended) {
-	public boolean isVerbose() {
-	public void setVerbose(boolean verbose) {
-	public final boolean performExtendedInstrumentation() {
-	public void execute() throws BuildException {
-		public void trace(String message) {
-		public void debug(String message) {
-		public void info(String message) {
-		public void warn(String message) {
-		public void error(String message) {
-public class InstrumentTask extends BasicInstrumentationTask {
+public class BytecodeEnhancementMetadataNonEnhancedPojoImpl implements BytecodeEnhancementMetadata {
+	public BytecodeEnhancementMetadataNonEnhancedPojoImpl(Class entityClass) {
+	public String getEntityName() {
+	public boolean isEnhancedForLazyLoading() {
+	public LazyAttributeLoadingInterceptor injectInterceptor(
+	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
-public class NonPojoInstrumentationMetadata implements EntityInstrumentationMetadata {
+public class BytecodeEnhancementMetadataNonPojoImpl implements BytecodeEnhancementMetadata {
-	public NonPojoInstrumentationMetadata(String entityName) {
+	public BytecodeEnhancementMetadataNonPojoImpl(String entityName) {
-	public boolean isInstrumented() {
+	public boolean isEnhancedForLazyLoading() {
-	public FieldInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
+	public LazyAttributeLoadingInterceptor injectInterceptor(
-	public FieldInterceptor injectInterceptor(
+	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
+public class BytecodeEnhancementMetadataPojoImpl implements BytecodeEnhancementMetadata {
+	public BytecodeEnhancementMetadataPojoImpl(Class entityClass) {
+	public String getEntityName() {
+	public boolean isEnhancedForLazyLoading() {
+	public LazyAttributeLoadingInterceptor extractInterceptor(Object entity) throws NotInstrumentedException {
+	public LazyAttributeLoadingInterceptor injectInterceptor(
-	public EntityInstrumentationMetadata getInstrumentationMetadata() {
-	public boolean isLazyLoadingBytecodeEnhanced() {
+	public BytecodeEnhancementMetadata getBytecodeEnhancementMetadata() {
-public abstract class DecompileUtils {
-	public static void decompileDumpedClass(String workingDir, String className) {
-		public EntityInstrumentationMetadata getInstrumentationMetadata() {
+		public BytecodeEnhancementMetadata getInstrumentationMetadata() {
-public class InstrumentTest extends BaseUnitTestCase {
-	public void testDirtyCheck() throws Exception {
-	public void testFetchAll() throws Exception {
-	public void testLazy() throws Exception {
-	public void testLazyManyToOne() throws Exception {
-	public void testSetFieldInterceptor() throws Exception {
-	public void testPropertyInitialized() throws Exception {
-	public void testManyToOneProxy() throws Exception {
-	public void testLazyPropertyCustomTypeExecutable() throws Exception {
-	public void testLazyBasicFieldAccess() throws Exception {
-	public void testLazyBasicPropertyAccess() throws Exception {
-	public void testSharedPKOneToOne() throws Exception {
-	public void testCustomColumnReadAndWrite() throws Exception {
-	public static class SkipCheck implements Skip.Matcher {
-		public boolean isMatch() {
-public abstract class AbstractExecutable implements Executable {
-	public final void prepare() {
-	public final void complete() {
-public interface Executable {
-	public void prepare();
-	public void execute() throws Exception;
-	public void complete();
-public class TestCustomColumnReadAndWrite extends AbstractExecutable {
-	public void execute() {
-public class TestDirtyCheckExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestFetchAllExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestFetchingLazyToOneExecutable implements Executable {
-	public void execute() throws Exception {
-	public final void prepare() {
-	public final void complete() {
-public class TestInjectFieldInterceptorExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestIsPropertyInitializedExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestLazyBasicFieldAccessExecutable extends AbstractExecutable {
-	public void execute() throws Exception {
-public class TestLazyBasicPropertyAccessExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestLazyExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestLazyManyToOneExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestLazyPropertyCustomTypeExecutable extends AbstractExecutable {
-	public void execute() throws Exception {
-public class TestManyToOneProxyExecutable extends AbstractExecutable {
-	public void execute() {
-public class TestSharedPKOneToOneExecutable extends AbstractExecutable {
-	public void execute() {
-public class CustomBlobType implements UserType {
-	public Object nullSafeGet(ResultSet rs, String names[], SessionImplementor session, Object owner) throws SQLException {
-	public void nullSafeSet(PreparedStatement ps, Object value, int index, SessionImplementor session) throws SQLException, HibernateException {
-	public Object deepCopy(Object value) {
-	public boolean isMutable() {
-	public int[] sqlTypes() {
-	public Class returnedClass() {
-	public boolean equals(Object x, Object y) {
-	public Object assemble(Serializable arg0, Object arg1)
-	public Serializable disassemble(Object arg0)
-	public int hashCode(Object arg0)
-	public Object replace(Object arg0, Object arg1, Object arg2)
-public class Document {
-	public Folder getFolder() {
-	public void setFolder(Folder folder) {
-	public Owner getOwner() {
-	public void setOwner(Owner owner) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public String getSummary() {
-	public void setSummary(String summary) {
-	public String getText() {
-	public String getUpperCaseName() {
-	public void setUpperCaseName(String upperCaseName) {
-	public void setSizeKb(double sizeKb) {
-	public double getSizeKb() {
-	public void updateText(String newText) {
-public class Entity {
-	public Entity() {
-	public Entity(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public Entity getChild() {
-	public void setChild(Entity child) {
-	public Entity getSibling() {
-	public void setSibling(Entity sibling) {
-public class EntityWithOneToOnes {
-	public EntityWithOneToOnes() {
-	public EntityWithOneToOnes(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public OneToOneNoProxy getOneToOneNoProxy() {
-	public void setOneToOneNoProxy(OneToOneNoProxy oneToOneNoProxy) {
-	public OneToOneProxy getOneToOneProxy() {
-	public void setOneToOneProxy(OneToOneProxy oneToOneProxy) {
-public class Folder {
-	public boolean nameWasread;
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public Collection getDocuments() {
-	public void setDocuments(Collection documents) {
-	public Folder getParent() {
-	public void setParent(Folder parent) {
-	public Collection getSubfolders() {
-	public void setSubfolders(Collection subfolders) {
-public class OneToOneNoProxy {
-	public OneToOneNoProxy() {}
-	public OneToOneNoProxy(String name) {
-	public Long getEntityId() {
-	public void setEntityId(Long entityId) {
-	public String getName() {
-	public void setName(String name) {
-	public EntityWithOneToOnes getEntity() {
-	public void setEntity(EntityWithOneToOnes entity) {
-public class OneToOneProxy {
-	public OneToOneProxy() {}
-	public OneToOneProxy(String name) {
-	public Long getEntityId() {
-	public void setEntityId(Long entityId) {
-	public String getName() {
-	public void setName(String name) {
-	public EntityWithOneToOnes getEntity() {
-	public void setEntity(EntityWithOneToOnes entity) {
-public class Owner {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-public class Passport {
-	public Passport() {
-	public Passport(Person person, String number, String issuingCountry) {
-	public Integer getId() {
-	public void setId(Integer id) {
-	public Person getPerson() {
-	public void setPerson(Person person) {
-	public String getNumber() {
-	public void setNumber(String number) {
-	public String getIssuingCountry() {
-	public void setIssuingCountry(String issuingCountry) {
-	public Date getIssueDate() {
-	public void setIssueDate(Date issueDate) {
-	public Date getExpirationDate() {
-	public void setExpirationDate(Date expirationDate) {
-public class Person {
-	public Person() {
-	public Person(String name) {
-	public Integer getId() {
-	public void setId(Integer id) {
-	public String getName() {
-	public void setName(String name) {
-	public Passport getPassport() {
-	public void setPassport(Passport passport) {
-public class Problematic {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public byte[] getBytes() {
-	public void setBytes(byte[] bytes) {
-	public Representation getRepresentation() {
-	public void setRepresentation(Representation rep) {
-	public static class Representation {
-		public Representation(byte[] bytes) {
-		public byte[] getBytes() {
-		public String toString() {
-public abstract class AbstractTransformingClassLoaderInstrumentTestCase extends BaseUnitTestCase {
-	public ClassLoadingIsolater isolater = new ClassLoadingIsolater(
-				public ClassLoader buildIsolatedClassLoader() {
-										public boolean shouldInstrumentField(String className, String fieldName) {
-										public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName) {
-				public void releaseIsolatedClassLoader(ClassLoader isolatedClassLoader) {
-	public void testSetFieldInterceptor() {
-	public void testDirtyCheck() {
-	public void testEagerFetchLazyToOne() {
-	public void testFetchAll() throws Exception {
-	public void testLazy() {
-	public void testLazyManyToOne() {
-	public void testPropertyInitialized() {
-	public void testManyToOneProxy() {
-	public void testLazyPropertyCustomType() {
-	public void testLazyBasicFieldAccess() {
-	public void testLazyBasicPropertyAccess() {
-	public void testSharedPKOneToOne() {
-	public void testCustomColumnReadAndWrite() {
-	public void executeExecutable(String name) {
-public class JavassistInstrumentationTest extends AbstractTransformingClassLoaderInstrumentTestCase {
-	public EntityInstrumentationMetadata getInstrumentationMetadata() {
+	public BytecodeEnhancementMetadata getInstrumentationMetadata() {
-public class InterceptFieldClassFileTransformer implements javax.persistence.spi.ClassTransformer {
-	public InterceptFieldClassFileTransformer(Collection<String> entities) {
-					public boolean shouldInstrumentClass(String className) {
-					public boolean shouldInstrumentField(String className, String fieldName) {
-					public boolean shouldTransformFieldAccess(
-	public byte[] transform(
-		public EntityInstrumentationMetadata getInstrumentationMetadata() {
+		public BytecodeEnhancementMetadata getInstrumentationMetadata() {
-public abstract class AbstractExecutable implements Executable {
+public abstract class AbstractExecutable implements EnhancerTestTask {
-	public void execute() throws Exception {
+	public void execute() {
+public class JpaRuntimeEnhancementTest extends BaseUnitTestCase {
+//	public ClassLoadingIsolater isolater = new ClassLoadingIsolater(
+//				public ClassLoader buildIsolatedClassLoader() {
+//						public boolean doFieldAccessEnhancement(CtClass classDescriptor) {
+//								public byte[] transform(
+//											public synchronized Throwable getCause() {
+//				public void releaseIsolatedClassLoader(ClassLoader isolatedClassLoader) {
+	public void LazyPropertyOnPreUpdate() throws Exception {
+//	public void executeExecutable(String name) {
-public interface Executable {
-	public void prepare();
-	public void execute() throws Exception;
-	public void complete();
-	public Class[] getAnnotatedClasses();
-public abstract class AbstractTransformingClassLoaderInstrumentTestCase extends BaseUnitTestCase {
-	public ClassLoadingIsolater isolater = new ClassLoadingIsolater(
-				public ClassLoader buildIsolatedClassLoader() {
-										public boolean shouldInstrumentField(String className, String fieldName) {
-										public boolean shouldTransformFieldAccess(String transformingClassName, String fieldOwnerClassName, String fieldName) {
-				public void releaseIsolatedClassLoader(ClassLoader isolatedClassLoader) {
-	public void LazyPropertyOnPreUpdate() throws Exception {
-	public void executeExecutable(String name) {
-public class JavassistInstrumentationTest extends AbstractTransformingClassLoaderInstrumentTestCase {
+public abstract class DecompileUtils {
+	public static void decompileDumpedClass(String workingDir, String className) {

Lines added containing method: 41. Lines removed containing method: 378. Tot = 419
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	-		public String getName() {
-		public String getName() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String getMethodName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ReflectionOptimizer getReflectionOptimizer(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getReturnType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-10664 87e3f0fd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public FlushMode getFlushMode();
-	public BasicQueryContract setFlushMode(FlushMode flushMode);
-	public CacheMode getCacheMode();
-	public BasicQueryContract setCacheMode(CacheMode cacheMode);
-	public boolean isCacheable();
-	public BasicQueryContract setCacheable(boolean cacheable);
-	public String getCacheRegion();
-	public BasicQueryContract setCacheRegion(String cacheRegion);
-	public Integer getTimeout();
-	public BasicQueryContract setTimeout(int timeout);
-	public Integer getFetchSize();
-	public BasicQueryContract setFetchSize(int fetchSize);
-	public boolean isReadOnly();
-	public BasicQueryContract setReadOnly(boolean readOnly);
-	public Type[] getReturnTypes();
-public interface Cache {
+public interface Cache extends javax.persistence.Cache {
-	public boolean containsEntity(Class entityClass, Serializable identifier);
-	public boolean containsEntity(String entityName, Serializable identifier);
-	public void evictEntity(Class entityClass, Serializable identifier);
-	public void evictEntity(String entityName, Serializable identifier);
-	public void evictEntityRegion(Class entityClass);
-	public void evictEntityRegion(String entityName);
-	public void evictEntityRegions();
-	public void evictNaturalIdRegion(Class naturalIdClass);
-	public void evictNaturalIdRegion(String naturalIdName);
-	public void evictNaturalIdRegions();
-	public boolean containsCollection(String role, Serializable ownerIdentifier);
-	public void evictCollection(String role, Serializable ownerIdentifier);
-	public void evictCollectionRegion(String role);
-	public void evictCollectionRegions();
-	public boolean containsQuery(String regionName);
-	public void evictDefaultQueryRegion();
-	public void evictQueryRegion(String regionName);
-	public void evictQueryRegions();
-	public void evictAllRegions();
+	public static LobCreator getLobCreator(SharedSessionContractImplementor session) {
-public interface Query extends BasicQueryContract {
+public interface Query<R> extends org.hibernate.BasicQueryContract, TypedQuery<R> {
-	public String getQueryString();
-	public Integer getMaxResults();
-	public Query setMaxResults(int maxResults);
-	public Integer getFirstResult();
-	public Query setFirstResult(int firstResult);
-	public Query setFlushMode(FlushMode flushMode);
-	public Query setCacheMode(CacheMode cacheMode);
-	public Query setCacheable(boolean cacheable);
-	public Query setCacheRegion(String cacheRegion);
-	public Query setTimeout(int timeout);
-	public Query setFetchSize(int fetchSize);
-	public Query setReadOnly(boolean readOnly);
-	public LockOptions getLockOptions();
-	public Query setLockOptions(LockOptions lockOptions);
-	public Query setLockMode(String alias, LockMode lockMode);
-	public String getComment();
-	public Query setComment(String comment);
-	public Query addQueryHint(String hint);
-	public String[] getReturnAliases();
-	public String[] getNamedParameters();
-	public Iterator iterate();
-	public ScrollableResults scroll();
-	public ScrollableResults scroll(ScrollMode scrollMode);
-	public List list();
-	public Object uniqueResult();
-	public int executeUpdate();
-	public Query setParameter(int position, Object val, Type type);
-	public Query setParameter(String name, Object val, Type type);
-	public Query setParameter(int position, Object val);
-	public Query setParameter(String name, Object val);
-	public Query setParameters(Object[] values, Type[] types);
-	public Query setParameterList(String name, Collection values, Type type);
-	public Query setParameterList(String name, Collection values);
-	public Query setParameterList(String name, Object[] values, Type type);
-	public Query setParameterList(String name, Object[] values);
-	public Query setProperties(Object bean);
-	public Query setProperties(Map bean);
-	public Query setString(int position, String val);
-	public Query setCharacter(int position, char val);
-	public Query setBoolean(int position, boolean val);
-	public Query setByte(int position, byte val);
-	public Query setShort(int position, short val);
-	public Query setInteger(int position, int val);
-	public Query setLong(int position, long val);
-	public Query setFloat(int position, float val);
-	public Query setDouble(int position, double val);
-	public Query setBinary(int position, byte[] val);
-	public Query setText(int position, String val);
-	public Query setSerializable(int position, Serializable val);
-	public Query setLocale(int position, Locale locale);
-	public Query setBigDecimal(int position, BigDecimal number);
-	public Query setBigInteger(int position, BigInteger number);
-	public Query setDate(int position, Date date);
-	public Query setTime(int position, Date date);
-	public Query setTimestamp(int position, Date date);
-	public Query setCalendar(int position, Calendar calendar);
-	public Query setCalendarDate(int position, Calendar calendar);
-	public Query setString(String name, String val);
-	public Query setCharacter(String name, char val);
-	public Query setBoolean(String name, boolean val);
-	public Query setByte(String name, byte val);
-	public Query setShort(String name, short val);
-	public Query setInteger(String name, int val);
-	public Query setLong(String name, long val);
-	public Query setFloat(String name, float val);
-	public Query setDouble(String name, double val);
-	public Query setBinary(String name, byte[] val);
-	public Query setText(String name, String val);
-	public Query setSerializable(String name, Serializable val);
-	public Query setLocale(String name, Locale locale);
-	public Query setBigDecimal(String name, BigDecimal number);
-	public Query setBigInteger(String name, BigInteger number);
-	public Query setDate(String name, Date date);
-	public Query setTime(String name, Date date);
-	public Query setTimestamp(String name, Date date);
-	public Query setCalendar(String name, Calendar calendar);
-	public Query setCalendarDate(String name, Calendar calendar);
-	public Query setEntity(int position, Object val);
-	public Query setEntity(String name, Object val);
-	public Query setResultTransformer(ResultTransformer transformer);
-public interface SQLQuery extends Query, SynchronizeableQuery {
+public interface SQLQuery extends Query<Object>, SynchronizeableQuery {
-	public SQLQuery setResultSetMapping(String name);
-	public boolean isCallable();
-	public List<NativeSQLQueryReturn> getQueryReturns();
-	public SQLQuery addScalar(String columnAlias);
-	public SQLQuery addScalar(String columnAlias, Type type);
-	public RootReturn addRoot(String tableAlias, String entityName);
-	public RootReturn addRoot(String tableAlias, Class entityType);
-	public SQLQuery addEntity(String entityName);
-	public SQLQuery addEntity(String tableAlias, String entityName);
-	public SQLQuery addEntity(String tableAlias, String entityName, LockMode lockMode);
-	public SQLQuery addEntity(Class entityType);
-	public SQLQuery addEntity(String tableAlias, Class entityType);
-	public SQLQuery addEntity(String tableAlias, Class entityName, LockMode lockMode);
-	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName);
-	public SQLQuery addJoin(String tableAlias, String path);
-	public SQLQuery addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName);
-	public SQLQuery addJoin(String tableAlias, String path, LockMode lockMode);
-	public static interface ReturnProperty {
-		public ReturnProperty addColumnAlias(String columnAlias);
-	public static interface RootReturn {
-		public RootReturn setLockMode(LockMode lockMode);
-		public RootReturn setDiscriminatorAlias(String columnAlias);
-		public RootReturn addProperty(String propertyName, String columnAlias);
-		public ReturnProperty addProperty(String propertyName);
-	public static interface FetchReturn {
-		public FetchReturn setLockMode(LockMode lockMode);
-		public FetchReturn addProperty(String propertyName, String columnAlias);
-		public ReturnProperty addProperty(String propertyName);
-public interface Session extends SharedSessionContract, java.io.Closeable {
+public interface Session extends SharedSessionContract, EntityManager, HibernateEntityManager, java.io.Closeable {
-public interface SessionBuilder {
+public interface SessionBuilder<T extends SessionBuilder> {
-	public Session openSession();
-	public SessionBuilder interceptor(Interceptor interceptor);
-	public SessionBuilder noInterceptor();
-	public SessionBuilder statementInspector(StatementInspector statementInspector);
-	public SessionBuilder connection(Connection connection);
-	public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode);
-	public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions);
-	public SessionBuilder autoClose(boolean autoClose);
-	public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion);
-	public SessionBuilder tenantIdentifier(String tenantIdentifier);
-	public SessionBuilder eventListeners(SessionEventListener... listeners);
-	public SessionBuilder clearEventListeners();
-public interface SessionFactory extends Referenceable, Serializable, java.io.Closeable {
+public interface SessionFactory extends EntityManagerFactory, HibernateEntityManagerFactory, Referenceable, Serializable, java.io.Closeable {
-public interface SharedSessionBuilder extends SessionBuilder {
+public interface SharedSessionBuilder<T extends SharedSessionBuilder> extends SessionBuilder<T> {
-	public SharedSessionBuilder interceptor();
-	public SharedSessionBuilder connection();
-	public SharedSessionBuilder connectionReleaseMode();
-	public SharedSessionBuilder autoJoinTransactions();
-	public SharedSessionBuilder autoClose();
-	public SharedSessionBuilder flushBeforeCompletion();
-	public SharedSessionBuilder transactionContext();
-public interface SharedSessionContract extends Serializable {
+public interface SharedSessionContract extends QueryProducer, Serializable {
-	public String getTenantIdentifier();
-	public Transaction beginTransaction();
-	public Transaction getTransaction();
-	public Query getNamedQuery(String queryName);
-	public Query createQuery(String queryString);
-	public SQLQuery createSQLQuery(String queryString);
-	public ProcedureCall getNamedProcedureCall(String name);
-	public ProcedureCall createStoredProcedureCall(String procedureName);
-	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses);
-	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings);
-	public Criteria createCriteria(Class persistentClass);
-	public Criteria createCriteria(Class persistentClass, String alias);
-	public Criteria createCriteria(String entityName);
-	public Criteria createCriteria(String entityName, String alias);
-public interface StatelessSessionBuilder {
+public interface StatelessSessionBuilder<T extends StatelessSessionBuilder> {
-	public StatelessSession openStatelessSession();
-	public StatelessSessionBuilder connection(Connection connection);
-	public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier);
-	public Collection<String> getSynchronizedQuerySpaces();
-	public SynchronizeableQuery addSynchronizedQuerySpace(String querySpace);
-	public SynchronizeableQuery addSynchronizedEntityName(String entityName) throws MappingException;
-	public SynchronizeableQuery addSynchronizedEntityClass(Class entityClass) throws MappingException;
-public interface Transaction {
+public interface Transaction extends EntityTransaction {
-	public void afterDeserialize(SessionImplementor session) {
+	public void afterDeserialize(SharedSessionContractImplementor session) {
-	public BulkOperationCleanupAction(SessionImplementor session, Queryable... affectedQueryables) {
+	public BulkOperationCleanupAction(SharedSessionContractImplementor session, Queryable... affectedQueryables) {
-	public BulkOperationCleanupAction(SessionImplementor session, Set tableSpaces) {
+	public BulkOperationCleanupAction(SharedSessionContractImplementor session, Set tableSpaces) {
-			public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+			public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
-	public void afterDeserialize(SessionImplementor session) {
+	public void afterDeserialize(SharedSessionContractImplementor session) {
-	public void afterDeserialize(SessionImplementor session) {
+	public void afterDeserialize(SharedSessionContractImplementor session) {
-		public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+		public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
-	public final SessionImplementor getSession() {
+	public final SharedSessionContractImplementor getSession() {
-	public void afterDeserialize(SessionImplementor session) {
+	public void afterDeserialize(SharedSessionContractImplementor session) {
-	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
+	public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) throws HibernateException {
-	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+	public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
-	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
+	public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) throws HibernateException {
-	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws CacheException {
+	public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) throws CacheException {
-	public void doAfterTransactionCompletion(boolean success, SessionImplementor session);
-	public Serializable[] getPropertySpaces();
-	public void beforeExecutions() throws HibernateException;
-	public void execute() throws HibernateException;
-	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess();
-	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess();
-	public void afterDeserialize(SessionImplementor session);
-	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory);
-	public SessionFactoryBuilder applyBeanManager(Object beanManager);
-	public SessionFactoryBuilder applyName(String sessionFactoryName);
-	public SessionFactoryBuilder applyNameAsJndiName(boolean isJndiName);
-	public SessionFactoryBuilder applyAutoClosing(boolean enabled);
-	public SessionFactoryBuilder applyAutoFlushing(boolean enabled);
-	public SessionFactoryBuilder applyStatisticsSupport(boolean enabled);
-	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor);
-	public SessionFactoryBuilder applyStatementInspector(StatementInspector statementInspector);
-	public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver... observers);
-	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy);
-	public SessionFactoryBuilder addEntityNameResolver(EntityNameResolver... entityNameResolvers);
-	public SessionFactoryBuilder applyEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate);
-	public SessionFactoryBuilder applyIdentifierRollbackSupport(boolean enabled);
-	public SessionFactoryBuilder applyDefaultEntityMode(EntityMode entityMode);
-	public SessionFactoryBuilder applyNullabilityChecking(boolean enabled);
-	public SessionFactoryBuilder applyLazyInitializationOutsideTransaction(boolean enabled);
-	public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory);
-	public SessionFactoryBuilder applyEntityTuplizer(
-	public SessionFactoryBuilder applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy);
-	public SessionFactoryBuilder applyTempTableDdlTransactionHandling(TempTableDdlTransactionHandling handling);
-	public SessionFactoryBuilder applyBatchFetchStyle(BatchFetchStyle style);
-	public SessionFactoryBuilder applyDefaultBatchFetchSize(int size);
-	public SessionFactoryBuilder applyMaximumFetchDepth(int depth);
-	public SessionFactoryBuilder applyDefaultNullPrecedence(NullPrecedence nullPrecedence);
-	public SessionFactoryBuilder applyOrderingOfInserts(boolean enabled);
-	public SessionFactoryBuilder applyOrderingOfUpdates(boolean enabled);
-	public SessionFactoryBuilder applyMultiTenancyStrategy(MultiTenancyStrategy strategy);
-	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver);
-	public SessionFactoryBuilder applyJtaTrackingByThread(boolean enabled);
-	public SessionFactoryBuilder applyPreferUserTransactions(boolean preferUserTransactions);
-	public SessionFactoryBuilder applyQuerySubstitutions(Map substitutions);
-	public SessionFactoryBuilder applyStrictJpaQueryLanguageCompliance(boolean enabled);
-	public SessionFactoryBuilder applyNamedQueryCheckingOnStartup(boolean enabled);
-	public SessionFactoryBuilder applySecondLevelCacheSupport(boolean enabled);
-	public SessionFactoryBuilder applyQueryCacheSupport(boolean enabled);
-	public SessionFactoryBuilder applyQueryCacheFactory(QueryCacheFactory factory);
-	public SessionFactoryBuilder applyCacheRegionPrefix(String prefix);
-	public SessionFactoryBuilder applyMinimalPutsForCaching(boolean enabled);
-	public SessionFactoryBuilder applyStructuredCacheEntries(boolean enabled);
-	public SessionFactoryBuilder applyDirectReferenceCaching(boolean enabled);
-	public SessionFactoryBuilder applyAutomaticEvictionOfCollectionCaches(boolean enabled);
-	public SessionFactoryBuilder applyJdbcBatchSize(int size);
-	public SessionFactoryBuilder applyJdbcBatchingForVersionedEntities(boolean enabled);
-	public SessionFactoryBuilder applyScrollableResultsSupport(boolean enabled);
-	public SessionFactoryBuilder applyResultSetsWrapping(boolean enabled);
-	public SessionFactoryBuilder applyGetGeneratedKeysSupport(boolean enabled);
-	public SessionFactoryBuilder applyJdbcFetchSize(int size);
-	public SessionFactoryBuilder applySqlComments(boolean enabled);
-	public SessionFactoryBuilder applySqlFunction(String registrationName, SQLFunction sqlFunction);
-	public <T extends SessionFactoryBuilder> T unwrap(Class<T> type);
-	public SessionFactory build();
+	public SessionFactoryBuilder applyStatelessInterceptor(Class<? extends Interceptor> statelessInterceptorClass) {
+		public Class<? extends Interceptor> getStatelessInterceptorImplementor() {
+	public Class<? extends Interceptor> getStatelessInterceptorImplementor() {
+	public Class<? extends Interceptor> getStatelessInterceptorImplementor() {
-					public Object doWork(SessionImplementor session, boolean isTemporarySession) {
+					public Object doWork(SharedSessionContractImplementor session, boolean isTemporarySession) {
-	public final void setSession(SessionImplementor session) {
+	public final void setSession(SharedSessionContractImplementor session) {
-	public SessionImplementor getLinkedSession() {
+	public SharedSessionContractImplementor getLinkedSession() {
-						public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-	public static Object createNaturalIdKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public static Object createNaturalIdKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
-	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
+	public CollectionRegion buildCollectionRegion(
+	public StandardQueryCache(QueryResultsRegion cacheRegion, CacheImplementor cacheManager) {
-	public QueryCache getQueryCache(
+	public QueryCache buildQueryCache(QueryResultsRegion region, CacheImplementor cacheManager) {
-	public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException;
-	public Object get(SessionImplementor session, Object key) throws CacheException;
-	public void put(SessionImplementor session, Object key, Object value) throws CacheException;
-	public void evict(Object key) throws CacheException;
-	public void evictAll() throws CacheException;
-	public void clear() throws CacheException;
-	public boolean put(QueryKey key, Type[] returnTypes, List result, boolean isNaturalKeyLookup, SessionImplementor session) throws HibernateException;
-	public List get(QueryKey key, Type[] returnTypes, boolean isNaturalKeyLookup, Set<Serializable> spaces, SessionImplementor session) throws HibernateException;
-	public void destroy();
-	public QueryResultsRegion getRegion();
-	public QueryCache getQueryCache(
-	public String getName();
-	public void destroy() throws CacheException;
-	public boolean contains(Object key);
-	public long getSizeInMemory();
-	public long getElementCountInMemory();
-	public long getElementCountOnDisk();
-	public Map toMap();
-	public long nextTimestamp();
-	public int getTimeout();
-	public void start(SessionFactoryOptions settings, Properties properties) throws CacheException;
-	public void stop();
-	public boolean isMinimalPutsEnabledByDefault();
-	public AccessType getDefaultAccessType();
-	public long nextTimestamp();
-	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
-	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
-	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
-	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
-	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
-	public UpdateTimestampsCache(SessionFactoryOptions settings, Properties props, final SessionFactoryImplementor factory) {
-	public UpdateTimestampsCache(SessionFactoryOptions settings, Properties props) {
+	public UpdateTimestampsCache(SessionFactoryImplementor sessionFactory, TimestampsRegion region) {
-	public void preInvalidate(Serializable[] spaces, SessionImplementor session) throws CacheException {
+	public void preInvalidate(Serializable[] spaces, SharedSessionContractImplementor session) throws CacheException {
-	public void invalidate(Serializable[] spaces, SessionImplementor session) throws CacheException {
+	public void invalidate(Serializable[] spaces, SharedSessionContractImplementor session) throws CacheException {
-	public boolean isUpToDate(Set<Serializable> spaces, Long timestamp, SessionImplementor session) throws CacheException {
+	public boolean isUpToDate(Set<Serializable> spaces, Long timestamp, SharedSessionContractImplementor session) throws CacheException {
-	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier);
-	public Object getCacheKeyId(Object cacheKey);
-	public EntityRegion getRegion();
-	public boolean insert(SessionImplementor session, Object key, Object value, Object version) throws CacheException;
-	public boolean afterInsert(SessionImplementor session, Object key, Object value, Object version) throws CacheException;
-	public boolean update(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException;
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException;
-	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session);
-	public Object[] getNaturalIdValues(Object cacheKey);
-	public NaturalIdRegion getRegion();
-	public boolean insert(SessionImplementor session, Object key, Object value) throws CacheException;
-	public boolean afterInsert(SessionImplementor session, Object key, Object value) throws CacheException;
-	public boolean update(SessionImplementor session, Object key, Object value) throws CacheException;
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, SoftLock lock) throws CacheException;
-	public final boolean unsetSession(SessionImplementor currentSession) {
+	public final boolean unsetSession(SharedSessionContractImplementor currentSession) {
-	public final boolean setCurrentSession(SessionImplementor session) throws HibernateException {
+	public final boolean setCurrentSession(SharedSessionContractImplementor session) throws HibernateException {
-	public final SessionImplementor getSession() {
+	public final SharedSessionContractImplementor getSession() {
-	public PersistentArrayHolder(SessionImplementor session, Object array) {
+	public PersistentArrayHolder(SharedSessionContractImplementor session, Object array) {
-	public PersistentArrayHolder(SessionImplementor session, CollectionPersister persister) {
+	public PersistentArrayHolder(SharedSessionContractImplementor session, CollectionPersister persister) {
-	public PersistentBag(SessionImplementor session) {
+	public PersistentBag(SharedSessionContractImplementor session) {
-	public PersistentBag(SessionImplementor session, Collection coll) {
+	public PersistentBag(SharedSessionContractImplementor session, Collection coll) {
-	public PersistentIdentifierBag(SessionImplementor session) {
+	public PersistentIdentifierBag(SharedSessionContractImplementor session) {
-	public PersistentIdentifierBag(SessionImplementor session, Collection coll) {
+	public PersistentIdentifierBag(SharedSessionContractImplementor session, Collection coll) {
-	public PersistentList(SessionImplementor session) {
+	public PersistentList(SharedSessionContractImplementor session) {
-	public PersistentList(SessionImplementor session, List list) {
+	public PersistentList(SharedSessionContractImplementor session, List list) {
-	public PersistentMap(SessionImplementor session) {
+	public PersistentMap(SharedSessionContractImplementor session) {
-	public PersistentMap(SessionImplementor session, Map map) {
+	public PersistentMap(SharedSessionContractImplementor session, Map map) {
-	public PersistentSet(SessionImplementor session) {
+	public PersistentSet(SharedSessionContractImplementor session) {
-	public PersistentSet(SessionImplementor session, java.util.Set set) {
+	public PersistentSet(SharedSessionContractImplementor session, java.util.Set set) {
-	public Object getOwner();
-	public void setOwner(Object entity);
-	public boolean empty();
-	public void setSnapshot(Serializable key, String role, Serializable snapshot);
-	public void postAction();
-	public Object getValue();
-	public void beginRead();
-	public boolean endRead();
-	public boolean afterInitialize();
-	public boolean isDirectlyAccessible();
-	public boolean unsetSession(SessionImplementor currentSession);
-	public boolean setCurrentSession(SessionImplementor session) throws HibernateException;
-	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner);
-	public Iterator entries(CollectionPersister persister);
-	public Object readFrom(ResultSet rs, CollectionPersister role, CollectionAliases descriptor, Object owner)
-	public Object getIdentifier(Object entry, int i);
-	public Object getIndex(Object entry, int i, CollectionPersister persister);
-	public Object getElement(Object entry);
-	public Object getSnapshotElement(Object entry, int i);
-	public void beforeInitialize(CollectionPersister persister, int anticipatedSize);
-	public boolean equalsSnapshot(CollectionPersister persister);
-	public boolean isSnapshotEmpty(Serializable snapshot);
-	public Serializable disassemble(CollectionPersister persister) ;
-	public boolean needsRecreate(CollectionPersister persister);
-	public Serializable getSnapshot(CollectionPersister persister);
-	public void forceInitialization();
-	public boolean entryExists(Object entry, int i);
-	public boolean needsInserting(Object entry, int i, Type elemType);
-	public boolean needsUpdating(Object entry, int i, Type elemType);
-	public boolean isRowUpdatePossible();
-	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula);
-	public boolean isWrapper(Object collection);
-	public boolean wasInitialized();
-	public boolean hasQueuedOperations();
-	public Iterator queuedAdditionIterator();
-	public Collection getQueuedOrphans(String entityName);
-	public Serializable getKey();
-	public String getRole();
-	public boolean isUnreferenced();
-	public boolean isDirty();
-	public void clearDirty();
-	public Serializable getStoredSnapshot();
-	public void dirty();
-	public void preInsert(CollectionPersister persister);
-	public void afterRowInsert(CollectionPersister persister, Object entry, int i);
-	public Collection getOrphans(Serializable snapshot, String entityName);
-		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
+		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
-		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
+		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
-		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
+		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
-		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
+		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
-		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
+		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
-		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
+		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
-		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
+		public Blob mergeBlob(Blob original, Blob target, SharedSessionContractImplementor session) {
-		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
+		public Clob mergeClob(Clob original, Clob target, SharedSessionContractImplementor session) {
-		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
+		public NClob mergeNClob(NClob original, NClob target, SharedSessionContractImplementor session) {
-		public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
+		public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session)
-	public Blob mergeBlob(Blob original, Blob target, SessionImplementor session);
-	public Clob mergeClob(Clob original, Clob target, SessionImplementor session);
-	public NClob mergeNClob(NClob original, NClob target, SessionImplementor session);
-	public Serializable executeAndExtract(PreparedStatement insert, SessionImplementor session)
+	public Serializable executeAndExtract(PreparedStatement insert, SharedSessionContractImplementor session)
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
+	public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
+	public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
+	public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
+	public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
+	public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
+	public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
-	public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session) {
+	public void lock(Serializable id, Object version, Object object, int timeout, SharedSessionContractImplementor session) {
-	public boolean isNullifiable(boolean earlyInsert, SessionImplementor session) {
+	public boolean isNullifiable(boolean earlyInsert, SharedSessionContractImplementor session) {
-		public Nullifier(Object self, boolean isDelete, boolean isEarlyInsert, SessionImplementor session) {
+		public Nullifier(Object self, boolean isDelete, boolean isEarlyInsert, SharedSessionContractImplementor session) {
-	public static boolean isNotTransient(String entityName, Object entity, Boolean assumed, SessionImplementor session) {
+	public static boolean isNotTransient(String entityName, Object entity, Boolean assumed, SharedSessionContractImplementor session) {
-	public static boolean isTransient(String entityName, Object entity, Boolean assumed, SessionImplementor session) {
+	public static boolean isTransient(String entityName, Object entity, Boolean assumed, SharedSessionContractImplementor session) {
-	public String toLoggableString(SessionImplementor session) {
+	public String toLoggableString(SharedSessionContractImplementor session) {
-	public Nullability(SessionImplementor session) {
+	public Nullability(SharedSessionContractImplementor session) {
-	public StatefulPersistenceContext(SessionImplementor session) {
+	public StatefulPersistenceContext(SharedSessionContractImplementor session) {
-	public SessionImplementor getSession() {
+	public SharedSessionContractImplementor getSession() {
-								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+								public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
-								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+								public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
-	public static Object increment(Object version, VersionType versionType, SessionImplementor session) {
+	public static Object increment(Object version, VersionType versionType, SharedSessionContractImplementor session) {
-	public ResourceRegistry getResourceRegistry(){
-	public  ConnectionReleaseMode getConnectionReleaseMode() {
-	public LogicalConnectionImplementor getLogicalConnection();
-	public Batch getBatch(BatchKey key);
-	public void executeBatch();
-	public void abortBatch();
-	public StatementPreparer getStatementPreparer();
-	public ResultSetReturn getResultSetReturn();
-	public void flushBeginning();
-	public void flushEnding();
-	public Connection close();
-	public void afterTransaction();
-	public void afterStatementExecution();
-	public <T> T coordinateWork(WorkExecutorVisitable<T> work);
-	public void cancelLastQuery();
-	public int determineRemainingTransactionTimeOutPeriod();
-	public void enableReleases();
-	public void disableReleases();
-	public void registerLastQuery(Statement statement);
-	public boolean isReadyForSerialization();
-	public ConnectionReleaseMode getConnectionReleaseMode();
-	public ResourceRegistry getResourceRegistry();
-	public ParameterMetadata getParameterMetadata(String nativeQuery) {
+	public ParameterMetadataImpl getParameterMetadata(String nativeQuery) {
-	public ParameterMetadata getParameterMetadata() {
+	public ParameterMetadataImpl getParameterMetadata() {
-	public int performExecuteUpdate(QueryParameters queryParameters, SessionImplementor session)
+	public int performExecuteUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session)
-public class NamedParameterDescriptor implements Serializable {
+public class NamedParameterDescriptor implements QueryParameter {
+	public Integer getPosition() {
+	public Class getParameterType() {
+	public boolean isJpaPositionalParameter() {
+	public Type getType() {
-public class OrdinalParameterDescriptor implements Serializable {
+public class OrdinalParameterDescriptor implements QueryParameter {
+	public Type getType() {
+	public String getName() {
+	public Integer getPosition() {
+	public Class getParameterType() {
+	public boolean isJpaPositionalParameter() {
+	public SharedSessionBuilder connectionHandlingMode() {
-	public SharedSessionBuilder transactionContext() {
-	public void close();
-	public QueryCache getQueryCache(String regionName) throws HibernateException;
-	public QueryCache getQueryCache();
-	public void addCacheRegion(String name, Region region);
-	public UpdateTimestampsCache getUpdateTimestampsCache();
-	public void evictQueries() throws HibernateException;
-	public Region getSecondLevelCacheRegion(String regionName);
-	public Region getNaturalIdCacheRegion(String regionName);
-	public Map<String, Region> getAllSecondLevelCacheRegions();
-	public RegionFactory getRegionFactory();
-	public SessionImplementor getSession();
+	public SharedSessionContractImplementor getSession();
+	public QueryParameters(
-	public boolean isReadOnly(SessionImplementor session) {
+	public boolean isReadOnly(SharedSessionContractImplementor session) {
-	public void processFilters(String sql, SessionImplementor session) {
+	public void processFilters(String sql, SharedSessionContractImplementor session) {
-public interface SessionBuilderImplementor extends SessionBuilder {
+public interface SessionBuilderImplementor<T extends SessionBuilder> extends SessionBuilder<T> {
-	public SessionBuilder owner(SessionOwner sessionOwner);
-public class SessionDelegatorBaseImpl implements SessionImplementor, Session {
-	public SessionDelegatorBaseImpl(SessionImplementor sessionImplementor, Session session) {
+public class SessionDelegatorBaseImpl implements SessionImplementor {
+	public SessionDelegatorBaseImpl(SessionImplementor delegate, Session session) {
+	public SessionDelegatorBaseImpl(SessionImplementor delegate) {
+	public UUID getSessionIdentifier() {
+	public void handlePersistenceException(PersistenceException e) {
+	public void throwPersistenceException(PersistenceException e) {
+	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
+	public RuntimeException convert(RuntimeException e) {
+	public RuntimeException convert(HibernateException e) {
+	public void throwPersistenceException(HibernateException e) {
+	public PersistenceException wrapStaleStateException(StaleStateException e) {
+	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
+	public LockOptions buildLockOptions(LockModeType lockModeType, Map<String, Object> properties) {
+	public <T> QueryImplementor<T> createQuery(
+	public void checkOpen(boolean markForRollbackIfClosed) {
+	public void markForRollbackOnly() {
+	public FlushModeType getFlushMode() {
+	public void setFlushMode(FlushModeType flushModeType) {
-	public FlushMode getFlushMode() {
+	public void setHibernateFlushMode(FlushMode flushMode) {
+	public FlushMode getHibernateFlushMode() {
-	public Connection connection() {
+	public void lock(Object entity, LockModeType lockMode) {
-	public void flush() {
+	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
-	public Query getNamedQuery(String name) {
+	public Connection connection() {
-	public Query getNamedSQLQuery(String name) {
+	public void flush() {
+	public JdbcServices getJdbcServices() {
+	public JdbcSessionContext getJdbcSessionContext() {
+	public boolean shouldAutoJoinTransaction() {
-	public Query createQuery(NamedQueryDefinition namedQueryDefinition) {
+	public SessionEventListenerManager getEventListenerManager() {
-	public SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition) {
+	public Transaction beginTransaction() {
-	public SessionEventListenerManager getEventListenerManager() {
+	public Transaction getTransaction() {
+	public void afterTransactionBegin() {
-	public Transaction beginTransaction() {
+	public void beforeTransactionCompletion() {
-	public Transaction getTransaction() {
+	public void afterTransactionCompletion(boolean successful, boolean delayed) {
+	public void flushBeforeTransactionCompletion() {
+	public EntityManagerFactory getEntityManagerFactory() {
+	public CriteriaBuilder getCriteriaBuilder() {
+	public Metamodel getMetamodel() {
+	public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
+	public EntityGraph<?> createEntityGraph(String graphName) {
+	public EntityGraph<?> getEntityGraph(String graphName) {
+	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
+	public QueryImplementor getNamedQuery(String name) {
+	public NativeQueryImplementor getNamedSQLQuery(String name) {
+	public NativeQueryImplementor getNamedNativeQuery(String name) {
+	public QueryImplementor createQuery(String queryString) {
+	public <T> QueryImplementor<T> createQuery(String queryString, Class<T> resultType) {
+	public <T> QueryImplementor<T> createQuery(CriteriaQuery<T> criteriaQuery) {
+	public QueryImplementor createQuery(CriteriaUpdate updateQuery) {
+	public QueryImplementor createQuery(CriteriaDelete deleteQuery) {
+	public QueryImplementor createNamedQuery(String name) {
+	public <T> QueryImplementor<T> createNamedQuery(String name, Class<T> resultClass) {
+	public NativeQueryImplementor createNativeQuery(String sqlString) {
+	public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
+	public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
+	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
+	public void joinTransaction() {
-	public Query createQuery(String queryString) {
+	public boolean isJoinedToTransaction() {
-	public SQLQuery createSQLQuery(String queryString) {
+	public <T> T unwrap(Class<T> cls) {
+	public Object getDelegate() {
+	public NativeQueryImplementor createSQLQuery(String queryString) {
-	public SessionFactory getSessionFactory() {
+	public SessionFactoryImplementor getSessionFactory() {
+	public LockModeType getLockMode(Object entity) {
+	public void setProperty(String propertyName, Object value) {
+	public Map<String, Object> getProperties() {
+	public void remove(Object entity) {
+	public <T> T find(Class<T> entityClass, Object primaryKey) {
+	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
+	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
+	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
+	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
+	public void refresh(Object entity, Map<String, Object> properties) {
+	public void refresh(Object entity, LockModeType lockMode) {
+	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
-	public Query createFilter(Object collection, String queryString) {
+	public org.hibernate.query.Query createFilter(Object collection, String queryString) {
+	public void detach(Object entity) {
+	public PersistenceUnitTransactionType getTransactionType() {
+	public SynchronizationType getSynchronizationType() {
+	public boolean isFlushBeforeCompletionEnabled() {
+	public ActionQueue getActionQueue() {
+	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
+	public void forceFlush(EntityEntry e) throws HibernateException {
+	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
+	public void persist(String entityName, Object object, Map createdAlready) throws HibernateException {
+	public void persistOnFlush(String entityName, Object object, Map copiedAlready) {
+	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
+	public void delete(String entityName, Object child, boolean isCascadeDeleteEnabled, Set transientEntities) {
+	public void removeOrphanBeforeUpdates(String entityName, Object child) {
+	public SessionImplementor getSession() {
+	public boolean useStreamForLobBinding() {
+	public LobCreator getLobCreator() {
-	public WrapperOptions getWrapperOptions() {
+	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
-	public Statistics getStatistics() {
+	public StatisticsImplementor getStatistics() {
-	public Cache getCache() {
+	public CacheImplementor getCache() {
+	public PersistenceUnitUtil getPersistenceUnitUtil() {
+	public void addNamedQuery(String name, Query query) {
+	public <T> T unwrap(Class<T> cls) {
+	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
-	public Properties getProperties() {
+	public Map<String, Object> getProperties() {
+	public EntityGraphImplementor findEntityGraphByName(String name) {
+	public String getName() {
+	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
+	public EntityManager createEntityManager() {
+	public EntityManager createEntityManager(Map map) {
+	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
+	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
+	public CriteriaBuilder getCriteriaBuilder() {
+	public MetamodelImplementor getMetamodel() {
+	public boolean isOpen() {
-public interface SessionImplementor extends Serializable, LobCreationContext, WrapperOptionsContext {
+public interface SessionImplementor
-	public boolean shouldAutoCloseSession();
-	public ExceptionMapper getExceptionMapper();
-	public AfterCompletionAction getAfterCompletionAction();
-	public ManagedFlushChecker getManagedFlushChecker();
-public interface SharedSessionContractImplementor {
+public interface SharedSessionContractImplementor
-public class TransactionImpl implements Transaction {
+public class TransactionImpl implements TransactionImplementor {
+	public boolean isActive() {
-	public void markRollbackOnly() {
+	public void setRollbackOnly() {
+	public boolean getRollbackOnly() {
-public interface TransactionImplementor {
+public interface TransactionImplementor extends Transaction {
-public interface EventSource extends SessionImplementor, Session {
+public interface EventSource extends SessionImplementor {
-	public ActionQueue getActionQueue();
-	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException;
-	public void forceFlush(EntityEntry e) throws HibernateException;
-	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException;
-	public void persist(String entityName, Object object, Map createdAlready) throws HibernateException;
-	public void persistOnFlush(String entityName, Object object, Map copiedAlready);
-	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException;
-	public void delete(String entityName, Object child, boolean isCascadeDeleteEnabled, Set transientEntities);
-	public void removeOrphanBeforeUpdates(String entityName, Object child);
-public interface EntityGraphImplementor {
+public interface EntityGraphImplementor<T> extends EntityGraph<T>, GraphNodeImplementor {
-	public List list(SessionImplementor session, QueryParameters queryParameters)
+	public List list(SharedSessionContractImplementor session, QueryParameters queryParameters)
-	public ScrollableResults scroll(QueryParameters queryParameters, SessionImplementor session)
+	public ScrollableResults scroll(QueryParameters queryParameters, SharedSessionContractImplementor session)
-	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session)
+	public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session)
-	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
+	public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
-	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
+	public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
-	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
+	public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
-	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
+	public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
-	public String[] getSqlStatements();
-	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException;
-		public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
+		public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position)
-	public List list(SessionImplementor session, QueryParameters queryParameters)
+	public List list(SharedSessionContractImplementor session, QueryParameters queryParameters)
-	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {
+	public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
-	public void prepare(
-	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess);
-	public static interface UpdateHandler {
-		public Queryable getTargetedQueryable();
-		public String[] getSqlStatements();
-		public int execute(SessionImplementor session, QueryParameters queryParameters);
-	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
-	public static interface DeleteHandler {
-		public Queryable getTargetedQueryable();
-		public String[] getSqlStatements();
-		public int execute(SessionImplementor session, QueryParameters queryParameters);
-	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
-	public int execute(SessionImplementor session, QueryParameters queryParameters) {
+	public int execute(SharedSessionContractImplementor session, QueryParameters queryParameters) {
-	public int execute(SessionImplementor session, QueryParameters queryParameters) {
+	public int execute(SharedSessionContractImplementor session, QueryParameters queryParameters) {
-	public void bindSessionIdentifier(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
+	public void bindSessionIdentifier(PreparedStatement ps, SharedSessionContractImplementor session, int position) throws SQLException {
-	public void cleanUpRows(String tableName, SessionImplementor session) {
+	public void cleanUpRows(String tableName, SharedSessionContractImplementor session) {
-	public JDBCException convert(SessionFactoryImplementor factory, SQLException e, String message, String sql) {
-	public Serializable generate(SessionImplementor s, Object obj) {
+	public Serializable generate(SharedSessionContractImplementor s, Object obj) {
-	public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
+	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
-	public static interface GenerationContextLocator {
+	public interface GenerationContextLocator {
-		public Serializable locateGenerationContext(SessionImplementor session, Object incomingObject);
-	public static interface GenerationPlan extends ExportableProducer {
+	public interface GenerationPlan extends ExportableProducer {
-		public void execute(SessionImplementor session, Object incomingObject, Object injectionContext);
-	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
+	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
-	public Serializable generate(SessionImplementor sessionImplementor, Object object) {
+	public Serializable generate(SharedSessionContractImplementor sessionImplementor, Object object) {
-	public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {
+	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
-	public Serializable generate(SessionImplementor session, Object object) throws HibernateException;
+	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException;
-		public Serializable executeAndExtract(PreparedStatement insert, SessionImplementor session)
+		public Serializable executeAndExtract(PreparedStatement insert, SharedSessionContractImplementor session)
-		public Serializable determineGeneratedIdentifier(SessionImplementor session, Object entity) {
+		public Serializable determineGeneratedIdentifier(SharedSessionContractImplementor session, Object entity) {
-	public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
+	public synchronized Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
-	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
+	public synchronized Serializable generate(final SharedSessionContractImplementor session, Object obj) {
-	public Serializable generate(SessionImplementor session, Object obj) {
+	public Serializable generate(SharedSessionContractImplementor session, Object obj) {
-	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
+	public synchronized Serializable generate(final SharedSessionContractImplementor session, Object obj) {
-	public Serializable generate(SessionImplementor s, Object obj) {
+	public Serializable generate(SharedSessionContractImplementor s, Object obj) {
-	public int getGeneratedVersion();
-	public UUID generateUUID(SessionImplementor session);
-	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
+	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
-	public Serializable generate(SessionImplementor session, Object obj) {
+	public Serializable generate(SharedSessionContractImplementor session, Object obj) {
-	public String getName();
-	public int getTimesAccessed();
-	public int getInitialValue();
-	public int getIncrementSize();
-	public AccessCallback buildCallback(SessionImplementor session);
-	public void prepare(Optimizer optimizer);
-	public String[] sqlCreateStrings(Dialect dialect);
-	public String[] sqlDropStrings(Dialect dialect);
-	public boolean isPhysicalSequence();
-	public AccessCallback buildCallback(final SessionImplementor session) {
+	public AccessCallback buildCallback(final SharedSessionContractImplementor session) {
-	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
+	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
-	public Serializable generate(final SessionImplementor session, final Object obj) {
+	public Serializable generate(final SharedSessionContractImplementor session, final Object obj) {
-	public AccessCallback buildCallback(final SessionImplementor session) {
+	public AccessCallback buildCallback(final SharedSessionContractImplementor session) {
-	public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert();
-	public Serializable performInsert(String insertSQL, SessionImplementor session, Binder binder);
-	public UUID generateUUID(SessionImplementor session) {
+	public UUID generateUUID(SharedSessionContractImplementor session) {
-	public UUID generateUUID(SessionImplementor session) {
+	public UUID generateUUID(SharedSessionContractImplementor session) {
-public abstract class AbstractBasicQueryContractImpl implements BasicQueryContract {
-	public FlushMode getFlushMode() {
-	public BasicQueryContract setFlushMode(FlushMode flushMode) {
-	public CacheMode getCacheMode() {
-	public BasicQueryContract setCacheMode(CacheMode cacheMode) {
-	public boolean isCacheable() {
-	public BasicQueryContract setCacheable(boolean cacheable) {
-	public String getCacheRegion() {
-	public BasicQueryContract setCacheRegion(String cacheRegion) {
-	public boolean isReadOnly() {
-	public BasicQueryContract setReadOnly(boolean readOnly) {
-	public Integer getTimeout() {
-	public BasicQueryContract setTimeout(int timeout) {
-	public Integer getFetchSize() {
-	public BasicQueryContract setFetchSize(int fetchSize) {
-	public QueryParameters buildQueryParametersObject() {
-public abstract class AbstractQueryImpl implements Query {
-	public AbstractQueryImpl(
-	public ParameterMetadata getParameterMetadata() {
-	public String toString() {
-	public final String getQueryString() {
-	public boolean isCacheable() {
-	public Query setCacheable(boolean cacheable) {
-	public String getCacheRegion() {
-	public Query setCacheRegion(String cacheRegion) {
-	public FlushMode getFlushMode() {
-	public Query setFlushMode(FlushMode flushMode) {
-	public CacheMode getCacheMode() {
-	public Query setCacheMode(CacheMode cacheMode) {
-	public String getComment() {
-	public Query setComment(String comment) {
-	public Query addQueryHint(String queryHint) {
-	public Integer getFirstResult() {
-	public Query setFirstResult(int firstResult) {
-	public Integer getMaxResults() {
-	public Query setMaxResults(int maxResults) {
-	public Integer getTimeout() {
-	public Query setTimeout(int timeout) {
-	public Integer getFetchSize() {
-	public Query setFetchSize(int fetchSize) {
-	public Type[] getReturnTypes() throws HibernateException {
-	public String[] getReturnAliases() throws HibernateException {
-	public Query setCollectionKey(Serializable collectionKey) {
-	public boolean isReadOnly() {
-	public Query setReadOnly(boolean readOnly) {
-	public Query setResultTransformer(ResultTransformer transformer) {
-	public void setOptionalEntityName(String optionalEntityName) {
-	public void setOptionalId(Serializable optionalId) {
-	public void setOptionalObject(Object optionalObject) {
-	public abstract LockOptions getLockOptions();
-	public String[] getNamedParameters() throws HibernateException {
-	public boolean hasNamedParameters() {
-	public Query setParameter(int position, Object val, Type type) {
-	public Query setParameter(String name, Object val, Type type) {
-	public Query setParameter(int position, Object val) throws HibernateException {
-	public Query setParameter(String name, Object val) throws HibernateException {
-	public Type guessType(Class clazz) throws HibernateException {
-	public Query setString(int position, String val) {
-	public Query setCharacter(int position, char val) {
-	public Query setBoolean(int position, boolean val) {
-	public Query setByte(int position, byte val) {
-	public Query setShort(int position, short val) {
-	public Query setInteger(int position, int val) {
-	public Query setLong(int position, long val) {
-	public Query setFloat(int position, float val) {
-	public Query setDouble(int position, double val) {
-	public Query setBinary(int position, byte[] val) {
-	public Query setText(int position, String val) {
-	public Query setSerializable(int position, Serializable val) {
-	public Query setDate(int position, Date date) {
-	public Query setTime(int position, Date date) {
-	public Query setTimestamp(int position, Date date) {
-	public Query setEntity(int position, Object val) {
-	public Query setLocale(int position, Locale locale) {
-	public Query setCalendar(int position, Calendar calendar) {
-	public Query setCalendarDate(int position, Calendar calendar) {
-	public Query setBinary(String name, byte[] val) {
-	public Query setText(String name, String val) {
-	public Query setBoolean(String name, boolean val) {
-	public Query setByte(String name, byte val) {
-	public Query setCharacter(String name, char val) {
-	public Query setDate(String name, Date date) {
-	public Query setDouble(String name, double val) {
-	public Query setEntity(String name, Object val) {
-	public Query setFloat(String name, float val) {
-	public Query setInteger(String name, int val) {
-	public Query setLocale(String name, Locale locale) {
-	public Query setCalendar(String name, Calendar calendar) {
-	public Query setCalendarDate(String name, Calendar calendar) {
-	public Query setLong(String name, long val) {
-	public Query setSerializable(String name, Serializable val) {
-	public Query setShort(String name, short val) {
-	public Query setString(String name, String val) {
-	public Query setTime(String name, Date date) {
-	public Query setTimestamp(String name, Date date) {
-	public Query setBigDecimal(int position, BigDecimal number) {
-	public Query setBigDecimal(String name, BigDecimal number) {
-	public Query setBigInteger(int position, BigInteger number) {
-	public Query setBigInteger(String name, BigInteger number) {
-	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
-	public Query setParameterList(String name, Collection vals) throws HibernateException {
-	public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException {
-	public Query setParameterList(String name, Object[] values) throws HibernateException {
-	public Query setProperties(Map map) throws HibernateException {
-	public Query setProperties(Object bean) throws HibernateException {
-	public Query setParameters(Object[] values, Type[] types) {
-	public Object uniqueResult() throws HibernateException {
-	public Type[] typeArray() {
-	public Object[] valueArray() {
-	public QueryParameters getQueryParameters(Map namedParams) {
-	public void applyEntityGraphQueryHint(EntityGraphQueryHint hint) {
-	public SessionFactoryImplementor getFactory() {
-	public <T> T execute(final LobCreationContext.Callback<T> callback) {
-					public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
-	public boolean isClosed() {
-	public Query createQuery(NamedQueryDefinition namedQueryDefinition) {
-	public SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition) {
-	public Query getNamedQuery(String queryName) throws MappingException {
-	public Query getNamedSQLQuery(String queryName) throws MappingException {
-	public Query createQuery(String queryString) {
-	public SQLQuery createSQLQuery(String sql) {
-	public ProcedureCall getNamedProcedureCall(String name) {
-	public ProcedureCall createStoredProcedureCall(String procedureName) {
-	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
-	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
-	public Transaction getTransaction() throws HibernateException {
-	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
-	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
-	public String getTenantIdentifier() {
-	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
-	public JdbcConnectionAccess getJdbcConnectionAccess() {
-	public UUID getSessionIdentifier() {
-		public Connection obtainConnection() throws SQLException {
-		public void releaseConnection(Connection connection) throws SQLException {
-		public boolean supportsAggressiveRelease() {
-		public Connection obtainConnection() throws SQLException {
-		public void releaseConnection(Connection connection) throws SQLException {
-		public boolean supportsAggressiveRelease() {
-	public class JdbcSessionContextImpl implements JdbcSessionContext {
-		public JdbcSessionContextImpl(SessionFactoryImpl sessionFactory, StatementInspector inspector) {
-		public boolean isScrollableResultSetsEnabled() {
-		public boolean isGetGeneratedKeysEnabled() {
-		public int getFetchSize() {
-		public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
-		public ConnectionReleaseMode getConnectionReleaseMode() {
-		public ConnectionAcquisitionMode getConnectionAcquisitionMode() {
-		public StatementInspector getStatementInspector() {
-		public JdbcObserver getObserver() {
-		public SessionFactoryImplementor getSessionFactory() {
-		public ServiceRegistry getServiceRegistry() {
-	public class JdbcObserverImpl implements JdbcObserver {
-		public JdbcObserverImpl() {
-		public void jdbcConnectionAcquisitionStart() {
-		public void jdbcConnectionAcquisitionEnd(Connection connection) {
-		public void jdbcConnectionReleaseStart() {
-		public void jdbcConnectionReleaseEnd() {
-		public void jdbcPrepareStatementStart() {
-		public void jdbcPrepareStatementEnd() {
-		public void jdbcExecuteStatementStart() {
-		public void jdbcExecuteStatementEnd() {
-		public void jdbcExecuteBatchStart() {
-		public void jdbcExecuteBatchEnd() {
-	public TransactionCoordinatorBuilder getTransactionCoordinatorBuilder() {
-	public WrapperOptions getWrapperOptions() {
-public class AbstractSharedSessionContract {
+public abstract class AbstractSharedSessionContract implements SharedSessionContractImplementor {
+	public AbstractSharedSessionContract(SessionFactoryImpl factory, SessionCreationOptions options) {
+	public SessionFactoryImplementor getFactory() {
+	public Interceptor getInterceptor() {
+	public SessionEventListenerManager getEventListenerManager() {
+	public JdbcCoordinator getJdbcCoordinator() {
+	public TransactionCoordinator getTransactionCoordinator() {
+	public JdbcSessionContext getJdbcSessionContext() {
+	public EntityNameResolver getEntityNameResolver() {
+	public UUID getSessionIdentifier() {
+	public String getTenantIdentifier() {
+	public long getTimestamp() {
+	public boolean isOpen() {
+	public boolean isClosed() {
+	public void close() {
+	public void checkOpen(boolean markForRollbackIfClosed) {
+	public void markForRollbackOnly() {
+	public boolean isTransactionInProgress() {
+	public Transaction getTransaction() throws HibernateException {
+	public Transaction beginTransaction() {
+	public boolean isConnected() {
+	public JdbcConnectionAccess getJdbcConnectionAccess() {
+	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
+	public boolean useStreamForLobBinding() {
+	public LobCreator getLobCreator() {
+	public <T> T execute(final LobCreationContext.Callback<T> callback) {
+	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
+	public JdbcServices getJdbcServices() {
+	public void setFlushMode(FlushMode flushMode) {
+	public FlushModeType getFlushMode() {
+	public void setHibernateFlushMode(FlushMode flushMode) {
+	public FlushMode getHibernateFlushMode() {
+	public CacheMode getCacheMode() {
+	public void setCacheMode(CacheMode cacheMode) {
+	public QueryImplementor getNamedQuery(String name) {
+	public QueryImplementor createQuery(String queryString) {
+	public <T> QueryImplementor<T> createQuery(String queryString, Class<T> resultClass) {
+	public QueryImplementor createNamedQuery(String name) {
+	public <R> QueryImplementor<R> createNamedQuery(String name, Class<R> resultClass) {
+	public NativeQueryImplementor createNativeQuery(String sqlString) {
+	public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
+	public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
+	public NativeQueryImplementor getNamedNativeQuery(String name) {
+	public NativeQueryImplementor createSQLQuery(String queryString) {
+	public NativeQueryImplementor getNamedSQLQuery(String name) {
+	public ProcedureCall getNamedProcedureCall(String name) {
+	public ProcedureCall createStoredProcedureCall(String procedureName) {
+	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
+	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
+	public RuntimeException convert(HibernateException e) {
+	public RuntimeException convert(RuntimeException e) {
+	public void handlePersistenceException(PersistenceException e) {
+	public void throwPersistenceException(PersistenceException e) {
+	public void throwPersistenceException(HibernateException e) {
+	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
+	public PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
+	public PersistenceException wrapStaleStateException(StaleStateException e) {
+	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
+	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
+	public SessionFactory getSessionFactory() {
+	public RegionFactory getRegionFactory() {
+	public String qualifyRegionName(String regionName) {
-	public QueryCache getQueryCache() {
+	public QueryCache getDefaultQueryCache() {
-	public void addCacheRegion(String name, Region region) {
-	public Region getSecondLevelCacheRegion(String regionName) {
+	public String[] getSecondLevelCacheRegionNames() {
-	public Region getNaturalIdCacheRegion(String regionName) {
+	public EntityRegionAccessStrategy getEntityRegionAccess(String regionName) {
-	public Map<String, Region> getAllSecondLevelCacheRegions() {
+	public CollectionRegionAccessStrategy getCollectionRegionAccess(String regionName) {
-	public RegionFactory getRegionFactory() {
+	public NaturalIdRegionAccessStrategy getNaturalIdCacheRegionAccessStrategy(String regionName) {
+	public boolean contains(Class cls, Object primaryKey) {
+	public void evict(Class cls, Object primaryKey) {
+	public void evict(Class cls) {
+	public void evictAll() {
+	public <T> T unwrap(Class<T> cls) {
+	public EntityRegionAccessStrategy determineEntityRegionAccessStrategy(PersistentClass model) {
+	public NaturalIdRegionAccessStrategy determineNaturalIdRegionAccessStrategy(PersistentClass model) {
+	public CollectionRegionAccessStrategy determineCollectionRegionAccessStrategy(Collection model) {
-public class CollectionFilterImpl extends QueryImpl {
-	public CollectionFilterImpl(
-	public Iterator iterate() throws HibernateException {
-	public List list() throws HibernateException {
-	public ScrollableResults scroll() throws HibernateException {
-	public Type[] typeArray() {
-	public Object[] valueArray() {
+public class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
+	public ContextualJdbcConnectionAccess(
-	public CriteriaImpl(String entityOrClassName, SessionImplementor session) {
+	public CriteriaImpl(String entityOrClassName, SharedSessionContractImplementor session) {
-	public CriteriaImpl(String entityOrClassName, String alias, SessionImplementor session) {
+	public CriteriaImpl(String entityOrClassName, String alias, SharedSessionContractImplementor session) {
-	public SessionImplementor getSession() {
+	public SharedSessionContractImplementor getSession() {
-	public void setSession(SessionImplementor session) {
+	public void setSession(SharedSessionContractImplementor session) {
-	public JdbcObserverImpl() {
+	public JdbcObserverImpl(SharedSessionContractImplementor session) {
-	public JdbcSessionContextImpl(SessionFactoryImpl sessionFactory, StatementInspector inspector) {
+	public JdbcSessionContextImpl(SharedSessionContractImplementor session, StatementInspector statementInspector) {
-public class NamedQueryRepository {
-	public NamedQueryRepository(
-	public NamedQueryRepository(
-	public NamedQueryDefinition getNamedQueryDefinition(String queryName) {
-	public NamedSQLQueryDefinition getNamedSQLQueryDefinition(String queryName) {
-	public ProcedureCallMemento getNamedProcedureCallMemento(String name) {
-	public ResultSetMappingDefinition getResultSetMappingDefinition(String mappingName) {
-	public synchronized void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
-	public synchronized void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
-	public synchronized void registerNamedProcedureCallMemento(String name, ProcedureCallMemento memento) {
-	public Map<String,HibernateException> checkNamedQueries(QueryPlanCache queryPlanCache) {
+public class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
+	public NonContextualJdbcConnectionAccess(
-public class QueryImpl extends AbstractQueryImpl {
-	public QueryImpl(
-	public QueryImpl(String queryString, SessionImplementor session, ParameterMetadata parameterMetadata) {
-	public Iterator iterate() throws HibernateException {
-	public ScrollableResults scroll() throws HibernateException {
-	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
-	public List list() throws HibernateException {
-	public int executeUpdate() throws HibernateException {
-	public Query setLockMode(String alias, LockMode lockMode) {
-	public Query setLockOptions(LockOptions lockOption) {
-	public LockOptions getLockOptions() {
-	public boolean isSelect() {
-public class SQLQueryImpl extends AbstractQueryImpl implements SQLQuery {
-	public List<NativeSQLQueryReturn> getQueryReturns() {
-	public Collection<String> getSynchronizedQuerySpaces() {
-	public boolean isCallable() {
-	public List list() throws HibernateException {
-	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
-	public ScrollableResults scroll() throws HibernateException {
-	public Iterator iterate() throws HibernateException {
-	public QueryParameters getQueryParameters(Map namedParams) {
-	public String[] getReturnAliases() throws HibernateException {
-	public Type[] getReturnTypes() throws HibernateException {
-	public Query setLockMode(String alias, LockMode lockMode) {
-	public Query setLockOptions(LockOptions lockOptions) {
-	public LockOptions getLockOptions() {
-	public SQLQuery addScalar(final String columnAlias, final Type type) {
-					public NativeSQLQueryReturn buildReturn() {
-	public SQLQuery addScalar(String columnAlias) {
-	public RootReturn addRoot(String tableAlias, String entityName) {
-	public RootReturn addRoot(String tableAlias, Class entityType) {
-	public SQLQuery addEntity(String entityName) {
-	public SQLQuery addEntity(String alias, String entityName) {
-	public SQLQuery addEntity(String alias, String entityName, LockMode lockMode) {
-	public SQLQuery addEntity(Class entityType) {
-	public SQLQuery addEntity(String alias, Class entityClass) {
-	public SQLQuery addEntity(String alias, Class entityClass, LockMode lockMode) {
-	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
-	public SQLQuery addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
-	public SQLQuery addJoin(String alias, String path) {
-	public SQLQuery addJoin(String alias, String path, LockMode lockMode) {
-	public SQLQuery setResultSetMapping(String name) {
-	public SQLQuery addSynchronizedQuerySpace(String querySpace) {
-	public SQLQuery addSynchronizedEntityName(String entityName) {
-	public SQLQuery addSynchronizedEntityClass(Class entityClass) {
-	public int executeUpdate() throws HibernateException {
-		public RootReturn setLockMode(LockMode lockMode) {
-		public RootReturn setDiscriminatorAlias(String alias) {
-		public RootReturn addProperty(String propertyName, String columnAlias) {
-		public ReturnProperty addProperty(final String propertyName) {
-				public ReturnProperty addColumnAlias(String columnAlias) {
-		public NativeSQLQueryReturn buildReturn() {
-		public FetchReturn setLockMode(LockMode lockMode) {
-		public FetchReturn addProperty(String propertyName, String columnAlias) {
-		public ReturnProperty addProperty(final String propertyName) {
-				public ReturnProperty addColumnAlias(String columnAlias) {
-		public NativeSQLQueryReturn buildReturn() {
-			public SessionFactoryImplementor getSessionFactory() {
-			public MetadataImplementor getMetadata() {
-					public void registerOnCloseAction(DelayedDropAction action) {
-	public Properties getProperties() {
+	public Map<String, Object> getProperties() {
+	public String getName() {
+	public JdbcServices getJdbcServices() {
-	public Map<String, EntityPersister> getEntityPersisters() {
+	public DeserializationResolver getDeserializationResolver() {
+			public SessionFactoryImplementor resolve() {
-	public EntityPersister getEntityPersister(String entityName) throws MappingException {
+	public Settings getSettings() {
-	public EntityPersister locateEntityPersister(Class byClass) {
+	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
-	public EntityPersister locateEntityPersister(String byName) {
+	public EntityManager createEntityManager() {
-	public DeserializationResolver getDeserializationResolver() {
-			public SessionFactoryImplementor resolve() {
+	public EntityManager createEntityManager(Map map) {
-	public Map<String, CollectionPersister> getCollectionPersisters() {
-	public CollectionPersister getCollectionPersister(String role) throws MappingException {
+	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
-	public Settings getSettings() {
+	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
-	public SessionFactoryOptions getSessionFactoryOptions() {
+	public CriteriaBuilder getCriteriaBuilder() {
-	public JdbcServices getJdbcServices() {
+	public MetamodelImplementor getMetamodel() {
-	public Dialect getDialect() {
+	public boolean isOpen() {
-	public Interceptor getInterceptor() {
+	public EntityGraphImplementor findEntityGraphByName(String name) {
-	public SQLExceptionConverter getSQLExceptionConverter() {
+	public Map getAllSecondLevelCacheRegions() {
-	public SqlExceptionHelper getSQLExceptionHelper() {
+	public SessionFactoryOptions getSessionFactoryOptions() {
-	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
+	public Interceptor getInterceptor() {
-	public NamedQueryRepository getNamedQueryRepository() {
+	public org.hibernate.query.spi.NamedQueryRepository getNamedQueryRepository() {
-	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
-	public NamedQueryDefinition getNamedQuery(String queryName) {
-	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
-	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
-	public ResultSetMappingDefinition getResultSetMapping(String mappingName) {
-	public String[] getImplementors(String className) throws MappingException {
-	public String getImportedClassName(String className) {
-	public Cache getCache() {
+	public CacheImplementor getCache() {
-	public void evictEntity(String entityName, Serializable id) throws HibernateException {
-	public void evictEntity(String entityName) throws HibernateException {
-	public void evict(Class persistentClass, Serializable id) throws HibernateException {
-	public void evict(Class persistentClass) throws HibernateException {
-	public void evictCollection(String roleName, Serializable id) throws HibernateException {
-	public void evictCollection(String roleName) throws HibernateException {
-	public void evictQueries() throws HibernateException {
-	public void evictQueries(String regionName) throws HibernateException {
-	public UpdateTimestampsCache getUpdateTimestampsCache() {
-	public QueryCache getQueryCache() {
+	public PersistenceUnitUtil getPersistenceUnitUtil() {
-	public QueryCache getQueryCache(String regionName) throws HibernateException {
+	public void addNamedQuery(String name, Query query) {
-	public Region getSecondLevelCacheRegion(String regionName) {
-	public RegionAccessStrategy getSecondLevelCacheRegionAccessStrategy(String regionName) {
-	public Region getNaturalIdCacheRegion(String regionName) {
+	public <T> T unwrap(Class<T> cls) {
-	public RegionAccessStrategy getNaturalIdCacheRegionAccessStrategy(String regionName) {
+	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
-	public Map getAllSecondLevelCacheRegions() {
-	public Statistics getStatistics() {
-	public StatisticsImplementor getStatisticsImplementor() {
+	public StatisticsImplementor getStatistics() {
+		public SessionOwner getSessionOwner() {
+		public ExceptionMapper getExceptionMapper() {
+		public AfterCompletionAction getAfterCompletionAction() {
+		public ManagedFlushChecker getManagedFlushChecker() {
+		public Connection getConnection() {
+		public Interceptor getInterceptor() {
+		public StatementInspector getStatementInspector() {
+		public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
+		public String getTenantIdentifier() {
+		public PersistenceUnitTransactionType getPersistenceUnitTransactionType() {
+		public SynchronizationType getSynchronizationType() {
+		public boolean isClearStateOnCloseEnabled() {
+		public boolean isFlushBeforeCompletionEnabled() {
-		public SessionBuilder owner(SessionOwner sessionOwner) {
+		public T owner(SessionOwner sessionOwner) {
-		public SessionBuilder interceptor(Interceptor interceptor) {
+		public T interceptor(Interceptor interceptor) {
-		public SessionBuilder noInterceptor() {
+		public T noInterceptor() {
-		public SessionBuilder statementInspector(StatementInspector statementInspector) {
+		public T statementInspector(StatementInspector statementInspector) {
-		public SessionBuilder connection(Connection connection) {
+		public T connection(Connection connection) {
-		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
+		public T connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
-		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
+		public T connectionHandlingMode(PhysicalConnectionHandlingMode connectionHandlingMode) {
+		public T autoJoinTransactions(boolean autoJoinTransactions) {
-		public SessionBuilder autoClose(boolean autoClose) {
+		public T autoClose(boolean autoClose) {
-		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
+		public T flushBeforeCompletion(boolean flushBeforeCompletion) {
-		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
+		public T tenantIdentifier(String tenantIdentifier) {
-		public SessionBuilder eventListeners(SessionEventListener... listeners) {
+		public T eventListeners(SessionEventListener... listeners) {
-		public SessionBuilder clearEventListeners() {
+		public T clearEventListeners() {
+		public T persistenceUnitTransactionType(PersistenceUnitTransactionType persistenceUnitTransactionType) {
-	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
+	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder, SessionCreationOptions {
+		public SessionOwner getSessionOwner() {
+		public ExceptionMapper getExceptionMapper() {
+		public AfterCompletionAction getAfterCompletionAction() {
+		public ManagedFlushChecker getManagedFlushChecker() {
+		public Connection getConnection() {
+		public Interceptor getInterceptor() {
+		public StatementInspector getStatementInspector() {
+		public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
+		public String getTenantIdentifier() {
+		public PersistenceUnitTransactionType getPersistenceUnitTransactionType() {
+		public SynchronizationType getSynchronizationType() {
+		public boolean isClearStateOnCloseEnabled() {
+		public boolean isFlushBeforeCompletionEnabled() {
-public final class SessionImpl extends AbstractSessionImpl implements EventSource {
+public final class SessionImpl
+	public SessionImpl(SessionFactoryImpl factory, SessionCreationOptions options) {
-				public String inspect(String sql) {
+						public void afterBegin() {
+						public void beforeCompletion() {
+						public void afterCompletion(boolean successful, boolean delayed) {
-				public void afterBegin() {
-				public void beforeCompletion() {
-				public void afterCompletion(boolean successful, boolean delayed) {
+//				public String inspect(String sql) {
+//				public void afterBegin() {
+//				public void beforeCompletion() {
+//				public void afterCompletion(boolean successful, boolean delayed) {
-	public long getTimestamp() {
-	public boolean isConnected() {
-	public boolean isTransactionInProgress() {
+	public void setFlushMode(FlushModeType flushModeType) {
-	public Query createFilter(Object collection, String queryString) {
+	public org.hibernate.query.Query createFilter(Object collection, String queryString) {
-	public Query getNamedQuery(String queryName) throws MappingException {
-	public void setFlushMode(FlushMode flushMode) {
-	public FlushMode getFlushMode() {
-	public CacheMode getCacheMode() {
-	public void setCacheMode(CacheMode cacheMode) {
-	public Transaction beginTransaction() throws HibernateException {
-	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
+	public List listFilter(Object collection, String filter, QueryParameters queryParameters) {
-	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
+	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) {
-	public Query createQuery(String queryString) {
-	public SQLQuery createSQLQuery(String sql) {
-	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
+	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) {
-	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
+	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) {
-	public void initializeCollection(PersistentCollection collection, boolean writing)
+	public void initializeCollection(PersistentCollection collection, boolean writing) {
-	public Interceptor getInterceptor() {
-	public TransactionCoordinator getTransactionCoordinator() {
-	public JdbcCoordinator getJdbcCoordinator() {
-	public JdbcSessionContext getJdbcSessionContext() {
+	public PersistenceUnitTransactionType getTransactionType() {
+	public SynchronizationType getSynchronizationType() {
-		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
+		public T tenantIdentifier(String tenantIdentifier) {
+		public T interceptor() {
-		public SharedSessionBuilder interceptor() {
+		public T connection() {
-		public SharedSessionBuilder connection() {
+		public T connectionReleaseMode() {
-		public SharedSessionBuilder connectionReleaseMode() {
+		public T connectionHandlingMode() {
-		public SharedSessionBuilder autoJoinTransactions() {
+		public T autoJoinTransactions() {
-		public SharedSessionBuilder autoClose() {
+		public T autoClose() {
-		public SharedSessionBuilder flushBeforeCompletion() {
+		public T flushBeforeCompletion() {
-		public SharedSessionBuilder transactionContext() {
-		public SharedSessionBuilder interceptor(Interceptor interceptor) {
-		public SharedSessionBuilder noInterceptor() {
-		public SharedSessionBuilder statementInspector(StatementInspector statementInspector) {
-		public SharedSessionBuilder connection(Connection connection) {
-		public SharedSessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
-		public SharedSessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
+		public boolean isTransactionCoordinatorShared() {
-		public SharedSessionBuilder autoClose(boolean autoClose) {
+		public TransactionCoordinator getTransactionCoordinator() {
-		public SharedSessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
+		public JdbcCoordinator getJdbcCoordinator() {
-		public SharedSessionBuilder eventListeners(SessionEventListener... listeners) {
+		public TransactionImplementor getTransaction() {
-		public SessionBuilder clearEventListeners() {
+		public ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses() {
-		public String resolveEntityName(Object entity) {
-		public RuntimeException mapStatusCheckFailure(String message, SystemException systemException) {
+	public boolean isFlushBeforeCompletionEnabled() {
-		public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure) {
-		public void doAction(boolean successful) {
+	public static class ManagedFlushCheckerStandardImpl implements ManagedFlushChecker {
-		public boolean shouldDoManagedFlush(SessionImpl session) {
+		public boolean shouldDoManagedFlush(SessionImplementor session) {
+	public SessionImplementor getSession() {
+	public void handlePersistenceException(PersistenceException e) {
+	public void throwPersistenceException(PersistenceException e) {
+	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
+	public PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
+	public RuntimeException convert(HibernateException e) {
+	public RuntimeException convert(RuntimeException e) {
+	public RuntimeException convert(RuntimeException e, LockOptions lockOptions) {
+	public void throwPersistenceException(HibernateException e) {
+	public PersistenceException wrapStaleStateException(StaleStateException e) {
+	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
+	public <T> QueryImplementor<T> createQuery(
+	public void remove(Object entity) {
+	public <T> T find(Class<T> entityClass, Object primaryKey) {
+	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
+	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockModeType) {
+	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
+	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
+	public void lock(Object entity, LockModeType lockModeType) {
+	public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
+	public void refresh(Object entity, Map<String, Object> properties) {
+	public void refresh(Object entity, LockModeType lockModeType) {
+	public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
+	public void detach(Object entity) {
+	public LockModeType getLockMode(Object entity) {
+	public void setProperty(String propertyName, Object value) {
+	public Map<String, Object> getProperties() {
+	public <T> QueryImplementor<T> createQuery(CriteriaQuery<T> criteriaQuery) {
+	public QueryImplementor createQuery(CriteriaUpdate criteriaUpdate) {
+	public QueryImplementor createQuery(CriteriaDelete criteriaDelete) {
+	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
+	public void joinTransaction() {
+	public boolean isJoinedToTransaction() {
+	public <T> T unwrap(Class<T> clazz) {
+	public Object getDelegate() {
+	public SessionFactoryImplementor getEntityManagerFactory() {
+	public CriteriaBuilder getCriteriaBuilder() {
+	public MetamodelImplementor getMetamodel() {
+	public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
+	public EntityGraph<?> createEntityGraph(String graphName) {
+	public EntityGraph<?> getEntityGraph(String graphName) {
+	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
-public class StatelessSessionImpl extends AbstractSessionImpl implements StatelessSession {
+public class StatelessSessionImpl extends AbstractSharedSessionContract implements StatelessSession {
-					public String inspect(String sql) {
-	public TransactionCoordinator getTransactionCoordinator() {
-	public JdbcCoordinator getJdbcCoordinator() {
-	public boolean isOpen() {
-	public void close() {
-	public SessionEventListenerManager getEventListenerManager() {
-	public int executeUpdate(String query, QueryParameters queryParameters)
+	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
+	public void setCacheMode(CacheMode cm) {
+	public void setFlushMode(FlushMode fm) {
+	public void setHibernateFlushMode(FlushMode flushMode) {
+	public String guessEntityName(Object entity) throws HibernateException {
-	public FlushMode getFlushMode() {
-	public Interceptor getInterceptor() {
-	public long getTimestamp() {
-	public String guessEntityName(Object entity) throws HibernateException {
-	public boolean isConnected() {
-	public boolean isTransactionInProgress() {
-	public void setCacheMode(CacheMode cm) {
-	public void setFlushMode(FlushMode fm) {
-	public Transaction beginTransaction() throws HibernateException {
-	public JdbcSessionContext getJdbcSessionContext() {
-	public T accept(WorkExecutor<T> executor, Connection connection) throws SQLException;
-public interface HibernateEntityManager extends EntityManager {
+public interface HibernateEntityManager extends EntityManager, QueryProducer {
-	public Session getSession();
-	public EntityManagerFactoryImpl getFactory() {
+	public SessionFactoryImplementor getFactory() {
-	public HibernateEntityManagerFactory getFactory() {
+	public SessionFactoryImplementor getFactory() {
-	public <T> SubgraphImpl<T> makeSubgraph() {
+	public <X> SubgraphImpl<X> makeSubgraph() {
-	public <T> SubgraphImpl<T> makeKeySubgraph() {
+	public <X> SubgraphImpl<X> makeKeySubgraph() {
-	public SubgraphImpl internalMakeKeySubgraph(Class type) {
-public class EntityGraphImpl<T> extends AbstractGraphNode<T> implements EntityGraph<T>, GraphNodeImplementor {
+public class EntityGraphImpl<T> extends AbstractGraphNode<T> implements EntityGraph<T>, EntityGraphImplementor<T> {
-	public EntityGraphImpl(String name, EntityType<T> entityType, EntityManagerFactoryImpl entityManagerFactory) {
+	public EntityGraphImpl(String name, EntityType<T> entityType, SessionFactoryImplementor sessionFactory) {
-	public void addAttributeNodes(Attribute<T, ?>... attributes) {
+	public final void addAttributeNodes(Attribute<T, ?>... attributes) {
-	public <T1 extends Object> Subgraph<? extends T1> addSubclassSubgraph(Class<? extends T1> type) {
+	public <X> Subgraph<? extends X> addSubclassSubgraph(Class<? extends X> type) {
-	public void addAttributeNodes(Attribute<T, ?>... attributes) {
+	public final void addAttributeNodes(Attribute<T, ?>... attributes) {
+public class ManagedFlushCheckerLegacyJpaImpl implements ManagedFlushChecker {
+	public static final ManagedFlushCheckerLegacyJpaImpl INSTANCE = new ManagedFlushCheckerLegacyJpaImpl();
+public class PersistenceUnitUtilImpl implements PersistenceUnitUtil, Serializable {
+	public PersistenceUnitUtilImpl(SessionFactoryImplementor sessionFactory) {
-public class StoredProcedureQueryImpl extends BaseQueryImpl implements StoredProcedureQuery {
-	public StoredProcedureQueryImpl(ProcedureCall procedureCall, HibernateEntityManagerImplementor entityManager) {
-	public StoredProcedureQueryImpl(ProcedureCallMemento memento, HibernateEntityManagerImplementor entityManager) {
-	public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
-	public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
-	public <T> StoredProcedureQueryImpl setParameter(Parameter<T> param, T value) {
-	public StoredProcedureQueryImpl setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
-	public StoredProcedureQueryImpl setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
-	public StoredProcedureQueryImpl setParameter(String name, Object value) {
-	public StoredProcedureQueryImpl setParameter(String name, Calendar value, TemporalType temporalType) {
-	public StoredProcedureQueryImpl setParameter(String name, Date value, TemporalType temporalType) {
-	public StoredProcedureQueryImpl setParameter(int position, Object value) {
-	public StoredProcedureQueryImpl setParameter(int position, Calendar value, TemporalType temporalType) {
-	public StoredProcedureQueryImpl setParameter(int position, Date value, TemporalType temporalType) {
-	public StoredProcedureQueryImpl setFlushMode(FlushModeType jpaFlushMode) {
-	public StoredProcedureQueryImpl setHint(String hintName, Object value) {
-	public boolean execute() {
-	public int executeUpdate() {
-	public Object getOutputParameterValue(int position) {
-	public Object getOutputParameterValue(String parameterName) {
-	public boolean hasMoreResults() {
-	public int getUpdateCount() {
-	public List getResultList() {
-	public Object getSingleResult() {
-	public <T> T unwrap(Class<T> cls) {
-	public Query setLockMode(LockModeType lockMode) {
-	public LockModeType getLockMode() {
-	public ProcedureCall getHibernateProcedureCall() {
-		public ParameterRegistrationImpl(
-		public String getName() {
-		public Integer getPosition() {
-		public Class<T> getParameterType() {
-		public boolean isJpaPositionalParameter() {
-		public Query getQuery() {
-		public ParameterMode getMode() {
-		public boolean isPassNullsEnabled() {
-		public void enablePassingNulls(boolean enabled) {
-		public boolean isBindable() {
-		public void bindValue(T value) {
-		public void bindValue(T value, TemporalType specifiedTemporalType) {
-					public T getValue() {
-					public TemporalType getSpecifiedTemporalType() {
-		public ParameterBind<T> getBind() {
+	public static FlushModeType getFlushModeType(FlushMode flushMode) {
+	public static FlushMode getFlushMode(FlushModeType flushModeType) {
+	public static FlushMode interpretFlushMode(Object value) {
+	public static FlushMode interpretExternalSetting(String externalName) {
-public abstract class AbstractEntityManagerImpl implements HibernateEntityManagerImplementor, Serializable {
-	public PersistenceUnitTransactionType getTransactionType() {
-	public SynchronizationType getSynchronizationType() {
-	public Query createQuery(String jpaqlString) {
-	public <T> TypedQuery<T> createQuery(String jpaqlString, Class<T> resultClass) {
-	public static class TupleBuilderTransformer extends BasicTransformerAdapter {
-		public TupleBuilderTransformer(org.hibernate.Query hqlQuery) {
-		public Object transformTuple(Object[] tuple, String[] aliases) {
-		public static class HqlTupleElementImpl<X> implements TupleElement<X> {
-			public HqlTupleElementImpl(int position, String alias, Type hibernateType) {
-			public Class getJavaType() {
-			public String getAlias() {
-			public int getPosition() {
-			public Type getHibernateType() {
-		public class HqlTupleImpl implements Tuple {
-			public HqlTupleImpl(Object[] tuple) {
-			public <X> X get(String alias, Class<X> type) {
-			public Object get(String alias) {
-			public <X> X get(int i, Class<X> type) {
-			public Object get(int i) {
-			public Object[] toArray() {
-			public List<TupleElement<?>> getElements() {
-			public <X> X get(TupleElement<X> tupleElement) {
-	public <T> QueryImpl<T> createQuery(
-		public Object transformTuple(Object[] tuple, String[] aliases) {
-			public <X> X get(TupleElement<X> tupleElement) {
-			public Object get(String alias) {
-			public <X> X get(String alias, Class<X> type) {
-			public Object get(int i) {
-			public <X> X get(int i, Class<X> type) {
-			public Object[] toArray() {
-			public List<TupleElement<?>> getElements() {
-	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
-	public Query createQuery(CriteriaUpdate criteriaUpdate) {
-	public Query createQuery(CriteriaDelete criteriaDelete) {
-	public Query createNamedQuery(String name) {
-	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
-	public Query createNativeQuery(String sqlString) {
-	public Query createNativeQuery(String sqlString, Class resultClass) {
-	public Query createNativeQuery(String sqlString, String resultSetMapping) {
-	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
-	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
-	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
-	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
-	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
-	public <A> A find(Class<A> entityClass, Object primaryKey) {
-	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
-	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType) {
-	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
-	public CacheMode determineAppropriateLocalCacheMode(Map<String, Object> localProperties) {
-	public void persist(Object entity) {
-	public <A> A merge(A entity) {
-	public void remove(Object entity) {
-	public void refresh(Object entity) {
-	public void refresh(Object entity, Map<String, Object> properties) {
-	public void refresh(Object entity, LockModeType lockModeType) {
-	public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
-	public boolean contains(Object entity) {
-	public LockModeType getLockMode(Object entity) {
-	public void setProperty(String s, Object o) {
-	public Map<String, Object> getProperties() {
-	public void flush() {
-	public abstract Session getSession();
-	public EntityTransaction getTransaction() {
-	public EntityManagerFactoryImpl getEntityManagerFactory() {
-	public HibernateEntityManagerFactory getFactory() {
-	public CriteriaBuilder getCriteriaBuilder() {
-	public Metamodel getMetamodel() {
-	public void setFlushMode(FlushModeType flushModeType) {
-	public void clear() {
-	public void detach(Object entity) {
-	public FlushModeType getFlushMode() {
-	public void lock(Object entity, LockModeType lockMode) {
-	public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
-	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
-	public boolean isTransactionInProgress() {
-	public <T> T unwrap(Class<T> clazz) {
-	public void markForRollbackOnly() {
-	public boolean isJoinedToTransaction() {
-	public void joinTransaction() {
-	public Object getDelegate() {
-	public void handlePersistenceException(PersistenceException e) {
-	public void throwPersistenceException(PersistenceException e) {
-	public RuntimeException convert(HibernateException e) {
-	public RuntimeException convert(RuntimeException e) {
-	public RuntimeException convert(RuntimeException e, LockOptions lockOptions) {
-	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
-	public void throwPersistenceException(HibernateException e) {
-	public PersistenceException wrapStaleStateException(StaleStateException e) {
-	public PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
+public class CriteriaQueryTupleTransformer extends BasicTransformerAdapter {
+	public CriteriaQueryTupleTransformer(List<ValueHandlerFactory.ValueHandler> valueHandlers, List tupleElements) {
-	public void checkOpen(boolean markForRollbackIfClosed) throws IllegalStateException;
-	public void markForRollbackOnly();
-	public void handlePersistenceException(PersistenceException e);
-	public void throwPersistenceException(PersistenceException e);
-	public RuntimeException convert(HibernateException e, LockOptions lockOptions);
-	public RuntimeException convert(HibernateException e);
-	public void throwPersistenceException(HibernateException e);
-	public PersistenceException wrapStaleStateException(StaleStateException e);
-	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties);
-	public static interface QueryOptions {
-		public static interface ResultMetadataValidator {
-			public void validate(Type[] returnTypes);
-		public ResultMetadataValidator getResultMetadataValidator();
-		public List<ValueHandlerFactory.ValueHandler> getValueHandlers();
-		public Map<String, Class> getNamedParameterExplicitTypes();
-	public <T> QueryImpl<T> createQuery(String jpaqlString, Class<T> resultClass, Selection selection, QueryOptions queryOptions);
-							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
+							public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
-	public void initialize(Serializable id, SessionImplementor session) throws HibernateException;
-	public void initialize(Serializable id, SessionImplementor session)
+	public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public void initialize(Serializable id, SessionImplementor session) throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public void initialize(Serializable id, SessionImplementor session)	throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session)	throws HibernateException {
-		public void initialize(Serializable id, SessionImplementor session)	throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session)	throws HibernateException {
-	public void initialize(Serializable id, SessionImplementor session)
+	public void initialize(Serializable id, SharedSessionContractImplementor session)
-	public void initialize(Serializable id, SessionImplementor session) throws HibernateException {
+	public void initialize(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-	public void initialize(Serializable id, SessionImplementor session)
+	public void initialize(Serializable id, SharedSessionContractImplementor session)
-		public void initialize(Serializable id, SessionImplementor session)	throws HibernateException {
+		public void initialize(Serializable id, SharedSessionContractImplementor session)	throws HibernateException {
-	public ScrollableResults scroll(SessionImplementor session, ScrollMode scrollMode)
+	public ScrollableResults scroll(SharedSessionContractImplementor session, ScrollMode scrollMode)
-	public List list(SessionImplementor session)
+	public List list(SharedSessionContractImplementor session)
-							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
+							public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
-	public Object extract(Object[] data, ResultSet resultSet, SessionImplementor session)
+	public Object extract(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session)
-	public List list(SessionImplementor session, QueryParameters queryParameters) throws HibernateException {
+	public List list(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
-					public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
+					public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
-	public ScrollableResults scroll(final QueryParameters queryParameters, final SessionImplementor session)
+	public ScrollableResults scroll(final QueryParameters queryParameters, final SharedSessionContractImplementor session)
-	public Object extract(Object[] data, ResultSet resultSet, SessionImplementor session)
+	public Object extract(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session)
-	public void performDiscovery(JdbcResultMetadata metadata, List<Type> types, List<String> aliases)
-	public Object extract(Object[] data, ResultSet resultSet, SessionImplementor session)
-	public Object buildResultRow(Object[] data, ResultSet resultSet, boolean hasTransformer, SessionImplementor session)
+	public Object buildResultRow(Object[] data, ResultSet resultSet, boolean hasTransformer, SharedSessionContractImplementor session)
-	public Object[] buildResultRow(Object[] data, ResultSet resultSet, SessionImplementor session)
+	public Object[] buildResultRow(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session)
-	public Object extract(Object[] data, ResultSet resultSet, SessionImplementor session)
+	public Object extract(Object[] data, ResultSet resultSet, SharedSessionContractImplementor session)
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
-	public Object loadElement(SessionImplementor session, Object key, Object index)
+	public Object loadElement(SharedSessionContractImplementor session, Object key, Object index)
-	public Object loadByUniqueKey(SessionImplementor session,Object key) {
+	public Object loadByUniqueKey(SharedSessionContractImplementor session, Object key) {
-		public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
+		public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
-		public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
+		public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session) throws HibernateException;
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions);
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session) throws HibernateException {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) throws HibernateException {
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
-		public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
+		public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
-//		public LockMode getLockMode() {
+//		public LockMode getHibernateFlushMode() {
-	public SessionImplementor getSession() {
+	public SharedSessionContractImplementor getSession() {
-	public static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
+	public static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SharedSessionContractImplementor session) {
-	public SessionImplementor getSession();
-	public QueryParameters getQueryParameters();
-	public boolean shouldUseOptionalEntityInformation();
-	public boolean shouldReturnProxies();
-	public LoadPlan getLoadPlan();
-	public static interface EntityReferenceProcessingState {
-		public EntityReference getEntityReference();
-		public void registerMissingIdentifier();
-		public boolean isMissingIdentifier();
-		public void registerIdentifierHydratedForm(Object hydratedForm);
-		public Object getIdentifierHydratedForm();
-		public void registerEntityKey(EntityKey entityKey);
-		public EntityKey getEntityKey();
-		public void registerHydratedState(Object[] hydratedState);
-		public Object[] getHydratedState();
-		public void registerEntityInstance(Object instance);
-		public Object getEntityInstance();
-	public EntityReferenceProcessingState getProcessingState(EntityReference entityReference);
-	public EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch);
-	public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance);
-	public static interface EntityKeyResolutionContext {
-		public EntityPersister getEntityPersister();
-		public LockMode getLockMode();
-		public EntityReference getEntityReference();
-	public void afterLoad(SessionImplementor session, Object entity, Loadable persister);
-		public Serializable locateGenerationContext(SessionImplementor session, Object incomingObject) {
+		public Serializable locateGenerationContext(SharedSessionContractImplementor session, Object incomingObject) {
-		public void execute(SessionImplementor session, Object incomingObject, Object injectionContext) {
+		public void execute(SharedSessionContractImplementor session, Object incomingObject, Object injectionContext) {
+public enum JpaMetaModelPopulationSetting {
+	public static JpaMetaModelPopulationSetting parse(String setting) {
+	public static JpaMetaModelPopulationSetting determineJpaMetaModelPopulationSetting(Map configurationValues) {
-	public static MetamodelImpl buildMetamodel(
+			public SessionFactoryImplementor getSessionFactory() {
+			public MetadataImplementor getMetadata() {
+	public java.util.Collection<EntityNameResolver> getEntityNameResolvers() {
+	public CollectionPersister collectionPersister(String role) {
+	public Map<String, CollectionPersister> collectionPersisters() {
+	public EntityPersister entityPersister(Class entityClass) {
+	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
+	public String[] getAllEntityNames() {
+	public String[] getAllCollectionRoles() {
+	public void close() {
-	public int getSourceLine();
-	public int getSourceColumn();
-	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position) throws SQLException;
-	public Type getExpectedType();
-	public void setExpectedType(Type expectedType);
-	public String renderDisplayInfo();
-	public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position) throws SQLException {
+	public int bind(PreparedStatement statement, QueryParameters qp, SharedSessionContractImplementor session, int position) throws SQLException {
-	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
+	public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
-	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
+	public Object readElement(ResultSet rs, Object owner, String[] aliases, SharedSessionContractImplementor session)
-	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
+	public Object readIndex(ResultSet rs, String[] aliases, SharedSessionContractImplementor session)
-	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
+	public Object readIdentifier(ResultSet rs, String alias, SharedSessionContractImplementor session)
-	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
+	public Object readKey(ResultSet rs, String[] aliases, SharedSessionContractImplementor session)
-	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
+	public int writeIdentifier(PreparedStatement st, Object id, int i, SharedSessionContractImplementor session)
-	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
+	public void remove(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void recreate(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void deleteRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void insertRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void updateRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
+	public void processQueuedOps(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
-	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
+	public boolean isAffectedByEnabledFilters(SharedSessionContractImplementor session) {
-	public int getSize(Serializable key, SessionImplementor session) {
+	public int getSize(Serializable key, SharedSessionContractImplementor session) {
-	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
+	public boolean indexExists(Serializable key, Object index, SharedSessionContractImplementor session) {
-	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
+	public boolean elementExists(Serializable key, Object element, SharedSessionContractImplementor session) {
-	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
+	public Object getElementByIndex(Serializable key, Object index, SharedSessionContractImplementor session, Object owner) {
-	public void initialize(Serializable key, SessionImplementor session) //TODO: add owner argument!!
-	public boolean hasCache();
-	public CollectionRegionAccessStrategy getCacheAccessStrategy();
-	public CacheEntryStructure getCacheEntryStructure();
-	public CollectionType getCollectionType();
-	public Type getKeyType();
-	public Type getIndexType();
-	public Type getElementType();
-	public Class getElementClass();
-	public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
-	public Object readElement(
-	public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
-	public Object readIdentifier(
-	public boolean isPrimitiveArray();
-	public boolean isArray();
-	public boolean isOneToMany();
-	public boolean isManyToMany();
-	public String getManyToManyFilterFragment(String alias, Map enabledFilters);
-	public boolean hasIndex();
-	public boolean isLazy();
-	public boolean isInverse();
-	public void remove(Serializable id, SessionImplementor session)
-	public void recreate(
-	public void deleteRows(
-	public void updateRows(
-	public void insertRows(
-	public void processQueuedOps(
-	public String getRole();
-	public EntityPersister getOwnerEntityPersister();
-	public IdentifierGenerator getIdentifierGenerator();
-	public Type getIdentifierType();
-	public boolean hasOrphanDelete();
-	public boolean hasOrdering();
-	public boolean hasManyToManyOrdering();
-	public Serializable[] getCollectionSpaces();
-	public CollectionMetadata getCollectionMetadata();
-	public abstract boolean isCascadeDeleteEnabled();
-	public boolean isVersioned();
-	public boolean isMutable();
-	public void postInstantiate() throws MappingException;
-	public SessionFactoryImplementor getFactory();
-	public boolean isAffectedByEnabledFilters(SessionImplementor session);
-	public String[] getKeyColumnAliases(String suffix);
-	public String[] getIndexColumnAliases(String suffix);
-	public String[] getElementColumnAliases(String suffix);
-	public String getIdentifierColumnAlias(String suffix);
-	public boolean isExtraLazy();
-	public int getSize(Serializable key, SessionImplementor session);
-	public boolean indexExists(Serializable key, Object index, SessionImplementor session);
-	public boolean elementExists(Serializable key, Object element, SessionImplementor session);
-	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner);
-	public int getBatchSize();
-	public String getMappedByProperty();
-	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
+	public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
-	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void recreate(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
+	public void insertRows(PersistentCollection collection, Serializable id, SharedSessionContractImplementor session)
-	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
+	public Object getElementByIndex(Serializable key, Object index, SharedSessionContractImplementor session, Object owner) {
-	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session) {
+	public Object initializeLazyProperty(String fieldName, Object entity, SharedSessionContractImplementor session) {
-	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
+	public Object[] getDatabaseSnapshot(Serializable id, SharedSessionContractImplementor session)
-	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session)
+	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SharedSessionContractImplementor session)
-	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
+	public Object forceVersionIncrement(Serializable id, Object currentVersion, SharedSessionContractImplementor session) {
-	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
+	public Object getCurrentVersion(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
+	public Serializable insert(Object[] fields, Object object, SharedSessionContractImplementor session)
-	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
+	public void insert(Serializable id, Object[] fields, Object object, SharedSessionContractImplementor session) {
-	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
+	public void delete(Serializable id, Object version, Object object, SharedSessionContractImplementor session)
-	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
+	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SharedSessionContractImplementor session) {
-	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
+	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SharedSessionContractImplementor session)
-	public List multiLoad(Serializable[] ids, SessionImplementor session, MultiLoadOptions loadOptions) {
+	public List multiLoad(Serializable[] ids, SharedSessionContractImplementor session, MultiLoadOptions loadOptions) {
-	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
+	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SharedSessionContractImplementor session)
-	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
+	public int[] findModified(Object[] old, Object[] current, Object entity, SharedSessionContractImplementor session)
-	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
+	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
-	public void afterReassociate(Object entity, SessionImplementor session) {
+	public void afterReassociate(Object entity, SharedSessionContractImplementor session) {
-	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
+	public Boolean isTransient(Object entity, SharedSessionContractImplementor session) throws HibernateException {
-	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
+	public Object createProxy(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-	public void afterInitialize(Object entity, SessionImplementor session) {
+	public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
-	public Serializable getIdentifier(Object entity, SessionImplementor session) {
+	public Serializable getIdentifier(Object entity, SharedSessionContractImplementor session) {
-	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
+	public void setIdentifier(Object entity, Serializable id, SharedSessionContractImplementor session) {
-	public Object instantiate(Serializable id, SessionImplementor session) {
+	public Object instantiate(Serializable id, SharedSessionContractImplementor session) {
-	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
+	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SharedSessionContractImplementor session)
-	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session)
+	public Object[] getNaturalIdentifierSnapshot(Serializable id, SharedSessionContractImplementor session)
-		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
+		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
-		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
+		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
-		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
+		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
-		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
+		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SharedSessionContractImplementor session) {
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache)
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
-	public static final String ENTITY_ID = "id";
-	public void generateEntityDefinition();
-	public void postInstantiate() throws MappingException;
-	public SessionFactoryImplementor getFactory();
-	public EntityEntryFactory getEntityEntryFactory();
-	public String getRootEntityName();
-	public String getEntityName();
-	public EntityMetamodel getEntityMetamodel();
-	public boolean isSubclassEntityName(String entityName);
-	public Serializable[] getPropertySpaces();
-	public Serializable[] getQuerySpaces();
-	public boolean hasProxy();
-	public boolean hasCollections();
-	public boolean hasMutableProperties();
-	public boolean hasSubselectLoadableCollections();
-	public boolean hasCascades();
-	public boolean isMutable();
-	public boolean isInherited();
-	public boolean isIdentifierAssignedByInsert();
-	public Type getPropertyType(String propertyName) throws MappingException;
-	public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session);
-	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session);
-	public boolean hasIdentifierProperty();
-	public boolean canExtractIdOutOfEntity();
-	public boolean isVersioned();
-	public VersionType getVersionType();
-	public int getVersionProperty();
-	public boolean hasNaturalIdentifier();
-	public int[] getNaturalIdentifierProperties();
-	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session);
-	public IdentifierGenerator getIdentifierGenerator();
-	public boolean hasLazyProperties();
-	public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
-	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
-	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
-	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
-	public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
-	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
-	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
-	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
-	public void update(
-	public Type[] getPropertyTypes();
-	public String[] getPropertyNames();
-	public boolean[] getPropertyInsertability();
-	public ValueInclusion[] getPropertyInsertGenerationInclusions();
-	public ValueInclusion[] getPropertyUpdateGenerationInclusions();
-	public boolean[] getPropertyUpdateability();
-	public boolean[] getPropertyCheckability();
-	public boolean[] getPropertyNullability();
-	public boolean[] getPropertyVersionability();
-	public boolean[] getPropertyLaziness();
-	public CascadeStyle[] getPropertyCascadeStyles();
-	public Type getIdentifierType();
-	public String getIdentifierPropertyName();
-	public boolean isCacheInvalidationRequired();
-	public boolean isLazyPropertiesCacheable();
-	public boolean hasCache();
-	public EntityRegionAccessStrategy getCacheAccessStrategy();
-	public CacheEntryStructure getCacheEntryStructure();
-	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
-	public boolean hasNaturalIdCache();
-	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy();
-	public ClassMetadata getClassMetadata();
-	public boolean isBatchLoadable();
-	public boolean isSelectBeforeUpdateRequired();
-	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
-	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session);
-	public Object getCurrentVersion(Serializable id, SessionImplementor session)
-	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
-	public boolean isInstrumented();
-	public boolean hasInsertGeneratedProperties();
-	public boolean hasUpdateGeneratedProperties();
-	public boolean isVersionPropertyGenerated();
-	public void afterReassociate(Object entity, SessionImplementor session);
-	public Object createProxy(Serializable id, SessionImplementor session)
-	public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException;
-	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException;
-	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
-	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
-	public Class getMappedClass();
-	public boolean implementsLifecycle();
-	public Class getConcreteProxyClass();
-	public void setPropertyValues(Object object, Object[] values);
-	public void setPropertyValue(Object object, int i, Object value);
-	public Object[] getPropertyValues(Object object);
-	public Object getPropertyValue(Object object, int i) throws HibernateException;
-	public Object getPropertyValue(Object object, String propertyName);
-	public Serializable getIdentifier(Object object) throws HibernateException;
-	public Serializable getIdentifier(Object entity, SessionImplementor session);
-	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
-	public Object getVersion(Object object) throws HibernateException;
-	public Object instantiate(Serializable id, SessionImplementor session);
-	public boolean isInstance(Object object);
-	public boolean hasUninitializedLazyProperties(Object object);
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
-	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory);
-	public EntityMode getEntityMode();
-	public EntityTuplizer getEntityTuplizer();
-	public BytecodeEnhancementMetadata getInstrumentationMetadata();
-	public FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
-	public int[] resolveAttributeIndexes(String[] attributeNames);
-	public boolean canUseReferenceCacheEntries();
-	public static final String ROWID_ALIAS = "rowid_";
-	public boolean hasSubclasses();
-	public Type getDiscriminatorType();
-	public Object getDiscriminatorValue();
-	public String getSubclassForDiscriminatorValue(Object value);
-	public String[] getIdentifierColumnNames();
-	public String[] getIdentifierAliases(String suffix);
-	public String[] getPropertyAliases(String suffix, int i);
-	public String[] getPropertyColumnNames(int i);
-	public String getDiscriminatorAlias(String suffix);
-	public String getDiscriminatorColumnName();
-	public boolean hasRowId();
-	public Object[] hydrate(
-	public boolean isAbstract();
-	public void registerAffectingFetchProfile(String fetchProfileName);
-	public String getTableAliasForColumn(String columnName, String rootAlias);
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions) {
-	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
+	public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session) {
-	public Object loadByUniqueKey(String propertyName, Object uniqueKey, SessionImplementor session) 
-	public int getPropertyIndex(String propertyName);
-	public ProcedureCall makeProcedureCall(Session session);
-	public ProcedureCall makeProcedureCall(SessionImplementor session);
-	public Map<String, Object> getHintsMap();
-public class ProcedureCallImpl extends AbstractBasicQueryContractImpl implements ProcedureCall, ResultContext {
+public class ProcedureCallImpl extends AbstractProducedQuery implements ProcedureCall, ResultContext {
-	public ProcedureCallImpl(SessionImplementor session, String procedureName) {
+	public ProcedureCallImpl(SharedSessionContractImplementor session, String procedureName) {
-	public ProcedureCallImpl(final SessionImplementor session, String procedureName, Class... resultClasses) {
+	public ProcedureCallImpl(final SharedSessionContractImplementor session, String procedureName, Class... resultClasses) {
-	public ProcedureCallImpl(final SessionImplementor session, String procedureName, String... resultSetMappings) {
+	public ProcedureCallImpl(final SharedSessionContractImplementor session, String procedureName, String... resultSetMappings) {
-	public SessionImplementor getSession() {
+	public SharedSessionContractImplementor getSession() {
+	public String getQueryString() {
+	public Query setEntity(int position, Object val) {
+	public Query setEntity(String name, Object val) {
-	public QueryParameters getQueryParameters() {
-	public QueryParameters buildQueryParametersObject() {
+	public QueryParameters getQueryParameters() {
-	public ProcedureCall makeProcedureCall(Session session) {
-	public ProcedureCall makeProcedureCall(SessionImplementor session) {
+	public ProcedureCall makeProcedureCall(SharedSessionContractImplementor session) {
-	public String renderCallableStatement(
-	public void registerParameters(
-		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-	public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+	public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-	public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+	public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-	public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+	public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
-	public final SessionImplementor getSession() {
+	public final SharedSessionContractImplementor getSession() {
-	public final void setSession(SessionImplementor s) throws HibernateException {
+	public final void setSession(SharedSessionContractImplementor s) throws HibernateException {
-	public final Object getImplementation(SessionImplementor s) throws HibernateException {
+	public final Object getImplementation(SharedSessionContractImplementor s) throws HibernateException {
-	public void initialize() throws HibernateException;
-	public Serializable getIdentifier();
-	public void setIdentifier(Serializable id);
-	public String getEntityName();
-	public Class getPersistentClass();
-	public boolean isUninitialized();
-	public Object getImplementation();
-	public Object getImplementation(SessionImplementor session) throws HibernateException;
-	public void setImplementation(Object target);
-	public boolean isReadOnlySettingAvailable();
-	public boolean isReadOnly();
-	public void setReadOnly(boolean readOnly);
-	public SessionImplementor getSession();
-	public void setSession(SessionImplementor session) throws HibernateException;
-	public void unsetSession();
-	public void setUnwrap(boolean unwrap);
-	public boolean isUnwrap();
-	public void postInstantiate(
-	public HibernateProxy getProxy(Serializable id,SessionImplementor session) throws HibernateException;
-	public HibernateProxy getProxy(final Serializable id, final SessionImplementor session)
+	public HibernateProxy getProxy(final Serializable id, final SharedSessionContractImplementor session) {
-public interface BasicQueryContract<T extends BasicQueryContract> {
+public interface BasicQueryContract extends org.hibernate.BasicQueryContract {
-public interface NativeQuery {
+public interface NativeQuery extends Query<Object>, SQLQuery {
+ * rather than here because it was previously the public API so we want to leave that
-public interface Query<Q extends TypedQuery,R> extends BasicQueryContract<Q>, TypedQuery<R> {
+public interface Query<R> extends TypedQuery<R>, org.hibernate.Query<R>, BasicQueryContract {
-public interface QueryParameter {
+public interface QueryParameter<T> extends javax.persistence.Parameter<T> {
-public interface HibernateCriteriaBuilder {
+public interface HibernateCriteriaBuilder extends CriteriaBuilder {
-			public Query buildCompiledQuery(
+			public QueryImplementor buildCompiledQuery(
-public class CriteriaBuilderImpl implements CriteriaBuilder, Serializable {
+public class CriteriaBuilderImpl implements HibernateCriteriaBuilder, Serializable {
-	public CriteriaBuilderImpl(EntityManagerFactoryImpl entityManagerFactory) {
+	public CriteriaBuilderImpl(SessionFactoryImpl sessionFactory) {
-	public  EntityManagerFactoryImpl getEntityManagerFactory() {
+	public  SessionFactoryImpl getEntityManagerFactory() {
-			public Query buildCompiledQuery(HibernateEntityManagerImplementor entityManager, final InterpretedParameterMetadata parameterMetadata) {
+			public QueryImplementor buildCompiledQuery(
-	public CriteriaCompiler(HibernateEntityManagerImplementor entityManager) {
+	public CriteriaCompiler(SessionImplementor entityManager) {
-	public Query compile(CompilableCriteria criteria) {
+	public QueryImplementor compile(CompilableCriteria criteria) {
-	public Query buildCompiledQuery(HibernateEntityManagerImplementor entityManager, InterpretedParameterMetadata interpretedParameterMetadata);
-	public Query getHibernateQuery() {
+	public QueryImplementor getHibernateQuery() {
-public class AbstractProducedQuery {
+public abstract class AbstractProducedQuery<R> implements QueryImplementor<R> {
+	public AbstractProducedQuery(
+	public SharedSessionContractImplementor getProducer() {
+	public FlushMode getHibernateFlushMode() {
+	public QueryImplementor setHibernateFlushMode(FlushMode flushMode) {
+	public QueryImplementor setFlushMode(FlushMode flushMode) {
+	public FlushModeType getFlushMode() {
+	public QueryImplementor setFlushMode(FlushModeType flushModeType) {
+	public CacheMode getCacheMode() {
+	public QueryImplementor setCacheMode(CacheMode cacheMode) {
+	public boolean isCacheable() {
+	public QueryImplementor setCacheable(boolean cacheable) {
+	public String getCacheRegion() {
+	public QueryImplementor setCacheRegion(String cacheRegion) {
+	public Integer getTimeout() {
+	public QueryImplementor setTimeout(int timeout) {
+	public Integer getFetchSize() {
+	public QueryImplementor setFetchSize(int fetchSize) {
+	public boolean isReadOnly() {
+	public QueryImplementor setReadOnly(boolean readOnly) {
+	public LockOptions getLockOptions() {
+	public QueryImplementor setLockOptions(LockOptions lockOptions) {
+	public QueryImplementor setLockMode(String alias, LockMode lockMode) {
+	public QueryImplementor setLockMode(LockModeType lockModeType) {
+	public String getComment() {
+	public QueryImplementor setComment(String comment) {
+	public QueryImplementor addQueryHint(String hint) {
+	public ParameterMetadata getParameterMetadata() {
+	public String[] getNamedParameters() {
+	public QueryImplementor setParameter(QueryParameter parameter, Object value) {
+	public QueryImplementor setParameter(Parameter parameter, Object value) {
+	public QueryImplementor setParameter(String name, Object value) {
+	public QueryImplementor setParameter(int position, Object value) {
+	public QueryImplementor setParameter(QueryParameter parameter, Object value, Type type) {
+	public QueryImplementor setParameter(String name, Object value, Type type) {
+	public QueryImplementor setParameter(int position, Object value, Type type) {
+	public QueryImplementor setParameter(QueryParameter parameter, Object value, TemporalType temporalType) {
+	public QueryImplementor setParameter(String name, Object value, TemporalType temporalType) {
+	public QueryImplementor setParameter(int position, Object value, TemporalType temporalType) {
+	public QueryImplementor setParameterList(QueryParameter parameter, Collection values) {
+	public QueryImplementor setParameterList(String name, Collection values) {
+	public QueryImplementor setParameterList(String name, Collection values, Type type) {
+	public QueryImplementor setParameterList(String name, Object[] values, Type type) {
+	public QueryImplementor setParameterList(String name, Object[] values) {
+	public QueryImplementor setParameter(Parameter param, Calendar value, TemporalType temporalType) {
+	public QueryImplementor setParameter(Parameter param, Date value, TemporalType temporalType) {
+	public QueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
+	public QueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
+	public QueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
+	public QueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
+	public Set<Parameter<?>> getParameters() {
+	public Parameter<?> getParameter(String name) {
+	public <T> Parameter<T> getParameter(String name, Class<T> type) {
+	public Parameter<?> getParameter(int position) {
+	public <T> Parameter<T> getParameter(int position, Class<T> type) {
+	public boolean isBound(Parameter<?> parameter) {
+	public <T> T getParameterValue(Parameter<T> parameter) {
+	public Object getParameterValue(String name) {
+	public Object getParameterValue(int position) {
+	public QueryImplementor setProperties(Object bean) {
+	public QueryImplementor setProperties(Map map) {
+	public QueryImplementor setResultTransformer(ResultTransformer transformer) {
+	public int getMaxResults() {
+	public QueryImplementor setMaxResults(int maxResult) {
+	public int getFirstResult() {
+	public QueryImplementor setFirstResult(int startPosition) {
+	public Set<String> getSupportedHints() {
+	public Map<String, Object> getHints() {
+	public QueryImplementor setHint(String hintName, Object value) {
+	public void applyEntityGraphQueryHint(EntityGraphQueryHint hint) {
+	public LockModeType getLockMode() {
+	public <T> T unwrap(Class<T> cls) {
+	public QueryParameters getQueryParameters() {
+	public Iterator<R> iterate() {
+	public ScrollableResults scroll() {
+	public ScrollableResults scroll(ScrollMode scrollMode) {
+	public List<R> list() {
+	public R uniqueResult() {
+	public static <R> R uniqueElement(List<R> list) throws NonUniqueResultException {
+	public int executeUpdate() throws HibernateException {
+	public void setOptionalEntityName(String optionalEntityName) {
+	public void setOptionalId(Serializable optionalId) {
+	public void setOptionalObject(Object optionalObject) {
+	public static final BindingTypeHelper INSTANCE = new BindingTypeHelper();
+	public BasicType determineTypeForTemporalType(TemporalType temporalType, Type baseType, Object bindValue) {
+	public BasicType resolveTimestampTemporalTypeVariant(Class javaType, Type baseType) {
+	public BasicType resolveDateTemporalTypeVariant(Class javaType, Type baseType) {
+	public BasicType resolveTimeTemporalTypeVariant(Class javaType, Type baseType) {
+public class CollectionFilterImpl extends org.hibernate.query.internal.AbstractProducedQuery {
+	public CollectionFilterImpl(
+	public String getQueryString() {
+	public Type[] getReturnTypes() {
+	public ScrollableResults scroll() throws HibernateException {
+	public Query setEntity(int position, Object val) {
+	public Query setEntity(String name, Object val) {
-public class NativeQueryImpl {
+public class NativeQueryImpl extends AbstractProducedQuery<Object> implements NativeQueryImplementor {
+	public NativeQueryImpl(
+	public NativeQueryImpl(
+	public NativeQuery setResultSetMapping(String name) {
+	public String getQueryString() {
+	public boolean isCallable() {
+	public List<NativeSQLQueryReturn> getQueryReturns() {
+	public QueryParameters getQueryParameters() {
+	public NativeQueryImplementor setCollectionKey(Serializable key) {
+	public NativeQueryImplementor addScalar(String columnAlias) {
+	public NativeQueryImplementor addScalar(String columnAlias, Type type) {
+					public NativeSQLQueryReturn buildReturn() {
+	public RootReturn addRoot(String tableAlias, String entityName) {
+	public RootReturn addRoot(String tableAlias, Class entityType) {
+	public NativeQueryImplementor addEntity(String entityName) {
+	public NativeQueryImplementor addEntity(String tableAlias, String entityName) {
+	public NativeQueryImplementor addEntity(String tableAlias, String entityName, LockMode lockMode) {
+	public NativeQueryImplementor addEntity(Class entityType) {
+	public NativeQueryImplementor addEntity(String tableAlias, Class entityClass) {
+	public NativeQueryImplementor addEntity(String tableAlias, Class entityClass, LockMode lockMode) {
+	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
+	public NativeQueryImplementor addJoin(String tableAlias, String path) {
+	public NativeQueryImplementor addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
+	public NativeQueryImplementor addJoin(String tableAlias, String path, LockMode lockMode) {
+	public String[] getReturnAliases() {
+	public Type[] getReturnTypes() {
+	public NativeQuery setEntity(int position, Object val) {
+	public NativeQuery setEntity(String name, Object val) {
+	public Collection<String> getSynchronizedQuerySpaces() {
+	public NativeQueryImplementor addSynchronizedQuerySpace(String querySpace) {
+	public NativeQueryImplementor addSynchronizedEntityName(String entityName) throws MappingException {
+	public NativeQueryImplementor addSynchronizedEntityClass(Class entityClass) throws MappingException {
+	public NativeQueryImplementor setHibernateFlushMode(FlushMode flushMode) {
+	public NativeQueryImplementor setFlushMode(FlushMode flushMode) {
+	public NativeQueryImplementor setFlushMode(FlushModeType flushModeType) {
+	public NativeQueryImplementor setCacheMode(CacheMode cacheMode) {
+	public NativeQueryImplementor setCacheable(boolean cacheable) {
+	public NativeQueryImplementor setCacheRegion(String cacheRegion) {
+	public NativeQueryImplementor setTimeout(int timeout) {
+	public NativeQueryImplementor setFetchSize(int fetchSize) {
+	public NativeQueryImplementor setReadOnly(boolean readOnly) {
+	public NativeQueryImplementor setLockOptions(LockOptions lockOptions) {
+	public NativeQueryImplementor setLockMode(String alias, LockMode lockMode) {
+	public NativeQueryImplementor setLockMode(LockModeType lockModeType) {
+	public NativeQueryImplementor setComment(String comment) {
+	public NativeQueryImplementor addQueryHint(String hint) {
+	public NativeQueryImplementor setParameter(QueryParameter parameter, Object value) {
+	public NativeQueryImplementor setParameter(Parameter parameter, Object value) {
+	public NativeQueryImplementor setParameter(String name, Object value) {
+	public NativeQueryImplementor setParameter(int position, Object value) {
+	public NativeQueryImplementor setParameter(QueryParameter parameter, Object value, Type type) {
+	public NativeQueryImplementor setParameter(String name, Object value, Type type) {
+	public NativeQueryImplementor setParameter(int position, Object value, Type type) {
+	public NativeQueryImplementor setParameter(QueryParameter parameter, Object value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameter(String name, Object value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameter(int position, Object value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameterList(QueryParameter parameter, Collection values) {
+	public NativeQueryImplementor setParameterList(String name, Collection values) {
+	public NativeQueryImplementor setParameterList(String name, Collection values, Type type) {
+	public NativeQueryImplementor setParameterList(String name, Object[] values, Type type) {
+	public NativeQueryImplementor setParameterList(String name, Object[] values) {
+	public NativeQueryImplementor setParameter(Parameter param, Calendar value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameter(Parameter param, Date value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
+	public NativeQueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
+	public NativeQueryImplementor setResultTransformer(ResultTransformer transformer) {
+	public NativeQueryImplementor setProperties(Map map) {
+	public NativeQueryImplementor setProperties(Object bean) {
+	public NativeQueryImplementor setMaxResults(int maxResult) {
+	public NativeQueryImplementor setFirstResult(int startPosition) {
+	public NativeQueryImplementor setHint(String hintName, Object value) {
-public class NativeQueryReturnBuilderFetchImpl implements SQLQuery.FetchReturn, NativeQueryReturnBuilder {
+public class NativeQueryReturnBuilderFetchImpl implements NativeQuery.FetchReturn, NativeQueryReturnBuilder {
-	public SQLQuery.FetchReturn setLockMode(LockMode lockMode) {
+	public NativeQuery.FetchReturn setLockMode(LockMode lockMode) {
-	public SQLQuery.FetchReturn addProperty(String propertyName, String columnAlias) {
+	public NativeQuery.FetchReturn addProperty(String propertyName, String columnAlias) {
-	public SQLQuery.ReturnProperty addProperty(final String propertyName) {
+	public NativeQuery.ReturnProperty addProperty(final String propertyName) {
-			public SQLQuery.ReturnProperty addColumnAlias(String columnAlias) {
+			public NativeQuery.ReturnProperty addColumnAlias(String columnAlias) {
-public class NativeQueryReturnBuilderRootImpl implements SQLQuery.RootReturn, NativeQueryReturnBuilder {
+public class NativeQueryReturnBuilderRootImpl implements NativeQuery.RootReturn, NativeQueryReturnBuilder {
-	public SQLQuery.RootReturn setLockMode(LockMode lockMode) {
+	public NativeQuery.RootReturn setLockMode(LockMode lockMode) {
-	public SQLQuery.RootReturn setDiscriminatorAlias(String alias) {
+	public NativeQuery.RootReturn setDiscriminatorAlias(String alias) {
-	public SQLQuery.RootReturn addProperty(String propertyName, String columnAlias) {
+	public NativeQuery.RootReturn addProperty(String propertyName, String columnAlias) {
-	public SQLQuery.ReturnProperty addProperty(final String propertyName) {
+	public NativeQuery.ReturnProperty addProperty(final String propertyName) {
-			public SQLQuery.ReturnProperty addColumnAlias(String columnAlias) {
+			public NativeQuery.ReturnProperty addColumnAlias(String columnAlias) {
+	public Set<QueryParameter<?>> collectAllParameters() {
+	public Set<Parameter<?>> collectAllParametersJpa() {
-	public String[] getNamedParameterNames() {
+	public Set<String> getNamedParameterNames() {
+	public <T> QueryParameter<T> resolve(Parameter<T> param) {
-public class QueryImpl {
+public class QueryImpl<R> extends AbstractProducedQuery<R> implements Query<R> {
+	public QueryImpl(
+	public String getQueryString() {
+	public Type[] getReturnTypes() {
+	public Query setEntity(int position, Object val) {
+	public Query setEntity(String name, Object val) {
-public class QueryParameterBindingImpl implements QueryParameterBinding {
+public class QueryParameterBindingImpl<T> implements QueryParameterBinding<T> {
-	public QueryParameterBindingImpl() {
+	public QueryParameterBindingImpl(Type type) {
-	public Object getBindValue() {
+	public T getBindValue() {
-	public void setBindValue(Object value) {
+	public void setBindValue(T value) {
-	public void setBindValue(Object value, Type clarifiedType) {
+	public void setBindValue(T value, Type clarifiedType) {
-	public void setBindValue(Object value, TemporalType clarifiedTemporalType) {
+	public void setBindValue(T value, TemporalType clarifiedTemporalType) {
+public class QueryParameterBindingsImpl implements QueryParameterBindings {
+	public static QueryParameterBindingsImpl from(ParameterMetadata parameterMetadata) {
+	public QueryParameterBindingsImpl() {
+	public QueryParameterBindingsImpl(Set<QueryParameter<?>> queryParameters) {
+	public boolean isBound(QueryParameter parameter) {
+	public <T> QueryParameterBinding<T> getBinding(QueryParameter<T> parameter) {
+	public <T> QueryParameterBinding<T> locateBinding(QueryParameter<T> parameter) {
+	public QueryParameterBinding getBinding(String name) {
+	public QueryParameterBinding getBinding(int position) {
+	public void verifyParametersBound(boolean reserveFirstParameter) {
+	public Collection<Type> collectBindTypes() {
+	public Collection<Object> collectBindValues() {
+	public Type[] collectPositionalBindTypes() {
+	public Object[] collectPositionalBindValues() {
+	public Map<String, TypedValue> collectNamedParameterBindings() {
+	public <T> QueryParameterListBinding<T> getQueryParameterListBinding(QueryParameter<T> queryParameter) {
+	public <T> QueryParameterListBinding<T> getQueryParameterListBinding(String name) {
+	public String expandListValuedParameters(String queryString, SharedSessionContractImplementor session) {
-public class QueryParameterImpl {
+public abstract class QueryParameterImpl<T> implements QueryParameter<T> {
+	public QueryParameterImpl(Type expectedType) {
+	public Type getType() {
+	public Class<T> getParameterType() {
-public class QueryParameterListBindingImpl {
+public class QueryParameterListBindingImpl<T> implements QueryParameterListBinding<T> {
+	public QueryParameterListBindingImpl(Type type) {
+	public void setBindValues(Collection<T> bindValues) {
+	public void setBindValues(Collection<T> values, Type clarifiedType) {
+	public void setBindValues(Collection<T> values, TemporalType clarifiedTemporalType) {
+	public Collection<T> getBindValues() {
+	public Type getBindType() {
-public class QueryParameterNamedImpl {
+public class QueryParameterNamedImpl<T> extends QueryParameterImpl<T> implements QueryParameter<T> {
+	public QueryParameterNamedImpl(String name, int[] sourceLocations, boolean jpaStyle, Type expectedType) {
+	public String getName() {
+	public Integer getPosition() {
+	public int[] getSourceLocations() {
+	public boolean isJpaPositionalParameter() {
+public interface ProcedureParameter<T> extends QueryParameter<T> {
-public interface LegacyHibernateQuery {
+public interface ProcedureParameterBinding<T> extends QueryParameterBinding<T> {
+public class ProcedureParameterBindingImpl<T> implements ProcedureParameterBindingImplementor<T> {
+	public ProcedureParameterBindingImpl(ProcedureParameterImplementor<T> parameter) {
+	public void setBindValue(T value) {
+	public void setBindValue(T value, Type clarifiedType) {
+	public void setBindValue(T value, TemporalType clarifiedTemporalType) {
+	public T getBindValue() {
+	public Type getBindType() {
+public class ProcedureParameterBindings implements QueryParameterBindings {
+	public <T> void registerParameter(ProcedureParameterImplementor<T> parameter) {
+	public boolean isBound(QueryParameter parameter) {
+	public <T> QueryParameterBinding<T> getBinding(QueryParameter<T> parameter) {
+	public <T> QueryParameterBinding<T> getBinding(String name) {
+	public <T> QueryParameterBinding<T> getBinding(int position) {
+public class ProcedureParameterImpl<T> extends QueryParameterImpl<T> implements ProcedureParameterImplementor<T> {
+	public ProcedureParameterImpl(ParameterRegistrationImplementor<T> nativeParamRegistration) {
+	public ParameterMode getMode() {
+	public boolean isPassNullsEnabled() {
+	public void enablePassingNulls(boolean enabled) {
+	public boolean isJpaPositionalParameter() {
+	public String getName() {
+	public Integer getPosition() {
+	public ParameterRegistrationImplementor<T> getNativeParameterRegistration() {
+public class ProcedureParameterMetadata implements ParameterMetadata {
+	public void registerParameter(ProcedureParameterImplementor parameter) {
+	public boolean hasNamedParameters() {
+	public boolean hasPositionalParameters() {
+	public Set<QueryParameter<?>> collectAllParameters() {
+	public Set<Parameter<?>> collectAllParametersJpa() {
+	public Set<String> getNamedParameterNames() {
+	public int getPositionalParameterCount() {
+	public <T> QueryParameter<T> getQueryParameter(String name) {
+	public <T> QueryParameter<T> getQueryParameter(Integer position) {
+	public <T> QueryParameter<T> resolve(Parameter<T> param) {
+public class StoredProcedureQueryImpl extends AbstractProducedQuery<Object> implements StoredProcedureQueryImplementor {
+	public StoredProcedureQueryImpl(ProcedureCall procedureCall, SessionImplementor session) {
+	public StoredProcedureQueryImpl(ProcedureCallMemento memento, SessionImplementor entityManager) {
+	public StoredProcedureQueryImplementor registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
+	public StoredProcedureQueryImplementor registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
+	public SessionImplementor getProducer() {
+	public ProcedureCall getHibernateProcedureCall() {
+	public String getQueryString() {
+	public Type[] getReturnTypes() {
+	public boolean execute() {
+	public int executeUpdate() {
+	public Object getOutputParameterValue(int position) {
+	public Object getOutputParameterValue(String parameterName) {
+	public boolean hasMoreResults() {
+	public int getUpdateCount() {
+	public List getResultList() {
+	public Object getSingleResult() {
+	public Object unwrap(Class cls) {
+	public StoredProcedureQueryImpl setLockMode(LockModeType lockMode) {
+	public LockModeType getLockMode() {
+	public StoredProcedureQueryImplementor setParameter(QueryParameter parameter, Object value) {
+	public StoredProcedureQueryImplementor setParameter(Parameter parameter, Object value) {
+	public StoredProcedureQueryImplementor setParameter(Parameter param, Calendar value, TemporalType temporalType) {
+	public StoredProcedureQueryImplementor setParameter(Parameter param, Date value, TemporalType temporalType) {
+	public StoredProcedureQueryImplementor setParameter(String name, Object value) {
+	public StoredProcedureQueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
+	public StoredProcedureQueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
+	public StoredProcedureQueryImplementor setParameter(int position, Object value) {
+	public StoredProcedureQueryImplementor setEntity(int position, Object val) {
+	public StoredProcedureQueryImplementor setEntity(String name, Object val) {
+	public StoredProcedureQueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
+	public StoredProcedureQueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
+	public StoredProcedureQueryImplementor setFlushMode(FlushModeType jpaFlushMode) {
+	public StoredProcedureQueryImplementor setHint(String hintName, Object value) {
+	public StoredProcedureQueryImplementor setFirstResult(int startPosition) {
+	public StoredProcedureQueryImplementor setMaxResults(int maxResult) {
+public interface ProcedureParameterBindingImplementor<T> extends ProcedureParameterBinding<T> {
+public interface ProcedureParameterImplementor<T> extends ProcedureParameter<T> {
+public interface StoredProcedureQueryImplementor extends StoredProcedureQuery, QueryImplementor<Object> {
+public interface NativeQueryImplementor extends QueryImplementor<Object>, NativeQuery {
+public interface QueryImplementor<R> extends Query<R> {
-public interface QueryParameterBinding {
+public interface QueryParameterBinding<T> {
-public class QueryParameterBindings {
-	public QueryParameterBindings() {
-	public QueryParameterBindings(Set<QueryParameter> queryParameters) {
-	public QueryParameterBinding getBinding(QueryParameter parameter) {
-	public QueryParameterBinding getNamedParameterBinding(String name) {
-	public QueryParameterBinding getPositionalParameterBinding(Integer position) {
-	public QueryParameterBinding getParameterBinding(QueryParameter parameter) {
+public interface QueryParameterBindings {
-public interface QueryParameterListBinding {
+public interface QueryParameterListBinding<T> {
-public interface QueryProducerImplementor {
+public interface QueryProducerImplementor extends QueryProducer {
+	public PhysicalConnectionHandlingMode getConnectionHandlingMode() {
+	public PhysicalConnectionHandlingMode getConnectionHandlingMode() {
-	public TransactionCoordinatorBuilder getTransactionCoordinatorBuilder();
-	public JdbcSessionContext getJdbcSessionContext();
-	public JdbcConnectionAccess getJdbcConnectionAccess();
-	public void afterTransactionBegin();
-	public void beforeTransactionCompletion();
-	public void afterTransactionCompletion(boolean successful, boolean delayed);
-	public void flushBeforeTransactionCompletion();
-	public void explicitJoin();
-	public boolean isJoined();
-	public void pulse();
-	public TransactionDriver getTransactionDriverControl();
-	public SynchronizationRegistry getLocalSynchronizations();
-	public boolean isActive();
-	public IsolationDelegate createIsolationDelegate();
-	public void addObserver(TransactionObserver observer);
-	public void removeObserver(TransactionObserver observer);
-	public TransactionCoordinatorBuilder getTransactionCoordinatorBuilder();
-	public void setTimeOut(int seconds);
-	public int getTimeOut();
-	public interface TransactionDriver {
-		public void begin();
-		public void commit();
-		public void rollback();
-		public TransactionStatus getStatus();
-		public void markRollbackOnly();
-	public RuntimeException mapStatusCheckFailure(String message, SystemException systemException);
-	public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure);
-	public boolean shouldDoManagedFlush(SessionImpl session);
-	public boolean isActive();
-	public void afterTransactionBegin();
-	public void beforeTransactionCompletion();
-	public void afterTransactionCompletion(boolean successful, boolean delayed);
-	public JdbcSessionOwner getJdbcSessionOwner();
-	public void setTransactionTimeOut(int seconds);
-	public void flushBeforeTransactionCompletion();
-	public SessionImplementor getSession();
-	public Set<String> getSynchronizedQuerySpaces();
-	public String getSql();
-	public QueryParameters getQueryParameters();
-	public NativeSQLQueryReturn[] getQueryReturns();
-	public EntityStatistics getEntityStatistics(String entityName) {
+	public ConcurrentEntityStatisticsImpl getEntityStatistics(String entityName) {
-	public CollectionStatistics getCollectionStatistics(String role) {
+	public ConcurrentCollectionStatisticsImpl getCollectionStatistics(String role) {
-	public NaturalIdCacheStatistics getNaturalIdCacheStatistics(String regionName) {
+	public ConcurrentNaturalIdCacheStatisticsImpl getNaturalIdCacheStatistics(String regionName) {
-	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
+	public ConcurrentSecondLevelCacheStatisticsImpl getSecondLevelCacheStatistics(String regionName) {
-	public QueryStatistics getQueryStatistics(String queryString) {
+	public ConcurrentQueryStatisticsImpl getQueryStatistics(String queryString) {
-	public void openSession();
-	public void closeSession();
-	public void flush();
-	public void connect();
-	public void prepareStatement();
-	public void closeStatement();
-	public void endTransaction(boolean success);
-	public void loadEntity(String entityName);
-	public void fetchEntity(String entityName);
-	public void updateEntity(String entityName);
-	public void insertEntity(String entityName);
-	public void deleteEntity(String entityName);
-	public void optimisticFailure(String entityName);
-	public void loadCollection(String role);
-	public void fetchCollection(String role);
-	public void updateCollection(String role);
-	public void recreateCollection(String role);
-	public void removeCollection(String role);
-	public void secondLevelCachePut(String regionName);
-	public void secondLevelCacheHit(String regionName);
-	public void secondLevelCacheMiss(String regionName);
-	public void naturalIdCachePut(String regionName);
-	public void naturalIdCacheHit(String regionName);
-	public void naturalIdCacheMiss(String regionName);
-	public void naturalIdQueryExecuted(String regionName, long time);
-	public void queryCachePut(String hql, String regionName);
-	public void queryCacheHit(String hql, String regionName);
-	public void queryCacheMiss(String hql, String regionName);
-	public void queryExecuted(String hql, int rows, long time);
-	public void updateTimestampsCacheHit();
-	public void updateTimestampsCacheMiss();
-	public void updateTimestampsCachePut();
-	public Serializable getIdentifier(Object entity, SessionImplementor session) {
+	public Serializable getIdentifier(Object entity, SharedSessionContractImplementor session) {
-	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
+	public void setIdentifier(Object entity, Serializable id, SharedSessionContractImplementor session) {
-		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
+		public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session);
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
+		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session);
-		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
+		public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session) {
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
+		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session) {
-		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
+		public Object getIdentifier(Object entity, EntityMode entityMode, SharedSessionContractImplementor session) {
-		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
+		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SharedSessionContractImplementor session) {
-	public Object[] getPropertyValues(Object entity) throws HibernateException {
+	public Object[] getPropertyValues(Object entity) {
-	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
+	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SharedSessionContractImplementor session) {
-	public final Object instantiate(Serializable id, SessionImplementor session) {
+	public final Object instantiate(Serializable id, SharedSessionContractImplementor session) {
-	public void afterInitialize(Object entity, SessionImplementor session) {
+	public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
-	public final Object createProxy(Serializable id, SessionImplementor session)
+	public final Object createProxy(Serializable id, SharedSessionContractImplementor session) {
-	public LazyAttributeLoadingInterceptor injectInterceptor(Object entity, SessionImplementor session) {
+	public LazyAttributeLoadingInterceptor injectInterceptor(Object entity, SharedSessionContractImplementor session) {
-	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
+	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SharedSessionContractImplementor session) {
-	public void afterInitialize(Object entity, SessionImplementor session) {
+	public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
-	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
+	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session)
-	public final boolean isDirty(Object old, Object current, SessionImplementor session) {
+	public final boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) {
-	public final boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
+	public final boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) {
-	public final Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
+	public final Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner)
-	public final T nullSafeGet(ResultSet rs, String name, final SessionImplementor session) throws SQLException {
+	public final T nullSafeGet(ResultSet rs, String name, final SharedSessionContractImplementor session) throws SQLException {
-	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException {
+	public Object get(ResultSet rs, String name, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-	public void set(PreparedStatement st, T value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void set(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-	public final Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
+	public final Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public final Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
+	public final Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public final void beforeAssemble(Serializable cached, SessionImplementor session) {
+	public final void beforeAssemble(Serializable cached, SharedSessionContractImplementor session) {
-	public final Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public final Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public final Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
+	public final Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public final Object semiResolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
+	public final Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public final Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache) {
+	public final Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) {
-	public T extract(CallableStatement statement, int startIndex, final SessionImplementor session) throws SQLException {
+	public T extract(CallableStatement statement, int startIndex, final SharedSessionContractImplementor session) throws SQLException {
-	public T extract(CallableStatement statement, String[] paramNames, final SessionImplementor session) throws SQLException {
+	public T extract(CallableStatement statement, String[] paramNames, final SharedSessionContractImplementor session) throws SQLException {
-	public void nullSafeSet(
+	public void nullSafeSet(CallableStatement st, Object value, String name, SharedSessionContractImplementor session) throws SQLException {
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner)
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner)
-	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException {
+	public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) throws HibernateException {
-	public Object resolve(Object value, SessionImplementor session, Object owner)
+	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner)
-	public Object semiResolve(Object value, SessionImplementor session, Object owner) 
+	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) 
-	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
+	public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
-	public void beforeAssemble(Serializable cached, SessionImplementor session) {}
+	public void beforeAssemble(Serializable cached, SharedSessionContractImplementor session) {}
-	/*public Object copy(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
+	/*public Object copy(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache)
-	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
+	public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
-	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
+	public Object nullSafeGet(ResultSet rs,	String[] names,	SharedSessionContractImplementor session,	Object owner)
-	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
+	public Object hydrate(ResultSet rs,	String[] names,	SharedSessionContractImplementor session,	Object owner)
-	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
+	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value,	int index, SharedSessionContractImplementor session)
-	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SharedSessionContractImplementor session)
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache)
-	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner) {
+	public Object nullSafeGet(ResultSet rs,	String name, SharedSessionContractImplementor session, Object owner) {
-	public Object semiResolve(Object value, SessionImplementor session, Object owner) {
+	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) {
-	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
+	public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session) throws HibernateException {
-	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
+	public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session) throws HibernateException {
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
+	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key)
-	public PersistentCollection wrap(SessionImplementor session, Object array) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object array) {
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
+	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key)
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public String[] getRegistrationKeys();
-	public byte[] seed(SessionImplementor session) {
+	public byte[] seed(SharedSessionContractImplementor session) {
-	public byte[] next(byte[] current, SessionImplementor session) {
+	public byte[] next(byte[] current, SharedSessionContractImplementor session) {
-	public Byte next(Byte current, SessionImplementor session) {
+	public Byte next(Byte current, SharedSessionContractImplementor session) {
-	public Byte seed(SessionImplementor session) {
+	public Byte seed(SharedSessionContractImplementor session) {
-	public Calendar next(Calendar current, SessionImplementor session) {
+	public Calendar next(Calendar current, SharedSessionContractImplementor session) {
-	public Calendar seed(SessionImplementor session) {
+	public Calendar seed(SharedSessionContractImplementor session) {
-	public boolean contains(Object collection, Object childObject, SessionImplementor session) {
+	public boolean contains(Object collection, Object childObject, SharedSessionContractImplementor session) {
-	public abstract PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key);
+	public abstract PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key);
-	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
+	public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws SQLException {
-	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] name, SharedSessionContractImplementor session, Object owner)
-	public Iterator getElementsIterator(Object collection, SessionImplementor session) {
+	public Iterator getElementsIterator(Object collection, SharedSessionContractImplementor session) {
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner)
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner)
-	public boolean isDirty(Object old, Object current, SessionImplementor session)
+	public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session)
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
-	public abstract PersistentCollection wrap(SessionImplementor session, Object collection);
+	public abstract PersistentCollection wrap(SharedSessionContractImplementor session, Object collection);
-	public Serializable getKeyOfOwner(Object owner, SessionImplementor session) {
+	public Serializable getKeyOfOwner(Object owner, SharedSessionContractImplementor session) {
-	public Serializable getIdOfOwnerOrNull(Serializable key, SessionImplementor session) {
+	public Serializable getIdOfOwnerOrNull(Serializable key, SharedSessionContractImplementor session) {
-	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
+	public Object hydrate(ResultSet rs, String[] name, SharedSessionContractImplementor session, Object owner) {
-	public Object resolve(Object value, SessionImplementor session, Object owner)
+	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner)
-	public Object semiResolve(Object value, SessionImplementor session, Object owner)
+	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner)
-	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
+	public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
-	public Object getCollection(Serializable key, SessionImplementor session, Object owner) {
+	public Object getCollection(Serializable key, SharedSessionContractImplementor session, Object owner) {
-	public boolean isDirty(final Object x, final Object y, final SessionImplementor session) throws HibernateException {
+	public boolean isDirty(final Object x, final Object y, final SharedSessionContractImplementor session) throws HibernateException {
-	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SessionImplementor session)
+	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SharedSessionContractImplementor session)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value, int begin, SharedSessionContractImplementor session)
-	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner)
-	public Object getPropertyValue(Object component, int i, SessionImplementor session)
+	public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session)
-	public Object[] getPropertyValues(Object component, SessionImplementor session)
+	public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session)
-	public Object instantiate(Object parent, SessionImplementor session)
+	public Object instantiate(Object parent, SharedSessionContractImplementor session)
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner)
-	public Object assemble(Serializable object, SessionImplementor session, Object owner)
+	public Object assemble(Serializable object, SharedSessionContractImplementor session, Object owner)
-	public Object resolve(Object value, SessionImplementor session, Object owner)
+	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner)
-	public Object semiResolve(Object value, SessionImplementor session, Object owner)
+	public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner)
-	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
+	public Object extract(CallableStatement statement, int startIndex, SharedSessionContractImplementor session) throws SQLException {
-	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session)
+	public Object extract(CallableStatement statement, String[] paramNames, SharedSessionContractImplementor session)
+	public String getName() {
+	public Class getReturnedClass() {
+	public boolean isMutable() {
-	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
+	public Object[] getPropertyValues(Object component, SharedSessionContractImplementor session) throws HibernateException {
-	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
+	public Object getPropertyValue(Object component, int i, SharedSessionContractImplementor session) throws HibernateException {
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public String getName() {
-	public Class getReturnedClass() {
-	public boolean isMutable() {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
+	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key)
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public boolean contains(Object collection, Object entity, SessionImplementor session) {
+	public boolean contains(Object collection, Object entity, SharedSessionContractImplementor session) {
-	public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SessionImplementor session)
+	public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SharedSessionContractImplementor session)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(
-	public Object nullSafeGet(ResultSet rs, String columnName, SessionImplementor session, Object owner)
+	public Object nullSafeGet(
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) {
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
+	public void nullSafeSet(
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+	public void nullSafeSet(
-	public Object deepCopy(Object value, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
-	public Object next(Object current, SessionImplementor session) {
+	public Object next(Object current, SharedSessionContractImplementor session) {
-	public Object seed(SessionImplementor session) {
+	public Object seed(SharedSessionContractImplementor session) {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session)
-	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
+	public Object extract(CallableStatement statement, int startIndex, SharedSessionContractImplementor session) throws SQLException {
-	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session)
+	public Object extract(CallableStatement statement, String[] paramNames, SharedSessionContractImplementor session)
-	public Date seed(SessionImplementor session) {
+	public Date seed(SharedSessionContractImplementor session) {
-	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner)
-	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
+	public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public Instant seed(SessionImplementor session) {
+	public Instant seed(SharedSessionContractImplementor session) {
-	public Instant next(Instant current, SessionImplementor session) {
+	public Instant next(Instant current, SharedSessionContractImplementor session) {
-	public Integer seed(SessionImplementor session) {
+	public Integer seed(SharedSessionContractImplementor session) {
-	public Integer next(Integer current, SessionImplementor session) {
+	public Integer next(Integer current, SharedSessionContractImplementor session) {
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
+	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public LocalDateTime seed(SessionImplementor session) {
+	public LocalDateTime seed(SharedSessionContractImplementor session) {
-	public LocalDateTime next(LocalDateTime current, SessionImplementor session) {
+	public LocalDateTime next(LocalDateTime current, SharedSessionContractImplementor session) {
-	public Long next(Long current, SessionImplementor session) {
+	public Long next(Long current, SharedSessionContractImplementor session) {
-	public Long seed(SessionImplementor session) {
+	public Long seed(SharedSessionContractImplementor session) {
-	public void beforeAssemble(Serializable oid, SessionImplementor session) {
+	public void beforeAssemble(Serializable oid, SharedSessionContractImplementor session) {
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+	public void nullSafeSet(
-	public String toXMLString(Object value, SessionFactoryImplementor factory)
+	public String toXMLString(Object value, SessionFactoryImplementor factory) throws HibernateException {
-	public Object fromXMLString(String xml, Mapping factory)
+	public Object fromXMLString(String xml, Mapping factory) throws HibernateException {
-	public Object deepCopy(Object value, SessionFactoryImplementor factory)
+	public Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
-	public OffsetDateTime seed(SessionImplementor session) {
+	public OffsetDateTime seed(SharedSessionContractImplementor session) {
-	public OffsetDateTime next(OffsetDateTime current, SessionImplementor session) {
+	public OffsetDateTime next(OffsetDateTime current, SharedSessionContractImplementor session) {
-	public boolean isNull(Object owner, SessionImplementor session) {
+	public boolean isNull(Object owner, SharedSessionContractImplementor session) {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session) {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) {
-	public boolean isDirty(Object old, Object current, SessionImplementor session) {
+	public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) {
-	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) {
+	public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) {
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public Object assemble(Serializable oid, SessionImplementor session, Object owner)
+	public Object assemble(Serializable oid, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public boolean canDoExtraction();
-	public T extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException;
-	public T extract(CallableStatement statement, String[] paramNames, SessionImplementor session) throws SQLException;
-	public boolean canDoSetting();
-	public void nullSafeSet(CallableStatement statement, Object value, String name, SessionImplementor session)
-	public static final SerializableType<Serializable> INSTANCE = new SerializableType<Serializable>( Serializable.class );
+	public static final SerializableType<Serializable> INSTANCE = new SerializableType<>( Serializable.class );
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
+	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public Short next(Short current, SessionImplementor session) {
+	public Short seed(SharedSessionContractImplementor session) {
-	public Short seed(SessionImplementor session) {
+	public Short next(Short current, SharedSessionContractImplementor session) {
-	public Date next(Date current, SessionImplementor session) {
+	public Date next(Date current, SharedSessionContractImplementor session) {
-	public Date seed(SessionImplementor session) {
+	public Date seed(SharedSessionContractImplementor session) {
-	public boolean isAssociationType();
-	public boolean isCollectionType();
-	public boolean isEntityType();
-	public boolean isAnyType();
-	public boolean isComponentType();
-	public int getColumnSpan(Mapping mapping) throws MappingException;
-	public int[] sqlTypes(Mapping mapping) throws MappingException;
-	public Size[] dictatedSizes(Mapping mapping) throws MappingException;
-	public Size[] defaultSizes(Mapping mapping) throws MappingException;
-	public Class getReturnedClass();
-	public boolean isSame(Object x, Object y) throws HibernateException;
-	public boolean isEqual(Object x, Object y) throws HibernateException;
-	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) throws HibernateException;
-	public int getHashCode(Object x) throws HibernateException;
-	public int getHashCode(Object x, SessionFactoryImplementor factory) throws HibernateException;
-	public int compare(Object x, Object y);
-	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException;
-	public boolean isDirty(Object oldState, Object currentState, boolean[] checkable, SessionImplementor session)
-	public boolean isModified(Object dbState, Object currentState, boolean[] checkable, SessionImplementor session)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
-	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
-	public String toLoggableString(Object value, SessionFactoryImplementor factory)
-	public String getName();
-	public Object deepCopy(Object value, SessionFactoryImplementor factory)
-	public boolean isMutable();
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException;
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
-	public void beforeAssemble(Serializable cached, SessionImplementor session);
-	public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
-	public Object resolve(Object value, SessionImplementor session, Object owner)
-	public Object semiResolve(Object value, SessionImplementor session, Object owner)
-	public Type getSemiResolvedType(SessionFactoryImplementor factory);
-	public Object replace(
-	public Object replace(
-	public boolean[] toColumnNullness(Object value, Mapping mapping);
-	public T seed(SessionImplementor session);
-	public T next(T current, SessionImplementor session);
-	public Comparator<T> getComparator();
-	public ZonedDateTime seed(SessionImplementor session) {
+	public ZonedDateTime seed(SharedSessionContractImplementor session) {
-	public ZonedDateTime next(ZonedDateTime current, SessionImplementor session) {
+	public ZonedDateTime next(ZonedDateTime current, SharedSessionContractImplementor session) {
-public interface WrapperOptionsContext {
+public interface WrapperOptionsContext extends WrapperOptions {
-	public String[] getPropertyNames();
-	public Type[] getPropertyTypes();
-	public Object getPropertyValue(Object component, int property) throws HibernateException;
-	public void setPropertyValue(Object component, int property, Object value) throws HibernateException;
-	public Class returnedClass();
-	public boolean equals(Object x, Object y) throws HibernateException;
-	public int hashCode(Object x) throws HibernateException;
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) 
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) 
-	public Object deepCopy(Object value) throws HibernateException;
-	public boolean isMutable();
-	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException;
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner) 
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner) 
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) 
-	public PersistentCollection wrap(SessionImplementor session, Object collection);
-	public Iterator getElementsIterator(Object collection);
-	public boolean contains(Object collection, Object entity);
-	public Object indexOf(Object collection, Object entity);
-	public Object replaceElements(
-	public Object instantiate(int anticipatedSize);
-	public int[] sqlTypes();
-	public Class returnedClass();
-	public boolean equals(Object x, Object y) throws HibernateException;
-	public int hashCode(Object x) throws HibernateException;
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException;
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException;
-	public Object deepCopy(Object value) throws HibernateException;
-	public boolean isMutable();
-	public Serializable disassemble(Object value) throws HibernateException;
-	public Object assemble(Serializable cached, Object owner) throws HibernateException;
-	public Object replace(Object original, Object target, Object owner) throws HibernateException;
-	public Object seed(SessionImplementor session);
-	public Object next(Object current, SessionImplementor session);
-		public void afterDeserialize(SessionImplementor session) {
+		public void afterDeserialize(SharedSessionContractImplementor session) {
-	public DollarValue nullSafeGet(ResultSet rs, String[] names,
+	public DollarValue nullSafeGet(
-	public void nullSafeSet(PreparedStatement st, Object value, int index,
+	public void nullSafeSet(
-	public MyDate nullSafeGet(ResultSet rs, String[] names,
+	public MyDate nullSafeGet(
-	public void nullSafeSet(PreparedStatement st, Object value, int index,
+	public void nullSafeSet(
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public Serializable disassemble(Object value, SessionImplementor session)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session)
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner)
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-    public Serializable generate(SessionImplementor arg0, Object arg1) throws HibernateException {
+    public Serializable generate(SharedSessionContractImplementor session, Object entity) throws HibernateException {
-    public Serializable generate(SessionImplementor arg0, Object arg1) throws HibernateException {
+    public Serializable generate(SharedSessionContractImplementor session, Object entity) throws HibernateException {
-	public Serializable generate(SessionImplementor aSessionImplementor, Object aObject) throws HibernateException {
+	public Serializable generate(SharedSessionContractImplementor session, Object aObject) throws HibernateException {
-	public Serializable disassemble(Object value, SessionImplementor aSessionImplementor) throws HibernateException {
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
-	public Object assemble(Serializable cached, SessionImplementor aSessionImplementor, Object aObject)
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object aObject)
-	public Object replace(Object original, Object target, SessionImplementor aSessionImplementor, Object aObject2)
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object aObject2)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
-		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
+		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SharedSessionContractImplementor session) {
-		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
+		public int[] findModified(Object[] old, Object[] current, Object object, SharedSessionContractImplementor session) {
-		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
+		public Object[] getNaturalIdentifierSnapshot(Serializable id, SharedSessionContractImplementor session) {
-		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
+		public Serializable loadEntityIdByNaturalId(
-		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
+		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SharedSessionContractImplementor session) {
-		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
+		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SharedSessionContractImplementor session) {
-		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
+		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SharedSessionContractImplementor session) {
-		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
+		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SharedSessionContractImplementor session) {
-		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
+		public void insert(Serializable id, Object[] fields, Object object, SharedSessionContractImplementor session) {
-		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
+		public Serializable insert(Object[] fields, Object object, SharedSessionContractImplementor session) {
-		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
+		public void delete(Serializable id, Object version, Object object, SharedSessionContractImplementor session) {
-		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
+		public void update(
-		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
+		public Object[] getDatabaseSnapshot(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
+		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SharedSessionContractImplementor session) {
-		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
+		public Object getCurrentVersion(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
+		public Object forceVersionIncrement(Serializable id, Object currentVersion, SharedSessionContractImplementor session) {
-		public void afterInitialize(Object entity, SessionImplementor session) {
+		public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
-		public void afterReassociate(Object entity, SessionImplementor session) {
+		public void afterReassociate(Object entity, SharedSessionContractImplementor session) {
-		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
+		public Object createProxy(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
+		public Boolean isTransient(Object object, SharedSessionContractImplementor session) throws HibernateException {
-		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
+		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SharedSessionContractImplementor session) {
-		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
+		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session) {
-		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
+		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session) {
-		public Serializable getIdentifier(Object entity, SessionImplementor session) {
+		public Serializable getIdentifier(Object entity, SharedSessionContractImplementor session) {
-		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
+		public void setIdentifier(Object entity, Serializable id, SharedSessionContractImplementor session) {
-		public Object instantiate(Serializable id, SessionImplementor session) {
+		public Object instantiate(Serializable id, SharedSessionContractImplementor session) {
-		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
+		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SharedSessionContractImplementor session) {
-		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
+		public void initialize(Serializable key, SharedSessionContractImplementor session) throws HibernateException {
-		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
+		public Object readKey(ResultSet rs, String[] keyAliases, SharedSessionContractImplementor session)
-		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
+		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SharedSessionContractImplementor session)
-		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
+		public Object readIndex(ResultSet rs, String[] columnAliases, SharedSessionContractImplementor session)
-		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
+		public Object readIdentifier(ResultSet rs, String columnAlias, SharedSessionContractImplementor session)
-		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
+		public void remove(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
+		public void recreate(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
-		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
+		public void deleteRows(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
-		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
+		public void updateRows(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
-		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
+		public void insertRows(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
-		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
+		public boolean isAffectedByEnabledFilters(SharedSessionContractImplementor session) {
-		public int getSize(Serializable key, SessionImplementor session) {
+		public int getSize(Serializable key, SharedSessionContractImplementor session) {
-		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
+		public boolean indexExists(Serializable key, Object index, SharedSessionContractImplementor session) {
-		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
+		public boolean elementExists(Serializable key, Object element, SharedSessionContractImplementor session) {
-		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
+		public Object getElementByIndex(Serializable key, Object index, SharedSessionContractImplementor session, Object owner) {
-		public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
+		public void processQueuedOps(PersistentCollection collection, Serializable key, SharedSessionContractImplementor session)
-	public Serializable generate(SessionImplementor s, Object obj) {
+	public Serializable generate(SharedSessionContractImplementor s, Object obj) {
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) throws HibernateException {
+	public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister) throws HibernateException {
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SessionImplementor session) throws HibernateException {
+	public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SharedSessionContractImplementor session) throws HibernateException {
-	public PersistentMyList(SessionImplementor session) {
+	public PersistentMyList(SharedSessionContractImplementor session) {
-	public PersistentMyList(SessionImplementor session, IMyList list) {
+	public PersistentMyList(SharedSessionContractImplementor session, IMyList list) {
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+	public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
-	public PersistentDefaultableList(SessionImplementor session) {
+	public PersistentDefaultableList(SharedSessionContractImplementor session) {
-	public PersistentDefaultableList(SessionImplementor session, List list) {
+	public PersistentDefaultableList(SharedSessionContractImplementor session, List list) {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) throws HibernateException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(
-	public void nullSafeSet(PreparedStatement st, Object value, int index,
+	public void nullSafeSet(
-	public Serializable disassemble(Object value, SessionImplementor session)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session)
-	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
+	public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner)
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-	public Boolean isTransient(Object object, SessionImplementor session) {
+	public Boolean isTransient(Object object, SharedSessionContractImplementor session) {
-	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
+	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SharedSessionContractImplementor session) {
-	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
+	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session) {
-	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
+	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session) {
-	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
+	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SharedSessionContractImplementor session) {
-	public Serializable getIdentifier(Object entity, SessionImplementor session) {
+	public Serializable getIdentifier(Object entity, SharedSessionContractImplementor session) {
-	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
+	public void setIdentifier(Object entity, Serializable id, SharedSessionContractImplementor session) {
-	public Object instantiate(Serializable id, SessionImplementor session) {
+	public Object instantiate(Serializable id, SharedSessionContractImplementor session) {
-	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
+	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SharedSessionContractImplementor session) {
-	public List multiLoad(Serializable[] ids, SessionImplementor session, MultiLoadOptions loadOptions) {
+	public List multiLoad(Serializable[] ids, SharedSessionContractImplementor session, MultiLoadOptions loadOptions) {
-	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
+	public Serializable insert(Object[] fields, Object object, SharedSessionContractImplementor session)
-	public Object createProxy(Serializable id, SessionImplementor session)
+	public Object createProxy(Serializable id, SharedSessionContractImplementor session)
-	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
+	public Object forceVersionIncrement(Serializable id, Object currentVersion, SharedSessionContractImplementor session)
-	public void afterInitialize(Object entity, SessionImplementor session) {
+	public void afterInitialize(Object entity, SharedSessionContractImplementor session) {
-	public void afterReassociate(Object entity, SessionImplementor session) {
+	public void afterReassociate(Object entity, SharedSessionContractImplementor session) {
-	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
+	public Object[] getDatabaseSnapshot(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
+	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SharedSessionContractImplementor session) {
-	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
+	public Object[] getNaturalIdentifierSnapshot(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
-	public boolean isMutable() { return true; }
+	public boolean isMutable() {
-	public Object nullSafeGet(ResultSet rs,	String[] names, SessionImplementor session,	Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
-	public Serializable disassemble(Object value, SessionImplementor session) {
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session) {
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner) 
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
-	public Serializable disassemble(Object value, SessionImplementor session)
+	public Serializable disassemble(Object value, SharedSessionContractImplementor session)
-	public Object replace(Object original, Object target, SessionImplementor session, Object owner) 
+	public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner)
-   public static EnumUserType createInstance(Class clazz){
-   public void setParameterValues(Properties params) {
-    public int[] sqlTypes() {
-    public Class returnedClass() {
-    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
-	public Object nullSafeGet(ResultSet resultSet, String[] names,
+	public static EnumUserType createInstance(Class clazz) {
-public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) 
-public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index,
+	public void setParameterValues(Properties params) {
+	public int[] sqlTypes() {
+	public Class returnedClass() {
+	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
+	public Object nullSafeGet(
-	public Object deepCopy(Object value) throws HibernateException{
-    public boolean isMutable() {
-    public Object assemble(Serializable cached, Object owner) throws HibernateException {
-    public Serializable disassemble(Object value) throws HibernateException {
-    public Object replace(Object original, Object target, Object owner) throws HibernateException {
-    public int hashCode(Object x) throws HibernateException {
-    public boolean equals(Object x, Object y) throws HibernateException {
+	public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
+	public void nullSafeSet(
+	public Object deepCopy(Object value) throws HibernateException {
+	public boolean isMutable() {
+	public Object assemble(Serializable cached, Object owner) throws HibernateException {
+	public Serializable disassemble(Object value) throws HibernateException {
+	public Object replace(Object original, Object target, Object owner) throws HibernateException {
+	public int hashCode(Object x) throws HibernateException {
+	public boolean equals(Object x, Object y) throws HibernateException {
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
-public class MonetaryAmountUserType
+public class MonetaryAmountUserType implements UserType {
-	public int[] sqlTypes() { return SQL_TYPES; }
+	public int[] sqlTypes() {
-	public Class returnedClass() { return MonetaryAmount.class; }
+	public Class returnedClass() {
-	public boolean isMutable() { return false; }
+	public boolean isMutable() {
-	public Object nullSafeGet(ResultSet resultSet,
+	public Object nullSafeGet(
-	public void nullSafeSet(PreparedStatement statement,
+	public void nullSafeSet(
-	public Object replace(Object original, Object target, Object owner)
+	public Object replace(Object original, Object target, Object owner) throws HibernateException {
-		public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+		public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
-		public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+		public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
-		public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+		public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-		public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+		public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
-		public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
+		public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
-		public Object assemble(Serializable cached, SessionImplementor session, Object owner)
+		public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner)
-		public Object replace(Object original, Object target, SessionImplementor session, Object owner)
+		public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner)
-	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
+	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
-	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
-	public QueryImpl(org.hibernate.Query query, AbstractEntityManagerImpl em) {
+	public QueryImpl(org.hibernate.Query query, HibernateEntityManagerImplementor em) {
+public abstract class AbstractEntityManagerImpl
+	public SessionFactoryImplementor getEntityManagerFactory() {
+	public PersistenceUnitTransactionType getTransactionType() {
+	public SynchronizationType getSynchronizationType() {
+	public CriteriaBuilder getCriteriaBuilder() {
+	public Metamodel getMetamodel() {
+	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
+	public RuntimeException convert(HibernateException e) {
+	public RuntimeException convert(RuntimeException e) {
+	public RuntimeException convert(RuntimeException e, LockOptions lockOptions) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
+	public boolean isConnected() {
+	public boolean isTransactionInProgress() {
-	public BaseQueryImpl(HibernateEntityManagerImplementor entityManager) {
+	public BaseQueryImpl(SharedSessionContractImplementor entityManager) {
-public class FunkyIdentifierGeneratorProvider implements IdentifierGeneratorStrategyProvider {
+public class FunkyIdentifierGeneratorProvider implements org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider {
-	public final Object get(SessionImplementor session, Object key, long txTimestamp) throws CacheException {
+	public final Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
-	public final SoftLock lockItem(SessionImplementor session, Object key, Object version) throws CacheException {
+	public final SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
-	public final void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
+	public final void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
-	public boolean insert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public boolean afterInsert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public boolean update(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
-	public Object get(SessionImplementor session, Object key) throws CacheException {
+	public Object get(SharedSessionContractImplementor session, Object key) throws CacheException {
-	public void put(SessionImplementor session, Object key, Object value) throws CacheException {
+	public void put(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean insert(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean afterInsert(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean update(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
-	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
-	public Object get(SessionImplementor session, Object key, long txTimestamp) throws CacheException {
+	public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
-	public boolean putFromLoad(SessionImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
-	public boolean putFromLoad(SessionImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
-	public SoftLock lockItem(SessionImplementor session, Object key, Object version) throws CacheException {
+	public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
-	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
+	public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
-	public void remove(SessionImplementor session, Object key) throws CacheException {
+	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
-	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
+	public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
-	public void remove(SessionImplementor session, Object key) throws CacheException {
+	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
-	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
+	public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
-	public boolean insert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public boolean afterInsert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public boolean update(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
-	public void remove(SessionImplementor session, Object key) throws CacheException {
+	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
-	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
+	public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
-	public void remove(SessionImplementor session, Object key) throws CacheException {
+	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
-	public boolean insert(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean afterInsert(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean update(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean insert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public boolean afterInsert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
+	public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
-	public boolean update(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
-	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
+	public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
-	public boolean insert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public boolean update(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
-	public boolean afterInsert(SessionImplementor session, Object key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
-	public boolean insert(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean update(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean afterInsert(SessionImplementor session, Object key, Object value) throws CacheException {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
-	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
-	public void remove(SessionImplementor session, Object key) throws CacheException {
+	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
-	public boolean afterInsert(SessionImplementor session, Object key, Object value, Object version) {
+	public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
-	public boolean afterUpdate(SessionImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
+	public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
-	public void remove(SessionImplementor session, Object key) throws CacheException {
+	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
-	public void remove(SessionImplementor session, Object key) throws CacheException {
+	public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {

Lines added containing method: 1355. Lines removed containing method: 2024. Tot = 3379
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public String getName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getMethodName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean useReflectionOptimizer() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getReturnType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static BytecodeProvider getBytecodeProvider() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-11646 3a813dcb_diff.java
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
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public String getName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean useReflectionOptimizer() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static BytecodeProvider getBytecodeProvider() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-8558  c6fa2b1d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+		Set<String> tmpLazyPropertyNames = new HashSet<String>( );
-				lazyPropertyNames.add( property.getName() );
+				tmpLazyPropertyNames.add( property.getName() );
+		lazyPropertyNames = tmpLazyPropertyNames.isEmpty() ? null : Collections.unmodifiableSet( tmpLazyPropertyNames );

Lines added: 3. Lines removed: 1. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public boolean contains(String name) {
+	public boolean contains(String name) {
+public class LazyAttributeLoader implements PersistentAttributeInterceptor {
+	public LazyAttributeLoader(SessionImplementor session, Set<String> lazyFields, String entityName) {
+	public String toString() {
+	public boolean readBoolean(Object obj, String name, boolean oldValue) {
+	public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
+	public byte readByte(Object obj, String name, byte oldValue) {
+	public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
+	public char readChar(Object obj, String name, char oldValue) {
+	public char writeChar(Object obj, String name, char oldValue, char newValue) {
+	public short readShort(Object obj, String name, short oldValue) {
+	public short writeShort(Object obj, String name, short oldValue, short newValue) {
+	public int readInt(Object obj, String name, int oldValue) {
+	public int writeInt(Object obj, String name, int oldValue, int newValue) {
+	public float readFloat(Object obj, String name, float oldValue) {
+	public float writeFloat(Object obj, String name, float oldValue, float newValue) {
+	public double readDouble(Object obj, String name, double oldValue) {
+	public double writeDouble(Object obj, String name, double oldValue, double newValue) {
+	public long readLong(Object obj, String name, long oldValue) {
+	public long writeLong(Object obj, String name, long oldValue, long newValue) {
+	public Object readObject(Object obj, String name, Object oldValue) {
+	public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
+	public boolean isLazyLoadingBytecodeEnhanced() {

Lines added containing method: 24. Lines removed containing method: 0. Tot = 24
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-9803  611f8a0e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer( mappedClass, getterNames, setterNames, propTypes );
+			optimizer = Environment.getBytecodeProvider().getReflectionOptimizer(
+					mappedClass,
+					getterNames,
+					setterNames,
+					propTypes
+			);

Lines added: 6. Lines removed: 1. Tot = 7
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
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getMethodName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SessionFactoryOptions getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getProxyInterface() {
-		if (proxyInterfaceName==null) {
+		if ( proxyInterfaceName == null ) {
-			if (proxyInterface == null) {
+			if ( proxyInterface == null ) {
-			throw new MappingException("proxy class not found: " + proxyInterfaceName, cnfe);
+			throw new MappingException( "proxy class not found: " + proxyInterfaceName, cnfe );

Lines added: 3. Lines removed: 3. Tot = 6
—————————
Method found in diff:	public Class getMappedClass() throws MappingException {
-		if (className==null) {
+		if ( className == null ) {
-			if (mappedClass == null) {
-				mappedClass = ReflectHelper.classForName(className);
+			if ( mappedClass == null ) {
+				mappedClass = ReflectHelper.classForName( className );
-			throw new MappingException("entity class not found: " + className, cnfe);
+			throw new MappingException( "entity class not found: " + className, cnfe );

Lines added: 4. Lines removed: 4. Tot = 8
—————————
Method found in diff:	public Class getReturnType() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public Iterator getPropertyClosureIterator() {
-    public Iterator getPropertyClosureIterator() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-9803  bd256e47_diff.java
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
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getMethodName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Map getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ReflectionOptimizer getReflectionOptimizer(

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static boolean useReflectionOptimizer() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getProxyInterface() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Iterator getPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static BytecodeProvider getBytecodeProvider() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-9837  9e063ffa_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public ClassLoaderAccessImpl(ClassLoaderService classLoaderService) {
-	public MappingBinder() {
+	public MappingBinder(ClassLoaderService classLoaderService) {
-	public MappingBinder(boolean validateXml) {
+	public MappingBinder(ClassLoaderService classLoaderService, boolean validateXml) {
-	public static final LocalXmlResourceResolver INSTANCE = new LocalXmlResourceResolver();
+	public LocalXmlResourceResolver(ClassLoaderService classLoaderService) {
+	public <T> T generateProxy(InvocationHandler handler, Class... interfaces) {
+	public <T> T workWithClassLoader(Work<T> work) {
+public abstract class ClassLoaderAccessDelegateImpl implements ClassLoaderAccess {
+	public <T> Class<T> classForName(String name) {
+	public URL locateResource(String resourceName) {
+public class ClassLoaderAccessLazyImpl implements ClassLoaderAccess {
+	public ClassLoaderAccessLazyImpl(MetadataBuildingOptions metadataBuildingOptions) {
+	public <T> Class<T> classForName(String name) {
+	public URL locateResource(String resourceName) {
-public class JPAMetadataProvider implements MetadataProvider, Serializable {
+public class JPAMetadataProvider implements MetadataProvider {
+	public JPAMetadataProvider(final MetadataBuildingOptions metadataBuildingOptions) {
-	public JPAOverriddenAnnotationReader(AnnotatedElement el, XMLContext xmlContext) {
+	public JPAOverriddenAnnotationReader(AnnotatedElement el, XMLContext xmlContext, ClassLoaderAccess classLoaderAccess) {
-	public static List<NamedEntityGraph> buildNamedEntityGraph(Element element, XMLContext.Default defaults) {
+	public static List<NamedEntityGraph> buildNamedEntityGraph(
-	public static List<NamedStoredProcedureQuery> buildNamedStoreProcedureQueries(Element element, XMLContext.Default defaults) {
+	public static List<NamedStoredProcedureQuery> buildNamedStoreProcedureQueries(
-	public static List<SqlResultSetMapping> buildSqlResultsetMappings(Element element, XMLContext.Default defaults) {
+	public static List<SqlResultSetMapping> buildSqlResultsetMappings(
-	public static List buildNamedQueries(Element element, boolean isNative, XMLContext.Default defaults) {
+	public static List buildNamedQueries(
+	public XMLContext(ClassLoaderAccess classLoaderAccess) {
-	public BeanValidationEventListener() {
-	public BeanValidationEventListener(ValidatorFactory factory, Map settings) {
+	public BeanValidationEventListener(ValidatorFactory factory, Map settings, ClassLoaderService classLoaderService) {
-	public void initialize(Map settings) {
+	public void initialize(Map settings, ClassLoaderService classLoaderService) {
-	public GroupsPerOperation(Map settings) {
+	public static GroupsPerOperation from(Map settings, ClassLoaderAccess classLoaderAccess) {
+	public static Class<?>[] buildGroupsForOperation(Operation operation, Map settings, ClassLoaderAccess classLoaderAccess) {
-	public static ResultSet generateProxy(ResultSet resultSet, ColumnNameCache columnNameCache) {
+	public static ResultSet generateProxy(
-	public static ClassLoader getProxyClassLoader() {
-	public static final ResultSetWrapper INSTANCE = new ResultSetWrapperImpl();
+	public ResultSetWrapperImpl(ServiceRegistry serviceRegistry) {
+		public JavaConstantConverter(SessionFactoryImplementor factory) {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnvironment) throws MappingException;
-		public ValueImpl(ExportableColumn column, Table table, BasicType type) {
+		public ValueImpl(ExportableColumn column, Table table, BasicType type, Database database) {
+		public ServiceRegistry getServiceRegistry() {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnvironment) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment d) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment env) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-	public void configure(Type type, Properties params, JdbcEnvironment jdbcEnv) throws MappingException {
+	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
-public final class ClassLoaderHelper {
-	public static ClassLoader overridenClassLoader;
-	public static ClassLoader getContextClassLoader() {
-	public static Reader getConfigStreamReader(final String path) throws HibernateException {
-	public static Properties getConfigProperties(String path) throws HibernateException {
-	public static boolean isPublic(Member member) {
-	public static Class reflectedPropertyClass(String className, String name) throws MappingException {
+	public static Class reflectedPropertyClass(
-	public static Getter getGetter(Class theClass, String name) throws MappingException {
-	public static Object getConstantValue(String name) {
+	public static Object getConstantValue(String name, ClassLoaderService classLoaderService) {
+	public static Field findField(Class containerClass, String propertyName) {
+	public static Method findGetterMethod(Class containerClass, String propertyName) {
+	public static Method findSetterMethod(Class containerClass, String propertyName, Class propertyType) {
-public class LocalXmlResourceResolver implements javax.xml.stream.XMLResolver {
-	public static final LocalXmlResourceResolver INSTANCE = new LocalXmlResourceResolver();
-	public static final String INITIAL_JPA_ORM_NS = "http://java.sun.com/xml/ns/persistence/orm";
-	public static final String SECOND_JPA_ORM_NS = "http://xmlns.jcp.org/xml/ns/persistence/orm";
-	public static final String HIBERNATE_MAPPING_DTD_URL_BASE = "http://www.hibernate.org/dtd/";
-	public static final String LEGACY_HIBERNATE_MAPPING_DTD_URL_BASE = "http://hibernate.sourceforge.net/";
-	public static final String CLASSPATH_EXTENSION_URL_BASE = "classpath://";
-	public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
-public class MappingReader {
-	public static final MappingReader INSTANCE = new MappingReader();
-	public XmlDocument readMappingDocument(InputSource source, Origin origin) {
-	public static enum SupportedOrmXsdVersion {
-		public static SupportedOrmXsdVersion parse(String name, Origin origin) {
-		public URL getSchemaUrl() {
-		public Schema getSchema() {
-	public XmlDocument readMappingDocument(EntityResolver entityResolver, InputSource source, Origin origin) {
+	public XMLHelper(ClassLoaderService classLoaderService) {
+					public DocumentFactory doWork(ClassLoader classLoader) {
-	public static final EntityResolver DEFAULT_DTD_RESOLVER = new DTDEntityResolver();
-	public SAXReader createSAXReader(ErrorHandler errorHandler, EntityResolver entityResolver) {
-	public DOMReader createDOMReader() {
+	public DocumentFactory getDocumentFactory() {
-	public static Element generateDom4jElement(String elementName) {
-	public static DocumentFactory getDocumentFactory() {
-	public static void dump(Element element) {
+	public SAXReader createSAXReader(ErrorLogger errorLogger, EntityResolver entityResolver) {
-	public PropertyAccessor getPropertyAccessor(Class clazz) {
+	public PropertyAccessStrategy getPropertyAccessStrategy(Class clazz) throws MappingException {
+	public ServiceRegistry getServiceRegistry() {
-	public PropertyAccessor getPropertyAccessor(Class clazz) {
+	public PropertyAccessStrategy getPropertyAccessStrategy(Class clazz) throws MappingException {
-	public JoinedSubclass(PersistentClass superclass) {
+	public JoinedSubclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
+	public ServiceRegistry getServiceRegistry() {
+	public PersistentClass(MetadataBuildingContext metadataBuildingContext) {
+	public ServiceRegistry getServiceRegistry() {
-	public PropertyAccessor getPropertyAccessor(Class clazz) throws MappingException {
+	public PropertyAccessStrategy getPropertyAccessStrategy(Class clazz) throws MappingException {
+	public RootClass(MetadataBuildingContext metadataBuildingContext) {
+	public ServiceRegistry getServiceRegistry() {
-	public SingleTableSubclass(PersistentClass superclass) {
+	public SingleTableSubclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
-	public Subclass(PersistentClass superclass) {
+	public Subclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
-	public void setTypeUsingReflection(String className, String propertyName)
+	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
-	public UnionSubclass(PersistentClass superclass) {
+	public UnionSubclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
-public class BasicPropertyAccessor implements PropertyAccessor {
-	public static final class BasicSetter implements Setter {
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-		public Method getMethod() {
-		public String getMethodName() {
-		public String toString() {
-	public static final class BasicGetter implements Getter {
-		public Object get(Object target) throws HibernateException {
-		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
-		public Class getReturnType() {
-		public Member getMember() {
-		public Method getMethod() {
-		public String getMethodName() {
-		public String toString() {
-	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
-	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
-	public static Getter createGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
-public class ChainedPropertyAccessor implements PropertyAccessor {
-	public ChainedPropertyAccessor(PropertyAccessor[] chain) {
-	public Getter getGetter(Class theClass, String propertyName)
-	public Setter getSetter(Class theClass, String propertyName)
-public class DirectPropertyAccessor implements PropertyAccessor {
-	public static final class DirectGetter implements Getter {
-		public Object get(Object target) throws HibernateException {
-		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
-		public Member getMember() {
-		public Method getMethod() {
-		public String getMethodName() {
-		public Class getReturnType() {
-		public String toString() {
-	public static final class DirectSetter implements Setter {
-		public Method getMethod() {
-		public String getMethodName() {
-		public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException {
-		public String toString() {
-	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
-	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
-public class EmbeddedPropertyAccessor implements PropertyAccessor {
-	public static final class EmbeddedGetter implements Getter {
-		public Object get(Object target) throws HibernateException {
-		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
-		public Member getMember() {
-		public Method getMethod() {
-		public String getMethodName() {
-		public Class getReturnType() {
-		public String toString() {
-	public static final class EmbeddedSetter implements Setter {
-		public Method getMethod() {
-		public String getMethodName() {
-		public void set(Object target, Object value, SessionFactoryImplementor factory) {
-		public String toString() {
-	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
-	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
-public class IndexPropertyAccessor implements PropertyAccessor {
-	public IndexPropertyAccessor(String collectionRole, String entityName) {
-	public Setter getSetter(Class theClass, String propertyName) {
-	public Getter getGetter(Class theClass, String propertyName) {
-	public static final class IndexSetter implements Setter {
-		public Method getMethod() {
-		public String getMethodName() {
-		public void set(Object target, Object value, SessionFactoryImplementor factory) {
-	public class IndexGetter implements Getter {
-		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) throws HibernateException {
-		public Object get(Object target)  {
-		public Member getMember() {
-		public Method getMethod() {
-		public String getMethodName() {
-		public Class getReturnType() {
-public class MapAccessor implements PropertyAccessor {
-	public Getter getGetter(Class theClass, String propertyName)
-	public Setter getSetter(Class theClass, String propertyName)
-	public static final class MapSetter implements Setter {
-		public Method getMethod() {
-		public String getMethodName() {
-		public void set(Object target, Object value, SessionFactoryImplementor factory)
-	public static final class MapGetter implements Getter {
-		public Member getMember() {
-		public Method getMethod() {
-		public String getMethodName() {
-		public Object get(Object target) throws HibernateException {
-		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
-		public Class getReturnType() {
-public class NoopAccessor implements PropertyAccessor {
-	public Getter getGetter(Class arg0, String arg1) throws PropertyNotFoundException {
-	public Setter getSetter(Class arg0, String arg1) throws PropertyNotFoundException {
-		public Object get(Object target) throws HibernateException {
-		public Object getForInsert(Object target, Map map, SessionImplementor arg1)
-		public Class getReturnType() {
-		public Member getMember() {
-		public String getMethodName() {
-		public Method getMethod() {
-		public void set(Object target, Object value, SessionFactoryImplementor arg2) {
-		public String getMethodName() {
-		public Method getMethod() {
-public interface PropertyAccessor {
-	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException;
-	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException;
-public final class PropertyAccessorFactory {
-	public static PropertyAccessor getPropertyAccessor(Property property, EntityMode mode) throws MappingException {
-	public static PropertyAccessor getDynamicMapPropertyAccessor() throws MappingException {
-	public static PropertyAccessor getPropertyAccessor(Class optionalClass, String type) throws MappingException {
-	public static PropertyAccessor getPropertyAccessor(String type) throws MappingException {
+public abstract class AbstractFieldSerialForm implements Serializable {
+public class PropertyAccessBasicImpl implements PropertyAccess {
+	public PropertyAccessBasicImpl(
+	public PropertyAccessStrategy getPropertyAccessStrategy() {
+	public Getter getGetter() {
+	public Setter getSetter() {
+public class PropertyAccessEmbeddedImpl implements PropertyAccess {
+	public PropertyAccessEmbeddedImpl(
+	public PropertyAccessStrategy getPropertyAccessStrategy() {
+	public Getter getGetter() {
+	public Setter getSetter() {
+		public GetterImpl(Class containerType) {
+		public Object get(Object owner) {
+		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Class getReturnType() {
+		public Member getMember() {
+		public String getMethodName() {
+		public Method getMethod() {
+		public static final SetterImpl INSTANCE = new SetterImpl();
+		public void set(Object target, Object value, SessionFactoryImplementor factory) {
+		public String getMethodName() {
+		public Method getMethod() {
+public class PropertyAccessFieldImpl implements PropertyAccess {
+	public PropertyAccessFieldImpl(
+	public PropertyAccessStrategy getPropertyAccessStrategy() {
+	public Getter getGetter() {
+	public Setter getSetter() {
+public class PropertyAccessMapImpl implements PropertyAccess {
+	public PropertyAccessMapImpl(PropertyAccessStrategyMapImpl strategy, final String propertyName) {
+	public PropertyAccessStrategy getPropertyAccessStrategy() {
+	public Getter getGetter() {
+	public Setter getSetter() {
+	public static class GetterImpl implements Getter {
+		public GetterImpl(String propertyName) {
+		public Object get(Object owner) {
+		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Class getReturnType() {
+		public Member getMember() {
+		public String getMethodName() {
+		public Method getMethod() {
+	public static class SetterImpl implements Setter {
+		public SetterImpl(String propertyName) {
+		public void set(Object target, Object value, SessionFactoryImplementor factory) {
+		public String getMethodName() {
+		public Method getMethod() {
+public class PropertyAccessMixedImpl implements PropertyAccess {
+	public PropertyAccessMixedImpl(
+	public PropertyAccessStrategy getPropertyAccessStrategy() {
+	public Getter getGetter() {
+	public Setter getSetter() {
-public class BackrefPropertyAccessor implements PropertyAccessor {
+public class PropertyAccessStrategyBackRefImpl implements PropertyAccessStrategy {
-	public BackrefPropertyAccessor(String collectionRole, String entityName) {
+	public PropertyAccessStrategyBackRefImpl(String collectionRole, String entityName) {
-	public Setter getSetter(Class theClass, String propertyName) {
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
-	public Getter getGetter(Class theClass, String propertyName) {
-	public static final class BackrefSetter implements Setter {
-		public Method getMethod() {
+		public PropertyAccessBackRefImpl(PropertyAccessStrategyBackRefImpl strategy) {
-		public String getMethodName() {
+		public PropertyAccessStrategy getPropertyAccessStrategy() {
-		public void set(Object target, Object value, SessionFactoryImplementor factory) {
+		public Getter getGetter() {
+		public Setter getSetter() {
+		public GetterImpl(String entityName, String propertyName) {
+		public Object get(Object owner) {
-	public class BackrefGetter implements Getter {
-		public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) {
+		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Class getReturnType() {
-		public Object get(Object target) {
+		public String getMethodName() {
+		public static final SetterImpl INSTANCE = new SetterImpl();
+		public void set(Object target, Object value, SessionFactoryImplementor factory) {
-		public Class getReturnType() {
+		public Method getMethod() {
+public class PropertyAccessStrategyBasicImpl implements PropertyAccessStrategy {
+	public static final PropertyAccessStrategyBasicImpl INSTANCE = new PropertyAccessStrategyBasicImpl();
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, final String propertyName) {
+public class PropertyAccessStrategyChainedImpl implements PropertyAccessStrategy {
+	public PropertyAccessStrategyChainedImpl(PropertyAccessStrategy... chain) {
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
+public class PropertyAccessStrategyEmbeddedImpl implements PropertyAccessStrategy {
+	public static final PropertyAccessStrategyEmbeddedImpl INSTANCE = new PropertyAccessStrategyEmbeddedImpl();
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
+public class PropertyAccessStrategyFieldImpl implements PropertyAccessStrategy {
+	public static final PropertyAccessStrategyFieldImpl INSTANCE = new PropertyAccessStrategyFieldImpl();
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
+public class PropertyAccessStrategyIndexBackRefImpl implements PropertyAccessStrategy {
+	public PropertyAccessStrategyIndexBackRefImpl(String collectionRole, String entityName) {
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
+		public PropertyAccessIndexBackRefImpl(PropertyAccessStrategyIndexBackRefImpl strategy) {
+		public PropertyAccessStrategy getPropertyAccessStrategy() {
+		public Getter getGetter() {
+		public Setter getSetter() {
+		public GetterImpl(String entityName, String propertyName) {
+		public Object get(Object owner) {
+		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Class getReturnType() {
+		public Member getMember() {
+		public String getMethodName() {
+		public Method getMethod() {
+		public static final SetterImpl INSTANCE = new SetterImpl();
+		public void set(Object target, Object value, SessionFactoryImplementor factory) {
+		public String getMethodName() {
+		public Method getMethod() {
+public class PropertyAccessStrategyMapImpl implements PropertyAccessStrategy {
+	public static final PropertyAccessStrategyMapImpl INSTANCE = new PropertyAccessStrategyMapImpl();
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
+public class PropertyAccessStrategyMixedImpl implements PropertyAccessStrategy {
+	public static final PropertyAccessStrategyMixedImpl INSTANCE = new PropertyAccessStrategyMixedImpl();
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
+public class PropertyAccessStrategyNoopImpl implements PropertyAccessStrategy {
+	public static final PropertyAccessStrategyNoopImpl INSTANCE = new PropertyAccessStrategyNoopImpl();
+	public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
+		public static final PropertyAccessNoopImpl INSTANCE = new PropertyAccessNoopImpl();
+		public PropertyAccessStrategy getPropertyAccessStrategy() {
+		public Getter getGetter() {
+		public Setter getSetter() {
+		public static final GetterImpl INSTANCE = new GetterImpl();
+		public Object get(Object owner) {
+		public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+		public Class getReturnType() {
+		public Member getMember() {
+		public String getMethodName() {
+		public Method getMethod() {
+		public static final SetterImpl INSTANCE = new SetterImpl();
+		public void set(Object target, Object value, SessionFactoryImplementor factory) {
+		public String getMethodName() {
+		public Method getMethod() {
+public class PropertyAccessStrategyResolverInitiator implements StandardServiceInitiator<PropertyAccessStrategyResolver> {
+	public static final PropertyAccessStrategyResolverInitiator INSTANCE = new PropertyAccessStrategyResolverInitiator();
+	public Class<PropertyAccessStrategyResolver> getServiceInitiated() {
+	public PropertyAccessStrategyResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+public class PropertyAccessStrategyResolverStandardImpl implements PropertyAccessStrategyResolver {
+	public PropertyAccessStrategyResolverStandardImpl(ServiceRegistry serviceRegistry) {
+	public PropertyAccessStrategy resolvePropertyAccessStrategy(
+public enum BuiltInPropertyAccessStrategies {
+	public String getExternalName() {
+	public PropertyAccessStrategy getStrategy() {
+	public static BuiltInPropertyAccessStrategies interpret(String name) {
-	public Object get(Object owner) throws HibernateException;
-	public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) 
-	public Member getMember();
-	public Class getReturnType();
-	public String getMethodName();
-	public Method getMethod();
+public class GetterFieldImpl implements Getter {
+	public GetterFieldImpl(Class containerClass, String propertyName, Field field) {
+	public Object get(Object owner) {
+	public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+	public Class getReturnType() {
+	public Member getMember() {
+	public String getMethodName() {
+	public Method getMethod() {
+public class GetterMethodImpl implements Getter {
+	public GetterMethodImpl(Class containerClass, String propertyName, Method getterMethod) {
+	public Object get(Object owner) {
+	public Object getForInsert(Object owner, Map mergeMap, SessionImplementor session) {
+	public Class getReturnType() {
+	public Member getMember() {
+	public String getMethodName() {
+	public Method getMethod() {
+public interface PropertyAccess {
+public class PropertyAccessBuildingException extends HibernateException {
+	public PropertyAccessBuildingException(String message) {
+	public PropertyAccessBuildingException(String message, Throwable cause) {
+public class PropertyAccessException extends HibernateException {
+	public PropertyAccessException(String message) {
+	public PropertyAccessException(String message, Throwable cause) {
+public class PropertyAccessSerializationException extends HibernateException {
+	public PropertyAccessSerializationException(String message) {
+	public PropertyAccessSerializationException(String message, Throwable cause) {
+public interface PropertyAccessStrategy {
+public interface PropertyAccessStrategyResolver extends Service {
-	public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException;
-	public String getMethodName();
-	public Method getMethod();
+public class SetterFieldImpl implements Setter {
+	public SetterFieldImpl(Class containerClass, String propertyName, Field field) {
+	public void set(Object target, Object value, SessionFactoryImplementor factory) {
+	public String getMethodName() {
+	public Method getMethod() {
+public class SetterMethodImpl implements Setter {
+	public SetterMethodImpl(Class containerClass, String propertyName, Method setterMethod) {
+	public void set(Object target, Object value, SessionFactoryImplementor factory) {
+	public String getMethodName() {
+	public Method getMethod() {
-public class Dom4jInstantiator implements Instantiator {
-	public Dom4jInstantiator(Component component) {
-	public Dom4jInstantiator(PersistentClass mappingInfo) {
-	public Object instantiate(Serializable id) {
-	public Object instantiate() {
-	public boolean isInstance(Object object) {
-	public ComponentMetamodel(Component component) {
+	public ComponentMetamodel(Component component, MetadataBuildingOptions metadataBuildingOptions) {
+	public ComponentTuplizerFactory(MetadataBuildingOptions metadataBuildingOptions) {
+	public void prepare() {
+	public void release() {
+	public void prepare() {
+	public void release() {
+	public void prepare() {
+	public void release() {
+	public XMLHelper getXmlHelper() {
+	public ServiceRegistry getServiceRegistry() {
-	public AuditEntitiesConfiguration(Properties properties, String revisionInfoEntityName) {
+	public AuditEntitiesConfiguration(
-	public GlobalConfiguration(Map properties, ClassLoaderService classLoaderService) {
+	public GlobalConfiguration(
+	public EnversService getEnversService() {
-public class AuditConfiguration {
-	public AuditConfiguration(MetadataImplementor metadata, MappingCollector mappingCollector) {
-	public AuditEntitiesConfiguration getAuditEntCfg() {
-	public AuditProcessManager getSyncManager() {
-	public GlobalConfiguration getGlobalCfg() {
-	public EntitiesConfigurations getEntCfg() {
-	public RevisionInfoQueryCreator getRevisionInfoQueryCreator() {
-	public RevisionInfoNumberReader getRevisionInfoNumberReader() {
-	public ModifiedEntityNamesReader getModifiedEntityNamesReader() {
-	public AuditStrategy getAuditStrategy() {
-	public void destroy() {
+	public AbstractIdMapper(ServiceRegistry serviceRegistry) {
+	public ServiceRegistry getServiceRegistry() {
-	public EmbeddedIdMapper(PropertyData idPropertyData, Class compositeIdClass) {
+	public EmbeddedIdMapper(PropertyData idPropertyData, Class compositeIdClass, ServiceRegistry serviceRegistry) {
-	public MultipleIdMapper(Class compositeIdClass) {
+	public MultipleIdMapper(Class compositeIdClass, ServiceRegistry serviceRegistry) {
-	public SingleIdMapper() {
+	public SingleIdMapper(ServiceRegistry serviceRegistry) {
-	public SingleIdMapper(PropertyData propertyData) {
+	public SingleIdMapper(ServiceRegistry serviceRegistry, PropertyData propertyData) {
-	public ModifiedEntityNamesReader(Class<?> revisionInfoClass, PropertyData modifiedEntityNamesData) {
+	public ModifiedEntityNamesReader(
-	public RevisionInfoNumberReader(Class<?> revisionInfoClass, PropertyData revisionInfoIdData) {
+	public RevisionInfoNumberReader(Class<?> revisionInfoClass, PropertyData revisionInfoIdData, ServiceRegistry serviceRegistry) {
-	public static Getter getGetter(Class cls, PropertyData propertyData) {
+	public static Getter getGetter(Class cls, PropertyData propertyData, ServiceRegistry serviceRegistry) {
-	public static Getter getGetter(Class cls, String propertyName, String accessorType) {
+	public static Getter getGetter(Class cls, String propertyName, String accessorType, ServiceRegistry serviceRegistry) {
-	public static Setter getSetter(Class cls, PropertyData propertyData) {
+	public static Setter getSetter(Class cls, PropertyData propertyData, ServiceRegistry serviceRegistry) {
-	public static Setter getSetter(Class cls, String propertyName, String accessorType) {
+	public static Setter getSetter(Class cls, String propertyName, String accessorType, ServiceRegistry serviceRegistry) {
+	public void prepare() {
+	public void release() {
+					public EmbeddedCacheManager doWork(ClassLoader classLoader) {
-	public OsgiSessionFactoryService(
+	public OsgiSessionFactoryService(OsgiJtaPlatform osgiJtaPlatform, OsgiServiceUtil osgiServiceUtil) {
-public class ProxoolConnectionProvider implements ConnectionProvider, Configurable, Stoppable {
+public class ProxoolConnectionProvider
+	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
-public class BasicTestingJdbcServiceImpl implements JdbcServices {
+public class BasicTestingJdbcServiceImpl implements JdbcServices, ServiceRegistryAwareService {
+	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
+public class ClassLoaderAccessTestingImpl implements ClassLoaderAccess {
+	public static final ClassLoaderAccessTestingImpl INSTANCE = new ClassLoaderAccessTestingImpl();
+	public <T> Class<T> classForName(String name) {
+	public URL locateResource(String resourceName) {
+public class ClassLoaderServiceTestingImpl extends ClassLoaderServiceImpl {
+	public static final ClassLoaderServiceTestingImpl INSTANCE = new ClassLoaderServiceTestingImpl();
+public class DialectFactoryTestingImpl implements DialectFactory {
+	public DialectFactoryTestingImpl() {
+	public DialectFactoryTestingImpl(Dialect dialect) {
+	public Dialect buildDialect(Map configValues, DialectResolutionInfoSource resolutionInfoSource) {
+	public MetadataBuildingContextTestingImpl() {
+public class ServiceRegistryTestingImpl
+	public static ServiceRegistryTestingImpl forUnitTesting() {
+	public ServiceRegistryTestingImpl(

Lines added containing method: 280. Lines removed containing method: 225. Tot = 505
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public String getName() { ... }

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-		public String getMethodName() {
-		public String getMethodName() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Map getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getProxyInterface() {
-				proxyInterface = ReflectHelper.classForName( proxyInterfaceName );
+				proxyInterface = metadataBuildingContext.getClassLoaderAccess().classForName( proxyInterfaceName );
-		catch (ClassNotFoundException cnfe) {
-			throw new MappingException( "proxy class not found: " + proxyInterfaceName, cnfe );
+		catch (ClassLoadingException e) {
+			throw new MappingException( "proxy class not found: " + proxyInterfaceName, e );

Lines added: 3. Lines removed: 3. Tot = 6
—————————
Method found in diff:	public Class getMappedClass() throws MappingException {
+
-				mappedClass = ReflectHelper.classForName( className );
+				mappedClass = metadataBuildingContext.getClassLoaderAccess().classForName( className );
-		catch (ClassNotFoundException cnfe) {
-			throw new MappingException( "entity class not found: " + className, cnfe );
+		catch (ClassLoadingException e) {
+			throw new MappingException( "entity class not found: " + className, e );

Lines added: 4. Lines removed: 3. Tot = 7
—————————
Method found in diff:	-		public Class getReturnType() {
-		public Class getReturnType() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Iterator getPropertyClosureIterator() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-9838  5b1da924_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public ProxyFactory buildProxyFactory() {
+	public ProxyFactory buildProxyFactory(SessionFactoryImplementor sessionFactory) {
-	public ProxyFactory buildProxyFactory();
+	public ProxyFactory buildProxyFactory(SessionFactoryImplementor sessionFactory);
+	public DeserializationResolver getDeserializationResolver() {
+	public DeserializationResolver getDeserializationResolver() {
+			public SessionFactoryImplementor resolve() {
+	public SessionFactory findSessionFactory(String uuid, String name) {
-		public boolean isHandled(Method m) {
+	public JavassistLazyInitializer(
-	public static HibernateProxy getProxy(
-	public static HibernateProxy getProxy(
-	public static Class getProxyFactory(
+		public boolean isHandled(Method m) {
+	public JavassistProxyFactory() {
+	public static javassist.util.proxy.ProxyFactory buildJavassistProxyFactory(
+	public static HibernateProxy deserializeProxy(SerializableProxy serializableProxy) {
-	public SerializableProxy() {

Lines added containing method: 11. Lines removed containing method: 7. Tot = 18
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
35/After/ HHH-9937  6d77ac39_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public final void setSession(SessionImplementor session) {
+	public boolean isAttributeLoaded(String fieldName) {
+	public boolean isUninitialized() {

Lines added containing method: 3. Lines removed containing method: 0. Tot = 3
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getName
* getMethodName
* getSettings
* getReflectionOptimizer
* useReflectionOptimizer
* isAssignableFrom
* getProxyInterface
* getMappedClass
* getReturnType
* getPropertyClosureIterator
* isLazy
* getBytecodeProvider
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final Class getMappedClass() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
