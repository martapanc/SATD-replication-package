49/report.java
Satd-method: public SingleTableEntityPersister(
********************************************
********************************************
49/Between/ HHH-2394  1cd8db2a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+public class DynamicFilterAliasGenerator implements FilterAliasGenerator {
+	public DynamicFilterAliasGenerator(String[] tables, String rootAlias) {
+	public String getAlias(String table) {
+	public static String generateTableAlias(String rootAlias, int tableNumber) {
-	public FilterAliasGenerator getFilterAliasGenerator(final String rootAlias){
+	public static int getTableId(String tableName, String[] tables) {
+	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
+	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
+	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
+public class SecondaryTableTest extends BaseCoreFunctionalTestCase {
+	public void testFilter(){
+public class User {
+	public int getId() {
+	public void setId(int id) {
+	public String getEmailAddress() {
+	public void setEmailAddress(String emailAddress) {
+	public String getUsername() {
+	public void setUsername(String username) {
+	public String getPassword() {
+	public void setPassword(String password) {
+	public int getAge() {
+	public void setAge(int age) {
+	public boolean isLockedOut() {
+	public void setLockedOut(boolean lockedOut) {

Lines added containing method: 23. Lines removed containing method: 1. Tot = 24
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
Method found in diff:	+	public String getAlias(String table) {
+	public String getAlias(String table) {
+		if (table == null){
+			return rootAlias;
+		} else{
+			return AbstractEntityPersister.generateTableAlias(rootAlias, AbstractEntityPersister.getTableId(table, tables));
+		}
+	}

Lines added: 7. Lines removed: 0. Tot = 7
—————————
Method found in diff:	public final String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-4881: 11ef3e07_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public Object getDiscriminatorValue() {
+	public Object getDiscriminatorValue();
+	public Object getDiscriminatorValue() {
+	public Object getDiscriminatorValue() {

Lines added containing method: 4. Lines removed containing method: 0. Tot = 4
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
Method found in diff:	+	public Object getDiscriminatorValue() {
+	public Object getDiscriminatorValue() {
+		return discriminatorValue;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
49/Between/ HHH-6371  24edf42c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public String getTypeName() {
+	public String getExplicitTypeName() {
+	public void setExplicitTypeName(String explicitTypeName) {
+	public String getJavaTypeName() {
-	public void setTypeName(String typeName) {
+	public void setJavaTypeName(String javaTypeName) {
-	public Type getExplicitType() {
+	public boolean isToOne() {
-	public void setExplicitType(Type explicitType) {
+	public void setToOne(boolean toOne) {
+	public void setTypeParameters(Map<String, String> typeParameters) {
+	public Type getResolvedTypeMapping() {
+	public void setResolvedTypeMapping(Type resolvedTypeMapping) {
+					public String getColumnAttribute() {
+					public String getFormulaAttribute() {
+					public List getColumnOrFormulaElements() {
+							public String getColumnAttribute() {
+							public String getFormulaAttribute() {
+							public List getColumnOrFormulaElements() {
-						public void processBeanInfo(BeanInfo beanInfo) throws Exception {
+		public String getColumnAttribute();
+		public String getFormulaAttribute();
+		public List getColumnOrFormulaElements();
+public interface SingularAttributeSource extends MetaAttributeContainer {
+	public String getName();
+	public String getTypeAttribute();
+    public XMLTypeElement getType();
+	public String getAccess();

Lines added containing method: 23. Lines removed containing method: 5. Tot = 28
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
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+    public XMLTypeElement getType();
+    public XMLTypeElement getType();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	+	public String getName();
+	public String getName();

Lines added: 1. Lines removed: 0. Tot = 1
—————————
Method found in diff:	public boolean hasFormula() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-6447  acc93a3d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public static CacheDataDescriptionImpl decode(PluralAttributeBinding model) {
+	public static CacheDataDescriptionImpl decode(AbstractPluralAttributeBinding model) {
-	public Iterable<PluralAttributeBinding> getCollectionBindings();
+	public Iterable<AbstractPluralAttributeBinding> getCollectionBindings();
-	public void setValue(Value value) {
-	public Value getValue() {
+	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
-	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
+	public boolean isBasicPropertyAccessor() {
-	public Set<CascadeType> getCascadeTypes() {
+	public String getPropertyAccessorName() {
-	public boolean isOptimisticLockable() {
+	public void setPropertyAccessorName(String propertyAccessorName) {
-	public MetaAttributeContext getMetaAttributeContext() {
+	public boolean isIncludedInOptimisticLocking() {
-	public int getValuesSpan() {
+	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
-	public Iterable<SimpleValue> getValues() {
-	public String getPropertyAccessorName() {
-	public boolean isBasicPropertyAccessor() {
+	public MetaAttributeContext getMetaAttributeContext() {
-	public boolean hasFormula() {
+	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
-	public boolean isNullable() {
-	public boolean[] getColumnInsertability() {
-	public boolean[] getColumnUpdateability() {
+public abstract class AbstractPluralAttributeBinding extends AbstractAttributeBinding implements PluralAttributeBinding {
+	public boolean isAssociation() {
+	public Table getCollectionTable() {
+	public void setCollectionTable(Table collectionTable) {
+	public CollectionKey getCollectionKey() {
+	public void setCollectionKey(CollectionKey collectionKey) {
+	public CollectionElement getCollectionElement() {
+	public CascadeStyle getCascadeStyle() {
+	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
+	public FetchMode getFetchMode() {
+	public void setFetchMode(FetchMode fetchMode) {
+	public boolean isExtraLazy() {
+	public boolean isInverse() {
+	public boolean isMutable() {
+	public boolean isSubselectLoadable() {
+	public String getCacheConcurrencyStrategy() {
+	public String getCacheRegionName() {
+	public String getOrderBy() {
+	public String getWhere() {
+	public String getReferencedPropertyName() {
+	public boolean isSorted() {
+	public Comparator getComparator() {
+	public void setComparator(Comparator comparator) {
+	public String getComparatorClassName() {
+	public boolean isOrphanDelete() {
+	public int getBatchSize() {
+	public Class getCollectionPersisterClass() {
+	public void addFilter(String name, String condition) {
+	public java.util.Map getFilterMap() {
+	public CustomSQL getCustomSQLInsert() {
+	public CustomSQL getCustomSQLUpdate() {
+	public CustomSQL getCustomSQLDelete() {
+	public CustomSQL getCustomSQLDeleteAll() {
+	public String getLoaderName() {
+public abstract class AbstractSingularAttributeBinding
+	public SingularAttribute getAttribute() {
+	public Value getValue() {
+	public void setSimpleValueBindings(Iterable<SimpleValueBinding> simpleValueBindings) {
+	public int getSimpleValueSpan() {
+	public Iterable<SimpleValueBinding> getSimpleValueBindings() {
+	public boolean hasDerivedValue() {
+	public boolean isNullable() {
-	public Iterable<CascadeStyle> getCascadeStyles();
+	public CascadeStyle getCascadeStyle();
+	public FetchMode getFetchMode();
+	public void setFetchMode(FetchMode fetchMode);
-	public Value getValue();
-	public MetaAttributeContext getMetaAttributeContext();
+	public boolean isAssociation();
-	public int getValuesSpan();
-	public Iterable<SimpleValue> getValues();
+	public boolean isBasicPropertyAccessor();
-	public boolean isBasicPropertyAccessor();
+	public void setPropertyAccessorName(String propertyAccessorName);
-	public boolean hasFormula();
+	public boolean isIncludedInOptimisticLocking();
-	public boolean isAlternateUniqueKey();
+	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking);
-	public boolean isNullable();
+	public MetaAttributeContext getMetaAttributeContext();
-	public boolean[] getColumnUpdateability();
-	public boolean[] getColumnInsertability();
-	public boolean isSimpleValue();
+	public boolean isAlternateUniqueKey();
-public class BagBinding extends PluralAttributeBinding {
-	public BagBinding initialize(PluralAttributeBindingState bindingState) {
+public class BagBinding extends AbstractPluralAttributeBinding {
-	public BasicCollectionElement(PluralAttributeBinding binding) {
+	public BasicCollectionElement(AbstractPluralAttributeBinding binding) {
-	public final CollectionElementType getCollectionElementType() {
+	public AbstractPluralAttributeBinding getCollectionBinding() {
+	public Value getElementValue() {
+	public final CollectionElementNature getCollectionElementNature() {
-	public boolean isOneToMany() {
+	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
-	public boolean isManyToMany() {
-public interface SimpleAttributeBindingState extends AttributeBindingState {
-	public PropertyGeneration getPropertyGeneration();
+public enum CollectionElementNature {
-public enum CollectionElementType {
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-		public boolean isOneToMany() {
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-		public boolean isManyToMany() {
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-		public boolean isManyToAny() {
-		public CollectionElement createCollectionElementInternal(PluralAttributeBinding attributeBinding) {
-	public String getName() {
-	public String toString() {
-	public boolean isOneToMany() {
-	public boolean isManyToMany() {
-	public boolean isManyToAny() {
-	public CollectionKey(PluralAttributeBinding collection) {
+	public CollectionKey(AbstractPluralAttributeBinding collection) {
-	public CompositeCollectionElement(PluralAttributeBinding binding) {
+	public CompositeCollectionElement(AbstractPluralAttributeBinding binding) {
+	public TableSpecification getTable(String containingTableName) {
-	public void setVersionBinding(SimpleAttributeBinding versionBinding) {
+	public void setVersionBinding(SimpleSingularAttributeBinding versionBinding) {
-	public SimpleAttributeBinding getVersioningValueBinding() {
+	public SimpleSingularAttributeBinding getVersioningValueBinding() {
-	public SimpleAttributeBinding makeSimpleIdAttributeBinding(Attribute attribute) {
+	public SimpleSingularAttributeBinding makeSimpleIdAttributeBinding(SingularAttribute attribute) {
-	public EntityDiscriminator makeEntityDiscriminator(Attribute attribute) {
-	public SimpleAttributeBinding makeVersionBinding(Attribute attribute) {
+//	public EntityDiscriminator makeEntityDiscriminator(Attribute attribute) {
+	public SimpleSingularAttributeBinding makeVersionBinding(SingularAttribute attribute) {
-	public SimpleAttributeBinding makeSimpleAttributeBinding(Attribute attribute) {
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
-	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(Attribute attribute) {
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
-	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementType collectionElementType) {
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
-	public SimpleAttributeBinding getValueBinding() {
+	public SimpleSingularAttributeBinding getValueBinding() {
-	public EntityDiscriminator initialize(ValueRelationalState state) {
-	public SimpleAttributeBinding getValueBinding() {
+	public SimpleSingularAttributeBinding getValueBinding() {
-	public void setValueBinding(SimpleAttributeBinding attributeBinding) {
+	public void setValueBinding(SimpleSingularAttributeBinding attributeBinding) {
-	public boolean isUpdatable();
-public class ManyToOneAttributeBinding extends SimpleAttributeBinding implements SingularAssociationAttributeBinding {
+public class ManyToOneAttributeBinding extends SimpleSingularAttributeBinding implements SingularAssociationAttributeBinding {
-	public final ManyToOneAttributeBinding initialize(ManyToOneAttributeBindingState state) {
-	public final ManyToOneAttributeBinding initialize(ManyToOneRelationalState state) {
+	public boolean isAssociation() {
-	public Iterable<CascadeStyle> getCascadeStyles() {
+	public CascadeStyle getCascadeStyle() {
+	public FetchMode getFetchMode() {
+	public void setFetchMode(FetchMode fetchMode) {
-	public boolean isSimpleValue() {
-	public void validate() {
+//	public void validate() {
-public abstract class PluralAttributeBinding extends AbstractAttributeBinding {
-	public boolean isSimpleValue() {
-	public Table getCollectionTable() {
-	public void setCollectionTable(Table collectionTable) {
-	public CollectionKey getCollectionKey() {
-	public void setCollectionKey(CollectionKey collectionKey) {
-	public CollectionElement getCollectionElement() {
-	public boolean isExtraLazy() {
-	public boolean isInverse() {
-	public boolean isMutable() {
-	public boolean isSubselectLoadable() {
-	public String getCacheConcurrencyStrategy() {
-	public String getCacheRegionName() {
-	public String getOrderBy() {
-	public String getWhere() {
-	public String getReferencedPropertyName() {
-	public boolean isSorted() {
-	public Comparator getComparator() {
-	public void setComparator(Comparator comparator) {
-	public String getComparatorClassName() {
-	public boolean isOrphanDelete() {
-	public int getBatchSize() {
-	public boolean isOptimisticLocked() {
-	public Class getCollectionPersisterClass() {
-	public void addFilter(String name, String condition) {
-	public java.util.Map getFilterMap() {
-	public CustomSQL getCustomSQLInsert() {
+public interface PluralAttributeBinding extends AttributeBinding, AssociationAttributeBinding {
-	public CustomSQL getCustomSQLUpdate() {
+	public CollectionKey getCollectionKey();
-	public CustomSQL getCustomSQLDelete() {
+	public CollectionElement getCollectionElement();
-	public CustomSQL getCustomSQLDeleteAll() {
+	public Table getCollectionTable();
-	public String getLoaderName() {
-public class SimpleAttributeBinding extends AbstractAttributeBinding implements SingularAttributeBinding, KeyValueBinding {
+public class SimpleSingularAttributeBinding
-	public final SimpleAttributeBinding initialize(SimpleAttributeBindingState state) {
-	public SimpleAttributeBinding initialize(ValueRelationalState state) {
+	public boolean isAssociation() {
-	public SingularAttribute getAttribute() {
+	public String getUnsavedValue() {
-	public boolean isSimpleValue() {
+	public void setUnsavedValue(String unsavedValue) {
-	public boolean isInsertable() {
+	public PropertyGeneration getGeneration() {
-	public void setInsertable(boolean insertable) {
+	public void setGeneration(PropertyGeneration generation) {
-	public boolean isUpdatable() {
+	public boolean isIncludedInOptimisticLocking() {
-	public void setUpdatable(boolean updatable) {
+	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
-	public String getUnsavedValue() {
-	public void setUnsavedValue(String unsaveValue) {
-	public PropertyGeneration getGeneration() {
-	public void setGeneration(PropertyGeneration generation) {
-	public String getPropertyAccessorName() {
-	public void setPropertyAccessorName(String propertyAccessorName) {
-	public boolean isIncludedInOptimisticLocking() {
-	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
+public class SimpleValueBinding {
+	public SimpleValueBinding() {
+	public SimpleValueBinding(SimpleValue simpleValue) {
+	public SimpleValueBinding(SimpleValue simpleValue, boolean includeInInsert, boolean includeInUpdate) {
+	public SimpleValueBinding(boolean includeInInsert, boolean includeInUpdate) {
+	public SimpleValue getSimpleValue() {
+	public void setSimpleValue(SimpleValue simpleValue) {
+	public boolean isDerived() {
+	public boolean isNullable() {
+	public boolean isIncludeInInsert() {
+	public void setIncludeInInsert(boolean includeInInsert) {
+	public boolean isIncludeInUpdate() {
+	public void setIncludeInUpdate(boolean includeInUpdate) {
+	public Value getValue();
+	public int getSimpleValueSpan();
+	public Iterable<SimpleValueBinding> getSimpleValueBindings();
+	public void setSimpleValueBindings(Iterable<SimpleValueBinding> simpleValueBindings);
+	public boolean hasDerivedValue();
+	public boolean isNullable();
+	public PropertyGeneration getGeneration();
-	public void addCollection(PluralAttributeBinding collectionBinding);
+	public void addCollection(AbstractPluralAttributeBinding collectionBinding);
+	public boolean isIncludedInInsert() {
+	public boolean isIncludedInUpdate() {
+	public String getContainingTableName() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
-	public Iterable<CascadeStyle> getCascadeStyle() {
+	public Iterable<CascadeStyle> getCascadeStyles() {
+	public FetchMode getFetchMode() {
+		public String getLogicalName() {
+	public FetchMode getFetchMode();
+	public String getPropertyAccessorName();
+	public boolean isIncludedInOptimisticLocking();
+	public boolean isIncludedInInsert();
+	public boolean isIncludedInUpdate();
+public enum PluralAttributeElementNature {
+public enum PluralAttributeNature {
-public interface ManyToOneAttributeBindingState extends SimpleAttributeBindingState {
+public interface PluralAttributeSource extends AttributeSource {
+	public PluralAttributeNature getPluralAttributeNature();
+	public PluralAttributeElementNature getPluralAttributeElementNature();
+	public String getContainingTableName();
+	public boolean areValuesIncludedInInsertByDefault();
+	public boolean areValuesIncludedInUpdateByDefault();
+	public boolean areValuesNullableByDefault();
+	public String getLogicalName();
+	public boolean isIncludedInInsert() {
+	public boolean isIncludedInUpdate() {
+	public String getContainingTableName() {
+	public boolean isIncludedInInsert() {
+	public boolean isIncludedInUpdate() {
+	public String getContainingTableName() {
+	public String getContainingTableName() {
-	public static MetaAttributeContext extractMetaAttributeContext(
-	public static boolean getBooleanValue(String value, boolean defaultValue) {
-	public static Set<String> getStringValueTokens(String str, String delimiters) {
-	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
+		public String getContainingTableName();
+		public boolean isIncludedInInsertByDefault();
+		public boolean isIncludedInUpdateByDefault();
+	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
+					public String getContainingTableName() {
+					public boolean isIncludedInInsertByDefault() {
+					public boolean isIncludedInUpdateByDefault() {
+	public FetchMode getFetchMode() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+					public String getContainingTableName() {
+					public boolean isIncludedInInsertByDefault() {
+					public boolean isIncludedInUpdateByDefault() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+			public String getLogicalName() {
+					public String getContainingTableName() {
+					public boolean isIncludedInInsertByDefault() {
+					public boolean isIncludedInUpdateByDefault() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+				public String getLogicalName() {
+				public String getLogicalName() {
+					public String getContainingTableName() {
+					public boolean isIncludedInInsertByDefault() {
+					public boolean isIncludedInUpdateByDefault() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+					public String getContainingTableName() {
+					public boolean isIncludedInInsertByDefault() {
+					public boolean isIncludedInUpdateByDefault() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
-	public PluralAttributeBinding getCollection(String collectionRole) {
+	public AbstractPluralAttributeBinding getCollection(String collectionRole) {
-	public Iterable<PluralAttributeBinding> getCollectionBindings() {
+	public Iterable<AbstractPluralAttributeBinding> getCollectionBindings() {
-	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
+	public void addCollection(AbstractPluralAttributeBinding pluralAttributeBinding) {
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
-	public static VersionProperty buildVersionProperty(SimpleAttributeBinding property, boolean lazyAvailable) {
+	public static VersionProperty buildVersionProperty(SimpleSingularAttributeBinding property, boolean lazyAvailable) {
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {
-		public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
+		public Class<? extends CollectionPersister> getCollectionPersisterClass(AbstractPluralAttributeBinding metadata) {

Lines added containing method: 198. Lines removed containing method: 133. Tot = 331
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
Method found in diff:	+	public CustomSQL getCustomSQLUpdate() {
+	public CustomSQL getCustomSQLUpdate() {
+		return customSQLUpdate;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public String getDiscriminatorValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDefaultCatalogName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getDefaultSchemaName();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public TableSpecification getTable(String containingTableName) {
+	public TableSpecification getTable(String containingTableName) {
+		// todo : implement this for secondary table look ups.  for now we just return the base table
+		return baseTable;
+	}

Lines added: 4. Lines removed: 0. Tot = 4
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public CustomSQL getCustomSQLInsert() {
+	public CustomSQL getCustomSQLInsert() {
+		return customSQLInsert;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public String getName() {
-	public String getName() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	+	public CustomSQL getCustomSQLDelete() {
+	public CustomSQL getCustomSQLDelete() {
+		return customSQLDelete;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
—————————
Method found in diff:	public boolean isCascadeDeleteEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public boolean hasFormula() {
-	public boolean hasFormula() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	public boolean isInverse() {
+	public boolean isInverse() {
+		return inverse;
+	}

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
49/Between/ HHH-6471  46102a2b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public EntityBinding(InheritanceType inheritanceType, EntityMode entityMode) {
+	public EntityBinding(EntityBinding superEntityBinding) {
+	public HierarchyDetails getHierarchyDetails() {
+	public EntityBinding getSuperEntityBinding() {
+	public boolean isRoot() {
-	public boolean isRoot() {
-	public void setInheritanceType(InheritanceType entityInheritanceType) {
-	public InheritanceType getInheritanceType() {
-	public void setSuperEntityBinding(EntityBinding superEntityBinding) {
-	public EntityBinding getSuperEntityBinding() {
-	public EntityIdentifier getEntityIdentifier() {
-	public EntityDiscriminator getEntityDiscriminator() {
-	public void setEntityDiscriminator(EntityDiscriminator entityDiscriminator) {
-	public String getDiscriminatorValue() {
-	public void setDiscriminatorValue(String discriminatorValue) {
-	public void setVersionBinding(SimpleSingularAttributeBinding versionBinding) {
-	public SimpleSingularAttributeBinding getVersioningValueBinding() {
+	public String getDiscriminatorMatchValue() {
-	public Caching getCaching() {
-	public void setCaching(Caching caching) {
-	public boolean isExplicitPolymorphism() {
-	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
-	public OptimisticLockStyle getOptimisticLockStyle() {
-	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
-	public EntityMode getEntityMode() {
-	public void setEntityMode(EntityMode entityMode) {
+public class HierarchyDetails {
+	public HierarchyDetails(EntityBinding rootEntityBinding, InheritanceType inheritanceType, EntityMode entityMode) {
+	public EntityBinding getRootEntityBinding() {
+	public InheritanceType getInheritanceType() {
+	public EntityMode getEntityMode() {
+	public EntityIdentifier getEntityIdentifier() {
+	public EntityDiscriminator getEntityDiscriminator() {
+	public OptimisticLockStyle getOptimisticLockStyle() {
+	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
+	public void setEntityDiscriminator(EntityDiscriminator entityDiscriminator) {
+	public SimpleSingularAttributeBinding getVersioningAttributeBinding() {
+	public void setVersioningAttributeBinding(SimpleSingularAttributeBinding versioningAttributeBinding) {
+	public Caching getCaching() {
+	public void setCaching(Caching caching) {
+	public boolean isExplicitPolymorphism() {
+	public void setExplicitPolymorphism(boolean explicitPolymorphism) {

Lines added containing method: 22. Lines removed containing method: 20. Tot = 42
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
Method found in diff:	-	public String getDiscriminatorValue() {
-	public String getDiscriminatorValue() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public TableSpecification getTable(String containingTableName) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-6471  486352ea_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public void setDiscriminatorMatchValue(String discriminatorMatchValue) {

Lines added containing method: 1. Lines removed containing method: 0. Tot = 1
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
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public TableSpecification getTable(String containingTableName) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-6472  9ec53fa1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public SimpleSingularAttributeBinding getValueBinding() {
+	public SimpleValue getBoundValue() {
+	public void setBoundValue(SimpleValue boundValue) {
-	public void setValueBinding(SimpleSingularAttributeBinding valueBinding) {
+	public HibernateTypeDescriptor getExplicitHibernateTypeDescriptor() {
-public class ColumnSourceImpl implements ColumnSource {
+public class ColumnSourceImpl extends ColumnValuesSourceImpl {
-	public boolean isNullable() {
-	public String getDefaultValue() {
-	public String getSqlType() {
-	public Datatype getDatatype() {
-	public Size getSize() {
-	public boolean isUnique() {
-	public String getComment() {
-	public boolean isIncludedInInsert() {
-	public boolean isIncludedInUpdate() {
-	public String getContainingTableName() {
+public class ColumnValuesSourceImpl implements ColumnSource {
+	public ColumnValuesSourceImpl(ColumnValues columnValues) {
+	public String getName() {
+	public boolean isNullable() {
+	public String getDefaultValue() {
+	public String getSqlType() {
+	public Datatype getDatatype() {
+	public Size getSize() {
+	public boolean isUnique() {
+	public String getComment() {
+	public boolean isIncludedInInsert() {
+	public boolean isIncludedInUpdate() {
+	public String getContainingTableName() {
+	public String getReadFragment() {
+	public String getWriteFragment() {
+	public String getCheckCondition() {
-public class DiscriminatorSourceImpl extends SingularAttributeSourceImpl implements DiscriminatorSource {
+public class DiscriminatorSourceImpl implements DiscriminatorSource {
-	public DiscriminatorSourceImpl(SimpleAttribute attribute) {
+	public DiscriminatorSourceImpl(EntityClass entityClass) {
+	public RelationalValueSource getDiscriminatorRelationalValueSource() {
+	public String getExplicitHibernateTypeName() {
-	public static SimpleAttribute createDiscriminatorAttribute(Map<DotName, List<AnnotationInstance>> annotations) {
-	public boolean isDiscriminator() {
+	public DiscriminatorColumnValues getDiscriminatorColumnValues() {
-	public SimpleAttribute getDiscriminatorAttribute() {
+	public Class<?> getDiscriminatorType() {
+	public String getDiscriminatorMatchValue() {
-	public String getDiscriminatorValue() {
+	public String getDiscriminatorMatchValue() {
-public interface DiscriminatorSource extends SingularAttributeSource {
+public interface DiscriminatorSource {
+	public RelationalValueSource getDiscriminatorRelationalValueSource();
+	public String getExplicitHibernateTypeName();
-	public String getDiscriminatorValue();
+	public String getDiscriminatorMatchValue();
-	public String getDiscriminatorValue() {
+	public String getDiscriminatorMatchValue() {
+			public RelationalValueSource getDiscriminatorRelationalValueSource() {
+			public String getExplicitHibernateTypeName() {
+			public boolean isForced() {
+			public boolean isInserted() {

Lines added containing method: 37. Lines removed containing method: 22. Tot = 59
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
Method found in diff:	-	public String getDiscriminatorValue() {
-	public String getDiscriminatorValue() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {
-		return columnValues.getName().isEmpty() ? attribute.getName() : columnValues.getName();
-	}
-
-	@Override
-	public boolean isNullable() {
-		return columnValues.isNullable();
-	}
-
-	@Override
-	public String getDefaultValue() {
-		// todo
-		return null;
-	}
-
-	@Override
-	public String getSqlType() {
-		// todo
-		return null;
-	}
-
-	@Override
-	public Datatype getDatatype() {
-		// todo
-		return null;
-	}
-
-	@Override
-	public Size getSize() {
-		return new Size(
-				columnValues.getPrecision(),
-				columnValues.getScale(),
-				columnValues.getLength(),
-				Size.LobMultiplier.NONE
-		);
+		return super.getName().isEmpty() ? attribute.getName() : super.getName();

Lines added: 1. Lines removed: 34. Tot = 35
—————————
Method found in diff:	public boolean isOptional() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-6480  e5400897_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public EntityBinding getEntityBinding() {
+	public AttributeBindingContainer getContainer() {
+public abstract class AbstractAttributeBindingContainer implements AttributeBindingContainer {
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
+	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+	public AttributeBinding locateAttributeBinding(String name) {
+	public Iterable<AttributeBinding> attributeBindings() {
+	public int getAttributeBindingClosureSpan() {
+	public Iterable<AttributeBinding> getAttributeBindingClosure() {
-	public EntityBinding getEntityBinding();
+	public AttributeBindingContainer getContainer();
+public interface AttributeBindingContainer {
+	public String getPathBase();
+	public AttributeContainer getAttributeContainer();
+	public Iterable<AttributeBinding> attributeBindings();
+	public AttributeBinding locateAttributeBinding(String name);
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute);
+	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute);
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute);
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature);
+	public EntityBinding seekEntityBinding();
+	public TableSpecification getPrimaryTable();
+	public TableSpecification locateTable(String containingTableName);
+	public Class<?> getClassReference();
+	public MetaAttributeContext getMetaAttributeContext();
+public class ComponentAttributeBinding extends AbstractSingularAttributeBinding implements AttributeBindingContainer {
+	public ComponentAttributeBinding(AttributeBindingContainer container, SingularAttribute attribute) {
+	public EntityBinding seekEntityBinding() {
+	public String getPathBase() {
+	public AttributeContainer getAttributeContainer() {
+	public Component getComponent() {
+	public boolean isAssociation() {
+	public MetaAttributeContext getMetaAttributeContext() {
+	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
+	public TableSpecification getPrimaryTable() {
+	public TableSpecification locateTable(String containingTableName) {
+	public AttributeBinding locateAttributeBinding(String name) {
+	public Iterable<AttributeBinding> attributeBindings() {
+	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
+	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
+	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
+	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+	public Class<?> getClassReference() {
+	public SingularAttribute getParentReference() {
+	public void setParentReference(SingularAttribute parentReference) {
+	public PropertyGeneration getGeneration() {
-public class EntityBinding {
+public class EntityBinding extends AbstractAttributeBindingContainer {
-	public TableSpecification getBaseTable() {
+	public TableSpecification getPrimaryTable() {
-	public TableSpecification getTable(String tableName) {
+	public TableSpecification locateTable(String tableName) {
-	public Iterable<AttributeBinding> getAttributeBindings() {
-	public AttributeBinding getAttributeBinding(String name) {
-	public int getAttributeBindingClosureSpan() {
-	public Iterable<AttributeBinding> getAttributeBindingClosure() {
-	public SimpleSingularAttributeBinding makeSimpleAttributeBinding(SingularAttribute attribute) {
+	public EntityBinding seekEntityBinding() {
-	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
+	public String getPathBase() {
-	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
+	public Class<?> getClassReference() {
+	public AttributeContainer getAttributeContainer() {
-	public LocalBindingContext getBindingContext() {
+	public LocalBindingContext getLocalBindingContext() {
+	public String getPath() {
+	public String getPath();
+	public LocalBindingContext getLocalBindingContext();
+public interface ComponentAttributeSource extends SingularAttributeSource, AttributeSourceContainer {
+	public String getClassName();
+	public Value<Class<?>> getClassReference();
+	public String getParentReferenceAttributeName();
-	public LocalBindingContext getBindingContext();
+	public LocalBindingContext getLocalBindingContext();
-	public LocalBindingContext getBindingContext() {
+	public LocalBindingContext getLocalBindingContext() {
+	public String getPath() {
+public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
+	public ComponentAttributeSourceImpl(
+	public String getClassName() {
+	public Value<Class<?>> getClassReference() {
+	public String getPath() {
+	public LocalBindingContext getLocalBindingContext() {
+	public String getParentReferenceAttributeName() {
+	public Iterable<AttributeSource> attributeSources() {
+	public boolean isVirtualAttribute() {
+	public SingularAttributeNature getNature() {
+	public ExplicitHibernateTypeSource getTypeInformation() {
+	public String getName() {
+	public boolean isSingular() {
+	public String getPropertyAccessorName() {
+	public boolean isInsertable() {
+	public boolean isUpdatable() {
+	public PropertyGeneration getGeneration() {
+	public boolean isLazy() {
+	public boolean isIncludedInOptimisticLocking() {
+	public Iterable<MetaAttributeSource> metaAttributes() {
+	public boolean areValuesIncludedInInsertByDefault() {
+	public boolean areValuesIncludedInUpdateByDefault() {
+	public boolean areValuesNullableByDefault() {
+	public List<RelationalValueSource> relationalValueSources() {
+	public String getDiscriminatorMatchValue() {
+	public String getDiscriminatorMatchValue() {
-	public abstract MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
+	public void testSimpleEntityWithSimpleComponentMapping() {
+	public abstract void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
+	public abstract void addSourcesForSimpleEntityBinding(MetadataSources sources);
-	public abstract MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources);
+	public abstract void addSourcesForManyToOne(MetadataSources sources);
-	public abstract MetadataImpl addSourcesForManyToOne(MetadataSources sources);
+	public abstract void addSourcesForComponentBinding(MetadataSources sources);
-	public MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources) {
+	public void addSourcesForSimpleEntityBinding(MetadataSources sources) {
-	public MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
+	public void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
-	public MetadataImpl addSourcesForManyToOne(MetadataSources sources) {
+	public void addSourcesForManyToOne(MetadataSources sources) {
+	public void addSourcesForComponentBinding(MetadataSources sources) {
-	public MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources) {
+	public void addSourcesForSimpleEntityBinding(MetadataSources sources) {
-	public MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
+	public void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
-	public MetadataImpl addSourcesForManyToOne(MetadataSources sources) {
+	public void addSourcesForManyToOne(MetadataSources sources) {
+	public void addSourcesForComponentBinding(MetadataSources sources) {
+public class SimpleEntityWithSimpleComponent {
+	public SimpleEntityWithSimpleComponent() {
+	public SimpleEntityWithSimpleComponent(String name) {
+	public Long getId() {
+	public void setId(Long id) {
+	public String getName() {
+	public void setName(String name) {
+	public SimpleComponent getSimpleComponent() {
+	public void setSimpleComponent(SimpleComponent simpleComponent) {
+	public static class SimpleComponent {
+		public String getValue1() {
+		public void setValue1(String value1) {
+		public String getValue2() {
+		public void setValue2(String value2) {

Lines added containing method: 117. Lines removed containing method: 24. Tot = 141
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
Method found in diff:	public CustomSQL getCustomSQLUpdate() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public TableSpecification getTable(String tableName) {
-	public TableSpecification getTable(String tableName) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public CustomSQL getCustomSQLInsert() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public CustomSQL getCustomSQLDelete() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-6498  dc7feab0_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public int getSubEntityBindingSpan() {
+	public int getSubEntityBindingClosureSpan() {
+	public Iterable<EntityBinding> getDirectSubEntityBindings() {
-	public Iterable<EntityBinding> getSubEntityBindingClosure() {
+	public Iterable<EntityBinding> getPostOrderSubEntityBindingClosure() {
+	public Iterable<EntityBinding> getPreOrderSubEntityBindingClosure() {
+	public boolean isDiscriminatorMatchValueNull() {
+	public boolean isDiscriminatorMatchValueNotNull() {
+	public Iterable<AttributeBinding> getSubEntityAttributeBindingClosure() {
+	public void testPreOrderRootSubEntityClosure() {
+	public void testPostOrderRootSubEntityClosure() {
+public class PartTimeEmployee extends Employee {
+	public int getPercent() {
+	public void setPercent(int percent) {
+public class SimpleInheritanceTest extends BaseCoreFunctionalTestCase {
+	public void configure(Configuration cfg) {
+	public String[] getMappings() {
+	public void testDiscriminatorSubclass() {
+	public void testAccessAsIncorrectSubclass() {
+	public void testQuerySubclassAttribute() {
+	public void testLoadSuperclassProxyPolymorphicAccess() {
+	public void testLoadSuperclassProxyEvictPolymorphicAccess() {

Lines added containing method: 20. Lines removed containing method: 2. Tot = 22
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
Method found in diff:	public boolean isLazy() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-6974  ef22e310_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+public class NaturalIdCacheKey implements Serializable {
+	public NaturalIdCacheKey(
+	public String toString() {
+	public Serializable[] getNaturalId() {
+	public Type[] getTypes() {
+	public int hashCode() {
+	public boolean equals(Object obj) {
+	public String getEntityOrRoleName() {
-public interface NaturalIdRegionAccessStrategy extends RegionAccessStrategy{
+public interface NaturalIdRegionAccessStrategy extends RegionAccessStrategy {
+	public boolean insert(Object key, Object value) throws CacheException;
+	public boolean afterInsert(Object key, Object value) throws CacheException;
+	public boolean update(Object key, Object value) throws CacheException;
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException;
+	public void loadedStateDeletedNotification(EntityEntry entityEntry) {
+	public void loadedStateInsertedNotification(EntityEntry entityEntry) {
-		public EntityPersister getPersister() {
-		public Object[] getValues() {
-		public boolean equals(Object other) {
-		public int hashCode() {
+	public void cacheNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues, CachedNaturalIdValueSource valueSource ) {
+				public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+				public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-	public void cacheNaturalIdResolution(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
+	public void evictNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues ) {
+	public void loadedStateInsertedNotification(EntityEntry entityEntry);
+	public void loadedStateDeletedNotification(EntityEntry entityEntry);
-	public void cacheNaturalIdResolution(EntityPersister persister, Serializable pk, Object[] naturalId);
+	public enum CachedNaturalIdValueSource {
+	public void cacheNaturalIdResolution(EntityPersister persister, Serializable pk, Object[] naturalId, CachedNaturalIdValueSource valueSource);
+	public void evictNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues );
+	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
+	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy();
+		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
+	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {

Lines added containing method: 28. Lines removed containing method: 7. Tot = 35
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
Method found in diff:	public Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCascadeDeleteEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-7214  9fc22a49_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+public class DuplicatedDiscriminatorValueTest extends BaseUnitTestCase {
+	public void testDuplicatedDiscriminatorValueSameHierarchy() {
+	public void testDuplicatedDiscriminatorValueDifferentHierarchy() {

Lines added containing method: 3. Lines removed containing method: 0. Tot = 3
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
49/Between/ HHH-8276  ba0d1454_diff.java
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
49/Between/ HHH-8276  dc7cdf9d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+public class PropertySetterAccessException extends PropertyAccessException {
+	public PropertySetterAccessException(
+	public String toString() {
+	public static String scalarName(String base, int num) {
+	public static String[] scalarNames(String base, int count) {
+	public static String[] scalarNames(int uniqueness, int count) {
+	public List getAssociations() {
+public abstract class AbstractBatchingEntityLoaderBuilder extends BatchingEntityLoaderBuilder {
-//	public EntityLoader(
-//	public EntityLoader(
-	public EntityLoader(
+	public static Builder forEntity(OuterJoinLoadable persister) {
-	public EntityLoader(
+	public static class Builder {
-	public EntityLoader(
+		public Builder(OuterJoinLoadable persister) {
+		public Builder withBatchSize(int batchSize) {
+		public Builder withInfluencers(LoadQueryInfluencers influencers) {
+		public Builder withLockMode(LockMode lockMode) {
+		public Builder withLockOptions(LockOptions lockOptions) {
+		public EntityLoader byPrimaryKey() {
+		public EntityLoader byUniqueKey(String[] keyColumnNames, Type keyType) {
+						public LoadQueryInfluencers getQueryInfluencers() {
+						public int getBatchSize() {
+						public LockMode getLockMode() {
+						public LockOptions getLockOptions() {
-	public EntityLoader(
-//	public Object loadByUniqueKey(SessionImplementor session,Object key) {
-//    public int[][] getCompositeKeyManyToOneTargetIndices() {
-public class LegacyBatchingEntityLoaderBuilder extends BatchingEntityLoaderBuilder {
+public class LegacyBatchingEntityLoaderBuilder extends AbstractBatchingEntityLoaderBuilder {
-public abstract class AbstractEntityLoadQueryImpl extends AbstractLoadQueryImpl {
-	public AbstractEntityLoadQueryImpl(EntityReturn entityReturn, List<JoinableAssociation> associations) {
-	public String toString() {
-public class EntityLoadQueryBuilderImpl implements LoadQueryBuilder {
-	public EntityLoadQueryBuilderImpl(
-	public String generateSql(
-	public String generateSql(
-		public void handleEntityReturn(EntityReturn rootEntityReturn) {
-		public void startingRootReturn(Return rootReturn) {
-		public void finishingRootReturn(Return rootReturn) {
-		public void startingEntityFetch(EntityFetch entityFetch) {
-		public void finishingEntityFetch(EntityFetch entityFetch) {
-		public void startingCollectionFetch(CollectionFetch collectionFetch) {
-		public void finishingCollectionFetch(CollectionFetch collectionFetch) {
-		public void startingCompositeFetch(CompositeFetch fetch) {
-		public void finishingCompositeFetch(CompositeFetch fetch) {
-		public void finish(LoadPlan loadPlan) {
-public class EntityLoadQueryImpl extends AbstractEntityLoadQueryImpl {
-	public EntityLoadQueryImpl(
-	public String generateSql(
-public class ResultSetProcessorImpl implements ResultSetProcessor {
-	public ResultSetProcessorImpl(LoadPlan loadPlan) {
-	public ScrollableResultSetProcessor toOnDemandForm() {
-	public List extractResults(
-		public void startingEntityFetch(EntityFetch entityFetch) {
-		public void startingCollectionFetch(CollectionFetch collectionFetch) {
-public class LoadQueryAliasResolutionContextImpl implements LoadQueryAliasResolutionContext {
+public class AliasResolutionContextImpl implements AliasResolutionContext {
+	public AliasResolutionContextImpl(SessionFactoryImplementor sessionFactory) {
+	public AliasResolutionContextImpl(SessionFactoryImplementor sessionFactory, int suffixSeed) {
-	public LoadQueryAliasResolutionContextImpl(
+	public AliasResolutionContextImpl(
-	public String resolveEntityReturnAlias(EntityReturn entityReturn) {
+	public String getSourceAlias(Return theReturn) {
-	public String resolveCollectionReturnAlias(CollectionReturn collectionReturn) {
+	public String[] resolveScalarColumnAliases(ScalarReturn scalarReturn) {
-	public String[] resolveScalarReturnAliases(ScalarReturn scalarReturn) {
+	public EntityReferenceAliases resolveAliases(EntityReference entityReference) {
-	public String resolveEntityTableAlias(EntityReference entityReference) {
+	public CollectionReferenceAliases resolveAliases(CollectionReference collectionReference) {
-	public EntityAliases resolveEntityColumnAliases(EntityReference entityReference) {
-	public String resolveCollectionTableAlias(CollectionReference collectionReference) {
-	public CollectionAliases resolveCollectionColumnAliases(CollectionReference collectionReference) {
-	public EntityAliases resolveCollectionElementColumnAliases(CollectionReference collectionReference) {
-		public LoadQueryEntityAliasesImpl(String tableAlias, EntityAliases columnAliases) {
+		public String getCollectionTableAlias() {
+		public String getElementTableAlias() {
+		public CollectionAliases getCollectionColumnAliases() {
+		public EntityAliases getEntityElementColumnAliases() {
+	public EntityReferenceAliasesImpl(String tableAlias, EntityAliases columnAliases) {
+	public String getTableAlias() {
+	public EntityAliases getColumnAliases() {
+public class CollectionReferenceReader {
+	public CollectionReferenceReader(CollectionReference collectionReference) {
+	public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
+public class CollectionReturnReader extends CollectionReferenceReader implements ReturnReader {
+	public CollectionReturnReader(CollectionReturn collectionReturn) {
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+public interface EntityIdentifierReader {
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+public class EntityReferenceReader {
+	public EntityReferenceReader(EntityReference entityReference) {
+	public EntityReference getEntityReference() {
+	public void hydrateIdentifier(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+	public void resolveEntityKey(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+	public void hydrateEntityState(ResultSet resultSet, ResultSetProcessingContext context) {
+	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) {
+	public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
+public class EntityReturnReader extends EntityReferenceReader implements ReturnReader {
+	public EntityReturnReader(EntityReturn entityReturn) {
+//	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+public class Helper {
+	public static final Helper INSTANCE = new Helper();
+	public EntityReference findOwnerEntityReference(FetchOwner owner) {
+public class OneToOneFetchReader extends EntityReferenceReader {
+	public OneToOneFetchReader(EntityFetch entityFetch, EntityReference ownerEntityReference) {
+		public OneToOneFetchIdentifierReader(EntityFetch oneToOne, EntityReference ownerEntityReference) {
+		public LockMode resolveLockMode(EntityReference entityReference) {
+	public boolean shouldUseOptionalEntityInformation() {
-	public EntityKey getDictatedRootEntityKey() {
+	public boolean shouldReturnProxies() {
+	public LoadPlan getLoadPlan() {
+	public LockMode resolveLockMode(EntityReference entityReference) {
-	public IdentifierResolutionContext getIdentifierResolutionContext(final EntityReference entityReference) {
+	public EntityReferenceProcessingState getProcessingState(final EntityReference entityReference) {
-				public void registerHydratedForm(Object hydratedForm) {
+				public void registerMissingIdentifier() {
-				public Object getHydratedForm() {
+				public boolean isMissingIdentifier() {
+				public void registerIdentifierHydratedForm(Object identifierHydratedForm) {
+				public Object getIdentifierHydratedForm() {
+				public void registerHydratedState(Object[] hydratedState) {
+				public Object[] getHydratedState() {
+				public void registerEntityInstance(Object entityInstance) {
+				public Object getEntityInstance() {
-	public Set<IdentifierResolutionContext> getIdentifierResolutionContexts() {
+	public EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch) {
-	public LoadQueryAliasResolutionContext getLoadQueryAliasResolutionContext() {
+	public AliasResolutionContext getAliasResolutionContext() {
-	public void registerHydratedEntity(EntityPersister persister, EntityKey entityKey, Object entityInstance) {
+	public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance) {
+	public static final ResultSetProcessorHelper INSTANCE = new ResultSetProcessorHelper();
+	public EntityKey interpretEntityKey(
+public class ResultSetProcessorImpl implements ResultSetProcessor {
+	public ResultSetProcessorImpl(
+	public ScrollableResultSetProcessor toOnDemandForm() {
+	public List extractResults(
+		public void startingEntityFetch(EntityFetch entityFetch) {
+		public void startingCollectionFetch(CollectionFetch collectionFetch) {
+		public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
+		public MixedReturnRowReader(LoadPlan loadPlan) {
+						public void startingEntityFetch(EntityFetch entityFetch) {
+						public void startingCollectionFetch(CollectionFetch collectionFetch) {
+		public void add(EntityReferenceReader reader);
+		public void add(CollectionReferenceReader reader);
+		public EntityLoaderRowReader(LoadPlan loadPlan) {
+//				public void add(EntityReferenceReader reader) {
+//				public void add(CollectionReferenceReader reader) {
+						public void startingEntityFetch(EntityFetch entityFetch) {
+						public void startingCollectionFetch(CollectionFetch collectionFetch) {
+		public CollectionInitializerRowReader(LoadPlan loadPlan) {
+						public void startingEntityFetch(EntityFetch entityFetch) {
+						public void startingCollectionFetch(CollectionFetch collectionFetch) {
+public class ScalarReturnReader implements ReturnReader {
+	public ScalarReturnReader(ScalarReturn scalarReturn) {
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+public interface ResultSetProcessingContext extends LockModeResolver {
+	public SessionImplementor getSession();
+	public QueryParameters getQueryParameters();
+	public boolean shouldUseOptionalEntityInformation();
+	public boolean shouldReturnProxies();
+	public LoadPlan getLoadPlan();
+	public static interface EntityReferenceProcessingState {
+		public EntityReference getEntityReference();
+		public void registerMissingIdentifier();
+		public boolean isMissingIdentifier();
+		public void registerIdentifierHydratedForm(Object hydratedForm);
+		public Object getIdentifierHydratedForm();
+		public void registerEntityKey(EntityKey entityKey);
+		public EntityKey getEntityKey();
+		public void registerHydratedState(Object[] hydratedState);
+		public Object[] getHydratedState();
+		public void registerEntityInstance(Object instance);
+		public Object getEntityInstance();
+	public EntityReferenceProcessingState getProcessingState(EntityReference entityReference);
+	public EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch);
+	public AliasResolutionContext getAliasResolutionContext();
+	public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance);
+	public static interface EntityKeyResolutionContext {
+		public EntityPersister getEntityPersister();
+		public LockMode getLockMode();
+		public EntityReference getEntityReference();
+	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext);
+	public void checkVersion(
+	public String getConcreteEntityTypeName(
+	public void loadFromResultSet(
+public interface ReturnReader {
+	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+public class EntityLoadQueryBuilderImpl implements EntityLoadQueryBuilder {
+	public static final EntityLoadQueryBuilderImpl INSTANCE = new EntityLoadQueryBuilderImpl();
+	public String generateSql(
+	public String generateSql(
+public class LoadQueryBuilderHelper {
+	public static void applyJoinFetches(
+public class SelectStatementBuilder {
+	public final Dialect dialect;
+	public SelectStatementBuilder(Dialect dialect) {
+	public void appendSelectClauseFragment(String selection) {
+	public void appendSelectClauseFragment(SelectFragment selectFragment) {
+	public void appendFromClauseFragment(String fragment) {
+	public void appendFromClauseFragment(String tableName, String alias) {
+	public void appendRestrictions(String restrictions) {
+//	public void appendOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
+	public void setOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
+	public void appendOrderByFragment(String ordering) {
+	public void setComment(String comment) {
+	public void setLockMode(LockMode lockMode) {
+	public void setLockOptions(LockOptions lockOptions) {
+	public String toStatementString() {
+public interface EntityLoadQueryBuilder {
+public interface QueryBuildingParameters {
+	public LoadQueryInfluencers getQueryInfluencers();
+	public int getBatchSize();
+	public LockMode getLockMode();
+	public LockOptions getLockOptions();
-public interface LoadQueryAliasResolutionContext {
+public interface AliasResolutionContext {
-	public String resolveEntityReturnAlias(EntityReturn entityReturn);
+	public String getSourceAlias(Return theReturn);
-	public String resolveCollectionReturnAlias(CollectionReturn collectionReturn);
+	public String[] resolveScalarColumnAliases(ScalarReturn scalarReturn);
+	public EntityReferenceAliases resolveAliases(EntityReference entityReference);
+	public CollectionReferenceAliases resolveAliases(CollectionReference collectionReference);
+public interface CollectionReferenceAliases {
+	public String getCollectionTableAlias();
+	public String getElementTableAlias();
+	public CollectionAliases getCollectionColumnAliases();
+	public EntityAliases getEntityElementColumnAliases();
+public interface EntityReferenceAliases {
+	public String getTableAlias();
+	public EntityAliases getColumnAliases();
+public class LoadQueryDetails {
+	public static LoadQueryDetails makeForBatching(
+	public SessionFactoryImplementor getFactory() {
+	public LoadPlan getLoadPlan() {
+	public AliasResolutionContext getAliasResolutionContext() {
+	public String getSqlStatement() {
+	public ResultSetProcessor getResultSetProcessor() {
+public interface LockModeResolver {
+	public LockMode resolveLockMode(EntityReference entityReference);
+	public static AnyFetch buildAnyFetch(
-	public LoadPlanImpl(boolean hasScalars, List<Return> returns) {
-	public LoadPlanImpl(boolean hasScalars, Return rootReturn) {
+	public LoadPlanImpl(EntityReturn rootReturn) {
-	public LoadPlanImpl(EntityReturn entityReturn) {
+	public LoadPlanImpl(CollectionReturn rootReturn) {
-	public boolean hasAnyScalarReturns() {
+	public LoadPlanImpl(List<? extends Return> returns, boolean areLazyAttributesForceFetched) {
-	public List<Return> getReturns() {
+	public List<? extends Return> getReturns() {
+	public Disposition getDisposition() {
+	public boolean areLazyAttributesForceFetched() {
+	public boolean hasAnyScalarReturns() {
-	public boolean hasEntityElements() {
+		public EntityReference getTargetEntityReference() {
+	public AnyFetch buildAnyFetch(
+	public AttributeDefinition getFetchedAttribute() {
-	public String getOwnerPropertyName() {
+	public Type getFetchedType() {
-	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	public String getAdditionalJoinConditions() {
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+public class AnyFetch extends AbstractPlanNode implements Fetch {
+	public AnyFetch(
+	public FetchOwner getOwner() {
+	public AnyType getFetchedType() {
+	public boolean isNullable() {
+	public String[] toSqlSelectFragments(String alias) {
+	public String getAdditionalJoinConditions() {
+	public FetchStrategy getFetchStrategy() {
+	public PropertyPath getPropertyPath() {
+	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
+	public AnyFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
+public interface BidirectionalEntityFetch {
+	public EntityReference getTargetEntityReference();
-	public String getOwnerPropertyName() {
+	public CollectionType getFetchedType() {
+	public String getAdditionalJoinConditions() {
+	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
-	public boolean hasEntityElements();
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+public class CompositeBasedSqlSelectFragmentResolver implements SqlSelectFragmentResolver {
+		public String[] toSqlSelectFragments(String alias);
+	public CompositeBasedSqlSelectFragmentResolver(
+	public String[] toSqlSelectFragments(String alias, AttributeDefinition attributeDefinition) {
-	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
+	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
+	public CollectionFetch buildCollectionFetch(
-public class CompositeFetchOwnerDelegate extends AbstractFetchOwnerDelegate implements FetchOwnerDelegate {
-	public CompositeFetchOwnerDelegate(
-	public static interface PropertyMappingDelegate {
-		public String[] toSqlSelectFragments(String alias);
-		public FetchMetadataImpl(
-		public boolean isNullable() {
-		public Type getType() {
-		public String[] toSqlSelectFragments(String alias) {
-	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
-	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
+	public EntityFetch buildEntityFetch(
+		public BidirectionalEntityElementGraphFetch(
+		public EntityReference getTargetEntityReference() {
-	public final EntityType getEntityType() {
+	public EntityType getFetchedType() {
+	public String[] toSqlSelectFragments(String alias) {
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
+	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
-public class EntityFetchOwnerDelegate extends AbstractFetchOwnerDelegate implements FetchOwnerDelegate {
-	public EntityFetchOwnerDelegate(EntityPersister entityPersister) {
-		public NonIdentifierAttributeFetchMetadata(
-		public boolean isNullable() {
-		public Type getType() {
-		public String[] toSqlSelectFragments(String alias) {
-		public NonEncapsulatedIdentifierAttributeFetchMetadata(
-		public boolean isNullable() {
-		public Type getType() {
-		public String[] toSqlSelectFragments(String alias) {
-	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
-public abstract class AbstractFetchOwnerDelegate implements FetchOwnerDelegate {
+public class EntityPersisterBasedSqlSelectFragmentResolver implements SqlSelectFragmentResolver {
+	public EntityPersisterBasedSqlSelectFragmentResolver(Queryable entityPersister) {
-	public FetchMetadata locateFetchMetadata(Fetch fetch) {
+	public String[] toSqlSelectFragments(String alias, AttributeDefinition attributeDefinition) {
-	public void validateFetchPlan(FetchStrategy fetchStrategy) {
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
+	public PropertyPath getPropertyPath();
+	public Type getFetchedType();
-	public String getOwnerPropertyName();
+	public FetchStrategy getFetchStrategy();
+	public String getAdditionalJoinConditions();
-	public FetchStrategy getFetchStrategy();
-	public PropertyPath getPropertyPath();
+	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException;
-	public void validateFetchPlan(FetchStrategy fetchStrategy);
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition);
+	public AnyFetch buildAnyFetch(
+	public SqlSelectFragmentResolver toSqlSelectFragmentResolver();
-public interface FetchOwnerDelegate {
-	public static interface FetchMetadata {
-		public boolean isNullable();
-		public Type getType();
-		public String[] toSqlSelectFragments(String alias);
-	public FetchMetadata locateFetchMetadata(Fetch fetch);
+public class KeyManyToOneBidirectionalEntityFetch extends EntityFetch implements BidirectionalEntityFetch {
+	public KeyManyToOneBidirectionalEntityFetch(
+	public KeyManyToOneBidirectionalEntityFetch(
+	public EntityReference getTargetEntityReference() {
+	public List<? extends Return> getReturns();
+	public Disposition getDisposition();
+	public boolean areLazyAttributesForceFetched();
-	public List<Return> getReturns();
+	public static enum Disposition {
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
-	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
-	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) {
-	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) {
-	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-public interface LoadQueryBuilder {
+public class SourceQualifiable {
+public interface SqlSelectFragmentResolver {
+	public String[] toSqlSelectFragments(String alias, AttributeDefinition attributeDefinition);
+	public void foundAny(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition) {
+		public AnyFetch buildAnyFetch(
+		public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
-		public void validateFetchPlan(FetchStrategy fetchStrategy) {
+		public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
-						public boolean isNullable() {
-						public Type getType() {
-						public String[] toSqlSelectFragments(String alias) {
-						public boolean isNullable() {
-						public Type getType() {
-						public String[] toSqlSelectFragments(String alias) {
+		public HydratedCompoundValueHandler getHydratedStateHandler(Fetch fetch) {
+	public void startingAnyFetch(AnyFetch fetch) {
+	public void finishingAnyFetch(AnyFetch fetch) {
+	public void startingAnyFetch(AnyFetch fetch) {
+	public void finishingAnyFetch(AnyFetch fetch) {
+	public void startingAnyFetch(AnyFetch fetch);
+	public void finishingAnyFetch(AnyFetch fetch);
+	public void startingAnyFetch(AnyFetch fetch) {
+	public void finishingAnyFetch(AnyFetch fetch) {
-	public void visit(List<Return> rootReturns) {
+	public void visit(List<? extends Return> rootReturns) {
-public interface ResultSetProcessingContext {
-	public SessionImplementor getSession();
-	public QueryParameters getQueryParameters();
-	public EntityKey getDictatedRootEntityKey();
-	public static interface IdentifierResolutionContext {
-		public EntityReference getEntityReference();
-		public void registerHydratedForm(Object hydratedForm);
-		public Object getHydratedForm();
-		public void registerEntityKey(EntityKey entityKey);
-		public EntityKey getEntityKey();
-	public IdentifierResolutionContext getIdentifierResolutionContext(EntityReference entityReference);
-	public Set<IdentifierResolutionContext> getIdentifierResolutionContexts();
-	public LoadQueryAliasResolutionContext getLoadQueryAliasResolutionContext();
-	public void registerHydratedEntity(EntityPersister persister, EntityKey entityKey, Object entityInstance);
-	public static interface EntityKeyResolutionContext {
-		public EntityPersister getEntityPersister();
-		public LockMode getLockMode();
-		public EntityReference getEntityReference();
-	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext);
-	public void checkVersion(
-	public String getConcreteEntityTypeName(
-	public void loadFromResultSet(
+			public AnyMappingDefinition toAnyMappingDefinition() {
+					public boolean isNullable() {
-	public void postInstantiate() throws MappingException {
+	public final void postInstantiate() throws MappingException {
-			public Iterator<AttributeDefinition> iterator() {
-					public boolean hasNext() {
-					public AttributeDefinition next() {
-					public void remove() {
+//			public Iterator<AttributeDefinition> iterator() {
+//					public boolean hasNext() {
+//					public AttributeDefinition next() {
+//					public void remove() {
-	public void postInstantiate() {
-								public boolean isCollection() {
+								public AssociationNature getAssociationNature() {
+								public AnyMappingDefinition toAnyDefinition() {
-								public Type getType() {
+								public AssociationType getType() {
+								public boolean isNullable() {
+								public boolean isNullable() {
+								public boolean isNullable() {
+		public boolean isNullable() {
+	public static boolean isJoinFetched(FetchStrategy fetchStrategy) {
+public class StandardAnyTypeDefinition implements AnyMappingDefinition {
+	public StandardAnyTypeDefinition(AnyType anyType, boolean definedAsLazy) {
+						public Object getDiscriminatorValue() {
+						public String getEntityName() {
+	public AnyType getType() {
+	public boolean isLazy() {
+	public Type getIdentifierType() {
+	public Type getDiscriminatorType() {
+	public Iterable<DiscriminatorMapping> getMappingDefinedDiscriminatorMappings() {
+public interface AnyMappingDefinition {
+	public AnyType getType();
+	public boolean isLazy();
+	public Type getIdentifierType();
+	public Type getDiscriminatorType();
+	public Iterable<DiscriminatorMapping> getMappingDefinedDiscriminatorMappings();
+	public static interface DiscriminatorMapping {
+		public Object getDiscriminatorValue();
+		public String getEntityName();
-	public boolean isCollection();
+	public static enum AssociationNature {
+	public AssociationNature getAssociationNature();
+	public AnyMappingDefinition toAnyDefinition();
+	public void foundAny(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition);
+	public AttributeSource getSource();
-	public AttributeSource getSource();
+	public boolean isNullable();
+	public AnyMappingDefinition toAnyMappingDefinition();
-	public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType, String on) {
+	public void addJoin(
-	public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType, String on) {
+	public void addJoin(String rhsTableName, String rhsAlias, String[] lhsColumns, String[] rhsColumns, JoinType joinType, String on) {
+	public DisjunctionFragment addCondition(String fragment) {
-	public boolean isCollection() {
+	public AssociationNature getAssociationNature() {
+	public AnyMappingDefinition toAnyDefinition() {
-	public boolean isCollection() {
+	public AssociationNature getAssociationNature() {
+	public AnyMappingDefinition toAnyDefinition() {
-	public AnyType(Type metaType, Type identifierType) {
+	public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType) {
-	public Object deepCopy(Object value, SessionFactoryImplementor factory)
+	public Type getIdentifierType() {
-	public boolean isMethodOf(Method method) {
+	public Type getDiscriminatorType() {
-	public boolean isSame(Object x, Object y) throws HibernateException {
+	public String getName() {
-	public int compare(Object x, Object y) {
+	public Class getReturnedClass() {
-	public int getColumnSpan(Mapping session)
+	public int[] sqlTypes(Mapping mapping) throws MappingException {
-	public String getName() {
+	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
-	public boolean isMutable() {
+	public Size[] defaultSizes(Mapping mapping) throws MappingException {
-	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner)
+	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
+	public boolean isAnyType() {
-	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
+	public boolean isAssociationType() {
-	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
+	public boolean isComponentType() {
-	public Object resolve(Object value, SessionImplementor session, Object owner)
+	public boolean isEmbedded() {
-	public Object semiResolve(Object value, SessionImplementor session, Object owner)
+	public boolean isMutable() {
+	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
-	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
+	public int compare(Object x, Object y) {
-	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
+	public boolean isSame(Object x, Object y) throws HibernateException {
+	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
-	public Class getReturnedClass() {
+	public boolean[] toColumnNullness(Object value, Mapping mapping) {
-	public int[] sqlTypes(Mapping mapping) throws MappingException {
+	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
-	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
+	public int getColumnSpan(Mapping session) {
-	public Size[] defaultSizes(Mapping mapping) throws MappingException {
+	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
-	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
+	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
-	public String toLoggableString(Object value, SessionFactoryImplementor factory) 
+	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-	public static final class ObjectTypeCacheEntry implements Serializable {
+	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
-	public Object assemble(
+	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
-	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
+	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
-	public boolean isAnyType() {
+	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
-	public Object replace(
+	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
-	public CascadeStyle getCascadeStyle(int i) {
+	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
-	public FetchMode getFetchMode(int i) {
+	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner) {
+	public Object semiResolve(Object value, SessionImplementor session, Object owner) {
+	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
+	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
+	public boolean isMethodOf(Method method) {
-	public Object getPropertyValue(Object component, int i, SessionImplementor session)
+	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
-	public Object[] getPropertyValues(Object component, SessionImplementor session)
+	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
-	public Type[] getSubtypes() {
+	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
-	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
+	public boolean[] getPropertyNullability() {
+	public Type[] getSubtypes() {
-	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
+	public CascadeStyle getCascadeStyle(int i) {
-	public boolean isComponentType() {
+	public FetchMode getFetchMode(int i) {
-	public boolean isAssociationType() {
-	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
-	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
-	public String getAssociatedEntityName(SessionFactoryImplementor factory)
-	public boolean[] getPropertyNullability() {
+	public String getLHSPropertyName() {
-	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
-	public String getRHSUniqueKeyPropertyName() {
-	public String getLHSPropertyName() {
+	public String getRHSUniqueKeyPropertyName() {
-	public boolean[] toColumnNullness(Object value, Mapping mapping) {
+	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
-	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) 
+	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
-	public boolean isEmbedded() {
+	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
+	public static final class ObjectTypeCacheEntry implements Serializable {
-	public MetaType(Map values, Type baseType) {
+	public MetaType(Map<Object,String> discriminatorValuesToEntityNameMap, Type baseType) {
+	public Map<Object, String> getDiscriminatorValuesToEntityNameMap() {
-	public ObjectType() {
+	public void testNestedCompositeElementCollectionQueryBuilding() {
+					public LoadQueryInfluencers getQueryInfluencers() {
+					public int getBatchSize() {
+					public LockMode getLockMode() {
+					public LockOptions getLockOptions() {
-						public void execute(Connection connection) throws SQLException {
-												public int[] getNamedParameterLocations(String name) {
+					public void execute(Connection connection) throws SQLException {
+											public int[] getNamedParameterLocations(String name) {
+		public boolean equals(Object o) {
+		public int hashCode() {
+public class Helper implements QueryBuildingParameters {
+	public static final Helper INSTANCE = new Helper();
+	public LoadPlan buildLoadPlan(SessionFactoryImplementor sf, EntityPersister entityPersister) {
+	public String generateSql(SessionFactoryImplementor sf, LoadPlan plan, AliasResolutionContext aliasResolutionContext) {
+	public LoadQueryInfluencers getQueryInfluencers() {
+	public int getBatchSize() {
+	public LockMode getLockMode() {
+	public LockOptions getLockOptions() {
+public class LoadPlanStructureAssertionHelper {
+	public static final LoadPlanStructureAssertionHelper INSTANCE = new LoadPlanStructureAssertionHelper();
+	public void performBasicComparison(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
+					public LoadQueryInfluencers getQueryInfluencers() {
+					public int getBatchSize() {
+					public LockMode getLockMode() {
+					public LockOptions getLockOptions() {
+public class LoadPlanStructureAssertionTest extends BaseUnitTestCase {
+	public void testJoinedOneToOne() {
+	public void testSpecialOneToOne() {
+	public void testEncapsulatedCompositeId() {
+	public void testManyToMany() {
+					public void foundAny(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition) {
+	public void testJoinFetchOfAnAnyTypeAttribute() {
+	public void loadFoo() {
+public class TestPathHelper {
+	public static URL resolveRootUrl(Class knownClass) {
+	public static File resolveRootDirectory(Class knownClass) {
+	public static File resolveClassFile(Class knownClass) {

Lines added containing method: 415. Lines removed containing method: 210. Tot = 625
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
Method found in diff:	public Type getType(Fetch fetch) {
-		return getFetchOwnerDelegate().locateFetchMetadata( fetch ).getType();
+		return fetch.getFetchedType();

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public int getColumnSpan(Mapping session)
-	public int getColumnSpan(Mapping session)

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public Dialect getDialect();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Class getPersistentClass() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-8637  9938937f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
+	public void applyTreatAsDeclarations(Set<String> treatAsDeclarations) {
-	public JoinFragment toJoinFragment(Map enabledFilters, boolean includeExtraJoins) throws MappingException {
+	public JoinFragment toJoinFragment(Map enabledFilters, boolean includeAllSubclassJoins) throws MappingException {
+	public Map<String, Set<String>> getTreatMap() {
+	public Set<String> getTreatAsDeclarationsByPath(String path) {
+	public void applyTreatAsDeclarations(Set<String> treatAsDeclarationsByPath) {
+	public void applyTreatAsDeclarations(Set<String> treatAsDeclarations) {
+	public static String[] reverse(String[] source) {
-	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
+	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
+	public String filterFragment(
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
+	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
+	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
-	public String fromJoinFragment(String alias,
+	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
+	public String fromJoinFragment(
-	public String whereJoinFragment(String alias,
+	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
+	public String whereJoinFragment(
+	public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) {
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
+	public String fromJoinFragment(
+	public String whereJoinFragment(
+	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations);
+	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations);
+	public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) throws MappingException;
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations);
+	public String filterFragment(String alias, Set<String> treatAsDeclarations) {
+	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
+	public String filterFragment(String alias, Set<String> treatAsDeclarations) {
+	public InFragment addValues(Object[] values) {
+	public String getOnCondition(
+	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters, Set<String> treatAsDeclarations);
+	public String getOnCondition(
-	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
+	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
+	public String getOnCondition(
-public class TreatKeywordTest extends AbstractJPATest {
+public class TreatKeywordTest extends BaseCoreFunctionalTestCase {
-	public void testUsageInSelect() {
+	public void testBasicUsageInJoin() {
+	public void testFilteringDiscriminatorSubclasses() {
+	public void testFilteringJoinedSubclasses() {
+	public static class JoinedEntity {
+		public Integer id;
+		public String name;
+		public JoinedEntity other;
+		public JoinedEntity() {
+		public JoinedEntity(Integer id, String name) {
+		public JoinedEntity(Integer id, String name, JoinedEntity other) {
+	public static class JoinedEntitySubclass extends JoinedEntity {
+		public JoinedEntitySubclass() {
+		public JoinedEntitySubclass(Integer id, String name) {
+		public JoinedEntitySubclass(Integer id, String name, JoinedEntity other) {
+	public static class JoinedEntitySubSubclass extends JoinedEntitySubclass {
+		public JoinedEntitySubSubclass() {
+		public JoinedEntitySubSubclass(Integer id, String name) {
+		public JoinedEntitySubSubclass(Integer id, String name, JoinedEntity other) {
+	public static class JoinedEntitySubclass2 extends JoinedEntity {
+		public JoinedEntitySubclass2() {
+		public JoinedEntitySubclass2(Integer id, String name) {
+		public JoinedEntitySubclass2(Integer id, String name, JoinedEntity other) {
+	public static class JoinedEntitySubSubclass2 extends JoinedEntitySubclass2 {
+		public JoinedEntitySubSubclass2() {
+		public JoinedEntitySubSubclass2(Integer id, String name) {
+		public JoinedEntitySubSubclass2(Integer id, String name, JoinedEntity other) {
+	public static class DiscriminatorEntity {
+		public Integer id;
+		public String name;
+		public DiscriminatorEntity other;
+		public DiscriminatorEntity() {
+		public DiscriminatorEntity(Integer id, String name) {
+		public DiscriminatorEntity(
+	public static class DiscriminatorEntitySubclass extends DiscriminatorEntity {
+		public DiscriminatorEntitySubclass() {
+		public DiscriminatorEntitySubclass(Integer id, String name) {
+		public DiscriminatorEntitySubclass(
+	public static class DiscriminatorEntitySubSubclass extends DiscriminatorEntitySubclass {
+		public DiscriminatorEntitySubSubclass() {
+		public DiscriminatorEntitySubSubclass(Integer id, String name) {
+		public DiscriminatorEntitySubSubclass(

Lines added containing method: 75. Lines removed containing method: 7. Tot = 82
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
Method found in diff:	public int getColumnSpan(Mapping session) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected Dialect getDialect() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isCascadeDeleteEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getAlias() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final String getEntityName() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInverse() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
49/Between/ HHH-9466  66ce8b7f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
public 
-	public static CacheDataDescriptionImpl decode(EntityBinding model) {
-	public static CacheDataDescriptionImpl decode(PluralAttributeBinding model) {
-	public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory,
-	public void integrate(
-	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey) {
-	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey) {
-	public String getColumnDefinitionUniquenessFragment(Column column) {
-	public String getTableCreationUniqueConstraintsFragment(Table table) {
-	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey) {
-	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey) {
-	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey) {
-	public String getColumnDefinitionUniquenessFragment(Column column);
-	public String getTableCreationUniqueConstraintsFragment(Table table);
-	public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey);
-	public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey);
-	public NativeQueryInterpreter initiateService(
-	public CacheImplementor initiateService(SessionFactoryImplementor sessionFactory, MetadataImplementor metadata, ServiceRegistryImplementor registry) {
-	public EventListenerRegistry initiateService(
-    public void integrate( MetadataImplementor metadata,
-	public SessionFactoryImpl(
-			public void sessionFactoryCreated(SessionFactory factory) {
-			public void sessionFactoryClosed(SessionFactory factory) {
-public interface Metadata {
-	public static interface Options {
-		public StandardServiceRegistry getServiceRegistry();
-		public MetadataSourceProcessingOrder getMetadataSourceProcessingOrder();
-		public NamingStrategy getNamingStrategy();
-		public SharedCacheMode getSharedCacheMode();
-		public AccessType getDefaultAccessType();
-		public boolean useNewIdentifierGenerators();
-        public boolean isGloballyQuotedIdentifiers();
-		public String getDefaultSchemaName();
-		public String getDefaultCatalogName();
-	public Options getOptions();
-	public SessionFactoryBuilder getSessionFactoryBuilder();
-	public SessionFactory buildSessionFactory();
-	public Iterable<EntityBinding> getEntityBindings();
-	public EntityBinding getEntityBinding(String entityName);
-	public EntityBinding getRootEntityBinding(String entityName);
-	public Iterable<PluralAttributeBinding> getCollectionBindings();
-	public TypeDef getTypeDefinition(String name);
-	public Iterable<TypeDef> getTypeDefinitions();
-	public Iterable<FilterDefinition> getFilterDefinitions();
-	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions();
-	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions();
-	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions();
-	public Iterable<Map.Entry<String, String>> getImports();
-	public Iterable<FetchProfile> getFetchProfiles();
-	public IdGenerator getIdGenerator(String name);
-public interface MetadataBuilder {
-	public MetadataBuilder with(NamingStrategy namingStrategy);
-	public MetadataBuilder with(MetadataSourceProcessingOrder metadataSourceProcessingOrder);
-	public MetadataBuilder with(SharedCacheMode cacheMode);
-	public MetadataBuilder with(AccessType accessType);
-	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled);
-	public Metadata build();
-public enum MetadataSourceProcessingOrder {
-public class MetadataSources {
-	public MetadataSources(ServiceRegistry serviceRegistry) {
-	public MetadataSources(ServiceRegistry serviceRegistry, EntityResolver entityResolver, NamingStrategy namingStrategy) {
-	public List<JaxbRoot> getJaxbRootList() {
-	public Iterable<String> getAnnotatedPackages() {
-	public Iterable<Class<?>> getAnnotatedClasses() {
-	public ServiceRegistry getServiceRegistry() {
-	public NamingStrategy getNamingStrategy() {
-	public MetadataBuilder getMetadataBuilder() {
-	public MetadataBuilder getMetadataBuilder(StandardServiceRegistry serviceRegistry) {
-	public Metadata buildMetadata() {
-	public Metadata buildMetadata(StandardServiceRegistry serviceRegistry) {
-	public MetadataSources addAnnotatedClass(Class annotatedClass) {
-	public MetadataSources addPackage(String packageName) {
-	public MetadataSources addResource(String name) {
-	public MetadataSources addClass(Class entityClass) {
-	public MetadataSources addFile(String path) {
-	public MetadataSources addFile(File file) {
-	public MetadataSources addCacheableFile(String path) {
-	public MetadataSources addCacheableFile(File file) {
-	public MetadataSources addInputStream(InputStream xmlInputStream) {
-	public MetadataSources addURL(URL url) {
-	public MetadataSources addDocument(Document document) {
-	public MetadataSources addJar(File jar) {
-	public MetadataSources addDirectory(File dir) {
-public interface SessionFactoryBuilder {
-	public SessionFactoryBuilder with(Interceptor interceptor);
-	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate);
-	public SessionFactory build();
-public class ValidationException extends HibernateException {
-	public ValidationException(String s) {
-	public ValidationException(String string, Throwable root) {
-public abstract class AbstractAttributeBinding implements AttributeBinding {
-	public AttributeBindingContainer getContainer() {
-	public Attribute getAttribute() {
-	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
-	public boolean isBasicPropertyAccessor() {
-	public String getPropertyAccessorName() {
-	public void setPropertyAccessorName(String propertyAccessorName) {
-	public boolean isIncludedInOptimisticLocking() {
-	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
-	public MetaAttributeContext getMetaAttributeContext() {
-	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
-	public boolean isAlternateUniqueKey() {
-	public void setAlternateUniqueKey(boolean alternateUniqueKey) {
-	public boolean isLazy() {
-	public void setLazy(boolean isLazy) {
-	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding referencingAttributeBinding) {
-	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
-	public void validate() {
-public abstract class AbstractCollectionElement {
-	public abstract CollectionElementNature getCollectionElementNature();
-	public AbstractPluralAttributeBinding getCollectionBinding() {
-	public Value getElementValue() {
-public abstract class AbstractPluralAttributeBinding extends AbstractAttributeBinding implements PluralAttributeBinding {
-	public PluralAttribute getAttribute() {
-	public boolean isAssociation() {
-	public TableSpecification getCollectionTable() {
-	public void setCollectionTable(Table collectionTable) {
-	public CollectionKey getCollectionKey() {
-	public AbstractCollectionElement getCollectionElement() {
-	public CascadeStyle getCascadeStyle() {
-	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
-	public boolean isOrphanDelete() {
-	public FetchMode getFetchMode() {
-	public FetchTiming getFetchTiming() {
-	public void setFetchTiming(FetchTiming fetchTiming) {
-	public FetchStyle getFetchStyle() {
-	public void setFetchStyle(FetchStyle fetchStyle) {
-	public String getCustomLoaderName() {
-	public void setCustomLoaderName(String customLoaderName) {
-	public CustomSQL getCustomSqlInsert() {
-	public void setCustomSqlInsert(CustomSQL customSqlInsert) {
-	public CustomSQL getCustomSqlUpdate() {
-	public void setCustomSqlUpdate(CustomSQL customSqlUpdate) {
-	public CustomSQL getCustomSqlDelete() {
-	public void setCustomSqlDelete(CustomSQL customSqlDelete) {
-	public CustomSQL getCustomSqlDeleteAll() {
-	public void setCustomSqlDeleteAll(CustomSQL customSqlDeleteAll) {
-	public Class<? extends CollectionPersister> getCollectionPersisterClass() {
-	public void setCollectionPersisterClass(Class<? extends CollectionPersister> collectionPersisterClass) {
-	public Caching getCaching() {
-	public void setCaching(Caching caching) {
-	public String getOrderBy() {
-	public void setOrderBy(String orderBy) {
-	public String getWhere() {
-	public void setWhere(String where) {
-	public boolean isInverse() {
-	public void setInverse(boolean inverse) {
-	public boolean isMutable() {
-	public void setMutable(boolean mutable) {
-	public int getBatchSize() {
-	public void setBatchSize(int batchSize) {
-	public String getReferencedPropertyName() {
-	public boolean isSorted() {
-	public Comparator getComparator() {
-	public void setComparator(Comparator comparator) {
-	public String getComparatorClassName() {
-	public void addFilter(String name, String condition) {
-	public java.util.Map getFilterMap() {
-public abstract class AbstractSingularAttributeBinding
-	public SingularAttribute getAttribute() {
-	public Value getValue() {
-	public void setSimpleValueBindings(Iterable<SimpleValueBinding> simpleValueBindings) {
-	public int getSimpleValueSpan() {
-	public Iterable<SimpleValueBinding> getSimpleValueBindings() {
-	public boolean hasDerivedValue() {
-	public boolean isNullable() {
-public interface AssociationAttributeBinding extends AttributeBinding {
-	public CascadeStyle getCascadeStyle();
-	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles);
-	public FetchTiming getFetchTiming();
-	public void setFetchTiming(FetchTiming fetchTiming);
-	public FetchStyle getFetchStyle();
-	public void setFetchStyle(FetchStyle fetchStyle);
-	public FetchMode getFetchMode();
-public interface AttributeBinding {
-	public AttributeBindingContainer getContainer();
-	public Attribute getAttribute();
-	public HibernateTypeDescriptor getHibernateTypeDescriptor();
-	public boolean isAssociation();
-	public boolean isBasicPropertyAccessor();
-	public String getPropertyAccessorName();
-	public void setPropertyAccessorName(String propertyAccessorName);
-	public boolean isIncludedInOptimisticLocking();
-	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking);
-	public MetaAttributeContext getMetaAttributeContext();
-	public boolean isAlternateUniqueKey();
-	public boolean isLazy();
-	public void addEntityReferencingAttributeBinding(SingularAssociationAttributeBinding attributeBinding);
-	public Set<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings();
-	public void validate();
-public interface AttributeBindingContainer {
-	public String getPathBase();
-	public AttributeContainer getAttributeContainer();
-	public Iterable<AttributeBinding> attributeBindings();
-	public AttributeBinding locateAttributeBinding(String name);
-	public BasicAttributeBinding makeBasicAttributeBinding(SingularAttribute attribute);
-	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute);
-	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute);
-	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature);
-	public SetBinding makeSetAttributeBinding(PluralAttribute attribute, CollectionElementNature nature);
-	public EntityBinding seekEntityBinding();
-	public Class<?> getClassReference();
-	public MetaAttributeContext getMetaAttributeContext();
-public class BagBinding extends AbstractPluralAttributeBinding {
-public class BasicAttributeBinding
-	public boolean isAssociation() {
-	public String getUnsavedValue() {
-	public void setUnsavedValue(String unsavedValue) {
-	public PropertyGeneration getGeneration() {
-	public void setGeneration(PropertyGeneration generation) {
-	public boolean isIncludedInOptimisticLocking() {
-	public void setIncludedInOptimisticLocking(boolean includedInOptimisticLocking) {
-	public boolean isKeyCascadeDeleteEnabled() {
-	public void setKeyCascadeDeleteEnabled(boolean keyCascadeDeleteEnabled) {
-	public boolean forceNonNullable() {
-	public boolean forceUnique() {
-	public MetaAttributeContext getMetaAttributeContext() {
-	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
-public class BasicCollectionElement extends AbstractCollectionElement {
-	public BasicCollectionElement(AbstractPluralAttributeBinding binding) {
-	public CollectionElementNature getCollectionElementNature() {
-	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
-public class Caching {
-	public Caching() {
-	public Caching(String region, AccessType accessType, boolean cacheLazyProperties) {
-	public String getRegion() {
-	public void setRegion(String region) {
-	public AccessType getAccessType() {
-	public void setAccessType(AccessType accessType) {
-	public boolean isCacheLazyProperties() {
-	public void setCacheLazyProperties(boolean cacheLazyProperties) {
-	public String toString() {
-public enum CascadeType {
-	public static CascadeType getCascadeType(String hbmOptionName) {
-	public static CascadeType getCascadeType(javax.persistence.CascadeType jpaCascade) {
-	public CascadeStyle toCascadeStyle() {
-public enum CollectionElementNature {
-public class CollectionKey {
-	public CollectionKey(AbstractPluralAttributeBinding pluralAttributeBinding) {
-	public AbstractPluralAttributeBinding getPluralAttributeBinding() {
-	public void prepareForeignKey(String foreignKeyName, String targetTableName) {
-	public ForeignKey getForeignKey() {
-public enum CollectionLaziness {
-public class ComponentAttributeBinding extends AbstractSingularAttributeBinding implements AttributeBindingContainer {
-	public ComponentAttributeBinding(AttributeBindingContainer container, SingularAttribute attribute) {
-	public EntityBinding seekEntityBinding() {
-	public String getPathBase() {
-	public AttributeContainer getAttributeContainer() {
-	public Component getComponent() {
-	public boolean isAssociation() {
-	public MetaAttributeContext getMetaAttributeContext() {
-	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
-	public AttributeBinding locateAttributeBinding(String name) {
-	public Iterable<AttributeBinding> attributeBindings() {
-	public BasicAttributeBinding makeBasicAttributeBinding(SingularAttribute attribute) {
-	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
-	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
-	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
-	public SetBinding makeSetAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
-	public Class<?> getClassReference() {
-	public SingularAttribute getParentReference() {
-	public void setParentReference(SingularAttribute parentReference) {
-	public PropertyGeneration getGeneration() {
-public class CompositeCollectionElement extends AbstractCollectionElement {
-	public CompositeCollectionElement(AbstractPluralAttributeBinding binding) {
-	public CollectionElementNature getCollectionElementNature() {
-public class CustomSQL {
-	public CustomSQL(String sql, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
-	public String getSql() {
-	public boolean isCallable() {
-	public ExecuteUpdateResultCheckStyle getCheckStyle() {
-public class EntityBinding implements AttributeBindingContainer {
-	public EntityBinding(InheritanceType inheritanceType, EntityMode entityMode) {
-	public EntityBinding(EntityBinding superEntityBinding) {
-	public HierarchyDetails getHierarchyDetails() {
-	public EntityBinding getSuperEntityBinding() {
-	public boolean isRoot() {
-	public boolean isPolymorphic() {
-	public boolean hasSubEntityBindings() {
-	public int getSubEntityBindingClosureSpan() {
-	public Iterable<EntityBinding> getDirectSubEntityBindings() {
-	public Iterable<EntityBinding> getPostOrderSubEntityBindingClosure() {
-	public Iterable<EntityBinding> getPreOrderSubEntityBindingClosure() {
-	public Entity getEntity() {
-	public void setEntity(Entity entity) {
-	public TableSpecification getPrimaryTable() {
-	public void setPrimaryTable(TableSpecification primaryTable) {
-    public TableSpecification locateTable(String tableName) {
-    public String getPrimaryTableName() {
-    public void setPrimaryTableName(String primaryTableName) {
-	public void addSecondaryTable(String tableName, TableSpecification table) {
-	public boolean isVersioned() {
-	public boolean isDiscriminatorMatchValueNull() {
-	public boolean isDiscriminatorMatchValueNotNull() {
-	public String getDiscriminatorMatchValue() {
-	public void setDiscriminatorMatchValue(String discriminatorMatchValue) {
-	public Iterable<FilterDefinition> getFilterDefinitions() {
-	public void addFilterDefinition(FilterDefinition filterDefinition) {
-	public Iterable<SingularAssociationAttributeBinding> getEntityReferencingAttributeBindings() {
-	public EntityBinding seekEntityBinding() {
-	public String getPathBase() {
-	public Class<?> getClassReference() {
-	public AttributeContainer getAttributeContainer() {
-	public MetaAttributeContext getMetaAttributeContext() {
-	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
-	public boolean isMutable() {
-	public void setMutable(boolean mutable) {
-	public boolean isLazy() {
-	public void setLazy(boolean lazy) {
-	public ValueHolder<Class<?>> getProxyInterfaceType() {
-	public void setProxyInterfaceType(ValueHolder<Class<?>> proxyInterfaceType) {
-	public String getWhereFilter() {
-	public void setWhereFilter(String whereFilter) {
-	public String getRowId() {
-	public void setRowId(String rowId) {
-	public boolean isDynamicUpdate() {
-	public void setDynamicUpdate(boolean dynamicUpdate) {
-	public boolean isDynamicInsert() {
-	public void setDynamicInsert(boolean dynamicInsert) {
-	public int getBatchSize() {
-	public void setBatchSize(int batchSize) {
-	public boolean isSelectBeforeUpdate() {
-	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
-	public boolean hasSubselectLoadableCollections() {
-	public Class<? extends EntityPersister> getCustomEntityPersisterClass() {
-	public void setCustomEntityPersisterClass(Class<? extends EntityPersister> customEntityPersisterClass) {
-	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass() {
-	public void setCustomEntityTuplizerClass(Class<? extends EntityTuplizer> customEntityTuplizerClass) {
-	public Boolean isAbstract() {
-	public void setAbstract(Boolean isAbstract) {
-	public Set<String> getSynchronizedTableNames() {
-	public void addSynchronizedTableNames(java.util.Collection<String> synchronizedTableNames) {
-	public String getJpaEntityName() {
-	public void setJpaEntityName(String jpaEntityName) {
-	public String getCustomLoaderName() {
-	public void setCustomLoaderName(String customLoaderName) {
-	public CustomSQL getCustomInsert() {
-	public void setCustomInsert(CustomSQL customInsert) {
-	public CustomSQL getCustomUpdate() {
-	public void setCustomUpdate(CustomSQL customUpdate) {
-	public CustomSQL getCustomDelete() {
-	public void setCustomDelete(CustomSQL customDelete) {
-	public String toString() {
-	public BasicAttributeBinding makeBasicAttributeBinding(SingularAttribute attribute) {
-	public ComponentAttributeBinding makeComponentAttributeBinding(SingularAttribute attribute) {
-	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(SingularAttribute attribute) {
-	public BagBinding makeBagAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
-	public SetBinding makeSetAttributeBinding(PluralAttribute attribute, CollectionElementNature nature) {
-	public AttributeBinding locateAttributeBinding(String name) {
-	public Iterable<AttributeBinding> attributeBindings() {
-	public int getAttributeBindingClosureSpan() {
-	public Iterable<AttributeBinding> getAttributeBindingClosure() {
-	public Iterable<AttributeBinding> getSubEntityAttributeBindingClosure() {
-	public void setJpaCallbackClasses( List<JpaCallbackClass> jpaCallbackClasses ) {
-    public Iterable<JpaCallbackClass> getJpaCallbackClasses() {
-public class EntityDiscriminator {
-	public EntityDiscriminator() {
-	public SimpleValue getBoundValue() {
-	public void setBoundValue(SimpleValue boundValue) {
-	public HibernateTypeDescriptor getExplicitHibernateTypeDescriptor() {
-	public boolean isForced() {
-	public void setForced(boolean forced) {
-	public boolean isInserted() {
-	public void setInserted(boolean inserted) {
-	public String toString() {
-public class EntityIdentifier {
-	public EntityIdentifier(EntityBinding entityBinding) {
-	public BasicAttributeBinding getValueBinding() {
-	public void setValueBinding(BasicAttributeBinding attributeBinding) {
-	public void setIdGenerator(IdGenerator idGenerator) {
-	public boolean isEmbedded() {
-	public boolean isIdentifierMapper() {
-	public IdentifierGenerator createIdentifierGenerator(IdentifierGeneratorFactory factory, Properties properties) {
-	public IdentifierGenerator getIdentifierGenerator() {
-public class FetchProfile {
-    public FetchProfile( String name,
-    public String getName() {
-    public Set<Fetch> getFetches() {
-    public void addFetch( String entity,
-    public static class Fetch {
-        public Fetch( String entity,
-        public String getEntity() {
-        public String getAssociation() {
-        public String getStyle() {
-public class Helper {
-	public static void checkPluralAttributeNature(PluralAttribute attribute, PluralAttributeNature expected) {
-public class HibernateTypeDescriptor {
-	public String getExplicitTypeName() {
-	public void setExplicitTypeName(String explicitTypeName) {
-	public String getJavaTypeName() {
-	public void setJavaTypeName(String javaTypeName) {
-	public boolean isToOne() {
-	public void setToOne(boolean toOne) {
-	public Map<String, String> getTypeParameters() {
-	public void setTypeParameters(Map<String, String> typeParameters) {
-	public Type getResolvedTypeMapping() {
-	public void setResolvedTypeMapping(Type resolvedTypeMapping) {
-public class HierarchyDetails {
-	public HierarchyDetails(EntityBinding rootEntityBinding, InheritanceType inheritanceType, EntityMode entityMode) {
-	public EntityBinding getRootEntityBinding() {
-	public InheritanceType getInheritanceType() {
-	public EntityMode getEntityMode() {
-	public EntityIdentifier getEntityIdentifier() {
-	public EntityDiscriminator getEntityDiscriminator() {
-	public OptimisticLockStyle getOptimisticLockStyle() {
-	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
-	public void setEntityDiscriminator(EntityDiscriminator entityDiscriminator) {
-	public BasicAttributeBinding getVersioningAttributeBinding() {
-	public void setVersioningAttributeBinding(BasicAttributeBinding versioningAttributeBinding) {
-	public Caching getCaching() {
-	public void setCaching(Caching caching) {
-	public boolean isExplicitPolymorphism() {
-	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
-public class IdGenerator implements Serializable {
-    public IdGenerator( String name,
-    public String getStrategy() {
-    public String getName() {
-    public Map<String, String> getParameters() {
-public enum InheritanceType {
-	public static InheritanceType get(javax.persistence.InheritanceType jpaType) {
-public interface KeyValueBinding extends AttributeBinding {
-	public boolean isKeyCascadeDeleteEnabled();
-	public String getUnsavedValue();
-public class ManyToAnyCollectionElement extends AbstractCollectionElement {
-	public CollectionElementNature getCollectionElementNature() {
-public class ManyToManyCollectionElement extends AbstractCollectionElement {
-	public CollectionElementNature getCollectionElementNature() {
-	public void fromHbmXml(Element node){
-	public String getManyToManyWhere() {
-	public void setManyToManyWhere(String manyToManyWhere) {
-	public String getManyToManyOrderBy() {
-	public void setManyToManyOrderBy(String manyToManyOrderBy) {
-public class ManyToOneAttributeBinding extends BasicAttributeBinding implements SingularAssociationAttributeBinding {
-	public boolean isAssociation() {
-	public final boolean isPropertyReference() {
-	public final String getReferencedEntityName() {
-	public void setReferencedEntityName(String referencedEntityName) {
-	public final String getReferencedAttributeName() {
-	public void setReferencedAttributeName(String referencedEntityAttributeName) {
-	public CascadeStyle getCascadeStyle() {
-	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles) {
-	public FetchTiming getFetchTiming() {
-	public void setFetchTiming(FetchTiming fetchTiming) {
-	public FetchStyle getFetchStyle() {
-	public void setFetchStyle(FetchStyle fetchStyle) {
-	public FetchMode getFetchMode() {
-	public final boolean isReferenceResolved() {
-	public final void resolveReference(AttributeBinding referencedAttributeBinding) {
-	public AttributeBinding getReferencedAttributeBinding() {
-	public final EntityBinding getReferencedEntityBinding() {
-//	public void validate() {
-public class MetaAttribute implements Serializable {
-	public MetaAttribute(String name) {
-	public String getName() {
-	public List<String> getValues() {
-	public void addValue(String value) {
-	public String getValue() {
-	public boolean isMultiValued() {
-	public String toString() {
-public class OneToManyCollectionElement extends AbstractCollectionElement {
-	public CollectionElementNature getCollectionElementNature() {
-public interface PluralAttributeBinding extends  AssociationAttributeBinding {
-	public PluralAttribute getAttribute();
-	public CollectionKey getCollectionKey();
-	public AbstractCollectionElement getCollectionElement();
-	public TableSpecification getCollectionTable();
-	public boolean isMutable();
-	public Caching getCaching();
-	public Class<? extends CollectionPersister> getCollectionPersisterClass();
-	public String getCustomLoaderName();
-	public CustomSQL getCustomSqlInsert();
-	public CustomSQL getCustomSqlUpdate();
-	public CustomSQL getCustomSqlDelete();
-	public CustomSQL getCustomSqlDeleteAll();
-	public boolean isOrphanDelete();
-public class SetBinding extends AbstractPluralAttributeBinding {
-	public Comparator getComparator() {
-	public void setComparator(Comparator comparator) {
-public class SimpleValueBinding {
-	public SimpleValueBinding() {
-	public SimpleValueBinding(SimpleValue simpleValue) {
-	public SimpleValueBinding(SimpleValue simpleValue, boolean includeInInsert, boolean includeInUpdate) {
-	public SimpleValueBinding(boolean includeInInsert, boolean includeInUpdate) {
-	public SimpleValue getSimpleValue() {
-	public void setSimpleValue(SimpleValue simpleValue) {
-	public boolean isDerived() {
-	public boolean isNullable() {
-	public boolean isIncludeInInsert() {
-	public void setIncludeInInsert(boolean includeInInsert) {
-	public boolean isIncludeInUpdate() {
-	public void setIncludeInUpdate(boolean includeInUpdate) {
-public interface SingularAssociationAttributeBinding extends SingularAttributeBinding, AssociationAttributeBinding {
-	public boolean isPropertyReference();
-	public String getReferencedEntityName();
-	public void setReferencedEntityName(String referencedEntityName);
-	public String getReferencedAttributeName();
-	public void setReferencedAttributeName(String referencedAttributeName);
-	public void resolveReference(AttributeBinding attributeBinding);
-	public boolean isReferenceResolved();
-	public EntityBinding getReferencedEntityBinding();
-	public AttributeBinding getReferencedAttributeBinding();
-public interface SingularAttributeBinding extends AttributeBinding {
-	public Value getValue();
-	public int getSimpleValueSpan();
-	public Iterable<SimpleValueBinding> getSimpleValueBindings();
-	public void setSimpleValueBindings(Iterable<SimpleValueBinding> simpleValueBindings);
-	public boolean hasDerivedValue();
-	public boolean isNullable();
-	public PropertyGeneration getGeneration();
-public class TypeDef implements Serializable {
-	public TypeDef(String name, String typeClass, Map<String, String> parameters) {
-	public String getName() {
-	public String getTypeClass() {
-    public Map<String, String> getParameters() {
-public abstract class AbstractAttributeContainer implements AttributeContainer, Hierarchical {
-	public AbstractAttributeContainer(String name, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
-	public String getName() {
-	public String getClassName() {
-	public Class<?> getClassReference() {
-	public ValueHolder<Class<?>> getClassReferenceUnresolved() {
-	public Hierarchical getSuperType() {
-	public Set<Attribute> attributes() {
-	public String getRoleBaseName() {
-	public Attribute locateAttribute(String name) {
-	public SingularAttribute locateSingularAttribute(String name) {
-	public SingularAttribute createSingularAttribute(String name) {
-	public SingularAttribute createVirtualSingularAttribute(String name) {
-	public SingularAttribute locateComponentAttribute(String name) {
-	public SingularAttribute createComponentAttribute(String name, Component component) {
-	public PluralAttribute locatePluralAttribute(String name) {
-	public PluralAttribute locateBag(String name) {
-	public PluralAttribute createBag(String name) {
-	public PluralAttribute locateSet(String name) {
-	public PluralAttribute createSet(String name) {
-	public IndexedPluralAttribute locateList(String name) {
-	public IndexedPluralAttribute createList(String name) {
-	public IndexedPluralAttribute locateMap(String name) {
-	public IndexedPluralAttribute createMap(String name) {
-	public String toString() {
-	public static class SingularAttributeImpl implements SingularAttribute {
-		public SingularAttributeImpl(String name, AttributeContainer attributeContainer) {
-		public boolean isTypeResolved() {
-		public void resolveType(Type type) {
-		public Type getSingularAttributeType() {
-		public String getName() {
-		public AttributeContainer getAttributeContainer() {
-		public boolean isSingular() {
-	public static class PluralAttributeImpl implements PluralAttribute {
-		public PluralAttributeImpl(String name, PluralAttributeNature nature, AttributeContainer attributeContainer) {
-		public AttributeContainer getAttributeContainer() {
-		public boolean isSingular() {
-		public PluralAttributeNature getNature() {
-		public String getName() {
-		public String getRole() {
-		public Type getElementType() {
-		public void setElementType(Type elementType) {
-	public static class IndexedPluralAttributeImpl extends PluralAttributeImpl implements IndexedPluralAttribute {
-		public IndexedPluralAttributeImpl(String name, PluralAttributeNature nature, AttributeContainer attributeContainer) {
-		public Type getIndexType() {
-		public void setIndexType(Type indexType) {
-public interface Attribute {
-	public String getName();
-	public AttributeContainer getAttributeContainer();
-	public boolean isSingular();
-public interface AttributeContainer extends Type {
-	public String getRoleBaseName();
-	public Attribute locateAttribute(String name);
-	public Set<Attribute> attributes();
-	public SingularAttribute locateSingularAttribute(String name);
-	public SingularAttribute createSingularAttribute(String name);
-	public SingularAttribute createVirtualSingularAttribute(String name);
-	public SingularAttribute locateComponentAttribute(String name);
-	public SingularAttribute createComponentAttribute(String name, Component component);
-	public PluralAttribute locatePluralAttribute(String name);
-	public PluralAttribute locateBag(String name);
-	public PluralAttribute createBag(String name);
-	public PluralAttribute locateSet(String name);
-	public PluralAttribute createSet(String name);
-	public IndexedPluralAttribute locateList(String name);
-	public IndexedPluralAttribute createList(String name);
-	public IndexedPluralAttribute locateMap(String name);
-	public IndexedPluralAttribute createMap(String name);
-public class BasicType implements Type {
-	public BasicType(String name, ValueHolder<Class<?>> classReference) {
-	public String getName() {
-	public String getClassName() {
-	public Class<?> getClassReference() {
-	public ValueHolder<Class<?>> getClassReferenceUnresolved() {
-	public boolean isAssociation() {
-	public boolean isComponent() {
-public class Component extends AbstractAttributeContainer {
-	public Component(String name, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
-	public boolean isAssociation() {
-	public boolean isComponent() {
-	public String getRoleBaseName() {
-public class Entity extends AbstractAttributeContainer {
-	public Entity(String entityName, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
-	public boolean isAssociation() {
-	public boolean isComponent() {
-public interface Hierarchical extends AttributeContainer {
-	public Hierarchical getSuperType();
-public interface IndexedPluralAttribute extends PluralAttribute {
-	public Type getIndexType();
-	public void setIndexType(Type indexType);
-public class JavaType {
-	public JavaType(final String name, final ClassLoaderService classLoaderService) {
-					public Class<?> initialize() {
-	public JavaType(Class<?> theClass) {
-	public String getName() {
-	public Class<?> getClassReference() {
-	public String toString() {
-public class NonEntity extends AbstractAttributeContainer {
-	public NonEntity(String entityName, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
-	public boolean isAssociation() {
-	public boolean isComponent() {
-public interface PluralAttribute extends Attribute {
-	public String getRole();
-	public PluralAttributeNature getNature();
-	public Type getElementType();
-	public void setElementType(Type elementType);
-public enum PluralAttributeNature {
-	public String getName() {
-	public Class getJavaContract() {
-	public boolean isIndexed() {
-public interface SingularAttribute extends Attribute {
-	public Type getSingularAttributeType();
-	public boolean isTypeResolved();
-	public void resolveType(Type type);
-public class Superclass extends AbstractAttributeContainer {
-	public Superclass(String entityName, String className, ValueHolder<Class<?>> classReference, Hierarchical superType) {
-	public boolean isAssociation() {
-	public boolean isComponent() {
-public interface Type {
-	public String getName();
-	public String getClassName();
-	public Class<?> getClassReference();
-	public ValueHolder<Class<?>> getClassReferenceUnresolved();
-	public boolean isAssociation();
-	public boolean isComponent();
-public enum TypeNature {
-	public String getName() {
-	public String toString() {
-public abstract class AbstractAuxiliaryDatabaseObject implements AuxiliaryDatabaseObject {
-	public void addDialectScope(String dialectName) {
-	public Iterable<String> getDialectScopes() {
-	public boolean appliesToDialect(Dialect dialect) {
-	public String getExportIdentifier() {
-public abstract class AbstractConstraint implements Constraint {
-	public TableSpecification getTable() {
-	public String getName() {
-	public Iterable<Column> getColumns() {
-	public void addColumn(Column column) {
-	public String[] sqlDropStrings(Dialect dialect) {
-	public String[] sqlCreateStrings(Dialect dialect) {
-public abstract class AbstractSimpleValue implements SimpleValue {
-	public TableSpecification getTable() {
-	public int getPosition() {
-	public Datatype getDatatype() {
-	public void setDatatype(Datatype datatype) {
-	public void validateJdbcTypes(JdbcCodes typeCodes) {
-public abstract class AbstractTableSpecification implements TableSpecification {
-	public AbstractTableSpecification() {
-	public int getTableNumber() {
-	public Iterable<SimpleValue> values() {
-	public Column locateOrCreateColumn(String name) {
-	public DerivedValue locateOrCreateDerivedValue(String fragment) {
-	public Tuple createTuple(String name) {
-	public Iterable<ForeignKey> getForeignKeys() {
-	public ForeignKey createForeignKey(TableSpecification targetTable, String name) {
-	public PrimaryKey getPrimaryKey() {
-public interface AuxiliaryDatabaseObject extends Exportable, Serializable {
-public class BasicAuxiliaryDatabaseObjectImpl extends AbstractAuxiliaryDatabaseObject {
-	public BasicAuxiliaryDatabaseObjectImpl(
-	public String[] sqlCreateStrings(Dialect dialect) {
-	public String[] sqlDropStrings(Dialect dialect) {
-public class CheckConstraint {
-	public CheckConstraint(Table table) {
-	public CheckConstraint(Table table, String name, String condition) {
-	public String getCondition() {
-	public void setCondition(String condition) {
-	public Table getTable() {
-	public String getName() {
-public class Column extends AbstractSimpleValue {
-	public void initialize(ColumnRelationalState state, boolean forceNonNullable, boolean forceUnique) {
-	public Identifier getColumnName() {
-	public boolean isNullable() {
-	public void setNullable(boolean nullable) {
-	public boolean isUnique() {
-	public void setUnique(boolean unique) {
-	public String getDefaultValue() {
-	public void setDefaultValue(String defaultValue) {
-	public String getCheckCondition() {
-	public void setCheckCondition(String checkCondition) {
-	public String getSqlType() {
-	public void setSqlType(String sqlType) {
-	public String getReadFragment() {
-	public void setReadFragment(String readFragment) {
-	public String getWriteFragment() {
-	public void setWriteFragment(String writeFragment) {
-	public String getComment() {
-	public void setComment(String comment) {
-	public Size getSize() {
-	public void setSize(Size size) {
-	public String toLoggableString() {
-	public String getAlias(Dialect dialect) {
-public interface Constraint extends Exportable {
-	public TableSpecification getTable();
-	public String getName();
-	public Iterable<Column> getColumns();
-public class Database {
-	public Database(Metadata.Options options) {
-	public Schema getDefaultSchema() {
-	public Schema locateSchema(Schema.Name name) {
-	public Schema getSchema(Identifier schema, Identifier catalog) {
-	public Schema getSchema(String schema, String catalog) {
-	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
-	public Iterable<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjects() {
-	public String[] generateSchemaCreationScript(Dialect dialect) {
-	public String[] generateDropSchemaScript(Dialect dialect) {
-public class Datatype {
-	public Datatype(int typeCode, String typeName, Class javaType) {
-    public int getTypeCode() {
-	public String getTypeName() {
-	public Class getJavaType() {
-	public boolean equals(Object o) {
-	public int hashCode() {
-	public String toString() {
-public class DerivedValue extends AbstractSimpleValue {
-	public DerivedValue(TableSpecification table, int position, String expression) {
-	public String toLoggableString() {
-	public String getAlias(Dialect dialect) {
-	public String getExpression() {
-public interface Exportable {
-	public String getExportIdentifier();
-	public String[] sqlCreateStrings(Dialect dialect);
-	public String[] sqlDropStrings(Dialect dialect);
-public class ForeignKey extends AbstractConstraint implements Constraint, Exportable {
-	public TableSpecification getSourceTable() {
-	public TableSpecification getTargetTable() {
-	public Iterable<Column> getSourceColumns() {
-	public Iterable<Column> getTargetColumns() {
-	public void addColumn(Column column) {
-	public void addColumnMapping(Column sourceColumn, Column targetColumn) {
-	public String getExportIdentifier() {
-	public ReferentialAction getDeleteRule() {
-	public void setDeleteRule(ReferentialAction deleteRule) {
-	public ReferentialAction getUpdateRule() {
-	public void setUpdateRule(ReferentialAction updateRule) {
-	public String[] sqlDropStrings(Dialect dialect) {
-	public String sqlConstraintStringInAlterTable(Dialect dialect) {
-	public static enum ReferentialAction {
-		public String getActionString() {
-public class Identifier {
-	public static Identifier toIdentifier(String name) {
-	public static boolean isQuoted(String name) {
-	public Identifier(String name, boolean quoted) {
-	public String getName() {
-	public boolean isQuoted() {
-	public String encloseInQuotesIfQuoted(Dialect dialect) {
-	public String toString() {
-	public boolean equals(Object o) {
-	public int hashCode() {
-public class IllegalIdentifierException extends HibernateException {
-	public IllegalIdentifierException(String s) {
-public class InLineView extends AbstractTableSpecification {
-	public InLineView(Schema schema, String logicalName, String select) {
-	public Schema getSchema() {
-	public String getSelect() {
-	public String getLoggableValueQualifier() {
-	public Iterable<Index> getIndexes() {
-	public Index getOrCreateIndex(String name) {
-	public Iterable<UniqueKey> getUniqueKeys() {
-	public UniqueKey getOrCreateUniqueKey(String name) {
-	public Iterable<CheckConstraint> getCheckConstraints() {
-	public void addCheckConstraint(String checkCondition) {
-	public Iterable<String> getComments() {
-	public void addComment(String comment) {
-	public String getQualifiedName(Dialect dialect) {
-	public String toLoggableString() {
-public class Index extends AbstractConstraint implements Constraint {
-	public String getExportIdentifier() {
-	public String[] sqlCreateStrings(Dialect dialect) {
-	public static String buildSqlCreateIndexString(
-	public static String buildSqlDropIndexString(
-	public String sqlConstraintStringInAlterTable(Dialect dialect) {
-	public String[] sqlDropStrings(Dialect dialect) {
-public interface Loggable {
-	public String toLoggableString();
-public class ObjectName {
-	public ObjectName(String objectName) {
-	public ObjectName(Identifier name) {
-	public ObjectName(Schema schema, String name) {
-	public ObjectName(Schema schema, Identifier name) {
-	public ObjectName(String schemaName, String catalogName, String name) {
-	public ObjectName(Identifier schema, Identifier catalog, Identifier name) {
-	public Identifier getSchema() {
-	public Identifier getCatalog() {
-	public Identifier getName() {
-	public String toText() {
-	public String toText(Dialect dialect) {
-	public boolean equals(Object o) {
-	public int hashCode() {
-	public String toString() {
-public class PrimaryKey extends AbstractConstraint implements Constraint, Exportable {
-	public String getName() {
-	public void setName(String name) {
-	public String getExportIdentifier() {
-	public String sqlConstraintStringInCreateTable(Dialect dialect) {
-	public String sqlConstraintStringInAlterTable(Dialect dialect) {
-public class Schema {
-	public Schema(Name name) {
-	public Schema(Identifier schema, Identifier catalog) {
-	public Name getName() {
-	public Table locateTable(Identifier name) {
-	public Table createTable(Identifier name) {
-	public Table locateOrCreateTable(Identifier name) {
-	public Iterable<Table> getTables() {
-	public InLineView getInLineView(String logicalName) {
-	public InLineView createInLineView(String logicalName, String subSelect) {
-	public String toString() {
-	public boolean equals(Object o) {
-	public int hashCode() {
-	public static class Name {
-		public Name(Identifier schema, Identifier catalog) {
-		public Name(String schema, String catalog) {
-		public Identifier getSchema() {
-		public Identifier getCatalog() {
-		public String toString() {
-		public boolean equals(Object o) {
-		public int hashCode() {
-public class Sequence implements Exportable {
-	public Sequence(Schema schema, String name) {
-	public Sequence(Schema schema, String name, int initialValue, int incrementSize) {
-	public Schema getSchema() {
-	public String getName() {
-	public String getExportIdentifier() {
-	public int getInitialValue() {
-	public int getIncrementSize() {
-	public String[] sqlCreateStrings(Dialect dialect) throws MappingException {
-	public String[] sqlDropStrings(Dialect dialect) throws MappingException {
-public interface SimpleValue extends Value {
-	public Datatype getDatatype();
-	public void setDatatype(Datatype datatype);
-	public String getAlias(Dialect dialect);
-public class Table extends AbstractTableSpecification implements Exportable {
-	public Table(Schema database, String tableName) {
-	public Table(Schema database, Identifier tableName) {
-	public Schema getSchema() {
-	public Identifier getTableName() {
-	public String getLoggableValueQualifier() {
-	public String getExportIdentifier() {
-	public String toLoggableString() {
-	public Iterable<Index> getIndexes() {
-	public Index getOrCreateIndex(String name) {
-	public Iterable<UniqueKey> getUniqueKeys() {
-	public UniqueKey getOrCreateUniqueKey(String name) {
-	public Iterable<CheckConstraint> getCheckConstraints() {
-	public void addCheckConstraint(String checkCondition) {
-	public Iterable<String> getComments() {
-	public void addComment(String comment) {
-	public String getQualifiedName(Dialect dialect) {
-	public String[] sqlCreateStrings(Dialect dialect) {
-	public String[] sqlDropStrings(Dialect dialect) {
-	public String toString() {
-public interface TableSpecification extends ValueContainer, Loggable {
-	public Schema getSchema();
-	public int getTableNumber();
-	public PrimaryKey getPrimaryKey();
-	public Column locateOrCreateColumn(String name);
-	public Tuple createTuple(String name);
-	public DerivedValue locateOrCreateDerivedValue(String fragment);
-	public Iterable<ForeignKey> getForeignKeys();
-	public ForeignKey createForeignKey(TableSpecification targetTable, String name);
-	public Iterable<Index> getIndexes();
-	public Index getOrCreateIndex(String name);
-	public Iterable<UniqueKey> getUniqueKeys();
-	public UniqueKey getOrCreateUniqueKey(String name);
-	public Iterable<CheckConstraint> getCheckConstraints();
-	public void addCheckConstraint(String checkCondition);
-	public Iterable<String> getComments();
-	public void addComment(String comment);
-	public String getQualifiedName(Dialect dialect);
-public class Tuple implements Value, ValueContainer, Loggable {
-	public Tuple(TableSpecification table, String name) {
-	public TableSpecification getTable() {
-	public int valuesSpan() {
-	public Iterable<SimpleValue> values() {
-	public void addValue(SimpleValue value) {
-	public String getLoggableValueQualifier() {
-	public String toLoggableString() {
-	public void validateJdbcTypes(JdbcCodes typeCodes) {
-public class UniqueKey extends AbstractConstraint implements Constraint {
-	public String getExportIdentifier() {
-	public String[] sqlCreateStrings(Dialect dialect) {
-	public String[] sqlDropStrings(Dialect dialect) {
-public interface Value {
-	public TableSpecification getTable();
-	public String toLoggableString();
-	public static class JdbcCodes {
-		public JdbcCodes(int[] typeCodes) {
-		public int nextJdbcCde() {
-		public int getIndex() {
-	public void validateJdbcTypes(JdbcCodes typeCodes);
-public interface ValueContainer {
-	public Iterable<SimpleValue> values();
-	public String getLoggableValueQualifier();
-public interface ColumnRelationalState extends SimpleValueRelationalState {
-public interface DerivedValueRelationalState extends SimpleValueRelationalState {
-public interface ManyToOneRelationalState extends ValueRelationalState {
-public interface SimpleValueRelationalState extends ValueRelationalState {
-public interface TupleRelationalState extends ValueRelationalState {
-public interface ValueRelationalState {
-public interface BindingContext {
-	public ServiceRegistry getServiceRegistry();
-	public NamingStrategy getNamingStrategy();
-	public MappingDefaults getMappingDefaults();
-	public MetadataImplementor getMetadataImplementor();
-	public <T> Class<T> locateClassByName(String name);
-	public Type makeJavaType(String className);
-	public boolean isGloballyQuotedIdentifiers();
-	public ValueHolder<Class<?>> makeClassReference(String className);
-	public String qualifyClassName(String name);
-public interface LocalBindingContext extends BindingContext {
-	public Origin getOrigin();
-public interface MappingDefaults {
-	public String getPackageName();
-	public String getSchemaName();
-	public String getCatalogName();
-	public String getIdColumnName();
-	public String getDiscriminatorColumnName();
-	public String getCascadeStyle();
-	public String getPropertyAccessorName();
-	public boolean areAssociationsLazy();
-	public AccessType getCacheAccessType();
-public class MappingException extends HibernateException {
-	public MappingException(String message, Origin origin) {
-	public MappingException(String message, Throwable root, Origin origin) {
-	public Origin getOrigin() {
-public class MappingNotFoundException extends MappingException {
-	public MappingNotFoundException(String message, Origin origin) {
-	public MappingNotFoundException(Origin origin) {
-	public MappingNotFoundException(String message, Throwable root, Origin origin) {
-	public MappingNotFoundException(Throwable root, Origin origin) {
-public class MetaAttributeContext {
-	public MetaAttributeContext() {
-	public MetaAttributeContext(MetaAttributeContext parentContext) {
-	public Iterable<String> getKeys() {
-	public Iterable<String> getLocalKeys() {
-	public MetaAttribute getMetaAttribute(String key) {
-	public MetaAttribute getLocalMetaAttribute(String key) {
-	public void add(MetaAttribute metaAttribute) {
-public interface MetadataImplementor extends Metadata, BindingContext, Mapping {
-	public ServiceRegistry getServiceRegistry();
-	public Database getDatabase();
-	public TypeResolver getTypeResolver();
-	public void addImport(String entityName, String entityName1);
-	public void addEntity(EntityBinding entityBinding);
-	public void addCollection(PluralAttributeBinding collectionBinding);
-	public void addFetchProfile(FetchProfile profile);
-	public void addTypeDefinition(TypeDef typeDef);
-	public void addFilterDefinition(FilterDefinition filterDefinition);
-	public void addIdGenerator(IdGenerator generator);
-	public void registerIdentifierGenerator(String name, String clazz);
-	public void addNamedNativeQuery(NamedSQLQueryDefinition def);
-	public void addNamedQuery(NamedQueryDefinition def);
-	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition);
-	public void setGloballyQuotedIdentifiers(boolean b);
-	public MetaAttributeContext getGlobalMetaAttributeContext();
-public interface MetadataSourceProcessor {
-	public void prepare(MetadataSources sources);
-	public void processIndependentMetadata(MetadataSources sources);
-	public void processTypeDependentMetadata(MetadataSources sources);
-	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames);
-	public void processMappingDependentMetadata(MetadataSources sources);
-public interface AnnotationBindingContext extends BindingContext {
-public class AnnotationBindingContextImpl implements AnnotationBindingContext {
-	public AnnotationBindingContextImpl(MetadataImplementor metadata, Index index) {
-					public ClassLoaderService initialize() {
-	public Index getIndex() {
-	public ClassInfo getClassInfo(String name) {
-	public void resolveAllTypes(String className) {
-	public ResolvedType getResolvedType(Class<?> clazz) {
-	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
-	public ServiceRegistry getServiceRegistry() {
-	public NamingStrategy getNamingStrategy() {
-	public MappingDefaults getMappingDefaults() {
-	public MetadataImplementor getMetadataImplementor() {
-	public <T> Class<T> locateClassByName(String name) {
-	public Type makeJavaType(String className) {
-	public ValueHolder<Class<?>> makeClassReference(String className) {
-	public String qualifyClassName(String name) {
-	public boolean isGloballyQuotedIdentifiers() {
-public class AnnotationMetadataSourceProcessorImpl implements MetadataSourceProcessor {
-	public AnnotationMetadataSourceProcessorImpl(MetadataImpl metadata) {
-	public void prepare(MetadataSources sources) {
-	public void processIndependentMetadata(MetadataSources sources) {
-	public void processTypeDependentMetadata(MetadataSources sources) {
-	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
-	public void processMappingDependentMetadata(MetadataSources sources) {
-public class EntityHierarchyBuilder {
-	public static Set<EntityHierarchy> createEntityHierarchies(AnnotationBindingContext bindingContext) {
-public class EntityHierarchyImpl implements EntityHierarchy {
-	public EntityHierarchyImpl(RootEntitySource source, InheritanceType inheritanceType) {
-	public InheritanceType getHierarchyInheritanceType() {
-	public RootEntitySource getRootEntitySource() {
-public class EnumConversionHelper {
-	public static String generationTypeToGeneratorStrategyName(GenerationType generatorEnum, boolean useNewGeneratorMappings) {
-	public static CascadeStyle cascadeTypeToCascadeStyle(CascadeType cascadeType) {
-	public static FetchMode annotationFetchModeToHibernateFetchMode(org.hibernate.annotations.FetchMode annotationFetchMode) {
-	public static Set<CascadeStyle> cascadeTypeToCascadeStyleSet(Set<CascadeType> cascadeTypes) {
-public interface HibernateDotNames {
-public interface JPADotNames {
-public class JandexHelper {
-	public static <T> T getValue(AnnotationInstance annotation, String element, Class<T> type) throws AssertionFailure {
-	public static <T> T getValue(AnnotationInstance annotation, String element, Class<T> type,
-	public static <T extends Enum<T>> T getEnumValue(AnnotationInstance annotation, String element, Class<T> type) {
-	public static String getPropertyName(AnnotationTarget target) {
-	public static AnnotationInstance getSingleAnnotation(ClassInfo classInfo, DotName annotationName)
-	public static AnnotationInstance getSingleAnnotation(Map<DotName, List<AnnotationInstance>> annotations, DotName annotationName)
-	public static boolean containsSingleAnnotations(Map<DotName, List<AnnotationInstance>> annotations, DotName annotationName)
-	public static Index indexForClass(ClassLoaderService classLoaderService, Class<?>... classes) {
-	public static Map<DotName, List<AnnotationInstance>> getMemberAnnotations(ClassInfo classInfo, String name) {
-public class ReflectionHelper {
-	public static String getPropertyName(Member member) {
-	public static boolean isProperty(Member m) {
-public class UnknownInheritanceTypeException extends HibernateException {
-	public UnknownInheritanceTypeException(String message) {
-public class AssociationAttribute extends MappedAttribute {
-	public static AssociationAttribute createAssociationAttribute(String name,
-	public boolean isIgnoreNotFound() {
-	public String getReferencedEntityType() {
-	public String getMappedBy() {
-	public AttributeNature getAssociationNature() {
-	public Set<CascadeType> getCascadeTypes() {
-	public boolean isOrphanRemoval() {
-	public FetchMode getFetchMode() {
-	public String getReferencedIdAttributeName() {
-	public boolean mapsId() {
-	public AttributeTypeResolver getHibernateTypeResolver() {
-	public boolean isLazy() {
-	public boolean isOptional() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getPropertyGeneration() {
-public enum AttributeNature {
-	public DotName getAnnotationDotName() {
-public class AttributeOverride {
-	public AttributeOverride(AnnotationInstance attributeOverrideAnnotation) {
-	public AttributeOverride(String prefix, AnnotationInstance attributeOverrideAnnotation) {
-	public ColumnValues getColumnValues() {
-	public String getAttributePath() {
-	public String toString() {
-	public boolean equals(Object o) {
-	public int hashCode() {
-public class BasicAttribute extends MappedAttribute {
-	public static BasicAttribute createSimpleAttribute(String name,
-	public boolean isVersioned() {
-	public boolean isLazy() {
-	public boolean isOptional() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getPropertyGeneration() {
-	public String getCustomWriteFragment() {
-	public String getCustomReadFragment() {
-	public String getCheckCondition() {
-	public IdGenerator getIdGenerator() {
-	public String toString() {
-	public AttributeTypeResolver getHibernateTypeResolver() {
-public class ColumnSourceImpl extends ColumnValuesSourceImpl {
-	public String getName() {
-	public String getReadFragment() {
-	public String getWriteFragment() {
-	public String getCheckCondition() {
-public class ColumnValues {
-	public ColumnValues(AnnotationInstance columnAnnotation) {
-	public final String getName() {
-	public final boolean isUnique() {
-	public final boolean isNullable() {
-	public final boolean isInsertable() {
-	public final boolean isUpdatable() {
-	public final String getColumnDefinition() {
-	public final String getTable() {
-	public final int getLength() {
-	public final int getPrecision() {
-	public final int getScale() {
-	public void setName(String name) {
-	public void setUnique(boolean unique) {
-	public void setNullable(boolean nullable) {
-	public void setInsertable(boolean insertable) {
-	public void setUpdatable(boolean updatable) {
-	public void setColumnDefinition(String columnDefinition) {
-	public void setTable(String table) {
-	public void setLength(int length) {
-	public void setPrecision(int precision) {
-	public void setScale(int scale) {
-	public String toString() {
-	public boolean equals(Object o) {
-	public int hashCode() {
-public class ColumnValuesSourceImpl implements ColumnSource {
-	public ColumnValuesSourceImpl(ColumnValues columnValues) {
-	public String getName() {
-	public boolean isNullable() {
-	public String getDefaultValue() {
-	public String getSqlType() {
-	public Datatype getDatatype() {
-	public Size getSize() {
-	public boolean isUnique() {
-	public String getComment() {
-	public boolean isIncludedInInsert() {
-	public boolean isIncludedInUpdate() {
-	public String getContainingTableName() {
-	public String getReadFragment() {
-	public String getWriteFragment() {
-	public String getCheckCondition() {
-public class DerivedValueSourceImpl implements DerivedValueSource {
-    public String getExpression() {
-    public String getContainingTableName() {
-public class DiscriminatorSourceImpl implements DiscriminatorSource {
-	public DiscriminatorSourceImpl(EntityClass entityClass) {
-	public boolean isForced() {
-	public boolean isInserted() {
-    public RelationalValueSource getDiscriminatorRelationalValueSource() {
-	public String getExplicitHibernateTypeName() {
-public class ExplicitHibernateTypeSourceImpl implements ExplicitHibernateTypeSource {
-    public ExplicitHibernateTypeSourceImpl(AttributeTypeResolver typeResolver) {
-    public String getName() {
-    public Map<String, String> getParameters() {
-public class FormulaValue {
-    public FormulaValue(String tableName, String expression) {
-    public String getExpression() {
-    public String getContainingTableName() {
-public abstract class MappedAttribute implements Comparable<MappedAttribute> {
-	public String getName() {
-	public final Class<?> getAttributeType() {
-	public String getAccessType() {
-	public EntityBindingContext getContext() {
-	public Map<DotName, List<AnnotationInstance>> annotations() {
-	public ColumnValues getColumnValues() {
-	public boolean isId() {
-	public boolean isOptimisticLockable() {
-	public int compareTo(MappedAttribute mappedProperty) {
-	public String toString() {
-	public abstract AttributeTypeResolver getHibernateTypeResolver();
-	public abstract boolean isLazy();
-	public abstract boolean isOptional();
-	public abstract boolean isInsertable();
-	public abstract boolean isUpdatable();
-	public abstract PropertyGeneration getPropertyGeneration();
-public class SimpleIdentifierSourceImpl implements SimpleIdentifierSource {
-	public SimpleIdentifierSourceImpl(BasicAttribute attribute, Map<String, AttributeOverride> attributeOverrideMap) {
-	public Nature getNature() {
-	public SingularAttributeSource getIdentifierAttributeSource() {
-	public IdGenerator getIdentifierGeneratorDescriptor() {
-public class SingularAttributeSourceImpl implements SingularAttributeSource {
-	public SingularAttributeSourceImpl(MappedAttribute attribute) {
-	public SingularAttributeSourceImpl(MappedAttribute attribute, AttributeOverride attributeOverride) {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getPropertyAccessorName() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public String getName() {
-	public List<RelationalValueSource> relationalValueSources() {
-	public boolean isVirtualAttribute() {
-	public boolean isSingular() {
-	public SingularAttributeNature getNature() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-public class ToOneAttributeSourceImpl extends SingularAttributeSourceImpl implements ToOneAttributeSource {
-	public ToOneAttributeSourceImpl(AssociationAttribute associationAttribute) {
-	public SingularAttributeNature getNature() {
-	public String getReferencedEntityName() {
-	public String getReferencedEntityAttributeName() {
-	public Iterable<CascadeStyle> getCascadeStyles() {
-	public FetchMode getFetchMode() {
-	public FetchTiming getFetchTiming() {
-	public FetchStyle getFetchStyle() {
-public abstract class AbstractAttributeTypeResolver implements AttributeTypeResolver {
-	final public String getExplicitHibernateTypeName() {
-	final public Map<String, String> getExplicitHibernateTypeParameters() {
-public interface AttributeTypeResolver {
-public class AttributeTypeResolverImpl extends AbstractAttributeTypeResolver {
-	public AttributeTypeResolverImpl(MappedAttribute mappedAttribute) {
-public class CompositeAttributeTypeResolver implements AttributeTypeResolver {
-	public CompositeAttributeTypeResolver(AttributeTypeResolverImpl explicitHibernateTypeResolver) {
-	public void addHibernateTypeResolver(AttributeTypeResolver resolver) {
-	public String getExplicitHibernateTypeName() {
-	public Map<String, String> getExplicitHibernateTypeParameters() {
-public class EnumeratedTypeResolver extends AbstractAttributeTypeResolver {
-	public EnumeratedTypeResolver(MappedAttribute mappedAttribute) {
-	public String resolveHibernateTypeName(AnnotationInstance enumeratedAnnotation) {
-public class LobTypeResolver extends AbstractAttributeTypeResolver {
-	public LobTypeResolver(MappedAttribute mappedAttribute) {
-	public String resolveHibernateTypeName(AnnotationInstance annotationInstance) {
-public class TemporalTypeResolver extends AbstractAttributeTypeResolver {
-	public TemporalTypeResolver(MappedAttribute mappedAttribute) {
-	public String resolveHibernateTypeName(AnnotationInstance temporalAnnotation) {
-public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
-	public ComponentAttributeSourceImpl(EmbeddableClass embeddableClass, String parentPath, Map<String, AttributeOverride> attributeOverrides) {
-	public boolean isVirtualAttribute() {
-	public SingularAttributeNature getNature() {
-	public boolean isSingular() {
-	public String getClassName() {
-	public ValueHolder<Class<?>> getClassReference() {
-	public String getName() {
-	public String getExplicitTuplizerClassName() {
-	public String getPropertyAccessorName() {
-	public LocalBindingContext getLocalBindingContext() {
-	public Iterable<AttributeSource> attributeSources() {
-	public String getPath() {
-	public String getParentReferenceAttributeName() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-	public List<RelationalValueSource> relationalValueSources() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-	public String toString() {
-public class ConfiguredClass {
-	public static final Logger LOG = Logger.getLogger( ConfiguredClass.class.getName() );
-	public ConfiguredClass(
-	public String getName() {
-	public Class<?> getConfiguredClass() {
-	public ClassInfo getClassInfo() {
-	public ConfiguredClass getParent() {
-	public EntityBindingContext getLocalBindingContext() {
-	public Iterable<BasicAttribute> getSimpleAttributes() {
-	public Iterable<BasicAttribute> getIdAttributes() {
-	public BasicAttribute getVersionAttribute() {
-	public Iterable<AssociationAttribute> getAssociationAttributes() {
-	public Map<String, EmbeddableClass> getEmbeddedClasses() {
-	public Map<String, AttributeOverride> getAttributeOverrideMap() {
-	public AccessType getClassAccessType() {
-	public String getCustomTuplizer() {
-	public String toString() {
-public enum ConfiguredClassType {
-public class EmbeddableClass extends ConfiguredClass {
-	public EmbeddableClass(
-	public String getEmbeddedAttributeName() {
-	public String getParentReferencingAttributeName() {
-public class EmbeddableHierarchy implements Iterable<EmbeddableClass> {
-	public static EmbeddableHierarchy createEmbeddableHierarchy(Class<?> embeddableClass, String propertyName, AccessType accessType, AnnotationBindingContext context) {
-	public AccessType getDefaultAccessType() {
-	public Iterator<EmbeddableClass> iterator() {
-	public EmbeddableClass getLeaf() {
-	public String toString() {
-public class EntityBindingContext implements LocalBindingContext, AnnotationBindingContext {
-	public EntityBindingContext(AnnotationBindingContext contextDelegate, ConfiguredClass source) {
-	public Origin getOrigin() {
-	public ServiceRegistry getServiceRegistry() {
-	public NamingStrategy getNamingStrategy() {
-	public MappingDefaults getMappingDefaults() {
-	public MetadataImplementor getMetadataImplementor() {
-	public <T> Class<T> locateClassByName(String name) {
-	public Type makeJavaType(String className) {
-	public boolean isGloballyQuotedIdentifiers() {
-	public ValueHolder<Class<?>> makeClassReference(String className) {
-	public String qualifyClassName(String name) {
-	public Index getIndex() {
-	public ClassInfo getClassInfo(String name) {
-	public void resolveAllTypes(String className) {
-	public ResolvedType getResolvedType(Class<?> clazz) {
-	public ResolvedTypeWithMembers resolveMemberTypes(ResolvedType type) {
-public class EntityClass extends ConfiguredClass {
-	public EntityClass(
-	public ColumnValues getDiscriminatorColumnValues() {
-	public FormulaValue getDiscriminatorFormula() {
-	public Class<?> getDiscriminatorType() {
-	public IdType getIdType() {
-	public boolean isExplicitPolymorphism() {
-	public boolean isMutable() {
-	public OptimisticLockStyle getOptimisticLockStyle() {
-	public String getWhereClause() {
-	public String getRowId() {
-	public Caching getCaching() {
-	public TableSource getPrimaryTableSource() {
-	public Set<TableSource> getSecondaryTableSources() {
-	public Set<ConstraintSource> getConstraintSources() {
-	public String getExplicitEntityName() {
-	public String getEntityName() {
-	public boolean isDynamicInsert() {
-	public boolean isDynamicUpdate() {
-	public boolean isSelectBeforeUpdate() {
-	public String getCustomLoaderQueryName() {
-	public CustomSQL getCustomInsert() {
-	public CustomSQL getCustomUpdate() {
-	public CustomSQL getCustomDelete() {
-	public List<String> getSynchronizedTableNames() {
-	public String getCustomPersister() {
-	public boolean isLazy() {
-	public String getProxy() {
-	public int getBatchSize() {
-	public boolean isEntityRoot() {
-	public boolean isDiscriminatorForced() {
-	public boolean isDiscriminatorIncludedInSql() {
-	public String getDiscriminatorMatchValue() {
-	public List<JpaCallbackClass> getJpaCallbacks() {
-		public String getCallbackMethod(Class<?> callbackType) {
-		public String getName() {
-		public boolean isListener() {
-public class EntitySourceImpl implements EntitySource {
-	public EntitySourceImpl(EntityClass entityClass) {
-	public EntityClass getEntityClass() {
-	public Origin getOrigin() {
-	public LocalBindingContext getLocalBindingContext() {
-	public String getEntityName() {
-	public String getClassName() {
-	public String getJpaEntityName() {
-	public TableSource getPrimaryTable() {
-	public boolean isAbstract() {
-	public boolean isLazy() {
-	public String getProxy() {
-	public int getBatchSize() {
-	public boolean isDynamicInsert() {
-	public boolean isDynamicUpdate() {
-	public boolean isSelectBeforeUpdate() {
-	public String getCustomTuplizerClassName() {
-	public String getCustomPersisterClassName() {
-	public String getCustomLoaderName() {
-	public CustomSQL getCustomSqlInsert() {
-	public CustomSQL getCustomSqlUpdate() {
-	public CustomSQL getCustomSqlDelete() {
-	public List<String> getSynchronizedTableNames() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-	public String getPath() {
-	public Iterable<AttributeSource> attributeSources() {
-	public void add(SubclassEntitySource subclassEntitySource) {
-	public Iterable<SubclassEntitySource> subclassEntitySources() {
-	public String getDiscriminatorMatchValue() {
-	public Iterable<ConstraintSource> getConstraints() {
-	public List<JpaCallbackClass> getJpaCallbackClasses() {
-	public Iterable<TableSource> getSecondaryTables() {
-public enum IdType {
-public class RootEntitySourceImpl extends EntitySourceImpl implements RootEntitySource {
-	public RootEntitySourceImpl(EntityClass entityClass) {
-	public IdentifierSource getIdentifierSource() {
-	public SingularAttributeSource getVersioningAttributeSource() {
-	public DiscriminatorSource getDiscriminatorSource() {
-	public EntityMode getEntityMode() {
-	public boolean isMutable() {
-	public boolean isExplicitPolymorphism() {
-	public String getWhere() {
-	public String getRowId() {
-	public OptimisticLockStyle getOptimisticLockStyle() {
-	public Caching getCaching() {
-public class SubclassEntitySourceImpl extends EntitySourceImpl implements SubclassEntitySource {
-	public SubclassEntitySourceImpl(EntityClass entityClass) {
-	public String getExplicitSchemaName() {
-	public String getExplicitCatalogName() {
-	public String getExplicitTableName() {
-	public String getLogicalName() {
-	public boolean equals(Object o) {
-	public int hashCode() {
-	public String toString() {
-	public String name() {
-	public String getTableName() {
-	public Iterable<String> columnNames() {
-	public boolean equals(Object o) {
-	public int hashCode() {
-	public String toString() {
-public class FetchProfileBinder {
-	public static void bind(AnnotationBindingContext bindingContext) {
-public class FilterDefBinder {
-	public static void bind(AnnotationBindingContext bindingContext) {
-public class IdGeneratorBinder {
-	public static void bind(AnnotationBindingContext bindingContext) {
-public class QueryBinder {
-	public static void bind(AnnotationBindingContext bindingContext) {
-public class TableBinder {
-	public static void bind(AnnotationBindingContext bindingContext) {
-public class TypeDefBinder {
-	public static void bind(AnnotationBindingContext bindingContext) {
-public interface PseudoJpaDotNames {
-	public void beforePush(IndexBuilder indexBuilder, DotName classDotName, AnnotationInstance annotationInstance) {
-	public static ExclusiveAnnotationFilter INSTANCE = new ExclusiveAnnotationFilter();
-		public Set<DotName> getNames() {
-		public Iterator iterator() {
-public interface IndexedAnnotationFilter extends JPADotNames {
-	public static NameTargetAnnotationFilter INSTANCE = new NameTargetAnnotationFilter();
-	public static NameTargetAnnotationFilter INSTANCE = new NameTargetAnnotationFilter();
-		public boolean process(AnnotationInstance annotationInstance) {
-		public boolean process(AnnotationInstance annotationInstance) {
-		public boolean process(AnnotationInstance annotationInstance) {
-		public String getClazz() {
-		public void setClazz(String className) {
-		public Boolean isMetadataComplete() {
-		public void setMetadataComplete(Boolean isMetadataComplete) {
-		public String getClazz() {
-		public void setClazz(String className) {
-		public Boolean isMetadataComplete() {
-		public void setMetadataComplete(Boolean isMetadataComplete) {
-		public String getClazz() {
-		public void setClazz(String className) {
-		public Boolean isMetadataComplete() {
-		public void setMetadataComplete(Boolean isMetadataComplete) {
-public class EntityMappingsMocker {
-	public EntityMappingsMocker(List<JaxbEntityMappings> entityMappingsList, Index index, ServiceRegistry serviceRegistry) {
-	public Index mockNewIndex() {
-	public static class Default implements Serializable {
-		public JaxbAccessType getAccess() {
-		public String getCatalog() {
-		public String getPackageName() {
-		public String getSchema() {
-		public Boolean isMetadataComplete() {
-		public Boolean isCascadePersist() {
-	public void filterIndexedAnnotations() {
-public class IndexBuilder {
-	public Map<DotName, List<AnnotationInstance>> getIndexedAnnotations(DotName name) {
-	public Map<DotName, List<AnnotationInstance>> getClassInfoAnnotationsMap(DotName name) {
-	public ClassInfo getClassInfo(DotName name) {
-	public ClassInfo getIndexedClassInfo(DotName name) {
-public class MockHelper {
-	public static AnnotationValue[] toArray(List<AnnotationValue> list) {
-	public static void addToCollectionIfNotNull(Collection collection, Object value) {
-	public static boolean targetEquals(AnnotationTarget t1, AnnotationTarget t2) {
-	public static boolean isNotEmpty(Collection collection) {
-		public String getCatalog() {
-		public String getSchema() {
-		public void setSchema(String schema) {
-		public void setCatalog(String catalog) {
-		public TableSchemaAware(JaxbTable table) {
-		public String getCatalog() {
-		public String getSchema() {
-		public void setSchema(String schema) {
-		public void setCatalog(String catalog) {
-		public JoinTableSchemaAware(JaxbJoinTable table) {
-		public String getCatalog() {
-		public String getSchema() {
-		public void setSchema(String schema) {
-		public void setCatalog(String catalog) {
-		public CollectionTableSchemaAware(JaxbCollectionTable table) {
-		public String getCatalog() {
-		public String getSchema() {
-		public void setSchema(String schema) {
-		public void setCatalog(String catalog) {
-public interface AssociationAttributeSource extends AttributeSource {
-	public Iterable<CascadeStyle> getCascadeStyles();
-	public FetchMode getFetchMode();
-	public FetchTiming getFetchTiming();
-	public FetchStyle getFetchStyle();
-public interface AttributeSource {
-	public String getName();
-	public boolean isSingular();
-	public ExplicitHibernateTypeSource getTypeInformation();
-	public String getPropertyAccessorName();
-	public boolean isIncludedInOptimisticLocking();
-	public Iterable<MetaAttributeSource> metaAttributes();
-public interface AttributeSourceContainer {
-	public String getPath();
-	public Iterable<AttributeSource> attributeSources();
-	public LocalBindingContext getLocalBindingContext();
-public interface BasicPluralAttributeElementSource extends PluralAttributeElementSource {
-	public List<RelationalValueSource> getValueSources();
-	public ExplicitHibernateTypeSource getExplicitHibernateTypeSource();
-public class Binder {
-	public Binder(MetadataImplementor metadata, List<String> processedEntityNames) {
-	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
-		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
-		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
-public interface ColumnSource extends RelationalValueSource {
-	public String getName();
-	public String getReadFragment();
-	public String getWriteFragment();
-	public boolean isNullable();
-	public String getDefaultValue();
-	public String getSqlType();
-	public Datatype getDatatype();
-	public Size getSize();
-	public boolean isUnique();
-	public String getCheckCondition();
-	public String getComment();
-	public boolean isIncludedInInsert();
-	public boolean isIncludedInUpdate();
-public interface ComponentAttributeSource extends SingularAttributeSource, AttributeSourceContainer {
-	public String getClassName();
-	public ValueHolder<Class<?>> getClassReference();
-	public String getParentReferenceAttributeName();
-	public String getExplicitTuplizerClassName();
-public interface ComponentIdentifierSource extends IdentifierSource {
-    public ComponentAttributeSource getIdentifierAttributeSource();
-public interface CompositePluralAttributeElementSource extends PluralAttributeElementSource, AttributeSourceContainer {
-	public String getClassName();
-	public ValueHolder<Class<?>> getClassReference();
-	public String getParentReferenceAttributeName();
-	public String getExplicitTuplizerClassName();
-public interface ConstraintSource {
-	public String name();
-	public String getTableName();
-public interface DerivedValueSource extends RelationalValueSource {
-	public String getExpression();
-public interface DiscriminatorSource {
-	public RelationalValueSource getDiscriminatorRelationalValueSource();
-	public String getExplicitHibernateTypeName();
-public interface EntityHierarchy {
-	public InheritanceType getHierarchyInheritanceType();
-	public RootEntitySource getRootEntitySource();
-public interface EntitySource extends SubclassEntityContainer, AttributeSourceContainer {
-	public Origin getOrigin();
-	public LocalBindingContext getLocalBindingContext();
-	public String getEntityName();
-	public String getClassName();
-	public String getJpaEntityName();
-	public TableSource getPrimaryTable();
-	public Iterable<TableSource> getSecondaryTables();
-	public String getCustomTuplizerClassName();
-	public String getCustomPersisterClassName();
-	public boolean isLazy();
-	public String getProxy();
-	public int getBatchSize();
-	public boolean isAbstract();
-	public boolean isDynamicInsert();
-	public boolean isDynamicUpdate();
-	public boolean isSelectBeforeUpdate();
-	public String getCustomLoaderName();
-	public CustomSQL getCustomSqlInsert();
-	public CustomSQL getCustomSqlUpdate();
-	public CustomSQL getCustomSqlDelete();
-	public List<String> getSynchronizedTableNames();
-	public Iterable<MetaAttributeSource> metaAttributes();
-	public String getDiscriminatorMatchValue();
-	public Iterable<ConstraintSource> getConstraints();
-public interface ExplicitHibernateTypeSource {
-	public String getName();
-	public Map<String,String> getParameters();
-public interface IdentifierSource {
-    public static enum Nature {
-	public Nature getNature();
-public interface JpaCallbackClass {
-public interface ManyToAnyPluralAttributeElementSource extends PluralAttributeElementSource {
-public interface ManyToManyPluralAttributeElementSource extends PluralAttributeElementSource {
-	public String getReferencedEntityName();
-	public String getReferencedEntityAttributeName();
-	public List<RelationalValueSource> getValueSources(); // these describe the "outgoing" link
-	public boolean isNotFoundAnException();
-	public String getExplicitForeignKeyName();
-	public boolean isUnique();
-	public String getOrderBy();
-	public String getWhere();
-	public FetchMode getFetchMode();
-	public boolean fetchImmediately();
-public interface MetaAttributeSource {
-	public String getName();
-	public String getValue();
-	public boolean isInheritable();
-public interface OneToManyPluralAttributeElementSource extends PluralAttributeElementSource {
-	public String getReferencedEntityName();
-	public boolean isNotFoundAnException();
-public interface Orderable {
-	public boolean isOrdered();
-	public String getOrder();
-public enum PluralAttributeElementNature {
-public interface PluralAttributeElementSource {
-	public PluralAttributeElementNature getNature();
-public interface PluralAttributeKeySource {
-	public List<RelationalValueSource> getValueSources();
-	public String getExplicitForeignKeyName();
-	public ForeignKey.ReferentialAction getOnDeleteAction();
-	public String getReferencedEntityAttributeName();
-public enum PluralAttributeNature {
-	public Class<?> reportedJavaType() {
-public interface PluralAttributeSource extends AssociationAttributeSource {
-	public PluralAttributeNature getPluralAttributeNature();
-	public PluralAttributeKeySource getKeySource();
-	public PluralAttributeElementSource getElementSource();
-	public String getExplicitSchemaName();
-	public String getExplicitCatalogName();
-	public String getExplicitCollectionTableName();
-	public String getCollectionTableComment();
-	public String getCollectionTableCheck();
-	public Caching getCaching();
-	public String getCustomPersisterClassName();
-	public String getWhere();
-	public boolean isInverse();
-	public String getCustomLoaderName();
-	public CustomSQL getCustomSqlInsert();
-	public CustomSQL getCustomSqlUpdate();
-	public CustomSQL getCustomSqlDelete();
-	public CustomSQL getCustomSqlDeleteAll();
-public interface RelationalValueSource {
-	public String getContainingTableName();
-public interface RelationalValueSourceContainer {
-	public boolean areValuesIncludedInInsertByDefault();
-	public boolean areValuesIncludedInUpdateByDefault();
-	public boolean areValuesNullableByDefault();
-	public List<RelationalValueSource> relationalValueSources();
-public interface RootEntitySource extends EntitySource {
-	public IdentifierSource getIdentifierSource();
-	public SingularAttributeSource getVersioningAttributeSource();
-	public DiscriminatorSource getDiscriminatorSource();
-	public EntityMode getEntityMode();
-	public boolean isMutable();
-	public boolean isExplicitPolymorphism();
-	public String getWhere();
-	public String getRowId();
-	public OptimisticLockStyle getOptimisticLockStyle();
-	public Caching getCaching();
-public interface SimpleIdentifierSource extends IdentifierSource {
-	public SingularAttributeSource getIdentifierAttributeSource();
-public enum SingularAttributeNature {
-public interface SingularAttributeSource extends AttributeSource, RelationalValueSourceContainer {
-	public boolean isVirtualAttribute();
-	public SingularAttributeNature getNature();
-	public boolean isInsertable();
-	public boolean isUpdatable();
-	public PropertyGeneration getGeneration();
-	public boolean isLazy();
-public interface Sortable {
-	public boolean isSorted();
-	public String getComparatorName();
-public interface SubclassEntityContainer {
-	public void add(SubclassEntitySource subclassEntitySource);
-	public Iterable<SubclassEntitySource> subclassEntitySources();
-public interface SubclassEntitySource extends EntitySource {
-public interface TableSource {
-	public String getExplicitSchemaName();
-	public String getExplicitCatalogName();
-	public String getExplicitTableName();
-	public String getLogicalName();
-public interface ToOneAttributeSource extends SingularAttributeSource, AssociationAttributeSource {
-	public String getReferencedEntityName();
-	public String getReferencedEntityAttributeName();
-public interface UniqueConstraintSource extends ConstraintSource {
-public abstract class AbstractEntitySourceImpl implements EntitySource {
-	public Origin getOrigin() {
-	public LocalBindingContext getLocalBindingContext() {
-	public String getEntityName() {
-	public String getClassName() {
-	public String getJpaEntityName() {
-	public boolean isAbstract() {
-	public boolean isLazy() {
-	public String getProxy() {
-	public int getBatchSize() {
-	public boolean isDynamicInsert() {
-	public boolean isDynamicUpdate() {
-	public boolean isSelectBeforeUpdate() {
-	public String getCustomTuplizerClassName() {
-	public String getCustomPersisterClassName() {
-	public String getCustomLoaderName() {
-	public CustomSQL getCustomSqlInsert() {
-	public CustomSQL getCustomSqlUpdate() {
-	public CustomSQL getCustomSqlDelete() {
-	public List<String> getSynchronizedTableNames() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-	public String getPath() {
-	public Iterable<AttributeSource> attributeSources() {
-	public void injectHierarchy(EntityHierarchyImpl entityHierarchy) {
-	public void add(SubclassEntitySource subclassEntitySource) {
-	public void add(SubclassEntitySourceImpl subclassEntitySource) {
-	public Iterable<SubclassEntitySource> subclassEntitySources() {
-	public String getDiscriminatorMatchValue() {
-	public Iterable<ConstraintSource> getConstraints() {
-	public Iterable<TableSource> getSecondaryTables() {
-	public List<JpaCallbackClass> getJpaCallbackClasses() {
-public abstract class AbstractPluralAttributeSourceImpl implements PluralAttributeSource {
-			public String getName() {
-			public Map<String, String> getParameters() {
-	public PluralAttributeElement getPluralAttributeElement() {
-	public PluralAttributeKeySource getKeySource() {
-	public PluralAttributeElementSource getElementSource() {
-	public String getExplicitSchemaName() {
-	public String getExplicitCatalogName() {
-	public String getExplicitCollectionTableName() {
-	public String getCollectionTableComment() {
-	public String getCollectionTableCheck() {
-	public Caching getCaching() {
-	public String getWhere() {
-	public String getName() {
-	public boolean isSingular() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getPropertyAccessorName() {
-	public boolean isIncludedInOptimisticLocking() {
-	public boolean isInverse() {
-	public String getCustomPersisterClassName() {
-	public String getCustomLoaderName() {
-	public CustomSQL getCustomSqlInsert() {
-	public CustomSQL getCustomSqlUpdate() {
-	public CustomSQL getCustomSqlDelete() {
-	public CustomSQL getCustomSqlDeleteAll() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-	public Iterable<CascadeStyle> getCascadeStyles() {
-	public FetchTiming getFetchTiming() {
-	public FetchStyle getFetchStyle() {
-	public FetchMode getFetchMode() {
-public class BagAttributeSourceImpl extends AbstractPluralAttributeSourceImpl implements Orderable {
-	public BagAttributeSourceImpl(JaxbBagElement bagElement, AttributeSourceContainer container) {
-	public PluralAttributeNature getPluralAttributeNature() {
-	public JaxbBagElement getPluralAttributeElement() {
-	public boolean isOrdered() {
-	public String getOrder() {
-public class BasicPluralAttributeElementSourceImpl implements BasicPluralAttributeElementSource {
-	public BasicPluralAttributeElementSourceImpl(
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-			public String getName() {
-			public Map<String, String> getParameters() {
-	public PluralAttributeElementNature getNature() {
-	public List<RelationalValueSource> getValueSources() {
-	public ExplicitHibernateTypeSource getExplicitHibernateTypeSource() {
-	public boolean isIncludedInInsert() {
-	public boolean isIncludedInUpdate() {
-	public String getContainingTableName() {
-	public String getName() {
-	public boolean isNullable() {
-	public String getDefaultValue() {
-	public String getSqlType() {
-	public Datatype getDatatype() {
-	public Size getSize() {
-	public String getReadFragment() {
-	public String getWriteFragment() {
-	public boolean isUnique() {
-	public String getCheckCondition() {
-	public String getComment() {
-	public String getName() {
-	public boolean isNullable() {
-	public String getDefaultValue() {
-	public String getSqlType() {
-	public Datatype getDatatype() {
-	public Size getSize() {
-	public String getReadFragment() {
-	public String getWriteFragment() {
-	public boolean isUnique() {
-	public String getCheckCondition() {
-	public String getComment() {
-	public boolean isIncludedInInsert() {
-	public boolean isIncludedInUpdate() {
-	public String getContainingTableName() {
-public class ComponentAttributeSourceImpl implements ComponentAttributeSource {
-	public ComponentAttributeSourceImpl(
-	public String getClassName() {
-	public ValueHolder<Class<?>> getClassReference() {
-	public String getPath() {
-	public LocalBindingContext getLocalBindingContext() {
-	public String getParentReferenceAttributeName() {
-	public String getExplicitTuplizerClassName() {
-	public Iterable<AttributeSource> attributeSources() {
-	public boolean isVirtualAttribute() {
-	public SingularAttributeNature getNature() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getName() {
-	public boolean isSingular() {
-	public String getPropertyAccessorName() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-	public List<RelationalValueSource> relationalValueSources() {
-public class CompositePluralAttributeElementSourceImpl implements CompositePluralAttributeElementSource {
-	public CompositePluralAttributeElementSourceImpl(
-	public PluralAttributeElementNature getNature() {
-	public String getClassName() {
-	public ValueHolder<Class<?>> getClassReference() {
-	public String getParentReferenceAttributeName() {
-	public String getExplicitTuplizerClassName() {
-	public String getPath() {
-	public Iterable<AttributeSource> attributeSources() {
-	public LocalBindingContext getLocalBindingContext() {
-public class EntityHierarchyImpl implements org.hibernate.metamodel.source.binder.EntityHierarchy {
-	public EntityHierarchyImpl(RootEntitySourceImpl rootEntitySource) {
-	public InheritanceType getHierarchyInheritanceType() {
-	public RootEntitySource getRootEntitySource() {
-	public void processSubclass(SubclassEntitySourceImpl subclassEntitySource) {
-	public String getExpression() {
-	public String getContainingTableName() {
-public interface HbmBindingContext extends LocalBindingContext {
-	public boolean isAutoImport();
-	public MetaAttributeContext getMetaAttributeContext();
-	public String determineEntityName(EntityElement entityElement);
-	public void processFetchProfiles(List<JaxbFetchProfileElement> fetchProfiles, String containingEntityName);
-public class HbmMetadataSourceProcessorImpl implements MetadataSourceProcessor {
-	public HbmMetadataSourceProcessorImpl(MetadataImplementor metadata) {
-	public void prepare(MetadataSources sources) {
-	public void processIndependentMetadata(MetadataSources sources) {
-	public void processTypeDependentMetadata(MetadataSources sources) {
-	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
-	public void processMappingDependentMetadata(MetadataSources sources) {
-public class Helper {
-	public static final ExplicitHibernateTypeSource TO_ONE_ATTRIBUTE_TYPE_SOURCE = new ExplicitHibernateTypeSource() {
-		public String getName() {
-		public Map<String, String> getParameters() {
-	public static InheritanceType interpretInheritanceType(EntityElement entityElement) {
-	public static CustomSQL buildCustomSql(CustomSqlElement customSqlElement) {
-	public static String determineEntityName(EntityElement entityElement, String unqualifiedClassPackage) {
-	public static String qualifyIfNeeded(String name, String unqualifiedClassPackage) {
-	public static String getPropertyAccessorName(String access, boolean isEmbedded, String defaultAccess) {
-	public static MetaAttributeContext extractMetaAttributeContext(
-	public static String getStringValue(String value, String defaultValue) {
-	public static int getIntValue(String value, int defaultValue) {
-	public static long getLongValue(String value, long defaultValue) {
-	public static boolean getBooleanValue(Boolean value, boolean defaultValue) {
-	public static Iterable<CascadeStyle> interpretCascadeStyles(String cascades, LocalBindingContext bindingContext) {
-	public static Map<String, String> extractParameters(List<JaxbParamElement> xmlParamElements) {
-	public static Iterable<MetaAttributeSource> buildMetaAttributeSources(List<JaxbMetaElement> metaElements) {
-							public String getName() {
-							public String getValue() {
-							public boolean isInheritable() {
-	public static Schema.Name determineDatabaseSchemaName(
-	public static Identifier resolveIdentifier(String explicitName, String defaultName, boolean globalQuoting) {
-    public static class ValueSourcesAdapter {
-        public String getContainingTableName() {
-        public boolean isIncludedInInsertByDefault() {
-        public boolean isIncludedInUpdateByDefault() {
-        public String getColumnAttribute() {
-        public String getFormulaAttribute() {
-        public List getColumnOrFormulaElements() {
-        public boolean isForceNotNull() {
-    public static List<RelationalValueSource> buildValueSources(
-	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
-public class HibernateMappingProcessor {
-				public ClassLoaderService initialize() {
-	public HibernateMappingProcessor(MetadataImplementor metadata, MappingDocument mappingDocument) {
-	public void processIndependentMetadata() {
-	public void processTypeDependentMetadata() {
-	public void processMappingDependentMetadata() {
-	public void processFetchProfiles(List<JaxbFetchProfileElement> fetchProfiles, String containingEntityName) {
-public class HierarchyBuilder {
-	public void processMappingDocument(MappingDocument mappingDocument) {
-	public List<EntityHierarchyImpl> groupEntityHierarchies() {
-public class ManyToManyPluralAttributeElementSourceImpl implements ManyToManyPluralAttributeElementSource {
-	public ManyToManyPluralAttributeElementSourceImpl(
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-	public PluralAttributeElementNature getNature() {
-	public String getReferencedEntityName() {
-	public String getReferencedEntityAttributeName() {
-	public List<RelationalValueSource> getValueSources() {
-	public boolean isNotFoundAnException() {
-	public String getExplicitForeignKeyName() {
-	public boolean isUnique() {
-	public String getOrderBy() {
-	public String getWhere() {
-	public FetchMode getFetchMode() {
-	public boolean fetchImmediately() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-	public String getName() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getPropertyAccessorName() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public Iterable<CascadeStyle> getCascadeStyles() {
-	public FetchTiming getFetchTiming() {
-	public FetchStyle getFetchStyle() {
-	public FetchMode getFetchMode() {
-	public SingularAttributeNature getNature() {
-	public boolean isVirtualAttribute() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-	public List<RelationalValueSource> relationalValueSources() {
-	public boolean isSingular() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-	public String getReferencedEntityName() {
-	public String getReferencedEntityAttributeName() {
-public class MappingDocument {
-	public MappingDocument(JaxbRoot<JaxbHibernateMapping> hbmJaxbRoot, MetadataImplementor metadata) {
-	public JaxbHibernateMapping getMappingRoot() {
-	public Origin getOrigin() {
-	public JaxbRoot<JaxbHibernateMapping> getJaxbRoot() {
-	public HbmBindingContext getMappingLocalBindingContext() {
-		public ServiceRegistry getServiceRegistry() {
-		public NamingStrategy getNamingStrategy() {
-		public MappingDefaults getMappingDefaults() {
-		public MetadataImplementor getMetadataImplementor() {
-		public <T> Class<T> locateClassByName(String name) {
-		public Type makeJavaType(String className) {
-		public ValueHolder<Class<?>> makeClassReference(String className) {
-		public boolean isAutoImport() {
-		public MetaAttributeContext getMetaAttributeContext() {
-		public Origin getOrigin() {
-		public String qualifyClassName(String unqualifiedName) {
-		public String determineEntityName(EntityElement entityElement) {
-		public boolean isGloballyQuotedIdentifiers() {
-		public void processFetchProfiles(List<JaxbFetchProfileElement> fetchProfiles, String containingEntityName) {
-public class OneToManyPluralAttributeElementSourceImpl implements OneToManyPluralAttributeElementSource {
-	public OneToManyPluralAttributeElementSourceImpl(
-	public PluralAttributeElementNature getNature() {
-	public String getReferencedEntityName() {
-	public boolean isNotFoundAnException() {
-public class PluralAttributeKeySourceImpl implements PluralAttributeKeySource {
-	public PluralAttributeKeySourceImpl(
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-	public List<RelationalValueSource> getValueSources() {
-	public String getExplicitForeignKeyName() {
-	public ForeignKey.ReferentialAction getOnDeleteAction() {
-	public String getReferencedEntityAttributeName() {
-			public String getName() {
-			public Map<String, String> getParameters() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-	public String getName() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getPropertyAccessorName() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public SingularAttributeNature getNature() {
-	public boolean isVirtualAttribute() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-	public List<RelationalValueSource> relationalValueSources() {
-	public boolean isSingular() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-public class RootEntitySourceImpl extends AbstractEntitySourceImpl implements RootEntitySource {
-	public IdentifierSource getIdentifierSource() {
-				public SingularAttributeSource getIdentifierAttributeSource() {
-				public IdGenerator getIdentifierGeneratorDescriptor() {
-				public Nature getNature() {
-	public SingularAttributeSource getVersioningAttributeSource() {
-	public EntityMode getEntityMode() {
-	public boolean isMutable() {
-	public boolean isExplicitPolymorphism() {
-	public String getWhere() {
-	public String getRowId() {
-	public OptimisticLockStyle getOptimisticLockStyle() {
-	public Caching getCaching() {
-	public TableSource getPrimaryTable() {
-			public String getExplicitSchemaName() {
-			public String getExplicitCatalogName() {
-			public String getExplicitTableName() {
-			public String getLogicalName() {
-	public String getDiscriminatorMatchValue() {
-	public DiscriminatorSource getDiscriminatorSource() {
-			public RelationalValueSource getDiscriminatorRelationalValueSource() {
-			public String getExplicitHibernateTypeName() {
-			public boolean isForced() {
-			public boolean isInserted() {
-public class SetAttributeSourceImpl extends AbstractPluralAttributeSourceImpl implements Orderable, Sortable {
-	public SetAttributeSourceImpl(JaxbSetElement setElement, AttributeSourceContainer container) {
-	public JaxbSetElement getPluralAttributeElement() {
-	public PluralAttributeNature getPluralAttributeNature() {
-	public boolean isSorted() {
-	public String getComparatorName() {
-	public boolean isOrdered() {
-	public String getOrder() {
-	public SingularIdentifierAttributeSourceImpl(
-			public String getName() {
-			public Map<String, String> getParameters() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-                    public boolean isForceNotNull() {
-	public String getName() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getPropertyAccessorName() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public SingularAttributeNature getNature() {
-	public boolean isVirtualAttribute() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-	public List<RelationalValueSource> relationalValueSources() {
-	public boolean isSingular() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-public class SubclassEntitySourceImpl extends AbstractEntitySourceImpl implements SubclassEntitySource {
-	public TableSource getPrimaryTable() {
-				public String getExplicitSchemaName() {
-				public String getExplicitCatalogName() {
-				public String getExplicitTableName() {
-				public String getLogicalName() {
-				public String getExplicitSchemaName() {
-				public String getExplicitCatalogName() {
-				public String getExplicitTableName() {
-				public String getLogicalName() {
-	public String getDiscriminatorMatchValue() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-		public String getName() {
-		public Map<String, String> getParameters() {
-	public String getName() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getPropertyAccessorName() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-				public PropertyGeneration initialize() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public SingularAttributeNature getNature() {
-	public boolean isVirtualAttribute() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-	public List<RelationalValueSource> relationalValueSources() {
-	public boolean isSingular() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-					public String getColumnAttribute() {
-					public String getFormulaAttribute() {
-					public List getColumnOrFormulaElements() {
-					public String getContainingTableName() {
-					public boolean isIncludedInInsertByDefault() {
-					public boolean isIncludedInUpdateByDefault() {
-		public String getName() {
-		public Map<String, String> getParameters() {
-	public String getName() {
-	public ExplicitHibernateTypeSource getTypeInformation() {
-	public String getPropertyAccessorName() {
-	public boolean isInsertable() {
-	public boolean isUpdatable() {
-				public PropertyGeneration initialize() {
-	public PropertyGeneration getGeneration() {
-	public boolean isLazy() {
-	public boolean isIncludedInOptimisticLocking() {
-	public SingularAttributeNature getNature() {
-	public boolean isVirtualAttribute() {
-	public boolean areValuesIncludedInInsertByDefault() {
-	public boolean areValuesIncludedInUpdateByDefault() {
-	public boolean areValuesNullableByDefault() {
-	public List<RelationalValueSource> relationalValueSources() {
-	public boolean isSingular() {
-	public Iterable<MetaAttributeSource> metaAttributes() {
-public class IdentifierGeneratorResolver {
-public class JaxbHelper {
-	public static final String ASSUMED_ORM_XSD_VERSION = "2.0";
-	public JaxbHelper(MetadataSources metadataSources) {
-	public JaxbRoot unmarshal(InputStream stream, Origin origin) {
-	public JaxbRoot unmarshal(Document document, Origin origin) {
-	public static final String HBM_SCHEMA_NAME = "org/hibernate/hibernate-mapping-4.0.xsd";
-	public static final String ORM_1_SCHEMA_NAME = "org/hibernate/jpa/orm_1_0.xsd";
-	public static final String ORM_2_SCHEMA_NAME = "org/hibernate/jpa/orm_2_0.xsd";
-		public boolean handleEvent(ValidationEvent validationEvent) {
-		public int getLineNumber() {
-		public int getColumnNumber() {
-		public String getMessage() {
-public class MetadataBuilderImpl implements MetadataBuilder {
-	public MetadataBuilderImpl(MetadataSources sources) {
-	public MetadataBuilderImpl(MetadataSources sources, StandardServiceRegistry serviceRegistry) {
-	public MetadataBuilder with(NamingStrategy namingStrategy) {
-	public MetadataBuilder with(MetadataSourceProcessingOrder metadataSourceProcessingOrder) {
-	public MetadataBuilder with(SharedCacheMode sharedCacheMode) {
-	public MetadataBuilder with(AccessType accessType) {
-	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled) {
-	public Metadata build() {
-	public static class OptionsImpl implements Metadata.Options {
-		public OptionsImpl(StandardServiceRegistry serviceRegistry) {
-						public AccessType convert(Object value) {
-						public Boolean convert(Object value) {
-						public String convert(Object value) {
-						public String convert(Object value) {
-                        public Boolean convert(Object value) {
-		public StandardServiceRegistry getServiceRegistry() {
-		public MetadataSourceProcessingOrder getMetadataSourceProcessingOrder() {
-		public NamingStrategy getNamingStrategy() {
-		public AccessType getDefaultAccessType() {
-		public SharedCacheMode getSharedCacheMode() {
-        public boolean useNewIdentifierGenerators() {
-        public boolean isGloballyQuotedIdentifiers() {
-		public String getDefaultSchemaName() {
-		public String getDefaultCatalogName() {
-public class MetadataImpl implements MetadataImplementor, Serializable {
-	public MetadataImpl(MetadataSources metadataSources, Options options) {
-					public ClassLoaderService initialize() {
-					public PersisterClassResolver initialize() {
-	public void addFetchProfile(FetchProfile profile) {
-	public void addFilterDefinition(FilterDefinition def) {
-	public Iterable<FilterDefinition> getFilterDefinitions() {
-	public void addIdGenerator(IdGenerator generator) {
-	public IdGenerator getIdGenerator(String name) {
-	public void registerIdentifierGenerator(String name, String generatorClassName) {
-	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
-	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
-	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
-	public void addNamedQuery(NamedQueryDefinition def) {
-	public NamedQueryDefinition getNamedQuery(String name) {
-	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions() {
-	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
-	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
-	public void addTypeDefinition(TypeDef typeDef) {
-	public Iterable<TypeDef> getTypeDefinitions() {
-	public TypeDef getTypeDefinition(String name) {
-	public Options getOptions() {
-	public ServiceRegistry getServiceRegistry() {
-	public <T> Class<T> locateClassByName(String name) {
-	public Type makeJavaType(String className) {
-	public ValueHolder<Class<?>> makeClassReference(final String className) {
-					public Class<?> initialize() {
-	public String qualifyClassName(String name) {
-	public Database getDatabase() {
-	public EntityBinding getEntityBinding(String entityName) {
-	public EntityBinding getRootEntityBinding(String entityName) {
-	public Iterable<EntityBinding> getEntityBindings() {
-	public void addEntity(EntityBinding entityBinding) {
-	public PluralAttributeBinding getCollection(String collectionRole) {
-	public Iterable<PluralAttributeBinding> getCollectionBindings() {
-	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
-	public void addImport(String importName, String entityName) {
-	public Iterable<Map.Entry<String, String>> getImports() {
-	public Iterable<FetchProfile> getFetchProfiles() {
-	public TypeResolver getTypeResolver() {
-	public SessionFactoryBuilder getSessionFactoryBuilder() {
-	public SessionFactory buildSessionFactory() {
-	public NamingStrategy getNamingStrategy() {
-    public boolean isGloballyQuotedIdentifiers() {
-    public void setGloballyQuotedIdentifiers(boolean globallyQuotedIdentifiers){
-	public MappingDefaults getMappingDefaults() {
-	public MetaAttributeContext getGlobalMetaAttributeContext() {
-	public MetadataImplementor getMetadataImplementor() {
-	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
-	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
-	public String getIdentifierPropertyName(String entityName) throws MappingException {
-	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
-		public String getPackageName() {
-		public String getSchemaName() {
-		public String getCatalogName() {
-		public String getIdColumnName() {
-		public String getDiscriminatorColumnName() {
-		public String getCascadeStyle() {
-		public String getPropertyAccessorName() {
-		public boolean areAssociationsLazy() {
-					public AccessType initialize() {
-		public AccessType getCacheAccessType() {
-public class OverriddenMappingDefaults implements MappingDefaults {
-	public OverriddenMappingDefaults(
-	public String getPackageName() {
-	public String getSchemaName() {
-	public String getCatalogName() {
-	public String getIdColumnName() {
-	public String getDiscriminatorColumnName() {
-	public String getCascadeStyle() {
-	public String getPropertyAccessorName() {
-	public boolean areAssociationsLazy() {
-	public AccessType getCacheAccessType() {
-public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
-	public SessionFactoryBuilder with(Interceptor interceptor) {
-	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate) {
-	public SessionFactory build() {
-		public SessionFactoryOptionsImpl(StandardServiceRegistry serviceRegistry) {
-				public void handleEntityNotFound(String entityName, Serializable id) {
-		public StandardServiceRegistry getServiceRegistry() {
-		public Interceptor getInterceptor() {
-		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
-	public AbstractEntityPersister(
-	public String getTemplateFromColumn(org.hibernate.metamodel.relational.Column column, SessionFactoryImplementor factory) {
-	public JoinedSubclassEntityPersister(
-	public SingleTableEntityPersister(
-	public UnionSubclassEntityPersister(
-	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
-	public EntityPersister createEntityPersister(EntityBinding metadata,
-	public CollectionPersister createCollectionPersister(
-	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
-	public EntityPersister createEntityPersister(
-	public CollectionPersister createCollectionPersister(
-	public static PropertyAccessor getPropertyAccessor(AttributeBinding property, EntityMode mode) throws MappingException {
-	public void integrate(
-	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
-	public SessionFactoryServiceRegistryImpl(
-	public R initiateService(SessionFactoryImplementor sessionFactory, MetadataImplementor metadata, ServiceRegistryImplementor registry);
-	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
-	public StatisticsImplementor initiateService(
-	public SchemaExport(MetadataImplementor metadata) {
-	public DynamicMapInstantiator(EntityBinding mappingInfo) {
-	public PojoInstantiator(EntityBinding entityBinding, ReflectionOptimizer.InstantiationOptimizer optimizer) {
-	public static IdentifierProperty buildIdentifierProperty(
-	public static VersionProperty buildVersionProperty(
-	public static StandardProperty buildStandardProperty(AttributeBinding property, boolean lazyAvailable) {
-	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappingInfo) {
-	public EntityMetamodel(
-	public static final Class[] ENTITY_TUP_CTOR_SIG_NEW = new Class[] { EntityMetamodel.class, EntityBinding.class };
-	public EntityTuplizer constructTuplizer(
-	public EntityTuplizer constructTuplizer(
-	public EntityTuplizer constructDefaultTuplizer(
-	public PojoEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
-public abstract class AbstractBasicBindingTests extends BaseUnitTestCase {
-	public void setUp() {
-	public void tearDown() {
-	public void testSimpleEntityMapping() {
-	public void testSimpleVersionedEntityMapping() {
-	public void testEntityWithManyToOneMapping() {
-	public void testSimpleEntityWithSimpleComponentMapping() {
-	public abstract void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
-	public abstract void addSourcesForSimpleEntityBinding(MetadataSources sources);
-	public abstract void addSourcesForManyToOne(MetadataSources sources);
-	public abstract void addSourcesForComponentBinding(MetadataSources sources);
-public class BasicAnnotationBindingTests extends AbstractBasicBindingTests {
-	public void addSourcesForSimpleEntityBinding(MetadataSources sources) {
-	public void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
-	public void addSourcesForManyToOne(MetadataSources sources) {
-	public void addSourcesForComponentBinding(MetadataSources sources) {
-public class BasicCollectionBindingTests extends BaseUnitTestCase {
-	public void setUp() {
-	public void tearDown() {
-//	public void testAnnotations() {
-	public void testHbm() {
-public class BasicHbmBindingTests extends AbstractBasicBindingTests {
-	public void addSourcesForSimpleEntityBinding(MetadataSources sources) {
-	public void addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
-	public void addSourcesForManyToOne(MetadataSources sources) {
-	public void addSourcesForComponentBinding(MetadataSources sources) {
-	public void testSimpleEntityWithSimpleComponentMapping() {
-public class EntityWithBasicCollections {
-	public EntityWithBasicCollections() {
-	public EntityWithBasicCollections(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public Collection<String> getTheBag() {
-	public void setTheBag(Collection<String> theBag) {
-	public Set<String> getTheSet() {
-	public void setTheSet(Set<String> theSet) {
-public class ManyToOneEntity {
-	public ManyToOneEntity() {
-	public ManyToOneEntity(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public SimpleEntity getSimpleEntity() {
-	public void setSimpleEntity(SimpleEntity simpleEntity) {
-	public String toString() {
-public class SimpleEntity {
-	public SimpleEntity() {
-	public SimpleEntity(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-public class SimpleEntitySubClass extends SimpleEntity {
-	public SimpleEntitySubClass() {
-public class SimpleEntityWithSimpleComponent {
-	public SimpleEntityWithSimpleComponent() {
-	public SimpleEntityWithSimpleComponent(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public SimpleComponent getSimpleComponent() {
-	public void setSimpleComponent(SimpleComponent simpleComponent) {
-	public static class SimpleComponent {
-		public String getValue1() {
-		public void setValue1(String value1) {
-		public String getValue2() {
-		public void setValue2(String value2) {
-public class SimpleValueBindingTests extends BaseUnitTestCase {
-	public static final Datatype BIGINT = new Datatype( Types.BIGINT, "BIGINT", Long.class );
-	public static final Datatype VARCHAR = new Datatype( Types.VARCHAR, "VARCHAR", String.class );
-	public void testBasicMiddleOutBuilding() {
-					public Class<?> initialize() {
-public class SimpleVersionedEntity {
-	public SimpleVersionedEntity() {
-	public SimpleVersionedEntity(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public long getVersion() {
-	public void setVersion(long version) {
-public class ObjectNameTests extends BaseUnitTestCase {
-	public void testMissingName() {
-	public void testIdentifierBuilding() {
-public class TableManipulationTests extends BaseUnitTestCase {
-	public static final Datatype VARCHAR = new Datatype( Types.VARCHAR, "VARCHAR", String.class );
-	public static final Datatype INTEGER = new Datatype( Types.INTEGER, "INTEGER", Long.class );
-	public void testTableCreation() {
-	public void testTableSpecificationCounter() {
-	public void testBasicForeignKeyDefinition() {
-	public void testQualifiedName() {
-public class AccessBindingTest extends BaseAnnotationBindingTestCase {
-	public void testDefaultFieldAccess() {
-		public int getId() {
-	public void testDefaultPropertyAccess() {
-		public int getId() {
-	public void testNoAccess() {
-		public String getName() {
-	public void testMixedAccess() {
-		public String getName() {
-	public void testExplicitClassConfiguredAccess() {
-public abstract class BaseAnnotationBindingTestCase extends BaseUnitTestCase {
-	public MethodRule buildMetaData = new MethodRule() {
-		public Statement apply(final Statement statement, FrameworkMethod frameworkMethod, Object o) {
-	public void tearDown() {
-	public EntityBinding getEntityBinding(Class<?> clazz) {
-	public EntityBinding getRootEntityBinding(Class<?> clazz) {
-		public void evaluate() throws Throwable {
-public class BatchSizeBindingTest extends BaseAnnotationBindingTestCase {
-	public void testNoBatchSize() {
-	public void testBatchSize() {
-public class CacheBindingTest extends BaseAnnotationBindingTestCase {
-	public void testHibernateCaching() {
-	public void testJpaCaching() {
-	public void testNoCaching() {
-public class CustomSQLBindingTest extends BaseAnnotationBindingTestCase {
-	public void testNoCustomSqlAnnotations() {
-	public void testCustomSqlAnnotations() {
-//	public void testDeleteAllWins() {
-public class EmbeddableBindingTest extends BaseAnnotationBindingTestCase {
-	public void testEmbeddable() {
-	public void testEmbeddableWithAttributeOverride() {
-	public class Address {
-	public class Zipcode {
-	public class Customer {
-	public void testNestedEmbeddable() {
-	public class A {
-		public B getB() {
-	public class B {
-		public String getFoo() {
-		public String getFubar() {
-	public class C {
-		public int getId() {
-		public A getA() {
-	public void testAttributeOverrideInEmbeddable() {
-	public class EmbeddableEntity {
-	public class MainEntity {
-	public void testParentReferencingAttributeName() {
-	public interface Car {
-	public class CarImpl implements Car {
-		public int getHorsePower() {
-	public class Owner {
-		public int getId() {
-		public Car getCar() {
-	public void testTargetAnnotationWithEmbeddable() {
-public class EmbeddedIdTest extends BaseAnnotationBindingTestCase {
-    public void testEmbeddable() {
-public class EnumeratedBindingTest extends BaseAnnotationBindingTestCase {
-	public void testEnumeratedTypeAttribute() {
-public class IdentifierGeneratorTest extends BaseAnnotationBindingTestCase {
-	public void testNoIdGeneration() {
-		public long getId() {
-	public void testAutoGenerationType() {
-		public long getId() {
-	public void testTableGenerationType() {
-		public long getId() {
-	public void testSequenceGenerationType() {
-		public long getId() {
-	public void testUndefinedGenerator() {
-		public long getId() {
-	public void testNamedGenerator() {
-public class InheritanceBindingTest extends BaseAnnotationBindingTestCase {
-	public void testNoInheritance() {
-	public void testDiscriminatorValue() {
-	public void testSubclassEntitySuperType() {
-	public void testRootEntitySuperType() {
-	public void testRootEntityBinding() {
-	public void testNoPolymorphism() {
-	public void testRootPolymporhism() {
-	public void testPreOrderRootSubEntityClosure() {
-	public void testPostOrderRootSubEntityClosure() {
-	public void testLeafSubclassOfRoot() {
-	public void testNonLeafSubclassOfRootPolymporhism() {
-	public void testLeafSubclassOfSubclassOfRootPolymporhism() {
-	public void testDefaultDiscriminatorOptions() {
-	public void testExplicitDiscriminatorOptions() {
-	public void testRootDiscriminatorMatchValue() {
-    public void testDiscriminatorFormula() {
-	public class SubclassOfSingleTableInheritance extends RootOfSingleTableInheritance {
-	public class OtherSubclassOfSingleTableInheritance extends RootOfSingleTableInheritance {
-	public class SubclassOfSubclassOfSingleTableInheritance extends SubclassOfSingleTableInheritance {
-public class LobBindingTests extends BaseAnnotationBindingTestCase {
-    public void testClobWithLobAnnotation() {
-    public void testBlobWithLobAnnotation() {
-    public void testStringWithLobAnnotation() {
-    public void testCharacterArrayWithLobAnnotation() {
-    public void testPrimitiveCharacterArrayWithLobAnnotation() {
-    public void testByteArrayWithLobAnnotation() {
-    public void testPrimitiveByteArrayWithLobAnnotation() {
-    public void testSerializableWithLobAnnotation() {
-    public void testNoLobAttribute() {
-public class MappedSuperclassTest extends BaseAnnotationBindingTestCase {
-	public void testSimpleAttributeOverrideInMappedSuperclass() {
-	public void testLastAttributeOverrideWins() {
-	public void testNonEntityBaseClass() {
-public class MapsIdTest extends BaseAnnotationBindingTestCase {
-	public class Employee {
-	public class DependentId {
-	public class Dependent {
-	public void testMapsIsOnOneToManyThrowsException() {
-public class ProxyBindingTest extends BaseAnnotationBindingTestCase {
-	public void testProxyNoAttributes() {
-	public void testNoProxy() {
-	public void testProxyDisabled() {
-	public void testProxyInterface() {
-public class QuotedIdentifierTest extends BaseAnnotationBindingTestCase {
-	public void testDelimitedIdentifiers() {
-public @interface Resources {
-public class RowIdBindingTests extends BaseAnnotationBindingTestCase {
-	public void testNoRowId() {
-	public void testRowId() {
-public class SecondaryTableTest extends BaseAnnotationBindingTestCase {
-	public void testSecondaryTableExists() {
-	public void testRetrievingUnknownTable() {
-public class SynchronizeBindingTest extends BaseAnnotationBindingTestCase {
-	public void testSynchronizeAnnotation() {
-	public void testNoSynchronizeAnnotation() {
-public class TableNameTest extends BaseAnnotationBindingTestCase {
-	public void testSingleInheritanceDefaultTableName() {
-	public void testJoinedSubclassDefaultTableName() {
-	public void testTablePerClassDefaultTableName() {
-public class TemporalBindingTest extends BaseAnnotationBindingTestCase {
-    public void testNoTemporalAnnotationOnTemporalTypeAttribute() {
-    public void testTemporalTypeAttribute() {
-    public void testTemporalTypeAsId() {
-public class UniqueConstraintBindingTest extends BaseAnnotationBindingTestCase {
-	public void testTableUniqueConstraints() {
-public class WhereClauseTest extends BaseAnnotationBindingTestCase {
-	public void testWhereFilter() {
-public class FetchProfileBinderTest extends BaseUnitTestCase {
-	public void setUp() {
-	public void tearDown() {
-	public void testSingleFetchProfile() {
-	public void testFetchProfiles() {
-	public void testNonJoinFetchThrowsException() {
-public class QueryBinderTest extends BaseUnitTestCase {
-	public void setUp() {
-	public void tearDown() {
-	public void testNoResultClass() {
-	public void testResultClass() {
-public abstract class BaseAnnotationIndexTestCase extends BaseUnitTestCase {
-	public void setUp() {
-	public void tearDown() {
-	public Set<EntityHierarchy> createEntityHierarchies(Class<?>... clazz) {
-	public EmbeddableHierarchy createEmbeddableHierarchy(AccessType accessType, Class<?>... configuredClasses) {
-public class EmbeddableHierarchyTest extends BaseAnnotationIndexTestCase {
-	public void testEmbeddableHierarchy() {
-	public void testEmbeddableHierarchyWithNotAnnotatedEntity() {
-	public class Foo {
-	public class A {
-	public class B extends A {
-public class EntityHierarchyTest extends BaseAnnotationIndexTestCase {
-	public void testSingleEntity() {
-	public void testSimpleInheritance() {
-	public void testMultipleHierarchies() {
-	public void testMappedSuperClass() {
-	public void testEntityAndMappedSuperClassAnnotations() {
-	public void testEntityAndEmbeddableAnnotations() {
-	public void testNoIdAnnotation() {
-	public void testDefaultInheritanceStrategy() {
-	public void testExplicitInheritanceStrategy() {
-	public void testMultipleConflictingInheritanceDefinitions() {
-public class GenericTypeDiscoveryTest extends BaseAnnotationIndexTestCase {
-	public void testGenericClassHierarchy() {
-	public void testUnresolvedType() {
-	public class Stuff<Value> {
-		public Value getValue() {
-		public void setValue(Value value) {
-	public class PricedStuff extends Stuff<Price> {
-	public class Item<Type, Owner> extends PricedStuff {
-		public Integer getId() {
-		public void setId(Integer id) {
-		public String getName() {
-		public void setName(String name) {
-		public Type getType() {
-		public void setType(Type type) {
-		public Owner getOwner() {
-		public void setOwner(Owner owner) {
-	public class Paper extends Item<PaperType, SomeGuy> {
-	public class PaperType {
-		public Integer getId() {
-		public void setId(Integer id) {
-		public String getName() {
-		public void setName(String name) {
-	public class Price {
-		public Integer getId() {
-		public void setId(Integer id) {
-		public Double getAmount() {
-		public void setAmount(Double amount) {
-		public String getCurrency() {
-		public void setCurrency(String currency) {
-	public class SomeGuy {
-		public Integer getId() {
-		public void setId(Integer id) {
-	public class UnresolvedType<T> {
-		public Integer getId() {
-		public void setId(Integer id) {
-		public T getState() {
-		public void setState(T state) {
-public class JandexHelperTest extends BaseUnitTestCase {
-	public void setUp() {
-	public void tearDown() {
-	public void testGetMemberAnnotations() {
-	public void testGettingNestedAnnotation() {
-	public void testTryingToRetrieveWrongType() {
-	public void testRetrieveDefaultEnumElement() {
-	public void testRetrieveExplicitEnumElement() {
-	public void testRetrieveStringArray() {
-	public void testRetrieveClassParameterAsClass() {
-	public void testRetrieveClassParameterAsString() {
-	public void testRetrieveUnknownParameter() {
-	public void testPrimitiveAnnotationAttributeTypes() {
-			public String convertToDatabaseColumn(URL attribute) {
-			public URL convertToEntityAttribute(String dbData) {
-public class TypeDiscoveryTest extends BaseAnnotationIndexTestCase {
-	public void testImplicitAndExplicitType() {
-public class Father {
-	public int getId() {
-	public void setId(int id) {
-	public String getName() {
-	public void setName(String name) {
-public class OrmXmlParserTests extends BaseUnitTestCase {
-	public void testSimpleOrmVersion2() {
-	public void testSimpleOrmVersion1() {
-	public void testInvalidOrmXmlThrowsException() {
-public class Star {
-public abstract class AbstractMockerTest {
-public class Author {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public List<Book> getBooks() {
-	public void setBooks(List<Book> books) {
-public class BasicMockerTest extends AbstractMockerTest {
-	public void testEntity() {
-	public void testEntityWithEntityMappingsConfiguration() {
-					public void check(AnnotationInstance annotationInstance) {
-public class Book {
-	public List<Topic> getTopics() {
-	public void setTopics(List<Topic> topics) {
-	public Author getAuthor() {
-	public void setAuthor(Author author) {
-	public Long getId() {
-	public void setId(Long id) {
-	public Date getPublishDate() {
-	public void setPublishDate(Date publishDate) {
-	public Long getVersion() {
-	public void setVersion(Long version) {
-public class DefaultConfigurationHelperTest extends AbstractMockerTest {
-	public void applyNullDefaultToEntity() {
-	public void applyDefaultToEntity() {
-	public void testDefaultCascadePersist() {
-	public void testDefaultSchemaToAnnotationInstance() {
-public class EntityListenerTest extends AbstractMockerTest {
-	public void basicEntityListenerMockTest() {
-public class IndexBuilderTest extends AbstractMockerTest {
-	public void test() {
-public class Item {
-	public Long getId() {
-	public void setId(Long id) {
-public class ItemListener {
-	public void prePersist(){}
-	public void postPersist(){}
-public class OverrideTest extends AbstractMockerTest {
-	public void testPersistenceUnitMetadataMetadataComplete() {
-	public void testEntityMetadataComplete() {
-	public void testOverrideToMappedSuperClass() {
-	public void testPersistenceUnitDefaultsCascadePersistInAnnotation() {
-	public void testPersistenceUnitDefaultsCascadePersistInXML() {
-		public CascadeAnnotationValueChecker(String... expected) {
-		public void check(AnnotationInstance annotationInstance) {
-	public void testAttributeOverride() {
-					public void check(AnnotationInstance annotationInstance) {
-	public void testSchemaInPersistenceMetadata() {
-	public void testSchemaInEntityMapping() {
-public class PersistenceMetadataMockerTest extends AbstractMockerTest {
-	public void testPersistenceMetadata() {
-			public void check(AnnotationInstance annotationInstance) {
-public class Topic {
-	public int getPosition() {
-	public void setPosition(int position) {
-	public String getSummary() {
-	public void setSummary(String summary) {
-	public String getTitle() {
-	public void setTitle(String title) {
-public class XmlHelper {
-    public static <T> JaxbRoot<T> unmarshallXml(String fileName, String schemaName, Class<T> clazz, ClassLoaderService classLoaderService)
-public class Foo {
-public class MetadataImplTest extends BaseUnitTestCase {
-	public void testAddingNullClass() {
-	public void testAddingNullPackageName() {
-	public void testAddingNonExistingPackageName() {
-	public void testAddingPackageName() {
-	public void testAddingPackageNameWithTrailingDot() {
-	public void testGettingSessionFactoryBuilder() {
-public class SessionFactoryBuilderImplTest extends BaseUnitTestCase {
-	public void testGettingSessionFactoryBuilder() {
-	public void testBuildSessionFactoryWithDefaultOptions() {
-	public void testBuildSessionFactoryWithUpdatedOptions() {
-			public void handleEntityNotFound(String entityName, Serializable id) {
-		public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
-		public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types)
-		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
-		public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
-		public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
-		public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
-		public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
-		public void preFlush(Iterator entities) throws CallbackException {
-		public void postFlush(Iterator entities) throws CallbackException {
-		public Boolean isTransient(Object entity) {
-		public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
-		public Object instantiate(String entityName, EntityMode entityMode, Serializable id)
-		public String getEntityName(Object object) throws CallbackException {
-		public Object getEntity(String entityName, Serializable id) throws CallbackException {
-		public void afterTransactionBegin(Transaction tx) {
-		public void beforeTransactionCompletion(Transaction tx) {
-		public void afterTransactionCompletion(Transaction tx) {
-		public String onPrepareStatement(String sql) {
-public class SimpleEntity {
-	public SimpleEntity() {
-	public SimpleEntity(String name) {
-	public Long getId() {
-	public void setId(Long id) {
-	public String getName() {
-	public void setName(String name) {
-	public void integrate(MetadataImplementor metadata,
-	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
-	public MyEntityTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
-		public void integrate(
-				    public void integrate( MetadataImplementor metadata,
-					public void integrate( MetadataImplementor metadata,
-					public void integrate(
-					public void integrate(
-					public void integrate(
-					public void integrate(
-					public void integrate(
-				    public void integrate( MetadataImplementor metadata,
-	public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory,
+	public static AnnotationInstance getSingleAnnotation(
-public class CallbackProcessorImpl implements CallbackProcessor {
-	public CallbackProcessorImpl(
-	public void processCallbacksForEntity(Object entityObject, CallbackRegistryImpl callbackRegistry) {
-	public void release() {
-	public void integrate(
-            public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
-		public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
-		public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
-	public void integrate(
-	public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
-	public static final String USE_NEW_METADATA_MAPPINGS = "hibernate.test.new_metadata_mappings";
-					public Boolean convert(Object value) {
+	public static final String RESOURCE_SEPARATOR = "/";

Lines added containing method: 2. Lines removed containing method: 2669. Tot = 2671
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
Method found in diff:	-		public String getDefaultCatalogName();
-		public String getDefaultCatalogName();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public boolean isLazy() {
-	public boolean isLazy() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-		public String getDefaultSchemaName();
-		public String getDefaultSchemaName();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public TableSpecification getTable() {
-	public TableSpecification getTable() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	private static Type getType(String className, ServiceRegistry serviceRegistry) {
-	private static Type getType(String className, ServiceRegistry serviceRegistry) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public boolean isPolymorphic() {
-	public boolean isPolymorphic() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public String objectToSQLString(Object value, Dialect dialect) throws Exception {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	protected int getColumnSpan() {
-	protected int getColumnSpan() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public static Dialect getDialect() throws HibernateException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    public String getName() {
-    public String getName() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public boolean isCascadeDeleteEnabled() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public boolean isOptional() {
-	public boolean isOptional() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public String getAlias(Dialect dialect) {
-	public String getAlias(Dialect dialect) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public SQLFunctionRegistry getSqlFunctionRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Settings getSettings() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object stringToObject(String xml) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	public String getEntityName() {
-	public String getEntityName() {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	-	public boolean isInverse() {
-	public boolean isInverse() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
