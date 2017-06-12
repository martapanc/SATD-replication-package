diff --git a/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
index 3665362576..a5630ea0ef 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/MetadataBuilder.java
@@ -1,485 +1,484 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.boot;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
 import org.hibernate.boot.archive.scan.spi.ScanOptions;
 import org.hibernate.boot.archive.scan.spi.Scanner;
 import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
 import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
 import org.hibernate.boot.model.TypeContributor;
 import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
 import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.MetadataSourceType;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.type.BasicType;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 import org.jboss.jandex.IndexView;
 
 /**
  * Contract for specifying various overrides to be used in metamodel building.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  *
  * @since 5.0
  */
 public interface MetadataBuilder {
 	/**
 	 * Specify the implicit catalog name to apply to any unqualified database names.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#DEFAULT_CATALOG}
 	 * setting if using property-based configuration.
 	 *
 	 * @param implicitCatalogName The implicit catalog name
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_CATALOG
 	 */
 	MetadataBuilder applyImplicitCatalogName(String implicitCatalogName);
 
 	/**
 	 * Specify the implicit schema name to apply to any unqualified database names.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#DEFAULT_SCHEMA}
 	 * setting if using property-based configuration.
 	 *
 	 * @param implicitSchemaName The implicit schema name
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_SCHEMA
 	 */
 	MetadataBuilder applyImplicitSchemaName(String implicitSchemaName);
 
 	/**
 	 * Specify the ImplicitNamingStrategy to use in building the Metadata.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#IMPLICIT_NAMING_STRATEGY}
 	 * setting if using property-based configuration.
 	 *
 	 * @param namingStrategy The ImplicitNamingStrategy to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#IMPLICIT_NAMING_STRATEGY
 	 */
 	MetadataBuilder applyImplicitNamingStrategy(ImplicitNamingStrategy namingStrategy);
 
 	/**
 	 * Specify the PhysicalNamingStrategy to use in building the Metadata.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#PHYSICAL_NAMING_STRATEGY}
 	 * setting if using property-based configuration.
 	 *
 	 * @param namingStrategy The PhysicalNamingStrategy to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#PHYSICAL_NAMING_STRATEGY
 	 */
 	MetadataBuilder applyPhysicalNamingStrategy(PhysicalNamingStrategy namingStrategy);
 
 	/**
 	 * Defines the Hibernate Commons Annotations ReflectionManager to use
 	 *
 	 * @param reflectionManager The ReflectionManager to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @deprecated Deprecated (with no replacement) to indicate that this will go away as
 	 * we migrate away from Hibernate Commons Annotations to Jandex for annotation handling
 	 * and XMl->annotation merging.
 	 */
 	@Deprecated
 	MetadataBuilder applyReflectionManager(ReflectionManager reflectionManager);
 
 	/**
 	 * Specify the second-level cache mode to be used.  This is the cache mode in terms of whether or
 	 * not to cache.
 	 * <p/>
 	 * Its default is defined by the {@coce javax.persistence.sharedCache.mode} setting if using
 	 * property-based configuration.
 	 *
 	 * @param cacheMode The cache mode.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #applyAccessType
 	 */
 	MetadataBuilder applySharedCacheMode(SharedCacheMode cacheMode);
 
 	/**
 	 * Specify the second-level access-type to be used by default for entities and collections that define second-level
 	 * caching, but do not specify a granular access-type.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#DEFAULT_CACHE_CONCURRENCY_STRATEGY}
 	 * setting if using property-based configuration.
 	 *
 	 * @param accessType The access-type to use as default.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_CACHE_CONCURRENCY_STRATEGY
 	 * @see #applySharedCacheMode(javax.persistence.SharedCacheMode)
 	 */
 	MetadataBuilder applyAccessType(AccessType accessType);
 
 	/**
 	 * Allows specifying a specific Jandex index to use for reading annotation information.
 	 * <p/>
 	 * It is <i>important</i> to understand that if a Jandex index is passed in, it is expected that
 	 * this Jandex index already contains all entries for all classes.  No additional indexing will be
 	 * done in this case.
 	 * <p/>
 	 * NOTE : Here for future expansion.  At the moment the passed Jandex index is not used.
 	 *
 	 * @param jandexView The Jandex index to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyIndexView(IndexView jandexView);
 
 	/**
 	 * Specify the options to be used in performing scanning.
 	 *
 	 * @param scanOptions The scan options.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#SCANNER_DISCOVERY
 	 */
 	MetadataBuilder applyScanOptions(ScanOptions scanOptions);
 
 	/**
 	 * Consider this temporary as discussed on {@link ScanEnvironment}
 	 *
 	 * @param scanEnvironment The environment for scanning
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyScanEnvironment(ScanEnvironment scanEnvironment);
 
 	/**
 	 * Specify a particular Scanner instance to use.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#SCANNER}
 	 * setting if using property-based configuration.
 	 *
 	 * @param scanner The scanner to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#SCANNER
 	 */
 	MetadataBuilder applyScanner(Scanner scanner);
 
 	/**
 	 * Specify a particular ArchiveDescriptorFactory instance to use in scanning.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#SCANNER_ARCHIVE_INTERPRETER}
 	 * setting if using property-based configuration.
 	 *
 	 * @param factory The ArchiveDescriptorFactory to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#SCANNER_ARCHIVE_INTERPRETER
 	 */
 	MetadataBuilder applyArchiveDescriptorFactory(ArchiveDescriptorFactory factory);
 
 	/**
 	 * Should we enable support for the "new" (since 3.2) identifier generator mappings for
 	 * handling:<ul>
 	 *     <li>{@link javax.persistence.GenerationType#SEQUENCE}</li>
 	 *     <li>{@link javax.persistence.GenerationType#IDENTITY}</li>
 	 *     <li>{@link javax.persistence.GenerationType#TABLE}</li>
 	 *     <li>{@link javax.persistence.GenerationType#AUTO}</li>
 	 * </ul>
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS}
 	 * setting if using property-based configuration.
 	 *
 	 *
 	 * @param enable {@code true} to enable; {@code false} to disable;don't call for
 	 * default.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS
 	 */
 	MetadataBuilder enableNewIdentifierGeneratorSupport(boolean enable);
 
 	/**
 	 * Should we process or ignore explicitly defined discriminators in the case
 	 * of joined-subclasses.  The legacy behavior of Hibernate was to ignore the
 	 * discriminator annotations because Hibernate (unlike some providers) does
 	 * not need discriminators to determine the concrete type when it comes to
 	 * joined inheritance.  However, for portability reasons we do now allow using
 	 * explicit discriminators along with joined inheritance.  It is configurable
 	 * though to support legacy apps.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS}
 	 * setting if using property-based configuration.
 	 *
 	 * @param enabled Should processing (not ignoring) explicit discriminators be
 	 * enabled?
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	MetadataBuilder enableExplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
 
 	/**
 	 * Similarly to {@link #enableExplicitDiscriminatorsForJoinedSubclassSupport},
 	 * but here how should we treat joined inheritance when there is no explicitly
 	 * defined discriminator annotations?  If enabled, we will handle joined
 	 * inheritance with no explicit discriminator annotations by implicitly
 	 * creating one (following the JPA implicit naming rules).
 	 * <p/>
 	 * Again the premise here is JPA portability, bearing in mind that some
 	 * JPA provider need these discriminators.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS}
 	 * setting if using property-based configuration.
 	 *
 	 * @param enabled Should we implicitly create discriminator for joined
 	 * inheritance if one is not explicitly mentioned?
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	MetadataBuilder enableImplicitDiscriminatorsForJoinedSubclassSupport(boolean enabled);
 
 	/**
 	 * For entities which do not explicitly say, should we force discriminators into
 	 * SQL selects?  The (historical) default is {@code false}
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT}
 	 * setting if using property-based configuration.
 	 *
 	 * @param supported {@code true} indicates we will force the discriminator into the select;
 	 * {@code false} indicates we will not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT
 	 */
 	MetadataBuilder enableImplicitForcingOfDiscriminatorsInSelect(boolean supported);
 
 	/**
 	 * Should nationalized variants of character data be used in the database types?  For example, should
 	 * {@code NVARCHAR} be used instead of {@code VARCHAR}?  {@code NCLOB} instead of {@code CLOB}?
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA}
 	 * setting if using property-based configuration.
 	 *
 	 * @param enabled {@code true} says to use nationalized variants; {@code false}
 	 * says to use the non-nationalized variants.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA
 	 */
 	MetadataBuilder enableGlobalNationalizedCharacterDataSupport(boolean enabled);
 
 	/**
 	 * Specify an additional or overridden basic type mapping.
 	 *
 	 * @param type The type addition or override.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyBasicType(BasicType type);
 
 	/**
 	 * Specify an additional or overridden basic type mapping supplying specific
 	 * registration keys.
 	 *
 	 * @param type The type addition or override.
 	 * @param keys The keys under which to register the basic type.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyBasicType(BasicType type, String... keys);
 
 	/**
 	 * Register an additional or overridden custom type mapping.
 	 *
 	 * @param type The custom type
 	 * @param keys The keys under which to register the custom type.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyBasicType(UserType type, String... keys);
 
 	/**
 	 * Register an additional or overridden composite custom type mapping.
 	 *
 	 * @param type The composite custom type
 	 * @param keys The keys under which to register the composite custom type.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyBasicType(CompositeUserType type, String... keys);
 
 	/**
 	 * Apply an explicit TypeContributor (implicit application via ServiceLoader will still happen too)
 	 *
 	 * @param typeContributor The contributor to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyTypes(TypeContributor typeContributor);
 
 	/**
 	 * Apply a CacheRegionDefinition to be applied to an entity, collection or query while building the
 	 * Metadata object.
 	 *
 	 * @param cacheRegionDefinition The cache region definition to apply
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition);
 
 	/**
 	 * Apply a ClassLoader for use while building the Metadata.
 	 * <p/>
 	 * Ideally we should avoid accessing ClassLoaders when perform 1st phase of bootstrap.  This
 	 * is a ClassLoader that can be used in cases when we have to.  IN EE managed environments, this
 	 * is the ClassLoader mandated by
 	 * {@link javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()}.  This ClassLoader
 	 * is thrown away by the container afterwards.  The idea being that the Class can still be enhanced
 	 * in the application ClassLoader.  In other environments, pass a ClassLoader that performs the
 	 * same function if desired.
 	 *
 	 * @param tempClassLoader ClassLoader for use during building the Metadata
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	MetadataBuilder applyTempClassLoader(ClassLoader tempClassLoader);
 
 	/**
 	 * Apply a specific ordering to the processing of sources.  Note that unlike most
 	 * of the methods on this contract that deal with multiple values internally, this
 	 * one *replaces* any already set (its more a setter) instead of adding to.
 	 * <p/>
 	 * Its default is defined by the {@link org.hibernate.cfg.AvailableSettings#ARTIFACT_PROCESSING_ORDER}
 	 * setting if using property-based configuration.
 	 *
 	 * @param sourceTypes The types, in the order they should be processed
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#ARTIFACT_PROCESSING_ORDER
 	 */
 	MetadataBuilder applySourceProcessOrdering(MetadataSourceType... sourceTypes);
 
 	MetadataBuilder applySqlFunction(String functionName, SQLFunction function);
 
 	MetadataBuilder applyAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
-
 	/**
 	 * Adds an AttributeConverter by a AttributeConverterDefinition
 	 *
 	 * @param definition The definition
 	 *
 	 * @return {@code this} for method chaining
 	 */
 	MetadataBuilder applyAttributeConverter(AttributeConverterDefinition definition);
 
 	/**
 	 * Adds an AttributeConverter by its Class.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(Class)
 	 */
 	MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass);
 
 	/**
 	 * Adds an AttributeConverter by its Class plus a boolean indicating whether to auto apply it.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(Class, boolean)
 	 */
 	MetadataBuilder applyAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply);
 
 	/**
 	 * Adds an AttributeConverter instance.
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(AttributeConverter)
 	 */
 	MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter);
 
 	/**
 	 * Adds an AttributeConverter instance, explicitly indicating whether to auto-apply.
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 *
 	 * @return {@code this} for method chaining
 	 *
 	 * @see org.hibernate.cfg.AttributeConverterDefinition#from(AttributeConverter, boolean)
 	 */
 	MetadataBuilder applyAttributeConverter(AttributeConverter attributeConverter, boolean autoApply);
 
 	MetadataBuilder applyIdGenerationTypeInterpreter(IdGeneratorStrategyInterpreter interpreter);
 
 
 //	/**
 //	 * Specify the resolve to be used in identifying the backing members of a
 //	 * persistent attributes.
 //	 *
 //	 * @param resolver The resolver to use
 //	 *
 //	 * @return {@code this}, for method chaining
 //	 */
 //	public MetadataBuilder with(PersistentAttributeMemberResolver resolver);
 
 	/**
 	 * Allows unwrapping this builder as another, more specific type.
 	 *
 	 * @param type
 	 * @param <T>
 	 *
 	 * @return The unwrapped builder.
 	 */
 	public <T extends MetadataBuilder> T unwrap(Class<T> type);
 
 	/**
 	 * Actually build the metamodel
 	 *
 	 * @return The built metadata.
 	 */
 	public Metadata build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterDescriptorImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterDescriptorImpl.java
