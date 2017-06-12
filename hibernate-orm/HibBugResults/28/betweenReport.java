28/report.java
Satd-method: public MetadataImpl(MetadataSources metadataSources, Options options) {
********************************************
********************************************
28/Between/ HHH-6091  7c39b19a_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6091  88a7edbd_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		Dialect dialect = metadataSources.getServiceRegistry().getService( JdbcServices.class ).getDialect();
-		this.serviceRegistry = metadataSources.getServiceRegistry();
+		this.serviceRegistry =  metadataSources.getServiceRegistry();
-		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory( dialect );
+		this.identifierGeneratorFactory = serviceRegistry.getService( MutableIdentifierGeneratorFactory.class );
+				//new DefaultIdentifierGeneratorFactory( dialect );

Lines added: 3. Lines removed: 3. Tot = 6
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6110  814b5149_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public SourceProcessingOrder getSourceProcessingOrder();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6110  ba44ae26_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public SourceProcessingOrder getSourceProcessingOrder();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6229  98e7f953_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6257  f90f224f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+		Dialect dialect = metadataSources.getServiceRegistry().getService( JdbcServices.class ).getDialect();
+		this.identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory( dialect );
+		// IdentifierGeneratorResolver.resolve() must execute after AttributeTypeResolver.resolve()
+		new IdentifierGeneratorResolver( this ).resolve();

Lines added: 4. Lines removed: 0. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6278  ce477662_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public SourceProcessingOrder getSourceProcessingOrder();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6340  159f6205_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6371  18215076_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		final Binder[] binders;
+		final SourceProcessor[] sourceProcessors;
-			binders = new Binder[] {
-					new HbmBinder( this ),
-					new AnnotationBinder( this )
+			sourceProcessors = new SourceProcessor[] {
+					new HbmSourceProcessor( this ),
+					new AnnotationSourceProcessor( this )
-			binders = new Binder[] {
-					new AnnotationBinder( this ),
-					new HbmBinder( this )
+			sourceProcessors = new SourceProcessor[] {
+					new AnnotationSourceProcessor( this ),
+					new HbmSourceProcessor( this )
+		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
+				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
+					@Override
+					public ClassLoaderService initialize() {
+						return serviceRegistry.getService( ClassLoaderService.class );
+					}
+				}
+		);
+		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
+				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
+					@Override
+					public PersisterClassResolver initialize() {
+						return serviceRegistry.getService( PersisterClassResolver.class );
+					}
+				}
+		);
+
+
-		prepare( binders, metadataSources );
-		bindIndependentMetadata( binders, metadataSources );
-		bindTypeDependentMetadata( binders, metadataSources );
-		bindMappingMetadata( binders, metadataSources, processedEntityNames );
-		bindMappingDependentMetadata( binders, metadataSources );
+		prepare( sourceProcessors, metadataSources );
+		bindIndependentMetadata( sourceProcessors, metadataSources );
+		bindTypeDependentMetadata( sourceProcessors, metadataSources );
+		bindMappingMetadata( sourceProcessors, metadataSources, processedEntityNames );
+		bindMappingDependentMetadata( sourceProcessors, metadataSources );

Lines added: 30. Lines removed: 12. Tot = 42
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6371  24edf42c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6371  3f31aa8f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-					new AnnotationsSourceProcessor( this )
+					new AnnotationProcessor( this )
-					new AnnotationsSourceProcessor( this ),
+					new AnnotationProcessor( this ),

Lines added: 2. Lines removed: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	+	public ServiceRegistry getServiceRegistry() {
+	public ServiceRegistry getServiceRegistry() {
+		return getMetadataImplementor().getServiceRegistry();
+	}

Lines added: 3. Lines removed: 0. Tot = 3
********************************************
********************************************
28/Between/ HHH-6371  594f689d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-					new HbmSourceProcessor( this ),
+					new HbmSourceProcessorImpl( this ),
-					new HbmSourceProcessor( this )
+					new HbmSourceProcessorImpl( this )

Lines added: 2. Lines removed: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public SourceProcessingOrder getSourceProcessingOrder() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void resolve(EntityReferencingAttributeBinding attributeBinding) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6371  8c28d46b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6371  c97075c3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6371  e339dac9_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public SourceProcessingOrder getSourceProcessingOrder() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void resolve(EntityReferencingAttributeBinding attributeBinding) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6372  919cdf7c_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+		new AttributeTypeResolver( this ).resolve();

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	+	private void resolve(AttributeBinding attributeBinding) {
+	private void resolve(AttributeBinding attributeBinding) {
+		if ( attributeBinding.getHibernateTypeDescriptor().getExplicitType() != null ) {
+			return; // already resolved
+		}
+
+		// this only works for "basic" attribute types
+		HibernateTypeDescriptor typeDescriptor = attributeBinding.getHibernateTypeDescriptor();
+		if ( typeDescriptor == null || typeDescriptor.getTypeName() == null) {
+			throw new MappingException( "Hibernate type name has not been defined for attribute: " +
+					getQualifiedAttributeName( attributeBinding )
+			);
+		}
+		if ( typeDescriptor.getTypeName() != null ) {
+			Properties typeParameters = null;
+			if ( typeDescriptor.getTypeParameters() != null ) {
+				typeParameters = new Properties();
+				typeParameters.putAll( typeDescriptor.getTypeParameters() );
+			}
+			typeDescriptor.setExplicitType(
+					metadata.getTypeResolver().heuristicType(
+							typeDescriptor.getTypeName(),
+							typeParameters
+					)
+			);
+		}
+	}

Lines added: 26. Lines removed: 0. Tot = 26
********************************************
********************************************
28/Between/ HHH-6416  30843f20_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6437  9972c7ec_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

+		this.database = new Database( options );

Lines added: 1. Lines removed: 0. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6447  4968ad11_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		new EntityReferenceResolver( this ).resolve();
+		new AssociationResolver( this ).resolve();

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	private void resolve(EntityReferencingAttributeBinding attributeBinding) {
-	private void resolve(EntityReferencingAttributeBinding attributeBinding) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
28/Between/ HHH-6447  acc93a3d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6447  c5b013d3_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6447  c7421837_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		final SourceProcessor[] sourceProcessors;
-		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
-			sourceProcessors = new SourceProcessor[] {
-					new HbmSourceProcessorImpl( this ),
-					new AnnotationProcessor( this )
+		final MetadataSourceProcessor[] metadataSourceProcessors;
+		if ( options.getMetadataSourceProcessingOrder() == MetadataSourceProcessingOrder.HBM_FIRST ) {
+			metadataSourceProcessors = new MetadataSourceProcessor[] {
+					new HbmMetadataSourceProcessorImpl( this ),
+					new AnnotationMetadataSourceProcessorImpl( this )
-			sourceProcessors = new SourceProcessor[] {
-					new AnnotationProcessor( this ),
-					new HbmSourceProcessorImpl( this )
+			metadataSourceProcessors = new MetadataSourceProcessor[] {
+					new AnnotationMetadataSourceProcessorImpl( this ),
+					new HbmMetadataSourceProcessorImpl( this )
-		prepare( sourceProcessors, metadataSources );
-		bindIndependentMetadata( sourceProcessors, metadataSources );
-		bindTypeDependentMetadata( sourceProcessors, metadataSources );
-		bindMappingMetadata( sourceProcessors, metadataSources, processedEntityNames );
-		bindMappingDependentMetadata( sourceProcessors, metadataSources );
+		prepare( metadataSourceProcessors, metadataSources );
+		bindIndependentMetadata( metadataSourceProcessors, metadataSources );
+		bindTypeDependentMetadata( metadataSourceProcessors, metadataSources );
+		bindMappingMetadata( metadataSourceProcessors, metadataSources, processedEntityNames );
+		bindMappingDependentMetadata( metadataSourceProcessors, metadataSources );

Lines added: 13. Lines removed: 13. Tot = 26
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	-		public SourceProcessingOrder getSourceProcessingOrder();
-		public SourceProcessingOrder getSourceProcessingOrder();

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6447  d3d1fdf4_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6471  46102a2b_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void resolve(SingularAssociationAttributeBinding attributeBinding) {
-						entityBinding.getEntityIdentifier().getValueBinding();
+						entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();

Lines added: 1. Lines removed: 1. Tot = 2
********************************************
********************************************
28/Between/ HHH-6480  e5400897_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void resolve(SingularAssociationAttributeBinding attributeBinding) {
-						entityBinding.getAttributeBinding( attributeBinding.getReferencedAttributeName() ) :
+						entityBinding.locateAttributeBinding( attributeBinding.getReferencedAttributeName() ) :

Lines added: 1. Lines removed: 1. Tot = 2
********************************************
********************************************
28/Between/ HHH-6503  631286c7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6506  2b0e0281_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		new AttributeTypeResolver( this ).resolve();
+		new HibernateTypeResolver( this ).resolve();

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-6683  f4fa1762_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	-	public BasicServiceRegistry getServiceRegistry() {
-	public BasicServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
28/Between/ HHH-6732  129c0f13_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void resolve(AST node) throws SemanticException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-7442  a86997c7_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
-				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
+		this.classLoaderService = new ValueHolder<ClassLoaderService>(
+				new ValueHolder.DeferredInitializer<ClassLoaderService>() {
-		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
-				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
+		this.persisterClassResolverService = new ValueHolder<PersisterClassResolver>(
+				new ValueHolder.DeferredInitializer<PersisterClassResolver>() {

Lines added: 4. Lines removed: 4. Tot = 8
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry();

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-7556  4ad49a02_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistryImplementor getServiceRegistry();

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-7580  7976e239_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-		this.serviceRegistry =  metadataSources.getServiceRegistry();
+		this.serviceRegistry =  options.getServiceRegistry();

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	+		public StandardServiceRegistry getServiceRegistry();
+		public StandardServiceRegistry getServiceRegistry();

Lines added: 1. Lines removed: 0. Tot = 1
********************************************
********************************************
28/Between/ HHH-7580  c52864d1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-8741  cd590470_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public StandardServiceRegistry getServiceRegistry();

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	protected void resolve(AST node) throws SemanticException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-8822  2a55763e_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public ServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
28/Between/ HHH-9466  66ce8b7f_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-	public MetadataImpl(MetadataSources metadataSources, Options options) {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
7c39b19ab2 

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* getSourceProcessingOrder
* getServiceRegistry
* resolve
—————————
Method found in diff:	public SessionFactoryServiceRegistry getServiceRegistry() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-	private void resolve(SingularAssociationAttributeBinding attributeBinding) {
-	private void resolve(SingularAssociationAttributeBinding attributeBinding) {

Lines added: 0. Lines removed: 1. Tot = 1
********************************************
********************************************