new file mode 100644
index 0000000000..523658d94c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterDescriptorImpl.java
@@ -0,0 +1,248 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.boot.internal;
+
+import java.lang.reflect.Field;
+import java.lang.reflect.Member;
+import java.lang.reflect.Method;
+import java.util.Collection;
+import java.util.List;
+import java.util.Map;
+import javax.persistence.AttributeConverter;
+
+import org.hibernate.AnnotationException;
+import org.hibernate.HibernateException;
+import org.hibernate.annotations.common.reflection.ReflectionManager;
+import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.annotations.common.reflection.java.JavaXMember;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
+import org.hibernate.boot.spi.MetadataBuildingContext;
+import org.hibernate.cfg.AttributeConverterDefinition;
+
+import com.fasterxml.classmate.ResolvedType;
+import com.fasterxml.classmate.ResolvedTypeWithMembers;
+import com.fasterxml.classmate.members.ResolvedField;
+import com.fasterxml.classmate.members.ResolvedMember;
+import com.fasterxml.classmate.members.ResolvedMethod;
+
+/**
+ * The standard AttributeConverterDescriptor implementation
+ *
+ * @author Steve Ebersole
+ */
+public class AttributeConverterDescriptorImpl implements AttributeConverterDescriptor {
+	private final AttributeConverter attributeConverter;
+	private final boolean autoApply;
+	private final ResolvedType domainType;
+	private final ResolvedType jdbcType;
+
+
+	public static AttributeConverterDescriptor create(
+			AttributeConverterDefinition definition,
+			ClassmateContext classmateContext) {
+		final AttributeConverter converter = definition.getAttributeConverter();
+		final Class converterClass = converter.getClass();
+
+		final ResolvedType converterType = classmateContext.getTypeResolver().resolve( converterClass );
+		final List<ResolvedType> converterParamTypes = converterType.typeParametersFor( AttributeConverter.class );
+		if ( converterParamTypes == null ) {
+			throw new AnnotationException(
+					"Could not extract type parameter information from AttributeConverter implementation ["
+							+ converterClass.getName() + "]"
+			);
+		}
+		else if ( converterParamTypes.size() != 2 ) {
+			throw new AnnotationException(
+					"Unexpected type parameter information for AttributeConverter implementation [" +
+							converterClass.getName() + "]; expected 2 parameter types, but found " + converterParamTypes.size()
+			);
+		}
+
+		return new AttributeConverterDescriptorImpl(
+				converter,
+				definition.isAutoApply(),
+				converterParamTypes.get( 0 ),
+				converterParamTypes.get( 1 )
+		);
+	}
+
+	private AttributeConverterDescriptorImpl(
+			AttributeConverter attributeConverter,
+			boolean autoApply,
+			ResolvedType domainType,
+			ResolvedType jdbcType) {
+		this.attributeConverter = attributeConverter;
+		this.autoApply = autoApply;
+		this.domainType = domainType;
+		this.jdbcType = jdbcType;
+	}
+
+	@Override
+	public AttributeConverter getAttributeConverter() {
+		return attributeConverter;
+	}
+
+	@Override
+	public Class<?> getDomainType() {
+		return domainType.getErasedType();
+	}
+
+	@Override
+	public Class<?> getJdbcType() {
+		return jdbcType.getErasedType();
+	}
+
+	@Override
+	@SuppressWarnings("SimplifiableIfStatement")
+	public boolean shouldAutoApplyToAttribute(XProperty xProperty, MetadataBuildingContext context) {
+		if ( !autoApply ) {
+			return false;
+		}
+
+		final ResolvedType attributeType = resolveAttributeType( xProperty, context );
+		return typesMatch( domainType, attributeType );
+	}
+
+	private ResolvedType resolveAttributeType(XProperty xProperty, MetadataBuildingContext context) {
+		return resolveMember( xProperty, context ).getType();
+	}
+
+	private ResolvedMember resolveMember(XProperty xProperty, MetadataBuildingContext buildingContext) {
+		final ClassmateContext classmateContext = buildingContext.getMetadataCollector().getClassmateContext();
+		final ReflectionManager reflectionManager = buildingContext.getBuildingOptions().getReflectionManager();
+
+		final ResolvedType declaringClassType = classmateContext.getTypeResolver().resolve(
+				reflectionManager.toClass( xProperty.getDeclaringClass() )
+		);
+		final ResolvedTypeWithMembers declaringClassWithMembers = classmateContext.getMemberResolver().resolve(
+				declaringClassType,
+				null,
+				null
+		);
+
+		final Member member = toMember( xProperty );
+		if ( member instanceof Method ) {
+			for ( ResolvedMethod resolvedMember : declaringClassWithMembers.getMemberMethods() ) {
+				if ( resolvedMember.getName().equals( member.getName() ) ) {
+					return resolvedMember;
+				}
+			}
+		}
+		else if ( member instanceof Field ) {
+			for ( ResolvedField resolvedMember : declaringClassWithMembers.getMemberFields() ) {
+				if ( resolvedMember.getName().equals( member.getName() ) ) {
+					return resolvedMember;
+				}
+			}
+		}
+		else {
+			throw new HibernateException( "Unexpected java.lang.reflect.Member type from org.hibernate.annotations.common.reflection.java.JavaXMember : " + member );
+		}
+
+		throw new HibernateException(
+				"Could not locate resolved type information for attribute [" + member.getName() + "] from Classmate"
+		);
+	}
+
+	private static Method memberMethod;
+
+	private static Member toMember(XProperty xProperty) {
+		if ( memberMethod == null ) {
+			Class<JavaXMember> javaXMemberClass = JavaXMember.class;
+			try {
+				memberMethod = javaXMemberClass.getDeclaredMethod( "getMember" );
+				memberMethod.setAccessible( true );
+			}
+			catch (NoSuchMethodException e) {
+				throw new HibernateException( "Could not access org.hibernate.annotations.common.reflection.java.JavaXMember#getMember method", e );
+			}
+		}
+
+		try {
+			return (Member) memberMethod.invoke( xProperty );
+		}
+		catch (Exception e) {
+			throw new HibernateException( "Could not access org.hibernate.annotations.common.reflection.java.JavaXMember#getMember method", e );
+		}
+	}
+
+	private boolean typesMatch(ResolvedType converterDefinedType, ResolvedType checkType) {
+		if ( !checkType.getErasedType().isAssignableFrom( converterDefinedType.getErasedType() ) ) {
+			return false;
+		}
+
+		// if the converter did not define any nested type parameters, then the check above is
+		// enough for a match
+		if ( converterDefinedType.getTypeParameters().isEmpty() ) {
+			return true;
+		}
+
+		// however, here the converter *did* define nested type parameters, so we'd have a converter defined using something like, e.g., List<String> for its
+		// domain type.
+		//
+		// we need to check those nested types as well
+
+		if ( checkType.getTypeParameters().isEmpty() ) {
+			// the domain type did not define nested type params.  a List<String> would not auto-match a List(<Object>)
+			return false;
+		}
+
+		if ( converterDefinedType.getTypeParameters().size() != checkType.getTypeParameters().size() ) {
+			// they had different number of type params somehow.
+			return false;
+		}
+
+		for ( int i = 0; i < converterDefinedType.getTypeParameters().size(); i++ ) {
+			if ( !typesMatch( converterDefinedType.getTypeParameters().get( i ), checkType.getTypeParameters().get( i ) ) ) {
+				return false;
+			}
+		}
+
+		return true;
+	}
+
+	@Override
+	public boolean shouldAutoApplyToCollectionElement(XProperty xProperty, MetadataBuildingContext context) {
+		if ( !autoApply ) {
+			return false;
+		}
+
+		final ResolvedMember collectionMember = resolveMember( xProperty, context );
+		final ResolvedType elementType;
+
+		if ( Map.class.isAssignableFrom( collectionMember.getType().getErasedType() ) ) {
+			elementType = collectionMember.getType().typeParametersFor( Map.class ).get( 1 );
+		}
+		else if ( Collection.class.isAssignableFrom( collectionMember.getType().getErasedType() ) ) {
+			elementType = collectionMember.getType().typeParametersFor( Collection.class ).get( 0 );
+		}
+		else {
+			throw new HibernateException( "Attribute was neither a Collection nor a Map : " + collectionMember.getType().getErasedType() );
+		}
+
+		return typesMatch( domainType, elementType );
+	}
+
+	@Override
+	public boolean shouldAutoApplyToMapKey(XProperty xProperty, MetadataBuildingContext context) {
+		if ( !autoApply ) {
+			return false;
+		}
+
+		final ResolvedMember collectionMember = resolveMember( xProperty, context );
+		final ResolvedType keyType;
+
+		if ( Map.class.isAssignableFrom( collectionMember.getType().getErasedType() ) ) {
+			keyType = collectionMember.getType().typeParametersFor( Map.class ).get( 0 );
+		}
+		else {
+			throw new HibernateException( "Attribute was not a Map : " + collectionMember.getType().getErasedType() );
+		}
+
+		return typesMatch( domainType, keyType );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterDescriptorNonAutoApplicableImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterDescriptorNonAutoApplicableImpl.java
new file mode 100644
index 0000000000..4557fc52ce
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterDescriptorNonAutoApplicableImpl.java
@@ -0,0 +1,199 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.boot.internal;
+
+import java.lang.reflect.ParameterizedType;
+import java.lang.reflect.Type;
+import java.lang.reflect.TypeVariable;
+import java.util.ArrayList;
+import java.util.Arrays;
+import java.util.List;
+import javax.persistence.AttributeConverter;
+
+import org.hibernate.AnnotationException;
+import org.hibernate.AssertionFailure;
+import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
+import org.hibernate.boot.spi.MetadataBuildingContext;
+
+/**
+ * Special-use AttributeConverterDescriptor implementation for cases where the converter will never
+ * be used for auto-apply.
+ *
+ * @author Steve Ebersole
+ */
+public class AttributeConverterDescriptorNonAutoApplicableImpl implements AttributeConverterDescriptor {
+	private final AttributeConverter converter;
+
+	private Class domainType;
+	private Class jdbcType;
+
+	public AttributeConverterDescriptorNonAutoApplicableImpl(AttributeConverter converter) {
+		this.converter = converter;
+
+		final Class attributeConverterClass = converter.getClass();
+		final ParameterizedType attributeConverterSignature = extractAttributeConverterParameterizedType(
+				attributeConverterClass
+		);
+		if ( attributeConverterSignature == null ) {
+			throw new AssertionFailure(
+					"Could not extract ParameterizedType representation of AttributeConverter definition " +
+							"from AttributeConverter implementation class [" + attributeConverterClass.getName() + "]"
+			);
+		}
+
+		if ( attributeConverterSignature.getActualTypeArguments().length < 2 ) {
+			throw new AnnotationException(
+					"AttributeConverter [" + attributeConverterClass.getName()
+							+ "] did not retain parameterized type information"
+			);
+		}
+
+		if ( attributeConverterSignature.getActualTypeArguments().length > 2 ) {
+			throw new AnnotationException(
+					"AttributeConverter [" + attributeConverterClass.getName()
+							+ "] specified more than 2 parameterized types"
+			);
+		}
+
+		this.domainType = extractClass( attributeConverterSignature.getActualTypeArguments()[0] );
+		if ( this.domainType == null ) {
+			throw new AnnotationException(
+					"Could not determine domain type from given AttributeConverter [" +
+							attributeConverterClass.getName() + "]"
+			);
+		}
+
+		this.jdbcType = extractClass(attributeConverterSignature.getActualTypeArguments()[1]);
+		if ( this.jdbcType == null ) {
+			throw new AnnotationException(
+					"Could not determine JDBC type from given AttributeConverter [" +
+							attributeConverterClass.getName() + "]"
+			);
+		}
+	}
+
+	private ParameterizedType extractAttributeConverterParameterizedType(Type base) {
+		if ( base != null ) {
+			Class clazz = extractClass( base );
+			List<Type> types = new ArrayList<Type>();
+			types.add( clazz.getGenericSuperclass() );
+			types.addAll( Arrays.asList( clazz.getGenericInterfaces() ) );
+			for ( Type type : types ) {
+				type = resolveType( type, base );
+				if ( ParameterizedType.class.isInstance( type ) ) {
+					final ParameterizedType parameterizedType = (ParameterizedType) type;
+					if ( AttributeConverter.class.equals( parameterizedType.getRawType() ) ) {
+						return parameterizedType;
+					}
+				}
+				ParameterizedType parameterizedType = extractAttributeConverterParameterizedType( type );
+				if ( parameterizedType != null ) {
+					return parameterizedType;
+				}
+			}
+		}
+		return null;
+	}
+
+	private static Class extractClass(Type type) {
+		if ( type instanceof Class ) {
+			return (Class) type;
+		}
+		else if ( type instanceof ParameterizedType ) {
+			return extractClass( ( (ParameterizedType) type ).getRawType() );
+		}
+		return null;
+	}
+
+	private static Type resolveType(Type target, Type context) {
+		if ( target instanceof ParameterizedType ) {
+			return resolveParameterizedType( (ParameterizedType) target, context );
+		}
+		else if ( target instanceof TypeVariable ) {
+			return resolveTypeVariable( (TypeVariable) target, (ParameterizedType) context );
+		}
+		return target;
+	}
+
+	private static ParameterizedType resolveParameterizedType(final ParameterizedType parameterizedType, Type context) {
+		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
+
+		final Type[] resolvedTypeArguments = new Type[actualTypeArguments.length];
+		for ( int idx = 0; idx < actualTypeArguments.length; idx++ ) {
+			resolvedTypeArguments[idx] = resolveType( actualTypeArguments[idx], context );
+		}
+		return new ParameterizedType() {
+
+			@Override
+			public Type[] getActualTypeArguments() {
+				return resolvedTypeArguments;
+			}
+
+			@Override
+			public Type getRawType() {
+				return parameterizedType.getRawType();
+			}
+
+			@Override
+			public Type getOwnerType() {
+				return parameterizedType.getOwnerType();
+			}
+
+		};
+	}
+
+	private static Type resolveTypeVariable(TypeVariable typeVariable, ParameterizedType context) {
+		Class clazz = extractClass( context.getRawType() );
+		TypeVariable[] typeParameters = clazz.getTypeParameters();
+		for ( int idx = 0; idx < typeParameters.length; idx++ ) {
+			if ( typeVariable.getName().equals( typeParameters[idx].getName() ) ) {
+				return resolveType( context.getActualTypeArguments()[idx], context );
+			}
+		}
+		return typeVariable;
+	}
+
+	private static Class extractType(TypeVariable typeVariable) {
+		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
+		if ( boundTypes == null || boundTypes.length != 1 ) {
+			return null;
+		}
+
+		return (Class) boundTypes[0];
+	}
+
+	@Override
+	public AttributeConverter getAttributeConverter() {
+		return converter;
+	}
+
+	@Override
+	public Class<?> getDomainType() {
+		return domainType;
+	}
+
+	@Override
+	public Class<?> getJdbcType() {
+		return jdbcType;
+	}
+
+	@Override
+	public boolean shouldAutoApplyToAttribute(XProperty xProperty, MetadataBuildingContext context) {
+		return false;
+	}
+
+	@Override
+	public boolean shouldAutoApplyToCollectionElement(XProperty xProperty, MetadataBuildingContext context) {
+		return false;
+	}
+
+	@Override
+	public boolean shouldAutoApplyToMapKey(XProperty xProperty, MetadataBuildingContext context) {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterManager.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterManager.java
new file mode 100644
index 0000000000..e5ca124cd1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/AttributeConverterManager.java
@@ -0,0 +1,190 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.boot.internal;
+
+import java.util.ArrayList;
+import java.util.Collection;
+import java.util.Collections;
+import java.util.List;
+import java.util.Locale;
+import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.boot.spi.AttributeConverterAutoApplyHandler;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
+import org.hibernate.boot.spi.MetadataBuildingContext;
+import org.hibernate.internal.util.StringHelper;
+
+import org.jboss.logging.Logger;
+
+/**
+ * @author Steve Ebersole
+ */
+public class AttributeConverterManager implements AttributeConverterAutoApplyHandler {
+	private static final Logger log = Logger.getLogger( AttributeConverterManager.class );
+
+	private Map<Class, AttributeConverterDescriptor> attributeConverterDescriptorsByClass;
+
+	void addConverter(AttributeConverterDescriptor descriptor) {
+		if ( attributeConverterDescriptorsByClass == null ) {
+			attributeConverterDescriptorsByClass = new ConcurrentHashMap<Class, AttributeConverterDescriptor>();
+		}
+
+		final Object old = attributeConverterDescriptorsByClass.put(
+				descriptor.getAttributeConverter().getClass(),
+				descriptor
+		);
+
+		if ( old != null ) {
+			throw new AssertionFailure(
+					String.format(
+							Locale.ENGLISH,
+							"AttributeConverter class [%s] registered multiple times",
+							descriptor.getAttributeConverter().getClass()
+					)
+			);
+		}
+	}
+
+	private Collection<AttributeConverterDescriptor> converterDescriptors() {
+		if ( attributeConverterDescriptorsByClass == null ) {
+			return Collections.emptyList();
+		}
+		return attributeConverterDescriptorsByClass.values();
+	}
+
+	@Override
+	public AttributeConverterDescriptor findAutoApplyConverterForAttribute(
+			XProperty xProperty,
+			MetadataBuildingContext context) {
+		List<AttributeConverterDescriptor> matched = new ArrayList<AttributeConverterDescriptor>();
+
+		for ( AttributeConverterDescriptor descriptor : converterDescriptors() ) {
+			log.debugf(
+					"Checking auto-apply AttributeConverter [%s] (type=%s) for match against attribute : %s.%s (type=%s)",
+					descriptor.toString(),
+					descriptor.getDomainType().getSimpleName(),
+					xProperty.getDeclaringClass().getName(),
+					xProperty.getName(),
+					xProperty.getType().getName()
+			);
+
+			if ( descriptor.shouldAutoApplyToAttribute( xProperty, context ) ) {
+				matched.add( descriptor );
+			}
+		}
+
+		if ( matched.isEmpty() ) {
+			return null;
+		}
+
+		if ( matched.size() == 1 ) {
+			return matched.get( 0 );
+		}
+
+		// otherwise, we had multiple matches
+		throw new RuntimeException(
+				String.format(
+						Locale.ROOT,
+						"Multiple auto-apply converters matched attribute [%s.%s] : %s",
+						xProperty.getDeclaringClass().getName(),
+						xProperty.getName(),
+						StringHelper.join( matched, RENDERER )
+				)
+		);
+	}
+
+	@Override
+	public AttributeConverterDescriptor findAutoApplyConverterForCollectionElement(
+			XProperty xProperty,
+			MetadataBuildingContext context) {
+		List<AttributeConverterDescriptor> matched = new ArrayList<AttributeConverterDescriptor>();
+
+		for ( AttributeConverterDescriptor descriptor : converterDescriptors() ) {
+			log.debugf(
+					"Checking auto-apply AttributeConverter [%s] (type=%s) for match against collection attribute's element : %s.%s (type=%s)",
+					descriptor.toString(),
+					descriptor.getDomainType().getSimpleName(),
+					xProperty.getDeclaringClass().getName(),
+					xProperty.getName(),
+					xProperty.getElementClass().getName()
+			);
+			if ( descriptor.shouldAutoApplyToCollectionElement( xProperty, context ) ) {
+				matched.add( descriptor );
+			}
+		}
+
+		if ( matched.isEmpty() ) {
+			return null;
+		}
+
+		if ( matched.size() == 1 ) {
+			return matched.get( 0 );
+		}
+
+		// otherwise, we had multiple matches
+		throw new RuntimeException(
+				String.format(
+						Locale.ROOT,
+						"Multiple auto-apply converters matched attribute [%s.%s] : %s",
+						xProperty.getDeclaringClass().getName(),
+						xProperty.getName(),
+						StringHelper.join( matched, RENDERER )
+				)
+		);
+	}
+
+	@Override
+	public AttributeConverterDescriptor findAutoApplyConverterForMapKey(
+			XProperty xProperty,
+			MetadataBuildingContext context) {
+		List<AttributeConverterDescriptor> matched = new ArrayList<AttributeConverterDescriptor>();
+
+		for ( AttributeConverterDescriptor descriptor : converterDescriptors() ) {
+			log.debugf(
+					"Checking auto-apply AttributeConverter [%s] (type=%s) for match against map attribute's key : %s.%s (type=%s)",
+					descriptor.toString(),
+					descriptor.getDomainType().getSimpleName(),
+					xProperty.getDeclaringClass().getName(),
+					xProperty.getName(),
+					xProperty.getMapKey().getName()
+			);
+			if ( descriptor.shouldAutoApplyToMapKey( xProperty, context ) ) {
+				matched.add( descriptor );
+			}
+		}
+
+		if ( matched.isEmpty() ) {
+			return null;
+		}
+
+		if ( matched.size() == 1 ) {
+			return matched.get( 0 );
+		}
+
+		// otherwise, we had multiple matches
+		throw new RuntimeException(
+				String.format(
+						Locale.ROOT,
+						"Multiple auto-apply converters matched attribute [%s.%s] : %s",
+						xProperty.getDeclaringClass().getName(),
+						xProperty.getName(),
+						StringHelper.join( matched, RENDERER )
+				)
+		);
+	}
+
+	private static StringHelper.Renderer<AttributeConverterDescriptor> RENDERER = new StringHelper.Renderer<AttributeConverterDescriptor>() {
+		@Override
+		public String render(AttributeConverterDescriptor value) {
+			return value.getAttributeConverter().getClass().getName();
+		}
+	};
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/ClassmateContext.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/ClassmateContext.java
new file mode 100644
index 0000000000..c0bc413a24
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/ClassmateContext.java
@@ -0,0 +1,37 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.boot.internal;
+
+import com.fasterxml.classmate.MemberResolver;
+import com.fasterxml.classmate.TypeResolver;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ClassmateContext {
+	private TypeResolver typeResolver = new TypeResolver();
+	private MemberResolver memberResolver = new MemberResolver( typeResolver );
+
+	public TypeResolver getTypeResolver() {
+		if ( typeResolver == null ) {
+			throw new IllegalStateException( "Classmate context has been released" );
+		}
+		return typeResolver;
+	}
+
+	public MemberResolver getMemberResolver() {
+		if ( memberResolver == null ) {
+			throw new IllegalStateException( "Classmate context has been released" );
+		}
+		return memberResolver;
+	}
+
+	public void release() {
+		typeResolver = null;
+		memberResolver = null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java
index 7767c14cad..e0b030926e 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/InFlightMetadataCollectorImpl.java
@@ -1,2264 +1,2237 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.boot.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import java.util.UUID;
 import java.util.concurrent.ConcurrentHashMap;
 import javax.persistence.AttributeConverter;
 import javax.persistence.Converter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.boot.CacheRegionDefinition;
 import org.hibernate.boot.SessionFactoryBuilder;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
 import org.hibernate.boot.model.naming.ImplicitIndexNameSource;
 import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.relational.ExportableProducer;
 import org.hibernate.boot.model.relational.Namespace;
 import org.hibernate.boot.model.source.internal.ConstraintSecondPass;
 import org.hibernate.boot.model.source.internal.ImplicitColumnNamingSecondPass;
 import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
+import org.hibernate.boot.spi.AttributeConverterAutoApplyHandler;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.InFlightMetadataCollector;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.boot.spi.MetadataBuildingOptions;
 import org.hibernate.boot.spi.NaturalIdUniqueKeyBinder;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.CopyIdentifierComponentSecondPass;
 import org.hibernate.cfg.CreateKeySecondPass;
 import org.hibernate.cfg.FkSecondPass;
 import org.hibernate.cfg.JPAIndexHolder;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.QuerySecondPass;
 import org.hibernate.cfg.RecoverableException;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.cfg.SecondaryTableSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DenormalizedTable;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.type.TypeResolver;
 
 /**
  * The implementation of the in-flight Metadata collector contract.
  *
  * The usage expectation is that this class is used until all Metadata info is
  * collected and then {@link #buildMetadataInstance} is called to generate
  * the complete (and immutable) Metadata object.
  *
  * @author Steve Ebersole
  */
 public class InFlightMetadataCollectorImpl implements InFlightMetadataCollector {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( InFlightMetadataCollectorImpl.class );
 
 	private final MetadataBuildingOptions options;
 	private final TypeResolver typeResolver;
 
+	private final AttributeConverterManager attributeConverterManager = new AttributeConverterManager();
+	private final ClassmateContext classmateContext = new ClassmateContext();
+
 	private final UUID uuid;
 	private final MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private final Map<String,PersistentClass> entityBindingMap = new HashMap<String, PersistentClass>();
 	private final Map<String,Collection> collectionBindingMap = new HashMap<String,Collection>();
 
 	private final Map<String, TypeDefinition> typeDefinitionMap = new HashMap<String, TypeDefinition>();
 	private final Map<String, FilterDefinition> filterDefinitionMap = new HashMap<String, FilterDefinition>();
 	private final Map<String, String> imports = new HashMap<String, String>();
 
 	private Database database;
 
 	private final Map<String, NamedQueryDefinition> namedQueryMap = new HashMap<String, NamedQueryDefinition>();
 	private final Map<String, NamedSQLQueryDefinition> namedNativeQueryMap = new HashMap<String, NamedSQLQueryDefinition>();
 	private final Map<String, NamedProcedureCallDefinition> namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>();
 	private final Map<String, ResultSetMappingDefinition> sqlResultSetMappingMap = new HashMap<String, ResultSetMappingDefinition>();
 
 	private final Map<String, NamedEntityGraphDefinition> namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
 	private final Map<String, FetchProfile> fetchProfileMap = new HashMap<String, FetchProfile>();
 	private final Map<String, IdentifierGeneratorDefinition> idGeneratorDefinitionMap = new HashMap<String, IdentifierGeneratorDefinition>();
 
-	private Map<Class, AttributeConverterDefinition> attributeConverterDefinitionsByClass;
 	private Map<String, SQLFunction> sqlFunctionMap;
 
-
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// All the annotation-processing-specific state :(
 	private final Set<String> defaultIdentifierGeneratorNames = new HashSet<String>();
 	private final Set<String> defaultNamedQueryNames = new HashSet<String>();
 	private final Set<String> defaultNamedNativeQueryNames = new HashSet<String>();
 	private final Set<String> defaultSqlResultSetMappingNames = new HashSet<String>();
 	private final Set<String> defaultNamedProcedureNames = new HashSet<String>();
 	private Map<String, AnyMetaDef> anyMetaDefs;
 	private Map<Class, MappedSuperclass> mappedSuperClasses;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
 	private Map<String, String> mappedByResolver;
 	private Map<String, String> propertyRefResolver;
 	private Set<DelayedPropertyReferenceHandler> delayedPropertyReferenceHandlers;
 	private Map<Table, List<UniqueConstraintHolder>> uniqueConstraintHoldersByTable;
 	private Map<Table, List<JPAIndexHolder>> jpaIndexHoldersByTable;
 
 	public InFlightMetadataCollectorImpl(
 			MetadataBuildingOptions options,
 			TypeResolver typeResolver) {
 		this.uuid = UUID.randomUUID();
 		this.options = options;
 		this.typeResolver = typeResolver;
 
 		this.identifierGeneratorFactory = options.getServiceRegistry().getService( MutableIdentifierGeneratorFactory.class );
 
 		for ( Map.Entry<String, SQLFunction> sqlFunctionEntry : options.getSqlFunctions().entrySet() ) {
 			if ( sqlFunctionMap == null ) {
 				// we need this to be a ConcurrentHashMap for the one we ultimately pass along to the SF
 				// but is this the reference that gets passed along?
 				sqlFunctionMap = new ConcurrentHashMap<String, SQLFunction>( 16, .75f, 1 );
 			}
 			sqlFunctionMap.put( sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue() );
 		}
 
 		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : options.getAuxiliaryDatabaseObjectList() ) {
 			getDatabase().addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 		}
 
 	}
 
 	@Override
 	public UUID getUUID() {
 		return null;
 	}
 
 	@Override
 	public MetadataBuildingOptions getMetadataBuildingOptions() {
 		return options;
 	}
 
 	@Override
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public Database getDatabase() {
 		// important to delay this instantiation until as late as possible.
 		if ( database == null ) {
 			this.database = new Database( options );
 		}
 		return database;
 	}
 
 	@Override
 	public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory) {
 		throw new UnsupportedOperationException( "#buildNamedQueryRepository should not be called on InFlightMetadataCollector" );
 	}
 
 	@Override
 	public Map<String, SQLFunction> getSqlFunctionMap() {
 		return sqlFunctionMap;
 	}
 
 	@Override
 	public void validate() throws MappingException {
 		// nothing to do
 	}
 
 	@Override
 	public Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
 		return new HashSet<MappedSuperclass>( mappedSuperClasses.values() );
 	}
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		throw new UnsupportedOperationException(
 				"You should not be building a SessionFactory from an in-flight metadata collector; and of course " +
 						"we should better segment this in the API :)"
 		);
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		throw new UnsupportedOperationException(
 				"You should not be building a SessionFactory from an in-flight metadata collector; and of course " +
 						"we should better segment this in the API :)"
 		);
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Entity handling
 
 	@Override
 	public java.util.Collection<PersistentClass> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	@Override
 	public Map<String, PersistentClass> getEntityBindingMap() {
 		return entityBindingMap;
 	}
 
 	@Override
 	public PersistentClass getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public void addEntityBinding(PersistentClass persistentClass) throws DuplicateMappingException {
 		final String entityName = persistentClass.getEntityName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, persistentClass );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Collection handling
 
 	@Override
 	public java.util.Collection<Collection> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	@Override
 	public Collection getCollectionBinding(String role) {
 		return collectionBindingMap.get( role );
 	}
 
 	@Override
 	public void addCollectionBinding(Collection collection) throws DuplicateMappingException {
 		final String collectionRole = collection.getRole();
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.COLLECTION, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, collection );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Hibernate Type handling
 
 	@Override
 	public TypeDefinition getTypeDefinition(String registrationKey) {
 		return typeDefinitionMap.get( registrationKey );
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDefinition typeDefinition) {
 		if ( typeDefinition == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 
 		// Need to register both by name and registration keys.
 		if ( !StringHelper.isEmpty( typeDefinition.getName() ) ) {
 			addTypeDefinition( typeDefinition.getName(), typeDefinition );
 		}
 
 		if ( typeDefinition.getRegistrationKeys() != null ) {
 			for ( String registrationKey : typeDefinition.getRegistrationKeys() ) {
 				addTypeDefinition( registrationKey, typeDefinition );
 			}
 		}
 	}
 
 	private void addTypeDefinition(String registrationKey, TypeDefinition typeDefinition) {
 		final TypeDefinition previous = typeDefinitionMap.put(
 				registrationKey, typeDefinition );
 		if ( previous != null ) {
 			log.debugf(
 					"Duplicate typedef name [%s] now -> %s",
 					registrationKey,
 					typeDefinition.getTypeImplementorClass().getName()
 			);
 		}
 	}
 
+	@Override
+	public ClassmateContext getClassmateContext() {
+		return classmateContext;
+	}
+
+
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// attribute converters
 
 	@Override
 	public void addAttributeConverter(AttributeConverterDefinition definition) {
-		if ( attributeConverterDefinitionsByClass == null ) {
-			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
-		}
-
-		final Object old = attributeConverterDefinitionsByClass.put(
-				definition.getAttributeConverter().getClass(),
-				definition
+		attributeConverterManager.addConverter(
+				AttributeConverterDescriptorImpl.create(
+						definition,
+						classmateContext
+				)
 		);
-
-		if ( old != null ) {
-			throw new AssertionFailure(
-					String.format(
-							Locale.ENGLISH,
-							"AttributeConverter class [%s] registered multiple times",
-							definition.getAttributeConverter().getClass()
-					)
-			);
-		}
-	}
-
-	public static AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
-		try {
-			return attributeConverterClass.newInstance();
-		}
-		catch (Exception e) {
-			throw new AnnotationException(
-					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]",
-					e
-			);
-		}
-	}
-
-	public static AttributeConverterDefinition toAttributeConverterDefinition(AttributeConverter attributeConverter) {
-		boolean autoApply = false;
-		Converter converterAnnotation = attributeConverter.getClass().getAnnotation( Converter.class );
-		if ( converterAnnotation != null ) {
-			autoApply = converterAnnotation.autoApply();
-		}
-
-		return new AttributeConverterDefinition( attributeConverter, autoApply );
 	}
 
 	@Override
 	public void addAttributeConverter(Class<? extends AttributeConverter> converterClass) {
-		addAttributeConverter(
-				toAttributeConverterDefinition(
-						instantiateAttributeConverter( converterClass )
-				)
-		);
+		addAttributeConverter( AttributeConverterDefinition.from( converterClass ) );
 	}
 
 	@Override
-	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
-		if ( attributeConverterDefinitionsByClass == null ) {
-			return Collections.emptyList();
-		}
-		return attributeConverterDefinitionsByClass.values();
+	public AttributeConverterAutoApplyHandler getAttributeConverterAutoApplyHandler() {
+		return attributeConverterManager;
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// filter definitions
 
 	@Override
 	public Map<String, FilterDefinition> getFilterDefinitions() {
 		return filterDefinitionMap;
 	}
 
 	@Override
 	public FilterDefinition getFilterDefinition(String name) {
 		return filterDefinitionMap.get( name );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition filterDefinition) {
 		if ( filterDefinition == null || filterDefinition.getFilterName() == null ) {
 			throw new IllegalArgumentException( "Filter definition object or name is null: "  + filterDefinition );
 		}
 		filterDefinitionMap.put( filterDefinition.getFilterName(), filterDefinition );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// fetch profiles
 
 	@Override
 	public java.util.Collection<FetchProfile> getFetchProfiles() {
 		return fetchProfileMap.values();
 	}
 
 	@Override
 	public FetchProfile getFetchProfile(String name) {
 		return fetchProfileMap.get( name );
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		if ( profile == null || profile.getName() == null ) {
 			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
 		}
 		FetchProfile old = fetchProfileMap.put( profile.getName(), profile );
 		if ( old != null ) {
 			log.warn( "Duplicated fetch profile with same name [" + profile.getName() + "] found." );
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// identifier generators
 
 	@Override
 	public IdentifierGeneratorDefinition getIdentifierGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGeneratorDefinitionMap.get( name );
 	}
 
 	@Override
 	public java.util.Collection<Table> collectTableMappings() {
 		ArrayList<Table> tables = new ArrayList<Table>();
 		for ( Namespace namespace : getDatabase().getNamespaces() ) {
 			tables.addAll( namespace.getTables() );
 		}
 		return tables;
 	}
 
 	@Override
 	public void addIdentifierGenerator(IdentifierGeneratorDefinition generator) {
 		if ( generator == null || generator.getName() == null ) {
 			throw new IllegalArgumentException( "ID generator object or name is null." );
 		}
 
 		if ( defaultIdentifierGeneratorNames.contains( generator.getName() ) ) {
 			return;
 		}
 
 		final IdentifierGeneratorDefinition old = idGeneratorDefinitionMap.put( generator.getName(), generator );
 		if ( old != null ) {
 			log.duplicateGeneratorName( old.getName() );
 		}
 	}
 
 	@Override
 	public void addDefaultIdentifierGenerator(IdentifierGeneratorDefinition generator) {
 		this.addIdentifierGenerator( generator );
 		defaultIdentifierGeneratorNames.add( generator.getName() );
 	}
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named EntityGraph handling
 
 	@Override
 	public NamedEntityGraphDefinition getNamedEntityGraph(String name) {
 		return namedEntityGraphMap.get( name );
 	}
 
 	@Override
 	public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs() {
 		return namedEntityGraphMap;
 	}
 
 	@Override
 	public void addNamedEntityGraph(NamedEntityGraphDefinition definition) {
 		final String name = definition.getRegisteredName();
 		final NamedEntityGraphDefinition previous = namedEntityGraphMap.put( name, definition );
 		if ( previous != null ) {
 			throw new DuplicateMappingException(
 					DuplicateMappingException.Type.NAMED_ENTITY_GRAPH, name );
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named query handling
 
 	public NamedQueryDefinition getNamedQueryDefinition(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryMap.get( name );
 	}
 
 	@Override
 	public java.util.Collection<NamedQueryDefinition> getNamedQueryDefinitions() {
 		return namedQueryMap.values();
 	}
 
 	@Override
 	public void addNamedQuery(NamedQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
 		}
 
 		if ( defaultNamedQueryNames.contains( def.getName() ) ) {
 			return;
 		}
 
 		applyNamedQuery( def.getName(), def );
 	}
 
 	private void applyNamedQuery(String name, NamedQueryDefinition query) {
 		checkQueryName( name );
 		namedQueryMap.put( name.intern(), query );
 	}
 
 	private void checkQueryName(String name) throws DuplicateMappingException {
 		if ( namedQueryMap.containsKey( name ) || namedNativeQueryMap.containsKey( name ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.QUERY, name );
 		}
 	}
 
 	@Override
 	public void addDefaultQuery(NamedQueryDefinition queryDefinition) {
 		applyNamedQuery( queryDefinition.getName(), queryDefinition );
 		defaultNamedQueryNames.add( queryDefinition.getName() );
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named native-query handling
 
 	@Override
 	public NamedSQLQueryDefinition getNamedNativeQueryDefinition(String name) {
 		return namedNativeQueryMap.get( name );
 	}
 
 	@Override
 	public java.util.Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
 		return namedNativeQueryMap.values();
 	}
 
 	@Override
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named native query definition object is null" );
 		}
 		if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named native query definition name is null: " + def.getQueryString() );
 		}
 
 		if ( defaultNamedNativeQueryNames.contains( def.getName() ) ) {
 			return;
 		}
 
 		applyNamedNativeQuery( def.getName(), def );
 	}
 
 	private void applyNamedNativeQuery(String name, NamedSQLQueryDefinition query) {
 		checkQueryName( name );
 		namedNativeQueryMap.put( name.intern(), query );
 	}
 
 	@Override
 	public void addDefaultNamedNativeQuery(NamedSQLQueryDefinition query) {
 		applyNamedNativeQuery( query.getName(), query );
 		defaultNamedNativeQueryNames.add( query.getName() );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Named stored-procedure handling
 
 	@Override
 	public java.util.Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions() {
 		return namedProcedureCallMap.values();
 	}
 
 	@Override
 	public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
 		if ( definition == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 
 		final String name = definition.getRegisteredName();
 
 		if ( defaultNamedProcedureNames.contains( name ) ) {
 			return;
 		}
 
 		final NamedProcedureCallDefinition previous = namedProcedureCallMap.put( name, definition );
 		if ( previous != null ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.PROCEDURE, name );
 		}
 	}
 
 	@Override
 	public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
 		addNamedProcedureCallDefinition( definition );
 		defaultNamedProcedureNames.add( definition.getRegisteredName() );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// result-set mapping handling
 
 	@Override
 	public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return sqlResultSetMappingMap;
 	}
 
 	@Override
 	public ResultSetMappingDefinition getResultSetMapping(String name) {
 		return sqlResultSetMappingMap.get( name );
 	}
 
 	@Override
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		if ( resultSetMappingDefinition == null ) {
 			throw new IllegalArgumentException( "Result-set mapping was null" );
 		}
 
 		final String name = resultSetMappingDefinition.getName();
 		if ( name == null ) {
 			throw new IllegalArgumentException( "Result-set mapping name is null: " + resultSetMappingDefinition );
 		}
 
 		if ( defaultSqlResultSetMappingNames.contains( name ) ) {
 			return;
 		}
 
 		applyResultSetMapping( resultSetMappingDefinition );
 	}
 
 	public void applyResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		final ResultSetMappingDefinition old = sqlResultSetMappingMap.put(
 				resultSetMappingDefinition.getName(),
 				resultSetMappingDefinition
 		);
 		if ( old != null ) {
 			throw new DuplicateMappingException(
 					DuplicateMappingException.Type.RESULT_SET_MAPPING,
 					resultSetMappingDefinition.getName()
 			);
 		}
 	}
 
 	@Override
 	public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 		final String name = definition.getName();
 		if ( !defaultSqlResultSetMappingNames.contains( name ) && sqlResultSetMappingMap.containsKey( name ) ) {
 			sqlResultSetMappingMap.remove( name );
 		}
 		applyResultSetMapping( definition );
 		defaultSqlResultSetMappingNames.add( name );
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// imports
 
 	@Override
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	@Override
 	public void addImport(String importName, String entityName) {
 		if ( importName == null || entityName == null ) {
 			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		log.tracev( "Import: {0} -> {1}", importName, entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			log.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Table handling
 
 	@Override
 	public Table addTable(
 			String schemaName,
 			String catalogName,
 			String name,
 			String subselectFragment,
 			boolean isAbstract) {
 		final Namespace namespace = getDatabase().locateNamespace(
 				getDatabase().toIdentifier( catalogName ),
 				getDatabase().toIdentifier( schemaName )
 		);
 
 		// annotation binding depends on the "table name" for @Subselect bindings
 		// being set into the generated table (mainly to avoid later NPE), but for now we need to keep that :(
 		final Identifier logicalName;
 		if ( name != null ) {
 			logicalName = getDatabase().toIdentifier( name );
 		}
 		else {
 			logicalName = null;
 		}
 
 		if ( subselectFragment != null ) {
 			return new Table( namespace, logicalName, subselectFragment, isAbstract );
 		}
 		else {
 			Table table = namespace.locateTable( logicalName );
 			if ( table != null ) {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 				return table;
 			}
 			return namespace.createTable( logicalName, isAbstract );
 		}
 	}
 
 	@Override
 	public Table addDenormalizedTable(
 			String schemaName,
 			String catalogName,
 			String name,
 			boolean isAbstract,
 			String subselectFragment,
 			Table includedTable) throws DuplicateMappingException {
 		final Namespace namespace = getDatabase().locateNamespace(
 				getDatabase().toIdentifier( catalogName ),
 				getDatabase().toIdentifier( schemaName )
 		);
 
 		// annotation binding depends on the "table name" for @Subselect bindings
 		// being set into the generated table (mainly to avoid later NPE), but for now we need to keep that :(
 		final Identifier logicalName;
 		if ( name != null ) {
 			logicalName = getDatabase().toIdentifier( name );
 		}
 		else {
 			logicalName = null;
 		}
 
 		if ( subselectFragment != null ) {
 			return new DenormalizedTable( namespace, logicalName, subselectFragment, isAbstract, includedTable );
 		}
 		else {
 			Table table = namespace.locateTable( logicalName );
 			if ( table != null ) {
 				throw new DuplicateMappingException( DuplicateMappingException.Type.TABLE, logicalName.toString() );
 			}
 			else {
 				table = namespace.createDenormalizedTable( logicalName, isAbstract, includedTable );
 			}
 			return table;
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Mapping impl
 
 	@Override
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		final PersistentClass pc = entityBindingMap.get( entityName );
 		if ( pc == null ) {
 			throw new MappingException( "persistent class not known: " + entityName );
 		}
 		return pc.getIdentifier().getType();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		final PersistentClass pc = entityBindingMap.get( entityName );
 		if ( pc == null ) {
 			throw new MappingException( "persistent class not known: " + entityName );
 		}
 		if ( !pc.hasIdentifierProperty() ) {
 			return null;
 		}
 		return pc.getIdentifierProperty().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		final PersistentClass pc = entityBindingMap.get( entityName );
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
 
 
 	private Map<Identifier,Identifier> logicalToPhysicalTableNameMap = new HashMap<Identifier, Identifier>();
 	private Map<Identifier,Identifier> physicalToLogicalTableNameMap = new HashMap<Identifier, Identifier>();
 
 	@Override
 	public void addTableNameBinding(Identifier logicalName, Table table) {
 		logicalToPhysicalTableNameMap.put( logicalName, table.getNameIdentifier() );
 		physicalToLogicalTableNameMap.put( table.getNameIdentifier(), logicalName );
 	}
 
 	@Override
 	public void addTableNameBinding(String schema, String catalog, String logicalName, String realTableName, Table denormalizedSuperTable) {
 		final Identifier logicalNameIdentifier = getDatabase().toIdentifier( logicalName );
 		final Identifier physicalNameIdentifier = getDatabase().toIdentifier( realTableName );
 
 		logicalToPhysicalTableNameMap.put( logicalNameIdentifier, physicalNameIdentifier );
 		physicalToLogicalTableNameMap.put( physicalNameIdentifier, logicalNameIdentifier );
 	}
 
 	@Override
 	public String getLogicalTableName(Table ownerTable) {
 		final Identifier logicalName = physicalToLogicalTableNameMap.get( ownerTable.getNameIdentifier() );
 		if ( logicalName == null ) {
 			throw new MappingException( "Unable to find physical table: " + ownerTable.getName() );
 		}
 		return logicalName.render();
 	}
 
 	@Override
 	public String getPhysicalTableName(Identifier logicalName) {
 		final Identifier physicalName = logicalToPhysicalTableNameMap.get( logicalName );
 		return physicalName == null ? null : physicalName.render();
 	}
 
 	@Override
 	public String getPhysicalTableName(String logicalName) {
 		return getPhysicalTableName( getDatabase().toIdentifier( logicalName ) );
 	}
 
 	/**
 	 * Internal struct used to maintain xref between physical and logical column
 	 * names for a table.  Mainly this is used to ensure that the defined NamingStrategy
 	 * is not creating duplicate column names.
 	 */
 	private class TableColumnNameBinding implements Serializable {
 		private final String tableName;
 		private Map<Identifier, String> logicalToPhysical = new HashMap<Identifier,String>();
 		private Map<String, Identifier> physicalToLogical = new HashMap<String,Identifier>();
 
 		private TableColumnNameBinding(String tableName) {
 			this.tableName = tableName;
 		}
 
 		public void addBinding(Identifier logicalName, Column physicalColumn) {
 			final String physicalNameString = physicalColumn.getQuotedName( getDatabase().getJdbcEnvironment().getDialect() );
 
 			bindLogicalToPhysical( logicalName, physicalNameString );
 			bindPhysicalToLogical( logicalName, physicalNameString );
 		}
 
 		private void bindLogicalToPhysical(Identifier logicalName, String physicalName) throws DuplicateMappingException {
 			final String existingPhysicalNameMapping = logicalToPhysical.put( logicalName, physicalName );
 			if ( existingPhysicalNameMapping != null ) {
 				final boolean areSame = logicalName.isQuoted()
 						? physicalName.equals( existingPhysicalNameMapping )
 						: physicalName.equalsIgnoreCase( existingPhysicalNameMapping );
 				if ( !areSame ) {
 					throw new DuplicateMappingException(
 							String.format(
 									Locale.ENGLISH,
 									"Table [%s] contains logical column name [%s] referring to multiple physical " +
 											"column names: [%s], [%s]",
 									tableName,
 									logicalName,
 									existingPhysicalNameMapping,
 									physicalName
 							),
 							DuplicateMappingException.Type.COLUMN_BINDING,
 							tableName + "." + logicalName
 					);
 				}
 			}
 		}
 
 		private void bindPhysicalToLogical(Identifier logicalName, String physicalName) throws DuplicateMappingException {
 			final Identifier existingLogicalName = physicalToLogical.put( physicalName, logicalName );
 			if ( existingLogicalName != null && ! existingLogicalName.equals( logicalName ) ) {
 				throw new DuplicateMappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Table [%s] contains physical column name [%s] referred to by multiple physical " +
 										"column names: [%s], [%s]",
 								tableName,
 								physicalName,
 								logicalName,
 								existingLogicalName
 						),
 						DuplicateMappingException.Type.COLUMN_BINDING,
 						tableName + "." + physicalName
 				);
 			}
 		}
 	}
 
 	private Map<Table,TableColumnNameBinding> columnNameBindingByTableMap;
 
 	@Override
 	public void addColumnNameBinding(Table table, String logicalName, Column column) throws DuplicateMappingException {
 		addColumnNameBinding( table, getDatabase().toIdentifier( logicalName ), column );
 	}
 
 	@Override
 	public void addColumnNameBinding(Table table, Identifier logicalName, Column column) throws DuplicateMappingException {
 		TableColumnNameBinding binding = null;
 
 		if ( columnNameBindingByTableMap == null ) {
 			columnNameBindingByTableMap = new HashMap<Table, TableColumnNameBinding>();
 		}
 		else {
 			binding = columnNameBindingByTableMap.get( table );
 		}
 
 		if ( binding == null ) {
 			binding = new TableColumnNameBinding( table.getName() );
 			columnNameBindingByTableMap.put( table, binding );
 		}
 
 		binding.addBinding( logicalName, column );
 	}
 
 	@Override
 	public String getPhysicalColumnName(Table table, String logicalName) throws MappingException {
 		return getPhysicalColumnName( table, getDatabase().toIdentifier( logicalName ) );
 	}
 
 	@Override
 	public String getPhysicalColumnName(Table table, Identifier logicalName) throws MappingException {
 		if ( logicalName == null ) {
 			throw new MappingException( "Logical column name cannot be null" );
 		}
 
 		Table currentTable = table;
 		String physicalName = null;
 
 		while ( currentTable != null ) {
 			final TableColumnNameBinding binding = columnNameBindingByTableMap.get( currentTable );
 			if ( binding != null ) {
 				physicalName = binding.logicalToPhysical.get( logicalName );
 				if ( physicalName != null ) {
 					break;
 				}
 			}
 
 			if ( DenormalizedTable.class.isInstance( currentTable ) ) {
 				currentTable = ( (DenormalizedTable) currentTable ).getIncludedTable();
 			}
 			else {
 				currentTable = null;
 			}
 		}
 
 		if ( physicalName == null ) {
 			throw new MappingException(
 					"Unable to find column with logical name " + logicalName.render() + " in table " + table.getName()
 			);
 		}
 		return physicalName;
 	}
 
 	@Override
 	public String getLogicalColumnName(Table table, String physicalName) throws MappingException {
 		return getLogicalColumnName( table, getDatabase().toIdentifier( physicalName ) );
 	}
 
 
 	@Override
 	public String getLogicalColumnName(Table table, Identifier physicalName) throws MappingException {
 		final String physicalNameString = physicalName.render( getDatabase().getJdbcEnvironment().getDialect() );
 		Identifier logicalName = null;
 
 		Table currentTable = table;
 		while ( currentTable != null ) {
 			final TableColumnNameBinding binding = columnNameBindingByTableMap.get( currentTable );
 
 			if ( binding != null ) {
 				logicalName = binding.physicalToLogical.get( physicalNameString );
 				if ( logicalName != null ) {
 					break;
 				}
 			}
 
 			if ( DenormalizedTable.class.isInstance( currentTable ) ) {
 				currentTable = ( (DenormalizedTable) currentTable ).getIncludedTable();
 			}
 			else {
 				currentTable = null;
 			}
 		}
 
 		if ( logicalName == null ) {
 			throw new MappingException(
 					"Unable to find column with physical name " + physicalNameString + " in table " + table.getName()
 			);
 		}
 		return logicalName.render();
 	}
 
 	@Override
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 		getDatabase().addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 	}
 
 	private final Map<String,AnnotatedClassType> annotatedClassTypeMap = new HashMap<String, AnnotatedClassType>();
 
 	@Override
 	public AnnotatedClassType getClassType(XClass clazz) {
 		AnnotatedClassType type = annotatedClassTypeMap.get( clazz.getName() );
 		if ( type == null ) {
 			return addClassType( clazz );
 		}
 		else {
 			return type;
 		}
 	}
 
 	@Override
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
 		annotatedClassTypeMap.put( clazz.getName(), type );
 		return type;
 	}
 
 	@Override
 	public void addAnyMetaDef(AnyMetaDef defAnn) {
 		if ( anyMetaDefs == null ) {
 			anyMetaDefs = new HashMap<String, AnyMetaDef>();
 		}
 		else {
 			if ( anyMetaDefs.containsKey( defAnn.name() ) ) {
 				throw new AnnotationException( "Two @AnyMetaDef with the same name defined: " + defAnn.name() );
 			}
 		}
 
 		anyMetaDefs.put( defAnn.name(), defAnn );
 	}
 
 	@Override
 	public AnyMetaDef getAnyMetaDef(String name) {
 		if ( anyMetaDefs == null ) {
 			return null;
 		}
 		return anyMetaDefs.get( name );
 	}
 
 
 	@Override
 	public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
 		if ( mappedSuperClasses == null ) {
 			mappedSuperClasses = new HashMap<Class, MappedSuperclass>();
 		}
 		mappedSuperClasses.put( type, mappedSuperclass );
 	}
 
 	@Override
 	public MappedSuperclass getMappedSuperclass(Class type) {
 		if ( mappedSuperClasses == null ) {
 			return null;
 		}
 		return mappedSuperClasses.get( type );
 	}
 
 	@Override
 	public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
 		if ( propertiesAnnotatedWithMapsId == null ) {
 			return null;
 		}
 
 		final Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 		return map == null ? null : map.get( propertyName );
 	}
 
 	@Override
 	public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
 		if ( propertiesAnnotatedWithMapsId == null ) {
 			propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		}
 
 		Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 		if ( map == null ) {
 			map = new HashMap<String, PropertyData>();
 			propertiesAnnotatedWithMapsId.put( entityType, map );
 		}
 		map.put( property.getProperty().getAnnotation( MapsId.class ).value(), property );
 	}
 
 	@Override
 	public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
 		if ( propertiesAnnotatedWithMapsId == null ) {
 			propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		}
 
 		Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 		if ( map == null ) {
 			map = new HashMap<String, PropertyData>();
 			propertiesAnnotatedWithMapsId.put( entityType, map );
 		}
 		map.put( mapsIdValue, property );
 	}
 
 	@Override
 	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
 		if ( propertiesAnnotatedWithIdAndToOne == null ) {
 			return null;
 		}
 
 		final Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 		return map == null ? null : map.get( propertyName );
 	}
 
 	@Override
 	public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
 		if ( propertiesAnnotatedWithIdAndToOne == null ) {
 			propertiesAnnotatedWithIdAndToOne = new HashMap<XClass, Map<String, PropertyData>>();
 		}
 
 		Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 		if ( map == null ) {
 			map = new HashMap<String, PropertyData>();
 			propertiesAnnotatedWithIdAndToOne.put( entityType, map );
 		}
 		map.put( property.getPropertyName(), property );
 	}
 
 	@Override
 	public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
 		if ( mappedByResolver == null ) {
 			mappedByResolver = new HashMap<String, String>();
 		}
 		mappedByResolver.put( entityName + "." + propertyName, inversePropertyName );
 	}
 
 	@Override
 	public String getFromMappedBy(String entityName, String propertyName) {
 		if ( mappedByResolver == null ) {
 			return null;
 		}
 		return mappedByResolver.get( entityName + "." + propertyName );
 	}
 
 	@Override
 	public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
 		if ( propertyRefResolver == null ) {
 			propertyRefResolver = new HashMap<String, String>();
 		}
 		propertyRefResolver.put( entityName + "." + propertyName, propertyRef );
 	}
 
 	@Override
 	public String getPropertyReferencedAssociation(String entityName, String propertyName) {
 		if ( propertyRefResolver == null ) {
 			return null;
 		}
 		return propertyRefResolver.get( entityName + "." + propertyName );
 	}
 
 	private static class DelayedPropertyReferenceHandlerAnnotationImpl implements DelayedPropertyReferenceHandler {
 		public final String referencedClass;
 		public final String propertyName;
 		public final boolean unique;
 
 		public DelayedPropertyReferenceHandlerAnnotationImpl(String referencedClass, String propertyName, boolean unique) {
 			this.referencedClass = referencedClass;
 			this.propertyName = propertyName;
 			this.unique = unique;
 		}
 
 		@Override
 		public void process(InFlightMetadataCollector metadataCollector) {
 			final PersistentClass clazz = metadataCollector.getEntityBinding( referencedClass );
 			if ( clazz == null ) {
 				throw new MappingException( "property-ref to unmapped class: " + referencedClass );
 			}
 
 			final Property prop = clazz.getReferencedProperty( propertyName );
 			if ( unique ) {
 				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 	}
 
 	@Override
 	public void addPropertyReference(String referencedClass, String propertyName) {
 		addDelayedPropertyReferenceHandler(
 				new DelayedPropertyReferenceHandlerAnnotationImpl( referencedClass, propertyName, false )
 		);
 	}
 
 	@Override
 	public void addDelayedPropertyReferenceHandler(DelayedPropertyReferenceHandler handler) {
 		if ( delayedPropertyReferenceHandlers == null ) {
 			delayedPropertyReferenceHandlers = new HashSet<DelayedPropertyReferenceHandler>();
 		}
 		delayedPropertyReferenceHandlers.add( handler );
 	}
 
 	@Override
 	public void addUniquePropertyReference(String referencedClass, String propertyName) {
 		addDelayedPropertyReferenceHandler(
 				new DelayedPropertyReferenceHandlerAnnotationImpl( referencedClass, propertyName, true )
 		);
 	}
 
 	@Override
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
 		List currentHolders = uniqueConstraintHoldersByTable == null ? null : uniqueConstraintHoldersByTable.get( table );
 		return currentHolders == null
 				? 0
 				: currentHolders.size();
 	}
 
 	@Override
 	public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
 		List<UniqueConstraintHolder> holderList = null;
 
 		if ( uniqueConstraintHoldersByTable == null ) {
 			uniqueConstraintHoldersByTable = new HashMap<Table, List<UniqueConstraintHolder>>();
 		}
 		else {
 			holderList = uniqueConstraintHoldersByTable.get( table );
 		}
 
 		if ( holderList == null ) {
 			holderList = new ArrayList<UniqueConstraintHolder>();
 			uniqueConstraintHoldersByTable.put( table, holderList );
 		}
 
 		holderList.addAll( uniqueConstraintHolders );
 	}
 
 	@Override
 	public void addJpaIndexHolders(Table table, List<JPAIndexHolder> holders) {
 		List<JPAIndexHolder> holderList = null;
 
 		if ( jpaIndexHoldersByTable == null ) {
 			jpaIndexHoldersByTable = new HashMap<Table, List<JPAIndexHolder>>();
 		}
 		else {
 			holderList = jpaIndexHoldersByTable.get( table );
 		}
 
 		if ( holderList == null ) {
 			holderList = new ArrayList<JPAIndexHolder>();
 			jpaIndexHoldersByTable.put( table, holderList );
 		}
 
 		holderList.addAll( holders );
 	}
 
 	private final Map<String,EntityTableXrefImpl> entityTableXrefMap = new HashMap<String, EntityTableXrefImpl>();
 
 	@Override
 	public EntityTableXref getEntityTableXref(String entityName) {
 		return entityTableXrefMap.get( entityName );
 	}
 
 	@Override
 	public EntityTableXref addEntityTableXref(
 			String entityName,
 			Identifier primaryTableLogicalName,
 			Table primaryTable,
 			EntityTableXref superEntityTableXref) {
 		final EntityTableXrefImpl entry = new EntityTableXrefImpl(
 				primaryTableLogicalName,
 				primaryTable,
 				(EntityTableXrefImpl) superEntityTableXref
 		);
 
 		entityTableXrefMap.put( entityName, entry );
 
 		return entry;
 	}
 
 	@Override
 	public Map<String, Join> getJoins(String entityName) {
 		EntityTableXrefImpl xrefEntry = entityTableXrefMap.get( entityName );
 		return xrefEntry == null ? null : xrefEntry.secondaryTableJoinMap;
 	}
 
 	private final class EntityTableXrefImpl implements EntityTableXref {
 		private final Identifier primaryTableLogicalName;
 		private final Table primaryTable;
 		private EntityTableXrefImpl superEntityTableXref;
 
 		//annotations needs a Map<String,Join>
 		//private Map<Identifier,Join> secondaryTableJoinMap;
 		private Map<String,Join> secondaryTableJoinMap;
 
 		public EntityTableXrefImpl(Identifier primaryTableLogicalName, Table primaryTable, EntityTableXrefImpl superEntityTableXref) {
 			this.primaryTableLogicalName = primaryTableLogicalName;
 			this.primaryTable = primaryTable;
 			this.superEntityTableXref = superEntityTableXref;
 		}
 
 		@Override
 		public void addSecondaryTable(LocalMetadataBuildingContext buildingContext, Identifier logicalName, Join secondaryTableJoin) {
 			if ( Identifier.areEqual( primaryTableLogicalName, logicalName ) ) {
 				throw new org.hibernate.boot.MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Attempt to add secondary table with same name as primary table [%s]",
 								primaryTableLogicalName
 						),
 						buildingContext.getOrigin()
 				);
 			}
 
 
 			if ( secondaryTableJoinMap == null ) {
 				//secondaryTableJoinMap = new HashMap<Identifier,Join>();
 				//secondaryTableJoinMap.put( logicalName, secondaryTableJoin );
 				secondaryTableJoinMap = new HashMap<String,Join>();
 				secondaryTableJoinMap.put( logicalName.getCanonicalName(), secondaryTableJoin );
 			}
 			else {
 				//final Join existing = secondaryTableJoinMap.put( logicalName, secondaryTableJoin );
 				final Join existing = secondaryTableJoinMap.put( logicalName.getCanonicalName(), secondaryTableJoin );
 
 				if ( existing != null ) {
 					throw new org.hibernate.boot.MappingException(
 							String.format(
 									Locale.ENGLISH,
 									"Added secondary table with same name [%s]",
 									logicalName
 							),
 							buildingContext.getOrigin()
 					);
 				}
 			}
 		}
 
 		@Override
 		public void addSecondaryTable(Identifier logicalName, Join secondaryTableJoin) {
 			if ( Identifier.areEqual( primaryTableLogicalName, logicalName ) ) {
 				throw new DuplicateSecondaryTableException( logicalName );
 			}
 
 
 			if ( secondaryTableJoinMap == null ) {
 				//secondaryTableJoinMap = new HashMap<Identifier,Join>();
 				//secondaryTableJoinMap.put( logicalName, secondaryTableJoin );
 				secondaryTableJoinMap = new HashMap<String,Join>();
 				secondaryTableJoinMap.put( logicalName.getCanonicalName(), secondaryTableJoin );
 			}
 			else {
 				//final Join existing = secondaryTableJoinMap.put( logicalName, secondaryTableJoin );
 				final Join existing = secondaryTableJoinMap.put( logicalName.getCanonicalName(), secondaryTableJoin );
 
 				if ( existing != null ) {
 					throw new DuplicateSecondaryTableException( logicalName );
 				}
 			}
 		}
 
 		@Override
 		public Table getPrimaryTable() {
 			return primaryTable;
 		}
 
 		@Override
 		public Table resolveTable(Identifier tableName) {
 			if ( tableName == null ) {
 				return primaryTable;
 			}
 
 			if ( Identifier.areEqual( primaryTableLogicalName, tableName ) ) {
 				return primaryTable;
 			}
 
 			Join secondaryTableJoin = null;
 			if ( secondaryTableJoinMap != null ) {
 				//secondaryTableJoin = secondaryTableJoinMap.get( tableName );
 				secondaryTableJoin = secondaryTableJoinMap.get( tableName.getCanonicalName() );
 			}
 
 			if ( secondaryTableJoin != null ) {
 				return secondaryTableJoin.getTable();
 			}
 
 			if ( superEntityTableXref != null ) {
 				return superEntityTableXref.resolveTable( tableName );
 			}
 
 			return null;
 		}
 
 		public Join locateJoin(Identifier tableName) {
 			if ( tableName == null ) {
 				return null;
 			}
 
 			Join join = null;
 			if ( secondaryTableJoinMap != null ) {
 				join = secondaryTableJoinMap.get( tableName.getCanonicalName() );
 			}
 
 			if ( join != null ) {
 				return join;
 			}
 
 			if ( superEntityTableXref != null ) {
 				return superEntityTableXref.locateJoin( tableName );
 			}
 
 			return null;
 		}
 	}
 
 	private ArrayList<PkDrivenByDefaultMapsIdSecondPass> pkDrivenByDefaultMapsIdSecondPassList;
 	private ArrayList<SetSimpleValueTypeSecondPass> setSimpleValueTypeSecondPassList;
 	private ArrayList<CopyIdentifierComponentSecondPass> copyIdentifierComponentSecondPasList;
 	private ArrayList<FkSecondPass> fkSecondPassList;
 	private ArrayList<CreateKeySecondPass> createKeySecondPasList;
 	private ArrayList<SecondaryTableSecondPass> secondaryTableSecondPassList;
 	private ArrayList<QuerySecondPass> querySecondPassList;
 	private ArrayList<ConstraintSecondPass> constraintSecondPassList;
 	private ArrayList<ImplicitColumnNamingSecondPass> implicitColumnNamingSecondPassList;
 
 	private ArrayList<SecondPass> generalSecondPassList;
 
 	@Override
 	public void addSecondPass(SecondPass secondPass) {
 		addSecondPass( secondPass, false );
 	}
 
 	@Override
 	public void addSecondPass(SecondPass secondPass, boolean onTopOfTheQueue) {
 		if ( secondPass instanceof PkDrivenByDefaultMapsIdSecondPass ) {
 			addPkDrivenByDefaultMapsIdSecondPass( (PkDrivenByDefaultMapsIdSecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof SetSimpleValueTypeSecondPass ) {
 			addSetSimpleValueTypeSecondPass( (SetSimpleValueTypeSecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof CopyIdentifierComponentSecondPass ) {
 			addCopyIdentifierComponentSecondPass( (CopyIdentifierComponentSecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof FkSecondPass ) {
 			addFkSecondPass( (FkSecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof CreateKeySecondPass ) {
 			addCreateKeySecondPass( (CreateKeySecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof SecondaryTableSecondPass ) {
 			addSecondaryTableSecondPass( (SecondaryTableSecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof QuerySecondPass ) {
 			addQuerySecondPass( (QuerySecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof ConstraintSecondPass ) {
 			addConstraintSecondPass( ( ConstraintSecondPass) secondPass, onTopOfTheQueue );
 		}
 		else if ( secondPass instanceof ImplicitColumnNamingSecondPass ) {
 			addImplicitColumnNamingSecondPass( (ImplicitColumnNamingSecondPass) secondPass );
 		}
 		else {
 			// add to the general SecondPass list
 			if ( generalSecondPassList == null ) {
 				generalSecondPassList = new ArrayList<SecondPass>();
 			}
 			addSecondPass( secondPass, generalSecondPassList, onTopOfTheQueue );
 		}
 	}
 
 	private void addPkDrivenByDefaultMapsIdSecondPass(
 			PkDrivenByDefaultMapsIdSecondPass secondPass,
 			boolean onTopOfTheQueue) {
 		if ( pkDrivenByDefaultMapsIdSecondPassList == null ) {
 			pkDrivenByDefaultMapsIdSecondPassList = new ArrayList<PkDrivenByDefaultMapsIdSecondPass>();
 		}
 		addSecondPass( secondPass, pkDrivenByDefaultMapsIdSecondPassList, onTopOfTheQueue );
 	}
 
 	private <T extends SecondPass> void addSecondPass(T secondPass, ArrayList<T> secondPassList, boolean onTopOfTheQueue) {
 		if ( onTopOfTheQueue ) {
 			secondPassList.add( 0, secondPass );
 		}
 		else {
 			secondPassList.add( secondPass );
 		}
 	}
 
 	private void addSetSimpleValueTypeSecondPass(SetSimpleValueTypeSecondPass secondPass, boolean onTopOfTheQueue) {
 		if ( setSimpleValueTypeSecondPassList == null ) {
 			setSimpleValueTypeSecondPassList = new ArrayList<SetSimpleValueTypeSecondPass>();
 		}
 		addSecondPass( secondPass, setSimpleValueTypeSecondPassList, onTopOfTheQueue );
 	}
 
 	private void addCopyIdentifierComponentSecondPass(
 			CopyIdentifierComponentSecondPass secondPass,
 			boolean onTopOfTheQueue) {
 		if ( copyIdentifierComponentSecondPasList == null ) {
 			copyIdentifierComponentSecondPasList = new ArrayList<CopyIdentifierComponentSecondPass>();
 		}
 		addSecondPass( secondPass, copyIdentifierComponentSecondPasList, onTopOfTheQueue );
 	}
 
 	private void addFkSecondPass(FkSecondPass secondPass, boolean onTopOfTheQueue) {
 		if ( fkSecondPassList == null ) {
 			fkSecondPassList = new ArrayList<FkSecondPass>();
 		}
 		addSecondPass( secondPass, fkSecondPassList, onTopOfTheQueue );
 	}
 
 	private void addCreateKeySecondPass(CreateKeySecondPass secondPass, boolean onTopOfTheQueue) {
 		if ( createKeySecondPasList == null ) {
 			createKeySecondPasList = new ArrayList<CreateKeySecondPass>();
 		}
 		addSecondPass( secondPass, createKeySecondPasList, onTopOfTheQueue );
 	}
 
 	private void addSecondaryTableSecondPass(SecondaryTableSecondPass secondPass, boolean onTopOfTheQueue) {
 		if ( secondaryTableSecondPassList == null ) {
 			secondaryTableSecondPassList = new ArrayList<SecondaryTableSecondPass>();
 		}
 		addSecondPass( secondPass, secondaryTableSecondPassList, onTopOfTheQueue );
 	}
 
 	private void addQuerySecondPass(QuerySecondPass secondPass, boolean onTopOfTheQueue) {
 		if ( querySecondPassList == null ) {
 			querySecondPassList = new ArrayList<QuerySecondPass>();
 		}
 		addSecondPass( secondPass, querySecondPassList, onTopOfTheQueue );
 	}
 
 	private void addConstraintSecondPass(ConstraintSecondPass secondPass, boolean onTopOfTheQueue) {
 		if ( constraintSecondPassList == null ) {
 			constraintSecondPassList = new ArrayList<ConstraintSecondPass>();
 		}
 		addSecondPass( secondPass, constraintSecondPassList, onTopOfTheQueue );
 	}
 
 	private void addImplicitColumnNamingSecondPass(ImplicitColumnNamingSecondPass secondPass) {
 		if ( implicitColumnNamingSecondPassList == null ) {
 			implicitColumnNamingSecondPassList = new ArrayList<ImplicitColumnNamingSecondPass>();
 		}
 		implicitColumnNamingSecondPassList.add( secondPass );
 	}
 
 
 	private boolean inSecondPass = false;
 
 
 	/**
 	 * Ugh!  But we need this done before we ask Envers to produce its entities.
 	 */
 	public void processSecondPasses(MetadataBuildingContext buildingContext) {
 		inSecondPass = true;
 
 		try {
 			processSecondPasses( implicitColumnNamingSecondPassList );
 
 			processSecondPasses( pkDrivenByDefaultMapsIdSecondPassList );
 			processSecondPasses( setSimpleValueTypeSecondPassList );
 			processSecondPasses( copyIdentifierComponentSecondPasList );
 
 			processFkSecondPassesInOrder();
 
 			processSecondPasses( createKeySecondPasList );
 			processSecondPasses( secondaryTableSecondPassList );
 
 			processSecondPasses( querySecondPassList );
 			processSecondPasses( generalSecondPassList );
 
 			processPropertyReferences();
 
 			secondPassCompileForeignKeys( buildingContext );
 
 			processSecondPasses( constraintSecondPassList );
 			processUniqueConstraintHolders( buildingContext );
 			processJPAIndexHolders( buildingContext );
 
 			processNaturalIdUniqueKeyBinders();
 
 			processCachingOverrides();
 		}
 		finally {
 			inSecondPass = false;
 		}
 	}
 
 	private void processSecondPasses(ArrayList<? extends SecondPass> secondPasses) {
 		if ( secondPasses == null ) {
 			return;
 		}
 
 		for ( SecondPass secondPass : secondPasses ) {
 			secondPass.doSecondPass( getEntityBindingMap() );
 		}
 
 		secondPasses.clear();
 	}
 
 	private void processFkSecondPassesInOrder() {
 		if ( fkSecondPassList == null || fkSecondPassList.isEmpty() ) {
 			return;
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPassList.size() );
 		for ( FkSecondPass sp : fkSecondPassList ) {
 			if ( sp.isInPrimaryKey() ) {
 				final String referenceEntityName = sp.getReferencedEntityName();
 				final PersistentClass classMapping = getEntityBinding( referenceEntityName );
 				final String dependentTable = classMapping.getTable().getQualifiedTableName().render();
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
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPassList.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( getEntityBindingMap() );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 
 		fkSecondPassList.clear();
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
 			String dependentTable = sp.getValue().getTable().getQualifiedTableName().render();
 			if ( dependentTable.compareTo( startTable ) == 0 ) {
 				String sb = "Foreign key circularity dependency involving the following tables: ";
 				throw new AnnotationException( sb );
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
 			for ( FkSecondPass pass : endOfQueueFkSecondPasses ) {
 				try {
 					pass.doSecondPass( getEntityBindingMap() );
 				}
 				catch (RecoverableException e) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = (RuntimeException) e.getCause();
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
 
 	private void secondPassCompileForeignKeys(MetadataBuildingContext buildingContext) {
 		int uniqueInteger = 0;
 		Set<ForeignKey> done = new HashSet<ForeignKey>();
 		for ( Table table : collectTableMappings() ) {
 			table.setUniqueInteger( uniqueInteger++ );
 			secondPassCompileForeignKeys( table, done, buildingContext );
 		}
 	}
 
 	protected void secondPassCompileForeignKeys(
 			final Table table,
 			Set<ForeignKey> done,
 			final MetadataBuildingContext buildingContext) throws MappingException {
 		table.createForeignKeys();
 
 		Iterator itr = table.getForeignKeyIterator();
 		while ( itr.hasNext() ) {
 			final ForeignKey fk = (ForeignKey) itr.next();
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
 
 				log.debugf( "Resolving reference to class: %s", referencedEntityName );
 				final PersistentClass referencedClass = getEntityBinding( referencedEntityName );
 				if ( referencedClass == null ) {
 					throw new MappingException(
 							"An association from the table " +
 									fk.getTable().getName() +
 									" refers to an unmapped class: " +
 									referencedEntityName
 					);
 				}
 				if ( referencedClass.isJoinedSubclass() ) {
 					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done, buildingContext );
 				}
 
 				fk.setReferencedTable( referencedClass.getTable() );
 
 				// todo : should we apply a physical naming too?
 				if ( fk.getName() == null ) {
 					final Identifier nameIdentifier = getMetadataBuildingOptions().getImplicitNamingStrategy().determineForeignKeyName(
 							new ImplicitForeignKeyNameSource() {
 								final List<Identifier> columnNames = extractColumnNames( fk.getColumns() );
 								List<Identifier> referencedColumnNames = null;
 
 								@Override
 								public Identifier getTableName() {
 									return table.getNameIdentifier();
 								}
 
 								@Override
 								public List<Identifier> getColumnNames() {
 									return columnNames;
 								}
 
 								@Override
 								public Identifier getReferencedTableName() {
 									return fk.getReferencedTable().getNameIdentifier();
 								}
 
 								@Override
 								public List<Identifier> getReferencedColumnNames() {
 									if ( referencedColumnNames == null ) {
 										referencedColumnNames = extractColumnNames( fk.getReferencedColumns() );
 									}
 									return referencedColumnNames;
 								}
 
 								@Override
 								public MetadataBuildingContext getBuildingContext() {
 									return buildingContext;
 								}
 							}
 					);
 
 					fk.setName( nameIdentifier.render( getDatabase().getJdbcEnvironment().getDialect() ) );
 				}
 
 				fk.alignColumns();
 			}
 		}
 	}
 
 	private List<Identifier> toIdentifiers(List<String> names) {
 		if ( names == null || names.isEmpty() ) {
 			return Collections.emptyList();
 		}
 
 		final List<Identifier> columnNames = CollectionHelper.arrayList( names.size() );
 		for ( String name : names ) {
 			columnNames.add( getDatabase().toIdentifier( name ) );
 		}
 		return columnNames;
 	}
 
 	private List<Identifier> toIdentifiers(String[] names) {
 		if ( names == null ) {
 			return Collections.emptyList();
 		}
 
 		final List<Identifier> columnNames = CollectionHelper.arrayList( names.length );
 		for ( String name : names ) {
 			columnNames.add( getDatabase().toIdentifier( name ) );
 		}
 		return columnNames;
 	}
 
 	@SuppressWarnings("unchecked")
 	private List<Identifier> extractColumnNames(List columns) {
 		if ( columns == null || columns.isEmpty() ) {
 			return Collections.emptyList();
 		}
 
 		final List<Identifier> columnNames = CollectionHelper.arrayList( columns.size() );
 		for ( Column column : (List<Column>) columns ) {
 			columnNames.add( getDatabase().toIdentifier( column.getQuotedName() ) );
 		}
 		return columnNames;
 
 	}
 
 	private void processPropertyReferences() {
 		if ( delayedPropertyReferenceHandlers == null ) {
 			return;
 		}
 		log.debug( "Processing association property references" );
 
 		for ( DelayedPropertyReferenceHandler delayedPropertyReferenceHandler : delayedPropertyReferenceHandlers ) {
 			delayedPropertyReferenceHandler.process( this );
 		}
 
 		delayedPropertyReferenceHandlers.clear();
 	}
 
 	private void processUniqueConstraintHolders(MetadataBuildingContext buildingContext) {
 		if ( uniqueConstraintHoldersByTable == null ) {
 			return;
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				buildUniqueKeyFromColumnNames( table, holder.getName(), holder.getColumns(), buildingContext );
 			}
 		}
 
 		uniqueConstraintHoldersByTable.clear();
 	}
 
 	private void buildUniqueKeyFromColumnNames(
 			Table table,
 			String keyName,
 			String[] columnNames,
 			MetadataBuildingContext buildingContext) {
 		buildUniqueKeyFromColumnNames( table, keyName, columnNames, null, true, buildingContext );
 	}
 
 	private void buildUniqueKeyFromColumnNames(
 			final Table table,
 			String keyName,
 			final String[] columnNames,
 			String[] orderings,
 			boolean unique,
 			final MetadataBuildingContext buildingContext) {
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = columnNames[index];
 			try {
 				final String physicalColumnName = getPhysicalColumnName( table, logicalColumnName );
 				columns[index] = new Column( physicalColumnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				// If at least 1 columnName does exist, 'columns' will contain a mix of Columns and nulls.  In order
 				// to exhaustively report all of the unbound columns at once, w/o an NPE in
 				// Constraint#generateName's array sorting, simply create a fake Column.
 				columns[index] = new Column( logicalColumnName );
 				unboundNoLogical.add( columns[index] );
 			}
 		}
 
 		if ( unique ) {
 			if ( StringHelper.isEmpty( keyName ) ) {
 				final Identifier keyNameIdentifier = getMetadataBuildingOptions().getImplicitNamingStrategy().determineUniqueKeyName(
 						new ImplicitUniqueKeyNameSource() {
 							@Override
 							public MetadataBuildingContext getBuildingContext() {
 								return buildingContext;
 							}
 
 							@Override
 							public Identifier getTableName() {
 								return table.getNameIdentifier();
 							}
 
 							private List<Identifier> columnNameIdentifiers;
 
 							@Override
 							public List<Identifier> getColumnNames() {
 								// be lazy about building these
 								if ( columnNameIdentifiers == null ) {
 									columnNameIdentifiers = toIdentifiers( columnNames );
 								}
 								return columnNameIdentifiers;
 							}
 						}
 				);
 				keyName = keyNameIdentifier.render( getDatabase().getJdbcEnvironment().getDialect() );
 			}
 
 			UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					uk.addColumn( column, order );
 					unbound.remove( column );
 				}
 			}
 		}
 		else {
 			if ( StringHelper.isEmpty( keyName ) ) {
 				final Identifier keyNameIdentifier = getMetadataBuildingOptions().getImplicitNamingStrategy().determineIndexName(
 						new ImplicitIndexNameSource() {
 							@Override
 							public MetadataBuildingContext getBuildingContext() {
 								return buildingContext;
 							}
 
 							@Override
 							public Identifier getTableName() {
 								return table.getNameIdentifier();
 							}
 
 							private List<Identifier> columnNameIdentifiers;
 
 							@Override
 							public List<Identifier> getColumnNames() {
 								// be lazy about building these
 								if ( columnNameIdentifiers == null ) {
 									columnNameIdentifiers = toIdentifiers( columnNames );
 								}
 								return columnNameIdentifiers;
 							}
 						}
 				);
 				keyName = keyNameIdentifier.render( getDatabase().getJdbcEnvironment().getDialect() );
 			}
 
 			Index index = table.getOrCreateIndex( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					index.addColumn( column, order );
 					unbound.remove( column );
 				}
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
 				sb.append("'").append( column.getName() ).append( "', " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append("'").append( column.getName() ).append( "', " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void processJPAIndexHolders(MetadataBuildingContext buildingContext) {
 		if ( jpaIndexHoldersByTable == null ) {
 			return;
 		}
 
 		for ( Table table : jpaIndexHoldersByTable.keySet() ) {
 			final List<JPAIndexHolder> jpaIndexHolders = jpaIndexHoldersByTable.get( table );
 			for ( JPAIndexHolder holder : jpaIndexHolders ) {
 				buildUniqueKeyFromColumnNames(
 						table,
 						holder.getName(),
 						holder.getColumns(),
 						holder.getOrdering(),
 						holder.isUnique(),
 						buildingContext
 				);
 			}
 		}
 	}
 
 	private Map<String,NaturalIdUniqueKeyBinder> naturalIdUniqueKeyBinderMap;
 
 	@Override
 	public NaturalIdUniqueKeyBinder locateNaturalIdUniqueKeyBinder(String entityName) {
 		if ( naturalIdUniqueKeyBinderMap == null ) {
 			return null;
 		}
 		return naturalIdUniqueKeyBinderMap.get( entityName );
 	}
 
 	@Override
 	public void registerNaturalIdUniqueKeyBinder(String entityName, NaturalIdUniqueKeyBinder ukBinder) {
 		if ( naturalIdUniqueKeyBinderMap == null ) {
 			naturalIdUniqueKeyBinderMap = new HashMap<String, NaturalIdUniqueKeyBinder>();
 		}
 		final NaturalIdUniqueKeyBinder previous = naturalIdUniqueKeyBinderMap.put( entityName, ukBinder );
 		if ( previous != null ) {
 			throw new AssertionFailure( "Previous NaturalIdUniqueKeyBinder already registered for entity name : " + entityName );
 		}
 	}
 
 	private void processNaturalIdUniqueKeyBinders() {
 		if ( naturalIdUniqueKeyBinderMap == null ) {
 			return;
 		}
 
 		for ( NaturalIdUniqueKeyBinder naturalIdUniqueKeyBinder : naturalIdUniqueKeyBinderMap.values() ) {
 			naturalIdUniqueKeyBinder.process();
 		}
 
 		naturalIdUniqueKeyBinderMap.clear();
 	}
 
 	private void processCachingOverrides() {
 		if ( options.getCacheRegionDefinitions() == null ) {
 			return;
 		}
 
 		for ( CacheRegionDefinition cacheRegionDefinition : options.getCacheRegionDefinitions() ) {
 			if ( cacheRegionDefinition.getRegionType() == CacheRegionDefinition.CacheRegionType.ENTITY ) {
 				final PersistentClass entityBinding = getEntityBinding( cacheRegionDefinition.getRole() );
 				if ( entityBinding == null ) {
 					throw new HibernateException(
 							"Cache override referenced an unknown entity : " + cacheRegionDefinition.getRole()
 					);
 				}
 				if ( !RootClass.class.isInstance( entityBinding ) ) {
 					throw new HibernateException(
 							"Cache override referenced a non-root entity : " + cacheRegionDefinition.getRole()
 					);
 				}
 				( (RootClass) entityBinding ).setCacheRegionName( cacheRegionDefinition.getRegion() );
 				( (RootClass) entityBinding ).setCacheConcurrencyStrategy( cacheRegionDefinition.getUsage() );
 				( (RootClass) entityBinding ).setLazyPropertiesCacheable( cacheRegionDefinition.isCacheLazy() );
 			}
 			else if ( cacheRegionDefinition.getRegionType() == CacheRegionDefinition.CacheRegionType.COLLECTION ) {
 				final Collection collectionBinding = getCollectionBinding( cacheRegionDefinition.getRole() );
 				if ( collectionBinding == null ) {
 					throw new HibernateException(
 							"Cache override referenced an unknown collection role : " + cacheRegionDefinition.getRole()
 					);
 				}
 				collectionBinding.setCacheRegionName( cacheRegionDefinition.getRegion() );
 				collectionBinding.setCacheConcurrencyStrategy( cacheRegionDefinition.getUsage() );
 			}
 		}
 	}
 
 	@Override
 	public boolean isInSecondPass() {
 		return inSecondPass;
 	}
 
 	/**
 	 * Builds the complete and immutable Metadata instance from the collected info.
 	 *
 	 * @return The complete and immutable Metadata instance
 	 */
 	public MetadataImpl buildMetadataInstance(MetadataBuildingContext buildingContext) {
 		processSecondPasses( buildingContext );
 		processExportableProducers( buildingContext );
 
-		return new MetadataImpl(
-				uuid,
-				options,
-				typeResolver,
-				identifierGeneratorFactory,
-				entityBindingMap,
-				mappedSuperClasses,
-				collectionBindingMap,
-				typeDefinitionMap,
-				filterDefinitionMap,
-				fetchProfileMap,
-				imports,
-				idGeneratorDefinitionMap,
-				namedQueryMap,
-				namedNativeQueryMap,
-				namedProcedureCallMap,
-				sqlResultSetMappingMap,
-				namedEntityGraphMap,
-				sqlFunctionMap,
-				getDatabase()
-		);
+		try {
+			return new MetadataImpl(
+					uuid,
+					options,
+					typeResolver,
+					identifierGeneratorFactory,
+					entityBindingMap,
+					mappedSuperClasses,
+					collectionBindingMap,
+					typeDefinitionMap,
+					filterDefinitionMap,
+					fetchProfileMap,
+					imports,
+					idGeneratorDefinitionMap,
+					namedQueryMap,
+					namedNativeQueryMap,
+					namedProcedureCallMap,
+					sqlResultSetMappingMap,
+					namedEntityGraphMap,
+					sqlFunctionMap,
+					getDatabase()
+			);
+		}
+		finally {
+			classmateContext.release();
+		}
 	}
 
 	private void processExportableProducers(MetadataBuildingContext buildingContext) {
 		// for now we only handle id generators as ExportableProducers
 
 		final Dialect dialect = getDatabase().getJdbcEnvironment().getDialect();
 		final String defaultCatalog = extractName( getDatabase().getDefaultNamespace().getName().getCatalog(), dialect );
 		final String defaultSchema = extractName( getDatabase().getDefaultNamespace().getName().getSchema(), dialect );
 
 		for ( PersistentClass entityBinding : entityBindingMap.values() ) {
 			if ( entityBinding.isInherited() ) {
 				continue;
 			}
 
 			handleIdentifierValueBinding(
 					entityBinding.getIdentifier(),
 					dialect,
 					defaultCatalog,
 					defaultSchema,
 					(RootClass) entityBinding
 			);
 		}
 
 		for ( Collection collection : collectionBindingMap.values() ) {
 			if ( !IdentifierCollection.class.isInstance( collection ) ) {
 				continue;
 			}
 
 			handleIdentifierValueBinding(
 					( (IdentifierCollection) collection ).getIdentifier(),
 					dialect,
 					defaultCatalog,
 					defaultSchema,
 					null
 			);
 		}
 	}
 
 	private void handleIdentifierValueBinding(
 			KeyValue identifierValueBinding,
 			Dialect dialect,
 			String defaultCatalog,
 			String defaultSchema,
 			RootClass entityBinding) {
 		// todo : store this result (back into the entity or into the KeyValue, maybe?)
 		// 		This process of instantiating the id-generator is called multiple times.
 		//		It was done this way in the old code too, so no "regression" here; but
 		//		it could be done better
 		try {
 			final IdentifierGenerator ig = identifierValueBinding.createIdentifierGenerator(
 					getIdentifierGeneratorFactory(),
 					dialect,
 					defaultCatalog,
 					defaultSchema,
 					entityBinding
 			);
 
 			if ( ig instanceof ExportableProducer ) {
 				( (ExportableProducer) ig ).registerExportables( getDatabase() );
 			}
 		}
 		catch (MappingException e) {
 			// ignore this for now.  The reasoning being "non-reflective" binding as needed
 			// by tools.  We want to hold off requiring classes being present until we
 			// try to build a SF.  Here, just building the Metadata, it is "ok" for an
 			// exception to occur, the same exception will happen later as we build the SF.
 			log.debugf( "Ignoring exception thrown when trying to build IdentifierGenerator as part of Metadata building", e );
 		}
 	}
 
 	private String extractName(Identifier identifier, Dialect dialect) {
 		if ( identifier == null ) {
 			return null;
 		}
 		return identifier.render( dialect );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/AttributeConverterAutoApplyHandler.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/AttributeConverterAutoApplyHandler.java
new file mode 100644
index 0000000000..723a8bcea9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/AttributeConverterAutoApplyHandler.java
@@ -0,0 +1,18 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.boot.spi;
+
+import org.hibernate.annotations.common.reflection.XProperty;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface AttributeConverterAutoApplyHandler {
+	AttributeConverterDescriptor findAutoApplyConverterForAttribute(XProperty xProperty, MetadataBuildingContext context);
+	AttributeConverterDescriptor findAutoApplyConverterForCollectionElement(XProperty xProperty, MetadataBuildingContext context);
+	AttributeConverterDescriptor findAutoApplyConverterForMapKey(XProperty xProperty, MetadataBuildingContext context);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/AttributeConverterDescriptor.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/AttributeConverterDescriptor.java
new file mode 100644
index 0000000000..0a41c9469d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/AttributeConverterDescriptor.java
@@ -0,0 +1,27 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.boot.spi;
+
+import javax.persistence.AttributeConverter;
+
+import org.hibernate.annotations.common.reflection.XProperty;
+
+/**
+ * Internal descriptor for an AttributeConverter implementation.
+ *
+ * @author Steve Ebersole
+ */
+public interface AttributeConverterDescriptor {
+	AttributeConverter getAttributeConverter();
+	Class<?> getDomainType();
+	Class<?> getJdbcType();
+
+	boolean shouldAutoApplyToAttribute(XProperty xProperty, MetadataBuildingContext context);
+	boolean shouldAutoApplyToCollectionElement(XProperty xProperty, MetadataBuildingContext context);
+	boolean shouldAutoApplyToMapKey(XProperty xProperty, MetadataBuildingContext context);
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java
index a932bbaab8..1b9a89fe02 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/InFlightMetadataCollector.java
@@ -1,326 +1,331 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.boot.spi;
 
 import java.io.Serializable;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.XClass;
+import org.hibernate.boot.internal.ClassmateContext;
 import org.hibernate.boot.model.IdentifierGeneratorDefinition;
 import org.hibernate.boot.model.TypeDefinition;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Database;
 import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.JPAIndexHolder;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.TypeResolver;
 
 /**
  * An in-flight representation of Metadata while Metadata is being built.
  *
  * @author Steve Ebersole
  *
  * @since 5.0
  */
 public interface InFlightMetadataCollector extends Mapping, MetadataImplementor {
 
 	/**
 	 * Add the PersistentClass for an entity mapping.
 	 *
 	 * @param persistentClass The entity metadata
 	 *
 	 * @throws DuplicateMappingException Indicates there was already an entry
 	 * corresponding to the given entity name.
 	 */
 	void addEntityBinding(PersistentClass persistentClass) throws DuplicateMappingException;
 
 	/**
 	 * Needed for SecondPass handling
 	 */
 	Map<String, PersistentClass> getEntityBindingMap();
 
 	/**
 	 * Adds an import (HQL entity rename).
 	 *
 	 * @param entityName The entity name being renamed.
 	 * @param rename The rename
 	 *
 	 * @throws DuplicateMappingException If rename already is mapped to another
 	 * entity name in this repository.
 	 */
 	void addImport(String entityName, String rename) throws DuplicateMappingException;
 
 	/**
 	 * Add collection mapping metadata to this repository.
 	 *
 	 * @param collection The collection metadata
 	 *
 	 * @throws DuplicateMappingException Indicates there was already an entry
 	 * corresponding to the given collection role
 	 */
 	void addCollectionBinding(Collection collection) throws DuplicateMappingException;
 
 	/**
 	 * Adds table metadata to this repository returning the created
 	 * metadata instance.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @param subselect A select statement which defines a logical table, much
 	 * like a DB view.
 	 * @param isAbstract Is the table abstract (i.e. not really existing in the DB)?
 	 *
 	 * @return The created table metadata, or the existing reference.
 	 */
 	Table addTable(String schema, String catalog, String name, String subselect, boolean isAbstract);
 
 	/**
 	 * Adds a 'denormalized table' to this repository.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @param isAbstract Is the table abstract (i.e. not really existing in the DB)?
 	 * @param subselect A select statement which defines a logical table, much
 	 * like a DB view.
 	 * @param includedTable ???
 	 *
 	 * @return The created table metadata.
 	 *
 	 * @throws DuplicateMappingException If such a table mapping already exists.
 	 */
 	Table addDenormalizedTable(
 			String schema,
 			String catalog,
 			String name,
 			boolean isAbstract,
 			String subselect,
 			Table includedTable) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named query to this repository.
 	 *
 	 * @param query The metadata
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	void addNamedQuery(NamedQueryDefinition query) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named SQL query to this repository.
 	 *
 	 * @param query The metadata
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	void addNamedNativeQuery(NamedSQLQueryDefinition query) throws DuplicateMappingException;
 
 	/**
 	 * Adds the metadata for a named SQL result set mapping to this repository.
 	 *
 	 * @param sqlResultSetMapping The metadata
 	 *
 	 * @throws DuplicateMappingException If metadata for another SQL result mapping was
 	 * already found under the given name.
 	 */
 	void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named stored procedure call to this repository.
 	 *
 	 * @param definition The procedure call information
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named entity graph to this repository
 	 *
 	 * @param namedEntityGraphDefinition The procedure call information
 	 *
 	 * @throws DuplicateMappingException If an entity graph already exists with that name.
 	 */
 	void addNamedEntityGraph(NamedEntityGraphDefinition namedEntityGraphDefinition);
 
 	/**
 	 * Adds a type definition to this metadata repository.
 	 *
 	 * @param typeDefinition The named type definition to add.
 	 *
 	 * @throws DuplicateMappingException If a TypeDefinition already exists with that name.
 	 */
 	void addTypeDefinition(TypeDefinition typeDefinition);
 
 	/**
 	 * Adds a filter definition to this repository.
 	 *
 	 * @param definition The filter definition to add.
 	 *
 	 * @throws DuplicateMappingException If a FilterDefinition already exists with that name.
 	 */
 	void addFilterDefinition(FilterDefinition definition);
 
 	/**
 	 * Add metadata pertaining to an auxiliary database object to this repository.
 	 *
 	 * @param auxiliaryDatabaseObject The metadata.
 	 */
 	void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
 	void addFetchProfile(FetchProfile profile);
 
 	TypeResolver getTypeResolver();
 
 	Database getDatabase();
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// make sure these are account for better in metamodel
 
 	void addIdentifierGenerator(IdentifierGeneratorDefinition generatorDefinition);
 
 
 	void addAttributeConverter(AttributeConverterDefinition converter);
 	void addAttributeConverter(Class<? extends AttributeConverter> converterClass);
-	java.util.Collection<AttributeConverterDefinition> getAttributeConverters();
+
+	AttributeConverterAutoApplyHandler getAttributeConverterAutoApplyHandler();
+
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// second passes
 
 	void addSecondPass(SecondPass secondPass);
 
 	void addSecondPass(SecondPass sp, boolean onTopOfTheQueue);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff needed for annotation binding :(
 
 	void addTableNameBinding(Identifier logicalName, Table table);
 	void addTableNameBinding(
 			String schema,
 			String catalog,
 			String logicalName,
 			String realTableName,
 			Table denormalizedSuperTable);
 	String getLogicalTableName(Table ownerTable);
 	String getPhysicalTableName(Identifier logicalName);
 	String getPhysicalTableName(String logicalName);
 
 	void addColumnNameBinding(Table table, Identifier logicalColumnName, Column column);
 	void addColumnNameBinding(Table table, String logicalColumnName, Column column);
 	String getPhysicalColumnName(Table table, Identifier logicalName) throws MappingException;
 	String getPhysicalColumnName(Table table, String logicalName) throws MappingException;
 	String getLogicalColumnName(Table table, Identifier physicalName);
 	String getLogicalColumnName(Table table, String physicalName);
 
 	void addDefaultIdentifierGenerator(IdentifierGeneratorDefinition generatorDefinition);
 
 	void addDefaultQuery(NamedQueryDefinition queryDefinition);
 
 	void addDefaultNamedNativeQuery(NamedSQLQueryDefinition query);
 
 	void addDefaultResultSetMapping(ResultSetMappingDefinition definition);
 
 	void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition procedureCallDefinition);
 
 	void addAnyMetaDef(AnyMetaDef defAnn);
 	AnyMetaDef getAnyMetaDef(String anyMetaDefName);
 
 	AnnotatedClassType addClassType(XClass clazz);
 	AnnotatedClassType getClassType(XClass clazz);
 
 	void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass);
 	MappedSuperclass getMappedSuperclass(Class type);
 
 	PropertyData getPropertyAnnotatedWithMapsId(XClass persistentXClass, String propertyName);
 	void addPropertyAnnotatedWithMapsId(XClass entity, PropertyData propertyAnnotatedElement);
 	void addPropertyAnnotatedWithMapsIdSpecj(XClass entity, PropertyData specJPropertyData, String s);
 
 	void addToOneAndIdProperty(XClass entity, PropertyData propertyAnnotatedElement);
 	PropertyData getPropertyAnnotatedWithIdAndToOne(XClass persistentXClass, String propertyName);
 
 	boolean isInSecondPass();
 
 	NaturalIdUniqueKeyBinder locateNaturalIdUniqueKeyBinder(String entityName);
 	void registerNaturalIdUniqueKeyBinder(String entityName, NaturalIdUniqueKeyBinder ukBinder);
 
+	ClassmateContext getClassmateContext();
+
 	interface DelayedPropertyReferenceHandler extends Serializable {
 		void process(InFlightMetadataCollector metadataCollector);
 	}
 	void addDelayedPropertyReferenceHandler(DelayedPropertyReferenceHandler handler);
 	void addPropertyReference(String entityName, String propertyName);
 	void addUniquePropertyReference(String entityName, String propertyName);
 
 	void addPropertyReferencedAssociation(String s, String propertyName, String syntheticPropertyName);
 	String getPropertyReferencedAssociation(String entityName, String mappedBy);
 
 	void addMappedBy(String name, String mappedBy, String propertyName);
 	String getFromMappedBy(String ownerEntityName, String propertyName);
 
 	void addUniqueConstraints(Table table, List uniqueConstraints);
 	void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraints);
 	void addJpaIndexHolders(Table table, List<JPAIndexHolder> jpaIndexHolders);
 
 
 	interface EntityTableXref {
 		void addSecondaryTable(LocalMetadataBuildingContext buildingContext, Identifier logicalName, Join secondaryTableJoin);
 		void addSecondaryTable(Identifier logicalName, Join secondaryTableJoin);
 		Table resolveTable(Identifier tableName);
 		Table getPrimaryTable();
 		Join locateJoin(Identifier tableName);
 	}
 
 	class DuplicateSecondaryTableException extends HibernateException {
 		private final Identifier tableName;
 
 		public DuplicateSecondaryTableException(Identifier tableName) {
 			super(
 					String.format(
 							Locale.ENGLISH,
 							"Table with that name [%s] already associated with entity",
 							tableName.render()
 					)
 			);
 			this.tableName = tableName;
 		}
 	}
 
 	EntityTableXref getEntityTableXref(String entityName);
 	EntityTableXref addEntityTableXref(
 			String entityName,
 			Identifier primaryTableLogicalName,
 			Table primaryTable,
 			EntityTableXref superEntityTableXref);
 	Map<String,Join> getJoins(String entityName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
index a3b24c3628..1d73a95aea 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AbstractPropertyHolder.java
@@ -1,460 +1,449 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import java.util.HashMap;
 import java.util.Map;
 import javax.persistence.AssociationOverride;
 import javax.persistence.AssociationOverrides;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.Column;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.MappedSuperclass;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.boot.internal.AttributeConverterDescriptorImpl;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.type.PrimitiveWrapperHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * No idea.
  *
  * @author Emmanuel Bernard
  */
 public abstract class AbstractPropertyHolder implements PropertyHolder {
 	private static final Logger log = CoreLogging.logger( AbstractPropertyHolder.class );
 
 	protected AbstractPropertyHolder parent;
 	private Map<String, Column[]> holderColumnOverride;
 	private Map<String, Column[]> currentPropertyColumnOverride;
 	private Map<String, JoinColumn[]> holderJoinColumnOverride;
 	private Map<String, JoinColumn[]> currentPropertyJoinColumnOverride;
 	private Map<String, JoinTable> holderJoinTableOverride;
 	private Map<String, JoinTable> currentPropertyJoinTableOverride;
 	private String path;
 	private MetadataBuildingContext context;
 	private Boolean isInIdClass;
 
 	AbstractPropertyHolder(
 			String path,
 			PropertyHolder parent,
 			XClass clazzToProcess,
 			MetadataBuildingContext context) {
 		this.path = path;
 		this.parent = (AbstractPropertyHolder) parent;
 		this.context = context;
 		buildHierarchyColumnOverride( clazzToProcess );
 	}
 
 	protected abstract String normalizeCompositePathForLogging(String attributeName);
 	protected abstract String normalizeCompositePath(String attributeName);
 
 	protected abstract AttributeConversionInfo locateAttributeConversionInfo(XProperty property);
 	protected abstract AttributeConversionInfo locateAttributeConversionInfo(String path);
 
 	@Override
-	public AttributeConverterDefinition resolveAttributeConverterDefinition(XProperty property) {
+	public AttributeConverterDescriptor resolveAttributeConverterDescriptor(XProperty property) {
 		AttributeConversionInfo info = locateAttributeConversionInfo( property );
 		if ( info != null ) {
 			if ( info.isConversionDisabled() ) {
 				return null;
 			}
 			else {
 				try {
-					return makeAttributeConverterDefinition( info );
+					return makeAttributeConverterDescriptor( info );
 				}
 				catch (Exception e) {
 					throw new IllegalStateException(
 							String.format( "Unable to instantiate AttributeConverter [%s", info.getConverterClass().getName() ),
 							e
 					);
 				}
 			}
 		}
 
 		log.debugf( "Attempting to locate auto-apply AttributeConverter for property [%s:%s]", path, property.getName() );
 
-		final Class propertyType = context.getBuildingOptions().getReflectionManager().toClass( property.getType() );
-		for ( AttributeConverterDefinition attributeConverterDefinition : context.getMetadataCollector().getAttributeConverters() ) {
-			if ( ! attributeConverterDefinition.isAutoApply() ) {
-				continue;
-			}
-			log.debugf(
-					"Checking auto-apply AttributeConverter [%s] type [%s] for match [%s]",
-					attributeConverterDefinition.toString(),
-					attributeConverterDefinition.getEntityAttributeType().getSimpleName(),
-					propertyType.getSimpleName()
-			);
-			if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), propertyType ) ) {
-				return attributeConverterDefinition;
-			}
-		}
-
-		return null;
+		return context.getMetadataCollector()
+				.getAttributeConverterAutoApplyHandler()
+				.findAutoApplyConverterForAttribute( property, context );
 	}
 
-	protected AttributeConverterDefinition makeAttributeConverterDefinition(AttributeConversionInfo conversion) {
+	protected AttributeConverterDescriptor makeAttributeConverterDescriptor(AttributeConversionInfo conversion) {
 		try {
-			return new AttributeConverterDefinition( conversion.getConverterClass().newInstance(), false );
+			AttributeConverterDefinition definition = new AttributeConverterDefinition( conversion.getConverterClass().newInstance(), false );
+			return AttributeConverterDescriptorImpl.create( definition, context.getMetadataCollector().getClassmateContext() );
 		}
 		catch (Exception e) {
 			throw new AnnotationException( "Unable to create AttributeConverter instance", e );
 		}
 	}
 
 	protected boolean areTypeMatch(Class converterDefinedType, Class propertyType) {
 		if ( converterDefinedType == null ) {
 			throw new AnnotationException( "AttributeConverter defined java type cannot be null" );
 		}
 		if ( propertyType == null ) {
 			throw new AnnotationException( "Property defined java type cannot be null" );
 		}
 
 		return converterDefinedType.equals( propertyType )
 				|| PrimitiveWrapperHelper.arePrimitiveWrapperEquivalents( converterDefinedType, propertyType );
 	}
 
 	@Override
 	public boolean isInIdClass() {
 		return isInIdClass != null ? isInIdClass : parent != null ? parent.isInIdClass() : false;
 	}
 
 	@Override
 	public void setInIdClass(Boolean isInIdClass) {
 		this.isInIdClass = isInIdClass;
 	}
 
 	@Override
 	public String getPath() {
 		return path;
 	}
 
 	/**
 	 * Get the mappings
 	 *
 	 * @return The mappings
 	 */
 	protected MetadataBuildingContext getContext() {
 		return context;
 	}
 
 	/**
 	 * Set the property be processed.  property can be null
 	 *
 	 * @param property The property
 	 */
 	protected void setCurrentProperty(XProperty property) {
 		if ( property == null ) {
 			this.currentPropertyColumnOverride = null;
 			this.currentPropertyJoinColumnOverride = null;
 			this.currentPropertyJoinTableOverride = null;
 		}
 		else {
 			this.currentPropertyColumnOverride = buildColumnOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyColumnOverride.size() == 0 ) {
 				this.currentPropertyColumnOverride = null;
 			}
 			this.currentPropertyJoinColumnOverride = buildJoinColumnOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyJoinColumnOverride.size() == 0 ) {
 				this.currentPropertyJoinColumnOverride = null;
 			}
 			this.currentPropertyJoinTableOverride = buildJoinTableOverride(
 					property,
 					getPath()
 			);
 			if ( this.currentPropertyJoinTableOverride.size() == 0 ) {
 				this.currentPropertyJoinTableOverride = null;
 			}
 		}
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	@Override
 	public Column[] getOverriddenColumn(String propertyName) {
 		Column[] result = getExactOverriddenColumn( propertyName );
 		if (result == null) {
 			//the commented code can be useful if people use the new prefixes on old mappings and vice versa
 			// if we enable them:
 			// WARNING: this can conflict with user's expectations if:
 	 		//  - the property uses some restricted values
 	 		//  - the user has overridden the column
 			// also change getOverriddenJoinColumn and getOverriddenJoinTable as well
 	 		
 //			if ( propertyName.contains( ".key." ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn( propertyName.replace( ".key.", ".index."  ) );
 //			}
 //			if ( result == null && propertyName.endsWith( ".key" ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn(
 //						propertyName.substring( 0, propertyName.length() - ".key".length() ) + ".index"
 //						);
 //			}
 //			if ( result == null && propertyName.contains( ".value." ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn( propertyName.replace( ".value.", ".element."  ) );
 //			}
 //			if ( result == null && propertyName.endsWith( ".value" ) ) {
 //				//support for legacy @AttributeOverride declarations
 //				//TODO cache the underlying regexp
 //				result = getExactOverriddenColumn(
 //						propertyName.substring( 0, propertyName.length() - ".value".length() ) + ".element"
 //						);
 //			}
 			if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 				//support for non map collections where no prefix is needed
 				//TODO cache the underlying regexp
 				result = getExactOverriddenColumn( propertyName.replace( ".collection&&element.", "."  ) );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * find the overridden rules from the exact property name.
 	 */
 	private Column[] getExactOverriddenColumn(String propertyName) {
 		Column[] override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenColumn( propertyName );
 		}
 		if ( override == null && currentPropertyColumnOverride != null ) {
 			override = currentPropertyColumnOverride.get( propertyName );
 		}
 		if ( override == null && holderColumnOverride != null ) {
 			override = holderColumnOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	@Override
 	public JoinColumn[] getOverriddenJoinColumn(String propertyName) {
 		JoinColumn[] result = getExactOverriddenJoinColumn( propertyName );
 		if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 			//support for non map collections where no prefix is needed
 			//TODO cache the underlying regexp
 			result = getExactOverriddenJoinColumn( propertyName.replace( ".collection&&element.", "."  ) );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 */
 	private JoinColumn[] getExactOverriddenJoinColumn(String propertyName) {
 		JoinColumn[] override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenJoinColumn( propertyName );
 		}
 		if ( override == null && currentPropertyJoinColumnOverride != null ) {
 			override = currentPropertyJoinColumnOverride.get( propertyName );
 		}
 		if ( override == null && holderJoinColumnOverride != null ) {
 			override = holderJoinColumnOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	@Override
 	public JoinTable getJoinTable(XProperty property) {
 		final String propertyName = StringHelper.qualify( getPath(), property.getName() );
 		JoinTable result = getOverriddenJoinTable( propertyName );
 		if (result == null) {
 			result = property.getAnnotation( JoinTable.class );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 * replace the placeholder 'collection&&element' with nothing
 	 *
 	 * These rules are here to support both JPA 2 and legacy overriding rules.
 	 */
 	public JoinTable getOverriddenJoinTable(String propertyName) {
 		JoinTable result = getExactOverriddenJoinTable( propertyName );
 		if ( result == null && propertyName.contains( ".collection&&element." ) ) {
 			//support for non map collections where no prefix is needed
 			//TODO cache the underlying regexp
 			result = getExactOverriddenJoinTable( propertyName.replace( ".collection&&element.", "."  ) );
 		}
 		return result;
 	}
 
 	/**
 	 * Get column overriding, property first, then parent, then holder
 	 */
 	private JoinTable getExactOverriddenJoinTable(String propertyName) {
 		JoinTable override = null;
 		if ( parent != null ) {
 			override = parent.getExactOverriddenJoinTable( propertyName );
 		}
 		if ( override == null && currentPropertyJoinTableOverride != null ) {
 			override = currentPropertyJoinTableOverride.get( propertyName );
 		}
 		if ( override == null && holderJoinTableOverride != null ) {
 			override = holderJoinTableOverride.get( propertyName );
 		}
 		return override;
 	}
 
 	private void buildHierarchyColumnOverride(XClass element) {
 		XClass current = element;
 		Map<String, Column[]> columnOverride = new HashMap<String, Column[]>();
 		Map<String, JoinColumn[]> joinColumnOverride = new HashMap<String, JoinColumn[]>();
 		Map<String, JoinTable> joinTableOverride = new HashMap<String, JoinTable>();
 		while ( current != null && !context.getBuildingOptions().getReflectionManager().toXClass( Object.class ).equals( current ) ) {
 			if ( current.isAnnotationPresent( Entity.class ) || current.isAnnotationPresent( MappedSuperclass.class )
 					|| current.isAnnotationPresent( Embeddable.class ) ) {
 				//FIXME is embeddable override?
 				Map<String, Column[]> currentOverride = buildColumnOverride( current, getPath() );
 				Map<String, JoinColumn[]> currentJoinOverride = buildJoinColumnOverride( current, getPath() );
 				Map<String, JoinTable> currentJoinTableOverride = buildJoinTableOverride( current, getPath() );
 				currentOverride.putAll( columnOverride ); //subclasses have precedence over superclasses
 				currentJoinOverride.putAll( joinColumnOverride ); //subclasses have precedence over superclasses
 				currentJoinTableOverride.putAll( joinTableOverride ); //subclasses have precedence over superclasses
 				columnOverride = currentOverride;
 				joinColumnOverride = currentJoinOverride;
 				joinTableOverride = currentJoinTableOverride;
 			}
 			current = current.getSuperclass();
 		}
 
 		holderColumnOverride = columnOverride.size() > 0 ? columnOverride : null;
 		holderJoinColumnOverride = joinColumnOverride.size() > 0 ? joinColumnOverride : null;
 		holderJoinTableOverride = joinTableOverride.size() > 0 ? joinTableOverride : null;
 	}
 
 	private static Map<String, Column[]> buildColumnOverride(XAnnotatedElement element, String path) {
 		Map<String, Column[]> columnOverride = new HashMap<String, Column[]>();
 		if ( element == null ) return columnOverride;
 		AttributeOverride singleOverride = element.getAnnotation( AttributeOverride.class );
 		AttributeOverrides multipleOverrides = element.getAnnotation( AttributeOverrides.class );
 		AttributeOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AttributeOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden columns
 		if ( overrides != null ) {
 			for (AttributeOverride depAttr : overrides) {
 				columnOverride.put(
 						StringHelper.qualify( path, depAttr.name() ),
 						new Column[] { depAttr.column() }
 				);
 			}
 		}
 		return columnOverride;
 	}
 
 	private static Map<String, JoinColumn[]> buildJoinColumnOverride(XAnnotatedElement element, String path) {
 		Map<String, JoinColumn[]> columnOverride = new HashMap<String, JoinColumn[]>();
 		if ( element == null ) return columnOverride;
 		AssociationOverride singleOverride = element.getAnnotation( AssociationOverride.class );
 		AssociationOverrides multipleOverrides = element.getAnnotation( AssociationOverrides.class );
 		AssociationOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AssociationOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden columns
 		if ( overrides != null ) {
 			for (AssociationOverride depAttr : overrides) {
 				columnOverride.put(
 						StringHelper.qualify( path, depAttr.name() ),
 						depAttr.joinColumns()
 				);
 			}
 		}
 		return columnOverride;
 	}
 
 	private static Map<String, JoinTable> buildJoinTableOverride(XAnnotatedElement element, String path) {
 		Map<String, JoinTable> tableOverride = new HashMap<String, JoinTable>();
 		if ( element == null ) return tableOverride;
 		AssociationOverride singleOverride = element.getAnnotation( AssociationOverride.class );
 		AssociationOverrides multipleOverrides = element.getAnnotation( AssociationOverrides.class );
 		AssociationOverride[] overrides;
 		if ( singleOverride != null ) {
 			overrides = new AssociationOverride[] { singleOverride };
 		}
 		else if ( multipleOverrides != null ) {
 			overrides = multipleOverrides.value();
 		}
 		else {
 			overrides = null;
 		}
 
 		//fill overridden tables
 		if ( overrides != null ) {
 			for (AssociationOverride depAttr : overrides) {
 				if ( depAttr.joinColumns().length == 0 ) {
 					tableOverride.put(
 							StringHelper.qualify( path, depAttr.name() ),
 							depAttr.joinTable()
 					);
 				}
 			}
 		}
 		return tableOverride;
 	}
 
 	@Override
 	public void setParentProperty(String parentProperty) {
 		throw new AssertionFailure( "Setting the parent property to a non component" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
index 933c3563c2..eb9e63a071 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AttributeConverterDefinition.java
@@ -1,268 +1,268 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.Type;
 import java.lang.reflect.TypeVariable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
-
 import javax.persistence.AttributeConverter;
 import javax.persistence.Converter;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
-import org.jboss.logging.Logger;
 
 /**
+ * Externalized representation of an AttributeConverter
+ *
  * @author Steve Ebersole
+ *
+ * @see org.hibernate.boot.spi.AttributeConverterDescriptor
  */
 public class AttributeConverterDefinition {
-	private static final Logger log = Logger.getLogger( AttributeConverterDefinition.class );
-
 	private final AttributeConverter attributeConverter;
 	private final boolean autoApply;
 	private final Class entityAttributeType;
 	private final Class databaseColumnType;
 
 	/**
 	 * Build an AttributeConverterDefinition from the AttributeConverter Class reference and
 	 * whether or not to auto-apply it.
 	 *
 	 * @param attributeConverterClass The AttributeConverter Class
 	 * @param autoApply Should the AttributeConverter be auto-applied?
 	 *
 	 * @return The constructed definition
 	 */
 	public static AttributeConverterDefinition from(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
 		return new AttributeConverterDefinition(
 				instantiateAttributeConverter( attributeConverterClass ),
 				autoApply
 		);
 	}
 
 	private static AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
 		try {
 			return attributeConverterClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]",
 					e
 			);
 		}
 	}
 
 	/**
 	 * Build an AttributeConverterDefinition from the AttributeConverter Class reference.  The
 	 * converter is searched for a {@link Converter} annotation	 to determine whether it should
 	 * be treated as auto-apply.  If the annotation is present, {@link Converter#autoApply()} is
 	 * used to make that determination.  If the annotation is not present, {@code false} is assumed.
 	 *
 	 * @param attributeConverterClass The converter class
 	 *
 	 * @return The constructed definition
 	 */
 	public static AttributeConverterDefinition from(Class<? extends AttributeConverter> attributeConverterClass) {
 		return from( instantiateAttributeConverter( attributeConverterClass ) );
 	}
 
 	/**
 	 * Build an AttributeConverterDefinition from an AttributeConverter instance.  The
 	 * converter is searched for a {@link Converter} annotation	 to determine whether it should
 	 * be treated as auto-apply.  If the annotation is present, {@link Converter#autoApply()} is
 	 * used to make that determination.  If the annotation is not present, {@code false} is assumed.
 	 *
 	 * @param attributeConverter The AttributeConverter instance
 	 *
 	 * @return The constructed definition
 	 */
 	public static AttributeConverterDefinition from(AttributeConverter attributeConverter) {
 		boolean autoApply = false;
 		Converter converterAnnotation = attributeConverter.getClass().getAnnotation( Converter.class );
 		if ( converterAnnotation != null ) {
 			autoApply = converterAnnotation.autoApply();
 		}
 
 		return new AttributeConverterDefinition( attributeConverter, autoApply );
 	}
 
 	/**
 	 * Build an AttributeConverterDefinition from the AttributeConverter instance and
 	 * whether or not to auto-apply it.
 	 *
 	 * @param attributeConverter The AttributeConverter instance
 	 * @param autoApply Should the AttributeConverter be auto-applied?
 	 *
 	 * @return The constructed definition
 	 */
 	public static AttributeConverterDefinition from(AttributeConverter attributeConverter, boolean autoApply) {
 		return new AttributeConverterDefinition( attributeConverter, autoApply );
 	}
 
 	public AttributeConverterDefinition(AttributeConverter attributeConverter, boolean autoApply) {
 		this.attributeConverter = attributeConverter;
 		this.autoApply = autoApply;
 
 		final Class attributeConverterClass = attributeConverter.getClass();
 		final ParameterizedType attributeConverterSignature = extractAttributeConverterParameterizedType( attributeConverterClass );
 		if ( attributeConverterSignature == null ) {
 			throw new AssertionFailure(
 					"Could not extract ParameterizedType representation of AttributeConverter definition " +
 							"from AttributeConverter implementation class [" + attributeConverterClass.getName() + "]"
 			);
 		}
 
 		if ( attributeConverterSignature.getActualTypeArguments().length < 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] did not retain parameterized type information"
 			);
 		}
 
 		if ( attributeConverterSignature.getActualTypeArguments().length > 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] specified more than 2 parameterized types"
 			);
 		}
 		entityAttributeType = extractClass( attributeConverterSignature.getActualTypeArguments()[0] );
 		if ( entityAttributeType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'entity attribute' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 
 		databaseColumnType = extractClass(attributeConverterSignature.getActualTypeArguments()[1]);
 		if ( databaseColumnType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'database column' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 	}
 
 	private ParameterizedType extractAttributeConverterParameterizedType(Type base) {
 		if ( base != null ) {
 			Class clazz = extractClass( base );
 			List<Type> types = new ArrayList<Type>();
 			types.add( clazz.getGenericSuperclass() );
 			types.addAll( Arrays.asList( clazz.getGenericInterfaces() ) );
 			for ( Type type : types ) {
 				type = resolveType( type, base );
 				if ( ParameterizedType.class.isInstance( type ) ) {
 					final ParameterizedType parameterizedType = (ParameterizedType) type;
 					if ( AttributeConverter.class.equals( parameterizedType.getRawType() ) ) {
 						return parameterizedType;
 					}
 				}
 				ParameterizedType parameterizedType = extractAttributeConverterParameterizedType( type );
 				if ( parameterizedType != null ) {
 					return parameterizedType;
 				}
 			}
 		}
 		return null;
 	}
 
 	private static Type resolveType(Type target, Type context) {
 		if ( target instanceof ParameterizedType ) {
 			return resolveParameterizedType( (ParameterizedType) target, context );
 		}
 		else if ( target instanceof TypeVariable ) {
 			return resolveTypeVariable( (TypeVariable) target, (ParameterizedType) context );
 		}
 		return target;
 	}
 
 	private static ParameterizedType resolveParameterizedType(final ParameterizedType parameterizedType, Type context) {
 		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
 
 		final Type[] resolvedTypeArguments = new Type[actualTypeArguments.length];
 		for ( int idx = 0; idx < actualTypeArguments.length; idx++ ) {
 			resolvedTypeArguments[idx] = resolveType( actualTypeArguments[idx], context );
 		}
 		return new ParameterizedType() {
 
 			@Override
 			public Type[] getActualTypeArguments() {
 				return resolvedTypeArguments;
 			}
 
 			@Override
 			public Type getRawType() {
 				return parameterizedType.getRawType();
 			}
 
 			@Override
 			public Type getOwnerType() {
 				return parameterizedType.getOwnerType();
 			}
 
 		};
 	}
 
 	private static Type resolveTypeVariable(TypeVariable typeVariable, ParameterizedType context) {
 		Class clazz = extractClass( context.getRawType() );
 		TypeVariable[] typeParameters = clazz.getTypeParameters();
 		for ( int idx = 0; idx < typeParameters.length; idx++ ) {
 			if ( typeVariable.getName().equals( typeParameters[idx].getName() ) ) {
 				return resolveType( context.getActualTypeArguments()[idx], context );
 			}
 		}
 		return typeVariable;
 	}
 
 	public AttributeConverter getAttributeConverter() {
 		return attributeConverter;
 	}
 
 	public boolean isAutoApply() {
 		return autoApply;
 	}
 
 	public Class getEntityAttributeType() {
 		return entityAttributeType;
 	}
 
 	public Class getDatabaseColumnType() {
 		return databaseColumnType;
 	}
 
 	private static Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
 
 	private static Class extractClass(Type type) {
 		if ( type instanceof Class ) {
 			return (Class) type;
 		}
 		else if ( type instanceof ParameterizedType ) {
 			return extractClass( ( (ParameterizedType) type ).getRawType() );
 		}
 		return null;
 	}
 
 	@Override
 	public String toString() {
 		return String.format(
 				"%s[converterClass=%s, domainType=%s, jdbcType=%s]",
 				this.getClass().getName(),
 				attributeConverter.getClass().getName(),
 				entityAttributeType.getName(),
 				databaseColumnType.getName()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
index 842f4974b8..efc4764f29 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/CollectionPropertyHolder.java
@@ -1,496 +1,468 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import java.util.HashMap;
 import java.util.Map;
 import javax.persistence.Convert;
 import javax.persistence.Converts;
 import javax.persistence.Enumerated;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.MapKeyClass;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.OneToMany;
 import javax.persistence.Temporal;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.CollectionType;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
-import org.hibernate.boot.model.source.spi.AttributePath;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Table;
 
 /**
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class CollectionPropertyHolder extends AbstractPropertyHolder {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( CollectionPropertyHolder.class );
 
 	private final Collection collection;
 
 	// assume true, the constructor will opt out where appropriate
 	private boolean canElementBeConverted = true;
 	private boolean canKeyBeConverted = true;
 
 	private Map<String,AttributeConversionInfo> elementAttributeConversionInfoMap;
 	private Map<String,AttributeConversionInfo> keyAttributeConversionInfoMap;
 
 	public CollectionPropertyHolder(
 			Collection collection,
 			String path,
 			XClass clazzToProcess,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			MetadataBuildingContext context) {
 		super( path, parentPropertyHolder, clazzToProcess, context );
 		this.collection = collection;
 		setCurrentProperty( property );
 
 		this.elementAttributeConversionInfoMap = new HashMap<String, AttributeConversionInfo>();
 		this.keyAttributeConversionInfoMap = new HashMap<String, AttributeConversionInfo>();
 	}
 
 	public Collection getCollectionBinding() {
 		return collection;
 	}
 
 	private void buildAttributeConversionInfoMaps(
 			XProperty collectionProperty,
 			Map<String,AttributeConversionInfo> elementAttributeConversionInfoMap,
 			Map<String,AttributeConversionInfo> keyAttributeConversionInfoMap) {
 		if ( collectionProperty == null ) {
 			// not sure this is valid condition
 			return;
 		}
 
 		{
 			final Convert convertAnnotation = collectionProperty.getAnnotation( Convert.class );
 			if ( convertAnnotation != null ) {
 				applyLocalConvert( convertAnnotation, collectionProperty, elementAttributeConversionInfoMap, keyAttributeConversionInfoMap );
 			}
 		}
 
 		{
 			final Converts convertsAnnotation = collectionProperty.getAnnotation( Converts.class );
 			if ( convertsAnnotation != null ) {
 				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
 					applyLocalConvert(
 							convertAnnotation,
 							collectionProperty,
 							elementAttributeConversionInfoMap,
 							keyAttributeConversionInfoMap
 					);
 				}
 			}
 		}
 	}
 
 	private void applyLocalConvert(
 			Convert convertAnnotation,
 			XProperty collectionProperty,
 			Map<String,AttributeConversionInfo> elementAttributeConversionInfoMap,
 			Map<String,AttributeConversionInfo> keyAttributeConversionInfoMap) {
 
 		// IMPL NOTE : the rules here are quite more lenient than what JPA says.  For example, JPA says
 		// that @Convert on a Map always needs to specify attributeName of key/value (or prefixed with
 		// key./value. for embedded paths).  However, we try to see if conversion of either is disabled
 		// for whatever reason.  For example, if the Map is annotated with @Enumerated the elements cannot
 		// be converted so any @Convert likely meant the key, so we apply it to the key
 
 		final AttributeConversionInfo info = new AttributeConversionInfo( convertAnnotation, collectionProperty );
 		if ( collection.isMap() ) {
 			boolean specCompliant = StringHelper.isNotEmpty( info.getAttributeName() )
 					&& ( info.getAttributeName().startsWith( "key" )
 					|| info.getAttributeName().startsWith( "value" ) );
 			if ( !specCompliant ) {
 				log.nonCompliantMapConversion( collection.getRole() );
 			}
 		}
 
 		if ( StringHelper.isEmpty( info.getAttributeName() ) ) {
 			// the @Convert did not name an attribute...
 			if ( canElementBeConverted && canKeyBeConverted ) {
 				throw new IllegalStateException(
 						"@Convert placed on Map attribute [" + collection.getRole()
 								+ "] must define attributeName of 'key' or 'value'"
 				);
 			}
 			else if ( canKeyBeConverted ) {
 				keyAttributeConversionInfoMap.put( "", info );
 			}
 			else if ( canElementBeConverted ) {
 				elementAttributeConversionInfoMap.put( "", info );
 			}
 			// if neither, we should not be here...
 		}
 		else {
 			// the @Convert named an attribute...
 			final String keyPath = removePrefix( info.getAttributeName(), "key" );
 			final String elementPath = removePrefix( info.getAttributeName(), "value" );
 
 			if ( canElementBeConverted && canKeyBeConverted && keyPath == null && elementPath == null ) {
 				// specified attributeName needs to have 'key.' or 'value.' prefix
 				throw new IllegalStateException(
 						"@Convert placed on Map attribute [" + collection.getRole()
 								+ "] must define attributeName of 'key' or 'value'"
 				);
 			}
 
 			if ( keyPath != null ) {
 				keyAttributeConversionInfoMap.put( keyPath, info );
 			}
 			else if ( elementPath != null ) {
 				elementAttributeConversionInfoMap.put( elementPath, info );
 			}
 		}
 	}
 
 	/**
 	 * Check if path has the given prefix and remove it.
 	 *
 	 * @param path Path.
 	 * @param prefix Prefix.
 	 * @return Path without prefix, or null, if path did not have the prefix.
 	 */
 	private String removePrefix(String path, String prefix) {
 		if ( path.equals(prefix) ) {
 			return "";
 		}
 
 		if (path.startsWith(prefix + ".")) {
 			return path.substring( prefix.length() + 1 );
 		}
 
 		return null;
 	}
 
 	@Override
 	protected String normalizeCompositePath(String attributeName) {
 		return attributeName;
 	}
 
 	@Override
 	protected String normalizeCompositePathForLogging(String attributeName) {
 		return collection.getRole() + '.' + attributeName;
 	}
 
 	@Override
 	public void startingProperty(XProperty property) {
 		if ( property == null ) {
 			return;
 		}
 
 		// todo : implement (and make sure it gets called - for handling collections of composites)
 	}
 
 	@Override
 	protected AttributeConversionInfo locateAttributeConversionInfo(XProperty property) {
 		if ( canElementBeConverted && canKeyBeConverted ) {
 			// need to decide whether 'property' refers to key/element
 			// todo : this may not work for 'basic collections' since there is no XProperty for the element
 		}
 		else if ( canKeyBeConverted ) {
 
 		}
 		else {
 			return null;
 		}
 
 		return null;
 	}
 
 	@Override
 	protected AttributeConversionInfo locateAttributeConversionInfo(String path) {
 		final String key = removePrefix( path, "key" );
 		if ( key != null ) {
 			return keyAttributeConversionInfoMap.get( key );
 		}
 
 		final String element = removePrefix( path, "element" );
 		if ( element != null ) {
 			return elementAttributeConversionInfoMap.get( element );
 		}
 
 		return elementAttributeConversionInfoMap.get( path );
 	}
 
 	public String getClassName() {
 		throw new AssertionFailure( "Collection property holder does not have a class name" );
 	}
 
 	public String getEntityOwnerClassName() {
 		return null;
 	}
 
 	public Table getTable() {
 		return collection.getCollectionTable();
 	}
 
 	public void addProperty(Property prop, XClass declaringClass) {
 		throw new AssertionFailure( "Cannot add property to a collection" );
 	}
 
 	public KeyValue getIdentifier() {
 		throw new AssertionFailure( "Identifier collection not yet managed" );
 	}
 
 	public boolean isOrWithinEmbeddedId() {
 		return false;
 	}
 
 	public PersistentClass getPersistentClass() {
 		return collection.getOwner();
 	}
 
 	public boolean isComponent() {
 		return false;
 	}
 
 	public boolean isEntity() {
 		return false;
 	}
 
 	public String getEntityName() {
 		return collection.getOwner().getEntityName();
 	}
 
 	public void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass) {
 		//Ejb3Column.checkPropertyConsistency( ); //already called earlier
 		throw new AssertionFailure( "addProperty to a join table of a collection: does it make sense?" );
 	}
 
 	public Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation) {
 		throw new AssertionFailure( "Add a <join> in a second pass" );
 	}
 
 	@Override
 	public String toString() {
 		return super.toString() + "(" + collection.getRole() + ")";
 	}
 
 	boolean prepared;
 
 	public void prepare(XProperty collectionProperty) {
 		// fugly
 		if ( prepared ) {
 			return;
 		}
 
 		if ( collectionProperty == null ) {
 			return;
 		}
 
 		prepared = true;
 
 		if ( collection.isMap() ) {
 			if ( collectionProperty.isAnnotationPresent( MapKeyEnumerated.class ) ) {
 				canKeyBeConverted = false;
 			}
 			else if ( collectionProperty.isAnnotationPresent( MapKeyTemporal.class ) ) {
 				canKeyBeConverted = false;
 			}
 			else if ( collectionProperty.isAnnotationPresent( MapKeyClass.class ) ) {
 				canKeyBeConverted = false;
 			}
 			else if ( collectionProperty.isAnnotationPresent( MapKeyType.class ) ) {
 				canKeyBeConverted = false;
 			}
 		}
 		else {
 			canKeyBeConverted = false;
 		}
 
 		if ( collectionProperty.isAnnotationPresent( ManyToAny.class ) ) {
 			canElementBeConverted = false;
 		}
 		else if ( collectionProperty.isAnnotationPresent( OneToMany.class ) ) {
 			canElementBeConverted = false;
 		}
 		else if ( collectionProperty.isAnnotationPresent( ManyToMany.class ) ) {
 			canElementBeConverted = false;
 		}
 		else if ( collectionProperty.isAnnotationPresent( Enumerated.class ) ) {
 			canElementBeConverted = false;
 		}
 		else if ( collectionProperty.isAnnotationPresent( Temporal.class ) ) {
 			canElementBeConverted = false;
 		}
 		else if ( collectionProperty.isAnnotationPresent( CollectionType.class ) ) {
 			canElementBeConverted = false;
 		}
 
 		// Is it valid to reference a collection attribute in a @Convert attached to the owner (entity) by path?
 		// if so we should pass in 'clazzToProcess' also
 		if ( canKeyBeConverted || canElementBeConverted ) {
 			buildAttributeConversionInfoMaps( collectionProperty, elementAttributeConversionInfoMap, keyAttributeConversionInfoMap );
 		}
 	}
 
-	public AttributeConverterDefinition resolveElementAttributeConverterDefinition(XClass elementXClass) {
+	public AttributeConverterDescriptor resolveElementAttributeConverterDescriptor(XProperty collectionXProperty, XClass elementXClass) {
 		AttributeConversionInfo info = locateAttributeConversionInfo( "element" );
 		if ( info != null ) {
 			if ( info.isConversionDisabled() ) {
 				return null;
 			}
 			else {
 				try {
-					return makeAttributeConverterDefinition( info );
+					return makeAttributeConverterDescriptor( info );
 				}
 				catch (Exception e) {
 					throw new IllegalStateException(
 							String.format( "Unable to instantiate AttributeConverter [%s", info.getConverterClass().getName() ),
 							e
 					);
 				}
 			}
 		}
 
 		log.debugf(
 				"Attempting to locate auto-apply AttributeConverter for collection element [%s]",
 				collection.getRole()
 		);
 
-		final Class elementClass = determineElementClass( elementXClass );
-		if ( elementClass != null ) {
-			for ( AttributeConverterDefinition attributeConverterDefinition : getContext().getMetadataCollector().getAttributeConverters() ) {
-				if ( ! attributeConverterDefinition.isAutoApply() ) {
-					continue;
-				}
-				log.debugf(
-						"Checking auto-apply AttributeConverter [%s] type [%s] for match [%s]",
-						attributeConverterDefinition.toString(),
-						attributeConverterDefinition.getEntityAttributeType().getSimpleName(),
-						elementClass.getSimpleName()
-				);
-				if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), elementClass ) ) {
-					return attributeConverterDefinition;
-				}
-			}
-		}
+		// todo : do we need to pass along `XClass elementXClass`?
 
-		return null;
+		return getContext().getMetadataCollector()
+				.getAttributeConverterAutoApplyHandler()
+				.findAutoApplyConverterForCollectionElement( collectionXProperty, getContext() );
 	}
 
 	private Class determineElementClass(XClass elementXClass) {
 		if ( elementXClass != null ) {
 			try {
 				return getContext().getBuildingOptions().getReflectionManager().toClass( elementXClass );
 			}
 			catch (Exception e) {
 				log.debugf(
 						"Unable to resolve XClass [%s] to Class for collection elements [%s]",
 						elementXClass.getName(),
 						collection.getRole()
 				);
 			}
 		}
 
 		if ( collection.getElement() != null ) {
 			if ( collection.getElement().getType() != null ) {
 				return collection.getElement().getType().getReturnedClass();
 			}
 		}
 
 		// currently this is called from paths where the element type really should be known,
 		// so log the fact that we could not resolve the collection element info
 		log.debugf(
 				"Unable to resolve element information for collection [%s]",
 				collection.getRole()
 		);
 		return null;
 	}
 
-	public AttributeConverterDefinition keyElementAttributeConverterDefinition(XClass keyXClass) {
+	public AttributeConverterDescriptor mapKeyAttributeConverterDescriptor(XProperty mapXProperty, XClass keyXClass) {
 		AttributeConversionInfo info = locateAttributeConversionInfo( "key" );
 		if ( info != null ) {
 			if ( info.isConversionDisabled() ) {
 				return null;
 			}
 			else {
 				try {
-					return makeAttributeConverterDefinition( info );
+					return makeAttributeConverterDescriptor( info );
 				}
 				catch (Exception e) {
 					throw new IllegalStateException(
 							String.format( "Unable to instantiate AttributeConverter [%s", info.getConverterClass().getName() ),
 							e
 					);
 				}
 			}
 		}
 
 		log.debugf(
 				"Attempting to locate auto-apply AttributeConverter for collection key [%s]",
 				collection.getRole()
 		);
 
-		final Class elementClass = determineKeyClass( keyXClass );
-		if ( elementClass != null ) {
-			for ( AttributeConverterDefinition attributeConverterDefinition : getContext().getMetadataCollector().getAttributeConverters() ) {
-				if ( ! attributeConverterDefinition.isAutoApply() ) {
-					continue;
-				}
-				log.debugf(
-						"Checking auto-apply AttributeConverter [%s] type [%s] for match [%s]",
-						attributeConverterDefinition.toString(),
-						attributeConverterDefinition.getEntityAttributeType().getSimpleName(),
-						elementClass.getSimpleName()
-				);
-				if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), elementClass ) ) {
-					return attributeConverterDefinition;
-				}
-			}
-		}
+		// todo : do we need to pass along `XClass keyXClass`?
 
-		return null;
+		return getContext().getMetadataCollector()
+				.getAttributeConverterAutoApplyHandler()
+				.findAutoApplyConverterForMapKey( mapXProperty, getContext() );
 	}
 
 	private Class determineKeyClass(XClass keyXClass) {
 		if ( keyXClass != null ) {
 			try {
 				return getContext().getBuildingOptions().getReflectionManager().toClass( keyXClass );
 			}
 			catch (Exception e) {
 				log.debugf(
 						"Unable to resolve XClass [%s] to Class for collection key [%s]",
 						keyXClass.getName(),
 						collection.getRole()
 				);
 			}
 		}
 
 		final IndexedCollection indexedCollection = (IndexedCollection) collection;
 		if ( indexedCollection.getIndex() != null ) {
 			if ( indexedCollection.getIndex().getType() != null ) {
 				return indexedCollection.getIndex().getType().getReturnedClass();
 			}
 		}
 
 		// currently this is called from paths where the element type really should be known,
 		// so log the fact that we could not resolve the collection element info
 		log.debugf(
 				"Unable to resolve key information for collection [%s]",
 				collection.getRole()
 		);
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
index bc3b58d6ce..9cf071ad7e 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/PropertyHolder.java
@@ -1,95 +1,96 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import javax.persistence.Column;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Table;
 
 /**
  * Property holder abstract property containers from their direct implementation
  *
  * @author Emmanuel Bernard
  */
 public interface PropertyHolder {
 	String getClassName();
 
 	String getEntityOwnerClassName();
 
 	Table getTable();
 
 	void addProperty(Property prop, XClass declaringClass);
 
 	void addProperty(Property prop, Ejb3Column[] columns, XClass declaringClass);
 
 	KeyValue getIdentifier();
 
 	/**
 	 * Return true if this component is or is embedded in a @EmbeddedId
 	 */
 	boolean isOrWithinEmbeddedId();
 
 	PersistentClass getPersistentClass();
 
 	boolean isComponent();
 
 	boolean isEntity();
 
 	void setParentProperty(String parentProperty);
 
 	String getPath();
 
 	/**
 	 * return null if the column is not overridden, or an array of column if true
 	 */
 	Column[] getOverriddenColumn(String propertyName);
 
 	/**
 	 * return null if the column is not overridden, or an array of column if true
 	 */
 	JoinColumn[] getOverriddenJoinColumn(String propertyName);
 
 	/**
 	 * return
 	 *  - null if no join table is present,
 	 *  - the join table if not overridden,
 	 *  - the overridden join table otherwise
 	 */
 	JoinTable getJoinTable(XProperty property);
 
 	String getEntityName();
 
 	Join addJoin(JoinTable joinTableAnn, boolean noDelayInPkColumnCreation);
 
 	boolean isInIdClass();
 
 	void setInIdClass(Boolean isInIdClass);
 
 	/**
 	 * Called during binding to allow the PropertyHolder to inspect its discovered properties.  Mainly
 	 * this is used in collecting attribute conversion declarations (via @Convert/@Converts).
 	 *
 	 * @param property The property
 	 */
 	void startingProperty(XProperty property);
 
 	/**
 	 * Determine the AttributeConverter to use for the given property.
 	 *
 	 * @param property
 	 * @return
 	 */
-	AttributeConverterDefinition resolveAttributeConverterDefinition(XProperty property);
+	AttributeConverterDescriptor resolveAttributeConverterDescriptor(XProperty property);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 3650d9ab09..fb34bfaec5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -463,1176 +463,1176 @@ public abstract class CollectionBinder {
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( loader != null ) {
 			collection.setLoaderName( loader.namedQuery() );
 		}
 
 		if (isMappedBy
 				&& (property.isAnnotationPresent( JoinColumn.class )
 					|| property.isAnnotationPresent( JoinColumns.class )
 					|| propertyHolder.getJoinTable( property ) != null ) ) {
 			String message = "Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: ";
 			message += StringHelper.qualify( propertyHolder.getPath(), propertyName );
 			throw new AnnotationException( message );
 		}
 
 		collection.setInverse( isMappedBy );
 
 		//many to many may need some second pass informations
 		if ( !oneToMany && isMappedBy ) {
 			buildingContext.getMetadataCollector().addMappedBy( getCollectionType().getName(), mappedBy, propertyName );
 		}
 		//TODO reducce tableBinder != null and oneToMany
 		XClass collectionType = getCollectionType();
 		if ( inheritanceStatePerClass == null) throw new AssertionFailure( "inheritanceStatePerClass not set" );
 		SecondPass sp = getSecondPass(
 				fkJoinColumns,
 				joinColumns,
 				inverseJoinColumns,
 				elementColumns,
 				mapKeyColumns,
 				mapKeyManyToManyColumns,
 				isEmbedded,
 				property,
 				collectionType,
 				ignoreNotFound,
 				oneToMany,
 				tableBinder,
 				buildingContext
 		);
 		if ( collectionType.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( ElementCollection.class ) //JPA 2
 				) {
 			// do it right away, otherwise @ManyToOne on composite element call addSecondPass
 			// and raise a ConcurrentModificationException
 			//sp.doSecondPass( CollectionHelper.EMPTY_MAP );
 			buildingContext.getMetadataCollector().addSecondPass( sp, !isMappedBy );
 		}
 		else {
 			buildingContext.getMetadataCollector().addSecondPass( sp, !isMappedBy );
 		}
 
 		buildingContext.getMetadataCollector().addCollectionBinding( collection );
 
 		//property building
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( collection );
 		binder.setCascade( cascadeStrategy );
 		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 		binder.setLazy( collection.isLazy() );
 		binder.setAccessType( accessType );
 		binder.setProperty( property );
 		binder.setInsertable( insertable );
 		binder.setUpdatable( updatable );
 		Property prop = binder.makeProperty();
 		//we don't care about the join stuffs because the column is on the association table.
 		if (! declaringClassSet) throw new AssertionFailure( "DeclaringClass is not set in CollectionBinder while binding" );
 		propertyHolder.addProperty( prop, declaringClass );
 	}
 
 	private void applySortingAndOrdering(Collection collection) {
 		boolean isSorted = isSortedCollection;
 
 		boolean hadOrderBy = false;
 		boolean hadExplicitSort = false;
 
 		Class<? extends Comparator> comparatorClass = null;
 
 		if ( jpaOrderBy == null && sqlOrderBy == null ) {
 			if ( deprecatedSort != null ) {
 				LOG.debug( "Encountered deprecated @Sort annotation; use @SortNatural or @SortComparator instead." );
 				if ( naturalSort != null || comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = deprecatedSort.type() != SortType.UNSORTED;
 				if ( deprecatedSort.type() == SortType.NATURAL ) {
 					isSorted = true;
 				}
 				else if ( deprecatedSort.type() == SortType.COMPARATOR ) {
 					isSorted = true;
 					comparatorClass = deprecatedSort.comparator();
 				}
 			}
 			else if ( naturalSort != null ) {
 				if ( comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = true;
 			}
 			else if ( comparatorSort != null ) {
 				hadExplicitSort = true;
 				comparatorClass = comparatorSort.value();
 			}
 		}
 		else {
 			if ( jpaOrderBy != null && sqlOrderBy != null ) {
 				throw new AnnotationException(
 						String.format(
 								"Illegal combination of @%s and @%s on %s",
 								javax.persistence.OrderBy.class.getName(),
 								OrderBy.class.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 
 			hadOrderBy = true;
 			hadExplicitSort = false;
 
 			// we can only apply the sql-based order by up front.  The jpa order by has to wait for second pass
 			if ( sqlOrderBy != null ) {
 				collection.setOrderBy( sqlOrderBy.clause() );
 			}
 		}
 
 		if ( isSortedCollection ) {
 			if ( ! hadExplicitSort && !hadOrderBy ) {
 				throw new AnnotationException(
 						"A sorted collection must define and ordering or sorting : " + safeCollectionRole()
 				);
 			}
 		}
 
 		collection.setSorted( isSortedCollection || hadExplicitSort );
 
 		if ( comparatorClass != null ) {
 			try {
 				collection.setComparator( comparatorClass.newInstance() );
 			}
 			catch (Exception e) {
 				throw new AnnotationException(
 						String.format(
 								"Could not instantiate comparator class [%s] for %s",
 								comparatorClass.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 		}
 	}
 
 	private AnnotationException buildIllegalSortCombination() {
 		return new AnnotationException(
 				String.format(
 						"Illegal combination of annotations on %s.  Only one of @%s, @%s and @%s can be used",
 						safeCollectionRole(),
 						Sort.class.getName(),
 						SortNatural.class.getName(),
 						SortComparator.class.getName()
 				)
 		);
 	}
 
 	private void defineFetchingStrategy() {
 		LazyCollection lazy = property.getAnnotation( LazyCollection.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		ManyToMany manyToMany = property.getAnnotation( ManyToMany.class );
 		ElementCollection elementCollection = property.getAnnotation( ElementCollection.class ); //jpa 2
 		ManyToAny manyToAny = property.getAnnotation( ManyToAny.class );
 		FetchType fetchType;
 		if ( oneToMany != null ) {
 			fetchType = oneToMany.fetch();
 		}
 		else if ( manyToMany != null ) {
 			fetchType = manyToMany.fetch();
 		}
 		else if ( elementCollection != null ) {
 			fetchType = elementCollection.fetch();
 		}
 		else if ( manyToAny != null ) {
 			fetchType = FetchType.LAZY;
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @ManyToOne nor @OneToMany nor @CollectionOfElements"
 			);
 		}
 		if ( lazy != null ) {
 			collection.setLazy( !( lazy.value() == LazyCollectionOption.FALSE ) );
 			collection.setExtraLazy( lazy.value() == LazyCollectionOption.EXTRA );
 		}
 		else {
 			collection.setLazy( fetchType == FetchType.LAZY );
 			collection.setExtraLazy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				collection.setFetchMode( FetchMode.JOIN );
 				collection.setLazy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 				collection.setSubselectLoadable( true );
 				collection.getOwner().setSubselectLoadableCollections( true );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			collection.setFetchMode( AnnotationBinder.getFetchMode( fetchType ) );
 		}
 	}
 
 	private XClass getCollectionType() {
 		if ( AnnotationBinder.isDefault( targetEntity, buildingContext ) ) {
 			if ( collectionType != null ) {
 				return collectionType;
 			}
 			else {
 				String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: "
 						+ safeCollectionRole();
 				throw new AnnotationException( errorMsg );
 			}
 		}
 		else {
 			return targetEntity;
 		}
 	}
 
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final MetadataBuildingContext buildingContext) {
 		return new CollectionSecondPass( buildingContext, collection ) {
 			@Override
             public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses,
 						collType,
 						fkJoinColumns,
 						keyColumns,
 						inverseColumns,
 						elementColumns,
 						isEmbedded,
 						property,
 						unique,
 						assocTableBinder,
 						ignoreNotFound,
 						buildingContext
 				);
 			}
 		};
 	}
 
 	/**
 	 * return true if it's a Fk, false if it's an association table
 	 */
 	protected boolean bindStarToManySecondPass(
 			Map persistentClasses,
 			XClass collType,
 			Ejb3JoinColumn[] fkJoinColumns,
 			Ejb3JoinColumn[] keyColumns,
 			Ejb3JoinColumn[] inverseColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XProperty property,
 			boolean unique,
 			TableBinder associationTableBinder,
 			boolean ignoreNotFound,
 			MetadataBuildingContext buildingContext) {
 		PersistentClass persistentClass = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean reversePropertyInJoin = false;
 		if ( persistentClass != null && StringHelper.isNotEmpty( this.mappedBy ) ) {
 			try {
 				reversePropertyInJoin = 0 != persistentClass.getJoinNumber(
 						persistentClass.getRecursiveProperty( this.mappedBy )
 				);
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( this.mappedBy )
 						.append( " in " )
 						.append( collection.getOwnerEntityName() )
 						.append( "." )
 						.append( property.getName() );
 				throw new AnnotationException( error.toString() );
 			}
 		}
 		if ( persistentClass != null
 				&& !reversePropertyInJoin
 				&& oneToMany
 				&& !this.isExplicitAssociationTable
 				&& ( joinColumns[0].isImplicit() && !BinderHelper.isEmptyAnnotationValue( this.mappedBy ) //implicit @JoinColumn
 				|| !fkJoinColumns[0].isImplicit() ) //this is an explicit @JoinColumn
 				) {
 			//this is a Foreign key
 			bindOneToManySecondPass(
 					getCollection(),
 					persistentClasses,
 					fkJoinColumns,
 					collType,
 					cascadeDeleteEnabled,
 					ignoreNotFound,
 					buildingContext,
 					inheritanceStatePerClass
 			);
 			return true;
 		}
 		else {
 			//this is an association table
 			bindManyToManySecondPass(
 					this.collection,
 					persistentClasses,
 					keyColumns,
 					inverseColumns,
 					elementColumns,
 					isEmbedded, collType,
 					ignoreNotFound, unique,
 					cascadeDeleteEnabled,
 					associationTableBinder,
 					property,
 					propertyHolder,
 					buildingContext
 			);
 			return false;
 		}
 	}
 
 	protected void bindOneToManySecondPass(
 			Collection collection,
 			Map persistentClasses,
 			Ejb3JoinColumn[] fkJoinColumns,
 			XClass collectionType,
 			boolean cascadeDeleteEnabled,
 			boolean ignoreNotFound,
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( buildingContext.getMetadataCollector(), collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		if ( jpaOrderBy != null ) {
 			final String orderByFragment = buildOrderByClauseFromHql(
 					jpaOrderBy.value(),
 					associatedClass,
 					collection.getRole()
 			);
 			if ( StringHelper.isNotEmpty( orderByFragment ) ) {
 				collection.setOrderBy( orderByFragment );
 			}
 		}
 
 		if ( buildingContext == null ) {
 			throw new AssertionFailure(
 					"CollectionSecondPass for oneToMany should not be called with null mappings"
 			);
 		}
 		Map<String, Join> joins = buildingContext.getMetadataCollector().getJoins( assocClass );
 		if ( associatedClass == null ) {
 			throw new MappingException(
 					"Association references unmapped class: " + assocClass
 			);
 		}
 		oneToMany.setAssociatedClass( associatedClass );
 		for (Ejb3JoinColumn column : fkJoinColumns) {
 			column.setPersistentClass( associatedClass, joins, inheritanceStatePerClass );
 			column.setJoins( joins );
 			collection.setCollectionTable( column.getTable() );
 		}
 		if ( debugEnabled ) {
 			LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 		}
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, buildingContext );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = buildingContext.getMetadataCollector().getEntityBinding( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + '_' + fkJoinColumns[0].getLogicalColumnName() + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 
 	private void bindFilters(boolean hasAssociationTable) {
 		Filter simpleFilter = property.getAnnotation( Filter.class );
 		//set filtering
 		//test incompatible choices
 		//if ( StringHelper.isNotEmpty( where ) ) collection.setWhere( where );
 		if ( simpleFilter != null ) {
 			if ( hasAssociationTable ) {
 				collection.addManyToManyFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 			else {
 				collection.addFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
 					collection.addManyToManyFilter( filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					collection.addFilter(filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
 				collection.addFilter(simpleFilterJoinTable.name(), simpleFilterJoinTable.condition(),
 						simpleFilterJoinTable.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilterJoinTable.aliases()), toAliasEntityMap(simpleFilterJoinTable.aliases()));
 					}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @FilterJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		FilterJoinTables filterJoinTables = property.getAnnotation( FilterJoinTables.class );
 		if ( filterJoinTables != null ) {
 			for (FilterJoinTable filter : filterJoinTables.value()) {
 				if ( hasAssociationTable ) {
 					collection.addFilter(filter.name(), filter.condition(),
 							filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					throw new AnnotationException(
 							"Illegal use of @FilterJoinTable on an association without join table:"
 									+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 					);
 				}
 			}
 		}
 
 		Where where = property.getAnnotation( Where.class );
 		String whereClause = where == null ? null : where.clause();
 		if ( StringHelper.isNotEmpty( whereClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setManyToManyWhere( whereClause );
 			}
 			else {
 				collection.setWhere( whereClause );
 			}
 		}
 
 		WhereJoinTable whereJoinTable = property.getAnnotation( WhereJoinTable.class );
 		String whereJoinTableClause = whereJoinTable == null ? null : whereJoinTable.clause();
 		if ( StringHelper.isNotEmpty( whereJoinTableClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setWhere( whereJoinTableClause );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @WhereJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 //		This cannot happen in annotations since the second fetch is hardcoded to join
 //		if ( ( ! collection.getManyToManyFilterMap().isEmpty() || collection.getManyToManyWhere() != null ) &&
 //		        collection.getFetchMode() == FetchMode.JOIN &&
 //		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 //			throw new MappingException(
 //			        "association with join table  defining filter or where without join fetching " +
 //			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 //				);
 //		}
 	}
 
 	private String getCondition(FilterJoinTable filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(Filter filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(String cond, String name) {
 		if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 			cond = buildingContext.getMetadataCollector().getFilterDefinition( name ).getDefaultFilterCondition();
 			if ( StringHelper.isEmpty( cond ) ) {
 				throw new AnnotationException(
 						"no filter condition found for filter " + name + " in "
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		return cond;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegionName = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ? null : cacheAnn.region();
 			cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy( cacheAnn.usage() );
 		}
 		else {
 			cacheConcurrencyStrategy = null;
 			cacheRegionName = null;
 		}
 	}
 
 	public void setOneToMany(boolean oneToMany) {
 		this.oneToMany = oneToMany;
 	}
 
 	public void setIndexColumn(IndexColumn indexColumn) {
 		this.indexColumn = indexColumn;
 	}
 
 	public void setMapKey(MapKey key) {
 		if ( key != null ) {
 			mapKeyPropertyName = key.name();
 		}
 	}
 
 	private static String buildOrderByClauseFromHql(String orderByFragment, PersistentClass associatedClass, String role) {
 		if ( orderByFragment != null ) {
 			if ( orderByFragment.length() == 0 ) {
 				//order by id
 				return "id asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "id desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static String adjustUserSuppliedValueCollectionOrderingFragment(String orderByFragment) {
 		if ( orderByFragment != null ) {
 			// NOTE: "$element$" is a specially recognized collection property recognized by the collection persister
 			if ( orderByFragment.length() == 0 ) {
 				//order by element
 				return "$element$ asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "$element$ desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static SimpleValue buildCollectionKey(
 			Collection collValue,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			MetadataBuildingContext buildingContext) {
 		//binding key reference using column
 		KeyValue keyVal;
 		//give a chance to override the referenced property name
 		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
 		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
 			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
 					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
 					joinColumns[0].getPropertyHolder().getEntityName();
 			String propRef = buildingContext.getMetadataCollector().getPropertyReferencedAssociation(
 					entityName,
 					joinColumns[0].getMappedBy()
 			);
 			if ( propRef != null ) {
 				collValue.setReferencedPropertyName( propRef );
 				buildingContext.getMetadataCollector().addPropertyReference( collValue.getOwnerEntityName(), propRef );
 			}
 		}
 		String propRef = collValue.getReferencedPropertyName();
 		if ( propRef == null ) {
 			keyVal = collValue.getOwner().getIdentifier();
 		}
 		else {
 			keyVal = (KeyValue) collValue.getOwner()
 					.getReferencedProperty( propRef )
 					.getValue();
 		}
 		DependantValue key = new DependantValue( buildingContext.getMetadataCollector(), collValue.getCollectionTable(), keyVal );
 		key.setTypeName( null );
 		Ejb3Column.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
 		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
 		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
 		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
 		collValue.setKey( key );
 
 		if ( property != null ) {
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				key.setForeignKeyName( fk.name() );
 			}
 			else {
 				final CollectionTable collectionTableAnn = property.getAnnotation( CollectionTable.class );
 				if ( collectionTableAnn != null ) {
 					if ( collectionTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 						key.setForeignKeyName( "none" );
 					}
 					else {
 						key.setForeignKeyName( StringHelper.nullIfEmpty( collectionTableAnn.foreignKey().name() ) );
 					}
 				}
 				else {
 					final JoinTable joinTableAnn = property.getAnnotation( JoinTable.class );
 					if ( joinTableAnn != null ) {
 						if ( joinTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 							key.setForeignKeyName( "none" );
 						}
 						else {
 							key.setForeignKeyName( StringHelper.nullIfEmpty( joinTableAnn.foreignKey().name() ) );
 
 						}
 					}
 				}
 			}
 		}
 
 		return key;
 	}
 
 	protected void bindManyToManySecondPass(
 			Collection collValue,
 			Map persistentClasses,
 			Ejb3JoinColumn[] joinColumns,
 			Ejb3JoinColumn[] inverseJoinColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XClass collType,
 			boolean ignoreNotFound, boolean unique,
 			boolean cascadeDeleteEnabled,
 			TableBinder associationTableBinder,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			MetadataBuildingContext buildingContext) throws MappingException {
 		if ( property == null ) {
 			throw new IllegalArgumentException( "null was passed for argument property" );
 		}
 
 		final PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		final String hqlOrderBy = extractHqlOrderBy( jpaOrderBy );
 
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
         if ( LOG.isDebugEnabled() ) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
             if ( isCollectionOfEntities && unique ) {
 				LOG.debugf("Binding a OneToMany: %s through an association table", path);
 			}
             else if (isCollectionOfEntities) {
 				LOG.debugf("Binding as ManyToMany: %s", path);
 			}
             else if (anyAnn != null) {
 				LOG.debugf("Binding a ManyToAny: %s", path);
 			}
             else {
 				LOG.debugf("Binding a collection of element: %s", path);
 			}
 		}
 		//check for user error
 		if ( !isCollectionOfEntities ) {
 			if ( property.isAnnotationPresent( ManyToMany.class ) || property.isAnnotationPresent( OneToMany.class ) ) {
 				String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 				throw new AnnotationException(
 						"Use of @OneToMany or @ManyToMany targeting an unmapped class: " + path + "[" + collType + "]"
 				);
 			}
 			else if ( anyAnn != null ) {
 				if ( parentPropertyHolder.getJoinTable( property ) == null ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"@JoinTable is mandatory when @ManyToAny is used: " + path
 					);
 				}
 			}
 			else {
 				JoinTable joinTableAnn = parentPropertyHolder.getJoinTable( property );
 				if ( joinTableAnn != null && joinTableAnn.inverseJoinColumns().length > 0 ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"Use of @JoinTable.inverseJoinColumns targeting an unmapped class: " + path + "[" + collType + "]"
 					);
 				}
 			}
 		}
 
 		boolean mappedBy = !BinderHelper.isEmptyAnnotationValue( joinColumns[0].getMappedBy() );
 		if ( mappedBy ) {
 			if ( !isCollectionOfEntities ) {
 				StringBuilder error = new StringBuilder( 80 )
 						.append(
 								"Collection of elements must not have mappedBy or association reference an unmapped entity: "
 						)
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Property otherSideProperty;
 			try {
 				otherSideProperty = collectionEntity.getRecursiveProperty( joinColumns[0].getMappedBy() );
 			}
 			catch (MappingException e) {
 				throw new AnnotationException(
 						"mappedBy reference an unknown target entity property: "
 								+ collType + "." + joinColumns[0].getMappedBy() + " in "
 								+ collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName()
 				);
 			}
 			Table table;
 			if ( otherSideProperty.getValue() instanceof Collection ) {
 				//this is a collection on the other side
 				table = ( (Collection) otherSideProperty.getValue() ).getCollectionTable();
 			}
 			else {
 				//This is a ToOne with a @JoinTable or a regular property
 				table = otherSideProperty.getValue().getTable();
 			}
 			collValue.setCollectionTable( table );
 			String entityName = collectionEntity.getEntityName();
 			for (Ejb3JoinColumn column : joinColumns) {
 				//column.setDefaultColumnHeader( joinColumns[0].getMappedBy() ); //seems not to be used, make sense
 				column.setManyToManyOwnerSideEntityName( entityName );
 			}
 		}
 		else {
 			//TODO: only for implicit columns?
 			//FIXME NamingStrategy
 			for (Ejb3JoinColumn column : joinColumns) {
 				String mappedByProperty = buildingContext.getMetadataCollector().getFromMappedBy(
 						collValue.getOwnerEntityName(), column.getPropertyName()
 				);
 				Table ownerTable = collValue.getOwner().getTable();
 				column.setMappedBy(
 						collValue.getOwner().getEntityName(),
 						collValue.getOwner().getJpaEntityName(),
 						buildingContext.getMetadataCollector().getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getClassName(),
 						collValue.getOwner().getEntityName(),
 						collValue.getOwner().getJpaEntityName(),
 						buildingContext.getMetadataCollector().getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getClassName() : null,
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
 						collectionEntity != null ? collectionEntity.getJpaEntityName() : null,
 						collectionEntity != null ? buildingContext.getMetadataCollector().getLogicalTableName(
 								collectionEntity.getTable()
 						) : null,
 						joinColumns[0].getPropertyName()
 				);
 			}
 			associationTableBinder.setJPA2ElementCollection( !isCollectionOfEntities && property.isAnnotationPresent( ElementCollection.class ));
 			collValue.setCollectionTable( associationTableBinder.bind() );
 		}
 		bindFilters( isCollectionOfEntities );
 		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, buildingContext );
 
 		ManyToOne element = null;
 		if ( isCollectionOfEntities ) {
 			element = new ManyToOne( buildingContext.getMetadataCollector(),  collValue.getCollectionTable() );
 			collValue.setElement( element );
 			element.setReferencedEntityName( collType.getName() );
 			//element.setFetchMode( fetchMode );
 			//element.setLazy( fetchMode != FetchMode.JOIN );
 			//make the second join non lazy
 			element.setFetchMode( FetchMode.JOIN );
 			element.setLazy( false );
 			element.setIgnoreNotFound( ignoreNotFound );
 			// as per 11.1.38 of JPA 2.0 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				element.setForeignKeyName( fk.name() );
 			}
 			else {
 				final JoinTable joinTableAnn = property.getAnnotation( JoinTable.class );
 				if ( joinTableAnn != null ) {
 					if ( joinTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 						element.setForeignKeyName( "none" );
 					}
 					else {
 						element.setForeignKeyName( StringHelper.nullIfEmpty( joinTableAnn.inverseForeignKey().name() ) );
 					}
 				}
 			}
 		}
 		else if ( anyAnn != null ) {
 			//@ManyToAny
 			//Make sure that collTyp is never used during the @ManyToAny branch: it will be set to void.class
 			PropertyData inferredData = new PropertyInferredData(null, property, "unsupported", buildingContext.getBuildingOptions().getReflectionManager() );
 			//override the table
 			for (Ejb3Column column : inverseJoinColumns) {
 				column.setTable( collValue.getCollectionTable() );
 			}
 			Any any = BinderHelper.buildAnyValue(
 					anyAnn.metaDef(),
 					inverseJoinColumns,
 					anyAnn.metaColumn(),
 					inferredData,
 					cascadeDeleteEnabled,
 					Nullability.NO_CONSTRAINT,
 					propertyHolder,
 					new EntityBinder(),
 					true,
 					buildingContext
 			);
 			collValue.setElement( any );
 		}
 		else {
 			XClass elementClass;
 			AnnotatedClassType classType;
 
 			CollectionPropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						null,
 						property,
 						parentPropertyHolder,
 						buildingContext
 				);
 			}
 			else {
 				elementClass = collType;
 				classType = buildingContext.getMetadataCollector().getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property,
 						parentPropertyHolder,
 						buildingContext
 				);
 
 				// 'parentPropertyHolder' is the PropertyHolder for the owner of the collection
 				// 'holder' is the CollectionPropertyHolder.
 				// 'property' is the collection XProperty
 				parentPropertyHolder.startingProperty( property );
 
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
 				// todo : force in the case of Convert annotation(s) with embedded paths (beyond key/value prefixes)?
 				if ( isEmbedded || attributeOverride ) {
 					classType = AnnotatedClassType.EMBEDDABLE;
 				}
 			}
 
 			if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 				EntityBinder entityBinder = new EntityBinder();
 				PersistentClass owner = collValue.getOwner();
 				boolean isPropertyAnnotated;
 				//FIXME support @Access for collection of elements
 				//String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" );
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					isPropertyAnnotated = prop.getPropertyAccessorName().equals( "property" );
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				PropertyData inferredData;
 				if ( isMap() ) {
 					//"value" is the JPA 2 prefix for map values (used to be "element")
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "value", elementClass );
 					}
 				}
 				else {
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						//"collection&&element" is not a valid property name => placeholder
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "collection&&element", elementClass );
 					}
 				}
 				//TODO be smart with isNullable
 				boolean isNullable = true;
 				Component component = AnnotationBinder.fillComponent(
 						holder,
 						inferredData,
 						isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD,
 						isNullable,
 						entityBinder,
 						false,
 						false,
 						true,
 						buildingContext,
 						inheritanceStatePerClass
 				);
 
 				collValue.setElement( component );
 
 				if ( StringHelper.isNotEmpty( hqlOrderBy ) ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 					if ( orderBy != null ) {
 						collValue.setOrderBy( orderBy );
 					}
 				}
 			}
 			else {
 				holder.prepare( property );
 
 				SimpleValueBinder elementBinder = new SimpleValueBinder();
 				elementBinder.setBuildingContext( buildingContext );
 				elementBinder.setReturnedClassName( collType.getName() );
 				if ( elementColumns == null || elementColumns.length == 0 ) {
 					elementColumns = new Ejb3Column[1];
 					Ejb3Column column = new Ejb3Column();
 					column.setImplicit( false );
 					//not following the spec but more clean
 					column.setNullable( true );
 					column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 					column.setLogicalColumnName( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
 					//TODO create an EMPTY_JOINS collection
 					column.setJoins( new HashMap<String, Join>() );
 					column.setBuildingContext( buildingContext );
 					column.bind();
 					elementColumns[0] = column;
 				}
 				//override the table
 				for (Ejb3Column column : elementColumns) {
 					column.setTable( collValue.getCollectionTable() );
 				}
 				elementBinder.setColumns( elementColumns );
 				elementBinder.setType(
 						property,
 						elementClass,
 						collValue.getOwnerEntityName(),
-						holder.resolveElementAttributeConverterDefinition( elementClass )
+						holder.resolveElementAttributeConverterDescriptor( property, elementClass )
 				);
 				elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 				elementBinder.setAccessType( accessType );
 				collValue.setElement( elementBinder.make() );
 				String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 				if ( orderBy != null ) {
 					collValue.setOrderBy( orderBy );
 				}
 			}
 		}
 
 		checkFilterConditions( collValue );
 
 		//FIXME: do optional = false
 		if ( isCollectionOfEntities ) {
 			bindManytoManyInverseFk( collectionEntity, inverseJoinColumns, element, unique, buildingContext );
 		}
 
 	}
 
 	private String extractHqlOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		if ( jpaOrderBy != null ) {
 			return jpaOrderBy.value(); // Null not possible. In case of empty expression, apply default ordering.
 		}
 		return null; // @OrderBy not found.
 	}
 
 	private static void checkFilterConditions(Collection collValue) {
 		//for now it can't happen, but sometime soon...
 		if ( ( collValue.getFilters().size() != 0 || StringHelper.isNotEmpty( collValue.getWhere() ) ) &&
 				collValue.getFetchMode() == FetchMode.JOIN &&
 				!( collValue.getElement() instanceof SimpleValue ) && //SimpleValue (CollectionOfElements) are always SELECT but it does not matter
 				collValue.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 					"@ManyToMany or @CollectionOfElements defining filter or where without join fetching "
 							+ "not valid within collection using join fetching[" + collValue.getRole() + "]"
 			);
 		}
 	}
 
 	private static void bindCollectionSecondPass(
 			Collection collValue,
 			PersistentClass collectionEntity,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			MetadataBuildingContext buildingContext) {
 		try {
 			BinderHelper.createSyntheticPropertyReference(
 					joinColumns,
 					collValue.getOwner(),
 					collectionEntity,
 					collValue,
 					false,
 					buildingContext
 			);
 		}
 		catch (AnnotationException ex) {
 			throw new AnnotationException( "Unable to map collection " + collValue.getOwner().getClassName() + "." + property.getName(), ex );
 		}
 		SimpleValue key = buildCollectionKey( collValue, joinColumns, cascadeDeleteEnabled, property, buildingContext );
 		if ( property.isAnnotationPresent( ElementCollection.class ) && joinColumns.length > 0 ) {
 			joinColumns[0].setJPA2ElementCollection( true );
 		}
 		TableBinder.bindFk( collValue.getOwner(), collectionEntity, joinColumns, key, false, buildingContext );
 	}
 
 	public void setCascadeDeleteEnabled(boolean onDeleteCascade) {
 		this.cascadeDeleteEnabled = onDeleteCascade;
 	}
 
 	private String safeCollectionRole() {
 		if ( propertyHolder != null ) {
 			return propertyHolder.getEntityName() + "." + propertyName;
 		}
 		else {
 			return "";
 		}
 	}
 
 
 	/**
 	 * bind the inverse FK of a ManyToMany
 	 * If we are in a mappedBy case, read the columns from the associated
 	 * collection element
 	 * Otherwise delegates to the usual algorithm
 	 */
 	public static void bindManytoManyInverseFk(
 			PersistentClass referencedEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			MetadataBuildingContext buildingContext) {
 		final String mappedBy = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedBy ) ) {
 			final Property property = referencedEntity.getRecursiveProperty( mappedBy );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				mappedByColumns = ( (Collection) property.getValue() ).getKey().getColumnIterator();
 			}
 			else {
 				//find the appropriate reference key, can be in a join
 				Iterator joinsIt = referencedEntity.getJoinIterator();
 				KeyValue key = null;
 				while ( joinsIt.hasNext() ) {
 					Join join = (Join) joinsIt.next();
 					if ( join.containsProperty( property ) ) {
 						key = join.getKey();
 						break;
 					}
 				}
 				if ( key == null ) key = property.getPersistentClass().getIdentifier();
 				mappedByColumns = key.getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 			String referencedPropertyName =
 					buildingContext.getMetadataCollector().getPropertyReferencedAssociation(
 							"inverse__" + referencedEntity.getEntityName(), mappedBy
 					);
 			if ( referencedPropertyName != null ) {
 				//TODO always a many to one?
 				( (ManyToOne) value ).setReferencedPropertyName( referencedPropertyName );
 				buildingContext.getMetadataCollector().addUniquePropertyReference(
 						referencedEntity.getEntityName(),
 						referencedPropertyName
 				);
 			}
 			( (ManyToOne) value ).setReferenceToPrimaryKey( referencedPropertyName == null );
 			value.createForeignKey();
 		}
 		else {
 			BinderHelper.createSyntheticPropertyReference( columns, referencedEntity, null, value, true, buildingContext );
 			TableBinder.bindFk( referencedEntity, null, columns, value, unique, buildingContext );
 		}
 	}
 
 	public void setFkJoinColumns(Ejb3JoinColumn[] ejb3JoinColumns) {
 		this.fkJoinColumns = ejb3JoinColumns;
 	}
 
 	public void setExplicitAssociationTable(boolean explicitAssocTable) {
 		this.isExplicitAssociationTable = explicitAssocTable;
 	}
 
 	public void setElementColumns(Ejb3Column[] elementColumns) {
 		this.elementColumns = elementColumns;
 	}
 
 	public void setEmbedded(boolean annotationPresent) {
 		this.isEmbedded = annotationPresent;
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 	public void setMapKeyColumns(Ejb3Column[] mapKeyColumns) {
 		this.mapKeyColumns = mapKeyColumns;
 	}
 
 	public void setMapKeyManyToManyColumns(Ejb3JoinColumn[] mapJoinColumns) {
 		this.mapKeyManyToManyColumns = mapJoinColumns;
 	}
 
 	public void setLocalGenerators(HashMap<String, IdentifierGeneratorDefinition> localGenerators) {
 		this.localGenerators = localGenerators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
index 9e008ec493..a0a5c7a5aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
@@ -1,430 +1,430 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionPropertyHolder;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.sql.Template;
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.MapKeyClass;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Random;
 
 /**
  * Implementation to bind a Map
  *
  * @author Emmanuel Bernard
  */
 public class MapBinder extends CollectionBinder {
 	public MapBinder(boolean sorted) {
 		super( sorted );
 	}
 
 	public boolean isMap() {
 		return true;
 	}
 
 	protected Collection createCollection(PersistentClass persistentClass) {
 		return new org.hibernate.mapping.Map( getBuildingContext().getMetadataCollector(), persistentClass );
 	}
 
 	@Override
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final MetadataBuildingContext buildingContext) {
 		return new CollectionSecondPass( buildingContext, MapBinder.this.collection ) {
 			public void secondPass(Map persistentClasses, Map inheritedMetas)
 					throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, buildingContext
 				);
 				bindKeyFromAssociationTable(
 						collType, persistentClasses, mapKeyPropertyName, property, isEmbedded, buildingContext,
 						mapKeyColumns, mapKeyManyToManyColumns,
 						inverseColumns != null ? inverseColumns[0].getPropertyName() : null
 				);
 			}
 		};
 	}
 
 	private void bindKeyFromAssociationTable(
 			XClass collType,
 			Map persistentClasses,
 			String mapKeyPropertyName,
 			XProperty property,
 			boolean isEmbedded,
 			MetadataBuildingContext buildingContext,
 			Ejb3Column[] mapKeyColumns,
 			Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			String targetPropertyName) {
 		if ( mapKeyPropertyName != null ) {
 			//this is an EJB3 @MapKey
 			PersistentClass associatedClass = (PersistentClass) persistentClasses.get( collType.getName() );
 			if ( associatedClass == null ) throw new AnnotationException( "Associated class not found: " + collType );
 			Property mapProperty = BinderHelper.findPropertyByName( associatedClass, mapKeyPropertyName );
 			if ( mapProperty == null ) {
 				throw new AnnotationException(
 						"Map key property not found: " + collType + "." + mapKeyPropertyName
 				);
 			}
 			org.hibernate.mapping.Map map = (org.hibernate.mapping.Map) this.collection;
 			Value indexValue = createFormulatedValue(
 					mapProperty.getValue(), map, targetPropertyName, associatedClass, buildingContext
 			);
 			map.setIndex( indexValue );
 		}
 		else {
 			//this is a true Map mapping
 			//TODO ugly copy/pastle from CollectionBinder.bindManyToManySecondPass
 			String mapKeyType;
 			Class target = void.class;
 			/*
 			 * target has priority over reflection for the map key type
 			 * JPA 2 has priority
 			 */
 			if ( property.isAnnotationPresent( MapKeyClass.class ) ) {
 				target = property.getAnnotation( MapKeyClass.class ).value();
 			}
 			if ( !void.class.equals( target ) ) {
 				mapKeyType = target.getName();
 			}
 			else {
 				mapKeyType = property.getMapKey().getName();
 			}
 			PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( mapKeyType );
 			boolean isIndexOfEntities = collectionEntity != null;
 			ManyToOne element = null;
 			org.hibernate.mapping.Map mapValue = (org.hibernate.mapping.Map) this.collection;
 			if ( isIndexOfEntities ) {
 				element = new ManyToOne( buildingContext.getMetadataCollector(), mapValue.getCollectionTable() );
 				mapValue.setIndex( element );
 				element.setReferencedEntityName( mapKeyType );
 				//element.setFetchMode( fetchMode );
 				//element.setLazy( fetchMode != FetchMode.JOIN );
 				//make the second join non lazy
 				element.setFetchMode( FetchMode.JOIN );
 				element.setLazy( false );
 				//does not make sense for a map key element.setIgnoreNotFound( ignoreNotFound );
 			}
 			else {
 				XClass keyXClass;
 				AnnotatedClassType classType;
 				if ( BinderHelper.PRIMITIVE_NAMES.contains( mapKeyType ) ) {
 					classType = AnnotatedClassType.NONE;
 					keyXClass = null;
 				}
 				else {
 					try {
 						keyXClass = buildingContext.getBuildingOptions().getReflectionManager().classForName( mapKeyType );
 					}
 					catch (ClassLoadingException e) {
 						throw new AnnotationException( "Unable to find class: " + mapKeyType, e );
 					}
 					classType = buildingContext.getMetadataCollector().getClassType( keyXClass );
 					//force in case of attribute override
 					boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 							|| property.isAnnotationPresent( AttributeOverrides.class );
 					if ( isEmbedded || attributeOverride ) {
 						classType = AnnotatedClassType.EMBEDDABLE;
 					}
 				}
 
 				CollectionPropertyHolder holder = PropertyHolderBuilder.buildPropertyHolder(
 						mapValue,
 						StringHelper.qualify( mapValue.getRole(), "mapkey" ),
 						keyXClass,
 						property,
 						propertyHolder,
 						buildingContext
 				);
 
 
 				// 'propertyHolder' is the PropertyHolder for the owner of the collection
 				// 'holder' is the CollectionPropertyHolder.
 				// 'property' is the collection XProperty
 				propertyHolder.startingProperty( property );
 				holder.prepare( property );
 
 				PersistentClass owner = mapValue.getOwner();
 				AccessType accessType;
 				// FIXME support @Access for collection of elements
 				// String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					accessType = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" )
 							? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					accessType = prop.getPropertyAccessorName().equals( "property" ) ? AccessType.PROPERTY
 							: AccessType.FIELD;
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 					EntityBinder entityBinder = new EntityBinder();
 
 					PropertyData inferredData;
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "index", keyXClass );
 					}
 					else {
 						//"key" is the JPA 2 prefix for map keys
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "key", keyXClass );
 					}
 
 					//TODO be smart with isNullable
 					Component component = AnnotationBinder.fillComponent(
 							holder,
 							inferredData,
 							accessType,
 							true,
 							entityBinder,
 							false,
 							false,
 							true,
 							buildingContext,
 							inheritanceStatePerClass
 					);
 					mapValue.setIndex( component );
 				}
 				else {
 					SimpleValueBinder elementBinder = new SimpleValueBinder();
 					elementBinder.setBuildingContext( buildingContext );
 					elementBinder.setReturnedClassName( mapKeyType );
 
 					Ejb3Column[] elementColumns = mapKeyColumns;
 					if ( elementColumns == null || elementColumns.length == 0 ) {
 						elementColumns = new Ejb3Column[1];
 						Ejb3Column column = new Ejb3Column();
 						column.setImplicit( false );
 						column.setNullable( true );
 						column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 						column.setLogicalColumnName( Collection.DEFAULT_KEY_COLUMN_NAME );
 						//TODO create an EMPTY_JOINS collection
 						column.setJoins( new HashMap<String, Join>() );
 						column.setBuildingContext( buildingContext );
 						column.bind();
 						elementColumns[0] = column;
 					}
 					//override the table
 					for (Ejb3Column column : elementColumns) {
 						column.setTable( mapValue.getCollectionTable() );
 					}
 					elementBinder.setColumns( elementColumns );
 					//do not call setType as it extract the type from @Type
 					//the algorithm generally does not apply for map key anyway
 					elementBinder.setKey(true);
 					elementBinder.setType(
 							property,
 							keyXClass,
 							this.collection.getOwnerEntityName(),
-							holder.keyElementAttributeConverterDefinition( keyXClass )
+							holder.mapKeyAttributeConverterDescriptor( property, keyXClass )
 					);
 					elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 					elementBinder.setAccessType( accessType );
 					mapValue.setIndex( elementBinder.make() );
 				}
 			}
 			//FIXME pass the Index Entity JoinColumns
 			if ( !collection.isOneToMany() ) {
 				//index column shoud not be null
 				for (Ejb3JoinColumn col : mapKeyManyToManyColumns) {
 					col.forceNotNull();
 				}
 			}
 			if ( isIndexOfEntities ) {
 				bindManytoManyInverseFk(
 						collectionEntity,
 						mapKeyManyToManyColumns,
 						element,
 						false, //a map key column has no unique constraint
 						buildingContext
 				);
 			}
 		}
 	}
 
 	protected Value createFormulatedValue(
 			Value value,
 			Collection collection,
 			String targetPropertyName,
 			PersistentClass associatedClass,
 			MetadataBuildingContext buildingContext) {
 		Value element = collection.getElement();
 		String fromAndWhere = null;
 		if ( !( element instanceof OneToMany ) ) {
 			String referencedPropertyName = null;
 			if ( element instanceof ToOne ) {
 				referencedPropertyName = ( (ToOne) element ).getReferencedPropertyName();
 			}
 			else if ( element instanceof DependantValue ) {
 				//TODO this never happen I think
 				if ( propertyName != null ) {
 					referencedPropertyName = collection.getReferencedPropertyName();
 				}
 				else {
 					throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 				}
 			}
 			Iterator referencedEntityColumns;
 			if ( referencedPropertyName == null ) {
 				referencedEntityColumns = associatedClass.getIdentifier().getColumnIterator();
 			}
 			else {
 				Property referencedProperty = associatedClass.getRecursiveProperty( referencedPropertyName );
 				referencedEntityColumns = referencedProperty.getColumnIterator();
 			}
 			String alias = "$alias$";
 			StringBuilder fromAndWhereSb = new StringBuilder( " from " )
 					.append( associatedClass.getTable().getName() )
 							//.append(" as ") //Oracle doesn't support it in subqueries
 					.append( " " )
 					.append( alias ).append( " where " );
 			Iterator collectionTableColumns = element.getColumnIterator();
 			while ( collectionTableColumns.hasNext() ) {
 				Column colColumn = (Column) collectionTableColumns.next();
 				Column refColumn = (Column) referencedEntityColumns.next();
 				fromAndWhereSb.append( alias ).append( '.' ).append( refColumn.getQuotedName() )
 						.append( '=' ).append( colColumn.getQuotedName() ).append( " and " );
 			}
 			fromAndWhere = fromAndWhereSb.substring( 0, fromAndWhereSb.length() - 5 );
 		}
 
 		if ( value instanceof Component ) {
 			Component component = (Component) value;
 			Iterator properties = component.getPropertyIterator();
 			Component indexComponent = new Component( getBuildingContext().getMetadataCollector(), collection );
 			indexComponent.setComponentClassName( component.getComponentClassName() );
 			while ( properties.hasNext() ) {
 				Property current = (Property) properties.next();
 				Property newProperty = new Property();
 				newProperty.setCascade( current.getCascade() );
 				newProperty.setValueGenerationStrategy( current.getValueGenerationStrategy() );
 				newProperty.setInsertable( false );
 				newProperty.setUpdateable( false );
 				newProperty.setMetaAttributes( current.getMetaAttributes() );
 				newProperty.setName( current.getName() );
 				newProperty.setNaturalIdentifier( false );
 				//newProperty.setOptimisticLocked( false );
 				newProperty.setOptional( false );
 				newProperty.setPersistentClass( current.getPersistentClass() );
 				newProperty.setPropertyAccessorName( current.getPropertyAccessorName() );
 				newProperty.setSelectable( current.isSelectable() );
 				newProperty.setValue(
 						createFormulatedValue(
 								current.getValue(), collection, targetPropertyName, associatedClass, buildingContext
 						)
 				);
 				indexComponent.addProperty( newProperty );
 			}
 			return indexComponent;
 		}
 		else if ( value instanceof SimpleValue ) {
 			SimpleValue sourceValue = (SimpleValue) value;
 			SimpleValue targetValue;
 			if ( value instanceof ManyToOne ) {
 				ManyToOne sourceManyToOne = (ManyToOne) sourceValue;
 				ManyToOne targetManyToOne = new ManyToOne( getBuildingContext().getMetadataCollector(), collection.getCollectionTable() );
 				targetManyToOne.setFetchMode( FetchMode.DEFAULT );
 				targetManyToOne.setLazy( true );
 				//targetValue.setIgnoreNotFound( ); does not make sense for a map key
 				targetManyToOne.setReferencedEntityName( sourceManyToOne.getReferencedEntityName() );
 				targetValue = targetManyToOne;
 			}
 			else {
 				targetValue = new SimpleValue( getBuildingContext().getMetadataCollector(), collection.getCollectionTable() );
 				targetValue.setTypeName( sourceValue.getTypeName() );
 				targetValue.setTypeParameters( sourceValue.getTypeParameters() );
 			}
 			Iterator columns = sourceValue.getColumnIterator();
 			Random random = new Random();
 			while ( columns.hasNext() ) {
 				Object current = columns.next();
 				Formula formula = new Formula();
 				String formulaString;
 				if ( current instanceof Column ) {
 					formulaString = ( (Column) current ).getQuotedName();
 				}
 				else if ( current instanceof Formula ) {
 					formulaString = ( (Formula) current ).getFormula();
 				}
 				else {
 					throw new AssertionFailure( "Unknown element in column iterator: " + current.getClass() );
 				}
 				if ( fromAndWhere != null ) {
 					formulaString = Template.renderWhereStringTemplate( formulaString, "$alias$", new HSQLDialect() );
 					formulaString = "(select " + formulaString + fromAndWhere + ")";
 					formulaString = StringHelper.replace(
 							formulaString,
 							"$alias$",
 							"a" + random.nextInt( 16 )
 					);
 				}
 				formula.setFormula( formulaString );
 				targetValue.addFormula( formula );
 
 			}
 			return targetValue;
 		}
 		else {
 			throw new AssertionFailure( "Unknown type encounters for map key: " + value.getClass() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
index 928b345403..4704f7eba7 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
@@ -1,489 +1,489 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.lang.annotation.Annotation;
 import java.util.Map;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.HibernateException;
 import org.hibernate.annotations.Generated;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.NaturalId;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.ValueGenerationType;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.tuple.AnnotationValueGeneration;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.ValueGenerator;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class PropertyBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PropertyBinder.class.getName());
 
 	private MetadataBuildingContext buildingContext;
 
 	private String name;
 	private String returnedClassName;
 	private boolean lazy;
 	private AccessType accessType;
 	private Ejb3Column[] columns;
 	private PropertyHolder holder;
 	private Value value;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private String cascade;
 	private SimpleValueBinder simpleValueBinder;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private boolean embedded;
 	private EntityBinder entityBinder;
 	private boolean isXToMany;
 	private String referencedEntityName;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public void setEntityBinder(EntityBinder entityBinder) {
 		this.entityBinder = entityBinder;
 	}
 
 	/*
 			 * property can be null
 			 * prefer propertyName to property.getName() since some are overloaded
 			 */
 	private XProperty property;
 	private XClass returnedClass;
 	private boolean isId;
 	private Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private Property mappingProperty;
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		insertable = columns[0].isInsertable();
 		updatable = columns[0].isUpdatable();
 		//consistency is checked later when we know the property name
 		this.columns = columns;
 	}
 
 	public void setHolder(PropertyHolder holder) {
 		this.holder = holder;
 	}
 
 	public void setValue(Value value) {
 		this.value = value;
 	}
 
 	public void setCascade(String cascadeStrategy) {
 		this.cascade = cascadeStrategy;
 	}
 
 	public void setBuildingContext(MetadataBuildingContext buildingContext) {
 		this.buildingContext = buildingContext;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	private void validateBind() {
 		if ( property.isAnnotationPresent( Immutable.class ) ) {
 			throw new AnnotationException(
 					"@Immutable on property not allowed. " +
 							"Only allowed on entity level or on a collection."
 			);
 		}
 		if ( !declaringClassSet ) {
 			throw new AssertionFailure( "declaringClass has not been set before a bind" );
 		}
 	}
 
 	private void validateMake() {
 		//TODO check necessary params for a make
 	}
 
 	private Property makePropertyAndValue() {
 		validateBind();
 
 		LOG.debugf( "MetadataSourceProcessor property %s with lazy=%s", name, lazy );
 		final String containerClassName = holder.getClassName();
 		holder.startingProperty( property );
 
 		simpleValueBinder = new SimpleValueBinder();
 		simpleValueBinder.setBuildingContext( buildingContext );
 		simpleValueBinder.setPropertyName( name );
 		simpleValueBinder.setReturnedClassName( returnedClassName );
 		simpleValueBinder.setColumns( columns );
 		simpleValueBinder.setPersistentClassName( containerClassName );
 		simpleValueBinder.setType(
 				property,
 				returnedClass,
 				containerClassName,
-				holder.resolveAttributeConverterDefinition( property )
+				holder.resolveAttributeConverterDescriptor( property )
 		);
 		simpleValueBinder.setReferencedEntityName( referencedEntityName );
 		simpleValueBinder.setAccessType( accessType );
 		SimpleValue propertyValue = simpleValueBinder.make();
 		setValue( propertyValue );
 		return makeProperty();
 	}
 
 	//used when value is provided
 	public Property makePropertyAndBind() {
 		return bind( makeProperty() );
 	}
 
 	//used to build everything from scratch
 	public Property makePropertyValueAndBind() {
 		return bind( makePropertyAndValue() );
 	}
 
 	public void setXToMany(boolean xToMany) {
 		this.isXToMany = xToMany;
 	}
 
 	private Property bind(Property prop) {
 		if (isId) {
 			final RootClass rootClass = ( RootClass ) holder.getPersistentClass();
 			//if an xToMany, it as to be wrapped today.
 			//FIXME this pose a problem as the PK is the class instead of the associated class which is not really compliant with the spec
 			if ( isXToMany || entityBinder.wrapIdsInEmbeddedComponents() ) {
 				Component identifier = (Component) rootClass.getIdentifier();
 				if (identifier == null) {
 					identifier = AnnotationBinder.createComponent(
 							holder,
 							new PropertyPreloadedData(null, null, null),
 							true,
 							false,
 							buildingContext
 					);
 					rootClass.setIdentifier( identifier );
 					identifier.setNullValue( "undefined" );
 					rootClass.setEmbeddedIdentifier( true );
 					rootClass.setIdentifierMapper( identifier );
 				}
 				//FIXME is it good enough?
 				identifier.addProperty( prop );
 			}
 			else {
 				rootClass.setIdentifier( ( KeyValue ) getValue() );
 				if (embedded) {
 					rootClass.setEmbeddedIdentifier( true );
 				}
 				else {
 					rootClass.setIdentifierProperty( prop );
 					final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 							declaringClass,
 							inheritanceStatePerClass,
 							buildingContext
 					);
 					if (superclass != null) {
 						superclass.setDeclaredIdentifierProperty(prop);
 					}
 					else {
 						//we know the property is on the actual entity
 						rootClass.setDeclaredIdentifierProperty( prop );
 					}
 				}
 			}
 		}
 		else {
 			holder.addProperty( prop, columns, declaringClass );
 		}
 		return prop;
 	}
 
 	//used when the value is provided and the binding is done elsewhere
 	public Property makeProperty() {
 		validateMake();
 		LOG.debugf( "Building property %s", name );
 		Property prop = new Property();
 		prop.setName( name );
 		prop.setValue( value );
 		prop.setLazy( lazy );
 		prop.setCascade( cascade );
 		prop.setPropertyAccessorName( accessType.getType() );
 
 		if ( property != null ) {
 			prop.setValueGenerationStrategy( determineValueGenerationStrategy( property ) );
 		}
 
 		NaturalId naturalId = property != null ? property.getAnnotation( NaturalId.class ) : null;
 		if ( naturalId != null ) {
 			if ( ! entityBinder.isRootEntity() ) {
 				throw new AnnotationException( "@NaturalId only valid on root entity (or its @MappedSuperclasses)" );
 			}
 			if ( ! naturalId.mutable() ) {
 				updatable = false;
 			}
 			prop.setNaturalIdentifier( true );
 		}
 
 		// HHH-4635 -- needed for dialect-specific property ordering
 		Lob lob = property != null ? property.getAnnotation( Lob.class ) : null;
 		prop.setLob( lob != null );
 
 		prop.setInsertable( insertable );
 		prop.setUpdateable( updatable );
 
 		// this is already handled for collections in CollectionBinder...
 		if ( Collection.class.isInstance( value ) ) {
 			prop.setOptimisticLocked( ( (Collection) value ).isOptimisticLocked() );
 		}
 		else {
 			final OptimisticLock lockAnn = property != null
 					? property.getAnnotation( OptimisticLock.class )
 					: null;
 			if ( lockAnn != null ) {
 				//TODO this should go to the core as a mapping validation checking
 				if ( lockAnn.excluded() && (
 						property.isAnnotationPresent( javax.persistence.Version.class )
 								|| property.isAnnotationPresent( Id.class )
 								|| property.isAnnotationPresent( EmbeddedId.class ) ) ) {
 					throw new AnnotationException(
 							"@OptimisticLock.exclude=true incompatible with @Id, @EmbeddedId and @Version: "
 									+ StringHelper.qualify( holder.getPath(), name )
 					);
 				}
 			}
 			final boolean isOwnedValue = !isToOneValue( value ) || insertable; // && updatable as well???
 			final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 					? ! lockAnn.excluded()
 					: isOwnedValue;
 			prop.setOptimisticLocked( includeInOptimisticLockChecks );
 		}
 
 		LOG.tracev( "Cascading {0} with {1}", name, cascade );
 		this.mappingProperty = prop;
 		return prop;
 	}
 
 	private ValueGeneration determineValueGenerationStrategy(XProperty property) {
 		ValueGeneration valueGeneration = getValueGenerationFromAnnotations( property );
 
 		if ( valueGeneration == null ) {
 			return NoValueGeneration.INSTANCE;
 		}
 
 		final GenerationTiming when = valueGeneration.getGenerationTiming();
 
 		if ( valueGeneration.getValueGenerator() == null ) {
 			insertable = false;
 			if ( when == GenerationTiming.ALWAYS ) {
 				updatable = false;
 			}
 		}
 
 		return valueGeneration;
 	}
 
 	/**
 	 * Returns the value generation strategy for the given property, if any.
 	 */
 	private ValueGeneration getValueGenerationFromAnnotations(XProperty property) {
 		AnnotationValueGeneration<?> valueGeneration = null;
 
 		for ( Annotation annotation : property.getAnnotations() ) {
 			AnnotationValueGeneration<?> candidate = getValueGenerationFromAnnotation( property, annotation );
 
 			if ( candidate != null ) {
 				if ( valueGeneration != null ) {
 					throw new AnnotationException(
 							"Only one generator annotation is allowed:" + StringHelper.qualify(
 									holder.getPath(),
 									name
 							)
 					);
 				}
 				else {
 					valueGeneration = candidate;
 				}
 			}
 		}
 
 		return valueGeneration;
 	}
 
 	/**
 	 * In case the given annotation is a value generator annotation, the corresponding value generation strategy to be
 	 * applied to the given property is returned, {@code null} otherwise.
 	 */
 	private <A extends Annotation> AnnotationValueGeneration<A> getValueGenerationFromAnnotation(
 			XProperty property,
 			A annotation) {
 		ValueGenerationType generatorAnnotation = annotation.annotationType()
 				.getAnnotation( ValueGenerationType.class );
 
 		if ( generatorAnnotation == null ) {
 			return null;
 		}
 
 		Class<? extends AnnotationValueGeneration<?>> generationType = generatorAnnotation.generatedBy();
 		AnnotationValueGeneration<A> valueGeneration = instantiateAndInitializeValueGeneration(
 				annotation, generationType, property
 		);
 
 		if ( annotation.annotationType() == Generated.class &&
 				property.isAnnotationPresent( javax.persistence.Version.class ) &&
 				valueGeneration.getGenerationTiming() == GenerationTiming.INSERT ) {
 
 			throw new AnnotationException(
 					"@Generated(INSERT) on a @Version property not allowed, use ALWAYS (or NEVER): "
 							+ StringHelper.qualify( holder.getPath(), name )
 			);
 		}
 
 		return valueGeneration;
 	}
 
 	/**
 	 * Instantiates the given generator annotation type, initializing it with the given instance of the corresponding
 	 * generator annotation and the property's type.
 	 */
 	private <A extends Annotation> AnnotationValueGeneration<A> instantiateAndInitializeValueGeneration(
 			A annotation,
 			Class<? extends AnnotationValueGeneration<?>> generationType,
 			XProperty property) {
 
 		try {
 			// This will cause a CCE in case the generation type doesn't match the annotation type; As this would be a
 			// programming error of the generation type developer and thus should show up during testing, we don't
 			// check this explicitly; If required, this could be done e.g. using ClassMate
 			@SuppressWarnings( "unchecked" )
 			AnnotationValueGeneration<A> valueGeneration = (AnnotationValueGeneration<A>) generationType.newInstance();
 			valueGeneration.initialize(
 					annotation,
 					buildingContext.getBuildingOptions().getReflectionManager().toClass( property.getType() )
 			);
 
 			return valueGeneration;
 		}
 		catch (HibernateException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Exception occurred during processing of generator annotation:" + StringHelper.qualify(
 							holder.getPath(),
 							name
 					), e
 			);
 		}
 	}
 
 	private static class NoValueGeneration implements ValueGeneration {
 		/**
 		 * Singleton access
 		 */
 		public static final NoValueGeneration INSTANCE = new NoValueGeneration();
 
 		@Override
 		public GenerationTiming getGenerationTiming() {
 			return GenerationTiming.NEVER;
 		}
 
 		@Override
 		public ValueGenerator<?> getValueGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean referenceColumnInSql() {
 			return true;
 		}
 
 		@Override
 		public String getDatabaseGeneratedReferencedColumnValue() {
 			return null;
 		}
 	}
 
 	private boolean isToOneValue(Value value) {
 		return ToOne.class.isInstance( value );
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setReturnedClass(XClass returnedClass) {
 		this.returnedClass = returnedClass;
 	}
 
 	public SimpleValueBinder getSimpleValueBinder() {
 		return simpleValueBinder;
 	}
 
 	public Value getValue() {
 		return value;
 	}
 
 	public void setId(boolean id) {
 		this.isId = id;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index f70b186d04..0518f6accb 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,548 +1,549 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.io.Serializable;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.Properties;
 import javax.persistence.Enumerated;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.Nationalized;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.ClassLoadingException;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.annotations.common.util.StandardClassLoaderDelegateImpl;
 import org.hibernate.boot.model.TypeDefinition;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.CharacterArrayClobType;
 import org.hibernate.type.CharacterArrayNClobType;
 import org.hibernate.type.CharacterNCharType;
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
 import org.hibernate.type.PrimitiveCharacterArrayNClobType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.StringNVarcharType;
 import org.hibernate.type.WrappedMaterializedBlobType;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SimpleValueBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SimpleValueBinder.class.getName());
 
 	private MetadataBuildingContext buildingContext;
 
 	private String propertyName;
 	private String returnedClassName;
 	private Ejb3Column[] columns;
 	private String persistentClassName;
 	private String explicitType = "";
 	private String defaultType = "";
 	private Properties typeParameters = new Properties();
 	private boolean isNationalized;
 	private boolean isLob;
 
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
 	private XProperty xproperty;
 	private AccessType accessType;
 
-	private AttributeConverterDefinition attributeConverterDefinition;
+	private AttributeConverterDescriptor attributeConverterDescriptor;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
 	}
 
 	public void setVersion(boolean isVersion) {
 		this.isVersion = isVersion;
 	}
 
 	public void setTimestampVersionType(String versionType) {
 		this.timeStampVersionType = versionType;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 
 		if ( defaultType.length() == 0 ) {
 			defaultType = returnedClassName;
 		}
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		this.columns = columns;
 	}
 
 
 	public void setPersistentClassName(String persistentClassName) {
 		this.persistentClassName = persistentClassName;
 	}
 
 	//TODO execute it lazily to be order safe
 
-	public void setType(XProperty property, XClass returnedClass, String declaringClassName, AttributeConverterDefinition attributeConverterDefinition) {
+	public void setType(XProperty property, XClass returnedClass, String declaringClassName, AttributeConverterDescriptor attributeConverterDescriptor) {
 		if ( returnedClass == null ) {
 			// we cannot guess anything
 			return;
 		}
 
 		XClass returnedClassOrElement = returnedClass;
 		boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
 		this.xproperty = property;
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 		isNationalized = property.isAnnotationPresent( Nationalized.class )
 				|| buildingContext.getBuildingOptions().useNationalizedCharacterData();
 
 		Type annType = null;
 		if ( (!key && property.isAnnotationPresent( Type.class ))
 				|| (key && property.isAnnotationPresent( MapKeyType.class )) ) {
 			if ( key ) {
 				MapKeyType ann = property.getAnnotation( MapKeyType.class );
 				annType = ann.value();
 			}
 			else {
 				annType = property.getAnnotation( Type.class );
 			}
 		}
 
 		if ( annType != null ) {
 			setExplicitType( annType );
 			type = explicitType;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) ) {
 
 			boolean isDate;
 			if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Date.class ) ) {
 				isDate = true;
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Calendar.class ) ) {
 				isDate = false;
 			}
 			else {
 				throw new AnnotationException(
 						"@Temporal should only be set on a java.util.Date or java.util.Calendar property: "
 								+ StringHelper.qualify( persistentClassName, propertyName )
 				);
 			}
 			final TemporalType temporalType = getTemporalType( property );
 			switch ( temporalType ) {
 				case DATE:
 					type = isDate ? "date" : "calendar_date";
 					break;
 				case TIME:
 					type = "time";
 					if ( !isDate ) {
 						throw new NotYetImplementedException(
 								"Calendar cannot persist TIME only"
 										+ StringHelper.qualify( persistentClassName, propertyName )
 						);
 					}
 					break;
 				case TIMESTAMP:
 					type = isDate ? "timestamp" : "calendar";
 					break;
 				default:
 					throw new AssertionFailure( "Unknown temporal type: " + temporalType );
 			}
 			explicitType = type;
 		}
 		else if ( !key && property.isAnnotationPresent( Lob.class ) ) {
 			isLob = true;
 			if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, java.sql.Clob.class ) ) {
 				type = isNationalized
 						? StandardBasicTypes.NCLOB.getName()
 						: StandardBasicTypes.CLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, java.sql.NClob.class ) ) {
 				type = StandardBasicTypes.NCLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				type = isNationalized
 						? StandardBasicTypes.MATERIALIZED_NCLOB.getName()
 						: StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
 				type = isNationalized
 						? CharacterArrayNClobType.class.getName()
 						: CharacterArrayClobType.class.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
 				type = isNationalized
 						? PrimitiveCharacterArrayNClobType.class.getName()
 						: PrimitiveCharacterArrayClobType.class.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Byte.class ) && isArray ) {
 				type = WrappedMaterializedBlobType.class.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, byte.class ) && isArray ) {
 				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager()
 					.toXClass( Serializable.class )
 					.isAssignableFrom( returnedClassOrElement ) ) {
 				type = SerializableToBlobType.class.getName();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
 			defaultType = type;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Enumerated.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyEnumerated.class ) ) ) {
 			final Class attributeJavaType = buildingContext.getBuildingOptions().getReflectionManager().toClass( returnedClassOrElement );
 			if ( !Enum.class.isAssignableFrom( attributeJavaType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"Attribute [%s.%s] was annotated as enumerated, but its java type is not an enum [%s]",
 								declaringClassName,
 								xproperty.getName(),
 								attributeJavaType.getName()
 						)
 				);
 			}
 			type = EnumType.class.getName();
 			explicitType = type;
 		}
 		else if ( isNationalized ) {
 			if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				// nvarchar
 				type = StringNVarcharType.INSTANCE.getName();
 				explicitType = type;
 			}
 			else if ( buildingContext.getBuildingOptions().getReflectionManager().equals( returnedClassOrElement, Character.class ) ) {
 				if ( isArray ) {
 					// nvarchar
 					type = StringNVarcharType.INSTANCE.getName();
 				}
 				else {
 					// nchar
 					type = CharacterNCharType.INSTANCE.getName();
 				}
 				explicitType = type;
 			}
 		}
 
 		// implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
 		}
 
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
 			}
 		}
 
 		defaultType = BinderHelper.isEmptyAnnotationValue( type ) ? returnedClassName : type;
 		this.typeParameters = typeParameters;
 
-		applyAttributeConverter( property, attributeConverterDefinition );
+		applyAttributeConverter( property, attributeConverterDescriptor );
 	}
 
-	private void applyAttributeConverter(XProperty property, AttributeConverterDefinition attributeConverterDefinition) {
-		if ( attributeConverterDefinition == null ) {
+	private void applyAttributeConverter(XProperty property, AttributeConverterDescriptor attributeConverterDescriptor) {
+		if ( attributeConverterDescriptor == null ) {
 			return;
 		}
 
 		LOG.debugf( "Starting applyAttributeConverter [%s:%s]", persistentClassName, property.getName() );
 
 		if ( property.isAnnotationPresent( Id.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Id attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( isVersion ) {
 			LOG.debugf( "Skipping AttributeConverter checks for version attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( property.isAnnotationPresent( Temporal.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Temporal attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( property.isAnnotationPresent( Enumerated.class ) ) {
 			LOG.debugf( "Skipping AttributeConverter checks for Enumerated attribute [%s]", property.getName() );
 			return;
 		}
 
 		if ( isAssociation() ) {
 			LOG.debugf( "Skipping AttributeConverter checks for association attribute [%s]", property.getName() );
 			return;
 		}
 
-		this.attributeConverterDefinition = attributeConverterDefinition;
+		this.attributeConverterDescriptor = attributeConverterDescriptor;
 	}
 
 	private boolean isAssociation() {
 		// todo : this information is only known to caller(s), need to pass that information in somehow.
 		// or, is this enough?
 		return referencedEntityName != null;
 	}
 
 	private TemporalType getTemporalType(XProperty property) {
 		if ( key ) {
 			MapKeyTemporal ann = property.getAnnotation( MapKeyTemporal.class );
 			return ann.value();
 		}
 		else {
 			Temporal ann = property.getAnnotation( Temporal.class );
 			return ann.value();
 		}
 	}
 
 	public void setExplicitType(String explicitType) {
 		this.explicitType = explicitType;
 	}
 
 	//FIXME raise an assertion failure  if setResolvedTypeMapping(String) and setResolvedTypeMapping(Type) are use at the same time
 
 	public void setExplicitType(Type typeAnn) {
 		if ( typeAnn != null ) {
 			explicitType = typeAnn.type();
 			typeParameters.clear();
 			for ( Parameter param : typeAnn.parameters() ) {
 				typeParameters.setProperty( param.name(), param.value() );
 			}
 		}
 	}
 
 	public void setBuildingContext(MetadataBuildingContext buildingContext) {
 		this.buildingContext = buildingContext;
 	}
 
 	private void validate() {
 		//TODO check necessary params
 		Ejb3Column.checkPropertyConsistency( columns, propertyName );
 	}
 
 	public SimpleValue make() {
 
 		validate();
 		LOG.debugf( "building SimpleValue for %s", propertyName );
 		if ( table == null ) {
 			table = columns[0].getTable();
 		}
 		simpleValue = new SimpleValue( buildingContext.getMetadataCollector(), table );
 		if ( isNationalized ) {
 			simpleValue.makeNationalized();
 		}
 		if ( isLob ) {
 			simpleValue.makeLob();
 		}
 
 		linkWithValue();
 
 		boolean isInSecondPass = buildingContext.getMetadataCollector().isInSecondPass();
 		if ( !isInSecondPass ) {
 			//Defer this to the second pass
 			buildingContext.getMetadataCollector().addSecondPass( new SetSimpleValueTypeSecondPass( this ) );
 		}
 		else {
 			//We are already in second pass
 			fillSimpleValue();
 		}
 		return simpleValue;
 	}
 
 	public void linkWithValue() {
 		if ( columns[0].isNameDeferred() && !buildingContext.getMetadataCollector().isInSecondPass() && referencedEntityName != null ) {
 			buildingContext.getMetadataCollector().addSecondPass(
 					new PkDrivenByDefaultMapsIdSecondPass(
 							referencedEntityName, (Ejb3JoinColumn[]) columns, simpleValue
 					)
 			);
 		}
 		else {
 			for ( Ejb3Column column : columns ) {
 				column.linkWithValue( simpleValue );
 			}
 		}
 	}
 
 	public void fillSimpleValue() {
 		LOG.debugf( "Starting fillSimpleValue for %s", propertyName );
                 
-		if ( attributeConverterDefinition != null ) {
+		if ( attributeConverterDescriptor != null ) {
 			if ( ! BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"AttributeConverter and explicit Type cannot be applied to same attribute [%s.%s];" +
 										"remove @Type or specify @Convert(disableConversion = true)",
 								persistentClassName,
 								propertyName
 						)
 				);
 			}
 			LOG.debugf(
 					"Applying JPA AttributeConverter [%s] to [%s:%s]",
-					attributeConverterDefinition,
+					attributeConverterDescriptor,
 					persistentClassName,
 					propertyName
 			);
-			simpleValue.setJpaAttributeConverterDefinition( attributeConverterDefinition );
+			simpleValue.setJpaAttributeConverterDescriptor( attributeConverterDescriptor );
 		}
 		else {
 			String type;
 			TypeDefinition typeDef;
 
 			if ( !BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				type = explicitType;
 				typeDef = buildingContext.getMetadataCollector().getTypeDefinition( type );
 			}
 			else {
 				// try implicit type
 				TypeDefinition implicitTypeDef = buildingContext.getMetadataCollector().getTypeDefinition( returnedClassName );
 				if ( implicitTypeDef != null ) {
 					typeDef = implicitTypeDef;
 					type = returnedClassName;
 				}
 				else {
 					typeDef = buildingContext.getMetadataCollector().getTypeDefinition( defaultType );
 					type = defaultType;
 				}
 			}
 
 			if ( typeDef != null ) {
 				type = typeDef.getTypeImplementorClass().getName();
 				simpleValue.setTypeParameters( typeDef.getParametersAsProperties() );
 			}
 			if ( typeParameters != null && typeParameters.size() != 0 ) {
 				//explicit type params takes precedence over type def params
 				simpleValue.setTypeParameters( typeParameters );
 			}
 			simpleValue.setTypeName( type );
 		}
 
-		if ( persistentClassName != null || attributeConverterDefinition != null ) {
+		if ( persistentClassName != null || attributeConverterDescriptor != null ) {
 			try {
 				simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 			}
 			catch (Exception e) {
 				throw new MappingException(
 						String.format(
 								Locale.ROOT,
 								"Unable to determine basic type mapping via reflection [%s -> %s]",
 								persistentClassName,
 								propertyName
 						),
 						e
 				);
 			}
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
 		
 		if ( simpleValue.getTypeName() != null && simpleValue.getTypeName().length() > 0
 				&& simpleValue.getMetadata().getTypeResolver().basic( simpleValue.getTypeName() ) == null ) {
 			try {
 				Class typeClass = buildingContext.getClassLoaderAccess().classForName( simpleValue.getTypeName() );
 
 				if ( typeClass != null && DynamicParameterizedType.class.isAssignableFrom( typeClass ) ) {
 					Properties parameters = simpleValue.getTypeParameters();
 					if ( parameters == null ) {
 						parameters = new Properties();
 					}
 					parameters.put( DynamicParameterizedType.IS_DYNAMIC, Boolean.toString( true ) );
 					parameters.put( DynamicParameterizedType.RETURNED_CLASS, returnedClassName );
 					parameters.put( DynamicParameterizedType.IS_PRIMARY_KEY, Boolean.toString( key ) );
 
 					parameters.put( DynamicParameterizedType.ENTITY, persistentClassName );
 					parameters.put( DynamicParameterizedType.XPROPERTY, xproperty );
 					parameters.put( DynamicParameterizedType.PROPERTY, xproperty.getName() );
 					parameters.put( DynamicParameterizedType.ACCESS_TYPE, accessType.getType() );
 					simpleValue.setTypeParameters( parameters );
 				}
 			}
 			catch (ClassLoadingException e) {
 				throw new MappingException( "Could not determine type for: " + simpleValue.getTypeName(), e );
 			}
 		}
 
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
 
 	public AccessType getAccessType() {
 		return accessType;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
index 0f5e94a893..2a22006bf0 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/StringHelper.java
@@ -1,806 +1,832 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal.util;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.BitSet;
+import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 public final class StringHelper {
 
 	private static final int ALIAS_TRUNCATE_LENGTH = 10;
 	public static final String WHITESPACE = " \n\r\f\t";
 	public static final String[] EMPTY_STRINGS = new String[0];
 
 	private StringHelper() { /* static methods only - hide constructor */
 	}
 
 	public static int lastIndexOfLetter(String string) {
 		for ( int i = 0; i < string.length(); i++ ) {
 			char character = string.charAt( i );
 			// Include "_".  See HHH-8073
 			if ( !Character.isLetter( character ) && !( '_' == character ) ) {
 				return i - 1;
 			}
 		}
 		return string.length() - 1;
 	}
 
 	public static String join(String seperator, String[] strings) {
 		int length = strings.length;
 		if ( length == 0 ) {
 			return "";
 		}
 		StringBuilder buf = new StringBuilder( length * strings[0].length() )
 				.append( strings[0] );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( seperator ).append( strings[i] );
 		}
 		return buf.toString();
 	}
 
 	public static String joinWithQualifierAndSuffix(
 			String[] values,
 			String qualifier,
 			String suffix,
 			String deliminator) {
 		int length = values.length;
 		if ( length == 0 ) {
 			return "";
 		}
 		StringBuilder buf = new StringBuilder( length * ( values[0].length() + suffix.length() ) )
 				.append( qualify( qualifier, values[0] ) ).append( suffix );
 		for ( int i = 1; i < length; i++ ) {
 			buf.append( deliminator ).append( qualify( qualifier, values[i] ) ).append( suffix );
 		}
 		return buf.toString();
 	}
 
 	public static String join(String seperator, Iterator objects) {
 		StringBuilder buf = new StringBuilder();
 		if ( objects.hasNext() ) {
 			buf.append( objects.next() );
 		}
 		while ( objects.hasNext() ) {
 			buf.append( seperator ).append( objects.next() );
 		}
 		return buf.toString();
 	}
 
 	public static String join(String separator, Iterable objects) {
 		return join( separator, objects.iterator() );
 	}
 
 	public static String[] add(String[] x, String sep, String[] y) {
 		final String[] result = new String[x.length];
 		for ( int i = 0; i < x.length; i++ ) {
 			result[i] = x[i] + sep + y[i];
 		}
 		return result;
 	}
 
 	public static String repeat(String string, int times) {
 		StringBuilder buf = new StringBuilder( string.length() * times );
 		for ( int i = 0; i < times; i++ ) {
 			buf.append( string );
 		}
 		return buf.toString();
 	}
 
 	public static String repeat(String string, int times, String deliminator) {
 		StringBuilder buf = new StringBuilder( ( string.length() * times ) + ( deliminator.length() * ( times - 1 ) ) )
 				.append( string );
 		for ( int i = 1; i < times; i++ ) {
 			buf.append( deliminator ).append( string );
 		}
 		return buf.toString();
 	}
 
 	public static String repeat(char character, int times) {
 		char[] buffer = new char[times];
 		Arrays.fill( buffer, character );
 		return new String( buffer );
 	}
 
 
 	public static String replace(String template, String placeholder, String replacement) {
 		return replace( template, placeholder, replacement, false );
 	}
 
 	public static String[] replace(String[] templates, String placeholder, String replacement) {
 		String[] result = new String[templates.length];
 		for ( int i = 0; i < templates.length; i++ ) {
 			result[i] = replace( templates[i], placeholder, replacement );
 		}
 		return result;
 	}
 
 	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
 		return replace( template, placeholder, replacement, wholeWords, false );
 	}
 
 	public static String replace(
 			String template,
 			String placeholder,
 			String replacement,
 			boolean wholeWords,
 			boolean encloseInParensIfNecessary) {
 		if ( template == null ) {
 			return null;
 		}
 		int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			String beforePlaceholder = template.substring( 0, loc );
 			String afterPlaceholder = template.substring( loc + placeholder.length() );
 			return replace(
 					beforePlaceholder,
 					afterPlaceholder,
 					placeholder,
 					replacement,
 					wholeWords,
 					encloseInParensIfNecessary
 			);
 		}
 	}
 
 
 	public static String replace(
 			String beforePlaceholder,
 			String afterPlaceholder,
 			String placeholder,
 			String replacement,
 			boolean wholeWords,
 			boolean encloseInParensIfNecessary) {
 		final boolean actuallyReplace =
 				!wholeWords
 						|| afterPlaceholder.length() == 0
 						|| !Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
 		boolean encloseInParens =
 				actuallyReplace
 						&& encloseInParensIfNecessary
 						&& !( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' )
 						&& !( getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')' );
 		StringBuilder buf = new StringBuilder( beforePlaceholder );
 		if ( encloseInParens ) {
 			buf.append( '(' );
 		}
 		buf.append( actuallyReplace ? replacement : placeholder );
 		if ( encloseInParens ) {
 			buf.append( ')' );
 		}
 		buf.append(
 				replace(
 						afterPlaceholder,
 						placeholder,
 						replacement,
 						wholeWords,
 						encloseInParensIfNecessary
 				)
 		);
 		return buf.toString();
 	}
 
 	public static char getLastNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = str.length() - 1; i >= 0; i-- ) {
 				char ch = str.charAt( i );
 				if ( !Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static char getFirstNonWhitespaceCharacter(String str) {
 		if ( str != null && str.length() > 0 ) {
 			for ( int i = 0; i < str.length(); i++ ) {
 				char ch = str.charAt( i );
 				if ( !Character.isWhitespace( ch ) ) {
 					return ch;
 				}
 			}
 		}
 		return '\0';
 	}
 
 	public static String replaceOnce(String template, String placeholder, String replacement) {
 		if ( template == null ) {
 			return null;  // returnign null!
 		}
 		int loc = template.indexOf( placeholder );
 		if ( loc < 0 ) {
 			return template;
 		}
 		else {
 			return template.substring( 0, loc ) + replacement + template.substring( loc + placeholder.length() );
 		}
 	}
 
 
 	public static String[] split(String seperators, String list) {
 		return split( seperators, list, false );
 	}
 
 	public static String[] split(String seperators, String list, boolean include) {
 		StringTokenizer tokens = new StringTokenizer( list, seperators, include );
 		String[] result = new String[tokens.countTokens()];
 		int i = 0;
 		while ( tokens.hasMoreTokens() ) {
 			result[i++] = tokens.nextToken();
 		}
 		return result;
 	}
 
 	public static String unqualify(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1 );
 	}
 
 	public static String qualifier(String qualifiedName) {
 		int loc = qualifiedName.lastIndexOf( "." );
 		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
 	}
 
 	/**
 	 * Collapses a name.  Mainly intended for use with classnames, where an example might serve best to explain.
 	 * Imagine you have a class named <samp>'org.hibernate.internal.util.StringHelper'</samp>; calling collapse on that
 	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
 	 *
 	 * @param name The name to collapse.
 	 *
 	 * @return The collapsed name.
 	 */
 	public static String collapse(String name) {
 		if ( name == null ) {
 			return null;
 		}
 		int breakPoint = name.lastIndexOf( '.' );
 		if ( breakPoint < 0 ) {
 			return name;
 		}
 		return collapseQualifier(
 				name.substring( 0, breakPoint ),
 				true
 		) + name.substring( breakPoint ); // includes last '.'
 	}
 
 	/**
 	 * Given a qualifier, collapse it.
 	 *
 	 * @param qualifier The qualifier to collapse.
 	 * @param includeDots Should we include the dots in the collapsed form?
 	 *
 	 * @return The collapsed form.
 	 */
 	public static String collapseQualifier(String qualifier, boolean includeDots) {
 		StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
 		String collapsed = Character.toString( tokenizer.nextToken().charAt( 0 ) );
 		while ( tokenizer.hasMoreTokens() ) {
 			if ( includeDots ) {
 				collapsed += '.';
 			}
 			collapsed += tokenizer.nextToken().charAt( 0 );
 		}
 		return collapsed;
 	}
 
 	/**
 	 * Partially unqualifies a qualified name.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself, or the partially unqualified form if it begins with the qualifier base.
 	 */
 	public static String partiallyUnqualify(String name, String qualifierBase) {
 		if ( name == null || !name.startsWith( qualifierBase ) ) {
 			return name;
 		}
 		return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
 	}
 
 	/**
 	 * Cross between {@link #collapse} and {@link #partiallyUnqualify}.  Functions much like {@link #collapse}
 	 * except that only the qualifierBase is collapsed.  For example, with a base of 'org.hibernate' the name
 	 * 'org.hibernate.internal.util.StringHelper' would become 'o.h.util.StringHelper'.
 	 *
 	 * @param name The (potentially) qualified name.
 	 * @param qualifierBase The qualifier base.
 	 *
 	 * @return The name itself if it does not begin with the qualifierBase, or the properly collapsed form otherwise.
 	 */
 	public static String collapseQualifierBase(String name, String qualifierBase) {
 		if ( name == null || !name.startsWith( qualifierBase ) ) {
 			return collapse( name );
 		}
 		return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
 	}
 
 	public static String[] suffix(String[] columns, String suffix) {
 		if ( suffix == null ) {
 			return columns;
 		}
 		String[] qualified = new String[columns.length];
 		for ( int i = 0; i < columns.length; i++ ) {
 			qualified[i] = suffix( columns[i], suffix );
 		}
 		return qualified;
 	}
 
 	private static String suffix(String name, String suffix) {
 		return ( suffix == null ) ? name : name + suffix;
 	}
 
 	public static String root(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
 	}
 
 	public static String unroot(String qualifiedName) {
 		int loc = qualifiedName.indexOf( "." );
 		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1, qualifiedName.length() );
 	}
 
 	public static boolean booleanValue(String tfString) {
 		String trimmed = tfString.trim().toLowerCase( Locale.ROOT );
 		return trimmed.equals( "true" ) || trimmed.equals( "t" );
 	}
 
 	public static String toString(Object[] array) {
 		int len = array.length;
 		if ( len == 0 ) {
 			return "";
 		}
 		StringBuilder buf = new StringBuilder( len * 12 );
 		for ( int i = 0; i < len - 1; i++ ) {
 			buf.append( array[i] ).append( ", " );
 		}
 		return buf.append( array[len - 1] ).toString();
 	}
 
 	public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
 		String[] result = new String[] {string};
 		while ( placeholders.hasNext() ) {
 			result = multiply( result, (String) placeholders.next(), (String[]) replacements.next() );
 		}
 		return result;
 	}
 
 	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
 		String[] results = new String[replacements.length * strings.length];
 		int n = 0;
 		for ( String replacement : replacements ) {
 			for ( String string : strings ) {
 				results[n++] = replaceOnce( string, placeholder, replacement );
 			}
 		}
 		return results;
 	}
 
 	public static int countUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if ( string == null ) {
 			return 0;
 		}
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int count = 0;
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				count++;
 			}
 		}
 		return count;
 	}
 
 	public static int[] locateUnquoted(String string, char character) {
 		if ( '\'' == character ) {
 			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
 		}
 		if ( string == null ) {
 			return new int[0];
 		}
 
 		ArrayList locations = new ArrayList( 20 );
 
 		// Impl note: takes advantage of the fact that an escpaed single quote
 		// embedded within a quote-block can really be handled as two seperate
 		// quote-blocks for the purposes of this method...
 		int stringLength = string.length();
 		boolean inQuote = false;
 		for ( int indx = 0; indx < stringLength; indx++ ) {
 			char c = string.charAt( indx );
 			if ( inQuote ) {
 				if ( '\'' == c ) {
 					inQuote = false;
 				}
 			}
 			else if ( '\'' == c ) {
 				inQuote = true;
 			}
 			else if ( c == character ) {
 				locations.add( indx );
 			}
 		}
 		return ArrayHelper.toIntArray( locations );
 	}
 
 	public static boolean isNotEmpty(String string) {
 		return string != null && string.length() > 0;
 	}
 
 	public static boolean isEmpty(String string) {
 		return string == null || string.length() == 0;
 	}
 
 	public static boolean isEmptyOrWhiteSpace(String string) {
 		return isEmpty( string ) || isEmpty( string.trim() );
 	}
 
 	public static String qualify(String prefix, String name) {
 		if ( name == null || prefix == null ) {
 			throw new NullPointerException( "prefix or name were null attempting to build qualified name" );
 		}
 		return prefix + '.' + name;
 	}
 
 	public static String[] qualify(String prefix, String[] names) {
 		if ( prefix == null ) {
 			return names;
 		}
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			qualified[i] = qualify( prefix, names[i] );
 		}
 		return qualified;
 	}
 
 	public static String[] qualifyIfNot(String prefix, String[] names) {
 		if ( prefix == null ) {
 			return names;
 		}
 		int len = names.length;
 		String[] qualified = new String[len];
 		for ( int i = 0; i < len; i++ ) {
 			if ( names[i].indexOf( '.' ) < 0 ) {
 				qualified[i] = qualify( prefix, names[i] );
 			}
 			else {
 				qualified[i] = names[i];
 			}
 		}
 		return qualified;
 	}
 
 	public static int firstIndexOfChar(String sqlString, BitSet keys, int startindex) {
 		for ( int i = startindex, size = sqlString.length(); i < size; i++ ) {
 			if ( keys.get( sqlString.charAt( i ) ) ) {
 				return i;
 			}
 		}
 		return -1;
 
 	}
 
 	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
 		BitSet keys = new BitSet();
 		for ( int i = 0, size = string.length(); i < size; i++ ) {
 			keys.set( string.charAt( i ) );
 		}
 		return firstIndexOfChar( sqlString, keys, startindex );
 
 	}
 
 	public static String truncate(String string, int length) {
 		if ( string.length() <= length ) {
 			return string;
 		}
 		else {
 			return string.substring( 0, length );
 		}
 	}
 
 	public static String generateAlias(String description) {
 		return generateAliasRoot( description ) + '_';
 	}
 
 	/**
 	 * Generate a nice alias for the given class name or collection role name and unique integer. Subclasses of
 	 * Loader do <em>not</em> have to use aliases of this form.
 	 *
 	 * @param description The base name (usually an entity-name or collection-role)
 	 * @param unique A uniquing value
 	 *
 	 * @return an alias of the form <samp>foo1_</samp>
 	 */
 	public static String generateAlias(String description, int unique) {
 		return generateAliasRoot( description )
 				+ Integer.toString( unique )
 				+ '_';
 	}
 
 	/**
 	 * Generates a root alias by truncating the "root name" defined by
 	 * the incoming decription and removing/modifying any non-valid
 	 * alias characters.
 	 *
 	 * @param description The root name from which to generate a root alias.
 	 *
 	 * @return The generated root alias.
 	 */
 	private static String generateAliasRoot(String description) {
 		String result = truncate( unqualifyEntityName( description ), ALIAS_TRUNCATE_LENGTH )
 				.toLowerCase( Locale.ROOT )
 				.replace( '/', '_' ) // entityNames may now include slashes for the representations
 				.replace( '$', '_' ); //classname may be an inner class
 		result = cleanAlias( result );
 		if ( Character.isDigit( result.charAt( result.length() - 1 ) ) ) {
 			return result + "x"; //ick!
 		}
 		else {
 			return result;
 		}
 	}
 
 	/**
 	 * Clean the generated alias by removing any non-alpha characters from the
 	 * beginning.
 	 *
 	 * @param alias The generated alias to be cleaned.
 	 *
 	 * @return The cleaned alias, stripped of any leading non-alpha characters.
 	 */
 	private static String cleanAlias(String alias) {
 		char[] chars = alias.toCharArray();
 		// short cut check...
 		if ( !Character.isLetter( chars[0] ) ) {
 			for ( int i = 1; i < chars.length; i++ ) {
 				// as soon as we encounter our first letter, return the substring
 				// from that position
 				if ( Character.isLetter( chars[i] ) ) {
 					return alias.substring( i );
 				}
 			}
 		}
 		return alias;
 	}
 
 	public static String unqualifyEntityName(String entityName) {
 		String result = unqualify( entityName );
 		int slashPos = result.indexOf( '/' );
 		if ( slashPos > 0 ) {
 			result = result.substring( 0, slashPos - 1 );
 		}
 		return result;
 	}
 
 	public static String moveAndToBeginning(String filter) {
 		if ( filter.trim().length() > 0 ) {
 			filter += " and ";
 			if ( filter.startsWith( " and " ) ) {
 				filter = filter.substring( 4 );
 			}
 		}
 		return filter;
 	}
 
 	/**
 	 * Determine if the given string is quoted (wrapped by '`' characters at beginning and end).
 	 *
 	 * @param name The name to check.
 	 *
 	 * @return True if the given string starts and ends with '`'; false otherwise.
 	 */
 	public static boolean isQuoted(String name) {
 		return name != null && name.length() != 0
 				&& ( ( name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`' )
 				|| ( name.charAt( 0 ) == '"' && name.charAt( name.length() - 1 ) == '"' ) );
 	}
 
 	/**
 	 * Return a representation of the given name ensuring quoting (wrapped with '`' characters).  If already wrapped
 	 * return name.
 	 *
 	 * @param name The name to quote.
 	 *
 	 * @return The quoted version.
 	 */
 	public static String quote(String name) {
 		if ( isEmpty( name ) || isQuoted( name ) ) {
 			return name;
 		}
 		// Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
 		else if ( name.startsWith( "\"" ) && name.endsWith( "\"" ) ) {
 			name = name.substring( 1, name.length() - 1 );
 		}
 
 		return "`" + name + '`';
 	}
 
 	/**
 	 * Return the unquoted version of name (stripping the start and end '`' characters if present).
 	 *
 	 * @param name The name to be unquoted.
 	 *
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name) {
 		return isQuoted( name ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Determine if the given name is quoted.  It is considered quoted if either:
 	 * <ol>
 	 * <li>starts AND ends with backticks (`)</li>
 	 * <li>starts with dialect-specified {@link org.hibernate.dialect.Dialect#openQuote() open-quote}
 	 * AND ends with dialect-specified {@link org.hibernate.dialect.Dialect#closeQuote() close-quote}</li>
 	 * </ol>
 	 *
 	 * @param name The name to check
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return True if quoted, false otherwise
 	 */
 	public static boolean isQuoted(String name, Dialect dialect) {
 		return name != null && name.length() != 0
 				&& ( ( name.charAt( 0 ) == '`' && name.charAt( name.length() - 1 ) == '`' )
 				|| ( name.charAt( 0 ) == '"' && name.charAt( name.length() - 1 ) == '"' )
 				|| ( name.charAt( 0 ) == dialect.openQuote()
 				&& name.charAt( name.length() - 1 ) == dialect.closeQuote() ) );
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param name The name to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted version.
 	 */
 	public static String unquote(String name, Dialect dialect) {
 		return isQuoted( name, dialect ) ? name.substring( 1, name.length() - 1 ) : name;
 	}
 
 	/**
 	 * Return the unquoted version of name stripping the start and end quote characters.
 	 *
 	 * @param names The names to be unquoted.
 	 * @param dialect The dialect (to determine the "real" quoting chars).
 	 *
 	 * @return The unquoted versions.
 	 */
 	public static String[] unquote(String[] names, Dialect dialect) {
 		if ( names == null ) {
 			return null;
 		}
 		String[] unquoted = new String[names.length];
 		for ( int i = 0; i < names.length; i++ ) {
 			unquoted[i] = unquote( names[i], dialect );
 		}
 		return unquoted;
 	}
 
 
 	public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";
 
 	public static StringBuilder buildBatchFetchRestrictionFragment(
 			String alias,
 			String[] columnNames,
 			Dialect dialect) {
 		// the general idea here is to just insert a placeholder that we can easily find later...
 		if ( columnNames.length == 1 ) {
 			// non-composite key
 			return new StringBuilder( StringHelper.qualify( alias, columnNames[0] ) )
 					.append( " in (" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 		}
 		else {
 			// composite key - the form to use here depends on what the dialect supports.
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				// use : (col1, col2) in ( (?,?), (?,?), ... )
 				StringBuilder builder = new StringBuilder();
 				builder.append( "(" );
 				boolean firstPass = true;
 				String deliminator = "";
 				for ( String columnName : columnNames ) {
 					builder.append( deliminator ).append( StringHelper.qualify( alias, columnName ) );
 					if ( firstPass ) {
 						firstPass = false;
 						deliminator = ",";
 					}
 				}
 				builder.append( ") in (" );
 				builder.append( BATCH_ID_PLACEHOLDER );
 				builder.append( ")" );
 				return builder;
 			}
 			else {
 				// use : ( (col1 = ? and col2 = ?) or (col1 = ? and col2 = ?) or ... )
 				//		unfortunately most of this building needs to be held off until we know
 				//		the exact number of ids :(
 				return new StringBuilder( "(" ).append( BATCH_ID_PLACEHOLDER ).append( ")" );
 			}
 		}
 	}
 
 	public static String expandBatchIdPlaceholder(
 			String sql,
 			Serializable[] ids,
 			String alias,
 			String[] keyColumnNames,
 			Dialect dialect) {
 		if ( keyColumnNames.length == 1 ) {
 			// non-composite
 			return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( "?", ids.length, "," ) );
 		}
 		else {
 			// composite
 			if ( dialect.supportsRowValueConstructorSyntaxInInList() ) {
 				final String tuple = "(" + StringHelper.repeat( "?", keyColumnNames.length, "," ) + ")";
 				return StringHelper.replace( sql, BATCH_ID_PLACEHOLDER, repeat( tuple, ids.length, "," ) );
 			}
 			else {
 				final String keyCheck = "(" + joinWithQualifierAndSuffix(
 						keyColumnNames,
 						alias,
 						" = ?",
 						" and "
 				) + ")";
 				return replace( sql, BATCH_ID_PLACEHOLDER, repeat( keyCheck, ids.length, " or " ) );
 			}
 		}
 	}
 
 	/**
 	 * Takes a String s and returns a new String[1] with s as the only element.
 	 * If s is null or "", return String[0].
 	 *
 	 * @param s
 	 *
 	 * @return String[]
 	 */
 	public static String[] toArrayElement(String s) {
 		return ( s == null || s.length() == 0 ) ? new String[0] : new String[] {s};
 	}
 
 	public static String nullIfEmpty(String value) {
 		return isEmpty( value ) ? null : value;
 	}
 
 	public static List<String> parseCommaSeparatedString(String incomingString) {
 		return Arrays.asList( incomingString.split( "\\s*,\\s*" ) );
 	}
+
+	public static <T> String join(Collection<T> values, Renderer<T> renderer) {
+		final StringBuilder buffer = new StringBuilder();
+		boolean firstPass = true;
+		for ( T value : values ) {
+			if ( firstPass ) {
+				firstPass = false;
+			}
+			else {
+				buffer.append( ", " );
+			}
+
+			buffer.append( renderer.render( value ) );
+		}
+
+		return buffer.toString();
+	}
+
+	public static <T> String join(T[] values, Renderer<T> renderer) {
+		return join( Arrays.asList( values ), renderer );
+	}
+
+	public interface Renderer<T> {
+		String render(T value);
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 4eb21fe2b7..09f12cee3e 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,685 +1,686 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.sql.Types;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
+import org.hibernate.boot.internal.AttributeConverterDescriptorNonAutoApplicableImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
+import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataImplementor;
-import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.JdbcTypeNameMapper;
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.LobTypeMappings;
 import org.hibernate.type.descriptor.sql.NationalizedTypeMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final MetadataImplementor metadata;
 
 	private final List<Selectable> columns = new ArrayList<Selectable>();
 
 	private String typeName;
 	private Properties typeParameters;
 	private boolean isNationalized;
 	private boolean isLob;
 
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private boolean cascadeDeleteEnabled;
 
-	private AttributeConverterDefinition attributeConverterDefinition;
+	private AttributeConverterDescriptor attributeConverterDescriptor;
 	private Type type;
 
 	public SimpleValue(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	public SimpleValue(MetadataImplementor metadata, Table table) {
 		this( metadata );
 		this.table = table;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadata().getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	@Override
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) {
 			columns.add(column);
 		}
 		column.setValue(this);
 		column.setTypeIndex( columns.size() - 1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add( formula );
 	}
 
 	@Override
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	@Override
 	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
 
 	public List getConstraintColumns() {
 		return columns;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		if ( typeName != null && typeName.startsWith( AttributeConverterTypeAdapter.NAME_PREFIX ) ) {
 			final String converterClassName = typeName.substring( AttributeConverterTypeAdapter.NAME_PREFIX.length() );
 			final ClassLoaderService cls = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			try {
 				final Class<AttributeConverter> converterClass = cls.classForName( converterClassName );
-				attributeConverterDefinition = new AttributeConverterDefinition( converterClass.newInstance(), false );
+				attributeConverterDescriptor = new AttributeConverterDescriptorNonAutoApplicableImpl( converterClass.newInstance() );
 				return;
 			}
 			catch (Exception e) {
 				log.logBadHbmAttributeConverterType( typeName, e.getMessage() );
 			}
 		}
 
 		this.typeName = typeName;
 	}
 
 	public void makeNationalized() {
 		this.isNationalized = true;
 	}
 
 	public boolean isNationalized() {
 		return isNationalized;
 	}
 
 	public void makeLob() {
 		this.isLob = true;
 	}
 
 	public boolean isLob() {
 		return isLob;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	@Override
 	public void createForeignKey() throws MappingException {}
 
 	@Override
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	private IdentifierGenerator identifierGenerator;
 
 	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 
 		if ( identifierGenerator != null ) {
 			return identifierGenerator;
 		}
 
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) {
 					tables.append(", ");
 				}
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		final ConfigurationService cs = metadata.getMetadataBuildingOptions().getServiceRegistry()
 				.getService( ConfigurationService.class );
 
 		params.put(
 				AvailableSettings.PREFER_POOLED_VALUES_LO,
 				cs.getSetting( AvailableSettings.PREFER_POOLED_VALUES_LO, StandardConverters.BOOLEAN, false )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		identifierGenerator = identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 
 		return identifierGenerator;
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Object selectable = itr.next();
 			if ( selectable instanceof Formula ) {
 				// if there are *any* formulas, then the Value overall is
 				// considered nullable
 				return true;
 			}
 			else if ( !( (Column) selectable ).isNullable() ) {
 				// if there is a single non-nullable column, the Value
 				// overall is considered non-nullable.
 				return false;
 			}
 		}
 		// nullable by default
 		return true;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 
 		if ( typeParameters != null
 				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
 				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
 			createParameterImpl();
 		}
 
 		Type result = metadata.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
-		if ( attributeConverterDefinition == null ) {
+		if ( attributeConverterDescriptor == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "Attribute types for a dynamic entity must be explicitly specified: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName, metadata.getMetadataBuildingOptions().getServiceRegistry().getService( ClassLoaderService.class ) ).getName();
 			// todo : to fully support isNationalized here we need do the process hinted at above
 			// 		essentially, much of the logic from #buildAttributeConverterTypeAdapter wrt resolving
 			//		a (1) SqlTypeDescriptor, a (2) JavaTypeDescriptor and dynamically building a BasicType
 			// 		combining them.
 			return;
 		}
 
 		// we had an AttributeConverter...
 		type = buildAttributeConverterTypeAdapter();
 	}
 
 	/**
 	 * Build a Hibernate Type that incorporates the JPA AttributeConverter.  AttributeConverter works totally in
 	 * memory, meaning it converts between one Java representation (the entity attribute representation) and another
 	 * (the value bound into JDBC statements or extracted from results).  However, the Hibernate Type system operates
 	 * at the lower level of actually dealing directly with those JDBC objects.  So even though we have an
 	 * AttributeConverter, we still need to "fill out" the rest of the BasicType data and bridge calls
 	 * to bind/extract through the converter.
 	 * <p/>
 	 * Essentially the idea here is that an intermediate Java type needs to be used.  Let's use an example as a means
 	 * to illustrate...  Consider an {@code AttributeConverter<Integer,String>}.  This tells Hibernate that the domain
 	 * model defines this attribute as an Integer value (the 'entityAttributeJavaType'), but that we need to treat the
 	 * value as a String (the 'databaseColumnJavaType') when dealing with JDBC (aka, the database type is a
 	 * VARCHAR/CHAR):<ul>
 	 *     <li>
 	 *         When binding values to PreparedStatements we need to convert the Integer value from the entity
 	 *         into a String and pass that String to setString.  The conversion is handled by calling
 	 *         {@link AttributeConverter#convertToDatabaseColumn(Object)}
 	 *     </li>
 	 *     <li>
 	 *         When extracting values from ResultSets (or CallableStatement parameters) we need to handle the
 	 *         value via getString, and convert that returned String to an Integer.  That conversion is handled
 	 *         by calling {@link AttributeConverter#convertToEntityAttribute(Object)}
 	 *     </li>
 	 * </ul>
 	 *
 	 * @return The built AttributeConverter -> Type adapter
 	 *
 	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 	 * then we can "play them against each other" in terms of determining proper typing
 	 *
 	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 	 */
 	@SuppressWarnings("unchecked")
 	private Type buildAttributeConverterTypeAdapter() {
 		// todo : validate the number of columns present here?
 
-		final Class entityAttributeJavaType = attributeConverterDefinition.getEntityAttributeType();
-		final Class databaseColumnJavaType = attributeConverterDefinition.getDatabaseColumnType();
+		final Class entityAttributeJavaType = attributeConverterDescriptor.getDomainType();
+		final Class databaseColumnJavaType = attributeConverterDescriptor.getJdbcType();
 
 
 		// resolve the JavaTypeDescriptor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.
 		final JavaTypeDescriptor entityAttributeJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 
 
 		// build the SqlTypeDescriptor adapter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Going back to the illustration, this should be a SqlTypeDescriptor that handles the Integer <-> String
 		//		conversions.  This is the more complicated piece.  First we need to determine the JDBC type code
 		//		corresponding to the AttributeConverter's declared "databaseColumnJavaType" (how we read that value out
 		// 		of ResultSets).  See JdbcTypeJavaClassMappings for details.  Again, given example, this should return
 		// 		VARCHAR/CHAR
 		int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 		if ( isLob() ) {
 			if ( LobTypeMappings.INSTANCE.hasCorrespondingLobCode( jdbcTypeCode ) ) {
 				jdbcTypeCode = LobTypeMappings.INSTANCE.getCorrespondingLobCode( jdbcTypeCode );
 			}
 			else {
 				if ( Serializable.class.isAssignableFrom( entityAttributeJavaType ) ) {
 					jdbcTypeCode = Types.BLOB;
 				}
 				else {
 					throw new IllegalArgumentException(
 							String.format(
 									Locale.ROOT,
 									"JDBC type-code [%s (%s)] not known to have a corresponding LOB equivalent, and Java type is not Serializable (to use BLOB)",
 									jdbcTypeCode,
 									JdbcTypeNameMapper.getTypeName( jdbcTypeCode )
 							)
 					);
 				}
 			}
 		}
 		if ( isNationalized() ) {
 			jdbcTypeCode = NationalizedTypeMappings.INSTANCE.getCorrespondingNationalizedCode( jdbcTypeCode );
 		}
 		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
 		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
 		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
-				attributeConverterDefinition.getAttributeConverter(),
+				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
-		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDefinition.getAttributeConverter().getClass().getName();
+		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDescriptor.getAttributeConverter().getClass().getName();
 		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
 				description,
-				attributeConverterDefinition.getAttributeConverter(),
+				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptorAdapter,
 				entityAttributeJavaType,
 				databaseColumnJavaType,
 				entityAttributeJavaTypeDescriptor
 		);
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 
-	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition attributeConverterDefinition) {
-		this.attributeConverterDefinition = attributeConverterDefinition;
+	public void setJpaAttributeConverterDescriptor(AttributeConverterDescriptor attributeConverterDescriptor) {
+		this.attributeConverterDescriptor = attributeConverterDescriptor;
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				Selectable column = columns.get(i);
 				if (column instanceof Column){
 					columnsNames[i] = ((Column) column).getName();
 				}
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							classLoaderService.classForName(
 									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
 							),
 							annotations,
 							table.getCatalog(),
 							table.getSchema(),
 							table.getName(),
 							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
 							columnsNames
 					)
 			);
 		}
 		catch ( ClassLoadingException e ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, e );
 		}
 	}
 
 	private static final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
 		private final Class returnedClass;
 		private final Annotation[] annotationsMethod;
 		private final String catalog;
 		private final String schema;
 		private final String table;
 		private final boolean primaryKey;
 		private final String[] columns;
 
 		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
 				String table, boolean primaryKey, String[] columns) {
 			this.returnedClass = returnedClass;
 			this.annotationsMethod = annotationsMethod;
 			this.catalog = catalog;
 			this.schema = schema;
 			this.table = table;
 			this.primaryKey = primaryKey;
 			this.columns = columns;
 		}
 
 		@Override
 		public Class getReturnedClass() {
 			return returnedClass;
 		}
 
 		@Override
 		public Annotation[] getAnnotationsMethod() {
 			return annotationsMethod;
 		}
 
 		@Override
 		public String getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String getSchema() {
 			return schema;
 		}
 
 		@Override
 		public String getTable() {
 			return table;
 		}
 
 		@Override
 		public boolean isPrimaryKey() {
 			return primaryKey;
 		}
 
 		@Override
 		public String[] getColumns() {
 			return columns;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/AttributeConverterTest.java b/hibernate-core/src/test/java/org/hibernate/test/converter/AttributeConverterTest.java
index dee6aa3cf9..46bd5e6c3f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/converter/AttributeConverterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/AttributeConverterTest.java
@@ -1,608 +1,609 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.converter;
 
 import java.io.Serializable;
 import java.sql.Timestamp;
 import java.sql.Types;
 import javax.persistence.AttributeConverter;
 import javax.persistence.Column;
 import javax.persistence.Convert;
 import javax.persistence.Converter;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.Table;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.IrrelevantEntity;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.MetadataSources;
+import org.hibernate.boot.internal.AttributeConverterDescriptorNonAutoApplicableImpl;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.ast.tree.JavaConstantNode;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.type.AbstractStandardBasicType;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.EnumJavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.StringTypeDescriptor;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.fail;
 
 /**
  * Tests the principle of adding "AttributeConverter" to the mix of {@link org.hibernate.type.Type} resolution
  *
  * @author Steve Ebersole
  */
 public class AttributeConverterTest extends BaseUnitTestCase {
 	@Test
 	public void testErrorInstantiatingConverterClass() {
 		Configuration cfg = new Configuration();
 		try {
 			cfg.addAttributeConverter( BlowsUpConverter.class );
 			fail( "expecting an exception" );
 		}
 		catch (AnnotationException e) {
 			assertNotNull( e.getCause() );
 			assertTyping( BlewUpException.class, e.getCause() );
 		}
 	}
 
 	public static class BlewUpException extends RuntimeException {
 	}
 
 	public static class BlowsUpConverter implements AttributeConverter<String,String> {
 		public BlowsUpConverter() {
 			throw new BlewUpException();
 		}
 
 		@Override
 		public String convertToDatabaseColumn(String attribute) {
 			return null;
 		}
 
 		@Override
 		public String convertToEntityAttribute(String dbData) {
 			return null;
 		}
 	}
 
 	@Test
 	public void testBasicOperation() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr ).buildMetadata();
 			SimpleValue simpleValue = new SimpleValue( metadata );
-			simpleValue.setJpaAttributeConverterDefinition(
-					new AttributeConverterDefinition( new StringClobConverter(), true )
+			simpleValue.setJpaAttributeConverterDescriptor(
+					new AttributeConverterDescriptorNonAutoApplicableImpl( new StringClobConverter() )
 			);
 			simpleValue.setTypeUsingReflection( IrrelevantEntity.class.getName(), "name" );
 
 			Type type = simpleValue.getType();
 			assertNotNull( type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testNonAutoApplyHandling() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester.class )
 					.getMetadataBuilder()
 					.applyAttributeConverter( NotAutoAppliedConverter.class, false )
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			if ( AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter with autoApply=false was auto applied" );
 			}
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 
 	}
 
 	@Test
 	public void testBasicConverterApplication() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester.class )
 					.getMetadataBuilder()
 					.applyAttributeConverter( StringClobConverter.class, true )
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8462")
 	public void testBasicOrmXmlConverterApplication() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester.class )
 					.addURL( ConfigHelper.findAsResource( "org/hibernate/test/converter/orm.xml" ) )
 					.getMetadataBuilder()
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AttributeConverterTypeAdapter basicType = assertTyping( AttributeConverterTypeAdapter.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.CLOB, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testBasicConverterDisableApplication() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Tester2.class )
 					.getMetadataBuilder()
 					.applyAttributeConverter( StringClobConverter.class, true )
 					.build();
 
 			PersistentClass tester = metadata.getEntityBinding( Tester2.class.getName() );
 			Property nameProp = tester.getProperty( "name" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			if ( AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter applied (should not have been)" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertSame( StringTypeDescriptor.INSTANCE, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.VARCHAR, basicType.getSqlTypeDescriptor().getSqlType() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Test
 	public void testBasicUsage() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( IntegerToVarcharConverter.class, false );
 		cfg.addAnnotatedClass( Tester4.class );
 		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
 
 		SessionFactory sf = cfg.buildSessionFactory();
 
 		try {
 			Session session = sf.openSession();
 			session.beginTransaction();
 			session.save( new Tester4( 1L, "steve", 200 ) );
 			session.getTransaction().commit();
 			session.close();
 
 			sf.getStatistics().clear();
 			session = sf.openSession();
 			session.beginTransaction();
 			session.get( Tester4.class, 1L );
 			session.getTransaction().commit();
 			session.close();
 			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
 
 			session = sf.openSession();
 			session.beginTransaction();
 			Tester4 t4 = (Tester4) session.get( Tester4.class, 1L );
 			t4.code = 300;
 			session.getTransaction().commit();
 			session.close();
 
 			session = sf.openSession();
 			session.beginTransaction();
 			t4 = (Tester4) session.get( Tester4.class, 1L );
 			assertEquals( 300, t4.code.longValue() );
 			session.delete( t4 );
 			session.getTransaction().commit();
 			session.close();
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	public void testBasicTimestampUsage() {
 		Configuration cfg = new Configuration();
 		cfg.addAttributeConverter( InstantConverter.class, false );
 		cfg.addAnnotatedClass( IrrelevantInstantEntity.class );
 		cfg.setProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
 
 		SessionFactory sf = cfg.buildSessionFactory();
 
 		try {
 			Session session = sf.openSession();
 			session.beginTransaction();
 			session.save( new IrrelevantInstantEntity( 1L ) );
 			session.getTransaction().commit();
 			session.close();
 
 			sf.getStatistics().clear();
 			session = sf.openSession();
 			session.beginTransaction();
 			IrrelevantInstantEntity e = (IrrelevantInstantEntity) session.get( IrrelevantInstantEntity.class, 1L );
 			session.getTransaction().commit();
 			session.close();
 			assertEquals( 0, sf.getStatistics().getEntityUpdateCount() );
 
 			session = sf.openSession();
 			session.beginTransaction();
 			session.delete( e );
 			session.getTransaction().commit();
 			session.close();
 		}
 		finally {
 			sf.close();
 		}
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-8866")
 	public void testEnumConverter() {
 		final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
 				.applySetting( AvailableSettings.HBM2DDL_AUTO, "create-drop" )
 				.build();
 
 		try {
 			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( EntityWithConvertibleField.class )
 					.getMetadataBuilder()
 					.applyAttributeConverter( ConvertibleEnumConverter.class, true )
 					.build();
 
 			// first lets validate that the converter was applied...
 			PersistentClass tester = metadata.getEntityBinding( EntityWithConvertibleField.class.getName() );
 			Property nameProp = tester.getProperty( "convertibleEnum" );
 			SimpleValue nameValue = (SimpleValue) nameProp.getValue();
 			Type type = nameValue.getType();
 			assertNotNull( type );
 			assertTyping( BasicType.class, type );
 			if ( !AttributeConverterTypeAdapter.class.isInstance( type ) ) {
 				fail( "AttributeConverter not applied" );
 			}
 			AbstractStandardBasicType basicType = assertTyping( AbstractStandardBasicType.class, type );
 			assertTyping( EnumJavaTypeDescriptor.class, basicType.getJavaTypeDescriptor() );
 			assertEquals( Types.VARCHAR, basicType.getSqlTypeDescriptor().getSqlType() );
 
 			// then lets build the SF and verify its use...
 			final SessionFactory sf = metadata.buildSessionFactory();
 			try {
 				Session s = sf.openSession();
 				s.getTransaction().begin();
 				EntityWithConvertibleField entity = new EntityWithConvertibleField();
 				entity.setId( "ID" );
 				entity.setConvertibleEnum( ConvertibleEnum.VALUE );
 				String entityID = entity.getId();
 				s.persist( entity );
 				s.getTransaction().commit();
 				s.close();
 
 				s = sf.openSession();
 				s.beginTransaction();
 				entity = (EntityWithConvertibleField) s.load( EntityWithConvertibleField.class, entityID );
 				assertEquals( ConvertibleEnum.VALUE, entity.getConvertibleEnum() );
 				s.getTransaction().commit();
 				s.close();
 
 				JavaConstantNode javaConstantNode = new JavaConstantNode();
 				javaConstantNode.setExpectedType( type );
 				javaConstantNode.setSessionFactory( (SessionFactoryImplementor) sf );
 				javaConstantNode.setText( "org.hibernate.test.converter.AttributeConverterTest$ConvertibleEnum.VALUE" );
 				final String outcome = javaConstantNode.getRenderText( (SessionFactoryImplementor) sf );
 				assertEquals( "'VALUE'", outcome );
 
 				s = sf.openSession();
 				s.beginTransaction();
 				s.createQuery( "FROM EntityWithConvertibleField e where e.convertibleEnum = org.hibernate.test.converter.AttributeConverterTest$ConvertibleEnum.VALUE" )
 						.list();
 				s.getTransaction().commit();
 				s.close();
 
 				s = sf.openSession();
 				s.beginTransaction();
 				s.delete( entity );
 				s.getTransaction().commit();
 				s.close();
 			}
 			finally {
 				try {
 					sf.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 	
 	
 
 	// Entity declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Entity(name = "T1")
 	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester {
 		@Id
 		private Long id;
 		private String name;
 
 		public Tester() {
 		}
 
 		public Tester(Long id, String name) {
 			this.id = id;
 			this.name = name;
 		}
 	}
 
 	@Entity(name = "T2")
 	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester2 {
 		@Id
 		private Long id;
 		@Convert(disableConversion = true)
 		private String name;
 	}
 
 	@Entity(name = "T3")
 	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester3 {
 		@Id
 		private Long id;
 		@org.hibernate.annotations.Type( type = "string" )
 		@Convert(disableConversion = true)
 		private String name;
 	}
 
 	@Entity(name = "T4")
 	@SuppressWarnings("UnusedDeclaration")
 	public static class Tester4 {
 		@Id
 		private Long id;
 		private String name;
 		@Convert( converter = IntegerToVarcharConverter.class )
 		private Integer code;
 
 		public Tester4() {
 		}
 
 		public Tester4(Long id, String name, Integer code) {
 			this.id = id;
 			this.name = name;
 			this.code = code;
 		}
 	}
 
 	// This class is for mimicking an Instant from Java 8, which a converter might convert to a java.sql.Timestamp
 	public static class Instant implements Serializable {
 		private static final long serialVersionUID = 1L;
 
 		private long javaMillis;
 
 		public Instant(long javaMillis) {
 			this.javaMillis = javaMillis;
 		}
 
 		public long toJavaMillis() {
 			return javaMillis;
 		}
 
 		public static Instant fromJavaMillis(long javaMillis) {
 			return new Instant( javaMillis );
 		}
 
 		public static Instant now() {
 			return new Instant( System.currentTimeMillis() );
 		}
 	}
 
 	@Entity
 	@Table(name = "irrelevantInstantEntity")
 	@SuppressWarnings("UnusedDeclaration")
 	public static class IrrelevantInstantEntity {
 		@Id
 		private Long id;
 		private Instant dateCreated;
 
 		public IrrelevantInstantEntity() {
 		}
 
 		public IrrelevantInstantEntity(Long id) {
 			this( id, Instant.now() );
 		}
 
 		public IrrelevantInstantEntity(Long id, Instant dateCreated) {
 			this.id = id;
 			this.dateCreated = dateCreated;
 		}
 
 		public Long getId() {
 			return id;
 		}
 
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		public Instant getDateCreated() {
 			return dateCreated;
 		}
 
 		public void setDateCreated(Instant dateCreated) {
 			this.dateCreated = dateCreated;
 		}
 	}
 
 	public static enum ConvertibleEnum {
 		VALUE,
 		DEFAULT;
 
 		public String convertToString() {
 			switch ( this ) {
 				case VALUE: {
 					return "VALUE";
 				}
 				default: {
 					return "DEFAULT";
 				}
 			}
 		}
 	}
 
 	@Converter(autoApply = true)
 	public static class ConvertibleEnumConverter implements AttributeConverter<ConvertibleEnum, String> {
 		@Override
 		public String convertToDatabaseColumn(ConvertibleEnum attribute) {
 			return attribute.convertToString();
 		}
 
 		@Override
 		public ConvertibleEnum convertToEntityAttribute(String dbData) {
 			return ConvertibleEnum.valueOf( dbData );
 		}
 	}
 
 	@Entity( name = "EntityWithConvertibleField" )
 	@Table( name = "EntityWithConvertibleField" )
 	public static class EntityWithConvertibleField {
 		private String id;
 		private ConvertibleEnum convertibleEnum;
 
 		@Id
 		@Column(name = "id")
 		public String getId() {
 			return id;
 		}
 
 		public void setId(String id) {
 			this.id = id;
 		}
 
 		@Column(name = "testEnum")
 
 		public ConvertibleEnum getConvertibleEnum() {
 			return convertibleEnum;
 		}
 
 		public void setConvertibleEnum(ConvertibleEnum convertibleEnum) {
 			this.convertibleEnum = convertibleEnum;
 		}
 	}
 
 
 	// Converter declarations used in the test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Converter(autoApply = false)
 	public static class NotAutoAppliedConverter implements AttributeConverter<String,String> {
 		@Override
 		public String convertToDatabaseColumn(String attribute) {
 			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
 		}
 
 		@Override
 		public String convertToEntityAttribute(String dbData) {
 			throw new IllegalStateException( "AttributeConverter should not have been applied/called" );
 		}
 	}
 
 	@Converter( autoApply = true )
 	public static class IntegerToVarcharConverter implements AttributeConverter<Integer,String> {
 		@Override
 		public String convertToDatabaseColumn(Integer attribute) {
 			return attribute == null ? null : attribute.toString();
 		}
 
 		@Override
 		public Integer convertToEntityAttribute(String dbData) {
 			return dbData == null ? null : Integer.valueOf( dbData );
 		}
 	}
 
 
 	@Converter( autoApply = true )
 	public static class InstantConverter implements AttributeConverter<Instant, Timestamp> {
 		@Override
 		public Timestamp convertToDatabaseColumn(Instant attribute) {
 			return new Timestamp( attribute.toJavaMillis() );
 		}
 
 		@Override
 		public Instant convertToEntityAttribute(Timestamp dbData) {
 			return Instant.fromJavaMillis( dbData.getTime() );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/converter/generics/ParameterizedAttributeConverterParameterTypeTest.java b/hibernate-core/src/test/java/org/hibernate/test/converter/generics/ParameterizedAttributeConverterParameterTypeTest.java
index 1bfd81c074..e25ee475a1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/converter/generics/ParameterizedAttributeConverterParameterTypeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/converter/generics/ParameterizedAttributeConverterParameterTypeTest.java
@@ -1,49 +1,193 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.converter.generics;
 
 import java.util.ArrayList;
 import java.util.List;
+import java.util.StringTokenizer;
 import javax.persistence.AttributeConverter;
+import javax.persistence.Converter;
+import javax.persistence.Entity;
+import javax.persistence.Id;
 
+import org.hibernate.boot.Metadata;
+import org.hibernate.boot.MetadataSources;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cfg.AttributeConverterDefinition;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.junit.AfterClass;
+import org.junit.BeforeClass;
 import org.junit.Test;
 
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
 import static org.junit.Assert.assertEquals;
 
 /**
  * Test the ability to interpret and understand AttributeConverter impls which
  * use parameterized types as one of (typically the "attribute type") its parameter types.
  * 
  * @author Svein Baardsen
  * @author Steve Ebersole
  */
 public class ParameterizedAttributeConverterParameterTypeTest extends BaseUnitTestCase {
 
+	private static StandardServiceRegistry ssr;
+
+	@BeforeClass
+	public static void beforeClass() {
+		ssr = new StandardServiceRegistryBuilder().build();
+	}
+
+	@AfterClass
+	public static void afterClass() {
+		if ( ssr != null ) {
+			StandardServiceRegistryBuilder.destroy( ssr );
+		}
+	}
+
 	public static class CustomAttributeConverter implements AttributeConverter<List<String>, Integer> {
 		@Override
 		public Integer convertToDatabaseColumn(List<String> attribute) {
 			return attribute.size();
 		}
 
 		@Override
 		public List<String> convertToEntityAttribute(Integer dbData) {
 			return new ArrayList<String>(dbData);
 		}
 	}
 	
 	@Test
 	@TestForIssue(jiraKey = "HHH-8804")
 	public void testGenericTypeParameters() {
 		AttributeConverterDefinition def = AttributeConverterDefinition.from( CustomAttributeConverter.class );
 		assertEquals( List.class, def.getEntityAttributeType() );
 	}
 
+	@Test
+	@TestForIssue( jiraKey = "HHH-10050" )
+	public void testNestedTypeParameterAutoApplication() {
+		final Metadata metadata = new MetadataSources( ssr )
+				.addAnnotatedClass( SampleEntity.class )
+				.getMetadataBuilder()
+				.applyAttributeConverter( IntegerListConverter.class )
+				.applyAttributeConverter( StringListConverter.class )
+				.build();
+
+		// lets make sure the auto-apply converters were applied properly...
+		PersistentClass pc = metadata.getEntityBinding( SampleEntity.class.getName() );
+
+		{
+			Property prop = pc.getProperty( "someStrings" );
+			AttributeConverterTypeAdapter type = assertTyping(
+					AttributeConverterTypeAdapter.class,
+					prop.getType()
+			);
+			assertTyping(
+					StringListConverter.class,
+					type.getAttributeConverter()
+			);
+		}
+
+		{
+			Property prop = pc.getProperty( "someIntegers" );
+			AttributeConverterTypeAdapter type = assertTyping(
+					AttributeConverterTypeAdapter.class,
+					prop.getType()
+			);
+			assertTyping(
+					IntegerListConverter.class,
+					type.getAttributeConverter()
+			);
+		}
+	}
+
+	@Entity
+	public static class SampleEntity {
+		@Id
+		private Integer id;
+		private List<String> someStrings;
+		private List<Integer> someIntegers;
+	}
+
+	@Converter( autoApply = true )
+	public static class IntegerListConverter implements AttributeConverter<List<Integer>,String> {
+		@Override
+		public String convertToDatabaseColumn(List<Integer> attribute) {
+			if ( attribute == null || attribute.isEmpty() ) {
+				return null;
+			}
+			else {
+				return StringHelper.join( ", ", attribute );
+			}
+		}
+
+		@Override
+		public List<Integer> convertToEntityAttribute(String dbData) {
+			if ( dbData == null ) {
+				return null;
+			}
+
+			dbData = dbData.trim();
+			if ( dbData.length() == 0 ) {
+				return null;
+			}
+
+			final List<Integer> integers = new ArrayList<Integer>();
+			final StringTokenizer tokens = new StringTokenizer( dbData, "," );
+
+			while ( tokens.hasMoreTokens() ) {
+				integers.add( Integer.valueOf( tokens.nextToken() ) );
+			}
+
+			return integers;
+		}
+	}
+
+	@Converter( autoApply = true )
+	public static class StringListConverter implements AttributeConverter<List<String>,String> {
+		@Override
+		public String convertToDatabaseColumn(List<String> attribute) {
+			if ( attribute == null || attribute.isEmpty() ) {
+				return null;
+			}
+			else {
+				return StringHelper.join( ", ", attribute );
+			}
+		}
+
+		@Override
+		public List<String> convertToEntityAttribute(String dbData) {
+			if ( dbData == null ) {
+				return null;
+			}
+
+			dbData = dbData.trim();
+			if ( dbData.length() == 0 ) {
+				return null;
+			}
+
+			final List<String> strings = new ArrayList<String>();
+			final StringTokenizer tokens = new StringTokenizer( dbData, "," );
+
+			while ( tokens.hasMoreTokens() ) {
+				strings.add( tokens.nextToken() );
+			}
+
+			return strings;
+		}
+	}
+
+
 }
diff --git a/libraries.gradle b/libraries.gradle
index d0a461a35f..72c99e05ae 100644
--- a/libraries.gradle
+++ b/libraries.gradle
@@ -1,112 +1,112 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 
 // build a map of the dependency artifacts to use.  Allows centralized definition of the version of artifacts to
 // use.  In that respect it serves a role similar to <dependencyManagement> in Maven
 ext {
 
     junitVersion = '4.11'
 //    h2Version = '1.2.145'
     h2Version = '1.3.176'
     bytemanVersion = '2.1.2'
     infinispanVersion = '7.2.1.Final'
     jnpVersion = '5.0.6.CR1'
     elVersion = '2.2.4'
 
     libraries = [
             // Ant
             ant:            'org.apache.ant:ant:1.8.2',
 
             // Antlr
             antlr:          'antlr:antlr:2.7.7',
 
             // Annotations
             commons_annotations: 'org.hibernate.common:hibernate-commons-annotations:5.0.0.Final',
             jandex:         'org.jboss:jandex:2.0.0.CR1',
-            classmate:      'com.fasterxml:classmate:0.8.0',
+            classmate:      'com.fasterxml:classmate:1.3.0',
 
             // Woodstox
             woodstox:           "org.codehaus.woodstox:woodstox-core-asl:4.3.0",
 
             // Dom4J
             dom4j:          'dom4j:dom4j:1.6.1@jar',
 
             // Javassist
             javassist:      'org.javassist:javassist:3.18.1-GA',
 
             // javax
             jpa:            'org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.0.Final',
             // There is a bug in the OSGi information in the JBoss one.  See https://issues.jboss.org/browse/JBEE-160
             //jta:            'org.jboss.spec.javax.transaction:jboss-transaction-api_1.2_spec:1.0.0.Final',
             jta:            'org.apache.geronimo.specs:geronimo-jta_1.1_spec:1.1.1',
             validation:     'javax.validation:validation-api:1.1.0.Final',
             jacc:           'org.jboss.spec.javax.security.jacc:jboss-jacc-api_1.4_spec:1.0.2.Final',
 
             // logging
             logging:        'org.jboss.logging:jboss-logging:3.3.0.Final',
             logging_annotations: 'org.jboss.logging:jboss-logging-annotations:2.0.0.Final',
             logging_processor:  'org.jboss.logging:jboss-logging-processor:2.0.0.Final',
 
             slf4j_api:                 "org.slf4j:slf4j-api:1.7.5",
             slf4j_log4j:               "org.slf4j:slf4j-log4j12:1.7.5",
 
             // jaxb task
 			jaxb:           'com.sun.xml.bind:jaxb-xjc:2.2.5',
 			jaxb2_basics:   'org.jvnet.jaxb2_commons:jaxb2-basics:0.6.3',
 			jaxb2_ant:      'org.jvnet.jaxb2_commons:jaxb2-basics-ant:0.6.3',
 			jaxb2_jaxb:     'org.jvnet.jaxb2_commons:jaxb2-basics-jaxb:2.2.4-1',
 			jaxb2_jaxb_xjc: 'org.jvnet.jaxb2_commons:jaxb2-basics-jaxb-xjc:2.2.4-1',
 
             // Animal Sniffer Ant Task and Java 1.6 API signature file
             // not using 1.9 for the time being due to MANIMALSNIFFER-34
             animal_sniffer:     'org.codehaus.mojo:animal-sniffer-ant-tasks:1.13',
             as_asm:             'org.ow2.asm:asm-all:5.0.3',
             java16_signature:   'org.codehaus.mojo.signature:java16:1.0@signature',
 
             //Maven plugin framework
             maven_plugin: 'org.apache.maven:maven-plugin-api:3.0.5',
             maven_plugin_tools: 'org.apache.maven.plugin-tools:maven-plugin-annotations:3.2',
 
             // ~~~~~~~~~~~~~~~~~~~~~~~~~~ testing
 
             log4j:          "log4j:log4j:1.2.17",
             junit:           "junit:junit:${junitVersion}",
             byteman:         "org.jboss.byteman:byteman:${bytemanVersion}",
             byteman_install: "org.jboss.byteman:byteman-install:${bytemanVersion}",
             byteman_bmunit:  "org.jboss.byteman:byteman-bmunit:${bytemanVersion}",
             shrinkwrap_api:  'org.jboss.shrinkwrap:shrinkwrap-api:1.0.0-beta-6',
             shrinkwrap:      'org.jboss.shrinkwrap:shrinkwrap-impl-base:1.0.0-beta-6',
             h2:              "com.h2database:h2:${h2Version}",
             derby:           "org.apache.derby:derby:10.11.1.1",
             jboss_jta:       "org.jboss.jbossts:jbossjta:4.16.4.Final",
             xapool:          "com.experlog:xapool:1.5.0",
             mockito:         'org.mockito:mockito-core:1.9.0',
 
             validator:       'org.hibernate:hibernate-validator:5.2.0.CR1',
             // EL required by Hibernate Validator at test runtime
             expression_language_api:  "javax.el:javax.el-api:${elVersion}",
             expression_language_impl: "org.glassfish.web:javax.el:${elVersion}",
 
             // required by Hibernate Validator at test runtime
             unified_el:      "org.glassfish:javax.el:3.0-b07",
 
             // ~~~~~~~~~~~~~~~~~~~~~~~~~~~  infinsipan
             infinispan:      "org.infinispan:infinispan-core:${infinispanVersion}",
             // ~~~~~~~~~~~~~~~~~~~~~~~~~~~  infinispan test
             infinispan_test: "org.infinispan:infinispan-core:${infinispanVersion}:tests@jar",
             rhq:             "org.rhq.helpers:rhq-pluginAnnotations:3.0.4",
             jboss_common_core: "org.jboss:jboss-common-core:2.2.16.GA@jar",
             jnp_client:      "org.jboss.naming:jnp-client:${jnpVersion}",
             jnp_server:      "org.jboss.naming:jnpserver:${jnpVersion}",
 
             c3p0:            "com.mchange:c3p0:0.9.2.1",
             ehcache:         "net.sf.ehcache:ehcache-core:2.4.3",
             proxool:         "proxool:proxool:0.8.3",
             hikaricp:        "com.zaxxer:HikariCP-java6:2.3.3"
 
         ]
 }
